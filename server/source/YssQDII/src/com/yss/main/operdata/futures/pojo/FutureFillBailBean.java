package com.yss.main.operdata.futures.pojo;

import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.util.*;

public class FutureFillBailBean
    extends BaseDataSettingBean {
    public FutureFillBailBean() {
    }

    private String sNum = "";
    private String sOldNum = "";
    private String sCashInd = ""; //现金方向
    private String sCashIndName = "";
    private String sCashPortCode = ""; //现金帐户组合
    private String sCashPortName = "";
    private String sBailPortCode = ""; //保证金组合
    private String sBailPortName = "";
    private String sCashAnalysisCode1 = ""; //现金分析代码
    private String sCashAnalysisName1 = "";
    private String sCashAnalysisCode2 = "";
    private String sCashAnalysisName2 = "";
    private String sCashAnalysisCode3 = "";
    private String sCashAnalysisName3 = "";
    private java.util.Date dTransDate = null; //业务日期  edit by jc
    private java.util.Date dTransferDate = null; //YssFun.toDate("9998-12-31"); //调拨日期
    private String sTransferTime = ""; //调拨时间
    private String sCashAcctCode = ""; //现金帐户
    private String sCashAcctName = "";
    private String sBailAcctCode = ""; //保证金帐户
    private String sBailAcctName = "";
    private String sBailAnalysisCode1 = ""; //分析代码
    private String sBailAnalysisName1 = "";
    private String sBailAnalysisCode2 = "";
    private String sBailAnalysisName2 = "";
    private String sBailAnalysisCode3 = "";
    private String sBailAnalysisName3 = "";
    private double dBaseCuryRate = 0; //基础汇率
    private double dPortCuryRate = 0; //组合汇率
    private double dMoney = 0; //原币金额
    private double dBaseCuryMoney = 0; //基础货币金额
    private double dPortCuryMoney = 0; //组合货币金额
    private String sDesc = ""; //备注
    private String sRecycled = "";
    private boolean bShow = false;
    private FutureFillBailBean filterType = null;

    public String getSTransferTime() {
        return sTransferTime;
    }

    public String getSNum() {
        return sNum;
    }

    public String getSCashPortName() {
        return sCashPortName;
    }

    public String getSCashPortCode() {
        return sCashPortCode;
    }

    public String getSCashAcctCode() {
        return sCashAcctCode;
    }

    public String getSBailPortName() {
        return sBailPortName;
    }

    public String getSBailPortCode() {
        return sBailPortCode;
    }

    public String getSBailAcctCode() {
        return sBailAcctCode;
    }

    public FutureFillBailBean getFilterType() {
        return filterType;
    }

    public Date getDTransferDate() {
        return dTransferDate;
    }

    public double getDPortCuryRate() {
        return dPortCuryRate;
    }

    public double getDPortCuryMoney() {
        return dPortCuryMoney;
    }

    public double getDMoney() {
        return dMoney;
    }

    public double getDBaseCuryRate() {
        return dBaseCuryRate;
    }

    public void setDBaseCuryMoney(double dBaseCuryMoney) {
        this.dBaseCuryMoney = dBaseCuryMoney;
    }

    public void setSTransferTime(String sTransferTime) {
        this.sTransferTime = sTransferTime;
    }

    public void setSNum(String sNum) {
        this.sNum = sNum;
    }

    public void setSCashPortName(String sCashPortName) {
        this.sCashPortName = sCashPortName;
    }

    public void setSCashPortCode(String sCashPortCode) {
        this.sCashPortCode = sCashPortCode;
    }

    public void setSCashAcctCode(String sCashAcctCode) {
        this.sCashAcctCode = sCashAcctCode;
    }

    public void setSBailPortName(String sBailPortName) {
        this.sBailPortName = sBailPortName;
    }

    public void setSBailPortCode(String sBailPortCode) {
        this.sBailPortCode = sBailPortCode;
    }

    public void setSBailAcctCode(String sBailAcctCode) {
        this.sBailAcctCode = sBailAcctCode;
    }

    public void setFilterType(FutureFillBailBean filterType) {
        this.filterType = filterType;
    }

    public void setDTransferDate(Date dTransferDate) {
        this.dTransferDate = dTransferDate;
    }

    public void setDPortCuryRate(double dPortCuryRate) {
        this.dPortCuryRate = dPortCuryRate;
    }

    public void setDPortCuryMoney(double dPortCuryMoney) {
        this.dPortCuryMoney = dPortCuryMoney;
    }

    public void setDMoney(double dMoney) {
        this.dMoney = dMoney;
    }

    public void setDBaseCuryRate(double dBaseCuryRate) {
        this.dBaseCuryRate = dBaseCuryRate;
    }

    public void setSRecycled(String sRecycled) {
        this.sRecycled = sRecycled;
    }

    public void setSCashInd(String sCashInd) {
        this.sCashInd = sCashInd;
    }

    public void setSCashIndName(String sCashIndName) {
        this.sCashIndName = sCashIndName;
    }

    public void setSCashAcctName(String sCashAcctName) {
        this.sCashAcctName = sCashAcctName;
    }

    public void setSBailAcctName(String sBailAcctName) {
        this.sBailAcctName = sBailAcctName;
    }

    public void setSDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    public void setSOldNum(String sOldNum) {
        this.sOldNum = sOldNum;
    }

    public void setSCashAnalysisName3(String sCashAnalysisName3) {
        this.sCashAnalysisName3 = sCashAnalysisName3;
    }

    public void setSCashAnalysisName2(String sCashAnalysisName2) {
        this.sCashAnalysisName2 = sCashAnalysisName2;
    }

    public void setSCashAnalysisName1(String sCashAnalysisName1) {
        this.sCashAnalysisName1 = sCashAnalysisName1;
    }

    public void setSCashAnalysisCode3(String sCashAnalysisCode3) {
        this.sCashAnalysisCode3 = sCashAnalysisCode3;
    }

    public void setSCashAnalysisCode2(String sCashAnalysisCode2) {
        this.sCashAnalysisCode2 = sCashAnalysisCode2;
    }

    public void setSCashAnalysisCode1(String sCashAnalysisCode1) {
        this.sCashAnalysisCode1 = sCashAnalysisCode1;
    }

    public void setSBailAnalysisName3(String sBailAnalysisName3) {
        this.sBailAnalysisName3 = sBailAnalysisName3;
    }

    public void setSBailAnalysisName2(String sBailAnalysisName2) {
        this.sBailAnalysisName2 = sBailAnalysisName2;
    }

    public void setSBailAnalysisName1(String sBailAnalysisName1) {
        this.sBailAnalysisName1 = sBailAnalysisName1;
    }

    public void setSBailAnalysisCode3(String sBailAnalysisCode3) {
        this.sBailAnalysisCode3 = sBailAnalysisCode3;
    }

    public void setSBailAnalysisCode2(String sBailAnalysisCode2) {
        this.sBailAnalysisCode2 = sBailAnalysisCode2;
    }

    public void setSBailAnalysisCode1(String sBailAnalysisCode1) {
        this.sBailAnalysisCode1 = sBailAnalysisCode1;
    }

    public void setBShow(boolean bShow) {
        this.bShow = bShow;
    }

    public void setDTransDate(Date dTransDate) {
        this.dTransDate = dTransDate;
    }

    public double getDBaseCuryMoney() {
        return dBaseCuryMoney;
    }

    public String getSRecycled() {
        return sRecycled;
    }

    public String getSCashInd() {
        return sCashInd;
    }

    public String getSCashIndName() {
        return sCashIndName;
    }

    public String getSCashAcctName() {
        return sCashAcctName;
    }

    public String getSBailAcctName() {
        return sBailAcctName;
    }

    public String getSDesc() {
        return sDesc;
    }

    public String getSOldNum() {
        return sOldNum;
    }

    public String getSCashAnalysisName3() {
        return sCashAnalysisName3;
    }

    public String getSCashAnalysisName2() {
        return sCashAnalysisName2;
    }

    public String getSCashAnalysisName1() {
        return sCashAnalysisName1;
    }

    public String getSCashAnalysisCode3() {
        return sCashAnalysisCode3;
    }

    public String getSCashAnalysisCode2() {
        return sCashAnalysisCode2;
    }

    public String getSCashAnalysisCode1() {
        return sCashAnalysisCode1;
    }

    public String getSBailAnalysisName3() {
        return sBailAnalysisName3;
    }

    public String getSBailAnalysisName2() {
        return sBailAnalysisName2;
    }

    public String getSBailAnalysisName1() {
        return sBailAnalysisName1;
    }

    public String getSBailAnalysisCode3() {
        return sBailAnalysisCode3;
    }

    public String getSBailAnalysisCode2() {
        return sBailAnalysisCode2;
    }

    public String getSBailAnalysisCode1() {
        return sBailAnalysisCode1;
    }

    public boolean isBShow() {
        return bShow;
    }

    public Date getDTransDate() {
        return dTransDate;
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
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            sCashInd = reqAry[0];
            sCashPortCode = reqAry[1];
            sBailPortCode = reqAry[2];
            sCashAnalysisCode1 = reqAry[3];
            sCashAnalysisCode2 = reqAry[4];
            sCashAnalysisCode3 = reqAry[5];
            if (YssFun.isDate(reqAry[6])) {
                dTransferDate = YssFun.toDate(reqAry[6]);
            }
            sTransferTime = reqAry[7];
            sCashAcctCode = reqAry[8];
            sBailAcctCode = reqAry[9];
            sBailAnalysisCode1 = reqAry[10];
            sBailAnalysisCode2 = reqAry[11];
            sBailAnalysisCode3 = reqAry[12];
            if (YssFun.isNumeric(reqAry[13])) {
                dBaseCuryRate = YssFun.toDouble(reqAry[13]);
            }
            if (YssFun.isNumeric(reqAry[14])) {
                dPortCuryRate = YssFun.toDouble(reqAry[14]);
            }
            if (YssFun.isNumeric(reqAry[15])) {
                dMoney = YssFun.toDouble(reqAry[15]);
            }
            if (YssFun.isNumeric(reqAry[16])) {
                dBaseCuryMoney = YssFun.toDouble(reqAry[16]);
            }
            if (YssFun.isNumeric(reqAry[17])) {
                dPortCuryMoney = YssFun.toDouble(reqAry[17]);
            }
            sDesc = reqAry[18];
            sNum = reqAry[19];
            sOldNum = reqAry[20];
            if (YssFun.isNumeric(reqAry[21])) {
                this.checkStateId = YssFun.toInt(reqAry[21]);
            }
            if (reqAry[22].equalsIgnoreCase("true")) {
                this.bShow = true;
            } else {
                this.bShow = false;
            }
            if (YssFun.isDate(reqAry[23])) { //edit by jc
                this.dTransDate = YssFun.toDate(reqAry[23]);
            }
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new FutureFillBailBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析期货保证金补交表信息出错");
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(sCashInd).append(YssCons.YSS_ITEMSPLITMARK1); ;
        buf.append(sCashIndName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sCashPortCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sCashPortName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sBailPortCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sBailPortName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sCashAnalysisCode1).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sCashAnalysisName1).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sCashAnalysisCode2).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sCashAnalysisName2).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sCashAnalysisCode3).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sCashAnalysisName3).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(dTransferDate).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sTransferTime).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sCashAcctCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sCashAcctName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sBailAcctCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sBailAcctName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sBailAnalysisCode1).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sBailAnalysisName1).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sBailAnalysisCode2).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sBailAnalysisName2).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sBailAnalysisCode3).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sBailAnalysisName3).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(dBaseCuryRate).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(dPortCuryRate).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(dMoney).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(dBaseCuryMoney).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(dPortCuryMoney).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sDesc).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(sNum).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(dTransDate).append(YssCons.YSS_ITEMSPLITMARK1); //edit by jc
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void setFurtureFillBailAttr(ResultSet rs) throws YssException {
        try {
            this.sNum = rs.getString("FNum");
            sCashInd = rs.getString("FCashInd");
            sCashIndName = rs.getString("FCashIndName");
            sCashPortCode = rs.getString("FCashPortCode");
            sCashPortName = rs.getString("FCashPortName");
            sBailPortCode = rs.getString("FBailPortCode");
            sBailPortName = rs.getString("FBailPortName");
            sCashAnalysisCode1 = rs.getString("FCashAnalysisCode1");
            sCashAnalysisCode2 = rs.getString("FCashAnalysisCode2");
            sCashAnalysisCode3 = rs.getString("FCashAnalysisCode3");
            sCashAnalysisName1 = rs.getString("FCashAnalysisName1");
            sCashAnalysisName2 = rs.getString("FCashAnalysisName2");
            sCashAnalysisName3 = rs.getString("FCashAnalysisName3");
            dTransferDate = rs.getDate("FTransferDate");
            sTransferTime = rs.getString("FTransferTime");
            sCashAcctCode = rs.getString("FCashAcctCode");
            sCashAcctName = rs.getString("FCashAcctName");
            sBailAcctCode = rs.getString("FBailAcctCode");
            sBailAcctName = rs.getString("FBailAcctName");
            sBailAnalysisCode1 = rs.getString("FBailAnalysisCode1");
            sBailAnalysisCode2 = rs.getString("FBailAnalysisCode2");
            sBailAnalysisCode3 = rs.getString("FBailAnalysisCode3");
            sBailAnalysisName1 = rs.getString("FBailAnalysisName1");
            sBailAnalysisName2 = rs.getString("FBailAnalysisName2");
            sBailAnalysisName3 = rs.getString("FBailAnalysisName3");
            dBaseCuryRate = rs.getDouble("FBaseCuryRate");
            dPortCuryRate = rs.getDouble("FPortCuryRate");
            dMoney = rs.getDouble("FMoney");
            dBaseCuryMoney = rs.getDouble("FBaseCuryMoney");
            dPortCuryMoney = rs.getDouble("FPortCuryMoney");
            sDesc = rs.getString("FDesc");
            dTransDate = rs.getDate("FTransDate"); //edit by jc
            super.setRecLog(rs);
        } catch (SQLException sex) {
            throw new YssException(sex.toString());
        }
    }
}
