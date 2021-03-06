package com.yss.main.voucher;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

//生成凭证链接 Tb_Vch_BuildLink
public class VchBuildLinkBean
    extends BaseDataSettingBean implements IDataSetting {
    private String projectCode = ""; //生成方案代码
    private String attrCode = ""; //属性代码;
    private String attrName = ""; //属性名称
    private String desc = ""; //描述
    private String oldProjectCode = "";
    private String oldAttrCode = "";
    private String[] listString;
    private ArrayList listObject = null;
    private VchBuildLinkBean filterType = null;

    public VchBuildLinkBean() {
    }

    private String builerFilter() {
        String reSql = "";
        if (this.filterType != null) {
            reSql = " where 1=1";
			/**shashijie 2012-7-2 STORY 2475 */
            if (this.filterType.projectCode != null &&
            		this.filterType.projectCode.length() > 0) {
                reSql += " and a.FProjectCode = '" +
                    this.filterType.projectCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.attrCode != null &&
            		this.filterType.attrCode.trim().length() > 0) {
                reSql += " and a.FAttrCode like '" +
                    this.filterType.attrCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.attrName != null &&
            		this.filterType.attrName.trim().length() > 0) {
                reSql += " and a.FAttrName like '" +
                    this.filterType.attrName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc != null && this.filterType.desc.trim().length() > 0) {
                reSql += " and a.FDesc like '" +
                    this.filterType.desc.replaceAll("'", "''") + "%'";
            }
			/**end*/
            if (this.filterType.checkStateId == 1) {
                reSql = reSql + " and a.FCheckState = 1 ";
            }
        }
        return reSql;
    }

    private void setVchAttr(ResultSet rs) throws SQLException {
        this.projectCode = rs.getString("FProjectCode");
        this.attrCode = rs.getString("FAttrCode");
        this.attrName = rs.getString("FAttrName");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = getListView1Headers();
            sqlStr =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Vch_BuildLink") +
                " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                builerFilter() +
                " order by a.FCheckState, a.FCreateTime desc, a.FCheckTime desc";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setVchAttr(rs);
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
                this.getListView1ShowCols() + "\n" + getListViewData2();
        } catch (Exception e) {
            throw new YssException("获取生成凭证链接出错!", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = getListView1Headers();
            sqlStr = "select b.*,d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Vch_Attr") +
                " b left join (select FUserCode,FUserName from Tb_Sys_UserList) d on b.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on b.FCheckUser = e.FUserCode " +
                " where not exists" +
                " (select * from " + pub.yssGetTableName("Tb_Vch_BuildLink") +
                " a where FProjectCode=" + dbl.sqlString(this.projectCode) +
                " and b.FAttrCode = a.FAttrCode)";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FAttrCode")).append("\t");
                bufShow.append(rs.getString("FAttrName")).append("\t");
                bufShow.append(rs.getString("FDesc")).append(YssCons.YSS_LINESPLITMARK);
                this.attrCode = rs.getString("FAttrCode");
                this.attrName = rs.getString("FAttrName");
                this.desc = rs.getString("FDesc");
                super.setRecLog(rs);
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
            throw new YssException("获取生成凭证链接出错!", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String strSql = "";
        try {
            conn = dbl.loadConnection();
            if (listObject != null) {
                for (int i = 0; i < listObject.size(); i++) {
                    VchBuildLinkBean vbl = (VchBuildLinkBean) listObject.get(i);
                    strSql = "insert into " + pub.yssGetTableName("Tb_Vch_BuildLink") +
                        " (FProjectCode,FAttrCode,FAttrName,FDesc,FCheckState,FCreator,FCreateTime) values(" +
                        dbl.sqlString(vbl.projectCode) + "," +
                        dbl.sqlString(vbl.attrCode) + "," +
                        dbl.sqlString(vbl.attrName) + "," +
                        dbl.sqlString(vbl.desc) + "," +
//                        (pub.getSysCheckState() ? "0" : "1") + "," +   
                        "1," + //edit by yanghaiming 20100809 对于新增的数据直接保存为已审核状态 MS01577 QDV4赢时胜上海2010年08月09日01_B 
                        (pub.getSysCheckState() ? "' '" : vbl.creatorCode) + "," +
                        "'" + YssFun.formatDatetime(new java.util.Date()) + "')";
                    conn.setAutoCommit(false);
                    bTrans = true;
                    dbl.executeSql(strSql);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
                listObject = null;
            }
        } catch (Exception e) {
            throw new YssException("新增生成凭证链接出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte bType) throws YssException {
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
            strSql = "update " + pub.yssGetTableName("Tb_Vch_BuildLink") +
                " set FCheckState = " + this.checkStateId + "," +
                " FCheckUser = " + dbl.sqlString(pub.getUserCode()) + "," +
                " FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FProjectCode=" + dbl.sqlString(this.projectCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核生成凭证链接出错", e);
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
            //执行删除方法时直接删除     edit by yanghaiming 20100809 MS01577 QDV4赢时胜上海2010年08月09日01_B
            strSql = "delete " + pub.yssGetTableName("Tb_Vch_BuildLink") +
//                " set FCheckState=" + this.checkStateId + "," +
//                " FCreator=" + dbl.sqlString(this.checkUserCode + " ") + "," +
//                " FCreateTime=" + dbl.sqlString(this.checkTime + " ") +
                " where FProjectCode=" + dbl.sqlString(this.oldProjectCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除生成凭证链接出错", e);
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
            strSql = "delete from " + pub.yssGetTableName("Tb_Vch_BuildLink") +
                " where FProjectCode=" + dbl.sqlString(this.oldProjectCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            addSetting();
        } catch (Exception e) {
            throw new YssException("修改生成凭证链接出错", e);
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
        buf.append(this.projectCode).append("\t");
        buf.append(this.attrCode).append("\t");
        buf.append(this.attrName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.checkStateId).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
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
     * parseRowStr
     *
     * @param sRowStr String
     */
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
                reqAry = sTmpStr.split("\t");
            }
          //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            if(reqAry == null)
            	return ;
            //---end---
            this.projectCode = reqAry[0];
            if (reqAry[0].length() == 0) {
                this.projectCode = " ";
            }
            this.attrCode = reqAry[1];
            if (reqAry[1].length() == 0) {
                this.attrCode = " ";
            }
            this.attrName = reqAry[2];
            if (reqAry[2].length() == 0) {
                this.attrName = " ";
            }
            this.desc = reqAry[3];
            this.oldProjectCode = reqAry[4];
            if (reqAry[4].length() == 0) {
                this.oldProjectCode = " ";
            }
            this.oldAttrCode = reqAry[5];
            if (reqAry[5].length() == 0) {
                this.oldAttrCode = " ";
            }
            this.checkStateId = Integer.parseInt(reqAry[6]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchBuildLinkBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
            if (sRowStr.indexOf("\r\n") > 0) {
                listString = sRowStr.split("\r\n");
                listObject = new ArrayList();
                for (int i = 1; i < listString.length; i++) {
                    VchBuildLinkBean vch = new VchBuildLinkBean();
                    vch.setYssPub(pub);
                    vch.parseRowStr(listString[i]);
                    listObject.add(vch);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析生成凭证链接出错!");
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

    public String getAttrCode() {
        return attrCode;
    }

    public String getAttrName() {
        return attrName;
    }

    public String getDesc() {
        return desc;
    }

    public VchBuildLinkBean getFilterType() {
        return filterType;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public void setFilterType(VchBuildLinkBean filterType) {
        this.filterType = filterType;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public String getOldAttrCode() {
        return oldAttrCode;
    }

    public String getOldProjectCode() {
        return oldProjectCode;
    }

    public void setOldProjectCode(String oldProjectCode) {
        this.oldProjectCode = oldProjectCode;
    }

    public void setOldAttrCode(String oldAttrCode) {
        this.oldAttrCode = oldAttrCode;
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
