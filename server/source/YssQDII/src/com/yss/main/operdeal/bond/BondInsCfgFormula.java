package com.yss.main.operdeal.bond;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.main.basesetting.CalcInsMeticBean;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadTypeBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.FixInterestBean;
import com.yss.main.parasetting.PeriodBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.main.storagemanage.SecRecPayBalBean;
import com.yss.main.storagemanage.SecurityStorageBean;
import com.yss.manager.SecRecPayStorageAdmin;
import com.yss.manager.SecurityStorageAdmin;
import com.yss.pojo.param.bond.YssBondIns;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssGlobal;
import com.yss.util.YssOperCons;

/**
 *
 * <p>Title: BaseBondOper</p>
 * <p>Description:通过利息公式中配置的公式计算债券利息  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */

//20130401 modified by liubo.Story #3714
//将派息判定时间由之前的实际结算日期变更为结算日期
public class BondInsCfgFormula
    extends BaseBondOper {
    private SecurityBean security;
    private YssBondIns bondIns;
    //edit by songjie 2013.04.02 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
   	public String sFIstg = "autoCalc";//判断版本 QDV4中保2010年03月03日01_A MS01009  获取版本信息  by leeyu 20100315
   	private HashMap hmBondParams=null;//保存债券参数 QDV4中保2010年03月03日01_A MS01009  获取版本信息  by leeyu 20100315
	//--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
	public static HashMap hmKey = null;
	public static HashMap hmSec = null;
	//--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
	
	private java.util.Date dInsStartDate = new Date();	//20130613 added by liubo.Story #3892.本期间计息起始日期
	
	
    public BondInsCfgFormula() {
    }

    public void init(Object obj) throws YssException {
        if (obj == null) {
            return;
        }
        bondIns = (YssBondIns) obj;
        this.sign = "(,),+,-,*,/,>,<,="; //添加了最后三个标记，为了在函数中直接判断。sj edit 20080804

        //------------------------设置债券的相关信息
        this.securityCode = bondIns.getSecurityCode();
        this.tradeDate = bondIns.getInsDate();
        try {
            getFixInterestInfo(securityCode, tradeDate, bondIns.getInsType());
            
            /**Start 20130910 added by liubo.Bug #9420.QDV4赢时胜(上海)2013年09月06日01_B
             * 当某只券的交易所既不是上交所也不是深交所时，就不要设置来源于国内接口*/
            if(security != null)
            {
            	if (!security.getExchangeCode().equalsIgnoreCase("CG") && !security.getExchangeCode().equalsIgnoreCase("CS"))
            	{
            		this.setFromDomestic(false);
            	}
            }
            /**End 20130910 added by liubo.Bug #9420.QDV4赢时胜(上海)2013年09月06日01_B*/
            
         	//QDV4中保2010年03月03日01_A MS01009  获取版本信息  by leeyu 20100315 合并太平版本
            //--- edit by songjie 2013.04.02 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            BondInsCfgFormula bonds = null;
            if(BondAssist.hmSec != null){
            	String[] securityCodes = securityCode.split(",");
            	for(int i = 0; i < securityCodes.length; i++){
    				if (BondAssist.hmSec != null && BondAssist.hmSec.get(securityCodes[i]) != null) {
    					if (BondAssist.hmParam != null && BondAssist.hmParam.get("sFIstg") != null) {
    						sFIstg = (String) BondAssist.hmParam.get("sFIstg");
    						if(this.hmSec != null && this.hmSec.get(securityCodes[i]) != null){
    							bonds = (BondInsCfgFormula)this.hmSec.get(securityCodes[i]);
    							bonds.sFIstg = this.sFIstg;
    							this.hmSec.put(securityCodes[i], bonds);
    						}
    					}
    				}
            	}
            }else{
					CtlPubPara clPub = new CtlPubPara();
					clPub.setYssPub(pub);
					sFIstg = clPub.getIncomeFIDateCalcType();
					hmBondParams = this.getBondThisStartAndEndDate();
				
            }

         	//--- edit by songjie 2013.04.02 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
         	//QDV4中保2010年03月03日01_A MS01009
        } catch (Exception e) {
            throw new YssException("系统进行债券计息初始化时出现异常!\n", e); // by 曹丞 2009.01.23 债券计息初始化异常信息 MS00004 QDV4.1-2009.2.1_09A
        }
    }
    
    /**shashijie 2012-1-19 STORY 1713 初始债券*/
    public void init2(Object obj,Date statDate,Date endDate,Date IssueDate,double FaceRate,String fixInterest) throws YssException {
    	if (obj == null) {
            return;
        }
        bondIns = (YssBondIns) obj;
        this.sign = "(,),+,-,*,/,>,<,=";//添加了最后三个标记，为了在函数中直接判断

        //------------------------设置债券的相关信息
        this.securityCode = bondIns.getSecurityCode();
        this.tradeDate = bondIns.getInsDate();
        this.dInsStartDate = statDate;		//20130613 added by liubo.Story #3892.本期间计息起始日期
        try {
            getFixInterestInfo2(securityCode,statDate,endDate,IssueDate,FaceRate,fixInterest);
        } catch (Exception e) {
            throw new YssException("初始化浮债时出现异常!\n", e);
        }
	}

	public double calBondInterest() throws YssException {
		this.setSecurityCode(this.securityCode);
        return this.calcFormulaDouble();
    }

    /**
     * 已计溢价额
     * @throws YssException
     * @return double
     */
    protected double getFixIntersetPremium() throws YssException {
        double fixPrepaid = 0.0;
        try {
            operFun.setYssPub(pub);
            //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
			fixPrepaid = operFun.getFixInterestBalance(this.tradeDate, 
			        this.securityCode, 
					bondIns.getPortCode(), bondIns.getAnalysisCode1(), 
					bondIns.getAnalysisCode2(), bondIns.getAnalysisCode3(),
					YssOperCons.Yss_ZJDBLX_Premium, 
					YssOperCons.YSS_ZJDBZLX_Premium_Fix, bondIns.getAttrClsCode(),
					bondIns.getInvestType());// add by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
        } catch (Exception e) {
            throw new YssException(e);
        }
        return fixPrepaid;
    }

    /**
     * 已计折价额
     * @throws YssException
     * @return double
     */
    protected double getFixIntersetDiscounts() throws YssException {
        double fixPrepaid = 0.0;
        try {
            operFun.setYssPub(pub);
            //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
			fixPrepaid = operFun.getFixInterestBalance(this.tradeDate, 
			        this.securityCode, 
					bondIns.getPortCode(), bondIns.getAnalysisCode1(), 
					bondIns.getAnalysisCode2(), bondIns.getAnalysisCode3(),
					YssOperCons.Yss_ZJDBLX_Discounts, 
					YssOperCons.YSS_ZJDBZLX_Discounts_Fix, 
					bondIns.getAttrClsCode(),
					bondIns.getInvestType());// add by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
        } catch (Exception e) {
            throw new YssException(e);
        }
        return fixPrepaid;
    }

    protected double getFixInterest() throws YssException {
        double fixInterest = 0;
        try {
            operFun.setYssPub(pub);
            //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
            fixInterest = operFun.getFixInterestBalance(this.tradeDate,
                this.securityCode,
                bondIns.getPortCode(), bondIns.getAnalysisCode1(),
                bondIns.getAnalysisCode2(), bondIns.getAnalysisCode3(),
                YssOperCons.YSS_ZJDBLX_Rec,
                YssOperCons.YSS_ZJDBZLX_FI_RecInterest,
                bondIns.getAttrClsCode(),
				bondIns.getInvestType());// add by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
            return fixInterest;
        } catch (Exception e) {
            throw new YssException(e);
        }

    }

    public Object getExpressValue(String sExpress, ArrayList alParams) throws
        YssException {
        Object objResult = null;
        if (sExpress.equalsIgnoreCase("Day360")) { //Excel中的Day360公式
            if (alParams.size() == 3) {
                objResult = new Long(day360( (java.util.Date) alParams.get(0),
                                            (java.util.Date) alParams.get(1),
                                            Boolean.getBoolean( (String) alParams.
                    get(2))));
            } else if (alParams.size() == 2) {
                objResult = new Long(day360( (java.util.Date) alParams.get(0),
                                            (java.util.Date) alParams.get(1)));
            }
        } else if (sExpress.equalsIgnoreCase("StgAmount")) { //获取库存数量
            if (alParams.size() == 3) {
                objResult = new Double(getStgAmount( (java.util.Date) alParams.get(
                    0), (String) alParams.get(1), (String) alParams.get(2)));
            } else if (alParams.size() == 2) {
                objResult = new Double(getStgAmount( (java.util.Date) alParams.get(
                    0), (String) alParams.get(1)));
            }
        } else if (sExpress.equalsIgnoreCase("FIInsBal")) { //获取债券利息余额
            if (alParams.size() == 3) {
                objResult = new Double(getFIInsBal( (java.util.Date) alParams.get(0),
                    (String) alParams.get(1),
                    (String) alParams.get(2)));
            } else if (alParams.size() == 2) {
                objResult = new Double(getFIInsBal( (java.util.Date) alParams.get(0),
                    (String) alParams.get(1)));
            }
        } else if (sExpress.equalsIgnoreCase("FIInsTrade")) { //获取债券交易的利息
            if (alParams.size() == 1) {
                objResult = new Double(getFIInsTrade( (java.util.Date) alParams.get(
                    0)));
            }
            //-------MS00209 QDV4建行2009年1月20日01_B  -----------------------------//
            else if (alParams.size() == 2) { //当公式中有第二个参数,一个boolean值。
                objResult = new Double(getFIInsTrade( (java.util.Date) alParams.get(
                    0), new Boolean( (String) alParams.get(1)).booleanValue())); //将boolean值传入公式中，以便此公式的重载方法可以调用。
            }
            //----------------------------------------------------------------------//
            //----MS00255 QDV4建行2009年2月17日02_B sj modified,添加对是否只在交易日当日获取买卖利息的判断。
            else if (alParams.size() == 3) {
                objResult = new Double(getFIInsTrade( (java.util.Date) alParams.get(
                    0), new Boolean( (String) alParams.get(1)).booleanValue(), (String) alParams.get(2)));
            }
            //----------------------------------------------------------------------//
			//添加参数条件 QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318 合并太平版本代码
         	else if(alParams.size() == 4){
        		 objResult = new Double(getFIInsTrade( (java.util.Date) alParams.get(
                	     0), new Boolean( (String) alParams.get(1)).booleanValue(),(String)alParams.get(2),
                    	 (String)alParams.get(3)));
         	}else if(alParams.size() == 5){ //add by fj 2012.03.12
	       		 objResult = new Double(getFIInsTrade( (java.util.Date) alParams.get(
	            	     0), new Boolean( (String) alParams.get(1)).booleanValue(),(String)alParams.get(2),
	                	 (String)alParams.get(3),(String)alParams.get(4)));
	     	}
         	//QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
        }
        //add by luopc QDV4易方达2011年6月9日02_A
        else if (sExpress.equalsIgnoreCase("FIInsTradeD")){
        	 //获取债券交易的利息
            
            if (alParams.size() == 3) {
                objResult = new Double(getFIInsTradeD( (java.util.Date) alParams.get(
                    0), new Boolean( (String) alParams.get(1)).booleanValue(), (String) alParams.get(2),""));
            }
            //----------------------------------------------------------------------//
			//添加参数条件 QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318 合并太平版本代码
         	else if(alParams.size() == 4){
        		 objResult = new Double(getFIInsTrade( (java.util.Date) alParams.get(
                	     0), new Boolean( (String) alParams.get(1)).booleanValue(),(String)alParams.get(2),
                    	 (String)alParams.get(3)));
         	}
         	//QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
        
        }
        else if (sExpress.equalsIgnoreCase("FIInsUncheckD")){//QDV4易方达2011年6月9日02_A
        	if (alParams.size() == 1) {
	        	objResult=new Double(getFIInsUncheckD( (java.util.Date) alParams.get(
	           	     0)));
        	}else if(alParams.size() == 2) {
	        	objResult=new Double(getFIInsUncheckD( (java.util.Date) alParams.get(
		           	     0),(String)alParams.get(1)));
        	}
        }
        
        else if (sExpress.equalsIgnoreCase("yearNum")){//QDV4易方达2011年6月9日02_A
        	if(alParams.size()==3){
        		objResult=new Double(yearNum((Date)alParams.get(0), (Date)alParams.get(1),new Boolean((String)alParams.get(2)).booleanValue()));     		
        	}
        	if(alParams.size()==2){
        		objResult=new Double(yearNum((Date)alParams.get(0), (Date)alParams.get(1)));     		
        	}
        	
        	
        }// add by luopc 
        
        ///add by luopc,h获取当日交易当日清算的数量  QDV4易方达2011年6月9日02_A
        else if(sExpress.equalsIgnoreCase("TradeClearAmountD")){
           if (alParams.size() == 1) {
               objResult = new Double(this.getTradeClearAmountD( (java.util.Date)
                   alParams.get(0)));
           }
       
       }
        else if (sExpress.equalsIgnoreCase("FIInsTradeSettleD")){//QDV4易方达2011年6月9日02_A
            if (alParams.size() == 1) {
                objResult = new Double(this.getFIInsTradeSettleD( (java.util.Date)
                    alParams.get(0)));
            }        	
        }
        else if (sExpress.equalsIgnoreCase("isHoliday")){//QDV4易方达2011年6月9日02_A
        	objResult=new Double(this.isHoliday((java.util.Date)alParams.get(0)));
        }
             
        ///add by luopc 
        else if(sExpress.equalsIgnoreCase("NoClearFiAmountD")){//QDV4易方达2011年6月9日02_A
        	 //获取未清算的债券数量
            if (alParams.size() == 1) {
                objResult = new Double(this.getNoClearFiAmountD( (java.util.Date)
                    alParams.get(0)));
            } 
        }
		else if(sExpress.equalsIgnoreCase("thisFIInsBal")){	//获取当日的历史库存利息余额 (当日历史库存利息余额＝昨日库存利息余额+当日买入利息+当日转货流入利息) by leeyu add 20100423
    	  if(alParams.size()==3){
    		  objResult = new Double(getThisFIInsBal((java.util.Date)alParams.get(0),
    				  (String)alParams.get(1), 
    				  (String) alParams.get(2)));
    	  }
        }
		
		 else if (sExpress.equalsIgnoreCase("FIInsAllTrade")){//获取所有债券交易产生的利息 包括网上网下 2009.08.27 蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
            objResult = new Double(getFIInsAllTrade((java.util.Date) alParams.get(0)));
        }
        else if (sExpress.equalsIgnoreCase("NoClearFiAmount")) { //获取未清算的债券数量
            if (alParams.size() == 1) {
                objResult = new Double(this.getNoClearFiAmount( (java.util.Date)
                    alParams.get(0)));
            } else if (alParams.size() == 2) { //2008.06.03 蒋锦 添加 当参数为两个的情况
                //--MS00293 QDV4赢时胜（上海）2009年3月6日01_AB  --------------------------
                if (alParams.get(1) instanceof Boolean) { //当第二个参数为boolean值
                    objResult = new Double(this.getNoClearFiAmount( (java.util.Date)
                        alParams.get(0), ( (Boolean) alParams.get(1)).booleanValue()));
                } else if (alParams.get(1) instanceof String) { //当第二个参数为string
                    objResult = new Double(this.getNoClearFiAmount( (java.util.Date)
                        alParams.get(0), (String) alParams.get(1)));
                }
                //----------------------------------------------------------------------
            }else if(alParams.size() == 4){ //fj 2012.03.14
            	objResult = new Double(this.getNoClearFiAmount( (java.util.Date)alParams.get(0), Boolean.parseBoolean((String) alParams.get(1)), 
            			(String) alParams.get(2), (String) alParams.get(3)));
            }
        }else if (sExpress.equalsIgnoreCase("TDayTradeAmount")){//获取T日债券的交易数量 2009-1-14 蒋锦 QDV4赢时胜（上海）2010年1月12日02_B
        	if(alParams.size() == 1){
        		objResult = new Double(this.getTDayTradeAmount((java.util.Date) alParams.get(0), false));
        	} else if(alParams.size() == 2){
        		objResult = new Double(
        							this.getTDayTradeAmount(
        									(java.util.Date) alParams.get(0), 
        									new Boolean((String) alParams.get(1)).booleanValue()));
        	}        	
        } else if(sExpress.equalsIgnoreCase("BeforeHoildayTradeAmount")){//获取节假日前一工作日的交易数量2010-01-14 蒋锦 添加 QDV4赢时胜（上海）2010年1月12日02_B
        	if(alParams.size() == 1){
        		objResult = new Double(this.getBeforeHoildayTradeAmount((java.util.Date) alParams.get(0)));
        	}
        } else if(sExpress.equalsIgnoreCase("BeforeHoildayFiInterest")){//获取节假日前一工作日的交易利息2010-01-14 蒋锦 添加 QDV4赢时胜（上海）2010年1月12日02_B
        	if(alParams.size() == 1){
        		objResult = new Double(this.getBeforeHoildayFiInterest((java.util.Date) alParams.get(0)));
        	}        	
        }else if (sExpress.equalsIgnoreCase("NoClearFiInterest")) {
            if (alParams.size() == 1) {
                objResult = new Double(this.getNoClearFiInterest( (java.util.Date)
                    alParams.get(0)));
            } else if (alParams.size() == 3) {
                objResult = new Double(this.getNoClearFiInterest( (java.util.Date)
                    alParams.get(0), ( new Boolean((String) alParams.get(1))).booleanValue(), (String) alParams.get(2)));
            }
        } else if (sExpress.equalsIgnoreCase("FactorU30")) { //计算按360日整除的日期
            if (alParams.size() == 2) {
                objResult = new Double(this.factorU30( (java.util.Date) alParams.
                    get(0),
                    (java.util.Date)
                    alParams.get(1)));
            }else if (alParams.size() == 3) {//BUG 2361  QDV4建行2011年08月02日02_B panjunfang add 20110810
                objResult = new Double(this.factorU30( (java.util.Date) alParams.
                    get(0),
                    (java.util.Date)
                    alParams.get(1),Boolean.getBoolean((String)alParams.get(2))));
            }
        } else if (sExpress.equalsIgnoreCase("FactorActual")) { //分别计算平闰年的日期
            if (alParams.size() == 2) {
                objResult = new Double(this.factorActual( (java.util.Date) alParams.
                    get(0), (java.util.Date) alParams.get(1)));
            }
        } else if (sExpress.equalsIgnoreCase("CompareDate")) {
            objResult = this.compareDate( (java.util.Date) alParams.get(0),
                                         (String) alParams.get(1),
                                         (java.util.Date) alParams.get(2));
        }
        else if (sExpress.equalsIgnoreCase("isEndDateOfFeb")){ //判断是否是二月最后一天 QDV4太平2011年03月11日01_B
        	 objResult = new Double(this.isEndDateOfFeb((java.util.Date) alParams.
                    get(0), (java.util.Date) alParams.get(1)));
         }
        //add by xuqiji:QDV4赢时胜（上海）2009年4月30日02_B  20090507 MS00429 通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误
        //利息设置模块，利息算法新增一个标记，参数为日期
        else if (sExpress.equalsIgnoreCase("FIntegrateFix")) {
            objResult = new Double(this.getIntegrateFix( (java.util.Date) alParams.get(0)));
        }
        //-------------------------------------------------end----------------------------------------------------------//
        //获取国内每百元债券利息 2009.08.10 蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        else if(sExpress.equalsIgnoreCase("IntAccPer100")){
            objResult = this.getIntAccPer100((java.util.Date) alParams.get(0));
        }
        //---MS00847 QDV4赢时胜（北京）2009年11月30日03_B add by songjie 2009.12.18 用于获取国内接口债券的计息天数---//
        else if (sExpress.equalsIgnoreCase("DomesticDays")){
        	objResult = new Double(this.getDomesticDays(Boolean.getBoolean((String)alParams.get(0))));
        }
        //---MS00847 QDV4赢时胜（北京）2009年11月30日03_B add by songjie 2009.12.18 ---//
        //----------20100329 蒋锦 国内 MS00955 添加 获取开始日期到结束日期之间的2月29日的天数------------//
        else if(sExpress.equalsIgnoreCase("CheckRN")){
        	objResult = new Double(this.checkRN((java.util.Date) alParams.get(0), (java.util.Date) alParams.get(1)));
        }
        //------------------------------------------------------------------------------//
        //add by yanghaiming 20110217 #582 30E/360计算计息天数算法
        else if (sExpress.equalsIgnoreCase("GetEDay")){
        	if (alParams.size() == 2) {
        		objResult = this.getEDay((java.util.Date) alParams.get(0),(java.util.Date) alParams.get(1));
        	}
        }
      //add by yanghaiming 20110217 #582 30U/360计算计息天数算法
        else if (sExpress.equalsIgnoreCase("GetUDay")){
        	if (alParams.size() == 2) {
        		objResult = this.getUDay((java.util.Date) alParams.get(0),(java.util.Date) alParams.get(1));
        	}
      //add by jsc 20120801 STORY #898 添加 德国、美国市场采用的利息因子计算方法
        }else if(sExpress.equalsIgnoreCase("factorUSA")){
        	if (alParams.size() == 2) {
                objResult = new Double(this.factorUSA( (java.util.Date) alParams.
                    get(0),
                    (java.util.Date)
                    alParams.get(1)));
            }else if (alParams.size() == 3) {
                objResult = new Double(this.factorUSA( (java.util.Date) alParams.
                    get(0),
                    (java.util.Date)
                    alParams.get(1),Boolean.getBoolean((String)alParams.get(2))));
            }
        }else if(sExpress.equalsIgnoreCase("factorISMA")){
        	if (alParams.size() == 2) {
                objResult = new Double(this.factorISMA( (java.util.Date) alParams.
                    get(0),
                    (java.util.Date)
                    alParams.get(1)));
            }else if (alParams.size() == 3) {
                objResult = new Double(this.factorISMA( (java.util.Date) alParams.
                    get(0),
                    (java.util.Date)
                    alParams.get(1),Boolean.getBoolean((String)alParams.get(2))));
            }
        }else if(sExpress.equalsIgnoreCase("factorEPlus")){
        	if (alParams.size() == 2) {
                objResult = new Double(this.factorEPlus( (java.util.Date) alParams.
                    get(0),
                    (java.util.Date)
                    alParams.get(1)));
            }else if (alParams.size() == 3) {
                objResult = new Double(this.factorEPlus( (java.util.Date) alParams.
                    get(0),
                    (java.util.Date)
                    alParams.get(1),Boolean.getBoolean((String)alParams.get(2))));
            }
        }
		/**Start 20131117 added by liubo.Story #13693变更。强制转换币种的关键字*/
        else if(sExpress.equalsIgnoreCase("RateCalcActually"))
        {
        	objResult = getRateCalcActually((String)alParams.get(0),(String)alParams.get(1));
        }
		/**End 20131117 added by liubo.Story #13693变更。强制转换币种的关键字*/
        
        return objResult;
    }
	/**
	 * 20131117 added by liubo.Story #13693变更。大致内容为债券的买入数据中，应收利息既不是交易币种也不是计息币种。
	 * 这种情况下，需要新建关键字，给定汇率强制进行转换
	 * @param sCurCuryCode		需要被转换的币种，即交易数据中应收利息的币种
	 * @param sTargetCuryCode	转换的目标币种
	 * @return
	 * @throws YssException
	 */
	public double getRateCalcActually(String sCurCuryCode,String sTargetCuryCode) throws YssException
	{
		double dReturn = 1;
		double dCurCuryRate = 1;
		double dTargetCuryRate = 1;
		try
		{
			if (sCurCuryCode != null && !sCurCuryCode.trim().equals(""))
			{

				dCurCuryRate = this.getSettingOper().getCuryRate(//基础汇率
						this.tradeDate,//汇率日期
						"",//基础汇率来源
						"",//基础汇率来源字段
						"",//组合来源
						"",//组合来源字段
						sCurCuryCode,//币种(原币,本币)
						bondIns.getPortCode(),//组合
						YssOperCons.YSS_RATE_BASE);//汇率标示
			}
			
			if (sTargetCuryCode != null && !sTargetCuryCode.trim().equals(""))
			{
				dTargetCuryRate = this.getSettingOper().getCuryRate(//基础汇率
						this.tradeDate,//汇率日期
						"",//基础汇率来源
						"",//基础汇率来源字段
						"",//组合来源
						"",//组合来源字段
						sTargetCuryCode,//币种(原币,本币)
						bondIns.getPortCode(),//组合
						YssOperCons.YSS_RATE_BASE);//汇率标示
			}
			
			dReturn = YssD.div(dCurCuryRate, dTargetCuryRate);
		}
		catch(Exception ye)
		{
			throw new YssException("获取实时汇率出错：" + ye.getMessage());
		}
		
		return dReturn;
	}

	/**
	 * add by songjie 2013.03.27 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
	 * 获取 票面金额
	 * 
	 * @return
	 * @throws YssException
	 */
	public BigDecimal getFaceValue() throws YssException {
		BigDecimal obj = new BigDecimal(0);
		ResultSet rs = null;
		String strSql = "";
		try {
			strSql = " select * from " + pub.yssGetTableName("Tb_Para_InterestTime") + " where FSecurityCode = "
					+ dbl.sqlString(security.getSecurityCode()) + " and " + dbl.sqlDate(bondIns.getInsDate())
					+ " between FInsStartDate and FInsEndDate ";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				obj = BigDecimal.valueOf(YssD.add(rs.getDouble("FPayMoney"), rs.getDouble("FRemainMoney")));
			}

			return obj;
		} catch (Exception e) {
			throw new YssException("获取债券票面金额出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
    public Object getKeywordValue(String sKeyword) throws YssException {
        Object objResult = null;

        if (security != null) {
            if (sKeyword.equalsIgnoreCase("FaceValue")) { //票面金额
            	//---add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            	if (this.hmSec != null && this.hmSec.get(security.getSecurityCode()) != null) {
            		BondInsCfgFormula bonds = (BondInsCfgFormula) hmSec.get(security.getSecurityCode());
            		objResult = bonds.security.getFixInterest().getStrFaceValue();
            	}else{
					if (bondIns.getFaceValue() != null) {
						objResult = bondIns.getFaceValue();
					} else {
						objResult = getFaceValue();
					}
            	}
            	//---add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            	//--- delete by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
//                objResult = security.getFixInterest().getStrFaceValue() == null ?
//                    new java.math.BigDecimal(0) :
//                    security.getFixInterest().getStrFaceValue(); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
            	//--- delete by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            } else if (sKeyword.equalsIgnoreCase("FactRate")) { //实际利率
                objResult = new Double(security.getFixInterest().getDFactRate());
            } else if (sKeyword.equalsIgnoreCase("FaceRate")) { //年利率
            	/**shashijie 2012-2-3 STORY 1713 获取利率*/
            	if (this.flage) {
            		objResult = getStrFaceRate();
				} else {
					objResult = security.getFixInterest().getStrFaceRate()==null ?
	                		 new java.math.BigDecimal(0) :
	                			 security.getFixInterest().getStrFaceRate();// bug 2381 by zhouwei 20111111
				}
				/**end*/
            } else if (sKeyword.equalsIgnoreCase("InsFrequency")) { //付息频率
                objResult = security.getFixInterest().getStrInsFrequency()==null ?
                		new java.math.BigDecimal(0) :
                			security.getFixInterest().getStrInsFrequency();// bug 2381 by zhouwei 20111111
            } else if (sKeyword.equalsIgnoreCase("InsStartDate")) { //计息起始日
                objResult = security.getFixInterest().getDtStartDate();
            } else if (sKeyword.equalsIgnoreCase("InsEndDate")) { //计息截至日
                objResult = security.getFixInterest().getDtInsEndDate();
            } else if (sKeyword.equalsIgnoreCase("ThisInsStartDate")) { //本计息期间计息起始日  2008.07.23 蒋锦 添加
            	/**shashijie 2012-2-3 STORY 1713 获取计息起始日,优先获取设置的*/
				if (flage) {
					objResult = getDtThisInsStartDate();
				} else {
					//QDV4中保2010年03月03日01_A MS01009 by leeyu 合并太平版本代码
	        	 	if(sFIstg.equalsIgnoreCase("autoCalc")){
	        			 objResult = security.getFixInterest().getDtThisInsStartDate();
	        		 }else{
	        			 if(hmBondParams.get("ThisInsStartDate")!=null&& !sOtherParams.equalsIgnoreCase("true")){
	        				 objResult = YssFun.toDate(String.valueOf(hmBondParams.get("ThisInsStartDate")));
	        		 	}else{
	        				 objResult = security.getFixInterest().getDtThisInsStartDate();
	        		 	}
	        	 	}
	        	 	//QDV4中保2010年03月03日01_A MS01009 by leeyu
				}
				/**end*/
            } else if (sKeyword.equalsIgnoreCase("ThisInsEndDate")) { //本计息期间计息截止日  2008.07.23 蒋锦 添加
            	/**shashijie 2012-2-3 STORY 1713 获取计息截止日,优先获取设置的 */
				if (flage) {
					objResult = getDtThisInsEndDate();
				} else {
					//QDV4中保2010年03月03日01_A MS01009 by leeyu 合并太平版本代码
	        	 	if(sFIstg.equalsIgnoreCase("autoCalc")){
	        		 	objResult = security.getFixInterest().getDtThisInsEndDate();
		        	 }else{
	    	    		 if(hmBondParams.get("ThisInsEndDate")!=null&& !sOtherParams.equalsIgnoreCase("true")){
	        				 objResult = YssFun.toDate(String.valueOf(hmBondParams.get("ThisInsEndDate")));
	        			 }else{
	        				 objResult = security.getFixInterest().getDtThisInsEndDate();
	        		 	}
	        	 	}
	        	 	//QDV4中保2010年03月03日01_A MS01009 by leeyu
				}
				/**end*/
            } 
            
            //Story #2933
            //根据付息期间是否包含2月29日这个日期来获取计息天数
            //如付息期间包含2月29日，则返回366；如付息期间不包含2月29日，则返回365。 
            //================================
            else if (sKeyword.equalsIgnoreCase("DynamicDays"))
            {
            	objResult = DynamicDays();
            }
            //================end================
            
            else if (sKeyword.equalsIgnoreCase("LastPaidInsDate")) { //上一派息日
                Double ned = new Double(this.intPl);
                objResult = this.getNearPayInsDate(this.dateNow, this.dateEnd,
                    this.tradeDate, ned.intValue(),
                    "Last");
            } else if (sKeyword.equalsIgnoreCase("NextPaidInsDate")) { //下一派息日
                Double ned = new Double(this.intPl);
                objResult = this.getNearPayInsDate(this.dateNow, this.dateEnd,
                    this.tradeDate, ned.intValue(),
                    "Next");
            } else if (sKeyword.equalsIgnoreCase("Amount")) { //计息数量
                objResult = new Double(bondIns.getInsAmount());
            } else if (sKeyword.equalsIgnoreCase("CashDate")) { //兑付日期
                objResult = security.getFixInterest().getDtInsCashDate();
            }
            //2008.05.15 蒋锦 添加
            else if (sKeyword.equalsIgnoreCase("IssuePrice")) { //发行价格
                objResult = security.getFixInterest().getStrIssuePrice() == null ?
                    new java.math.BigDecimal(0) :
                    security.getFixInterest().getStrIssuePrice(); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
            } else if (sKeyword.equalsIgnoreCase("LastInsEndDate")) { //计息截至日前一天,在贴现债中有此运用。sj edit 20080710
                objResult = YssFun.addDay(security.getFixInterest().getDtInsEndDate(),
                                          -1);
            } else if (sKeyword.equalsIgnoreCase("FormulaRate")) { //根据债券比率代码取相应的比率
                objResult = new Double(getPerFormulaRate());
            } else if (sKeyword.equalsIgnoreCase("isnDatesub1")) { //计息日减一天。sj edit 20080729.暂无bug编号。
                objResult = YssFun.addDay(bondIns.getInsDate(), -1);
            } else if (sKeyword.equalsIgnoreCase("isnDateadd1")) { //计息日加一天。sj edit 20080729
            	//modify by fangjiang 2013.04.10 债券优化
            	if("Day".equalsIgnoreCase(bondIns.getInsType())){
            		objResult = YssFun.addDay(bondIns.getInsDate(), 1);
            	}else {
            		if(("CG").equalsIgnoreCase(security.getExchangeCode()) || ("CS").equalsIgnoreCase(security.getExchangeCode())){//上交所、 深交所
            			objResult = YssFun.addDay(bondIns.getInsDate(), 1);
            		}else{
            			objResult = bondIns.getInsDate();
            		}
            	}                
            } else if (sKeyword.equalsIgnoreCase("InsDays")) { //获取计息年天数.
                objResult = new Double(getInsDays());
            } else if (sKeyword.equalsIgnoreCase("getDomesticStdIns")){ //国内标准利息算法 2009.07.16 蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                objResult = new Double(getDomesticStdIns(bondIns.getInsAmount(), tradeDate));
            }
			else if(sKeyword.equalsIgnoreCase("checkBuySecurity")){ //判断当日是否做过买入证券处理 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
       	  		objResult = new Double(this.checkBuySecurity());
         	}
            //-------add by songjie 2009-12-17 QDV4赢时胜（北京）2009年11月30日03_B 用于获取或内接口计算债券利息时获取税前 或 税后票面利率 ----//
            else if (sKeyword.equalsIgnoreCase("DomesticFaceRate")){
            	objResult = new Double(getDomesticFaceRate(bondIns.getIsBeforeRate()));
            } 
            //add by songjie 2010.03.27
            else if(sKeyword.equalsIgnoreCase("isRate100")){
            	objResult = Boolean.valueOf(getIsRate100(bondIns.getIsRate100())) ;
            }
            //add by songjie 2010.03.27
            else if (sKeyword.equalsIgnoreCase("DomesticPeriodDays")){//付息周期的天数 = 本计息期间计息截止日 - 本计息期间计息起始日
            	objResult = new Double(getDomesticPeriodDays());
            }
            else if (sKeyword.equalsIgnoreCase("DomesticYears")){//债券起息日至估值日的整年数
            	objResult = new Double(this.getDomesticYears());
            }
            else if (sKeyword.equalsIgnoreCase("DomesticCashDays")){//债券起息日至估值日的整年数
            	objResult = new Double(this.getDomesticCashDays());
            }
            else if(sKeyword.equalsIgnoreCase("checkPreBuySecurity")){ //判断昨日是否做过买入证券处理 add by wangzuochun 2010.10.13 MS01837    计提债券计息有误    QDV4太平2010年10月11日01_B    
        	  	objResult = new Double(this.checkPreBuySecurity());
            }else if (sKeyword.equalsIgnoreCase("cpiPrice")){//add by yanghaiming 20110212 #461 浮动CPI
            	objResult = new Double(bondIns.getCpiPrice());
            }else if (sKeyword.equalsIgnoreCase("BaseCpiPrice")){//add by yanghaiming 20110212 #461 基础CPI
            	objResult = new Double(security.getFixInterest().getBaseCPI());
            }
            //-------add by songjie 2009-12-17 QDV4赢时胜（北京）2009年11月30日03_B 用于获取或内接口计算债券利息时获取税前 或 税后票面利率 ----//
            //add by zhouwei 20120217 保留位数，交易所为8，银行间为12 start-------
            else if(sKeyword.equalsIgnoreCase("roundCode")){
            	if(("CG").equalsIgnoreCase(security.getExchangeCode()) || ("CS").equalsIgnoreCase(security.getExchangeCode())){//上交所、 深交所
            		objResult=new Double(8);
            	}else if(("CY").equalsIgnoreCase(security.getExchangeCode())){//银行间
            		objResult=new Double(12);
            	}else{
            		objResult=new Double(8);
            	}
            }
            //add by zhouwei 20120217 保留位数，交易所为8，银行间为12 end-----------
	        else if (sKeyword.equalsIgnoreCase("tszq")){//add by fangjiang 2012.02.18  处理特殊债券
	        	if("Day".equalsIgnoreCase(bondIns.getInsType())){
	        		objResult = new Double(0);
	        	}else{
	        		//modify by fangjiang 债券优化 2013.04.10
	        		if(("CS").equalsIgnoreCase(security.getExchangeCode()) || ("CG").equalsIgnoreCase(security.getExchangeCode())){
	        			objResult = new Double(0);
	        		}else{
	        			objResult = new Double(-1);
	        		}
	        		//modify by fangjiang 2012.07.12 深圳公司债从T+1改为T+0
		        	/*if(("CS").equalsIgnoreCase(security.getExchangeCode()) && "FI08".equalsIgnoreCase(security.getStrSubCategoryCode())){ //深圳公司债  
		        		CtlPubPara ctl = new CtlPubPara();
		        		ctl.setYssPub(pub);
		        		String qyrq = ctl.getQYRQ("szgszqyrq");
		        		Date date = null;
		        		if(null != qyrq){
		        			date = YssFun.parseDate(qyrq);
		        		}
		        		//---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
		        		int dif = 0;
		        		if(date != null){
		        			dif = YssFun.dateDiff(date, bondIns.getInsDate());
		        		}
		        		//---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
		        		if(null == qyrq || dif < 0){
		        			BaseOperDeal operDeal = new BaseOperDeal();	
			        		operDeal.setYssPub(this.pub);
			        		int d = YssFun.dateDiff(bondIns.getInsDate(),operDeal.getWorkDay(security.getHolidaysCode(), bondIns.getInsDate(), security.getIntSettleDays()));
			        		objResult = new Double(d-1);	   
		        		}else{
		        			objResult = new Double(0);	   
		        		} 		     		
		        	}else if(("CY").equalsIgnoreCase(security.getExchangeCode())){ //银行间
		        		BaseOperDeal operDeal = new BaseOperDeal();	      
		        		operDeal.setYssPub(this.pub);
		        		//int d = YssFun.dateDiff(bondIns.getInsDate(),operDeal.getWorkDay(security.getHolidaysCode(), bondIns.getInsDate(), security.getIntSettleDays()));
		        		//modify by zhouwei 20120421 T+0与T+1计息
		        		if(null == bondIns.getSettleDate()){
		        			objResult = new Double(0);
		        		}else{
		        			int d = YssFun.dateDiff(bondIns.getInsDate(), bondIns.getSettleDate());
			        		objResult = new Double(d-1);
		        		}	        		    		
		        	}
		        	else{
		        		objResult = new Double(0);
		        	}*/
	        	}
	        }
	        else if(sKeyword.equalsIgnoreCase("jxfs")){
	        	/*
	        	 * 计息方式，用来区分债券交易时含息到那一天
	        	 * 交易所： （深圳公司债除外）
	        	 * T+0，含息到交易当天
	        	 * T+1，含息到交割日期的前一个工作日
	        	 * 银行间、深圳公司债
	        	 * 含息到交割日的前一自然日
	        	 * jxfs==1，任意一个交易日，它的含息截止日都<=交易日
	        	 * jxfs==2, 含息到交割日的前一自然日
	        	 * modify by fangjiang 债券优化 2013.04.10
	        	 */
	        	String exchangeCode = security.getExchangeCode(); //交易所
	        	String subCatCode = security.getStrSubCategoryCode(); //子品种
	        	if(("CG").equalsIgnoreCase(exchangeCode) || ("CS").equalsIgnoreCase(exchangeCode)) { //上交所、深交所
	        		objResult = new Double(1);
	        	}else{ //银行间、境外
	        		objResult = new Double(2);
	        	}
        		/*if(0 == security.getIntSettleDays()){ //T+0交易
        			objResult = new Double(1);
        		}else{
        			if(("CG").equalsIgnoreCase(exchangeCode)) { //上交所
    	        		objResult = new Double(1);
    	        	}else if(("CS").equalsIgnoreCase(exchangeCode)){ //深交所
    	        		if("FI08".equalsIgnoreCase(security.getStrSubCategoryCode())){ //公司债
    	        			objResult = new Double(2);
    	        		}else{
    	        			objResult = new Double(1);
    	        		}
    	        	}else if(("CY").equalsIgnoreCase(exchangeCode)){ //银行间
    	        		objResult = new Double(2);
    	        	}
        		}	        	*/
	        }
	        else if(sKeyword.equalsIgnoreCase("szgsz")){
	        	if(("CS").equalsIgnoreCase(security.getExchangeCode()) && "FI08".equalsIgnoreCase(security.getStrSubCategoryCode())){ //深圳公司债  
	        		objResult = new Boolean(true);	        		
	        	}else{
	        		objResult = new Boolean(false);	
	        	}
	        }
	        else if(sKeyword.equalsIgnoreCase("yhjzq")){
	        	if(("CY").equalsIgnoreCase(security.getExchangeCode())){ //银行间
	        		objResult = new Boolean(true);	
	        	}else{
	        		objResult = new Boolean(false);	
	        	}
	        }else if(sKeyword.equalsIgnoreCase("insType_Day")){
	        	if(("Day").equalsIgnoreCase(bondIns.getInsType())){ //银行间
	        		objResult = new Boolean(true);	
	        	}else{
	        		objResult = new Boolean(false);	
	        	}
	        }
            else {
                objResult = sKeyword;
            }
        }
        if (bondIns != null) { 
            if (sKeyword.equalsIgnoreCase("isnDate")) {
                objResult = bondIns.getInsDate();
            } else if (sKeyword.equalsIgnoreCase("Bal")) { //已计提利息
                objResult = new Double(getFixInterest());
            } else if (sKeyword.equalsIgnoreCase("DisRate")) { //贴现率
                objResult = new Double(bondIns.getDisRate());
            } else if (sKeyword.equalsIgnoreCase("Factor")) { //报价因子
                objResult = new Double(bondIns.getFactor());
            } else if (sKeyword.equalsIgnoreCase("Premium")) { //溢价
                objResult = new Double(this.getFixIntersetPremium());
            } else if (sKeyword.equalsIgnoreCase("Discounts")) { //折价
                objResult = new Double(this.getFixIntersetDiscounts());
            } else if(sKeyword.equalsIgnoreCase("tradeSettleDate")){//交易结算日期 2010-03-27 蒋锦 添加 国内 MS00955
            	objResult = bondIns.getSettleDate() == null?bondIns.getInsDate():bondIns.getSettleDate();
            } else if(sKeyword.equalsIgnoreCase("tsDate")){
            	if("Day".equalsIgnoreCase(bondIns.getInsType())){
	        		objResult = bondIns.getInsDate();
            	}else{
            		String exchangeCode = security.getExchangeCode();
            		if(("CG").equalsIgnoreCase(exchangeCode) || ("CS").equalsIgnoreCase(exchangeCode)) { //上交所、深交所
    	        		objResult = bondIns.getInsDate();
    	        	}else{ //银行间、境外
    	        		objResult = YssFun.addDay(bondIns.getInsDate(), -1);
    	        	}
            	}
            }
        }

        return objResult;
    }

    /**shashijie 2012-2-3 STORY 优先获取计息期间设置的起止日期
	* @return*/
	private Object getDtThisInsEndDate() {
		//默认等于原先的计息截止日
		Date endDate = security.getFixInterest().getDtThisInsEndDate();
		//获取计息期间设置的起止日期
		endDate = getInsStartDate(bondIns.getInsDate(),security.getStrSecurityCode(),2,endDate);
		return endDate;
	}

	/**shashijie 2012-2-3 STORY 1713 优先获取计息期间设置的起止日期 */
	private Object getDtThisInsStartDate() {
		//默认等于原先的计息起始日
		Date StartDate = security.getFixInterest().getDtThisInsStartDate();
		//获取计息期间设置的起止日期
		StartDate = getInsStartDate(bondIns.getInsDate(),security.getStrSecurityCode(),1,StartDate);
		return StartDate;
	}

	/**shashijie 2012-2-3 STORY 1713  获取计息期间设置的起止日期
	* @param startDate 本次计息起息日或截止日
	* @param SecurityCode 证券代码
	* @param tag 标示 1==起息,2==截止
	* @param def 默认日期
	* @return*/
	private Date getInsStartDate(Date startDate,String SecurityCode,int tag,Date def) {
		ResultSet rs = null;
		Date date = def;
		try {
			String query = getSqlTime(startDate, SecurityCode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				date = tag ==1?rs.getDate("FInsStartDate"):rs.getDate("FInsEndDate");
			}
		} catch (Exception e) {

		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return date;
	}

	/**shashijie 2012-2-3 STORY 1713 按优先级去利率*/
	private Object getStrFaceRate() {
		BigDecimal value = security.getFixInterest().getStrFaceRate();
		//获取债券期间设置
		value = getInterestTime(bondIns.getInsDate(),security.getStrSecurityCode(),value);
		//获取债券利率设置
		value = getDriftRate(bondIns.getInsDate(),value);
		return value;
	}
	

	/**shashijie 2012-2-3 STORY 1713 获取债券利率设置
	* @param dateNow 计提日期
	* @param def 默认值
	*/
	private BigDecimal getDriftRate(Date dateNow,BigDecimal def) {
		ResultSet rs = null;
		ResultSet rs2 = null;
		BigDecimal value = def;
		try {
			String strSql = getSqlTime(dateNow,securityCode);
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				String sqlString = " Select * From "+pub.yssGetTableName("Tb_Para_DriftRate")+
					" Where FSecurityCode = "+dbl.sqlString(securityCode)+
					" And FStartDate >= "+dbl.sqlDate(rs.getDate("FInsStartDate"))+
					" And FStartDate <= "+dbl.sqlDate(rs.getDate("FInsEndDate"));
				rs2 = dbl.openResultSet(sqlString);
				if (rs2.next()) {
					value = rs2.getBigDecimal("FRate");
				}
			}
		} catch (Exception e) {
			
		} finally {
			dbl.closeResultSetFinal(rs,rs2);
		}
		return value;
	}

	/**shashijie 2012-2-3 STORY 1713 获取债券期间设置
	* @param dateNow 计息起始日
	* @param SecurityCode 证券代码
	* @param def 默认值
	*/
	private BigDecimal getInterestTime(Date dateNow, String SecurityCode,BigDecimal def) {
		ResultSet rs = null;
		BigDecimal value = def;
		try {
			String strSql = getSqlTime(dateNow,securityCode);
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				value = rs.getBigDecimal("FFaceRate");
			}
		} catch (Exception e) {
			
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}

	/**shashijie 2012-2-3 STORY 1713 */
	private String getSqlTime(Date dateNow, String SecurityCode) {
		String SQL = "Select * From "+pub.yssGetTableName("Tb_Para_InterestTime")+
		" Where FSecurityCode = "+dbl.sqlString(SecurityCode)+
		" And FInsStartDate <= "+dbl.sqlDate(dateNow)+
		" And FInsEndDate >= "+dbl.sqlDate(dateNow);
		return SQL;
	}

	/**
     * 判断计息起始日和计息当天是否为二月最后一天
     * @param dStartDate
     * @param dEndDate
     * @return “是” 返回 1，“否” 返回 0，默认“否”
     * @throws YssException
     * BUG1504 QDV4太平2011年03月11日01_B
     * panjunfang add 20110421
     */
    public double isEndDateOfFeb(java.util.Date dStartDate,java.util.Date dEndDate) throws YssException {
 		int iStartMonth = 0, iEndMonth = 0, iStartDay = 0, iEndDay = 0;
 		int iEndFebDate = 0; // 存放二月最后一天的天数
 		int dResult = 0;
 		try {
 			iStartMonth = YssFun.getMonth(dStartDate);
 			iStartDay = YssFun.getDay(dStartDate);
 			iEndMonth = YssFun.getMonth(dEndDate);
 			iEndDay = YssFun.getDay(dEndDate);
 			iEndFebDate = YssFun.endOfMonth(YssFun.getYear(dStartDate), 2);
 			if (iStartMonth == 2 && iEndMonth == 2) { // 当都是月末且都是二月份的最后一天
 				if (iEndDay == iEndFebDate && iStartDay == iEndFebDate) {
 					dResult = 1;
 				}
 			}
 		} catch (Exception e) {
 			throw new YssException(e);
 		}
 		return dResult;
    }
    
    /**
     * 赋入如公式等相应的值
     * @param SecurityCode String
     * @param insDate Date
     * @param insType String
     * @throws YssException
     */
    public void getFixInterestInfo(String SecurityCode, java.util.Date insDate,
                                   String insType) throws
        YssException {
        String FixInterestCode = SecurityCode;
        java.util.Date FixInsDate = insDate;
        String FixInterestInsType = insType;
        String sqlStr = "";
        String sqlTypeStr = "";
        ResultSet rs = null;
        HashMap hmInteArgs = new HashMap();
        try {
            if (FixInterestInsType != null && FixInterestInsType.length() > 0) {
                sqlTypeStr = " left join (select FCIMCode,FCIMName,FCIMType,FFormula,FSPICode from Tb_Base_CalcInsMetic) b on ";
                if (FixInterestInsType.equalsIgnoreCase("Day")) {
                    sqlTypeStr = sqlTypeStr + " a.FCalcInsMeticDay = b.FCIMCode ";
                } else if (FixInterestInsType.equalsIgnoreCase("Buy")) {
                    sqlTypeStr = sqlTypeStr + " a.FCalcInsMeticBuy = b.FCIMCode ";
                } else if (FixInterestInsType.equalsIgnoreCase("Sell")) {
                    sqlTypeStr = sqlTypeStr + " a.FCalcInsMeticSell = b.FCIMCode ";
                } else if (FixInterestInsType.equalsIgnoreCase("ValPrice")) {
                    sqlTypeStr = sqlTypeStr + " a.FCalcPriceMetic = b.FCIMCode ";
                } else if (FixInterestInsType.equalsIgnoreCase("Premium")) { // add by leeyu 溢价/折价
                    sqlTypeStr = sqlTypeStr + " a.FAmortization = b.FCIMCode ";
                }
                /**shashijie 2012-1-19 STORY 1713*/
                else if (FixInterestInsType.equalsIgnoreCase("FTaskMoneyCode")) {//百元派息金额
                	sqlTypeStr = sqlTypeStr + " a.FTaskMoneyCode = b.FCIMCode ";
				}
				/**end*/
            }
            
            //--- add by songjie 2013.04.02 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            BondInsCfgFormula bonds = null;
        	FixInterestBean fixIt = null;
			if (BondAssist.hmSec != null) {
				String[] fixInterestCodes = FixInterestCode.split(",");
				for (int i = 0; i < fixInterestCodes.length; i++) {
					if (BondAssist.hmSec != null && BondAssist.hmSec.get(fixInterestCodes[i]) != null) {
						fixIt = (FixInterestBean) BondAssist.hmSec.get(fixInterestCodes[i]);

						security = fixIt.getSecurity();
						security.setFixInterest(fixIt);
						this.intPl = fixIt.getStrInsFrequency().doubleValue();
						this.dubMz = fixIt.getStrFaceValue().doubleValue();
						this.dateNow = fixIt.getDtInsStartDate();
						this.dateEnd = fixIt.getDtInsEndDate();
						this.periodCode = fixIt.getStrPeriodCode();
						this.formula = fixIt.getFormula();
						hmBondParams = fixIt.getHmBondDate();

						if (this.hmSec != null && this.hmSec.get(fixInterestCodes[i]) == null) {
							bonds = new BondInsCfgFormula();
							bonds.security = this.security;
							bonds.security.setFixInterest(fixIt);
							bonds.securityCode = fixInterestCodes[i];
							bonds.tradeDate = this.tradeDate;
							bonds.intPl = this.intPl;
							bonds.dubMz = this.dubMz;
							bonds.dateNow = this.dateNow;
							bonds.dateEnd = this.dateEnd;
							bonds.periodCode = this.periodCode;
							bonds.formula = this.formula;
							bonds.hmBondParams = this.hmBondParams;
							bonds.flage = false;
							bonds.bondIns = this.bondIns;
							this.hmSec.put(fixInterestCodes[i], bonds);
						}
					}
				}
			}else{
                     	//--- add by songjie 2013.04.02 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
                     	sqlStr = "select a.*,b.* from " +
                     		"(select a1.* from " + pub.yssGetTableName("Tb_Para_FixInterest") +
                     		" a1 join " +
                     		"(select FSecurityCode from " + //启用日期无作用 xuqiji 20090427:QDV4赢时胜（上海）2009年4月21日01_B   MS00405    收益计提计提债券利息时对债券启用日期有判断
                     		pub.yssGetTableName("Tb_Para_FixInterest") +
                         	//启用日期无作用 xuqiji 20090427:QDV4赢时胜（上海）2009年4月21日01_B   MS00405    收益计提计提债券利息时对债券启用日期有判断
                     		"  where  FCheckState = 1 and FSecurityCode = " +
                     		//--------------------------------end----------------------------------------------------------------//
                     		dbl.sqlString(FixInterestCode) +
                     		" group by FSecurityCode) a2 on a1.FSecurityCode = a2.FSecurityCode) a " +
                     		sqlTypeStr;
                     	rs = dbl.openResultSet(sqlStr);
                     	if (rs.next()) {
                     		FixInterestBean interest = new FixInterestBean();
                         	//---2008.07.22 蒋锦 添加 计算债券下一计息起始日和截止日---//
                     		getNextStartDateAndEndDate(insDate, rs, hmInteArgs);
                     		//add by yanghaiming 20110222 #601
                     		if(rs.getString("FVALUEDATES") != null && rs.getString("FVALUEDATES").length() > 0){//如果债券信息设置中有设置过起息日，则做本期起息日截止日处理
                     			getRealStartDateAndEndDate(insDate,SecurityCode,rs, hmInteArgs);
                     		}
                         	//add by yanghaiming 20110222 #601
                     		interest.setDtThisInsStartDate( (java.util.Date) hmInteArgs.get(
                             	"InsStartDate"));
                     		interest.setDtThisInsEndDate( (java.util.Date) hmInteArgs.get(
                             	"InsEndDate"));
                     		//----------------------------------------------------//
                     		interest.setDFactRate(rs.getDouble("FFactRate")); //暂时屏掉 by leeyu
                     		interest.setStrFaceValue(rs.getBigDecimal("FFaceValue")); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
                     		interest.setStrFaceRate(rs.getBigDecimal("FFaceRate"));// bug 2381 by zhouwei 20111111
                     		interest.setStrInsFrequency(rs.getBigDecimal("FInsFrequency"));// bug 2381 by zhouwei 20111111
                     		interest.setDtStartDate(rs.getDate("FInsStartDate"));
                     		interest.setDtInsEndDate(rs.getDate("FInsEndDate"));
                     		interest.setDtInsCashDate(rs.getDate("FInsCashDate"));
                     		//2008.05.19 蒋锦 添加 发行价格
                     		interest.setStrIssuePrice(rs.getBigDecimal("FIssuePrice")); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
                     		//add by yanghaiming 20110221 #461
                     		interest.setBaseCPI(rs.getDouble("FBASECPI"));
                     		//add by yanghaiming 20110221 #461
                     		security = new SecurityBean();
                     		//--------MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 2009-07-28 蒋锦 添加 获取证券相关信息--------//
                     		security.setYssPub(pub);
                     		security.setSecurityCode(FixInterestCode);
                     		security.getSetting();
                     		//--------------------------------------------------------------------------------------------//
                     		security.setFixInterest(interest);
                     		this.intPl = rs.getDouble("FInsFrequency");
                     		this.dubMz = rs.getDouble("FFaceValue");
                     		this.dateNow = rs.getDate("FInsStartDate");
                     		this.dateEnd = rs.getDate("FInsEndDate");
                     		this.periodCode = rs.getString("FPeriodCode");
                     		this.formula = rs.getString("FFormula"); //赋予父类公式的值
                     	}
                     	
                     	dbl.closeResultSetFinal(rs);
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**shashijie 2012-1-19 STORY 1713 赋入如公式等相应的值*/
	private void getFixInterestInfo2(String securityCode, Date statDate, Date endDate, Date issueDate, double faceRate,
			String fixInterest) throws YssException {
		//--- edit by songjie 2013.04.19 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
		String sqlStr = "";
		ResultSet rs = null;
		FixInterestBean interest = null;
		try {
			sqlStr = "select a.*,b.*,inter.FISecurityCode,inter.FPayMoney,inter.FRemainMoney " 
				    + " from (select a1.* from " + pub.yssGetTableName("Tb_Para_FixInterest")
					+ " a1 join (select FSecurityCode from " + pub.yssGetTableName("Tb_Para_FixInterest")
					+ "  where  FCheckState = 1 and FSecurityCode = " + dbl.sqlString(securityCode)
					+ " group by FSecurityCode) a2 on a1.FSecurityCode = a2.FSecurityCode) a "
					+ " left join (select FCIMCode,FCIMName,FCIMType,FFormula,FSPICode from Tb_Base_CalcInsMetic) b "
					+ " on a.FTaskMoneyCode = b.FCIMCode "
					+ " left join (select FSecurityCode as FISecurityCode,FPayMoney,FRemainMoney from " 
					+ pub.yssGetTableName("Tb_Para_InterestTime") 
					+ " where FInsStartDate = " + dbl.sqlDate(statDate) + " and FInsEndDate = " 
					+ dbl.sqlDate(endDate) + " and FCheckState = 1) inter " 
					+ " on inter.FISecurityCode = a.FSecurityCode ";
			rs = dbl.openResultSet(sqlStr);
			if (rs.next()) {
				interest = new FixInterestBean();
				
				if(rs.getString("FISecurityCode") == null){
					interest.setStrFaceValue(rs.getBigDecimal("FFaceValue"));
				}else{
					interest.setStrFaceValue(BigDecimal.valueOf(
					YssD.add(rs.getDouble("FPayMoney"), rs.getDouble("FRemainMoney"))));
				}
				
				bondIns.setFaceValue(interest.getStrFaceValue());
				
				interest.setDFactRate(rs.getDouble("FFactRate"));
				
				interest.setStrInsFrequency(rs.getBigDecimal("FInsFrequency"));
				interest.setDtStartDate(rs.getDate("FInsStartDate"));
				interest.setDtInsEndDate(rs.getDate("FInsEndDate"));
				interest.setStrIssuePrice(rs.getBigDecimal("FIssuePrice"));
				interest.setBaseCPI(rs.getDouble("FBASECPI"));

				security = new SecurityBean();
				security.setYssPub(pub);
				security.setSecurityCode(securityCode);
				security.getSetting();
				security.setFixInterest(interest);

				if(security.getStrExchangeCode().equals("CG") || security.getStrExchangeCode().equals("CS")){
			        bondIns.setInsDate(endDate);
				}else{
			        bondIns.setInsDate(YssFun.addDay(endDate, 1));
				}
				
				this.intPl = rs.getDouble("FInsFrequency");
				this.dubMz = rs.getDouble("FFaceValue");
				this.dateNow = rs.getDate("FInsStartDate");
				this.dateEnd = rs.getDate("FInsEndDate");
				this.periodCode = rs.getString("FPeriodCode");
				this.formula = rs.getString("FFormula"); // 赋予父类公式的值
			}

			interest.setYssPub(pub);
			interest.setStrSecurityCode(securityCode);// 证券代码
			interest.setDtThisInsStartDate(statDate);// 当前计息期间计息起始日
			interest.setDtThisInsEndDate(endDate);// 当前计息期间计息截止日
			interest.setStrFaceRate(BigDecimal.valueOf(faceRate));// 税后票面利率
			interest.setDtInsCashDate(issueDate);// 兑付日期
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		//--- edit by songjie 2013.04.19 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
	}

	/**shashijie 2012-1-19 STORY 1713 */
	private void setSecurityBean(FixInterestBean interest) {
		security = new SecurityBean();
        security.setYssPub(pub);
        security.setSecurityCode(securityCode);
        security.getSetting();
        security.setFixInterest(interest);
	}

	public double getStgAmount(java.util.Date dDate, String sDisDay) throws
        YssException {
        return getStgAmount(dDate, sDisDay, "work");
    }

    /**
     * 获取库存数量
     * @param dDate java.util.Date 计息日期
     * @param iDisDay int 距离天数(可以为负数)
     * @param sType String work-工作日  natural-自然日
     * @throws YssException
     */
    public double getStgAmount(java.util.Date dDate, String sDisDay,
                               String sType) throws YssException {
        int iDisDay = 0;
        ArrayList secRecPayList = null;

        //--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
        if(BondInsCfgFormulaN.hmKey != null && BondInsCfgFormulaN.hmKey.get("StgAmount") != null){
        	HashMap hmsecs = (HashMap)BondInsCfgFormulaN.hmKey.get("StgAmount");
        	String analy1 = bondIns.getAnalysisCode1();
        	String analy2 = bondIns.getAnalysisCode2();
        	if(analy1.trim().length() == 0){
        		analy1 = " ";
        	}
        	if(analy2.trim().length() == 0){
        		analy2 = " ";
        	}
        	String key = bondIns.getSecurityCode() + "\t" + 
        	bondIns.getPortCode() + "\t" +
        	bondIns.getAttrClsCode() + "\t" + 
        	bondIns.getInvestType() + "\t" +
        	analy1 + "\t" + analy2 + "\t" + 
        	YssFun.formatDate(dDate,"yyyyMMdd");
        	if(hmsecs.get(key) != null){
        		return ((Double)hmsecs.get(key)).doubleValue();
        	}else{
        		return 0;
        	}
        }
        //--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
        
        if (sDisDay.startsWith("!")) {
            iDisDay = YssFun.toInt(sDisDay.substring(1)) * -1;
        } else {
            iDisDay = YssFun.toInt(sDisDay);
        }

        if (sType.equalsIgnoreCase("work")) {
            dDate = this.getSettingOper().getWorkDay(bondIns.getHolidaysCode(),
                dDate, iDisDay);
        } else if (sType.equalsIgnoreCase("natural")) {
            dDate = YssFun.addDay(dDate, iDisDay);
        }
        SecurityStorageAdmin secStorage = new SecurityStorageAdmin();
        secStorage.setYssPub(pub);
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        secRecPayList = secStorage.getSecBeans(dDate, dDate, bondIns.getPortCode(),
                                               bondIns.getAnalysisCode1(),
                                               bondIns.getAnalysisCode2(),
                                               bondIns.getSecurityCode(),
                                               "", bondIns.getAttrClsCode(),
                                               //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 添加投资类型
                                               bondIns.getInvestType());
        if (secRecPayList.size() == 0) {
            return 0;
        } else {
            return YssFun.toDouble( ( (SecurityStorageBean) secRecPayList.get(0)).
                                   getStrStorageAmount());
        }
    }

    public double getFIInsBal(java.util.Date dDate, String sDisDay) throws
        YssException {
        return getFIInsBal(dDate, sDisDay, "work");
    }

    /**
     * 获取债券应收利息
     * @param dDate java.util.Date 计息日期
     * @param iDisDay int 距离天数(可以为负数)
     * @param sType String work-工作日  natural-自然日
     * @throws YssException
     */
    public double getFIInsBal(java.util.Date dDate, String sDisDay, String sType) throws
        YssException {
        int iDisDay = 0;
        boolean bIsQCData = false; //是否为期初数 by leeyu 080710
        ArrayList secRecPayList = null;
        //fanghaoln 20091118 MS00745 QDV4华夏2009年9月30日01_B 
        if(YssFun.dateDiff(security.getFixInterest().getDtThisInsStartDate(),dDate)==0){//当为结息日期时以前的利息为0
        	return 0;
        }
        
        //--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
        if(BondInsCfgFormulaN.hmKey != null && BondInsCfgFormulaN.hmKey.get("FIInsBal") != null){
        	HashMap hmsecs = (HashMap)BondInsCfgFormulaN.hmKey.get("FIInsBal");
        	String analysis1 = bondIns.getAnalysisCode1();
        	String analysis2 = bondIns.getAnalysisCode2();
        	if(analysis1.trim().length() == 0){
        		analysis1 = " ";
        	}
        	if(analysis2.trim().length() == 0){
        		analysis2 = " ";
        	}
        	String key = bondIns.getSecurityCode() + "\t" + 
        	bondIns.getPortCode() + "\t" +
        	bondIns.getAttrClsCode() + "\t" + 
        	bondIns.getInvestType() + "\t" +
        	analysis1 + "\t" + analysis2 + "\t"
			+ YssFun.formatDate(dDate,"yyyyMMdd");
        	if(hmsecs.get(key) != null){
        		return ((Double)hmsecs.get(key)).doubleValue();
        	}else{
        		return 0;
        	}
        }    
        //--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
        
        //-----------------------end MS00745--------------------------------------------------------------------------
        if (sDisDay.startsWith("!")) {
            iDisDay = YssFun.toInt(sDisDay.substring(1)) * -1;
        } else {
            iDisDay = YssFun.toInt(sDisDay);
        }
        if ( (YssFun.getYear(dDate) ==
              YssFun.getYear(YssFun.addDay(dDate, iDisDay)) + 1) &&
            YssFun.formatDate(YssFun.addDay(dDate, iDisDay),
                              "yyyy-MM-dd").equals(YssFun.getYear(YssFun.addDay(
                                  dDate, iDisDay)) +
            "-12-31")) {
            //判断是头一年 且 日期为年底
            bIsQCData = true;
        }else {//fanghaoln 20100106 MS00901 QDV4华夏2010年01月06日01_B  当是一年的第一天时，为期初数，不去减。
        	if (sType.equalsIgnoreCase("work")) {
                dDate = this.getSettingOper().getWorkDay(bondIns.getHolidaysCode(),
                    dDate, iDisDay);
            } else if (sType.equalsIgnoreCase("natural")) {
                dDate = YssFun.addDay(dDate, iDisDay);
            }
        }
        SecRecPayStorageAdmin secRecPayStorage = new SecRecPayStorageAdmin();
        secRecPayStorage.setYssPub(pub);
        //=======MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化===========
        //modify by sunkey 20090425
        //因为之前的方法获取的数据会频繁查询数据库，造成效率低下，因此替换为新的方法
//      secRecPayList = secRecPayStorage.getSecRecBeans(dDate, dDate, "06",
//            "06FI", bondIns.getPortCode(), bondIns.getAnalysisCode1(),
//            bondIns.getAnalysisCode2(), bondIns.getSecurityCode(), "",
//            bIsQCData);
        //方法增加了投资类型 蒋锦 2009.09.05 MS00656:QDV4赢时胜(上海)2009年8月24日01_A
        secRecPayList = secRecPayStorage.getSecRecBeans(bondIns.getSecurityCode(),
            dDate, dDate, "06", "06FI", bondIns.getPortCode(),
            bondIns.getAnalysisCode1(), bondIns.getAnalysisCode2(),
            "", bIsQCData, bondIns.getAttrClsCode(), "");
        //==========================End MS00006===================================
        if (secRecPayList.size() == 0) {
            return 0;
        } else {
            return ( (SecRecPayBalBean) secRecPayList.get(0)).getDBal();
        }
    }

    /**
     * 重载方法。
     * sj add 20080124
     * @param dDate Date
     * @throws YssException
     * @return double
     * MS00255 QDV4建行2009年2月17日02_B sj modified
     */
    public double getFIInsTrade(java.util.Date dDate, String isOnlyBargainDate) throws YssException {
        return getFIInsTrade(dDate, false, isOnlyBargainDate);
    }

    /**
     * 重载方法，默认为买入结算日当日开始计息。
     * sj add 20080124
     * @param dDate Date
     * @throws YssException
     * @return double
     * MS00255 QDV4建行2009年2月17日02_B sj modified
     */
    public double getFIInsTrade(java.util.Date dDate, boolean DayFi) throws YssException {
        return getFIInsTrade(dDate, DayFi, ""); //----MS00255 QDV4建行2009年2月17日02_B sj modified,不是只在交易日当日获取买卖利息
    }

    /**
     * 重载方法，默认为买入结算日当日开始计息。
     * sj add 20080124
     * @param dDate Date
     * @throws YssException
     * @return double
     */
    public double getFIInsTrade(java.util.Date dDate) throws YssException {
        return getFIInsTrade(dDate, true, ""); //----MS00255 QDV4建行2009年2月17日02_B sj modified,不是只在交易日当日获取买卖利息
    }

   /** 合并太平版本代码
    * 重载此方法，用于处理原有的数据
    * QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
    * @param dDate
    * @param DayFi
    * @param isOnlyBargainDate
    * @return
    * @throws YssException
    */
   public double getFIInsTrade(java.util.Date dDate, boolean DayFi
           ,String isOnlyBargainDate) throws YssException {
	   return getFIInsTrade(dDate,DayFi,isOnlyBargainDate,"");//将原默认加空值 QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
   }
   
   //add by fj 2012.03.12
   public double getFIInsTrade(java.util.Date dDate, boolean DayFi
           ,String isOnlyBargainDate, String ctlSettleDate) throws YssException {
	   return getFIInsTrade(dDate,DayFi,isOnlyBargainDate,"","0");
   }
   
    //获取债券买卖的利息
    public double getFIInsTrade(java.util.Date dDate, boolean DayFi
                               ,String isOnlyBargainDate,//----MS00255 QDV4建行2009年2月17日02_B sj modified,增加对是否只取交易日当日的买卖利息
                               String tradeTypeCodes,
                               String ctlSettleDate) throws //添加交易类型字段，这里获取固定交易类型的数据  QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
        YssException { //增加参数,用于判断是否从买入结算日当日开始计息 sj edit 20080124
        String strSql = "";
        ResultSet rs = null;
        double dResult = 0;

        boolean analy1;
        boolean analy2;
        boolean analy3;

        try {
		    //--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(BondInsCfgFormulaN.hmKey != null && BondInsCfgFormulaN.hmKey.get("FIInsTrade") != null){
            	HashMap hmsecs = (HashMap)BondInsCfgFormulaN.hmKey.get("FIInsTrade");
            	String analysis1 = bondIns.getAnalysisCode1();
            	String analysis2 = bondIns.getAnalysisCode2();
            	if(analysis1.trim().length() == 0){
            		analysis1 = " ";
            	}
            	if(analysis2.trim().length() == 0){
            		analysis2 = " ";
            	}
            	String key = bondIns.getSecurityCode() + "\t" + 
            	bondIns.getPortCode() + "\t" +
            	bondIns.getAttrClsCode() + "\t" + 
            	bondIns.getInvestType() + "\t" +
            	analysis1 + "\t" + analysis2 + "\t" + 
            	YssFun.formatDate(dDate,"yyyyMMdd");
            	if(hmsecs.get(key) != null){
            		return ((Double)hmsecs.get(key)).doubleValue();
            	}else{
            		return 0;
            	}
            }     
        	//--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            //-----MS00209 QDV4建行2009年1月20日01_B sj modified --------------------------//
            if (DayFi) { //为true时，减去买入利息
                strSql =
                    "select sum(FAccruedinterest*FAmountInd*-1) as FBondIns from ";
            } else { //为false时，加上买入利息
                strSql =
                    "select sum(FAccruedinterest*FAmountInd) as FBondIns from ";
            }
            //---------------------------------------------------------------------------//
            strSql += pub.yssGetTableName("Tb_Data_SubTrade") +
                " a left join (select * from Tb_Base_TradeType where FCheckState = 1) b on a.FTradeTypeCode = b.FTradeTypeCode"

	            /**Start  20130614 added by liubo.Story #3892.需求北京-(博时基金)QDIIV4.0(高)20130424001*/
	            /** 以该条债券的证券代码和操作日期为条件，找出操作日期下，该债券的当前计息期间*/
				+ " left join (select * from " + pub.yssGetTableName("tb_para_interesttime")
				+ " where FCheckState = 1 "
				+ " and FSecurityCode = " + dbl.sqlString(bondIns.getSecurityCode()) + ") it "
                + " on " + dbl.sqlDate(dDate) + " between FINSSTARTDATE and FINSEndDATE " +

	            /**End  20130614 added by liubo.Story #3892.需求北京-(博时基金)QDIIV4.0(高)20130424001*/
                " where a.FSecurityCode = " +
                dbl.sqlString(bondIns.getSecurityCode()) +
                " and a.FCheckState = 1 " + //--- add by wangzuochun 2010.05.21 MS01180    利息公司中"FIInsTrade"获取交易利息时,未加已审核条件    QDV4华夏2010年5月20日01_B  
                " AND a.FAttrClsCode = " + dbl.sqlString(bondIns.getAttrClsCode()) +//2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                " and FPortCode = " + dbl.sqlString(bondIns.getPortCode()) + //增加组合的筛选,获取某组合的获取债券买卖的利息.sj edit 20080818 bug 0000417
                (analy1 ?
                 " and FInvMgrCode = " + dbl.sqlString(bondIns.getAnalysisCode1()) :
                 "") +
                (analy2 ?
                 " and FBrokerCode = " + dbl.sqlString(bondIns.getAnalysisCode2()) :
                 "");
         	//添加对交易方式的处理 QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318 合并太平版本代码
         	if(tradeTypeCodes!=null && tradeTypeCodes.length()>0){
        		 strSql=strSql + " and a.FTradeTypeCode in ("+operSql.sqlCodes(tradeTypeCodes)+")";
         	}
	         //QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
//               " and FBargainDate < " + dbl.sqlDate(dDate);//MS00209 QDV4建行2009年1月20日01_B 将筛选调整移入判断条件内。
            //---------------//MS00209 QDV4建行2009年1月20日01_B sj modified--------------------------//
            if (DayFi) { //为true时，不包括交易和实际结算日期
                //----MS00255 QDV4建行2009年2月17日02_B sj modified ----------//
                if (isOnlyBargainDate.toLowerCase().equalsIgnoreCase("B")) { //只在交易日获取买卖利息
                    strSql +=
                        " and FBargainDate = " + dbl.sqlDate(dDate);
                } else {
                    strSql +=
                        " and FBargainDate < " + dbl.sqlDate(dDate); //将筛选调整移入判断条件内
                }
                //-----------------------------------------------------------//
                if("0".equalsIgnoreCase(ctlSettleDate)){
	                strSql +=
	                    " and " + dbl.sqlDate(dDate) + " < a.FSettleDate";
                }else if("1".equalsIgnoreCase(ctlSettleDate)){
                	strSql +=
	                    " and (1+ " + dbl.sqlDate(dDate) + ") < a.FSettleDate";
                }
            } else { //当为false时，包括交易和实际结算日期。包含交易日期与实际结算日期相同的情况。
                //----MS00255 QDV4建行2009年2月17日02_B sj modified ----------//
                if (isOnlyBargainDate.toLowerCase().equalsIgnoreCase("B")) { //只在交易日获取买卖利息
                    strSql +=
                        " and FBargainDate = " + dbl.sqlDate(dDate);
                } else {
                    strSql +=
                        " and FBargainDate <= " + dbl.sqlDate(dDate); //将筛选调整移入判断条件内，包括交易日期当日的数据
                }
                //----------------------------------------------------------//
                if("0".equalsIgnoreCase(ctlSettleDate)){
	                strSql +=
	                    " and " + dbl.sqlDate(dDate) + " <= FSettleDate";
                }else if("1".equalsIgnoreCase(ctlSettleDate)){
                	strSql +=
	                    " and (1+ " + dbl.sqlDate(dDate) + ") <= FSettleDate";
                }
            }
            //--------------------------------------------------------------------------------------------
            
            /**Start  20130613 added by liubo.Story #3892.需求北京-(博时基金)QDIIV4.0(高)20130424001*/
            /** 债券利息算法中，获取关键字FIInsTrade时，未区分本期代入代出利息、上期代入代出利息，
             * 从而导致当有交易数据的成交日~结算日期间跨越两个债券计息期间时，收益计提结果错误 
             * 因此，修改关键字FIInsTrade取值规则，增加”成交日”必须大于等于“债券本计息期间起始日“的判断规则*/

			//20130712 modified by liubo.Bug #8601
			//在获取当前计息期间的时候，考虑计息期间设置没数据的情况
			//若没有数据，则不做成交日不得小于计息起始日的判断
			//=============================
//            strSql += " and FBargainDate >= " + dbl.sqlDate(this.dInsStartDate);

			strSql += " and ((it.fsecuritycode is null) " +
					  " or (it.fsecuritycode is not null and FBargainDate >= it.FInsStartDate))";
			//===============end==============
            
            /**End  20130613 added by liubo.Story #3892.需求北京-(博时基金)QDIIV4.0(高)20130424001*/
            
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dResult = rs.getDouble("FBondIns");
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 重载的方法，加入计息方式的参数
     * @param dDate Date
     * @param culcType String --head 计头不计尾 --tail 计尾不计头 --both 头尾均计
     * @return double
     * @throws YssException
     * MS00293 QDV4赢时胜（上海）2009年3月6日01_AB
     */
    public double getNoClearFiAmount(java.util.Date dDate, String culcType) throws YssException {
        return getNoClearFiAmount(dDate, true, culcType, "0");
    }

    /**
     * 重载的方法，保留原有的调用
     * @param dDate Date
     * @param DayFi boolean
     * @return double
     * @throws YssException
     * MS00293 QDV4赢时胜（上海）2009年3月6日01_AB
     */
    public double getNoClearFiAmount(java.util.Date dDate, boolean DayFi) throws YssException {
        return getNoClearFiAmount(dDate, DayFi, "", "0"); //对计息方式的参数，不做处理。
    }

    /**
     * 重载方法，默认为结算日开始计息
     * chenjia add 20080321
     * @param dDate Date
     * @throws YssException
     * @return double
     * MS00293 QDV4赢时胜（上海）2009年3月6日01_AB
     */
    public double getNoClearFiAmount(java.util.Date dDate) throws YssException {
        return getNoClearFiAmount(dDate, true, "", "0"); //不做处理
    }
    
    public double getNoClearFiAmount(java.util.Date dDate, boolean DayFi, String ctlSettleDate) throws YssException {
        return getNoClearFiAmount(dDate, true, "", ctlSettleDate); //不做处理
    }

    //获取交易表中未结算的债券数量
    public double getNoClearFiAmount(java.util.Date dDate, boolean DayFi, String culcType, String ctlSettleDate) throws //----MS00293 QDV4赢时胜（上海）2009年3月6日01_AB 添加计息方式的参数
        YssException {
        String strSql = "";
        ResultSet rs = null;
        double dResult = 0;
        boolean analy1;
        boolean analy2;
        boolean analy3;
        //保存业务资料表中数据的计算日期 add by xuqiji 20090608:QDV4中保2009年06月05日01_B  MS00489 债券转换后计息时计算出两条一正一负的数据
        java.util.Date factSettleDate = null;
        //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
        java.util.Date fBargainDate = null;
        HashMap overSettleDate = null;
        HashMap overDate = new HashMap();
        //------------------------------------end-----------------------------------------------//
        try {
        	//--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(BondInsCfgFormulaN.hmKey != null && BondInsCfgFormulaN.hmKey.get("NoClearFiAmount") != null){
            	HashMap hmsecs = (HashMap)BondInsCfgFormulaN.hmKey.get("NoClearFiAmount");
            	String analysis1 = bondIns.getAnalysisCode1();
            	String analysis2 = bondIns.getAnalysisCode2();
            	if(analysis1.trim().length() == 0){
            		analysis1 = " ";
            	}
            	if(analysis2.trim().length() == 0){
            		analysis2 = " ";
            	}
            	String key = bondIns.getSecurityCode() + "\t" + 
            	bondIns.getPortCode() + "\t" +
            	bondIns.getAttrClsCode() + "\t" + 
            	bondIns.getInvestType() + "\t" +
            	analysis1 + "\t" + analysis2 + "\t" +
            	YssFun.formatDate(dDate,"yyyyMMdd");
            	if(hmsecs.get(key) != null){
            		HashMap hmSubSecs =(HashMap)hmsecs.get(key);
            		if(hmSubSecs.get("FAmount") != null){
            			return ((Double)hmSubSecs.get("FAmount")).doubleValue();
            		}else{
            			return 0;
            		}
            		
            	}else{
            		return 0;
            	}
            }        	
        	//--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            
			boolean bTPVer =false;//保存版本类型,合并版本时调整
        	CtlPubPara pubPara = null; //区分太平资产与QD版本不一致参数，合并版本时调整 by leeyu
        	pubPara =new CtlPubPara();
        	pubPara.setYssPub(pub);
        	String sPara =pubPara.getNavType();//通过净值表类型来判断
        	if(sPara!=null && sPara.trim().equalsIgnoreCase("new")){
        		bTPVer=false;//国内QDII统计模式
        	}else{
        		bTPVer=true;//太平资产统计模式
        	}
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
            strSql = "select sum(FTradeAmount*FAmountInd) as FAmount ,max(FSettleDate) as FSettleDate,max(FBargainDate) as FBargainDate  from " + //add by xuqiji 20090608:QDV4中保2009年06月05日01_B  MS00489 债券转换后计息时计算出两条一正一负的数据
                //---------------------------------------end----------------------------//
                "(select * from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState = 1) " +
                " a left join (select * from Tb_Base_TradeType where FCheckState = 1) b on a.FTradeTypeCode = b.FTradeTypeCode" +
                " where FSecurityCode = " +
                dbl.sqlString(bondIns.getSecurityCode()) +
                " and FPortCode = " + dbl.sqlString(bondIns.getPortCode()) + //增加组合的筛选,获取某组合的未结算债券数量.sj edit 20080818 bug 0000417
                " AND FAttrClsCode = " + dbl.sqlString(bondIns.getAttrClsCode()) +//2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                (analy1 ?
                 " and FInvMgrCode = " + dbl.sqlString(bondIns.getAnalysisCode1()) :
                 "") +
                (analy2 ?
                 " and FBrokerCode = " + dbl.sqlString(bondIns.getAnalysisCode2()) :
                 "");
            //----MS00293 QDV4赢时胜（上海）2009年3月6日01_AB  --------------------------
            if (culcType.equalsIgnoreCase("head")) { // 计头不计尾
                strSql +=
                    " and FBargainDate + 1 = " + dbl.sqlDate(dDate);
            } else if (culcType.equalsIgnoreCase("tail")) { //计尾不计头
                strSql +=
                    " and FBargainDate = " + dbl.sqlDate(dDate);
            } else if (culcType.equalsIgnoreCase("both")) { //
                strSql +=
                    " and FBargainDate <= " + dbl.sqlDate(dDate);
            } else { //默认的取数方式
                strSql +=
                    " and FBargainDate <= " + dbl.sqlDate(dDate);
            }
            //----------------------------------------------------------------------
//               " and FBargainDate <= " + dbl.sqlDate(dDate);
            // " and " + dbl.sqlDate(dDate) + " < FSettleDate";
            //---------------当为true时，从买入结算日当日开始计息。chenjia 20080321 add--------------------------//
            if (DayFi) {
            	if("0".equalsIgnoreCase(ctlSettleDate)){
            		strSql +=
                        " and " + dbl.sqlDate(dDate) + " < FSettleDate";
            	}else if("1".equalsIgnoreCase(ctlSettleDate)){ //fj 2012.03.13
            		strSql +=
                        " and (1+ " + dbl.sqlDate(dDate) + ") < FSettleDate";
            	}   
            } else { //当为false时，买入结算日后一日开始计息。
            	if("0".equalsIgnoreCase(ctlSettleDate)){
            		strSql +=
                        " and " + dbl.sqlDate(dDate) + " <= FSettleDate";
            	}else if("1".equalsIgnoreCase(ctlSettleDate)){ //fj 2012.03.13
            		strSql +=
                        " and (1+" + dbl.sqlDate(dDate) + ") <= FSettleDate";
            	} 
            }
            //-----------------------------------------------------------------------------------
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dResult = rs.getDouble("FAmount");
                //add by xuqiji 20090608:QDV4中保2009年06月05日01_B  MS00489 债券转换后计息时计算出两条一正一负的数据
                //获取证券的结算日期和交易日期，对于当天有交易的证券
                //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
                factSettleDate = rs.getDate("FSettleDate"); //结算日期
                fBargainDate = rs.getDate("FBargainDate"); //交易日期
                //-------------------------------end-----------------------------------------//
            }
            //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
            overDate.put("FSettleDate", factSettleDate);
            overDate.put("FBargainDate", fBargainDate);
            //-------------------------end------------------------//
            //add by xuqiji 20090608:QDV4中保2009年06月05日01_B  MS00489 债券转换后计息时计算出两条一正一负的数据-----
            overSettleDate = getFactSettleDate(dDate, analy1); //根据条件查询出买入或卖出证券的结算日期和交易日期

			if(bTPVer){
				//-------MS00693 QDV4中保2009年09月10日01_B sj --------------------------------------------------------------
		//         if (overSettleDate.get("FSettleDate") == null) { //判断是否为空，若为空直接把结果返回出去
		         if (overSettleDate == null){
		            dResult = YssD.mul(dResult, -1);
		            return dResult;
		         }
		         //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
		         if (factSettleDate != null) {
		             dResult = YssD.mul(YssD.add(dResult, this.getIntegratedData(overDate, dDate, analy1, analy2, DayFi)), -1);
		         } else {
		             dResult = YssD.add(dResult, this.getAllIntegratedData(overSettleDate, dDate, analy1, analy2, DayFi));//MS00693 QDV4中保2009年09月10日01_B sj 调整方法
		         }
		//----------------------------------------------------------------------------------------------------------
			}else{
	            if (overSettleDate.get("FSettleDate") == null) { //判断是否为空，若为空直接把结果返回出去
	                dResult = YssD.mul(dResult, -1);
	                return dResult;
	            }
	            //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
	            if (factSettleDate != null) {
	                dResult = YssD.mul(YssD.add(dResult, this.getIntegratedData(overDate, dDate, analy1, analy2, DayFi)), -1);
	            } else {
	                dResult = YssD.mul(YssD.add(dResult, this.getIntegratedData(overSettleDate, dDate, analy1, analy2, DayFi)), -1);
	            }
			}
            //-------------------------------------------end----------------------------------------------//
            return dResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    
    ///add by luopc , QDV4易方达2011年6月9日02_A 获取当日交易清算的数据，即当日T+0的交易数量
    public double getTradeClearAmountD(java.util.Date dDate) throws YssException {
   	 ResultSet rs=null;
   	 double dResult=0;
   	 try{
   		 String strSql="select sum(ftradeamount * FAmountInd) as famount "
   	    	 +"from "+pub.yssGetTableName("Tb_Data_Intbakbond")+" a"
   	    	 +" left join (select * from Tb_Base_TradeType where FCheckState = 1) b on a.FTradeTypeCode ="
   	    	 +"b.FTradeTypeCode "
   	    	 +" and  a.FSecurityCode = "+ dbl.sqlString(bondIns.getSecurityCode())
   	    	 +" and a.FCheckState = 1"
   	    	 +" AND a.FAttrClsCode = ' '"
   	    	 +" and a.FPortCode="+ dbl.sqlString(bondIns.getPortCode())
   	    	 +" and FBargainDate=FSettleDate"
   	    	 +" and FSettleDate="+dbl.sqlDate(dDate);
   		 	rs = dbl.openResultSet(strSql);
   	    	 if(rs.next()){
   	    		 dResult = rs.getDouble("FAmount");
   	    	 }
   	    	 return dResult ;
   		 
   	    }
        catch (Exception e) {
           throw new YssException(e);
       } finally {
           dbl.closeResultSetFinal(rs);
       }
   	 
   	 
    }
   ///银行间债券交易数据，获取未交割的数量。add  by luopc（整个方法）QDV4易方达2011年6月9日02_A
   public double getNoClearFiAmountD(java.util.Date dDate) throws 
   YssException {
   String strSql = "";
   ResultSet rs = null;
   double dResult = 0;
   boolean analy1;
   boolean analy2;
   boolean analy3;
   //保存业务资料表中数据的计算日期 add by xuqiji 20090608:QDV4中保2009年06月05日01_B  MS00489 债券转换后计息时计算出两条一正一负的数据
   java.util.Date factSettleDate = null;
   //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
   java.util.Date fBargainDate = null;
   HashMap overSettleDate = null;
   //delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
   //HashMap overDate = new HashMap();
   //------------------------------------end-----------------------------------------------//
   try {
	   

	   /**Start 20130704 deleted by liubo.Bug #8539.QDV4赢时胜(北京)2013年07月02日01_B*/
	   
			//--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
//			if (BondInsCfgFormulaN.hmKey != null && BondInsCfgFormulaN.hmKey.get("NoClearFiAmountD") != null) {
//				HashMap hmsecs = (HashMap) BondInsCfgFormulaN.hmKey.get("NoClearFiAmountD");
//				String analysis1 = bondIns.getAnalysisCode1();
//				String analysis2 = bondIns.getAnalysisCode2();
//				if (analysis1.trim().length() == 0) {
//					analysis1 = " ";
//				}
//				if (analysis2.trim().length() == 0) {
//					analysis2 = " ";
//				}
//				String key = bondIns.getSecurityCode() + "\t" + bondIns.getPortCode() + "\t" + bondIns.getAttrClsCode()
//						+ "\t" + bondIns.getInvestType() + "\t" + analysis1 + "\t" + analysis2 + "\t"
//						+ YssFun.formatDate(dDate, "yyyyMMdd");
//				if (hmsecs.get(key) != null) {
//					return ((Double) hmsecs.get(key)).doubleValue();
//				} else {
//					return 0;
//				}
//			}
			//--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
	   
	   /**End 20130704 deleted by liubo.Bug #8539.QDV4赢时胜(北京)2013年07月02日01_B*/
	   
		boolean bTPVer =false;//保存版本类型,合并版本时调整
   	CtlPubPara pubPara = null; //区分太平资产与QD版本不一致参数，合并版本时调整 by leeyu
   	pubPara =new CtlPubPara();
   	pubPara.setYssPub(pub);
   	String sPara =pubPara.getNavType();//通过净值表类型来判断
   	if(sPara!=null && sPara.trim().equalsIgnoreCase("new")){
   		bTPVer=false;//国内QDII统计模式
   	}else{
   		bTPVer=true;//太平资产统计模式
   	}
       analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
       analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
       analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
       //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
       strSql = "select sum(FTradeAmount*FAmountInd) as FAmount ," +
       		" max(FSettleDate) as FSettleDate,max(FBargainDate) as FBargainDate  " +
       		
       		" ,FInvestType " +	//20130704 added by liubo.Bug #8539.获取投资类型
       		
       		" from " + //add by xuqiji 20090608:QDV4中保2009年06月05日01_B  MS00489 债券转换后计息时计算出两条一正一负的数据
           //---------------------------------------end----------------------------//
           "(select * from " + pub.yssGetTableName("Tb_Data_Intbakbond") +
           " where FCheckState = 1) " +
           " a left join (select * from Tb_Base_TradeType where FCheckState = 1) b on a.FTradeTypeCode = b.FTradeTypeCode" +
           " where FSecurityCode = " +
           dbl.sqlString(bondIns.getSecurityCode()) +
           " and FPortCode = " + dbl.sqlString(bondIns.getPortCode()) + //增加组合的筛选,获取某组合的未结算债券数量.sj edit 20080818 bug 0000417
           " AND FAttrClsCode = " + dbl.sqlString(bondIns.getAttrClsCode()) +//2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
           (analy1 ?
            " and FInvMgrCode = " + dbl.sqlString(bondIns.getAnalysisCode1()) :
            "") +
           (analy2 ?
            " and FBrokerCode = " + dbl.sqlString(bondIns.getAnalysisCode2()) :
            "");

           strSql +=
               " and FBargainDate <= " + dbl.sqlDate(dDate);


           strSql +=
               " and " + dbl.sqlDate(dDate) + " < FSettleDate " +
               " group by FInvestType";	//20130704 added by liubo.Bug #8539.获取投资类型
       
       //-----------------------------------------------------------------------------------
       rs = dbl.openResultSet(strSql);
       if (rs.next()) {
//           dResult = rs.getDouble("FAmount");
           //add by xuqiji 20090608:QDV4中保2009年06月05日01_B  MS00489 债券转换后计息时计算出两条一正一负的数据
           //获取证券的结算日期和交易日期，对于当天有交易的证券
           //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
           //--- delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
//           factSettleDate = rs.getDate("FSettleDate"); //结算日期
//           fBargainDate = rs.getDate("FBargainDate"); //交易日期
           //--- delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
           //-------------------------------end-----------------------------------------//
           
    	   /**Start 20130704 added by liubo.Bug #8539.QDV4赢时胜(北京)2013年07月02日01_B
    	    * 主要修改hmsecs的判断取值条件。此段代码若放在方法最顶部会有一个问题：
    	    * 之前拼接key变量的值时，取投资类型是用的bondIns.getInvestType()，
    	    * 但实际上在调用此类的init方法之前，被传入的YssBondIns对象并没有调用setInvestType()进行赋值
    	    * 因此在调用bondIns.getInvestType()始终获取到空字符串
    	    * 所以将此段代码移动到这个位置，实时使用rs.getString("FInvestType")获取投资类型，避免上述问题*/
           if (BondInsCfgFormulaN.hmKey != null && BondInsCfgFormulaN.hmKey.get("NoClearFiAmountD") != null) {
				HashMap hmsecs = (HashMap) BondInsCfgFormulaN.hmKey.get("NoClearFiAmountD");
				String analysis1 = bondIns.getAnalysisCode1();
				String analysis2 = bondIns.getAnalysisCode2();
				//在这里有个问题，之前设置analysis1的值为空格，但是BondInsCfgFormulaN类在设置KEY值时，analysis1被设置成空字符串
				//因此需要把这里的analysis1也设置成空字符串
				//================================
				if (analysis1.trim().length() == 0) {
					analysis1 = "";
				}
				//==============end==================
				if (analysis2.trim().length() == 0) {
					analysis2 = " ";
				}
				String key = bondIns.getSecurityCode() + "\t" + bondIns.getPortCode() + "\t" + bondIns.getAttrClsCode()
						+ "\t" + rs.getString("FInvestType") + "\t" + analysis1 + "\t" + analysis2 + "\t"
						+ YssFun.formatDate(dDate, "yyyyMMdd");
				if (hmsecs.get(key) != null) {
					return ((Double) hmsecs.get(key)).doubleValue();
				} else {
					return 0;
				}
			}
           else
           {
        	   dResult = rs.getDouble("FAmount");
           }
    	   /**End 20130704 added by liubo.Bug #8539.QDV4赢时胜(北京)2013年07月02日01_B*/
       }


       //-------------------------------------------end----------------------------------------------//
       return dResult;
   }
    catch (Exception e) {
       throw new YssException(e);
   } finally {
       dbl.closeResultSetFinal(rs);
   }
}
    /**
     * 如果dDate为节假日则返回节假日前一天的交易数量，否则返回0
     * 2010-01-14 蒋锦 添加 QDV4赢时胜（上海）2010年1月12日02_B
     * @param dDate：业务日期
     * @return
     * @throws YssException
     */
    private double getBeforeHoildayTradeAmount(java.util.Date dDate) throws YssException{
    	double dbTradeAmount = 0;
    	BaseOperDeal operDeal = new BaseOperDeal();
    	try{
    		operDeal.setYssPub(pub);
    		if(!dDate.equals(operDeal.getWorkDay(bondIns.getHolidaysCode(), dDate, 0))){
    			dbTradeAmount = getTDayTradeAmount(dDate, true);
    		}else{
    			dbTradeAmount = 0;
    		}
    		
    	}catch(Exception ex){
    		throw new YssException("获取节假日前一天的交易数量出错！", ex);
    	}
    	return dbTradeAmount;
    }
    
     
    /**
     * 获取T日债券交易数量 卖出为负数买入为正数，返回当日买入卖出的合计值
     * 2010-01-14 蒋锦 添加 QDV4赢时胜（上海）2010年1月12日02_B
     * @param dDate 业务日期
     * @param isJudgeHoliday 是否判断节假日，如果输入true当的dDate为节假日时将取节假日前一天的交易数量
     * @return 交易数量
     * @throws YssException
     */
    private double getTDayTradeAmount(java.util.Date dDate, boolean isJudgeHoliday) throws YssException{
    	double dbAmount = 0;
    	String strSql = "";
        ResultSet rs = null;
        boolean analy1;
        boolean analy2;
        java.util.Date dBargainDate; //T日的日期
    	try{
    		if(isJudgeHoliday){
    			BaseOperDeal operDeal = new BaseOperDeal();
    			operDeal.setYssPub(pub);
    			//判断dDate是否节假日
    			if(!dDate.equals(operDeal.getWorkDay(bondIns.getHolidaysCode(), dDate, 0))){
    				dBargainDate = operDeal.getWorkDay(bondIns.getHolidaysCode(), dDate, -1);
    			} else {
    				dBargainDate = dDate;
    			}
    		} else {
    			dBargainDate = dDate;
    		}
    		analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            
    		strSql = "select sum(FTradeAmount*FAmountInd) as FAmount from " + 
            "(select * from " + pub.yssGetTableName("Tb_Data_SubTrade") +
            " where FCheckState = 1) " +
            " a left join (select * from Tb_Base_TradeType where FCheckState = 1)" +
            " b on a.FTradeTypeCode = b.FTradeTypeCode" +
            " where FSecurityCode = " +
            dbl.sqlString(bondIns.getSecurityCode()) +
            " and FPortCode = " + dbl.sqlString(bondIns.getPortCode()) + 
            " AND FAttrClsCode = " + dbl.sqlString(bondIns.getAttrClsCode()) +
            (analy1 ?
             " and FInvMgrCode = " + dbl.sqlString(bondIns.getAnalysisCode1()) :
             "") +
            (analy2 ?
             " and FBrokerCode = " + dbl.sqlString(bondIns.getAnalysisCode2()) :
             "") +
             " AND FBargainDate = " + dbl.sqlDate(dBargainDate);
    		
    		rs = dbl.openResultSet(strSql);
    		if(rs.next()){
    			dbAmount = rs.getDouble("FAmount");
    		}
    	}catch(Exception ex){
    		throw new YssException("获取T日债券交易数量出错！", ex);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    	return dbAmount;
    }

    private double getNoClearFiInterest(java.util.Date dInsDate) throws YssException{
        return getNoClearFiInterest(dInsDate, true, "");
    }
    
    /**
     * 获取节假日前一天的交易利息，如果dDate为节假日折返回前一工作日的利息，否则返回0
     * 2010-01-14 蒋锦 添加 QDV4赢时胜（上海）2010年1月12日02_B
     * @param dDate
     * @return
     * @throws YssException
     */
    private double getBeforeHoildayFiInterest(java.util.Date dDate) throws YssException{
    	double dbMoney = 0;
    	BaseOperDeal operDeal = new BaseOperDeal();
    	try{
    		operDeal.setYssPub(pub);
    		if(!dDate.equals(operDeal.getWorkDay(bondIns.getHolidaysCode(), dDate, 0))){
    			dbMoney = 
    				getNoClearFiInterest(
    					operDeal.getWorkDay(bondIns.getHolidaysCode(), dDate, -1), 
    					false, 
    					"tail");
    		}else{
    			dbMoney = 0;
    		}
    	}catch(Exception ex){
    		throw new YssException("获取节假日前一天的未结算交易利息出错！", ex);
    	}
    	return dbMoney;
    }

    /**
     *
     * @param dInsDate Date
     * @param DayFi boolean
     * @param culcType String
     * @return double
     * @throws YssException
     */
    private double getNoClearFiInterest(java.util.Date dInsDate,
                                        boolean DayFi,
                                        String culcType) throws YssException{
        double dbInterest = 0;
        ResultSet rs = null;
        String strSql = "";
        boolean analy1;
        boolean analy2;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");

            //2009.09.15 蒋锦 修改
            //如果是收入默认为流出，所有应收应付金额都要乘以方向
            strSql = "SELECT SUM(FMoney) AS FMoney" +
                " FROM (select MAX(FSettleDate) as FSettleDate," +
                " MAX(FBargainDate) as FBargainDate, FSecurityCode, FPortCode," +
                " FAttrClsCode" +
                " from (select *" +
                " from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState = 1 " +
                (analy1 ?
                 " and FInvMgrCode = " + dbl.sqlString(bondIns.getAnalysisCode1()) :
                 "") +
                (analy2 ?
                 " and FBrokerCode = " + dbl.sqlString(bondIns.getAnalysisCode2()) :
                 "") +
                ") a" +
                " left join (select *" +
                " from Tb_Base_TradeType" +
                " where FCheckState = 1) b on a.FTradeTypeCode =" +
                " b.FTradeTypeCode" +
                " where FSecurityCode = " + dbl.sqlString(bondIns.getSecurityCode()) +
                " AND FAttrClsCode = " + dbl.sqlString(bondIns.getAttrClsCode()) +
                " AND FPortCode = " + dbl.sqlString(bondIns.getPortCode()) +
                (analy1 ?
                 " and FInvMgrCode = " + dbl.sqlString(bondIns.getAnalysisCode1()) :
                 "") +
                (analy2 ?
                 " and FBrokerCode = " + dbl.sqlString(bondIns.getAnalysisCode2()) :
                 "");
            if (culcType.equalsIgnoreCase("head")) { // 计头不计尾
                strSql +=
                    " and FBargainDate + 1 = " + dbl.sqlDate(dInsDate);
            } else if (culcType.equalsIgnoreCase("tail")) { //计尾不计头
                strSql +=
                    " and FBargainDate = " + dbl.sqlDate(dInsDate);
            } else if (culcType.equalsIgnoreCase("both")) { //
                strSql +=
                    " and FBargainDate <= " + dbl.sqlDate(dInsDate);
            } else { //默认的取数方式
                strSql +=
                    " and FBargainDate <= " + dbl.sqlDate(dInsDate);
            }
            //---------------当为true时，从买入结算日当日开始计息。--------------------------//
            if (DayFi) {
                strSql +=
                    " and " + dbl.sqlDate(dInsDate) + " < FSettleDate";
            } else { //当为false时，买入结算日后一日开始计息。
                strSql +=
                    " and " + dbl.sqlDate(dInsDate) + " <= FSettleDate";
            }
            //-----------------------------------------------------------------------------------
            strSql = strSql + " GROUP BY FSecurityCode, FPortCode, FAttrClsCode) tra" +
                " JOIN (SELECT CASE" +
                " WHEN FTsfTypeCode = '02' THEN" +
                " FMoney * -1" +
                " ELSE" +
                " FMoney" +
                " END * FInOut AS FMoney," +
                " FTransDate,FPortCode,FSecurityCode,FAttrClsCode" +
                " FROM " + pub.yssGetTableName("Tb_Data_Secrecpay") +
                " WHERE FCheckState = 1" +
                (analy1 ?
                 " and FAnalysisCode1 = " + dbl.sqlString(bondIns.getAnalysisCode1()) :
                 "") +
                (analy2 ?
                 " and FBrokerCode = " + dbl.sqlString(bondIns.getAnalysisCode2()) :
                 "") +
                " AND (FSubTsfTypeCode = '06FI_B' OR FSubTsfTypeCode = '02FI_B')) rec ON tra.FBargainDate = rec.FTransDate" +
                " AND tra.FPortCode = rec.FPortCode AND tra.FSecurityCode = rec.FSecurityCode AND tra.FAttrClsCode = rec.FAttrClsCode";
            rs = dbl.openResultSet(strSql);
            if(rs.next()){
                dbInterest = rs.getDouble("FMoney");
            }
        } catch (Exception ex) {
            throw new YssException("获取未清算的债券利息出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
        return dbInterest;
    }

    /**
     * 公式 : (360*(Y2-Y1)+30*(M2-M1)+(D2-D1))/360
     * @param dStartDate Date
     * @param dEndDate Date
     * @return double
     * @throws YssException
     * 1:若D2与D1都为二月的最后一天,则D2=30
     * 2:若D1为二月的最后一天,则D1=30
     * 3:若D2=31且D1=30或31 则D2=30
     * 4:若D1=31 则D1=30
     */
    public double factorU30(java.util.Date dStartDate, java.util.Date dEndDate) throws
        YssException {
    	return this.factorU30(dStartDate, dEndDate, true);
    }

    /**
     * 为确保原有利息公式不受影响，重载factorU30，BUG2361 QDV4建行2011年08月02日02_B panjunfang add 20110810
     * 公式 : (360*(Y2-Y1)+30*(M2-M1)+(D2-D1))/360
     * @param dStartDate Date
     * @param dEndDate Date
     * @param isEndDateAdd1  true 表示在利息公式中配置计息日期加1， false表示利息公式中直接传入计息日期当天，在方法中加1  
     * @return double
     * @throws YssException
     * 1:若D2与D1都为二月的最后一天,则D2=30
     * 2:若D1为二月的最后一天,则D1=30
     * 3:若D2=31且D1=30或31 则D2=30
     * 4:若D1=31 则D1=30
     */
    public double factorU30(java.util.Date dStartDate, java.util.Date dEndDate,boolean isEndDateAdd1) throws
    	YssException {
        int iEndYear = 0, iStartYear = 0, iStartMonth = 0, iEndMonth = 0,
        iStartDay = 0, iEndDay = 0;
        int iStartFriDate = 0, iEndFriDate = 0; //存放两个日期里二月的天数
        double dDate = 0;
		//--- add by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B start---//
		int finalStartDay = 0, finalEndDay = 0;//最终的计息起始日，最终的计息日
		//--- add by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B end---//
        try {
			iStartYear = YssFun.getYear(dStartDate);
			iStartMonth = YssFun.getMonth(dStartDate);
			iStartDay = YssFun.getDay(dStartDate);
			iStartFriDate = YssFun.endOfMonth(iStartYear, 2);
			iEndYear = YssFun.getYear(dEndDate);
			iEndMonth = YssFun.getMonth(dEndDate);
			iEndDay = YssFun.getDay(dEndDate);
			iEndFriDate = YssFun.endOfMonth(iEndYear, 2);
			
			//--- add by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B start---//
			finalStartDay = iStartDay;
			finalEndDay = iEndDay;
			//--- add by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B end---//
			
			if (iStartMonth == 2 && iEndMonth == 2) { // 当都是月末且都是二月份的最后一天
				if (iEndDay == iEndFriDate && iStartDay == iStartFriDate) {
//					iEndDay = 30;//delete by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B
					finalEndDay = 30;//add by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B
				}
			} else if (iStartMonth == 2) { // 当D1是月末
				if (iStartDay == iStartFriDate) { // && iEndDay != iEndFriDate
//					iStartDay = 30;//delete by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B
					finalStartDay = 30;//add by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B
				}
			}
			
			//--- delete by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B start---//
//			if (iStartDay == 31 && iEndDay != 31) {
//				iStartDay = 30;
//			}
//			if (iEndDay == 31 && (iStartDay == 31 || iStartDay == 30)) { // 修改 ly 因为这是并且的关系
//				iEndDay = 30;
//				
//				// #898 希望能够新增利息算法，处理这种特殊债券,计息起始日为31日的，则需要在计息起始日那天计提出利息
//				// 这里如果仅仅把计息日减1，不把计息起始日减1，利息就会翻倍
//				if (iStartDay == 31 /*&& YssFun.dateDiff(dEndDate, dStartDate) == 0*/) {
//					iStartDay = iStartDay - 1;
//				}
//			}
			//--- delete by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B end---//
			
			//--- add by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B start---//
			if(iEndDay == 31 && (iStartDay == 30 || iStartDay == 31)){
				finalEndDay = 30;
			}
			if(iStartDay == 31){
				finalStartDay = 30; 
			}
			//--- add by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B end---//
			
			if (isEndDateAdd1) {
				//--- edit by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B start---//
				//iStartDay 改为 finalStartDay，iEndDay 改为 finalEndDay
				dDate = (360 * (iEndYear - iStartYear) + 30 * (iEndMonth - iStartMonth) + (finalEndDay - finalStartDay)) * 1.0 / 360;
				//--- edit by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B end---//
			} else {
				//--- edit by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B start---//
				//iStartDay 改为 finalStartDay，iEndDay 改为 finalEndDay
				dDate = (360 * (iEndYear - iStartYear) + 30 * (iEndMonth - iStartMonth) + (finalEndDay - finalStartDay + 1)) * 1.0 / 360;
				//--- edit by songjie 2014.02.26 BUG 88504 QDV4国泰基金2014年02月08日01_B end---//
			}
	    } catch (Exception e) {
	        throw new YssException("计算factorU30出错!\n", e);
	    }
	    return dDate;
    }
    /**
     * 公式: Days not in leap year/365 + Days in leap year/366
     * @param dStartDate Date
     * @param dEndDate Date
     * @return int
     * @throws YssException
     * 将闰年与平年的日期分别计算
     */
    public double factorActual(java.util.Date dStartDate,
                               java.util.Date dEndDate) throws YssException {
        int iStartYear = 0, iEndYear = 0;
        int iStartSumDate = 0, iEndSumDate = 0;
        double dDate = 0;
        try {
            java.util.Date dTmp = null;
            if (dStartDate.after(dEndDate)) {
                dTmp = dStartDate;
                dStartDate = dEndDate;
                dEndDate = dTmp;
            }
            iStartYear = YssFun.getYear(dStartDate);
            iEndYear = YssFun.getYear(dEndDate);
            if (iStartYear != iEndYear) {
                iStartSumDate = YssFun.dateDiff(dStartDate,
                                                YssFun.toDate(iStartYear + "-12-31"));
                iEndSumDate = YssFun.dateDiff(YssFun.toDate(iEndYear + "-01-01"),
                                              dEndDate);
                if (YssFun.isLeapYear(iStartYear)) {
                    dDate += (iStartSumDate * 1.0 / 366);
                } else {
                    dDate += (iStartSumDate * 1.0 / 365);
                }
                if (YssFun.isLeapYear(iEndYear)) {
                    dDate += (iEndSumDate * 1.0 / 366);
                } else {
                    dDate += (iEndSumDate * 1.0 / 365);
                }
                for (int iYear = iStartYear + 1; iYear < iEndYear; iYear++) {
                    if (YssFun.isLeapYear(iYear)) {
                        dDate += (366 * 1.0 / 366);
                    } else {
                        dDate += (365 * 1.0 / 365);
                    }
                }
            } else {
                iStartSumDate = 0;
                iEndSumDate = YssFun.dateDiff(dStartDate, dEndDate);
                if (YssFun.isLeapYear(iStartYear)) {
                    dDate += (iEndSumDate * 1.0 / 366);
                } else {
                    dDate += (iEndSumDate * 1.0 / 365);
                }
            }
        } catch (Exception e) {
            throw new YssException("计算factorActual方法出错!\n", e);
        }
        return dDate;
    }

    /**
     * 设置两个日期比较关系的配置字符串。此公式专用于动态编译用的返回值。sj edit 20080708.
     * @param isnEndDate Date
     * @param isnDate Date
     * @param types String
     * @return String
     * @throws YssException
     */
    public String compareDate(java.util.Date isnEndDate, String types,
                              java.util.Date isnDate
        ) throws
        YssException {
        String reStr = "";
        String endDate = "";
        String curDate = "";
        try {
            endDate = YssFun.formatDate(isnEndDate, "yyyy-MM-dd");
            curDate = YssFun.formatDate(isnDate, "yyyy-MM-dd");
        } catch (Exception e) {
            throw new YssException("设置日期比较出错!\n");
        }
        if (types.equalsIgnoreCase("<")) { //后一个日期数值小于前一个日期数值.
            reStr = "YssFun.dateDiff(YssFun.parseDate(\"" + curDate + "\")," +
                "YssFun.parseDate(\"" + endDate + "\")) < 0";
        } else if (types.equalsIgnoreCase(">")) { //大于.
            reStr = "YssFun.dateDiff(YssFun.parseDate(\"" + curDate + "\")," +
                "YssFun.parseDate(\"" + endDate + "\")) > 0";
        } else if (types.equalsIgnoreCase("==")) { //等于.
            reStr = "YssFun.dateDiff(YssFun.parseDate(\"" + curDate + "\")," +
                "YssFun.parseDate(\"" + endDate + "\")) == 0";
        } else if (types.equalsIgnoreCase(">=")) { //大于等于.
            reStr = "YssFun.dateDiff(YssFun.parseDate(\"" + curDate + "\")," +
                "YssFun.parseDate(\"" + endDate + "\")) >= 0";
        } else if (types.equalsIgnoreCase("<=")) { //小于等于.
            reStr = "YssFun.dateDiff(YssFun.parseDate(\"" + curDate + "\")," +
                "YssFun.parseDate(\"" + endDate + "\")) <= 0";
        }
        return reStr;
    }
    
    /**
     * 获取计息起始日、截止日、付息日
     */
    public void getNextStartDateAndEndDate(
                                           HashMap hmReturn,
                                           java.util.Date dBigInsStartDate1,
                                           java.util.Date dBigInsEndDate1,
                                           java.util.Date dBigFXDate) throws YssException {
        try {            
            hmReturn.put("InsStartDate", dBigInsStartDate1);
            hmReturn.put("InsEndDate", dBigInsEndDate1);
            hmReturn.put("InsEndDate", dBigFXDate);               
        } catch (Exception e) {
            throw new YssException("计算计息起始日期和截止日期出错！\n", e);
        }
    }
    

    /**
     * 重载的计算债券计息期间起息日和截止日的方法，不再依赖查询债券信息的结果集
     * 2009-09-02 蒋锦 添加 MS00656    关于实际利率的需求    QDV4赢时胜(上海)2009年8月24日01_A
     * @param dTheDay Date：当前日期
     * @param dBigInsStartDate Date：总的起息日
     * @param dBigInsEndDate Date：总的截止日
     * @param iFrequency double：年付息频率
     * @param hmReturn HashMap
     * @throws YssException
     */
    public void getNextStartDateAndEndDate(java.util.Date dTheDay,
                                           java.util.Date dBigInsStartDate,
                                           java.util.Date dBigInsEndDate,
                                           double iFrequency,
                                           HashMap hmReturn,
                                           String holidayCode) throws YssException {
        int iBigMonth = 0; //总的起始日到总的截止日的实际相隔月份数
        int iTermA = 0; //每个付息期间的间隔月份数
        int iTermB = 0; //实际已付息的月份数
        int iDegreeC = 0; //已计息次数
        int iFxCs = 0; //总的付息次数
        BaseOperDeal deal = new BaseOperDeal();
    	deal.setYssPub(this.pub);
        try {
            if (hmReturn == null) {
                throw new YssException("输入参数出错，Map 不能为 null!");
            }
            //如果是一次还本付息，那么起息日和截止日就是总的起息日和截止日
            if (iFrequency == 0) {
                hmReturn.put("InsStartDate", dBigInsStartDate);
                hmReturn.put("InsEndDate", dBigInsEndDate);
                //--- add by songjie 2013.04.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
                hmReturn.put("PaidInterest", iDegreeC + 1);
				//--- edit by songjie 2013.06.18 BUG 8324 QDV4赢时胜(北京)2013年06月19日03_B start---//
				//处理一次还本付息 的派息日获取逻辑 若为 一次还本付息 则 派息日 = 本期间计息截止日
                hmReturn.put("InsFXDate", dBigInsEndDate);
                //--- edit by songjie 2013.06.18 BUG 8324 QDV4赢时胜(北京)2013年06月19日03_B end---//
				//--- add by songjie 2013.04.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
                return;
            }
            iBigMonth = YssFun.monthDiff(dBigInsStartDate, dBigInsEndDate);
            iFxCs = (int) (iBigMonth / (12 / iFrequency));
            //如果取模不等于0，说明从起始日到截止日间相隔不是整数个付息次数，
            //那么公式 iFrequency = iBigMonth / (12 / iFrequency)，算出来的付息次数要比实际的付息次数少一个月
            if (iBigMonth % (12 / iFrequency) != 0) {
                iFxCs += 1;
            }
            //edit by lidaolong 20110426 BUG1812权益处理时，出现错误 
            if (iFxCs==0){
            	hmReturn.put("error", "erroInfo");
            	return ;
            }//end by lidaolong
            	 iTermA = YssFun.monthDiff(dBigInsStartDate,
                         YssFun.addDate(dBigInsEndDate, 1,
                        		 Calendar.DAY_OF_MONTH)) /
                        		 iFxCs;
           
           
            //如果当前日期的 DayOfMonth 小于 计息起始日的 DayOfMonth，同时当前日期的 DayOfMonth 不是本月的最后一天，
            //那么说明从计息起始日到计息截止日的最后一个月的天数不满一个月，
            //所以使用 YssFun.monthDiff() 函数计算出来的间隔月份就要减一个月才是实际的相隔月份
            if (YssFun.getDay(dTheDay) < YssFun.getDay(dBigInsStartDate) &&
                YssFun.getDay(dTheDay) != YssFun.endOfMonth(dTheDay)) {
                iTermB = YssFun.monthDiff(dBigInsStartDate, dTheDay) - 1;
            } else {
                iTermB = YssFun.monthDiff(dBigInsStartDate, dTheDay);
            }
            iDegreeC = iTermB / iTermA;
            iDegreeC = iDegreeC < 0 ? 0 : iDegreeC;

            int iYear = YssFun.getYear(dBigInsStartDate) +
                (int) (iTermA * iDegreeC / 12);
            int iMonth = YssFun.getMonth(dBigInsStartDate) +
                iTermA * iDegreeC % 12;
            int iDay = 0;
            if (iMonth > 12) {
                iYear += 1;
                iMonth -= 12;
            }
            if (YssFun.endOfMonth(iYear, iMonth) >= YssFun.getDay(dBigInsStartDate)) {
                iDay = YssFun.getDay(dBigInsStartDate);
            } else {
                iDay = YssFun.endOfMonth(iYear, iMonth);
            }
            GregorianCalendar gcStart = new GregorianCalendar(iYear, iMonth - 1,
                iDay);
            //add by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
            hmReturn.put("PaidInterest", iDegreeC + 1);
            //装载下一计息起始日
            hmReturn.put("InsStartDate", gcStart.getTime());
            iMonth += iTermA;
            if (iMonth > 11) {
                iYear += 1;
                iMonth -= 12;
            }
            if (YssFun.endOfMonth(iYear, iMonth) >= YssFun.getDay(dBigInsStartDate)) {
                iDay = YssFun.getDay(dBigInsStartDate);
            } else {
                iDay = YssFun.endOfMonth(iYear, iMonth);
            }
            //使用 YssFun.getMonth() 计算得到的月份是实际的月份，
            //如果使用年月日来初始化日历，日历的启示月份是0，所以初始化日历时 iMonth 需要减1
            GregorianCalendar gcEnd = new GregorianCalendar(iYear, iMonth - 1,
                iDay);
            gcEnd.add(Calendar.DAY_OF_MONTH, -1);
            //装载下一计息截止日
            if (gcEnd.getTime().compareTo(dBigInsEndDate) > 0) {
                hmReturn.put("InsEndDate", dBigInsEndDate);
            } else {
                hmReturn.put("InsEndDate", gcEnd.getTime());
            }
            //装载付息日
            if(holidayCode.length() > 1){
            	//--- edit by songjie 2013.06.18 BUG 8324 QDV4赢时胜(北京)2013年06月19日03_B start---//
            	//如果  债券付息期间截止日的年份 大于 当前系统日期对应的年份，则 付息日期 的返回值 本期间截止日
            	java.util.Date endDate = (java.util.Date)hmReturn.get("InsEndDate");
            	java.util.Date sysDate = new Date();
        		/**Start 20140117 modified by liubo.Bug #87294.赢时胜(上海开发)2014年01月09日03_B
        		 * 若到帐日等于截止日，则可能会出现派息日晚于到帐日的情况，客户觉得推算规则不太合理。
        		 * 在此修改为到帐日=截止日+1自然日*/
            	if(YssD.sub(YssFun.getYear(endDate), YssFun.getYear(dTheDay)) > 0){
            		hmReturn.put("InsFXDate", YssFun.addDay((java.util.Date)hmReturn.get("InsEndDate"), 1));
            		/**End 20140117 modified by liubo.Bug #87294.赢时胜(上海开发)2014年01月09日03_B*/
            	}else{
            		hmReturn.put("InsFXDate", deal.getWorkDay(holidayCode, (java.util.Date)hmReturn.get("InsEndDate"), 1));
            	}
            	//--- edit by songjie 2013.06.18 BUG 8324 QDV4赢时胜(北京)2013年06月19日03_B end---//
            }
        } catch (Exception e) {
            throw new YssException("计算计息起始日期和截止日期出错！\n", e);
        }
    }

    /**
     * 计算当前日期后的下一个计息起始日和截止日
     * @param dTheDay Date：当前日期
     * @param rs ResultSet：包含债券信息的结果集
     * @param hmReturn HashMap：用于返回结果值的哈希表
     * @throws YssException
     */
    public void getNextStartDateAndEndDate(java.util.Date dTheDay, ResultSet rs,
                                           HashMap hmReturn) throws YssException {
        try {
            getNextStartDateAndEndDate(dTheDay,
                                       rs.getDate("FInsStartDate"),
                                       rs.getDate("FInsEndDate"),
                                       rs.getDouble("FInsFrequency"),
                                       hmReturn,
                                       "");
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
    
    public void getNextStartDateAndEndDate(java.util.Date dTheDay,
        java.util.Date dBigInsStartDate,
        java.util.Date dBigInsEndDate,
        double iFrequency,
        HashMap hmReturn)throws YssException {
		try {
			getNextStartDateAndEndDate(dTheDay,
					dBigInsStartDate,
					dBigInsEndDate,
					iFrequency,
			        hmReturn,
			        "");
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		}
	}
	

    /**
     * 根据日期取比率公式里的比率值
     * @param dDate Date
     * @return double
     * @throws YssException
     */
    private double getPerFormulaRate() throws YssException {
        double dRate = 0;
        String sqlStr = "";
        ResultSet rs = null;
        /**
         * add by Liyu 20080728
         * bug:ZB0000332(question 2)
         */
        try {
            sqlStr = " select * from " +
                pub.yssGetTableName("tb_para_performula_rela") + "  rela " +
                " join (select max(FRangeDate) as FRangeDate,y.FFormulaCode from " +
                pub.yssGetTableName("tb_para_performula_rela") + "  y " +
                " join (select * from " +
                pub.yssGetTableName("tb_para_performula") +
                " a where FFormulaCode = " +
                " (select FPerExpCode from " +
                pub.yssGetTableName("tb_para_fixinterest") +
                "  where FSecurityCode='" + bondIns.getSecurityCode() +
                "') and FCheckstate=1) x " +
                " on y.FFormulaCode = x.FFormulaCode " +
                " where y.FRangeDate <= " + dbl.sqlDate(bondIns.getInsDate()) +
                " and y.FCheckState=1 group by y.FFormulaCode ) re " +
                " on rela.FFormulaCode= re.FFormulaCode and rela.FRangeDate=re.FRangeDate ";
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                //取小于日期的最大日期
                dRate = rs.getDouble("FPerValue");
            } else {
                //取大于日期的最小日期
                sqlStr = " select * from " +
                    pub.yssGetTableName("tb_para_performula_rela") + "  rela " +
                    " join (select min(FRangeDate) as FRangeDate,y.FFormulaCode from " +
                    pub.yssGetTableName("tb_para_performula_rela") + " y " +
                    " join (select * from " +
                    pub.yssGetTableName("tb_para_performula") +
                    " a where FFormulaCode = " +
                    " (select FPerExpCode from " +
                    pub.yssGetTableName("tb_para_fixinterest") +
                    " where FSecurityCode='" + bondIns.getSecurityCode() +
                    "') and FCheckState =1) x " +
                    " on y.FFormulaCode = x.FFormulaCode " +
                    " where y.FRangeDate >=" + dbl.sqlDate(bondIns.getInsDate()) +
                    " and y.FcheckState=1 group by y.FFormulaCode ) re " +
                    " on rela.FFormulaCode= re.FFormulaCode and rela.FRangeDate=re.FRangeDate ";
                rs = dbl.openResultSet(sqlStr);
                if (rs.next()) {
                    dRate = rs.getDouble("FPerValue");
                } else {
                    dRate = 0; //若无只能传默认值 0
                }
            }
            return dRate;
        } catch (Exception ex) {
            throw new YssException("根据日期取比率值出错!\n", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取计息年天数.
     * @throws YssException
     * @return int
     */
    private double getInsDays() throws YssException {
      double days = 0;
      double lDays = 1;//当年天数 xuqiji 20100309 MS01012  QDV4赢时胜（中保）2010年03月08日01_B   债券计息的时候提示除数为零的错误   
      PeriodBean period = null;
      period = new PeriodBean();
      period.setYssPub(pub);
      
      //---add by songjie 2012.02.24 BUG 3925 QDV4赢时胜(上海开发部)2012年2月24日01_B start---//
      //如果没有设置计息天数 则提示用户设置
      if(this.periodCode.trim().equals("")){
    	  throw new YssException("请设置【" + this.securityCode + "】债券的计息天数！");
      }
      //---add by songjie 2012.02.24 BUG 3925 QDV4赢时胜(上海开发部)2012年2月24日01_B end---//
      
      period.setPeriodCode(this.periodCode);
      //add by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
      period = security.getFixInterest().getPeriod();
      //delete by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
      //period.getSetting();
      //-------------xuqiji 20100309 MS01012  QDV4赢时胜（中保）2010年03月08日01_B   债券计息的时候提示除数为零的错误 ---//
      //edit by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
      if(period.getPeriodType() == 1){//期间类型：实际天数--1，固定天数--0.
    	  if(YssFun.isLeapYear(this.tradeDate)){//闰年
    		  lDays = 366;
    	  }else{//平年
    		  lDays = 365;
    	  }
    	  days = lDays;
      }else{
    	  days = period.getDayOfYear();
      }
      //-------------------------end------------------------//
      return days;
   }

    /**
     * 此方法为了获取综合业务的证券应收应付中证券利息,此sql语句添加了投资经理和券商代码作为条件
     * @param dDate Date 业务日期
     * @return double 返回得到的FMoney*FInout
     * @throws YssException 异常
     * add by xuqiji:QDV4赢时胜（上海）2009年4月30日02_B  20090507 MS00429
     * 通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误
     */
    private double getIntegrateFix(java.util.Date dDate) throws YssException {
        double integrateFix = 0.0;
        int inout = 0;
        ResultSet rs = null;
        StringBuffer buff = null;
        boolean analy1;
        boolean analy2;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            buff = new StringBuffer();
            buff.append(" select FMoney,FInout from ");
            buff.append(pub.yssGetTableName("tb_data_secrecpay "));
            buff.append(" a where exists ( select 'X' from ");
            buff.append(pub.yssGetTableName("tb_data_integrated"));
            buff.append(" b where a.FNum = b.FRelaNum and FNumType = 'SecRecPay' and FCheckState = 1 ) ");
            buff.append(" and FCheckState = 1 and FTsfTypeCode = '06' and FSubTsfTypeCode = '06FI' ");
            buff.append(" and FTransDate =  ");
            buff.append(dbl.sqlDate(dDate));
            buff.append(" and FSecurityCode =  ");
            buff.append(dbl.sqlString(this.bondIns.getSecurityCode()));
            buff.append(" AND FAttrClsCode = ").append(dbl.sqlString(bondIns.getAttrClsCode()));
            buff.append(analy1 ? " and FAnalysisCode1 = " + dbl.sqlString(bondIns.getAnalysisCode1()) : "");
            buff.append(analy2 ? " and FAnalysisCode2 = " + dbl.sqlString(bondIns.getAnalysisCode2()) : "");
            rs = dbl.openResultSet(buff.toString());
            if (rs.next()) {
                inout = rs.getInt("FInout");
                integrateFix = rs.getDouble("FMoney");
                integrateFix = YssD.mul(integrateFix, inout); //此处为了按照是流入或流出证券利息算法的新标记得到的Money值与流入或流出的乘积
            }
        } catch (Exception e) {
            throw new YssException("获取综合业务的证券应收应付利息出错！\r\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return integrateFix;
    }

    //-------------------------------------------------end----------------------------------------------------------------//
    /**
     * 查询综合业务中数据-证券流出数据
     * @param factSettleDate Date 业务资料证券的结算日期
     * @param dDate 操作日期
     * @param analy1 boolean 分析代码1
     * @param analy2 boolean 分析代码2
     * @return double 返回证券数据流出数量
     * @throws YssException 异常
     * add by xuqiji 20090608 :QDV4中保2009年06月05日01_B  MS00489 债券转换后计息时计算出两条一正一负的数据
     */
    private double getIntegratedData(HashMap factSettleDate,
                                     java.util.Date dDate, boolean analy1,
                                     boolean analy2, boolean DayFi) throws
        YssException {
        StringBuffer buff = null;
        ResultSet rs = null;
        double result = 0;
        try {
            buff = new StringBuffer();
            buff.append(" select sum(FAmount) as FAmount from (select * from ").
                append(pub.yssGetTableName("Tb_Data_Integrated"));
            buff.append(" where FCheckState = 1) a where FSecurityCode = ").append(
                dbl.sqlString(bondIns.getSecurityCode()));
            buff.append(" and FPortCode =").append(dbl.sqlString(bondIns.
                getPortCode()));
            buff.append( (analy1 ?
                          " and FAnalysisCode1 = " +
                          dbl.sqlString(bondIns.getAnalysisCode1()) :
                          ""));
            buff.append( (analy2 ?
                          " and FAnalysisCode2 = " +
                          dbl.sqlString(bondIns.getAnalysisCode2()) :
                          ""));
            //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
            buff.append(" and FOperDate >=").append(dbl.sqlDate(factSettleDate.get("FBargainDate").toString()));
            buff.append(" and FOperDate <=").append(dbl.sqlDate(dDate));
            if (DayFi) {
                buff.append(" and ").append(dbl.sqlDate(dDate)).append(" <").append(
                    dbl.sqlDate(factSettleDate.get("FSettleDate").toString()));
            } else {
                buff.append(" and ").append(dbl.sqlDate(dDate)).append(" <=").
                    append(dbl.sqlDate(factSettleDate.get("FSettleDate").
                                       toString()));
            }
            //----------------------------------end----------------------------------------//
            buff.append(" and FTradeTypeCode='80'");

            rs = dbl.openResultSet(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                result = rs.getDouble("FAmount");
            }
        } catch (Exception e) {
            throw new YssException("查询综合业务中数据出错！\r\t", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
    }

   /** 合并太平版本代码
    * 获取多天的综合业务数据
    * @param factSettleDate HashMap
    * @param dDate Date
    * @param analy1 boolean
    * @param analy2 boolean
    * @param DayFi boolean
    * @return double
    * @throws YssException
	* MS00693 QDV4中保2009年09月10日01_B sj
    */
   private double getAllIntegratedData(HashMap factSettleDate,
                                       java.util.Date dDate, boolean analy1,
                                       boolean analy2, boolean DayFi) throws
         YssException {
      StringBuffer buff = null;
      ResultSet rs = null;
      double result = 0;
      double allResult = 0;
      HashMap map = null;
      Iterator it = null;
      try {
         it = factSettleDate.keySet().iterator();
         while(it.hasNext()){

            map = (HashMap)factSettleDate.get((String)it.next());
            buff = new StringBuffer();
            buff.append(" select sum(FAmount) as FAmount from (select * from ").
                  append(pub.yssGetTableName("Tb_Data_Integrated"));
            buff.append(" where FCheckState = 1) a where FSecurityCode = ").
                  append(
                        dbl.sqlString(bondIns.getSecurityCode()));
            buff.append(" and FPortCode =").append(dbl.sqlString(bondIns.
                  getPortCode()));
            buff.append( (analy1 ?
                          " and FAnalysisCode1 = " +
                          dbl.sqlString(bondIns.getAnalysisCode1()) :
                          ""));
            buff.append( (analy2 ?
                          " and FAnalysisCode2 = " +
                          dbl.sqlString((String)map.get("FBrokerCode")) :
                          ""));
           
            buff.append(" and FOperDate >=").append(dbl.sqlDate(map.
                  get("FBargainDate").toString()));
            buff.append(" and FOperDate <=").append(dbl.sqlDate(dDate));
            if (DayFi) {
               buff.append(" and ").append(dbl.sqlDate(dDate)).append(" <").
                     append(
                           dbl.sqlDate(map.get("FSettleDate").
                                       toString()));
            }
            else {
               buff.append(" and ").append(dbl.sqlDate(dDate)).append(" <=").
                     append(dbl.sqlDate(map.get("FSettleDate").
                                        toString()));
            }
           
            buff.append(" and FTradeTypeCode='80'");

            rs = dbl.openResultSet(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
               result = rs.getDouble("FAmount");
            }
            allResult += result;
         }
      }
      catch (Exception e) {
         throw new YssException("查询综合业务中数据出错！\r\t", e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }
      return allResult;
   }


   /**
     * 查询业务资料表中数据
     * @param dDate Date 操作日期
     * @param analy1 boolean 分析代码1
     * @return Date 返回业务资料中的证券结算日期
     * @throws YssException 异常
     * add by xuqiji 20090608 :QDV4中保2009年06月05日01_B  MS00489 债券转换后计息时计算出两条一正一负的数据
     */
    private HashMap getFactSettleDate(java.util.Date dDate,
                                      boolean analy1) throws YssException { //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
        StringBuffer buff = null;
        ResultSet rs = null;
        //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
        java.util.Date factSettleDate = null;
        java.util.Date fBargainDate = null;
        HashMap map = null;
      	HashMap eachSecurityAndBrokerMap = null;//MS00693 QDV4中保2009年09月10日01_B sj
        //----------------------------------end-------------------------//
        try {
//         map = new HashMap();//MS00693 QDV4中保2009年09月10日01_B sj 调整位置
         	eachSecurityAndBrokerMap = new HashMap();//MS00693 QDV4中保2009年09月10日01_B sj
            buff = new StringBuffer();
            buff.append(
                " select max(FSettleDate) as FSettleDate,max(FBargainDate) as FBargainDate,FBrokerCode from (select * from "); //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
            buff.append(pub.yssGetTableName("Tb_Data_SubTrade"));
            buff.append(" where FCheckState = 1) a  where FSecurityCode =");
            buff.append(dbl.sqlString(bondIns.getSecurityCode()));
            buff.append(" and FPortCode =").append(dbl.sqlString(bondIns.
                getPortCode()));
            buff.append( (analy1 ?
                          " and FInvMgrCode = " +
                          dbl.sqlString(bondIns.getAnalysisCode1()) :
                          ""));
            //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
            buff.append(" and FBargainDate <=").append(dbl.sqlDate(dDate));
            buff.append(" and ").append(dbl.sqlDate(dDate)).append(
                " <= FSettleDate").append(" group by FBrokerCode");//添加券商代码，合并太平版本时调整 by leeyu 20100818
            //--------------------------end------------------------------------//
            rs = dbl.openResultSet(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
            	map = new HashMap();//MS00693 QDV4中保2009年09月10日01_B sj 调整位置
                factSettleDate = rs.getDate("FSettleDate");
                fBargainDate = rs.getDate("FBargainDate"); //edit by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
            	//}//MS00693 QDV4中保2009年09月10日01_B sj 调整位置 合并太平版本代码
            	//add by xuqiji 20090610:QDV4赢时胜（上海）2009年6月10日01_B  MS00494 债券转换券商后进行收益计提结果计算错误
            	map.put("FSettleDate", factSettleDate);
            	map.put("FBargainDate", fBargainDate);
            	map.put("FBrokerCode",rs.getString("FBrokerCode"));//MS00693 QDV4中保2009年09月10日01_B sj 调整位置
            	//----------------------------------------------end------------------------------------//
            	eachSecurityAndBrokerMap.put(rs.getString("FBrokerCode") + "\t" + YssFun.formatDate(rs.getDate("FBargainDate")),map);//MS00693 QDV4中保2009年09月10日01_B sj 放置不同的券商
         	}//MS00693 QDV4中保2009年09月10日01_B sj 调整位置
        } catch (Exception e) {
            throw new YssException(" 查询业务资料表中数据出错！\r\t", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
      return eachSecurityAndBrokerMap;//MS00693 QDV4中保2009年09月10日01_B sj
    }

    /**
     * 获取每百元债券利息
     * @param dInsDate Date
     * @return BigDecimal
     * @throws YssException
     */
    private BigDecimal getIntAccPer100(java.util.Date dInsDate) throws YssException{
        ResultSet rs = null;
        String strSql = "";
        BigDecimal bigPer100;
        try {
            strSql = "SELECT FIntAccPer100 FROM " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " WHERE FCheckState = 1 AND FRecordDate = " + dbl.sqlDate(dInsDate) +
                " AND FSecurityCode = " + dbl.sqlString(security.getStrSecurityCode());
            rs = dbl.openResultSet(strSql);
            if (rs.next()){
                bigPer100 = rs.getBigDecimal("FIntAccPer100");
            } else {
            	//2009-1-14 蒋锦 修改 直接使用 dateDiff 会少计算一天计息日期，所以最后要加一天
                //QDV4赢时胜（上海）2010年1月12日02_B
                //每百元债券利息 = （每百元票面利率÷365）×（票面金额÷100）×已记提天数
                bigPer100 = YssD.mulD(YssD.divD(new BigDecimal(security.getFixInterest().getStrFaceRate() + ""), new BigDecimal("365")),
                                    YssD.mulD(YssD.divD(new BigDecimal(security.getFixInterest().getStrFaceValue() + ""), new BigDecimal("100")),
                                             new BigDecimal(YssFun.dateDiff(security.getFixInterest().getDtThisInsStartDate(), dInsDate) + 1 + "")));
                if(security.getStrExchangeCode().equalsIgnoreCase(YssOperCons.YSS_JYSDM_YHJ)){
                    bigPer100 = YssD.roundD(bigPer100, 12);
                } else {
                    CNInterfaceParamAdmin param = new CNInterfaceParamAdmin();
                    param.setYssPub(pub);
                    HashMap hmRoundScale = (HashMap) param.getReadTypeBean();
                    ReadTypeBean readType = (ReadTypeBean)hmRoundScale.get(pub.getPrefixTB() + " " + bondIns.getPortCode());
                   if(readType != null){
                       bigPer100 = YssD.roundD(bigPer100, readType.getExchangePreci());
                   } else {
                       //交易所默认保留8位
                       bigPer100 = YssD.roundD(bigPer100, 8);
                   }
                }
            }
        } catch (Exception ex) {
            throw new YssException("获取每百元债券利息出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
        return bigPer100;
    }

    /**
     * 2009.07.16 蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * @param dbAmount double:计息数量
     * @param dTradeDate Date：计息日期（包含）
     * @return Double：利息
     * @throws YssException
     */
    private double getDomesticStdIns(double dbAmount,
                                     java.util.Date dTradeDate)
        throws YssException{
        ResultSet rs = null;
        BigDecimal bigPer100;
        double dbBondIns = 0;
        try {
            bigPer100 = getIntAccPer100(dTradeDate);
            dbBondIns = YssD.round(YssD.mulD(new BigDecimal(dbAmount + ""), bigPer100), 2);
        } catch (Exception ex) {
            throw new YssException("使用国内标准利息算法出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
        return dbBondIns;
    }

    /**
     * 2009.07.16 蒋锦 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 从应收应付数据表中查出所有债券交易产生的交易利息
     * @param dTransDate Date
     * @return double
     * @throws YssException
     */
    private double getFIInsAllTrade(java.util.Date dTransDate) throws YssException{
        ResultSet rs = null;
        String strSql = "";
        double dbResult = 0;
        boolean analy1;
        boolean analy2;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");

            strSql = "SELECT FMoney, FSubTsfTypeCode, FInOut" +
                "  FROM " + pub.yssGetTableName("Tb_Data_Secrecpay") +
                " WHERE FTransDate = " + dbl.sqlDate(dTransDate) +
                " AND FCheckState = 1" +
                " AND FSubTsfTypeCode IN ('06FI_B', '02FI_B')" +
                " AND FSecurityCode = " + dbl.sqlString(bondIns.getSecurityCode()) +
                " AND FPortCode = " + dbl.sqlString(bondIns.getPortCode()) +
                " AND FAttrClsCode = " + dbl.sqlString(bondIns.getAttrClsCode());
            if(analy1){
                strSql = strSql + " AND FAnalysisCode1 = " + dbl.sqlString(bondIns.getAnalysisCode1());
            }
            if(analy2){
                strSql = strSql + " AND FAnalysisCode2 = " + dbl.sqlString(bondIns.getAnalysisCode2());
            }
            rs = dbl.openResultSet(strSql);
            while(rs.next()){
                if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("06FI_B")){
                    dbResult += YssD.mul(rs.getDouble("FMoney"), rs.getDouble("FInOut"));
                } else {
                    dbResult += YssD.mul(rs.getDouble("FMoney"), rs.getDouble("FInOut"), -1);
                }
            }
        } catch (Exception ex) {
            throw new YssException("获取债券计息日所有交易利息出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
        return dbResult;
    }
    
    /**
     * QDII国内 ：MS00847
     * QDV4赢时胜（北京）2009年11月30日03_B
     * add by songjie
     * 根据参数来判断取债券信息设置表中的税前票面利率 或 税后票面利率
     * 2009-12-15
     */
    private double getDomesticFaceRate(boolean isBefore) throws YssException{
    	double dbResult = 0;
    	ResultSet rs = null;
        String strSql = "";
        String strFactRate = "";
    	try{	
    		//isBefore == true 则strFactRate = "FBeforeFaceRate" 否则 = "FFaceRate"
    		strFactRate = isBefore?"FBeforeFaceRate":"FFaceRate";
    		
    		//在债券信息设置表中查询已审核的相关证券的税前 或 税后票面利率
    		strSql = " select " + strFactRate + " from " + 
    		pub.yssGetTableName("Tb_Para_FixInterest") + 
    		" where FSecurityCode = " + dbl.sqlString(bondIns.getSecurityCode()) + 
    		" and FCheckState = 1 ";

    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			dbResult = rs.getDouble(strFactRate);
    		}
    		
    		return dbResult;
    	}catch (Exception ex) {
            throw new YssException("根据参数获取债券信息设置中的税前票面利率或税后票面利率出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * add by songjie
     * 2010.03.27
     * @param isRate100
     * @return
     * @throws YssException
     */
    private boolean getIsRate100(boolean isRate100) throws YssException{
    	try{
    		return isRate100;
    	}catch(Exception e){
    		throw new YssException("判断获取百元债券利息公式 或 债券利息公式出错！", e);
    	}
    }
    
    /**
     * QDII国内 ：MS00847
     * QDV4赢时胜（北京）2009年11月30日03_B
     * add by songjie
     * 根据参数获取国内债券的计息天数
     * 2009-12-18
     * @param s
     * @return
     * @throws YssException
     */
    private int getDomesticDays(boolean include29) throws YssException{
    	java.util.Date startDate = null;//开始计息日期
    	java.util.Date dInsDate = null;//估值日期
    	int startNum = 0;//用于判断闰年的开始年份
    	int endNum = 0;//用于判断闰年的截止年份
    	int subDays = 0;//若包含2月29号  则累加开始计息日期和计息截至日之间的2月29号的天数
    	try{
    		dInsDate = bondIns.getInsDate();
    		startDate = security.getFixInterest().getDtThisInsStartDate();
    		
    		//若开始计息日期小于2月 或 等于2月 但是小于等于2月28号的话 判断的开始年份就为当年  否则 为下一年
    		if(Integer.parseInt(YssFun.formatDate(startDate, "yyyyMMdd").substring(4, 6)) < 2 || 
    				(Integer.parseInt(YssFun.formatDate(startDate, "yyyyMMdd").substring(4, 6)) == 2 
    						&& Integer.parseInt(YssFun.formatDate(startDate, "yyyyMMdd").substring(6, 8)) <= 28)){
    			startNum = Integer.parseInt(YssFun.formatDate(startDate, "yyyyMMdd").substring(0, 4));
    		}
    		else{
    			startNum = Integer.parseInt(YssFun.formatDate(startDate, "yyyyMMdd").substring(0, 4)) + 1;
    		}
    		
    		//若计息截止日大于2月的话 那么判断的截至年份就为当年  否则 为 上一年
    		endNum = (Integer.parseInt(YssFun.formatDate(dInsDate, "yyyyMMdd").substring(4, 6)) > 2)?
    				Integer.parseInt(YssFun.formatDate(dInsDate, "yyyyMMdd").substring(0, 4)):
    					(Integer.parseInt(YssFun.formatDate(dInsDate, "yyyyMMdd").substring(0, 4)) - 1);
    		
    		//若计息天数不包含闰年的2月29号
            if(startNum <= endNum && !include29){
            	for(int i = startNum; i <= endNum; i++){
        			//判断闰年的方法：能被400整除，或者能被4整除而不能被100整除
        			if(i%400 == 0 || (i%4 == 0 && i%100 != 0)){
        				subDays += 1;
        			}
        		}
            }
    		
            //计息天数 = 估值日 - 计息开始日 - （若不包含闰年的2月29则  - 计息开始日到估值日之间的2月29日的天数） 
    		return YssFun.dateDiff(startDate, dInsDate) - subDays;
    	}
    	catch(Exception e){
    		throw new YssException("根据参数获取国内债券的计息天数出错！", e);
    	}
    }
    
    /**
     * QDII国内 ：MS00847
     * QDV4赢时胜（北京）2009年11月30日03_B
     * add by songjie
     * 获取国内债券的起息日至到期兑付日的实际天数
     * 2009-12-18
     * @param s
     * @return
     * @throws YssException
     */
    private int getDomesticCashDays() throws YssException{
    	try{
    		//起息日至到期兑付日的实际天数 = 到期兑付日 - 起息日
    		return YssFun.dateDiff(security.getFixInterest().getDtInsStartDate(), security.getFixInterest().getDtInsCashDate());
    	}
    	catch(Exception e){
    		throw new YssException("根据参数获取国内债券的计息天数出错！", e);
    	}
    }
    
    /**
     * 获取国内债券的付息周期天数
     * QDII国内 ：MS00847
     * QDV4赢时胜（北京）2009年11月30日03_B
     * add by songjie
     * 2009-12-18
     * @return
     * @throws YssException
     */
    private int getDomesticPeriodDays() throws YssException{
    	try{
    		//付息周期天数 = 计息截至日 - 计息开始日
    		return YssFun.dateDiff(security.getFixInterest().getDtThisInsStartDate(), security.getFixInterest().getDtThisInsEndDate());
    	}
    	catch(Exception e){
    		throw new YssException("获取国内债券的付息周期天数出错！", e);
    	}
    }
    
    /**
     * 获取债券起息日至估值日的整年数
     * QDII国内 ：MS00847
     * QDV4赢时胜（北京）2009年11月30日03_B
     * add by songjie
     * 2009-12-18
     * @param dInsDate
     * @return
     * @throws YssException
     */
    private int getDomesticYears() throws YssException{
    	try{
    		//债券起息日至估值日的整年数 = 估值日对应的年份 - 开始计息日对应的年份 + 1
    		return bondIns.getInsDate().getYear() - security.getFixInterest().getDtThisInsStartDate().getYear() + 1;
    	}
    	catch(Exception e){
    		throw new YssException("获取国内债券的付息周期天数出错！", e);
    	}
    }
    
    /**
     * 获取开始日期到结束日期之间2月29日存在的天数，头尾均记,开始日期必须小于结束日期
     * 2010-03-29 蒋锦 添加 国内 MS00955
     * @param startDate：开始日期
     * @param endDate：结束日期
     * @return 2月29日 存在的天数。就是在startDate到endDate之间存在几个2月29日
     * @throws YssException
     */
    private int checkRN(java.util.Date startDate, java.util.Date endDate) throws YssException{
    	int iDays = 0;
    	try{
    		if(endDate.before(startDate)){
    			return 0;
    		}
    		iDays = YssFun.getLeapYears(startDate, endDate);
    	}catch(Exception ex){
    		throw new YssException("判断期间段2月29日的天数出错！", ex);
    	}
    	return iDays;
    }
	
	/** 合并太平版本代码
    * 通过获取债券计息表来获取计息起始日与计息截止日
    * QDV4中保2010年03月03日01_A MS01009 by leeyu
    * @return
    * @throws YssException
    */
   private HashMap getBondThisStartAndEndDate() throws YssException{
	   HashMap hmBondDate=new HashMap();
	   ResultSet rs =null;
	   String sqlStr="";
	   try{
		   sqlStr="select distinct FCurCpnDate,FNextCpnDate from "+pub.yssGetTableName("Tb_Para_BondParamater")+
		   " where FSecurityCode ="+dbl.sqlString(securityCode)+" and "+
		   dbl.sqlDate(tradeDate)+" between FCurCpnDate and FNextCpnDate";
		   rs =dbl.openResultSet(sqlStr);
		   if(rs.next()){
			   hmBondDate.put("ThisInsStartDate",YssFun.formatDate(rs.getDate("FCurCpnDate"),"yyyy-MM-dd"));//本计息期间计息起始日
			   hmBondDate.put("ThisInsEndDate",YssFun.formatDate(rs.getDate("FNextCpnDate"),"yyyy-MM-dd")); //本计息期间计息截止日
		   }
	   }catch(Exception ex){
		   throw new YssException(ex.getMessage());
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
	   return hmBondDate;
   }
   /** 合并太平版本代码
    * 判断T日是否有买入利息  by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
    * @return 买过-1 没有买过-0
    * @throws YssException
    */
   private int checkBuySecurity() throws YssException{
	   int iCheck=0;
	   ResultSet rs =null;
	   String sql ="";
       boolean analy1;
       boolean analy2;
       boolean analy3;
	   try{
			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
			analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
           
		   //通过证券应收应付中的买入利息发生额与当日应收应付库存余额进行比较
		   sql="select '1' from (select FStorageDate,FSecurityCode,FPortCode,FAnalySisCode1,FAnalySisCode2,FAnalySisCode3,FTsfTypeCode,FSubTsfTypeCode,"+
		   "FCatType,FAttrClsCode,FCuryCode,FBal,FInvestType from "+
		   pub.yssGetTableName("Tb_Stock_Secrecpay")+
		   " where FStorageDate="+dbl.sqlDate(tradeDate)+
		   " and FSecurityCode="+dbl.sqlString(bondIns.getSecurityCode())+
		   " and FPortCode="+dbl.sqlString(bondIns.getPortCode())+
		   " and FAnalySisCode1="+ dbl.sqlString(bondIns.getAnalysisCode1().length()==0?" ":bondIns.getAnalysisCode1())+
		   " and FAnalySisCode2="+ dbl.sqlString(bondIns.getAnalysisCode2().length()==0?" ":bondIns.getAnalysisCode2())+
		   " and FAnalySisCode3="+ dbl.sqlString(bondIns.getAnalysisCode3().length()==0?" ":bondIns.getAnalysisCode3())+
		   " and FAttrClsCode="+dbl.sqlString(bondIns.getAttrClsCode())+
		   " and FYearMonth<>"+dbl.sqlString(YssFun.formatDate(tradeDate,"yyyy")+"00")+
		   ") a join (select FTransDate,FSecurityCode,FPortCode,FAnalySisCode1,FAnalySisCode2,FAnalySisCode3,"+
		   " FTsfTypeCode,case when FSubTsfTypeCode='06FI_B' then '06FI' else FSubTsfTypeCode end as FSubTsfTypeCode,"+
		   " FCatType,FAttrClsCode,FCuryCode,sum(FMoney*FInOut) as FMoney,FInvestType from "+
		   pub.yssGetTableName("Tb_Data_Secrecpay")+
		   " where FSecurityCode="+dbl.sqlString(bondIns.getSecurityCode())+
		   " and FPortCode="+dbl.sqlString(bondIns.getPortCode())+
		   " and FAttrClsCode="+dbl.sqlString(bondIns.getAttrClsCode())+
		   " and FTransDate="+dbl.sqlDate(tradeDate)+" and FTsfTypeCode='06' and FSubTsfTypeCode ='06FI_B' and FCheckState=1 "+//取买入利息
		   //--------QDV4太平2010年10月28日01_B panjunfang modify 20101101 当日应收应付库存也需要加上分析代码作为条件，与证券应收应付保持一致-------
		   //增加是否配置库存配置信息的判断  modify 20110225
		   (checkBuySecurityCodition(tradeDate).length() > 0 ? checkBuySecurityCodition(tradeDate) : 
		   (analy1 ? " and FAnalySisCode1="+ dbl.sqlString(bondIns.getAnalysisCode1().length()==0?" ":bondIns.getAnalysisCode1()) : "")+
		   (analy2 ? " and FAnalySisCode2="+ dbl.sqlString(bondIns.getAnalysisCode2().length()==0?" ":bondIns.getAnalysisCode2()) : "")+
		   (analy3 ? " and FAnalySisCode3="+ dbl.sqlString(bondIns.getAnalysisCode3().length()==0?" ":bondIns.getAnalysisCode3()) : "")) +
		   //-----------------end ---------------------------------------
		   " group by FTransDate,FSecurityCode,FPortCode,FAnalySisCode1,FAnalySisCode2,FAnalySisCode3,FTsfTypeCode,FSubTsfTypeCode,FCatType,FAttrClsCode,FCuryCode,FInvestType) b on "+
		   " a.FStorageDate=b.FTransDate and a.FSecurityCode=b.FSecurityCode and a.FPortCode=b.FPortCode and a.FTsfTypeCode =b.FTsfTypeCode and a.FInvestType=b.FInvestType"+
		   " and a.FSubTsfTypeCode=b.FSubTsfTypeCode and a.FCatType=b.FCatType and a.FAttrClsCode=b.FAttrClsCode and a.FCuryCode=b.FCuryCode where a.FBal<>0";
		   rs =dbl.openResultSet(sql);
		   if(rs.next()){			   
				iCheck=1;			   
		   }else{
			   iCheck=0;
		   }
	   }catch(Exception ex){
		   throw new YssException("判断当日买入证券利息出错",ex);
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
	   return iCheck;
   }
   
   /**
    * 获取当日的历史库存 历史库存利息＝昨日库存利息余额+当日买入利息+转货流入利息
    * by leeyu 20100423
    * @param dDate
    * @param sDisDay
    * @param sType
    * @return
    * @throws YssException
    */
	public double getThisFIInsBal(java.util.Date dDate, String sDisDay, String sType)
			throws YssException {
		int iDisDay = 0;
		double dBal=0.0D;
		ResultSet rs = null;
		String sqlStr="";
		try{
		    //--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(BondInsCfgFormulaN.hmKey != null && BondInsCfgFormulaN.hmKey.get("thisFIInsBal") != null){
            	HashMap hmsecs = (HashMap)BondInsCfgFormulaN.hmKey.get("thisFIInsBal");
            	String analysis1 = bondIns.getAnalysisCode1();
            	String analysis2 = bondIns.getAnalysisCode2();
            	if(analysis1.trim().length() == 0){
            		analysis1 = " ";
            	}
            	if(analysis2.trim().length() == 0){
            		analysis2 = " ";
            	}
            	String key = bondIns.getSecurityCode() + "\t" + 
            	bondIns.getPortCode() + "\t" +
            	bondIns.getAttrClsCode() + "\t" + 
            	bondIns.getInvestType() + "\t" +
            	analysis1 + "\t" + analysis2 + "\t" +
            	YssFun.formatDate(dDate,"yyyyMMdd");
            	if(hmsecs.get(key) != null){
            		return ((Double)hmsecs.get(key)).doubleValue();
            	}else{
            		return 0;
            	}
            }    
			//--- add by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            
			YssGlobal.hmSecRecBeans.clear();
			if (sDisDay.startsWith("!")) {
				iDisDay = YssFun.toInt(sDisDay.substring(1)) * -1;
			} else {
				iDisDay = YssFun.toInt(sDisDay);
			}
			if (sType.equalsIgnoreCase("work")) {
				dDate = this.getSettingOper().getWorkDay(bondIns.getHolidaysCode(),
						dDate, iDisDay);
			} else if (sType.equalsIgnoreCase("natural")) {
				dDate = YssFun.addDay(dDate, iDisDay);
			}
			sqlStr="select FSecurityCode,FBal from "+pub.yssGetTableName("Tb_Stock_SecRecPay") +
				" where 1 = 1 "+
				" and FSecurityCode="+dbl.sqlString(bondIns.getSecurityCode())+
				" and FPortCode="+dbl.sqlString(bondIns.getPortCode())+
				" and "+operSql.sqlStoragEve(dDate)+
				" and FAnalysisCode1="+dbl.sqlString(bondIns.getAnalysisCode1())+
				" and FAnalysisCode2="+dbl.sqlString(bondIns.getAnalysisCode2())+
				" and FTsfTypeCode ='06' and FSubTsfTypeCode ='06FI'"+
				" AND FAttrClsCode = "+dbl.sqlString(bondIns.getAttrClsCode());
			rs =dbl.openResultSet(sqlStr);
			if(rs.next()){
				dBal =rs.getDouble("FBal");
			}
			dbl.closeResultSetFinal(rs);
			sqlStr="select sum(FMoney) as FMoney from("+
			//综合业务中转货的流入
			" select FMoney*FInOut as FMoney,'A' as FType from "+pub.yssGetTableName("Tb_Data_SecRecPay")+
			" where FNum in( select distinct FRelaNum from "+pub.yssGetTableName("Tb_Data_Integrated")+
			" where FNum in( select distinct FNum from "+pub.yssGetTableName("Tb_Data_Integrated")+
			" where FPortCode ="+dbl.sqlString(bondIns.getPortCode())+
			" and FSecurityCode ="+dbl.sqlString(bondIns.getSecurityCode())+
			" and FAnalysisCode1="+dbl.sqlString(bondIns.getAnalysisCode1())+
			" and FAnalysisCode2="+dbl.sqlString(bondIns.getAnalysisCode2())+
			" and FAttrClsCode="+dbl.sqlString(bondIns.getAttrClsCode())+
			"  and FTradeTypeCode in('80','81') and FOperDate = "+dbl.sqlDate(dDate)+
			" ) and FNumType='SecRecPay' ) and FInOut=1 and FCheckState=1 "+
			" union "+
			//买入编号
			" select FMoney*FInOut as FMoney,'B' as FType from "+pub.yssGetTableName("Tb_Data_SecRecPay")+
			" where FPortCode ="+dbl.sqlString(bondIns.getPortCode())+" and FTransDate="+dbl.sqlDate(dDate)+
			" and FSecurityCode ="+dbl.sqlString(bondIns.getSecurityCode())+
			" and FAnalysisCode1="+dbl.sqlString(bondIns.getAnalysisCode1())+
			" and FAnalysisCode2="+dbl.sqlString(bondIns.getAnalysisCode2())+
			" and FAttrClsCode="+dbl.sqlString(bondIns.getAttrClsCode())+
			" and FTsfTypeCode='06' and FSubTsfTypeCode='06FI_B' and FCheckState=1) ";
			rs = dbl.openResultSet(sqlStr);
			if(rs.next()){
				dBal =YssD.add(dBal, rs.getDouble("FMoney"));
			}
		}catch(Exception ex){
			throw new YssException("获取当日的历史库存出错！",ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dBal;
	}
	
	/**
	    * 判断T-1日是否有买入利息  add by wangzuochun 2010.10.13 MS01837    计提债券计息有误    QDV4太平2010年10月11日01_B    
	    * @return 买过-1 没有买过-0
	    * @throws YssException
	    */
	   private int checkPreBuySecurity() throws YssException{
		   int iCheck=0;
		   ResultSet rs =null;
		   String sql ="";
		   try{
			   //通过证券应收应付中的买入利息发生额与当日应收应付库存余额进行比较
			   sql="select '1' from (select FStorageDate,FSecurityCode,FPortCode,FAnalySisCode1,FAnalySisCode2,FAnalySisCode3,FTsfTypeCode,FSubTsfTypeCode,"+
			   "FCatType,FAttrClsCode,FCuryCode,FBal from "+
			   pub.yssGetTableName("Tb_Stock_Secrecpay")+
			   " where FStorageDate="+dbl.sqlDate(YssFun.addDay(tradeDate, -1))+
			   " and FSecurityCode="+dbl.sqlString(bondIns.getSecurityCode())+
			   " and FPortCode="+dbl.sqlString(bondIns.getPortCode())+
			   " and FAnalySisCode1="+ dbl.sqlString(bondIns.getAnalysisCode1().length()==0?" ":bondIns.getAnalysisCode1())+
			   " and FAnalySisCode2="+ dbl.sqlString(bondIns.getAnalysisCode2().length()==0?" ":bondIns.getAnalysisCode2())+
			   " and FAnalySisCode3="+ dbl.sqlString(bondIns.getAnalysisCode3().length()==0?" ":bondIns.getAnalysisCode3())+
			   " and FAttrClsCode="+dbl.sqlString(bondIns.getAttrClsCode())+
			   " and FYearMonth<>"+dbl.sqlString(YssFun.formatDate(YssFun.addDay(tradeDate, -1),"yyyy")+"00")+
			   ") a join (select FTransDate,FSecurityCode,FPortCode,FAnalySisCode1,FAnalySisCode2,FAnalySisCode3,"+
			   " FTsfTypeCode,case when FSubTsfTypeCode='06FI_B' then '06FI' else FSubTsfTypeCode end as FSubTsfTypeCode,"+
			   " FCatType,FAttrClsCode,FCuryCode,sum(FMoney*FInOut) as FMoney from "+
			   pub.yssGetTableName("Tb_Data_Secrecpay")+
			   " where FSecurityCode="+dbl.sqlString(bondIns.getSecurityCode())+
			   " and FPortCode="+dbl.sqlString(bondIns.getPortCode())+
			   " and FAttrClsCode="+dbl.sqlString(bondIns.getAttrClsCode())+
			   " and FTransDate="+dbl.sqlDate(YssFun.addDay(tradeDate, -1))+" and FTsfTypeCode='06' and FSubTsfTypeCode ='06FI_B' and FCheckState=1 "+//取买入利息
			   //--------QDV4太平2010年10月28日01_B panjunfang modify 20101101 当日应收应付库存也需要加上分析代码作为条件，与证券应收应付保持一致-------
			   (checkBuySecurityCodition(YssFun.addDay(tradeDate, -1)).length() > 0 ? checkBuySecurityCodition(YssFun.addDay(tradeDate, -1)) :  
			   " and FAnalySisCode1="+ dbl.sqlString(bondIns.getAnalysisCode1().length()==0?" ":bondIns.getAnalysisCode1())+
			   " and FAnalySisCode2="+ dbl.sqlString(bondIns.getAnalysisCode2().length()==0?" ":bondIns.getAnalysisCode2())+
			   " and FAnalySisCode3="+ dbl.sqlString(bondIns.getAnalysisCode3().length()==0?" ":bondIns.getAnalysisCode3()) ) +
			   //-----------------end ---------------------------------------
			   " group by FTransDate,FSecurityCode,FPortCode,FAnalySisCode1,FAnalySisCode2,FAnalySisCode3,FTsfTypeCode,FSubTsfTypeCode,FCatType,FAttrClsCode,FCuryCode) b on "+
			   " a.FStorageDate=b.FTransDate and a.FSecurityCode=b.FSecurityCode and a.FPortCode=b.FPortCode and a.FTsfTypeCode =b.FTsfTypeCode "+
			   " and a.FSubTsfTypeCode=b.FSubTsfTypeCode and a.FCatType=b.FCatType and a.FAttrClsCode=b.FAttrClsCode and a.FCuryCode=b.FCuryCode where a.FBal<>0";
			   rs =dbl.openResultSet(sql);
			   if(rs.next()){			   
					iCheck=1;			   
			   }else{
				   iCheck=0;
			   }
		   }catch(Exception ex){
			   throw new YssException("判断当日买入证券利息出错",ex);
		   }finally{
			   dbl.closeResultSetFinal(rs);
		   }
		   return iCheck;
	   }
	   
	   /**
	    * 判断T日是否有买入利息时应将分析代码作为条件，同时要考虑转货的情况
	    * QDV4太平2010年10月28日01_B
	    * @param dDate
	    * @return
	    * @throws YssException
	    */
	   private String checkBuySecurityCodition(java.util.Date dDate) throws YssException{
		   ResultSet rs =null;
		   String sql ="";
		   String strReturn = "";
		   try{
			   sql = "select FAnalySisCode1, FAnalySisCode2,FAnalySisCode3 from " + pub.yssGetTableName("tb_data_integrated") + 
			   		" a where exists (select  fnum  from " + pub.yssGetTableName("tb_data_integrated") + 
			   		" where foperdate = " + dbl.sqlDate(dDate) +
			   		" and fportcode = " + dbl.sqlString(bondIns.getPortCode()) + 
			   		" and FSecurityCode = " + dbl.sqlString(bondIns.getSecurityCode()) + 
			   		" and FCheckState = 1 " + 
					" and FAnalySisCode1="+ dbl.sqlString(bondIns.getAnalysisCode1().length()==0?" ":bondIns.getAnalysisCode1())+
					" and FAnalySisCode2="+ dbl.sqlString(bondIns.getAnalysisCode2().length()==0?" ":bondIns.getAnalysisCode2())+
					" and FAnalySisCode3="+ dbl.sqlString(bondIns.getAnalysisCode3().length()==0?" ":bondIns.getAnalysisCode3())+
					" and FAttrClsCode="+dbl.sqlString(bondIns.getAttrClsCode()) + 
					" and FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_Cost) +
					" and FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FI_COST) + 
					" and finouttype = 1 and a.fnum = fnum and a.fsecuritycode = fsecuritycode)";
			   rs =dbl.openResultSet(sql);
			   while(rs.next()){
				   strReturn += " (FAnalySisCode1 = " + dbl.sqlString(rs.getString("FAnalySisCode1")) + 
				   			" and FAnalySisCode2 = " + dbl.sqlString(rs.getString("FAnalySisCode2")) + 
				   			" and FAnalySisCode3 = " + dbl.sqlString(rs.getString("FAnalySisCode3")) + ") or ";
			   }
			   if(strReturn.length() > 0){
				   strReturn = strReturn.substring(0, strReturn.length() - 3);
				   strReturn = " and (" + strReturn + ")";
			   }
		   }catch(Exception ex){
			   throw new YssException("判断当日买入证券利息出错",ex);
		   }finally{
			   dbl.closeResultSetFinal(rs);
		   }
		   return strReturn;
	   }
	   
	   /**
	     * add by yanghaiming 20110217 #582 30E/360计算计息天数算法
	     * @param isnStartDate Date
	     * @param isnDate Date
	     * @return Long
	     * @throws YssException
	     */
	    public Long getEDay(java.util.Date isnStartDate,java.util.Date isnDate) throws YssException {
	    	int eDay = 0;//初始计息天数为0
//	    	if (isnDate.getDate() == 31){//如果计息日为31号改为30号，则计息日减一天
//	    		eDay += -1;
//	    	}//计息起始日不为31号，则初始计息天数为1，若计息起始日为31号则初始计息天数为0，而31号会改为30号，则计息天数又加一，所以此处不对计息起始日的日期做处理
//	    	
	    	if (isnStartDate.getDate() != 31){
	    		isnDate = YssFun.addDay(isnDate, 1);
	    	}else{
	    		isnStartDate = YssFun.addDay(isnStartDate, -1);
	    	}
	    	if (isnDate.getDate() == 31){
	    		isnDate = YssFun.addDay(isnDate, -1);
	    	}
	    	eDay += YssD.add(YssD.mul(360, YssD.sub(isnDate.getYear(), isnStartDate.getYear())), 
					YssD.mul(30, YssD.sub(isnDate.getMonth(), isnStartDate.getMonth())), 
					YssD.sub(isnDate.getDate(), isnStartDate.getDate()));
	        return new Long(eDay);
	    }
	    
	    /**
	     * add by yanghaiming 20110217 #582 30U/360计算计息天数算法
	     * @param isnStartDate Date
	     * @param isnDate Date
	     * @return Long
	     * @throws YssException
	     */
	    public Long getUDay(java.util.Date isnStartDate,java.util.Date isnDate) throws YssException {
	    	int eDay = 0;//初始计息天数为0
//	    	if (isnDate.getDate() == 31 && (isnStartDate.getDate() == 31 || isnStartDate.getDate() == 30)){//如果计息日为31号改为30号，并且计息起始日为30或31号，则计息日减一天
//	    		eDay += -1;
//	    	}//计息起始日不为31号，则初始计息天数为1，若计息起始日为31号则初始计息天数为0，而31号会改为30号，则计息天数又加一，所以此处不对计息起始日的日期做处理
	    	if (isnStartDate.getDate() != 31){
	    		isnDate = YssFun.addDay(isnDate, 1);
	    	}else{
	    		isnStartDate = YssFun.addDay(isnStartDate, -1);
	    	}
	    	if (isnDate.getDate() == 31 && (isnStartDate.getDate() == 31 || isnStartDate.getDate() == 30)){
	    		isnDate = YssFun.addDay(isnDate, -1);
	    	}
	    	eDay += YssD.add(YssD.mul(360, YssD.sub(isnDate.getYear(), isnStartDate.getYear())), 
					YssD.mul(30, YssD.sub(isnDate.getMonth(), isnStartDate.getMonth())), 
					YssD.sub(isnDate.getDate(), isnStartDate.getDate()));
	        return new Long(eDay);
	    }
	    
       
	  
	    public double factorUSA(java.util.Date dStartDate, java.util.Date dEndDate) throws
	        YssException {
	    	return this.factorUSA(dStartDate, dEndDate, true);
	    }

	    /**
	     *  STORY #898 希望能够新增利息算法，处理这种特殊债券
	     *    【美国市场的企业债和机构债】现采用的该利息计算方法  
         *
         *  主要特点在：2月份最后一天的计息上。
         *   1. 2月份： 如果计息起始日和计息截止日都是2月底的情况：
         *      如果是【2.29】计息截止日 ，则当天计提两天息。
         *      如果是【2.28】计息截止日，则当天计提3天息。
         *      
         *   2. 2月份： 如果计息起始日和计息截止日不都是2月底的情况：
         *      则2月底当天就只提一天的利息；
         *      3.1 提2天息( 2.29)
         *      3.1 提3天息(2.28)
	     *  add by jsc 20120801 
	     * @param dStartDate
	     * @param dEndDate
	     * @param isEndDateAdd1  
	     *  true : 取当天到计息起始日这段时间
	     *  false: 取前一天到计息起始日这段时间
	     * @return
	     * @throws YssException
	     */
		public double factorUSA(java.util.Date dStartDate, java.util.Date dEndDate,boolean isToday)
				throws YssException {
	
	        int iEndYear = 0, iStartYear = 0, iStartMonth = 0, iEndMonth = 0,
	        iStartDay = 0, iEndDay = 0;
	        int iStartFriDate = 0, iEndFriDate = 0; //存放两个日期里二月的天数
	        double dDate = 0;
	        
	        try {
	        	//计息起始日年月日，及2月份最后日期
		        iStartYear = YssFun.getYear(dStartDate);
		        iStartMonth = YssFun.getMonth(dStartDate);
		        iStartDay = YssFun.getDay(dStartDate);
		        iStartFriDate = YssFun.endOfMonth(iStartYear, 2);
		        //计息日的年月日，及2月份最后日期
		        iEndYear = YssFun.getYear(dEndDate);
		        iEndMonth = YssFun.getMonth(dEndDate);
		        iEndDay = YssFun.getDay(dEndDate);
		        iEndFriDate = YssFun.endOfMonth(iEndYear, 2);

                //1. 普通月份处理(除去2月份)
		        
		        if (iStartMonth == 2 && iEndMonth == 2) { //当都是月末且都是二月份的最后一天
		            if (isToday && iEndDay == iEndFriDate && iStartDay == iStartFriDate && YssFun.dateDiff(this.security.getFixInterest().getDtThisInsEndDate(), dEndDate)==0) {
		                iEndDay = 30;
		            }
		        } 
		        
		        if (iStartMonth == 2) { //当D1是月末
		            if (iStartDay == iStartFriDate) { //&& iEndDay != iEndFriDate
		                iStartDay = 30;
		            }
		        }
		        
		        //1. 31号就不再计提利息
		       if(iEndDay ==31){
		    	   return 0; 
		       }

		        
		        if(isToday){
			        dDate = (360 * (iEndYear - iStartYear) + 30 * (iEndMonth - iStartMonth) +
			                 (iEndDay - iStartDay)) * 1.0 / 360;
		        }else{
		        	int days =0;
		        	if(iEndMonth ==3 && iEndDay==1){
		        		iEndMonth=2;
		        		days = YssFun.endOfMonth(YssFun.addDay(dEndDate, -1))-iStartDay;
		        	}else{
		        		days = iEndDay - iStartDay-1;
		        	}
		        	
		        	
			        dDate = (360 * (iEndYear - iStartYear) + 30 * (iEndMonth - iStartMonth) +days)
			                  * 1.0 / 360;
		        }


		    } catch (Exception e) {
		        throw new YssException("计算factorUSA出错!\n", e);
		    }
		    return dDate;
		}
	    
	    public double factorISMA(java.util.Date dStartDate, java.util.Date dEndDate)
			throws YssException {
		return this.factorISMA(dStartDate, dEndDate, true);
	    }

	    /**
	     *  STORY #898 希望能够新增利息算法，处理这种特殊债券
	     *    【欧洲债券市场】现采用的利息计算方法  ISMA
	     *     不对2月底做任何处理，也就是说3.1 可能提2~3 天息。
	     *     具体是2天还是3天看当年是否为闰年
	     *  add by jsc 20120801 
	     *  
	     * @param dStartDate
	     * @param dEndDate
	     * @return
	     * @throws YssException
	     */
		public double factorISMA(java.util.Date dStartDate, java.util.Date dEndDate,boolean isToday)
				throws YssException {
	
	        int iEndYear = 0, iStartYear = 0, iStartMonth = 0, iEndMonth = 0,
	        iStartDay = 0, iEndDay = 0;
	        int iStartFriDate = 0, iEndFriDate = 0; //存放两个日期里二月的天数
	        double dDate = 0;
	        
	        try {
	        	//计息起始日年月日，及2月份最后日期
		        iStartYear = YssFun.getYear(dStartDate);
		        iStartMonth = YssFun.getMonth(dStartDate);
		        iStartDay = YssFun.getDay(dStartDate);
		        iStartFriDate = YssFun.endOfMonth(iStartYear, 2);
		        //计息日的年月日，及2月份最后日期
		        iEndYear = YssFun.getYear(dEndDate);
		        iEndMonth = YssFun.getMonth(dEndDate);
		        iEndDay = YssFun.getDay(dEndDate);
		        iEndFriDate = YssFun.endOfMonth(iEndYear, 2);

                
		        //1. 31号就不再计提利息
			       if(iEndDay ==31){
			    	   return 0; 
			       }
		       
		        if(isToday){
			        dDate = (360 * (iEndYear - iStartYear) + 30 * (iEndMonth - iStartMonth) +
			                 (iEndDay - iStartDay)) * 1.0 / 360;
		        }else{
		        	int days =0;
		        	if(iEndMonth ==3 && iEndDay==1){
		        		iEndMonth=2;
		        		days = YssFun.endOfMonth(YssFun.addDay(dEndDate, -1))-iStartDay;
		        	}else{
		        		days = iEndDay - iStartDay-1;
		        	}
		        	
		        	
			        dDate = (360 * (iEndYear - iStartYear) + 30 * (iEndMonth - iStartMonth) +
			        		days) * 1.0 / 360;
		        }


		    } catch (Exception e) {
		        throw new YssException("计算factorISMA出错!\n", e);
		    }
		    return dDate;
		}
		
		  public double factorEPlus(java.util.Date dStartDate, java.util.Date dEndDate)
				throws YssException {
			return this.factorEPlus(dStartDate, dEndDate, true);
		    }

		  /**
		     *  STORY #898 希望能够新增利息算法，处理这种特殊债券
		     *    【德国债券市场】现采用的利息计算方法 30E+/360
		     *    
		     *     1. 1号不计息的情况
		     *     31日计息，1日不计息；
		     *     2. 1号计息的情况是在3月1日
		     *        2～3息，具体看2月底有几天
		     *  add by jsc 20120801 
		     *  
		     * @param dStartDate
		     * @param dEndDate
		     * @return
		     * @throws YssException
		     */
			public double factorEPlus(java.util.Date dStartDate, java.util.Date dEndDate,boolean isToday)
					throws YssException {
		
		        int iEndYear = 0, iStartYear = 0, iStartMonth = 0, iEndMonth = 0,
		        iStartDay = 0, iEndDay = 0;
		        int iStartFriDate = 0, iEndFriDate = 0; //存放两个日期里二月的天数
		        double dDate = 0;
		        
		        try {
		        	//计息起始日年月日，及2月份最后日期
			        iStartYear = YssFun.getYear(dStartDate);
			        iStartMonth = YssFun.getMonth(dStartDate);
			        iStartDay = YssFun.getDay(dStartDate);
			        iStartFriDate = YssFun.endOfMonth(iStartYear, 2);
			        //计息日的年月日，及2月份最后日期
			        iEndYear = YssFun.getYear(dEndDate);
			        iEndMonth = YssFun.getMonth(dEndDate);
			        iEndDay = YssFun.getDay(dEndDate);
			        iEndFriDate = YssFun.endOfMonth(iEndYear, 2);

	                //1. 普通月份处理(除去2月份)
			        //1.1 特殊债券处理：起息日是31日情况，要在起息日当天(31)当天要计提利息.
			        if (iStartDay == 31 && iEndDay != 31) {
			            iStartDay = 30;
			        }
			        if (iEndDay == 31 && (iStartDay == 31 || iStartDay == 30)) { //修改 ly 因为这是并且的关系
			            iEndDay = 1;
			            if(iEndMonth ==12){
			            	iStartYear +=1;
			            	iEndMonth=1;
			            }else{
			            	iEndMonth +=1;
			            }
			            
			        }

			      
			        
			        if(isToday){
				        dDate = (360 * (iEndYear - iStartYear) + 30 * (iEndMonth - iStartMonth) +
				                 (iEndDay - iStartDay)) * 1.0 / 360;
			        }else{
			        	
			        	int days =0;
			        	if(iEndMonth ==3 && iEndDay==1){
			        		iEndMonth=2;
			        		days = YssFun.endOfMonth(YssFun.addDay(dEndDate, -1))-iStartDay;
			        	}else{
			        		days = iEndDay - iStartDay-1;
			        	}
			        	
				        dDate = (360 * (iEndYear - iStartYear) + 30 * (iEndMonth - iStartMonth) +days) * 1.0 / 360;
			        }


			    } catch (Exception e) {
			        throw new YssException("计算factorEPlus出错!\n", e);
			    }
			    return dDate;
			}
		
		
		
	    /**
	     * @author yanghaiming 20110222 #601
	     * @param insDate Date; 计息日期
	     * @param SecurityCode String：债券代码
	     * @param rs ResultSet：包含债券信息的结果集
	     * @param hmReturn HashMap：用于返回结果值的哈希表
	     * @throws YssException
	     */
	    public void getRealStartDateAndEndDate(java.util.Date insDate,String SecurityCode, ResultSet rs,
	                                           HashMap hmReturn) throws YssException {
	    	java.util.Date startDate = (java.util.Date) hmReturn.get("InsStartDate");
	    	java.util.Date endDate = (java.util.Date) hmReturn.get("InsEndDate");
	    	java.util.Date lastStartDate = null;
	    	java.util.Date nextEndDate = null;
	    	java.util.Date lastEndDate = null;
	    	java.util.Date tempStartDate = null;
	    	java.util.Date tempEndDate = null;
	    	String dates = "";
	    	int iTerm = 0;//付息期间的间隔月份数
	    	String[] dateAry = null;
	        try {
	        	dates = rs.getString("FVALUEDATES");
	        	iTerm = YssFun.toInt(String.valueOf(YssD.div(12, rs.getDouble("FInsFrequency"))));
	        	lastStartDate = YssFun.addMonth(startDate, 0-iTerm);
	        	nextEndDate = YssFun.addMonth(endDate, iTerm);
	        	lastEndDate = YssFun.addMonth(endDate, 0-iTerm);
	        	if (dates != null && !dates.equalsIgnoreCase(" ")){
		        	dateAry = dates.split(";");
					for (int i = 0; i < dateAry.length; i++) {
						if (YssFun.dateDiff(insDate, YssFun.toDate(dateAry[i].toString())) <= 0){//获取起息日设置中小于最近计息日的记录，得到设置中的起息日
							if (tempStartDate == null){
								tempStartDate = YssFun.toDate(dateAry[i].toString());
							}else if (YssFun.dateDiff(tempStartDate, YssFun.toDate(dateAry[i].toString())) > 0){
								tempStartDate = YssFun.toDate(dateAry[i].toString());
							}
						}else{//获取起息日设置中大于计息日最近的记录，得到设置中的截息日
							if (tempEndDate == null){
								tempEndDate = YssFun.addDay(YssFun.toDate(dateAry[i].toString()),-1);
							}else if (YssFun.dateDiff(tempEndDate, YssFun.toDate(dateAry[i].toString())) < 0){
								tempEndDate = YssFun.addDay(YssFun.toDate(dateAry[i].toString()),-1);
							}
						}
//						if (YssFun.dateDiff(startDate, YssFun.toDate(dateAry[i]
//								.toString())) > 0
//								&& YssFun.dateDiff(endDate, YssFun.toDate(dateAry[i]
//								.toString())) < 0 && YssFun.dateDiff(insDate, YssFun.toDate(dateAry[i]
//								.toString())) > 0) {// 设置的起息日大于系统计算出来的起息日，并且小于系统计算出来的截止日,并且计息日小于设置的起息日
//							hmReturn.put("InsStartDate", lastStartDate);
//							if(YssFun.dateDiff(lastEndDate, YssFun.toDate(dateAry[i]
//							       .toString())) > 0
//							       && YssFun.dateDiff(lastEndDate, YssFun.toDate(dateAry[i]
//							       .toString())) < 0){// 设置的起息日大于系统计算出来的截至，并且小于下一个计息期间的截止日
//								hmReturn.put("InsEndDate", YssFun.addDay(YssFun.toDate(dateAry[i].toString()),-1));//截至日为下一个起息日前一天
//							}else{
//								
//							}
//						} else if(YssFun.dateDiff(startDate, YssFun.toDate(dateAry[i]
//						        .toString())) > 0
//						        && YssFun.dateDiff(endDate, YssFun.toDate(dateAry[i]
//						        .toString())) < 0 && YssFun.dateDiff(insDate, YssFun.toDate(dateAry[i]
//						        .toString())) > 0){// 设置的起息日大于系统计算出来的起息日，并且小于系统计算出来的截止日,并且计息日大于设置的起息日
//							hmReturn.put("InsStartDate", YssFun.toDate(dateAry[i].toString()));
//						}else if (YssFun.dateDiff(endDate, YssFun.toDate(dateAry[i]
//								.toString())) > 0
//								&& YssFun.dateDiff(nextEndDate, YssFun.toDate(dateAry[i]
//										.toString())) < 0) {// 设置的起息日大于系统计算出来的截至，并且小于下一个计息期间的截止日
////							if (YssFun.toDate(dateAry[i].toString()).compareTo(rs.getDate("FInsEndDate")) > 0) {
////				                hmReturn.put("InsEndDate", rs.getDate("FInsEndDate"));
////				            } else {
//				                hmReturn.put("InsEndDate", YssFun.addDay(YssFun.toDate(dateAry[i].toString()),-1));//截至日为下一个起息日前一天
////				            }
//						}
					}
//					if (tempStartDate == null){
//						tempStartDate = startDate;
//					}
//					if (tempEndDate == null){
//						tempEndDate = endDate;
//					}
					if (tempStartDate != null 
							&& YssFun.dateDiff(startDate, tempStartDate) >= 0 
							&& YssFun.dateDiff(endDate, tempStartDate) <= 0
							&& YssFun.dateDiff(YssFun.addMonth(insDate, 0-iTerm),tempStartDate) > 0){//如果设置的起息日大于上一期截息日
						hmReturn.put("InsStartDate", tempStartDate);
					}else if(tempEndDate != null && tempStartDate == null
							&& YssFun.dateDiff(endDate, tempEndDate) <= 0 
							&& YssFun.dateDiff(nextEndDate, tempEndDate) <= 0){
						hmReturn.put("InsStartDate", lastStartDate);
					}
					if (tempEndDate != null 
							&& YssFun.dateDiff(endDate, tempEndDate) >= 0 
							&& YssFun.dateDiff(nextEndDate, tempEndDate) < 0
							&& YssFun.dateDiff(YssFun.addMonth(insDate, iTerm),tempEndDate) < 0){
						hmReturn.put("InsEndDate", tempEndDate);
						if (tempStartDate != null 
								&& YssFun.dateDiff(lastStartDate, tempStartDate) >= 0 
								&& YssFun.dateDiff(startDate, tempStartDate) <= 0
								&& YssFun.dateDiff(YssFun.addMonth(insDate, 0-iTerm),tempStartDate) > 0){
							hmReturn.put("InsStartDate", tempStartDate);
						}
					}else if (tempEndDate != null 
							&& YssFun.dateDiff(lastEndDate, tempEndDate) >= 0 
							&& YssFun.dateDiff(endDate, tempEndDate) < 0
							&& YssFun.dateDiff(YssFun.addMonth(insDate, iTerm),tempEndDate) < 0){
						hmReturn.put("InsEndDate", tempEndDate);
						hmReturn.put("InsStartDate", lastStartDate);
						if (tempStartDate != null 
								&& YssFun.dateDiff(lastStartDate, tempStartDate) >= 0 
								&& YssFun.dateDiff(startDate, tempStartDate) <= 0
								&& YssFun.dateDiff(YssFun.addMonth(insDate, 0-iTerm),tempStartDate) > 0){
							hmReturn.put("InsStartDate", tempStartDate);
						}
					}
	        	}
	        } catch (Exception e) {
	            throw new YssException(e.getMessage(), e);
	        }
	    }
	    //add by luopc QDV4易方达2011年6月9日02_A 获取当日并包括当日的利息余额
	     private double yearNum(java.util.Date isDate,java.util.Date startDate,boolean containIsDate) throws YssException{
	     	double yearNum=0;
	     	Date y;//移动的日期
	     	for(y=startDate;y.before(isDate);y=YssFun.addYear(y, 1)){
	     			yearNum+=1;
	     	}
	     	yearNum--;

	     	//从for循环中出来的y此时在当前日期以后，
	     	//当年天数为days
	     	int days=YssFun.dateDiff(YssFun.addYear(y, -1), y);
	     	//当前日期距离当前期间起息日的天数
	     	y=YssFun.addYear(y, -1);
	     	int dayDiff=0;
	     	dayDiff=YssFun.dateDiff(y,isDate)+1;

	     	
	     	yearNum+=(dayDiff+0.0)/days;
	     	return yearNum;
	     }
	     //add by luopc，获取结算日前一天的利息余额，因为交易利息是计算截止结算日前一日的利息余额
	     //QDV4易方达2011年6月9日02_A
	     private double yearNum(java.util.Date isDate,java.util.Date startDate) throws YssException{
	     	double yearNum=0;
	     	Date y;//移动的日期
	     	for(y=startDate;y.before(isDate);y=YssFun.addYear(y, 1)){
	     			yearNum+=1;
	     	}
	     	yearNum--;

	     	//从for循环中出来的y此时在当前日期以后，
	     	//当年天数为days
	     	int days=YssFun.dateDiff(YssFun.addYear(y, -1), y);
	     	//当前日期距离当前期间起息日的天数
	     	y=YssFun.addYear(y, -1);
	     	int dayDiff=0;
	     	dayDiff=YssFun.dateDiff(y,isDate);  	
	     	yearNum+=(dayDiff+0.0)/days;
	     	return yearNum;
	     }
	     
	     public double getFIInsUncheckD(java.util.Date dDate) throws YssException { 
	    	 return getFIInsUncheckD(dDate, "0");
	     }
	     
	   //add by luopc QDV4易方达2011年6月9日02_A
	    public double getFIInsUncheckD(java.util.Date dDate, String ctlBargainDate) throws //添加交易类型字段，这里获取固定交易类型的数据  QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
			YssException { //增加参数,用于判断是否从买入结算日当日开始计息 sj edit 20080124
			String strSql = "";
			ResultSet rs = null;
			double dResult = 0;	
			boolean analy1;
			boolean analy2;
			boolean analy3;
			try {
				analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
				analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
				analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
				strSql =
				 "select fbondins*FAmountInd as FBondIns,FSettleDate,FBargainDate from ";
				//---------------------------------------------------------------------------//
				strSql += pub.yssGetTableName("Tb_Data_Intbakbond") +
				" a left join (select * from Tb_Base_TradeType where FCheckState = 1) b on a.FTradeTypeCode = b.FTradeTypeCode" +
				" where FSecurityCode = " +
				dbl.sqlString(bondIns.getSecurityCode()) +
				" and a.FCheckState = 1 " + //--- add by wangzuochun 2010.05.21 MS01180    利息公司中"FIInsTrade"获取交易利息时,未加已审核条件    QDV4华夏2010年5月20日01_B  
				" AND a.FAttrClsCode = " + dbl.sqlString(bondIns.getAttrClsCode()) +//2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
				" and FPortCode = " + dbl.sqlString(bondIns.getPortCode()) + //增加组合的筛选,获取某组合的获取债券买卖的利息.sj edit 20080818 bug 0000417
				(analy1 ?
				" and FInvMgrCode = " + dbl.sqlString(bondIns.getAnalysisCode1()) :
				"") +
				(analy2 ?
				" and FBrokerCode = " + dbl.sqlString(bondIns.getAnalysisCode2()) :
				"");
				//QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
				//" and FBargainDate < " + dbl.sqlDate(dDate);//MS00209 QDV4建行2009年1月20日01_B 将筛选调整移入判断条件内。
				//---------------//MS00209 QDV4建行2009年1月20日01_B sj modified--------------------------//\
				
				//modify by zhouwei 20120420 根据银行间债券的成交日和结算日来区分T+0和T+N
//				if("0".equalsIgnoreCase(ctlBargainDate)){
//					strSql +=
//					     " and FBargainDate <= " + dbl.sqlDate(dDate); 
//				}else if("1".equalsIgnoreCase(ctlBargainDate)){
//					strSql +=
//					     " and FBargainDate < " + dbl.sqlDate(dDate);
//				}
				
				//-----------------------------------------------------------//
//				strSql +=
//				 " and " + dbl.sqlDate(dDate) + " < FSettleDate";
//				
				//--------------------------------------------------------------------------------------------
				strSql += " and FBargainDate <="+dbl.sqlDate(dDate)+" and FSettleDate >=" + dbl.sqlDate(dDate);
				rs = dbl.openResultSet(strSql);
				//modify by zhouwei 20120420  交易利息的处理
				while(rs.next()) {
					if(YssFun.dateDiff(rs.getDate("FSettleDate"),rs.getDate("FBargainDate"))==0
							&& YssFun.dateDiff(dDate,rs.getDate("FBargainDate"))==0){//T+0
						dResult=YssD.add(rs.getDouble("FBondIns"),dResult);
					}
					if(YssFun.dateDiff(rs.getDate("FSettleDate"),rs.getDate("FBargainDate"))!=0
							&& YssFun.dateDiff(rs.getDate("FBargainDate"),dDate)>0
							&& YssFun.dateDiff(dDate,rs.getDate("FSettleDate"))>0){//T+N
						dResult=YssD.add(-rs.getDouble("FBondIns"),dResult);
					}
				}
				//add by zhouwei 20120419 债券转托管业务 
				dbl.closeResultSetFinal(rs);
				strSql="select sum(a.FBONDINS) as FBONDINS from "+pub.yssGetTableName("TB_DATA_DEVTRUSTBOND")
		              +" a LEFT JOIN " + pub.yssGetTableName("Tb_Para_Security") + " b" 
		              +" on a.FSecurityCode=b.FSecurityCode"
					  +" where a.FSecurityCode = " +dbl.sqlString(bondIns.getSecurityCode()) +//转出银行间债券
						" and  a.FCheckState = 1 and b.FEXCHANGECODE='CY'" +   
						" and  a.FPortCode = " + dbl.sqlString(bondIns.getPortCode()) 
					  + " and  a.FBARGAINDATE="+dbl.sqlDate(dDate);
				rs=dbl.openResultSet(strSql);
				if(rs.next()){
					dResult=YssD.sub(dResult, rs.getDouble("FBONDINS"));
				}
				dbl.closeResultSetFinal(rs);
				strSql="select sum(a.FBONDINS) as FBONDINS from "+pub.yssGetTableName("TB_DATA_DEVTRUSTBOND")
					  +" a LEFT JOIN " + pub.yssGetTableName("Tb_Para_Security") + " b" 
					  +" on a.FInSecurityCode=b.FSecurityCode"
					  +" where a.FInSecurityCode = " +dbl.sqlString(bondIns.getSecurityCode()) +//转入银行间债券
						" and  a.FCheckState = 1 and b.FEXCHANGECODE='CY'" +   
						" and  a.FPortCode = " + dbl.sqlString(bondIns.getPortCode()) 
					  + " and  a.FBARGAINDATE="+dbl.sqlDate(dDate);
				rs=dbl.openResultSet(strSql);
				if(rs.next()){
					dResult=YssD.add(dResult, rs.getDouble("FBONDINS"));
				}
				//add by zhouwei 证券变更业务，变更后债券为银行间债券时 BUG4367
				dbl.closeResultSetFinal(rs);
				strSql="select SUM(NVL(c.fmoney,0)) as fmoney from "+pub.yssGetTableName("Tb_Para_SecCodeChange")+" a "
					  +" left join (select fexchangecode,fsecuritycode from "+pub.yssGetTableName("tb_para_security")+" where fcheckstate=1)b"
					  +" on a.fsecuritycodeafter=b.fsecuritycode"
					  +" left join (select FMONEY,FTRANSDATE,FPORTCODE,FSECURITYCODE from "+pub.yssGetTableName("tb_data_secrecpay")
					  +"  where fcheckstate=1 and FSUBTSFTYPECODE='06FI_B' and FPortCode="+dbl.sqlString(bondIns.getPortCode())+") c"//变更后债券利息
					  +" on a.fbusinessdate=c.ftransdate and a.fsecuritycodeafter=c.fsecuritycode"
					  +" where a.fcheckstate=1 and b.fexchangecode='CY' and c.fmoney is not null"
					  +" and a.fsecuritycodeafter="+dbl.sqlString(bondIns.getSecurityCode())
					  +" and a.fbusinessdate="+dbl.sqlDate(dDate);
				rs=dbl.openResultSet(strSql);
				if(rs.next()){
					dResult=YssD.add(dResult, rs.getDouble("fmoney"));
				}
				return dResult;
			} catch (Exception e) {
				throw new YssException(e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
	 }
	   //add by luopc QDV4易方达2011年6月9日02_A
     public double getFIInsTradeD(java.util.Date dDate, boolean DayFi
             ,String isOnlyBargainDate,//----MS00255 QDV4建行2009年2月17日02_B sj modified,增加对是否只取交易日当日的买卖利息
             String tradeTypeCodes) throws //添加交易类型字段，这里获取固定交易类型的数据  QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
		YssException { //增加参数,用于判断是否从买入结算日当日开始计息 sj edit 20080124
		String strSql = "";
		ResultSet rs = null;
		double dResult = 0;
		boolean analy1;
		boolean analy2;
		boolean analy3;
		try {
			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
			analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
			//-----MS00209 QDV4建行2009年1月20日01_B sj modified --------------------------//
			if (DayFi) { //为true时，减去买入利息
			strSql =
			 "select sum(fbondins*FAmountInd*-1) as FBondIns from ";
			} else { //为false时，加上买入利息
			strSql =
			 "select sum(fbondins*FAmountInd) as FBondIns from ";
			}
			//---------------------------------------------------------------------------//
			strSql += pub.yssGetTableName("Tb_Data_Intbakbond") +
			" a left join (select * from Tb_Base_TradeType where FCheckState = 1) b on a.FTradeTypeCode = b.FTradeTypeCode" +
			" where FSecurityCode = " +
			dbl.sqlString(bondIns.getSecurityCode()) +
			" and a.FCheckState = 1 " + //--- add by wangzuochun 2010.05.21 MS01180    利息公司中"FIInsTrade"获取交易利息时,未加已审核条件    QDV4华夏2010年5月20日01_B  
			" AND a.FAttrClsCode = " + dbl.sqlString(bondIns.getAttrClsCode()) +//2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
			" and FPortCode = " + dbl.sqlString(bondIns.getPortCode()) + //增加组合的筛选,获取某组合的获取债券买卖的利息.sj edit 20080818 bug 0000417
			(analy1 ?
			" and FInvMgrCode = " + dbl.sqlString(bondIns.getAnalysisCode1()) :
			"") +
			(analy2 ?
			" and FBrokerCode = " + dbl.sqlString(bondIns.getAnalysisCode2()) :
			"");
			//添加对交易方式的处理 QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318 合并太平版本代码
			if(tradeTypeCodes!=null && tradeTypeCodes.length()>0){
			strSql=strSql + " and a.FTradeTypeCode in ("+operSql.sqlCodes(tradeTypeCodes)+")";
			}
			//QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
			//" and FBargainDate < " + dbl.sqlDate(dDate);//MS00209 QDV4建行2009年1月20日01_B 将筛选调整移入判断条件内。
			//---------------//MS00209 QDV4建行2009年1月20日01_B sj modified--------------------------//
			if (DayFi) { //为true时，不包括交易和实际结算日期
			//----MS00255 QDV4建行2009年2月17日02_B sj modified ----------//
			if (isOnlyBargainDate.toLowerCase().equalsIgnoreCase("B")) { //只在交易日获取买卖利息
			 strSql +=
			     " and FBargainDate = " + dbl.sqlDate(dDate);
			} else {
			 strSql +=
			     " and FBargainDate < " + dbl.sqlDate(dDate); //将筛选调整移入判断条件内
			}
			//-----------------------------------------------------------//
			strSql +=
			 " and " + dbl.sqlDate(dDate) + " < FSettleDate";
			} else { //当为false时，包括交易和实际结算日期。包含交易日期与实际结算日期相同的情况。
			//----MS00255 QDV4建行2009年2月17日02_B sj modified ----------//
			if (isOnlyBargainDate.toLowerCase().equalsIgnoreCase("B")) { //只在交易日获取买卖利息
			 strSql +=
			     " and FBargainDate = " + dbl.sqlDate(dDate);
			} else {
			 strSql +=
			     " and FBargainDate <= " + dbl.sqlDate(dDate); //将筛选调整移入判断条件内，包括交易日期当日的数据
			}
			//----------------------------------------------------------//
			strSql +=
			 " and " + dbl.sqlDate(dDate) + " <= FSettleDate";
			}
			//--------------------------------------------------------------------------------------------
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
			dResult = rs.getDouble("FBondIns");
			}
			return dResult;
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	 }
	 //add by luopc QDV4易方达2011年6月9日02_A
     private double isHoliday(java.util.Date dDate){
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);    	 
    	 try {
			if(dDate.equals(operDeal.getWorkDay(bondIns.getHolidaysCode(), dDate, 0))){
				 return 0;
			 }
			else return 1;
		} catch (YssException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
    	 
     }
	     
     /// 获取当日（是工作日）交易同时交割的应收利息
	 //add by luopc QDV4易方达2011年6月9日02_A
     public double getFIInsTradeSettleD(java.util.Date dDate) throws //添加交易类型字段，这里获取固定交易类型的数据  QDV4中保2010年03月03日03_A MS01011 by leeyu 20100318
 			YssException { //增加参数,用于判断是否从买入结算日当日开始计息 sj edit 20080124
		String strSql = "";
		ResultSet rs = null;
		double dResult = 0;
		boolean analy1;
		boolean analy2;
		boolean analy3;
		try {
		analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
		analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
		analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");

		strSql =
		 "select sum(fbondins*FAmountInd) as FBondIns from ";

		//---------------------------------------------------------------------------//
		strSql += pub.yssGetTableName("Tb_Data_Intbakbond") +
		" a left join (select * from Tb_Base_TradeType where FCheckState = 1) b on a.FTradeTypeCode = b.FTradeTypeCode" +
		" where FSecurityCode = " +
		dbl.sqlString(bondIns.getSecurityCode()) +
		" and a.FCheckState = 1 " + //--- add by wangzuochun 2010.05.21 MS01180    利息公司中"FIInsTrade"获取交易利息时,未加已审核条件    QDV4华夏2010年5月20日01_B  
		" AND a.FAttrClsCode = " + dbl.sqlString(bondIns.getAttrClsCode()) +//2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
		" and FPortCode = " + dbl.sqlString(bondIns.getPortCode()) + //增加组合的筛选,获取某组合的获取债券买卖的利息.sj edit 20080818 bug 0000417
		(analy1 ?
		" and FInvMgrCode = " + dbl.sqlString(bondIns.getAnalysisCode1()) :
		"") +
		(analy2 ?
		" and FBrokerCode = " + dbl.sqlString(bondIns.getAnalysisCode2()) :
		"");

		strSql +=
		 " and " + dbl.sqlDate(dDate) + " = FSettleDate"+" and fbargaindate="+dbl.sqlDate(dDate);

		//--------------------------------------------------------------------------------------------
		rs = dbl.openResultSet(strSql);
		if (rs.next()) {
			dResult = rs.getDouble("FBondIns");
		}
		return dResult;
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
     }
	     
     /**
      * add by songjie 
      * 2011.10.18 
      * 需求 1245 QDV4易方达2011年6月20日01_A
      */
     public void insertDate()throws YssException{
    	 PreparedStatement pst = null;
    	 Connection conn = null;
    	 String sqlStr = null;
    	 StringBuffer buff = null;
    	 try{
    		 String securitycode = this.security.getSecurityCode();
    		 Date isDate = bondIns.getInsDate();
    		 
    		 Date startDate = this.security.getFixInterest().getDtStartDate();//计息起始日
    		 Date endDate = this.security.getFixInterest().getDtInsEndDate();//计息截止日
    		 Date cashDate = this.security.getFixInterest().getDtInsCashDate();//到期日期
    		 Date thisStartDate = this.security.getFixInterest().getDtThisInsStartDate();//本期间计息起始日
    		 Date thisEndDate = this.security.getFixInterest().getDtThisInsEndDate();//本期间计息截止日
    		 int dqts = YssFun.dateDiff(isDate, cashDate);
    		 int fxts = YssFun.dateDiff(isDate, thisEndDate);
    	
    		 if(!dbl.yssTableExist("tmp_FI_Date")){
    			 buff = new StringBuffer();
    			 
    			 buff.append(" create table TMP_FI_DATE ");
    			 buff.append(" ( ");
    			 buff.append(" SECURITYCODE  VARCHAR2(50) not null, ");
    			 buff.append(" ISDATE        DATE not null, ");
    			 buff.append(" CASHDATE      DATE not null, ");
    			 buff.append(" STARTDATE     DATE not null, ");
    			 buff.append(" ENDDATE       DATE not null, ");
    			 buff.append(" THISSTARTDATE DATE not null, ");
    			 buff.append(" THISENDDATE   DATE not null, ");
    			 buff.append(" FXTS          NUMBER(5) not null, ");
    			 buff.append(" DQTS          NUMBER(5) not null, ");
    			 buff.append(" constraint PK_TMP_FI_DATE primary key (ISDATE, SECURITYCODE) ");
    			 buff.append(" ) ");
    			 
    			 dbl.executeSql(buff.toString());
    			 buff.delete(0, buff.length());
    		 }
    		 
    		 conn = dbl.loadConnection();
    		 sqlStr = "delete from tmp_FI_Date a where a.isDate="+dbl.sqlDate(isDate)+" and a.securitycode = "+ dbl.sqlString(securitycode) ;
    	
    		 conn.setAutoCommit(false);
    		 
    		 dbl.executeSql(sqlStr);
    		 sqlStr = "insert into tmp_FI_Date(SECURITYCODE,ISDATE,CASHDATE,STARTDATE," +
    		 "ENDDATE,THISSTARTDATE,THISENDDATE,FXTS,DQTS) values (?,?,?,?,?,?,?,?,?)";
    		 pst=conn.prepareStatement(sqlStr);
    		 
    		 pst.setString(1, securitycode);
    		 pst.setDate(2, YssFun.toSqlDate(isDate));
    		 pst.setDate(3, YssFun.toSqlDate(cashDate));
    		 pst.setDate(4, YssFun.toSqlDate(startDate));
    		 pst.setDate(5, YssFun.toSqlDate(endDate));
    		 pst.setDate(6, YssFun.toSqlDate(thisStartDate));
    		 pst.setDate(7, YssFun.toSqlDate(thisEndDate));
    		 pst.setInt(8, fxts);
    		 pst.setInt(9, dqts);
    		 
    		 pst.execute();
    		 
    		 conn.commit();
    		 conn.setAutoCommit(true);
    	 }catch(Exception e){
    		 throw new YssException("获取债券付息日期出错！", e);
    	 }finally{
    		 dbl.closeStatementFinal(pst);
    		 dbl.endTransFinal(conn, false);
    	 }
		
    }
     
     /**
      * 20120908 added by liubo.Story #2933
      * 根据付息期间是否包含2月29日这个日期来获取计息天数
      * 如付息期间包含2月29日，则返回366；如付息期间不包含2月29日，则返回365。 
      */
     public double DynamicDays()throws YssException
     {
    	 double dReturn = 365;
    	 String strSql = "";
    	 ResultSet rs = null;
    	 
    	 try
    	 {
	    	 java.util.Date dInsStartDate = (Date)this.getKeywordValue("ThisInsStartDate");
	    	 java.util.Date dInsEndDate = (Date)this.getKeywordValue("ThisInsEndDate");
	    	 
	    	 strSql = "select 'true' as TheResult from dual " +
	    	 		  " where to_date('0229','mmdd') between " + dbl.sqlDate(dInsStartDate) + " and " + dbl.sqlDate(dInsEndDate);
	    	 rs = dbl.queryByPreparedStatement(strSql);
	    	 
	    	 while(rs.next())
	    	 {
	    		 if (rs.getString("TheResult").equalsIgnoreCase("true"))
	    		 {
	    			 dReturn = 366;
	    		 }
	    	 }
	    	 
	    	 return dReturn;
    	 }
    	 catch(Exception ye)
    	 {
    		 throw new YssException("获取动态计息天数出错：" + ye.getMessage());
    	 }
    	 finally
    	 {
    		 dbl.closeResultSetFinal(rs);
    	 }
    	 
    	 
     }
     
}

