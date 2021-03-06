package com.yss.main.operdeal.report.navseclend.pojo;

import com.yss.dsub.BaseBean;

public class NavSecLendBean extends BaseBean {

	private java.util.Date navDate = null; //净值日期
    String portCode = ""; //组合代码
    String keyCode = ""; //项目代码
    String keyName = ""; //项目名称
    String orderKeyCode = ""; //排序编号
    double Detail = 0; //明细汇总  --判断是否为明细，在前台时若是汇总加上+ - 号
    String reTypeCode = ""; //记录分类代码  --包括证券，现金，运营收支
    String curyCode = ""; //货币名称
    double price = 0.0; //行情价格
    double otPrice1 = 0.0; //其他行情1
    double otPrice2 = 0.0; //其他行情2
    double otPrice3 = 0.0; //其他行情3
    String sedolCode = ""; //外部代码
    String isinCode = ""; //Insi 代码
    double sparAmt = 0.0; //票面值/股数
    double baseCuryRate = 0.0;
    double portCuryRate = 0.0; //组合汇率
    double bookCost = 0.0; //成本
    double marketValue = 0.0; //市值
    double payValue = 0.0; //浮动盈亏
    double portBookCost = 0.0; //组合货币成本
    double portMarketValue = 0.0; //组合货币市值
    double portPayValue = 0.0; //组合货币浮动盈亏
    double portexchangeValue = 0.0; //汇兑损益
    String gradeType1 = "";
    String gradeType2 = "";
    String gradeType3 = "";
    String gradeType4 = "";
    String gradeType5 = "";
    String gradeType6 = "";
    String invMgrCode = "";
    int inOut = 1;
    double unitCost = 0D; //原币单位成本
    double changeWithCost = 0D; //原币涨跌
    double portUnitCost = 0D; //组合货币单位成本
    double portChangeWithCost = 0D; //组合货币涨跌
    String secLendType = "";	//借入借出标志
    
    double totalBal = 0D;
    double totalMarketValue = 0D;
    double totalValueAdd = 0D;
    double totalPortBal = 0D;
    double totalPortMarketValue = 0D;
    double totalPortValueAdd = 0D;
    double totalAmount = 0D;
    
	public java.util.Date getNavDate() {
		return navDate;
	}
	public void setNavDate(java.util.Date navDate) {
		this.navDate = navDate;
	}
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public String getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getOrderKeyCode() {
		return orderKeyCode;
	}
	public void setOrderKeyCode(String orderKeyCode) {
		this.orderKeyCode = orderKeyCode;
	}
	public double getDetail() {
		return Detail;
	}
	public void setDetail(double detail) {
		Detail = detail;
	}
	public String getReTypeCode() {
		return reTypeCode;
	}
	public void setReTypeCode(String reTypeCode) {
		this.reTypeCode = reTypeCode;
	}
	public String getCuryCode() {
		return curyCode;
	}
	public void setCuryCode(String curyCode) {
		this.curyCode = curyCode;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getOtPrice1() {
		return otPrice1;
	}
	public void setOtPrice1(double otPrice1) {
		this.otPrice1 = otPrice1;
	}
	public double getOtPrice2() {
		return otPrice2;
	}
	public void setOtPrice2(double otPrice2) {
		this.otPrice2 = otPrice2;
	}
	public double getOtPrice3() {
		return otPrice3;
	}
	public void setOtPrice3(double otPrice3) {
		this.otPrice3 = otPrice3;
	}
	public String getSedolCode() {
		return sedolCode;
	}
	public void setSedolCode(String sedolCode) {
		this.sedolCode = sedolCode;
	}
	public String getIsinCode() {
		return isinCode;
	}
	public void setIsinCode(String isinCode) {
		this.isinCode = isinCode;
	}
	public double getSparAmt() {
		return sparAmt;
	}
	public void setSparAmt(double sparAmt) {
		this.sparAmt = sparAmt;
	}
	public double getBaseCuryRate() {
		return baseCuryRate;
	}
	public void setBaseCuryRate(double baseCuryRate) {
		this.baseCuryRate = baseCuryRate;
	}
	public double getPortCuryRate() {
		return portCuryRate;
	}
	public void setPortCuryRate(double portCuryRate) {
		this.portCuryRate = portCuryRate;
	}
	public double getBookCost() {
		return bookCost;
	}
	public void setBookCost(double bookCost) {
		this.bookCost = bookCost;
	}
	public double getMarketValue() {
		return marketValue;
	}
	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}
	public double getPayValue() {
		return payValue;
	}
	public void setPayValue(double payValue) {
		this.payValue = payValue;
	}
	public double getPortBookCost() {
		return portBookCost;
	}
	public void setPortBookCost(double portBookCost) {
		this.portBookCost = portBookCost;
	}
	public double getPortMarketValue() {
		return portMarketValue;
	}
	public void setPortMarketValue(double portMarketValue) {
		this.portMarketValue = portMarketValue;
	}
	public double getPortPayValue() {
		return portPayValue;
	}
	public void setPortPayValue(double portPayValue) {
		this.portPayValue = portPayValue;
	}
	public double getPortexchangeValue() {
		return portexchangeValue;
	}
	public void setPortexchangeValue(double portexchangeValue) {
		this.portexchangeValue = portexchangeValue;
	}
	public String getGradeType1() {
		return gradeType1;
	}
	public void setGradeType1(String gradeType1) {
		this.gradeType1 = gradeType1;
	}
	public String getGradeType2() {
		return gradeType2;
	}
	public void setGradeType2(String gradeType2) {
		this.gradeType2 = gradeType2;
	}
	public String getGradeType3() {
		return gradeType3;
	}
	public void setGradeType3(String gradeType3) {
		this.gradeType3 = gradeType3;
	}
	public String getGradeType4() {
		return gradeType4;
	}
	public void setGradeType4(String gradeType4) {
		this.gradeType4 = gradeType4;
	}
	public String getGradeType5() {
		return gradeType5;
	}
	public void setGradeType5(String gradeType5) {
		this.gradeType5 = gradeType5;
	}
	public String getGradeType6() {
		return gradeType6;
	}
	public void setGradeType6(String gradeType6) {
		this.gradeType6 = gradeType6;
	}
	public String getInvMgrCode() {
		return invMgrCode;
	}
	public void setInvMgrCode(String invMgrCode) {
		this.invMgrCode = invMgrCode;
	}
	public int getInOut() {
		return inOut;
	}
	public void setInOut(int inOut) {
		this.inOut = inOut;
	}
	public double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}
	public double getChangeWithCost() {
		return changeWithCost;
	}
	public void setChangeWithCost(double changeWithCost) {
		this.changeWithCost = changeWithCost;
	}
	public double getPortUnitCost() {
		return portUnitCost;
	}
	public void setPortUnitCost(double portUnitCost) {
		this.portUnitCost = portUnitCost;
	}
	public double getPortChangeWithCost() {
		return portChangeWithCost;
	}
	public void setPortChangeWithCost(double portChangeWithCost) {
		this.portChangeWithCost = portChangeWithCost;
	}
	public String getSecLendType() {
		return secLendType;
	}
	public void setSecLendType(String secLendType) {
		this.secLendType = secLendType;
	}
	public double getTotalBal() {
		return totalBal;
	}
	public void setTotalBal(double totalBal) {
		this.totalBal = totalBal;
	}
	public double getTotalMarketValue() {
		return totalMarketValue;
	}
	public void setTotalMarketValue(double totalMarketValue) {
		this.totalMarketValue = totalMarketValue;
	}
	public double getTotalValueAdd() {
		return totalValueAdd;
	}
	public void setTotalValueAdd(double totalValueAdd) {
		this.totalValueAdd = totalValueAdd;
	}
	public double getTotalPortBal() {
		return totalPortBal;
	}
	public void setTotalPortBal(double totalPortBal) {
		this.totalPortBal = totalPortBal;
	}
	public double getTotalPortMarketValue() {
		return totalPortMarketValue;
	}
	public void setTotalPortMarketValue(double totalPortMarketValue) {
		this.totalPortMarketValue = totalPortMarketValue;
	}
	public double getTotalPortValueAdd() {
		return totalPortValueAdd;
	}
	public void setTotalPortValueAdd(double totalPortValueAdd) {
		this.totalPortValueAdd = totalPortValueAdd;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	
}
