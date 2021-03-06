package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

//Tb_XXX_Para_CtlSet参数类型设置
public class CtlSetBean
    extends BaseDataSettingBean implements IDataSetting {
    private String orderCode = ""; //排序编号
    private String paraSetCode = ""; //参数类型代码
    private String paraSetName = ""; //参数类型名称
    private String ctlGrpCode = ""; //控件组代码
    private String ctlCode = ""; //控件代码
    private String ctlInd = ""; //控件标识
    private String ctlValue = ""; //控件值
    private String desc = ""; //描述
    private String oldOrderCode = "";
    private String oldParaSetCode = "";
    private String oldCtlGrpCode = "";
    private String oldCtlCode = "";
    private String strHashTable = "";
    private CtlSetBean filterType = null;

    public CtlSetBean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "参数类型代码\t参数类型名称\t控件组代码";
            strSql =
                "select distinct(FParaSetCode),FParaSetName,FCtlGrpCode from " +
                pub.yssGetTableName("Tb_Para_CtlSet") + buildFilterSql();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FParaSetCode") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FParaSetName") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FCtlGrpCode") + "").trim());
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.paraSetCode = rs.getString("FParaSetCode");
                this.paraSetName = rs.getString("FParaSetName");
                this.ctlGrpCode = rs.getString("FCtlGrpCode");
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取参数类型设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "参数类型代码\t参数类型名称\t控件组代码";
            strSql = "select FParaSetCode,FParaSetName,FCtlGrpCode from " +
                pub.yssGetTableName("Tb_Para_CtlSet") +
                " where 1=2 ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FParaSetCode") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FParaSetName") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FCtlGrpCode") + "").trim());
                bufShow.append(YssCons.YSS_LINESPLITMARK);
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
        } catch (Exception e) {
            throw new YssException("获取参数类型设置表头出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        return "";
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    public void setManagerAttr(ResultSet rs) throws SQLException {
        this.orderCode = rs.getString("FOrderCode");
        this.paraSetCode = rs.getString("FParaSetCode");
        this.paraSetName = rs.getString("FParaSetName");
        this.ctlGrpCode = rs.getString("FCtlGrpCode");
        this.ctlCode = rs.getString("FCtlCode");
        this.ctlInd = rs.getString("FCtlInd");
        this.ctlValue = rs.getString("FCtlValue");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);
    }

    /**
     * buildFilterSql
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.oldParaSetCode.length() != 0) {
                sResult = sResult + " and FParaSetCode = '" +
                    filterType.oldParaSetCode.replaceAll("'", "''") + "'";
            }
        }
        return sResult;
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        String sqlString = "";
        ResultSet rs = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            if (this.strHashTable.length() > 1) {
                String[] strs = strHashTable.split("\r\n");
                for (int i = 0; i < strs.length; i++) {
                    CtlSetBean ctlSetBean = new CtlSetBean();
                    ctlSetBean.setYssPub(pub);
                    ctlSetBean.parseRowStr(strs[i]);
                    conn.setAutoCommit(false);
                    bTrans = true;
                    strSql = "insert into " + pub.yssGetTableName("Tb_Para_CtlSet") +
                        " (FOrderCode,FParaSetCode,FParaSetName,FCtlGrpCode,FCtlCode," +
                        "FCtlInd,FCtlValue,FDesc,FCheckState,FCreator,FCreateTime) values(" +
                        dbl.sqlString(ctlSetBean.orderCode) + "," +
                        dbl.sqlString(ctlSetBean.paraSetCode) + "," +
                        dbl.sqlString(ctlSetBean.paraSetName) + "," +
                        dbl.sqlString(ctlSetBean.ctlGrpCode) + "," +
                        dbl.sqlString(ctlSetBean.ctlCode) + "," +
                        dbl.sqlString(ctlSetBean.ctlInd) +
                        dbl.sqlString(ctlSetBean.ctlValue) + "," +
                        dbl.sqlString(ctlSetBean.desc) + "," +
                        (pub.getSysCheckState() ? "0" : "1") + "," +
                        (pub.getSysCheckState() ? "' '" : this.creatorCode) + "," +
                        "'" + YssFun.formatDatetime(new java.util.Date()) + "')";
                    dbl.executeSql(strSql);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
            } else {
                sqlString = "select * from Tb_PFSys_FaceCfgInfo where FCtlGrpCode = " +
                    dbl.sqlString(this.ctlGrpCode);
                rs = dbl.openResultSet(sqlString);
                while (rs.next()) {
                    conn.setAutoCommit(false);
                    bTrans = true;
                    strSql = "insert into " + pub.yssGetTableName("Tb_Para_CtlSet") +
                        " (FOrderCode,FParaSetCode,FParaSetName,FCtlGrpCode,FCtlCode," +
                        "FCtlInd,FCtlValue,FDesc,FCheckState,FCreator,FCreateTime) values(" +
                        dbl.sqlString( ( (orderCode.length() == 0) ? " " :
                                        this.orderCode)) + "," +
                        dbl.sqlString(this.paraSetCode) + "," +
                        dbl.sqlString(this.paraSetName) + "," +
                        dbl.sqlString(this.ctlGrpCode) + "," +
                        dbl.sqlString(rs.getString("FCtlCode")) + "," +
                        dbl.sqlString( (rs.getString("FCtlInd") == null) ? " " :
                                      rs.getString("FCtlInd")) + "," +
                        dbl.sqlString(this.ctlValue) + "," +
                        dbl.sqlString(this.desc) + "," +
                        (pub.getSysCheckState() ? "0" : "1") + "," +
                        (pub.getSysCheckState() ? "' '" : this.creatorCode) + "," +
                        "'" + YssFun.formatDatetime(new java.util.Date()) + "')";
                    dbl.executeSql(strSql);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
            }
        } catch (Exception e) {
            throw new YssException("增加参数类型设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Para_CtlSet"),
                               "FOrderCode,FParaSetCode,FCtlGrpCode,FCtlCode",
                               orderCode + "," + paraSetCode + "," + ctlGrpCode +
                               "," + ctlCode,
                               oldOrderCode + "," + oldParaSetCode + "," +
                               oldCtlGrpCode + "," + oldCtlCode);
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "update " + pub.yssGetTableName("Tb_Para_CtlSet") +
                " set FCheckState=" + this.checkStateId + "," +
                " FCreator=" + dbl.sqlString(this.checkUserCode + " ") + "," +
                " FCreateTime=" + dbl.sqlString(this.checkTime + " ") +
                " where FParaSetCode=" + dbl.sqlString(this.paraSetCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核凭证生成方案设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "update " + pub.yssGetTableName("Tb_Para_CtlSet") +
                " set FCheckState=" + this.checkStateId + "," +
                " FCreator=" + dbl.sqlString(this.checkUserCode + " ") + "," +
                " FCreateTime=" + dbl.sqlString(this.checkTime + " ") +
                " where FParaSetCode=" + dbl.sqlString(this.paraSetCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除参数类型设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "delete from " + pub.yssGetTableName("Tb_Para_CtlSet") +
                " where FParaSetCode=" + dbl.sqlString(this.oldParaSetCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            addSetting();
        } catch (Exception e) {
            throw new YssException("修改参数类型设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.orderCode).append("\t");
        buf.append(this.paraSetCode).append("\t");
        buf.append(this.paraSetName).append("\t");
        buf.append(this.ctlGrpCode).append("\t");
        buf.append(this.ctlCode).append("\t");
        buf.append(this.ctlInd).append("\t");
        buf.append(this.ctlValue).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
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
            this.orderCode = reqAry[0];
            this.paraSetCode = reqAry[1];
            this.paraSetName = reqAry[2];
            this.ctlGrpCode = reqAry[3];
            this.ctlCode = reqAry[4];
            this.ctlInd = reqAry[5];
            this.ctlValue = reqAry[6];
            this.desc = reqAry[7];
            this.oldOrderCode = reqAry[8];
            this.oldParaSetCode = reqAry[9];
            this.oldCtlGrpCode = reqAry[10];
            this.oldCtlCode = reqAry[11];
            super.checkStateId = Integer.parseInt(reqAry[12]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new CtlSetBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
            if (sRowStr.indexOf("\r\f") > 0) {
                this.strHashTable = sRowStr.split("\r\f")[1];
            }
        } catch (Exception e) {
            throw new YssException("解析参数类型设置出错", e);
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    public String getCtlCode() {
        return ctlCode;
    }

    public String getCtlGrpCode() {
        return ctlGrpCode;
    }

    public String getCtlInd() {
        return ctlInd;
    }

    public String getCtlValue() {
        return ctlValue;
    }

    public String getDesc() {
        return desc;
    }

    public String getOldCtlCode() {
        return oldCtlCode;
    }

    public String getOldCtlGrpCode() {
        return oldCtlGrpCode;
    }

    public String getOldOrderCode() {
        return oldOrderCode;
    }

    public String getOldParaSetCode() {
        return oldParaSetCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getParaSetCode() {
        return paraSetCode;
    }

    public String getParaSetName() {
        return paraSetName;
    }

    public void setParaSetName(String paraSetName) {
        this.paraSetName = paraSetName;
    }

    public void setParaSetCode(String paraSetCode) {
        this.paraSetCode = paraSetCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setOldParaSetCode(String oldParaSetCode) {
        this.oldParaSetCode = oldParaSetCode;
    }

    public void setOldOrderCode(String oldOrderCode) {
        this.oldOrderCode = oldOrderCode;
    }

    public void setOldCtlGrpCode(String oldCtlGrpCode) {
        this.oldCtlGrpCode = oldCtlGrpCode;
    }

    public void setOldCtlCode(String oldCtlCode) {
        this.oldCtlCode = oldCtlCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCtlValue(String ctlValue) {
        this.ctlValue = ctlValue;
    }

    public void setCtlInd(String ctlInd) {
        this.ctlInd = ctlInd;
    }

    public void setCtlGrpCode(String ctlGrpCode) {
        this.ctlGrpCode = ctlGrpCode;
    }

    public void setCtlCode(String ctlCode) {
        this.ctlCode = ctlCode;
    }

    public CtlSetBean getFilterType() {
        return filterType;
    }

    public void setFilterType(CtlSetBean filterType) {
        this.filterType = filterType;
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
