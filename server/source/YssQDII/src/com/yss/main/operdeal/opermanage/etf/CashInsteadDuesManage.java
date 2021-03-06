package com.yss.main.operdeal.opermanage.etf;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

import com.yss.commeach.EachRateOper;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.main.cashmanage.*;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.manager.*;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssOperCons;

/**
 * ETF应付替代款的生成和结转
 * MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A 
 * @author pjf
 * 20092021 create
 */
public class CashInsteadDuesManage extends BaseOperManage {
	
	private ETFParamSetBean etfParamBean = null;// ETF参数的实体类
	private ETFParamSetAdmin etfParamAdmin = null;
	private HashMap etfParam = null;//保存参数设置

	public void initOperManageInfo(Date dDate, String portCode) throws YssException {
		this.sPortCode = portCode;
		this.dDate = dDate;
		
		etfParamAdmin = new ETFParamSetAdmin();
		etfParamAdmin.setYssPub(pub);
		etfParam = etfParamAdmin.getETFParamInfo(sPortCode);
		etfParamBean = (ETFParamSetBean)etfParam.get(sPortCode);
	}

	public void doOpertion() throws YssException {				
		CreateETFCashInsteadDues("B");//生成应付替代款（申购）
		CreateETFCashInsteadDues("S");//生成应付替代款（赎回）
		TransferCashInsteadDues("B");//应付替代款结转（申购）
		TransferCashInsteadDues("S");//应付替代款结转（赎回）
	}

	/**
	 * 应付替代款的结转
	 * 产生应付替代款的资金调拨 
	 * 结转金额 = 从交易结算明细关联表中取清算标识为Y，应退合计不为0且退款日期为当日的 应退合计 * 数据方向的总和
	 * @throws YssException
	 */
	private void TransferCashInsteadDues(String strBS) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		double dTransInsdDues = 0;
		double dYfSGKTk = 0;//应付申购款退款金额
		double dYfSGKBk = 0;//应付申购款补款金额
		double dBaseTransInsdDues = 0;
		double dPortTransInsdDues = 0;
		double dReplaceCash = 0;
		double dBaseReplaceCash = 0;
		double dPortReplaceCash = 0;
		String strCashAccCode = "";//存款账户
		String strClearAccCode = "";//备付金账户
		String strCuryCode = "";
		String strSupplyMode = "";//补票方式
        EachRateOper rateOper = new EachRateOper();//新建获取利率的通用类
        double BaseCuryRate = 1.0;
        double PortCuryRate = 1.0;
        CashTransAdmin cashTransAdmin = null;
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
        CashPecPayBean cashPecPayBean = null;
        CashPayRecAdmin cashPayRecAdmin = null;
        Connection conn = null;
        boolean bTrans = true;
		try{
			strCashAccCode = etfParamBean.getCashAccCode();
			strClearAccCode = etfParamBean.getClearAccCode();
			strCuryCode = this.getCuryCodeByCashAccCode(strCashAccCode);
			strSupplyMode = etfParamBean.getSupplyMode();
			
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			//通过台账表获取应付替代款的结转数据
			strSql = buildTransferSql(strBS);
			
            rs = dbl.queryByPreparedStatement(strSql);
            while(rs.next()){
            	if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(strSupplyMode)){
            		//易方达ETF补票台账中计算的是单位篮子的应付替代款，因此需要乘以篮子数 panjunfang modify 20110816
            		dTransInsdDues += calTotalInsteadDues(rs.getDate("fbuydate"),strBS,rs.getDouble("FSumReturn"),rs.getDouble("FNORMSCALE"));  
            		
    				strCashAccCode = rs.getString("FCashAccCode");
    				strClearAccCode = rs.getString("FClearAccCode");
    				strCuryCode = rs.getString("FCuryCode");
            	}else{
            		if("F".equals(rs.getString("FInOut"))){
            			//退款
            			dYfSGKTk += rs.getDouble("FSumReturn");
            		} else {
            			//补款
            			dYfSGKBk += rs.getDouble("FSumReturn");
            		}
            		dTransInsdDues += rs.getDouble("FSumReturn");
            		dReplaceCash += rs.getDouble("FReplaceCash");//用于冲减可以现金替代金额
            	}    	
            } 
            if((strSupplyMode.equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) 
            		|| strSupplyMode.equals(YssOperCons.YSS_ETF_MAKEUP_TIMESUB) 
            		|| strSupplyMode.equals(YssOperCons.YSS_ETF_MAKEUP_ONE)
            		|| strSupplyMode.equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)) 
            		&& strBS.equals("S")){
            	//华夏和华宝YSS_ETF_MAKEUP_TIMESUBETF台帐中的赎回应付替代款为负，
            	//而嘉实ETF等赎回应付替代款为正，此处处理为一致
            	dTransInsdDues = -dTransInsdDues;
            	dReplaceCash = -dReplaceCash;
            }
			BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
					strCuryCode, sPortCode, YssOperCons.YSS_RATE_BASE);// 获取当日的基础汇率
			dBaseTransInsdDues = this.getSettingOper().calBaseMoney(
					dTransInsdDues, BaseCuryRate);// 计算基础货币金额
			dBaseReplaceCash = this.getSettingOper().calBaseMoney(
					dReplaceCash, BaseCuryRate);// 计算基础货币金额

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, strCuryCode, sPortCode);
            PortCuryRate = rateOper.getDPortRate();     //获取当日的组合汇率
			dPortTransInsdDues = this.getSettingOper().calPortMoney(
												dTransInsdDues, BaseCuryRate, PortCuryRate, strCuryCode,
														dDate, sPortCode); // 计算组合货币金额
			dPortReplaceCash = this.getSettingOper().calPortMoney(
					dReplaceCash, BaseCuryRate, PortCuryRate, strCuryCode,
							dDate, sPortCode); // 计算组合货币金额
			
			//产生应付替代款的资金调拨

            if (cashTransAdmin == null) {
            	cashTransAdmin = new CashTransAdmin(); //生成资金调拨控制类
            	cashTransAdmin.setYssPub(pub);
            }
            
            if((strSupplyMode.equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) || 
            		strSupplyMode.equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)) && 
            		strBS.equals("B")){
            	//华夏恒指ETF应付替代款（申购）区分退款和补款
				this.doCashTransHX(cashTransAdmin, "TK", dYfSGKTk,
						BaseCuryRate, PortCuryRate);//退款资金调拨
				
				this.doCashTransHX(cashTransAdmin, "BK", dYfSGKBk,
						BaseCuryRate, PortCuryRate);//补款资金调拨
            }else{
				doCashTrans(cashTransAdmin, strBS, dTransInsdDues,
						BaseCuryRate, PortCuryRate);
            }			

            if(strBS.equals("S")){
            	//插入资金调拨，传入调拨日期、业务日期、调拨类型、调拨子类型、关联编号类型、组合代码和自动录入标志来删除原有调拨数据
            	cashTransAdmin.insert("",dDate,dDate,
            								YssOperCons.YSS_ZJDBLX_Capital,
            								YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSellTrans,
            								"","","","","","ETF_ID","",1,"",sPortCode,0,"","","",true,"");
            }else{
            	//插入资金调拨，传入调拨日期、业务日期、调拨类型、调拨子类型、关联编号类型、组合代码和自动录入标志来删除原有调拨数据
            	cashTransAdmin.insert("",dDate,dDate,
            								YssOperCons.YSS_ZJDBLX_Capital,
            								YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuyTrans + "," + 
            								YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuyTrans_BK,
            								"","","","","","ETF_ID","",1,"",sPortCode,0,"","","",true,"");
            }
            
            //生成用于冲减应付代替款的应收应付款
            cashPayRecAdmin = new CashPayRecAdmin();
            cashPayRecAdmin.setYssPub(pub);
                
			cashPecPayBean = new CashPecPayBean();
			cashPecPayBean.setTradeDate(dDate);
			cashPecPayBean.setPortCode(sPortCode);
			cashPecPayBean.setBaseCuryRate(BaseCuryRate);
			cashPecPayBean.setPortCuryRate(PortCuryRate);
            if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(strSupplyMode)
            		|| YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(strSupplyMode)) {
            	//易方达只有现金替代需要结转到备付金账户，其余款项结转到现金账户
            	cashPecPayBean.setCashAccCode(strCashAccCode);
            }else{
            	cashPecPayBean.setCashAccCode((strClearAccCode == null || strClearAccCode.length() == 0) ? strCashAccCode : strClearAccCode);
            }			
			cashPecPayBean.setInOutType(1); 
			cashPecPayBean.setCuryCode(strCuryCode);
			cashPecPayBean.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);    // 设置业务类型为03	
			cashPecPayBean.checkStateId = 1;                              // 设置审核状态为已审核
			if(strBS.equals("S")){
				cashPecPayBean.setMoney(YssD.sub(dTransInsdDues,dReplaceCash));
				cashPecPayBean.setBaseCuryMoney(YssD.sub(dBaseTransInsdDues,dBaseReplaceCash));
				cashPecPayBean.setPortCuryMoney(YssD.sub(dPortTransInsdDues,dPortReplaceCash));
				cashPecPayBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSellDone); // 设置业务子类型为03TA_IDS
    			cashPayRecAdmin.addList(cashPecPayBean);
    			
    			if(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE.equals(strSupplyMode)
    					|| YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(strSupplyMode)) {
    				//华夏恒指etf可以现金替代（赎回）在应付赎回款结转日期进行冲减
    				//而易方达etf可以现金替代（赎回）在补票完成当天进行冲减
        			//冲减可以现金替代
        			CashPecPayBean cashPecPayBean2 = (CashPecPayBean) cashPecPayBean.clone();
        			cashPecPayBean2.setMoney(dReplaceCash);
        			cashPecPayBean2.setBaseCuryMoney(dBaseReplaceCash);
        			cashPecPayBean2.setPortCuryMoney(dPortReplaceCash);    		
        			cashPecPayBean2.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPSellDone); // 设置业务子类型为03TA_CR
        			cashPayRecAdmin.addList(cashPecPayBean2);
    			}
    			
    			String sSubTsfTypeCode = "";
    			if(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE.equals(strSupplyMode) || YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(strSupplyMode)){
    				sSubTsfTypeCode = YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSellDone + "," + YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPSellDone;
    			}else{
    				sSubTsfTypeCode = YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSellDone;
    			}
    			
    			cashPayRecAdmin.insert(dDate, dDate, 
    										YssOperCons.YSS_ZJDBLX_Fee, 
    										sSubTsfTypeCode,
    											"", sPortCode, "", "", "", 0);
			}else{
				cashPecPayBean.setMoney(dTransInsdDues);
				cashPecPayBean.setBaseCuryMoney(dBaseTransInsdDues);
				cashPecPayBean.setPortCuryMoney(dPortTransInsdDues);
				cashPecPayBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuyDone); // 设置业务子类型为03TA_IDB
    			cashPayRecAdmin.addList(cashPecPayBean);
    			cashPayRecAdmin.insert(dDate, dDate, 
    										YssOperCons.YSS_ZJDBLX_Fee, YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuyDone, 
    											"", sPortCode, "", "", "", 0);
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("ETF应付替代款结转出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}
	
	private String buildTransferSql(String strBS) throws YssException {
		String sQuerySql = "";
		try{
			if (etfParamBean.getSupplyMode().equals(
					YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) ||
				etfParamBean.getSupplyMode().equals(
						YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)) {
				sQuerySql =
						"select sum(FSumReturn) as FSumReturn,sum(FReplaceCash) as FReplaceCash," + 
						" FPortCode,FBuyDate ,FInOut" + 
						" from (select s.FPortCode,s.FSumReturn,s.FReplaceCash,s.FBuyDate, " + 
						" case when s.fsumreturn < 0 then 'S' else 'F' end as FInOut from " + 
						pub.yssGetTableName("Tb_ETF_StandingBook") + 
						" s join (select fportcode,fbuydate,max(fdate) as fdate from " + 
						pub.yssGetTableName("Tb_ETF_StandingBook") + 
						" where FPortCode = " + dbl.sqlString(sPortCode) + 
						" and FBS = " + dbl.sqlString(strBS) + 
						" and fdate <= " + dbl.sqlDate(dDate) +
						" group by fportcode , fbuydate) sb " + 
						" on sb.fbuydate = s.fbuydate and s.fdate = sb.fdate " + 
						" where s.FPortCode = " + dbl.sqlString(sPortCode) + 
						" and s.FRefundDate = " + dbl.sqlDate(dDate) + //退款日期为当日业务日期
						" and s.FSecurityCode <> ' ' and (s.FStockHolderCode <> ' ' OR s.FStockHolderCode IS NULL)" + //排除汇总明细数据
						" and s.FBS = " + dbl.sqlString(strBS) + 
						" ) group by FPortCode,FBuyDate,FInOut";
			}else{
				sQuerySql = "select a.*,p.FCashAccCode,p.FSupplyMode,p.FClearAccCode,p.FNORMSCALE,CA.FCuryCode " + 
						" from (select FPortCode,sum(FSumReturn) as FSumReturn,sum(FReplaceCash) as FReplaceCash,FBuyDate from " + 
						pub.yssGetTableName("Tb_ETF_StandingBook") + 
						" where FPortCode = " + dbl.sqlString(sPortCode) + 
						" and FRefundDate = " + dbl.sqlDate(dDate) + //退款日期为当日业务日期
						" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' OR FStockHolderCode IS NULL)" + //排除汇总明细数据
						" and FBS = " + dbl.sqlString(strBS) + 
						" group by FPortCode,FBuyDate )a left join ( " + 
						" select FCashAccCode,FClearAccCode ,FPortCode,FSupplyMode,FNORMSCALE from " + 
						pub.yssGetTableName("Tb_ETF_Param") + 
						" where FPortCode = " + dbl.sqlString(sPortCode) + 
						" ) p on p.FPortCode = a.FPortCode " + 
			            " left join (" +
			            " select ca1.FCashAccCode,ca1.FStartDate,ca2.FCuryCode from " +
			            " (select FCashAccCode, max(FStartDate) as FStartDate from " + 
			            pub.yssGetTableName("Tb_Para_CashAccount") +
			            " where FStartDate <= " + dbl.sqlDate(dDate) +
			            " and FCheckState = 1 and FState =0 group by FCashAccCode) ca1 join (select FCashAccCode,FStartDate,FCuryCode from " + 
			            pub.yssGetTableName("Tb_Para_CashAccount") +
			            " where FCheckState = 1 and FState =0) ca2 on ca2.FStartDate = ca1.FStartDate and ca2.FCashAccCode = ca1.FCashAccCode " +
			            ") CA on CA.FCashAccCode = p.FCashAccCode";	      
			}
		}catch(Exception e){
			throw new YssException(e.getMessage());
		}
		return sQuerySql;
	}

	/**
	 * 生成应付替代款
	 * 当日补票产生的应付替代款*数据方向（申购为正，赎回为负）进行汇总
	 * @throws YssException
	 */
	private void CreateETFCashInsteadDues(String strBS) throws YssException {
        CashPecPayBean cashPecPayBean = null;
        CashPayRecAdmin cashPayRecAdmin = null;
		String strSql = "";
		ResultSet rs = null;
		double dInsteadDues = 0;
		double dBaseInsteadDues = 0;
		double dPortInsteadDues = 0;
		String strCashAccCode = "";
		String strCuryCode = "";
        EachRateOper rateOper = new EachRateOper();     //新建获取利率的通用类
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        try{
			if (cashPayRecAdmin == null) {
				cashPayRecAdmin = new CashPayRecAdmin();
				cashPayRecAdmin.setYssPub(pub);
			}
			
			strSql = buildQuerySql(strBS);
			
            rs = dbl.queryByPreparedStatement(strSql);
            while(rs.next()){            	
            	if(rs.getString("FSUPPLYMODE").equals(YssOperCons.YSS_ETF_MAKEUP_ONE)){
            		dInsteadDues += calTotalInsteadDues(rs.getDate("fbuydate"),strBS,rs.getDouble("FMakeUpRepCash"),rs.getDouble("FNORMSCALE"));            			
            	}else{
            		dInsteadDues = rs.getDouble("FMakeUpRepCash");
            	}
				strCashAccCode = rs.getString("FCashAccCode");
				strCuryCode = rs.getString("FCuryCode");
	            
				BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
						strCuryCode, sPortCode, YssOperCons.YSS_RATE_BASE);// 获取当日的基础汇率
	            dBaseInsteadDues = this.getSettingOper().calBaseMoney(dInsteadDues, BaseCuryRate);//计算基础货币金额

	            rateOper.setYssPub(pub);
	            rateOper.getInnerPortRate(dDate, strCuryCode, sPortCode);
	            PortCuryRate = rateOper.getDPortRate();     //获取当日的组合汇率
				dPortInsteadDues = this.getSettingOper().calPortMoney(
											dInsteadDues, BaseCuryRate, PortCuryRate, strCuryCode,
														dDate, sPortCode); // 计算组合货币金额
	            
				cashPecPayBean = new CashPecPayBean();
				cashPecPayBean.setTradeDate(dDate);
				cashPecPayBean.setPortCode(sPortCode);
				cashPecPayBean.setBaseCuryRate(BaseCuryRate);
				cashPecPayBean.setPortCuryRate(PortCuryRate);
				if(rs.getString("FClearAccCode") == null 
				   || rs.getString("FClearAccCode").length() == 0 
				   || rs.getString("FSUPPLYMODE").equals(YssOperCons.YSS_ETF_MAKEUP_ONE)
				   || rs.getString("FSUPPLYMODE").equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ) ){
					cashPecPayBean.setCashAccCode(strCashAccCode);
				}else{
					cashPecPayBean.setCashAccCode(rs.getString("FClearAccCode"));
				}
				
				cashPecPayBean.setInOutType(1); 
				cashPecPayBean.setCuryCode(strCuryCode);
				cashPecPayBean.checkStateId = 1;                              // 设置审核状态为已审核
				cashPecPayBean.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);    // 设置业务类型为07

				if(strBS.equals("B")){
					cashPecPayBean.setMoney(dInsteadDues);
					cashPecPayBean.setBaseCuryMoney(dBaseInsteadDues);
					cashPecPayBean.setPortCuryMoney(dPortInsteadDues);
					cashPecPayBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuy); // 设置业务子类型为07TA_IDB
					
					cashPayRecAdmin.addList(cashPecPayBean);
				}else{
					cashPecPayBean.setMoney(-dInsteadDues);
					cashPecPayBean.setBaseCuryMoney(-dBaseInsteadDues);
					cashPecPayBean.setPortCuryMoney(-dPortInsteadDues);
					cashPecPayBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSell); // 设置业务子类型为07TA_IDS
					
					cashPayRecAdmin.addList(cashPecPayBean);
				}
            }
			cashPayRecAdmin.insert(dDate, dDate, 
									YssOperCons.YSS_ZJDBLX_Pay, 
									strBS.equals("B") ? YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuy : YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSell, 
									"", sPortCode, "", "", "", 0);
        }catch(Exception e){
        	throw new YssException("生成ETF应付替代款出错！",e);
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	private String buildQuerySql(String strBS) throws YssException {
		String strSql = "";
		
		try{
			if (etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) 
				 || etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)) {
				Date dTradeDate = getDateNotT(strBS);
	           strSql = "select x.*,p.FCashAccCode,p.FClearAccCode ,p.fnormscale,p.FSUPPLYMODE,CA.FCuryCode " +            			
	           			" from (select FPortCode,sum(FMakeUpRepCash) as FMakeUpRepCash,fbuydate from (" + 
			    		" select FPortCode,FMakeUpRepCash1 as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			    		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			    		(dTradeDate == null ? " and 1 = 2" : " and FMakeUpDate1 = " + dbl.sqlDate(dTradeDate) + 
			    		" and fbuydate = " + dbl.sqlDate(dTradeDate)) + 
			    		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" +
			    		" and FBS = " + dbl.sqlString(strBS) + 
			    		" and fdate = " + dbl.sqlDate(dDate) + 
			    		" union all " + 
			    		" select FPortCode,FMakeUpRepCash2 as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			     		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			     		" and FMakeUpDate2 = " + dbl.sqlDate(dDate) + 
			     		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" + 
			     		" and FBS = " + dbl.sqlString(strBS) + 
			     		" and FMakeUpDate2 = fdate" + 
			     		" union all " + 
			    		" select FPortCode,FMakeUpRepCash3 as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			     		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			     		" and FMakeUpDate3 = " + dbl.sqlDate(dDate) + 
			     		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" + 
			     		" and FBS = " + dbl.sqlString(strBS) + 
			     		" and FMakeUpDate3 = fdate" + 
			     		" union all " + 
			    		" select FPortCode,FMakeUpRepCash4 as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			     		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			     		" and FMakeUpDate4 = " + dbl.sqlDate(dDate) + 
			     		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" +
			     		" and FBS = " + dbl.sqlString(strBS) + 
			     		" and FMakeUpDate4 = fdate" + 
			     		" union all " + 
			    		" select FPortCode,FMakeUpRepCash5 as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			     		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			     		" and FMakeUpDate5 = " + dbl.sqlDate(dDate) + 
			     		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" + 
			     		" and FBS = " + dbl.sqlString(strBS) + 
			     		" and FMakeUpDate5 = fdate" + 
			     		" union all " + 
			    		" select FPortCode,FMustMkUpRepCash as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			     		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			     		" and FMustMkUpDate = " + dbl.sqlDate(dDate) + 
			     		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" +
			     		" and FBS = " + dbl.sqlString(strBS) + 
			     		" and FMustMkUpDate = fdate" + 
			     		" ) group by FPortCode,fbuydate ) x left join (" +              		
						" select FCashAccCode,FClearAccCode ,FPortCode,FNORMSCALE,FSUPPLYMODE from " + pub.yssGetTableName("Tb_ETF_Param") + 
						" where FPortCode = " + dbl.sqlString(sPortCode) + 
						" ) p on p.FPortCode = x.FPortCode " + 
			            " left join (" +
			            " select ca1.FCashAccCode,ca1.FStartDate,ca2.FCuryCode from " +
			            " (select FCashAccCode, max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_CashAccount") +
			            " where FStartDate <= " + dbl.sqlDate(dDate) +
			            " and FCheckState = 1 and FState =0 group by FCashAccCode) ca1 join (select FCashAccCode,FStartDate,FCuryCode from " + 
			            pub.yssGetTableName("Tb_Para_CashAccount") +
			            " where FCheckState = 1 and FState =0) ca2 on ca2.FStartDate = ca1.FStartDate and ca2.FCashAccCode = ca1.FCashAccCode " +
			            ") CA on CA.FCashAccCode = p.FCashAccCode";
			}else{
	           strSql = "select x.*,p.FCashAccCode,p.FClearAccCode ,p.fnormscale,p.FSUPPLYMODE,CA.FCuryCode " +            			
	           			" from (select FPortCode,sum(FMakeUpRepCash) as FMakeUpRepCash,fbuydate from (" + 
			    		" select FPortCode,FMakeUpRepCash1 as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			    		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			    		" and FMakeUpDate1 = " + dbl.sqlDate(dDate) + 
			    		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" +
			    		" and FBS = " + dbl.sqlString(strBS) + 
			    		" union all " + 
			    		" select FPortCode,FMakeUpRepCash2 as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			     		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			     		" and FMakeUpDate2 = " + dbl.sqlDate(dDate) + 
			     		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" + 
			     		" and FBS = " + dbl.sqlString(strBS) + 
			     		" union all " + 
			    		" select FPortCode,FMakeUpRepCash3 as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			     		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			     		" and FMakeUpDate3 = " + dbl.sqlDate(dDate) + 
			     		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" + 
			     		" and FBS = " + dbl.sqlString(strBS) + 
			     		" union all " + 
			    		" select FPortCode,FMakeUpRepCash4 as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			     		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			     		" and FMakeUpDate4 = " + dbl.sqlDate(dDate) + 
			     		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" +
			     		" and FBS = " + dbl.sqlString(strBS) + 
			     		" union all " + 
			    		" select FPortCode,FMakeUpRepCash5 as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			     		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			     		" and FMakeUpDate5 = " + dbl.sqlDate(dDate) + 
			     		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" + 
			     		" and FBS = " + dbl.sqlString(strBS) + 
			     		" union all " + 
			    		" select FPortCode,FMustMkUpRepCash as FMakeUpRepCash,fbuydate from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			     		" where FPortCode = " + dbl.sqlString(sPortCode) + 
			     		" and FMustMkUpDate = " + dbl.sqlDate(dDate) + 
			     		" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" +
			     		" and FBS = " + dbl.sqlString(strBS) + 
			     		" ) group by FPortCode,fbuydate ) x left join (" +              		
						" select FCashAccCode,FClearAccCode ,FPortCode,FNORMSCALE,FSUPPLYMODE from " + pub.yssGetTableName("Tb_ETF_Param") + 
						" where FPortCode = " + dbl.sqlString(sPortCode) + 
						" ) p on p.FPortCode = x.FPortCode " + 
			            " left join (" +
			            " select ca1.FCashAccCode,ca1.FStartDate,ca2.FCuryCode from " +
			            " (select FCashAccCode, max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_CashAccount") +
			            " where FStartDate <= " + dbl.sqlDate(dDate) +
			            " and FCheckState = 1 and FState =0 group by FCashAccCode) ca1 join (select FCashAccCode,FStartDate,FCuryCode from " + 
			            pub.yssGetTableName("Tb_Para_CashAccount") +
			            " where FCheckState = 1 and FState =0) ca2 on ca2.FStartDate = ca1.FStartDate and ca2.FCashAccCode = ca1.FCashAccCode " +
			            ") CA on CA.FCashAccCode = p.FCashAccCode";
			}
        }catch(Exception e){
        	throw new YssException(e.getMessage());
        }
		return strSql;
	}

	/**
	 * STORY #1434 易方达跨境ETF
	 * QDV4易方达基金2011年7月27日01_A
	 * @return
	 */
	private double calTotalInsteadDues(Date buyDate,String strBS,double unitValue,double dNormScale) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
		double dTotal = 0;
		try{
			buff = new StringBuffer(100);
			buff.append("select sum(ftradeamount) as ftradeamount,fstockholdercode from ")
				.append(pub.yssGetTableName("tb_etf_ghinterface"))
				.append(" where fopertype = '2ndcode' and fmark = ")
				.append(strBS.equals("B") ? "'S'" : "'B'")
				.append(" and fportcode = ").append(dbl.sqlString(sPortCode))
				.append(" and fbargaindate = ").append(dbl.sqlDate(buyDate))
				.append("  group by fstockholdercode");
			rs = dbl.queryByPreparedStatement(buff.toString());
			while(rs.next()){
				//投资者汇总金额  = 单位篮子应付替代款 * 各投资者申赎份额 / 最小申赎份额 
				dTotal += YssD.round(YssD.mul(unitValue, 
										YssD.div(rs.getDouble("ftradeamount"), dNormScale)),2);
			}
        }catch(Exception e){
        	throw new YssException("汇总投资者应付替代款出错！",e);
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
		return dTotal;
	}
	/**  
	 * 根据确认日期得出交易日期
	 * @param date
	 * @return
	 * @throws YssException
	 * @author arealw ,2011-11-25
	 * @modified
	 */
	private Date getDateNotT(String strBS) throws YssException {
		
		Date dBuyDate = null;
		StringBuffer buffer = new StringBuffer(100);
		ResultSet rs = null;
		try{
			buffer.append("SELECT distinct ftradedate FROM  ")
				.append(pub.yssGetTableName("TB_TA_TRADE"))
				.append(" WHERE FCONFIMDATE = ").append(dbl.sqlDate(dDate))
				.append(" AND FPORTCODE = ").append(dbl.sqlString(sPortCode))
				.append(" AND FSELLTYPE in ")
				.append("B".equals(strBS)? "'01'":"'02'")
				.append(" AND FCHECKSTATE = 1");
			rs = dbl.queryByPreparedStatement(buffer.toString());
			if(rs.next()){
				dBuyDate = rs.getDate("ftradedate");
			}
		}catch (Exception e) {
			throw new YssException("获取申赎日期出错 ！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dBuyDate;
	}
	
	/**
	 * 获取现金账户对应的币种
	 * @return
	 * @throws YssException
	 */
	private String getCuryCodeByCashAccCode(String sCashAccCode) throws YssException {
		String sCuryCode = "";
		StringBuffer buf = null;
		ResultSet rs = null;
		try{
			buf = new StringBuffer(200);
			buf.append("select ca.fcashacccode,ca.fcurycode from ")
				.append(pub.yssGetTableName("TB_PARA_CASHACCOUNT"))
				.append(" ca where ca.fcheckstate = 1")
				.append(" and ca.fcashacccode = ").append(dbl.sqlString(sCashAccCode));
			
			rs = dbl.queryByPreparedStatement(buf.toString());			
			buf.delete(0, buf.length());
			if(rs.next()){
				sCuryCode = rs.getString("fcurycode");
			}
		}catch (Exception e) {
			throw new YssException("获取现金账户币种出错 ！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}

		return sCuryCode;
	}
	
	/**
	 * 设置应付替代款资金调拨属性
	 * @throws YssException
	 */
	private void doCashTrans(CashTransAdmin cashTransAdmin, String strBS,
					double dTransInsdDues, double BaseCuryRate, double PortCuryRate) throws YssException {
		
		TransferSetBean transferSet = new TransferSetBean();
		if(dTransInsdDues > 0){
            transferSet.setIInOut(-1); //流出
            transferSet.setDMoney(dTransInsdDues); //设置金额
		}else{
            transferSet.setIInOut(1); //流入
            transferSet.setDMoney(-dTransInsdDues); //设置金额
		}
        transferSet.setSPortCode(sPortCode);
        //设置现金账户
        String cashAccCode = "";
        if(etfParamBean.getClearAccCode() == null || 
        		etfParamBean.getClearAccCode().trim().length() == 0 || 
				YssOperCons.YSS_ETF_MAKEUP_ONE.equals(etfParamBean.getSupplyMode()) ||
				YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(etfParamBean.getSupplyMode()) ){
        	cashAccCode = etfParamBean.getCashAccCode();
        }else{
        	cashAccCode = etfParamBean.getClearAccCode();
        }
        transferSet.setSCashAccCode(cashAccCode); 
        transferSet.setDBaseRate(BaseCuryRate);
        transferSet.setDPortRate(PortCuryRate);
        transferSet.checkStateId = 1;
        
        TransferBean transfer = new TransferBean();
        transfer.setDtTransDate(dDate); 
        transfer.setDtTransferDate(dDate); //调拨日期为运营收支品种开始日期
        transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Capital);
        if(strBS.equals("S")){
        	transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSellTrans);
        }else{
        	transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuyTrans);
        }
        transfer.setFNumType("ETF_ID"); //设置关联编号类型为ETF_ID
        transfer.checkStateId = 1;
        transfer.setDataSource(1);
        
        ArrayList subTransfer = new ArrayList();      //实例化放置资金调拨子数据的容器
        subTransfer.add(transferSet);       //将资金调拨子数据放入容器
        transfer.setSubTrans(subTransfer);  //将子数据放入资金调拨中
        cashTransAdmin.addList(transfer);
	}
	
	/**
	 * 华夏恒指ETF
	 * 应付替代款（申购）区分退款和补款
	 * @param cashTransAdmin
	 * @param sType
	 * @param dTransInsdDues
	 * @param BaseCuryRate
	 * @param PortCuryRate
	 * @throws YssException
	 */
	private void doCashTransHX(CashTransAdmin cashTransAdmin, String sType,
			double dValue, double BaseCuryRate, double PortCuryRate) throws YssException {
			
		TransferSetBean transferSet = new TransferSetBean();
		if(dValue > 0){
		    transferSet.setIInOut(-1); //流出
		    transferSet.setDMoney(dValue); //设置金额
		}else{
		    transferSet.setIInOut(1); //流入
		    transferSet.setDMoney(-dValue); //设置金额
		}
		transferSet.setSPortCode(sPortCode);
		//设置现金账户
		if(etfParamBean.getClearAccCode() == null
		   || etfParamBean.getClearAccCode().trim().length() == 0
		   || YssOperCons.YSS_ETF_MAKEUP_ONE.equals(etfParamBean.getSupplyMode())
		   || YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(etfParamBean.getSupplyMode())){
			transferSet.setSCashAccCode(etfParamBean.getCashAccCode());
		}else{
			transferSet.setSCashAccCode(etfParamBean.getClearAccCode());
		}
		transferSet.setDBaseRate(BaseCuryRate);
		transferSet.setDPortRate(PortCuryRate);
		transferSet.checkStateId = 1;
		
		TransferBean transfer = new TransferBean();
		transfer.setDtTransDate(dDate); 
		transfer.setDtTransferDate(dDate); //调拨日期为运营收支品种开始日期
		transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Capital);
		if(sType.equals("TK")){
			transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuyTrans);
		}else{
			transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuyTrans_BK);
		}
		transfer.setFNumType("ETF_ID"); //设置关联编号类型为ETF_ID
		transfer.checkStateId = 1;
		transfer.setDataSource(1);
		
		ArrayList subTransfer = new ArrayList();      //实例化放置资金调拨子数据的容器
		subTransfer.add(transferSet);       //将资金调拨子数据放入容器
		transfer.setSubTrans(subTransfer);  //将子数据放入资金调拨中
		cashTransAdmin.addList(transfer);
	}	
}
