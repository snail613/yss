package com.yss.main.operdeal.report.reptab.valrep;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.yss.main.operdeal.report.reptab.valrep.pojo.ValRepBean;
import com.yss.util.YssException;

public class AccIntValRep  extends BaseValRep{
	public AccIntValRep(){
	}

	  private ArrayList alSec=null;

	public ArrayList getValRepData() throws YssException {
	      alSec =new ArrayList();
	      getAccIntValData();
	      return alSec;
	   }
		//合并邵宏伟修改报表代码 xuqiji 20100608
	   private void getAccIntValData() throws YssException{
	      String sqlStr="";
	      ResultSet rs =null;
	      try{
	    	  sqlStr = "select 'EQ##EQ98##' || subTrade.FAttrClsCode || '##' || security.FTradeCury || '##' || subTrade.FSecurityCode as FOrderCode, subTrade.FSecurityCode, security.FSecurityName, divide.FCuryCode, nvl(rate.FExRate, 1) as FBaseCuryRate, subTrade.FBargainDate, "
	    		  + "  subTrade.FFActSettleDate, subTrade.FAccruedInterest as FTotalCost, subTrade.FAccruedInterest as FMarketValue, round(subTrade.FAccruedInterest*nvl(rate.FExRate, 1) - FAccruedinterestB, 2) as FSyvBaseCuryBal, nvl(round(round(subTrade.FAccruedInterest * nvl(rate.FExRate, 1), 2)/FPortMarketValue, 4), 0) as FFundAllotProportion from ( "
	    		  + " select FPortCode, FSecurityCode, FBargainDate ,FFActSettleDate, FAttrClsCode, sum(FAccruedInterest) as FAccruedInterest, round(sum(FAccruedInterest*FBaseCuryRate/FPortCuryRate), 2) as FAccruedInterestB from " + pub.yssGetTableName("tb_data_subtrade")
	    		  + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(sPortCode) + " and FBargainDate <= " + dbl.sqlDate(dEndDate) + " and FTradeTypeCode = '06' and FFActSettleDate > " + dbl.sqlDate(dEndDate) + " group by FPortCode, FBargainDate, FSecurityCode, FAttrClsCode,FFActSettleDate) subTrade "
	    		  + " join (select distinct FSecurityCode, FCuryCode, FDividendDate, FDistributedate from " + pub.yssGetTableName("tb_data_dividend") + " where FCheckState = 1) divide on subTrade.FSecurityCode = divide.FSecurityCode and subTrade.FBargainDate = divide.FDividendDate "
	    		  + " left join (select FSecurityCode, FSecurityName, FTradeCury from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1) security on subTrade.FSecurityCode = security.FSecurityCode "
	    		  + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FExRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate")
	    		  + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") rate on divide.FCuryCode = rate.FCuryCode "
	    		  + " left join (select FPortCode, FDate, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") p on subTrade.FPortCode = " + dbl.sqlString(sPortCode)
	    		  + " union "
	    		  + " select 'EQ##EQ98##' || subTrade.FAttrClsCode || '##' || security.FTradeCury ||'##total' as FOrderCode, '港幣等值金額：' as FSecurityCode, '' as FSecurityName, security.FTradeCury as FCuryCode, nvl(rateSecurity.FExRate, 1) as FBaseCuryRate, to_date('9998-12-31', 'yyyy-MM-dd') as FDividendDate, "
	    		  + " to_date('9998-12-31', 'yyyy-MM-dd') as FDistributeDate, sum(subTrade.FAccruedInterestB) as FTotalCost, sum(round(subTrade.FAccruedInterest*nvl(rateDividend.FExRate, 1), 2)) as FMarketValue, sum(round(subTrade.FAccruedInterest*nvl(rateDividend.FExRate, 1) - FAccruedInterestB, 2)) as FSyvBaseCuryBal, nvl(sum(round(round(subTrade.FAccruedInterest * nvl(rateSecurity.FExRate, 1), 2)/FPortMarketValue, 4)), 0) as FFundAllotProportion from ( "
	    		  + " select FPortCode, FSecurityCode, FBargainDate, FAttrClsCode, sum(FAccruedInterest) as FAccruedInterest, round(sum(FAccruedInterest*FBaseCuryRate/FPortCuryRate), 2) as FAccruedInterestB from " + pub.yssGetTableName("tb_data_subtrade")
	    		  + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(sPortCode) + " and FBargainDate <= " + dbl.sqlDate(dEndDate) + " and FTradeTypeCode = '06' and FFActSettleDate > " + dbl.sqlDate(dEndDate) + " group by FPortCode, FBargainDate, FSecurityCode, FAttrClsCode) subTrade "
	    		  + " join (select distinct FSecurityCode, FCuryCode, FDividendDate, FDistributeDate from " + pub.yssGetTableName("tb_data_dividend") + " where FCheckState = 1 ) divide on subTrade.FSecurityCode = divide.FSecurityCode and subTrade.FBargainDate = divide.FDividendDate "
	    		  + " left join (select FSecurityCode, FSecurityName, FTradeCury from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1) security on subTrade.FSecurityCode = security.FSecurityCode "
	    		  + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FExRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate")
	    		  + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") rateDividend on divide.FCuryCode = rateDividend.FCuryCode "
	    		  + " left join (select rate1.FCuryCode, FBaseRate/FPortRate as FExRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate,FCuryCode from " + pub.yssGetTableName("tb_data_valrate")
	    		  + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") rateSecurity on security.FTradeCury = rateSecurity.FCuryCode "
	    		  + " left join (select FPortCode, FDate, FBaseCuryBal as FPortMarketValue from " + pub.yssGetTableName("tb_data_summary") + " where FCode = 'NetThree' and FDate = " + dbl.sqlDate(dEndDate) + " and FPortCode = " + dbl.sqlString(sPortCode) + ") p on subTrade.FPortCode = " + dbl.sqlString(sPortCode)
	    		  + " group by security.FTradeCury, rateSecurity.FExrate, subTrade.FAttrClsCode ";
	         rs = dbl.openResultSet(sqlStr);
	         while(rs.next()){
	            valBean =new ValRepBean();
	            valBean.setOrder(rs.getString("FOrderCode"));
	            valBean.setSecurityCode(rs.getString("FSecurityCode"));
	            valBean.setSecurityName(rs.getString("FSecurityName"));
	            valBean.setCuryCode(rs.getString("FCuryCode"));
	            valBean.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
	            valBean.setInsStartDate(rs.getDate("FBargainDate"));
	            valBean.setInsEndDate(rs.getDate("FFActSettleDate"));
	            valBean.setTotalCost(rs.getDouble("FTotalCost"));
	            valBean.setMvalue(rs.getDouble("FMarketValue"));
	            valBean.setSyvBaseCuryBal(rs.getDouble("FSyvBaseCuryBal"));
	            valBean.setFundAllotProportion(rs.getDouble("FFundAllotProportion"));
	            alSec.add(valBean);
	         }
	      }catch(Exception ex){
	         throw new YssException("取股票红利估值数据出错",ex);
	      }finally{
	         dbl.closeResultSetFinal(rs);
	      }
	   }
}
