package com.yss.pojo.dayfinish;

import java.util.*;

import com.yss.util.*;

public class AccInterest
    extends AbsBaseIncome {
    private String CashAccCode;
    private String CashAccName;
    private String PortCode;
    private String PortName;
    private String InvMgrCode;
    private String InvMgrName;
    private String CatCode;
    private String CatName;
    private String FormulaCode;
    private String FormulaName;
    private String RoundCode;
    private String RoundName;
    private String PeriodCode;
    private String PeriodName;
    private int interestOrigin;
    private int interestCycle;
    private int interesetWay;
    private double fixRate; //当利息来源为“固定利率”时，用此利率进行计算

    private java.util.Date currentDate; //计息日期

    private double Money; //库存金额+交易金额
    private double baseMoney; //基础货币库存金额+交易金额
    private double portMoney; //组合货币库存金额+交易金额
    private double interestMoney; //本次利息金额
    private double baseInterestMoney; //基础货币利息金额
    private double portInterestMoney; //组合货币利息金额
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率
    private String strCuryCode = ""; //币种代码
    private double arSumMoney; //累计应收利息
    private double paidSumMoney; //累计实收利息

    private java.util.Date startDate;
    private java.util.Date endDate;
    private String portCodes;
    public Date getEndDate() {
        return endDate;
    }

    public String getPeriodName() {
        return PeriodName;
    }

    public double getPaidSumMoney() {
        return paidSumMoney;
    }

    public int getInterestCycle() {
        return interestCycle;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public double getPortInterestMoney() {
        return portInterestMoney;
    }

    public double getFixRate() {
        return fixRate;
    }

    public String getPortCode() {
        return PortCode;
    }

    public double getPortMoney() {
        return portMoney;
    }

    public String getRoundCode() {
        return RoundCode;
    }

    public String getPortCodes() {
        return portCodes;
    }

    public double getBaseInterestMoney() {
        return baseInterestMoney;
    }

    public String getCashAccCode() {
        return CashAccCode;
    }

    public String getFormulaName() {
        return FormulaName;
    }

    public int getInteresetWay() {
        return interesetWay;
    }

    public String getPortName() {
        return PortName;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public double getMoney() {
        return Money;
    }

    public int getInterestOrigin() {
        return interestOrigin;
    }

    public String getCatName() {
        return CatName;
    }

    public String getInvMgrName() {
        return InvMgrName;
    }

    public String getInvMgrCode() {
        return InvMgrCode;
    }

    public String getFormulaCode() {
        return FormulaCode;
    }

    public double getArSumMoney() {
        return arSumMoney;
    }

    public String getPeriodCode() {
        return PeriodCode;
    }

    public double getInterestMoney() {
        return interestMoney;
    }

    public String getCatCode() {
        return CatCode;
    }

    public String getRoundName() {
        return RoundName;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getCashAccName() {
        return CashAccName;
    }

    public double getBaseMoney() {
        return baseMoney;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStrCuryCode(String strCuryCode) {
        this.strCuryCode = strCuryCode;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPeriodName(String PeriodName) {
        this.PeriodName = PeriodName;
    }

    public void setPaidSumMoney(double paidSumMoney) {
        this.paidSumMoney = paidSumMoney;
    }

    public void setInterestCycle(int interestCycle) {
        this.interestCycle = interestCycle;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setPortInterestMoney(double portInterestMoney) {
        this.portInterestMoney = portInterestMoney;
    }

    public void setFixRate(double fixRate) {
        this.fixRate = fixRate;
    }

    public void setPortCode(String PortCode) {
        this.PortCode = PortCode;
    }

    public void setPortMoney(double portMoney) {
        this.portMoney = portMoney;
    }

    public void setRoundCode(String RoundCode) {
        this.RoundCode = RoundCode;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public void setBaseInterestMoney(double baseInterestMoney) {
        this.baseInterestMoney = baseInterestMoney;
    }

    public void setCashAccCode(String CashAccCode) {
        this.CashAccCode = CashAccCode;
    }

    public void setFormulaName(String FormulaName) {
        this.FormulaName = FormulaName;
    }

    public void setInteresetWay(int interesetWay) {
        this.interesetWay = interesetWay;
    }

    public void setPortName(String PortName) {
        this.PortName = PortName;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public void setMoney(double Money) {
        this.Money = Money;
    }

    public void setInterestOrigin(int interestOrigin) {
        this.interestOrigin = interestOrigin;
    }

    public void setCatName(String CatName) {
        this.CatName = CatName;
    }

    public void setInvMgrName(String InvMgrName) {
        this.InvMgrName = InvMgrName;
    }

    public void setInvMgrCode(String InvMgrCode) {
        this.InvMgrCode = InvMgrCode;
    }

    public void setFormulaCode(String FormulaCode) {
        this.FormulaCode = FormulaCode;
    }

    public void setArSumMoney(double arSumMoney) {
        this.arSumMoney = arSumMoney;
    }

    public void setPeriodCode(String PeriodCode) {
        this.PeriodCode = PeriodCode;
    }

    public void setInterestMoney(double interestMoney) {
        this.interestMoney = interestMoney;
    }

    public void setCatCode(String CatCode) {
        this.CatCode = CatCode;
    }

    public void setRoundName(String RoundName) {
        this.RoundName = RoundName;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setCashAccName(String CashAccName) {
        this.CashAccName = CashAccName;
    }

    public void setBaseMoney(double baseMoney) {
        this.baseMoney = baseMoney;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStrCuryCode() {
        return strCuryCode;
    }

    public AccInterest() {
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        this.autoParseRowStr("CashAccCode\tCashAccName\tPortCode\tPortName\tInvMgrCode\tInvMgrName\tCatCode\t" +
                             "CatName\tFormulaCode\tFormulaName\tRoundCode\tRoundName\tPeriodCode\t" +
                             "PeriodName\tInterestCycle\tInterestOrigin\tFixRate\tCurrentDate\tMoney\t" +
                             "BaseMoney\tPortMoney\tInterestMoney\tBaseInterestMoney\t" +
                             "PortInterestMoney\tBaseCuryRate\tPortCuryRate\tStrCuryCode\tArSumMoney\t" +
                             "PaidSumMoney\tStartDate\tEndDate\tPortCodes\tInteresetWay",
                             sRowStr);
    }

    public String buildShowStr() throws YssException {
        return this.autoBuildRowStr("CashAccCode\tCashAccName\tCurrentDate\tPortName\tInvMgrName\tCatName\tFormulaName\tRoundName\tPeriodName\tInterestCycle\tInterestOrigin\tMoney;#,##0.####\tBaseMoney;#,##0.####\tPortMoney;#,##0.####\tArSumMoney;#,##0.##\tPaidSumMoney;#,##0.##");
    }

    public String buildAllStr() throws YssException {
        return this.autoBuildRowStr("CashAccCode\tCashAccName\tPortCode\tPortName\tInvMgrCode\tInvMgrName\tCatCode\tCatName\tFormulaCode\tFormulaName\tRoundCode\tRoundName\tPeriodCode\tPeriodName\tInterestCycle\tInterestOrigin\tFixRate\tCurrentDate\tMoney\tBaseMoney\tPortMoney\tInterestMoney\tBaseInterestMoney\tPortInterestMoney\tBaseCuryRate\tPortCuryRate\tStrCuryCode\tArSumMoney\tPaidSumMoney\tInteresetWay");
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
}
