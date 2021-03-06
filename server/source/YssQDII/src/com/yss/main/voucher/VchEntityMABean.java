package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchEntityMABean
    extends BaseDataSettingBean implements IDataSetting {
    private String vchTplCode = "";
    private String entityCode = "";
    private String type = "";
    private String maField = "";
    private String maFieldValue = "";
    private String operSign = "";
    private String operSignValue = ""; //添加对operSign代码的值的显示 add liyu 1121
    private String desc = "";

    private String maDictCode = "";
    private String maDictName = "";
    private double maConent;

    private String orderIndex = "";

    private String tmpOrderIndex = "";

    private String tmpVchTplCode = "";
    private String tmpEntityCode = "";

    private VchEntityMABean filterType;

    private String dataSource = "";

    private String valueType = "";

    private String tmpType = "";
    public String getDesc() {
        return desc;
    }

    public String getOperSign() {
        return operSign;
    }

    public VchEntityMABean getFilterType() {
        return filterType;
    }

    public String getMaField() {
        return maField;
    }

    public String getType() {
        return type;
    }

    public void setVchTplCode(String vchTplCode) {
        this.vchTplCode = vchTplCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOperSign(String operSign) {
        this.operSign = operSign;
    }

    public void setFilterType(VchEntityMABean filterType) {
        this.filterType = filterType;
    }

    public void setMaField(String maField) {
        this.maField = maField;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public void setTmpVchTplCode(String tmpVchTplCode) {
        this.tmpVchTplCode = tmpVchTplCode;
    }

    public void setTmpEntityCode(String tmpEntityCode) {
        this.tmpEntityCode = tmpEntityCode;
    }

    public void setTmpType(String tmpType) {
        this.tmpType = tmpType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public void setTmpOrderIndex(String tmpOrderIndex) {
        this.tmpOrderIndex = tmpOrderIndex;
    }

    public void setOrderIndex(String orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void setMaFieldValue(String maFieldValue) {
        this.maFieldValue = maFieldValue;
    }

    public void setMaDictName(String maDictName) {
        this.maDictName = maDictName;
    }

    public void setMaDictCode(String maDictCode) {
        this.maDictCode = maDictCode;
    }

    public void setMaConent(double maConent) {
        this.maConent = maConent;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setOperSignValue(String operSignValue) {
        this.operSignValue = operSignValue;
    }

    public String getVchTplCode() {
        return vchTplCode;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public String getTmpVchTplCode() {
        return tmpVchTplCode;
    }

    public String getTmpEntityCode() {
        return tmpEntityCode;
    }

    public String getTmpType() {
        return tmpType;
    }

    public String getValueType() {
        return valueType;
    }

    public String getTmpOrderIndex() {
        return tmpOrderIndex;
    }

    public String getOrderIndex() {
        return orderIndex;
    }

    public String getMaFieldValue() {
        return maFieldValue;
    }

    public String getMaDictName() {
        return maDictName;
    }

    public String getMaDictCode() {
        return maDictCode;
    }

    public double getMaConent() {
        return maConent;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getOperSignValue() {
        return operSignValue;
    }

    public VchEntityMABean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql =
            "select y.* from " +
            "(select FVchTplCode,FEntityCode,FCheckState from " +
            pub.yssGetTableName("Tb_Vch_EntityMA") + " " +
            " where FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName , " +
            "  d.FDesc as FMAFieldValue,e.FVocName as FOperSignValue, " +
            "  f.FdictName as FMADictValue ,g.FVocName as FValueTypeValue from " +
            pub.yssGetTableName("Tb_Vch_EntityMA") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join(select FAliasName,FDesc from " +
            pub.yssGetTableName("Tb_Vch_DsTabField") +
            " where FVchDsCode=" + dbl.sqlString(this.dataSource) +
            ")d on d.FAliasName=a.FMAField" +
            " left join Tb_Fun_Vocabulary e on a.FOperSign = e.FVocCode and e.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_OperSign) +

            " left join(select distinct FdictCode,FdictName from " +
            pub.yssGetTableName("Tb_Vch_Dict") +
            " )f on f.FdictCode=a.FMADict" +

            " left join Tb_Fun_Vocabulary g on a.FValueType=g.FVocCode and g.FVocTypeCode= " +
            dbl.sqlString(YssCons.YSS_VALUETYPE) +
            buildFilterSql() +
            ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode" +
            //" order by y.FVchTplCode,y.FEntityCode";
            " order by y.FOrderNum"; // by leeyu BUG:000391
        return builderListViewData(strSql);

    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setEntityMaAttr(rs);
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_OperSign + "," +
                                        YssCons.YSS_VALUETYPE);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取分录金额数量信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.vchTplCode.length() != 0) {
                sResult = sResult + " and a.FVchTplCode='" + //Bug编号MS00142 需求单编号QDV4赢时胜上海2009年1月5日01_B modify by 宋洁 2009-01-14 因原来代码查询不准确，会导致查出多余的信息，已经将查询条件由 like改为 = .
                    filterType.vchTplCode.replaceAll("'", "''") + "'"; //Bug编号MS00142 需求单编号QDV4赢时胜上海2009年1月5日01_B modify by 宋洁 2009-01-14 因原来代码查询不准确，会导致查出多余的信息，已经将查询条件由 like改为 = .
            }
            if (this.filterType.entityCode.length() != 0) {
                sResult = sResult + " and a.FEntityCode like '" +
                    filterType.entityCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.type.length() != 0) {
                sResult = sResult + " and a.FType like '" +
                    filterType.type.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.maField.length() != 0) {
                sResult = sResult + " and a.FMAField like '" +
                    filterType.maField.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.operSign.length() != 0) {
                sResult = sResult + " and a.FOperSign like '" +
                    filterType.operSign.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
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
    public String getListViewData3() throws YssException {
        String strSql =
            "select y.* from " +
            "(select FVchTplCode,FEntityCode,FCheckState from " +
            pub.yssGetTableName("Tb_Vch_EntityMA") + " " +
            " where FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ," +
            " d.FDesc as FMAFieldValue,e.FVocName as FOperSignValue ,f.FdictName as FMADictValue from " +
            pub.yssGetTableName("Tb_Vch_EntityMA") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join(select FAliasName,FDesc from " +
            pub.yssGetTableName("Tb_Vch_DsTabField") +
            "  where FVchDsCode=" + dbl.sqlString(this.dataSource) +
            ")d on d.FAliasName=a.FMAField" +
            " left join Tb_Fun_Vocabulary e on a.FOperSign = e.FVocCode and e.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_OperSign) +
            " left join(select distinct FdictCode,FdictName from " +
            pub.yssGetTableName("Tb_Vch_Dict") +
            " )f on f.FdictCode=a.FMADict" +
            " where 1=2" +
            ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode" +
            " order by y.FVchTplCode,y.FEntityCode";
        return builderListViewData(strSql);
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
        Connection con = dbl.loadConnection();
        ResultSet rs = null;
        String strSql = "";
        PreparedStatement pstmt = null;
        String Num = "";
        int i = 0;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                " where FVchTplCode=" + dbl.sqlString(this.tmpVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(this.tmpEntityCode) +
                " and FType=" + dbl.sqlString(this.tmpType);
            rs = dbl.openResultSet(strSql);
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                "(FVchTplCode,FEntityCode,FOrderNum,FType," +
                " FMAConent,FMAField,FMADict,FOperSign,FValueType,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(strSql);
            while (rs.next()) {
                super.parseRecLog();
                //    Num = dbFun.getNextInnerCode(pub.yssGetTableName(
                //          "Tb_Vch_EntityMA"),
                //                                 dbl.sqlRight("FOrderNum", 1), "1",
                //                                " where 1=1 ", 1);
                i = i + 1;
                Num = String.valueOf(i);
                pstmt.setString(1, this.vchTplCode);
                pstmt.setString(2, this.entityCode);
                pstmt.setString(3, Num);
                if (rs.getString("FType") == null) {
                    pstmt.setString(4, "");
                } else {
                    pstmt.setString(4, rs.getString("FType"));
                }
                pstmt.setDouble(5, rs.getDouble("FMAConent"));
                if (rs.getString("FMAField") == null) {
                    pstmt.setString(6, "");
                } else {
                    pstmt.setString(6, rs.getString("FMAField"));
                }
                if (rs.getString("FMADict") == null) {
                    pstmt.setString(7, "");
                } else {
                    pstmt.setString(7, rs.getString("FMADict"));
                }
                if (rs.getString("FOperSign") == null) {
                    pstmt.setString(8, "");
                } else {
                    pstmt.setString(8, rs.getString("FOperSign"));
                }
                if (rs.getString("FValueType") == null) {
                    pstmt.setString(9, "");
                } else {
                    pstmt.setString(9, rs.getString("FValueType"));
                }
                if (rs.getString("FDesc") == null) {
                    pstmt.setString(10, "");
                } else {
                    pstmt.setString(10, rs.getString("FDesc"));
                }
                pstmt.setInt(11, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(12, this.creatorCode);
                pstmt.setString(13, this.creatorTime);
                pstmt.setString(14,
                                (pub.getSysCheckState() ? " " : this.creatorCode));
                pstmt.executeUpdate();

            }
            return "";
        } catch (Exception e) {
            throw new YssException("新增科目信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
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
        PreparedStatement pstmt = null;
        Connection con = dbl.loadConnection();
        String sql = "";
        String Num = "";
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            sql = "delete from " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                " where FVchTplCode=" + dbl.sqlString(this.tmpVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(this.tmpEntityCode) +
                " and FType=" + dbl.sqlString(this.tmpType);
            dbl.executeSql(sql);

            sql = "insert into " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                "(FVchTplCode,FEntityCode,FOrderNum,FType," +
                " FMAConent,FMAField,FMADict,FOperSign,FValueType,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(sql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
//            this.orderIndex =
//                  dbFun.getNextInnerCode("Tb_001_Vch_EntityMA",
//                                         dbl.sqlRight("FOrderNum", 1), "1",
//                                         " where 1=1", 1);
                this.parseRowStr(sMutilRowAry[i]);
                if (this.tmpVchTplCode.trim().length() > 0 && this.valueType.trim().length() > 0) {
                    Num = String.valueOf(i + 1);
                    pstmt.setString(1, this.tmpVchTplCode);
                    pstmt.setString(2, this.tmpEntityCode);
                    pstmt.setString(3, Num);
                    pstmt.setString(4, this.tmpType);
                    pstmt.setDouble(5, this.maConent);
                    pstmt.setString(6, this.maField);
                    pstmt.setString(7, this.maDictCode);
                    pstmt.setString(8, this.operSign);
                    pstmt.setString(9, this.valueType);
                    pstmt.setString(10, this.desc);

                    pstmt.setInt(11, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(12, this.creatorCode);
                    pstmt.setString(13, this.creatorTime);
                    pstmt.setString(14,
                                    (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new YssException("保存分录摘要信息出错\r\n" + ex.getMessage());
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
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

        buf.append(this.type).append("\t");
        buf.append(this.maField).append("\t");
        buf.append(this.maFieldValue).append("\t");
        buf.append(this.operSign).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.tmpOrderIndex).append("\t");
        buf.append(this.maConent).append("\t");
        buf.append(this.maDictCode).append("\t");
        buf.append(this.maDictName).append("\t");
        buf.append(this.valueType).append("\t");
        buf.append(this.operSignValue).append("\t");
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
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.vchTplCode = reqAry[0];
            this.entityCode = reqAry[1];
            this.type = reqAry[2];
            this.maField = reqAry[3];
            this.operSign = reqAry[4];
            this.desc = reqAry[5];
            if (reqAry[6].length() > 0) {
                this.maConent = Double.parseDouble(reqAry[6]);
            }
            this.maDictCode = reqAry[7];
            this.dataSource = reqAry[8];
            this.valueType = reqAry[9];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchEntityMABean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析分录信息出错");
        }

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */

    public void setEntityMaAttr(ResultSet rs) throws SQLException {
        this.type = rs.getString("FType");
        this.maField = rs.getString("FMAField");
        this.maFieldValue = rs.getString("FMAFieldValue");
        this.operSign = rs.getString("FOperSign");
        this.desc = rs.getString("FDesc");
        this.maConent = rs.getDouble("FMAConent");
        this.maDictCode = rs.getString("FMADict");
        this.maDictName = rs.getString("FMADictValue");
        this.tmpOrderIndex = String.valueOf(rs.getInt("FOrderNum"));
        this.valueType = rs.getString("FValueType");
        this.operSignValue = rs.getString("FOperSignValue"); // add liyu 1121
        super.setRecLog(rs);
    }

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
