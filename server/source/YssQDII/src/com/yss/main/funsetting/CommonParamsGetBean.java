package com.yss.main.funsetting;

import java.io.*;
import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class CommonParamsGetBean
    extends BaseDataSettingBean implements IDataSetting, Serializable {

    private String sCPTypeCode = ""; //参数类型代码
    private String sCondType = ""; //条件类型代码
    private String sCondCode = ""; //条件代码
    private String sCPAttrCode = ""; //参数属性代码
    private String sParamValue = ""; //参数值

//获取参数值的通用方法
    public String getCommonParams(String sCPTypeCode, String sCondType, String sCondCode, String sCPAttrCode) throws YssException {

        String str = "";
        ResultSet rs = null;
        try {
            str = "select b.fparamvalue from (select * from tb_fun_commonparams " +
                " where FCPTypeCode in" + sCPTypeCode +
                " and Fcondtype in " + sCondType +
                " and Fcondcode in" + sCondCode +
                ") a join (select fcptypecode, FParamValue from tb_fun_commonparamssub " +
                " where fparamcode in" + sCPAttrCode +
                " ) b on b.fcptypecode = a.fcptypecode";

            rs = dbl.openResultSet(str);
            while (rs.next()) {
                this.sParamValue = rs.getString("FParamValue");
            }
        } catch (Exception e) {
            throw new YssException("获取参数值错误", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sParamValue;
    }

//通过配置得到参数值
    public String getValue1() throws YssException {
        String value = "";
        value = getCommonParams("('11')", "('Port')", "('CNJX', 'GCHINA')", "('testvoc')");
        return value;
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
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
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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
    public String saveMutliSetting(String sMutilRowStr) {
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
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
