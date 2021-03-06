package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title: DataRelaBean </p>
 * <p>Description: 接口配置规则设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ConfigureBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strCode = ""; //配置代码
    private String strName = ""; //配置名称
    private String strImpExp = "0"; //导入导出
    private String strFileType = ""; //文件类型
    private String strFilePath = ""; //文件路径
    private String strFileName = ""; //文件名称
    private String strSqlText = ""; //脚本代码
    private String strDesc = ""; //配置描述
    private String strTabName = ""; //关联系统表名

    private ConfigureBean filterType;

    private String configurerela;
    private String strOldCode;

    public ConfigureBean() {
    }

    /**
     * addSetting
     * 新增关系定义设置信息
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Dao_Configure") +
                " (FCfgCode, FCfgName, FInOut, " +
                " FFileType, FFilePath, FFileName, FSqlText, FDesc, FCreator, FCreateTime) " +
                " values(" + dbl.sqlString(this.strCode) + "," +
                dbl.sqlString(this.strName) + "," +
                this.strImpExp + "," +
                dbl.sqlString(this.strFileType) + "," +
                dbl.sqlString(this.strFilePath) + "," +
                dbl.sqlString(this.strFileName) + "," +
                dbl.sqlString(this.strSqlText) + "," +
                dbl.sqlString(this.strDesc) + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            if (this.configurerela != null) {
                ConfigureRelaBean configurerela = new ConfigureRelaBean();
                configurerela.setYssPub(pub);
                configurerela.saveMutliSetting(this.configurerela);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增关系定义设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     * 删除关系定义设置信息
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "delete from " + pub.yssGetTableName("Tb_Dao_Configure") +
                " where FCfgCode = " + dbl.sqlString(this.strCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            strSql = "delete from " + pub.yssGetTableName("Tb_Dao_Configure_Rela") +
                " where FCfgCode = " + dbl.sqlString(this.strCode);
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除关系定义设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editSetting
     * 修改关系定义设置信息
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Dao_Configure") +
                "  set FCfgCode = " +
                dbl.sqlString(this.strCode) + ", FCfgName = "
                + dbl.sqlString(this.strName) + ",FInOut = " +
                this.strImpExp + ",FFileType = "
                + dbl.sqlString(this.strFileType) + ", FFilePath = "
                + dbl.sqlString(this.strFilePath) + ", FFileName = " +
                dbl.sqlString(this.strFileName) + ", FSqlText = " +
                dbl.sqlString(this.strSqlText) + ", FDesc=" +
                dbl.sqlString(this.strDesc) + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                dbl.sqlString(this.creatorTime) +
                " where FCfgCode = " + dbl.sqlString(this.strOldCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            if (this.configurerela != null) {
                ConfigureRelaBean configurerela = new ConfigureRelaBean();
                configurerela.setYssPub(pub);
                configurerela.saveMutliSetting(this.configurerela);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改关系定义设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * parseRowStr
     * 解析接口配置信息
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
                    this.configurerela = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }

            reqAry = sTmpStr.split("\t");
            this.strCode = reqAry[0];
            this.strName = reqAry[1];
            this.strImpExp = reqAry[2];
            this.strFileType = reqAry[3];
            this.strFilePath = reqAry[4];
            this.strFileName = reqAry[5];
            this.strSqlText = reqAry[6];
            this.strDesc = reqAry[7];
            this.strOldCode = reqAry[8];

            this.creatorCode = pub.getUserCode();
            this.creatorTime = YssFun.formatDatetime(new java.util.Date());

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new ConfigureBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析接口配置请求信息出错", e);
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.strCode).append("\t");
        buffer.append(this.strName).append("\t");
        buffer.append(this.strImpExp).append("\t");
        buffer.append(this.strFileType).append("\t");
        buffer.append(this.strFilePath).append("\t");
        buffer.append(this.strFileName).append("\t");
        buffer.append(this.strSqlText).append("\t");
        buffer.append(this.strDesc).append("\t");
        buffer.append(this.strTabName);
        return buffer.toString();

    }

    /**
     * checkInput
     * 检查接口配置是否合法
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        String strSql = "", strTmp = "";
        if (btOper == YssCons.OP_ADD) {
            strSql =
                "select FCfgCode from " + pub.yssGetTableName("Tb_Dao_Configure") +
                "" +
                " where  FCfgCode = " +
                dbl.sqlString(this.strCode.trim()) + "";
            strTmp = dbFun.GetValuebySql(strSql);
            if (strTmp.length() > 0) {
                throw new YssException("数据接口配置【" + this.strCode.trim() +
                                       "】已存在，请重新输入");
            }
        } else if (btOper == YssCons.OP_EDIT) {
            if (!this.strCode.trim().equalsIgnoreCase(this.strOldCode)) {
                strSql =
                    "select FCfgCode from " +
                    pub.yssGetTableName("Tb_Dao_Configure") + "" +
                    " where FCfgCode = " +
                    dbl.sqlString(this.strCode.trim()) + "";
                strTmp = dbFun.GetValuebySql(strSql);
                if (strTmp.length() > 0) {
                    throw new YssException("数据接口配置【" + this.strCode.trim() +
                                           "】已存在，请重新输入");
                }
            }
        }
    }

    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    public void saveSetting(byte btOper) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            if (btOper == YssCons.OP_ADD) {
                strSql =
                    "insert into " + pub.yssGetTableName("Tb_Dao_Configure") +
                    " (FCfgCode, FCfgName, FInOut, " +
                    " FFileType, FFilePath, FFileName, FSqlText, FDesc, FCreator, FCreateTime) " +
                    " values(" + dbl.sqlString(this.strCode) + "," +
                    dbl.sqlString(this.strName) + "," +
                    this.strImpExp + "," +
                    dbl.sqlDate(this.strFileType) + "," +
                    dbl.sqlString(this.strFilePath) + "," +
                    dbl.sqlString(this.strFileName) + "," +
                    dbl.sqlString(this.strSqlText) + "," +
                    dbl.sqlString(this.strDesc) + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + ")";
            } else if (btOper == YssCons.OP_EDIT) {
                strSql = "update " + pub.yssGetTableName("Tb_Dao_Configure") +
                    "  set FCfgCode = " +
                    dbl.sqlString(this.strCode) + ", FCfgName = "
                    + dbl.sqlString(this.strName) + ",FInOut = " +
                    this.strImpExp + ",FFileType = "
                    + dbl.sqlDate(this.strFileType) + ", FFilePath = "
                    + dbl.sqlString(this.strFilePath) + ", FFileName = " +
                    dbl.sqlString(this.strFileName) + ", FSqlText = " +
                    dbl.sqlString(this.strSqlText) + ", FDesc=" +
                    dbl.sqlString(this.strDesc) + ", FCreator = " +
                    dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                    dbl.sqlString(this.creatorTime) +
                    " where FCfgCode = " + dbl.sqlString(this.strOldCode);
            } else if (btOper == YssCons.OP_DEL) {
                strSql = "delete from " + pub.yssGetTableName("Tb_Dao_Configure") +
                    " where FCfgCode = " + dbl.sqlString(this.strCode);
            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            // 联系人处理
            //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            //if (btOper == YssCons.OP_EDIT && this.strCode != this.strOldCode) {
            if (btOper == YssCons.OP_EDIT && !this.strCode.equalsIgnoreCase(this.strOldCode)) {
            //---end---
                strSql = "update " + pub.yssGetTableName("Tb_Dao_Configure_Rela") +
                    "  set FCfgCode = " +
                    dbl.sqlString(this.strCode) +
                    " where FCfgCode = " + dbl.sqlString(this.strOldCode);
                dbl.executeSql(strSql);
            }

            if (this.configurerela != null) {
                if (btOper == YssCons.OP_ADD || btOper == YssCons.OP_EDIT) {
                    ConfigureRelaBean configurerela = new ConfigureRelaBean();
                    configurerela.setYssPub(pub);
                    configurerela.saveMutliSetting(this.configurerela);
                }
            }
            if (btOper == YssCons.OP_DEL) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Dao_Configure_Rela") +
                    " where FCfgCode = " + dbl.sqlString(this.strCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新接口配置信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr Sring
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * getSetting
     *
     * @return IParaSet
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() throws YssException {
        StringBuffer buf = new StringBuffer();
        String strSql = "", strResult = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Dao_Configure") +
                " " +
                " order by FCfgCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append( (rs.getString("FCfgCode") + "").trim()).append("\t");
                buf.append( (rs.getString("FCfgName") + "").trim()).append("\t");
                buf.append( (rs.getString("FInOut") + "").trim()).append(
                    "\t");
                buf.append(YssFun.formatDate(rs.getDate("FFileType"))).append("\t");
                buf.append( (rs.getString("FFilePath") + "").trim()).append("\t");
                buf.append( (rs.getString("FFileName") + "").trim()).append("\t");
                buf.append( (rs.getString("FSqlText") + "").trim()).append("\t");
                buf.append( (rs.getString("FDesc") + "").trim()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            strResult = buf.toString();
            if (strResult.length() > 2) {
                strResult = strResult.substring(0, strResult.length() - 2);
            }
            return strResult;
        } catch (Exception e) {
            throw new YssException("获取接口配置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String builderListViewData(String strSql, int iIsTabName) throws
        YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setConfigureAttr(rs);
                if (iIsTabName == 1) {
                    this.strTabName = rs.getString("FTabName");
                }
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
            throw new YssException("获取接口配置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData1
     * 获取接口配置数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select a.*,b.FVocName as FInOutValue from " +
            pub.yssGetTableName("Tb_Dao_Configure") + "  a " +
            " left join Tb_Fun_Vocabulary b on " + dbl.sqlToChar("a.FInOut") + " = b.FVocCode and b.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CFG_INOUT) +
            buildFilterSql() +
            " order by a.FCreateTime desc, a.FInOut, a.FCfgCode";
        return this.builderListViewData(strSql, 0);
    }

    /**
     * getListViewData4
     * 获取带系统表名信息的接口配置数据
     * @return String
     */
    public String getListViewData3() throws YssException {
        String strSql = "";
        strSql = "select a.*, b.FTabName,c.FVocName as FInOutValue from " +
            pub.yssGetTableName("Tb_Dao_Configure") + "  a " +
            " left join (select FCfgCode as FCode, FTabName from " +
            pub.yssGetTableName("Tb_Dao_Configure_Rela") +
            " group by FCfgCode,FTabName ) b on a.FCfgCode = b.FCode" +
            " left join Tb_Fun_Vocabulary c on " + dbl.sqlToChar("a.FInOut") + " = c.FVocCode and c.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CFG_INOUT) +
            " where b.FTabName is not null " +
            " and a.FInOut =" + this.strImpExp +
            " order by a.FInOut, a.FCfgCode";
        return this.builderListViewData(strSql, 1);
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData4() {
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
     * buildFilterSql
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strCode.length() != 0) {
                sResult = sResult + " and a.FCfgCode like '" +
                    filterType.strCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strName.length() != 0) {
                sResult = sResult + " and a.FCfgName like '" +
                    filterType.strName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strImpExp.length() == 1) {
                sResult = sResult + " and a.FInOut = " +
                    dbl.sqlString(filterType.strImpExp);
            }
            if (this.filterType.strFileType.length() != 0 &&
                !this.filterType.strFileType.equalsIgnoreCase("ALL")) {
                sResult = sResult + " and a.FFileType = " +
                    dbl.sqlString(filterType.strFileType);
            }
            if (this.filterType.strFilePath.length() != 0) {
                sResult = sResult + " and a.FFilePath like '" +
                    filterType.strFilePath.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strFileName.length() != 0) {
                sResult = sResult + " and a.FFileName = " +
                    dbl.sqlString(filterType.strFileName);
            }
            if (this.filterType.strSqlText.length() != 0) {
                sResult = sResult + " and a.FSqlText = " +
                    dbl.sqlString(filterType.strSqlText);
            }
            if (this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }

        }
        return sResult;
    }

    public void setConfigureAttr(ResultSet rs) throws SQLException {
        this.strCode = rs.getString("FCfgCode") + "";
        this.strName = rs.getString("FCfgName") + "";
        this.strImpExp = rs.getString("FInOut") + "";
        this.strFileType = rs.getString("FFileType") + "";
        this.strFilePath = rs.getString("FFilePath") + "";
        this.strFileName = rs.getString("FFileName") + "";
        this.strSqlText = rs.getString("FSqlText") + "";
        this.strDesc = rs.getString("FDesc") + "";
    }

    /**
     * exportData
     *
     * @param sRequestStr String
     * @return String
     */
    public String exportData(String sRequestStr) {
        return "";
    }

    /**
     * importData
     *
     * @param sRequestStr String
     */
    public void importData(String sRequestStr) {
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
