package com.yss.main.operdeal.opermanage.etf;

import java.sql.ResultSet;
import java.util.*;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.commeach.EachRateOper;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.util.*;
import com.yss.main.cashmanage.*;
import com.yss.main.etfoperation.*;
import com.yss.main.etfoperation.pojo.*;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;

/**
 * 现金替代和现金差额结转
 * MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A
 * @author panjunfang
 * create 20091023
 */
public class CashInsBalTransManage extends BaseOperManage {

	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.sPortCode = portCode;
		this.dDate = dDate;
	}

	public void doOpertion() throws YssException {
		ETFParamSetAdmin etfParamAdmin = new ETFParamSetAdmin();
		etfParamAdmin.setYssPub(pub);
		ETFParamSetBean etfParamBean = new ETFParamSetBean();
		HashMap hm = new HashMap();
		hm = etfParamAdmin.getETFParamInfo(sPortCode);
		etfParamBean = (ETFParamSetBean)hm.get(sPortCode);
		if(etfParamBean == null){
			throw new YssException("组合【" + sPortCode + "】对应的ETF参数设置不存在或未审核！");
		}
		transferCashBalance(etfParamBean);//结转现金差额
		transferPurchaseReplaceCash(etfParamBean);//结转现金替代（申购）		
		if(etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)){
			transferCashInsMust(etfParamBean);//赎回必须现金替代结转
		}
		else if(etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)){
			transferCashMust(etfParamBean);
		}
		else if(etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_ONE)){
			transferRedeemMustReplaceCash(etfParamBean);//结转应付赎回款-ETF赎回-必须现金替代款	
			transferRedeemMayReplaceCash(etfParamBean);//结转应付赎回款-ETF赎回-可以现金替代款
		}
	}

	/**
	 * 结转应付赎回款-ETF赎回-可以现金替代款
	 * 在补票当日将可以现金替代款冲减掉
	 */
	private void transferRedeemMayReplaceCash(ETFParamSetBean paramSet) throws YssException {
		StringBuffer sbSql = null;
		ResultSet rs = null;
        CashPecPayBean cashPecPay = null;
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);
		try{
			sbSql = new StringBuffer(200);
			sbSql.append("select x.FBUYDATE,x.FREPLACECASH,y.*, ca.FCuryCode,")
				 .append("c.FNormScale,c.FCashAccCode,c.FSUPPLYMODE,c.FClearAccCode from (")
				 .append("select sum(FREPLACECASH) as FREPLACECASH,FBUYDATE from ")
				 .append(pub.yssGetTableName("Tb_ETF_StandingBook"))//台帐表
				 .append(" where FPortCode = ").append(dbl.sqlString(sPortCode))
				 .append(" and FMAKEUPDATE1 =").append(dbl.sqlDate(dDate))//退款日期为当前业务日期
				 .append(" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' OR FStockHolderCode IS NULL)")
				 .append(" and FBS = 'S' group by FBUYDATE) x left join (select FNum,FPortCode,FTradeDate,")
				 .append(" FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FPortCuryRate,FBaseCuryRate,")
				 .append(" fsellamount,FCashRepAmount from ").append(pub.yssGetTableName("Tb_TA_Trade"))//TA交易数据
				 .append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(sPortCode))
				 .append(" and FSellType in ('02')")//交易方式为赎回
				 .append(" ) y on x.FBUYDATE = y.FTradeDate")
				 .append(" left join (select FPortCode ,FNormScale,FCashAccCode,FClearAccCode,FSUPPLYMODE from ")
				 .append(pub.yssGetTableName("Tb_ETF_Param"))
				 .append(" where fcheckstate = 1) c on y.FPortCode = c.FPortCode")
				 .append(" left join (select FCashAccCode,FCuryCode from ")
				 .append(pub.yssGetTableName("Tb_Para_Cashaccount"))
				 .append(" where FCheckState = 1 and FState =0) ca on ca.FCashAccCode = c.FCashAccCode");
			rs = dbl.queryByPreparedStatement(sbSql.toString());
			while(rs.next()){
				cashPecPay = new CashPecPayBean();//冲减现金应收应付
        		cashPecPay.setMoney(YssD.mul(rs.getDouble("FREPLACECASH"), //单位篮子必须现金替代金额 × 篮子数
												YssD.div(rs.getDouble("fsellamount"), rs.getDouble("FNormScale"))));
				cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);//业务类型	
				cashPecPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPSellDone);//业务子类型
				cashPecPay.setRelaOrderNum(rs.getString("FNum"));
				setCashRecPayProperty(rs,cashPecPay,paramSet);//设置现金应收应付其他属性
				cashPayRecAdmin.addList(cashPecPay);
			}
			cashPayRecAdmin.insert(dDate, dDate, 
									YssOperCons.YSS_ZJDBLX_Fee, 
									YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPSellDone , 
									"", sPortCode, "", "", "", 0);
			
		}catch(Exception e){
			throw new YssException("冲减应付赎回款-ETF赎回-可以现金替代款出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}	
	}

	/**
	 * 结转应付赎回款-ETF赎回-必须现金替代款
	 * @throws YssException
	 */
	private void transferRedeemMustReplaceCash(ETFParamSetBean paramSet) throws YssException {
		StringBuffer sbSql = null;
		ResultSet rs = null;
        CashPecPayBean cashPecPay = null;
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);
        
        CashTransAdmin cashTransAdmin = new CashTransAdmin();
        cashTransAdmin.setYssPub(pub);
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
		try{
			sbSql = new StringBuffer(200);
			sbSql.append("select x.FBUYDATE,y.*, ca.FCuryCode,c.FNormScale,")
				 .append("c.FSUPPLYMODE,c.FCashAccCode,c.FClearAccCode,s.FTotalMoney from (")
				 .append("select distinct FBUYDATE from ").append(pub.yssGetTableName("Tb_ETF_StandingBook"))//台帐表
				 .append(" where FPortCode = ").append(dbl.sqlString(sPortCode))
				 .append(" and FRefundDate =").append(dbl.sqlDate(dDate))//退款日期为当前业务日期
				 .append(" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' OR FStockHolderCode IS NULL)")
				 .append(" and FBS = 'S' ) x left join (select FNum,FPortCode,FTradeDate,")
				 .append(" FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FPortCuryRate,FBaseCuryRate,")
				 .append(" fsellamount,FCashRepAmount from ").append(pub.yssGetTableName("Tb_TA_Trade"))//TA交易数据
				 .append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(sPortCode))
				 .append(" and FSellType in ('02')")//交易方式为赎回
				 .append(" ) y on x.FBUYDATE = y.FTradeDate")
				 .append(" left join (select FDate,fportcode,sum(FTotalMoney) as FTotalMoney from ")
				 .append(pub.yssGetTableName("Tb_ETF_Stocklist"))
				 .append(" where fportcode = ").append(dbl.sqlString(sPortCode))
				 .append(" and freplacemark in('6') group by FDate,fportcode ) s")
				 .append(" on x.FBUYDATE = s.FDate")
				 .append(" left join (select FPortCode ,FNormScale,FCashAccCode,FClearAccCode,FSUPPLYMODE from ")
				 .append(pub.yssGetTableName("Tb_ETF_Param"))
				 .append(" where fcheckstate = 1) c on y.FPortCode = c.FPortCode")
				 .append(" left join (select FCashAccCode,FCuryCode from ")
				 .append(pub.yssGetTableName("Tb_Para_Cashaccount"))
				 .append(" where FCheckState = 1 and FState =0) ca on ca.FCashAccCode = c.FCashAccCode");
			rs = dbl.queryByPreparedStatement(sbSql.toString());
			while(rs.next()){
				cashPecPay = new CashPecPayBean();//冲减现金应收应付
        		cashPecPay.setMoney(YssD.mul(rs.getDouble("FTotalMoney"), //单位篮子必须现金替代金额 × 篮子数
												YssD.div(rs.getDouble("fsellamount"), rs.getDouble("FNormScale"))));
				cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);//业务类型	
				cashPecPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustRPSellDone);//业务子类型
				cashPecPay.setRelaOrderNum(rs.getString("FNum"));
				setCashRecPayProperty(rs,cashPecPay,paramSet);//设置现金应收应付其他属性
				cashPayRecAdmin.addList(cashPecPay);
				
				transfer = new TransferBean();//资金调拨
				transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustTrans);
				transfer.setFNumType("ETFSHMUST");
                setTransfer(rs,transfer);         //设置资金调拨数据
                
                transferSet = new TransferSetBean();//资金调拨子表
    			transferSet.setIInOut(-1);                 		 
            	transferSet.setDMoney(YssD.mul(rs.getDouble("FTotalMoney"), //单位篮子必须现金替代金额 × 篮子数
												YssD.div(rs.getDouble("fsellamount"), rs.getDouble("FNormScale"))));

                setTransferSet(rs,transferSet,paramSet);  //设置资金调拨子表其他属性
                subTransfer = new ArrayList();      //实例化放置资金调拨子数据的容器
                subTransfer.add(transferSet);       //将资金调拨子数据放入容器
                transfer.setSubTrans(subTransfer);  //将子数据放入资金调拨中
                cashTransAdmin.addList(transfer);
			}
			cashPayRecAdmin.insert(dDate, dDate, 
									YssOperCons.YSS_ZJDBLX_Fee, 
									YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustRPSellDone , 
									"", sPortCode, "", "", "", 0);
			cashTransAdmin.insert("",dDate,null,//根据编号类型、业务类型等删除原有调拨数据
									YssOperCons.YSS_ZJDBLX_Capital,YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustTrans,
									"","","","","","ETFSHMUST","",1,"",sPortCode,0,"","","",true,"");	
			
		}catch(Exception e){
			throw new YssException("结转应付赎回款-ETF赎回-必须现金替代款出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}	
	}

	/**
	 * 结转现金替代款（可以现金替代和必须现金替代）-ETF申购
	 */
	private void transferPurchaseReplaceCash(ETFParamSetBean paramSet) throws YssException {
		StringBuffer sbSql = null;
		ResultSet rs = null;
		CashPecPayBean cashPecPay1 = null;//存放可以现金替代冲减应收应付
		CashPecPayBean cashPecPay2 = null;//存放必须现金替代冲减应收应付
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();//现金应收应付控制类
        cashPayRecAdmin.setYssPub(pub);
        
        TransferBean transfer1 = null;//可以现金替代资金调拨
        TransferSetBean transferSet1 = null;
        ArrayList subTransfer1 = null;
        TransferBean transfer2 = null;//必须现金替代资金调拨
        TransferSetBean transferSet2 = null;
        ArrayList subTransfer2 = null;
        CashTransAdmin cashTransAdmin = new CashTransAdmin();//资金调拨控制类
        cashTransAdmin.setYssPub(pub);
		try{
			sbSql = new StringBuffer(200);
			sbSql.append("select x.*,y.FClearAccCode,y.FNormScale,z.FMustMoney from ")
		      	  .append(pub.yssGetTableName("Tb_TA_Trade"))//从TA交易数据中获取申购替代金额总额及结转日期
		      	  .append(" x left join (select FPortCode,FClearAccCode,FNormScale from ")
		      	  .append(pub.yssGetTableName("Tb_ETF_Param"))//从ETF参数设置中获取备付金结转账户和最小申赎份额
		      	  .append(" ) y on y.FPortCode = x.FPortCode ")
		      	  .append(" left join (select FPortCode,FDate,sum(FTotalMoney) as FMustMoney from ")
		      	  .append(pub.yssGetTableName("Tb_ETF_StockList"))//从股票篮中获取单位篮子必须现金替代金额
		      	  .append(" where FCheckState = 1 and FReplaceMark in ('2','6')")//必须现金替代标识（深交所：2   上交所：6）
		      	  .append(" and fsecuritycode <> ").append(dbl.sqlString(paramSet.getCapitalCode()))//去掉虚拟成份股 arealw 20111126 增加
		      	  .append("group by FPortCode,FDate) z on z.FPortCode = x.fportcode and z.FDate = x.ftradedate")
		      	  .append(" where x.FCashReplaceDate = ").append(dbl.sqlDate(dDate))
		      	  .append(" and x.FPortCode = ").append(dbl.sqlString(sPortCode))
		      	  .append(" and x.FSellType in ('01') and x.FCheckState = 1");
			rs = dbl.queryByPreparedStatement(sbSql.toString());
			while(rs.next()){
				cashPecPay1 = new CashPecPayBean();//冲减现金应收应付（可以现金替代）
        		//可以现金替代款金额 = 申购总替代金额 - 必须现金替代金额
        		cashPecPay1.setMoney(YssD.sub(rs.getDouble("FCashRepAmount"), 
        										YssD.mul(rs.getDouble("FMustMoney"),
        												YssD.div(rs.getDouble("FSellAmount"),rs.getDouble("FNormScale")))));
				cashPecPay1.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);//业务类型
				cashPecPay1.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPBuyDone);//业务子类型
				cashPecPay1.setRelaOrderNum(rs.getString("FNum"));
				setCashRecPayProperty(rs,cashPecPay1,paramSet);//设置现金应收应付其他属性
				if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(paramSet.getSupplyMode())
						|| YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(paramSet.getSupplyMode())){
					//易方达ETF 现金替代 T+1日结转到备付金账户，T+2日备付金账户结转到存款账户
					cashPecPay1.setCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
															rs.getString("FCashAccCode") : rs.getString("FClearAccCode"));
				}
				cashPayRecAdmin.addList(cashPecPay1);
				
				transfer1 = new TransferBean();//资金调拨（可以现金替代）
				transfer1.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsteadTrans);
				transfer1.setFNumType("ETFSG");
                setTransfer(rs,transfer1);    //设置资金调拨数据
                
                transferSet1 = new TransferSetBean();//资金调拨子表
              //可以现金替代款调拨金额 = 申购总替代金额 - 必须现金替代金额
                transferSet1.setDMoney(YssD.sub(rs.getDouble("FCashRepAmount"), 
												YssD.mul(rs.getDouble("FMustMoney"),
														YssD.div(rs.getDouble("FSellAmount"),rs.getDouble("FNormScale")))));
                transferSet1.setIInOut(1); 
                setTransferSet(rs,transferSet1,paramSet);  //设置资金调拨子表其他属性
				if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(paramSet.getSupplyMode())
						|| YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(paramSet.getSupplyMode())){
					//易方达ETF 现金替代 T+1日结转到备付金账户，T+2日备付金账户结转到存款账户
					transferSet1.setSCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
															rs.getString("FCashAccCode") : rs.getString("FClearAccCode"));
				}
                subTransfer1 = new ArrayList();      //实例化放置资金调拨子数据的容器
                subTransfer1.add(transferSet1);       //将资金调拨子数据放入容器
                transfer1.setSubTrans(subTransfer1);  //将子数据放入资金调拨中
                cashTransAdmin.addList(transfer1);
                
				cashPecPay2 = new CashPecPayBean();//冲减现金应收应付（必须现金替代）
        		//必须现金替代金额 = 单位篮子必须现金替代金额 × 篮子数
        		cashPecPay2.setMoney(YssD.mul(rs.getDouble("FMustMoney"),
        												YssD.div(rs.getDouble("FSellAmount"),rs.getDouble("FNormScale"))));
				cashPecPay2.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);//业务类型
				cashPecPay2.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashMustInsRPBuyDone);//业务子类型
				cashPecPay2.setRelaOrderNum(rs.getString("FNum"));
				setCashRecPayProperty(rs,cashPecPay2,paramSet);//设置现金应收应付其他属性
				if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(paramSet.getSupplyMode())
						|| YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(paramSet.getSupplyMode())){
					//易方达ETF 现金替代 T+1日结转到备付金账户，T+2日备付金账户结转到存款账户
					cashPecPay2.setCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
															rs.getString("FCashAccCode") : rs.getString("FClearAccCode"));
				}
				cashPayRecAdmin.addList(cashPecPay2);
				
				transfer2 = new TransferBean();//资金调拨（必须现金替代）
				transfer2.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsteadTrans);
				transfer2.setFNumType("ETFSG");
                setTransfer(rs,transfer2);    //设置资金调拨数据
                
                transferSet2 = new TransferSetBean();//资金调拨子表
                //必须现金替代调拨金额 = 单位篮子必须现金替代金额 × 篮子数
                transferSet2.setDMoney(YssD.mul(rs.getDouble("FMustMoney"),
														YssD.div(rs.getDouble("FSellAmount"),rs.getDouble("FNormScale"))));
                transferSet2.setIInOut(1); 
                setTransferSet(rs,transferSet2,paramSet);  //设置资金调拨子表其他属性
				if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(paramSet.getSupplyMode())
						|| YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(paramSet.getSupplyMode())){
					//易方达ETF 现金替代 T+1日结转到备付金账户，T+2日备付金账户结转到存款账户
					transferSet2.setSCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
															rs.getString("FCashAccCode") : rs.getString("FClearAccCode"));
				}
                subTransfer2 = new ArrayList();      //实例化放置资金调拨子数据的容器
                subTransfer2.add(transferSet2);       //将资金调拨子数据放入容器
                transfer2.setSubTrans(subTransfer2);  //将子数据放入资金调拨中
                cashTransAdmin.addList(transfer2);
			}
			cashPayRecAdmin.insert(dDate, dDate, 
								YssOperCons.YSS_ZJDBLX_Income, 
								YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPBuyDone + "," + YssOperCons.YSS_ZJDBZLX_ETF_CashMustInsRPBuyDone, 
								"", sPortCode, "", "", "", 0);
			cashTransAdmin.insert("",dDate,null,
								YssOperCons.YSS_ZJDBLX_Capital,YssOperCons.YSS_ZJDBZLX_ETF_CashInsteadTrans,
								"","","","","","ETFSG","",1,"",sPortCode,0,"","","",true,"");	//根据关联编号、编号类型、业务类型等删除原有调拨数据	
			
		}catch(Exception e){
			throw new YssException("结转现金替代款-ETF申购出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 结转现金差额（申购、赎回）
	 */
	private void transferCashBalance(ETFParamSetBean paramSet) throws YssException {
		StringBuffer sbSql = null;
		ResultSet rs = null;
        CashPecPayBean cashPecPay = null;
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);
        CashTransAdmin cashTransAdmin = new CashTransAdmin();
        cashTransAdmin.setYssPub(pub);
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
		try{
			sbSql = new StringBuffer(200);
			sbSql.append("select x.* ,y.FClearAccCode from (select FNum,FPortCode,FTradeDate,FSellType,FCuryCode,")
				 .append("FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FPortCuryRate,FBaseCuryRate,FCashAccCode,")
				 .append("FCashBal as FMoney from ").append(pub.yssGetTableName("Tb_TA_Trade"))//取TA交易数据中现金差额
				 .append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(sPortCode))
				 .append(" and FSellType in ('01','02')")//交易方式为申购、赎回
				 .append(" and FCashBalanceDate = ").append(dbl.sqlDate(dDate))//结转日期为当前业务日期
				 .append(" ) x left join (select FPortCode,FClearAccCode from ")
				 .append(pub.yssGetTableName("Tb_ETF_Param"))//从ETF参数设置中关联出清算备付金账户
			 	 .append(" ) y on y.FPortCode = x.FPortCode");
			rs = dbl.queryByPreparedStatement(sbSql.toString());
			while(rs.next()){
				cashPecPay = new CashPecPayBean();//冲减现金应收应付
				cashPecPay.setMoney(rs.getDouble("FMoney"));
				if(rs.getString("FSellType").equals("01")){//如果为申购
					cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);//业务类型
					cashPecPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPBuyDone);//业务子类型			
				}else{
					cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);//业务类型	
					cashPecPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPSellDone);//业务子类型
				}
				cashPecPay.setRelaOrderNum(rs.getString("FNum"));
				setCashRecPayProperty(rs,cashPecPay,paramSet);//设置现金应收应付其他属性
				cashPayRecAdmin.addList(cashPecPay);
				
				transfer = new TransferBean();//资金调拨
				transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashBalTrans);
				transfer.setFNumType("ETF" + ("01".equals(rs.getString("FSellType")) ? "SG" : "SH"));
                setTransfer(rs,transfer);         //设置资金调拨数据
                
                transferSet = new TransferSetBean();//资金调拨子表
                if ((rs.getString("FSellType").equals("01") && rs.getDouble("FMoney") > 0)|| 
                		(rs.getString("FSellType").equals("02") && rs.getDouble("FMoney") < 0)) {//申购且金额为正、赎回且金额为负
                	transferSet.setIInOut(1);//设置为流入				
    			}else{
    				transferSet.setIInOut(-1);   
    			}
            	if(rs.getDouble("FMoney") > 0){                		 
            		transferSet.setDMoney(rs.getDouble("FMoney")); 
            	}else{
            		transferSet.setDMoney(-rs.getDouble("FMoney")); 
            	} 
                setTransferSet(rs,transferSet,paramSet);  //设置资金调拨子表其他属性
                subTransfer = new ArrayList();      //实例化放置资金调拨子数据的容器
                subTransfer.add(transferSet);       //将资金调拨子数据放入容器
                transfer.setSubTrans(subTransfer);  //将子数据放入资金调拨中
                cashTransAdmin.addList(transfer);
			}
			cashPayRecAdmin.insert(dDate, dDate, 
								YssOperCons.YSS_ZJDBLX_Income + "," + YssOperCons.YSS_ZJDBLX_Fee, 
								YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPBuyDone + "," + YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPSellDone, 
								"", sPortCode, "", "", "", 0);
			cashTransAdmin.insert("",dDate,null,
								YssOperCons.YSS_ZJDBLX_Capital,YssOperCons.YSS_ZJDBZLX_ETF_CashBalTrans,
								"","","","","","ETFSG,ETFSH","",1,"",sPortCode,0,"","","",true,"");	//根据关联编号、编号类型、业务类型等删除原有调拨数据	
			
		}catch(Exception e){
			throw new YssException("结转现金差额出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}

	private void setCashRecPayProperty(ResultSet rs,CashPecPayBean cashPecPay,ETFParamSetBean paramSet) throws YssException {
		double BaseCuryRate = 0;
		double PortCuryRate = 0;
		double Money = 0;
		double BaseMoney = 0;
		double PortMoney = 0;
		EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
		try {
			BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取当日基础汇率
					rs.getString("FCuryCode"), rs.getString("FPortCode"),
					YssOperCons.YSS_RATE_BASE);

			rateOper.setYssPub(pub);
			rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), 
												rs.getString("FPortCode"));
			PortCuryRate = rateOper.getDPortRate(); //获取当日组合汇率
			BaseMoney = this.getSettingOper().calBaseMoney(cashPecPay.getMoney(), BaseCuryRate); //计算基础货币金额
			PortMoney = this.getSettingOper().calPortMoney(cashPecPay.getMoney(), BaseCuryRate,
					PortCuryRate, rs.getString("FCuryCode"), dDate,
					rs.getString("FPortCode")); //计算组合货币金额

			cashPecPay.setTradeDate(dDate);
			cashPecPay.setPortCode(rs.getString("FPortCode"));
			cashPecPay.setBaseCuryRate(BaseCuryRate);
			cashPecPay.setPortCuryRate(PortCuryRate);
			cashPecPay.setMoney(cashPecPay.getMoney());
			cashPecPay.setBaseCuryMoney(BaseMoney);
			cashPecPay.setPortCuryMoney(PortMoney);
			if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(paramSet.getSupplyMode()) ||
					YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(paramSet.getSupplyMode())){
				cashPecPay.setCashAccCode(rs.getString("FCashAccCode"));
			}else{
				cashPecPay.setCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
						rs.getString("FCashAccCode") : rs.getString("FClearAccCode"));
			}
			cashPecPay.setInOutType(1); 
			cashPecPay.setCuryCode(rs.getString("FCuryCode"));
			cashPecPay.checkStateId = 1;  
			
		} catch (Exception e) {
			throw new YssException("设置现金差额和现金替代应收应付数据时出现异常！", e);
		}
	}

	/**
	 * 设置资金调拨子表数据
	 * @param rs
	 * @param transferSet
	 * @throws YssException
	 */
	private void setTransferSet(ResultSet rs ,TransferSetBean transferSet,ETFParamSetBean paramSet) throws YssException {
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        try {
            double dBaseRate = 1;
            double dPortRate = 1;

            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("FCuryCode"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE); //获取业务当天的基础汇率

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                      rs.getString("FPortCode"));
            dPortRate = rateOper.getDPortRate(); //获取业务当天的组合汇率

            transferSet.setSPortCode(rs.getString("FPortCode"));
            transferSet.setSAnalysisCode1(rs.getString("FAnalysisCode1") == null ? "" :
                                          rs.getString("FAnalysisCode1"));
            transferSet.setSAnalysisCode2(rs.getString("FAnalysisCode2") == null ? "" :
                                          rs.getString("FAnalysisCode2"));
            transferSet.setSAnalysisCode3(rs.getString("FAnalysisCode3") == null ? "" :
                                          rs.getString("FAnalysisCode3"));
            if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(paramSet.getSupplyMode())||
            		YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(paramSet.getSupplyMode())){
                transferSet.setSCashAccCode(rs.getString("FCashAccCode")); //设置现金账户
            }else{
                transferSet.setSCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
														rs.getString("FCashAccCode") : rs.getString("FClearAccCode")); //设置现金账户
            }            
            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
	}

	private void setTransfer(ResultSet rs,TransferBean transfer) throws YssException {
        try {
            transfer.setDtTransDate(rs.getDate("FTradeDate")); //业务日期为TA 数据交易日期
            transfer.setDtTransferDate(dDate); //调拨日期为结转日期
            transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Capital);
            transfer.setFRelaNum(rs.getString("FNum"));
            transfer.setDataSource(1);
            transfer.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
	}
	
	/**
	 * 产生赎回必须现金替代的资金调拨（华夏恒指ETF）
	 * @param paramSet
	 * @throws YssException
	 */
	private void transferCashInsMust(ETFParamSetBean paramSet) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
		Date dTransDate = null;//赎回日期
		EachExchangeHolidays holiday = null;//节假日代码类
		String sRowStr ="";
		HashMap holidays = null;
        CashPecPayBean cashPecPay = null;
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);
        CashTransAdmin cashTransAdmin = new CashTransAdmin();
        cashTransAdmin.setYssPub(pub);
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
		try{
			holidays = paramSet.getHoildaysRela();//获取保存节假日代码的hash表
			
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			//拼接参数：节假日代码+当天偏离天数+操作日期
			sRowStr = paramSet.getSHolidayCode() + "\t" + 0 + "\t" + YssFun.formatDate(dDate);
			holiday.parseRowStr(sRowStr);//解析参数
			dTransDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取当天的最近一个工作日
			if(YssFun.dateDiff(dDate, dTransDate) != 0){//如果当日不是工作日，直接跳出
				return;
			}

			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = (holidays.containsKey(YssOperCons.YSS_ETF_OVERTYPE_SHREPLACEINS)?(String)holidays.get(YssOperCons.YSS_ETF_OVERTYPE_SHREPLACEINS):paramSet.getSHolidayCode()) + "\t" + -paramSet.getSSHReplaceOver() + "\t" + YssFun.formatDate(dTransDate);
			holiday.parseRowStr(sRowStr);//解析参数
			dTransDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//根据ETF参数设置中的赎回现金替代结转参数，获取估值日期对应的赎回日期
			
			buff = new StringBuffer(1000);
        	buff.append("select a.fportcode,a.fdate,a.ftotalmoney,b.FTradeAmount,c.FNormScale,c.FCashAccCode ,c.FClearAccCode,ca.FCuryCode,ta.fnum from (");
        	buff.append(" select fportcode,fdate,sum(FTotalMoney) as FTotalMoney from ").append(pub.yssGetTableName("Tb_ETF_Stocklist"));
        	buff.append(" where fdate = ").append(dbl.sqlDate(dTransDate)).append(" and fportcode = ").append(dbl.sqlString(sPortCode));
        	buff.append(" and freplacemark = '2' and fsecuritycode <> ").append(dbl.sqlString(paramSet.getCapitalCode())).append(" group by fportcode,fdate ) a ");
        	buff.append(" join (select FPortCode ,sum(FTradeAmount) as FTradeAmount from ").append(pub.yssGetTableName("Tb_Etf_Jginterface"));
        	buff.append(" where fbargaindate = ").append(dbl.sqlDate(dTransDate));
        	buff.append(" and fopertype='2ndcode' and ftradetypecode = 'KB' group by FPortCode) b on b.fportcode = a.fportcode");
        	buff.append(" left join (select fnum,fportcode ,FTradeDate from ").append(pub.yssGetTableName("Tb_TA_Trade"));
        	buff.append(" where fselltype = '02' and FTradeDate = ").append(dbl.sqlDate(dTransDate)).append(") ta on ta.fportcode = a.fportcode");
        	buff.append(" left join (select FPortCode ,FNormScale,FCashAccCode,FClearAccCode from ").append(pub.yssGetTableName("Tb_ETF_Param"));
        	buff.append(" ) c on c.FPortCode = a.FPortCode ");
        	buff.append(" left join (select FCashAccCode,FCuryCode from ").append(pub.yssGetTableName("Tb_Para_Cashaccount"));
        	buff.append(" where FCheckState = 1 and FState =0) ca on ca.FCashAccCode = c.FCashAccCode");
        	
        	rs = dbl.queryByPreparedStatement(buff.toString());
        	while(rs.next()){
        		cashPecPay = new CashPecPayBean();
				cashPecPay = new CashPecPayBean();//冲减现金应收应付
        		cashPecPay.setMoney(YssD.mul(rs.getDouble("FTotalMoney"), YssD.div(rs.getDouble("FTradeAmount"), rs.getDouble("FNormScale"))));
				cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);//业务类型
				cashPecPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustRPSellDone);//业务子类型
				cashPecPay.setRelaOrderNum(rs.getString("FNum"));
				setCashRecPayProperty(rs,cashPecPay,paramSet);//设置冲减的现金应收应付数据

				cashPayRecAdmin.addList(cashPecPay);
				
                transfer = setTransfer(rs);         //设置资金调拨数据
                transferSet = setTransferSet(rs);   //设置资金调拨子数据
                subTransfer = new ArrayList();      //实例化放置资金调拨子数据的容器
                subTransfer.add(transferSet);       //将资金调拨子数据放入容器
                transfer.setSubTrans(subTransfer);  //将子数据放入资金调拨中
                cashTransAdmin.addList(transfer);
        	}
			cashPayRecAdmin.insert(dDate, dDate,
					YssOperCons.YSS_ZJDBLX_Fee, YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustRPSellDone, 
					"", sPortCode, "", "", "", 0);
			cashTransAdmin.insert("",dDate,dTransDate,
					YssOperCons.YSS_ZJDBLX_Capital,YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustTrans,
					"","","","","","ETFSHMUST","",1,"",sPortCode,0,"","","",true,"");	//根据编号类型、业务类型等删除原有调拨数据
        } catch (Exception e) {
            throw new YssException("产生赎回必须现金替代的资金调拨数据出现异常！", e);
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	
	private void transferCashMust(ETFParamSetBean paramSet) throws YssException {
		StringBuffer buff = new StringBuffer();
		ResultSet rs = null;
        CashPecPayBean cashPecPay = null;
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);
        CashTransAdmin cashTransAdmin = new CashTransAdmin();
        cashTransAdmin.setYssPub(pub);
        TransferBean transfer = null;
        TransferSetBean transferSet = null;
        ArrayList subTransfer = null;
		try{
			buff.append("select x.*,x.ftradedate as fdate,x.fsellamount as FTradeAmount,y.FClearAccCode,y.FNormScale,z.FMustMoney as FTotalMoney from ")
	      	    .append(pub.yssGetTableName("Tb_TA_Trade"))
	      	    .append(" x left join (select FPortCode,FClearAccCode,FNormScale from ")
	      	    .append(pub.yssGetTableName("Tb_ETF_Param"))//从ETF参数设置中获取备付金结转账户和最小申赎份额
	      	    .append(" ) y on y.FPortCode = x.FPortCode ")
	      	    .append(" left join (select FPortCode,FDate,sum(FTotalMoney) as FMustMoney from ")
	      	    .append(pub.yssGetTableName("Tb_ETF_StockList"))//从股票篮中获取单位篮子必须现金替代金额
	      	    .append(" where FCheckState = 1 and FReplaceMark in ('2','6')")//必须现金替代标识（深交所：2   上交所：6）
	      	    .append(" and fsecuritycode <> ").append(dbl.sqlString(paramSet.getCapitalCode()))//去掉虚拟成份股 arealw 20111126 增加
	      	    .append("group by FPortCode,FDate) z on z.FPortCode = x.fportcode and z.FDate = x.ftradedate")
	      	    .append(" where x.FCashReplaceDate = ").append(dbl.sqlDate(dDate))
	      	    .append(" and x.FPortCode = ").append(dbl.sqlString(sPortCode))
	      	    .append(" and x.FSellType in ('02') and x.FCheckState = 1");
			
        	rs = dbl.queryByPreparedStatement(buff.toString());
        	while(rs.next()){
        		cashPecPay = new CashPecPayBean();
				cashPecPay = new CashPecPayBean();//冲减现金应收应付
        		cashPecPay.setMoney(YssD.mul(rs.getDouble("FTotalMoney"), YssD.div(rs.getDouble("FTradeAmount"), rs.getDouble("FNormScale"))));
				cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);//业务类型
				cashPecPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustRPSellDone);//业务子类型
				cashPecPay.setRelaOrderNum(rs.getString("FNum"));
				setCashRecPayProperty(rs,cashPecPay,paramSet);//设置冲减的现金应收应付数据

				cashPayRecAdmin.addList(cashPecPay);
				
                transfer = setTransfer(rs);         //设置资金调拨数据
                transferSet = setTransferSet(rs);   //设置资金调拨子数据                
                transferSet.setSCashAccCode(rs.getString("FCashAccCode"));				
                
                subTransfer = new ArrayList();      //实例化放置资金调拨子数据的容器
                subTransfer.add(transferSet);       //将资金调拨子数据放入容器
                transfer.setSubTrans(subTransfer);  //将子数据放入资金调拨中
                cashTransAdmin.addList(transfer);
        	}
			cashPayRecAdmin.insert(dDate, dDate,
					YssOperCons.YSS_ZJDBLX_Fee, YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustRPSellDone, 
					"", sPortCode, "", "", "", 0);
			cashTransAdmin.insert("",dDate,null,
					YssOperCons.YSS_ZJDBLX_Capital,YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustTrans,
					"","","","","","ETFSHMUST","",1,"",sPortCode,0,"","","",true,"");	//根据编号类型、业务类型等删除原有调拨数据
        } catch (Exception e) {
            throw new YssException("产生赎回必须现金替代的资金调拨数据出现异常！", e);
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	
	private TransferBean setTransfer(ResultSet rs) throws YssException {
        TransferBean transfer = null;
        try {
            transfer = new TransferBean();
            transfer.setDtTransDate(rs.getDate("FDate")); //业务日期为该笔必须现金替代对应的赎回日期
            transfer.setDtTransferDate(dDate); //调拨日期为结转日期
            transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Capital);
            transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustTrans);//设置调拨子类型为04CR_2
            transfer.setFNumType("ETFSHMUST");
            transfer.setFRelaNum(rs.getString("FNum"));
            transfer.setDataSource(1);
            transfer.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨数据出现异常！", e);
        }
        return transfer; //返回资金调拨数据
	}
	
	private TransferSetBean setTransferSet(ResultSet rs) throws YssException {
        TransferSetBean transferSet = null;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        try {
            transferSet = new TransferSetBean();
            double dBaseRate = 1;
            double dPortRate = 1;

            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("FCuryCode"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE); //获取业务当天的基础汇率

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                      rs.getString("FPortCode"));
            dPortRate = rateOper.getDPortRate(); //获取业务当天的组合汇率

            transferSet.setSPortCode(rs.getString("FPortCode"));
            transferSet.setSAnalysisCode1("");
            transferSet.setSAnalysisCode2("");
            transferSet.setSAnalysisCode3("");
            transferSet.setSCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ?
            											rs.getString("FCashAccCode") : rs.getString("FClearAccCode")); //设置现金账户
            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.setDMoney(YssD.mul(rs.getDouble("FTotalMoney"), 
            									YssD.div(rs.getDouble("FTradeAmount"), rs.getDouble("FNormScale")))); 
            transferSet.setIInOut(-1); 
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
        return transferSet; //返回资金调拨子数据
	}
}
