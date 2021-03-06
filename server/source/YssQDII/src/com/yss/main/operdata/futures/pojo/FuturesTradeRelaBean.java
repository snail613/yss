package com.yss.main.operdata.futures.pojo;

import java.util.*;

public class FuturesTradeRelaBean {
    public FuturesTradeRelaBean() {
    }

    public double getBaseCuryMoney() {
        return baseCuryMoney;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getCreator() {
        return creator;
    }

    public double getMoney() {
        return money;
    }

    public String getNum() {
        return num;
    }

    public double getPortCuryMoney() {
        return portCuryMoney;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public int getSettleState() {
        return settleState;
    }

    public String getTsfTypeCode() {
        return tsfTypeCode;
    }

    public Date getTransDate() {
        return transDate;
    }

    public double getStorageAmount() {
        return storageAmount;
    }

    public String getCloseNum() {
        return closeNum;
    }

    public double getBailMoney() {
        return bailMoney;
    }

    public String getPortCode() {
        return portCode;
    }

    public void setSettleState(int settleState) {
        this.settleState = settleState;
    }

    public void setTsfTypeCode(String tsfTypeCode) {
        this.tsfTypeCode = tsfTypeCode;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setPortCuryMoney(double portCuryMoney) {
        this.portCuryMoney = portCuryMoney;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setBaseCuryMoney(double baseCuryMoney) {
        this.baseCuryMoney = baseCuryMoney;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public void setStorageAmount(double storageAmount) {
        this.storageAmount = storageAmount;
    }

    public void setCloseNum(String closeNum) {
        this.closeNum = closeNum;
    }

    public void setBailMoney(double bailMoney) {
        this.bailMoney = bailMoney;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    //交易编号
    private String num = "";

    //关联编号
    private String closeNum = "";

    //调拨类型
    private String tsfTypeCode = "";

    //业务日期
    private java.util.Date transDate;

    //原币金额
    private double money;

    //基础汇率
    private double baseCuryRate;

    //基础货币金额
    private double baseCuryMoney;

    //组合汇率
    private double portCuryRate;

    //组合货币金额
    private double portCuryMoney;

    //结算状态
    private int settleState;

    //库存数量
    private double storageAmount;

    //保证金
    private double bailMoney;

    //组合代码 2009.03.21 蒋锦 添加
    private String portCode;
	private String creator = "";
    private String createTime = "";
    private String checkUser = "";


	private String checkTime = "";
    //--- add by songjie 2012.12.08 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
    private String brokerCode = "";//券商代码
    private double valMoney;
    private double valBaseMoney;
    private double valPortMoney;
    public String getBrokerCode() {
		return brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public double getValMoney() {
		return valMoney;
	}

	public void setValMoney(double valMoney) {
		this.valMoney = valMoney;
	}

	public double getValBaseMoney() {
		return valBaseMoney;
	}

	public void setValBaseMoney(double valBaseMoney) {
		this.valBaseMoney = valBaseMoney;
	}

	public double getValPortMoney() {
		return valPortMoney;
	}

	public void setValPortMoney(double valPortMoney) {
		this.valPortMoney = valPortMoney;
	}
	
	//--- add by songjie 2012.12.08 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
}
