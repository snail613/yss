package com.yss.main.operdeal.bond.pojo;

import java.util.Date;

import com.yss.dsub.BaseBean;

public class BondFactRateBean extends BaseBean{

	private String securityCode = "";
	private String portCode = "";
	private String investType = "";
	private Date tradeDate = null;
	private Date changeDate = null;
	private double factRate = 0.0;
	
	public double getFactRate() {
		return factRate;
	}
	public void setFactRate(double factRate) {
		this.factRate = factRate;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public String getInvestType() {
		return investType;
	}
	public void setInvestType(String investType) {
		this.investType = investType;
	}
	public Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public Date getChangeDate() {
		return changeDate;
	}
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
}
