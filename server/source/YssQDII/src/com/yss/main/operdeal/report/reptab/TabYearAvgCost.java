package com.yss.main.operdeal.report.reptab;

import com.yss.main.dao.*;
import com.yss.dsub.BaseBean;
import com.yss.dsub.YssPub;
import java.util.Iterator;
import java.util.HashMap;
import com.yss.pojo.param.reptab.YssYearAvgCost;
import com.yss.pojo.cache.YssKeyDouble;
import com.yss.util.*;
import java.sql.*;
import com.yss.base.*;

//计算股票年平均成本
public class TabYearAvgCost
      extends BaseAPOperValue {

   private java.util.Date startDate; //起始日期
   private java.util.Date endDate; //截止日期

   public TabYearAvgCost() {
   }

   /**
    * getOperDoubleValue
    *
    * @return double
    */
   public double getOperDoubleValue() {
      return 0.0;
   }

   /**
    * init
    *
    * @param bean BaseBean
    */
   public void init(Object bean) throws YssException {
      String reqAry[] = null;
      String reqAry1[] = null;
      String sRowStr = (String) bean;
      if (sRowStr.trim().length() == 0)return;
      reqAry = sRowStr.split("\n");
      reqAry1 = reqAry[0].split("\r");
      this.startDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      this.endDate = YssFun.toDate(reqAry1[1]);
   }

   /**
    * invokeOperMothed
    *
    * @return Object
    */
   public Object invokeOperMothed() throws YssException {
      String strSql = "";
      String strInsert = "";
      PreparedStatement pst = null;
      HashMap hmResult = new HashMap();
      YssKeyDouble keyDouble = null;
      String strSqlStorage = "";
      Connection conn = dbl.loadConnection();
      ResultSet rs = null;
      ResultSet rsStorage = null;
      boolean bTrans = false;
      int dateNum;

      try {
         createYearAvgCostTmpTable();

         dateNum = YssFun.dateDiff(this.startDate, this.endDate);

         strSql = "select sum(FTradeMoney * FCashInd * FBaseCuryRate) as FTradeMoney,a.FPortCode,a.FInvMgrCode,FBargainDate,b.FTradeCury from " +
               pub.yssGetTableName("tb_data_subtrade") +
               " a join (select FSecurityCode, FTradeCury from " +
               pub.yssGetTableName("Tb_Para_Security") +
               " ) b on a.FSecurityCode = b.FSecurityCode " +
               "left join (select * from tb_base_tradetype where FCheckState = 1) c on a.FTradeTypeCode = c.FTradeTypeCode " +
               " where a.FBargainDate between " + dbl.sqlDate(this.startDate) +
               " and " + dbl.sqlDate(this.endDate) +
               " and a.FCheckState = 1 group by b.FTradeCury, a.FPortCode, a.FInvMgrCode, a.FBargainDate";

         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            setHaspMapResult(rs, hmResult, rs.getDate("FBargainDate"));
         }

         strSqlStorage = "select sum(FBaseCuryCost) as FBaseCuryCost,FPortCode,FCuryCode,FAnalysisCode1 from " +
               pub.yssGetTableName("Tb_Stock_Security") +
               " a1 join (select FSecurityCode from " +
               pub.yssGetTableName("Tb_Para_Security") +
               " where FCatCode = 'EQ' and FCheckState = 1) a2 on a1.FSecurityCode = a2.FSecurityCode " +
               " where FYearMonth = " +
               dbl.sqlString(YssFun.formatDate(this.startDate, "yyyy") + "00") +
               " group by a1.FPortCode, a1.FCuryCode, a1.FAnalysisCode1";

         rsStorage = dbl.openResultSet(strSqlStorage);
         while (rsStorage.next()) {
            addStorage(rsStorage, hmResult);
         }

         strInsert = "insert into Tb_tmp_avgcost_" +
               pub.getUserCode() +
               " (FPortCode,FInvMgrCode,FTradeCury,FTradeMoney,FBalance,FDate) " +
               " values (?,?,?,?,?,?)";
         pst = conn.prepareStatement(strInsert);

         Iterator iter = hmResult.values().iterator();
         conn.setAutoCommit(false);
         bTrans = true;
         while (iter.hasNext()) {
            keyDouble = (YssKeyDouble) iter.next();

            pst.setString(1, keyDouble.getStrPortCode());
            pst.setString(2, keyDouble.getStrInvMgrCode());
            pst.setString(3, keyDouble.getStrCuryCode());
            pst.setDouble(4, keyDouble.getDAvgCostValue());
            pst.setDouble(5,keyDouble.getBalance());
            pst.setDate(6, YssFun.toSqlDate(this.endDate));

            pst.executeUpdate();

         }
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);

         return "";
      }

      catch (Exception e) {
         throw new YssException("股票年平均成本临时表插入数据出错！");
      }

      finally {
         dbl.closeStatementFinal(pst);
         dbl.endTransFinal(conn, bTrans);
         dbl.closeResultSetFinal(rs);
         dbl.closeResultSetFinal(rsStorage);
      }
   }

   public void setHaspMapResult(ResultSet rs, HashMap hmResult,
                                java.util.Date dDate) throws
         SQLException {
      String strHmKey = "";
      double dTmp = 0;
      YssKeyDouble keyDouble = null;

      strHmKey = rs.getString("FPortCode") + "," + rs.getString("FInvMgrCode") +
            "," + rs.getString("FTradeCury");

      if (!hmResult.containsKey(strHmKey)) {
         keyDouble = new YssKeyDouble();
         keyDouble.setStrPortCode(rs.getString("FPortCode"));
         keyDouble.setStrInvMgrCode(rs.getString("FInvMgrCode"));
         keyDouble.setStrCuryCode(rs.getString("FTradeCury"));

         dTmp = calDayAvgCost(rs.getDate("FBargainDate"),
                              rs.getDouble("FTradeMoney"));

         keyDouble.setDAvgCostValue(dTmp);
         hmResult.put(strHmKey, keyDouble);
      }
      else {
         keyDouble = (YssKeyDouble) hmResult.get(strHmKey);
         dTmp = calDayAvgCost(rs.getDate("FBargainDate"),
                              rs.getDouble("FTradeMoney"));
         keyDouble.setDAvgCostValue(YssD.add(dTmp,
                                             keyDouble.getDAvgCostValue()));
         hmResult.put(strHmKey, keyDouble);
      }

   }

   protected double calDayAvgCost(java.util.Date dDate, double dMoney) {
      double dResult = 0;
      int dayNum = 0;
      int iDay = 0;
      double dTmp = 0;
      dayNum = YssFun.dateDiff(this.startDate, this.endDate);
      iDay = YssFun.dateDiff(this.startDate, dDate);
      dTmp = YssD.sub(dayNum, iDay);
      dTmp = YssD.div(dTmp, dayNum);
      dResult = YssD.mul(dMoney, dTmp);
      return dResult;
   }

   public void addStorage(ResultSet rs, HashMap hmResult) throws SQLException {
      String strHmKey = "";
      YssKeyDouble keyDouble = null;

      strHmKey = rs.getString("FPortCode") + "," +
            rs.getString("FAnalysisCode1") +
            "," + rs.getString("FCuryCode");

      if (!hmResult.containsKey(strHmKey)) {
         keyDouble = new YssKeyDouble();
         keyDouble.setStrPortCode(rs.getString("FPortCode"));
         keyDouble.setStrInvMgrCode(rs.getString("FAnalysisCode1"));
         keyDouble.setStrCuryCode(rs.getString("FCuryCode"));

         keyDouble.setBalance(rs.getDouble("FBaseCuryCost"));
         hmResult.put(strHmKey, keyDouble);
      }
      else {
         keyDouble = (YssKeyDouble) hmResult.get(strHmKey);
         keyDouble.setBalance(rs.getDouble("FBaseCuryCost"));
         hmResult.put(strHmKey, keyDouble);
      }

   }

   private void createYearAvgCostTmpTable() throws YssException {
      String strSql = "";
      try {
         if (dbl.yssTableExist("Tb_tmp_avgcost_" + pub.getUserCode())) {
        	 /**shashijie ,2011-10-12 , STORY 1698*/
            dbl.executeSql(dbl.doOperSqlDrop("drop table Tb_tmp_avgcost_" +
                           pub.getUserCode()));
            /**end*/
         }

         strSql = "create table tb_tmp_avgcost_" + pub.getUserCode() +
               "( FPortCode varchar2(20)," +
               " FInvMgrCode varchar2(20)," +
               " FTradeCury varchar2(20)," +
               " FTradeMoney decimal(18, 4)," +
               " FBalance decimal(18,4)," +
               " FDate Date)";

         dbl.executeSql(strSql);
      }
      catch (Exception e) {
         throw new YssException("创建股票年平均成本临时表出错！");
      }

   }
}
