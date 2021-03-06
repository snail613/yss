package com.yss.main.operdata.futures.pojo;

/**
 * 期货套期保值交易关联表实体bean
 * @author xuqiji 20100510 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A
 *
 */
public class FuturesHedgRelaBean {
	private String sNum = "";//交易编号
	private String sSetNum = "";//开仓编号
	private String sTradeDate = "9998-12-31";//交易日期
	private String sSecurityCode = "";//证券代码---配套证券的证券代码
	private String sTsfTypeCode = "";//应付应付类型 不同于资金调拨的调拨类型
	private String sPortCode = "";//组合代码
	private String sHedgingType ="";//套期类型 ‘fairvalue’-公允价值套期；‘cash’-现金流套期
	private double dMoney =0;//原币金额
	private double dBaseCuryMoeny =0;//基础货币金额
	private double dPortCuryMoeny = 0;//组合货币金额
	private double dBaseCuryRate =1;//基础汇率
	private double dPortCuryRate =1;//组合汇率
	private double dTradeAmount =0;//交易数量
	public double getDTradeAmount() {
		return dTradeAmount;
	}
	public void setDTradeAmount(double tradeAmount) {
		dTradeAmount = tradeAmount;
	}
	public FuturesHedgRelaBean() {
		super();
	}
	public double getDBaseCuryMoeny() {
		return dBaseCuryMoeny;
	}
	public void setDBaseCuryMoeny(double baseCuryMoeny) {
		dBaseCuryMoeny = baseCuryMoeny;
	}
	public double getDBaseCuryRate() {
		return dBaseCuryRate;
	}
	public void setDBaseCuryRate(double baseCuryRate) {
		dBaseCuryRate = baseCuryRate;
	}
	public double getDMoney() {
		return dMoney;
	}
	public void setDMoney(double money) {
		dMoney = money;
	}
	public double getDPortCuryMoeny() {
		return dPortCuryMoeny;
	}
	public void setDPortCuryMoeny(double portCuryMoeny) {
		dPortCuryMoeny = portCuryMoeny;
	}
	public double getDPortCuryRate() {
		return dPortCuryRate;
	}
	public void setDPortCuryRate(double portCuryRate) {
		dPortCuryRate = portCuryRate;
	}
	public String getSHedgingType() {
		return sHedgingType;
	}
	public void setSHedgingType(String hedgingType) {
		sHedgingType = hedgingType;
	}
	public String getSNum() {
		return sNum;
	}
	public void setSNum(String num) {
		sNum = num;
	}
	public String getSPortCode() {
		return sPortCode;
	}
	public void setSPortCode(String portCode) {
		sPortCode = portCode;
	}
	public String getSSecurityCode() {
		return sSecurityCode;
	}
	public void setSSecurityCode(String securityCode) {
		sSecurityCode = securityCode;
	}
	public String getSTradeDate() {
		return sTradeDate;
	}
	public void setSTradeDate(String tradeDate) {
		sTradeDate = tradeDate;
	}
	public String getSTsfTypeCode() {
		return sTsfTypeCode;
	}
	public void setSTsfTypeCode(String tsfTypeCode) {
		sTsfTypeCode = tsfTypeCode;
	}
	public String getSSetNum() {
		return sSetNum;
	}
	public void setSSetNum(String setNum) {
		sSetNum = setNum;
	}

}
