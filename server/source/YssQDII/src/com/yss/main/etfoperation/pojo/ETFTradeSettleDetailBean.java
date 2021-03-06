package com.yss.main.etfoperation.pojo;

import java.util.Date;
import java.util.ArrayList;

import com.yss.main.parasetting.SecurityBean;

/**
 * add by songjie
 * 2009.10.10
 * V4.1_ETF:MS00002
 * QDV4.1赢时胜（上海）2009年9月28日01_A
 * 交易结算明细表对应的实体类
 */
public class ETFTradeSettleDetailBean implements Cloneable{
	private String num = "";//申请编号
	private String portCode = "";//组合代码
	private String securityCode = "";//证券代码
	private String stockHolderCode = "";//股东代码
	private String brokerCode = "";//券商代码
	private String seatCode = "";//席位代码
	private String bs = "";//买卖标志
	private java.util.Date buyDate = null;//申购日期
	private double replaceAmount = 0;//替代数量
	private double BraketNum = 0;//篮子数
	private double unitCost = 0;//单位成本
	private double oReplaceCash = 0;//替代金额(原币)
	private double hReplaceCash = 0;//替代金额(本币)
	private double ocReplaceCash = 0;//可退替代款(原币)
	private double hcReplaceCash = 0;//可退替代款(本币)
	private double exchangeRate = 0;//汇率
	private ArrayList alTradeSettleDel = null;//用于储存明细的实体类
	private ArrayList alTradeSettleDelRef = new ArrayList();//用于储存明细关联表的实体类
	//用于储存相同申请编号的交易结算明细关联表的实体类
	private ETFTradeSettleDetailRefBean tradeSettleDelRef = null;
	private SecurityBean securityBean = null;//证券信息设置实体类
	private double price = 0;
	private String tradeNum="";//成交编号
	private String marktype = "";//标志类型 time =实时补票  difference = 钆差补票
	//and by fangjiang 2013.01.08 STORY #3402
	private ETFTradeSettleDetailRefBean targetDelRef = null;

	public ETFTradeSettleDetailRefBean getTargetDelRef() {
		return targetDelRef;
	}

	public void setTargetDelRef(ETFTradeSettleDetailRefBean targetDelRef) {
		this.targetDelRef = targetDelRef;
	}
	//end by fangjiang 2013.01.08 STORY #3402

	java.util.Date marketValueDate = null;

    public java.util.Date getMarketValueDate() {
		return marketValueDate;
	}

	public void setMarketValueDate(java.util.Date marketValueDate) {
		this.marketValueDate = marketValueDate;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public SecurityBean getSecurityBean() {
		return securityBean;
	}

	public void setSecurityBean(SecurityBean securityBean) {
		this.securityBean = securityBean;
	}

	/**
     * 构造函数
     */
    public ETFTradeSettleDetailBean() {

    }

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
    
    public double getOcReplaceCash() {
        return ocReplaceCash;
    }

    public double getOReplaceCash() {
        return oReplaceCash;
    }

    public double getHcReplaceCash() {
        return hcReplaceCash;
    }

    public double getHReplaceCash() {
        return hReplaceCash;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public String getBs() {
        return bs;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setOcReplaceCash(double ocReplaceCash) {
        this.ocReplaceCash = ocReplaceCash;
    }

    public void setOReplaceCash(double oReplaceCash) {
        this.oReplaceCash = oReplaceCash;
    }

    public void setHcReplaceCash(double hcReplaceCash) {
        this.hcReplaceCash = hcReplaceCash;
    }

    public void setHReplaceCash(double hReplaceCash) {
        this.hReplaceCash = hReplaceCash;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setAlTradeSettleDel(ArrayList alTradeSettleDel) {
        this.alTradeSettleDel = alTradeSettleDel;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public void setStockHolderCode(String stockHolderCode) {
        this.stockHolderCode = stockHolderCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public void setReplaceAmount(double replaceAmount) {
        this.replaceAmount = replaceAmount;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setTradeSettleDelRef(ETFTradeSettleDetailRefBean tradeSettleDelRef) {
        this.tradeSettleDelRef = tradeSettleDelRef;
    }

    public String getNum() {
        return num;
    }

    public ArrayList getAlTradeSettleDel() {
        return alTradeSettleDel;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public String getStockHolderCode() {
        return stockHolderCode;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public double getReplaceAmount() {
        return replaceAmount;
    }

    public String getPortCode() {
        return portCode;
    }

    public ETFTradeSettleDetailRefBean getTradeSettleDelRef() {
        return tradeSettleDelRef;
    }

    public void setBraketNum(double BraketNum) {
        this.BraketNum = BraketNum;
    }

    public double getBraketNum() {
        return BraketNum;
    }

	public String getTradeNum() {
		return tradeNum;
	}

	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}

	public String getMarktype() {
		return marktype;
	}

	public void setMarktype(String marktype) {
		this.marktype = marktype;
	}

	public ArrayList getAlTradeSettleDelRef() {
		return alTradeSettleDelRef;
	}

	public void setAlTradeSettleDelRef(ArrayList alTradeSettleDelRef) {
		this.alTradeSettleDelRef = alTradeSettleDelRef;
	}
	
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
