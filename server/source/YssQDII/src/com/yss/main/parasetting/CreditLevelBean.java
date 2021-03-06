package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class CreditLevelBean
    extends BaseDataSettingBean implements IDataSetting {
    public String getSecurityName() {
        return securityName;
    }

    public String getDesc() {
        return desc;
    }

    public String getCreditType() {
        return creditType;
    }

    public String getCreditLevelCode() {
        return creditLevelCode;
    }

    public String getOldSecurityCode() {
        return oldSecurityCode;
    }

    public CreditLevelBean getFilterType() {
        return filterType;
    }

    public String getOrganCode() {
        return organCode;
    }

    public String getCreditLevelName() {
        return creditLevelName;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public void setCreditLevelCode(String creditLevelCode) {
        this.creditLevelCode = creditLevelCode;
    }

    public void setOldSecurityCode(String oldSecurityCode) {
        this.oldSecurityCode = oldSecurityCode;
    }

    public void setFilterType(CreditLevelBean filterType) {
        this.filterType = filterType;
    }

    public void setOrganCode(String organCode) {
        this.organCode = organCode;
    }

    public void setCreditLevelName(String creditLevelName) {
        this.creditLevelName = creditLevelName;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getOrganName() {
        return organName;
    }

    ///信用评级设置
    public CreditLevelBean() {
    }

    private String securityCode = ""; //证券代码
    private String securityName = ""; //证券名称
    private String creditType = ""; //评级类型
    private String organCode = ""; //机构代码
    private String organName = ""; //机构名称
    private String creditLevelCode = ""; //信用评级代码
    private String creditLevelName = ""; //信用评级名称
    private String desc = "";
    private String oldSecurityCode = "";
    private CreditLevelBean filterType;
    public String masterSecurityCode = ""; //保存主表的SecurityCode的值;

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            rs.close();
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取信用评级设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public void setSecurityAttr(ResultSet rs) throws SQLException {
        this.securityCode = rs.getString("FSecurityCode");
        this.securityName = rs.getString("FSecurityName");
        this.creditType = rs.getString("FCreditType");
        this.organCode = rs.getString("FOrganCode");
        this.organName = rs.getString("FOrganName");
        this.creditLevelCode = rs.getString("FCreditLevelCode");
        this.creditLevelName = rs.getString("FCreditLevelName");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);
    }

    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select a.*,b.FAffCorpCode as FOrganCode,b.FAffCorpName as FOrganName ," +
            "c.FSecurityCode as FSecurityCode,c.FSecurityName as FSecurityName ," +
            "d.FCreditLevelCode as FCreditLevelCode,d.FCreditLevelName as FCreditLevelName, " +
            "e.FUserName as FCreatorName, f.FUserName as FCheckUserName " +
            " from " + pub.yssGetTableName("Tb_Para_CreditLevel") + " a" +
            " left join ( select FAffCorpCode,FAffCorpName from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + ") b" +
            " on a.FOrganCode = b.FAffCorpCode " +
            " left join ( select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") + ") c" +
            " on a.FSecurityCode = c.FSecurityCode " +
            " left join (select FCreditLevelCode ,FCreditLevelName from " + pub.yssGetTableName("Tb_Para_CreditLevelDict") + ") d " +
            " on a.FCreditLevel = d.FCreditLevelCode " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCreator = e.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) f on a.FCheckUser = f.FUserCode" +
            buildFilterSql() +
            "  and a.FCheckState <> 2 order by c.FSecurityCode";
        return this.builderListViewData(strSql);
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            if (this.masterSecurityCode.trim().length() == 0) {
                this.masterSecurityCode = "%";
            }
            sHeader = "机构代码\t机构名称\t信用评级代码\t信用评级名称";
            strSql = "select a.*,b.FAffCorpCode as FOrganCode,b.FAffCorpName as FOrganName ," +
                "c.FSecurityCode as FSecurityCode,c.FSecurityName as FSecurityName ," +
                "d.FCreditLevelCode as FCreditLevelCode,d.FCreditLevelName as FCreditLevelName " +
                ",f.FUserName as FCreatorName, e.FUserName as FCheckUserName " +
                " from " + pub.yssGetTableName("Tb_Para_CreditLevel") + " a" +
                " left join ( select FAffCorpCode,FAffCorpName from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + ") b" +
                " on a.FOrganCode = b.FAffCorpCode " +
                " left join ( select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") + ") c" +
                " on a.FSecurityCode = c.FSecurityCode " +
                " left join (select FCreditLevelCode ,FCreditLevelName from " + pub.yssGetTableName("Tb_Para_CreditLevelDict") + ") d " +
                " on a.FCreditLevel = d.FCreditLevelCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCreator = e.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) f on a.FCheckUser = f.FUserCode" +
                " where FCheckState =1 and a.FSecurityCode ='" + this.masterSecurityCode + "' order by c.FSecurityCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FOrganCode") + " ".trim()).append("\t");
                bufShow.append(rs.getString("FOrganName") + " ".trim()).append("\t");
                bufShow.append(rs.getString("FCreditLevelCode") + " ".trim()).append("\t");
                bufShow.append(rs.getString("FCreditLevelName") + " ".trim()).append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.setSecurityAttr(rs);
                /*      this.securityCode = rs.getString("FSecurityCode");
                        this.securityName = rs.getString("FSecurityName");
                        this.organCode = rs.getString("FOrganCode");
                        this.organName = rs.getString("FOrganName");
                        this.creditLevelCode = rs.getString("FCreditLevelCode");
                        this.creditLevelName = rs.getString("FCreditLevelName");
                        this.desc = rs.getString("FDesc");
                 */bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            rs.close();
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

        } catch (Exception e) {
            throw new YssException("获取数据时出错", e);
        }finally{
        	dbl.closeResultSetFinal(rs);//合并太平资产调整  2010.08.24
        }
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.creditType).append("\t");
        buf.append(this.organCode).append("\t");
        buf.append(this.organName).append("\t");
        buf.append(this.creditLevelCode).append("\t");
        buf.append(this.creditLevelName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.oldSecurityCode).append("\t");
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.securityCode = reqAry[0];
            this.securityName = reqAry[1];
            this.creditType = reqAry[2];
            this.organCode = reqAry[3];
            this.organName = reqAry[4];
            this.creditLevelCode = reqAry[5];
            if (this.creditLevelCode.trim().length() == 0) {
                this.creditLevelCode = " ";
            }
            this.creditLevelName = reqAry[6];
            this.desc = reqAry[7];
            this.checkStateId = Integer.parseInt(reqAry[8]);
            this.oldSecurityCode = reqAry[9];
            this.masterSecurityCode = reqAry[10];
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CreditLevelBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析信用评级信息出错", e);
        }
    }

    public String getTreeViewData1() {
        return "";
    }

    public String getTreeViewData2() {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_CreditLevel") +
                " (FSecurityCode,FCreditType,FOrganCode,FCreditLevel," +
                "FDesc,FCheckState,FCreator,FCreateTime) values(" +
                dbl.sqlString(this.securityCode) + " ," +
                dbl.sqlString(this.creditType) + " ," +
                dbl.sqlString(this.organCode) + " ," +
                dbl.sqlString(this.creditLevelCode.trim()) + " ," +
                dbl.sqlString(this.desc) + ", " +
                (pub.getSysCheckState() ? "0" : "1") + " ," +
                dbl.sqlString(this.creatorCode + " ") + " ," +
                dbl.sqlString(this.creatorTime + " ") + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增信用评级信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public void checkInput(byte btOper) throws YssException {
        /*    dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_FixInterest"),
                                         "FSecurityCode,",
                                         this.securityCode + "," ,
                                         this.oldSecurityCode );
         */
    }

    public void checkSetting() throws YssException {

    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_CreditLevel") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode() + " ") +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = " +
                dbl.sqlString(this.oldSecurityCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除信用评级信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_CreditLevel") +
                " set " +
                " FSecurityCode = " + dbl.sqlString(this.securityCode) + " ," +
                " FCreditType = " + dbl.sqlString(this.creditType) + " ," +
                " FOrganCode = " + dbl.sqlString(this.organCode) + " ," +
                " FCreditLevel = " + dbl.sqlString(this.creditLevelCode.trim()) + " ," +
                " FCheckState = " + (pub.getSysCheckState() ? "0" : "1") + " ," +
                " FDesc = " + dbl.sqlString(this.desc) + " ," +
                " FCreator = " + (pub.getSysCheckState() ? " " : dbl.sqlString(this.creatorCode) + " ") + " ," +
                " FCreateTime = " + dbl.sqlString(this.creatorTime + " ") + " ," +
                " FCheckUser = " + (pub.getSysCheckState() ? " " : dbl.sqlString(this.checkUserCode) + " ") + " ," +
                " FCheckTime = " + dbl.sqlString(this.checkTime + " ") +
                " where FSecurityCode =" + dbl.sqlString(this.oldSecurityCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改信用评级信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String getAllSetting() {
        return "";
    }

    public IDataSetting getSetting() {
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        //save multiprous Result;
        String strSql = "";
        String[] strAry = null;
        Connection conn = null;
        ResultSet rs = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strAry = sMutilRowStr.split("\f\f");
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = " select * from " + pub.yssGetTableName("Tb_Para_CreditLevel") + " where FSecurityCode ='" + this.masterSecurityCode + "'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs != null) {
                    if (this.masterSecurityCode.trim().length() != 0 && this.masterSecurityCode.trim() != null) {
                        strSql = "delete from " + pub.yssGetTableName("Tb_Para_creditLevel") + " where FSecurityCode = '" +
                            this.masterSecurityCode + "'";
                        dbl.executeSql(strSql);
                    }
                }
            }
            for (int i = 0; i < strAry.length; i++) {
                this.parseRowStr(strAry[i]);
                strSql = "insert into " + pub.yssGetTableName("Tb_Para_CreditLevel") +
                    " (FSecurityCode,FCreditType,FOrgancode,FCreditLevel,FDesc,FCheckState," +
                    "FCreator,FCreateTime) values(" +
                    dbl.sqlString(this.securityCode) + ", " +
                    dbl.sqlString(this.creditType) + ", " +
                    dbl.sqlString(this.organCode) + ", " +
                    dbl.sqlString(this.creditLevelCode) + ", " +
                    dbl.sqlString(this.desc) + ", " + "'1'" + ", " +
                    dbl.sqlString(this.creatorCode + " ") + ", " +
                    dbl.sqlString(this.creatorTime + " ") + ")";

                dbl.executeSql(strSql);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("保存信用评级信息出错", e);
        }
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult += " where 1=1";

            if (this.filterType.securityCode.length() != 0) {
                sResult += " and a.FSecurityCode like '" +
                    this.filterType.securityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.securityCode.length() != 0) {
                sResult += " and a.FSecurityCode like '" +
                    this.filterType.securityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.organCode.length() != 0) {
                sResult += " and a.FOrganCode like '" +
                    this.filterType.organCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.creditType.length() != 0) {
                sResult += " and a.FCreditType like '" +
                    this.filterType.creditType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult += " and a.FDesc like '" +
                    this.filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }
}
