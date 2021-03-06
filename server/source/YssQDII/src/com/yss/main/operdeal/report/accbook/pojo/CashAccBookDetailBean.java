package com.yss.main.operdeal.report.accbook.pojo;

import java.util.*;

public class CashAccBookDetailBean {
    private Date transferDate; //调拨日期
    private Date transDate; //业务日期
    private String accountCode; //帐户代码
    private String accountName; //帐户名称
    private String curyCode; //货币
    private String tsfTypeCode; //调拨类型
    private String subTsfTypeCode; //调拨子类型
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率
    private double inMoney; //流入金额
    private double outMoney; //流出金额
    private double endFund; //结存
    private double portInMoney; //流入金额(本位币金额)
    private double portOutMoney; //流出金额(本位币金额)
    private double portEndFund; //结存(本位币金额)
    private String transNum; //调拨编号(本位币金额)
    private int order; //排序字段
    private String index; //+ 、-

    public CashAccBookDetailBean() {
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getEndFund() {
        return endFund;
    }

    public double getInMoney() {
        return inMoney;
    }

    public double getOutMoney() {
        return outMoney;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public double getPortEndFund() {
        return portEndFund;
    }

    public double getPortInMoney() {
        return portInMoney;
    }

    public double getPortOutMoney() {
        return portOutMoney;
    }

    public String getSubTsfTypeCode() {
        return subTsfTypeCode;
    }

    public Date getTransDate() {
        return transDate;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public String getTsfTypeCode() {
        return tsfTypeCode;
    }

    public void setTsfTypeCode(String tsfTypeCode) {
        this.tsfTypeCode = tsfTypeCode;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public void setSubTsfTypeCode(String subTsfTypeCode) {
        this.subTsfTypeCode = subTsfTypeCode;
    }

    public void setPortOutMoney(double portOutMoney) {
        this.portOutMoney = portOutMoney;
    }

    public void setPortEndFund(double portEndFund) {
        this.portEndFund = portEndFund;
    }

    public void setOutMoney(double outMoney) {
        this.outMoney = outMoney;
    }

    public void setInMoney(double inMoney) {
        this.inMoney = inMoney;
    }

    public void setEndFund(double endFund) {
        this.endFund = endFund;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public void setPortInMoney(double portInMoney) {
        this.portInMoney = portInMoney;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getTransNum() {
        return transNum;
    }

    public void setTransNum(String transNum) {
        this.transNum = transNum;
    }

    public String getIndex() {
        return index;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
