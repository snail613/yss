package com.yss.main.etfoperation.pojo;

import java.util.Date;

/**
 * add by songjie
 * 2009.10.10
 * V4.1_ETF:MS00002
 * QDV4.1赢时胜（上海）2009年9月28日01_A
 * 交易结算明细表对应的实体类
 */
public class ETFTradeSettleDetailRefBean implements Cloneable{
	private String num = "";//申请编号
	private String refNum = "";//关联编号
	private java.util.Date makeUpDate = null;//补票日期
	private double unitCost = 0;//单位成本
	private double makeUpAmount = 0;//补票数量
	private double oMakeUpCost = 0;//补票成本（原币）
	private double hMakeUpCost = 0;//补票成本（本币）
	private double opReplaceCash = 0;//应付替代款（原币）
	private double hpReplaceCash = 0;//应付替代款（本币）
	private double ocReplaceCash = 0;//可退替代款（原币）
	private double hcReplaceCash = 0;//可退替代款（本币）
	private java.util.Date exRightDate = null;//除权日期
	private double sumAmount = 0;//总数量
	private double realAmount = 0;//实际数量
	private double interest = 0;//派息（原币）
	private double warrantCost = 0;//权证价值（原币）
	private double remaindAmount = 0;//剩余数量
	private double ocRefundSum = 0;//应退合计（原币）
	private double hcRefundSum = 0;//应退合计（本币）
	private double oCanRepCash = 0;//可退替代款（原币）
	private double hCanRepCash = 0;//可退替代款（本币）
	private java.util.Date refundDate = null;//退款日期
	private String dataMark = "";//数据标识
	private String dataDirection = "";//数据方向
	private String settleMark = "";//清算标识
	private double exchangeRate = 0;//汇率	
	private double factAmount = 0;//实际申赎数量
	private double bbinterest = 0;//派息(本币)
	private double bbwarrantCost = 0;//权证价值(本币)
	private double rightRate = 0;//权益数据汇率
	private double tradeUnitCost = 0;//成交单价
	private double feeUnitCost = 0;//费用单价
	private String deleteMark = "";//删除标志
	/**shashijie 2011-08-07 STORY 1434*/
	private double deflationAmount = 0;//缩股数量
	/**end*/
	
	//add by fangjiang 2013.01.15 STORY #3402
	private double fz = 0.0; //分子，计算单位成本需要，因为单位成本不保留位数
	private double fm = 0.0; //分母，计算单位成本需要，因为单位成本不保留位数
	/**add---huhuichao 2013-8-7 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
	private double otherRight = 0;//其他权益(原币)
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
	/**end---huhuichao 2013-8-7 STORY  4276*/
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

	/**
     * 构造函数
     */
    public ETFTradeSettleDetailRefBean() {

    }
    
	public String getDeleteMark() {
		return deleteMark;
	}

	public void setDeleteMark(String deleteMark) {
		this.deleteMark = deleteMark;
	}
    
    public double getTradeUnitCost() {
		return tradeUnitCost;
	}

	public void setTradeUnitCost(double tradeUnitCost) {
		this.tradeUnitCost = tradeUnitCost;
	}

	public double getFeeUnitCost() {
		return feeUnitCost;
	}

	public void setFeeUnitCost(double feeUnitCost) {
		this.feeUnitCost = feeUnitCost;
	}

	public double getRightRate() {
		return rightRate;
	}

	public void setRightRate(double rightRate) {
		this.rightRate = rightRate;
	}
    
	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
    
    public double getWarrantCost() {
        return warrantCost;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getoMakeUpCost() {
		return oMakeUpCost;
	}

	public void setoMakeUpCost(double oMakeUpCost) {
		this.oMakeUpCost = oMakeUpCost;
	}

	public double gethMakeUpCost() {
		return hMakeUpCost;
	}

	public void sethMakeUpCost(double hMakeUpCost) {
		this.hMakeUpCost = hMakeUpCost;
	}

	public double getSumAmount() {
        return sumAmount;
    }

    public String getSettleMark() {
        return settleMark;
    }

    public double getRemaindAmount() {
        return remaindAmount;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public double getRealAmount() {
        return realAmount;
    }

    public double getOpReplaceCash() {
        return opReplaceCash;
    }

    public double getOcReplaceCash() {
        return ocReplaceCash;
    }

    public double getOcRefundSum() {
        return ocRefundSum;
    }

    public double getOCanRepCash() {
        return oCanRepCash;
    }

    public String getNum() {
        return num;
    }
    
    public String getRefNum(){
    	return refNum;
    }

    public Date getMakeUpDate() {
        return makeUpDate;
    }

    public double getMakeUpAmount() {
        return makeUpAmount;
    }

    public double getInterest() {
        return interest;
    }

    public double getHpReplaceCash() {
        return hpReplaceCash;
    }

    public double getHcReplaceCash() {
        return hcReplaceCash;
    }

    public double getHcRefundSum() {
        return hcRefundSum;
    }

    public double getHCanRepCash() {
        return hCanRepCash;
    }

    public Date getExRightDate() {
        return exRightDate;
    }

    public String getDataMark() {
        return dataMark;
    }

    public void setDataDirection(String dataDirection) {
        this.dataDirection = dataDirection;
    }

    public void setWarrantCost(double warrantCost) {
        this.warrantCost = warrantCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public void setSumAmount(double sumAmount) {
        this.sumAmount = sumAmount;
    }

    public void setSettleMark(String settleMark) {
        this.settleMark = settleMark;
    }

    public void setRemaindAmount(double remaindAmount) {
        this.remaindAmount = remaindAmount;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public void setRealAmount(double realAmount) {
        this.realAmount = realAmount;
    }

    public void setOpReplaceCash(double opReplaceCash) {
        this.opReplaceCash = opReplaceCash;
    }

    public void setOcReplaceCash(double ocReplaceCash) {
        this.ocReplaceCash = ocReplaceCash;
    }

    public void setOcRefundSum(double ocRefundSum) {
        this.ocRefundSum = ocRefundSum;
    }

    public void setOCanRepCash(double oCanRepCash) {
        this.oCanRepCash = oCanRepCash;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setRefNum(String refNum){
    	this.refNum = refNum;
    }
    
    public void setMakeUpDate(Date makeUpDate) {
        this.makeUpDate = makeUpDate;
    }

    public void setMakeUpAmount(double makeUpAmount) {
        this.makeUpAmount = makeUpAmount;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public void setHpReplaceCash(double hpReplaceCash) {
        this.hpReplaceCash = hpReplaceCash;
    }

    public void setHcReplaceCash(double hcReplaceCash) {
        this.hcReplaceCash = hcReplaceCash;
    }

    public void setHcRefundSum(double hcRefundSum) {
        this.hcRefundSum = hcRefundSum;
    }

    public void setHCanRepCash(double hCanRepCash) {
        this.hCanRepCash = hCanRepCash;
    }

    public void setExRightDate(Date exRightDate) {
        this.exRightDate = exRightDate;
    }

    public void setDataMark(String dataMark) {
        this.dataMark = dataMark;
    }

    public String getDataDirection() {
        return dataDirection;
    }

	public double getFactAmount() {
		return factAmount;
	}

	public void setFactAmount(double factAmount) {
		this.factAmount = factAmount;
	}

	public double getBbinterest() {
		return bbinterest;
	}

	public void setBbinterest(double bbinterest) {
		this.bbinterest = bbinterest;
	}

	public double getBbwarrantCost() {
		return bbwarrantCost;
	}

	public void setBbwarrantCost(double bbwarrantCost) {
		this.bbwarrantCost = bbwarrantCost;
	}

	public double getDeflationAmount() {
		return deflationAmount;
	}

	public void setDeflationAmount(double deflationAmount) {
		this.deflationAmount = deflationAmount;
	}
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
