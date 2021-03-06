package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchEntityCondBean
    extends BaseDataSettingBean implements IDataSetting {

    private String vchTplCode = "";
    private String entityCode = "";
    private String conRela = "";
    private String fieldName = "";
    private String fieldNameValue = "";
    private String sign = "";
    private String valueSource = "";
    private String value = "";
    private String desc = "";

    private String orderIndex = "";

    private String tmpOrderIndex = "";

    private String tmpVchTplCode = "";
    private String tmpEntityCode = "";

    private VchEntityCondBean filterType;
    public String getValueSource() {
        return valueSource;
    }

    public String getFieldName() {
        return fieldName;
    }

    public VchEntityCondBean getFilterType() {
        return filterType;
    }

    public String getConRela() {
        return conRela;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public String getValue() {
        return value;
    }

    public void setVchTplCode(String vchTplCode) {
        this.vchTplCode = vchTplCode;
    }

    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFilterType(VchEntityCondBean filterType) {
        this.filterType = filterType;
    }

    public void setConRela(String conRela) {
        this.conRela = conRela;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTmpVchTplCode(String tmpVchTplCode) {
        this.tmpVchTplCode = tmpVchTplCode;
    }

    public void setTmpEntityCode(String tmpEntityCode) {
        this.tmpEntityCode = tmpEntityCode;
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

    public VchEntityCondBean() {
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
            pub.yssGetTableName("Tb_Vch_EntityCond") + " " +
            " where FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
            " d.FVocName as FConRelaValue ,e.FVocName as FSignValue ," +
            " h.FDesc as FFieldNameValue," +
            " g.FVocName as FValueSourceValue from " +
            pub.yssGetTableName("Tb_Vch_EntityCond") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join Tb_Fun_Vocabulary d on a.FConRela = d.FVocCode and d.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CONReal) +
            " left join Tb_Fun_Vocabulary e on a.FConRela = e.FVocCode and e.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_SIGN) +
            " left join Tb_Fun_Vocabulary g on a.FValueSource = g.FVocCode and g.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_VALSource) +
            " left join(select FAliasName,FDesc from " +
            pub.yssGetTableName("Tb_Vch_DsTabField") +
            " )h on h.FAliasName=a.FFieldName" +
            buildFilterSql() +
            ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode" +
            //" order by y.FVchTplCode,y.FEntityCode";
            " order by y.FOrderIndex"; // by leeyu BUG:000391
        return builderListViewData(strSql);
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
            pub.yssGetTableName("Tb_Vch_EntityCond") + " " +
            " where FVchTplCode =" + dbl.sqlString(this.vchTplCode) +
            " and FEntityCode=" + dbl.sqlString(this.entityCode) +
            " and FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
            " d.FVocName as FConRelaValue ,e.FVocName as FSignValue ," +
            " h.FDesc as FFieldNameValue, " +
            "g.FVocName as FValueSourceValue from " +
            pub.yssGetTableName("Tb_Vch_EntityCond") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join Tb_Fun_Vocabulary d on a.FConRela = d.FVocCode and d.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CONReal) +
            " left join Tb_Fun_Vocabulary e on a.FConRela = e.FVocCode and e.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_SIGN) +
            " left join Tb_Fun_Vocabulary g on a.FValueSource = g.FVocCode and g.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_VALSource) +
            " left join(select FAliasName,FDesc from " +
            pub.yssGetTableName("Tb_Vch_DsTabField") +
            " )h on h.FAliasName=a.FFieldName" +
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
                setEntityAttr(rs);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_CONReal + "," +
                                        YssCons.YSS_SIGN + "," +
                                        YssCons.YSS_VALSource);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取证券分录信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setEntityAttr(ResultSet rs) throws SQLException {
        //   this.vchTplCode =rs.getString("FVchTplCode");
        //    this.entityCode =rs.getString("FEntityCode");
        this.conRela = rs.getString("FConRela");
        this.fieldName = rs.getString("FFieldName");
        this.fieldNameValue = rs.getString("FFieldNameValue");
        this.sign = rs.getString("FSign");
        this.valueSource = rs.getString("FValueSource");
        this.value = rs.getString("FValue");
        this.desc = rs.getString("FDesc");
        this.tmpOrderIndex = String.valueOf(rs.getInt("FOrderIndex"));
        ;
        super.setRecLog(rs);
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
            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_EntityCond") +
                " where FVchTplCode=" + dbl.sqlString(this.tmpVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(this.tmpEntityCode);
            rs = dbl.openResultSet(strSql);
            strSql =
                " insert into " + pub.yssGetTableName("Tb_Vch_EntityCond") +
                " (FVchTplCode,FEntityCode,FOrderIndex," +
                " FConRela,FFieldName,FSign,FValueSource,FValue,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(strSql);
            while (rs.next()) {
                super.parseRecLog();
                //     Num = dbFun.getNextInnerCode(pub.yssGetTableName(
                //          "Tb_Vch_EntityCond"),
                //                                 dbl.sqlRight("FOrderNum", 1), "1",
                //                                 " where 1=1", 1);
                i = i + 1;
                Num = String.valueOf(i);
                pstmt.setString(1, this.vchTplCode);
                pstmt.setString(2, this.entityCode);
                pstmt.setString(3, Num);
                if (rs.getString("FConRela") == null) {
                    pstmt.setString(4, "");
                } else {
                    pstmt.setString(4, rs.getString("FConRela"));
                }
                if (rs.getString("FFieldName") == null) {
                    pstmt.setString(5, "");
                } else {
                    pstmt.setString(5, rs.getString("FFieldName"));
                }
                if (rs.getString("FSign") == null) {
                    pstmt.setString(6, "");
                } else {
                    pstmt.setString(6, rs.getString("FSign"));
                }
                if (rs.getString("FValueSource") == null) {
                    pstmt.setString(7, "");
                } else {
                    pstmt.setString(7, rs.getString("FValueSource"));
                }
                if (rs.getString("FValue") == null) {
                    pstmt.setString(8, "");
                } else {
                    pstmt.setString(8, rs.getString("FValue"));
                }
                if (rs.getString("FDesc") == null) {
                    pstmt.setString(9, rs.getString("FDesc"));
                }
                pstmt.setInt(10, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(11, this.creatorCode);
                pstmt.setString(12, this.creatorTime);
                pstmt.setString(13,
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
        Connection conn = dbl.loadConnection();
        String strSql = "";
        String Num = "";
        try {

            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            strSql = "delete from " + pub.yssGetTableName("Tb_Vch_EntityCond") +
                " where FVchTplCode = " +
                dbl.sqlString(this.tmpVchTplCode) + " and FEntityCode = " +
                dbl.sqlString(this.tmpEntityCode);
            dbl.executeSql(strSql);
            strSql =
                " insert into " + pub.yssGetTableName("Tb_Vch_EntityCond") +
                " (FVchTplCode,FEntityCode,FOrderIndex," +
                " FConRela,FFieldName,FSign,FValueSource,FValue,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
//            this.orderIndex =
//                  dbFun.getNextInnerCode(pub.yssGetTableName(
//                  "Tb_Vch_EntityCond"),
//                                         dbl.sqlRight("FOrderIndex", 1), "1",
//                                         " where 1=1", 1);
                this.parseRowStr(sMutilRowAry[i]);
                if (this.tmpVchTplCode.trim().length() > 0) {
                    Num = String.valueOf(i + 1);
                    pstmt.setString(1, this.tmpVchTplCode);
                    pstmt.setString(2, this.tmpEntityCode);
                    pstmt.setString(3, Num);
                    pstmt.setString(4, this.conRela);
                    pstmt.setString(5, this.fieldName);
                    pstmt.setString(6, this.sign);
                    pstmt.setString(7, this.valueSource);
                    pstmt.setString(8, this.value);
                    pstmt.setString(9, this.desc);
                    pstmt.setInt(10, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(11, this.creatorCode);
                    pstmt.setString(12, this.creatorTime);
                    pstmt.setString(13,
                                    (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.executeUpdate();
                }
            }

        } catch (Exception e) {
            throw new YssException("保存分录条件信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            return "";
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.fieldName).append("\t");
        buf.append(this.fieldNameValue).append("\t");

        buf.append(this.valueSource).append("\t");
        buf.append(this.value).append("\t");
        buf.append(this.conRela).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.tmpOrderIndex).append("\t");
        buf.append(this.sign).append("\t");//add by zhouwei 20120315 条件符号
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
            this.conRela = reqAry[2];
            this.fieldName = reqAry[3];
            this.sign = reqAry[4];
            this.valueSource = reqAry[5];
            this.value = reqAry[6];
            this.desc = reqAry[7];
            //    this.checkStateId = Integer.parseInt(reqAry[10]);
            //    this.orderIndex=reqAry[8];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchEntityCondBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析分录条件信息出错", e);
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

    /**
     * buildFilterSql
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.vchTplCode.length() != 0) {
                sResult = sResult + " and a.FVchTplCode like '" +
                    filterType.vchTplCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.entityCode.length() != 0) {
                sResult = sResult + " and a.FEntityCode like '" +
                    filterType.entityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.conRela.length() != 0) {
                sResult = sResult + " and a.FConRela like '" +
                    filterType.conRela.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.fieldName.length() != 0) {
                sResult = sResult + " and a.FFieldName like '" +
                    filterType.fieldName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sign.length() != 0) {
                sResult = sResult + " and a.FSign like '" +
                    filterType.sign.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.valueSource.length() != 0) {
                sResult = sResult + " and a.FValueSource like '" +
                    filterType.valueSource.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.value.length() != 0) {
                sResult = sResult + " and a.FValue like '" +
                    filterType.value.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
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
