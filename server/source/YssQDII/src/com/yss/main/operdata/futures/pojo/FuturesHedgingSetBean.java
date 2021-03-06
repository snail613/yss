package com.yss.main.operdata.futures.pojo;

public class FuturesHedgingSetBean {
	private String sFNum="";//交易编号
	private String sTradeDate = "9998-12-31";
	private double dFTradeAmount;//交易数量
	private double dFTradeMoney;//成交金额
	private String sFHedgingType="";//套期类型
	private String sSecurityCode="";//交易证券
	private String sSecurityName="";
	private String sPortCode="";//投资组合
	private String sPortName="";
	private String sTradeTypeCode="";//交易方式
	private String sTradeTypeName="";
	private String sBrokeCode="";//交易券商
	private String sBrokeName="";
	private String sInvMgrCode="";//投资经理
	private String sInvMgrName="";
	private String sRecycled = ""; //回收站数据
	private String sOldFNum="";
	
	public String getsOldFNum() {
		return sOldFNum;
	}
	public void setsOldFNum(String sOldFNum) {
		this.sOldFNum = sOldFNum;
	}
	private String sHedgSecurityData ="";//保存套期证券信息
	
	public String getsHedgSecurityData() {
		return sHedgSecurityData;
	}
	public void setsHedgSecurityData(String sHedgSecurityData) {
		this.sHedgSecurityData = sHedgSecurityData;
	}
	public String getsRecycled() {
		return sRecycled;
	}
	public void setsRecycled(String sRecycled) {
		this.sRecycled = sRecycled;
	}
	public String getsTradeDate() {
		return sTradeDate;
	}
	public void setsTradeDate(String sTradeDate) {
		this.sTradeDate = sTradeDate;
	}
	public String getsTradeTypeCode() {
		return sTradeTypeCode;
	}
	public void setsTradeTypeCode(String sTradeTypeCode) {
		this.sTradeTypeCode = sTradeTypeCode;
	}
	public String getsTradeTypeName() {
		return sTradeTypeName;
	}
	public void setsTradeTypeName(String sTradeTypeName) {
		this.sTradeTypeName = sTradeTypeName;
	}
	public String getsSecurityCode() {
		return sSecurityCode;
	}
	public void setsSecurityCode(String sSecurityCode) {
		this.sSecurityCode = sSecurityCode;
	}
	public String getsSecurityName() {
		return sSecurityName;
	}
	public void setsSecurityName(String sSecurityName) {
		this.sSecurityName = sSecurityName;
	}
	public String getsPortCode() {
		return sPortCode;
	}
	public void setsPortCode(String sPortCode) {
		this.sPortCode = sPortCode;
	}
	public String getsPortName() {
		return sPortName;
	}
	public void setsPortName(String sPortName) {
		this.sPortName = sPortName;
	}
	public String getsBrokeCode() {
		return sBrokeCode;
	}
	public void setsBrokeCode(String sBrokeCode) {
		this.sBrokeCode = sBrokeCode;
	}
	public String getsBrokeName() {
		return sBrokeName;
	}
	public void setsBrokeName(String sBrokeName) {
		this.sBrokeName = sBrokeName;
	}
	public String getsInvMgrCode() {
		return sInvMgrCode;
	}
	public void setsInvMgrCode(String sInvMgrCode) {
		this.sInvMgrCode = sInvMgrCode;
	}
	public String getsInvMgrName() {
		return sInvMgrName;
	}
	public void setsInvMgrName(String sInvMgrName) {
		this.sInvMgrName = sInvMgrName;
	}
	public String getsFNum() {
		return sFNum;
	}
	public void setsFNum(String sFNum) {
		this.sFNum = sFNum;
	}
	public double getdFTradeAmount() {
		return dFTradeAmount;
	}
	public void setdFTradeAmount(double dFTradeAmount) {
		this.dFTradeAmount = dFTradeAmount;
	}
	public double getdFTradeMoney() {
		return dFTradeMoney;
	}
	public void setdFTradeMoney(double dFTradeMoney) {
		this.dFTradeMoney = dFTradeMoney;
	}
	public String getsFHedgingType() {
		return sFHedgingType;
	}
	public void setsFHedgingType(String sFHedgingType) {
		this.sFHedgingType = sFHedgingType;
	}
	
	

}
