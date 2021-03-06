package com.yss.main.etfoperation.pojo;

import java.util.ArrayList;

/**
 * add by songjie 
 * 2009.10.20
 * V4.1_ETF:MS00002
 * QDV4.1赢时胜（上海）2009年9月28日01_A
 * @author 宋洁
 *
 */
public class StandingBookBean {
	private String num = "";//申请编号
	private java.util.Date buyDate = null;//申赎日期
	private String bs = "";//买卖标志
	private String portCode = "";//组合代码
	private String securityCode = "";//证券代码
	private String stockHolderCode = "";//股东代码
	private String brokerCode = "";//券商代码
	private String seatCode = "";//席位号
	private double makeUpAmount = 0;//补票数量
	private double unitCost = 0;//单位成本
	private double replaceCash = 0;//替代金额
	private double canReplaceCash = 0;//可退替代款
	private java.util.Date exRightDate = null;//除权日期
	private double sumAmount = 0;//总数量
	private double realAmount = 0;//实际数量
	private double totalInterest = 0;//总派息
	private double warrantCost = 0;//权证价值
	private java.util.Date makeUpDate1 = null;//第一次补票的日期
	private double makeUpAmount1 = 0;//第一次补票的数量
	private double makeUpUnitCost1 = 0;//第一次补票的单位成本
	private double makeUpRepCash1 = 0;//第一次补票的应付替代款
	private double canMkUpRepCash1 = 0;//第一次补票的可退替代款
	private java.util.Date makeUpDate2 = null;//第二次补票的日期
	private double makeUpAmount2 = 0;//第二次补票的数量
	private double makeUpUnitCost2 = 0;//第二次补票的单位成本
	private double makeUpRepCash2 = 0;//第二次补票的应付替代款
	private double canMkUpRepCash2 = 0;//第二次补票的可退替代款
	private java.util.Date makeUpDate3 = null;//第三次补票的日期
	private double makeUpAmount3 = 0;//第三次补票的数量
	private double makeUpUnitCost3 = 0;//第三次补票的单位成本
	private double makeUpRepCash3 = 0;//第三次补票的应付替代款
	private double canMkUpRepCash3 = 0;//第三次补票的可退替代款
	private java.util.Date makeUpDate4 = null;//第四次补票的日期
	private double makeUpAmount4 = 0;//第四次补票的数量
	private double makeUpUnitCost4 = 0;//第四次补票的单位成本
	private double makeUpRepCash4 = 0;//第四次补票的应付替代款
	private double canMkUpRepCash4 = 0;//第四次补票的可退替代款
	private java.util.Date makeUpDate5 = null;//第五次补票的日期
	private double makeUpAmount5 = 0;//第五次补票的数量
	private double makeUpUnitCost5 = 0;//第五次补票的单位成本
	private double makeUpRepCash5 = 0;//第五次补票的应付替代款
	private double canMkUpRepCash5 = 0;//第五次补票的可退替代款
	private java.util.Date mustMkUpDate = null;//强制补票的日期
	private double mustMkUpAmount = 0;//强制补票的数量
	private double mustMkUpUnitCost = 0;//强制补票的单位成本
	private double mustMkUpRepCash = 0;//强制补票的应付替代款
	private double mustCMkUpRepCash = 0;//强制补票的可退替代款
	private double remaindAmount = 0;//剩余数量
	private double sumReturn = 0;//应退合计
	private java.util.Date refundDate = null;//退款日期
	private double exchangeRate  =0;//汇率
	private double oMakeUpCost1 = 0;//第一次补票的总成本（原币）
	private double hMakeUpCost1 = 0;//第一次补票的总成本（本币）
	private double oMakeUpCost2 = 0;//第二次补票的总成本（原币）
	private double hMakeUpCost2 = 0;//第二次补票的总成本（本币）
	private double oMakeUpCost3 = 0;//第三次补票的总成本（原币）
	private double hMakeUpCost3 = 0;//第三次补票的总成本（本币）
	private double oMakeUpCost4 = 0;//第四次补票的总成本（原币）
	private double hMakeUpCost4 = 0;//第四次补票的总成本（本币）
	private double oMakeUpCost5 = 0;//第五次补票的总成本（原币）
	private double hMakeUpCost5 = 0;//第五次补票的总成本（本币）
	private double oMustMkUpCost = 0;//强制补票的总成本（原币）
	private double hMustMkUpCost = 0;//强制补票的总成本（本币）
	private String orderCode = "";//排序编号
	private String gradeType1 = "";//分级类型1
	private String gradeType2 = "";//分级类型2
	private String gradeType3 = "";//分级类型3
	private double exRate1 = 0;//第一次补票汇率	
	private double exRate2 = 0;//第二次补票汇率	
	private double exRate3 = 0;//第三次补票汇率	
	private double exRate4 = 0;//第四次补票汇率	
	private double exRate5 = 0;//第五次补票汇率	
	private double mustExRate = 0;//强制处理汇率
	private double factExRate = 0;//实际汇率
	private java.util.Date exRateDate = null; //换汇日期
	private double factAmount = 0;//实际补票数量
	private ArrayList subBooks = new ArrayList();  //台帐子表数据
	/**add---shashijie 2011-12-15 STORY 1434	Bean中一直缺少次字段,现在补上*/
	private double cashBal = 0;//现金差额
	/**end---shashijie 2011-12-15 STORY 1434*/
	private String markType = "";//标志类型 time =实时补票 ,difference = 钆差补票
	private String rateType = "";//汇率类型 T+1 = T+1日汇率类型 ,T+4 = T+4 日汇率类型
	private double bBInterest =0;//总派息（本币）
	private double bBWarrantCost =0;//权证价值（本币）
	private double rightRate = 0;//权益数据汇率
	private double tradeUnitCost1 = 0;//第一次补票的成交单价
	private double feeUnitCost1 = 0;//第一次补票的费用单价
	private double tradeUnitCost2 = 0;//第二次补票的成交单价
	private double feeUnitCost2 = 0;//第二次补票的费用单价
	private double tradeUnitCost3 = 0;//第三次补票的成交单价
	private double feeUnitCost3 = 0;//第三次补票的费用单价
	private double tradeUnitCost4 = 0;//第四次补票的成交单价
	private double feeUnitCost4 = 0;//第四次补票的费用单价
	private double tradeUnitCost5 = 0;//第五次补票的成交单价
	private double feeUnitCost5 = 0;//第五次补票的费用单价
	private double mustTradeUnitCost = 0;//强制补票的成交单价
	private double mustFeeUnitCost = 0;//强制补票的费用单价	
	private String tradeNum = "";//成交编号 
	/**add---shashijie 2011.08.07 STORY 1434*/
	private double deflationAmount = 0;//缩股数量
	/**end---shashijie 2011.08.07 STORY 1434*/
	
	/**add---shashijie 2013-5-16 STORY 3713 需求北京-(博时基金)QDIIV4.0(高)20130307001 增加字段*/
	private String Fsecurityname = "";//证券名称
	private String Fexchangecode = "";//市场代码(交易所代码)
	private String Fexchangename = "";//市场名称(交易所名称)
	private String Fpremiumscale = "";//溢价比例
	private double FMmakeupunitcost1 = 0;//补票单位成本本币 
	private double RefundMValue = 0;//可退替代款估值增值
	/**end---shashijie 2013-5-16 STORY 3713 需求北京-(博时基金)QDIIV4.0(高)20130307001*/
	
	/**add---huhuichao 2013-8-3 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
	private double otherRight = 0;//其他权益
	private double bBotherRight = 0;//其他权益（本币）
	public double getOtherRight() {
		return otherRight;
	}

	public void setOtherRight(double otherRight) {
		this.otherRight = otherRight;
	}
	public double getbBOtherRight() {
		return bBotherRight;
	}

	public void setbBOtherRight(double bBotherRight) {
		this.bBotherRight = bBotherRight;
	}
	/**end---huhuichao 2013-8-3 STORY  4276*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public double getTradeUnitCost1() {
		return tradeUnitCost1;
	}

	public void setTradeUnitCost1(double tradeUnitCost1) {
		this.tradeUnitCost1 = tradeUnitCost1;
	}

	public double getFeeUnitCost1() {
		return feeUnitCost1;
	}

	public void setFeeUnitCost1(double feeUnitCost1) {
		this.feeUnitCost1 = feeUnitCost1;
	}

	public double getTradeUnitCost2() {
		return tradeUnitCost2;
	}

	public void setTradeUnitCost2(double tradeUnitCost2) {
		this.tradeUnitCost2 = tradeUnitCost2;
	}

	public double getFeeUnitCost2() {
		return feeUnitCost2;
	}

	public void setFeeUnitCost2(double feeUnitCost2) {
		this.feeUnitCost2 = feeUnitCost2;
	}

	public double getTradeUnitCost3() {
		return tradeUnitCost3;
	}

	public void setTradeUnitCost3(double tradeUnitCost3) {
		this.tradeUnitCost3 = tradeUnitCost3;
	}

	public double getFeeUnitCost3() {
		return feeUnitCost3;
	}

	public void setFeeUnitCost3(double feeUnitCost3) {
		this.feeUnitCost3 = feeUnitCost3;
	}

	public double getTradeUnitCost4() {
		return tradeUnitCost4;
	}

	public void setTradeUnitCost4(double tradeUnitCost4) {
		this.tradeUnitCost4 = tradeUnitCost4;
	}

	public double getFeeUnitCost4() {
		return feeUnitCost4;
	}

	public void setFeeUnitCost4(double feeUnitCost4) {
		this.feeUnitCost4 = feeUnitCost4;
	}

	public double getTradeUnitCost5() {
		return tradeUnitCost5;
	}

	public void setTradeUnitCost5(double tradeUnitCost5) {
		this.tradeUnitCost5 = tradeUnitCost5;
	}

	public double getFeeUnitCost5() {
		return feeUnitCost5;
	}

	public void setFeeUnitCost5(double feeUnitCost5) {
		this.feeUnitCost5 = feeUnitCost5;
	}

	public double getMustTradeUnitCost() {
		return mustTradeUnitCost;
	}

	public void setMustTradeUnitCost(double mustTradeUnitCost) {
		this.mustTradeUnitCost = mustTradeUnitCost;
	}

	public double getMustFeeUnitCost() {
		return mustFeeUnitCost;
	}

	public void setMustFeeUnitCost(double mustFeeUnitCost) {
		this.mustFeeUnitCost = mustFeeUnitCost;
	}

	public double getBBInterest() {
		return bBInterest;
	}

	public void setBBInterest(double interest) {
		bBInterest = interest;
	}

	public double getBBWarrantCost() {
		return bBWarrantCost;
	}

	public void setBBWarrantCost(double warrantCost) {
		bBWarrantCost = warrantCost;
	}

	public String getMarkType() {
		return markType;
	}

	public void setMarkType(String markType) {
		this.markType = markType;
	}

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public double getRightRate() {
		return rightRate;
	}

	public void setRightRate(double rightRate) {
		this.rightRate = rightRate;
	}

	public double getFactAmount() {
		return factAmount;
	}

	public void setFactAmount(double factAmount) {
		this.factAmount = factAmount;
	}
	
	public double getoMakeUpCost1() {
		return oMakeUpCost1;
	}

	public void setoMakeUpCost1(double oMakeUpCost1) {
		this.oMakeUpCost1 = oMakeUpCost1;
	}

	public double gethMakeUpCost1() {
		return hMakeUpCost1;
	}

	public void sethMakeUpCost1(double hMakeUpCost1) {
		this.hMakeUpCost1 = hMakeUpCost1;
	}

	public double getoMakeUpCost2() {
		return oMakeUpCost2;
	}

	public void setoMakeUpCost2(double oMakeUpCost2) {
		this.oMakeUpCost2 = oMakeUpCost2;
	}

	public double gethMakeUpCost2() {
		return hMakeUpCost2;
	}

	public void sethMakeUpCost2(double hMakeUpCost2) {
		this.hMakeUpCost2 = hMakeUpCost2;
	}

	public double getoMakeUpCost3() {
		return oMakeUpCost3;
	}

	public void setoMakeUpCost3(double oMakeUpCost3) {
		this.oMakeUpCost3 = oMakeUpCost3;
	}

	public double gethMakeUpCost3() {
		return hMakeUpCost3;
	}

	public void sethMakeUpCost3(double hMakeUpCost3) {
		this.hMakeUpCost3 = hMakeUpCost3;
	}

	public double getoMakeUpCost4() {
		return oMakeUpCost4;
	}

	public void setoMakeUpCost4(double oMakeUpCost4) {
		this.oMakeUpCost4 = oMakeUpCost4;
	}

	public double gethMakeUpCost4() {
		return hMakeUpCost4;
	}

	public void sethMakeUpCost4(double hMakeUpCost4) {
		this.hMakeUpCost4 = hMakeUpCost4;
	}

	public double getoMakeUpCost5() {
		return oMakeUpCost5;
	}

	public void setoMakeUpCost5(double oMakeUpCost5) {
		this.oMakeUpCost5 = oMakeUpCost5;
	}

	public double gethMakeUpCost5() {
		return hMakeUpCost5;
	}

	public void sethMakeUpCost5(double hMakeUpCost5) {
		this.hMakeUpCost5 = hMakeUpCost5;
	}

	public double getoMustMkUpCost() {
		return oMustMkUpCost;
	}

	public void setoMustMkUpCost(double oMustMkUpCost) {
		this.oMustMkUpCost = oMustMkUpCost;
	}

	public double gethMustMkUpCost() {
		return hMustMkUpCost;
	}

	public void sethMustMkUpCost(double hMustMkUpCost) {
		this.hMustMkUpCost = hMustMkUpCost;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
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

	public double getExRate1() {
		return exRate1;
	}

	public void setExRate1(double exRate1) {
		this.exRate1 = exRate1;
	}

	public double getExRate2() {
		return exRate2;
	}

	public void setExRate2(double exRate2) {
		this.exRate2 = exRate2;
	}

	public double getExRate3() {
		return exRate3;
	}

	public void setExRate3(double exRate3) {
		this.exRate3 = exRate3;
	}

	public double getExRate4() {
		return exRate4;
	}

	public void setExRate4(double exRate4) {
		this.exRate4 = exRate4;
	}

	public double getExRate5() {
		return exRate5;
	}

	public void setExRate5(double exRate5) {
		this.exRate5 = exRate5;
	}

	public double getMustExRate() {
		return mustExRate;
	}

	public void setMustExRate(double mustExRate) {
		this.mustExRate = mustExRate;
	}

	public double getFactExRate() {
		return factExRate;
	}

	public void setFactExRate(double factExRate) {
		this.factExRate = factExRate;
	}

	public java.util.Date getExRateDate() {
		return exRateDate;
	}

	public void setExRateDate(java.util.Date exRateDate) {
		this.exRateDate = exRateDate;
	}

	public ArrayList getSubBooks() {
		return subBooks;
	}

	public void setSubBooks(ArrayList subBooks) {
		this.subBooks = subBooks;
	}

	/**
     * 构造函数
     */
    public StandingBookBean() {

    }
	
	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}
    
	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	
    public java.util.Date getBuyDate() {
		return buyDate;
	}

	public void setBuyDate(java.util.Date buyDate) {
		this.buyDate = buyDate;
	}

	public String getBs() {
		return bs;
	}

	public void setBs(String bs) {
		this.bs = bs;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getStockHolderCode() {
		return stockHolderCode;
	}

	public void setStockHolderCode(String stockHolderCode) {
		this.stockHolderCode = stockHolderCode;
	}

	public String getBrokerCode() {
		return brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public String getSeatCode() {
		return seatCode;
	}

	public void setSeatCode(String seatCode) {
		this.seatCode = seatCode;
	}

	public double getMakeUpAmount() {
		return makeUpAmount;
	}

	public void setMakeUpAmount(double makeUpAmount) {
		this.makeUpAmount = makeUpAmount;
	}

	public double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}

	public double getReplaceCash() {
		return replaceCash;
	}

	public void setReplaceCash(double replaceCash) {
		this.replaceCash = replaceCash;
	}

	public double getCanReplaceCash() {
		return canReplaceCash;
	}

	public void setCanReplaceCash(double canReplaceCash) {
		this.canReplaceCash = canReplaceCash;
	}

	public java.util.Date getExRightDate() {
		return exRightDate;
	}

	public void setExRightDate(java.util.Date exRightDate) {
		this.exRightDate = exRightDate;
	}

	public double getSumAmount() {
		return sumAmount;
	}

	public void setSumAmount(double sumAmount) {
		this.sumAmount = sumAmount;
	}

	public double getRealAmount() {
		return realAmount;
	}

	public void setRealAmount(double realAmount) {
		this.realAmount = realAmount;
	}

	public double getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(double totalInterest) {
		this.totalInterest = totalInterest;
	}

	public double getWarrantCost() {
		return warrantCost;
	}

	public void setWarrantCost(double warrantCost) {
		this.warrantCost = warrantCost;
	}

	public java.util.Date getMakeUpDate1() {
		return makeUpDate1;
	}

	public void setMakeUpDate1(java.util.Date makeUpDate1) {
		this.makeUpDate1 = makeUpDate1;
	}

	public double getMakeUpAmount1() {
		return makeUpAmount1;
	}

	public void setMakeUpAmount1(double makeUpAmount1) {
		this.makeUpAmount1 = makeUpAmount1;
	}

	public double getMakeUpUnitCost1() {
		return makeUpUnitCost1;
	}

	public void setMakeUpUnitCost1(double makeUpUnitCost1) {
		this.makeUpUnitCost1 = makeUpUnitCost1;
	}

	public double getMakeUpRepCash1() {
		return makeUpRepCash1;
	}

	public void setMakeUpRepCash1(double makeUpRepCash1) {
		this.makeUpRepCash1 = makeUpRepCash1;
	}

	public double getCanMkUpRepCash1() {
		return canMkUpRepCash1;
	}

	public void setCanMkUpRepCash1(double canMkUpRepCash1) {
		this.canMkUpRepCash1 = canMkUpRepCash1;
	}

	public java.util.Date getMakeUpDate2() {
		return makeUpDate2;
	}

	public void setMakeUpDate2(java.util.Date makeUpDate2) {
		this.makeUpDate2 = makeUpDate2;
	}

	public double getMakeUpAmount2() {
		return makeUpAmount2;
	}

	public void setMakeUpAmount2(double makeUpAmount2) {
		this.makeUpAmount2 = makeUpAmount2;
	}

	public double getMakeUpUnitCost2() {
		return makeUpUnitCost2;
	}

	public void setMakeUpUnitCost2(double makeUpUnitCost2) {
		this.makeUpUnitCost2 = makeUpUnitCost2;
	}

	public double getMakeUpRepCash2() {
		return makeUpRepCash2;
	}

	public void setMakeUpRepCash2(double makeUpRepCash2) {
		this.makeUpRepCash2 = makeUpRepCash2;
	}

	public double getCanMkUpRepCash2() {
		return canMkUpRepCash2;
	}

	public void setCanMkUpRepCash2(double canMkUpRepCash2) {
		this.canMkUpRepCash2 = canMkUpRepCash2;
	}

	public java.util.Date getMakeUpDate3() {
		return makeUpDate3;
	}

	public void setMakeUpDate3(java.util.Date makeUpDate3) {
		this.makeUpDate3 = makeUpDate3;
	}

	public double getMakeUpAmount3() {
		return makeUpAmount3;
	}

	public void setMakeUpAmount3(double makeUpAmount3) {
		this.makeUpAmount3 = makeUpAmount3;
	}

	public double getMakeUpUnitCost3() {
		return makeUpUnitCost3;
	}

	public void setMakeUpUnitCost3(double makeUpUnitCost3) {
		this.makeUpUnitCost3 = makeUpUnitCost3;
	}

	public double getMakeUpRepCash3() {
		return makeUpRepCash3;
	}

	public void setMakeUpRepCash3(double makeUpRepCash3) {
		this.makeUpRepCash3 = makeUpRepCash3;
	}

	public double getCanMkUpRepCash3() {
		return canMkUpRepCash3;
	}

	public void setCanMkUpRepCash3(double canMkUpRepCash3) {
		this.canMkUpRepCash3 = canMkUpRepCash3;
	}

	public java.util.Date getMakeUpDate4() {
		return makeUpDate4;
	}

	public void setMakeUpDate4(java.util.Date makeUpDate4) {
		this.makeUpDate4 = makeUpDate4;
	}

	public double getMakeUpAmount4() {
		return makeUpAmount4;
	}

	public void setMakeUpAmount4(double makeUpAmount4) {
		this.makeUpAmount4 = makeUpAmount4;
	}

	public double getMakeUpUnitCost4() {
		return makeUpUnitCost4;
	}

	public void setMakeUpUnitCost4(double makeUpUnitCost4) {
		this.makeUpUnitCost4 = makeUpUnitCost4;
	}

	public double getMakeUpRepCash4() {
		return makeUpRepCash4;
	}

	public void setMakeUpRepCash4(double makeUpRepCash4) {
		this.makeUpRepCash4 = makeUpRepCash4;
	}

	public double getCanMkUpRepCash4() {
		return canMkUpRepCash4;
	}

	public void setCanMkUpRepCash4(double canMkUpRepCash4) {
		this.canMkUpRepCash4 = canMkUpRepCash4;
	}

	public java.util.Date getMakeUpDate5() {
		return makeUpDate5;
	}

	public void setMakeUpDate5(java.util.Date makeUpDate5) {
		this.makeUpDate5 = makeUpDate5;
	}

	public double getMakeUpAmount5() {
		return makeUpAmount5;
	}

	public void setMakeUpAmount5(double makeUpAmount5) {
		this.makeUpAmount5 = makeUpAmount5;
	}

	public double getMakeUpUnitCost5() {
		return makeUpUnitCost5;
	}

	public void setMakeUpUnitCost5(double makeUpUnitCost5) {
		this.makeUpUnitCost5 = makeUpUnitCost5;
	}

	public double getMakeUpRepCash5() {
		return makeUpRepCash5;
	}

	public void setMakeUpRepCash5(double makeUpRepCash5) {
		this.makeUpRepCash5 = makeUpRepCash5;
	}

	public double getCanMkUpRepCash5() {
		return canMkUpRepCash5;
	}

	public void setCanMkUpRepCash5(double canMkUpRepCash5) {
		this.canMkUpRepCash5 = canMkUpRepCash5;
	}

	public java.util.Date getMustMkUpDate() {
		return mustMkUpDate;
	}

	public void setMustMkUpDate(java.util.Date mustMkUpDate) {
		this.mustMkUpDate = mustMkUpDate;
	}

	public double getMustMkUpAmount() {
		return mustMkUpAmount;
	}

	public void setMustMkUpAmount(double mustMkUpAmount) {
		this.mustMkUpAmount = mustMkUpAmount;
	}

	public double getMustMkUpUnitCost() {
		return mustMkUpUnitCost;
	}

	public void setMustMkUpUnitCost(double mustMkUpUnitCost) {
		this.mustMkUpUnitCost = mustMkUpUnitCost;
	}

	public double getMustMkUpRepCash() {
		return mustMkUpRepCash;
	}

	public void setMustMkUpRepCash(double mustMkUpRepCash) {
		this.mustMkUpRepCash = mustMkUpRepCash;
	}

	public double getMustCMkUpRepCash() {
		return mustCMkUpRepCash;
	}

	public void setMustCMkUpRepCash(double mustCMkUpRepCash) {
		this.mustCMkUpRepCash = mustCMkUpRepCash;
	}

	public double getRemaindAmount() {
		return remaindAmount;
	}

	public void setRemaindAmount(double remaindAmount) {
		this.remaindAmount = remaindAmount;
	}

	public double getSumReturn() {
		return sumReturn;
	}

	public void setSumReturn(double sumReturn) {
		this.sumReturn = sumReturn;
	}

	public java.util.Date getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(java.util.Date refundDate) {
		this.refundDate = refundDate;
	}

	public String getTradeNum() {
		return tradeNum;
	}

	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}

	public double getDeflationAmount() {
		return deflationAmount;
	}

	public void setDeflationAmount(double deflationAmount) {
		this.deflationAmount = deflationAmount;
	}

	public double getCashBal() {
		return cashBal;
	}

	public void setCashBal(double cashBal) {
		this.cashBal = cashBal;
	}

	/**add---shashijie 2013-5-16 返回 fsecurityname 的值 */
	public String getFsecurityname() {
		return Fsecurityname;
	}

	/**add---shashijie 2013-5-16 传入fsecurityname 设置  fsecurityname 的值*/
	public void setFsecurityname(String fsecurityname) {
		Fsecurityname = fsecurityname;
	}

	/**add---shashijie 2013-5-16 返回 fexchangecode 的值 */
	public String getFexchangecode() {
		return Fexchangecode;
	}

	/**add---shashijie 2013-5-16 传入fexchangecode 设置  fexchangecode 的值*/
	public void setFexchangecode(String fexchangecode) {
		Fexchangecode = fexchangecode;
	}

	/**add---shashijie 2013-5-16 返回 fexchangename 的值 */
	public String getFexchangename() {
		return Fexchangename;
	}

	/**add---shashijie 2013-5-16 传入fexchangename 设置  fexchangename 的值*/
	public void setFexchangename(String fexchangename) {
		Fexchangename = fexchangename;
	}

	/**add---shashijie 2013-5-16 返回 fpremiumscale 的值 */
	public String getFpremiumscale() {
		return Fpremiumscale;
	}

	/**add---shashijie 2013-5-16 传入fpremiumscale 设置  fpremiumscale 的值*/
	public void setFpremiumscale(String fpremiumscale) {
		Fpremiumscale = fpremiumscale;
	}

	/**add---shashijie 2013-5-16 返回 fMmakeupunitcost1 的值 */
	public double getFMmakeupunitcost1() {
		return FMmakeupunitcost1;
	}

	/**add---shashijie 2013-5-16 传入fMmakeupunitcost1 设置  fMmakeupunitcost1 的值*/
	public void setFMmakeupunitcost1(double fMmakeupunitcost1) {
		FMmakeupunitcost1 = fMmakeupunitcost1;
	}

	/**add---shashijie 2013-5-16 返回 refundMValue 的值 */
	public double getRefundMValue() {
		return RefundMValue;
	}

	/**add---shashijie 2013-5-16 传入refundMValue 设置  refundMValue 的值*/
	public void setRefundMValue(double refundMValue) {
		RefundMValue = refundMValue;
	}

	
}
