package com.yss.main.operdeal.report.reptab;

import com.yss.base.*;
import com.yss.util.*;
import java.sql.*;
import java.util.*;
import com.yss.pojo.sys.*;
import java.util.HashMap;

//为收益汇总表建参数数据源临时表
public class TabYieldSum
      extends BaseAPOperValue {
   private String define = "";
   private java.util.Date beginDate;
   private java.util.Date endDate;
   private final static String TEMPTAB = "tb_Temp_YieldSumTree_";
   private HashMap hmField;
   boolean bFlag = false; //判断FPortCodes字段是否被付值
   public java.util.Date getEndDate() {
      return endDate;
   }

   public String getDefine() {
      return define;
   }

   public void setBeginDate(java.util.Date beginDate) {
      this.beginDate = beginDate;
   }

   public void setEndDate(java.util.Date endDate) {
      this.endDate = endDate;
   }

   public void setDefine(String define) {
      this.define = define;
   }

   public java.util.Date getBeginDate() {
      return beginDate;
   }

   public TabYieldSum() {

   }

   public void init(Object bean) throws YssException {
      String reqAry[] = null;
      String reqAry1[] = null;
      String sRowStr = (String) bean;
      if (sRowStr.trim().length() == 0) {
         return;
      }
      reqAry = sRowStr.split("\n");
      reqAry1 = reqAry[0].split("\r");
      this.beginDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      this.endDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[2].split("\r");
      this.define = reqAry1[1];
   }

   public Object invokeOperMothed() throws YssException {
//      this.define = "AssetSource,Assignee,Port,InvMgr,Cat,Cury";
      hmField = new HashMap();
      hmField.put("AssetSource", "FAssetSource");
      hmField.put("Assignee", "FAssigneeCode");
      hmField.put("Port", "FPortCode");
      hmField.put("InvMgr", "FAnalysisCode1");
      hmField.put("Cat", "FCatCode");
      hmField.put("Cury", "FCuryCode");

      createTmpTable();
      setClassTable();
//      refreshTmpTable();
      return "";
   }

//   protected void refreshTmpTable() throws YssException {
//      String strSql1 = "";
//      String strSql2 = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      try {
//         strSql1 = "update tb_Temp_YieldSumTree_admin set FPPortCodes = " +
//               "(select FPortCodes from tb_Temp_YieldSumTree_admin where FParentCode = '[root]' and FOrderCode = '001') " +
//               " where FParentCode = '[root]' and FOrderCode = '001'";
//
//         strSql2 = "update tb_Temp_YieldSumTree_admin set FPPortCodes = " +
//               "(select FPortCodes from tb_Temp_YieldSumTree_admin where FParentCode = '[root]' and FOrderCode = '002') " +
//               " where FParentCode = '[root]' and FOrderCode = '002'";
//
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql1);
//         dbl.executeSql(strSql2);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//
//      }
//      catch (Exception e) {
//         throw new YssException(e.getMessage());
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
//
//   }

   protected void createTmpTable() throws YssException {
      String strSql = "";
      try {
         if (dbl.yssTableExist(pub.yssGetTableName(TEMPTAB +
               pub.getUserCode()))) {
        	 /**shashijie ,2011-10-12 , STORY 1698*/
            dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                           pub.yssGetTableName(TEMPTAB +
                                               pub.getUserCode())));
            /**end*/
         }

         strSql = "create table " +
               pub.yssGetTableName(TEMPTAB + pub.getUserCode()) +
               " (FCode varchar2(150)," +
               " FName varchar2(50)," +
               " FCatCode varchar2(20)," +
               " FCuryCode varchar2(20)," +
               " FInvMgrCode varchar2(20)," +
               " FPortCodes varchar2(200)," +
               " FCusCatCode varchar2(20)," +
               " FPCatCode varchar2(20)," +
               " FPCuryCode varchar2(20)," +
               " FPInvMgrCode varchar2(20)," +
               " FPPortCodes varchar2(200)," +
               " FPCusCatCode varchar2(20)," +
               " FParentCode varchar2(150)," +
               " FOrderCode varchar2(20))";
         dbl.executeSql(strSql);

      }
      catch (Exception e) {
         throw new YssException("收益汇总临时表出错", e);
      }
   }

   protected void setClassTable() throws YssException {
      String strSql = "";
      ResultSet rs = null;
      HashMap hmIndex = new HashMap();
      HashMap hmData = new HashMap();
      String[] sDefineAry = null;
      YssTreeNode tNode = null;
      int iOrder = 1;
      String sOrderIndex = "";
      try {

         sDefineAry = define.split(",");
         hmIndex.put("[root]", "001");
         strSql = "select distinct FAssetSource,FAssigneeCode,port.FPortCode,FAnalysisCode1,FCatCode,FCuryCode from (" +
               " select a.*,b.FCatCode from " +
               pub.yssGetTableName("tb_stock_security") +
               " a join (select fsecuritycode,FCatCode from " +
               pub.yssGetTableName("Tb_Para_Security") +
               " where FCheckState = 1)" +
               " b on a.fsecuritycode = b.fsecuritycode " +
               " where FStorageDate between " + dbl.sqlDate(beginDate) +
               " and " + dbl.sqlDate(endDate) + ") stock left join" +
               " (select a. *, b.FAssigneeCode, (case when c.FPortLinkCode is null then a.FPortCode " +
               " else c.FPortLinkCode end) as FSubPortCode from ( " +
               "select * from " + pub.yssGetTableName("tb_para_portfolio") +
               " ) a left join (select FPortCode,FSubCode as FAssigneeCode" +
               " from " + pub.yssGetTableName("tb_para_portfolio_relaship") +
               " where FRelaType = 'Assignee' and FCheckState = 1) b" +
               " on a.FPortCode = b.FPortCode " +
               " left join (select FPortCode,FSubCode as FPortLinkCode from " +
               pub.yssGetTableName("tb_para_portfolio_relaship") +
               " where FRelaType = 'PortLink' and FCheckState = 1 ) c on a.FPortCode = b.FPortCode " +
               " where FCheckState = 1) port on stock.FPortCode = port.FPortCode " +
               " where port.FPortCode is not null" +
               " union " +
               " select distinct FAssetSource,FAssigneeCode,port.FPortCode,FAnalysisCode1,FAnalysisCode2,FCuryCode from" +
               " (select * from " +
               pub.yssGetTableName("tb_stock_Cash") +
               " where FStorageDate between " + dbl.sqlDate(beginDate) +
               " and " + dbl.sqlDate(endDate) + ") stock left join" +
               " (select a. *, b.FAssigneeCode, (case when c.FPortLinkCode is null then a.FPortCode " +
               " else c.FPortLinkCode end) as FSubPortCode from ( " +
               "select * from " + pub.yssGetTableName("tb_para_portfolio") +
               " ) a left join (select FPortCode,FSubCode as FAssigneeCode" +
               " from " + pub.yssGetTableName("tb_para_portfolio_relaship") +
               " where FRelaType = 'Assignee' and FCheckState = 1) b" +
               " on a.FPortCode = b.FPortCode " +
               " left join (select FPortCode,FSubCode as FPortLinkCode from " +
               pub.yssGetTableName("tb_para_portfolio_relaship") +
               " where FRelaType = 'PortLink' and FCheckState = 1 ) c on a.FPortCode = b.FPortCode " +
               " where FCheckState = 1) port on stock.FPortCode = port.FPortCode " +
               " where port.FPortCode is not null";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
//            sOrderIndex = "001";
            for (int i = 0; i < sDefineAry.length; i++) {
               tNode = new YssTreeNode();
               tNode.setCode(builderCode(rs, i + 1, hmField, define));
               if (i == 0) {
                  tNode.setParentCode("[root]");
               }
               else {
                  tNode.setParentCode(builderCode(rs, i, hmField, define));
               }
               tNode.setOrderCode( (String) hmIndex.get(tNode.getParentCode()));
               if (!hmData.containsKey(tNode.getCode())) {
                  hmData.put(tNode.getCode(), tNode);
                  sOrderIndex = (String) hmIndex.get(tNode.getParentCode());
//                  sOrderIndex = YssFun.right(sOrderIndex,3);
                  iOrder = Integer.parseInt(YssFun.right(sOrderIndex, 3));
                  hmIndex.put(tNode.getCode(), sOrderIndex + "001");
                  iOrder++;
                  sOrderIndex = sOrderIndex.substring(0,
                        sOrderIndex.length() - 3)
                        + YssFun.formatNumber(iOrder, "000");
                  hmIndex.put(tNode.getParentCode(), sOrderIndex);
               }
//               sOrderIndex += "001";
            }
         }
         insertTempTable(hmData);
//         insertPerformanceTable(hmData, "tb_Temp_PerformanceTree_");
//         adjustTable("tb_Temp_PerformanceTree_");
      }
      catch (Exception e) {
         throw new YssException(e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   protected void insertTempTable(HashMap hmData) throws
         YssException {
      String strSql = "";
      String sPortCodes = "";
      boolean bTrans = false;
      Connection conn = dbl.loadConnection();
      PreparedStatement pstmt = null;
      YssTreeNode tNode = null;
      String[] sDefineAry = null;
      String[] sNodeAry = null;
      String sFlag = "";
      String sFValue = "";
      try {
         sDefineAry = define.split(",");
         Iterator iter = hmData.values().iterator();
         conn.setAutoCommit(false);
         bTrans = true;
         strSql = "insert into " +
               pub.yssGetTableName(TEMPTAB + pub.getUserCode()) +
               " (FCode,FName,FParentCode,FOrderCode,FPortCodes,FCatCode,FCuryCode,FInvMgrCode," +
               " FPPortCodes,FPCatCode,FPCuryCode,FPInvMgrCode,FCusCatCode,FPCusCatCode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
         pstmt = conn.prepareStatement(strSql);
         while (iter.hasNext()) {
            tNode = (YssTreeNode) iter.next();
            sNodeAry = tNode.getCode().split("\f");
            pstmt.setString(1, tNode.getCode());
            tNode.setName(this.getItemName(tNode.getCode()));
            pstmt.setString(2, tNode.getName());
            pstmt.setString(3, tNode.getParentCode());
            pstmt.setString(4, tNode.getOrderCode());
            setNullStmt(pstmt, sDefineAry); //把没有定义的字段都设置为null;
            setPstmt(pstmt, sDefineAry, tNode.getCode(), false);
            if (!tNode.getParentCode().equalsIgnoreCase("[root]")) {
               setPstmt(pstmt, sDefineAry, tNode.getParentCode(), true);
            }
            else {
               setPstmt(pstmt, sDefineAry, tNode.getCode(), true); //如果父代码为"[root]"时，上级条件和本级相同
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

   private void setPstmt(PreparedStatement pstmt, String[] sDefineAry,
                         String sCode, boolean bParent) throws YssException {
      String[] sCodeAry = null;
      String sPortCodes = null;
      String sAssetCode = null;
      try {
         sCodeAry = sCode.split("\f");
         bFlag = false;
//         if (sCodeAry.length == 1 && !bParent) return;
         for (int i = 0; i < sCodeAry.length; i++) {
            if (sDefineAry[i].equalsIgnoreCase("AssetSource")) {
               bFlag = true;
               sPortCodes = getItemPortCodes(sCodeAry[i], "AssetSource");
               sAssetCode = sCodeAry[i];
               if (!bParent) {
                  pstmt.setString(5, sPortCodes);
               }
               else {
                  pstmt.setString(9, sPortCodes);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("Assignee")) {
               bFlag = true;
               sPortCodes = getItemPortCodes(sAssetCode + ";" + sCodeAry[i],
                                             "Assignee");
               if (!bParent) {
                  pstmt.setString(5, sPortCodes);
               }
               else {
                  pstmt.setString(9, sPortCodes);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("Port")) {
               bFlag = true;
               sPortCodes = getItemPortCodes(sCodeAry[i], "Port");
               if (!bParent) {
                  pstmt.setString(5, sPortCodes);
               }
               else {
                  pstmt.setString(9, sPortCodes);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("Cat")) {
               if (!bParent) {
                  pstmt.setString(6, sCodeAry[i]);
                  pstmt.setString(13, "1");
               }
               else {
                  pstmt.setString(10, sCodeAry[i]);
                  pstmt.setString(14, "1");
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("Cury")) {
               if (!bParent) {
                  pstmt.setString(7, sCodeAry[i]);
               }
               else {
                  pstmt.setString(11, sCodeAry[i]);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("InvMgr")) {
               if (!bParent) {
                  pstmt.setString(8, sCodeAry[i]);
               }
               else {
                  pstmt.setString(12, sCodeAry[i]);
               }
            }
         }

//         if (sCodeAry.length > 1){
         for (int i = sDefineAry.length -
              (sDefineAry.length - sCodeAry.length); i < sDefineAry.length;
              i++) {
            if (!bFlag) {
               if (!bParent) {
                  pstmt.setString(5, null);
               }
               else {
                  pstmt.setString(9, null);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("Cat")) {
               if (!bParent) {
                  pstmt.setString(6, null);
                  pstmt.setString(13, "null");
               }
               else {
                  pstmt.setString(10, null);
                  pstmt.setString(14, "null");
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("Cury")) {
               if (!bParent) {
                  pstmt.setString(7, null);
               }
               else {
                  pstmt.setString(11, null);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("InvMgr")) {
               if (!bParent) {
                  pstmt.setString(8, null);
               }
               else {
                  pstmt.setString(12, null);
               }
            }
         }
//         }
      }
      catch (Exception e) {
         throw new YssException(e);
      }
   }

   private void setNullStmt(PreparedStatement pstmt, String[] sDefineAry) throws
         YssException {

//      HashMap hmBak = (HashMap) hmField.clone();
//      ArrayList list = null;
      try {
         pstmt.setString(6, null);
         pstmt.setString(10, null);
         pstmt.setString(13, "0");
         pstmt.setString(14, "0");
         pstmt.setString(7, null);
         pstmt.setString(11, null);
         pstmt.setString(5, null);
         pstmt.setString(9, null);
         pstmt.setString(8, null);
         pstmt.setString(12, null);
//
//         for (int i = 0; i < sDefineAry.length; i++) {
//            if (hmBak.containsKey(sDefineAry[i])) {
//               hmBak.remove(sDefineAry[i]);
//            }
//         }
//         if (hmBak.size() > 0) {
//            list = new ArrayList();
//         }else{
//            return;
//         }
//         Iterator iter = hmBak.keySet().iterator();
//         while (iter.hasNext()) {
//            list.add(iter.next());
//         }
//         for (int i = 0; i < list.size(); i++) {
//            if ( ( (String) list.get(i)).equalsIgnoreCase("Cat")) {
//               pstmt.setString(6, null);
//               pstmt.setString(10, null);
//               pstmt.setString(13,"null");
//               pstmt.setString(14,"null");
//
//            }
//            else if ( ( (String) list.get(i)).equalsIgnoreCase("Cury")) {
//               pstmt.setString(7, null);
//               pstmt.setString(11, null);
//            }
//            else if ( ( (String) list.get(i)).equalsIgnoreCase("InvMgr")) {
//               pstmt.setString(8, null);
//               pstmt.setString(12, null);
//            }
//         }
//         if (!bFlag) {
//            pstmt.setString(5, null);
//            pstmt.setString(9, null);
//         }
      }
//
      catch (Exception e) {
         throw new YssException(e);
      }
   }

   protected String getItemPortCodes(String sCode, String sInd) throws
         YssException {
      String sResult = "";
      String strSql = "";
      ResultSet rs = null;
      String[] sNodeAry = null;
      try {
         if (sInd.equalsIgnoreCase("AssetSource")) {
            //根据资产来源获取组合
            strSql = "select FPortCode as FPort from " +
                  pub.yssGetTableName("Tb_Para_Portfolio") +
                  " where FCheckState = 1 and FAssetSource = " +
                  dbl.sqlString(sCode) + " and FPortType = 0";
         }
         else if (sInd.equalsIgnoreCase("Assignee")) {
            //根据委托人获取组合
            sNodeAry = sCode.split(";");
            if (!sNodeAry[0].equalsIgnoreCase("null")) {
               strSql =
                     "select a.FPortCode as FPort from (select FPortCode from " +
                     pub.yssGetTableName("tb_para_portfolio") +
                     " where FCheckState = 1 and FAssetSource = " +
                     dbl.sqlString(sNodeAry[0]) +
                     " and FPortType = 0) a join (select FPortCode from " +
                     pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                     " where FCheckState = 1 and FRelaType = 'Assignee' and FSubCode = " +
                     dbl.sqlString(sNodeAry[1]) +
                     ")b on a.FPortCode = b.FPortCode";
            }
            else {
               strSql =
                     "select FPortCode as FPort from " +
                     pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                     " where FCheckState = 1 and FRelaType = 'Assignee' and FSubCode = " +
                     dbl.sqlString(sNodeAry[1]);
            }
         }
         else if (sInd.equalsIgnoreCase("Port")) {
            //单个组合
            strSql = "";
            sResult = sCode;
         }
         if (strSql.length() > 0) {
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
               sResult += rs.getString("FPort") + ",";
            }
            if (sResult.length() > 1) {
               sResult = sResult.substring(0, sResult.length() - 1);
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

   protected String getItemName(String sNodeCode) throws YssException {
      String sCode = "";
      String sResult = "";
      String[] sNodeAry = null;
      String strSql = "";
      ResultSet rs = null;
      String[] sDefineAry = null;
      String sFlag = "";
      try {
         sNodeAry = sNodeCode.split("\f");
         sDefineAry = define.split(",");
         sCode = sNodeAry[sNodeAry.length - 1];
         sFlag = sDefineAry[sNodeAry.length - 1];
         if (sFlag.equalsIgnoreCase("AssetSource")) {
            //资产来源名称从字汇表获取
            strSql =
                  "select FVocCode as FItemCode,FVocName as FItemName from Tb_Fun_Vocabulary" +
                  " where FCheckState = 1 and FVocTypeCode = " +
                  dbl.sqlString(YssCons.YSS_PRT_ASSETSOURCE) +
                  " and FVocCode = " + dbl.sqlString(sCode);
         }
         else if (sFlag.equalsIgnoreCase("Assignee")) {
            //客户名称从委托人表获取
            strSql =
                  "select FAssigneeCode as FItemCode,FAssigneeName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_Assignee") +
                  " where FCheckState = 1 " +
                  " and FAssigneeCode = " + dbl.sqlString(sCode);
         }
         else if (sFlag.equalsIgnoreCase("Port")) {
            //组合名称从组合表获取
            strSql =
                  "select FPortCode as FItemCode,FPortName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_Portfolio") +
                  " where FCheckState = 1 " +
                  " and FPortCode = " + dbl.sqlString(sCode);
         }
         else if (sFlag.equalsIgnoreCase("Cury")) {
            //货币名称从货币表获取
            strSql =
                  "select FCuryCode as FItemCode,FCuryName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_Currency") +
                  " where FCheckState = 1 " +
                  " and FCuryCode = " + dbl.sqlString(sCode);
         }
         else if (sFlag.equalsIgnoreCase("Cat")) {
            //品种类型名称从品种类型表获取
            strSql = "select FCatCode as FItemCode,FCatName as FItemName from " +
                  " Tb_Base_Category where FCheckState = 1 " +
                  " and FCatCode = " + dbl.sqlString(sCode);
         }
         else if (sFlag.equalsIgnoreCase("InvMgr")) {
            //品种类型名称从品种类型表获取
            strSql =
                  "select FInvMgrCode as FItemCode,FInvMgrName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_InvestManager") +
                  " where FCheckState = 1 " +
                  " and FInvMgrCode = " + dbl.sqlString(sCode);
         }

         rs = dbl.openResultSet(strSql);
         if (rs.next()) {
            if (sNodeAry.length == 1) {
               sResult = "`" + rs.getString("FItemCode") + "--" +
                     rs.getString("FItemName") + "";
            }
            else if (sNodeAry.length == 2) {
               sResult = "`      " + rs.getString("FItemCode") + "--" +
                     rs.getString("FItemName") + "";
            }
            else if (sNodeAry.length == 3) {
               sResult = "`            " + rs.getString("FItemCode") + "--" +
                     rs.getString("FItemName") + "";
            }
            else if (sNodeAry.length == 4) {
               sResult = "`                  " + rs.getString("FItemCode") +
                     "--" +
                     rs.getString("FItemName") + "";
            }
            else if (sNodeAry.length == 5) {
               sResult = "`                        " + rs.getString("FItemCode") +
                     "--" +
                     rs.getString("FItemName") +
                     "";
            }
            else if (sNodeAry.length == 6) {
               sResult = "`                              " +
                     rs.getString("FItemCode") + "--" +
                     rs.getString("FItemName") + "";
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

   /**
    * builderCode
    *
    * @param rs ResultSet
    * @param i int
    * @return String
    */
   private String builderCode(ResultSet rs, int idx, HashMap hmField,
                              String sDefine) throws YssException {
      String[] sDefineAry = null;
      String sField = "";
      StringBuffer buf = new StringBuffer();
      try {
         sDefineAry = sDefine.split(",");
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

}
