package com.yss.main.operdeal.report.reptab;

import com.yss.base.BaseAPOperValue;
import com.yss.util.*;
import java.sql.*;
import java.util.*;
import com.yss.pojo.sys.*;

public class TabJYSLDCashFore
      extends BaseAPOperValue {

   private java.util.Date dStartDate;
   private java.util.Date dEndDate;
   private String portCode;

   public TabJYSLDCashFore() {
   }

   public void init(Object bean) throws YssException {
      String reqAry[] = null;
      String reqAry1[] = null;
      String sRowStr = (String) bean;
      if (sRowStr.trim().length() == 0)return;
      reqAry = sRowStr.split("\n");
      reqAry1 = reqAry[0].split("\r");
      this.dStartDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      this.dEndDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[2].split("\r");
      this.portCode = reqAry1[1];
   }

   public Object invokeOperMothed() throws YssException {
      createTmpTable();
      setCashForeTable();
      return "";
   }

   protected void createTmpTable() throws YssException {
      String strSql = "";
      try {
         if (dbl.yssTableExist(pub.yssGetTableName("tb_Temp_JY_CashForeTree_" +
               pub.getUserCode()))) {
        	 /**shashijie ,2011-10-12 , STORY 1698*/
            dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                           pub.yssGetTableName("tb_Temp_JY_CashForeTree_" +
                                               pub.getUserCode())));
            /**end*/
         }

         strSql = "create table " +
               pub.yssGetTableName("tb_Temp_JY_CashForeTree_" + pub.getUserCode()) +
               "(FPortCode varchar2(20)," +
               " FOrderCode int," +
               " FDate Date)";
         dbl.executeSql(strSql);

      }
      catch (Exception e) {
         throw new YssException("生成交银现金头寸预测表（本币）临时表出错");
      }
   }

   protected void setCashForeTable() throws YssException {
      String strSql = "";
      java.util.Date fDate;
      int n = 0;
      int j = 0;
      boolean bTrans = false; //代表是否开始了事务
      Connection conn = dbl.loadConnection();
      try {
         n = YssFun.dateDiff(this.dStartDate, this.dEndDate);
         for (j = 0; j <= n; j++) {
            fDate = YssFun.addDay(this.dStartDate, j);

            strSql = "insert into tb_Temp_JY_CashForeTree_" + pub.getUserCode() +
                  "(FPortCode,FOrderCode,FDate)" +
                  " values(" + dbl.sqlString(this.portCode) + "," +
                  j + "," +
                  dbl.sqlDate(fDate) +
                  ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

         }

      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }

   }

}
