package com.yss.pojo.cache;

public class YssFeeType {
    private double money = -1; //金额
    private double amount = -1; //数量
    private double cost = -1; //成本
    private double income = -1; //收入
    private double interest = -1; //利息
    private double fee = -1; //费用

    public double getInterest() {
        return interest;
    }

    public double getCost() {
        return cost;
    }

    public double getIncome() {
        return income;
    }

    public double getMoney() {
        return money;
    }

    public double getAmount() {
        return amount;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getFee() {
        return fee;
    }

    public YssFeeType() {
    }
}
