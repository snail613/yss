package com.yss.main.etfoperation.pojo;

import java.util.Date;

/**
 * @author fangjiang 2013.01.08 STORY #3402
 * fangjiang 2013.04.15 STORY #3848 ETF台账在处理权益时要用税前比率
 *
 */

public class ETFEquityBean {

	private Date rightDate = null; //除权日期
	private String securityCode = ""; //证券代码
	
	private double sgTaxRatio = 0.0; //送股税后权益比例
	
	private double fhTaxRatio = 0.0; //分红税后权益比例
	private String fhCuryCode = ""; //分红币种
	
	private String pgSecurityCode = ""; //配股代码
	private double pgPrice = 0.0; //配股价格
	private double pgTaxRatio = 0.0; //配股税后权益比例
	private String pgCuryCode = ""; //配股币种
	private double pgHq = 0.0; //配股行情
	private double zgHq = 0.0; //正股行情
	
	
	public Date getRightDate() {
		return rightDate;
	}
	public void setRightDate(Date rightDate) {
		this.rightDate = rightDate;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public double getSgTaxRatio() {
		return sgTaxRatio;
	}
	public void setSgTaxRatio(double sgTaxRatio) {
		this.sgTaxRatio = sgTaxRatio;
	}
	public double getFhTaxRatio() {
		return fhTaxRatio;
	}
	public void setFhTaxRatio(double fhTaxRatio) {
		this.fhTaxRatio = fhTaxRatio;
	}
	public String getFhCuryCode() {
		return fhCuryCode;
	}
	public void setFhCuryCode(String fhCuryCode) {
		this.fhCuryCode = fhCuryCode;
	}
	public String getPgSecurityCode() {
		return pgSecurityCode;
	}
	public void setPgSecurityCode(String pgSecurityCode) {
		this.pgSecurityCode = pgSecurityCode;
	}
	public double getPgPrice() {
		return pgPrice;
	}
	public void setPgPrice(double pgPrice) {
		this.pgPrice = pgPrice;
	}
	public double getPgTaxRatio() {
		return pgTaxRatio;
	}
	public void setPgTaxRatio(double pgTaxRatio) {
		this.pgTaxRatio = pgTaxRatio;
	}
	public String getPgCuryCode() {
		return pgCuryCode;
	}
	public void setPgCuryCode(String pgCuryCode) {
		this.pgCuryCode = pgCuryCode;
	}
	public double getPgHq() {
		return pgHq;
	}
	public void setPgHq(double pgHq) {
		this.pgHq = pgHq;
	}
	public double getZgHq() {
		return zgHq;
	}
	public void setZgHq(double zgHq) {
		this.zgHq = zgHq;
	}
	
	
}
