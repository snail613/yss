package com.yss.main.operdata;

import java.util.*;

/**
 * 该类为估值汇率的实体类，用来存放估值汇率相关信息
 * MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie
 * QDV4.1赢时胜上海2009年2月1日05_A
 * 2009.04.11
 *
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ValExRateBean {
    private java.util.Date valDate; //估值日期
    private String portCode; //组合代码
    private String curyCode; //币种代码
    private double baseRate; //基础汇率
    private double OTBaseRate1; //其他基础汇率1
    private double OTBaseRate2; //其他基础汇率2
    private double OTBaseRate3; //其他基础汇率3
    private double portRate; //组合汇率
    private double OTPortRate1; //其他组合汇率1
    private double OTPortRate2; //其他组合汇率2
    private double OTPortRate3; //其他组合汇率3
    private java.util.Date exchangeRateDate; //汇率日期

    public ValExRateBean() {
    }

    public Date getValDate() {
        return valDate;
    }

    public double getPortRate() {
        return portRate;
    }

    public String getPortCode() {
        return portCode;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    public void setValDate(Date valDate) {
        this.valDate = valDate;
    }

    public void setPortRate(double portRate) {
        this.portRate = portRate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setExchangeRateDate(Date exchangeRateDate) {
        this.exchangeRateDate = exchangeRateDate;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public Date getExchangeRateDate() {
        return exchangeRateDate;
    }

    public void setOTPortRate3(double OTPortRate3) {
        this.OTPortRate3 = OTPortRate3;
    }

    public double getOTPortRate3() {
        return OTPortRate3;
    }

    public void setOTPortRate2(double OTPortRate2) {
        this.OTPortRate2 = OTPortRate2;
    }

    public double getOTPortRate2() {
        return OTPortRate2;
    }

    public void setOTPortRate1(double OTPortRate1) {
        this.OTPortRate1 = OTPortRate1;
    }

    public double getOTPortRate1() {
        return OTPortRate1;
    }

    public void setOTBaseRate3(double OTBaseRate3) {
        this.OTBaseRate3 = OTBaseRate3;
    }

    public double getOTBaseRate3() {
        return OTBaseRate3;
    }

    public void setOTBaseRate2(double OTBaseRate2) {
        this.OTBaseRate2 = OTBaseRate2;
    }

    public double getOTBaseRate2() {
        return OTBaseRate2;
    }

    public void setOTBaseRate1(double OTBaseRate1) {
        this.OTBaseRate1 = OTBaseRate1;
    }

    public double getOTBaseRate1() {
        return OTBaseRate1;
    }
}
