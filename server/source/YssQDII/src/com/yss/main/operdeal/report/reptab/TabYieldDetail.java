package com.yss.main.operdeal.report.reptab;

import com.yss.base.*;
import com.yss.util.*;
import java.sql.*;
import java.util.*;
import com.yss.pojo.sys.*;

//为收益明细表建参数数据源临时表
public class TabYieldDetail
      extends BaseAPOperValue {

   private String define = "";
   private java.util.Date beginDate;
   private java.util.Date endDate;
   private HashMap hmField;
   private final static String TEMPTAB = "tb_Temp_YieldDetailTree_";
   public void setDefine(String define) {
      this.define = define;
   }

   public String getDefine() {
      return define;
   }

   public TabYieldDetail() {
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
      hmField = new HashMap();
      hmField.put("Port", "FPortCode");
      hmField.put("Cat", "FCatCode");
      hmField.put("Security", "FMarketCode");
      hmField.put("InvMgr", "FAnalysisCode1");

      createTmpTable();
      setClassTable();
      return "";
   }

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
               " FName varchar2(80)," +
               " FPortCode varchar2(20)," +
               " FInvMgrCode varchar2(20)," +
               " FCatCode varchar2(20)," +
               " FSecurityCode varchar2(20)," +
               " FPPortCode varchar2(20)," +
               " FPInvMgrCode varchar2(20)," +
               " FPCatCode varchar2(20)," +
               " FPSecurityCode varchar2(20)," +
               " FParentCode varchar2(150)," +
               " FOrderCode varchar2(20))";
         dbl.executeSql(strSql);

      }
      catch (Exception e) {
         throw new YssException("收益明细临时表出错", e);
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
         strSql =
               "select distinct a.FPortCode,b.FAnalysisCode1, b.FMarketCode, b.FCatCode from " +
               " (select FPortCode from " +
               pub.yssGetTableName("tb_para_portfolio") +
               " where FCheckState = 1) a join " +
               " (select FPortCode,FAnalysisCode1,FCatCode,FMarketCode from (select b1. *,b2.FCatCode,b2.FMarketCode from " +
               " (select * from " + pub.yssGetTableName("Tb_Stock_Security") +
               " where FCheckState = 1 and FStorageDate between " +
               dbl.sqlDate(beginDate) +
               " and " + dbl.sqlDate(endDate) +
               " and (FStorageAmount <> 0 or FStorageCost <> 0 or FMStorageCost <> 0 or FVStorageCost <> 0 or" +
               " FPortCuryCost <> 0 or FMPortCuryCost <> 0 or FVPortCuryCost <> 0 or FBaseCuryCost <> 0 or" +
               " FMBaseCuryCost <> 0 or FVBaseCuryCost <> 0)" +
               ") b1 join (select FSecurityCode,FCatCode,FMarketCode from " +
               pub.yssGetTableName("Tb_Para_Security") +
               " where FCheckState = 1) b2 on b1.FSecurityCode = b2.FSecurityCode)" +
               ") b on a.FPortCode = b.FPortCode";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
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
         insertTempTable(hmData);
      }
      catch (Exception e) {
         throw new YssException(e);
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
               " (FCode,FName,FParentCode,FOrderCode,FPortCode,FCatCode,FSecurityCode," +
               " FPPortCode,FPCatCode,FPsecurityCode,FInvMgrCode,FPInvMgrCode) values (?,?,?,?,?,?,?,?,?,?,?,?)";
         pstmt = conn.prepareStatement(strSql);
         while (iter.hasNext()) {
            tNode = (YssTreeNode) iter.next();
            sNodeAry = tNode.getCode().split("\f");
            pstmt.setString(1, tNode.getCode());
            tNode.setName(this.getItemName(tNode.getCode()));
            pstmt.setString(2, tNode.getName());
            pstmt.setString(3, tNode.getParentCode());
            pstmt.setString(4, tNode.getOrderCode());

            setPstmt(pstmt, sDefineAry, tNode.getCode(), false);
            if (!tNode.getParentCode().equalsIgnoreCase("[root]")) {
               setPstmt(pstmt, sDefineAry, tNode.getParentCode(), true);
            }
            else {
               setPstmt(pstmt, sDefineAry, tNode.getCode(), true); //如果父代码为"[root]"时，上级条件和本级相同
            }
            setNullStmt(pstmt, sDefineAry); //把没有定义的字段都设置为null;
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

         if (sFlag.equalsIgnoreCase("Port")) {
            //组合名称从组合表获取
            strSql =
                  "select FPortCode as FItemCode,FPortName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_Portfolio") +
                  " where FCheckState = 1 " +
                  " and FPortCode = " + dbl.sqlString(sCode);
         }
         else if (sFlag.equalsIgnoreCase("Cat")) {
            //品种类型名称从品种类型表获取
            strSql = "select FCatCode as FItemCode,FCatName as FItemName from " +
                  " Tb_Base_Category where FCheckState = 1 " +
                  " and FCatCode = " + dbl.sqlString(sCode);
         }
         else if (sFlag.equalsIgnoreCase("Security")) {
            strSql =
                  "select FSecurityCode as FItemCode,FSecurityName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_Security") +
                  " where FCheckState = 1 and FMarketCode = " +
                  dbl.sqlString(sCode);
         }
         else if (sFlag.equalsIgnoreCase("InvMgr")) {
            strSql =
                  "select FInvMgrCode as FItemCode,FInvMgrName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_InvestManager") +
                  " where FCheckState = 1 and FInvMgrCode = " +
                  dbl.sqlString(sCode);
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
                     "--" + rs.getString("FItemName") + "";
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

   private void setPstmt(PreparedStatement pstmt, String[] sDefineAry,
                         String sCode, boolean bParent) throws YssException {
      String[] sCodeAry = null;
      try {
         sCodeAry = sCode.split("\f");
         for (int i = 0; i < sCodeAry.length; i++) {
            if (sDefineAry[i].equalsIgnoreCase("Port")) {
               if (!bParent) {
                  pstmt.setString(5, sCodeAry[i]);
               }
               else {
                  pstmt.setString(8, sCodeAry[i]);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("Cat")) {
               if (!bParent) {
                  pstmt.setString(6, sCodeAry[i]);
               }
               else {
                  pstmt.setString(9, sCodeAry[i]);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("Security")) {
               if (!bParent) {
                  pstmt.setString(7, sCodeAry[i]);
               }
               else {
                  pstmt.setString(10, sCodeAry[i]);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("InvMgr")) {
               if (!bParent) {
                  pstmt.setString(11, sCodeAry[i]);
               }
               else {
                  pstmt.setString(12, sCodeAry[i]);
               }
            }
         }

         for (int i = sDefineAry.length -
              (sDefineAry.length - sCodeAry.length); i < sDefineAry.length;
              i++) {
            if (sDefineAry[i].equalsIgnoreCase("Port")) {
               if (!bParent) {
                  pstmt.setString(5, null);
               }
               else {
                  pstmt.setString(8, null);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("Cat")) {
               if (!bParent) {
                  pstmt.setString(6, null);
               }
               else {
                  pstmt.setString(9, null);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("Security")) {
               if (!bParent) {
                  pstmt.setString(7, null);
               }
               else {
                  pstmt.setString(10, null);
               }
            }
            else if (sDefineAry[i].equalsIgnoreCase("InvMgr")) {
               if (!bParent) {
                  pstmt.setString(11, null);
               }
               else {
                  pstmt.setString(12, null);
               }
            }
         }
      }
      catch (Exception e) {
         throw new YssException(e);
      }
   }

   private void setNullStmt(PreparedStatement pstmt, String[] sDefineAry) throws
         YssException {
      HashMap hmBak = (HashMap) hmField.clone();
      boolean bFlag = false;
      ArrayList list = null;
      try {
         for (int i = 0; i < sDefineAry.length; i++) {
            if (hmBak.containsKey(sDefineAry[i])) {
               hmBak.remove(sDefineAry[i]);
            }
         }
         if (hmBak.size() > 0) {
            list = new ArrayList();
         }
         else {
            return;
         }
         Iterator iter = hmBak.keySet().iterator();
         while (iter.hasNext()) {
            list.add(iter.next());
         }
         for (int i = 0; i < list.size(); i++) {
            if ( ( (String) list.get(i)).equalsIgnoreCase("Port")) {
               pstmt.setString(5, null);
               pstmt.setString(8, null);
            }
            else if ( ( (String) list.get(i)).equalsIgnoreCase("Cat")) {
               pstmt.setString(6, null);
               pstmt.setString(9, null);
            }
            else if ( ( (String) list.get(i)).equalsIgnoreCase("Security")) {
               pstmt.setString(7, null);
               pstmt.setString(10, null);
            }
            else if ( ( (String) list.get(i)).equalsIgnoreCase("InvMgr")) {
               pstmt.setString(11, null);
               pstmt.setString(12, null);
            }
         }
      }

      catch (Exception e) {
         throw new YssException(e);
      }
   }

}
