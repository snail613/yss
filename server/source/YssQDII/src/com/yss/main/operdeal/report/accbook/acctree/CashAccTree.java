package com.yss.main.operdeal.report.accbook.acctree;

import com.yss.dsub.BaseBean;
import com.yss.main.dao.IAccBookOper;
import java.sql.*;
import com.yss.util.*;
import java.util.Date;
import java.util.*;
import com.yss.pojo.sys.YssTreeNode;
import java.util.Iterator;
import com.yss.dsub.*;

public class CashAccTree
    extends BaseBean implements IAccBookOper {
    public CashAccTree() {
    }

    private HashMap hmFieldIndRela;
    private HashMap hmStorageFieldRela;
    private String sBookDefine;
    private String invmgrField = "";

    /**
     * setBookClassTable
     *加载树数据的入口
     * @param sBookDefine String
     * @param iShowType int
     */
    public void setBookClassTable(String sBookDefine, int iShowType) throws
        YssException {
        String[] sBookDefineAry = null;
        this.createTmpTable();
        this.sBookDefine = sBookDefine;
        sBookDefineAry = sBookDefine.split(";");
        setClearedClassTable(sBookDefineAry);

    }

    //建表，如有先删除
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
            if (dbl.dbType == YssCons.DB_ORA) {
                strSql = "create table " +
                    pub.yssGetTableName("tb_Temp_BookCls_" + pub.getUserCode()) +
                    " (FCode varchar2(200)," +
                    " FName varchar2(100)," +
                    " FParentCode varchar2(200)," +
                    " FOrderCode varchar2(100))";
                dbl.executeSql(strSql);
            } else {
                strSql = "create table " +
                    pub.yssGetTableName("tb_Temp_BookCls_" + pub.getUserCode()) +
                    " (FCode varchar(200)," +
                    " FName varchar(100)," +
                    " FParentCode varchar(200)," +
                    " FOrderCode varchar(100))";
                dbl.executeSql(strSql);
            }
        } catch (Exception e) {
            throw new YssException("生成现金台帐树形菜单临时表出错！", e);
        }
    }

//获取数据
    protected void setClearedClassTable(String[] sBookDefineAry) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        String sAry[] = null;
        String sCode[] = null;
        HashMap hmData = new HashMap();
        String sFieldCode = "";
        String sFieldName = "";
        String sOrderIndex = "";
        YssTreeNode tNode = null;

        boolean analy1;
        boolean analy2;

        try {

            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");

            sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Cash);

            strSql =
                "select a.FCashAccCode,a.FBankCode,a.FAccType as FAccTypeCode,a.FSubAccType as FSubAccTypeCode,a.FPortCode,a.FCuryCode,h.FTsfTypeCode,h.FSubTsfTypeCode," +
                " b.FCashAccName,c.FCuryName,d.FPortName,e.FBankName,f.facctypename,g.fsubacctypename, h.FTsfTypeName,h.FSubTsfTypeName " +
//                sAry[0] + " from ( " +
                (sAry[0].length() == 0 ? ",'' as FInvmgrCode,'' as FInvmgrName,'' as FCatCode,'' as FCatName " : sAry[1]) + " from ( " + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
                " select aa.FCashAccCode,ab.FBankCode,ab.FAccType,ab.FSubAccType,aa.FPortCode,aa.FCuryCode," +
                " aa.FAnalysisCode1,aa.FAnalysisCode2,aa.FAnalysisCode3 from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                //-------------------------------------------------------------------------------------------
                " aa left join (select FCashAccCode,FBankCode,FAccType,FSubAccType from " +
                pub.yssGetTableName("tb_para_cashaccount") +
                " WHERE FCheckState = 1) ab on aa.Fcashacccode = ab.FCashAccCode " +
                " WHERE FCheckState = 1 " +
                " group by aa.FCashAccCode,ab.FBankCode,ab.FAccType,ab.FSubAccType,aa.FPortCode,aa.FCuryCode,aa.FAnalysisCode1,aa.FAnalysisCode2,aa.FAnalysisCode3) " +
                //--------------------------------------------------------------------------------------------
            
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
           
                " a left join (select FCashAccCode,FCashAccName from " +             
                pub.yssGetTableName("tb_para_cashaccount") +
                " where FCheckState = 1 " + 
                ") b on a.FCashAccCode = b.FCashAccCode" +
              
              
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
                " left join (select FAccTypeCode, FAccTypeName from Tb_Base_AccountType) f on a.FAccType = f.FAccTypeCode " +
                " left join (select FSubAccTypeName, FSubAccTypeCode from Tb_Base_SubAccountType) g on a.FSubAccType = g.FSubAccTypeCode " +
                //====================================调拨类型，调拨子类型
                " left join ( select distinct h1.FCashAccCode,h1.FTsfTypeCode,h1.FSubTsfTypeCode,h2.FTsfTypeName,h3.FSubTsfTypeName from " +
                "( select * from " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                "  where FCheckState=1 AND FTsfTypeCode IN ('06','07') ) h1 " +
                " left join ( select  FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType  where FCheckState=1) " +
                " h2 on h1.FTsfTypeCode = h2.ftsftypecode " +
                " left join  ( select  FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType  where FCheckState=1) " +
                " h3 on h1.FSubTsfTypeCode = h3.FSubTsfTypeCode ) h  on a.FCashAccCode = h.FCashAccCode" +
                //---------------------------
                sAry[1];

            strSql += " union ";

            strSql +=
                " select a.FCashAccCode,b.FBankCode,b.FAccType as FAccTypeCode,b.FSubAccType as FSubAccTypeCode,a.FPortCode,b.FCuryCode, f.FTsfTypeCode, f.FSubTsfTypeCode," +
                "b.FCashAccName,b.FCuryName,e.FPortName,b.FBankName,b.facctypename,b.fsubacctypename,f.FTsfTypeName, f.FSubTsfTypeName " +
//                (analy1?", a.FInvmgrCode, d.FInvmgrName":" ") +
                (analy1 ? ", a.FInvmgrCode, d.FInvmgrName" : ",'' as FInvmgrCode, '' as FInvmgrName ") + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
//                (analy2?",c.FCatCode, c.FCatName ":" ") +
                (analy2 ? ",c.FCatCode, c.FCatName " : ",'' as FCatCode, '' as FCatName ") + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
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
        
                
                " (select b2.*,b3.FBankName,b4.FCuryName,b5.facctypename,b6.fsubacctypename from (select FCashAccCode, FCashAccName, FCuryCode, FBankCode,FAccType,FSubAccType from " +
                pub.yssGetTableName("tb_para_cashaccount") +
                " where  FCheckState = 1 ) b2  left join " +
                
                //end by lidaolong 
                //---------------------------------------------------------------
                " (select FBankCode,FBankName from " +
                pub.yssGetTableName("tb_para_bank") +
                " where FCheckState = 1) b3 on b2.Fbankcode = b3.FBankCode left join " +
                " (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) b4 on b2.Fcurycode = b4.FCuryCode  left join " +

                " (select FAccTypeCode, FAccTypeName from Tb_Base_AccountType" +
                " where FCheckState = 1) b5 on b2.FAccType = b5.FAccTypeCode left join " +
                " (select FSubAccTypeName, FSubAccTypeCode from Tb_Base_SubAccountType" +
                " where FCheckState = 1) b6 on b2.FSubAccType = b6.FSubAccTypeCode) b on a.FCashAccCode =  b.FCashAccCode " +
                //----------------------------调拨类型

                " left join (" +
                " select distinct f1.FCashAccCode,f1.FTsfTypeCode,f2.FTsfTypeName,f1.FSubTsfTypeCode,f3.FSubTsfTypeName from (select * from " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                "  where FCheckState=1 AND FTsfTypeCode IN ('06','07')) f1" +
                " left join ( select  FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType  where FCheckState=1) f2 on f1.FTsfTypeCode = f2.ftsftypecode " +
                " left join  ( select  FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType  where FCheckState=1) " +
                " f3 on f1.FSubTsfTypeCode = f3.FSubTsfTypeCode " +
                " ) f on a.FCashAccCode = f.FCashAccCode " +
                //---------------------------------------------------------------
                "  left join (select c2.*,c3.FCatName from (select Fsecuritycode, max(FStartDate) as FStartDate from " +
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
                           
                    // end by lidaolong 
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

                    sCode = builderCode(rs, i + 1).split("\f");
                    //如果没有代码的放弃，有代码没有中文的则不显示中文
                    if (sCode[sCode.length - 1] == null ||
                        sCode[sCode.length - 1].equalsIgnoreCase("null")) {
                        continue;
                    }
                    tNode.setName(sCode[sCode.length - 1] + "_" +
                                  rs.getString(sFieldName));
                    if (rs.getString(sFieldName) == null ||
                        rs.getString(sFieldName).equalsIgnoreCase("null")) {
                        tNode.setName(sCode[sCode.length - 1]);
                    }

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

    //解析数据,根据级数来得到各层的代码窜起来
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

    //将数据插入存放树结构的表中
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

    //初始化HashMap，便于字段的匹配
    protected void setRelaMap() {
        try {
            invmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.
                YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_InvMgr);
        } catch (YssException ex1) {
        }

        hmFieldIndRela = new HashMap();
        hmFieldIndRela.put("InvMgr", "FInvMgr");
        hmFieldIndRela.put("CatType", "FCat");
        hmFieldIndRela.put("Port", "FPort");
        hmFieldIndRela.put("Acc", "FCashAcc");
        hmFieldIndRela.put("Bank", "FBank");
        hmFieldIndRela.put("AccType", "FAccType");
        hmFieldIndRela.put("SubAccType", "FSubAccType");
        hmFieldIndRela.put("Cury", "FCury");
        hmFieldIndRela.put("TsfType", "FTsfType");
        hmFieldIndRela.put("SubTsfType", "FSubTsfType");

        hmStorageFieldRela = new HashMap();
        try {
            hmStorageFieldRela.put("InvMgr", invmgrField);

            hmStorageFieldRela.put("CatType",
                                   this.getSettingOper().
                                   getStorageAnalysisField(YssOperCons.
                YSS_KCLX_Cash,
                YssOperCons.YSS_KCPZ_CatType));

            hmStorageFieldRela.put("Acc", "FCashAccCode");
            hmStorageFieldRela.put("Port", "FPortCode");
            hmStorageFieldRela.put("Bank", "FBankCode");
            hmStorageFieldRela.put("Cury", "FCuryCode");
            hmStorageFieldRela.put("AccType", "FAccTypeCode");
            hmStorageFieldRela.put("SubAccType", "FSubAccTypeCode");
            hmStorageFieldRela.put("TsfType", "FTsfTypeCode");
            hmStorageFieldRela.put("SubTsfType", "FSubTsfTypeCode");
        }

        catch (YssException ex) {
        }

    }

    /**
     * getBookSql
     *
     * @param sBookDefine String
     * @param sBookLink String
     * @param dBeginDate Date
     * @param dEndDate Date
     * @param iShowType int
     * @param sCheckItems String
     * @return String
     */
    public String getBookSql(String sBookDefine, String sBookLink,
                             Date dBeginDate, Date dEndDate, int iShowType,
                             String sCheckItems) {
        return "";
    }

    /**
     * setYssPub
     *
     * @param pub YssPub
     */
    public void setYssPub(YssPub pub) {
        super.setYssPub(pub);
        this.setRelaMap();
    }

}
