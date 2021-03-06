package com.yss.pojo.cache;

import com.yss.main.dao.*;

public class YssKeyDouble
    implements IYssConvert {

    private String strPortCode = "";
    private String strInvMgrCode = "";
    private String strCuryCode = "";
    private String securityCode = "";
    private String cashAccCode = "";
    private String catCode = "";
    private double dAvgCostValue;
    private double balance;
    public String getStrPortCode() {
        return strPortCode;
    }

    public String getStrInvMgrCode() {
        return strInvMgrCode;
    }

    public void setStrCuryCode(String strCuryCode) {
        this.strCuryCode = strCuryCode;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setStrInvMgrCode(String strInvMgrCode) {
        this.strInvMgrCode = strInvMgrCode;
    }

    public void setDAvgCostValue(double dAvgCostValue) {
        this.dAvgCostValue = dAvgCostValue;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public String getStrCuryCode() {
        return strCuryCode;
    }

    public double getDAvgCostValue() {
        return dAvgCostValue;
    }

    public double getBalance() {
        return balance;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public String getCatCode() {
        return catCode;
    }

    public YssKeyDouble() {
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
        String[] sRowAry = sRowStr.split("\f");
        this.securityCode = sRowAry[0];
        this.strPortCode = sRowAry[1];
        this.strInvMgrCode = sRowAry[2];
        this.strCuryCode = sRowAry[3];
    }
}
