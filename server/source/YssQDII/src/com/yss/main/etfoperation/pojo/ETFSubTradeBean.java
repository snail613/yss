package com.yss.main.etfoperation.pojo;

import com.yss.dsub.BaseDataSettingBean;

/**
 * ETF交易数据子表的实体类
 * 
 * @author xuqiji 20091119 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 * 
 */
public class ETFSubTradeBean extends BaseDataSettingBean {
	private String num = ""; // 交易拆分数据流水号

	private String securityCode = ""; // 交易证券代码

	private String portCode = ""; // 组合代码

	private String tradeCode = ""; // 交易方式代码

	private String cashAcctCode = ""; // 现金帐户代码

	private String bargainDate = "1900-01-01"; // 成交日期

	private String bargainTime = "00:00:00"; // 成交时间

	private double portCuryRate; // 组合汇率

	private double baseCuryRate; // 基础汇率

	private double tradeAmount; // 交易数量

	private double tradePrice; // 交易价格

	private double tradeMoney; // 交易总额

	private String FFeeCode1 = ""; // 为了直接在后台进行汇总计算

	private String FFeeCode2 = "";

	private String FFeeCode3 = "";

	private String FFeeCode4 = "";

	private String FFeeCode5 = "";

	private String FFeeCode6 = "";

	private String FFeeCode7 = "";

	private String FFeeCode8 = "";

	private double FTradeFee1 = 0; // 为了直接在后台进行汇总计算

	private double FTradeFee2 = 0;

	private double FTradeFee3 = 0;

	private double FTradeFee4 = 0;

	private double FTradeFee5 = 0;

	private double FTradeFee6 = 0;

	private double FTradeFee7 = 0;

	private double FTradeFee8 = 0;
	
	private double totalCost = 0;//实收实付金额
	
	private double cost =0;//原币成本
	
	private double baseCuryCost =0;//基础货币成本
	
	private double portCuryCost =0;//组合货币成本
	
	private String ETFTradeWayCode ="";//交易类型  主动投资 --- ACTIVE,补 票 ---- REPLACE
	
	private double BBTotalCost =0;//本币实收实付金额
	
	private double vmoney = 0;//原币估值增值
	
	private double vBBMoney =0;//本币估值增值
	
	private double BBTradeFee = 0;//本币交易费用
	
	//add by fangjiang 2013.01.15 STORY #3402
	private double fz = 0.0; //分子，计算单位成本需要，因为单位成本不保留位数
	private double fm = 0.0; //分母，计算单位成本需要，因为单位成本不保留位数

	public double getFz() {
		return fz;
	}

	public void setFz(double fz) {
		this.fz = fz;
	}

	public double getFm() {
		return fm;
	}

	public void setFm(double fm) {
		this.fm = fm;
	}

	public String getBargainDate() {
		return bargainDate;
	}

	public void setBargainDate(String bargainDate) {
		this.bargainDate = bargainDate;
	}

	public String getBargainTime() {
		return bargainTime;
	}

	public void setBargainTime(String bargainTime) {
		this.bargainTime = bargainTime;
	}

	public double getBaseCuryCost() {
		return baseCuryCost;
	}

	public void setBaseCuryCost(double baseCuryCost) {
		this.baseCuryCost = baseCuryCost;
	}

	public double getBaseCuryRate() {
		return baseCuryRate;
	}

	public void setBaseCuryRate(double baseCuryRate) {
		this.baseCuryRate = baseCuryRate;
	}

	public double getBBTotalCost() {
		return BBTotalCost;
	}

	public void setBBTotalCost(double totalCost) {
		BBTotalCost = totalCost;
	}

	public double getBBTradeFee() {
		return BBTradeFee;
	}

	public void setBBTradeFee(double tradeFee) {
		BBTradeFee = tradeFee;
	}

	public String getCashAcctCode() {
		return cashAcctCode;
	}

	public void setCashAcctCode(String cashAcctCode) {
		this.cashAcctCode = cashAcctCode;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getETFTradeWayCode() {
		return ETFTradeWayCode;
	}

	public void setETFTradeWayCode(String tradeWayCode) {
		ETFTradeWayCode = tradeWayCode;
	}

	public String getFFeeCode1() {
		return FFeeCode1;
	}

	public void setFFeeCode1(String feeCode1) {
		FFeeCode1 = feeCode1;
	}

	public String getFFeeCode2() {
		return FFeeCode2;
	}

	public void setFFeeCode2(String feeCode2) {
		FFeeCode2 = feeCode2;
	}

	public String getFFeeCode3() {
		return FFeeCode3;
	}

	public void setFFeeCode3(String feeCode3) {
		FFeeCode3 = feeCode3;
	}

	public String getFFeeCode4() {
		return FFeeCode4;
	}

	public void setFFeeCode4(String feeCode4) {
		FFeeCode4 = feeCode4;
	}

	public String getFFeeCode5() {
		return FFeeCode5;
	}

	public void setFFeeCode5(String feeCode5) {
		FFeeCode5 = feeCode5;
	}

	public String getFFeeCode6() {
		return FFeeCode6;
	}

	public void setFFeeCode6(String feeCode6) {
		FFeeCode6 = feeCode6;
	}

	public String getFFeeCode7() {
		return FFeeCode7;
	}

	public void setFFeeCode7(String feeCode7) {
		FFeeCode7 = feeCode7;
	}

	public String getFFeeCode8() {
		return FFeeCode8;
	}

	public void setFFeeCode8(String feeCode8) {
		FFeeCode8 = feeCode8;
	}

	public double getFTradeFee1() {
		return FTradeFee1;
	}

	public void setFTradeFee1(double tradeFee1) {
		FTradeFee1 = tradeFee1;
	}

	public double getFTradeFee2() {
		return FTradeFee2;
	}

	public void setFTradeFee2(double tradeFee2) {
		FTradeFee2 = tradeFee2;
	}

	public double getFTradeFee3() {
		return FTradeFee3;
	}

	public void setFTradeFee3(double tradeFee3) {
		FTradeFee3 = tradeFee3;
	}

	public double getFTradeFee4() {
		return FTradeFee4;
	}

	public void setFTradeFee4(double tradeFee4) {
		FTradeFee4 = tradeFee4;
	}

	public double getFTradeFee5() {
		return FTradeFee5;
	}

	public void setFTradeFee5(double tradeFee5) {
		FTradeFee5 = tradeFee5;
	}

	public double getFTradeFee6() {
		return FTradeFee6;
	}

	public void setFTradeFee6(double tradeFee6) {
		FTradeFee6 = tradeFee6;
	}

	public double getFTradeFee7() {
		return FTradeFee7;
	}

	public void setFTradeFee7(double tradeFee7) {
		FTradeFee7 = tradeFee7;
	}

	public double getFTradeFee8() {
		return FTradeFee8;
	}

	public void setFTradeFee8(double tradeFee8) {
		FTradeFee8 = tradeFee8;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public double getPortCuryCost() {
		return portCuryCost;
	}

	public void setPortCuryCost(double portCuryCost) {
		this.portCuryCost = portCuryCost;
	}

	public double getPortCuryRate() {
		return portCuryRate;
	}

	public void setPortCuryRate(double portCuryRate) {
		this.portCuryRate = portCuryRate;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public double getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public double getTradeMoney() {
		return tradeMoney;
	}

	public void setTradeMoney(double tradeMoney) {
		this.tradeMoney = tradeMoney;
	}

	public double getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(double tradePrice) {
		this.tradePrice = tradePrice;
	}

	public double getVBBMoney() {
		return vBBMoney;
	}

	public void setVBBMoney(double money) {
		vBBMoney = money;
	}

	public double getVmoney() {
		return vmoney;
	}

	public void setVmoney(double vmoney) {
		this.vmoney = vmoney;
	}

}















