package com.yss.main.datainterface;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class ImpExpDataBean
    extends BaseDataSettingBean implements IDataInterface {

    private String cfgCode = ""; //系统表名
    private String tabName = ""; //表名参数
    private String sysTabName = ""; //系统表名
    private String sqlText = ""; //导出脚本
    private String dataStr = ""; //数据
    private String sSqlFieldName = ""; //条件字段名
    private String sSqlFieldType = ""; //条件字段类型
    private Object sqlVal1 = null; //条件1
    private Object sqlVal2 = null; //条件2

    public ImpExpDataBean() {

    }

    private boolean checkImportData(String sParamData) throws YssException {
        boolean blIsCancel = false;

        if (sqlVal2 == null) {
            if (sSqlFieldType.indexOf("DATE") > -1) {
                java.util.Date dParamDate = YssFun.toDate(sParamData);
                if (YssFun.dateDiff(dParamDate, (java.util.Date) sqlVal1) != 0) {
                    blIsCancel = true;
                }
            } else {
                if (!sParamData.equals(sqlVal1.toString())) {
                    blIsCancel = true;
                }
            }
        } else {
            if (sSqlFieldType.indexOf("DATE") > -1) {
                java.util.Date dParamDate = YssFun.toDate(sParamData);
                if (YssFun.dateDiff(dParamDate, (java.util.Date) sqlVal1) > 0 ||
                    YssFun.dateDiff(dParamDate, (java.util.Date) sqlVal2) < 0) {
                    blIsCancel = true;
                }

            } else {
                if (sParamData.compareTo(sqlVal1.toString()) < 0 ||
                    sParamData.compareTo(sqlVal2.toString()) > 0) {
                    blIsCancel = true;
                }
            }
        }

        return blIsCancel;
    }

    private String buildDelSqlFilter(String sqlwhere, HashMap hmFieldType) throws YssException {
        String sqlAry[] = null;
        String tmpVal = "";
        String reStr = "";
        String sFieldName = "";

        sSqlFieldName = "";
        sSqlFieldType = "";
        sqlVal1 = null;
        sqlVal2 = null;

        if (sqlwhere.trim().length() > 0) {
            sqlAry = sqlwhere.split("\t");
            sFieldName = sqlAry[0];
            sSqlFieldName = sFieldName;
            tmpVal = (String) hmFieldType.get(sFieldName.toUpperCase());
            sSqlFieldType = tmpVal;
            if (tmpVal != null && sqlAry.length == 2) {
                if (tmpVal.indexOf("DATE") > -1) {
                    reStr = " where " + sFieldName + " = " + dbl.sqlDate(sqlAry[1]);
                    sqlVal1 = YssFun.toDate(sqlAry[1]);
                } else {
                    reStr = " where " + sFieldName + " = " + dbl.sqlString(sqlAry[1]);
                    sqlVal1 = sqlAry[1];
                }
            } else if (tmpVal != null && sqlAry.length == 3) {
                if (tmpVal.indexOf("DATE") > -1) {
                    reStr = " where " + sFieldName + " between " + dbl.sqlDate(sqlAry[1]) +
                        " and " + dbl.sqlDate(sqlAry[2]);
                    sqlVal1 = YssFun.toDate(sqlAry[1]);
                    sqlVal2 = YssFun.toDate(sqlAry[2]);
                } else {
                    reStr = " where " + sFieldName + " between " + dbl.sqlDate(sqlAry[1]) +
                        " and " + dbl.sqlDate(sqlAry[2]);
                    sqlVal1 = sqlAry[1];
                    sqlVal1 = sqlAry[2];
                }
            }
        }

        return reStr;
    }

    public void importData(String sRequestStr) throws YssException {
        String strSql = "";
        String defaultVal = "";
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        Connection conn = dbl.loadConnection();
        PreparedStatement pstmt = null;
        String sFields = "";
        String sParams = "";
        HashMap hmOutFields = new HashMap();
        HashMap hmIndex = new HashMap();
        HashMap hmColType = new HashMap();
        HashMap hmColDefault = new HashMap();
        String[] allDataAry = null;
        String[] tabDataAry = null;
        String[] dataRowAry = null;
        String[] dataColAry = null;
        String[] sFieldAry = null;
        int iFieldIndex = -1;
        boolean bTrans = false;
        String tmpVal = "";
        try {
            allDataAry = sRequestStr.split("\r\f\r\f");
            conn.setAutoCommit(false);
            bTrans = true;
            for (int tabIdx = 0; tabIdx < allDataAry.length; tabIdx++) {
                this.parseRowStr(allDataAry[tabIdx]);
                //将外部数据放入字符串数组中
                dataRowAry = this.dataStr.split(YssCons.YSS_LINESPLITMARK);
                hmIndex.clear();
                hmColType.clear();
                hmOutFields.clear();
                hmColDefault.clear();
                sFields = "";
                sParams = "";

                //把外部数据的字段放入Map中，Map的key是外部字段名，value是顺序号
                dataColAry = dataRowAry[0].split("\t");
                for (int i = 0; i < dataColAry.length; i++) {
                    hmIndex.put(dataColAry[i], new Integer(i));
                }

                //把字段的类型放入Map中，并统计表所有的字段
                strSql = "select * from " + this.sysTabName + " where 1=2";
                rs = dbl.openResultSet(strSql);
                rsmd = rs.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    hmColType.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
                    sFields += rsmd.getColumnName(i) + ",";
                    sParams += "?,";
                }
                if (sFields.length() > 1) {
                    sFields = sFields.substring(0, sFields.length() - 1);
                    sParams = sParams.substring(0, sParams.length() - 1);
                }
                dbl.closeResultSetFinal(rs);
                //将外部字段和系统表字段的关联关系放入Map中
                strSql = "select * from " + pub.yssGetTableName("Tb_Dao_Configure_Rela") +
                    " where FCfgCode = " + dbl.sqlString(this.cfgCode) +
                    " and FTabName = " + dbl.sqlString(this.tabName);
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    hmOutFields.put(rs.getString("FFieldName").toUpperCase(), rs.getString("FOutField"));
                }
                if (hmOutFields.size() == 0) {
                    throw new YssException("没有设置表【" + this.tabName + "】的【" +
                                           this.tabName + "】的关系定义，请先设置后再导入");
                }
                dbl.closeResultSetFinal(rs);
                //获取字段默认值
                strSql = "select * from Tb_Fun_DataDict" +
                    " where FTabName = " + dbl.sqlString(this.tabName);

                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    hmColDefault.put(rs.getString("FFieldName").toUpperCase(),
                                     rs.getString("FDefaultValue") == null ? "" : rs.getString("FDefaultValue"));
                }
                dbl.closeResultSetFinal(rs);

                //删除指定条件范围内的数据（默认为全部）
                strSql = "delete from " + this.sysTabName + buildDelSqlFilter(this.sqlText, hmColType);
                dbl.executeSql(strSql);

                sFieldAry = sFields.split(",");
                strSql = "insert into " + this.sysTabName + " (" + sFields +
                    ") values (" + sParams + ")";

                pstmt = conn.prepareStatement(strSql);
                for (int iRow = 1; iRow < dataRowAry.length; iRow++) {
                    dataColAry = (dataRowAry[iRow] + "\tnull").split("\t");
                    //判断是否符合条件条件
                    if (hmOutFields.get(sSqlFieldName.toUpperCase()) != null) {
                        iFieldIndex = ( (Integer) hmIndex.get(hmOutFields.get(sSqlFieldName.toUpperCase()))).intValue();
                        if (checkImportData(dataColAry[iFieldIndex])) {
                            continue;
                        }
                    }

                    for (int iCol = 0; iCol < sFieldAry.length; iCol++) {
                        //判断字段是否有关联的外部字段，如果没有，则输入默认值
                        if (hmOutFields.get(sFieldAry[iCol]) == null) {
                            if (sFieldAry[iCol].equalsIgnoreCase("FCheckState")) {
                                pstmt.setInt(iCol + 1, 1);
                            } else if (sFieldAry[iCol].equalsIgnoreCase("FCreator")) {
                                pstmt.setString(iCol + 1, pub.getUserCode());
                            } else if (sFieldAry[iCol].equalsIgnoreCase("FCreateTime")) {
                                pstmt.setString(iCol + 1,
                                                YssFun.formatDatetime(new java.util.
                                    Date()));
                            } else if (sFieldAry[iCol].equalsIgnoreCase("FCheckUser")) {
                                pstmt.setString(iCol + 1, pub.getUserCode());
                            } else if (sFieldAry[iCol].equalsIgnoreCase("FCheckTime")) {
                                pstmt.setString(iCol + 1,
                                                YssFun.formatDatetime(new java.util.
                                    Date()));
                            } else {
                                //获取当前字段类型
                                tmpVal = (String) hmColType.get(sFieldAry[iCol]);
                                defaultVal = (String) hmColDefault.get(sFieldAry[iCol]);
                                if (tmpVal.indexOf("DATE") > -1) {
                                    if (defaultVal == null ||
                                        defaultVal.length() == 0 ||
                                        !YssFun.isDate(defaultVal)) {
                                        defaultVal = "1900-01-01";
                                    }
                                    pstmt.setDate(iCol + 1, YssFun.toSqlDate(YssFun.toDate(defaultVal)));
                                } else if (tmpVal.indexOf("NUMBER") > -1) {
                                    if (defaultVal == null ||
                                        defaultVal.length() == 0 ||
                                        !YssFun.isNumeric(defaultVal)) {
                                        defaultVal = "0";
                                    }
                                    pstmt.setString(iCol + 1, defaultVal);
                                } else {
                                    if (defaultVal == null ||
                                        defaultVal.length() == 0) {
                                        defaultVal = " ";
                                    }
                                    pstmt.setString(iCol + 1, defaultVal);
                                }
                            }
                        } else {
                            if (hmIndex.get(hmOutFields.get(sFieldAry[iCol])) == null) {
                                throw new YssException("外部数据与数据接口关系定义设置不匹配，请检测后重新导入");
                            }
                            //获取外部字段在外部数据列的序列号
                            iFieldIndex = ( (Integer) hmIndex.get(hmOutFields.get(sFieldAry[iCol]))).intValue();

                            //获取当前字段类型
                            tmpVal = (String) hmColType.get(sFieldAry[iCol]);
                            defaultVal = (String) hmColDefault.get(sFieldAry[iCol]);
                            //按类型区分设置数据
                            if (tmpVal.indexOf("DATE") > -1) {
                                if (defaultVal == null ||
                                    defaultVal.length() == 0 ||
                                    !YssFun.isDate(defaultVal)) {
                                    defaultVal = "1900-01-01";
                                }
                                pstmt.setDate(iCol + 1,
                                              YssFun.toSqlDate( (YssFun.isDate(
                                                  dataColAry[
                                                  iFieldIndex])) ?
                                    YssFun.toDate(dataColAry[iFieldIndex]) :
                                    YssFun.toDate(defaultVal)));
                            } else if (tmpVal.indexOf("NUMBER") > -1) {
                                if (defaultVal == null ||
                                    defaultVal.length() == 0 ||
                                    !YssFun.isNumeric(defaultVal)) {
                                    defaultVal = "0";
                                }
                                pstmt.setString(iCol + 1,
                                                (dataColAry[iFieldIndex].length() == 0) ?
                                                defaultVal : dataColAry[iFieldIndex]);
                            } else {
                                if (defaultVal == null ||
                                    defaultVal.length() == 0) {
                                    defaultVal = " ";
                                }
                                pstmt.setString(iCol + 1,
                                                (dataColAry[iFieldIndex].length() == 0) ?
                                                defaultVal : dataColAry[iFieldIndex] + "");
                            }
                        }
                    }
                    pstmt.executeUpdate();
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            dbl.endTransFinal(bTrans);
            throw new YssException("导入数据时出错: " + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
    }

    /**
     * ExportData
     *
     * @return String
     */
    public String exportData(String sRequestStr) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        String[] allDataAry = null;
        String sOuterFields = "";
        String sFieldsDesc = "";
        String sFieldsName = "";
        String[] sFieldsNameAry = null;
        HashMap hmColType = new HashMap();
        StringBuffer buf = new StringBuffer();
        StringBuffer reBuf = new StringBuffer();
        String sReTabData = "";
        String sResult = "";
        String tmpVal = "";
        try {
            allDataAry = sRequestStr.split("\r\f\r\f");
            for (int tabIdx = 0; tabIdx < allDataAry.length; tabIdx++) {
                this.parseRowStr(allDataAry[tabIdx]);
                sFieldsName = "";
                sOuterFields = "";
                sFieldsDesc = "";
                buf.setLength(0);
                hmColType.clear();

                /*         if (this.dataStr.length() != 0)
                         {
                            this.sqlText = this.dataStr;
                            this.dataStr = "";
                         }*/

                strSql = "select a.FFieldName as FFieldName, a.FOutField as FOutField," +
                    " b.FFieldDesc as FFieldDesc from " +
                    " (select * from " + pub.yssGetTableName("Tb_Dao_Configure_Rela") + " where FCfgCode = " +
                    dbl.sqlString(this.cfgCode) + " and FTabName = " +
                    dbl.sqlString(this.tabName) + ") a left join " +
                    " (select * from Tb_Fun_DataDict where FTabName = " +
                    dbl.sqlString(this.tabName) + ") b " +
                    "on a.FTabName = b.FTabName and a.FFieldName = b.FFieldName";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    sFieldsName += rs.getString("FFieldName") + "\t";
                    sOuterFields += rs.getString("FOutField") + "\t";
                    sFieldsDesc += rs.getString("FFieldDesc") + "\t";
                }
                if (sFieldsName.length() > 1) {
                    sFieldsName = sFieldsName.substring(0,
                        sFieldsName.length() - 1);
                    sOuterFields = sOuterFields.substring(0,
                        sOuterFields.length() - 1);
                    sFieldsDesc = sFieldsDesc.substring(0,
                        sFieldsDesc.length() - 1);
                    sFieldsNameAry = sFieldsName.split("\t");

                    buf.append(sOuterFields).append(YssCons.YSS_LINESPLITMARK);
                    buf.append(sFieldsDesc).append(YssCons.YSS_LINESPLITMARK);
                } else {
                    throw new YssException("没有设置表【" + this.tabName + "】的的关系定义，请先设置后再导出");
                }
                dbl.closeResultSetFinal(rs);

                strSql = "select * from " + this.sysTabName;
                rs = dbl.openResultSet(strSql);
                rsmd = rs.getMetaData();

                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    hmColType.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
                }

                dbl.closeResultSetFinal(rs);

                strSql = "select * from " + this.sysTabName + buildDelSqlFilter(this.sqlText, hmColType);
                rs = dbl.openResultSet(strSql);

                while (rs.next()) {
                    for (int i = 0; i < sFieldsNameAry.length; i++) {
                        tmpVal = (String) hmColType.get(sFieldsNameAry[i].toUpperCase());
                        if (tmpVal == null) {
                            throw new YssException("表【" + this.tabName + "】不存在字段【" +
                                sFieldsNameAry[i] + "】，请重新设置后再导出");
                        }
                        if (tmpVal.indexOf("DATE") > -1) {
                            buf.append( (rs.getString(sFieldsNameAry[i]) == null) ? "" : YssFun.formatDate(rs.getDate(sFieldsNameAry[i]), YssCons.YSS_DATEFORMAT));
                        } else {
                            buf.append( (rs.getString(sFieldsNameAry[i]) == null) ? "" : rs.getString(sFieldsNameAry[i]));
                        }
                        if (i < sFieldsNameAry.length - 1) {
                            buf.append("\t");
                        }
                    }
                    buf.append(YssCons.YSS_LINESPLITMARK);
                }
                dbl.closeResultSetFinal(rs);
                sReTabData = buf.toString();
                if (sReTabData.length() > 2) {
                    sReTabData = sReTabData.substring(0, sReTabData.length() - 2);
                }
                this.dataStr = sReTabData;
                reBuf.append(this.buildRowStr()).append("\r\f\r\f");
                sResult = reBuf.toString();
            }
            if (sResult.length() > 4) {
                sResult = sResult.substring(0, sResult.length() - 4);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("导出数据时出错: " + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.cfgCode).append("\r\f");
        buf.append(this.tabName).append("\r\f");
        buf.append(this.dataStr);
        return buf.toString();
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
    public void checkSetting() {
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
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() {
        return "";
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
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
        String[] reqAry = sRowStr.split("\r\f");
        this.cfgCode = reqAry[0];
        this.tabName = reqAry[1];
        this.sqlText = reqAry[2];
        this.dataStr = reqAry[3];
        if (this.tabName.toUpperCase().indexOf("_FUN_") < 0 &&
            this.tabName.toUpperCase().indexOf("_BASE_") < 0 &&
            this.tabName.toUpperCase().indexOf("_SYS_") < 0) {
            this.sysTabName = pub.yssGetTableName(this.tabName);
        } else {
            this.sysTabName = this.tabName;
        }

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

}
