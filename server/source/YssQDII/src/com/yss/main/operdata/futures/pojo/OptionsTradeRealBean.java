package com.yss.main.operdata.futures.pojo;

/**
 * <p>Title: xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持</p>
 *
 * <p>Description: 对应期权交易数据表TB_XXX_DATA_OPTIONSTRADERELA</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OptionsTradeRealBean
    extends OptionsTradeBean {
    private String num; //交易编号
    private String closeNum; //卖出或行权编号 01-买入状态-开仓，02-买入状态-平仓,03-卖出状态-开仓,04-卖出状态-平仓，05-买入行权，06-卖出结算
    private String oldNum;
    private String securityCode; //证券代码
    private String securityName; //证券名称
    private String portCode; //组合代码
    private String portName; //组合名称
    private String brokerCode; //券商代码
    private String brokerName; //券商名称
    private String invMgrCode; //投资经理代码
    private String invMgrName; //投资经理名称
    private String tradeTypeCode; //交易方式代码
    private String tradeTypeName; //交易方式名称
    private String begBailAcctCode; //初始保证金帐户代码
    private String begBailAcctName; //初始保证金账户名称
    private String chageBailAcctCode; //变动保证金帐户代码
    private String chageBailAcctName; //变动保证金账户名称
    private String bargainDate; //成交日期
    private String oldBargainDate;
    private String settleDate; //结算日期
    private int settleType; //结算方式
    private double tradeAmount; //交易数量
    private double closeAmount; //卖出数量\库存数量

    private double tradePrice; //交易价格
    private double tradeMoney; //成交金额
    private double begBailMoney; //初始保证金金额
    private double settleMoney; //结算金额  计算卖出估值增值

    private double lastAppMoney; //剩余估值增值
    private int settleState; //结算状态
    private String feeCode1; //费用代码1
    private double tradeFee1; //交易费用1
    private String feeCode2; //费用代码2
    private double tradeFee2; //交易费用2
    private String feeCode3; //费用代码3
    private double tradeFee3; //交易费用3
    private String feeCode4; //费用代码4
    private double tradeFee4; //交易费用4
    private String feeCode5; //费用代码5
    private double tradeFee5; //交易费用5
    private String feeCode6; //费用代码6
    private double tradeFee6; //交易费用6
    private String feeCode7; //费用代码7
    private double tradeFee7; //交易费用7
    private String feeCode8; //费用代码8
    private double tradeFee8; //交易费用8
    private String fees; //交易费用拼接字符串
    private String desc; //描述
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率
    private String investTastic; //投资策略
    private String isOnlyColumn = "1"; //是否只读取列名的标志
    private String recycled; //保存未解析前的字符串

    private String multipy; //放大倍数
    private String bailScale; //保证金比例
    private String bailFix; //每首固定保证金
    private String bailType; //保证金类型
    //----xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务-----//
    private double dExercisePrice;//行权价
    private double dIndexPrice;//指数收盘价
    //------------------------------end-----------------------------------//
    private String tradeType; //保存期权交易类别：认购：CALL、认沽：PUT

    private double YStorageAmount;
    
    private String executeTypeCode; //行权方式        //add by fangjiang 2011.09.08 story 1342
    private double marketValue;     //期权行情价  //add by fangjiang 2011.09.08 story 1342
    private String dropRightDataSource = " "; //放弃行权来源 0-手工，1-自动  add by fangjiang 2011.09.08 story 1342
    
    public String getDropRightDataSource() {
		return dropRightDataSource;
	}

	public void setDropRightDataSource(String dropRightDataSource) {
		this.dropRightDataSource = dropRightDataSource;
	}

	public String getExecuteTypeCode() {
		return executeTypeCode;
	}

	public void setExecuteTypeCode(String executeTypeCode) {
		this.executeTypeCode = executeTypeCode;
	}

	public double getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}

    public OptionsTradeRealBean() {
    }

    public String getBargainDate() {
        return bargainDate;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getBegBailAcctCode() {
        return begBailAcctCode;
    }

    public String getBegBailAcctName() {
        return begBailAcctName;
    }

    public double getBegBailMoney() {
        return begBailMoney;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getChageBailAcctCode() {
        return chageBailAcctCode;
    }

    public String getChageBailAcctName() {
        return chageBailAcctName;
    }

    public String getDesc() {
        return desc;
    }

    public String getFeeCode2() {
        return feeCode2;
    }

    public String getFeeCode1() {
        return feeCode1;
    }

    public double getCloseAmount() {
        return closeAmount;
    }

    public String getFeeCode3() {
        return feeCode3;
    }

    public String getFeeCode4() {
        return feeCode4;
    }

    public String getFeeCode5() {
        return feeCode5;
    }

    public String getFeeCode6() {
        return feeCode6;
    }

    public String getFeeCode7() {
        return feeCode7;
    }

    public String getFeeCode8() {
        return feeCode8;
    }

    public String getFees() {
        return fees;
    }

    public String getInvestTastic() {
        return investTastic;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public String getInvMgrName() {
        return invMgrName;
    }

    public String getIsOnlyColumn() {
        return isOnlyColumn;
    }

    public double getLastAppMoney() {
        return lastAppMoney;
    }

    public String getNum() {
        return num;
    }

    public String getOldBargainDate() {
        return oldBargainDate;
    }

    public String getOldNum() {
        return oldNum;
    }

    public String getPortCode() {
        return portCode;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public String getPortName() {
        return portName;
    }

    public String getRecycled() {
        return recycled;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getSecurityName() {
        return securityName;
    }

    public double getSettleMoney() {
        return settleMoney;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public int getSettleState() {
        return settleState;
    }

    public int getSettleType() {
        return settleType;
    }

    public double getTradeAmount() {
        return tradeAmount;
    }

    public double getTradeFee1() {
        return tradeFee1;
    }

    public double getTradeFee2() {
        return tradeFee2;
    }

    public double getTradeFee3() {
        return tradeFee3;
    }

    public double getTradeFee4() {
        return tradeFee4;
    }

    public double getTradeFee5() {
        return tradeFee5;
    }

    public double getTradeFee6() {
        return tradeFee6;
    }

    public double getTradeFee7() {
        return tradeFee7;
    }

    public double getTradeFee8() {
        return tradeFee8;
    }

    public double getTradeMoney() {
        return tradeMoney;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public String getTradeTypeCode() {
        return tradeTypeCode;
    }

    public String getTradeTypeName() {
        return tradeTypeName;
    }

    public String getBailFix() {
        return bailFix;
    }

    public String getBailScale() {
        return bailScale;
    }

    public String getMultipy() {
        return multipy;
    }

    public String getBailType() {
        return bailType;
    }

    public String getCloseNum() {
        return closeNum;
    }

    public String getTradeType() {
        return tradeType;
    }

    public double getYStorageAmount() {
        return YStorageAmount;
    }

    public void setYStorageAmount(double YStorageAmount) {
        this.YStorageAmount = YStorageAmount;
    }

    public void setTradeTypeName(String tradeTypeName) {
        this.tradeTypeName = tradeTypeName;
    }

    public void setTradeTypeCode(String tradeTypeCode) {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public void setTradeMoney(double tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    public void setTradeFee8(double tradeFee8) {
        this.tradeFee8 = tradeFee8;
    }

    public void setTradeFee7(double tradeFee7) {
        this.tradeFee7 = tradeFee7;
    }

    public void setTradeFee6(double tradeFee6) {
        this.tradeFee6 = tradeFee6;
    }

    public void setTradeFee5(double tradeFee5) {
        this.tradeFee5 = tradeFee5;
    }

    public void setTradeFee4(double tradeFee4) {
        this.tradeFee4 = tradeFee4;
    }

    public void setTradeFee3(double tradeFee3) {
        this.tradeFee3 = tradeFee3;
    }

    public void setTradeFee2(double tradeFee2) {
        this.tradeFee2 = tradeFee2;
    }

    public void setTradeFee1(double tradeFee1) {
        this.tradeFee1 = tradeFee1;
    }

    public void setTradeAmount(double tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public void setSettleType(int settleType) {
        this.settleType = settleType;
    }

    public void setSettleState(int settleState) {
        this.settleState = settleState;
    }

    public void setSettleMoney(double settleMoney) {
        this.settleMoney = settleMoney;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setRecycled(String recycled) {
        this.recycled = recycled;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

    public void setOldBargainDate(String oldBargainDate) {
        this.oldBargainDate = oldBargainDate;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setLastAppMoney(double lastAppMoney) {
        this.lastAppMoney = lastAppMoney;
    }

    public void setIsOnlyColumn(String isOnlyColumn) {
        this.isOnlyColumn = isOnlyColumn;
    }

    public void setInvMgrName(String invMgrName) {
        this.invMgrName = invMgrName;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setInvestTastic(String investTastic) {
        this.investTastic = investTastic;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public void setFeeCode8(String feeCode8) {
        this.feeCode8 = feeCode8;
    }

    public void setFeeCode7(String feeCode7) {
        this.feeCode7 = feeCode7;
    }

    public void setFeeCode6(String feeCode6) {
        this.feeCode6 = feeCode6;
    }

    public void setFeeCode5(String feeCode5) {
        this.feeCode5 = feeCode5;
    }

    public void setFeeCode4(String feeCode4) {
        this.feeCode4 = feeCode4;
    }

    public void setFeeCode3(String feeCode3) {
        this.feeCode3 = feeCode3;
    }

    public void setFeeCode2(String feeCode2) {
        this.feeCode2 = feeCode2;
    }

    public void setFeeCode1(String feeCode1) {
        this.feeCode1 = feeCode1;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCloseAmount(double closeAmount) {
        this.closeAmount = closeAmount;
    }

    public void setChageBailAcctName(String chageBailAcctName) {
        this.chageBailAcctName = chageBailAcctName;
    }

    public void setChageBailAcctCode(String chageBailAcctCode) {
        this.chageBailAcctCode = chageBailAcctCode;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setBegBailMoney(double begBailMoney) {
        this.begBailMoney = begBailMoney;
    }

    public void setBegBailAcctName(String begBailAcctName) {
        this.begBailAcctName = begBailAcctName;
    }

    public void setBegBailAcctCode(String begBailAcctCode) {
        this.begBailAcctCode = begBailAcctCode;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setBargainDate(String bargainDate) {
        this.bargainDate = bargainDate;
    }

    public void setBailFix(String bailFix) {
        this.bailFix = bailFix;
    }

    public void setBailScale(String bailScale) {
        this.bailScale = bailScale;
    }

    public void setMultipy(String multipy) {
        this.multipy = multipy;
    }

    public void setBailType(String bailType) {
        this.bailType = bailType;
    }

    public void setCloseNum(String closeNum) {
        this.closeNum = closeNum;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
    //--------xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务-----//
	public double getDExercisePrice() {
		return dExercisePrice;
	}

	public void setDExercisePrice(double exercisePrice) {
		dExercisePrice = exercisePrice;
	}

	public double getDIndexPrice() {
		return dIndexPrice;
	}

	public void setDIndexPrice(double indexPrice) {
		dIndexPrice = indexPrice;
	}
	//-------------------------------end--------------------------------//
}
