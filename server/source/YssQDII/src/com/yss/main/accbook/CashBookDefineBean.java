package com.yss.main.accbook;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import java.sql.*;
import com.yss.main.dao.IAccBookOper;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.*;

public class CashBookDefineBean
    extends BaseDataSettingBean implements IDataSetting {
    private String cBookCode = ""; //现金台帐定义代码
    private String cBookName = ""; //现金台帐定义名称
    private String bookLink = ""; //链接设置
    private String desc = ""; //定义描述
    private String oldCBookCode = "";

    private CashBookDefineBean filterType;
    private String sRecycled = ""; //保存未解析前的字符串

    public CashBookDefineBean() {
    }

    /**
     * parseRowStr
     * 解析现金台帐定义设置请求
     * @param sRowStr String
     */
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
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.cBookCode = reqAry[0];
            this.cBookName = reqAry[1];
            this.bookLink = reqAry[2];
            this.desc = reqAry[3];
            this.checkStateId = Integer.parseInt(reqAry[4]);
            this.oldCBookCode = reqAry[5];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CashBookDefineBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析现金台帐定义设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.cBookCode).append("\t");
        buf.append(this.cBookName).append("\t");
        buf.append(this.bookLink).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Book_Cash"),
                               "FCBookCode",
                               this.cBookCode, this.oldCBookCode);
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
    private String buildFilterSql() {
        String sResult = " where 1=1 ";
        if (this.filterType != null) {
            if (this.filterType.cBookCode.length() != 0) {
                sResult = sResult + " and a.FCBookCode like '" +
                    filterType.cBookCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.cBookName.length() != 0) {
                sResult = sResult + " and a.FCBookName like '" +
                    filterType.cBookName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.cBookName.length() != 0) {
                sResult = sResult + " and a.FBookLink like '" +
                    filterType.bookLink.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取所有现金台帐定义设置信息
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Book_Cash") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc"; // wdy modify 20070830

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.cBookCode = rs.getString("FCBookCode") + "";
                this.cBookName = rs.getString("FCBookName") + "";
                this.bookLink = rs.getString("FBookLink") + "";
                this.desc = rs.getString("FDesc") + "";
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_ACC_SETTLETYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取现金台帐定义信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的现金台帐定义设置信息
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "定义代码\t定义名称\t定义描述";
            strSql =
                "select a.*, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Book_Cash") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                buildFilterSql() +
                " and a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCBookCode") + "").trim()).
                    append("\t");
                bufShow.append( (rs.getString("FCBookName") + "").trim()).
                    append("\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(),
                                           50)).append(YssCons.YSS_LINESPLITMARK);

                this.cBookCode = rs.getString("FCBookCode") + "";
                this.cBookName = rs.getString("FCBookName") + "";
                this.bookLink = rs.getString("FBookLink") + "";
                this.desc = rs.getString("FDesc") + "";
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
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
            throw new YssException("获取可用现金台帐定义信息出错", e);
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

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " +
                pub.yssGetTableName("Tb_Book_Cash") +
                " where FCBookCode = " +
                dbl.sqlString(this.cBookCode);

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.cBookCode = rs.getString("FCBookCode") + "";
                this.cBookName = rs.getString("FCBookName") + "";
                this.bookLink = rs.getString("FBookLink") + "";
                this.desc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FcreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
            }
        } catch (Exception e) {
            throw new YssException("获取现金台帐定义信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            return null;
        }
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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * saveSetting
     *
     * @param btOper byte
     */
    public void saveSetting(byte btOper) {
    }

    /**
     * addSetting
     * 新增现金台帐定义数据
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Book_Cash") +
                "(FCBookCode,FCBookName,FBookLink,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.cBookCode) + "," +
                dbl.sqlString(this.cBookName) + "," +
                dbl.sqlString(this.bookLink) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增现金台帐定义数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkSetting
     * 功能：可以处理台帐管理业务中现金台帐的设置审核、反审核、和回收站的还原功能。
     *      还原功能可以同时处理多条信息
     * @throws YssException
     * 修改时间：2008年6月13号
     * 修改人：蒋春
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData.length == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Book_Cash") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FCBookCode = " +
                        dbl.sqlString(this.cBookCode);

                    dbl.executeSql(strSql);
                }
            } else {
                if (this.cBookCode != null && this.cBookCode.trim().length() > 0) {
                    strSql = "update " + pub.yssGetTableName("Tb_Book_Cash") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FCBookCode = " +
                        dbl.sqlString(this.cBookCode);

                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核现金台帐定义数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting
     * 删除现金台帐定义数据
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Book_Cash") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FCBookCode = " +
                dbl.sqlString(this.cBookCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除现金台帐定义数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editSetting
     * 修改现金台帐定义数据
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Book_Cash") +
                " set FCBookCode = " +
                dbl.sqlString(this.cBookCode) + ", FCBookName = " +
                dbl.sqlString(this.cBookName) + ", FBookLink = " +
                dbl.sqlString(this.bookLink) + ", FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FCBookCode = " +
                dbl.sqlString(this.oldCBookCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改现金台帐定义数据出错", e);
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
    public String getBeforeEditData() throws YssException {
        CashBookDefineBean befEditBean = new CashBookDefineBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Book_Cash") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " where  a.FCBookCode =" + dbl.sqlString(this.oldCBookCode) + // wdy 添加别名a
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.cBookCode = rs.getString("FCBookCode") + "";
                befEditBean.cBookName = rs.getString("FCBookName") + "";
                befEditBean.bookLink = rs.getString("FBookLink") + "";
                befEditBean.desc = rs.getString("FDesc") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Book_Cash") +
                        " where FCBookCode = " + dbl.sqlString(this.cBookCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
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
