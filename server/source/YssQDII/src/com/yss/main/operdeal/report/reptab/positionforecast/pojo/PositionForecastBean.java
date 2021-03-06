package com.yss.main.operdeal.report.reptab.positionforecast.pojo;

import com.yss.dsub.BaseBean;

public class PositionForecastBean extends BaseBean {
	// ~ 报表属性
	private java.sql.Date dDealDate = null; // 处理日期
	private String sCashAccCode = ""; // 账户代码
	private String sCuryCode = ""; // 币种代码
	private java.sql.Date dForecastDate = null; // 预测日期
	private String sPortCode = ""; // 组合代码

	private double dBal = 0; // 账面余额_原币
	private double dPortBal = 0; // 账面余额_本位币
	private double dForecastInMoney = 0; // 待进款_原币
	private double dForecastInPortMoney = 0; // 待进款_本位币
	private double dForecastOutMoney = 0; // 待出款_原币
	private double dForecastOutPortMoney = 0; // 待出款_本位币
	private double dForecastBal = 0; // 预测数_原币
	private double dForecastPortBal = 0; // 预测数_本位币
	private double dOtherMoney = 0;
	private double dOtherPortMoney = 0; // 其他_本位币

	public void PositionForecastBean() {
	}

	public java.sql.Date getDealDate() {
		return dDealDate;
	}

	public void setDealDate(java.sql.Date dDealDate) {
		this.dDealDate = dDealDate;
	}

	public String getCashAccCode() {
		return sCashAccCode;
	}

	public void setCashAccCode(String sCashAccCode) {
		this.sCashAccCode = sCashAccCode;
	}

	public String getCuryCode() {
		return sCuryCode;
	}

	public void setCuryCode(String sCuryCode) {
		this.sCuryCode = sCuryCode;
	}

	public java.sql.Date getForecastDate() {
		return dForecastDate;
	}

	public void setForecastDate(java.sql.Date dForecastDate) {
		this.dForecastDate = dForecastDate;
	}

	public String getPortCode() {
		return sPortCode;
	}

	public void setPortCode(String sPortCode) {
		this.sPortCode = sPortCode;
	}

	public double getBal() {
		return dBal;
	}

	public void setBal(double dBal) {
		this.dBal = dBal;
	}

	public double getPortBal() {
		return dPortBal;
	}

	public void setPortBal(double dPortBal) {
		this.dPortBal = dPortBal;
	}

	public double getForecastInMoney() {
		return dForecastInMoney;
	}

	public void setForecastInMoney(double dForecastInMoney) {
		this.dForecastInMoney = dForecastInMoney;
	}

	public double getForecastInPortMoney() {
		return dForecastInPortMoney;
	}

	public void setForecastInPortMoney(double dForecastInPortMoney) {
		this.dForecastInPortMoney = dForecastInPortMoney;
	}

	public double getForecastOutMoney() {
		return dForecastOutMoney;
	}

	public void setForecastOutMoney(double dForecastOutMoney) {
		this.dForecastOutMoney = dForecastOutMoney;
	}

	public double getForecastOutPortMoney() {
		return dForecastOutPortMoney;
	}

	public void setForecastOutPortMoney(double dForecastOutPortMoney) {
		this.dForecastOutPortMoney = dForecastOutPortMoney;
	}

	public double getForecastBal() {
		return dForecastBal;
	}

	public void setForecastBal(double dForecastBal) {
		this.dForecastBal = dForecastBal;
	}

	public double getForecastPortBal() {
		return dForecastPortBal;
	}

	public void setForecastPortBal(double dForecastPortBal) {
		this.dForecastPortBal = dForecastPortBal;
	}

	public double getOtherPortMoney() {
		return dOtherPortMoney;
	}

	public void setOtherPortMoney(double dOhterMoney) {
		this.dOtherPortMoney = dOhterMoney;
	}

	public double getdOtherMoney() {
		return dOtherMoney;
	}

	public void setdOtherMoney(double dOtherMoney) {
		this.dOtherMoney = dOtherMoney;
	}
	
}
