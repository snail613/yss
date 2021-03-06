package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title: DataRelaBean </p>
 * <p>Description: 接口配置关系定义设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ConfigureRelaBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strCfgCode = ""; //配置代码
    private String strTabName = ""; //系统表名
    private String strTableDesc = ""; //系统表描述
    private String strFieldName = ""; //系统表字段名
    private String strFieldDesc = ""; //系统表字段描述
    private String strCorField = ""; //外部字段名
    private String strDesc = ""; //关系描述

    private String strOldCfgCode = "";
    private String strOldTabName = "";
    private String strOldFieldName = "";

    private ConfigureRelaBean filterType;

    public ConfigureRelaBean() {
    }

    /**
     * parseRowStr
     * 解析关系定义设置请求
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

            reqAry = sTmpStr.split("\t");
            this.strCfgCode = reqAry[0];
            this.strTabName = reqAry[1];
            this.strFieldName = reqAry[2];
            this.strCorField = reqAry[3];
            this.strDesc = reqAry[4];
            this.strOldCfgCode = reqAry[5];
            this.strOldTabName = reqAry[6];
            this.strOldFieldName = reqAry[7];

            this.creatorCode = pub.getUserCode();
            this.creatorTime = YssFun.formatDatetime(new java.util.Date());

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ConfigureRelaBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析关系定义设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strCfgCode.trim()).append("\t");
        buf.append(this.strTabName.trim()).append("\t");
        buf.append(this.strTableDesc.trim()).append("\t");
        buf.append(this.strFieldName.trim()).append("\t");
        buf.append(this.strFieldDesc.trim()).append("\t");
        buf.append(this.strCorField.trim()).append("\t");
        buf.append(this.strDesc.trim());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        String strSql = "", strTmp = "";
        if (btOper == YssCons.OP_ADD) {
            strSql =
                "select FCfgCode from " +
                pub.yssGetTableName("Tb_Dao_Configure_Rela") + "" +
                " where FTabName = " +
                dbl.sqlString(this.strTabName.trim()) + " and FFieldName = " +
                dbl.sqlString(this.strFieldName.trim()) + " and FCfgCode = " +
                dbl.sqlString(this.strCfgCode.trim()) + "";
            strTmp = dbFun.GetValuebySql(strSql);
            if (strTmp.length() > 0) {
                throw new YssException("数据接口配置【" + this.strCfgCode.trim() +
                                       "】中中表【" + this.strTabName.trim() +
                                       "】的字段【" + this.strFieldName.trim() +
                                       "】的关系定义被占用，请重新输入");
            }
        } else if (btOper == YssCons.OP_EDIT) {
            if (!this.strTabName.trim().equalsIgnoreCase(this.strOldTabName) ||
                !this.strFieldName.trim().equalsIgnoreCase(this.strOldFieldName) ||
                !this.strCfgCode.trim().equalsIgnoreCase(this.strOldCfgCode)) {
                strSql =
                    "select FCfgCode from " +
                    pub.yssGetTableName("Tb_Dao_Configure_Rela") + "" +
                    " where FTabName = " +
                    dbl.sqlString(this.strTabName.trim()) + " and FFieldName = " +
                    dbl.sqlString(this.strFieldName.trim()) + " and FCfgCode = " +
                    dbl.sqlString(this.strCfgCode.trim()) + "";
                strTmp = dbFun.GetValuebySql(strSql);
                if (strTmp.length() > 0) {
                    throw new YssException("数据接口配置【" + this.strCfgCode.trim() +
                                           "】中中表【" + this.strTabName.trim() +
                                           "】的字段【" + this.strFieldName.trim() +
                                           "】的关系定义被占用，请重新输入");

                }
            }
        }
    }

    /**
     * saveSetting
     * 更新关系定义设置信息
     * @param btOper byte
     */
    public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            if (btOper == YssCons.OP_ADD) {
                strSql = "insert into " +
                    pub.yssGetTableName("Tb_Dao_Configure_Rela") + "" +
                    " (FCfgCode,FTabName,FFieldName,FOutField,FDesc,fcreator,fcreatetime)" +
                    " values(" + dbl.sqlString(this.strCfgCode) + "," +
                    dbl.sqlString(this.strTabName) + "," +
                    dbl.sqlString(this.strFieldName) + "," +
                    dbl.sqlString(this.strCorField) + "," +
                    dbl.sqlString(this.strTableDesc) + "," +
                    dbl.sqlString(this.strFieldName) + "," +
                    dbl.sqlString(this.strFieldDesc) + "," +
                    dbl.sqlString(this.strCorField) + "," +
                    dbl.sqlString(this.strDesc) + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + ")";
            } else if (btOper == YssCons.OP_EDIT) {
                strSql = "update " + pub.yssGetTableName("Tb_Dao_Configure_Rela") +
                    " set FCfgCode = " +
                    dbl.sqlString(this.strCfgCode) + ",FTabName = " +
                    dbl.sqlString(this.strTabName) + ",FFieldName = " +
                    dbl.sqlString(this.strFieldName) + ",FOutField = " +
                    dbl.sqlString(this.strCorField) + ",FDesc = " +
                    dbl.sqlString(this.strDesc) + ",FCreator = " +
                    dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                    dbl.sqlString(this.creatorTime) +
                    " where FTabName = " +
                    dbl.sqlString(this.strOldTabName.trim()) +
                    " and FFieldName = " +
                    dbl.sqlString(this.strOldFieldName.trim()) +
                    " and FCfgCode = " +
                    dbl.sqlString(this.strOldCfgCode.trim()) + "";
            } else if (btOper == YssCons.OP_DEL) {
                strSql = "delect from " +
                    pub.yssGetTableName("Tb_Dao_Configure_Rela") + "" +
                    " where FTabName = " +
                    dbl.sqlString(this.strTabName.trim()) + " and FFieldName = " +
                    dbl.sqlString(this.strFieldName.trim()) + " and FCfgCode = " +
                    dbl.sqlString(this.strCfgCode.trim()) + "";
            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新关系定义信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);
            strSql = "delete from " + pub.yssGetTableName("Tb_Dao_Configure_Rela") +
                " where FCfgCode = " +
                dbl.sqlString(this.strCfgCode);
            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Dao_Configure_Rela") +
                " (FCfgCode,FTabName,FFieldName,FOutField," +
                "FDesc,FCreator,FCreateTime) values (?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                if (i > 0) {
                    this.parseRowStr(sMutilRowAry[i]);
                }
                if (this.strTabName.trim().length() > 0) {
                    pstmt.setString(1, this.strCfgCode);
                    pstmt.setString(2, this.strTabName);
                    pstmt.setString(3, this.strFieldName);
                    pstmt.setString(4, this.strCorField);
                    pstmt.setString(5, this.strDesc);
                    pstmt.setString(6, this.creatorCode);
                    pstmt.setString(7, this.creatorTime);
                    pstmt.executeUpdate();
                }
            }
            return "";//modified by yeshenghong for CCB security check 20121018 
        } catch (Exception e) {
            throw new YssException("保存关系定义出错", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
        return null;
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
    }

    /**
     * getListViewData1
     * 获取关系定义数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        try {
            sHeader = "系统表名称\t系统字段名称\t外部字段名称\t关系描述";
            String sql =
                "select a.*, b.FTableDesc as FTableDesc, b.FFieldDesc as FFieldDesc from " +
                pub.yssGetTableName("Tb_Dao_Configure_Rela") + " a" +
                " left join (select FTabName,FFieldName,FTableDesc,FFieldDesc from Tb_Fun_DataDict) b on a.FTabName = b.FTabName" +
                " and a.FFieldName = b.FFieldName " +
                " where FCfgCode = " +
                dbl.sqlString(this.filterType.strCfgCode) +
                " order by a.FTabName, a.FFieldName";
            ResultSet rs = dbl.openResultSet(sql);
            while (rs.next()) {
                buf.append( (rs.getString("FTableDesc") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FFieldDesc") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FOutField") + "").trim());
                buf.append("\t");
                buf.append( (YssFun.left( (rs.getString("FDesc") + "").trim(), 20)).
                           trim());
                buf.append(YssCons.YSS_LINESPLITMARK);

                this.strCfgCode = rs.getString("FCfgCode") + "";
                this.strTabName = rs.getString("FTabName") + "";
                this.strTableDesc = rs.getString("FTableDesc") + "";
                this.strFieldName = rs.getString("FFieldName") + "";
                this.strFieldDesc = rs.getString("FFieldDesc") + "";
                this.strCorField = rs.getString("FOutField") + "";
                this.strDesc = rs.getString("FDesc") + "";
                buf1.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取关系定义数据出错", e);
        }
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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /**
     * checkSetting
     *
     * @return String
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     *
     */
    public void delSetting() {
    }

    /**
     * importData
     *
     * @param sRequestStr String
     */
    public void importData(String sRequestStr) {
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
     * editSetting
     *
     * @return String
     */
    public String editSetting() {
        return "";
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
