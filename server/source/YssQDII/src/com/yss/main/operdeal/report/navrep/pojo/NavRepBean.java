package com.yss.main.operdeal.report.navrep.pojo;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class NavRepBean
    extends BaseBean
    implements IYssConvert {
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

    //MS00570 QDV4华安2009年07月16日01_AB sj ------------------------------------//
    double unitCost = 0D; //原币单位成本
    double changeWithCost = 0D; //原币涨跌

    //-------------------------------------------------------------------------//

    //===============by xuxuming,20090818.MS00637 QDV4华安2009年08月14日01_AB=============
    double portUnitCost = 0D; //组合货币单位成本
    double portChangeWithCost = 0D; //组合货币涨跌

    //==================================================================================
    
    String investType = " "; //add by fangjiang 2011.07.23 story 1176

    public String getInvestType() {
		return investType;
	}

	public void setInvestType(String investType) {
		this.investType = investType;
	}

	public NavRepBean() {
    }

    //MS00570 QDV4华安2009年07月16日01_AB sj ------------------------------------//
    public double getUnitCost() {
        return this.unitCost;
    }

    public double getChangeWithCost() {
        return this.changeWithCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public void setChangeWithCost(double changeWithCost) {
        this.changeWithCost = changeWithCost;
    }

    //--------------------------------------------------------------------------

    //===============by xuxuming,20090818.MS00637 QDV4华安2009年08月14日01_AB=============
    public void setPortUnitCost(double portUnitCost) {
        this.portUnitCost = portUnitCost;
    }

    public void setPortChangeWithCost(double portChangeWithCost) {
        this.portChangeWithCost = portChangeWithCost;
    }

    public double getPortUnitCost() {
        return this.portUnitCost;
    }

    public double getPortChangeWithCost() {
        return portChangeWithCost;
    }

    //==================================================================================

    public String getPortCode() {
        return portCode;
    }

    public String getSedolCode() {
        return sedolCode;
    }

    public double getOtPrice2() {
        return otPrice2;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public String getOrderKeyCode() {
        return orderKeyCode;
    }

    public double getDetail() {
        return Detail;
    }

    public double getPayValue() {
        return payValue;
    }

    public double getOtPrice3() {
        return otPrice3;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public java.util.Date getNavDate() {
        return navDate;
    }

    public double getOtPrice1() {
        return otPrice1;
    }

    public String getIsinCode() {
        return isinCode;
    }

    public double getPrice() {
        return price;
    }

    public double getBookCost() {
        return bookCost;
    }

    public double getPortPayValue() {
        return portPayValue;
    }

    public double getPortBookCost() {
        return portBookCost;
    }

    public double getSparAmt() {
        return sparAmt;
    }

    public double getPortMarketValue() {
        return portMarketValue;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public void setReTypeCode(String reTypeCode) {
        this.reTypeCode = reTypeCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setSedolCode(String sedolCode) {
        this.sedolCode = sedolCode;
    }

    public void setOtPrice2(double otPrice2) {
        this.otPrice2 = otPrice2;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setOrderKeyCode(String orderKeyCode) {
        this.orderKeyCode = orderKeyCode;
    }

    public void setDetail(double Detail) {
        this.Detail = Detail;
    }

    public void setPayValue(double payValue) {
        this.payValue = payValue;
    }

    public void setOtPrice3(double otPrice3) {
        this.otPrice3 = otPrice3;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setNavDate(java.util.Date navDate) {
        this.navDate = navDate;
    }

    public void setOtPrice1(double otPrice1) {
        this.otPrice1 = otPrice1;
    }

    public void setIsinCode(String isinCode) {
        this.isinCode = isinCode;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setBookCost(double bookCost) {
        this.bookCost = bookCost;
    }

    public void setPortPayValue(double portPayValue) {
        this.portPayValue = portPayValue;
    }

    public void setPortBookCost(double portBookCost) {
        this.portBookCost = portBookCost;
    }

    public void setSparAmt(double sparAmt) {
        this.sparAmt = sparAmt;
    }

    public void setPortMarketValue(double portMarketValue) {
        this.portMarketValue = portMarketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public void setPortexchangeValue(double portexchangeValue) {
        this.portexchangeValue = portexchangeValue;
    }

    public void setGradeType6(String gradeType6) {
        this.gradeType6 = gradeType6;
    }

    public void setGradeType4(String gradeType4) {
        this.gradeType4 = gradeType4;
    }

    public void setGradeType2(String gradeType2) {
        this.gradeType2 = gradeType2;
    }

    public void setGradeType3(String gradeType3) {
        this.gradeType3 = gradeType3;
    }

    public void setGradeType1(String gradeType1) {
        this.gradeType1 = gradeType1;
    }

    public void setGradeType5(String gradeType5) {
        this.gradeType5 = gradeType5;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setInOut(int inOut) {
        this.inOut = inOut;
    }

    public void setGradeType(String gradeType1) {
        this.gradeType1 = gradeType1;
    }

    public String getReTypeCode() {
        return reTypeCode;
    }

    public double getPortexchangeValue() {
        return portexchangeValue;
    }

    public String getGradeType1() {
        return gradeType1;
    }

    public String getGradeType6() {
        return gradeType6;
    }

    public String getGradeType4() {
        return gradeType4;
    }

    public String getGradeType2() {
        return gradeType2;
    }

    public String getGradeType3() {
        return gradeType3;
    }

    public String getGradeType5() {
        return gradeType5;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public int getInOut() {
        return inOut;
    }

    public void parseRowStr(String sRowStr) throws YssException {

    }

    /**
     * buildRowStr：
     * 产生一行的数据
     * @return String
     */
    public String buildRowStr() throws YssException {
        return this.autoBuildRowStr(
            "Detail\tOrderKeyCode\tReTypeCode\tNAVDate\tKeyCode\tKeyName\tSEDOLCode\tISINCode\tCuryCode\tSParAmt" +
            "\tPrice\tOTPrice1\tOTPrice2\tOTPrice3\tPortCuryRate\tBookCost\tMarketValue\tPayValue\tPortBookCost\tPortPayValue" +
            "\tPortexchangeValue");
    }

    /**
     * getOperValue :
     * 获取对象中特定变量的值
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        return "";
    }

}
