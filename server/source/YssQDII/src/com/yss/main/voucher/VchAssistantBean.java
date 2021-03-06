package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class VchAssistantBean
    extends BaseDataSettingBean implements IDataSetting {
    private String ValueType = "";
    private String assistantConent = "";
    private String assistantFieldCode = "";
    private String assistantFieldName = "";
    private String dataSource = "";

    private String assistantDictCode = "";
    private String assistantDictName = "";
    private String desc;
    private String vchTplCode;
    private String entityCode;
    private String ValueTypeValue;
    //  private String orderNum="";     //�����

    private VchAssistantBean filterType;

    private String orderIndex = "";

    private String tmpOrderIndex = "";

    private String tmpVchTplCode = "";
    private String tmpEntityCode = "";

    public VchAssistantBean() {
    }

    public void setValueType(String ValueType) {
        this.ValueType = ValueType;
    }

    public String getValueType() {
        return ValueType;
    }

    public void setAssistantConent(String assistantConent) {
        this.assistantConent = assistantConent;
    }

    public void setAssistantFieldCode(String assistantField) {
        this.assistantFieldCode = assistantField;
    }

    public void setAssistantDictCode(String assistantDictCode) {
        this.assistantDictCode = assistantDictCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setFilterType(VchAssistantBean filterType) {
        this.filterType = filterType;
    }

    public void setVchTplCode(String vchTplCode) {
        this.vchTplCode = vchTplCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    //  public void setOrderNum(String orderNum) {
    //    this.orderNum = orderNum;
    //  }

    public void setAssistantFieldName(String assistantFieldName) {
        this.assistantFieldName = assistantFieldName;
    }

    public void setAssistantDictName(String assistantDictName) {
        this.assistantDictName = assistantDictName;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setTmpOrderIndex(String tmpOrderIndex) {
        this.tmpOrderIndex = tmpOrderIndex;
    }

    public void setOrderIndex(String orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void setTmpVchTplCode(String tmpVchTplCode) {
        this.tmpVchTplCode = tmpVchTplCode;
    }

    public void setTmpEntityCode(String tmpEntityCode) {
        this.tmpEntityCode = tmpEntityCode;
    }

    public void setValueTypeValue(String ValueTypeValue) {
        this.ValueTypeValue = ValueTypeValue;
    }

    public String getAssistantConent() {
        return assistantConent;
    }

    public String getAssistantFieldCode() {
        return assistantFieldCode;
    }

    public String getAssistantDictCode() {
        return assistantDictCode;
    }

    public String getDesc() {
        return desc;
    }

    public VchAssistantBean getFilterType() {
        return filterType;
    }

    public String getVchTplCode() {
        return vchTplCode;
    }

    public String getEntityCode() {
        return entityCode;
    }

    //  public String getOrderNum() {
    //     return orderNum;
    // }

    public String getAssistantFieldName() {
        return assistantFieldName;
    }

    public String getAssistantDictName() {
        return assistantDictName;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getTmpOrderIndex() {
        return tmpOrderIndex;
    }

    public String getOrderIndex() {
        return orderIndex;
    }

    public String getTmpVchTplCode() {
        return tmpVchTplCode;
    }

    public String getTmpEntityCode() {
        return tmpEntityCode;
    }

    public String getValueTypeValue() {
        return ValueTypeValue;
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        String strSql = "";
        PreparedStatement pstmt = null;
        String Num = "";
        int i = 0;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_Assistant") +
                " where FVchTplCode=" + dbl.sqlString(this.tmpVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(this.tmpEntityCode);
            rs = dbl.openResultSet(strSql);
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Vch_Assistant") +
                "(FVchTplCode,FEntityCode,FOrderNum,FValueType," +
                " FAssistantConent,FAssistantField,FAssistantDict,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);
            while (rs.next()) {
                super.parseRecLog();
                //     Num = dbFun.getNextInnerCode(pub.yssGetTableName(
                //           "Tb_Vch_Assistant"),
                //                                 dbl.sqlRight("FOrderNum", 1), "1",
                //                                " where 1=1", 1);
                i = i + 1;
                Num = String.valueOf(i);
                pstmt.setString(1, vchTplCode);
                pstmt.setString(2, entityCode);
                pstmt.setString(3, Num);
                if (rs.getString("FValueType") == null) {
                    pstmt.setString(4, "");
                } else {
                    pstmt.setString(4, rs.getString("FValueType"));
                }
                if (rs.getString("FAssistantConent") == null) {
                    pstmt.setString(5, "");
                } else {
                    pstmt.setString(5, rs.getString("FAssistantConent"));
                }
                if (rs.getString("FAssistantField") == null) {
                    pstmt.setString(6, "");
                } else {
                    pstmt.setString(6, rs.getString("FAssistantField"));
                }
                if (rs.getString("FAssistantDict") == null) {
                    pstmt.setString(7, "");
                } else {
                    pstmt.setString(7, rs.getString("FAssistantDict"));
                }
                pstmt.setString(8, this.desc);
                pstmt.setInt(9, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(10, this.creatorCode);
                pstmt.setString(11, this.creatorTime);
                pstmt.setString(12,
                                (pub.getSysCheckState() ? " " : this.creatorCode));
                pstmt.executeUpdate();
            }
            return "";
        } catch (Exception e) {
            throw new YssException("�������������ó���!");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }

    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {
    }

    public void checkSetting() throws YssException {
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection con = dbl.loadConnection();
        String sql = "";
        String Num = "";
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            sql = "delete from " + pub.yssGetTableName("Tb_Vch_Assistant") +
                " where FVchTplCode=" + dbl.sqlString(this.tmpVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(this.tmpEntityCode);
            dbl.executeSql(sql);

            sql = "insert into " + pub.yssGetTableName("Tb_Vch_Assistant") +
                "(FVchTplCode,FEntityCode,FOrderNum,FValueType," +
                " FAssistantConent,FAssistantField,FAssistantDict,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(sql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                //     Num =
                //           dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Vch_Assistant"),
                //                                  dbl.sqlRight("FOrderNum", 1), "1",
                //                                 " where 1=1", 1);

                this.parseRowStr(sMutilRowAry[i]);
                if (this.tmpVchTplCode.trim().length() > 0 && ValueType.trim().length() > 0) { // by leeyu 080714
                    Num = String.valueOf(i + 1);
                    pstmt.setString(1, this.tmpVchTplCode);
                    pstmt.setString(2, this.tmpEntityCode);
                    pstmt.setString(3, Num);
                    pstmt.setString(4, this.ValueType);
                    pstmt.setString(5, this.assistantConent);
                    pstmt.setString(6, this.assistantFieldCode);
                    pstmt.setString(7, this.assistantDictCode);
                    pstmt.setString(8, this.desc);
                    pstmt.setInt(9, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(10, this.creatorCode);
                    pstmt.setString(11, this
                                    .creatorTime);
                    pstmt.setString(12,
                                    (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new YssException("���渨���������ó���\r\n" + ex.getMessage());
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
        return "";

    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.vchTplCode.length() != 0) { // wdy add 20070903 ���ӱ�������a
                sResult = sResult + " and a.FVchTplCode like '" +
                    filterType.vchTplCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.entityCode.length() != 0) {
                sResult = sResult + " and a.FEntityCode like '" +
                    filterType.entityCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.ValueType.length() != 0) {
                sResult = sResult + " and a.FValueType like '" +
                    filterType.ValueType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.assistantConent.length() != 0) {
                sResult = sResult + " and a.FAssistantConent like '" +
                    filterType.assistantConent.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.assistantFieldCode.length() != 0) {
                sResult = sResult + " and a.FAssistantField like '" +
                    filterType.assistantFieldCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.assistantDictCode.length() != 0) {
                sResult = sResult + " and a.FAssistantDict like '" +
                    filterType.assistantDictCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
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
                setAssistantAttr(rs);
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
            throw new YssException("��ȡ�����������ó�����", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * setAssistantAttr
     *
     * @param rs ResultSet
     */
    private void setAssistantAttr(ResultSet rs) throws SQLException {
        this.assistantConent = rs.getString("FAssistantConent");
        this.assistantFieldCode = rs.getString("FAssistantField");
        this.assistantFieldName = rs.getString("FAssistantFieldName");
        this.assistantDictCode = rs.getString("FAssistantDict");
        this.assistantDictName = rs.getString("FAssistantDictName");
        this.desc = rs.getString("FDesc");
        this.tmpOrderIndex = String.valueOf(rs.getInt("FOrderNum"));
        this.ValueType = rs.getString("FValueType");
        this.ValueTypeValue = rs.getString("FValueTypeValue");
    }

    public String getListViewData1() throws YssException {
        String sqlStr = " select y.* from (" +
            " select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
            " d.FDesc as FAssistantFieldName ,e.FVocName as FValueTypeValue" +
            " ,f.FDictName as FAssistantDictName " +
            " from " + pub.yssGetTableName("Tb_Vch_Assistant") +
            " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FAliasName,FDesc from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
            "  where FVchDsCode=" + dbl.sqlString(this.dataSource) +
            " )d on a.FAssistantField = d.FAliasName " +
            " left join Tb_Fun_Vocabulary e on a.FValueType=e.FVocCode and e.FVocTypeCode= " +
            dbl.sqlString(YssCons.YSS_VALUETYPE) +
            " left join(select distinct FDictCode,FDictName from " + pub.yssGetTableName("Tb_Vch_Dict") +
            " ) f on f.FDictCode=a.FAssistantDict " +
            buildFilterSql() +
            " ) y" +
            //  " left join Tb_Fun_Vocabulary g on y.FAssistantDict=g.FVocCode and g.FVocTypeCode= "+
            //    dbl.sqlString(YssCons.YSS_VALUETYPE)

            //" order by y.FVchTplCode,y.FEntityCode";//�˴��� ��� by leeyu BUG:000391
            " order by y.FOrderNum";
        return builderListViewData(sqlStr);
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        String strSql = "select y.* from (" +
            "select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
            " d.FDesc as FAssistantFieldName,e.FVocName as FValueTypeValue " +
            " ,f.FDictName as FAssistantDictName " +
            " from " + pub.yssGetTableName("Tb_Vch_Assistant") +
            " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FAliasName,FDesc from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
            "  where FVchDsCode=" + dbl.sqlString(this.dataSource) +
            " )d on a.FAssistantField = d.FAliasName " +
            " left join Tb_Fun_Vocabulary e on a.FValueType=e.FVocCode and e.FVocTypeCode= " +
            dbl.sqlString(YssCons.YSS_VALUETYPE) +
            " left join(select distinct FDictCode,FDictName from " + pub.yssGetTableName("Tb_Vch_Dict") +
            " ) f on f.FDictCode=a.FAssistantDict " +
            " ) y " +
            //   " left join Tb_Fun_Vocabulary g on y.FAssistantDict=g.FVocCode and g.FVocTypeCode= "+
            //   dbl.sqlString(YssCons.YSS_VALUETYPE)+
            " where 1=2 " +
            " order by y.FVchTplCode,y.FEntityCode";

        return builderListViewData(strSql);
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

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
            this.ValueType = reqAry[2];
            this.assistantConent = reqAry[3];
            this.assistantFieldCode = reqAry[4];
            this.assistantDictCode = reqAry[5];
            this.desc = reqAry[6];
            this.dataSource = reqAry[7];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchAssistantBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("���������������ó���");
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.ValueType).append("\t");
        buf.append(this.assistantConent).append("\t");
        buf.append(this.assistantFieldCode).append("\t");
        buf.append(this.assistantFieldName).append("\t");
        buf.append(this.assistantDictCode).append("\t");
        buf.append(this.assistantDictName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.tmpOrderIndex).append("\t");
        buf.append(this.ValueTypeValue).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
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
