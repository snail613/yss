package com.yss.main.operdeal.report.reptab;

import com.yss.main.dao.*;
import com.yss.dsub.*;
import com.yss.util.*;
import java.sql.*;
import java.util.*;
import com.yss.pojo.sys.*;
import com.yss.pojo.cache.*;
import com.yss.pojo.cache.YssKeyDouble;
import com.yss.base.BaseAPOperValue;

//为Performance报表建参数数据源临时表，该报表分成三个级别，
//分别为资产来源、受托人、组合
public class TabPerformance
      extends BaseAPOperValue {
   private java.util.Date endDate;
   private String catCode;
   public TabPerformance() {
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
      this.endDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      this.catCode = reqAry1[1];
   }

   /**
    * invokeOperMothed
    *
    * @return Object
    */
   public Object invokeOperMothed() throws YssException {
      createTmpTable();
      setSecClassTable();
      setAvgFundInTable();
      return "";
   }

   protected void setAvgFundInTable() throws YssException {
      String strSql = "";
      ResultSet rs = null;
      double dTmp = 0;
      java.util.Date dBeginDate = null;
      HashMap hmData = new HashMap();
      YssKeyDouble kd = null;
      try {
         dBeginDate = YssFun.toDate(YssFun.getYear(endDate) + "-01-01");
         strSql = "select FPortCode,FTransferDate,sum(FMoney*FInOut*FBaseCuryRate) as FSumMoney from " +
               pub.yssGetTableName("Tb_Cash_SubTransfer") +
               " a join (select * from " +
               pub.yssGetTableName("Tb_Cash_Transfer") +
               " where FCheckState = 1 and FTsfTypeCode = '" +
               YssOperCons.YSS_ZJDBLX_InnerAccount +
               "' and FSubTsfTypeCode = '" +
               YssOperCons.YSS_ZJDBZLX_COST_FACT +
               "' and FTransferDate between " +
               dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(endDate) + ") b" +
               " on a.FNum = b.FNum " +
               " where a.FAnalysisCode2 = " + dbl.sqlString(catCode) +
               " group by a.FPortCode, a.FTransferDate";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            if (hmData.containsKey(rs.getString("FPortCode"))) {
               kd = (YssKeyDouble) hmData.get(rs.getString("FPortCode"));
               dTmp = calDayAvgFindIn(rs.getDate("FTransferDate"), dBeginDate,
                                      rs.getDouble("FSumMoney"));
               kd.setDAvgCostValue(YssD.add(kd.getDAvgCostValue(), dTmp));
            }
            else {
               kd = new YssKeyDouble();
               kd.setStrPortCode(rs.getString("FPortCode"));
               dTmp = calDayAvgFindIn(rs.getDate("FTransferDate"), dBeginDate,
                                      rs.getDouble("FSumMoney"));
               kd.setDAvgCostValue(dTmp);
               hmData.put(kd.getStrPortCode(), kd);
            }
         }
         insertAvgFundInTable(hmData, "tb_Temp_PortAvgFundIn_");
      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   protected double calDayAvgFindIn(java.util.Date dDate,
                                    java.util.Date beginDate, double dMoney) {
      double dResult = 0;
      int iDay = 0;
      double dTmp = 0;
      iDay = YssFun.dateDiff(beginDate, dDate);
      dTmp = YssD.sub(365, iDay);
      dTmp = YssD.div(dTmp, 365);
      dResult = YssD.mul(dMoney, dTmp);
      return dResult;
   }

   protected void setSecClassTable() throws YssException {
      String strSql = "";
      ResultSet rs = null;
      HashMap hmIndex = new HashMap();
      HashMap hmData = new HashMap();
      String[] sValSecDefineAry = null;
      YssTreeNode tNode = null;
      int iOrder = 1;
      String sOrderIndex = "";
      String sValSecDefine = "AssetSource;Assignee;Port;SubPort";
      HashMap hmField = new HashMap();
      try {
         hmField.put("AssetSource", "FAssetSource");
         hmField.put("Assignee", "FAssigneeCode");
         hmField.put("Port", "FPortCode");
         hmField.put("SubPort", "FSubPortCode");
         sValSecDefineAry = sValSecDefine.split(";");
         hmIndex.put("[root]", "001");
         strSql = "select a. *, b.FAssigneeCode, (case when c.FPortLinkCode is null then a.FPortCode " +
               " else c.FPortLinkCode end) as FSubPortCode from ( " +
               "select * from " + pub.yssGetTableName("tb_para_portfolio") +
               " ) a left join (select FPortCode,FSubCode as FAssigneeCode" +
               " from " + pub.yssGetTableName("tb_para_portfolio_relaship") +
               " where FRelaType = 'Assignee' and FCheckState = 1) b" +
               " on a.FPortCode = b.FPortCode " +
               " left join (select FPortCode,FSubCode as FPortLinkCode from " +
               pub.yssGetTableName("tb_para_portfolio_relaship") +
               " where FRelaType = 'PortLink' and FCheckState = 1 ) c on a.FPortCode = b.FPortCode " +
               "where a.FCheckState = 1";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
//            sOrderIndex = "001";
            for (int i = 0; i < sValSecDefineAry.length; i++) {
               tNode = new YssTreeNode();
               tNode.setCode(builderCode(rs, i + 1, hmField, sValSecDefine));
               if (i == 0) {
                  tNode.setParentCode("[root]");
               }
               else {
                  tNode.setParentCode(builderCode(rs, i, hmField, sValSecDefine));
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
         insertPerformanceTable(hmData, "tb_Temp_PerformanceTree_");
         adjustTable("tb_Temp_PerformanceTree_");
      }
      catch (Exception e) {
         throw new YssException("");
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
   }

   protected void createTmpTable() throws YssException {
      String strSql = "";
      try {
         if (dbl.yssTableExist(pub.yssGetTableName("tb_Temp_PerformanceTree_" +
               pub.getUserCode()))) {
        	 /**shashijie ,2011-10-12 , STORY 1698*/
            dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                           pub.yssGetTableName("tb_Temp_PerformanceTree_" +
                                               pub.getUserCode())));
            /**end*/
         }

         strSql = "create table " +
               pub.yssGetTableName("tb_Temp_PerformanceTree_" + pub.getUserCode()) +
               " (FCode varchar2(70)," +
               " FName varchar2(50)," +
               " FParentCode varchar2(70)," +
               " FPortCodes varchar2(200)," +
               " FOrderCode varchar2(20))";
         dbl.executeSql(strSql);

         if (dbl.yssTableExist(pub.yssGetTableName("tb_Temp_PortAvgFundIn_" +
               pub.getUserCode()))) {
        	 /**shashijie ,2011-10-12 , STORY 1698*/
            dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                           pub.yssGetTableName("tb_Temp_PortAvgFundIn_" +
                                               pub.getUserCode())));
            /**end*/
         }

         strSql = "create table " +
               pub.yssGetTableName("tb_Temp_PortAvgFundIn_" + pub.getUserCode()) +
               " (FPortCode varchar2(20)," +
               " FMoney number(18,12))";
         dbl.executeSql(strSql);

      }
      catch (Exception e) {
         throw new YssException("");
      }
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
            //资产来源名称从字汇表获取
            strSql =
                  "select FVocCode,FVocName as FItemName from Tb_Fun_Vocabulary" +
                  " where FCheckState = 1 and FVocTypeCode = " +
                  dbl.sqlString(YssCons.YSS_PRT_ASSETSOURCE) +
                  " and FVocCode = " + dbl.sqlString(sCode);
         }
         else if (sNodeAry.length == 2) {
            //客户名称从委托人表获取
            strSql = "select FAssigneeCode,FAssigneeName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_Assignee") +
                  " where FCheckState = 1 " +
                  " and FAssigneeCode = " + dbl.sqlString(sCode);
         }
         else if (sNodeAry.length == 3) {
            //组合名称从组合表获取
            strSql = "select FPortCode,FPortName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_Portfolio") +
                  " where FCheckState = 1 " +
                  " and FPortCode = " + dbl.sqlString(sCode);
         }
         else if (sNodeAry.length == 4) {
            strSql = "select FPortCode,FPortName as FItemName from " +
                  pub.yssGetTableName("Tb_Para_Portfolio") +
                  " where FCheckState = 1 " +
                  " and FPortCode = " + dbl.sqlString(sCode);
         }
         rs = dbl.openResultSet(strSql);
         if (rs.next()) {
            if (sNodeAry.length == 1) {
               sResult = "`" + rs.getString("FItemName") + "";
            }
            else if (sNodeAry.length == 2) {
               sResult = "`  " + rs.getString("FItemName") + "";
            }
            else if (sNodeAry.length == 3) {
               sResult = "`    " + rs.getString("FItemName") + "";
            }
            else if (sNodeAry.length == 4) {
               sResult = "`      " + rs.getString("FItemName") + "";
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

   protected String getItemPortCodes(String sNodeCode) throws YssException {
      String sCode = "";
      String sResult = "";
      String strSql = "";
      ResultSet rs = null;
      ResultSet rs1 = null;
      String[] sNodeAry = null;
      try {
         sNodeAry = sNodeCode.split("\f");
         sCode = sNodeAry[sNodeAry.length - 1];
         if (sNodeAry.length == 1) {
            //根据资产来源获取组合
            strSql = "select FPortCode as FPort from " +
                  pub.yssGetTableName("Tb_Para_Portfolio") +
                  " where FCheckState = 1 and FAssetSource = " +
                  dbl.sqlString(sCode) + " and FPortType = 0";
         }
         else if (sNodeAry.length == 2) {
            //根据委托人获取组合
            strSql = "select a.FPortCode as FPort from (select FPortCode from " +
                  pub.yssGetTableName("tb_para_portfolio") +
                  " where FCheckState = 1 and FAssetSource = " +
                  dbl.sqlString(sNodeAry[0]) +
                  " and FPortType = 0) a join (select FPortCode from " +
                  pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                  " where FCheckState = 1 and FRelaType = 'Assignee' and FSubCode = " +
                  dbl.sqlString(sNodeAry[1]) +
                  ")b on a.FPortCode = b.FPortCode";

            /*"select FPortCode from " +
             pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
             " where FCheckState = 1 and FRelaType = 'Assignee'" +
                               " and FSubCode = " + dbl.sqlString(sCode);*/
         }
         else if (sNodeAry.length == 3) {
            //根据汇总组合获取明细组合
//            strSql =  "select (case when b.FPortType = 2 then a.FSubCode else " +
//                  dbl.sqlString(sCode) + " end) as FPort from (" +
//                  "select FSubCode from " +
//                  pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
//                  " where FCheckState = 1 and FPortCode = " +
//                  dbl.sqlString(sCode) + " and FRelaType = 'PortLink'" +
//                  ") a left join (select FPortCode,FPortType from " +
//                  pub.yssGetTableName("Tb_Para_Portfolio") +
//                  " where FCheckState = 1 ) b on a.FPortCode = b.FPortCode";
            strSql = "select  FSubCode from " +
                  pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                  " where FCheckState = 1 and FPortCode = " +
                  dbl.sqlString(sCode) + " and FRelaType = 'PortLink'";

            rs1 = dbl.openResultSet(strSql);
            if (rs1.next()) {
               rs1.beforeFirst();
               while (rs1.next()) {
                  sResult += rs1.getString("FSubCode") + ",";
               }
               if (sResult.length() > 1) {
                  sResult = sResult.substring(0, sResult.length() - 1);
               }
            }
            else sResult = sCode;
            strSql = "";
         }
         else if (sNodeAry.length == 4) {
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
         dbl.closeResultSetFinal(rs1);
      }

   }

   protected void insertPerformanceTable(HashMap hmData, String sTableName) throws
         YssException {
      String strSql = "";
      String sPortCodes = "";
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
               " (FCode,FName,FParentCode,FOrderCode,FPortCodes) values (?,?,?,?,?)";
         pstmt = conn.prepareStatement(strSql);
         while (iter.hasNext()) {
            tNode = (YssTreeNode) iter.next();
            tNode.setName(this.getItemName(tNode.getCode()));
            sPortCodes = this.getItemPortCodes(tNode.getCode());

            pstmt.setString(1, tNode.getCode());
            pstmt.setString(2, tNode.getName());
            pstmt.setString(3, tNode.getParentCode());
            pstmt.setString(4, tNode.getOrderCode());
            pstmt.setString(5, sPortCodes);
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

   protected void adjustTable(String sTableName) throws YssException {
      String strSql = "";
      String strDel = "";
      ResultSet rs = null;
      String code = "";
      String[] sCodeAry = null;
      boolean bTrans = false; //代表是否开始了事务
      Connection conn = dbl.loadConnection();
      try {
         strSql = "select FCode,FOrderCode from " +
               pub.yssGetTableName(sTableName + pub.getUserCode()) +
               " where " + dbl.sqlLen("FOrderCode") + " = 12 ";
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
            code = rs.getString("FCode");
            sCodeAry = code.split("\f");
            if (sCodeAry[2].equalsIgnoreCase(sCodeAry[3])) {
               strDel = "delete from " +
                     pub.yssGetTableName(sTableName + pub.getUserCode()) +
                     " where FOrderCode = " +
                     dbl.sqlString(rs.getString("FOrderCode"));

               conn.setAutoCommit(false);
               bTrans = true;
               dbl.executeSql(strDel);
               conn.commit();
               bTrans = false;
               conn.setAutoCommit(true);

            }
         }
      }
      catch (Exception e) {
         throw new YssException(e.getMessage());
      }
      finally {
         dbl.closeResultSetFinal(rs);
         dbl.endTransFinal(conn, bTrans);
      }

   }

   protected void insertAvgFundInTable(HashMap hmData, String sTableName) throws
         YssException {
      String strSql = "";
      boolean bTrans = false;
      Connection conn = dbl.loadConnection();
      PreparedStatement pstmt = null;
      YssKeyDouble keyDouble = null;
      try {
         Iterator iter = hmData.values().iterator();
         conn.setAutoCommit(false);
         bTrans = true;
         strSql = "insert into " +
               pub.yssGetTableName(sTableName + pub.getUserCode()) +
               " (FPortCode,FMoney) values (?,?)";
         pstmt = conn.prepareStatement(strSql);
         while (iter.hasNext()) {
            keyDouble = (YssKeyDouble) iter.next();
            pstmt.setString(1, keyDouble.getStrPortCode());
            pstmt.setDouble(2, keyDouble.getDAvgCostValue());
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
         dbl.endTransFinal(conn, bTrans);
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

}
