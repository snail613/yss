package com.yss.main.operdeal.opermanage.etf;

import java.util.Date;
import java.util.HashMap;
import java.sql.*;

import com.yss.commeach.EachRateOper;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.manager.CashPayRecAdmin;
import com.yss.util.*;

/**
 * 可退替代款应收应付的产生
 * MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A 
 * @author panjunfang
 * 20091021 create
 * 
 */
public class CashInsteadManage extends BaseOperManage {
	
	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.sPortCode = portCode;
		this.dDate = dDate;
	}

	public void doOpertion() throws YssException {
		try{
			ETFParamSetAdmin etfParamAdmin = new ETFParamSetAdmin();
			etfParamAdmin.setYssPub(pub);
			ETFParamSetBean etfParamBean = new ETFParamSetBean();
			HashMap hm = new HashMap();
			hm = etfParamAdmin.getETFParamInfo(sPortCode);
			etfParamBean = (ETFParamSetBean)hm.get(sPortCode);
			Date dBuyDate = null;
			if(etfParamBean == null){
				throw new YssException("组合【" + sPortCode + "】对应的ETF参数设置不存在或未审核！");
			}
			if(etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_NO)){//无补票方式不产生可退替代款
				return;
			}
			if(etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_ONE)){//易方达ETF panjunfang add 20110812
				dBuyDate = getBuyDate();
			}else{
				dBuyDate = this.dDate;
			}
			//modify by fangjiang 2013.04.22 story 3873
			if(etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)){//arealw 2011-11-25 增加华夏恒指非T+1确认的可退替代款处理
				maxBackCashInsteadNotT("B",dBuyDate,etfParamBean);//申购可退替代款;
				maxBackCashInsteadNotT("S",dBuyDate,etfParamBean);//赎回可退替代款
			}else if(etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)){
				maxBackCashInsteadNotT("B",dBuyDate,etfParamBean);//申购可退替代款;
			}else{				
				maxBackCashInstead("B",dBuyDate,etfParamBean);//申购可退替代款
			    //maxBackCashInstead("S",dBuyDate,etfParamBean);//赎回可退替代款
			}
		}catch(Exception e){
			throw new YssException(e.getMessage());
		}
	}

	/**
	 * 可退替代款 = 当日申购赎回产生的可退替代款总和 - 当日（申购/赎回）补票的可退替代款的总和
	 * @throws YssException
	 */
	private void maxBackCashInstead(String strBS,Date dBuyDate,ETFParamSetBean etfParamBean) throws YssException{
        CashPecPayBean cashPecPayBean = null;
        CashPayRecAdmin cashPayRecAdmin = null;
		String strSql = "";
		ResultSet rs = null;
		double dCanBackCash = 0;
		double dBaseCanBackCash = 0;
		double dPortCanBackCash = 0;
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
			strSql = "select x.*,p.FCashAccCode,CA.FCuryCode,ta.fsellamount,ta.FCASHREPAMOUNT " + 
					(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(etfParamBean.getSupplyMode()) ? 
					",nav.fportmarketvalue" : "" ) + 
					" from (select FPortCode,sum(FHCReplaceCash) as FReplaceCash,FBs,FBuydate from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl") + 
					" where FPortCode = " + dbl.sqlString(sPortCode) + 
					(dBuyDate == null ? " and 1 = 2" : " and FBuyDate = " + dbl.sqlDate(dBuyDate)) + 
					" and FBS = " + dbl.sqlString(strBS) + 
					" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" + 
					" group by FPortCode,FBS,FBuyDate " + 
					" union all" + 
					" select FPortCode,sum(FHCPReplaceCash) as FReplaceCash,FDataDirection as FBs,FBuydate from (" + 
					" select tmtb21.FNum,tmtb21.FDataDirection,tmtb21.FHCPReplaceCash,tmtb22.FPortCode,tmtb22.FBuydate  from (" + 
					" select * from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + 
					" where FMakeUpDate = " + dbl.sqlDate(dDate) + 
					" ) tmtb21 join (" +
					" select FNum,FPortCode,FBuyDate from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl") + 
					" where FNum in (" + 
					" select FNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + 
					" where FMakeUpDate = " + dbl.sqlDate(dDate) +
					" ) and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)) tmtb22 on tmtb22.FNum = tmtb21.FNum " + 
					" ) where FPortCode = " + dbl.sqlString(sPortCode) + 
					(strBS.equals("B") ? " and FDataDirection = '1' " : " and FDataDirection = '-1' ") + 
					" group by FPortCode,FDataDirection,FBuydate " + 
					" ) x left join (" + 
					" select FCashAccCode,FPortCode from " + pub.yssGetTableName("Tb_ETF_Param") + 
					" where FPortCode = " + dbl.sqlString(sPortCode) + 
					" ) p on p.FPortCode = x.FPortCode " + 
                    " left join (" +
                    " select ca1.FCashAccCode,ca1.FStartDate,ca2.FCuryCode from " +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                 /*   " (select FCashAccCode, max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_CashAccount") +*/
                    " (select FCashAccCode,FStartDate from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                    /*" where FStartDate <= " + dbl.sqlDate(dDate) +*/
                    " where 1=1 " +
                   
                    " and FCheckState = 1 and FState =0 ) ca1 join (select FCashAccCode,FStartDate,FCuryCode from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where FCheckState = 1 and FState =0) ca2 on  ca2.FCashAccCode = ca1.FCashAccCode " +
                    ") CA on CA.FCashAccCode = p.FCashAccCode" + 
                    " left join (select FSELLAMOUNT,FTRADEDATE,FCASHREPAMOUNT from " + pub.yssGetTableName("TB_TA_TRADE") + 
                    " where fportcode = " + dbl.sqlString(sPortCode) + 
                    " and fselltype = " + dbl.sqlString((strBS.equals("B") ? "01" : "02")) +
                    " and fcheckstate = 1) ta on ta.ftradedate = x.fbuydate";
			if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(etfParamBean.getSupplyMode())){
				//易方达ETF 可退替代款 = 替代金额 - 篮子估值
				//关联净值表获取篮子市值
				strSql = strSql + " left join (select fnavdate,fportmarketvalue from " + pub.yssGetTableName("Tb_ETF_Navdata") + 
						" where fportcode = " + dbl.sqlString(sPortCode) + 
						" and fretypecode = 'Total' and fkeycode = 'StockListVal' " + 
						" ) nav on nav.fnavdate = x.fbuydate ";
			}
			 //end 
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next()){
				strCashAccCode = rs.getString("FCashAccCode");
				strCuryCode = rs.getString("FCuryCode");
				if(etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_ONE)){//易方达ETF
					double dbasketCount = YssD.div(rs.getDouble("fsellamount"), etfParamBean.getNormScale());
					//可退替代款 = 替代金额 - 单位篮子估值 × 篮子数
					dCanBackCash = YssD.sub(rs.getDouble("FCASHREPAMOUNT"),
												YssD.mul(rs.getDouble("fportmarketvalue"), dbasketCount));
				}else{
					dCanBackCash = YssD.round(rs.getDouble("FReplaceCash"),2);
				}				
				BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
						strCuryCode, sPortCode, YssOperCons.YSS_RATE_BASE);// 获取当日的基础汇率
				dBaseCanBackCash = this.getSettingOper().calBaseMoney(
											dCanBackCash, BaseCuryRate);// 计算基础货币金额
				rateOper.setYssPub(pub);
				rateOper.getInnerPortRate(dDate, strCuryCode, sPortCode);
				PortCuryRate = rateOper.getDPortRate(); // 获取当日的组合汇率
				dPortCanBackCash = this.getSettingOper().calPortMoney(
											dCanBackCash, BaseCuryRate, PortCuryRate, strCuryCode,
											dDate, sPortCode); // 计算组合货币金额
				cashPecPayBean = new CashPecPayBean();
				cashPecPayBean.setTradeDate(dDate);
				cashPecPayBean.setPortCode(sPortCode);
				cashPecPayBean.setBaseCuryRate(BaseCuryRate);
				cashPecPayBean.setPortCuryRate(PortCuryRate);
				cashPecPayBean.setMoney(dCanBackCash);
				cashPecPayBean.setBaseCuryMoney(dBaseCanBackCash);
				cashPecPayBean.setPortCuryMoney(dPortCanBackCash);
				cashPecPayBean.setCashAccCode(strCashAccCode);
				if(rs.getString("FBs").equals("B")||rs.getString("FBs").equals("S")){
					cashPecPayBean.setInOutType(1);
				}else{
					cashPecPayBean.setInOutType(-1);
				}
				cashPecPayBean.setCuryCode(strCuryCode);
				cashPecPayBean.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); // 设置业务类型为07
				if (rs.getString("FBs").equals("B")||rs.getString("FBs").equals("1")) {
					cashPecPayBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_SGCanBackCash); // 申购：设置业务子类型为07TA_CBCB
				} else {
					cashPecPayBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_SHCanBackCash); // 赎回：设置业务子类型为07TA_CBCS
				}
				cashPecPayBean.checkStateId = 1; // 设置审核状态为已审核

				cashPayRecAdmin.addList(cashPecPayBean);
			}
			cashPayRecAdmin.insert(dDate, dDate,
									YssOperCons.YSS_ZJDBLX_Pay,
									strBS.equals("B") ? YssOperCons.YSS_ZJDBZLX_ETF_SGCanBackCash : YssOperCons.YSS_ZJDBZLX_ETF_SHCanBackCash,
									"", sPortCode, "", "", "", 0);
		}catch(Exception e){
			throw new YssException("生成应付可退替代款出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 可退替代款 非T日确认的ETF处理过程
	 * 非T日确认，则在业务日期处理时不包含当日申购赎回引起的补票、强制处理的可退替代款金额
	 * arealw 2011-11-25 新增非T日确认的情况
	 * 此过程包含上面的过程，如果不同申购日的申请编号重复，则会出现问题。
	 * @throws YssException 
	 */
	private void maxBackCashInsteadNotT(String strBS,Date dBuyDate,ETFParamSetBean etfParamBean) throws YssException{
        CashPecPayBean cashPecPayBean = null;
        CashPayRecAdmin cashPayRecAdmin = null;
		String strSql = "";
		ResultSet rs = null;
		double dBaseCanBackCash = 0;
		double dPortCanBackCash = 0;
		String strCashAccCode = "";
		String strCuryCode = "";
        EachRateOper rateOper = new EachRateOper();     //新建获取利率的通用类
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        double dbasketCount = 1;
		try{
			if (cashPayRecAdmin == null) {
				cashPayRecAdmin = new CashPayRecAdmin();
				cashPayRecAdmin.setYssPub(pub);
			}
			Date tradeDate = getDateNotT(strBS);
			
			strSql = "select x.*,p.FCashAccCode,CA.FCuryCode,ta.fsellamount from (" + 
					" select FPortCode,sum(FHCReplaceCash) as FReplaceCash,FBs,FBuydate from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl") + 
					" where FPortCode = " + dbl.sqlString(sPortCode) + 
					(tradeDate == null ? " and 1 = 2" : " and FBuyDate = " + dbl.sqlDate(tradeDate)) + 
					" and FBS = " + dbl.sqlString(strBS) + 
					" and FSecurityCode <> ' ' and (FStockHolderCode <> ' ' or FStockHolderCode is null)" + 
					" group by FPortCode,FBS,FBuyDate " + 
					" union all" + 
					" select FPortCode,sum(FHCPReplaceCash) as FReplaceCash,FDataDirection as FBs,FBuydate from (" + 
					" select tmtb21.FNum,tmtb21.FDataDirection,tmtb21.FHCPReplaceCash,tmtb22.FPortCode,tmtb22.FBuydate  from (" + 
					" select * from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + 
					" where FMakeUpDate = " + dbl.sqlDate(dDate) + 
					" ) tmtb21 join (" +
					" select FNum,FPortCode,FBuyDate from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl") + 
					" where FNum in (" + 
					" select FNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + 
					" where FMakeUpDate = " + dbl.sqlDate(dDate) +
					" ) and FSecurityCode <> ' ' and FBuyDate <> " + dbl.sqlDate(dDate) + " and (FStockHolderCode <> ' ' or FStockHolderCode is null)) tmtb22 on tmtb22.FNum = tmtb21.FNum " + 
					" ) where FPortCode = " + dbl.sqlString(sPortCode) + 
					(strBS.equals("B") ? " and FDataDirection = '1' " : " and FDataDirection = '-1' ") + 
					" group by FPortCode,FDataDirection,FBuydate " + 
					" union all" + 
					" select FPortCode,sum(FHCPReplaceCash) as FReplaceCash,FDataDirection as FBs,FBuydate from (" + 
					" select tmtb21.FNum,tmtb21.FDataDirection,tmtb21.FHCPReplaceCash,tmtb22.FPortCode,tmtb22.FBuydate  from (" + 
					" select * from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + 
					" where FMakeUpDate < " + dbl.sqlDate(dDate) + 
					" ) tmtb21 join (" +
					" select FNum,FPortCode,FBuyDate from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl") + 
					" where FNum in (" + 
					" select FNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + 
					" where FMakeUpDate < " + dbl.sqlDate(dDate) +
					" ) and FSecurityCode <> ' ' " + 
					(tradeDate == null ? " and 1 = 2" : " and FBuyDate = " + dbl.sqlDate(tradeDate)) + 
					" and (FStockHolderCode <> ' ' or FStockHolderCode is null)) tmtb22 on tmtb22.FNum = tmtb21.FNum " + 
					" ) where FPortCode = " + dbl.sqlString(sPortCode) + 
					(strBS.equals("B") ? " and FDataDirection = '1' " : " and FDataDirection = '-1' ") + 
					" group by FPortCode,FDataDirection,FBuydate " + 
					" ) x left join (" + 
					" select FCashAccCode,FPortCode from " + pub.yssGetTableName("Tb_ETF_Param") + 
					" where FPortCode = " + dbl.sqlString(sPortCode) + 
					" ) p on p.FPortCode = x.FPortCode " + 
                    " left join (" +
                    " select ca1.FCashAccCode,ca1.FStartDate,ca2.FCuryCode from " +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                 /*   " (select FCashAccCode, max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_CashAccount") +*/
                    " (select FCashAccCode,FStartDate from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                    /*" where FStartDate <= " + dbl.sqlDate(dDate) +*/
                    " where 1=1 " +
                   
                    " and FCheckState = 1 and FState =0 ) ca1 join (select FCashAccCode,FStartDate,FCuryCode from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where FCheckState = 1 and FState =0) ca2 on  ca2.FCashAccCode = ca1.FCashAccCode " +
                    ") CA on CA.FCashAccCode = p.FCashAccCode" + 
                    " left join (select FSELLAMOUNT,FTRADEDATE from " + pub.yssGetTableName("TB_TA_TRADE") + 
                    " where fportcode = " + dbl.sqlString(sPortCode) + 
                    " and fselltype = " + dbl.sqlString((strBS.equals("B") ? "01" : "02")) +
                    " and fcheckstate = 1) ta on ta.ftradedate = x.fbuydate";
			 //end 
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next()){
				strCashAccCode = rs.getString("FCashAccCode");
				strCuryCode = rs.getString("FCuryCode");
				if(etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_ONE)){//易方达ETF panjunfang add 20110812
					dbasketCount = YssD.div(rs.getDouble("fsellamount"), etfParamBean.getNormScale());
				}
				BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
						strCuryCode, sPortCode, YssOperCons.YSS_RATE_BASE);// 获取当日的基础汇率
				dBaseCanBackCash = this.getSettingOper().calBaseMoney(YssD.mul(
						rs.getDouble("FReplaceCash"),dbasketCount), BaseCuryRate);// 计算基础货币金额

				rateOper.setYssPub(pub);
				rateOper.getInnerPortRate(dDate, strCuryCode, sPortCode);
				PortCuryRate = rateOper.getDPortRate(); // 获取当日的组合汇率
				dPortCanBackCash = this.getSettingOper().calPortMoney(
						YssD.mul(rs.getDouble("FReplaceCash"),dbasketCount), BaseCuryRate, PortCuryRate, strCuryCode,
						dDate, sPortCode); // 计算组合货币金额

				cashPecPayBean = new CashPecPayBean();
				cashPecPayBean.setTradeDate(dDate);
				cashPecPayBean.setPortCode(sPortCode);
				cashPecPayBean.setBaseCuryRate(BaseCuryRate);
				cashPecPayBean.setPortCuryRate(PortCuryRate);
				cashPecPayBean.setMoney(YssD.mul(rs.getDouble("FReplaceCash"),dbasketCount));
				cashPecPayBean.setBaseCuryMoney(dBaseCanBackCash);
				cashPecPayBean.setPortCuryMoney(dPortCanBackCash);
				cashPecPayBean.setCashAccCode(strCashAccCode);
				if(rs.getString("FBs").equals("B")||rs.getString("FBs").equals("S")){
					cashPecPayBean.setInOutType(1);
				}else{
					cashPecPayBean.setInOutType(-1);
				}
				cashPecPayBean.setCuryCode(strCuryCode);
				cashPecPayBean.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); // 设置业务类型为07
				if (rs.getString("FBs").equals("B")||rs.getString("FBs").equals("1")) {
					cashPecPayBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_SGCanBackCash); // 申购：设置业务子类型为07TA_CBCB
				} else {
					cashPecPayBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_SHCanBackCash); // 赎回：设置业务子类型为07TA_CBCS
				}
				cashPecPayBean.checkStateId = 1; // 设置审核状态为已审核

				cashPayRecAdmin.addList(cashPecPayBean);
			}
			cashPayRecAdmin.insert(dDate, dDate,
									YssOperCons.YSS_ZJDBLX_Pay,
									strBS.equals("B") ? YssOperCons.YSS_ZJDBZLX_ETF_SGCanBackCash : YssOperCons.YSS_ZJDBZLX_ETF_SHCanBackCash,
									"", sPortCode, "", "", "", 0);
		}catch(Exception e){
			throw new YssException("生成应付可退替代款出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 
	 * 根据确认日期得出申赎日期
	 * @param date
	 * @return
	 * @throws YssException
	 * @author panjunfang ,2011-8-12
	 * @modified
	 */
	private Date getBuyDate() throws YssException {
		Date dBuyDate = null;
		StringBuffer buffer = new StringBuffer(100);
		ResultSet rs = null;
		try{
			buffer.append("SELECT  FTRADEDATE,FSELLAMOUNT,FSELLTYPE FROM ").append(pub.yssGetTableName("TB_TA_TRADE"))
				.append(" WHERE FCONFIMDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" AND FPORTCODE = ").append(dbl.sqlString(this.sPortCode))
				.append(" AND FSELLTYPE IN ('01','02')")
				.append(" AND FCHECKSTATE = 1");
			rs = dbl.queryByPreparedStatement(buffer.toString());
			if(rs.next()){
				dBuyDate = rs.getDate("FTRADEDATE");
			}
		}catch (Exception e) {
			throw new YssException("获取申赎日期出错 ！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dBuyDate;
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
				.append(" WHERE FCONFIMDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" AND FPORTCODE = ").append(dbl.sqlString(this.sPortCode))
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
}
