package com.yss.main.operdeal.report.accbook.pojo;

import com.yss.main.dao.IKey;

public class SecBookSumRecPayBean
    implements IKey {
    private String keyCode; //代码
    private String keyName; //名称
    private String curyCode; //货币
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率
    private double beginMoney; //原币期初成本
    private double inMoney; //原币流入成本
    private double outMoney; //原币流出成本
    private double endMoney; //原币期末成本
    private double portBeginMoney; //本位币期初成本
    private double portInMoney; //本位币流入成本
    private double portOutMoney; //本位币流出成本
    private double exchangeInDec; //汇兑损益
    private double portEndMoney; //本位币期末成本
    private double portMarketValue; //本位币市值

    public SecBookSumRecPayBean() {
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public double getBeginMoney() {
        return beginMoney;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getEndMoney() {
        return endMoney;
    }

    public double getExchangeInDec() {
        return exchangeInDec;
    }

    public double getInMoney() {
        return inMoney;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public String getKeyName() {
        return keyName;
    }

    public double getOutMoney() {
        return outMoney;
    }

    public double getPortBeginMoney() {
        return portBeginMoney;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public double getPortEndMoney() {
        return portEndMoney;
    }

    public double getPortInMoney() {
        return portInMoney;
    }

    public double getPortMarketValue() {
        return portMarketValue;
    }

    public double getPortOutMoney() {
        return portOutMoney;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setBeginMoney(double beginMoney) {
        this.beginMoney = beginMoney;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setEndMoney(double endMoney) {
        this.endMoney = endMoney;
    }

    public void setExchangeInDec(double exchangeInDec) {
        this.exchangeInDec = exchangeInDec;
    }

    public void setInMoney(double inMoney) {
        this.inMoney = inMoney;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setOutMoney(double outMoney) {
        this.outMoney = outMoney;
    }

    public void setPortBeginMoney(double portBeginMoney) {
        this.portBeginMoney = portBeginMoney;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setPortEndMoney(double portEndMoney) {
        this.portEndMoney = portEndMoney;
    }

    public void setPortInMoney(double portInMoney) {
        this.portInMoney = portInMoney;
    }

    public void setPortMarketValue(double portMarketValue) {
        this.portMarketValue = portMarketValue;
    }

    public void setPortOutMoney(double portOutMoney) {
        this.portOutMoney = portOutMoney;
    }

    public Object getKey() {
        return this.keyCode;
    }

    public void setKey(Object obj) {

    }
}
