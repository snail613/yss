package com.yss.main.operdeal.report.repfix.cashuserable.pojo;

import java.util.*;

public class TaBean {
    public TaBean() {
    }

    private String fundCode = ""; //基金代码
    java.util.Date tradeDate = null; //申请日期
    private String tradeType = ""; //交易类型
    private double tradeAmount = 0.0; //申购金额/赎回份额

    public Date getTradeDate() {
        return tradeDate;
    }

    public String getTradeType() {
        return tradeType;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setTradeAmount(double tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public double getTradeAmount() {
        return tradeAmount;
    }
}
