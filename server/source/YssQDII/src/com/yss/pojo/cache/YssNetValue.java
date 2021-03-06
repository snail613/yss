package com.yss.pojo.cache;

import com.yss.main.dao.*;

public class YssNetValue
    implements IYssConvert {
    private String invMgrCode = "";
    private double baseNetValue;
    private double portNetValue;
    private double unitBaseNetValue; //基础货币单位净值
    private double unitPortNetValue; //组合货币单位净值
    private double incBaseMV; //基础货币估值增值
    private double incPortMV; //组合货币估值增值
    private double exBaseFX; //基础货币汇兑损益
    private double exPortFX; //组合货币汇兑损益
    private double capital; //实收资本数量
    private double capitalCost; //实收资本原币成本
    private double capitalBaseCuryCost; //实收资本基础货币成本
    private double capitalPortCuryCost; //实收资本组合货币成本
    private double baseUseCash; //基础货币现金头寸
    private double portUseCash; //组合货币现金头寸
    private double basePl; //基础货币损益平准金（已实现）
    private double portUnPl; //组合货币损益平准金（未实现）
    private double baseUnPl; //基础货币损益平准金（未实现）
    private double portPl; //组合货币损益平准金（已实现）
    public double getPortNetValue() {
        return portNetValue;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public void setBaseNetValue(double baseNetValue) {
        this.baseNetValue = baseNetValue;
    }

    public void setPortNetValue(double portNetValue) {
        this.portNetValue = portNetValue;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setExPortFX(double exPortFX) {
        this.exPortFX = exPortFX;
    }

    public void setIncPortMV(double incPortMV) {
        this.incPortMV = incPortMV;
    }

    public void setPortUnPl(double portUnPl) {
        this.portUnPl = portUnPl;
    }

    public void setBasePl(double basePl) {
        this.basePl = basePl;
    }

    public void setBaseUseCash(double baseUseCash) {
        this.baseUseCash = baseUseCash;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public void setCapitalCost(double capitalCost) {
        this.capitalCost = capitalCost;
    }

    public void setCapitalBaseCuryCost(double capitalBaseCuryCost) {
        this.capitalBaseCuryCost = capitalBaseCuryCost;
    }

    public void setCapitalPortCuryCost(double capitalPortCuryCost) {
        this.capitalPortCuryCost = capitalPortCuryCost;
    }

    public void setUnitBaseNetValue(double unitBaseNetValue) {
        this.unitBaseNetValue = unitBaseNetValue;
    }

    public void setPortUseCash(double portUseCash) {
        this.portUseCash = portUseCash;
    }

    public void setExBaseFX(double exBaseFX) {
        this.exBaseFX = exBaseFX;
    }

    public void setIncBaseMV(double incBaseMV) {
        this.incBaseMV = incBaseMV;
    }

    public void setUnitPortNetValue(double unitPortNetValue) {
        this.unitPortNetValue = unitPortNetValue;
    }

    public void setPortPl(double portPl) {
        this.portPl = portPl;
    }

    public void setBaseUnPl(double baseUnPl) {
        this.baseUnPl = baseUnPl;
    }

    public double getBaseNetValue() {
        return baseNetValue;
    }

    public double getExPortFX() {
        return exPortFX;
    }

    public double getIncPortMV() {
        return incPortMV;
    }

    public double getPortUnPl() {
        return portUnPl;
    }

    public double getBasePl() {
        return basePl;
    }

    public double getBaseUseCash() {
        return baseUseCash;
    }

    public double getCapital() {
        return capital;
    }

    public double getCapitalCost() {
        return capitalCost;
    }

    public double getCapitalBaseCuryCost() {
        return capitalBaseCuryCost;
    }

    public double getCapitalPortCuryCost() {
        return capitalPortCuryCost;
    }

    public double getUnitBaseNetValue() {
        return unitBaseNetValue;
    }

    public double getPortUseCash() {
        return portUseCash;
    }

    public double getExBaseFX() {
        return exBaseFX;
    }

    public double getIncBaseMV() {
        return incBaseMV;
    }

    public double getUnitPortNetValue() {
        return unitPortNetValue;
    }

    public double getBaseUnPl() {
        return baseUnPl;
    }

    public double getPortPl() {
        return portPl;
    }

    public YssNetValue() {
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
    }
}
