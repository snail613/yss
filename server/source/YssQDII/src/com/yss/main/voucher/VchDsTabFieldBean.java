package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class VchDsTabFieldBean
    extends BaseDataSettingBean implements IDataSetting {

    private String dsVchDsCode = "";
    private String fieldName = "";
    private String aliasName = "";
    private String function = "";
    private String desc = "";
    private String oldVchDsCode = "";
    private String oldFieldName = "";

    private VchDsTabFieldBean FilterType;
    public String getDesc() {
        return desc;
    }

    public String getDsTplCode() {
        return dsVchDsCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFunction() {
        return function;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDsTplCode(String dsTplCode) {
        this.dsVchDsCode = dsTplCode;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public void setOldVchDsCode(String oldVchDsCode) {
        this.oldVchDsCode = oldVchDsCode;
    }

    public void setOldFieldName(String oldFieldName) {
        this.oldFieldName = oldFieldName;
    }

    public void setDsVchDsCode(String dsVchDsCode) {
        this.dsVchDsCode = dsVchDsCode;
    }

    public void setFilterType(VchDsTabFieldBean FilterType) {
        this.FilterType = FilterType;
    }

    public String getAliasName() {
        return aliasName;
    }

    public String getOldVchDsCode() {
        return oldVchDsCode;
    }

    public String getOldFieldName() {
        return oldFieldName;
    }

    public String getDsVchDsCode() {
        return dsVchDsCode;
    }

    public VchDsTabFieldBean getFilterType() {
        return FilterType;
    }

    public VchDsTabFieldBean() {
    }

    private String buildFilterSql() {
        String sqlStr = "";
        if (this.FilterType != null) {
            sqlStr = " where 1=1";
            if (this.FilterType.dsVchDsCode.length() != 0) {
            	//modify by wangzuochun 2010.03.30  MS01052   凭证数据源字段配置，查询相关字段有问题   QDV4赢时胜（上海）2010年3月23日02_B 
                sqlStr += " and a.FVchDsCode = '" + this.FilterType.dsVchDsCode.replaceAll("'", "''") + "'";
                //----------------------------------MS01052------------------------------------//
            }
            if (this.FilterType.fieldName.length() != 0) {
                sqlStr += " and a.FFieldName like '" + this.FilterType.fieldName.replaceAll("'", "''") + "%'";
            }
            if (this.aliasName.length() != 0) {
                sqlStr += " and a.FAliasName like '" + this.FilterType.aliasName.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.function.length() != 0) {
                sqlStr += " and a.FFunction like '" + this.FilterType.function.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.desc.length() != 0) {
                sqlStr += " and a.FDesc like '" + this.FilterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sqlStr;
    }

    private void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.dsVchDsCode = rs.getString("FVchDsCode");
        this.fieldName = rs.getString("FFieldName");
        //  this.resTab = rs.getString("FResTab");
        this.aliasName = rs.getString("FAliasName");
        this.function = rs.getString("FFunction");
        this.desc = rs.getString("FDesc");
        super.buildRecLog(rs);
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        int i = 0;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,e.FVocName as FVFunction from " +
                pub.yssGetTableName("Tb_Vch_DsTabField") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary e on a.FFunction = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUNCTION) +
                buildFilterSql() + " order by a.FVchDsCode ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
//            bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
//                  append(YssCons.YSS_LINESPLITMARK);
                bufShow.append(rs.getString("FFieldName")).append("\t");
                bufShow.append(rs.getString("FFunction")).append("\t");
                bufShow.append(rs.getString("FAliasName")).append(YssCons.
                    YSS_LINESPLITMARK);
                // bufShow.append(rs.getInt("FIsTotalInd")==1?"√":"").append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                i++;
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            // VocabularyBean vocabulary = new VocabularyBean();
            // vocabulary.setYssPub(pub);
            // sVocStr = vocabulary.getVoc(YssCons.YSS_FUNCTION);
            //  sTabCellInfo = getTabCellInfo(i);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols(); //+"\r\f"+"voc"+sVocStr;
        } catch (Exception e) {
            throw new YssException("获取凭证数据源字段设置信息出错", e);
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
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "字段名\t字段描述";
            strSql = "select y.* from " +
                "(select FVchDsCode,FAliasName from " + pub.yssGetTableName("Tb_Vch_DsTabField") + " " +
                " where FCheckState=1 and FVchDsCode=" + dbl.sqlString(this.dsVchDsCode) +
                " order by FVchDsCode,FFieldName) x join" +
                " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Vch_DsTabField") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " ) y on x.FVchDsCode = y.FVchDsCode  and " +
                "  x.FAliasName=y.FAliasName";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FAliasName") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FDesc") + "").trim());
                bufShow.append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                setDsTabFieldAttr(rs);
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
            throw new YssException("获取可用凭证数据源字段信息出错！", e);
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Vch_DsTabField") +
                "(FVchDsCode, FFieldName, FAliasName, FFunction,FDesc," + //少加了一个逗号fazmm20070919
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.dsVchDsCode) + "," +
//                     dbl.sqlString(this.resTab) + "," +
                dbl.sqlString(this.fieldName) + "," +
                dbl.sqlString(this.aliasName) + "," +
                dbl.sqlString(this.function) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增数据源字段设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Vch_DsTabField") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FVchDsCode = " + dbl.sqlString(this.oldVchDsCode) +
                " and FFieldName=" + dbl.sqlString(this.oldFieldName);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核凭证数据源字段信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting
     */
    public void delSetting() {
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
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowAry = null;
        String[] sMutilRowStrAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";

        try {

            conn.setAutoCommit(false);
            bTrans = true;
            sMutilRowStrAry = sMutilRowStr.split("\r\f\n");
            sMutilRowAry = sMutilRowStrAry[0].split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);
            strSql = "delete from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
                " where FVchDsCode = " +
                dbl.sqlString(this.dsVchDsCode);
            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Vch_DsTabField") +
                "(FVchDsCode, FFieldName, FAliasName, FFunction," +
                " FCheckState,FCreator,FCreateTime,FCheckUser,FDesc)" +
                " values (?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {

                this.parseRowStr(sMutilRowAry[i]);
                if (this.fieldName.length() > 0 && this.aliasName.length() > 0) {
                    pstmt.setString(1, this.dsVchDsCode);
                    pstmt.setString(2, this.fieldName);
                    pstmt.setString(3, this.aliasName);
                    pstmt.setString(4, this.function);
                    pstmt.setInt(5, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(6, this.creatorCode);
                    pstmt.setString(7, this.creatorTime);
                    pstmt.setString(8,
                                    (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.setString(9, this.desc);
                    pstmt.executeUpdate();
                }

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (SQLException e) {
            throw new YssException("保存数据源字段设置信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }

    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() throws YssException {
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
        buf.append(this.dsVchDsCode).append("\t");
        buf.append(this.fieldName).append("\t");
        buf.append(this.aliasName).append("\t");
        buf.append(this.function).append("\t");
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
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
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
            this.dsVchDsCode = reqAry[0];
            this.fieldName = reqAry[1];
            this.aliasName = reqAry[2];
            this.function = reqAry[3];
            this.desc = reqAry[4];
            super.checkStateId = Integer.parseInt(reqAry[5]);
            this.oldFieldName = reqAry[6];
            this.oldVchDsCode = reqAry[7];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new VchDsTabFieldBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析凭证数据源字段设置出错", e);
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

    public void setDsTabFieldAttr(ResultSet rs) throws SQLException {
        this.dsVchDsCode = rs.getString("FVchDsCode");
        this.fieldName = rs.getString("FFieldName");
        this.aliasName = rs.getString("FAliasName");
        this.function = rs.getString("FFunction");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);

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
