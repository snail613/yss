package com.yss.main.operdata;

import java.util.*;

public class ValMktPriceBean {
    private java.util.Date valDate;
    private String portCode;
    private String securityCode;
    private double price;
    private double otPrice1;
    private double otPrice2;
    private double otPrice3;
    private String marketStatus = ""; //新增 行情状态，by leeyu 2008-10-17 BUG:0000486
    //------ MS00265 QDV4建行2009年2月23日01_B
    private String valType = ""; //估值类型

    //----MS00006 QDV4.1赢时胜上海2009年2月1日05_A add by songjie 2009.04.22----//
    private java.util.Date marketValueDate; //行情日期
    //2009.7.9 蒋锦 添加 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A
    private String attrClsCode;//属性分类

    public Date getMarketValueDate() {
        return marketValueDate;
    }

    public void setMarketValueDate(java.util.Date marketValueDate) {
        this.marketValueDate = marketValueDate;
    }

    //----MS00006 QDV4.1赢时胜上海2009年2月1日05_A add by songjie 2009.04.22----//

    public String getValType() {
        return this.valType;
    }

    public void setValType(String valType) {
        this.valType = valType;
    }

    //---------------------------------------

    //-----MS00272 QDV4赢时胜（上海）2009年2月26日01_B -----
    private String mtvCode; // 估值方法

    public String getMtvCode() {
        return this.mtvCode;
    }

    public void setMtvCode(String mtvCode) {
        this.mtvCode = mtvCode;
    }

    //---------------------------------------------------
    public double getOtPrice3() {
        return otPrice3;
    }

    public String getPortCode() {
        return portCode;
    }

    public double getPrice() {
        return price;
    }

    public Date getValDate() {
        return valDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public double getOtPrice2() {
        return otPrice2;
    }

    public void setOtPrice1(double otPrice1) {
        this.otPrice1 = otPrice1;
    }

    public void setOtPrice3(double otPrice3) {
        this.otPrice3 = otPrice3;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setValDate(Date valDate) {
        this.valDate = valDate;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setOtPrice2(double otPrice2) {
        this.otPrice2 = otPrice2;
    }

    public double getOtPrice1() {
        return otPrice1;
    }

    //================新增 by leeyu 2008-10-17 行情状态
    public void setMarketStatus(String MarketStatus) {
        this.marketStatus = MarketStatus;
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public String getMarketStatus() {
        return marketStatus;
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    //==========================2008-10-17

    public ValMktPriceBean() {
    }
}
