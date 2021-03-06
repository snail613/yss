package com.yss.main.cusreport;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class RepParamCtlGrpBean
    extends BaseDataSettingBean implements IDataSetting {

    private String CtlGrpCode = ""; //参数控件组代码
    private String CtlGrpName = ""; //参数控件组名称
    private String IsOnlyColumn = ""; //是否只显示列名

    private String RepParamCtrls; //参数控件

    private String oldCtlGrpCode = "";
    private RepParamCtlGrpBean filterType;
    private String sRecycled = null;

    public void setCtlGrpName(String CtlGrpName) {
        this.CtlGrpName = CtlGrpName;
    }

    public String getCtlGrpName() {
        return CtlGrpName;
    }

    public void setCtlGrpCode(String ctlGrpCode) {
        this.CtlGrpCode = ctlGrpCode;
    }

    public String getCtlGrpCode() {
        return this.CtlGrpCode;
    }

    public RepParamCtlGrpBean() {
    }

    /**
     * parseRowStr
     * 解析数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.RepParamCtrls = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.CtlGrpCode = reqAry[0];
            this.CtlGrpName = reqAry[1];
            if (YssFun.isNumeric(reqAry[2])) {
                super.checkStateId = Integer.parseInt(reqAry[2]);
            }
            this.oldCtlGrpCode = reqAry[3];
            this.IsOnlyColumn = reqAry[4];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new RepParamCtlGrpBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析参数控件组设置出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.CtlGrpCode).append("\t");
        buf.append(this.CtlGrpName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Rep_ParamCtlGrp"),
                               "FCtlGrpCode",
                               this.CtlGrpCode, this.oldCtlGrpCode);
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
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.* from " +
                pub.yssGetTableName("Tb_Rep_ParamCtlGrp") + " a " +
                " where a.FCheckState = 1 and a.FCtlGrpCode = " +
                dbl.sqlString(CtlGrpCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.CtlGrpCode = rs.getString("FCtlGrpCode") + "";
                this.CtlGrpName = rs.getString("FCtlGrpName") + "";
            }
            return null;
        } catch (Exception e) {
            throw new YssException("获取报表参数控件组设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = " where 1 = 1 ";
        if (this.IsOnlyColumn.equalsIgnoreCase("1")) {
            sResult += " and 1 = 2 ";
            return sResult;
        }
        if (this.filterType != null) {
            if (this.filterType.CtlGrpCode.length() != 0) {
                sResult = sResult + " and a.FCtlGrpCode like '" +
                    filterType.CtlGrpCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.CtlGrpName.length() != 0) {
                sResult = sResult + " and a.FCtlGrpName like '" +
                    filterType.CtlGrpName.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public void setRepAttr(ResultSet rs) throws SQLException {
        this.CtlGrpCode = rs.getString("FCtlGrpCode") + "";
        this.CtlGrpName = rs.getString("FCtlGrpName") + "";
        super.setRecLog(rs);
    }

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

                this.setRepAttr(rs);
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
            throw new YssException("获取参数控件组信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData1
     * 获取参数控件组数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql =
            "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Rep_ParamCtlGrp") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
//            修改 邱健 为使前台回收站也能够显示数据
//            " and a.FCheckState <> 2 order by a.FCheckState, a.FCreateTime desc";
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql =
            "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Rep_ParamCtlGrp") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    public String getListViewData2() throws YssException {
        String strSql = "";
        strSql =
            "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Rep_ParamCtlGrp") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            " and a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

        return this.builderListViewData(strSql);

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
     * addSetting
     * 新增参数控件组
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                "(FCtlGrpCode, FCtlGrpName, " +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.CtlGrpCode) + "," +
                dbl.sqlString(this.CtlGrpName) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";

            dbl.executeSql(strSql);

            if (this.RepParamCtrls != null) {
                RepParamCtlBean repParamCtl = new RepParamCtlBean();
                repParamCtl.setYssPub(pub);
                repParamCtl.saveMutliSetting(this.RepParamCtrls, true,
                                             this.CtlGrpCode);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) { 	//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FCtlGrpCode = " + dbl.sqlString(this.CtlGrpCode);
                    dbl.executeSql(strSql);

                    strSql = "update " + pub.yssGetTableName("Tb_Rep_ParamCtl") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FCtlGrpCode = " + dbl.sqlString(this.CtlGrpCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核参数控件组信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCtlGrpCode = " +
                dbl.sqlString(this.CtlGrpCode);

            dbl.executeSql(strSql);

            strSql = "update " + pub.yssGetTableName("Tb_Rep_ParamCtl") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCtlGrpCode = " +
                dbl.sqlString(this.CtlGrpCode);

            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除参数控件组信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                " set FCtlGrpCode = " +
                dbl.sqlString(this.CtlGrpCode) + ", FCtlGrpName = " +
                dbl.sqlString(this.CtlGrpName) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FCtlGrpCode = " +
                dbl.sqlString(this.oldCtlGrpCode);

            dbl.executeSql(strSql);

            if (this.RepParamCtrls != null && this.RepParamCtrls.length() > 0) {
                RepParamCtlBean repParamCtl = new RepParamCtlBean();
                repParamCtl.setYssPub(pub);
                repParamCtl.saveMutliSetting(this.RepParamCtrls, true,
                                             this.oldCtlGrpCode);
            } else {
                strSql = "delete from " + pub.yssGetTableName("Tb_Rep_ParamCtl") +
                    " where FCtlGrpCode = " +
                    dbl.sqlString(this.oldCtlGrpCode);
                dbl.executeSql(strSql);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改参数控件组信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {	//modify huangqirong 2012-07-01 findbugs
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                        " where FCtlGrpCode = " + dbl.sqlString(this.CtlGrpCode);
                    dbl.executeSql(strSql);

                    strSql = "delete from " + pub.yssGetTableName("Tb_Rep_ParamCtl") +
                        " where FCtlGrpCode = " + dbl.sqlString(this.CtlGrpCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除参数控件组信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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
