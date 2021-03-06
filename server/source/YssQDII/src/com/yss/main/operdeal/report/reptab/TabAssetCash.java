package com.yss.main.operdeal.report.reptab;

import com.yss.base.BaseAPOperValue;
import com.yss.util.*;
import java.sql.*;
import java.util.*;
import com.yss.pojo.sys.*;

//用于资产分布表中出现金部分的资产分布的树形结构临时表
public class TabAssetCash
      extends BaseAPOperValue {
   private java.util.Date dDate;
   private String portCode;
   private String invMgrCode;

   public TabAssetCash() {
   }

   public void init(Object bean) throws YssException {
      String reqAry[] = null;
      String reqAry1[] = null;
      String sRowStr = (String) bean;
      if (sRowStr.trim().length() == 0)return;
      reqAry = sRowStr.split("\n");
      reqAry1 = reqAry[0].split("\r");
      this.dDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      this.portCode = reqAry1[1];
      if (reqAry.length > 2) {
         reqAry1 = reqAry[2].split("\r");
         this.invMgrCode = reqAry1[1];
      }
   }

   public Object invokeOperMothed() throws YssException {
      createTmpTable();
      setAssetCashTable();
      return "";
   }

   protected void createTmpTable() throws YssException {
      String strSql = "";
      try {
         if (dbl.yssTableExist(pub.yssGetTableName("tb_Temp_AssetCashTree_" +
               pub.getUserCode()))) {
        	 /**shashijie ,2011-10-12 , STORY 1698*/
            dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                           pub.yssGetTableName("tb_Temp_AssetCashTree_" +
                                               pub.getUserCode())));
            /**end*/
         }

         strSql = "create table " +
               pub.yssGetTableName("tb_Temp_AssetCashTree_" + pub.getUserCode()) +
               " (FCode varchar2(70)," +
               " FName varchar2(50)," +
               " FCatCode varchar2(20)," +
               " FCuryCode varchar2(20)," +
               " FInvMgrCode varchar2(20)," +
               " FParentCode varchar2(70)," +
               " FOrderCode varchar2(20))";
         dbl.executeSql(strSql);

      }
      catch (Exception e) {
         throw new YssException("生成证券部分资产分布临时表出错");
      }

   }

   protected void setAssetCashTable() throws YssException {
      String strSql = "";
      ResultSet rs = null;
      HashMap hmIndex = new HashMap();
      HashMap hmData = new HashMap();
      String[] sValCashDefineAry = null;
      YssTreeNode tNode = null;
      int iOrder = 1;
      String sOrderIndex = "";
      String sValCashDefine = "category;currency";
      HashMap hmField = new HashMap();
      try {
         hmField.put("category", "FAnalysisCode2");
         hmField.put("currency", "FCuryCode");
         sValCashDefineAry = sValCashDefine.split(";");
         hmIndex.put("[root]", "001");
         strSql = "select distinct FAnalysisCode2 , FCuryCode from " +
               pub.yssGetTableName("Tb_Stock_Cash") +
               "  where FCheckState = 1 and FPortCode = " +
               dbl.sqlString(this.portCode) +
               (this.invMgrCode==null ? " " :
               " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode)) +
               " and FStorageDate = " + dbl.sqlDate(this.dDate) +
               " and FYearMonth = " +
               dbl.sqlString(YssFun.formatDate(this.dDate, "yyyyMM"));

         rs = dbl.openResultSet(strSql);

         while (rs.next()) {
            for (int i = 0; i < sValCashDefineAry.length; i++) {
               tNode = new YssTreeNode();
               tNode.setCode(builderCode(rs, i + 1, hmField, sValCashDefine));
               if (i == 0) {
                  tNode.setParentCode("[root]");
               }
               else {
                  tNode.setParentCode(builderCode(rs, i, hmField,
                                                  sValCashDefine));
               }
               tNode.setOrderCode( (String) hmIndex.get(tNode.getParentCode()));
               if (!hmData.containsKey(tNode.getCode())) {
                  hmData.put(tNode.getCode(), tNode);
                  sOrderIndex = (String) hmIndex.get(tNode.getParentCode());
                  iOrder = Integer.parseInt(YssFun.right(sOrderIndex, 3));
                  hmIndex.put(tNode.getCode(), sOrderIndex + "001");
                  iOrder++;
                  sOrderIndex = sOrderIndex.substring(0,
                        sOrderIndex.length() - 3)
                        + YssFun.formatNumber(iOrder, "000");
                  hmIndex.put(tNode.getParentCode(), sOrderIndex);
               }
            }
         }
         insertAssetCashTable(hmData, "tb_Temp_AssetCashTree_");
      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   private String builderCode(ResultSet rs, int idx, HashMap hmField,
                              String sDefine) throws YssException {
      String[] sDefineAry = null;
      String sField = "";
      StringBuffer buf = new StringBuffer();
      try {
         sDefineAry = sDefine.split(";");
         for (int i = 0; i < idx; i++) {
            sField = (String) hmField.get(sDefineAry[i]);
            buf.append(rs.getString(sField) + "").append("\f");
         }
         if (buf.length() > 0) {
            buf.setLength(buf.length() - 1);
         }
         return buf.toString();
      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }
   }

   protected void insertAssetCashTable(HashMap hmData, String sTableName) throws
         YssException {
      String strSql = "";
      String sCatCode = "";
      String sCuryCode = "";
      boolean bTrans = false;
      Connection conn = dbl.loadConnection();
      PreparedStatement pstmt = null;
      YssTreeNode tNode = null;
      try {
         Iterator iter = hmData.values().iterator();
         conn.setAutoCommit(false);
         bTrans = true;
         strSql = "insert into " +
               pub.yssGetTableName(sTableName + pub.getUserCode()) +
               " (FCode,FName,FCatCode,FCuryCode,FParentCode,FOrderCode,FInvMgrCode) values (?,?,?,?,?,?,?)";
         pstmt = conn.prepareStatement(strSql);
         while (iter.hasNext()) {
            tNode = (YssTreeNode) iter.next();
            sCatCode = this.getCatCode(tNode.getCode());
            sCuryCode = this.getCuryCode(tNode.getCode());
            tNode.setName(this.getItemName(tNode.getCode()));

            pstmt.setString(1, tNode.getCode());
            pstmt.setString(2, tNode.getName());
            pstmt.setString(3, sCatCode);
            pstmt.setString(4, sCuryCode);
            pstmt.setString(5, tNode.getParentCode());
            pstmt.setString(6, tNode.getOrderCode());
            if(this.invMgrCode==null){
               pstmt.setString(7,null);
            }else{
               pstmt.setString(7, this.invMgrCode);
            }
            pstmt.executeUpdate();
         }
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }
      finally {
		 dbl.closeStatementFinal(pstmt);//add by rujiangpeng 20100603打开多张报表系统需重新登录
         dbl.endTransFinal(conn, bTrans);
      }
   }

   protected String getCatCode(String sNodeCode) {
      String[] sNodeAry = null;
      String sCode = "";
      sNodeAry = sNodeCode.split("\f");
      sCode = sNodeAry[0];
      return sCode;
   }

   protected String getCuryCode(String sNodeCode) {
      String[] sNodeAry = null;
      String sCode = "";
      sNodeAry = sNodeCode.split("\f");
      if (sNodeAry.length == 2) {
         sCode = sNodeAry[1];
      }
      else if (sNodeAry.length == 1) {
         sCode = null;
      }
      return sCode;
   }

   protected String getItemName(String sNodeCode) throws YssException {
      String sCode = "";
      String sResult = "";
      String[] sNodeAry = null;
      String strSql = "";
      ResultSet rs = null;
      try {
         sNodeAry = sNodeCode.split("\f");
         sCode = sNodeAry[sNodeAry.length - 1];
         if (sNodeAry.length == 1) {
            //品种从品种类型表中获取
            strSql =
                  "select FCatCode,FCatName as FItemName from Tb_Base_Category" +
                  " where FCheckState = 1 and FCatCode = " +
                  dbl.sqlString(sCode);
         }
         else if (sNodeAry.length == 2) {
            //币种从币种设置表中获取
            strSql = "select FCuryCode,FCuryName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_Currency") +
                  " where FCheckState = 1 " +
                  " and FCuryCode = " + dbl.sqlString(sCode);
         }
         rs = dbl.openResultSet(strSql);
         if (rs.next()) {
            if (sNodeAry.length == 1) {
               sResult = "`" + rs.getString("FItemName") + "";
            }
            else if (sNodeAry.length == 2) {
               sResult = "`    " + rs.getString("FItemName") + "";
            }
         }
         return sResult;
      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

}
