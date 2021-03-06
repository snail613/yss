package com.yss.main.operdeal;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.pojo.sys.*;
import com.yss.util.*;

public class BaseSecurityBookOper
    extends BaseBean implements IAccBookOper {
    private HashMap hmFieldRela;
    private HashMap hmFieldIndRela;
    private HashMap hmAliasRela;
    private HashMap hmTableRela;
    private HashMap hmTableTradeRela;
    private HashMap hmStorageFieldRela;
    private String sBookDefine;
    private String invmgrField = "";

    public BaseSecurityBookOper() {
    }

    public void setYssPub(YssPub pub) {
        super.setYssPub(pub);
        this.setRelaMap();
    }

    /**
     * setBookClassTable
     *
     * @param sBookDefine String
     * @param iShowType int
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

    /*protected void setNoClearClassTable(String[] sBookDefineAry) throws
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
                 " (select b2.*,b3.FBankName,b4.FCuryName from (select FCashAccCode, max(FStartDate) as FStartDate from " +
                 pub.yssGetTableName("tb_para_cashaccount") +
                 " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                 " and FCheckState = 1 group by FCashAccCode) b1 join (select FCashAccCode, FCashAccName, FCuryCode, FBankCode, FStartDate from " +
                 pub.yssGetTableName("tb_para_cashaccount") + ") b2 on b1.FCashAccCode = b2.FCashAccCode and b1.FStartDate = b2.FStartDate left join" +
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
     " (select d2.* from (select FInvmgrCode, max(FStartDate) as FStartDate from " +
                 pub.yssGetTableName("Tb_Para_Investmanager") +
                 " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                 " and FCheckState = 1 group by FInvmgrCode) d1 join " +
                 " (select FInvmgrCode, FInvmgrName, FStartDate from " +
                 pub.yssGetTableName("Tb_Para_Investmanager") +
                 ") d2 on d1.FInvmgrCode = d2.FInvmgrCode and d1.FStartDate = d2.FStartDate) d " +
                 " on a.FInvmgrCode = d.FInvmgrCode left join " +
                 //---------------------------------------------------------------
     " (select e2.* from (select FPortCode, max(FStartDate) as FStartDate from " +
                 pub.yssGetTableName("Tb_Para_Portfolio") +
                 " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                 " and FCheckState = 1 group by FPortCode) e1 join (select FPortCode, FPortName, FStartDate from " +
                 pub.yssGetTableName("Tb_Para_Portfolio") + ") e2 " +
                 " on e1.FPortCode = e2.FPortCode and e1.FStartDate = e2.FStartDate) e on a.FPortCode = e.FPortCode " +
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
                 }
                 else {
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

        }
        catch (Exception e) {

        }
        finally {
           dbl.closeResultSetFinal(rs);
        }

     }*/

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
        String endString ="";
        boolean analy1; //判断是否需要用分析代码；杨
        boolean analy2;

        try {

            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");

            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Security);
            //add by zhouxiang MS01544 现金台帐和证券台帐中，选择一条信息后点击展开按钮报错 
            if(sAry[0].indexOf("FBrokerCode")>-1&& sAry[0].indexOf("FBrokerName")>-1)
            {endString="";}
            else
            {endString=",'' as FBrokerCode,'' as FBrokerName";}
            //end by zhoxiang MS01544  现金台帐和证券台帐中，选择一条信息后点击展开按钮报错 
//         sInvmgrField = this.getSettingOper().getStorageAnalysisField(
//               YssOperCons.YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_InvMgr);
            strSql =
                "select a.FSecurityCode,a.FPortCode,a.FCuryCode,b.FCatCode,b.FSubCatCode," +
                "b.FSecurityName,c.FCuryName,d.FPortName,e.FCatName,f.FSubCatName, " +
                //--------------2008.01.27 添加 蒋锦 用于应收应付------------//
                "g.FTsfTypeCode,g.FSubTsfTypeCode,g.FTsfTypeName,g.FSubTsfTypeName " +
                //--------------------------------------------------------//
//               sAry[0] + " from ( " +
                (sAry[0].length() == 0 ? ",'' as FInvmgrCode,'' as FInvmgrName,'' as FBrokerCode,'' as FBrokerName " : sAry[0]+endString) + " from ( " + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
                " select FSecurityCode,FPortCode,FCuryCode," +
                " FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " group by FSecurityCode,FPortCode,FCuryCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3) " +
                //--------------------------------------------------------------------------------------------
                " a left join (select bb.FSecurityCode,bb.FSecurityName,bb.FCatCode,bb.FSubCatCode from (" +
                " select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("tb_para_security") +
                " where FCheckState = 1 and FStartDate < = " +
                dbl.sqlDate(new java.util.Date()) +
                "group by FSecurityCode) ba join  " +
                " (select * from " + pub.yssGetTableName("tb_para_security") +
                ") bb on ba.FSecurityCode = bb.FSecurityCode and ba.FStartDate = bb.FStartDate) b on a.FSecurityCode = b.FSecurityCode" +
                //--------------------------------------------------------------------------------------------
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) c on a.FCuryCode = c.FCuryCode" +
                //--------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
       
                
                " left join (select FPortCode,FPortName from " +           
                pub.yssGetTableName("tb_para_Portfolio") +
                " where FCheckState = 1 ) d on a.FPortCode = d.FPortCode" +
             
                //end by lidaolong
                //---------------------------------------------------------------------------------------------
                " left join (select FCatCode,FCatName from tb_base_category " +
                " where FCheckState = 1) e on b.FCatCode = e.FCatCode" +
                //---------------------------------------------------------------------------------------------
                //------------------2008.01.27 添加 蒋锦 添加对应收应付的查询------------------//
                " LEFT JOIN (SELECT FSecurityCode, FTsfTypeName, FSubTsfTypeName, ga.FTsfTypeCode, ga.FSubTsfTypeCode" +
                " FROM (SELECT DISTINCT FTsfTypeCode, FSubTsfTypeCode, FCheckState, FSecurityCode" +
                " FROM " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " WHERE SUBSTR(FYearMonth, 5, 2) <> '00' AND FTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + "," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
                " )) ga" +
                " LEFT JOIN (SELECT FTsfTypeCode, FTsfTypeName" +
                " FROM Tb_Base_TransferType ) gb" +
                " ON ga.FTsfTypeCode = gb.FTsfTypeCode" +
                " LEFT JOIN (SELECT FSubTsfTypeCode, FSubTsfTypeName" +
                " FROM Tb_Base_SubTransferType) gc ON ga.FSubTsfTypeCode = gc.FSubTsfTypeCode" +
                " WHERE FCheckState = 1) g" +
                " ON a.FSecurityCode = g.FSecurityCode" +
                //-------------------------------------------------------------------------//
                " left join (select FSubCatCode,FSubCatName from tb_base_subcategory " +
                " where FCheckState = 1) f on b.FSubCatCode = f.FSubCatCode" +
                sAry[1];

            strSql += " union ";

            strSql +=
                " select a.FSecurityCode,a.FPortCode,c.FCuryCode,c.FCatCode,c.FSubCatCode," +
                "c.FSecurityName,c.FCuryName,e.FPortName,c.FCatName,c.FSubCatName," +
                //----------2008.01.27 添加 蒋锦 用于应收应付----------//
                "g.FTsfTypeCode, g.FSubTsfTypeCode, g.FTsfTypeName, g.FSubTsfTypeName " +
                //--------------------------------------------------//

//               (analy1?",a.FInvmgrCode, d.FInvmgrName ":" ") +
                (analy1 ? ",a.FInvmgrCode, d.FInvmgrName " : ",'' as FInvmgrCode,'' as FInvmgrName ") + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
//               (analy2?",a.FBrokerCode, b.FBrokerName ":" ") +
                (analy2 ? ",a.FBrokerCode, b.FBrokerName " : ",'' as FBrokerCode,'' as FBrokerName ") + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
                " from " +
                //---------------------------------------------------------------
                " (select FSecurityCode,FPortCode,FInvMgrCode,FBrokerCode from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                ") a left join " +
                //---------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                " (select FBrokerCode, FBrokerName from " +
                pub.yssGetTableName("tb_para_broker") +
                " where  FCheckState = 1 ) b on a.FBrokerCode = b.FBrokerCode left join " +
               
                
                //end by lidaolong
                //---------------------------------------------------------------
                " (select c2.*,c3.FCatName,c4.FSubCatName,c5.FCuryName from " +
                " (select FsecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FsecurityCode) c1 join " +
                " (select FSecurityCode, FSecurityName, FCatCode, FTradeCury as FCuryCode, FSubCatCode, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") c2 on c1.Fsecuritycode = c2.Fsecuritycode and c1.FStartDate = c2.FStartDate left join " +
                " (select FCatCode, FCatName from tb_base_category where FCheckState = 1) c3 " +
                " on c2.Fcatcode = c3.FCatCode left join " +
                " (select FSubCatCode, FSubCatName from tb_base_subcategory where FCheckState = 1) c4 " +
                " on c2.FSubCatCode = c4.FSubCatCode left join " +
                " (select FCuryCode, FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) c5 on c2.FCuryCode = c5.FCuryCode) c " +
                " on a.Fsecuritycode = c.Fsecuritycode left join " +
                //---------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                " (select FInvmgrCode, FInvmgrName from " +
                pub.yssGetTableName("Tb_Para_Investmanager") +
                " where  FCheckState = 1) d " +
                " on a.FInvmgrCode = d.FInvmgrCode left join " +
            
                
                //end  by lidaolong
                //---------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码

                " (select FPortCode, FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) e on a.FPortCode = e.FPortCode " +
           
                //end by lidaolong
                //---------------------------------------------------------------
                //------------------2008.01.27 添加 蒋锦 添加对应收应付的查询------------------//
                " LEFT JOIN (SELECT FSecurityCode, FTsfTypeName, FSubTsfTypeName, ga.FTsfTypeCode, ga.FSubTsfTypeCode" +
                " FROM (SELECT DISTINCT FTsfTypeCode, FSubTsfTypeCode, FCheckState, FSecurityCode" +
                " FROM " + pub.yssGetTableName("Tb_Data_SecRecPay") +
                " WHERE FTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + "," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
                " )) ga" +
                " LEFT JOIN (SELECT FTsfTypeCode, FTsfTypeName" +
                " FROM Tb_Base_TransferType) gb ON ga.FTsfTypeCode = gb.FTsfTypeCode" +
                " LEFT JOIN (SELECT FSubTsfTypeCode, FSubTsfTypeName" +
                " FROM Tb_Base_SubTransferType) gc ON ga.FSubTsfTypeCode = gc.FSubTsfTypeCode" +
                " WHERE FCheckState = 1" +
                " ) g ON a.FSecurityCode = g.FSecurityCode" +
                //-------------------------------------------------------------------------//
                " where 1=1 " +
                (analy1 ? " and a.FInvmgrCode <> ' ' " : " ") +
                " and a.FPortCode <> ' ' " +
                " and a.FSecurityCode <> ' ' " +
                (analy2 ? " and a.FBrokerCode <> ' '" : " ");

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
                " FOrderCode varchar2(50))"; //当值过大是，插入数据就会发生错误。导致显示的数据有误。sj edit
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("");
        }
    }

    protected void setRelaMap() {

        try {
            invmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
//      invmgrField = "FAnalysisCode1";
        } catch (YssException ex1) {
        }

        hmFieldIndRela = new HashMap();
        hmFieldIndRela.put("InvMgr", "FInvMgr");
        hmFieldIndRela.put("CatType", "FCat");
        hmFieldIndRela.put("Port", "FPort");
        hmFieldIndRela.put("SubCatType", "FSubCat");
        hmFieldIndRela.put("Broker", "FBroker");
        hmFieldIndRela.put("Cury", "FCury");
        hmFieldIndRela.put("SecCode", "FSecurity");
        //------------2008.01.27 添加 蒋锦 用于应收应付-------------//
        hmFieldIndRela.put("TsfType", "FTsfType"); //调拨类型
        hmFieldIndRela.put("SubTsfType", "FSubTsfType"); //调拨子类型
        //-------------------------------------------------------//


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
        hmAliasRela.put("InvMgr", "FInvMgrCode as FCode");
        hmAliasRela.put("CatType", "FCatCode as FCode");
        hmAliasRela.put("Port", "FPortCode as FCode");
        hmAliasRela.put("SubCatType", "FSubCatCode as FCode");
        hmAliasRela.put("Broker", "FBrokerCode as FCode");
        hmAliasRela.put("Cury", "FCuryCode as FCode");
        hmAliasRela.put("SecCode", "FSecurityCode as FCode");

        hmTableRela = new HashMap();

        hmTableRela.put("InvMgr",
        		  // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
   
        	      " (select FInvMgrCode, FInvMgrName as FName from " +
                  pub.yssGetTableName("Tb_Para_InvestManager") +
                  " where FCheckState = 1 ) z on m.Fcode = z.FInvMgrCode");
  
        //end by lidaolong 
        hmTableRela.put("SecCode",
                        " (select zb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                        pub.yssGetTableName("Tb_Para_Security") +
                        " where FStartDate <= " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FSecurityCode) za " +
                        " join (select FSecurityCode, FSecurityName as FName, FStartDate from " +
                        pub.yssGetTableName("Tb_Para_Security") +
                        ") zb on za.FSecurityCode = zb.FSecurityCode and za.FStartDate = zb.FStartDate) z on m.Fcode = z.FSecurityCode");
        hmTableRela.put("CatType",
                        "(select FCatCode,FCatName as FName from Tb_Base_Category" +
                        " where FCheckState = 1) z on m.Fcode = z.FCatCode");
        hmTableRela.put("Port",
        		  // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    
        	      " (select FPortCode, FPortName as FName from " +
                  pub.yssGetTableName("Tb_Para_Portfolio") +
                  " where FCheckState = 1 ) z on m.Fcode = z.FPortCode");

        
        //end by lidaolong 
        
        hmTableRela.put("SubCatType",
                        " (select FSubCatCode,FSubCatName as FName from Tb_Base_SubCategory" +
                        " where FCheckState = 1) z on m.Fcode = z.FSubCatCode");
        hmTableRela.put("Broker",
        		  // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
       
        	    " (select FBrokerCode, FBrokerName as FName from " +
                pub.yssGetTableName("Tb_Para_Broker") +
                " where  FCheckState = 1 ) z on m.Fcode = z.FBrokerCode");

        //end by lidaolong 
        
        hmTableRela.put("Cury",
                        " (select FCuryCode,FCuryName as FName from " +
                        pub.yssGetTableName("Tb_Para_Currency") +
                        " where FCheckState = 1) z on m.FCode = z.FCuryCode");

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
        hmTableTradeRela.put("SubCatType",
                             "(select FSubCatCode,FSubCatName as FAccName from Tb_Base_SubCategory" +
                             " where FCheckState = 1) z on x.FAccCode = z.FSubCatCode");
        hmTableTradeRela.put("SecCode",
                             "(select FSecurityCode,FSecurityName as FAccName from " +
                             pub.yssGetTableName("tb_para_security") +
                             " where FCheckState = 1) z on x.FAccCode = z.FSecurityCode");
        hmTableTradeRela.put("Broker",
                             "(select FBankCode,FBankName as FAccName from " +
                             pub.yssGetTableName("Tb_Para_Bank") +
                             " where FCheckState = 1) z on x.FAccCode = z.FBankCode");
        hmTableTradeRela.put("Cury",
                             "(select FCuryCode,FCuryName as FAccName from " +
                             pub.yssGetTableName("Tb_Para_Currency") +
                             " where FCheckState = 1) z on x.FAccCode = z.FCuryCode");

        hmFieldRela = new HashMap();
        hmFieldRela.put("SecCode", "FSecurityCode");
        hmFieldRela.put("Broker", "FBrokerCode");
        hmFieldRela.put("InvMgr", "FInvMgrCode");
        hmFieldRela.put("Port", "FPortCode");
        hmFieldRela.put("Cury", "FCuryCode");
        hmFieldRela.put("CatType", "FCatCode");
        hmFieldRela.put("SubCatType", "FSubCatCode");

        hmStorageFieldRela = new HashMap();
        try {
            hmStorageFieldRela.put("InvMgr", invmgrField);
            hmStorageFieldRela.put("Broker",
                                   this.getSettingOper().
                                   getStorageAnalysisField(YssOperCons.
                YSS_KCLX_Security,
                YssOperCons.YSS_KCPZ_Broker));

            hmStorageFieldRela.put("CatType", "FCatCode");
            hmStorageFieldRela.put("SubCatType", "FSubCatCode");
            hmStorageFieldRela.put("Port", "FPortCode");
            hmStorageFieldRela.put("SecCode", "FSecurityCode");
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

    /**
     * getBookWhereSql
     *
     * @param sBookDefine String
     * @param sBookLink String
     * @return String
     */
    public String getBookSql(String sBookDefine, String sBookLink,
                             java.util.Date dBeginDate,
                             java.util.Date dEndDate, int iShowType,
                             String sCheckItems) throws
        YssException {
        String[] sBookDefineAry = null;
        String[] sBookLinkAry = null;
        String strSql = "";
        try {
            sBookDefineAry = sBookDefine.split(";");
            sBookLinkAry = sBookLink.split("\f");
            if (sBookLinkAry.length - 1 < sBookDefineAry.length) {
                strSql = getNoDetailBookSql(sBookDefineAry, sBookLinkAry,
                                            dBeginDate, dEndDate, iShowType,
                                            sCheckItems);
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

//获取非明细的未清算的数据
    protected String getDetailNoClearedBookSql(String[] sBookDefineAry,
                                               String[] sBookLinkAry,
                                               java.util.Date dBeginDate,
                                               java.util.Date dEndDate) {
        String strSql = "";
        String sFieldCode = "";
        String sTmpDefine = "";
        String sSecWhereSql = " where ";
        String sTradeWhereSql = " where ";
        for (int i = 1; i < sBookLinkAry.length; i++) {
            sTmpDefine = (String) sBookDefineAry[i - 1];
            sFieldCode = ( (String) hmFieldRela.get(sTmpDefine));
            if (sTmpDefine.equalsIgnoreCase("SecCode") ||
                sTmpDefine.equalsIgnoreCase("InvMgr") ||
                sTmpDefine.equalsIgnoreCase("Port") ||
                sTmpDefine.equalsIgnoreCase("Broker")) {
                sTradeWhereSql += sFieldCode + " = " +
                    dbl.sqlString(sBookLinkAry[i]) + " and ";
            } else if (sTmpDefine.equalsIgnoreCase("SecCode") ||
                       sTmpDefine.equalsIgnoreCase("CatType") ||
                       sTmpDefine.equalsIgnoreCase("SubCatType")) {
                sSecWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            } else if (sTmpDefine.equalsIgnoreCase("Cury")) {
                sSecWhereSql += "FTradeCury = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            }
        }

        strSql =
            " select FCuryCode,FInOut,FSecurityCode,FSecurityName," +
            " FTradeAmount, FTotalCost, (FTotalCost*FBaseCuryRate) as FBaseTotalCost,FBargainDate," +
            " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, " +
            " FNum as FSubNum, '0' as FIsCleared" +
            " from (select FNum,FSecurityCode,FPortCode,FInvMgrCode,FBrokerCode,FTradeAmount," +
            " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, " +
            " FTotalCost,FBaseCuryRate,a2.FAmountInd as FInOut,FBargainDate,a1.FCheckState from " +
            pub.yssGetTableName("Tb_Data_SubTrade") +
            " a1 join (select * from Tb_Base_TradeType where FCheckState = 1" +
            ") a2 on a1.FTradeTypeCode = a2.FTradeTypeCode " + sTradeWhereSql +
            " (FBargainDate between " + dbl.sqlDate(dBeginDate) +
            " and " + dbl.sqlDate(dEndDate) + ") and " +
            " FSettleState = 0 and a1.FCheckState = 1) a join" +
            //------------------------------------------------------------------
            " (select eb.FSecurityCode as FSecCode, FSecurityName, FCatCode, FSubCatCode, FCuryCode" +
            " from (select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") + " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FCatCode," +
            " FSubCatCode, FTradeCury as FCuryCode, FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            sSecWhereSql + " FCheckState=1" +
            ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate" +
            ") sec  on a.FSecurityCode = sec.FSecCode";

        return strSql;
    }

//获取明细的己清算的数据
    protected String getDetailClearedBookSql(String[] sBookDefineAry,
                                             String[] sBookLinkAry,
                                             java.util.Date dBeginDate,
                                             java.util.Date dEndDate) {

        String strSql = "";
        String sFieldCode = "";
        String sTmpDefine = "";
        String sSecWhereSql = " where ";
        String sTradeWhereSql = " where ";
        for (int i = 1; i < sBookLinkAry.length; i++) {
            sTmpDefine = (String) sBookDefineAry[i - 1];
            sFieldCode = ( (String) hmFieldRela.get(sTmpDefine));
            if (sTmpDefine.equalsIgnoreCase("SecCode") ||
                sTmpDefine.equalsIgnoreCase("InvMgr") ||
                sTmpDefine.equalsIgnoreCase("Port") ||
                sTmpDefine.equalsIgnoreCase("Broker")) {
                sTradeWhereSql += sFieldCode + " = " +
                    dbl.sqlString(sBookLinkAry[i]) + " and ";
            } else if (sTmpDefine.equalsIgnoreCase("SecCode") ||
                       sTmpDefine.equalsIgnoreCase("CatType") ||
                       sTmpDefine.equalsIgnoreCase("SubCatType")) {
                sSecWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            } else if (sTmpDefine.equalsIgnoreCase("Cury")) {
                sSecWhereSql += "FTradeCury = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            }
        }

        strSql =
            " select FCuryCode,FInOut,FSecurityCode,FSecurityName," +
            " FTradeAmount, FTotalCost, (FTotalCost*FBaseCuryRate) as FBaseTotalCost,FBargainDate," +
            " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, " +
            " FNum as FSubNum, '1' as FIsCleared" +
            " from (select FNum,FSecurityCode,FPortCode,FInvMgrCode,FBrokerCode,FTradeAmount," +
            " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, " +
            " FTotalCost,FBaseCuryRate,a2.FAmountInd as FInOut,FBargainDate,a1.FCheckState from " +
            pub.yssGetTableName("Tb_Data_SubTrade") +
            " a1 join (select * from Tb_Base_TradeType where FCheckState = 1" +
            ") a2 on a1.FTradeTypeCode = a2.FTradeTypeCode " + sTradeWhereSql +
            " (FBargainDate between " + dbl.sqlDate(dBeginDate) +
            " and " + dbl.sqlDate(dEndDate) + ") and " +
            " FSettleState = 1 and a1.FCheckState = 1) a join" +
            //------------------------------------------------------------------
            " (select eb.FSecurityCode as FSecCode, FSecurityName, FCatCode, FSubCatCode, FCuryCode" +
            " from (select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") + " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FCatCode," +
            " FSubCatCode, FTradeCury as FCuryCode, FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            sSecWhereSql + " FCheckState=1" +
            ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate" +
            ") sec  on a.FSecurityCode = sec.FSecCode";

        return strSql;
    }

    protected String getNoDetailBookSql(String[] sBookDefineAry,
                                        String[] sBookLinkAry,
                                        java.util.Date dBeginDate,
                                        java.util.Date dEndDate, int iShowType,
                                        String sCheckItems) throws
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
                                            dBeginDate, dEndDate, sCheckItems) +
                " union " +
                getNoDetailNoClearedSql(sBookDefineAry, sBookLinkAry,
                                        dBeginDate, dEndDate, sCheckItems);
        }
        return sResult;
    }

//获取非明细的未清算的数据
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
        sTransWhereSql = " where ";
        sStorageWhereSql = " where ";
        for (int i = 1; i < sBookLinkAry.length; i++) {
            sTmpDefine = (String) sBookDefineAry[i - 1];
            sFieldCode = ( (String) hmFieldRela.get(sTmpDefine));
//         sWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) + " and ";
//         sFieldCodes = (String) hmFieldIndRela.get(sTmpDefine) + "Code";
            if (sTmpDefine.equalsIgnoreCase("SecCode") ||
                sTmpDefine.equalsIgnoreCase("InvMgr") ||
                sTmpDefine.equalsIgnoreCase("Port") ||
                sTmpDefine.equalsIgnoreCase("Broker")) {
                sTransWhereSql += sFieldCode + " = " +
                    dbl.sqlString(sBookLinkAry[i]) + " and ";
            } else if (sTmpDefine.equalsIgnoreCase("SecCode") ||
                       sTmpDefine.equalsIgnoreCase("CatType") ||
                       sTmpDefine.equalsIgnoreCase("SubCatType")) {
                sSecWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            } else if (sTmpDefine.equalsIgnoreCase("Cury")) {
                sSecWhereSql += "FTradeCury = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            }

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

            if (sInd.equalsIgnoreCase("SecCode") ||
                sInd.equalsIgnoreCase("InvMgr") ||
                sInd.equalsIgnoreCase("Port") ||
                sInd.equalsIgnoreCase("Broker")) {
                sTransWhereSql += sFieldCodes + " in (" +
                    sCheckWhereStr + ") and ";
            } else if (sInd.equalsIgnoreCase("SecCode") ||
                       sInd.equalsIgnoreCase("CatType") ||
                       sInd.equalsIgnoreCase("SubCatType")) {
                sSecWhereSql += sFieldCodes + " in (" +
                    sCheckWhereStr + ") and ";
            } else if (sInd.equalsIgnoreCase("Cury")) {
                sSecWhereSql += "FTradeCury in (" +
                    sCheckWhereStr + ") and ";
            }

            sStorageWhereSql += sStorageFieldCode + " in (" +
                sCheckWhereStr + ") and ";
        }

        strSql = "select z.FName,'0' as FIsCleared,m.*,n.* " +
            "from (select Nvl(cs.FAccCode, x.FCode) as FCode,FBeginAmount,FBeginMBalance,FBeginBaseMBal," +
            "FBeginBalance,FBeginBaseBal,FBeginVBalance,FBeginBaseVBal,FCsCuryCode,FCuryCode,FInOut,FTradeAmount,FMCost," +
            "FCost,FVCost,FMBaseCuryCost,FBaseCuryCost,FVBaseCuryCost from (select "
            + sStorageFieldCode + " as FAccCode," +
            " sum(FStorageAmount) as FBeginAmount, " +
            " sum(FMStorageCost) as FBeginMBalance," +
            " sum(FMBaseCuryCost) as FBeginBaseMBal," +
            " sum(FStorageCost) as FBeginBalance, " +
            " sum(FBaseCuryCost) as FBeginBaseBal," +
            " sum(FVStorageCost) as FBeginVBalance," +
            " sum(FVBaseCuryCost) as FBeginBaseVBal," +
            " FCuryCode as FCsCuryCode from " +
            pub.yssGetTableName("Tb_Stock_Security") + " c1 left join " +
            //------------------------------------------------------------------
            " (select FSecurityCode as FSecCode, FCatCode, FSubCatCode" +
            " from " + pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1) c2  on c1.FSecurityCode = c2.FSecCode " +
            sStorageWhereSql + this.operSql.sqlStoragEve(dBeginDate) +
            " group by " +
            sStorageFieldCode + ",c1.FCuryCode) cs full join" +
            //------------------------------------------------------------------
            " (select " + sFieldAlias + ",FCuryCode,2 as FInOut," +
            " sum(FTradeAmount * FInOut) as FTradeAmount," +
            " sum(FMCost * FInOut) as FMCost, sum(FCost * FInOut) as FCost, sum(FVCost * FInOut) as FVCost, " +
            " sum(FMBaseCuryCost * FInOut) as FMBaseCuryCost, sum(FBaseCuryCost * FInOut) as FBaseCuryCost, " +
            " sum(FVBaseCuryCost * FInOut) as FVBaseCuryCost from " +
            //-------------------------------------------------------------------
            " (select FSecurityCode, FPortCode, FInvMgrCode, FBrokerCode, FTradeAmount," +
            " FMCost, FCost, FVCost, FMBaseCuryCost, FBaseCuryCost, FVBaseCuryCost, " +
            " a2.FAmountInd as FInOut,FBargainDate,a1.FCheckState from " +
            pub.yssGetTableName("Tb_Data_SubTrade") +
            " a1 join (select * from Tb_Base_TradeType where FCheckState = 1" +
            ") a2 on a1.FTradeTypeCode = a2.FTradeTypeCode " +
            sTransWhereSql + " (FBargainDate between " +
            dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) +
            ") and " +
            " a1.FCheckState=1 and FSettleState = 0) a join" +
            //------------------------------------------------------------------
            " (select eb.FSecurityCode as FSecCode, FCatCode, FSubCatCode, FCuryCode" +
            " from (select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") + " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FCatCode," +
            " FSubCatCode, FTradeCury as FCuryCode, FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            sSecWhereSql + " FCheckState=1" +
            ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate" +
            ") sec  on a.FSecurityCode = sec.FSecCode" +
            //------------------------------------------------------------------
            " group by " + sFieldCodes +
            ",FCuryCode,2) x " +
            "on x.FCode = cs.FAccCode and x.FCuryCode = cs.FCsCuryCode) m left join " +
            //------------------------------------------------------------------
            "(select sum(FMBal) as FMBalMV,sum(FMBaseCuryBal) as FBaseMBalMV,sum(FBal) as FBalMV,sum(FBaseCuryBal) as FBaseBalMV," +
            "sum(FVBal) as FVBalMV,sum(FVBaseCuryBal) as FBaseVBalMV," +
            sStorageFieldCode +
            " as FMVCode from " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
            " r1 left join " +
            //------------------------------------------------------------------
            " (select FSecurityCode as FSecCode, FCatCode, FSubCatCode" +
            " from " + pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1) r2  on r1.FSecurityCode = r2.FSecCode " +
            sStorageWhereSql + " FStorageDate= " + dbl.sqlDate(dEndDate) +
            //   this.operSql.sqlStoragEve(dBeginDate) +
            " and FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
            " group by " +
            sStorageFieldCode +
            ",r1.FCuryCode) n on m.FCode = n.FMVCode left join " +
            //------------------------------------------------------------------
            (String) hmTableRela.get(sInd);

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
        String sInd = "";
        String strSql = "";
        String sStorageFieldCode = "";
        String sTmpDefine = "";
        String sTransWhereSql = "";
        String sCheckWhereStr = "";

        sSecWhereSql = " where ";
        sTransWhereSql = " where ";
        sStorageWhereSql = " where ";
        for (int i = 1; i < sBookLinkAry.length; i++) {
            sTmpDefine = (String) sBookDefineAry[i - 1];
            sFieldCode = ( (String) hmFieldRela.get(sTmpDefine));
//         sWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) + " and ";
//         sFieldCodes = (String) hmFieldIndRela.get(sTmpDefine) + "Code";
            if (sTmpDefine.equalsIgnoreCase("SecCode") ||
                sTmpDefine.equalsIgnoreCase("InvMgr") ||
                sTmpDefine.equalsIgnoreCase("Port") ||
                sTmpDefine.equalsIgnoreCase("Broker")) {
                sTransWhereSql += sFieldCode + " = " +
                    dbl.sqlString(sBookLinkAry[i]) + " and ";
            } else if (sTmpDefine.equalsIgnoreCase("SecCode") ||
                       sTmpDefine.equalsIgnoreCase("CatType") ||
                       sTmpDefine.equalsIgnoreCase("SubCatType")) {
                sSecWhereSql += sFieldCode + " = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            } else if (sTmpDefine.equalsIgnoreCase("Cury")) {
                sSecWhereSql += "FTradeCury = " + dbl.sqlString(sBookLinkAry[i]) +
                    " and ";
            }

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

            if (sInd.equalsIgnoreCase("SecCode") ||
                sInd.equalsIgnoreCase("InvMgr") ||
                sInd.equalsIgnoreCase("Port") ||
                sInd.equalsIgnoreCase("Broker")) {
                sTransWhereSql += sFieldCodes + " in (" +
                    sCheckWhereStr + ") and ";
            } else if (sInd.equalsIgnoreCase("SecCode") ||
                       sInd.equalsIgnoreCase("CatType") ||
                       sInd.equalsIgnoreCase("SubCatType")) {
                sSecWhereSql += sFieldCodes + " in (" +
                    sCheckWhereStr + ") and ";
            } else if (sInd.equalsIgnoreCase("Cury")) {
                sSecWhereSql += "FTradeCury in (" +
                    sCheckWhereStr + ") and ";
            }

            sStorageWhereSql += sStorageFieldCode + " in (" +
                sCheckWhereStr + ") and ";
        }
        strSql = "select z.FName,'1' as FIsCleared,m.*,n.* " +
            "from (select Nvl(cs.FAccCode, x.FCode) as FCode,FBeginAmount,FBeginMBalance,FBeginBaseMBal," +
            "FBeginBalance,FBeginBaseBal,FBeginVBalance,FBeginBaseVBal,FCsCuryCode,FCuryCode,FInOut,FTradeAmount,FMCost," +
            "FCost,FVCost,FMBaseCuryCost,FBaseCuryCost,FVBaseCuryCost from (select "
            + sStorageFieldCode + " as FAccCode," +
            " sum(FStorageAmount) as FBeginAmount, " +
            " sum(FMStorageCost) as FBeginMBalance," +
            " sum(FMBaseCuryCost) as FBeginBaseMBal," +
            " sum(FStorageCost) as FBeginBalance, " +
            " sum(FBaseCuryCost) as FBeginBaseBal," +
            " sum(FVStorageCost) as FBeginVBalance," +
            " sum(FVBaseCuryCost) as FBeginBaseVBal," +
            " FCuryCode as FCsCuryCode from " +
            pub.yssGetTableName("Tb_Stock_Security") + " c1 left join " +
            //------------------------------------------------------------------
            " (select FSecurityCode as FSecCode, FCatCode, FSubCatCode" +
            " from " + pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1) c2  on c1.FSecurityCode = c2.FSecCode " +
            sStorageWhereSql + this.operSql.sqlStoragEve(dBeginDate) +
            " group by " +
            sStorageFieldCode + ",c1.FCuryCode) cs full join" +
            //------------------------------------------------------------------
            " (select " + sFieldAlias + ",FCuryCode,FInOut," +
            " sum(FTradeAmount) as FTradeAmount," +
            " sum(FMCost) as FMCost, sum(FCost) as FCost, sum(FVCost) as FVCost, " +
            " sum(FMBaseCuryCost) as FMBaseCuryCost, sum(FBaseCuryCost) as FBaseCuryCost, " +
            " sum(FVBaseCuryCost) as FVBaseCuryCost from " +
            //-------------------------------------------------------------------
            " (select FSecurityCode, FPortCode, FInvMgrCode, FBrokerCode, FTradeAmount," +
            " FMCost, FCost, FVCost, FMBaseCuryCost, FBaseCuryCost, FVBaseCuryCost, " +
            " a2.FAmountInd as FInOut from " +
            pub.yssGetTableName("Tb_Data_SubTrade") +
            " a1 join (select * from Tb_Base_TradeType where FCheckState = 1" +
            ") a2 on a1.FTradeTypeCode = a2.FTradeTypeCode " +
            sTransWhereSql + " (FBargainDate between " +
            dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate) +
            ") and " +
            " a1.FCheckState=1 and FSettleState = 1) a join" +
            //------------------------------------------------------------------
            " (select eb.FSecurityCode as FSecCode, FCatCode, FSubCatCode, FCuryCode" +
            " from (select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") + " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FCatCode," +
            " FSubCatCode, FTradeCury as FCuryCode, FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            sSecWhereSql + " FCheckState=1" +
            ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate" +
            ") sec  on a.FSecurityCode = sec.FSecCode" +
            //------------------------------------------------------------------
            " group by " + sFieldCodes +
            ",FCuryCode,FInOut) x " +
            "on x.FCode = cs.FAccCode and x.FCuryCode = cs.FCsCuryCode) m left join " +
            //------------------------------------------------------------------
            "(select sum(FMBal) as FMBalMV,sum(FMBaseCuryBal) as FBaseMBalMV,sum(FBal) as FBalMV,sum(FBaseCuryBal) as FBaseBalMV," +
            "sum(FVBal) as FVBalMV,sum(FVBaseCuryBal) as FBaseVBalMV," +
            sStorageFieldCode +
            " as FMVCode from " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
            " r1 left join " +
            //------------------------------------------------------------------
            " (select FSecurityCode as FSecCode, FCatCode, FSubCatCode" +
            " from " + pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1) r2  on r1.FSecurityCode = r2.FSecCode " +
            sStorageWhereSql + " FStorageDate = " + dbl.sqlDate(dEndDate) +
            //           this.operSql.sqlStoragEve(dBeginDate) +
            " and FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
            " group by " +
            sStorageFieldCode +
            ",r1.FCuryCode) n on m.FCode = n.FMVCode left join " +
            //------------------------------------------------------------------
            (String) hmTableRela.get(sInd);

        return strSql;
    }

}
