package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchEntitySubjectBean
    extends BaseDataSettingBean implements IDataSetting {

    private String vchTplCode = "";
    private String entityCode = "";
    private String subjectDictCode = "";
    private String subjectDictName = "";
    private String subjectFieldCode = "";
    private String subjectFieldName = "";

    private String subjectConnet = "";
    private String desc = "";

    private String dataSource = "";

    private String orderIndex = "";

    private String tmpOrderIndex = "";

    private String tmpVchTplCode = "";
    private String tmpEntityCode = "";

    private String valueType = "";

    private VchEntitySubjectBean filterType;
    public String getDesc() {
        return desc;
    }

    public VchEntitySubjectBean getFilterType() {
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

    public void setFilterType(VchEntitySubjectBean filterType) {
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

    public void setSubjectFieldName(String subjectFieldName) {
        this.subjectFieldName = subjectFieldName;
    }

    public void setSubjectFieldCode(String subjectFieldCode) {
        this.subjectFieldCode = subjectFieldCode;
    }

    public void setSubjectDictName(String subjectDictName) {
        this.subjectDictName = subjectDictName;
    }

    public void setSubjectDictCode(String subjectDictCode) {
        this.subjectDictCode = subjectDictCode;
    }

    public void setSubjectConnet(String subjectConnet) {
        this.subjectConnet = subjectConnet;
    }

    public void setOrderIndex(String orderIndex) {
        this.orderIndex = orderIndex;
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

    public String getSubjectFieldName() {
        return subjectFieldName;
    }

    public String getSubjectFieldCode() {
        return subjectFieldCode;
    }

    public String getSubjectDictName() {
        return subjectDictName;
    }

    public String getSubjectDictCode() {
        return subjectDictCode;
    }

    public String getSubjectConnet() {
        return subjectConnet;
    }

    public String getOrderIndex() {
        return orderIndex;
    }

    public String getDataSource() {
        return dataSource;
    }

    public VchEntitySubjectBean() {
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
            pub.yssGetTableName("Tb_Vch_EntitySubject") + " " +
            " where FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName " +
            "  ,d.FDesc as FSubjectFieldValue ,e.FDictName as FSubjectDictValue,f.FVocName as  FValueTypeValue from " +
            pub.yssGetTableName("Tb_Vch_EntitySubject") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join(select FAliasName,FDesc from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
            " where FVchDsCode=" + dbl.sqlString(this.dataSource) + ")d on d.FAliasName=a.FSubjectField" +
            " left join(select distinct FDictCode,FDictName from " + pub.yssGetTableName("Tb_Vch_Dict") +
            " where FCheckState=1)e on e.FDictCode=a.FSubjectDict" +

            " left join Tb_Fun_Vocabulary f on a.FValueType=f.FVocCode and f.FVocTypeCode= " +
            dbl.sqlString(YssCons.YSS_VALUETYPE) +
            buildFilterSql() +
            ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode" +
            //" order by y.FVchTplCode,y.FEntityCode,y.FOrderNum";
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
                setEntitySubjectAttr(rs);
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
            throw new YssException("获取分录科目信息出错！", e);
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

            if (this.filterType.subjectDictCode.length() != 0) {
                sResult = sResult + " and a.FSubjectDict like '" +
                    filterType.subjectDictName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.subjectFieldCode.length() != 0) {
                sResult = sResult + " and a.FSubjectField like '" +
                    filterType.subjectFieldCode.replaceAll("'", "''") + "%'";
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
            pub.yssGetTableName("Tb_Vch_EntitySubject") + " " +
            " where FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName " +
            "  ,d.FDesc as FSubjectFieldValue ,e.FDictName as FSubjectDictValue ,f.FVocName as FValueTypeValue from " +
            pub.yssGetTableName("Tb_Vch_EntitySubject") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join(select FAliasName,FDesc from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
            " where FVchDsCode=" + dbl.sqlString(this.dataSource) + ")d on d.FAliasName=a.FSubjectField" +
            " left join(select distinct FDictCode,FDictName from " + pub.yssGetTableName("Tb_Vch_Dict") +
            " where FCheckState=1)e on e.FDictCode=a.FSubjectDict" +
            " left join Tb_Fun_Vocabulary f on a.FValueType=f.FVocCode and f.FVocTypeCode= " +
            dbl.sqlString(YssCons.YSS_VALUETYPE) +
            " where 1=2" +
            ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode" +
            " order by y.FVchTplCode,y.FEntityCode,y.FOrderNum";
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
            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_EntitySubject") +
                " where FVchTplCode=" + dbl.sqlString(this.tmpVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(this.tmpEntityCode);
            rs = dbl.openResultSet(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Vch_EntitySubject") +
                "(FVchTplCode,FEntityCode,FOrderNum," +
                " FSubjectConent,FSubjectField,FSubjectDict,FValueType,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(strSql);
            while (rs.next()) {
                super.parseRecLog();
                //      Num = dbFun.getNextInnerCode(pub.yssGetTableName(
                //           "Tb_Vch_EntitySubject"),
                //                          dbl.sqlRight("FOrderNum", 1), "1",
                //                         " where 1=1", 1);
                i = i + 1;
                Num = String.valueOf(i);
                pstmt.setString(1, this.vchTplCode);
                pstmt.setString(2, this.entityCode);
                pstmt.setString(3, Num);
                if (rs.getString("FSubjectConent") == null) {
                    pstmt.setString(4, "");
                } else {
                    pstmt.setString(4, rs.getString("FSubjectConent"));
                }
                if (rs.getString("FSubjectField") == null) {
                    pstmt.setString(5, "");
                } else {
                    pstmt.setString(5, rs.getString("FSubjectField"));
                }
                if (rs.getString("FSubjectDict") == null) {
                    pstmt.setString(6, "");
                } else {
                    pstmt.setString(6, rs.getString("FSubjectDict"));
                }
                if (rs.getString("FValueType") == null) {
                    pstmt.setString(7, "");
                } else {
                    pstmt.setString(7, rs.getString("FValueType"));
                }
                if (rs.getString("FDesc") == null) {
                    pstmt.setString(8, "");
                } else {
                    pstmt.setString(8, this.desc);
                }
                pstmt.setInt(9, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(10, this.creatorCode);
                pstmt.setString(11, this.creatorTime);
                pstmt.setString(12, (pub.getSysCheckState() ? " " : this.creatorCode));
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
        String[] sMutilRowStrAry = null;
        PreparedStatement pstmt = null;
        Connection con = dbl.loadConnection();
        String sql = "";
        String Num = "";
        try {

            sMutilRowStrAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            sql = "delete from " + pub.yssGetTableName("Tb_Vch_EntitySubject") +
                " where FVchTplCode=" + dbl.sqlString(this.tmpVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(this.tmpEntityCode);
            dbl.executeSql(sql);

            sql = "insert into " + pub.yssGetTableName("Tb_Vch_EntitySubject") +
                "(FVchTplCode,FEntityCode,FOrderNum," +
                " FSubjectConent,FSubjectField,FSubjectDict,FValueType,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(sql);
            for (int i = 0; i < sMutilRowStrAry.length; i++) {

                this.parseRowStr(sMutilRowStrAry[i]);

                if (this.tmpVchTplCode.trim().length() > 0) {
                    Num = String.valueOf(i + 1);
                    pstmt.setString(1, this.tmpVchTplCode);
                    pstmt.setString(2, this.tmpEntityCode);
                    pstmt.setString(3, Num);
                    pstmt.setString(4, this.subjectConnet);
                    pstmt.setString(5, this.subjectFieldCode);
                    pstmt.setString(6, this.subjectDictCode);
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
            throw new YssException("保存分录科目信息出错\r\n" + ex.getMessage());
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
        buf.append(this.subjectDictCode).append("\t");
        buf.append(this.subjectDictName).append("\t");
        buf.append(this.subjectFieldCode).append("\t");
        buf.append(this.subjectFieldName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.tmpOrderIndex).append("\t");
        buf.append(this.subjectConnet).append("\t");
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
            this.subjectDictCode = reqAry[2];
            this.subjectFieldCode = reqAry[3];
            this.desc = reqAry[4];
            this.subjectConnet = reqAry[5];
            this.dataSource = reqAry[6];
            this.valueType = reqAry[7];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchEntitySubjectBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析分录科目信息出错");
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

    public void setEntitySubjectAttr(ResultSet rs) throws YssException {
        try {
            this.subjectDictCode = rs.getString("FSubjectDict");
            this.subjectDictName = rs.getString("FSubjectDictValue");
            this.subjectFieldCode = rs.getString("FSubjectField");
            this.subjectFieldName = rs.getString("FSubjectFieldValue");
            this.desc = rs.getString("FDesc");
            this.subjectConnet = rs.getString("FSubjectConent");
            this.valueType = rs.getString("FValueType");
            this.tmpOrderIndex = String.valueOf(rs.getInt("FOrderNum"));
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

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
