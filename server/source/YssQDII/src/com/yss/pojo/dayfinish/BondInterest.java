package com.yss.pojo.dayfinish;

import java.util.*;

import com.yss.util.*;

public class BondInterest
    extends AbsBaseIncome {
    private String securityCode;
    private String securityName;
    private String portCode;
    private String portName;
    private String invmgrCode;
    private String invmgrName;
    private String brokerCode;
    private String brokerName;
    private String subCatCode = ""; //品种子类型
    private String subCatName = ""; //品种子类型名称
    private java.util.Date curCpnDate; //本次起息日
    private java.util.Date nextCpnDate; //下次起息日
    private java.util.Date currentDate; //计息日期
    private double faceRate; //票面利率
    private double accPer100; //百元利息
    private double factor; //报价因子
    private int calcInsWay; //计算方法
    private String calcInsWayName; //计算方法名称
    private int InsFrequency; //付息频率
    private double amount; //证券数量
    private double interestMoney; //本次利息金额
    private double baseInterestMoney; //基础货币金额
    private double portInterestMoney; //组合货币金额
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率
    private String strCuryCode = ""; //币种代码
    private double arSumMoney; //累计应收利息
    private double paidSumMoney; //累计实收利息

    private java.util.Date startDate; //计息区间起始日
    private java.util.Date endDate; //计息区间截至日
    private String portCodes; //要计算利息的所有组合的代码，以,分隔

    public double getAccPer100() {
        return accPer100;
    }

    public String getPortCode() {
        return portCode;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getCalcInsWayName() {
        return calcInsWayName;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public double getPaidSumMoney() {
        return paidSumMoney;
    }

    public Date getCurCpnDate() {
        return curCpnDate;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public double getPortInterestMoney() {
        return portInterestMoney;
    }

    public double getAmount() {
        return amount;
    }

    public double getFactor() {
        return factor;
    }

    public int getCalcInsWay() {
        return calcInsWay;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getPortCodes() {
        return portCodes;
    }

    public double getBaseInterestMoney() {
        return baseInterestMoney;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getInvmgrCode() {
        return invmgrCode;
    }

    public String getPortName() {
        return portName;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public Date getNextCpnDate() {
        return nextCpnDate;
    }

    public String getInvmgrName() {
        return invmgrName;
    }

    public String getSubCatCode() {
        return subCatCode;
    }

    public String getSecurityName() {
        return securityName;
    }

    public double getArSumMoney() {
        return arSumMoney;
    }

    public double getFaceRate() {
        return faceRate;
    }

    public double getInterestMoney() {
        return interestMoney;
    }

    public int getInsFrequency() {
        return InsFrequency;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStrCuryCode(String strCuryCode) {
        this.strCuryCode = strCuryCode;
    }

    public void setAccPer100(double accPer100) {
        this.accPer100 = accPer100;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setCalcInsWayName(String calcInsWayName) {
        this.calcInsWayName = calcInsWayName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setPaidSumMoney(double paidSumMoney) {
        this.paidSumMoney = paidSumMoney;
    }

    public void setCurCpnDate(Date curCpnDate) {
        this.curCpnDate = curCpnDate;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setPortInterestMoney(double portInterestMoney) {
        this.portInterestMoney = portInterestMoney;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public void setCalcInsWay(int calcInsWay) {
        this.calcInsWay = calcInsWay;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public void setBaseInterestMoney(double baseInterestMoney) {
        this.baseInterestMoney = baseInterestMoney;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setInvmgrCode(String invmgrCode) {
        this.invmgrCode = invmgrCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public void setNextCpnDate(Date nextCpnDate) {
        this.nextCpnDate = nextCpnDate;
    }

    public void setInvmgrName(String invmgrName) {
        this.invmgrName = invmgrName;
    }

    public void setSubCatCode(String subCatCode) {
        this.subCatCode = subCatCode;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setArSumMoney(double arSumMoney) {
        this.arSumMoney = arSumMoney;
    }

    public void setFaceRate(double faceRate) {
        this.faceRate = faceRate;
    }

    public void setInterestMoney(double interestMoney) {
        this.interestMoney = interestMoney;
    }

    public void setInsFrequency(int InsFrequency) {
        this.InsFrequency = InsFrequency;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStrCuryCode() {
        return strCuryCode;
    }

    public BondInterest() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
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
    public void parseRowStr(String sRowStr) throws YssException {
        this.autoParseRowStr("SecurityCode\tSecurityName\tPortCode\tPortName\tInvmgrCode\tInvmgrName\tBrokerCode\tBrokerName\tSubCatCode\tSubCatName\tCurCpnDate\tNextCpnDate\tCurrentDate\tFaceRate\tAccPer100\tCalcInsWay\tInsFrequency\tAmount\tFactor\tInterestMoney\tBaseCuryRate\tPortCuryRate\tBaseInterestMoney\tPortInterestMoney\tStrCuryCode\tArSumMoney\tPaidSumMoney\tStartDate\tEndDate\tPortCodes",
                             sRowStr);
    }

    public String buildShowStr() throws YssException {
        return this.autoBuildRowStr(
            "SecurityCode\tSecurityName\tCurrentDate\tPortName\tInvmgrName\tBrokerName\tSubCatName\tCurCpnDate\tNextCpnDate\tAccPer100;#,##0.############\tFaceRate;#,##0.########\tInsFrequency;#,##0.####\tAmount;#,##0.####\tFactor;#,##0.######\tCalcInsWayName\tArSumMoney;#,##0.##\tPaidSumMoney;#,##0.##");
    }

    public String buildAllStr() throws YssException {
        return this.autoBuildRowStr("SecurityCode\tSecurityName\tPortCode\tPortName\tInvmgrCode\tInvmgrName\tBrokerCode\tBrokerName\tSubCatCode\tSubCatName\tCurCpnDate\tNextCpnDate\tCurrentDate\tFaceRate\tAccPer100\tCalcInsWay\tInsFrequency\tAmount\tFactor\tInterestMoney\tBaseCuryRate\tPortCuryRate\tBaseInterestMoney\tPortInterestMoney\tStrCuryCode\tArSumMoney\tPaidSumMoney");
    }

}
