package com.yss.main.operdata.futures.pojo;

/**
 * 期货被套证券应收应付数据表实体bean
 * @author xuqiji 20100510 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A
 *
 */
public class FuturesHedgRecpayBean {
	private String sNumOrSec = "";//交易编号或证券代码 当使用先入先出核算时存期货交易编号，当使用移动加权核算时存期货证券代码
	private String sTsfTypeCode = "";//应付应付类型 不同于资金调拨的调拨类型
	private String sTradeDate = "9998-12-31";//库存日期
	private String sSecurityCode = "";//证券代码---配套证券的证券代码
	private String sPortCode = "";//组合代码
	private String sHedgingType ="";//套期类型 ‘fairvalue’-公允价值套期；‘cash’-现金流套期
	private double dMoney =0;//原币金额
	private double dBaseCuryMoeny =0;//基础货币金额
	private double dPortCuryMoeny = 0;//组合货币金额
	private double dBaseCuryRate =1;//基础汇率
	private double dPortCuryRate =1;//组合汇率
	private int iInOut =0;//流入流出方向 1—流入，-1—流出
	public int getIInOut() {
		return iInOut;
	}

	public void setIInOut(int inOut) {
		iInOut = inOut;
	}

	public FuturesHedgRecpayBean() {
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

	public String getSNumOrSec() {
		return sNumOrSec;
	}

	public void setSNumOrSec(String numOrSec) {
		sNumOrSec = numOrSec;
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

	public void setSTradeDate(String TradeDate) {
		sTradeDate = TradeDate;
	}

	public String getSTsfTypeCode() {
		return sTsfTypeCode;
	}

	public void setSTsfTypeCode(String tsfTypeCode) {
		sTsfTypeCode = tsfTypeCode;
	}

}
