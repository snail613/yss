package com.yss.main.operdeal.income.stat;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.sun.msv.datatype.xsd.TotalDigitsFacet;
import com.yss.commeach.EachRateOper;
import com.yss.main.dayfinish.IncomeStatBean;
import com.yss.main.dayfinish.InvestFeeBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.invest.BaseInvestOper;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.report.navrep.CtlNavRep;
import com.yss.main.parasetting.InvestPayBean;
import com.yss.main.parasetting.InvestRelaSetBean;
import com.yss.main.parasetting.PeriodBean;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.pojo.param.invest.YssInvestInfo;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.util.YssUtil;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.manager.InvestPayAdimin;
import com.yss.util.YssCons;

public class StatInvestFee
    extends BaseIncomeStatDeal {
    public StatInvestFee() {
    }

    public ArrayList getDayIncomes(java.util.Date dDate) throws YssException {
        ArrayList reIncomes = new ArrayList();
        String strSql = "";
        ResultSet rs = null;
        InvestFeeBean pay = null;
        InvestPayBean ivpBean = null;
        HashMap hmPubIVFees = null;
        HashMap hmBak = null;
        String[] sPortCodeAry = null;
        InvestFeeBean invest = null;
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        boolean analy1;
        boolean analy2;
        boolean analy3;
        //-----月计提费用 sj modified MS00052 --------
        CtlPubPara pubpara = null;
        String resultPubpara = "";
        String[] paraInvestCodes = null;
        String[] paraInvestFormula = null;
        HashMap investAndFormula = new HashMap();
        String formula = "";
        //------------------------------------------
        String CuryCode="";//现金账户币种代码
   	    String CuryCode1="";
        String stCashAccCodes="";//现金账户代码
        String str="";
        ResultSet rsStr = null;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------
		//---add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A start---//
        String portCode = "";//组合代码
        Date logBeginDate = new Date();//开始时间
		//---add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A end---//
        try {
            //加入了运营收支的分析代码的判断，改变了以前的写法  杨文奇 20070906
            analy1 = operSql.storageAnalysis("FAnalysisCode1",
                                             YssOperCons.YSS_KCLX_InvestPayRec);
            analy2 = operSql.storageAnalysis("FAnalysisCode2",
                                             YssOperCons.YSS_KCLX_InvestPayRec);
            analy3 = operSql.storageAnalysis("FAnalysisCode3",
                                             YssOperCons.YSS_KCLX_InvestPayRec);

            hmPubIVFees = getPubInvestFees(dDate);
            hmBak = (HashMap) hmPubIVFees.clone();
            sPortCodeAry = portCodes.split(",");
            for (int i = 0; i < sPortCodeAry.length; i++) {
				//add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A
            	portCode = sPortCodeAry[i];//获取组合代码
                hmPubIVFees = hmBak;
                //----------sj modified MS00052 ------------------------------------
                pubpara = new CtlPubPara();
                pubpara.setYssPub(pub);
                resultPubpara = pubpara.getInvestInfo(sPortCodeAry[i]);
                if (resultPubpara.split("\t")[0].split("[|]").length == 0) {
                    throw new YssException("请在通用参数设置中完成对费用类型的设置!");
                }
                paraInvestCodes = (resultPubpara.split("\t")[0].split("[|]")[0]).
                    split(",");
                if ( (resultPubpara.split("\t")[1]).split("[|]").length == 0) {
                    throw new YssException("请在通用参数设置中完成对费用计算公式的设置!");
                }
                //modify by fangjiang 2011.10.26 STORY #1589
                paraInvestFormula = (resultPubpara.split("\t")[1]).split("[|]")[0].split(",");
                formula = paraInvestFormula[0];
                if(paraInvestCodes != null){
                	for(int j=0; j<paraInvestCodes.length; j++){
                		if(paraInvestFormula.length > 1){
                			investAndFormula.put(paraInvestCodes[j], paraInvestFormula[j]);
                		}else{
                			investAndFormula.put(paraInvestCodes[j], paraInvestFormula[0]);
                		}	
                	}
                }
                //-------------------STORY #1589-----------------
                //------------------------------------------------------------------
                strSql =
                    "select a.FIVPayCatCode,a.FStartDate,a.FPortCode,a.FRoundCode,a.FCuryCode,a.FPerExpCode,A.FHOLIDAYSCODE," + //添加节假日代码FHOLIDAYSCODE sunkey@Modify 工银分盘 20091223
                    "a.FAccrueType," + //计提方式 MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A panjunfang add 20090715
                    "a.fpaydate,a.FPeriodOfBC, a.FLimitedAmount," +	//20120512 added by liubo.Story #2217.支付日期设置,不差时期，支付下限
                    "a.FLowerCurrencyCode,a.FSupplementDates," + // add by huangqirong 2013-01-22 story #3488下限币种 ，补差日期
                    "a.FPeriodCode,a.FFixRate,a.FPayOrigin,a.FACRoundCode,a.FACbegindate,a.FACEndDate,a.FEXPIRDATE,a.FACTotalMoney," + //增加投资运营品种终止日期
                    "a.FCashAccCode," + //add by fangjiang 2011.02.11 #2279
                    "a.FApportionType," +	//20110810 added by liubo.Story #1227.获取均摊方式
                    "a.FTransition,a.FTransitiondate,a.FPaidIn," +		//20120217 added by liubo.Story 2139.预提转待摊、预提转待摊中的转换日期、预提转待摊中的实收金额
                    (analy1 ? "a.FAnalysisCode1" : "' '") + " as FAnalysisCode1," + //多了个逗号，导致出错20070913，杨
                    (analy2 ? "a.FAnalysisCode2" : "' '") + " as FAnalysisCode2," +
                    (analy3 ? "a.FAnalysisCode3" : "' '") + " as FAnalysisCode3," +
                    "a.FARIEXPCODE," + //modify by zhangjun 2011.12.29 story 1273
                    "a.fportclscode,"+//story 2253 add by zhouwei 20120221 增加组合分级字段
                    "m.FIVPayCatName,z.FPortCuryCode,p.FPayType,q.FIVType from " +
                    " (select * from "+pub.yssGetTableName("Tb_Para_InvestPay") + " where fcheckstate=1) a join " +
                    "(select FIVPayCatCode,FPortClsCode,max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("Tb_Para_InvestPay") +
                    " where FStartDate <= " + dbl.sqlDate(dDate) +
                    " and FCheckState = 1 and FPortCode = " +
                    dbl.sqlString(sPortCodeAry[i]) +
                    " and fportclscode=' '"+//bug4704 add by zhouwei 20120601 RQFII分级产品，会产生多笔管理费，受托费等
                    " group by FIVPayCatCode,FPortClsCode) r on a.FIVPayCatCode = r.FIVPayCatCode and a.FStartDate = r.FStartDate " + //group by 加了FPortClsCode fangjiang 2012.02.28
                    //-------------------------------------------------------------------
                    " left join (select FIVPayCatCode,FIVPayCatName from " +
                    " Tb_Base_InvestPayCat" +
                    " where FCheckState = 1) m on a.FIVPayCatCode = m.FIVPayCatCode " + 
                    //----------------------------------------------------------------------
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
           
                    " left join (select  FPortCode, FPortCury as FPortCuryCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +                                
                    " where FCheckState = 1 and FASSETGROUPCODE = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +           
                    " ) z on a.FPortCode = z.fportcode" +
              
                    // end by lidaolong
                    //-----------------------------------------------------
                    " left join (select FIVPayCatCode,FPayType from Tb_Base_InvestPayCat where FCheckState = 1)" +
                    " p on a.FIVPayCatCode=p.FIVPayCatCode " +
                    //-----------------------------------------------------------------
                    " left join (select FIVPayCatCode,FIVType from Tb_Base_InvestPayCat where FCheckState = 1)" +
                    " q on a.FIVPayCatCode = q.FIVPayCatCode " +
                    //因为前台界面收支币种由现金账户替换，故需通过关联现金账户来获取收支币种货币代码，MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A panjunfang add 20090629
//                    " left join (select FCashAccCode,FCuryCode,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_CashAccount") + " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FCheckState = 1 and FState =0 group by FCashAccCode,FCuryCode) CA on CA.FCashAccCode = a.FCashAccCode " +
                    //------------------------------------------------------------------------------------------
                    " where a.FPortCode=" + dbl.sqlString(sPortCodeAry[i]) +
                    " and a.FIvPayCatCode in ( " + operSql.sqlCodes(selCodes) +
                    ") and a.fportclscode=' '";//story 2253 add by zhouwei 20120221 只筛选无组合分级的运营品种，对于（管理费，托管费，受托费）相当于汇总标志

                //---------------------------------------------------------------

                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    //如果运营品种对应的现金账户被反审核、删除了，那么要给出提示，否则会因为取不到币种儿出现异常
                    //sunkey@Modify 20090810 MS00018:QDV4.1赢时胜（上海）2009年4月20日18_A
                	// add by wuweiqi 20110303  #2279   农行需求 
                	 stCashAccCodes=rs.getString("FCashAccCode");
                	 str="select FCuryCode from " +
                	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
                     pub.yssGetTableName("Tb_Para_CashAccount") + " where  FCheckState = 1 and FState =0 and FCashAccCode in ("+ operSql.sqlCodes(stCashAccCodes) +")";
        	
                	 //end by lidaolong
                	 rsStr = dbl.queryByPreparedStatement(str); //modify by fangjiang 2011.08.14 STORY #788
                	 CuryCode = ""; //先清除，add by wangzuochun 2011.03.30 BUG #1587 同时计提两费（管理费、托管费）会导致净值重复出现管理费
                	 while(rsStr.next())
                	 {  
                		 CuryCode1 = rsStr.getString("FCuryCode");
                		 CuryCode+=CuryCode1+",";
                	 }
                	 //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
                	 dbl.closeResultSetFinal(rsStr);
                	 
                	 if(!CuryCode.equals("")){
                		 CuryCode=CuryCode.substring(0,CuryCode.length()-1); 
                   	}
                    if (CuryCode == null || CuryCode.equals("")) {
                    	/**Start 20131206 modified by liubo.Bug #85111. QDV4赢时胜(上海)2013年12月6日06_B
                    	 * 更正缺少现金账户的提示信息*/
                        throw new YssException("对不起，运营收支品种【" + rs.getString("FIVPayCatName") + "】的现金账户未审核或已被删除!" +
                                               "\n请到 组合业务参数-运营费用设置 中重新设置对应的现金账户，谢谢！");
                        /**End 20131206 modified by liubo.Bug #85111. QDV4赢时胜(上海)2013年12月6日06_B*/
                    }
                    if(rs.getDate("FACBeginDate") != null && YssFun.dateDiff(rs.getDate("FACBeginDate"),dDate) < 0){//panjunfang add 20090901 如果运营品种类型为预提待摊，则当计提日期大于等于开始日期才计提而不是以启用日期为基准。
                        continue;
                    }
                    pay = new InvestFeeBean();
                    ivpBean = new InvestPayBean();
                    ivpBean.setIvPayCatCode(rs.getString("FIVPayCatCode") + "");
                    ivpBean.setIvPayCatName(rs.getString("FIVPayCatName") + "");
                    ivpBean.setRoundCode(rs.getString("FRoundCode") + "");
                    ivpBean.setAcroundCode(rs.getString("FACRoundCode") + "");
                    ivpBean.setPerExpCode(rs.getString("FPerExpCode") + "");
                    ivpBean.setFixRate(rs.getBigDecimal("FFixRate")); //panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                    ivpBean.setPeriodCode(rs.getString("FPeriodCode") + "");
                    ivpBean.setPayOrigin(rs.getInt("FPayOrigin"));
                    ivpBean.setAccrueTypeCode(rs.getString("FAccrueType")); //计提方式 MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A panjunfang add 20090715
                    ivpBean.setfACBeginDate(rs.getDate("FACBeginDate"));
                    ivpBean.setfACEndDate(rs.getDate("FACEndDate"));
                    ivpBean.setfACTotalMoney(rs.getDouble("FACTotalMoney"));
                    ivpBean.setFIVType(rs.getString("FIVType"));
                    ivpBean.setIvPayType(rs.getInt("FPayType"));
                    ivpBean.setExpirDate(rs.getDate("FEXPIRDATE")); //添加到期日期的处理 modify by wangzuochun 2009.06.23 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
					ivpBean.setHolidaysCode(rs.getString("FHOLIDAYSCODE"));
					ivpBean.setApportionType(rs.getString("FApportionType"));	//20110810 added by liubo.Story #1227.获取均摊方式
					ivpBean.setTransition(rs.getString("FTransition"));			//20120217 added by liubo.Story 2139.预提转待摊
					ivpBean.setTransitionDate(rs.getDate("FTransitionDate"));	//20120217 added by liubo.Story 2139.预提转待摊中的转换日期
					ivpBean.setPaidIn(rs.getDouble("FPaidIn"));					//20120217 added by liubo.Story 2139.预提转待摊中的实收金额
                    
                    // Start MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                    if (ivpBean.getFIVType().equalsIgnoreCase("DEFERREDFEE")) { //若运营品种类型为待摊，则计提为16类型。
                        ivpBean.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_PAYOUT);
                        ivpBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_PRE_PAYOUT);
                    } else { //若运营品种类型为预提、两费，则计提为07
                        ivpBean.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
                        ivpBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_IV_Pay);
                    }
                    
                    ivpBean.setPayDate(dbl.clobStrValue(rs.getClob("FPayDate")));	//20120512 added by liubo.Story #2217.支付日期设置
                    ivpBean.setLimitedAmount(rs.getDouble("FLimitedAmount"));	//20120512 added by liubo.Story #2217.支付下限
                    ivpBean.setPeriodOfBC(rs.getString("FPeriodOfBC"));			//20120512 added by liubo.Story #2217.补差时期
                    ivpBean.setLowerCurrencyCode(rs.getString("FLowerCurrencyCode")); // add by huangqirong 2013-01-22 story #3488下限币种 
                    ivpBean.setSupplementDate(rs.getString("FSupplementDates")); // add by huangqirong 2013-01-22 story #3488补差日期 
                    
                    // End MS00017 panjunfang modify 2009.06.29 =====================   
                    BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    		CuryCode,
                        sPortCodeAry[i], YssOperCons.YSS_RATE_BASE);
                    //组合汇率
                    //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                    rateOper.getInnerPortRate(dDate, CuryCode, sPortCodeAry[i]);
                    PortCuryRate = rateOper.getDPortRate();
                    //-----------------------------------------------------------------------------------
                    pay.setPortCury(CuryCode);
                    pay.setBaseRate(BaseCuryRate);
                    pay.setPortRate(PortCuryRate); 
                    pay.setPortCode(rs.getString("FPortCode") + "");
	                pay.setInvMgrCode(analy1 ? rs.getString("FAnalysisCode1") + "" : "");
	                pay.setIvpBean(ivpBean);
	                pay.setCurrentDate(dDate);
	                //CuryCode=rs.getString("FCurrenyCode");// #2279  add by wuweiqi 20110126 获取币种
	                /*if(!getAutoCharge(sPortCodeAry[i])&& CuryCode.indexOf(",")!=-1){
	                	throw new YssException(sPortCodeAry[i]+"组合下现金账户不能为多个账户！！"); 
	                }*/
                    //--------sj modified MS00052 -----------------------------------------------------
	                //中保管理费计提需求 by leeyu 20100713 QDV4中保2010年06月18日03_A MS01332
	                if(rs.getInt("FPayOrigin")==2){//如果收支来源为“月末净值”
	                	//这里要判断月末的发生额；判断日期是否为月末，若不是则不计提
	                	if(!YssFun.formatDate(beginDate,"yyyyMM").equalsIgnoreCase(YssFun.formatDate(endDate,"yyyyMM"))){
	                		throw new YssException("计息的开始日期与结束日期的年月不同，选择日期区间错误！"); 
	                	}
	                	if(!(YssFun.formatDate(YssFun.addDay(beginDate,1), "yyyy-MM-dd").equalsIgnoreCase(YssFun.formatDate(beginDate,"yyyy-MM")+"-02"))){
	                		throw new YssException("所选起始日期不是当月的月初日期，请重新选择日期");
	                	}
	                	if(!(YssFun.formatDate(YssFun.addDay(endDate,1), "yyyy-MM-dd").equalsIgnoreCase(YssFun.formatDate(YssFun.addMonth(endDate, 1),"yyyy-MM")+"-01"))){
	                		throw new YssException("所选截止日期不是当月的月末，请重新选择日期");
	                	}
	                	if(YssFun.formatDate(YssFun.addDay(dDate,1), "yyyy-MM-dd").equalsIgnoreCase(YssFun.formatDate(YssFun.addMonth(endDate, 1),"yyyy-MM")+"-01")){
	                		pay.getIvpBean().setFAnalysisCode1(rs.getString("FAnalysisCode1")==null?" ":rs.getString("FAnalysisCode1"));
	                		pay.getIvpBean().setFAnalysisCode2(rs.getString("FAnalysisCode2")==null?" ":rs.getString("FAnalysisCode2"));
	                		pay.getIvpBean().setFAnalysisCode3(rs.getString("FAnalysisCode3")==null?" ":rs.getString("FAnalysisCode3"));
	                		invest = calcFees(pay,analy1,analy2,analy3);
	                	}else{
	                		continue;
	                	}
	                }	 
	                else{
	                	//中保管理费计提需求 by leeyu 20100713
	                	if (existCalcInvestCode(rs.getString("FIVPayCatCode"),
	                                            paraInvestCodes)) { //如果存在需要用公式计算的费用
	                    	formula = (String)investAndFormula.get(rs.getString("FIVPayCatCode")); //add by fangjiang 2011.10.26 STORY #1589
	                    	 //---------------------------add by wuweiqi 20110110 资产估值后自动计提两费  QDV4建信2010年12月17日01_A--------------------------------------------//
	                    	/**shashijie 2012-7-2 STORY 2475 */
	                    	if((paraInvestFormula!=null ? paraInvestFormula[0]
                                 : "").equals("CaltrusFee")) {  //QDV4工银2010年12月22日01_A 专用算法  
                    		/**end*/
	                			invest = getCalcWithFormula(pay, ivpBean,
					                      formula,
					                      dDate, analy1, analy2, analy3);
	                        } else if(formula.equals("CalcFeeModay")){//QDV4建信2010年12月17日01_A 建信全球机遇基金计提管理费	
	                        	invest = getCalcWithFormula(pay, ivpBean,
	                        			  formula,
					                      dDate, analy1, analy2, analy3);
	                        //add by fangjiang 2011.10.26 STORY #1589
	                        } else if(formula.equals("ManageFee(Total)") || formula.equals("TrusteeFee(Total)") 
	                        //edit by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
	                        || formula.equals("FAFee(Total)")){ 
	                        	invest = getCalcWithFormula(pay, ivpBean,
	                        			  formula,
					                      dDate, analy1, analy2, analy3);
	                        } 
	                        //----------------------------end by wuweiqi 20110110 --------------------------------------------------------------------//
	                        else {
	                        	//------- 判断之前的计提过程中,按月计提的类型是否已计提过.若计提过,则向用户抛出提示信息.MS00270 QDV4赢时胜（上海）2009年2月25日01_B ------------------------
	                        	checkIVPayHasCalc(rs.getString("FIVPayCatCode"), rs.getString("FPortCode"), analy1 ? rs.getString("FAnalysisCode1") + "" : "", this.getAlResult());
		                        
	                        	//---------------------------------------------------------------------------------------------------------------------------------------------
		                        if (!existInvestPay(rs.getString("FIVPayCatCode"),
		                                            rs.getString("FPortCode"), dDate)) { //如果在本月没有计提过费用
		                            invest = getCalcWithFormula(pay, ivpBean,
	                            		formula,
		                                dDate, analy1, analy2, analy3);
		                        }
	                        }
	                    } else if(rs.getString("FARIEXPCODE") != null) { 
	                    	formula = rs.getString("FARIEXPCODE");
	                    	invest = getCalcWithFormula(pay, ivpBean,
				                      formula,
				                      dDate, analy1, analy2, analy3); //modify by zhangjun 2011.12.29 story 1273
	                    }else {
	                    	 //#2279  add by wuweiqi 20110126 计提两费时管理费和托管费区分境外和境内
	                    	if(getAutoCharge(sPortCodeAry[i]) && CuryCode.indexOf(",")!=-1 && 
	                    	    ("IV00101".equalsIgnoreCase(rs.getString("Fivpaycatcode")) || 
	                    	    "IV00102".equalsIgnoreCase(rs.getString("Fivpaycatcode")) ||
	                    	    "IV00201".equalsIgnoreCase(rs.getString("Fivpaycatcode")) ||
	                    	    "IV00202".equalsIgnoreCase(rs.getString("Fivpaycatcode")))){
	                    		String[] curyCode_arr = null;
	                    	    String cury = "";
	                    	    String[] cury_arr = null;
	                    		HashSet h = new HashSet();
	                         	curyCode_arr = CuryCode.split(",");
	                         	for(int k=0; k<curyCode_arr.length; k++) {
	                         		h.add(curyCode_arr[k]);
	                         	}
	                         	Iterator it = h.iterator();
	                         	while(it.hasNext()){
	                         		cury += it.next().toString() + ",";
	                         	}
	                         	if(cury.endsWith(",")){
	                         		cury = cury.substring(0,cury.length()-1);
	                         	}
	                         	cury_arr = cury.split(",");
	                         	                           
	                         	for(int j=0; j<cury_arr.length; j++) {
	                         		InvestFeeBean invest_tmp = (InvestFeeBean)pay.clone();
	                         		InvestFeeBean invest1 = calculateIncome(invest_tmp, cury_arr[j], rs.getString("Fivpaycatcode"), rs.getString("FCashAccCode"));
	                         		reIncomes.add(invest1);
	                         	}                  		
	                    		continue;
	                    	}else{
	                            invest = calculateIncome(pay); 
	                    	}
	                    }
           	   		}//中保管理费计提需求 by leeyu 20100713
                    //---------------------------------------------------------------------------------
                    /*
                     hmPubIVFees.put(rs.getString("FIVPayCatCode"), pay);
                                }

                                dbl.closeResultSetFinal(rs);

                     Iterator it = hmPubIVFees.values().iterator(); //把Hash表里面的数据转到ArrayList里面来

                                while (it.hasNext()) {
//               investTmp = (InvestFeeBean) it.next();
//               invest = new InvestFeeBean();
//               invest.setPortCode(sPortCodeAry[i]);
                                   invest = (InvestFeeBean) it.next();

                                   strSql = "select FPortCury from " +
                                         pub.yssGetTableName("Tb_Para_Portfolio") +
                                         " where FCheckState = 1 and FPortCode = " +
                                         dbl.sqlString(sPortCodeAry[i]);

                                   rs = dbl.openResultSet(strSql);
                                   if (rs.next()) {
                                      portCury = rs.getString("FPortCury");
                                   }

                                   dbl.closeResultSetFinal(rs);

                     BaseCuryRate = this.getSettingOper().getCuryRate(dDate, portCury,
                     sPortCodeAry[i], YssOperCons.YSS_RATE_BASE);
                                   //组合汇率
                     PortCuryRate = this.getSettingOper().getCuryRate(dDate, portCury,
                     sPortCodeAry[i], YssOperCons.YSS_RATE_PORT);

                                   invest.setIvpBean(investTmp.getIvpBean());
                                   invest.setCurrentDate(dDate);
                                   invest.setPortCury(portCury);
                                   invest.setBaseRate(BaseCuryRate);
                                   invest.setPortRate(PortCuryRate);

                                   invest = calculateIncome(invest);
                     */
                    reIncomes.add(invest);
                    

                }
            }

            return reIncomes;
        } catch (Exception e) {
        	//---add by songjie 2013.01.09 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	try{
                logOper.setDayFinishIData(this,7,operType, pub, true, 
                		portCode, dDate,
                		new Date(), dDate,
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                		(logInfo + "\r\n计提费用出错\r\n" + e.getMessage())
                		.replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", ""),
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                		" ",logBeginDate,logSumCode,new Date());
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---add by songjie 2013.01.09 STORY #2343 QDV4建行2012年3月2日04_A end---//
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	finally{
        		//by 曹丞 2009.01.24 计算营运收支费用异常信息 MS00004 QDV4.1-2009.2.1_09A
        		throw new YssException("系统计算运营收支费时出现异常!" + "\n", e); 
        	}
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsStr);
        }
    }

    /**
     * getPubInvestFees
     * 获取公共的两费设置信息，条件是在Tb_Para_InvestPay表中FPortCode为空的
     * 返回类型是HashMap,Key为运营收支品种代码(FIVPayCatCode),Value为InvestPayBean的实例
     */
    public HashMap getPubInvestFees(java.util.Date dDate) throws YssException {
        HashMap hmResult = new HashMap();
        ResultSet rs = null;
        String strSql = "";
        InvestPayBean ivpBean = null;
        InvestFeeBean investfee = null;

        try {
            strSql =
                "select a.*,m.FIVPayCatName,p.FPayType,q.FIVType from " +
                pub.yssGetTableName("Tb_Para_InvestPay") + " a join " +
                "(select FIVPayCatCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_InvestPay") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FIVPayCatCode,FStartDate) r on a.FIVPayCatCode = r.FIVPayCatCode and a.FStartDate = r.FStartDate " +
                //-------------------------------------------------------------------
                " left join (select FIVPayCatCode,FIVPayCatName from " +
                " Tb_Base_InvestPayCat" +
                " where FCheckState = 1) m on a.FIVPayCatCode = m.FIVPayCatCode " +
                //-------------------------------------------------------------------
                " left join (select FIVPayCatCode,FPayType from Tb_Base_InvestPayCat where FCheckState = 1)" +
                " p on a.FIVPayCatCode=p.FIVPayCatCode " +
                //-------------------------------------------------------------------
                " left join (select FIVPayCatCode,FIVType from Tb_Base_InvestPayCat where FCheckState = 1)" +
                " q on a.FIVPayCatCode = q.FIVPayCatCode " +
                //-------------------------------------------------------------------
                " where a.FPortCode = ' ' and  a.FIvPayCatCode in (" +
                operSql.sqlCodes(selCodes) + ")";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                investfee = new InvestFeeBean();
                ivpBean = new InvestPayBean();
                ivpBean.setIvPayCatCode(rs.getString("FIVPayCatCode") + "");
                ivpBean.setIvPayCatName(rs.getString("FIVPayCatName") + "");
                ivpBean.setRoundCode(rs.getString("FRoundCode") + "");
                ivpBean.setAcroundCode(rs.getString("FACRoundCode") + "");
                ivpBean.setPerExpCode(rs.getString("FPerExpCode") + "");
                ivpBean.setFixRate(rs.getBigDecimal("FFixRate"));//panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                ivpBean.setPeriodCode(rs.getString("FPeriodCode") + "");
                ivpBean.setPayOrigin(rs.getInt("FPayOrigin"));
//            ivpBean.setIvPayType(rs.getInt("FPayType"));
                ivpBean.setfACBeginDate(rs.getDate("fACBeginDate"));
                ivpBean.setfACEndDate(rs.getDate("fACEndDate"));
                ivpBean.setfACTotalMoney(rs.getDouble("fACTotalMoney"));
                ivpBean.setFIVType(rs.getString("FIVType"));

                // Start MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                if (ivpBean.getFIVType().equalsIgnoreCase("DEFERREDFEE")) { //若运营品种类型为待摊，则计提为16类型。
                    ivpBean.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_PAYOUT);
                    ivpBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_PRE_PAYOUT);
                } else { //若运营品种类型为预提和两费，则计提为07
                    ivpBean.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
                    ivpBean.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_IV_Pay);
                }
                // End MS00017 panjunfang modify 2009.06.29======================
                investfee.setIvpBean(ivpBean);

                hmResult.put(rs.getString("FIVPayCatCode"), investfee);
            }
        } catch (Exception e) {
            throw new YssException("获取公共费用信息出错!" + "\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmResult;
    }

    /*
       //计算得到组合应计提的费用 如果到投资经理层面的话 具体的费用
     public InvestFeeBean calculateIncome(InvestFeeBean bean) throws YssException {
          String strSql = "";
          ResultSet rs = null;
          ResultSet rsSum = null;
          ResultSet rsMail = null; //得到尾差投资经理
          ArrayList InvestFees = new ArrayList(); //这个ArrayList 保存的是费用那个BEAN
          InvestFeeBean obj = null;
          double sumBaseNetValue = 0; //这个组合的基础货币净值总额
          double sumPortNetValue = 0; //这个组合的组合货币净值总额
          double sumBaseFee = 0; //得到的总费用 为了计算尾差费用
          double sumPortFee = 0; //得到的总费用
          double baseInvMgrFee = 0; //按照比例计算出的基础货币的费用值
          double portInvMgrFee = 0; //按照比例计算出的组合货币的费用值
          double sumBaseInvMgrFee = 0; //储存该组合的所有投资经理基础货币所占的费用值
          double sumPortInvMgrFee = 0; //储存该组合的所有投资经理组合货币所占的费用值

          boolean sign = false;//true净值区分投资经理 false净值不区分投资经理，20070806，杨
          try {
             InvestFeeBean pay = bean;
             strSql = "select sum(FBaseNetValue) as FTotalBaseNetValue,sum(FPortNetValue) as FTotalPortNetValue from " +
                   pub.yssGetTableName("Tb_Data_NetValue") +
                   " where FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                   " and FNavDate=" +
                   dbl.sqlDate(YssFun.addDay(pay.getCurrentDate(), -1));

             rsSum = dbl.openResultSet(strSql);
             while (rsSum.next()) {
                sumBaseNetValue = rsSum.getDouble("FTotalBaseNetValue");
     sumBaseFee = this.getSettingOper().calMoneyByPerExp(pay.getIvpBean().
                      getPerExpCode(),
                      pay.getIvpBean().getRoundCode(), sumBaseNetValue);
                sumPortNetValue = rsSum.getDouble("FTotalPortNetValue");
     sumPortFee = this.getSettingOper().calMoneyByPerExp(pay.getIvpBean().
                      getPerExpCode(),
                      pay.getIvpBean().getRoundCode(), sumPortNetValue);
             }
             if (sumBaseNetValue == 0) { //分母不能为零 为零代表没有此费用
                return pay;
             }
             dbl.closeResultSetFinal(rsSum);

             strSql = "select FInvMgrCode from " +
                   pub.yssGetTableName("Tb_Data_NetValue") +
                   " where FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                   " and FNavDate=" +
                   dbl.sqlDate(YssFun.addDay(pay.getCurrentDate(), -1));
             rs = dbl.openResultSet(strSql);
             while(rs.next()){
                if (rs.getString("FInvMgrCode") != null &&
                    rs.getString("FInvMgrCode").equalsIgnoreCase("") &&
                    rs.getString("FInvMgrCode").equalsIgnoreCase(" ")) {
                   sign = true;
                }
             }
             dbl.closeResultSetFinal(rs);

             if(!sign){
                //净值不按投资经理来分
                obj = new InvestFeeBean();
                obj.setInvMgrCode(" ");
                obj.setBaseInvestFee(sumBaseFee);
                obj.setPortInvestFee(sumPortFee);
                InvestFees.add(obj);
             }
             else {
                //查询所有投资组合下投资经理不为空的
     strSql = "select * from " + pub.yssGetTableName("Tb_Data_NetValue") +
                      " where FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                      " and FInvMgrCode != ' '  and FNavDate=" +
                      dbl.sqlDate(YssFun.addDay(pay.getCurrentDate(), -1));
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                   obj = new InvestFeeBean();

     if (pay.getIvpBean().getFixRate() == 0.0) { //计算费用  得到其比例值 乘以 该组合的待摊费用
                      baseInvMgrFee = (rs.getDouble("FBaseNetValue") /
                                       sumBaseNetValue *
                                       sumBaseFee);
                      portInvMgrFee = rs.getDouble("FPortNetValue") /
                            sumPortNetValue *
                            sumPortFee;
                   }

                   sumBaseInvMgrFee = sumBaseInvMgrFee + baseInvMgrFee;
                   sumPortInvMgrFee = sumPortInvMgrFee + portInvMgrFee;
                   obj.setInvMgrCode(rs.getString("FInvMgrCode"));
                   obj.setBaseInvestFee(baseInvMgrFee);
                   obj.setPortInvestFee(portInvMgrFee);
                   InvestFees.add(obj);

                }
                //---------------------------------------------------------------------------
                //通过这个SQL语句 获得尾差投资经理
                strSql = "select * from " +
                      pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                      " where FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                      " and FRelaGrade='1' and FRelaType='InvestManager'";
                rsMail = dbl.openResultSet(strSql);
                if (rsMail.next()) {
                   obj.setInvMgrCode(rs.getString("FInvMgrCode"));
     obj.setBaseInvestFee(sumBaseFee - sumBaseInvMgrFee); //总费用 减去 已经扣去的费用
                   obj.setPortInvestFee(sumPortFee - sumPortInvMgrFee);
                   InvestFees.add(obj); //增加到尾差投资经理
                }
             }
             //------------------------------------------------------------------------------
             pay.setInvMgrInvestFee(InvestFees);
             return pay;
          }
          catch (Exception e) {
             throw new YssException("计算费用信息出错", e);
          }
          finally {
             dbl.closeResultSetFinal(rs);
             dbl.closeResultSetFinal(rsMail);
          }

       }
     */
    
	/****
	 *#2279::  add by wuweiqi 20110124 农行需求
	 *判断通用参数中计提两费是否区分境内外资产
	 * 返回true时区分，false不区分
	 * boolean 
	 */
    public boolean getAutoCharge(String sPortCodeAry) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ResultSet rsTemp = null;
        String FctlValue ="";
        String FctlValue1="";
        String strCode="";
        boolean isExit=false;
        try {
         	strSql= " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
         			" where FPUBPARACODE = 'incomeFee' and FPARAID!=0 and FCtlCode='selctlPortCode'";
        	 rs = dbl.queryByPreparedStatement(strSql);   //modify by fangjiang 2011.08.14 STORY #788
	         while(rs.next()){
	             FctlValue = rs.getString("FCTLVALUE");
	             strCode=FctlValue.substring(0,FctlValue.indexOf("|"));
	             if(strCode.equals(sPortCodeAry)){
	            	 strSql = " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
		    	              " where FPUBPARACODE = 'incomeFee' " +
		    	              " and FCtlCode = 'cobYesNo'" +
		    	              " and FPARAID=(select min(FPARAID) from "+
		    	              pub.yssGetTableName("TB_PFOper_PUBPARA") +
		    	              " where FPUBPARACODE ='incomeFee' and FPARAID!=0 and FCTLVALUE=" +
		    	              dbl.sqlString(FctlValue) +")" ;
	            	 rsTemp = dbl.queryByPreparedStatement(strSql);  //modify by fangjiang 2011.08.14 STORY #788
   		              while(rsTemp.next()){
    		        	  FctlValue1 = rsTemp.getString("FCTLVALUE");
    		          } 
    		          if(FctlValue1.substring(0, FctlValue1.indexOf(",")).equals("1")){
    		        	  isExit=true;
   		              }    
	            	  break;
	             }
	         }
           return isExit;
        }
        catch (Exception e) {
           throw new YssException("计算费用信息出错", e);
        }
        finally {
           dbl.closeResultSetFinal(rsTemp);//modified by yeshenghong BUG3958
           dbl.closeResultSetFinal(rs);
        }
     }   

//计算得到组合应计提的费用 如果到投资经理层面的话 具体的费用
    //费用计提方式：
    //原币金额：按照组合货币净值计提，
    //基础货币金额：原币金额*基础汇率
    //组合货币金额：原币金额*基础汇率/组合汇率
    public InvestFeeBean calculateIncome(InvestFeeBean bean) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ResultSet rsSum = null;
        ResultSet rsMail = null; //得到尾差投资经理
        ArrayList InvestFees = new ArrayList(); //这个ArrayList 保存的是费用那个BEAN
        InvestFeeBean obj = null;
        double sumPortNetValue = 0; //这个组合的组合货币净值总额
        double sumFee = 0; //得到的总费用
        double invMgrFee = 0; //按照比例计算出的组合货币的费用值
        double sumInvMgrFee = 0; //储存该组合的所有投资经理组合货币所占的费用值

        double aveFee = 0; 		//预提待摊日平均费用
        double curFee = 0; 		//今日的费用
        double yesterFee = 0; 	//昨日的费用
        boolean sign = false; 	//true净值区分投资经理 false净值不区分投资经理，20070806，杨
        boolean yes = false; 	//yes为true，则按昨目净值计提，yes为false，则按今日净值计提，20070810，杨
        
        Date dNavDate = null;	//净值表日期 sunkey@Modify 20091125
        String sHDayCode = "";	//节假日代码 
        double supplementMoney = 0;//补差金额 add by huangqirong 2013-02-01 story #3488
        
        //=============这里根据估值净值表来判断 属于中保版本还是国内版本。MS00064 by leeyu 2008-12-4
        com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara clPub = null; // add by leeyu
        String sNavType = "";
        //================2008-12-4

        //Start MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A
        ResultSet rsValDay = null;          //获取计提日对应的估值日
        ResultSet rsValSum = null;          //获取估值日资产净值
        java.util.Date valDay = null;       //计提日对应的估值日
        double valSumPortMarketValue = 0;   //估值日资产净值总额
        double valSumFee = 0;               //按估值日计提到的总费用
        //End MS00018 panjunfang add 20090715 ========================


        try {
            InvestFeeBean pay = bean;
            ////add baopingping 2011.07.04 #Story 1138 添加对指数费的处理
            Date GussData=YssFun.addDay(pay.getCurrentDate(), -1);//取财务估值表中昨日的日期 
            BaseOperDeal bOper=new  BaseOperDeal();
            bOper.setYssPub(pub);
            IncomeStatBean Incom=new IncomeStatBean();
            Incom.setYssPub(pub);
            //String Fport=Incom.GetPara(pay.getPortCode(),"selPort");
            String Type=Incom.GetPara(pay.getPortCode(),"cbxType");
            String Fport1=Incom.GetPara(pay.getPortCode(),"selPort1");
            String Money=Incom.GetPara(pay.getPortCode(),"txtDays");
            String FNet=Incom.GetPara(pay.getPortCode(),"selNet");
            String Sport=Incom.GetPara(pay.getPortCode(),"selPort");
            
            double dRatePara = 1.0;		//20111202 added by liubo.
            double dRateNav = 1.0;		//20111202 added by liubo.
            
            String Scode=null;
            if(Sport!=null)
            {
            Scode=Sport.substring(0,FNet.indexOf('|')+1);
            }
            String  FivCode=null;
            String FivType=null;
            String FivName=null;
            double Money_1=0;
            String FPort1Code="1.0";
            String M_oney_1="";
            String[] M_oney=null;
            if(FNet!=null)
            {
         	   FivName=FNet.substring(FNet.indexOf('|')+1,FNet.length());
         	   FivCode=FNet.substring(0,FNet.indexOf('|'));
            }
            if(Type!=null)
            {
         	   FivType=Type.split(",")[0];
         	   
            }
            if(FivType==null){
       		  FivType="0";
       	   }
            if(Money!=null)
            {
            M_oney=Money.split(",");
            for(int i=0;i<M_oney.length;i++)
            {
            	M_oney_1+=M_oney[i];
            }
            Money_1 =Double.valueOf(M_oney_1).doubleValue();
            }
            if(Fport1 !=null)
            {
            	FPort1Code=Fport1.substring(0,Fport1.indexOf('|'));
            }
            
            //20111202 added by liubo.Bug #3193
            //原先代码中，未对组合货币和通用参数所选择的币种进行匹配。这里统一转换成基础货币，再进行比对
            //=============================
            BaseOperDeal oper = new BaseOperDeal();	
            
            oper.setYssPub(pub);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            
            dRatePara = oper.getCuryRate(GussData,FPort1Code,pay.getPortCode(),YssOperCons.YSS_RATE_BASE);
            Money_1 = Money_1 * dRatePara;
            //=============================
            
            
            //20120512 added by liubo.Story #2217
            //若投资运营收支字段的支付下限不为0，则需要对计提金额做一些特殊判断
            //===============================
            boolean bIfTheDateOfBC = false;		//计提日期是否符合补差日条件
            double dTotal  = 0.0;				//已计提金额。该变量会加上当日计提出的金额，然后与投资运营收支界面的支付下限中设置的值进行比较
            ResultSet rsTemp = null;
            Calendar cDay1 = Calendar.getInstance();   
            String sDateOfBC = "";				//记录补差日的日期 
            
            //当投资运营收支设置界面的补差日期为0，即计提月底费用时补差，则每月的最后一天为补差日。
            if (pay.getIvpBean().getPeriodOfBC().equals("0"))
            {
            	cDay1.setTime(pay.getCurrentDate());
            	if (String.valueOf(cDay1.getActualMaximum(Calendar.DATE)).equals(YssFun.formatDate(pay.getCurrentDate(),"dd")))
            	{
            		sDateOfBC = YssFun.formatDate(pay.getCurrentDate(),"yyyy-MM-dd");
            		bIfTheDateOfBC = true;
            	}
            }
            //start modify huangqirong 2013-01-22 story #3488
            //当投资运营收支设置界面的补差日期为1，即支付日前一日计提时补差，则判断当日是否为“支付日期设置”中已设置日期的前一自然日，是则为“补差日”
            else if("1".equalsIgnoreCase(pay.getIvpBean().getPeriodOfBC()))
            {
            	if(pay.getIvpBean().getPayDate() != null && pay.getIvpBean().getPayDate().trim().length() > 0)
            	{
            		String[] sPayDateList = pay.getIvpBean().getPayDate().split(",");
            		for (int i = 0; i < sPayDateList.length; i++)
            		{
            			if (!sPayDateList[i].trim().equals(""))
            			{
	            			if (YssFun.formatDate(sPayDateList[i],"yyyyMMdd").equalsIgnoreCase(YssFun.formatDate(YssFun.addDay(pay.getCurrentDate(), 1),"yyyyMMdd")))
	            			{
	            				sDateOfBC = YssFun.formatDate(pay.getCurrentDate(),"yyyy-MM-dd");
	            				bIfTheDateOfBC = true;
	            				break;
	            			}
            			}
            		}
            	}
            }else if("2".equalsIgnoreCase(pay.getIvpBean().getPeriodOfBC())){
            	if(pay.getIvpBean().getSupplementDate() != null && pay.getIvpBean().getSupplementDate().trim().length() > 0)
            	{
            		String[] supplementDates = pay.getIvpBean().getSupplementDate().split(",");
            		for (int i = 0; i < supplementDates.length; i++)
            		{
            			if (!supplementDates[i].trim().equals(""))
            			{
	            			if (YssFun.formatDate(supplementDates[i],"yyyyMMdd").equalsIgnoreCase(YssFun.formatDate(pay.getCurrentDate(),"yyyyMMdd")))
	            			{
	            				sDateOfBC = YssFun.formatDate(pay.getCurrentDate(),"yyyy-MM-dd");
	            				bIfTheDateOfBC = true;
	            				break;
	            			}
            			}
            		}
            	}
            }
            
            if (bIfTheDateOfBC)
            {
            	strSql = "Select * from " + pub.yssGetTableName("Tb_Stock_Invest") + " where FIVPayCatCode = " + dbl.sqlString(pay.getIvpBean().getIvPayCatCode()) +
            			 " and FPortCode = " + dbl.sqlString(pay.getPortCode()) + " and FStorageDate = " + dbl.sqlDate(YssFun.addDay(YssFun.toDate(sDateOfBC), -1));
            	
            	rsTemp = dbl.queryByPreparedStatement(strSql);
            	
            	while(rsTemp.next())
            	{
            		dTotal = rsTemp.getDouble("FBal");
            	}
            	
            	dbl.closeResultSetFinal(rsTemp);
            	
            }
            //==============end=================
            
//
//            double testBaseRate = oper.getCuryRate(ddd,"USD","QD160121",YssOperCons.YSS_RATE_BASE);
//            double testPortRate = oper.getCuryRate(ddd,"USD","QD160121",YssOperCons.YSS_RATE_PORT);
            
            //-------end--------------
            //pay.getIvpBean().getPayOrigin()==0,按昨日净值计提
            //pay.getIvpBean().getPayOrigin()==1，按今日净值计提
            if (pay.getIvpBean().getFIVType().equalsIgnoreCase("managetrusteeFee")) { //收益品种类型为两费，20090630 panjunfang mofify，MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                if (pay.getIvpBean().getAccrueTypeCode().equalsIgnoreCase("evedaynav")//按工作日资产净值计提
                    //edit by songjie 2011.04.18 BUG 1676 QDV4中银基金2011年04月11日01_B 按自然日资产净值计提
                	|| pay.getIvpBean().getAccrueTypeCode().equalsIgnoreCase("evendaynav")) { //按每日资产净值计提 MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A panjunfang add 20090715
            	//添加节假日获取，如果设置了节假日则昨日净值应该取上一工作日净值 sunkey@Modify 20091125
            	yes = pay.getIvpBean().getPayOrigin() == 0 ? true : false;
            	sHDayCode = pay.getIvpBean().getHolidaysCode();
//            	//若收支来源为昨日净值  且按工作日资产净值计提的话
//            	if(yes && pay.getIvpBean().getAccrueTypeCode().equalsIgnoreCase("evedaynav")){
//            		if(sHDayCode == null || sHDayCode.equals("")){ //modify by wangzuochun 2010.02.25  MS01002   收益计提-两费计提报错   QDV4赢时胜上海2010年02月25日01_B  
//            			dNavDate = YssFun.addDay(pay.getCurrentDate(), -1);
//            		}else{
//            			dNavDate = super.getSettingOper().getWorkDay(sHDayCode, pay.getCurrentDate(), -1);
//            		}
//            	}else{
//            		dNavDate = pay.getCurrentDate();
//            	}
            	//若选择工作日资产净值
            	if(pay.getIvpBean().getAccrueTypeCode().equalsIgnoreCase("evedaynav")){
            		if(yes){//若选择昨日净值
                		if(sHDayCode == null || sHDayCode.equals("")){ //modify by wangzuochun 2010.02.25  MS01002   收益计提-两费计提报错   QDV4赢时胜上海2010年02月25日01_B  
                			dNavDate = YssFun.addDay(pay.getCurrentDate(), -1);
                		}else{
                			dNavDate = super.getSettingOper().getWorkDay(sHDayCode, pay.getCurrentDate(), -1);
                		}
            		}else{
            			dNavDate = pay.getCurrentDate();
            		}
            	}else{//若选择自然日资产净值
            		if(yes){//若选择昨日净值
                		dNavDate = YssFun.addDay(pay.getCurrentDate(), -1);
            		}else{
            			dNavDate = pay.getCurrentDate();
            		}
            	}
                //================这里根据估值净值表来判断 属于中保版本还是国内版本。MS00064 by leeyu 2008-12-4
                clPub = new com.yss.main.operdeal.platform.pfoper.pubpara.
                    CtlPubPara(); //add by leeyu
                clPub.setYssPub(pub);
                sNavType = clPub.getNavType();
                //=====================2008-12-4
                //add baopingping 2011.07.04 #Story 1138 添加对指数费的处理          
                EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
                rateOper.setYssPub(pub);
                double FMarketValue=0;
                double PortCuryRate=0;
                double BaseCuryRate=0;
                if(pay.getIvpBean().getIvPayCatName().equalsIgnoreCase(FivName) && FNet!=null)//根据选择的费用和通用参数设置的费用对比
            	{
	    			if(FivType.equalsIgnoreCase("0"))
	    			{
	    				String sql ="select * from "+pub.yssGetTableName("Tb_Rep_GuessValue")+" where Fdate="+dbl.sqlDate(GussData)+
		    			" and FacctCode='8800'" +
		    			" and FcurCode=' '";
		    			rs=dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
		    			while(rs.next())
		    			{
		    				//20111202 modified by liubo.Bug #3193
		    				//============================
		    				dRateNav = oper.getCuryRate(GussData,FPort1Code,pay.getPortCode(),YssOperCons.YSS_RATE_BASE);
		    			    FMarketValue=rs.getDouble("FStandardMoneyMarketValue");
		    			    if(pay.getPortCury()==FPort1Code){
		    			    	sumPortNetValue=FMarketValue;
		    			    	}
//		    			    else if(FPort1Code.equalsIgnoreCase("USD")){
//		    			    	sumPortNetValue=FMarketValue*pay.getBaseRate();
//		    			    }
		    			    else{
		    			    	sumPortNetValue=FMarketValue*dRateNav;
		    			    }
		    				//============================
		    			    //sumPortNetValue=bOper.converMoney(pay.getPortCury(),FPort1Code, FMarketValue, pay.getBaseRate(), pay.getPortRate(),8);//将查询到的币种金额转换成通用参数设置的币种金额
		    			}
	    				if(sumPortNetValue>Money_1)
	    				{
	    					if(pay.getIvpBean().getFixRate() == null || pay.getIvpBean().getFixRate().compareTo(new BigDecimal(0.0)) == 0){//如果没有设定固定比率，则按比率公式计算出计提费用,panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                                sumFee = this.getSettingOper().calMoneyByPerExp(pay.getIvpBean().
                                getPerExpCode(),
                                pay.getIvpBean().getRoundCode(), FMarketValue,
                                pay.getCurrentDate());
                            }else{
                                sumFee = new BigDecimal(Double.toString(FMarketValue)).multiply(pay.getIvpBean().getFixRate()).doubleValue();//panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                                sumFee = this.getSettingOper().reckonRoundMoney(pay.getIvpBean().getRoundCode(), sumFee);//按舍入条件进行舍入处理
                            }
	    				}else
	    				{
	    					sumFee=0;
	    				}
	    			}else
	    			{
	    				strSql = "select sum(FBaseNetValue) as FTotalBaseNetValue,sum(FPortNetValue) as FTotalPortNetValue from " +
                        pub.yssGetTableName("Tb_Data_NetValue") +
                        " where FType = '01'" + //取类型为资产净值的 胡昆  20070920
                        " and FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                        " and FNavDate=" + dbl.sqlDate(dNavDate) +
                        //============ 这里根据估值净值表来判断 属于中保版本还是国内版本。MS00064 by leeyu 2008-12-4
                        (sNavType.equalsIgnoreCase("new") ? (" AND FInvMgrCode = ' '") :
                         ""); //2008.07.16 蒋锦 添加投资经理作为查询条件
                        //===========2008-12-4

                          rsSum = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
	                          while (rsSum.next()) {
	                            double  sumPortNetValue1 = rsSum.getDouble("FTotalPortNetValue");
			    				//20111202 modified by liubo.Bug #3193
			    				//============================
	                            dRateNav = oper.getCuryRate(GussData,pay.getPortCury(),pay.getPortCode(),YssOperCons.YSS_RATE_BASE);
	      		    			
	                            if(pay.getPortCury()==FPort1Code){
			    			    	sumPortNetValue=sumPortNetValue1;
			    			    }
//	                            else if(FPort1Code.equalsIgnoreCase("USD")){
//			    			    	sumPortNetValue=sumPortNetValue1/dRateNav;
//			    			    }
			    			    else{
			    			    	sumPortNetValue=sumPortNetValue1*dRateNav;;
			    			    }
	                            //===========end==================
	                        if(sumPortNetValue>Money_1)
	                        {
                              if(pay.getIvpBean().getFixRate() == null || pay.getIvpBean().getFixRate().compareTo(new BigDecimal(0.0)) == 0){//如果没有设定固定比率，则按比率公式计算出计提费用,panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                                  sumFee = this.getSettingOper().calMoneyByPerExp(pay.getIvpBean().
                                  getPerExpCode(),
                                  pay.getIvpBean().getRoundCode(), sumPortNetValue1,
                                  pay.getCurrentDate());
                              }else{
                                  sumFee = new BigDecimal(Double.toString(sumPortNetValue1)).multiply(pay.getIvpBean().getFixRate()).doubleValue();//panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                                  sumFee = this.getSettingOper().reckonRoundMoney(pay.getIvpBean().getRoundCode(), sumFee);//按舍入条件进行舍入处理
                              }
	                         }else{
	                            	  sumFee=0;
	                              }
                          }
                          dbl.closeResultSetFinal(rsSum);
	                         
	    			}
	    			//----end--------------
            	}else{
            		strSql = "select sum(FBaseNetValue) as FTotalBaseNetValue,sum(FPortNetValue) as FTotalPortNetValue from " +
                    pub.yssGetTableName("Tb_Data_NetValue") +
                    " where FType = '01'" + //取类型为资产净值的 胡昆  20070920
                    " and FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                    " and FNavDate=" + dbl.sqlDate(dNavDate) +
                    //============ 这里根据估值净值表来判断 属于中保版本还是国内版本。MS00064 by leeyu 2008-12-4
                    (sNavType.equalsIgnoreCase("new") ? (" AND FInvMgrCode = ' '") :
                     ""); //2008.07.16 蒋锦 添加投资经理作为查询条件
                    //===========2008-12-4

                      rsSum = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                      while (rsSum.next()) {
                          sumPortNetValue = rsSum.getDouble("FTotalPortNetValue");
                          if(pay.getIvpBean().getFixRate() == null || pay.getIvpBean().getFixRate().compareTo(new BigDecimal(0.0)) == 0){//如果没有设定固定比率，则按比率公式计算出计提费用,panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                        	  /**shashijie 2012-9-17 STORY 2974 修改两费计提算法,中间不保留位数 */
                        	  /*sumFee = this.getSettingOper().calMoneyByPerExp(pay.getIvpBean().
                                      getPerExpCode(),
                                      pay.getIvpBean().getRoundCode(), sumPortNetValue,
                                      pay.getCurrentDate());*/
                        	  sumFee = this.getSettingOper().calMoneyByPerExp(pay.getIvpBean().
                                      getPerExpCode(),
                                      sumPortNetValue,
                                      pay.getCurrentDate());
                        	  /**end shashijie 2012-9-17 STORY */
                          }else{
                              sumFee = new BigDecimal(Double.toString(sumPortNetValue)).multiply(pay.getIvpBean().getFixRate()).doubleValue();//panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                              sumFee = this.getSettingOper().reckonRoundMoney(pay.getIvpBean().getRoundCode(), sumFee);//按舍入条件进行舍入处理
                          }
                      }
                
                      dbl.closeResultSetFinal(rsSum);
            	}
                if (sumPortNetValue == 0) { //分母不能为零 为零代表没有此费用
                    return pay; 
                }

                PeriodBean Period = new PeriodBean();
                Period.setYssPub(pub);
                Period.setPeriodCode(pay.getIvpBean().getPeriodCode());
                if (Period.getPeriodCode() == null || Period.getPeriodCode().trim().equals("null") ||
                    Period.getPeriodCode().trim().equalsIgnoreCase("")) {
                    throw new YssException("请先维护" + pay.getIvpBean().getIvPayCatName() +
                                           "的期间设置");
                } // 检查费用期间设置是否正确 by caocheng 09.02.04 MS00004 QDV4.1-2009.2.1_09A
                Period.getSetting();
                //------ modify by wangzuochun 2011.04.24 
                //如果periodType类型是1，则是实际天数类型，需要取得当年的实际天数；
                if (Period.getPeriodType() == 1) {
                	//如果是闰年，实际天数为366
                	/**shashijie 2013-1-14 BUG 6872 投资运营收支设置中设置期间代码为“实际天数”时，2013年1月1日计提的费用错误
                	 * 这里修改成取实际的计提日期,不根据页面上的下拉框推算出的估值日期去取年实际天数 */
                	//if(YssFun.isLeapYear(dNavDate)) {
                	if(YssFun.isLeapYear(pay.getCurrentDate())){
                		sumFee = YssD.div(sumFee, 366); //费用除以期间设置中每年天数
                	}
                	/**end shashijie 2013-1-14 BUG 6872 */
                	//如果不是闰年，实际天数为365
                	else{
                		sumFee = YssD.div(sumFee, 365); //费用除以期间设置中每年天数
                	}
                }
                else {
                	sumFee = YssD.div(sumFee, Period.getDayOfYear()); //费用除以期间设置中每年天数，20070810，杨
                }
                //---------------- BUG 1776 进行收益计提，计提出的管理费并没有除以实际天数 ------------//
                sumFee = this.getSettingOper().reckonRoundMoney(pay.getIvpBean().getRoundCode(), sumFee); //add by fangjiang 2011.04.06 STORY #616 运营应收应付原币金额和汇率计算需要修改 
                
                //20120514 added by liubo.Story #2217
                //当计提日期符合补差日条件时，需要判断昨日费用已计提总金额加上当日按系统当前算法计算出的金额，是否小于“支付下限”金额。
                //若小于，则当日计提的费用金额为“支付下限”金额减去昨日已计提总金额。若大于，则当日计提的费用金额同当前系统计提金额。
                //==================================
                //modify huangqirong 2013-01-24 story #3488
                if("2".equalsIgnoreCase(pay.getIvpBean().getPeriodOfBC()) && bIfTheDateOfBC){
                	String currency = pay.getIvpBean().getLowerCurrencyCode(); //下限币种代码
                	if(currency != null && currency.trim().length() > 0) //设置了下限币种
                	{                		
                		if(!pay.getPortCury().equalsIgnoreCase(currency)){ //下限币种和计提币种不一致                		
	                		double lowerMoney = pay.getIvpBean().getLimitedAmount(); //下限金额
	                		BaseOperDeal operDeal = new BaseOperDeal();
	                		operDeal.setYssPub(pub);	
	                		double lowerBaseRate = operDeal.getCuryRate(pay.getCurrentDate(), currency, pay.getPortCode(), "base");
	                		double payBaseRate = operDeal.getCuryRate(pay.getCurrentDate(), pay.getPortCury(), pay.getPortCode(), "base");
	                		
	                		double payLowerMoney = YssD.div(YssD.mul(lowerMoney, lowerBaseRate) , payBaseRate); //下限币种金额 转成计提币种金额
	                		
	                		double totalToDay = YssD.add(dTotal, sumFee);
	                		supplementMoney =  totalToDay < payLowerMoney ? YssD.sub(payLowerMoney, totalToDay) : 0;
                		}else if(pay.getPortCury().equalsIgnoreCase(currency)){	//下限币种和计提币种不一致
                			double lowerMoney = pay.getIvpBean().getLimitedAmount(); //下限金额
	                		double totalToDay = YssD.add(dTotal, sumFee);
	                		supplementMoney =  totalToDay < lowerMoney ? YssD.sub(lowerMoney, totalToDay) : 0;
                		}
                	}else{	//未设置下限币种
                		double totalToDay = YssD.add(dTotal, sumFee);                    		
                		supplementMoney =  totalToDay < pay.getIvpBean().getLimitedAmount() ? YssD.sub(pay.getIvpBean().getLimitedAmount(), totalToDay) : 0;
                	}
                }else if (bIfTheDateOfBC){
            		String currency = pay.getIvpBean().getLowerCurrencyCode(); //下限币种代码
                	if(currency != null && currency.trim().length() > 0) //设置了下限币种
                	{                		
                		if(!pay.getPortCury().equalsIgnoreCase(currency)){ //下限币种和计提币种不一致                		
	                		double lowerMoney = pay.getIvpBean().getLimitedAmount(); //下限金额
	                		BaseOperDeal operDeal = new BaseOperDeal();
	                		operDeal.setYssPub(pub);	
	                		double lowerBaseRate = operDeal.getCuryRate(pay.getCurrentDate(), currency, pay.getPortCode(), "base");
	                		double payBaseRate = operDeal.getCuryRate(pay.getCurrentDate(), pay.getPortCury(), pay.getPortCode(), "base");
	                		
	                		double payLowerMoney = YssD.div(YssD.mul(lowerMoney, lowerBaseRate) , payBaseRate); //下限币种金额 转成计提币种金额
	                		
	                		double totalToDay = YssD.add(dTotal, sumFee);
	                		supplementMoney =  totalToDay < payLowerMoney ? YssD.sub(payLowerMoney, totalToDay) : 0;
                		}else if(pay.getPortCury().equalsIgnoreCase(currency)){	//下限币种和计提币种不一致
                			double lowerMoney = pay.getIvpBean().getLimitedAmount(); //下限金额
	                		double totalToDay = YssD.add(dTotal, sumFee);
	                		supplementMoney =  totalToDay < lowerMoney ? YssD.sub(lowerMoney, totalToDay) : 0;
                		}
                	}else{
                		if (pay.getIvpBean().getLimitedAmount() > YssD.add(dTotal, sumFee))
                		{
                			//sumFee = YssD.sub(pay.getIvpBean().getLimitedAmount(),dTotal); //之前的计提方式
                			double totalToDay = YssD.add(dTotal, sumFee);
                			supplementMoney = YssD.sub(pay.getIvpBean().getLimitedAmount(),totalToDay);
                		}
                	}
                }
                //================end==================
                
                strSql = "select FInvMgrCode from " +
                    pub.yssGetTableName("Tb_Data_NetValue") +
                    " where FType = '01'" + //取类型为资产净值的  胡昆  20070920
                    " and FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                    " and FNavDate=" + dbl.sqlDate(dNavDate);
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    if (rs.getString("FInvMgrCode") != null &&
                        rs.getString("FInvMgrCode").trim().length() != 0) {
                        sign = true;
                    }
                }
                dbl.closeResultSetFinal(rs);
                if (!sign) {
                    //净值不按投资经理来分
                    obj = new InvestFeeBean();
                    obj.setInvMgrCode(" ");
                    obj.setInvestFee(sumFee);
                    obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(sumFee,
                        pay.getBaseRate()));
                    obj.setPortInvestFee(this.getSettingOper().calPortMoney(sumFee,
                        pay.getBaseRate(), pay.getPortRate(),
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        pay.getPortCury(), pay.getCurrentDate(), pay.getPortCode()));
                    InvestFees.add(obj);
                  //start add by huangqirong 2013-02-01 story #3488
                    if(supplementMoney > 0){
                    	InvestPayBean investPay = (InvestPayBean)pay.getIvpBean().clone();                    	
                    	investPay.setTsfTypeCode(YssOperCons.Yss_ZJDBLX_SUPPLEMENT);//计提补差 调拨类型
                    	investPay.setSubTsfTypeCode(YssOperCons.Yss_ZJDBLX_SUPPLEMENT_IV);//计提补差 调拨子类型         
                    	investPay.setFIVType(pay.getIvpBean().getFIVType());
                    	investPay.setIvPayCatCode(pay.getIvpBean().getIvPayCatCode());
                    	investPay.setFAnalysisCode3(pay.getIvpBean().getFAnalysisCode3());
                    	investPay.setRoundCode(pay.getIvpBean().getRoundCode());
                    	investPay.setAcroundCode(pay.getIvpBean().getAcroundCode());
                    	investPay.setAttrClsCode(pay.getIvpBean().getAttrClsCode());
                    	obj = (InvestFeeBean)pay.clone();
                        obj.setInvMgrCode(" "); //暂不区分投资经理
                        obj.setInvestFee(supplementMoney); //原币计提费用
                        obj.setIvpBean(investPay);
                        obj.setHaveIvpBean(true);
                        obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(supplementMoney, pay.getBaseRate())); //根据原币计提费用和基础汇率算出基础货币计提费用
                        obj.setPortInvestFee(this.getSettingOper().calPortMoney(supplementMoney, pay.getBaseRate(), pay.getPortRate(),
                        		pay.getPortCury(), pay.getCurrentDate(), pay.getPortCode())); //计算组合货币计提费用        
                        InvestFees.add(obj);
                    }
                    //end add by huangqirong 2013-02-01 story #3488
                } else {
                    //查询所有投资组合下投资经理不为空的
                    strSql = "select * from " +
                        pub.yssGetTableName("Tb_Data_NetValue") +
                        " where FType = '01'" + //取类型为资产净值的  胡昆  20070920
                        " and FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                        " and FInvMgrCode != ' '  and FNavDate=" + dbl.sqlDate(dNavDate);
                    rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                    while (rs.next()) {
                        obj = new InvestFeeBean();

                        if (pay.getIvpBean().getFixRate() == null || pay.getIvpBean().getFixRate().compareTo(new BigDecimal(0.0)) == 0) { //计算费用  得到其比例值 乘以 该组合的待摊费用

                            invMgrFee = rs.getDouble("FPortNetValue") /
                                sumPortNetValue *
                                sumFee;
                        }

                        sumInvMgrFee = sumInvMgrFee + invMgrFee;
                        obj.setInvMgrCode(rs.getString("FInvMgrCode"));

                        obj.setInvestFee(invMgrFee);
                        obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(
                            invMgrFee, pay.getBaseRate()));
                        obj.setPortInvestFee(this.getSettingOper().calPortMoney(
                            invMgrFee, pay.getBaseRate(), pay.getPortRate(),
                            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            pay.getPortCury(), pay.getCurrentDate(),
                            pay.getPortCode()));
                        //obj.setBaseInvestFee(YssD.mul(invMgrFee, pay.getBaseRate()));
                        //obj.setPortInvestFee(YssD.div(YssD.mul(invMgrFee,
                        //    pay.getBaseRate()), pay.getPortRate()));
                        InvestFees.add(obj);
                    }
                    
                    //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
                    dbl.closeResultSetFinal(rs);
                    //---------------------------------------------------------------------------
                    //通过这个SQL语句 获得尾差投资经理
                    strSql = "select * from " +
                        pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                        " where FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                        " and FRelaGrade='1' and FRelaType='InvestManager'";
                    rsMail = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                    if (rsMail.next()) {
                        obj.setInvMgrCode(rs.getString("FInvMgrCode"));
                        //总费用 减去 已经扣去的费用
                        obj.setInvestFee(sumFee - sumInvMgrFee);
                        obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(
                            (sumFee - sumInvMgrFee), pay.getBaseRate()));
                        obj.setPortInvestFee(this.getSettingOper().calPortMoney(
                            (sumFee - sumInvMgrFee), pay.getBaseRate(),
                            pay.getPortRate(),
                            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            pay.getPortCury(), pay.getCurrentDate(),
                            pay.getPortCode()));
                        InvestFees.add(obj); //增加到尾差投资经理
                    }
                }
                //------------------------------------------------------------------------------
                pay.setInvMgrInvestFee(InvestFees);
                } else { //按估值日资产净值计提 MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A panjunfang add 20090715
                    if (pay.getIvpBean().getPayOrigin() == 0) { //收支来源为昨日资产净值
                        strSql = "select * from " + pub.yssGetTableName("Tb_Para_ValuationDay") +
                            " where FPortCode = " + dbl.sqlString(pay.getPortCode()) +
                            " and FDate < " + dbl.sqlDate(pay.getCurrentDate()) +
                            " and FCheckState = 1 order by FDate desc";
                    } else { //收支来源为当日资产净值
                        strSql = "select * from " + pub.yssGetTableName("Tb_Para_ValuationDay") +
                            " where FPortCode = " + dbl.sqlString(pay.getPortCode()) +
                            " and FDate <= " + dbl.sqlDate(pay.getCurrentDate()) +
                            " and FCheckState = 1 order by FDate desc";
                    }
                    rsValDay = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                    if (rsValDay.next()) {
                        valDay = rsValDay.getDate("FDate"); //取得计提日对应的估值日
                    } else {
                        throw new YssException("请先设定估值日再进行两费计提！");
                    }
                    dbl.closeResultSetFinal(rsValDay);
                    
                    //Bug #2653
                    //在选择按估值日计提的时候，dNavDate不会在此段代码前进行赋值，在后面直接调用会报NullPointerException
                    //==============================
                    
                    if (dNavDate == null)
                    {
                    	dNavDate = valDay;
                    }
                    
                    //============end==================
                    
                    //add baopingping 2011.07.04 #Story 1138 添加对指数费的处理           
                    EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
                    rateOper.setYssPub(pub);
                    double M_FMarketValue=0;
                    double FMarketValue=0;
                    double PortCuryRate=0;
                    double BaseCuryRate=0;
                    if(pay.getIvpBean().getIvPayCatName().equalsIgnoreCase(FivName) && FNet!=null)//根据选择的费用和通用参数设置的费用对比
                	{
    	    			if(FivType.equalsIgnoreCase("0"))
    	    			{
    	    				String sql ="select * from "+pub.yssGetTableName("Tb_Rep_GuessValue")+" where Fdate="+dbl.sqlDate(GussData)+
    		    			" and FacctCode='8800'" +
    		    			" and FcurCode=' '";
    		    			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
    		    			while(rs.next())
    		    			{
    		    			    FMarketValue=rs.getDouble("FStandardMoneyMarketValue");

    		    				//20111202 modified by liubo.Bug #3193
    		    				//============================
	                            dRateNav = oper.getCuryRate(GussData,pay.getPortCury(),pay.getPortCode(),YssOperCons.YSS_RATE_BASE);
    		    			    if(pay.getPortCury()==FPort1Code){
    		    			    	sumPortNetValue=FMarketValue;
    		    			    }
//    		    			    else if(FPort1Code.equalsIgnoreCase("USD")){
//    		    			    	sumPortNetValue=FMarketValue*pay.getBaseRate();
//    		    			    }
    		    			    else{
    		    			    	sumPortNetValue=FMarketValue*dRateNav;
    		    			    }
    		    			    //=============end==============
    		    			    //sumPortNetValue=bOper.converMoney(pay.getPortCury(),FPort1Code, FMarketValue, pay.getBaseRate(), pay.getPortRate(),8);//将查询到的币种金额转换成通用参数设置的币种金额
    		    			}
    	    				if(sumPortNetValue>Money_1)
    	    				{
    	    					if(pay.getIvpBean().getFixRate() == null || pay.getIvpBean().getFixRate().compareTo(new BigDecimal(0.0)) == 0){//如果没有设定固定比率，则按比率公式计算出计提费用,panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                                    sumFee = this.getSettingOper().calMoneyByPerExp(pay.getIvpBean().
                                    getPerExpCode(),
                                    pay.getIvpBean().getRoundCode(), FMarketValue,
                                    pay.getCurrentDate());
                                }else{
                                    sumFee = new BigDecimal(Double.toString(FMarketValue)).multiply(pay.getIvpBean().getFixRate()).doubleValue();//panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                                    sumFee = this.getSettingOper().reckonRoundMoney(pay.getIvpBean().getRoundCode(), sumFee);//按舍入条件进行舍入处理
                                }
    	    				}else
    	    				{
    	    					sumFee=0;
    	    				}
    	    			}else
    	    			{
    	    				strSql = "select sum(FBaseNetValue) as FTotalBaseNetValue,sum(FPortNetValue) as FTotalPortNetValue from " +
                            pub.yssGetTableName("Tb_Data_NetValue") +
                            " where FType = '01'" + //取类型为资产净值的 胡昆  20070920
                            " and FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                            " and FNavDate=" + dbl.sqlDate(dNavDate) +
                            //============ 这里根据估值净值表来判断 属于中保版本还是国内版本。MS00064 by leeyu 2008-12-4
                            (sNavType.equalsIgnoreCase("new") ? (" AND FInvMgrCode = ' '") :
                             ""); //2008.07.16 蒋锦 添加投资经理作为查询条件
                            //===========2008-12-4

                              rsSum = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
    	                          while (rsSum.next()) {
    	                            double  sumPortNetValue1 = rsSum.getDouble("FTotalPortNetValue");
    			    				//20111202 modified by liubo.Bug #3193
    			    				//============================
    	                            dRateNav = oper.getCuryRate(GussData,pay.getPortCury(),pay.getPortCode(),YssOperCons.YSS_RATE_BASE);
    	                            if(pay.getPortCury()==FPort1Code){
    			    			    	sumPortNetValue=sumPortNetValue1;
    			    			    }
//    	                            else if(FPort1Code.equalsIgnoreCase("USD")){
//    			    			    	sumPortNetValue=sumPortNetValue1*pay.getBaseRate();
//    			    			    }
    	                            else{
    			    			    	sumPortNetValue=sumPortNetValue1*dRateNav;
    			    			    }
    	                            //=============end==============
    	                        if(sumPortNetValue>Money_1)
    	                        {
                                  if(pay.getIvpBean().getFixRate() == null || pay.getIvpBean().getFixRate().compareTo(new BigDecimal(0.0)) == 0){//如果没有设定固定比率，则按比率公式计算出计提费用,panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                                      sumFee = this.getSettingOper().calMoneyByPerExp(pay.getIvpBean().
                                      getPerExpCode(),
                                      pay.getIvpBean().getRoundCode(), sumPortNetValue1,
                                      pay.getCurrentDate());
                                  }else{
                                      sumFee = new BigDecimal(Double.toString(sumPortNetValue1)).multiply(pay.getIvpBean().getFixRate()).doubleValue();//panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                                      sumFee = this.getSettingOper().reckonRoundMoney(pay.getIvpBean().getRoundCode(), sumFee);//按舍入条件进行舍入处理
                                  }
    	                         }else{
    	                            	  sumFee=0;
    	                              }
                              }
                              dbl.closeResultSetFinal(rsSum);
    	                         
    	    			}
    	    			//----end--------------
                	}else{
                		strSql = "select sum(FBaseNetValue) as FTotalBaseNetValue,sum(FPortNetValue) as FTotalPortNetValue from " +
                        pub.yssGetTableName("Tb_Data_NetValue") +
                        " where FType = '01'" + //取类型为资产净值的 胡昆  20070920
                        " and FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                        " and FNavDate=" + dbl.sqlDate(dNavDate) +
                        //============ 这里根据估值净值表来判断 属于中保版本还是国内版本。MS00064 by leeyu 2008-12-4
                        (sNavType.equalsIgnoreCase("new") ? (" AND FInvMgrCode = ' '") :
                         ""); //2008.07.16 蒋锦 添加投资经理作为查询条件
                        //===========2008-12-4

                          rsSum = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                          while (rsSum.next()) {
                              sumPortNetValue = rsSum.getDouble("FTotalPortNetValue");
                              if(pay.getIvpBean().getFixRate() == null || pay.getIvpBean().getFixRate().compareTo(new BigDecimal(0.0)) == 0){//如果没有设定固定比率，则按比率公式计算出计提费用,panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                                  sumFee = this.getSettingOper().calMoneyByPerExp(pay.getIvpBean().
                                  getPerExpCode(),
                                  pay.getIvpBean().getRoundCode(), sumPortNetValue,
                                  pay.getCurrentDate());
                              }else{
                                  sumFee = new BigDecimal(Double.toString(sumPortNetValue)).multiply(pay.getIvpBean().getFixRate()).doubleValue();//panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
                                  sumFee = this.getSettingOper().reckonRoundMoney(pay.getIvpBean().getRoundCode(), sumFee);//按舍入条件进行舍入处理
                              }
                          }
                    
                          dbl.closeResultSetFinal(rsSum);
                	}
                    if (valSumPortMarketValue == 0) { //分母不能为零 为零代表没有此费用
                        return pay;
                    }
                    PeriodBean Period = new PeriodBean();
                    Period.setYssPub(pub);
                    Period.setPeriodCode(pay.getIvpBean().getPeriodCode());

                    if (Period.getPeriodCode() == null || Period.getPeriodCode().trim().equals("null") ||
                        Period.getPeriodCode().trim().equalsIgnoreCase("")) {
                        throw new YssException("请先维护" + pay.getIvpBean().getIvPayCatName() +
                                               "的期间设置");
                    } // 检查费用期间设置是否正确

                    Period.getSetting();

                    sumFee = YssD.div(valSumFee, Period.getDayOfYear()); //费用除以期间设置中每年天数即每天计提的费用
                    
                    //20120514 added by liubo.Story #2217
                    //当计提日期符合补差日条件时，需要判断昨日费用已计提总金额加上当日按系统当前算法计算出的金额，是否小于“支付下限”金额。
                    //若小于，则当日计提的费用金额为“支付下限”金额减去昨日已计提总金额。若大于，则当日计提的费用金额同当前系统计提金额。
                    //==================================
                    //start modify huangqirong 2013-01-22 story #3488
                    if("2".equalsIgnoreCase(pay.getIvpBean().getPeriodOfBC()) && bIfTheDateOfBC){
                    	String currency = pay.getIvpBean().getLowerCurrencyCode(); //下限币种代码
                    	if(currency != null && currency.trim().length() > 0) //设置了下限币种
                    	{                		
                    		if(!pay.getPortCury().equalsIgnoreCase(currency)){ //下限币种和计提币种不一致                		
    	                		double lowerMoney = pay.getIvpBean().getLimitedAmount(); //下限金额
    	                		BaseOperDeal operDeal = new BaseOperDeal();
    	                		operDeal.setYssPub(pub);	
    	                		double lowerBaseRate = operDeal.getCuryRate(pay.getCurrentDate(), currency, pay.getPortCode(), "base");
    	                		double payBaseRate = operDeal.getCuryRate(pay.getCurrentDate(), pay.getPortCury(), pay.getPortCode(), "base");
    	                		
    	                		double payLowerMoney = YssD.div(YssD.mul(lowerMoney, lowerBaseRate) , payBaseRate); //下限币种金额 转成计提币种金额
    	                		
    	                		double totalToDay = YssD.add(dTotal, sumFee);
    	                		supplementMoney =  totalToDay < payLowerMoney ? YssD.sub(payLowerMoney, totalToDay) : 0;
                    		}else if(pay.getPortCury().equalsIgnoreCase(currency)){	//下限币种和计提币种不一致
                    			double lowerMoney = pay.getIvpBean().getLimitedAmount(); //下限金额
    	                		double totalToDay = YssD.add(dTotal, sumFee);
    	                		supplementMoney =  totalToDay < lowerMoney ? YssD.sub(lowerMoney, totalToDay) : 0;
                    		}
                    	}else{	//未设置下限币种
                    		double totalToDay = YssD.add(dTotal, sumFee);                    		
                    		supplementMoney =  totalToDay < pay.getIvpBean().getLimitedAmount() ? YssD.sub(pay.getIvpBean().getLimitedAmount(), totalToDay) : 0;
                    	}
                    }else if (bIfTheDateOfBC){
                    	String currency = pay.getIvpBean().getLowerCurrencyCode(); //下限币种代码
                    	if(currency != null && currency.trim().length() > 0) //设置了下限币种
                    	{                		
                    		if(!pay.getPortCury().equalsIgnoreCase(currency)){ //下限币种和计提币种不一致                		
    	                		double lowerMoney = pay.getIvpBean().getLimitedAmount(); //下限金额
    	                		BaseOperDeal operDeal = new BaseOperDeal();
    	                		operDeal.setYssPub(pub);	
    	                		double lowerBaseRate = operDeal.getCuryRate(pay.getCurrentDate(), currency, pay.getPortCode(), "base");
    	                		double payBaseRate = operDeal.getCuryRate(pay.getCurrentDate(), pay.getPortCury(), pay.getPortCode(), "base");
    	                		
    	                		double payLowerMoney = YssD.div(YssD.mul(lowerMoney, lowerBaseRate) , payBaseRate); //下限币种金额 转成计提币种金额
    	                		
    	                		double totalToDay = YssD.add(dTotal, sumFee);
    	                		supplementMoney =  totalToDay < payLowerMoney ? YssD.sub(payLowerMoney, totalToDay) : 0;
                    		}else if(pay.getPortCury().equalsIgnoreCase(currency)){	//下限币种和计提币种不一致
                    			double lowerMoney = pay.getIvpBean().getLimitedAmount(); //下限金额
    	                		double totalToDay = YssD.add(dTotal, sumFee);
    	                		supplementMoney =  totalToDay < lowerMoney ? YssD.sub(lowerMoney, totalToDay) : 0;
                    		}
                    	}else{
                    		if (pay.getIvpBean().getLimitedAmount() > YssD.add(dTotal, sumFee))
                    		{
                    			//sumFee = YssD.sub(pay.getIvpBean().getLimitedAmount(),dTotal); //之前的计提方式
                    			double totalToDay = YssD.add(dTotal, sumFee);
                    			supplementMoney = YssD.sub(pay.getIvpBean().getLimitedAmount(),totalToDay);
                    		}
                    	}
                    }
                    //======end====================

                    obj = new InvestFeeBean();
                    obj.setInvMgrCode(" "); //暂不区分投资经理
                    obj.setInvestFee(sumFee); //原币计提费用
                    obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(sumFee,
                        pay.getBaseRate())); //根据原币计提费用和基础汇率算出基础货币计提费用
                    obj.setPortInvestFee(this.getSettingOper().calPortMoney(sumFee,
                        pay.getBaseRate(), pay.getPortRate(),
                        pay.getPortCury(), pay.getCurrentDate(), pay.getPortCode())); //计算组合货币计提费用        
                    InvestFees.add(obj);
                  //start add by huangqirong 2013-02-01 story #3488
                    if(supplementMoney > 0){
                    	InvestPayBean investPay = (InvestPayBean)pay.getIvpBean().clone();                    	
                    	investPay.setTsfTypeCode(YssOperCons.Yss_ZJDBLX_SUPPLEMENT);//计提补差 调拨类型
                    	investPay.setSubTsfTypeCode(YssOperCons.Yss_ZJDBLX_SUPPLEMENT_IV);//计提补差 调拨子类型         
                    	investPay.setFIVType(pay.getIvpBean().getFIVType());
                    	investPay.setIvPayCatCode(pay.getIvpBean().getIvPayCatCode());
                    	investPay.setFAnalysisCode3(pay.getIvpBean().getFAnalysisCode3());
                    	investPay.setRoundCode(pay.getIvpBean().getRoundCode());
                    	investPay.setAcroundCode(pay.getIvpBean().getAcroundCode());
                    	investPay.setAttrClsCode(pay.getIvpBean().getAttrClsCode());
                    	obj = (InvestFeeBean)pay.clone();
                        obj.setInvMgrCode(" "); //暂不区分投资经理
                        obj.setInvestFee(supplementMoney); //原币计提费用
                        obj.setIvpBean(investPay);
                        obj.setHaveIvpBean(true);
                        obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(supplementMoney, pay.getBaseRate())); //根据原币计提费用和基础汇率算出基础货币计提费用
                        obj.setPortInvestFee(this.getSettingOper().calPortMoney(supplementMoney, pay.getBaseRate(), pay.getPortRate(),
                        		pay.getPortCury(), pay.getCurrentDate(), pay.getPortCode())); //计算组合货币计提费用        
                        InvestFees.add(obj);
                    }
                    //end add by huangqirong 2013-02-01 story #3488
                    pay.setInvMgrInvestFee(InvestFees);
                }
            } else { //预提待摊日平均费用
            	int iAveDaysCount = 1;		//20110811 added by liubo.Story #1227.该月实际均摊的天数
            	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            	
                obj = new InvestFeeBean();
                InvestPayBean investPay = pay.getIvpBean();
                java.util.Date tempDate; //比较运营收支品种的结束日期和终止日期，取两者中靠前的日期。panjunfang add 20090629
                //MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                if (YssFun.dateDiff(investPay.getfACEndDate(), investPay.getExpirDate()) >= 0) {
                    tempDate = investPay.getfACEndDate();
                } else {
                    tempDate = investPay.getExpirDate();
                }
                if (YssFun.dateDiff(pay.getCurrentDate(), tempDate) >= 0) { //当前日期同时满足小于等于结束日期和终止日期时才计提预提待摊 panjunfang modify 20090629
                    //20110809 added by liubo.Story #1227
                	//当运营品种类型为待摊，且均摊方式为“按月再按日均摊”时，需要将总额除以期间总月份，然后再分别除以每个月的实际计提的天数，得到每天计提的金额。
                	//******************************************
                	if ("deferredFee".equals(pay.getIvpBean().getFIVType()) && "1".equals(pay.getIvpBean().getApportionType()))
                    {
                		//首先将待摊总金额除以期间月份总数
                    	aveFee = YssD.div(investPay.getfACTotalMoney(),YssFun.monthDiff(investPay.getfACBeginDate(), tempDate) + 1);
                    	
                    	//当计提日期属于结束日期的月份，需要在当月进行轧差。
                    	if (YssFun.getMonth(pay.getCurrentDate()) == YssFun.getMonth(tempDate) && YssFun.getYear(pay.getCurrentDate()) == YssFun.getYear(tempDate))
                    	{
                    		aveFee = investPay.getfACTotalMoney() - getTotalExceptLastMonth(YssFun.monthDiff(investPay.getfACBeginDate(),tempDate), investPay.getfACTotalMoney());
                    	}
                    	
                    	aveFee = (double)((int)(aveFee * 100)) / 100.0;
                    	
                    	//此段代码为具体计算每个月的实际的计提天数
                    	//=============================================
                    	
                    	//当计提日期属于计提开始的第一个月份时，实际计提天数为起始日期到该月最后一天
                    	if (YssFun.getMonth(pay.getCurrentDate()) == YssFun.getMonth(investPay.getfACBeginDate()) && YssFun.getYear(pay.getCurrentDate()) == YssFun.getYear(investPay.getfACBeginDate()))
                    	{
                    		iAveDaysCount = YssFun.dateDiff(investPay.getfACBeginDate(),dateFormat.parse(String.valueOf(YssFun.getYear(pay.getCurrentDate())) + "-" + String.valueOf(YssFun.getMonth(pay.getCurrentDate())) + "-" + String.valueOf(YssFun.endOfMonth(pay.getCurrentDate())))) + 1;
                    	}
                    	//当计提日期属于计提结束的最后一个月份时，实际计提天数为该月的1号到结束日期当天。
                    	else if (YssFun.getMonth(pay.getCurrentDate()) == YssFun.getMonth(tempDate) && YssFun.getYear(pay.getCurrentDate()) == YssFun.getYear(tempDate))
                    	{
                    		iAveDaysCount = YssFun.dateDiff(dateFormat.parse(String.valueOf(YssFun.getYear(pay.getCurrentDate())) + "-" + String.valueOf(YssFun.getMonth(pay.getCurrentDate())) + "-" + "1"),tempDate) + 1;
                    	}
                    	//其他的情况下，实际计提天数按整月的天数来计算
                    	else
                    	{
                    		iAveDaysCount = YssFun.getMonthLastDay(YssFun.getYear(pay.getCurrentDate()), YssFun.getMonth(pay.getCurrentDate()));
                    	}
                    	//======================end=====================
                    	
                    	//当计提日期为每个月最后一天或计提结束一天时，需要对每个月的计提金额进行轧差，得到当日计提金额
                    	if (YssFun.getDay(pay.getCurrentDate()) == YssFun.endOfMonth(pay.getCurrentDate()) || (YssFun.getYear(pay.getCurrentDate()) + YssFun.getMonth(pay.getCurrentDate()) + YssFun.getDay(pay.getCurrentDate())) == (YssFun.getYear(tempDate) + YssFun.getMonth(tempDate) + YssFun.getDay(tempDate)))
                    	{
                    		aveFee = YssD.round(YssD.sub(aveFee,getTotalExceptLastMonth(iAveDaysCount - 1, aveFee)),2);
                    	}
                    	//其他情况下,直接用月均摊费用除以计提天数，得到当日计提金额
                    	else
                    	{
                    		aveFee = YssD.round(YssD.div(aveFee,iAveDaysCount), 2);
                    	}


                    	obj.setInvestFee(aveFee);
                    	                    	
                    }
                	
                	//*******************end***********************
                    else
                    {
                    	//added by liubo.Story #2139
                    	//当运营品种类型为预提，启用预提转待摊，切计提日期大于等于转换日期时，进行特殊处理
                    	//====================================
                    	double dbCurPaidIn = 0;		//此变量用于存储转换日期前一日的运营库存月余额
                    	
                    	if(investPay.getFIVType().equalsIgnoreCase("accruedFee") && investPay.getTransition().equalsIgnoreCase("1") && YssFun.dateDiff(investPay.getTransitionDate(),pay.getCurrentDate()) >= 0)
                    	{
                    		ResultSet rsTransition = null;
                    		
                        	strSql = "select FBal as Total from " + pub.yssGetTableName("Tb_Stock_Invest") +
                			" where FIVPayCatCode = " + dbl.sqlString(investPay.getIvPayCatCode()) +" and FPortCode = " + dbl.sqlString(pay.getPortCode()) + 
                			" and FStorageDate = " + dbl.sqlDate(YssFun.addDate(investPay.getTransitionDate(), -1, Calendar.DAY_OF_MONTH));
                	
                    		rsTransition = dbl.queryByPreparedStatement(strSql);
                    		
                    		while(rsTransition.next())
                    		{
                    			dbCurPaidIn = rsTransition.getDouble("Total");	//获得转换日期前一日的运营库存月余额
                    		}
		                    dbl.closeResultSetFinal(rsTransition);
		                    //+++++++++++++++++++++++++++
		                    //每日计提金额等于（实收金额 - 转换日期前一日运营库存余额）/ 转换日期至结束日期之间的天数
		                	if (investPay.getPaidIn() != 0) {
		                        aveFee = YssD.div(investPay.getPaidIn() - dbCurPaidIn,
		                                          YssFun.dateDiff(investPay.getTransitionDate(),
		                            investPay.getfACEndDate()) + 1);
		                        aveFee = this.getSettingOper().reckonRoundMoney(investPay.
		                            getAcroundCode(), aveFee);
		                    }
		                	//++++++++++++end+++++++++++
		                    curFee = aveFee *
		                        (YssFun.dateDiff(investPay.getTransitionDate(),
		                                         pay.getCurrentDate()) + 1);
		                    yesterFee = aveFee *
		                        (YssFun.dateDiff(investPay.getTransitionDate(),
		                                         YssFun.addDay(pay.getCurrentDate(), -1)) +
		                         1);
                    		
                    	}

                    	//================end====================
                    	else
                    	{
                    		if (investPay.getfACTotalMoney() != 0) {
		                        aveFee = YssD.div(investPay.getfACTotalMoney(),
		                                          YssFun.dateDiff(investPay.getfACBeginDate(),
		                            investPay.getfACEndDate()) + 1);
		                        aveFee = this.getSettingOper().reckonRoundMoney(investPay.
		                            getAcroundCode(), aveFee);
		                    }
		                    curFee = aveFee *
		                        (YssFun.dateDiff(investPay.getfACBeginDate(),
		                                         pay.getCurrentDate()) + 1);
		                    yesterFee = aveFee *
		                        (YssFun.dateDiff(investPay.getfACBeginDate(),
		                                         YssFun.addDay(pay.getCurrentDate(), -1)) +
		                         1);
                    	}
	                    // 20090629 panjunfang modify, MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
	                    
	                    //modified by liubo.Story #2139
                    	//未启用预提转待摊的预提项，或者计提日小于转换日期的启用了预提转待摊的预提项，采用正常规则计提
	                    //================================
	                    if((pay.getIvpBean().getFIVType().equalsIgnoreCase("accruedFee") && pay.getIvpBean().getTransition().equalsIgnoreCase("0")) || (investPay.getFIVType().equalsIgnoreCase("accruedFee") && investPay.getTransition().equalsIgnoreCase("1") && YssFun.dateDiff(investPay.getTransitionDate(),pay.getCurrentDate()) < 0)){//预提的计提费用，结束日期当天的费用采用倒轧计算
	                    //==================end==============   
	                    	if (YssFun.dateDiff(pay.getCurrentDate(), investPay.getfACEndDate()) > 0) {
	                            obj.setInvestFee(curFee - yesterFee);
	                        } else if (YssFun.dateDiff(pay.getCurrentDate(), investPay.getfACEndDate()) == 0) {
	                            obj.setInvestFee(investPay.getfACTotalMoney() - yesterFee);
	                        }
	                    }else{
	                    	if (pay.getIvpBean().getFIVType().equalsIgnoreCase("accruedFee") && pay.getIvpBean().getTransition().equalsIgnoreCase("1") && YssFun.dateDiff(investPay.getTransitionDate(),pay.getCurrentDate()) >= 0)
	                    	{
	                    		obj.getIvpBean().setSubTsfTypeCode("16IV");
	                    		obj.getIvpBean().setTsfTypeCode("16");
	                    		pay.getIvpBean().setSubTsfTypeCode("16IV");
	                    		pay.getIvpBean().setTsfTypeCode("16");
		                        if (YssFun.dateDiff(pay.getCurrentDate(), tempDate) > 0) {
		                            obj.setInvestFee(curFee - yesterFee);
		                        } else if (YssFun.dateDiff(pay.getCurrentDate(), tempDate) == 0) {
		                            obj.setInvestFee(investPay.getPaidIn() - dbCurPaidIn - yesterFee);
		                        }
	                    	}
	                    	else
	                    	{
	                    		if (YssFun.dateDiff(pay.getCurrentDate(), tempDate) > 0) {
		                            obj.setInvestFee(curFee - yesterFee);
		                        } else if (YssFun.dateDiff(pay.getCurrentDate(), tempDate) == 0) {
		                            obj.setInvestFee(investPay.getfACTotalMoney() - yesterFee);
		                        }
	                    	}
	                    }
                    }
                    obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(obj.
                        getInvestFee()
                        , pay.getBaseRate()));
                    obj.setPortInvestFee(this.getSettingOper().calPortMoney(
                        obj.getInvestFee(), pay.getBaseRate(), pay.getPortRate(),
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        pay.getPortCury(), pay.getCurrentDate(), pay.getPortCode()));
                    InvestFees.add(obj);
                }
                //--------------------------------------------------------------------------------
                pay.setInvMgrInvestFee(InvestFees);
            }
            return pay;
        } catch (Exception e) {
            throw new YssException("系统按组合计算应计提费用时出现异常!" + "\n", e); //by caocheng 09.02.04 MS00004 QDV4.1-2009.2.1_09A
        } finally {
        	//edit by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs,rsSum,rsValDay,rsValSum);
            dbl.closeResultSetFinal(rsMail);
        }

    }

    /***
     * #2279  add  by wuweiqi 20110125 QDV4农行2010年11月30日01_A
     * 处理现金账户中多账户的费用计提
     * @param bean
     * @param CuryCode
     * @return
     * @throws YssException
     */
    public InvestFeeBean calculateIncome(InvestFeeBean bean, String curyCode, String ivPayCatCode, String cashAccCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ResultSet rsSum = null;
        ResultSet rsMail = null; //得到尾差投资经理
        ArrayList InvestFees = new ArrayList(); //这个ArrayList 保存的是费用那个BEAN
        InvestFeeBean obj = null;
        double sumPortNetValue = 0; //这个组合的组合货币净值总额
        double sumFee = 0; //得到的总费用
        double invMgrFee = 0; //按照比例计算出的组合货币的费用值
        double sumInvMgrFee = 0; //储存该组合的所有投资经理组合货币所占的费用值

        double aveFee = 0; 		//预提待摊日平均费用
        double curFee = 0; 		//今日的费用
        double yesterFee = 0; 	//昨日的费用
        boolean sign = false; 	//true净值区分投资经理 false净值不区分投资经理，20070806，杨
        boolean yes = false; 	//yes为true，则按昨目净值计提，yes为false，则按今日净值计提，20070810，杨
        
        Date dNavDate = null;	//净值表日期 sunkey@Modify 20091125
        String sHDayCode = "";	//节假日代码 
        
        //Start MS00018 国内计提两费 QDV4.1赢时胜（上海）2009年4月20日18_A
        ResultSet rsValDay = null;          //获取计提日对应的估值日
        java.util.Date valDay = null;       //计提日对应的估值日
        double valSumPortMarketValue = 0;   //估值日资产净值总额
        double valSumFee = 0;               //按估值日计提到的总费用
        
        double baseCuryRate = 0;
        double portCuryRate = 0;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        
        try {      	      	
            InvestFeeBean pay = bean;   
            
            baseCuryRate = this.getSettingOper().getCuryRate(pay.getCurrentDate(), curyCode,
                    pay.getPortCode(), YssOperCons.YSS_RATE_BASE);
		    rateOper.getInnerPortRate(pay.getCurrentDate(), curyCode, pay.getPortCode());
		    portCuryRate = rateOper.getDPortRate();
		    pay.setPortCury(curyCode);
		    pay.setBaseRate(baseCuryRate);
		    pay.setPortRate(portCuryRate); 
            
            if (pay.getIvpBean().getFIVType().equalsIgnoreCase("managetrusteeFee")) { 
                if (pay.getIvpBean().getAccrueTypeCode().equalsIgnoreCase("evedaynav")//按工作日资产净值计提
                	//若按自然日资产净值计提
                    || pay.getIvpBean().getAccrueTypeCode().equalsIgnoreCase("evendaynav")) {           	
	            	yes = pay.getIvpBean().getPayOrigin() == 0 ? true : false;
	            	sHDayCode = pay.getIvpBean().getHolidaysCode();
	            	//---edit by songjie 2011.04.28 BUG 1676 QDV4中银基金2011年04月11日01_B---//
	            	//若按工作日资产净值计提
	            	if(pay.getIvpBean().getAccrueTypeCode().equalsIgnoreCase("evedaynav")){
						if (yes) {//若选择昨日净值
							if (sHDayCode == null || sHDayCode.equals("")) {
								dNavDate = YssFun.addDay(pay.getCurrentDate(),-1);
							} else {
								dNavDate = super.getSettingOper().getWorkDay(sHDayCode, pay.getCurrentDate(), -1);
							}
						} else {
							dNavDate = pay.getCurrentDate();
						}
	            	}else{//若按自然日资产净值计提
						if (yes) {//若选择昨日净值
							dNavDate = YssFun.addDay(pay.getCurrentDate(),-1);
						} else {
							dNavDate = pay.getCurrentDate();
						}
	            	}
	            	//---edit by songjie 2011.04.28 BUG 1676 QDV4中银基金2011年04月11日01_B---//
	            	sumPortNetValue = getNetValueByCury(pay, dNavDate, ivPayCatCode, curyCode, cashAccCode);
	                if(pay.getIvpBean().getFixRate() == null || pay.getIvpBean().getFixRate().compareTo(new BigDecimal(0.0)) == 0){//如果没有设定固定比率，则按比率公式计算出计提费用,panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
	                    sumFee = this.getSettingOper().calMoneyByPerExp(pay.getIvpBean().
	                            getPerExpCode(),
	                            pay.getIvpBean().getRoundCode(), sumPortNetValue,
	                            pay.getCurrentDate());
	                }else{
	                    sumFee = new BigDecimal(Double.toString(sumPortNetValue)).multiply(pay.getIvpBean().getFixRate()).doubleValue();//panjunfang modify 20090815 修改投资运营收支设置中固定比率的精度
	                    sumFee = this.getSettingOper().reckonRoundMoney(pay.getIvpBean().getRoundCode(), sumFee);//按舍入条件进行舍入处理
	                }
	                if (sumPortNetValue == 0) { //分母不能为零 为零代表没有此费用
	                	return pay;
	                }
		            PeriodBean Period = new PeriodBean();
	                Period.setYssPub(pub);
	                Period.setPeriodCode(pay.getIvpBean().getPeriodCode());
	                if (Period.getPeriodCode() == null || Period.getPeriodCode().trim().equals("null") ||
	                    Period.getPeriodCode().trim().equalsIgnoreCase("")) {
	                    throw new YssException("请先维护" + pay.getIvpBean().getIvPayCatName() + "的期间设置");
	                } 
	                Period.getSetting();
	
	                sumFee = YssD.div(sumFee, Period.getDayOfYear()); //费用除以期间设置中每年天数，20070810，杨
	                sumFee = this.getSettingOper().reckonRoundMoney(pay.getIvpBean().getRoundCode(), sumFee); //add by fangjiang 2011.04.06 STORY #616 运营应收应付原币金额和汇率计算需要修改 
	
	                strSql = "select FInvMgrCode from " +
	                           pub.yssGetTableName("Tb_Data_NetValue") +
		                       " where FType = '01'" + //取类型为资产净值的  胡昆  20070920
		                       " and FPortCode=" + dbl.sqlString(pay.getPortCode()) +
		                       " and FNavDate=" + dbl.sqlDate(dNavDate);
	                rs = dbl.openResultSet(strSql);
	                while (rs.next()) {
	                    if (rs.getString("FInvMgrCode") != null &&
	                        rs.getString("FInvMgrCode").trim().length() != 0) {
	                        sign = true;
	                    }
	                }
	                dbl.closeResultSetFinal(rs);	
	                if (!sign) {
	                    //净值不按投资经理来分
	                    obj = new InvestFeeBean();
	                    obj.setInvMgrCode(" ");
	                    obj.setInvestFee(sumFee);
	                    obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(sumFee,
	                        pay.getBaseRate()));
	                    obj.setPortInvestFee(this.getSettingOper().calPortMoney(sumFee,
	                        pay.getBaseRate(), pay.getPortRate(),
	 
	                        pay.getPortCury(), pay.getCurrentDate(), pay.getPortCode()));
	                    obj.setPortCury(curyCode);//对应币种
	                    InvestFees.add(obj);
	                } else {
	                    //查询所有投资组合下投资经理不为空的
	                    strSql = "select * from " +
	                              pub.yssGetTableName("Tb_Data_NetValue") +
	                              " where FType = '01'" + //取类型为资产净值的  胡昆  20070920
	                              " and FPortCode=" + dbl.sqlString(pay.getPortCode()) +
	                              " and FInvMgrCode != ' '  and FNavDate=" + dbl.sqlDate(dNavDate);
	                    rs = dbl.openResultSet(strSql);
	                    while (rs.next()) {
	                        obj = new InvestFeeBean();
	                        if (pay.getIvpBean().getFixRate() == null || pay.getIvpBean().getFixRate().compareTo(new BigDecimal(0.0)) == 0) { //计算费用  得到其比例值 乘以 该组合的待摊费用
	                           invMgrFee = rs.getDouble("FPortNetValue") / sumPortNetValue * sumFee;
	                        }
	                        sumInvMgrFee = sumInvMgrFee + invMgrFee;
	                        obj.setInvMgrCode(rs.getString("FInvMgrCode"));
	                        obj.setInvestFee(invMgrFee);
	                        obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(
	                             invMgrFee, pay.getBaseRate()));
	                        obj.setPortInvestFee(this.getSettingOper().calPortMoney(
	                            invMgrFee, pay.getBaseRate(), pay.getPortRate(),
	                            pay.getPortCury(), pay.getCurrentDate(),
	                            pay.getPortCode()));
	                        obj.setPortCury(curyCode);//对应币种
	                        InvestFees.add(obj);
	                    }
	                    //---------------------------------------------------------------------------
	                    //通过这个SQL语句 获得尾差投资经理
	                    strSql = "select * from " +
	                        pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
	                        " where FPortCode=" + dbl.sqlString(pay.getPortCode()) +
	                        " and FRelaGrade='1' and FRelaType='InvestManager'";
	                    rsMail = dbl.openResultSet(strSql);
	                    if (rsMail.next()) {
	                        obj.setInvMgrCode(rs.getString("FInvMgrCode"));
	                        //总费用 减去 已经扣去的费用
	                        obj.setInvestFee(sumFee - sumInvMgrFee);
	                        obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(
	                            (sumFee - sumInvMgrFee), pay.getBaseRate()));
	                        obj.setPortInvestFee(this.getSettingOper().calPortMoney(
	                            (sumFee - sumInvMgrFee), pay.getBaseRate(),
	                            pay.getPortRate(),
	                            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
	                            pay.getPortCury(), pay.getCurrentDate(),
	                            pay.getPortCode()));
	                        obj.setPortCury(curyCode);//对应币种
	                        InvestFees.add(obj); //增加到尾差投资经理
	                    }
	                }
                }
            }
	        pay.setInvMgrInvestFee(InvestFees);	   
	        return pay;       
        } catch (Exception e) {
            throw new YssException("系统按组合计算应计提费用时出现异常!" + "\n", e); //by caocheng 09.02.04 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsMail);
        }
    }
    
    /***
     * #2279 add  by wuweiqi 20110125 QDV4农行2010年11月30日01_A
     * 获取对应现金账户币种的资产净值
     * @param pay
     * @param dNavDate
     * @param curyCode
     * @return
     * @throws YssException
     */
    private double getNetValueByCury(InvestFeeBean pay, java.util.Date dNavDate, String ivPayCatCode, String curyCode, String cashAccCode) throws YssException{
        String strSql = null;//用于储存sql语句
        ResultSet rs = null;//声明结果集
        double sumValue=0;//对应某个币种的资产净值
        double totalSecurity=0;
        double totalCash=0;
        double totalInvest=0;
        String strCashAccCode = "";
        try{
        	strSql = " select FCuryCode, FCashAccCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+
        	         " where FCheckState =1 and FCashAccCode in ("+operSql.sqlCodes(cashAccCode)+")";
        	rs = dbl.openResultSet(strSql);
        	while(rs.next()){
        		if(rs.getString("FCuryCode").equalsIgnoreCase(curyCode)){
        			strCashAccCode += rs.getString("FCashAccCode")+",";
        		}	
        	}
        	if(strCashAccCode.endsWith(",")){
        		strCashAccCode = strCashAccCode.substring(0, strCashAccCode.length()-1);
        	}
        	
        	if(
        		((ivPayCatCode.equalsIgnoreCase("IV00101") || ivPayCatCode.equalsIgnoreCase("IV00201")) && curyCode.equalsIgnoreCase("CNY"))
        		||((ivPayCatCode.equalsIgnoreCase("IV00102") || ivPayCatCode.equalsIgnoreCase("IV00202")) && !curyCode.equalsIgnoreCase("CNY"))
        	){
        		//证劵
            	strSql="select sum(case when FInOut = 1 then FMarketValue else FMarketValue*-1 end) as FTotalMarketValue from " +
            	        pub.yssGetTableName("Tb_Data_NavData") +
            	        " where FNavDate = " + dbl.sqlDate(dNavDate) +
                        " and FPortCode=" + dbl.sqlString(pay.getPortCode()) +
                        " and FCuryCode = " + dbl.sqlString(curyCode) +
                        " and FDetail = 0 and FReTypeCode = 'Security'";
                rs = dbl.openResultSet(strSql);
                if(rs.next()){
                	totalSecurity = rs.getDouble("FTotalMarketValue");
                }
        	}
        	  	
            //现金
            strSql="select sum(case when FInOut = 1 then FMarketValue else FMarketValue*-1 end) as FTotalRepMarketValue from " +
	    	        pub.yssGetTableName("Tb_Data_NavData") + 
	    	        " where FNavDate = " + dbl.sqlDate(dNavDate) +
	                " and FPortCode = " + dbl.sqlString(pay.getPortCode()) +
	                " and FGradetype4 = " + dbl.sqlString(curyCode) +
	                " and FGradetype5 in ( " + operSql.sqlCodes(strCashAccCode) +
	                " ) and FDetail = 0 and FReTypeCode = 'Cash'";
            rs = dbl.openResultSet(strSql);
            if(rs.next()){
            	totalCash = rs.getDouble("FTotalRepMarketValue");
            }
            
            //运营
            strSql="select sum(case when FInOut = 1 then FMarketValue else FMarketValue*-1 end) as FTotalFee from " +
	    	        pub.yssGetTableName("Tb_Data_NavData") +
	    	        " where FNavDate = " + dbl.sqlDate(dNavDate) +
	                " and FPortCode = " + dbl.sqlString(pay.getPortCode()) +
	                " and FGradeType2 = " + dbl.sqlString(ivPayCatCode) +
	                " and FCuryCode = " + dbl.sqlString(curyCode) +
	                " and FDetail = 0 and FReTypeCode = 'Invest'";
	        rs = dbl.openResultSet(strSql);
	        if(rs.next()){
	        	totalInvest = rs.getDouble("FTotalFee");
	        }
	        
	        sumValue = totalSecurity + totalCash + totalInvest;//资产净值
            return sumValue;
        }
        catch(Exception e){
            throw new YssException("获取对应货币资产净值时出现异常！", e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    
    public void saveIncomes(ArrayList alIncome) throws YssException {
        String strSql = "";
        int i = 0;
        String sFNum = "";
        InvestFeeBean investFee = new InvestFeeBean();
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //---add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        InvestPayAdimin investPayAdmin = new InvestPayAdimin();
        investPayAdmin.setYssPub(pub);
        //---add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        //--- add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        double money = 0;//原币金额
        Date logBeginDate = null;
        //--- add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            //2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
            //为表加锁，以免在多用户同时处理时出现交易编号重复
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_InvestPayRec"));

            strSql = "insert into " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                "(FNum,FIVPayCatCode,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3" +
                ",FTsfTypeCode,FSubTsfTypeCode,FCuryCode,FMoney,FBaseCuryRate,FBaseCuryMoney,FPortCuryRate,FPortCuryMoney" +
                ",FDataSource,FStockInd" +
                ",FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,fattrclscode)" +//NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 
            pst = conn.prepareStatement(strSql);

            strSql = "delete from " +
                pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " where FTransDate between " + dbl.sqlDate(beginDate) +
                " and " + dbl.sqlDate(endDate) +
                " and FPortCode in (" +
                operSql.sqlCodes(portCodes) +
                ") and FIvPayCatCode in ( " + operSql.sqlCodes(selCodes) + ")" +
                " and FTsfTypeCode in ('16','07','97') " + //panjunfang modify 20090629 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A //modify huangqirong 2013-02-02 story #3488
                " and FDataSource =0"+
                " and frelatype is null";//modify by zhouwei 20120405 增加关联类型的判断
            dbl.executeSql(strSql);

            for (i = 0; i < alIncome.size(); i++) {
                investFee = (InvestFeeBean) alIncome.get(i);  
                
                //add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                logBeginDate = new Date();
                
                if (investFee.getInvMgrInvestFee() != null) {
                    for (int j = 0; j < investFee.getInvMgrInvestFee().size(); j++) {
                    	//start add by huangqirong 2013-02-02 story #3488
                    	if(((InvestFeeBean)investFee.getInvMgrInvestFee().get(j)).isHaveIvpBean() && ((InvestFeeBean)investFee.getInvMgrInvestFee().get(j)).getIvpBean() != null){                   	
                    		investFee.setIvpBean(((InvestFeeBean)investFee.getInvMgrInvestFee().get(j)).getIvpBean());
                    	}
                    	//---end add by huangqirong 2013-02-02 story #3488
                    	//----------------add by wuweiqi 20110116 费用为0时不进行插入操作-----------------//
	                    if(((InvestFeeBean)investFee.getInvMgrInvestFee().get(j)).getInvestFee()==0){
	                        continue;
	                    }
	                    //----------------end by wuweiqi ----------------------------------------------//
	                    //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//                        sFNum = "IPR" +
//                            YssFun.formatDatetime(investFee.getCurrentDate()).
//                            substring(0, 8) +
//                            dbFun.getNextInnerCode(pub.yssGetTableName(
//                                "Tb_Data_InvestPayRec"),
//                            dbl.sqlRight("FNUM", 9),
//                            "000000001",
//                            " where FTransDate = " +
//                            dbl.sqlDate(investFee.
//                                        getCurrentDate()));
	                    //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
	                    //add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
	                    sFNum = investPayAdmin.getNum();
                        pst.setString(1, sFNum);
                        pst.setString(2, investFee.getIvpBean().getIvPayCatCode());
                        pst.setDate(3, YssFun.toSqlDate(investFee.getCurrentDate()));
                        pst.setString(4, investFee.getPortCode());

                        //得到投资经理
                        pst.setString(5, " ");
                        if (investFee.getIvpBean().getFIVType().equalsIgnoreCase(
                            "managetrusteeFee")) { //运营收支品种类型为两费,panjunfang modify 20090629 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                            if ( ( (InvestFeeBean) (investFee.
                                getInvMgrInvestFee().
                                get(j))).getInvMgrCode().trim().
                                length() != 0) {
                                pst.setString(5,
                                              ( (InvestFeeBean) (investFee.
                                    getInvMgrInvestFee().
                                    get(j))).getInvMgrCode());
                            }
                        } else {
                            pst.setString(5,
                                          investFee.getInvMgrCode().trim().length() ==
                                          0 ? " " : investFee.getInvMgrCode());
                        }
                        pst.setString(6, " ");
                        pst.setString(7, " ");

	                  //增加对品种类型填充 中保处理管理费 by leeyu 20100713 QDV4中保2010年06月18日03_A MS01332
	                  if (investFee.getIvpBean().getFIVType()
	                		  .equalsIgnoreCase("managetrusteeFee")) {
	                	  if (((InvestFeeBean) (investFee
	                			  .getInvMgrInvestFee().get(j))).getIvpBean()
	                			  .getFAnalysisCode3()
	                			  .trim().length() != 0) {
	                		  pst.setString(7, ((InvestFeeBean) (investFee
	                				  .getInvMgrInvestFee().get(j))).getIvpBean()
	                				  .getFAnalysisCode3());
	                	  }
	                  } else {
	                	  pst.setString(7, investFee.getIvpBean().getFAnalysisCode3().trim()
	                			  .length() == 0 ? " " : investFee
	                					  .getIvpBean().getFAnalysisCode3());
	                  }
	                  //增加对品种类型填充 中保处理管理费 by leeyu 20100713
                        pst.setString(8, investFee.getIvpBean().getTsfTypeCode());
                        pst.setString(9, investFee.getIvpBean().getSubTsfTypeCode());
                        // #2279  add by wuweiqi 20110126 区分是否为多币种---------------//
                        if(((InvestFeeBean)investFee.getInvMgrInvestFee().get(j)).getPortCury()!= null){
                        	pst.setString(10, ((InvestFeeBean)investFee.getInvMgrInvestFee().get(j)).getPortCury());
                        }else{
                        	pst.setString(10, investFee.getPortCury());
                        }
                        //------------------------end by wuweiqi --------------------------//
                        //--- add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
						if (investFee.getIvpBean().getFIVType().equalsIgnoreCase(
                            "managetrusteeFee")) { //运营收支品种类型为两费,panjunfang modify 20090629 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                        	money = this.getSettingOper().reckonRoundMoney(
                                    investFee.getIvpBean().getRoundCode(),
                                    ((InvestFeeBean)(investFee.getInvMgrInvestFee().
                                    get(j))).getInvestFee());
                        } else {
                        	money = this.getSettingOper().reckonRoundMoney(
                                    investFee.getIvpBean().getAcroundCode(),
                                    ( (InvestFeeBean) (investFee.getInvMgrInvestFee().
                                    get(j))).getInvestFee());
                        }
                        pst.setDouble(11,money);
						//--- add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                        pst.setDouble(12, investFee.getBaseRate());
                        if (investFee.getIvpBean().getFIVType().equalsIgnoreCase(
                            "managetrusteeFee")) { //运营收支品种类型为两费,panjunfang modify 20090629 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                            pst.setDouble(13,
                                          this.getSettingOper().reckonRoundMoney(
                                              investFee.
                                              getIvpBean().getRoundCode(),
                                              ( (InvestFeeBean) (investFee.
                                getInvMgrInvestFee().
                                get(j))).getBaseInvestFee()));
                        } else {
                            pst.setDouble(13,
                                          this.getSettingOper().reckonRoundMoney(
                                              investFee.
                                              getIvpBean().getAcroundCode(),
                                              ( (InvestFeeBean) (investFee.
                                getInvMgrInvestFee().
                                get(j))).getBaseInvestFee()));

                        }
                        pst.setDouble(14, investFee.getPortRate());
                        if (investFee.getIvpBean().getFIVType().equalsIgnoreCase(
                            "managetrusteeFee")) { //运营收支品种类型为两费,panjunfang modify 20090629 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                            pst.setDouble(15,
                                          this.getSettingOper().reckonRoundMoney(
                                              investFee.
                                              getIvpBean().getRoundCode(),
                                              ( (InvestFeeBean) (investFee.
                                getInvMgrInvestFee().
                                get(j))).getPortInvestFee()));
                        } else {
                            pst.setDouble(15,
                                          this.getSettingOper().reckonRoundMoney(
                                              investFee.
                                              getIvpBean().getAcroundCode(),
                                              ( (InvestFeeBean) (investFee.
                                getInvMgrInvestFee().
                                get(j))).getPortInvestFee()));

                        }
                        pst.setDouble(16, 0);
                        pst.setDouble(17, 0);

                        pst.setInt(18, 1);
                        pst.setString(19, pub.getUserCode());
                        pst.setString(20, YssFun.formatDatetime(new java.util.Date()));
                        pst.setString(21, pub.getUserCode());
                        pst.setString(22, YssFun.formatDatetime(new java.util.Date()));
                       //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 ----//
                        if(((InvestFeeBean)investFee.getInvMgrInvestFee().get(j)).getIvpBean().getAttrClsCode().trim().length() !=0){
                        	pst.setString(23, ((InvestFeeBean)investFee.getInvMgrInvestFee().get(j)).getIvpBean().getAttrClsCode());
                        }else{
                        	pst.setString(23, " ");
                        }
                    	//--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao end -----------//
                        pst.executeUpdate();
                        
                        //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
                        logInfo = "运营费用代码:" + investFee.getIvpBean().getIvPayCatCode() + 
                                   "\r\n运营费用：" + money;
                        
                        if(logOper != null){
                        	logOper.setDayFinishIData(this,7,operType, pub, false, 
                        		investFee.getPortCode(), investFee.getCurrentDate(),
                        		investFee.getCurrentDate(),investFee.getCurrentDate(),
                        		logInfo," ",logBeginDate,logSumCode,new Date());
                        }
                        //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
                    }
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
        	//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
        	try{
                logOper.setDayFinishIData(this,7,operType, pub, true, 
                		investFee.getPortCode(), investFee.getCurrentDate(),
                		investFee.getCurrentDate(), investFee.getCurrentDate(),
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                		(logInfo + "\r\n计提费用出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
                		.replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", ""),
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                		" ",logBeginDate,logSumCode,new Date());
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	finally{//添加 finally 保证可以抛出异常
            	// by 曹丞 2009.01.24 保存计提费用金额异常信息 MS00004 QDV4.1-2009.2.1_09A
                throw new YssException("系统保存计提费用金额时出现异常!" + "\n", e); 
        	}
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 判断要计算的费用是否为在通用参数中设置的要以公式计算的种类
     * @param investCode String
     * @param investCodes String[]
     * @return boolean
     * @throws YssException
     * sj modified MS00052
     */
    private boolean existCalcInvestCode(String investCode, String[] investCodes) throws
        YssException {
        boolean existInvestCode = false;
        if (investCodes == null) { //若为null，默认返回false。
            return existInvestCode;
        }
        for (int i = 0; i < investCodes.length; i++) {
            if (investCode.equalsIgnoreCase(investCodes[i])) {
                existInvestCode = true;
                break;
            }
        }
        return existInvestCode;
    }

    /**
     * 调用公式计算的方法，把计算所得放入容器中。以便录入数据。
     * @param pay InvestFeeBean
     * @param invest InvestPayBean
     * @param formula String
     * @param dDate Date
     * @param analys1 boolean
     * @param analys2 boolean
     * @param analys3 boolean
     * @return InvestFeeBean
     * @throws YssException
     * sj modified MS00052
     */
    private InvestFeeBean getCalcWithFormula(InvestFeeBean pay,
                                             InvestPayBean invest,
                                             String formula,
                                             java.util.Date dDate,
                                             boolean analys1, boolean analys2,
                                             boolean analys3
        ) throws YssException {
        ArrayList InvestFees = new ArrayList();
        double reValue = 0;
        reValue = calcWithFormula(pay, invest, formula, dDate, analys1, analys2,
                                  analys3);
        //------------------------------------------------------------------------
        InvestFeeBean obj = new InvestFeeBean();
        obj.setInvMgrCode(" ");
        obj.setInvestFee(reValue);
        obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(reValue,
            pay.getBaseRate()));
        obj.setPortInvestFee(this.getSettingOper().calPortMoney(reValue,
            pay.getBaseRate(), pay.getPortRate(),
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            pay.getPortCury(), pay.getCurrentDate(), pay.getPortCode()));
        InvestFees.add(obj);
        //------------------------------------------------------------------------
        //start modify huangqirong 2013-03-18 story #3682
        //当投资运营收支设置界面的补差日期 存在时进行计算补差处理        
        String sDateOfBC = "";				//记录补差日的日期 
        boolean bIfTheDateOfBC = false;		//计提日期是否符合补差日条件
        String strSql ="" ;
        double dTotal  = 0.0;	//已计提金额。
        double supplementMoney = 0.0 ; //补差金额
        double sumFee = reValue ;
        
        if("2".equalsIgnoreCase(pay.getIvpBean().getPeriodOfBC())){
        	if(pay.getIvpBean().getSupplementDate() != null && pay.getIvpBean().getSupplementDate().trim().length() > 0 && !"null".equalsIgnoreCase(pay.getIvpBean().getSupplementDate().trim()) )
        	{
        		String[] supplementDates = pay.getIvpBean().getSupplementDate().split(",");
        		for (int i = 0; i < supplementDates.length; i++)
        		{
        			if (!supplementDates[i].trim().equals(""))
        			{
            			if (YssFun.formatDate(supplementDates[i],"yyyyMMdd").equalsIgnoreCase(YssFun.formatDate(pay.getCurrentDate(),"yyyyMMdd")))
            			{
            				sDateOfBC = YssFun.formatDate(pay.getCurrentDate(),"yyyy-MM-dd");
            				bIfTheDateOfBC = true;
            				break;
            			}
        			}
        		}
        	}
        }
        
        if (bIfTheDateOfBC)
        {
        	ResultSet rsTemp = null;
        	strSql = "Select * from " + pub.yssGetTableName("Tb_Stock_Invest") + " where FIVPayCatCode = " + dbl.sqlString(pay.getIvpBean().getIvPayCatCode()) +
        			 " and FPortCode = " + dbl.sqlString(pay.getPortCode()) + " and FStorageDate = " + dbl.sqlDate(YssFun.addDay(YssFun.toDate(sDateOfBC), -1));
        	try {
        		rsTemp = dbl.queryByPreparedStatement(strSql);
        		while(rsTemp.next())
            	{
            		dTotal = rsTemp.getDouble("FBal");
            	}
			} catch (Exception e) {
				System.out.println("获取前一天运营库存数据出错：" + e.getMessage());
				throw new YssException("获取前一天运营库存数据出错：" + e.getMessage());
			}finally{
				dbl.closeResultSetFinal(rsTemp);
			}
        }
        
        if("2".equalsIgnoreCase(pay.getIvpBean().getPeriodOfBC()) && bIfTheDateOfBC){
        	String currency = pay.getIvpBean().getLowerCurrencyCode(); //下限币种代码
        	if(currency != null && currency.trim().length() > 0) //设置了下限币种
        	{                		
        		if(!pay.getPortCury().equalsIgnoreCase(currency)){ //下限币种和计提币种不一致                		
            		double lowerMoney = pay.getIvpBean().getLimitedAmount(); //下限金额
            		BaseOperDeal operDeal = new BaseOperDeal();
            		operDeal.setYssPub(pub);	
            		double lowerBaseRate = operDeal.getCuryRate(pay.getCurrentDate(), currency, pay.getPortCode(), "base");
            		double payBaseRate = operDeal.getCuryRate(pay.getCurrentDate(), pay.getPortCury(), pay.getPortCode(), "base");
            		
            		double payLowerMoney = YssD.div(YssD.mul(lowerMoney, lowerBaseRate) , payBaseRate); //下限币种金额 转成计提币种金额
            		
            		double totalToDay = YssD.add(dTotal, sumFee);
            		supplementMoney =  totalToDay < payLowerMoney ? YssD.sub(payLowerMoney, totalToDay) : 0;
        		}else if(pay.getPortCury().equalsIgnoreCase(currency)){	//下限币种和计提币种不一致
        			double lowerMoney = pay.getIvpBean().getLimitedAmount(); //下限金额
            		double totalToDay = YssD.add(dTotal, sumFee);
            		supplementMoney =  totalToDay < lowerMoney ? YssD.sub(lowerMoney, totalToDay) : 0;
        		}
        	}else{	//未设置下限币种
        		double totalToDay = YssD.add(dTotal, sumFee);                    		
        		supplementMoney =  totalToDay < pay.getIvpBean().getLimitedAmount() ? YssD.sub(pay.getIvpBean().getLimitedAmount(), totalToDay) : 0;
        	}
        }
       
        if(supplementMoney > 0){
        	InvestPayBean investPay = new InvestPayBean();                    	
        	investPay.setTsfTypeCode(YssOperCons.Yss_ZJDBLX_SUPPLEMENT);//计提补差 调拨类型
        	investPay.setSubTsfTypeCode(YssOperCons.Yss_ZJDBLX_SUPPLEMENT_IV);//计提补差 调拨子类型         
        	investPay.setFIVType(pay.getIvpBean().getFIVType());
        	investPay.setIvPayCatCode(pay.getIvpBean().getIvPayCatCode());
        	investPay.setFAnalysisCode3(pay.getIvpBean().getFAnalysisCode3());
        	investPay.setRoundCode(pay.getIvpBean().getRoundCode());
        	investPay.setAcroundCode(pay.getIvpBean().getAcroundCode());
        	investPay.setAttrClsCode(pay.getIvpBean().getAttrClsCode());
        	obj = new InvestFeeBean();
            obj.setInvMgrCode(" "); //暂不区分投资经理
            obj.setInvestFee(supplementMoney); //原币计提费用
            obj.setIvpBean(investPay);
            obj.setHaveIvpBean(true);
            obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(supplementMoney, pay.getBaseRate())); //根据原币计提费用和基础汇率算出基础货币计提费用
            obj.setPortInvestFee(this.getSettingOper().calPortMoney(supplementMoney, pay.getBaseRate(), pay.getPortRate(),
            		pay.getPortCury(), pay.getCurrentDate(), pay.getPortCode())); //计算组合货币计提费用        
            InvestFees.add(obj);
        }
        //end add by huangqirong 2013-03-18 story #3682        
        pay.setInvMgrInvestFee(InvestFees);
        return pay;
    }

    /**
     * 调用设置的公式，计算费用。
     * @param pay InvestFeeBean
     * @param invest InvestPayBean
     * @param formula String
     * @param dDate Date
     * @param analys1 boolean
     * @param analys2 boolean
     * @param analys3 boolean
     * @return double
     * @throws YssException
     * sj modified MS00052
     */
    public double calcWithFormula(InvestFeeBean pay, InvestPayBean invest,
                                   String formula,
                                   java.util.Date dDate,
                                   boolean analys1, boolean analys2,
                                   boolean analys3) throws YssException {
        double reValue = 0;
        YssInvestInfo investInfo = new YssInvestInfo();
        BaseInvestOper investOper = null;
        if (invest.getPeriodCode() != null) {
            if (invest.getPeriodCode().trim().length() == 0) {
                throw new YssException("请设置【" +
                                       invest.getIvPayCatCode() +
                                       "】运营收支期间！");
            }
        }
        if (formula == null || formula.trim().length() == 0) {
            throw new YssException("请设置【" +
                                   invest.getIvPayCatCode() +
                                   "】运营收支计算公式！");
        }
        if (analys1) { //实际上只有在为预提待摊的时候会有投资经理的设置
            investInfo.setSAnalys1(pay.getInvMgrCode().length() > 0 ? pay.getInvMgrCode() : " ");
        }
        if (analys2) {
            investInfo.setSAnalys2(invest.getFAnalysisCode2());
        }
        if (analys3) {
            investInfo.setSAnalys3(invest.getFAnalysisCode3());
        }
        investInfo.setDDate(dDate);
        investInfo.setSIVPayCatCode(invest.getIvPayCatCode());
        investInfo.setSPortCode(pay.getPortCode());
        investInfo.setCalcFomula(formula);
        //------------------------------------------------------------------------
        BaseOperDeal operDeal = new BaseOperDeal();
        operDeal.setYssPub(pub);
        investOper = operDeal.getInvestSpringRe(investInfo.getCalcFomula());
        if (investOper == null) {
            throw new YssException(
                "请设置【基础参数模块的Spring调用】，引用BeanId：FeeCalc");
        }
        investOper.setYssPub(pub);
        investOper.init(investInfo);
        reValue = investOper.calcInvest();
        return reValue;
    }

    /**
     * 判断当月此费用是否已计算过。
     * @param sInvestCodes String
     * @param sPortCode String
     * @param dDate Date
     * @return boolean
     * @throws YssException
     * sj modified MS00052
     */
     private boolean existInvestPay(String sInvestCodes, String sPortCode,
                                   java.util.Date dDate) throws YssException {
        boolean existInvest = false;
        String sqlStr = "";
        ResultSet rs = null;
        int currentYear = YssFun.getYear(dDate);
        int currentMonth = YssFun.getMonth(dDate);
        Calendar calendar = new GregorianCalendar(currentYear, currentMonth, 0);
        sqlStr = "select invest.*,pay.Fivpaycatname as FIvPayCatName from " +
            "(select * from " + pub.yssGetTableName("Tb_data_investpayrec") +
            " where FCheckState = 1 and FSubTsfTypeCode not like '99%' and FIvPayCatCode = " +
            dbl.sqlString(sInvestCodes) +
            " and FPortCode = " + dbl.sqlString(sPortCode) +
            " and (FTransDate between " +
            dbl.sqlDate(String.valueOf(currentYear) + "-" +
                        (currentMonth > 9 ? String.valueOf(currentMonth) :
                         "0" + String.valueOf(currentMonth))
                        + "-01") + " and  " +
            dbl.sqlDate(String.valueOf(currentYear) + "-" +
                        (currentMonth > 9 ? String.valueOf(currentMonth) :
                         "0" + String.valueOf(currentMonth))
                        + "-" +
                        String.valueOf(calendar.getActualMaximum(Calendar.DATE))) +
            ")" +
            /**shashijie 2012-6-6 BUG 4732 增加业务类型业务子类型的判断,以免把收益支付的数据也查询出来了 */
            " And FTsfTypeCode = "+dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay)+
            " And FSubTsfTypeCode = "+dbl.sqlString(YssOperCons.YSS_ZJDBZLX_IV_Pay)+
			/**end*/
            " ) invest " +
            " left join " +
            " (select * from tb_base_investpaycat where FCheckState = 1) pay " +
            " on invest.FIvPayCatCode = pay.Fivpaycatcode ";
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                existInvest = true;
                throw new YssException(rs.getString("FIvPayCatName") +
                                       "上月的费用已计提,若需重新计提请删除运营应收应付中" +
                                       String.valueOf(currentMonth) + "月的" +
                                       rs.getString("FIvPayCatName") + ",其业务日期为" + YssFun.formatDate(rs.getDate("FTransDate")));
            }
        } catch (YssException e) {
            //throw new YssException(e.getMessage());
            throw new YssException(e); //by caocheng MS00004 QDV4.1-2009.2.1_09A 新的异常处理方法
        } catch (Exception ex) {
            // throw new YssException("获取当月计提费用出错!"+"\n");
            throw new YssException("获取当月计提费用出错!" + "\n", ex); //by caocheng MS00004 QDV4.1-2009.2.1_09A 新的异常处理方法
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return existInvest;
    }

    /**
     * 判断是否为按月计提的类型，在操作时却以日期段为操作，导致产生多条应收应付数据
     * @param IVPayCatCode String
     * @param portCode String
     * @param analys1 String
     * @param reIncomes ArrayList 之前已计提的数据的容器
     * @throws YssException
     * sj MS00270 QDV4赢时胜（上海）2009年2月25日01_B
     */
    private void checkIVPayHasCalc(String IVPayCatCode, String portCode, String analys1, ArrayList reIncomes) throws YssException {
        InvestFeeBean invest = null;
        String sqlStr = "";
        ResultSet rs = null;
        if (null == reIncomes || reIncomes.size() == 0) {
            return;
        }
        java.util.Iterator it = reIncomes.iterator();
        sqlStr = "select * from tb_base_investpaycat where FCheckState = 1 and Fivpaycatcode = " + dbl.sqlString(IVPayCatCode); //获取运营费用的名称
        while (it.hasNext()) {
            invest = (InvestFeeBean) it.next();
            if (IVPayCatCode.equalsIgnoreCase(invest.getIvpBean().getIvPayCatCode())
                && portCode.equalsIgnoreCase(invest.getPortCode())
                && analys1.equalsIgnoreCase(invest.getInvMgrCode())) { //当已上条件的运营费用已已计提
                try {
                    rs = dbl.openResultSet(sqlStr);
                    if (rs.next()) {
                        throw new YssException("对不起！" + rs.getString("FivpaycatName") + "为按月计提类型，请确认计提时间为一日！"); //抛出提示信息。
                    }
                } catch (Exception ex) {
                    throw new YssException(ex);
                } finally {
                    dbl.closeResultSetFinal(rs);
                }
            }
        }
    }
	/**
    * 获取费用关联设置
    * QDV4中保2010年06月18日03_A MS01332 中保管理费计提的需求 by leeyu 20100716
    * @param investCode
    * @return
    * @throws YssException
    */
   private ArrayList getInvestRelaParams(String investCode,String portCode,String analySisCode1,String analySisCode2,String analySisCode3) throws YssException{
	   ResultSet rs =null;
	   String strSql="";
	   ArrayList arrParams=new ArrayList();
	   InvestRelaSetBean investRela =null;
	   try{
		   strSql="select a.*,b.FRoundCode as FBRoundCode,b.FPerExpCode as FBPerExpCode,"+
		   " b.FPeriodCode as FBPeriodCode,b.FFixRate as FBFixRate "+
		   " from (select * from "+pub.yssGetTableName("Tb_Para_InvestFeeRela")+
		   " where FIvPayCatCode="+dbl.sqlString(investCode)+
		   " and FPortCode="+ dbl.sqlString(portCode) +
		   " and FAnalySisCode1="+dbl.sqlString(analySisCode1.length()==0?" ":analySisCode1)+
		   " and FAnalySisCode2="+dbl.sqlString(analySisCode2.length()==0?" ":analySisCode2)+
		   " and FAnalySisCode3="+dbl.sqlString(analySisCode3.length()==0?" ":analySisCode3)+
		   		" )a left join (select * from "+
		   pub.yssGetTableName("Tb_Para_Investpay")+
		   " where FIvPayCatCode="+dbl.sqlString(investCode)+
		   " and FPortCode="+ dbl.sqlString(portCode) +
		   " and FAnalySisCode1="+dbl.sqlString(analySisCode1.length()==0?" ":analySisCode1)+
		   " and FAnalySisCode2="+dbl.sqlString(analySisCode2.length()==0?" ":analySisCode2)+
		   " and FAnalySisCode3="+dbl.sqlString(analySisCode3.length()==0?" ":analySisCode3)+
		   " ) b on a.FIvPayCatCode=b.FIvPayCatCode "+
		   " and a.FAnalySisCode1=b.FAnalySisCode1 "+
		   " and a.FAnalySisCode2=b.FAnalySisCode2 "+
		   " and a.FAnalySisCode3=b.FAnalySisCode3 ";
		   rs =dbl.openResultSet(strSql);
		   while(rs.next()){
			   investRela = new InvestRelaSetBean();
			   investRela.setPortCode(rs.getString("FPortCode"));
			   investRela.setCatCode(rs.getString("FCatCode"));
			   //investRela.setInvMgrCode(rs.getString("FInvMgrCode"));
			   investRela.setAnalysisCode1(rs.getString("FAnalySisCode1"));
			   investRela.setAnalysisCode2(rs.getString("FAnalySisCode2"));
			   investRela.setAnalysisCode3(rs.getString("FAnalySisCode3"));
			   investRela.setiVPayCatCode(rs.getString("FIvPayCatCode"));
			   investRela.setFixRate(rs.getDouble("FFixRate"));
			   investRela.setRoundCode(rs.getString("FRoundCode"));
			   investRela.setPerExpCode(rs.getString("FPerExpCode"));
			   arrParams.add(investRela);
		   }
	   }catch(Exception ex){
		   throw new YssException("获取运营设置代码【"+investCode+"】参数出错");
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
	   return arrParams;
   }
   
   /**
    * 获取资本数据,算出平均净值
    * QDV4中保2010年06月18日03_A MS01332 中保管理费计提的需求 by leeyu 20100716
    * @param investRela
    * @param startDate
    * @param endDate
    * @return 期末净值+流入资本－流出资本
    * 当为流入时：  －金额+（金额/（月末日期－月初日期）*（月末日期－业务日期+1）
    * 当为流出时：  +金额-（金额/（月末日期－月初日期）*（月末日期－业务日期+1）
    * @throws YssException
    */
   private double getCashTransAsset(InvestRelaSetBean investRela,java.util.Date startDate,java.util.Date endDate) throws YssException{
	   ResultSet rs =null;
	   String strSql="";
	   double dSumAsset=0.0D;
	   try{
		   //获取期末净值
		   dSumAsset = getEndMonthNavData(investRela,startDate,endDate);
		   strSql="select sum(subCash.FMoney*FBaseCuryRate/FPortCuryRate) as FPortCuryMony,subCash.FInOut,cash.FTransDate from "+pub.yssGetTableName("Tb_Cash_SubTransfer")+" subCash "+
		   " join (select * from "+pub.yssGetTableName("Tb_Cash_Transfer")+
		   " where FTransDate between "+dbl.sqlDate(startDate)+" and "+dbl.sqlDate(endDate)+" and FTsfTypeCode='"+YssOperCons.YSS_ZJDBLX_Capital+"' and FCheckState=1 ) cash on subcash.FNum=cash.FNum "+
		   " where subCash.FPortCode="+dbl.sqlString(investRela.getPortCode())+
		   (investRela.getCatCode().length()>0?(" and subCash.FAnalySisCode2="+dbl.sqlString(investRela.getCatCode())):" ")+
		   (investRela.getAnalysisCode1().length()>0?(" and subCash.FAnalySisCode1="+dbl.sqlString(investRela.getAnalysisCode1())):"")+//add by jiangshichao 2010.08.19
		   " group by subCash.FInOut,cash.FTransDate"+
		   " Order by cash.FtransDate ";
		   rs =dbl.openResultSet(strSql);
		   while(rs.next()){
			   if(rs.getInt("FInOut")==1){
				   //当为流入时： －金额+（金额/（月末日期－月初日期）*（月末日期－业务日期+1）
				   dSumAsset = YssD.sub(dSumAsset, rs.getDouble("FPortCuryMony"));
				   //dSumAsset = YssD.round(YssD.add(dSumAsset, YssD.mul(YssD.div(rs.getDouble("FPortCuryMony"), YssFun.dateDiff(rs.getDate("FTransDate"), startDate)+1),YssFun.dateDiff(endDate , startDate))),2);
				   dSumAsset = YssD.round(YssD.add(dSumAsset, YssD.mul(YssD.div(rs.getDouble("FPortCuryMony"), YssFun.dateDiff(YssFun.addDay(startDate,-1),endDate)),YssFun.dateDiff(rs.getDate("FTransDate"),endDate)+1)),2);
			   }else{
				   //当为流出时： +金额-（金额/（月末日期－月初日期）*（月末日期－业务日期+1）
				   dSumAsset = YssD.add(dSumAsset, rs.getDouble("FPortCuryMony"));
				   //dSumAsset = YssD.round(YssD.sub(dSumAsset, YssD.mul(YssD.div(rs.getDouble("FPortCuryMony"), YssFun.dateDiff(rs.getDate("FTransDate"), startDate)+1),YssFun.dateDiff(endDate , startDate))),2);
				   //dSumAsset = YssD.round(YssD.add(dSumAsset, YssD.mul(YssD.div(rs.getDouble("FPortCuryMony"), YssFun.dateDiff(YssFun.addDay(startDate,-1),endDate)),YssFun.dateDiff(rs.getDate("FTransDate"),endDate)+1)),2);
				   dSumAsset = YssD.round(YssD.sub(dSumAsset, YssD.mul(YssD.div(rs.getDouble("FPortCuryMony"), YssFun.dateDiff(YssFun.addDay(startDate,-1),endDate)),YssFun.dateDiff(rs.getDate("FTransDate"),endDate)+1)),2);
			   }
		   }
	   }catch(Exception ex){
		   throw new YssException("计算组合【"+investRela.getPortCode()+"】期间从【"+YssFun.formatDate(startDate)+"】到【"+YssFun.formatDate(endDate)+"】平均净值数据出错",ex);
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
	   return dSumAsset;
   }
   
   /**
    * QDV4中保2010年06月18日03_A MS01332 中保管理费计提的需求 by leeyu 20100716
    * 获取月初-1天(上月月末)的净值
    * 中保管理费计提需求 by leeyu 20100713
    * @param investRela
    * @param startDate
    * @return
    * @throws YssException
    */
   private double getStartMonthNavData(InvestRelaSetBean investRela,java.util.Date startDate) throws YssException{
	   ResultSet rs = null;
	   String strSql = "";
	   double dNavData = 0.0D;
	   java.util.Date dLastEndMonth = null;
	   try{
		   //获取组合信息
		   PortfolioBean portfolio =new PortfolioBean();
		   portfolio.setYssPub(pub);
		   portfolio.setPortCode(investRela.getPortCode());
		   portfolio.getSetting();
		   if(YssFun.formatDate(startDate,"MMdd").equalsIgnoreCase("0101")||YssFun.formatDate(startDate,"yyyyMM").equalsIgnoreCase(YssFun.formatDate(portfolio.getInceptionDate(),"yyyyMM"))){
			   //如果是年初或本组合基金成立月，取期初净值表的数据
			   strSql="select FPortMarketValue from "+pub.yssGetTableName("Tb_Data_NavBeginPeriod")+
			   " where FPortCode="+dbl.sqlString(investRela.getPortCode())+" and FCatCode="+dbl.sqlString(investRela.getCatCode())+
			   (investRela.getAnalysisCode1().trim().length()>0?" and FInvMgrCode="+dbl.sqlString(investRela.getAnalysisCode1()):" and FInvMgrCode=' '")+
			   " and FNavDate=(select max(FNavDate) as FNavDate from "+
			   pub.yssGetTableName("Tb_Data_NavBeginPeriod")+
			   " where FPortCode="+dbl.sqlString(investRela.getPortCode())+" and FCatCode="+dbl.sqlString(investRela.getCatCode())+
			   (investRela.getAnalysisCode1().trim().length()>0?" and FInvMgrCode="+dbl.sqlString(investRela.getAnalysisCode1()):" and FInvMgrCode=' '")+
			   " and FNavDate<="+dbl.sqlDate(startDate)+" )";
			   rs = dbl.openResultSet(strSql);
			   if(rs.next()){
				   dNavData=rs.getDouble("FPortMarketValue");
			   }
		   }else{
			   //取上月期末组合的数据
			   dLastEndMonth=YssFun.addDay(startDate, -1);
			   //重新计提上月月末的净值
			   CtlNavRep navrep = new CtlNavRep();
		       navrep.setPortCode(investRela.getPortCode());
		       navrep.setInvMgrCode(investRela.getAnalysisCode1().trim().length()>0?investRela.getAnalysisCode1():"total");
		       navrep.setDDate(dLastEndMonth);
		       navrep.setIsSelect(false);
		       navrep.setYssPub(pub);
		       navrep.invokeOperMothed();
		         
			   strSql="select FPortMarketValue from "+pub.yssGetTableName("Tb_Data_Navdata")+
			   " where FPortCode="+dbl.sqlString(investRela.getPortCode())+" and FNavDate= "+dbl.sqlDate(dLastEndMonth)+
			   " and FReTypeCode='Total' and FKeyCode<>'TotalValue' and FKeyCode = 'TotalValue_"+investRela.getCatCode()+"'"+
			   (investRela.getAnalysisCode1().trim().length()>0?(" and FInvMgrCode="+dbl.sqlString(investRela.getAnalysisCode1())):" and FInvMgrCode='total'");
			   rs =dbl.openResultSet(strSql);
			   if(rs.next()){
				   dNavData=rs.getDouble("FPortMarketValue");
			   }
		   }
	   }catch(Exception ex){
		   throw new YssException(ex.getMessage(),ex);
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
	   return dNavData;
   }
   
   /**
    * 获取月末净值
    * 中保管理费计提需求 by leeyu 20100713
    * QDV4中保2010年06月18日03_A MS01332
    * @param investRela
    * @param startDate 开始日期
    * @param endDate 结束日期
    * @return
    * @throws YssException
    */
   private double getEndMonthNavData(InvestRelaSetBean investRela,java.util.Date startDate,java.util.Date endDate) throws YssException{
	   ResultSet rs =null;
	   String strSql="";
	   double dPayData=0.0D;//发生额
	   double dNavData=0.0D;//市值
	   try{
		   //如果月末有发生额数据这里先减掉，算出不含发生额的净值
		   strSql="select sum(FPortCuryMoney*b.FAddType) as FPortCuryMoney,a.FAnalySisCode1,a.FAnalySisCode2,a.FIvPayCatCode from "+
		   //"select sum(FPortCuryMoney*b.FAddType) as FPortCuryMoney,a.FAnalySisCode1,a.FAnalySisCode2,a.FAnalySisCode3,a.FIvPayCatCode from "+
		   " (select FAnalySisCode1,FAnalySisCode2,FAnalySisCode3,FIvPayCatCode,FPortCuryMoney from "+pub.yssGetTableName("Tb_Data_Investpayrec")+
		   " where FPortCode="+dbl.sqlString(investRela.getPortCode())+" and FIvPayCatCode="+dbl.sqlString(investRela.getiVPayCatCode())+" and FTransDate between "+dbl.sqlDate(startDate)+" and "+dbl.sqlDate(endDate) +
		   " and FTsfTypeCode="+dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay)+" and FSubTsfTypeCode="+dbl.sqlString(YssOperCons.YSS_ZJDBZLX_IV_Pay)+" and FCheckState=1) a "+
		   " join(select FIVPayCatCode,case when FPayType=1 then 1 else -1 end as FAddType from Tb_Base_InvestPayCat) b "+
		   " on a.FIvPayCatCode=b.FIvPayCatCode where 1=1 "+
		   (investRela.getAnalysisCode1().trim().length()>0?(" and FAnalySisCode1="+dbl.sqlString(investRela.getAnalysisCode1())):"")+
		   //(investRela.getCatCode().length()>0?(" and FAnalySisCode3="+dbl.sqlString(investRela.getCatCode())):"")+
		   //" group by FAnalySisCode1,FAnalySisCode2,FAnalySisCode3,a.FIvPayCatCode";
		   " group by FAnalySisCode1,FAnalySisCode2,a.FIvPayCatCode";
		   rs =dbl.openResultSet(strSql);
		   if(rs.next()){
			   dPayData = rs.getDouble("FPortCuryMoney");
		   }
		   dbl.closeResultSetFinal(rs);
		   //获取当月月末的净值
		   strSql="select FPortMarketValue from "+pub.yssGetTableName("Tb_Data_Navdata")+
		   " where FPortCode="+dbl.sqlString(investRela.getPortCode())+" and FNavDate= "+dbl.sqlDate(endDate)+
		   " and FReTypeCode='Total' and FKeyCode<>'TotalValue' and FKeyCode = 'TotalValue_"+investRela.getCatCode()+"'"+
		   " and FInvMgrCode="+dbl.sqlString((investRela.getAnalysisCode1().trim().length()>0?investRela.getAnalysisCode1():"total"));
		   rs =dbl.openResultSet(strSql);
		   if(rs.next()){
			   dNavData = rs.getDouble("FPortMarketValue");
		   }
	   }catch(Exception ex){
		   throw new YssException("获取月末净值信息出错",ex);
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
	   return YssD.add(dNavData, dPayData);//这里获取的是未减费用之前的净值
   }
   
   /**
    * 计算费用 QDV4中保2010年06月18日03_A MS01332
    * @param avgNavValue 平均净值
    * @param lastMonthNavValue 上月末净值
    * @return 返回 round((平均净值+上月月末净值)/2*(期末日期-期初日期)/ 总期间天数*费用费率,舍入位数)
    * @throws YssException
    */
   private double calcFees(double avgNavValue,double lastMonthNavValue,InvestFeeBean pay,InvestRelaSetBean investRela) throws YssException{
		if (pay.getIvpBean().getPeriodCode() == null
				|| pay.getIvpBean().getPeriodCode().trim().equals("null")
				|| pay.getIvpBean().getPeriodCode().trim().equalsIgnoreCase("")) {
			throw new YssException("请先维护" + pay.getIvpBean().getIvPayCatName()
					+ "的期间设置");
		}
		PeriodBean Period = new PeriodBean();
		Period.setYssPub(pub);
		Period.setPeriodCode(pay.getIvpBean().getPeriodCode());
		Period.getSetting();
		if(investRela.getFixRate()!=0.0D){
			return this.getSettingOper().reckonRoundMoney(investRela.getRoundCode(),YssD.div(YssD.mul(YssD.div(YssD.add(avgNavValue, lastMonthNavValue), 2),
					YssFun.dateDiff(YssFun.addDay(beginDate,-1), endDate), investRela.getFixRate()),Period.getDayOfYear()));
		}else{
			return this.getSettingOper().calMoneyByPerExp(investRela.getPerExpCode(), investRela.getRoundCode(), YssD.mul(YssD.div(YssD.add(avgNavValue, lastMonthNavValue), 2),
					YssFun.dateDiff(YssFun.addDay(beginDate,-1), endDate),Period.getDayOfYear()), endDate);			
		}
   }
   
   /**
    * 按月末净值计提费用  QDV4中保2010年06月18日03_A MS01332
    * 中保管理费计提需求 by leeyu 20100713
    * @param pay
    * @param analy1
    * @param analy2
    * @param analy3
    * @return
    * @throws YssException
    */
   private InvestFeeBean calcFees(InvestFeeBean pay,boolean analy1,boolean analy2,boolean analy3) throws YssException{
	   ArrayList arrInvestPara=null;
	   ArrayList arrInvestFee =new ArrayList();
	   InvestRelaSetBean investRela =null;
	   double avgNav=0.0D;//本期平均市值
	   double dLastEndMonthNav = 0.0D;//上期末市值
	   InvestFeeBean obj=null;
	   try{
		   arrInvestPara =getInvestRelaParams(pay.getIvpBean().getIvPayCatCode(),pay.getPortCode(),pay.getIvpBean().getFAnalysisCode1(),pay.getIvpBean().getFAnalysisCode2(),pay.getIvpBean().getFAnalysisCode3());
		   if(arrInvestPara.size()==0){
			   //如果没有设置参数还是按原来的模式进行计算
			   pay = calculateIncome(pay);
		   }else{
			   for(int i=0;i<arrInvestPara.size();i++){
				   investRela =(InvestRelaSetBean)arrInvestPara.get(i);
				   avgNav = getCashTransAsset(investRela,beginDate,endDate);
				   dLastEndMonthNav = getStartMonthNavData(investRela,beginDate);
				   obj = new InvestFeeBean();
				   obj.setInvMgrCode(" ");//投资经理这里暂放空格
				   if(analy1)
					   obj.setInvMgrCode(investRela.getAnalysisCode1());
				   obj.getIvpBean().setFAnalysisCode1(obj.getInvMgrCode());
				   obj.getIvpBean().setFAnalysisCode3(investRela.getCatCode());
				   //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 ----//
				   obj.getIvpBean().setAttrClsCode(investRela.getCatCode());
				  //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao end -----------//
				   obj.setInvestFee(calcFees(avgNav,dLastEndMonthNav,pay,investRela));
				   obj.setBaseInvestFee(this.getSettingOper().calBaseMoney(
						   obj.getInvestFee(), pay.getBaseRate()));
	                  obj.setPortInvestFee(this.getSettingOper().calPortMoney(
	                		  obj.getInvestFee(), pay.getBaseRate(), pay.getPortRate(),
	                        pay.getPortCury(), pay.getCurrentDate(),
	                        pay.getPortCode()));
				   arrInvestFee.add(obj);
			   }
			   pay.setInvMgrInvestFee(arrInvestFee);
		   }
	   }catch(Exception ex){
		   throw new YssException(ex.getMessage(),ex);
	   }finally{
		   
	   }
	   return pay;
   }
   
   //20110817 added by liubo.Story #1227
   //此方法用于计算均摊方式为“按月再按日”的待摊数据的最后一个月之前几个月的分摊的总额。
   //将前几个月的分摊的金额进行累加，得出最后一个月之前的几个月的分摊总额。
   
   private double getTotalExceptLastMonth(int iMonthDiff,double dbTotal) throws YssException
   {
	   double dbResult = 0.00;
	   double dbAvgMonth = 0.00;
//	   java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00");  
	   
	   dbAvgMonth = YssD.round(YssD.div(dbTotal, iMonthDiff + 1),2);
	   
	   for (int i = 1; i <= iMonthDiff; i++)
	   {
		   dbResult = dbResult + dbAvgMonth ;
	   }

	   return YssD.round(dbResult,2);

   }
}
