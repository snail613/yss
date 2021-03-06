package com.yss.main.operdeal.report.accbook.acctree;

import com.yss.dsub.BaseBean;
import com.yss.main.dao.IAccBookOper;
import java.sql.*;
import com.yss.util.*;
import java.util.*;
import com.yss.pojo.sys.YssTreeNode;
import java.util.Iterator;
import com.yss.dsub.*;

public class SecAccTree
    extends BaseBean implements IAccBookOper {
    public SecAccTree() {
    }

    private HashMap hmFieldIndRela;
    private String sBookDefine;
    private String invmgrField = "";

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
            throw new YssException("生成证券台帐树形菜单临时表出错", e);
        }
    }

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

//        strSql =
//              "select a.FSecurityCode,a.FPortCode,a.FCuryCode,b.FCatCode,b.FSubCatCode," +
//              "b.FSecurityName,c.FCuryName,d.FPortName,e.FCatName,f.FSubCatName, " +
//              "g.FTsfTypeCode,g.FSubTsfTypeCode,g.FTsfTypeName,g.FSubTsfTypeName " +
//
//              sAry[0] + " from ( " +
//              " select FSecurityCode,FPortCode,FCuryCode," +
//              " FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
//              pub.yssGetTableName("Tb_Stock_Security") +
//              " group by FSecurityCode,FPortCode,FCuryCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3) " +
//              //--------------------------------------------------------------------------------------------
//              " a left join (select bb.FSecurityCode,bb.FSecurityName,bb.FCatCode,bb.FSubCatCode from (" +
//              " select FSecurityCode,max(FStartDate) as FStartDate from " +
//              pub.yssGetTableName("tb_para_security") +
//              " where FCheckState = 1 and FStartDate < = " +
//              dbl.sqlDate(new java.util.Date()) +
//              "group by FSecurityCode) ba join  " +
//              " (select * from " + pub.yssGetTableName("tb_para_security") +
//              ") bb on ba.FSecurityCode = bb.FSecurityCode and ba.FStartDate = bb.FStartDate) b on a.FSecurityCode = b.FSecurityCode" +
//              //--------------------------------------------------------------------------------------------
//              " left join (select FCuryCode,FCuryName from " +
//              pub.yssGetTableName("tb_para_currency") +
//              " where FCheckState = 1) c on a.FCuryCode = c.FCuryCode" +
//              //--------------------------------------------------------------------------------------------
//              " left join (select db.FPortCode,db.FPortName from (" +
//              " select FPortCode,max(FStartDate) as FStartDate from " +
//              pub.yssGetTableName("tb_para_Portfolio") +
//              " where FCheckState = 1 and FStartDate < = " +
//              dbl.sqlDate(new java.util.Date()) +
//              "group by FPortCode) da join  " +
//              " (select FPortCode,FPortName,FStartDate from " +
//              pub.yssGetTableName("tb_para_Portfolio") +
//              ") db on da.FPortCode = db.FPortCode and da.FStartDate = db.FStartDate) d on a.FPortCode = d.FPortCode" +
//              //---------------------------------------------------------------------------------------------
//              " left join (select FCatCode,FCatName from tb_base_category " +
//              " where FCheckState = 1) e on b.FCatCode = e.FCatCode" +
//              //---------------------------------------------------------------------------------------------
//
//              " LEFT JOIN (SELECT FSecurityCode, FTsfTypeName, FSubTsfTypeName, ga.FTsfTypeCode, ga.FSubTsfTypeCode" +
//              " FROM (SELECT DISTINCT FTsfTypeCode, FSubTsfTypeCode, FCheckState, FSecurityCode" +
//              " FROM " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
//              " WHERE SUBSTR(FYearMonth, 5, 2) <> '00' AND FTsfTypeCode in (" +
//              YssOperCons.YSS_ZJDBLX_Rec + "," + YssOperCons.YSS_ZJDBLX_Pay +
//              "," + YssOperCons.YSS_ZJDBLX_MV +
//              " )) ga" +
//              " LEFT JOIN (SELECT FTsfTypeCode, FTsfTypeName" +
//              " FROM Tb_Base_TransferType ) gb" +
//              " ON ga.FTsfTypeCode = gb.FTsfTypeCode" +
//              " LEFT JOIN (SELECT FSubTsfTypeCode, FSubTsfTypeName" +
//              " FROM Tb_Base_SubTransferType) gc ON ga.FSubTsfTypeCode = gc.FSubTsfTypeCode" +
//              " WHERE FCheckState = 1) g" +
//              " ON a.FSecurityCode = g.FSecurityCode" +
//              //-------------------------------------------------------------------------//
//              " left join (select FSubCatCode,FSubCatName from tb_base_subcategory " +
//              " where FCheckState = 1) f on b.FSubCatCode = f.FSubCatCode" +
//              sAry[1];
//
//        strSql += " union ";

            strSql +=
                " select distinct a.FSecurityCode,a.FPortCode,c.FCuryCode,c.FCatCode,c.FSubCatCode," +
                "c.FSecurityName,c.FCuryName,e.FPortName,c.FCatName,c.FSubCatName," +
                "g.FTsfTypeCode, g.FSubTsfTypeCode, g.FTsfTypeName, g.FSubTsfTypeName " +
                //(analy1 ? ",a.FInvmgrCode, d.FInvmgrName " : " ") +
                (analy1 ? ",a.FInvmgrCode, d.FInvmgrName " : ",'' as FInvmgrCode, '' as FInvmgrName ") + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
                //(analy2 ? ",a.FBrokerCode, b.FBrokerName " : " ") +
                (analy2 ? ",a.FBrokerCode, b.FBrokerName " : ",'' as FBrokerCode, '' as FBrokerName ") + //QDV4南方2009年1月5日09_B MS00153 这里判断如果无分析代码时也应该加上默认值 leeyu 2009-1-13
                " from " +
                //---------------------------------------------------------------
                " (select distinct FSecurityCode,FPortCode,FInvMgrCode,FBrokerCode from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " WHERE FCheckState = 1 ) a left join " +
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
                
                //end by lidaolong
                //---------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                " (select FPortCode, FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) e on a.FPortCode = e.FPortCode " +
               
                // end by lidaolong
                //---------------------------------------------------------------
                " LEFT JOIN (SELECT DISTINCT FSecurityCode, FTsfTypeName, FSubTsfTypeName, ga.FTsfTypeCode, ga.FSubTsfTypeCode" +
                " FROM (SELECT FTsfTypeCode, FSubTsfTypeCode, FCheckState, FSecurityCode" +
                " FROM " + pub.yssGetTableName("Tb_Data_SecRecPay") +
                " WHERE FTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + "," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) +
                "," + dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
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

                    sCode = builderCode(rs, i + 1).split("\f");
                    //如果没有代码的放弃，有代码没有中文的则不显示中文
                    if (sCode[sCode.length - 1] == null || sCode[sCode.length - 1].equalsIgnoreCase(" ") || sCode[sCode.length - 1].equalsIgnoreCase("null")) {
                        continue;
                    }
                    tNode.setName(sCode[sCode.length - 1] + "_" + rs.getString(sFieldName));
                    if (rs.getString(sFieldName) == null || rs.getString(sFieldName).equalsIgnoreCase("null")) {
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
        boolean bTrans = false;
        try {
            Iterator iter = hmData.values().iterator();
            conn.setAutoCommit(false);
            strSql = "insert into " +
                pub.yssGetTableName("tb_Temp_BookCls_" + pub.getUserCode()) +
                " (FCode,FName,FParentCode,FOrderCode) values (?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);
            conn.setAutoCommit(false);
            bTrans = true;
            while (iter.hasNext()) {
                tNode = (YssTreeNode) iter.next();
                pstmt.setString(1, tNode.getCode());
                pstmt.setString(2, tNode.getName());
                pstmt.setString(3, tNode.getParentCode());
                pstmt.setString(4, tNode.getOrderCode());
                pstmt.executeUpdate();
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("将树数据插入临时表出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    protected void setRelaMap() {
        try {
            invmgrField = this.getSettingOper().getStorageAnalysisField(
                YssOperCons.
                YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_InvMgr);

            hmFieldIndRela = new HashMap();
            hmFieldIndRela.put("InvMgr", "FInvMgr");
            hmFieldIndRela.put("CatType", "FCat");
            hmFieldIndRela.put("Port", "FPort");
            hmFieldIndRela.put("SubCatType", "FSubCat");
            hmFieldIndRela.put("Broker", "FBroker");
            hmFieldIndRela.put("Cury", "FCury");
            hmFieldIndRela.put("SecCode", "FSecurity");
            hmFieldIndRela.put("TsfType", "FTsfType");
            hmFieldIndRela.put("SubTsfType", "FSubTsfType");
        } catch (Exception e) {

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

        String strSql = "";
        try {
            return strSql;
        } catch (Exception e) {
            throw new YssException("获取证券台帐查询语句出错");
        }
    }

}
