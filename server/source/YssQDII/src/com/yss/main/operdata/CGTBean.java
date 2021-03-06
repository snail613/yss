package com.yss.main.operdata;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataInterface;
import com.yss.util.YssException;

public class CGTBean {
	
	private String num = "";              //编号
	private String relaNum = "";          //关联编号
	private String securityCode = "";     //证券代码
	private java.util.Date date = null;   //日期
	private java.util.Date settleDate = null;   //结算日期，卖出时用到
	private String tradeTypeCode = "";    //交易方式
	private double cost;                  //成本
	private double marketValue;           //市值
	private double app;                   //估增
	private double cgt;                   //资本利得税
	private double bcgt;                  //本位币资本利得税
	private String curyCode;              //币种
	private String cashAccCode;           //现金账户
	private String portCode;
	
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public String getRelaNum() {
		return relaNum;
	}
	public void setRelaNum(String relaNum) {
		this.relaNum = relaNum;
	}
	public String getCuryCode() {
		return curyCode;
	}
	public void setCuryCode(String curyCode) {
		this.curyCode = curyCode;
	}
	public String getCashAccCode() {
		return cashAccCode;
	}
	public void setCashAccCode(String cashAccCode) {
		this.cashAccCode = cashAccCode;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public java.util.Date getDate() {
		return date;
	}
	public void setDate(java.util.Date date) {
		this.date = date;
	}
	public java.util.Date getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(java.util.Date settleDate) {
		this.settleDate = settleDate;
	}
	public String getTradeTypeCode() {
		return tradeTypeCode;
	}
	public void setTradeTypeCode(String tradeTypeCode) {
		this.tradeTypeCode = tradeTypeCode;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getMarketValue() {
		return marketValue;
	}
	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}
	public double getApp() {
		return app;
	}
	public void setApp(double app) {
		this.app = app;
	}
	public double getCgt() {
		return cgt;
	}
	public void setCgt(double cgt) {
		this.cgt = cgt;
	}
	public double getBcgt() {
		return bcgt;
	}
	public void setBcgt(double bcgt) {
		this.bcgt = bcgt;
	}
}
