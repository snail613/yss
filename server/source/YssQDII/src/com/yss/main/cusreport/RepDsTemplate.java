package com.yss.main.cusreport;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class RepDsTemplate
    extends BaseDataSettingBean implements IDataSetting {
    private String templateCode = "";
    private String templateName = "";
    private String desc = "";
    private String oldTemplateCode = "";
    private RepDsTemplate filterType = null;
    public String getDesc() {
        return desc;
    }

    public RepDsTemplate getFilterType() {
        return filterType;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setOldTemplateCode(String oldTemplateCode) {
        this.oldTemplateCode = oldTemplateCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setFilterType(RepDsTemplate filterType) {
        this.filterType = filterType;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getOldTemplateCode() {
        return oldTemplateCode;
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Rep_DsTemplate"),
                               "FRepDsTplCode",
                               this.templateCode,
                               this.oldTemplateCode);
    }

    public void checkSetting() throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Rep_DsTemplate") +
                " set FCheckstate =" + this.checkStateId + "," +
                " FCheckUser='" + pub.getUserCode() + " '," +
                " FCheckTime='" + YssFun.formatDatetime(new java.util.Date()) +
                " ' " +
                " where FRepDsTplCode =" +
                dbl.sqlString(this.oldTemplateCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("错误信息", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String addSetting() throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Rep_DsTemplate") +
                "(FRepDsTplCode,FRepDsTplName,FDesc,FCheckState,FCreator,FCreateTime) values(" +
                dbl.sqlString(this.templateCode) + " , " +
                dbl.sqlString(this.templateName) + " , " +
                dbl.sqlString(this.desc) + " , " +
                (pub.getSysCheckState() ? "0" : "1") + " ," +
                dbl.sqlString(this.creatorCode + " ") + " ," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增报表数据源模板设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String editSetting() throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Rep_DsTemplate") +
                " set FRepDsTplCode = " + dbl.sqlString(this.templateCode) + " ," +
                " FRepDsTplName = " + dbl.sqlString(this.templateName) + " ," +
                " FDesc = " + dbl.sqlString(this.desc) + " ," +
                " FCheckState = " + (pub.getSysCheckState() ? "0" : "1") + " ," +
                //    " FCreator = '" +
                //    (pub.getSysCheckState() ? " " :
                //     this.creatorCode + " ") + "' ," +
                //    " FCreateTime = " + dbl.sqlString(this.creatorTime + " ") + " ," +
                " FCheckUser = '" +
                (pub.getSysCheckState() ? " " :
                 this.checkUserCode + " ") + "' ," +
                " FCheckTime = " + dbl.sqlString(this.checkTime + " ") +
                " where FRepDsTplCode =" + dbl.sqlString(this.oldTemplateCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改报表数据源模板设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Rep_DsTemplate") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode() + " ") +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FRepDsTplCode = " +
                dbl.sqlString(this.oldTemplateCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除报表数据源模板设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String getOperValue(String sType) throws YssException {
        String str = "";
        return str;
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String dataSource[] = null;
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
            this.templateCode = reqAry[0];
            this.templateName = reqAry[1];
            this.desc = reqAry[2];
            if (this.desc.trim().length() == 0) {
                this.desc = " ";
            }
            this.oldTemplateCode = reqAry[3];
            this.checkStateId = Integer.parseInt(reqAry[4]);
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RepDsTemplate();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析报表数据源模板设置出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.templateCode).append("\t");
        buf.append(this.templateName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();

            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Rep_DsTemplate") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode and a.FCheckState<>2 " +
                this.FilterSql() +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
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
            throw new YssException("获取报表数据源模板设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    private String FilterSql() throws YssException {
        String Sql = "";
        if (this.filterType != null) {
            Sql += " where 1=1";
            if (this.filterType.templateCode.length() != 0) {
                Sql += " and a.FRepDsTplCode like '" + this.filterType.templateCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.templateName.length() != 0) {
                Sql += " and a.FRepDsTplName like '" + this.filterType.templateName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                Sql += " and a.FDesc like '" + this.filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return Sql;
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.templateCode = rs.getString("FRepDsTplCode") + "";
        this.templateName = rs.getString("FRepDsTplName") + "";
        this.desc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
    }

    public String getListViewData2() throws YssException {
        String str = "";
        return str;
    }

    public String getListViewData3() throws YssException {
        String str = "";
        return str;
    }

    public String getListViewData4() throws YssException {
        String str = "";
        return str;
    }

    public RepDsTemplate() {
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

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr) {
        return "";
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
