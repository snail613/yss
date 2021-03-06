package com.yss.main.operdata.futures.pojo;

/**
 * 期货套期证券关联表实体bean
 * @author xuqiji 20100513
 *
 */
public class FuturesHedgingSecuritySetBean {
	private String sNum="";//交易编号
	private String sSecurityCode="";//交易证券
	private String sSecurityName = "";
	private double dTradeAmount;//交易数量
	private double dTradePrice;//交易价格
	private double dTradeMoney;//交易金额
	private double dBaseCuryRate =1;//基础汇率
	private double dPortCuryRate =1;//组合汇率
	
	public FuturesHedgingSecuritySetBean() {
		super();
	}

	public double getDBaseCuryRate() {
		return dBaseCuryRate;
	}

	public void setDBaseCuryRate(double baseCuryRate) {
		dBaseCuryRate = baseCuryRate;
	}

	public double getDPortCuryRate() {
		return dPortCuryRate;
	}

	public void setDPortCuryRate(double portCuryRate) {
		dPortCuryRate = portCuryRate;
	}

	public double getDTradeAmount() {
		return dTradeAmount;
	}

	public void setDTradeAmount(double tradeAmount) {
		dTradeAmount = tradeAmount;
	}

	public double getDTradeMoney() {
		return dTradeMoney;
	}

	public void setDTradeMoney(double tradeMoney) {
		dTradeMoney = tradeMoney;
	}

	public double getDTradePrice() {
		return dTradePrice;
	}

	public void setDTradePrice(double tradePrice) {
		dTradePrice = tradePrice;
	}

	public String getSNum() {
		return sNum;
	}

	public void setSNum(String num) {
		sNum = num;
	}

	public String getSSecurityCode() {
		return sSecurityCode;
	}

	public void setSSecurityCode(String securityCode) {
		sSecurityCode = securityCode;
	}

	public String getSSecurityName() {
		return sSecurityName;
	}

	public void setSSecurityName(String securityName) {
		sSecurityName = securityName;
	}
}
