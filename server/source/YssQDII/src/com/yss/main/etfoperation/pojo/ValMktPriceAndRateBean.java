package com.yss.main.etfoperation.pojo;

public class ValMktPriceAndRateBean {
	String securityCode;
	double price = 0;
	double baseRate = 0;
	double portRate = 0;
	
	/**
	 * ���캯��
	 */
	public ValMktPriceAndRateBean(){
		
	} 
	
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getBaseRate() {
		return baseRate;
	}
	public void setBaseRate(double baseRate) {
		this.baseRate = baseRate;
	}
	public double getPortRate() {
		return portRate;
	}
	public void setPortRate(double portRate) {
		this.portRate = portRate;
	}
}
