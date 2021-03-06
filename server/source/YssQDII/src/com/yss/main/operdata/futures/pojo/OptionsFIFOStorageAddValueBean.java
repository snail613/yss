package com.yss.main.operdata.futures.pojo;

/**
 * 期权先入先出估值增值余额表实体bean
 * @author xuqiji 20100506
 *
 */
public class OptionsFIFOStorageAddValueBean {
	private String sNum = "";//成交编号
	private String sStorageDate ="9998-12-31";//库存日期
	private String sSecurityCode = "";//证券代码
	private String sPortCode = "";//组合代码
	private double dCuryValue = 0;//原币估值增值余额
	private double dPortCuryValue = 0;//组合货币估值增值余额
	private double dBaseCuryValue = 0;//基础货币估值增值余额
	private double dBaseCuryRate = 1;//基础汇率
	private double dPortCuryRate = 1;//组合汇率
	public double getDBaseCuryRate() {
		return dBaseCuryRate;
	}
	public void setDBaseCuryRate(double baseCuryRate) {
		dBaseCuryRate = baseCuryRate;
	}
	public double getDBaseCuryValue() {
		return dBaseCuryValue;
	}
	public void setDBaseCuryValue(double baseCuryValue) {
		dBaseCuryValue = baseCuryValue;
	}
	public double getDCuryValue() {
		return dCuryValue;
	}
	public void setDCuryValue(double curyValue) {
		dCuryValue = curyValue;
	}
	public double getDPortCuryRate() {
		return dPortCuryRate;
	}
	public void setDPortCuryRate(double portCuryRate) {
		dPortCuryRate = portCuryRate;
	}
	public double getDPortCuryValue() {
		return dPortCuryValue;
	}
	public void setDPortCuryValue(double portCuryValue) {
		dPortCuryValue = portCuryValue;
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
	public OptionsFIFOStorageAddValueBean() {
		super();
	}

}
