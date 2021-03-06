package com.yss.main.operdeal.report.reptab;

import java.util.*;

import com.yss.base.*;
import com.yss.main.dayfinish.OffAcctBean;
import com.yss.main.operdeal.report.reptab.valrep.BondValRep;
import com.yss.util.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * <p>Title: </p>
 * 计算未完成交易明细表
 * <p>Description: </p>
 * 此类只产生相应数据，要获取报表的值需要在数据源中配置获取。
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TabUntradeDetail
      extends BaseAPOperValue {
   private Date dBeginDate = null; //查询开始时间
   private Date dEndDate = null; //查询结束时间
   private String sPortCode = ""; //组合代码
   private boolean isCreate = false;//是否生成报表 add by qiuxufeng 20101118 QDV4太平2010年09月16日03_A

   public TabUntradeDetail() {
   }

	private class UntradeBean {
		String TradeNum;
		Date BargainDate;
		Date SettleDate;
		String SecurityName;
		String BrokerName;
		String CatCode;
		String TradeTypeCode;
		String PortCode;
		String CuryCode;
		double Amount;
		double Price;
		double Money;
		double BaseCuryMoney;
		double BaseCuryRate;
		double PortCuryRate;
		double UnitCost;
		double Cost;
		double BaseCuryCost;
		double AccInt;
		double BaseCuryAccInt;
		double GainLoss;
		double BaseCuryGainLoss;
		double SyBaseCuryBal;
	}


   /**
    * 初始化页面传过来的数据，分别得到查询的起始时间和结束时间，组合的代码
    * @param bean Object
    * @throws YssException
    */
public void init(Object bean) throws YssException {
      String reqAry[] = null;
      String reqAry1[] = null;
      String sRowStr = (String) bean;
      if (sRowStr.trim().length() == 0) {
         return;
      }
      reqAry = sRowStr.split("\n");
      reqAry1 = reqAry[0].split("\r");
      this.dBeginDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      this.dEndDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[2].split("\r");
      this.sPortCode = reqAry1[1];
      if(reqAry.length > 3) {
	      reqAry1 = reqAry[3].split("\r");
	      if(reqAry1[2].equalsIgnoreCase("ComboBox1")) {
			if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0，则只查询已生成的报表数据
				this.isCreate = false;
			} else { // 生成报表
				this.isCreate = true;
			}
	      }
      }
   }

public Object invokeOperMothed() throws YssException {
      HashMap valueMap = null;
      valueMap = new HashMap();
      try {
         createUntradeTable();
     	 //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
         if(isCreate) {
      	  	OffAcctBean offAcct = new OffAcctBean();
     		offAcct.setYssPub(this.pub);
      	  	String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
      	  	String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.sPortCode);
      	  	if(!tmpInfo.trim().equalsIgnoreCase("")) {
      	  		return "<OFFACCT>" + tmpInfo;
      	  	}
      	  	//=================end=================
      	    deleteUntradeDetail();
            getUntradeDetail(valueMap);
            insertUntradeDetail(valueMap);
         }
      }
      catch (YssException ex) {
         throw new YssException(ex.getMessage());
      }
      return "";
   }

   /**
    * add by songjie 2010.12.08
    * 添加删除未完成交易明细数据的方法
    * 138 QDV4太平2010年09月16日02_A
    * @throws YssException
    */
   private void deleteUntradeDetail()throws YssException{
	   String sqlStr = "";
	   try {
		   sqlStr = "delete from " + pub.yssGetTableName("Tb_Rep_SummaryUntrade") + 
		   " where FPortCode = " + dbl.sqlString(this.sPortCode) + 
           " and FBargainDate between " + dbl.sqlDate(this.dBeginDate) + 
           " and " + dbl.sqlDate(this.dEndDate);
			dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("删除未完成交易明细数据出错！" + e.getMessage());
		}
   }

   /**
    * insertToTempSecRecUnrealised
    *
    * @param valueMap HashMap
    */
   private void insertUntradeDetail(HashMap valueMap) throws
         YssException {
      if (null == valueMap || valueMap.isEmpty()) {
         return;
      }
      UntradeBean untrade = null;
      Object object = null;
      PreparedStatement prst = null;
      //edit by songjie 2010.12.08 138 QDV4太平2010年09月16日02_A 
      String sqlStr = "insert into " + pub.yssGetTableName("Tb_Rep_SummaryUntrade")
      + "(FNum,FBargainDate,FSettleDate,FSecurityName,FBrokerName,FCatCode,FTradeTypeCode,FCuryCode,"
      + "FAmount,FPrice,FMoney,FBaseCuryMoney,FBaseCuryRate,FPortCuryRate,FUnitCost,FCost,FBaseCuryCost,"
      + "FAccInt,FBaseCuryAccInt,FGainLoss,FBaseCuryGainLoss,FSyBaseCuryBal,FPortCode) "
      + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      try {
         prst = dbl.openPreparedStatement(sqlStr);
         Iterator it = valueMap.keySet().iterator();
         while (it.hasNext()) {
        	 untrade = (UntradeBean) valueMap.get( it.next());
        	 prst.setString(1, untrade.TradeNum);
        	 prst.setDate(2, YssFun.toSqlDate(untrade.BargainDate));
        	 prst.setDate(3, YssFun.toSqlDate(untrade.SettleDate));
        	 prst.setString(4, untrade.SecurityName);
        	 prst.setString(5, untrade.BrokerName);
        	 prst.setString(6, untrade.CatCode);
        	 prst.setString(7, untrade.TradeTypeCode);
        	 prst.setString(8, untrade.CuryCode);
        	 prst.setDouble(9, untrade.Amount);
        	 prst.setDouble(10, untrade.Price);
        	 prst.setDouble(11, untrade.Money);
        	 prst.setDouble(12, untrade.BaseCuryMoney);
        	 prst.setDouble(13, untrade.BaseCuryRate);
        	 prst.setDouble(14, untrade.PortCuryRate);
        	 prst.setDouble(15, untrade.UnitCost);
        	 prst.setDouble(16, untrade.Cost);
        	 prst.setDouble(17, untrade.BaseCuryCost);
        	 prst.setDouble(18, untrade.AccInt);
        	 prst.setDouble(19, untrade.BaseCuryAccInt);
        	 prst.setDouble(20, untrade.GainLoss);
        	 prst.setDouble(21, untrade.BaseCuryGainLoss);
        	 prst.setDouble(22, untrade.SyBaseCuryBal);
          prst.setString(23, this.sPortCode);
            prst.executeUpdate();
         }
      }
      catch (YssException ex) {
         throw new YssException("insert error", ex);
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
	  //add by rujiangpeng 20100603打开多张报表系统需重新登录
      finally{
    	  dbl.closeStatementFinal(prst);
      }

   }

   /**
    * getUntradeBuy
    *
    * @param valueMap HashMap
    */
   private void getUntradeDetail(HashMap valueMap) throws
         YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }

      BondValRep valBond = new BondValRep();
      valBond.setYssPub(pub);
      HashMap hmBoughtInt = valBond.getBoughtInt(sPortCode, dBeginDate, dEndDate);

      Connection conn = dbl.loadConnection();
      boolean bTrans = false;
      try {
         conn.setAutoCommit(false);
      }
      catch (SQLException ex) {
      }
      bTrans = true;
      String sqlStr = "select FNum, FBargainDate, FFActSettleDate, b.FSecurityName || ',' || a.FSecurityCode as FSecurityName, FBrokerName, FCatCode, FTradeTypeCode, FTradeCury as FCuryCode, FTradeAmount as FAmount, "
    	  + " FVCost / FTradeAmount  * FFactor as FUnitCost, FVCost as FCost, d.FBaseCuryRate as FPortCuryRate, a.FBaseCuryRate, FVBaseCuryCost as FBaseCuryCost, FTradePrice as FPrice, FFactSettleMoney - FAccruedInterest as FMoney, "
    	  + " (FFactSettleMoney - FAccruedInterest) * d.FBaseCuryRate as FBaseCuryMoney, FAccruedInterest * d.FBaseCuryRate as FBaseAccruedInterest, FTotalCost - FAccruedInterest - FVCost as FGainLoss, "
    	  + " (FTotalCost - FAccruedInterest) * d.FBaseCuryRate - FVBaseCuryCost as FBaseGainLoss, FAccruedInterest as FAccruedInterest, FVCost * d.FBaseCuryRate - a.FVBaseCuryCost as FSyBaseCuryBal from "
    	  + pub.yssGetTableName("tb_data_subtrade") + " a join (select FSecurityCode, FSecurityName, FTradeCury, FCatCode, FFactor from " + pub.yssGetTableName("tb_para_security") + " where FCheckState = 1) b on a.FSecurityCode = b.FSecurityCode "
    	  + " left join (select FBrokerCode, FBrokerName from " + pub.yssGetTableName("tb_para_broker") + " where FCheckState = 1) c on a.FBrokerCode = c.FBrokerCode "
    	  + " left join (select rate1.FCuryCode, FBaseRate / FPortRate as FBaseCuryRate from " + pub.yssGetTableName("tb_data_valrate") + " rate1 join (select max(FValDate) as FValDate, FCuryCode from "
    	  + pub.yssGetTableName("tb_data_valrate") + " where FPortCode = " + dbl.sqlString(sPortCode) + " and FValDate <= " + dbl.sqlDate(dEndDate)
    	  + " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " + dbl.sqlString(sPortCode) + ") d on b.FTradeCury = d.FCuryCode "
    	  + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(sPortCode) + " and FBargainDate between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate)
    	  + " and FFActSettleDate > " + dbl.sqlDate(dEndDate) + " and FTradeAmount <> 0 and FCost <> 0  order by FBargainDate, FTradeCury, FSecurityName, FBrokerName, FNum ";
      ResultSet rs = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         UntradeBean untrade = null;

         while (rs.next()) {
        	untrade = new UntradeBean();
        	untrade.TradeNum = rs.getString("FNum");
        	untrade.BargainDate = rs.getDate("FBargainDate");
        	untrade.SettleDate = rs.getDate("FFActSettleDate");
        	untrade.SecurityName = rs.getString("FSecurityName");
        	untrade.BrokerName = rs.getString("FBrokerName");
        	untrade.CatCode = rs.getString("FCatCode");
        	untrade.TradeTypeCode=  rs.getString("FTradeTypeCode");
        	untrade.CuryCode = rs.getString("FCuryCode");
        	untrade.Amount = rs.getDouble("FAmount");
        	untrade.Price = rs.getDouble("FPrice");
        	untrade.Money = rs.getDouble("FMoney");
        	untrade.BaseCuryMoney = rs.getDouble("FBaseCuryMoney");
        	untrade.BaseCuryRate = rs.getDouble("FBaseCuryRate");
        	untrade.PortCuryRate = rs.getDouble("FPortCuryRate");
        	untrade.UnitCost = rs.getDouble("FUnitCost");
        	untrade.Cost = rs.getDouble("FCost");
        	untrade.BaseCuryCost = rs.getDouble("FBaseCuryCost");
        	untrade.AccInt = rs.getDouble("FAccruedInterest");
        	untrade.BaseCuryAccInt = rs.getDouble("FBaseAccruedInterest");
        	untrade.GainLoss = rs.getDouble("FGainLoss");
        	untrade.BaseCuryGainLoss = rs.getDouble("FBaseGainLoss");
        	untrade.SyBaseCuryBal = rs.getDouble("FSyBaseCuryBal");

            String key = untrade.TradeNum;
            valueMap.put(key, untrade);
         }
      }
      catch (YssException ex) {
         throw new YssException("获取未完成交易明细数据出错！", ex);
      }
      catch (SQLException ex) {
         throw new YssException(ex.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }


      try {
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (SQLException ex1) {
         throw new YssException(ex1.getMessage());
      }
      finally {
         dbl.endTransFinal(conn, bTrans);
      }
   }

	/**
	 * createUntradeTable
	 */
	private void createUntradeTable() throws YssException {
		String strSql = "";
		try {
			//edit by songjie 2010.12.08 138 QDV4太平2010年09月16日02_A 
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Rep_SummaryUntrade"))) {
//				dbl.executeSql("drop table "
//						+ pub.yssGetTableName("Tb_SummaryUntrade"));
				//delete by songjie 2010.12.08 138 QDV4太平2010年09月16日02_A
//            dbl.executeSql("delete from " + pub.yssGetTableName("Tb_SummaryUntrade") + " where FPortCode = " + dbl.sqlString(this.sPortCode)
//                 + " and FBargainDate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate)  );
				//delete by songjie 2010.12.08 138 QDV4太平2010年09月16日02_A
				return;//add by songjie 2010.12.08 138 QDV4太平2010年09月16日02_A
			}else {
				//edit by songjie 2010.12.08 138 QDV4太平2010年09月16日02_A 
            strSql = "create table " + pub.yssGetTableName("Tb_Rep_SummaryUntrade")
                  + " (FNum varchar2(20) not null, "
                  + " FBargainDate date not null,"
                  + " FSettleDate date not null,"
                  + " FSecurityName varchar2(200) not null,"
                  + " FBrokerName varchar2(200) not null,"
                  + " FCatCode varchar2(20) not null,"
                  + " FTradeTypeCode varchar2(20),"
                  + " FCuryCode varchar2(20) not null,"
                  + " FAmount number(18,4)," + " FPrice number(18,4),"
                  + " FMoney number(18,4)," + " FBaseCuryMoney number(18,4),"
                  + " FBaseCuryRate number(18,12),"
                  + " FPortCuryRate number(18,12),"
                  + " FUnitCost number(18,4)," + " FCost number(18,4),"
                  + " FBaseCuryCost number(18,4)," + " FAccInt number(18,4),"
                  + " FBaseCuryAccInt number(18,4),"
                  + " FGainLoss number(18,4),"
                  + " FBaseCuryGainLoss number(18,4),"
                  + " FSyBaseCuryBal number(18,4),"
                  + " FPortCode VARCHAR2(20))";
            dbl.executeSql(strSql);
         }
		} catch (Exception e) {
			throw new YssException("创建未完成交易明细表出错!");
		}
	}
}
