package com.yss.main.operdeal.report.repfix.cashuserable.pojo;

import java.util.*;

public class CashBean {

    public CashBean() {
    }

    java.util.Date beginDate = null; //预期起始日
    java.util.Date endDate = null; //预期截止日
    java.util.Date payDate = null; //支付日期
    String account1 = ""; //现金帐号1
    String account2 = ""; //现金帐户2
    String resume = ""; //摘要
    double money = 0.0; //支付金额
    String cashWay = ""; //资金方向

    public String getAccount2() {
        return account2;
    }

    public String getResume() {
        return resume;
    }

    public Date getEndDate() {
        return endDate;
    }

    public double getMoney() {
        return money;
    }

    public String getCashWay() {
        return cashWay;
    }

    public Date getPayDate() {
        return payDate;
    }

    public String getAccount1() {
        return account1;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setAccount2(String account2) {
        this.account2 = account2;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setCashWay(String cashWay) {
        this.cashWay = cashWay;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public void setAccount1(String account1) {
        this.account1 = account1;
    }

    public Date getBeginDate() {
        return beginDate;
    }
}
