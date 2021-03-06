package com.yss.main.etfoperation.pojo;

/**
 * add by songjie
 * 2009.10.10
 * V4.1_ETF:MS00002
 * QDV4.1赢时胜（上海）2009年9月28日01_A
 * ETF股票篮表对应的实体类
 */
public class ETFStockListBean {
    String portCode = "";//组合代码
    String securityCode = "";//证券代码
    String securityName = "";//证券名称
    double amount = 0;//证券数量
    String replaceMark = "";//替代标识
    double premiumScale = 0;//溢价比例
    double totalMoney = 0;//总金额
    String desc = "";//描述

    /**
     * 构造函数
     */
    public ETFStockListBean() {

    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getReplaceMark() {
        return replaceMark;
    }

    public double getPremiumScale() {
        return premiumScale;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setReplaceMark(String replaceMark) {
        this.replaceMark = replaceMark;
    }

    public void setPremiumScale(double premiumScale) {
        this.premiumScale = premiumScale;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getAmount() {
        return amount;
    }

}
