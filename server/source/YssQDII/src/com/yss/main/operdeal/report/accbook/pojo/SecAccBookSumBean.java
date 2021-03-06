package com.yss.main.operdeal.report.accbook.pojo;

import com.yss.main.dao.IKey;

public class SecAccBookSumBean
    implements IKey {

    private String keyCode; //代码
    private String keyName; //名称
    private String curyCode; //货币
    private double marketPrice; //行情价格
    private double baseRate; //基础汇率
    private double portRate; //组合汇率
    private double beginAmount; //期初数量
    private double inAmount; //流入数量
    private double outAmount; //流出数量
    private double endAmount; //期末数量
    private double beginCost; //原币期初成本
    private double inCost; //原币流入成本
    private double outCost; //原币流出成本
    private double endCost; //原币期末成本
    private double marketValue; //原币市值
    private double portBeginCost; //本位币期初成本
    private double portInCost; //本位币流入成本
    private double portOutCost; //本位币流出成本
    private double exchangeInDec; //汇兑损益
    private double portEndCost; //本位币期末成本
    private double portMarketValue; //本位币市值

    private double bal; //原币估值增值
    private double portBal; //本位币股指增值

    public SecAccBookSumBean() {
    }

    public double getBaseRate() {
        return baseRate;
    }

    public double getBeginAmount() {
        return beginAmount;
    }

    public double getBeginCost() {
        return beginCost;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getEndAmount() {
        return endAmount;
    }

    public double getExchangeInDec() {
        return exchangeInDec;
    }

    public double getInAmount() {
        return inAmount;
    }

    public double getInCost() {
        return inCost;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public String getKeyName() {
        return keyName;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public double getOutAmount() {
        return outAmount;
    }

    public double getOutCost() {
        return outCost;
    }

    public double getPortBeginCost() {
        return portBeginCost;
    }

    public double getPortEndCost() {
        return portEndCost;
    }

    public double getPortInCost() {
        return portInCost;
    }

    public double getPortMarketValue() {
        return portMarketValue;
    }

    public double getPortOutCost() {
        return portOutCost;
    }

    public double getPortRate() {
        return portRate;
    }

    public double getBal() {
        return bal;
    }

    public double getPortBal() {
        return portBal;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    public void setBeginAmount(double beginAmount) {
        this.beginAmount = beginAmount;
    }

    public void setBeginCost(double beginCost) {
        this.beginCost = beginCost;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setEndAmount(double endAmount) {
        this.endAmount = endAmount;
    }

    public void setExchangeInDec(double exchangeInDec) {
        this.exchangeInDec = exchangeInDec;
    }

    public void setInAmount(double inAmount) {
        this.inAmount = inAmount;
    }

    public void setInCost(double inCost) {
        this.inCost = inCost;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public void setOutAmount(double outAmount) {
        this.outAmount = outAmount;
    }

    public void setOutCost(double outCost) {
        this.outCost = outCost;
    }

    public void setPortBeginCost(double portBeginCost) {
        this.portBeginCost = portBeginCost;
    }

    public void setPortEndCost(double portEndCost) {
        this.portEndCost = portEndCost;
    }

    public void setPortInCost(double portInCost) {
        this.portInCost = portInCost;
    }

    public void setPortMarketValue(double portMarketValue) {
        this.portMarketValue = portMarketValue;
    }

    public void setPortOutCost(double portOutCost) {
        this.portOutCost = portOutCost;
    }

    public void setPortRate(double portRate) {
        this.portRate = portRate;
    }

    public double getEndCost() {
        return endCost;
    }

    public void setEndCost(double endCost) {
        this.endCost = endCost;
    }

    public void setBal(double bal) {
        this.bal = bal;
    }

    public void setPortBal(double portBal) {
        this.portBal = portBal;
    }

    public Object getKey() {
        return this.keyCode;
    }

    public void setKey(Object obj) {

    }
}
