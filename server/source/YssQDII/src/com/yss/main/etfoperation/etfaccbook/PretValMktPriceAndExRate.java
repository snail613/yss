package com.yss.main.etfoperation.etfaccbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.commeach.EachRateOper;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.operdata.ExchangeRateBean;
import com.yss.main.operdata.MarketValueBean;
import com.yss.main.operdata.ValExRateBean;
import com.yss.main.operdata.ValMktPriceBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.MTVMethodBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 处理估值行情和汇率的Bean MS00006 QDV4.1赢时胜上海2009年2月1日05_A 用于处理生成估值行情和估值汇率
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author songjie 2009.04.03
 * @version 1.0
 */
public class PretValMktPriceAndExRate extends BaseDataSettingBean {
	private HashMap allMtvMethodHm; // 用于储存组合代码对应的估值方法列表 key-组合代码
									// value-估值方法的arrayList

	private HashMap mktOrExValueHm; // 用于储存证券代码对应的行情的类 key-(组合代码 + 证券代码 +
									// 行情来源代码), value-证券代码及行情来源对应的行情的类
	// 也可以储存证券代码对应的币种的汇率类 key-(组合代码 + 币种代码 + 汇率来源代码), value-币种及汇率来源对应的汇率类

	private HashMap portSecHm; // 用于储存组合代码对应的证券代码列 key-组合代码
								// value-组合代码对应的证券代码的ArrayList或HashMap

	private HashMap portRateHm; // 用于储存组合代码对应的估值当天的组合汇率

	private HashMap portCuryHm; // 用于储存组合代码对应的组合货币代码

	private ArrayList alMtvMktOrExValue = null; // 用于储存要插入到估值行情表的估值行情对象。
	// 也可用于储存插入到估值汇率表的估值汇率对象。
	public String futures = "FU"; // 期货品种类型代码
	public String securitys = "DR,OP,EQ,FI,TR,DE,RE,CL,PN,RT"; // 股票的所有品种类型代码
	public String allCategorys = null; // 所有要估值的品种类型代码
	public boolean haveSecsMV = false; // 用于判断是否要做证券浮动盈亏的业务
	public boolean haveIndexFutruesMV = false; // 用于判断是否要做股指期货的业务

	/**shashijie 2011-08-19 STORY 1434*/
	private ETFParamSetBean paramSet = null;// ETF参数的实体类
	private ETFParamSetAdmin paramSetAdmin = null;
	private HashMap etfParam = null;//保存参数设置
	/**ehd*/
	
	/**shashijie 2012-12-12 STORY 3328 注释 STORY 3416的代码*/
	//private boolean paramExRate=false; // add by zhaoxianlin 20121019 #STORY 3146
	/**end*/
	
	/**
	 * 构造函数
	 */
	public PretValMktPriceAndExRate() {

	}

	// /**
	// * 用于获取品种类型代码信息
	// * @param arrvalTypeAry String[] 为资产估值界面已选的业务类型
	// * @throws YssException
	// */
	// public void initform(String[] arrValTypeAry) throws YssException {
	// ArrayList alVal = new ArrayList(); //新建一个ArrayList用于储存所有已选业务类型
	// allCategorys = ""; //给品种类型信息赋初值
	// for (int i = 0; i < arrValTypeAry.length; i++) { //循环所有业务类型
	// alVal.add(arrValTypeAry[i]); //把所有业务类型都添加到alVal中
	// }
	// if (alVal.contains(YssOperCons.YSS_GZLX_INDEX_HDSY)) {
	// //若alVal中包括YssOperCons.YSS_GZLX_INDEX_HDSY这样的业务类型
	// allCategorys += futures + ","; //则添加期货品种类型代码到品种类型信息中
	// haveIndexFutruesMV = true; //表示要做股指期货的业务
	// }
	// if (alVal.contains(YssOperCons.YSS_GZLX_SEC_FDYY)) {
	// //若alVal中包括YssOperCons.YSS_GZLX_SEC_FDYY这样的业务类型
	// allCategorys += securitys + ","; //则添加股票的所有品种类型代码到品种类型信息中
	// haveSecsMV = true; //表示要做证券浮动盈亏的业务
	// }
	// if (allCategorys.length() > 1) { //若品种类型信息字符串中有数据
	// allCategorys = allCategorys.substring(0, allCategorys.length() - 1);
	// //则将字符串最后的逗号去掉
	// }
	// }

	/**
	 * 用于获取估值行情和估值汇率并存到估值行情和估值汇率表中
	 * 
	 * @param portCodes
	 *            String
	 * @param dDate
	 *            Date
	 * @param valTypeAry
	 *            String[]
	 * @throws YssException
	 */
	public HashMap getValMktPriceAndExRateBy(String portCodes, java.util.Date dDate) throws YssException, SQLException {
		// initform(arrValTypeAry); //确定所有的品种类型代码，组成带逗号的品种类型代码的字符串
		/**shashijie 2011-08-19 STORY 1434 */
		initDate(portCodes);//初始化ETF参数设置对象
		/**end*/
		CtlPubPara ctlpubpara = new CtlPubPara(); // 新建CtlPubPara实例
		ctlpubpara.setYssPub(pub); // 设置pub
		getMtvMethodsBy(portCodes); // 根据组合代码获取所有相关的估值方法信息
		// if (!allCategorys.equals("")) { //若品种类型代码不为空
		getValMktPriceBy(portCodes, dDate, ctlpubpara); // 则获取估值行情信息并插入到估值行情表中去
		// }
		getValExRateBy(portCodes, dDate, ctlpubpara); // 获取估值汇率信息并插入到估值汇率表中去
		return allMtvMethodHm; // 用于返回已选组合的估值方法实例的HashMap
	}

	/**
	 * 用于获取估值行情并插入到估值行情表中
	 * 
	 * @param portCodes
	 *            String
	 * @param dDate
	 *            Date
	 * @param ctlpubpara
	 *            CtlPubPara
	 * @throws YssException
	 */
	private void getValMktPriceBy(String portCodes, java.util.Date dDate, CtlPubPara ctlpubpara) throws YssException, SQLException {
		String priMarketPrice = null; // 声明一个字符串
		getMakeUpSecurityBy(portCodes, dDate); // 根据组合代码和估值日期获取所有证券库存的证券代码
		getMarketValueBy(portCodes, dDate); // 根据组合代码和估值日期获取得到的证券代码的相关的所有行情来源的行情信息储存到hashMap中
		priMarketPrice = ctlpubpara.getPriMarketPrice(); // 获取通用参数配置信息
		if (priMarketPrice.equalsIgnoreCase("valuation")) { // 若通用参数配置信息为valuation
			getMTVMethod(portCodes, ctlpubpara, dDate); // 获取国内的估值方法和估值行情信息
		} else if (priMarketPrice.equalsIgnoreCase("day")) { // 若通用参数配置信息为day
			getOutterMTVMethod(portCodes, dDate); // 获取中保的估值方法和估值行情信息
		}
		insertValMktPrice(portCodes, dDate); // 将得到的估值行情数据插入到估值行情表中
	}

	/**
	 * 用于获取估值汇率并插入到估值汇率表中
	 * 
	 * @param portCodes
	 *            String
	 * @param dDate
	 *            Date
	 * @throws YssException
	 */
	private void getValExRateBy(String portCodes, java.util.Date dDate, CtlPubPara ctlpubpara) throws YssException, SQLException {
		getMakeUpCurrencyBy(portCodes, dDate); // 根据组合代码和估值日期获取所有证券库存，证券应收应付，现金应收应付以及运营应收应付的币种代码
		getExRateBy(portCodes, dDate); // 根据组合代码和估值日期获取得到的币种的所有汇率来源的汇率信息存储到hashMap中
		getExMTVMethodBy(portCodes, dDate, ctlpubpara); // 获取估值方法和估值汇率信息
		insertValRate(portCodes, dDate); // 将得到的估值汇率信息插入到估值汇率表中去
	}

	/**
	 * 获取所有组合代码的所有估值方法
	 * @param portCode String
	 * @throws YssException
	 */
	private void getMtvMethodsBy(String portCodes) throws YssException {
		String strSql = ""; // 用于储存sql语句
		String mtvCode = ""; // 用于储存估值方法代码
		String portCode = null; // 用于储存组合代码
		String[] arrPortcodes = null; // 用于储存组合代码对应的估值方法代码，key - 组合代码，value -
										// 估值方法代码
		ResultSet rs = null; // 结果集
		ArrayList mtvMethods = null; // 储存估值方法代码对应的估值方法实例的ArrayList
		allMtvMethodHm = null; // 储存组合代码对应的估值方法的实例 key - 组合代码 value -
								// 组合代码对应的估值方法实例的ArrayList
		HashMap alPortMTVCode = null; // 储存组合代码对应的估值方法信息，key - 组合代码，value -
										// 组和代码对应的多个或一个估值方法信息的用"\t"隔开的字符串
		MTVMethodBean mtvMethod = null; // 声明估值方法实例
		try {
			allMtvMethodHm = new HashMap();
			alPortMTVCode = new HashMap();

			// 查询所有组合代码对应的估值方法代码，并按照估值方法的优先级从大到小排序
			strSql = " select a.FPortCode,b.FRelaGrade,b.FSubCode from (select FPortCode from " 
				    + pub.yssGetTableName("TB_Para_PortFolio_RelaShip")
					+ " where FPortCode in (" + operSql.sqlCodes(portCodes)
					+ ") order by FPortCode) a left join (select fportCode,FRelaGrade,FSubCode from "
					+ pub.yssGetTableName("TB_Para_PortFolio_RelaShip") + " where FRelaType = 'MTV') b on a.FPortCode = b.FPortCode "
					+ " group by a.FportCode,b.Frelagrade,b.Fsubcode ";

			rs = dbl.openResultSet(strSql);

			while (rs.next()) {
				if (alPortMTVCode.get(rs.getString("FPortCode")) != null) { // 若alPortMTVCode中有当前组合代码对应的估值方法代码信息字符串
					mtvCode = (String) alPortMTVCode.get(rs.getString("FPortCode")); // 则取出当前组合代码对应的估值方法代码信息字符串
					mtvCode += rs.getString("FSubCode") + "\t"; // 给当前组合代码对应的估值方法信息字符串添加估值方法代码信息以"\t"隔开
					alPortMTVCode.put(portCode, mtvCode); // 更新alPortMTVCode中当前组和代码对应的估值方法信息，原先的组合代码对应的value会被覆盖掉
				} else { // 若alPortMTVCode中没有当前组合代码对应的估值方法代码信息字符串
					mtvCode = ""; // 新建一个用于储存估值方法代码信息的字符串
					mtvCode += rs.getString("FSubCode") + "\t"; // 给估值方法代码信息字符串添加估值方法代码信息以"\t"隔开
					portCode = rs.getString("FPortCode"); // 获取当前组合代码信息
					alPortMTVCode.put(portCode, mtvCode); // 将组合代码和组合代码对应的估值方法代码信息字符串储存到alPortMTVCode中
				}
			}

			arrPortcodes = portCodes.split(","); // 将组合代码字符串用逗号隔开

			for (int i = 0; i < arrPortcodes.length; i++) { // 循环组合代码
				mtvCode = (String) alPortMTVCode.get(arrPortcodes[i]); // 在alPortMTVCode中根据组合代码获取相应的估值方法代码字符串
				mtvCode = mtvCode.substring(0, mtvCode.length() - 1); // 将估值方法代码字符串的最后的"\t"去掉
				mtvMethod = new MTVMethodBean(); // 新建一个估值方法的Bean
				mtvMethod.setYssPub(pub); // 设置pub
				mtvMethods = mtvMethod.getMTVInfo(mtvCode); // 通过估值方法代码字符串得到对应的估值方法实例的arrayList
				allMtvMethodHm.put(arrPortcodes[i], mtvMethods); // 将组合代码和组合代码对应的估值方法实例的arrayList储存到allMtvMethodHm中
			}
		} catch (Exception e) {
			throw new YssException("获取组合代码对应的估值方法出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取估值当天的所有组合代码的所有需要补票的证券代码
	 * @param portCode String
	 * @return String
	 * @throws YssException
	 */
	public void getMakeUpSecurityBy(String portCodes, java.util.Date dDate) throws YssException {
		String strSql = null; // 用于储存sql语句
		ResultSet rs = null; // 声明结果集
		String portCode = ""; // 组合代码
		portSecHm = new HashMap(); // 用于组合代码对应的证券的信息
		String[] strPortCodes = null;
		
		/**shashijie 2013-1-16 STORY 3402 修改:查看补票完成日与强制处理日,哪个长用哪个节假日倒推*/
		/**shashijie 2011-08-19 STORY 1434 */
		Date makeDate = getWorkDayMake(dDate);//获取倒退出的补票工作日
		/**end shashijie 2011-08-19 STORY 1434 */
		/**end shashijie 2013-1-16 STORY 3402 */
		
		try {
			strSql = " select makeup.* ,sec.fcatcode,sec.fsubcatcode,sec.ffactor from (select distinct FSecurityCode,FPortCode "
				+ " from (select a.FNum, a.FPortcode, a.FSecuritycode, a.FStockholdercode, a.FBrokercode, a.FSeatCode, a.FBs, "
				+ " a.FBuydate,a.FReplaceamount, a.FBraketnum, a.FUnitcost, a.FOreplacecash, a.FHreplacecash, a.FOcreplacecash, "
				+ " a.FHcreplacecash, b.FRemaindAmount as FRemaindAmount, b.FRefNum as FRefNum,b.FMakeUpDate as FMakeUpDate from " 
				+ pub.yssGetTableName("Tb_ETF_TradeStlDtl") + " a left join (select max(FMakeUpDate) as FMakeUpDate, FRemaindAmount,"
				+ " FNum, FRefNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + " where FMakeUpDate < " + dbl.sqlDate(dDate)
				+ " group by FRemaindAmount, FNum, FRefNum) b on a.FNum = b.FNum order by a.FNum) c where " 
				+ " ((c.FRemaindAmount <> 0 and c.FMakeUpDate < " + dbl.sqlDate(dDate) 
				+ ") or FNum not in (select distinct FNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
				+ " where FMakeUpDate < " + dbl.sqlDate(dDate) 
				+ ")) and FBuyDate <= " + dbl.sqlDate(dDate) 
				+ " and FNum in (select distinct FNum from " 
				+ pub.yssGetTableName("Tb_ETF_TradeStlDtl") + " where FPortCode in (" 
				+ operSql.sqlCodes(portCodes) + " )) and FSecurityCode <> ' '" 
				//2009.12.23 蒋锦 修改 不从过户库中获取证券代码，直接从股票篮获取
				//股票篮中可能有基金代码，所以需要判断股票篮中的证券是否在证券信息表中
//				+ " union select distinct FSecurityCode, FPortCode from " + pub.yssGetTableName("Tb_ETF_GHInterface")
//				+ " where FBargainDate = " + dbl.sqlDate(dDate) + " and FPortCode in (" + operSql.sqlCodes(portCodes) 
//				+ ") and FSecurityCode in (select FSecurityCode from " + pub.yssGetTableName("Tb_Etf_Stocklist") 
//				+ " where FDate = " + dbl.sqlDate(dDate) + " and FReplaceMark = '1') and FAppNum <> 'ETFJIJIN' " 
				+ "union select FSecurityCode, FPortCode from " + pub.yssGetTableName("Tb_Etf_Stocklist") + " where FDate = "
				+ dbl.sqlDate(dDate) + " and FPortCode IN ( " + operSql.sqlCodes(portCodes)
				+ ") AND FSecurityCode IN (SELECT FSecurityCode FROM "
				+ pub.yssGetTableName("TB_PARA_Security")
				+ " WHERE FCheckState = 1)" 
				
				+ "union select FSecurityCode, FPortCode from " + pub.yssGetTableName("tb_stock_security")
                + " where fstoragedate = " + dbl.sqlDate(YssFun.addDay(dDate, -1))
                + " and FPortCode = " + dbl.sqlString(portCodes) +
				
				/**shashijie 2011-08-19 STORY 1434 ,增加对权证代码的行情处理*/
				" Union select FTSecurityCode as FSecurityCode," + dbl.sqlString(portCodes) +
				" as FPortCode From "+pub.yssGetTableName("tb_data_rightsissue")+
		        " where FCheckState = 1 and FExrightDate <= "+dbl.sqlDate(dDate)+" and FExrightDate > "+
		        dbl.sqlDate(makeDate) +
				/**end*/
		        /**add---huhuichao 2013-8-8 STORY  4276  增加对送股权益（A送B）的标的证券的行情处理*/
		        " Union select FTSecurityCode as FSecurityCode," + dbl.sqlString(portCodes) +
				" as FPortCode From "+pub.yssGetTableName("Tb_Data_BonusShare")+
		        " where FCheckState = 1 and FExrightDate <= "+dbl.sqlDate(dDate)+" and FExrightDate > "+
		        dbl.sqlDate(makeDate) +
				/**end---huhuichao 2013-8-8 STORY  4276*/
				" ) makeup left join "
				+ " (select FSecurityCode,FCatCode,FSubCatCode,FFactor from " + pub.yssGetTableName("tb_Para_Security") 
				+ " where FCheckState = 1) sec on makeup.FSecurityCode = sec.fsecuritycode order by FPortCode ";
			rs = dbl.openResultSet(strSql);

			while (rs.next()) {
				portCode = rs.getString("FPortCode"); // 获取组合代码

				//若为全额现金替代
				if(portCode.equals(" ")){
					strPortCodes = portCodes.split(",");
					
					for(int i = 0; i < strPortCodes.length; i++){
						portCode = strPortCodes[i];
						portSecHm = setETFTradeSetDel(portSecHm, portCode, rs);
					}
				}
				else{
					portSecHm = setETFTradeSetDel(portSecHm, portCode, rs);
				}
			}
		} catch (Exception e) {
			throw new YssException("获取估值当天的所有组合代码的所有需要补票的证券代码出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 设置估值当天的所有组合代码的所有需要补票的证券的数据
	 * @param portSecHm
	 * @param portCode
	 * @param rs
	 * @return
	 * @throws YssException
	 */
	private HashMap setETFTradeSetDel(HashMap portSecHm, String portCode, ResultSet rs)throws YssException{
		HashMap alSec = null; // 用于储存证券代码
		try{
			if (!portSecHm.containsKey(portCode)) { // 若portSecHm不包含当前循环到的组合代码
				alSec = new HashMap(); // 则新建一个用于储存证券代码的HashMap

				ETFTradeSettleDetailBean etfTradeSelDet = new ETFTradeSettleDetailBean();

				SecurityBean security = new SecurityBean(); // 新建证券对应的Bean
				
				security.setFactor(rs.getDouble("FFactor")); // 设置报价因子
				security.setCategoryCode(rs.getString("FCatCode")); // 设置品种类型代码
				security.setSubCategoryCode(rs.getString("FSubCatCode")); // 設置品种子类型代码
				etfTradeSelDet.setSecurityBean(security);//设置证券信息
				etfTradeSelDet.setSecurityCode(rs.getString("FSecurityCode"));
				etfTradeSelDet.setPortCode(portCode);

				if (rs.getString("FCatCode") == null) { // 若品种类型为空则跳出异常信息提示
					throw new YssException("请检查证券代码为【" + rs.getString("FSecurityCode") + "】的证券信息设置，该证券已被删除或反审核！");
				}

				alSec.put(rs.getString("FSecurityCode"), etfTradeSelDet);
				portSecHm.put(portCode, alSec); // 将组合代码和alSec储存到portSecHm中
			} else { // 若portSecHm包含当前循环到的组合代码
				alSec = (HashMap) portSecHm.get(portCode); // 获取组和代码对应的证券信息

				ETFTradeSettleDetailBean etfTradeSelDet = new ETFTradeSettleDetailBean();
				SecurityBean security = new SecurityBean(); // 新建证券对应的Bean
				
				security.setFactor(rs.getDouble("FFactor")); // 设置报价因子
				security.setCategoryCode(rs.getString("FCatCode")); // 设置品种类型代码
				security.setSubCategoryCode(rs.getString("FSubCatCode")); // 設置品种子类型代码
				etfTradeSelDet.setSecurityBean(security);//设置证券信息
				etfTradeSelDet.setSecurityCode(rs.getString("FSecurityCode"));
				etfTradeSelDet.setPortCode(portCode);
				
				alSec.put(rs.getString("FSecurityCode"), etfTradeSelDet);
				portSecHm.put(portCode, alSec);
			}
			
			return portSecHm;
		}
		catch(Exception e){
			throw new YssException("获取估值当天的所有组合代码的所有需要补票的证券代码出错", e);
		}
	}
	
	/**
	 * 获取所有组合代码的所有证券代码对应的行情类
	 * @param securityCode String
	 * @param portCode String
	 * @param dDate Date
	 * @throws YssException
	 */
	public void getMarketValueBy(String portCodes, java.util.Date dDate) throws YssException {
		String strSql = null; // 储存sql语句
		ResultSet rs = null; // 声明结果集
		mktOrExValueHm = new HashMap(); // 新建HashMap

		/**shashijie 2013-1-16 STORY 3402 修改:查看补票完成日与强制处理日,哪个长用哪个节假日倒推*/
		/**shashijie 2011-08-19 STORY 1434 */
		Date makeDate = getWorkDayMake(dDate);//获取倒退出的补票工作日
		/**end shashijie 2011-08-19 STORY 1434*/
		/**end shashijie 2013-1-16 STORY 3402 */
		
		try {
			strSql = " select b.FPortCode,b.FSecurityCode,b.FMktSrcCode,b.FMktValueDate,b.FMktValueTime, "
					+ " b.FBargainAmount,b.FBargainMoney,b.FYClosePrice,b.FOpenPrice, "
					+ " b.FTopPrice,b.FLowPrice,b.FClosingPrice,b.FAveragePrice,b.FNewPrice, "
					+ " b.FMktPrice1,b.FMktPrice2,b.FMarketStatus,b.FDesc,b.FCheckState "
					+ " from (select fmktsrccode,max(FMktValueDate) as FMktValueDate,FSecurityCode,FPortCode from "
					+ pub.yssGetTableName("tb_data_marketvalue") + " mkt1 where FCheckState = 1 and FMktValueDate <=" 
					+ dbl.sqlDate(dDate) + " group by FMktsrccode, FSecurityCode, FPortCode order by FMktValueDate desc) " 
					+ " a left join (select * from " + pub.yssGetTableName("tb_data_marketvalue") + " mkt where FSecurityCode in (" 
					+ " select distinct FSecurityCode from (select a.FNum, a.FPortcode, a.FSecuritycode, a.FStockholdercode, " 
					+ " a.FBrokercode, a.FSeatCode, a.FBs,a.FBuydate,a.FReplaceamount, a.FBraketnum, a.FUnitcost, a.FOreplacecash, " 
					+ " a.FHreplacecash, a.FOcreplacecash,a.FHcreplacecash, b.FRemaindAmount as FRemaindAmount, b.FRefNum as FRefNum, " 
					+ " b.FMakeUpDate as FMakeUpDate from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl") 
					+ " a left join (select max(FMakeUpDate) as FMakeUpDate, "
					+ " FRemaindAmount, FNum, FRefNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") 
					+ " where FMakeUpDate < "
					+ dbl.sqlDate(dDate) + " group by FRemaindAmount, FNum, FRefNum) b on a.FNum = b.FNum order by a.FNum) c "
					+ " where ((c.FRemaindAmount <> 0 and c.FMakeUpDate <" + dbl.sqlDate(dDate) 
					+ ") or FNum not in (select distinct FNum from " 
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + " where FMakeUpDate <" + dbl.sqlDate(dDate) 
					+ ")) and FBuyDate <= " + dbl.sqlDate(dDate) + " and FNum in (select distinct FNum from " 
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl") + " where FPortCode in (" + operSql.sqlCodes(portCodes) 
					+ ")) and FSecurityCode <> ' ' "   
					+ " union  select distinct FSecurityCode from " + pub.yssGetTableName("Tb_ETF_GHInterface") 
					+ " where FBargainDate = " + dbl.sqlDate(dDate) + " and FPortCode in( " + operSql.sqlCodes(portCodes)
					+ " )and FSecurityCode in (select FSecurityCode from " + pub.yssGetTableName("Tb_Etf_Stocklist")
					+ " where FDate = " + dbl.sqlDate(dDate) 
					+ " and FReplaceMark = '1') and FAppNum <> 'ETFJIJIN' " 
					+ " union select FSecurityCode from "
					+ pub.yssGetTableName("Tb_Etf_Stocklist") + " where FDate = " + dbl.sqlDate(dDate) 
					
					+ "union select FSecurityCode from " + pub.yssGetTableName("tb_stock_security")
					+ " where fstoragedate = " + dbl.sqlDate(YssFun.addDay(dDate, -1))
					+ " and FPortCode = " + dbl.sqlString(portCodes) + 
					
					/**shashijie 2011-08-19 STORY 1434 ,增加查询配股权证代码*/
					" Union " + 
                    " select FTSecurityCode as fsecuritycode from "+
                    pub.yssGetTableName("tb_data_rightsissue")+" where FCheckState = 1 and FExrightDate <= "+
                    dbl.sqlDate(dDate)+" and FExrightDate > "+dbl.sqlDate(makeDate)
                    /**add---huhuichao 2013-8-8 STORY  4276  增加查询送股标的证券代码 */
                    +" Union " + 
                    " select FTSecurityCode as fsecuritycode from "+
                    pub.yssGetTableName("Tb_Data_BonusShare")+" where FCheckState = 1 and FExrightDate <= "+
                    dbl.sqlDate(dDate)+" and FExrightDate > "+dbl.sqlDate(makeDate)
                    /**end---huhuichao 2013-8-8 STORY  4276*/
					+ " )) b on a.fmktsrccode = b.fmktsrccode and a.fMktValueDate = b.fMktValueDate "
					+ " and a.FSecurityCode = b.FSecurityCode and a.FPortCode = b.FPortCode "
					+ " where b.FCheckState = 1 order by a.fsecuritycode,FMktValueDate";

			rs = dbl.openResultSet(strSql);

			while (rs.next()) {
				setMarketValue(rs); // 设置行情对象的属性并将组合代码证券代码行情来源代码对应的行情实例储存到mktOrExValueHm中
			}
		} catch (Exception e) {
			throw new YssException("获取证券对应的估值行情出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 用于设置行情对象的属性
	 * @param rs ResultSet
	 */
	private void setMarketValue(ResultSet rs) throws SQLException {
		MarketValueBean marketvalue = new MarketValueBean(); // 新建行情实例

		marketvalue.setStrMktSrcCode(rs.getString("FMktSrcCode")); // 设置行情来源代码
		marketvalue.setStrSecurityCode(rs.getString("FSecurityCode")); // 设置证券代码
		marketvalue.setStrMktValueDate(rs.getDate("FMktValueDate").toString()); // 设置行情日期
		marketvalue.setStrMktValueTime(rs.getString("FMktValueTime")); // 设置行情具体时间
		marketvalue.setStrPortCode(rs.getString("FPortCode").trim()); // 设置组合代码
		marketvalue.setDblBargainAmount(rs.getDouble("FBargainAmount")); // 设置交易数量
		marketvalue.setDblBargainMoney(rs.getDouble("FBargainMoney")); // 设置交易金额
		marketvalue.setDblYClosePrice(rs.getDouble("FYClosePrice")); // 设置关盘价
		marketvalue.setDblOpenPrice(rs.getDouble("FOpenPrice")); // 设置开盘价
		marketvalue.setDblTopPrice(rs.getDouble("FTopPrice")); // 设置最高价
		marketvalue.setDblLowPrice(rs.getDouble("FLowPrice")); // 设置最低价
		marketvalue.setDblClosingPrice(rs.getDouble("FClosingPrice")); // 设置停盘价
		marketvalue.setDblAveragePrice(rs.getDouble("FAveragePrice")); // 设置平均价
		marketvalue.setDblNewPrice(rs.getDouble("FNewPrice")); // 设置最新价
		marketvalue.setDblMktPrice1(rs.getDouble("FMktPrice1")); // 设置市价1
		marketvalue.setDblMktPrice2(rs.getDouble("FMktPrice2")); // 设置市价2
		marketvalue.setMarketStatus(rs.getString("FMarketStatus")); // 设置行情状态
		marketvalue.setStrDesc(rs.getString("FDesc")); // 设置描述
		marketvalue.setStrCheckState(rs.getInt("FCheckState")); // 设置审核状态
		setPrice(marketvalue, rs); // 将查询到的行情数据的各种价格放入到一个HashMap中，并把这个HashMap作为行情对象的一个属性
		mktOrExValueHm.put(rs.getString("FPortCode").trim() + rs.getString("FSecurityCode") + rs.getString("FMktSrcCode"), marketvalue); // 设置行情对象的属性并将组合代码证券代码行情来源代码对应的行情实例储存到mktOrExValueHm中
	}

	/**
	 * 将查询到的行情数据的各种价格放入到一个HashMap中，并把这个HashMap作为行情对象的一个属性
	 * 
	 * @param marketvalue MarketValueBean
	 * @param rs ResultSet
	 * @throws SQLException
	 */
	private void setPrice(MarketValueBean marketvalue, ResultSet rs) throws SQLException {
		java.util.HashMap price = new HashMap(); // 新建一个key - 行情表中的行情字段 value - 行情价格 的HashMap

		price.put("FYClosePrice", new Double(rs.getDouble("FYClosePrice"))); // 添加关盘价格
		price.put("FOpenPrice", new Double(rs.getDouble("FOpenPrice"))); // 添加开盘价格
		price.put("FTopPrice", new Double(rs.getDouble("FTopPrice"))); // 添加最高价
		price.put("FLowPrice", new Double(rs.getDouble("FLowPrice"))); // 添加最低价
		price.put("FClosingPrice", new Double(rs.getDouble("FClosingPrice"))); // 添加停盘价
		price.put("FAveragePrice", new Double(rs.getDouble("FAveragePrice"))); // 添加平均价
		price.put("FNewPrice", new Double(rs.getDouble("FNewPrice"))); // 添加最新价
		price.put("FMktPrice1", new Double(rs.getDouble("FMktPrice1"))); // 添加市价1
		price.put("FMktPrice2", new Double(rs.getDouble("FMktPrice2"))); // 添加市价2

		marketvalue.setPrice(price); // 将各种价格作为行情实例的一个属性
	}

	/**
	 * 获取国内当天证券代码对应的估值方法和行情
	 * 
	 * @param portCodes String 组合代码
	 * @param ctlpubpara CtlPubPara 通用参数实例
	 * @param dDate Date 估值日期
	 * @throws YssException
	 */
	private void getMTVMethod(String portCodes, CtlPubPara ctlpubpara, java.util.Date dDate) throws YssException {
		alMtvMktOrExValue = new ArrayList(); // 新建alMtvMktOrExValue用于储存估值行情
		String[] arrPortCode = null; // 新建String[]用于储存拆分后的组合代码

		try {
			arrPortCode = portCodes.split(","); // 拆分组合代码

			for (int i = 0; i < arrPortCode.length; i++) { // 循环组合代码
				HashMap alSec = (HashMap) portSecHm.get(arrPortCode[i]); // 获取组合代码对应的相关的证券信息，键为证券代码，值为证券库存实例
				ArrayList alMTV = (ArrayList) allMtvMethodHm.get(arrPortCode[i]); // 获取组合代码对应的估值方法实例的ArrayList
				boolean isACTV = ctlpubpara.getIsUseACTVInfo(arrPortCode[i]); // 获取是否要储存行情状态的标志信息

				if (alSec == null || alMTV == null) { // 若组合代码对应的相关的证券信息为空或组合代码对应的估值方法实例为空则结束本次循环，跳到下一轮循环
					continue;
				}

				Iterator secValue = alSec.values().iterator(); // 得到组合代码对应的相关的证券信息的迭代器

				while (secValue.hasNext()) { // 若指针可以后移
					getSubMTVMethod(secValue, alMTV, isACTV, arrPortCode[i], dDate); // 获取当天证券代码对应的估值方法和行情
				}
			}
		} catch (Exception e) {
			throw new YssException("获取证券对应的估值方法出错", e);
		}
	}

	/**
	 * 获取当天证券代码对应的估值方法和行情
	 * 
	 * @param secValue
	 *            Iterator 组合代码对应的证券信息的迭代器
	 * @param alMTV
	 *            ArrayList 组合代码对应的估值方法的ArrayList
	 * @param isACTV
	 *            boolean 是否要储存行情状态的标志信息
	 * @param portCode
	 *            String 组合代码
	 * @param dDate
	 *            Date 估值日期
	 * @throws YssException
	 */
	private void getSubMTVMethod(Iterator secValue, ArrayList alMTV, boolean isACTV, String portCode, java.util.Date dDate) throws YssException {
		String securityCode = null; // 证券代码
		MTVMethodBean mtvMethod = null; // 声明估值方法实例
		MarketValueBean mktValues = null; // 声明行情实例
		Object price = null;
		String strSql = ""; // 用于储存sql语句
		ResultSet rs = null; // 用于声明结果集
		ValMktPriceBean valMktPrice = null;
		SecurityBean security = null;
		ETFTradeSettleDetailBean etfTradeSelDet = null;
		try {
			etfTradeSelDet = (ETFTradeSettleDetailBean)secValue.next();// 指针后移，取当前值强制转换为证券库存实例
			securityCode = etfTradeSelDet.getSecurityCode();// 获取证券代码

			for (int k = 0; k < alMTV.size(); k++) { // 循环估值方法
				mtvMethod = (MTVMethodBean) alMTV.get(k); // 获取当前估值方法实例
				// 在最近的行情数据中查找带组合代码的对应证券代码和估值方法行情来源代码的行情实例
				mktValues = (MarketValueBean) mktOrExValueHm.get(portCode + securityCode + mtvMethod.getMktSrcCode());
				if (mktValues == null) { // 若没有带组合代码的对应证券代码和估值方法行情来源代码的行情实例
					// 则查找不带组合代码的对应证券代码和估值方法行情来源代码的行情实例
					mktValues = (MarketValueBean) mktOrExValueHm.get("" + securityCode + mtvMethod.getMktSrcCode());
				}
				if (mktValues != null) { // 若找到对应的行情实例
					// 且行情实例中对应估值方法行情字段的信息不为空
					if (mktValues.getPrice().get(mtvMethod.getMktPriceCode()) != null) {
						valMktPrice = new ValMktPriceBean(); // 则新建估值行情实例
						valMktPrice.setValDate(dDate); // 设置估值日期
						valMktPrice.setPortCode(portCode); // 设置组合代码
						valMktPrice.setSecurityCode(securityCode); // 设置证券代码

						if (isACTV) { // 若是否要储存行情状态的标志信息为true
							valMktPrice.setMarketStatus(mktValues.getMarketStatus()); // 则储存行情状态信息
						}

						price = mktValues.getPrice().get(mtvMethod.getMktPriceCode()); // 获取行情实例中估值方法实例的行情字段对应的行情价格信息
						security = etfTradeSelDet.getSecurityBean(); // 获取证券库存实例对应的证券信息
						valMktPrice.setPrice(YssD.div(Double.parseDouble(price.toString()), security.getFactor(), 12)); // 根据证券信息的报价因子来设置行情价格
						valMktPrice.setOtPrice1(Double.parseDouble((mktValues.getPrice().get("FOpenPrice")).toString()));//2010.01.15 add by songjie 
						valMktPrice.setMtvCode(mtvMethod.getMTVCode()); // 设置估值方法代码
						valMktPrice.setMarketValueDate(YssFun.toDate(mktValues.getStrMktValueDate())); // 设置行情日期
						valMktPrice.setValType("SecsMV"); // 设置估值类型为SecsMV

						if (security.getCategoryCode().equals("FU")) {
							valMktPrice.setValType("IndexFutruesMV");
						}

						if (security.getSubCategoryCode().equals("OP02")) { // 若证券的品种子类型为OP02
							strSql = " select * from " + pub.yssGetTableName("Tb_Data_RightsIssue") + " where FCheckState = 1 and "
									+ dbl.sqlDate(mktValues.getStrMktValueDate()) + " between FExRightDate and FExpirationDate and FSecurityCode = "
									+ dbl.sqlString(securityCode);
							rs = dbl.openResultSet(strSql); // 则查询FRIPrice的数据
							while (rs.next()) {
								valMktPrice.setOtPrice1(rs.getDouble("FRIPrice")); // 设置OTPrice1字段的数据
							}
						}
						alMtvMktOrExValue.add(valMktPrice); // 添加估值行情实例到alMtvMktOrExValue
						return;
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("获取证券对应的估值方法出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取中保当天证券代码对应的估值方法
	 * 
	 * @param portCodes
	 *            String 组合代码
	 * @param dDate
	 *            Date 估值日期
	 * @throws YssException
	 */
	private void getOutterMTVMethod(String portCodes, java.util.Date dDate) throws YssException {
		String[] arrPortCode = null; // 用于存放组合代码
		String securityCode = null; // 证券代码
		alMtvMktOrExValue = new ArrayList(); // 新建ArrayList用于储存估值行情
		ETFTradeSettleDetailBean etfTradeSelDet = null;
		try {
			arrPortCode = portCodes.split(","); // 拆分组合代码

			for (int i = 0; i < arrPortCode.length; i++) { // 循环组合代码
				HashMap alSec = (HashMap) portSecHm.get(arrPortCode[i]); // 获取组合代码对应的证券代码的信息
				ArrayList alMTV = (ArrayList) allMtvMethodHm.get(arrPortCode[i]); // 获取组合代码对应的估值方法信息

				// 若组合代码对应的证券代码不为空和组合代码对应的估值方法信息不为空
				if (alSec != null && alMTV != null) {
					Iterator secValue = alSec.values().iterator(); // 获取组合代码对应的证券代码的迭代器

					while (secValue.hasNext()) { // 若指针可以后移
						etfTradeSelDet = (ETFTradeSettleDetailBean)secValue.next();// 则指针后移
						securityCode = etfTradeSelDet.getSecurityCode();
						getAnotherOutterMTVMethod(arrPortCode[i], securityCode, alMTV, etfTradeSelDet, dDate); // 获取中保的证券的估值方法和行情
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("获取中保的证券对应的估值方法和行情数据出错", e);
		}
	}

	/**
	 * 获取中保的证券的估值方法和行情
	 * 
	 * @param portCode
	 *            String 组合代码
	 * @param securityCode
	 *            String 证券代码
	 * @param alMTV
	 *            ArrayList 组合代码对应的估值方法的ArrayList
	 * @param securityStorage
	 *            SecurityStorageBean 证券库存实例
	 * @param dDate
	 *            Date 估值日期
	 * @throws YssException
	 */
	private void getAnotherOutterMTVMethod(String portCode, String securityCode, ArrayList alMTV, ETFTradeSettleDetailBean etfTradeSelDet,
			java.util.Date dDate) throws YssException {
		Object price = null; // 行情价格

		MTVMethodBean mtvMethod = null; // 估值方法实例
		MTVMethodBean hasPortMtvMethod = null; // 带组合代码的估值方法实例
		MTVMethodBean noPortMtvMethod = null; // 不带组和代码的估值方法实例
		MTVMethodBean finalMtvMethod = null; // 最终的估值方法实例

		MarketValueBean hasPortMktValues = null; // 带组合代码的行情实例
		MarketValueBean noPortMktValues = null; // 不带组合代码的行情实例
		MarketValueBean maxHasPortMktValues = null; // 最近的带组合代码的行情实例
		MarketValueBean maxNoPortMktValues = null; // 最近的不带组合代码的行情实例
		MarketValueBean finalMktValues = null; // 最终的行情实例

		java.util.Date hasPortDate = null; // 带组合代码的行情日期
		java.util.Date noPortDate = null; // 不带组和代码的行情日期
		String strSql = ""; // 用于储存sql语句
		ResultSet rs = null; // 声明结果集
		ValMktPriceBean valMktPrice = null;
		try {
			for (int i = 0; i < alMTV.size(); i++) { // 循环估值方法
				mtvMethod = (MTVMethodBean) alMTV.get(i); // 当前的估值方法实例

				// 在最近的行情数据中查找带组合代码的对应证券代码和估值方法行情来源代码的行情实例
				hasPortMktValues = (MarketValueBean) mktOrExValueHm.get(portCode + securityCode + mtvMethod.getMktSrcCode());

				// 在最近的行情数据中查找不带组合代码的对应证券代码和估值方法行情来源代码的行情实例
				noPortMktValues = (MarketValueBean) mktOrExValueHm.get("" + securityCode + mtvMethod.getMktSrcCode());

				// 若有带组合代码的行情实例
				if (hasPortMktValues != null) {
					// 判断带组合代码的行情日期为空或（带组合的行情日期不为空且带组合代码的行情日期早于带组合代码的行情实例的行情日期）
					if (hasPortDate == null || (hasPortDate != null && hasPortDate.before(YssFun.parseDate(hasPortMktValues.getStrMktValueDate())))) {
						hasPortDate = YssFun.parseDate(hasPortMktValues.getStrMktValueDate()); // 则设置带组合的行情日期为带组合代码的行情实例的行情日期
						hasPortMtvMethod = mtvMethod; // 设置当前估值方法实例为带组合代码的估值方法实例
						maxHasPortMktValues = hasPortMktValues; // 设置最近的带组合代码的行情实例为带组合代码的行情实例
					}
				}

				// 若有不带组合代码的行情实例
				if (noPortMktValues != null) {
					// 判断不带组合代码的行情日期为空或（不带组合的行情日期不为空且不带组合代码的行情日期早于不带组合代码的行情实例的行情日期）
					if (noPortDate == null || (noPortDate != null && noPortDate.before(YssFun.parseDate(noPortMktValues.getStrMktValueDate())))) {
						noPortDate = YssFun.parseDate(noPortMktValues.getStrMktValueDate()); // 则设置不带组合的行情日期为不带组合代码的行情实例的行情日期
						noPortMtvMethod = mtvMethod; // 设置当前估值方法实例为不带组合代码的估值方法实例
						maxNoPortMktValues = noPortMktValues; // 设置最近的不带组合代码的行情实例为不带组合代码的行情实例
					}
				}
			}

			// 若循环结束后，带组合代码的日期为空且不带组合代码的日期也为空
			if (hasPortDate == null && noPortDate == null) {
				return; // 则返回
			}

			// 若带组合代码的日期为空且不带组合代码的日期不为空
			// 或带组合代码的日期不为空且不带组合代码的日期不为空且带组合码的日期在不带组合代码的日期之前
			if ((hasPortDate == null && noPortDate != null) || (hasPortDate != null && noPortDate != null && (hasPortDate.before(noPortDate)))) {
				finalMtvMethod = noPortMtvMethod; // 则最终的估值方法为不带组合代码的估值方法
				finalMktValues = maxNoPortMktValues; // 最终的估值行情为最近的不带组合代码的行情实例
			}

			// 若带组合代码的日期不为空且不带组合代码的日期为空
			// 或带组合代码的日期不为空且不带组合代码的日期不为空且带组合代码的日期在不带组合代码的日期之后
			// 或带组合代码的日期不为空且不带组合代码的日期不为空且带组合代码的日期等于不带组合代码的日期
			if ((hasPortDate != null && noPortDate == null) || (hasPortDate != null && noPortDate != null && (hasPortDate.after(noPortDate)))
					|| (hasPortDate != null && noPortDate != null && hasPortDate.equals(noPortDate))) {
				finalMtvMethod = hasPortMtvMethod; // 则最终的估值方法为带组合代码的估值方法
				finalMktValues = maxHasPortMktValues; // 最终的估值行情为最近的带组合代码的行情实例
			}

			// 若最终的估值方法和最终的估值行情都不为空
			if (finalMtvMethod != null && finalMktValues != null) {
				valMktPrice = new ValMktPriceBean(); // 则新建估值行情实例
				valMktPrice.setValDate(dDate); // 设置估值日期
				valMktPrice.setPortCode(portCode); // 设置组合代码
				valMktPrice.setSecurityCode(securityCode); // 设置证券代码

				price = finalMktValues.getPrice().get(finalMtvMethod.getMktPriceCode()); // 获取行情价格

				SecurityBean security = etfTradeSelDet.getSecurityBean(); // 获取证券实例

				valMktPrice.setPrice(YssD.div(Double.parseDouble(price.toString()), security.getFactor(), 12)); // 设置行情价格
				valMktPrice.setOtPrice1(Double.parseDouble((finalMktValues.getPrice().get("FOpenPrice")).toString()));//2010.01.15 add by songjie 
				valMktPrice.setMtvCode(finalMtvMethod.getMTVCode()); // 设置估值方法代码
				valMktPrice.setMarketValueDate(YssFun.toDate(finalMktValues.getStrMktValueDate())); // 设置行情日期
				valMktPrice.setValType("SecsMV"); // 设置估值类型为SecsMV

				if (security.getSubCategoryCode().equals("FU")) {
					valMktPrice.setValType("IndexFutruesMV");
				}

				if (security.getSubCategoryCode().equals("OP02")) { // 若品种子类型为OP02
					strSql = " select * from " + pub.yssGetTableName("Tb_Data_RightsIssue") + " where FCheckState = 1 and "
							+ dbl.sqlDate(finalMktValues.getStrMktValueDate()) + " between FExRightDate and FExpirationDate and FSecurityCode = "
							+ dbl.sqlString(securityCode);
					rs = dbl.openResultSet(strSql); // 则查询FRIPrice字段的数据
					while (rs.next()) {
						valMktPrice.setOtPrice1(rs.getDouble("FRIPrice")); // 设置OTPrice1字段的数据
					}
				}
				
				alMtvMktOrExValue.add(valMktPrice); // 添加估值行情实例到alMtvMktOrExValue
			}
		} catch (Exception e) {
			throw new YssException("选择中保的证券的估值方法和行情时出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 将证券对应的估值行情数据插入到估值行情表中
	 * 
	 * @param portCodes
	 *            String 组合代码
	 * @param dDate
	 *            Date 估值日期
	 * @throws YssException
	 */
	private void insertValMktPrice(String portCodes, java.util.Date dDate) throws YssException, SQLException {
		String strSql = ""; // 用于储存sql语句
		PreparedStatement pst = null; // 声明PreparedStatement
		Connection conn = dbl.loadConnection(); // 获取数据库连接
		boolean bTrans = false;
		ValMktPriceBean valMktPrice = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			Iterator iterator = alMtvMktOrExValue.iterator(); // 得到储存估值行情的ArrayList的迭代器

			strSql = " delete from " + pub.yssGetTableName("Tb_Data_PretValMktPrice") + " where FValDate = " + dbl.sqlDate(dDate)
			+ " and FPortCode in(" + operSql.sqlCodes(portCodes) + ")";
			
			dbl.executeSql(strSql);

			strSql = "insert into " + pub.yssGetTableName("Tb_Data_PretValMktPrice")
					+ " (FValDate,FPortCode,FSecurityCode,FPrice,FOTPrice1,FOTPrice2,FOTPrice3,FCheckState,FCreator,FCreateTime,"
					+ " FCheckUser,FCheckTime,FMarketStatus,FMTvCode,FValType,FMarketValueDate)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);

			while (iterator.hasNext()) {
				valMktPrice = (ValMktPriceBean) iterator.next();
				
				pst.setDate(1, YssFun.toSqlDate(valMktPrice.getValDate()));
				pst.setString(2, valMktPrice.getPortCode() + "");
				pst.setString(3, valMktPrice.getSecurityCode() + "");
				pst.setDouble(4, valMktPrice.getPrice());
				pst.setDouble(5, valMktPrice.getOtPrice1());
				pst.setDouble(6, valMktPrice.getOtPrice2());
				pst.setDouble(7, valMktPrice.getOtPrice3());
				pst.setInt(8, 1);
				pst.setString(9, pub.getUserCode());
				pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
				pst.setString(11, pub.getUserCode());
				pst.setString(12, YssFun.formatDatetime(new java.util.Date()));
				pst.setString(13, valMktPrice.getMarketStatus() + "");
				pst.setString(14, valMktPrice.getMtvCode() + "");
				pst.setString(15, valMktPrice.getValType() + "");
				pst.setDate(16, YssFun.toSqlDate(valMktPrice.getMarketValueDate()));

				pst.addBatch(); // 将执行语句添加到批处理中
			}
			
			pst.executeBatch(); // 批处理插入估值行情表的语句
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("数据插入估值行情表时出错", e);
		} finally {
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 获取当天的所有持仓证券代码对应的币种类型 要查所有非基础货币的货币信息
	 * 
	 * @param portCodes
	 *            String 组和代码
	 * @param dDate
	 *            Date 估值日期
	 * @throws YssException
	 */
	public void getMakeUpCurrencyBy(String portCodes, java.util.Date dDate) throws YssException {
		ResultSet rs = null; // 声明结果集
		String currencyCode = ""; // 币种代码
		String strSql = ""; // 用于储存sql语句
		String portCode = ""; // 组和代码
		ArrayList alCury = null; // 用于储存组合代码对应的币种代码
		alMtvMktOrExValue = new ArrayList(); // 新建ArrayList用于储存所有汇率来源的最近的汇率信息
		portSecHm = new HashMap(); // 用于储存组合代码对应的币种信息的ArrayList
		String[] strPortCodes = null;
		try {
			strSql = " select distinct FPortCode,FCuryCode from (select makeup.FSecurityCode,makeup.FPortCode,sec.Ftradecury as FCuryCode " 
				+ " from (select distinct FSecurityCode,FPortCode from (select a.FNum, a.FPortcode, a.FSecuritycode,a.Fbuydate, "  
				+ " b.FRemaindAmount as FRemaindAmount, b.FRefNum as FRefNum, b.FMakeUpDate as FMakeUpDate from " 
				+ pub.yssGetTableName("Tb_ETF_TradeStlDtl")
				+ " a left join (select max(FMakeUpDate) as FMakeUpDate, FRemaindAmount, FNum, FRefNum from " 
				+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + " where FMakeUpDate < " + dbl.sqlDate(dDate) 
				+ " group by FRemaindAmount, FNum, FRefNum) b on a.FNum = b.FNum order by a.FNum) c "
				+ " where ((c.FRemaindAmount <> 0 and c.FMakeUpDate < " + dbl.sqlDate(dDate) 
				+ ") or FNum not in (select distinct FNum from " 
				+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + " where FMakeUpDate < " + dbl.sqlDate(dDate) 
				+ ")) and FBuyDate <= " + dbl.sqlDate(dDate) + " and FNum in (select distinct FNum from " 
				+ pub.yssGetTableName("Tb_ETF_TradeStlDtl") + " where FPortCode in (" + operSql.sqlCodes(portCodes) 
				+ ")) and FSecurityCode <> ' '" 
				+ " union select distinct FSecurityCode,FPortCode from " + pub.yssGetTableName("Tb_ETF_GHInterface")
				+ " where FBargainDate = " + dbl.sqlDate(dDate) + " and FPortCode in(" + operSql.sqlCodes(portCodes) 
				+ ") and FSecurityCode in (select FSecurityCode from " + pub.yssGetTableName("Tb_Etf_Stocklist")
				+ " where FDate = " + dbl.sqlDate(dDate) + " and FReplaceMark = '1') "
				+ " and FAppNum <> 'ETFJIJIN' union select FSecurityCode, " + dbl.sqlString(portCodes) + " from " + pub.yssGetTableName("Tb_Etf_Stocklist")
				+ " where FDate = " + dbl.sqlDate(dDate) + " and FReplaceMark in( '1','5') "
                //取替代标识为可以现金替代的证券 深交所：1 上交所：5
                //STORY #1434 QDV4易方达基金2011年7月27日01_A panjunfang modify 20111130
				+ " ) makeup left join " + pub.yssGetTableName("Tb_Para_Security") 
				+ " sec on makeup.FSecurityCode = sec.Fsecuritycode)";

			rs = dbl.openResultSet(strSql);

			while (rs.next()) {
				currencyCode = rs.getString("FCuryCode"); // 获取币种代码
				portCode = rs.getString("FPortCode"); // 获取组合代码
				
				//若为全额现金替代
				if(portCode.equals(" ")){
					strPortCodes = portCodes.split(",");
					for(int i = 0; i < strPortCodes.length; i++){
						if (!portSecHm.containsKey(portCode)) { // 若portSecHm不包含组合代码
							alCury = new ArrayList(); // 新建一个ArrayList
							alCury.add(currencyCode); // 添加币种代码到alCury
							portSecHm.put(portCode, alCury); // 添加组合代码,组合代码对应的币种的arrayList到portSecHm
						} else {
							alCury = (ArrayList) portSecHm.get(portCode); // 获取组和代码对应的币种代码的ArrayList
							alCury.add(currencyCode); // 添加币种代码到alCury
							portSecHm.put(portCode, alCury); // 更新组合代码对应的币种代码的ArrayList
						}
					}
				}

				if (!portSecHm.containsKey(portCode)) { // 若portSecHm不包含组合代码
					alCury = new ArrayList(); // 新建一个ArrayList
					alCury.add(currencyCode); // 添加币种代码到alCury
					portSecHm.put(portCode, alCury); // 添加组合代码,组合代码对应的币种的arrayList到portSecHm
				} else {
					alCury = (ArrayList) portSecHm.get(portCode); // 获取组和代码对应的币种代码的ArrayList
					alCury.add(currencyCode); // 添加币种代码到alCury
					portSecHm.put(portCode, alCury); // 更新组合代码对应的币种代码的ArrayList
				}
			}
		} catch (Exception e) {
			throw new YssException("获取组合代码对应的币种信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取证券代码的币种对应的汇率类 要查所有非基础货币的货币信息
	 * @param securityCode String
	 * @param portCode String
	 * @param dDate Date
	 * @throws YssException
	 */
	public void getExRateBy(String portCodes, java.util.Date dDate) throws YssException {
		String strSql = null; // 用于储存sql语句
		ResultSet rs = null; // 声明结果集
		/**shashijie 2012-12-12 STORY 3328 注释 STORY 3416的代码*/
		//String baseRateSrc = paramSet.getsBaseRateSrcCode();  //add by zhaoxianlin 20121019 #STORY 3146
		/**end shashijie 2012-12-12 STORY 3328*/
		mktOrExValueHm = new HashMap(); // 新建HashMap用于储存所有汇率来源的最近的汇率信息
		try {
			strSql = " select b.fportCode,b.fexratesrccode,b.fcurycode,b.fmarkcury,b.fexratedate,b.fexratetime,b.fexrate1,"
					+ " b.fexrate2,b.fexrate3,b.fexrate4,b.fexrate5,b.fexrate6,b.fexrate7,b.fexrate8,b.fcheckstate "
					+ " from (select FPortCode,FExRateSrcCode, max(FExrateDate) as FExrateDate, FCuryCode from "
					+ pub.yssGetTableName("tb_data_exchangerate") + " exr1 where FCheckState = 1 and FCuryCode <> "
					+ dbl.sqlString(pub.getBaseCury()) + " and FExrateDate <= " + dbl.sqlDate(dDate)
					+ " group by FPortCode,FExRateSrcCode, FCuryCode order by FExrateDate desc) a left join (select * from "
					+ pub.yssGetTableName("tb_data_exchangerate") + " exr where exists (" 
					+ " select distinct FCuryCode from (select makeup.FSecurityCode, makeup.FPortCode, sec.Ftradecury as FCuryCode "
					+ " from (select distinct FSecurityCode, FPortCode from (select a.FNum, a.FPortcode, a.FSecuritycode,a.FBuyDate, "
					+ " b.FRemaindAmount as FRemaindAmount, b.FRefNum as FRefNum,b.FMakeUpDate as FMakeUpDate from " 
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl") 
					+ " a left join (select max(FMakeUpDate) as FMakeUpDate, FRemaindAmount, FNum, FRefNum from " 
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + " where FMakeUpDate < " + dbl.sqlDate(dDate)
					+ " group by FRemaindAmount, FNum, FRefNum) b on a.FNum = b.FNum order by a.FNum) c "
					+ " where ((c.FRemaindAmount <> 0 and c.FMakeUpDate < " + dbl.sqlDate(dDate) 
					+ ") or FNum not in (select distinct FNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") 
					+ " where FMakeUpDate < " + dbl.sqlDate(dDate) + ")) and FBuyDate <= " + dbl.sqlDate(dDate) 
					+ " and FNum in (select distinct FNum from " 
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl") + " where FPortCode in (" 
					+ operSql.sqlCodes(portCodes) + ")) and FSecurityCode <> ' '" 
//					+ " union select distinct FSecurityCode, FPortCode from " + pub.yssGetTableName("Tb_ETF_GHInterface")
//					+ " where FBargainDate = " + dbl.sqlDate(dDate) + " and FPortCode in(" + operSql.sqlCodes(portCodes) 
//					+ " ) and FSecurityCode in (select FSecurityCode from " + pub.yssGetTableName("Tb_Etf_Stocklist") 
//					+ " where FDate = " + dbl.sqlDate(dDate) + " and FReplaceMark = '1') and FAppNum <> 'ETFJIJIN' " 
					+ " union select FSecurityCode, FPortCode from " + pub.yssGetTableName("Tb_Etf_Stocklist") 
					+ " where FDate = " + dbl.sqlDate(dDate)
					+ " AND FPortCode IN (" + operSql.sqlCodes(portCodes)
					+ " )) makeup left join " + pub.yssGetTableName("Tb_Para_Security") 
					+ " sec on makeup.FSecurityCode = sec.Fsecuritycode)) and exr.FCheckState = 1) b on a.fexratesrccode = b.fexratesrccode "
					+ " and a.FExrateDate = b.FExrateDate and a.FCuryCode = b.FCuryCode and a.FPortCode = b.FPortCode " 
					+ " where B.fexratesrccode is not null order by a.FCuryCode ";
			/**shashijie 2012-12-12 STORY 3328 注释 STORY 3416的代码*/
			//----add by zhaoxianlin 20121019 #STORY 3146------ 
			/*if(baseRateSrc!=null){
				strSql=strSql.replaceFirst("group by ", " and FExRateSrcCode = " +dbl.sqlString(baseRateSrc) +" group by ");
			}*/
			//-----end ------------------------------
			/**end*/
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				setExchangeRate(rs); // 用于设置汇率对象的属性
			}
		} catch (Exception e) {
			throw new YssException("获取证券币种对应的汇率行情出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 用于设置汇率对象的属性
	 * 
	 * @param rs
	 *            ResultSet 结果集实例
	 */
	private void setExchangeRate(ResultSet rs) throws SQLException {
		ExchangeRateBean exchangeRate = new ExchangeRateBean(); // 新建汇率实例

		exchangeRate.setStrExRateSrcCode(rs.getString("FExrateSrcCode")); // 设置汇率来源代码
		exchangeRate.setStrCuryCode(rs.getString("FCuryCode")); // 设置币种代码
		exchangeRate.setStrMarkCuryCode(rs.getString("FMarkCury")); // 设置基准货币代码
		exchangeRate.setStrExRateDate(rs.getDate("FExrateDate").toString()); // 设置汇率日期
		exchangeRate.setStrExRateTime(rs.getString("FExrateTime")); // 设置汇率时间
		exchangeRate.setStrPortCode(rs.getString("FPortCode")); // 设置组合代码
		exchangeRate.setStrExRate1(rs.getDouble("FExRate1")); // 设置汇率1
		exchangeRate.setStrExRate2(rs.getDouble("FExRate2")); // 设置汇率2
		exchangeRate.setStrExRate3(rs.getDouble("FExRate3")); // 设置汇率3
		exchangeRate.setStrExRate4(rs.getDouble("FExRate4")); // 设置汇率4
		exchangeRate.setStrExRate5(rs.getDouble("FExRate5")); // 设置汇率5
		exchangeRate.setStrExRate6(rs.getDouble("FExRate6")); // 设置汇率6
		exchangeRate.setStrExRate7(rs.getDouble("FExRate7")); // 设置汇率7
		exchangeRate.setStrExRate8(rs.getDouble("FExRate8")); // 设置汇率8

		setRate(exchangeRate, rs); // 设置汇率对象的属性

		mktOrExValueHm.put(rs.getString("FPortCode").trim() + rs.getString("FCuryCode")
				+ rs.getString("FExrateSrcCode"), exchangeRate); // 将组合代码，币种代码汇率来源代码及汇率实例储存到mktOrExValueHm中
	}

	/**
	 * 用于设置汇率对象的属性
	 * 
	 * @param rs
	 *            ResultSet
	 */
	private void setRate(ExchangeRateBean exchangeRate, ResultSet rs) throws SQLException {
		// 新建HashMap用于储存汇率数据表中汇率数据字段如FExRate1对应的数据
		java.util.HashMap rate = new HashMap();

		rate.put("FExRate1", new Double(rs.getDouble("FExRate1")));
		rate.put("FExRate2", new Double(rs.getDouble("FExRate2")));
		rate.put("FExRate3", new Double(rs.getDouble("FExRate3")));
		rate.put("FExRate4", new Double(rs.getDouble("FExRate4")));
		rate.put("FExRate5", new Double(rs.getDouble("FExRate5")));
		rate.put("FExRate6", new Double(rs.getDouble("FExRate6")));
		rate.put("FExRate7", new Double(rs.getDouble("FExRate7")));
		rate.put("FExRate8", new Double(rs.getDouble("FExRate8")));

		exchangeRate.setRate(rate); // 设置汇率数据信息
	}

	/**
	 * 存放所有要估值的组合代码下的基础货币信息，算出组合汇率，然后存放到 alMtvMktOrExValue
	 * 
	 * @param portCode
	 *            String 组合代码
	 * @param dDate
	 *            Date 估值日期
	 * @throws YssException
	 */
	private void putBaseRateInHt(String portCode, java.util.Date dDate) throws YssException {
		double portRate = 1;
		EachRateOper eachRateOper = null;
		try {
			eachRateOper = new EachRateOper(); // 新建EachRateOper实例
			eachRateOper.setYssPub(pub); // 设置pub
			ValExRateBean valExRate = new ValExRateBean(); // 新建估值汇率实例
			valExRate.setValDate(dDate); // 设置估值日期
			valExRate.setPortCode(portCode); // 设置组合代码
			valExRate.setCuryCode(pub.getBaseCury()); // 设置基础货币代码
			valExRate.setBaseRate(1); // 设置基础汇率为1
			eachRateOper.getInnerPortRate(dDate, pub.getBaseCury(), portCode, "", "", "", ""); // 调用折算组合汇率的方法
			portRate = eachRateOper.getDPortRate(); // 得到组合汇率
			if(portRate == 0){
				portRate = 1;
			}
			valExRate.setPortRate(portRate); // 设置组合汇率
			valExRate.setExchangeRateDate(dDate); // 设置汇率日期
			alMtvMktOrExValue.add(valExRate); // 将估值汇率实例添加到alMtvMktOrExValue
		} catch (Exception e) {
			throw new YssException("往储存汇率数据的哈希表中插入基础汇率数据时出错", e);
		}
	}

	/**
	 * 获取当天证券的币种代码对应的估值方法和汇率数据
	 * 
	 * @param portCodes
	 *            String 组合代码
	 * @param dDate
	 *            Date 估值日期
	 * @param ctlpubpara
	 *            CtlPubPara 通用参数实例
	 * @throws YssException
	 */
	private void getExMTVMethodBy(String portCodes, java.util.Date dDate, CtlPubPara ctlpubpara) throws YssException {
		String[] arrPortCode = null; // 用于储存拆分后的组合代码
		String curyCode = null; // 币种代码
		ArrayList alMTV = null; // 用于储存组合代码对应的估值方法实例的ArrayList
		ArrayList alCury = null; // 用语储存组和代码对应的币种代码的ArrayList
		String priMarketPrice = null; // 用于判断是日期优先还是估值方法优先
		String strSql = ""; // 用于储存sql语句
		ResultSet rs = null; // 用于声明结果集
		ResultSet rs1 = null; // 用于声明结果集
		String portCury = null; // 组合货币
		ExchangeRateBean exchangeRate = null;
		portRateHm = new HashMap();
		portCuryHm = new HashMap();
		String portCode = null;
		try {
			priMarketPrice = ctlpubpara.getPriMarketPrice(); // 得到判断是日期优先还是估值方法优先的参数
			arrPortCode = portCodes.split(","); // 拆分组合代码

			strSql = "select FPortCury,FPortCode from " + pub.yssGetTableName("tb_Para_Portfolio") + " where FPortCode in("
					+ operSql.sqlCodes(portCodes) + ")";
			rs = dbl.openResultSet(strSql); // 查询组合代码对应的组合货币代码

			while (rs.next()) {
				portCury = rs.getString("FPortCury"); // 获取组合货币代码
				portCode = rs.getString("FPortCode"); // 获取组合代码
				portCuryHm.put(portCode, portCury);

				strSql = " select b.* from (select FExRateSrcCode,max(FExrateDate) as FExrateDate,FCuryCode from "
						+ pub.yssGetTableName("tb_data_exchangerate") + " exc where FCheckState = 1 and FCuryCode = " + dbl.sqlString(portCury)
						+ " and FExrateDate <= " + dbl.sqlDate(dDate)
						+ " group by FExRateSrcCode,FCuryCode order by FExrateDate desc)a left join (select * from "
						+ pub.yssGetTableName("tb_data_exchangerate") + " ) b on b.Fexratesrccode = a.Fexratesrccode "
						+ " and b.Fexratedate = a.Fexratedate and b.FCuryCode = a.Fcurycode " + " where b.FPortCode in(' ',"
						+ dbl.sqlString(portCode) + ") and FCheckState = 1";
				rs1 = dbl.openResultSet(strSql); // 查询组合货币最近的汇率信息
				while (rs1.next()) {
					exchangeRate = new ExchangeRateBean(); // 新建汇率实例
					exchangeRate.setStrExRateSrcCode(rs1.getString("FExRateSrcCode")); // 设置汇率来源代码
					exchangeRate.setStrCuryCode(rs1.getString("FCuryCode")); // 设置币种代码
					exchangeRate.setStrMarkCuryCode(rs1.getString("FMarkCury")); // 设置基准货币代码
					exchangeRate.setStrExRateDate(rs1.getDate("FExrateDate").toString()); // 设置汇率日期
					exchangeRate.setStrExRateTime(rs1.getString("FExrateTime")); // 设置汇率时间
					exchangeRate.setStrPortCode(rs1.getString("FPortCode")); // 设置组合代码
					setRate(exchangeRate, rs1); // 设置汇率对象的属性
					portRateHm.put(rs1.getString("FPortCode").trim() + rs1.getString("FExRateSrcCode"), exchangeRate); // 将组合代码和汇率来源代码对应的汇率信息储存到HashMap中
				}
				dbl.closeResultSetFinal(rs1);
			}

			for (int i = 0; i < arrPortCode.length; i++) { // 循环组合代码
				alCury = (ArrayList) portSecHm.get(arrPortCode[i]); // 得到组合代码对应的币种代码的ArrayList
				alMTV = (ArrayList) allMtvMethodHm.get(arrPortCode[i]); // 得到组合代码对应的估值方法实例的ArrayList

				// 若组合代码对应的币种代码的ArrayList不为空且组合代码对应的估值方法实例的ArrayList也不为空
				if (alCury != null && alMTV != null) {
					for (int j = 0; j < alCury.size(); j++) { // 循环组合代码对应的币种

						curyCode = (String) alCury.get(j); // 得到当前币种

						if (priMarketPrice.equalsIgnoreCase("valuation")) { // 若估值方法优先
							getSubExMTVMethod(arrPortCode[i], curyCode, alMTV, dDate); // 用于获取国内公司的币种对应的估值方法和汇率数据
						} else if (priMarketPrice.equalsIgnoreCase("day")) { // 若日期优先
							getSubOutterExMTVMethodBy(arrPortCode[i], curyCode, alMTV, dDate); // 用于获取中保公司的币种对应的估值方法和汇率数据
						}
					}
				}
				putBaseRateInHt(arrPortCode[i], dDate); // 存放所有要估值的组合代码下的基础货币信息，算出组合汇率，然后存放到
														// alMtvMktOrExValue
			}

		} catch (Exception e) {
			throw new YssException("获取证券币种对应的估值方法和汇率数据出错", e);
		} finally {
			dbl.closeResultSetFinal(rs, rs1);
		}
	}

	/**
	 * 用于获取国内公司的币种对应的估值方法和汇率数据
	 * @param portCode String 组合代码
	 * @param curyCode String 币种代码
	 * @param alCury ArrayList 组合代码对应的币种代码的ArrayList
	 * @param alMTV ArrayList 组合代码对应的估值方法实例的ArrayList
	 * @param dDate Date 估值日期
	 * @throws YssException
	 */
	private void getSubExMTVMethod(String portCode, String curyCode, ArrayList alMTV, java.util.Date dDate) throws YssException {
		double rate = 1; // 初始化基础汇率利率
		MTVMethodBean mtvMethod = null; // 声明估值方法实例
		ExchangeRateBean exchangeBaseRate = null; // 声明基础汇率实例
		ExchangeRateBean exchangeRate = null;
		boolean canSave = false;
		ValExRateBean valExRate = null;
		EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        /**shashijie 2012-12-12 STORY 3328 注释 STORY 3416的代码*/
        /*double tempBaseRate= 1; //add by zhaoxianlin 20121019 #STORY 3146
        double tempPortRate = 1;*///add by zhaoxianlin 20121019 #STORY 3146
        /**end*/
		try {
			for (int i = 0; i < alMTV.size(); i++) { // 循环组合代码对应的估值方法
				mtvMethod = (MTVMethodBean) alMTV.get(i); // 得到当前的估值方法实例
				if (curyCode.equals(portCuryHm.get(portCode))) { // 表示当前币种代码为当前组合代码的组合货币代码
					exchangeBaseRate = (ExchangeRateBean) mktOrExValueHm.get(portCode + curyCode + mtvMethod.getPortRateSrcCode());
					if (exchangeBaseRate == null) {
						exchangeBaseRate = (ExchangeRateBean) mktOrExValueHm.get("" + curyCode + mtvMethod.getPortRateSrcCode());
					}
					if (exchangeBaseRate != null) {
						if (exchangeBaseRate.getRate().get(mtvMethod.getPortRateCode()) != null) {
							canSave = true;
						}
					}
				} else {
					// 先判断哈希表中是否有主键字段中的组合代码不为空的汇率信息，若有值的话，就优先取值
					exchangeBaseRate = (ExchangeRateBean) mktOrExValueHm.get(portCode + curyCode + mtvMethod.getBaseRateSrcCode());

					// 若哈希表中没有主键字段中的组合代码不为空的基础汇率和组合汇率信息，则判断表中
					// 是否有主键字段中的组合代码为空的基础汇率和组合汇率信息，有的话，就取值
					if (exchangeBaseRate == null) {
						exchangeBaseRate = (ExchangeRateBean) mktOrExValueHm.get("" + curyCode + mtvMethod.getBaseRateSrcCode());
						/**shashijie 2012-12-12 STORY 3328 注释 STORY 3416的代码*/
						//----add by zhaoxianlin 20121019 #STORY 3146------ 
						/*if(paramExRate){
							exchangeBaseRate = (ExchangeRateBean) mktOrExValueHm.get("" + curyCode + paramSet.getsBaseRateSrcCode() );
						}*/
						//-----end ------------------------------
						/**end*/
						//modified by zhaoxianlin  20121017 STORY #3146 
					}
					if (exchangeBaseRate != null) {
						// 若有相应的汇率信息，那么就判断汇率数据中有没有估值方法中对应的基础汇率字段的信息，
						// 有的话就新建一个估值汇率实例，将汇率信息储存到实例的相应属性中，并将实例添加到用于储存估值汇率实例的ArrayList中
						/**shashijie 2012-12-12 STORY 3328 注释 STORY 3416的代码*/
						//modified by zhaoxianlin 20121019 #STORY 3146  当汇率来源从ETF参数设置中取值时为true
						/*if (exchangeBaseRate.getRate().get(mtvMethod.getBaseRateCode()) != null||exchangeBaseRate.getRate().get(paramSet.getsBaseRateCode()) != null) {
							canSave = true;
							if(exchangeBaseRate.getRate().get(paramSet.getsBaseRateCode())!=null){  //add by zhaoxianlin 20121019 #STORY 3146
								tempBaseRate = Double.parseDouble(exchangeBaseRate.getRate().get(paramSet.getsBaseRateCode()).toString());
							}
						}*/
						/**end*/
						if (exchangeBaseRate.getRate().get(mtvMethod.getBaseRateCode()) != null) {
							canSave = true;
						}
					}
				}
				if (canSave == true) {
					valExRate = new ValExRateBean();
					valExRate.setValDate(dDate);
					valExRate.setPortCode(portCode);
					valExRate.setCuryCode(curyCode);

					rate = this.getSettingOper().getCuryRate(dDate, mtvMethod.getBaseRateSrcCode(), mtvMethod.getBaseRateCode(),
							mtvMethod.getPortRateSrcCode(), mtvMethod.getPortRateCode(), curyCode, portCode, YssOperCons.YSS_RATE_BASE); // 折算基础汇率利率

					valExRate.setBaseRate(rate); // 设置基础汇率
					exchangeRate = (ExchangeRateBean) portRateHm.get(portCode + mtvMethod.getPortRateSrcCode()); // 获取组合代码级汇率来源代码对应的汇率实例
					if (exchangeRate == null) {
						exchangeRate = (ExchangeRateBean) portRateHm.get(mtvMethod.getPortRateSrcCode()); // 获取组合代码级汇率来源代码对应的汇率实例
					}

					if (exchangeRate != null) { // 若汇率实例不为空
						if (exchangeRate.getRate().get(mtvMethod.getPortRateCode()) != null) { // 若汇率实例中对应的估值方法的汇率字段不为空
							rateOper.getInnerPortRate(dDate, curyCode, portCode, mtvMethod.getBaseRateSrcCode(), mtvMethod.getBaseRateCode(),
									mtvMethod.getPortRateSrcCode(), mtvMethod.getPortRateCode()); //用通用方法，获取组合汇率
							
							valExRate.setPortRate(rateOper.getDPortRate());//获取组合汇率
						}
					}
					/**shashijie 2012-12-12 STORY 3328 注释 STORY 3416的代码*/
					//----add by zhaoxianlin 20121019 #STORY 3146------ 
					/*if(paramExRate){  //取ETF参数设置中的基础汇率、组合汇率。
//						rate=this.getSettingOper().getCuryRate(dDate,paramSet.getsBaseRateSrcCode(), paramSet.getsBaseRateCode(),
//								paramSet.getsPortRateSrcCode(), paramSet.getsPortRateCode(), curyCode, portCode, YssOperCons.YSS_RATE_BASE);
						valExRate.setBaseRate(tempBaseRate);
						//exchangeRate = (ExchangeRateBean) portRateHm.get(paramSet.getsPortRateSrcCode()); // 获取ETF参数设置中汇率来源代码对应的汇率实例
						if (exchangeRate != null) { 
//							if (exchangeRate.getRate().get(paramSet.getsPortRateCode()) != null) { // 若汇率实例中对应的估值方法的汇率字段不为空
//								rateOper.getInnerPortRate(dDate, curyCode, portCode, paramSet.getsBaseRateSrcCode(), paramSet.getsBaseRateCode(),
//										paramSet.getsPortRateSrcCode(), paramSet.getsPortRateCode()); //用通用方法，获取组合汇率
							tempPortRate = Double.parseDouble(exchangeRate.getRate().get(paramSet.getsPortRateCode()).toString());
						    valExRate.setPortRate(tempPortRate);//获取组合汇率
						}
						*//**end*//*
					}*/
					//-----end ------------------------------
					if(valExRate.getPortRate() == 0){
						valExRate.setPortRate(1);
					}
					// 则设置基础汇率实例的汇率日期为估值汇率实例的汇率日期
					valExRate.setExchangeRateDate(YssFun.toDate(exchangeBaseRate.getStrExRateDate()));

					alMtvMktOrExValue.add(valExRate); // 添加估值汇率实例到alMtvMktOrExValue
					return;
				}
			}
		} catch (Exception e) {
			throw new YssException("获取证券币种对应的估值方法和汇率数据出错", e);
		}
	}

	/**
	 * 获取中保当天证券代码对应的估值方法和汇率
	 * 
	 * @param portCode
	 *            String 组合代码
	 * @param curyCode
	 *            String 币种代码
	 * @param alMTV
	 *            ArrayList 组合代码对应的估值方法实例的ArrayList
	 * @param dDate
	 *            Date 估值日期
	 * @throws YssException
	 */
	private void getSubOutterExMTVMethodBy(String portCode, String curyCode, ArrayList alMTV, java.util.Date dDate) throws YssException {
		MTVMethodBean mtvMethod = null; // 估值方法实例
		MTVMethodBean hasPortMtvMethod = null; // 有组合的估值方法实例
		MTVMethodBean noPortMtvMethod = null; // 没有组合的估值方法实例
		MTVMethodBean finalMtvMethod = null; // 最终的估值方法实例

		ExchangeRateBean hasPortBExValues = null; // 有组合的基础汇率实例
		ExchangeRateBean noPortBExValues = null; // 没有组合的基础汇率实例

		ExchangeRateBean maxHasPortBExValues = null; // 日期最近的有组合的基础汇率实例
		ExchangeRateBean maxNoPortBExValues = null; // 日期最近的没有组合的基础汇率实例

		ExchangeRateBean finalBExValues = null; // 最终的基础汇率实例

		ExchangeRateBean exchangeRate = null;

		java.util.Date hasPortBDate = null; // 有组合的基础汇率的日期
		java.util.Date noPortBDate = null; // 没有组合的基础汇率的日期

		java.util.Date finalBDate = null; // 最近的基础汇率的日期

		double baseRate = 1; // 初始化基础汇率
		ValExRateBean valExRate = null;
		try {
			for (int i = 0; i < alMTV.size(); i++) { // 循环组合代码对应的估值方法
				mtvMethod = (MTVMethodBean) alMTV.get(i); // 得到当前的估值方法实例

				if (curyCode.equals(portCuryHm.get(portCode))) { // 表示当前币种代码为当前组合代码的组合货币代码
					// 在最近的汇率数据中获取有组合代码，币种代码，组合汇率来源代码信息的汇率实例
					hasPortBExValues = (ExchangeRateBean) mktOrExValueHm.get(portCode + curyCode + mtvMethod.getPortRateSrcCode());
					// 在最近的汇率数据中获取没有组合代码，币种代码，组合汇率来源代码信息的汇率实例
					noPortBExValues = (ExchangeRateBean) mktOrExValueHm.get("" + curyCode + mtvMethod.getPortRateSrcCode());

				} else {
					// 在最近的汇率数据中获取有组合代码，币种代码，基础汇率来源代码信息的汇率实例
					hasPortBExValues = (ExchangeRateBean) mktOrExValueHm.get(portCode + curyCode + mtvMethod.getBaseRateSrcCode());

					// 在最近的汇率数据中获取没有组合代码，币种代码，基础汇率来源代码信息的汇率实例
					noPortBExValues = (ExchangeRateBean) mktOrExValueHm.get("" + curyCode + mtvMethod.getBaseRateSrcCode());
				}

				// 若有组合的基础汇率实例不为空且有组合的组合汇率实例不为空
				if (hasPortBExValues != null) {
					// 若有组合的基础汇率日期为空且有组合的组合汇率日期为空
					if (hasPortBDate == null) {
						// 则设置有组合的基础汇率实例的汇率日期为有组合的基础汇率日期
						hasPortBDate = YssFun.parseDate(hasPortBExValues.getStrExRateDate());

						hasPortMtvMethod = mtvMethod; // 有组合的估值方法实例为当前的估值方法实例
						maxHasPortBExValues = hasPortBExValues; // 日期最近的有组合的基础汇率实例为有组合的基础汇率实例
					} else {
						// 若有组合的基础汇率的日期不为空且有组合的组合汇率的日期也不为空
						// 且有组合的基础汇率的日期在有组合的基础汇率实例的汇率日期之前
						if (hasPortBDate != null && hasPortBDate.before(YssFun.parseDate(hasPortBExValues.getStrExRateDate()))) {
							// 则设置有组合的基础汇率实例的汇率日期为有组合的基础汇率日期
							hasPortBDate = YssFun.parseDate(hasPortBExValues.getStrExRateDate());
							// 有组合的估值方法实例为当前的估值方法实例
							hasPortMtvMethod = mtvMethod;
							// 日期最近的有组合的基础汇率实例为有组合的基础汇率实例
							maxHasPortBExValues = hasPortBExValues;
						}
					}
				}

				// 若没有组合的基础汇率实例不为空且没有组合的组合汇率实例不为空
				if (noPortBExValues != null) {
					// 若没有有组合的基础汇率日期为空且没有组合的组合汇率日期为空
					if (noPortBDate == null) {
						// 则设置没有组合的基础汇率实例的汇率日期为没有组合的基础汇率日期
						noPortBDate = YssFun.parseDate(noPortBExValues.getStrExRateDate());

						// 没有组合的估值方法实例为当前的估值方法实例
						noPortMtvMethod = mtvMethod;

						// 日期最近的没有组合的基础汇率实例为没有组合的基础汇率实例
						maxNoPortBExValues = noPortBExValues;
					} else {
						// 若没有组合的基础汇率的日期不为空且没有组合的组合汇率的日期也不为空
						// 且没有组合的基础汇率的日期在没有组合的基础汇率实例的汇率日期之前
						if (noPortBDate != null && noPortBDate.before(YssFun.parseDate(noPortBExValues.getStrExRateDate()))) {
							// 则设置没有组合的基础汇率实例的汇率日期为没有组合的基础汇率日期
							noPortBDate = YssFun.parseDate(noPortBExValues.getStrExRateDate());

							// 没有组合的估值方法实例为当前的估值方法实例
							noPortMtvMethod = mtvMethod;

							// 日期最近的没有组合的基础汇率实例为没有组合的基础汇率实例
							maxNoPortBExValues = noPortBExValues;
						}
					}
				}
			}

			// 若有组合的基础汇率日期，有组合的组合汇率日期，没有组合的基础汇率日期，没有组合的组合汇率日期都为空则返回
			if (hasPortBDate == null && noPortBDate == null) {
				return;
			}

			// 若有组合的基础汇率日期为空且没有组合的基础汇率日期不为空
			// 或有组合的基础汇率日期，没有组合的基础汇率日期都不为空
			// 且有组合的基础汇率日期早于没有组合的基础汇率日期
			if ((hasPortBDate == null && noPortBDate != null) || (hasPortBDate != null && noPortBDate != null && (hasPortBDate.before(noPortBDate)))) {
				// 最终的基础汇率实例为最近的没有组合的基础汇率实例
				finalBExValues = maxNoPortBExValues;

				// 最终各的估值方法实例为没有组合的估值方法实例
				finalMtvMethod = noPortMtvMethod;
			}

			// 若有组合的基础汇率日期不为空且没有组合的基础汇率日期为空
			// 或有组合的基础汇率日期，没有组合的基础汇率日期都不为空且有组合的基础汇率日期晚于没有组合的基础汇率日期
			// 或有组合的基础汇率日期,没有组合的基础汇率日期都不为空且有组合的基础汇率日期等于没有组合的基础汇率日期
			if ((hasPortBDate != null && noPortBDate == null) || (hasPortBDate != null && noPortBDate != null && (hasPortBDate.after(noPortBDate)))
					|| (hasPortBDate != null && noPortBDate != null && hasPortBDate.equals(noPortBDate))) {
				// 最终的基础汇率实例为最的有组合的基础汇率实例
				finalBExValues = maxHasPortBExValues;

				// 最终各的估值方法实例为有组合的估值方法实例
				finalMtvMethod = hasPortMtvMethod;
			}

			// 若最终的估值方法实例不为空且最终的基础汇率实例不为空且最终的组合汇率实例不为空，则新建一个估值汇率实例，给实例赋相应属性并添加到alMtvMktOrExValue中
			if (finalMtvMethod != null && finalBExValues != null) {
				valExRate = new ValExRateBean();
				valExRate.setValDate(dDate);
				valExRate.setPortCode(portCode);
				valExRate.setCuryCode(curyCode);

				baseRate = this.getSettingOper().getCuryRate(dDate, finalMtvMethod.getBaseRateSrcCode(), finalMtvMethod.getBaseRateCode(),
						finalMtvMethod.getPortRateSrcCode(), finalMtvMethod.getPortRateCode(), curyCode, portCode, YssOperCons.YSS_RATE_BASE);

				valExRate.setBaseRate(baseRate);

				exchangeRate = (ExchangeRateBean) portRateHm.get(portCode + mtvMethod.getPortRateSrcCode()); // 获取组合代码级汇率来源代码对应的汇率实例

				if (exchangeRate == null) {
					exchangeRate = (ExchangeRateBean) portRateHm.get(mtvMethod.getPortRateSrcCode()); // 获取组合代码级汇率来源代码对应的汇率实例
				}

				if (exchangeRate != null) { // 若汇率实例不为空
					if (exchangeRate.getRate().get(mtvMethod.getPortRateCode()) != null) { // 若汇率实例中对应的估值方法的汇率字段不为空
						valExRate.setPortRate(Double.parseDouble(exchangeRate.getRate().get(mtvMethod.getPortRateCode()).toString())); // 设置组合汇率
					}
				}
				
				if(valExRate.getPortRate() == 0){
					valExRate.setPortRate(1);
				}

				finalBDate = YssFun.parseDate(finalBExValues.getStrExRateDate());

				valExRate.setExchangeRateDate(finalBDate);

				alMtvMktOrExValue.add(valExRate);
			}
		} catch (Exception e) {
			throw new YssException("选择中保的证券的估值方法和汇率时出错", e);
		}
	}

	/**
	 * 将证券对应的行情数据插入到估值行情表中
	 * 
	 * @param alMtvMktOrExValue
	 *            ArrayList
	 * @param portCode
	 *            String
	 * @param dDate
	 *            Date
	 * @throws YssException
	 */
	private void insertValRate(String portCodes, java.util.Date dDate) throws YssException, SQLException {
		String strSql = ""; // 用于储存sql语句
		PreparedStatement pst = null;
		Connection conn = dbl.loadConnection(); // 得到数据库连接
		boolean bTrans = false;
		ValExRateBean valRate = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			Iterator iterator = alMtvMktOrExValue.iterator(); // 得到估值汇率的HashMap的迭代器

			strSql = " delete from " + pub.yssGetTableName("Tb_Data_PretValRate") 
				+ " where FValDate = " + dbl.sqlDate(dDate) + " and FPortCode in("
					+ operSql.sqlCodes(portCodes) + ")";
			dbl.executeSql(strSql); // 从股指汇率表中删除相应估值日期和组合代码对应的数据

			strSql = " insert into " + pub.yssGetTableName("Tb_Data_PretValRate")
					+ " (FValDate,FPortCode,FcuryCode,FBaseRate,FOTBaseRate1,FOTBaseRate2,FOTBaseRate3,"
					+ " FPortRate,FOTPortRate1,FOTPortRate2,FOTPortRate3,FCheckState,FCreator,FCreateTime, "
					+ " FCheckUser,FCheckTime,FExchangeRateDate)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			pst = conn.prepareStatement(strSql);

			while (iterator.hasNext()) {
				valRate = (ValExRateBean) iterator.next();
				pst.setDate(1, YssFun.toSqlDate(valRate.getValDate()));
				pst.setString(2, valRate.getPortCode() + "");
				pst.setString(3, valRate.getCuryCode() + "");
				pst.setDouble(4, valRate.getBaseRate());
				pst.setDouble(5, valRate.getOTBaseRate1());
				pst.setDouble(6, valRate.getOTBaseRate2());
				pst.setDouble(7, valRate.getOTBaseRate3());
				if (valRate.getPortRate() != 0) { // 若有组合汇率
					pst.setDouble(8, valRate.getPortRate()); // 则设置组合汇率
				} else { // 若没有组合汇率
					pst.setDouble(8, 0); // 则设置组合汇率为零
				}
				pst.setDouble(9, valRate.getOTPortRate1());
				pst.setDouble(10, valRate.getOTPortRate2());
				pst.setDouble(11, valRate.getOTPortRate3());
				pst.setInt(12, 1);
				pst.setString(13, pub.getUserCode());
				pst.setString(14, YssFun.formatDatetime(new java.util.Date()));
				pst.setString(15, pub.getUserCode());
				pst.setString(16, YssFun.formatDatetime(new java.util.Date()));
				pst.setDate(17, YssFun.toSqlDate(valRate.getExchangeRateDate()));

				pst.addBatch();
			}
			
			pst.executeBatch(); // 批处理执行命令语句
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("数据插入估值汇率表时出错", e);
		} finally {
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**shashijie 2013-1-16 STORY 3402 修改:查看补票完成日与强制处理日,哪个长用哪个节假日倒推*/
	/**shashijie,2011-7-19, 向前推出int BeginSupply个工作日(考虑国内国外),STORY 1434*/
	private Date getWorkDayMake(Date dDate) throws YssException {
		Date mDate = null;//工作日
		String sDate = "";
		//补票完成日>强制处理日
		if (paramSet.getDealDayNum() >= paramSet.getLastestDealDayNum()) {
			sDate = paramSetAdmin.getWorkDay(dDate, 
						(String)paramSet.getHoildaysRela().get("dealdaynum"), paramSet.getDealDayNum()*-1, 
						(String)paramSet.getHoildaysRela().get("dealdaynum2"), paramSet.getDealDayNum2()*-1);
		}else {
			sDate = paramSetAdmin.getWorkDay(dDate, 
						(String)paramSet.getHoildaysRela().get("lastestdealdaynum"), paramSet.getLastestDealDayNum()*-1, 
						(String)paramSet.getHoildaysRela().get("lastestdealdaynum2"), paramSet.getLastestDealDayNum2()*-1);
		}
        mDate = YssFun.toDate(sDate);
		return mDate;
	}
	
	/**初始化ETF参数设置对象
	 * @param portCodes 组合
	 * @author shashijie ,2011-8-19 , STORY 1434
	 */
	private void initDate(String portCodes) throws YssException {
		paramSetAdmin = new ETFParamSetAdmin();//实例化参数设置的操作类
		paramSetAdmin.setYssPub(pub);//设置pub
		etfParam = paramSetAdmin.getETFParamInfo(portCodes); // 根据已选组合代码用于获取相关ETF参数数据
		paramSet = (ETFParamSetBean) etfParam.get(portCodes);//根据组合代码获取参数设置的实体bean
		/**shashijie 2012-12-12 STORY 3328 注释 STORY 3416的代码*/
		//----add by zhaoxianlin 20121019 #STORY 3146------ 
		//当ETF台账基本参数设置中基础汇率和组合汇率来源不为空时，采用台账中设置的汇率来源及汇率字段
		/*if(paramSet.getsBaseRateSrcCode()!=null&&paramSet.getsBaseRateSrcCode().length()!=0){
			this.paramExRate=true;
		}*/
		//-----end ------------------------------
		/**end*/
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
