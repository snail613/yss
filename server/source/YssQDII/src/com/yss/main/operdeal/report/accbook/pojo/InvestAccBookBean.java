package com.yss.main.operdeal.report.accbook.pojo;

public class InvestAccBookBean {
    private String code; //代码
    private String name; //名称
    private String curyCode; //货币
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率
    private double initialMoney; //期初金额
    private double inMoney; //流入金额
    private double outMoney; //流出金额
    private double finalMoney; //期末金额
    private double portInitialCost; //期初成本(本位币金额)
    private double portInMoney; //流入金额(本位币金额)
    private double portOutMoney; //流出金额(本位币金额)
    private double portRateFx; //汇兑损益(本位币金额)
    private double portFinalCost; //期末成本(本位币金额)

    public InvestAccBookBean() {
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getCode() {
        return code;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getFinalMoney() {
        return finalMoney;
    }

    public double getInitialMoney() {
        return initialMoney;
    }

    public double getInMoney() {
        return inMoney;
    }

    public String getName() {
        return name;
    }

    public double getOutMoney() {
        return outMoney;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public double getPortFinalCost() {
        return portFinalCost;
    }

    public double getPortInitialCost() {
        return portInitialCost;
    }

    public double getPortInMoney() {
        return portInMoney;
    }

    public double getPortOutMoney() {
        return portOutMoney;
    }

    public double getPortRateFx() {
        return portRateFx;
    }

    public void setPortRateFx(double portRateFx) {
        this.portRateFx = portRateFx;
    }

    public void setPortOutMoney(double portOutMoney) {
        this.portOutMoney = portOutMoney;
    }

    public void setPortInMoney(double portInMoney) {
        this.portInMoney = portInMoney;
    }

    public void setPortInitialCost(double portInitialCost) {
        this.portInitialCost = portInitialCost;
    }

    public void setPortFinalCost(double portFinalCost) {
        this.portFinalCost = portFinalCost;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setOutMoney(double outMoney) {
        this.outMoney = outMoney;
    }

    public void setInMoney(double inMoney) {
        this.inMoney = inMoney;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInitialMoney(double initialMoney) {
        this.initialMoney = initialMoney;
    }

    public void setFinalMoney(double finalMoney) {
        this.finalMoney = finalMoney;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }
}
