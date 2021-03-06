package com.yss.main.operdeal.report.accbook.pojo;

import java.util.*;

public class SecAccBookDetailBean {
    public SecAccBookDetailBean() {
    }

    private Date tradeDate; //交易日期
    private String securityCode; //证券代码
    private String SecurityName; //证券名称
    private String curyCode; //货币
    private String tradeType; //交易类型
    private double bargainPrice; //成交价格
    private double baseRate; //基础汇率
    private double portRate; //组合汇率
    private double settleMoney; //结算金额
    private double inAmount; //流入数量
    private double outAmount; //流出数量
    private double endAmount; //结存数量
    private double inCost; //流入成本
    private double outCost; //流出成本
    private double endCost; //结存成本
    private double portInCost; //本位币流入成本
    private double portOutCost; //本位币流出成本
    private double endportCost; //本位币结存成本
    private double settleBaseRate; //结算基础汇率
    private double settlePortRate; //结算组合汇率
    private double factSettleMoney; //实际结算金额
    private String tradeNum; //交易编号
    private int order; //排序字段
    private String index; //+ 、-

    public double getBargainPrice() {
        return bargainPrice;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getEndAmount() {
        return endAmount;
    }

    public double getEndCost() {
        return endCost;
    }

    public double getEndportCost() {
        return endportCost;
    }

    public double getFactSettleMoney() {
        return factSettleMoney;
    }

    public double getInAmount() {
        return inAmount;
    }

    public double getInCost() {
        return inCost;
    }

    public double getOutAmount() {
        return outAmount;
    }

    public double getOutCost() {
        return outCost;
    }

    public double getPortInCost() {
        return portInCost;
    }

    public double getPortRate() {
        return portRate;
    }

    public double getPortOutCost() {
        return portOutCost;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getSecurityName() {
        return SecurityName;
    }

    public double getSettleBaseRate() {
        return settleBaseRate;
    }

    public double getSettleMoney() {
        return settleMoney;
    }

    public double getSettlePortRate() {
        return settlePortRate;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setBargainPrice(double bargainPrice) {
        this.bargainPrice = bargainPrice;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setEndAmount(double endAmount) {
        this.endAmount = endAmount;
    }

    public void setEndCost(double endCost) {
        this.endCost = endCost;
    }

    public void setEndportCost(double endportCost) {
        this.endportCost = endportCost;
    }

    public void setFactSettleMoney(double factSettleMoney) {
        this.factSettleMoney = factSettleMoney;
    }

    public void setInAmount(double inAmount) {
        this.inAmount = inAmount;
    }

    public void setInCost(double inCost) {
        this.inCost = inCost;
    }

    public void setOutAmount(double outAmount) {
        this.outAmount = outAmount;
    }

    public void setOutCost(double outCost) {
        this.outCost = outCost;
    }

    public void setPortInCost(double portInCost) {
        this.portInCost = portInCost;
    }

    public void setPortOutCost(double portOutCost) {
        this.portOutCost = portOutCost;
    }

    public void setPortRate(double portRate) {
        this.portRate = portRate;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setSettleBaseRate(double settleBaseRate) {
        this.settleBaseRate = settleBaseRate;
    }

    public void setSecurityName(String SecurityName) {
        this.SecurityName = SecurityName;
    }

    public void setSettleMoney(double settleMoney) {
        this.settleMoney = settleMoney;
    }

    public void setSettlePortRate(double settlePortRate) {
        this.settlePortRate = settlePortRate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
