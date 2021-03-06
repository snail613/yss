package com.yss.main.etfoperation.pojo;

public class SubStandingBookBean {

	private String bs = "";						//买卖标志
	private String rateType = "";				//汇率类型
	private java.util.Date buyDate = null;		//申赎日期
	private String securityCode = "";			//证券代码
	private String stockHolderCode = "";		//股东代码
	private String tradeNum = "";				//申赎交易编号
	private java.util.Date exRateDate = null;
	private String portCode = "";
	private double rateProLoss = 0;	//汇兑损益
	private double sumRefund = 0;	//应退合计
	private double exchangeRate = 0;//汇率
	private String markType = "";//补票方式：实时补票和钆差补票
	
	public String getMarkType() {
		return markType;
	}
	public void setMarkType(String markType) {
		this.markType = markType;
	}
	public String getRateType() {
		return rateType;
	}
	public void setRateType(String rateType) {
		this.rateType = rateType;
	}
	public String getBs() {
		return bs;
	}
	public void setBs(String bs) {
		this.bs = bs;
	}
	public java.util.Date getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(java.util.Date buyDate) {
		this.buyDate = buyDate;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public String getStockHolderCode() {
		return stockHolderCode;
	}
	public void setStockHolderCode(String stockHolderCode) {
		this.stockHolderCode = stockHolderCode;
	}
	public String getTradeNum() {
		return tradeNum;
	}
	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}
	public double getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	public java.util.Date getExRateDate() {
		return exRateDate;
	}
	public void setExRateDate(java.util.Date exRateDate) {
		this.exRateDate = exRateDate;
	}
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public double getRateProLoss() {
		return rateProLoss;
	}
	public void setRateProLoss(double rateProLoss) {
		this.rateProLoss = rateProLoss;
	}
	public double getSumRefund() {
		return sumRefund;
	}
	public void setSumRefund(double sumRefund) {
		this.sumRefund = sumRefund;
	}
}
