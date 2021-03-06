package com.yss.main.report;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

import java.util.*;
import java.sql.*;

public class ValuationRepBean
    extends BaseReportBean implements IClientReportView, IKey {
    private String portCode;
    private java.util.Date dDate;
    private String invMgrCode; //chenyibo  20071003
    private double AccrOut; //sj 20071105 应付利息
    private double baseAccrOut;
    private double portAccrOut;
    private double fotprice1; //sj 20071109 add 其他行情1
    private double fotprice2; //其他行情2
    private double fotprice3; //其他行情3
    private String externalCode; //外部代码 sj 20071111 add
    private String ISINCode; //Insi代码 sj 20071111
    private HashMap hmFieldSec;
    private HashMap hmFieldCash;
    private String valSecDefine;
    private String valCashDefine;
    private String valInvestDefine;
    private String portCuryCode;

    private String keyCode = "";
    private String keyName = "";
    private String curyCode = "";
    private double parAmt;
    private double mktPrice;
    private double baseExchangeRate;
    private double portExchangeRate;

    private double bookCost;
    private double mValue;
    private double accrInt;
    private double gLMValue;
    private double recMoney;
    private double payMoney;

    private double baseBookCost;
    private double baseMValue;
    private double baseAccrInt;
    private double baseGLMValue;
    private double baseGLFX;
    private double baseGLFXAccrInt;
    private double baseRecMoney;
    private double basePayMoney;

    private double portBookCost;
    private double portMValue;
    private double portAccrInt;
    private double portGLMValue;
    private double portGLFX;
    private double portGLFXAccrInt;
    private double portRecMoney;
    private double portPayMoney;

    private String categoryName = "";
    private String subCategoryName = "";
    private String cusSubCategoryName = "";
    private double totalCost;
    private int extendNum;
    private String plusMark = "";
    private String space = "";
    private String sParAmt = "";

    public String getPortCode() {
        return portCode;
    }

    public void setDDate(java.util.Date dDate) {
        this.dDate = dDate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setValCashDefine(String valCashDefine) {
        this.valCashDefine = valCashDefine;
    }

    public void setValSecDefine(String valSecDefine) {
        this.valSecDefine = valSecDefine;
    }

    public void setPortAccrInt(double portAccrInt) {
        this.portAccrInt = portAccrInt;
    }

    public void setParAmt(double parAmt) {
        this.parAmt = parAmt;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public void setGLMValue(double gLMValue) {
        this.gLMValue = gLMValue;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setPortMValue(double portMValue) {
        this.portMValue = portMValue;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setBookCost(double bookCost) {
        this.bookCost = bookCost;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public void setPortBookCost(double portBookCost) {
        this.portBookCost = portBookCost;
    }

    public void setAccrInt(double accrInt) {
        this.accrInt = accrInt;
    }

    public void setMValue(double mValue) {
        this.mValue = mValue;
    }

    public void setMktPrice(double mktPrice) {
        this.mktPrice = mktPrice;
    }

    public void setPortGLMValue(double portGLMValue) {
        this.portGLMValue = portGLMValue;
    }

    public void setPortCuryCode(String portCuryCode) {
        this.portCuryCode = portCuryCode;
    }

    public void setCusSubCategoryName(String cusSubCategoryName) {
        this.cusSubCategoryName = cusSubCategoryName;
    }

    public void setPlusMark(String plusMark) {
        this.plusMark = plusMark;
    }

    public void setExtendNum(int extendNum) {
        this.extendNum = extendNum;
    }

    public void setPortGLFXAccrInt(double portGLFXAccrInt) {
        this.portGLFXAccrInt = portGLFXAccrInt;
    }

    public void setBaseGLFX(double baseGLFX) {
        this.baseGLFX = baseGLFX;
    }

    public void setBaseAccrInt(double baseAccrInt) {
        this.baseAccrInt = baseAccrInt;
    }

    public void setBaseGLFXAccrInt(double baseGLFXAccrInt) {
        this.baseGLFXAccrInt = baseGLFXAccrInt;
    }

    public void setBaseGLMValue(double baseGLMValue) {
        this.baseGLMValue = baseGLMValue;
    }

    public void setBaseMValue(double baseMValue) {
        this.baseMValue = baseMValue;
    }

    public void setBaseBookCost(double baseBookCost) {
        this.baseBookCost = baseBookCost;
    }

    public void setPortGLFX(double portGLFX) {
        this.portGLFX = portGLFX;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public void setSParAmt(String sParAmt) {
        this.sParAmt = sParAmt;
    }

    public void setBaseExchangeRate(double baseExchangeRate) {
        this.baseExchangeRate = baseExchangeRate;
    }

    public void setPortExchangeRate(double portExchangeRate) {
        this.portExchangeRate = portExchangeRate;
    }

    public void setValInvestDefine(String valInvestDefine) {
        this.valInvestDefine = valInvestDefine;
    }

    public void setPortPayMoney(double portPayMoney) {
        this.portPayMoney = portPayMoney;
    }

    public void setBasePayMoney(double basePayMoney) {
        this.basePayMoney = basePayMoney;
    }

    public void setPayMoney(double payMoney) {
        this.payMoney = payMoney;
    }

    public void setRecMoney(double recMoney) {
        this.recMoney = recMoney;
    }

    public void setBaseRecMoney(double baseRecMoney) {
        this.baseRecMoney = baseRecMoney;
    }

    public void setPortRecMoney(double portRecMoney) {
        this.portRecMoney = portRecMoney;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setPortAccrOut(double portAccrOut) {
        this.portAccrOut = portAccrOut;
    }

    public void setHmFieldSec(HashMap hmFieldSec) {
        this.hmFieldSec = hmFieldSec;
    }

    public void setHmFieldCash(HashMap hmFieldCash) {
        this.hmFieldCash = hmFieldCash;
    }

    public void setAccrOut(double AccrOut) {
        this.AccrOut = AccrOut;
    }

    public void setBaseAccrOut(double baseAccrOut) {
        this.baseAccrOut = baseAccrOut;
    }

    public void setFotprice2(double fotprice2) {
        this.fotprice2 = fotprice2;
    }

    public void setFotprice1(double fotprice1) {
        this.fotprice1 = fotprice1;
    }

    public void setFotprice3(double fotprice3) {
        this.fotprice3 = fotprice3;
    }

    public void setISINCode(String ISINCode) {
        this.ISINCode = ISINCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public java.util.Date getDDate() {
        return dDate;
    }

    public String getValCashDefine() {
        return valCashDefine;
    }

    public String getValSecDefine() {
        return valSecDefine;
    }

    public double getPortAccrInt() {
        return portAccrInt;
    }

    public double getParAmt() {
        return parAmt;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public double getGLMValue() {
        return gLMValue;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getPortMValue() {
        return portMValue;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getBookCost() {
        return bookCost;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public double getPortBookCost() {
        return portBookCost;
    }

    public double getAccrInt() {
        return accrInt;
    }

    public double getMValue() {
        return mValue;
    }

    public double getMktPrice() {
        return mktPrice;
    }

    public double getPortGLMValue() {
        return portGLMValue;
    }

    public String getPortCuryCode() {
        return portCuryCode;
    }

    public String getCusSubCategoryName() {
        return cusSubCategoryName;
    }

    public String getPlusMark() {
        return plusMark;
    }

    public int getExtendNum() {
        return extendNum;
    }

    public double getPortGLFXAccrInt() {
        return portGLFXAccrInt;
    }

    public double getBaseGLFX() {
        return baseGLFX;
    }

    public double getBaseAccrInt() {
        return baseAccrInt;
    }

    public double getBaseGLFXAccrInt() {
        return baseGLFXAccrInt;
    }

    public double getBaseGLMValue() {
        return baseGLMValue;
    }

    public double getBaseMValue() {
        return baseMValue;
    }

    public double getBaseBookCost() {
        return baseBookCost;
    }

    public double getPortGLFX() {
        return portGLFX;
    }

    public String getSpace() {
        return space;
    }

    public String getSParAmt() {
        return sParAmt;
    }

    public double getBaseExchangeRate() {
        return baseExchangeRate;
    }

    public double getPortExchangeRate() {
        return portExchangeRate;
    }

    public String getValInvestDefine() {
        return valInvestDefine;
    }

    public double getPortPayMoney() {
        return portPayMoney;
    }

    public double getBasePayMoney() {
        return basePayMoney;
    }

    public double getPayMoney() {
        return payMoney;
    }

    public double getRecMoney() {
        return recMoney;
    }

    public double getBaseRecMoney() {
        return baseRecMoney;
    }

    public double getPortRecMoney() {
        return portRecMoney;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public double getPortAccrOut() {
        return portAccrOut;
    }

    public HashMap getHmFieldSec() {
        return hmFieldSec;
    }

    public HashMap getHmFieldCash() {
        return hmFieldCash;
    }

    public double getAccrOut() {
        return AccrOut;
    }

    public double getBaseAccrOut() {
        return baseAccrOut;
    }

    public double getFotprice2() {
        return fotprice2;
    }

    public double getFotprice1() {
        return fotprice1;
    }

    public double getFotprice3() {
        return fotprice3;
    }

    public String getISINCode() {
        return ISINCode;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public ValuationRepBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        return this.autoBuildRowStr(this.getReportFields1());
    }

    public String buildTotalRowStr() throws YssException {
        return this.autoBuildRowStr(this.getReportFields2());
    }

    public String buildTotalValueRowStr() throws YssException {
        return this.autoBuildRowStr(this.getReportFields3());
    }
    
    /**
     * add by huangqirong 2012-03-01 story #2088  字段设置保留位数
     * */
    public String buildTotalValueRowStr1() throws YssException {
        return this.autoBuildRowStr(this.getReportFields4());
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     * 解析估值报表请求
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            reqAry = sRowStr.split("\t");
            this.portCode = reqAry[0];
            this.portCuryCode = reqAry[1];
            this.dDate = YssFun.toDate(reqAry[2]);
            this.invMgrCode = reqAry[3];

            this.valCashDefine = "AccType;SubAccType;Cury";
            //this.valSecDefine = "CatType;CatSubType;Cury";
            this.valSecDefine = "CatType;CatSubType;CatCusType;AttrCls;Cury"; //CatCusType为自定义品种。
            this.valInvestDefine = "PayType";
        } catch (Exception e) {
            throw new YssException(" 解析估值报表请求出错" + "\n" + e.getMessage());
        }
    }

    /**
     * getKey
     *
     * @return Object
     */
    public Object getKey() {
        return "";
    }

    /**
     * setKey
     *
     * @param obj Object
     */
    public void setKey(Object obj) {
    }

    /**
     * getReportData
     *
     * @param sReportType String
     * @return String
     */
    public String getReportData(String sReportType) throws YssException {
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        IBuildReport valuationRepOper = (IBuildReport) pub.getOperDealCtx().
            getBean("valuationrep");
        ResultSet rs = null;
        this.keyCode = "";
        this.curyCode = "";
        try {
            valuationRepOper.setYssPub(pub);
            buf.append(getReportHeaders(sReportType)).append(YssCons.
                YSS_LINESPLITMARK);
            valuationRepOper.initBuildReport(this);
            buf.append(valuationRepOper.buildReport(""));
            //valuationRepOper.saveReport("");//由新净值统计生成资产净值数据。在这里不再生成。sj edit 20080722
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("获取估值报表出错" + "\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getReportHeaders
     *
     * @param sReportType String
     * @return String
     */
    public String getReportHeaders(String sReportType) {
        String reStr = "";
        String reStrKey = "";
        // reStr = this.getReportHeaders1();
        reStrKey = this.getReportFields1();
        //reStr + YssCons.YSS_LINESPLITMARK +
        return "" + YssCons.YSS_LINESPLITMARK + reStrKey;
    }

    public void setDefaultParam() {
        keyCode = "";
        keyName = "";
        curyCode = "";
        parAmt = 0;
        mktPrice = 0;
        fotprice1 = 0; //其他行情1
        fotprice2 = 0;
        fotprice3 = 0;
        externalCode = ""; //sj 20071111 add
        ISINCode = "";
        baseExchangeRate = 0;
        portExchangeRate = 0;

        bookCost = 0;
        mValue = 0;
        accrInt = 0;
        AccrOut = 0; //temp
        gLMValue = 0;
        baseBookCost = 0;
        baseMValue = 0;
        baseAccrInt = 0;
        baseAccrOut = 0; //temp
        baseGLMValue = 0;
        baseGLFX = 0;
        baseGLFXAccrInt = 0;
        portBookCost = 0;
        portMValue = 0;
        portAccrInt = 0;
        portAccrOut = 0; //temp
        portGLMValue = 0;
        portGLFX = 0;
        portGLFXAccrInt = 0;
        recMoney = 0;
        payMoney = 0;
        baseRecMoney = 0;
        basePayMoney = 0;
        portRecMoney = 0;
        portPayMoney = 0;

        categoryName = "";
        subCategoryName = "";
        cusSubCategoryName = "";
        totalCost = 0;
        space = "";
        sParAmt = "";
    }
    
    //add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
    public String checkReportBeforeSearch(String sReportType){
    	return "";
    }

    /**shashijie 2011.04.07 STORY #805 头寸表应该预测T日到T+N-1日共N个工作日的头寸 */
	public String getSaveDefuntDay(String sRepotyType) throws YssException {
		return "";
	}
	
    public String GetBookSetName(String sPortCode) throws YssException
    {
    	return "";
    	
    }
}
