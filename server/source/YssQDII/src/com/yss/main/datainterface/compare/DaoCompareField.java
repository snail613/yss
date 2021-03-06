package com.yss.main.datainterface.compare;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * 接口核对配置字段POJO类
 * QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090430
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DaoCompareField
    extends BaseDataSettingBean implements
    IDataSetting {
    private String sCompCode = ""; //配置源代码
    private String sCompName = "";
    private String sFieldCode = "";
    private String sFieldName = "";
    private double dRangeMin = 0;
    private double dRangeMax = 0;
    private int iAccountType = 0;
    private int iPKField = 0;
    private int iGroupField = 0;
    private int iGroupTitleField = 0;
    private int iRogatoryField = 0;
    private String sFieldType = ""; //字段类型
    private String sFieldPre = ""; //字段精度
    private String sGroupFieldMark = "";
    private String sOldCompCode = "";
    private String sRecycled = "";
    private DaoCompareField filterType;

    public DaoCompareField() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.sRecycled = sRowStr;
            this.sCompCode = reqAry[0];
            this.sFieldCode = reqAry[1];
            this.sFieldName = reqAry[2];
            if (YssFun.isNumeric(reqAry[3])) {
                this.dRangeMax = YssFun.toDouble(reqAry[3]);
            }
            if (YssFun.isNumeric(reqAry[4])) {
                this.dRangeMin = YssFun.toDouble(reqAry[4]);
            }
            if (YssFun.isNumeric(reqAry[5])) {
                this.iAccountType = YssFun.toInt(reqAry[5]);
            }
            if (YssFun.isNumeric(reqAry[6])) {
                this.iPKField = YssFun.toInt(reqAry[6]);
            }
            if (YssFun.isNumeric(reqAry[7])) {
                this.iGroupField = YssFun.toInt(reqAry[7]);
            }
            if (YssFun.isNumeric(reqAry[8])) {
                this.iGroupTitleField = YssFun.toInt(reqAry[8]);
            }
            if (YssFun.isNumeric(reqAry[9])) {
                this.iRogatoryField = YssFun.toInt(reqAry[9]);
            }
            if (YssFun.isNumeric(reqAry[10])) {
                this.checkStateId = YssFun.toInt(reqAry[10]);
            }
            this.sFieldType = reqAry[11]; //xuqiji 更改数组下标，原因数据库表中一字段删除 2009 03 23
            this.sFieldPre = reqAry[12];
            this.sOldCompCode = reqAry[13]; //--------------
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoCompareField();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析预处理接口信息出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sCompCode).append("\t");
        buf.append(this.sCompName).append("\t");
        buf.append(this.sFieldCode).append("\t");
        buf.append(this.sFieldName).append("\t");
        buf.append(this.iAccountType).append("\t");
        buf.append(this.iGroupField).append("\t");
        buf.append(this.iGroupTitleField).append("\t");
        buf.append(this.iPKField).append("\t");
        buf.append(this.iRogatoryField).append("\t");
        buf.append(this.dRangeMax).append("\t");
        buf.append(this.dRangeMin).append("\t");
        buf.append(this.sGroupFieldMark).append("\t");
        buf.append(this.sFieldType).append("\t");
        buf.append(this.sFieldPre).append("\t");
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "update " + pub.yssGetTableName("Tb_Dao_CompField") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCompCode =" + dbl.sqlString(this.sCompCode);
            dbl.executeSql(sqlStr);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("删除数据核对字段信息出错!", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void checkSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "update " + pub.yssGetTableName("Tb_Dao_CompField") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCompCode =" + dbl.sqlString(this.sCompCode);
            dbl.executeSql(sqlStr);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("审核数据核对字段信息出错!", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        PreparedStatement stm = null;
        String sqlStr = "";
        String[] arrData = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            int index = 1;
            sqlStr = "delete from " + pub.yssGetTableName("Tb_Dao_CompField") +
                " where FCOMPCODE=" + dbl.sqlString(this.sOldCompCode);
            dbl.executeSql(sqlStr);
            sqlStr = "insert into " + pub.yssGetTableName("Tb_Dao_CompField") +
                "(FCOMPCODE,FFIELDCODE,FFIELDNAME,FACCOUNTTYPE,FRANGEMIN,FRANGEMAX,FPKFIELD,FGROUPFIELD," +
                "FGROUPTITLEFIELD,FROGATORYFIELD,FCHECKSTATE,FCREATOR,FCREATETIME,FOrderIndex,FFieldType,FFieldPre) Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            bTrans = true;
            stm = dbl.openPreparedStatement(sqlStr);
            arrData = sMutilRowStr.split("\r\f");
            for (int i = 0; i < arrData.length; i++) {
                this.parseRowStr(arrData[i]);
                if (this.sCompCode.trim().length() == 0 || this.sFieldCode.trim().length() == 0) {
                    continue;
                }

                stm.setString(1, this.sCompCode);
                stm.setString(2, this.sFieldCode);
                stm.setString(3, this.sFieldName);
                stm.setInt(4, this.iAccountType);
                stm.setDouble(5, this.dRangeMin);
                stm.setDouble(6, this.dRangeMax);
                stm.setInt(7, this.iPKField);
                stm.setInt(8, this.iGroupField);
                stm.setInt(9, this.iGroupTitleField);
                stm.setInt(10, this.iRogatoryField);
                stm.setInt(11, this.checkStateId);
                stm.setString(12, this.creatorCode);
                stm.setString(13, this.creatorTime);
                stm.setInt(14, index++);
                stm.setString(15, this.sFieldType);
                stm.setString(16, this.sFieldPre);
                stm.addBatch();
            }
            stm.executeBatch();
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("保存数据核对字段信息出错！", ex);
        } finally {
            dbl.closeStatementFinal(stm);
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Dao_CompField") +
                " where FCompCode=" + dbl.sqlString(this.sCompCode) +
                " and FFIELDCODE=" + dbl.sqlString(this.sFieldCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.sCompCode = rs.getString("FCOMPCODE");
                this.sFieldCode = rs.getString("FFIELDCODE");
                this.sFieldName = rs.getString("FFIELDNAME");
                this.iAccountType = rs.getInt("FACCOUNTTYPE");
                this.iGroupField = rs.getInt("FGROUPFIELD");
                this.iGroupTitleField = rs.getInt("FGROUPTITLEFIELD");
                this.iPKField = rs.getInt("FPKFIELD");
                this.iRogatoryField = rs.getInt("FROGATORYFIELD");
                this.dRangeMax = rs.getDouble("FRANGEMAX");
                this.dRangeMin = rs.getDouble("FRANGEMIN");
                this.sFieldType = rs.getString("FFieldType");
                this.sFieldPre = rs.getString("FFieldPre");
            }
        } catch (Exception ex) {
            throw new YssException("获取单条数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;

    }

    public String getAllSetting() throws YssException {
        return "";
    }

    private String builerFilter() throws YssException {
        String filterSql = "";
        if (this.filterType != null) {
            filterSql = " where 1=1 ";
            if (filterType.sCompCode != null && filterType.sCompCode.trim().length() > 0) {
                filterSql += " and a.FCOMPCODE ='" + filterType.sCompCode.replaceAll("'", "''") + "'";
            }
        }
        return filterSql;
    }

    private void setFieldValue(ResultSet rs) throws SQLException {
        this.sCompCode = rs.getString("FCOMPCODE");
        this.sCompName = rs.getString("FCOMPNAME");
        this.sFieldCode = rs.getString("FFIELDCODE");
        this.sFieldName = rs.getString("FFIELDNAME");
        this.iAccountType = rs.getInt("FACCOUNTTYPE");
        this.iGroupField = rs.getInt("FGROUPFIELD");
        this.iGroupTitleField = rs.getInt("FGROUPTITLEFIELD");
        this.iPKField = rs.getInt("FPKFIELD");
        this.iRogatoryField = rs.getInt("FROGATORYFIELD");
        this.dRangeMax = rs.getDouble("FRANGEMAX");
        this.dRangeMin = rs.getDouble("FRANGEMIN");
        this.sGroupFieldMark = rs.getString("FGroupFieldMark");
        this.sFieldType = rs.getString("FFieldType");
        this.sFieldPre = rs.getString("FFieldPre");
    }

    public void deleteRecycleData() throws YssException {
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    public String getListViewData1() throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sShowQueue = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufQueue = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = getListView1Headers();
            sqlStr = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                "d.FCompName,case when a.FGroupField=1 then '√' else '' end as FGroupFieldMark from " +
                pub.yssGetTableName("Tb_Dao_CompField") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FCompCode,FCompName from " + pub.yssGetTableName("Tb_Dao_Compare") + ") d on a.FCompCode=d.FCompCode " +
                builerFilter() +
                " order by a.FOrderIndex , a.FCheckState";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                setFieldValue(rs);
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                if (rs.getInt("FRogatoryField") == 1) { //如果此字段为查询字段，就在查询Buf中添加
                    bufQueue.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                        append(YssCons.YSS_LINESPLITMARK);
                }
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            if (bufQueue.toString().length() > 2) {
                sShowQueue = bufQueue.toString().substring(0, bufQueue.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + sShowQueue;

        } catch (Exception e) {
            throw new YssException("获取接口数据核对字段设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String getSOldCompCode() {
        return sOldCompCode;
    }

    public String getSCompCode() {
        return sCompCode;
    }

    public void setFilterType(DaoCompareField filterType) {
        this.filterType = filterType;
    }

    public void setSOldCompCode(String sOldCompCode) {
        this.sOldCompCode = sOldCompCode;
    }

    public void setSCompCode(String sCompCode) {
        this.sCompCode = sCompCode;
    }

    public void setSRecycled(String sRecycled) {
        this.sRecycled = sRecycled;
    }

    public void setSGroupFieldMark(String sGroupFieldMark) {
        this.sGroupFieldMark = sGroupFieldMark;
    }

    public void setSFieldName(String sFieldName) {
        this.sFieldName = sFieldName;
    }

    public void setSFieldCode(String sFieldCode) {
        this.sFieldCode = sFieldCode;
    }

    public void setSCompName(String sCompName) {
        this.sCompName = sCompName;
    }

    public void setIRogatoryField(int iRogatoryField) {
        this.iRogatoryField = iRogatoryField;
    }

    public void setIPKField(int iPKField) {
        this.iPKField = iPKField;
    }

    public void setIGroupTitleField(int iGroupTitleField) {
        this.iGroupTitleField = iGroupTitleField;
    }

    public void setIGroupField(int iGroupField) {
        this.iGroupField = iGroupField;
    }

    public void setIAccountType(int iAccountType) {
        this.iAccountType = iAccountType;
    }

    public void setDRangeMin(double dRangeMin) {
        this.dRangeMin = dRangeMin;
    }

    public void setDRangeMax(double dRangeMax) {
        this.dRangeMax = dRangeMax;
    }

    public void setSFieldType(String sFieldType) {
        this.sFieldType = sFieldType;
    }

    public void setSFieldPre(String sFieldPre) {
        this.sFieldPre = sFieldPre;
    }

    public DaoCompareField getFilterType() {
        return filterType;
    }

    public String getSRecycled() {
        return sRecycled;
    }

    public String getSGroupFieldMark() {
        return sGroupFieldMark;
    }

    public String getSFieldName() {
        return sFieldName;
    }

    public String getSFieldCode() {
        return sFieldCode;
    }

    public String getSCompName() {
        return sCompName;
    }

    public int getIRogatoryField() {
        return iRogatoryField;
    }

    public int getIPKField() {
        return iPKField;
    }

    public int getIGroupTitleField() {
        return iGroupTitleField;
    }

    public int getIGroupField() {
        return iGroupField;
    }

    public int getIAccountType() {
        return iAccountType;
    }

    public double getDRangeMin() {
        return dRangeMin;
    }

    public double getDRangeMax() {
        return dRangeMax;
    }

    public String getSFieldType() {
        return sFieldType;
    }

    public String getSFieldPre() {
        return sFieldPre;
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
