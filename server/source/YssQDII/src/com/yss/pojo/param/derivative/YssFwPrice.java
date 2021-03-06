package com.yss.pojo.param.derivative;

import java.util.Date;
import com.yss.main.dao.IKey;

public class YssFwPrice
    implements IKey {
    private java.util.Date quoteDate; //报价日期
    private java.util.Date spotDate; //即期日期
    private java.util.Date fwDate; //远期日期
    private int limitDays; //期限天数
    private double buyPrice; //远期买价
    private double sellPrice; //远期卖价
    private double buyPoint; //点数买价
    private double sellPoint; //点数卖价
    private double avgPrice; //远期平均价
    private double margin; //利差
    private int factDays; //实际天数

    public double getSellPrice() {
        return sellPrice;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public double getBuyPoint() {
        return buyPoint;
    }

    public double getMargin() {
        return margin;
    }

    public double getSellPoint() {
        return sellPoint;
    }

    public int getFactDays() {
        return factDays;
    }

    public Date getSpotDate() {
        return spotDate;
    }

    public Date getFwDate() {
        return fwDate;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public Date getQuoteDate() {
        return quoteDate;
    }

    public void setLimitDays(int limitDays) {
        this.limitDays = limitDays;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public void setAvgPrice(double avgPrice) {
        this.avgPrice = avgPrice;
    }

    public void setBuyPoint(double buyPoint) {
        this.buyPoint = buyPoint;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public void setSellPoint(double sellPoint) {
        this.sellPoint = sellPoint;
    }

    public void setFactDays(int factDays) {
        this.factDays = factDays;
    }

    public void setSpotDate(Date spotDate) {
        this.spotDate = spotDate;
    }

    public void setFwDate(Date fwDate) {
        this.fwDate = fwDate;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setQuoteDate(Date quoteDate) {
        this.quoteDate = quoteDate;
    }

    public int getLimitDays() {
        return limitDays;
    }

    public YssFwPrice() {
    }

    public Object getKey() {
        return new Integer(this.limitDays);
    }

    public void setKey(Object obj) {
        this.limitDays = ( (Integer) obj).intValue();
    }

}
