package com.yss.main.parasetting;

//QDV4赢时胜上海2009年1月14日01_B MS00188
import java.math.*;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.parasetting.FixInterest.DriftRateBean;
import com.yss.main.parasetting.FixInterest.InterestTime;
import com.yss.util.*;

/**
 * <p>Title: FixInterestBean </p>
 * <p>Description: 债券信息维护 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */

// 20130418 modified by liubo.Story #3528
// 【浮动利率】被舍弃，【计息期间设置】被放到专门的一个菜单条中设置。
// 因此在债券信息设置界面中，所有关于【浮动利率】、【计息期间设置】的逻辑，都要被删除
public class FixInterestBean
    extends BaseDataSettingBean implements
    IDataSetting {
    private String strSecurityCode = ""; //证券代码
    private String strSecurityName = ""; //证券名称
    private java.util.Date dtStartDate; //启用日期
    private String strCatCode = ""; //品种类型代码
    private String strCatName = ""; //品种类型名称
    private String strSubCatCode = ""; //品种子类型
    private String strSubCatName = ""; //品种子类型名称
    private String strCusCatCode = ""; //自定义品种类型
    private String strCusCatName = ""; //自定义品种名称
    private BigDecimal strFaceValue; //面额 //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
    private BigDecimal strFaceRate; //票面利率  bug 2381 by zhouwei 20111111  精度
    private BigDecimal dbPretaxFaceRate;//税前票面利率，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A bug 2381 by zhouwei 20111111  精度
    private double baseCPI;//基础CPI add by yanghaiming 20110209 #461 QDV4博时2010年12月29日01_A
    private BigDecimal strIssuePrice; //发行价格 修改字段精度 QDV4赢时胜上海2009年1月14日01_B MS00188
    private java.util.Date dtIssueDate; //发行日期
    private java.util.Date dtInsStartDate; //计息起始日
    private java.util.Date dtInsEndDate; //计息截止日
    private java.util.Date dtInsCashDate; //兑付日期
    private BigDecimal strInsFrequency; //付息频率  bug 2381 by zhouwei 20111111  精度
    private String strQuoteWay = ""; //报价方式
//----------------------2007.11.12 添加 蒋锦--------------------------//
    private String strCalcInsMeticDay = ""; //每日利息算法
    private String strCalcInsMeticBuy = ""; //买入利息算法
    private String strCalcInsMeticSell = ""; //卖出利息算法
//-------------------------------------------------------------------//
//----------------------2008.07.22 添加 蒋锦--------------------------//
    private java.util.Date dtThisInsStartDate; //当前计息期间计息起始日
    private java.util.Date dtThisInsEndDate; //当前计息期间计息截止日
//-------------------------------------------------------------------//
    private String strCalcInsMeticDayName = "";
    private String strCalcInsMeticBuyName = "";
    private String strCalcInsMeticSellName = "";

    //------------------------------------------------------- lzp  add 2007 12.13
    private String strCalcPriceCode = ""; //估值价算法
    private String strCalcPriceName = "";
    //------------------------------------------------------

    private String strCalcInsWay = ""; //计息方法
    private String strInsOrigin = ""; //利息来源
    private String strPerExpCode = ""; //利息公式代码
    private String strPerExpName = ""; //利息公式名称
    private String strPeriodCode = ""; //期间代码
    private String strPeriodName = ""; //期间名称
    private String strRoundCode = ""; //舍入设置代码
    private String strRoundName = ""; //舍入设置名称
    private String strDesc = ""; //描述
    private String strCreditLevel = ""; //信用评级
    private String sMutilStr = ""; //保存多条记录操作的字符串;
    
    /**shashijie 2012-01-05 STORY 1713 */
    private String DriftRateStr = "";//利率设置
    private String InterestTimeStr = "";//利率期间
    private String FTaskMoneyCode = "";//百元派息金额
    private String FTaskMoneyName = "";//百元派息金额名称
    /**end*/
    
    private java.util.Date dtOldStartDate;
    private String strOldSecurityCode = "";
    private FixInterestBean filterType;
    private String sRecycled = "";

    private String strIsOnlyColumns = "0"; //在查询时只显示列，不查询数据

//------------为了在债券计息时获取按此条件获取值.sj edit 20080618 -------
    private String sPortCodes = "";
    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090515
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    //------------------------------------------------------------------------------------
    private java.util.Date dStartDate;
    private java.util.Date dEndDate;
    //==================合中保的版本
    private double dFactRate = 0; //新增实际利率 by leeyu 080808
    private String sAmortizationCode = ""; //摊销溢价
    private String sAmortizationName = "";
    //============= by leeyu
    //-------------------------------------------------------
    private String checkAccLinks = ""; //批量审核 MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩
    private String sShowType="";//QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315 合并太平版本代码
    private String strFvalueDates="";// add by wuweiqi 20110223 添加起息日期
    
    private String sInterTaxPeriodCode = "";
    private String sInterTaxPeriodName = "";
    //--- add by songjie 2013.03.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
    private String exchangeCode = "";//交易所代码
    private String beanId = "";
    private SecurityBean security = null;
    private String formula = "";//计息公式
    private HashMap hmBondDate = null;
    private PeriodBean period = null;//期间设置
    public String getExchangeCode(){
    	return this.exchangeCode;
    }
    public void setExchangeCode(String exchangeCode){
    	this.exchangeCode = exchangeCode;
    }
    public void setStrCalcInsMeticDay(String strCalcInsMeticDay){
    	this.strCalcInsMeticDay = strCalcInsMeticDay;
    }
    public void setStrCalcInsMeticBuy(String strCalcInsMeticBuy){
    	this.strCalcInsMeticBuy = strCalcInsMeticBuy;
    }
    public void setStrCalcInsMeticSell(String strCalcInsMeticSell){
    	this.strCalcInsMeticSell = strCalcInsMeticSell;
    }
    public String getBeanId(){
    	return this.beanId;
    }
    public void setBeanId(String beanId){
    	this.beanId = beanId;
    }
    public SecurityBean getSecurity(){
    	return security;
    }
    public void setSecurity(SecurityBean security){
    	this.security = security;
    }
    public String getFormula(){
    	return this.formula;
    }
    public void setFormula(String formula){
    	this.formula = formula;
    }
    public HashMap getHmBondDate(){
    	return this.hmBondDate;
    }
    public void setHmBondDate(HashMap hmBondDate){
    	this.hmBondDate = hmBondDate;
    }
    
    public PeriodBean getPeriod(){
    	return this.period;
    }
    public void setPeriod(PeriodBean period){
    	this.period = period;
    }
    //--- add by songjie 2013.03.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
    
    public String getInterTaxPeriodCode() {
		return sInterTaxPeriodCode;
	}

	public void setInterTaxPeriodCode(String sInterTaxPeriodCode) {
		this.sInterTaxPeriodCode = sInterTaxPeriodCode;
	}
    
    public String getsInterTaxPeriodName() {
		return sInterTaxPeriodName;
	}

	public void setInterTaxPeriodName(String sInterTaxPeriodName) {
		this.sInterTaxPeriodName = sInterTaxPeriodName;
	}
    
    public String getStrFvalueDates() {
		return strFvalueDates;
	}

	public void setStrFvalueDates(String strFvalueDates) {
		this.strFvalueDates = strFvalueDates;
	}

	public String getsShowType() {
		return sShowType;
	}

	public void setsShowType(String sShowType) {
		this.sShowType = sShowType;
	}
    //MS00179 QDV4建行2009年1月07日01_B 2009.02.17 方浩
    public void setCheckAccLinks(String checkAccLinks) {
        this.checkAccLinks = checkAccLinks;
    }

    public String getCheckAccLinks() {
        return checkAccLinks;
    }

    //---------------------------------------------------------------------

    public void setstrCreditLevel(String strCreditLevel) {
        this.strCreditLevel = strCreditLevel;
    }

    public void setFilterType(FixInterestBean filterType) {
        this.filterType = filterType;
    }

    public void setStrQuoteWay(String strQuoteWay) {
        this.strQuoteWay = strQuoteWay;
    }

    public void setStrCusCatCode(String strCusCatCode) {
        this.strCusCatCode = strCusCatCode;
    }

    public void setStrPeriodName(String strPeriodName) {
        this.strPeriodName = strPeriodName;
    }

    public void setStrRoundCode(String strRoundCode) {
        this.strRoundCode = strRoundCode;
    }

    public void setStrPerExpCode(String strPerExpCode) {
        this.strPerExpCode = strPerExpCode;
    }

    public void setStrRoundName(String strRoundName) {
        this.strRoundName = strRoundName;
    }
    //bug 2381 by zhouwei 20111111  精度
    public void setStrFaceRate(BigDecimal strFaceRate) {
        this.strFaceRate = strFaceRate;
    }
    
    public void setDbPretaxFaceRate(BigDecimal dbPretaxFaceRate) {
        this.dbPretaxFaceRate = dbPretaxFaceRate;
    }
    
    public void setStrSubCatName(String strSubCatName) {
        this.strSubCatName = strSubCatName;
    }

    public void setStrCalcInsWay(String strCalcInsWay) {
        this.strCalcInsWay = strCalcInsWay;
    }

    public void setDtInsEndDate(java.util.Date dtInsEndDate) {
        this.dtInsEndDate = dtInsEndDate;
    }

    public void setStrIssuePrice(BigDecimal strIssuePrice) { //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
        this.strIssuePrice = strIssuePrice;
    }

    public void setStrCatCode(String strCatCode) {
        this.strCatCode = strCatCode;
    }

    public void setStrPerExpName(String strPerExpName) {
        this.strPerExpName = strPerExpName;
    }

    public void setStrCusCatName(String strCusCatName) {
        this.strCusCatName = strCusCatName;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setDtInsCashDate(java.util.Date dtInsCashDate) {
        this.dtInsCashDate = dtInsCashDate;
    }

    public void setStrPeriodCode(String strPeriodCode) {
        this.strPeriodCode = strPeriodCode;
    }

    public void setStrSubCatCode(String strSubCatCode) {
        this.strSubCatCode = strSubCatCode;
    }

    public void setDtIssueDate(java.util.Date dtIssueDate) {
        this.dtIssueDate = dtIssueDate;
    }
  //bug 2381 by zhouwei 20111111  精度
    public void setStrInsFrequency(BigDecimal strInsFrequency) {
        this.strInsFrequency = strInsFrequency;
    }

    public void setStrCatName(String strCatName) {
        this.strCatName = strCatName;
    }

    public void setStrFaceValue(BigDecimal strFaceValue) { //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
        this.strFaceValue = strFaceValue;
    }

    public void setStrOldSecurityCode(String strOldSecurityCode) {
        this.strOldSecurityCode = strOldSecurityCode;
    }

    public void setStrInsOrigin(String strInsOrigin) {
        this.strInsOrigin = strInsOrigin;
    }

    public void setDtStartDate(java.util.Date dtStartDate) {
        this.dtStartDate = dtStartDate;
    }

    public void setStrCalcInsMeticSellName(String strCalcInsMeticSellName) {
        this.strCalcInsMeticSellName = strCalcInsMeticSellName;
    }

    public void setStrCalcInsMeticDayName(String strCalcInsMeticDayName) {
        this.strCalcInsMeticDayName = strCalcInsMeticDayName;
    }

    public void setStrCalcInsMeticBuyName(String strCalcInsMeticBuyName) {
        this.strCalcInsMeticBuyName = strCalcInsMeticBuyName;
    }

    public String getstrCreditLevel() {
        return strCreditLevel;
    }

    public FixInterestBean getFilterType() {
        return filterType;
    }

    public String getStrQuoteWay() {
        return strQuoteWay;
    }

    public String getStrCusCatCode() {
        return strCusCatCode;
    }

    public String getStrPeriodName() {
        return strPeriodName;
    }

    public String getStrRoundCode() {
        return strRoundCode;
    }

    public String getStrPerExpCode() {
        return strPerExpCode;
    }

    public String getStrRoundName() {
        return strRoundName;
    }
  //bug 2381 by zhouwei 20111111  精度
    public BigDecimal getStrFaceRate() {
        return strFaceRate;
    }
  //bug 2381 by zhouwei 20111111  精度
    public BigDecimal getDbPretaxFaceRate() {
        return dbPretaxFaceRate;
    }

    public String getStrSubCatName() {
        return strSubCatName;
    }

    public String getStrCalcInsWay() {
        return strCalcInsWay;
    }

    public java.util.Date getDtInsEndDate() {
        return dtInsEndDate;
    }

    public String getStrCalcInsMeticSell() {
        return strCalcInsMeticSell;
    }

    public BigDecimal getStrIssuePrice() { //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
        return strIssuePrice;
    }

    public String getStrCalcInsMeticDay() {
        return strCalcInsMeticDay;
    }

    public String getStrCatCode() {
        return strCatCode;
    }

    public String getStrPerExpName() {
        return strPerExpName;
    }

    public String getStrCusCatName() {
        return strCusCatName;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    public String getStrSecurityName() {
        return strSecurityName;
    }

    public String getStrCalcInsMeticBuy() {
        return strCalcInsMeticBuy;
    }

    public java.util.Date getDtInsCashDate() {
        return dtInsCashDate;
    }

    public String getStrPeriodCode() {
        return strPeriodCode;
    }

    public String getStrSubCatCode() {
        return strSubCatCode;
    }

    public java.util.Date getDtIssueDate() {
        return dtIssueDate;
    }
  //bug 2381 by zhouwei 20111111  精度
    public BigDecimal getStrInsFrequency() {
        return strInsFrequency;
    }

    public String getStrCatName() {
        return strCatName;
    }

    public BigDecimal getStrFaceValue() { //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
        return strFaceValue;
    }

    public String getStrOldSecurityCode() {
        return strOldSecurityCode;
    }

    public String getStrInsOrigin() {
        return strInsOrigin;
    }

    public java.util.Date getDtStartDate() {
        return dtStartDate;
    }

    public String getStrCalcInsMeticSellName() {
        return strCalcInsMeticSellName;
    }

    public String getStrCalcInsMeticDayName() {
        return strCalcInsMeticDayName;
    }

    public String getStrCalcInsMeticBuyName() {
        return strCalcInsMeticBuyName;
    }

    public java.util.Date getDtInsStartDate() {
        return dtInsStartDate;
    }

    public Date getDtThisInsEndDate() {
        return this.dtThisInsEndDate;
    }

    public Date getDtThisInsStartDate() {
        return dtThisInsStartDate;
    }

    public void setDtInsStartDate(java.util.Date dtInsStartDate) {
        this.dtInsStartDate = dtInsStartDate;
    }

    public void setDtThisInsEndDate(Date dtThisInsEndDate) {
        this.dtThisInsEndDate = dtThisInsEndDate;
    }

    public void setDtThisInsStartDate(Date dtThisInsStartDate) {
        this.dtThisInsStartDate = dtThisInsStartDate;
    }

    public double getBaseCPI() {
		return baseCPI;
	}

	public void setBaseCPI(double baseCPI) {
		this.baseCPI = baseCPI;
	}

	public FixInterestBean() {
    }

    /**
     * parseRowStr
     * 解析证券信息维护请求
     * 修改日期：2007-11-12
     * 蒋锦 添加字段
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                
                /**shashijie 2012-01-05 STORY 1713 */
                String[] moder = sRowStr.split("\r\t");
                if (moder.length >= 3) {
                    this.sMutilStr = moder[2];//信用评级
                }
                /**end*/
                
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.strSecurityCode = reqAry[0];
            this.strSecurityName = reqAry[1];
            this.dtStartDate = YssFun.toDate(reqAry[2]);
            this.strCatCode = reqAry[3];
            this.strCatName = reqAry[4];
            this.strSubCatCode = reqAry[5];
            this.strSubCatName = reqAry[6];
            this.strCusCatCode = reqAry[7];
            this.strCusCatName = reqAry[8];
            if (YssFun.isNumeric(reqAry[9])) {
                this.strFaceValue = new BigDecimal(reqAry[9]); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
            }
            if (YssFun.isNumeric(reqAry[10])) {
                this.strFaceRate = new BigDecimal(reqAry[10]);//bug 2381 by zhouwei 20111111  精度
            }
            if (YssFun.isNumeric(reqAry[11])) {
                this.strIssuePrice = new BigDecimal(reqAry[11]); //Double.parseDouble(reqAry[11]);//修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
            }
            this.dtIssueDate = YssFun.toDate(reqAry[12]);
            this.dtInsStartDate = YssFun.toDate(reqAry[13]);
            this.dtInsEndDate = YssFun.toDate(reqAry[14]);
            this.dtInsCashDate = YssFun.toDate(reqAry[15]);
            if (YssFun.isNumeric(reqAry[16])) {
                this.strInsFrequency = new BigDecimal(reqAry[16]);//bug 2381 by zhouwei 20111111  精度
            }
            this.strQuoteWay = reqAry[17];
            //-------------------------2007.11.12 添加 蒋锦---------------------------//
            this.strCalcInsMeticDay = reqAry[18];
            this.strCalcInsMeticBuy = reqAry[19];
            this.strCalcInsMeticSell = reqAry[20];
            //-----------------------------------------------------------------------//
            this.strCalcPriceCode = reqAry[21]; // lzp  add
            this.strCalcInsWay = reqAry[22];
            this.strInsOrigin = reqAry[23];
            this.strPerExpCode = reqAry[24];
            this.strPerExpName = reqAry[25];
            this.strPeriodCode = reqAry[26];
            this.strPeriodName = reqAry[27];
            this.strRoundCode = reqAry[28];
            this.strRoundName = reqAry[29];
            //---edit by songjie 2011.06.16 BUG 2004 QDV4赢时胜(测试)2011年5月30日02_B---//
			if (reqAry[30].indexOf("【Enter】") > -1) {
				this.strDesc = reqAry[30].replaceAll("【Enter】", "\r\n");
			} else {
				this.strDesc = reqAry[30];
			}
            //---edit by songjie 2011.06.16 BUG 2004 QDV4赢时胜(测试)2011年5月30日02_B---//
            this.checkStateId = Integer.parseInt(reqAry[31]);
            this.strIsOnlyColumns = reqAry[32];
            this.strOldSecurityCode = reqAry[33];
            this.dtOldStartDate = YssFun.toDate(reqAry[34]);
            this.sPortCodes = reqAry[35];
            if (YssFun.isDate(reqAry[36])) {
                if (!reqAry[36].equalsIgnoreCase("0001-01-01")) {
                    this.dStartDate = YssFun.parseDate(reqAry[36]);
                }
            }
            if (YssFun.isDate(reqAry[37])) {
                if (!reqAry[37].equalsIgnoreCase("0001-01-01")) {
                    this.dEndDate = YssFun.parseDate(reqAry[37]);
                }
            }
            //==================合中保的版本
            if (YssFun.isNumeric(reqAry[38])) {
                this.dFactRate = YssFun.toDouble(reqAry[38]);
            } // add by leeyu 080808
            this.sAmortizationCode = reqAry[39]; // add by leeyu
            //==============by leeyu
            //-----------------------------------------------------------------------------------------
            //MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩
            this.checkAccLinks = reqAry[40]; //如果是审核和反审核，则把所有的信息都放入了数组的40位
            this.checkAccLinks = this.checkAccLinks.replaceAll("\f", "\t"); //为了便于使用通用的解析过程
            this.sShowType = reqAry[41];//QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
            //--------------------------------------------------------------------------------------------
            // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090515
            this.assetGroupCode = reqAry[42];
            this.assetGroupName = reqAry[43];
            //---------------------------------------------------------------------------------------------
            if(YssFun.isNumeric(reqAry[44])) {//税前票面利率，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                this.dbPretaxFaceRate = new BigDecimal(reqAry[44]);//bug 2381 by zhouwei 20111111  精度
            }
            if(YssFun.isNumeric(reqAry[45])) {//基础CPI add by yanghaiming 20110209 #461 QDV4博时2010年12月29日01_A
                this.baseCPI = YssFun.toDouble(reqAry[45]);
            }
            if(YssFun.isNumeric(reqAry[46])) {
                this.strFvalueDates = reqAry[46];
            }
            
            /**shashijie 2012-1-18 STORY 1713 */
            this.FTaskMoneyCode = reqAry[47];
			/**end*/
            
            this.sInterTaxPeriodCode = reqAry[48];
            this.sInterTaxPeriodName = reqAry[49];
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new FixInterestBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析债券信息出错", e);
        }
    }

    /**
     * buildRowStr
     * 获取数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strSecurityCode);
        buf.append("\t");
        buf.append(this.strSecurityName);
        buf.append("\t");
        buf.append(YssFun.formatDate(this.dtStartDate, YssCons.YSS_DATEFORMAT));
        buf.append("\t");
        buf.append(this.strCatCode);
        buf.append("\t");
        buf.append(this.strCatName);
        buf.append("\t");
        buf.append(this.strSubCatCode);
        buf.append("\t");
        buf.append(this.strSubCatName);
        buf.append("\t");
        buf.append(this.strCusCatCode);
        buf.append("\t");
        buf.append(this.strCusCatName);
        buf.append("\t");
        buf.append(this.strFaceValue);
        buf.append("\t");
        buf.append(this.strFaceRate);
        buf.append("\t");
        buf.append(this.strIssuePrice);
        buf.append("\t");
        buf.append(YssFun.formatDate(this.dtIssueDate, YssCons.YSS_DATEFORMAT));
        buf.append("\t");
        buf.append(YssFun.formatDate(this.dtInsStartDate,
                                     YssCons.YSS_DATEFORMAT));
        buf.append("\t");
        buf.append(YssFun.formatDate(this.dtInsEndDate, YssCons.YSS_DATEFORMAT));
        buf.append("\t");
        buf.append(YssFun.formatDate(this.dtInsCashDate, YssCons.YSS_DATEFORMAT));
        buf.append("\t");
        buf.append(this.strInsFrequency);
        buf.append("\t");
        buf.append(this.strQuoteWay);
        buf.append("\t");
        //-------------------------2007.11.12 添加 蒋锦-------------------------//
        buf.append(this.strCalcInsMeticDay);
        buf.append("\t");
        buf.append(this.strCalcInsMeticBuy);
        buf.append("\t");
        buf.append(this.strCalcInsMeticSell);
        buf.append("\t");
        //---------------------------------------------------------------------//
        buf.append(this.strCalcInsWay);
        buf.append("\t");
        buf.append(this.strInsOrigin);
        buf.append("\t");
        buf.append(this.strPerExpCode);
        buf.append("\t");
        buf.append(this.strPerExpName);
        buf.append("\t");
        buf.append(this.strPeriodCode);
        buf.append("\t");
        buf.append(this.strPeriodName);
        buf.append("\t");
        buf.append(this.strRoundCode);
        buf.append("\t");
        buf.append(this.strRoundName);
        buf.append("\t");
        buf.append(this.strDesc);
        buf.append("\t");
        buf.append(this.strCreditLevel);
        buf.append("\t");
        buf.append(this.strCalcInsMeticBuyName);
        buf.append("\t");
        buf.append(this.strCalcInsMeticDayName);
        buf.append("\t");
        buf.append(this.strCalcInsMeticSellName);
        buf.append("\t");
        //--------------- lzp add
        buf.append(this.strCalcPriceCode);
        buf.append("\t");
        buf.append(this.strCalcPriceName);
        buf.append("\t");
        //========================合中保的版本时用的
        buf.append(this.dFactRate).append("\t"); // add by leeyu 080808
        buf.append(this.sAmortizationCode).append("\t"); // add by leeyu
        buf.append(this.sAmortizationName).append("\t"); // add by leeyu
        //========================by leeyu
        // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090515
        buf.append(this.assetGroupCode).append("\t");
        buf.append(this.assetGroupName).append("\t");
        //---------------------------------------------------------------------------------------------
        buf.append(this.dbPretaxFaceRate).append("\t");//税前票面利率，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
        buf.append(this.baseCPI).append("\t");//基础CPI add by yanghaiming 20110209 #461 QDV4博时2010年12月29日01_A
        buf.append(this.strFvalueDates).append("\t"); // wuweiqi
        
        /**shashijie 2012-1-18 STORY 1713 */
		buf.append(this.FTaskMoneyCode).append("\t");
		buf.append(this.FTaskMoneyName).append("\t");
		/**end*/
		buf.append(this.sInterTaxPeriodCode).append("\t");
		buf.append(this.sInterTaxPeriodName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查证券信息维护数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Para_FixInterest"),
                               "FSecurityCode,FStartDate",
                               this.strSecurityCode + "," +
                               YssFun.formatDate(this.dtStartDate),
                               this.strOldSecurityCode + "," +
                               YssFun.formatDate(this.dtOldStartDate));

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            //20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
            //===================================
    		if(pub.isBrown()==true)
    			return " where 1=1";
            //==============end=====================
            if (this.filterType != null) {
                //sResult = " where 1=1 ";
            	 if (this.filterType.strIsOnlyColumns.equalsIgnoreCase("1")  && pub.isBrown()==false) {	//20111027 modified by liubo.STORY #1285.
                    sResult = " where 1=2 ";
                    return sResult;
                }
                //add by yanghaiming 20100930 如果 strIsOnlyColumns 为“0”时则需要查询出数据，将上面的“where 1 = 1” 放到这里来增加
                else if (this.filterType.strIsOnlyColumns.equalsIgnoreCase("0")){
                	sResult = " where 1=1 ";
                }
                //add by yanghaiming 20100930 如果 strIsOnlyColumns 为“0”时则需要查询出数据，将上面的“where 1 = 1” 放到这里来增加
                if (this.filterType.checkStateId == 1) {
                    sResult = sResult + " and a.FCheckState = 1 ";
                }
                
                //------ modify by wangzuochun 2010.11.24 QDV4太平2010年11月24日01_B
                if (this.filterType.strSecurityCode.length() != 0) {
                    sResult = sResult + " and a.FSecurityCode = " +
                    		  dbl.sqlString(filterType.strSecurityCode);
                }
                //--------------------- QDV4太平2010年11月24日01_B -----------------//
                if (filterType.strIssuePrice != null &&
                    filterType.strIssuePrice.compareTo(new BigDecimal(0)) > 0) { //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
                    sResult = sResult + " and a.FIssuePrice =" +
                        filterType.strIssuePrice;
                }
                if (this.filterType.dtIssueDate != null &&
                    !this.filterType.dtIssueDate.equals(YssFun.toDate(
                        "1900-01-01"))) {
                    sResult = sResult + " and a.FIssueDate =" +
                        dbl.sqlDate(filterType.dtIssueDate);
                }
                if (this.filterType.dtInsStartDate != null &&
                    !this.filterType.dtInsStartDate.equals(YssFun.toDate(
                        "1900-01-01"))) {
                    sResult = sResult + " and a.FInsStartDate =" +
                        dbl.sqlDate(filterType.dtInsStartDate);
                }
                if (this.filterType.dtInsEndDate != null &&
                    !this.filterType.dtInsEndDate.equals(YssFun.toDate(
                        "1900-01-01"))) {
                    sResult = sResult + " and a.FInsEndDate =" +
                        dbl.sqlDate(filterType.dtInsEndDate);
                }
                if (this.filterType.dtInsCashDate != null &&
                    !this.filterType.dtInsCashDate.equals(YssFun.toDate(
                        "1900-01-01"))) {
                    sResult = sResult + " and a.FInsCashDate =" +
                        dbl.sqlDate(filterType.dtInsCashDate);
                }
                if (this.filterType.dtStartDate != null &&
                    !this.filterType.dtStartDate.equals(YssFun.toDate(
                        "9998-12-31")) &&
                    !this.filterType.dtStartDate.equals(YssFun.toDate(
                        "1900-01-01"))) {
                    sResult = sResult + " and a.FStartDate <= " +
                        dbl.sqlDate(filterType.dtStartDate);
                }
                if (filterType.strFaceValue != null &&
                    filterType.strFaceValue.compareTo(new BigDecimal(0)) > 0) { //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
                    sResult = sResult + " and a.FFaceValue = " +
                        filterType.strFaceValue;
                }
                if (this.filterType.strInsFrequency!=null && this.filterType.strInsFrequency .compareTo(new BigDecimal(0))> 0) {//bug 2381 by zhouwei 20111111  精度
                    sResult = sResult + " and a.FInsFrequency =" +
                        filterType.strInsFrequency;
                }
                if (this.filterType.strPerExpCode.length() != 0) {
                    sResult = sResult + " and a.FPerExpCode like '" +
                        filterType.strPerExpCode.replaceAll("'", "''") +
                        "%'";
                }
                if (!this.filterType.strCreditLevel.equalsIgnoreCase("99") &&
                    this.filterType.strCreditLevel.length() != 0) {
                    sResult = sResult + " and a.FCreditLevel like '" +
                        filterType.strCreditLevel.replaceAll("'", "''") +
                        "%'";
                }

                if (!this.filterType.strInsOrigin.equalsIgnoreCase("99") &&
                    this.filterType.strInsOrigin.length() != 0) {
                    sResult = sResult + " and a.FInterestOrigin = " +
                        this.filterType.strInsOrigin;
                }
                if (!this.filterType.strCalcInsWay.equalsIgnoreCase("99") &&
                    this.filterType.strCalcInsWay.length() != 0) {
                    sResult = sResult + " and a.FCALCINSWAY = " +
                        this.filterType.strCalcInsWay;
                }
                if (!this.filterType.strQuoteWay.equalsIgnoreCase("99") &&
                    this.filterType.strQuoteWay.length() != 0) {
                    sResult = sResult + " and a.FQuoteWay = " +
                        this.filterType.strQuoteWay;
                }
                //----------------------2007.11.12 添加 蒋锦-----------------------------//
                if (this.filterType.strCalcInsMeticDay.length() != 0) {
                    sResult = sResult + " and a.FCalcInsMeticDay Like '" +
                        this.filterType.strCalcInsMeticDay.replaceAll("'",
                        "''") + "%'";
                }
                if (this.filterType.strCalcInsMeticBuy.length() != 0) {
                    sResult = sResult + " and a.FCalcInsMeticBuy Like '" +
                        this.filterType.strCalcInsMeticBuy.replaceAll("'",
                        "''") + "%'";
                }
                if (this.filterType.strCalcInsMeticSell.length() != 0) {
                    sResult = sResult + " and a.FCalcInsMeticSell Like '" +
                        this.filterType.strCalcInsMeticSell.replaceAll(
                            "'", "''") + "%'";
                }
                //---------------------------------------------------------------------lzp  add
                if (this.filterType.strCalcPriceCode.length() != 0) {
                    sResult = sResult + " and a.FCalcPriceMetic Like '" +
                        this.filterType.strCalcPriceCode.replaceAll("'",
                        "''") + "%'";
                }
                //------------------------------
                if (this.filterType.strPerExpCode.length() != 0) {
                    sResult = sResult + " and a.FPerExpCode Like '" +
                        this.filterType.strPerExpCode.replaceAll("'",
                        "''") + "%'";
                }
                if (this.filterType.strPeriodCode.length() != 0) {
                    sResult = sResult + " and a.FPeriodCode Like '" +
                        this.filterType.strPeriodCode.replaceAll("'",
                        "''") + "%'";
                }
                if (this.filterType.strRoundCode.length() != 0) {
                    sResult = sResult + " and a.FRoundCode like '" +
                        filterType.strRoundCode.replaceAll("'", "''") +
                        "%'";
                }
                if (this.filterType.strDesc.length() != 0) {
                    sResult = sResult + " and a.FDesc like '" +
                        filterType.strDesc.replaceAll("'", "''") + "%'";
                }
                //----------------------------2010.09.14 添加 仇旭峰---------------------------------------//
                if (this.filterType.strCatCode.length() != 0) {
                	//edit by songjie 2011.06.16 查询报未明确到列
                	sResult = sResult + " and f.FCatCode like '" +
                		filterType.strCatCode.replaceAll("'", "''") +
                		"%'";
                }
                if (this.filterType.strSubCatCode.length() != 0) {
                	//edit by songjie 2011.06.16 查询报未明确到列
                	sResult = sResult + " and f.FSubCatCode like '" +
                    	filterType.strSubCatCode.replaceAll("'", "''") +
                    	"%'";
                }
                /**shashijie 2012-1-18 STORY 1713 */
                if (this.filterType.FTaskMoneyCode.length() != 0) {
                    sResult = sResult + " and a.FTaskMoneyCode Like '" +
                        this.filterType.FTaskMoneyCode.replaceAll("'",
                        "''") + "%'";
                }
				/**end*/
            }
        } catch (Exception e) {
            throw new YssException("筛选债券信息数据出错", e);
        }
        return sResult;
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_FIX_INTORG + "," +
                                        YssCons.YSS_FIX_CALCINSWAY + "," +
                                        YssCons.YSS_FIX_QUOTEWAY + "," +
                                        YssCons.YSS_FIX_Level);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取债券信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取债券信息
     * 此方法已被修改
     * 修改时间：2008年2月25号
     * 修改人：单亮
     * 原方法的功能：查询出债券信息数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        
        //废代码，注释掉！！！ by yanghaiming 20100930
        //-----MS01707 QDV4赢时胜(测试)2010年09月07日01_AB 2010.09.15 仇旭峰
        //债券信息设置界面增加证券代码，品种类型和品种子类型的查询条件
//    	String sHeader = "";//表头
//    	String sShowDataStr = "";
//    	String sAllDataStr = "";
//        sHeader = this.getListView1Headers();//获取表头名称
//        if (this.strIsOnlyColumns.equalsIgnoreCase("1")) {//strIsOnlyColumns为 1 时只加载表头数据
//            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr+ "\r\f" +
//            this.getListView1ShowCols();
//        }
        //--------------------------------------------------------------------
      //废代码，注释掉！！！ by yanghaiming 20100930
        strSql = "select y.* from " +
            " (select FSecurityCode,FCheckState,max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_FixInterest") +
            //--- MS00378 QDV4中保2009年04月10日02_B -获取所有的债券信息 -----------
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            //------------------------------------------------------------------
            //修改前的代码
            //"and FCheckState <> 2 group by FSecurityCode,FCheckState) x join" +
            //修改后的代码
            //----------------------------begin
            " group by FSecurityCode,FCheckState) x join" +
            //----------------------------end
            " (select t.Fcimname As FTaskMoneyName ,a.*, l.FVocName as FQuoteWayValue, m.FVocName as FCalcInsWayValue, n.FVocName as FInterestOriginValue," +
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, f.FSecurityName, f.FCatCode, bb.FCatName,f.FSubCatCode," +
            " cc.FSubCatName, f.FCusCatCode,dd.FCusCatName, g.FRoundName ,h.FPeriodName, i.FFormulaName " +
            ",o.FCIMName as FCalcInsMeticDayName,p.FCIMName as FCalcInsMeticBuyName,q.FCIMName as FCalcInsMeticSellName,r.FCIMName as FCalcPriceName,s.FCIMName as FAmortizationName ," + //合中保的版本时用的
            "  w.FFormulaName as fInterTaxperexpName  from " + pub.yssGetTableName("Tb_Para_FixInterest") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select d.* from " +
            pub.yssGetTableName("Tb_Para_Security") + " d join (" +
            " select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            " where " +
            //--- MS00378 QDV4中保2009年04月10日02_B -获取所有的证券信息 -----------
//            " FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//            " and " +
            //------------------------------------------------------------------
            " FCatCode = 'FI' group by FSecurityCode ) e on d.FSecurityCode = e.FSecurityCode and d.FStartDate = e.FStartDate " +
            ") f on a.FSecurityCode = f.FSecurityCode " +
            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1  and FCatCode = 'FI') bb on bb.FCatCode = f.FCatCode " +
            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1 and FCatCode = 'FI') cc on cc.FSubCatCode = f.FSubCatCode " +
            " left join (select FCusCatCode,FCusCatName from " +
            pub.yssGetTableName("Tb_Para_CustomCategory") +
            " where FCheckState = 1) dd on dd.FCusCatCode = f.FCusCatCode " +
            " left join (select FPeriodCode,FPeriodName from " +
            pub.yssGetTableName("Tb_Para_Period") +
            " where FCheckState = 1) h on a.FPeriodCode = h.FPeriodCode " +
            " left join (select FFormulaCode, FFormulaName from " +
            pub.yssGetTableName("Tb_Para_Performula") +
            " where FCheckState = 1) i on a.FPerExpCode = i.FFormulaCode" +
            " left join (select FRoundCode,FRoundName from " +
            pub.yssGetTableName("Tb_Para_Rounding") +
            " where FCheckState = 1) g on a.FRoundCode = g.FRoundCode" +
            //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FQuoteWay"，否则在使用DB2数据库时会报数据类型错误
            " left join Tb_Fun_Vocabulary l on " +
            dbl.sqlToChar("a.FQuoteWay") +
            " = l.FVocCode and l.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_FIX_QUOTEWAY) +
            //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FCalcInsWay"，否则在使用DB2数据库时会报数据类型错误
            " left join Tb_Fun_Vocabulary m on " +
            dbl.sqlToChar("a.FCalcInsWay") +
            " = m.FVocCode and m.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_FIX_CALCINSWAY) +
            //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FInterestOrigin"，否则在使用DB2数据库时会报数据类型错误
            " left join Tb_Fun_Vocabulary n on " +
            dbl.sqlToChar("a.FInterestOrigin") +
            " = n.FVocCode and n.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_FIX_INTORG) +
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) o on a.FCalcInsMeticDay = o.FCIMCode " + //sj 20071207 add
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) p on a.FCalcInsMeticBuy = p.FCIMCode " + //前台没有显示方法名称
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) q on a.FCalcInsMeticSell = q.FCIMCode " +
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) r on a.FCalcPriceMetic = r.FCIMCode " + // lzp  add  2007 12.13
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) s on a.FAmortization = s.FCIMCode " + // add by leeyu　合中保的版本时用的
            /**shashijie 2012-2-9 STORY 1713 */
            " Left Join (Select Fcimcode, Fcimname From Tb_Base_Calcinsmetic) t On a.FTaskMoneyCode = t.Fcimcode " + //百元派息公式名称
			/**end*/
			   " left join (select FFormulaCode,FFormulaName from " +
            pub.yssGetTableName("Tb_Para_Performula") +
            " where FCheckState=1) w on a.fInterTaxperexpcode=w.FFormulaCode" +
            buildFilterSql() +
            ") y on x.FSecurityCode = y.FSecurityCode and x.FStartDate = y.FStartDate" +
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     * 获取现金帐户设置的全部数据
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql = "select t.Fcimname As FTaskMoneyName ,a.*, l.FVocName as FQuoteWayValue, m.FVocName as FCalcInsWayValue, n.FVocName as FInterestOriginValue," +
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, f.FSecurityName, f.FCatCode, bb.FCatName,f.FSubCatCode," +
            " cc.FSubCatName, f.FCusCatCode,dd.FCusCatName, g.FRoundName ,h.FPeriodName, i.FFormulaName " +
            ",o.FCIMName as FCalcInsMeticDayName,p.FCIMName as FCalcInsMeticBuyName,q.FCIMName as FCalcInsMeticSellName,r.FCIMName as FCalcPriceName,s.FCIMName as FAmortizationName  " + // by leeyu合中保的版本时
            " from " + pub.yssGetTableName("Tb_Para_FixInterest") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select d.* from " +
            pub.yssGetTableName("Tb_Para_Security") + " d join (" +
            " select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCatCode = 'FI' group by FSecurityCode ) e on d.FSecurityCode = e.FSecurityCode and d.FStartDate = e.FStartDate " +
            ") f on a.FSecurityCode = f.FSecurityCode " +
            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1  and FCatCode = 'FI') bb on bb.FCatCode = f.FCatCode " +
            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1 and FCatCode = 'FI') cc on cc.FSubCatCode = f.FSubCatCode " +
            " left join (select FCusCatCode,FCusCatName from " +
            pub.yssGetTableName("Tb_Para_CustomCategory") +
            " where FCheckState = 1) dd on dd.FCusCatCode = f.FCusCatCode " +
            " left join (select FPeriodCode,FPeriodName from " +
            pub.yssGetTableName("Tb_Para_Period") +
            " where FCheckState = 1) h on a.FPeriodCode = h.FPeriodCode " +
            " left join (select FFormulaCode, FFormulaName from " +
            pub.yssGetTableName("Tb_Para_Performula") +
            " where FCheckState = 1) i on a.FPerExpCode = i.FFormulaCode" +
            " left join (select FRoundCode,FRoundName from " +
            pub.yssGetTableName("Tb_Para_Rounding") +
            " where FCheckState = 1) g on a.FRoundCode = g.FRoundCode" +
            //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FQuoteWay"，否则在使用DB2数据库时会报数据类型错误
            " left join Tb_Fun_Vocabulary l on " +
            dbl.sqlToChar("a.FQuoteWay") +
            " = l.FVocCode and l.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_FIX_QUOTEWAY) +
            //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FCalcInsWay"，否则在使用DB2数据库时会报数据类型错误
            " left join Tb_Fun_Vocabulary m on " +
            dbl.sqlToChar("a.FCalcInsWay") +
            " = m.FVocCode and m.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_FIX_CALCINSWAY) +
            //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FInterestOrigin"，否则在使用DB2数据库时会报数据类型错误
            " left join Tb_Fun_Vocabulary n on " +
            dbl.sqlToChar("a.FInterestOrigin") +
            " = n.FVocCode and n.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_FIX_INTORG) +
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) o on a.FCalcInsMeticDay = o.FCIMCode " +
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) p on a.FCalcInsMeticBuy = p.FCIMCode " +
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) q on a.FCalcInsMeticSell = q.FCIMCode " +
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) r on a.FCalcPriceMetic = r.FCIMCode " + // lzp  add  2007 12.13
            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) s on a.FAmortization = s.FCIMCode " + // add by leeyu　合中保的版本时用的
            /**shashijie 2012-2-9 STORY 1713 */
            " Left Join (Select Fcimcode, Fcimname From Tb_Base_Calcinsmetic) t On a.FTaskMoneyCode = t.Fcimcode " + //百元派息公式名称
			/**end*/
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc"; //点全部显示时报错，没有y，应为a。杨文奇20071008
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     * 获取已审核的现金帐户信息维护数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        try {
            sHeader = "债券代码\t债券名称\t启用日期";
            strSql = "select y.* from " +
                " (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_FixInterest") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                "and FCheckState = 1 group by FSecurityCode) x join" +
                " (select t.Fcimname As FTaskMoneyName ,a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, f.FSecurityName, f.FCatCode, bb.FCatName,f.FSubCatCode," +
                " cc.FSubCatName, f.FCusCatCode,dd.FCusCatName, g.FRoundName ,h.FPeriodName, i.FFormulaName " +
                //========by leeyu 合中保的版本　
                ",o.FCIMName as FCalcInsMeticDayName,p.FCIMName as FCalcInsMeticBuyName,q.FCIMName as FCalcInsMeticSellName,r.FCIMName as FCalcPriceName  " +
                ", l.FVocName as FQuoteWayValue, m.FVocName as FCalcInsWayValue, n.FVocName as FInterestOriginValue,s.FCIMName as FAmortizationName  " +
                //===============
                " from " + pub.yssGetTableName("Tb_Para_FixInterest") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select d.* from " +
                pub.yssGetTableName("Tb_Para_Security") + " d join (" +
                " select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCatCode = 'FI' group by FSecurityCode ) e on d.FSecurityCode = e.FSecurityCode and d.FStartDate = e.FStartDate " +
                ") f on a.FSecurityCode = f.FSecurityCode " +
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1  and FCatCode = 'FI') bb on bb.FCatCode = f.FCatCode " +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1 and FCatCode = 'FI') cc on cc.FSubCatCode = f.FSubCatCode " +
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " where FCheckState = 1) dd on dd.FCusCatCode = f.FCusCatCode " +
                " left join (select FPeriodCode,FPeriodName from " +
                pub.yssGetTableName("Tb_Para_Period") +
                " where FCheckState = 1) h on a.FPeriodCode = h.FPeriodCode " +
                " left join (select FFormulaCode, FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState = 1) i on a.FPerExpCode = i.FFormulaCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) g on a.FRoundCode = g.FRoundCode" +
                //-----------------lzp  add 2007 12.14
                " left join Tb_Fun_Vocabulary l on " +
                dbl.sqlToChar("a.FQuoteWay") +
                " = l.FVocCode and l.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIX_QUOTEWAY) +
                " left join Tb_Fun_Vocabulary m on " +
                dbl.sqlToChar("a.FCalcInsWay") +
                " = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIX_CALCINSWAY) +
                " left join Tb_Fun_Vocabulary n on " +
                dbl.sqlToChar("a.FInterestOrigin") +
                " = n.FVocCode and n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIX_INTORG) +
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) o on a.FCalcInsMeticDay = o.FCIMCode " +
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) p on a.FCalcInsMeticBuy = p.FCIMCode " +
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) q on a.FCalcInsMeticSell = q.FCIMCode " +
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) r on a.FCalcPriceMetic = r.FCIMCode " +
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) s on a.FAmortization = s.FCIMCode " + // add by leeyu 合中保的版本
                /**shashijie 2012-2-9 STORY 1713 */
                " Left Join (Select Fcimcode, Fcimname From Tb_Base_Calcinsmetic) t On a.FTaskMoneyCode = t.Fcimcode " + //百元派息公式名称
    			/**end*/
                //------------------
                buildFilterSql() +
                ") y on x.FSecurityCode = y.FSecurityCode and x.FStartDate = y.FStartDate" +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecurityCode") + " ").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FSecurityName") + " ").trim()).
                    append(
                        "\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"),
                                                 YssCons.YSS_DATEFORMAT)).
                    append(
                        YssCons.YSS_LINESPLITMARK);
                setSecurityAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取债券信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData3
     * 2009年5月15日添加跨组合群的处理
     * 因为此处获取数据添加了组合群代码和名称，而调用setSecurityAttr是无这两项的
     * 因此不能再使用公用的方法处理了，自己独立一个拼接字符串的处理
     * 等待调用setSecurityAttr的方法里的sql语句包含此两项时，此处需要再次修改
     * authro : panjunfang
     * BugNO  : 国内项目 MS00002:QDV4.1赢时胜（上海）2009年4月20日01_A 跨组合群
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = ""; //列表头
        String sShowDataStr = ""; //所有显示的数据
        String sAllDataStr = ""; //所有数据
        String sVocStr = ""; //词汇
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String strSql = "";
        ResultSet rs = null;
        try {
            sHeader = this.getListView3Headers(); //getListView1ShowCols -> getListView3ShowCols,panjunfang modify 20090813 MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
            strSql = "select show.*,h.fassetgroupcode, h.fassetgroupname from (select y.* from " +
                " (select FSecurityCode,FCheckState from " + //xuqiji 20090427 删除,max(FStartDate) as FStartDate:QDV4赢时胜（上海）2009年4月21日01_B   MS00405    收益计提计提债券利息时对债券启用日期有判断
                pub.yssGetTableName("Tb_Para_FixInterest") +
                //" where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +//xuqiji 20090427:QDV4赢时胜（上海）2009年4月21日01_B  MS00405    收益计提计提债券利息时对债券启用日期有判断
                " where FCheckState <> 2 group by FSecurityCode,FCheckState) x join" + //xuqiji 20090427:QDV4赢时胜（上海）2009年4月21日01_B  MS00405    收益计提计提债券利息时对债券启用日期有判断
                " (select t.Fcimname As FTaskMoneyName ,a.*, l.FVocName as FQuoteWayValue, m.FVocName as FCalcInsWayValue, n.FVocName as FInterestOriginValue," +
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, f.FSecurityName, f.FCatCode, bb.FCatName,f.FSubCatCode," +
                " cc.FSubCatName, f.FCusCatCode,dd.FCusCatName, g.FRoundName ,h.FPeriodName, i.FFormulaName " +
                ",o.FCIMName as FCalcInsMeticDayName,p.FCIMName as FCalcInsMeticBuyName,q.FCIMName as FCalcInsMeticSellName,r.FCIMName as FCalcPriceName,s.FCIMName as FAmortizationName " + //合中保的版本byleeyu
                " from " + pub.yssGetTableName("Tb_Para_FixInterest") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select d.* from " +
                pub.yssGetTableName("Tb_Para_Security") + " d join (" +
                " select FSecurityCode from " + //xuqiji 20090427 删除,max(FStartDate) as FStartDate:QDV4赢时胜（上海）2009年4月21日01_B   MS00405    收益计提计提债券利息时对债券启用日期有判断
                pub.yssGetTableName("Tb_Para_Security") +
                //" where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +//xuqiji 20090427:QDV4赢时胜（上海）2009年4月21日01_B   MS00405    收益计提计提债券利息时对债券启用日期有判断
                " where FCatCode = 'FI' group by FSecurityCode ) e on d.FSecurityCode = e.FSecurityCode  " + //xuqiji 20090427 删除 and d.FStartDate = e.FStartDate:QDV4赢时胜（上海）2009年4月21日01_B   MS00405    收益计提计提债券利息时对债券启用日期有判断
                ") f on a.FSecurityCode = f.FSecurityCode " +
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1  and FCatCode = 'FI') bb on bb.FCatCode = f.FCatCode " +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1 and FCatCode = 'FI') cc on cc.FSubCatCode = f.FSubCatCode " +
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " where FCheckState = 1) dd on dd.FCusCatCode = f.FCusCatCode " +
                " left join (select FPeriodCode,FPeriodName from " +
                pub.yssGetTableName("Tb_Para_Period") +
                " where FCheckState = 1) h on a.FPeriodCode = h.FPeriodCode " +
                " left join (select FFormulaCode, FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState = 1) i on a.FPerExpCode = i.FFormulaCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) g on a.FRoundCode = g.FRoundCode" +
                " left join Tb_Fun_Vocabulary l on a.FQuoteWay = l.FVocCode and l.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIX_QUOTEWAY) +
                " left join Tb_Fun_Vocabulary m on a.FCalcInsWay = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIX_CALCINSWAY) +
                " left join Tb_Fun_Vocabulary n on a.FInterestOrigin = n.FVocCode and n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIX_INTORG) +
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) o on a.FCalcInsMeticDay = o.FCIMCode " + //sj 20071207 add
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) p on a.FCalcInsMeticBuy = p.FCIMCode " + //前台没有显示方法名称
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) q on a.FCalcInsMeticSell = q.FCIMCode " +
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) r on a.FCalcPriceMetic = r.FCIMCode " + // lzp  add  2007 12.13
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) s on a.FAmortization = s.FCIMCode " + // add by leeyu 合中保的版本
                /**shashijie 2012-2-9 STORY 1713 */
                " Left Join (Select Fcimcode, Fcimname From Tb_Base_Calcinsmetic) t On a.FTaskMoneyCode = t.Fcimcode " + //百元派息公式名称
    			/**end*/
                buildFilterSql() +
                ") y on x.FSecurityCode = y.FSecurityCode " + //xuqiji 20090427 删除 and x.FStartDate = y.FStartDate:QDV4赢时胜（上海）2009年4月21日01_B  MS00405    收益计提计提债券利息时对债券启用日期有判断
                " order by y.FCheckState, y.FCreateTime desc) show" +
                //--------- MS00245 QDV4交银施罗德2009年02月13日01_B sj modified -------------------------------------------------//
                " join (select distinct FSecurityCode from (select FSecurityCode from " + //获取唯一值
                pub.yssGetTableName("tb_stock_security") +
                " where FCheckState = 1 and FPortCode in (" +
                this.operSql.sqlCodes(this.filterType.sPortCodes) +
                ") and FStorageDate between  " +
                dbl.sqlDate(YssFun.addDay(this.filterType.dStartDate, -1)) + " and " + //前一天的库存数据
                dbl.sqlDate(this.filterType.dEndDate) +
                " union select FSecurityCode from " + pub.yssGetTableName("tb_data_subtrade") + //以下为合并交易子表中的交易数据
                " where FCheckState = 1 and FPortCode in (" + this.operSql.sqlCodes(this.filterType.sPortCodes) + ") and FBargainDate between " +
                dbl.sqlDate(YssFun.addDay(this.filterType.dStartDate, -1)) + " and " + dbl.sqlDate(this.filterType.dEndDate) +
                //-- MS00263  QDV4交银施罗德2009年02月13日01_B ------------------------------------------------------------------//
                " union select FSecurityCode from " + pub.yssGetTableName("tb_data_integrated") + //以下为综合业务表中的交易数据
                " where FCheckState = 1 and FPortCode in (" + this.operSql.sqlCodes(this.filterType.sPortCodes) + ") and FExchangeDate between " +
                dbl.sqlDate(YssFun.addDay(this.filterType.dStartDate, -1)) + " and " +
                dbl.sqlDate(this.filterType.dEndDate) +
                ") al) stock on show.FSecurityCode = stock.FSecurityCode " +
                // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
                //=====================================================================================
                " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
                pub.getPrefixTB() + "' "+
				//添加证券应收应付数据关联，目的在于判断当日有没有作过计息统计 QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
                 (sShowType.equalsIgnoreCase("showAll")?"":(" where not exists (select '1' from "+pub.yssGetTableName("Tb_Data_Secrecpay")+" sec "+
                	" where sec.FTransDate between "+dbl.sqlDate(this.filterType.dStartDate)+" and "+dbl.sqlDate(this.filterType.dEndDate)+
                	" and sec.FPortCode in("+operSql.sqlCodes(this.filterType.sPortCodes)+
                	") and FTsfTypeCode='"+YssOperCons.YSS_ZJDBLX_Rec+"' and FSubTsfTypeCode='"+
                	YssOperCons.YSS_ZJDBZLX_FI_RecInterest+"' "+
                 	" and sec.FSecurityCode=show.FSecurityCode )"));
            	//add by huangqirong 2012-07-25 bug #4940
            	if(YssFun.dateDiff(this.filterType.dStartDate, this.filterType.dEndDate) == 0){
            		strSql += " and show.FSecurityCode not in( select psc.fsecuritycodebefore as FSecurityCode " +
            				  " from " + pub.yssGetTableName("tb_para_seccodechange") + " psc where psc.fbusinessdate between " + dbl.sqlDate(YssFun.addDay(this.filterType.dStartDate, -1)) + 
            				  " and  " + dbl.sqlDate(this.filterType.dEndDate) + " and psc.fcheckstate = 1 ) " ;
            	}
            	//add by huangqirong 2012-07-25 bug #4940
            //--------------------------------------------------------------------------------------------------------------//
            //===============================================================================
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView3ShowCols())).//getListView1ShowCols -> getListView3ShowCols,panjunfang modify 20090813 MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
                // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang 20090515
                this.assetGroupCode = rs.getString("fassetgroupcode") + ""; //给组合群代码赋值
                this.assetGroupName = rs.getString("fassetgroupname") + ""; //给组合群名称赋值
                //---------------------------------------------------------------------------------------------
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_FIX_INTORG + "," +
                                        YssCons.YSS_FIX_CALCINSWAY + "," +
                                        YssCons.YSS_FIX_QUOTEWAY + "," +
                                        YssCons.YSS_FIX_Level);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView3ShowCols() + "\r\f" + "voc" + sVocStr;//getListView1ShowCols -> getListView3ShowCols,panjunfang modify 20090813 MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
        } catch (Exception e) {
            throw new YssException("获取债券信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /***************************************************************
     * 【#3979:: 计提债券利息时，添加债券信息的检查】
     *  获取债券信息列表前，检查债券信息是否已维护
     * add by jiangshichao 2011.04.01
     * @return
     * @throws YssException
     */
    public String CheckFixInterInfo()throws YssException{
    	
    	ResultSet rs = null;
    	String CheckInfo = "";
    	StringBuffer buf = new StringBuffer();
    	try{
    		buf.append(" select * from ");
    		buf.append(" (select a2.* from ");
    		buf.append(" (select distinct FSecurityCode from ");
    		//--- 证券库存中的债券代码
    		buf.append(" (select FSecurityCode from "+pub.yssGetTableName("tb_stock_security")+"  where FCheckState = 1  and FPortCode in (" + this.operSql.sqlCodes(this.sPortCodes) + ") ");
    		buf.append(" and FStorageDate between "+dbl.sqlDate(this.dStartDate)+" and "+dbl.sqlDate(this.dEndDate));
    		//--- 交易子表中的债券代码
    		buf.append(" union all ");
    		buf.append(" select FSecurityCode from "+pub.yssGetTableName("tb_data_subtrade")+"  where FCheckState = 1 and FPortCode in (" + this.operSql.sqlCodes(this.sPortCodes) + ") ");
    		buf.append(" and FBargainDate between "+dbl.sqlDate(this.dStartDate)+" and "+dbl.sqlDate(this.dEndDate));
    		
    		//--- 综合业务中的债券代码 
    		buf.append(" union all ");
    		buf.append(" select FSecurityCode from "+pub.yssGetTableName("tb_data_integrated")+"  where FCheckState = 1 and FPortCode in (" + this.operSql.sqlCodes(this.sPortCodes) + ") ");
    		buf.append(" and FExchangeDate between "+dbl.sqlDate(this.dStartDate)+" and "+dbl.sqlDate(this.dEndDate)+" ))a1");
    		//---
    		buf.append(" join ");
    		buf.append(" (select fsecuritycode,fsecurityname from "+pub.yssGetTableName("Tb_Para_Security")+" where fcheckstate=1 and fcatcode='FI')a2 ");
    		buf.append(" on a1.fsecuritycode = a2.fsecuritycode )stock ");
    		//-----------------------------------------------------//
    		buf.append(" where not exists (select fsecuritycode from "+pub.yssGetTableName("tb_para_fixinterest")+" fix where fcheckstate=1 and stock.fsecuritycode=fix.fsecuritycode)");
    		
    		rs = dbl.openResultSet(buf.toString());
    		while(rs.next()){
    			CheckInfo +=" 证券代码："+rs.getString("FSecurityCode")+"		证券名称："+rs.getString("fsecurityname")+"\r\n";
    		}
    		
    		return CheckInfo;
    	}catch(Exception e){
    		throw new YssException("【日终处理-收益计提】检查债券信息出错......");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
	
	
    /**
     * getSetting　合中保的版本时用的 byleeyu
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " +
                pub.yssGetTableName("Tb_Para_FixInterest") +
                " where FSecurityCode =" +
                dbl.sqlString(this.strSecurityCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dtStartDate = rs.getDate("FStartDate"); //启用日期
                strFaceValue = rs.getDouble("FFaceValue") + "".length() == 0 ?
                    new BigDecimal(0) :
                    rs.getBigDecimal("FFaceValue"); ////修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
                strFaceRate = rs.getDouble("FFaceRate") + "".length() == 0 ? new BigDecimal(0) :
                    rs.getBigDecimal("FFaceRate"); //票面利率//bug 2381 by zhouwei 20111111  精度
                strIssuePrice = rs.getDouble("FIssuePrice") + "".length() == 0 ?
                    new BigDecimal(0) :
                    rs.getBigDecimal("FIssuePrice"); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
                dtIssueDate = rs.getDate("FIssueDate"); //发行日期
                dtInsStartDate = rs.getDate("FInsStartDate"); //计息起始日
                dtInsEndDate = rs.getDate("FInsEndDate"); //计息截止日
                dtInsCashDate = rs.getDate("FInsCashDate"); //兑付日期
                strInsFrequency = rs.getDouble("FInsFrequency") + "".length() ==
                     0 ? new BigDecimal(0): rs.getBigDecimal("FInsFrequency"); //付息频率 bug 2381 by zhouwei 20111111  精度
                strQuoteWay = rs.getString("FQuoteWay") + ""; //报价方式
                strCalcInsMeticDay = rs.getString("FCalcInsMeticDay") + ""; //每日利息算法
                strCalcInsMeticBuy = rs.getString("FCalcInsMeticBuy") + ""; //买入利息算法
                strCalcInsMeticSell = rs.getString("FCalcInsMeticSell") + ""; //卖出利息算法
                strCalcPriceCode = rs.getString("FCalcPriceMetic") + ""; //估值价算法
                strCalcInsWay = rs.getString("FCalcInsWay") + ""; //计息方法
                strInsOrigin = rs.getString("FInterestOrigin") + ""; //利息来源
                strPerExpCode = rs.getString("FPerExpCode") + ""; //利息公式代码
                strPeriodCode = rs.getString("FPeriodCode") + ""; //期间代码
                strRoundCode = rs.getString("FRoundCode") + ""; //舍入设置代码
                strDesc = rs.getString("FDesc") + ""; //描述
                strCreditLevel = rs.getString("FCreditLevel") + ""; //信用评级
                dFactRate = rs.getDouble("FFactRate") + "".length() == 0 ? 0 :
                    rs.getDouble("FFactRate");
                sAmortizationCode = rs.getString("FAmortization") + ""; //摊销溢价 by leeyu
                dbPretaxFaceRate = rs.getDouble("FBeforeFaceRate") + "".length() == 0 ?new BigDecimal(0) : // bug 2381 by zhouwei 20111111  精度
                    rs.getBigDecimal("FBeforeFaceRate");//税前票面利率，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                baseCPI = (rs.getDouble("FBASECPI") + "").length() == 0 ? 0 : rs.getDouble("FBASECPI");//基础CPI add by yanghaiming 20110209 #461 QDV4博时2010年12月29日01_A
                
                /**shashijie 2012-1-18 STORY 1713 */
                FTaskMoneyCode = rs.getString("FTaskMoneyCode") + ""; //每百元派息金额
				/**end*/
            }
        } catch (Exception ex) {
            throw new YssException("获取债券的基本信息出错", ex);
        }
        return null;
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

     public String addSetting() throws YssException {
         String strSql = "";
         boolean bTrans = false; //代表是否开始了事务
         Connection conn = dbl.loadConnection();
         try {
             strSql = "insert into " +
                 pub.yssGetTableName("Tb_Para_FixInterest") +
                 "(FSECURITYCODE, FSTARTDATE, FISSUEDATE, FISSUEPRICE, FINSSTARTDATE, FINSENDDATE, " +
                 " FINSCASHDATE, FFACEVALUE, FFACERATE, FINSFREQUENCY, FQUOTEWAY,FCreditLevel,FCALCINSWAY, " +
                 " FBeforeFaceRate, " + //税前票面利率，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                 " FBASECPI, " + //基础CPI add by yanghaiming 20110209 #461 QDV4博时2010年12月29日01_A
                 " FCalcInsMeticDay, FCalcInsMeticBuy, FCalcInsMeticSell,FCalcPriceMetic, " +
                 " FINTERESTORIGIN, FPEREXPCODE, FPERIODCODE, FROUNDCODE, FDESC ," +
                 " FCHECKSTATE, FCREATOR, FCREATETIME,FCheckUser,FFactRate,FAmortization,FVALUEDATES " +
                /**shashijie 2012-1-18 STORY 1713*/
                " ,FTaskMoneyCode,fInterTaxperexpcode "+
                /**end*/
                 " ) values(" + //合中保的版本时用的 by leeyu
                 dbl.sqlString(this.strSecurityCode) + "," +
                 dbl.sqlDate(this.dtStartDate) + "," +
                 dbl.sqlDate(this.dtIssueDate) + "," +
                 this.strIssuePrice + "," +
                 dbl.sqlDate(this.dtInsStartDate) + "," +
                 dbl.sqlDate(this.dtInsEndDate) + "," +
                 dbl.sqlDate(this.dtInsCashDate) + "," +
                 this.strFaceValue + "," +
                 this.strFaceRate + "," +
                 this.strInsFrequency + "," +
                 YssFun.toInt(this.strQuoteWay) + "," +
                 dbl.sqlString(this.strCreditLevel) + "," +
                 YssFun.toInt(this.strCalcInsWay) + "," +
                 this.dbPretaxFaceRate + "," + //税前票面利率，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                 this.baseCPI + "," + //基础CPI add by yanghaiming 20110209 #461 QDV4博时2010年12月29日01_A
                 //------------------2007.11.12 添加 蒋锦--------------------//
                 dbl.sqlString(this.strCalcInsMeticDay) + "," +
                 dbl.sqlString(this.strCalcInsMeticBuy) + "," +
                 dbl.sqlString(this.strCalcInsMeticSell) + "," +
                 //---------------------------------------------------------//
                 dbl.sqlString(this.strCalcPriceCode) + "," + // lzp  add
                 YssFun.toInt(this.strInsOrigin) + "," +
                 dbl.sqlString(this.strPerExpCode) + "," +
                 dbl.sqlString(this.strPeriodCode.length() == 0 ? " " :
                               this.strPeriodCode) + "," +
                 dbl.sqlString(this.strRoundCode.length() == 0 ? " " :
                               this.strRoundCode) + "," +
                 dbl.sqlString(this.strDesc) + "," +
                 (pub.getSysCheckState() ? "0" : "1") + "," +
                 dbl.sqlString(this.creatorCode) + "," +
                 dbl.sqlString(this.creatorTime) + "," +
                 //===========合中保的版本时用的
                 (pub.getSysCheckState() ? "' '" :
                  dbl.sqlString(this.creatorCode)) + "," +
                 this.dFactRate + "," +
                 dbl.sqlString(this.sAmortizationCode) + "," +
                 dbl.sqlString(this.strFvalueDates.length() == 0 ? " " :
                     this.strFvalueDates)+""+ // add by wuweiqi 20110223 #2581::债券计息节假日时存在除息的金额和到账的金额不一致的情况处理
             	//=============================
             	/**shashijie 2012-1-18 STORY 1713*/
				" , "+dbl.sqlString(this.FTaskMoneyCode)+","+dbl.sqlString(this.sInterTaxPeriodCode)+
				/**end*/
				" ) ";
             
             conn.setAutoCommit(false);
             //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
             if (this.sMutilStr != null && this.sMutilStr.trim().length() != 0) {
                 CreditLevelBean level = new CreditLevelBean();
                 level.setYssPub(pub);
                 level.masterSecurityCode = this.strSecurityCode;
                 level.saveMutliSetting(this.sMutilStr);
             }
             
             bTrans = true;
             dbl.executeSql(strSql);
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
         }

         catch (Exception e) {
             throw new YssException("增加债券信息出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }

         return null;
     }

    /**
     * editSetting
     * 修改日期：2007-11-12
     * 蒋锦 添加字段
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_FixInterest") +
                " set " +
                "  FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode) +
                ", FStartDate = " + dbl.sqlDate(this.dtStartDate) +
                ", FISSUEDATE = " + dbl.sqlDate(this.dtIssueDate) +
                ", FISSUEPRICE = " + this.strIssuePrice +
                ", FINSSTARTDATE = " + dbl.sqlDate(this.dtInsStartDate) +
                ", FINSENDDATE = " + dbl.sqlDate(this.dtInsEndDate) +
                ", FINSCASHDATE = " + dbl.sqlDate(this.dtInsCashDate) +
                ", FFACEVALUE = " + this.strFaceValue +
                ", FFACERATE = " + this.strFaceRate +
                ", FBeforeFaceRate = " + this.dbPretaxFaceRate + //税前票面利率，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                ", FBASECPI = " + this.baseCPI + //基础CPI add by yanghaiming 20110209 #461 QDV4博时2010年12月29日01_A
                ", FINSFREQUENCY = " + this.strInsFrequency +
                ", FQUOTEWAY = " + YssFun.toInt(this.strQuoteWay) +
                ", FCreditLevel = " + dbl.sqlString(this.strCreditLevel) +
                ", FCALCINSWAY = " + YssFun.toInt(this.strCalcInsWay) +
                //----------------------2007.11.12 添加 蒋锦----------------------//
                ", FCalcInsMeticDay = " +
                dbl.sqlString(this.strCalcInsMeticDay) +
                ", FCalcInsMeticBuy = " +
                dbl.sqlString(this.strCalcInsMeticBuy) +
                ", FCalcInsMeticSell = " +
                dbl.sqlString(this.strCalcInsMeticSell) +
                //---------------------------------------------------------------//
                ", FCalcPriceMetic = " +
                dbl.sqlString(this.strCalcPriceCode) + // lzp  add
                ", FINTERESTORIGIN = " + YssFun.toInt(this.strInsOrigin) +
                ", FPEREXPCODE = " + dbl.sqlString(this.strPerExpCode) +
                ", FFactRate =" + this.dFactRate + // add by leeyu 080808 合中保的版本时用的
                ", FPERIODCODE = " +
                dbl.sqlString(this.strPeriodCode.length() == 0 ? " " :
                              this.strPeriodCode) +
                ", FRoundCode = " +
                dbl.sqlString(this.strRoundCode.length() == 0 ? " " :
                              this.strRoundCode) +
                ", FDesc = " + dbl.sqlString(this.strDesc) +
                ",FAmortization=" + dbl.sqlString(this.sAmortizationCode) + // add by leeyu　合中保的版本时用的
                ", FCHECKSTATE = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FValueDates = " + dbl.sqlString(this.strFvalueDates.length() == 0 ? " " :
                    this.strFvalueDates )+//add by wuweiqi 20110223 #2581::债券计息节假日时存在除息的金额和到账的金额不一致的情况处理
                ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                 
                /**shashijie 2012-1-18 STORY 1713*/
				" , FTaskMoneyCode = "+dbl.sqlString(this.FTaskMoneyCode)+
				/**end*/
                 ",fInterTaxperexpcode="+dbl.sqlString(this.sInterTaxPeriodCode)+
                " where FSECURITYCODE = " +
                dbl.sqlString(this.strOldSecurityCode) +
                " and FSTARTDATE = " + dbl.sqlDate(this.dtOldStartDate);           
            conn.setAutoCommit(false);
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.sMutilStr != null && this.sMutilStr.trim().length() != 0) {
                CreditLevelBean level = new CreditLevelBean();
                level.setYssPub(pub);
                level.masterSecurityCode = this.strOldSecurityCode;
                level.saveMutliSetting(this.sMutilStr);
            }
            
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改债券信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，即放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int Count = 0;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_FixInterest") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FSECURITYCODE = " +
                dbl.sqlString(this.strOldSecurityCode) +
                " and FStartDate = " + dbl.sqlDate(this.dtStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            strSql = "select count(FSecurityCode) from " +
                pub.yssGetTableName("Tb_Para_CreditLevel") +
                " where FSecurityCode ='" + this.strOldSecurityCode + "'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                Count = rs.getInt(1);
            }
            if (Count != 0) {
                for (int i = 0; i < Count; i++) {
                    strSql = " delete from " +
                        pub.yssGetTableName("Tb_Para_CreditLevel") +
                        " where FSecurityCode ='" +
                        this.strOldSecurityCode + "'";
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年3月25号
     *  修改人：单亮
     *  原方法功能：只能处理债券信息的审核和未审核的单条信息。
     *  新方法功能：可以债券信息连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改原因：原方法不能同时处理多条信息
     *  修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        //修改后的代码
        //--------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String[] sChkAccLinkAry = null;
        SecurityBean security = null;
        try {
            security = new SecurityBean();
            security.setYssPub(pub);
            //----------------------------------------------------------------------------------
            //MS00179 QDV4建行2009年1月07日01_B 2009.02.17 方浩
            ////批量审核与反审核和批量删除
            conn.setAutoCommit(false); //手动开启一个事物
            bTrans = true;
            this.setYssPub(pub); //设置一些基础信息
            if (! (checkAccLinks == null || checkAccLinks.equalsIgnoreCase(""))) { //判断是否有批量数据
                sChkAccLinkAry = this.checkAccLinks.split("\r\n"); //把选中的批量数据解析到单条入到数组中
                for (int i = 0; i < sChkAccLinkAry.length; i++) { //循环遍历这个数组
                    this.parseRowStr(sChkAccLinkAry[i]); //把这个选择中的单个条目放到当前对象中
                    strSql = "update " +
                        pub.yssGetTableName("Tb_Para_FixInterest") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = '" +
                        pub.getUserCode() +
                        "', FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSECURITYCODE = " +
                        dbl.sqlString(this.strOldSecurityCode) +
                        " and FStartDate = " +
                        dbl.sqlDate(this.dtStartDate); //更新的SQL语句
                    dbl.executeSql(strSql); //执行SQL语句
                    // MS00427 QDV4赢时胜（上海）2009年04月27日04_B 在审核债券时,不联动审核证券设置 sj -----
//                    strSql = "update " + pub.yssGetTableName("Tb_Para_Security") +
//                             " set FCheckState = " +
//                             this.checkStateId + ", FCheckUser = '" +
//                             pub.getUserCode() +
//                             "', FCheckTime = '" +
//                             YssFun.formatDatetime(new java.util.Date()) + "'" +
//                             " where FSECURITYCODE = " +
//                             dbl.sqlString(this.strSecurityCode); //更新的SQL语句
//                    dbl.executeSql(strSql); //审核反审核债券的同时审核证券表信息 MS00045
                    //-------------------------------------------------------------------------------
                }
                //-------------------------------------------------------------------------------------------
                //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            } else if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " +
                        pub.yssGetTableName("Tb_Para_FixInterest") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = '" +
                        pub.getUserCode() +
                        "', FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSECURITYCODE = " +
                        dbl.sqlString(this.strOldSecurityCode) +
                        " and FStartDate = " +
                        dbl.sqlDate(this.dtStartDate);
                    dbl.executeSql(strSql);
                    strSql = "update " + pub.yssGetTableName("Tb_Para_Security") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = '" +
                        pub.getUserCode() +
                        "', FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSECURITYCODE = " +
                        dbl.sqlString(this.strSecurityCode);
                    dbl.executeSql(strSql); //审核反审核债券的同时审核证券表信息 MS00045
                }
              //如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            } else if (strOldSecurityCode != null && !strOldSecurityCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_FixInterest") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = '" +
                    pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSECURITYCODE = " +
                    dbl.sqlString(this.strOldSecurityCode) +
                    " and FStartDate = " + dbl.sqlDate(this.dtStartDate);
                dbl.executeSql(strSql);
                //执行sql语句
//            dbl.executeSql(strSql);//上面已经有了
                strSql = "update " + pub.yssGetTableName("Tb_Para_Security") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = '" +
                    pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSECURITYCODE = " +
                    dbl.sqlString(this.strSecurityCode);
                dbl.executeSql(strSql); //审核反审核债券的同时审核证券表信息

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核债券信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //-----------------end
    }
    
    /**
     * 为各项变量赋值
     * 修改日期：2007-11-12
     * 蒋锦 添加字段
     */
    public void setSecurityAttr(ResultSet rs) throws SQLException {
        this.strSecurityCode = rs.getString("FSecurityCode");
        this.strSecurityName = rs.getString("FSecurityName");
        this.dtStartDate = rs.getDate("FStartDate");
        this.strCatCode = rs.getString("FCatCode");
        this.strCatName = rs.getString("FCatName");
        this.strSubCatCode = rs.getString("FSubCatCode");
        this.strSubCatName = rs.getString("FSubCatName");
        this.strCusCatCode = rs.getString("FCusCatCode") != null ?
            rs.getString("FCusCatCode") : "";
        this.strCusCatName = rs.getString("FCusCatName") != null ?
            rs.getString("FCusCatName") : "";
        this.strFaceValue = rs.getBigDecimal("FFaceValue"); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
        this.strFaceRate = rs.getBigDecimal("FFaceRate");//bug 2381 by zhouwei 20111111  精度
        this.dbPretaxFaceRate = rs.getBigDecimal("FBeforeFaceRate");//税前票面利率，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
        this.baseCPI = rs.getDouble("FBASECPI");//基础CPI add by yanghaiming 20110209 #461 QDV4博时2010年12月29日01_A
        this.strIssuePrice = rs.getBigDecimal("FIssuePrice"); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
        this.dtIssueDate = rs.getDate("FIssueDate");
        this.dtInsStartDate = rs.getDate("FInsStartDate");
        this.dtInsEndDate = rs.getDate("FInsEndDate");
        this.dtInsCashDate = rs.getDate("FInsCashDate");
        this.strInsFrequency = rs.getBigDecimal("FInsFrequency");//bug 2381 by zhouwei 20111111  精度
        this.strQuoteWay = rs.getString("FQuoteWay");
        //----------------------2007.11.12 添加 蒋锦------------------------//
        this.strCalcInsMeticDay = rs.getString("FCalcInsMeticDay");
        this.strCalcInsMeticBuy = rs.getString("FCalcInsMeticBuy");
        this.strCalcInsMeticSell = rs.getString("FCalcInsMeticSell");
        //-----------------------------------------------------------------//
        this.strCalcPriceCode = rs.getString("FCalcPriceMetic"); // lzp  add
        this.strCalcPriceName = rs.getString("FCalcPriceName"); // lzp  add
        this.strCalcInsMeticBuyName = rs.getString("FCalcInsMeticBuyName");
        this.strCalcInsMeticDayName = rs.getString("FCalcInsMeticDayName");
        this.strCalcInsMeticSellName = rs.getString("FCalcInsMeticSellName");
        this.strCalcInsWay = rs.getString("FCalcInsWay");
        this.strInsOrigin = rs.getString("FInterestOrigin");
        this.strPerExpCode = rs.getString("FPerExpCode");
        this.strPerExpName = rs.getString("FFormulaName");
        this.strPeriodCode = rs.getString("FPeriodCode");
        this.strPeriodName = rs.getString("FPeriodName");
        this.strRoundCode = rs.getString("FRoundCode");
        this.strRoundName = rs.getString("FRoundName");
        this.strDesc = rs.getString("FDesc");
        this.strCreditLevel = rs.getString("FCreditLevel");
        //==============合中保的版本时用的
        this.dFactRate = rs.getDouble("FFactRate") + "".length() == 0 ? 0 :
            rs.getDouble("FFactRate"); // by leeyu 080808
        sAmortizationCode = rs.getString("FAmortization") + ""; //摊销溢价 by leeyu
        sAmortizationName = rs.getString("FAmortizationName") + ""; //摊销溢价 by leeyu
        //======================================
        /**shashijie 2012-1-18 STORY 1713*/
		this.FTaskMoneyCode = rs.getString("FTaskMoneyCode")+"";//每百元派息金额
		this.FTaskMoneyName = rs.getString("FTaskMoneyName")+"";//每百元派息金额名称
		/**end*/
        this.strFvalueDates=rs.getString("FVALUEDATES")+"";//#2581::债券计息节假日时存在除息的金额和到账的金额不一致的情况处理 add by wuweiqi 20110224
        
        if(dbl.isFieldExist(rs,"fInterTaxperexpcode")&&dbl.isFieldExist(rs, "fInterTaxperexpName")){
        	this.sInterTaxPeriodCode = rs.getString("fInterTaxperexpcode");
        	this.sInterTaxPeriodName = rs.getString("fInterTaxperexpName");
        }
        super.setRecLog(rs);
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException{
    	//--- #3979::计提债券利息时，添加债券信息的检查 add by jiangshichao 2011.04.01 -------------------------------
    	String str = "";
    	if(sType.equalsIgnoreCase("checkFixInterestInfo")){
    		
    		str = CheckFixInterInfo();
    		return str;
    	}
    	//--- #3979::计提债券利息时，添加债券信息的检查   end ---------------------------------------------------------
        return "";
    }

    /**
     * getBeforeEditData
     * 修改日期：2007-11-12
     * 蒋锦 添加字段
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        FixInterestBean befFixInterestBean = new FixInterestBean();
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "select a.*, l.FVocName as FQuoteWayValue, m.FVocName as FCalcInsWayValue, n.FVocName as FInterestOriginValue," +
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, f.FSecurityName, f.FCatCode, bb.FCatName,f.FSubCatCode," +
                " cc.FSubCatName, f.FCusCatCode,dd.FCusCatName, g.FRoundName ,h.FPeriodName, i.FFormulaName " +
                //------------lzp   add   2007  12.14
                ",o.FCIMName as FCalcInsMeticDayName,p.FCIMName as FCalcInsMeticBuyName,q.FCIMName as FCalcInsMeticSellName,r.FCIMName as FCalcPriceName  " +
                //-------------------
                " from " + pub.yssGetTableName("Tb_Para_FixInterest") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select d.* from " +
                pub.yssGetTableName("Tb_Para_Security") + " d join (" +
                " select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCatCode = 'FI' group by FSecurityCode ) e on d.FSecurityCode = e.FSecurityCode and d.FStartDate = e.FStartDate " +
                ") f on a.FSecurityCode = f.FSecurityCode " +
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1  and FCatCode = 'FI') bb on bb.FCatCode = f.FCatCode " +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1 and FCatCode = 'FI') cc on cc.FSubCatCode = f.FSubCatCode " +
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " where FCheckState = 1) dd on dd.FCusCatCode = f.FCusCatCode " +
                " left join (select FPeriodCode,FPeriodName from " +
                pub.yssGetTableName("Tb_Para_Period") +
                " where FCheckState = 1) h on a.FPeriodCode = h.FPeriodCode " +
                " left join (select FFormulaCode, FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState = 1) i on a.FPerExpCode = i.FFormulaCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) g on a.FRoundCode = g.FRoundCode" +
                " left join Tb_Fun_Vocabulary l on " +
                dbl.sqlToChar("a.FQuoteWay") +
                " = l.FVocCode and l.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIX_QUOTEWAY) +
                " left join Tb_Fun_Vocabulary m on " +
                dbl.sqlToChar("a.FCalcInsWay") +
                " = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIX_CALCINSWAY) +
                " left join Tb_Fun_Vocabulary n on " +
                dbl.sqlToChar("a.FInterestOrigin") +
                " = n.FVocCode and n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIX_INTORG) +
                //-----------------lzp  add 2007 12.14
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) o on a.FCalcInsMeticDay = o.FCIMCode " +
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) p on a.FCalcInsMeticBuy = p.FCIMCode " +
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) q on a.FCalcInsMeticSell = q.FCIMCode " +
                " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) r on a.FCalcPriceMetic = r.FCIMCode " +
                //------------------
                "where a.FSecurityCode=" +
                dbl.sqlString(this.strOldSecurityCode) +
                "and a.FStartDate=" + dbl.sqlDate(this.dtOldStartDate);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befFixInterestBean.strSecurityCode = rs.getString(
                    "FSecurityCode");
                befFixInterestBean.strSecurityName = rs.getString(
                    "FSecurityName");
                befFixInterestBean.dtStartDate = rs.getDate("FStartDate");
                befFixInterestBean.strCatCode = rs.getString("FCatCode");
                befFixInterestBean.strCatName = rs.getString("FCatName");
                befFixInterestBean.strSubCatCode = rs.getString("FSubCatCode");
                befFixInterestBean.strSubCatName = rs.getString("FSubCatName");
                befFixInterestBean.strCusCatCode = rs.getString("FCusCatCode") != null ?
                    rs.getString("FCusCatCode") : "";
                befFixInterestBean.strCusCatName = rs.getString("FCusCatName") != null ?
                    rs.getString("FCusCatName") : "";
                befFixInterestBean.strFaceValue = rs.getBigDecimal("FFaceValue"); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
                befFixInterestBean.strFaceRate = rs.getBigDecimal("FFaceRate");// bug 2381 by zhouwei 20111111
                befFixInterestBean.strIssuePrice = rs.getBigDecimal(
                    "FIssuePrice"); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
                befFixInterestBean.dtIssueDate = rs.getDate("FIssueDate");
                befFixInterestBean.dtInsStartDate = rs.getDate("FInsStartDate");
                befFixInterestBean.dtInsEndDate = rs.getDate("FInsEndDate");
                befFixInterestBean.dtInsCashDate = rs.getDate("FInsCashDate");
                befFixInterestBean.strInsFrequency = rs.getBigDecimal(
                    "FInsFrequency");// bug 2381 by zhouwei 20111111
                befFixInterestBean.strQuoteWay = rs.getString("FQuoteWay");
                //--------------------------2007.11.12 添加 蒋锦--------------------------//
                befFixInterestBean.strCalcInsMeticDay = rs.getString(
                    "FCalcInsMeticDay");
                befFixInterestBean.strCalcInsMeticBuy = rs.getString(
                    "FCalcInsMeticBuy");
                befFixInterestBean.strCalcInsMeticSell = rs.getString(
                    "FCalcInsMeticSell");
                //----------------------------------------------------lzp  add 2007 12.14
                befFixInterestBean.strCalcPriceCode = rs.getString(
                    "FCalcPriceMetic");
                befFixInterestBean.strCalcPriceName = rs.getString(
                    "FCalcPriceName");
                befFixInterestBean.strCalcInsMeticBuyName = rs.getString(
                    "FCalcInsMeticBuyName");
                befFixInterestBean.strCalcInsMeticDayName = rs.getString(
                    "FCalcInsMeticDayName");
                befFixInterestBean.strCalcInsMeticSellName = rs.getString(
                    "FCalcInsMeticSellName");
                // ------------------------------------------------------------------

                befFixInterestBean.strCalcInsWay = rs.getString("FCalcInsWay");
                befFixInterestBean.strInsOrigin = rs.getString(
                    "FInterestOrigin");
                befFixInterestBean.strPerExpCode = rs.getString("FPerExpCode");
                befFixInterestBean.strPerExpName = rs.getString("FFormulaName");
                befFixInterestBean.strPeriodCode = rs.getString("FPeriodCode");
                befFixInterestBean.strPeriodName = rs.getString("FPeriodName");
                befFixInterestBean.strRoundCode = rs.getString("FRoundCode");
                befFixInterestBean.strRoundName = rs.getString("FRoundName");
                befFixInterestBean.strDesc = rs.getString("FDesc");
                befFixInterestBean.strCreditLevel = rs.getString("FCreditLevel");
                befFixInterestBean.dFactRate = rs.getDouble("FFactRate"); //合中保的版本时用的
                // bug 2381 by zhouwei 20111111
                befFixInterestBean.dbPretaxFaceRate = rs.getBigDecimal("FBeforeFaceRate");//税前票面利率，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                befFixInterestBean.baseCPI = rs.getDouble("FBASECPI");//基础CPI add by yanghaiming 20110209 #461 QDV4博时2010年12月29日01_A
                /**shashijie 2012-1-18 STORY 1713*/
                befFixInterestBean.FTaskMoneyCode = rs.getString("FTaskMoneyCode");
				/**end*/
                
            }
            return befFixInterestBean.buildRowStr();

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    public String getStrCalcPriceCode() {
        return strCalcPriceCode;
    }

    public String getStrCalcPriceName() {
        return strCalcPriceName;
    }

    public void setStrCalcPriceCode(String strCalcPriceCode) {
        this.strCalcPriceCode = strCalcPriceCode;
    }

    public void setStrCalcPriceName(String strCalcPriceName) {
        this.strCalcPriceName = strCalcPriceName;
    }

    /**
     * 从回收站中删除数据，即从数据库中彻底删除数据
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Para_FixInterest") +
                        " where FSECURITYCODE = " +
                        dbl.sqlString(this.strOldSecurityCode) +
                        " and FStartDate = " +
                        dbl.sqlDate(this.dtStartDate);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }

            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (strOldSecurityCode != "" && strOldSecurityCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_FixInterest") +
                    " where FSECURITYCODE = " +
                    dbl.sqlString(this.strOldSecurityCode) +
                    " and FStartDate = " + dbl.sqlDate(this.dtStartDate);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public java.util.Date getDStartDate() {
        return dStartDate;
    }

    public String getSPortCode() {
        return sPortCodes;
    }

    public java.util.Date getDEndDate() {
        return dEndDate;
    }

    public double getDFactRate() {
        return dFactRate;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public String getSAmortizationCode() {
        return sAmortizationCode;
    }

    public String getSAmortizationName() {
        return sAmortizationName;
    }

    public void setDStartDate(Date dDate) {
        this.dStartDate = dDate;
    }

    public void setSPortCode(String sPortCodes) {
        this.sPortCodes = sPortCodes;
    }

    public void setDEndDate(Date dEndDate) {
        this.dEndDate = dEndDate;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setDFactRate(double dFactRate) {
        this.dFactRate = dFactRate;
    }

    public void setSAmortizationCode(String sAmortizationCode) {
        this.sAmortizationCode = sAmortizationCode;
    }

    public void setSAmortizationName(String sAmortizationName) {
        this.sAmortizationName = sAmortizationName;
    }

    //新增持有到期债券 by leeyu 合中保的版本
    public String addFixInterestCYDQ(FixInterestBean interest,
                                     java.util.Date dDate) throws YssException {
        String securityCode = "";
        SecurityBean security = null;
        try {
            security = new SecurityBean();
            security.setYssPub(pub);
            security.setSecurityCode(interest.getStrSecurityCode());
            if (interest.getStrSecurityCode().length() > 0 &&
                interest.
                getStrSubCatCode().indexOf("_M" +
                                           YssFun.formatDate(dDate, "MMdd")) <=
                0) {
                securityCode = interest.getStrSecurityCode() + "_M" +
                    YssFun.formatDate(dDate, "MMdd") +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "tb_para_fixinterest"),
                                           dbl.sqlRight("FSecurityCode", 2), "01",
                                           " where FSecurityCode like '" +
                                           interest.getStrSecurityCode() + "_M" +
                                           YssFun.formatDate(dDate, "MMdd") +
                                           "%'");
            } else {
                securityCode = YssFun.left(interest.getStrSecurityCode(),
                                           interest.getStrSecurityCode().length() -
                                           8) + "_M" +
                    YssFun.formatDate(dDate, "MMdd") +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "tb_para_fixinterest"),
                                           dbl.sqlRight("FSecurityCode", 2), "01",
                                           " where FSecurityCode like '" +
                                           YssFun.left(interest.getStrSecurityCode(),
                    interest.getStrSecurityCode().length() - 2) +
                                           "%'");
            }
            interest.setStrSecurityCode(securityCode);
            super.checkStateId = 1;
            interest.addSetting();
            security.getSetting();
            security.setSecurityCode(securityCode);
            security.setStrSubCategoryCode("FI05");
            security.addSetting();
            return securityCode;
        } catch (Exception ex) {
            throw new YssException("新增债券的持有到期债券出错", ex);
        }
    }
    
    
    //modified by liubo.Story #1916
    //跨组合群的调度方案设置
    //==========================================
    public String getTreeViewGroupData1() throws YssException {
    	
    	String strSql = "";
    	
    	String[] sAllAssetGroup = getAllAssetGroup().split("\t");
    	strSql = "select allData from (";
        
    	for (int i = 0; i < sAllAssetGroup.length; i++)
    	{
	        strSql = strSql + " select y.*,'" + sAllAssetGroup[i] + "' as FAssetGroupCode from " +
	            " (select FSecurityCode,FCheckState,max(FStartDate) as FStartDate from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_FixInterest" +
	            " group by FSecurityCode,FCheckState) x join" +
	            " (select t.Fcimname As FTaskMoneyName ,a.*, l.FVocName as FQuoteWayValue, m.FVocName as FCalcInsWayValue, n.FVocName as FInterestOriginValue," +
	            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, f.FSecurityName, f.FCatCode, bb.FCatName,f.FSubCatCode," +
	            " cc.FSubCatName, f.FCusCatCode,dd.FCusCatName, g.FRoundName ,h.FPeriodName, i.FFormulaName " +
	            ",o.FCIMName as FCalcInsMeticDayName,p.FCIMName as FCalcInsMeticBuyName,q.FCIMName as FCalcInsMeticSellName,r.FCIMName as FCalcPriceName,s.FCIMName as FAmortizationName " + //合中保的版本时用的
	            " from Tb_" + sAllAssetGroup[i] + "_Para_FixInterest" + " a " +
	            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	            " left join (select d.* from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_Security" + " d join (" +
	            " select FSecurityCode, max(FStartDate) as FStartDate from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_Security" +
	            " where " +
	            " FCatCode = 'FI' group by FSecurityCode ) e on d.FSecurityCode = e.FSecurityCode and d.FStartDate = e.FStartDate " +
	            ") f on a.FSecurityCode = f.FSecurityCode " +
	            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1  and FCatCode = 'FI') bb on bb.FCatCode = f.FCatCode " +
	            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1 and FCatCode = 'FI') cc on cc.FSubCatCode = f.FSubCatCode " +
	            " left join (select FCusCatCode,FCusCatName from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_CustomCategory" +
	            " where FCheckState = 1) dd on dd.FCusCatCode = f.FCusCatCode " +
	            " left join (select FPeriodCode,FPeriodName from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_Period" +
	            " where FCheckState = 1) h on a.FPeriodCode = h.FPeriodCode " +
	            " left join (select FFormulaCode, FFormulaName from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_Performula" +
	            " where FCheckState = 1) i on a.FPerExpCode = i.FFormulaCode" +
	            " left join (select FRoundCode,FRoundName from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_Rounding" +
	            " where FCheckState = 1) g on a.FRoundCode = g.FRoundCode" +
	            " left join Tb_Fun_Vocabulary l on " +
	            dbl.sqlToChar("a.FQuoteWay") +
	            " = l.FVocCode and l.FVocTypeCode = " +
	            dbl.sqlString(YssCons.YSS_FIX_QUOTEWAY) +
	            " left join Tb_Fun_Vocabulary m on " +
	            dbl.sqlToChar("a.FCalcInsWay") +
	            " = m.FVocCode and m.FVocTypeCode = " +
	            dbl.sqlString(YssCons.YSS_FIX_CALCINSWAY) +
	            " left join Tb_Fun_Vocabulary n on " +
	            dbl.sqlToChar("a.FInterestOrigin") +
	            " = n.FVocCode and n.FVocTypeCode = " +
	            dbl.sqlString(YssCons.YSS_FIX_INTORG) +
	            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) o on a.FCalcInsMeticDay = o.FCIMCode " + //sj 20071207 add
	            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) p on a.FCalcInsMeticBuy = p.FCIMCode " + //前台没有显示方法名称
	            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) q on a.FCalcInsMeticSell = q.FCIMCode " +
	            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) r on a.FCalcPriceMetic = r.FCIMCode " + // lzp  add  2007 12.13
	            " left join (select FCIMCode,FCIMName from Tb_Base_CalcInsMetic) s on a.FAmortization = s.FCIMCode " + // add by leeyu　合中保的版本时用的
	            /**shashijie 2012-2-9 STORY 1713 */
                " Left Join (Select Fcimcode, Fcimname From Tb_Base_Calcinsmetic) t On a.FTaskMoneyCode = t.Fcimcode " + //百元派息公式名称
    			/**end*/
	            buildFilterSql() +
	            ") y on x.FSecurityCode = y.FSecurityCode and x.FStartDate = y.FStartDate" +
	            " union";
    	}
    	
    	strSql = strSql.substring(0,strSql.length() - 5);
    	strSql = strSql + ") allData order by allData.FAssetGroupCode,allData.FCreateTime";
        
	        String sHeader = "";
	        String sShowDataStr = "";
	        String sAllDataStr = "";
	        String sVocStr = "";
	        StringBuffer bufShow = new StringBuffer();
	        StringBuffer bufAll = new StringBuffer();
	        ResultSet rs = null;
	        try {
	            sHeader = this.getListView1Headers() + "\t组合群代码";
	            rs = dbl.openResultSet(strSql);
	            while (rs.next()) {
	                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).append("\t" + rs.getString("FAssetGroupCode")).
	                    append(YssCons.YSS_LINESPLITMARK);
	
	                setSecurityAttr(rs);
	                this.assetGroupCode = rs.getString("FAssetGroupCode");
	                bufAll.append(this.buildRowStr()).append(YssCons.
	                    YSS_LINESPLITMARK);
	            }
	            if (bufShow.toString().length() > 2) {
	                sShowDataStr = bufShow.toString().substring(0,
	                    bufShow.toString().length() - 2);
	            }
	
	            if (bufAll.toString().length() > 2) {
	                sAllDataStr = bufAll.toString().substring(0,
	                    bufAll.toString().length() - 2);
	            }
	            VocabularyBean vocabulary = new VocabularyBean();
	            vocabulary.setYssPub(pub);
	            sVocStr = vocabulary.getVoc(YssCons.YSS_FIX_INTORG + "," +
	                                        YssCons.YSS_FIX_CALCINSWAY + "," +
	                                        YssCons.YSS_FIX_QUOTEWAY + "," +
	                                        YssCons.YSS_FIX_Level);
	            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
	                "\r\f" +
	                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
	        } catch (Exception e) {
	            throw new YssException("获取债券信息出错", e);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
    }
    //=================end=========================

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    /**
     * 从后台加载出我们跨组合群的内容
     * 修改人：panjunfang
     * 修改人时间:20090515
     * BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * @return String
     * @throws YssException
     */
    public String getListViewGroupData3() throws YssException {
        StringBuffer sGroups = new StringBuffer(); //定义一个字符用来保存执行后的结果传到前台
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码
        //分割组合群代码
        String[] assetGroupCodes = this.filterType.assetGroupCode.split(YssCons.YSS_GROUPSPLITMARK);
        //分割组合代码
        String[] strPortCodes = this.filterType.sPortCodes.split(YssCons.YSS_GROUPSPLITMARK);
        try {
            for (int i = 0; i < assetGroupCodes.length; i++) { //循环遍历每一个组合群
                this.assetGroupCode = assetGroupCodes[i]; //获取组合群代码
                pub.setPrefixTB(this.assetGroupCode); //将该组合群代码设为表前缀
                this.filterType.sPortCodes = strPortCodes[i]; //得到当前组合群下的组合代码
                String sGroup = this.getListViewData3(); //调用先前的实现方法
                sGroups.append(sGroup).append(YssCons.YSS_GROUPSPLITMARK); //返回结果用<-AGP->间隔
            }
            if (sGroups.length() > 7) {
                sGroups.delete(sGroups.length() - 7, sGroups.length()); //去除尾部多余的组合群解析符
            }
        } catch (Exception e) {
            throw new YssException("进行组合群循环处理时出错！", e);
        } finally {
            pub.setPrefixTB(sPrefixTB);
        }
        return sGroups.toString(); //返回到前台的结果
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }
    
    /**
     * 获取债券信息代码
     * add by lvhx 2010.06.24 MS01297 计息业务的明细通过业务日期和组合动态获取 
     *QDV4赢时胜（深圳）2010年06月02日01_A  
     */
    //edit by songjie 2013.08.22 BUG 9160 QDV4赢时胜(上海开发)2013年8月21日01_B 添加参数 transDate 业务日期
    public String getIncomeTypeData(java.util.Date transDate) throws YssException {
        ResultSet rs = null;
        StringBuffer strResult = new StringBuffer();
        //--- edit by songjie 2013.08.22 BUG 9160 QDV4赢时胜(上海开发)2013年8月21日01_B start---//
        StringBuffer sbSql = new StringBuffer();
        try{
        	//调度方案执行 收益计提获取证券代码逻辑 改为
        	//根据 已审核的债券信息设置的证券 是否有计息日前一日的相关组合群、相关组合 已审核库存数据 作为查询条件
        	
        	/**Start 20131009 modified by liubo.Bug #80413.QDV4赢时胜(上海)2013年09月27日01_B
        	 * 提出人指出，若某只新券（即之前没有库存）做T+0的DVP交易，前一日没有库存，这种情况下这只新券无法计提出利息
        	 * 因此需要在关联前一日库存的同时，关联交易子表*/
        	sbSql.append("select x.FSecurityCode from ")
	            .append(" (select FSecurityCode,FCheckState,max(FStartDate) as FStartDate from ")
	            .append(pub.yssGetTableName("Tb_Para_FixInterest"))
	            .append(" where fcheckstate = '1' ")
	            .append(" group by FSecurityCode,FCheckState) x ")
	            .append(" left join ")
	            .append(" (select FSecurityCode from " + pub.yssGetTableName("Tb_Stock_Security")) 
	            .append(" where FStorageDate = " + dbl.sqlDate(YssFun.addDay(transDate, -1))) 
	            .append(" and FPortCode = " + dbl.sqlString(this.sPortCodes) + " and FCheckState = 1) b ")
	            .append(" on x.FSecurityCode = b.FSecurityCode ")
	            .append(" left join ")
	            .append(" (select FSecurityCode from " + pub.yssGetTableName("tb_data_subtrade"))
	            .append(" where fbargaindate = " + dbl.sqlDate(transDate))
	            .append(" and FPortCode = " + dbl.sqlString(this.sPortCodes) + " ) c ")
	            .append(" on x.FSecurityCode = c.FSecurityCode ")
	            /**Start 20131017 modified by liubo.Bug #81302.QDV4赢时胜(上海)2013年10月16日01_B
	             * 增加银行间债券数据的判断与获取*/
	            .append(" left join (select FSecurityCode from " + pub.yssGetTableName("Tb_Data_IntBakBond"))
	            .append(" where FBargainDate = " + dbl.sqlDate(transDate))
	            .append(" and FPortCode = " + dbl.sqlString(this.sPortCodes) + " ) d ")
	            .append(" on x.FSecurityCode = d.FSecurityCode ")
	            .append(" where (x.FSecurityCode in b.FSecurityCode) or (x.FSecurityCode in c.FSecurityCode)")
	            .append(" or (x.FSecurityCode in d.FSecurityCode) ");
        		/**End 20131017 added by liubo.Bug #81302.QDV4赢时胜(上海)2013年10月16日01_B*/
        	
        	/**End 20131009 modified by liubo.Bug #80413.QDV4赢时胜(上海)2013年09月27日01_B*/
        	
	        rs = dbl.openResultSet(sbSql.toString());
	        //--- edit by songjie 2013.08.22 BUG 9160 QDV4赢时胜(上海开发)2013年8月21日01_B end---//
	        while(rs.next()){
	        	strResult.append(rs.getString("FSecurityCode")).append(",");
	        }
	        if(strResult.length() > 1){
        		strResult.delete(strResult.length() - 1, strResult.length());
        	}
	        return strResult.toString();
        } catch (Exception e) {
            throw new YssException("获取债券代码出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
	 * 20111205 modified by liubo.Story #1916
	 * 查询当前库中所有组合群
	 * return ResultSet
	 * @throws YssException 
	 */
	public String getAllAssetGroup() throws YssException{
		ResultSet rs=null;
		String sql=null;
		String FAssetGroupCode="";
		try{
			sql="select * from Tb_Sys_AssetGroup order by FAssetGroupCode";
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				FAssetGroupCode+=rs.getString("FAssetGroupCode")+"\t";
			}
			return FAssetGroupCode;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	public String getFTaskMoneyCode() {
		return FTaskMoneyCode;
	}

	public void setFTaskMoneyCode(String fTaskMoneyCode) {
		FTaskMoneyCode = fTaskMoneyCode;
	}

	public String getFTaskMoneyName() {
		return FTaskMoneyName;
	}

	public void setFTaskMoneyName(String fTaskMoneyName) {
		FTaskMoneyName = fTaskMoneyName;
	}
}
