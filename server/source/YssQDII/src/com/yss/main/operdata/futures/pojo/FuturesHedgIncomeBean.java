package com.yss.main.operdata.futures.pojo;

/**
 * 套期保值收益表实体Bean（Tb_XXX_Data_HedgIncome）
 * @author xuqiji 20100510 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A   
 *
 */
public class FuturesHedgIncomeBean {
	private String sNumOrSec = "";//交易编号或证券代码 当使用先入先出核算时存期货交易编号，当使用移动加权核算时存期货证券代码
	private String sDate = "9998-12-31";//业务日期
	private String sPortCode = "";//组合代码
	private String sSecurityCode = "";//期货代码
	private String sHedgingType ="";//套期类型 ‘fairvalue’-公允价值套期；‘cash’-现金流套期
	private double dStroageAmount = 0;//持仓数量
	private double dBal =0;//原币收益
	private double dBaseCuryBal =0;//基础货币收益
	private double dPortCuryBal = 0;//组合货币收益
	private double dBaseCuryRate =1;//基础汇率
	private double dPortCuryRate =1;//组合汇率
	public FuturesHedgIncomeBean() {
		super();
	}
	public double getDBal() {
		return dBal;
	}
	public void setDBal(double bal) {
		dBal = bal;
	}
	public double getDBaseCuryBal() {
		return dBaseCuryBal;
	}
	public void setDBaseCuryBal(double baseCuryBal) {
		dBaseCuryBal = baseCuryBal;
	}
	public double getDBaseCuryRate() {
		return dBaseCuryRate;
	}
	public void setDBaseCuryRate(double baseCuryRate) {
		dBaseCuryRate = baseCuryRate;
	}
	public double getDPortCuryBal() {
		return dPortCuryBal;
	}
	public void setDPortCuryBal(double portCuryBal) {
		dPortCuryBal = portCuryBal;
	}
	public double getDPortCuryRate() {
		return dPortCuryRate;
	}
	public void setDPortCuryRate(double portCuryRate) {
		dPortCuryRate = portCuryRate;
	}
	public double getDStroageAmount() {
		return dStroageAmount;
	}
	public void setDStroageAmount(double stroageAmount) {
		dStroageAmount = stroageAmount;
	}
	public String getSDate() {
		return sDate;
	}
	public void setSDate(String date) {
		sDate = date;
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

}
