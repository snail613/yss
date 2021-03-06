package com.yss.main.operdeal.valuation;

import java.sql.ResultSet;
import java.util.*;

import com.yss.base.*;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.PerformulaRelaBean;
import com.yss.pojo.param.derivative.YssFwPrice;
import com.yss.util.*;

/**
 * <p>Title: LeverGradeFundCfg</p>
 *
 * <p>Description: 杠杆分级基金算法配置</p>
 *
 * add by yeshenghong 2013.06.09
 */
public class LeverGradeFundCfg
    extends BaseCalcFormula {
    public LeverGradeFundCfg() {
    }
    
//    private ForwardTradeBean aFwTrade;
//    private int curPeriod = 0; //当前的期限下标。
    private String formulaCode = "";//add by yeshenghong 20130509  story3759代码实现   公式代码
    private String portCode = "";
    private String portclscode = "";
    private Date ywDate = null;
    
    //20130619 added by liubo.Story #3759.调用类型
    //"normal"为正常调用，即在算法公式中使用。"rep"为查询报表时使用，主要是份额折算底稿会用到。默认normal
    //===============================
    private String sInvokeType = "normal"; 	
    //==============end=================
    
    public void init(String forCode,String portCode,String portClsCode,Date ywDate) {
//        aAlFwMarket = alFwMarket;
        this.formulaCode = forCode;
        this.portCode = portCode;
        this.portclscode = portClsCode;
        this.ywDate = ywDate;
        this.sign = "(,),+,-,*,/,>,<,=";
        try {
        	setLeverGradeFundInfo(); //设置公式
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	/**
	 * 20130619 added by liubo.Story #3759
	 * 重载一个init方法，主要增加sInvType调用类型这个参数。在份额折算底稿中会用到
	 * @param forCode		算法公式代码
	 * @param portCode		投资组合
	 * @param portClsCode	分级组合代码
	 * @param ywDate		业务日期
	 * @param sInvType		调用类型
	 */
    public void init(String forCode,String portCode,String portClsCode,Date ywDate,String sInvType) {
    	
    	init(forCode,portCode,portClsCode,ywDate);
    	
    	this.sInvokeType = sInvType;
    	
    }
    

    public double calcGradeFundNetValue() throws YssException {
        return this.calcFormulaDouble();
    }
    
    public Object getExpressValueEx(String sExpress,ArrayList alParams, String sEndStr) throws YssException
    {
    	return getExpressValue(sExpress,alParams) + sEndStr;
    }
  
    /*
     * story 3759 20130609 yeshenghong 表达式解析
     * */
    public Object getExpressValue(String sExpress, ArrayList alParams) throws
        YssException {
        Object objResult = null;
        if (sExpress.trim().equalsIgnoreCase("baseDate"))// 获取份额折算的基准日期
		{
        	 objResult = getBaseDate((Date)alParams.get(0));
		}else if (sExpress.trim().equalsIgnoreCase("curBaseDate"))
		{
       	 objResult = getCurBaseDate((Date)alParams.get(0));
		}else if(sExpress.trim().equalsIgnoreCase("yield")) // 获取利率  
		{
			 if(alParams.size()==2)
			 {
				 objResult = getYearYiled((String)alParams.get(0),(Date)alParams.get(1));
			 }else {
				 objResult = getYearYiled((String)alParams.get(0));
			 }
		}else if(sExpress.trim().equalsIgnoreCase("daysOfYear"))//获取指定日期期间的年天数
		{
			 objResult = getYearDays((Date)alParams.get(0));
		}else if(sExpress.trim().equalsIgnoreCase("netValue"))//获取指定组合的单位净值
		{
			 objResult = getPortNetValue((String)alParams.get(0),(Date)alParams.get(1));
		}else if(sExpress.trim().equalsIgnoreCase("amount"))//获取指定组合的份额
		{
			 objResult = getPortTotalAmount((String)alParams.get(0));
		}else if(sExpress.trim().equalsIgnoreCase("priorNav"))//获取指定CLASS的优先单位净值  公式算出
		{
			 
			 if(alParams.size()==2)
			{
				objResult = getPriorClassNetValue((String)alParams.get(0),(Date)alParams.get(1));
			}else
			{
				objResult = getPriorClassNetValue((String)alParams.get(0));
			}
		}else if(sExpress.trim().equalsIgnoreCase("priorNavBySet")) //通过杠杆基金设置计算优先类净值
		{
			 
			objResult = getPriorClassNetValueBySet((String)alParams.get(0),(Date)alParams.get(1));
		}else if(sExpress.trim().equalsIgnoreCase("classNetValue"))//获取指定CLASS的优先单位净值  公式算出
		{
			 objResult = getPortClassAssetValue((String)alParams.get(0));
		}else if(sExpress.trim().equalsIgnoreCase("classNav"))//获取指定CLASS的单位净值
		{
			if(alParams.size()==2)
			{
				objResult = getPortClassNetValue((String)alParams.get(0),(Date)alParams.get(1));
			}else
			{
				objResult = getPortClassNetValue((String)alParams.get(0));
			}
		}else if(sExpress.trim().equalsIgnoreCase("classStaticValue"))//获取多CLASS的统计值
		{
			 objResult = getPortClassStaticValue((String)alParams.get(0),(Date)alParams.get(1),(String)alParams.get(2));
		}
		else if(sExpress.trim().equalsIgnoreCase("baseAmount"))//获取基础份额实收资本
		{
			 objResult = getBaseAmountValue((String)alParams.get(0),(String)alParams.get(1));
		}else if(sExpress.trim().equalsIgnoreCase("digit"))//资产净值保留位数
		{
			 objResult = getPortNavDigit((String)alParams.get(0));
		}else if(sExpress.trim().equalsIgnoreCase("portrate"))//获取币种的组合汇率
		{
			 objResult = getClsPortRate((Date)alParams.get(0),(String)alParams.get(1));
		}else if(sExpress.trim().equalsIgnoreCase("getClsPortCode"))
		{
			objResult = getClsPortCode((String)alParams.get(0),(String)alParams.get(1),(String)alParams.get(2));
		}else if(sExpress.trim().equalsIgnoreCase("baseDate2"))// 获取份额折算的基准日期  海富通
		{
       	 	objResult = getBaseDate2((Date)alParams.get(0));
		}
        return objResult;
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
    	String portclsCode = "123001";
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
	 * add by yeshenghong 2013-7-30
	 * 获取币种的组合汇率
	 * @param string
     * @param date 
	 * @return String
     * @throws YssException 
	 */
    private double getClsPortRate(Date date, String curyCode) throws YssException {
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
	 * add by yeshenghong 2013-7-30
	 * 获取分级基金资产净值保留位数
	 * @param string
	 * @return String
     * @throws YssException 
	 */
    private String getPortNavDigit(String clsPortCode) throws YssException {
		// TODO Auto-generated method stub
    	CtlPubPara pubPara = new CtlPubPara();
	   	pubPara.setYssPub(this.pub);
    	String digits = pubPara.getDigitsPortMethod("PubParaUnitCls","dayfinish","CtlPubParaUnitCls","portClsSel","txtdigit",clsPortCode,"3");	
		return digits;
	}

	/**
	 * add by yeshenghong 2013-5-27
	 * 获取基础份额实收资本
	 * @param string
     * @param date 
	 * @return String
     * @throws YssException 
	 */
	private double getBaseAmountValue(String qfCnCw, String clsPortCode) throws YssException {
		// TODO Auto-generated method stub
		ResultSet rs = null;
        String strSql = "";
        double clsShareValue = 0;
        try {
        		if(qfCnCw.equals("0"))//表示场内
        		{
        			strSql = " select FFloorShare from " + pub.yssGetTableName("tb_ta_levershare") + " where " +
    			 	 " fportcode = " + dbl.sqlString(portCode) + " and  FConversionDate = " + dbl.sqlDate(this.ywDate);
        			rs = dbl.openResultSet(strSql);
                    if (rs.next()) {
                    	clsShareValue = rs.getDouble("FFloorShare");
                    }
        		}else//1 表示场外
        		{
        			strSql = " select FOtcShare from " + pub.yssGetTableName("tb_ta_levershare") + " where " +
   			 	      " fportcode = " + dbl.sqlString(portCode) + " and  FConversionDate = " + dbl.sqlDate(this.ywDate);
        			rs = dbl.openResultSet(strSql);
                    if (rs.next()) {
                    	clsShareValue = rs.getDouble("FOtcShare");
                    }
        		}
                
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return clsShareValue;
	}
	
	/**
	 * 获取指定CLASS的单位净值
	 * add by yeshenghong 2013-5-16
	 * @param string
	 * @return Object
     * @throws YssException 
	 */
	private double getPortClassNetValue(String clsPortCode,Date dDate) throws YssException {
		ResultSet rs = null;
        String strSql = "";
        double clsNetValue = 0;
        try {
        		strSql = " select FClassNetValue from " + pub.yssGetTableName("tb_data_MultiClassNet") + " where fnavdate = " + dbl.sqlDate(dDate)
        			 	+ " and fportcode = " + dbl.sqlString(portCode) + " and  FCurycode = " + dbl.sqlString(clsPortCode) +
        			 	" and ftype = '02'  ";
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
        			 	" and ftype =  " + staticType;
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
	 * 获取指定CLASS的单位净值
	 * add by yeshenghong 2013-5-16
	 * @param string
	 * @return Object
     * @throws YssException 
	 */
	private double getPortClassNetValue(String clsPortCode) throws YssException {
		ResultSet rs = null;
        String strSql = "";
        double clsNetValue = 0;
        try {
        		strSql = " select FClassNetValue from " + pub.yssGetTableName("tb_data_MultiClassNet") + " where fnavdate = " + dbl.sqlDate(this.ywDate)
        			 	+ " and fportcode = " + dbl.sqlString(portCode) + " and  FCurycode = " + dbl.sqlString(clsPortCode) +
        			 	" and ftype = '02'  ";
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
	 * 获取指定CLASS的资产净值
	 * add by yeshenghong 2013-5-16
	 * @param string
	 * @return Object
     * @throws YssException 
	 */
	private double getPortClassAssetValue(String clsPortCode) throws YssException {
		ResultSet rs = null;
        String strSql = "";
        double clsNetValue = 0;
        try {
        		strSql = " select FClassNetValue from " + pub.yssGetTableName("tb_data_MultiClassNet") + " where fnavdate = " + dbl.sqlDate(this.ywDate)
        			 	+ " and fportcode = " + dbl.sqlString(portCode) + " and  FCurycode = " + dbl.sqlString(clsPortCode) +
        			 	" and ftype = '06'  ";
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
     * 获取指定CLASS的优先单位净值  公式算出
	 * add by yeshenghong 2013-5-16
	 * 20130608 modified by liubo.Story #3759.将此方法的private修饰词改为public，方便份额折算底稿进行调用
	 * @param string
	 * @return double
	 * story 3759
     * @throws YssException 
	 */
	public double getPriorClassNetValue(String clsPortCode) throws YssException {
		ResultSet rs = null;
        double clsNetValue = 1;
        Date preDate = this.getBaseDate(this.ywDate);
        Date fixDate = this.ywDate;
        double yield = 0;
        double fixDays = 0;
        long yearDays;
        try {
        	    ArrayList periodList = this.dividePriorFixPeriod(clsPortCode, this.ywDate);
        	    for(Iterator iter = periodList.iterator();iter.hasNext();)
        	    {
        	    	fixDate = (Date)iter.next();//新的计息起始日
        	    	if(!iter.hasNext())//最后一段计算方法
        	    	{
        	    		yield =  this.getYearYiled(clsPortCode, fixDate);
        	    		fixDays = YssFun.dateDiff(preDate,fixDate) + 1;//新计息日前一天   减  基准日
        	    		yearDays = this.getYearDays(fixDate);//年天数
        	    		clsNetValue += (YssFun.roundIt(Math.pow(1+yield, fixDays/yearDays),16)-1);//
        	    	}else if(YssFun.dateDiff(preDate,fixDate)>0)
        	    	{
        	    		yield =  this.getYearYiled(clsPortCode, YssFun.addDay(fixDate, -1));
        	    		fixDays = YssFun.dateDiff(preDate,YssFun.addDay(fixDate, -1)) + 1;//新计息日前一天   减  基准日
        	    		yearDays = this.getYearDays(YssFun.addDay(fixDate, -1));//年天数
        	    		clsNetValue += (YssFun.roundIt(Math.pow(1+yield, fixDays/yearDays),16)-1);//
        	    	}
        	    	preDate = fixDate;
        	    }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return clsNetValue;
	}
	
	/**
     * 获取优先CLASS基准日期的优先单位净值  公式算出
	 * add by yeshenghong 2013-5-16
	 * @param string
	 * @return double
	 * story 3759
     * @throws YssException 
	 */
	private double getPriorClassNetValueBySet(String clsPortCode,Date curDate) throws YssException {
        double clsNetValue = 1;
        Date preDate = this.getCurBaseDate(curDate);
        Date fixDate = curDate;
        double yield = 0;
        double fixDays = 0;
        long yearDays;
    	yield =  this.getYearYiled(clsPortCode, fixDate);
		fixDays = YssFun.dateDiff(preDate,YssFun.addDay(fixDate, -1)) + 1;//新计息日   减  基准日
		yearDays = this.getYearDays(fixDate);//年天数
		clsNetValue += (YssFun.roundIt(Math.pow(1+yield, fixDays/yearDays),16)-1);//
        return clsNetValue;
	}

	/**
     * 获取优先CLASS基准日期的优先单位净值  公式算出
	 * add by yeshenghong 2013-5-16
	 * 20130608 modified by liubo.Story #3759.将此方法的private修饰词改为public，方便份额折算底稿进行调用
	 * @param string
	 * @return double
	 * story 3759
     * @throws YssException 
	 */
	public double getPriorClassNetValue(String clsPortCode,Date curDate) throws YssException {
        double clsNetValue = 1;
        Date preDate = this.getBaseDate(curDate);
        Date fixDate = curDate;
        double yield = 0;
        double fixDays = 0;
        long yearDays;
    	yield =  this.getYearYiled(clsPortCode, fixDate);
		fixDays = YssFun.dateDiff(preDate,fixDate) + 1;//新计息日   减  基准日
		yearDays = this.getYearDays(fixDate);//年天数
		clsNetValue += (YssFun.roundIt(Math.pow(1+yield, fixDays/yearDays),16)-1);//
        return clsNetValue;
	}

	/*
     * 获取指定组合的份额
     * add by yeshenghong story 3759 20130516
     * */
    private double getPortTotalAmount(String portCode) throws YssException {
    	 ResultSet rs = null;
         String strSql = "";
         double netAmount = 0;
         try {
                 strSql = " select sum(fstorageamount) as fstorageamount from " +  pub.yssGetTableName("tb_stock_ta") + 
                 		" where fyearmonth not like '%00' and fstoragedate = " + dbl.sqlDate(this.ywDate) + 
                 		" and (fportcode = " + dbl.sqlString(portCode) + 
                 		" or fportclscode = " + dbl.sqlString(portCode) + ")";
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 netAmount = rs.getDouble("fstorageamount");
                 }
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	return netAmount;
	}

	/*
     * 获取指定组合的单位净值
     * add by yeshenghong story 3759 20130516
     * */
    private Object getPortNetValue(String portCode, Date date) throws YssException {
		// TODO Auto-generated method stub
    	 ResultSet rs = null;
         String strSql = "";
         double netValue = 0;
         try {
                 strSql = " select  FPortMarketValue from " +  pub.yssGetTableName("tb_data_navdata") + " t  " +
                 		" where  t.FPortcode = " + dbl.sqlString(portCode) + " and fkeycode = 'TotalValue'" + 
                 		" and fnavdate = " + dbl.sqlDate(date);
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 netValue = rs.getDouble("FPortMarketValue");
                 }
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	return netValue;
	}

	/*
     * 获取指定日期期间的年天数
     * add by yeshenghong story 3759 20130516
     * */
    private long getYearDays(Date dDate) {
		// TODO Auto-generated method stub
    	return YssFun.isLeapYear(dDate) ? 366:365;
	}

    /*
     * 获取利率  
     * add by yeshenghong story 3759 20130516
     * */
	private double getYearYiled(String portclscode, Date ywDate) throws YssException {
    	 ResultSet rs = null;
         String strSql = "";
         double yearyield = 0;
         String yieldFormula = "";
         try {
                 strSql = " select distinct FConvention from " +  pub.yssGetTableName("tb_ta_portcls") + " t  " +
                 		" where  t.FPortcode = " + dbl.sqlString(portCode) + " and fconvention is not null";
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 yieldFormula = rs.getString("FConvention");
                 }
                 yearyield = getFixValue(yieldFormula,ywDate);
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	return yearyield;
	}
    
	/*
     * 获取利率  
     * add by yeshenghong story 3759 20130516
     * */
	private double getYearYiled(String portclscode) throws YssException {
    	 ResultSet rs = null;
         String strSql = "";
         double yearyield = 0;
         String yieldFormula = "";
         try {
                 strSql = " select distinct FConvention from " +  pub.yssGetTableName("tb_ta_portcls") + " t  " +
                 		" where  t.FPortcode = " + dbl.sqlString(portCode) + " and fconvention is not null";
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 yieldFormula = rs.getString("FConvention");
                 }
                 yearyield = getFixValue(yieldFormula,this.ywDate);
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	return yearyield;
	}

    /**
     * add by yeshenghong 2013-05-16 story #3759 获取比率公式固定值
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
                dResult = performula.getPerValue(); //去固定值
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
     * add by yeshenghong 2013-05-16 story #3759 优先份额计息期间划分
     * @param sPerExpCode String
     * @param dDate Date
     * @return double
     * @throws YssException
     */
    private ArrayList dividePriorFixPeriod(String portClsCode,
                               java.util.Date dDate) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        Date preDate = null;
        Date curDate = null;
        ArrayList periodList = new ArrayList();
        try {
            
            strSql = " select frangedate from  " + pub.yssGetTableName("Tb_Para_Performula_Rela") + " f join  (select * from " 
            	    + pub.yssGetTableName("tb_ta_portcls") + " where fportclscode = " + dbl.sqlString(portClsCode)   
            		+ ") t on f.fformulacode = t.fconvention  where f.frangedate > " + dbl.sqlDate(this.getBaseDate(dDate)) 
            		+ " and f.frangedate <= " + dbl.sqlDate(dDate);
            rs = dbl.openResultSet(strSql);
            while(rs.next())
            {
            	curDate = rs.getDate("frangedate");
            	if(preDate!=null&&YssFun.getYear(preDate)<YssFun.getYear(curDate))
            	{
            		periodList.add(YssFun.parseDate((YssFun.getYear(preDate)+1) + "-1-1"));//添加跨年年份日期
            	}
            	periodList.add(curDate);
            	preDate = curDate;
            }
            if(preDate!=null&&YssFun.getYear(preDate)<YssFun.getYear(this.ywDate))
        	{
        		periodList.add(YssFun.parseDate(YssFun.getYear(this.ywDate) + "-1-1"));//添加跨年年份日期
        	}
            periodList.add(this.ywDate);
            return periodList;
        } catch (Exception e) {
            throw new YssException("获取比率期间出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

	/*
	 * add by yeshenghong 2013-05-16 story #3759
     * 获取份额折算的基准日期
     * */
    private Date getBaseDate(Date date) throws YssException {
    	 ResultSet rs = null;
         String strSql = "";
         Date baseDate = null;
         try {
                 strSql = " select max(ftradedate) as ftradedate from (" +
                 		" select distinct max(FMarkDate) + 1 as ftradedate from (select case when FMarkDate is null then" +
                 		" ftradedate else FMarkDate end as FMarkDate from  " + pub.yssGetTableName("tb_ta_trade") + 
                 		" where fselltype in ('09') " + " and FPortcode = " + dbl.sqlString(portCode) + " " +
                 		
                 	/**Start modified by liubo.Story #3759.调用类型sInvokeType值为normal，表示进行正常调用
                 	 * 值为rep，表示由份额折算底稿在调用，且折算底稿在计算折算前的A类单位净值，需要排除估值日当天的TA拆分数据*/
                 		" and FTradeDate " + (this.sInvokeType.equals("normal") ? " <= " : " < ") +
                 		dbl.sqlDate(date) +
                 	/**Start modified by liubo.Story #3759.调用类型sInvokeType值为normal，表示进行正常调用*/
                 		
                 		" ) union select distinct min(ftradedate) as ftradedate from " + pub.yssGetTableName("tb_ta_trade") + 
                 		" where fselltype in ('00') " + " and FPortcode = " + dbl.sqlString(portCode) + " )";
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 baseDate = rs.getDate("ftradedate");
                 }
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	return baseDate;
	}
    
    
    /*
	 * add by yeshenghong 2013-07-31 story #3759
     * 获取份额折算的基准日期
     * 海富通获取基准日期的方法不同
     * */
    private Date getBaseDate2(Date date) throws YssException {
    	 ResultSet rs = null;
         String strSql = "";
         Date baseDate = null;
         try {
                 strSql = " select max(ftradedate) as ftradedate from (" +
                 		" select distinct max(FMarkDate) as ftradedate from (select case when FMarkDate is null then" +
                 		" ftradedate else FMarkDate end as FMarkDate from  " + pub.yssGetTableName("tb_ta_trade") + 
                 		" where fselltype in ('09') " + " and FPortcode = " + dbl.sqlString(portCode) + " " +
                 		
                 	/**Start modified by liubo.Story #3759.调用类型sInvokeType值为normal，表示进行正常调用
                 	 * 值为rep，表示由份额折算底稿在调用，且折算底稿在计算折算前的A类单位净值，需要排除估值日当天的TA拆分数据*/
                 		" and FTradeDate " + (this.sInvokeType.equals("normal") ? " <= " : " < ") +
                 		dbl.sqlDate(date) +
                 	/**Start modified by liubo.Story #3759.调用类型sInvokeType值为normal，表示进行正常调用*/
                 		
                 		" ) union select distinct min(ftradedate) as ftradedate from " + pub.yssGetTableName("tb_ta_trade") + 
                 		" where fselltype in ('00') " + " and FPortcode = " + dbl.sqlString(portCode) + " )";
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 baseDate = rs.getDate("ftradedate");
                 }
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	return baseDate;
	}
    
    /*
	 * add by yeshenghong 2013-05-16 story #3759
     * 获取份额折算的基准日期  从折算期间设置中
     * */
    private Date getCurBaseDate(Date date) throws YssException {
    	 ResultSet rs = null;
         String strSql = "";
         Date baseDate = null;
         try {
                 strSql = " select  max(FBaseDate) as ftradedate from " + pub.yssGetTableName("tb_ta_LeverShare") + 
                 		" where FPortcode = " + dbl.sqlString(portCode) + " and FConversionDate <= " + dbl.sqlDate(date);
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 baseDate = rs.getDate("ftradedate");
                 }
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	return baseDate;
	}

    /*
     * 关键字解析
	 * add by yeshenghong 2013-05-16 story #3759
     * 获取份额折算的基准日期
     * */
    public Object getKeywordValue(String sKeyword) throws YssException {
        Object objResult = null;
        
        if(sKeyword.trim().equalsIgnoreCase("portcode"))
		{
			 objResult = this.portCode;
		}else if(sKeyword.trim().equalsIgnoreCase("portclscode"))
		{
			 objResult = this.portclscode;
		}else if(sKeyword.trim().equalsIgnoreCase("ywDate"))
		{
			 objResult = this.ywDate;
		}else if(sKeyword.trim().equalsIgnoreCase("portclscodeA"))
		{
			 objResult = this.getPriorPortCode();
		}else if(sKeyword.trim().equalsIgnoreCase("portclscodeB"))
		{
			 objResult = this.getRadicalPortCode();
		}else if(sKeyword.trim().equalsIgnoreCase("splitCase")){
			objResult = this.getSplitCaseCode();
		}else if(sKeyword.trim().equalsIgnoreCase("isQfCnCw")){//是否区分场内场外
			objResult = this.getQfCnCwCode();
		}
        /**Start 20130819 added by liubo.Story #3844.需求上海-(汇添富)QDIIV4.0(高)20130411001
         * 某个投资组合基础类分级组合的组合分级代码*/
		else if(sKeyword.trim().equalsIgnoreCase("portclscodeBase"))
		{
			objResult = this.getBasePortCode();
		}
        /**End 20130819 added by liubo.Story #3844.需求上海-(汇添富)QDIIV4.0(高)20130411001*/
		else{
            objResult = sKeyword;
        }
                
        return objResult;
    }
    
    /* 
     * add by yeshenghong 2013-05-22 story #3759
     * 获取区分场内场外折算的设置
     * */
    private String getQfCnCwCode() throws YssException {
    	ResultSet rs = null;
        String strSql = "";
        String  qfCnCw = "";
        try {
		    strSql = " select distinct t.feachhandle from "
				+ pub.yssGetTableName("tb_ta_levershare") + " t  "
				+ " where t.fconversiondate =  " + dbl.sqlDate(this.ywDate)
				+ " and t.FPortcode = " + dbl.sqlString(this.portCode);
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				qfCnCw = rs.getString("feachhandle");
			}
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		return qfCnCw;
	}

	/* 
     * add by yeshenghong 2013-05-22 story #3759
     * 获取折算类型代码
     * */
   private String getSplitCaseCode() throws YssException {
   	 ResultSet rs = null;
        String strSql = "";
        String  discountType = "";
        try {
		    strSql = " select distinct t.fconversiontype from "
				+ pub.yssGetTableName("tb_ta_levershare") + " t  "
				+ " where t.fconversiondate =  " + dbl.sqlDate(this.ywDate)
				+ " and t.FPortcode = " + dbl.sqlString(this.portCode);
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				discountType = rs.getString("fconversiontype");
			}
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		return discountType;
   }
   
   /*
    * add by yeshenghong 2013-05-22 story #3759
    * 获取进取类的分级组合代码
    * */
   private String getRadicalPortCode() throws YssException {
   	 ResultSet rs = null;
        String strSql = "";
        String  portClsCode = "";
        try {
                strSql = " select FPortClsCode from " + pub.yssGetTableName("tb_ta_portcls") + " where FCheckState = 1 " +
                		  " and FShareCategory = 2 and FPortCode = " + dbl.sqlString(this.portCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
               	 portClsCode = rs.getString("FPortClsCode");
                }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		return portClsCode;
	}

    /*
     * add by yeshenghong 2013-05-22 story #3759
     * 获取优先类的分级组合代码
     * */
    private String getPriorPortCode() throws YssException {
    	 ResultSet rs = null;
         String strSql = "";
         String  portClsCode = "";
         try {
                 strSql = " select FPortClsCode from " + pub.yssGetTableName("tb_ta_portcls") + " where FCheckState = 1 " +
                 		  " and FShareCategory = 1 and FPortCode = " + dbl.sqlString(this.portCode);
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 portClsCode = rs.getString("FPortClsCode");
                 }
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
		return portClsCode;
	}

    /*
     * 20130819 added by liubo.Story #3844.
     * 获取基础类的分级组合代码
     * */
    private String getBasePortCode() throws YssException {
    	 ResultSet rs = null;
         String strSql = "";
         String  portClsCode = "";
         try {
                 strSql = " select FPortClsCode from " + pub.yssGetTableName("tb_ta_portcls") + " where FCheckState = 1 " +
                 		  " and FShareCategory = 3 and FPortCode = " + dbl.sqlString(this.portCode);
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 portClsCode = rs.getString("FPortClsCode");
                 }
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
		return portClsCode;
	}

	/*
     * 设置杠杆基金的算法公式
	 * add by yeshenghong 2013-05-16 story #3759
     * 获取份额折算的基准日期
     * */
    private void setLeverGradeFundInfo() throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
//            if (this.aFwTrade != null) {
                strSql = " select FFormula from Tb_Base_CalcInsMetic where FCheckState = 1 and FCIMCode = " + dbl.sqlString(this.formulaCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    this.formula = rs.getString("FFormula");
                }
//            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    

	/**
     * 20130608 added by liubo.Story #3759.
     * 通过此方法，生成A类单位净值的EXCEL公式
	 * @param string
	 * @return double
	 * story 3759
     * @throws YssException 
	 */
	public String getPriorClassNetValueForRepExp(String clsPortCode) throws YssException {
		StringBuffer buff = new StringBuffer();
        Date preDate = this.getBaseDate(this.ywDate);
        Date fixDate = this.ywDate;
        double yield = 0;
        double fixDays = 0;
        long yearDays;
        try 
        {
        	buff.append("=Round(");
        	
        	int iInterval = 0;
        	
    	    ArrayList periodList = this.dividePriorFixPeriod(clsPortCode, this.ywDate);
    	    for(Iterator iter = periodList.iterator();iter.hasNext();)
    	    {
	    		if (iInterval > 0)
	    		{
	    			buff.append("+");
	    		}
	    		
    	    	fixDate = (Date)iter.next();//新的计息起始日
    	    	if(!iter.hasNext())//最后一段计算方法
    	    	{
    	    		yield =  this.getYearYiled(clsPortCode, fixDate);
    	    		fixDays = YssFun.dateDiff(preDate,fixDate) + 1;//新计息日前一天   减  基准日
    	    		yearDays = this.getYearDays(fixDate);//年天数
    	    		buff.append("POWER(1+" + YssFun.formatNumber(yield, "#,##0.###") + ","
    	    				+ fixDays + "/" + yearDays);
    	    		if (iInterval > 0)
    	    		{
    	    			buff.append("-1");
    	    		}
    	    		buff.append("),14)");
    	    	}
    	    	else if(YssFun.dateDiff(preDate,fixDate)>0)
    	    	{
    	    		yield =  this.getYearYiled(clsPortCode, YssFun.addDay(fixDate, -1));
    	    		fixDays = YssFun.dateDiff(preDate,YssFun.addDay(fixDate, -1)) + 1;//新计息日前一天   减  基准日
    	    		yearDays = this.getYearDays(YssFun.addDay(fixDate, -1));//年天数
    	    		buff.append("POWER(1+" + YssFun.formatNumber(yield, "#,##0.###") + ","
    	    				+ fixDays + "/" + yearDays);
    	    		if (iInterval > 0)
    	    		{
    	    			buff.append("-1");
    	    		}
    	    		buff.append("),14)");
    	    	}
    	    	preDate = fixDate;
    	    	
    	    	iInterval ++;
    	    	
    	    }
        } 
        catch (Exception e) 
        {
            throw new YssException(e);
        }
        return buff.toString();
	}
	



 
}
