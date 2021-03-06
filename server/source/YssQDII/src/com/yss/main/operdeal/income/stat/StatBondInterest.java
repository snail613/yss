package com.yss.main.operdeal.income.stat;

import com.yss.util.*;
import java.util.*;
import java.util.Date;
import java.sql.*;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.manager.*;
import com.yss.main.operdeal.bond.*;
import com.yss.main.operdata.*;
import com.yss.pojo.param.bond.YssBondIns;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.commeach.EachRateOper;
import com.yss.main.parasetting.FixInterestBean;
import com.yss.main.storagemanage.SecurityStorageBean;
import com.yss.main.operdeal.stgstat.StgSecurity;

public class StatBondInterest
    extends BaseIncomeStatDeal {

    //债券库存 2009.09.04 蒋锦 添加
    //MS00656 QDV4赢时胜(上海)2009年8月24日01_A
    private HashMap hmSecStg = null;
	private String secCodes="";//QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315 合并太平版本代码
    
	//--- add by songjie 2013.06.13 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 start---//
	private String calProcess = "";//债券利息计算公式
    //--- add by songjie 2013.06.13 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 end---//
    
	public StatBondInterest() {
    }

    public ArrayList getDayIncomes(java.util.Date dDate) throws YssException {
        ArrayList alIncomes = new ArrayList();
        SecPecPayBean pay = null;
        String strSql = "";
        ResultSet rs = null;
        double baseCuryRate;
        double portCuryRate;

        boolean analy1;
        boolean analy2;
        boolean analy3; // 2007.12.06 添加 蒋锦 证券库存的分析代码
        java.util.Date dFactDate = null;
        BaseStgStatDeal secstgstat = null;

        double dInsMoney = 0; //利息金额
        //MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
        //不计息的组合代码
        HashMap hmNoStatPorts = null;
        CtlPubPara pubPara = new CtlPubPara();
        pubPara.setYssPub(pub);

        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------
        SecIntegratedAdmin integrAdmin = new SecIntegratedAdmin(pub);
        ArrayList alIntegr = new ArrayList();
        int digit = 0; //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B  MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        Date logBeginDate = null;
        String portCode = "";//组合代码
        //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            hmNoStatPorts = pubPara.getDontStatBondIns();
            if(hmNoStatPorts.size() != 0){
                String sNoStatPorts = "";
                Iterator it = hmNoStatPorts.keySet().iterator();
                while(it.hasNext()){
                    sNoStatPorts += ((String)it.next() + ",");
                }
                sNoStatPorts = sNoStatPorts.substring(0, sNoStatPorts.length() - 1);
                throw new YssException("对不起，您选择的组合“" + sNoStatPorts + "”已被设置为不计息，请重新选择需要计息的组合，或重新设置通用业务参数！");
            }

            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");

            //------------2008.08.27 蒋锦 添加 在库存统计之前要先调用证券业务的业务处理--------------//
            //MS00022    国内债券业务    QDV4.1赢时胜（上海）2009年4月20日22_A
            
            //BUG3870 MODIFY BY ZHOUWEI 20120511 不进行证券业务处理
//            CtlSecManage secManage = new CtlSecManage();
//            secManage.setYssPub(pub);
//            String[] arrPortCode = portCodes.split(",");
//            for(int i = 0; i < arrPortCode.length; i++){
//                secManage.initOperManageInfo(dDate, arrPortCode[i]);
//                secManage.doOpertion();
//            }
            //--------------------------------------------------------------------------------//

            //-----------------------------------------------------------------------------------
            //---add by songjie 2011.12.19 BUG 3330 大成基金2011年12月06日01_B start---//
            if(selCodes.trim().length() == 0){
            	return new ArrayList();
            }
            //---add by songjie 2011.12.19 BUG 3330 大成基金2011年12月06日01_B end---//
            
            secstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("SecurityStorage"); //证券库存  sj 20071130 每次都先统计库存
            secstgstat.setYssPub(pub);
         	secstgstat.setStatCodes(selCodes);//添加证券代码，by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
            //债券计息时不需要完全的证券库存统计 2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
         	//
         	//add huangqirong 2012-07-20 bug #4940 老券库存转入 新券库存   之前是在库存统计那边才转入 收益计提也会统计库存但是没有转入
         	secstgstat.setStgFrom("Valuation");
         	StgSecurity security = (StgSecurity)secstgstat;
         	security.getSecNameExchangeData(dDate, operSql.sqlCodes(portCodes));
         	secstgstat.setStgFrom("");
         	//---end---
        	
            secstgstat.partStroageStat1(dDate, dDate, operSql.sqlCodes(portCodes), true, false); //sj
            //-----------------------------------------------------------------------------------
            //-----------MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14
            CtlPubPara pubpara = new CtlPubPara();
            boolean isNew = false;
            pubpara.setYssPub(pub);
            String sType = pubpara.getNavType();
            if (sType.toLowerCase().equalsIgnoreCase("new")) {
                isNew = false;
            } else {
                isNew = true;
            }
            //------MS00127
	        //QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
	        String sFIType=pubpara.getIncomeFIDateCalcType();         
	        YssGlobal.hmSecRecBeans.clear();//清空原有的应收应付数据
	        deleteSecPay(dDate,selCodes,portCodes);//删除之前已计提的数据
	        //QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
            secstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("SecRecPay"); //证券应收应付库存
            secstgstat.setYssPub(pub);
            dFactDate = YssFun.addDay(dDate, -1); //取前一天的日期
            if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
                dFactDate = dDate;
            }
            System.out.println("Date=" + YssFun.formatDate(dFactDate, "yyyy-MM-dd"));
            //2009.04.25 蒋锦 添加 MS00006 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
            //不需要更新汇率行情
			secstgstat.setStatCodes(selCodes);//添加证券代码，by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
            secstgstat.stroageStat(dFactDate, dFactDate, operSql.sqlCodes(portCodes), true, false, false); //by leeyu 080708 这里要先统计证券应收应付
			//太平资产债券计息时还需临时统计当日的应收应付利息，原因是他的利息公式中用到当日的利息余额。这里的统计应收应付余额是不包含本笔利息的余额 合并太平版本代码
			if(YssFun.dateDiff(dFactDate, dDate)!=0)//添加判断，如果是年初的话下一句与上一句代码统计重复，故加判断 by leeyu 20100414
        	 	secstgstat.stroageStat(dDate,dDate, operSql.sqlCodes(portCodes), true, false, false); //by leeyu 080708 这里要先统计证券应收应付 //这里调整为统计昨日与今日两天的证券应收应付库存 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
            //------------------------
			
            //--- add by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            BondAssist bondAssist = new BondAssist();
            bondAssist.setYssPub(pub);
            bondAssist.getBasicBondInfo(selCodes,dDate,portCodes);
            bondAssist.insertFixRelaInfo(selCodes, dDate, portCodes);
            //--- add by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
			
         	strSql = "select FHSecurityCode,FHPortCode,FHolidaysCode," +
                //"FHaveAmount," + //去除数量 sj
                "case when t4.FSecurityCode is null then 'No' else 'Yes' end as FSourceType,"+
                "FExchangeCode as FQExchangeCode, FMarketCode as FQMarketCode," +
                "FCatCode as FQCatCode,FSubCatCode as FQSubCatCode," +
                "FCusCatCode as FQCusCatCode,FISINCode as FQISincode," +
                "FExternalCode as FQExternalCode," +
                dbl.sqlIsNull("FTradeCury", "FPortCuryCode") + " as FCuryCode," +
                "FSettleDayType as FQSettleDayType,FHolidaysCode as FQHolidaysCode," +
                "FSettleDays as FQSettleDays,FSectorCode as FQSectorCode," +
                "FTotalShare as FQTotalShare,FCurrentShare as FQCurrentShare," +
                "FHandAmount as FQHandAmount,FFactor as FQFactor," +
                //2007.12.01 修改 蒋锦 为 FStartDate、FDesc 增加别名否则在DB2下会出现字段模糊错误
                //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                //edit by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加证券名称
                "FIssueCorpCode as FQIssueCorpCode,q.FStartDate as FQStartDate, q.FDesc as FQDesc, q.FSecurityName, h.FAttrClsCode, "+
                " h.FInvestType, " + //2008-08-22 宋洁 添加 投资类型字段 国内:MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
                (analy1 ? "h.FAnalysisCode1," : " ") + //杨文奇20071214
                (analy2 ? "h.FAnalysisCode2," : " ") +
                (analy3 ? "h.FAnalysisCode3," : " ") +

                "r.*,y.*,yes.*,z.*,m.FSPICode,n.FBeanId,(case when zb.FCPIPRICE >0 then zb.FCPIPRICE else za.FCPIPRICE end) as FCPIPRICE from (" +//edit by yanghaiming 20110212 #461
                " select d.*, 'FLINK' as FLINK from " +//edit by yanghaiming 20110212 #461
                pub.yssGetTableName("tb_Para_Security") +
                //当不判断了启用日期字段后，后面的join group by FSecuritycode就没有意义，这里注释掉以提高SQl执行效率 by leeyu 20100819 
                " d "+
//                "/*join (select FSecurityCode from " + //xuqiji 20090427 删除 Max(FStartDate) as FStartDate:QDV4赢时胜（上海）2009年4月21日01_B  MS00405    收益计提计提债券利息时对债券启用日期有判断
//                pub.yssGetTableName("tb_Para_Security") + //" where FStartDate<=" +dbl.sqlDate(dDate) + //by xuqiji 20090427:QDV4赢时胜（上海）2009年4月21日01_B  MS00405    收益计提计提债券利息时对债券启用日期有判断
//                " where FCheckState = 1 group by FSecurityCode) e on d.FSecurityCode = e.FSecurityCode*/ " + //xuqiji 20090427 删除 and d.FStartDate = e.FStartDate:QDV4赢时胜（上海）2009年4月21日01_B  MS00405    收益计提计提债券利息时对债券启用日期有判断
                " where d.FCatCode = 'FI' and d.FCheckState = 1" +
               " and d.FSecurityCode in (" + operSql.sqlCodes(selCodes) + ")"+//添加证券条件,对查询参数的优化　by leeyu 20100414 合并太平版本
               " ) q  " +
                //--------------------------------------------------------------------------------//证券信息
                //" left join (select " +
                "  join (select " + //这里改为 join 获取是与库存相关的证券 by leeyu 20100416 合并太平版本代码
                //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                //2009.08.22 宋洁 添加 投资类型字段 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
                " distinct FPortCode as FHPortCode, FSecurityCode as FHSecurityCode, FAttrClsCode, FInvestType " + //2007.12.06 修改 蒋锦 添加 DISTINCT
                //" ,FStorageAmount as FHaveAmount" + //去除数量 sj
                (analy1 ? ",FAnalysisCode1" : " ") + //缺少“，”，杨文奇20071214
                (analy2 ? ",FAnalysisCode2" : " ") +
                (analy3 ? ",FAnalysisCode3" : " ") +
                " from " +
                //--------------------------------------------------------------------------------//库存
                pub.yssGetTableName("Tb_Stock_Security") +
                //--------------------------------------------------------------------------------
                " where FCheckState = 1 and FPortCode in (" +
                operSql.sqlCodes(portCodes) +
                ")" +
                //-------- add by wangzuochun 2010.06.17  MS01309    计提报错    QDV4赢时胜(测试)2010年06月17日01_B   
                " and FStorageDate between " + dbl.sqlDate(YssFun.addDay(dDate, -1)) + " and " + dbl.sqlDate(YssFun.addDay(dDate, 1)) +
               " and FSecurityCode in (" + operSql.sqlCodes(selCodes) + ")"+//把证券条件放在这里,优化查询参数  by leeyu 20100414 合并太平版本代码
                //----------------------------------------MS01309-------------------------------------------------//
                // " and FStorageDate = " + dbl.sqlDate(dDate) + //去除日期判断，为了一直能获取证券代码和组合代码 sj
                ") h on h.FHSecurityCode = q.FSecurityCode" + //sj add 20071223
                //--------------------------------------------------------------------------------//债券信息
               //" left join (select o2.* from " +
               "  join (select o2.* from " + //这里用join再获取债券信息中设置过的债券 by leeyu 20100416 合并太平版本代码
               //当不判断了启用日期字段后，后面的join group by FSecuritycode就没有意义，这里注释掉以提高SQl执行效率 by leeyu 20100819 
                pub.yssGetTableName("Tb_Para_FixInterest") + " o2 "+
//                "/*join " +
//                "(select FSecurityCode  from " + //xuqiji 20090427 删除,max(FStartDate) as FStartDate:QDV4赢时胜（上海）2009年4月21日01_B   MS00405    收益计提计提债券利息时对债券启用日期有判断
//                pub.yssGetTableName("Tb_Para_FixInterest") +
//                //" where FStartDate <= " + dbl.sqlDate(dDate) +    //xuqiji 20090427:QDV4赢时胜（上海）2009年4月21日01_B   MS00405    收益计提计提债券利息时对债券启用日期有判断
//                " where FSecurityCode in (" + operSql.sqlCodes(selCodes) + ")"+
//                " and FCheckState = 1 group by FSecurityCode) P2 " + //xuqiji 20090427 :QDV4赢时胜（上海）2009年4月21日01_B  MS00405    收益计提计提债券利息时对债券启用日期有判断
//			   " on o2.FSecurityCode=P2.FSecurityCode*/"+
			   ") r on h.FHSecurityCode = r.FSecurityCode" +//添加证券做为条件,对查询参数优化 by leeyu 20100414 合并太平版本代码
                //---------------------------------------------------------------------------------//债券利息
                " left join (select c.FIntAccPer100 as FYInAccPer100,c.FSecurityCode as FYSecurityCode from " +
                pub.yssGetTableName("tb_data_bondinterest") +
                " c join (select FSecurityCode,max(FRecordDate) as FRecordDate from " + //改left join 为 join 多出无用记录
                pub.yssGetTableName("tb_data_bondinterest") +
                " where FRecordDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 and " + dbl.sqlDate(dDate) +
                " between  FCurCpnDate and FNextCpnDate" +
               " and FSecurityCode in (" + operSql.sqlCodes(selCodes) + ")"+//添加证券做为条件,对查询参数的优化　by leeyu 20100414 合并太平版本代码
                " group by FSecurityCode) c1 on c.FSecurityCode = c1.FSecurityCode and c.FRecordDate = c1.FRecordDate" +
                " ) y on h.FHSecurityCode = y.FYSecurityCode " +
                //----------------------------------------------------------------------------------//前一天债券利息
                " left join (select c.FIntAccPer100 as FYesIntAccPer100,c.FSecurityCode as FYesSecurityCode from " +
                pub.yssGetTableName("Tb_data_bondinterest") +
                " c join (select FSecurityCode,max(FRecordDate) as FRecordDate from " +
                pub.yssGetTableName("tb_data_bondinterest") +
                " where FRecordDate <= " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                " and FCheckState = 1 and " +
                dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                " between FCurCpnDate and FNextCpnDate" +
               " and FSecurityCode in (" + operSql.sqlCodes(selCodes) + ")" +//添加证券做为条件,对查询参数的优化　by leeyu 20100414 合并太平版本代码
                " group by FSecurityCode) c1 on  c.FSecurityCode = c1.FSecurityCode and c.FRecordDate = c1.FRecordDate" +
                " ) yes on h.FHSecurityCode = yes.FYesSecurityCode " +
                //----------------------------------------------------------------------------------//组合信息
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
      
                " left join (select  FPortCode, FPortCury as FPortCuryCode from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getPrefixTB()) +               
                " ) z on h.FHPortcode = z.FPortcode" +
              
                
                //end by lidaolong
                //----------------------------------------------------------------------------------------------------sj add 20071120 获取Spring调用的代码
                " left join (select FCIMCode,FSPICode from Tb_Base_CalcInsMetic) m on m.FCIMCode = r.FCalcInsMeticDay" + // 在计息时可写死为每日计息
                " left join (select FSICode,FBeanId from TB_FUN_SPINGINVOKE) n on n.FSICode = m.FSPICode" +
              //QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315 合并太平版本代码
                " left join (select distinct FSecurityCode,FCurCpnDate,FNextCpnDate from "+pub.yssGetTableName("Tb_Para_BondParamater")+
                " where FSecurityCode in("+operSql.sqlCodes(selCodes)+") and "+
                dbl.sqlDate(dDate)+" between FCurCpnDate and FNextCpnDate and FcheckState=1) t4 on q.FSecurityCode=t4.FSecurityCode " +
                //QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315 合并太平版本代码               
               //" where FHSecurityCode in (" + operSql.sqlCodes(selCodes) + ")";//此条件已放在上面的语句里，对查询参数的优化 by leeyu 20100414
         		//add by yanghaiming 20110212 #461
         		"left join (select 'FLINK' as FLINK, FCPIPRICE from " + pub.yssGetTableName("TB_DATA_CPIVALUE") +
         		" where FCHECKSTATE = 1 and FCPIVALUEDATE = (select max(FCPIVALUEDATE) from " + pub.yssGetTableName("TB_DATA_CPIVALUE") +
                " where FCHECKSTATE = 1 and FPORTCODE = ' ' and FCPIVALUEDATE <= " + dbl.sqlDate(dDate) + ")  and FPORTCODE = ' ') za on q.FLINK = za.Flink" +
                " left join (select FCPIPRICE,FPORTCODE from " + pub.yssGetTableName("TB_DATA_CPIVALUE") +
                " where FCHECKSTATE = 1 and FCPIVALUEDATE = (select max(FCPIVALUEDATE) from " + pub.yssGetTableName("TB_DATA_CPIVALUE") +
                " where FCHECKSTATE = 1 and FPORTCODE <> ' ' and FCPIVALUEDATE <= " + dbl.sqlDate(dDate) + ")) zb on h.FHPortcode = zb.FPORTCODE" +
         		//add by huangqirong 2012-07-25 bug #4940 排除老券
         		" where q.FSecurityCode not in( select psc.fsecuritycodebefore as FSecurityCode  from " + pub.yssGetTableName("tb_para_seccodechange") + 
         		" psc where psc.fbusinessdate = " + dbl.sqlDate(dDate) + " and psc.fcheckstate = 1) " ;
         		//---end---
            rs = dbl.openResultSet(strSql); 
            while (rs.next()) {
            	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	logBeginDate = new Date();
            	portCode = rs.getString("FHPortCode");
            	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            	
                if ((YssFun.dateDiff(dDate, rs.getDate("FINSENDDATE")) < 0) || //若截至日期小于计息日期,则不计息.sj edit 20080827
                	//edit by songjie 2012.02.27 BUG 3869 QDV4赢时胜(测试)2012年02月14日01_B
                	(YssFun.dateDiff(dDate, rs.getDate("FInsStartDate")) > 0)) {//若计息开始日期大于计息日期，则不计息  
                    continue;
                }
				//QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315 合并太平版本代码
	            if(!sFIType.equalsIgnoreCase("autoCalc") && rs.getString("FSourceType").equalsIgnoreCase("No") && !otherParam.equalsIgnoreCase("true")){
	            	secCodes+=(rs.getString("FHSecurityCode")+",");
	            	continue;
	            }
	            //QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315 合并太平版本代码
	            
                //MS00656 QDV4赢时胜(上海)2009年8月24日01_A 2009.09.07 蒋锦
                //银行间和贴现债在 2月29日 计息  下面的判断先注释掉，2月29日计不计息在公式中体现，不用代码写死
                /*if(YssFun.formatDate(dDate, "MMdd").equalsIgnoreCase("0229") &&
                   !rs.getString("FQSubCatCode").equalsIgnoreCase("FI04") &&
                   !rs.getString("FQExchangeCode").equalsIgnoreCase(YssOperCons.YSS_JYSDM_YHJ)){
                    continue;
                }*/
	            
                pay = new SecPecPayBean();
                pay.setTransDate(dDate);
                pay.setStrPortCode(rs.getString("FHPortCode") + "");
                pay.setInvMgrCode(analy1 ? (rs.getString("FAnalysisCode1") + "") :
                                  " ");
                pay.setBrokerCode(analy2 ? (rs.getString("FAnalysisCode2") + "") :
                                  " ");
                pay.setStrSecurityCode(rs.getString("FHSecurityCode"));
                pay.setStrCuryCode(rs.getString("FCuryCode") + "");
                pay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                pay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FI_RecInterest);
                //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                pay.setAttrClsCode(rs.getString("FAttrClsCode"));
                //2009.08.22 宋洁 添加 投资类型字段 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
                pay.setInvestType(rs.getString("FInvestType"));

                //基础汇率
                baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FCuryCode"), pay.getStrPortCode(),
                    YssOperCons.YSS_RATE_BASE);

                //组合汇率
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), pay.getStrPortCode());
                portCuryRate = rateOper.getDPortRate();
                //-----------------------------------------------------------------------------------
                //数量
                //pay.setAmount(rs.getDouble("FHaveAmount"));

                pay.setBaseCuryRate(baseCuryRate);
                pay.setPortCuryRate(portCuryRate);

                //原币利息金额 = [（到昨日为目的最大库存日期的库存数量 + 到昨日为止的最大库存日期加1到今日的交易数量）/报价因子 * 百元债券利息]
                //备注：取到昨日为止的最大库存日期，是为了保证当天的交易都被统计进来
                //内部计算

                if (rs.getInt("FInterestOrigin") == 0) { //sj 20071120 edit 重写的计算债券计息的方式

                    dInsMoney = innerCal(rs.getString("FSecurityCode"),
                                         rs.getString("FPeriodCode"),
                                         rs.getString("FCalcInsMeticDay")
                                         , rs.getString("FPortCode"),
                                         rs.getString("FHolidaysCode"),
                                         dDate,
                                         rs.getDouble("FQFactor"),
                                         analy1 ? rs.getString("FAnalysisCode1") :
                                         "",
                                         analy2 ? rs.getString("FAnalysisCode2") :
                                         "",
                                         //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                                         rs.getString("FAttrClsCode"),//若存在分析代码,则加入. sj edit 20080729.
                                         rs.getDouble("FCPIPRICE"),//add by yanghaiming 20110212 #461 增加浮动CPI
                                         //add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                                         rs.getString("FInvestType")); //增加投资类型
                    //-----------------------根据在债券信息设置里的舍入方式设置来确定小数保留位数.sj edit 20080702 ----------------------
                    dInsMoney = setDigit(rs.getString("FRoundCode"), rs.getString("FHSecurityCode"), dInsMoney, isNew); //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14
                    //-------------------------------------------------------------------------------------------------------------
                    pay.setMoney(dInsMoney);
                }
                //----------------------------------------------------------------------------------------------------------------
                //外部计算
                else if (rs.getInt("FInterestOrigin") == 1) {

                    pay.setAmount(getAmount(dDate, analy1, analy2, analy3)); //调用新加入的方法 sj
                    dInsMoney = outerCal(rs.getDouble("FYInAccPer100"),
                                         rs.getDouble("FYesIntAccPer100")
                                         , rs.getDouble("FQFactor"), pay.getAmount());
                    //-----------------------根据在债券信息设置里的舍入方式设置来确定小数保留位数.sj edit 20080702 ----------------------
                    dInsMoney = setDigit(rs.getString("FRoundCode"), rs.getString("FHSecurityCode"), dInsMoney, isNew); //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14
                    //-------------------------------------------------------------------------------------------------------------
                    pay.setMoney(dInsMoney);
                }
                //先外部再内部
                else if (rs.getInt("FInterestOrigin") == 3) {
                    dInsMoney = outerCal(rs.getDouble("FYIntAccPer100"),
                                         rs.getDouble("FYesIntAccPer100")
                                         , rs.getDouble("FFactor"), pay.getAmount());
                    if (dInsMoney == 0.0) {
                        dInsMoney = innerCal(rs.getString("FSecurityCode"),
                                             rs.getString("FPeriodCode"),
                                             rs.getString("FCalcInsMeticDay")
                                             , rs.getString("FPortCode"),
                                             rs.getString("FHolidaysCode"),
                                             dDate,
                                             rs.getDouble("FQFactor"),
                                             analy1 ? rs.getString("FAnalysisCode1") :
                                             "",
                                             analy2 ? rs.getString("FAnalysisCode2") :
                                             "",
                                             //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                                             rs.getString("FAttrClsCode"), //若存在分析代码,则加入. sj edit 20080729.
                                             rs.getDouble("FCPIPRICE"), //add by yanghaiming 20110212 #461 增加浮动CPI
                                             //add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                                             rs.getString("FInvestType")); //增加投资类型
                    }
                    //-----------------------根据在债券信息设置里的舍入方式设置来确定小数保留位数.sj edit 20080702 ----------------------
                    dInsMoney = setDigit(rs.getString("FRoundCode"), rs.getString("FHSecurityCode"), dInsMoney, isNew); //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14
                    //-------------------------------------------------------------------------------------------------------------
                    pay.setMoney(dInsMoney);
                }
                //先内部再外部
                else if (rs.getInt("FInterestOrigin") == 4) {
                    dInsMoney = innerCal(rs.getString("FSecurityCode"),
                                         rs.getString("FPeriodCode"),
                                         rs.getString("FCalcInsMeticDay")
                                         , rs.getString("FPortCode"),
                                         rs.getString("FHolidaysCode"),
                                         dDate,
                                         rs.getDouble("FQFactor"),
                                         analy1 ? rs.getString("FAnalysisCode1") :
                                         "",
                                         analy2 ? rs.getString("FAnalysisCode2") :
                                         "",
                                         //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                                         rs.getString("FAttrClsCode"), //若存在分析代码,则加入. sj edit 20080729.
                                         rs.getDouble("FCPIPRICE"), //add by yanghaiming 20110212 #461 增加浮动CPI
                                         //add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                                         rs.getString("FInvestType")); //增加投资类型
                    if (dInsMoney == 0.0) {
                        dInsMoney = outerCal(rs.getDouble("FYIntAccPer100"),
                                             rs.getDouble("FYesIntAccPer100")
                                             , rs.getDouble("FFactor"), pay.getAmount());
                    }
                    //-----------------------根据在债券信息设置里的舍入方式设置来确定小数保留位数.sj edit 20080702 ----------------------
                    dInsMoney = setDigit(rs.getString("FRoundCode"), rs.getString("FHSecurityCode"), dInsMoney, isNew); //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14
                    //-------------------------------------------------------------------------------------------------------------
                    pay.setMoney(dInsMoney);
                }
                pay.setBaseCuryMoney(setDigit(rs.getString("FRoundCode"),
                                              rs.getString("FHSecurityCode"),
                                              YssD.mul(pay.getMoney(), baseCuryRate), isNew)); //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14

                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 使用通用的计算组合金额的方法--------------------------
                //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
                digit = this.getSettingOper().getRoundDigit(dInsMoney); //得到原币的小数位
                pay.setPortCuryMoney(setDigit(rs.getString("FRoundCode"),
                                              rs.getString("FHSecurityCode"),
                                              this.getSettingOper().calPortMoney(
                                                  pay.getMoney(),
                                                  baseCuryRate, portCuryRate,
                                                  rs.getString("FCuryCode"), dDate, pay.getStrPortCode(), digit), isNew));
                //---------------------------------------end 20090505--------------------------------------------------------------------------//
                //-----------------------------------------------------------------------------------------------------------
                pay.setMMoney(pay.getMoney());
                pay.setVMoney(pay.getMoney());

                pay.setMBaseCuryMoney(pay.getBaseCuryMoney());
                pay.setVBaseCuryMoney(pay.getBaseCuryMoney());

                pay.setMPortCuryMoney(pay.getPortCuryMoney());
                pay.setVPortCuryMoney(pay.getPortCuryMoney());

                //alIncomes.add(pay);
				//--- 为了提高计息效率，增加库存日期的获取 sj -------
	            if (pay.getMoney() != 0){ //更改为当计的利息不等于0的情况时插入数据 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
	                alIncomes.add(pay);
	            }
	            
	            
	            // add by jiangshichao 20120228  添加债券利息税   当日发生额来计算 start -------------------------
//	            if(pay.getMoney() >0 && rs.getString("fInterTaxperexpcode") != null){
//	            	SecPecPayBean  dInterestTax =   getInterTax( rs.getString("fInterTaxperexpcode"),pay);
//	            	 alIncomes.add(dInterestTax);
//	            }
	            // 添加债券利息税 end ------------------------------------------------------------------
	            //add by zhouwei 20120308 根据债券利息以及利息税的余额，轧差来计算当日利息税
	            if(rs.getString("fInterTaxperexpcode") != null){
	            	//---edit by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
	            	pay = getInterTaxByBalance( rs.getString("fInterTaxperexpcode"),pay);
	                alIncomes.add(pay);
	                //---edit by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
	            }
	            //---------------------------------------------
                //如果投资类型为可供出售或持有到期则计算溢折价摊销金额
                //MS00656 QDV4赢时胜(上海)2009年8月24日01_A
                if ((rs.getString("FInvestType").equalsIgnoreCase(YssOperCons.YSS_INVESTTYPE_CYDQ) ||
                    rs.getString("FInvestType").equalsIgnoreCase(YssOperCons.YSS_INVESTTYPE_KGCS)) &&
                    pay.getMoney() != 0) {
                	//---edit by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
                	pay = calPADAmortization(pay, alIntegr, analy1, analy2, analy3);
                    alIncomes.add(pay);
                    //---edit by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
                }
//                if (rs.getString("FQSubCatCode").equalsIgnoreCase("FI05")||
//					//add by rujiangpeng MS01346 QDV4中保2010年06月24日01_A
//					rs.getString("FQSubCatCode").equalsIgnoreCase("FI15")) { //若类型为 持有到期，则计算摊销金额。
//                    pay = setAmortization(pay, rs, isNew);
//                    if (pay != null) {
//                        alIncomes.add(pay);
//                    }
//                }
                
                //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
                logInfo = "证券代码:" + rs.getString("FHSecurityCode") + 
                           "\r\n证券名称:" + rs.getString("FSecurityName") + 
                           "\r\n债券利息:" + pay.getMoney() +  
                           //--- add by songjie 2013.06.13 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 start---//
                           //添加债券计息公式到业务日志
                           (this.calProcess.trim().length() > 0 ? ("\r\n计算过程:\r\n" + this.calProcess) : "") +
                           //--- add by songjie 2013.06.13 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 start---//
                           YssCons.YSS_LINESPLITMARK;
                //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
                
                //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, false, 
                		portCode, dDate,dDate,dDate,logInfo,
                		" ",logBeginDate,logSumCode,new Date());
        		}
                //---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
            }
            
            //插入摊销的溢折价变动
			//QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315 合并太平版本代码
	         if(secCodes.endsWith(","))
	        	 secCodes =secCodes.substring(0,secCodes.length()-1);
	         resultMes =secCodes;//这里将未计息的证券代码传到前台
	         //QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
	         //这里将每天计息的数据按日保存，因为要统计当日利息余额时必须按日保存利息发生额 by leeyu 20100423
	         saveIncomes(alIncomes,dDate);
	         alIncomes.clear();
	         //这里将每天计息的数据按日保存，因为在上面的统计当日应收应付库存时要用 by leeyu 20100423
            conn.setAutoCommit(false);
            bTrans = true;
            integrAdmin.addList(alIntegr);
            integrAdmin.insert(dDate, dDate, "", portCodes, YssOperCons.YSS_JYLX_PADAMORT, "05", "05FIDI", "", "", "", "", "", -1, true);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            return alIncomes;
        } catch (Exception e) {
        	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, true, 
                		portCode, dDate, dDate, dDate, 
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                		(logInfo + "\r\n债券计息出错\r\n" + e.getMessage()) //处理日志信息 除去特殊符号
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
        		//by 曹丞 2009.01.23 库存债券计息异常信息 MS00004 QDV4.1-2009.2.1_09A
        		throw new YssException("系统进行库存债券计息时出现异常!\n", e); 
        	}
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
            //--- add by songjie 2013.03.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            BondAssist.hmSec = null;
            BondAssist.hmParam = null;
            BondAssist.hmPort = null;
            BondAssist.hmBonds = null;
            BondInsCfgFormula.hmSec = null;
            BondInsCfgFormulaN.hmSec = null;
            BondInsCfgFormulaN.hmKey = null;
            //--- add by songjie 2013.03.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
        }

    }

    //获取已计提的债券利息 修改人：胡昆 20070730
    private double getPickFiInterest(double dFaceRate, int iPeriodDay,
                                     double dFactor, int iPickDay) {
        double dResult = 0;
        if (dFactor == 0) {
            dFactor = 1;
        }
        dResult = YssD.div(dFaceRate, iPeriodDay);
        dResult = YssD.div(dResult, dFactor);
        dResult = YssD.mul(dResult, iPickDay);
        return dResult;
    }

   /**
    * 重写保存的方法，这里按日保存数据  by leeyu 20100423 合并太平版本代码时调整
    * @param alIncome
    * @param dDate
    * @throws YssException
    */
	private void saveIncomes(ArrayList alIncome,java.util.Date dDate) throws YssException {
		if(alIncome.size()<=0)
		   return;
        int i = 0;
        SecPecPayBean secpecpay = null;
        SecRecPayAdmin recpay = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = true; //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
        String sSecurityCodes = "";
        CtlPubPara pubpara = null;
        boolean isFourDigit = false;
        //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
        String sAnalysisCode1 = "";
        String sAnalysisCode2 = "";
        String exitNum = "";
        //--------------------------------------end-----------------------------------------------------------------------------------------//
        try {
            conn.setAutoCommit(false);
            //------------------------------------------------
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            String digit = pubpara.getKeepFourDigit();
            if (digit.toLowerCase().equalsIgnoreCase("two")) {
                isFourDigit = false;
            } else if (digit.toLowerCase().equalsIgnoreCase("four")) {
                isFourDigit = true;
            }
            //-----------------------------------------------
            recpay = new SecRecPayAdmin();
            recpay.setYssPub(pub);
            for (i = 0; i < alIncome.size(); i++) {
                secpecpay = (SecPecPayBean) alIncome.get(i);
                secpecpay.checkStateId = 1;
                sSecurityCodes += secpecpay.getStrSecurityCode() + ","; //sj edit 20080618
                //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
                sAnalysisCode1 += secpecpay.getInvMgrCode() + ",";
                sAnalysisCode2 += secpecpay.getBrokerCode() + ",";
                //--------------------------------------------------end------------------------------------------------------------------//
                recpay.addList(secpecpay);
            }
            //因为证券应收应付的操作BEAN 已做更改
            if (sSecurityCodes.length() > 0) {
                sSecurityCodes = sSecurityCodes.substring(0,
                    sSecurityCodes.length() - 1);
                //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
                sAnalysisCode1 = sAnalysisCode1.substring(0,
                    sAnalysisCode1.length() - 1);
                sAnalysisCode2 = sAnalysisCode2.substring(0,
                    sAnalysisCode2.length() - 1);
                //---------------------------------------------------end-------------------------------------------------------------//
            }
            //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
            exitNum = getExitNum();
            //2009.09.04 蒋锦 添加 先删掉溢折价摊销的应收应付
            //MS00656 QDV4赢时胜(上海)2009年8月24日01_A 摊销的应收应付的方向都是流出
            recpay.delete("", beginDate, endDate, "05", "05FIDI", "", "", portCodes, "", "", "", 0, -1);
            //insert 方法 增加了参数 2009.06.29 蒋锦 添加 关联编号 关联编号类型
            //MS00013   QDV4.1赢时胜（上海）2009年4月20日13_A
            recpay.insert("", dDate, dDate,
                          YssOperCons.YSS_ZJDBLX_Rec + "," + YssOperCons.Yss_ZJDBLX_Premium + "," + YssOperCons.Yss_ZJDBLX_Discounts + ",05,"+YssOperCons.YSS_ZJDBLX_Pay,
                          YssOperCons.YSS_ZJDBZLX_FI_RecInterest + "," + YssOperCons.YSS_ZJDBZLX_Premium_Fix + "," + YssOperCons.YSS_ZJDBZLX_Discounts_Fix + "," + "05FIDI,"+YssOperCons.YSS_ZJDBZLX_LXS_FI,
                          portCodes, sAnalysisCode1, sAnalysisCode2, selCodes.trim().length()>0?selCodes:sSecurityCodes, "", -99, true, 0, isFourDigit, exitNum, "", ""); //sj edit 20080618 增加债券代码 append 为true的话，则可保留4位小数。
            //MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
            //---------------------------------------------------end 20090505--------------------------------------------------------------------//
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("系统保存债券计息时出现异常!\n", e); // by 曹丞 2009.01.23 保存债券计息异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
	
    public void saveIncomes(ArrayList alIncome) throws YssException {
	    if(alIncome.size()<=0)//增加判断，若为0直接返回  by leeyu 20100423 合并太平版本代码
		    return;
        int i = 0;
        SecPecPayBean secpecpay = null;
        SecRecPayAdmin recpay = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = true; //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
        String sSecurityCodes = "";
        CtlPubPara pubpara = null;
        boolean isFourDigit = false;
        //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
        String sAnalysisCode1 = "";
        String sAnalysisCode2 = "";
        String exitNum = "";
        //--------------------------------------end-----------------------------------------------------------------------------------------//
        try {
            conn.setAutoCommit(false);
            //------------------------------------------------
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            String digit = pubpara.getKeepFourDigit();
            if (digit.toLowerCase().equalsIgnoreCase("two")) {
                isFourDigit = false;
            } else if (digit.toLowerCase().equalsIgnoreCase("four")) {
                isFourDigit = true;
            }
            //-----------------------------------------------
            recpay = new SecRecPayAdmin();
            recpay.setYssPub(pub);
            for (i = 0; i < alIncome.size(); i++) {
                secpecpay = (SecPecPayBean) alIncome.get(i);
                secpecpay.checkStateId = 1;
                sSecurityCodes += secpecpay.getStrSecurityCode() + ","; //sj edit 20080618
                //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
                sAnalysisCode1 += secpecpay.getInvMgrCode() + ",";
                sAnalysisCode2 += secpecpay.getBrokerCode() + ",";
                //--------------------------------------------------end------------------------------------------------------------------//
                recpay.addList(secpecpay);
            }
            //因为证券应收应付的操作BEAN 已做更改
            if (sSecurityCodes.length() > 0) {
                sSecurityCodes = sSecurityCodes.substring(0,
                    sSecurityCodes.length() - 1);
                //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
                sAnalysisCode1 = sAnalysisCode1.substring(0,
                    sAnalysisCode1.length() - 1);
                sAnalysisCode2 = sAnalysisCode2.substring(0,
                    sAnalysisCode2.length() - 1);
                //---------------------------------------------------end-------------------------------------------------------------//
            }
            //add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
            exitNum = getExitNum();
            //2009.09.04 蒋锦 添加 先删掉溢折价摊销的应收应付
            //MS00656 QDV4赢时胜(上海)2009年8月24日01_A 摊销的应收应付的方向都是流出
            recpay.delete("", beginDate, endDate, "05", "05FIDI", "", "", portCodes, "", "", "", 0, -1);
            //insert 方法 增加了参数 2009.06.29 蒋锦 添加 关联编号 关联编号类型
            //MS00013   QDV4.1赢时胜（上海）2009年4月20日13_A
            recpay.insert("", beginDate, endDate,
                          YssOperCons.YSS_ZJDBLX_Rec + "," + YssOperCons.Yss_ZJDBLX_Premium + "," + YssOperCons.Yss_ZJDBLX_Discounts + ",05,"+YssOperCons.YSS_ZJDBLX_Pay,
                          YssOperCons.YSS_ZJDBZLX_FI_RecInterest + "," + YssOperCons.YSS_ZJDBZLX_Premium_Fix + "," + YssOperCons.YSS_ZJDBZLX_Discounts_Fix + "," + "05FIDI,"+YssOperCons.YSS_ZJDBZLX_LXS_FI,
                          portCodes, sAnalysisCode1, sAnalysisCode2, sSecurityCodes, "", -99, true, 0, isFourDigit, exitNum, "", ""); //sj edit 20080618 增加债券代码 append 为true的话，则可保留4位小数。
            //MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
            //---------------------------------------------------end 20090505--------------------------------------------------------------------//
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("系统保存债券计息时出现异常!\n", e); // by 曹丞 2009.01.23 保存债券计息异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public double outerCal(double YIntAccPer100, double YesIntAccPer100,
                           double Factor, double Amount) {
        double reVal = 0.0;
        double todayInt = 0.0;
        double yesInt = 0.0;
        if (Factor != 0) {
            todayInt = YssD.mul(YssD.div(Amount,
                                         Factor),
                                YIntAccPer100);
            yesInt = YssD.mul(YssD.div(Amount,
                                       Factor),
                              YesIntAccPer100);
        } else {
            todayInt = YssD.mul(Amount,
                                YIntAccPer100);
            yesInt = YssD.mul(Amount,
                              YesIntAccPer100);
        }
        reVal = YssD.sub(todayInt, yesInt);
        return reVal;
    }

//--------------------放入分析代码的相关数据，以便在计算如未结算的债券数量时获取正确数据。暂无相关bug编号 sj edit 20080729-------------------------------------------------------------------------------

    /**
     * 2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * @param SecurityCode String
     * @param PeriodCode String
     * @param CalcInsMeticDay String
     * @param PortCode String
     * @param FHolidaysCode String
     * @param dDate Date
     * @param dFactor double
     * @param analys1 String
     * @param sAttrClsCode String
     * @return double
     * @throws YssException
     */
    public double innerCal(String SecurityCode, String PeriodCode,
                           String CalcInsMeticDay
                           , String PortCode, String FHolidaysCode,
                           java.util.Date dDate, double dFactor, String analys1,
                           //edit by sognjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 增加投资类型
                           String sAttrClsCode,double cpiPrice,String investType) throws//add by yanghaiming 20110212 #461 增加浮动CPI
        YssException {
        return innerCal(SecurityCode, PeriodCode, CalcInsMeticDay, PortCode,
        		        //edit by sognjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 增加投资类型
                        FHolidaysCode, dDate, dFactor, analys1, "", sAttrClsCode,cpiPrice,investType);
    }

    /**
     * 2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * @param SecurityCode String
     * @param PeriodCode String
     * @param CalcInsMeticDay String
     * @param PortCode String
     * @param FHolidaysCode String
     * @param dDate Date
     * @param dFactor double
     * @param sAttrClsCode String
     * @return double
     * @throws YssException
     */
    public double innerCal(String SecurityCode, String PeriodCode,
                           String CalcInsMeticDay
                           , String PortCode, String FHolidaysCode,
                           java.util.Date dDate, double dFactor,
                           //edit by sognjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 增加投资类型
                           String sAttrClsCode,double cpiPrice,String investType) throws//add by yanghaiming 20110212 #461 增加浮动CPI
        YssException {
        return innerCal(SecurityCode, PeriodCode, CalcInsMeticDay, PortCode,
        		        //edit by sognjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 增加投资类型
                        FHolidaysCode, dDate, dFactor, "", "", sAttrClsCode,cpiPrice,investType);
    }

    /**
     * 2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * @param SecurityCode String
     * @param PeriodCode String
     * @param CalcInsMeticDay String
     * @param PortCode String
     * @param FHolidaysCode String
     * @param dDate Date
     * @param dFactor double
     * @param analys1 String
     * @param analys2 String
     * @param sAttrClsCode String
     * @return double
     * @throws YssException
     */
    public double innerCal(String SecurityCode, String PeriodCode,
                           String CalcInsMeticDay
                           , String PortCode, String FHolidaysCode,
                           java.util.Date dDate, double dFactor, String analys1, String analys2,
                           //增加投资类型 edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           String sAttrClsCode,double cpiPrice,String investType) throws //增加了报价因子的获取.sj edit 20080826//add by yanghaiming 20110212 #461 增加浮动CPI
        YssException {
    	//增加投资类型 edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
        return innerCal(SecurityCode, PeriodCode, CalcInsMeticDay, PortCode, FHolidaysCode, dDate, dFactor, analys1, analys2, "", sAttrClsCode,cpiPrice, investType);
    }

    /**
     * 2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * @param SecurityCode String
     * @param PeriodCode String
     * @param CalcInsMeticDay String
     * @param PortCode String
     * @param FHolidaysCode String
     * @param dDate Date
     * @param dFactor double
     * @param analys1 String
     * @param analys2 String
     * @param sType String
     * @param sAttrClsCode String
     * @return double
     * @throws YssException
     */
    public double innerCal(String SecurityCode, String PeriodCode,
                           String CalcInsMeticDay,
                           String PortCode,
                           String FHolidaysCode, //重载此方法
                           java.util.Date dDate,
                           double dFactor,
                           String analys1,
                           String analys2,
                           String sType,
                           String sAttrClsCode,//增加了一个类型,从外面传进来的类型  by leeyu
                           double cpiPrice,//add by yanghaiming 20110212 #461 增加抗通胀浮动CPI
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 增加投资类型
                           String investType) throws 
        YssException {
        double reVal = 0.0;
        YssBondIns bondIns = null;
        BaseBondOper bondOper = null;
        //---delete by songjie 2012.02.24 BUG 3925 QDV4赢时胜(上海开发部)2012年2月24日01_B start---//
		//在解析公式的时候 如果公式中包含InsDays关键字 若未设置计息天数 则提示设置
//        if (PeriodCode != null) {
//            if (PeriodCode.trim().length() == 0) {
//                throw new YssException("请设置【" +
//                                       SecurityCode +
//                                       "】债券的计息天数！");
//            }
//        }
        //---delete by songjie 2012.02.24 BUG 3925 QDV4赢时胜(上海开发部)2012年2月24日01_B end---//
        if (CalcInsMeticDay != null &&
            CalcInsMeticDay.length() > 0) { //每日利息算法
            bondIns = new YssBondIns();
            if (sType.length() == 0) {
                bondIns.setInsType("Day");
            } else {
                bondIns.setInsType(sType);
            }
        }
		//-----邵宏伟2010-2-9增加else控制,否则每日利息算法为空时，bondIns变量为Null报错 合并太平版本代码---------------------------------------
	    else {
	        throw new YssException("请设置【" +
	                             SecurityCode +
	                             "】债券的每日利息算法！");

	     }
	    //----------------------------------------
        //------------------------若有分析代码，则加入。sj edit 20080630--------------
        if (analys1 != null && analys1.length() > 0) {
            bondIns.setAnalysisCode1(analys1);
        }
        if (analys2 != null && analys2.length() > 0) {
            bondIns.setAnalysisCode2(analys2);
        }
        //-------------------------------------------------------------------------
        if (bondIns != null) {
        	//--- add by songjie 2013.04.02 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(bondIns.getInsType().trim().equals("Day") && BondAssist.hmSec != null && BondAssist.hmSec.get(SecurityCode) != null){
            	FixInterestBean  fixIt= (FixInterestBean)BondAssist.hmSec.get(SecurityCode);
            	String beanId = fixIt.getBeanId();
                bondOper = (BaseBondOper) pub.getOperDealCtx().getBean(beanId);
            }else{
            	//--- add by songjie 2013.04.02 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
                BaseOperDeal operDeal = new BaseOperDeal();
                operDeal.setYssPub(pub);
                bondOper = operDeal.getSpringRe(SecurityCode,
                                                (sType.length() > 0 ? sType : "Day")); //生成BaseBondOper
            }//add by songjie 2013.04.02 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001

            if (bondOper == null) {
                throw new YssException(
                    "请设置【基础参数模块的Spring调用】，引用BeanId：BondInsCfgFormula");

            }
            bondIns.setSecurityCode(SecurityCode);
            bondIns.setInsDate(dDate);
            bondIns.setPortCode(PortCode);
            bondIns.setHolidaysCode(FHolidaysCode);
            bondIns.setAttrClsCode(sAttrClsCode);
            //-------增加了报价因子的获取.sj edit 20080826 ---//
            bondIns.setFactor(dFactor);
            //---------------------------------------------//
            bondIns.setCpiPrice(cpiPrice);//add by yanghaiming 20110212 #461 增加浮动CPI
            //add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
            bondIns.setInvestType(investType);
            
            //--- add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(BondAssist.hmSec != null && BondAssist.hmSec.get(SecurityCode) != null){
            	FixInterestBean  fixIt= (FixInterestBean)BondAssist.hmSec.get(SecurityCode);
            	bondIns.setFaceValue(fixIt.getStrFaceValue());
            }
            //--- add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            
            bondOper.setYssPub(pub);
            bondOper.init(bondIns);
         	bondOper.setsOtherParams(otherParam);//QDV4中保2010年03月03日01_A MS01009  获取版本信息  by leeyu 20100315
            reVal = bondOper.calBondInterest();
			//--- add by songjie 2013.06.13 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 start---//
            this.calProcess = bondOper.getCalProcess();//获取债券计息公式
			//--- add by songjie 2013.06.13 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 end---//
			//add by songjie 2011.10.18 需求 #1245 QDV4易方达2011年6月20日01_A
            bondOper.insertDate();
        }
        return reVal;
    }
    
    /**
     * 2009.12.17 
     * add by songjie 
     * 用于计算国内接口的税前或税后的债券利息
     * MS00847 
     * QDV4赢时胜（北京）2009年11月30日03_B
     * @param SecurityCode String
     * @param PeriodCode String
     * @param CalcInsMeticDay String
     * @param PortCode String
     * @param FHolidaysCode String
     * @param dDate Date
     * @param dFactor double
     * @param analys1 String
     * @param analys2 String
     * @param sType String
     * @param sAttrClsCode String
     * @return double
     * @throws YssException
     */
    public HashMap domesticInnerCal(String SecurityCode, 
    		               String PeriodCode,
                           String CalcInsMeticDay,
                           String PortCode,
                           String FHolidaysCode, //重载此方法
                           java.util.Date dDate,
                           double dFactor,
                           String analys1,
                           String analys2,
                           String sType,
                           String sAttrClsCode,
                           String portCode,
                           String exchangeCode) throws //增加了一个类型,从外面传进来的类型  by leeyu
        YssException {
        double reVal = 0.0;
        double beforeVal = 0.0;
        YssBondIns bondIns = null;
        YssBondIns beforeBondIns = null;
        BaseBondOper bondOper = null;
        BaseOperDeal operDeal = null;
        HashMap hmRate = new HashMap();
        //---delete by songjie 2012.02.24 BUG 3925 QDV4赢时胜(上海开发部)2012年2月24日01_B start---//
        //在解析公式的时候 如果公式中包含InsDays关键字 若未设置计息天数 则提示设置
//        if (PeriodCode != null) {
//            if (PeriodCode.trim().length() == 0) {
//                throw new YssException("请设置【" +
//                                       SecurityCode +
//                                       "】债券的计息天数！");
//            }
//        }
        //---delete by songjie 2012.02.24 BUG 3925 QDV4赢时胜(上海开发部)2012年2月24日01_B end---//
        if (CalcInsMeticDay != null &&
            CalcInsMeticDay.length() > 0) { //每日利息算法
            bondIns = new YssBondIns();
            beforeBondIns = new YssBondIns();
            if (sType.length() == 0) {
                bondIns.setInsType("Day");
                beforeBondIns.setInsType("Day");
            } else {
                bondIns.setInsType(sType);
                beforeBondIns.setInsType(sType);
            }
        }
        //------------------------若有分析代码，则加入。sj edit 20080630--------------
        if (bondIns != null && analys1 != null && analys1.length() > 0) {
            bondIns.setAnalysisCode1(analys1);
            beforeBondIns.setAnalysisCode1(analys1);
        }
        if (bondIns != null && analys2 != null && analys2.length() > 0) {
            bondIns.setAnalysisCode2(analys2);
            beforeBondIns.setAnalysisCode2(analys2);
        }
        //-------------------------------------------------------------------------
        if (bondIns != null) {
            operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);
            //---add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(BondAssist.hmSec != null && BondAssist.hmSec.get(SecurityCode) != null){
            	FixInterestBean  fixIt= (FixInterestBean)BondAssist.hmSec.get(SecurityCode);
            	String beanId = fixIt.getBeanId();
                bondOper = (BaseBondOper) pub.getOperDealCtx().getBean(beanId);
            }else{
            	//---add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
                bondOper = operDeal.getSpringRe(SecurityCode,
                        (sType.length() > 0 ? sType : "Day")); //生成BaseBondOper
            }//add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001

            if (bondOper == null) {
                throw new YssException(
                    "请设置【基础参数模块的Spring调用】，引用BeanId：BondInsCfgFormula");
            }
            bondIns.setSecurityCode(SecurityCode);
            bondIns.setInsDate(dDate);
            //modify by zhangfa 20100915 MS01724    国内银行间债券计提利息后，百元利息没有显示。    QDV4交银施罗德2010年9月10日01_B    
            bondIns.setPortCode(portCode);
            //--------------------------------------------------------------------------------------------------------------
            bondIns.setHolidaysCode(FHolidaysCode);
            bondIns.setAttrClsCode(sAttrClsCode);
            //-------增加了报价因子的获取.sj edit 20080826 ---//
            bondIns.setFactor(dFactor);
            //---------------------------------------------//
            bondIns.setIsBeforeRate(false);//设置计算税后利息的标志
            //add by songjie 2010.03.10 MS00909 QDII4.1赢时胜上海2010年03月16日01_B
            bondIns.setInsAmount(1);//设置成交数量为1
            //add by songjie 2010.03.27
            bondIns.setIsRate100(true);//表示获取没百元债券利息的公式
            
            //--- add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(BondAssist.hmSec != null && BondAssist.hmSec.get(SecurityCode) != null){
            	FixInterestBean fixIt = (FixInterestBean)BondAssist.hmSec.get(SecurityCode);
            	bondIns.setFaceValue(fixIt.getStrFaceValue());
            }
            //--- add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            
            bondOper.setFromDomestic(true);//设置从国内接口导入调用的标志
            bondOper.setPortCode(portCode);
            bondOper.setYssPub(pub);
            bondOper.init(bondIns);
            //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
            if(exchangeCode.equalsIgnoreCase(YssOperCons.YSS_JYSDM_YHJ)){
            	bondOper.setFromBank(true);
            }
            //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
            reVal = bondOper.calBondInterest();//计算税后百元债券利息
            
            operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);
            //--- delete by songjie 2013.03.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
//            bondOper = operDeal.getSpringRe(SecurityCode,
//                                            (sType.length() > 0 ? sType : "Day")); //生成BaseBondOper
//            if (bondOper == null) {
//                throw new YssException(
//                    "请设置【基础参数模块的Spring调用】，引用BeanId：BondInsCfgFormula");
//
//            }
            //--- delete by songjie 2013.03.28 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            
            beforeBondIns.setSecurityCode(SecurityCode);
            beforeBondIns.setInsDate(dDate);
            //modify by zhangfa 20100915 MS01724    国内银行间债券计提利息后，百元利息没有显示。    QDV4交银施罗德2010年9月10日01_B    
            beforeBondIns.setPortCode(portCode);
            //---------------------------------------------------------------------------------------------------------------
            beforeBondIns.setHolidaysCode(FHolidaysCode);
            beforeBondIns.setAttrClsCode(sAttrClsCode);
            //-------增加了报价因子的获取.sj edit 20080826 ---//
            beforeBondIns.setFactor(dFactor);
            //---------------------------------------------//
            beforeBondIns.setIsBeforeRate(true);//设置计算税前利息的标志
            //add by songjie 2010.03.10 MS00909 QDII4.1赢时胜上海2010年03月16日01_B
            beforeBondIns.setInsAmount(1);//设置成交数量为1
            //add by songjie 2010.03.27
            beforeBondIns.setIsRate100(true);//表示获取没百元债券利息的公式
            
            //--- add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(BondAssist.hmSec != null && BondAssist.hmSec.get(SecurityCode) != null){
            	FixInterestBean  fixIt= (FixInterestBean)BondAssist.hmSec.get(SecurityCode);
            	beforeBondIns.setFaceValue(fixIt.getStrFaceValue());
            }
            //--- add by songjie 2013.03.29 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            
            bondOper.setFromDomestic(true);//设置从国内接口导入调用的标志
            bondOper.setPortCode(portCode);
            bondOper.setYssPub(pub);
            bondOper.init(beforeBondIns);
            //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
            if(exchangeCode.equalsIgnoreCase(YssOperCons.YSS_JYSDM_YHJ)){
            	bondOper.setFromBank(true);
            }
            //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
            beforeVal = bondOper.calBondInterest();//计算税前百元债券利息
        }
        hmRate.put("before", String.valueOf(beforeVal));
        hmRate.put("after", String.valueOf(reVal));
        
        return hmRate;
    }
    
//-----------------------------------------------------------------------------------------------------------------------
    /**
     * 为了在外部计息时获取数量
     * sj 080117
     * @param dDate Date
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @throws YssException
     * @return double
     */
    private double getAmount(java.util.Date dDate, boolean analy1,
                             boolean analy2, boolean analy3) throws YssException {
        String strSql = "";
        double reAmount = 0.0;
        ResultSet rs = null;
        try {
            strSql = "select distinct FPortCode, FSecurityCode " +
                " ,FStorageAmount " +
                (analy1 ? ",FAnalysisCode1" : " ") +
                (analy2 ? ",FAnalysisCode2" : " ") +
                (analy3 ? ",FAnalysisCode3" : " ") +
                " from " +
                //--------------------------------------------------------------------------------//库存
                pub.yssGetTableName("Tb_Stock_Security") +
                //--------------------------------------------------------------------------------
                " where FCheckState = 1 and FPortCode in (" +
                operSql.sqlCodes(portCodes) +
                ") and FStorageDate = " + dbl.sqlDate(dDate);
            rs = dbl.openResultSet(strSql); 
            if (rs.next()) {
                reAmount = rs.getDouble("FStorageAmount");
            }
            return reAmount;
        } catch (Exception e) {
            throw new YssException("系统债券通过外部计息方式时获取库存出现异常!\n", e); // by 曹丞 2009.01.23 债券通过外部计息方式时获取库存异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            //return reAmount; //如果此处有返回值的话，抛出的异常将被忽略，一次屏蔽 sunkey 20090204 QDV4.1-BugNO:MS00004 指示信息的解析处理
            //之前游标没有关闭，修改关闭游标 sunkey 20090204 QDV4.1-BugNO:MS00004 指示信息的解析处理
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 设置小数位。sj edit 20080702
     * @param roundCode String
     * @param securityCode String
     * @param dInsMoney double
     * @throws YssException
     * @return double
     */
    public double setDigit(String roundCode, String securityCode,
                           double dInsMoney, boolean bCheck) throws YssException { //MS00127 QDV4赢时胜上海2008年12月25日03_B 增加布尔值判断小数位数 leeyu 2009-1-24
        //-----------------------根据在债券信息设置里的舍入方式设置来确定小数保留位数.sj edit 20080702
        if (bCheck) { //根据所传参数判断，如果为真就执行
            if (roundCode == null ||
                roundCode.trim().length() == 0) { //修改债券舍入设置的判断逻辑，在接口中读入的信息中舍入设置为空字符。sj edit 200801112
                throw new YssException("债券编号【" + securityCode + "】没有设置舍入方式，请检查！");
            }
            dInsMoney = this.getSettingOper().reckonRoundMoney(roundCode, dInsMoney);
        }
        return dInsMoney;
    }

    /**
     * 计算每日摊销的溢折价
     * MS00656 QDV4赢时胜(上海)2009年8月24日01_A
     * @param secIns SecPecPayBean：每日利息
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @return SecPecPayBean
     * @throws YssException
     */
    private SecPecPayBean calPADAmortization(SecPecPayBean secIns,
                                             ArrayList alIntegr,
                                             boolean analy1,
                                             boolean analy2,
                                             boolean analy3) throws YssException{
        ResultSet rs = null;
        SecPecPayBean secAmorti = null;
        String strSql = "";
        String sKey = "";
        SecurityStorageBean storage = null;
        SecIntegratedBean integrBean = new SecIntegratedBean();
        try {
            if(hmSecStg == null){
                hmSecStg = new HashMap();
                strSql = "SELECT FSecurityCode, FStorageDate, FPortCode, FCatType, FAttrClsCode, FInvestType, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FEffectiveRate, FStorageCost, FStorageAmount" +
                    " FROM " + pub.yssGetTableName("TB_Stock_Security") +
                    " WHERE FYearMonth <> " + dbl.sqlString(YssFun.getYear(secIns.getTransDate()) + "00") +
                    " AND FStorageDate = " + dbl.sqlDate(secIns.getTransDate()) +
                    " AND FPortCode = " + dbl.sqlString(secIns.getStrPortCode());

                rs = dbl.openResultSet(strSql);
                while(rs.next()){
                    sKey = rs.getString("FSecurityCode") + "\f" +
                        rs.getString("FPortCode") +
                        (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                        (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                        (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                        rs.getString("FAttrClsCode") + "\f" +
                        rs.getString("FInvestType");
                    storage = new SecurityStorageBean();
                    storage.setStrStorageAmount(rs.getDouble("FStorageAmount") + "");
                    storage.setEffectiveRate(rs.getDouble("FEffectiveRate"));
                    storage.setStrStorageCost(rs.getDouble("FStorageCost") + "");
                    hmSecStg.put(sKey, storage);
                }
            }

            sKey = secIns.getStrSecurityCode() + "\f" +
                secIns.getStrPortCode() +
                (analy1 ? "\f" + secIns.getInvMgrCode() : "") +
                (analy2 ? "\f" + secIns.getBrokerCode() : "") + "\f" +
                secIns.getAttrClsCode() + "\f" +
                secIns.getInvestType();

            storage = (SecurityStorageBean)hmSecStg.get(sKey);
            //-------- add by wangzuochun 2010.06.17  MS01309    计提报错    QDV4赢时胜(测试)2010年06月17日01_B  
            if (storage == null){
            	secAmorti = new SecPecPayBean();
            	secAmorti.setTransDate(secIns.getTransDate());
            	return secAmorti;
            }
            //------   MS01309    计提报错    QDV4赢时胜(测试)2010年06月17日01_B    ------//
            secAmorti = secIns.deepCopy();
            secAmorti.setInOutType(-1);
            secAmorti.setStrTsfTypeCode("05");
            secAmorti.setStrSubTsfTypeCode("05FIDI");
            secAmorti.setMoney(
                YssD.mul(
                    YssD.sub(
                        YssD.round(
                            YssD.mul(
                                YssFun.toDouble(storage.getStrStorageCost()),
                                storage.getEffectiveRate()),
                            2),
                        secIns.getMoney()),
                -1));
            secAmorti.setBaseCuryMoney(this.getSettingOper().calBaseMoney(secAmorti.getMoney(),
                secAmorti.getBaseCuryRate(), 2));
            secAmorti.setPortCuryMoney(this.getSettingOper().calPortMoney(secAmorti.getMoney(),
                secAmorti.getBaseCuryRate(), secAmorti.getPortCuryRate(),
                secAmorti.getStrCuryCode(), secAmorti.getTransDate(), secAmorti.getStrPortCode(), 2)
                );
            secAmorti.setMMoney(secAmorti.getMoney());
            secAmorti.setVMoney(secAmorti.getMoney());
            secAmorti.setMBaseCuryMoney(secAmorti.getBaseCuryMoney());
            secAmorti.setVBaseCuryMoney(secAmorti.getBaseCuryMoney());
            secAmorti.setMPortCuryMoney(secAmorti.getPortCuryMoney());
            secAmorti.setVPortCuryMoney(secAmorti.getPortCuryMoney());

            //-----------------冲减证券摊余成本-------------------//
            integrBean.setIInOutType( -1);
            integrBean.setInvestType(secAmorti.getInvestType());
            integrBean.setSSecurityCode(secAmorti.getStrSecurityCode());
            integrBean.setSExchangeDate(YssFun.formatDate(secAmorti.getTransDate(), "yyyy-MM-dd"));
            integrBean.setSOperDate(YssFun.formatDate(secAmorti.getTransDate(), "yyyy-MM-dd"));
            integrBean.setSRelaNum(" ");
            integrBean.setSNumType(" ");

            integrBean.setSTradeTypeCode(YssOperCons.YSS_JYLX_PADAMORT);

            integrBean.setSPortCode(secAmorti.getStrPortCode());
            if (analy1) {
                integrBean.setSAnalysisCode1(secAmorti.getInvMgrCode());
            } else {
                integrBean.setSAnalysisCode1(" ");
            }
            if (analy2) {
                integrBean.setSAnalysisCode2(secAmorti.getBrokerCode());
            } else {
                integrBean.setSAnalysisCode2(" ");
            }
            integrBean.setSAnalysisCode3(" ");

            integrBean.setAttrClsCode(secAmorti.getAttrClsCode());

            integrBean.setDAmount(0);

            integrBean.setDCost(YssD.mul(secAmorti.getMoney(), integrBean.getIInOutType()));
            integrBean.setDMCost(integrBean.getDCost());
            integrBean.setDVCost(integrBean.getDCost());

            integrBean.setDBaseCuryRate(secAmorti.getBaseCuryRate());

            integrBean.setDBaseCost(YssD.mul(secAmorti.getBaseCuryMoney(), integrBean.getIInOutType()));
            integrBean.setDMBaseCost(integrBean.getDBaseCost());
            integrBean.setDVBaseCost(integrBean.getDBaseCost());

            integrBean.setDPortCuryRate(secAmorti.getPortCuryRate());

            integrBean.setDPortCost(YssD.mul(secAmorti.getPortCuryMoney(), integrBean.getIInOutType()));
            integrBean.setDMPortCost(integrBean.getDPortCost());
            integrBean.setDVPortCost(integrBean.getDPortCost());

            integrBean.checkStateId = 1;
            integrBean.setSTsfTypeCode("05");
            integrBean.setSSubTsfTypeCode("05FIDI");

            alIntegr.add(integrBean);
            //----------------------------------------------//
        } catch (Exception ex) {
            throw new YssException("计算每日摊销溢折价出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
        return secAmorti;
    }

    /**
     * 计算摊销数据。
     * @param pays SecPecPayBean
     * @param rs ResultSet
     * @throws YssException
     * @return SecPecPayBean
     */
    private SecPecPayBean setAmortization(SecPecPayBean pays, ResultSet rs, boolean isNewType) throws YssException { //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14
        double dInsMoney = 0; //利息金额
        SecPecPayBean pay = null;
        try {
            if (YssFun.dateDiff(pays.getTransDate(), rs.getDate("FINSENDDATE")) <
                0) {
                return null;
            }
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            boolean analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            pay = new SecPecPayBean();
            pay.setTransDate(pays.getTransDate());
            pay.setStrPortCode(pays.getStrPortCode());
            pay.setInvMgrCode(pays.getInvMgrCode());
            pay.setBrokerCode(pays.getBrokerCode());
            pay.setStrSecurityCode(pays.getStrSecurityCode());
            pay.setStrCuryCode(pays.getStrCuryCode());
            if (rs.getDouble("FIssuePrice") > rs.getDouble("FFaceValue")) { //若发行价格>面值
                pay.setStrTsfTypeCode(YssOperCons.Yss_ZJDBLX_Premium);
                pay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_Premium_Fix);
                pay.setInOutType(1);
            } else if (rs.getDouble("FIssuePrice") < rs.getDouble("FFaceValue")) { //若发行价格<面值
                pay.setStrTsfTypeCode(YssOperCons.Yss_ZJDBLX_Discounts);
                pay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_Discounts_Fix);
                pay.setInOutType(1);
            } else {
                pay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                pay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FI_RecInterest);
            }
            pay.setBaseCuryRate(pays.getBaseCuryRate());
            pay.setPortCuryRate(pays.getPortCuryRate());
            if (rs.getInt("FInterestOrigin") == 0) {
                dInsMoney = innerCal(rs.getString("FSecurityCode"),
                                     rs.getString("FPeriodCode"),
                                     rs.getString("FCalcInsMeticDay")
                                     , rs.getString("FPortCode"),
                                     rs.getString("FHolidaysCode"),
                                     pays.getTransDate(),
                                     rs.getDouble("FQFactor"),
                                     pays.getInvMgrCode(),
                                     pays.getBrokerCode(),
                                     "Premium",
                                     pays.getAttrClsCode(),//溢价与折价  2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                                     rs.getDouble("FCPIPRICE"),//add by yanghaiming 20110212 #461 增加浮动CPI
                                     //add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 增加投资类型
                                     rs.getString("FInvestType"));
                dInsMoney = setDigit(rs.getString("FRoundCode"),
                                     rs.getString("FHSecurityCode"), dInsMoney, isNewType); //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14
                pay.setMoney(dInsMoney);
            } else if (rs.getInt("FInterestOrigin") == 1) {
                pay.setAmount(getAmount(pays.getTransDate(), analy1, analy2, analy3));
                dInsMoney = outerCal(rs.getDouble("FYInAccPer100"),
                                     rs.getDouble("FYesIntAccPer100")
                                     , rs.getDouble("FQFactor"), pays.getAmount());
                dInsMoney = setDigit(rs.getString("FRoundCode"),
                                     rs.getString("FHSecurityCode"), dInsMoney, isNewType); //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14
                pay.setMoney(dInsMoney);
            } else if (rs.getInt("FInterestOrigin") == 3) {
                dInsMoney = outerCal(rs.getDouble("FYIntAccPer100"),
                                     rs.getDouble("FYesIntAccPer100")
                                     , rs.getDouble("FFactor"), pays.getAmount());
                if (dInsMoney == 0.0) {
                    dInsMoney = innerCal(rs.getString("FSecurityCode"),
                                         rs.getString("FPeriodCode"),
                                         rs.getString("FCalcInsMeticDay")
                                         , rs.getString("FPortCode"),
                                         rs.getString("FHolidaysCode"),
                                         pays.getTransDate(),
                                         rs.getDouble("FQFactor"),
                                         analy1 ? rs.getString("FAnalysisCode1") :
                                         "",
                                         analy2 ? rs.getString("FAnalysisCode2") :
                                         "",
                                         pay.getAttrClsCode(),//2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                                         rs.getDouble("FCPIPRICE"),//add by yanghaiming 20110212 #461 增加浮动CPI
                                         rs.getString("FInvestType"));//add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 增加投资类型
                }
                dInsMoney = setDigit(rs.getString("FRoundCode"),
                                     rs.getString("FHSecurityCode"), dInsMoney, isNewType); //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14
                pay.setMoney(dInsMoney);
            } else if (rs.getInt("FInterestOrigin") == 4) {
                dInsMoney = innerCal(rs.getString("FSecurityCode"),
                                     rs.getString("FPeriodCode"),
                                     rs.getString("FCalcInsMeticDay")
                                     , rs.getString("FPortCode"),
                                     rs.getString("FHolidaysCode"),
                                     pays.getTransDate(),
                                     rs.getDouble("FQFactor"),
                                     analy1 ? rs.getString("FAnalysisCode1") :
                                     "",
                                     analy2 ? rs.getString("FAnalysisCode2") :
                                     "",
                                     rs.getString("FAttrClsCode"),
                                     rs.getDouble("FCPIPRICE"), //add by yanghaiming 20110212 #461 增加浮动CPI
                                     rs.getString("FInvestType"));//add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 增加投资类型
                if (dInsMoney == 0.0) {
                    dInsMoney = outerCal(rs.getDouble("FYIntAccPer100"),
                                         rs.getDouble("FYesIntAccPer100")
                                         , rs.getDouble("FFactor"), pays.getAmount());
                }
                dInsMoney = setDigit(rs.getString("FRoundCode"), rs.getString("FHSecurityCode"), dInsMoney, isNewType); //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14
                pay.setMoney(dInsMoney);
            }
            pay.setBaseCuryMoney(setDigit(rs.getString("FRoundCode"),
                                          rs.getString("FHSecurityCode"),
                                          YssD.mul(pay.getMoney(),
                //                  pays.getBaseCuryRate())));
                pays.getBaseCuryRate()), isNewType)); //MS00127 QDV4赢时胜上海2008年12月25日03_B 国内债券数据不用手动设置舍入位数 byleeyu 2009-1-14

            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 使用通用的计算组合金额的方法--------------------------
            pay.setPortCuryMoney(setDigit(rs.getString("FRoundCode"),
                                          rs.getString("FHSecurityCode"),
                                          this.getSettingOper().calPortMoney(
                                              pay.getMoney(),
                                              pay.getBaseCuryRate(), pay.getPortCuryRate(),
                                              rs.getString("FCuryCode"), pays.getTransDate(), pay.getStrPortCode()), isNewType));
            //-----------------------------------------------------------------------------------------------------------
            pay.setMMoney(pay.getMoney());
            pay.setVMoney(pay.getMoney());
            pay.setMBaseCuryMoney(pay.getBaseCuryMoney());
            pay.setVBaseCuryMoney(pay.getBaseCuryMoney());
            pay.setMPortCuryMoney(pay.getPortCuryMoney());
            pay.setVPortCuryMoney(pay.getPortCuryMoney());
            pay.setAttrClsCode(YssOperCons.Yss_JYLX_CYDQ);
        } catch (Exception ex) {
            throw new YssException("计算摊销数据出错!\n", ex);
        }
        return pay;
    }

    /**
     * 查询证券应收应付关联与综合业务的数据
     * @return String 返回查询的交易编号
     * @throws YssException 异常
     * add by xuqiji 20090505:QDV4赢时胜（上海）2009年4月30日02_B MS00429    通过综合业务中的证券成本进行业务操作后产生的数据信息与数据金额均有误 ---
     */
    private String getExitNum() throws YssException {
        ResultSet rs = null;
        String exitNum = "";
        StringBuffer buff = null;
        try {
            buff = new StringBuffer();
//         buff.append("select FNum from ");
//         buff.append(pub.yssGetTableName("tb_data_secrecpay "));
//         buff.append(" a where exists ( select 'X' from ");
//         buff.append(pub.yssGetTableName("tb_data_integrated"));
//         buff.append(" b where a.FNum = b.FRelaNum and FNumType = 'SecRecPay' and FCheckState = 1 ) ");
//         buff.append("and FCheckState = 1 and FTsfTypeCode = '06'and FSubTsfTypeCode = '06FI' ");
//         buff.append("and FTransDate  between ");
//         buff.append(dbl.sqlDate(beginDate));
//         buff.append(" and ");
//         buff.append(dbl.sqlDate(endDate));
         //优化代码执行速度  by leeyu 20100326 QDV4中保2010年03月03日03_A MS1011
	     	buff.append("select a.FNum from ");
	        buff.append(pub.yssGetTableName("Tb_Data_Secrecpay"));
	        buff.append(" a ,");
	        buff.append(pub.yssGetTableName("Tb_Data_Integrated"));
	        buff.append(" b ");
	        buff.append(" where  a.FNum = b.FRelaNum and b.FNumType = 'SecRecPay' ");
	        buff.append(" and a.FCheckState = 1  and a.FTsfTypeCode = '06' ");
	        buff.append(" and a.FSubTsfTypeCode = '06FI' ");
	        buff.append(" and a.FTransDate between ").append(dbl.sqlDate(beginDate));
            buff.append(" and ");
            buff.append(dbl.sqlDate(endDate));
            rs = dbl.queryByPreparedStatement(buff.toString()); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                exitNum += rs.getString("FNum") + ",";
            }
        } catch (Exception e) {
            throw new YssException("查询证券应收应付关联与综合业务的数据出错！\r\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return exitNum;
    }
	/**
    * 删除本日计过的债券利息 合并太平版本代码
    * QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
    * @throws YssException
    */
   private void deleteSecPay(java.util.Date dDate,String security,String portCode) throws YssException{
	   Connection conn =null;
	   boolean bTrans =false;
	   String sql ="";
	   try{
		   conn =dbl.loadConnection();
		   conn.setAutoCommit(bTrans);
		   bTrans =true;
		   sql ="delete from "+pub.yssGetTableName("Tb_Data_secrecpay")+
		        //edit by songjie 2012.04.06 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B 添加 FDataOrigin = 0 自删除自动生成的数据
		   		" where FDataOrigin = 0 and FSecurityCode in(" + operSql.sqlCodes(security)
					+ ") and FTransDate=" + dbl.sqlDate(dDate)
					+ " and FPortCode in(" + operSql.sqlCodes(portCode)
					+ ")  and FTsfTypeCode='06' and FSubTsfTypeCode='06FI' "
					+ " and FNum not in(select FRelaNum from "
					+ pub.yssGetTableName("Tb_Data_Integrated")
					+ " where FNum in(select  FNum from "
					+ pub.yssGetTableName("Tb_Data_Integrated")
					+ " where FSecurityCode in(" + operSql.sqlCodes(security)
					+ ") and FOperDate=" + dbl.sqlDate(dDate)//改为业务日
					+ " and FPortCode in(" + operSql.sqlCodes(portCode)
					+ ") and FTradeTypeCode in('81')) "
					+ " and FNumType='SecRecPay') ";
		   dbl.executeSql(sql);
		   conn.commit();
		   conn.setAutoCommit(bTrans);
		   bTrans =false;
	   }catch(Exception ex){
		   throw new YssException("删除当日的债券利息出错！",ex);
	   }finally{
		   dbl.endTransFinal(conn, bTrans);
	   }
   }
   public void afterIncomes() throws YssException{

   }

   private  SecPecPayBean getInterTax(String strInterTaxCode,SecPecPayBean pay) throws  YssException{
   	
	   SecPecPayBean InterestTax = null;
   	double dInterestTax =0;
   	  int iDays = 0;
   	try{
   		InterestTax = (SecPecPayBean)pay.clone();
   		dInterestTax =  this.getSettingOper().calMoneyByPerExp(pay.getTransDate(),strInterTaxCode, pay.getMoney());		
   		InterestTax.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
   		InterestTax.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_LXS_FI);
   		InterestTax.setMoney(YssFun.roundIt(dInterestTax, 2)); 
   		InterestTax.setBaseCuryMoney(this.getSettingOper().calBaseMoney(InterestTax. getMoney(), InterestTax.getBaseCuryRate())); 
   		InterestTax.setPortCuryMoney(this.getSettingOper().calPortMoney(InterestTax.getMoney(), InterestTax.getBaseCuryRate(),
        InterestTax.getPortCuryRate(),
        InterestTax.getStrCuryCode(),
        InterestTax.getTransDate(),
        InterestTax.getStrPortCode()));
   		InterestTax.setMMoney(InterestTax.getMoney());
   		InterestTax.setVMoney(InterestTax.getMoney());
   		InterestTax.setMBaseCuryMoney(InterestTax.getBaseCuryMoney());
   		InterestTax.setVBaseCuryMoney(InterestTax.getBaseCuryMoney());
   		InterestTax.setMPortCuryMoney(InterestTax.getPortCuryMoney());
   		InterestTax.setVPortCuryMoney(InterestTax.getPortCuryMoney());
   		
   		return InterestTax;
   	}catch(Exception e){
   		throw new YssException("计提债券利息税出错......");
   	}
   }
   
   
   /**
    * add by zhouwei 20120309 
    * 根据利息税的余额，来轧差求出利息税
 * @param strInterTaxCode
 * @param pay
 * @return
 * @throws YssException
 */
	private  SecPecPayBean getInterTaxByBalance(String strInterTaxCode,SecPecPayBean pay) throws  YssException{
			ResultSet rs=null;
		    SecPecPayBean InterestTax = null;
	   		double dInterestTax =0;
	   		double balanceInterest=0;//前一天剩余利息
	   		double balanceInterestTax=0;//前一天剩余利息税
	   		String strSql="";
	   		boolean analy1;
	        boolean analy2;
	        double tradeInterest=0;
	   	try{
	   		analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
//            tradeInterest=getFIInsAllTrade(pay);
            //实现思路：当日应收利息(直接取库存)*利息税率-昨日利息税余额	hukun	20120322
            strSql="select a.FSecurityCode,a.FStorageDate,nvl(a.FBal,0) as bonInter,Nvl(b.FBal,0) as bondInterTax from "
	   		   +pub.yssGetTableName("Tb_Stock_SecRecPay")+" a  left join (select * from "+pub.yssGetTableName("Tb_Stock_SecRecPay")
	   		   +" where FTsfTypeCode="+dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay)+" and FSubTsfTypeCode="+dbl.sqlString(YssOperCons.YSS_ZJDBZLX_LXS_FI)
	   		   +" and FStorageDate="+dbl.sqlDate(YssFun.addDay(pay.getTransDate(),-1))
	   		   +" ) b on a.fportcode=b.fportcode and a.FSecurityCode=b.FSecurityCode"
	   		   +" and a.FAttrClsCode=b.FAttrClsCode  and a.FAnalysisCode1=b.FAnalysisCode1 and a.FAnalysisCode1=b.FAnalysisCode1"
	   		   +" and a.FAnalysisCode2=b.FAnalysisCode2"
	   		   +" where a.FStorageDate="+dbl.sqlDate(pay.getTransDate())
	   		   +" and a.fportcode="+dbl.sqlString(pay.getStrPortCode())+" and a.FSecurityCode="+dbl.sqlString(pay.getStrSecurityCode())
	   		   +" and a.FAttrClsCode="+dbl.sqlString(pay.getAttrClsCode().equals("")?" ":pay.getAttrClsCode())
	   		   +" and a.FTsfTypeCode="+dbl.sqlString(pay.getStrTsfTypeCode())+" and a.FSubTsfTypeCode="+dbl.sqlString(pay.getStrSubTsfTypeCode());
	   		if(analy1){
                strSql = strSql + " AND a.FAnalysisCode1 = " + dbl.sqlString(pay.getInvMgrCode().equals("")?" ":pay.getInvMgrCode());
            }
            if(analy2){
                strSql = strSql + " AND a.FAnalysisCode2 = " + dbl.sqlString(pay.getBrokerCode().equals("")?" ":pay.getBrokerCode());
            }
	   		rs=dbl.openResultSet(strSql);
	   		while(rs.next()){
	   			balanceInterest=rs.getDouble("bonInter");
	   			balanceInterestTax=rs.getDouble("bondInterTax");
	   		}		
	   		InterestTax = (SecPecPayBean)pay.clone();
	   		dInterestTax =  this.getSettingOper().calMoneyByPerExp(pay.getTransDate(),strInterTaxCode, YssD.add(balanceInterest, InterestTax.getMoney()));		
	   		InterestTax.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
	   		InterestTax.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_LXS_FI);
	   		InterestTax.setMoney(YssFun.roundIt(YssD.sub(dInterestTax,balanceInterestTax), 2)); 
	   		InterestTax.setBaseCuryMoney(this.getSettingOper().calBaseMoney(InterestTax. getMoney(), InterestTax.getBaseCuryRate())); 
	   		InterestTax.setPortCuryMoney(this.getSettingOper().calPortMoney(InterestTax.getMoney(), InterestTax.getBaseCuryRate(),
	        InterestTax.getPortCuryRate(),
	        InterestTax.getStrCuryCode(),
	        InterestTax.getTransDate(),
	        InterestTax.getStrPortCode()));
	   		InterestTax.setMMoney(InterestTax.getMoney());
	   		InterestTax.setVMoney(InterestTax.getMoney());
	   		InterestTax.setMBaseCuryMoney(InterestTax.getBaseCuryMoney());
	   		InterestTax.setVBaseCuryMoney(InterestTax.getBaseCuryMoney());
	   		InterestTax.setMPortCuryMoney(InterestTax.getPortCuryMoney());
	   		InterestTax.setVPortCuryMoney(InterestTax.getPortCuryMoney());	   		
	   		return InterestTax;
	   	}catch(Exception e){
	   		throw new YssException("计提债券利息税出错!",e);
	   	}finally{
            dbl.closeResultSetFinal(rs);
        }
	  }
	 /**add by zhouwei 20120309 获取所有交易数据的应收利息
	 * @param pay
	 * @return
	 * @throws YssException
	 */
	private double getFIInsAllTrade(SecPecPayBean pay) throws YssException{
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
	                " WHERE FTransDate = " + dbl.sqlDate(pay.getTransDate()) +
	                " AND FCheckState = 1" +
	                " AND FSubTsfTypeCode IN ('06FI_B', '02FI_B')" +
	                " AND FSecurityCode = " + dbl.sqlString(pay.getStrSecurityCode()) +
	                " AND FPortCode = " + dbl.sqlString(pay.getStrPortCode()) +
	                " AND FAttrClsCode = " + dbl.sqlString(pay.getAttrClsCode());
	            if(analy1){
	                strSql = strSql + " AND FAnalysisCode1 = " + dbl.sqlString(pay.getInvMgrCode().equals("")?" ":pay.getInvMgrCode());
	            }
	            if(analy2){
	                strSql = strSql + " AND FAnalysisCode2 = " + dbl.sqlString(pay.getBrokerCode().equals("")?" ":pay.getBrokerCode());
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
}
