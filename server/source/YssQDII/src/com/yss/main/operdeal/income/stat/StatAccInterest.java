package com.yss.main.operdeal.income.stat;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.operdeal.stgstat.*;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.util.*;

public class StatAccInterest
    extends BaseIncomeStatDeal {
    private java.util.Date allDate = null;
    //add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    private HashMap hmAccType = null;
    
    public StatAccInterest() {
    }
    private HashMap jsfMap = new HashMap();
    public ArrayList getDayIncomes(java.util.Date dDate) throws YssException {

        ArrayList alIncomes = new ArrayList();
        ArrayList alTemp = new ArrayList();
        CashAccountBean cash = new CashAccountBean();
        String strSql = "";
        ResultSet rs = null;
        BaseStgStatDeal secstgstat = null;
        //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        Date logBeginDate = null;
        String portCode = "";//组合代码
        //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
        	//add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        	hmAccType = getAccTypeByAccount(selCodes);
        	
        	//-------- add by wangzuochun 2010.02.10 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A 
        	if (this.modeCode != null && this.modeCode.length() > 0 && this.modeCode.equals("1")) {
        		return alIncomes;
        	}
        	//--------
        	
            allDate = dDate;
            cash.setYssPub(pub);
            //-----------------------------------------------------------------------------------
            //2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
            //将统计库存从getHaveBalanceIntrest中提出来，统计一次就够了
            secstgstat = (BaseStgStatDeal) pub. //现金库存  sj  每次都先统计库存
                getOperDealCtx().getBean("CashStorage");
            secstgstat.setYssPub(pub);
            secstgstat.setStatCodes(this.selCodes);//add by huangqirong 2013-04-15 bug #7545 选中的现金账户
            secstgstat.stroageStat(dDate,
                                   dDate,
                                   operSql.sqlCodes(portCodes), true, false);
            //-----------------------------------------------------------------------------------

            strSql =
                //--------------------------------------------edited by zhouxiang MS01444 -------
            	"select distinct FCashAccCode,FCashAccName,FCuryCode,FFormulaCode,FRoundCode," +
            	//-----------------------------------------------end by zhouxiang MS01444 -------
            	//----- sj modified 20090701 MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj ------------------
                "FInterestorigin," +
                //-------------------------------------------------------------------------------------------
                " FPeriodCode,FBankCode,FPortCode,FDepDurCode,FInterestCycle,FInterestorigin,FInterestWay,FInterestAlg,FFixRate,nvl(fperexpcode,' ')as fperexpcode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FCashAccCode in (" +
                operSql.sqlCodes(selCodes) +
                ") and FPortCode in (" + operSql.sqlCodes(this.portCodes) + ")";
            rs = dbl.queryByPreparedStatement(strSql);
            while (rs.next()) {
            	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	logBeginDate = new Date();
            	portCode = rs.getString("FPortCode");
            	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            	
                cash.setStrCashAcctCode(rs.getString("FCashAccCode"));
                cash.setStrPortCode(rs.getString("FPortCode"));
                cash.setStrPeriodCode(rs.getString("FPeriodCode"));
                cash.setInterestOrigin(rs.getInt("FInterestorigin"));
                cash.setStrInterTax(rs.getString("fperexpcode"));
                cash.setInterestAlg(rs.getInt("FInterestAlg"));
                cash.setStrCurrencyCode(rs.getString("FCuryCode"));
                //add by jiangshichao 2012.02.28   添加利息税设置  start ---------------------------
                
                // 添加利息税设置 end ---------------------------------------------------------------------
                if (rs.getInt("FInterestWay") == 0) { //按照当日余额计算
                	if(cash.getInterestAlg()!=1)
                	{
                		alTemp = getHaveBalanceIntrest(cash, dDate);
                	}else
                	{
                		alTemp = getHaveBalanceIntrestByJsf(cash, dDate);
                	}
                    alIncomes.addAll(alTemp);
                } else if (rs.getInt("FInterestWay") == 2) { //按照前日余额计算
                	if(cash.getInterestAlg()!=1)
                	{
                		alTemp = getHaveBalanceIntrest(cash, YssFun.addDay(dDate, -1));
                	}
                    else
                    {
                    	 alTemp = getHaveBalanceIntrestByJsf(cash, YssFun.addDay(dDate, -1));
                    }
                    alIncomes.addAll(alTemp);
                } else if (rs.getInt("FInterestWay") == 1) { //按照单笔计算
                    //----- sj modified 20090701 MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj ------------------
                    if(rs.getInt("FInterestorigin") == 0){//当为内部方式时
                        alTemp = calcInnerSavingInterest(cash, dDate); //使用新建的定存计息方法，放弃之前的方法。
                    }else{//当为非内部方式是
                        alTemp = getEachSavingInterest(cash,dDate);
                    }
                    //-------------------------------------------------------------------------------------------
                    alIncomes.addAll(alTemp);
                }
               
            }
            
            return alIncomes;
        }catch(YssException e){
        	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, true, 
                		portCode, dDate, dDate, dDate, 
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                		(logInfo + "\r\n现金账户计息出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
                		.replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", ""),
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                		" ",logBeginDate,logSumCode,new Date());
        		}
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	finally{//添加 finally 保证可以抛出异常
        		throw new YssException(e.getMessage(),e);
        	}
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        }
        catch (Exception e) {
            throw new YssException("系统现金存款计息时出现异常!\n", e); // by 曹丞 2009.01.23 统计运营应收应付库存异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * @param selCodes2 账户
     * @throws YssException 
     * @return使用现金账户获取现金账户类型
     */
	private HashMap getAccTypeByAccount(String selCodes2) throws YssException {
		ResultSet rs=null;
		String sqlStr="";
		HashMap hmAccType = new HashMap();
		try{
			/**
			 * shashijie :BUG #1060 多条（五十条）债券信息同时进行计提利息时，报错。 现金计息也再次报错
			 * 原因是这里获取现金账户的时候没有考虑前台多选,导致这里的传入的债券(现金账户)代码过长
			 * selCodes2字符串变成"(债券或现金账户)代码1,代码2,代码3......这里会超过4000字符长度",导致oracle报"文本字符串过长"
			 */
//			String[] selCodes = null;
//			if (selCodes2.indexOf(",")>0) {
//				selCodes = selCodes2.split(",");
//			}//查询每个传入代码的现金账户,此sql同原先一样无改动
//			if (selCodes!=null) {
			sqlStr="select a.FCashAccCode, a.fsubacctype,b.fsubacctypename from "+pub.yssGetTableName("Tb_Para_CashAccount")
				+" a  left join ( select fsubacctypecode,fsubacctypename from "+pub.yssGetTableName("Tb_Base_SubAccountType")
				+" where fcheckstate=1) b on a.fsubacctype=b.fsubacctypecode "
				+" where a.fcheckstate = 1 and a.fcashacccode in( "+operSql.sqlCodes(selCodes2)
				+" ) order by a.fsubacctype";
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if(rs.next()){//这里加个逗号
				//edit by songjie 2012.08.20 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 替换分隔符
				hmAccType.put(rs.getString("FCashAccCode"),"账户子类型代码:" + rs.getString("fsubacctype") + 
						      "\r\n账户子类型名称:"+rs.getString("fsubacctypename"));
			}
			/**end......shashijie :BUG #1060 多条（五十条）债券信息同时进行计提利息时，报错。 现金计息也再次报错*/
//			} 
			
			return hmAccType;
		}catch(Exception e){
			throw new YssException("使用现金账户获取账户类型、子类型出错");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
    
    protected ArrayList getHaveBalanceIntrest(CashAccountBean cashacc,
                                              java.util.Date dDate) throws
        YssException {
        ArrayList alIncomes = new ArrayList();
        String strSql = "";
        ResultSet rs = null;
        java.util.Date tmpDate = null;
        CashPecPayBean pay = null;
        double dTmpMoney = 0;
        double BaseCuryRate = 0;
        double PortCuryRate = 0;

        boolean analy1; //判断是否需要用分析代码；杨
        boolean analy2;

        long lDays = 1;
        String strException="";
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------
        
        try {

			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");

			strSql =
			// ---edited by zhouxiang MS01444 计提存款计息只取组合中启用日期最大的
			// by guyichuan 20110520 STORY #561 增加FLoanCode字段
			"select distinct  x.*,FCashAccName,y.FCuryCode,FFormulaCode,FLoanCode,FRoundCode,FPeriodCode,"
					+ "FInterestCycle,FInterestorigin,FInterestWay,FFixRate,FPortName,FPortCury,FIntereset"
					+ " from (" + " select * from  "
					+ pub.yssGetTableName("Tb_Stock_Cash")
					+
					// 这里去掉这种获取最大日期的方法，对SQL优化处理 by leeyu 20100819 合并太平版本调整
					// " where FStorageDate = (select max(FStorageDate) from " +
					// pub.yssGetTableName("Tb_Stock_Cash") +
					// " where FStorageDate <= " + dbl.sqlDate(dDate) +
					" where FStorageDate = "
					+ dbl.sqlDate(dDate)
					+ " and FYearMonth <> "
					+ dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00")
					+
					// 这里去掉这种获取最大日期的方法，对SQL优化处理 by leeyu 20100819 合并太平版本调整
					// " and FCheckState = 1) and FYearMonth <> " +
					// dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00") +
					// -------------------------------
					" and FCheckState = 1 and FPortCode in ("
					+ operSql.sqlCodes(portCodes)
					+ ")"
					+ " ) x left join "
					+
					// -------------------------------
					" (select FCashAccCode,FCashAccName,FCuryCode,FFormulaCode,FLoanCode,FRoundCode,"
					+ " FPeriodCode,FBankCode,FDepDurCode,FInterestCycle,FInterestorigin,FInterestWay,FFixRate from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where FCheckState = 1) y on x.FCashAccCode = y.FCashAccCode "
					+
					// --------------------------------
					// ---------------------edited by zhouxiang
					// MS01444----------
					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码

					" left join (select FPortCode,FPortName,FPortCury from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ "  where FCheckState = 1) z on x.FPortcode = z.FPortcode "
					+
					// end by lidaolong
					// ----------------------------------
					" left join (select m1.FIntereset,m1.FBankCode,m1.FCuryCode,m1.FDepDurCode from "
					+ pub.yssGetTableName("Tb_Para_DepositInterest")
					+ " m1 join (select FBankCode,FCuryCode,FDepDurCode,max(FStartDate) as FStartDate from "
					+ pub.yssGetTableName("Tb_Para_DepositInterest")
					+ " where FStartDate <= "
					+ dbl.sqlDate(dDate)
					+ " and FCheckState = 1 group by FBankCode,FCuryCode,FDepDurCode) m2 on m1.FBankCode = m2.FBankCode "
					+ " and m1.FCuryCode = m2.FCuryCode and m1.FDepDurCode = m2.FDepDurCode and m1.FStartDate = m2.FStartDate "
					+ " ) m on y.FBankCode = m.FBankCode and y.FCuryCode = m.FCuryCode and y.FDepDurCode = m.FDepDurCode "
					+ " where x.FCashAccCode = "
					+ dbl.sqlString(cashacc.getStrCashAcctCode());
			
			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788

            while (rs.next()) {
                //----------增加现金帐户币种检查 by caocheng 2009.02.05 QDV4.1-BugNO:MS00004----------------------//
                if (rs.getString("FCuryCode") == null || rs.getString("FCuryCode").trim().length() == 0) {
                    throw new YssException("帐户期间【" + rs.getString("FPeriodCode") + "】" +
                                           "的币种设置有误,请检查!");
                }
                //---------------------------------------------------------------------------------------------//
                pay = new CashPecPayBean();
                pay.setTradeDate(allDate);
                pay.setPortCode(rs.getString("FPortCode") + "");
                pay.setInvestManagerCode(analy1 ?
                                         (rs.getString("FAnalysisCode1") + "") :
                                         " ");
                pay.setCategoryCode(analy2 ? (rs.getString("FAnalysisCode2") + "") :
                                    " ");
                pay.setCashAccCode(rs.getString("FCashAccCode") + "");
                pay.setCuryCode(rs.getString("FCuryCode") + "");
                pay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                pay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_DE_RecInterest);

                //基础汇率
                BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FCuryCode"), pay.getPortCode(),
                    YssOperCons.YSS_RATE_BASE);
                //组合汇率
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), pay.getPortCode());
                PortCuryRate = rateOper.getDPortRate();
                //-----------------------------------------------------------------------------------

                pay.setBaseCuryRate(BaseCuryRate);
                pay.setPortCuryRate(PortCuryRate);
                //-----------------增加汇率有效性检查 by caocheng 2009.02.05 QDV4.1-BugNO:MS00004--------//
                if (BaseCuryRate == 0 || PortCuryRate == 0) {
                	//20120320 modified by liubo.Bug #3988
                	//==================================
//                    throw new YssException("帐户期间【" + rs.getString("FPeriodCode") + "】" +
//                                           "的汇率信息有误,请检查!");
                	throw new YssException("【" + rs.getString("FCashAccCode") + "】的帐户期间【" + rs.getString("FPeriodCode") + "】调用的汇率中的基准货币" +
                							"与组合【" + rs.getString("FPortCode") + "】中的基础货币不符，请检查！");
                	//=================end=================
                }
                PeriodBean Period = new PeriodBean();
                Period.setYssPub(pub);
                Period.setPeriodCode(rs.getString("FPeriodCode"));
                //========只有内部计息情况才能设置期间，否则不用检查 sunkey 20090402 QDV4.1-BugNO:MS00004 指示信息的解析处理
                if (rs.getInt("FInterestorigin") == 0 && (rs.getString("FPeriodCode") == null || rs.getString("FPeriodCode").trim().equals(""))) {
                    //异常信息添加前分隔符和【】，用来区分异常类别和突出显示现金账户, 曹丞 sunkey 20090204 QDV4.1-BugNO:MS00004 指示信息的解析处理
                    throw new YssException("请先维护现金帐户【" + pay.getCashAccCode() +
                                           "】的期间设置");
                }

                // 后面要用FPeriodCode条件查询所以不能为空，所以期间代码为null时处理是不对的
                if (Period.getPeriodCode() != null) {
                    Period.getSetting();
                }
                //----fanghaoln 20090715 MS00556 QDV4招商基金2009年06月30日01_B----

                if (rs.getInt("FInterestorigin") == 0) { //内部根据计息公式计算
                    //----------MS00365 QDV4建行2009年4月9日01_B 在中间过程中部需要舍入----------------
                	//by guyichuan 20110520 STORY #561 如果余额<0，采用贷款利率公式
                	if (rs.getString("FFormulaCode") != null||rs.getString("FLoanCode") != null) {
                    	String finterstCode=rs.getString("FFormulaCode");     
                    	if(rs.getDouble("FAccBalance")<0){
                    		if(rs.getString("FLoanCode")==null||rs.getString("FLoanCode").equals("null")||rs.getString("FLoanCode").length()==0){
                    			strException="贷款";
                    			throw new YssException("现金帐户【" + pay.getCashAccCode() +
                                "】没有设置贷款计息公式，不能进行内部公式计算，请检查!");
                    		}
                    		finterstCode=rs.getString("FLoanCode");
                    		//原币
                            dTmpMoney = this.getSettingOper().calMoneyByPerExp(dDate,finterstCode,
                                Math.abs(rs.getDouble("FAccBalance"))); //调用不需要舍入的计算方法
                        
                            pay.setTsfTypeCode("07");
                            pay.setSubTsfTypeCode("07LI");
                    	}else if(rs.getString("FFormulaCode")==null||rs.getString("FFormulaCode").equals("null")||"".equals(rs.getString("FFormulaCode"))){
                    		strException="存款";
                    		throw new YssException("现金帐户【" + pay.getCashAccCode() +
                            "】没有设置存款计息公式，不能进行内部公式计算，请检查!");
                    	}else{
//                    		//原币
                            dTmpMoney = this.getSettingOper().calMoneyByPerExp(dDate,finterstCode,
                                rs.getDouble("FAccBalance"));//调用不需要舍入的计算方法
                            
                    	}
                    	//---end--STORY #561---                    	                    
                    } else {
                        //异常信息添加前分隔符和【】，用来区分异常类别和突出显示现金账户, 曹丞 sunkey 20090204 QDV4.1-BugNO:MS00004 指示信息的解析处理
                        throw new YssException("现金帐户【" + pay.getCashAccCode() +
                                               "】没有设置计息公式，不能进行内部公式计算，请检查!"); //调整异常信息。
                    }
                    //------------------------------------------------------------------------------
                } else if (rs.getInt("FInterestorigin") == 1) { //外部根据存款利率表的利率进行计算
                    dTmpMoney = YssD.mul(rs.getDouble("FAccBalance"),
                                         rs.getDouble("FIntereset"));
                } else if (rs.getInt("FInterestorigin") == 3) { //根据帐户固定利率计算
                    dTmpMoney = YssD.mul(rs.getDouble("FAccBalance"),
                                         rs.getDouble("FFixRate"));
                }
                if (Period.getDayOfYear() == -1) {
                    if (YssFun.isLeapYear(dDate)) {
                        lDays = 366;
                    } else {
                        lDays = 365;
                    }
                } else if (Period.getPeriodType() == 1) { //sj modified 20090124 实际天数 MS00211 QDV4中保2009年01月22日01_A
                    if (YssFun.isLeapYear(dDate)) { //闰年
                        lDays = 366;
                    } else { //平年
                        lDays = 365;
                    }
                } else {
                    lDays = Period.getDayOfYear();
                }
                pay.setMoney(YssD.round(YssD.div(dTmpMoney, lDays), 2)); //为了计算后面的基础货币金额和组合货币金额的准确性，所以原币金额先要保留位数fazmm20070805
                pay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(pay.
                    getMoney(), pay.getBaseCuryRate())); //调用公有的计算基础货币的方法 sunny
                pay.setPortCuryMoney(this.getSettingOper().calPortMoney(pay.
                    getMoney(), pay.getBaseCuryRate(),
                    pay.getPortCuryRate(),
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    pay.getCuryCode(),
                    pay.getTradeDate(),
                    pay.getPortCode()));
                pay.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
                
                alIncomes.add(pay);
                
                //add by jiangshichao 2012.02.28  添加存款利息税处理  start ----------------------------------
                //1.判断是否需要计提利息税
                if(cashacc.getStrInterTax().trim().length()>0&& pay.getMoney()>0){
                	CashPecPayBean  dInterestTax = getInterTax(cashacc,pay);
                    alIncomes.add(dInterestTax);
                }
              //存款利息税处理 end ---------------------------------------------------------------------------------
            }
            return alIncomes;
        }catch(YssException e){
        	throw new YssException(e.getMessage());
        }catch(Exception e) {
            throw new YssException("系统按余额计算现金存款利息时出现异常!\n", e); // by 曹丞 2009.01.23 统计运营应收应付库存异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    
	// 积数法计算利息
	protected ArrayList getHaveBalanceIntrestByJsf(CashAccountBean cashacc,
			Date dDate) throws YssException {
		ArrayList alIncomes = new ArrayList();
		String strSql = "";
		ResultSet rs = null;
		java.util.Date tmpDate = null;
		CashPecPayBean pay = null;
		double dTmpMoney = 0;
		double BaseCuryRate = 0;
		double PortCuryRate = 0;

		boolean analy1; // 判断是否需要用分析代码；杨
		boolean analy2;

		long lDays = 1;
		String strException = "";
		// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090415 --
		EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
		rateOper.setYssPub(pub);
		// -----------------------------------------------------------

		double accumBalan = 0;
		String[] portcodes;
		PeriodBean Period = new PeriodBean();
		Period.setYssPub(pub);
		try {

			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");

			portcodes = portCodes.split(",");
			for (int i = 0; i < portcodes.length; i++) {
				strSql =
				// ---edited by zhouxiang MS01444 计提存款计息只取组合中启用日期最大的
				// by guyichuan 20110520 STORY #561 增加FLoanCode字段
				"select distinct  x.*,FCashAccName,y.FCuryCode,FFormulaCode,FLoanCode,FRoundCode,FPeriodCode,"
						+ "FInterestCycle,FInterestorigin,FInterestWay,FFixRate,FPortName,FPortCury,FIntereset"
						+ " from (" + " select * from  "
						+ pub.yssGetTableName("Tb_Stock_Cash")
						+ " where FStorageDate <= " + dbl.sqlDate(dDate) 
						+ " and FYearMonth not like '%00' "
						+ " and FCheckState = 1 and FPortCode = " + dbl.sqlString(portcodes[i])
						+ " and Fcashacccode = "
						+ dbl.sqlString(cashacc.getStrCashAcctCode())
						+ " ) x left join "
						+
						// -------------------------------
						" (select FCashAccCode,FCashAccName,FCuryCode,FFormulaCode,FLoanCode,FRoundCode,"
						+ " FPeriodCode,FBankCode,FDepDurCode,FInterestCycle,FInterestorigin,FInterestWay,FFixRate from "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ " where FCheckState = 1) y on x.FCashAccCode = y.FCashAccCode "
						+
						// --------------------------------
						// ---------------------edited by zhouxiang
						// MS01444----------
						// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码

						" left join (select FPortCode,FPortName,FPortCury from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						+ "  where FCheckState = 1) z on x.FPortcode = z.FPortcode "
						+
						// end by lidaolong
						// ----------------------------------
						" left join (select m1.FIntereset,m1.FBankCode,m1.FCuryCode,m1.FDepDurCode from "
						+ pub.yssGetTableName("Tb_Para_DepositInterest")
						+ " m1 join (select FBankCode,FCuryCode,FDepDurCode,max(FStartDate) as FStartDate from "
						+ pub.yssGetTableName("Tb_Para_DepositInterest")
						+ " where FStartDate <= "
						+ dbl.sqlDate(dDate)
						+ " and FCheckState = 1 group by FBankCode,FCuryCode,FDepDurCode) m2 on m1.FBankCode = m2.FBankCode "
						+ " and m1.FCuryCode = m2.FCuryCode and m1.FDepDurCode = m2.FDepDurCode and m1.FStartDate = m2.FStartDate "
						+ " ) m on y.FBankCode = m.FBankCode and y.FCuryCode = m.FCuryCode and y.FDepDurCode = m.FDepDurCode "
						+ " where x.FCashAccCode = "
						+ dbl.sqlString(cashacc.getStrCashAcctCode())
						+ " order by x.fstoragedate desc  ";

				rs = dbl.queryByPreparedStatement(strSql); // modify by fangjiang 2011.08.14 STORY #788
				pay = new CashPecPayBean();
				pay.setPortCode(portcodes[i]);
				pay.setTradeDate(dDate);
				pay.setCashAccCode(cashacc.getStrCashAcctCode());
				pay.setCuryCode(cashacc.getStrCurrencyCode());
				pay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
				pay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_DE_RecInterest);
				BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
						cashacc.getStrCurrencyCode(), portcodes[i],
						YssOperCons.YSS_RATE_BASE);
				pay.setBaseCuryRate(BaseCuryRate);
			
				// 组合汇率
				// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090415
				// --------------------------
			
				rateOper.getInnerPortRate(dDate, cashacc.getStrCurrencyCode(),
						 portcodes[i]);
				PortCuryRate = rateOper.getDPortRate();
				pay.setPortCuryRate(PortCuryRate);
				
				while (rs.next()) {
					// ----------增加现金帐户币种检查 by caocheng 2009.02.05
					// QDV4.1-BugNO:MS00004----------------------//
					if (rs.getString("FCuryCode") == null
							|| rs.getString("FCuryCode").trim().length() == 0) {
						throw new YssException("帐户期间【"
								+ rs.getString("FPeriodCode") + "】"
								+ "的币种设置有误,请检查!");
					}
					// ---------------------------------------------------------------------------------------------//
					// pay = new CashPecPayBean();
					
					//
					if(pay.getInvestManagerCode()==null)
					{
						pay.setInvestManagerCode(analy1 ? (rs
								.getString("FAnalysisCode1") + "") : " ");
					}
					if(pay.getCategoryCode()==null)
					{
						pay.setCategoryCode(analy2 ? (rs
								.getString("FAnalysisCode2") + "") : " ");
					}
					if(pay.getStrAttrClsCode().equals(""))
					{
						pay.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
					}
					
					// -----------------增加汇率有效性检查 by caocheng 2009.02.05
					// QDV4.1-BugNO:MS00004--------//
					if (pay.getBaseCuryRate() == 0 || pay.getPortCuryRate() == 0) {
						// 20120320 modified by liubo.Bug #3988
						// ==================================
						// throw new YssException("帐户期间【" +
						// rs.getString("FPeriodCode") + "】" +
						// "的汇率信息有误,请检查!");
						throw new YssException("【"
								+ rs.getString("FCashAccCode") + "】的帐户期间【"
								+ rs.getString("FPeriodCode") + "】调用的汇率中的基准货币"
								+ "与组合【" + rs.getString("FPortCode")
								+ "】中的基础货币不符，请检查！");
						// =================end=================
					}

					
					// ========只有内部计息情况才能设置期间，否则不用检查 sunkey 20090402
					// QDV4.1-BugNO:MS00004 指示信息的解析处理
					if (rs.getInt("FInterestorigin") == 0
							&& (rs.getString("FPeriodCode") == null || rs
									.getString("FPeriodCode").trim().equals(""))) {
						// 异常信息添加前分隔符和【】，用来区分异常类别和突出显示现金账户, 曹丞 sunkey 20090204
						// QDV4.1-BugNO:MS00004 指示信息的解析处理
						throw new YssException("请先维护现金帐户【"
								+ pay.getCashAccCode() + "】的期间设置");
					}
					if(Period.getPeriodCode()==null||!Period.getPeriodCode().equals(rs.getString("FPeriodCode")))
					{
						Period.setPeriodCode(rs.getString("FPeriodCode"));
					// 后面要用FPeriodCode条件查询所以不能为空，所以期间代码为null时处理是不对的
						if (Period.getPeriodCode() != null) {
							Period.getSetting();
						}
						if (Period.getPeriodType() == 1) { // sj modified
							// 20090124 实际天数
							// MS00211
							// QDV4中保2009年01月22日01_A
							if (YssFun.isLeapYear(rs.getDate("FStorageDate"))) { // 闰年
								lDays = 366;
							} else { // 平年
								lDays = 365;
							}
						} else {
							lDays = Period.getDayOfYear();
						}
					}else
					{
						if(Period.getPeriodType() == 1)
						{
							if (YssFun.isLeapYear(rs.getDate("FStorageDate"))) { // 闰年
								lDays = 366;
							} else { // 平年
								lDays = 365;
							}
						}
					}
					// ----fanghaoln 20090715 MS00556
					// QDV4招商基金2009年06月30日01_B----

					if (rs.getInt("FInterestorigin") == 0) { // 内部根据计息公式计算
						// ----------MS00365 QDV4建行2009年4月9日01_B
						// 在中间过程中部需要舍入----------------
						// by guyichuan 20110520 STORY #561 如果余额<0，采用贷款利率公式
						if (rs.getString("FFormulaCode") != null
								|| rs.getString("FLoanCode") != null) {
							String finterstCode = rs.getString("FFormulaCode");
							if (rs.getDouble("FAccBalance") < 0) {
								if (rs.getString("FLoanCode") == null
										|| rs.getString("FLoanCode").equals(
												"null")
										|| rs.getString("FLoanCode").length() == 0) {
									strException = "贷款";
									throw new YssException("现金帐户【"
											+ pay.getCashAccCode()
											+ "】没有设置贷款计息公式，不能进行内部公式计算，请检查!");
								}
								finterstCode = rs.getString("FLoanCode");
								// 原币
								dTmpMoney = this.getSettingOper().calMoneyByPerExp(rs.getDate("FStorageDate"),
												finterstCode,Math.abs(rs.getDouble("FAccBalance"))); // 调用不需要舍入的计算方法

								pay.setTsfTypeCode("07");
								pay.setSubTsfTypeCode("07LI");
							} else if (rs.getString("FFormulaCode") == null
									|| rs.getString("FFormulaCode").equals(
											"null")
									|| "".equals(rs.getString("FFormulaCode"))) {
								strException = "存款";
								throw new YssException("现金帐户【"
										+ pay.getCashAccCode()
										+ "】没有设置存款计息公式，不能进行内部公式计算，请检查!");
							} else {
								// //原币
								dTmpMoney = this.getSettingOper().calMoneyByPerExp(rs.getDate("FStorageDate"), finterstCode,
												rs.getDouble("FAccBalance"));// 调用不需要舍入的计算方法

							}
							// ---end--STORY #561---
						} else {
							// 异常信息添加前分隔符和【】，用来区分异常类别和突出显示现金账户, 曹丞 sunkey
							// 20090204
							// QDV4.1-BugNO:MS00004 指示信息的解析处理
							throw new YssException("现金帐户【"
									+ pay.getCashAccCode()
									+ "】没有设置计息公式，不能进行内部公式计算，请检查!"); // 调整异常信息。
						}
						// ------------------------------------------------------------------------------
					} else if (rs.getInt("FInterestorigin") == 1) { // 外部根据存款利率表的利率进行计算
						dTmpMoney = YssD.mul(rs.getDouble("FAccBalance"), rs
								.getDouble("FIntereset"));
					} else if (rs.getInt("FInterestorigin") == 3) { // 根据帐户固定利率计算
						dTmpMoney = YssD.mul(rs.getDouble("FAccBalance"), rs.getDouble("FFixRate"));
					}
					
				
					accumBalan += YssD.div(dTmpMoney, lDays);
					
				}
				
				dbl.closeResultSetFinal(rs);
				if(dayCount>0)
				{
					dDate = YssFun.addDay(dDate,-dayCount);
				}
				strSql = " select sum(fmoney) as fmoney "
						+ " from "
						+ pub.yssGetTableName("tb_data_cashpayrec")
						+ " t "
						+ " where t.fsubtsftypecode = '06DE' and t.fcashacccode = "
						+ dbl.sqlString(cashacc.getStrCashAcctCode())
						+ " and fportcode = "
						+ dbl.sqlString(portcodes[i])
						+ " and t.ftransdate < "
						+ dbl.sqlDate(dDate)
						+ " and t.fcheckstate = 1" ;
				rs = dbl.openResultSet(strSql);
				if(rs.next())
				{
					double preBalan = 0;
					accumBalan = accumBalan - rs.getDouble("fmoney");
					if(jsfMap.containsKey(cashacc.getStrCashAcctCode()))
					{
						preBalan = (Double)jsfMap.get(cashacc.getStrCashAcctCode());
						accumBalan -= preBalan;
					}
					preBalan += YssD.round(accumBalan,2);
					jsfMap.put(cashacc.getStrCashAcctCode(), preBalan); 
					//baseAccumBalan = baseAccumBalan - rs.getDouble("fbasecurymoney");
					//portAccumBalan = portAccumBalan - rs.getDouble("fportcurymoney");
				}
				pay.setMoney(YssD.round(accumBalan,2));
				pay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(pay.
	                    getMoney(), pay.getBaseCuryRate())); //调用公有的计算基础货币的方法 sunny
	                pay.setPortCuryMoney(this.getSettingOper().calPortMoney(pay.
	                    getMoney(), pay.getBaseCuryRate(),
	                    pay.getPortCuryRate(),
	                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
	                    pay.getCuryCode(),
	                    pay.getTradeDate(),
	                    pay.getPortCode()));
				alIncomes.add(pay);
				
				// add by jiangshichao 2012.02.28 添加存款利息税处理 start
				// ----------------------------------
				// 1.判断是否需要计提利息税
				if (cashacc.getStrInterTax().trim().length() > 0
						&& pay.getMoney() > 0) {
					CashPecPayBean dInterestTax = getInterTax(cashacc, pay);
					alIncomes.add(dInterestTax);
				}
				// 存款利息税处理 end
				// ---------------------------------------------------------------------------------
				dbl.closeResultSetFinal(rs);
			}

			return alIncomes;
		} catch (YssException e) {
			throw new YssException(e.getMessage());
		} catch (Exception e) {
			throw new YssException("系统按余额计算现金存款利息时出现异常!\n", e); // by 曹丞
			// 2009.01.23
			// 统计运营应收应付库存异常信息
			// MS00004
			// QDV4.1-2009.2.1_09A
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}
    
//    private double getInterestBalance(Date date)//获取累计余额 P1 × r1 + ... + Pn * rn
//    {
//    	String strSql = "select * from ";
//    	
//    	return 0;
//    }
//    
//    private double getCountingBalance(Date date)//计提累计余额
//    {
//    	
//    	return 0;
//    }
    
    /**
     * 获取单笔存款的利息
     * @param cashacc CashAccountBean
     * @param dDate Date
     * @return ArrayList
     * @throws YssException
     */
    protected ArrayList getEachSavingInterest(CashAccountBean cashacc,
                                              java.util.Date dDate) throws
        YssException {
        ArrayList alIncomes = new ArrayList();
        double BaseCuryRate = 0; //基础汇率
        double PortCuryRate = 0;
        double dResult = 0; //计算出来的金额
        String sqlDay = "";
        String strSql = "";
        ResultSet rs = null;
        String sqlDate = "";
        CashPecPayBean pay = null; //应收应付BEAN
        boolean analy1; //判断是否存在分析代码
        boolean analy2;
        long lDays = 1;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            PeriodBean Period = new PeriodBean();
            Period.setYssPub(pub);
            Period.setPeriodCode(cashacc.getStrPeriodCode() + "");
            //<--添加判断条件，只有利息来源为内部计息才设置账户期间 MS00603 edited by libo
            //因为只有利息来源为“内部”的时候才要设置期间，因此如果利息来源为内部时，才判断期间为空格或为null
            if (cashacc.getInterestOrigin() == 0 &&
                (Period.getPeriodCode() == null ||
                 Period.getPeriodCode().trim().equalsIgnoreCase("") ||
                 Period.getPeriodCode().trim().equalsIgnoreCase("null"))) {
                throw new YssException("请先维护现金帐户【" + cashacc.getStrCashAcctCode() +
                                       "】的期间设置");
            }
            //End MS00603  定期存款计提利息，期间设置为不可操作状态 QDV4中金2009年7月28日01_B-->

            //Start MS00556 期间代码为null时不可获取期间信息，因为未null时是没有对应期间信息的，而且会报错
            if(Period.getPeriodCode()!=null){
                Period.getSetting();
            }
            //End MS00556 QDV4招商基金2009年06月30日01_B end by sunkey 20090803

            if (Period.getDayInd().equalsIgnoreCase("0")) { //计头不计尾
                sqlDate = " FSavingDate and " + dbl.sqlDateAdd("FMatureDate", "-1");
                sqlDay =
                    ",x.FSavingDate as dStartDate,x.FMatureDate-1 as dEndDate ";
            } else if (Period.getDayInd().equalsIgnoreCase("1")) { //计尾不计头
                sqlDate = dbl.sqlDateAdd("FSavingDate", "+1") + " and FMatureDate";
                sqlDay =
                    ",x.FSavingDate+1 as dStartDate,x.FMatureDate as dEndDate ";
            } else if (Period.getDayInd().equalsIgnoreCase("2")) { //头尾均计
                sqlDate = " FSavingDate and FMatureDate";
                sqlDay =
                    ",x.FSavingDate as dStartDate,x.FMatureDate-1 as dEndDate "; ;
            }
            strSql = "select x.*,y.FIntereset,z.FPortCury" + sqlDay + " from (" +
                " select a.*,b.FCuryCode,b.FBankCode,FFixRate from " +
                pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                " a left join (select FCashAccCode,FBankCode,FCuryCode,FFixRate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) b on a.FCashAccCode = b.FCashAccCode " +
                " where a.FCashAccCode = " +
                dbl.sqlString(cashacc.getStrCashAcctCode()) +
                " and FPortCode in (" + operSql.sqlCodes(portCodes) + ")" + //应该是所有的组合
                " and FCheckState = 1 and " + dbl.sqlDate(dDate) +
                " between " + sqlDate + ") x " +

                " left join (select FPortCode,FPortName,FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) z on x.FPortcode = z.FPortcode " +

                " left join" +
                " (select y1.* from " +
                pub.yssGetTableName("Tb_Para_DepositInterest") +
                " y1 join (select FBankCode,FCuryCode,FDepDurCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_DepositInterest") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " group by FBankCode,FCuryCode,FDepDurCode " +
                ") y2 on y1.FBankCode=y2.FBankCode and y1.FCuryCode=y2.FCuryCode " +
                " and y1.FDepDurCode=y2.FDepDurCode and y1.FStartDate=y2.FStartDate" +
                " ) y on x.FBankCode = y.FBankCode and x.FCuryCode = y.FCuryCode and x.FDepDurCode = y.FDepDurCode";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //------------------增加币种有效性检查 by caocheng 2009.02.05 QDV4.1-BugNO:MS00004---------------//
                if (rs.getString("FCuryCode") == null || rs.getString("FCuryCode").trim().length() == 0) {
                    throw new YssException("现金账户【" + pay.getCashAccCode() + "】" +
                                           "的币种信息有误,请检查!");
                }
                //--------------------------------------------------------------------------------------------//
                pay = new CashPecPayBean();
                if (cashacc.getInterestOrigin() == 1) { //根据维护固定收益，采用倒轧计算fazmm20071020
                    if (YssFun.dateDiff(dDate, rs.getDate("dEndDate")) > 0) { //最后一天等于0
                        dResult =
                            YssD.div(rs.getDouble("frecinterest"),
                                     YssFun.
                                     dateDiff(rs.getDate("dStartDate"),
                                              rs.getDate("dEndDate")) + 1);
                    } else { //最后一天采用倒轧fazmm2007101201
                        dResult = rs.getDouble("frecinterest") -
                            YssD.mul(YssFun.roundIt(YssD.div(rs.getDouble(
                                "frecinterest"),
                            YssFun.
                            dateDiff(rs.getDate("dStartDate"),
                                     rs.getDate("dEndDate")) + 1), 2), YssFun.
                                     dateDiff(rs.getDate("dStartDate"),
                                              rs.getDate("dEndDate")));

                    }
                } else {
                    if (cashacc.getInterestOrigin() == 4) { //根据存款利率表计算
                        dResult =
                            YssD.mul(rs.getDouble("FInMoney"),
                                     rs.getDouble("FIntereset"));
                    } else if (cashacc.getInterestOrigin() == 0) { //根据内部计息公式计算
                        if (rs.getString("FFormulaCode") != null &&
                            rs.getString("FRoundCode") != null) {
                            //alter by sunny  当做了两笔只有存款期限不同的定存业务 他会把计息金额给累计起来 这是不对的
                            dResult =
                                this.getSettingOper().calMoneyByPerExp(rs.getString(
                                    "FFormulaCode"), rs.getString("FRoundCode"),
                                rs.getDouble("FInMoney"), dDate);
                        } else {
                            throw new YssException("存款编号为【" + rs.getString("FNum") +
                                "】的存款没有设置计息公式或舍入方式，不能进行内部公式计算，请检查!");
                        }
                    } else if (cashacc.getInterestOrigin() == 3) { //固定利率
                        dResult =
                            YssD.mul(rs.getDouble("FInMoney"),
                                     rs.getDouble("FFixRate"));
                    }
                    //除以计息天数,得到每日计提金额fazmm20071020
                    if (Period.getDayOfYear() == -1) {
                        if (YssFun.isLeapYear(dDate)) {
                            lDays = 366;
                        } else {
                            lDays = 365;
                        }
                    } else if (Period.getPeriodType() == 1) { //sj modified 20090124 实际天数 MS00211 QDV4中保2009年01月22日01_A
                        if (YssFun.isLeapYear(dDate)) { //闰年
                            lDays = 366;
                        } else { //平年
                            lDays = 365;
                        }
                    } else {
                        lDays = Period.getDayOfYear();
                    }
                    	dResult = YssD.div(dResult, lDays);
                }

                pay.setTradeDate(dDate);
                pay.setPortCode(rs.getString("FPortCode") + "");
                pay.setInvestManagerCode(analy1 ?
                                         (rs.getString("FAnalysisCode1") + "") :
                                         " ");
                pay.setCategoryCode(analy2 ? (rs.getString("FAnalysisCode2") + "") :
                                    " ");
                pay.setCashAccCode(rs.getString("FCashAccCode") + "");
                pay.setCuryCode(rs.getString("FCuryCode") + "");
                pay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                pay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_DE_RecInterest);

                //基础汇率
                BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FCuryCode"), pay.getPortCode(),
                    YssOperCons.YSS_RATE_BASE);
                //组合汇率
//            PortCuryRate = this.getSettingOper().getCuryRate(dDate,
//                  rs.getString("FPortCury"), pay.getPortCode(),
//                  YssOperCons.YSS_RATE_PORT);
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), pay.getPortCode());
                PortCuryRate = rateOper.getDPortRate();
                //-----------------------------------------------------------------------------------
                //------------增加汇率有效性检查 by caocheng 2009.02.05 QDV4.1-BugNO:MS00004----------//
                if (BaseCuryRate == 0 || PortCuryRate == 0) {
                    throw new YssException("现金账户【" + pay.getCashAccCode() + "】" +
                                           "的汇率信息有误,请检查!");
                }
                //---------------------------------------------------------------------------------//
                pay.setBaseCuryRate(BaseCuryRate);
                pay.setPortCuryRate(PortCuryRate);
                pay.setMoney(this.getSettingOper().reckonRoundMoney(rs.getString(
                    "FRoundCode"), dResult)); //为了不出现尾差.根据舍入位数设置.sj eidt 20080618
                int Digit = this.getSettingOper().getRoundDigit(rs.getString(
                    "FRoundCode")); //sj edit 20080807 获取舍入设置的小数位数. 暂无 bug
                pay.setBaseCuryMoney(YssFun.roundIt(YssD.mul(pay.getMoney(),
                    pay.getBaseCuryRate()), Digit)); //通过获取的舍入小数位数来舍入 暂无 bug
//            pay.setPortCuryMoney(YssFun.roundIt(YssD.div(YssD.mul(pay.getMoney(),
//                  pay.getBaseCuryRate()), pay.getPortCuryRate()), Digit)); //通过获取的舍入小数位数来舍入 暂无 bug
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 使用通用的计算组合金额的方法--------------------------
                pay.setPortCuryMoney(this.getSettingOper().calPortMoney(pay.
                    getMoney(), pay.getBaseCuryRate(),
                    pay.getPortCuryRate(),
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    rs.getString("FCuryCode"),
                    dDate,
                    pay.getPortCode(),Digit)); // 加入保留位数条件 modify by wangzuochun 2010.10.27 BUG #198 定存业务现金应收应付金额不对（QDV4太平2010年10月25日01_B） 
                //-----------------------------------------------------------------------------------------------------------
               pay.setRelaNum(rs.getString("fnum"));//add by jsc 20120620 添加关联编号
                
                alIncomes.add(pay);
                
         	   //add by jiangshichao 2012.02.28  添加存款利息税处理  start ----------------------------------
                //1.判断是否需要计提利息税
                if(cashacc.getStrInterTax().trim().length()>0 && pay.getMoney()>0){
                	CashPecPayBean  dInterestTax =   getInterTax(cashacc,pay);
                    alIncomes.add(dInterestTax);
                }
              //存款利息税处理 end ---------------------------------------------------------------------------------
     
            }
            return alIncomes;
        } catch (Exception e) {
            throw new YssException("系统进统计运营应收应付库存时出现异常!\n", e); //by caocheng 2009.01.23 统计运营应收应付库存异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    

    public void saveIncomes(ArrayList alIncome) throws YssException {
        int i = 0;
        CashPayRecAdmin payrec = null;
        CashPecPayBean cashpecpay = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String sTAExc = "";
        //String strMode = ""; // add by wangzuochun 2009.12.30 MS00895
        //---------------
        CtlPubPara pubpara = null;
        boolean isFourDigit = false;
        //---------------
        //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
        String portCode = "";
        Date transDate = null;
        Date logBeginDate = null;
        String accountType = "";
		//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            payrec = new CashPayRecAdmin();
            payrec.setYssPub(pub);
            for (i = 0; i < alIncome.size(); i++) {
                cashpecpay = (CashPecPayBean) alIncome.get(i);
                
                //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
            	logBeginDate = new Date();
            	portCode = cashpecpay.getPortCode();
            	transDate = cashpecpay.getTradeDate();
            	
            	if(hmAccType.get(cashpecpay.getCashAccCode()) != null){
            		accountType = (String)hmAccType.get(cashpecpay.getCashAccCode());
            	}
                logInfo = "现金账户代码:" + cashpecpay.getCashAccCode();
                
                if(cashpecpay.getSubTsfTypeCode().indexOf("07LXS") == -1){
                	logInfo += "\r\n利息:";
                }else{
                	logInfo += "\r\n利息税:";
                }
                
                logInfo += cashpecpay.getMoney();
                //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
                
                cashpecpay.checkStateId = 1; //fanghaoln 20090716 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
                payrec.addList(cashpecpay);
                
                //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, false, 
                		portCode, transDate,transDate,transDate,logInfo,
                		accountType,logBeginDate,logSumCode,new Date());
        		}
                //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
            }

            //------------------------------------------------判断应收应付的小数位是否为保留4位。sj edit 20081010//
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            String digit = pubpara.getKeepFourDigit();
            if (digit.toLowerCase().equalsIgnoreCase("two")) {
                isFourDigit = false;
            } else if (digit.toLowerCase().equalsIgnoreCase("four")) {
                isFourDigit = true;
            }
            //---------------------------------------------------------------------------------------------//
            //-------- add by wangzuochun 2009.12.30 MS00895 --------//
            //strMode = pubpara.getInterestMode();
            // 按其他方式计息
            if (this.modeCode != null && this.modeCode.length() > 0 && this.modeCode.equals("1")) {
            	payrec.getList().clear();
            }
            //---------------------- MS00895 ------------------------//
            payrec.insert(this.beginDate, this.endDate,
                          "06,07"					//modified by guyichuan 20110520 STORY #561 增加贷款帐户及应付贷款利息的支持
                          ,"06DE,07LI,07LXS_DE", selCodes,  //add by jiangshichao 20120228 存款利息税
                          portCodes, "", "", "", 0, isFourDigit);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            //----------------2008.05.06 蒋锦 添加 用于 TA 申购款计息的 动态编译的业务扩展--------------------//
            doTASellInterest();
            //-------- add by wangzuochun 2009.12.30 MS00895 --------//
            if (this.modeCode != null && this.modeCode.length() > 0 && this.modeCode.equals("1")) {
        		return ;
        	}
            //---------------------- MS00895 ------------------------//
            //------------------------------------------------------------------------------------------//
            BaseStgStatDeal secstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("CashPayRec");
            secstgstat.setYssPub(pub);
            secstgstat.setOperType("xjjt"); //add by huangqirong 2013-04-15 bug #7545 操作类型为 现金计提
            secstgstat.setStatCodes(this.selCodes) ; //add by huangqirong 2013-04-15 bug #7545 选中的现金账户
            secstgstat.stroageStat(beginDate, endDate, operSql.sqlCodes(portCodes));
        } catch (Exception e) {
        	//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
        	try{
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, false, 
                		portCode, transDate,transDate, transDate,
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                		(logInfo + "\r\n现金账户计息出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
                		.replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", ""),
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                		accountType, logBeginDate, logSumCode, new Date());
        		}
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	finally{//添加 finally 保证可以抛出异常
        		// by 曹丞 2009.01.23 保存现金计息结果异常信息 MS00004 QDV4.1-2009.2.1_09A
        		throw new YssException("系统保存现金计息结果时出现异常!\n", e); 
        	}
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            if (sTAExc.length() > 0) {
                throw new YssException(sTAExc);
            }
        }
    }
    
    /**
     * modify by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A
     * @throws YssException
     */
    public void doTASellInterest() throws YssException {
        String sTableName = "";
        String strSql = "";
        String strInsert = "";
        int iParaID = 0;
        StringBuffer bufSql = new StringBuffer();
        ResultSet rs = null;
        //------- add by wangzuochun 2010.06.25 MS01322    序号为1的设置被删除以后，系统计提不出来    QDV4国泰2010年6月21日03_B  
        ResultSet rsMax = null;
        int maxID = 0;
        //------MS01322-----//
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        HashMap hmParams = new HashMap();
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------
        try {
            //------------------创建装载网点信息参数的临时表-------------------//
			sTableName = "TB_" + pub.getAssetGroupCode() + "_TMP_TAParam_"
			///**Start---panjunfang 2014-1-9*/
			// 表名拼接用户名代码，若用户名代码较长的情况下，会导致创建临时表报表名过长的错误
			//因此改为取用户id
				+ pub.getUserID();			
			/**End---panjunfang 2014-1-9  */
            if (dbl.yssTableExist(sTableName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("DROP TABLE " + sTableName));
                /**end*/
            }
            bufSql.append("CREATE TABLE " + sTableName + "(");
            bufSql.append(" FPortCode Varchar(20),");
            bufSql.append(" FPoint    Varchar(200),");
            bufSql.append(" FHolidays Varchar(20),");
            bufSql.append(" FDayNum   DECIMAL(3, 0),");
            bufSql.append(" FValue    DECIMAL(12, 6),");
            bufSql.append(" FType     DECIMAL(1, 0),");
            //-----增加启用日期设置 sj modified 20081216 MS00057 ---//
            bufSql.append(" FBeginDate DATE ,");
            //---------------------------------------------------//
            bufSql.append(" FDays     DECIMAL(3, 0),");//MS01247 QDV4易方达2010年5月21日02_A add by jiangshichao 2010.06.08
            //add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A 组合分级代码
            bufSql.append(" FPortClsCode Varchar(20)");
            bufSql.append(")");
            dbl.executeSql(bufSql.toString());
            //------------------------------------------------------------//
            //-----------------从通用参数表和界面配置表中查出参数值并判断FParaID顺序存入临时表中-----------------//
            String sPortCode = "";
            String[] arrPoints = null;
            String sHolidays = null;
            int iDaysOfYear = 0;//年实际天数 add by jiangshichao MS01247 QDV4易方达2010年5月21日02_A
            int iDayNum = 0;
            double dbValue = 0.0;
            int iType = 0;
            boolean flag = false;//申购款是否计息 add by jiangshichao MS01007 QDV4南方2010年3月3日01_A
            //-----增加启用日期设置 sj modified 20081216 MS00057 ---//
            java.util.Date dBeginDate = null;
            //---------------------------------------------------//
            //add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A 组合分级代码
            String portClsCode = "";
            //------- add by wangzuochun 2010.06.25 MS01322    序号为1的设置被删除以后，系统计提不出来    QDV4国泰2010年6月21日03_B  
            String strSqlMax = " select max(FParaID) as FParaID from "
					+ pub.yssGetTableName("TB_PFOper_PUBPARA")
					+ " WHERE FPubParaCode = "
					+ dbl.sqlString(YssCons.YSS_TYYWLX_TASELLINTEREST);
			rsMax = dbl.queryByPreparedStatement(strSqlMax); //modify by fangjiang 2011.08.14 STORY #788
			if (rsMax.next()) {
				maxID = rsMax.getInt("FParaID");
			}
			
			dbl.closeResultSetFinal(rsMax);
			//----------- MS01322 -----------// 
            while (true) {
                //是否能查询到数据的标志
                boolean bHasParams = false;
                //使用 FParaId 字段的累加作为查询条件,如果查询不到数据则跳出查询
                iParaID++;
                //------- add by wangzuochun 2010.06.25 MS01322    序号为1的设置被删除以后，系统计提不出来    QDV4国泰2010年6月21日03_B  
                if (maxID == 0){
                	break;
                }
                
                if(iParaID > maxID){
                	break;
                }
                //------- MS01322 ------//
                strSql = "SELECT b.FCtlGrpCode, b.FCtlCode, b.FCtlType, b.FFunModules, a.FPubParaCode, a.FCtlValue, a.FParaID " +
                    " FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") + " a " +
                    " LEFT JOIN Tb_PFSys_FaceCfgInfo b ON a.FCtlGrpCode = b.FCtlGrpCode" +
                    " AND a.FCtlCode = b.FCtlCode " +
                    " WHERE FPubParaCode = " +
                    dbl.sqlString(YssCons.YSS_TYYWLX_TASELLINTEREST) +
                    " AND FParaID = " + String.valueOf(iParaID);
                rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788 // modify by wangzuochun  2010.06.25 MS01322    序号为1的设置被删除以后，系统计提不出来    QDV4国泰2010年6月21日03_B    
                //------- add by wangzuochun 2010.06.25 MS01322    序号为1的设置被删除以后，系统计提不出来    QDV4国泰2010年6月21日03_B    
                if (rs.next()){
                	rs.beforeFirst();
                }
                else {
                	continue;
                }
                //------ MS01322 ------//
                while (rs.next()) {
                    bHasParams = true;
                    //--- MS01248  QDV4易方达2010年5月21日01_A  TA申购款计息需去掉“是否计息”选项   add by jiangshichao 2010.06.08 ---
                    if (rs.getString("FCtlCode").equalsIgnoreCase("cboInterestMode")) {
                    	flag = rs.getString("FCtlValue").split(",")[0].equalsIgnoreCase("daily");
                    	modeCode = flag?"0":"1";
                    }
                    //--- MS01248  QDV4易方达2010年5月21日01_A  TA申购款计息需去掉“是否计息”选项   end ------------------------------
                    
                    //--- MS01247 QDV4易方达2010年5月21日02_A TA申购款计息中需维护年利率和一年天数 add by jiangshichao 2010.06.07 ---
                    else if(rs.getString("FCtlCode").equalsIgnoreCase("txtYearDays")){
                    	iDaysOfYear = Integer.parseInt(rs.getString("FCtlValue"));
                    }
                    //--- MS01007 QDV4南方2010年3月3日01_A 设置申购款利息计息需改成灵活配置  end ------------------------------
                    else if (rs.getString("FCtlCode").equalsIgnoreCase("cbxType")) {
                        iType = Integer.parseInt(rs.getString("FCtlValue").
                                                 split(",")[0]);
                    } else if (rs.getString("FCtlCode").equalsIgnoreCase(
                        "selNet")) {
                        arrPoints = rs.getString("FCtlValue").split("[|]")[0].split(",");
                    } else if (rs.getString("FCtlCode").equalsIgnoreCase("selHolidays")) {
                        sHolidays = rs.getString("FCtlValue").split("[|]")[0];
                    } else if (rs.getString("FCtlCode").equalsIgnoreCase(
                        "selPort")) {
                        sPortCode = rs.getString("FCtlValue").split("[|]")[0];
                    } else if (rs.getString("FCtlCode").equalsIgnoreCase(
                        "txtDays")) {
                        iDayNum = Integer.parseInt(rs.getString("FCtlValue"));
                    } else if (rs.getString("FCtlCode").equalsIgnoreCase(
                        "txtValue")) {
                        dbValue = Double.parseDouble(rs.getString("FCtlValue"));
                    }
                    //-----增加启用日期设置 sj modified 20081216 MS00057 --------------//
                    else if (rs.getString("FCtlCode").equalsIgnoreCase("dtpBegin")) {
                        dBeginDate = rs.getDate("FCtlValue");
                    }
                    //---------------------------------------------------------------//
                    //---add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A start---//
                    else if (rs.getString("FCtlCode").equalsIgnoreCase("selClsPort")) {
                    	if(rs.getString("FCtlValue").split("[|]").length >= 2){
                    		portClsCode = rs.getString("FCtlValue").split("[|]")[0];
                    	}
                    }
                    //---add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A end---//
                }
                dbl.closeResultSetFinal(rs);
                if (!bHasParams) {
                    break;
                }
                for (int i = 0; i < arrPoints.length; i++) {
                    TASellInteParam taSell = null;
                    //String sKey = arrPoints[i] + "\t" + sPortCode;
                    //-----增加启用日期设置 sj modified 20081216 MS00057 --------------//
					//edit by songjie 2012.08.06 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A 添加 portClsCode 
                    String sKey = arrPoints[i] + "\t" + sPortCode + "\t" + dBeginDate + "\t" + portClsCode;
                    //---------------------------------------------------------------//
                    taSell = (TASellInteParam) hmParams.get(sKey);
                    if (taSell == null) {
                        taSell = new TASellInteParam();
                    } else {
                        if (taSell.paraID > iParaID) {
                            continue;
                        }
                    }
                    taSell.iDaysOfYear = iDaysOfYear;// add by jiangshichao 2010.06.08
                    taSell.dayNum = iDayNum;
                    taSell.paraID = iParaID;
                    taSell.points = arrPoints[i];
                    taSell.value = dbValue;
                    taSell.type = iType;
                    taSell.portCode = sPortCode;
                    taSell.holidays = sHolidays;
                    //-----增加启用日期设置 sj modified 20081216 MS00057 -//
                    taSell.dBeginDate = dBeginDate;
                    //-------------------------------------------------//
                    //add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A 组合分级代码
                    taSell.portClsCode = portClsCode;
                    hmParams.put(sKey, taSell);
                }
            }
            //-----增加启用日期设置 sj modified 20081216 MS00057 --------------//
            //edit by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A 添加字段 FPortClsCode
            strInsert = "INSERT INTO " + sTableName + " VALUES(?,?,?,?,?,?,?,?,?)";
            //---------------------------------------------------------------//
            Iterator it = hmParams.values().iterator();
            conn.setAutoCommit(false);
            bTrans = true;
            PreparedStatement pstmt = conn.prepareStatement(strInsert);
            while (it.hasNext()) {
                TASellInteParam taSell = (TASellInteParam) it.next();
                pstmt.setString(1, taSell.portCode);
                pstmt.setString(2, taSell.points);
                pstmt.setString(3, taSell.holidays);
                pstmt.setInt(4, taSell.dayNum);
                pstmt.setDouble(5, taSell.value);
                pstmt.setInt(6, taSell.type);
                //------- MS00057 -----------------------------------//
                pstmt.setDate(7, YssFun.toSqlDate(taSell.dBeginDate));
                //---------------------------------------------------//
                pstmt.setInt(8,taSell.iDaysOfYear);
                // add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A
                pstmt.setString(9, taSell.portClsCode);
                pstmt.executeUpdate();
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeStatementFinal(pstmt);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------------------------------------------------------------------------------------//
            //储存所有天数的应收应付记录
            HashMap hmPecPay = new HashMap();
            //add by xuqiji 20090513:QDV4海富通2009年05月11日02_AB MS00441  为直销申购款计息时取数建立一张临时表-----------
            Hashtable taInterestSource = null;
            String tableTem = ""; //存放TA直销申购的临时表
            String portCodesTem = ""; //存放TA直销申购的组合代码
            //String strMode = ""; // add by wangzuochun 2009.12.30 MS00895
            //新建通用参数类
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            //根据组合代码获取控件中该组合对应的临时表，Key:portCodes,value:表名，
            taInterestSource = pubPara.getTAInterestSource(this.portCodes);
            Enumeration taIS = taInterestSource.keys(); //根据Keys遍历Hash表
            while (taIS.hasMoreElements()) {
                portCodesTem = (String) taIS.nextElement(); //把遍历的Keys值赋值给临时变量
                tableTem = (String) taInterestSource.get(portCodesTem); //根据组合代码获取它的Values
                
                //------ add by wangzuochun 2009.12.30 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A 
                //strMode = pubPara.getInterestMode();

                // 按其他方式计息
                if (this.modeCode != null && this.modeCode.length() >0 && this.modeCode.equals("1")) {
                	CalculateTaInterest(sTableName, hmPecPay, portCodesTem, tableTem);
                }
                // 按日计息
                else {
                	getCalaInterest(sTableName, hmPecPay, portCodesTem, tableTem);
                }
                //------ MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A
                
//                if (tableTem != null && !"".equals(tableTem.trim())) { //判断是否组合代码有对应的表
//                    getCalaInterest(sTableName, hmPecPay, portCodesTem, tableTem);
//                } else {
//                    getCalaInterest(sTableName, hmPecPay, portCodesTem, tableTem);
//                }
            }
            //------------------------------end 20090514--------------------------------------------------------//
            
            if (this.modeCode != null && this.modeCode.length() > 0 && this.modeCode.equals("0")) {

				Iterator itTA = hmPecPay.values().iterator();
				CashPayRecAdmin payrecTA = new CashPayRecAdmin();
				payrecTA.setYssPub(pub);
				while (itTA.hasNext()) {
					payrecTA.addList((CashPecPayBean) itTA.next());
				}

				conn.setAutoCommit(false);
				bTrans = true;
				payrecTA.insert(this.beginDate, this.endDate,
						YssOperCons.YSS_ZJDBLX_Rec,
						YssOperCons.YSS_ZJDBZLX_PF_REC, "", portCodes, "", "",
						"", 0);
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsMax); // add by wangzuochun  2010.06.25 MS01322    序号为1的设置被删除以后，系统计提不出来    QDV4国泰2010年6月21日03_B    
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     *
     * <p>Title: </p>
     *
     * <p>Description: 参数临时表的实体类</p>
     *
     * <p>Copyright: Copyright (c) 2006</p>
     *
     * <p>Company: </p>
     *
     * @author not attributable
     * @version 1.0
     */
    class TASellInteParam {
        TASellInteParam() {}

        public String portCode = "";
        public String points = "";
        public String holidays = "";
        public int dayNum;
        public double value;
        public int type;
        public int paraID;
        //-----增加启用日期设置 sj modified 20081216 MS00057 ---//
        public java.util.Date dBeginDate = null;
        //---------------------------------------------------//
        public int iDaysOfYear;
        //add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A 组合分级代码
        public String portClsCode = "";
    }

    /**
     * 获取计算利息的方法，其中增加判断：1.从正式表中获取数据，2.从临时表中获取数据 参数为：1.portCodesTem 2.tableTem
     * @param sTableName String 创建的装载网点信息参数的临时表
     * @param hmPecPay HashMap 传入一个HashMap
     * @param portCodesTem String 组合代码，可能有多个，格式类似为：“001，002，003”
     * @param tableTem String 传入的表名：1.系统正式表 2.临时表--此临时表通过通用参数设置的表
     * @return HashMap 返回一个HashMap
     * @throws YssException 异常
     * add by xuqiji 20090514:QDV4海富通2009年05月11日02_AB MS00441  为直销申购款计息时取数建立一张临时表
     */
    public HashMap getCalaInterest(String sTableName, HashMap hmPecPay, String portCodesTem, String tableTem) throws YssException {
        //循环日期
        int iDay = YssFun.dateDiff(this.beginDate, this.endDate);
        //当前记息日期
        java.util.Date dCurrDay = this.beginDate;
        //基础汇率
        double BaseCuryRate = 0.0;
        //组合汇率
        double PortCuryRate = 0.0;
        //保存sql语句
        String strSql = "";
        //声明游标
        ResultSet rs = null;
        //新建获取利率的通用类
        EachRateOper rateOper = new EachRateOper();
        //放到全局变量中
        rateOper.setYssPub(pub);
        try {
            for (int i = 0; i <= iDay; i++) {
                strSql = "SELECT a.FPortCode, a.FAnalysisCode1, a.FAnalysisCode2, a.FAnalysisCode3, a.FCashAccCode, a.FCuryCode, SUM(FSellMoney) AS FMoney, b.FValue, c.FPortCury, b.FDayNum, a.FSettleDate, b.FType, b.FHolidays,b.FDays " +
                    " FROM (SELECT * FROM " +
                    (!"".equals(tableTem.trim()) ? tableTem : pub.yssGetTableName("Tb_Ta_Trade")) +
                    " WHERE FCheckState = 1 AND FSellType = '01' AND FPortCode IN (" +
                    operSql.sqlCodes(portCodesTem) 
                    + ") and FCashAccCode in (" +  operSql.sqlCodes(selCodes) + ") ) a" +  //modify by fangjiang 2010.12.14 BUG #703
                    " JOIN (SELECT * FROM " + sTableName +
                    " WHERE FPortCode IN (" + operSql.sqlCodes(portCodesTem) +
                    " ) and FBeginDate = (select max(FBeginDate) as FBeginDate from " + sTableName +
                    " where FBeginDate <= " + dbl.sqlDate(YssFun.addDay(this.beginDate, i)) + ")" +
                    ") b ON a.FSellNetCode = b.FPoint" +
                    " and a.FPortCode = b.FPortCode " + //add by yeshenghong 6432 20121130
                    //----------------------------------------------------------------------------------------//
                    //-----------------MS00227 QDV4赢时胜上海2009年2月4日02_B  sj modified ---------------------//
                	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
               
                    
                    " left join " +
                    " (SELECT  FPortCode, FPortCury " +
                    " from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where FCheckState = 1 ) c ON a.FPortCode = c.FPortCode" +
                    
                    //end by lidaolong
                    " WHERE " + dbl.sqlDate(dCurrDay) +
                    " BETWEEN a.FConfimDate AND a.FSettleDate" +
                    //2008.06.25 蒋锦 修改 将交易记录的结算日期和延迟天数添加为分组条件
                    " GROUP BY b.FValue, b.FPoint, a.FAnalysisCode1, a.FAnalysisCode2, a.FAnalysisCode3, a.FCashAccCode, a.FCuryCode, a.FPortCode, a.FSettleDate, b.FDayNum, b.FType, c.FPortCury, b.FHolidays,b.FDays";
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    //从结算日期，往前推延迟天数之前最近的工作日日期
                    java.util.Date dWorkDate = null;
                    //结算日期
                    java.util.Date dSettleDate = rs.getDate("FSettleDate");

                    //判断计头不计尾，还是计尾部计头，计算实际结算日期和记息区间
                    if (rs.getInt("FType") == 0) { //表示计头不计尾
                        dSettleDate = YssFun.addDay(dSettleDate, -1);
                        dWorkDate = this.getSettingOper().getWorkDay(rs.getString(
                            "FHolidays"), rs.getDate("FSettleDate"),
                            rs.getInt("FDayNum") *  -1);
                    } else {
                        dWorkDate = this.getSettingOper().getWorkDay(rs.getString(
                            "FHolidays"), rs.getDate("FSettleDate"),
                            rs.getInt("FDayNum") * -1);
                        dWorkDate = YssFun.addDay(dWorkDate, 1);
                    }
                    //判断记息日期是否在记息区间中
                    if (dCurrDay.compareTo(dWorkDate) >= 0 &&
                        dCurrDay.compareTo(dSettleDate) <= 0) {

                        //将应收应付数据存放在哈希表中，将相同记息日期、组合、分析代码、帐户代码的应收金额累加在一条应收记录中
                        CashPecPayBean pay = null;
                        String sKey = rs.getString("FPortCode") + "\t" +
                            rs.getString("FAnalysisCode1") + "" + "\t" +
                            rs.getString("FAnalysisCode2") + "" + "\t" +
                            rs.getString("FCashAccCode") + "\t" +
                            rs.getString("FCuryCode") + "\t" +
                            YssFun.formatDate(dCurrDay, "yyyy-MM-dd");
                        pay = (CashPecPayBean) hmPecPay.get(sKey);
                        if (pay == null) {
                            pay = new CashPecPayBean();
                            pay.setTradeDate(dCurrDay);
                            pay.setPortCode(rs.getString("FPortCode") + "");
                            pay.setInvestManagerCode(rs.getString("FAnalysisCode1") + "");
                            pay.setCategoryCode(rs.getString("FAnalysisCode2") + "");
                            pay.setCashAccCode(rs.getString("FCashAccCode") + "");
                            pay.setCuryCode(rs.getString("FCuryCode") + "");
                            pay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                            pay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_PF_REC);
                            //基础汇率
                            BaseCuryRate = this.getSettingOper().getCuryRate(
                                dCurrDay, rs.getString("FCuryCode"), pay.getPortCode(),
                                YssOperCons.YSS_RATE_BASE);
                            //组合汇率
                            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                            rateOper.getInnerPortRate(dCurrDay,
                                rs.getString("FCuryCode"),
                                pay.getPortCode());
                            PortCuryRate = rateOper.getDPortRate();
                            //-----------------------------------------------------------------------------------
                            pay.checkStateId = 1; //计提出的数据放入已审核 MS00642:QDV4赢时胜（上海）2009年8月20日01_B sunkey@modify 20090820
                            pay.setBaseCuryRate(BaseCuryRate);
                            pay.setPortCuryRate(PortCuryRate);
                            pay.setMoney(YssD.round(YssD.mul(rs.getDouble("FMoney"),
                                    YssD.div(rs.getDouble("FValue"), rs.getInt("FDays"))), 2));// add by jiangshichao
//                            pay.setMoney(YssD.round(YssD.mul(rs.getDouble("FMoney"),
//                                rs.getDouble("FValue")), 2));
                            pay.setBaseCuryMoney(YssD.mul(pay.getMoney(),
                                pay.getBaseCuryRate()));
                            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 使用通用的计算组合金额的方法--------------------------
                            pay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                                pay.
                                getMoney(), pay.getBaseCuryRate(),
                                pay.getPortCuryRate(),
                                rs.getString("FCuryCode"),
                                dCurrDay,
                                pay.getPortCode()));
                            //-----------------------------------------------------------------------------------------------------------

                            hmPecPay.put(sKey, pay);
                        } else {
                            //基础汇率
                            BaseCuryRate = this.getSettingOper().getCuryRate(
                                dCurrDay, rs.getString("FCuryCode"), pay.getPortCode(),
                                YssOperCons.YSS_RATE_BASE);
                            //组合汇率
                            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                            rateOper.getInnerPortRate(dCurrDay, rs.getString("FCuryCode"), pay.getPortCode());
                            PortCuryRate = rateOper.getDPortRate();
                            //-----------------------------------------------------------------------------------
                            pay.checkStateId = 1; //计提出的数据放入已审核 MS00642:QDV4赢时胜（上海）2009年8月20日01_B sunkey 20090820
                            
                            //------ 金额没有除以天数 modify by wangzuochun  2010.08.30  MS01661    TA申购款计息金额不正确    QDV4赢时胜深圳2010年8月30日01_B  
                            pay.setMoney(pay.getMoney() +
                                         YssD.round(YssD.mul(rs.getDouble("FMoney"), YssD.div(rs.getDouble("FValue"), rs.getInt("FDays"))), 2));
                            
                            //-------------------------------------MS01661--------------------------------------------//
                            pay.setBaseCuryMoney(YssD.mul(pay.getMoney(), pay.getBaseCuryRate()));
                            pay.setPortCuryMoney(YssD.div(YssD.mul(pay.getMoney(),
                                pay.getBaseCuryRate()), pay.getPortCuryRate()));
                        }
                    }
                }
                dCurrDay = YssFun.addDay(dCurrDay, 1);
                dbl.closeResultSetFinal(rs); //关闭游标,add by xuxuming,20091021.循环体内游标必须关闭，否则多次循环会报错
            }
        } catch (Exception e) {
            throw new YssException("获取计算利息出错！\r\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmPecPay;
    }
    
//MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj  以下为定存计息的新的处理方法-------------------------------------------------------------------------------------------------------------------------
    /**
     * 计算定存利息
     * @param cashacc CashAccountBean
     * @param dDate Date
     * @return ArrayList
     * @throws YssException
     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
     */
    private ArrayList calcInnerSavingInterest(CashAccountBean cashacc, java.util.Date dDate) throws YssException {
        ArrayList income = new ArrayList();
        ResultSet rs = null;
        String sql = null;
        CashPecPayBean cashPecpay = null;
        try {
            sql = builderSavingSql(cashacc, dDate); //获取定存的sql语句
            rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                cashPecpay = setEachInterest(rs, dDate, cashacc); //设置定存利息的数据
                //edit by lidaolong #526 QDV4长信基金2011年1月14日01_A
                if (cashPecpay.getMoney() !=0 ){
                	   income.add(cashPecpay);
                	   //add by jiangshichao 2012.02.28  添加存款利息税处理  start ----------------------------------
                       //1.判断是否需要计提利息税
                       if(cashacc.getStrInterTax().trim().length()>0 && cashPecpay.getMoney()>0){
                       	CashPecPayBean  dInterestTax =   getInterTax(cashacc,cashPecpay);
                       	income.add(dInterestTax);
                       }
                     //存款利息税处理 end ---------------------------------------------------------------------------------
                }
             //end ---
            }
        } catch (Exception e) {
            throw new YssException("计算定存每日利息出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return income;
    }
    
//  //MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj  以下为定存计息的新的处理方法-------------------------------------------------------------------------------------------------------------------------
//    /**
//     * 计算定存利息
//     * @param cashacc CashAccountBean
//     * @param dDate Date
//     * @return ArrayList
//     * @throws YssException
//     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
//     */
//    private ArrayList calcInnerSavingInterestByJsf(CashAccountBean cashacc, java.util.Date dDate) throws YssException {
//        ArrayList income = new ArrayList();
//        ResultSet rs = null;
//        String sql = null;
//        CashPecPayBean cashPecpay = null;
//        try {
//            sql = builderSavingSqlByJsf(cashacc, dDate); //获取定存的sql语句
//            rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
//            while (rs.next()) {
//                cashPecpay = setEachInterestJsf(rs, dDate, cashacc); //设置定存利息的数据
//                //edit by lidaolong #526 QDV4长信基金2011年1月14日01_A
//                if (cashPecpay.getMoney() !=0 ){
//                	   income.add(cashPecpay);
//                	   //add by jiangshichao 2012.02.28  添加存款利息税处理  start ----------------------------------
//                       //1.判断是否需要计提利息税
//                       if(cashacc.getStrInterTax().trim().length()>0 && cashPecpay.getMoney()>0){
//                       	CashPecPayBean  dInterestTax =   getInterTax(cashacc,cashPecpay);
//                       	income.add(dInterestTax);
//                       }
//                     //存款利息税处理 end ---------------------------------------------------------------------------------
//                   	
//                }
//             //end ---
//                if(cashacc.getInterestAlg()==1)//积数法 存值到现金应收应付中
//                {
//                	CashPecPayBean  interestBal = (CashPecPayBean) cashPecpay.clone(); 
//                	interestBal.setSubTsfTypeCode("06DE01");
//                	interestBal.setMoney(accumInterBal); //为了计算后面的基础货币金额和组合货币金额的准确性，所以原币金额先要保留位数fazmm20070805
//                	interestBal.setBaseCuryMoney(this.getSettingOper().calBaseMoney(interestBal.
//                         getMoney(), interestBal.getBaseCuryRate())); //调用公有的计算基础货币的方法 sunny
//                	interestBal.setPortCuryMoney(this.getSettingOper().calPortMoney(interestBal.
//                         getMoney(), interestBal.getBaseCuryRate(),
//                         interestBal.getPortCuryRate(),
//                         interestBal.getCuryCode(),
//                         interestBal.getTradeDate(),
//                         interestBal.getPortCode()));
//                	income.add(interestBal);
//                	CashPecPayBean  interestSum = (CashPecPayBean) cashPecpay.clone(); 
//                	interestBal.setSubTsfTypeCode("06DE02");
//                	interestBal.setMoney(accumInterSum); //为了计算后面的基础货币金额和组合货币金额的准确性，所以原币金额先要保留位数fazmm20070805
//                	interestBal.setBaseCuryMoney(this.getSettingOper().calBaseMoney(interestBal.
//                         getMoney(), interestBal.getBaseCuryRate())); //调用公有的计算基础货币的方法 sunny
//                	interestBal.setPortCuryMoney(this.getSettingOper().calPortMoney(interestBal.
//                         getMoney(), interestBal.getBaseCuryRate(),
//                         interestBal.getPortCuryRate(),
//                         interestBal.getCuryCode(),
//                         interestBal.getTradeDate(),
//                         interestBal.getPortCode()));
//                	income.add(interestSum);
//                }
//            }
//        } catch (Exception e) {
//            throw new YssException("计算定存每日利息出现异常！", e);
//        } finally {
//            dbl.closeResultSetFinal(rs);
//        }
//        return income;
//    }

    /**
     * 设置国内存款业务的应收应付数据设置
     * @param rs ResultSet
     * @return CashPecPayBean
     * @throws YssException
     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
     */
    private CashPecPayBean setEachInterest(ResultSet rs, java.util.Date dDate, CashAccountBean cashacc) throws YssException {
        double dBaseCuryRate = 0D;
        double dPortCuryRate = 0D;
        boolean analy1 = false;
        boolean analy2 = false;
        double dResult = 0D;
        CashPecPayBean cashPecpay = null;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        double accumBalan = 0;
		int dayCount = 0;
		double preBal = 0;
//		double preBaseBal = 0;
//		double prePortBal = 0;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            cashPecpay = new CashPecPayBean();
            cashPecpay.setTradeDate(dDate);
            cashPecpay.setPortCode(rs.getString("FPortCode"));

            cashPecpay.setInvestManagerCode(analy1 ?
                                            (rs.getString("FAnalysisCode1") + "") :
                                            " ");
            cashPecpay.setCategoryCode(analy2 ? (rs.getString("FAnalysisCode2") + "") :
                                       " ");

            cashPecpay.setCashAccCode(rs.getString("FInterestAccCode")); //利息账户
            //selCodes
            if (!rs.getString("FInterestAccCode").equalsIgnoreCase(rs.getString("FCashAccCode"))) { //当利息账户和定存账户不同时
                if (selCodes.indexOf(rs.getString("FInterestAccCode")) < 0) { //当前台传入的账户中没有利息帐号
                    selCodes = selCodes + "," + rs.getString("FInterestAccCode"); //添加此利息账户，以便删除
                }
            }
            cashPecpay.setCuryCode(rs.getString("FCuryCode") + "");
            cashPecpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
            cashPecpay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_DE_RecInterest);

            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
            cashPecpay.setStrAttrClsCode(rs.getString("fattrclscode"));
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//    
            if(cashacc.getInterestAlg()!=1)
            {
            	/******************在此处计算定存每日利息*******************/
                dResult = calcSavingEachInterest(rs, cashacc, dDate); //计算每日利息
                /********************************************************/
            }else
            {
            	for(Date operDate=rs.getDate("FSavingDate");operDate.before(dDate);operDate=YssFun.addDay(operDate, 1))
            	{
            		dayCount++;
            		if(dResult==0)
            		{
            			dResult = calcSavingEachInterest(rs, cashacc, operDate);
            		}
            	}
            	//preBal = this.getSettingOper().reckonRoundMoney(rs.getString("FRoundCode"), dResult);
            	preBal += dResult * dayCount;
            	dResult = dResult * dayCount + calcSavingEachInterest(rs, cashacc, dDate);
            	
            	preBal = this.getSettingOper().reckonRoundMoney(rs.getString("FRoundCode"), preBal);
            	accumBalan = dResult - preBal;
            }
          

            //基础汇率
            dBaseCuryRate = this.getSettingOper().getCuryRate(dDate,
                rs.getString("FCuryCode"), cashPecpay.getPortCode(),
                YssOperCons.YSS_RATE_BASE);
            //组合汇率
            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), cashPecpay.getPortCode());
            dPortCuryRate = rateOper.getDPortRate();

            if (dBaseCuryRate == 0 || dPortCuryRate == 0) {
                throw new YssException("现金账户【" + cashPecpay.getCashAccCode() + "】" +
                                       "的汇率信息有误,请检查!");
            }

            cashPecpay.setBaseCuryRate(dBaseCuryRate);
            cashPecpay.setPortCuryRate(dPortCuryRate);
            if (null == rs.getString("FRoundCode")) {//增加对存款业务中舍人设置的判断，若没有设置则进行提示。
                throw new YssException("存款业务中舍入设置" +
                                       "信息有误,请检查！");
            }
            int Digit = this.getSettingOper().getRoundDigit(rs.getString(
            "FRoundCode")); // 获取舍入设置的小数位数.
            if(cashacc.getInterestAlg()!=1)
            {
	            cashPecpay.setMoney(this.getSettingOper().reckonRoundMoney(rs.getString("FRoundCode"), dResult));//为了不出现尾差.根据舍入位数设置. 
	        }else
            {
            	cashPecpay.setMoney(this.getSettingOper().reckonRoundMoney(rs.getString("FRoundCode"), accumBalan));//为了不出现尾差.根据舍入位数设置. 
//	            
//	            cashPecpay.setBaseCuryMoney(YssFun.roundIt(baseAccumBalan, Digit)); //通过获取的舍入小数位数来舍入
//	
//	            cashPecpay.setPortCuryMoney(YssFun.roundIt(portAccumBalan, Digit)); // 加入保留位数条件 modify by wangzuochun 2010.10.27 BUG #198 定存业务现金应收应付金额不对（QDV4太平2010年10月25日01_B） 
  
            }
            cashPecpay.setBaseCuryMoney(YssFun.roundIt(YssD.mul(cashPecpay.getMoney(),
	                cashPecpay.getBaseCuryRate()), Digit)); //通过获取的舍入小数位数来舍入
	
            cashPecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(cashPecpay.
	                getMoney(), cashPecpay.getBaseCuryRate(),
	                cashPecpay.getPortCuryRate(),
	
	                rs.getString("FCuryCode"),
	                dDate,
	                cashPecpay.getPortCode(),Digit)); // 加入保留位数条件 modify by wangzuochun 2010.10.27 BUG #198 定存业务现金应收应付金额不对（QDV4太平2010年10月25日01_B） 

            cashPecpay.checkStateId = 1; //设置为已审核
            //edited by MS01455    当天有多笔定存业务“到期”、“首期”时，业务处理产生的利息收入资金调拨金额不对  -------------------------------------
            /**shashijie 2012-7-24 STORY 2796 注释掉判断,计提产生的现金应收数据都要存入关联的定存编号,为了开发提前支取利息功能*/
            /*boolean flag=SearchFirstFrelnum(rs);
			if (flag) {*/
				cashPecpay.setRelaNum(rs.getString("FNum"));
			//}
			/**end*/
            //end ---------------by MS01455    2010.7.24 ---当天有多笔定存业务“到期”、“首期”时，业务处理产生的利息收入资金调拨金额不对  ------------
        } catch (Exception e) {
            throw new YssException("设置定存利息数据出现异常！", e);
        }
        return cashPecpay;
    }
    
//    /**
//     * 设置国内存款业务的应收应付数据设置
//     * @param rs ResultSet
//     * @return CashPecPayBean
//     * @throws YssException
//     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
//     */
//    private CashPecPayBean setEachInterestJsf(ResultSet rs, java.util.Date dDate, CashAccountBean cashacc) throws YssException {
//        double dBaseCuryRate = 0D;
//        double dPortCuryRate = 0D;
//        boolean analy1 = false;
//        boolean analy2 = false;
//        double dResult = 0D;
//        CashPecPayBean cashPecpay = null;
//        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
//        rateOper.setYssPub(pub);
//        double accumBalan = 0;
//		double portAccumBalan = 0;
//		double baseAccumBalan = 0; 
//		ResultSet operDateRs;
//		String strSql = "";
//        try {
//            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
//            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
//            cashPecpay = new CashPecPayBean();
//            cashPecpay.setTradeDate(dDate);
//            cashPecpay.setPortCode(rs.getString("FPortCode"));
//
//            cashPecpay.setInvestManagerCode(analy1 ?
//                                            (rs.getString("FAnalysisCode1") + "") :
//                                            " ");
//            cashPecpay.setCategoryCode(analy2 ? (rs.getString("FAnalysisCode2") + "") :
//                                       " ");
//
//            cashPecpay.setCashAccCode(rs.getString("FInterestAccCode")); //利息账户
//            //selCodes
//            if (!rs.getString("FInterestAccCode").equalsIgnoreCase(rs.getString("FCashAccCode"))) { //当利息账户和定存账户不同时
//                if (selCodes.indexOf(rs.getString("FInterestAccCode")) < 0) { //当前台传入的账户中没有利息帐号
//                    selCodes = selCodes + "," + rs.getString("FInterestAccCode"); //添加此利息账户，以便删除
//                }
//            }
//            cashPecpay.setCuryCode(rs.getString("FCuryCode") + "");
//            cashPecpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
//            cashPecpay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_DE_RecInterest);
//
//            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
//            cashPecpay.setStrAttrClsCode(rs.getString("fattrclscode"));
//            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//  
//            
//            /******************在此处计算定存每日利息*******************/
//            dResult = calcSavingEachInterest(rs, cashacc, dDate); //计算每日利息
//            /********************************************************/
//
//            //基础汇率
//            dBaseCuryRate = this.getSettingOper().getCuryRate(dDate,
//                rs.getString("FCuryCode"), cashPecpay.getPortCode(),
//                YssOperCons.YSS_RATE_BASE);
//            //组合汇率
//            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), cashPecpay.getPortCode());
//            dPortCuryRate = rateOper.getDPortRate();
//
//            if (dBaseCuryRate == 0 || dPortCuryRate == 0) {
//                throw new YssException("现金账户【" + cashPecpay.getCashAccCode() + "】" +
//                                       "的汇率信息有误,请检查!");
//            }
//
//            cashPecpay.setBaseCuryRate(dBaseCuryRate);
//            cashPecpay.setPortCuryRate(dPortCuryRate);
//            if (null == rs.getString("FRoundCode")) {//增加对存款业务中舍人设置的判断，若没有设置则进行提示。
//                throw new YssException("存款业务中舍入设置" +
//                                       "信息有误,请检查！");
//            }
//            
//            cashPecpay.setMoney(this.getSettingOper().reckonRoundMoney(rs.getString("FRoundCode"), dResult));//为了不出现尾差.根据舍入位数设置. 
//            int Digit = this.getSettingOper().getRoundDigit(rs.getString(
//                "FRoundCode")); // 获取舍入设置的小数位数.
//            cashPecpay.setBaseCuryMoney(YssFun.roundIt(YssD.mul(cashPecpay.getMoney(),
//                cashPecpay.getBaseCuryRate()), Digit)); //通过获取的舍入小数位数来舍入
//
//            cashPecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(cashPecpay.
//                getMoney(), cashPecpay.getBaseCuryRate(),
//                cashPecpay.getPortCuryRate(),
//
//                rs.getString("FCuryCode"),
//                dDate,
//                cashPecpay.getPortCode(),Digit)); // 加入保留位数条件 modify by wangzuochun 2010.10.27 BUG #198 定存业务现金应收应付金额不对（QDV4太平2010年10月25日01_B） 
//            cashPecpay.checkStateId = 1; //设置为已审核
//            //edited by MS01455    当天有多笔定存业务“到期”、“首期”时，业务处理产生的利息收入资金调拨金额不对  -------------------------------------
//            boolean flag=SearchFirstFrelnum(rs);
//			if (flag) {
//				cashPecpay.setRelaNum(rs.getString("FNum"));
//			}
//            //end ---------------by MS01455    2010.7.24 ---当天有多笔定存业务“到期”、“首期”时，业务处理产生的利息收入资金调拨金额不对  ------------
//        } catch (Exception e) {
//            throw new YssException("设置定存利息数据出现异常！", e);
//        }
//        return cashPecpay;
//    }

    /**判断是否是最后一天计提*/
    private boolean isOverDay(ResultSet rs) throws YssException , SQLException {
    	//如果是记头不计尾
    	if (rs.getInt("FDayInd")==0) {
    		//到期日前一天就是实际最后一天
    		if (YssFun.addDay(rs.getDate("FMatureDate"), -1).equals(this.allDate)) {
				return true;
			}
		}else {
			if (rs.getDate("FMatureDate").equals(this.allDate)) {
				return true;
			}
		}
		return false;
    }
    

	/**
     * 
     * @throws  
     * @方法名：SearchFirstFrelnum
     * edited by MS01455    当天有多笔定存业务“到期”、“首期”时，业务处理产生的利息收入资金调拨金额不对  -------------------------------------
     * @参数:ResultSet rs 定存业务交易数据结果
     * @返回类型：boolean 用于判断是否对现金应收应付里面的进行sRelaNum插入操作
     * @说明：TODO
     */
  
	private boolean SearchFirstFrelnum(ResultSet rs) throws YssException {
		ResultSet subrs = null;
		String figureType = "0";
		try {

			figureType = rs.getString("FDayInd") == null ? "0" : rs
					.getString("FDayInd");
			String strSql = "select * from "
					+ pub.yssGetTableName("tb_data_cashpayrec")
					+ " where ftransdate = "
					+ (figureType.equals("0") ? dbl.sqlDate(rs
							.getDate("FSavingDate")) : dbl.sqlDate(YssFun
							.addDay(rs.getDate("FSavingDate"), 1)))
					+ " and fportcode ="
					+ dbl.sqlString(rs.getString("Fportcode"))
					+ " and fcashacccode ="
					+ dbl.sqlString(rs.getString("Fcashacccode"))
					+ " and fsubtsftypecode ="
					+ dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest)
					+ " and frelanum is null";
			subrs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			if (subrs.next()) {
				return false;
			}
		} catch (Exception e) {
			throw new YssException("查询首期关联编号出现异常！", e);
		} 
		//---add by songjie 2011.04.27  资产估值报游标超出最大数错误---//
		finally{
			dbl.closeResultSetFinal(subrs);
		}
		//---add by songjie 2011.04.27  资产估值报游标超出最大数错误---//
		return true;
	}

	/**
     * 拼装获取定存数据的sql语句
     * @return String
     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
     */
    private String builderSavingSql(CashAccountBean cashacc, java.util.Date dDate) {
        StringBuffer buf = new StringBuffer();
        buf.append("select x.*,z.FPortCury,pe.FDayInd,");
        buf.append("x.FSavingDate,"); //获取首期的存入日期
        buf.append("x.FMatureDate,"); //获取首期的到期日期
        buf.append(" n.FSavingDate as FBeginDate,"); //获取关联定存的存入日期
        buf.append("n.fmaturedate as FEndDate "); //获取关联定存的到期日期
        buf.append(" from (select a.*, b.FCuryCode, b.FBankCode, FFixRate from (select * from  ");
        buf.append(pub.yssGetTableName("TB_Cash_SavingInAcc")); //获取定存数据
        buf.append(" where FCheckState = 1 ");
        buf.append("and FTradeType in (").append(dbl.sqlString(YssOperCons.YSS_SAVING_FIRST)).append(",").append(dbl.sqlString(YssOperCons.YSS_SAVING_BUY)); //此处只获取首期或买入类型
        buf.append(") and ");
        buf.append(dbl.sqlDate(dDate));
        buf.append(" between FSavingDate and FMatureDate");

        buf.append(") a left join (select FCashAccCode, FBankCode, FCuryCode, FFixRate ");
        buf.append(" from ");
        buf.append(pub.yssGetTableName("TB_Para_CashAccount")); //获取固定利率
        buf.append(" where FCheckState = 1) b on a.FCashAccCode = b.FCashAccCode where a.FCashAccCode = ");
        buf.append(dbl.sqlString(cashacc.getStrCashAcctCode()));
        buf.append(" and FPortCode in (");
        buf.append(operSql.sqlCodes(this.portCodes));
        buf.append(") and FCheckState = 1 ");
        buf.append(" ) x");
        
    	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
        
        //edited by zhouxiang MS01444 计题申购款利息，现金应收应付中会查询到多笔相同的应收存款利息  
     //end--- by zhouxiang MS01444 计题申购款利息，现金应收应付中会查询到多笔相同的应收存款利息  
        
        buf.append(" left join (select FPortCode, FPortName, FPortCury ");
        buf.append(" from ");
        buf.append(pub.yssGetTableName("TB_Para_Portfolio")); //获取组合货币       
        buf.append(" where FCheckState = 1) z on x.FPortcode = z.FPortcode");
        
        //end by lidaolong 
        buf.append(" left join (select FSavingNum, FSavingDate, FMatureDate from ");
        buf.append(pub.yssGetTableName("TB_Cash_SavingInAcc")); //获取关联定存
        buf.append(" where FCheckState = 1"); //根据存单编号来获取关联定存
        buf.append(" and FTradeType not in (").append(dbl.sqlString("first")).append(",").append(dbl.sqlString("buy")); //此处只获取首期或买入类型
        buf.append(" )) n on n.fsavingnum = x.fsavingnum");
        
    	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     /*   buf.append(" left join (select  ca1.fcashacccode,ca1.fperiodcode from ");
        buf.append(pub.yssGetTableName("Tb_Para_Cashaccount")).append(" ca1 join (select fcashacccode, max(fstartdate) as fstartdate");
        buf.append(" from ").append(pub.yssGetTableName("Tb_Para_Cashaccount")).append(" where fstartdate <= ");
        buf.append(dbl.sqlDate(new java.util.Date())).append(" group by fcashacccode) ca2 on ca1.fcashacccode = ca2.fcashacccode");
        buf.append(" and ca1.fstartdate = ca2.fstartdate where FCheckState = 1) ca on ca.fcashacccode = x.fcashacccode"); 
       */
        buf.append(" left join (select fcashacccode,fperiodcode from ");
        buf.append(pub.yssGetTableName("Tb_Para_Cashaccount"));
        buf.append(" where FCheckState = 1) ca on ca.fcashacccode = x.fcashacccode"); 
       
    	// end by lidaolong
        buf.append(" left join (select fperiodcode,FDayInd from ").append(pub.yssGetTableName("tb_para_period"));
        buf.append(" where FCheckState = 1) pe on ca.fperiodcode = pe.fperiodcode");
        return buf.toString();
    }
    
    
    /**
     * 计算每日利息
     * @param rs ResultSet
     * @param cashacc CashAccountBean
     * @return double
     * @throws YssException
     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
     */
    private double calcSavingEachInterest(ResultSet rs, CashAccountBean cashacc, java.util.Date dDate) throws YssException {
        double result = 0D;
        PeriodBean period = null;
        int iDays = 0;
        try {
            period = new PeriodBean();
            period.setYssPub(pub);
            period.setPeriodCode(cashacc.getStrPeriodCode());

            if (cashacc.getInterestOrigin() == 0 &&
                (period.getPeriodCode() == null ||
                 period.getPeriodCode().trim().equalsIgnoreCase("") ||
                 period.getPeriodCode().trim().equalsIgnoreCase("null"))) {
                throw new YssException("请先维护现金帐户【" + cashacc.getStrCashAcctCode() +
                                       "】的期间设置");
            }
            //只有期间代码不为null,才进行处理
            if(period.getPeriodCode() != null){
                period.getSetting();
            }

            if (rs.getInt("FSavingType") == 4) { //comm
                result = calcCommSavingEachInterest(rs, period, cashacc, dDate);
            } else if (rs.getInt("FSavingType") == 3) { //circu 调整定存类型，3为通知存款
                result = calcCircuSavingEachInterest(rs, period, dDate);
            } else if (rs.getInt("FSavingType") == 2) { //convention 调整定存类型，2为协议存款
                result = calcConventionSavingEachInterest(rs, period,dDate);//增加期间设置，以进行计头计尾的判断
            }
            //----------------------------------------------------------------------------------------------------
            //以上为以年息算出的利息
            //一下为获取年天数，以计算天利息。前提为不是固定收益的计息方式
            //此处已经考虑了不含固定收益的计息方法
            if (null == rs.getString("FCALCTYPE") || rs.getString("FCALCTYPE").trim().length() == 0
                || !"fixincome".equalsIgnoreCase(rs.getString("FCALCTYPE"))) { //当没有计息方式设置，或者计息方式不是固定收益时，需要转换。
                if (period.getDayOfYear() == -1 || period.getPeriodType() == 1) { //此为默认条件
                    iDays = 360; //默认为360天
                } else {
                    iDays = period.getDayOfYear(); //获取期间设置的天数
                }
				result = YssD.div(result, iDays); //年利息除以年天数，得到每日利息
				
                /**shashijie 2011.04.12 STORY #815 
            	 * 若是最后一天计提的话使用钆差的方式得到最后利息金额*/
                if (isOverDay(rs) && result!=0&&(cashacc.getInterestAlg()!= 1)) {
                	if (rs.getInt("FSavingType") == 4 && 
                			"fixrate".equalsIgnoreCase(rs.getString("FCALCTYPE"))) {//固定利率
                		result = getInterest(rs,result);
					}
                	if (rs.getInt("FSavingType") == 2) {//2为协议存款
                		result = getInterest(rs,result);
					}
    			} 
                /**end*/
            }
        } catch (Exception e) {
            throw new YssException("计算每日利息出现异常！", e);
        }
        return result;
    }

    /**固定利率最后一天计算利息需要钆差*/
    private double getInterest(ResultSet rs ,double result) throws YssException,SQLException {
    	double allInterest;//总利息
    	double interest;//之前已计提的利息
    	double inteValue;//钆差
    	double dateNum;//总期限天数
    	double oldDateNum;//之前已计提天数
    	if (rs.getInt("FDayInd")==2){//头尾均计
    		dateNum = YssD.add(YssFun.dateDiff(rs.getDate("FSavingDate"),rs.getDate("FMatureDate")),1);
    		oldDateNum = YssFun.dateDiff(rs.getDate("FSavingDate"),rs.getDate("FMatureDate"));
    	} else {
    		dateNum = YssFun.dateDiff(rs.getDate("FSavingDate"),rs.getDate("FMatureDate"));
    		oldDateNum = YssD.sub(YssFun.dateDiff(rs.getDate("FSavingDate"),rs.getDate("FMatureDate")),1);
		}
    	//(每日利息*总期限天数)保留位数
    	allInterest = YssD.mul(result, dateNum);
    	allInterest = this.getSettingOper().reckonRoundMoney(rs.getString("FRoundCode"), allInterest);
    	//(每日利息(保留位数)*之前已计提天数)
    	double RoundResult = this.getSettingOper().reckonRoundMoney(rs.getString("FRoundCode"), result);
    	interest = YssD.mul(RoundResult, oldDateNum);
    	//(总利息-之前已计提的利息)
    	inteValue = YssD.sub(allInterest , interest);
    	return inteValue;
	}

	/**
     * 计算普通定存的每日利息
     * @param rs ResultSet
     * @param period PeriodBean 期间设置
     * @param cashacc CashAccountBean
     * @return double
     * @throws YssException
     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
     */
    private double calcCommSavingEachInterest(ResultSet rs, PeriodBean Period, CashAccountBean cashacc, java.util.Date dDate) throws YssException {
        double result = 0D;
        //add by lidaolong 2011.02.23 #526 QDV4长信基金2011年1月14日01_A
        double foutmoney = 0D;
        ResultSet rs1 = null;
        try {
        String sql1 = " select sum(foutmoney) as foutmoney from " + pub.yssGetTableName("TB_Cash_Consavingpriext") 
        				+ " where fcheckstate = 1 and Fconsavingnum = " + dbl.sqlString(rs.getString("fnum"))
        				/**shashijie 2012-7-25 STORY 2796 增加条件,判断为本金提出时才取出流出金额*/
						+" And FTakeType = '0' "
						/**end*/
        				+ " and fextdate <= " + dbl.sqlDate(dDate);
        	rs1 = dbl.queryByPreparedStatement(sql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs1.next()) {
            	foutmoney = rs1.getDouble("foutmoney");
            }
        } catch(Exception e) {
        	throw new YssException("获取协议定存本金提取出错！", e);
        } finally {
        	 dbl.closeResultSetFinal(rs1);
        }
         if (foutmoney>0){
        	 return result;
         }
        //--------------
        
        try {
            if ("fixincome".equalsIgnoreCase(rs.getString("FCALCTYPE"))) {  //根据维护固定收益，采用倒轧
                if (Period.getDayInd().equalsIgnoreCase("0")) {             //计头不计尾
                    if (YssFun.dateDiff(rs.getDate("FSavingDate"), dDate) >= 0 &&
                        YssFun.dateDiff(rs.getDate("FMatureDate"), dDate) < 0) {
                        result = calcCommSavingWithFixIncome(rs,Period, dDate);    // 计算固定收益的普通定存利息,增加期间设置的信息
                    }
                } else if (Period.getDayInd().equalsIgnoreCase("1")) {      //计尾不计头
                    if (YssFun.dateDiff(rs.getDate("FSavingDate"), dDate) > 0 &&
                        YssFun.dateDiff(rs.getDate("FMatureDate"), dDate) <= 0) {
                        result = calcCommSavingWithFixIncome(rs,Period, dDate);    // 计算固定收益的普通定存利息
                    }
                } else if (Period.getDayInd().equalsIgnoreCase("2")) {      //both
                    result = calcCommSavingWithFixIncome(rs,Period, dDate);        // 计算固定收益的普通定存利息
                }
            } else if ("fixrate".equalsIgnoreCase(rs.getString("FCALCTYPE"))) { //固定利率
                if (Period.getDayInd().equalsIgnoreCase("0")) {             //计头不计尾
                    if (YssFun.dateDiff(rs.getDate("FSavingDate"), dDate) >= 0 &&
                        YssFun.dateDiff(rs.getDate("FMatureDate"), dDate) < 0) {
                        result = calcCommSavingWithFixRate(rs, dDate);      // 计算固定利率的普通定存利息
                    }
                } else if (Period.getDayInd().equalsIgnoreCase("1")) {      //计尾不计头
                    if (YssFun.dateDiff(rs.getDate("FSavingDate"), dDate) > 0 &&
                        YssFun.dateDiff(rs.getDate("FMatureDate"), dDate) <= 0) {
                        result = calcCommSavingWithFixRate(rs, dDate);      // 计算固定利率的普通定存利息
                    }
                } else if (Period.getDayInd().equalsIgnoreCase("2")) {      //both
                    result = calcCommSavingWithFixRate(rs, dDate);          // 计算固定利率的普通定存利息
                }
            }
        } catch (Exception e) {
            throw new YssException("计算普通定存的每日利息出现异常！", e);
        }
        return result;
    }

    /**
     * 计算固定收益的普通定存利息
     * @param rs ResultSet
     * @param dDate Date
     * @param Period Period
     * @return double
     * @throws YssException
     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
     */
    private double calcCommSavingWithFixIncome(ResultSet rs,PeriodBean Period,java.util.Date dDate) throws YssException {
        double result = 0D;
        java.util.Date curDate = null;//用于计算当为不同的期间时的最后一日的日期值
        try {
            if (Period.getDayInd().equalsIgnoreCase("0")) { //计头不计尾
                curDate = YssFun.addDay(rs.getDate("FMatureDate"), -1);
            } else if (Period.getDayInd().equalsIgnoreCase("1")) { //计尾不计头
                curDate = rs.getDate("FMatureDate");
            } else if (Period.getDayInd().equalsIgnoreCase("2")) { //both
                curDate = rs.getDate("FMatureDate");
            }
            if (null != rs.getDate("FEndDate") && YssFun.dateDiff(rs.getDate("FBeginDate"), dDate) >= 0) { //若endDate有数值，则说明此普通定存的类型有转出数据关联，做特殊处理。
                /*若计息日期大于等于转出的存入日期，则不再进行计息*/
                return result;
            } else {
                if (YssFun.dateDiff(dDate, curDate) > 0) { //最后一天等于0,调整为转换之后的日期
                    result =
                        YssD.div(rs.getDouble("frecinterest"), //存款收益
                                 YssFun.
                                 dateDiff(rs.getDate("FSavingDate"),
                                          rs.getDate("FMatureDate")));//此处不用加1，作出调整
                } else { //最后一天采用倒轧
                    result = rs.getDouble("frecinterest") -
                        YssD.mul(YssFun.roundIt(YssD.div(rs.getDouble(
                            "frecinterest"), //存款收益
                        YssFun.
                        dateDiff(rs.getDate("FSavingDate"),
                                 rs.getDate("FMatureDate"))), 2),
                                 YssD.sub(YssFun.dateDiff(rs.getDate("FSavingDate"),
                                          rs.getDate("FMatureDate")),1));//此处日期天数-1，作出调整
                }
            }
        } catch (Exception e) {
            throw new YssException("计算固定收益的普通定存利息出现异常！", e);
        }
        return result;
    }

    /**
     * 计算固定利率的普通定存利息
     * @param rs ResultSet
     * @param dDate Date
     * @return double
     * @throws YssException
     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
     */
    private double calcCommSavingWithFixRate(ResultSet rs, java.util.Date dDate) throws YssException, SQLException {
        double result = 0D;
        //计息公式与舍入设置
        if (rs.getString("FFormulaCode") != null && rs.getString("FRoundCode") != null) {
			//(计息公式,舍入设置,总金额,业务日期)
			result = this.getSettingOper().calMoneyByPerExp(rs.getString("FFormulaCode"), rs.getString("FRoundCode"),
	                rs.getDouble("FInMoney"), dDate); //使用计息公式进行计算
        } else {
            throw new YssException("存款编号为【" + rs.getString("FNum") +
                                   "】的存款没有设置计息公式或舍入方式，不能进行内部公式计算，请检查!");
        }

        return result;
    }




/**
     * 通知定存每日计息
     * @param rs ResultSet
     * @return double
     */
    private double calcCircuSavingEachInterest(ResultSet rs, PeriodBean period, java.util.Date dDate) throws SQLException, YssException {
        double result = 0D;
        java.util.Date currentDate = null;
        if (null != rs.getDate("FEndDate")) { //当关联定存信息有数据
            currentDate = rs.getDate("FEndDate"); //设置到期日期，计息结束
        } else {
            currentDate = rs.getDate("FMatureDate"); //获取首期的到期日期(9998-12-31)。此时一直计息
        }
        if (YssFun.dateDiff(dDate, currentDate) >= 0) { //到期日期时，计息结束
            /*调用 计算固定利率的普通定存利息 -- 其算法一致*/
            if (period.getDayInd().equalsIgnoreCase("0")) { //计头不计尾
                if (YssFun.dateDiff(rs.getDate("FSavingDate"), dDate) >= 0 &&
                    YssFun.dateDiff(currentDate, dDate) < 0) {
                    result = calcCommSavingWithFixRate(rs, dDate); // 计算固定利率的普通定存利息
                }
            } else if (period.getDayInd().equalsIgnoreCase("1")) { ////计尾不计头
                if (YssFun.dateDiff(rs.getDate("FSavingDate"), dDate) > 0 &&
                    YssFun.dateDiff(currentDate, dDate) <= 0) {
                    result = calcCommSavingWithFixRate(rs, dDate); // 计算固定利率的普通定存利息
                }
            } else if (period.getDayInd().equalsIgnoreCase("2")) { //both
                result = calcCommSavingWithFixRate(rs, dDate); // 计算固定利率的普通定存利息
            }

        }
        return result;
    }

    /**
     * 协议定存每日计息
     * @param rs ResultSet
     * @param dDate Date
     * @return double
     * @throws SQLException
     * @throws YssException
     */
    private double calcConventionSaving(ResultSet rs, java.util.Date dDate) throws SQLException, YssException {
        double result = 0D;
        double basicInterest = 0D; //基准金额利息
        double conventionInterest = 0D; //协议金额利息

        basicInterest = YssD.mul(rs.getDouble("FBasicMoney"), rs.getDouble("FBasicRate")); //基准金额×汇率
        //add by fangjiang 2010.11.29 TASK #1096::协议存款业务需支持提前提取本金的功能
        double foutmoney = 0D;
        ResultSet rs1 = null;
        String sql1 = " select sum(foutmoney) as foutmoney from " + pub.yssGetTableName("TB_Cash_Consavingpriext") 
        				+ " where fcheckstate = 1 and Fconsavingnum = " + dbl.sqlString(rs.getString("fnum"))
        				+ " and fextdate <= " + dbl.sqlDate(dDate);
        try {
        	rs1 = dbl.queryByPreparedStatement(sql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs1.next()) {
            	foutmoney = rs1.getDouble("foutmoney");
            }
        } catch(Exception e) {
        	throw new YssException("获取协议定存本金提取出错！", e);
        } finally {
        	 dbl.closeResultSetFinal(rs1);
        }
        //--------------
        if (rs.getString("FFormulaCode") != null &&
            rs.getString("FRoundCode") != null) {
        	//modify by fangjiang 2010.11.29 TASK #1096::协议存款业务需支持提前提取本金的功能
            conventionInterest =
                this.getSettingOper().calMoneyByPerExp(rs.getString(
                    "FFormulaCode"), rs.getString("FRoundCode"),
                YssD.sub(rs.getDouble("FInMoney"), foutmoney, rs.getDouble("FBasicMoney")), dDate); //此处的金额为（存入金额 – 基本额度）
            //------------------------
        } else {
            throw new YssException("存款编号为【" + rs.getString("FNum") +
                                   "】的存款没有设置计息公式或舍入方式，不能进行内部公式计算，请检查!");
        }

        conventionInterest =
            result = YssD.add(basicInterest, conventionInterest); //基准金额利息 + 协议金额利息
        return result;

    }

    /**
     * 协议定存每日计息
     * @param rs ResultSet
	 * @param PeriondBean Period
     * @return double
     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
     */
    private double calcConventionSavingEachInterest(ResultSet rs, PeriodBean Period, java.util.Date dDate) throws SQLException, YssException {
        double resultInterest = 0D;
        if (Period.getDayInd().equalsIgnoreCase("0")) { //计头不计尾
            if (YssFun.dateDiff(rs.getDate("FSavingDate"), dDate) >= 0 &&
                YssFun.dateDiff(rs.getDate("FMatureDate"), dDate) < 0) {
                resultInterest = calcConventionSaving(rs, dDate); // 计算固定利率的普通定存利息
            }
        } else if (Period.getDayInd().equalsIgnoreCase("1")) { //计尾不计头
            if (YssFun.dateDiff(rs.getDate("FSavingDate"), dDate) > 0 &&
                YssFun.dateDiff(rs.getDate("FMatureDate"), dDate) <= 0) {
                resultInterest = calcConventionSaving(rs, dDate); // 计算固定利率的普通定存利息
            }
        } else if (Period.getDayInd().equalsIgnoreCase("2")) { //both
            resultInterest = calcConventionSaving(rs, dDate); // 计算固定利率的普通定存利息
        }

        return resultInterest;
    }
    //--------------------------------------------------------------------------------------------------------------------------新的定存计息方式end
    /**
     * add by wangzuochun 2009.12.30 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A 
     * @param sTableName
     * @param hmPecPay
     * @param portCodesTem
     * @param tableTem
     * @throws YssException
     * MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A sj
     */
    public void CalculateTaInterest(String sTableName, HashMap hmPecPay, String portCodesTem, String tableTem) throws YssException {
        //循环日期
        int iDay = YssFun.dateDiff(this.beginDate, this.endDate);
        //当前记息日期
        java.util.Date dCurrDay = this.beginDate;
        //基础汇率
        double BaseCuryRate = 0.0;
        //组合汇率
        double PortCuryRate = 0.0;
        //保存sql语句
        String strSql = "";
        //声明游标
        ResultSet rs = null;
        //新建获取利率的通用类
        EachRateOper rateOper = new EachRateOper();
        //放到全局变量中
        rateOper.setYssPub(pub);
        //---add by songjie 2012.08.02 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A start---//
        boolean addSubSql = false;
        String clsPort = "";//分级组合代码
		//---add by songjie 2012.08.02 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A end---//
        try {
		    //add by songjie 2011.06.29 BUG 2153 QDV4易方达2011年6月21日01_B
        	String[] portCodeStr = portCodesTem.split(",");
            for (int i = 0; i <= iDay; i++) {
			    //add by songjie 2011.06.29 BUG 2153 QDV4易方达2011年6月21日01_B
            	for(int j = 0; j < portCodeStr.length; j++){
            		
                //---add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A start---//	
            	strSql = " select * from " + sTableName +  " where FPortCode IN (" + 
            	operSql.sqlCodes(portCodeStr[j]) + ") and FBeginDate = (select max(FBeginDate) as FBeginDate from " + 
            	sTableName + " where FBeginDate <= " + dbl.sqlDate(YssFun.addDay(this.beginDate, i)) + 
            	" and FPortCode in(" + operSql.sqlCodes(portCodeStr[j]) +"))";	
            	rs = dbl.openResultSet(strSql);
            	if(rs.next()){
            		clsPort = rs.getString("FPortClsCode");
            		if(clsPort != null && clsPort.trim().length() > 0){
            			addSubSql = true;
            		}
            	}
            	//---add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A end---//
            	
            	//--- MS01247  QDV4易方达2010年5月21日02_A  TA申购款计息中需维护年利率和一年天数  add by jiangshichao 2010.06.08 ---
                //edit by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A 添加 组合分级代码
                strSql = "SELECT a.FPortCode, a.FAnalysisCode1, a.FAnalysisCode2, a.FAnalysisCode3, a.FCashAccCode, a.FCuryCode, a.FPortClsCode, " +
                		 "SUM(FSellMoney) AS FMoney, b.FValue, c.FPortCury, b.FDayNum, a.FSettleDate, b.FType, b.FHolidays,b.FDays " +
                    " FROM (SELECT * FROM " +
                    (!"".equals(tableTem.trim()) ? tableTem : pub.yssGetTableName("Tb_Ta_Trade")) +
                    " WHERE FCheckState = 1 AND FSellType = '01' AND FPortCode IN (" +
                    //edit by songjie 2011.06.29 BUG 2153 QDV4易方达2011年6月21日01_B
					operSql.sqlCodes(portCodeStr[j]) + " )" + " and FCashAcccode in (" +
                    operSql.sqlCodes(this.selCodes) + " )"  +
                    " and " + dbl.sqlDate(dCurrDay) + " BETWEEN FConfimDate AND FSettleDate) a" +
                    " JOIN (SELECT * FROM " + sTableName +
                    //edit by songjie 2011.06.29 BUG 2153 QDV4易方达2011年6月21日01_B
					" WHERE FPortCode IN (" + operSql.sqlCodes(portCodeStr[j]) +
                    " ) and FBeginDate = (select max(FBeginDate) as FBeginDate from " + sTableName +
                    //---edit by songjie 2011.06.29 BUG 2153 QDV4易方达2011年6月21日01_B---//
					" where FBeginDate <= " + dbl.sqlDate(YssFun.addDay(this.beginDate, i)) + " and FPortCode in(" + 
                    operSql.sqlCodes(portCodeStr[j]) +"))" +") b ON a.FSellNetCode = b.FPoint " + 
                    //add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A
                    (addSubSql ? "and a.FPortClsCode = b.FPortClsCode ": "") +
					//---edit by songjie 2011.06.29 BUG 2153 QDV4易方达2011年6月21日01_B---//
                	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                    
                   /* " left join " +
                    " (SELECT port.FPortCode as FPortCode,port.FStartDate as FStartDate,dPort.FPortCury as FPortCury " +
                    " FROM (select FPortCode, Max(FStartDate) as FStartDate " +
                    " from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(dCurrDay) +
                    " group by FPortCode) port " +
                    " join (select FPortCode, FPortCury,FStartDate " +
                    " from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where FCheckState = 1) dPort on port.FPortCode = dPort.FPortCode " +
                    " and port.FStartDate = dPort.FStartDate " + //增加对组合的启用日期，审核状态的筛选。
                    
                    ") c ON a.FPortCode = c.FPortCode" +*/

                    " left join " +
                    " (SELECT  FPortCode, FPortCury " +                 
                    " from " + pub.yssGetTableName("Tb_Para_Portfolio") +           
                    " where FCheckState = 1) c ON a.FPortCode = c.FPortCode" +
                    
                    //end by lidaolong
                    // 将交易记录的结算日期和延迟天数添加为分组条件
                    " GROUP BY b.FValue, b.FPoint, a.FAnalysisCode1, a.FAnalysisCode2, a.FAnalysisCode3, a.FCashAccCode, " +
                    //edit by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A  添加 组合分级代码
                    "a.FCuryCode, a.FPortCode, a.FSettleDate, a.FPortClsCode, b.FDayNum, b.FType, c.FPortCury, b.FHolidays,b.FDays";
                   //--- MS01247  QDV4易方达2010年5月21日02_A  TA申购款计息中需维护年利率和一年天数  end --------
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    //从结算日期，往前推延迟天数之前最近的工作日日期
                    java.util.Date dWorkDate = null;
                    //结算日期
                    java.util.Date dSettleDate = rs.getDate("FSettleDate");

                    //判断计头不计尾，还是计尾部计头，计算实际结算日期和记息区间
                    if (rs.getInt("FType") == 0) { //表示计头不计尾
                        dSettleDate = YssFun.addDay(dSettleDate, -1);
                        dWorkDate = this.getSettingOper().getWorkDay(rs.getString(
                            "FHolidays"), rs.getDate("FSettleDate"),
                            rs.getInt("FDayNum") * -1);
                    } else {
                        dWorkDate = this.getSettingOper().getWorkDay(rs.getString(
                            "FHolidays"), rs.getDate("FSettleDate"),
                            rs.getInt("FDayNum") * -1);
                        dWorkDate = YssFun.addDay(dWorkDate, 1);
                    }
                    //判断记息日期是否在记息区间中
                    if (dCurrDay.compareTo(dWorkDate) >= 0 &&
                        dCurrDay.compareTo(dSettleDate) <= 0) {

                        //将应收应付数据存放在哈希表中，将相同记息日期、组合、分析代码、帐户代码的应收金额累加在一条应收记录中
                        TaInterestPecPayBean pay = null;
                        String sKey = rs.getString("FPortCode") + "\t" +
                            rs.getString("FAnalysisCode1") + "" + "\t" +
                            rs.getString("FAnalysisCode2") + "" + "\t" +
                            rs.getString("FCashAccCode") + "\t" +
                            rs.getString("FCuryCode") + "\t" +
                            YssFun.formatDate(dCurrDay, "yyyy-MM-dd");
                        pay = (TaInterestPecPayBean) hmPecPay.get(sKey);
                        if (pay == null) {
                            pay = new TaInterestPecPayBean();
                            //add by songjie 2012.08.06 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A
                            setInterestRecPay(rs, pay,dCurrDay,rateOper);
//                            pay.setMoney(YssD.round(YssD.mul(rs.getDouble("FMoney"),
//                                rs.getDouble("FValue")), 2));
                            pay.setMoney(YssD.round(YssD.mul(rs.getDouble("FMoney"),
                                  YssD.div(rs.getDouble("FValue"), rs.getInt("FDays"))), 2));//--- MS01247QDV4易方达2010年5月21日02_A  TA申购款计息中需维护年利率和一年天数  add by jiangshichao 2010.06.08
                            pay.setBaseCuryMoney(YssD.mul(pay.getMoney(),
                                pay.getBaseCuryRate()));
                            //---- 使用通用的计算组合金额的方法
                            pay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                                pay.
                                getMoney(), pay.getBaseCuryRate(),
                                pay.getPortCuryRate(),
                                rs.getString("FCuryCode"),
                                dCurrDay,
                                pay.getPortCode()));
                            

                            hmPecPay.put(sKey, pay);
                        } else {
                        	//add by songjie 2012.08.06 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A
                        	setInterestRecPay(rs, pay,dCurrDay,rateOper);
                            
                            //------ 金额没有除以天数 modify by wangzuochun  2010.08.30  MS01661    TA申购款计息金额不正确    QDV4赢时胜深圳2010年8月30日01_B  
                            pay.setMoney(pay.getMoney() +
                                         YssD.round(YssD.mul(rs.getDouble("FMoney"), YssD.div(rs.getDouble("FValue"), rs.getInt("FDays"))), 2));
                            
                            //-------------------------------------MS01661--------------------------------------------//
                            
                            pay.setBaseCuryMoney(YssD.mul(pay.getMoney(), pay.getBaseCuryRate()));
                            pay.setPortCuryMoney(YssD.div(YssD.mul(pay.getMoney(),
                                pay.getBaseCuryRate()), pay.getPortCuryRate()));
                            
                            // add by songjie 2012.08.01 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A
                            hmPecPay.put(sKey, pay);
                        }
                    }
                }
            	}//add by songjie 2011.06.29 BUG 2153 QDV4易方达2011年6月21日01_B
                dCurrDay = YssFun.addDay(dCurrDay, 1);
                dbl.closeResultSetFinal(rs); //关闭游标
            }           

            Iterator itTA = hmPecPay.values().iterator();
            TaInterestPecPayBean payTA = null;
            Connection conn = dbl.loadConnection();
            boolean bTrans = false;
            PreparedStatement pst = null;
            String sFNum = "";
            int iFNum = 0;
            boolean bFirst = true;
            HashMap htDiffDate = new HashMap(); // 存放不同日期的 Max FNum 值
            String strAccCodes = "";
            
            conn.setAutoCommit(false);
            bTrans = true;
            
            if (this.selCodes != null && this.selCodes.length() > 0) {
            	String strAccount [] = this.selCodes.split(",");
            	for (int i = 0 ; i < strAccount.length; i++) {
            		strAccCodes = strAccCodes + dbl.sqlString(strAccount[i]) + ",";
            	}
            	strAccCodes = strAccCodes.substring(0, strAccCodes.length() - 1 );
            	
            	String delSql = " delete from " + pub.yssGetTableName("Tb_Data_InterestPayRec")+ 
				" where FTransDate between " + dbl.sqlDate(beginDate) +
				" and " + dbl.sqlDate(endDate) + 
				//---edit by songjie 2011.06.29 BUG 2153 QDV4易方达2011年6月21日01_B---//
				" and FPortCode in (" + operSql.sqlCodes(portCodes) + 
				" ) and FCashAccCode in (" + strAccCodes + ")";
            	//---edit by songjie 2011.06.29 BUG 2153 QDV4易方达2011年6月21日01_B---//
            	if (itTA.hasNext()) {
                	dbl.executeSql(delSql);
                }
            }
            
            
            strSql = "insert into "
				+ pub.yssGetTableName("Tb_Data_InterestPayRec")
				+ "(FNum,FTransDate,FBeginDate,FEndDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FTsfTypeCode"
				+ ",FSubTsfTypeCode,FCuryCode,FMoney,FBaseCuryRate,FBaseCuryMoney,FPortCuryRate,FPortCuryMoney"
				+ ",FDataSource,FStockInd"
				+
				//-------------------------------
				",FDesc"
				+
				//-------------------------------
				",FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FInOut)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		    
            pst = conn.prepareStatement(strSql);
            
            while (itTA.hasNext()) {
                payTA = (TaInterestPecPayBean)itTA.next();
                
                if (htDiffDate.get(payTA.getTradeDate()) == null) {
                    //如果本次的日期与上一次的日期不同的话就得再取一次编号
                    sFNum = getNum(payTA);
                }
				//---add by songjie 2012.08.06 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A start---//
				else{
                	sFNum = (String)htDiffDate.get(payTA.getTradeDate());
                }
				//---add by songjie 2012.08.06 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A end---//
                //如果金额全部为零时  就不进行保存
                // 8-2
                if (payTA.getMoney() == 0 &&
                	payTA.getBaseCuryMoney() == 0 &&
                	payTA.getPortCuryMoney() == 0) {
                    continue;
                }

                if (sFNum.trim().length() > 0 && sFNum.length() > 11) {
                    iFNum = YssFun.toInt(YssFun.right(sFNum, 9)); //取出后9位的长度
                    sFNum = YssFun.left(sFNum, 11); //取出左边11位长度
                    iFNum++;
                    sFNum += YssFun.formatNumber(iFNum, "000000000");
                }
                //---add by songjie 2012.08.06 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A start---//
                if (htDiffDate.get(payTA.getTradeDate()) == null){
                	htDiffDate.put(payTA.getTradeDate(), sFNum);
                }
                //---add by songjie 2012.08.06 STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A end---//
                pst.setString(1, sFNum);
                pst.setDate(2, YssFun.toSqlDate(payTA.getTradeDate()));
                
                pst.setDate(3, YssFun.toSqlDate(payTA.getBeginDate()));
                pst.setDate(4, YssFun.toSqlDate(payTA.getEndDate()));
                
                pst.setString(5, payTA.getPortCode());
                pst.setString(6,
                		payTA.getInvestManagerCode().length() != 0 ?
                				payTA.getInvestManagerCode() : " "); //无库存信息配置赋空值
                pst.setString(7,
                		payTA.getCategoryCode().length() != 0 ?
                				payTA.getCategoryCode() : " ");
                pst.setString(8, " ");
                pst.setString(9, payTA.getCashAccCode());
                pst.setString(10, payTA.getTsfTypeCode());
                pst.setString(11, payTA.getSubTsfTypeCode());
                pst.setString(12, payTA.getCuryCode());
                pst.setDouble(13, YssFun.roundIt(payTA.getMoney(), 2)); //默认保留2位小数。
                pst.setDouble(14, YssFun.roundIt(payTA.getBaseCuryRate(), 15)); //hxqdii
                pst.setDouble(15, YssFun.roundIt(payTA.getBaseCuryMoney(),2));
                pst.setDouble(16, YssFun.roundIt(payTA.getPortCuryRate(), 15)); //hxqdii
                pst.setDouble(17, YssFun.roundIt(payTA.getPortCuryMoney(),2));
                pst.setDouble(18, payTA.getDataSource());
                pst.setDouble(19, payTA.getStockInd());
                //------------------------------------------
                pst.setString(20, payTA.getDesc());
                //------------------------------------------
                pst.setInt(21, payTA.checkStateId); //增加一个是否审核的功能
                pst.setString(22, pub.getUserCode());
                pst.setString(23, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(24, pub.getUserCode());
                pst.setString(25, YssFun.formatDatetime(new java.util.Date()));
                pst.setInt(26, payTA.getInOutType());
                pst.executeUpdate();
                
            }

            conn.commit(); // 提交事务
            bTrans = false;
            conn.setAutoCommit(true);
            
        } catch (Exception e) {
            throw new YssException("获取计算利息出错！\r\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * add by songjie 2012.08.06 
     * STORY #2832 QDV4赢时胜（易方达基金）2012年7月31日01_A
     */
    private void setInterestRecPay(ResultSet rs, TaInterestPecPayBean pay,java.util.Date dCurrDay,EachRateOper rateOper) throws YssException,SQLException{
    	try{
    		pay.setBeginDate(this.beginDate);
    		pay.setEndDate(this.endDate);
        
    		pay.setTradeDate(dCurrDay);
    		pay.setPortCode(rs.getString("FPortCode") + "");
    		pay.setInvestManagerCode(rs.getString("FAnalysisCode1") + "");
    		pay.setCategoryCode(rs.getString("FAnalysisCode2") + "");
    		pay.setCashAccCode(rs.getString("FCashAccCode") + "");
    		pay.setCuryCode(rs.getString("FCuryCode") + "");
    		pay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
    		pay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_PF_REC);
    		//基础汇率
    		double BaseCuryRate = this.getSettingOper().getCuryRate(
    				dCurrDay, rs.getString("FCuryCode"), pay.getPortCode(),
    				YssOperCons.YSS_RATE_BASE);
    		//组合汇率
    		rateOper.getInnerPortRate(dCurrDay,
    				rs.getString("FCuryCode"),
    				pay.getPortCode());
    		double PortCuryRate = rateOper.getDPortRate();
        
    		//-----------------------------------------------------------------------------------
    		pay.checkStateId = 1; //计提出的数据放入已审核 
    		pay.setBaseCuryRate(BaseCuryRate);
    		pay.setPortCuryRate(PortCuryRate);
    	}catch(YssException e){
    		throw new YssException(e.getMessage());
    	}
    }
    
    /**
     * add by wangzuochun 2009.12.30 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A 
     * 添加获取最大编号的方法
     * @param taPay
     * @return
     * @throws YssException
     */
    private String getNum(TaInterestPecPayBean taPay) throws YssException {
        String sFNum = "";
        try {
            sFNum = "SRP" +
                YssFun.formatDatetime(taPay.getTradeDate()).
                substring(0, 8) +
                dbFun.getNextInnerCode(pub.yssGetTableName(
                    "Tb_Data_InterestPayRec"),
                                       dbl.sqlRight("FNUM", 9), "000000001",
                                       " where FTransDate = " +
                                       dbl.sqlDate(taPay.getTradeDate()));

            return sFNum;
        } catch (Exception e) {
            throw new YssException("计算最大编号出错!" + "\n", e);
        }
    }

    
    
    private  CashPecPayBean getInterTax(CashAccountBean cashacc,CashPecPayBean pay) throws  YssException{
    	
    	CashPecPayBean InterestTax = null;
    	double dInterestTax =0;
    	  int iDays = 0;
    	try{
    		InterestTax = (CashPecPayBean)pay.clone();
    		dInterestTax =  this.getSettingOper().calMoneyByPerExp(pay.getTradeDate(),cashacc.getStrInterTax(), pay.getMoney());		
    		InterestTax.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
    		InterestTax.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_LXS_DE);
    		InterestTax.setMoney(YssFun.roundIt(dInterestTax, 2)); 
    		InterestTax.setBaseCuryMoney(this.getSettingOper().calBaseMoney(InterestTax. getMoney(), InterestTax.getBaseCuryRate())); 
    		InterestTax.setPortCuryMoney(this.getSettingOper().calPortMoney(InterestTax.getMoney(), InterestTax.getBaseCuryRate(),
              InterestTax.getPortCuryRate(),
              InterestTax.getCuryCode(),
              InterestTax.getTradeDate(),
              InterestTax.getPortCode()));
    		
    		
    		return InterestTax;
    	}catch(Exception e){
    		throw new YssException("计提存款利息税出错......");
    	}
    }
    
}
