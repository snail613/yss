package com.yss.main.operdata.futures.pojo;

/**
 * <p>Title: xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持</p>
 *
 * <p>Description: 期权成本以及估值增值表对应的POJO类</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
/***
 * 此类主要是定义一些变量，以及SETTER,GETTER方法，对应的表：期权成本以及估值增值表
 */

public class OptionsTradeCostRealBean implements Cloneable {
    private String SFNum="";//交易编号
    private String sSetNum = "";//开仓编号
    private String SFCatType="";//品种类型
    private double FCuryCost=0;//原币成本
    private double FPortCuryCost=0;//组合货币成本
    private double FBaseCuryCost=0;//基础货币成本
    private double FBaseCuryRate=0;//基础汇率
    private double FPortCuryRate=0;//组合汇率
    private double FOriginalAddValue=0;//原币估值增值
    private double FBaseAddValue=0;//基础货币估值增值
    private double FPortAddValue=0;//组合货币估值增值
    private String screator="";//创建人
    private String SCreatorTime="";//创建时间
    private String SCheckUser="";//审核人
    private String SCheckTime="";//审核时间
    private String sDate = "";//保存当天交易日期
    private double dTradeAmount = 0;//交易数量
    
    private String portCode = ""; //add by fangjiang 2011.09.14 story 1342

    public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public double getDTradeAmount() {
		return dTradeAmount;
	}

	public void setDTradeAmount(double tradeAmount) {
		dTradeAmount = tradeAmount;
	}

	public String getSDate() {
		return sDate;
	}

	public void setSDate(String date) {
		sDate = date;
	}

	public OptionsTradeCostRealBean() {
   }

    public double getFBaseAddValue() {
        return FBaseAddValue;
    }

    public double getFBaseCuryCost() {
        return FBaseCuryCost;
    }

    public double getFBaseCuryRate() {
        return FBaseCuryRate;
    }

    public double getFCuryCost() {
        return FCuryCost;
    }

    public double getFOriginalAddValue() {
        return FOriginalAddValue;
    }

    public double getFPortAddValue() {
        return FPortAddValue;
    }

    public double getFPortCuryCost() {
        return FPortCuryCost;
    }

    public double getFPortCuryRate() {
        return FPortCuryRate;
    }

    public String getSFCatType() {
        return SFCatType;
    }

    public String getSFNum() {
        return SFNum;
    }

    public void setFBaseAddValue(double FBaseAddValue) {
        this.FBaseAddValue = FBaseAddValue;
    }

    public void setFBaseCuryCost(double FBaseCuryCost) {
        this.FBaseCuryCost = FBaseCuryCost;
    }

    public void setFBaseCuryRate(double FBaseCuryRate) {
        this.FBaseCuryRate = FBaseCuryRate;
    }

    public void setFCuryCost(double FCuryCost) {
        this.FCuryCost = FCuryCost;
    }

    public void setFOriginalAddValue(double FOriginalAddValue) {
        this.FOriginalAddValue = FOriginalAddValue;
    }

    public void setFPortAddValue(double FPortAddValue) {
        this.FPortAddValue = FPortAddValue;
    }

    public void setFPortCuryCost(double FPortCuryCost) {
        this.FPortCuryCost = FPortCuryCost;
    }

    public void setFPortCuryRate(double FPortCuryRate) {
        this.FPortCuryRate = FPortCuryRate;
    }

    public void setSFCatType(String SFCatType) {
        this.SFCatType = SFCatType;
    }

    public void setSFNum(String SFNum) {
        this.SFNum = SFNum;
    }

    public String getSCheckTime() {
        return SCheckTime;
    }

    public String getSCheckUser() {
        return SCheckUser;
    }

    public String getScreator() {
        return screator;
    }

    public String getSCreatorTime() {
        return SCreatorTime;
    }

    public void setSCheckTime(String SCheckTime) {
        this.SCheckTime = SCheckTime;
    }

    public void setSCheckUser(String SCheckUser) {
        this.SCheckUser = SCheckUser;
    }

    public void setScreator(String screator) {
        this.screator = screator;
    }

    public void setSCreatorTime(String SCreatorTime) {
        this.SCreatorTime = SCreatorTime;
    }

	public String getSSetNum() {
		return sSetNum;
	}

	public void setSSetNum(String setNum) {
		sSetNum = setNum;
	}
	
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
}
