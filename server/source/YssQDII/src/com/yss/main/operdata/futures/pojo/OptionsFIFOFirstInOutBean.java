package com.yss.main.operdata.futures.pojo;

import java.util.Date;

/**
 * 期权先入先出库存表实体bean
 * @author xuqiji 20100505
 *
 */
public class OptionsFIFOFirstInOutBean {
	private String sNum = "";//成交编号
	private String sStorageDate ="9998-12-31";//库存日期
	private String sSecurityCode = "";//证券代码
	private String sPortCode = "";//组合代码
	private double dCuryCost = 0;//原币成本
	private double dPortCuryCost = 0;//组合货币成本
	private double dBaseCuryCost = 0;//基础货币成本
	private double dBaseCuryRate = 1;//基础汇率
	private double dPortCuryRate = 1;//组合汇率
	private double dBailMoney = 0;//保证金金额
	private double dStorageAmount = 0;//库存数量
	//add by fangjiang 2011.09.09 story 1342
	private double curyValue;     //先入先出昨日原币估值增值余额
	private double baseCuryValue; //先入先出昨日基础货币估值增值余额
	private double portCuryValue; //先入先出昨日本位币估值增值余额
	
	public double getCuryValue() {
		return curyValue;
	}

	public void setCuryValue(double curyValue) {
		this.curyValue = curyValue;
	}

	public double getBaseCuryValue() {
		return baseCuryValue;
	}

	public void setBaseCuryValue(double baseCuryValue) {
		this.baseCuryValue = baseCuryValue;
	}

	public double getPortCuryValue() {
		return portCuryValue;
	}

	public void setPortCuryValue(double portCuryValue) {
		this.portCuryValue = portCuryValue;
	}
	//-----------------------

	public double getDStorageAmount() {
		return dStorageAmount;
	}

	public void setDStorageAmount(double storageAmount) {
		dStorageAmount = storageAmount;
	}

	public OptionsFIFOFirstInOutBean() {
		super();
	}
	
	public double getDBailMoney() {
		return dBailMoney;
	}
	public void setDBailMoney(double bailMoney) {
		dBailMoney = bailMoney;
	}
	public double getDBaseCuryCost() {
		return dBaseCuryCost;
	}
	public void setDBaseCuryCost(double baseCuryCost) {
		dBaseCuryCost = baseCuryCost;
	}
	public double getDBaseCuryRate() {
		return dBaseCuryRate;
	}
	public void setDBaseCuryRate(double baseCuryRate) {
		dBaseCuryRate = baseCuryRate;
	}
	public double getDCuryCost() {
		return dCuryCost;
	}
	public void setDCuryCost(double curyCost) {
		dCuryCost = curyCost;
	}
	public double getDPortCuryCost() {
		return dPortCuryCost;
	}
	public void setDPortCuryCost(double portCuryCost) {
		dPortCuryCost = portCuryCost;
	}
	public double getDPortCuryRate() {
		return dPortCuryRate;
	}
	public void setDPortCuryRate(double portCuryRate) {
		dPortCuryRate = portCuryRate;
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
	public String getSStorageDate() {
		return sStorageDate;
	}
	public void setSStorageDate(String storageDate) {
		sStorageDate = storageDate;
	}
}
