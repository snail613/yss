package com.yss.main.operdeal.invest;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.parasetting.InvestPayBean;
import com.yss.main.parasetting.PerformulaRelaBean;
import com.yss.main.parasetting.PeriodBean;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.pojo.param.invest.YssInvestInfo;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class InvestCfgFormula
    extends BaseInvestOper {
    private YssInvestInfo investInfo = null;
    private String IVPayCatCode = "";
    private String portCode = "";
    private java.util.Date tradeDate = null;
    private String analys1 = "";
    private String analys2 = "";
    private String analys3 = "";
    private String calcFomula = "";
    private InvestPayBean investpay = null;
    private double money = 0.0; //add by fangjiang 2011.10.26 STORY #1589
    public InvestCfgFormula() {
    }

    /**
     * initial the Invest information
     * @param obj Object
     * @throws YssException
     */
    public void init(Object obj) throws YssException {
        if (obj == null) {
            return;
        }
        investInfo = (YssInvestInfo) obj;
        this.sign = "(,),+,-,*,/,>,<,="; //添加了最后三个标记，为了在函数中直接判断。sj edit 20080804

        this.IVPayCatCode = investInfo.getSIVPayCatCode();
        this.portCode = investInfo.getSPortCode();
        this.tradeDate = investInfo.getDDate();
        this.analys1 = investInfo.getSAnalys1();
        this.analys2 = investInfo.getSAnalys2();
        this.analys3 = investInfo.getSAnalys3();
        this.calcFomula = investInfo.getCalcFomula();
        try {
            getCalcInvestInfo(IVPayCatCode, portCode, analys1, analys2, analys3,
                              calcFomula);
        } catch (Exception e) {
            throw new YssException("初始化运营收支出错!");
        }
    }
    
    //add by fangjiang 2011.10.26 STORY #1589
    public void init(double money, String fomula, String portCode, java.util.Date date) throws YssException {
    	String sqlStr = "";
        ResultSet rs = null;
    	this.sign = "(,),+,-,*,/,>,<,=";
    	this.portCode = portCode;
    	this.tradeDate = date;
    	this.money = money;
    	sqlStr = "select FFormula,FSPICode from Tb_Base_CalcInsMetic where FCheckState = 1 and FSPICode = " +dbl.sqlString(fomula);
	    try {
	        rs = dbl.openResultSet(sqlStr);
	        if (rs.next()) {
	            this.formula = rs.getString("FFormula");
	        } else {
	            throw new YssException("无法获取费用计算公式，请检查算法公式设置！");
	        }
	    } catch (Exception ex) {
	        throw new YssException("获取计算公式出错!");
	    } finally {
	        dbl.closeResultSetFinal(rs);
	    }
    }
    
    public void init(double money, String fomula, String iVPayCatCode, String portCode, String clsPortCode, java.util.Date date) throws YssException {
    	String sqlStr = "";
        ResultSet rs = null;
    	this.sign = "(,),+,-,*,/,>,<,=";
    	this.portCode = portCode;
    	this.tradeDate = date;
    	this.money = money;
    	sqlStr = "select FFormula,FSPICode from Tb_Base_CalcInsMetic where FCheckState = 1 and FSPICode = " +dbl.sqlString(fomula);
	    try {	    	
	        rs = dbl.openResultSet(sqlStr);
	        if (rs.next()) {
	            this.formula = rs.getString("FFormula");
	        } else {
	            throw new YssException("无法获取费用计算公式，请检查算法公式设置！");
	        }
	        getCalcInvestInfo(iVPayCatCode, portCode, clsPortCode);
	    } catch (Exception ex) {
	        throw new YssException("获取计算公式出错!");
	    } finally {
	        dbl.closeResultSetFinal(rs);
	    }
    }

    public double calcInvest() throws YssException {
        return this.calcFormulaDouble();
    }

    private void getCalcInvestInfo(String ivPayCatCode, String portCode,
                                   String analys1, String analys2,
                                   String analys3, String calcFomula) throws
        YssException {
        String sqlStr = "";
        ResultSet rs = null;
        investpay = new InvestPayBean();
        try {
            investpay.setIvPayCatCode(ivPayCatCode);
            investpay.setPortCode(portCode);
            investpay.setFAnalysisCode1(analys1);
            investpay.setFAnalysisCode2(analys2);
            investpay.setFAnalysisCode3(analys3);
            investpay.setYssPub(pub);
            investpay.getSetting();
        } catch (Exception e) {
            throw new YssException("计算运营收支-获取运营收支信息出错!");
        }
        if (calcFomula == null || calcFomula.trim().length() == 0) {
            throw new YssException("无法获取计算公式");
        }
        sqlStr = "select FCIMCode,FCIMName,FCIMType,FFormula,FSPICode from Tb_Base_CalcInsMetic where FCheckState = 1  and FSPICode = " +
            dbl.sqlString(calcFomula);
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                this.formula = rs.getString("FFormula");
            } else {
                throw new YssException("无法获取费用计算公式，请检查算法公式设置！");
            }
        } catch (Exception ex) {
            throw new YssException("获取计算公式出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    private void getCalcInvestInfo(String ivPayCatCode, String portCode, String clsPortCode) throws YssException {
		String sqlStr = "";
		ResultSet rs = null;
		investpay = new InvestPayBean();
		try {
			investpay.setIvPayCatCode(ivPayCatCode);
			investpay.setPortCode(portCode);
			investpay.setClsPortCode(clsPortCode);
			investpay.setYssPub(pub);
			investpay.getSetting2();
		} catch (Exception e) {
			throw new YssException("计算运营收支-获取运营收支信息出错!");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

    public Object getExpressValue(String sExpress, ArrayList alParams) throws
        YssException {
    	Object objResult = null;
        if (sExpress.equalsIgnoreCase("NetValue")) { 
            if (alParams.size() == 1) {
                objResult = new Double(this.money);
            } 
        }else if(sExpress.equalsIgnoreCase("ManageFee")){
        	if (alParams.size() == 1) {
                objResult = new Double(getManageFee(convertToPortClsCode((String)alParams.get(0))));
            } 
        }else if(sExpress.equalsIgnoreCase("TrusteeFee")){
        	if (alParams.size() == 1) {
                objResult = new Double(getTrusteeFee(convertToPortClsCode((String)alParams.get(0))));
            } 
        }else if(sExpress.equalsIgnoreCase("STFee")){
        	if (alParams.size() == 1) {
                objResult = new Double(getSTFee(convertToPortClsCode((String)alParams.get(0))));
            } 
        }
        //---add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A start---//
        else if(sExpress.equalsIgnoreCase("FAFee")){
        	if (alParams.size() == 1) {
                objResult = new Double(getFAFee(convertToPortClsCode((String)alParams.get(0))));
            } 
        }
        //---add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A end---//
        else if(sExpress.equalsIgnoreCase("calMoneyByPerExp")){ //fj 2012.02.26
        	if (alParams.size() == 1) {
        		BaseOperDeal base = new BaseOperDeal();
    			base.setYssPub(pub);
                objResult = new Double(base.calMoneyByPerExp(investpay.getPerExpCode(), 
                		               this.money, this.tradeDate));
            } 
        }else if(sExpress.equalsIgnoreCase("getInvestValueByCode")){ //fj 2012.03.12
        	if (alParams.size() == 3) {
        		objResult = new Double(getInvestValueByCode((String)alParams.get(0),
				        				(String)alParams.get(1),
				        				(String)alParams.get(2)));
            } 
        }else if(sExpress.equalsIgnoreCase("getScaleByClsPortCode")){ //fj 2012.03.12
        	if (alParams.size() == 2) {
        		objResult = new Double(getScaleByClsPortCode((String)alParams.get(0),
        				              (String)alParams.get(1))); //第二个参数是计算占比的方式
            } 
        }
        //add by fangjiang 2012.05.02 stroy 2565
        else if(sExpress.equalsIgnoreCase("MarketValue")){ //fj 2012.03.12
        	if (alParams.size() == 3) {
        		objResult = new Double(getMarketValue((java.util.Date)alParams.get(0),
        				              (String)alParams.get(1),(String)alParams.get(2))); //第二个参数是计算占比的方式
            } 
        }
        //20121122 added by yeshenghong Story #3264
        //获取分级基金 C类销售份额资产净值
        //==============================
        else if (sExpress.equalsIgnoreCase("CServiceShare")) {
        	if (alParams.size() == 2){
        		objResult = new Double(getCServiceShare((String)alParams.get(0),(String)alParams.get(1)));
        	}
        }
        /**shashijie 2013-1-28 STORY 3497 指数使用费特殊计提公式,获取汇率,传参:(日期,原币,组合) */
        else if (sExpress.equalsIgnoreCase("getRateByDate")) {
        	if (alParams.size() == 3){
        		objResult = new Double(getRateByDate(
        				getYesterday("", "", this.tradeDate, -1),
        				(String)alParams.get(1),
        				this.portCode));
        	}
        }
		/**end shashijie 2013-1-28 STORY */

        //20130419 added by liubo.Story #3853
        //计算纳斯达克指数使用费。函数里的参数，表示小数保留位数
        //=============================
        else if (sExpress.equalsIgnoreCase("NasdaqZS")) {
        	if (alParams.size() == 1){
        		objResult = new String(NasdaqZS(Integer.parseInt((String)alParams.get(0))));
        	}
        }
        //=================end============
        
        //--- add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 start---//
        else if (sExpress.equalsIgnoreCase("NasdaqIndexFee")) {
        	if (alParams.size() == 1){
        		objResult = new String(NasdaqIndexFee(Integer.parseInt((String)alParams.get(0))));
        	}
        }else if(sExpress.trim().equalsIgnoreCase("portrate"))//获取币种的组合汇率
		{
			 objResult = getClsPortRate((Date)alParams.get(0),(String)alParams.get(1));//add by yeshenghong 20130802
		}else if(sExpress.trim().equalsIgnoreCase("classStaticValue"))//获取多CLASS的统计值
		{
			 objResult = getPortClassStaticValue((String)alParams.get(0),(Date)alParams.get(1),(String)alParams.get(2));
		}else if(sExpress.trim().equalsIgnoreCase("getClsPortCode"))
		{
			objResult = getClsPortCode((String)alParams.get(0),(String)alParams.get(1),(String)alParams.get(2));
		}else if(sExpress.trim().equalsIgnoreCase("getFixedDate"))
		{
			objResult =getFixedDate((Date)alParams.get(0),(String)alParams.get(1));//add by yeshenghong 20130802
		}
        //--- add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 end---//
        return objResult;
    }
    
    /**
	 * add by yeshenghong 2013-7-30
	 * 获取日期
	 * @param date   日期
     * @param 日期的改变
	 * @return String
     * @throws YssException 
     * @throws YssException 
	 */
    private Date getFixedDate(Date date, String countDays) throws YssException {
		// TODO Auto-generated method stub
    	int count = Integer.parseInt(countDays);
    	return YssFun.addDay(date, count);
	}
    
    /**
	 * add by yeshenghong 2013-7-30
	 * 获取组合分级代码
	 * @param clsType   份额类别
     * @param curyCode  币种代码 
     * @param cashType  现钞现汇 
	 * @return String
     * @throws YssException 
     * @throws YssException 
	 */
    private String getClsPortCode(String clsType, String curyCode, String cashType) throws YssException {
		// TODO Auto-generated method stub
    	ResultSet rs = null;
    	String strSql  = "";
    	String portclsCode = "";
    	try {
			strSql = " select FPORTCLSCODE from " + pub.yssGetTableName("tb_ta_portcls") + " where " +
		 	 " fportcode = " + dbl.sqlString(portCode) + " and  FSHARECATEGORY = " + clsType + 
		 	 " and FPORTCLSCURRENCY = " + dbl.sqlString(curyCode) +  " and FPORTCLSCASH = " + dbl.sqlString(cashType)   ;
			rs = dbl.openResultSet(strSql);
            if (rs.next()) {
            	portclsCode = rs.getString("FPORTCLSCODE");
            }
	    } catch (Exception e) {
	        throw new YssException(e);
	    } finally {
	        dbl.closeResultSetFinal(rs);
	    }
			return portclsCode;
	}
    
    /**
	 * 获取指定CLASS的指定类型的值
	 * add by yeshenghong 2013-5-16
	 * @param string
	 * @return Object
     * @throws YssException 
	 */
	private double getPortClassStaticValue(String clsPortCode,Date dDate,String staticType) throws YssException {
		ResultSet rs = null;
        String strSql = "";
        double clsNetValue = 0;
        try {
        		strSql = " select FClassNetValue from " + pub.yssGetTableName("tb_data_MultiClassNet") + " where fnavdate = " + dbl.sqlDate(dDate)
        			 	+ " and fportcode = " + dbl.sqlString(portCode) + " and  FCurycode = " + dbl.sqlString(clsPortCode) +
        			 	" and ftype =  " + dbl.sqlString(staticType);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                	clsNetValue = rs.getDouble("FClassNetValue");
                }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return clsNetValue;
	}
	
    /**
	 * add by yeshenghong 2013-7-30
	 * 获取币种的汇率
	 * @param string
     * @param date 
	 * @return String
     * @throws YssException 
	 */
    private double getClsPortRate(Date date, String curyCode) throws YssException {
		// TODO Auto-generated method stub
    	// TODO Auto-generated method stub
    	BaseOperDeal obj = new BaseOperDeal();
        obj.setYssPub(pub);
        double sPortRate = obj.getCuryRate(date, curyCode,
                                    this.portCode,
                                    YssOperCons.YSS_RATE_PORT);
        double sBaseRate = obj.getCuryRate(date, curyCode,
                this.portCode,
                YssOperCons.YSS_RATE_BASE);
		return sBaseRate/sPortRate;
	}
    
    /**
     * add by zhouwei 20120228 
     * 根据公式中分级级别定位到组合分级代码
     * @param portClsRank
     * @return
     * @throws YssException
     * modify by fangjiang 2012.03.07 
     */
    private String convertToPortClsCode(String portClsRank) throws YssException{
    	ResultSet rs=null;
    	String sql="";
    	String result="";
    	TaTradeBean ta = new TaTradeBean();
        ta.setYssPub(this.pub);
    	try{
    		if(ta.getAccWayState(this.portCode) == 0){ //博时多Class（多币种）
    			result = portClsRank;
			}else if(ta.getAccWayState(this.portCode) == 1){ //嘉时多Class（单币种）
				sql="select * from "+pub.yssGetTableName("tb_ta_portcls")+" where fcheckstate=1"
	    		   +" and fportcode="+dbl.sqlString(this.portCode)+" and fportclsrank="+dbl.sqlString(portClsRank);
	    		rs=dbl.openResultSet(sql);
	    		if(rs.next()){
	    			result = rs.getString("fportclscode");
	    		}
			}
    		return result;
    	}catch (Exception ex) {
			throw new YssException("根据分级级别查找组合分级代码出错！", ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    }
    
    public Object getKeywordValue(String sKeyword) throws YssException {
        Object objResult = null;
        if (investpay != null) {
            if (sKeyword.equalsIgnoreCase("feeRate")) { //年费率
                objResult = new Double(getPerValue(investpay.getPerExpCode(),
                    this.tradeDate));
            } else if (sKeyword.equalsIgnoreCase("Period")) { //期间
            	//modified by liubo.Story #2126
            	//考虑所选择区间的天数为0和1（也就是实际天数）
            	//==============================
//                objResult = new Double(getPeriod(investpay.getPeriodCode()));
            	//start modify huangqirong 2013-02-28 bug #7168 区间取数更改
            	/*double dbDateCount = getPeriod(investpay.getPeriodCode());
            	if(dbDateCount == 0)
            	{
            		objResult = new Double(365);
            	}
            	else if(dbDateCount == 1)
            	{
            		objResult = new Double(366);
            	}
            	else
            	{
            		objResult = new Double(dbDateCount);
            	}
            	*/
            	PeriodBean period = new PeriodBean();
				period.setYssPub(pub);
				period.setPeriodCode(this.investpay.getPeriodCode());
				// 检查费用期间设置是否正确
				if (period.getPeriodCode() == null
						|| period.getPeriodCode().trim().equals("null")
						|| period.getPeriodCode().trim().equalsIgnoreCase("")) {
					throw new YssException("请先维护" + this.investpay.getIvPayCatName() + "的期间设置");
				}
				period.getSetting();
            	// 如果periodType类型是1，则是实际天数类型，需要取得当年的实际天数；
				if (period.getPeriodType() == 1) {
					// 如果是闰年，实际天数为366
					if (YssFun.isLeapYear(this.tradeDate)) {
						objResult = new Double(366); // 费用除以期间设置中每年天数
					}
					// 如果不是闰年，实际天数为365
					else {
						objResult = new Double(365); // 费用除以期间设置中每年天数
					}
				} else {
					objResult = new Double(period.getDayOfYear()); // 费用除以期间设置中每年天数
				}
				//end modify huangqirong 2013-02-28 bug #7168 区间取数更改
            	//============end==================
            } else if (sKeyword.equalsIgnoreCase("isEstablishMonth")) { //是否为基金起始月份
                objResult = new Boolean(isEstablishMonth(tradeDate, portCode));
            } else if (sKeyword.equalsIgnoreCase("lastMonthNet")) { //上月末的资产净值
                objResult = new Double(getLastMonthNet(tradeDate, portCode, true)); ////MS00270 QDV4赢时胜（上海）2009年2月25日01_B
            } else if (sKeyword.equalsIgnoreCase("lastFactMonthsDays")) { //上月的实际天数
                objResult = new Double(getLastMonthFactDays(tradeDate, portCode));
            } else if (sKeyword.equalsIgnoreCase("lastMonthsDays")) { //上月的天数
                objResult = new Double(getLastMonthDays(tradeDate));
            }
            //--MS00257 QDV4博时2009年02月19日01_A  sj modifeid -------------------------//
            else if (sKeyword.equalsIgnoreCase("prelastMonthNet")) { //获取上上个月的净值数据
                objResult = new Double(getPreLastMonthNet(tradeDate, portCode));
            }
            //--------------------------------------------------------------------------//
            //------- MS00270 QDV4赢时胜（上海）2009年2月25日01_B 获取成立月的第一日净值数据--//
            else if (sKeyword.equalsIgnoreCase("lastFirstNet")) {
                objResult = new Double(getLastFirstNet(tradeDate, portCode));
            }
            //--------------------------------------------------------------------------//
            //----------add by wuweiqi 20110105  STORY #446、#432   --------------------------------//
            else if (sKeyword.equalsIgnoreCase("isHoliday")) {//是否为节假日
            	objResult = new Boolean(isHoliday(IVPayCatCode, portCode));
            }
            else if (sKeyword.equalsIgnoreCase("days")) {//获取节假日天数
            	objResult = new Double(getDays(IVPayCatCode, portCode));
            }
            else if (sKeyword.equalsIgnoreCase("toDayNet")) {//获取当日的资产净值
            	objResult = new Double(getDayNet(tradeDate, portCode));
            }
            //---add by songjie 2012.08.17 STORY #2886 QDV4中行2012年8月17日01_A start---//
            else if (sKeyword.equalsIgnoreCase("TAAmount")) {//获取当日的TA库存数量
            	objResult = new Double(getTAAmount(tradeDate, portCode));
            }
            //---add by songjie 2012.08.17 STORY #2886 QDV4中行2012年8月17日01_A end---//
            else if (sKeyword.equalsIgnoreCase("isMonthLastWorkDay")) {//是否为当月的月末最后一个工作日
            	objResult = new Boolean(isMonthLastWorkDay(IVPayCatCode,tradeDate, portCode));
            }
            else if (sKeyword.equalsIgnoreCase("monthLastWorkDayNet")) {//获取当月最后一个工作日的资产净值
            	objResult = new Double(getDayNet(tradeDate, portCode));
            }
            //20120206 added by liubo.Story #2126
            //获取昨日资产净值
            //=====================================
            else if (sKeyword.equalsIgnoreCase("yesterdayNav")) {
            	objResult = new Double(getYesterdayNav(portCode, IVPayCatCode, tradeDate));
            }
            //==================end===================
            
            //added by liubo.Story #1434
            //判断计提日当天是否为当年的最后一天。若是，返回“1=1”，不是则返回“1=2”
            //========================
            else if(sKeyword.equalsIgnoreCase("isLastDayOfYear")) {
            	objResult= new String(getTheLastDayOfYear(tradeDate));
            }
            //=============end===========
            
            //20121107 added by liubo.Story #2997
            //返回值说明：为当月最后一个工作日，返回1=1，否则返回1=2
            //==============================
            else if (sKeyword.equalsIgnoreCase("isLastWorkDayOfMonth")) {//是否为当月的月末最后一个工作日
            	objResult = new String(isMonthLastWorkDay(IVPayCatCode,tradeDate, portCode,false));
            }
            //===============end===============
            

            //added by liubo.Story #1434
            //获取实际进行计提的运营收支品种的当年实际计提总总金额，公式为计提前一日运营库存余额+计提日当日实际计提金额
            //===================================
            else if (sKeyword.equalsIgnoreCase("ReallyLimit")) {
            	objResult= new Double(getReallyLimit(portCode,tradeDate,IVPayCatCode));
            }
            //==============end=====================
            

            //added by liubo.Story #1434
            //获取应当计提的总金额。若ReallyLimit小于该金额则要用总金额-ReallyLimit进行轧差
            //若某只基金不是计提日当年成立，则直接返回70000
            //若是集体日的当年成立，则要用70000*datediff(基金成立日期,计提日期)/当年天数  来获取实际的总金额
            //==============================
            else if (sKeyword.equalsIgnoreCase("TargetLimit")) {
            	objResult= new Double(getTargetLimit(portCode,tradeDate,IVPayCatCode));
            }
            //=============end=================

            //added by liubo.Story #1434
            //获取计提日当年的年天数
            //==============================
            else if (sKeyword.equalsIgnoreCase("DaysOfYear")) {
            	objResult = new Double(getDaysOfYear(tradeDate));
            }
            //==============end================
            
            //20121106 added by liubo.Story #2997
            //获取计提日当月的天数
            //==============================
            else if (sKeyword.equalsIgnoreCase("DaysOfMonth")) {
            	objResult = new Double(getDaysOfMonth(tradeDate));
            }
            //===============end===============
            //------------------------------------end by wuweiqi -----------------------------------------------------//
            //add by fangjiang 2012.05.02 stroy 2565
            else if(sKeyword.equalsIgnoreCase("investDate")){
            	objResult = this.tradeDate;
            } else if(sKeyword.equalsIgnoreCase("aimETF")){
            	objResult = new String(this.getAimETF());
            }
            //end by fangjiang 2012.05.02 stroy 2565
            //add by huangqirong 2013-01-8 story #3400            
            else if(sKeyword.equalsIgnoreCase("FormulaFixValue")){ //取比率公式固定值
            	objResult = this.getFixValue(investpay.getPerExpCode(), this.tradeDate);
            }else if(sKeyword.equalsIgnoreCase("FactPeriodDays")){ //实际天数
				PeriodBean period = new PeriodBean();
				period.setYssPub(pub);
				period.setPeriodCode(this.investpay.getPeriodCode());
				// 检查费用期间设置是否正确
				if (period.getPeriodCode() == null
						|| period.getPeriodCode().trim().equals("null")
						|| period.getPeriodCode().trim().equalsIgnoreCase("")) {
					throw new YssException("请先维护" + this.investpay.getIvPayCatName() + "的期间设置");
				}
				period.getSetting();
				// 如果periodType类型是1，则是实际天数类型，需要取得当年的实际天数；
				if (period.getPeriodType() == 1) {
					// 如果是闰年，实际天数为366
					if (YssFun.isLeapYear(this.tradeDate)) {
						objResult = new Double(366); // 费用除以期间设置中每年天数
					}
					// 如果不是闰年，实际天数为365
					else {
						objResult = new Double(365); // 费用除以期间设置中每年天数
					}
				} else {
					objResult = new Double(period.getDayOfYear()); // 费用除以期间设置中每年天数
				}
            }else if(sKeyword.equalsIgnoreCase("FeeYesterdayBlance")){ //获取前一天的运营费用余额
            	objResult = new Double(this.getYestDayInvest(this.portCode, this.tradeDate, this.IVPayCatCode));
            }
            //---end---
            /**shashijie 2013-1-10 STORY 3402 国泰ETF,获取指数使用费情况1,均摊 */
            else if(sKeyword.equalsIgnoreCase("guotaiETFFee1")){
            	objResult = new Double(getGuoTaiETFFee1(tradeDate,portCode,investpay));
            }
            //国泰ETF,获取指数使用费情况2,需要判断昨日资产净值是否大于等价的1E美元
            else if(sKeyword.equalsIgnoreCase("guotaiETFFee2")){
            	objResult = new Double(this.getGuoTaiETFFee2(tradeDate,portCode,investpay));
            }
			/**end shashijie 2013-1-10 STORY */
            /**start add by huangqirong 2013-7-9 Story #4155 已计提天数  */
            else if(sKeyword.equalsIgnoreCase("InvestDays")){ //已计提天数
            	objResult = new Double(this.getInvestDays(this.tradeDate , this.IVPayCatCode));
            }else if(sKeyword.equalsIgnoreCase("FloatInvestFees")){//累计运营计提
            	objResult = new Double(this.getFloatInvestFees(this.IVPayCatCode, this.investpay.getPerExpCode(), 
            			this.portCode, this.tradeDate));
            }
			/**end add by huangqirong 2013-7-9 Story #4155 已计提天数*/
            else {
            	objResult = sKeyword;
            }

        } else {
        	objResult = sKeyword;
        }
        return objResult;
    }
    /**
     * add by huangqirong 2013-07-11 Story #4155
     * 计算每天的计提累计费用
     * */
    private double getFloatInvestFees(String sIvCode , String sPerExpCode , String sPortCode , java.util.Date dDate) throws YssException{
    	double  tatolValue = 0 ; //
		String strSql = "";
		ResultSet rs = null;
		ResultSet rsValDay = null;
		java.util.Date days = null;				//起始日期
		double yearRate = 0 ;					//年费率
		double objResult = 0 ;					//年天数
    	try
    	{
    		strSql = " select FIVPAYCATCODE, max(FSTARTDATE) as FDays," +
			"FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3," +
				 "FPORTCODE,FPORTCLSCODE from " + pub.yssGetTableName("Tb_Para_InvestPay") + " where FCheckState = 1 " + 
				 " and FStartDate <= " + dbl.sqlDate(dDate) + " and FIVPayCatCode = " + 
				 	dbl.sqlString(sIvCode) + 
				 " group by FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE " ;
	
    		rs = dbl.openResultSet(strSql);
			if(rs.next()){
				days = new java.util.Date(rs.getDate("FDays").getTime());
			}
			
			if(days != null){
				
				yearRate = this.getPerValue(sPerExpCode, dDate);				
				days = YssFun.addDay(days, -1);
				dDate = YssFun.addDay(dDate, -2);
				
				PeriodBean period = new PeriodBean();
				period.setYssPub(pub);
				period.setPeriodCode(this.investpay.getPeriodCode());
				// 检查费用期间设置是否正确
				if (period.getPeriodCode() == null
						|| period.getPeriodCode().trim().equals("null")
						|| period.getPeriodCode().trim().equalsIgnoreCase("")) {
					throw new YssException("请先维护" + this.investpay.getIvPayCatName() + "的期间设置");
				}
				period.getSetting();
				// 如果periodType类型是1，则是实际天数类型，需要取得当年的实际天数；
				if (period.getPeriodType() == 1) {
					// 如果是闰年，实际天数为366
					if (YssFun.isLeapYear(this.tradeDate)) {
						objResult = new Double(366); // 费用除以期间设置中每年天数
					}
					// 如果不是闰年，实际天数为365
					else {
						objResult = new Double(365); // 费用除以期间设置中每年天数
					}
				} else {
					objResult = new Double(period.getDayOfYear()); // 费用除以期间设置中每年天数
				}
				
				strSql = "select Fportnetvalue from " + pub.yssGetTableName("Tb_data_netvalue") +
                		" where FCheckState = 1 and FType = '01' and FportCode = " + dbl.sqlString(sPortCode)+ 
                		" and FNavDate between " + dbl.sqlDate(days) + " and " + dbl.sqlDate(dDate);
				rsValDay = dbl.openResultSet(strSql);
	            while(rsValDay.next()) {
	            	tatolValue += YssD.div(YssD.mul(rsValDay.getDouble("Fportnetvalue"), yearRate) , objResult);
	            }
			}
    	}catch(Exception ye){
    		throw new YssException("获取累计费用出错：" + ye);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		dbl.closeResultSetFinal(rsValDay);
    	}
    	return tatolValue;
    }
    
    /**
     * add by huangqirong 2013-7-9 Story #4155 已计提天数 
     * */
    private Double getInvestDays(java.util.Date investDate , String ivpayCatCode) throws YssException{
    	ResultSet rs = null;
    	double days = 0; //计提天数
    	String sql = " select FIVPAYCATCODE,(" + dbl.sqlDate(investDate) + " - max(FSTARTDATE)) + 1 as FDays," +
    			"FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3," +
    				 "FPORTCODE,FPORTCLSCODE from " + pub.yssGetTableName("Tb_Para_InvestPay") + " where FCheckState = 1 " + 
    				 " and FStartDate <= " + dbl.sqlDate(investDate) + " and FIVPayCatCode = " + 
    				 	dbl.sqlString(ivpayCatCode) + 
    				 " group by FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE " ;
    	try {
    		rs = dbl.openResultSet(sql);
    		if(rs.next()){
    			days = rs.getDouble("FDays");
    		}    		
		} catch (Exception e) {
			throw new YssException("获取已计提天数出错：" + e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return days;
    }
    
    /**shashijie 2013-1-10 STORY 3402 国泰ETF,获取指数使用费情况2,需要判断昨日资产净值是否大于等价的1E美元 */
	private double getGuoTaiETFFee2(Date dDate, String portCode,
			InvestPayBean investpay) throws YssException {
		//倒推出上一个自然日或工作日,最后讨论不使用
		/*Date yesterday = getYesterday(investpay.getHolidaysCode(),
				investpay.getAccrueTypeCode(),dDate,-1);*/
		//获取昨日汇率,对美元的
		double yesterRate = getRateByDate(dDate,"USD",portCode);
		//当年实际天数
		double yearDay = getYearDay(investpay,this.tradeDate);
		//获取昨日资产净值
		double yesterAssets = getYesterdayNav(portCode, investpay.getIvPayCatCode(), dDate);
		//费率0.06%		//不根据费率取数,直接写死
		double perValue = 0.0006;//getPerValue(investpay.getPerExpCode(), dDate);
		double value = 0;
		double E1 = YssD.mul(100000000, yesterRate);//1E美金等价的人命币
		//判断资产净值是否小于1E美金
		if (yesterAssets < E1) {
			//指数使用费 H2=E×0.06%÷当年天数
			value = getCommonAlgorithm(yesterAssets,perValue,yearDay);
		} else {
			//指数使用费 H2=1亿美元或等值人民币×0.06%÷当年天数 +（E-1亿美元或等值人民币）×0.04%÷当年天数
			double one = getCommonAlgorithm(E1,perValue,yearDay);
			double sub = YssD.sub(yesterAssets, E1);
			double two = getCommonAlgorithm(sub,0.0004,yearDay);
			value = YssD.add(one, two);
		}
		//根据舍入设置保留位数
		value = this.getSettingOper().reckonRoundMoney(
				investpay.getRoundCode() ,
				value);
		return value;
	}

	/**shashijie 2013-1-10 STORY 3402 国泰ETF,获取指数使用费情况1 */
	private double getGuoTaiETFFee1(Date dDate, String portCode,
			InvestPayBean investpay) throws YssException {
		//倒推出上一个自然日或工作日,最后讨论不使用
		/*Date yesterday = getYesterday(investpay.getHolidaysCode(),
				investpay.getAccrueTypeCode(),dDate,-1);*/
		//获取当日汇率,对美元的
		double yesterRate = getRateByDate(dDate,"USD",portCode);
		//当年实际天数
		double yearDay = getYearDay(investpay,this.tradeDate);
		//指数使用费 H1 = 40,000÷当年天数×美元对人民币汇率，保留两位小数
		double value = getCommonAlgorithm(40000, yesterRate,yearDay);
		//根据舍入设置保留位数
		value = this.getSettingOper().reckonRoundMoney(
				investpay.getRoundCode() ,
				value);
		return value;
	}

	/**shashijie 2013-1-10 STORY 3402 普通算法,d1*d2/d3*/
	private double getCommonAlgorithm(double d1, double d2,
			double d3) {
		double money = YssD.div(
				YssD.mul(d1, d2),d3);
		return money;
	}

	/**shashijie 2013-1-10 STORY 3402 根据费用信息设置获取上一个自然日或工作日*/
	protected Date getYesterday(String sHolidaysCode, String sAccrueTypeCode,
			Date dDate, int i) throws YssException{
		Date valueDate = dDate;
		if (sHolidaysCode==null || sHolidaysCode.trim().equals("")
				|| sHolidaysCode.trim().equalsIgnoreCase("null")) {
			valueDate = YssFun.addDay(valueDate, i);
			return valueDate;
		}
		//若选择工作日资产净值
    	if(sAccrueTypeCode.equalsIgnoreCase("EveDayNAV")){
    		valueDate = super.getSettingOper().getWorkDay(sHolidaysCode, dDate, i);
    	}else{//若选择自然日资产净值
    		valueDate = YssFun.addDay(dDate, i);
    	}
    	return valueDate;
	}

	/**shashijie 2013-1-10 STORY 3402 获取当年实际天数,期间设置 */
	private double getYearDay(InvestPayBean investpay,Date dDate) throws YssException {
		double dResult = 0;
		if (investpay==null) {
			return dResult;
		}
		PeriodBean period = new PeriodBean();
		period.setYssPub(pub);
		period.setPeriodCode(investpay.getPeriodCode());
		// 检查费用期间设置是否正确
		if (period.getPeriodCode() == null
				|| period.getPeriodCode().trim().equals("null")
				|| period.getPeriodCode().trim().equalsIgnoreCase("")) {
			throw new YssException("请先维护" + investpay.getIvPayCatName() + "的期间设置");
		}
		period.getSetting();
		// 如果periodType类型是1，则是实际天数类型，需要取得当年的实际天数；
		if (period.getPeriodType() == 1) {
			// 如果是闰年，实际天数为366
			if (YssFun.isLeapYear(dDate)) {
				dResult = 366; // 费用除以期间设置中每年天数
			}
			// 如果不是闰年，实际天数为365
			else {
				dResult = 365; // 费用除以期间设置中每年天数
			}
		} else {
			dResult = new Double(period.getDayOfYear()); // 费用除以期间设置中每年天数
		}
		return dResult;
	}

	/**shashijie 2013-1-10 STORY 3402 获取汇率 */
	private double getRateByDate(Date dDate, String sCuryCode, String portCode) throws YssException {
		double rate = 1;
		
		//基础汇率
		double baseRate = this.getSettingOper().getCuryRate(//基础汇率
					dDate,//汇率日期
					"",//基础汇率来源
					"",//基础汇率来源字段
					"",//组合来源
					"",//组合来源字段
					sCuryCode,//币种(原币,本币)
					portCode,//组合
					YssOperCons.YSS_RATE_BASE);//汇率标示
		//组合汇率
		double portRate = this.getSettingOper().getCuryRate(
					dDate, "", "",
					"",//汇率来源
					"",//汇率来源字段
					"",
					portCode,
					YssOperCons.YSS_RATE_PORT);
		rate = YssD.div(baseRate, portRate);
		return rate;
	}

	/**
     * 判断是否当月的月末最后一个工作日
     * add by wuweiqi 20110110 两费计算 QDV4工银2010年11月1日01_A  
     * @param sPerExpCode String
     * @param dDate Date
     * @return double
     * @throws YssException
     */
    private boolean isMonthLastWorkDay(String IVPayCatCode,java.util.Date tradeDate ,String PortCode
                               ) throws
        YssException {
        String strSql = "";
        boolean dResult =false;
        ResultSet rs = null;
        String fHolidaycode="";
        java.util.Date settleDate=null;
        int days=0;
        BaseOperDeal deal = new BaseOperDeal();
        deal.setYssPub(pub);
        java.util.Date thisDate = null;
        int nextMonth = 0;
        if (tradeDate == null) {
            throw new YssException("费用公式解析-无法获取日期!");
        }
        if (PortCode == null || PortCode.trim().length() == 0) {
            throw new YssException("费用公式解析-无法获取组合信息!");
        }
        try {
            strSql = "select FholidaysCode from " +
                pub.yssGetTableName("Tb_Para_InvestPay") +
                " where FCheckState = 1 and FportCode = " +
                dbl.sqlString(PortCode)+
                " and Fivpaycatcode=" +
                dbl.sqlString(IVPayCatCode)+
                " and FStartdate = (select max(FStartdate) as FStartdate from " +
                pub.yssGetTableName("Tb_Para_InvestPay") +
               " where FCheckState = 1 " +
               " and FPortCode = " +
                dbl.sqlString(PortCode)+
               " and Fivpaycatcode=" +
               dbl.sqlString(IVPayCatCode)+")";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
            	fHolidaycode =rs.getString("FholidaysCode");
            } 
            if(fHolidaycode==null){
            	 throw new YssException("节假日未设定！");
            }
            settleDate=deal.getWorkDay(fHolidaycode, tradeDate,0);
            days=YssFun.dateDiff(tradeDate, settleDate);
            if(days == 0){
            	thisDate = YssFun.addDay(tradeDate, 1);
            	settleDate=deal.getWorkDay(fHolidaycode, thisDate,0);
            	nextMonth = YssFun.getMonth(YssFun.addMonth(tradeDate, 1));//下个月的月数
            	if(nextMonth==YssFun.getMonth(settleDate)){
            		dResult=true;
            	}
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("获取是否为当月月末最后一个工作日出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 判断是否为节假日
     * add by wuweiqi 20110110 两费计算  QDV4建信2010年12月17日01_A 
     * @param sPerExpCode String
     * @param dDate Date
     * @return double
     * @throws YssException
     */
    private boolean isHoliday(String IVPayCatCode ,String PortCode
                               ) throws
        YssException {
        String strSql = "";
        boolean dResult =false;
        ResultSet rs = null;
        String fHolidaycode="";
        java.util.Date settleDate=null;
        int days=0;
        BaseOperDeal deal = new BaseOperDeal();
        deal.setYssPub(pub);
        try {
        	if(this.investpay.getHolidaysCode().length() == 0){      		      	
	            strSql = "select FholidaysCode from " +
	                pub.yssGetTableName("Tb_Para_InvestPay") +
	                " where FCheckState = 1 and FportCode = " +
	                dbl.sqlString(PortCode)+
	                " and Fivpaycatcode=" +
	                dbl.sqlString(IVPayCatCode)+
	                " and FStartdate = (select max(FStartdate) as FStartdate from " +
	                 pub.yssGetTableName("Tb_Para_InvestPay") +
	                " where FCheckState = 1 " +
	                " and FPortCode = " +
	                 dbl.sqlString(PortCode)+
	                " and Fivpaycatcode=" +
	                dbl.sqlString(IVPayCatCode)+")";
	            rs = dbl.openResultSet(strSql);
	            if (rs.next()) {
	            	fHolidaycode =rs.getString("FholidaysCode");
	            } 
	            if(fHolidaycode==null){
	            	 throw new YssException("节假日未设定！");
	            }
        	}else{
        		fHolidaycode = this.investpay.getHolidaysCode();
        	}
            settleDate=deal.getWorkDay(fHolidaycode, tradeDate,0);
            days=YssFun.dateDiff(tradeDate, settleDate);
            if(days!=0){
            	dResult=false;
            }
            else{
            	dResult=true;
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("判断是否为节假日出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 获取节假日后第一个工作日需要费用计提的天数
     * add by wuweiqi 20110115 QDV4建信2010年12月17日01_A 
     * @param sPerExpCode String
     * @param dDate Date
     * @return double
     * @throws YssException
     */
    private int getDays(String IVPayCatCode ,String PortCode
                               ) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        String fHolidaycode="";
        java.util.Date lastDate=null;
        int days=0;
        BaseOperDeal deal = new BaseOperDeal();
        deal.setYssPub(pub);
        try {
        	if(this.investpay.getHolidaysCode().length() == 0){
	            strSql = "select FholidaysCode from " +
	                pub.yssGetTableName("Tb_Para_InvestPay") +
	                " where FCheckState = 1 and FportCode = " +
	                dbl.sqlString(PortCode)+
	                " and Fivpaycatcode=" +
	                dbl.sqlString(IVPayCatCode)+
	                " and FStartdate = (select max(FStartdate) as FStartdate from " +
	                pub.yssGetTableName("Tb_Para_InvestPay") +
	               " where FCheckState = 1 " +
	               " and FPortCode = " +
	                dbl.sqlString(PortCode)+
	               " and Fivpaycatcode=" +
	               dbl.sqlString(IVPayCatCode)+")";
	            rs = dbl.openResultSet(strSql);
	            if (rs.next()) {
	            	fHolidaycode =rs.getString("FholidaysCode");
	            }
	            if(fHolidaycode==null){
	            	 throw new YssException("节假日未设定！");
	            }
        	}else {
        		fHolidaycode = this.investpay.getHolidaysCode();
        	}
            lastDate = YssFun.addDay(tradeDate, -1);
            if(deal.isWorkDay(fHolidaycode, lastDate,0)){
            	days = 0;
            }else{
            	do{
            		days++;
            		lastDate = YssFun.addDay(lastDate, -1);
            	}
            	while(!deal.isWorkDay(fHolidaycode, lastDate,0));
            }
            days++;
            return days;
        } catch (Exception e) {
            throw new YssException("获取节假日后未计提天数出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 获得当日资产净值
     * add by wuweiqi 20110115  QDV4工银2010年11月1日01_A  
     * @param sPerExpCode String
     * @param dDate Date
     * @return double
     * @throws YssException
     */
    public double getDayNet(java.util.Date tradeDate,String PortCode
                               ) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        double portnetvalue=0;
        try {
            strSql = "select distinct Fportnetvalue from " +
                pub.yssGetTableName("Tb_data_netvalue") +
                " where FCheckState = 1 and FType = '01' and FportCode = " +
                dbl.sqlString(PortCode)+
                " and FNavDate=" +
                dbl.sqlDate(tradeDate);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
            	portnetvalue =rs.getDouble("Fportnetvalue");
            } 
            return portnetvalue;
        } catch (Exception e) {
            throw new YssException("获取当日资产净值出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * add by songjie 2012.08.17
     * STORY #2886 QDV4中行2012年8月17日01_A
     * @param tradeDate
     * @param PortCode
     * @return
     * @throws YssException
     */
    public double getTAAmount(java.util.Date tradeDate,String PortCode) throws YssException{
        String strSql = "";
        ResultSet rs = null;
        double taAmount =0;
        int payOrgin = 0;//默认：按昨日净值 计提管理费
        try {
        	strSql = " select * from " + pub.yssGetTableName("Tb_Para_Investpay")+
        	" where FStartDate = (select max(FStartDate) from " + 
        	pub.yssGetTableName("Tb_Para_Investpay") + " where FStartDate <= " + 
        	//--- edit by songjie 2013.07.02 BUG 8452 QDV4中行2013年6月26日01_B start---//
        	//运营费用设置代码由写死的IV001 改为 传参的运营费用设置代码
        	dbl.sqlDate(tradeDate) + " and FIVPayCatCode = " + dbl.sqlString(this.IVPayCatCode) + 
        	") and FIVPayCatCode = " + dbl.sqlString(this.IVPayCatCode) +
        	" and FPortCode = " + dbl.sqlString(PortCode);
        	//--- edit by songjie 2013.07.02 BUG 8452 QDV4中行2013年6月26日01_B end---//
        	rs = dbl.openResultSet(strSql);
        	while(rs.next()){
        		payOrgin = rs.getInt("FPayOrigin");
        	}
        	
        	dbl.closeResultSetFinal(rs);
        	
        	//若为按照 当日实收资本计提两费 则 获取 已选组合、估值日 的 TA库存数量
        	if(payOrgin == 3){
            	strSql = " select sum(FStorageAmount) as FStorageAmount from " + pub.yssGetTableName("Tb_Stock_TA") +
            	" where FCheckState = 1 and FPortCode = " + dbl.sqlString(PortCode) + 
            	"and FStorageDate = " + dbl.sqlDate(tradeDate);
            	
            	rs = dbl.openResultSet(strSql);
            	while(rs.next()){
            		taAmount = rs.getDouble("FStorageAmount");
            	}
        	}else{
        		taAmount = 0;
        	}

            return taAmount;
        } catch (Exception e) {
            throw new YssException("获取TA库存数量出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * 获取费用的期间
     * @param PeriodCode String
     * @return double
     * @throws YssException
     */
    private double getPeriod(String PeriodCode) throws YssException {
        if (PeriodCode.trim().length() == 0) {
            throw new YssException("解析公式-无法获取期间设置");
        }
        PeriodBean Period = new PeriodBean();
        Period.setYssPub(pub);
        Period.setPeriodCode(PeriodCode);
        Period.setYssPub(pub);
        Period.getSetting();
        return Period.getDayOfYear();
    }

    /**
     * 获取费率
     * @param sPerExpCode String
     * @param dDate Date
     * @return double
     * @throws YssException
     */
    private double getPerValue(String sPerExpCode,
                               java.util.Date dDate) throws
        YssException {
        String strSql = "";
        double dResult = 0;
        ResultSet rs = null;
        int perType = 0;
        HashMap hmPerRela = null;
        PerformulaRelaBean performula = null;
        try {
            strSql = "select FPerType from " +
                pub.yssGetTableName("tb_para_performula") +
                " where FCheckState = 1 and FFormulaCode = " +
                dbl.sqlString(sPerExpCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                perType = rs.getInt("FPerType"); //0绝对值，1相对值
            } else {
                //----------------------彭鹏 2008.2.20 BUG0000049----------------//
                if (sPerExpCode.equalsIgnoreCase("null")) {
                    throw new YssException("请检查比率公式是否未设置或已经审核");
                }
                //--------------------------------------------------------------//
                throw new YssException("请检查比率公式" + sPerExpCode + "是否维护比率类型并已经审核");
            }
            dbl.closeResultSetFinal(rs);
            hmPerRela = this.getSettingOper().getPerformulaRela(perType,
                sPerExpCode, 99999999999.0, dDate);
            if (perType == 0) { //绝对值
                if (hmPerRela == null) {
                    throw new YssException("解析公式-获取比例公式出错!");
                }
                performula = (PerformulaRelaBean) hmPerRela.get(new Integer(1));
                dResult = performula.getPerValue();
            } else {
                throw new YssException("解析公式-比例公式不是绝对值!");
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("获取费用比率出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    
    /**
     * add by huangqirong 2013-01-08 story #3400 获取比率公式固定值
     * @param sPerExpCode String
     * @param dDate Date
     * @return double
     * @throws YssException
     */
    private double getFixValue(String sPerExpCode,
                               java.util.Date dDate) throws
        YssException {
        String strSql = "";
        double dResult = 0;
        ResultSet rs = null;
        int perType = 0;
        HashMap hmPerRela = null;
        PerformulaRelaBean performula = null;
        try {
            strSql = "select FPerType from " +
                pub.yssGetTableName("tb_para_performula") +
                " where FCheckState = 1 and FFormulaCode = " +
                dbl.sqlString(sPerExpCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                perType = rs.getInt("FPerType"); //0绝对值，1相对值
            } else {                
                if (sPerExpCode.equalsIgnoreCase("null")) {
                    throw new YssException("请检查比率公式是否未设置或已经审核");
                }
                throw new YssException("请检查比率公式" + sPerExpCode + "是否维护比率类型并已经审核");
            }
            dbl.closeResultSetFinal(rs);
            hmPerRela = this.getSettingOper().getPerformulaRela(perType,
                sPerExpCode, 99999999999.0, dDate);
            if (perType == 0) { //绝对值
                if (hmPerRela == null) {
                    throw new YssException("解析公式-获取比例公式出错!");
                }
                performula = (PerformulaRelaBean) hmPerRela.get(new Integer(1));
                dResult = performula.getFixValue(); //去固定值
            } else {
                throw new YssException("解析公式-比例公式不是绝对值!");
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("获取费用比率出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 判断上月是否为基金成立的月份
     * @param dDate Date
     * @param sPortCode String
     * @return boolean
     * @throws YssException
     */
    private boolean isEstablishMonth(java.util.Date dDate, String sPortCode) throws
        YssException {
        if (dDate == null) {
            throw new YssException("费用公式解析-无法获取日期!");
        }
        if (sPortCode == null || sPortCode.trim().length() == 0) {
            throw new YssException("费用公式解析-无法获取组合信息!");
        }
        boolean isEstablishMonth = false;
        java.util.Date inceptionDate = null;
        int inceptionMonth = 0;
        int currentMonth = 0;
        //------------------------------------------
        PortfolioBean port = new PortfolioBean();
        port.setYssPub(pub);
        port.setPortCode(sPortCode);
        port.getSetting();
        //------------------------------------------
        inceptionDate = port.getInceptionDate();
        inceptionMonth = YssFun.getMonth(YssFun.addMonth(inceptionDate, 1)); //代码调整，将基金成立日的月份加一。sj modified 20090115.
        currentMonth = YssFun.getMonth(dDate);
        if (inceptionMonth == currentMonth) {
            isEstablishMonth = true;
        } else {
            isEstablishMonth = false;
        }
        return isEstablishMonth;
    }

    /**
     * 获取上月天数
     * @param dDate Date
     * @return int
     * @throws YssException
     */
    private double getLastMonthDays(java.util.Date dDate) throws YssException {
        double lastMonthDays = 0;
        Calendar calendar = new GregorianCalendar(YssFun.getYear(YssFun.addMonth(
            dDate, -1)), YssFun.getMonth(YssFun.addMonth(
                dDate, -1)), 0); //调整为上月的月份 sj modified 20090115
        lastMonthDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return lastMonthDays;
    }

    /**
     * 获取上月的实际天数
     * @param dDate Date
     * @param sPortCode String
     * @return int
     * @throws YssException
     */
    private double getLastMonthFactDays(java.util.Date dDate, String sPortCode) throws
        YssException {
        if (dDate == null) {
            throw new YssException("费用公式解析-无法获取日期!");
        }
        if (sPortCode == null || sPortCode.trim().length() == 0) {
            throw new YssException("费用公式解析-无法获取组合信息!");
        }
        double lastMonthFactDays = 0;
        java.util.Date inceptionDate = null;
        java.util.Date lastMonthMaxDate = null;
        String lastDateStr = "";
        java.util.Date lastDate = null;
        //------------------------------------------
        PortfolioBean port = new PortfolioBean();
        port.setYssPub(pub);
        port.setPortCode(sPortCode);
        port.getSetting();
        //------------------------------------------
        inceptionDate = port.getInceptionDate();
        if (inceptionDate == null || YssFun.formatDate(inceptionDate, "yyyy-MM-dd").equalsIgnoreCase("9998-12-31")) {
            throw new YssException("费用公式解析-无法获取正确的基金启用日期!");
        }
        lastDateStr = String.valueOf(YssFun.getYear(dDate)) + "-" +
            (YssFun.getMonth(dDate) > 9 ? String.valueOf(YssFun.getMonth(dDate)) :
             "0" + String.valueOf(YssFun.getMonth(dDate))) + "-01";
        lastDate = YssFun.addDay(YssFun.toDate(lastDateStr), -1);
        lastMonthFactDays = YssFun.dateDiff(inceptionDate, lastDate);
        return lastMonthFactDays + 1; //实际天数应该在原有的基础上再加上一天。sj modified 20090115
    }

    /**
     * 获取上月末的基金资产净值
     * @param dDate Date
     * @param sPortCode String
     * @return double
     * @throws YssException
     */
    private double getLastMonthNet(java.util.Date dDate, String sPortCode
                                   , boolean isPreLastMonth //MS00270 QDV4赢时胜（上海）2009年2月25日01_B
        ) throws
        YssException {
        double lastMonthNet = 0.0;
        String lastDateStr = "";
        String lastIniDateStr = "";
        String sqlStr = "";
        ResultSet rs = null;
        java.util.Date lastDate = null;
        java.util.Date lastIniDate = null;
        java.util.Date inceptionDate = null;
        int lastMonth = 0;
        int lastYear = 0;
        if (dDate == null) {
            throw new YssException("费用公式解析-无法获取日期!");
        }
        if (sPortCode == null || sPortCode.trim().length() == 0) {
            throw new YssException("费用公式解析-无法获取组合信息!");
        }
        lastDateStr = String.valueOf(YssFun.getYear(dDate)) + "-" +
            (YssFun.getMonth(dDate) > 9 ? String.valueOf(YssFun.getMonth(dDate)) :
             "0" + String.valueOf(YssFun.getMonth(dDate))) + "-01";
        lastDate = YssFun.addDay(YssFun.toDate(lastDateStr), -1);
        lastMonth = YssFun.getMonth(lastDate);
        lastYear = YssFun.getYear(lastDate);
        lastIniDateStr = String.valueOf(lastYear) + "-" +
            (lastMonth > 9 ? String.valueOf(lastMonth) :
             "0" + String.valueOf(lastMonth)) + "-01";
        lastIniDate = YssFun.toDate(lastIniDateStr);
        //------------------------------------------
        PortfolioBean port = new PortfolioBean();
        port.setYssPub(pub);
        port.setPortCode(sPortCode);
        port.getSetting();
        //------------------------------------------
        inceptionDate = port.getInceptionDate();
        if (inceptionDate == null || YssFun.formatDate(inceptionDate, "yyyy-MM-dd").equalsIgnoreCase("9998-12-31")) {
            throw new YssException("费用公式解析-无法获取正确的基金启用日期!");
        }
        if (YssFun.dateDiff(inceptionDate, YssFun.toDate(lastDateStr)) < 0
            && isPreLastMonth) { //MS00270 QDV4赢时胜（上海）2009年2月25日01_B
            throw new YssException("费用公式解析-本月为基金启用月份，无需计提费用!");
        }
        sqlStr = "select * from " + pub.yssGetTableName("Tb_data_netvalue") +
            " where FType = '01' and FCheckState = 1 " +
            //" and FINVMGRCODE = ' ' " + 去除对投资经理的筛选。MS00257 QDV4博时2009年02月19日01_A  sj modifeid
            " and FPortCode = " +
            dbl.sqlString(sPortCode) +
            " and FNavDate = (select max(FNavDate) as FNavDate from " +
            pub.yssGetTableName("Tb_data_netvalue") +
            " where FCheckState = 1 " +
            //" and FINVMGRCODE = ' ' " + 去除对投资经理的筛选。MS00257 QDV4博时2009年02月19日01_A  sj modifeid
            " and FPortCode = " +
            dbl.sqlString(sPortCode) + " and FNavDate between " +
            dbl.sqlDate(lastIniDate) + " and " + dbl.sqlDate(lastDate) + ")";
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                lastMonthNet = rs.getDouble("FPortNetValue");
            } else if (isPreLastMonth) { //MS00270 QDV4赢时胜（上海）2009年2月25日01_B
                throw new YssException("费用公式解析-上月末无资产净值!!");
            }
        } catch (Exception ex) {
            throw new YssException("费用公式解析-获取上月末净值出错!!", ex); //将里层的信息向外抛出。sj modified 20090115
        } finally {
            dbl.closeResultSetFinal(rs); //MS00270 QDV4赢时胜（上海）2009年2月25日01_B
        }
        return lastMonthNet;
    }

    /**
     * 获取上上个月的净值数据
     * @param dDate Date
     * @param sPortCode String
     * @return double
     * @throws YssException
     * MS00257 QDV4博时2009年02月19日01_A  sj modifeid
     */
    private double getPreLastMonthNet(java.util.Date dDate, String sPortCode) throws YssException {
        double preLastNet = 0.0;
        String lastDateStr = "";
        java.util.Date lastDate = null;
        if (dDate == null) {
            throw new YssException("费用公式解析-无法获取日期!");
        }
        if (sPortCode == null || sPortCode.trim().length() == 0) {
            throw new YssException("费用公式解析-无法获取组合信息!");
        }
        lastDateStr = String.valueOf(YssFun.getYear(dDate)) + "-" +
            (YssFun.getMonth(dDate) > 9 ? String.valueOf(YssFun.getMonth(dDate)) :
             "0" + String.valueOf(YssFun.getMonth(dDate))) + "-01";
        lastDate = YssFun.addDay(YssFun.toDate(lastDateStr), -1); //获取上月的月初日期。
        preLastNet = getLastMonthNet(lastDate, sPortCode,
                                     false //MS00270 QDV4赢时胜（上海）2009年2月25日01_B
            ); //调用获取上个月净值数据的方法，传入的日期为上个月月初的日期，这样就能获取上上个月的净值数据。
        return preLastNet;
    }

    /**
     * 获取基金成立日的第一日的净值数据
     * @param dDate Date
     * @param sPortCode String
     * @return double
     * @throws YssException
     * MS00270 QDV4赢时胜（上海）2009年2月25日01_B
     */
    private double getLastFirstNet(java.util.Date dDate, String sPortCode) throws YssException {
        double lastFirstNet = 0.0;
        String lastDateStr = "";
        String lastIniDateStr = "";
        String sqlStr = "";
        ResultSet rs = null;
        java.util.Date lastDate = null;
        java.util.Date lastIniDate = null;
        java.util.Date inceptionDate = null;
        int lastMonth = 0;
        int lastYear = 0;
        if (dDate == null) {
            throw new YssException("费用公式解析-无法获取日期!");
        }
        if (sPortCode == null || sPortCode.trim().length() == 0) {
            throw new YssException("费用公式解析-无法获取组合信息!");
        }
        lastDateStr = String.valueOf(YssFun.getYear(dDate)) + "-" +
            (YssFun.getMonth(dDate) > 9 ? String.valueOf(YssFun.getMonth(dDate)) :
             "0" + String.valueOf(YssFun.getMonth(dDate))) + "-01";
        lastDate = YssFun.addDay(YssFun.toDate(lastDateStr), -1);
        lastMonth = YssFun.getMonth(lastDate);
        lastYear = YssFun.getYear(lastDate);
        lastIniDateStr = String.valueOf(lastYear) + "-" +
            (lastMonth > 9 ? String.valueOf(lastMonth) :
             "0" + String.valueOf(lastMonth)) + "-01";
        lastIniDate = YssFun.toDate(lastIniDateStr);
        sqlStr = "select * from " + pub.yssGetTableName("Tb_data_netvalue") +
            " where FType = '01' and FCheckState = 1 " +
            " and FPortCode = " +
            dbl.sqlString(sPortCode) +
            " and FNavDate = (select min(FNavDate) as FNavDate from " + //min用于获取最小日的净值数据
            pub.yssGetTableName("Tb_data_netvalue") +
            " where FCheckState = 1 " +
            " and FPortCode = " +
            dbl.sqlString(sPortCode) + " and FNavDate between " +
            dbl.sqlDate(lastIniDate) + " and " + dbl.sqlDate(lastDate) + ")";
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                lastFirstNet = rs.getDouble("FPortNetValue");
            }
        } catch (Exception ex) {
            throw new YssException("费用公式解析-获取上月初净值出错!!", ex); //将里层的信息向外抛出。
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return lastFirstNet;
    }
    
    private double getManageFee(String curyCode) throws YssException {    	
        ResultSet rs = null;
        double result = 0.0;
        String sqlStr = " select FManageFee from " + pub.yssGetTableName("tb_data_multiclassNet") +
                        " where FPortCode = " + dbl.sqlString(this.portCode) + " and FNavdate = " + dbl.sqlDate(this.tradeDate) +
                        " and FType = '01' and FCuryCode = " + dbl.sqlString(curyCode);
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
            	result = rs.getDouble("FManageFee");
            }
        } catch (Exception ex) {
            
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
    }
    
    private double getTrusteeFee(String curyCode) throws YssException {    	
    	ResultSet rs = null;
        double result = 0.0;
        String sqlStr = " select FTrusteeFee from " + pub.yssGetTableName("tb_data_multiclassNet") +
                        " where FPortCode = " + dbl.sqlString(this.portCode) + " and FNavdate = " + dbl.sqlDate(this.tradeDate) +
                        " and FType = '01' and FCuryCode = " + dbl.sqlString(curyCode);
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
            	result = rs.getDouble("FTrusteeFee");
            }
        } catch (Exception ex) {
		    //add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
            throw new YssException("获取费用数据报错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
    }
    
    private double getSTFee(String curyCode) throws YssException {    	
    	ResultSet rs = null;
        double result = 0.0;
        String sqlStr = " select FSTFee from " + pub.yssGetTableName("tb_data_multiclassNet") +
                        " where FPortCode = " + dbl.sqlString(this.portCode) + " and FNavdate = " + dbl.sqlDate(this.tradeDate) +
                        " and FType = '01' and FCuryCode = " + dbl.sqlString(curyCode);
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
            	result = rs.getDouble("FSTFee");
            }
        } catch (Exception ex) {
		    //add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
            throw new YssException("获取费用数据报错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
    }
    
	//add by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
    private double getFAFee(String curyCode) throws YssException {    	
    	ResultSet rs = null;
        double result = 0.0;
        String sqlStr = " select FFAFee from " + pub.yssGetTableName("tb_data_multiclassNet") +
                        " where FPortCode = " + dbl.sqlString(this.portCode) + " and FNavdate = " + dbl.sqlDate(this.tradeDate) +
                        " and FType = '01' and FCuryCode = " + dbl.sqlString(curyCode);
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
            	result = rs.getDouble("FFAFee");
            }
        } catch (Exception ex) {
            throw new YssException("获取费用数据报错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
    }
    

    /**
     * added by liubo.Story #2126
     * 获取前一日资产净值
     * @param dDate Date
     * @param sPortCode String
     * @param sIvCode String
     * @return double
     * @throws YssException
     *
     */
    private double getYesterdayNav(String sPortCode,String sIvCode,java.util.Date dDate) throws YssException
    {
    	try
    	{
    		String strSql = "";
    		ResultSet rs = null;
    		ResultSet rsValDay = null;			
    		String sAccrueTypeCode = "";			//计提方式
    		String sHolidaysCode = "";				//节假日群代码
    		java.util.Date dCurDate = null;			//实际取净值的日期
                		
    		//根据运营收支品种代码和组合代码获取出它的节假日和计提方式
    		//---------------------------------
    		strSql = "Select * from " + pub.yssGetTableName("Tb_Para_InvestPay") + " where FIVPayCatCode = " + dbl.sqlString(sIvCode) + " and FPortCode = " + dbl.sqlString(sPortCode);
    		
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
    			sAccrueTypeCode = (rs.getString("FAccrueType") == null ? "EveNDayNAV" : rs.getString("FAccrueType"));
    			sHolidaysCode = (rs.getString("FHolidaysCode") == null ? " " : rs.getString("FHolidaysCode"));
    		}
    		
    		dbl.closeResultSetFinal(rs);
    		//--------------end-------------------
    		
	        if (sAccrueTypeCode.equalsIgnoreCase("EveDayNAV")//按工作日资产净值计提
	        	|| sAccrueTypeCode.equalsIgnoreCase("EveNDayNAV")) { //按每日资产净值计提 MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A panjunfang add 20090715

	    	//若选择工作日资产净值
		    	if(sAccrueTypeCode.equalsIgnoreCase("EveDayNAV")){
	            		if(sHolidaysCode == null || sHolidaysCode.equals("")){ //modify by wangzuochun 2010.02.25  MS01002   收益计提-两费计提报错   QDV4赢时胜上海2010年02月25日01_B  
	            			dCurDate = YssFun.addDay(dDate, -1);
	            		}else{
	            			dCurDate = super.getSettingOper().getWorkDay(sHolidaysCode, dDate, -1);
	            		}
	        	}else{//若选择自然日资产净值
	        			dCurDate = YssFun.addDay(dDate, -1);
	        	}
		        
		    	return getDayNet(dCurDate,sPortCode);
	       
	        } else { //按估值日资产净值计提 
	                strSql = "select * from " + pub.yssGetTableName("Tb_Para_ValuationDay") +
	                    " where FPortCode = " + dbl.sqlString(sPortCode) +
	                    " and FDate < " + dbl.sqlDate(dDate) +
	                    " and FCheckState = 1 order by FDate desc";
	            rsValDay = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
	            if (rsValDay.next()) {
	                dCurDate = rsValDay.getDate("FDate"); //取得计提日对应的估值日
	            } else {
	                throw new YssException("请先设定估值日再进行两费计提！");
	            }
	            dbl.closeResultSetFinal(rsValDay);
	            
	            return getDayNet(dCurDate,sPortCode);
	        }
    	}
    	catch(Exception ye)
    	{
    		throw new YssException("获取昨日资产净值出错：" + ye);
    	}
    	
    }
    
    //added by liubo.Story #1434
    //判断计提日当天是否为当年的最后一天。若是，返回“1=1”，不是则返回“1=2”
    //========================
    private String getTheLastDayOfYear(java.util.Date dDate) throws YssException
    {
    	boolean bResult = false;
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(dDate);
    	String sReturn = "";
    	
    	if (cal.get(Calendar.MONTH) + 1 == 12 && cal.get(Calendar.DAY_OF_MONTH) == 31)
    	{
    		bResult = true;
    	}
    	else
    	{
    		bResult = false;
    	}
    	
    	sReturn = (bResult ? "1=1" : "1=2");
    	
    	return sReturn;
    }
    
    //added by liubo.Story #1434
    //获取应当计提的总金额。若ReallyLimit小于该金额则要用总金额-ReallyLimit进行轧差
    //若某只基金不是计提日当年成立，则直接用70000
    //若是集体日的当年成立，则要用70000*datediff(基金成立日期,计提日期)/当年天数  来获取实际的总金额
    //==============================
    private double getTargetLimit(String sPortCode,java.util.Date dDate,String sIvCode) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;
    	Calendar cal = Calendar.getInstance();
    	Calendar calCreated = Calendar.getInstance();
    	double dbPoor = 1;
    	
    	try
    	{
    		cal.setTime(dDate);
    		
    		strSql = "select * from "
				+ pub.yssGetTableName("TB_TA_TRADE")
				+ " where FCheckState = 1"
				+ " and FSellType = '00' "
				+ " and FPortCode = "
				+ dbl.sqlString(sPortCode);
    		
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
    			calCreated.setTime(rs.getDate("FTRADEDATE"));
    		}

    		double dbDateCount = (YssFun.isLeapYear(dDate) ? 366 : 365);

    		if (cal.get(Calendar.YEAR) == calCreated.get(Calendar.YEAR))
    		{
    			dbPoor = YssFun.dateDiff(calCreated.getTime(),cal.getTime());
    		}
    		else
    		{
    			dbPoor = dbDateCount;
    		}
    		
        	return YssD.div(dbPoor, dbDateCount);
    		
    	}
    	catch(Exception ye)
    	{
    		throw new YssException("获取指数维护费目标金额出错：" + ye.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    }

    //added by liubo.Story #1434
    //获取计提日当年的年天数
    //==============================
    private double getDaysOfYear(java.util.Date dDate)
    {
    	return (YssFun.isLeapYear(dDate) ? 366 : 365);
    }
    
    //20121106 added by liubo.Story #2997
    //获取计提日当月的天数
    //==============================
    private double getDaysOfMonth(java.util.Date dDate) throws YssException
    {
    	String strDateFmt = YssFun.formatDate(dDate);
    	
    	try
    	{
	    	String sYear = strDateFmt.split("-")[0];
	    	String sMonth = strDateFmt.split("-")[1];
	    	
	    	return YssFun.getMonthLastDay(Integer.valueOf(sYear),Integer.valueOf(sMonth));
    	}
    	catch(Exception ye)
    	{
    		throw new YssException();
    	}
    }
    
    //20121122 added by yeshenghong.Story #3264
    //获取分级基金C类销售服务费
    //==============================
    private double getCServiceShare(String classFundCode,String datePre) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;
    	double dbTotal = 0;
    	int addDays = 0;
    	try
    	{
    		if(datePre.startsWith("!"))
    		{//表示负值
    			addDays = (-1) * Integer.parseInt(datePre.substring(1, datePre.length()));
    		}else
    		{
    			addDays = Integer.parseInt(datePre);
    		}
    		strSql = "Select FCLASSNETVALUE from " + pub.yssGetTableName("tb_data_multiclassnet") +" where FNAVDATE = " + dbl.sqlDate(YssFun.addDay(tradeDate, addDays)) +
			 " and FCURYCODE = " + dbl.sqlString(classFundCode) + " and FType = '01' " ;
	
    		rs = dbl.queryByPreparedStatement(strSql);
	
			while(rs.next())
			{
				dbTotal = rs.getDouble("FCLASSNETVALUE");
			}
	
			return dbTotal;
	    	
    	}
    	catch(Exception ye)
    	{
    		throw new YssException();
    	}
    }

    /**
     * add by huangqirong 2012-01-11 story #3400 
     * 获取前一天的运营费用余额
     * */
    private double getYestDayInvest(String sPortCode, java.util.Date dDate, String sIvCode) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;    	
    	double dbTotal = 0;
    	try
    	{
    		strSql = "Select sum(FBal) as Total from " + pub.yssGetTableName("Tb_Stock_Invest") +" where FSTORAGEDATE = " + 
    		dbl.sqlDate(YssFun.addDay(dDate, -1)) +
    				 " and FYearMonth not like '____00' and FIVPAYCATCODE = " + dbl.sqlString(sIvCode);
    		
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
    			dbTotal = rs.getDouble("Total");
    		}    		
    		return dbTotal;
    		
    	}
    	catch(Exception e)
    	{
    		throw new YssException(e.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    }
    
    //added by liubo.Story #1434
    //获取实际进行计提的运营收支品种的当年实际计提总总金额，公式为计提前一日运营库存余额+计提日当日实际计提金额
    //===================================
    private double getReallyLimit(String sPortCode,java.util.Date dDate,String sIvCode) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;
    	Calendar cal = Calendar.getInstance();
    	Calendar calCreated = Calendar.getInstance();
    	double dbTotal = 0;
    	
    	
    	try
    	{
        	cal.setTime(dDate);
        	
        	calCreated.set(cal.get(Calendar.YEAR),11,31);
        	
    		strSql = "Select sum(FBal) as Total from " + pub.yssGetTableName("Tb_Stock_Invest") +" where FSTORAGEDATE = " + dbl.sqlDate(YssFun.addDay(calCreated.getTime(), -1)) +
    				 " and FIVPAYCATCODE = " + dbl.sqlString(sIvCode);
    		
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
    			dbTotal = rs.getDouble("Total");
    		}
    		
    		return dbTotal;
    		
    	}
    	catch(Exception e)
    	{
    		throw new YssException(e.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    }
    
    /**
     * 判断是否当月的月末最后一个工作日,重载原先的方法，主要变更返回值类型。为true时返回 1=1，为false时返回 1=2
     * 20121107 added by liubo.Story #2997
     * @param sPerExpCode String
     * @param dDate Date
     * @return double
     * @throws YssException
     */
    private String isMonthLastWorkDay(String IVPayCatCode,java.util.Date tradeDate ,String PortCode,Boolean bTemp) throws
        YssException {
        String strSql = "";
        boolean dResult =false;
        String strReturn = "1=2";
        ResultSet rs = null;
        String fHolidaycode="";
        java.util.Date settleDate=null;
        int days=0;
        BaseOperDeal deal = new BaseOperDeal();
        deal.setYssPub(pub);
        java.util.Date thisDate = null;
        int nextMonth = 0;
        if (tradeDate == null) {
            throw new YssException("费用公式解析-无法获取日期!");
        }
        if (PortCode == null || PortCode.trim().length() == 0) {
            throw new YssException("费用公式解析-无法获取组合信息!");
        }
        try {
            strSql = "select FholidaysCode from " +
                pub.yssGetTableName("Tb_Para_InvestPay") +
                " where FCheckState = 1 and FportCode = " +
                dbl.sqlString(PortCode)+
                " and Fivpaycatcode=" +
                dbl.sqlString(IVPayCatCode)+
                " and FStartdate = (select max(FStartdate) as FStartdate from " +
                pub.yssGetTableName("Tb_Para_InvestPay") +
               " where FCheckState = 1 " +
               " and FPortCode = " +
                dbl.sqlString(PortCode)+
               " and Fivpaycatcode=" +
               dbl.sqlString(IVPayCatCode)+")";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
            	fHolidaycode =rs.getString("FholidaysCode");
            } 
            if(fHolidaycode==null){
            	 throw new YssException("节假日未设定！");
            }
            settleDate=deal.getWorkDay(fHolidaycode, tradeDate,0);
            days=YssFun.dateDiff(tradeDate, settleDate);
            if(days == 0){
            	thisDate = YssFun.addDay(tradeDate, 1);
            	settleDate=deal.getWorkDay(fHolidaycode, thisDate,0);
            	nextMonth = YssFun.getMonth(YssFun.addMonth(tradeDate, 1));//下个月的月数
            	if(nextMonth==YssFun.getMonth(settleDate)){
            		strReturn = "1=1";
            	}
            }
            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取是否为当月月末最后一个工作日出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
	private double getInvestValueByCode(String ivPayCatCode, String tsfTypeCode, String subTsfTypeCode) throws YssException{
    	ResultSet rs = null;
        double result = 0.0;
        String sqlStr = " select fportcode, fivpaycatcode, fmoney from " + pub.yssGetTableName("tb_data_InvestPayRec") +
                        " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portCode) + " and ftransdate = " 
                        + dbl.sqlDate(this.tradeDate) + " and FIVPayCatCode = " + dbl.sqlString(ivPayCatCode) + 
                        " and FTsfTypeCode = " + dbl.sqlString(tsfTypeCode) + " and FSubTsfTypeCode = " + dbl.sqlString(subTsfTypeCode);
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
            	result = rs.getDouble("fmoney");
            }
        } catch (Exception ex) {
            
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
    }
    
    private double getScaleByClsPortCode(String clsPortCode, String scaleType) throws YssException{
        double result = 0.0;
        try {
        	if("1".equalsIgnoreCase(scaleType)){
        		result = YssD.div(
        				          	getTaStock(clsPortCode,this.tradeDate), 
        				          	sumTaZSStock(this.tradeDate)
        				          );
        	}
        } catch (Exception ex) {
            
        } finally {
        }
        return result;
    }
    
    private double getTaStock(String clsPortCode, Date d) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	double result = 0.0;
    	sqlStr = " select * from " + pub.yssGetTableName("Tb_Stock_Ta") +
    			 " where FCheckState = 1 and FPortClsCode = " + dbl.sqlString(clsPortCode) + 
    			 " and FPortCode = " + dbl.sqlString(this.portCode) + " and FStorageDate = " + dbl.sqlDate(d);    	
		try{
			rs = dbl.queryByPreparedStatement(sqlStr);
			while(rs.next()){
				result = rs.getDouble("FfjzhzsStorageAmount");
				
        	}    					
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
        return result;
    }
    
    //获得各分级折算后的库存数量之和
    private double sumTaZSStock(Date d) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	double result = 0.0;
    	sqlStr = " select sum(FfjzhzsStorageAmount) as FfjzhzsStorageAmount from " + pub.yssGetTableName("Tb_Stock_Ta") +
    			 " where FCheckState = 1 " + " and FPortCode = " + dbl.sqlString(this.portCode) + " and FStorageDate = " + dbl.sqlDate(d);    	
		try{
			rs = dbl.queryByPreparedStatement(sqlStr);
			while(rs.next()){
				result = rs.getDouble("FfjzhzsStorageAmount");
        	}    					
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
        return result;
    }
    
    //获得目标ETF add by fangjiang 2012.05.02 stroy 2565
    private String getAimETF() throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	String result = "";
    	sqlStr = " select FAimETFCode from " + pub.yssGetTableName("TB_PARA_PORTFOLIO") +
    			 " where FCheckState = 1 " + " and FPortCode = " + dbl.sqlString(this.portCode);    	
		try{
			rs = dbl.queryByPreparedStatement(sqlStr);
			while(rs.next()){
				result = rs.getString("FAimETFCode");
        	}    					
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
        return result;
    }
    
    //获得市值 add by fangjiang 2012.05.02 stroy 2565
    private double getMarketValue(Date d, String sDisDay, String aimETF) throws YssException{
    	String sqlStr = null;
    	ResultSet rs = null;
    	double result = 0.0;
    		
		String sAccrueTypeCode = "";			//计提方式
		String sHolidaysCode = "";				//节假日群代码
		java.util.Date dCurDate = null;			//实际取净值的日期
		
		int iDisDay = 0;
        if (sDisDay.startsWith("!")) {
            iDisDay = YssFun.toInt(sDisDay.substring(1)) * -1;
        } else {
            iDisDay = YssFun.toInt(sDisDay);
        }    
            		
		try{
			//根据运营收支品种代码和组合代码获取出它的节假日和计提方式
			sqlStr = " Select * from " + pub.yssGetTableName("Tb_Para_InvestPay") + " where FIVPayCatCode = " + 
			         dbl.sqlString(this.IVPayCatCode) + " and FPortCode = " + dbl.sqlString(this.portCode);		
			rs = dbl.queryByPreparedStatement(sqlStr);
			
			while(rs.next())
			{
				sAccrueTypeCode = (rs.getString("FAccrueType") == null ? "EveNDayNAV" : rs.getString("FAccrueType"));
				sHolidaysCode = (rs.getString("FHolidaysCode") == null ? " " : rs.getString("FHolidaysCode"));
			}		
			dbl.closeResultSetFinal(rs);
			//--------------end-------------------
			
	        if (sAccrueTypeCode.equalsIgnoreCase("EveDayNAV")//按工作日资产净值计提
	        	|| sAccrueTypeCode.equalsIgnoreCase("EveNDayNAV")) { //按自然日资产净值计提 
	        	
		    	if(sAccrueTypeCode.equalsIgnoreCase("EveDayNAV")){ //若选择工作日资产净值
	            		if(sHolidaysCode == null || sHolidaysCode.equals("")){ 
	            			dCurDate = YssFun.addDay(d, iDisDay);
	            		}else{
	            			dCurDate = super.getSettingOper().getWorkDay(sHolidaysCode, d, iDisDay);
	            		}
	        	}else{ //若选择自然日资产净值
	        			dCurDate = YssFun.addDay(d, iDisDay);
	        	}       
	        } else { //按估值日资产净值计提 
	        	sqlStr = " select * from " + pub.yssGetTableName("Tb_Para_ValuationDay") +
	                     " where FPortCode = " + dbl.sqlString(this.portCode) +
	                     " and FDate < " + dbl.sqlDate(YssFun.addDay(d, iDisDay + 1)) +
	                     " and FCheckState = 1 order by FDate desc";
	            rs = dbl.queryByPreparedStatement(sqlStr); 
	            if (rs.next()) {
	                dCurDate = rs.getDate("FDate"); //取得计提日对应的估值日
	            } else {
	                throw new YssException("请先设定估值日再进行两费计提！");
	            }
	            dbl.closeResultSetFinal(rs);
	            
	        }
	        
	        //modify by fangjiang 2013.05.23 story 3856
	    	sqlStr = " select FPortMarketValue from " + pub.yssGetTableName("Tb_Data_NavData") +
	    			 " where FkeyCode like '" + aimETF + "%' and fretypecode = 'Security' and fdetail = 0 " + 
	    			 " and FPortCode = " + dbl.sqlString(this.portCode) + " and fnavdate = " + dbl.sqlDate(dCurDate);    	
	    	//end by fangjiang 2013.05.23 story 3856
	    	
			rs = dbl.queryByPreparedStatement(sqlStr);
			while(rs.next()){
				//此处要取本位币市值
				result = rs.getDouble("FPortMarketValue");
        	}    					
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
        return result;
    }
    
    /**
     * add by songjie 2013.05.15 
     * STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001  
     * 检查比率公式是否未设置或已经审核
     * @throws YssException
     */
    private void judgeSetPerformula() throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	try{
    		strSql = "select FPerType from " +
            pub.yssGetTableName("tb_para_performula") +
            " where FCheckState = 1 and FFormulaCode = " +
            dbl.sqlString(investpay.getPerExpCode());
    		
	        rs = dbl.queryByPreparedStatement(strSql);
	        
	        if (!rs.next())
	        {
	            if (investpay.getPerExpCode() == null || investpay.getPerExpCode().equalsIgnoreCase("null")) 
	            {
	                throw new YssException("请检查比率公式是否未设置或已经审核");
	            }
	            throw new YssException("请检查比率公式" + investpay.getPerExpCode() + "是否维护比率类型并已经审核");
	        } 
    	}catch(Exception e){
    		throw new YssException("检查比率公式出错",e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /**
     * add by songjie 2013.05.15 
     * STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001
     * 功能描述：
     * 获取比率公式中设置的最大日期和最小日期，
     * 最大日期为结束日期，最小日期为起始日期，
	 * 其中以起始日期的数据为准，获取最低限额数据和费率
	 * 
     * @param dStartDate 本期间开始日
     * @param dEndDate 本期间结束日
     * @param dPerRatio 费率
     * @param dLeastValue 最低限额
     * @param dCurDate 业务日期
     * @throws YssException
     */
	private HashMap getPerformulaInfo(java.util.Date dCurDate) throws YssException {
		java.util.Date dStartDate = null;//本期间开始日
		java.util.Date dEndDate = null;//本期间结束日
		double dPerRatio = 0;//费率
		double dLeastValue = 0;//最低限额
		ResultSet rs = null;
		String strSql = "";
		HashMap hmInfo = new HashMap();
		try {
			strSql = buildPerformulaSql(dCurDate);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if (rs.getInt("FType") == 1) {
					dStartDate = rs.getDate("FRangeDate");
					dPerRatio = rs.getDouble("FPerValue");
					dLeastValue = rs.getDouble("FLeastValue");
				} else if (rs.getInt("FType") == 2) {
					dEndDate = rs.getDate("FRangeDate");
				}
			}
			
			if(dStartDate != null){
				hmInfo.put("StartDate", dStartDate);
			}
			if(dEndDate != null){
				hmInfo.put("EndDate", dEndDate);
			}
			
			hmInfo.put("Ratio", dPerRatio);
			hmInfo.put("LeastValue", dLeastValue);
			
			return hmInfo;
		} catch (Exception e) {
			throw new YssException("获取比率公式设置出错",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2013.06.08
	 * STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001
	 * 拼接获取比率公式数据的sql
	 * 
	 * 明细sql逻辑：
	 * 获取 相关运营费用设置对应的比率公式中 
	 * 小于等于业务日期的最大启用日期对应的 启用日期、利率、最低金额
	 * 以及
	 * 大于          业务日期的最小启用日期对应的 启用日期、利率、最低金额
	 * @param dCurDate 业务日期
	 * @return 
	 * @throws YssException
	 */
	private String buildPerformulaSql(java.util.Date dCurDate) throws YssException{
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append(" select 1 as FType, a.FRangeDate, a.FPerValue, a.FLeastValue ")
			     .append(" from ")
			     .append(pub.yssGetTableName("Tb_Para_Performula_Rela")) 
			     .append(" a where a.FCheckState = 1 ")
			     .append(" and a.FFormulaCode = " + dbl.sqlString(investpay.getPerExpCode()))
			     .append(" and exists (select FRangeDate from (select Max(FRangeDate) as FRangeDate from ")
			     .append(pub.yssGetTableName("Tb_Para_Performula_Rela") + " where FCheckState = 1 and ")
				 .append(" FFormulaCode = " + dbl.sqlString(investpay.getPerExpCode())) 
				 .append(" and FRangeDate <= " + dbl.sqlDate(dCurDate) +") b where a.FRangeDate = b.FRangeDate) ")
				 .append(" union all ")
				 .append(" select 2 as FType, a.FRangeDate, a.FPerValue, a.FLeastValue ")
				 .append(" from ")
				 .append(pub.yssGetTableName("Tb_Para_Performula_Rela")) 
				 .append(" a where a.FCheckState = 1 ")
				 .append(" and a.FFormulaCode = " + dbl.sqlString(investpay.getPerExpCode())) 
				 .append(" and exists (select FRangeDate from (select Min(FRangeDate) as FRangeDate from ") 
				 .append(pub.yssGetTableName("Tb_Para_Performula_Rela") + " where FCheckState = 1 and ") 
				 .append(" FFormulaCode = " + dbl.sqlString(investpay.getPerExpCode())) 
				 .append(" and FRangeDate > " + dbl.sqlDate(dCurDate) +") b where a.FRangeDate = b.FRangeDate) ");
			
			return sbSql.toString();
		}catch(Exception e){
			throw new YssException("拼接获取比率公式数据的sql出错",e);
		}
	}
    
	/**
	 * add by songjie 2013.05.15 
	 * STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001
	 * 功能描述：
	 * 获取运营收支项目中现金账户的币种
	 * 若现金账户币种与最低限额币种一致，则汇率为1.0；
	 * 若不一致，需要获取一个最低限额币种转换为现金账户币种的汇率
	 * @param dCurDate 业务日期
	 * @return
	 * @throws YssException
	 */
	private double getMiddleRate(java.util.Date dCurDate)throws YssException{
		ResultSet rs = null;
		String strSql = "";
		double dRate = 0;
		double baseRate = 0;
		double portRate = 0;
		String sAccCury = "";
		try{
            strSql = " select FCURYCODE from " + pub.yssGetTableName("tb_para_cashaccount") + 
		 	" where FCASHACCCODE = " + dbl.sqlString(this.investpay.getCashAccCode());
            
            rs = dbl.queryByPreparedStatement(strSql);
            
            if (rs.next())
            {
            	sAccCury = rs.getString("FCURYCODE");
            }
            
            
            if (this.investpay.getLowerCurrencyCode() == null 
                || this.investpay.getLowerCurrencyCode().trim().equals("") 
                || this.investpay.getLowerCurrencyCode().trim().equals(sAccCury))
            {
            	dRate = 1.0;
            }
            else
            {
            	baseRate = this.getSettingOper().getCuryRate(//基础汇率
            				dCurDate,//汇率日期
        					"",//基础汇率来源
        					"",//基础汇率来源字段
        					"",//组合来源
        					"",//组合来源字段
        					this.investpay.getLowerCurrencyCode(),//币种(原币,本币)
        					this.portCode,//组合
        					YssOperCons.YSS_RATE_BASE);//汇率标示
            	
        		
            	portRate = this.getSettingOper().getCuryRate(//组合汇率
            				dCurDate,//汇率日期
        					"",//基础汇率来源
        					"",//基础汇率来源字段
        					"",//组合来源
        					"",//组合来源字段
        					sAccCury,//币种(原币,本币)
        					this.portCode,//组合
        					YssOperCons.YSS_RATE_PORT);//汇率标示
            		
        		dRate = YssD.div(baseRate, portRate);
            	
            }
			return dRate;
		} catch (Exception e) {
			throw new YssException("获取中间汇率出错",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	 
	 /**
	  * add by songjie 2013.06.08 
	  * STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001
	  * 根据运营费用设置 的 期间设置 获取 当年天数
	  * @param dCurDate
	  * @return
	  * @throws YssException
	  */
	private int getFinalDaysOfYear(java.util.Date dCurDate) throws YssException {
		int days = 0;
		try {
			PeriodBean Period = new PeriodBean();
			Period.setYssPub(pub);
			Period.setPeriodCode(investpay.getPeriodCode());
			Period.getSetting();

			if (Period.getPeriodType() == 1) {// 如果期间类型 = 实际天数
				if (YssFun.isLeapYear(dCurDate)) {
					days = 366;
				} else {
					days = 365;
				}
			} else {// 如果期间类型 = 固定天数
				days = Period.getDayOfYear();// 获取已设置的天数信息
			}

			return days;
		} catch (Exception e) {
			throw new YssException("获取年天数", e);
		}
	}
	 
    /**
     * 计算纳斯达克指数使用费。
     * *********************************************************************************************************
     * 20130419 added by liubo.Story #3853
     * Story #3853(对应 模式1) 计算规则为：
     * 每日计提公式为：MAX[ MAX(A,B) – 前一日MAX(A,B) , 0] 。
   	 * 设定期间段总天数为X，期间段起始日期为T，第N日为T+N-1日，计提比率为V，期间最低应付费用为Z（可选币种），第N天的资产净值为NAV(T+N)  。
   	 * A为T+N日按费率计提的总费用 =round( NAV(T-1) *V/X ,2) + round(NAV(T) *V/X,2) +………round( NAV(T+N-1)*V/X ,2)。
   	 * 此处的中间过程是否保留小数位以及具体的小数位数由iDigits参数控制
   	 * B为T+N日按最低限额计算的最低总费用=round(Z*X/(N+1),2)，如果最低限额币种为外币，则按T+N日的汇率换算。
   	 *
   	 * 此方法用于计算每一天的 MAX(A,B)的数据，在另外一个方法中在做MAX[ MAX(A,B) – 前一日MAX(A,B) , 0]的计算
   	 * iFlag参数表示计算类型。为0表示计算计提日期当天的数据；为1表示计算计提日期前一日的数据
   	 * 
   	 * *********************************************************************************************************
   	 * 
   	 * add by songjie 2013.06.08 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001
   	 * STORY #3744(对应 模式2) 计算规则为：
   	 * 	 * 指数使用费 = [计提日]对应的MAX(A,B) – [计提日-1]对应的MAX(A1,B1)；
	 *
	 * A = ([本期间起始日]对应的资产净值 * 指数年费率 / 当年天数 +
	 * [本期间起始日+1]对应的资产净值* 指数年费率 / 当年天数 + 
	 * [本期间起始日+2]对应的资产净值* 指数年费率 / 当年天数 +
	 * ……+
	 * [计提日]对应的资产净值* 指数年费率 / 当年天数)；
	 * 
	 * B = 本期间的最低限额 * 计提日的汇率 * 从期间起始日到计提日的总天数 / 本期间总天数。
	 * 
	 * A1 = ([本期间起始日]对应的资产净值* 指数年费率 / 当年天数 + 
	 * [本期间起始日+1]对应的资产净值* 指数年费率 / 当年天数 +
	 * [本期间起始日+2]对应的资产净值* 指数年费率 / 当年天数 +
	 * ……+
	 * [计提日-1]对应的资产净值* 指数年费率 / 当年天数)；
	 * 
	 * B1 = 本期间的最低限额 * [计提日-1]的汇率 * 从期间起始日到[计提日-1]的总天数 / 本期间总天数。
	 * 
	 * *********************************************************************************************************
	 * 
     * @param mode 模式    
     * 模式1 对应需求  Story #3853 算法  
     * 模式2 对应需求  STORY #3744 算法
     * 
	 * @param dCurDate 业务日期
	 * @param iDigits 根据最低限额计算出的指数使用费的保留位数
	 * @param iFlag 延迟天数
	 * 
     * @throws YssException
     */
    private double calcNasdaqZS(java.util.Date dCurDate,int iDigits,int iFlag, int mode) throws YssException
    {
    	double dReturn = 0;
    	double dPerRatio = 0.0;				//费率
    	double dLeastValue = 0.0;			//最低限额
    	java.util.Date dStartDate = null;	//期间起始日期
    	java.util.Date dEndDate = null;		//期间结束日期
    	double dCurrentValue = 0.0;			//T+N日按费率计提的实际总费用
    	double dMinValue = 0.0;				//T+N日最低限额计算的最低总费用
    	int iDateDiff = 0;					//该期间的总天数
    	int iDateIntervel = 0;				//T+N的N，实际也就是起始日期到计提日期之间间隔的天数
    	double dRate = 1.0;					//汇率
    	HashMap hmInfo = null;
    	//--- add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 start---//
    	int days = 0;                       //期间天数
    	//--- add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 end---//
    	try
    	{
    		//首先判断设置了该算法公式的运营收支项目，是否有设置比率公式
    		//================================
    		
    		//--- add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 start---//
    		judgeSetPerformula();//检查比率公式是否未设置或已经审核
    		//--- add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 end---//
    		
	        //获取比率公式中设置的最大日期和最小日期，最大日期为结束日期，最小日期为起始日期
	        //其中以起始日期的数据为准，获取最低限额数据和费率
	        //============================
    		
            //--- add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 start---//
            //获取比率公式中设置的最大日期和最小日期，最大日期为结束日期，最小日期为起始日期
    		//其中以起始日期的数据为准，获取最低限额数据和费率
    		hmInfo = getPerformulaInfo(dCurDate);
    		if(hmInfo.get("StartDate") != null){
    			dStartDate = (Date)hmInfo.get("StartDate");
    		}
    		if(hmInfo.get("EndDate") != null){
    			dEndDate = (Date)hmInfo.get("EndDate");
    		}
    		if(hmInfo.get("Ratio") != null){
    			dPerRatio = ((Double)hmInfo.get("Ratio")).doubleValue();
    		}
    		if(hmInfo.get("LeastValue") != null){
    			dLeastValue = ((Double)hmInfo.get("LeastValue")).doubleValue();
    		}
            //--- add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 end---//
            
            if (dStartDate == null || dEndDate == null)
            {
            	return 0;
            }
            
            if (iFlag == 1)
            {
            	if (dCurDate.equals(dStartDate))
            	{
            		return 0;
            	}
            	else
            	{
            		dCurDate = YssFun.addDay(dCurDate, -1);
            	}
            }
            
            iDateDiff = YssFun.dateDiff(dStartDate,dEndDate);			//计算期间总天数。因为每个期间的结束日期实际也就是下一个期间的开始日期，所以天数会比计算出的少一天
            iDateIntervel = YssFun.dateDiff(dStartDate,dCurDate) + 1;	//获取T+N中的N，也就是起始日期到计提日期之间间隔的天数
            
            //--- edit by songjie 2013.06.08 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 start---//
            if(mode == 1){//添加模式判断
            //--- edit by songjie 2013.06.08 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 end---//
            	//计算T+N日按费率计提的实际总费用
            	dCurrentValue = getNetValueOfPeriod(YssFun.addDay(dStartDate, -1),YssFun.addDay(dCurDate, -1),this.portCode,dPerRatio,iDateDiff,iDigits);
            }
            //--- add by songjie 2013.06.08 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 start---//
            if(mode == 2){
	    	    days = getFinalDaysOfYear(dCurDate);
	    		
	    	    if(investpay.getPayOrigin() == 0){//若收支来源 = 昨日净值
	    	    //按费率计提的实际总费用  是 根据本期间起始日前一日的资产净值  到 业务日期前一日的资产净值 计算的总费用
		            dCurrentValue = getNetValueOfPeriod(YssFun.addDay(dStartDate, -1),
		            		YssFun.addDay(dCurDate, -1),this.portCode,dPerRatio,days,iDigits);
	    	    }else if(investpay.getPayOrigin() == 1){//若收支来源 = 当日净值
	    			//按费率计提的实际总费用  是 根据本期间起始日的资产净值  到 业务日期的资产净值 计算的总费用
		            dCurrentValue = getNetValueOfPeriod(dStartDate,dCurDate,
		            		this.portCode,dPerRatio,days,iDigits);
	    	    }
            }
            //--- add by songjie 2013.06.08 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 end---//
            
            //获取运营收支项目中现金账户的币种
            //若现金账户币种与最低限额币种一致，则汇率为1.0；若不一致，需要获取一个最低限额币种转换为现金账户币种的汇率
            //=====================================\
            
            //--- add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 start---//
            dRate = getMiddleRate(dCurDate);//获取最低限额币种转换为现金账户币种的汇率
            //--- add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 end---//
            
            //===================end==================
            
            //计算T+N日最低限额计算的最低总费用
            dMinValue = YssFun.roundIt(YssD.mul(YssD.div(YssD.mul(dLeastValue,dRate), iDateDiff),iDateIntervel), iDigits);
            
            if (dMinValue > dCurrentValue)
            {
            	dReturn = dMinValue;
            }
            else
            {
            	dReturn = dCurrentValue;
            }
            
        	return dReturn;
    	}
    	catch(Exception ye)
    	{
    		throw new YssException(ye.getMessage());
    	}
    }
    
    /**
     * 计算纳斯达克指数使用费。
     * 调用calcNasdaqZS方法，分别算出当日的数据和前一日的数据，然后做MAX[ MAX(A,B) – 前一日MAX(A,B) , 0]的计算
     * @param iDigits.小数位数
     * @return String.返回一个由计算出的结果数值所组成的SQL语句，由父类生成SQL函数，得到最后结果
     * @throws YssException
     */
    private String NasdaqZS(int iDigits) throws YssException
    {
    	double dReturn = 0;
    	
		//--- edit by songjie 2013.06.09 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 start---//
		//方法重构
    	dReturn = YssD.sub(calcNasdaqZS(this.tradeDate, iDigits,0,1), calcNasdaqZS(this.tradeDate,iDigits,1,1));
    	//--- edit by songjie 2013.06.09 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 end---//
    	if (dReturn > 0)
    	{
    		
    	}
    	else
    	{
    		dReturn = 0;
    	}
    	
    	return "select distinct " + dReturn + " as val into v_value from Tb_Sys_UserList;";
    }
    
    /**
     * add by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001
     * 计算纳斯达克指数使用费
     * @param iDigits 根据本期间的最低限额计算出的费用的保留位数
     * @return
     * @throws YssException
     */
    private String NasdaqIndexFee(int iDigits) throws YssException
    {
    	double dReturn = 0;
    	
    	dReturn = YssD.sub(calcNasdaqZS(this.tradeDate, iDigits,0, 2), calcNasdaqZS(this.tradeDate,iDigits,1, 2));
    	
    	if (dReturn < 0)
    	{
    		dReturn = 0;
    	}
    	
    	return "select distinct " + dReturn + " as val into v_value from Tb_Sys_UserList;";
    }
    
    //20130419 added by liubo.Story #3853
    //根据传入的起始日期、截止日期、组合代码、费率、期间总天数，获取该日期段内的纳斯达克指数使用费。并按iDigits参数来保留位数
    public double getNetValueOfPeriod(java.util.Date dStartdate,java.util.Date dEndDate,String PortCode,double sPerRatio,int iDayCount,int iDigits) throws YssException 
    {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
        	//--- edit by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 start---//
        	//修改sql逻辑，直接在sql中做求和处理，不用代码做求和计算
            strSql = "select sum(Round(FPortMarketValue * " + sPerRatio + "/" + iDayCount + "," + iDigits + ")) as FPortMarketValue from " +
            //--- edit by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 end---//    
            pub.yssGetTableName("tb_data_navdata") +
                " where FReTypeCode = 'Total' and FKeyCode = 'TotalValue' and FportCode = " +
                dbl.sqlString(PortCode)+
                " and FNavDate between " + dbl.sqlDate(dStartdate) + " and " + dbl.sqlDate(dEndDate);
            
            rs = dbl.queryByPreparedStatement(strSql);
            
            while (rs.next()) 
            {
            	//--- edit by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 start---//
            	//修改sql逻辑，直接在sql中做求和处理，不用代码做求和计算
            	dReturn = rs.getDouble("FPortMarketValue");
            	//--- edit by songjie 2013.05.15 STORY #3744 需求上海-[中银基金]QDIIV4.0[高]20130320001 end---//
            } 
            return dReturn;
        } catch (Exception e) {
            throw new YssException("获取某个时期的资产净值出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
}
