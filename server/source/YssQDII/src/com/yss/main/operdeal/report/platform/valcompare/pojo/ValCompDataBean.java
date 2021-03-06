package com.yss.main.operdeal.report.platform.valcompare.pojo;

/**
 * <p>Title: 表 TB_PFOper_ValCompData 的实体类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ValCompDataBean {

    private String portCode = ""; //组合代码
    private String comProjectCode = ""; //核对方案代码
    private String gzKeyCode = ""; //估值项目编号
    private String gzKeyName = ""; //估值项目名称
    private String cwKeyCode = ""; //财务项目编号
    private String cwKeyName = ""; //财务项目名称
    private double gzCost; //估值原币成本
    private double gzPortCost; //估值组合货币成本
    private double gzMarketValue; //估值原币市值
    private double gzPortMarketValue; //估值组合货币市值
    private double cwCost; //财务原币成本
    private double cwPortCost; //财务组合货币成本
    private double cwMarketValue; //财务原币市值
    private double cwPortMarketValue; //财务组合货币市值
    private double gzAmount; //估值数量
    private double cwAmount; //财务数量
    private String gzReTypeCode; //分类代码  2008.06.12 蒋锦 添加 用于判断类型

    public String getComProjectCode() {
        return comProjectCode;
    }

    public double getCwAmount() {
        return cwAmount;
    }

    public double getCwCost() {
        return cwCost;
    }

    public String getCwKeyCode() {
        return cwKeyCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getCwKeyName() {
        return cwKeyName;
    }

    public double getCwMarketValue() {
        return cwMarketValue;
    }

    public double getCwPortCost() {
        return cwPortCost;
    }

    public double getCwPortMarketValue() {
        return cwPortMarketValue;
    }

    public double getGzAmount() {
        return gzAmount;
    }

    public double getGzCost() {
        return gzCost;
    }

    public String getGzKeyCode() {
        return gzKeyCode;
    }

    public String getGzKeyName() {
        return gzKeyName;
    }

    public double getGzMarketValue() {
        return gzMarketValue;
    }

    public double getGzPortCost() {
        return gzPortCost;
    }

    public double getGzPortMarketValue() {
        return gzPortMarketValue;
    }

    public String getGZReTypeCode() {
        return this.gzReTypeCode;
    }

    public void setComProjectCode(String comProjectCode) {
        this.comProjectCode = comProjectCode;
    }

    public void setCwAmount(double cwAmount) {
        this.cwAmount = cwAmount;
    }

    public void setCwCost(double cwCost) {
        this.cwCost = cwCost;
    }

    public void setCwKeyName(String cwKeyName) {
        this.cwKeyName = cwKeyName;
    }

    public void setCwKeyCode(String cwKeyCode) {
        this.cwKeyCode = cwKeyCode;
    }

    public void setCwMarketValue(double cwMarketValue) {
        this.cwMarketValue = cwMarketValue;
    }

    public void setCwPortCost(double cwPortCost) {
        this.cwPortCost = cwPortCost;
    }

    public void setCwPortMarketValue(double cwPortMarketValue) {
        this.cwPortMarketValue = cwPortMarketValue;
    }

    public void setGzAmount(double gzAmount) {
        this.gzAmount = gzAmount;
    }

    public void setGzCost(double gzCost) {
        this.gzCost = gzCost;
    }

    public void setGzKeyCode(String gzKeyCode) {
        this.gzKeyCode = gzKeyCode;
    }

    public void setGzKeyName(String gzKeyName) {
        this.gzKeyName = gzKeyName;
    }

    public void setGzMarketValue(double gzMarketValue) {
        this.gzMarketValue = gzMarketValue;
    }

    public void setGzPortCost(double gzPortCost) {
        this.gzPortCost = gzPortCost;
    }

    public void setGzPortMarketValue(double gzPortMarketValue) {
        this.gzPortMarketValue = gzPortMarketValue;
    }

    public void setGzReTypeCode(String gzReTypeCode) {
        this.gzReTypeCode = gzReTypeCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public ValCompDataBean() {
    }

}
