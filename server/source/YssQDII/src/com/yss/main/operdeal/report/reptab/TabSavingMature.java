package com.yss.main.operdeal.report.reptab;

import com.yss.base.*;
import com.yss.util.*;
import java.sql.*;

//定期存款到期处理报表中需要的临时表，作为参数数据源使用
public class TabSavingMature
      extends BaseAPOperValue {
   private final static String TEMPTAB = "tb_tmp_CkDqCl";

   private String inAccNums = "";
   public TabSavingMature() {
   }

   public void init(Object bean) throws YssException {
      inAccNums = (String)bean;
   }

   public Object invokeOperMothed() throws YssException {
      createTmpTable();
      insertData();
      return null;
   }

   protected void insertData() throws YssException{
      String strSql = "";
      Connection conn = dbl.loadConnection();
      PreparedStatement pst = null;
      String[] inAccNumAry = inAccNums.split(",");
      try{
         conn.setAutoCommit(false);
         strSql = "delete from " + TEMPTAB +
               " where FUserCode = " + dbl.sqlString(pub.getUserCode());
         dbl.executeSql(strSql);

         strSql = "insert into " + TEMPTAB +
               "(FSavingInAccNum,FUserCode) values (?,?)";
         pst = conn.prepareStatement(strSql);
         for (int i=0; i<inAccNumAry.length; i++){
            pst.setString(1,inAccNumAry[i]);
            pst.setString(2,pub.getUserCode());
            pst.executeUpdate();
         }

         conn.commit();
         conn.setAutoCommit(true);
      }catch(Exception e){
         throw new YssException(e);
      }finally{
         dbl.closeStatementFinal(pst);
      }
   }

   protected void createTmpTable() throws YssException {
      String strSql = "";
      try {
         if (!dbl.yssTableExist(TEMPTAB)) {
            //2008.06.06 蒋锦 修改 考虑 DB2 的使用
            if(dbl.dbType == YssCons.DB_ORA){
            strSql = "create table " + TEMPTAB +
                  "( FSavingInAccNum varchar2(20)," +
                  " FUserCode varchar2(20)" +
                  ")";
            }
            else{
               strSql = "create table " + TEMPTAB +
                     "( FSavingInAccNum varchar(20)," +
                     " FUserCode varchar(20)" +
                     ")";
            }
            dbl.executeSql(strSql);
         }
      }
      catch (Exception e) {
         throw new YssException(e);
      }
   }

}
