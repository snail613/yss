package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchEntityResumeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String vchTplCode = "";
    private String entityCode = "";

    private String resumeFieldCode = "";
    private String resumeFieldName = "";
    private String desc = "";

    private String dictCode = "";
    private String dictName = "";
    private String resumeConent = "";

    private String orderIndex = "";

    private String tmpOrderIndex = "";

    private String dataSource = "";

    private String tmpVchTplCode = "";
    private String tmpEntityCode = "";

    private VchEntityResumeBean filterType = null;

    private String valueType = "";

    public String getDesc() {
        return desc;
    }

    public VchEntityResumeBean getFilterType() {
        return filterType;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setVchTplCode(String vchTplCode) {
        this.vchTplCode = vchTplCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setFilterType(VchEntityResumeBean filterType) {
        this.filterType = filterType;
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

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public void setTmpOrderIndex(String tmpOrderIndex) {
        this.tmpOrderIndex = tmpOrderIndex;
    }

    public void setResumeFieldName(String resumeFieldName) {
        this.resumeFieldName = resumeFieldName;
    }

    public void setResumeFieldCode(String resumeFieldCode) {
        this.resumeFieldCode = resumeFieldCode;
    }

    public void setResumeConent(String resumeConent) {
        this.resumeConent = resumeConent;
    }

    public void setOrderIndex(String orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getVchTplCode() {
        return vchTplCode;
    }

    public String getTmpVchTplCode() {
        return tmpVchTplCode;
    }

    public String getTmpEntityCode() {
        return tmpEntityCode;
    }

    public String getValueType() {
        return valueType;
    }

    public String getTmpOrderIndex() {
        return tmpOrderIndex;
    }

    public String getResumeFieldName() {
        return resumeFieldName;
    }

    public String getResumeFieldCode() {
        return resumeFieldCode;
    }

    public String getResumeConent() {
        return resumeConent;
    }

    public String getOrderIndex() {
        return orderIndex;
    }

    public String getDictName() {
        return dictName;
    }

    public String getDictCode() {
        return dictCode;
    }

    public String getDataSource() {
        return dataSource;
    }

    public VchEntityResumeBean() {
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
            pub.yssGetTableName("Tb_Vch_EntityResume") + " " +
            " where FVchTplCode =" + dbl.sqlString(this.vchTplCode) +
            " and FEntityCode=" + dbl.sqlString(this.entityCode) +
            " and FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ," +
            "  d.FDictName as FResumeDictValue ,e.FDesc as FResumeFieldValue,f.FVocName as FValueTypeValue from " +
            pub.yssGetTableName("Tb_Vch_EntityResume") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join(select distinct FDictCode,FDictName from " + pub.yssGetTableName("Tb_Vch_Dict") +
            " ) d on d.FDictCode=a.FResumeDict " +
            " left join(select FAliasName,FDesc from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
            " where FVchDsCode=" + dbl.sqlString(this.dataSource) + ") e on e.FAliasName=a.FResumeField" +

            " left join Tb_Fun_Vocabulary f on a.FValueType = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_VALUETYPE) +
            buildFilterSql() +
            ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode" +
            " order by y.FVchTplCode,y.FEntityCode";
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
                setEntityResumeAttr(rs);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_VALUETYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取分录摘要信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setEntityResumeAttr(ResultSet rs) throws YssException {
        try {

            this.resumeFieldCode = rs.getString("FResumeField");
            this.resumeFieldName = rs.getString("FResumeFieldValue");
            this.dictCode = rs.getString("FResumeDict");
            this.dictName = rs.getString("FResumeDictValue");
            this.resumeConent = rs.getString("FResumeConent");
            this.desc = rs.getString("FDesc");
            this.valueType = rs.getString("FValueType");
            this.tmpOrderIndex = String.valueOf(rs.getInt("FOrderNum"));
        } catch (Exception e) {
            throw new YssException(e.getMessage());
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
            pub.yssGetTableName("Tb_Vch_EntityResume") + " " +
            " where FVchTplCode =" + dbl.sqlString(this.vchTplCode) +
            " and FEntityCode=" + dbl.sqlString(this.entityCode) +
            " and FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ," +
            "  d.FDictName as FResumeDictValue ,e.FDesc as FResumeFieldValue ,f.FVocName as FValueTypeValue from " +
            pub.yssGetTableName("Tb_Vch_EntityResume") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join(select distinct FDictCode,FDictName from " + pub.yssGetTableName("Tb_Vch_Dict") +
            " ) d on d.FDictCode=a.FResumeDict" +
            " left join(select FAliasName,FDesc from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
            " where FVchDsCode=" + dbl.sqlString(this.dataSource) + ") e on e.FAliasName=a.FResumeField " +
            " left join Tb_Fun_Vocabulary f on a.FValueType = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_VALUETYPE) +
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
    public String getListViewData4() throws YssException {
        String strSql =
            "select y.* from " +
            "(select FVchTplCode,FEntityCode,FCheckState from " +
            pub.yssGetTableName("Tb_Vch_EntityResume") + " " +
            " where FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ," +
            "  e.FDesc as FResumeFieldValue,d.FDictName as FResumeDictValue, f.FVocName as FValueTypeValue from " +
            pub.yssGetTableName("Tb_Vch_EntityResume") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join(select distinct FDictCode,FDictName from " + pub.yssGetTableName("Tb_Vch_Dict") +
            " ) d on d.FDictCode=a.FResumeDict" +
            " left join(select FAliasName,FDesc from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
            " where FVchDsCode=" + dbl.sqlString(this.dataSource) + ") e on e.FAliasName=a.FResumeField " +
            " left join Tb_Fun_Vocabulary f on a.FValueType = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_VALUETYPE) +
            buildFilterSql() +
            ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode" +
            //" order by y.FVchTplCode,y.FEntityCode";
            " order by y.FOrderNum"; // by leeyu BUG:000391
        return builderListViewData(strSql);

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
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
            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                " where FVchTplCode=" + dbl.sqlString(this.tmpVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(this.tmpEntityCode);
            rs = dbl.openResultSet(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                "(FVchTplCode,FEntityCode,FOrderNum," +
                " FResumeDict,FResumeField,FResumeConent,FValueType,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(strSql);
            while (rs.next()) {
                super.parseRecLog();
                //    Num = dbFun.getNextInnerCode(pub.yssGetTableName(
                //        "Tb_Vch_EntityResume"),
                //                       dbl.sqlRight("FOrderNum", 1), "1",
                //                       " where 1=1", 1);
                i = i + 1;
                Num = String.valueOf(i);
                pstmt.setString(1, this.vchTplCode);
                pstmt.setString(2, this.entityCode);
                pstmt.setString(3, Num);
                if (rs.getString("FResumeDict") == null) {
                    pstmt.setString(4, "");
                } else {
                    pstmt.setString(4, rs.getString("FResumeDict"));
                }
                if (rs.getString("FResumeField") == null) {
                    pstmt.setString(5, "");
                } else {
                    pstmt.setString(5, rs.getString("FResumeField"));
                }
                if (rs.getString("FResumeConent") == null) {
                    pstmt.setString(6, "");
                } else {
                    pstmt.setString(6, rs.getString("FResumeConent"));
                }
                if (rs.getString("FValueType") == null) {

                    pstmt.setString(7, "");
                } else {
                    pstmt.setString(7, rs.getString("FValueType"));
                }
                if (rs.getString("FDesc") == null) {
                    pstmt.setString(8, "");
                } else {
                    pstmt.setString(8, rs.getString("FDesc"));
                }
                pstmt.setInt(9, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(10, this.creatorCode);
                pstmt.setString(11, this.creatorTime);
                pstmt.setString(12, (pub.getSysCheckState() ? " " : this.creatorCode));
                pstmt.executeUpdate();
            }
            return "";
        } catch (Exception e) {
            throw new YssException("新增摘要信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
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
     * delSetting
     */
    public void delSetting() {
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
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
            sql = "delete from " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                " where FVchTplCode=" + dbl.sqlString(this.tmpVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(this.tmpEntityCode);
            dbl.executeSql(sql);

            sql = "insert into " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                "(FVchTplCode,FEntityCode,FOrderNum," +
                " FResumeDict,FResumeField,FResumeConent,FValueType,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(sql);

            for (int i = 0; i < sMutilRowAry.length; i++) {

                this.parseRowStr(sMutilRowAry[i]);
                if (this.tmpVchTplCode.trim().length() > 0) {
                    Num = String.valueOf(i + 1);
                    pstmt.setString(1, this.tmpVchTplCode);
                    pstmt.setString(2, this.tmpEntityCode);
                    pstmt.setString(3, Num);
                    pstmt.setString(4, this.dictCode);
                    pstmt.setString(5, this.resumeFieldCode);
                    pstmt.setString(6, this.resumeConent);
                    pstmt.setString(7, this.valueType);
                    pstmt.setString(8, this.desc);
                    pstmt.setInt(9, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(10, this.creatorCode);
                    pstmt.setString(11, this.creatorTime);
                    pstmt.setString(12, (pub.getSysCheckState() ? " " : this.creatorCode));
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
     * getSetting
     *
     * @return IDataSetting
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
            this.dictCode = reqAry[2];
            this.resumeFieldCode = reqAry[3];
            this.resumeConent = reqAry[4];
            this.desc = reqAry[5];
            this.dataSource = reqAry[6];
            this.valueType = reqAry[7];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchEntityResumeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析分录摘要信息出错");
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();

        buf.append(this.dictCode).append("\t");
        buf.append(this.dictName).append("\t");
        buf.append(this.resumeFieldCode).append("\t");
        buf.append(this.resumeFieldName).append("\t");
        buf.append(this.resumeConent).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.tmpOrderIndex).append("\t");
        buf.append(this.valueType).append("\t");

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
