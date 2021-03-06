package com.yss.main.taoperation;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class TAPortTypeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String portTypeCode;
    private String portTypeName;
    private String oldPortTypeCode;
    private String desc;
    private TAPortTypeBean filterType;
    private String sRecycled = null; //保存未解析前的字符串

    public TAPortTypeBean() {
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_TA_PortType"),
                               "FPortTypeCode",
                               this.portTypeCode,
                               this.oldPortTypeCode);

    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " insert into " + pub.yssGetTableName("Tb_TA_PortType") +
                " (FPortTypeCode, FPortTypeName,FDesc, FCheckState,FCreator,FCreateTime) values("
                + dbl.sqlString(this.portTypeCode) + ","
                + dbl.sqlString(this.portTypeName) + ","
                + dbl.sqlString(this.desc) + ","
                + (pub.getSysCheckState() ? "0" : "1") + ","
                + dbl.sqlString(this.creatorCode) + ","
                + dbl.sqlString(this.creatorTime) + ")"; //BugNo:0000310 4 edit by jc
            //(pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorTime)) +
            //")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加TA组合设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("Tb_TA_PortType") +
                " set FPortTypeCode=" + dbl.sqlString(this.portTypeCode) + "," +
                " FPortTypeName=" + dbl.sqlString(this.portTypeName) + "," +
                " FDesc=" + dbl.sqlString(this.desc) + "," +
                " FCheckState=" + this.checkStateId +
                //BugNo:0000310 4 edit by jc
                ", FCreateTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCreator = " +
                dbl.sqlString(pub.getUserCode()) +
                //------------------------jc
                " where FPortTypeCode=" + dbl.sqlString(this.oldPortTypeCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改TA组合设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("Tb_TA_PortType") +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FPortTypeCode=" + dbl.sqlString(this.oldPortTypeCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除TA组合设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
			/**shashijie 2012-7-2 STORY 2475 */
            if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = " update " + pub.yssGetTableName("Tb_TA_PortType") +
                        " set FCheckState=" + this.checkStateId +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "', FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + //BugNo:0000310 4 edit by jc
                        " where FPortTypeCode=" +
                        dbl.sqlString(this.oldPortTypeCode);
                    dbl.executeSql(strSql);
                    conn.commit();
                }

            }
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核TA组合设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
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

    private void setPortTypeAttr(ResultSet rs) throws SQLException, YssException {
        this.portTypeCode = rs.getString("FPortTypeCode");
        this.portTypeName = rs.getString("FPortTypeName");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);

    }

    private String FilterStr() {
        String str = "";
        if (this.filterType != null) {
            str = " where 1=1 ";
			/**shashijie 2012-7-2 STORY 2475 */
            if (this.filterType.portTypeCode != null &&
            		this.filterType.portTypeCode.length() != 0) {
                str += " and a.FPortTypeCode like '" +
                    this.filterType.portTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.portTypeName != null &&
            		this.filterType.portTypeName.length() != 0) {
                str += " and a.FPortTypeName like '" +
                    this.filterType.portTypeName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc != null && this.filterType.desc.length() != 0) {
                str += " and a.FDesc like '" +
                    this.filterType.desc.replaceAll("'", "''") + "%'";
            }
			/**end*/
        }
        return str;
    }

    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                " select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName " +
                " from " + pub.yssGetTableName("Tb_TA_PortType") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                this.FilterStr() +
                " order by a.FCheckState , a.FCheckTime desc, a.FCreateTime desc ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setPortTypeAttr(rs);
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
            throw new YssException("获取TA组合设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "TA组合代码\tTA组合名称";
            strSql =
                " select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName " +
                " from " + pub.yssGetTableName("Tb_TA_PortType") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                "  where a.FCheckState=1 order by a.FCheckState ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FPortTypeCode")).append("\t").append(
                    rs.getString("FPortTypeName"))
                    .append(YssCons.YSS_LINESPLITMARK);

                setPortTypeAttr(rs);
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
            throw new YssException("获取TA组合设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.portTypeCode = reqAry[0];
            this.portTypeName = reqAry[1];
            this.desc = reqAry[2];
            this.checkStateId = Integer.parseInt(reqAry[3]);
            this.oldPortTypeCode = reqAry[4];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TAPortTypeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析TA组合设置出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.portTypeCode).append("\t");
        buf.append(this.portTypeName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void setPortTypeCode(String portTypeCode) {
        this.portTypeCode = portTypeCode;
    }

    public void setPortTypeName(String portTypeName) {
        this.portTypeName = portTypeName;
    }

    public void setOldPortTypeCode(String oldPortTypeCode) {
        this.oldPortTypeCode = oldPortTypeCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setFilterType(TAPortTypeBean filterType) {
        this.filterType = filterType;
    }

    public String getPortTypeCode() {
        return portTypeCode;
    }

    public String getPortTypeName() {
        return portTypeName;
    }

    public String getOldPortTypeCode() {
        return oldPortTypeCode;
    }

    public String getDesc() {
        return desc;
    }

    public TAPortTypeBean getFilterType() {
        return filterType;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String[] arrData = null;
        String sql = null;
        Connection conn = null;
        boolean bTrans = false; //是否开始事务标志
        try {
            conn = dbl.loadConnection();
			/**shashijie 2012-7-2 STORY 2475 */
            if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                //设置事务
                bTrans = true;
                conn.setAutoCommit(false);
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    sql = "delete from " + this.pub.yssGetTableName("Tb_TA_PortType") +
                        " where FPortTypeCode = " +
                        this.dbl.sqlString(this.oldPortTypeCode);
                    dbl.executeSql(sql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            this.dbl.endTransFinal(conn, bTrans);
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
