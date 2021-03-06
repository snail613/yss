package com.yss.main.operdeal.businesswork.pojo;

public class TradeSellRelaPojo {

    public TradeSellRelaPojo() {
    }

    public double getAppreciation() {

        return appreciation;
    }

    public double getBaseAppreciation() {

        return baseAppreciation;
    }

    public double getMAppreciation() {

        return mAppreciation;
    }

    public double getMBaseAppreciation() {

        return mBaseAppreciation;
    }

    public double getMPortAppreciation() {

        return mPortAppreciation;
    }

    public double getPortAppreciation() {

        return portAppreciation;
    }

    public double getRevenue() {

        return revenue;
    }

    public double getVAppreciation() {

        return vAppreciation;
    }

    public double getVBaseAppreciation() {

        return vBaseAppreciation;
    }

    public double getVPortAppreciation() {

        return vPortAppreciation;
    }

    public String getNum() {
        return num;
    }

    public double getStorageAmount() {
        return storageAmount;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getSecuritycode() {
        return securitycode;
    }

    public String getAnalysisCode1() {
        return analysisCode1;
    }

    public String getAnalysisCode2() {
        return analysisCode2;
    }

    public String getAnalysisCode3() {
        return analysisCode3;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getBal() {
        return bal;
    }

    public double getBaseCuryBal() {
        return baseCuryBal;
    }

    public double getMBal() {
        return mBal;
    }

    public double getMBaseCuryBal() {
        return mBaseCuryBal;
    }

    public double getMPortCuryBal() {
        return mPortCuryBal;
    }

    public double getPortCuryBal() {
        return portCuryBal;
    }

    public double getVBal() {
        return vBal;
    }

    public double getVBaseCuryBal() {
        return vBaseCuryBal;
    }

    public double getVPortCuryBal() {
        return vPortCuryBal;
    }

    public String getTsftypecode() {
        return tsftypecode;
    }

    public String getSubtsftypecode() {
        return subtsftypecode;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public void setAppreciation(double appreciation) {

        this.appreciation = appreciation;
    }

    public void setBaseAppreciation(double baseAppreciation) {

        this.baseAppreciation = baseAppreciation;
    }

    public void setMAppreciation(double mAppreciation) {

        this.mAppreciation = mAppreciation;
    }

    public void setMBaseAppreciation(double mBaseAppreciation) {

        this.mBaseAppreciation = mBaseAppreciation;
    }

    public void setMPortAppreciation(double mPortAppreciation) {

        this.mPortAppreciation = mPortAppreciation;
    }

    public void setPortAppreciation(double portAppreciation) {

        this.portAppreciation = portAppreciation;
    }

    public void setRevenue(double revenue) {

        this.revenue = revenue;
    }

    public void setVAppreciation(double vAppreciation) {

        this.vAppreciation = vAppreciation;
    }

    public void setVBaseAppreciation(double vBaseAppreciation) {

        this.vBaseAppreciation = vBaseAppreciation;
    }

    public void setVPortAppreciation(double vPortAppreciation) {

        this.vPortAppreciation = vPortAppreciation;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setStorageAmount(double storageAmount) {
        this.storageAmount = storageAmount;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setSecuritycode(String securitycode) {
        this.securitycode = securitycode;
    }

    public void setAnalysisCode1(String analysisCode1) {
        this.analysisCode1 = analysisCode1;
    }

    public void setAnalysisCode2(String analysisCode2) {
        this.analysisCode2 = analysisCode2;
    }

    public void setAnalysisCode3(String analysisCode3) {
        this.analysisCode3 = analysisCode3;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setVPortCuryBal(double vPortCuryBal) {
        this.vPortCuryBal = vPortCuryBal;
    }

    public void setVBaseCuryBal(double vBaseCuryBal) {
        this.vBaseCuryBal = vBaseCuryBal;
    }

    public void setVBal(double vBal) {
        this.vBal = vBal;
    }

    public void setPortCuryBal(double portCuryBal) {
        this.portCuryBal = portCuryBal;
    }

    public void setMPortCuryBal(double mPortCuryBal) {
        this.mPortCuryBal = mPortCuryBal;
    }

    public void setMBaseCuryBal(double mBaseCuryBal) {
        this.mBaseCuryBal = mBaseCuryBal;
    }

    public void setMBal(double mBal) {
        this.mBal = mBal;
    }

    public void setBaseCuryBal(double baseCuryBal) {
        this.baseCuryBal = baseCuryBal;
    }

    public void setBal(double bal) {
        this.bal = bal;
    }

    public void setTsftypecode(String tsftypecode) {
        this.tsftypecode = tsftypecode;
    }

    public void setSubtsftypecode(String subtsftypecode) {
        this.subtsftypecode = subtsftypecode;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    private String securitycode; //证券代码
    private String portCode; //组合代码
    private double storageAmount; //库存数量
    private String analysisCode1;
    private String analysisCode2;
    private String analysisCode3;
    private String curyCode;
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率

    private String num; //交易编号
    private String tsftypecode; //业务类型
    private String subtsftypecode; //业务子类型
    private double appreciation; //原币核算估值增值
    private double mAppreciation; //原币管理估值增值
    private double vAppreciation; //原币估值估值增值
    private double baseAppreciation; //基础货币核算估值增值
    private double mBaseAppreciation; //基础货币管理估值增值
    private double vBaseAppreciation; //基础货币估值估值增值
    private double portAppreciation; //组合货币核算估值增值
    private double mPortAppreciation; //组合货币管理估值增值
    private double vPortAppreciation; //组合货币估值估值增值
    private double revenue; //差价收入

    private double bal; //原币余额
    private double mBal; //原币管理余额
    private double vBal; //原币估值余额
    private double baseCuryBal; //基础货币余额
    private double mBaseCuryBal; //基础货币管理余额
    private double vBaseCuryBal; //基础货币估值余额
    private double portCuryBal; //组合货币余额
    private double mPortCuryBal; //组合货币管理余额
    private double vPortCuryBal; //组合货币估值余额

}
