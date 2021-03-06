package com.yss.main.accbook;

import com.yss.dsub.BaseDataSettingBean;
import java.sql.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.pojo.sys.*;

public class AccTypeTreeBean
    extends BaseDataSettingBean implements IDataSetting {

    private YssTreeNode tNode;
    private String type;
    private String link;
    public AccTypeTreeBean() {
        tNode = new YssTreeNode();
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
        buf.append(tNode.getCode()).append("\t");
        buf.append(tNode.getName()).append("\t");
        buf.append(tNode.getParentCode()).append("\t");
        buf.append(tNode.getOrderCode()).append("\t");
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
    public String getTreeViewData2() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            IAccBookOper abo = (IAccBookOper) pub.getOperDealCtx().getBean(type);
            abo.setYssPub(pub);
            abo.setBookClassTable(link, YssOperCons.Yss_TZXSLX_CLERARED);
            strSql = "select * from " +
                pub.yssGetTableName("tb_Temp_BookCls_" + pub.getUserCode()) +
                " order by FOrderCode, FCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                tNode.setCode(rs.getString("FCode") + "");
                tNode.setName(rs.getString("FName") + "");
                tNode.setParentCode(rs.getString("FParentCode") + "");
                tNode.setOrderCode("1");
                buf.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (buf.length() > 1) {
                buf.delete(buf.length() - 1, buf.length());
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("获取台帐链接出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
        String[] sRowAry = sRowStr.split("\t");
        tNode.setCode(sRowAry[0]);
        tNode.setName(sRowAry[1]);
        tNode.setParentCode(sRowAry[2]);
        tNode.setOrderCode(sRowAry[3]);
        this.type = sRowAry[4];
        this.link = sRowAry[5];
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
