package com.yss.main.operdata.futures.pojo;

/**
 * 期货套期保值库存表实体bean（TB_XXX_Stock_HedgSecurity）
 * @author xuqiji 20100510 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A
 *
 */
public class FuturesHedgingStorageBean {
	private String sNumOrSec = "";//交易编号或证券代码 当使用先入先出核算时存期货交易编号，当使用移动加权核算时存期货证券代码
	private String sStroageDate = "9998-12-31";//库存日期
	private String sSecurityCode = "";//证券代码--配套证券的证券代码
	private String sPortCode = "";//组合代码
	private String sHedgingType ="";//套期类型 ‘fairvalue’-公允价值套期；‘cash’-现金流套期
	private double dStroageAmount = 0;//库存数量
	private double dCuryCost =0;//原币成本
	private double dBaseCuryCost =0;//基础货币成本
	private double dPortCuryCost = 0;//组合货币成本
	private double dBaseCuryRate =1;//基础汇率
	private double dPortCuryRate =1;//组合汇率

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

	public double getDStroageAmount() {
		return dStroageAmount;
	}

	public void setDStroageAmount(double stroageAmount) {
		dStroageAmount = stroageAmount;
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

	public String getSStroageDate() {
		return sStroageDate;
	}

	public void setSStroageDate(String stroageDate) {
		sStroageDate = stroageDate;
	}

	public FuturesHedgingStorageBean() {
		super();
	}

}
