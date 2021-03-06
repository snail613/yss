package com.yss.main.operdeal;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.pojo.sys.*;
import com.yss.util.*;

public class BaseCashBookOper
    extends BaseBean implements IAccBookOper {
    private HashMap hmFieldRela;
    private HashMap hmFieldIndRela;
    private HashMap hmAliasRela;
    private HashMap hmTableRela;
    private HashMap hmTableTradeRela;
    private HashMap hmStorageFieldRela;
    private String sBookDefine;
    private String invmgrField = "";
    private String catField = "";
    public BaseCashBookOper() {
    }

    public void setYssPub(YssPub pub) {
        super.setYssPub(pub);
        this.setRelaMap();
    }

    /**
     * setBookClassTable
     *
     * @param sBookDefine String
     * @return String
     */
    public void setBookClassTable(String sBookDefine, int iShowType) throws
        YssException {
        String[] sBookDefineAry = null;
        createTmpTable();
        this.sBookDefine = sBookDefine;
        sBookDefineAry = sBookDefine.split(";");
        setClearedClassTable(sBookDefineAry);
//      setNoClearClassTable(sBookDefineAry);
        /*
              if (iShowType == YssOperCons.Yss_TZXSLX_ALL) {
                 setClearedClassTable(sBookDefineAry);
                 setNoClearClassTable(sBookDefineAry);
              }
              else if (iShowType == YssOperCons.Yss_TZXSLX_CLERARED) {
                 setClearedClassTable(sBookDefineAry);
              }
              else if (iShowType == YssOperCons.Yss_TZXSLX_NOCLERAR) {
                 setNoClearClassTable(sBookDefineAry);
              }
         */
    }

    protected void setNoClearClassTable(String[] sBookDefineAry) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        YssTreeNode tNode = null;
        String sOrderIndex = "";
        String sFieldCode = "";
        String sFieldName = "";
        HashMap hmData = new HashMap();
        try {
            strSql +=
                " select a.FCashAccCode,b.FBankCode,a.FPortCode,b.FCuryCode," +
                "b.FCashAccName,b.FCuryName,e.FPortName,b.FBankName, a.FInvmgrCode, d.FInvmgrName,c.FCatCode, c.FCatName from " +
                //---------------------------------------------------------------
                " (select FSecurityCode,FPortCode,FInvMgrCode,FCashAccCode from " +
                pub.yssGetTableName("Tb_Data_SubTrade") + ") a left join " +
                //---------------------------------------------------------------
                
          
                " (select b2.*,b3.FBankName,b4.FCuryName from (select FCashAccCode, FCashAccName, FCuryCode, FBankCode from " +
                pub.yssGetTableName("tb_para_cashaccount") +
                " where FCheckState = 1 ) b2  left join" +
               
                //end by lidaolong
                //---------------------------------------------------------------
                " (select FBankCode,FBankName from " +
                pub.yssGetTableName("tb_para_bank") +
                " where FCheckState = 1) b3 on b2.Fbankcode = b3.FBankCode left join " +
                " (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) b4 on b2.Fcurycode = b4.FCuryCode) b on a.FCashAccCode = b.FCashAccCode left join " +
                //---------------------------------------------------------------
                " (select c2.*,c3.FCatName from (select Fsecuritycode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by Fsecuritycode) c1 join " +
                " (select Fsecuritycode, FCatCode, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") c2 on c1.Fsecuritycode = c2.Fsecuritycode and c1.FStartDate = c2.FStartDate" +
                " left join (select FCatCode, FCatName from tb_base_category where FCheckState = 1) c3 on c2.Fcatcode = c3.FCatCode) c " +
                " on a.Fsecuritycode = c.Fsecuritycode left join " +
                //---------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
           
                " (select FInvmgrCode, FInvmgrName from " +
                pub.yssGetTableName("Tb_Para_Investmanager") +
                " where FCheckState = 1 ) d " +
                " on a.FInvmgrCode = d.FInvmgrCode left join " +
                
                //end by lidaolong
                //---------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
           
                " (select FPortCode, FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) e on a.FPortCode = e.FPortCode " +
             
                
                //end bylidaolong
                //---------------------------------------------------------------
                " where a.FInvmgrCode <> ' ' and a.FPortCode <> ' ' and a.FCashAccCode <> ' '";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sOrderIndex = "001";
                for (int i = 0; i < sBookDefineAry.length; i++) {
                    tNode = new YssTreeNode();
                    sFieldCode = (String) hmFieldIndRela.get(sBookDefineAry[i]) +
                        "Code";
                    sFieldName = (String) hmFieldIndRela.get(sBookDefineAry[i]) +
                        "Name";
                    tNode.setCode(sBookDefineAry[i] + "\f" + builderCode(rs, i + 1));
                    if (i == 0) {
                        tNode.setParentCode("[root]");
                    } else {
                        tNode.setParentCode(sBookDefineAry[i - 1] + "\f" +
                                            builderCode(rs, i));
                    }
                    tNode.setOrderCode(sOrderIndex);
                    tNode.setName(rs.getString(sFieldName));
                    if (!hmData.containsKey(tNode.getCode())) {
                        hmData.put(tNode.getCode(), tNode);
                    }
                    sOrderIndex += "001";
                }
            }
            insertTempTable(hmData);

        } catch (Exception e) {

        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    protected void setClearedClassTable(String[] sBookDefineAry) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        String sAry[] = null;
        HashMap hmData = new HashMap();
        String sFieldCode = "";
        String sFieldName = "";
        String sOrderIndex = "";
        YssTreeNode tNode = null;
        String endString="";
        boolean analy1; //判断是否需要用分析代码；杨
        boolean analy2;

        try {

            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");

            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Cash);
            //add by zhouxiang MS01544 现金台帐和证券台帐中，选择一条信息后点击展开按钮报错 
            if(sAry[0].indexOf("FCatCode")>-1&& sAry[0].indexOf("FCatName")>-1)
            {endString="";}
            else
            {endString=",'' as FCatCode,'' as FCatName";}
            //end by zhoxiang MS01544  现金台帐和证券台帐中，选择一条信息后点击展开按钮报错 
//         sInvmgrField = this.getSettingOper().getStorageAnalysisField(
//               YssOperCons.YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_InvMgr);
            strSql =
                "select a.FCashAccCode,a.FBankCode,a.FPortCode,a.FCuryCode," +
                " b.FCashAccName,c.FCuryName,d.FPortName,e.FBankName " +
//               sAry[0] + " from ( " +
                (sAry[0].length() == 0 ? ",'' as FInvMgrCode,'' as FInvMgrName,'' as FCatCode,'' as FCatName " : sAry[0]+endString) + " from ( " + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
                " select aa.FCashAccCode,ab.FBankCode,aa.FPortCode,aa.FCuryCode," +
                " aa.FAnalysisCode1,aa.FAnalysisCode2,aa.FAnalysisCode3 from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                //-------------------------------------------------------------------------------------------
                " aa left join (select FCashAccCode,FBankCode from " +
                pub.yssGetTableName("tb_para_cashaccount") +
                ") ab on aa.Fcashacccode = ab.FCashAccCode" +
                " group by aa.FCashAccCode,ab.FBankCode,aa.FPortCode,aa.FCuryCode,aa.FAnalysisCode1,aa.FAnalysisCode2,aa.FAnalysisCode3) " +
                //--------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
       
                " a left join (select FCashAccCode,FCashAccName from " +              
                pub.yssGetTableName("tb_para_cashaccount") +
                " where FCheckState = 1) b on a.FCashAccCode = b.FCashAccCode" +
               
                //end by lidaolong 
                //--------------------------------------------------------------------------------------------
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " ) c on a.FCuryCode = c.FCuryCode" +
                //--------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
               
                " left join (select FPortCode,FPortName from " +
             
                pub.yssGetTableName("tb_para_Portfolio") +
                " where FCheckState = 1) d on a.FPortCode = d.FPortCode" +
             
                
                //end by lidaolong
                //---------------------------------------------------------------------------------------------
                " left join (select FBankCode,FBankName from " +
                pub.yssGetTableName("tb_para_bank") +
                " ) e on a.FBankCode = e.FBankCode" +
                sAry[1];

            strSql += " union ";

            strSql +=
                " select a.FCashAccCode,b.FBankCode,a.FPortCode,b.FCuryCode," +
                "b.FCashAccName,b.FCuryName,e.FPortName,b.FBankName" +
//               (analy1?", a.FInvmgrCode, d.FInvmgrName":" ") +
                (analy1 ? ", a.FInvmgrCode, d.FInvmgrName" : ",'' as FInvmgrCode,'' as FInvmgrName ") + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
//               (analy2?",c.FCatCode, c.FCatName ":" ") +
                (analy2 ? ",c.FCatCode, c.FCatName " : ",'' as FCatCode,'' as FCatName ") + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
                " from " +
                //---------------------------------------------------------------
                " (select a1.FSecurityCode,a2.FPortCode," +
                (analy1 ? ("a2." + invmgrField + " as FInvmgrCode,") : " ") +
                "a2.FCashAccCode " +
                " from " + pub.yssGetTableName("Tb_Cash_transfer") + " a1 join (select * from " +
                pub.yssGetTableName("Tb_Cash_Subtransfer") +
                "  where FCheckState = 1) a2 on a1.fnum = a2.fnum where a1.FCheckState = 1" +
                " union select FSecurityCode,FPortCode" +
                (analy1 ? ",FInvMgrCode" : " ") +
                ",FCashAccCode from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState = 1) a left join " +
                //---------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    
                " (select b2.*,b3.FBankName,b4.FCuryName from (select FCashAccCode, FCashAccName, FCuryCode, FBankCode from " +
                pub.yssGetTableName("tb_para_cashaccount") +
                " where  FCheckState = 1 ) b2  left join" +
             
                
                //end by lidaolong
                //---------------------------------------------------------------
                " (select FBankCode,FBankName from " +
                pub.yssGetTableName("tb_para_bank") +
                " where FCheckState = 1) b3 on b2.Fbankcode = b3.FBankCode left join " +
                " (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) b4 on b2.Fcurycode = b4.FCuryCode) b on a.FCashAccCode = b.FCashAccCode left join " +
                //---------------------------------------------------------------
                " (select c2.*,c3.FCatName from (select Fsecuritycode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by Fsecuritycode) c1 join " +
                " (select Fsecuritycode, FCatCode, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") c2 on c1.Fsecuritycode = c2.Fsecuritycode and c1.FStartDate = c2.FStartDate" +
                " left join (select FCatCode, FCatName from tb_base_category where FCheckState = 1) c3 on c2.Fcatcode = c3.FCatCode) c " +
                " on a.Fsecuritycode = c.Fsecuritycode " +
                //---------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
  
                
                (analy1 ? (" left join (select FInvmgrCode, FInvmgrName from " +
                        pub.yssGetTableName("Tb_Para_Investmanager") +
                        " where  FCheckState = 1 ) d " +
                        " on a.FInvmgrCode = d.FInvmgrCode ") : " ") +
           
                        
                        
                 //end by lidaolong          
                 //---------------------------------------------------------------
               // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                           
        
                   " left join (select FPortCode, FPortName from " +
                  pub.yssGetTableName("Tb_Para_Portfolio") +
                   " where  FCheckState = 1 ) e on a.FPortCode = e.FPortCode " +
                          
                //end by lidaolong
                //---------------------------------------------------------------
                " where 1=1 " +
                (analy1 ? " and a.FInvmgrCode <> ' ' " : " ") +
                " and a.FPortCode <> ' ' and a.FCashAccCode <> ' '";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sOrderIndex = "001";
                for (int i = 0; i < sBookDefineAry.length; i++) {
                    tNode = new YssTreeNode();
                    sFieldCode = (String) hmFieldIndRela.get(sBookDefineAry[i]) +
                        "Code";
                    sFieldName = (String) hmFieldIndRela.get(sBookDefineAry[i]) +
                        "Name";
                    tNode.setCode(sBookDefineAry[i] + "\f" + builderCode(rs, i + 1));
                    if (i == 0) {
                        tNode.setParentCode("[root]");
                    } else {
                        tNode.setParentCode(sBookDefineAry[i - 1] + "\f" +
                                            builderCode(rs, i));
                    }
                    tNode.setOrderCode(sOrderIndex);
                    tNode.setName(rs.getString(sFieldName));
                    if (!hmData.containsKey(tNode.getCode())) {
                        hmData.put(tNode.getCode(), tNode);
                    }
                    sOrderIndex += "001";
                }
            }
            insertTempTable(hmData);
        } catch (Exception e) {
            throw new YssException("获取台帐类型表出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private String builderCode(ResultSet rs, int idx) throws YssException {
        String[] sBookDefineAry = null;
        String sField = "";
        StringBuffer buf = new StringBuffer();
        try {
            sBookDefineAry = sBookDefine.split(";");
            for (int i = 0; i < idx; i++) {
                sField = (String) hmFieldIndRela.get(sBookDefineAry[i]) + "Code";
                buf.append(rs.getString(sField) + "").append("\f");
            }
            if (buf.length() > 0) {
                buf.setLength(buf.length() - 1);
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException();
        }
    }

    public void insertTempTable(HashMap hmData) throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        PreparedStatement pstmt = null;
        YssTreeNode tNode = null;
        try {
            Iterator iter = hmData.values().iterator();
            conn.setAutoCommit(false);
            strSql = "insert into " +
                pub.yssGetTableName("tb_Temp_BookCls_" + pub.getUserCode()) +
                " (FCode,FName,FParentCode,FOrderCode) values (?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);
            while (iter.hasNext()) {
                tNode = (YssTreeNode) iter.next();
                pstmt.setString(1, tNode.getCode());
                pstmt.setString(2, tNode.getName());
                pstmt.setString(3, tNode.getParentCode());
                pstmt.setString(4, tNode.getOrderCode());
                pstmt.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {

        }
    }

    protected void setRelaMap() {

        try {
            invmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.
                YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_InvMgr);
            catField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.
                YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_CatType);

//      invmgrField = "FAnalysisCode1";
        } catch (YssException ex1) {
        }

        hmFieldIndRela = new HashMap();
        hmFieldIndRela.put("InvMgr", "FInvMgr");
        hmFieldIndRela.put("CatType", "FCat");
        hmFieldIndRela.put("Port", "FPort");
        hmFieldIndRela.put("Acc", "FCashAcc");
        hmFieldIndRela.put("Bank", "FBank");
        hmFieldIndRela.put("Cury", "FCury");

        /*
              hmAliasRela = new HashMap();
              hmAliasRela.put("InvMgr",
         "FOutInvMgrCode as FOutCode,FInInvMgrCode as FInCode");
              hmAliasRela.put("CatType",
         "FOutCatTypeCode as FOutCode,FInCatTypeCode as FInCode");
         hmAliasRela.put("Port", "FOutPortCode as FOutCode,FInPortCode as FInCode");
              hmAliasRela.put("Acc",
         "FOutCashAccCode as FOutCode,FInCashAccCode as FInCode");
              hmAliasRela.put("Bank",
         "FOutBankCode as FOutCode, FInBankCode as FInCode");
              hmAliasRela.put("Cury",
         "FOutCuryCode as FOutCode, FInCuryCode as FInCode");
         */
        hmAliasRela = new HashMap();
        hmAliasRela.put("InvMgr", invmgrField + " as FCode");
        hmAliasRela.put("CatType", catField + " as FCode");
        hmAliasRela.put("Port", "FPortCode as FCode");
        hmAliasRela.put("Acc", "FCashAccCode as FCode");
        hmAliasRela.put("Bank", "FBankCode as FCode");
        hmAliasRela.put("Cury", "FCuryCode as FCode");

        hmTableRela = new HashMap();
        hmTableRela.put("InvMgr",
//                      "(select FInvMgrCode as FOutCode,FInvMgrName as FOutName from " +
//                      pub.yssGetTableName("tb_para_investmanager") +
//                      " where FCheckState = 1) y on x.FOutcode = y.FOutCode" +
                        " (select FInvMgrCode as FCode,FInvMgrName as FName from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FCheckState = 1) z on x.Fcode = z.FCode" +
                        " left join (select FInvMgrCode as FAccCode,FInvMgrName as FAccName from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FCheckState = 1) w on cs.FAccCode = w.FAccCode");
        hmTableRela.put("CatType",
//                      "(select FCatCode as FOutCode,FCatName as FOutName from Tb_Base_Category" +
//                      " where FCheckState = 1) y on x.FOutcode = y.FOutCode" +
                        " (select FCatCode as FCode,FCatName as FName from Tb_Base_Category" +
                        " where FCheckState = 1) z on x.Fcode = z.FCode" +
                        " left join (select FCatCode as FAccCode,FCatName as FAccName from Tb_Base_Category" +
                        " where FCheckState = 1) w on cs.FAccCode = w.FAccCode");
        hmTableRela.put("Port",
//                      "(select FPortCode as FOutCode,FPortName as FOutName from " +
//                      pub.yssGetTableName("Tb_Para_Portfolio") +
//                      " where FCheckState = 1) y on x.FOutCode = y.FOutCode" +
                        " (select FPortCode as FCode,FPortName as FName from " +
                        pub.yssGetTableName("Tb_Para_Portfolio") +
                        " where FCheckState = 1) z on x.FCode = z.FCode" +
                        " left join(select FPortCode as FAccCode,FPortName as FAccName from " +
                        pub.yssGetTableName("Tb_Para_Portfolio") +
                        " where FCheckState = 1) w on cs.FAccCode = w.FAccCode");
        hmTableRela.put("Acc",
//                      "(select FCashAccCode as FOutCode,FCashAccName as FOutName from " +
//                      pub.yssGetTableName("tb_para_cashaccount") +
//                      " where FCheckState = 1) y on x.FOutCode = y.FOutCode " +
                        " (select FCashAccCode as FCode,FCashAccName as FName from " +
                        pub.yssGetTableName("tb_para_cashaccount") +
                        " where FCheckState = 1) z on x.FCode = z.FCode" +
                        " left join (select FCashAccCode as FAccCode,FCashAccName as FAccName from " +
                        pub.yssGetTableName("tb_para_cashaccount") +
                        " where FCheckState = 1) w on cs.FAccCode = w.FAccCode");
        hmTableRela.put("Bank",
//                      "(select FBankCode as FOutCode,FBankName as FOutName from " +
//                      pub.yssGetTableName("Tb_Para_Bank") +
//                     " where FCheckState = 1) y on x.FOutCode = y.FOutCode" +
                        " (select FBankCode as FCode,FBankName as FName from " +
                        pub.yssGetTableName("Tb_Para_Bank") +
                        " where FCheckState = 1) z on x.FCode = z.FCode" +
                        " left join (select FBankCode as FAccCode,FBankName as FAccName from " +
                        pub.yssGetTableName("Tb_Para_Bank") +
                        " where FCheckState = 1) w on cs.FAccCode = w.FAccCode");
        hmTableRela.put("Cury",
//                      "(select FCuryCode as FOutCode,FCuryName as FOutName from " +
//                      pub.yssGetTableName("Tb_Para_Currency") +
//                      " where FCheckState = 1) y on x.FOutCode = y.FOutCode" +
                        " (select FCuryCode as FCode,FCuryName as FName from " +
                        pub.yssGetTableName("Tb_Para_Currency") +
                        " where FCheckState = 1) z on x.FCode = z.FCode" +
                        " left join(select FCuryCode as FAccCode,FCuryName as FAccName from " +
                        pub.yssGetTableName("Tb_Para_Currency") +
                        " where FCheckState = 1) w on cs.FAccCode = w.FAccCode");

        hmTableTradeRela = new HashMap();
        hmTableTradeRela.put("InvMgr",
                             "(select FInvMgrCode,FInvMgrName as FAccName from " +
                             pub.yssGetTableName("tb_para_investmanager") +
                             " where FCheckState = 1) z on x.FAccCode = z.FInvMgrCode");
        hmTableTradeRela.put("CatType",
                             "(select FCatCode,FCatName as FAcName from Tb_Base_Category" +
                             " where FCheckState = 1) z on x.FAccCode = z.FCatCode");
        hmTableTradeRela.put("Port",
                             "(select FPortCode,FPortName as FAccName from " +
                             pub.yssGetTableName("Tb_Para_Portfolio") +
                             " where FCheckState = 1) z on x.FAccCode = z.FPortCode");
        hmTableTradeRela.put("Acc",
                             "(select FCashAccCode,FCashAccName as FAccName from " +
                             pub.yssGetTableName("tb_para_cashaccount") +
                             " where FCheckState = 1) z on x.FAccCode = z.FCashAccCode");
        hmTableTradeRela.put("Bank",
                             "(select FBankCode,FBankName as FAccName from " +
                             pub.yssGetTableName("Tb_Para_Bank") +
                             " where FCheckState = 1) z on x.FAccCode = z.FBankCode");
        hmTableTradeRela.put("Cury",
                             "(select FCuryCode,FCuryName as FAccName from " +
                             pub.yssGetTableName("Tb_Para_Currency") +
                             " where FCheckState = 1) z on x.FAccCode = z.FCuryCode");

        hmFieldRela = new HashMap();
        hmFieldRela.put("InvMgr", invmgrField);
        hmFieldRela.put("Port", "FPortCode");
        hmFieldRela.put("Acc", "FCashAccCode");
        hmFieldRela.put("Cury", "FCuryCode");
        hmFieldRela.put("Bank", "FBankCode");
        hmFieldRela.put("CatType", catField);

        hmStorageFieldRela = new HashMap();
        try {
            hmStorageFieldRela.put("InvMgr", invmgrField);
//      hmStorageFieldRela.put("CatType", "FAnalysisCode2");
            hmStorageFieldRela.put("CatType",
                                   this.getSettingOper().
                                   getStorageAnalysisField(YssOperCons.
                YSS_KCLX_Cash,
                YssOperCons.YSS_KCPZ_CatType));

            hmStorageFieldRela.put("Acc", "FCashAccCode");
            hmStorageFieldRela.put("Port", "FPortCode");
            hmStorageFieldRela.put("Bank", "FBankCode");
            hmStorageFieldRela.put("Cury", "FCuryCode");
        }

        catch (YssException ex) {
        }
    }

    protected void setTransferRela(String sBookDefine, String sBookLink) {
        String[] sBookDefineAry = null;
        String[] sBookLinkAry = null;
        sBookDefineAry = sBookDefine.split(";");
        sBookLinkAry = sBookLink.split("\f");

    }

    protected void createTmpTable() throws YssException {
        String strSql = "";
        try {
            if (dbl.yssTableExist(pub.yssGetTableName("tb_Temp_BookCls_" +
                pub.getUserCode()))) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                               pub.yssGetTableName("tb_Temp_BookCls_" +
                    pub.getUserCode())));
                /**end*/
            }

            strSql = "create table " +
                pub.yssGetTableName("tb_Temp_BookCls_" + pub.getUserCode()) +
                " (FCode varchar2(200)," +
//               " FName varchar2(50)," +
                " FName varchar2(200)," + //当Name的值过大是，插入数据就会发生错误。导致显示的数据有误。sj edit
                " FParentCode varchar2(200)," +
//               " FOrderCode varchar2(50))";
                " FOrderCode varchar2(200))"; //当值过大是，插入数据就会发生错误。导致显示的数据有误。sj edit
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("");
        }
    }

    /**
     * getBookWhereSql
     *
     * @param sBookDefine String
     * @param sBookLink String
     * @return String
     */
    public String getBookSql(String sBookDefine, String sBookLink,
                             java.util.Date dBeginDate,
                             java.util.Date dEndDate, int iShowType, String sCheckItems) throws
        YssException {
        String[] sBookDefineAry = null;
        String[] sBookLinkAry = null;
        String strSql = "";
        try {
            sBookDefineAry = sBookDefine.split(";");
            sBookLinkAry = sBookLink.split("\f");
            if (sBookLinkAry.length - 1 < sBookDefineAry.length) {
                strSql = getNoDetailBookSql(sBookDefineAry, sBookLinkAry,
                                            dBeginDate, dEndDate, iShowType, sCheckItems);
            } else if (sBookLinkAry.length - 1 == sBookDefineAry.length) {
                strSql = getDetailBookSql(sBookDefineAry, sBookLinkAry,
                                          dBeginDate, dEndDate, iShowType);
            }

            return strSql;
        } catch (Exception e) {
            throw new YssException("获取现金台帐查询语句出错");
        }
    }

    protected String getDetailBookSql(String[] sBookDefineAry,
                                      String[] sBookLinkAry,
                                      java.util.Date dBeginDate,
                                      java.util.Date dEndDate, int iShowType) {

        String sResult = "";
        String sWhereSql = "";
        String sFieldCode = null;

        if (iShowType == YssOperCons.Yss_TZXSLX_CLERARED) {
            sResult = getDetailClearedBookSql(sBookDefineAry, sBookLinkAry,
                                              dBeginDate, dEndDate);
        } else if (iShowType == YssOperCons.Yss_TZXSLX_NOCLERAR) {
            sResult = getDetailNoClearedBookSql(sBookDefineAry, sBookLinkAry,
                                                dBeginDate, dEndDate);
        } else if (iShowType == YssOperCons.Yss_TZXSLX_ALL) {
            sResult = getDetailClearedBookSql(sBookDefineAry, sBookLinkAry,
                                              dBeginDate, dEndDate) +
                " union " +
                getDetailNoClearedBookSql(sBookDefineAry, sBookLinkAry,
                                          dBeginDate, dEndDate);
        }
        return sResult;

    }

    protected String getDetailNoClearedBookSql(String[] sBookDefineAry,
                                               String[] sBookLinkAry,
                                               java.util.Date dBeginDate,
                                               java.util.Date dEndDate) {
        String strSql = "";
        String sFieldCode = "";
        String sWhereSql = " where ";
        for (int i = 1; i < sBookLinkAry.length; i++) {
            sFieldCode = ( (String) hmFieldIndRela.get(sBookDefineAry[i - 1])) +
                "Code";
            sWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) +
                " and ";
        }

        sWhereSql += " (FBargainDate between " +
            dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) +
            ")";

        strSql =
            " select FCuryCode,a.FCashInd as FInOut,a.FCashAccCode,ca.FCashAccName,ca.FAccTypeName,ca.FSubAccTypeName," +
            " FMoney, (FMoney*FBaseCuryRate) as FBaseMoney,FBankCode,FBargainDate as FTransferDate,a.FNum as FSubNum, " +
            " '0' as FIsCleared" +
            (invmgrField.length() != 0 ? ", FInvMgrCode" : " ") +
            (catField.length() != 0 ? ",FCatCode" : " ") +
//            " sum(FOutMoney) as FOutMoney,sum(FInMoney) as FInMoney," +
//            " sum(FOutMoney*FoutBaseCuryRate) as FBaseOutMoney,sum(FInMoney*FInBaseCuryRate) as FBaseInMoney" +
            " from (select FSecurityCode,FPortCode,FInvMgrCode,FCashAccCode," +
            " FTotalCost as FMoney,FBaseCuryRate,a2.FCashInd,FBargainDate, a1.FCheckState,FNum from " +
            pub.yssGetTableName("Tb_Data_SubTrade") +
            " a1 left join (select * from Tb_Base_TradeType where FCheckState = 1) a2 " +
            " on a1.FTradeTypeCode=a2.FTradeTypeCode where FSettleState = 0 and a1.FCheckState = 1) a left join" +
            //------------------------------------------------------------------
            " (select FCashAccCode as FCashAcc,FCashAccName,FBankCode,FCuryCode,ca2.FAccTypeName,ca2.FSubAccTypeName,ca1.FSubAccType " +
            " from " + pub.yssGetTableName("tb_para_cashaccount") +
            " ca1 left join" +
            " (select ca22.FAccTypeCode,ca22.FAccTypeName,FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType ca21 left join " +
            " (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) ca22 on ca21.FAccTypeCode = ca22.FAccTypeCode) ca2 " +
            " on ca1.facctype = ca2.FAccTypeCode  and ca1.FSubAccType = ca2.FSubAccTypeCode where FCheckState = 1) ca on a.FCashAccCode = ca.FCashAcc left join" +
            //------------------------------------------------------------------
//            " (select FCashAccCode,FCashAccName,FBankCode as FInBankCode,FCuryCode as FInCuryCode,ca2.FAccTypeName,ca2.FSubAccTypeName,ca1.FSubAccType " +
//            " from " + pub.yssGetTableName("tb_para_cashaccount") +
//           " ca1 left join" +
//            " (select ca22.FAccTypeCode,ca22.FAccTypeName,FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType ca21 left join" +
//            " (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) ca22 on ca21.FAccTypeCode = ca22.FAccTypeCode) ca2 " +
//            " on ca1.facctype = ca2.FAccTypeCode  and ca1.FSubAccType = ca2.FSubAccTypeCode where FCheckState = 1) caIn on a.FInCashAccCode = caIn.FCashAccCode left join" +
            //------------------------------------------------------------------
            " (select sec1.FCatCode, FSecurityCode from " +
            pub.yssGetTableName("Tb_Para_Security") + " sec1 left join " +
            " (select FCatCode from Tb_Base_Category where FCheckState = 1) sec2 on sec1.fcatcode = sec2.FCatCode" +
            " where FCheckState = 1) sec on a.Fsecuritycode = sec.FSecurityCode" +
            sWhereSql +
            " and a.FCheckState = 1"; // wdy modify 20070831
        return strSql;

    }

    protected String getDetailClearedBookSql(String[] sBookDefineAry,
                                             String[] sBookLinkAry,
                                             java.util.Date dBeginDate,
                                             java.util.Date dEndDate) {

        String strSql = "";
        String sFieldCode = "";
        String sWhereSql = " where ";
        for (int i = 1; i < sBookLinkAry.length; i++) {
            sFieldCode = ( (String) hmFieldRela.get(sBookDefineAry[i - 1]));
            sWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) +
                " and ";
        }
        sWhereSql += " (FTransferDate between " +
            dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) +
            ")";
        strSql =
            " select FCuryCode,FInOut,a.FCashAccCode,ca.FCashAccName,ca.FAccTypeName,ca.FSubAccTypeName," +
            " FMoney, (FMoney*FBaseCuryRate) as FBaseMoney,FBankCode,FTransferDate,(a.FNum " +
            dbl.sqlJN() + " a.FSubNum) as FSubNum, '1' as FIsCleared" +
            (invmgrField.length() != 0 ? ("," + invmgrField) : " ") +
            (catField.length() != 0 ? ("," + catField) : " ") +
            " from (select a1.FSecurityCode,a2.FPortCode" +
            (invmgrField.length() != 0 ? (",a2." + invmgrField) : " ") +
            (catField.length() != 0 ? (",a2." + catField) : " ") +
            ",a2.FCashAccCode," +
            " a2.FMoney,a2.FBaseCuryRate,a2.FInOut,a1.FTransferDate,a2.FCheckState,a2.FNum,a2.FSubNum from " +
            pub.yssGetTableName("Tb_Cash_Transfer") +
            " a1 join (select * from " +
            pub.yssGetTableName("Tb_Cash_Subtransfer") +
            ") a2 on a1.fnum = a2.fnum where a1.FCheckState = 1) a left join" +
            //------------------------------------------------------------------
            " (select FCashAccCode as FCashAcc,FCashAccName,FBankCode,FCuryCode,ca2.FAccTypeName,ca2.FSubAccTypeName,ca1.FSubAccType " +
            " from " + pub.yssGetTableName("tb_para_cashaccount") +
            " ca1 left join" +
            " (select ca22.FAccTypeCode,ca22.FAccTypeName,FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType ca21 left join " +
            " (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) ca22 on ca21.FAccTypeCode = ca22.FAccTypeCode) ca2 " +
            " on ca1.facctype = ca2.FAccTypeCode  and ca1.FSubAccType = ca2.FSubAccTypeCode where FCheckState = 1) ca on a.FCashAccCode = ca.FCashAcc left join" +
            //------------------------------------------------------------------
//            " (select FCashAccCode,FCashAccName,FBankCode as FInBankCode,FCuryCode as FInCuryCode,ca2.FAccTypeName,ca2.FSubAccTypeName,ca1.FSubAccType " +
//            " from " + pub.yssGetTableName("tb_para_cashaccount") +
//           " ca1 left join" +
//            " (select ca22.FAccTypeCode,ca22.FAccTypeName,FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType ca21 left join" +
//            " (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) ca22 on ca21.FAccTypeCode = ca22.FAccTypeCode) ca2 " +
//            " on ca1.facctype = ca2.FAccTypeCode  and ca1.FSubAccType = ca2.FSubAccTypeCode where FCheckState = 1) caIn on a.FInCashAccCode = caIn.FCashAccCode left join" +
            //------------------------------------------------------------------
            " (select sec1.FCatCode, FSecurityCode from " +
            pub.yssGetTableName("Tb_Para_Security") + " sec1 left join " +
            " (select FCatCode from Tb_Base_Category where FCheckState = 1 " +
            " ) sec2 on sec1.fcatcode = sec2.FCatCode where FCheckState = 1) sec on a.Fsecuritycode = sec.FSecurityCode" +
            sWhereSql + " and a.FCheckState = 1";
        return strSql;

    }

    protected String getNoDetailBookSql(String[] sBookDefineAry,
                                        String[] sBookLinkAry,
                                        java.util.Date dBeginDate,
                                        java.util.Date dEndDate, int iShowType, String sCheckItems) throws
        YssException {
        String sResult = "";
        if (iShowType == YssOperCons.Yss_TZXSLX_CLERARED) {
            sResult = getNoDetailClearedSql(sBookDefineAry, sBookLinkAry,
                                            dBeginDate, dEndDate, sCheckItems);
        } else if (iShowType == YssOperCons.Yss_TZXSLX_NOCLERAR) {
            sResult = getNoDetailNoClearedSql(sBookDefineAry, sBookLinkAry,
                                              dBeginDate, dEndDate, sCheckItems);
        } else if (iShowType == YssOperCons.Yss_TZXSLX_ALL) {
            sResult = getNoDetailClearedSql(sBookDefineAry, sBookLinkAry,
                                            dBeginDate, dEndDate, sCheckItems) + " union " +
                getNoDetailNoClearedSql(sBookDefineAry, sBookLinkAry,
                                        dBeginDate, dEndDate, sCheckItems);
        }
        return sResult;
    }

    protected String getNoDetailNoClearedSql(String[] sBookDefineAry,
                                             String[] sBookLinkAry,
                                             java.util.Date dBeginDate,
                                             java.util.Date dEndDate,
                                             String sCheckItems) {
        String sFieldCode = "";
        String sFieldCodes = "";
        String sFieldAlias = "";
        String sSecWhereSql = "";
        String sStorageWhereSql = "";
        String sAccWhereSql = "";
        String sInd = "";
        String strSql = "";
        String sStorageFieldCode = "";
        String sTmpDefine = "";
        String sTransWhereSql = "";
        String sCheckWhereStr = "";

        sSecWhereSql = " where ";
        sTransWhereSql = " where (FBargainDate between " +
            dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) +
            ") and ";
        sStorageWhereSql = " where ";
        sAccWhereSql = " where ";
        for (int i = 1; i < sBookLinkAry.length; i++) {
            sTmpDefine = (String) sBookDefineAry[i - 1];
            sFieldCode = ( (String) hmFieldRela.get(sTmpDefine));
//         sWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) + " and ";
//         sFieldCodes = (String) hmFieldIndRela.get(sTmpDefine) + "Code";
            if (sTmpDefine.equalsIgnoreCase("Bank") ||
                sTmpDefine.equalsIgnoreCase("Cury") ||
                sTmpDefine.equalsIgnoreCase("Acc")) {
                sAccWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            } else if (sTmpDefine.equalsIgnoreCase("InvMgr") ||
                       sTmpDefine.equalsIgnoreCase("Port")) {
                sFieldCode = ( (String) hmFieldIndRela.get(sTmpDefine)) + "Code";
                sTransWhereSql += sFieldCode + " = " +
                    dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            } else if (sTmpDefine.equalsIgnoreCase("CatType")) {
                sSecWhereSql += " FCatCode = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            }

            sStorageWhereSql +=
                (String) hmStorageFieldRela.get(sBookDefineAry[i - 1]) +
                " = " + dbl.sqlString(sBookLinkAry[i]) + " and ";
        }

        sInd = sBookDefineAry[sBookLinkAry.length - 1];
        sFieldCodes = (String) hmFieldIndRela.get(sInd) + "Code";
        sFieldAlias = (String) hmFieldIndRela.get(sInd) + "Code as FCode";

        sStorageFieldCode = (String) hmStorageFieldRela.get(sInd);

        if (sCheckItems.length() > 0) {
            sCheckWhereStr = this.operSql.sqlCodes(sCheckItems);

            if (sInd.equalsIgnoreCase("Bank") ||
                sInd.equalsIgnoreCase("Cury") ||
                sInd.equalsIgnoreCase("Acc")) {
                sAccWhereSql += sFieldCodes + " in (" +
                    sCheckWhereStr + ") and ";
            } else if (sInd.equalsIgnoreCase("InvMgr") ||
                       sInd.equalsIgnoreCase("Port")) {
                sTransWhereSql += sFieldCodes + " in (" +
                    sCheckWhereStr + ") and ";
            } else if (sInd.equalsIgnoreCase("CatType")) {
                sSecWhereSql += sFieldCodes + " in (" +
                    sCheckWhereStr + ") and ";
            }

            sStorageWhereSql += sStorageFieldCode + " in (" +
                sCheckWhereStr + ") and ";
        }

        strSql =
            "select w.FAccName,cs.*,'0' as FIsCleared,x.*,z.FName," +
            " cu1.FCsCuryName,cu2.FCuryName " +
            " from(" +
            " select " + sStorageFieldCode +
            " as FAccCode,sum(FAccBalance) as FBeginAccBalance," +
            " sum(FBaseCuryBal) as FBeginBaseAccBal,cs1.FCuryCode as FCsCuryCode,FBankAccount  as FCsBankAccount from " +
            pub.yssGetTableName("Tb_Stock_Cash") +
            " cs1 left join " +
            " (select FCashAccCode as FCashCode,FBankCode,FBankAccount from " +
            pub.yssGetTableName("tb_para_cashaccount") +
            " where FCheckState = 1) cs2 on cs1.FCashAccCode = cs2.FCashCode " +
            sStorageWhereSql + " cs1.FCheckState = 1 and " +
            getStorageDateSql(dBeginDate) + " group by " +
            sStorageFieldCode +
            ",cs1.FCuryCode,FBankAccount) cs full join" +
            //------------------------------------------------------------------
            " (select " + sFieldAlias + " ,FCuryCode,FBankAccount, 2 as FInOut," +
            " sum(FMoney*FInOut) as FMoney, sum(FMoney*FInOut*FBaseCuryRate) as FBaseMoney, " +
            " 0 as FUndueMoney,0 as FBaseUndueCuryRate " +
            " from (select FSecurityCode,FPortCode,FInvMgrCode,FCashAccCode,FTotalCost as FMoney," +
            " FBaseCuryRate, a2.FCashInd as FInOut ,FBargainDate,a1.FCheckState from " +
            pub.yssGetTableName("Tb_Data_SubTrade") +
            " a1 join (select * from Tb_Base_TradeType where FCheckState = 1" +
            ") a2 on a1.FTradeTypeCode = a2.FTradeTypeCode " + sTransWhereSql +
            " (FBargainDate between " + dbl.sqlDate(dBeginDate) +
            " and " + dbl.sqlDate(dEndDate) + ") and " +
            " FTotalCost <>0 and FSettleState = 0 and a1.FCheckState = 1) a join" +
            //------------------------------------------------------------------
            " (select FCashAccCode as FCashCode,FBankCode,FBankAccount,FCuryCode from " +
            pub.yssGetTableName("tb_para_cashaccount") + sAccWhereSql +
            " FCheckState = 1) ca on a.FCashAccCode = ca.FCashCode " +
            //------------------------------------------------------------------
//            " (select FCashAccCode,FBankCode as FInBankCode,FCuryCode as FInCuryCode from " +
//            pub.yssGetTableName("tb_para_cashaccount") + sAccWhereSql +
//            " 1=1) caIn on a.FInCashAccCode = caIn.FCashAccCode " +
            //------------------------------------------------------------------
            " join (select FSecurityCode,sec1.Fcatcode," +
            " sec2.FCatName as FOutCatName,sec2.FCatName as FInCatName from " +
            pub.yssGetTableName("tb_para_security") + " sec1 left join" +
            " (select FCatCode as FCatCode2,FCatName from Tb_Base_Category where FCheckState = 1" +
            " ) sec2 on sec1.fcatcode = sec2.FCatCode2 " +
            sSecWhereSql +
            " FCheckState=1) sec  on a.FSecurityCode = sec.FSecurityCode" +
            //------------------------------------------------------------------
            " group by " + sFieldCodes +
            ",FCuryCode,FBankAccount,2) x " +
            //------------------------------------------------------------------
            " on (x.FCode = cs.FAccCode" +
            " and x.FCuryCode = cs.FCsCuryCode ) left join" +
            //------------------------------------------------------------------
            (String) hmTableRela.get(sInd) +
            //------------------------------------------------------------------
            " left join (select FCuryCode,FCuryName as FCsCuryName from " + pub.yssGetTableName("tb_para_currency") +
            " where FCheckState = 1) cu1 on cs.FCsCuryCode = cu1.FCuryCode " +
            //------------------------------------------------------------------
            " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("tb_para_currency") +
            " where FCheckState = 1) cu2 on x.FCuryCode = cu2.FCuryCode "; ;

        return strSql;
    }

//获取非明细的己清算的数据
    protected String getNoDetailClearedSql(String[] sBookDefineAry,
                                           String[] sBookLinkAry,
                                           java.util.Date dBeginDate,
                                           java.util.Date dEndDate,
                                           String sCheckItems) {
        String sFieldCode = "";
        String sFieldCodes = "";
        String sFieldAlias = "";
        String sSecWhereSql = "";
        String sStorageWhereSql = "";
        String sAccWhereSql = "";
        String sInd = "";
        String strSql = "";
        String sStorageFieldCode = "";
        String sTmpDefine = "";
        String sTransWhereSql = "";
        String sCheckWhereStr = "";

        sSecWhereSql = " where ";
        sTransWhereSql = " where ";
        sStorageWhereSql = " where ";
        sAccWhereSql = " where ";
        for (int i = 1; i < sBookLinkAry.length; i++) {
            sTmpDefine = (String) sBookDefineAry[i - 1];
            sFieldCode = ( (String) hmFieldRela.get(sTmpDefine));
//         sWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) + " and ";
//         sFieldCodes = (String) hmFieldIndRela.get(sTmpDefine) + "Code";
            if (sTmpDefine.equalsIgnoreCase("Bank") ||
                sTmpDefine.equalsIgnoreCase("Cury") ||
                sTmpDefine.equalsIgnoreCase("Acc")) {
                sAccWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            } else if (sTmpDefine.equalsIgnoreCase("InvMgr") ||
                       sTmpDefine.equalsIgnoreCase("Port") ||
                       sTmpDefine.equalsIgnoreCase("CatType")) {
                sTransWhereSql += sFieldCode + " = " +
                    dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            }
//         else if (sTmpDefine.equalsIgnoreCase("CatType")) {
//            sSecWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) +
//                  " and ";
//         }

            sStorageWhereSql +=
                (String) hmStorageFieldRela.get(sBookDefineAry[i - 1]) +
                " = " + dbl.sqlString(sBookLinkAry[i]) + " and ";
        }

        sInd = sBookDefineAry[sBookLinkAry.length - 1];
        sFieldCodes = (String) hmFieldRela.get(sInd);
        sFieldAlias = (String) hmAliasRela.get(sInd);

        sStorageFieldCode = (String) hmStorageFieldRela.get(sInd);

        if (sCheckItems.length() > 0) {
            sCheckWhereStr = this.operSql.sqlCodes(sCheckItems);

            if (sInd.equalsIgnoreCase("Bank") ||
                sInd.equalsIgnoreCase("Cury") ||
                sInd.equalsIgnoreCase("Acc")) {
                sAccWhereSql += sFieldCodes + " in (" +
                    sCheckWhereStr + ") and ";
            } else if (sInd.equalsIgnoreCase("InvMgr") ||
                       sInd.equalsIgnoreCase("Port")) {
                sTransWhereSql += sFieldCodes + " in (" +
                    sCheckWhereStr + ") and ";
            } else if (sInd.equalsIgnoreCase("CatType")) {
                sSecWhereSql += sFieldCodes + " in (" +
                    sCheckWhereStr + ") and ";
            }

            sStorageWhereSql += sStorageFieldCode + " in (" +
                sCheckWhereStr + ") and ";
        }

        strSql =
            "select distinct w.FAccName,cs.*,'1' as FIsCleared,x.*,z.FName,cu1.FCsCuryName,cu2.FCuryName from(" +
            " select " + sStorageFieldCode +
            " as FAccCode,sum(FAccBalance) as FBeginAccBalance,sum(FBaseCuryBal) as FBeginBaseAccBal," +
            "cs1.FCuryCode as FCsCuryCode,FBankAccount as FCsBankAccount from " +
            pub.yssGetTableName("Tb_Stock_Cash") +
            " cs1 left join " +
            " (select FCashAccCode as FCashCode,FBankCode,FBankAccount from " +
            pub.yssGetTableName("tb_para_cashaccount") +
            ") cs2 on cs1.FCashAccCode = cs2.FCashCode " +
            sStorageWhereSql + getStorageDateSql(dBeginDate) + " group by " +
            sStorageFieldCode +
            ",cs1.FCuryCode,FBankAccount) cs full join" +
            //------------------------------------------------------------------
            " (select " + sFieldAlias + " ,FCuryCode,FBankAccount,FInOut," +
            " sum(FMoney) as FMoney, sum(FMoney*FBaseCuryRate) as FBaseMoney, " +
            " sum(a.FUndueMoney) as FUndueMoney,sum(a.FUndueMoney * a.FBaseUndueCuryRate) as FBaseUndueMoney " +

            " from (select a1.FSecurityCode,a2.FPortCode" +
            (invmgrField.length() != 0 ? (",a2." + invmgrField) : " ") +
            (catField.length() != 0 ? (",a2." + catField) : " ") +
            ",a2.FCashAccCode," +
            " a2.FMoney,a2.FBaseCuryRate,0 as FUndueMoney,0 as FBaseUndueCuryRate,a2.FInOut,a1.FTransferDate,a2.FCheckState from " +
            pub.yssGetTableName("Tb_Cash_Transfer") +
            " a1 join (select * from " +
            pub.yssGetTableName("Tb_Cash_Subtransfer") +
            " ) a2 on a1.fnum = a2.fnum " + sTransWhereSql + " (FTransferDate between " +
            dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) + ") and " +
            " a1.FCheckState=1" +
            //------------------------------------------------------------------
            " union select a1.FSecurityCode,a2.FPortCode" +
            (invmgrField.length() != 0 ? (",a2." + invmgrField) : " ") +
            (catField.length() != 0 ? (",a2." + catField) : " ") +
            " ,a2.FCashAccCode,0 as FMoney,0 as FBaseCuryRate,a2.FMoney as FUndueMoney" +
            " ,a2.FBaseCuryRate as FBaseUndueCuryRate,a2.FInOut,a1.FTransferDate,a2.FCheckState from " +
            pub.yssGetTableName("Tb_Cash_Transfer") +
            " a1 join (select * from " +
            pub.yssGetTableName("Tb_Cash_Subtransfer") +
            " ) a2 on a1.fnum = a2.fnum " + sTransWhereSql + " (FTransferDate > " +
            dbl.sqlDate(dEndDate) + " and FTransDate <= " + dbl.sqlDate(dEndDate) + ") and a1.FCheckState=1 ) a join" +
            //------------------------------------------------------------------
            " (select FCashAccCode as FCashCode,FBankCode,FBankAccount,FCuryCode from " +
            pub.yssGetTableName("tb_para_cashaccount") + sAccWhereSql +
            " 1=1) ca on a.FCashAccCode = ca.FCashCode " +
            //------------------------------------------------------------------
//            " (select FCashAccCode,FBankCode as FInBankCode,FCuryCode as FInCuryCode from " +
//            pub.yssGetTableName("tb_para_cashaccount") + sAccWhereSql +
//            " 1=1) caIn on a.FInCashAccCode = caIn.FCashAccCode " +
            //------------------------------------------------------------------
//            " join (select FSecurityCode,sec1.Fcatcode," +
//            " sec2.FCatName as FOutCatName,sec2.FCatName as FInCatName from " +
//            pub.yssGetTableName("tb_para_security") + " sec1 left join" +
//            " (select FCatCode as FCatCode2,FCatName from Tb_Base_Category where FCheckState = 1) sec2 on sec1.fcatcode = sec2.FCatCode2 " +
//            sSecWhereSql +
//            " FCheckState=1) sec  on a.FSecurityCode = sec.FSecurityCode" +
            //------------------------------------------------------------------
            " group by " + sFieldCodes +
            ",FCuryCode,FBankAccount,FInOut) x " +
            //------------------------------------------------------------------
            " on (x.FCode = cs.FAccCode" +
            " and x.FCuryCode = cs.FCsCuryCode ) left join" +
            //------------------------------------------------------------------
            (String) hmTableRela.get(sInd) +
            //------------------------------------------------------------------
            " left join (select FCuryCode,FCuryName as FCsCuryName from " + pub.yssGetTableName("tb_para_currency") +
            " where FCheckState = 1) cu1 on cs.FCsCuryCode = cu1.FCuryCode " +
            //------------------------------------------------------------------
            " left join (select FCuryCode,FCuryName from " + pub.yssGetTableName("tb_para_currency") +
            " where FCheckState = 1) cu2 on x.FCuryCode = cu2.FCuryCode ";
//            " order by x.FCode, x.FCuryCode, x.FCashInd desc";
        return strSql;
    }

    protected String getStorageDateSql(java.util.Date dDate) {
        String sResult = "";
        if (YssFun.formatDate(dDate, "MMdd").equalsIgnoreCase("0101")) {
            sResult = " FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00");
        } else if (YssFun.formatDate(dDate, "MMdd").equalsIgnoreCase("0102")) {
            sResult = " FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00"); ;
        } else {
            sResult = " FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1));
        }
        return sResult;
    }
}
