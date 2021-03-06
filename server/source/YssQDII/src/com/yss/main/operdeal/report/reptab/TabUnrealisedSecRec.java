package com.yss.main.operdeal.report.reptab;

import java.util.*;

import com.yss.base.*;
import com.yss.main.dayfinish.OffAcctBean;
import com.yss.main.operdeal.report.reptab.valrep.BondValRep;
import com.yss.util.*;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * <p>Title: </p>
 * 计算未兑现证券增值/贬值及债券收益分布
 * <p>Description: </p>
 * 此类只产生相应数据，要获取报表的值需要在数据源中配置获取。
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TabUnrealisedSecRec
      extends BaseAPOperValue {
   private Date dBeginDate = null; //查询开始时间
   private Date dEndDate = null; //查询结束时间
   private String sPortCode = ""; //组合代码
   private boolean isCreate;//是否生成报表 add by qiuxufeng 20101118 QDV4太平2010年09月16日03_A

   public TabUnrealisedSecRec() {
   }

   private class UnrealisedSecRecBean {
      String Code; //代码 or 合计数
      String Name; //项目名
      String CatCode;
      String SubCatCode;
      String PortCode;
      String CuryCode;
      double Amount;
      double TotalCost;
      double TotalCostB;
      double Bal;
      double BalB;
      double MaketPrice;
      double MaketPriceB;
      double MaketValue;
      double MaketValueB;
      double UnrealisedMoney;
      double UnrealisedMoneyB;
      double BondInterest;
      double BondInterestB;
      java.sql.Date FDate;
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
      this.dBeginDate = YssFun.toDate(reqAry1[1].substring(0, 7) + "-01");
      this.dEndDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      this.sPortCode = reqAry1[1];
      reqAry1 = reqAry[2].split("\r");
      if(reqAry1[2].equalsIgnoreCase("ComboBox1")) {
		if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0，则只查询已生成的报表数据
			this.isCreate = false;
		} else { // 生成报表
			this.isCreate = true;
		}
      }
   }

   public Object invokeOperMothed() throws YssException {
      HashMap valueMap = null;
      valueMap = new HashMap();
      try {
         createTempUnrealisedSecRec();
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
      	  	deleteSecRecUnrealised();//add by qiuxufeng 20101208 生成前删除该条件的原有数据
            getUnrealisedSecRecValue(valueMap);
            insertToTempSecRecUnrealised(valueMap);
         }
      }
      catch (YssException ex) {
         throw new YssException(ex.getMessage());
      }
      return "";
   }
   
   private void deleteSecRecUnrealised() throws YssException {
	   String sqlStr = "";
	   try {
		   sqlStr = "delete from tb_Data_UnrealisedSecRec_" + pub.getUserCode() +
			   " where FPortCode = " + dbl.sqlString(this.sPortCode) + 
	           " and FDate = " + dbl.sqlDate(this.dEndDate);
		   dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("删除证券增值贬值以及债券收益数据出错！" + e.getMessage());
		}
   }

   /**
    * insertToTempSecRecUnrealised
    *
    * @param valueMap HashMap
    */
   private void insertToTempSecRecUnrealised(HashMap valueMap) throws
         YssException {
      if (null == valueMap || valueMap.isEmpty()) {
         return;
      }
      UnrealisedSecRecBean unrealisedSecRec = null;
      Object object = null;
      PreparedStatement prst = null;
      String sqlStr = "insert into tb_Data_UnrealisedSecRec_" + pub.getUserCode() +
            " (FCode,FName,FCatCode,FSubCatCode,FPortCode,FCuryCode,FAmount,FTotalCost,FTotalCostB,FBal,FBalB," +
            "FMaketPrice,FMaketPriceB,FMaketValue,FMaketValueB,FUnrealisedMoney,FUnrealisedMoneyB," +
            "FBondInterest,FBondInterestB,FDate) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      try {
         prst = dbl.openPreparedStatement(sqlStr);
         Iterator it = valueMap.keySet().iterator();
         while (it.hasNext()) {
            unrealisedSecRec = (UnrealisedSecRecBean) valueMap.get(it.next());
            prst.setString(1, unrealisedSecRec.Code);
            prst.setString(2, unrealisedSecRec.Name);
            prst.setString(3, unrealisedSecRec.CatCode);
            prst.setString(4, unrealisedSecRec.SubCatCode);
            prst.setString(5, unrealisedSecRec.PortCode);
            prst.setString(6, unrealisedSecRec.CuryCode);
            prst.setDouble(7, unrealisedSecRec.Amount);
            prst.setDouble(8, YssFun.roundIt(unrealisedSecRec.TotalCost, 4));
            prst.setDouble(9, YssFun.roundIt(unrealisedSecRec.TotalCostB, 4));
            prst.setDouble(10, YssFun.roundIt(unrealisedSecRec.Bal, 4));
            prst.setDouble(11, YssFun.roundIt(unrealisedSecRec.BalB, 4));
            prst.setDouble(12, YssFun.roundIt(unrealisedSecRec.MaketPrice, 4));
            prst.setDouble(13, YssFun.roundIt(unrealisedSecRec.MaketPriceB, 4));
            prst.setDouble(14, YssFun.roundIt(unrealisedSecRec.MaketValue, 4));
            prst.setDouble(15, YssFun.roundIt(unrealisedSecRec.MaketValueB, 4));
            prst.setDouble(16,
                           YssFun.roundIt(unrealisedSecRec.UnrealisedMoney, 4));
            prst.setDouble(17,
                           YssFun.roundIt(unrealisedSecRec.UnrealisedMoneyB, 4));
            prst.setDouble(18, YssFun.roundIt(unrealisedSecRec.BondInterest, 4));
            prst.setDouble(19, YssFun.roundIt(unrealisedSecRec.BondInterestB, 4));
            prst.setDate(20, unrealisedSecRec.FDate);//add by qiuxufeng QDV4太平2010年09月16日02_A

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
    * getUnrealisedSecRecValue
    *
    * @param valueMap HashMap
    */
   private void getUnrealisedSecRecValue(HashMap valueMap) throws
         YssException {
      if (null == valueMap) {
         throw new YssException("未实例化Map！");
      }

      BondValRep valBond = new BondValRep();
      valBond.setYssPub(pub);
      HashMap hmBoughtInt = valBond.getBoughtInt(sPortCode, dBeginDate,
                                                 dEndDate);

      Connection conn = dbl.loadConnection();
      boolean bTrans = false;
      try {
         conn.setAutoCommit(false);
      }
      catch (SQLException ex) {
      }
      bTrans = true;
      String sqlStr = "select * from ("
            + " select a.FSecurityCode, c.FSecurityName, d.FCatName, case when c.FSubCatCode in ('FI05','FI06','FI07') then c.FSubCatCode else c.FCatCode end as FSubCatCode, a.FPortCode, a.FCuryCode, FBaseCuryRate, a.FStorageAmount, case when FStorageAmount <> 0 then round(FStorageCost / FStorageAmount * FFactor, 2) else 0 end as FAvgPrice, case when FStorageAmount <> 0 then round(FPortCuryCost / FStorageAmount * FFactor, 2) else 0 end as FAvgPriceB, FStorageCost, FPortCuryCost, "
            //+ " FPrice as MarketPrice, FPrice * FBaseCuryRate as MarketPriceB, round(FPrice * FStorageAmount, 2) as MarketValue, round(round(FPrice * FStorageAmount, 2) * FBaseCuryRate, 2) as MarketValueB, round(FPrice * FStorageAmount, 2) - FStorageCost as FGainAndLoss, round((round(FPrice * FStorageAmount, 2) - FStorageCost) * FBaseCuryRate, 2) as FGainAndLossB, FBal as AccIngterest from ( "
            //+ " select FPortCode, FCuryCode, FSecurityCode, sum(FStorageAmount) as FStorageAmount, sum(FVStorageCost) as FStorageCost, sum(FVPortcuryCost) as FPortCuryCost from " +
            //edit by qiuxufeng 增加日期字段 QDV4太平2010年09月16日02_A
            + " FPrice as MarketPrice, FPrice * FBaseCuryRate as MarketPriceB, round(FPrice * FStorageAmount, 2) as MarketValue, round(round(FPrice * FStorageAmount, 2) * FBaseCuryRate, 2) as MarketValueB, round(FPrice * FStorageAmount, 2) - FStorageCost as FGainAndLoss, round((round(FPrice * FStorageAmount, 2) - FStorageCost) * FBaseCuryRate, 2) as FGainAndLossB, FBal as AccIngterest, FStorageDate as fdate from ( "
            + " select FStorageDate, FPortCode, FCuryCode, FSecurityCode, sum(FStorageAmount) as FStorageAmount, sum(FVStorageCost) as FStorageCost, sum(FVPortcuryCost) as FPortCuryCost from " +
            pub.yssGetTableName("tb_stock_security") + " where FStorageDate = " +
            dbl.sqlDate(dEndDate) + " and FPortCode = " +
            dbl.sqlString(sPortCode) +
            //" and FStorageAmount <> 0 group by FPortCode, FCuryCode, FSecurityCode) a "
            //edit by qiuxufeng 增加日期字段 QDV4太平2010年09月16日02_A
            " and FStorageAmount <> 0 group by FPortCode, FCuryCode, FSecurityCode, FStorageDate) a "
            + " left join (select FSecurityCode, sum(FBal) as FBal from " +
            pub.yssGetTableName("tb_stock_secrecpay") +
            " where FStorageDate = " + dbl.sqlDate(dEndDate) +
            " and FSubTsfTypeCode = '06FI' and FPortCode = " +
            dbl.sqlString(sPortCode) +
            " group by FSecurityCode) b on a.FSecurityCode = b.FSecurityCode "
            + " left join (select FSecurityCode, FSecurityName, FCatCode, FSubCatCode, FFactor from " +
            pub.yssGetTableName("tb_para_security") +
            " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode "
            + " left join (select FCatCode, FCatName from tb_base_category where FCheckState = 1) d on c.FCatCode = d.FCatCode "
            + " left join (select price1.FSecurityCode, price1.FPrice*factor.FFactor as FPrice from " +
            pub.yssGetTableName("tb_data_valmktprice") +
            " price1 join (select FSecurityCode, max(FValDate) FValDate from " +
            pub.yssGetTableName("tb_data_valmktprice") + " where FPortCode = " +
            dbl.sqlString(sPortCode) + " and FValDate <= " +
            dbl.sqlDate(dEndDate) + " group by FSecurityCode) price2 "
            + " on price1.FValDate = price2.FValDate and price1.FSecurityCode = price2.FSecurityCode join (select FSecurityCode,FFactor from " +
            pub.yssGetTableName("tb_para_security") + ") factor on price1.fsecuritycode = factor.fsecuritycode where price1.FPortCode = " +
            dbl.sqlString(sPortCode) +
            ") e on a.FSecurityCode = e.FSecurityCode "
            + " left join (select rate1.FCuryCode, FBaseRate / FPortRate as FBaseCuryRate from " +
            pub.yssGetTableName("tb_data_valrate") +
            " rate1 join (select max(FValDate) as FValDate, FCuryCode from " +
            pub.yssGetTableName("tb_data_valrate") + " where FPortCode = " +
            dbl.sqlString(sPortCode) + " and FValDate <= " +
            dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 "
            + " on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " +
            dbl.sqlString(sPortCode) + ") f on a.FCuryCode = f.FCuryCode "
            + " union "
            + " select '合计数：' as FSecurityCode, '' as FSecurityName, FCatName, FSubCatCode, FPortCode, FCuryCode, FBaseCuryRate, 0 as FStorageAmount, 0 as FAvgPrice, 0 as FAvgPriceB, sum(FStorageCost) as FStorageCost, sum(FPortCuryCost) as FPortCuryCost, "
            //+ " 0 as MarketPrice, 0 as MarketPriceB, sum(MarketValue) as MarketValue, sum(MarketValueB) as MarketValueB, sum(FGainAndLoss) as FGainAndLoss, sum(FGainAndLossB) as FGainAndLossB, sum(AccIngterest) as AccIngterest from ("
            //edit by qiuxufeng 增加日期字段 QDV4太平2010年09月16日02_A
            + " 0 as MarketPrice, 0 as MarketPriceB, sum(MarketValue) as MarketValue, sum(MarketValueB) as MarketValueB, sum(FGainAndLoss) as FGainAndLoss, sum(FGainAndLossB) as FGainAndLossB, sum(AccIngterest) as AccIngterest, FStorageDate as fdate from ("
            + " select a.FSecurityCode, c.FSecurityName, d.FCatName, case when c.FSubCatCode in ('FI05','FI06','FI07') then c.FSubCatCode else c.FCatCode end as FSubCatCode, a.FPortCode, a.FCuryCode, FBaseCuryRate, a.FStorageAmount, case when FStorageAmount <> 0 then round(FStorageCost / FStorageAmount * FFactor, 2) else 0 end as FAvgPrice, case when FStorageAmount <> 0 then round(FPortCuryCost / FStorageAmount * FFactor, 2) else 0 end as FAvgPriceB, FStorageCost, FPortCuryCost, "
            //+ " FPrice as MarketPrice, FPrice * FBaseCuryRate as MarketPriceB, round(FPrice * FStorageAmount, 2) as MarketValue, round(round(FPrice * FStorageAmount, 2) * FBaseCuryRate, 2) as MarketValueB, round(FPrice * FStorageAmount, 2) - FStorageCost as FGainAndLoss, round((round(FPrice * FStorageAmount, 2) - FStorageCost) * FBaseCuryRate, 2) as FGainAndLossB, FBal as AccIngterest from ( "
            //+ " select FPortCode, FCuryCode, FSecurityCode, sum(FStorageAmount) as FStorageAmount, sum(FVStorageCost) as FStorageCost, sum(FVPortcuryCost) as FPortCuryCost from " +
            + " FPrice as MarketPrice, FPrice * FBaseCuryRate as MarketPriceB, round(FPrice * FStorageAmount, 2) as MarketValue, round(round(FPrice * FStorageAmount, 2) * FBaseCuryRate, 2) as MarketValueB, round(FPrice * FStorageAmount, 2) - FStorageCost as FGainAndLoss, round((round(FPrice * FStorageAmount, 2) - FStorageCost) * FBaseCuryRate, 2) as FGainAndLossB, FBal as AccIngterest, FStorageDate from ( "
            + " select FPortCode, FCuryCode, FSecurityCode, sum(FStorageAmount) as FStorageAmount, sum(FVStorageCost) as FStorageCost, sum(FVPortcuryCost) as FPortCuryCost, FStorageDate from " +
            pub.yssGetTableName("tb_stock_security") + " where FStorageDate = " +
            dbl.sqlDate(dEndDate) + " and FPortCode = " +
            dbl.sqlString(sPortCode) +
            //" and FStorageAmount <> 0 group by FPortCode, FCuryCode, FSecurityCode) a "
            " and FStorageAmount <> 0 group by FPortCode, FCuryCode, FSecurityCode, FStorageDate) a "
            + " left join (select FSecurityCode, sum(FBal) as FBal from " +
            pub.yssGetTableName("tb_stock_secrecpay") +
            " where FStorageDate = " + dbl.sqlDate(dEndDate) +
            " and FSubTsfTypeCode = '06FI' and FPortCode = " +
            dbl.sqlString(sPortCode) +
            " group by FSecurityCode) b on a.FSecurityCode = b.FSecurityCode "
            + " left join (select FSecurityCode, FSecurityName, FCatCode, FSubCatCode, FFactor from " +
            pub.yssGetTableName("tb_para_security") +
            " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode "
            + " left join (select FCatCode, FCatName from tb_base_category where FCheckState = 1) d on c.FCatCode = d.FCatCode "
            + " left join (select price1.FSecurityCode, price1.FPrice from " +
            pub.yssGetTableName("tb_data_valmktprice") +
            " price1 join (select FSecurityCode, max(FValDate) FValDate from " +
            pub.yssGetTableName("tb_data_valmktprice") + " where FPortCode = " +
            dbl.sqlString(sPortCode) + " and FValDate <= " +
            dbl.sqlDate(dEndDate) + " group by FSecurityCode) price2 "
            + " on price1.FValDate = price2.FValDate and price1.FSecurityCode = price2.FSecurityCode where price1.FPortCode = " +
            dbl.sqlString(sPortCode) +
            ") e on a.FSecurityCode = e.FSecurityCode "
            + " left join (select rate1.FCuryCode, FBaseRate / FPortRate as FBaseCuryRate from " +
            pub.yssGetTableName("tb_data_valrate") +
            " rate1 join (select max(FValDate) as FValDate, FCuryCode from " +
            pub.yssGetTableName("tb_data_valrate") + " where FPortCode = " +
            dbl.sqlString(sPortCode) + " and FValDate <= " +
            dbl.sqlDate(dEndDate) + " group by FCuryCode) rate2 "
            + " on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = " +
            dbl.sqlString(sPortCode) + ") f on a.FCuryCode = f.FCuryCode "
            +
            //" ) group by FPortCode, FCuryCode, FBaseCuryRate, FCatName, FSubCatCode "
            " ) group by FPortCode, FCuryCode, FBaseCuryRate, FCatName, FSubCatCode, FStorageDate "
            + " ) order by FSubCatCode, FCuryCode, FSecurityCode";
      ResultSet rs = null;
      try {
         rs = dbl.openResultSet(sqlStr);
         UnrealisedSecRecBean unrealisedSecRec = null;

         //合计买入利息
         double PortTotalBoughtInt = 0;
         String LastCuryCode = ""; //上条记录的币种
         while (rs.next()) {
            unrealisedSecRec = new UnrealisedSecRecBean();
            unrealisedSecRec.Code = rs.getString("FSecurityCode");
            unrealisedSecRec.Name = rs.getString("FSecurityName");
            unrealisedSecRec.CatCode = rs.getString("FCatName");
            unrealisedSecRec.SubCatCode = rs.getString("FSubCatCode");
            unrealisedSecRec.PortCode = rs.getString("FPortCode");
            unrealisedSecRec.CuryCode = rs.getString("FCuryCode");
            unrealisedSecRec.Amount = rs.getDouble("FStorageAmount");
            unrealisedSecRec.TotalCost = rs.getDouble("FAvgPrice");
            unrealisedSecRec.TotalCostB = rs.getDouble("FAvgPriceB");
            unrealisedSecRec.Bal = rs.getDouble("FStorageCost");
            unrealisedSecRec.BalB = rs.getDouble("FPortCuryCost");
            unrealisedSecRec.MaketPrice = rs.getDouble("MarketPrice");
            unrealisedSecRec.MaketPriceB = rs.getDouble("MarketPriceB");
            unrealisedSecRec.MaketValue = rs.getDouble("MarketValue");
            unrealisedSecRec.MaketValueB = rs.getDouble("MarketValueB");
            unrealisedSecRec.UnrealisedMoney = rs.getDouble("FGainAndLoss");
            unrealisedSecRec.UnrealisedMoneyB = rs.getDouble("FGainAndLossB");
            unrealisedSecRec.BondInterest = rs.getDouble("AccIngterest");
            unrealisedSecRec.FDate = rs.getDate("FDate");

            if (unrealisedSecRec.Name == null &&
                LastCuryCode.equals(unrealisedSecRec.CuryCode)) {
               unrealisedSecRec.BondInterestB = PortTotalBoughtInt;
               PortTotalBoughtInt = 0;
            }
            else {
               double dBal = 0;
               double dPortBal = 0;
               if (hmBoughtInt != null && !hmBoughtInt.isEmpty()) {
                  String sValue = (String) hmBoughtInt.get(unrealisedSecRec.
                        Code);
                  if (sValue != null && !"".equals(sValue)) {
                     dBal = Double.parseDouble(sValue.split("#")[1]);
                     dPortBal = Double.parseDouble(sValue.split("#")[2]);
                  }
               }

               unrealisedSecRec.BondInterestB = (rs.getDouble("AccIngterest") -
                                                 dBal) *
                     rs.getDouble("FBaseCuryRate") + dPortBal;
               PortTotalBoughtInt += unrealisedSecRec.BondInterestB;
            }

            //保留币种信息，方便按币种分组统计利息收入合计数
            LastCuryCode = unrealisedSecRec.CuryCode;

            String key = unrealisedSecRec.PortCode + "#" +
                  unrealisedSecRec.CatCode + "#" + unrealisedSecRec.SubCatCode +
                  "#" + unrealisedSecRec.CuryCode + "#" + unrealisedSecRec.Code;
            valueMap.put(key, unrealisedSecRec);
         }
      }
      catch (YssException ex) {
         throw new YssException("获取未兑现证券增值/贬值及债券收益分布数据出错！", ex);
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
    * createTempUnrealisedSecRec
    */
   private void createTempUnrealisedSecRec() throws YssException {
      String strSql = "";
      ResultSet rs = null;
      ResultSetMetaData rsmd = null;
      HashMap hmRsField = new HashMap();
      try {
         if (dbl.yssTableExist("tb_Data_UnrealisedSecRec_" + pub.getUserCode())) {
//            dbl.executeSql("drop table tb_Data_UnrealisedSecRec_" +
//                           pub.getUserCode());
        	 //edit by qiuxufeng 持久化表不删除 QDV4太平2010年09月16日02_A
        	 //判断原库表是否存在fdate字段，不存在则增加该字段
        	 strSql = "select * from tb_Data_UnrealisedSecRec_" + pub.getUserCode();
        	 rs = dbl.openResultSet(strSql);
        	 rsmd = rs.getMetaData();
        	 for (int i = 1; i <= rsmd.getColumnCount(); i++) {
        		 hmRsField.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
             }
        	 if(!hmRsField.containsKey("FDATE")) {
        		strSql = "alter table tb_Data_UnrealisedSecRec_" + pub.getUserCode() + " add fdate date null";
          		dbl.openResultSet(strSql);
        	 }
        	 //end
        	 return;
         }
         strSql = "create table tb_Data_UnrealisedSecRec_" + pub.getUserCode() +
               " (FCode varchar2(70) not null," +
               " FName varchar2(50)," +
               " FCatCode varchar2(20) not null," +
               " FSubCatCode varchar2(20) not null," +
               " FPortCode varchar2(20) not null," +
               " FCuryCode varchar2(20) not null," +
               " FAmount number(18,4)," +
               " FTotalCost number(18,4)," +
               " FTotalCostB number(18,4)," +
               " FBal number(18,4)," +
               " FBalB number(18,4)," +
               " FMaketPrice number(18,4)," +
               " FMaketPriceB number(18,4)," +
               " FMaketValue number(18,4)," +
               " FMaketValueB number(18,4)," +
               " FUnrealisedMoney number(18,4)," +
               " FUnrealisedMoneyB number(18,4)," +
               " FBondInterest number(18,4)," +
               " FBondInterestB number(18,4)," +
               " FDate Date)";
         dbl.executeSql(strSql);
      }
      catch (Exception e) {
         throw new YssException("生成临时未兑现证券增值/贬值及债券收益分布表出错!");
      }

   }

}
