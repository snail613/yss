package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class VchPortBookBean
    extends BaseDataSettingBean implements IDataSetting {
    private String portCode = "";
    private String portName = "";
    private String bookSetCode = "";
    private String bookSetName = "";

    public String getPortCode() {
        return this.portCode;
    }

    public String getPortName() {
        return this.portName;
    }

    public String getBookSetCode() {
        return this.bookSetCode;
    }

    public String getBookSetName() {
        return this.bookSetName;
    }

    public void setPortCode(String resqStr) {
        this.portCode = resqStr;
    }

    public void setPortName(String resqStr) {
        this.portName = resqStr;
    }

    public void setBookSetCode(String resqStr) {
        this.bookSetCode = resqStr;
    }

    public void setBookSetName(String resqStr) {
        this.bookSetName = resqStr;
    }

    public VchPortBookBean() {
    }

    public void setPortBookAttr(ResultSet rs) throws SQLException {
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        this.bookSetCode = rs.getString("FBookSetCode") + "";
        this.bookSetName = rs.getString("FBookSetName") + "";
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.portCode.trim()).append("\t");
        buf.append(this.portName.trim()).append("\t");
        buf.append(this.bookSetCode.trim()).append("\t");
        buf.append(this.bookSetName.trim()).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
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
            reqAry = sRowStr.split("\t");
            this.portCode = reqAry[0];
            this.portName = reqAry[1];
            this.bookSetCode = reqAry[2];
            this.bookSetName = reqAry[3];
            super.parseRecLog();
        } catch (Exception e) {
            throw new YssException("����������˳���");
        }
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
