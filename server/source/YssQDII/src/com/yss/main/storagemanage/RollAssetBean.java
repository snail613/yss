package com.yss.main.storagemanage;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class RollAssetBean
    extends BaseDataSettingBean implements IDataSetting {
    private String yearMonth = ""; //存入年月
    private String storageDate; //存入日期
    private String portCode = ""; //组合代码
    private double rollBal = 0.0; //滚动资产
    private String curyCode = ""; //币种代码
    private double paidCapital = 0.0; //实收资产
    private double portCuryRate = 0; //组合汇率
    private double baseCuryRate = 0; //基础汇率
    private String oldYearMonth = ""; //
    private String oldStorageDate = ""; //
    private String oldPortCode = ""; //
    private RollAssetBean filterType = null; //

    public String getPortCode() {
        return portCode;
    }

    public double getPaidCapital() {
        return paidCapital;
    }

    public double getRollBal() {
        return rollBal;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public RollAssetBean getFilterType() {
        return filterType;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getOldYearMonth() {
        return oldYearMonth;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getStorageDate() {
        return storageDate;
    }

    public void setOldStorageDate(String oldStorageDate) {
        this.oldStorageDate = oldStorageDate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPaidCapital(double paidCapital) {
        this.paidCapital = paidCapital;
    }

    public void setRollBal(double rollBal) {
        this.rollBal = rollBal;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setFilterType(RollAssetBean filterType) {
        this.filterType = filterType;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setOldYearMonth(String oldYearMonth) {
        this.oldYearMonth = oldYearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setStorageDate(String storageDate) {
        this.storageDate = storageDate;
    }

    public String getOldStorageDate() {
        return oldStorageDate;
    }

    public RollAssetBean() {
        ///滚动资产设置
    }

    /*public void setAttr(String yearmonth,String portcode,String storagedate,double rollbar){
       this.yearMonth = yearmonth;
       this.portCode = portcode;
       this.storageDate = storagedate;
       this.rollBal = rollbar;
        }*/
    public String getListViewData1() {
        return "";
    }

    public String getListViewData2() {
        return "";
    }

    public String getListViewData3() {
        return "";
    }

    public String getListViewData4() {
        return "";
    }

    public String getTreeViewData1() {
        return "";
    }

    public String getTreeViewData2() {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String buildRowStr() {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
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
            if (reqAry[0].length() != 0) {
                this.storageDate = reqAry[0];
            }
            if (reqAry[1].length() != 0) {
                this.portCode = reqAry[1];
            }
            if (reqAry[2].length() != 0) {
                this.yearMonth = reqAry[2];
            }
            if (reqAry[3].length() != 0) {
                this.curyCode = reqAry[3];
            }
            if (YssFun.isNumeric(reqAry[4]) && reqAry[4].length() > 0) {
                this.paidCapital = Double.parseDouble(reqAry[4]);
            } else {
                this.paidCapital = 0;
            }
            if (YssFun.isNumeric(reqAry[5]) && reqAry[5].length() > 0) {
                this.rollBal = Double.parseDouble(reqAry[5]);
            } else {
                this.rollBal = 0;
            }
            if (YssFun.isNumeric(reqAry[6]) && reqAry[6].length() > 0) {
                this.portCuryRate = Double.parseDouble(reqAry[6]);
            }
            if (YssFun.isNumeric(reqAry[7]) && reqAry[7].length() > 0) {
                this.baseCuryRate = Double.parseDouble(reqAry[7]);
            }
            if (reqAry[8].length() != 0) {
                this.oldStorageDate = reqAry[8];
            }
            if (reqAry[9].length() != 0) {
                this.oldPortCode = reqAry[9];
            }
            if (reqAry[10].length() != 0) {
                this.oldYearMonth = reqAry[10];
            }
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {

                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("null")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析滚动资产信息出错", e);
        }

    }

    public String getOperValue(String sType) {
        return "";
    }

    public String getBeforeEditData() {
        return "";
    }

    public void checkInput(byte btOper) {

    }

    public String addSetting() {
        return "";
    }

    public String editSetting() {
        return "";
    }

    public void delSetting() {

    }

    public void checkSetting() {

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = dbl.loadConnection();
        int iCount = 0;
        String reSql = "";
        ResultSet rs = null;
        boolean bTrans = false;
        try {
            this.parseRowStr(sMutilRowStr);
            conn.setAutoCommit(false);
            bTrans = true;
            reSql = " delete from " + pub.yssGetTableName("Tb_Stock_RollAsset") +
                " where FStorageDate = " + dbl.sqlDate(this.oldStorageDate) + " and " +
                " FYearMonth = " + dbl.sqlString(this.oldYearMonth) + " and " +
                " FPortCode = " + dbl.sqlString(this.oldPortCode);
            dbl.executeSql(reSql);
            reSql = " insert into " + pub.yssGetTableName("Tb_Stock_RollAsset") +
                " (FStorageDate,FPortCode,FYearMonth,FCuryCode,FPaidCapital,FRollBal,FPortCuryRate," +
                " FBaseCuryRate,FCheckState,FCreator,FCreateTime) values(" +
                dbl.sqlDate(this.storageDate) + ", " +
                dbl.sqlString(this.portCode) + " , " +
                dbl.sqlString(this.yearMonth) + " , " +
                dbl.sqlString(this.curyCode) + ", " + //币种代码
                this.paidCapital + ", " +
                this.rollBal + ", " +
                1 + ", " +
                1 + ", " +
                1 + ", " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ", " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorTime)) + ")";
            dbl.executeSql(reSql);
            bTrans = false;
            conn.commit();

        } catch (Exception e) {
            throw new YssException("滚动资产更正错误", e);
        }
        return "";
    }

    public IDataSetting getSetting() {
        return null;
    }

    public String getAllSetting() {
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
