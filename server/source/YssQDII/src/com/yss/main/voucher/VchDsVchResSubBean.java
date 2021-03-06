package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class VchDsVchResSubBean
    extends BaseDataSettingBean implements IDataSetting {
    ///凭证资源表
    private String resTabName = ""; //资源表名
    private String resTabDesc = ""; //资源表描述
    private String fieldName = ""; //字段名
    private String fieldDesc = ""; //字段描述
    private int relaField = 0; //关联字段
    private String fieldType = ""; //字段类型
    private String desc = "";
    private String oldResTabName = "";
    private String oldFieldName = "";
    private VchDsVchResSubBean filterType = null;
    public String getDesc() {
        return desc;
    }

    public String getOldResTabName() {
        return oldResTabName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getRelaField() {
        return relaField;
    }

    public VchDsVchResSubBean getFilterType() {
        return filterType;
    }

    public String getResTabDesc() {
        return resTabDesc;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public String getOldFieldName() {
        return oldFieldName;
    }

    public void setResTabName(String resTabName) {
        this.resTabName = resTabName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldResTabName(String oldResTabName) {
        this.oldResTabName = oldResTabName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setRelaField(int relaField) {
        this.relaField = relaField;
    }

    public void setFilterType(VchDsVchResSubBean filterType) {
        this.filterType = filterType;
    }

    public void setResTabDesc(String resTabDesc) {
        this.resTabDesc = resTabDesc;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public void setOldFieldName(String oldFieldName) {
        this.oldFieldName = oldFieldName;
    }

    public String getResTabName() {
        return resTabName;
    }

    public String getOperValue(String sType) {
        return "";
    }

    public void checkInput(byte bType) throws YssException {
        dbFun.checkInputCommon(bType,
                               pub.yssGetTableName("Tb_Vch_ResTab"),
                               "FResTabName,FFieldName",
                               this.resTabName + "," + this.fieldName,
                               this.oldResTabName + "," + this.oldFieldName);
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\f") >= 0) {
                sTmpStr = sRowStr.split("\r\f")[0];
            } else {
                sTmpStr = sRowStr;

            }
            reqAry = sTmpStr.split("\t");
            this.resTabName = reqAry[0];
            if (reqAry[0].length() == 0) {
                this.resTabName = " ";
            }
            this.resTabDesc = reqAry[1];
            if (reqAry[1].length() == 0) {
                this.resTabDesc = " ";
            }
            this.fieldName = reqAry[2];
            if (reqAry[2].length() == 0) {
                this.fieldName = " ";
            }
            this.fieldDesc = reqAry[3];
            this.relaField = Integer.parseInt(reqAry[4]);
            this.fieldType = reqAry[5];
            this.desc = reqAry[6];
            this.oldResTabName = reqAry[7];
            this.oldFieldName = reqAry[8];
            this.checkStateId = Integer.parseInt(reqAry[9]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchDsVchResSubBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析凭证资源信息出错!");
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.resTabName).append("\t");
        buf.append(this.resTabDesc).append("\t");
        buf.append(this.fieldName).append("\t");
        buf.append(this.fieldDesc).append("\t");
        buf.append(this.relaField).append("\t");
        buf.append(this.fieldType).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.oldResTabName).append("\t");
        buf.append(this.oldFieldName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkSetting() throws YssException {

    }

    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = "delete from " + pub.yssGetTableName("Tb_Vch_ResTab") +
                " where FResTabName =" + dbl.sqlString(this.resTabName) + " and FFieldName=' ' or FFieldName= " +
                dbl.sqlString(this.oldFieldName) +
                " and FResTabName =" + dbl.sqlString(this.resTabName);
            dbl.executeSql(sqlStr);
            sqlStr = "insert into " + pub.yssGetTableName("Tb_Vch_ResTab") +
                "(FResTabName,FResTabDesc,FFieldName,FFieldDesc," +
                "FRelaField,FFieldType,FDesc,FCheckState,FCreator,FCreateTime) values(" +
                dbl.sqlString(this.resTabName) + "," +
                dbl.sqlString(this.resTabDesc) + "," +
                dbl.sqlString(this.fieldName) + "," +
                dbl.sqlString(this.fieldDesc) + "," +
                this.relaField + "," +
                dbl.sqlString(this.fieldType) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(creatorCode)) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlDate(new java.util.Date())) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return this.buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增资源凭证数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String editSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        int iRow = 0;
        try {
            conn = dbl.loadConnection();
            sqlStr = "select count(*) from " + pub.yssGetTableName("Tb_Vch_ResTab") +
                " where FResTabName=" + dbl.sqlString(this.oldResTabName) +
                " and FFieldName=" + dbl.sqlString(this.oldFieldName);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                iRow = rs.getInt(1);
            }
            if (iRow > 0) {
                sqlStr = "update " + pub.yssGetTableName("Tb_Vch_ResTab") +
                    " set FresTabName=" + dbl.sqlString(this.resTabName) + "," +
                    " FResTabDesc=" + dbl.sqlString(this.resTabDesc) + "," +
                    " FFieldName=" + dbl.sqlString(this.fieldName) + "," +
                    " FFieldDesc=" + dbl.sqlString(this.fieldDesc) + "," +
                    " FRelaField=" + this.relaField + "," +
                    " FFieldType=" + dbl.sqlString(this.fieldType) + "," +
                    " FDesc=" + dbl.sqlString(this.desc) +
                    " where FResTabName=" + dbl.sqlString(this.oldResTabName) +
                    " and FFieldName=" + dbl.sqlString(this.oldFieldName);
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(sqlStr);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return this.buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改资源凭证数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void delSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        int iRow = 0;
        ResultSet rs = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = "select count(*) from " + pub.yssGetTableName("Tb_Vch_ResTab") +
                " where FResTabName=" + dbl.sqlString(this.oldResTabName);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                iRow = rs.getInt(1);
            }
            if (iRow > 1) {
                sqlStr = "delete from " + pub.yssGetTableName("Tb_Vch_ResTab") +
                    " where FResTabName=" + dbl.sqlString(this.oldResTabName) +
                    " and FFieldName =" + dbl.sqlString(this.oldFieldName);
            } else {
                sqlStr = "update " + pub.yssGetTableName("Tb_Vch_ResTab") +
                    " set FFieldName=' ' where FResTabName=" + dbl.sqlString(this.oldResTabName);
            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除资源凭证数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.resTabName = rs.getString("FResTabName");
        this.resTabDesc = rs.getString("FResTabDesc");
        this.fieldName = rs.getString("FFieldName");
        this.fieldDesc = rs.getString("FFieldDesc");
        this.relaField = rs.getInt("FRelaField");
        this.fieldType = rs.getString("FFieldType");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sqlStr = "";
        if (this.filterType != null) {
            //sqlStr+=" where 1=1 and Fcheckstate<>2";
            sqlStr += " where 1=1 "; //将删除的数据也显示到前台 by leeyu 2008-10-27 BUG:0000491
            if (this.filterType.resTabName != null) {
                sqlStr += " and FResTabName =" + dbl.sqlString(this.filterType.resTabName);
            }
        }
        return sqlStr;
    }

    public String getListViewData1() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sDateStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "字段名称\t字段描述";
            strSql =
                "select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Vch_ResTab") + " a"
                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode"
                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode"
                + this.buildFilterSql()
                + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FFieldName")).append("\t");
                bufShow.append(rs.getString("FFieldDesc")).append(YssCons.YSS_LINESPLITMARK);
                if (rs.getString("FFieldName").trim().length() == 0) {
                    bufShow.delete(0, bufShow.length());
                }
                this.setResultSetAttr(rs);
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            // +"\r\f" +
            //this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取资源凭证数据信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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

    public String getBeforeEditData() {
        return "";
    }

    public String getAllSetting() {
        return "";
    }

    public IDataSetting getSetting() {
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = null;
        String sql = "";
        String[] ary;
        try {
            conn = dbl.loadConnection();
            ary = sMutilRowStr.split("\f\f");
            sql = "delete from " + pub.yssGetTableName("Tb_Vch_ResTab") +
                " where FFieldName =" + dbl.sqlString(this.resTabName);
            dbl.executeSql(sql);
            for (int i = 0; i < ary.length; i++) {
                this.parseRowStr(ary[i].toString());
                this.addSetting();
            }
        } catch (Exception e) {
            throw new YssException("保存数据出错", e);
        } finally {
            dbl.endTransFinal(conn, false);
        }
        return "";
    }

    public VchDsVchResSubBean() {
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
