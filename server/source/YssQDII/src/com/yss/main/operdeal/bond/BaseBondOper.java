package com.yss.main.operdeal.bond;

import java.math.BigDecimal;
import java.util.Date;

import com.yss.base.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title: BaseBondOper</p>
 * <p>Description:债券业务公共类  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */

public class BaseBondOper
    extends BaseCalcFormula implements IYssConvert {
//   * 付息频率@param intPl double
//   * 债券面值@param dubMz double
//   * 票面年利息@param dubLv double
//   * 付息天数@param intTs int
//   * 债券全价@param dubQj double
//   * 已付息次数@param intCs int
//   * 总付息次数@param intCount int
//   * 债券交易日@param dateNow Date
//   * 债券到期日@param dateEnd Date
    protected String securityCode = "";
    protected java.util.Date tradeDate; //买入债券的日期
    protected double intPl; //付息频率
    protected double dubMz; //债券面值
    protected double dubLv; //票面年利息
    protected int intTs; //付息天数
    protected double dubQj; //债券全价
    protected int intCs; //已付息次数
    protected int intCount; //总付息次数
    protected java.util.Date dateNow; //计息开始日
    protected java.util.Date dateEnd; //计息截止日
    protected String periodCode = ""; //期间代码
    protected double dubAmount = 0; //债券数量
    protected double dubFactor = 0; //报价因子
	protected String sOtherParams="";//保存债券参数 QDV4中保2010年03月03日01_A MS01009  获取版本信息  by leeyu 20100315
    // add by songjie 2009.12.17 
    //QDII国内：MS00847 
    //QDV4赢时胜（北京）2009年11月30日03_B 
    //用于判断是否是国内接口部分的调用 是的话  为 true 否则为 false
    private boolean fromDomestic = false;
    private String portCode = "";//组合代码
    
    /**shashijie 2012-2-3 STORY 1713 */
	public boolean flage = true;//判断是否使用优先级取值
	/**end*/

	/**
    * QDV4中保2010年03月03日01_A MS01009  获取版本信息  by leeyu 20100315
    * @return
    */
   public String getsOtherParams() {
		return sOtherParams;
	}

   /**
    * QDV4中保2010年03月03日01_A MS01009  获取版本信息  by leeyu 20100315
    * @param sOtherParams
    */
	public void setsOtherParams(String sOtherParams) {
		this.sOtherParams = sOtherParams;
	}
	public boolean getFromDomestic(){
    	return fromDomestic;
    }
    
    public void setFromDomestic(boolean fromDomestic){
    	this.fromDomestic = fromDomestic;
    }
    
    public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
    // add by songjie 2009.12.17 
    //QDII国内：MS00847 
    //QDV4赢时胜（北京）2009年11月30日03_B 
    //用于判断是否是国内接口部分的调用 是的话  为 true 否则为 false
    
	// add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
	private boolean fromBank = false;
	
    public boolean getFromBank() {
		return fromBank;
	}

	public void setFromBank(boolean fromBank) {
		this.fromBank = fromBank;
	}
	// add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B

	protected Object initObj;

    public BaseBondOper() {

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
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

    public void init(Object obj) throws YssException {
        initObj = obj;
    } // 抛出的异常类型改为YssException by caocheng 2009.01.22 MS00004 QDV4.1-2009.2.1_09A

    /**shashijie 2012-1-19 STORY 1713 */
    public void init2(Object obj,Date statDate,Date endDate,Date IssueDate,double FaceRate,
    		String fixInterest) throws YssException {
    	initObj = obj;
    }
    
    /**
     * init
     * 用来传递参数，可多次重载
     */
    public void init(String securityCode) throws YssException {
        this.securityCode = securityCode;
    }

    public void init(java.util.Date tradeDate, double intPl,
                     double dubMz, double dubLv, int intTs,
                     double dubQj, int intCs, int intCount,
                     java.util.Date dateNow, java.util.Date dateEnd) throws
        YssException {
        this.tradeDate = tradeDate;
        this.intPl = intPl;
        this.dubMz = dubMz;
        this.dubLv = dubLv;
        this.intTs = intTs;
        this.dubQj = dubQj;
        this.intCs = intCs;
        this.intCount = intCount;
        this.dateNow = dateNow;
        this.dateEnd = dateEnd;
    }

    public void init(java.util.Date dateNow, java.util.Date dateEnd,
                     double dubLv, String sPeriodCode, double dubAmount,
                     double dubFactor) throws YssException {
        this.dateNow = dateNow;
        this.dateEnd = dateEnd;
        this.dubLv = dubLv;
        this.periodCode = sPeriodCode;
        this.dubAmount = dubAmount;
        this.dubFactor = dubFactor;
    }

    /**
     * calBondInterest
     *计算债券利息
     */
    public double calBondInterest() throws YssException {
        return 0;
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRowAry = null;
        if (sRowStr != null && sRowStr.length() > 0) {
            sRowAry = sRowStr.split("\t");
            if (sRowAry.length == 7) {
                if (YssFun.isNumeric(sRowAry[0])) {
                    intPl = YssFun.toNumber(sRowAry[0]);
                }
                if (YssFun.isNumeric(sRowAry[1])) {
                    dubMz = YssFun.toNumber(sRowAry[1]);
                }
                if (YssFun.isNumeric(sRowAry[2])) {
                    dubLv = YssFun.toNumber(sRowAry[2]);
                }
                if (YssFun.isNumeric(sRowAry[3])) {
                    dubQj = YssFun.toInt(sRowAry[3]);
                }
                if (!sRowAry[4].equalsIgnoreCase("null") &&
                    YssFun.isDate(YssFun.formatDate(sRowAry[4]))) {
                    tradeDate = YssFun.toDate(YssFun.formatDate(sRowAry[4]));
                }
                if (!sRowAry[5].equalsIgnoreCase("null") &&
                    YssFun.isDate(YssFun.formatDate(sRowAry[5]))) {
                    dateNow = YssFun.toDate(YssFun.formatDate(sRowAry[5]));
                }
                if (!sRowAry[6].equalsIgnoreCase("null") &&
                    YssFun.isDate(YssFun.formatDate(sRowAry[6]))) {
                    dateEnd = YssFun.toDate(YssFun.formatDate(sRowAry[6]));
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(new BaseBondOper().day360(YssFun.toDate("2000-02-01"), YssFun.toDate("2000-03-01")));
        } catch (YssException ex) {
        }
    }

    /**
     *获取最近付息日期
     * 计息起始日@param dInsStartDate Date
     * 计息截止日@param dInsEndDate double
     * 结算日期@param dDate Date
     * 年付息频率@param iInsPl int
     * 标识@param sInd String Next-下一个派息入  Last-上一个派息日
     * @return Date
     */
    public java.util.Date getNearPayInsDate(java.util.Date dInsStartDate,
                                            java.util.Date dInsEndDate,
                                            java.util.Date dDate,
                                            int iInsPl, String sInd) {

        int iDisMonth = 0; //每次付息间隔月份
        int iPaidMonth = 0; //已付息月份
        int iCurPay = 0; //当前付息期
        java.util.Date dtResult = null;
        iDisMonth = 12 / iInsPl;

        if (sInd.equalsIgnoreCase("Next")) {
            dtResult = dInsStartDate;
            while (YssFun.dateDiff(dtResult, dDate) >= 0) {
                dtResult = YssFun.addMonth(dtResult, iDisMonth);
            }
        } else if (sInd.equalsIgnoreCase("Last")) {
            dtResult = dInsEndDate;
            while (YssFun.dateDiff(dtResult, dDate) <= 0) {
                dtResult = YssFun.addMonth(dtResult, -iDisMonth);
            }
        }
        return dtResult;

        //最近付息到期日计算公式
        /**
         * 已付息月份=(业务日期年-起息日年)*12+业务日期月-起息日月
         *  当前付息期=case when (已付息月份/每次付息间隔月份-int(已付息月份/每次付息间隔月份))>0 then
         *      int(已付息月份/每次付息间隔月份)+1 when (已付息月份/每次付息间隔月份-int(已付息月份/每次付息间隔月份))=0 and 业日的天数>起息日天数 then
         *      int(已付息月份/每次付息间隔月份)+1 else int(已付息月份/每次付息间隔月份) end
         * 最近付息到期日=起息日+当前付息期*每次付息间隔月份
         *
         */

//      iPaidMonth = (YssFun.getYear(dDate) - YssFun.getYear(dInsStartDate)) * 12 +
//            YssFun.getMonth(dDate) - YssFun.getMonth(dInsStartDate);
//
//      System.out.println(YssD.div(iPaidMonth,iDisMonth));
//      if ( ( (iPaidMonth / iDisMonth) - (iPaidMonth / iDisMonth)) >= 0) {
//         if (sInd.equalsIgnoreCase("Next")){
//            iCurPay = iPaidMonth / iDisMonth + 1;
//         }else if (sInd.equalsIgnoreCase("Last")){
//            iCurPay = iPaidMonth / iDisMonth;
//            if (YssD.div(iPaidMonth,iDisMonth)==0){
//
//            }
//         }
//      }
//      else {
//         if (sInd.equalsIgnoreCase("Next")){
//            iCurPay = iPaidMonth / iDisMonth;
//         }else if (sInd.equalsIgnoreCase("Last")){
//            iCurPay = iPaidMonth / iDisMonth - 1;
//         }
//      }
//      dtResult = YssFun.addMonth(dInsStartDate, iCurPay * iDisMonth);
//      if (YssFun.dateDiff(dInsEndDate, dtResult) > 0) {
//         dtResult = dInsEndDate;
//      }
//      return dtResult;
    }

    public long day360(java.util.Date dtStartDate, java.util.Date dtEndDate) {
        return day360(dtStartDate, dtEndDate, false);
    }

    public long day360(java.util.Date dtStartDate, java.util.Date dtEndDate, boolean bMethod) {
        int iMonth = 0;
        long lDays = 0;
        int iDisYear = 0;
        int iDisMonth = 0;
        int iSign = 1;
        java.util.Date tmpdate;
        if (YssFun.dateDiff(dtStartDate, dtEndDate) < 0) {
            tmpdate = dtStartDate;
            dtStartDate = dtEndDate;
            dtEndDate = tmpdate;
            iSign = -1;
        }
//      iDisYear = YssFun.getYear(dtEndDate) - YssFun.getYear(dtStartDate);
//      if (iDisYear>1){
//         dtStartDate = YssFun.addYear(dtStartDate,iDisYear-1);
//         lDays = iDisYear*360;
//      }
        iDisMonth = (YssFun.getYear(dtEndDate) - YssFun.getYear(dtStartDate)) * 12 +
            YssFun.getMonth(dtEndDate) - YssFun.getMonth(dtStartDate);
        if (iDisMonth > 1) {
            dtStartDate = YssFun.addMonth(dtStartDate, iDisMonth - 1);
            lDays = (iDisMonth - 1) * 30;
        }
        if (!bMethod) { //美国方法 (NASD)。如果起始日期是一个月的 31 号，则等于同月的 30 号。
            //如果终止日期是一个月的 31 号，并且起始日期早于 30 号，则终止日期等于下一个月的 1 号，否则，终止日期等于本月的 30 号。
            if ( (YssFun.getMonth(dtEndDate) - YssFun.getMonth(dtStartDate)) == 0) { //同月
                lDays = lDays + YssFun.dateDiff(dtStartDate, dtEndDate); //直接用月末-月初 天数
            } else { //不同月
                if (YssFun.getDay(dtStartDate) == YssFun.endOfMonth(dtStartDate)) { //如果起始日是月末的最后一天
                    lDays = lDays + (YssFun.endOfMonth(dtStartDate) - YssFun.getDay(dtStartDate)); //加上月初的天数
                } else {
                    lDays = lDays + (30 - YssFun.getDay(dtStartDate));
                }
                if (YssFun.getDay(dtEndDate) != YssFun.endOfMonth(dtEndDate)) { //如果终止日不是月末的最后最后一天
                    lDays = lDays + YssFun.getDay(dtEndDate); //加上起始日的天数
                } else {
                    lDays = lDays + YssFun.endOfMonth(dtEndDate);
                }
            }
        } else { //欧洲方法。起始日期和终止日期为一个月的 31 号，都将等于本月的 30 号。
            if (YssFun.getMonth(dtEndDate) - YssFun.getMonth(dtStartDate) == 0) { //同月
                if (YssFun.getDay(dtEndDate) > 30) {
                    lDays = lDays + (30 - YssFun.getDay(dtStartDate)); //月末天数大于30直接用30-月初天数
                } else {
                    lDays = lDays + YssFun.dateDiff(dtStartDate, dtEndDate); //月末天数-月初天数得实际天数
                }
            } else { //不同月
                if (YssFun.getDay(dtStartDate) <= 30) {
                    lDays = lDays + (30 - YssFun.getDay(dtStartDate)); //用30天-月初天数 得到月初天数
                }
                if (YssFun.getDay(dtEndDate) <= 30) {
                    lDays = lDays + YssFun.getDay(dtEndDate); //直接+月末天数
                } else {
                    lDays = lDays + 30; //直接+30
                }
            }
        }
        return lDays * iSign;
    }

    /**
     *获取计息次数
     * 计息起始日@param dInsStartDate Date
     * 结算日期@param dDate Date
     * 年付息频率@param iInsPl int
     * @return Date
     */
    public int getPaidInsTime(java.util.Date dInsStartDate,
                              java.util.Date dDate, int iInsPl) {
        int iDisMonth = 0;
        int iPayDisMonth = 0;
        int iResult = 0;
        iDisMonth = YssFun.dateDiff(dInsStartDate, dDate);
        iPayDisMonth = 12 / iInsPl;
        iResult = iDisMonth / iPayDisMonth;
        return iResult;
    }
    
    /**
     * add by songjie 2011.10.18 
     * 需求 #1245 QDV4易方达2011年6月20日01_A
     */
    public void insertDate()throws YssException{
    }
}
