package com.yss.main.operdeal.report.accbook.pojo;

import java.util.*;

public class SecBookDetailRecPayBean {

    private Date transDate; //业务日期
    private String securityCode; //证券代码
    private String securityName; //证券名称
    private String tsfTypeCode; //业务类型
    private String subTsfTypeCode; //业务子类型
    private String curyCode; //货币
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率
    private double inMoney; //流入成本
    private double outMoney; //流出成本
    private double endMoney; //结存成本
    private double portInMoney; //本位币流入成本
    private double portOutMoney; //本位币流出成本
    private double portEndMoney; //本位币结存成本
    private String num; //交易编号
    private int order; //排序字段
    private String index; //+ 、-

    public SecBookDetailRecPayBean() {
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getEndMoney() {
        return endMoney;
    }

    public String getIndex() {
        return index;
    }

    public double getInMoney() {
        return inMoney;
    }

    public String getNum() {
        return num;
    }

    public int getOrder() {
        return order;
    }

    public double getOutMoney() {
        return outMoney;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public double getPortInMoney() {
        return portInMoney;
    }

    public double getPortEndMoney() {
        return portEndMoney;
    }

    public double getPortOutMoney() {
        return portOutMoney;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getSubTsfTypeCode() {
        return subTsfTypeCode;
    }

    public Date getTransDate() {
        return transDate;
    }

    public String getTsfTypeCode() {
        return tsfTypeCode;
    }

    public void setTsfTypeCode(String tsfTypeCode) {
        this.tsfTypeCode = tsfTypeCode;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public void setSubTsfTypeCode(String subTsfTypeCode) {
        this.subTsfTypeCode = subTsfTypeCode;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setPortOutMoney(double portOutMoney) {
        this.portOutMoney = portOutMoney;
    }

    public void setPortInMoney(double portInMoney) {
        this.portInMoney = portInMoney;
    }

    public void setPortEndMoney(double portEndMoney) {
        this.portEndMoney = portEndMoney;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setOutMoney(double outMoney) {
        this.outMoney = outMoney;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setInMoney(double inMoney) {
        this.inMoney = inMoney;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setEndMoney(double endMoney) {
        this.endMoney = endMoney;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }
}
