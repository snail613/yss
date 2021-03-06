package com.yss.main.report;

import java.math.BigDecimal;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;


import org.apache.log4j.Logger;

import com.sun.xml.ws.client.BindingProviderProperties;
import com.yss.ciss.ws.client.ciss.CISSServiceForQDII;
import com.yss.ciss.ws.client.ciss.ComparableStatusVO;
import com.yss.ciss.ws.service.client.ResponseMsg;
import com.yss.dsub.*;
import com.yss.log.DayFinishLogBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*; //QDV4海富通2008年12月31日03_B MS00176
import com.yss.main.funsetting.*; //调用这个类取节假日群的方法 BUG：0000517
import com.yss.main.operdata.AppCheck;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.operdeal.report.navrep.CtlNavRep;
import com.yss.main.operdeal.report.repfix.*;
import com.yss.main.parasetting.*;
import com.yss.main.syssetting.RightBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.util.*;
import com.yss.vsub.*;
import com.yss.webServices.AccountClinkage.services.ExchangeDate;

public class GuessValue extends BaseBean implements IGuessValueReport,
		IYssConvert {
	java.util.Date StartDate;
	int AccountingYear;
	int AccountingPeriod = 1;
	String YssTabPrefix = "";
	String AccLen = "";
	int iDayRealy = 0; // 日终处理
	private String sPortCode = "";
	private double dAmount = 0; // 存储份额，用来计算基础货币单位净值 20081222 BugNO:MS00083
	// 2009-11-03 蒋锦 添加 sGVType MS00004 QDV4.1赢时胜（上海）2009年9月28日03_A
	private String sGVTableName = "TB_Rep_GuessValue";// 估值表名称
	boolean bIsETFGV = false;// 是否为ETF估值表 如果生成 ETF 估值表数据插入 TB_001_ETF_GUESSVALUE
								// 中， 否则数据插入 TB_001_Rep_GUESSVALUE 中，ETF
								// 估值表不统计本期间证券交易方式为 ETFBS 的凭证
	
	String sIsIncludeModifyingData = "false";		//20120526 added by liubo.Story #2452.创建估值表时是否包含市值修改数据

	//---add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
	public String logSumCode = "";//汇总日志编号
    public SingleLogOper logOper;//日志实例
	//---add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
    
	public GuessValue() {
	}

	public void createGuessValue(java.util.Date date, int fSetCode)
			throws YssException {
		String sql = null;
		ResultSet rs = null;
		
		ResultSet rsModifying = null;
		
		String PortCode = "";
		String sPortCury = "";
		boolean isCommit = false;
		double totalMarketValue = 0;
		// double totalCost = 0;
		CtlPubPara pubpara = null;
		boolean bIsRound = false;
		boolean bMVIsRound2 = false;
		// ---MS00348 QDV4招商证券2009年04月01日01_B 。以实现以录入期初数的方式成立的资产的估值。
		int intMonth; // 调整月份
		int intStartMonth = 0; // 启用月份
		boolean bShowYSX = false; // 显示财务估值表中已实现部分的数据 QDV4海富通2009年05月11日01_AB
									// MS00439 by leeyu 20090519
		boolean bShowGz = false;// MS01160 QDV4招商基金2010年5月6日01_A
								// 财务估值表增加一栏显示所有的估值增值余额 add by jiangshichao
								// 2010.05.25
		// -----------------------------------------------------------------------------
		// --- MS00621 QDV4中金2009年08月06日01_B sj ----------------------------
		PortfolioBean port = null; // 组合信息设置
		// panjunfang add 20100731 QDV4交银施罗德2010年07月30日01_B----------
		String strDigits = "";
		//add by yeshenghong 20111108 STORY#1562 不显示减值准备
	    String CboShowDec = "CboShowDec";
	    String showDec = "0"; // 0为显示 1 不显示
	    // end  to control 减值准备
	  //不显示减值准备相关科目  yeshenghong #1652
		int iDigits = 15;// 汇率参与市值计算时小数保留位数，默认与原有一致即保留15位
		// ----------------------------------------------------------------------
//		String query = "select fvarvalue from lvarlist where fvarname = '凭证录入需要审核'";
//	    String vchEntryStr = "0";// modified by  yeshenghong 20120214 story 1992
        PreparedStatement pst=null;
		try {
//			rs = dbl.openResultSet(query);
//	    	if(rs.next())
//	    	{
//	    		vchEntryStr = rs.getString("fvarvalue");
//	    	}
//	    	dbl.closeResultSetFinal(rs); 
			// ------MS00402 QDV4海富通2009年04月21日01_AB -----
			judgeCanGreateGuess(date, fSetCode); // 判断是否能够生成财务估值表
			// ------------------------------------------------
			// 流程控制设置执行的组合代码 2009.04.17 蒋锦 添加 MS00003
			// 判断是否在组合中执行
			if (pub.getFlow() != null
					&& pub.getFlow().keySet().contains(pub.getUserCode())) {
				((FlowBean) pub.getFlow().get(pub.getUserCode()))
						.setFPortCodes(sPortCode);
			}
			// --------------2008.09.04 蒋锦 添加
			// 从通用参数获取计算市值时是否四舍五入两位小数---------------//
			CtlPubPara pubPara = new CtlPubPara();
			pubPara.setYssPub(pub);
			bIsRound = pubPara.getMVIsRound();
			bMVIsRound2 = pubPara.getMValueRoundOfCalFX();// 20110112 panjunfang
															// add
															// 从通用参数获取计算汇兑损益时，市值是否中间不保留位数最终保留两位小数
			// --------20100731 panjunfang add QDV4交银施罗德2010年07月30日01_B
			// 汇率是否保留13位小数参与市值计算----------------------------------------------------------------------//
			CtlPubPara pubParaRate = new CtlPubPara();
			pubParaRate.setYssPub(pub);
			strDigits = pubParaRate.getExactGuessValue(getPortCode(fSetCode
					+ "", YssFun.formatDate(date, "yyyy")));
			if (strDigits.length() > 0 && strDigits.equals("1")) {// 20100731
																	// panjunfang
																	// modify
																	// QDV4交银施罗德2010年07月30日01_B
				iDigits = 13;// 通过通用参数判断 汇率是否保留13位小数参与市值计算
			}
			// -----------------------------通过通用参数的设置来判断停牌状态的方式。将原有的取停牌信息放在这里取
			// by leeyu 修改 BUG:0000517---------//
			CtlPubPara ctlpubpara = new CtlPubPara();
			ctlpubpara.setYssPub(pub);
			boolean isACTV = ctlpubpara.getIsUseACTVInfo(getPortCode(fSetCode
					+ "", YssFun.left(YssFun.formatDate(date), 4)));
			boolean isUseAmount = ctlpubpara.getIsUseAmount(getPortCode(
					fSetCode + "", YssFun.left(YssFun.formatDate(date), 4)));// add
																				// by
																				// yanghaiming
																				// 20101109
																				// QDV4华安基金2010年10月11日01_A
																				// 获取通用参数
			// MS01160 QDV4招商基金2010年5月6日01_A 财务估值表增加一栏显示所有的估值增值余额 add by
			// jiangshichao 2010.05.25--
			bShowGz = ctlpubpara.getGuessValueShowGZ().equalsIgnoreCase("1") ? true
					: false;
			// MS01160 QDV4招商基金2010年5月6日01_A 财务估值表增加一栏显示所有的估值增值余额
			// --------------------------------
			// -------------------------------------------------------------------------------------//
			// ------通过参数获取资产类、负债类合计计算的科目级别 by sunkey 20081210
			// BugNO:MS00072--------//
			int acctLevel = -1;
			// -------------------------------------------------------------------------------------//
			// QDV4海富通2009年05月11日01_AB MS00439 by leeyu 2009-05-19 通过参数来控制数据的显示
			CtlPubPara ctlPub = new CtlPubPara();
			ctlPub.setYssPub(pub);
			bShowYSX = ctlPub.checkGuessReal(getPortCode(fSetCode + "", YssFun
					.formatDate(date, "yyyy"))); // 显示财务估值表中已实现部分的数据
													// QDV4海富通2009年05月11日01_AB
													// MS00439
			// QDV4海富通2009年05月11日01_AB MS00439
			if(ctlPub.getShowValueSetting("CboShowDec").equalsIgnoreCase("1")){
				showDec = "1";
			}
			/***************************************************************
			 * 南方基金没有设置辅助核算项 ，
			 *  获取估值财务报表辅助核算项设置，来选择估值行情表的关联方式.
			 *  系统默认认为启用辅助核算项设置
			 */
			
			String  strJoinPara="";
			if(ctlPub.getGuessUseAuxiaccSet(getPortCode(fSetCode
					+ "", YssFun.left(YssFun.formatDate(date), 4)))){
				//启用辅助核算项设置
				strJoinPara = " on AccBalance.fcode= Market.FSecurityCode ";
			}else{
				//不启用辅助核算项设置
				strJoinPara = " on  case when facctattrid='H' and facctlevel=4  then substr(AccBalance.facctcode, 9, length(AccBalance.facctcode))||' CG'  " +
						                   " when facctattrid='S'  and facctlevel=4 then substr(AccBalance.facctcode, 9, length(AccBalance.facctcode))||' CS'  when facctattrid<>'H' and facctattrid<>'S' and facctlevel=4 then  substr(AccBalance.facctcode, 9, length(AccBalance.facctcode)) end =  Market.FSecurityCode ";
			}
			
			dbl.loadConnection().setAutoCommit(false);
			// 因为财务估值表参数需要一个setID条件作为取值条件 故而查询此字段 sunkey 20081218 BubNO:MS00072
			sql =
			// --MS00348 QDV4招商证券2009年04月01日01_B
			// 获取套帐起始月份---------------------------
				//---edit by songjie 2011.06.08 BUG 1995 QDV4赢时胜上海2011年5月26日01_B---//
			"select a.FAccLen,b.fportcode,b.FPortCury,a.fsetID,a.FStartMonth from lsetlist a inner join (select * from "
					+
					// ------------------------------------------------------------------------------
					pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FCheckState = 1) b on a.fsetid=b.fassetcode where fsetcode="
					//---edit by songjie 2011.06.08 BUG 1995 QDV4赢时胜上海2011年5月26日01_B---//
					+ fSetCode
					+ " and fyear="
					+ AccountingYear
					+ " order by FSetCode desc";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				AccLen = rs.getString("FAccLen");
				PortCode = rs.getString("fportcode");
				sPortCury = rs.getString("FPortCury");
				acctLevel = pubPara.getAcctLevel(rs.getString("FSETID")); // 获取统证券清算款到计资产负债合计的科目值
																			// sunkey
																			// 20081218
																			// BugNO:Ms00072
				// --MS00348 QDV4招商证券2009年04月01日01_B
				// 获取套帐起始月份---------------------------
				intStartMonth = rs.getInt("FStartMonth");
				// ------------------------------------------------------------------------------
			} else {
				throw new YssException("财务系统中的资产代码与估值系统中资产代码不匹配！");
			}

			rs.getStatement().close();

			// 使用变量 sGVTableName 代替原来写死的表名 设置变量 MS00004
			// QDV4.1赢时胜（上海）2009年9月28日03_A 蒋锦
			sql = "Delete from " + pub.yssGetTableName(sGVTableName)
					+ " where fdate=" + dbl.sqlDate(date)
					+ " and FPortCode = '" + fSetCode + "'";
			dbl.executeSql(sql);
			// sql="Insert into "+pub.yssGetTableName("tb_rep_guessvalue")+" Select '"+fSetCode+"',"+dbl.sqlDate(date)+", facctcode,GuessValue.fcurcode,facctname,facctattr,facctclass, case when Fexrate1 is null and fendbal<>0  then round(fbendbal/fendbal,4) when GuessValue.fcurcode='"+sPortCury+"' then 1 else Fexrate1 end as  Fexrate1 ,faendbal,fendbal,fbendbal,0,0,case when faendbal=0 then 0 when faendbal>0 and  MarketPrice is Null then round(fendbal/faendbal,4) else MarketPrice end as MarketPrice ,case when faendbal=0 then fendbal when MarketPrice is Null then fendbal else round(faendbal*MarketPrice,4) end ,case when faendbal=0 then fbendbal when MarketPrice is Null then fbendbal when Fexrate1 is Null then fbendbal else round(faendbal*MarketPrice*Fexrate1,4) end ,0,0,case when faendbal=0 then 0 when MarketPrice is Null then 0 else  round(faendbal*MarketPrice-fendbal,4) end ,case when faendbal=0 then 0 when MarketPrice is Null then 0 when Fexrate1 is Null then fbendbal else round(faendbal*MarketPrice*Fexrate1-fbendbal,4) end ,case when faendbal=0 then ' ' when FMktvalueDate is null then '【无行情】' when "+
			// dbl.sqlDateDiff("FMktvalueDate",dbl.sqlDate(date))+"=0 then '【正常交易】' else '【停牌】('"+dbl.sqlJN()+dbl.sqlFormat("FMktvalueDate","yyyy-MM-dd")+dbl.sqlJN()+"')' end ,facctlevel,1 as facctdetail from  "+
			// 以下代码拆分上一行代码fazmm20070922
			// -----------------------------------------------------------------------------------------------------------------------
			sql = "Insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " Select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ", GuessValue.facctcode,GuessValue.fcurcode,GuessValue.facctname,facctattr,facctclass, "
					+ "case when Fexrate1 is null and fendbal<>0  then round(fbendbal/fendbal,6) when GuessValue.fcurcode='"
					+ sPortCury
					+ "' then 1 else ROUND(Fexrate1, 15) end as  Fexrate1, faendbal, " 
					+ "case when facctattr = '投资估值增值_可退替代款' then 0 " //可退替代款估增没有成本
					+ " when facctattr like '远期%' then 0 else fendbal end as fendbal, "  //modify by wangzuochun 2011.06.17 BUG 1979 远期是没有成本的，在财务估值表中将成本都调整为0
					+ // 汇率
					// ---------------------//ETF估值表的现金类科目的本位币直接以原币乘以汇率得到，目的是为了解决ETF估值表数据无汇率损益
					// by ctq 20091116
					(bIsETFGV ? " case when facctattr not like '%股票投资%' and facctattr not like '%债券投资%' and facctattr not like '%基金投资%' "
							+ " and facctattr not like '%权证投资%' and facctattr not like '%买入返售%' and facctattr not like '%存托凭证%'"
							+ " and facctattr not like '%卖出回购%' then round(fendbal*Fexrate1,2) when GuessValue.facctattr = '投资估值增值_可退替代款' then 0 when facctattr like '远期%' then 0 else fbendbal end fbendbal ,"
							: " case when GuessValue.facctattr = '投资估值增值_可退替代款' then 0 when facctattr like '远期%' then 0 else fbendbal end as fbendbal, ") // //modify by wangzuochun 2011.06.17 BUG 1979 远期是没有成本的，在财务估值表中将成本都调整为0
					+ // 可退替代款估值增 值不计入成本 add panjunfang 2010-02-03 华夏恒指ETF

					// ---------------------------------------------------------------------------------------------------------
					// "case when faendbal=0 then 0 when faendbal>0 and  MarketPrice is Null then round(fendbal/faendbal,4) else nvl(MarketPrice,0) end as MarketPrice,"
					// + // 行情
					" 0,0, case when faendbal=0 then 0 when faendbal>0 and  MarketPrice is Null then round(fendbal/faendbal,4) else (case when MarketPrice is null then 0 else MarketPrice end)  end as MarketPrice,"
					+ // 添加对行情数据的判断，防止数据为空 QDV4建行2009年3月9日01_B MS00299 by leeyu
						// 20090312
					" (case when FOTPrice1 is null then 0 else FOTPrice1 end) as FOTPrice1,"
					+ " (case when FOTPrice2 is null then 0 else FOTPrice2 end) as FOTPrice2,"
					+ " (case when FOTPrice3 is null then 0 else FOTPrice3 end) as FOTPrice3,"; // 其他备用行情
																								// 胡昆
																								// 20071111
			if (bIsRound) {
				sql += " (case when "
						+ dbl.sqlInstr("facctattr", "'配股权证'")
						+
						// fanghaoln 20091125 MS00820 QDV4华夏2009年11月17日01_B
						// 取得和行情在是已经除过报价因子的，所以不用在除报价因子了
						" > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice > 0 Then round(faendbal * MarketPrice, 2)"
						+ // 原币市值=数量*(行情) 行情就是配股权证的估值价格 20080715 胡坤
						// --------------------------------------------------------end
						// MS00820--------------------------------------------------------------------------------
						" when "
						+ dbl.sqlInstr("facctattr", "'远期'")
						+ " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (FOTPrice1 - MarketPrice), 2) "
						+ // 原币市值=数量*(委托价格－行情)
						// -------- MS00370 QDV4招商证券2009年04月09日01_AB
						// ------------------------------------------------------
						/*
						 * 调整股指期货处理。股指期货在衍生工具中的科目余额就是估值增值了，不需要再另行估值。
						 * 如果按中金和华夏模式结转为保证金，则科目余额自然为零，不用再另外强制赋0；
						 * 更新注意：对于股指期货的判断改为获取科目性质"其他衍生工具_股指期货"。
						 */
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (MarketPrice - FOTPrice1), 2)"
						// + //原币市值=数量*(行情-成本价)
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal < 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (FOTPrice1 - MarketPrice), 2)"
						// + //原币市值=数量*(成本价-行情)
						/**shashijie 2012-3-13 STORY 2335 修正财务估值表中股指期货估增的显示效果 */
						" when "
						+ dbl.sqlInstr("facctattr", "'其他衍生工具_股指期货_初始'")
						+ " > 0 then " +
						" NVL(Fendbal + NVL(gzFendbal,0),0) "+//BUG4754 增加字段空值判断
						" when "
						+ dbl.sqlInstr("facctattr", "'其他衍生工具_股指期货'")
						+ " > 0 then " +
						//---add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
						(YssCons.YSS_STOCK_INDEX_FUTURE_MV_MODE.equals("default") ? 
						" NVL(Fendbal ,0) " : " NVL(Fendbal - NVL(gzFendbal,0),0) ")
						//---add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
						/**end*/
						+ // 当为股指期货时不计算市值 QDV4中金2009年02月24日02_A MS00268 by leeyu
							// 20090225
						//--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						" when instr(FacctAttr, '其他衍生工具_股指期权') > 0 and faendbal <> 0 " + 
						" and MarketPrice is not null and MarketPrice > 0 then " +
						" round(faendbal * MarketPrice * FMultiple, 2) " +
						" when instr(FacctAttr, '其他衍生工具_股票期权') > 0 and faendbal <> 0 " + 
						" and MarketPrice is not null and MarketPrice > 0 then " +
						" round(faendbal * MarketPrice * FMultiple, 2) " +
						//--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						" when faendbal = 0 then fendbal when MarketPrice is Null then fendbal else round(faendbal * MarketPrice, 2) end)"; // 原币市值
				//20120530 added by liubo.Story #2452
				//估值表勾选了“含证券市值调整数据”后，如果某只券进行过证券市值调整，在计算原币\本位币市值时，需要加上调整后的原币\本位币的差异值
				//=======================================
				if (sIsIncludeModifyingData.trim().equalsIgnoreCase("true"))
				{
					sql += " + round(NVL(modifying.FOMDIFFERENCE,0),2)"; 
				}
				sql += " ,";
				//==================end==================
				// -----------------------------------------------------------------------------------------------------
			} else {
				sql += " case when "
						+ dbl.sqlInstr("facctattr", "'配股权证'")
						+
						// fanghaoln 20091125 MS00820 QDV4华夏2009年11月17日01_B
						// 取得和行情在是已经除过报价因子的，所以不用在除报价因子了
						" > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice > 0 Then faendbal * MarketPrice "
						+ // 原币市值=数量*(行情) 行情就是配股权证的估值价格 20080715 胡坤
						// --------------------------------------------------------end
						// MS00820--------------------------------------------------------------------------------
						" when "
						+ dbl.sqlInstr("facctattr", "'远期'")
						+ " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then faendbal * (FOTPrice1 - MarketPrice) "
						+ // 原币市值=数量*(委托价格－行情)
						// -------- MS00370 QDV4招商证券2009年04月09日01_AB
						// ------------------------------------------------------
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then faendbal * (MarketPrice - FOTPrice1)"
						// + //原币市值=数量*(行情-成本价)
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal < 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then faendbal * (FOTPrice1 - MarketPrice)"
						// + //原币市值=数量*(成本价-行情)
						/**shashijie 2012-3-13 STORY 2335 修正财务估值表中股指期货估增的显示效果 */
						" when "
						+ dbl.sqlInstr("facctattr", "'其他衍生工具_股指期货_初始'")
						+ " > 0 then "+
						" NVL(Fendbal + NVL(gzFendbal,0),0) "+//BUG4754 增加字段空值判断
						" when "
						+ dbl.sqlInstr("facctattr", "'其他衍生工具_股指期货'")
						+ " > 0 then "+
						//---edit by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
						(YssCons.YSS_STOCK_INDEX_FUTURE_MV_MODE.equals("default") ? 
						" NVL(Fendbal ,0) " : " NVL(Fendbal - NVL(gzFendbal,0),0) ")
						//---edit by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
						/**end*/
						+ // 当为股指期货时不计算市值 QDV4中金2009年02月24日02_A MS00268 by leeyu
							// 20090225
						//--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						" when instr(FacctAttr, '其他衍生工具_股指期权') > 0 and faendbal <> 0 " + 
						" and MarketPrice is not null and MarketPrice > 0 then " +
						" faendbal * MarketPrice * FMultiple " +
						" when instr(FacctAttr, '其他衍生工具_股票期权') > 0 and faendbal <> 0 " + 
						" and MarketPrice is not null and MarketPrice > 0 then " +
						" faendbal * MarketPrice * FMultiple " +
						//--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						
						//20120629 modified by liubo.Bug #4851
						//==================================
//						" when faendbal = 0 then fendbal when MarketPrice is Null then fendbal else faendbal * MarketPrice end) "; // 原币市值
						" when faendbal = 0 then fendbal when MarketPrice is Null then fendbal else faendbal * MarketPrice end ";
						//================end==================
				
						//20120530 added by liubo.Story #2452
				//估值表勾选了“含证券市值调整数据”后，如果某只券进行过证券市值调整，在计算原币\本位币市值时，需要加上调整后的原币\本位币的差异值
				//=======================================
				if (sIsIncludeModifyingData.trim().equalsIgnoreCase("true"))
				{
					sql += " + NVL(modifying.FOMDIFFERENCE,0)"; 
				}
				sql += " ,";
				//==================end===================
				
				// ----------------------------------------------------------------------------------------------------
			}
			// 已把配股价格，远期委托价格，股指期货成本价格放入FOTPrice1，这里增加对这些品种的处理 胡昆 20071111
			// "case when faendbal=0 then fbendbal when MarketPrice is Null then fbendbal when Fexrate1 is Null then fbendbal else  round(round(round(faendbal*MarketPrice,2)-fendbal,2)*Fexrate1,2) + round(round(fendbal*Fexrate1,2)-fbendbal,2) +fbendbal  end ,"
			// + // 本位币市值
			// 本位币市值，新股没行情，市值中则没有把由于汇率引起的估值增值减掉。杨文奇20071017
			// 如果行情为空时，为成本乘以汇率得到市值，而不是round(faendbal * round(fendbal / faendbal,
			// 4) * Fexrate1,2)，中间过程不需要保留4位小数
			// 如果有问题及时反馈fazmm2001101
			// --------------------------------2008.03.24 蒋锦 修改
			// 去掉这块代码中的四舍五入，在这里算出来会和净值表对不上------------------------//
			if (bIsRound) {
				// fanghaoln 20091125 MS00820 QDV4华夏2009年11月17日01_B
				// 取得和行情在是已经除过报价因子的，所以不用在除报价因子了
				sql += " (case when "
						+ dbl.sqlInstr("facctattr", "'配股权证'")
						+ " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice > 0 Then round(ROUND(faendbal * MarketPrice, 2) * ROUND(Fexrate1, 15), 2)"
						+ // 本位币市值=数量*(行情)*汇率 行情就是配股权证的估值价格 20080715 胡坤
						// --------------------------------------------------------end
						// MS00820--------------------------------------------------------------------------------
						" when "
						+ dbl.sqlInstr("facctattr", "'远期'")
						+ " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(ROUND(faendbal * (FOTPrice1 - MarketPrice), 2) * ROUND(Fexrate1, 15), 2)"
						+ // 本位币市值=数量*(委托价格－行情)*汇率
						// -------- MS00370 QDV4招商证券2009年04月09日01_AB
						// ------------------------------------------------------
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(ROUND(faendbal * (MarketPrice - FOTPrice1), 2) * ROUND(Fexrate1, 15), 2)"
						// + //本位币市值=数量*(行情-成本价)*汇率
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal < 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(ROUND(faendbal * (FOTPrice1 - MarketPrice), 2) * ROUND(Fexrate1, 15), 2)"
						// + //本位币市值=数量*(成本价-行情)*汇率
						/**shashijie 2012-3-13 STORY 2335 修正财务估值表中股指期货估增的显示效果 */
						" when "
						+ dbl.sqlInstr("facctattr", "'其他衍生工具_股指期货_初始'")
						+ " > 0 then "+
						//---edit by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
						(YssCons.YSS_STOCK_INDEX_FUTURE_MV_MODE.equals("default") ? 
						" NVL(Fbendbal ,0) " : " NVL(Fbendbal + NVL(gzFbendbal,0),0) ") +
						//---edit by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
						" when "
						+ dbl.sqlInstr("facctattr", "'其他衍生工具_股指期货'")
						+ " > 0 then "+
						//" NVL(Fbendbal + NVL(gzFbendbal,0),0) "//BUG4754 增加字段空值判断
						//---edit by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
						(YssCons.YSS_STOCK_INDEX_FUTURE_MV_MODE.equals("default") ? 
						" NVL(Fbendbal + NVL(gzFbendbal,0),0) " : " NVL(Fbendbal - NVL(gzFbendbal,0),0) ")
						//---edit by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
						/**end*/
						+ // 当为股指期货时不计算市值 QDV4中金2009年02月24日02_A MS00268 by leeyu
							// 20090225
						// ----------------------------------------------------------------------------------------------------
						// " when faendbal = 0 then fbendbal when MarketPrice is Null then fbendbal when Fexrate1 is Null then fbendbal else round(faendbal * MarketPrice * Fexrate1, 2) end,"
						// +// 本位币市值
						" when Fexrate1 is Null then fbendbal "
						+ // 数量不为0，行情汇率为空，采用本位币余额
						// ---------------ETF估值表中现金类的科目的本位币市值改为以原币市值乘以汇率得到--------------------------------------------------
						(bIsETFGV ? " when facctattr not like '%股票投资%' and facctattr not like '%债券投资%' and facctattr not like '%基金投资%' "
								+ " and facctattr not like '%权证投资%' and facctattr not like '%买入返售%' and facctattr not like '%存托凭证%'"
								+ " and facctattr not like '%卖出回购%' then round(fendbal*Fexrate1,2) "
								: " ")
						+
						// -----------------------------------------------------------------------------------------------------------------------------------
						" when faendbal = 0 then fbendbal "
						+ " when MarketPrice is Null then round(fendbal * ROUND(Fexrate1, 15), 2) "; // 彭彪20071120
																										// 如有数量无行情时，根据成本/数量的行情计算本位币市值
			} else {
				sql += " (case when "
						+ dbl.sqlInstr("facctattr", "'配股权证'")
						+
						// fanghaoln 20091125 MS00820 QDV4华夏2009年11月17日01_B
						// 取得和行情在是已经除过报价因子的，所以不用在除报价因子了
						" > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice > 0 Then round(faendbal * MarketPrice * ROUND(Fexrate1, 15), 2)"
						+ // 本位币市值=数量*(行情)*汇率 行情就是配股权证的估值价格 20080715 胡坤
						// --------------------------------------------------------end
						// MS00820------------------------------------------------------------------------------
						" when "
						+ dbl.sqlInstr("facctattr", "'远期'")
						+ " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (FOTPrice1 - MarketPrice) * ROUND(Fexrate1, 15), 2)"
						+ // 本位币市值=数量*(委托价格－行情)*汇率
						// -------- MS00370 QDV4招商证券2009年04月09日01_AB
						// ------------------------------------------------------
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (MarketPrice - FOTPrice1) * ROUND(Fexrate1, 15), 2)"
						// + //本位币市值=数量*(行情-成本价)*汇率
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal < 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (FOTPrice1 - MarketPrice) * ROUND(Fexrate1, 15), 2)"
						// + //本位币市值=数量*(成本价-行情)*汇率
						/**shashijie 2012-3-13 STORY 2335 修正财务估值表中股指期货估增的显示效果 */
						" when "
						+ dbl.sqlInstr("facctattr", "'其他衍生工具_股指期货_初始'")
						+ " > 0 then "+
						//20120412 modified by liubo.Bug #4261
						//在这个分支的两个NVL函数未写完整
						//=================================
						//" NVL(Fbendbal + gzFbendbal,) "+
						" NVL(Fbendbal + NVL(gzFbendbal,0),0) "+//BUG4754 增加字段空值判断
						" when "
						+ dbl.sqlInstr("facctattr", "'其他衍生工具_股指期货'")
						+ " > 0 then "+
						//" NVL(Fbendbal ,) "
						" NVL(Fbendbal ,0) "
						//=================end================
						/**end*/
						+ // 当为股指期货时不计算市值 QDV4中金2009年02月24日02_A MS00268 by leeyu
							// 20090225
						// ----------------------------------------------------------------------------------------------------
						// " when faendbal = 0 then fbendbal when MarketPrice is Null then fbendbal when Fexrate1 is Null then fbendbal else round(faendbal * MarketPrice * Fexrate1, 2) end,"
						// +// 本位币市值
						" when Fexrate1 is Null then fbendbal "
						+ // 数量不为0，行情汇率为空，采用本位币余额
						// ---------------ETF估值表中现金类的科目的本位币市值改为以原币市值乘以汇率得到--------------------------------------------------
						(bIsETFGV ? " when facctattr not like '%股票投资%' and facctattr not like '%债券投资%' and facctattr not like '%基金投资%' "
								+ " and facctattr not like '%权证投资%' and facctattr not like '%买入返售%' and facctattr not like '%存托凭证%'"
								+ " and facctattr not like '%卖出回购%' then round(fendbal*Fexrate1,2) "
								: " ")
						+
						// -----------------------------------------------------------------------------------------------------------------------------------
						" when faendbal = 0 then fbendbal "
						+ " when MarketPrice is Null then round(fendbal * ROUND(Fexrate1, 15), 2) "; // 彭彪20071120
																										// 如有数量无行情时，根据成本/数量的行情计算本位币市值

			}
			if (bIsRound) {
				if (bMVIsRound2) {
					sql += //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						   " when instr(FacctAttr, '其他衍生工具_股指期权') > 0 and faendbal <> 0 " + 
					       " and MarketPrice is not null and MarketPrice > 0 then " +
					       " round(faendbal * MarketPrice * FMultiple * ROUND(Fexrate1, 15), 2) " +
						   " when instr(FacctAttr, '其他衍生工具_股票期权') > 0 and faendbal <> 0 " + 
					       " and MarketPrice is not null and MarketPrice > 0 then " +
					       " round(faendbal * MarketPrice * FMultiple * ROUND(Fexrate1, 15), 2) " +
					       //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						   " when Fexrate1 is Null then fbendbal else round(faendbal * MarketPrice * ROUND(Fexrate1, 15), 2) end) "; // 本位币市值计算中间不保留位数
																																	// QDV4汇添富2011年01月10日01_A
																																	// panjunfang
																																	// modify
																																	// 20110117
				} else {
					sql += //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						   " when instr(FacctAttr, '其他衍生工具_股指期权') > 0 and faendbal <> 0 " + 
					       " and MarketPrice is not null and MarketPrice > 0 then " +
					       " round(round(faendbal * MarketPrice * FMultiple,2) * ROUND(Fexrate1, 15), 2) " +
						   " when instr(FacctAttr, '其他衍生工具_股票期权') > 0 and faendbal <> 0 " + 
					       " and MarketPrice is not null and MarketPrice > 0 then " +
					       " round(round(faendbal * MarketPrice * FMultiple,2) * ROUND(Fexrate1, 15), 2) " +
					       //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						   " when Fexrate1 is Null then fbendbal else round(round(faendbal * MarketPrice,2) * ROUND(Fexrate1, 15), 2) end) "; // 本位币市值
				}
				//20120530 added by liubo.Story #2452
				//估值表勾选了“含证券市值调整数据”后，如果某只券进行过证券市值调整，在计算原币\本位币市值时，需要加上调整后的原币\本位币的差异值
				//=======================================
				if (sIsIncludeModifyingData.trim().equalsIgnoreCase("true"))
				{
					sql += " + round(NVL(modifying.FSMDIFFERENCE,0),2)"; 
				}
				sql += " ,";
				//=================end====================
			} else {
				sql += //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
					   " when instr(FacctAttr, '其他衍生工具_股指期权') > 0 and faendbal <> 0 " + 
				       " and MarketPrice is not null and MarketPrice > 0 then " +
				       " round(faendbal * MarketPrice * FMultiple * ROUND(Fexrate1, 15), 2) " +
					   " when instr(FacctAttr, '其他衍生工具_股票期权') > 0 and faendbal <> 0 " + 
				       " and MarketPrice is not null and MarketPrice > 0 then " +
				       " round(faendbal * MarketPrice * FMultiple * ROUND(Fexrate1, 15), 2) " +
				       //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
					   " when Fexrate1 is Null then fbendbal else round(faendbal * MarketPrice * ROUND(Fexrate1, 15), 2) end) "; // 本位币市值
				
				//20120530 added by liubo.Story #2452
				//估值表勾选了“含证券市值调整数据”后，如果某只券进行过证券市值调整，在计算原币\本位币市值时，需要加上调整后的原币\本位币的差异值
				//=======================================
				if (sIsIncludeModifyingData.trim().equalsIgnoreCase("true"))
				{
					sql += " + NVL(modifying.FSMDIFFERENCE,0)"; 
				}
				sql += " ,";
				//=================end====================
			}
			// ----------------------------------
			if (bIsRound) {
				sql += " 0,0,"
						+
						// ----------------------------------
						" (case when "
						+ dbl.sqlInstr("facctattr", "'配股权证'")
						+
						// fanghaoln 20091125 MS00820 QDV4华夏2009年11月17日01_B
						// 取得和行情在是已经除过报价因子的，所以不用在除报价因子了
						" > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice > 0 Then round(faendbal * MarketPrice, 2)"
						+ // 原币估值增值=数量*(行情) 行情就是配股权证的估值价格 20080715 胡坤
						// --------------------------------------------------------end
						// MS00820------------------------------------------------------------------------------
						" when "
						+ dbl.sqlInstr("facctattr", "'远期'")
						+ " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (FOTPrice1 - MarketPrice), 2)"
						+ // 原币估值增值=数量*(委托价格－行情)
						// -------- MS00370 QDV4招商证券2009年04月09日01_AB
						// ------------------------------------------------------
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (MarketPrice - FOTPrice1), 2)"
						// + //原币估值增值=数量*(行情-成本价)
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal < 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (FOTPrice1 - MarketPrice), 2)"
						// + //原币估值增值=数量*(成本价-行情)
						/**shashijie 2012-3-13 STORY 2335 修正财务估值表中股指期货估增的显示效果 */
						" when instr(FacctAttr,'其他衍生工具_股指期货_初始')>0 and FacctLevel<>1 then "+
						" NVL(gzFendbal,0) "+
						" when instr(FacctAttr,'其他衍生工具_股指期货')>0 and FacctLevel<>1 then "+
						" 0 "
						/**end*/
						+ // 当为股指期货时不计算估值增值 QDV4中金2009年02月24日02_A MS00268 by
							// leeyu 20090225
						//--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						" when instr(FacctAttr, '其他衍生工具_股指期权') > 0 and faendbal <> 0 " +
						" and MarketPrice is not null and MarketPrice > 0 then " +
						" round(round(faendbal * MarketPrice * FMultiple, 2) - fendbal, 2) " +
						" when instr(FacctAttr, '其他衍生工具_股票期权') > 0 and faendbal <> 0 " +
						" and MarketPrice is not null and MarketPrice > 0 then " +
						" round(round(faendbal * MarketPrice * FMultiple, 2) - fendbal, 2) " +
						//--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						// ----------------------------------------------------------------------------------------------------
						" when faendbal = 0 then 0 when MarketPrice is Null then 0 else round(ROUND(faendbal * MarketPrice, 2) - fendbal, 2) end) ";
				
				//20120530 added by liubo.Story #2452
				//估值表勾选了“含证券市值调整数据”后，如果某只券进行过证券市值调整，在计算原币\本位币估值增值时，需要加上调整后的原币\本位币的差异值
				//=======================================
				if (sIsIncludeModifyingData.trim().equalsIgnoreCase("true"))
		        {
		          sql += " + round(NVL(modifying.FOMDIFFERENCE,0),2)"; 
		        }
				//==================end====================
				
		        sql += " ,";
						 // 原币估值增值
						// 本位币估值增值 ＝ 原币估值增值＋估值增值汇兑损益 ＋ 成本汇兑损益
						// 本位币估值增值 ＝ 本位币估值增值（round(原币估值增值×汇率,2)） +
						// 成本汇兑损益（round(原币成本×汇率,2)-本位币成本）
						// "case when faendbal=0 then 0 when MarketPrice is Null then 0 when Fexrate1 is Null then fbendbal else round(round(round(faendbal*MarketPrice,2)-fendbal,2)*Fexrate1,2) + round(round(fendbal*Fexrate1,2)-fbendbal,2) end ,"
						// + //本位币估值增值
						// 本位币估值增值,新股没行情，由于汇兑损益产生的估值增值，因为MarketPrice为NULL，所以显示不出来估值增值。杨文奇20071016
						// 彭彪20071030 先保留2位小数再相减,避免出现尾差
						// 如果行情为空时，为成本乘以汇率得到本位币市值，然后减去本位币成本，中间过程不需要保留4位小数
						// 如果有问题及时联系我fazmm20071101
						// fanghaoln 20091125 MS00820 QDV4华夏2009年11月17日01_B
						// 取得和行情在是已经除过报价因子的，所以不用在除报价因子了
		        sql += " (case when "
						+ dbl.sqlInstr("facctattr", "'配股权证'")
						+ "> 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 > 0 Then round(ROUND(faendbal * MarketPrice, 2) * ROUND(Fexrate1, 15), 2)"
						+ // 本位币估值增值=数量*(行情)*汇率 行情就是配股权证的估值价格 20080715 胡坤
						// --------------------------------------------------------end
						// MS00820------------------------------------------------------------------------------
						" when "
						+ dbl.sqlInstr("facctattr", "'远期'")
						+ " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(Round(faendbal * (FOTPrice1 - MarketPrice), 2) * ROUND(Fexrate1, 15), 2)"
						+ // 本位币估值增值=数量*(委托价格－行情)*汇率
						// -------- MS00370 QDV4招商证券2009年04月09日01_AB
						// ------------------------------------------------------
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(Round(faendbal * (MarketPrice - FOTPrice1), 2) * ROUND(Fexrate1, 15), 2)"
						// + //本位币估值增值=数量*(行情-成本价)*汇率
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal < 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(Round(faendbal * (FOTPrice1 - MarketPrice), 2) * ROUND(Fexrate1, 15), 2)"+
						// //本位币估值增值=数量*(成本价-行情)*汇率
						/**shashijie 2012-3-13 STORY 2335 修正财务估值表中股指期货估增的显示效果 */
						" when instr(FacctAttr,'其他衍生工具_股指期货_初始')>0 and FacctLevel<>1 then "+
						" NVL(gzFBendbal,0) "+
						" when instr(FacctAttr,'其他衍生工具_股指期货')>0 and FacctLevel<>1 then "+
						" 0 "
						/**end*/
																						; // 当为股指期货时不计算估值增值
																							// QDV4中金2009年02月24日02_A
																							// MS00268
																							// by
																							// leeyu
																							// 20090225
				// ----------------------------------------------------------------------------------------------------
			} else {
				sql += " 0,0,"
						+
						// ----------------------------------
						" (case when "
						+ dbl.sqlInstr("facctattr", "'配股权证'")
						+
						// fanghaoln 20091125 MS00820 QDV4华夏2009年11月17日01_B
						// 取得和行情在是已经除过报价因子的，所以不用在除报价因子了
						" > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice > 0 Then faendbal * MarketPrice "
						+ // 原币估值增值=数量*(行情) 行情就是配股权证的估值价格 20080715 胡坤
						// --------------------------------------------------------end
						// MS00820------------------------------------------------------------------------------
						" when "
						+ dbl.sqlInstr("facctattr", "'远期'")
						+ " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then faendbal * (FOTPrice1 - MarketPrice)"
						+ // 原币估值增值=数量*(委托价格－行情)
						// -------- MS00370 QDV4招商证券2009年04月09日01_AB
						// ------------------------------------------------------
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then faendbal * (MarketPrice - FOTPrice1)"
						// + //原币估值增值=数量*(行情-成本价)
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal < 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then faendbal * (FOTPrice1 - MarketPrice)"
						// + //原币估值增值=数量*(成本价-行情)
						/**shashijie 2012-3-13 STORY 2335 修正财务估值表中股指期货估增的显示效果 */
						" when instr(FacctAttr,'其他衍生工具_股指期货_初始')>0 and FacctLevel<>1 then "+
						" NVL(gzFendbal,0) "+
						" when instr(FacctAttr,'其他衍生工具_股指期货')>0 and FacctLevel<>1 then "+
						" 0 "
						/**end*/
						+ // 当为股指期货时不计算估值增值 QDV4中金2009年02月24日02_A MS00268 by
							// leeyu 20090225
						// ----------------------------------------------------------------------------------------------------
						//--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						" when instr(FacctAttr, '其他衍生工具_股指期权') > 0 and faendbal <> 0 " +
						" and MarketPrice is not null and MarketPrice > 0 then " +
						" faendbal * MarketPrice * FMultiple - fendbal " +
						" when instr(FacctAttr, '其他衍生工具_股票期权') > 0 and faendbal <> 0 " +
						" and MarketPrice is not null and MarketPrice > 0 then " +
						" faendbal * MarketPrice * FMultiple - fendbal " +
						//--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						" when faendbal = 0 then 0 when MarketPrice is Null then 0 else faendbal * MarketPrice - fendbal end) ";
				//20120530 added by liubo.Story #2452
				//估值表勾选了“含证券市值调整数据”后，如果某只券进行过证券市值调整，在计算原币\本位币估值增值时，需要加上调整后的原币\本位币的差异值
				//=======================================
				if (sIsIncludeModifyingData.trim().equalsIgnoreCase("true"))
				{
					sql += " + NVL(modifying.FOMDIFFERENCE,0)"; 
				}
				//==================end===================
				sql += " ,";
						// 原币估值增值
						// 本位币估值增值 ＝ 原币估值增值＋估值增值汇兑损益 ＋ 成本汇兑损益
						// 本位币估值增值 ＝ 本位币估值增值（round(原币估值增值×汇率,2)） +
						// 成本汇兑损益（round(原币成本×汇率,2)-本位币成本）
						// "case when faendbal=0 then 0 when MarketPrice is Null then 0 when Fexrate1 is Null then fbendbal else round(round(round(faendbal*MarketPrice,2)-fendbal,2)*Fexrate1,2) + round(round(fendbal*Fexrate1,2)-fbendbal,2) end ,"
						// + //本位币估值增值
						// 本位币估值增值,新股没行情，由于汇兑损益产生的估值增值，因为MarketPrice为NULL，所以显示不出来估值增值。杨文奇20071016
						// 彭彪20071030 先保留2位小数再相减,避免出现尾差
						// 如果行情为空时，为成本乘以汇率得到本位币市值，然后减去本位币成本，中间过程不需要保留4位小数
						// 如果有问题及时联系我fazmm20071101
				sql += " (case when "
						+ dbl.sqlInstr("facctattr", "'配股权证'")
						+
						// fanghaoln 20091125 MS00820 QDV4华夏2009年11月17日01_B
						// 取得和行情在是已经除过报价因子的，所以不用在除报价因子了
						"> 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 > 0 Then round(faendbal * MarketPrice * ROUND(Fexrate1, 15), 2)"
						+ // 本位币估值增值=数量*(行情)*汇率 行情就是配股权证的估值价格 20080715 胡坤
						// --------------------------------------------------------end
						// MS00820----------------------------------------------------------------------------
						" when "
						+ dbl.sqlInstr("facctattr", "'远期'")
						+ " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (FOTPrice1 - MarketPrice) * ROUND(Fexrate1, 15), 2)"
						+ // 本位币估值增值=数量*(委托价格－行情)*汇率
						// -------- MS00370 QDV4招商证券2009年04月09日01_AB
						// ------------------------------------------------------
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal > 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (MarketPrice - FOTPrice1) * ROUND(Fexrate1, 15), 2)"
						// + //本位币估值增值=数量*(行情-成本价)*汇率
						// " when " + dbl.sqlInstr("facctattr", "'股指期货'") +
						// " > 0 and faendbal < 0 and FOTPrice1 > 0 and MarketPrice is not null and MarketPrice - FOTPrice1 <> 0 Then round(faendbal * (FOTPrice1 - MarketPrice) * ROUND(Fexrate1, 15), 2)"+
						// //本位币估值增值=数量*(成本价-行情)*汇率
						/**shashijie 2012-3-13 STORY 2335 修正财务估值表中股指期货估增的显示效果 */
						" when instr(FacctAttr,'其他衍生工具_股指期货_初始')>0 and FacctLevel<>1 then "+
						" NVL(gzFBendbal,0) "+
						" when instr(FacctAttr,'其他衍生工具_股指期货')>0 and FacctLevel<>1 then "+
						" 0 "
						/**end*/
						; // 当为股指期货时不计算估值增值
																							// QDV4中金2009年02月24日02_A
																							// MS00268
																							// by
																							// leeyu
																							// 20090225
				// ----------------------------------------------------------------------------------------------------
			}
			// --MS00349 QDV4招商证券2009年04月01日02_B
			// 增加对没有汇率的情况的处理，使组合估值增值的值为上期余额---------------------------
			if (bIsRound) {
				if (bMVIsRound2) {// 本位币市值计算中间不保留位数 QDV4汇添富2011年01月10日01_A
									// panjunfang modify 20110117
					sql += //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						   " when instr(FacctAttr, '其他衍生工具_股指期权') > 0 and faendbal <> 0 " +
						   " and MarketPrice is not null and MarketPrice > 0 then " +
						   " round(round(faendbal*MarketPrice * FMultiple * ROUND(Fexrate1, 15), 2) - fbendbal,2) " +
						   " when instr(FacctAttr, '其他衍生工具_股票期权') > 0 and faendbal <> 0 " +
						   " and MarketPrice is not null and MarketPrice > 0 then " +
						   " round(round(faendbal*MarketPrice * FMultiple * ROUND(Fexrate1, 15), 2) - fbendbal,2) " +
						   //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						   " when faendbal = 0 then 0 when MarketPrice is Null and Fexrate1 is not Null then round(round(fendbal * ROUND(Fexrate1, 15), 2) - fbendbal,2) when Fexrate1 is Null then fbendbal else round(round(faendbal*MarketPrice * ROUND(Fexrate1, 15),2)-fbendbal,2) end) ";
				} else {
					sql += //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						   " when instr(FacctAttr, '其他衍生工具_股指期权') > 0 and faendbal <> 0 " +
						   " and MarketPrice is not null and MarketPrice > 0 then " +
						   " round(round(round(faendbal * MarketPrice * FMultiple,2) * ROUND(Fexrate1, 15),2) - fbendbal,2) " + 
						   " when instr(FacctAttr, '其他衍生工具_股票期权') > 0 and faendbal <> 0 " +
						   " and MarketPrice is not null and MarketPrice > 0 then " +
						   " round(round(round(faendbal * MarketPrice * FMultiple,2) * ROUND(Fexrate1, 15),2) - fbendbal,2) " + 
						   //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
						   " when faendbal = 0 then 0 when MarketPrice is Null and Fexrate1 is not Null then round(round(fendbal * ROUND(Fexrate1, 15), 2) - fbendbal,2) when Fexrate1 is Null then fbendbal else round(round(round(faendbal*MarketPrice,2) * ROUND(Fexrate1, 15),2)-fbendbal,2) end) ";
				}
				//20120530 added by liubo.Story #2452
				//估值表勾选了“含证券市值调整数据”后，如果某只券进行过证券市值调整，在计算原币\本位币估值增值时，需要加上调整后的原币\本位币的差异值
				//=======================================
				if (sIsIncludeModifyingData.trim().equalsIgnoreCase("true"))
				{
					sql += " + round(NVL(modifying.FSMDIFFERENCE,0),2)"; 
				}
				sql += " ,";
				//===================end======================
				
			} else {
				sql += //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
					   " when instr(FacctAttr, '其他衍生工具_股指期权') > 0 and faendbal <> 0 " + 
					   " and MarketPrice is not null and MarketPrice > 0 then " + 
					   " round(round(faendbal * MarketPrice * FMultiple * round(Fexrate1, 15), 2) - fbendbal, 2) " +
					   " when instr(FacctAttr, '其他衍生工具_股票期权') > 0 and faendbal <> 0 " + 
					   " and MarketPrice is not null and MarketPrice > 0 then " + 
					   " round(round(faendbal * MarketPrice * FMultiple * round(Fexrate1, 15), 2) - fbendbal, 2) " +
					   //--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
					   " when faendbal = 0 then 0 when MarketPrice is Null and Fexrate1 is not Null then round(round(fendbal * ROUND(Fexrate1, 15), 2) - fbendbal,2) when Fexrate1 is Null then fbendbal else round(round(faendbal*MarketPrice * ROUND(Fexrate1, 15),2)-fbendbal,2) end) ";
				
				//20120530 added by liubo.Story #2452
				//估值表勾选了“含证券市值调整数据”后，如果某只券进行过证券市值调整，在计算原币\本位币估值增值时，需要加上调整后的原币\本位币的差异值
				//=======================================
				if (sIsIncludeModifyingData.trim().equalsIgnoreCase("true"))
				{
					sql += " + NVL(modifying.FSMDIFFERENCE,0)"; 
				}
				sql += " ,";
				//==================end=====================
			}
			// ---------------------------------------------------------------------------------------------------
			// //-----------------------------通过通用参数的设置来判断停牌状态的方式。sj edit
			// 20081017 bug 0000486---------//
			// CtlPubPara ctlpubpara = new CtlPubPara();
			// ctlpubpara.setYssPub(pub);
			// boolean isACTV = ctlpubpara.getIsUseACTVInfo(getPortCode(
			// fSetCode + "", YssFun.left(YssFun.formatDate(date), 4)));
			// //-------------------------------------------------------------------------------------//
			// 这个已经搬到上面去了 by leeyu BUG:0000517
			if (isACTV) {
				if (isUseAmount) {// add by yanghaiming 20101109
									// QDV4华安基金2010年10月11日01_A 数量为0时显示为停牌
					        //--- edit by songjie 2013.05.02 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001 start---//
					        //如果 估值日能够获取到行情，且行情数据的总成交数量 = 0，则 获取 行情信息的总成交数量 > 0 且 行情日期 <= 估值日 的最大日期  作为最近的正常交易日
					sql += "case when substr(GuessValue.facctcode,0,4) = '1102' and (FBARGAINAMOUNT = 0) then '【停牌】('" //modify huangqirong 2013-04-19 bug #7476 增加表别名识别准确字段值
							+ dbl.sqlJN()
							+ dbl.sqlFormat("FMaxMktDate", "yyyy-MM-dd")
							+ dbl.sqlJN()
							+ "')' " 
							//如果估值日获取不到行情，则 获取 行情信息中  行情日期 <= 估值日 的  最大日期 作为最近的正常交易日
							+ " when substr(GuessValue.facctcode,0,4) = '1102' and (FBARGAINAMOUNT is null) then '【停牌】('" //modify huangqirong 2013-04-19 bug #7476 增加表别名识别准确字段值
							+ dbl.sqlJN()
							+ dbl.sqlFormat("FMktvalueDate", "yyyy-MM-dd")
							+ dbl.sqlJN()
							+ "')' " 
							//--- edit by songjie 2013.05.02 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001 end---//
							+ " else ("
							+ "case when upper(FMarketStatus) = 'ACTV'"
							+ " and "
							+ dbl.sqlDateDiff("FMktvalueDate", dbl
									.sqlDate(date))
							+ "=0 "
							+ // 这里加上当日无行情的情况 by leeyu BUG:0000517
							" then '【正常交易】' "
							+ " when faendbal = 0 then ' ' "
							+ // 当为非证券类的，不显示停牌信息。
							" when FMktvalueDate is null then '【无行情】'"
							+ // 若从来没有行情的情况 by leeyu BUG:0000517
							" else '【停牌】('"
							+ dbl.sqlJN()
							+ dbl.sqlFormat("FMktvalueDate", "yyyy-MM-dd")
							+ dbl.sqlJN() + "')' end) end,"; // 权益信息
				} else {
					// -----------------------------------
					sql += "case when upper(FMarketStatus) = 'ACTV'"
							+ " and "
							+ dbl.sqlDateDiff("FMktvalueDate", dbl
									.sqlDate(date))
							+ "=0 "
							+ // 这里加上当日无行情的情况 by leeyu BUG:0000517
							" then '【正常交易】' "
							+ " when faendbal = 0 then ' ' "
							+ // 当为非证券类的，不显示停牌信息。
							" when FMktvalueDate is null then '【无行情】'"
							+ // 若从来没有行情的情况 by leeyu BUG:0000517
							" else '【停牌】('" + dbl.sqlJN()
							+ dbl.sqlFormat("FMktvalueDate", "yyyy-MM-dd")
							+ dbl.sqlJN() + "')' end ,"; // 权益信息
					// -----------------------------------
				}
			} else {
				if (isUseAmount) {// add by yanghaiming 20101109
									// QDV4华安基金2010年10月11日01_A 数量为0时显示为停牌
				   	        //--- edit by songjie 2013.05.02 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001 start---//
					        //如果 估值日能够获取到行情，且行情数据的总成交数量 = 0，则 获取 行情信息的总成交数量 > 0 且 行情日期 <= 估值日 的最大日期  作为最近的正常交易日
					sql += "case when substr(GuessValue.facctcode,0,4) = '1102' and (FBARGAINAMOUNT = 0) then '【停牌】('"	//modify huangqirong 2013-04-19 bug #7476 增加表别名识别准确字段值
							+ dbl.sqlJN()
							+ dbl.sqlFormat("FMaxMktDate", "yyyy-MM-dd")
							+ dbl.sqlJN()
							+ "')' " 
							//如果估值日获取不到行情，则 获取 行情信息中  行情日期 <= 估值日 的  最大日期 作为最近的正常交易日
							+ " when substr(GuessValue.facctcode,0,4) = '1102' and (FBARGAINAMOUNT is null) then '【停牌】('"	//modify huangqirong 2013-04-19 bug #7476 增加表别名识别准确字段值
							+ dbl.sqlJN()
							+ dbl.sqlFormat("FMktvalueDate", "yyyy-MM-dd")
							+ dbl.sqlJN()
							+ "')' " 
							//--- edit by songjie 2013.05.02 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001 end---//
							+ " else ("
							+ "case when faendbal=0 then ' ' when FMktvalueDate is null then '【无行情】' when "
							+ dbl.sqlDateDiff("FMktvalueDate", dbl
									.sqlDate(date))
							+ "=0 then '【正常交易】' else '【停牌】('"
							+ dbl.sqlJN()
							+ dbl.sqlFormat("FMktvalueDate", "yyyy-MM-dd")
							+ dbl.sqlJN() + "')' end) end,";// 权益信息
				} else {
					// -----------------------------------
					sql += "case when faendbal=0 then ' ' when FMktvalueDate is null then '【无行情】' when "
							+ dbl.sqlDateDiff("FMktvalueDate", dbl
									.sqlDate(date))
							+ "=0 then '【正常交易】' else '【停牌】('"
							+ dbl.sqlJN()
							+ dbl.sqlFormat("FMktvalueDate", "yyyy-MM-dd")
							+ dbl.sqlJN() + "')' end ,";// 权益信息
					// -----------------------------------
				}
			}
			// --MS00348 QDV4招商证券2009年04月01日01_B ---------------------------
			if (AccountingPeriod > intStartMonth) { // 当前月份大于启用月份
				intMonth = (AccountingPeriod - 1);
			} else if (AccountingPeriod == intStartMonth) { // 当前月份等于启用月份
				intMonth = 0;
			} else { // 当启用月份小于当前月份时
				throw new YssException("生成财务估值表的日期小于套帐的起始月份！");
			} // end 判断启用月份的值
			// ------------------------------------------------------------------
			String tempStr = "";
			tempStr= "facctlevel,1 as facctdetail,remark from  "
					+ // 彭彪20071020
						// 增加备注，与辅助核算中的备注关联，如需显示ISIN，只要将ISIN插入到辅助核算表中的备注即可，如需显示其他对应插入相关项
					// -----------------------------------------------------------------------------------------------------------------------
					"("
					+ "Select distinct AccBalance.facctcode,AccBalance.facctname,AccBalance.facctattr,AccBalance.facctclass,fcurcode,faendbal,fendbal,fbendbal,FMktvalueDate,MarketPrice,facctlevel,FOTPrice1,FOTPrice2,FOTPrice3,remark,FFactor"
					//add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B 添加期权放大倍数 FMultiple
					//edit by songjie 2013.05.02 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001 添加 Market.FMaxMktDate
					+ ",FMarketStatus,Market.FBARGAINAMOUNT,Market.FMaxMktDate, QP.FMultiple " + 
					/**shashijie 2012-3-13 STORY 2335 */
					" ,gz.gzFendbal As gzFendbal, gz.gzFbendbal As gzFBendbal "+
					/**end*/
																// sj edit
																// 20081017//edit
																// by
																// yanghaiming
																// 20101109
																// QDV4华安基金2010年10月11日01_A
																// 增加Market.FBARGAINAMOUNT字段
					" from " + "(" + "select case when "
					+ dbl.sqlLen(dbl.sqlTrim("fcode"))
					+ ">0 then facctcode"
					+ dbl.sqlJN()
					+ "'_'"
					+ dbl.sqlJN()
					+ "fcode else facctcode end  as facctcode,case when "
					+ dbl.sqlLen(dbl.sqlTrim("AuxiAccName"))
					+ ">0 then AuxiAccName else facctname end facctname ,facctattr,facctclass,fcurcode,sum(faendbal) as faendbal,fcode,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,case when "
					+ dbl.sqlLen(dbl.sqlTrim("fcode"))
					+ ">0  then facctlevel+1 else facctlevel end  as facctlevel,"
					+ dbl.sqlIsNull("remark", "' '")
					+ " as remark,facctattrid " //modify by jsc 20120308 
					+ " from("
					+ "select ba.facctcode,ba.fcurcode,fendbal,fbendbal,faendbal,case when "
					+ dbl.sqlInstr("ba.fauxiacc", "'|'")
					+ ">0 then "
					+ dbl.sqlSubStr("ba.fauxiacc", "3", dbl.sqlInstr(
							"ba.fauxiacc", "'|'")
							+ "-3")
					+ " else "
					+ dbl.sqlSubStr("ba.fauxiacc", "3", dbl
							.sqlLen("ba.fauxiacc"))
					+ " end as fCode ,case when "
					+ dbl.sqlInstr("ba.fauxiacc", "'|'")
					+ ">0 then "
					+ dbl.sqlSubStr("ba.fauxiacc", "0", dbl.sqlInstr(
							"ba.fauxiacc", "'|'")
							+ "-1")
					+ " else ba.fauxiacc end as fauxiacc,baac.facctattr,baac.facctname,baac.facctclass,baac.facctlevel,baac.facctattrid from "
					+ YssTabPrefix
					+ "lbalance ba inner join "
					+ YssTabPrefix
					+ "laccount baac on ba.facctcode=baac.facctcode where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					// intMonth +
					// "  and baac.facctdetail=1 and(baac.facctclass='资产类' or baac.facctclass='负债类' or  baac.facctclass='共同类') and baac.facctattr not like '投资估值增值%'"
					// +
					intMonth
					+ "  and baac.facctdetail=1 and(baac.facctclass='资产类' or baac.facctclass='负债类' or  baac.facctclass='共同类') "
					+
					// MS01160 QDV4招商基金2010年5月6日01_A 财务估值表增加一栏显示所有的估值增值余额 add by
					// jiangshichao 2010.05.25
					(bShowGz ? "" : "  and (baac.facctattr not like '投资估值增值%' or baac.facctattr like '%可退替代款%')")
					+(showDec.equalsIgnoreCase("0")? "": " and baac.facctattr not like '减值准备%'")//bug3252 yeshenghong
                    +(showDec.equalsIgnoreCase("0")? "":" and baac.facctcode not like '150311%'")
					// MS01160 QDV4招商基金2010年5月6日01_A 财务估值表增加一栏显示所有的估值增值余额
					// -------------------------------
					// ------------------------------------------------------------------------------
                    /**shashijie 2012-3-13 STORY 2335 */
					+" And Baac.Facctattr Not Like '公允价值_股指期货%' "
					/**end*/
					+ " union all "
					+
					// " select cw.fkmh as facctcode,fcyid as fcurcode,case when fjd='J' then FBal else FBal*-1 end as fendbal,case when fjd='J' then FBBal else FBBal*-1 end as fbendbal,case when fjd='J' thnfbsl else fbsl*-1 end as faendba,case when "+dbl.sqlInstr("cw.fauxiacc","'|'")+">0 then "+dbl.sqlSubStr("cw.fauxiacc","3",dbl.sqlInstr("cw.fauxiacc","'|'")+"-3"
					// )+" else "+dbl.sqlSubStr("cw.fauxiacc","3",dbl.sqlLen("cw.fauxiacc"))+" end as fCode ,case when "+dbl.sqlInstr("cw.fauxiacc","'|'")+">0 then "+dbl.sqlSubStr("cw.fauxiacc","0",dbl.sqlInstr("cw.fauxiacc","'|'")+"-1"
					// )+" else cw.fauxiacc end as fauxiacc,cwac.facctattr,cwac.facctname,cwac.facctclass,cwac.facctlevel from "+YssTabPrefix+"fcwvch cw inner join "+YssTabPrefix+"laccount cwac on cw.fkmh=cwac.facctcode  where fdate>="+dbl.sqlDate(StartDate)+" and fdate<= "+dbl.sqlDate(date)+"  and(cwac.facctclass='资产类' or cwac.facctclass='负债类' or  cwac.facctclass='共同类') and cwac.facctattr not like '投资估值增值%'"+
					// 以下针对于上一条代码进行分行，以及处理了不同凭证，分录一模一样的记录采用内连接之后，只剩下一条记录的问题fazmm20070921
					// ----------------------------------------------------------------------------------------------------------
					" select cw.fkmh as facctcode,fcyid as fcurcode,case when fjd='J' then FBal else FBal*-1 end as fendbal,"
					+ "case when fjd='J' then FBBal else FBBal*-1 end as fbendbal,case when fjd='J' then fbsl else fbsl*-1 end as faendbal,"
					+ "case when "
					+ dbl.sqlInstr("cw.fauxiacc", "'|'")
					+ ">0 then "
					+ dbl.sqlSubStr("cw.fauxiacc", "3", dbl.sqlInstr(
							"cw.fauxiacc", "'|'")
							+ "-3")
					+ " else "
					+ dbl.sqlSubStr("cw.fauxiacc", "3", dbl
							.sqlLen("cw.fauxiacc"))
					+ " end as fCode ,case when "
					+ dbl.sqlInstr("cw.fauxiacc", "'|'")
					+ ">0"
					+ " then "
					+ dbl.sqlSubStr("cw.fauxiacc", "0", dbl.sqlInstr(
							"cw.fauxiacc", "'|'")
							+ "-1")
					+ " else cw.fauxiacc end as fauxiacc,cwac.facctattr,cwac.facctname,cwac.facctclass,cwac.facctlevel ,cwac.facctattrid from ("
					+ "select fkmh,fcyid,fjd,fauxiacc, sum(FBal) as FBal,sum(FBBal) as FBBal,sum(fbsl) as fbsl from "
					+ YssTabPrefix
					+ "fcwvch "
					+
					// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where fdate>=" + dbl.sqlDate(StartDate)
							+ " and fdate< " + dbl.sqlDate(date)
							+ " OR (FDate = " + dbl.sqlDate(date)
							+ " AND Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + ")")
							: ("where fdate>=" + dbl.sqlDate(StartDate)
									+ " and fdate<= " + dbl.sqlDate(date)))
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " group by fkmh,fcyid,fjd,fauxiacc "
					+ ") cw inner join "
					+ YssTabPrefix
					+ "laccount cwac on cw.fkmh=cwac.facctcode "
					+
					// " where (cwac.facctclass='资产类' or cwac.facctclass='负债类' or  cwac.facctclass='共同类') and cwac.facctattr not like '投资估值增值%'"
					// +
					" where (cwac.facctclass='资产类' or cwac.facctclass='负债类' or  cwac.facctclass='共同类') "
					+
					// MS01160 QDV4招商基金2010年5月6日01_A 财务估值表增加一栏显示所有的估值增值余额 add by
					// jiangshichao 2010.05.25
					//STORY #1789 恒指ETF 可退替代款估值增值不能排除
					(bShowGz ? "" : "  and (cwac.facctattr not like '投资估值增值%' or cwac.facctattr like '%可退替代款%')")
					+(showDec.equalsIgnoreCase("0")? "": " and cwac.facctattr not like '减值准备%'")//bug3252 yeshenghong
                    +(showDec.equalsIgnoreCase("0")? "":" and cwac.facctcode not like '150311%'")
					+
					// MS01160 QDV4招商基金2010年5月6日01_A 财务估值表增加一栏显示所有的估值增值余额
					// -------------------------------
					// ---------------------------------------------------------------------------------------------------------------------
					/**shashijie 2012-3-13 STORY 2335 */
					" And Cwac.Facctattr Not Like '公允价值_股指期货%' "+
					/**end*/
					") bal Left join "
					+ YssTabPrefix
					+ "auxiaccset aa on bal.fauxiacc=aa.auxiaccid group by facctcode,facctattrid,fcode,AuxiAccName,facctname,facctattr,facctclass,fcurcode,facctlevel,remark"
					+ ") AccBalance"
					+ " Left Join "
					+ "("
					+ "select Fvaldate as FMktvalueDate"
					+ ",FMarketStatus"
					+ // sj edit 20081017
					//edit by songjie 2013.05.02 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001 添加 FMaxMktDate
					",Fsecuritycode,Fprice as MarketPrice,FOTPrice1,FOTPrice2,FOTPrice3,FFactor,FBARGAINAMOUNT,FMaxMktDate from "
					+ // edit by yanghaiming 20101109 QDV4华安基金2010年10月11日01_A
						// 增加FBARGAINAMOUNT字段
					"("
					+
					// edit by yanghaiming 20101109 QDV4华安基金2010年10月11日01_A
					// 增加mkdatte.FBARGAINAMOUNT字段
					//edit by songjie 2013.05.02 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001 添加 maxmkt.FMaxMktDate
					" Select vmkdatte.*,FFactor,mkdatte.FBARGAINAMOUNT,maxmkt.FMaxMktDate " 
					+ " From (select ValMKP.Fvaldate,ValMKP.Fportcode,ValMKP.Fsecuritycode,ValMKP.Fprice,ValMKP.FOTPrice1,ValMKP.FOTPrice2,ValMKP.FOTPrice3,ValMKP.Fcheckstate"
					+ ",ValMKP.FMarketStatus"
					+ // sj edit 20081017
					" from  "
					+ pub.yssGetTableName("Tb_Data_ValMktPrice")
					+ " ValMKP "
					+ " inner join "
					+ "("
					+ " Select FPortCode,FSecurityCode ,max(FValDate) as FValDate from "
					+ pub.yssGetTableName("Tb_Data_ValMktPrice")
					+ " where fvaldate<="
					+ dbl.sqlDate(date)
					+ " group by FPortCode,FSecurityCode "
					+ ") ValMKPDate on ValMKP.Fvaldate=ValMKPDate.Fvaldate and ValMKP.Fportcode=ValMKPDate.Fportcode and ValMKP.Fsecuritycode=ValMKPDate.Fsecuritycode "
					+
					// edit by yanghaiming 20101109 QDV4华安基金2010年10月11日01_A
					// 关联行情表查询出数量
					" )vmkdatte left join (select mkv.FMKTVALUEDATE,mkv.Fportcode,mkv.Fsecuritycode,mkv.Fcheckstate,mkv.FBARGAINAMOUNT from "
					+ pub.yssGetTableName("tb_data_marketvalue")
					+ " mkv inner join (Select FPortCode,FSecurityCode,max(FMKTVALUEDATE) as FMKTVALUEDATE from "
					+ pub.yssGetTableName("tb_data_marketvalue")
					+ " where FMKTVALUEDATE <="
					+ dbl.sqlDate(date)
					//edit by songjie 2013.05.02 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001 添加 FCheckState = 1 作为查询条件
					+ " and FCheckState = 1 group by FPortCode,FSecurityCode) mkcdate on mkv.FMKTVALUEDATE =mkcdate.FMKTVALUEDATE and mkv.Fportcode = mkcdate.Fportcode and mkv.Fsecuritycode = mkcdate.Fsecuritycode"
					+ " where mkv.fportcode = ' ') mkdatte on vmkdatte.Fsecuritycode = mkdatte.Fsecuritycode and vmkdatte.Fvaldate = mkdatte.FMKTVALUEDATE"
					
					//--- add by songjie 2013.05.02 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001 start---//
					+ " left join (select mkv.FMKTVALUEDATE as FMaxMKTDate, mkv.Fportcode, mkv.Fsecuritycode, mkv.FBARGAINAMOUNT from "
					+ pub.yssGetTableName("tb_data_marketvalue")
					+ " mkv inner join (Select FPortCode, FSecurityCode, max(FMKTVALUEDATE) as FMKTVALUEDATE from "
					+ pub.yssGetTableName("tb_data_marketvalue")
					+ " where FMKTVALUEDATE <= " + dbl.sqlDate(date) + 
					" and FBargainAmount > 0 and FCheckState = 1 group by FPortCode, FSecurityCode) mkcdate " 
					+ " on mkv.FMKTVALUEDATE = mkcdate.FMKTVALUEDATE and mkv.Fportcode = mkcdate.Fportcode " 
					+ " and mkv.Fsecuritycode = mkcdate.Fsecuritycode where mkv.fportcode = ' ') maxmkt " 
					+ " on vmkdatte.Fsecuritycode = maxmkt.Fsecuritycode "  
					//--- add by songjie 2013.05.02 STORY #3895 需求深圳-(南方基金)QDII估值系统V4.0(紧急)20130425001 end---//
					+
					// edit by yanghaiming 20101109 QDV4华安基金2010年10月11日01_A
					// 关联行情表查询出数量
					" left join (select FSecurityCode, FFactor  from "
					+ pub.yssGetTableName("tb_para_security")
					+ " ) scrt "
					+ // hexiangrong 20071211 增加证券报价因子的处理
					"on vmkdatte.FSecurityCode = scrt.FSecurityCode"
					+ ") MKPrice where fvaldate<="
					+ dbl.sqlDate(date)
					+ " and fportcode='"
					+ PortCode
					+ "' and fcheckstate=1 "
					+

					" ) Market "
					//+ " on AccBalance.fcode= Market.FSecurityCode "
					+ strJoinPara   //add by jsc 20120308
					//--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
					+ " left join (select b.FOptionCode, b.FMultiple from " + pub.yssGetTableName("Tb_Para_OptionContract")
					+ " b where b.FCheckState = 1) QP on AccBalance.fcode = QP.FOptionCode " 
					/**shashijie 2012-3-13 STORY 2335 */
					+ getQHPathSQL(bShowGz,showDec,intMonth,date)
					/**end*/
					//--add by songjie 2011.05.25 BUG 1957 QDV4博时2011年05月19日02_B--//
					+
					// "where  AccBalance.fbendbal<>0 or AccBalance.FAEndBal<>0"
					// + //彭彪20071026权证送配，只有数量没有成本
					"where  AccBalance.fbendbal<>0 or AccBalance.FAEndBal<>0 or AccBalance.FEndBal<>0 "
					+ // 添加对期末余额的判断，QDV4建行2009年3月9日01_B MS00299 by leeyu
						// 20090312
					") GuessValue "
					+ " Left join "
					+ "("
					+ "Select Fcurycode , ROUND(Fbaserate, "
					+ iDigits
					+ ") as Fexrate1 from "
					+
					// 20100731 panjunfang modify
					// QDV4交银施罗德2010年07月30日01_B,通过通用参数判断 汇率是否保留13位小数参与市值计算
					// round函数默认只能保留14位小数，此处保留15位无任何意义
					"("
					+
					// 汇率是不要截位fazmm20070930
					// " select valRate.Fportcode,valRate.Fcurycode,valRate.Fvaldate,round(valRate.Fbaserate/valRate.FPortRate,8)as Fbaserate,valRate.Fcheckstate from "+pub.yssGetTableName("Tb_Data_ValRate")+" valRate "+
					// -------------- zhouss 20091012
					// valRate.Fbaserate/valRate.FPortRate,默认会只保留5位小数增加trunc函数计算中间汇率截取小数点前15位。
					// MS00748 QDV4赢时胜（上海）2009年10月13日01_B ----------
					
					//20130605 modified by liubo.Bug #8140.建行估值表的某只券的市值与净值表相差一分钱的问题
					//直接使用Sql的round或者trunc函数进行计算，无论计算结果本身的精度如何
					//都将返回一个包含整数和小数，一共15位的number，可能会丢失精度
					//使用to_char函数转换一下round和trunc函数的计算记过，将返回一个包含完整精度的数据。
					//而ORACLE在操作这个数据的时候将自动进行转换
					//===========================================
//					" select valRate.Fportcode,valRate.Fcurycode,valRate.Fvaldate,trunc(valRate.Fbaserate/valRate.FPortRate,15)as Fbaserate,valRate.Fcheckstate from "
					" select valRate.Fportcode,valRate.Fcurycode,valRate.Fvaldate," +
					" to_char(Trunc(valRate.Fbaserate/valRate.FPortRate,18))as Fbaserate,valRate.Fcheckstate from "
					//====================Bug 8140 end=======================
					+
					// ----------- end zhouss 20091012
					// -------------------------------------------------------------------------------------------------------------------------------------------------
					pub.yssGetTableName("Tb_Data_ValRate")
					+ " valRate "
					+ " inner join "
					+ "("
					+ " Select FPortCode,FCurYCode,Max(FValDate) as FValDate from "
					+ pub.yssGetTableName("Tb_Data_ValRate")
					+ " where Fvaldate<="
					+ dbl.sqlDate(date)
					+ " group by FPortCode,FCurYCode "
					+ " ) ValRateDate on ValRate.Fportcode=ValRateDate.Fportcode and ValRate.Fcurycode=ValRateDate.FcuryCode and ValRate.Fvaldate=ValRateDate.FvalDate "
					+ " ) Rat where Fportcode='"
					+ PortCode
					+ "' and Fvaldate<="
					+ dbl.sqlDate(date)
					+ " and Fcheckstate=1 "
					+ ") Rate"
					+ " on GuessValue.fcurcode=Rate.fcurycode"
					+ " left join (select * from " + pub.yssGetTableName("tb_rep_gvdatamodifying") + " where fdate = " + dbl.sqlDate(date) + ") modifying "
					+ " on GuessValue.facctcode = modifying.facctcode" ;
			sql += tempStr;
			/**shashijie 2012-3-13 STORY 2335 */
			//System.out.println(sql);
			/**end*/
			dbl.executeSql(sql);
			sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					//modify by wangzuochun 2011.06.17 BUG 1979 远期是没有成本的，在财务估值表中将成本都调整为0
					+ " select  GVSum.FPortCode,GVSum.FDate,GVSum.FAcctCode,GVSum.FCurCode,ac.facctname,ac.facctattr,ac.facctclass,GVSum.FExchangeRate,GVSum.FAmount," 
					+ " case when ac.facctattr like '远期%' then 0 else GVSum.FCost end as FCost, " 
					+ " case when ac.facctattr like '远期%' then 0 else GVSum.FSTANDARDMONEYCOST end as FSTANDARDMONEYCOST, " 
					+ " 0,0,GVSum.FMARKETPRICE,0,0,0,GVSum.FMarketValue,GVSum.FSTANDARDMONEYMARKETVALUE,0,0,GVSum.FAPPRECIATION,GVSum.FSTANDARDMONEYAPPRECIATION,' ',GVSum.FACCTLEVEL,GVSum.FACCTDETAIL,' ' as Fdesc "
					+ "from "
					+ "("
					+ " select FPortCode,FDate,FAcctCode,FCURCode,avg(FExchangeRate) as FExchangeRate,0 as FAmount,Sum(FCost) as FCost,Sum(FSTANDARDMONEYCOST) as FSTANDARDMONEYCOST,0 as FCOSTTONETRATIO,0 as FMARKETPRICE,Sum(FMarketValue) as FMarketValue,Sum(FSTANDARDMONEYMARKETVALUE) as FSTANDARDMONEYMARKETVALUE,0,Sum(FAPPRECIATION) as FAPPRECIATION,Sum(FSTANDARDMONEYAPPRECIATION) as FSTANDARDMONEYAPPRECIATION,' ' as FMARKETDESCRIBE,FACCTLEVEL-1 as FACCTLEVEL,FACCTDETAIL,sum(FOTPrice1) as FOTPrice1,sum(FOTPrice2) as FOTPrice2,sum(FOTPrice3) as FOTPrice3 "
					+ " from ("
					+ "Select FPortCode,FDate, "
					+ dbl.sqlLeft("FacctCode", dbl.sqlInstr("FacctCode", "'_'")
							+ "-1")
					+ " as FAcctCode,FCURCode,FExchangeRate,FAmount,FCost,FSTANDARDMONEYCOST,FCOSTTONETRATIO,FMARKETPRICE,FMarketValue,FOTPrice1,FOTPrice2,FOTPrice3,FSTANDARDMONEYMARKETVALUE,FMarketValueToRatio,FAPPRECIATION,FSTANDARDMONEYAPPRECIATION,FMARKETDESCRIBE,FACCTLEVEL,FACCTDETAIL  from "
					+ pub.yssGetTableName(sGVTableName)
					+ " Where FPORTCODE='"
					+ fSetCode
					+ "' and FDate="
					+ dbl.sqlDate(date)
					+ " and  "
					+ dbl.sqlInstr("FacctCode", "'_'")
					+ ">0 and facctdetail=1"
					+
					// modify by nimengjing 2010.12.30 BUG #774
					// 系统存在多组合时，设置显示估增科目的余额，生成财务估值表报错
					//edit by songjie 2012.04.26 BUG 4298 QDV4广发基金2012年4月16日01_B
					(bShowGz ? "" : " and facctattr not like '投资估值增值%'")
					+(showDec.equalsIgnoreCase("0")? "": " and facctattr not like '减值准备%'")//bug3252 yeshenghong
                    +(showDec.equalsIgnoreCase("0")? "":" and facctcode not like '150311%'")
					+ " ) GV group by FPortCode,FDate,FAcctCode,FCURCode,FACCTLEVEL,FACCTDETAIL"
					+
					// ------------------------------------end bug
					// #774-------------------------------------------------------------
					")GVSum inner join "
					+ YssTabPrefix
					+ "laccount ac on GVSum.FacctCode=ac.FAcctCode and GVSum.FCurCode=ac.Fcurcode";
			dbl.executeSql(sql);
			for (int i = AccLen.length(); i > 1; i--) {
				int len = 0;
				for (int j = 0; j < i - 1; j++) {
					len += Integer.parseInt(AccLen.substring(j, j + 1));
				}
				sql = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ " select  FPortCode,FDate,b.facctcode,a.fcurcode,b.facctname,b.facctattr,b.facctclass,avg(a.fexchangerate) as fexchangerate,"
						+ "sum(a.famount) as famount,sum(a.fcost) as fcost,sum(a.fstandardmoneycost) as fstandardmoneycost,sum(a.fcosttonetratio) as fcosttonetratio,"
						+ "sum(a.fstandardmoneycosttonetratio) as fstandardmoneycosttonetratio,avg(a.fmarketprice) as fmarketprice,avg(FOTPrice1) as FOTPrice1,avg(FOTPrice2) as FOTPrice2,avg(FOTPrice3) as FOTPrice3,"
						+ "sum(a.fmarketvalue) as fmarketvalue,sum(a.fstandardmoneymarketvalue) as fstandardmoneymarketvalue,sum(a.fmarketvaluetoratio) as fmarketvaluetoratio,"
						+ "sum(a.fstandardmoneymarketvaluetorat) as fstandardmoneymarketvaluetorat,sum(a.fappreciation)as fappreciation,"
						+ "sum(a.fstandardmoneyappreciation) as fstandardmoneyappreciation,' ' as fmarketdescribe,b.facctlevel,b.facctdetail,' ' as Fdesc from "
						+ pub.yssGetTableName(sGVTableName)
						+ " a inner join "
						+ YssTabPrefix
						+ "laccount b on "
						+ dbl.sqlLeft("a.facctcode", len)
						+ "=b.facctcode "
						+ "where "
						+ dbl.sqlInstr("a.facctcode", "'_'")
						+ "=0 and a.facctdetail=1 and a.FPortCode='"
						+ fSetCode
						+ "' and fdate="
						+ dbl.sqlDate(date)
						+ " and  "
						+ dbl.sqlLen("a.facctcode")
						+ ">"
						+ len
						//STORY #1789 恒指ETF 可退替代款估值增值不能排除 panjunfang modify 20120524
						+ " and (a.facctattr not like '投资估值增值%' or a.facctattr like '%可退替代款%')";
						if(showDec.equalsIgnoreCase("1"))
						{
							sql += " and b.facctattr not like '减值准备%'"; // modified by yeshenghong #3252 20111110 减值准备不参与资产类合计
							sql += " and b.facctcode not like '150311%'"; 
						}
						 // 上面采用所有科目（包括估值增值）的值都插入到估值表，那么这边通过合计项时就不用加上估值增值fazmm20070922
						sql += " group by FPortCode,FDate,b.facctcode,a.fcurcode,b.facctname,b.facctattr,b.facctclass,b.facctlevel,b.facctdetail";

				dbl.executeSql(sql);
			}
			// -------------------通过通用参数获取是否包括清算款的信息。sj edit 20080617
			// --------------------------------------------------------------------------------------------------------------
			pubpara = new CtlPubPara();
			pubpara.setYssPub(pub);
			if (pubpara.getCashLiqu(this.sPortCode).equalsIgnoreCase("Yes")) { // 包括清算款。
				sql = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ ",'8600' facctcode,fcurcode,'可用头寸' as facctname,facctclass as facctattr,'合计' as facctclass,0,0,0,sum(fstandardmoneycost) as fstandardmoneycost,0,0,0,0,0,0,0,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,0,0,0,0,' ',0,0,' ' "
						+ " from"
						+ "("
						+ " select ' ' as fcurcode,'可用头寸'  as facctclass,fstandardmoneycost,fstandardmoneymarketvalue  from "
						+ pub.yssGetTableName(sGVTableName)
						+ " where facctdetail=1 and  "
						+ dbl.sqlInstr("facctcode", "'_'")
						+ "=0  and FPortCode='"
						+ fSetCode
						+ "' and fdate="
						+ dbl.sqlDate(date)
						+ " and (facctattr like '银行存款%' or facctattr like '证券清算款%')"
						+ ") guvalue group by fcurcode, facctclass";
				dbl.executeSql(sql);
				sql = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ ",'8600' facctcode,fcurcode,'可用头寸' as facctname,facctclass as facctattr,'合计'  as facctclass,0,0,sum(fcost) as fcost,sum(fstandardmoneycost) as fstandardmoneycost,0,0,0,0,0,0,sum(fmarketvalue) as fmarketvalue,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,0,0,0,0,' ',0,0,' ' "
						+ " from"
						+ "("
						+ " select fcurcode,'可用头寸'  as facctclass,fcost,fstandardmoneycost,fmarketvalue,fstandardmoneymarketvalue  from "
						+ pub.yssGetTableName(sGVTableName)
						+ " where facctdetail=1 and  "
						+ dbl.sqlInstr("facctcode", "'_'")
						+ "=0   and (facctattr like '银行存款%' or facctattr like '证券清算款%')  and FPortCode='"
						+ fSetCode + "' and fdate=" + dbl.sqlDate(date) + ""
						+ ") guvalue group by fcurcode, facctclass";
				dbl.executeSql(sql); // 彭彪20071020
										// 嘉实要求注掉可用头寸，如其他家有需要增加可用头寸的，需增加参数选择。没有就这样了。
			} else if (pubpara.getCashLiqu(this.sPortCode).equalsIgnoreCase(
					"No")) { // 不包括清算款。
				sql = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ ",'8600' facctcode,fcurcode,'可用头寸' as facctname,facctclass as facctattr,'合计' as facctclass,0,0,0,sum(fstandardmoneycost) as fstandardmoneycost,0,0,0,0,0,0,0,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,0,0,0,0,' ',0,0,' ' "
						+ " from"
						+ "("
						+ " select ' ' as fcurcode,'可用头寸'  as facctclass,fstandardmoneycost,fstandardmoneymarketvalue  from "
						+ pub.yssGetTableName(sGVTableName)
						+ " where facctdetail=1 and  "
						+ dbl.sqlInstr("facctcode", "'_'")
						+ "=0  and FPortCode='" + fSetCode + "' and fdate="
						+ dbl.sqlDate(date) + " and (facctattr like '银行存款%')"
						+ ") guvalue group by fcurcode, facctclass";
				dbl.executeSql(sql);
				sql = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ ",'8600' facctcode,fcurcode,'可用头寸' as facctname,facctclass as facctattr,'合计'  as facctclass,0,0,sum(fcost) as fcost,sum(fstandardmoneycost) as fstandardmoneycost,0,0,0,0,0,0,sum(fmarketvalue) as fmarketvalue,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,0,0,0,0,' ',0,0,' ' "
						+ " from"
						+ "("
						+ " select fcurcode,'可用头寸'  as facctclass,fcost,fstandardmoneycost,fmarketvalue,fstandardmoneymarketvalue  from "
						+ pub.yssGetTableName(sGVTableName)
						+ " where facctdetail=1 and  "
						+ dbl.sqlInstr("facctcode", "'_'")
						+ "=0   and (facctattr like '银行存款%')  and FPortCode='"
						+ fSetCode + "' and fdate=" + dbl.sqlDate(date) + ""
						+ ") guvalue group by fcurcode, facctclass";
				dbl.executeSql(sql);
			}
			// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'实收资本','实收资本','合计',0,sum(faendbal) as faendbal,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,0,0,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '8700' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode   where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					intMonth
					+
					// ------------------------------------------------------------------------------
					" and  facctattr like '实收资本%' and facctdetail=1 "
					+ " union all "
					+ " select  '8700' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c " 
					+ "inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+
					// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where (c.fdate>=" + dbl.sqlDate(StartDate)
							+ " and c.fdate< " + dbl.sqlDate(date)
							+ " and  facctattr like '实收资本%')  OR (c.FDate = "
							+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '实收资本%')")
							: ("where c.fdate>=" + dbl.sqlDate(StartDate)
									+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '实收资本%'"))
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
			dbl.executeSql(sql);
			sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'实收资本','实收资本','合计',0,sum(faendbal) as faendbal,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,0,0,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '8700' as facctcode,a.fcurcode,(faendbal*fbaldc) as faendbal ,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode   where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					intMonth
					+
					// ----------------
					" and  facctattr like '实收资本%' and facctdetail=1 "
					+ " union all "
					+ " select  '8700' facctcode,c.fcyid as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c " 
					+ "inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+
					// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where (c.fdate>=" + dbl.sqlDate(StartDate)
							+ " and c.fdate< " + dbl.sqlDate(date)
							+ " and  facctattr like '实收资本%') OR (c.FDate = "
							+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '实收资本%')")
							: ("where c.fdate>=" + dbl.sqlDate(StartDate)
									+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '实收资本%'"))
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null)" //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
			dbl.executeSql(sql);
			
			
			sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)//ysh
					+ ",case when facctclass='资产类' then '8800' else '8801' end facctcode,' ',case when facctclass='资产类' then '资产类合计' else '负债类合计' end facctname,facctclass as facctattr,'合计' as facctclass,0,0,0,sum(fstandardmoneycost) as fstandardmoneycost,0,0,0,0,0,0,0,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,0,0,0,0,' ',0,0,' '"
					+ " from "
					+ "("
					// MS00393 QDV4招商证券2009年04月17日01_B
					// 在判断为资产类时，增加等于的判断。当为股指期货是原币成本为0 ---------------
					//modify by fangjiang 2011.08.08 BUG 2329
					
					//20121116 modified by liubo.Story #3273
					//在统计资产合计跟负债合计时，修改“以成本、市值的正负数做为条件，将共同类科目带入资产和负债中”的逻辑
					//改为依据科目表的“统计类别（FGVStat）”的值作为条件，将共同类的科目带入资产和负债中，具体逻辑：
					//某个科目的统计类别值为0，表示不参与统计
					//统计类别为1，表示参与资产类的合计，不考虑成本、市值的正负数
					//统计类别为2，表示参与负债类的合计，不考虑成本、市值的正负数
					//统计类别为3，表示按照旧有的“以成本、市值的正负数做为条件，将共同类科目带入资产和负债中”的逻辑带入资产、负债中
					//=====================================
//					" select case when facctclass='共同类' and fstandardmoneymarketvalue>=0 then '资产类' when  facctclass='共同类' and fstandardmoneymarketvalue<0 then '负债类' else facctclass end as facctclass,fstandardmoneycost,fstandardmoneymarketvalue  from "
					+ " select case when gv.facctclass = '共同类' then "
					+ " case when acct.fgvstat = '3' then "
					+ " case when gv.facctclass='共同类' and fstandardmoneymarketvalue>=0 then '资产类' when  gv.facctclass='共同类' and fstandardmoneymarketvalue<0 then '负债类' else gv.facctclass end"
					+ " when acct.fgvstat = '1' then '资产类' "
					+ " when acct.fgvstat = '2' then '负债类' "
					+ " end else gv.facctclass end as facctclass,"
					+ " fstandardmoneycost,fstandardmoneymarketvalue  from "
					// ------------------------------------------------------------------------------------------------------
					
					+ pub.yssGetTableName(sGVTableName)
					+" gv left join (select * from " + YssTabPrefix + "laccount where FAcctClass = '共同类') acct on gv.facctcode = acct.facctcode " +
					// 如果共同类入资产类、负债类合计的级别用户进行了设置 ,共同类不根据明细只根据级别计算 sunkey
					// 20081211 bugNO:MS00072
					(acctLevel >= 1 ? " where gv.facctdetail = (case when gv.facctclass = '共同类' then gv.facctdetail else 1 end) "
							+ " and gv.facctlevel = (case when gv.facctclass = '共同类' then "
							+ acctLevel + " else gv.facctlevel end) "
							: " where gv.facctdetail=1 ")
					+
					//================end=====================
					
					// ----------------------------------------------------------------------------------------------------
					"and  " + dbl.sqlInstr("gv.facctcode", "'_'") + "=0  and "
					+ dbl.sqlInstr("gv.facctcode", "gv.facctclass")
					+ "=0  " 
					+ "and 1 = (case when gv.facctclass = '共同类' then case when acct.fgvstat = '0' then '2' else '1' end else '1' end)"
					+ " and FPortCode='" + fSetCode + "' and fdate="
					+ dbl.sqlDate(date) ;

			//20121123 deleted by liubo.Story #3273
			//财务估值表中共同类完全按照财务系统中科目设置的统计分类设置来统计，现有代码中的写死的都可以去掉
			//+++++++++++++++++++++++++++++++++++++++++++
//					+ " and gv.facctattr not like '投资估值增值%'"
//					+ " and gv.facctcode not like '2161%'"; //add baopingping #story 1294 20110805 2161相关科目 不参与资产净值的核对
			
					//20121116 deleted by liubo.Story #3273
					//对共同类的科目的排除，将只按照统计类别进行
					//************************************
			
					//STORY #1789 恒指ETF 可退替代款估值增值不能排除 panjunfang modify 20120806
//					+ " or facctattr like '%可退替代款%')"
					
					//20120315 added by liubo.Story #2336
					//==================================
//					+ " and not (facctcode like '3102%' and (FAcctAttr like '%其他衍生工具_股指期货_初始%' or FAcctAttr like '%其他衍生工具_股指期货_冲抵%'))"
//					+ " and not (facctcode like '3003%'  and FAcctAttr like '%证券清算款_股指期货%')";
					//================end==================

					//*****************end*******************
//					if(showDec.equalsIgnoreCase("1"))
//					{
//						sql += " and facctattr not like '减值准备%'"; // modified by yeshenghong #1652 20111110 减值准备不参与资产类合计
//					}
			//++++++++++++++++++++end+++++++++++++++++++++++
					sql += " ) guvalue group by facctclass ";
			dbl.executeSql(sql);
			sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",case when facctclass='资产类' then '8800' else '8801' end facctcode,fcurcode,case when facctclass='资产类' then '资产类合计' else '负债类合计' end facctname,facctclass as facctattr,'合计' as facctclass,0,0,sum(fcost) as fcost,sum(fstandardmoneycost) as fstandardmoneycost,0,0,0,0,0,0,sum(fmarketvalue) as fmarketvalue,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,0,0,0,0,' ',0,0,' '"
					+ " from "
					+ "("
					// MS00393 QDV4招商证券2009年04月17日01_B
					// 在判断为资产类时，增加等于的判断。当为股指期货是组合成本为0 ---------------
					//modify by fangjiang 2011.08.08 BUG 2329
					
					//20121116 modified by liubo.Story #3273
					//在统计资产合计跟负债合计时，修改“以成本、市值的正负数做为条件，将共同类科目带入资产和负债中”的逻辑
					//改为依据科目表的“统计类别（FGVStat）”的值作为条件，将共同类的科目带入资产和负债中，具体逻辑：
					//某个科目的统计类别值为0，表示不参与统计
					//统计类别为1，表示参与资产类的合计，不考虑成本、市值的正负数
					//统计类别为2，表示参与负债类的合计，不考虑成本、市值的正负数
					//统计类别为3，表示按照旧有的“以成本、市值的正负数做为条件，将共同类科目带入资产和负债中”的逻辑带入资产、负债中
					//=====================================
//					" select fcurcode, case when facctclass='共同类' and fstandardmoneymarketvalue>=0 then '资产类' when  facctclass='共同类' and fstandardmoneymarketvalue<0 then '负债类' else facctclass end as facctclass,fcost,fstandardmoneycost,fmarketvalue,fstandardmoneymarketvalue  from "
					+ " select gv.FCurCode,case when gv.facctclass = '共同类' then "
					+ " case when acct.fgvstat = '3' then "
					+ " case when gv.facctclass='共同类' and fstandardmoneymarketvalue>=0 then '资产类' when  gv.facctclass='共同类' and fstandardmoneymarketvalue<0 then '负债类' else gv.facctclass end"
					+ " when acct.fgvstat = '1' then '资产类' "
					+ " when acct.fgvstat = '2' then '负债类' "
					+ " end else gv.facctclass end as facctclass,"
					+ " fcost,fstandardmoneycost,fmarketvalue,fstandardmoneymarketvalue  from "
					// ---------------------------------------------------------------------------------------------------------
					+ pub.yssGetTableName(sGVTableName)
					+" gv left join (select * from " + YssTabPrefix + "laccount where FAcctClass = '共同类') acct on gv.facctcode = acct.facctcode "
					+
					// 如果共同类入资产类、负债类合计的级别用户进行了设置 ,共同类不根据明细只根据级别计算 sunkey
					// 20081211 bugNO:MS00072
					(acctLevel >= 1 ? " where gv.facctdetail = (case when gv.facctclass = '共同类' then gv.facctdetail else 1 end) "
							+ " and gv.facctlevel = (case when gv.facctclass = '共同类' then "
							+ acctLevel + " else gv.facctlevel end) "
							: " where gv.facctdetail=1 ")
					+
					//================end=====================
					// ----------------------------------------------------------------------------------------------------
					"and  " + dbl.sqlInstr("gv.facctcode", "'_'")
					+ "=0  and FPortCode='" + fSetCode + "' and fdate="
					+ dbl.sqlDate(date) + "  and gv.facctattr not like '投资估值增值%'"
					+ "and 1 = (case when gv.facctclass = '共同类' then case when acct.fgvstat = '0' then '2' else '1' end else '1' end)";
			
			//20121123 deleted by liubo.Story #3273
			//财务估值表中共同类完全按照财务系统中科目设置的统计分类设置来统计，现有代码中的写死的都可以去掉
			//+++++++++++++++++++++++++++++++++++++++++++
					//STORY #1789 恒指ETF 可退替代款估值增值不能排除 panjunfang modify 20120806
//					+ " or facctattr like '%可退替代款%')"
//					+" and gv.facctcode not like '2161%'"; //add baopingping #story 1294 20110805 2161相关科目 不参与资产净值的核对
			
					//20121116 deleted by liubo.Story #3273
					//对共同类的科目的排除，将只按照统计类别进行
					//************************************
			
					//20120315 added by liubo.Story #2336
					//==================================
//					+ " and not (facctcode like '3102%' and (FAcctAttr like '%其他衍生工具_股指期货_初始%' or FAcctAttr like '%其他衍生工具_股指期货_冲抵%'))"
//					+ " and not (facctcode like '3003%'  and FAcctAttr like '%证券清算款_股指期货%')";
					//================end==================
			
					//***************end*********************
			
//					if(showDec.equalsIgnoreCase("1"))
//					{
//						sql += " and gv.facctattr not like '减值准备%'"; // modified by yeshenghong #1652 20111110 减值准备不参与资产类合计
//					}

			//+++++++++++++++++++++++end++++++++++++++++++++
					sql += " ) guvalue group by facctclass,fcurcode";
			dbl.executeSql(sql);
			// 彭彪20070115 增加显示损益平准金

			sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'未实现损益平准金','未实现损益平准金','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '8803' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode   where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					intMonth
					+
					// ----------------------------------------------------
					" and  facctattr like '损益平准金_未实现%' and facctdetail=1 "
					+ " union all "
					+ " select  '8803' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c " 
					+ "inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+
					// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where (c.fdate>="
							+ dbl.sqlDate(StartDate)
							+ " and c.fdate< "
							+ dbl.sqlDate(date)
							+ " and  facctattr like '损益平准金_未实现%') OR (c.FDate = "
							+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '损益平准金_未实现%')")
							: ("where c.fdate>=" + dbl.sqlDate(StartDate)
									+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '损益平准金_未实现%'"))
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
			dbl.executeSql(sql);
			sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'  —申购','未实现损益平准金','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '8804' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode   where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					intMonth
					+
					// ---------------------------------------------------
					" and  facctattr like '损益平准金_未实现_申购' and facctdetail=1 "
					+ " union all "
					+ " select  '8804' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c " 
					+ "inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+
					// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where (c.fdate>="
							+ dbl.sqlDate(StartDate)
							+ " and c.fdate< "
							+ dbl.sqlDate(date)
							+ " and  facctattr like '损益平准金_未实现_申购') OR (c.FDate = "
							+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '损益平准金_未实现_申购')")
							: ("where c.fdate>=" + dbl.sqlDate(StartDate)
									+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '损益平准金_未实现_申购'"))
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
			dbl.executeSql(sql);
			sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'  —赎回','未实现损益平准金','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '8805' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode   where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					intMonth
					+
					// ---------------------------------------------------
					" and  facctattr like '损益平准金_未实现_赎回' and facctdetail=1 "
					+ " union all "
					+ " select  '8805' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c " 
					+ " inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+
					// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where (c.fdate>="
							+ dbl.sqlDate(StartDate)
							+ " and c.fdate< "
							+ dbl.sqlDate(date)
							+ " and  facctattr like '损益平准金_未实现_赎回') OR (c.FDate = "
							+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '损益平准金_未实现_赎回')")
							: ("where c.fdate>=" + dbl.sqlDate(StartDate)
									+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '损益平准金_未实现_赎回'"))
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
			dbl.executeSql(sql);
			/**add---huhuichao 2013-10-9 BUG  80201 显示财务估值表统计项中未实现损益平准金和已实现损益平准金的子项转入、转出明细项*/
			//未实现损益平准金_转入
			sql = " insert into  "
				+ pub.yssGetTableName(sGVTableName)
				+ " select '"
				+ fSetCode
				+ "',"
				+ dbl.sqlDate(date)
				+ " ,facctcode,fcurcode,'  —转入','未实现损益平准金','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as" +
						" fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
				+ "("
				+ " select '880501' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as" +
						" fendbal,(fbendbal*fbaldc) as fbendbal from "
				+ YssTabPrefix
				+ "lbalance  a inner join "
				+ YssTabPrefix
				+ "laccount b on a.facctcode=b.facctcode   where fmonth="
				+
				// --MS00348 QDV4招商证券2009年04月01日01_B
				// 使用调整的月份---------------------------
				intMonth
				+
				// ---------------------------------------------------
				" and  facctattr like '损益平准金_未实现_转入' and facctdetail=1 "
				+ " union all "
				+ " select  '880501' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as" +
						" faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as" +
						" fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
				+ YssTabPrefix
				+ "fcwvch  c " 
				+ "inner join "
				+ YssTabPrefix
				+ "laccount d on c.fkmh=d.facctcode "
				+
				// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
				// 2009-11-03 蒋锦 添加 sGVType MS00004
				// QDV4.1赢时胜（上海）2009年9月28日03_A
				(bIsETFGV ? ("where (c.fdate>="
						+ dbl.sqlDate(StartDate)
						+ " and c.fdate< "
						+ dbl.sqlDate(date)
						+ " and  facctattr like '损益平准金_未实现_转入') OR (c.FDate = "
						+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
						+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '损益平准金_未实现_转入')")
						: ("where c.fdate>=" + dbl.sqlDate(StartDate)
								+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '损益平准金_未实现_转入'"))
				+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
				+ " ) balance group by facctcode,fcurcode";
		     dbl.executeSql(sql);
		   //未实现损益平准金_转出
		     sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'  —转出','未实现损益平准金','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as" +
							" fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '880502' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as" +
							" fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode   where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					intMonth
					+
					// ---------------------------------------------------
					" and  facctattr like '损益平准金_未实现_转出' and facctdetail=1 "
					+ " union all "
					+ " select  '880502' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc " +
							"as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc " +
							"as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c " 
					+ "inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+
					// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where (c.fdate>="
							+ dbl.sqlDate(StartDate)
							+ " and c.fdate< "
							+ dbl.sqlDate(date)
							+ " and  facctattr like '损益平准金_未实现_转出') OR (c.FDate = "
							+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '损益平准金_未实现_转出')")
							: ("where c.fdate>=" + dbl.sqlDate(StartDate)
									+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '损益平准金_未实现_转出'"))
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
			     dbl.executeSql(sql);
			/**end---huhuichao 2013-10-9 BUG  80201*/
			sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'已实现损益平准金','已实现损益平准金','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '8806' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode   where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					intMonth
					+
					// ---------------------------------------------------
					" and  facctattr like '损益平准金_已实现%' and facctdetail=1 "
					+ " union all "
					+ " select  '8806' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c " 
					+ " inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+
					// 如果生成 ETF 估值 表 ， 不查?乐等掌诘碧斓腅TF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where (c.fdate>="
							+ dbl.sqlDate(StartDate)
							+ " and c.fdate< "
							+ dbl.sqlDate(date)
							+ " and  facctattr like '损益平准金_已实现%') OR (c.FDate = "
							+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '损益平准金_已实现%')")
							: ("where c.fdate>=" + dbl.sqlDate(StartDate)
									+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '损益平准金_已实现%'"))
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
			dbl.executeSql(sql);
			sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'  —申购','已实现损益平准金','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '8807' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode   where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					intMonth
					+
					// ---------------------------------------------------
					" and  facctattr like '损益平准金_已实现_申购' and facctdetail=1 "
					+ " union all "
					+ " select  '8807' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c " 
					+ "inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+
					// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where (c.fdate>="
							+ dbl.sqlDate(StartDate)
							+ " and c.fdate< "
							+ dbl.sqlDate(date)
							+ " and  facctattr like '损益平准金_已实现_申购') OR (c.FDate = "
							+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '损益平准金_已实现_申购')")
							: ("where c.fdate>=" + dbl.sqlDate(StartDate)
									+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '损益平准金_已实现_申购'"))
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
			dbl.executeSql(sql);
			sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'  —赎回','已实现损益平准金','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '8808' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode   where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					intMonth
					+
					// ---------------------------------------------------
					" and  facctattr like '损益平准金_已实现_赎回' and facctdetail=1 "
					+ " union all "
					+ " select  '8808' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c " 
					+ "inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+
					// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where (c.fdate>="
							+ dbl.sqlDate(StartDate)
							+ " and c.fdate< "
							+ dbl.sqlDate(date)
							+ " and  facctattr like '损益平准金_已实现_赎回') OR (c.FDate = "
							+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '损益平准金_已实现_赎回')")
							: ("where c.fdate>=" + dbl.sqlDate(StartDate)
									+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '损益平准金_已实现_赎回'"))
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
			dbl.executeSql(sql);
			/**add---huhuichao 2013-10-9 BUG  80201 显示财务估值表统计项中未实现损益平准金和已实现损益平准金的子项转入、转出明细项*/
			//已实现损益平准金_转入
			sql = " insert into  "
				+ pub.yssGetTableName(sGVTableName)
				+ " select '"
				+ fSetCode
				+ "',"
				+ dbl.sqlDate(date)
				+ " ,facctcode,fcurcode,'  —转入','已实现损益平准金','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) " +
						"as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
				+ "("
				+ " select '880801' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) " +
						"as fendbal,(fbendbal*fbaldc) as fbendbal from "
				+ YssTabPrefix
				+ "lbalance  a inner join "
				+ YssTabPrefix
				+ "laccount b on a.facctcode=b.facctcode   where fmonth="
				+
				// --MS00348 QDV4招商证券2009年04月01日01_B
				// 使用调整的月份---------------------------
				intMonth
				+
				// ---------------------------------------------------
				" and  facctattr like '损益平准金_已实现_转入' and facctdetail=1 "
				+ " union all "
				+ " select  '880801' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc " +
						"as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
				+ YssTabPrefix
				+ "fcwvch  c " 
				+ "inner join "
				+ YssTabPrefix
				+ "laccount d on c.fkmh=d.facctcode "
				+
				// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
				// 2009-11-03 蒋锦 添加 sGVType MS00004
				// QDV4.1赢时胜（上海）2009年9月28日03_A
				(bIsETFGV ? ("where (c.fdate>="
						+ dbl.sqlDate(StartDate)
						+ " and c.fdate< "
						+ dbl.sqlDate(date)
						+ " and  facctattr like '损益平准金_已实现_转入') OR (c.FDate = "
						+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
						+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '损益平准金_已实现_转入')")
						: ("where c.fdate>=" + dbl.sqlDate(StartDate)
								+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '损益平准金_已实现_转入'"))
				+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
				+ " ) balance group by facctcode,fcurcode";
		dbl.executeSql(sql);
		//已实现损益平准金_转出
		sql = " insert into  "
			+ pub.yssGetTableName(sGVTableName)
			+ " select '"
			+ fSetCode
			+ "',"
			+ dbl.sqlDate(date)
			+ " ,facctcode,fcurcode,'  —转出','已实现损益平准金','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) " +
					"as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
			+ "("
			+ " select '880802' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) " +
					"as fendbal,(fbendbal*fbaldc) as fbendbal from "
			+ YssTabPrefix
			+ "lbalance  a inner join "
			+ YssTabPrefix
			+ "laccount b on a.facctcode=b.facctcode   where fmonth="
			+
			// --MS00348 QDV4招商证券2009年04月01日01_B
			// 使用调整的月份---------------------------
			intMonth
			+
			// ---------------------------------------------------
			" and  facctattr like '损益平准金_已实现_转出' and facctdetail=1 "
			+ " union all "
			+ " select  '880802' facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc " +
					"as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc " +
					"as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
			+ YssTabPrefix
			+ "fcwvch  c " 
			+ "inner join "
			+ YssTabPrefix
			+ "laccount d on c.fkmh=d.facctcode "
			+
			// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
			// 2009-11-03 蒋锦 添加 sGVType MS00004
			// QDV4.1赢时胜（上海）2009年9月28日03_A
			(bIsETFGV ? ("where (c.fdate>="
					+ dbl.sqlDate(StartDate)
					+ " and c.fdate< "
					+ dbl.sqlDate(date)
					+ " and  facctattr like '损益平准金_已实现_转出') OR (c.FDate = "
					+ dbl.sqlDate(date) + " AND c.Fzqjyfs <> "
					+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + " and  facctattr like '损益平准金_已实现_转出')")
					: ("where c.fdate>=" + dbl.sqlDate(StartDate)
							+ " and c.fdate<= " + dbl.sqlDate(date) + " and  facctattr like '损益平准金_已实现_转出'"))
			+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
			+ " ) balance group by facctcode,fcurcode";
	        dbl.executeSql(sql);
			/**end---huhuichao 2013-10-9 BUG  80201*/

				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'9000' facctcode,fcurcode,'资产净值' as facctname,facctclass as facctattr,'合计' as facctclass,0,0,0,sum(fstandardmoneycost) as fstandardmoneycost,0,0,0,0,0,0,0,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,0,0,0,0,' ',0,0,' ' "
					+ " from"
					+ "("
					+ " select ' ' as fcurcode,'资产净值'  as facctclass,fstandardmoneycost,fstandardmoneymarketvalue  from "
					+ pub.yssGetTableName(sGVTableName)
					+ " where facctdetail=1 and "
					+ dbl.sqlInstr("facctcode", "facctclass") + "=0  and  "
					+ dbl.sqlInstr("facctcode", "'_'") + "=0  and FPortCode='"
					+ fSetCode + "' and fdate=" + dbl.sqlDate(date)
					//STORY #1789 恒指ETF 可退替代款估值增值不能排除 panjunfang modify 20120524
					+ "  and (facctattr not like '投资估值增值%' or facctattr like '%可退替代款%')"
					+" and facctcode not like '2161%'" //add baopingping #story 1294 20110805 2161相关科目 不参与资产净值的核对
					//20120411 added by liubo.Bug #4259
					//==================================
					+ " and not (facctcode like '3102%' and (FAcctAttr like '%其他衍生工具_股指期货_初始%' or FAcctAttr like '%其他衍生工具_股指期货_冲抵%'))"
					+ " and not (facctcode like '3003%'  and FAcctAttr like '%证券清算款_股指期货%')";
					//================end==================
					if(showDec.equalsIgnoreCase("1"))
					{
						sql += " and facctattr not like '减值准备%'"; // modified by yeshenghong #1652 20111110 减值准备不参与资产类合计
					}
					sql += ") guvalue group by fcurcode, facctclass";
			dbl.executeSql(sql);
			
				
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'9000' facctcode,fcurcode,'资产净值' as facctname,facctclass as facctattr,'合计'  as facctclass,0,0,sum(fcost) as fcost,sum(fstandardmoneycost) as fstandardmoneycost,0,0,0,0,0,0,sum(fmarketvalue) as fmarketvalue,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,0,0,0,0,' ',0,0,' ' "
					+ " from"
					+ "("
					+ " select fcurcode,'资产净值'  as facctclass,fcost,fstandardmoneycost,fmarketvalue,fstandardmoneymarketvalue  from "
					+ pub.yssGetTableName(sGVTableName)
					+ " where facctdetail=1 and "
					+ dbl.sqlInstr("facctcode", "facctclass") + "=0  and  "
					+ dbl.sqlInstr("facctcode", "'_'")
					+ "=0    and FPortCode='" + fSetCode + "' and fdate="
					//STORY #1789 恒指ETF 可退替代款估值增值不能排除 panjunfang modify 20120524
					+ dbl.sqlDate(date) + "  and (facctattr not like '投资估值增值%' or facctattr like '%可退替代款%')"
					+" and facctcode not like '2161%'" //add baopingping #story 1294 20110805 2161相关科目 不参与资产净值的核对
					//20120411 added by liubo.Bug #4259
					//==================================
					+ " and not (facctcode like '3102%' and (FAcctAttr like '%其他衍生工具_股指期货_初始%' or FAcctAttr like '%其他衍生工具_股指期货_冲抵%'))"
					+ " and not (facctcode like '3003%'  and FAcctAttr like '%证券清算款_股指期货%')";
					//================end==================
					if(showDec.equalsIgnoreCase("1"))
					{
						sql += " and facctattr not like '减值准备%'"; // modified by yeshenghong #1652 20111110 减值准备不参与资产类合计
					}
					sql += ") guvalue group by fcurcode, facctclass";

			dbl.executeSql(sql);

			// -------------------获取资产净值的值,用于计算占净值比例 sj 20080221 add
			// ----------------------------------------------------------------


				sql = " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'9000' facctcode,fcurcode,'资产净值' as facctname,facctclass as facctattr,'合计' as facctclass,0,0,0,sum(fstandardmoneycost) as fstandardmoneycost,0,0,0,0,0,0,0,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,0,0,0,0,' ',0,0,' ' "
					+ " from"
					+ "("
					+ " select ' ' as fcurcode,'资产净值'  as facctclass,fstandardmoneycost,fstandardmoneymarketvalue  from "
					+ pub.yssGetTableName(sGVTableName)
					+ " where facctdetail=1 and "
					+ dbl.sqlInstr("facctcode", "facctclass") + "=0  and  "
					+ dbl.sqlInstr("facctcode", "'_'") + "=0  and FPortCode='"
					+ fSetCode + "' and fdate=" + dbl.sqlDate(date)
					//STORY #1789 恒指ETF 可退替代款估值增值不能排除 panjunfang modify 20120524
					+ "  and (facctattr not like '投资估值增值%' or facctattr like '%可退替代款%')"
					+(showDec.equalsIgnoreCase("0")? "": " and facctattr not like '减值准备%'")//bug3252 yeshenghong
                    +(showDec.equalsIgnoreCase("0")? "":" and facctcode not like '150311%'")					
                    //20120411 added by liubo.Bug #4259
					//==================================
					+ " and not (facctcode like '3102%' and (FAcctAttr like '%其他衍生工具_股指期货_初始%' or FAcctAttr like '%其他衍生工具_股指期货_冲抵%'))"
					+ " and not (facctcode like '3003%'  and FAcctAttr like '%证券清算款_股指期货%')"
					//================end==================
					+ ") guvalue group by fcurcode, facctclass";

			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788

			if (rs.next()) {
				totalMarketValue = rs.getDouble("fstandardmoneymarketvalue");
				// totalCost = rs.getDouble("fstandardmoneycost");
			}
			
			//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
			dbl.closeResultSetFinal(rs);
			
			// --------------------------------------------------------------------------------------------------------
			insertInvestNav(date, PortCode, fSetCode);
			sql = "select * from "
					+ pub.yssGetTableName(sGVTableName)
					+ " where facctcode='9000' and FPortCode='"
					+ fSetCode
					+ "' and fdate="
					+ dbl.sqlDate(date)
					+ " and (fmarketvalue <> 0 or fstandardmoneycost <> 0 or fcost <> 0 or fstandardmoneymarketvalue <> 0)";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				// ---- MS00621 QDV4中金2009年08月06日01_B sj
				// ----------------------------
				port = new PortfolioBean();
				port.setYssPub(pub);
				port.setPortCode(PortCode);
				port.getSetting(); // 以上代码用于获取组合信息
				if (!rs.getString("FCurCode").equalsIgnoreCase(
						port.getCurrencyCode())
						&& rs.getString("FCurCode").trim().length() > 0) { // 判断净值数据中的货币是否为组合货币，并且对无货币的情况不进行处理
					continue; // 若为组合货币，则跳过，不执行。
				}
				// -----------------------------------------------------------------------
				if (rs.getString("fcurcode").trim().length() > 0
						&& rs.getDouble("fmarketvalue") != 0
						&& rs.getDouble("fcost") != 0) {
					sql = "update "
							+ pub.yssGetTableName(sGVTableName)
							+ " set  fmarketvaluetoratio=abs(round(fmarketvalue/"
							+ YssFun.formatNumber(rs.getDouble("fmarketvalue"),
									"0.00######")
							+ ",4)),fcosttonetratio=abs(round(fcost/"
							+ YssFun.formatNumber(rs.getDouble("fcost"),
									"0.00######")
							+ ",4))  Where facctclass<>'合计'  and FPortCode='"
							+ fSetCode + "' and fdate=" + dbl.sqlDate(date)
							+ "  and fcurcode='" + rs.getString("fcurcode")
							+ "'";
				}
				// else if(rs.getDouble("fmarketvalue")!=0
				// &&rs.getDouble("fcost")!=0) {//应该判断的是本位币的值，而不是原币的值 sj
				// 20080408 edit
				else if (rs.getDouble("fstandardmoneymarketvalue") != 0
						&& rs.getDouble("fstandardmoneycost") != 0) {
					// modify by fangjiang 2010.10.18 MS01767
					// QDV4易方达2010年9月20日01_A
					sql = "update "
							+ pub.yssGetTableName(sGVTableName)
							+ " set fstandardmoneycosttonetratio=round(fstandardmoneycost/"
							+ YssFun.formatNumber(rs
									.getDouble("fstandardmoneycost"),
									"0.00######")
							+ ",4),fstandardmoneymarketvaluetorat=round(fstandardmoneymarketvalue/"
							+ YssFun.formatNumber(rs
									.getDouble("fstandardmoneymarketvalue"),
									"0.00######")
							+ ",4) Where facctclass<>'合计'  and FPortCode='"
							+ fSetCode + "' and fdate=" + dbl.sqlDate(date);
					// ----------------------------------
				}
				dbl.executeSql(sql);
				dbl.loadConnection().commit();
			}
			dbl.closeResultSetFinal(rs);
			sql = "select * from " + YssTabPrefix
					+ "laccount where facctattr like '实收资本%' and facctlevel=1";
			rs = dbl.openResultSet_antReadonly(sql);
			if (rs.next()) {
				// --MS00348 QDV4招商证券2009年04月01日01_B
				// 使用调整的月份---------------------------
				double[] dou = getAccBalance(date, fSetCode, rs
						.getString("facctcode"), "", intMonth, true);
				// --------------------------------------------------
				if (dou[0] > 0) {
					// 设置份额，在统计基础货币单位净值的时候用 sunkey 20081222 BugNO:MS00083
					dAmount = dou[0];
					// ------只显示单位净值项目即可。
					// sql="insert into "+pub.yssGetTableName("tb_rep_guessvalue")+" select '"+fSetCode+"',"+dbl.sqlDate(date)+",'9500',' ','单位净值','单位净值','合计',0,0,0,round(fstandardmoneycost/"+dou[0]+",3),0,0,0,0,round(fstandardmoneymarketvalue/"+dou[0]+",3),0,0,0,0,' ',0,0 from "+pub.yssGetTableName("tb_rep_guessvalue")+" where facctcode='9000' and "+dbl.sqlLen(dbl.sqlTrim("fcurcode"))+" is null  and FPortCode='"+fSetCode+"' and fdate="+dbl.sqlDate(date);
					// dbl.executeSql(sql);
					// 2008-6-6 单亮 修改在插入单位净值的时候按照通用参数设置的小数位数来插入 begin
					int iDigit = 0;
					CtlPubPara ctlPara = new CtlPubPara();
					ctlPara.setYssPub(pub);
					// fSetCode (new
					// SimpleDateFormat("yyyy-MM-dd")).format(date)
					String sDate = (new SimpleDateFormat("yyyy-MM-dd"))
							.format(date);
					iDigit = Integer.parseInt(ctlPara.getCashUnit(getPortCode(
							fSetCode + "", YssFun.left(sDate, 4))));
					// end
					sql = "insert into "
							+ pub.yssGetTableName(sGVTableName)
							+ // 单亮 2008-5-4 将9600修改为90001
							" select '"
							+ fSetCode
							+ "',"
							+ dbl.sqlDate(date)
							+ ",'9600',' ','单位净值','单位净值','合计',0,0,0,0,0,0,0,0,0,0,0,round(fstandardmoneymarketvalue/"
							+ dou[0]
							+ ","
							+ iDigit
							+ "),0,0,0,0,' ',0,0,' ' from "
							+ // 2008-6-6 单亮 修改在插入单位净值的时候按照通用参数设置的小数位数来插入
							pub.yssGetTableName(sGVTableName)
							+ " where facctcode='9000' and "
							+ dbl.sqlLen(dbl.sqlTrim("fcurcode"))
							+ " is null  and FPortCode='" + fSetCode
							+ "' and fdate=" + dbl.sqlDate(date);
					dbl.executeSql(sql);
				}
			}
			dbl.closeResultSetFinal(rs);
			// ------------------------将科目级别为0的数据/前面计算的净值数据再更新这些记录 sj edit
			// 20080221--------------------------//
			if (totalMarketValue > 0) {
				// modify by fangjiang 2010.10.18 MS01767 QDV4易方达2010年9月20日01_A
				sql = "update "
						+ pub.yssGetTableName(sGVTableName)
						+ " set FSTANDARDMONEYMARKETVALUETORAT = ROUND(FStandardMoneyMarketValue/"
						+ totalMarketValue + ",6) " + " where FDate = "
						+ dbl.sqlDate(date) + " and FPortCode = '" + fSetCode
						+ "' and FAcctDetail = 0";
				// ------------------
				dbl.executeSql(sql);
			}
			// -----------------------------------------------------------------------------------------------------------------
			// ===================这里更新停牌的日期信息 by leeyu 2008-11-5 BUG:0000517
			

			/**Start 20131015 deleted by liubo.Bug #80734.QDV4南方2013年10月08日01_B*/
//			if (isACTV) {
//				updateACTV(date);
//			}
			/**End 20131015 deleted by liubo.Bug #80734.QDV4南方2013年10月08日01_B*/
			
			// ================================================ 2008-11-5
			// insertUSNav(date, sPortCode, fSetCode);
			insertBaseNav(date, sPortCode, fSetCode, ctlpubpara); // 基础货币资产净值、单位净值
																	// xuqiji
																	// 20100312
																	// MS01020
																	// 财务估值表和净值表需修改显示单位净值的方式
																	// QDV4易方达2010年3月10日01_A
			insertMVFX(date, sPortCode, fSetCode);
			// ---MS00374 QDV4建行2009年4月11日01_A 获取并保存累计净值信息 ------
			insertAccumulateUnit(date, sPortCode, fSetCode);
			// -------------------------------------------------------------
			dbl.loadConnection().commit();
			isCommit = true;
			insertPercentChange(date, sPortCode, fSetCode); // 插入涨跌幅比例
															// QDV4海富通2008年12月31日03_B
															// MS00176 by leeyu
															// 20090212
			// insertUSNav(date, sPortCode, fSetCode);
			// dbl.loadConnection().commit();
			// isCommit = true;
			// QDV4海富通2009年05月11日01_AB MS00439 若参数为真，则将已实现收益添加到财务估值表中 by leeyu
			// 2009-05-18
			if (bShowYSX) {
				insertInCome(date, sPortCode, fSetCode);
			}
			// QDV4海富通2009年05月11日01_AB MS00439

			// ---------------------------增加最小申赎单位净值、篮子估值、现金差额 by ctq
			// 2009-11-12-----------
			//add by baopingping #story 1263 2011-08-01
			InsertUpdayValue(date,fSetCode);//添加日净值增长率
			InsertAmountRate(date,fSetCode);//添加本期基金份额净值增长率
			InsertAmont(date,fSetCode);//添加累计净值增长率			
           //---------------end------------			
			
			/**Start 20130711 added by liubo.Story #4106.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001
			 * 在估值表中插入市场指数日净值增长率的数据（科目代码9990）*/
			
			setIndexDailyGrowthRate(String.valueOf(fSetCode),date);
			
			/**End 20130711 added by liubo.Story #4106.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001*/
			
			String supplyMode = getSupplyMode(PortCode);
			if (bIsETFGV || 
					YssOperCons.YSS_ETF_MAKEUP_ONE.equals(supplyMode) || 
					YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE.equals(supplyMode) ||
					YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(supplyMode)) {
				// 插入最小申赎单位净值数据 最小申赎单位净值=资产净值*基准比例/资产份额
				sql = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ "(fportcode, fdate, facctcode, fcurcode, facctname, facctattr, facctclass, "
						+ "fexchangerate, famount, fcost, fstandardmoneycost, fcosttonetratio, fstandardmoneycosttonetratio, fmarketprice,"
						+ "fotprice1, fotprice2, fotprice3, fmarketvalue, fstandardmoneymarketvalue, fmarketvaluetoratio,"
						+ "fstandardmoneymarketvaluetorat, fappreciation, fstandardmoneyappreciation, fmarketdescribe, facctlevel, facctdetail, fdesc)"
						+ "select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ ",'9800',' ','最小申赎单位净值','最小申赎单位净值' ,'合计',"
						+ "0,0,0,0,0,0,0,"
						+ "0,0,0,0,round(fnav*fnormscale/fshare,2),0,"
						+ "0,0,0,' ',0,0,' '  "
						
						//20121026 modified by liubo.
						//变更取数来源，从ETF净值数据表变更为财务估值表
						//================================
//						+ " from (select fportmarketvalue as fnav from "
//						+ pub.yssGetTableName("tb_etf_navdata") + " where fnavdate="
//						+ dbl.sqlDate(date) + " and fportcode= " + dbl.sqlString(sPortCode)
//						+ " and fkeycode = 'TotalValue') t1,"
//						+ "(select fportmarketvalue as fshare from "
//						+ pub.yssGetTableName("tb_etf_navdata") + " where fnavdate="
//						+ dbl.sqlDate(date) + " and fportcode= " + dbl.sqlString(sPortCode)
//						+ " and fkeycode='TotalAmount') t2,"
//						+ "(select FNormScale from "
//						+ pub.yssGetTableName("Tb_ETF_Param")
//						+ " where fportcode=" + dbl.sqlString(sPortCode)
//						+ " ) t3 "
						+ " from (select FSTANDARDMONEYMARKETVALUE as fnav from "
						+ pub.yssGetTableName(sGVTableName) + " where FDate = " + dbl.sqlDate(date) 
						+ " and fportcode= " + dbl.sqlString(String.valueOf(fSetCode))
						+ " and FAcctCode = '9000' and FCurCode=  ' ') t1,"
						+ " (select FAmount as fshare from " + pub.yssGetTableName(sGVTableName)
						+ " where FDate = " + dbl.sqlDate(date) + " and fportcode= " + dbl.sqlString(String.valueOf(fSetCode))
						+ " and FAcctCode = '8700' and FCurCode=  ' ') t2,"
						+ "(select FNormScale from "
						+ pub.yssGetTableName("Tb_ETF_Param")
						+ " where fcheckstate = 1 and fportcode=" + dbl.sqlString(sPortCode)
						+ " ) t3 ";
						//================end================
						
				dbl.executeSql(sql);

				// 插入篮子估值数据 篮子估值=SUM(股票数量*单价*汇率)
				sql = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ "(fportcode, fdate, facctcode, fcurcode, facctname, facctattr, facctclass, "
						+ "fexchangerate, famount, fcost, fstandardmoneycost, fcosttonetratio, fstandardmoneycosttonetratio, fmarketprice,"
						+ "fotprice1, fotprice2, fotprice3, fmarketvalue, fstandardmoneymarketvalue, fmarketvaluetoratio,"
						+ "fstandardmoneymarketvaluetorat, fappreciation, fstandardmoneyappreciation, fmarketdescribe, facctlevel, facctdetail, FDesc)"
						+ "select '" + fSetCode + "'," + dbl.sqlDate(date)
						+ ",'9801',' ','篮子估值','篮子估值' ,'合计'," + "0,0,0,0,0,0,0,"
						+ "0,0,0,0,t1.fportmarketvalue,0,"
						+ "0,0,0,' ',0,0, ' ' " + " from "
						+ pub.yssGetTableName("tb_etf_navdata") + " t1 "
						+ " where t1.fportcode=" + dbl.sqlString(sPortCode)
						+ " and t1.fnavdate=" + dbl.sqlDate(date)
						+ " and t1.fkeycode=" + dbl.sqlString("StockListVal");
				dbl.executeSql(sql);

				// 插入现金差额数据 现金差额=最小申赎单位净值-篮子估值
				sql = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ "(fportcode, fdate, facctcode, fcurcode, facctname, facctattr, facctclass, "
						+ "fexchangerate, famount, fcost, fstandardmoneycost, fcosttonetratio, fstandardmoneycosttonetratio, fmarketprice,"
						+ "fotprice1, fotprice2, fotprice3, fmarketvalue, fstandardmoneymarketvalue, fmarketvaluetoratio,"
						+ "fstandardmoneymarketvaluetorat, fappreciation, fstandardmoneyappreciation, fmarketdescribe, facctlevel, facctdetail, FDesc)"
						+ "select '" + fSetCode + "'," + dbl.sqlDate(date)
						/**shashijie 2012-01-07 STORY 1789  财务估值表显示单位2个字  */
						+ ",'9802',' ','单位现金差额','单位现金差额' ,'合计'," + "0,0,0,0,0,0,0,"
						/**end*/
						+ "0,0,0,0,t1.fportmarketvalue,0,"
						+ "0,0,0,' ',0,0,' ' " + " from "

						//20121026 modified by liubo.
						//变更取数来源，从ETF净值数据表变更为财务估值表
						//================================
//						+ pub.yssGetTableName("tb_etf_navdata") + " t1 "
//						+ " where t1.fportcode=" + dbl.sqlString(sPortCode)
//						+ " and t1.fnavdate=" + dbl.sqlDate(date)
//						+ " and t1.fkeycode=" + dbl.sqlString("UnitCashBal")
						+ " (select t2.FSTANDARDMONEYMARKETVALUE - t3.FSTANDARDMONEYMARKETVALUE as fportmarketvalue" 
						+ " from (select FPortCode,FDate,FSTANDARDMONEYMARKETVALUE from " + pub.yssGetTableName(sGVTableName) 
						+ " where FAcctCode = '9801') t3 "
						+ " left join (select FPortCode,FDate,FSTANDARDMONEYMARKETVALUE from " + pub.yssGetTableName(sGVTableName) 
						+ " where FAcctCode = '9800') t2 " 
						+ " on t2.FPortCode = t3.FPortCode and t2.FDate = t3.FDate "
						+ " where t3.FDate = " + dbl.sqlDate(date) + " and t3.FPortCode = " + dbl.sqlString(String.valueOf(fSetCode))
						+ " ) t1";
						//================end================
				dbl.executeSql(sql);
			}
			// ---------------------------------------------------------------------------------------------
			
			int digit=0;
			CtlPubPara ctlPara = new CtlPubPara();
			ctlPara.setYssPub(pub);
			String sDate = (new SimpleDateFormat("yyyy-MM-dd"))
					.format(date);
			digit = Integer.parseInt(ctlPara.getCashUnit(getPortCode(
					fSetCode + "", YssFun.left(sDate, 4))));			
			TaTradeBean tb=new TaTradeBean();
			tb.setYssPub(pub);
			boolean isClass=tb.isMultiClass(this.sPortCode);		
			if(isClass){
				int clsType=0;
				clsType=tb.getAccWayState(this.sPortCode);
				//add by zhouwei 20120228  统计各个分级的权益类财务凭证
				if(clsType==1){//1基准资产份额
					boolean isAuxiAcc=false;//财务表是否使用辅助核算项
					int iniMonth = 0;
					iniMonth = YssFun.toInt(YssFun.formatDate(
							YssFun.addMonth(date, -1), "MM")); // 取上一个月的日期
					if (YssFun.toInt(YssFun.formatDate(date, "MM")) == 1) {
						iniMonth = 0; // 判断，如果本月的日期为1,则上一个月的日期为0
					}
					//实收基金,损益平准金，本期利润，利润分配等权益类
					Map clsPortMap=new HashMap();
					List rootList=new ArrayList();
					double fendbal=0; 
					double fbendbal=0;
					double faendbal=0;
					int flag=1;
					String facctcode="";//科目
					String fcurcode="";//币种
					String fauxiacc="";//分级标示
					sql="insert into "+ pub.yssGetTableName(sGVTableName)+"(FPORTCODE,FDATE,FACCTCODE,FCURCODE,FACCTNAME,FACCTATTR,FACCTCLASS,FEXCHANGERATE,FAMOUNT,FCOST,FSTANDARDMONEYCOST,FCOSTTONETRATIO,FSTANDARDMONEYCOSTTONETRATIO"
					   +",FMARKETPRICE,FOTPRICE1,FOTPRICE2,FOTPRICE3,FMARKETVALUE,FSTANDARDMONEYMARKETVALUE,FMARKETVALUETORATIO,FSTANDARDMONEYMARKETVALUETORAT,FAPPRECIATION,FSTANDARDMONEYAPPRECIATION,FMARKETDESCRIBE,FACCTLEVEL,FACCTDETAIL,FDESC)"
					   +" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					pst=dbl.getPreparedStatement(sql);
					sql="select c.*,d.*,e.AuxiAccName,' ' as fcurcode from (select FAuxiAcc,kmdm,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,sum(faendbal) as faendbal from  ( "
					   +"select (case when a.FAuxiAcc is null then b.FAuxiAcc else a.FAuxiAcc end) as  FAuxiAcc,(case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else a.fcyid end) as fcurcode,"
				       +"(case when b.fendbal is null then 0 else b.fendbal end) - (case when fjje is null then 0 else fjje end) + (case when fdje is null then 0 else fdje end) as fendbal,"
					   +"(case when b.fbendbal is null then 0 else b.fbendbal end) - (case when fbjje is null then 0 else fbjje end) + (case when fbdje is null then 0 else fbdje end) as fbendbal,"
				       +"(case when b.faendbal is null then 0 else b.faendbal end) - (case when fjsl is null then 0 else fjsl end) + (case when fdsl is null then 0 else fdsl end) as faendbal"
					   +" from (select fkmh,fcyid,fterm,FAuxiAcc,sum(case when fjd = 'J' then fbal else 0 end) as fjje,"
				       +"sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,"
					   +"sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,"
				       +"sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "+YssTabPrefix+"fcwvch "
				       +" where fterm ="+ YssFun.formatDate(date, "MM")+" and  "+ dbl.sqlDay("FDate")+ " <= "+ YssFun.formatDate(date, "dd")
				       +" group by fkmh, fcyid, fterm,FAuxiAcc) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,-1 * fendbal as fendbal,fbaccdebit,fbacccredit,-1 * fbendbal as fbendbal,faaccdebit,-1 * faendbal as faendbal,FAuxiAcc from"
				       +"  "+YssTabPrefix+"Lbalance where fmonth ="+iniMonth+" and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode and a.FAuxiAcc=b.FAuxiAcc ) group by FAuxiAcc,kmdm ) c "
				       +" join (select facctcode, facctname, famount, facctattr, FAcctClass,FAcctLevel,FAcctParent from  "+YssTabPrefix+"Laccount where  fbalDC in (1, -1, 0) and  FAcctClass='权益类'"
				       +" and  FAcctDetail=1 ) d on c.kmdm = d.facctcode"
				       +" join  "+YssTabPrefix+"AuxiAccSet e on e.auxiaccid=c.FAuxiAcc where c.fbendbal<>0 order by c.FAuxiAcc,c.kmdm";
					rs=dbl.openResultSet(sql);
					while(rs.next()){
					    fauxiacc=rs.getString("FAuxiAcc");
						String rePortCls=rs.getString("FAuxiAcc").substring(2);//根据财务辅助核算项来找到所属组合分级
						if(!clsPortMap.containsKey(fauxiacc)){//根目录
							if(flag>1){//根据汇总的权益数据，来更新财务估值表
								sql="update "+pub.yssGetTableName(sGVTableName)+" set FMARKETVALUE="+fendbal
								  +",FSTANDARDMONEYMARKETVALUE="+fbendbal+",FAMOUNT="+faendbal+",FACCTNAME='权益合计',FACCTATTR='权益合计'"
								  +" where FPORTCODE="+dbl.sqlString(Integer.toString(fSetCode))+" and FDATE="+dbl.sqlDate(date)
								  +" and FACCTCODE="+dbl.sqlString(facctcode);
								dbl.executeSql(sql);
							    fendbal=0;
								fbendbal=0;
							    faendbal=0;
							}
							facctcode=rs.getString("AuxiAccName").substring(0,1)+rs.getString("FAcctParent");
							fcurcode=rs.getString("fcurcode");
							setPstAttr(pst, rs, true, fSetCode, date, digit,rePortCls ,false , 1 , 1); //modify huangqirong 2012-09-06 story #2782
							clsPortMap.put(fauxiacc, fauxiacc);
						}
						fendbal=YssD.add(rs.getDouble("fendbal"), fendbal);//原币
						fbendbal=YssD.add(rs.getDouble("fbendbal"), fbendbal);//本位币
						faendbal=YssD.add(rs.getDouble("faendbal"), faendbal);//数量
						setPstAttr(pst, rs, false, fSetCode, date, digit,rePortCls , false , 1 , 1); //modify huangqirong 2012-09-06 story #2782
						flag++;
						isAuxiAcc=true;
					}
					if(flag>1){//最后一项的保存
						sql="update "+pub.yssGetTableName(sGVTableName)+" set FMARKETVALUE="+fendbal
								  +",FSTANDARDMONEYMARKETVALUE="+fbendbal+",FAMOUNT="+faendbal+",FACCTNAME='权益合计',FACCTATTR='权益合计'"
								  +" where FPORTCODE="+dbl.sqlString(Integer.toString(fSetCode))+" and FDATE="+dbl.sqlDate(date)
								  +" and FACCTCODE="+dbl.sqlString(facctcode);
								dbl.executeSql(sql);
					}
					dbl.closeResultSetFinal(rs);
					if(!isAuxiAcc){//没用使用辅助核算项
						flag=1;
						fendbal=0;
						fbendbal=0;
						faendbal=0;
						sql="select d.FPORTCLSCODE || ' Class' as AuxiAccName,c.*,d.*,' ' as fcurcode from (select kmdm,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,sum(faendbal) as faendbal from  ( "
							   +"select (case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else a.fcyid end) as fcurcode,"
							   +"(case when b.fendbal is null then 0 else b.fendbal end) - (case when fjje is null then 0 else fjje end) + (case when fdje is null then 0 else fdje end) as fendbal,"
							   +"(case when b.fbendbal is null then 0 else b.fbendbal end) - (case when fbjje is null then 0 else fbjje end) + (case when fbdje is null then 0 else fbdje end) as fbendbal,"
							   +"(case when b.faendbal is null then 0 else b.faendbal end) - (case when fjsl is null then 0 else fjsl end) + (case when fdsl is null then 0 else fdsl end) as faendbal"
							   +" from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,"
							   +"sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,"
							   +"sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,"
							   +"sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "+YssTabPrefix+"fcwvch "
							   +" where fterm ="+ YssFun.formatDate(date, "MM")+" and  "+ dbl.sqlDay("FDate")+ " <= "+ YssFun.formatDate(date, "dd")
							   +" group by fkmh, fcyid, fterm) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,-1 * fendbal as fendbal,fbaccdebit,fbacccredit,-1 * fbendbal as fbendbal,faaccdebit,-1 * faendbal as faendbal,FAuxiAcc from"
							   +"  "+YssTabPrefix+"Lbalance where fmonth ="+iniMonth+" and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) group by kmdm ) c "
							   +" join (select facctcode, facctname, famount, facctattr, FAcctClass,FAcctLevel,FAcctParent,FPORTCLSCODE from  "+YssTabPrefix+"Laccount where  fbalDC in (1, -1, 0) and  FAcctClass='权益类'"
							   +" and  FAcctDetail=1 ) d on c.kmdm = d.facctcode  where c.fbendbal<>0  and  d.FPORTCLSCODE<>' ' order by d.FPORTCLSCODE,c.kmdm";
							rs=dbl.openResultSet(sql);
							while(rs.next()){
							    fauxiacc=rs.getString("FPORTCLSCODE").trim();
								String rePortCls=rs.getString("FPORTCLSCODE");
								if(!clsPortMap.containsKey(fauxiacc)){//根目录
									if(flag>1){//根据汇总的权益数据，来更新财务估值表
										sql="update "+pub.yssGetTableName(sGVTableName)+" set FMARKETVALUE="+fendbal
										  +",FSTANDARDMONEYMARKETVALUE="+fbendbal+",FAMOUNT="+faendbal+",FACCTNAME='权益合计',FACCTATTR='权益合计'"
										  +" where FPORTCODE="+dbl.sqlString(Integer.toString(fSetCode))+" and FDATE="+dbl.sqlDate(date)
										  +" and FACCTCODE="+dbl.sqlString(facctcode);
										dbl.executeSql(sql);
									    fendbal=0;
										fbendbal=0;
									    faendbal=0;
									}
									facctcode=rs.getString("FPORTCLSCODE")+rs.getString("FAcctParent");
									fcurcode=rs.getString("fcurcode");
									setPstAttr(pst, rs, true, fSetCode, date, digit, rePortCls , false , 1 , 1);//modify huangqirong 2012-09-06 story #2782
									clsPortMap.put(fauxiacc, fauxiacc);
								}
								fendbal=YssD.add(rs.getDouble("fendbal"), fendbal);//原币
								fbendbal=YssD.add(rs.getDouble("fbendbal"), fbendbal);//本位币
								faendbal=YssD.add(rs.getDouble("faendbal"), faendbal);//数量
								setPstAttr(pst, rs, false, fSetCode, date, digit,rePortCls ,false , 1 , 1);//modify huangqirong 2012-09-06 story #2782
								flag++;
							}
							if(flag>1){//最后一项的保存
								sql="update "+pub.yssGetTableName(sGVTableName)+" set FMARKETVALUE="+fendbal
										  +",FSTANDARDMONEYMARKETVALUE="+fbendbal+",FAMOUNT="+faendbal+",FACCTNAME='权益合计',FACCTATTR='权益合计'"
										  +" where FPORTCODE="+dbl.sqlString(Integer.toString(fSetCode))+" and FDATE="+dbl.sqlDate(date)
										  +" and FACCTCODE="+dbl.sqlString(facctcode);
										dbl.executeSql(sql);
							}
					}
				}else if(clsType == 0){//0按资产净值分级
					dealNetVaulePortCls(rs, fSetCode, date, digit, intMonth);
				}else if(clsType == 2){ //2按汇率分级
					dealMClassByRate(rs, fSetCode, date, digit, intMonth , clsType); //add by fangjiang 2012.05.16 story 2565 //modify huangqirong 2013-02-05 Story #3433 , bug #6975
				}
				//add by huangqirong 2012-09-04 story #2782
				else if(clsType == 3){ // 3 按资产净值分级 国内
					this.dealMclassValue(fSetCode , date , digit);
				}
				//---end---
				//---add by songjie 2012.11.22 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
				else if(clsType == 4){//按资产净值分级（国内多层分级）
					this.dealMAMclassValue(date, digit, fSetCode);
				}
				//---add by songjie 2012.11.22 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 end---//
			}
			dbl.loadConnection().commit();
			isCommit = true;
			//-----------end by STORY #2013 ------------
		}
		// ---modify by sj MS00402 QDV4海富通2009年04月21日01_AB 使用优化的抛错信息方式
		// --------------
		catch (SQLException sqle) {
			// throw new YssException("生成估值表出错！\r\n" + sqle.getMessage());
			throw new YssException("生成估值表出错！", sqle);
		} catch (Exception e) {
			// throw new YssException("生成估值表出错！\r\n" + e.getMessage());
			throw new YssException("生成估值表出错！", e);
		}
		// -------------------------------------------------------------------------
		finally {
			try {
				if (!isCommit) {
					dbl.loadConnection().rollback();
				}
			} catch (Exception e) {
			}
			try {
				if (rs != null) {
					rs.getStatement().close();
				}
				if(pst!=null){
					dbl.closeStatementFinal(pst);
				}
			} catch (Exception e) {
			}
			try {
				dbl.loadConnection().setAutoCommit(true);
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * 20130711 added by liubo.Story #4106.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001
	 * 在估值表中插入市场指数日净值增长率的数据（科目代码9990）
	 * 计算公式为：（T日指数行情-上一工作日日指数行情）/上一工作日日指数行情
	 * 若查询不到当日行情数据时，则向前倒查出“最近一日“的行情数据
	 * 若“昨日指数行情“为0或空，则 “市场指数日净值增长率”直接取0
	 * 上述公式的取数的数据源为“基金业绩比较基准”界面
	 * @param sSetCode	套账代码
	 * @param dDate		估值日期
	 * @throws YssException
	 */
	private void setIndexDailyGrowthRate(String sSetCode,java.util.Date dDate) throws YssException
	{
		String strSql = "";
		ResultSet rs = null;
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		double dTheDayValue = 0;
		double dYesterdayValue = 0;
		
		try
		{
			conn.setAutoCommit(false); //设置自动提交事物
			
			//获取当天指数行情
			strSql = "select * from  " + pub.yssGetTableName("tb_data_comparebaseline") +
					 " where FStartDate in (select max(FStartDate) from " + pub.yssGetTableName("tb_data_comparebaseline") +
					 " where FStartDate <= " + dbl.sqlDate(dDate) + ")" + 
					 " and FCHECKSTATE = 1" + 
					 " and FPortCode = " + dbl.sqlString(this.sPortCode);
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				dTheDayValue = rs.getDouble("FStandardValue");
			}
			
			dbl.closeResultSetFinal(rs);
            /**add---huhuichao 2013-9-6 BUG  9402 市场指数日净值增长率的算法问题 */
			String holiday="";//节假日
			String dDate1="";//当前日期的上一个交易日
			String sql1="";
			BaseOperDeal deal = new BaseOperDeal();
            deal.setYssPub(pub);
    		sql1="select fholidayscode from " +
			     pub.yssGetTableName("Tb_TA_CashSettle ")+
			     "where fcheckstate=1 and FStartDate=(select max(FStartDate)from " +
			     pub.yssGetTableName("Tb_TA_CashSettle ")+ " where fcheckstate=1 )";
    		rs = dbl.openResultSet(sql1);
    		if(rs.next())
    		{
    			holiday=rs.getString("fholidayscode");
    		}
    		//dDate=YssFun.parseDate(dDate);//指令日期   从前台获取
    		dDate1=YssFun.formatDate(deal.getWorkDay(holiday,dDate, -1), "yyyy-MM-dd");//当前日期的上一个交易日
			//获取上一个工作日指数行情
			strSql = "select * from  " + pub.yssGetTableName("tb_data_comparebaseline") +
					 " where FStartDate = " + dbl.sqlDate(dDate1) +
					 " and FCHECKSTATE = 1" + 
					 " and FPortCode = " + dbl.sqlString(this.sPortCode);
			/**end---huhuichao 2013-9-6 BUG  9402*/
			rs = dbl.queryByPreparedStatement(strSql);

			while(rs.next())
			{
				dYesterdayValue = rs.getDouble("FStandardValue");
			}
			
			dbl.closeResultSetFinal(rs);
			
			//若“昨日指数行情“为0或空，则 “市场指数日净值增长率”直接取0
			if (dYesterdayValue == 0)
			{
				dTheDayValue = 0;
			}
			else
			{
				dTheDayValue = YssD.div(YssD.sub(dTheDayValue, dYesterdayValue), dYesterdayValue);
			}
			
			strSql = "insert into "
				+ pub.yssGetTableName(sGVTableName)
				+ " select '" + sSetCode + "',"+ dbl.sqlDate(dDate)
				/**add---huhuichao 2013-9-6 BUG  9402 市场指数日净值增长率的算法问题 */
				+ ",'9990',' ','市场指数日净值增长率','市场指数日净值增长率','合计'"
				/**end---huhuichao 2013-9-6 BUG  9402  */
				+ ",0,0,0,0,0,0,0,0,0,0,0,round(" + dTheDayValue + ",4)"
				+ ",0,0,0,0,' ',0,0,' ' from "
				+ pub.yssGetTableName(sGVTableName)
				+ " where facctcode='9000' and "
				+ dbl.sqlLen(dbl.sqlTrim("fcurcode"))
				+ " is null  and FPortCode='" + sSetCode
				+ "' and fdate=" + dbl.sqlDate(dDate);
			dbl.executeSql(strSql);
			
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = true;
		}
		catch(Exception ye)
		{
			throw new YssException(ye);
		}
		finally
		{
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}
	
	
	
	//add by zhouwei 财务报表分级显示
	/**
	 * modify huangqirong 2012-09-06 story #2782
	 * */
	private void setPstAttr(PreparedStatement pst,ResultSet rs,boolean isRoot,int fSetCode,Date date,int digit,String rePortCls , boolean converPlace , double baseRate , double portRate){
		try{
		
			pst.setString(1,fSetCode+"" );
			pst.setDate(2,new java.sql.Date(date.getTime()));
			pst.setString(3, isRoot?rePortCls+rs.getString("FAcctParent"):rePortCls+rs.getString("kmdm"));//pst.setString(3, isRoot?rs.getString("AuxiAccName"):rs.getString("facctattr"));
			pst.setString(4, rs.getString("fcurcode"));
			pst.setString(5, isRoot?rs.getString("AuxiAccName"):rs.getString("facctattr"));
			pst.setString(6, isRoot?rs.getString("AuxiAccName"):rs.getString("facctattr"));
			pst.setString(7, "class"+"\t"+rePortCls);//标志按照基准资产份额分级             rs.getString("facctclass")
			pst.setDouble(8, 0);
			pst.setDouble(9, rs.getDouble("FAEndBal"));
			pst.setDouble(10, 0);
			pst.setDouble(11, 0);
			pst.setDouble(12, 0);
			pst.setDouble(13, 0);
			pst.setDouble(14, 0);
			pst.setDouble(15, 0);
			pst.setDouble(16, 0);
			pst.setDouble(17, 0);
			
			/**
			 * modify huangqirong 2012-09-06 story #2782
			 * 是否重现调整 原币本币存储
			 */
			if(converPlace){
				if("实收资本".equalsIgnoreCase(rs.getString("FAcctATTR"))){
					pst.setDouble(18, YssD.round(rs.getDouble("fendbal"), digit));//原币市值  modified by yeshenghong story4127 原来使用的是本币
					pst.setDouble(19, YssD.round(rs.getDouble("fbendbal"), digit));//本位币市值  modified by yeshenghong story4127 原来使用的是原币
				}else{
					pst.setDouble(18, YssD.round(rs.getDouble("fendbal"), 2));//原币市值
				   	double baseMoney = YssD.div(
				   								YssD.mul(rs.getDouble("fbendbal"), portRate), 
				   								baseRate
				   								);
				   	
					pst.setDouble(19, YssD.round(baseMoney, 2));//本位币市值
				}
			}else{
				pst.setDouble(18, YssD.round(rs.getDouble("FendBal"), digit));//原币市值
				pst.setDouble(19, YssD.round(rs.getDouble("fbendbal"), digit));//本位币市值
			}
			//---end---
			
			pst.setDouble(20, 0);
			pst.setDouble(21, 0);
			pst.setDouble(22, 0);
			pst.setDouble(23, 0);
			pst.setString(24, " ");
			pst.setInt(25, rs.getInt("FAcctLevel"));
			pst.setInt(26, isRoot?0:1);//isRoot?0:1
			pst.setString(27, " ");
			pst.execute();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add by songjie 2012.11.22 
	 * STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * */
	private void setMPstAttr(PreparedStatement pst,ResultSet rs,int fSetCode,Date date,int digit,String rePortCls){
		try{
		
			pst.setString(1,fSetCode+"" );
			pst.setDate(2,new java.sql.Date(date.getTime()));
			//财务估值表科目 = 分级级别_分级组合代码_合计项专用科目号
			pst.setString(3, rs.getString("FPORTClsLever") + "_" + rs.getString("FPORTCLSCODE") + "_" + rs.getString("KMDM"));
			pst.setString(4, rs.getString("fcurcode"));
			pst.setString(5, rs.getString("facctattr"));
			pst.setString(6, rs.getString("facctattr"));
			pst.setString(7, "class\t"+rePortCls);//标志按照基准资产份额分级 
			pst.setDouble(8, 0);
			pst.setDouble(9, rs.getDouble("FAEndBal"));
			pst.setDouble(10, 0);
			pst.setDouble(11, 0);
			pst.setDouble(12, 0);
			pst.setDouble(13, 0);
			pst.setDouble(14, 0);
			pst.setDouble(15, 0);
			pst.setDouble(16, 0);
			pst.setDouble(17, 0);
			pst.setDouble(18, YssD.round(rs.getDouble("FendBal"), digit));//原币市值
			pst.setDouble(19, YssD.round(rs.getDouble("fbendbal"), digit));//本位币市值
			pst.setDouble(20, 0);
			pst.setDouble(21, 0);
			pst.setDouble(22, 0);
			pst.setDouble(23, 0);
			pst.setString(24, " ");
			pst.setInt(25, rs.getInt("FAcctLevel"));
			pst.setInt(26, 1);//isRoot?0:1
			pst.setString(27, " ");
			pst.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add by songjie 2012.11.22 
	 * STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * 计算 按照资产净值分级（国内多层分级）对应的合计项
	 * MAMclass = more and more class 多层分级
	 * @param setCode
	 * @param date
	 * @param digit
	 * @throws YssException
	 */
	private void dealMAMclassValue(Date date, int digit, int fSetCode)throws YssException{	
		String sql = "";
		String gvSql = "";
		String classSql = "";
		PreparedStatement gvPst = null;
		PreparedStatement classPst = null;
		ResultSet rs = null;
		int iniMonth = 0;
		Hashtable hsUnitParentCode = new Hashtable();
		Connection conn = null;
		boolean bTrans = false;
		try{
			iniMonth = YssFun.toInt(YssFun.formatDate(YssFun.addMonth(date, -1), "MM")); // 取上一个月的日期
			if (YssFun.toInt(YssFun.formatDate(date, "MM")) == 1) {
				iniMonth = 0; // 判断，如果本月的日期为1,则上一个月的日期为0
			}
			
			conn = dbl.loadConnection();
			conn.setAutoCommit(false); //设置自动提交事物
			
			sql = " delete from " + pub.yssGetTableName("Tb_Data_MultiClassNet") +
            " where FPortCode = " + dbl.sqlString(this.sPortCode) +
            " and FNAVDate = " + dbl.sqlDate(date) +
            " and FType <> 'zb' ";      
			dbl.executeSql(sql);
			
			gvSql = "insert into "+ pub.yssGetTableName(sGVTableName)+"(FPORTCODE,FDATE,FACCTCODE,FCURCODE,FACCTNAME,FACCTATTR,FACCTCLASS,FEXCHANGERATE,FAMOUNT,FCOST,FSTANDARDMONEYCOST,FCOSTTONETRATIO,FSTANDARDMONEYCOSTTONETRATIO"
			   		+ ",FMARKETPRICE,FOTPRICE1,FOTPRICE2,FOTPRICE3,FMARKETVALUE,FSTANDARDMONEYMARKETVALUE,FMARKETVALUETORATIO,FSTANDARDMONEYMARKETVALUETORAT,FAPPRECIATION,FSTANDARDMONEYAPPRECIATION,FMARKETDESCRIBE,FACCTLEVEL,FACCTDETAIL,FDESC)"
			   		+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			gvPst = dbl.getPreparedStatement(gvSql);
			
			classSql = " insert into " + pub.yssGetTableName("Tb_Data_MultiClassNet") +
		       " (FNAVDate,FPortCode,FType,FPortCuryCode,FCuryCode,FNetValueBeforeFee,FManageFee,FTrusteeFee," +
		       " FNetValue,FClassNetValue,FCheckState,FCreator,FCreateTime,FSTFee,FFXJFee) " + 
		       " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "; 
			classPst = dbl.getPreparedStatement(classSql);

    		createTAClsPortSummationItem(gvPst, classPst, date, fSetCode, digit,iniMonth);//生成分级组合相关的财务估值表合计项数据
    		
			//根据TA组合分机设置 的 合计项显示项  生成TA分级组合对应的 合计项数据
			sql = " select a.FPortClsCode,a.FCURYCODE,cry.FCuryName,b.Fportclsname,b.fshowitem,b.foffset,b.FPortClsRank from " + 
				  pub.yssGetTableName("TB_Ta_Classfunddegree")
				  + " a join " + pub.yssGetTableName("Tb_Ta_Portcls") + " b on a.FPortClsCode = b.FPortClsCode " 
				  + " left join " + pub.yssGetTableName("Tb_Para_Currency") + " cry on cry.FCuryCode = a.FCuryCode "
				  + " where a.FCheckState = 1 and  a.FPORTCLSCODE <> ' ' and a.FCURYCODE <> ' ' and a.Fportcode = " + dbl.sqlString(sPortCode)
				  //add by yeshenghong story4151  20130814
				  + " and b.FCheckState = 1 and (b.fportclsschema = 'abLimited' or b.fportclsschema = 'inNetValue_chinaM')  order by fportclsrank";//组合分级与币种都不为空
			rs = dbl.queryByPreparedStatement(sql); 				
			while (rs.next()) {
				String[] showItems=(rs.getString("fshowitem")==null?"":rs.getString("fshowitem")).split(",");
				java.util.Arrays.sort(showItems);	//added by liubo.Story #2719.利用数组帮助类自动排序
				for(int i=0;i<showItems.length;i++){
					String typeCode=showItems[i];
					//05 - 基金实收资本、08 - 累计单位净值、22 - 今日单位净值、
					//09 - 净值日增长率、31 - 昨日单位净值、27 - 可分配收益、
					//28 - 单位可分配收益、30 - 期初单位净值、10 - 本期净值增长率、
					//11 - 累计净值增长率、
				    if(typeCode.equals("05") || typeCode.equals("08") || 
							typeCode.equals("22") || typeCode.equals("09") || 
							typeCode.equals("31") || typeCode.equals("27") ||
							typeCode.equals("28") || typeCode.equals("30") ||
							typeCode.equals("10") || typeCode.equals("11")){
						getPaidInCapital(hsUnitParentCode, gvPst,classPst,rs,fSetCode,date,iniMonth,digit,typeCode);
					}else if(typeCode.equals("24")){//本期收益_已实现
						getRealizedCurrentIncome(gvPst,classPst,rs,fSetCode,date,iniMonth,digit);
					}else if(typeCode.equals("25")){//本期收益_未实现
						getUnRealizedCurrentIncome(gvPst,classPst,rs,fSetCode,date,iniMonth,digit);
					}else if(typeCode.equals("26")){//利润分配_未分配利润_未实现
						getUnRealizedUndistributedProfit(gvPst,classPst,digit,rs,fSetCode,date,iniMonth);
					}else if(typeCode.equals("29")){//基金本日收益
						getTodayProfit(gvPst, classPst, rs, fSetCode, date, iniMonth, digit);
					}else if(typeCode.equals("32")){//累计派现金额
						getTotalCashDividend(gvPst, classPst, rs, fSetCode, date, iniMonth, digit);
					}else if(typeCode.equals("35")){//实现收益
						getRealizedProfit(gvPst, classPst, rs, fSetCode, date, iniMonth, digit);
					}
				}
			}
			
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = true;
		}catch(Exception e){
			throw new YssException("计算组合分级合计项出错", e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(classPst,gvPst);
		}
	}

	  /**
	   * add by songjie 2012.11.21
	   * STORY #3264 需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	   * 计算 实现收益 本位币金额
	   * @return
	   */
	  private void getRealizedProfit(PreparedStatement pst,PreparedStatement classPst, ResultSet rs,
	      int fSetCode,Date date, int iniMonth, int digit) throws YssException{
	    String strSql = "";
	    ResultSet rs1 = null;
	    String portClsLevers = "";//分级组合  及 该分级 子级组合 对应的 分级级别 的 数据
	    double subAmount = 0;//实收资本_数量
	    double superAmount = 0;//上级总的实收资本_数量
	    double realizedProfit = 0;//实现收益
	    double bEndBal = 0;//可分配收益_已实现_本位币金额_该级别所有分级组合
		double baseRate = 0;
		double portRate = 0;
	    try{
	    	String parentCode = "";//父级分级组合代码
			strSql = " select * from " + pub.yssGetTableName("Tb_TA_PortCls") + 
			" where FCheckState = 1 and FParrentCode <> ' ' and FPortClsCode = " + 
			dbl.sqlString(rs.getString("FPortClsCode")) + " and FPortCode = " + 
			dbl.sqlString(this.sPortCode);
			rs1 = dbl.openResultSet(strSql);
			while(rs1.next()){
				parentCode = rs1.getString("FParrentCode");
			}
			
			dbl.closeResultSetFinal(rs1);
			
			strSql = " select * from " + pub.yssGetTableName("Tb_TA_PortCls") + 
			" where FCheckState = 1 and FParrentCode = ' ' and FPortClsCode = " +
			dbl.sqlString(parentCode) + " and FPortCode = " + dbl.sqlString(this.sPortCode);
			rs1 = dbl.openResultSet(strSql);
			while(rs1.next()){
				portClsLevers += rs1.getString("FPortClsRank") + ",";
			}
			
			dbl.closeResultSetFinal(rs1);
			
			strSql = " select FPortClsCode,FPortClsRank,FParrentCode from " + 
			pub.yssGetTableName("Tb_TA_PortCls") + 
			" where FCheckState = 1 and FParrentCode = " + dbl.sqlString(parentCode) + 
			" and FPortCode = " + dbl.sqlString(this.sPortCode);
			rs1 = dbl.openResultSet(strSql);
			while(rs1.next()){
				portClsLevers += rs1.getString("FPortClsRank") + ",";
			}
			
			if(portClsLevers.length() > 1){
				portClsLevers = portClsLevers.substring(0, portClsLevers.length() - 1);
			}
			
			dbl.closeResultSetFinal(rs1);
			
			//可分配收益_已实现_分级组合（汇总级）
			strSql = getSumValueSql("已实现", portClsLevers,date,iniMonth);//权益类  科目性质 like '%已实现%' 汇总值
			rs1 = dbl.openResultSet(strSql);
			while(rs1.next()){
				bEndBal += rs1.getDouble("FBEndBal");//本位币金额
			}
			
			dbl.closeResultSetFinal(rs1);
			
			//实收资本_分级组合（汇总级）
			strSql = getSumValueSql("实收资本", portClsLevers,date,iniMonth);//权益类  科目性质 like '%已实现%' 汇总值
			rs1 = dbl.openResultSet(strSql);
			while(rs1.next()){
				superAmount += rs1.getDouble("FAendBal");//该级别所有分级组合份额总数量
			}
			
			dbl.closeResultSetFinal(rs1);
			
			//实收资本_分级组合（明细级）
			strSql = getSumValueSql("实收资本", rs.getString("FPortClsRank"),date,iniMonth);//权益类  科目性质 like '%已实现%' 汇总值
			rs1 = dbl.openResultSet(strSql);
			while(rs1.next()){
				subAmount += rs1.getDouble("FAendBal");//分级组合份额数量
			}
			
			dbl.closeResultSetFinal(rs1);
			
			//分级组合_实现收益 = round(可分配收益_已实现_本位币金额_该级别所有分级组合 * 分级组合实收资本_份额数量 / 该级别所有分级组合实收资本_数量 ,2)
			realizedProfit = YssD.div(YssD.mul(bEndBal, subAmount), superAmount);
			
			//基础汇率
			baseRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  rs.getString("FCuryCode"), 
		  				  this.sPortCode, 
		  				  YssOperCons.YSS_RATE_BASE);
		   	//组合汇率
		   	portRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  "", 
		  				  sPortCode, 
		  				  YssOperCons.YSS_RATE_PORT);

	      	setClassPst(classPst, rs, date, "35",0, 
	      			YssD.round(realizedProfit, 2), 
	    			 YssD.round(realizedProfit, 2));//基金本日收益 
	    }catch(Exception e){
	      throw new YssException("计算【实现收益】出错");
	    }finally{
	      dbl.closeResultSetFinal(rs1);
	    }
	  }
	
	  /**
	   * add by songjie 2012.11.21 
	   * STORY #3264 需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	   * 计算 分级组合累计派现金额
	   * @return
	   */
	  private void getTotalCashDividend(PreparedStatement pst,PreparedStatement classPst, ResultSet rs,
	      int fSetCode,Date date, int iniMonth, int digit) throws YssException{
			double baseRate = 0;
			double portRate = 0;
			double totalCashDividend = 0;
			try{
				//基础汇率
				baseRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  rs.getString("FCuryCode"), 
		  				  this.sPortCode, 
		  				  YssOperCons.YSS_RATE_BASE);
				//组合汇率
				portRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  "", 
		  				  sPortCode, 
		  				  YssOperCons.YSS_RATE_PORT);
				//分级组合累计派现金额 = TA交易数据：现金分红：分级组合：销售价格 每日累计合计
				totalCashDividend = getTATradeDividendInfo(date, rs.getString("FPortClsCode"), true);
	    	
				setClassPst(classPst, rs, date, "32",0, totalCashDividend, totalCashDividend);//累计派现金额
	    }catch(Exception e){
	    	throw new YssException("计算【累计派现金额】出错");
	    }
	  }
	
	  /**
	   * add by songjie 2012.11.21
	   * STORY #3264 需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	   * 计算 基金本日收益 本位币金额
	   * @return
	   */
	  private void getTodayProfit(PreparedStatement pst,PreparedStatement classPst, ResultSet rs,
	      int fSetCode,Date date, int iniMonth, int digit) throws YssException{
	    String strSql = "";
	    ResultSet rs1 = null;
	    String portClsLevers = "";//分级组合  及 该分级 子级组合 对应的 分级级别 的 数据
	    double amount = 0;//数量
	    double endBal = 0;//原币金额
	    double bEndBal = 0;//本位币金额
		double baseRate = 0;
		double portRate = 0;
	    try{
			//基础汇率
			baseRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  rs.getString("FCuryCode"), 
		  				  this.sPortCode, 
		  				  YssOperCons.YSS_RATE_BASE);
		   	//组合汇率
		   	portRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  "", 
		  				  sPortCode, 
		  				  YssOperCons.YSS_RATE_PORT);
		   	
			//获取组合分级对应的所有TA分级级别数据，用,隔开
			portClsLevers = getPortClsCodes(rs, "");
			
			if(portClsLevers.length() > 1){
				portClsLevers = portClsLevers.substring(0,portClsLevers.length() - 1);
			}
	    	
			//基金本日收益_分级组合 = 当日 科目性质 like '本期利润_已实现' 和 科目性质 like '本期利润_未实现' 的本位币金额 合计值
			strSql = " select a.Fkmh as kmdm, A.fcyid as fcurcode,d.*,pc.Fportclscode, " +
	               " (case when fdje is null then 0 else fdje end) - (case when fjje is null then 0 else fjje end) as fendbal, " +
	               " (case when fbdje is null then 0 else fbdje end) - (case when fbjje is null then 0 else fbjje end) as fbendbal, " +
	               " (case when fdsl is null then 0 else fdsl end) - (case when fjsl is null then 0 else fjsl end) as faendbal " +
	               " from (select fkmh,fcyid,fterm, " +
	               " sum(case when fjd = 'J' then fbal else 0 end) as fjje, " +
	               " sum(case when fjd = 'D' then fbal else 0 end) as fdje, " +
	               " sum(case when fjd = 'J' then fsl else 0 end) as fjsl, " +
	               " sum(case when fjd = 'D' then fsl else 0 end) as fdsl, " +
	               " sum(case when fjd = 'J' then fbbal else 0 end) as fbjje, " +
	               " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje, " +
	               " sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl, " +
	               " sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl " +
	               " from " + YssTabPrefix + "fcwvch " +
	               " where fterm = " + YssFun.formatDate(date, "MM") + " and FDate = " + dbl.sqlDate(date) +
	               " group by fkmh, fcyid, fterm) a " +
	               " join (select facctcode,facctname,famount,facctattr,FAcctClass, " +
	               " FAcctLevel,FAcctParent,FPORTCLSCODE as FPORTClsLever " +
	               " from " + YssTabPrefix + "Laccount " +
	               " where fbalDC in (1, -1, 0) and FAcctClass = '权益类' " +
	               " and FAcctDetail = 1) d on a.fkmh = d.facctcode " +
	               " left join (select FPortClsCode, Fportclsrank " +
	               " from " + pub.yssGetTableName("Tb_Ta_Portcls") +
	               " where FCheckState = 1 ) pc on d.FPORTClsLever = pc.Fportclsrank " +
	               " where d.FPORTClsLever in(" + operSql.sqlCodes(portClsLevers) + 
	               " ) and FAcctattr like '%本期收益%'";
			rs1 = dbl.openResultSet(strSql);
	      	while(rs1.next()){
	      		amount += rs1.getDouble("FAEndBal");
	      		endBal += rs1.getDouble("FendBal");
	      		bEndBal += rs1.getDouble("fbendbal");
	      	}
	      
	      	setClassPst(classPst, rs, date, "29",amount, 
	      			YssD.round(bEndBal, digit), 
	      			YssD.round(bEndBal, digit));//基金本日收益
	    }catch(Exception e){
	      throw new YssException("计算【基金本日收益】出错");
	    }finally{
	      dbl.closeResultSetFinal(rs1);
	    }
	  }
	
	/**
	 * add by songjie 2012.11.22 
	 * STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * 生成分级组合相关的财务估值表合计项数据
	 * @throws YssException
	 */
	private void createTAClsPortSummationItem(PreparedStatement gvPst, 
			PreparedStatement classPst, Date date, int fSetCode, int digit, int iniMonth)throws YssException{
		String sql = "";
		ResultSet rs = null;
		String baseSql = "";
		ResultSet rs1 = null;
		try{
    		//---生成分级级别相关的财务估值表各科目余额数据 start---//
			
    		baseSql=" select d.FPORTClsLever || ' Class' as AuxiAccName,c.*,d.*,' ' as fcurcode, pc.FPortClsCode " 
    			   +" from (select kmdm,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,sum(faendbal) as faendbal from  ( "
				   +" select (case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null " 
				   +" then b.fcurcode else a.fcyid end) as fcurcode, "
				   +" (case when b.fendbal is null then 0 else b.fendbal end) - (case when fjje is null then 0 else fjje end) + " 
				   +" (case when fdje is null then 0 else fdje end) as fendbal, "
				   +" (case when b.fbendbal is null then 0 else b.fbendbal end) - (case when fbjje is null then 0 else fbjje end) " 
				   +" + (case when fbdje is null then 0 else fbdje end) as fbendbal, "
				   +" (case when b.faendbal is null then 0 else b.faendbal end) - (case when fjsl is null then 0 else fjsl end) " 
				   +" + (case when fdsl is null then 0 else fdsl end) as faendbal "
				   +" from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje, "
				   +" sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl, "
				   +" sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje, " 
				   +" sum(case when fjd = 'D' then fbbal else 0 end) as fbdje, "
				   +" sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl " 
				   +" from " + YssTabPrefix + "fcwvch "
				   +" where fterm =" + YssFun.formatDate(date, "MM") + " and  " + dbl.sqlDay("FDate")+ " <= "+ YssFun.formatDate(date, "dd")
				   +" group by fkmh, fcyid, fterm) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit, " 
				   +" -1 * fendbal as fendbal,fbaccdebit,fbacccredit,-1 * fbendbal as fbendbal,faaccdebit,-1 * faendbal as faendbal,FAuxiAcc from "
				   +"  " + YssTabPrefix + "Lbalance where fmonth =" + iniMonth + " and fisdetail = 1) b " 
				   +" on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) group by kmdm ) c "
				   +" join (select facctcode, facctname, famount, facctattr, FAcctClass,FAcctLevel,FAcctParent,FPORTCLSCODE as FPORTClsLever " 
				   +" from  " + YssTabPrefix + "Laccount where  fbalDC in (1, -1, 0) and  FAcctClass = '权益类' "
				   +" and FAcctDetail=1 ) d on c.kmdm = d.facctcode " 
				   +" left join (select FPortClsCode,Fportclsrank from " + pub.yssGetTableName("Tb_Ta_Portcls")
				   +" where FCheckState = 1) pc on d.FPORTClsLever = pc.Fportclsrank " 
				   +" where c.fbendbal<>0 ";
    		
    		sql = baseSql + " and d.FPORTClsLever <> ' ' order by d.FPORTClsLever,c.kmdm ";
			rs = dbl.openResultSet(sql);
			
			while(rs.next()){
				String rePortCls=rs.getString("FPORTClsLever");
				this.setMPstAttr(gvPst, rs, fSetCode, date, digit, rePortCls );
			}
			
			dbl.closeResultSetFinal(rs);
			String strSql = "";
			
			String portClsRank = "";
			sql = " select * from  " + pub.yssGetTableName("Tb_Ta_PortCls") + " where FCheckState = 1 " +
				  //modified by yeshenghong story4151  20130814  ---start 
				  " and FParrentCode = ' ' and (fportclsschema = 'abLimited' or fportclsschema = 'inNetValue_chinaM')";
			      //modified by yeshenghong story4151  20130814  ---end 
			rs = dbl.openResultSet(sql);
			while(rs.next()){
				portClsRank = rs.getString("FPortClsRank") + ",";
				strSql = " select * from " + pub.yssGetTableName("Tb_Ta_PortCls") + 
				" where FCheckState = 1 and FParrentCode = " + dbl.sqlString(rs.getString("FPortClsCode"));
				rs1 = dbl.openResultSet(strSql);
				while(rs1.next()){
					portClsRank += rs1.getString("FPortClsRank") + ",";
				}
				
				dbl.closeResultSetFinal(rs1);
				
				if(portClsRank.length() > 1){
					portClsRank = portClsRank.substring(0, portClsRank.length() - 1);
				}
				
				strSql = " select a.*,c.FCuryCode,c.FPortClsCode from (select " + 
				dbl.sqlString(rs.getString("FPortClsRank")) + " as FPortClsLever, sum(FAEndBal) as FAEndBal, " + 
				" sum(FEndBal) as FEndBal, sum(FBEndBal) as FBEndBal from ( " + baseSql + 
				" and d.FPORTClsLever in (" + operSql.sqlCodes(portClsRank) + "))) a "+
				" join (select * from " + pub.yssGetTableName("Tb_Ta_PortCls") + 
				//modified by yeshenghong story4151  20130814 
				" where FCheckState = 1 and (fportclsschema = 'abLimited' or fportclsschema = 'inNetValue_chinaM')) b " +
				" on a.FPortClsLever = b.FPortClsRank " +
				" join (select * from " + pub.yssGetTableName("Tb_Ta_ClassFundDegree") + 
				" where FCheckState = 1) c on b.FPortClsCode = c.FPortClsCode ";
				
				rs1 = dbl.openResultSet(strSql);
				while(rs1.next()){
					setGvPst(gvPst, rs, date, "9600",
							" ","资产净值", "资产净值", "class\t"+rs.getString("FPortClsRank"),
							fSetCode, rs1.getDouble("FAEndBal"), 
							YssD.round(rs1.getDouble("FendBal"), digit), 
							YssD.round(rs1.getDouble("fbendbal"), digit),
							0, 0);
					
			      	setClassPst(classPst, rs1, date, "01",rs1.getDouble("FAEndBal"), 
			      			YssD.round(rs1.getDouble("fbendbal"), digit), 
			      			YssD.round(rs1.getDouble("fbendbal"), digit));//资产净值
				}
				
				dbl.closeResultSetFinal(rs1);
				
				sql=" select FAuxiacc ,sum(Fendbal) as Fendbal , sum(fbendbal) as fbendbal , sum(faendbal) as faendbal from ( " +
					"select c.*,d.*,e.AuxiAccName,' ' as fcurcode from (select FAuxiAcc,kmdm,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,sum(faendbal) as faendbal ,Fcurcode as FbaseCuryCode from  ( "
				   +"select (case when a.FAuxiAcc is null then b.FAuxiAcc else a.FAuxiAcc end) as  FAuxiAcc,(case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else a.fcyid end) as fcurcode,"
			       +"(case when b.fendbal is null then 0 else b.fendbal end) - (case when fjje is null then 0 else fjje end) + (case when fdje is null then 0 else fdje end) as fendbal,"
				   +"(case when b.fbendbal is null then 0 else b.fbendbal end) - (case when fbjje is null then 0 else fbjje end) + (case when fbdje is null then 0 else fbdje end) as fbendbal,"
			       +"(case when b.faendbal is null then 0 else b.faendbal end) - (case when fjsl is null then 0 else fjsl end) + (case when fdsl is null then 0 else fdsl end) as faendbal"
				   +" from (select fkmh,fcyid,fterm,FAuxiAcc,sum(case when fjd = 'J' then fbal else 0 end) as fjje,"
			       +"sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,"
				   +"sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,"
			       +"sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "+YssTabPrefix+"fcwvch "
			       +" where fterm ="+ YssFun.formatDate(date, "MM")+" and  "+ dbl.sqlDay("FDate")+ " <= "+ YssFun.formatDate(date, "dd")
			       +" group by fkmh, fcyid, fterm,FAuxiAcc) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,-1 * fendbal as fendbal,fbaccdebit,fbacccredit,-1 * fbendbal as fbendbal,faaccdebit,-1 * faendbal as faendbal,FAuxiAcc from"
			       +"  "+YssTabPrefix+"Lbalance where fmonth ="+iniMonth+" and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode and a.FAuxiAcc=b.FAuxiAcc ) group by FAuxiAcc,kmdm ,fcurcode) c "
			       +" join (select facctcode, facctname, famount, facctattr, FAcctClass,FAcctLevel,FAcctParent from  "+YssTabPrefix+"Laccount where  fbalDC in (1, -1, 0) and  FAcctClass='权益类' and (FAcctName like '%未实现%' or Facctattr like '%未实现%') "
			       +" and  FAcctDetail=1 and FPortClsCode in(" + operSql.sqlCodes(portClsRank) + ")) d on c.kmdm = d.facctcode"
			       +" left join  "+YssTabPrefix+"AuxiAccSet e on e.auxiaccid=c.FAuxiAcc where c.fbendbal<>0 order by c.FAuxiAcc,c.kmdm"
				   + " ) group by Fauxiacc ";
				rs1=dbl.openResultSet(sql);
				while(rs1.next()){
			      	setClassPst(classPst, rs, date, "wsx",rs1.getDouble("FAEndBal"), 
			      			YssD.round(rs1.getDouble("FendBal"), digit), 
			      			YssD.round(rs1.getDouble("fbendbal"), digit));//资产净值
				}
				dbl.closeResultSetFinal(rs1);
			}
		}catch(Exception e){
			throw new YssException("生成分级组合相关的财务估值表合计项数据");
		}finally{
			dbl.closeResultSetFinal(rs,rs1);
		}
	}

	/**
	 * add by songjie 2012.11.22 
	 * STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * 获取TA分级组合子集数据
	 * @param rs
	 * @param portClsLevers
	 * @return
	 * @throws YssException
	 */
	private String getPortClsCodes(ResultSet rs, String portClsLevers) throws YssException{
		String strSql = "";
		ResultSet rs1 = null;
		boolean haveInfo = false;
		try{
			portClsLevers += rs.getString("FPortClsRank") + ",";
			
			strSql = " select * from " + pub.yssGetTableName("Tb_TA_PortCls") + 
			" where FCheckState = 1 and FParrentCode = " + dbl.sqlString(rs.getString("FPortClsCode")) +
			" and FPortCode = " + dbl.sqlString(this.sPortCode);
			rs1 = dbl.openResultSet(strSql);
			while(rs1.next()){
				haveInfo = true;
				portClsLevers += rs1.getString("FPortClsRank") + ",";
				getPortClsCodes(rs1, portClsLevers);
			}
			if(!haveInfo){
				return portClsLevers;
			}
			
			return portClsLevers;
		}catch(Exception e){
			throw new YssException("获取分级组合子集数据");
		}finally{
			dbl.closeResultSetFinal(rs1);
		}
	}
	
	/**
	 * add by songjie 2012.11.26
	 * STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * 获取父级实收资本数量
	 * @param portClsCode
	 * @param fSetCode
	 * @param date
	 * @return
	 * @throws YssException
	 */
	private double getParentPaidInCapital(String portClsCode,int fSetCode,Date date)throws YssException{
		String strSql = "";
		ResultSet rs = null;
		String parentCode = "";
		String sql = "";
		ResultSet rs1 = null;
		double paidInCapital = 0;
		try{
			strSql = " select * from " + pub.yssGetTableName("Tb_Ta_PortCls") + 
			" where FPortClsCode = " + dbl.sqlString(portClsCode) + 
			" and FCheckState = 1 ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				parentCode = rs.getString("FParrentCode");
			}
			
			dbl.closeResultSetFinal(rs);
			
			if(parentCode.trim().length() != 0){
				strSql = " select * from " + pub.yssGetTableName("Tb_Ta_PortCls") + 
				" where FParrentCode = " + dbl.sqlString(parentCode) + 
				" and FCheckState = 1 ";
				rs = dbl.openResultSet(strSql);
				
				while(rs.next()){
					sql = "select * from " + pub.yssGetTableName("TB_Rep_GuessValue")+ 
					" gv where FPORTCODE = " + fSetCode + 
					" and FDATE = " + dbl.sqlDate(date) + 
					" and Facctcode like '" + rs.getString("FPortClsRank") +"_%'" + 
					" and FAcctName = '实收资本' ";	
					rs1 = dbl.openResultSet(sql);
					if(rs1.next()){
						paidInCapital += rs1.getDouble("FAmount");
					}
					dbl.closeResultSetFinal(rs1);
				}
				
				dbl.closeResultSetFinal(rs);
			}
			return paidInCapital;
		}catch(Exception e){
			throw new YssException("获取TA组合分级数据出错");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2012.11.26
	 * STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * 获取父级组合级别
	 */
	private String getParentLever(String portClsCode)throws YssException{
		String strSql = "";
		ResultSet rs = null;
		String portClsRank = "";
		try{
			strSql = " select nvl(b.fportclsrank,a.fportclsrank) as FPortClsRank," +
					 " nvl(b.fportclscode,a.fportclscode) as fportclscode from " + pub.yssGetTableName("Tb_Ta_PortCls") + 
			         " a left join (select * from " + pub.yssGetTableName("Tb_Ta_PortCls") + 
			         " where FCheckState = 1 )b on a.FParrentCode = b.FPortClsCode " +
			         " where a.FPortClsCode = " + dbl.sqlString(portClsCode) + 
			         " and a.FCheckState = 1 ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				portClsRank = rs.getString("FPortClsRank")+"_"+rs.getString("fportclscode")+"_";
			}
			return portClsRank;
		}catch(Exception e){
			throw new YssException("获取父级组合级别出错");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2012.11.22 
	 * STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * 计算分级组合合计项 ：实收资本、今日单位净值、累计单位净值、日净值增长率
	 */
	private void getPaidInCapital(Hashtable hsUnitParentCode, PreparedStatement pst,PreparedStatement classPst, 
			ResultSet rs,int fSetCode, Date date, int iniMonth, int digit, String sumType) throws YssException{
		ResultSet rs1 = null;
		String sql = "";
		String strSql = "";
		double realizedAmount = 0;//已实现_数量
		double realizedEndBal = 0;//已实现_原币金额
		double realizedBEndBal = 0;//已实现_本位币金额
		double unRealizedAmount = 0;//未实现_数量
		double unRealizedEndBal = 0;//未实现_原币金额
		double unRealizedBEndBal = 0;//未实现_本位币金额
		double finalAmount = 0;
		double finalEndBal = 0;
		double finalBEndBal = 0;
		String portClsLevers = "";//分级组合  及 该分级 子级组合 对应的 分级级别 的 数据
		double amountOfPaidInCapital = 0;//实收资本_数量
		double moneyOfPaidInCapital = 0;//实收资本_金额
		double portclsNetValue = 0;
		double unitNetValue = 0;
		String subSql = "";
		double baseRate = 0;
		double portRate = 0;
		try{
			//获取组合分级对应的所有TA分级级别数据，用,隔开
			portClsLevers = getPortClsCodes(rs, "");
			
			if(portClsLevers.length() > 1){
				portClsLevers = portClsLevers.substring(0,portClsLevers.length() - 1);
			}
			
			if(portClsLevers.indexOf(",") != -1){
				String[] portCls = portClsLevers.split(",");
				for(int i = 0; i < portCls.length; i++){
					subSql += "class\t" + portCls[i] + ",";
				}
				
				if(subSql.length() > 1){
					subSql = subSql.substring(0,subSql.length() - 1);
				}
			}else{
				subSql = "class\t" + portClsLevers;
			}
			
			//基础汇率
			baseRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  rs.getString("FCuryCode"), 
		  				  this.sPortCode, 
		  				  YssOperCons.YSS_RATE_BASE);
		   	//组合汇率
		   	portRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  "", 
		  				  sPortCode, 
		  				  YssOperCons.YSS_RATE_PORT);
			
			//实收资本						
			sql = " select rgv.*,tcfdg.* from (" +
				  " select substr(gv.facctclass,7) as FAcctClassCode," +
				  " gv.fportcode as FassetCode, " +
				  " gv.famount as famount," +
				  " gv.FmarketValue as FmarketValue," +
				  " gv.Fstandardmoneymarketvalue as Fstandardmoneymarketvalue from " +pub.yssGetTableName(sGVTableName)+ 
				  " gv where FPORTCODE = " + dbl.sqlString(Integer.toString(fSetCode)) + 
				  " and FDATE = " + dbl.sqlDate(date) +
				  " and facctclass in( " + operSql.sqlCodes(subSql) + ")" +
				  " and FACCTATTR = " + dbl.sqlString("实收资本") + " ) rgv " +
				  " left join " +
				  " ( select * from " + pub.yssGetTableName("tb_ta_portcls") + " ) ta " +
				  " on rgv.FAcctClassCode = ta.fportclsrank and ta.fportcode = " + dbl.sqlString(this.sPortCode) +
				  " left join ( " + 
				  " select tcfdg1.* from (" +
				  " select FPORTCLSCODE, FPORTCODE, FCURYCODE ,max(FSTARTDATE) as FSTARTDATE " + 
                  " from " + pub.yssGetTableName("TB_Ta_Classfunddegree")+ " where FCHECKSTATE = 1 " +
                  " group by FPORTCLSCODE, FPORTCODE, FCURYCODE ) tcfdg1 " +
                  " left join " +
                  	pub.yssGetTableName("TB_Ta_Classfunddegree") +
                  " tcfdg2 on tcfdg1.FPORTCODE = tcfdg2.FPORTCODE " +
                  " and tcfdg1.FPORTCLSCODE = tcfdg2.FPORTCLSCODE " + 
                  " and tcfdg1.FCURYCODE = tcfdg2.FCURYCODE " +
                  " and tcfdg1.FSTARTDATE = tcfdg2.FSTARTDATE " +
				  " ) tcfdg " +
				  " on tcfdg.fportcode = " + dbl.sqlString(this.sPortCode)+
				  " and ta.FPORTCLSCODE = tcfdg.FPORTCLSCODE ";
			rs1 = dbl.queryByPreparedStatement(sql); 	
			while(rs1.next()){
				amountOfPaidInCapital += YssD.round(rs1.getDouble("famount"),2);
				moneyOfPaidInCapital += rs1.getDouble("FStandardMoneyMarketValue");
			}
			dbl.closeResultSetFinal(rs1);
			
			if(sumType.equals("05")){//实收资本
		      	setClassPst(classPst, rs, date, "05",0, amountOfPaidInCapital, 
		      			YssD.round(moneyOfPaidInCapital,digit));//实收资本
			}
			
			//累计单位净值
			CtlNavRep navRep = new CtlNavRep();
			navRep.setYssPub(this.pub);
			navRep.setPortCode(this.sPortCode);
			
			//获取上级分级组合上级本位币资产净值
			portclsNetValue = this.getAssetNetValue(date, "" + fSetCode , 
					getParentLever(rs.getString("FPortClsCode"))
					 + "9600","Fstandardmoneymarketvalue");
			
			//获取今日上级分级组合实收资本_数量
			double superAmount = getParentPaidInCapital(rs.getString("FPortClsCode"), fSetCode,date);
			
			if(superAmount != 0){
				//今日单位净值 = 今日分级组合本位币资产净值 / 今日分级组合实收资本_数量
				unitNetValue = YssD.round(YssD.div(portclsNetValue, superAmount) , digit);
				
				if(!rs.getString("FCuryCode").equals(pub.getPortBaseCury(sPortCode))){
					//unitNetValue = YssD.round(YssD.div(YssD.mul(YssD.div(portclsNetValue, superAmount), portRate), baseRate),4);
					unitNetValue = YssD.round(YssD.div(YssD.mul(unitNetValue, portRate), baseRate),4);
				}
			}
			
			if (sumType.equals("22")) {// 今日单位净值
		      	setClassPst(classPst, rs, date, "22",0, unitNetValue, unitNetValue);//单位净值
			}
			
			//昨日单位净值
			double yesUnitNetValue = getSingleMuticlassNet(this.sPortCode, rs.getString("FPortClsCode"), "22", 
					YssFun.addDate(date, -1, Calendar.DAY_OF_MONTH), "FClassNetValue");
			
			if(sumType.equals("09")){//日净值增长率
				//	   A类CNY净值日增长率   = （A类CNY今日单位净值 -（A类CNY昨日单位净值 - TA交易数据：当日：现金分红：A类CNY：销售价格））
				//                       / （A类CNY昨日单位净值 - TA交易数据：当日：现金分红：A类CNY：销售价格）
				double unitBonusAmount = getTATradeDividendInfo(date, rs.getString("FPORTCLSCODE"), false);//TA交易数据：当日：现金分红：分级组合：销售价格
				double dayGrowthRate = 0;//日净值增长率
				//A类CNY昨日单位净值 - TA交易数据
				double a = YssD.sub(yesUnitNetValue, unitBonusAmount);
				if(a == 0){
					dayGrowthRate = 0;
				}else{
					 dayGrowthRate = YssD.div(YssD.sub(unitNetValue,
                             YssD.sub(yesUnitNetValue, unitBonusAmount)), 
                             YssD.sub(yesUnitNetValue, unitBonusAmount));
				}

		      	setClassPst(classPst, rs, date, "09",0, 0, 
		      			YssD.round(dayGrowthRate,4));//日净值增长率
			}
			
			//累计单位净值
			double sumDivided = getPortClsSplitUnit(unitNetValue , date ,this.sPortCode, rs.getString("FPORTCLSCODE")); //考虑基金分级拆分
			
			if(!rs.getString("FCuryCode").equals(pub.getPortBaseCury(sPortCode))){
				sumDivided = YssD.round(sumDivided,4);
			}else{
				sumDivided = YssD.round(sumDivided,3);
			}
			
			if(sumType.equals("08")){//累计单位净值
		      	setClassPst(classPst, rs, date, "08",0, sumDivided, sumDivided);//累计单位净值
			}
			
//			可分配收益_未实现_分级组合
//		    = 凭证中分级级别相关的  科目性质  like '%本期利润_未实现%' 以及 科目性质  like '%利润分配_未实现%' 的本位币金额 累计值
				
//			可分配收益_已实现_分级组合
//			= 凭证中分级级别相关的  科目性质  like '%本期利润_已实现%' 以及 科目性质  like '%利润分配_已实现%' 的本位币金额 累计值
				strSql = getSumValueSql("本期收益_未实现", portClsLevers,date,iniMonth);
			
				rs1 = dbl.openResultSet(strSql);
				if(rs1.next()){
					unRealizedAmount += rs1.getDouble("FAEndBal");//数量
					unRealizedEndBal += rs1.getDouble("FEndBal");//原币金额
					unRealizedBEndBal += rs1.getDouble("FBEndbal");//本位币金额
				}
				
				dbl.closeResultSetFinal(rs1);
				rs1 = null;
				
				strSql = getSumValueSql("利润分配_未分配利润_未实现", portClsLevers,date,iniMonth);
				
				rs1 = dbl.openResultSet(strSql);
				if(rs1.next()){
					unRealizedAmount += rs1.getDouble("FAEndBal");//数量
					unRealizedEndBal += rs1.getDouble("FEndBal");//原币金额
					unRealizedBEndBal += rs1.getDouble("FBEndbal");//本位币金额
				}
				
				dbl.closeResultSetFinal(rs1);
				rs1 = null;
				
				strSql = getSumValueSql("本期收益_已实现", portClsLevers,date,iniMonth);
				
				rs1 = dbl.openResultSet(strSql);
				if(rs1.next()){
					realizedAmount += rs1.getDouble("FAEndBal");//数量
					realizedEndBal += rs1.getDouble("FEndBal");//原币金额
					realizedBEndBal += rs1.getDouble("FBEndbal");//本位币金额
				}
				
				dbl.closeResultSetFinal(rs1);
				rs1 = null;
				
				strSql = getSumValueSql("利润分配_未分配利润_已实现", portClsLevers,date,iniMonth);
				
				rs1 = dbl.openResultSet(strSql);
				if(rs1.next()){
					realizedAmount += rs1.getDouble("FAEndBal");//数量
					realizedEndBal += rs1.getDouble("FEndBal");//原币金额
					realizedBEndBal += rs1.getDouble("FBEndbal");//本位币金额
				}
				
				dbl.closeResultSetFinal(rs1);
				rs1 = null;
				
//				可分配收益_分级组合
//				= if (可分配收益_未实现_分级组合 > 0) then 可分配收益_已实现_分级组合 - 可分配收益_未实现_分级组合
//				  else                              then 可分配收益_已实现_分级组合
				if(unRealizedBEndBal > 0){
					finalEndBal = realizedEndBal;
					finalBEndBal = realizedBEndBal;					
				}else{
					finalEndBal = YssD.add(realizedEndBal, unRealizedEndBal);
					finalBEndBal = YssD.add(realizedBEndBal, unRealizedBEndBal);
				}
				
				if(sumType.equals("27")){//可分配收益
			      	setClassPst(classPst, rs, date, "27",finalAmount, 
			      			YssD.round(finalBEndBal, 2), 
			      			YssD.round(finalBEndBal, 2));//可分配收益
				}
				
				if(sumType.equals("28")){//单位可分配收益
					//单位可分配收益_A类 = round（可分配收益_分级组合/基金实收资本_分级组合_数量，4）
			      	setClassPst(classPst, rs, date, "28",finalAmount, 
			      			YssD.round(YssD.div(finalBEndBal, amountOfPaidInCapital), 4), 
			      			YssD.round(YssD.div(finalBEndBal, amountOfPaidInCapital), 4));//单位可分配收益
				}
				
//				   分级组合期初单位净值 
//				   = if（当年成立基金）
//				         then 取基金份额成本设置中的分级组合基金份额成本金额
//					 else
//					     then 取上一年最后一天的分级组合单位净值
		    	boolean firstYear = false;
		    	double openingUnitNet = 0;//期初单位净值 
		    	//判断是否当年成立基金
		    	strSql = " select min(FYear) as FYear,FSetCode from LSetList where FSetCode = " + fSetCode + " group by FSetCode ";
		    	rs1 = dbl.openResultSet(strSql);
		    	if(rs1.next()){
		    		//edit by songjie 2013.01.04 new Date() 改为  date BUG 6807 QDV4建行2013年01月04日02_B
		    		if((rs1.getInt("FYear") + "").equals(YssFun.formatDate(date,"yyyy"))){
		    			firstYear = true;
		    		}
		    	}
		    	
		    	dbl.closeResultSetFinal(rs1);
		    	
		    	//if（当年成立基金） 取基金份额成本设置中的A类CNY基金份额成本金额
		    	if(firstYear){
		    		strSql = " select max(FStartDate) as FStartDate,FPortClsCode,FDEGREECOST from " + 
		    		pub.yssGetTableName("Tb_Ta_Classfunddegree") + " where FCheckState = 1 and FPortClsCode = " + 
		    		dbl.sqlString(rs.getString("FPortClsCode")) + " and FStartDate <= " + dbl.sqlDate(date) + 
		    		" and FPortCode = " + dbl.sqlString(this.sPortCode) + " group by FPortClsCode,FDEGREECOST ";
		    		rs1 = dbl.openResultSet(strSql);
		    		if(rs1.next()){
		    			openingUnitNet = rs1.getDouble("FDEGREECOST");//基金份额成本
		    		}
		    	//否则 取上一年最后一天的A类CNY单位净值
		    	}else{
		    		//--- edit by songjie 2013.01.07 BUG 6843 QDV4建行2013年01月07日01_B start---//
		    		//--- edit by songjie 2013.01.07 BUG 6855 QDV4建行2013年01月07日02_B start---//
		    		strSql = " select FClassNetValue from " + pub.yssGetTableName("Tb_Data_MultiClassNet") + " where FNavDate = " +
		    		dbl.sqlDate(YssFun.toDate((Integer.parseInt(YssFun.formatDate(date,"yyyyMMdd").substring(0,4)) - 1 + "-12-31"))) +
		    		" and FType = '22' and FCuryCode = " + dbl.sqlString(rs.getString("FPortClsCode")) +
		    		" and FPortCode = " + dbl.sqlString(this.sPortCode);
		    		rs1 = dbl.openResultSet(strSql);
		    		if(rs1.next()){
		    			openingUnitNet = rs1.getDouble("FClassNetValue");
		    		}
		    		//--- edit by songjie 2013.01.07 BUG 6855 QDV4建行2013年01月07日02_B end---//
		    		//--- edit by songjie 2013.01.07 BUG 6843 QDV4建行2013年01月07日01_B end---//
//		    		strSql = " select FStandardMoneyMarketValue from " + pub.yssGetTableName(sGVTableName) + 
//		    		//edit by songjie 2013.01.04 获取上年最后一天的逻辑错误  BUG 6807 QDV4建行2013年01月04日02_B
//		    		" where FDate = " + dbl.sqlDate(YssFun.toDate((Integer.parseInt(YssFun.formatDate(date,"yyyyMMdd").substring(0,4)) - 1 + "-12-31"))) +
//		    		//--- edit by songjie 2013.01.07 BUG 6843 QDV4建行2013年01月07日01_B start---//
//		    		" and FAcctCode = " + dbl.sqlString(rs.getString("FPortClsRank") +"_" + rs.getString("FPortClsCode") + "_9600") + 
//		    		" and FAcctClass = "+ dbl.sqlString("class\t" + rs.getString("FPortClsRank")) +
//		    		" and FPortCode = " + fSetCode + " and FCurCode = ' '";
//		    		//--- edit by songjie 2013.01.07 BUG 6843 QDV4建行2013年01月07日01_B end---//
//		    		rs1 = dbl.openResultSet(strSql);
//		    		if(rs1.next()){
//		    			openingUnitNet = rs1.getDouble("FStandardMoneyMarketValue");//去年最后一天的分级组合对应的单位净值
//		    		}
		    	}
		    	
		    	dbl.closeResultSetFinal(rs1);
		    	
		    	if(sumType.equals("30")){//期初单位净值
		    		if(!rs.getString("FCuryCode").equals(pub.getPortBaseCury(sPortCode))){
		    			setClassPst(classPst, rs, date, "30",0,
		    					YssD.round(openingUnitNet, 4),
		    					YssD.round(openingUnitNet, 4));//期初单位净值
		    		}else{
		    			setClassPst(classPst, rs, date, "30",0,
		    					YssD.round(openingUnitNet, 3),
		    					YssD.round(openingUnitNet, 3));//期初单位净值
		    		}
		    	}
		    	
		    	if(sumType.equals("31")){//昨日单位净值
		    		if(!rs.getString("FCuryCode").equals(pub.getPortBaseCury(sPortCode))){
		    			setClassPst(classPst, rs, date, "31",0,
		    					YssD.round(yesUnitNetValue, 4),
		    					YssD.round(yesUnitNetValue, 4));//期初单位净值
		    		}else{
		    			setClassPst(classPst, rs, date, "31",0,
		    					YssD.round(yesUnitNetValue, 3),
		    					YssD.round(yesUnitNetValue, 3));//期初单位净值
		    		}
		    	}
		    	
		    	//A类CNY本期净值增长率 = A类CNY今日单位净值/A类CNY期初单位净值 - 1
		    	double currentNetGrowthRate = YssD.round(YssD.div(unitNetValue, ((openingUnitNet == 0) ? 1 : openingUnitNet)) - 1, 4);
		    	if(sumType.equals("10")){//本期净值增长率
					setClassPst(classPst, rs, date, "10",0,0,currentNetGrowthRate);//本期净值增长率
		    	}
		    	
		    	//累计净值增长率
		    	CtlNavRep ctlNav = new CtlNavRep();
		    	ctlNav.setYssPub(pub);
		    	ctlNav.dDate = date;
		    	ctlNav.portCode = this.sPortCode;
		    	//edit by songjie 2013.01.04 new Date() 改为 已选日期  BUG 6807 QDV4建行2013年01月04日02_B
		    	double totalNetGrowthRate = YssD.round(ctlNav.getAccumulateScale(date,rs.getString("FPortClsCode"),unitNetValue,4,false), 4);
		    	if(sumType.equals("11")){//累计净值增长率
					setClassPst(classPst, rs, date, "11",0,0,totalNetGrowthRate);//累计净值增长率
		    	}
		    	
		}catch(Exception e){
			throw new YssException("计算合计项出错", e);
		}finally{
			dbl.closeResultSetFinal(rs1);
		}
	}
	
	/**
	 * add by songjie 2012.11.24 
	 * STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * @param classPst
	 * @param rs
	 * @param date
	 * @param TypeCode
	 * @param amount
	 * @param netValue
	 * @param classNetValue
	 * @throws YssException
	 */
	private void setClassPst(PreparedStatement classPst, ResultSet rs, Date date, String TypeCode,
			double amount, double netValue, double classNetValue) 
	throws YssException {
		try {
	        classPst.setDate(1, YssFun.toSqlDate(date));
	        classPst.setString(2, this.sPortCode);
	        classPst.setString(3, TypeCode);
	        classPst.setString(4, "CNY");//本位币
	        classPst.setString(5, rs.getString("FPORTCLSCODE"));//分级组合代码
	        classPst.setDouble(6, amount);
	        classPst.setDouble(7 ,0.0);
	        classPst.setDouble(8, 0.0);        
	        classPst.setDouble(9, netValue);//FNetValue
	        classPst.setDouble(10, classNetValue);//本位币金额 FClassNetValue
	        classPst.setInt(11, 1);
	        classPst.setString(12, pub.getUserCode());
	        classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
	        classPst.setDouble(14, 0);
	        classPst.setDouble(15, 0);
	        
	        classPst.executeUpdate();  
		} catch (Exception e) {
			throw new YssException("多Class资产净值表赋值出错");
		}
	}
	
	/**
	 * add by songjie 2012.11.24 
	 * STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * @param pst
	 * @param rs
	 * @param date
	 * @param acctCode
	 * @param fSetCode
	 * @param amount
	 * @param marketValue
	 * @param standardMoneyMarketValue
	 * @throws YssException
	 */
	private void setGvPst(PreparedStatement pst, ResultSet rs, Date date, String acctCode,
			String curyCode,String acctName, String acctAttr, String acctClass,
			int fSetCode, double amount, double marketValue, double standardMoneyMarketValue,
			int acctLever, int acctDetail) 
	throws YssException {
		try {
			pst.setString(1, fSetCode + "");
			pst.setDate(2, new java.sql.Date(date.getTime()));
			pst.setString(3, rs.getString("FPortClsRank") + "_" + rs.getString("fportclscode") + "_" + acctCode);
			pst.setString(4, curyCode);
			pst.setString(5, acctName);
			pst.setString(6, acctAttr);
			pst.setString(7, acctClass);// 标志按照基准资产份额分级
			pst.setDouble(8, 0);
			pst.setDouble(9, amount);
			pst.setDouble(10, 0);
			pst.setDouble(11, 0);
			pst.setDouble(12, 0);
			pst.setDouble(13, 0);
			pst.setDouble(14, 0);
			pst.setDouble(15, 0);
			pst.setDouble(16, 0);
			pst.setDouble(17, 0);
			pst.setDouble(18, marketValue);// 原币市值
			pst.setDouble(19, standardMoneyMarketValue);// 本位币市值
			pst.setDouble(20, 0);
			pst.setDouble(21, 0);
			pst.setDouble(22, 0);
			pst.setDouble(23, 0);
			pst.setString(24, " ");
			pst.setInt(25, acctLever);
			pst.setInt(26, acctDetail);
			pst.setString(27, " ");

			pst.executeUpdate();
		} catch (Exception e) {
			throw new YssException("财务估值表赋值出错");
		}
	}
	
	/**
	 * add by songjie 2012.11.22 
	 * STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * 获取TA交易数据 分红业务的销售价格 
	 * @return
	 */
	private double getTATradeDividendInfo(Date endDate, String clsPortCode, boolean total) throws YssException{
		double accummulate = 0.0;
		String sqlStr = null;
		ResultSet rs = null;
		String sign = total ? " <= " : " = ";
		try{
			// 得到当天的累计分红
			sqlStr = "select sum(FSellPrice) as FAccumulateDivided from " + pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 and FConfimDate " + sign + dbl.sqlDate(endDate) + " and FSellType = '03' "
					+ " and FPortCode = " + dbl.sqlString(this.sPortCode) + " and Fportclscode = "
					+ dbl.sqlString(clsPortCode);
			rs = dbl.openResultSet(sqlStr);
			if (rs.next()) {
				accummulate = rs.getDouble("FAccumulateDivided"); // 获取汇总的值
			}
			
			return accummulate;
		}catch(Exception e){
			 throw new YssException("获取 TA分红数据出错");
		}finally{
			 dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2012.11.21 
	 * STORY #3264 需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * 计算 本期利润_已实现 科目 本位币金额 累计值
	 * @return
	 */
	private void getRealizedCurrentIncome(PreparedStatement pst,PreparedStatement classPst, ResultSet rs,
			int fSetCode,Date date, 
			int iniMonth, int digit) throws YssException{
		String strSql = "";
		ResultSet rs1 = null;
		double baseRate = 0;
		double portRate = 0;
		try{
			//基础汇率
			baseRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  rs.getString("FCuryCode"), 
		  				  this.sPortCode, 
		  				  YssOperCons.YSS_RATE_BASE);
		   	//组合汇率
		   	portRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  "", 
		  				  sPortCode, 
		  				  YssOperCons.YSS_RATE_PORT);
			
			strSql = getSumValueSql("本期收益_已实现", rs.getString("FPortClsRank"),date,iniMonth);
			rs1 = dbl.openResultSet(strSql);
			if(rs1.next()){
				setClassPst(classPst, rs, date, "24",rs1.getDouble("FAEndBal"),
						YssD.round(rs1.getDouble("fbendbal"), digit),
						YssD.round(rs1.getDouble("fbendbal"), digit));// 本期收益_已实现
			}
		}catch(Exception e){
			throw new YssException("计算【本期收益_已实现】出错");
		}finally{
			dbl.closeResultSetFinal(rs1);
		}
	}
	
	/**
	 * add by songjie 2012.11.21 
	 * STORY #3264 需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * 计算 利润分配_未分配利润_未实现  科目 本位币金额 累计值
	 * @return
	 */
	private void getUnRealizedUndistributedProfit(PreparedStatement pst,PreparedStatement classPst,int digit,
			ResultSet rs,int fSetCode,Date date, int iniMonth) throws YssException{
		String strSql = "";
		ResultSet rs1 = null;
		double baseRate = 0;
		double portRate = 0;
		try{
			//基础汇率
			baseRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  rs.getString("FCuryCode"), 
		  				  this.sPortCode, 
		  				  YssOperCons.YSS_RATE_BASE);
		   	//组合汇率
		   	portRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  "", 
		  				  sPortCode, 
		  				  YssOperCons.YSS_RATE_PORT);
			strSql = getSumValueSql("利润分配_未分配利润_未实现", rs.getString("FPortClsRank"),date,iniMonth);

			rs1 = dbl.openResultSet(strSql);
			if(rs1.next()){
				setClassPst(classPst, rs, date, "26",rs1.getDouble("FAEndBal"),
						YssD.round(rs1.getDouble("fbendbal"), digit),
						YssD.round(rs1.getDouble("fbendbal"), digit));// 利润分配_未分配利润_未实现
			}
		}catch(Exception e){
			throw new YssException("计算【 利润分配_未分配利润_未实现】出错");
		}finally{
			dbl.closeResultSetFinal(rs1);
		}
	}
	
	/**
	 * add by songjie 2012.11.21 
	 * STORY #3264 需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * 计算 本期利润_未实现 科目 本位币金额 累计值
	 * @return
	 */
	private void getUnRealizedCurrentIncome(PreparedStatement pst,PreparedStatement classPst,ResultSet rs,
			int fSetCode,Date date,int iniMonth, int digit) throws YssException{
		String strSql = "";
		ResultSet rs1 = null;
		double baseRate = 0;
		double portRate = 0;
		try{
			//基础汇率
			baseRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  rs.getString("FCuryCode"), 
		  				  this.sPortCode, 
		  				  YssOperCons.YSS_RATE_BASE);
		   	//组合汇率
		   	portRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  "", 
		  				  sPortCode, 
		  				  YssOperCons.YSS_RATE_PORT);
			strSql = getSumValueSql("本期收益_未实现", rs.getString("FPortClsRank"),date,iniMonth);

			rs1 = dbl.openResultSet(strSql);
			if(rs1.next()){
				setClassPst(classPst, rs, date, "25",rs1.getDouble("FAEndBal"),
						YssD.round(rs1.getDouble("fbendbal"), digit),
						YssD.round(rs1.getDouble("fbendbal"), digit));// 本期收益_未实现
			}
		}catch(Exception e){
			throw new YssException("计算【本期收益_未实现】出错");
		}finally{
			dbl.closeResultSetFinal(rs1);
		}
	}
	
	/**
	 * add by songjie 2012.11.22
	 * STORY #3264 需求北京-[建设银行]QDIIV4.0[紧急]20121108002
	 * @param acctAttr 科目性质
	 * @param portClsCode 分级级别
	 * @param date 业务日期
	 * @param iniMonth 上个月的日期 只明细到月份
	 * @return
	 */
	private String getSumValueSql(String acctAttr, String portClsCode,Date date,int iniMonth){
		String strSql = 
	          " select c.*, d.*, ' ' as fcurcode from (select kmdm, sum(fendbal) as fendbal, sum(fbendbal) as fbendbal, sum(faendbal) as faendbal from  ( "
	          + " select (case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else a.fcyid end) as fcurcode, "
	          + " (case when b.fendbal is null then 0 else b.fendbal end) - (case when fjje is null then 0 else fjje end) + (case when fdje is null then 0 else fdje end) as fendbal, "
	          + " (case when b.fbendbal is null then 0 else b.fbendbal end) - (case when fbjje is null then 0 else fbjje end) + (case when fbdje is null then 0 else fbdje end) as fbendbal, "
	          + " (case when b.faendbal is null then 0 else b.faendbal end) - (case when fjsl is null then 0 else fjsl end) + (case when fdsl is null then 0 else fdsl end) as faendbal "
	          + " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje, "
	          + " sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl, "
	          + " sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje, "
	          + " sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "+YssTabPrefix+"fcwvch "
	          + " where fterm =" + YssFun.formatDate(date, "MM") + " and " + dbl.sqlDay("FDate")+ " <= "+ YssFun.formatDate(date, "dd")
	          + " group by fkmh, fcyid, fterm) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit," 
	          + "-1 * fendbal as fendbal,fbaccdebit,fbacccredit,-1 * fbendbal as fbendbal,faaccdebit,-1 * faendbal as faendbal,FAuxiAcc from "
	          + YssTabPrefix+"Lbalance where fmonth = "+iniMonth+" and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) group by kmdm ) c "
	          + " join (select facctcode, facctname, famount, facctattr, FAcctClass, FAcctLevel, FAcctParent, FPORTCLSCODE from  "+YssTabPrefix+"Laccount " 
	          + "where fbalDC in (1, -1, 0) and FAcctClass = '权益类' and (FAcctName like '%" + acctAttr + "%' or Facctattr like '%" + acctAttr + "%') "
	          + " and FAcctDetail = 1 and FPortClsCode in (" + operSql.sqlCodes(portClsCode) + ")) d " 
	          + " on c.kmdm = d.facctcode  where c.fbendbal <> 0  and d.FPORTCLSCODE <> ' ' order by d.FPORTCLSCODE,c.kmdm";
		return strSql;
	}
	
	private void dealNetVaulePortCls(ResultSet rs,int fSetCode,Date date,int digit,int intMonth){
		//modify by fangjiang 2012.01.05 STORY #2013
		try{
			String sql="";
			dbl.loadConnection().setAutoCommit(false);
			char c = 'A';
			sql = " select a.FCuryCode, FCuryName from " + pub.yssGetTableName("TB_Ta_Classfunddegree")
				  + " a join " + pub.yssGetTableName("TB_para_currency") + " b on a.FCuryCode = b.FCuryCode " 
				  + " where a.FCheckState = 1 and a.Fportcode = " + dbl.sqlString(sPortCode);
			rs = dbl.queryByPreparedStatement(sql); 				
			while (rs.next()) {
				
				sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'未实现损益平准金_多Class_" + rs.getString("FCuryName") + "','未实现损益平准金_多Class_" 
					+ rs.getString("FCuryName") + "','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '" + c + "8803' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode where fmonth="
					+
					intMonth
					+
					" and  facctattr like '损益平准金_未实现%' and facctdetail=1 and a.FcurCode = " + dbl.sqlString(rs.getString("FCuryCode")) 
					+ " union all "
					+ " select '" + c + "8803' as facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+ " where c.fdate>=" + dbl.sqlDate(StartDate)
					+ " and c.fdate<= " + dbl.sqlDate(date) + " and facctattr like '损益平准金_未实现%' and FcyId = " + dbl.sqlString(rs.getString("FCuryCode")) 
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
				
				dbl.executeSql(sql);
				
				sql = " insert into  "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ " ,facctcode,fcurcode,'  —申购','未实现损益平准金_多Class_" + rs.getString("FCuryName") + "','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from "
						+ "("
						+ " select '" + c + "8804' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
						+ YssTabPrefix
						+ "lbalance a inner join "
						+ YssTabPrefix
						+ "laccount b on a.facctcode=b.facctcode where fmonth="
						+
						intMonth
						+
						" and  facctattr like '损益平准金_未实现_申购' and facctdetail=1 and a.FcurCode = " + dbl.sqlString(rs.getString("FCuryCode")) 
						+ " union all "
						+ " select '" + c + "8804' as facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
						+ YssTabPrefix
						+ "fcwvch  c inner join "
						+ YssTabPrefix
						+ "laccount d on c.fkmh=d.facctcode "
						+ " where c.fdate>=" + dbl.sqlDate(StartDate)
						+ " and c.fdate<= " + dbl.sqlDate(date) + " and facctattr like '损益平准金_未实现_申购' and FcyId = " + dbl.sqlString(rs.getString("FCuryCode"))
						+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
						+ " ) balance group by facctcode,fcurcode";
				
				dbl.executeSql(sql);
				
				sql = " insert into  "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ " ,facctcode,fcurcode,'  —赎回','未实现损益平准金_多Class_" + rs.getString("FCuryName") + "','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
						+ "("
						+ " select '" + c + "8805' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
						+ YssTabPrefix
						+ "lbalance  a inner join "
						+ YssTabPrefix
						+ "laccount b on a.facctcode=b.facctcode where fmonth="
						+
						intMonth
						+
						" and  facctattr like '损益平准金_未实现_赎回' and facctdetail=1 and a.FcurCode = " + dbl.sqlString(rs.getString("FCuryCode")) 
						+ " union all "
						+ " select '" + c + "8805' as facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
						+ YssTabPrefix
						+ "fcwvch  c inner join "
						+ YssTabPrefix
						+ "laccount d on c.fkmh=d.facctcode "
						+ " where c.fdate>=" + dbl.sqlDate(StartDate)
						+ " and c.fdate<= " + dbl.sqlDate(date) + " and facctattr like '损益平准金_未实现_赎回' and FcyId = " + dbl.sqlString(rs.getString("FCuryCode"))
						+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
						+ " ) balance group by facctcode,fcurcode";
				
				dbl.executeSql(sql);
				
				sql = " insert into  "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ " ,facctcode,fcurcode,'已实现损益平准金_多Class_" + rs.getString("FCuryName") 
						+ "','已实现损益平准金_多Class_" + rs.getString("FCuryName") +"','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
						+ "("
						+ " select '" + c + "8806' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
						+ YssTabPrefix
						+ "lbalance  a inner join "
						+ YssTabPrefix
						+ "laccount b on a.facctcode=b.facctcode   where fmonth="
						+
						intMonth
						+
						" and  facctattr like '损益平准金_已实现%' and facctdetail=1 and a.FcurCode = " + dbl.sqlString(rs.getString("FCuryCode"))
						+ " union all "
						+ " select '" + c + "8806' as facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
						+ YssTabPrefix
						+ "fcwvch c inner join "
						+ YssTabPrefix
						+ "laccount d on c.fkmh=d.facctcode "
				        + " where c.fdate>=" + dbl.sqlDate(StartDate)
		        		+ " and c.fdate<= " + dbl.sqlDate(date) + " and facctattr like '损益平准金_已实现%' and FcyId = " + dbl.sqlString(rs.getString("FCuryCode"))
		        		+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
						+ " ) balance group by facctcode,fcurcode";
				
				dbl.executeSql(sql);
				
				sql = " insert into  "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ " ,facctcode,fcurcode,'  —申购','已实现损益平准金_多Class_" + rs.getString("FCuryName") + "','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
						+ "("
						+ " select '" + c + "8807' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
						+ YssTabPrefix
						+ "lbalance a inner join "
						+ YssTabPrefix
						+ "laccount b on a.facctcode=b.facctcode where fmonth="
						+
						intMonth
						+
						" and facctattr like '损益平准金_已实现_申购' and facctdetail=1 and a.FcurCode = " + dbl.sqlString(rs.getString("FCuryCode"))
						+ " union all "
						+ " select '" + c + "8807' as facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
						+ YssTabPrefix
						+ "fcwvch c inner join "
						+ YssTabPrefix
						+ "laccount d on c.fkmh=d.facctcode "
						+ " where c.fdate>=" + dbl.sqlDate(StartDate)
						+ " and c.fdate<= " + dbl.sqlDate(date) + " and facctattr like '损益平准金_已实现_申购' and FcyId = " + dbl.sqlString(rs.getString("FCuryCode"))
						+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
						+ " ) balance group by facctcode,fcurcode";
				
				dbl.executeSql(sql);
				
				sql = " insert into  "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ " ,facctcode,fcurcode,'  —赎回','已实现损益平准金_多Class_" + rs.getString("FCuryName") + "','合计',0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,' ',0,0,' ' from  "
						+ "("
						+ " select '" + c + "8808' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
						+ YssTabPrefix
						+ "lbalance a inner join "
						+ YssTabPrefix
						+ "laccount b on a.facctcode=b.facctcode where fmonth="
						+
						intMonth
						+
						" and  facctattr like '损益平准金_已实现_赎回' and facctdetail=1 and a.FcurCode = " + dbl.sqlString(rs.getString("FCuryCode"))
						+ " union all "
						+ " select '" + c + "8808' as facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
						+ YssTabPrefix
						+ "fcwvch  c inner join "
						+ YssTabPrefix
						+ "laccount d on c.fkmh=d.facctcode "
						+ " where c.fdate>=" + dbl.sqlDate(StartDate)
						+ " and c.fdate<= " + dbl.sqlDate(date) + " and facctattr like '损益平准金_已实现_赎回' and FcyId = " + dbl.sqlString(rs.getString("FCuryCode"))
						+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
						+ " ) balance group by facctcode,fcurcode";
				dbl.executeSql(sql);
				
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ", '" + c + "9000',' ','" + "资产净值_多Class_" + rs.getString("FCuryName")
					+ "','" + "资产净值_多Class_" + rs.getString("FCuryName") + "','合计',0,0,0,"
					+ "round(FClassNetValue," + digit + "),0,0,0,0,0,0,0,"
					+ "round(FClassNetValue," + digit + "),0,0,0,0,' ',0,0,' '"
					+ " from " + pub.yssGetTableName("tb_data_multiclassnet")
					+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) 
					+ " and FType = '01' and Fcurycode = " + dbl.sqlString(rs.getString("FCuryCode"));
			
			    dbl.executeSql(sql);
						
				sql = " insert into  "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ " ,facctcode,fcurcode,'" + "实收资本_多Class_" + rs.getString("FCuryName") + "'," + "'" + "实收资本_多Class_" + rs.getString("FCuryName") + "','合计',0,sum(faendbal) as faendbal,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,0,0,0,0,0,0,0,0,0,0,0,0,' ',0,0,' ' from  "
					+ "("
					+ " select '" + c + "8700' as facctcode,' ' as fcurcode,(faendbal*fbaldc) as faendbal,(fendbal*fbaldc) as fendbal,(fbendbal*fbaldc) as fbendbal from "
					+ YssTabPrefix
					+ "lbalance  a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode where fmonth="
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					intMonth
					+
					// ------------------------------------------------------------------------------
					" and facctattr like '实收资本%' and facctdetail=1 and a.FcurCode = " + dbl.sqlString(rs.getString("FCuryCode")) 
					+ " union all "
					+ " select '" + c + "8700' as facctcode,' ' as fcurcode,(case when fjd='D' then -1*fsl else fsl end)*fbaldc as faendbal,(case when fjd='D' then -1* fbal else fbal end)*fbaldc as fendbal,(case when fjd='D' then -1* fbbal else fbbal end)*fbaldc as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch  c inner join "
					+ YssTabPrefix
					+ "laccount d on c.fkmh=d.facctcode "
					+ " where c.fdate>= " + dbl.sqlDate(StartDate)
					+ " and c.fdate<= " + dbl.sqlDate(date) + " and facctattr like '实收资本%' and FcyId = " + dbl.sqlString(rs.getString("FCuryCode"))
					+ " and (c.fconfirmer <> ' ' or c.fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) balance group by facctcode,fcurcode";
				
				dbl.executeSql(sql);
				
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'" + c + "9600',' ','" + "单位净值_多Class_" + rs.getString("FCuryName") + "','单位净值_多Class_" + rs.getString("FCuryName") + "','合计',0,0,0,0,0,0,0,0,0,0,0,round(FClassNetValue," + digit + ")"
					+ ",0,0,0,0,' ',0,0,' ' from "
					+ pub.yssGetTableName("tb_data_multiclassnet")
					+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and FType = '02' and Fcurycode = " + dbl.sqlString(rs.getString("FCuryCode"));
				
				dbl.executeSql(sql);
				
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'" + c + "9604',' ','" + "估值增值_多Class_" + rs.getString("FCuryName") + "','估值增值_多Class_" + rs.getString("FCuryName") + "','合计',0,0,0,0,0,0,0,0,0,0,0,"
					+ "round(FClassNetValue,2),0,0,0,0,' ',0,0,' ' from "
					+ pub.yssGetTableName("tb_data_multiclassnet")
					+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and FType = '03' and Fcurycode = " + dbl.sqlString(rs.getString("FCuryCode"));
				
				dbl.executeSql(sql);
				
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'" + c + "9603',' ','" + "汇兑损益_多Class_" + rs.getString("FCuryName") + "','汇兑损益_多Class_" + rs.getString("FCuryName") + "','合计',0,0,0,0,0,0,0,0,0,0,0,"
					+ "round(FClassNetValue,2),0,0,0,0,' ',0,0,' ' from "
					+ pub.yssGetTableName("tb_data_multiclassnet")
					+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and FType = '04' and Fcurycode = " + dbl.sqlString(rs.getString("FCuryCode"));
				
				dbl.executeSql(sql);
				
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'" + c + "9612',' ','" + "累计单位净值_多Class_" + rs.getString("FCuryName") + "','累计单位净值_多Class_" + rs.getString("FCuryName") + "','合计',0,0,0,0,0,0,0,0,0,0,0,"
					+ "round(FClassNetValue," + digit + "),0,0,0,0,' ',0,0,' ' from "
					+ pub.yssGetTableName("tb_data_multiclassnet")
					+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and FType = '08' and Fcurycode = " + dbl.sqlString(rs.getString("FCuryCode"));
				
				dbl.executeSql(sql);
				
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'" + c + "9991',' ','" + "日净值增长率_多Class_" + rs.getString("FCuryName") + "','日净值增长率_多Class_" + rs.getString("FCuryName") + "','合计',0,0,0,0,0,0,0,0,0,0,0,"
					+ "round(FClassNetValue," + digit + "),0,0,0,0,' ',0,0,' ' from "
					+ pub.yssGetTableName("tb_data_multiclassnet")
					+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and FType = '09' and Fcurycode = " + dbl.sqlString(rs.getString("FCuryCode"));
				
				dbl.executeSql(sql);
				
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'" + c + "9992',' ','" + "累计净值增长率_多Class_" + rs.getString("FCuryName") + "','累计净值增长率_多Class_" + rs.getString("FCuryName") + "','合计',0,0,0,0,0,0,0,0,0,0,0,"
					+ "round(FClassNetValue," + digit + "),0,0,0,0,' ',0,0,' ' from "
					+ pub.yssGetTableName("tb_data_multiclassnet")
					+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and FType = '10' and Fcurycode = " + dbl.sqlString(rs.getString("FCuryCode"));
				
				dbl.executeSql(sql);
				
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'" + c + "9993',' ','" + "本期基金份额净值增长率_多Class_" + rs.getString("FCuryName") + "','本期基金份额净值增长率_多Class_" + rs.getString("FCuryName") + "','合计',0,0,0,0,0,0,0,0,0,0,0,"
					+ "round(FClassNetValue," + digit + "),0,0,0,0,' ',0,0,' ' from "
					+ pub.yssGetTableName("tb_data_multiclassnet")
					+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and FType = '11' and Fcurycode = " + dbl.sqlString(rs.getString("FCuryCode"));
				
				dbl.executeSql(sql);
				
				c = (char)(c + 1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//add by fangjiang 2012.05.16 story 2565
	private void dealMClassByRate(ResultSet rs,int fSetCode,Date date,int digit,int intMonth , int flag){ //modify huangqirong 2013-02-05 Story #3433 , bug #6975
		
		try{
			
			String sql="";
			//---按照组合分级的显示项生成 story 2727 add by zhouwei 20120620 start---//
			boolean inShowItem=false;//是否按照分级的显示项来生成
			HashMap vocMap=new HashMap();//分级组合显示项词汇
			//---按照组合分级的显示项生成 story 2727 add by zhouwei 20120620 end---//
			dbl.loadConnection().setAutoCommit(false);
			//--- story 2727 add by zhouwei 20120620 start---//
			//如果所有组合分级的显示项都没有设置，则认为采用之前易方达ETF的计算方法，否则就根据显示项来进行计算 story 2727 by zhouwei 20120620
    		sql="select  FPORTCLSCODE from "+pub.yssGetTableName("tb_ta_portcls")+" where fshowitem is not null"
    		   +" and fcheckstate=1 and Fportcode = " + dbl.sqlString(sPortCode);
    		rs=dbl.openResultSet(sql);
    		if(rs.next()){
    			inShowItem=true;
    		}
    		dbl.closeResultSetFinal(rs);
    		sql = "select * from Tb_Fun_Vocabulary " +
             " where FVocTypeCode = " + dbl.sqlString(YssCons.YSS_TA_PORTCLS_SHOWITEM);
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			vocMap.put(rs.getString("FVOCCODE"), rs.getString("FVOCNAME"));
    		}
    		dbl.closeResultSetFinal(rs);
    		//--- story 2727 add by zhouwei 20120620 end---//
			char c = 'A';
			// story 2727 edit by zhouwei 2012.06.20
			sql = " select a.FPortClsCode,a.FCURYCODE,cry.FCuryName, b.Fportclsname,b.fshowitem,b.foffset from " + pub.yssGetTableName("TB_Ta_Classfunddegree")
				  + " a join " + pub.yssGetTableName("Tb_Ta_Portcls") + " b on a.FPortClsCode = b.FPortClsCode " 
				  //edit by songjie 2012.07.18 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
				  + " left join " + pub.yssGetTableName("Tb_Para_Currency") + " cry on cry.FCuryCode = a.FCuryCode "
				  + " where a.FCheckState = 1 and a.Fportcode = " + dbl.sqlString(sPortCode)
				  + " and  a.FPORTCLSCODE<>' ' and a.FCURYCODE<>' '"//组合分级与币种都不为空
				  //story4151 modified by  yeshenghong 20130813  ---start
				  + " and b.FCheckState = 1 and (b.fportclsschema = 'abLimited' or b.fportclsschema = 'inExRate') "; 
			      //story4151 modified by  yeshenghong 20130813  ---end
			rs = dbl.queryByPreparedStatement(sql); 				
			while (rs.next()) {
				if(!inShowItem){
					sql = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ ",'" + c + "9600',' ','" + "单位净值_" + rs.getString("Fportclsname") + "','单位净值_" + rs.getString("Fportclsname") + "','合计',0,0,0,0,0,0,0,0,0,0,0,FClassNetValue" 
						+ ",0,0,0,0,' ',0,0,' ' from "
						+ pub.yssGetTableName("tb_data_multiclassnet")
						+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and FType = '02' and Fcurycode = " + dbl.sqlString(rs.getString("FPortClsCode"));
					
					dbl.executeSql(sql);
					
					sql = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ " select '"
						+ fSetCode
						+ "',"
						+ dbl.sqlDate(date)
						+ ",'" + c + "9612',' ','" + "累计单位净值_" + rs.getString("Fportclsname") + "','累计单位净值_" + rs.getString("Fportclsname") + "','合计',0,0,0,0,0,0,0,0,0,0,0,"
						+ "FClassNetValue,0,0,0,0,' ',0,0,' ' from "
						+ pub.yssGetTableName("tb_data_multiclassnet")
						+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and FType = '08' and Fcurycode = " + dbl.sqlString(rs.getString("FPortClsCode"));
					
					dbl.executeSql(sql);
					c = (char)(c + 1);
				}else{//按照组合分级的显示项生成 story 2727 add by zhouwei 20120620
					c=insertDataByClsShowItems(rs, fSetCode, date, c, vocMap , flag); //modify huangqirong 2013-02-05 Story #3433 , bug #6975
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/*
	 * story 2727 add by zhouwei 20120620 
	 * 按照组合分级的显示项来生成财务估值表
	 * */
	/*private char insertDataByClsShowItems(ResultSet rs,int fSetCode,Date date,
				char c,HashMap vocMap) throws YssException{
		String sql="";
		try{
			String[] showItems=(rs.getString("fshowitem")==null?"":rs.getString("fshowitem")).split(",");
			for(int i=0;i<showItems.length;i++){
				String typeCode=showItems[i];
				String showName=(String) vocMap.get(typeCode);
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'" + c + "9600"+"',' ','" + showName+"_" + rs.getString("Fportclsname") + "','"+showName+"_"+ rs.getString("Fportclsname") + "','合计',0,0,0,0,0,0,0,0,0,0,0,FClassNetValue" 
					+ ",0,0,0,0,' ',0,0,' ' from "
					+ pub.yssGetTableName("tb_data_multiclassnet")
					+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and FType = "+dbl.sqlString(typeCode)+" and Fcurycode = " + dbl.sqlString(rs.getString("FPortClsCode"));			
				dbl.executeSql(sql);
				c = (char)(c + 1);
			}	
			return c;
		}catch (Exception e) {
			throw new YssException("按照组合分级显示项生成财务数据出错！");
		}
	}*/
	
	private char insertDataByClsShowItems(ResultSet rs,int fSetCode,Date date,
			char c,HashMap vocMap, int flag) throws YssException{ //modify huangqirong 2013-02-05 Story #3433 , bug #6975
		String sql="";
		String strSql = "";
		ResultSet rs1 = null;
		double navValue = 0;
		double unitValue = 0;
		double baseRate = 0;
		double portRate = 0;
		double dMarketValue = 0;
		//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
		double navValueOfStandard = 0;//总的本位币资产净值
		double totalTaAmount = 0;//总的实收资本，即 总的TA库存数量
		//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
		try{
			//基础汇率
			baseRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  rs.getString("FCuryCode"), 
		  				  this.sPortCode, 
		  				  YssOperCons.YSS_RATE_BASE);
		   	//组合汇率
		   	portRate = this.getSettingOper().getCuryRate(
		  				  date, 
		  				  "", 
		  				  sPortCode, 
		  				  YssOperCons.YSS_RATE_PORT); 
		   	
		   	CtlPubPara pubPara = new CtlPubPara();
		   	pubPara.setYssPub(this.pub);
		   	String digits = pubPara.getDigitsPortMethod("PubParaUnitCls","dayfinish","CtlPubParaUnitCls","portClsSel","txtdigit",rs.getString("FPortClsCode"),"3");			   	
		   	int digt = YssFun.toInt(digits);
		   	
//			strSql = " select FStandardMoneyCost from " + pub.yssGetTableName(sGVTableName) + 
//			" where FDate = " + dbl.sqlDate(date) + " and FPortCode = " + fSetCode + 
//			" and FAcctCode in ('9000') ";
//			rs1 = dbl.openResultSet(strSql);
//			while(rs1.next()){
//				//财务估值表中的分级组合原币资产净值
//				navValue = rs1.getDouble("FStandardMoneyCost");				
//				navValue = YssD.div
//			  	             (
//			  	                YssD.mul
//			  	                (
//			  	                	  navValue, 
//				            		  portRate
//			  	                ),
//			       		      	baseRate
//					         );	
//				navValue = YssD.round(navValue, 2);		
//			}
//			
//			dbl.closeResultSetFinal(rs1);
//			rs1 = null;
			
			strSql = " select FStandardMoneyMarketValue from " + pub.yssGetTableName(sGVTableName) + 
			" where FDate = " + dbl.sqlDate(date) + " and FPortCode = " + fSetCode + 
			" and FAcctCode in ('9000') and FCurCode = ' '";
			
			rs1 = dbl.queryByPreparedStatement(strSql);
			
			while(rs1.next())
			{
				//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
				navValueOfStandard = rs1.getDouble("FStandardMoneyMarketValue");//总的本位币资产净值
				//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
				navValue = rs1.getDouble("FStandardMoneyMarketValue");
				navValue = YssD.div
 	             (
 	                YssD.mul
 	                (
 	                		navValue, 
	            		portRate
 	                ),
      		      	baseRate
		         );	
				navValue = YssD.round(navValue, 2);		
			}
			
			dbl.closeResultSetFinal(rs1);
			rs1 = null;
			
			strSql = " select FStandardMoneyMarketValue from " + pub.yssGetTableName(sGVTableName) + 
			" where FDate = " + dbl.sqlDate(date) + " and FPortCode = " + fSetCode + 
			" and FAcctCode in ('9600')";
			rs1 = dbl.openResultSet(strSql);
			while(rs1.next()){
				//财务估值表的分级组合原币单位净值
				unitValue = rs1.getDouble("FStandardMoneyMarketValue");
				unitValue =  YssD.div
	 	             		(
			  	                YssD.mul
			  	                (
			  	                	  unitValue, 
				            		  portRate
			  	                ),
			       		      	baseRate
					         );
				unitValue = YssD.round(unitValue, digt);
				
			}
			
			//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
			CtlNavRep navRep = new CtlNavRep();
			navRep.setYssPub(this.pub);
			navRep.setPortCode(this.sPortCode);		
			
			totalTaAmount = getTotalTAAmount(date, fSetCode);//总的实收资本 ，即 总的TA库存数量
			//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
			
			String[] showItems=(rs.getString("fshowitem")==null?"":rs.getString("fshowitem")).split(",");
			java.util.Arrays.sort(showItems);	//added by liubo.Story #2719.利用数组帮助类自动排序
			for(int i=0;i<showItems.length;i++){
				String typeCode=showItems[i];
				String showName=(String) vocMap.get(typeCode);
				double value = 0 ;
				
				if(typeCode.equals("23")){		//显示项包含每十份单位净值
					value = YssD.mul(unitValue,10);
				}else if(typeCode.equals("05")){ 	//实收资本 
					//--- delete by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
//					CtlNavRep navRep = new CtlNavRep();
//					navRep.setYssPub(this.pub);
//					navRep.setPortCode(this.sPortCode);
					//--- delete by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
					navRep.setFlag(flag); //modify by huangqirong 2013-01-29 Story #3433 ,bug #6975					
					//value = navRep.getTaStock(rs.getString("FCuryCode") , date , 0); //modify by huangqirong 2013-01-29 Story #3433 ,bug #6975 设置组合分级
					value = navRep.getTaStock(rs.getString("FPortClsCode") , date , 0);//add by huangqirong 2013-01-29 Story #3433 ,bug #6975 设置组合分级
				}
				else if(typeCode.equals("06"))
				{
					//--- delete by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
//					CtlNavRep navRep = new CtlNavRep();
//					navRep.setYssPub(this.pub);
//					navRep.setPortCode(this.sPortCode);
					//--- delete by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
					navRep.setFlag(flag); //add by huangqirong 2013-01-29  Story #3433 ,bug #6975
					if (rs.getString("FOFFSET").trim().equals("0"))
					{
						value = YssD.div(YssD.mul(navRep.getTotalValue(date), navRep.getTaStock(rs.getString("FPortClsCode") , date , 0)), navRep.getTAStockAmount(date,this.sPortCode)); //modify by huangqirong 2013-01-29 story #3433 , bug #6975
					}				    
					else
				    {
						value = YssD.sub(navRep.getTotalValue(date), navRep.getNetValueOffset(date,this.sPortCode));
				    }
				}
				else if(typeCode.equals("07"))
				{
					//--- delete by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
//					CtlNavRep navRep = new CtlNavRep();
//					navRep.setYssPub(this.pub);
//					navRep.setPortCode(this.sPortCode);
					//--- delete by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
					
					value = YssD.round(YssD.div(navValue, navRep.getTAStockAmount(date,this.sPortCode)),digt); //modify huangqirong 2013-01-10 STORY #3433 多币种财务估值表对美元单位净值保留4位小数位
					
				}
				else if(typeCode.equals("02")){	//组合分级单位原币净值
					value = unitValue ;
				}else if(typeCode.equals("22")){	//今日组合分级单位原币净值
					value = unitValue ;
				}else if(typeCode.equals("01")){	//组合分级资产原币净值
					value = navValue ;
				}else if(typeCode.equals("08")){	//组合分级累计原币单位净值
					//--- delete by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
//					CtlNavRep navRep = new CtlNavRep();
//					navRep.setYssPub(this.pub);
//					navRep.setPortCode(this.sPortCode);
					//--- delete by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
					value = YssD.add
		                    (
		                    		unitValue, 
		                    		navRep.getSumDivided(date,rs.getString("FPortClsCode"))
		           		 	);
					value = YssD.round(value, digt);
				}
				//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
				else if(typeCode.equals("38") || typeCode.equals("39")){
					navRep.setDDate(date);
					double clsTaAmount = navRep.getTaStock(rs.getString("FCuryCode") , date , 0);//分级组合TA库存数量
					//非钆差分级组合资产净值_本位币之和
					double unSpecialSumNet = navRep.calUnSpecialSumNet(navValueOfStandard,totalTaAmount);
					if (rs.getString("FOFFSET").trim().equals("0")){//若为非钆差分级组合，则
						//非钆差分级组合的资产净值_原币 
						//= 基金总资产净值_本位币 * （非钆差分级组合的的份额数量 / 总的份额数量）* 组合汇率 / 基础汇率；
						value = YssD.div(
								         YssD.mul(
								        		  YssD.mul(navValueOfStandard,
								                           YssD.div(clsTaAmount, 
								        		                    totalTaAmount)
								        		           ),
		                                          portRate),
		                                 baseRate);
								         
					}else{//若为钆差分级组合，则
						//分级组合资产净值_原币（轧差） 
				    	// =（基金总资产净值_本位币 – ∑（其他非钆差分级组合的份额总净值_本位币））* 组合汇率 / 基础汇率；
						//其他非钆差分级组合的份额总净值_本位币
				    	// = ∑（基金总资产净值_本位币 *（各分级的份额数量 / 总的份额数量））；
						value = YssD.div(
								         YssD.mul(
										          YssD.sub(navValueOfStandard, 
										        		   unSpecialSumNet
												           ),
                                                  portRate),
                                         baseRate);
					}
					if(typeCode.equals("38")){
						value = YssD.round(value, digt);
					}
					if(typeCode.equals("39")){
						//各分级单位净值_原币 = 各分级资产净值_原币 / 各分级的份额数量；
						value = YssD.div(value, clsTaAmount);
						value = YssD.round(value, digt);
					}
				}
				//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
				
				sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " select '"
					+ fSetCode
					+ "',"
					+ dbl.sqlDate(date)
					//edit by songjie 2012.07.19 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
					//--- edit by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
					//换行
					+ ",'" + c + "9700"+"',' ','" + showName+ "_" + rs.getString("FPortClsCode") + "_" 
					+ rs.getString("FCuryName") + "','"+showName + "_" + rs.getString("FPortClsCode") + "_" 
					+ rs.getString("FCuryName") + "','合计',0,0,0,0,0,0,0,0,0,0,0,"+ value //modify by huangqirong 2013.02.05 Story #3433 , bug #6975 9600 --> 9700 分级 名称显示调整
					+ ",0,0,0,0,' ',0,0,' ' from "
					+ pub.yssGetTableName("tb_data_multiclassnet")
					+ " where fnavdate = " + dbl.sqlDate(date) + " and FPortCode = " + dbl.sqlString(this.sPortCode) + 
					//--- edit by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
					" and FType = "+dbl.sqlString(typeCode)+" and Fcurycode = " + dbl.sqlString(rs.getString("FPortClsCode"));			
				dbl.executeSql(sql);
				c = (char)(c + 1);
			}	
			return c;
		}catch (Exception e) {
			throw new YssException("按照组合分级显示项生成财务数据出错！");
		}
	}
	
	/**
	 * add by songjie 2013.05.31 
	 * STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001
	 * 获取财务估值表实收资本
	 * @return
	 * @throws YssException
	 */
	private double getTotalTAAmount(Date date,int fSetCode) throws YssException{
		double totalTAAmount = 0;
		ResultSet rs = null;
		String strSql = "";
		try{
			strSql = " select FAmount from " + pub.yssGetTableName(sGVTableName) + 
			" where FDate = " + dbl.sqlDate(date) + " and FPortCode = " + fSetCode + 
			" and FAcctCode in ('8700')";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				totalTAAmount = rs.getDouble("FAmount");
			}
			
			return totalTAAmount;
		}catch(Exception e){
			throw new YssException("获取财务估值表实收资本出错",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**shashijie 2012-3-13 STORY 2335 获取期货SQL */
	private String getQHPathSQL(boolean bShowGz,String showDec,int intMonth,Date date) {
        
		String query = 
			" Left Join (Select Fcode, Sum(Fendbal) As gzFendbal, Sum(Fbendbal) As gzFbendbal "+
			" From (Select Ba.Facctcode, Fendbal, Fbendbal, Case When Instr(Ba.Fauxiacc, '|') > 0 Then "+
			" Substr(Ba.Fauxiacc, 3, Instr(Ba.Fauxiacc, '|') - 3) Else Substr(Ba.Fauxiacc, 3, Length(Ba.Fauxiacc)) "+
            " End As Fcode, Case When Instr(Ba.Fauxiacc, '|') > 0 Then "+
            " Substr(Ba.Fauxiacc, 0, Instr(Ba.Fauxiacc, '|') - 1) Else Ba.Fauxiacc End As Fauxiacc, "+
            " Baac.Facctattr, Baac.Facctname, Baac.Facctclass From "+YssTabPrefix+"lbalance Ba "+
            " Inner Join "+YssTabPrefix+"laccount Baac On Ba.Facctcode = Baac.Facctcode Where Fmonth = "+intMonth+
            " And Baac.Facctdetail = 1  And (Baac.Facctclass = '资产类' Or Baac.Facctclass = '负债类' Or " +
            " Baac.Facctclass = '共同类')"+
            (bShowGz ? "" : " And (Baac.Facctattr Not Like '投资估值增值%' or  Baac.Facctattr like '%可退替代款%')")+
            (showDec.equalsIgnoreCase("0")? "": " and Baac.facctattr not like '减值准备%'")+//bug3252 yeshenghong
            (showDec.equalsIgnoreCase("0")? "":" and Baac.facctcode not like '150311%'")+
            " Union All "+
            " Select Cw.Fkmh As Facctcode, Case When Fjd = 'J' Then Fbal Else Fbal * -1 End As Fendbal," +
            " Case When Fjd = 'J' Then Fbbal Else Fbbal * -1 End As Fbendbal," +
            " Case When Instr(Cw.Fauxiacc, '|') > 0 Then Substr(Cw.Fauxiacc, 3, Instr(Cw.Fauxiacc, '|') - 3)" +
            " Else Substr(Cw.Fauxiacc, 3, Length(Cw.Fauxiacc)) End As Fcode, " +
            " Case When Instr(Cw.Fauxiacc, '|') > 0 Then Substr(Cw.Fauxiacc, 0, Instr(Cw.Fauxiacc, '|') - 1)" +
            " Else Cw.Fauxiacc End As Fauxiacc, Cwac.Facctattr, Cwac.Facctname, Cwac.Facctclass" +
            " From (Select Fkmh, Fjd, Fauxiacc, Sum(Fbal) As Fbal, Sum(Fbbal) As Fbbal From "+YssTabPrefix+"fcwvch" +
    		" Where Fdate >= "+ dbl.sqlDate(StartDate)+" And Fdate <= "+dbl.sqlDate(date)+
    		" Group By Fkmh, Fjd, Fauxiacc) Cw" +
    		" Inner Join "+YssTabPrefix+"laccount Cwac On Cw.Fkmh = Cwac.Facctcode" +
    		" Where (Cwac.Facctclass = '资产类' Or Cwac.Facctclass = '负债类' Or Cwac.Facctclass = '共同类')" +
    		(bShowGz ? "" : " And (Cwac.Facctattr Not Like '投资估值增值%' or Cwac.Facctattr like '%可退替代款%') ")+
            (showDec.equalsIgnoreCase("0")? "": " and Cwac.facctattr not like '减值准备%'")+//bug3252 yeshenghong
            (showDec.equalsIgnoreCase("0")? "":" and Cwac.facctcode not like '150311%'")+
            " ) Bal"+
            " Left Join "+YssTabPrefix+"auxiaccset Aa On Bal.Fauxiacc = Aa.Auxiaccid "+
            " Where Instr(Facctattr, '公允价值_股指期货') > 0 Group By Facctcode, Fcode, Auxiaccname, Facctname," +
            " Facctattr, Facctclass, Remark ) gz On gz.fcode = Accbalance.fcode ";
		return query;
	}
	

	/**
	 * MS00774 华夏股指期货需求：同一个代码核算空头和多头 QDV4华夏2009年10月29日02_A
	 * 根据ISINCODE，汇总财务估值表中股指期货类型的当天该组合的记录
	 * 
	 * @param date
	 * @param sPortCode2
	 * @throws YssException
	 */
	private void summaryByCode(Date date, int fSetCode) throws YssException {
		ResultSet rs = null;
		Connection con = null;
		String sqlStr = "";
		boolean bTrans = false; // 代表是否开始了事务
		try {
			con = dbl.loadConnection();
			con.setAutoCommit(false);
			bTrans = true;
			sqlStr = "select facctattr from "
					+ pub.yssGetTableName("tb_rep_guessvalue")
					+ " a where instr(a.facctattr, '其他衍生工具_股指期货') > 0 "
					+ " and a.fdate =" + dbl.sqlDate(date)
					+ " and a.fportcode =" + fSetCode + " and rownum=1";// 先查询出股指期货类型的记录
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {// 只需要取一次。当有股指期货类型时，才进行以下操作

				// 1.查询出需要汇总的记录，写入临时表；
				// 2.从估值表中删除写入临时表的数据；
				// 3.将临时表的数据汇总，写入估值表
				sqlStr = "create table tmp_summary_"
						+ pub.getUserCode()
						+ " as ( select * from ( select a.*,"
						+ " b.fsecuritycode, b.fisincode as fisincode from "
						+ pub.yssGetTableName("tb_rep_guessvalue")
						+ " a left join (select fsecuritycode, fisincode from "
						+ pub.yssGetTableName("Tb_Para_Security")
						+ ") b on "
						+ " b.fsecuritycode =substr(a.facctcode,instr(a.facctcode,'_') + 1)"
						+ " where instr(a.facctattr, '其他衍生工具_股指期货') > 0 "
						+ " and a.fdate ="
						+ dbl.sqlDate(date)
						+ " and a.fportcode ="
						+ fSetCode
						+ " and b.fisincode is not null) where fisincode in ( select  d.fisincode as fisincode from "
						+ pub.yssGetTableName("tb_rep_guessvalue")
						+ " e left join (select fsecuritycode, fisincode"
						+ " from "
						+ pub.yssGetTableName("Tb_Para_Security")
						+ ") d on d.fsecuritycode =substr(e.facctcode,instr(e.facctcode,'_') + 1)"
						+ " where instr(e.facctattr, '其他衍生工具_股指期货') > 0 "
						+ " and e.fdate ="
						+ dbl.sqlDate(date)
						+ " and e.fportcode ="
						+ fSetCode
						+ " and d.fisincode is not null group by d.fisincode having count(d.fisincode)>1))";// 查出ISIN码重复的记录
				dbl.executeSql(sqlStr);// 将isin码重复的记录写入临时表
				sqlStr = "delete from "
						+ pub.yssGetTableName("tb_rep_guessvalue")
						+ " a where (FPortCode,FDate,facctcode,FCurCode) in "// 只需要根据主键来删除
						+ " (select FPortCode,FDate,facctcode,FCurCode from tmp_summary_"
						+ pub.getUserCode() + ")";
				dbl.executeSql(sqlStr);// 删除isin码重复的记录,因为后面要对这些记录进行汇总，重新插入
				sqlStr = "Insert into "
						+ pub.yssGetTableName("tb_rep_guessvalue")
						+ " select FPortCode,FDate,FAcctCode1 as FAcctCode,"
						+ "FCurCode,facctname,facctattr,"
						+ "facctclass,avg(FExchangeRate) as FExchangeRate,"
						+ "sum(FAmount) as FAmount,sum(FCost) as FCost,"
						+ "sum(FSTANDARDMONEYCOST) as FSTANDARDMONEYCOST,sum(FCostToNetRatio) as FCostToNetRatio,"
						+ "sum(FStandardMoneyCostToNetRatio) as FStandardMoneyCostToNetRatio,"
						+ "avg(FMARKETPRICE) as FMARKETPRICE,avg(FOTPrice1) as FOTPrice1,"
						+ "avg(FOTPrice2) as FOTPrice2,avg(FOTPrice3) as FOTPrice3,"
						+ "sum(FMarketValue) as FMarketValue,sum(FSTANDARDMONEYMARKETVALUE) as FSTANDARDMONEYMARKETVALUE,"
						+ "sum(FMarketValueToRatio) as FMarketValueToRatio,sum(FStandardMoneyMarketValueToRat) as FStandardMoneyMarketValueToRat,"
						+ "sum(FAPPRECIATION) as FAPPRECIATION,"
						+ "sum(FSTANDARDMONEYAPPRECIATION) as FSTANDARDMONEYAPPRECIATION,"
						+ "FMarketDescribe,FACCTLEVEL,FACCTDETAIL,Fdesc  from ( select "
						+ " tB.*,substr(tB.facctcode,0,instr(tB.facctcode,'_')) ||tB.fisincode as facctcode1 from "
						+ " tmp_summary_" + pub.getUserCode()
						+ " tB) tmpTb  group by tmpTb.facctcode1,FPortCode,"
						+ "FDate,FCurCode,facctname,"
						+ "facctattr,facctclass,FMarketDescribe,"
						+ "FACCTLEVEL,FACCTDETAIL,Fdesc";
				dbl.executeSql(sqlStr);// 将ISIN码重复的记录对应字段汇总，然后插入估值表
				sqlStr = "drop table tmp_summary_" + pub.getUserCode();
				dbl.executeSql(sqlStr);// 删除临时表

				con.commit();
				bTrans = false;
				con.setAutoCommit(true);
			}
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		} finally {
			try {
				if (dbl.yssTableExist("tmp_summary_" + pub.getUserCode())) {// 如果临时表存在，将其删除.每次执行完，一定要删除临时表
					sqlStr = "drop table tmp_summary_" + pub.getUserCode();
					dbl.executeSql(sqlStr);// 删除临时表
				}
			} catch (Exception e) {
				throw new YssException(e.getMessage());
			}
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(con, bTrans);
		}
	}

	/**
	 * 插入涨跌幅比例 代码为 9605 条件：如果主要交易市场中有一个交易市场当日有交易就要在报表中显示涨跌幅
	 * QDV4海富通2008年12月31日03_B MS00176 by leeyu 20090212
	 * 
	 * @param date
	 *            Date
	 * @param sPortCode
	 *            String
	 * @param setCode
	 *            String
	 * @throws YssException
	 */
	private void insertPercentChange(java.util.Date date, String sPortCode,
			int setCode) throws YssException {
		ResultSet rs = null;
		Connection con = null;
		String sqlStr = "";
		String sExchangeMarket = ""; // 市场
		String[] arrMarket = null; // 单个市场
		boolean bChange = false, bShow = false; // 是否显示
		java.util.Date dWorkDay = null; // 工作日
		Hashtable htChange = null; // 存放通用参数的方法
		CtlPubPara ctlPara = null;
		BaseOperDeal deal = null;

		try {
			con = dbl.loadConnection();
			deal = new BaseOperDeal();
			deal.setYssPub(pub);
			ctlPara = new CtlPubPara();
			ctlPara.setYssPub(pub);
			htChange = ctlPara.getPercentChange();
			if (htChange.get("Show") != null) {
				bShow = Boolean.valueOf((String) htChange.get("Show"))
						.booleanValue();
			}
			if (bShow) { // 当参数为要求显示时
				sExchangeMarket = (String) htChange.get("Market");
				if (sExchangeMarket == null) {
					return;
				}
				arrMarket = sExchangeMarket.split(","); // 先将市场代码放入到节假日变量中，在下面的方法中替换掉
				for (int i = 0; i < arrMarket.length; i++) {
					sqlStr = "select FHolidaysCode from tb_base_exchange where FExchangeCode="
							+ dbl.sqlString(arrMarket[i])
							+ " and FCheckState=1";
					rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
					if (rs.next()) {
						dWorkDay = deal.getWorkDay(rs
								.getString("FHolidaysCode"), date, 0);
					} else {
						dWorkDay = YssFun.addDay(new java.util.Date(), -1); // 如果交易所代码不正确
					}
					if (dWorkDay.compareTo(date) == 0) { // 如果两个日期相同就返回　说明交易市场中最少有一个当日有交易
						bChange = true;
						break;
					}
				} // end for;
			}
			if (bChange) { // 如果计算出需要显示的话，则就要插入一行新数据到财务估值表中
				sqlStr = "insert into "
						+ pub.yssGetTableName(sGVTableName)
						+ " values("
						+ setCode
						+ ","
						+ dbl.sqlDate(date)
						+ ","
						+ " 9605,' ',"
						+ " '当日单位净值涨跌幅比例','净值比例','合计',0,0,0,0,0,0,0,0,0,0,0,"
						+ // 0
						" (select round((a.FStandardMoneyMarketValue-b.FStandardMoneyMarketValue)/b.FStandardMoneyMarketValue*100,4) from ("
						+ " select FStandardMoneyMarketValue,FPortCode,FAcctCode from "
						+ pub.yssGetTableName(sGVTableName)
						+ " where FAcctCode ='9600' and FDate="
						+ dbl.sqlDate(date)
						+ " and FPortCode="
						+ setCode
						+ " )a "
						+ " left join (select FStandardMoneyMarketValue,FPortCode,FAcctCode from "
						+ pub.yssGetTableName(sGVTableName)
						+ " where FAcctCode ='9600' and FDate=(select max(FDate) as FDate from "
						+ pub.yssGetTableName(sGVTableName)
						+ " where FAcctCode ='9600' and FDate<"
						+ dbl.sqlDate(date)
						+ " and FPortCode="
						+ setCode
						+ ") and FPortCode="
						+ setCode
						+ ")b "
						+ " on a.FPortCode=b.FPortCode and a.FAcctCode=b.FAcctCode) "
						+ ",0,0,0,0,' ',0,0,' ')";
				con.setAutoCommit(false);
				dbl.executeSql(sqlStr);
				con.commit();
				con.setAutoCommit(true);
			}
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(con, false);
		}
	}

	// 插入投资经理的净值
	private void insertInvestNav(java.util.Date date, String sPortCode,
			int fSetCode) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		int iCount = 1;
		InvestManagerBean ivtBean = new InvestManagerBean();
		try {
			ivtBean.setYssPub(pub);
			strSql = "select * from " + pub.yssGetTableName("Tb_Data_NetValue")
					+ " where FPortCode = " + dbl.sqlString(sPortCode)
					+ " and FNavDate = " + dbl.sqlDate(date) +
					// " and FType = '01' and FInvMgrCode = ' '";
					// ----- MS00233 QDV4华夏2009年2月04日01_B sj modified
					// -------------//
					" and FType = '01' and FInvMgrCode != ' '"; // 获取实际的基金经理代码
			// ------------------------------------------------------------------//
			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				ivtBean.setStrCode(rs.getString("FInvMgrCode"));
				ivtBean.getSetting();
				strSql = "insert into " + pub.yssGetTableName(sGVTableName)
						+ " values(" + fSetCode + "," + dbl.sqlDate(date) + ","
						+ " 600" + String.valueOf(iCount) + ",' '," + " '"
						+ ivtBean.getStrName() + "净值','投资经理净值','合计',0,0,0,"
						+ rs.getDouble("FPortNetValue") + ",0,0,0,0,0,0,0,"
						+ rs.getDouble("FPortNetValue")
						+ ",0,0,0,0,' ',0,0,' ')";
				dbl.executeSql(strSql);
				iCount++;
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * add by songjie 2013.02.19 
	 * STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002
	 * 用于判断 用户是否有浏览当前组合群、组合对应的财务估值表的权限
	 * @return
	 * @throws YssException
	 */
	private boolean judgeRight(String setCode) throws YssException{
		ResultSet rs = null;
		ResultSet rs2 = null;
		YssFinance finance = new YssFinance(); 
		String sPortCode = "";
		String strSql = "";
		String strSql2 = "";
		boolean haveRight = false;
		
		/**Start 20130913 added by liubo.Bug #9462.QDV4南方2013年09月12日02_B
		 * 实例化一个RightBean，用来拼接权限继承的SQL语句*/
		RightBean right = new RightBean();
		right.setUserCode(pub.getUserCode());
		right.setYssPub(pub);
		/**End 20130913 added by liubo.Bug #9462.QDV4南方2013年09月12日02_B*/
		try{
			
			finance.setYssPub(pub); //设置公共信息
	        sPortCode = finance.getPortCode(setCode + ""); //调用方法得到组合代码
			strSql = " select * from Tb_sys_userright where FRightCode = 'GuessValue' and FOperTypes like '%brow%' " + 
			" and FRightInd = 'Right' and FrightType = 'port' " +
			
			/**Start 20130913 added by liubo.Bug #9462.QDV4南方2013年09月12日02_B
			 * 调用方法，拼接查询继承到的权限的Sql语句段*/
			" and (FUserCode = " + dbl.sqlString(pub.getUserCode()) +  
			right.getInheritedRights(pub.getAssetGroupCode(), "") + ")" +
			/**End 20130913 added by liubo.Bug #9462.QDV4南方2013年09月12日02_B*/
			
			" and FAssetGroupCode = " + dbl.sqlString(pub.getPrefixTB()) + " and FPortCode = " + dbl.sqlString(sPortCode) ;
			rs = dbl.openResultSet(strSql);
			if(rs.next()){
				haveRight = true;
			}
			
			dbl.closeResultSetFinal(rs);
			
			if(haveRight){
				return haveRight;
			}else{
				strSql = " select * from Tb_sys_UserRight where FRightInd = 'Role' and FrightType = 'port' and FAssetGroupCode = " + 
				dbl.sqlString(pub.getPrefixTB()) + " and FPortCode = " + dbl.sqlString(sPortCode) + " and " +

				/**Start 20130913 added by liubo.Bug #9462.QDV4南方2013年09月12日02_B
				 * 调用方法，拼接查询继承到的权限的Sql语句段*/
				" (FUserCode =  " + dbl.sqlString(pub.getUserCode()) + 
				right.getInheritedRights(pub.getAssetGroupCode(), "") + ")";
				/**End 20130913 added by liubo.Bug #9462.QDV4南方2013年09月12日02_B*/
				
				rs = dbl.openResultSet(strSql);
				while(rs.next()){
					strSql2 = " select * from Tb_Sys_RoleRight where FRightCode = 'GuessValue' and FRoleCode = " + 
					dbl.sqlString(rs.getString("FRightCode")) + " and FOperTypes like '%brow%' ";
					rs2 = dbl.openResultSet(strSql2);
					if(rs2.next()){
						haveRight = true;
					}
					
					dbl.closeResultSetFinal(rs2);
				}
				dbl.closeResultSetFinal(rs);
			}
			
			return haveRight;
		}catch(Exception e){
			throw new YssException("判断是否有浏览财务估值表的权限");
		}finally{
			dbl.closeResultSetFinal(rs,rs2);
		}
	}
	
	
	public String getParameter(String parameter) throws YssException {
		StringBuffer buffer = new StringBuffer();
		String sql = null;
		int setCode = 0;
		ResultSet rs = null, rstmp = null;
		java.util.Date date;
		String ErrorState = "";
		boolean DefaultShowLevel = false;//是否默认显示级别标示
		//---add by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 start---//
		boolean haveRight = false;//判断是否有浏览财务估值表的权限
		boolean findSet = false;//判断是否找到相关的套帐
		//---add by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 end---//
		try {
			if (!dbl.yssTableExist("lsetlist")) {
				throw new YssException("系统未找到相关的财务数据，请确认财务系统是否创建！");
			}
			
			sql = " select * from " + pub.yssGetTableName("Tb_Para_Portfolio")
				+ " where fcheckstate=1 order by fportcode ";
			
			rs = dbl.queryByPreparedStatement(sql);
			while (rs.next()) {
				/**Start---panjunfang modify 2013-9-2 Story 4127 */
				//循环到下个套账时重新赋值日期为界面所选日期，避免前一个套账将date调整后导致后续套账无法加载到界面。
				date = YssFun.toDate(parameter);
				/**End---panjunfang 2013-9-2 Story 4127*/

				//add by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002
				findSet = false;
				setCode = 0;
				sql = " select * from lsetlist where fsetid="
						+ dbl.sqlString(rs.getString("FAssetCode"))//modified by yeshenghong  去除套账链接设置  20130408
						+ " and fyear=" + YssFun.getYear(date)
						+ " order by FSetCode desc";
				rstmp = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
				//edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 if 改为 while
				while (rstmp.next()) {
					setCode = rstmp.getInt("FSetCode");
					
					//--- edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 start---//
					haveRight = judgeRight(setCode + "");
					if(!haveRight){
						setCode = 0;
						continue;
					}else{
						if (buffer.length() > 0) {
							buffer.append("\r\n");
						}
						
						buffer.append(YssFun.formatDate(date, "yyyy-MM-dd"))
						.append("\t").append(rstmp.getString("FSetName"))
						.append("\t").append(setCode).append("\t").append(
								rstmp.getString("FAccLen").length())
						.append("\t");
						DefaultShowLevel = true;
						
						findSet = true;
						break;
					}
					//--- edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 end---//
				}
				rstmp.getStatement().close();
				// 还原以下代码 modify by wangzuochun 2010.01.10 BUG #811
				// 打开财务估值表时提示“获取财务估值表出错”，
				// modify by zhangfa MS01630 生成财务估值表时，新建的套帐，无法选择日期
				// QDV4友邦华泰2010年08月12日01_B

				//edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 
				//buffer.length() == 0 改为 !findSet
				if (!findSet) {
					sql = "Select Max(FDate) As FDate From "
							+ pub.yssGetTableName("tb_rep_guessvalue");
					rstmp = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
					if (rstmp.next()) {
						if (rstmp.getDate("FDate") != null) {
							java.util.Date dateTemp = rstmp.getDate("FDate"); //------ modify by wangzuochun 2011.06.03 BUG 1967 组合群002有多组合的情况下，加载后，无法选择其他日期
							sql = "select * from lsetlist where fsetid="
								    + dbl.sqlString(rs.getString("FAssetCode"))//modified by yeshenghong  去除套账链接设置  20130408
									+ " and fyear=" + YssFun.getYear(dateTemp) //------ modify by wangzuochun 2011.06.03 BUG 1967 组合群002有多组合的情况下，加载后，无法选择其他日期
									+ " order by FSetCode desc";
							rstmp = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
							//edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002
							while (rstmp.next()) {
								setCode = rstmp.getInt("FSetCode");
								
								//--- edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 start---//
								haveRight = judgeRight(setCode + "");
								if(!haveRight){
									setCode = 0;
									continue;
								}else{
									if (buffer.length() > 0) {
										buffer.append("\r\n");
									}
									
									//---add by yeshenghong  20130410  财务估值报错 start---//
									String maxYear = rstmp.getString("FYEAR");
									if(maxYear.length() > 0 && YssFun.getYear(date) > Integer.parseInt(maxYear)){
										date = YssFun.toDate(maxYear + "-" + String.valueOf(YssFun.getMonth(date)) + "-" + String.valueOf(YssFun.getDay(date)));
									}
									
									//--- add by songjie 2013.05.10 BUG 7775 QDV4赢时胜(上海)2013年05月08日02_B start---//
									//如果获取的最大年份 > 已选财务估值表日期，则为无效数据
									if(Integer.parseInt(maxYear) > YssFun.getYear(date)){
										setCode = 0;
										continue;
									}
									//--- add by songjie 2013.05.10 BUG 7775 QDV4赢时胜(上海)2013年05月08日02_B end---//
									//---add  by yeshenghong  20130410  财务估值报错  end---//
									buffer.append(
											YssFun.formatDate(dateTemp, "yyyy-MM-dd")) //------ modify by wangzuochun 2011.06.03 BUG 1967 组合群002有多组合的情况下，加载后，无法选择其他日期
											.append("\t").append(
													rstmp.getString("FSetName"))
											.append("\t").append(setCode).append(
													"\t").append(
													rstmp.getString("FAccLen")
															.length()).append("\t");
									DefaultShowLevel = true;
									findSet = true;
									break;
								}
								//--- edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 end---//
							}
						}
					}
					rstmp.getStatement().close();
				}
				
				//edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002
				//buffer.length() == 0 改为 !findSet
				if (!findSet) { // 若依然没有获取到相关的套帐信息，则获取年份最大的套帐信息。MS00181
											// QDV4光大保德信2009年1月7日01_B sj
											// modified 20090122
					sql = "select * from lsetlist where fsetid="
							+ dbl.sqlString(rs.getString("FAssetCode"))//modified by yeshenghong  去除套账链接设置  20130408
							+ " and fyear="
							+ "(select MAX(FYEAR) as FYEAR from lsetlist where fsetid = " + dbl.sqlString(rs.getString("FAssetCode")) + " )" + // 获取年份最大的套帐信息  //modify huangqirong 2013-04-25 bug #7607
							" order by FSetCode desc";
					rstmp = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
					//edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)2013021700 
					while (rstmp.next()) {
						setCode = rstmp.getInt("FSetCode"); // 将最大年份的套帐信息获取
						//--- edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 start---//
						haveRight = judgeRight(setCode + "");
						if(!haveRight){
							setCode = 0;
							continue;
						}else{
							if (buffer.length() > 0) {
								buffer.append("\r\n");
							}
							
							//---add by songjie 2012.07.20 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A start---//
							String maxYear = rstmp.getString("FYEAR");
							if(maxYear.length() > 0 && YssFun.getYear(date) > Integer.parseInt(maxYear)){
								date = YssFun.toDate(maxYear + "-" + String.valueOf(YssFun.getMonth(date)) + "-" + String.valueOf(YssFun.getDay(date)));
							}
							//---add by songjie 2012.07.20 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A end---//
							
							//--- add by songjie 2013.05.10 BUG 7775 QDV4赢时胜(上海)2013年05月08日02_B start---//
							//如果获取的最大年份 > 已选财务估值表日期，则为无效数据
							if(Integer.parseInt(maxYear) > YssFun.getYear(date)){
								setCode = 0;
								continue;
							}
							//--- add by songjie 2013.05.10 BUG 7775 QDV4赢时胜(上海)2013年05月08日02_B end---//
							
							buffer.append(YssFun.formatDate(date, "yyyy-MM-dd"))
									.append("\t").append(
											rstmp.getString("FSetName")).append(
											"\t").append(setCode).append("\t")
									.append(rstmp.getString("FAccLen").length())
									.append("\t");
							DefaultShowLevel = true;
							findSet = true;
							break;
						}
						//--- edit by songjie 2013.02.19 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 end---//
					} // 将获取的套帐信息拼装，传递到前台。
					rstmp.getStatement().close();
				}

				// -------------------------------------------------------------------------------------------------------------------------------------
				if (setCode > 0) {
					ErrorState = setAccountingPeriod(date, setCode, true);// edited
																			// by
																			// zhouxiang
																			// MS01339
																			// 财务估值表显示的日期，与日期控件显示的日期不一致
					sql = "Select * from " + YssTabPrefix
							+ "lcurrency order by fisbase desc";
					rstmp = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
					while (rstmp.next()) {
						if (rstmp.getRow() == 1) {
							buffer.append("所有币种").append("\f").append("***");
						}
						buffer.append("@").append(rstmp.getString("FCurName"))
								.append("\f").append(
										rstmp.getString("FCurCode"));
					}
					rstmp.getStatement().close();
				}
				
				/**获取套帐标示和套帐名称,然后拼接字符串作为查询通用业务参数条件
				 *获取通用参数中的显示级别DefaultShowLevel字段对应的值,最后根据这个字段的值显示级别
				 * 2011.1.31 shashijie STORY #356 财务估值表科目显示级别可通过参数的方式设置默认级别*/
				if (DefaultShowLevel) {
					String[] buffers = null;
					
					//20110106 added by liubo.Bug 3366
					//此if中的ResultSet对象，全部都使用的重新实例化后的rstmp。在关闭ResultSet时，无论实例化了多少个rstmp，都只会关闭一个。如此会造成超过游标最大数的Exception
					//====================================
					ResultSet rsTmp1 = null;		
					ResultSet rsTmp2 = null;	
					//================end====================
					
					if (buffer.toString().indexOf("\r\n")>0) {//如果有多出换行则始终取最后一行的数据
						buffers = (buffer.toString().split("\r\n")[buffer.toString().split("\r\n").length - 1]).split("\t");
					} else {
						buffers = buffer.toString().split("\t");
					}
					
					sql = "SELECT FSetId,FSetName FROM lsetlist WHERE fyear="+YssFun.getYear(YssFun.toDate(buffers[0]))
						+" AND fsetcode="+dbl.sqlString(buffers[2])
						+" AND fsetname="+dbl.sqlString(buffers[1])
						+" AND length(TRIM(facclen))=4";
					rstmp = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
					if (rstmp.next()) {// 获取通用参数列表,因为可以重复添加所以这里用fparaid参数值编号(参数值编号)排序以后设置的为主
						//edit by songjie 2011.05.30 BUG 1994 QDV4建行2011年05月26日01_B
						sql = "SELECT FParaId FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") + " WHERE fctlvalue = " 
							+ dbl.sqlString(rstmp.getString("FSetId")+"|"+rstmp.getString("FSetName"))+" ORDER BY fparaid DESC"; 
						
						//20110106 modified by liubo.Bug 3366
						//=================================
//						rstmp = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
						rsTmp1 = dbl.queryByPreparedStatement(sql); 
						//===============end================
						
						//20110106 modified by liubo.Bug 3366
						//=================================
//						if (rstmp.next()) {
						if (rsTmp1.next()) {
						//==============end================
							//edit by songjie 2011.05.30 BUG 1994 QDV4建行2011年05月26日01_B
							sql = "SELECT FCtlValue FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") + " WHERE fpubparacode='finish_ParaFVT' " +
									"AND fparagroupcode='dayfinish' " +
									"AND fctlgrpcode='ctlGrpFVT' " +
									"AND fctlcode='DefaultShowLevel' " +
									
									//20110106 modified by liubo.Bug 3366
									//=================================
//									"AND fparaid="+dbl.sqlString(rstmp.getString("FParaId"));
									"AND fparaid="+dbl.sqlString(rsTmp1.getString("FParaId"));
									//==============end=================
							
							
							//20110106 modified by liubo.Bug 3366
							//=================================
//							rstmp = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
							rsTmp2 = dbl.queryByPreparedStatement(sql);
							//=============end====================
//							if (rstmp.next()) {
							if (rsTmp2.next()) {
								//20110106 modified by liubo.Bug 3366
								//=================================
//								String[] fctvalue = rstmp.getString("FCtlValue").split(",");
								String[] fctvalue = rsTmp2.getString("FCtlValue").split(",");
								//===============end=====================
								buffer.append("\t").append(fctvalue[0]).append("\t");// 将获取的默认级别值到拼到最后一个参数，传递到前台。
							}
							dbl.closeResultSetFinal(rsTmp2);	//20110106 added by liubo.Bug 3366
						}
						dbl.closeResultSetFinal(rsTmp1);		//20110106 added by liubo.Bug 3366
					} 
					DefaultShowLevel = false;
					rstmp.getStatement().close();
				}
				
			}
			
			dbl.closeResultSetFinal(rs);
			
			//--- add by songjie 2013.02.21 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 start---//
			if(buffer.length() == 0){
				haveRight = false;
				sql = "select l.fsetcode from " + pub.yssGetTableName("Tb_Para_Portfolio") 
				+ " p join lsetlist l on p.fassetcode = l.fsetid where p.fcheckstate=1 order by fportcode ";
		
				rs = dbl.openResultSet(sql);
				while (rs.next()) {
					setCode = rs.getInt("fsetcode");//modified by yeshenghong  取消套账链接设置  20130408
					haveRight = judgeRight(setCode + "");
					if(haveRight){
						break;
					}
				}
				
				if(!haveRight){
					throw new YssException("请设置用户【" + pub.getUserCode() + "】浏览财务估值表的权限！");
				}
			}
			//--- add by songjie 2013.02.21 STORY #3639 需求深圳-(招商银行)QDII估值系统V4.0(高)20130217002 end---//
			
		} catch (SQLException sqle) {
			throw new YssException("获取估值表参数数据出错！\r\n" + sqle.getMessage());
		} catch (Exception e) {
			throw new YssException("获取估值表参数数据出错！\r\n" + e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.getStatement().close();
				}
			} catch (Exception e) {
			}
			try {
				if (rstmp != null) {
					rstmp.getStatement().close();
				}
			} catch (Exception e) {
			}
		}
		buffer.append("\f<").append(ErrorState);
		return buffer.toString();
	}

	public String getGuessValueReport(String parameter) throws YssException {
		StringBuffer buffer = new StringBuffer();
		String strArray[];
		String sql = null;
		ResultSet rs = null;
		String tmp = "";
		String sType = "0";
		boolean isJSPortCode=false;		//add by 黄啟荣   2011-06-07 STORY #1103
		String formatStr = null; // 用来存放持仓位数保留小数位的格式化字符串 sunkey 20090112
									// BugNO:MS00090
		boolean doLoad = false; // 用来判断是否显示本期基金净值增长率 BugNo:0000478 edit by jc
		boolean bShowYSX = false; // 显示财务估值表中已实现部分的数据 QDV4海富通2009年05月11日01_AB					
			// MS00439 by leeyu 20090515
		CtlPubPara ctlPub = new CtlPubPara(); // 显示财务估值表中已实现部分的数据
												// QDV4海富通2009年05月11日01_AB
												// MS00439 by leeyu 20090515
		//------add by baopingping  20110608  STORY #1041
		ctlPub.setYssPub(pub);
		String portSel="portSel";
		String cboUnitValue="cboUnitValue";//是否显示单位净值（人民币） add by baopingping  201106010  STORY #1041
		String CboAssetValue="CboAssetValue";//是否显示资产净值（人民币）add by baopingping  20110608  STORY #1041
		String CboUsable="CboUsable";//用来判断是否显示可用头寸  add by baopingping  20110608  STORY #1041
		String CboPaidIn="CboPaidIn";//是否显示实收资本 add by baopingping  20110608  STORY #1041
		String CboUAssetValue="CboUAssetValue";//是否显示资产净值（美元）add by baopingping  20110608  STORY #1041
		String cboUUnitlValue="cboUUnitlValue";//是否显示单位净值（美元） add by baopingping  20110608  STORY #1041
		String CboExchang="CboExchang";//是否显示汇兑损益  add by baopingping  20110608  STORY #1041
	    String CboAddValue="CboAddValue";//是否显示估值增值     add by baopingping  20110608  STORY #1041
	    String CboAddUnitValue="CboAddUnitValue";//“是否显示累计单位净值        add by baopingping  20110608  STORY #1041
	    String CboAddupAsset = "CboAddupAsset";//是否显示日净值增长率          add by baopingping  20110608  STORY #1041
	    String CboAddLot = "CboAddLot";//是否显示本期基金份额净值增长率        add by baopingping  20110608  STORY #1041
	    String CboAddUpValue="CboAddUpValue";	//是否显示。add by baopingping  20110608  STORY #1041
	    //----end----------------
	    //add by yeshenghong 20111108 STORY#1562 不显示减值准备
	    String CboShowDec = "CboShowDec";
	    String showDec = "0"; // 0为显示 1 不显示,默认为0
	    // end  to control 减值准备
	    List classList=new ArrayList();//各个组合分级的显示汇总
	    double totalNetValue=0;//总资产净值
		try {
			strArray = parameter.split("\t", -1);
			String portCodeId=this.getPortCode(strArray[1],  YssFun
					.left(strArray[0], 4));//add by baopingping  20110608  STORY #1041
			
			/**shashijie 2012-11-5 BUG 6129 解决改BUG的第二个问题时发现,这里的组合代码需付给成员变量
			 * 否则下面处理组合分级显示的时候系统会没有组合代码*/
			this.sPortCode = portCodeId;
			/**end shashijie 2012-11-5 BUG */
			
			//add by 黄啟荣   2011-06-07 STORY #1103
			if(strArray.length>4&&strArray[4].equalsIgnoreCase("true")){
				isJSPortCode=true;			
			}else if(strArray.length>=4&&strArray[3].equalsIgnoreCase("true")){
				isJSPortCode=true;			
			}
			//----------end---------
			setAccountingPeriod(YssFun.toDate(strArray[0]), YssFun
					.toInt(strArray[1]));
			if(YssTabPrefix != null && YssTabPrefix.trim().length() == 0)//add huangqirong 2013-04-25 bug #7607
				return "";  //add huangqirong 2013-04-25 bug #7607
			// ------------持仓数量小数位的修改 sunkey 20081224 BungNO:MS00090
			// 通过方法获取格式化字符串,传入的sql语句用来将套帐编号转换成获取通用参数所需的套帐ID
			formatStr = this.getFormatNumStr(
					"select a.fsetID from lsetlist a inner join  "
							+ pub.yssGetTableName("Tb_Para_Portfolio")
							+ " b on a.fsetid=b.fassetcode where fsetcode="
							+ strArray[1] + " and fyear=" + AccountingYear
							+ " order by FSetCode desc", "2");
			//------add by baopingping  20110610  STORY #1041
			String Result = "8810,8811,8812";
			//不查出可用头寸
			if(ctlPub.getvalue(portSel,CboUsable,portCodeId).equalsIgnoreCase("1")){
				Result += ",8600";
			}
			//不查出实收资本
			if(ctlPub.getvalue(portSel,CboPaidIn,portCodeId).equalsIgnoreCase("1")){
				Result += ",8700";
			}
			//不查出资产净值（美元）
			if(ctlPub.getvalue(portSel, CboUAssetValue,portCodeId).equalsIgnoreCase("1")){
				
				Result+=",9601";
			}
			//不查出单位净值（美元）
			if(ctlPub.getvalue(portSel, cboUUnitlValue,portCodeId).equalsIgnoreCase("1")){
				Result+=",9602";
			}
		    //不查出累计净值
			if(ctlPub.getvalue(portSel, CboAddUnitValue,portCodeId).equalsIgnoreCase("1")){
				Result+=",9612";
			}
			//不查出汇兑损益
			if(ctlPub.getvalue(portSel, CboExchang,portCodeId).equalsIgnoreCase("1")){
				Result+=",9603";
			}
			//不查出估值增值
			if(ctlPub.getvalue(portSel, CboAddValue,portCodeId).equalsIgnoreCase("1")){
				Result+=",9604";
			}
			//不查出单位净值(人民币)
			if(ctlPub.getvalue(portSel, cboUnitValue,portCodeId).equalsIgnoreCase("1")){
				Result+=",9600";
			}
			//不查出资产净值(人民币)
			if(ctlPub.getvalue(portSel, CboAssetValue,portCodeId).equalsIgnoreCase("1")){
				Result+=",9000";
			}
			//不查出日净值增长率
			if(ctlPub.getvalue(portSel, CboAddUpValue,portCodeId).equalsIgnoreCase("1")){
				Result+=",9991";
			}
			//不查出累计净值增长率
			if(ctlPub.getvalue(portSel,CboAddupAsset,portCodeId).equalsIgnoreCase("1")){
				Result+=",9992";
			}
			//不查出本期基金份额净值增长率 
			if(ctlPub.getvalue(portSel,CboAddLot,portCodeId).equalsIgnoreCase("1")){
				Result+=",9993";
			}
			/**add---huhuichao 2013-9-6 BUG  9402 市场指数日净值增长率的算法问题 */
			/**Start 20130711 added by liubo.Story #4106.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001
			 * 通过估值表统计项显示的通参，判断是否显示市场指数日净值增长率（默认不显示）*/
			if(ctlPub.getvalue(portSel,"cboIdxDateRate",portCodeId).equalsIgnoreCase("1")){
				Result+=",9990";
			}
			/**End 20130711 added by liubo.Story #4106.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001*/
			/**end---huhuichao 2013-9-6 BUG  9402  */
			
			//不显示减值准备相关科目  yeshenghong #1652
//			if(ctlPub.getShowValueSetting(portSel, CboShowDec).equalsIgnoreCase("0")){
//				showDec = "0";
//			}
			if(ctlPub.getShowValueSetting("CboShowDec").equalsIgnoreCase("1")){
				showDec = "1";
			}
			
			
			YssDbOperSql YD=new YssDbOperSql(pub);
			YD.setYssPub(pub);
			String sqlResult="("+YD.sqlCodes(Result)+")";
			//----------------end---------------
			
			/**********modify by zhangjun 2012-03-03 STORY #2199 华夏ETF财务估值表中的股票投资明细科目按股票代码从小到大排序
			if (strArray[2].equalsIgnoreCase("***")) {
				// modify by fangjiang 2010.10.18 MS01767 QDV4易方达2010年9月20日01_A
				//modify by fangjiang 2012.01.05 story 2013   考虑到多Class 将 a.facctcode = '8700' 改成 a.facctcode = '8700' or a.facctcode like '_8700'
				sql = "select symbol,facctcode,fcurcode,facctname,facctclass,avg(fexchangerate) as fexchangerate,sum(famount) as famount,sum(fcost) as fcost,sum(fstandardmoneycost) as fstandardmoneycost,abs(ROUND(sum(ratio),4)) as ratio,avg(fmarketprice) as fmarketprice,sum(fmarketvalue) as fmarketvalue,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,abs(ROUND(sum(fmvratio),6)) as fmvratio,sum(fappreciation) as fappreciation,sum(fstandardmoneyappreciation) as fstandardmoneyappreciation,fmarketdescribe,fdesc,facctlevel,facctdetail,avg(FOtPrice1) as FOtPrice1,avg(FOtPrice2) as FOtPrice2,avg(FOtPrice3) as FOtPrice3 from "
						+
						// ------------------------------
						// 将成本的round位数从保留2位改为4位 sj edit 20080408
						"(" +
						// 8801负债类合计项目需转换进行显示fazmm20070923
						" Select case when a.facctdetail=0 then '-' when a.facctdetail=1 and "
						+ dbl.sqlLen(dbl.sqlTrim("b.fauxiacc"))
						+ " is not null then '-' else ' ' end as symbol,"
						+ " a.facctcode,a.facctclass,case when b.fcurcode='***' then ' ' when b.fcurcode is null then a.fcurcode else b.fcurcode end as fcurcode,"
						+ "a.facctname,case when b.fcurcode='***' then 0 else a.fexchangerate end as fexchangerate,"
						+ "(case when b.fcurcode='***' then 0 else a.famount end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as famount,"
						+ "(case when b.fcurcode='***' then 0 else a.fcost end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end)  as fcost,"
						+ "a.fstandardmoneycost*(case  "
						+ "when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneycost,a.fstandardmoneycosttonetratio as ratio,"
						+ "case when b.fcurcode='***' then 0 else a.fmarketprice end as fmarketprice,"
						+ "(case when b.fcurcode='***' then 0 when (a.facctcode = '8700' or a.facctcode like '_8700') then a.fcost else a.fmarketvalue end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fmarketvalue,"
						+ // BugNo:0000368 edit by jc
						"(case when (a.facctcode = '8700' or a.facctcode like '_8700') then a.fcost else a.fstandardmoneymarketvalue end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneymarketvalue,"
						+ // BugNo:0000368 edit by jc
						"a.fstandardmoneymarketvaluetorat as fmvratio,case when b.fcurcode='***' then 0 else a.fappreciation end  as fappreciation ,"
						+ "a.fstandardmoneyappreciation,a.fmarketdescribe,a.facctlevel,a.facctdetail ,substr(a.facctcode,1,8) bacctcode,fdesc,FOtPrice1,FOtPrice2,FOtPrice3  from "
						+ pub.yssGetTableName(sGVTableName)
						+ " a "
						+ "left join "
						+ YssTabPrefix
						+ "laccount b on a.facctcode=b.facctcode "
						+ "where ((a.facctclass='合计' and "
						+ dbl.sqlLen(dbl.sqlTrim("a.fcurcode"))
						+ " is null) or (a.facctclass='合计' and a.facctcode='8600') or (a.facctclass<>'合计')) and FPortCode='"
						+ strArray[1]
						+ "' and fdate="
						+ dbl.sqlDate(strArray[0])
						
						+ " and a.FAcctCode not in ('C100','D200')"		
						+" and a.FAcctCode not like '2161%'";
						
						if(showDec.equalsIgnoreCase("1"))
						{
							sql += " and a.facctattr not like '减值准备%'"; // modified by yeshenghong #1652 不显示减值准备							
						}
						sql += " and a.FAcctCode not in "+sqlResult+""  
						
						+ " ) guevalue group by symbol, facctcode, bacctcode , facctclass,fcurcode,facctname,fmarketdescribe,facctlevel,facctdetail,fdesc order by facctcode,FStandardmoneymarketvalue desc,facctlevel ";
			
			
			}else {
					
							sql = "Select case when a.facctdetail=0 then '-' when a.facctdetail=1 and "
								+ dbl.sqlLen(dbl.sqlTrim("b.fauxiacc"))
								+ " is not null then '-' else ' ' end as symbol, a.facctcode,a.fcurcode,a.facctname,a.facctclass,a.fexchangerate,(a.famount*(case when b.fbaldc is null then 1 else b.fbaldc end)) as famount,( a.fcost*(case when b.fbaldc is null then 1 else b.fbaldc end) ) as fcost,( a.fstandardmoneycost*(case when b.fbaldc is null then 1 else b.fbaldc end)) as fstandardmoneycost,a.fcosttonetratio as ratio,(a.fmarketprice) as fmarketprice ,( a.fmarketvalue*(case when b.fbaldc is null then 1 else b.fbaldc end) ) as fmarketvalue,(a.fstandardmoneymarketvalue*(case when b.fbaldc is null then 1 else b.fbaldc end) ) as fstandardmoneymarketvalue,a.fmarketvaluetoratio as fmvratio,(a.fappreciation) as fappreciation,(a.fstandardmoneyappreciation) as fstandardmoneyappreciation,a.fmarketdescribe,a.fdesc,a.facctlevel,a.facctdetail from "
								+ pub.yssGetTableName(sGVTableName)
								+ " a left join "
								+ YssTabPrefix
								+ "laccount b on a.facctcode=b.facctcode where a.FPortCode='"
								+ strArray[1]
								+ "' and a.fdate="
								+ dbl.sqlDate(strArray[0])
								+" and a.FAcctCode not like '2161%'";//modify baopingping #story 1294  不显示2161相关类科目
								if(showDec.equalsIgnoreCase("1"))
								{
									sql += " and a.facctattr not like '减值准备%'"; // modified by yeshenghong #1652 不显示减值准备							
								}
								sql += " and a.FAcctCode not in "+sqlResult+"" //modify by baopingping  20110610  STORY #1041
								+ // 不显示三个属性的值，在下面单独显示 QDV4海富通2009年05月11日01_AB MS00439
									// by leeyu 20090519
								" and a.fcurcode='"
								+ strArray[2]
								+ "' and FAcctCode<>'C100' order by facctcode,facctlevel ";
						
			}
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			*/
			
			//modify by zhangjun 2012-03-03  STORY #2199 华夏ETF财务估值表中的股票投资明细科目按股票代码从小到大排序
			
			if (strArray[2].equalsIgnoreCase("***")) {				
				sql = " select * from ((" 
					    +"select symbol,facctcode,fcurcode,facctname,facctclass,avg(fexchangerate) as fexchangerate,sum(famount) as famount,sum(fcost) as fcost,sum(fstandardmoneycost) as fstandardmoneycost,abs(ROUND(sum(ratio),4)) as ratio,avg(fmarketprice) as fmarketprice,sum(fmarketvalue) as fmarketvalue,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,abs(ROUND(sum(fmvratio),6)) as fmvratio,sum(fappreciation) as fappreciation,sum(fstandardmoneyappreciation) as fstandardmoneyappreciation,fmarketdescribe,fdesc,facctlevel,facctdetail,avg(FOtPrice1) as FOtPrice1,avg(FOtPrice2) as FOtPrice2,avg(FOtPrice3) as FOtPrice3,facctcode as facctcode1,0 as facctcode2 from "
						+ "(" + " Select case when a.facctdetail=0 then '-' when a.facctdetail=1 and "
						+ dbl.sqlLen(dbl.sqlTrim("b.fauxiacc"))
						+ " is not null then '-' else ' ' end as symbol,"
						+ " a.facctcode,a.facctclass,case when b.fcurcode='***' then ' ' when b.fcurcode is null then a.fcurcode else b.fcurcode end as fcurcode,"
						+ "a.facctname,case when b.fcurcode='***' then 0 else a.fexchangerate end as fexchangerate,"
						+ "(case when b.fcurcode='***' then 0 else a.famount end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as famount,"
						+ "(case when b.fcurcode='***' then 0 else a.fcost end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end)  as fcost,"
						+ "a.fstandardmoneycost*(case  "
						+ "when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneycost,a.fstandardmoneycosttonetratio as ratio,"
						+ "case when b.fcurcode='***' then 0 else a.fmarketprice end as fmarketprice,"
						+ "(case when b.fcurcode='***' then 0 when (a.facctcode = '8700' or a.facctcode like '_8700') then a.fcost else a.fmarketvalue end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fmarketvalue,"
						+ // BugNo:0000368 edit by jc
						"(case when (a.facctcode = '8700' or a.facctcode like '_8700') then a.fcost else a.fstandardmoneymarketvalue end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneymarketvalue,"
						+ // BugNo:0000368 edit by jc
						"a.fstandardmoneymarketvaluetorat as fmvratio,case when b.fcurcode='***' then 0 else a.fappreciation end  as fappreciation ,"
						+ "a.fstandardmoneyappreciation,a.fmarketdescribe,a.facctlevel,a.facctdetail ,substr(a.facctcode,1,8) bacctcode,fdesc,FOtPrice1,FOtPrice2,FOtPrice3  from "
						+ pub.yssGetTableName(sGVTableName)
						+ " a "
						+ "left join "
						+ YssTabPrefix
						+ "laccount b on a.facctcode=b.facctcode "
						+ "where ((a.facctclass='合计' and "
						+ dbl.sqlLen(dbl.sqlTrim("a.fcurcode"))
						+ " is null) or (a.facctclass='合计' and a.facctcode='8600') or (a.facctclass<>'合计')) and FPortCode='"
						+ strArray[1]
						+ "' and fdate="
						+ dbl.sqlDate(strArray[0]);
						/*  modify by zhangjun 2012-03-03 story #2199
						+ " and a.FAcctCode not in ('C100','D200')"		
						+" and a.FAcctCode not like '2161%'";
						
						if(showDec.equalsIgnoreCase("1"))
						{
							sql += " and a.facctattr not like '减值准备%'"; // modified by yeshenghong #1652 不显示减值准备							
						}
						sql += " and a.FAcctCode not in "+sqlResult+""  
						*/
						if(showDec.equalsIgnoreCase("1"))
						{
							sql += " and a.facctattr not like '减值准备%'"; 							
						}
					sql += " and ( a.FAcctCode like '1002%'" 
						+ " or a.FAcctCode like '1021%'" 
						+ " or a.FAcctCode like '1031%' ) " +	
						" ) guevalue group by symbol, facctcode, bacctcode , facctclass,fcurcode,facctname,fmarketdescribe,facctlevel,facctdetail,fdesc ) " ;
						//"order by facctcode,FStandardmoneymarketvalue desc,facctlevel ) " ;
				
						
				//***********1102 股票投资单独处理******************************
				
				sql = sql + " union ( select aaa.*, " 
						+" case when facctdetail = 1 and  Facctlevel = 4 and facctcode like '1102%' and " 
						+" get_val_facctcode(facctcode) != 0 then " //modified by yeshenghong BUG
						+" substr(facctcode,0,INSTR(facctcode,'_',1,1)-1) " 
						+" else Facctcode  end  as facctcode1 ,"
						
						+" case when facctdetail = 1 and  Facctlevel = 4 and facctcode like '1102%_%' and "
						+" get_val_facctcode(facctcode) != 0 then "
						+" get_val_facctcode(facctcode) "
						
						//+" then to_number(substr(facctcode,INSTR(facctcode,'_',1,1)+1,INSTR(facctcode,' ',1,1)-INSTR(facctcode,'_',1,1)))"
						+" else 0  end  as facctcode2 from ( "
						+" select symbol,facctcode,fcurcode,facctname,facctclass,avg(fexchangerate) as fexchangerate,sum(famount) as famount,sum(fcost) as fcost,sum(fstandardmoneycost) as fstandardmoneycost,abs(ROUND(sum(ratio),4)) as ratio,avg(fmarketprice) as fmarketprice,sum(fmarketvalue) as fmarketvalue,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,abs(ROUND(sum(fmvratio),6)) as fmvratio,sum(fappreciation) as fappreciation,sum(fstandardmoneyappreciation) as fstandardmoneyappreciation,fmarketdescribe,fdesc,facctlevel,facctdetail,avg(FOtPrice1) as FOtPrice1,avg(FOtPrice2) as FOtPrice2,avg(FOtPrice3) as FOtPrice3 from "
						+ "(" + " Select case when a.facctdetail=0 then '-' when a.facctdetail=1 and "
						+ dbl.sqlLen(dbl.sqlTrim("b.fauxiacc"))
						+ " is not null then '-' else ' ' end as symbol,"
						+ " a.facctcode,a.facctclass,case when b.fcurcode='***' then ' ' when b.fcurcode is null then a.fcurcode else b.fcurcode end as fcurcode,"
						+ "a.facctname,case when b.fcurcode='***' then 0 else a.fexchangerate end as fexchangerate,"
						+ "(case when b.fcurcode='***' then 0 else a.famount end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as famount,"
						+ "(case when b.fcurcode='***' then 0 else a.fcost end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end)  as fcost,"
						+ "a.fstandardmoneycost*(case  "
						+ "when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneycost,a.fstandardmoneycosttonetratio as ratio,"
						+ "case when b.fcurcode='***' then 0 else a.fmarketprice end as fmarketprice,"
						+ "(case when b.fcurcode='***' then 0 when (a.facctcode = '8700' or a.facctcode like '_8700') then a.fcost else a.fmarketvalue end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fmarketvalue,"
						+ // BugNo:0000368 edit by jc
						"(case when (a.facctcode = '8700' or a.facctcode like '_8700') then a.fcost else a.fstandardmoneymarketvalue end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneymarketvalue,"
						+ // BugNo:0000368 edit by jc
						"a.fstandardmoneymarketvaluetorat as fmvratio,case when b.fcurcode='***' then 0 else a.fappreciation end  as fappreciation ,"
						+ "a.fstandardmoneyappreciation,a.fmarketdescribe,a.facctlevel,a.facctdetail ,substr(a.facctcode,1,8) bacctcode,fdesc,FOtPrice1,FOtPrice2,FOtPrice3  from "
						+ pub.yssGetTableName(sGVTableName)
						+ " a "
						+ "left join "
						+ YssTabPrefix
						+ "laccount b on" +
						" case when instr(a.facctcode,'_')>0 then substr(a.facctcode,0,instr(a.facctcode,'_')-1) else a.facctcode end = b.facctcode "
						//delete by jsc 20120401 BUG4127财务估值表的资本利得税的辅助核算项，金额应为黑字，但是显示在估值表上却为红字
						//" a.facctcode=b.facctcode "
						
						+ "where ((a.facctclass='合计' and "
						+ dbl.sqlLen(dbl.sqlTrim("a.fcurcode"))
						+ " is null) or (a.facctclass='合计' and a.facctcode='8600') or (a.facctclass<>'合计')) and FPortCode='"
						+ strArray[1]
						+ "' and fdate="
						+ dbl.sqlDate(strArray[0]);
						// modify by zhangjun 2012-03-03 story #2199
						if(showDec.equalsIgnoreCase("1"))
						{
							sql += " and a.facctattr not like '减值准备%'"; 							
						}
						sql += " and a.FAcctCode like '1102%'" +	
						" ) guevalue group by symbol, facctcode, bacctcode , facctclass,fcurcode,facctname,fmarketdescribe,facctlevel,facctdetail,fdesc " +
						" ) aaa ) " ;
				
				//*************1002和1102之外的科目******************************
				sql = sql + "union ( select symbol,facctcode,fcurcode,facctname,facctclass,avg(fexchangerate) as fexchangerate,sum(famount) as famount,sum(fcost) as fcost,sum(fstandardmoneycost) as fstandardmoneycost,abs(ROUND(sum(ratio),4)) as ratio,avg(fmarketprice) as fmarketprice,sum(fmarketvalue) as fmarketvalue,sum(fstandardmoneymarketvalue) as fstandardmoneymarketvalue,abs(ROUND(sum(fmvratio),6)) as fmvratio,sum(fappreciation) as fappreciation,sum(fstandardmoneyappreciation) as fstandardmoneyappreciation,fmarketdescribe,fdesc,facctlevel,facctdetail,avg(FOtPrice1) as FOtPrice1,avg(FOtPrice2) as FOtPrice2,avg(FOtPrice3) as FOtPrice3 ,facctcode as facctcode1,0 as facctcode2 from "
						+"(" + " Select case when a.facctdetail=0 then '-' when a.facctdetail=1 and "
						+ dbl.sqlLen(dbl.sqlTrim("b.fauxiacc"))
						+ " is not null then '-' else ' ' end as symbol,"
						+ " a.facctcode,a.facctclass,case when b.fcurcode='***' then ' ' when b.fcurcode is null then a.fcurcode else b.fcurcode end as fcurcode,"
						+ "a.facctname,case when b.fcurcode='***' then 0 else a.fexchangerate end as fexchangerate,"
						+ "(case when b.fcurcode='***' then 0 else a.famount end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as famount,"
						+ "(case when b.fcurcode='***' then 0 else a.fcost end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end)  as fcost,"
						+ "a.fstandardmoneycost * (case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneycost,"
						+ "a.fstandardmoneycosttonetratio as ratio,"
						+ "case when b.fcurcode='***' then 0 else a.fmarketprice end as fmarketprice,"
						+ "(case when b.fcurcode='***' then 0 when (a.facctcode = '8700' or a.facctcode like '_8700') then a.FCost else a.fmarketvalue end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fmarketvalue,"
						+ // BugNo:0000368 edit by jc
						//edit by songjie 2012.07.18 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
						"(case when (a.facctcode = '8700' or a.facctcode like '_8700') then a.fStandardMoneycost else a.fstandardmoneymarketvalue end)*(case when a.facctcode ='8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneymarketvalue,"
						+ // BugNo:0000368 edit by jc
						"a.fstandardmoneymarketvaluetorat as fmvratio,case when b.fcurcode='***' then 0 else a.fappreciation end  as fappreciation ,"
						+ "a.fstandardmoneyappreciation,a.fmarketdescribe,a.facctlevel,a.facctdetail ,substr(a.facctcode,1,8) bacctcode,fdesc,FOtPrice1,FOtPrice2,FOtPrice3  from "
						+ pub.yssGetTableName(sGVTableName)
						+ " a "
						+ "left join "
						+ YssTabPrefix
						+ "laccount b on " +" case when instr(a.facctcode,'_')>0 then substr(a.facctcode,0,instr(a.facctcode,'_')-1) else a.facctcode end = b.facctcode "
						//delete by jsc 20120401 BUG4127财务估值表的资本利得税的辅助核算项，金额应为黑字，但是显示在估值表上却为红字
						//" a.facctcode=b.facctcode "
						+ "where ((a.facctclass='合计' and "
						+ dbl.sqlLen(dbl.sqlTrim("a.fcurcode"))
						+ " is null) or (a.facctclass='合计' and a.facctcode='8600') or (a.facctclass<>'合计')) and FPortCode='"
						+ strArray[1]
						+ "' and fdate="
						+ dbl.sqlDate(strArray[0])
						
						+ " and a.FAcctCode not in ('C100','D200')"	
						//********以下科目上面已作处理************
						+ " and a.FAcctCode not like '1002%'" 
						+ " and a.FAcctCode not like '1021%'" 
						+ " and a.FAcctCode not like '1031%'" 
						+ " and a.FAcctCode not like '1102%'" 
						+ " and a.FAcctCode not like '2161%'";
						//****8888***********************************
						if(showDec.equalsIgnoreCase("1"))
						{
							sql += " and a.facctattr not like '减值准备%'";						
						}
						sql += " and a.FAcctCode not in "+sqlResult+""  
						
						+ " ) guevalue group by symbol, facctcode, bacctcode , facctclass,fcurcode,facctname,fmarketdescribe,facctlevel,facctdetail,fdesc " 
						+" )) order by facctcode1,facctcode2,FStandardmoneymarketvalue desc,facctlevel";
			
						
			}else {
					
							sql = "Select case when a.facctdetail=0 then '-' when a.facctdetail=1 and "
								+ dbl.sqlLen(dbl.sqlTrim("b.fauxiacc"))
								+ " is not null then '-' else ' ' end as symbol, a.facctcode,a.fcurcode,a.facctname,a.facctclass,a.fexchangerate,(a.famount*(case when b.fbaldc is null then 1 else b.fbaldc end)) as famount,( a.fcost*(case when b.fbaldc is null then 1 else b.fbaldc end) ) as fcost,( a.fstandardmoneycost*(case when b.fbaldc is null then 1 else b.fbaldc end)) as fstandardmoneycost,a.fcosttonetratio as ratio,(a.fmarketprice) as fmarketprice ,( a.fmarketvalue*(case when b.fbaldc is null then 1 else b.fbaldc end) ) as fmarketvalue,(a.fstandardmoneymarketvalue*(case when b.fbaldc is null then 1 else b.fbaldc end) ) as fstandardmoneymarketvalue,a.fmarketvaluetoratio as fmvratio,(a.fappreciation) as fappreciation,(a.fstandardmoneyappreciation) as fstandardmoneyappreciation,a.fmarketdescribe,a.fdesc,a.facctlevel,a.facctdetail from "
								+ pub.yssGetTableName(sGVTableName)
								+ " a left join "
								+ YssTabPrefix
								+ "laccount b " +" on case when instr(a.facctcode,'_')>0 then substr(a.facctcode,0,instr(a.facctcode,'_')-1) else a.facctcode end = b.facctcode "
								//delete by jsc 20120401 BUG4127财务估值表的资本利得税的辅助核算项，金额应为黑字，但是显示在估值表上却为红字
								//" a.facctcode=b.facctcode "
								+ " where a.FPortCode='"
								+ strArray[1]
								+ "' and a.fdate="
								+ dbl.sqlDate(strArray[0])
								+" and a.FAcctCode not like '2161%'";//modify baopingping #story 1294  不显示2161相关类科目
								if(showDec.equalsIgnoreCase("1"))
								{
									sql += " and a.facctattr not like '减值准备%'"; // modified by yeshenghong #1652 不显示减值准备							
								}
								sql += " and a.FAcctCode not in "+sqlResult+"" //modify by baopingping  20110610  STORY #1041
								+ // 不显示三个属性的值，在下面单独显示 QDV4海富通2009年05月11日01_AB MS00439
									// by leeyu 20090519
								" and a.fcurcode='"
								+ strArray[2]
								+ "' and FAcctCode<>'C100' order by facctcode,facctlevel ";
						
			}
			rs = dbl.queryByPreparedStatement(sql);
			//---------------------------------end by zhangjun 2012-03-03------------------------------------------------------
			while (rs.next()) {
				if(rs.getString("FACCTCLASS")!=null && !rs.getString("FACCTCLASS").equals("")){
					String[] facctclass=rs.getString("FACCTCLASS").split("\t");
					if(facctclass[0].equalsIgnoreCase("class")){
						//组合分级   科目名称   金额    等级    是否明细项      数量
						classList.add(facctclass[1]+"\t"+rs.getString("facctname")+"\t"+
										rs.getDouble("fstandardmoneymarketvalue")+"\t"+rs.getInt("FACCTLEVEL")
										+"\t"+rs.getInt("FACCTDETAIL")+"\t"+rs.getDouble("famount"));
						continue;
					}
				}
				//---------------------------end------------------------
				if (rs.getRow() > 1) {
					buffer.append("\r\n");
				}
				if ((!tmp.equalsIgnoreCase(rs.getString("facctclass")))
						&& rs.getString("facctclass").equalsIgnoreCase("合计")) {
					buffer.append("\r\n\r\n");
				}
				tmp = rs.getString("facctclass");
				buffer
						.append(rs.getString("facctclass").equalsIgnoreCase(
								"合计") ? "" : rs.getString("symbol"));
				buffer.append("\t")
						.append(
								rs.getString("facctclass").equalsIgnoreCase(
										"合计") ? (strArray[2]
										.equalsIgnoreCase("***")
										&& rs.getString("fcurcode").trim()
												.length() > 0 ? "  —其中"
										+ rs.getString("fcurcode") : rs
										.getString("facctname")) : rs
										.getString("facctcode")).append("\t");
				// append(rs.getString("facctclass").equalsIgnoreCase("合计") ?
				// (rs.getString("facctcode").equalsIgnoreCase("9600") ||
				// rs.getString("facctcode").equalsIgnoreCase("9601") ?
				// YssFun.formatNumber(rs.getDouble(
				// "fstandardmoneymarketvalue"), "0.000") : "") :
				// rs.getString("facctname")).append("\t");
				// -------------sj edit 20080422
				// ---------------------------------------
				//modify by fangjiang 2012.01.08 story 2013
				String acctCode = rs.getString("facctcode");
				if(acctCode.length() == 5){
					acctCode = acctCode.substring(1);
				}
				if (rs.getString("facctclass").equalsIgnoreCase("合计")) { // sj
																			// edit
																			// 20080422
																			// 增加以美元计的单位净值，使它的显示位数为6
					// 2008-5-20
					// 添加||rs.getString("facctcode").equalsIgnoreCase("9600")让以前生成的净值表的单位净值显示在科目名称一列
					// 单亮
					// if
					// (rs.getString("facctcode").equalsIgnoreCase("90001")||rs.getString("facctcode").equalsIgnoreCase("9600"))
					// { //单亮 2008-5-4 将9600修改为90001
					
					if (acctCode.equalsIgnoreCase("9600")
							|| acctCode.equalsIgnoreCase(
									"9612")) { // MS00374 QDV4建行2009年4月11日01_A
												// 添加对累计净值的判断
						// ===========================根据通用参数设置来显示单位净值的小数位数 by
						// leeyu 2008-11-3 0000516
						// 2008-6-6 单亮
						// 源代码，在已经修改了在插入单位净值时按照配置参数显示的小数位数，所以在此处不用限制显示的小数位数
						// begin
						int iDigit = 0; // 李钰 添加,用于根据前台的 单位净值保留长度 设置来设置财务估值表的
										// 单位净值保留长度 080528
						String sFormat = "";
						CtlPubPara ctlPara = new CtlPubPara();
						ctlPara.setYssPub(pub);
						iDigit = Integer.parseInt(ctlPara
								.getCashUnit(getPortCode(strArray[1], YssFun
										.left(strArray[0], 4))));
						// buffer.append(YssFun.formatNumber(rs.getDouble(
						// "fstandardmoneymarketvalue"), "0.000"));
						if (iDigit > 0) {
							for (int i = 0; i < iDigit; i++) {
								sFormat += "0";
							}
							sFormat = "#0." + sFormat;
						} else {
							sFormat = "#.000"; // 默认保留3位
						}
						buffer
								.append(YssFun
										.formatNumber(
												YssD
														.round(
																rs
																		.getDouble("fstandardmoneymarketvalue"),
																iDigit),
												sFormat));

						// 修改后的代码
						// buffer.append(rs.getDouble("fstandardmoneymarketvalue"));
						// end
						// ===============================2008-11-3
						// BugNo:0000478 edit by jc 如果有单位净值的话就显示本期基金净值增长率
						if (String.valueOf(
								rs.getDouble("fstandardmoneymarketvalue"))
								.length() > 0) {
							//doLoad = true;modify by baopingping #story 1263 2011-08-01
						}
						// ----------------------jc
					} else if (acctCode.equalsIgnoreCase(
							"9602")) { // 2008-5-4 sl 将单位净值（美元）的编号修改为9002
										// （要修改其显示的位置）2008-5-20 sl
										// 将单位净值（美元）9002的编号修改为9602
						buffer.append(YssFun.formatNumber(rs
								.getDouble("Fmvratio"), "0.000000"));
					}
					//add by baopingping #story 1263     2011-08-01 添加“日净值增长率”，“累计净值增长率”，“本期基金份额净值增长率” 在财务估值表中的显示
					else if(acctCode.equalsIgnoreCase("9991")){
						buffer
						.append(
								YssFun.formatNumber(YssD.mul(rs
										.getDouble("fstandardmoneymarketvalue"), 100), "#.00")
								+ "%").append("\t");
					}else if(acctCode.equalsIgnoreCase("9993")){
						buffer
						.append(
								YssFun.formatNumber(YssD.mul(rs
										.getDouble("fstandardmoneymarketvalue"), 100), "#.00")
								+ "%").append("\t");
					}else if(acctCode.equalsIgnoreCase("9992")){
						buffer
						.append(
								YssFun.formatNumber(YssD.mul(rs
										.getDouble("fstandardmoneymarketvalue"), 100), "#.00")
								+ "%").append("\t");
					}
					//------------end----------------------
					/**add---huhuichao 2013-9-6 BUG  9402 市场指数日净值增长率的算法问题 */
					/**Start 20130711 added by liubo.Story #4106.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001
					 * 控制市场指数日净值增长率的显示型式（以百分比型式在科目名称列中显示）*/
					else if(acctCode.equalsIgnoreCase("9990")){
						buffer
						.append(
								YssFun.formatNumber(YssD.mul(rs
										.getDouble("fstandardmoneymarketvalue"), 100), "#.0000")
								+ "%").append("\t");
					}
					/**End 20130711 added by liubo.Story #4106.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001*/
					/**end---huhuichao 2013-9-6 BUG  9402  */
					// =======QDV4海富通2008年12月31日03_B MS00176 加载表中的涨跌幅比例
					else if (acctCode.equalsIgnoreCase("9605")) {
						buffer
								.append(
										YssFun
												.formatNumber(
														rs
																.getDouble("fstandardmoneymarketvalue"),
														"0.00##")
												+ "%").append("\t");
					}
					// ===QDV4海富通2008年12月31日03_B MS00176
					//modify by huangqirong 2013.02.05 bug #6975
					
					//20130722 modified by liubo.刘瑞测试多币种的问题
					//按汇率分级的多币种基金，多币种部分显示时，数值无千分号，修改之*/
					//=====================================
					else if(acctCode.equalsIgnoreCase("9700")){
						if(rs.getString("facctname").contains("资产净值") || rs.getString("facctname").contains("实收资本")){
							buffer.append(YssFun.formatNumber(rs
//									.getDouble("fstandardmoneymarketvalue"), "0.00"));
									.getDouble("fstandardmoneymarketvalue"), "#,##0.00"));
						}else if(rs.getString("facctname").contains("份额总净值_")){
							buffer.append(YssFun.formatNumber(rs
//									.getDouble("fstandardmoneymarketvalue"), "0.00"));
									.getDouble("fstandardmoneymarketvalue"), "#,##0.00"));
						}else if(rs.getString("facctname").contains("单位净值")){
							CtlPubPara ctlPara = new CtlPubPara();
							ctlPara.setYssPub(pub);
							String digits = "3";
							if(rs.getString("FAcctName") != null && rs.getString("FAcctName").trim().length() > 0 && rs.getString("FAcctName").contains("_") && rs.getString("FAcctName").split("_").length > 2)
								digits = ctlPara.getDigitsPortMethod("PubParaUnitCls","dayfinish","CtlPubParaUnitCls","portClsSel","txtdigit",rs.getString("FAcctName").substring(rs.getString("FAcctName").indexOf("_") + 1 ,rs.getString("FAcctName").lastIndexOf("_")),"3");			   	
						   	String sFormat = "";
							int digt = YssFun.toInt(digits);							
						   	if (digt > 0) {
								for (int i = 0; i < digt; i++) {
									sFormat += "0";
								}
								sFormat = "#,##0." + sFormat;
							} else {
								sFormat = "#,###.000"; // 默认保留3位
							}
						   	buffer.append(YssFun.formatNumber(YssD.round(rs.getDouble("fstandardmoneymarketvalue"),digt), sFormat));
						}else{
							buffer.append(YssFun.formatNumber(rs.getDouble("fstandardmoneymarketvalue"), "#,###.000"));
						}
					}
					//==================end===================
					
					//---end---
					else {
						buffer.append("");
					}
					buffer.append("\t");
				} else {
					buffer.append(rs.getString("facctname")).append("\t");
				}
				// ------------------------------------------------------------------------
				buffer.append(rs.getString("fcurcode")).append("\t").append(
						rs.getDouble("fexchangerate") != 0 ? YssFun
								.formatNumber(rs.getDouble("fexchangerate"),
										"0.00###") : "").append("\t");

				// ------------持仓数量小数位的修改 sunkey 20081224 BungNO:MS00090
				// 通过获取到的格式化字符串，进行格式化输出获取到的数据
				buffer.append(
						rs.getDouble("famount") != 0 ? YssFun.formatNumber(rs
								.getDouble("famount"), formatStr) : "").append(
						"\t");
				// ---------------------------

				// ------南方增加得单位成本列
				buffer.append(
						rs.getDouble("famount") != 0 ? YssFun.formatNumber(rs
								.getDouble("fcost")
								/ rs.getDouble("famount"), "#,##0.00") : "")
						.append("\t");
				// --------------------------------
				// buffer.append(YssFun.formatNumber(rs.getDouble("fcost"),
				// "0.00##")).
				// append("\t").append(YssFun.formatNumber(rs.getDouble(
				// "fstandardmoneycost"), "0.00##")).append("\t");
				// --------------------------------
				// --------sj edit 20080423
				// -----------------------------------------
				// 原代码 2008-5-4 单亮
				// if (rs.getString("facctcode").equalsIgnoreCase("9001") ||
				// rs.getString("facctcode").equalsIgnoreCase("9602") ||
				// rs.getString("facctcode").equalsIgnoreCase("9603")) {
				// buffer.append(YssFun.formatNumber(rs.getDouble("fcost"),
				// "0.00##")).
				// append("\t").append(YssFun.formatNumber(rs.getDouble(
				// "fstandardmoneymarketvalue"), "0.00##")).append("\t");
				// //因为以美元计价的净值和汇兑损益和估指增值是存储在市值中的，只是显示在成本中
				// }
				// 修改后的 2008-5-4 单亮
				// begin
				if (acctCode.equalsIgnoreCase("9601")) // ||
				// rs.getString("facctcode").equalsIgnoreCase("9604") ||
				// rs.getString("facctcode").equalsIgnoreCase("9603"))
				{ // 2008-5-6 单亮 把9603修改为9601（修改其显示的位置）//2008-5-20 单亮
					// 把9601修改为9603
					buffer.append(
							YssFun
									.formatNumber(rs.getDouble("fcost"),
											"0.00##")).append("\t");

					buffer
							.append(
									acctCode.equalsIgnoreCase(
											"9600")
											|| acctCode
													.equalsIgnoreCase("9602")
											|| // 2008-5-4 sl
												// 将单位净值（美元）的编号修改为9002
												// （要修改其显示的位置）// 2008-5-20 sl
												// 将单位净值（美元）9002的编号修改为9602
											acctCode
													.equalsIgnoreCase("9604")
											|| acctCode
													.equalsIgnoreCase("9603")
											|| // 2008-5-6 单亮
												// 把9603修改为9601（修改其显示的位置）//2008-5-20
												// 单亮 把9601修改为9603（修改其显示的位置）
											acctCode
													.equalsIgnoreCase("9601")
											|| acctCode
													.equalsIgnoreCase("9612") ? // MS00374
																				// QDV4建行2009年4月11日01_A
																				// 添加对累计净值的判断
									"0"
											: YssFun
													.formatNumber(
															rs
																	.getDouble("fstandardmoneymarketvalue"),
															"0.00##")).append(
									"\t"); // 因为以美元计价的净值和汇兑损益和估指增值是存储在市值中的，只是显示在成本中

				}
				//add by 黄啟荣   2011-06-07 STORY #1103
				else if (acctCode.equalsIgnoreCase("8700")){
					if(isJSPortCode){
						buffer.append("\t")
						.append(
								YssFun.formatNumber(rs
										.getDouble("fcost"),
										"0.00##")).append("\t");
					}else{
						buffer.append(YssFun.formatNumber(rs.getDouble("fcost"),
												"0.00##")).append("\t")
								.append(
										YssFun.formatNumber(rs
												//edit by songjie 2012.07.18 TORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
												.getDouble("fstandardmoneycost"),
												"0.00##")).append("\t");
					}
				//-----end-------
				}
				// end
				else {
					buffer.append(
							YssFun
									.formatNumber(rs.getDouble("fcost"),
											"0.00##")).append("\t")
							.append(
									YssFun.formatNumber(rs
											.getDouble("fstandardmoneycost"),
											"0.00##")).append("\t");
				}
				// ------------------------------------------------------------------
				buffer.append(
						rs.getDouble("ratio") != 0 ? YssFun.formatNumber(rs
								.getDouble("ratio") * 100, "0.00##") : "")
						.append("\t");
				buffer.append(
						rs.getDouble("fmarketprice") != 0 ? YssFun
								.formatNumber(rs.getDouble("fmarketprice"),
										"#,##0.00##") : "").append("\t");
				buffer.append(
						rs.getDouble("FOTPrice1") != 0 ? YssFun.formatNumber(rs
								.getDouble("FOTPrice1"), "#,##0.00##") : "")
						.append("\t"); // 增加既期价格的显示

				// 2008-5-7 单亮 在市值的原币下面显示值 begin
				if (acctCode.equalsIgnoreCase("9601")) {
					buffer
							.append(
									acctCode.equalsIgnoreCase(
											"9601") ? YssFun
											.formatNumber(
													rs
															.getDouble("fstandardmoneymarketvalue"),
													"0.00##")
											: "0").append("\t");

				}
				//add by 黄啟荣  2011-06-07 STORY #1103
				else if(acctCode.equalsIgnoreCase("8700")){
					if(isJSPortCode){
						buffer.append("\t");
					}else{
						buffer.append(YssFun.formatNumber(rs.getDouble("fmarketvalue"),
												"0.00##")).append("\t");						
					}					
				}
				//----------end----------------
				else {
					// end
					buffer.append(
							YssFun.formatNumber(rs.getDouble("fmarketvalue"),
									"0.00##")).append("\t");
				}
				// 2009-5-4--单亮 下面为原来的代码，因为要在市值下面显示所以修改了此段代码，在下面
				// buffer.append(rs.getString("facctcode").equalsIgnoreCase("9600")
				// ||
				// rs.getString("facctcode").equalsIgnoreCase("9002") ||//
				// 2008-5-4 sl 将单位净值（美元）的编号修改为9002 （要修改其显示的位置）
				// rs.getString("facctcode").equalsIgnoreCase("9602") ||
				// rs.getString("facctcode").equalsIgnoreCase("9603") ||
				// rs.getString("facctcode").equalsIgnoreCase("9001") ? // sj
				// edit 20080422
				// "0" :
				// YssFun.formatNumber(rs.getDouble(
				// "fstandardmoneymarketvalue"),
				// "0.00##")).append("\t");
				// 修改后的代码2009-5-4--单亮
				// begin

				if (acctCode.equalsIgnoreCase("9603") || // 2008-5-6
																			// 单亮
																			// 把9603修改为9601（修改其显示的位置）
						// rs.getString("facctcode").equalsIgnoreCase("9600") ||
						acctCode.equalsIgnoreCase("9604") || // 2008-5-15
																				// sl
						acctCode.equalsIgnoreCase("9600") || // 单亮
																				// 2008-5-20
																				// 用来屏蔽单位净值在市值中的显示
						acctCode.equalsIgnoreCase("9601") || // 2008-5-7
																				// 单亮
																				// 把9601(美元总净值)是显示位置修改市值的到原币下面
																				// ，
																				// 所以在此处屏蔽掉
						acctCode.equalsIgnoreCase("9612")) { // MS00374
																				// QDV4建行2009年4月11日01_A
																				// 添加对累计净值的判断

					if (acctCode.equalsIgnoreCase("9603")) { // 2008-5-6
																				// 单亮
																				// 把9603修改为9601（修改其显示的位置）//2008-5-6
																				// 单亮
																				// 把9601修改为9603
						if(rs.getString("facctcode").equalsIgnoreCase("9603")){
						
							buffer
									.append(
											acctCode
													.equalsIgnoreCase("9603") ? getMarketValueToRatio(
													strArray[1], strArray[0])
													: YssFun
															.formatNumber(
																	rs
																			.getDouble("fstandardmoneymarketvalue"),
																	"0.00##"))
									.append("\t");
						}else{
							buffer.append(
									YssFun.formatNumber(rs.getDouble("fstandardmoneymarketvalue"), "0.00##")).append("\t");
						}
					}
					// 2008-5-15 单亮 修改获取估值增值的方法
					if (acctCode.equalsIgnoreCase("9604")) {
						if(rs.getString("facctcode").equalsIgnoreCase("9604")){
							buffer.append(
							// ------ 合罗鹏程代码 add by wangzuochun 2010.12.11 BUG #563
							// 多组合情况下，财务估值表估值增值金额有误
									getAppreciation(strArray[0], strArray[1]))
									.append("\t");// modified by luopc
						}else{
							buffer.append(
									YssFun.formatNumber(rs.getDouble("fstandardmoneymarketvalue"), "0.00##")).append("\t");
						}

					}
					// 2009-01-06 sunkey 处理资产净值（基础货币）的备注显示位置
					// BugNO:MS00083
					// 如果没有以下代码，将缺少一个单元格，造成备注信息在停牌栏显示
					if (acctCode.equalsIgnoreCase("9601")) {
						buffer.append("\t");
					}

				}else{
					buffer
							.append(
									acctCode.equalsIgnoreCase(
											"9600") ? "0" : // 单亮 2008-5-4
															// 将9600修改为90001单亮
															// 2008-5-20
															// 将90001修改为9600
											YssFun
													.formatNumber(
															rs
																	.getDouble("fstandardmoneymarketvalue"),
															"0.00##")).append(
															//=================end===============
									"\t");

				}

				// buffer.append(rs.getString("facctcode").equalsIgnoreCase("9603")?getOperValue("FMarketValueToRatio\t"+strArray[1]+"\t"+strArray[0]):
				// YssFun.formatNumber(rs.getDouble(
				// "fstandardmoneymarketvalue"),
				// "0.00##")).append("\t");

				// buffer.append(rs.getString("facctcode").equalsIgnoreCase("90001")?"0"
				// ://单亮 2008-5-4 将9600修改为90001
				// YssFun.formatNumber(rs.getDouble(
				// "fstandardmoneymarketvalue"),
				// "0.00##")).append("\t");-

				// end

				// buffer.append(rs.getDouble("fmvratio") != 0 ?
				// YssFun.formatNumber(rs.getDouble("fmvratio") * 100,
				// "0.00##") : "").append("\t");
				if (acctCode.equalsIgnoreCase("9602")) {
					buffer.append("").append("\t");
				} else {
					buffer.append(
							rs.getDouble("fmvratio") != 0 ? YssFun
									.formatNumber(
											rs.getDouble("fmvratio") * 100,
											"0.00") : "").append("\t");
				}
				buffer.append(
						YssFun.formatNumber(rs.getDouble("fappreciation"),
								"0.00##")).append("\t");
				buffer.append(
						YssFun.formatNumber(rs
								.getDouble("fstandardmoneyappreciation"),
								"0.00##")).append("\t");
				buffer.append(rs.getString("fmarketdescribe")).append("\t");
				buffer.append(rs.getString("fdesc")).append("\t");
				buffer.append(rs.getInt("facctlevel")).append("\t");

				// ==============================================================================================
				// MS00651 QDV4华夏2009年8月24日02_A fanghaoln
				if (rs.getString("facctcode").equalsIgnoreCase("9600") && acctCode.equalsIgnoreCase("9600")) { // 判断上一行数据可是单位净值
					double amtRateAll = getAccummulateDivided(strArray[0],
							strArray[1]); // 得到份额份额累计净值
					buffer.append("\r\n");
					buffer.append("\t");
					buffer.append("份额累计净值").append("\t");
					// ===========================根据通用参数设置来显示单位净值的小数位数
					int iDigit = 0; // 李钰 添加,用于根据前台的 单位净值保留长度 设置来设置财务估值表的
									// 单位净值保留长度
					String sFormat = "";
					CtlPubPara ctlPara = new CtlPubPara();
					ctlPara.setYssPub(pub);
					iDigit = Integer.parseInt(ctlPara.getCashUnit(getPortCode(
							strArray[1], YssFun.left(strArray[0], 4))));
					if (iDigit > 0) {
						for (int i = 0; i < iDigit; i++) {
							sFormat += "0";
						}
						sFormat = "#0." + sFormat;
					} else {
						sFormat = "#.000"; // 默认保留3位
					}
					double frRateAll = amtRateAll
							+ rs.getDouble("fstandardmoneymarketvalue");
					buffer.append(YssFun.formatNumber(YssD.round(frRateAll,
							iDigit), sFormat));
					buffer
							.append("\t \t\t\t\t0.00\t0.00\t\t\t\t0.00\t0.00\t\t0.00\t \t \t \t0\t"); // 填满一行，否则显示会出问题
				}
				// ====================================================================================================================
			}
			//-------------------------------end story 2013-------------------------------
			//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
			dbl.closeResultSetFinal(rs);
			/* modify by baopingping #story 1263 2011-08-01 将此段代码注释掉 因为这三个值的显示功能在上面已经有代码实现了
			// MS00651 QDV4华夏2009年8月24日02_A 显示日净值增长率 fanghaoln
			if (doLoad && ctlPub.getvalue(portSel, CboAddUpValue,portCodeId).equalsIgnoreCase("0")) {//显示日净值增长率  modify by baopingping  20110608  STORY #1041
				double amtRate = getDateAmountRate(strArray[0], strArray[1]); // 计算得到日净值增长率
				buffer.append("\r\n");
				buffer.append("\t");
				buffer.append("日净值增长率").append("\t");
				buffer.append(
						YssFun.formatNumber(YssD.mul(amtRate, 100), "#.00")
								+ "%").append("\t"); // 显示百分比 MS00795
														// QDV4华夏2009年11月03日01_B
														// fanghaoln 20091113
				buffer
						.append("\t \t\t\t\t0.00\t0.00\t\t\t\t0.00\t0.00\t\t0.00\t \t \t \t0\t"); // 填满一行，否则显示会出问题
			}
			// ====================================MS00651 end
			// ====================================================================================
			// BugNo:0000478 edit by jc 显示本期基金净值增长率
			if (doLoad && ctlPub.getvalue(portSel,CboAddLot,portCodeId).equalsIgnoreCase("0")) {//显示本期基金净值增长率   modify by baopingping  20110608  STORY #1041
				double amtRate = getAmountRate(strArray[0], strArray[1]);
				buffer.append("\r\n");
				buffer.append("\t");
				buffer.append("本期基金份额净值增长率").append("\t");
				buffer.append(
						YssFun.formatNumber(YssD.mul(amtRate, 100), "#.00")
								+ "%").append("\t"); // 显示百分比 MS00795
														// QDV4华夏2009年11月03日01_B
														// fanghaoln 20091113
				buffer
						.append("\t \t\t\t\t0.00\t0.00\t\t\t\t0.00\t0.00\t\t0.00\t \t \t \t0\t"); // 填满一行，否则显示会出问题
			}
			// MS00651 QDV4华夏2009年8月24日02_A 显示累计净值增长率 fanghaoln
			if (doLoad && ctlPub.getvalue(portSel,CboAddupAsset,portCodeId).equalsIgnoreCase("0")) {//显示本期基金净值增长率    modify by baopingping  20110608  STORY #1041
				double amtRate = getAllAmountRate(strArray[0], strArray[1]); // 得到累计净值增长率
				buffer.append("\r\n");
				buffer.append("\t");
				buffer.append("累计净值增长率").append("\t");
				buffer.append(
						YssFun.formatNumber(YssD.mul(amtRate, 100), "#.00")
								+ "%").append("\t"); // 显示百分比 MS00795
														// QDV4华夏2009年11月03日01_B
														// fanghaoln 20091113
				buffer
						.append("\t \t\t\t\t0.00\t0.00\t\t\t\t0.00\t0.00\t\t0.00\t \t \t \t0\t"); // 填满一行，否则显示会出问题
			}
			--------end------*/
			//20110628 added by liubo.Stroy #1095
			//增加“本年利润”、“本年已实现收益”、“本年可分配利润（期间数）”、“本年末可分配利润（期末数）”4个参数的显示
			
			//***********************************
			buffer.append(getCurrentYear(strArray[0],portCodeId));
			//***********Story #1095 end*********************
			
			
			
			// ====================================MS00651 end
			// ====================================================================================
			// 财务估值表增加已实现收益、可分配收益、单位可分配收益 QDV4海富通2009年05月11日01_AB MS00439 by
			// leeyu 20090515
			ctlPub.setYssPub(pub);
			bShowYSX = ctlPub.checkGuessReal(getPortCode(strArray[1], YssFun
					.left(strArray[0], 4))); // 显示财务估值表中已实现部分的数据
												// QDV4海富通2009年05月11日01_AB
												// MS00439
			if (bShowYSX) {

				sql = "select FStandardMoneyMarketValue,FAcctName from "
						+ pub.yssGetTableName("Tb_rep_guessvalue")
						+ " where FDate ="
						+ dbl.sqlDate(YssFun.toDate(strArray[0]))
						//edit by songjie 2011.08.04 BUG 2331 QDV4易方达2011年7月28日01_B 添加FPortCode作为查询条件
						+ " and FAcctCode in('8810','8811','8812') and FPortCode = " + dbl.sqlString(strArray[1]) + "order by FAcctCode ";
				rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
				while (rs.next()) {
					buffer.append("\r\n");
					buffer.append("\t");
					buffer.append(rs.getString("FAcctName")).append("\t");
					buffer.append(
							YssFun.formatNumber(rs
									.getDouble("FStandardMoneyMarketValue"),
									"#,###.####")).append("\t");
					buffer
							.append("\t \t\t\t\t0.00\t0.00\t\t\t\t0.00\t0.00\t\t0.00\t \t \t \t0\t"); // 填满一行，否则显示会出问题
				}
				
				//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
				dbl.closeResultSetFinal(rs);
			}
			// QDV4海富通2009年05月11日01_AB MS00439
			// ----------------------jc
			
			
			
			//modify huangqirong 2012-09-16 story #2782
			TaTradeBean ta=new TaTradeBean();
			ta.setYssPub(pub);
			int clsType = ta.getAccWayState(this.sPortCode);
			//add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
			String key = "";
			//edit by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
			String clsPortStr = "";
			switch(clsType)
			{
				case 0:
					clsPortStr = " ('abLimited','inNetValue') ";
					break;
				case 1:
					clsPortStr = " ('abLimited','inBasicNetValue') ";
					break;
				case 2:
					clsPortStr = " ('abLimited','inExRate') ";
					break;
				case 3:
					clsPortStr = " ('abLimited','inNetValue_china') ";
					break;
				case 4:
					clsPortStr = " ('abLimited','inNetValue_chinaM') ";
					break;
				case 5:
					clsPortStr = " ('abLimited','inNetValue_chinaL') ";
					break;
			}
			if(clsType == 3 || clsType == 4||clsType == 5){
				
				//edit by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
				sql = "select cfd.*, cls.FPortClsName, cls.FPortClsRank from " + pub.yssGetTableName("Tb_TA_ClassFundDegree") + " cfd " +
				" join (select FPortClsCode,FPortClsName,FPortClsRank from " + pub.yssGetTableName("Tb_TA_Portcls") + 
				" where FCheckState = 1 and fportclsschema in " + clsPortStr + " ) " + //modified by yeshenghong story4151 20130823
				" cls on cfd.FPortClsCode = cls.FPortClsCode where cfd.fcheckstate = 1 and " +
				" cfd.fportcode = " + dbl.sqlString(this.sPortCode) + " order by cls.FPortClsRank";
				rs = dbl.openResultSet(sql);
				while(rs.next()){
					//组合分级   科目名称   金额    等级    是否明细项  数量	
					//---edit by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
					if(clsType == 3||clsType == 5){
						key = rs.getString("fportclscode");
					}else{
						key = rs.getString("FPortClsName");
					}
					String showItem = "";
					if(clsType == 3){
						showItem = this.getPortClsData(this.sPortCode, key, "FShowItem", false);	//根据 分级组合获取显示项设置
					}else{
						showItem = this.getPortClsData(this.sPortCode, rs.getString("fportclscode"), "FShowItem", false);	//根据 分级组合获取显示项设置
					}
					//---edit by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 end---//
					
					if(showItem == null) //分级无设置显示项
						continue ;					
					
						//主标题
						buffer.append("\r\n");
						buffer.append("-\t");
						//---edit by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
						if(clsType == 3){
							buffer.append("组合分级"+key).append("\t");
						}else{
							buffer.append(key).append("\t");
						}
						//---edit by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
						buffer.append(" ").append("\t");
						buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t1\t1\t"); // 填满一行，否则显示会出问题
					
						//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
						if(clsType == 4){
							key = rs.getString("fportclscode");
						}
						//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 end---//
						
						String[] itemArray = showItem.split(",");
						//start add by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数
						CtlPubPara pubPara = new CtlPubPara();
					   	pubPara.setYssPub(this.pub);
					   	String digits = pubPara.getDigitsPortMethod("PubParaUnitCls","dayfinish","CtlPubParaUnitCls",
					   			"portClsSel","txtdigit",rs.getString("fportclscode"),"3");
					   	int digit = YssFun.toInt(digits);
					   	String format = this.getFormatString(digit);
					  //end add by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数
						
						if(this.arr_contain_str(itemArray, "01")){
							//资产净值					
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("资产净值").append("\t");
							buffer.append(
									YssFun.formatNumber(
											this.getSingleMuticlassNet(this.sPortCode, key, "01", 
													YssFun.toDate(strArray[0]), "FClassNetValue")
											/**shashijie 2012-11-5 BUG 6129 第三小问题,财务估值表整数也要保留2位小数*/
											,"#,###.00##")).append("\t");
											/**end shashijie 2012-11-5 BUG */
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						
						//add by  yeshenghong story4151   ---start
						if(this.arr_contain_str(itemArray, "38")){
							//资产净值					
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("资产净值(原币)").append("\t");
							buffer.append(
									YssFun.formatNumber(
											this.getSingleMuticlassNet(this.sPortCode, key, "38", 
													YssFun.toDate(strArray[0]), "FClassNetValue")
											,"#,###.00##")).append("\t");
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						//add by  yeshenghong story4151   ---end
						
						//add by fangjiang story 3264 2012.12.13
						if(this.arr_contain_str(itemArray, "011")){
							//资产净值					
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("资产净值(本币计价)").append("\t");
							buffer.append(
									YssFun.formatNumber(
											this.getSingleMuticlassNet(this.sPortCode, key, "011", 
													YssFun.toDate(strArray[0]), "FClassNetValue")
											,"#,###.00##")).append("\t");
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						//end by fangjiang story 3264 2012.12.13
						
						if(this.arr_contain_str(itemArray, "05")){
							//份额					
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("实收资本").append("\t");
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "05", 
												YssFun.toDate(strArray[0]), "FClassNetValue")
									/**shashijie 2012-11-5 BUG 6129 第三小问题,财务估值表整数也要保留2位小数*/
									,"#,###.00##")).append("\t");
									/**end shashijie 2012-11-5 BUG */
								buffer.append("\t \t");
								buffer.append(YssFun.formatNumber(
											this.getSingleMuticlassNet(this.sPortCode, key, "05", 
													YssFun.toDate(strArray[0]), "FNetValue")
										/**shashijie 2012-11-5 BUG 6129 第三小问题,财务估值表整数也要保留2位小数*/
										,"#,###.00##"));
										/**end shashijie 2012-11-5 BUG */
								buffer.append("\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						
						//add by fangjiang story 3264 2012.12.13
						if(this.arr_contain_str(itemArray, "03")){					
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("损益平准金未实现").append("\t");
							buffer.append(
									YssFun.formatNumber(
											this.getSingleMuticlassNet(this.sPortCode, key, "03", 
													YssFun.toDate(strArray[0]), "FClassNetValue")
											,"#,###.00##")).append("\t");
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "04")){				
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("损益平准金已实现").append("\t");
							buffer.append(
									YssFun.formatNumber(
											this.getSingleMuticlassNet(this.sPortCode, key, "04", 
													YssFun.toDate(strArray[0]), "FClassNetValue")
											,"#,###.00##")).append("\t");
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						//end by fangjiang story 3264 2012.12.13
						
						if(this.arr_contain_str(itemArray, "02")){
							//单位净值
							//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
							buffer.append("\r\n");
							buffer.append("\t");
							//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 end---//
							buffer.append("单位净值").append("\t");
							double tvalue = this.getSingleMuticlassNet(this.sPortCode, key, "02", 
									YssFun.toDate(strArray[0]), "FClassNetValue");
							//start modify by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数
							///**shashijie 2012-11-5 BUG 6129 第三小问题,财务估值表整数也要保留2位小数*/
							//buffer.append(YssFun.formatNumber(tvalue,"#,##0.00##")).append("\t");
							///**end shashijie 2012-11-5 BUG */
							buffer.append(YssFun.formatNumber(tvalue,format)).append("\t");
							//end modify by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题	
						}
						
						if(this.arr_contain_str(itemArray, "022")){
							//高精度单位净值
							//---add by yeshenghong 2013.05.22 STORY #3759 20130522 start---//
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("单位净值（高精度）").append("\t");
							double tvalue = this.getSingleMuticlassNet(this.sPortCode, key, "022", 
									YssFun.toDate(strArray[0]), "FClassNetValue");
							buffer.append(YssFun.formatNumber(tvalue,"#,##0.00000000000000##")).append("\t");
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
							//---add by yeshenghong 2013.05.22 STORY #3759 20130522 end---//
						}
						
						if(this.arr_contain_str(itemArray, "021")){
							//单位净值(本币计价)
							//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
							buffer.append("\r\n");
							buffer.append("\t");
							//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 end---//
							buffer.append("单位净值(本币计价)").append("\t");						
							buffer.append(YssFun.formatNumber(
									this.getSingleMuticlassNet(this.sPortCode, key, "021", 
											YssFun.toDate(strArray[0]), "FClassNetValue")
							//start modify by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数
									///**shashijie 2012-11-5 BUG 6129 第三小问题,财务估值表整数也要保留2位小数*/
									//,"#,##0.00##")).append("\t");										
									///**end shashijie 2012-11-5 BUG */
									,format)).append("\t");	
							//end modify by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						
						if(this.arr_contain_str(itemArray, "08")){
							//累计单位净值
							//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
							buffer.append("\r\n");
							buffer.append("\t");
							//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 end---//
							buffer.append("累计单位净值").append("\t");						
							buffer.append(YssFun.formatNumber(
									this.getSingleMuticlassNet(this.sPortCode, key, "08", 
											YssFun.toDate(strArray[0]), "FClassNetValue")
							//start modify by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数				
									/**shashijie 2012-11-5 BUG 6129 第三小问题,财务估值表整数也要保留2位小数*/
									//,"#,##0.00##")).append("\t");
									/**end shashijie 2012-11-5 */
									,format)).append("\t");	
							//end modify by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数	
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						
						//add by fangjiang story 3264 2012.12.13
						if(this.arr_contain_str(itemArray, "081")){				
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("累计单位净值（本币计价）").append("\t");
							buffer.append(
									YssFun.formatNumber(
											this.getSingleMuticlassNet(this.sPortCode, key, "081", 
													YssFun.toDate(strArray[0]), "FClassNetValue")
							//start modify by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数
											//,"#,###.00##")).append("\t");
											,format)).append("\t");	
							//end modify by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						//end by fangjiang story 3264 2012.12.13
						
						if(this.arr_contain_str(itemArray, "09")){
							//日净值增长率
							//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
							buffer.append("\r\n");
							buffer.append("\t");
							//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 end---//
							buffer.append("日净值增长率").append("\t");						
							buffer.append(YssFun.formatNumber(
									YssD.mul(
										this.getSingleMuticlassNet(this.sPortCode, key, "09", YssFun.toDate(strArray[0]), "FClassNetValue"),
										100
									)
									,"#.00")+ "%").append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						
						//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 start---//
						if(this.arr_contain_str(itemArray, "22")){
							//今日单位净值
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("今日单位净值").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "22", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.00##")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "24")){
							//本期收益_已实现
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("本期收益_已实现").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "24", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.00##")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "25")){
							//本期收益_未实现
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("本期收益_未实现").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "25", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.00##")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "26")){
							//利润分配_未分配利润_未实现
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("利润分配_未分配利润_未实现").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "26", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.00##")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "27")){
							//可分配收益
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("可分配收益").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "27", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.00##")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "28")){
							//单位可分配收益
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("单位可分配收益").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "28", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.0000")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "29")){
							//基金本日收益
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("基金本日收益").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "29", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.00##")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "30")){
							//期初单位净值
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("期初单位净值").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "30", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.00##")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "31")){
							//昨日单位净值
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("昨日单位净值").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "31", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.00##")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "32")){
							//累计派现金额
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("累计派现金额").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "32", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.00##")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "10")){
							//本期净值增长率
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("本期净值增长率").append("\t");						
							buffer.append(YssFun.formatNumber(
									YssD.mul(
										this.getSingleMuticlassNet(this.sPortCode, key, "10", YssFun.toDate(strArray[0]), "FClassNetValue"),
										100
									)
									,"#.00")+ "%").append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "11")){
							//累计净值增长率
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("累计净值增长率").append("\t");						
							buffer.append(YssFun.formatNumber(
									YssD.mul(
										this.getSingleMuticlassNet(this.sPortCode, key, "11", YssFun.toDate(strArray[0]), "FClassNetValue"),
										100
									)
									,"#.00")+ "%").append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						if(this.arr_contain_str(itemArray, "35")){
							//实现收益
							buffer.append("\r\n");
							buffer.append("\t");
							buffer.append("实现收益").append("\t");						
							buffer.append(YssFun.formatNumber(
										this.getSingleMuticlassNet(this.sPortCode, key, "35", YssFun.toDate(strArray[0]), "FClassNetValue")
										,"#,##0.00##")).append("\t");										
							buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						}
						//---add by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002 end---//
					}
				dbl.closeResultSetFinal(rs);
			} else {
				//add by zhouwei 20120229 组合分级的显示 start------------
				Map rootMap=new HashMap();//筛选主标题
				Map clsNetValue=new HashMap();//组合分级----资产净值 费后			
				//计算各个Class的资产净值 费前
				sql = " select Round(cfd.FClassNetValue,2) as FClassNetValue,cfd.fcurycode as clsPortCode,cfd.Ftype from "+pub.yssGetTableName("tb_data_multiclassnet")
					+ " cfd join (select FPortClsCode,FPortClsName,FPortClsRank from " + pub.yssGetTableName("Tb_TA_Portcls") 
				    + " where FCheckState = 1 and fportclsschema in " + clsPortStr + " ) "  //modified by yeshenghong story4151 20130823
				    + " cls on cfd.fcurycode = cls.FPortClsCode where  cfd.Ftype in ('01','08') and cfd.FNAVDate ="+dbl.sqlDate(strArray[0])
				    + " and cfd.FPORTCODE="+dbl.sqlString(portCodeId);
				rs=dbl.openResultSet(sql);
				while(rs.next()){
					clsNetValue.put(rs.getString("clsPortCode").trim()+"\t"+rs.getString("Ftype").trim(), rs.getDouble("FClassNetValue"));	
				}
				dbl.closeResultSetFinal(rs);
				CtlPubPara pubPara=new CtlPubPara();
				pubPara.setYssPub(pub);
				String[] digitsMethod=pubPara.getDigitsCalMethod(portCodeId).split("\t");
				for(int i=0;i<classList.size();i++){
					//组合分级   科目名称   金额    等级    是否明细项  数量
					String[] values=((String)classList.get(i)).split("\t");
					//edit by songjie 2012.11.26 STORY #3264需求北京-[建设银行]QDIIV4.0[紧急]20121108002
					key=values[0];
					if(!rootMap.containsKey(key)){//主标题
						buffer.append("\r\n");
						buffer.append("-\t");
						buffer.append("组合分级"+key).append("\t");
						buffer.append(" ").append("\t");
						buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t1\t1\t"); // 填满一行，否则显示会出问题
						rootMap.put(key, key);
						buffer.append("\r\n");
						buffer.append("\t");
						buffer.append("资产净值").append("\t");
						buffer.append(YssFun.formatNumber(clsNetValue.get(key+"\t01")==null?new Double(0):((Double)clsNetValue.get(key+"\t01")),"#,###.####")).append("\t");
						buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
					}
					if(values[1].equals("权益合计")){
						buffer.append("\r\n");
						buffer.append("\t");
						buffer.append(values[1]).append("\t");
						buffer.append(
								YssFun.formatNumber(YssD.round(YssFun.toDouble(values[2]),2),"#,###.####")).append("\t");
						buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
					}	
					if(values[1].equals("实收资本")){//份额
						buffer.append("\r\n");
						buffer.append("\t");
						buffer.append("份额").append("\t");
						buffer.append(
								YssFun.formatNumber(YssD.round(YssFun.toDouble(values[5]),4),"#,###.####")).append("\t");
						buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
						buffer.append("\r\n");
						buffer.append("\t");
						buffer.append("单位净值").append("\t");
						double tvalue=clsNetValue.get(key+"\t01")==null?new Double(0):((Double)clsNetValue.get(key+"\t01"));
						
					    CtlPubPara ctlPara = new CtlPubPara();
						ctlPara.setYssPub(pub);
						String sFormat = "";
						int iDigit = Integer.parseInt(ctlPara.getCashUnit(getPortCode(
									strArray[1], YssFun.left(strArray[0], 4))));
						if (iDigit > 0) {
							for (int j = 0; j < iDigit; j++) {
								sFormat += "0";
							}
							sFormat = "#0." + sFormat;
						} else {
							sFormat = "#.0000"; // 默认保留4位
						}//modified by yeshenghong 20121203 bug6459
						
						if(digitsMethod[0].equals("-1")){//截位
							buffer.append(YssFun.formatNumber(YssD.round(YssD.div(tvalue,YssFun.toDouble(values[5])),iDigit,true),sFormat)).append("\t");
						}else if(digitsMethod[0].equals("0")){//四舍五入
							buffer.append(YssFun.formatNumber(YssD.round(YssD.div(tvalue,YssFun.toDouble(values[5])),iDigit,false),sFormat)).append("\t");
						}else{//进位
							BigDecimal b = new BigDecimal(Double.toString(YssD.div(tvalue,YssFun.toDouble(values[5]))));
						    BigDecimal one = new BigDecimal("1");
//							buffer.append(YssFun.formatNumber(YssD.round(YssD.div(tvalue,YssFun.toDouble(values[5])),iDigit),sFormat)).append("\t");;
						    buffer.append(
						    		 YssFun.formatNumber(b.divide(one, iDigit,BigDecimal.ROUND_UP).doubleValue(),sFormat)).append("\t");
						}					
						buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题	
						
						//add by jsc 20120628 
						buffer.append("\r\n");
						buffer.append("\t");
						buffer.append("累计单位净值").append("\t");
						double dLjvalue=clsNetValue.get(key+"\t08")==null?new Double(0):((Double)clsNetValue.get(key+"\t08"));
						if(digitsMethod[0].equals("-1")){//截位
							buffer.append(YssFun.formatNumber(YssD.round(dLjvalue,Integer.parseInt(digitsMethod[1]),true),"#,###.####")).append("\t");
						}else if(digitsMethod[0].equals("0")){//四舍五入
							buffer.append(YssFun.formatNumber(YssD.round(dLjvalue,Integer.parseInt(digitsMethod[1]),false),"#,###.####")).append("\t");
						}else{//进位
							 BigDecimal b1 = new BigDecimal(dLjvalue);
						     BigDecimal one1 = new BigDecimal("1");
						     buffer.append(YssFun.formatNumber(b1.divide(one1, Integer.parseInt(digitsMethod[1]),BigDecimal.ROUND_UP).doubleValue(),"#,###.####")).append("\t");
						}					
						buffer.append("\t \t\t\t\t \t \t\t\t\t \t \t\t \t \t \t2\t2\t"); // 填满一行，否则显示会出问题
					}				
				}
			}
			//---end---
			
			//add by zhouwei 20120229 组合分级的显示 end------------
			// 下面为添加日终标记
			sql = "select * from " + pub.yssGetTableName(sGVTableName)
					+ " where FPortCode='" + YssFun.toInt(strArray[1])
					+ "' and FDate=" + dbl.sqlDate(YssFun.toDate(strArray[0]))
					+ " and FACCTCODE='C100'";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				sType = rs.getString("FAcctLevel");
			}
			buffer.append("\f\f").append(sType);
			rs.getStatement().close();
		} catch (SQLException sqle) {
			throw new YssException("获取财务估值表出错！");
		} catch (Exception e) {
//			throw new YssException("获取财务估值表出错！");
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs); // 添加　rs关闭　QDV4海富通2009年05月11日01_AB
											// MS00439 by leeyu 20090515
		}
		return buffer.toString();
	}
	/**
	 * add by huangqirong 2013-06-18 bug #8330 嘉实多币种
	 * 生成保留位数格式
	 * */
	private String getFormatString(int iDigit){
		String sFormat = ""; // 默认保留3位
		if (iDigit > 0) {
			for (int i = 0; i < iDigit; i++) {
				sFormat += "0";
			}
			sFormat = "#0." + sFormat;
		}else{
			sFormat = "#.000"; // 默认保留3位
		}
		return sFormat + "##";
	}

	/**
	 * 获取累计分红数据 author fanghaoln edittime:20090904
	 * 
	 * @return double 返回累计分红数据
	 * @throws YssException
	 *             MS00651 QDV4华夏2009年8月24日02_A
	 */
	private double getAccummulateDivided(String endDate, String sPortID)
			throws YssException {
		double accummulate = 0.0;
		String sqlStr = null;
		String portCode = "";
		ResultSet rs = null;
		try {
			portCode = this.getPortCode(sPortID, YssFun.left(endDate, 4)); // 调用方法得到组合代码
			sqlStr = "select sum(FSellPrice) as FAccumulateDivided from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 and FConfimDate <="
					+ dbl.sqlDate(YssFun.toDate(endDate))
					+ " and FSellType = '03'" + " and FPortCode = "
					+ dbl.sqlString(portCode); // 获取累计分红价格，数据为小于等于当前日期
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				accummulate = rs.getDouble("FAccumulateDivided"); // 获取汇总的值。
			}
		} catch (Exception e) {
			throw new YssException("获取累计分红数据出现异常！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return accummulate; // 返回累计分红
	}

	/**
	 * 日净值增长率 author fanghaoln edittime:20090904
	 * 
	 * @return double 返回累计分红数据
	 * @throws YssException
	 *             MS00651 QDV4华夏2009年8月24日02_A
	 */
	private double getDateAmountRate(String endDate, String sPortID)
			throws YssException {
		double accummulate = 0.0;
		double todayRate = 0.0;
		double yesdayRate = 0.0;
		String sqlStr = null;
		String portCode = "";
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		try {
			portCode = this.getPortCode(sPortID, YssFun.left(endDate, 4)); // 调用方法得到组合代码
			// 得到当天的累计分红
			sqlStr = "select sum(FSellPrice) as FAccumulateDivided from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 and FConfimDate ="
					+ dbl.sqlDate(YssFun.toDate(endDate))
					+ " and FSellType = '03'" + " and FPortCode = "
					+ dbl.sqlString(portCode); // 获取累计分红价格，数据为小于等于当前日期
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				accummulate = rs.getDouble("FAccumulateDivided"); // 获取汇总的值。
			}
			dbl.closeResultSetFinal(rs);
			// 得到今天的单位净值
			sqlStr = "select  fstandardmoneymarketvalue from "
					+ pub.yssGetTableName("tb_rep_guessvalue")
					+ " where facctcode='9600' and fdate ="
					+ dbl.sqlDate(YssFun.toDate(endDate)) + " and FPortCode = "
					+ dbl.sqlString(sPortID); // 得到今天的单位净值
			rs1 = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if (rs1.next()) {
				todayRate = rs1.getDouble("fstandardmoneymarketvalue"); // 获取汇总的值。
			}
			dbl.closeResultSetFinal(rs1);
			// 得到昨天的单位净值
			sqlStr = "select  fstandardmoneymarketvalue from "
					+ pub.yssGetTableName("tb_rep_guessvalue")
					+ " where facctcode='9600' and fdate ="
					+ dbl.sqlDate(YssFun.addDay(YssFun.toDate(endDate), -1))
					+ " and FPortCode = " + dbl.sqlString(sPortID); // 得到昨天的单位净值
			rs2 = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if (rs2.next()) {
				yesdayRate = rs2.getDouble("fstandardmoneymarketvalue"); // 获取汇总的值。
			}
			dbl.closeResultSetFinal(rs2);
			if (yesdayRate != 0) {
				accummulate = (todayRate - yesdayRate + accummulate)
						/ (yesdayRate - accummulate);
			}
		} catch (Exception e) {
			throw new YssException("获取累计分红数据出现异常！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return YssD.round(accummulate, 4);
	}

	// 获取本期基金净值增长率
	private double getAmountRate(String endDate, String sPortID)
			throws YssException {
		String sql = "";
		String portCode = "";
		String startDate = "";
		ResultSet rs = null;
		ResultSet tmpRs = null;
		MainFinanceIndex mfi = new MainFinanceIndex(); // 主要财务指标实例
		double dReturn = 0; // 本期基金净值增长率
		int year = 0;
		try {
			year = YssFun.getYear(YssFun.toDate(endDate));
//			portCode = this.getPortCode(sPortID, YssFun.left(endDate, 4));
			portCode = this.getPortCodeByAssetID(sPortID, YssFun.left(endDate, 4));
			//20110808 modified by liubo.Bug 2334
        	//若为期内成立基金，则期初单位基金资产净值为基金成立日的单位净值，其中基金成立日指该组合TA交易数据中销售类型为“00”（基金成立）对应的交易日期。
        	//**********************************
			sql = "select FTradeDate from "
					+ pub.yssGetTableName("Tb_TA_Trade")
					+ " where FPortCode = " + dbl.sqlString(portCode) 
					+ " and FSellType = '00'" 
					+ " and FCheckState = '1'";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				if (YssFun.getYear(rs.getDate("FTradeDate")) == year) { // 如果基金是今年成立的则取成立日期为期初日期
					
					//modified by liubo.Bug #3977
					//若基金是今年成立的则取成立日期为期初日期
					//=========================
//					startDate = YssFun.formatDate(YssFun.addDay(rs
//							.getDate("FTradeDate"), 1)); 
					startDate = YssFun.formatDate(rs.getDate("FTradeDate")); 
					//===========end==============
			//****************end*****************
				} else { // 这里传入的期初日期其实就是要取数据的日期，故要加上一天带入计算,
							// 如果基金不是今年成立的则取去年的最后一天的日期为期初日期
					sql = "select max(fdate) as fdate from "
							+ pub.yssGetTableName("tb_rep_guessvalue")
							+ " where " + dbl.sqlYear("fdate") + " = "
							+ (year - 1);
					dbl.closeResultSetFinal(tmpRs);
					tmpRs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
					while (tmpRs.next()) {
						//modified by liubo.Bug #3977
						//若基金不是今年成立的则取去年的最后一天的净值
						//==================================
						startDate = YssFun.formatDate(tmpRs.getDate("FDate"));
//						startDate = YssFun.formatDate(YssFun.addDay(tmpRs
//								.getDate("FDate"), 1)); 
						//===================end===============
					} 
				}
				mfi.setYssPub(pub); // 初始化
				mfi.setStartDate(startDate); // 传参 期初日期
				mfi.setEndDate(endDate); // 传参 期末日期
				mfi.setPortCode(portCode); // 传参 组合代码
				mfi.setPortID(sPortID); //20110601 modified by liubo  STORY #936  传参财务估值表中使用的套账代码
				dReturn = mfi.getAmountRate(); // //20110601 modified by liubo  STORY #936  调用计算方法，计算本期基金净值增长率
			}
		} catch (Exception e) {
			throw new YssException("获取本期基金净值增长率出错！" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(tmpRs);
			dbl.closeResultSetFinal(rs);
		}
		return YssD.round(dReturn, 4); // 返回本期基金净值增长率的值，取4位小数
	}

	/**
	 * 累计净值增长率 author fanghaoln edittime:20090904
	 * 
	 * @return double 返回累计分红数据
	 * @throws YssException
	 *             MS00651 QDV4华夏2009年8月24日02_A
	 */
	private double getAllAmountRate(String endDate, String sPortID)
			throws YssException {
		ArrayList accummulate = new ArrayList();
		ArrayList yesdayRate = new ArrayList();
		int i = 0;
		String sqlStr = null;
		String portCode = "";
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		java.util.Date RateDate;
		double firstRate = 1.0; // 保存第一次计算
		double centerRate = 1.0; // 保存从第二次到第倒数第二次
		double lastRate = 1.0; // 保存最后一次计算
		double lastDateRate = 0.0; // 期末单位基金资产净值
		try {
			portCode = this.getPortCode(sPortID, YssFun.left(endDate, 4)); // 调用方法得到组合代码
			// 期末单位基金资产净值
			sqlStr = "select  fstandardmoneymarketvalue from "
					+ pub.yssGetTableName("tb_rep_guessvalue")
					+ " where facctcode='9600' and fdate ="
					+ dbl.sqlDate(YssFun.toDate(endDate)) + " and FPortCode = "
					+ dbl.sqlString(sPortID); // 期末单位基金资产净值
			rs1 = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while (rs1.next()) {
				lastDateRate = rs1.getDouble("fstandardmoneymarketvalue"); // 期末单位基金资产净值
			}
			dbl.closeResultSetFinal(rs1);
			// ========================================================================================================================
			// 得到当天的累计分红
			sqlStr = "select FSellPrice as FAccumulateDivided,FConfimDate from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 and FConfimDate <="
					+ dbl.sqlDate(YssFun.toDate(endDate))
					+ " and FSellType = '03' "
					+ " and FPortCode = "
					+ dbl.sqlString(portCode) + " order by FConfimDate"; // 获取累计分红价格，数据为小于等于当前日期
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				accummulate.add(new Double(rs.getDouble("FAccumulateDivided"))); // 获取第N次分红的金额。
				RateDate = rs.getDate("FConfimDate");

				// 得到分红前的的单位净值
				sqlStr = "select  fstandardmoneymarketvalue from "
						+ pub.yssGetTableName("tb_rep_guessvalue")
						+ " where facctcode='9600' and fdate ="
						+ dbl.sqlDate(YssFun.addDay(RateDate, -1))
						+ " and FPortCode = " + dbl.sqlString(sPortID); // 获取单位净值
				rs2 = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
				if (rs2.next()) {
					yesdayRate.add(new Double(rs2
							.getDouble("fstandardmoneymarketvalue"))); // 获取分红前的的单位净值
					i++;//add huangqirong 2013-04-24 财务估值表没有分红数据， 生成财务估值表数据出错
				}
				dbl.closeResultSetFinal(rs2);
				//i++; //modify huangqirong 2013-04-24 财务估值表没有分红数据， 生成财务估值表数据出错
				// ===================================================================================
			}
			dbl.closeResultSetFinal(rs);
			if (i >= 1) {
				firstRate = ((Double) yesdayRate.get(0)).doubleValue(); // 保存第一次计算
				for (int j = 1; j < i; j++) {
					//--- edit by songjie 2013.03.18 判断 arraylist 所包含对象的个数  start---//
					if(yesdayRate.size() > 1){
						centerRate = ((Double) yesdayRate.get(j)).doubleValue()
								/ (((Double) yesdayRate.get(j - 1)).doubleValue() - ((Double) accummulate
										.get(j - 1)).doubleValue()) * centerRate; // 从第二次到最后一次的前一次
					}
					//--- edit by songjie 2013.03.18 判断 arraylist 所包含对象的个数  end---//
				}
				//--- edit by songjie 2013.03.18 判断 arraylist 所包含对象的个数  start---//
				if(yesdayRate.size() > 1){
					lastRate = lastDateRate
							/ (((Double) yesdayRate.get(i - 1)).doubleValue() - ((Double) accummulate
									.get(i - 1)).doubleValue()); // 保存最后一次计算
				}
				//--- edit by songjie 2013.03.18 判断 arraylist 所包含对象的个数  end---//
				//20111202 modified by liubo.Bug #3177
				//=======================================
				firstRate = YssD.round(firstRate,2) * YssD.round(centerRate,2) * YssD.round(lastRate,2) - 1; // 把第一到最后乘起来就得到了累计净值增长率

				//================end=======================
			} else {
				firstRate = lastDateRate - 1; // 没有分红计处累计净值增长率
			}
		} catch (Exception e) {
			throw new YssException("获取累计分红数据出现异常！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return YssD.round(firstRate, 4);
	}

	// ------插入日终处理部分
	// edit by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能
	public String dayConfirm(String parameter) throws YssException {
		String strArray[];
		// String sql = null;
		// ResultSet rs = null;
		java.util.Date date = null;
		java.util.Date dEndDate = null;
		int fSetCode = 0;
		String strCurrency = "";
		// boolean isCommit = false;
		strArray = parameter.split("\t", -1);
		
		String sDate = strArray[0];
		fSetCode = YssFun.toInt(strArray[1]);
		strCurrency = strArray[2];
		iDayRealy = YssFun.toInt(strArray[3]);
		String[] reqDate = sDate.split("~n~");// 将日期分隔开。有可能前台传来的是日期区间
		date = YssFun.toDate(reqDate[0]);// 这一个日期是一定有的
		java.util.Date dBeginDate = date;// 这个日期保存起来，回传给前台的报表数据，是此日期的数据
		if (reqDate != null && reqDate.length > 1) {// 需要判断有没有第二个日期
			dEndDate = YssFun.toDate(reqDate[1]);
		}
		if (dEndDate != null) {// 此时需要对这个区间的所有日期进行反确认财务估值表
			while (dEndDate.after(date)) {// 循环取日期段内每一天，不包括最后一天。最后一天会在后面进行处理
				this.doConfirm(date, fSetCode, iDayRealy);
				date = YssFun.addDay(date, 1);// 起始日期加1天，这样才能循环取到日期区间内的每一天
			}
		}
		this.doConfirm(date, fSetCode, iDayRealy);// 1.当前台只传一个日期时，前面的方法就没有执行，只执行这里，对该日期进行操作；2.传两个日期时，前面执行了，但没有对最后一天进行处理，此处对最后一天进行操作。
		return getGuessValueReport(YssFun.formatDate(dBeginDate) + "\t"
				+ fSetCode + "\t" + strCurrency + "\t" + iDayRealy + "\tnull");// 根据界面上第一个日期，来生成报表数据
		/*
		 * try { dbl.loadConnection().setAutoCommit(false); strArray =
		 * parameter.split("\t", -1);
		 * setAccountingPeriod(YssFun.toDate(strArray[0]),
		 * YssFun.toInt(strArray[1])); date = YssFun.toDate(strArray[0]);
		 * fSetCode = YssFun.toInt(strArray[1]); iDayRealy =
		 * YssFun.toInt(strArray[3]); sql = "select * from " +
		 * pub.yssGetTableName("tb_rep_guessvalue") + " where FPortCode='" +
		 * fSetCode + "' and FDate=" + dbl.sqlDate(date); rs =
		 * dbl.openResultSet(sql); if (rs.next() == false) { return
		 * getGuessValueReport(parameter); } sql = "delete from " +
		 * pub.yssGetTableName("tb_rep_guessvalue") + " where FPortCode='" +
		 * fSetCode + "' and FDate=" + dbl.sqlDate(date) +
		 * " and FACCTCODE='C100'"; dbl.executeSql(sql); sql = "insert into " +
		 * pub.yssGetTableName("tb_rep_guessvalue") +
		 * " (FPORTCODE,FDATE,FACCTCODE,FCURCODE,FACCTNAME,FACCTATTR,FACCTCLASS,FEXCHANGERATE,FAMOUNT,FCOST,FSTANDARDMONEYCOST,FCOSTTONETRATIO,FSTANDARDMONEYCOSTTONETRATIO,"
		 * +
		 * " FMARKETPRICE,FOTPRICE1,FOTPRICE2,FOTPRICE3,FMARKETVALUE,FSTANDARDMONEYMARKETVALUE,FMARKETVALUETORATIO,FSTANDARDMONEYMARKETVALUETORAT,FAPPRECIATION,"
		 * +
		 * " FSTANDARDMONEYAPPRECIATION,FMARKETDESCRIBE,FACCTLEVEL,FACCTDETAIL,FDESC ) values("
		 * + dbl.sqlString(fSetCode + "") + "," + dbl.sqlDate(date) + "," +
		 * "'C100',' ','C100',' ',' ',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,' '," +
		 * iDayRealy + ",0," + dbl.sqlString(pub.getUserCode() + (iDayRealy == 1
		 * ? " 日终确认日期:" : " 日终反确认日期:") + YssFun.formatDate(new
		 * java.util.Date())) + ")"; dbl.executeSql(sql);
		 * dbl.loadConnection().commit(); isCommit = true; rs.close(); } catch
		 * (Exception e) { throw new YssException("日终确认出错！", e); } finally { if
		 * (!isCommit) { try { dbl.loadConnection().rollback(); } catch
		 * (YssException ex) { } catch (SQLException ex) { } } } return
		 * getGuessValueReport(parameter);
		 */
	}
	
	/**
	 * add by huangqirong 2013-16-03 Story #3871 
	 * 调用 场内交易数据接口 WebService
	 * */
	private void doChinaWS(int setCode , String date){
		ResultSet rset =null ;
		String portCode = "";
		String sql1 = "select tt.fportcode from " + pub.yssGetTableName("tb_Para_Portfolio")+" tt where tt.fassetcode " +
		"in( select distinct(fsetid) from lsetlist t where t.fsetcode='" + setCode + "')";
        try {
	         rset = dbl.openResultSet(sql1);
	         while(rset.next()){
		         portCode = rset.getString("fportcode");
	         }
        } catch (Exception e) {
	        e.printStackTrace();
        }
		//** start dongqingsong 建行清算联动 story #3871 (11)
		//**根据通用参数判断，如果该组合群数据来源是webservice，执行以下请求，否则不做任何处理，其余的操作不变
		boolean flag = false;
		flag = this.getExchangeDate(portCode);
		if(flag && portCode != null && portCode.trim().length() > 0){
			ExchangeDate ex = new ExchangeDate();
			ex.setPub(this.pub);
			ex.setConnection(this.pub.getDbLink().getConnection());
			ex.getReqRecord(portCode, date , this.pub.getPrefixTB());
		}
		//	** end dongqingsong 建行清算联动 story #3871 (11)
	}

	/**
	 * 在确认和反确认净值表时，最终调用此方法时行处理。将原来dayConfirm方法的核心提取出来了，以便进行按日期区间来反确认
	 * 
	 * @param date
	 *            ,表明是对这个指定日期进行相应操作：确认或者反确认
	 * @param fSetCode
	 *            ，这个代码对应的是组合代码，在财务估值表中是1,2,3……等整数，对应如001,002……之类的数据
	 * @param iDayRealy
	 *            ，此参数指明所要进行的操作。0，反确认；1，确认
	 * @return
	 * @throws YssException
	 */
	// //====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能
	public void doConfirm(java.util.Date date, int fSetCode, int iDayRealy)
			throws YssException {
		String sql = null;
		ResultSet rs = null;
		boolean isCommit = false;
		try {
			/**start add by huangqirong 2013-06-03 Story #3871 调用 场内交易数据接口 WebService*/
			if(iDayRealy == 1)
			    this.doChinaWS(fSetCode, YssFun.formatDate(date, "yyyy-MM-dd")); 
			/**end add by huangqirong 2013-06-03 Story #3871 调用 场内交易数据接口 WebService*/
			dbl.loadConnection().setAutoCommit(false);
			setAccountingPeriod(date, fSetCode);
			sql = "select * from " + pub.yssGetTableName("tb_rep_guessvalue")
					+ " where FPortCode='" + fSetCode + "' and FDate="
					+ dbl.sqlDate(date);
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			if (!rs.next()) {
				return;
			}
			// fanghaoln 20100324 MS01026 QDV4赢时胜(测试)2010年03月16日01_B
			rs.close();// 关闭结果集
			rs.getStatement().close();// 关闭Statement
			// -------------------end--MS01026------------------------------------
			sql = "delete from " + pub.yssGetTableName("tb_rep_guessvalue")
					+ " where FPortCode='" + fSetCode + "' and FDate="
					+ dbl.sqlDate(date) + " and FACCTCODE='C100'";
			dbl.executeSql(sql);
			sql = "insert into "
					+ pub.yssGetTableName("tb_rep_guessvalue")
					+ " (FPORTCODE,FDATE,FACCTCODE,FCURCODE,FACCTNAME,FACCTATTR,FACCTCLASS,FEXCHANGERATE,FAMOUNT,FCOST,FSTANDARDMONEYCOST,FCOSTTONETRATIO,FSTANDARDMONEYCOSTTONETRATIO,"
					+ " FMARKETPRICE,FOTPRICE1,FOTPRICE2,FOTPRICE3,FMARKETVALUE,FSTANDARDMONEYMARKETVALUE,FMARKETVALUETORATIO,FSTANDARDMONEYMARKETVALUETORAT,FAPPRECIATION,"
					+ " FSTANDARDMONEYAPPRECIATION,FMARKETDESCRIBE,FACCTLEVEL,FACCTDETAIL,FDESC ) values("
					+ dbl.sqlString(fSetCode + "")
					+ ","
					+ dbl.sqlDate(date)
					+ ","
					+ "'C100',' ','C100',' ',' ',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,' ',"
					+ iDayRealy
					+ ",0,"
					+ dbl.sqlString(pub.getUserCode()
							+ (iDayRealy == 1 ? " 日终确认日期:" : " 日终反确认日期:")
							+ YssFun.formatDate(new java.util.Date())) + ")";
			dbl.executeSql(sql);
			
			

			
			dbl.loadConnection().commit();
			isCommit = true;
			
			//20120416 added by liubo.Story #2439
			//在每天确认\反确认估值表时，想托管系统发出接口请求。这个步骤无论成功与否，不会影响QDII估值系统的操作
			//===================================
			Logger log =null;
			log = Logger.getLogger("stdout");
			com.yss.ciss.ws.client.ciss.ResponseMsg respon = null;
			YssFinance yfiance = new YssFinance();
			PortfolioBean port = new PortfolioBean();
			try
			{
				yfiance.setYssPub(pub);
	            port.setYssPub(pub);
				port.setPortCode(yfiance.getPortCode(String.valueOf(fSetCode)));
	            port.getSetting();
	            
				CISSServiceForQDII ciss = new CISSServiceForQDII();
				ComparableStatusVO statusVO = new ComparableStatusVO();
				statusVO.setExecName(pub.getUserCode());
				statusVO.setMsgDate(YssFun.formatDate(date,"yyyyMMdd") + "-" + YssFun.formatDate(date,"yyyyMMdd"));
				statusVO.setProCode(port.getAssetCode());
				statusVO.setMsgType(iDayRealy == 1 ? "GZBQR" : "GZBUNQR");
				
				List<ComparableStatusVO> list = new ArrayList<ComparableStatusVO>();
				list.add(statusVO);
				
				respon = ciss.getCISSService().sendComparableStatus(list);
				log.info(respon.getStatus() + (respon.getStatus().trim().equals("0") ? respon.getRemark() : ""));
				System.out.println(respon.getStatus() + (respon.getStatus().trim().equals("0") ? respon.getRemark() : ""));
			}
			catch(Exception ye)
			{
				System.out.println("向托管系统提交“确认\\反确认估值表”请求失败：" + ye.getMessage());
				log.error("向托管系统提交“确认\\反确认估值表”请求失败：" + ye.getMessage());
			}
			//=================end==================
			
		} catch (Exception e) {
			throw new YssException("日终确认出错！", e);
		} finally {
			if (!isCommit) {
				try {
					dbl.loadConnection().rollback();
				} catch (YssException ex) {
				} catch (SQLException ex) {
				}
			}
			//DELETE BY JAINGSHICHAO BUG 2413 42sp6中发现一个与财务估值表确认和反确认有关的BUG 2011.08.10
			//BUG 说明： 同一个服务器连接不同数据库的情况。做了财务估值表确认和反确认后，数据库连接发现串了。
			//这里不能关闭连接。否则造成连接错乱的问题
//			// ---- QDV4国泰基金2011年1月18日01_B-------
//			try {
//				// 关闭游标
//				dbl.closeResultSetFinal(rs);
//				dbl.loadConnection().close();
//			} catch (SQLException e) {
//				// throw new YssException("数据库链如此关闭出错！", e);
//			}
//			// ---add by lidaolong 2011.1.21-----
			
			//add by jiangshichao 2011.08.10 BUG 2413
			dbl.closeResultSetFinal(rs);
		}
	}

	java.util.Date dDate = null;
	int iSetCode = 0;
	public String buildGuessValueReport(String parameter) throws YssException {
		String strArray[];
		String sql = "";
		ResultSet rs = null;
		YssFinance finance = null;
		//---add by songjie 2012.09.21 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
  	    Date logStartTime = null;//业务子项开始时间
  	    if(logOper == null){//添加非空判断
  	    	logOper = SingleLogOper.getInstance();
  	    }
        Date tradeDate = null;//业务日期
        String setCode = "";//套帐代码
        DayFinishLogBean df = new DayFinishLogBean();
        boolean isError = false;//判断是否为异常日志
        boolean comeFromDD = false;//判断是否通过调度方案调用
        //---add by songjie 2012.09.21 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		try {
			//---add by songjie 2012.09.21 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			if(logSumCode.length() == 0){
				df.setYssPub(pub);
				logSumCode = df.getLogSumCodes();
			}else{
				comeFromDD = true;
			}
			this.setFunName("GuessValue");
        	logStartTime = new Date();//开始时间
        	strArray = parameter.split("\t", -1);
        	tradeDate = YssFun.toDate(strArray[0]);
        	setCode = YssFun.formatNumber(YssFun.toInt(strArray[1]), "000");
			//---add by songjie 2012.09.21 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	
			finance = new YssFinance();
			finance.setYssPub(pub);
			
			sPortCode = finance.getPortCode(strArray[1]);
			setAccountingPeriod(YssFun.toDate(strArray[0]), YssFun
					.toInt(strArray[1]));
			dbl.loadConnection().setAutoCommit(false);
			
			sql = "select * from " + pub.yssGetTableName("tb_rep_guessvalue")
					+ " where fdate=" + dbl.sqlDate(YssFun.toDate(strArray[0]))
					+ " and FPortCode = '" + YssFun.toInt(strArray[1]) + "'"
					+ " and FAcctCode='C100'";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				iDayRealy = rs.getInt("FAcctLevel");
			}
			rs.getStatement().close();
			if (iDayRealy == 1) {
				throw new YssException("当日估值表已确认");
			}
			// return; //如果日终已确认就不插入数据到表 by liyu 1225

			if (strArray.length >= 4) {
				// 判断是否生成ETF台帐，设置变量 MS00004 QDV4.1赢时胜（上海）2009年9月28日03_A
				if (strArray[3].equalsIgnoreCase("ETF")) {
					bIsETFGV = true;
					sGVTableName = "TB_ETF_GuessValue";
				}
			}
			if (strArray.length >= 6)
			{
				sIsIncludeModifyingData = strArray[5];
			}
			
			dDate = YssFun.toDate(strArray[0]);
			iSetCode = YssFun.toInt(strArray[1]);
			createGuessValue(YssFun.toDate(strArray[0]), YssFun
					.toInt(strArray[1]));

			//20121029 added by liubo
			//计算出ETF净值数据表、财务估值表的“最小申赎单位净值”项目的数据， 获取ETF净值数据表、财务估值表的“篮子估值”、“现金差额”项目的数据
			//将这六个数据分别进行比对，值不同的项目需要返回给前台，然后给出提示
			if (bIsETFGV || YssOperCons.YSS_ETF_MAKEUP_ONE.equals(getSupplyMode(sPortCode))) {
				getETFAndGuessValDiff();
			}
			
			//--- add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
			//edit by songjie 2012.11.20 添加非空判断
			if(logOper != null){
				logOper.setDayFinishIData(this, 16,"财务估值表生成", pub, false, sPortCode, 
						tradeDate,tradeDate,tradeDate,"套帐【" + setCode +"】财务估值表生成成功",
						logStartTime,logSumCode, new java.util.Date());
			}
            //--- add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
		} catch (Exception e) {
			//--- add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
			try{
				isError = true;//设置为异常状态
    			//edit by songjie 2012.11.20 添加非空判断
    			if(logOper != null){
    				logOper.setDayFinishIData(this, 16,"财务估值表生成", pub, true, sPortCode, 
    						tradeDate,tradeDate,tradeDate,"套帐【" + setCode +"】财务估值表生成失败\r\n" + 
    						//edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A 处理日志信息 除去特殊符号
    						e.getMessage().replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", ""),
    						logStartTime,logSumCode, new java.util.Date());
    			}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//--- add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
			//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
			finally{//添加 finally 保证可以抛出异常
				throw new YssException("产生财务估值表数据出错！", e);
			}
			//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
		} finally {
			dbl.closeResultSetFinal(rs); // MS00165 关闭RS连接
			// dbl.closeConnection();//QDV4南方2009年1月6日02_B MS00165 by leeyu
			// 去掉这句关闭连接的语句2009-02-03
			
			//--- add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
            if(!comeFromDD){//插入业务日志数据
    			//edit by songjie 2012.11.20 添加非空判断
    			if(logOper != null){
    				logOper.setDayFinishIData(this, 16,"sum", pub, isError, 
    						sPortCode, tradeDate,tradeDate,tradeDate," ",
    						logStartTime,logSumCode, new java.util.Date());
    			}
            }
            //--- add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
		}
		return getGuessValueReport(parameter);
	}

	private void setAccountingPeriod(java.util.Date date, int fSetCode)
			throws YssException {
		String sql = null;
		ResultSet rs = null;
		ResultSet rstmp = null;
		String maxYear = "";
		//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
		int startMonth = 0;//做账起始月份
		//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
		try {
			if (!dbl.yssTableExist("lsetlist")) {
				throw new YssException("系统未找到相关的财务数据，请确认财务系统是否创建！");
			}
			
			//---add by songjie 2012.07.20 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A start---//
			sql = "select * from lsetlist where fsetcode = "+ fSetCode + " and fyear = " + 
			      //edit by songjie 2013.01.04 BUG 6806 QDV4建行2013年01月04日01_B 改为 根据套账号获取最大年份
				  "(select MAX(FYEAR) as FYEAR from lsetlist where fsetCode = " + fSetCode + ") order by FSetCode desc"; // 获取年份最大的套帐信息
				
			rstmp = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			if (rstmp.next()) {
				maxYear = rstmp.getString("FYear");
				//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
				startMonth = rstmp.getInt("FStartMonth");
				//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
			}
			
			//---edit by songjie 2013.01.04 BUG 6806 QDV4建行2013年01月04日01_B start---//
			dbl.closeResultSetFinal(rstmp);
			rstmp = null;
			
			//系统日期对应的年份  > 如果该套帐的最大年份
			if((maxYear.length() > 0 && YssFun.getYear(date) > Integer.parseInt(maxYear)) ||
				//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 start---//
			    //如果  date 对应的年份 = 该套帐的最大年份 且 date 对应的月份 < 该套帐的最大年份 的最大月份
				(maxYear.length() > 0 && (YssFun.getYear(date) == Integer.parseInt(maxYear)) && 
			     (YssFun.getMonth(date) < startMonth))){
				//--- add by songjie 2013.05.31 STORY #3965 需求上海-[光大保德信基金]QDIIV4.0[紧急]20130515001 end---//
				//获取财务估值表相关套帐对应的最大的业务日期
				sql = " select max(FDate) as FDate from " + pub.yssGetTableName("Tb_Rep_Guessvalue") + 
				      " where FPortcode = " + fSetCode;
				rstmp = dbl.queryByPreparedStatement(sql); 
				if (rstmp.next()) {
					date = rstmp.getDate("FDate");
				}
				
				dbl.closeResultSetFinal(rstmp);
				rstmp = null;
				//date = YssFun.toDate(maxYear + "-" + String.valueOf(YssFun.getMonth(date)) + "-" + String.valueOf(YssFun.getDay(date)));
				//---edit by songjie 2013.01.04 BUG 6806 QDV4建行2013年01月04日01_B end---//
			}
			
			//---add by songjie 2012.07.20 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A end---//
			
			// 如果会计期间有问题的话，报错fazmm20070925
			//end huangqirong 2013-04-25 bug #7607	
			AccountingYear = YssFun.getYear(date);
			AccountingPeriod = YssFun.getMonth(date);
			StartDate = date;
			YssTabPrefix = "A" + AccountingYear
					+ YssFun.formatNumber(fSetCode, "000");
			if (dbl.yssTableExist("AccountingPeriod")) {
				sql = "select * from AccountingPeriod where FSetCode="
						+ fSetCode
						+ " and  FStartDate=(select max(FStartDate) from AccountingPeriod where FSetCode="
						+ fSetCode + " and  FStartDate<=" + dbl.sqlDate(date)
						+ ")";
				rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
				if (rs.next()) {
					AccountingYear = rs.getInt("FYear");
					AccountingPeriod = rs.getInt("FTerm");
					StartDate = rs.getDate("FStartDate");
					YssTabPrefix = "A".concat(rs.getString("FYear")).concat(
							YssFun.formatNumber(fSetCode, "000"));
				} else {
					throw new YssException("系统未找到相关财务数据！");//将日期去掉  容易引起误解  20130411 yeshenghong
				}
				rs.getStatement().close();
			} else {
				throw new YssException("系统未找到相关的财务数据，请确认财务系统是否创建！");
			}
		} catch (SQLException sqle) {
			throw new YssException(sqle);
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			try {
				if (rs != null) {
					rs.getStatement().close();
				}
				if (rstmp != null) {
					rstmp.getStatement().close();
				}
			} catch (Exception e) {
			}
		}
	}

	private double[] getAccBalance(java.util.Date date, int setCode,
			String acctCode, String curCode, int intMonth, // MS00348
															// QDV4招商证券2009年04月01日01_B
															// 使用调整的月份
			boolean standard) throws YssException {
		double retvalue[] = new double[2];
		String sql = null;
		ResultSet rs = null;
//		String query = "select fvarvalue from lvarlist where fvarname = '凭证录入需要审核'";
//	    String vchEntryStr = "0";// modified by  yeshenghong 20120214 story 1992
		try {
//			rs = dbl.openResultSet(query);
//	    	if(rs.next())
//	    	{
//	    		vchEntryStr = rs.getString("fvarvalue");
//	    	}
//	    	dbl.closeResultSetFinal(rs);
			setAccountingPeriod(date, setCode);
			sql = "select facctcode,sum(faendbal) as faendbal,sum(fendbal)as fendbal,sum(fbendbal) as fbendbal from "
					+ "("
					+ "select a.facctcode,faendbal*(case when b.fbaldc is null then 1 else b.fbaldc end) as faendbal,fendbal*(case when b.fbaldc is null then 1 else b.fbaldc end) as fendbal,fbendbal*(case when b.fbaldc is null then 1 else b.fbaldc end) as fbendbal from "
					+ "(" + " select facctcode,faendbal,fendbal,fbendbal from "
					+
					// --MS00348 QDV4招商证券2009年04月01日01_B
					// 使用调整的月份---------------------------
					YssTabPrefix
					+ "lbalance  where fmonth="
					+ intMonth
					+
					// ------------------------------------------------------------------------------
					" and facctcode='"
					+ acctCode
					+ "' "
					+ (curCode.trim().length() == 0
							|| curCode.equalsIgnoreCase("***") ? ""
							: " and fcurcode='" + curCode + "'")
					+ " union all "
					+ " select '"
					+ acctCode
					+ "' as facctcode,case when fjd='D'then -1*fsl else fsl end  as faendbal,case when fjd='D' then -1* fbal else fbal end as fendbal,case when fjd='D' then -1* fbbal else fbbal end as fbendbal from "
					+ YssTabPrefix
					+ "fcwvch "
					+
					// 如果生成 ETF 估值 表 ， 不查询估值日期当天的ETF申赎凭证
					// 2009-11-03 蒋锦 添加 sGVType MS00004
					// QDV4.1赢时胜（上海）2009年9月28日03_A
					(bIsETFGV ? ("where fdate>=" + dbl.sqlDate(StartDate)
							+ " and fdate< " + dbl.sqlDate(date)
							+ " OR (FDate = " + dbl.sqlDate(date)
							+ " AND Fzqjyfs <> "
							+ dbl.sqlString(YssOperCons.YSS_ETF_BUYORSELL) + ")")
							: ("where fdate>=" + dbl.sqlDate(StartDate)
									+ " and fdate<= " + dbl.sqlDate(date)))
					+ " and fkmh like '"
					+ acctCode
					+ "%'"
					+ (curCode.trim().length() == 0
							|| curCode.equalsIgnoreCase("***") ? ""
							: " and fcyid='" + curCode + "'")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ ") a inner join "
					+ YssTabPrefix
					+ "laccount b on a.facctcode=b.facctcode "
					+ ") balance group by facctcode";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				retvalue[0] = rs.getDouble("faendbal");
				retvalue[1] = rs.getDouble(standard ? "fbendbal" : "fendbal");
			}
		} catch (SQLException sqle) {
			throw new YssException("获取科目余额出错！");
		} catch (Exception e) {
			throw new YssException("获取科目余额出错！");
		}
		//---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
		finally{
			dbl.closeResultSetFinal(rs);
		}
		//---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
		return retvalue;
	}

	/**
	 * 插入美元总净值和美元单位净值 2008-5-6 单亮
	 * 
	 * @param date
	 *            Date 估值日期
	 * @param sPortCode
	 *            String 组合代码
	 * @throws YssException
	 */
	private void insertUSNav(java.util.Date date, String sPortCode, int fSetCode)
			throws YssException {
		ResultSet rs = null;
		ResultSet nextRs = null;
		String strSql = "";
		double usMoneyMarketValue = 0;
		PortfolioBean port = null;
		String sCuryName = "";
		try {
			// --------------------2008.05.27 蒋锦 添加
			// 取基础货币名称----------------------------//
			strSql = "SELECT FCuryName FROM "
					+ pub.yssGetTableName("Tb_Para_Currency")
					+ " WHERE FCuryCode = "
					+ dbl.sqlString(pub.getPortBaseCury(sPortCode));// edit by
																	// lidaolong
																	// 2011.01.24;QDV4上海2010年12月10日02_A
			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				sCuryName = rs.getString("FCuryName");
			}
			dbl.closeResultSetFinal(rs);
			// --------------------------------------------------------------------------------//
			// 查询出美元的总净值
			strSql = "select FBaseNetValue from "
					+ pub.yssGetTableName("Tb_Data_NetValue")
					+ " where FNAVDate = " + dbl.sqlDate(date)
					+ " and fportcode = " + dbl.sqlString(sPortCode)
					+ " and ftype = '01' AND FInvMgrCode = ' '"; // 2008.07.16
																	// 蒋锦
																	// 添加投资经理作为查询条件
			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				usMoneyMarketValue = rs.getDouble("FBaseNetValue");
				// 插入美元的总净值
				strSql = " insert into "
						+ pub.yssGetTableName("tb_rep_guessvalue") + " values("
						+ "'" + fSetCode + "'," + dbl.sqlDate(date)
						+ " ,'9601',' ','资产净值(" + sCuryName
						+ ")','资产净值','合计',0,0,0,0,0,0,0,0,0,0,0,"
						+ usMoneyMarketValue + ",0,0,0,0,' ',0,0,' ')";
				dbl.executeSql(strSql);
				// 查询出美元的单位净值
				// 2008-5-9 单亮 修改前的方法
				// strSql =
				// " select FBaseNetValue1/FBaseNetValue as usMoneyMarketValue from "
				// +
				// " (select FBaseNetValue as FBaseNetValue1 from " +
				// pub.yssGetTableName("Tb_Data_NetValue") +
				// " where FNAVDate =  " +
				// dbl.sqlDate(date) + " and fportcode = " +
				// dbl.sqlString(sPortCode) + " and ftype = '01') " +
				// " left join" +
				// " (select FBaseNetValue from " +
				// pub.yssGetTableName("Tb_Data_NetValue") +
				// " where FNAVDate = " +
				// dbl.sqlDate(date) + " and fportcode = " +
				// dbl.sqlString(sPortCode) + " and ftype = '05')" +
				// "  on 1=1 ";
				// 查询出美元的单位净值
				// 2008-5-9 单亮 修改后的方??
				// begin
				strSql = "select FBaseNetValue from "
						+ pub.yssGetTableName("Tb_Data_NetValue")
						+ " where FNAVDate = " + dbl.sqlDate(date)
						+ " and fportcode = " + dbl.sqlString(sPortCode)
						+ " and ftype = '02' AND FInvMgrCode = ' '"; // 2008.07.16
																		// 蒋锦
																		// 添加投资经理作为查询条件
				nextRs = dbl.openResultSet_antReadonly(strSql);
				// end
				if (nextRs.next()) {
					usMoneyMarketValue = nextRs.getDouble("FBaseNetValue");
					// 插入出美元的单位净值
					strSql = "insert into "
							+ pub.yssGetTableName("tb_rep_guessvalue")
							+ " values(" + "'" + fSetCode + "',"
							+ dbl.sqlDate(date) + ",'9602',' ','单位净值("
							+ sCuryName
							+ ")','单位净值','合计',0,0,0,0,0,0,0,0,0,0,0,0,0,"
							+ "Round(" + usMoneyMarketValue
							+ ",6),0,0,' ',0,0,' ')";
					dbl.executeSql(strSql);
				}
			}
		} catch (YssException e) {
			throw e;
		} catch (Exception ex) {
			throw new YssException("", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(nextRs);
		}
	}

	/**
	 * 按美元计价的总净值和单位净值 sj add 20080422
	 * 
	 * @param date
	 *            Date
	 * @param sPortCode
	 *            String
	 * @param fSetCode
	 *            int
	 * @throws YssException
	 */
	/*
	 * private void insertUSNav(java.util.Date date, String sPortCode, int
	 * fSetCode) throws YssException { ResultSet rs = null; ResultSet nextRs =
	 * null; String strSql = ""; double usMoneyMarketValue = 0; double
	 * dSrcCuryRate = 0; double dTagerCuryRate = 0; PortfolioBean port = null;
	 * try { strSql =
	 * "select FPortCode,FDate,FAcctCode,FCurCode,FAcctName,FStandardMoneyMarketValue from "
	 * + pub.yssGetTableName("Tb_Rep_GuessValue") + " where FPortCode = '" +
	 * fSetCode + "' and FDate = " + dbl.sqlDate(date) +
	 * " and FAcctCode = '9000' and (FCurCode = ' ' or FCurCode is null or FCurCode = '')"
	 * ; rs = dbl.openResultSet(strSql); if (rs.next()) { //usMoneyMarketValue =
	 * YssD.mul(rs.getDouble( //"FStandardMoneyMarketValue"),
	 * rs.getDouble("FPortRate")); port = new PortfolioBean();
	 * port.setYssPub(pub); port.setPortCode(sPortCode); port.getSetting();
	 * dSrcCuryRate = getSettingOper().getCuryRate(date, port.getCurrencyCode(),
	 * sPortCode, YssOperCons.YSS_RATE_BASE); dTagerCuryRate =
	 * getSettingOper().getCuryRate(date, "USD", sPortCode,
	 * YssOperCons.YSS_RATE_BASE); usMoneyMarketValue =
	 * getSettingOper().converMoney(port. getCurrencyCode(), "USD",
	 * rs.getDouble("FStandardMoneyMarketValue"), dSrcCuryRate, dTagerCuryRate);
	 * //插入资产净值(美元 strSql = "insert into " +
	 * pub.yssGetTableName("tb_rep_guessvalue") + " values(" + "'" + fSetCode +
	 * "'," + dbl.sqlDate(date) +
	 * ",'9601',' ','资产净值(美元)','资产净值','合计',0,0,0,0,0,0,0,0,0,0,0," +
	 * usMoneyMarketValue + ",0,0,0,0,' ',0,0,' ')"; dbl.executeSql(strSql);
	 * //--
	 * ----------------------------------------------------------------------
	 * ---------------------------------------------- //strSql =
	 * "select * from " + YssTabPrefix +
	 * //"laccount where facctattr like '实收资本%' and facctlevel=1"; strSql =
	 * "select * from " + pub.yssGetTableName("Tb_Rep_GuessValue") +
	 * " where FDate = " + dbl.sqlDate(date) + " and FAcctCode = '90001'"; //单亮
	 * 2008-5-4 将9600修改为90001 nextRs = dbl.openResultSet_antReadonly(strSql); if
	 * (nextRs.next()) { usMoneyMarketValue = getSettingOper().converMoney(port.
	 * getCurrencyCode(), "USD", nextRs.getDouble("FStandardMoneyMarketValue"),
	 * dSrcCuryRate, dTagerCuryRate, 6); //插入单位净值(美元 strSql = "insert into " +
	 * pub.yssGetTableName("tb_rep_guessvalue") + " values(" + "'" + fSetCode +
	 * "'," + dbl.sqlDate(date) +
	 * ",'9002',' ','单位净值(美元)','单位净值','合计',0,0,0,0,0,0,0,0,0,0,0,0,0," + //
	 * 2008-5-4 sl 将单位净值（美元）的编号修改为9002 （要修改其显示的位置） "Round(" + usMoneyMarketValue
	 * + ",6),0,0,' ',0,0,' ')"; dbl.executeSql(strSql); }
	 * //--------------------
	 * ----------------------------------------------------
	 * ---------------------------------------------- } } catch (YssException e)
	 * { throw e; } catch (Exception ex) { throw new YssException("", ex); }
	 * finally { dbl.closeResultSetFinal(rs); dbl.closeResultSetFinal(nextRs); }
	 * }
	 */

	/**
	 * 插入估值增值和汇兑损益 sj add 20080422
	 * 
	 * @param date
	 *            Date
	 * @param sPortCode
	 *            String
	 * @param fSetCode
	 *            int
	 * @throws YssException
	 */
	private void insertMVFX(java.util.Date date, String sPortCode, int fSetCode)
			throws YssException {
		ResultSet rs = null;
		String sqlStr = "";
		PortfolioBean port = null;
		double usMoneyMarketValue = 0;
		double dSrcCuryRate = 0;
		double dTagerCuryRate = 0;
		try {
			port = new PortfolioBean();
			port.setYssPub(pub);
			port.setPortCode(sPortCode);
			port.getSetting();
			dSrcCuryRate = getSettingOper().getCuryRate(date,
					port.getCurrencyCode(), sPortCode,
					YssOperCons.YSS_RATE_BASE);
			dTagerCuryRate = getSettingOper().getCuryRate(date, "CNY",
					sPortCode, YssOperCons.YSS_RATE_BASE);
			sqlStr = "select * from " + pub.yssGetTableName("Tb_Data_NetValue")
					+ " where FNAVDate = " + dbl.sqlDate(date)
					+ " and FPortCode = " + dbl.sqlString(sPortCode)
					+ " and FType in ('04','03') and FInvMgrCode = ' '";
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				//20121203 added by liubo.Bug #6496
				//9604科目：估值增值的取数逻辑，前台显示与数据库存数不一致，现在改为同一种逻辑
				//================================
//				usMoneyMarketValue = getSettingOper().converMoney(
//						port.getCurrencyCode(), "CNY",
//						rs.getDouble("FPortNetValue"), dSrcCuryRate,
//						dTagerCuryRate);
				usMoneyMarketValue = YssFun.toDouble(getAppreciation(YssFun.formatDate(date),String.valueOf(fSetCode)));
				//===============end=================
				if (rs.getString("FType").equalsIgnoreCase("03")) {
					sqlStr = "insert into "
							+ pub.yssGetTableName(sGVTableName)
							+ " values("
							+ "'"
							+ fSetCode
							+ "',"
							+ dbl.sqlDate(date)
							+ ",'9604',' ','估值增值','估值增值','合计',0,0,0,0,0,0,0,0,0,0,0,"
							+ usMoneyMarketValue + ",0,0,0,0,' ',0,0,' ')";
					dbl.executeSql(sqlStr);

				} else {
					sqlStr = "insert into "
							+ pub.yssGetTableName(sGVTableName)
							+ " values("
							+ "'"
							+ fSetCode
							+ "',"
							+ dbl.sqlDate(date)
							+ ",'9603',' ','汇兑损益','汇兑损益','合计',0,0,0,0,0,0,0,0,0,0,0,"
							+ getMarketValueToRatioInsert(sPortCode,
									(new SimpleDateFormat("yyyy-MM-dd"))
											.format(date))
							+ ",0,0,0,0,' ',0,0,' ')";
					dbl.executeSql(sqlStr);
				}
			}
		} catch (YssException e) {
			throw e;
		} catch (Exception e) {
			throw new YssException("", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 增加财务估值表已实现收益、可分配收益、单位可分配收益 QDV4海富通2009年05月11日01_AB MS00439 by leeyu
	 * 20090519
	 * 
	 * @param date
	 *            Date
	 * @param portCode
	 *            String
	 * @param portSet
	 *            int
	 * @throws YssException
	 */
	private void insertInCome(java.util.Date date, String portCode, int portSet)
			throws YssException {
		String sql = "";
		ResultSet rs = null;
		boolean bTrans = true;
		Connection conn = dbl.loadConnection();
		double dYsX = 0.0D, dKFPSJ = 0.0D, dSsZB = 0.0D; // 定义已实现收益、可分配收益、实收资本
		// 已实现收益 = 本期科目 + 损益类收入 - 损益类费用
//		String query = "select fvarvalue from lvarlist where fvarname = '凭证录入需要审核'";
//	    String vchEntryStr = "0";// modified by  yeshenghong 20120214 story 1992
		try {
//			rs = dbl.openResultSet(query);
//	    	if(rs.next())
//	    	{
//	    		vchEntryStr = rs.getString("fvarvalue");
//	    	}
//	    	dbl.closeResultSetFinal(rs);
			int iniMonth = 0;
			iniMonth = YssFun.toInt(YssFun.formatDate(
					YssFun.addMonth(date, -1), "MM")); // 取上一个月的日期
			if (YssFun.toInt(YssFun.formatDate(date, "MM")) == 1) {
				iniMonth = 0; // 判断，如果本月的日期为1,则上一个月的日期为0
			}
			conn.setAutoCommit(false);
			sql = "select sum(JE) as FMoney from ("
					+
					// 本期科目=损益平准金_已实现+本期利润_已实现
					// 损益平准金_已实现
					" select sum(c.fbendbal)*(-1) as JE from (select (case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else a.fcyid end) as fcurcode,"
					+ " (case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,(case when b.fbendbal is null then 0 else b.fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,"
					+ " sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl "
					+ " from "
					+ YssTabPrefix
					+ "Fcwvch where fterm = "
					+ YssFun.formatDate(date, "MM")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(date, "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ YssTabPrefix
					+ "Lbalance "
					+ " where fmonth ="
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ YssTabPrefix
					+ "Laccount where FAcctcode like '401101%' and fbalDC in (1, -1, 0)) d on c.kmdm = d.facctcode  "
					+
					// 本期利润_已实现
					" union select sum(c.fbendbal)*(-1) as JE from (select (case when A.fkmh is null then b.facctcode else A.fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else A.fcyid end) as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,"
					+ " (case when b.fbendbal is null then 0 else fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,"
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ YssTabPrefix
					+ "Fcwvch where fterm ="
					+ YssFun.formatDate(date, "MM")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(date, "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,"
					+ " fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ YssTabPrefix
					+ "Lbalance where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ YssTabPrefix
					+ "Laccount "
					+ " where FAcctcode like '410301%' and fbalDC in (1, -1, 0)) d on c.kmdm = d.facctcode  "
					+
					// 利润分配_未分配利润_已实现
					" union select sum(c.fbendbal)*(-1) as JE from (select (case when A.fkmh is null then b.facctcode else A.fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else A.fcyid end) as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,"
					+ " (case when b.fbendbal is null then 0 else fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,"
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ YssTabPrefix
					+ "Fcwvch where fterm ="
					+ YssFun.formatDate(date, "MM")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(date, "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,"
					+ " fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ YssTabPrefix
					+ "Lbalance where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ YssTabPrefix
					+ "Laccount "
					+ " where FAcctcode like '41040201%' and fbalDC in (1, -1, 0)) d on c.kmdm = d.facctcode  "
					+
					// 损益类收入
					" union select sum(c.fbendbal) * (-1) as JE from (select (case when A.fkmh is null then b.facctcode else A.fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else A.fcyid end) as fcurcode,NVL(b.fendbal, 0) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,(case when b.fbendbal is null then 0 else b.fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,"
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ YssTabPrefix
					+ "Fcwvch where fterm = "
					+ YssFun.formatDate(date, "MM")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(date, "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,"
					+ " fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ YssTabPrefix
					+ "Lbalance where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ YssTabPrefix
					+ "Laccount "
					+ " where FAcctcode like '6%' and FAcctcode not like '6101%' and fbalDC in (-1, -1, -1)) d on c.kmdm = d.facctcode  "
					+
					// 损益类费用
					" union select sum(c.fbendbal)*(-1) as je from (select (case when A.fkmh is null then b.facctcode else A.fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else A.fcyid end) as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,(case when b.fbendbal is null then 0 else b.fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,"
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ YssTabPrefix
					+ "Fcwvch where fterm = "
					+ YssFun.formatDate(date, "MM")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(date, "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,"
					+ " fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ YssTabPrefix
					+ "Lbalance where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ YssTabPrefix
					+ "Laccount "
					+ " where FAcctcode like '6%' and fbalDC in (1, 1, 1)) d on c.kmdm = d.facctcode  "
					+ ")";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				dYsX = rs.getDouble("FMoney");
			}
			// 关闭游标
			dbl.closeResultSetFinal(rs);
			sql = "insert into " + pub.yssGetTableName(sGVTableName)
					+ " values(" + "'" + portSet + "'," + dbl.sqlDate(date)
					+ ",'8810',' ','已实现收益','已实现收益','合计',0,0,0,0,0,0,0,0,0,0,0,"
					+ dYsX + ",0,0,0,0,' ',0,0,' ')";
			dbl.executeSql(sql);
			// 可分配收益=资产合计-负债合计-未实现利得-实收资本
			// wuweiqi 2010-10-29 可分配收益计算重新计算
			//sql = "select sum(FEndZC) as FMOney from ( " +
			//modify by jiangshichao STORY #1443 生成财务估值表的时候，计算可分配收益的算法
			sql = "select  a.FEndZC + (case when b.FEndZC < 0 then b.FEndZC else 0 end ) as FMOney from ( " +
			// 资产合计-负债合计-实收资本
					"select sum(fstandardmoneycost * (case when FacctCode = '8700' then -1 else 1 end)) as FEndZC from ( select (case when FAcctCode = '8700' then fstandardmoneycost else FStandardMoneyMarketValue end) as fstandardmoneycost,FAcctCode from "
					+ pub.yssGetTableName(sGVTableName)
					+ " where FDate = "
					+ dbl.sqlDate(date)
					+ " and FAcctCode in ('8800', '8801', '8700') and FCurCode = ' ' and FPortCode = "
					+ portSet
					+ " ) )a"
					+
					// 未实现收益利得总和
					//"union select (case when sum(FEndZC1)>0 then sum(FEndZC1)*(-1) else 0 end) as FEndZC from ( "
					//+ edit by yanghaiming 20101105 可分配收益=资产合计-负债合计-实收资本-(if
						// 未实现部分>0 then 未实现部分 else 0 )
					// 损益平准金_未实现
					//"  select sum(c.fbendbal)*(-1) as FEndZC1 from (select case when A.fkmh is null then b.facctcode else A.fkmh end as kmdm,case when A.fcyid is null then b.fcurcode else A.fcyid end as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,"
					//modify by jiangshichao STORY #1443 生成财务估值表的时候，计算可分配收益的算法
					" join ( select sum(fendzc) as fendzc from ( select sum(c.fbendbal)*(-1)*(-1) as FEndZC from (select case when A.fkmh is null then b.facctcode else A.fkmh end as kmdm,case when A.fcyid is null then b.fcurcode else A.fcyid end as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,"
					+ " (case when b.fbendbal is null then 0 else b.fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.Faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal from "
					+ " (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,"
					+ " sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,"
					+ " sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ YssTabPrefix
					+ "Fcwvch where fterm = "
					+ YssFun.formatDate(date, "MM")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(date, "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,"
					+ " faaccdebit,faendbal from "
					+ YssTabPrefix
					+ "Lbalance where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ YssTabPrefix
					+ "Laccount where FAcctcode like '401102%' "
					+ " and fbalDC in (1, -1, 0)) d on c.kmdm = d.facctcode  "
					+ // 完成转换
					// 公允价值变动损益
					" union select sum(c.fbendbal)*(-1)*(-1) as FEndZC from (select case when A.fkmh is null then b.facctcode else a.fkmh end as kmdm,case when A.fcyid is null then b.fcurcode else a.fcyid end as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,(case when b.fbendbal is null then 0 else b.fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,"
					+ " (case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,"
					+ " sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl "
					+ " from "
					+ YssTabPrefix
					+ "Fcwvch where fterm = "
					+ YssFun.formatDate(date, "MM")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(date, "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ YssTabPrefix
					+ "Lbalance "
					+ " where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ YssTabPrefix
					+ "Laccount where FAcctcode like '6101%' and fbalDC in (1, -1, 0)) d on c.kmdm = d.facctcode  "
					+
					// 利润分配_未分配利润_未实现 STORY #1443 生成财务估值表的时候，计算可分配收益的算法
					" union select sum(c.fbendbal) *(-1)*(-1) as FEndZC from (select (case when A.fkmh is null then b.facctcode else A.fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else A.fcyid end) as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,(case when b.fbendbal is null then 0 else b.fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,"
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ YssTabPrefix
					+ "Fcwvch where fterm = "
					+ YssFun.formatDate(date, "MM")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(date, "dd")
					+ " group by fkmh, fcyid, fterm) a "
					+ " full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ YssTabPrefix
					+ "Lbalance where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass "
					+ " from "
					+ YssTabPrefix
					+ "Laccount where FAcctcode like '41040202%' and fbalDC in (1, -1, 0)) d on c.kmdm = d.facctcode  "
					+
					// 本期收益_未实现  STORY #1443 生成财务估值表的时候，计算可分配收益的算法
					" union select sum(c.fbendbal)*(-1)*(-1) as FEndZC from (select (case when A.fkmh is null then b.facctcode else A.fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else A.fcyid end) as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,(case when b.fbendbal is null then 0 else b.fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,"
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ YssTabPrefix
					+ "Fcwvch where fterm = "
					+ YssFun.formatDate(date, "MM")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(date, "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,"
					+ " fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ YssTabPrefix
					+ "Lbalance where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass "
					+ " from "
					+ YssTabPrefix
					+ "Laccount where FAcctcode like '410302%' and fbalDC in (1, -1, 0)) d on c.kmdm = d.facctcode )) b on 1 = 1";//STORY #1443 生成财务估值表的时候，计算可分配收益的算法
				rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				dKFPSJ = rs.getDouble("FMoney");
			}
			// 关闭游标
			dbl.closeResultSetFinal(rs);
			sql = "insert into " + pub.yssGetTableName(sGVTableName)
					+ " values(" + "'" + portSet + "'," + dbl.sqlDate(date)
					+ ",'8811',' ','可分配收益','可分配收益','合计',0,0,0,0,0,0,0,0,0,0,0,"
					+ dKFPSJ + ",0,0,0,0,' ',0,0,' ')";
			dbl.executeSql(sql);

			// 单位可分配收益=用可分配收益/实收资本
			sql = "select fstandardmoneycost from "
					+ pub.yssGetTableName(sGVTableName) + " where FPortCode="
					+ portSet + " and FDate=" + dbl.sqlDate(date)
					+ " and FAcctCode ='8700' and FCurCode =' '";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				dSsZB = rs.getDouble("fstandardmoneycost");
			}
			sql = "insert into "
					+ pub.yssGetTableName(sGVTableName)
					+ " values("
					+ "'"
					+ portSet
					+ "',"
					+ dbl.sqlDate(date)
					+ ",'8812',' ','单位可分配收益','可分配收益','合计',0,0,0,0,0,0,0,0,0,0,0,"
					+ YssD.div(dKFPSJ, dSsZB) + ",0,0,0,0,' ',0,0,' ')";
			dbl.executeSql(sql);

			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		} catch (Exception ex) {
			throw new YssException("计算已实现收益、可分配收益、单位可分配收益时出错!", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
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
	 * @param sType
	 *            String
	 * @return String
	 */
	public String getOperValue(String showtype) throws YssException {
		return "";

	}
	
	/**
	 * add by zhouwei 20120305 
	 * 按基准资产份额进行分级核算的组合做多class资产净值的检查
	 * @param paras
	 * @return
	 * @throws YssException
	 */
	public String checkSomeNetIndexs(String paras) throws YssException{
		String[] strArray = paras.split("\t", -1);
		String portCodeId="";
		ResultSet rs=null;
		String sql="";
		Map clsTotalRightsMap=new HashMap();//组合分级的权益合计   财务系统
		StringBuffer restr=new StringBuffer();
		try{
		    portCodeId=this.getPortCode(strArray[1],  YssFun
				.left(strArray[0], 4));
		    setAccountingPeriod(YssFun.toDate(strArray[0]), YssFun
					.toInt(strArray[1]));
		    //判断是否需要进行检查指标,只有按基准资产份额进行组合分级的进行检查
			TaTradeBean tb=new TaTradeBean();
			tb.setYssPub(pub);
			boolean isClass=tb.isMultiClass(portCodeId);		
			if(isClass){
				int clsType=0;
				clsType=tb.getAccWayState(portCodeId);
				if(clsType==0){//资产净值
					return "false";
				}
			}else{
				return "false";
			}
			//-------------------------end--------------------
		    //获取财务估值表的总净值,和各class的权益合计
		    sql="select * from "+pub.yssGetTableName("tb_Rep_GuessValue")+" a where "
		       +"  a.facctattr='权益合计'   and FPortCode='"
						+ Integer.parseInt(strArray[1])
						+ "' and fdate="
						+ dbl.sqlDate(strArray[0]);
		    rs=dbl.openResultSet(sql);
		    while(rs.next()){
		    	String acctcode=rs.getString("facctcode");
		    	String[] acctclass=rs.getString("facctclass").split("\t");
		    	if(acctclass.length==2 && acctclass[0].equalsIgnoreCase("class")){
		    		clsTotalRightsMap.put(acctclass[1].trim(), rs.getDouble("FSTANDARDMONEYMARKETVALUE"));
		    	}
		    }
		    dbl.closeResultSetFinal(rs);	
		    //--------------------------------end-----------------
		    //获取各class的费后资产净值
		    sql="select Round(a.FClassNetValue,2) as FClassNetValue,a.fcurycode from "+pub.yssGetTableName("tb_data_multiclassnet")
		       +" a  where a.FNAVDate = "+dbl.sqlDate(strArray[0])+" and a.FPortCode = "+dbl.sqlString(portCodeId)+"  and a.Ftype = '01'";
		    rs=dbl.openResultSet(sql);
		    while(rs.next()){
		    	String clsPort=rs.getString("fcurycode").trim();
		    	//比较权益合计与资产净值的值
		    	if(clsTotalRightsMap.containsKey(clsPort) && (Double)clsTotalRightsMap.get(clsPort)!=rs.getDouble("FClassNetValue")){
		    		restr.append("组合分级"+clsPort+"的权益合计与资产净值不相等\r\n");
		    	}
		    }
		    dbl.closeResultSetFinal(rs);
		    //---------------end--------------------			
		}catch (Exception ex) {
			throw new YssException("检查多clss的财务估值表指标出错！", ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return restr.toString();
	}
	/**
	 * parseRowStr
	 * 
	 * @param sRowStr
	 *            String
	 */
	public void parseRowStr(String sRowStr) {
	}

	/**
	 * 获取财务库中的汇兑损益并插入估值表 2008-5-6 单亮
	 * 
	 * @param portCode
	 *            String 套帐号
	 * @param tradeDate
	 *            String 估值日期
	 * @return String
	 * @throws YssException
	 */
	private String getMarketValueToRatioInsert(String sPortCode,
			String tradeDate) throws YssException, SQLException {

		String sql = null;
		ResultSet rs = null;
		String rTotal = null; // 计算出的汇兑损益
		int iMonth = 0; // 估值日期的月份
		String lAccountName = null; // 科目表名称
		String fcwvchName = null; // 凭证表的表名称
		String fSetCode = null; // 套帐号
		String sStarDate = null; // 本月的开始日期
		YssFinance yfiance = new YssFinance();
//		String query = "select fvarvalue from lvarlist where fvarname = '凭证录入需要审核'";
//	    String vchEntryStr = "0";// modified by  yeshenghong 20120214 story 1992
		try {
//			rs = dbl.openResultSet(query);
//	    	if(rs.next())
//	    	{
//	    		vchEntryStr = rs.getString("fvarvalue");
//	    	}
//	    	dbl.closeResultSetFinal(rs);
			yfiance.setYssPub(pub);
			iMonth = Integer.parseInt(tradeDate.toString().substring(6, 7));
			// if (portCode.length() == 1) {
			// sPortCode = "00" + portCode;
			// }
			// else if (portCode.length() == 2) {
			// sPortCode = "0" + portCode;
			// }
			// sPortCode = YssFun.formatNumber(Integer.parseInt(portCode),
			// "000");
			sStarDate = tradeDate.substring(0, 8) + "01";
			fSetCode = yfiance.getCWSetCode(sPortCode); // 获取套帐号
			lAccountName = "a" + tradeDate.toString().substring(0, 4)
					+ fSetCode + "lAccount";
			fcwvchName = "a" + tradeDate.toString().substring(0, 4) + fSetCode
					+ "fcwvch";
			dbl.loadConnection().setAutoCommit(false);
			/*
			 * sql =" select sum(FBEndBal) from(" + " select FBEndBal" +
			 * " from " + balanceName + " where fmonth = " + (iMonth - 1) +
			 * " and facctcode = "+
			 * "(select facctcode from a2008001LAccount where facctattr = '财务费用_汇兑损益')"
			 * + " union "+
			 */
			sql = " select sum(fbbal)" + " from(" + " select fbbal from( "
					+ " select sum(FBBal) as fbbal" + " from "
					+ fcwvchName
					+ " where fdate>="
					+ dbl.sqlDate(sStarDate.toString())
					+ " and"
					+ " fdate <="
					+ dbl.sqlDate(tradeDate.toString())
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ "  and fjd = 'D'  and"
					+ " fkmh IN"
					+ " (select facctcode from "
					+ lAccountName
					+ " where facctattr = '财务费用_汇兑损益') )"
					+ "union ("
					+ " select -sum(FBBal) as fbbal"
					+ " from "
					+ fcwvchName
					+ " where fdate>="
					+ dbl.sqlDate(sStarDate.toString())
					+ " and"
					+ " fdate <="
					+ dbl.sqlDate(tradeDate.toString())
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ "  and fjd = 'J'  and"
					+ " fkmh IN"
					+ " (select facctcode from "
					+ lAccountName
					+ " where facctattr = '财务费用_汇兑损益') ))";

			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				rTotal = rs.getString(1);
			}
			if (rTotal == null) { // 如果rTtotal==null
				rTotal = "0";
			}
			return rTotal;
		} catch (Exception e) {
			throw new YssException("计算汇兑损益出错！" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取估值表中的汇兑损益 2008-6-5 单亮
	 * 
	 * @param sPortCode
	 *            查询的套帐号
	 * @param tradeDate
	 *            查询日期
	 * @return 查询出的汇兑损益
	 * @throws YssException
	 * @throws SQLException
	 */
	private String getMarketValueToRatio(String sPortCode, String tradeDate)
			throws YssException, SQLException {

		String sql = null;
		ResultSet rs = null;
		String rTotal = null; // 查询出的汇兑损益
		try {
			sql = "   select * from " + pub.yssGetTableName(sGVTableName)
					+ " where fportcode=" + dbl.sqlString(sPortCode)
					+ " and  facctcode = '9603' and fdate = "
					+ dbl.sqlDate(tradeDate.toString()) + "";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				rTotal = rs.getString("fstandardmoneymarketvalue");
			}
			return rTotal;
		} catch (Exception e) {
			throw new YssException("获取汇兑损益出错！" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取财务库中的估值增值 2008-5-15 单亮
	 * 
	 * @param tradeDate
	 *            String 估值日期
	 * @return String
	 * @throws YssException
	 */
	private String getAppreciation(String tradeDate) throws YssException,
			SQLException {
		String sql = null;
		ResultSet rs = null;
		String rTotal = null; // 计算出的估值增值
		try {
			dbl.loadConnection().setAutoCommit(false);
			sql = "select sum(FStandardMoneyAppreciation) from "
					+ pub.yssGetTableName(sGVTableName) + "   where fdate = "
					+ dbl.sqlDate(tradeDate.toString())
					+ " and facctdetail = 1 and FAcctLevel = 4";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				rTotal = rs.getString(1);
			}
			return rTotal;
		} catch (Exception e) {
			throw new YssException("获取估值增值出错！" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 合罗鹏程代码 add by wangzuochun 2010.12.11 BUG #563 多组合情况下，财务估值表估值增值金额有误 add by
	 * luopc
	 */
	private String getAppreciation(String tradeDate, String portcode)
			throws YssException, SQLException {
		String sql = null;
		ResultSet rs = null;
		String rTotal = null; // 计算出的估值增值
		try {
			dbl.loadConnection().setAutoCommit(false);
			sql = "select sum(FStandardMoneyAppreciation) from "
					+ pub.yssGetTableName(sGVTableName) + "   where fdate = "
					+ dbl.sqlDate(tradeDate.toString()) + " and fportcode ="
					+ portcode + " and facctdetail = 1 and FAcctLevel = 4";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				rTotal = rs.getString(1);
			}
			return rTotal;
		} catch (Exception e) {
			throw new YssException("获取估值增值出错！" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	// 此方法是根据财务中的帐号,年份来关联估值系统?械淖楹?.从而?〕鲎楹洗? by liyu add 080527
			
		private String getPortCode(String sPortID, String sYear) throws YssException {
		ResultSet rs = null;
		String sqlStr = "";
		String sResult = "";
		try {
			sqlStr = "select * from "
					+ pub.yssGetTableName("tb_para_portfolio")
					+ " where FAssetCode = "
					+ " (select FSetId from lsetlist where FsetCode="
					+ dbl.sqlString(sPortID) + " and FYear="
					+ dbl.sqlString(sYear) + " )"
					+" and fcheckstate=1 ";
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				sResult = rs.getString("FPortCode");
			}
		} catch (Exception ex) {
			throw new YssException("取组合信息出错,请检查设置是否正确", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return sResult;
	}
		
		// 此方法是根据财务中的帐号,年份来关联估值系统中的组合.从而取出组合代码 by liyu add 080527
		
		private String getPortCodeByAssetID(String sPortID, String sYear) throws YssException {
		ResultSet rs = null;
		String sqlStr = "";
		String sResult = "";
		try { //modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
			sqlStr = "select a.fportcode from " + pub.yssGetTableName("tb_para_portfolio") + " a " +
					 " join lsetlist b on a.fassetcode = b.fsetid " +
					 " where  b.FsetCode='" + sPortID + "' and b.FYear='" + sYear + "' " ;
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if(rs.next()) {
//				if (rs.getString("fportcodeLink")!= null && rs.getString("fportcodeLink").length() > 0)
//				{
//					sResult = rs.getString("fportcode");
//					break;
//				}
				sResult = rs.getString("fportcode");
			}
		} catch (Exception ex) {
			throw new YssException("取组合信息出错,请检查设置是否正确", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return sResult;
	}

	/**
	 * 用于更新停牌信息 by leeyu BUG:0000517
	 * 
	 * @param date
	 *            Date
	 * @throws YssException
	 */
	private void updateACTV(java.util.Date date) throws YssException {
		ResultSet rs1 = null, rs = null;
		String sqlStr = "", sql = "";
		BaseOperDeal deal = new BaseOperDeal();
		SecurityBean sec = null;
		String sqlUpdate = "";
		try {
			deal.setYssPub(pub);
			// 1:取出当日所有的原停牌数据
			sql = "select case when "
					+ dbl.sqlInstr("FAcctCode", "'_'")
					+ " >0 then "
					+ dbl.sqlSubStr("FAcctCode", dbl.sqlInstr("FAcctCode",
							"'_'")
							+ "+1")
					+ " else FAcctCode end as FSecurityCode,FAcctCode  from "
					+ pub.yssGetTableName("tb_rep_guessvalue")
					+ " where Fdate =" + dbl.sqlDate(date)
					+ " and FMarketDescRiBE like '【停牌】%' and FAcctlevel=4";
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			// 2:取出估值行情表中的数据 做代码的更新操作
			while (rs.next()) {
				sqlStr = "select case when max(FValDate) is null then "
						+ dbl.sqlDate(date)
						+ " else max(FValDate) end as FValDate from "
						+ pub.yssGetTableName("tb_data_valmktprice")
						+ " where FCheckState=1 and FSecurityCode="
						+ dbl.sqlString(rs.getString("FSecurityCode"))
						+ " and FPortCode="
						+ dbl.sqlString(sPortCode)
						+ " and (FMarketStatus is not null and FMarketStatus = 'ACTV') and FValDate<="
						+ dbl.sqlDate(date);
				rs1 = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
				if (rs1.next()) {
					sec = new SecurityBean();
					sec.setYssPub(pub);
					sec.setSecurityCode(rs.getString("FSecurityCode"));
					sec.getSetting();
					if (rs1.getDate("FValDate").equals(
							deal.getWorkDay(sec.getHolidaysCode(), rs1
									.getDate("FValDate"), 0))
							&& rs1.getDate("FValDate").equals(
									deal.getWorkDay(sec.getHolidaysCode(),
											date, -1))
							&& !date.equals(deal.getWorkDay(sec
									.getHolidaysCode(), date, 0))) {
						sqlUpdate = "update "
								+ pub.yssGetTableName("tb_rep_guessvalue")
								+ " set FMarketDescRiBE='【无行情】'"
								+ " where Fdate ="
								+ dbl.sqlDate(date)
								+ " and FMarketDescRiBE like '【停牌】%' and FAcctlevel=4 and FAcctCode = "
								+ dbl.sqlString(rs.getString("FAcctCode"));
					} else {
						sqlUpdate = "update "
								+ pub.yssGetTableName("tb_rep_guessvalue")
								+ " set FMarketDescRiBE='【停牌】('"
								+ dbl.sqlJN()
								+ dbl.sqlString(YssFun.formatDate(deal
										.getWorkDay(sec.getHolidaysCode(), rs1
												.getDate("FValDate"), 1),
										"yyyy-MM-dd")
										+ ")")
								+ " where Fdate ="
								+ dbl.sqlDate(date)
								+ " and FMarketDescRiBE like '【停牌】%' and FAcctlevel=4 and FAcctCode = "
								+ dbl.sqlString(rs.getString("FAcctCode"));
					}
					dbl.executeSql(sqlUpdate);
				}
				dbl.closeResultSetFinal(rs1);//add by zhaoxianlin 20130116 BUG6630通用参数中设置了财务估值表停牌信息，进行调度方案执行时报游标错误 
			}
		} catch (Exception ex) {
			throw new YssException("更新停牌信息出错", ex);
		} finally {
			dbl.closeResultSetFinal(rs, rs1);
		}
	}

	/**
	 * 插入基础货币净值和单位净值 之前处理基础货币的方法数据是从净值表中取， 但是由于改用新净值表后，原净值表中的数据已不再统计，造成原方法不可用
	 * 原方法:insertUSNav
	 * 
	 * @param date
	 *            Date 业务日期
	 * @param sPortCode
	 *            String 组合编号
	 * @param fSetCode
	 *            int 套帐编号
	 * @throws YssException
	 * @version sunkey 20081222 BugNO:MS00083
	 * @author xuqiji 20100312 MS01020 财务估值表和净值表需修改显示单位净值的方式
	 *         QDV4易方达2010年3月10日01_A
	 */
	private void insertBaseNav(java.util.Date date, String sPortCode,
			int fSetCode, CtlPubPara ctlpubpara) throws YssException {

		// 判断份额是不是为0，为0的话将不执行以下操作,避免除数为0
		if (dAmount == 0) {
			return;
		}

		ResultSet rs = null;
		Statement st = null;

		String sBaseCuryName = ""; // 基础货币名称
		double dPortRate = 0; // 组合货币汇率
		StringBuffer bufSql = new StringBuffer(); // SQL语句存储的buf，用Stringbuffer拼装SqL要比String拼装速度快
		// ----xuqiji 20100312 MS01020 财务估值表和净值表需修改显示单位净值的方式
		// QDV4易方达2010年3月10日01_A ----//
		Hashtable hmValueSetData = null;
		String sShowGuessValueSetData = "";
		// ---------------------end 20100312--------------//
		try {
			hmValueSetData = ctlpubpara.getGuessValueShowSetData(sPortCode);// xuqiji
																			// 20100312
																			// MS01020
																			// 财务估值表和净值表需修改显示单位净值的方式
																			// QDV4易方达2010年3月10日01_A
			// 获取基础货币名称和组合汇率
			// 原理:通过估值汇率表和货币配置表的货币代码进行匹配
			// 估值汇率表取汇率日期为业务日期、组合编号是要生成财务估值表所在组合的编号的组合汇率和货币代码
			// 通过以上条件，采用估值汇率表右连接货币配置表，
			// 匹配成功后从估值汇率表取组合汇率，货币配置表中取基础货币名称
			bufSql.append("SELECT B.FCURYNAME,A.FPORTRATE FROM ").append(
					"(SELECT FPORTRATE,FCURYCODE FROM ").append(
					pub.yssGetTableName("TB_DATA_VALRATE")).append(
					" WHERE FVALDATE=(SELECT MAX(FVALDATE) FROM ").append(
					pub.yssGetTableName("TB_DATA_VALRATE")).append(
					" WHERE FVALDATE <= ").append(dbl.sqlDate(date)).append(
					" AND FCURYCODE = ").append(
					dbl.sqlString(pub.getPortBaseCury(sPortCode)))// edit by
																	// lidaolong
																	// 2011.01.24;QDV4上海2010年12月10日02_A
					.append(")").append(" AND FCURYCODE = ").append(
							dbl.sqlString(pub.getPortBaseCury(sPortCode)))// edit
																			// by
																			// lidaolong
																			// 2011.01.24;QDV4上海2010年12月10日02_A
					.append(" AND FPORTCODE = ").append(
							dbl.sqlString(sPortCode)).append(") A ").append(
							"RIGHT JOIN ").append(
							pub.yssGetTableName("TB_PARA_CURRENCY")).append(
							" B ON A.FCURYCODE = B.FCURYCODE ").append(
							" WHERE B.FCURYCODE = ").append(
							dbl.sqlString(pub.getPortBaseCury(sPortCode)));// edit
																			// by
																			// lidaolong
																			// 2011.01.24;QDV4上海2010年12月10日02_A
			rs = dbl.queryByPreparedStatement(bufSql.toString()); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				sBaseCuryName = rs.getString("FCURYNAME"); // 获取基础货币名称
				dPortRate = rs.getDouble("FPORTRATE"); // 获取组合汇率
			}
			bufSql = null; // 释放StringBuffer对象
			rs.getStatement().close(); // 关闭statment对象，避免造成游标混乱
			rs.close(); // 关闭结果集

			// 查询资产净值和单位资产净值
			// 原理：因为统计基础货币净值是在统计资产净值之后
			// 直接从估值表中取成本和市值，科目属性为资产净值，科目号9000
			// 日期为业务日期，货币代码为' '，因为要统计多个资产净值，为' '的才是汇总
			// 还要通过套账号进行匹配，注：*这里是套账号，并非组合号
			bufSql = new StringBuffer();
			bufSql.append("SELECT FSTANDARDMONEYMARKETVALUE").append(" FROM ")
					.append(pub.yssGetTableName(sGVTableName)).append(
							" WHERE FACCTATTR = '资产净值'").append(
							" AND FACCTCODE = '9000'").append(" AND FDATE = ")
					.append(dbl.sqlDate(date)).append(" AND FCURCODE = ' '")
					.append(" AND FPORTCODE = ").append(fSetCode);
			rs = dbl.queryByPreparedStatement(bufSql.toString()); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				// 如果业务日的资产净值和单位净值统计完毕后，执行基础货币的资产净值和单位净值的统计
				// 打开statment对象，备批量操作使用
				st = dbl.openStatement();
				// ------xuqiji 20100312 MS01020 财务估值表和净值表需修改显示单位净值的方式
				// QDV4易方达2010年3月10日01_A -----//
				//modify by baopingping  20110608  STORY #1041  注释掉下面的条件目的是为了不管 资产净值（人民币） 是否显示 都将资产净值（美元）插入到TB_Rep_GuessValue表中
				/*if (hmValueSetData.containsKey("CboAssetValue")) {
					sShowGuessValueSetData = (String) hmValueSetData
							.get("CboAssetValue");
					if (sShowGuessValueSetData.equalsIgnoreCase("1")
							|| sShowGuessValueSetData.equalsIgnoreCase("true")) {*/// “是”，则产生资产净值（人民币）这一行
						// -----插入基础货币资产净值
						bufSql = null;
						bufSql = new StringBuffer();
						bufSql
								.append("insert into ")
								.append(pub.yssGetTableName(sGVTableName))
								.append(" values('")
								.append(fSetCode)
								.append("',")
								.append(dbl.sqlDate(date))
								.append(",")
								.append("'9601',' ','资产净值(")
								// --基础货币资产净值 ，科目代号为9601
								.append(sBaseCuryName)
								.append(")','资产净值','合计',0,0,0,0,0,0,0,0,0,0,0,")
								.append(
										rs
												.getDouble("FSTANDARDMONEYMARKETVALUE"))
								.append("*").append(dPortRate) // 基础货币净值=[资产净值(市值)]
																// * 组合汇率
								.append(",0,0,0,0,' ',0,0,").append(
										"'"
												+ (dPortRate == 0 ? "没有汇率或汇率为0"
														: " ") + "')"); // 一直无组合汇率，在备注中告知用户
						st.addBatch(bufSql.toString());
				/*	}
				}*/
						//modify by baopingping  20110608  STORY #1041  注释掉下面的条件目的是为了不管 单位净值（人民币） 是否显示 都将单位净值（美元）插入到TB_Rep_GuessValue表中
				/*if (hmValueSetData.containsKey("cboUnitValue")) {
					sShowGuessValueSetData = (String) hmValueSetData
							.get("cboUnitValue");
					if (sShowGuessValueSetData.equalsIgnoreCase("1")
							|| sShowGuessValueSetData.equalsIgnoreCase("true")) {*/// “是”，则产生单位净值（人民币）这一行
						// ----插入基础货币单位净值
						bufSql = null;
						bufSql = new StringBuffer();
						bufSql
								.append("insert into ")
								.append(pub.yssGetTableName(sGVTableName))
								.append(" values('")
								.append(fSetCode)
								.append("',")
								.append(dbl.sqlDate(date))
								.append(",")
								.append("'9602',' ','单位净值(")
								// --基础货币资产净值 ，科目代号为9602
								.append(sBaseCuryName)
								.append(
										")','单位净值','合计',0,0,0,0,0,0,0,0,0,0,0,0,0,")
								// 基础货币单位净值=round([资产净值(市值)] * 组合汇率 / 份额 ,6)
								.append("round(")
								.append(
										rs
												.getDouble("FSTANDARDMONEYMARKETVALUE"))
								.append("*").append(dPortRate).append("/")
								.append(dAmount).append(",6),0,0,' ',0,0,")
								.append(
										"'"
												+ (dPortRate == 0 ? "没有汇率或汇率为0"
														: " ") + "')"); // 一直无组合汇率，在备注中告知用户;;
						st.addBatch(bufSql.toString());
				/*	}
				}*/
				//----end------
				// -------------------------------------end
				// 20100312---------------------------------------------//
				// 执行持久化操作
				st.executeBatch();
				// 释放
				st.close();
			}
			// 释放资源
			bufSql = null;
			rs.getStatement().close();
			rs.close();
		} catch (Exception e) {
			throw new YssException("对不起，插入基础货币净值时出现错误!", e);
		} finally {
			dbl.closeStatementFinal(st);
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取格式化字符串，这个只能格式化数字成指定小数位的字符串 默认保留1位小数 参数： String para -- sql语句或要保留的小数位
	 * String defaultDeci -- 默认保留几位小数
	 * 
	 * @param strSql
	 *            String
	 * @return String
	 * @throws YssException
	 * @version sunkey 20081224 BugNO:MS00090
	 */
	private String getFormatNumStr(String strSql, String defaultDeci)
			throws YssException {
		int dmal = 0;
		// 通过sql语句查询数据，并将其转换成数字类型作为要保留的小数位
		
		try{
			CtlPubPara ctlPara = new CtlPubPara();
			ctlPara.setYssPub(pub);
			String tmp = (String) ctlPara.getGuessValuePara(new YssDbFun(pub)
					.GetValuebySql(strSql), "cboAmountDecimal");
			//add by guyichuan  20110411 #820  不限制小数位
			if(tmp!=null && "不限制小数位".equals(tmp.trim())){
				return "#,##0.######";  //系统最大支持6位小数,###### :小于6位按原位数输出
			}
			// 因为在参数设置中排序是按照词汇的编号排序的，如果设置默认值，必须设置为小的数据，此时我设置了负数，此处要进行绝对值
			dmal = Math.abs(tmp.length() == 0 ? Integer.parseInt(defaultDeci)
					: Integer.parseInt(tmp));
			// 根据要保留的小数位，使用训话构建类似"#,##0.000"格式的格式化字符串
			StringBuffer tmpBuf = new StringBuffer("#,##0");
			for (int i = 0; i < dmal; i++) {
				// 第一次循环的时候要在格式化字符串后加“.”处理
				if (i == 0) {
					tmpBuf.append(".");
				}
				tmpBuf.append("0");
			}
			return tmpBuf.toString();
		}catch(NullPointerException  ex){
			throw new YssException(ex.getMessage()); 

		}finally{
			
		}
		
		
	}

	/**
	 * 计算并保存累计净值
	 * 
	 * @throws YssException
	 *             MS00374 QDV4建行2009年4月11日01_A
	 */
	private void insertAccumulateUnit(java.util.Date dDate, String portCode,
			int fSetCode) throws YssException {
		String sqlStr = "";
		ResultSet rs = null;
		StringBuffer bufSql = null;
		double accumulate = 0.0; // 累计净值
		int iDigit = 0; // 累计净值保留位数
		CtlPubPara ctlPara = null; // 通用参数
		/* 从净值表中获取累计净值 */
		//sqlStr = "select FPrice from " + pub.yssGetTableName("Tb_Data_NavData")
		//		+ " where FOrderCode = " + dbl.sqlString("Total7")
		//		+ " and FPortCode = " + dbl.sqlString(portCode)
		//		+ " and FNavDate = " + dbl.sqlDate(dDate);
				//STORY #1877 累计净值采用财务估值表里的单位净值加分红数据来计算 modify by jiangshichao 2011.11.16
		sqlStr = "select fstandardmoneymarketvalue from " + pub.yssGetTableName("tb_rep_guessvalue")
				+ " where facctcode = '9600' and fcurcode=' '" 
				+ " and FPortCode = " +fSetCode
				+ " and FDate = " + dbl.sqlDate(dDate);
		try {
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) { // 若已经在净值表中生成了累计净值
				//accumulate = rs.getDouble("FPrice"); // 获取的累计净值
				accumulate = getSplitUnit(rs.getDouble("fstandardmoneymarketvalue"),dDate, portCode); //STORY #1877 累计净值采用财务估值表里的单位净值加分红数据来计算 modify by jiangshichao 2011.11.16
				String sDate = (new SimpleDateFormat("yyyy-MM-dd"))
						.format(dDate);
				ctlPara = new CtlPubPara();
				ctlPara.setYssPub(pub);
				iDigit = Integer.parseInt(ctlPara.getCashUnit(getPortCode(
						fSetCode + "", YssFun.left(sDate, 4)))); // 通过通用参数来获取累计净值应该保留的小数位数
				bufSql = new StringBuffer();
				bufSql.append("insert into ").append(
						pub.yssGetTableName(sGVTableName)).append(" values('")
						.append(fSetCode).append("',").append(
								dbl.sqlDate(dDate)).append(",")
						.append("'9612',' ','累计单位净值'")
						// 累计单位净值 ，科目代号为9612
						.append(",'累计单位净值','合计',0,0,0,0,0,0,0,0,0,0,0,")
						.append("round(").append(accumulate).append(",")
						.append(iDigit).append(")") // 累计净值,并以通用参数的设置值来保留位数
						.append(",0,").append("0,0,0,' ',0,0,").append("' ')");
				dbl.executeSql(bufSql.toString());
			} // end if
		} catch (Exception e) {
			throw new YssException("获取累计净值出现异常！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	
	// --- STORY #1877 累计净值采用财务估值表里的单位净值加分红数据来计算  add by jiangshichao 2011.11.16  start
	
	
	/**
	 *  STORY #1877 累计净值采用财务估值表里的单位净值加分红数据来计算   add by jiangshichao 2011.11.16
	 * 计算累计单位净值
	 * 分两种情形：1.没有基金拆分时；2.有基金拆分时。
	 * 1.没有基金拆分时算法：基金份额净值+累计分红金额(以前就是这么计算的)
	 * 2.有基金拆分时，累计净值的算法如下:
     *累计单位净值=｛【（基金份额净值+基金最后一次拆分后的单位分红金额）*基金最后一次拆细比例 +
     *   基金最后一次拆细前的的单位分红金额】×基金倒数第二次拆分比例＋基金倒数第二次拆分前的分红金额 ｝×……
     *   基金第一次拆分比例＋基金第一次拆分前的分红金额。
     *@return Double 累计净值
     *@param  Double unit 基金份额净值
     *@throws YssException
     *@author ysstech_xuxuming
	 */
	private double getSplitUnit(double dUnit,java.util.Date dDate, String portCode) throws YssException {
//		HashMap hmSplitRatio = null;// 拆分日期+拆分比例
		ArrayList arraySplitRatio = new ArrayList();// 拆分日期
		ArrayList arraySplitAll = new ArrayList();//存储拆分时间，
		double dSplitUnit = 0.0;// 累计单位净值
		arraySplitAll = this.getHmSplitRatio(dDate,portCode);
		String strSplitDate = YssFun.formatDate(dDate);// 字符以界面上选的日期开始
		try {
			for (Iterator iter = arraySplitAll.iterator(); iter
					.hasNext();) {
				String tmpRatioAll = (String) iter.next();
				String[] tmpAry = null;
				tmpAry = tmpRatioAll.split(YssCons.YSS_ITEMSPLITMARK1);
				String tmpDate = (String) tmpAry[0];// 取出里面保存的日期
				arraySplitRatio.add(tmpAry[1]);// 将拆分比例保存到列表
				strSplitDate += YssCons.YSS_ITEMSPLITMARK1 + tmpDate
						+ YssCons.YSS_LINESPLITMARK + tmpDate;// 将每次拆分日期保存
				// 将每次拆分日期 分为区间保存，两两为一组，构成一个区间；如：date1,date2,date3
			}
			strSplitDate += YssCons.YSS_ITEMSPLITMARK1
					+ YssFun.formatDate(dDate);// 字符以界面上选的日期结束

			String reqAry[] = null;
			String tmpAry[] = null;
			reqAry = strSplitDate.split(YssCons.YSS_LINESPLITMARK);
			dSplitUnit = dUnit;
			for (int i = 0; i < arraySplitRatio.size(); i++) {// 遍历每一次拆分比例
				tmpAry = reqAry[i].split(YssCons.YSS_ITEMSPLITMARK1);// 日期区间与列表中拆分比例是对应的
				dSplitUnit = YssD.mul(Double.parseDouble(arraySplitRatio.get(i)
						.toString()), YssD.add(dSplitUnit, this
						.getSplitDivided(YssFun.toDate(tmpAry[1]), YssFun
								.toDate(tmpAry[0]),portCode)));
			}
			// 1.无基金拆分情形，要加上累计分红金额；
			// 2.有基金拆分的情形，前面已经计算了每次拆分比例和每次拆分后分红金额，现在还需要加上第一次拆分前的分红金额
			// 因为日期区间段比拆分比例要多一次，前面对拆分比例的遍历是没有考虑最后这一次的。
			tmpAry = reqAry[reqAry.length - 1]
					.split(YssCons.YSS_ITEMSPLITMARK1);// 取最后一次日期区间，即是第一次分红之前的时间段(有基金拆分时),或界面上传来的日期(无基金拆分时)
			dSplitUnit = YssD.add(dSplitUnit, this.getSplitDivided(YssFun  //只需要根据后一个日期来计算，统计<=tmpAry[0]的分红，故前一日期设为固定值，不参与计算
					.toDate("9998-12-31"), YssFun.toDate(tmpAry[0]),portCode));// 基金第一次拆分前的分红金额(有基金拆分时),或 累计分红金额(无基金拆分时)

		} catch (Exception e) {
			throw new YssException("计算累计单位净值时出现异常");
		} finally {
			arraySplitAll.clear();
			arraySplitRatio.clear();
		}
		return dSplitUnit;
	}
	
	
	 /**
     * 获取基金拆分时的各次分红金额
     * @param dStartDate,dEndDate 取两个日期之间的所有分红金额之和
     * @return Double 返回两次日期之间的分红金额总额
     * @throws YssException
     * @author ysstech_xuxuming
     * 计算累计净值时，要用到分红金额
     */
	private double getSplitDivided(java.util.Date dStartDate,
			java.util.Date dEndDate, String portCode) throws YssException {
		String sqlStr = null;
		ResultSet rs = null;
		double dSplitDivided = 0.0;// 存放基金拆分的分红金额
		try {
			sqlStr = "select sum(FSellPrice) as FSplitDivided from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 and FPortCode = "
					+ dbl.sqlString(portCode) + " and FSellType = '03'"; // 获取累计分红价格，数据为小于等于当前日期
            //传进来的日期是 拆分日期。因为拆分日期当天不会出现分红，所以统不统计拆分当天的分红金额是没有影响的
			if (dStartDate != null&&!YssFun.formatDate(dStartDate).equals("9998-12-31")) {
				sqlStr += " and FConfimDate >= " + dbl.sqlDate(dStartDate);//因为拆分日期当天无分红，此时用 >= 和用 > 是一样的。
			}
			if (dEndDate != null&&!YssFun.formatDate(dEndDate).equals("9998-12-31")) {
				sqlStr += " and FConfimDate <= " + dbl.sqlDate(dEndDate);
			}
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				dSplitDivided = rs.getDouble("FSplitDivided");
			}
		} catch (Exception e) {
			throw new YssException("获取基金拆分时的分红数据出现异常！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return dSplitDivided;
	}
	
	/**
     * 获取基金拆分的每次拆分比例
     * @return HashMap 返回每次拆分比例
     * @throws YssException
     * @author ysstech_xuxuming
     * 计算累计净值时，要用到每次的拆分比例 
     */
	private ArrayList getHmSplitRatio(java.util.Date dDate, String portCode) throws YssException {
		String sqlStr = "";
		ResultSet rs = null;
		ArrayList arraySplitDate = new ArrayList();//存储拆分时间,拆分比例，因为要按顺序取
		try {
			sqlStr = "select FSplitRatio,FConfimDate from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState=1 and FPortCode= "
					+ dbl.sqlString(portCode)
					+ " and FSplitRatio >0"//排除为空的记录
					+ " and FConfimDate <="+dbl.sqlDate(dDate)//只对之前的日期进行计算，所以只获取之前的拆分比例
					+ " order by FConFimDate desc";
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				arraySplitDate.add(YssFun.formatDate(rs.getDate("FConfimDate"))+YssCons.YSS_ITEMSPLITMARK1+
						new Double(rs.getDouble("FSplitRatio")).toString());
			}
		} catch (Exception e) {
			throw new YssException("获取基金拆分的每次拆分比例出现异常");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return arraySplitDate;
	}
	
	// --- STORY #1877 累计净值采用财务估值表里的单位净值加分红数据来计算  add by jiangshichao 2011.11.16  end 
	
	
	
	
	/**
	 * 判断是否能够生成财务估值表
	 * 
	 * @param dDate
	 *            Date 生成日期
	 * @param fSetCode
	 *            int 套装号
	 * @throws YssException
	 *             MS00402 QDV4海富通2009年04月21日01_AB author sj
	 */
	private void judgeCanGreateGuess(java.util.Date dDate, int fSetCode)
			throws YssException {
		CtlPubPara ctlPara = null; // 通用参数
		String sPortCode = null;
		String judgeCheckState = null;
		ctlPara = new CtlPubPara();
		ctlPara.setYssPub(pub);
		sPortCode = getPortCode(fSetCode + "", YssFun.left(YssFun
				.formatDate(dDate), 4)); // 通过套装获取组合代码
		if (null == sPortCode) { // 控制可能的未获取组合的情况
			throw new YssException("系统未能获取组合代码！");
		}
		judgeCheckState = ctlPara.getJudgeGreateGuess(sPortCode); // 通过通用参数判断是否生成财务估值表
		if (null != judgeCheckState && judgeCheckState.equalsIgnoreCase("yes")) { // 判断通用参数设置
			judgeVchCheckState(dDate); // 通过手工凭证是否审核来判断
		}
	}

	/**
	 * 判断手工凭证是否已审核,若未审核则抛出异常
	 * 
	 * @param dDate
	 *            Date 凭证发生日期
	 * @throws YssException
	 *             MS00402 QDV4海富通2009年04月21日01_AB author sj
	 */
	private void judgeVchCheckState(java.util.Date dDate) throws YssException {
		ResultSet rs = null;
		StringBuffer bufSql = new StringBuffer();
		StringBuffer bufMessage = new StringBuffer();
		// 获取手工且未审核的凭证
		bufSql.append("select distinct(FVCHPDH) from "); // 去除科目代码的获取，新说明不需要科目代码
		bufSql.append(YssTabPrefix);
		bufSql
				.append("fcwvch where FCHECKR = ' ' and FPZLY = 'HD' and Fdate = "); // 获取凭证发生日期
		bufSql.append(dbl.sqlDate(dDate));
		try {
			rs = dbl.queryByPreparedStatement(bufSql.toString()); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) { // 将未审核的凭证的信息获取,并向前台传递
				bufMessage.append("凭证编号【");
				bufMessage.append(rs.getString("FVCHPDH"));
				bufMessage.append("】");
				bufMessage.append("\r\n");
			}
			if (bufMessage.length() > 0) { // 当有相应的信息时,抛出异常
				bufMessage.insert(0, "以下手工凭证尚未审核,请将其审核!\r\n");
				throw new YssException(bufMessage.toString()); // 将异常抛出
			}
		} catch (Exception e) {
			throw new YssException(e); // 捕获之前抛出的异常
		} finally {
			bufSql = null;
			bufMessage = null;
			dbl.closeResultSetFinal(rs);
		}
	}

	/*********************************************************************
	 * BUG NO: MS01000 QDV4北京2010年02月25日01_A BUG DEC: 核对财务表资产净值和净值统计表的资产净值
	 * 
	 * @param squest
	 *            :portCode\tdate
	 * @author jiangshichao 2010.03.22
	 * @throws Exception
	 */
	public String checkMarketvalue(String squest) throws YssException {
		String[] arr = squest.split("\t");
		String portCode = arr[0];
		String date = arr[1];
		double marketValue_jz = 0;
		double marketValue_cw = 0;
		String checkResult = "";

		ResultSet rs = null;
		String sql = "";

		try {
			// ---查询净值统计表里的资产净值 -------
			sql = " select fportmarketvalue as marketvalue_jz from "
					+ pub.yssGetTableName("Tb_Data_NavData")
					+ " where  FPortCode = " + dbl.sqlString(portCode)
					+ " and FNavDate = " + dbl.sqlDate(date)
					+ " and fkeyname like '资产净值：%' "// edit by licai 20101126 BUG #486 财务估值表 点击'核对净值'按钮时，查询净值统计表里的资产净值会查询出三条记录
					// add by licai 20110308 BUG #486 财务估值表 点击'核对净值'按钮 时，查询净值统计表里的资产净值会查询出三条记录
					+ " and fkeycode = 'TotalValue' ";
			        // add by licai 20110308 BUG #486===================================================================end
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				marketValue_jz = YssD.round(rs.getDouble("marketvalue_jz"), 2);
			} else {
				throw new YssException("请先生成【" + date + "】的净值统计表！！");
			}
			dbl.closeResultSetFinal(rs);

			// ---查询财务估值表里的资产净值 -----
			sql = " select fstandardmoneymarketvalue as marketvalue_cw from "
					+ pub.yssGetTableName("Tb_Rep_GuessValue")
					+ " where fdate="
					+ dbl.sqlDate(date)
					+ " and fcurcode=' ' and facctname ='资产净值' and fportcode in("
					+ " select distinct fsetcode from Lsetlist where fsetid in(select fassetcode from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where fportcode= " + dbl.sqlString(portCode)
					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
					/*+ " and FStartdate =" + "( select max(FStartdate) from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where fportcode= " + dbl.sqlString(portCode) + " )))"; */
					+ " ))";
					//end by lidaolong
			/**shashijie 2012-11-26 BUG 6330 财务估值表界面核对净值按钮有问题 
			 * 考虑到多组合群时会取出多条记录,这里控制只取出汇总组合的净值不取分级组合的净值*/
			sql += " And FAcctClass Like "+dbl.sqlString("%合计%");
			/**end shashijie 2012-11-26 BUG 6330*/
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				marketValue_cw = YssD.round(rs.getDouble("marketvalue_cw"), 2);
			} else {
				throw new YssException("请先生成【" + date + "】的财务估值表！！");
			}

			// ---产生核对结果的字符串（0：核对一致；1：核对不一致）
			if (marketValue_jz == marketValue_cw) {

				checkResult = "0\t"
						+ YssFun.formatNumber(marketValue_jz, "###,##0.##")
						+ "\t"
						+ YssFun.formatNumber(marketValue_cw, "###,##0.##");
			} else if (marketValue_jz != marketValue_cw) {
				checkResult = "1\t"
						+ YssFun.formatNumber(marketValue_jz, "###,##0.##")
						+ "\t"
						+ YssFun.formatNumber(marketValue_cw, "###,##0.##");
			}
			return checkResult;
		} catch (Exception e) {
			throw new YssException("【日终处理-财务估值表-核对资产净值】出现异常！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	private String setAccountingPeriod(java.util.Date date, int fSetCode,
			boolean states) throws YssException { // edited by zhouxiang MS01339
													// 财务估值表显示的日期，与日期控件显示的日期不一致
		String sql = null;
		String ErrorState = "";// 会计日期报错标志
		ResultSet rs = null;
		try {
			if (!dbl.yssTableExist("lsetlist")) {
				throw new YssException("系统未找到相关的财务数据，请确认财务系统是否创建！");
			}
			// 如果会计期间有问题的话，报错fazmm20070925
			AccountingYear = YssFun.getYear(date);
			AccountingPeriod = YssFun.getMonth(date);
			StartDate = date;
			YssTabPrefix = "A" + AccountingYear
					+ YssFun.formatNumber(fSetCode, "000");
			if (dbl.yssTableExist("AccountingPeriod")) {
				sql = "select * from AccountingPeriod where FSetCode="
						+ fSetCode
						+ " and  FStartDate=(select max(FStartDate) from AccountingPeriod where FSetCode="
						+ fSetCode + " and  FStartDate<=" + dbl.sqlDate(date)
						+ ")";
				rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
				if (rs.next()) {
					AccountingYear = rs.getInt("FYear");
					AccountingPeriod = rs.getInt("FTerm");
					StartDate = rs.getDate("FStartDate");
					YssTabPrefix = "A".concat(rs.getString("FYear")).concat(
							YssFun.formatNumber(fSetCode, "000"));

				} else {
					ErrorState = Integer.toString(fSetCode);
				}

				rs.getStatement().close();
			} else {
				throw new YssException("系统未找到相关的财务数据，请确认财务系统是否创建！");
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			try {
				if (rs != null) {
					rs.getStatement().close();
				}
			} catch (Exception e) {
			}
		}
		return ErrorState;
	}

	// add by fangjiang 2010.12.10 STORY #95 生成财务估值表时，与余额表进行比对，不一致需给出提示，并出报表
	// modify by fangjiang 2011.06.03 story #899
	public String checkbgApp(String parameter) throws YssException {
		String[] strArray = parameter.split("\t", -1);
		String checkDate = strArray[0];
		YssFinance finance = new YssFinance();
		finance.setYssPub(pub);
		String portCode = finance.getPortCode(strArray[1]);
		
		//20120609 added by liubo.Bug #4749
		//判断生成的估值表是否有包含证券市值调整数据
		//===============================
		sIsIncludeModifyingData = strArray[2];
		//=============end==================

		String year = checkDate.split("-")[0];
		String month = checkDate.split("-")[1];
		// String date = checkDate.split("-")[2];
		int lMonth = Integer.parseInt(month);

		String set = finance.getCWSetCode(portCode);
		String YssTablePrefix = "A" + year + set;
		int YssStartMonth = getThisSetAccLen(set, Integer.parseInt(year));
		// String addr = portCode + "_" + year + month + date;

		String strSql = "";
		ResultSet rs = null;
		
		String gAcctCode = ""; // 财务估值表科目代码
		String bAcctCode = ""; // 余额表科目代码
		double gApp; // 财务估值表估值增值
		double bApp; // 余额表估值增值
		double gStandardApp; // 财务估值表本位币估值增值
		double bStandardApp; // 余额表本位币估值增值
		double diff; // 估增不一致金额
		double standardDiff; // 估增本位币不一致金额
		int flag = 0;
		String curyCode = ""; //币种
		String acctName = ""; //科目名称
		CtlPubPara ctlPub = new CtlPubPara(); 
		ctlPub.setYssPub(pub);
		AppCheck appCheck = null;
		AppCheck appCheckAdmin = new AppCheck();
//		String query = "select fvarvalue from lvarlist where fvarname = '凭证录入需要审核'";
//	    String vchEntryStr = "0";// modified by  yeshenghong 20120214 story 1992
		
		ArrayList<String> arrAuxiList = new ArrayList<String>();//20130628 added by liubo.Bug #8346.取到的辅助核算项的集合
		
		try {
//			rs = dbl.openResultSet(query);
//	    	if(rs.next())
//	    	{
//	    		vchEntryStr = rs.getString("fvarvalue");
//	    	}
//	    	dbl.closeResultSetFinal(rs);
			/*
			 * strSql = " Delete from tmpBalBal where FAddr='" + addr + "'";
			 * dbl.executeSql(strSql);
			 */

			// 从余额表中导入年初数及上月数据到临时余额表中
			/*
			 * strSql = " insert into tmpBalBal select " + addr + "," +
			 * " a.*  from " + YssTablePrefix + "lbalance a where fmonth=0" +
			 * ((lMonth > 1) ? " or fmonth=" + (lMonth - 1) : "");
			 * dbl.executeSql(strSql);
			 */
			
			/**Start 20130628 modified by liubo.Bug #8346.QDV4建行2013年06月20日01_B
			 * 在做证券代码转换后，转换前的证券代码在估值表中是肯定不会体现的。
			 * 但是在当天有可能会有关于转换前代码的证券的业务，同时生成了凭证并导入到了财务系统
			 * 如此就可能造成估值表余额表核对时遗漏掉该转换前代码的估增凭证
			 * 因此需要修改核对逻辑，主要是要full join取余额表的查询临时表
			 * 另外设置一个FFlag字段，0表示符合上述情况的特殊数据；1表示正常取数的数据*/			
			strSql = "select * from (" +
					"select (case when a.facctcode is null then 0 else 1 end) as FFlag," +
					" (case when a.facctcode is null then " +
					" (b.fkmh || '_' || substr(FAuxiAcc,3)) else a.facctcode end) as facctcode, " +
					" b.facctname, b.fcyid, b.fkmh,b.FauxiAcc, Nvl(a.FAppreciation,0) as FAppreciation, " +
					" b.fendbal, " +
					" Nvl(a.FStandardMoneyAppreciation,0) as FStandardMoneyAppreciation," +
					" b.fbendbal" +
					" from (select t.facctcode,  t.FAcctClass, t.FAmount, t.FMarketPrice, t.FAppreciation,t.FStandardMoneyAppreciation, " +
					" (case when m.facctattr like '债券投资%' then replace(m.facctattr, '债券投资', '投资估值增值') " +
		            " when m.facctattr = '债券投资' then replace(m.facctattr, '债券投资', '投资估值增值_债券') " +
		            " when m.facctattr like '股票投资%' then replace(m.facctattr, '股票投资', '投资估值增值_股票') " + //modified by yeshenghong 20120217 story 2160
		            " else m.facctattr end) as facctattr " +
					"from (select facctcode,FAcctClass,FAmount,FMarketPrice, FAppreciation, " +
		            " FStandardMoneyAppreciation from " +
		            pub.yssGetTableName("Tb_Rep_GuessValue") +
		            " where fdate = " +
					dbl.sqlDate(checkDate) +
					" and FPortCode = " + 
					dbl.sqlString(strArray[1]) +
		            " and FAcctDetail = 1 and facctcode like '%/_%' escape '/'  and famount > 0 and FMarketPrice > 0) t " +
					" full join (select facctcode, facctattr from " +
					 YssTablePrefix +
					 "laccount) m on m.facctcode = substr(t.facctcode, 1, INSTR(t.facctcode, '_') - 1)) a " 
					 
					 /**Start 20130717 modified by liubo.Bug #8635.QDV4建行2013年07月15日01_B
					  * 这里的FULL JOIN改为JOIN，避免关联到非债券和股票的科目*/
					+ " join (select NVL(fkmh, facctcode) as fkmh, NVL(fcyid, fcurcode) as fcyid, NVL(fendbal, 0), "
					+ "　NVL(fjje, 0), NVL(fdje, 0), NVL(fjje, 0) + NVL(faccdebit, 0), "
					/**End 20130717 modified by liubo.Bug #8635.QDV4建行2013年07月15日01_B*/
					
					+ " NVL(fdje, 0) + NVL(facccredit, 0), NVL(fendbal, 0) + NVL(fjje, 0) - NVL(fdje, 0) as fendbal, NVL(fbendbal, 0), "
					+ " NVL(fbjje, 0), NVL(fbdje, 0), NVL(fbjje, 0) + NVL(fbaccdebit, 0), NVL(fbdje, 0) + NVL(fbacccredit, 0), "
					+ " NVL(fbendbal, 0) + NVL(fbjje, 0) - NVL(fbdje, 0) as fbendbal, NVL(faendbal, 0), NVL(fjsl, 0), NVL(fdsl, 0), "
					+ " NVL(fjsl, 0) + NVL(faaccdebit, 0), NVL(fdsl, 0) + NVL(faacccredit, 0), NVL(faendbal, 0) + NVL(fjsl, 0) - NVL(fdsl, 0), "
					+ " case when a.FAuxiAcc is null then b.FauxiAcc else a.FauxiAcc end as FauxiAcc, facctname, facctattr"
					+ " from (select fkmh, fcyid, sum(case when fjd = 'J' then fbal else 0 end) as fjje, "
					+ " sum(case when fjd = 'D' then fbal else 0 end) as fdje, sum(case when fjd = 'J' then fsl else 0 end) as fjsl, "
					+ " sum(case when fjd = 'D' then fsl else 0 end) as fdsl, sum(case when fjd = 'J' then fbbal else 0 end) as fbjje, "
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje, FauxiAcc, facctname,facctattr "
					+ " from (select * from "
					+ YssTablePrefix
					+ "fcwvch where fterm="
					+ lMonth
					+ " and fdate <= "
					+ dbl.sqlDate(checkDate)
					//20120609 added by liubo.Bug #4749
					//判断生成的估值表是否有包含证券市值调整数据
					//若不是，需要在获取的财务系统的凭证数据中排除掉自动生成的估增差异凭证
					//===============================
					+ (sIsIncludeModifyingData.trim().equalsIgnoreCase("true") ? "" : " and fvchzy not like '%估增差异%'")
					//================end===============
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) m join (select facctcode, facctname, facctattr from "
					+ YssTablePrefix
					+ "laccount where facctdetail = 1 "
					+ " and substr(facctattr, 1, 6) = '投资估值增值') n on m.fkmh = n.facctcode group by fkmh, fcyid, FauxiAcc, facctname,facctattr) a "
					+ " left join (select c.facctcode, fmonth, c.fcurcode, faccdebit, facccredit, fendbal, fbaccdebit, fbacccredit, fbendbal,"
					+ " faaccdebit, faacccredit, faendbal, c.fauxiacc  from (select * from "
					+ YssTablePrefix
					+ "LBalance where fmonth = "
					+ ((lMonth <= YssStartMonth) ? 0 : lMonth - 1)
					+ " ) c join (select facctcode, facctname from "
					+ YssTablePrefix
					+ "laccount where facctdetail = 1 "
					+ " and substr(facctattr, 1, 6) = '投资估值增值') d on c.facctcode = d.facctcode) b on a.fkmh = b.facctcode "
					+ " and a.fcyid = b.fcurcode and a.fauxiacc = b.fauxiacc) b on "
					//---modified by zhaoxianlin 20130108 Bug BUG6762生成完财务估值表有系统提示---atart//
					//+ " INSTR(a.facctcode, substr(b.FauxiAcc,3,LENGTH(b.FauxiAcc) - 2)) =  INSTR(a.facctcode, '_') + 1 " +
					+" substr(a.facctcode,instr(a.facctcode, '_')+1) = substr(b.fauxiacc, 3, length(b.fauxiacc) - 2)"
					//---modified by zhaoxianlin 20130108 Bug BUG6762生成完财务估值表有系统提示---end//
					+" and a.facctattr = b.facctattr "
					//add by songjie 2011.05.31 BUG 1907 QDV4建行2011年05月11日01_B
					+ " where b.fendbal <> 0 and b.fbendbal <> 0 " +
					
					//201201019 added by liubo.Bug #5961
					//之前系统根据科目性质从财务系统的科目表中关联科目，只将“债券投资”关联“投资估值增值_债券”，“股票投资”关联“投资估值增值_股票”
					//没有考虑诸如期货、基金等科目的估值增值科目的转换。在这里合并一个查询语句，将其他类的固执增值科目，用证券代码关联进来
					//***************************************************
					" union all " +
					
					" select (case when a.facctcode is null then 0 else 1 end) as FFlag," +
					" (case when a.facctcode is null then " +
					" (b.fkmh || '_' || substr(FAuxiAcc,3)) else a.facctcode end) as facctcode, " +
					" b.facctname, b.fcyid, b.fkmh,b.FauxiAcc, " +
					" Nvl(a.FAppreciation,0) as FAppreciation, " +
					" b.fendbal, " +
					
					/**Start 20130717 modified by liubo.Bug #8635.QDV4建行2013年07月15日01_B
					 * 取数逻辑问题，这里应该取本位币估值增值。之前的逻辑是取原币估增（a.FAppreciation）*/
					" Nvl(a.FStandardMoneyAppreciation,0) as FStandardMoneyAppreciation," +
					/**End 20130717 modified by liubo.Bug #8635.QDV4建行2013年07月15日01_B*/
					
					" b.fbendbal" +
					" from (select t.facctcode,  t.FAcctClass, t.FAmount, t.FMarketPrice, t.FAppreciation," +
					" t.FStandardMoneyAppreciation, " +
					" facctattr " +
					" from (select facctcode,FAcctClass,FAmount,FMarketPrice, FAppreciation, " +
		            " FStandardMoneyAppreciation from " +
		            pub.yssGetTableName("Tb_Rep_GuessValue") +
		            " where fdate = " +
					dbl.sqlDate(checkDate) +
					" and FPortCode = " + 
					dbl.sqlString(strArray[1]) +
		            " and FAcctDetail = 1 and facctcode like '%/_%' escape '/'  and famount > 0 and FMarketPrice > 0) t " +
					" join (select facctcode, facctattr from " +
					 YssTablePrefix +
					 "laccount where facctattr not like '债券投资%'  " +
					 //20130411 modified by liubo.Bug #7480
					 //若使用facctattr <> '股票投资'的形式来剔除股票的明细科目，在遇到类似“股票投资_非公开发行新股增发”股票投资的子类的科目性质时
					 //会出现排除不掉的情况，从而造成数据重复
					 //============================
//					 "and facctattr <> '股票投资') " +
					 "and facctattr not like '股票投资%') " +
					 //=============end===============
					 "m on m.facctcode = substr(t.facctcode, 1, INSTR(t.facctcode, '_') - 1)) a " 
					+ " join (select NVL(fkmh, facctcode) as fkmh, NVL(fcyid, fcurcode) as fcyid, NVL(fendbal, 0), NVL(fjje, 0), NVL(fdje, 0), NVL(fjje, 0) + NVL(faccdebit, 0), "
					+ " NVL(fdje, 0) + NVL(facccredit, 0), NVL(fendbal, 0) + NVL(fjje, 0) - NVL(fdje, 0) as fendbal, NVL(fbendbal, 0), "
					+ " NVL(fbjje, 0), NVL(fbdje, 0), NVL(fbjje, 0) + NVL(fbaccdebit, 0), NVL(fbdje, 0) + NVL(fbacccredit, 0), "
					+ " NVL(fbendbal, 0) + NVL(fbjje, 0) - NVL(fbdje, 0) as fbendbal, NVL(faendbal, 0), NVL(fjsl, 0), NVL(fdsl, 0), "
					+ " NVL(fjsl, 0) + NVL(faaccdebit, 0), NVL(fdsl, 0) + NVL(faacccredit, 0), NVL(faendbal, 0) + NVL(fjsl, 0) - NVL(fdsl, 0), "
					+ " case when a.FAuxiAcc is null then b.FauxiAcc else a.FauxiAcc end as FauxiAcc, facctname, facctattr"
					+ " from (select fkmh, fcyid, sum(case when fjd = 'J' then fbal else 0 end) as fjje, "
					+ " sum(case when fjd = 'D' then fbal else 0 end) as fdje, sum(case when fjd = 'J' then fsl else 0 end) as fjsl, "
					+ " sum(case when fjd = 'D' then fsl else 0 end) as fdsl, sum(case when fjd = 'J' then fbbal else 0 end) as fbjje, "
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje, FauxiAcc, facctname,facctattr "
					+ " from (select * from "
					+ YssTablePrefix
					+ "fcwvch where fterm="
					+ lMonth
					+ " and fdate <= "
					+ dbl.sqlDate(checkDate)
					+ (sIsIncludeModifyingData.trim().equalsIgnoreCase("true") ? "" : " and fvchzy not like '%估增差异%'")
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " //add by yeshenghong 20120410 story2425
					+ " ) m join (select facctcode, facctname, facctattr from "
					+ YssTablePrefix
					+ "laccount where facctdetail = 1 "
					+ " and substr(facctattr, 1, 6) = '投资估值增值') n on m.fkmh = n.facctcode group by fkmh, fcyid, FauxiAcc, facctname,facctattr) a "
					+ " left join (select c.facctcode, fmonth, c.fcurcode, faccdebit, facccredit, fendbal, fbaccdebit, fbacccredit, fbendbal,"
					+ " faaccdebit, faacccredit, faendbal, c.fauxiacc  from (select * from "
					+ YssTablePrefix
					+ "LBalance where fmonth = "
					+ ((lMonth <= YssStartMonth) ? 0 : lMonth - 1)
					+ " ) c join (select facctcode, facctname from "
					+ YssTablePrefix
					+ "laccount where facctdetail = 1 "
					+ " and substr(facctattr, 1, 6) = '投资估值增值') d on c.facctcode = d.facctcode) b on a.fkmh = b.facctcode "
					+ " and a.fcyid = b.fcurcode and a.fauxiacc = b.fauxiacc) b on "
					//---modified by zhaoxianlin 20130108 Bug BUG6762生成完财务估值表有系统提示---atart//
					//+ " INSTR(a.facctcode, substr(b.FauxiAcc,3,LENGTH(b.FauxiAcc) - 2)) =  INSTR(a.facctcode, '_') + 1 " +
					+" substr(a.facctcode,instr(a.facctcode, '_')+1) = substr(b.fauxiacc, 3, length(b.fauxiacc) - 2) "
					//---modified by zhaoxianlin 20130108 Bug BUG6762生成完财务估值表有系统提示---end//
					
					+ " where b.fendbal <> 0 and b.fbendbal <> 0 "
					+ ") order by Fflag desc,FAcctCode";
					//**********************end*****************************
			/**End 20130628 modified by liubo.Bug #8346.QDV4建行2013年06月20日01_B */
					
			//20130711 modified by liubo.使用queryByPreparedStatement做查询时报内部参数错误
			//==============================
//			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			rs = dbl.openResultSet(strSql);
			//=============end=================
			while (rs.next()) {	
				
				/**Start 20130628 modified by liubo.Bug #8346.QDV4建行2013年06月20日01_B*/
				if(rs.getString("Fflag").equals("0"))
				{
					if (arrAuxiList.contains(rs.getString("FAuxiAcc")))
					{
						continue;
					}
					else
					{
						arrAuxiList.add(rs.getString("FAuxiAcc"));
					}
				}
				else
				{
					arrAuxiList.add(rs.getString("FAuxiAcc"));
				}
				/**End 20130628 modified by liubo.Bug #8346.QDV4建行2013年06月20日01_B*/
				
				curyCode = rs.getString("fcyid");
				gAcctCode = rs.getString("facctcode");
				bAcctCode = rs.getString("fkmh");
				acctName = rs.getString("facctname");
				gApp = rs.getDouble("FAppreciation");
				bApp = rs.getDouble("fendbal");
				diff = YssD.sub(gApp, bApp);
				gStandardApp = rs.getDouble("FStandardMoneyAppreciation");
				bStandardApp = rs.getDouble("fbendbal");	
				standardDiff = YssD.sub(gStandardApp, bStandardApp);
				
				appCheck = new AppCheck();
				appCheck.setsPortCode(portCode);
				appCheck.setdDate(YssFun.toDate(checkDate));
				appCheck.setCuryCode(curyCode);
				appCheck.setAcctName(acctName);
				appCheck.setAcctCode(gAcctCode);
				appCheck.setgApp(gApp);
				appCheck.setbApp(bApp);
				appCheck.setDiff(diff);
				appCheck.setgStandardApp(gStandardApp);
				appCheck.setbStandardApp(bStandardApp);
				appCheck.setStandardDiff(standardDiff);
				//add by fangjiang 2011.08.08 STORY #1268 
				if(diff == 0 && standardDiff == 0) {
					appCheck.setFlag(1);
				} else {
					appCheck.setFlag(3);
				}
				//----------------------
				appCheckAdmin.addList(appCheck);
				
				if(diff != 0 || standardDiff != 0) {
					flag++;	
				}						
			}
			appCheckAdmin.setYssPub(pub);
			appCheckAdmin.init(YssFun.toDate(checkDate), portCode);
			appCheckAdmin.insert();
			
			//--- add by songjie 2013.02.20 STORY #3429 需求深圳-[YSS_SZ]QDV4.0[中]20121221001 start---//
			//将比对结果存入财务估值表  便于其他系统获取并查看
			strSql = " insert into " + pub.yssGetTableName("Tb_Rep_Guessvalue") + 
			" (FPORTCODE ,FDATE ,FACCTCODE ,FCURCODE ,FACCTNAME ,FACCTATTR ,FACCTCLASS, " +
			" FEXCHANGERATE ,FAMOUNT ,FCOST ,FSTANDARDMONEYCOST ,FCOSTTONETRATIO, " +
			" FSTANDARDMONEYCOSTTONETRATIO ,FMARKETPRICE ,FOTPRICE1 ,FOTPRICE2, " +
			" FOTPRICE3 ,FMARKETVALUE ,FSTANDARDMONEYMARKETVALUE ,FMARKETVALUETORATIO, " +
			" FSTANDARDMONEYMARKETVALUETORAT ,FAPPRECIATION ,FSTANDARDMONEYAPPRECIATION, " +
			" FMARKETDESCRIBE ,FACCTLEVEL ,FACCTDETAIL ,FDESC)values" + 
			"(" + 
			dbl.sqlString(strArray[1]) + "," +
			dbl.sqlDate(checkDate) + "," +
			"'D200', ' ', 'D200', ' ' ,' ', 0 ,0 ,0 ,0 ,0, 0 ,0 ,0 ,0, 0 ,0 ," + 
			(flag > 0 ? "'0'" : "'1'") + " ," + 
			" 0, 0 ,0 ,0, '' ,0 ,0 ," +
			(flag > 0 ? "'与余额表核对不一致'" : "'与余额表核对一致'")+
			") ";
			
			dbl.executeSql(strSql);
			//--- add by songjie 2013.02.20 STORY #3429 需求深圳-[YSS_SZ]QDV4.0[中]20121221001 end---//
		} catch (Exception e) {
			//add by songjie 2013.02.19 STORY #3429 需求深圳-[YSS_SZ]QDV4.0[中]20121221001
			throw new YssException("财务估值表与余额表比对报错！");
		} finally {			
			dbl.closeResultSetFinal(rs);
		}
		return String.valueOf(flag);
	}
	
	
	/**
	 * @author guyichuan 2011.06.29
	 * STORY #1183 符合本位币市值占净值比就给出提示
	 * @param str str
	 * @return sResult
	 */
	public String checkRatio(String str)throws YssException{
		String portCode=null;
		String checkDate=null;
		String sResult="";
		
		if(str !=null && str.length()!=0){
			String[] strArray = str.split("\t", -1);
			checkDate = strArray[0];
			YssFinance finance = new YssFinance();
			finance.setYssPub(pub);
			portCode = finance.getPortCode(strArray[1]);
		}else return null;
		
		ResultSet rs=null;
		ResultSet rs2=null;
		
		String KeMuCode=null;   //科目代码
		String filterSql=null;
		String strSymbol=null;  //关系式
		int symbol=0;
		
		double ratio=0;            //本位币市值占净值比
		double tmpRatio=0;			//录入的占净值比
		
		StringBuffer bufSql=new StringBuffer();
		bufSql.append(" select a.portCode, b.Ratio, c.symbol, d.kemuCode,e.FVocName as strSymbol");
		bufSql.append(" from (select FctlValue as portCode, FParaId");
		bufSql.append(" from "+pub.yssGetTableName("TB_PFOper_PUBPARA"));
		bufSql.append(" where FPubParaCode = 'Is_Gusspara02'");
		bufSql.append(" and FParaGroupCode = 'Gusspara'");
		bufSql.append(" and FctlCode = 'selPort' and FctlValue like "+dbl.sqlString(portCode+"|%")+") a");
		bufSql.append(" join (select FctlValue as Ratio, FParaId");
		bufSql.append(" from "+pub.yssGetTableName("TB_PFOper_PUBPARA"));
		bufSql.append(" where FPubParaCode = 'Is_Gusspara02'  and FParaGroupCode = 'Gusspara'");
		bufSql.append(" and FctlCode = 'txtBox2') b on a.FParaId = b.FParaId");
		bufSql.append("  join (select substr(FctlValue,0,1) as symbol, FParaId");
		bufSql.append(" from "+pub.yssGetTableName("TB_PFOper_PUBPARA"));
		bufSql.append(" where FPubParaCode = 'Is_Gusspara02'");
		bufSql.append(" and FParaGroupCode = 'Gusspara'  and FctlCode = 'cbBox') c on c.FParaId = a.FParaId");
		bufSql.append(" join (select FctlValue as kemuCode, FParaId");
		bufSql.append(" from "+pub.yssGetTableName("TB_PFOper_PUBPARA"));
		bufSql.append(" where FPubParaCode = 'Is_Gusspara02'");
		bufSql.append(" and FParaGroupCode = 'Gusspara'  and FctlCode = 'txtTip') d on d.FParaId = a.FParaId");
		bufSql.append(" left join (select FVocName,FVocCode from Tb_Fun_Vocabulary ");
		bufSql.append(" where FVocTypeCode='IsGusspara02' )e");
		bufSql.append(" on e.FVocCode=c.symbol");
		
		try {
			rs = dbl.queryByPreparedStatement(bufSql.toString()); //modify by fangjiang 2011.08.14 STORY #788
			while(rs.next()){
				KeMuCode=rs.getString("kemuCode");					//录入的科目代码
				tmpRatio=Double.valueOf(rs.getString("Ratio")).doubleValue();	//录入的占净值比
				strSymbol=rs.getString("strSymbol");				//关系式
				symbol=Integer.parseInt(rs.getString("symbol"));
				boolean flag = false;
				
				if(KeMuCode !=null && KeMuCode.indexOf("*") !=-1){//科目代码包含*
					filterSql=" and FAcctCode <> "+dbl.sqlString(KeMuCode.substring(0, KeMuCode.length()-1))
					    +" and FAcctCode like "+dbl.sqlString(KeMuCode.replace('*', '%')+"/_%")+" escape '/'"
						+" and FAcctDetail=1";
				}else{
					filterSql=" and FAcctCode="+dbl.sqlString(KeMuCode);
				}
				bufSql.delete(0, bufSql.length());//清空bufSql
				bufSql.append(" select FPortCode, FDate,FAcctCode,");
				bufSql.append("  sum(fstandardmoneymarketvaluetorat) as ratio,");
				bufSql.append(" FAcctLevel,FAcctDetail,FAcctName");
				bufSql.append(" from "+pub.yssGetTableName("Tb_Rep_GuessValue"));
				bufSql.append(" where FDate="+dbl.sqlDate(checkDate));
				bufSql.append(filterSql);
				bufSql.append("  group by FPortCode,FDate,FAcctCode,FAcctLevel,FAcctName,FAcctDetail");
				bufSql.append(" order by fdate asc, facctcode asc");
				
				rs2=dbl.queryByPreparedStatement(bufSql.toString()); //modify by fangjiang 2011.08.14 STORY #788
				while(rs2.next()){
					ratio=Math.abs(rs2.getDouble("ratio"))*100;  //本位币市值占净值比
					ratio=(double) YssD.round(ratio, 2);		  //保留两位小数					
					switch(symbol){
						case 1:flag=(ratio>tmpRatio?true:false);break;
						case 2:flag=(ratio<tmpRatio?true:false);break;
						case 3:flag=(ratio>=tmpRatio?true:false);break;
						case 4:flag=(ratio<=tmpRatio?true:false);break;
						case 5:flag=(ratio==tmpRatio?true:false);break;
					}
					if(flag){
						sResult +="【科目代码："+rs2.getString("FAcctCode")+",科目名称："+
						rs2.getString("FAcctName")+"】市值占净值比为"+ratio+"%，"+strSymbol+tmpRatio+"%";
					}
				}
				dbl.closeResultSetFinal(rs2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new YssException("核查本位币市值占净值比出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs2);
			dbl.closeResultSetFinal(rs);
		}
		return sResult;
	}
	

	/*********************************************************************
	 * Story #1194计算出净值统计表和财务估值表的未分配利润，并返回给前台
	 * 
	 * @param sRequest
	 * @return String
	 * @author liubo 2011715
	 * @throws YssException
	 */
	public String CheckUndistributedProfit(String sRequest) throws YssException
	{
		ResultSet rsTotalValue = null;			//获取资产净值
		ResultSet rsTotalAmount = null;			//获取实收资本
		ResultSet rsRealizedGain = null;		//获取已实现收益
		ResultSet rsNavData = null;				//获取净值统计表的未分配利润
		
		double dbTotalValue = 0;				//资产净值
		double dbTotalAmount = 0;				//实收资本
		double dbRealizedGain = 0;				//已实现收益
		double dbNavData = 0;					//净值统计表的未分配利润
		String sDate = "";
		String sPortCode = "";
		try
		{
			String sData[] = sRequest.split("\t");
			sDate = sData[0];
			
			//20110718 added by liubo.Story #1194
			//通过调用YssFinance类的getPortCode方法来获取净值统计表的组合代码
			//******************************
			YssFinance fina = new YssFinance();
			fina.setYssPub(pub);
			sPortCode = fina.getPortCode(sData[1]);
			//****************end******************
			
			DecimalFormat dft = new DecimalFormat("0.000");
			
			StringBuffer buff = new StringBuffer();
			buff.append(" select FStandardMoneyMarketValue as FStandardMoneyMarketValue from " + pub.yssGetTableName(sGVTableName));
			buff.append(" where FPortCode = " + dbl.sqlString(sData[1]) + " and FDate = " + dbl.sqlDate(sDate));
			buff.append(" and FACCTATTR = '资产净值'").append(" AND FACCTCODE = '9000'").append(" AND FCURCODE = ' ' ");
			
			rsTotalValue = dbl.queryByPreparedStatement(buff.toString()); //modify by fangjiang 2011.08.14 STORY #788
			
			while(rsTotalValue.next())
			{
				dbTotalValue = rsTotalValue.getDouble("FStandardMoneyMarketValue");
			}
			
			buff = new StringBuffer();
			
			buff.append(" select FCost  as FStandardMoneyMarketValue from " + pub.yssGetTableName(sGVTableName));
			buff.append(" where FPortCode = " + dbl.sqlString(sData[1]) + " and FDate = " + dbl.sqlDate(sDate));
			buff.append(" and FACCTATTR = '实收资本'").append(" AND FACCTCODE = '8700'").append(" AND FCURCODE = ' ' ");
			
			rsTotalAmount = dbl.queryByPreparedStatement(buff.toString()); //modify by fangjiang 2011.08.14 STORY #788
			
			while(rsTotalAmount.next())
			{
				dbTotalAmount = rsTotalAmount.getDouble("FStandardMoneyMarketValue");
			}
			
			buff = new StringBuffer();
			
			buff.append(" select FStandardMoneyMarketValue  as FStandardMoneyMarketValue from " + pub.yssGetTableName(sGVTableName));
			buff.append(" where FPortCode = " + dbl.sqlString(sData[1]) + " and FDate = " + dbl.sqlDate(sDate));
			buff.append(" and FACCTATTR = '已实现收益'").append(" AND FACCTCODE = '8810'");
			
			rsRealizedGain = dbl.queryByPreparedStatement(buff.toString()); //modify by fangjiang 2011.08.14 STORY #788
			
			while(rsRealizedGain.next())
			{
				dbRealizedGain = rsRealizedGain.getDouble("FStandardMoneyMarketValue");
			}
			
			buff = new StringBuffer();
			
			buff.append(" select sum(FPortMarketValue) as cnt from " + pub.yssGetTableName("Tb_Data_NavData") + " ");
			buff.append(" where fnavdate = " + dbl.sqlDate(sDate));
			buff.append(" and fkeycode in ('MV','UnPL') and FPortCode = '" + sPortCode + "'");
			
			rsNavData = dbl.queryByPreparedStatement(buff.toString()); //modify by fangjiang 2011.08.14 STORY #788
			
			while(rsNavData.next())
			{
				dbNavData = rsNavData.getDouble("cnt");
			}
			
			return dft.format(dbTotalValue - dbTotalAmount - dbRealizedGain) + "\t" + dft.format(dbNavData);
			
		}
		
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		
		finally
		{
			dbl.closeResultSetFinal(rsTotalValue,rsTotalAmount,rsRealizedGain,rsNavData);
		}
		
	}
	
	
	
	/*********************************************************************
	 * Story #1095       增加“本年利润”、“本年已实现收益”、“本年可分配利润（期间数）”、“本年末可分配利润（期末数）”4个参数的显示
	 * 
	 * @param sDate
	 * @return String
	 * @author liubo 20110628
	 * @throws YssException
	 */
	private String getCurrentYear(String sDate,String sPordCode) throws YssException
	{
		String strSql = "";
		ResultSet rsA = null;   //获取“本年利润”
//		ResultSet rsB = null;	//获取“本年已实现收益”
//		ResultSet rsC = null;	//获取“本年可分配利润（期间数）”
//		ResultSet rsD = null;	//获取“本年末可分配利润（期末数）”
		
		StringBuffer buffer = new StringBuffer();

		int iMonth = 0;			//估值日月份
		String portSel="portSel";
		CtlPubPara ctlPub = new CtlPubPara(); // 显示财务估值表中已实现部分的数据   
		String cboAlProfits="cboAlProfits";//是否显示本年末可分配利润   
		String cboAttrProfit="cboAttrProfit";//是否显示本年可分配利润   
		String  cboCurrentYear="cboCurrentYear";//是否显示本年利润 
		String cboRealizedGain="cboRealizedGain";//是否显示本年已实现收益 
//		String query = "select fvarvalue from lvarlist where fvarname = '凭证录入需要审核'";
//	    String vchEntryStr = "0";// modified by  yeshenghong 20120214 story 1992
		try
		{
//			rsA = dbl.openResultSet(query);
//	    	if(rsA.next())
//	    	{
//	    		vchEntryStr = rsA.getString("fvarvalue");
//	    	}
//	    	dbl.closeResultSetFinal(rsA);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date date = df.parse(sDate);//got your required date
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			iMonth = cal.get(Calendar.MONTH) + 1;//Calender的月份区间是0——11，所以要在算出的月份上+1

			ctlPub.setYssPub(pub);
			//20110629 added by liubo.Stroy #1095
			//取“财务估值表显示设置”的参数值，判断是否显示“本年利润”
			//***************************************
			if (ctlPub.getvalue(portSel,cboCurrentYear,sPordCode).equalsIgnoreCase("1"))
				//*******************end********************
			{
				
				//假如估值表日期为2010年5月20日，投资组合为A，对应套帐代码为001.
				//则本年利润为所有以“6”开头科目在5月1日至20日该科目在凭证表a2010001FcwVch中的发生额，加上科目“本期利润”在余额表4月期末本位币余额。
				//“本期利润”科目代码为4103
				
				strSql = "select sum(bal) as CNT from ( " +
						" select sum(FBBal) as bal from " + YssTabPrefix + "fcwvch " +
					  " where fterm = '" + String.valueOf(iMonth)  + "'"
					  + " and (fconfirmer <> ' ' or fconfirmer is null) " + //add by yeshenghong 20120410 story2425 
					  " and fywdate between trunc(to_date('" + sDate + "','yyyy-mm-dd'),'MON') and to_date('" + sDate + "','yyyy-mm-dd') and fkmh like '6%' " +
					  " union all " +
					  " select FBEndBal as bal from " + YssTabPrefix + "lbalance " +
					  " where fmonth = '" + String.valueOf(iMonth - 1) + "' and facctcode = '4103')";
					 
				rsA = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
				while(rsA.next())
				{
					//20110720 modified by liubo.Story #1095
					//对取值进行判定，当取到NULL时不进行返回字符串的拼接，已实现无数据不显示科目
					//*********************************************
					if (rsA.getString("CNT") != null)
					{
						buffer.append("\r\n");
						buffer.append("\t");
						buffer.append("本年利润").append("\t");
						buffer.append(YssFun.formatNumber(rsA.getDouble("CNT"), "#.00")).append("\t"); 
						buffer.append("\t \t\t\t\t0.00\t0.00\t\t\t\t0.00\t0.00\t\t0.00\t \t \t \t0\t"); 
					}
					//****************end**********************
				}
				dbl.closeResultSetFinal(rsA);
			
			}
			//20110629 added by liubo.Stroy #1095
			//取“财务估值表显示设置”的参数值，判断是否显示“本年已实现收益”
			//***************************************
			if (ctlPub.getvalue(portSel,cboRealizedGain,sPordCode).equalsIgnoreCase("1"))
			//********************end*******************
			{
				
				//如第1点中的例子，则“本年已实现收益”为除“公允价值变动损益”外的6字头科目余额和“本期利润_已实现”科目余额之和。
				//“公允价值变动损益”科目代码为6101，“本期利润_已实现”科目代码为410301
				
				strSql = "select sum(bal) as CNT from (" +
						"select sum(FBEndBal) as bal from " + YssTabPrefix + "lbalance  " +
						" where fmonth = '" + String.valueOf(iMonth - 1) + "' and facctcode like '6%' and facctcode not like '6101%' " +
						" and facctcode in (select facctcode from " + YssTabPrefix + "laccount where FAcctParent = '  ' or FAcctParent = ' ' or FAcctParent = '')" +
						" union all " +
						" select sum(FBEndBal) as bal from " + YssTabPrefix + "lbalance  " +
						" where fmonth = '" + String.valueOf(iMonth - 1) + "' and facctcode = '410301')";
						
				
				rsA = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
				while(rsA.next())
				{
					//20110720 modified by liubo.Story #1095
					//对取值进行判定，当取到NULL时不进行返回字符串的拼接，已实现无数据不显示科目
					//*********************************************
					if (rsA.getString("CNT") != null)
					{
						buffer.append("\r\n");
						buffer.append("\t");
						buffer.append("本年已实现收益").append("\t");
						buffer.append(YssFun.formatNumber(rsA.getDouble("CNT"), "#.00")).append("\t"); 
						buffer.append("\t \t\t\t\t0.00\t0.00\t\t\t\t0.00\t0.00\t\t0.00\t \t \t \t0\t"); 
					}
					//********************end***********************
				}

				dbl.closeResultSetFinal(rsA);
			}

			//20110629 added by liubo.Stroy #1095
			//取“财务估值表显示设置”的参数值，判断是否显示“本年可分配利润”
			//***************************************
			if (ctlPub.getvalue(portSel,cboAttrProfit,sPordCode).equalsIgnoreCase("1"))
			//********************end*******************
			{
				
				//本年可分配利润（期间数）取以下两者的最小值：
				//A 科目“本期利润_已实现”余额与科目“损益平准金_已实现”余额之和
				//B 科目“公允价值变动损益”、“损益平准金未实现”、“本期利润-未实现”余额之和
				//“本期利润_已实现”科目代码为410301,“损益平准金_已实现”科目代码为401101
				//“公允价值变动损益”科目代码为6101。，“损益平准金未实现”科目代码为401102，“本期利润-未实现”科目代码为410302
				
				strSql = "select (case when sum(acctA) < sum(acctB) then sum(acctA) else sum(acctB) end) AS CNT  from ( " +
						  " select sum(FBEndBal) as acctA,null as acctB from " + YssTabPrefix + "lbalance  " +
						  " where fmonth = '" + String.valueOf(iMonth - 1) + "' and facctcode in ('410301','401101') " +
						  " union all " +
						  " select null as acctA,sum(FBEndBal) as acctB from " + YssTabPrefix + "lbalance  " +
						  " where fmonth = '" + String.valueOf(iMonth - 1) + "' and facctcode in ('410302','401102','6101'))";
		
				rsA = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
				while(rsA.next())
				{
					//20110720 modified by liubo.Story #1095
					//对取值进行判定，当取到NULL时不进行返回字符串的拼接，已实现无数据不显示科目
					//*********************************************
					if (rsA.getString("CNT") != null)
					{
						buffer.append("\r\n");
						buffer.append("\t");
						buffer.append("本年可分配利润（期间数）").append("\t");
						buffer.append(YssFun.formatNumber(rsA.getDouble("CNT"), "#.00")).append("\t"); 
						buffer.append("\t \t\t\t\t0.00\t0.00\t\t\t\t0.00\t0.00\t\t0.00\t \t \t \t0\t");
					}
					//*********************end**************************
				}

				dbl.closeResultSetFinal(rsA);
			}
			//20110629 added by liubo.Stroy #1095
			//取“财务估值表显示设置”的参数值，判断是否显示“本年末可分配利润”
			//***************************************
			if (ctlPub.getvalue(portSel,cboAlProfits,sPordCode).equalsIgnoreCase("1"))
			//***************end************************
			{
				
				//本年末可分配利润（期末数）取以下两者的最小值：
				//A 科目“本期利润_已实现”、“损益平准金已实现”、“利润分配_已实现”余额之和。
				//B 科目“公允价值变动损益”、“损益平准金未实现”、“本期利润_未实现”、“利润分配_未实现”余额之和。
				//“本期利润_已实现”科目代码为410301,“损益平准金_已实现”科目代码为401101,“利润分配_已实现”科目代码为41040201
				//“公允价值变动损益”科目代码为6101。，“损益平准金未实现”科目代码为401102，“本期利润-未实现”科目代码为410302,“利润分配_未实现”科目代码为41040202
				
				strSql = " select (case when sum(acctA) < sum(acctB) then sum(acctA) else sum(acctB) end) AS CNT  from ( " +
						 " select sum(FBEndBal) as acctA,null as acctB from " + YssTabPrefix + "lbalance  " +
						 " where fmonth = '" + String.valueOf(iMonth - 1) + "' and facctcode in ('410301','401101','41040201')" +
						 " union all " +
						 " select null as acctA,sum(FBEndBal) as acctB from " + YssTabPrefix + "lbalance  " +
						 " where fmonth = '" + String.valueOf(iMonth - 1) + "' and (facctcode in ('410302','401102','6101','41040202')))";
				
				rsA = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
				while(rsA.next())
				{
					//20110720 modified by liubo.Story #1095
					//对取值进行判定，当取到NULL时不进行返回字符串的拼接，已实现无数据不显示科目
					//*********************************************
					if (rsA.getString("CNT") != null)
					{
						buffer.append("\r\n");
						buffer.append("\t");
						buffer.append("本年末可分配利润（期末数）").append("\t");
						buffer.append(YssFun.formatNumber(rsA.getDouble("CNT"), "#.00")).append("\t"); 
						buffer.append("\t \t\t\t\t0.00\t0.00\t\t\t\t0.00\t0.00\t\t0.00\t \t \t \t0\t"); 
					}
					//*********************end*********************
				}

				dbl.closeResultSetFinal(rsA);
			}
	
			return buffer.toString();
		
		}
		catch(Exception e)
		{
			throw new YssException(e);
		}
		finally
		{
			dbl.closeResultSetFinal(rsA);
//			dbl.closeResultSetFinal(rsB);
//			dbl.closeResultSetFinal(rsC);
//			dbl.closeResultSetFinal(rsD);
		}
	}

	private int getThisSetAccLen(String FSetCode, int FYear)
			throws YssException {
		ResultSet Rs = null;
		String sql = "";
		int YssStartMonth = 0;
		try {
			sql = "select facclen,fstartmonth From lsetlist where fsetcode="
					+ FSetCode + " and fyear=" + FYear;
			Rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			if (Rs.next()) {
				YssStartMonth = Rs.getInt("fstartmonth");
			}
			Rs.getStatement().close();
			Rs = null;
		} catch (SQLException ee) {
		} finally {
			try {
				if (Rs != null)
					Rs.getStatement().close();
			} catch (SQLException ex) {
			}
		}
		return YssStartMonth;
	}
	// ----------------------------
   /**
    * add by baopingping #story 1263 2011-07-28
    * 往TB_XXX_Rep_GuessValue表中添加“日净值增长率”
    * @throws YssException 
    *  
    */
	public void InsertUpdayValue(Date date,int fSetCode) throws YssException{
		StringBuffer buf=new StringBuffer();
		String SetCode=String.valueOf(fSetCode);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String TimeDate = sdf.format(date);
		double amtRate = getDateAmountRate(TimeDate, SetCode); // 计算得到日净值增长率
		String strSql=null;
			try {
				buf.append("insert into "+pub.yssGetTableName(sGVTableName)+" values (");
				buf.append(fSetCode+","+dbl.sqlDate(date)+","+"9991,' ', '日净值增长率',");
				buf.append("'日净值增长率','合计',0,0,0,"+amtRate+",0,0,0,0,0,0,0,"+amtRate);
				buf.append(",0,0,0,0,' ',0,0,' ')");
				strSql=buf.toString();
				dbl.executeSql(strSql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
	}
	
	/**
	 * add by baopingping #story 1263 2011-07-28
	 * 往TB_XXX_Rep_GuessValue表中添加“本期基金份额净值增长率”
	 * @throws YssException 
	 *  
	 */	   
	public void InsertAmountRate(Date date,int fSetCode) throws YssException{
		StringBuffer buf=new StringBuffer();
		String SetCode=String.valueOf(fSetCode);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String TimeDate = sdf.format(date);
		double amtRate = getAmountRate(TimeDate, SetCode);//得到本期基金份额净值增长率
		String strSql=null;
			try {
				buf.append("insert into "+pub.yssGetTableName(sGVTableName)+" values (");
				buf.append(fSetCode+","+dbl.sqlDate(date)+","+"9993,' ', '本期基金份额净值增长率',");
				buf.append("'本期基金份额净值增长率','合计',0,0,0,"+amtRate+",0,0,0,0,0,0,0,"+amtRate);
				buf.append(",0,0,0,0,' ',0,0,' ')");
				strSql=buf.toString();
				dbl.executeSql(strSql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
	}

	   /**
	    * add by baopingping #story 1263 2011-07-28
	    * 往TB_XXX_Rep_GuessValue表中添加“累计净值增长率”
	    * @throws YssException 
	    *  
	    */
	public void InsertAmont(Date date,int fSetcode) throws YssException{
		String strSql=null;
		StringBuffer buf=new StringBuffer();
		String SetCode=String.valueOf(fSetcode);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String TimeDate = sdf.format(date);
		double amtRate = getAllAmountRate(TimeDate, SetCode); // 得到累计净值增长率
		try{
			buf.append("insert into "+pub.yssGetTableName(sGVTableName)+" values (");
			buf.append(fSetcode+","+dbl.sqlDate(date)+","+"9992,' ', '累计净值增长率',");
			buf.append("'累计净值增长率','合计',0,0,0,"+amtRate+",0,0,0,0,0,0,0,"+amtRate);
			buf.append(",0,0,0,0,' ',0,0,' ')");
			strSql=buf.toString();
			dbl.executeSql(strSql);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	//20120228 added by liubo.Story #2248.通过前台传入的组合跟报表日期，获取每日确认表信息
	public String getReconcileInfo(String sParam) throws YssException
	{
		String strSql = "";
		ResultSet rs = null;
		YssFinance yfiance = new YssFinance();
		try
		{
			if (!dbl.yssTableExist("DayClosed"))
			{
				return "Failed";
			}
			
			String[] sParamList = sParam.split("\t");
			
			if(sParamList.length <3)
			{
				return "";
			}
			yfiance.setYssPub(pub);
			
			String sBookSetCode = getPortCode(sParamList[0], YssFun.formatDate(sParamList[1], "yyyy"));
			sBookSetCode = yfiance.getCWSetCode(sBookSetCode);
			
			strSql = "select count(*) as CNT from DayClosed where FSetCode = " + dbl.sqlString(sBookSetCode) + " and FClosedDate between " + dbl.sqlDate(sParamList[1]) + " and " + dbl.sqlDate(sParamList[2]);
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next())
			{
			
				if (rs.getInt("CNT") > 0)
				{
					return "Yes";
				}
				else
				{
					return "No";
				}
			}
			return "No";
			
		}
		catch(Exception ye)
		{
			throw new YssException("获取每日确认表出现错误：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
	}
	
	//20120228 added by liubo.Story #2248.通过前台传入的组合跟报表日期，操作每日确认表（插入或删除）
	public String ReconcileInfoOperation(String sParam) throws YssException
	{
		String strSql = "";
		YssFinance yfiance = new YssFinance();
		PortfolioBean port = new PortfolioBean();
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			String[] sParamList = sParam.split("\t");
			
			if(sParamList.length <4)
			{
				return "Failed";
			}
			
			yfiance.setYssPub(pub);
            port.setYssPub(pub);

			String sBookSetCode = getPortCode(sParamList[0], YssFun.formatDate(sParamList[1], "yyyy"));
			sBookSetCode = yfiance.getCWSetCode(sBookSetCode);

			strSql = "Delete from DayClosed where FSetCode = " + dbl.sqlString(sBookSetCode) + " and FClosedDate between " + dbl.sqlDate(sParamList[1]) + " and " + dbl.sqlDate(sParamList[2]);
			dbl.executeSql(strSql);
			
			if (sParamList[3].equals("Adding"))
			{
				strSql = "insert into DayClosed(FSetCode,FClosedDate,Fbzm,fdcbz,fdcuser,fDCtime,fType)" +
						 " select distinct " + dbl.sqlString(sBookSetCode) + " as FSetCode,FDATE as FClosedDate , 'DZ' as Fbzm,0 as fdcbz," + dbl.sqlString(pub.getUserCode()) + " as fdcuser," + 
						 dbl.sqlDate(sdf.format(dt)) + " as fDCtime,'估值表' as fType from " + pub.yssGetTableName("Tb_Rep_GuessValue") +
						 " where FDATE between " + dbl.sqlDate(sParamList[1]) + " and " + dbl.sqlDate(sParamList[2]);
				dbl.executeSql(strSql);
			}
			
			//20120416 added by liubo.Story #2439
			//在估值表做可对账\取消可对账操作时，想托管系统发出接口请求。这个步骤无论成功与否，不会影响QDII估值系统的操作
			//===================================
            
			Logger log =null;
			log = Logger.getLogger("stdout");
			com.yss.ciss.ws.client.ciss.ResponseMsg respon = null;
			try
			{
				port.setPortCode(yfiance.getPortCode(sParamList[0]));
	            port.getSetting();
	            
				CISSServiceForQDII ciss = new CISSServiceForQDII();
				ComparableStatusVO statusVO = new ComparableStatusVO();

//				((BindingProvider) ciss).getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, 30000);
//				((BindingProvider) ciss).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 30000);
				
				statusVO.setExecName(pub.getUserCode());
				statusVO.setMsgDate(YssFun.formatDate(sParamList[1],"yyyyMMdd") + "-" + YssFun.formatDate(sParamList[2],"yyyyMMdd"));
				statusVO.setProCode(port.getAssetCode());
				statusVO.setMsgType(sParamList[3].equals("Adding") ? "GZBDZ" : "GZBUNDZ");

				List<ComparableStatusVO> list = new ArrayList<ComparableStatusVO>();
				list.add(statusVO);
				
				respon = ciss.getCISSService().sendComparableStatus(list);
				log.info(respon.getStatus() + (respon.getStatus().trim().equals("0") ? respon.getRemark() : ""));
				


				
				System.out.println(respon.getStatus() + (respon.getStatus().trim().equals("0") ? respon.getRemark() : ""));
			}
			catch(Exception ye)
			{
				System.out.println("向托管系统提交“可对账\\取消可对账”请求失败：" + ye.getMessage());
				log.error("向托管系统提交“可对账\\取消可对账”请求失败：" + ye.getMessage());
			}
			//=================end==================
			
			return "";
		
		}
		catch(Exception ye)
		{
			throw new YssException("处理每日确认表出现错误：" + ye.getMessage());
		}
	}
	
	//20120228 added by liubo.Story #2248.通过前台传入的组合跟报表日期，返回平台锁定/解锁联动信息
	public String GetLockedStatus(String sParam) throws YssException
	{
		String strSql = "";
		ResultSet rs = null;
		YssFinance yfiance = new YssFinance();
		
		try
		{			
			String[] sParamList = sParam.split("\t");
		
			if(sParamList.length <3)
			{
				return "Failed";
			}
			
			yfiance.setYssPub(pub);
	
			String sBookSetCode = getPortCode(sParamList[0], YssFun.formatDate(sParamList[1], "yyyy"));
			sBookSetCode = yfiance.getCWSetCode(sBookSetCode);
			
			strSql = "select * from LockStatus where FSetCode in (Select FSetID from Lsetlist where trim(to_char(FsetCode,'000')) = " + dbl.sqlString(sBookSetCode) +
					 ") and ftime between " + dbl.sqlDate(sParamList[1]) + " and " + dbl.sqlDate(sParamList[2]);
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				if (rs.getString("fstatus").equals("1"))
				{
					return "Locked";
				}
				else
				{
					return "UnLocked";
				}
			}
			
			return "Locked";
			
		}
		catch(Exception ye)
		{
			throw new YssException("获取平台锁定/解锁联动信息出现错误：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
	}
	
	//20120301 added by liubo.Story #2248.获取某段时间内有无确认任何一天的估值表
	public String GetDayConfirms(String sParam) throws YssException
	{
		String strSql = "";
		ResultSet rs = null;
		
		try
		{			
			String[] sParamList = sParam.split("\t");
		
			if(sParamList.length <3)
			{
				return "Failed";
			}
			
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_GuessValue") + " where FPortCode = " + dbl.sqlString(sParamList[0]) +
					 " and FDate between " + dbl.sqlDate(sParamList[1]) + " and " + dbl.sqlDate(sParamList[2]) +
					 " and FAcctCode = 'C100' and FDesc like '%日终确认%'";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				return "YES";
			}
			
			return "NO";
			
		}
		catch(Exception ye)
		{
			throw new YssException("获取估值表确认信息出现错误：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
	}
	

    //20120301 added by liubo.Story #2235
    //获取打印估值表时的表尾描述信息
    //******************************
	public String GetGVReportDescription(String sParam) throws YssException
	{
		String strSql = "";
		String sConfirmed = " ";
		ResultSet rs = null;
		
		try
		{
			String sProducer  = pub.getUserName();
			String sReturn = "制表：" + pub.getUserName() + "|复核：|审核：                  ";	//初始化返回值的原始信息
			String[] sParamList = sParam.split("\t");
			
			if(sParamList.length <2)
			{
				return sReturn;
			}
			
			//报表制作人，是在Tb_XXX_Rep_GuessValue中插入的一条科目代号为D200的记录。该条记录的描述字段会有记录制表人的用户名
			//复核人，是在是在Tb_XXX_Rep_GuessValue中插入的一条科目代号为C100，同时FDESC字段带有“日终确认”记录（即每日确认估值表的记录。反确认估值表时会带有“日终凡确认”的字样）
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_GuessValue") + " where FDate = " + dbl.sqlDate(sParamList[1]) +
					 " and FPortCode = " + dbl.sqlString(sParamList[0]) +
					 " and (FACCTCODE = 'D200' or (FACCTCODE = 'C100' and FDESC like '%日终确认%'))";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				/**add---huhuichao 2013-9-10 BUG  9428   财务估值表打印预览报错  */
				//20120412 modified by liubo. Bug #4206
				//根据获取到的制表人和复核人用户代码，获取用户名称
				//==================================
				//delete by huhuichao Tb_XXX_Rep_GuessValue中科目代号为D200的记录，并不是记录报表制作人，此处直接取 当前用户就可以
//				if(rs.getString("FACCTCODE").equals("D200"))
//				{
//					sProducer = (rs.getString("FDESC").substring(0,rs.getString("FDESC").indexOf("估值表生成日期")));
//					sProducer = getUserName(sProducer);
//				}
				if(rs.getString("FACCTCODE").equals("C100"))
				{
					sConfirmed = (rs.getString("FDESC").substring(0,rs.getString("FDESC").indexOf("日终确认日期") - 1));
					sConfirmed = getUserName(sConfirmed);
				}
				//==============end====================
				/**end---huhuichao 2013-9-10 BUG 9428*/
			}
			
			sReturn = "制表：" + sProducer + "|复核：" + sConfirmed + "|审核：                  ";
			
			return sReturn;
		}
		catch(Exception ye)
		{
			throw new YssException("获取财务估值表报表描述出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
			
		}
		
	}
	
	//20120412 added by liubo. Bug #4206
	//根据传入的用户代码，获取用户名称。传入的用户代码为空，或者查询不到该用户代码的记录，返回空字符串
	private String getUserName(String sUserCode) throws YssException
	{
		String strSql = "";
		String sReturn = "";
		ResultSet rs = null;
		try
		{
			if(sUserCode == null || sUserCode.trim().equals(""))
			{
				return "";
			}
			
			strSql = "Select * from tb_sys_userlist where FUserCode = " + dbl.sqlString(sUserCode);
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				sReturn = rs.getString("FUserName");
				if(sReturn == null || sReturn.trim().equals(""))
				{
					sReturn = sUserCode;
				}
			}
			
			return sReturn;
		}
		catch(Exception ye)
		{
			throw new YssException("获取估值表表尾描述信息出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
	}
	
	/**
	 * 获取ETF组合补票方式
	 * T+1日确认净值，取消净值表一的逻辑，此种情况需要在净值表二中显示现金差额、最小申赎单位净值等
	 * QDV4易方达基金2011年7月27日01_A
	 * panjunfang add 20120308
	 * @param portCode
	 * @return
	 * @throws YssException
	 */
	private String getSupplyMode(String portCode) throws YssException{
		String supplyMode = "";
		StringBuffer buf=new StringBuffer();
		ResultSet rs = null;
		try{
			buf.append("select b.fsupplymode from (select fportcode from ")
				.append(pub.yssGetTableName("tb_para_portfolio"))
				.append(" where fportcode = ").append(dbl.sqlString(portCode))
				.append(" and fcheckstate = 1 and fsubassettype = '0106') a ")
				.append(" left join (select fportcode ,fsupplymode from ")
				.append(pub.yssGetTableName("tb_etf_param"))
				.append(" where fcheckstate = 1) b on a.fportcode = b.fportcode");
			rs = dbl.queryByPreparedStatement(buf.toString());
			if(rs.next()){
				supplyMode = rs.getString("fsupplymode");
			}
			if(null == supplyMode){
				supplyMode = "";
			}
		}catch(Exception e){
			throw new YssException("获取当前ETF组合对应补票方式出错！" + e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return supplyMode;
	}
	
	/**
	 * add by huangqirong 2012-09-04 story #2782
	 * */
	private void dealMclassValue(int setCode, Date date, int digit)throws YssException{		
		String sql = "";
		String gvSql = "";
		String classSql = "";
		PreparedStatement gvPst = null;
		PreparedStatement classPst = null;
		boolean isAuxiAcc=false;//财务表是否使用辅助核算项
		ResultSet rs = null;
		int iniMonth = 0;
		iniMonth = YssFun.toInt(YssFun.formatDate(YssFun.addMonth(date, -1), "MM")); // 取上一个月的日期
		if (YssFun.toInt(YssFun.formatDate(date, "MM")) == 1) {
			iniMonth = 0; // 判断，如果本月的日期为1,则上一个月的日期为0
		}
		//实收基金,损益平准金，本期利润，利润分配等权益类
		Map<String ,String> clsPortMap=new HashMap<String,String>();
		//List rootList=new ArrayList();
		double fendbal=0;
		double fbendbal=0;
		double faendbal=0;
		int flag=1;
		String facctcode="";//科目
		//String fcurcode="";//币种
		String fauxiacc="";//分级标示
		//基础汇率
		double baseRate = 1 ;
		//组合汇率
		double portRate = 1 ;
		
		double baseMoney = 0 ;
		
		String flagType = "资产净值";
		
		Hashtable hsUnitParentCode = new Hashtable();	//20121022 added by liubo.Bug #5997
		
		try{
			dbl.loadConnection().setAutoCommit(true); //设置自动提交事物
			
			sql = " delete from " + pub.yssGetTableName("Tb_Data_MultiClassNet") +
            " where FPortCode = " + dbl.sqlString(this.sPortCode) +
            " and FType <> 'zb'" +
            " and FNAVDate = " + dbl.sqlDate(date);      
			dbl.executeSql(sql);
			
			gvSql = "insert into "+ pub.yssGetTableName(sGVTableName)+"(FPORTCODE,FDATE,FACCTCODE,FCURCODE,FACCTNAME,FACCTATTR,FACCTCLASS,FEXCHANGERATE,FAMOUNT,FCOST,FSTANDARDMONEYCOST,FCOSTTONETRATIO,FSTANDARDMONEYCOSTTONETRATIO"
			   		+ ",FMARKETPRICE,FOTPRICE1,FOTPRICE2,FOTPRICE3,FMARKETVALUE,FSTANDARDMONEYMARKETVALUE,FMARKETVALUETORATIO,FSTANDARDMONEYMARKETVALUETORAT,FAPPRECIATION,FSTANDARDMONEYAPPRECIATION,FMARKETDESCRIBE,FACCTLEVEL,FACCTDETAIL,FDESC)"
			   		+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			gvPst = dbl.getPreparedStatement(gvSql);
			
			sql="select c.*,d.*,e.AuxiAccName,' ' as fcurcode from (select FAuxiAcc,kmdm,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,sum(faendbal) as faendbal ,Fcurcode as FbaseCuryCode from  ( "
			   +"select (case when a.FAuxiAcc is null then b.FAuxiAcc else a.FAuxiAcc end) as  FAuxiAcc,(case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else a.fcyid end) as fcurcode,"
		       +"(case when b.fendbal is null then 0 else b.fendbal end) - (case when fjje is null then 0 else fjje end) + (case when fdje is null then 0 else fdje end) as fendbal,"
			   +"(case when b.fbendbal is null then 0 else b.fbendbal end) - (case when fbjje is null then 0 else fbjje end) + (case when fbdje is null then 0 else fbdje end) as fbendbal,"
		       +"(case when b.faendbal is null then 0 else b.faendbal end) - (case when fjsl is null then 0 else fjsl end) + (case when fdsl is null then 0 else fdsl end) as faendbal"
			   +" from (select fkmh,fcyid,fterm,FAuxiAcc,sum(case when fjd = 'J' then fbal else 0 end) as fjje,"
		       +"sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,"
			   +"sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,"
		       +"sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "+YssTabPrefix+"fcwvch "
		       +" where fterm ="+ YssFun.formatDate(date, "MM")+" and  "+ dbl.sqlDay("FDate")+ " <= "+ YssFun.formatDate(date, "dd")
		       +" group by fkmh, fcyid, fterm,FAuxiAcc) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,-1 * fendbal as fendbal,fbaccdebit,fbacccredit,-1 * fbendbal as fbendbal,faaccdebit,-1 * faendbal as faendbal,FAuxiAcc from"
		       +"  "+YssTabPrefix+"Lbalance where fmonth ="+iniMonth+" and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode and a.FAuxiAcc=b.FAuxiAcc ) group by FAuxiAcc,kmdm ,fcurcode) c "
		       +" join (select facctcode, facctname, famount, facctattr, FAcctClass,FAcctLevel,FAcctParent from  "+YssTabPrefix+"Laccount where  fbalDC in (1, -1, 0) and  FAcctClass='权益类'"
		       +" and  FAcctDetail=1 ) d on c.kmdm = d.facctcode"
		       +" join  "+YssTabPrefix+"AuxiAccSet e on e.auxiaccid=c.FAuxiAcc where c.fbendbal<>0 order by c.FAuxiAcc,c.kmdm";
			rs=dbl.openResultSet(sql);
			while(rs.next()){
			    fauxiacc=rs.getString("FAuxiAcc");
				String rePortCls=rs.getString("FAuxiAcc").substring(2);//根据财务辅助核算项来找到所属组合分级
			   	
				if(!clsPortMap.containsKey(fauxiacc)){//根目录
					if(flag>1){//根据汇总的权益数据，来更新财务估值表
						
						baseMoney = YssD.round(YssD.div(
   									YssD.mul(fbendbal, portRate), 
   									baseRate
   									) ,2);
						
						sql="update "+pub.yssGetTableName(sGVTableName)+" set FMARKETVALUE="+ fbendbal 
						  +",FSTANDARDMONEYMARKETVALUE=" + baseMoney + ",FAMOUNT="+faendbal+",FACCTNAME='资产净值',FACCTATTR='资产净值'"
						  +" where FPORTCODE="+dbl.sqlString(Integer.toString(setCode))+" and FDATE="+dbl.sqlDate(date)
						  +" and FACCTCODE="+dbl.sqlString(facctcode);
						dbl.executeSql(sql);
					    fendbal=0;
						fbendbal=0;
					    faendbal=0;
					}
					
					//基础汇率
					baseRate = this.getSettingOper().getCuryRate(
				  				  date, 
				  				  this.getPortClsData(this.sPortCode , rs.getString("FPORTCLSCODE") , "FCuryCode"  , true ), 
				  				  this.sPortCode, 
				  				  YssOperCons.YSS_RATE_BASE);
				   	//组合汇率
				   	portRate = this.getSettingOper().getCuryRate(
				  				  date, 
				  				  "", 
				  				  sPortCode, 
				  				  YssOperCons.YSS_RATE_PORT); 
				   	
					facctcode=rs.getString("AuxiAccName").substring(0,1)+rs.getString("FAcctParent");
					//fcurcode=rs.getString("fcurcode");
					setPstAttr(gvPst, rs, true, setCode, date, digit,rePortCls , true , baseRate , portRate );
					
					//20121022 modified by liubo.Bug #5997
					//实际运作过程中实收基金科目的级数是不固定的，需要将其调整为灵活获取相关科目
					//======================================
					if (rs.getString("FAcctAttr").equalsIgnoreCase("实收资本"))
					{
						if (hsUnitParentCode.get(rePortCls) == null)	
						{
							hsUnitParentCode.put(rePortCls, rePortCls+rs.getString("FAcctParent"));
						}
					}
					//==================end====================
					
					clsPortMap.put(fauxiacc, fauxiacc);
				}
				fendbal=YssD.add(rs.getDouble("fendbal"), fendbal);//原币
				fbendbal=YssD.add(rs.getDouble("fbendbal"), fbendbal);//本位币
				faendbal=YssD.add(rs.getDouble("faendbal"), faendbal);//数量
				
				setPstAttr(gvPst, rs, false, setCode, date, digit,rePortCls , true , baseRate , portRate );
				
				flag++;
				isAuxiAcc = true;
			}
			if(flag>1){//最后一项的保存
				baseMoney = YssD.round(YssD.div(
							YssD.mul(fbendbal, portRate), 
							baseRate
							),2);
				sql="update "+pub.yssGetTableName(sGVTableName)+" set FMARKETVALUE="+fbendbal
						  +",FSTANDARDMONEYMARKETVALUE="+ baseMoney +",FAMOUNT="+faendbal+",FACCTNAME='资产净值',FACCTATTR='资产净值'"
						  +" where FPORTCODE="+dbl.sqlString(Integer.toString(setCode))+" and FDATE="+dbl.sqlDate(date)
						  +" and FACCTCODE="+dbl.sqlString(facctcode);
						dbl.executeSql(sql);
			}
			dbl.closeResultSetFinal(rs);
			
			if(!isAuxiAcc){//没用使用辅助核算项
				flag=1;
				fendbal=0;
				fbendbal=0;
				faendbal=0;
				sql="select d.FPORTCLSCODE || ' Class' as AuxiAccName,c.*,d.*,' ' as fcurcode from (select kmdm,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,sum(faendbal) as faendbal from  ( "
					   +"select (case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else a.fcyid end) as fcurcode,"
					   +"(case when b.fendbal is null then 0 else b.fendbal end) - (case when fjje is null then 0 else fjje end) + (case when fdje is null then 0 else fdje end) as fendbal,"
					   +"(case when b.fbendbal is null then 0 else b.fbendbal end) - (case when fbjje is null then 0 else fbjje end) + (case when fbdje is null then 0 else fbdje end) as fbendbal,"
					   +"(case when b.faendbal is null then 0 else b.faendbal end) - (case when fjsl is null then 0 else fjsl end) + (case when fdsl is null then 0 else fdsl end) as faendbal"
					   +" from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,"
					   +"sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,"
					   +"sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,"
					   +"sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "+YssTabPrefix+"fcwvch "
					   +" where fterm ="+ YssFun.formatDate(date, "MM")+" and  "+ dbl.sqlDay("FDate")+ " <= "+ YssFun.formatDate(date, "dd")
					   +" group by fkmh, fcyid, fterm) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,-1 * fendbal as fendbal,fbaccdebit,fbacccredit,-1 * fbendbal as fbendbal,faaccdebit,-1 * faendbal as faendbal,FAuxiAcc from"
					   +"  "+YssTabPrefix+"Lbalance where fmonth ="+iniMonth+" and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) group by kmdm ) c "
					   +" join (select facctcode, facctname, famount, facctattr, FAcctClass,FAcctLevel,FAcctParent,FPORTCLSCODE from  "+YssTabPrefix+"Laccount where  fbalDC in (1, -1, 0) and  FAcctClass='权益类'"
					   +" and  FAcctDetail=1 ) d on c.kmdm = d.facctcode  where c.fbendbal<>0  and  d.FPORTCLSCODE<>' ' order by d.FPORTCLSCODE,c.kmdm";
				rs = dbl.openResultSet(sql);
				while(rs.next()){
				    fauxiacc=rs.getString("FPORTCLSCODE").trim();
					String rePortCls=rs.getString("FPORTCLSCODE");
				   	
					if(!clsPortMap.containsKey(fauxiacc)){//根目录
						if(flag>1){//根据汇总的权益数据，来更新财务估值表
							baseMoney = YssD.round(YssD.div(
			   								YssD.mul(fbendbal, portRate), 
			   								baseRate
	   									),
	   									2);
							
							sql="update "+pub.yssGetTableName(sGVTableName)+" set FMARKETVALUE="+fbendbal
							  +",FSTANDARDMONEYMARKETVALUE=" + baseMoney + ",FAMOUNT="+faendbal+",FACCTNAME='资产净值',FACCTATTR='资产净值'"
							  +" where FPORTCODE="+dbl.sqlString(Integer.toString(setCode))+" and FDATE="+dbl.sqlDate(date)
							  +" and FACCTCODE="+dbl.sqlString(facctcode);
							dbl.executeSql(sql);
						    fendbal=0;
							fbendbal=0;
						    faendbal=0;
						}
						
						//基础汇率
						baseRate = this.getSettingOper().getCuryRate(
					  				  date, 
					  				  this.getPortClsData(sPortCode , rs.getString("FPORTCLSCODE") , "FCuryCode" , true ), 
					  				  this.sPortCode, 
					  				  YssOperCons.YSS_RATE_BASE);
					   	//组合汇率
					   	portRate = this.getSettingOper().getCuryRate(
					  				  date, 
					  				  "", 
					  				  sPortCode, 
					  				  YssOperCons.YSS_RATE_PORT);
						
						facctcode=rs.getString("FPORTCLSCODE")+rs.getString("FAcctParent");
						//fcurcode=rs.getString("fcurcode");
						this.setPstAttr(gvPst, rs, true, setCode, date, digit, rePortCls , true , baseRate , portRate );
						
						//20121022 modified by liubo.Bug #5997
						//实际运作过程中实收基金科目的级数是不固定的，需要将其调整为灵活获取相关科目
						//======================================
						if (rs.getString("FAcctAttr").equalsIgnoreCase("实收资本"))
						{
							if (hsUnitParentCode.get(rePortCls) == null)	
							{
								hsUnitParentCode.put(rePortCls, rePortCls+rs.getString("FAcctParent"));
							}
						}
						//================end======================
						
						clsPortMap.put(fauxiacc, fauxiacc);
					}
					fendbal=YssD.add(rs.getDouble("fendbal"), fendbal);//原币
					fbendbal=YssD.add(rs.getDouble("fbendbal"), fbendbal);//本位币
					faendbal=YssD.add(rs.getDouble("faendbal"), faendbal);//数量
					this.setPstAttr(gvPst, rs, false, setCode, date, digit,rePortCls , true , baseRate , portRate );
					flag++;
				}
				if(flag>1){//最后一项的保存
					baseMoney = YssD.round(YssD.div(
									YssD.mul(fbendbal, portRate), 
									baseRate
								),
								2);
					sql="update "+pub.yssGetTableName(sGVTableName)+" set FMARKETVALUE="+fbendbal
							  +",FSTANDARDMONEYMARKETVALUE="+baseMoney+",FAMOUNT="+faendbal+",FACCTNAME='资产净值',FACCTATTR='资产净值'"
							  +" where FPORTCODE="+dbl.sqlString(Integer.toString(setCode))+" and FDATE="+dbl.sqlDate(date)
							  +" and FACCTCODE="+dbl.sqlString(facctcode);
							dbl.executeSql(sql);
				}
			}			
			dbl.closeResultSetFinal(rs);
			
			//分级资产净值
			sql = " select rgv.*,tcfdg.* from (" +
				  " select substr(gv.facctclass,7) as FAcctClassCode,gv.FmarketValue as FmarketValue," +
				  " gv.Fstandardmoneymarketvalue as Fstandardmoneymarketvalue from " +pub.yssGetTableName(sGVTableName)+ 
				  " gv where FPORTCODE = " + dbl.sqlString(Integer.toString(setCode)) + 
				  " and FDATE = " + dbl.sqlDate(date) +
				  " and facctclass like '%class%' and FACCTATTR = " + dbl.sqlString(flagType) + " ) rgv " +
				  " left join " +
				  " ( " +
				  " select * from " + pub.yssGetTableName("tb_ta_portcls") + " tp " +
				  " ) ta " +
				  " on rgv.FAcctClassCode = ta.fportclsrank and ta.fportcode = " + dbl.sqlString(this.sPortCode) +
				  
				  " left join ( " + 
				  " select tcfdg1.* from (" +
				  " select FPORTCLSCODE, FPORTCODE, FCURYCODE ,max(FSTARTDATE) as FSTARTDATE " + 
                  " from " + pub.yssGetTableName("TB_Ta_Classfunddegree")+ " where FCHECKSTATE = 1 " +
                  " group by FPORTCLSCODE, FPORTCODE, FCURYCODE ) tcfdg1 " +
                  " left join " +
                  	pub.yssGetTableName("TB_Ta_Classfunddegree") +
                  " tcfdg2 on tcfdg1.FPORTCODE = tcfdg2.FPORTCODE " +
                  " and tcfdg1.FPORTCLSCODE = tcfdg2.FPORTCLSCODE " + 
                  " and tcfdg1.FCURYCODE = tcfdg2.FCURYCODE " +
                  " and tcfdg1.FSTARTDATE = tcfdg2.FSTARTDATE " +
				  " ) tcfdg " +
				  " on tcfdg.fportcode = " + dbl.sqlString(this.sPortCode)+
				  " and ta.FPORTCLSCODE = tcfdg.FPORTCLSCODE ";
			
			classSql = " insert into " + pub.yssGetTableName("Tb_Data_MultiClassNet") +
			       " (FNAVDate,FPortCode,FType,FPortCuryCode,FCuryCode,FNetValueBeforeFee,FManageFee,FTrusteeFee," +
			       " FNetValue,FClassNetValue,FCheckState,FCreator,FCreateTime,FSTFee,FFXJFee) " + 
			       " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "; 
			classPst = dbl.getPreparedStatement(classSql);
			
			rs = dbl.openResultSet(sql);
			//modify by fangjiang story 3264 2012.12.13
			while(rs.next()){				
				classPst.setDate(1, YssFun.toSqlDate(date));
				classPst.setString(2, this.sPortCode);
				classPst.setString(3, "01");
				classPst.setString(4, "CNY");
				classPst.setString(5, rs.getString("FPORTCLSCODE"));
				classPst.setDouble(6, 0.0);
				classPst.setDouble(7 ,0.0);
				classPst.setDouble(8, 0.0);				
				classPst.setDouble(9, YssD.round(rs.getDouble("Fstandardmoneymarketvalue"),2));
				classPst.setDouble(10,YssD.round(rs.getDouble("Fstandardmoneymarketvalue"),2));
				classPst.setInt(11,1);
				classPst.setString(12, pub.getUserCode());
				classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				classPst.setDouble(14, 0);
				classPst.setDouble(15, 0);
				classPst.executeUpdate();
				
				classPst.setDate(1, YssFun.toSqlDate(date));
				classPst.setString(2, this.sPortCode);
				classPst.setString(3, "011");
				classPst.setString(4, "CNY");
				classPst.setString(5, rs.getString("FPORTCLSCODE"));
				classPst.setDouble(6, 0.0);
				classPst.setDouble(7 ,0.0);
				classPst.setDouble(8, 0.0);				
				classPst.setDouble(9, YssD.round(rs.getDouble("FmarketValue"),2));
				classPst.setDouble(10,YssD.round(rs.getDouble("FmarketValue"),2));
				classPst.setInt(11,1);
				classPst.setString(12, pub.getUserCode());
				classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				classPst.setDouble(14, 0);
				classPst.setDouble(15, 0);
				classPst.executeUpdate();				
			}
			dbl.closeResultSetFinal(rs);
			
			sql = " select fportclscode,facctattr,sum(fbdje) as fbdje, sum(fbjje) as fbjje from (" +
				  " select a.*, b.FPORTCLSCODE as fportclsrank, b.facctattr,c.fportclscode " +
				  "	  from (select fkmh, " +
				  "             fcyid, " +
				  "             fterm," +
				  "             sum(case" +
				  "                   when fjd = 'J' then" +
				  "                    fbal" +
				  "                   else" +
				  "                    0" +
				  "                 end) as fjje," +
				  "             sum(case" +
				  "                   when fjd = 'D' then" +
				  "                    fbal" +
				  "                   else" +
				  "                    0" +
				  "                 end) as fdje," +
				  "             sum(case" +
				  "                   when fjd = 'J' then" +
				  "                    fsl" +
				  "                   else" +
				  "                    0" +
				  "                 end) as fjsl," +
				  "             sum(case" +
				  "                   when fjd = 'D' then" +
				  "                    fsl" +
				  "                   else" +
				  "                    0" +
				  "                 end) as fdsl," +
				  "             sum(case" +
				  "                   when fjd = 'J' then" +
				  "                    fbbal" +
				  "                   else" +
				  "                    0" +
				  "                 end) as fbjje," +
				  "             sum(case" +
				  "                   when fjd = 'D' then" +
				  "                    fbbal" +
				  "                   else" +
				  "                    0" +
				  "                 end) as fbdje," +
				  "             sum(case" +
				  "                   when fjd = 'J' then" +
				  "                    fbsl" +
				  "                   else" +
				  "                    0" +
				  "                 end) as fbjsl," +
				  "             sum(case" +
				  "                   when fjd = 'D' then" +
				  "                    fbsl" +
				  "                   else" +
				  "                    0" +
				  "                 end) as fbdsl" +
				  "        from " + YssTabPrefix + "fcwvch" +
				  "       where fterm = " + YssFun.formatDate(date, "MM") +
				  "        and  "+ dbl.sqlDay("FDate")+ " = "+ YssFun.formatDate(date, "dd") +
				  "        and fpzly <> 'DBZJZ' and fkmh like '4011%'" +
				  "       group by fkmh, fcyid, fterm) a" +
				  "   join (select facctcode, FPORTCLSCODE, substr(facctattr,1,9) as facctattr" +
				  "        from " + YssTabPrefix + "Laccount" +
				  "       where fbalDC in (1, -1, 0)" +
				  "         and FAcctClass = '权益类'" +
				  "         and FAcctDetail = 1" +
				  "         and FPORTCLSCODE <> ' ') b on a.fkmh = b.facctcode" +
				  "   join (select fportclscode, fportclsrank from " + pub.yssGetTableName("tb_ta_portcls") +
				  "    where fcheckstate = 1) c on b.fportclscode = c.fportclsrank " +
				  " ) group by fportclscode, facctattr ";
			rs = dbl.openResultSet(sql);
			while(rs.next()){				
				classPst.setDate(1, YssFun.toSqlDate(date));
				classPst.setString(2, this.sPortCode);
				if("损益平准金_未实现".equals(rs.getString("facctattr"))){
					classPst.setString(3, "03");
				}else{
					classPst.setString(3, "04");
				}
				classPst.setString(4, "CNY");
				classPst.setString(5, rs.getString("FPORTCLSCODE"));
				classPst.setDouble(6, 0.0);
				classPst.setDouble(7 ,0.0);
				classPst.setDouble(8, 0.0);				
				classPst.setDouble(9, YssD.sub(rs.getDouble("fbdje"), rs.getDouble("fbjje")));
				classPst.setDouble(10, YssD.sub(rs.getDouble("fbdje"), rs.getDouble("fbjje")));
				classPst.setInt(11,1);
				classPst.setString(12, pub.getUserCode());
				classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				classPst.setDouble(14, 0);
				classPst.setDouble(15, 0);
				classPst.executeUpdate();
			}
			dbl.closeResultSetFinal(rs);
			
			//实收资本	
			flagType = "实收资本";						
			sql = " select rgv.*,tcfdg.* from (" +
				  " select substr(gv.facctclass,7) as FAcctClassCode," +
				  " gv.fportcode as FassetCode, " +
				  " gv.famount as famount," +
				  " gv.FmarketValue as FmarketValue," +
				  " gv.Fstandardmoneymarketvalue as Fstandardmoneymarketvalue from " +pub.yssGetTableName(sGVTableName)+ 
				  " gv where FPORTCODE = " + dbl.sqlString(Integer.toString(setCode)) + 
				  " and FDATE = " + dbl.sqlDate(date) +
				  " and facctclass like '%class%' " +
				  " and FACCTATTR = " + dbl.sqlString(flagType) + " ) rgv " +
				  " left join " +
				  " ( " +
				  " select * from " + pub.yssGetTableName("tb_ta_portcls") + " tp " +
				  " ) ta " +
				  " on rgv.FAcctClassCode = ta.fportclsrank and ta.fportcode = " + dbl.sqlString(this.sPortCode) +
				  
				  " left join ( " + 
				  " select tcfdg1.* from (" +
				  " select FPORTCLSCODE, FPORTCODE, FCURYCODE ,max(FSTARTDATE) as FSTARTDATE " + 
                  " from " + pub.yssGetTableName("TB_Ta_Classfunddegree")+ " where FCHECKSTATE = 1 " +
                  " group by FPORTCLSCODE, FPORTCODE, FCURYCODE ) tcfdg1 " +
                  " left join " +
                  	pub.yssGetTableName("TB_Ta_Classfunddegree") +
                  " tcfdg2 on tcfdg1.FPORTCODE = tcfdg2.FPORTCODE " +
                  " and tcfdg1.FPORTCLSCODE = tcfdg2.FPORTCLSCODE " + 
                  " and tcfdg1.FCURYCODE = tcfdg2.FCURYCODE " +
                  " and tcfdg1.FSTARTDATE = tcfdg2.FSTARTDATE " +
				  " ) tcfdg " +
				  " on tcfdg.fportcode = " + dbl.sqlString(this.sPortCode)+
				  " and ta.FPORTCLSCODE = tcfdg.FPORTCLSCODE ";
			rs = dbl.queryByPreparedStatement(sql); 				
			while (rs.next()) {
				//start modify by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数
				CtlPubPara pubPara = new CtlPubPara();
			   	pubPara.setYssPub(this.pub);
				String digits = pubPara.getDigitsPortMethod("PubParaUnitCls","dayfinish","CtlPubParaUnitCls","portClsSel","txtdigit",rs.getString("FPORTCLSCODE"),"3");			   	
				digit = YssFun.toInt(digits);
				//end modify by huangqirong 2013-06-18 bug #8330 通参控制显示保留位数
				classPst.setDate(1, YssFun.toSqlDate(date));
				classPst.setString(2, this.sPortCode);
				classPst.setString(3, "05");
				classPst.setString(4, "CNY");
				classPst.setString(5, rs.getString("FPORTCLSCODE"));
				classPst.setDouble(6, 0.0);
				classPst.setDouble(7 ,0.0);
				classPst.setDouble(8, 0.0);				
				classPst.setDouble(9, YssD.round(rs.getDouble("famount"),2));
				classPst.setDouble(10, YssD.round(rs.getDouble("Fstandardmoneymarketvalue"),2));
				classPst.setInt(11,1);
				classPst.setString(12, pub.getUserCode());
				classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				classPst.setDouble(14, 0);
				classPst.setDouble(15, 0);
				classPst.executeUpdate();					

				//单位净值
				
				//20121022 modified by liubo.Bug #5997
				//实际运作过程中实收基金科目的级数是不固定的，需要将其调整为灵活获取相关科目
				//======================================
//				double portclsNetValue = this.getAssetNetValue(date, "" + setCode , rs.getString("FAcctClassCode") + "4001", "Fstandardmoneymarketvalue"); //分级资产净值		
				double portclsNetValue = this.getAssetNetValue(date, "" + setCode , (String)(hsUnitParentCode.get(rs.getString("FAcctClassCode")) == null ? "4001" : hsUnitParentCode.get(rs.getString("FAcctClassCode"))), "Fstandardmoneymarketvalue");
				//====================end==================
				
				double unitNetValue = YssD.round(YssD.div(portclsNetValue, rs.getDouble("famount")) , digit); 
				
				gvPst.setString(1,rs.getString("FassetCode") );
				gvPst.setDate(2,new java.sql.Date(date.getTime()));
				gvPst.setString(3, rs.getString("FAcctClassCode")+"9600");
				gvPst.setString(4, rs.getString("fcurycode"));
				gvPst.setString(5, "单位净值");
				gvPst.setString(6, "单位净值");
				gvPst.setString(7, "合计");//标志按照基准资产份额分级  "class"+"\t"+rs.getString("FAcctClassCode")
				gvPst.setDouble(8, 0);
				gvPst.setDouble(9, 0);
				gvPst.setDouble(10, 0);
				gvPst.setDouble(11, 0);
				gvPst.setDouble(12, 0);
				gvPst.setDouble(13, 0);
				gvPst.setDouble(14, 0);
				gvPst.setDouble(15, 0);
				gvPst.setDouble(16, 0);
				gvPst.setDouble(17, 0);						
				gvPst.setDouble(18, 0);//原币市值
				gvPst.setDouble(19, unitNetValue);//本位币市值
				gvPst.setDouble(20, 0);
				gvPst.setDouble(21, 0);
				gvPst.setDouble(22, 0);
				gvPst.setDouble(23, 0);
				gvPst.setString(24, " ");
				gvPst.setInt(25, 0);
				gvPst.setInt(26,0);
				gvPst.setString(27, " ");
				gvPst.executeUpdate();
				
				classPst.setDate(1, YssFun.toSqlDate(date));
				classPst.setString(2, this.sPortCode);
				classPst.setString(3, "02");
				classPst.setString(4, "CNY");
				classPst.setString(5, rs.getString("FPORTCLSCODE"));
				classPst.setDouble(6, 0.0);
				classPst.setDouble(7, 0.0);
				classPst.setDouble(8, 0.0);				
				classPst.setDouble(9, 0.0);
				classPst.setDouble(10, unitNetValue); //单位净值
				classPst.setInt(11,1);
				classPst.setString(12, pub.getUserCode());
				classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				classPst.setDouble(14, 0);
				classPst.setDouble(15, 0);
				classPst.executeUpdate();				
				
				//累计单位净值
				CtlNavRep navRep = new CtlNavRep();
				navRep.setYssPub(this.pub);
				navRep.setPortCode(this.sPortCode);
				
				//double sumDivided = YssD.round(YssD.add(unitNetValue, navRep.getSumDivided(date ,rs.getString("FPORTCLSCODE"))),digit); // 无基金分级拆分
				double sumDivided = YssD.round(getPortClsSplitUnit(unitNetValue , date ,this.sPortCode, rs.getString("FPORTCLSCODE")),digit); //考虑基金分级拆分
				
				gvPst.setString(1,rs.getString("FassetCode") );
				gvPst.setDate(2,new java.sql.Date(date.getTime()));
				gvPst.setString(3, rs.getString("FAcctClassCode")+"9612");
				gvPst.setString(4, rs.getString("fcurycode"));
				gvPst.setString(5, "累计单位净值");
				gvPst.setString(6, "累计单位净值");
				gvPst.setString(7, "合计");//标志按照基准资产份额分级  "class"+"\t"+rs.getString("FAcctClassCode")
				gvPst.setDouble(8, 0);
				gvPst.setDouble(9, 0);
				gvPst.setDouble(10, 0);
				gvPst.setDouble(11, 0);
				gvPst.setDouble(12, 0);
				gvPst.setDouble(13, 0);
				gvPst.setDouble(14, 0);
				gvPst.setDouble(15, 0);
				gvPst.setDouble(16, 0);
				gvPst.setDouble(17, 0);						
				gvPst.setDouble(18, 0);//原币市值
				gvPst.setDouble(19, sumDivided);//本位币市值
				gvPst.setDouble(20, 0);
				gvPst.setDouble(21, 0);
				gvPst.setDouble(22, 0);
				gvPst.setDouble(23, 0);
				gvPst.setString(24, " ");
				gvPst.setInt(25, 0);
				gvPst.setInt(26,0);
				gvPst.setString(27, " ");
				gvPst.executeUpdate();
				
				classPst.setDate(1,  YssFun.toSqlDate(date));
				classPst.setString(2, this.sPortCode);
				classPst.setString(3, "08");
				classPst.setString(4, "CNY");
				classPst.setString(5, rs.getString("FPORTCLSCODE"));
				classPst.setDouble(6, 0.0);
				classPst.setDouble(7 ,0.0);
				classPst.setDouble(8, 0.0);				
				classPst.setDouble(9, 0.0);
				classPst.setDouble(10, sumDivided); //
				classPst.setInt(11,1);
				classPst.setString(12, pub.getUserCode());
				classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				classPst.setDouble(14, 0);
				classPst.setDouble(15, 0);
				classPst.executeUpdate();
				
				//add by fangjiang story 3246 2012.12.13
				//基础汇率
				baseRate = this.getSettingOper().getCuryRate(
			  				  date, 
			  				  this.getPortClsData(sPortCode, rs.getString("FAcctClassCode"), "FCuryCode", true ), 
			  				  this.sPortCode, 
			  				  YssOperCons.YSS_RATE_BASE);
			   	//组合汇率
			   	portRate = this.getSettingOper().getCuryRate(
			  				  date, 
			  				  "", 
			  				  sPortCode, 
			  				  YssOperCons.YSS_RATE_PORT);
			   	
			   	classPst.setDate(1,  YssFun.toSqlDate(date));
				classPst.setString(2, this.sPortCode);
				classPst.setString(3, "081");
				classPst.setString(4, "CNY");
				classPst.setString(5, rs.getString("FPORTCLSCODE"));
				classPst.setDouble(6, 0.0);
				classPst.setDouble(7 ,0.0);
				classPst.setDouble(8, 0.0);				
				classPst.setDouble(9, 0.0);
				classPst.setDouble(10, YssD.round(YssD.div(YssD.mul(sumDivided, baseRate), portRate), digit)); //
				classPst.setInt(11,1);
				classPst.setString(12, pub.getUserCode());
				classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				classPst.setDouble(14, 0);
				classPst.setDouble(15, 0);
				classPst.executeUpdate();
				//end by fangjiang story 3246 2012.12.13
				
				//单位净值（本币计价）
				double clsNetValue = this.getAssetNetValue(date, "" + setCode , rs.getString("FAcctClassCode") + "4001", "Fmarketvalue"); //分级资产净值	
				double unitNetValueBB = YssD.round(YssD.div(clsNetValue, rs.getDouble("famount")) , digit); 
				
				gvPst.setString(1,rs.getString("FassetCode") );
				gvPst.setDate(2,new java.sql.Date(date.getTime()));
				gvPst.setString(3, rs.getString("FAcctClassCode")+"9602");
				gvPst.setString(4, rs.getString("fcurycode"));
				gvPst.setString(5, "单位净值（本币计价）");
				gvPst.setString(6, "单位净值（本币计价）");
				gvPst.setString(7, "合计");
				gvPst.setDouble(8, 0);
				gvPst.setDouble(9, 0);
				gvPst.setDouble(10, 0);
				gvPst.setDouble(11, 0);
				gvPst.setDouble(12, 0);
				gvPst.setDouble(13, 0);
				gvPst.setDouble(14, 0);
				gvPst.setDouble(15, 0);
				gvPst.setDouble(16, 0);
				gvPst.setDouble(17, 0);						
				gvPst.setDouble(18, 0);//原币市值
				gvPst.setDouble(19, unitNetValueBB);//本位币市值
				gvPst.setDouble(20, 0);
				gvPst.setDouble(21, 0);
				gvPst.setDouble(22, 0);
				gvPst.setDouble(23, 0);
				gvPst.setString(24, " ");
				gvPst.setInt(25, 0);
				gvPst.setInt(26,0);
				gvPst.setString(27, " ");
				gvPst.executeUpdate();
				
				classPst.setDate(1, YssFun.toSqlDate(date));
				classPst.setString(2, this.sPortCode);
				classPst.setString(3, "021");
				classPst.setString(4, "CNY");
				classPst.setString(5, rs.getString("FPORTCLSCODE"));
				classPst.setDouble(6, 0.0);
				classPst.setDouble(7 ,0.0);
				classPst.setDouble(8, 0.0);				
				classPst.setDouble(9, 0.0);
				classPst.setDouble(10, unitNetValueBB); //单位净值
				classPst.setInt(11,1);
				classPst.setString(12, pub.getUserCode());
				classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				classPst.setDouble(14, 0);
				classPst.setDouble(15, 0);
				classPst.executeUpdate();	
				
				//日净值增长率
				double rjzzzl = this.getDateAmountRate(date, unitNetValue, rs.getString("FPORTCLSCODE")); 
				
				gvPst.setString(1,rs.getString("FassetCode") );
				gvPst.setDate(2,new java.sql.Date(date.getTime()));
				gvPst.setString(3, rs.getString("FAcctClassCode")+"9991");
				gvPst.setString(4, rs.getString("fcurycode"));
				gvPst.setString(5, "日净值增长率");
				gvPst.setString(6, "日净值增长率");
				gvPst.setString(7, "合计");
				gvPst.setDouble(8, 0);
				gvPst.setDouble(9, 0);
				gvPst.setDouble(10, 0);
				gvPst.setDouble(11, 0);
				gvPst.setDouble(12, 0);
				gvPst.setDouble(13, 0);
				gvPst.setDouble(14, 0);
				gvPst.setDouble(15, 0);
				gvPst.setDouble(16, 0);
				gvPst.setDouble(17, 0);						
				gvPst.setDouble(18, rjzzzl);//原币市值
				gvPst.setDouble(19, rjzzzl);//本位币市值
				gvPst.setDouble(20, 0);
				gvPst.setDouble(21, 0);
				gvPst.setDouble(22, 0);
				gvPst.setDouble(23, 0);
				gvPst.setString(24, " ");
				gvPst.setInt(25, 0);
				gvPst.setInt(26,0);
				gvPst.setString(27, " ");
				gvPst.executeUpdate();
				
				classPst.setDate(1, YssFun.toSqlDate(date));
				classPst.setString(2, this.sPortCode);
				classPst.setString(3, "09");
				classPst.setString(4, "CNY");
				classPst.setString(5, rs.getString("FPORTCLSCODE"));
				classPst.setDouble(6, 0.0);
				classPst.setDouble(7 ,0.0);
				classPst.setDouble(8, 0.0);				
				classPst.setDouble(9, 0.0);
				classPst.setDouble(10, rjzzzl); 
				classPst.setInt(11,1);
				classPst.setString(12, pub.getUserCode());
				classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				classPst.setDouble(14, 0);
				classPst.setDouble(15, 0);
				classPst.executeUpdate();
			}
			
			isAuxiAcc = false;
			
			sql=" select FAuxiacc ,sum(Fendbal) as Fendbal , sum(fbendbal) as fbendbal , sum(faendbal) as faendbal from ( " +
				"select c.*,d.*,e.AuxiAccName,' ' as fcurcode from (select FAuxiAcc,kmdm,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,sum(faendbal) as faendbal ,Fcurcode as FbaseCuryCode from  ( "
				   +"select (case when a.FAuxiAcc is null then b.FAuxiAcc else a.FAuxiAcc end) as  FAuxiAcc,(case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else a.fcyid end) as fcurcode,"
			       +"(case when b.fendbal is null then 0 else b.fendbal end) - (case when fjje is null then 0 else fjje end) + (case when fdje is null then 0 else fdje end) as fendbal,"
				   +"(case when b.fbendbal is null then 0 else b.fbendbal end) - (case when fbjje is null then 0 else fbjje end) + (case when fbdje is null then 0 else fbdje end) as fbendbal,"
			       +"(case when b.faendbal is null then 0 else b.faendbal end) - (case when fjsl is null then 0 else fjsl end) + (case when fdsl is null then 0 else fdsl end) as faendbal"
				   +" from (select fkmh,fcyid,fterm,FAuxiAcc,sum(case when fjd = 'J' then fbal else 0 end) as fjje,"
			       +"sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,"
				   +"sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,"
			       +"sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "+YssTabPrefix+"fcwvch "
			       +" where fterm ="+ YssFun.formatDate(date, "MM")+" and  "+ dbl.sqlDay("FDate")+ " <= "+ YssFun.formatDate(date, "dd")
			       +" group by fkmh, fcyid, fterm,FAuxiAcc) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,-1 * fendbal as fendbal,fbaccdebit,fbacccredit,-1 * fbendbal as fbendbal,faaccdebit,-1 * faendbal as faendbal,FAuxiAcc from"
			       +"  "+YssTabPrefix+"Lbalance where fmonth ="+iniMonth+" and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode and a.FAuxiAcc=b.FAuxiAcc ) group by FAuxiAcc,kmdm ,fcurcode) c "
			       +" join (select facctcode, facctname, famount, facctattr, FAcctClass,FAcctLevel,FAcctParent from  "+YssTabPrefix+"Laccount where  fbalDC in (1, -1, 0) and  FAcctClass='权益类' and (FAcctName like '%未实现%' or Facctattr like '%未实现%') "
			       +" and  FAcctDetail=1 ) d on c.kmdm = d.facctcode"
			       +" join  "+YssTabPrefix+"AuxiAccSet e on e.auxiaccid=c.FAuxiAcc where c.fbendbal<>0 order by c.FAuxiAcc,c.kmdm"
				   + " ) group by Fauxiacc ";
				rs=dbl.openResultSet(sql);
				while(rs.next()){
					classPst.setDate(1,  YssFun.toSqlDate(date));
					classPst.setString(2, this.sPortCode);
					classPst.setString(3, "wsx");
					classPst.setString(4, "CNY");
					classPst.setString(5, this.getPortClsData(this.sPortCode, rs.getString("FPORTCLSCODE"), "fportclscode" , true ));
					classPst.setDouble(6, 0.0);
					classPst.setDouble(7 ,0.0);
					classPst.setDouble(8, 0.0);				
					classPst.setDouble(9, YssD.round(rs.getDouble("fbendbal"),2));
					classPst.setDouble(10, YssD.round(rs.getDouble("fbendbal"),2));
					classPst.setInt(11,1);
					classPst.setString(12, pub.getUserCode());
					classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
					classPst.setDouble(14, 0);
					classPst.setDouble(15, 0);
					classPst.executeUpdate();
					isAuxiAcc = true;
				}
				rs.close();
				
				if(!isAuxiAcc){
					sql= " select FPORTCLSCODE ,sum(Fendbal) as Fendbal , sum(fbendbal) as fbendbal , sum(faendbal) as faendbal from (" +
							"select c.*,d.*,' ' as fcurcode from (select kmdm,sum(fendbal) as fendbal,sum(fbendbal) as fbendbal,sum(faendbal) as faendbal from  ( "
						   +"select (case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else a.fcyid end) as fcurcode,"
						   +"(case when b.fendbal is null then 0 else b.fendbal end) - (case when fjje is null then 0 else fjje end) + (case when fdje is null then 0 else fdje end) as fendbal,"
						   +"(case when b.fbendbal is null then 0 else b.fbendbal end) - (case when fbjje is null then 0 else fbjje end) + (case when fbdje is null then 0 else fbdje end) as fbendbal,"
						   +"(case when b.faendbal is null then 0 else b.faendbal end) - (case when fjsl is null then 0 else fjsl end) + (case when fdsl is null then 0 else fdsl end) as faendbal"
						   +" from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,"
						   +"sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,"
						   +"sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,"
						   +"sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "+YssTabPrefix+"fcwvch "
						   +" where fterm ="+ YssFun.formatDate(date, "MM")+" and  "+ dbl.sqlDay("FDate")+ " <= "+ YssFun.formatDate(date, "dd")
						   +" group by fkmh, fcyid, fterm) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,-1 * fendbal as fendbal,fbaccdebit,fbacccredit,-1 * fbendbal as fbendbal,faaccdebit,-1 * faendbal as faendbal,FAuxiAcc from"
						   +"  "+YssTabPrefix+"Lbalance where fmonth ="+iniMonth+" and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) group by kmdm ) c "
						   +" join (select facctcode, facctname, famount, facctattr, FAcctClass,FAcctLevel,FAcctParent,FPORTCLSCODE from  "+YssTabPrefix+"Laccount where  fbalDC in (1, -1, 0) and  FAcctClass='权益类' and (FAcctName like '%未实现%' or Facctattr like '%未实现%') "
						   +" and  FAcctDetail=1 ) d on c.kmdm = d.facctcode  where c.fbendbal<>0  and  d.FPORTCLSCODE<>' ' order by d.FPORTCLSCODE,c.kmdm)"
						   + " group by FPORTCLSCODE " ;
					rs=dbl.openResultSet(sql);
					while(rs.next()){
						classPst.setDate(1, YssFun.toSqlDate(date));
						classPst.setString(2, this.sPortCode);
						classPst.setString(3, "wsx");
						classPst.setString(4, "CNY");
						classPst.setString(5, this.getPortClsData(this.sPortCode, rs.getString("FPORTCLSCODE"), "fportclscode" , true ));
						classPst.setDouble(6, 0.0);
						classPst.setDouble(7 ,0.0);
						classPst.setDouble(8, 0.0);				
						classPst.setDouble(9, YssD.round(rs.getDouble("fbendbal"),2));
						classPst.setDouble(10, YssD.round(rs.getDouble("fbendbal"),2));
						classPst.setInt(11,1);
						classPst.setString(12, pub.getUserCode());
						classPst.setString(13, YssFun.formatDatetime(new java.util.Date()));
						classPst.setDouble(14, 0);
						classPst.setDouble(15, 0);
						classPst.executeUpdate();
					}
				}
			gvPst.close();
			classPst.close();
		}catch (Exception e){
			throw new YssException("生成分级指标项出错！" + e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	
	/**
	 * add by huangqirong 2012-09-05 story #2782
	 * 获取估值表某科目指标项的值
	 * */
	private double getAssetNetValue(Date date , String portCode, String acctCode , String field)throws YssException{
		double result = 0 ;
		ResultSet rs = null;
		String sql = "select * from " + pub.yssGetTableName("TB_Rep_GuessValue")+ 
					" gv where FPORTCODE = " + dbl.sqlString(portCode) + 
					" and FDATE = " + dbl.sqlDate(date) + 
					" and Facctcode = " + dbl.sqlString(acctCode);		
		
		try {
			rs = dbl.openResultSet(sql);
			if(field != null && field.trim().length() > 0 && rs.next()){
				result = rs.getDouble(field);
			}
		} catch (Exception e) {
			throw new YssException("获取估值表指标项出错：" + e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}	
		return result;
	}
	
	/**
	 * add by huangqirong 2012-09-05 story #2782
	 * 根据组合分级的级别获取分级组合
	 * */
	private String getPortClsData(String portCode , String portClsRankOrPortCtls , String field , boolean isPortClsOrShowItem) throws YssException{
		String result = "" ;
		ResultSet rs = null;
		String sql = "";		
		
			sql = " select ta.fshowitem as FShowItem, ta.fportclsrank as FPortClsRank,tcfd.* from ( " + 
		       		 " select * from " + pub.yssGetTableName("tb_ta_portcls")+ " tp where tp.fportcode = " + dbl.sqlString(portCode) +
		       		 " and " +
		       		 	( isPortClsOrShowItem 
		       		 			? " tp.fportclsrank = " + dbl.sqlString(portClsRankOrPortCtls)            //分级组合 级别
		       		 					: "tp.fportclscode = " + dbl.sqlString(portClsRankOrPortCtls) ) + //分级组合 代码
		       		 " ) ta left join ( select * from " + pub.yssGetTableName("Tb_TA_ClassFundDegree") + 
		       		 " ) tcfd " +
		       		 " on ta.fportcode = tcfd.fportcode and ta.fportclscode = tcfd.fportclscode ";		
		try {
			rs = dbl.openResultSet(sql);
			if(rs.next()){
				result = rs.getString(field);
			}
		} catch (Exception e) {
			throw new YssException("获取估值表指标项出错：" + e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return result;
	}
	
	/**
	 * add by huangqirong 2012-09-05 story #2782
	 * 根据组合分级的级别获取分级组合 指标项
	 * */
	private double getSingleMuticlassNet(String portCode, String portCtls , String type , Date date ,String field) throws YssException{
		double result = 0;
		ResultSet rs = null;
		String sql = " select * from " + pub.yssGetTableName("tb_data_multiclassnet") + 
					 " dm where dm.fcheckstate = 1 and dm.fnavdate = " + dbl.sqlDate(date) + 
					 " and dm.fportcode = " + dbl.sqlString(portCode) + 
					 " and dm.fcurycode = " + dbl.sqlString(portCtls) + 
					 " and  dm.ftype = " + dbl.sqlString(type);
		try {
			rs = dbl.openResultSet(sql);
			if(rs.next()){
				result = rs.getDouble(field);
			}
		} catch (Exception e) {
			throw new YssException("获取估值表指标项出错：" + e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return result;
	}
	
	/**
	 * 分级累计净值采用财务估值表里的单位净值加分红数据来计算 
	 * 计算分级累计单位净值
	 * 分两种情形：1.没有基金拆分时；2.有基金拆分时。
	 * 1.没有基金分级拆分时算法：基金分级份额净值+分级累计分红金额(以前就是这么计算的)
	 * 2.有基金分级拆分时，分级累计净值的算法如下:
     *分级累计单位净值=｛【（基金分级份额净值+基金分级最后一次拆分后的单位分红金额）*基金分级最后一次拆细比例 +
     *   基金分级最后一次拆细前的的单位分红金额】×基金分级倒数第二次拆分比例＋基金分级倒数第二次拆分前的分红金额 ｝×……
     *   基金分级第一次拆分比例＋基金分级第一次拆分前的分红金额。
     *@return Double 分级累计净值
     *@param  Double unit 分级基金份额净值
     *@throws YssException
     *@author ysstech add by huangqirong 2012-09-20 STORY #2782
	 */
	private double getPortClsSplitUnit(double dUnit,java.util.Date dDate, String portCode ,String portClsCode) throws YssException {
		ArrayList arraySplitRatio = new ArrayList();// 拆分日期
		ArrayList arraySplitAll = new ArrayList();//存储拆分时间，
		double dSplitUnit = 0.0;// 分级累计单位净值
		arraySplitAll = this.getPortClsHmSplitRatio(dDate, portCode, portClsCode);
		String strSplitDate = YssFun.formatDate(dDate);// 字符以界面上选的日期开始
		try {
			for (Iterator iter = arraySplitAll.iterator(); iter.hasNext();) {
				String tmpRatioAll = (String) iter.next();
				String[] tmpAry = null;
				tmpAry = tmpRatioAll.split(YssCons.YSS_ITEMSPLITMARK1);
				String tmpDate = (String) tmpAry[0];// 取出里面保存的日期
				arraySplitRatio.add(tmpAry[1]);// 将拆分比例保存到列表
				strSplitDate += YssCons.YSS_ITEMSPLITMARK1 + tmpDate + YssCons.YSS_LINESPLITMARK + tmpDate;// 将每次分级拆分日期保存
				// 将每次分级拆分日期 分为区间保存，两两为一组，构成一个区间；如：date1,date2,date3
			}
			strSplitDate += YssCons.YSS_ITEMSPLITMARK1 + YssFun.formatDate(dDate);// 字符以界面上选的日期结束

			String reqAry[] = null;
			String tmpAry[] = null;
			reqAry = strSplitDate.split(YssCons.YSS_LINESPLITMARK);
			dSplitUnit = dUnit;
			for (int i = 0; i < arraySplitRatio.size(); i++) {// 遍历每一次分级拆分比例
				tmpAry = reqAry[i].split(YssCons.YSS_ITEMSPLITMARK1);// 日期区间与列表中分级拆分比例是对应的
				dSplitUnit = YssD.mul(Double.parseDouble(arraySplitRatio.get(i).toString()), YssD.add(
										dSplitUnit,
										this.getPortClsSplitDivided(
																	YssFun.toDate(tmpAry[1]), 
																	YssFun.toDate(tmpAry[0]),portCode , portClsCode)
																									  )
									);
			}
			// 1.无基金分级拆分情形，要加上累计分红金额；
			// 2.有基金分级拆分的情形，前面已经计算了每次分级拆分比例和每次拆分后分红金额，现在还需要加上第一次分级拆分前的分红金额
			// 因为日期区间段比拆分比例要多一次，前面对分级拆分比例的遍历是没有考虑最后这一次的。
			tmpAry = reqAry[reqAry.length - 1].split(YssCons.YSS_ITEMSPLITMARK1);// 取最后一次日期区间，即是第一次分级分红之前的时间段(有基金拆分时),或界面上传来的日期(无基金分级拆分时)
			
			//只需要根据后一个日期来计算，统计<=tmpAry[0]的分红，故前一日期设为固定值，不参与计算
			dSplitUnit = YssD.add(dSplitUnit, 
									this.getPortClsSplitDivided(YssFun.toDate("9998-12-31"), YssFun.toDate(tmpAry[0]),portCode , portClsCode)
								);// 基金分级第一次拆分前的分红金额(有基金拆分时),或 累计分红金额(无基金分级拆分时)
		} catch (Exception e) {
			throw new YssException("计算分级累计单位净值时出现异常");
		} finally {
			arraySplitAll.clear();
			arraySplitRatio.clear();
		}
		return dSplitUnit;
	}
	
	
	 /**
     * 获取基金分级拆分时的各次分红金额
     * @param dStartDate,dEndDate 取两个日期之间的所有分红金额之和
     * @return Double 返回两次日期之间的分红金额总额
     * @throws YssException
     * @author ysstech add by huangqirong 2012-09-20 STORY #2782
     * 计算累计净值时，要用到分红金额
     */
	private double getPortClsSplitDivided(java.util.Date dStartDate, java.util.Date dEndDate, String portCode , String portClsCode) throws YssException {
		String sqlStr = null;
		ResultSet rs = null;
		double dSplitDivided = 0.0;// 存放基金分级拆分的分红金额
		try {
			sqlStr = "select sum(FSellPrice) as FSplitDivided from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 "
					+ " and FPortCode = " + dbl.sqlString(portCode) 
					+ " and FPortClsCode = " + dbl.sqlString(portClsCode)
					+ " and FSellType = '03'"; // 获取累计分红价格，数据为小于等于当前日期
            //传进来的日期是 拆分日期。因为拆分日期当天不会出现分红，所以统不统计拆分当天的分红金额是没有影响的
			if (dStartDate != null&&!YssFun.formatDate(dStartDate).equals("9998-12-31")) {
				sqlStr += " and FConfimDate >= " + dbl.sqlDate(dStartDate);//因为拆分日期当天无分红，此时用 >= 和用 > 是一样的。
			}
			if (dEndDate != null&&!YssFun.formatDate(dEndDate).equals("9998-12-31")) {
				sqlStr += " and FConfimDate <= " + dbl.sqlDate(dEndDate);
			}
			rs = dbl.queryByPreparedStatement(sqlStr);
			if (rs.next()) {
				dSplitDivided = rs.getDouble("FSplitDivided");
			}
		} catch (Exception e) {
			throw new YssException("获取基金分级拆分时的分红数据出现异常！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return dSplitDivided;
	}
	
	/**
     * 获取基金分级拆分的每次拆分比例
     * @return HashMap 返回每次分级拆分比例
     * @throws YssException
     * @author ysstech add by huangqirong 2012-09-20 STORY #2782
     * 计算累计净值时，要用到每次分级的拆分比例 
     */
	private ArrayList getPortClsHmSplitRatio(java.util.Date dDate, String portCode , String portClsCode) throws YssException {
		String sqlStr = "";
		ResultSet rs = null;
		ArrayList arraySplitDate = new ArrayList();//存储拆分时间,拆分比例，因为要按顺序取
		try {
			sqlStr = "select FSplitRatio,FConfimDate from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState=1 and FPortCode= " + dbl.sqlString(portCode) 
					+ " and FPortClsCode = " + dbl.sqlString(portClsCode)
					+ " and FSplitRatio > 0"//排除为空的记录
					+ " and FConfimDate <= " + dbl.sqlDate(dDate)//只对之前的日期进行计算，所以只获取之前的拆分比例					
					+ " order by FConFimDate desc";
			rs = dbl.queryByPreparedStatement(sqlStr);
			while (rs.next()) {
				arraySplitDate.add(YssFun.formatDate(rs.getDate("FConfimDate")) + 
									YssCons.YSS_ITEMSPLITMARK1 + new Double(rs.getDouble("FSplitRatio")).toString()
								  );
			}
		} catch (Exception e) {
			throw new YssException("获取基金分级拆分的每次拆分比例出现异常");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return arraySplitDate;
	}
	
	private double getDateAmountRate(Date endDate, double todayRate, String clsPortCode) throws YssException {
		double accummulate = 0.0;
		double yesdayRate = 0.0;
		String sqlStr = null;
		String portCode = "";
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		try {
			// 得到当天的累计分红
			sqlStr = "select sum(FSellPrice) as FAccumulateDivided from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 and FConfimDate ="
					+ dbl.sqlDate(endDate)
					+ " and FSellType = '03'" + " and FPortCode = "
					+ dbl.sqlString(this.sPortCode)
					+ " and Fportclscode = "
					+ dbl.sqlString(clsPortCode); 
			rs = dbl.queryByPreparedStatement(sqlStr); 
			if (rs.next()) {
				accummulate = rs.getDouble("FAccumulateDivided"); // 获取汇总的值。
			}

			yesdayRate = this.getSingleMuticlassNet(this.sPortCode, clsPortCode, "02", YssFun.addDay(endDate,-1), "FClassNetValue");
			
			if (yesdayRate != 0) {
				accummulate = (todayRate - yesdayRate + accummulate)
						/ (yesdayRate - accummulate);
			}
		} catch (Exception e) {
			throw new YssException("获取累计分红数据出现异常！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return YssD.round(accummulate, 4);
	}
	
	//add by fangjiang story 3264 2012.12.13
	public boolean arr_contain_str(String[] arr, String s) throws YssException {
		boolean flag = false;
		try {
			for(int i=0; i<arr.length; i++){
				if(arr[i].equalsIgnoreCase(s))
					flag = true;
			}
		} catch (Exception e) {
			throw new YssException("程序出现异常！", e);
		} finally {
			
		}
		return flag;
	}
	
	//20121029 added by liubo
	//计算出ETF净值数据表、财务估值表的“最小申赎单位净值”项目的数据， 获取ETF净值数据表、财务估值表的“篮子估值”、“现金差额”项目的数据
	//将这六个数据分别进行比对，值不同的项目需要返回给前台，然后给出提示
	public void getETFAndGuessValDiff() throws YssException
	{
		String strReturn = "";
		String strSql = "";
		ResultSet rs = null;
		ResultSet rsMin = null;
		
		double dMinValOfETF = 0;		//ETF净值数据表“最小申赎单位净值”的数据
		double dMinValOfGuessVal = 0;	//财务估值表“最小申赎单位净值”的数据
		double dlzgz = 0;				//财务估值表“篮子估值”的数据
		double dETFlzgz = 0;			//ETF净值数据表“篮子估值”的数据
		double dxjce = 0;				//财务估值表“现金差额”的数据
		double dETFxjce = 0;			//ETF净值数据表“现金差额”的数据

		try
		{
			//分别获取ETF净值数据表和财务估值表的“篮子估值”、“现金差额”项目的数据
			strSql = "select a.FSTANDARDMONEYMARKETVALUE as lzgz,b.fstandardmoneymarketvalue as xjce," + 
					 "c.fportmarketvalue as ETFlzgz,d.fportmarketvalue as ETFxjce from " + 
					 " (select FDate,FSTANDARDMONEYMARKETVALUE from " + pub.yssGetTableName("tb_rep_guessvalue") + 
					 " where FAcctCode = '9801' and FPortCode = " + dbl.sqlString(String.valueOf(iSetCode)) + 
				 	 " ) a left join (select FDate,FSTANDARDMONEYMARKETVALUE from " + pub.yssGetTableName("tb_rep_guessvalue") + 
				 	 " where FAcctCode = '9802' and FPortCode = " + dbl.sqlString(String.valueOf(iSetCode)) + 
				     " ) b on a.fdate = b.Fdate" +
				 	 " left join (select FNavDate,FPORTMARKETVALUE from " + pub.yssGetTableName("Tb_ETF_NavData") +  
				 	 " where FKeyCode = 'StockListVal' and FPortCode = " + dbl.sqlString(String.valueOf(sPortCode)) + 
				 	 " ) c on a.fdate = c.FNavdate" +
				 	 " left join (select FNavDate,FPORTMARKETVALUE from " + pub.yssGetTableName("Tb_ETF_NavData") +
				 	 " where FKeyCode = 'UnitCashBal' and FPortCode = " + dbl.sqlString(String.valueOf(sPortCode)) + 
				 	 " ) d on a.fdate = d.FNavdate" +
				 	 " where a.Fdate = " + dbl.sqlDate(dDate);
			
			rs = dbl.openResultSet(strSql);
			
			if (rs.next())
			{
				dlzgz = rs.getDouble("lzgz");
				dETFlzgz = rs.getDouble("ETFlzgz");
				dxjce = rs.getDouble("xjce");
				dETFxjce = rs.getDouble("ETFxjce");
			}
			if(dxjce != dETFxjce)
			{
				strReturn = "财务估值表与ETF净值数据表的【现金差额】不匹配！\r\n";
				strReturn += "财务估值表数据为：" + String.valueOf(dxjce) + "；    ETF净值数据表数据为：" + dETFxjce + "\r\n";
			}
			
			//当某个项目的值不匹配时，抛出一个异常
			//前台捕获异常后，会抓取“财务估值表与ETF净值数据表数据不匹配”的信息。
			//若存在这条信息，则给出某个项目数据不匹配的提示
			//若不存在这条信息，则前台按一般异常进行处理
			if (!strReturn.trim().equals(""))
			{
				throw new YssException(strReturn);
			}
			
		}
		catch(Exception ye)
		{
			throw new YssException(ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rsMin,rs);
		}		
	}
	
	/**
	 * add dongqingsong 2013-06-03 story #3871 联动清算 建行联动清算需求
	 * 通过通用参数判断是否调用场内交易数据  Story #3871 
	 * @param portCode
	 */
	private boolean getExchangeDate(String portCode){
		boolean flag = false;
		try {
			flag =this.Judge(portCode);
		}catch(YssException e) {
				e.printStackTrace();
		}
			return flag;
		}
	
    /**
     * add dongqingsong 2013-05-24 story #3871 联动清算 建行联动清算需求
     * @return 获取特定控件的值  ，返回清算账户来源    其中返回值：为01值是webservic来源 ，
     * 作用是：判断是否对场内交易数据进行（11）号接口的推送服务
     * @throws YssException
     */
    private boolean Judge(String portCode) throws YssException {
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String portCodeAndName="";
        boolean flag= false;
        try {
        	portCodeAndName=this.PortAndName(portCode);
        	flag=this.getExchangeType(portCodeAndName);
        	return flag;
        } catch (Exception e) {
            throw new YssException("获取清算账户来源出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsTest, grpRs);
        }
    }
    
    /**
     * add dongqingsong 2013-05-22 story #3871 联动清算 建行联动清算需求
     * @param str
     * @return
     * @throws YssException
     * 01代表清算账户来源是webservice 
     */
    private boolean  getExchangeType(String str) throws YssException{
    	ResultSet rs=null;
    	String fctlvalueArray = "";
    	String PrefixTb =this.pub.getPrefixTB().toString();
    	String source="";
    	boolean flag=false;
    	try{
	    	String sqlStr = "select t.fctlvalue from Tb_"+PrefixTb+"_Pfoper_Pubpara t where FPubParaCode = 'exchangeTrade' " +
			"and t.fctlcode = 'ComboBox' and t.fparaid =" +
			" (select max(tt.fparaid) as fparaid from Tb_"+PrefixTb+"_Pfoper_Pubpara tt  where FPubParaCode = 'exchangeTrade'" +
			" and tt.fctlvalue ='"+str+"')";
	    	rs = dbl.openResultSet(sqlStr);
			while(rs.next()){
				fctlvalueArray=rs.getString("fctlvalue");
			}
			if(fctlvalueArray == null ||fctlvalueArray.trim().length() == 0)
				return false;
			String[] strInfo=fctlvalueArray.split(",");
			source=strInfo[0];
			if(source.equals("01")){
				flag=true;
			}
    	}catch (Exception e) {
            throw new YssException("获取清算账户来源出错！", e);
		}
		return flag;
    }
    
    /**
     * add dongqingsong 2013-05-10 story #3871 联动清算 建行联动清算需求
     * @param portCode 组合代码
     * @return 组合和组合名称的连接值
     * @throws YssException
     */
    private String PortAndName(String portCode) throws YssException{
    	String portcode=portCode;
    	String PrefixTb =pub.getPrefixTB().toString();
    	ResultSet rs=null;
    	String portAndName="";
    	if(portcode.contains("'")){
         	portcode=portCode.replace("'", "");
        }
        try{
        	String getNameSql="select a.fportcode,a.fportname, a.fportcode||'|'||a.fportname as PortName from " +
        			"Tb_"+PrefixTb+"_Para_Portfolio a where a.fportcode='"+portcode+"'";
        	rs = dbl.openResultSet(getNameSql);
         	while(rs.next()){
         		 portAndName=rs.getString("PortName");
         	}
        }catch (Exception e) {
             throw new YssException("获取公共参数出错！", e);
        } 
         return portAndName;
    }
}
