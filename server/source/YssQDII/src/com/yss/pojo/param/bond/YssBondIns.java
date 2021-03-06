package com.yss.pojo.param.bond;

import java.math.BigDecimal;

public class YssBondIns {
    private String securityCode = "";
    private String portCode = "";
    private String analysisCode1 = "";
    private String analysisCode2 = "";
    private String analysisCode3 = "";
    private java.util.Date insDate; //计息日期
    private double insAmount; //计息数量
    private String insType; //计息类型  Day-每日利息  Buy-买入利息  Sell-卖出利息
    private String holidaysCode = ""; //节假日群代码
    private double disRate; //贴现率
    private double Factor; //报价因子
    private String attrClsCode = " ";//2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
    private double cpiPrice;//add by yanghaiming 20110212 #461
    //---add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B---//
    private String investType = "C";//投资类型	//20130706 modified by liubo.Bug ##8559.按数据库说明书的描述，这个字段应该默认“C”
    //---add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
    private BigDecimal faceValue = null;//票面金额
    
    public BigDecimal getFaceValue() {
    	return this.faceValue;
    }
    
    public void setFaceValue(BigDecimal faceValue){
    	this.faceValue = faceValue;
    }
    //---add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
    
    public String getInvestType() {
		return investType;
	}

	public void setInvestType(String investType) {
		this.investType = investType;
	}
	//---add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B---//
	public double getCpiPrice() {
		return cpiPrice;
	}

	public void setCpiPrice(double cpiPrice) {
		this.cpiPrice = cpiPrice;
	}



	//20100327 蒋锦 添加 可传送结算日期 国内 MS00955
    private java.util.Date settleDate;//结算日期
   
    
    public java.util.Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(java.util.Date settleDate) {
		this.settleDate = settleDate;
	}



	//2009-12-17 add by songjie 
    //添加判断 获取 税前票面利率 或 税后票面利率的值
    //MS00847 QDV4赢时胜（北京）2009年11月30日03_B
    private boolean isBeforeRate = false;
    
    public boolean getIsBeforeRate(){
    	return isBeforeRate;
    }
    
    public void setIsBeforeRate(boolean isBeforeRate){
    	this.isBeforeRate = isBeforeRate;
    }
    //2009-12-17 add by songjie 
    //添加判断 获取 税前票面利率 或 税后票面利率的值
    //MS00847 QDV4赢时胜（北京）2009年11月30日03_B
   
    //add by songjie 2010.03.27 
    private boolean isRate100 = false;
    
    public boolean getIsRate100() {
		return isRate100;
	}

	public void setIsRate100(boolean isRate100) {
		this.isRate100 = isRate100;
	}
	//add by songjie 2010.03.27 

	public java.util.Date getInsDate() {
        return insDate;
    }

    public String getAnalysisCode2() {
        return analysisCode2;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getAnalysisCode1() {
        return analysisCode1;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getAnalysisCode3() {
        return analysisCode3;
    }

    public void setInsAmount(double insAmount) {
        this.insAmount = insAmount;
    }

    public void setInsDate(java.util.Date insDate) {
        this.insDate = insDate;
    }

    public void setAnalysisCode2(String analysisCode2) {
        this.analysisCode2 = analysisCode2;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setAnalysisCode1(String analysisCode1) {
        this.analysisCode1 = analysisCode1;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setAnalysisCode3(String analysisCode3) {
        this.analysisCode3 = analysisCode3;
    }

    public void setInsType(String insType) {
        this.insType = insType;
    }

    public void setHolidaysCode(String holidaysCode) {
        this.holidaysCode = holidaysCode;
    }

    public void setDisRate(double disRate) {
        this.disRate = disRate;
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public void setFactor(double Factor) {
        this.Factor = Factor;
    }

    public double getInsAmount() {
        return insAmount;
    }

    public String getInsType() {
        return insType;
    }

    public String getHolidaysCode() {
        return holidaysCode;
    }

    public double getDisRate() {
        return disRate;
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    public double getFactor() {
        return Factor;
    }
    
    

    public YssBondIns() {
    }
}
