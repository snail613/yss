package com.yss.main.etfoperation.etfaccbook.dealboshi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.dealmakeup.CtlDealMakeUp;
import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.etfaccbook.PretValMktPriceAndExRate;
import com.yss.main.etfoperation.etfaccbook.SearchMarketPrice;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFStockListBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.etfoperation.pojo.ValMktPriceAndRateBean;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.opermanage.etf.ApartETFSupplyAnaDoInvest;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CreateRefDataOfMakeUpMust extends CtlETFAccBook  {
	private String portCodes = ""; // 已选组合代码
	private java.util.Date tradeDate = null; // 估值日期
	
	private HashMap hmTradeSettleDelSum = null; // 储存需要插入到交易结算明细表中的实体类
	
	private HashMap etfParam = null; // 用于储存组合代码对应的参数
	
	private HashMap hmStockList = new HashMap(); // 用于储存符合条件的股票篮实体类
	
	private HashMap hmMakeUpInfo = null; // 储存需要补票的数据
	
	private ArrayList alNum = new ArrayList();
	// key - (securityCode + buyDate.toString()) value -
	// 相关证券代码，申赎日期对应的交易结算明细汇总数据
	private HashMap hmSumMakeUpInfos = new HashMap();

	// key - (stockHolderCode + buyDate.toString()) value -
	// 相关证券代码，申赎日期对应的交易结算明细汇总数据
	private HashMap hmSHSumMakeUpInfos = new HashMap();

	// 用于储存key - (证券代码) value - TradeSubBean的ArrayList
	// 用于储存相关证券代码对应的储存多条交易数据的ArrayList
	private HashMap hmTradeInfo = new HashMap();
	
	private HashMap hmETFTradeInfo = new HashMap();

	// 用于储存估值当日相关申请编号对应的送股的汇总数据 key - FNum
	// value - 储存申请编号对应的汇总的总数量和实际数量的交易计算明细关联实体类
	private HashMap hmCurrentSGInfo = new HashMap();

	private HashMap hmMaxNum = new HashMap();// 用于储存相关证券，相关申赎日期的最大申请编号

	private HashMap hmMaxRefNum = new HashMap();// 储存相关申请编号的最大的关联编号
	
	private ArrayList alBuyDate = new ArrayList();//用于储存需要补票的申赎日期
	
	HashMap hmTotalAmount = new HashMap();//用于储存需要补票的相关证券代码的总的补票数量
	
	/**
	 * 构造函数
	 */
	public CreateRefDataOfMakeUpMust() {
		
	}
	
	public void doInsert(java.util.Date tradeDate, String portCodes) throws YssException {
		PretValMktPriceAndExRate valMktPriceRate = null;
		String makeUpsecurityCodes = "";
		HashMap hmHoliday = null;
		HashMap hmbasket = null;
		CtlDealMakeUp dealMakeUp = null;
		HashMap hmTransfer = new HashMap();
		HashMap hmSumMakeUpAmount = null;
		try{
			this.tradeDate = tradeDate;
			this.portCodes = portCodes;
			
			valMktPriceRate = new PretValMktPriceAndExRate();
			valMktPriceRate.setYssPub(pub);
			
			dealMakeUp = new CtlDealMakeUp();
			dealMakeUp.setYssPub(pub);
			
			valMktPriceRate.getValMktPriceAndExRateBy(portCodes, tradeDate);
			
			insertIntoTradeSettleDetail(); // 将ETF申购赎回数据储存到交易结算明细表中
			
			makeUpsecurityCodes = getMakeUpInfo();// 获取需要补票的明细数据
			
			hmHoliday = getSecurityInfo(makeUpsecurityCodes);
			
			getTradeInfoOfMakeUp();// 获取需要补票的交易数据
			
			getMaxNumInfo();// 获取相关证券，相关申赎日期的最大申请编号
			
			getMaxRefNum();// 获取需要补票的最大的关联编号
			
			// 获取估值当天组合代码对应的一级市场代码区分买卖标志的篮子数
			hmbasket = getTotalETFTradeAmount();
			
			dealMakeUp.setHmHoliday(hmHoliday);
			dealMakeUp.setHmbasket(hmbasket);
			dealMakeUp.setEtfParam(etfParam);
			dealMakeUp.setHmCurrentSGInfo(hmCurrentSGInfo);
			dealMakeUp.setHmTradeInfo(hmTradeInfo);
			dealMakeUp.setHmSumMakeUpInfos(hmSumMakeUpInfos);
			dealMakeUp.setHmMaxNum(hmMaxNum);
			dealMakeUp.setHmMakeUpInfo(hmMakeUpInfo);
			dealMakeUp.setHmMaxRefNum(hmMaxRefNum);
			dealMakeUp.setTradeDate(tradeDate);
			dealMakeUp.setAlNum(alNum);
			dealMakeUp.setHmSHSumMakeUpInfos(hmSHSumMakeUpInfos);
			dealMakeUp.setHmStockList(hmStockList);
			dealMakeUp.setHmETFTradeInfo(hmETFTradeInfo);
			dealMakeUp.setHmTotalAmount(hmTotalAmount);

			hmTransfer = dealMakeUp.dealMakeUpInfo(2,hmSumMakeUpAmount);
			
			hmTradeInfo = (HashMap) hmTransfer.get("hmTradeInfo");
			hmMaxRefNum = (HashMap) hmTransfer.get("hmMaxRefNum");
			hmMakeUpInfo = (HashMap) hmTransfer.get("hmMakeUpInfo");
			
			mustGetStock(hmHoliday);// 做强制处理
			
			insertIntoTradeSettleDetailRef();// 将权益和补票数据插入到交易结算明细关联表中
						
			updateSettleMark();// 根据ETF参数设置更新剩余数量为零 数据的清算标志
			
			InsertIntoETFSubTrade();//插入Tb_XXX_ETF_SubTrade表的操作 注意此步骤应在insertIntoTradeSettleDetailRef（）方法后
		}
		catch(Exception e){
			throw new YssException("生成ETF台账出错！", e);
		}
	}
	
	/**
	 * 将ETF申购赎回数据储存到交易结算明细表中
	 * 
	 * @throws YssException
	 */
	private void insertIntoTradeSettleDetail() throws YssException {
		String etfSeatCode = ""; // ETF席位
		String basketCodes = ""; // 可以补票的股票篮数据
		String basketCode = ""; // 可以补票的股票篮数据
		String[] strBasketCodes = null;
		String basketCodes1 = ""; // 可以补票的替代标志为1的股票篮数据
		String basketCodes3 = ""; // 可以补票的替代标志为3的股票篮数据
		String levelOneMarketCode = ""; // 组合代码对应的一级市场代码
		String levelTwoMarketCode = ""; // 组合代码对应的二级市场代码
		double basicRate = 0; // 组合代码对应的基准比例

		String[] portCodeStr = null; // 用于储存拆分后的组合代码
		String strSql = "";
		PreparedStatement pstStm = null;
		String sNewNum = "";
		ETFParamSetAdmin paramSetAdmin = null;
		ETFParamSetBean paramSet = null;

		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ArrayList alSec = new ArrayList();
		String[] securityCodes = null;
		hmTradeSettleDelSum = new HashMap();
		ArrayList alExistSecCodes = null;
		ArrayList alStockHolderCodes = null;
		HashMap hmSecStock = null;
		String securityCode = null;
		String stockHolderCode = null;
		String key = "";
		String bookTotalType = "";// 汇总方式
		try {
			paramSetAdmin = new ETFParamSetAdmin();
			paramSetAdmin.setYssPub(pub);

			SearchMarketPrice searchMarketPrice = new SearchMarketPrice();
			searchMarketPrice.setYssPub(pub);

			etfParam = paramSetAdmin.getETFParamInfo(portCodes); // 根据已选组合代码用于获取相关ETF参数数据

			portCodeStr = portCodes.split(",");// 拆分已选组合代码

			basketCodes = getStockListInfo(); // 在股票篮表中查询替代标识等于1（允许补票）和3（全额补票）的股票数据

			if (basketCodes != null && !basketCodes.equals("")) {
				if (basketCodes.indexOf("\t") != -1) {
					strBasketCodes = basketCodes.split("\t");
					if (strBasketCodes.length >= 2) {
						basketCode = strBasketCodes[0];
						basketCodes1 = strBasketCodes[1];
					}
					if (strBasketCodes.length >= 3) {
						basketCodes3 = strBasketCodes[2];
					}
				}

				securityCodes = basketCode.split(",");

				for (int i = 0; i < securityCodes.length; i++) {
					alSec.add(securityCodes[i]);
				}

				// 循环已选组合代码
				for (int i = 0; i < portCodeStr.length; i++) {
					if (etfParam != null) {
						paramSet = (ETFParamSetBean) etfParam.get(portCodeStr[i]);
						
						levelOneMarketCode = paramSet.getOneGradeMktCode();
						levelTwoMarketCode = paramSet.getTwoGradeMktCode();
						basicRate = paramSet.getNormScale();
						etfSeatCode = paramSet.getETFSeat();
					}

					// 获取要插入到交易结算明细表的需要补票的数据
					hmSecStock = getMakeUpStockInfo(portCodeStr[i], basketCodes1, basketCodes3, etfSeatCode, levelOneMarketCode, levelTwoMarketCode, basicRate);
					alExistSecCodes = (ArrayList) hmSecStock.get("security");
					alStockHolderCodes = (ArrayList) hmSecStock.get("stockholder");
				}

				conn.setAutoCommit(false);
				bTrans = true;

				if (hmTradeSettleDelSum != null && !hmTradeSettleDelSum.isEmpty()) {
					// 根据已选组合代码和估值日期删除交易结算明细表
					strSql = " delete from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl") + " where FPortCode in(" + operSql.sqlCodes(portCodes)
							+ ") and FBuyDate = " + dbl.sqlDate(this.tradeDate);

					dbl.executeSql(strSql);

					sNewNum = "T"
							+ YssFun.formatDate(this.tradeDate, "yyyyMMdd")
							+ dbFun.getNextInnerCode(pub.yssGetTableName("Tb_ETF_TradeStlDtl"), dbl.sqlRight("FNUM", 6), "000000", " where FBuyDate="
									+ dbl.sqlDate(this.tradeDate) + " and FNum like 'T" + YssFun.formatDate(this.tradeDate, "yyyyMMdd") + "%'");

					strSql = " insert into " + pub.yssGetTableName("Tb_ETF_TradeStlDtl")
							+ "(FNum,FPortCode,FSecurityCode,FStockHolderCode,FBrokerCode,FSeatCode,FBs,FBuyDate,FReplaceAmount,"
							+ "FBraketNum,FUnitCost,FOReplaceCash,FHReplaceCash,FOCReplaceCash,FHCReplaceCash,Fcreator,FCreateTime,FExchangeRate)"
							+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

					pstStm = dbl.openPreparedStatement(strSql);

					// 循环已选组合代码
					for (int i = 0; i < portCodeStr.length; i++) {
						paramSet = (ETFParamSetBean) etfParam.get(portCodeStr[i]);
						bookTotalType = paramSet.getSBookTotalType();// 组合代码对应的汇总方式

						// 若按证券代码汇总
						if (bookTotalType.equals("stock")) {
							for (int j = 0; j < alExistSecCodes.size(); j++) {
								securityCode = (String) alExistSecCodes.get(j);

								key = securityCode + "B" + portCodeStr[i];
								sNewNum = subInsertIntoTradeSettleDetail(key, pstStm, sNewNum);

								key = securityCode + "S" + portCodeStr[i];
								sNewNum = subInsertIntoTradeSettleDetail(key, pstStm, sNewNum);
							}
						}

						// 若按股东代码汇总
						if (bookTotalType.equals("investor")) {
							for (int j = 0; j < alStockHolderCodes.size(); j++) {
								stockHolderCode = (String) alStockHolderCodes.get(j);

								key = stockHolderCode + "B" + portCodeStr[i];
								sNewNum = subInsertIntoTradeSettleDetail(key, pstStm, sNewNum);

								key = stockHolderCode + "S" + portCodeStr[i];
								sNewNum = subInsertIntoTradeSettleDetail(key, pstStm, sNewNum);
							}
						}
					}

					pstStm.executeBatch();

					conn.commit();
					bTrans = false;
					conn.setAutoCommit(true);
				}
			}

		} catch (Exception e) {
			throw new YssException("将ETF申购赎回数据储存到交易结算明细表中出错！", e);
		} finally {
			dbl.closeStatementFinal(pstStm);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * 在股票篮表中查询替代标识等于1（允许补票）和3（全额补票）的股票数据
	 * 
	 * @return String
	 * @throws YssException
	 */
	private String getStockListInfo() throws YssException {
		String strSql = null;
		ResultSet rs = null;
		String basketCodes1 = ""; // 符合条件的用逗号隔开的替代标志为1的证券代码
		String basketCodes3 = "";// 符合条件的用逗号隔开的替代标志为3的证券代码
		String basketCodes = "";// 符合条件的用逗号隔开的替代标志为1的证券代码
		ETFStockListBean stockList = null;
		String replaceMark = "";// 替代标志
		try {
			strSql = " select * from " + pub.yssGetTableName("Tb_ETF_StockList") + " where FReplaceMark in('1','3') and FDate = "
					+ dbl.sqlDate(tradeDate);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				replaceMark = rs.getString("FReplaceMark");// 替代标志

				stockList = new ETFStockListBean();
				stockList.setAmount(rs.getDouble("FAmount")); // 证券数量
				stockList.setDesc(rs.getString("FDesc")); // 描述
				stockList.setPortCode(rs.getString("FPortCode")); // 组合代码
				stockList.setPremiumScale(rs.getDouble("FPremiumScale")); // 溢价比例
				stockList.setReplaceMark(replaceMark); // 替代标识
				stockList.setSecurityCode(rs.getString("FSecurityCode")); // 证券代码
				stockList.setTotalMoney(rs.getDouble("FTotalMoney")); // 总金额

				hmStockList.put(rs.getString("FSecurityCode") + " " + rs.getString("FPortCode"), stockList);

				basketCodes += rs.getString("FSecurityCode") + ",";

				if (replaceMark.equals("1")) {
					basketCodes1 += rs.getString("FSecurityCode") + ",";
				}

				if (replaceMark.equals("3")) {
					basketCodes3 += rs.getString("FSecurityCode") + ",";
				}
			}

			if (basketCodes.length() >= 1) {
				basketCodes = basketCodes.substring(0, basketCodes.length() - 1);
			}

			if (basketCodes1.length() >= 1) {
				basketCodes1 = basketCodes1.substring(0, basketCodes1.length() - 1);
			}

			if (basketCodes3.length() >= 1) {
				basketCodes3 = basketCodes3.substring(0, basketCodes3.length() - 1);
			}

			if (basketCodes.equals("") && basketCodes1.equals("") && basketCodes3.equals("")) {
				return "";
			}

			return basketCodes + "\t" + basketCodes1 + "\t" + basketCodes3;
		} catch (Exception e) {
			throw new YssException("查询股票篮数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 获取要插入到交易结算明细表的需要补票的数据
	 * 
	 * @throws YssException
	 */
	private HashMap getMakeUpStockInfo(String portCode, String basketCodes1, String basketCodes3, 
			String etfSeatCode, String levelOneMarketCode, String levelTwoMarketCode,
			double basicRate) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rsval = null;
		String seatCode = ""; // 席位代码
		String securityCode = ""; // 证券代码
		double relaNum = 0; // 编号
		double basketAmount = 0; // 篮子数
		java.util.Date bargainDate = null; // 申赎日期
		String brokerCode = ""; // 券商代码
		String bs = ""; // 买卖标志
		double amount = 0; // 股票栏中需要补票的证券数量
		double totalMoney = 0; // 股票栏中的替代金额
		double premiumScale = 0; // 溢价比例
		double tradeAmount = 0; // ETF过户表或中登交易结算明细表中的成交数量
		String stockHolderCode = ""; // 股东代码

		String tradeTypeCode = "";// 业务类型

		double hcReplaceCash = 0; // 可退替代款(本币)
		double ocReplaceCash = 0; // 可退替代款(原币)
		double hReplaceCash = 0; // 替代金额(本币)
		double oReplaceCash = 0; // 替代金额(原币)

		double replaceAmount = 0; // 替代数量
		double replacePrice = 0; // 替代金额
		double unitcost = 0; // 单位成本
		double appNum = 0; // ETF过户表中的申请编号

		// 储存在股票篮中查询到的需要补票的，但是在ETF过户表中根据业务日期等于估值日期的条件查询不到的证券代码
		ArrayList alExistSecCodes = new ArrayList();
		ArrayList alstockHolderCodes = new ArrayList();

		ETFStockListBean stockList = null;
		ETFTradeSettleDetailBean etfTradeSettleDetail = null;

		HashMap hmYJDM = null;
		String[] securityCodes = null;
		double exchangeRate = 0;// 汇率
		ETFParamSetBean paramSet = null;
		String bookTotalType = "";// 汇总方式
		HashMap hmSecStock = new HashMap();
		HashMap hmVal = new HashMap();
		ValMktPriceAndRateBean valMktPriceRate = null;
		int unitDigit = 0;//单位成本保留为数
		double price = 0;
		try {
			hmYJDM = getETFInfoOfLevelOne(portCode, levelOneMarketCode);
			exchangeRate = 1;// 汇率

			paramSet = (ETFParamSetBean) etfParam.get(portCode);// 根据组合代码获取ETF参数设置

			if (paramSet != null) {
				bookTotalType = paramSet.getSBookTotalType();// 汇总方式
				unitDigit = paramSet.getSUnitdigit();//单位成本保留为数
			}
			
			strSql = " delete from " + pub.yssGetTableName("tb_etf_tradstldtlref") 
			+ " where FMakeUpDate = " + dbl.sqlDate(tradeDate) 
			+ " or FExRightDate = " + dbl.sqlDate(tradeDate);
			
			dbl.executeSql(strSql);
			
			// 在过户表中查询出申购赎回日期等于估值日期，组合代码为已选组合代码，证券代码为股票栏中能够现金替代的证券代码
			// 且申请编号不等于'ETFJIJIN'的数据，
			// 左关联中登结算明细表中席位代码不等于ETF参数设置中设置的ETF席位号 关联条件为过户表的成交编号等于中登结算明细表中的成交编号
			strSql = " select distinct mx.*, val.FPrice, val.FBaseRate, val.FPortRate from (select distinct a.*,b.FClearCode, "
					+ " b.FSeatNum as FSeatCode,b.FStockHolderCode as FFStockHolderCode from (select * from "
					+ pub.yssGetTableName("Tb_ETF_GHInterface") + " where FBargainDate = " + dbl.sqlDate(tradeDate)
					+ " and FPortCode = " + dbl.sqlString(portCode) + " and FSecurityCode in(" + operSql.sqlCodes(basketCodes1)
					+ " ) and FAppNum <> 'ETFJIJIN' order by FTradeNum,FRelaNum) a left join (select * from "
					+ pub.yssGetTableName("Tb_ETF_JSMXInterface") + " where FPortCode = " + dbl.sqlString(portCode)
					+ " and FSeatNum <> " + dbl.sqlString(etfSeatCode) + " and FSettleDate = " + dbl.sqlDate(tradeDate)
					+ ") b on a.FTradeNum = b.FTradeNum order by a.FTradeNum ) mx "
					+ " left join (select valsec.*, valrate.fbaserate, valrate.Fportrate from (select valmkt.*, sec.ftradecury as FTradeCury "
					+ " from (select * from " + pub.yssGetTableName("Tb_Data_Pretvalmktprice") 
					+ " where FPortCode = " + dbl.sqlString(portCode) + " and FValDate = " + dbl.sqlDate(tradeDate)
					+ " )valmkt left join " + pub.yssGetTableName("Tb_Para_Security")
					+ " sec on valmkt.fsecuritycode = sec.fsecuritycode) valsec left join "
					+ " (select * from " + pub.yssGetTableName("Tb_Data_Pretvalrate")
					+ " where FPortCode = " + dbl.sqlString(portCode) + " and FValDate = " + dbl.sqlDate(tradeDate)
					+ " )valrate on valsec.FTradeCury = valrate.Fcurycode) val on mx.FSecurityCode = val.FSecurityCode ";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				seatCode = rs.getString("FSeatCode");

				if (seatCode == null) {
					continue;
				}

				securityCode = rs.getString("FSecurityCode"); // 证券代码
				tradeAmount = rs.getDouble("FTradeAmount"); // 已提交的证券数量
				appNum = Double.parseDouble(rs.getString("FAppNum")); // 申请编号
				bargainDate = rs.getDate("FBargainDate"); // 申赎日期
				brokerCode = rs.getString("FClearCode"); // 券商代码
				bs = rs.getString("FMark"); // 买卖标志
				relaNum = rs.getDouble("FRelaNum"); // 编号
				stockHolderCode = rs.getString("FFStockholderCode"); // 股东代码

				// 若按证券代码汇总
				if (bookTotalType.equals("stock")) {
					if (!alExistSecCodes.contains(securityCode)) {
						alExistSecCodes.add(securityCode);
					}
				}
				
				// 若按股东代码汇总
				if (bookTotalType.equals("investor")) {
					if (!alstockHolderCodes.contains(stockHolderCode)) {
						alstockHolderCodes.add(stockHolderCode);
					}
				}

				basketAmount = YssD.div(Double.parseDouble((String) hmYJDM.get(String.valueOf(relaNum))), basicRate); // 篮子数

				basketAmount = Math.abs(basketAmount);// 对计算出的篮子数取绝对值

				stockList = (ETFStockListBean) hmStockList.get(securityCode + " " + portCode);

				if (stockList != null) {
					amount = stockList.getAmount();
					premiumScale = stockList.getPremiumScale(); // 溢价比例
					totalMoney = stockList.getTotalMoney(); // 股票篮中的替代金额
				}

				replaceAmount = YssD.mul(basketAmount, amount) - tradeAmount; // 替代数量
				replacePrice = YssD.div(appNum, 1000); // 替代价格

				unitcost = YssFun.roundIt(rs.getDouble("FPrice"), unitDigit);// 收盘价等于单位成本
				
				price = rs.getDouble("FPrice");
				
				exchangeRate = YssD.div(rs.getDouble("FBaseRate"), rs.getDouble("FPortRate"));

				if (bs.equals("B")) {
					// 若交易类型=B,则替代股数*替代价格*（1+溢价比例）
					oReplaceCash = YssD.mul(replaceAmount, replacePrice, YssD.add(1, premiumScale)); // 替代金额(原币)
				}
				if (bs.equals("S")) {
					// 若交易类型=S,则替代股数*替代价格*（1-溢价比例）
					oReplaceCash = YssD.mul(replaceAmount, replacePrice, YssD.sub(1, premiumScale)); // 替代金额(原币)
				}
				
				hReplaceCash = oReplaceCash;

				if (bs.equals("B")) {
					// 可退替代款（本币） = round(替代金额（本币）,2) - round(估值行情 * 替代数量 * 估值汇率,2)
					hcReplaceCash = YssD.round(hReplaceCash, 2)
							- YssD.round(YssD.mul(price, replaceAmount, exchangeRate),2);
				}
				if (bs.equals("S")) {
					// 可退替代款（本币） = round(估值行情 * 替代数量 * 估值汇率,2)
					hcReplaceCash = YssD.round(YssD
							.mul(price, replaceAmount, exchangeRate),2);
				}

				ocReplaceCash = hcReplaceCash;
				
				etfTradeSettleDetail = new ETFTradeSettleDetailBean();

				etfTradeSettleDetail.setBraketNum(basketAmount); // 篮子数
				etfTradeSettleDetail.setBrokerCode(brokerCode); // 券商代码
				etfTradeSettleDetail.setBs(bs); // 买卖标志
				etfTradeSettleDetail.setBuyDate(bargainDate); // 申赎日期
				etfTradeSettleDetail.setHcReplaceCash(YssFun.roundIt(hcReplaceCash, 2)); // 可退替代款（本币）
				etfTradeSettleDetail.setHReplaceCash(YssFun.roundIt(hReplaceCash, 2)); // 替代金额（本币）
				etfTradeSettleDetail.setOcReplaceCash(YssFun.roundIt(ocReplaceCash, 2)); // 可退替代款（原币）
				etfTradeSettleDetail.setOReplaceCash(YssFun.roundIt(oReplaceCash, 2)); // 替代金额（原币）
				etfTradeSettleDetail.setReplaceAmount(replaceAmount); // 替代数量
				etfTradeSettleDetail.setPortCode(portCode); // 组合代码
				etfTradeSettleDetail.setSeatCode(seatCode); // 席位代码
				etfTradeSettleDetail.setSecurityCode(securityCode); // 证券代码
				etfTradeSettleDetail.setStockHolderCode(stockHolderCode); // 股东代码
				etfTradeSettleDetail.setUnitCost(unitcost); // 单位成本
				etfTradeSettleDetail.setExchangeRate(exchangeRate);

				// 若按股票汇总
				if (bookTotalType.equals("stock")) {
					setETFTradeSettleDetailBean(etfTradeSettleDetail);// 储存汇总和明细的交易结算明细数据到哈希表中
				}

				// 若按股东汇总
				if (bookTotalType.equals("investor")) {
					setStockHolderETFTradeSetDetBean(etfTradeSettleDetail);// 储存汇总和明细的交易结算明细数据到哈希表中
				}
			}

			// 若股票栏中允许现金替代的证券在过户库中没有数据，则在中登结算明细表中查询相关组合代码对应的一级市场代码的数据，
			// 查询出相应的股东代码和成交数量，根据查询出的一级市场代码的成交数量计算出股票篮的篮子数
			if (!basketCodes3.equals("")) {
				strSql = " select valsec.*, valrate.fbaserate as FBaseRate, valrate.Fportrate as FPortRate from (select valmkt.*, sec.ftradecury as FTradeCury "
						+ " from (select * from " + pub.yssGetTableName("Tb_Data_Pretvalmktprice") 
						+ " where FPortCode = " + dbl.sqlString(portCode) 
						+ " and FValDate = " + dbl.sqlDate(tradeDate) + " )valmkt left join "
						+ pub.yssGetTableName("Tb_Para_Security")
						+ " sec on valmkt.fsecuritycode = sec.fsecuritycode) valsec left join  "
						+ " (select * from " + pub.yssGetTableName("Tb_Data_Pretvalrate")
						+ " where FValDate = " + dbl.sqlDate(tradeDate) + " and FPortCode = " + dbl.sqlString(this.portCodes)
						+ " )valrate on valsec.FTradeCury = valrate.Fcurycode " 
						+ "where valsec.Fsecuritycode in (" + operSql.sqlCodes(basketCodes3) + ") ";

				rsval = dbl.openResultSet(strSql);
				while (rsval.next()) {
					securityCode = rsval.getString("FSecurityCode");

					valMktPriceRate = new ValMktPriceAndRateBean();

					valMktPriceRate.setSecurityCode(rsval.getString("FSecurityCode"));
					valMktPriceRate.setPrice(rsval.getDouble("FPrice"));
					valMktPriceRate.setBaseRate(rsval.getDouble("FBaseRate"));
					valMktPriceRate.setPortRate(rsval.getDouble("FPortRate"));

					hmVal.put(securityCode, valMktPriceRate);
				}

				strSql = " select * from " + pub.yssGetTableName("Tb_ETF_JSMXInterface") + " where FSecurityCode1 = "
						+ dbl.sqlString(levelTwoMarketCode) + " and FSettleDate = " + dbl.sqlDate(tradeDate) + " and FSeatNum <> "
						+ dbl.sqlString(etfSeatCode) + " and FTotalMoney = 0 and FPortCode = " + dbl.sqlString(portCode) + " order by FTradeNum ";
				rs1 = dbl.openResultSet(strSql);
				while (rs1.next()) {
					stockHolderCode = rs1.getString("FStockholderCode"); // 股东代码
					tradeTypeCode = rs1.getString("FTradeTypeCode");// 业务类型
					tradeAmount = rs1.getDouble("FTradeAmount");// 成交数量
					bargainDate = rs1.getDate("FClearDate"); // 申赎日期为中登结算明细表中的清算日期
					seatCode = rs1.getString("FSeatNum"); // 席位代码
					brokerCode = rs1.getString("FClearCode"); // 券商代码
					
					// 业务类型102-ETF 申购 ,则对应的股票的买卖标志为申购
					if (tradeTypeCode.equals("102")) {
						bs = "B";
					}
					// 业务类型103-ETF 赎回，则对应的股票的买卖标志为赎回
					if (tradeTypeCode.equals("103")) {
						bs = "S";
					}

					basketAmount = Math.abs(YssD.div(tradeAmount, basicRate));// 对计算出的篮子数取绝对值

					// 若按股东代码汇总
					if (bookTotalType.equals("investor")) {
						if (!alstockHolderCodes.contains(stockHolderCode)) {
							alstockHolderCodes.add(stockHolderCode);
						}
					}

					securityCodes = basketCodes3.split(",");

					for (int i = 0; i < securityCodes.length; i++) {
						securityCode = securityCodes[i];

						// 若按证券代码汇总
						if (bookTotalType.equals("stock")) {
							if (!alExistSecCodes.contains(securityCode)) {
								alExistSecCodes.add(securityCode);
							}
						}

						stockList = (ETFStockListBean) hmStockList.get(securityCode + " " + portCode);// 获取股票篮中相关证券的数据

						if (stockList != null) {
							totalMoney = stockList.getTotalMoney(); // 股票篮中的替代金额
							premiumScale = stockList.getPremiumScale(); // 溢价比例
							amount = stockList.getAmount();// 股票栏中的替代数量
						}

						replaceAmount = YssD.mul(amount, basketAmount);// 替代数量 = 股票栏中的替代数量 × 篮子数

						valMktPriceRate = (ValMktPriceAndRateBean) hmVal.get(securityCode);
						if (valMktPriceRate != null) {
							unitcost = YssFun.roundIt(valMktPriceRate.getPrice(), unitDigit);// 收盘价等于单位成本
							exchangeRate = YssD.div(valMktPriceRate.getBaseRate(), valMktPriceRate.getPortRate());
							price = valMktPriceRate.getPrice();
						}

						// 若为申购
						if (bs.equals("B")) {
							// 若交易类型=B,则替代金额*篮子数*（1+溢价比例）
							hReplaceCash = YssD.mul(totalMoney, basketAmount, YssD.add(1, premiumScale)); // 替代金额(本币)
						}
						else{
							// 若交易类型=S,则 股票篮的股数 × 最近的收盘价 × 篮子数 × 估值汇率
							hReplaceCash = YssD.mul(amount, price, basketAmount, exchangeRate);
						}
						
						oReplaceCash = hReplaceCash;

						if (bs.equals("B")) {
							// 可退替代款（本币） = 替代金额（本币） - 估值行情 * 替代数量 * 估值汇率
							hcReplaceCash = hReplaceCash
									- YssD.mul(price, replaceAmount, exchangeRate);
						}
						if (bs.equals("S")) {
							// 可退替代款（本币） = 估值行情 * 替代数量 * 估值汇率
							hcReplaceCash = YssD
									.mul(price, replaceAmount, exchangeRate);
						}

						ocReplaceCash = hcReplaceCash;
						
						etfTradeSettleDetail = new ETFTradeSettleDetailBean();

						etfTradeSettleDetail.setBraketNum(basketAmount); // 篮子数
						etfTradeSettleDetail.setBrokerCode(brokerCode); // 券商代码
						etfTradeSettleDetail.setBs(bs); // 买卖标志
						etfTradeSettleDetail.setBuyDate(bargainDate); // 申赎日期
						etfTradeSettleDetail.setHcReplaceCash(YssFun.roundIt(hcReplaceCash, 2)); // 可退替代款（本币）
						etfTradeSettleDetail.setHReplaceCash(YssFun.roundIt(hReplaceCash, 2)); // 替代金额（本币）
						etfTradeSettleDetail.setOcReplaceCash(YssFun.roundIt(ocReplaceCash, 2)); // 可退替代款（原币）
						etfTradeSettleDetail.setOReplaceCash(YssFun.roundIt(oReplaceCash, 2)); // 替代金额（原币）
						etfTradeSettleDetail.setReplaceAmount(replaceAmount); // 替代数量
						etfTradeSettleDetail.setPortCode(portCode); // 组合代码
						etfTradeSettleDetail.setSeatCode(seatCode); // 席位代码
						etfTradeSettleDetail.setSecurityCode(securityCode); // 证券代码
						etfTradeSettleDetail.setStockHolderCode(stockHolderCode); // 股东代码
						etfTradeSettleDetail.setUnitCost(unitcost); // 单位成本
						etfTradeSettleDetail.setExchangeRate(exchangeRate);

						// 若按股票汇总
						if (bookTotalType.equals("stock")) {
							setETFTradeSettleDetailBean(etfTradeSettleDetail);// 储存汇总和明细的交易结算明细数据到哈希表中
						}

						// 若按股东汇总
						if (bookTotalType.equals("investor")) {
							setStockHolderETFTradeSetDetBean(etfTradeSettleDetail);// 储存汇总和明细的交易结算明细数据到哈希表中
						}
					}
				}
			}
			hmSecStock.put("security", alExistSecCodes);
			hmSecStock.put("stockholder", alstockHolderCodes);
			return hmSecStock;
		} catch (Exception e) {
			throw new YssException("获取要插入到交易结算明细表的需要补票的数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs, rs1, rsval);
		}
	}
	
	/**
	 * 给插入到交易结算明细表的数据赋值 并按顺序拼接申请编号
	 * 
	 * @param key
	 * @param pstStm
	 * @param sNewNum
	 * @return
	 * @throws YssException
	 */
	private String subInsertIntoTradeSettleDetail(String key, PreparedStatement pstStm, String sNewNum) throws YssException {
		ETFTradeSettleDetailBean tradeSettleDel = null;
		ArrayList alTradeSettleDel = null;
		String sSubNum = "";
		Object o = null;
		try {
			o = hmTradeSettleDelSum.get(key);

			if (o != null) {
				tradeSettleDel = (ETFTradeSettleDetailBean) o;
				sSubNum = sNewNum.substring(0, sNewNum.length() - 6) + YssFun.formatNumber(Integer.parseInt(YssFun.right(sNewNum, 6)) + 1, "000000");
				sNewNum = sSubNum;

				pstStm.setString(1, sSubNum);
				pstStm.setString(2, tradeSettleDel.getPortCode());
				pstStm.setString(3, tradeSettleDel.getSecurityCode());
				pstStm.setString(4, tradeSettleDel.getStockHolderCode());
				pstStm.setString(5, tradeSettleDel.getBrokerCode());
				pstStm.setString(6, tradeSettleDel.getSeatCode());
				pstStm.setString(7, tradeSettleDel.getBs());
				pstStm.setDate(8, YssFun.toSqlDate(tradeSettleDel.getBuyDate()));
				pstStm.setDouble(9, tradeSettleDel.getReplaceAmount());
				pstStm.setDouble(10, tradeSettleDel.getBraketNum());
				pstStm.setDouble(11, tradeSettleDel.getUnitCost());
				pstStm.setDouble(12, tradeSettleDel.getOReplaceCash());
				pstStm.setDouble(13, tradeSettleDel.getHReplaceCash());
				pstStm.setDouble(14, tradeSettleDel.getOcReplaceCash());
				pstStm.setDouble(15, tradeSettleDel.getHcReplaceCash());
				pstStm.setString(16, pub.getUserCode());
				pstStm.setString(17, YssFun.formatDatetime(new java.util.Date()));
				pstStm.setDouble(18, tradeSettleDel.getExchangeRate());

				pstStm.addBatch();

				alTradeSettleDel = tradeSettleDel.getAlTradeSettleDel();
				for (int k = 0; k < alTradeSettleDel.size(); k++) {
					tradeSettleDel = (ETFTradeSettleDetailBean) alTradeSettleDel.get(k);

					sSubNum = sNewNum.substring(0, sNewNum.length() - 6)
							+ YssFun.formatNumber(Integer.parseInt(YssFun.right(sNewNum, 6)) + 1, "000000");
					sNewNum = sSubNum;

					pstStm.setString(1, sSubNum);
					pstStm.setString(2, tradeSettleDel.getPortCode());
					pstStm.setString(3, tradeSettleDel.getSecurityCode());
					pstStm.setString(4, tradeSettleDel.getStockHolderCode());
					pstStm.setString(5, tradeSettleDel.getBrokerCode());
					pstStm.setString(6, tradeSettleDel.getSeatCode());
					pstStm.setString(7, tradeSettleDel.getBs());
					pstStm.setDate(8, YssFun.toSqlDate(tradeSettleDel.getBuyDate()));
					pstStm.setDouble(9, tradeSettleDel.getReplaceAmount());
					pstStm.setDouble(10, tradeSettleDel.getBraketNum());
					pstStm.setDouble(11, tradeSettleDel.getUnitCost());
					pstStm.setDouble(12, tradeSettleDel.getOReplaceCash());
					pstStm.setDouble(13, tradeSettleDel.getHReplaceCash());
					pstStm.setDouble(14, tradeSettleDel.getOcReplaceCash());
					pstStm.setDouble(15, tradeSettleDel.getHcReplaceCash());
					pstStm.setString(16, pub.getUserCode());
					pstStm.setString(17, YssFun.formatDatetime(new java.util.Date()));
					pstStm.setDouble(18, tradeSettleDel.getExchangeRate());
					
					pstStm.addBatch();
				}
			}

			return sNewNum;
		} catch (Exception e) {
			throw new YssException("给插入到交易结算明细表的数据赋值 并按顺序拼接申请编号出错！", e);
		}
	}
	
	/**
	 * 在ETF过户库中查询证券代码为一级市场代码的基金的成交数量
	 * 
	 * @param levelOneMarketCode
	 *            String
	 * @return double
	 * @throws YssException
	 */
	private HashMap getETFInfoOfLevelOne(String portCode, String levelOneMarketCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		double tradeAmount = 0;
		double relaNum = 0;
		HashMap hmYJDM = new HashMap();
		try {
			strSql = " select FRelaNum,FTradeNum,FTradeAmount,FSecurityCode from " + pub.yssGetTableName("Tb_ETF_GHInterface")
					+ " where FSecurityCode = " + dbl.sqlString(levelOneMarketCode) + " and FBargainDate = " + dbl.sqlDate(tradeDate)
					+ " and FPortCode = " + dbl.sqlString(portCode);

			rs = dbl.openResultSet(strSql);

			while (rs.next()) {
				tradeAmount = rs.getDouble("FTradeAmount");
				relaNum = rs.getDouble("FRelaNum");

				hmYJDM.put(String.valueOf(relaNum), String.valueOf(tradeAmount));
			}

			return hmYJDM;
		} catch (Exception e) {
			throw new YssException("根据组合代码查询ETF参数设置数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 用于储存汇总和明细的交易结算明细数据到哈希表中
	 */
	private void setETFTradeSettleDetailBean(ETFTradeSettleDetailBean etfTradeSettleDetail) throws YssException {
		ETFTradeSettleDetailBean etfTradeSettleDetSum = null;
		ArrayList alTradeSettleDetSum = null;
		String securityCode = "";
		String bs = "";
		String portCode = "";
		try {
			securityCode = etfTradeSettleDetail.getSecurityCode();
			bs = etfTradeSettleDetail.getBs();
			portCode = etfTradeSettleDetail.getPortCode();
			if (hmTradeSettleDelSum.get(securityCode + bs + portCode) != null) {
				etfTradeSettleDetSum = (ETFTradeSettleDetailBean) hmTradeSettleDelSum.get(securityCode + bs + portCode);

				if (etfTradeSettleDetSum.getAlTradeSettleDel() != null) {
					alTradeSettleDetSum = etfTradeSettleDetSum.getAlTradeSettleDel();
					alTradeSettleDetSum.add(etfTradeSettleDetail);

					etfTradeSettleDetSum.setAlTradeSettleDel(alTradeSettleDetSum);
					etfTradeSettleDetSum.setHcReplaceCash(etfTradeSettleDetail.getHcReplaceCash() + etfTradeSettleDetSum.getHcReplaceCash()); // 可退替代款（本币）
					etfTradeSettleDetSum.setHReplaceCash(etfTradeSettleDetail.getHReplaceCash() + etfTradeSettleDetSum.getHReplaceCash()); // 替代金额（本币）
					etfTradeSettleDetSum.setOcReplaceCash(etfTradeSettleDetail.getOcReplaceCash() + etfTradeSettleDetSum.getOcReplaceCash()); // 可退替代款（原币）
					etfTradeSettleDetSum.setOReplaceCash(etfTradeSettleDetail.getOReplaceCash() + etfTradeSettleDetSum.getOReplaceCash()); // 替代金额（原币）
					etfTradeSettleDetSum.setReplaceAmount(etfTradeSettleDetail.getReplaceAmount() + etfTradeSettleDetSum.getReplaceAmount()); // 替代数量

				}
			} else {
				etfTradeSettleDetSum = new ETFTradeSettleDetailBean();

				etfTradeSettleDetSum.setBraketNum(etfTradeSettleDetail.getBraketNum()); // 篮子数
				etfTradeSettleDetSum.setBrokerCode(" "); // 券商代码
				etfTradeSettleDetSum.setBs(etfTradeSettleDetail.getBs()); // 买卖标志
				etfTradeSettleDetSum.setBuyDate(etfTradeSettleDetail.getBuyDate()); // 申赎日期
				etfTradeSettleDetSum.setHcReplaceCash(etfTradeSettleDetail.getHcReplaceCash()); // 可退替代款（本币）
				etfTradeSettleDetSum.setHReplaceCash(etfTradeSettleDetail.getHReplaceCash()); // 替代金额（本币）
				etfTradeSettleDetSum.setOcReplaceCash(etfTradeSettleDetail.getOcReplaceCash()); // 可退替代款（原币）
				etfTradeSettleDetSum.setOReplaceCash(etfTradeSettleDetail.getOReplaceCash()); // 替代金额（原币）
				etfTradeSettleDetSum.setReplaceAmount(etfTradeSettleDetail.getReplaceAmount()); // 替代数量
				etfTradeSettleDetSum.setPortCode(etfTradeSettleDetail.getPortCode()); // 组合代码
				etfTradeSettleDetSum.setSeatCode(" "); // 席位代码
				etfTradeSettleDetSum.setSecurityCode(etfTradeSettleDetail.getSecurityCode()); // 证券代码
				etfTradeSettleDetSum.setUnitCost(etfTradeSettleDetail.getUnitCost()); // 单位成本
				etfTradeSettleDetSum.setStockHolderCode(" "); // 股东代码

				alTradeSettleDetSum = new ArrayList();
				alTradeSettleDetSum.add(etfTradeSettleDetail);
				etfTradeSettleDetSum.setAlTradeSettleDel(alTradeSettleDetSum);
			}

			hmTradeSettleDelSum.put(securityCode + bs + portCode, etfTradeSettleDetSum);
		} catch (Exception e) {
			throw new YssException("储存汇总和明细的交易结算明细数据到哈希表中出错！", e);
		}
	}
	
	/**
	 * 用于储存汇总和明细的交易结算明细数据到哈希表中
	 */
	private void setStockHolderETFTradeSetDetBean(ETFTradeSettleDetailBean etfTradeSettleDetail) throws YssException {
		ETFTradeSettleDetailBean etfTradeSettleDetSum = null;
		ArrayList alTradeSettleDetSum = null;
		String stockHolderCode = "";
		String bs = "";
		String portCode = "";
		try {
			bs = etfTradeSettleDetail.getBs();
			portCode = etfTradeSettleDetail.getPortCode();
			stockHolderCode = etfTradeSettleDetail.getStockHolderCode();

			if (hmTradeSettleDelSum.get(stockHolderCode + bs + portCode) != null) {
				etfTradeSettleDetSum = (ETFTradeSettleDetailBean) hmTradeSettleDelSum.get(stockHolderCode + bs + portCode);

				if (etfTradeSettleDetSum.getAlTradeSettleDel() != null) {
					alTradeSettleDetSum = etfTradeSettleDetSum.getAlTradeSettleDel();
					alTradeSettleDetSum.add(etfTradeSettleDetail);
					etfTradeSettleDetSum.setAlTradeSettleDel(alTradeSettleDetSum);

					etfTradeSettleDetSum.setHcReplaceCash(etfTradeSettleDetail.getHcReplaceCash() + etfTradeSettleDetSum.getHcReplaceCash()); // 可退替代款（本币）
					etfTradeSettleDetSum.setHReplaceCash(etfTradeSettleDetail.getHReplaceCash() + etfTradeSettleDetSum.getHReplaceCash()); // 替代金额（本币）
					etfTradeSettleDetSum.setOcReplaceCash(etfTradeSettleDetail.getOcReplaceCash() + etfTradeSettleDetSum.getOcReplaceCash()); // 可退替代款（原币）
					etfTradeSettleDetSum.setOReplaceCash(etfTradeSettleDetail.getOReplaceCash() + etfTradeSettleDetSum.getOReplaceCash()); // 替代金额（原币）
					etfTradeSettleDetSum.setReplaceAmount(etfTradeSettleDetail.getReplaceAmount() + etfTradeSettleDetSum.getReplaceAmount()); // 替代数量
				}
			} else {
				etfTradeSettleDetSum = new ETFTradeSettleDetailBean();

				etfTradeSettleDetSum.setBraketNum(etfTradeSettleDetail.getBraketNum()); // 篮子数
				etfTradeSettleDetSum.setBrokerCode(etfTradeSettleDetail.getBrokerCode()); // 券商代码
				etfTradeSettleDetSum.setBs(etfTradeSettleDetail.getBs()); // 买卖标志
				etfTradeSettleDetSum.setBuyDate(etfTradeSettleDetail.getBuyDate()); // 申赎日期
				etfTradeSettleDetSum.setHcReplaceCash(etfTradeSettleDetail.getHcReplaceCash()); // 可退替代款（本币）
				etfTradeSettleDetSum.setHReplaceCash(etfTradeSettleDetail.getHReplaceCash()); // 替代金额（本币）
				etfTradeSettleDetSum.setOcReplaceCash(etfTradeSettleDetail.getOcReplaceCash()); // 可退替代款（原币）
				etfTradeSettleDetSum.setOReplaceCash(etfTradeSettleDetail.getOReplaceCash()); // 替代金额（原币）
				etfTradeSettleDetSum.setReplaceAmount(etfTradeSettleDetail.getReplaceAmount()); // 替代数量
				etfTradeSettleDetSum.setPortCode(etfTradeSettleDetail.getPortCode()); // 组合代码
				etfTradeSettleDetSum.setSeatCode(etfTradeSettleDetail.getSeatCode()); // 席位代码
				etfTradeSettleDetSum.setSecurityCode(" "); // 证券代码
				etfTradeSettleDetSum.setUnitCost(etfTradeSettleDetail.getUnitCost()); // 单位成本
				etfTradeSettleDetSum.setStockHolderCode(etfTradeSettleDetail.getStockHolderCode()); // 股东代码

				alTradeSettleDetSum = new ArrayList();
				alTradeSettleDetSum.add(etfTradeSettleDetail);
				etfTradeSettleDetSum.setAlTradeSettleDel(alTradeSettleDetSum);
			}

			hmTradeSettleDelSum.put(stockHolderCode + bs + portCode, etfTradeSettleDetSum);
		} catch (Exception e) {
			throw new YssException("储存汇总和明细的交易结算明细数据到哈希表中出错！", e);
		}
	}
	
	/**
	 * 获取需要补票的数据
	 * 
	 * @throws YssException
	 */
	private String getMakeUpInfo() throws YssException {
		String strSql = "";
		ResultSet rs = null; // 声明结果集
		ETFTradeSettleDetailBean tradeSettleDel = null;
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;
		String num = "";// 申请编号
		String securityCode = "";// 证券代码
		String bs = "";// 买卖标志
		java.util.Date buyDate = null;// 申赎日期
		String stockHolderCode = "";// 股东代码
		String portCode = "";// 组合代码
		HashMap hmRef = new HashMap();
		String refNum = "";
		double remaindAmount = 0;
		String securityCodes = "";
		ArrayList alSecs = new ArrayList();
		ArrayList alTradeSettleDel = null;
		double makeUpAmount = 0;//相关证券代码的需要补票的数量
		try {
			SearchMarketPrice searchMarketPrice = new SearchMarketPrice();
			searchMarketPrice.setYssPub(pub);

			hmMakeUpInfo = new HashMap();

			strSql = " select * from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FRemaindAmount <> 0 and FNum in(select distinct FNum from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl")
					+ " where FPortCode in (" + operSql.sqlCodes(this.portCodes) + "))";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				num = rs.getString("FNum");
				refNum = rs.getString("FRefNum");
				tradeSettleDelRef = new ETFTradeSettleDetailRefBean();

				tradeSettleDelRef.setNum(num);
				tradeSettleDelRef.setRefNum(refNum);
				tradeSettleDelRef.setOcRefundSum(rs.getDouble("FOCRefundSum"));// 应退合计（原币）
				tradeSettleDelRef.setHcRefundSum(rs.getDouble("FHCRefundSum"));// 应退合计（本币）
				tradeSettleDelRef.setOCanRepCash(rs.getDouble("FOCanRepCash"));// 可退替代款余额（原币）
				tradeSettleDelRef.setHCanRepCash(rs.getDouble("FHCanRepCash"));// 可退替代款余额（本币）
				tradeSettleDelRef.setSumAmount(rs.getDouble("FSumAmount"));// 总数量
				tradeSettleDelRef.setRealAmount(rs.getDouble("FRealAmount"));// 实际数量
				tradeSettleDelRef.setInterest(rs.getDouble("FInterest"));// 派息
				tradeSettleDelRef.setWarrantCost(rs.getDouble("FWarrantCost"));// 权证价值
				tradeSettleDelRef.setRemaindAmount(rs.getDouble("FRemaindAmount"));// 剩余数量
				tradeSettleDelRef.setRefundDate(rs.getDate("FRefundDate"));// 退款日期
				tradeSettleDelRef.setSettleMark(rs.getString("FSettleMark"));// 清算标志

				hmRef.put(num + String.valueOf(refNum), tradeSettleDelRef);
			}

			dbl.closeResultSetFinal(rs);
			rs = null;

			strSql = " select distinct makeup.*, val.FPrice, val.FMarketValueDate, val.FBaseRate, val.FPortRate from "
					+ "(select c.* from (select a.FNum,a.FPortcode,a.FSecuritycode,a.FStockholdercode,a.FBrokercode, "
					+ " a.FSeatCode,a.FBs,a.FBuydate,a.FReplaceamount,a.FBraketnum,a.FUnitcost,a.FOreplacecash, "
					+ " a.FHreplacecash,a.FOcreplacecash,a.FHcreplacecash,b.FRemaindAmount as FRemaindAmount, " 
					+ " b.FRefNum as FRefNum,b.FMakeUpDate as FMakeUpDate from "
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl")
					+ " a left join (select max(FMakeUpDate) as FMakeUpDate,"
					+ " FRemaindAmount,FNum,FRefNum from "
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FMakeUpDate < "
					+ dbl.sqlDate(tradeDate)
					+ " group by FRemaindAmount, FNum,FRefNum) b on a.FNum = b.FNum order by a.FNum) c "
					+ " where " 
					+ "((c.FRemaindAmount <> 0 and FMakeUpDate < " + dbl.sqlDate(tradeDate) 
					+ ") or FNum not in(select distinct FNum from "
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FMakeUpDate < " + dbl.sqlDate(tradeDate) 
					+ " ) and FBuyDate <= " + dbl.sqlDate(tradeDate) +") "
					+ " and FNum not in (select distinct FNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") 
					+ " where FSettleMark = 'Y' and FDate <> " + dbl.sqlDate(tradeDate)//FValDate -> FDate  20101214 panjunfang modify 与表字段一致
					+ " ) and FNum in(select distinct FNum from "
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl")
					+ " where FPortCode in ("
					+ operSql.sqlCodes(this.portCodes)
					+ ")) "
					+ " and FSecurityCode <> ' ') makeup left join (select valsec.*, valrate.fbaserate, valrate.Fportrate from (select valmkt.*, sec.ftradecury as FTradeCury "
					+ " from "
					+ "(select * from " + pub.yssGetTableName("Tb_Data_Pretvalmktprice") 
					+ " where FPortCode in(" + operSql.sqlCodes(this.portCodes) 
					+ ") and FValDate = " + dbl.sqlDate(tradeDate) 
					+ ") valmkt left join "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " sec on valmkt.fsecuritycode = sec.fsecuritycode) valsec left join "
					+ "(select * from " + pub.yssGetTableName("Tb_Data_Pretvalrate")
					+ " where FPortCode in(" + operSql.sqlCodes(this.portCodes) 
					+ ") and FValDate = " + dbl.sqlDate(tradeDate) 
					+ " )valrate on valsec.FTradeCury = valrate.Fcurycode) val on makeup.FSecurityCode = val.FSecurityCode order by FNum ";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				securityCode = rs.getString("FSecuritycode");
				
				if (!alSecs.contains(securityCode)) {
					alSecs.add(securityCode);
					securityCodes += securityCode + ",";
				}
				
				if(hmTotalAmount.get(securityCode + rs.getString("FBS")) == null){
					makeUpAmount = 0;
					
					if(rs.getDouble("FRemaindAmount") != 0){
						makeUpAmount += rs.getDouble("FRemaindAmount");//剩余数量
					}
					else{
						makeUpAmount += rs.getDouble("FReplaceAmount");//替代数量
					}
				}
				else{
					makeUpAmount = Double.parseDouble(((String)hmTotalAmount.get(securityCode + rs.getString("FBS"))));
					
					if(rs.getDouble("FRemaindAmount") != 0){
						makeUpAmount += rs.getDouble("FRemaindAmount");//剩余数量
					}
					else{
						makeUpAmount += rs.getDouble("FReplaceAmount");//替代数量
					}
				}
				
				hmTotalAmount.put(securityCode + rs.getString("FBS"), String.valueOf(makeUpAmount));
			}

			if (securityCodes.length() >= 1) {
				securityCodes = securityCodes.substring(0, securityCodes.length() - 1);
			}

			dbl.closeResultSetFinal(rs);
			rs = null;

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				num = rs.getString("FNum");
				refNum = rs.getString("FRefNum");
				securityCode = rs.getString("FSecuritycode");
				bs = rs.getString("FBS");

				buyDate = rs.getDate("FBuyDate");
				
				if(!alBuyDate.contains(buyDate)){
					alBuyDate.add(buyDate);
				}
				
				stockHolderCode = rs.getString("FStockholdercode");
				portCode = rs.getString("FPortcode");
				remaindAmount = rs.getDouble("FRemaindAmount");

				alNum.add(num);

				tradeSettleDel = new ETFTradeSettleDetailBean();

				tradeSettleDel.setNum(num);
				tradeSettleDel.setPortCode(portCode);
				
				tradeSettleDel.setSecurityCode(securityCode);
				tradeSettleDel.setStockHolderCode(stockHolderCode);
				tradeSettleDel.setBrokerCode(rs.getString("FBrokercode"));
				tradeSettleDel.setSeatCode(rs.getString("FSeatCode"));
				tradeSettleDel.setBs(bs);
				tradeSettleDel.setBuyDate(rs.getDate("FBuyDate"));
				tradeSettleDel.setReplaceAmount(rs.getDouble("FReplaceAmount"));
				tradeSettleDel.setBraketNum(rs.getDouble("FBraketNum"));
				tradeSettleDel.setUnitCost(rs.getDouble("FUnitCost"));
				tradeSettleDel.setOReplaceCash(rs.getDouble("FOReplaceCash"));
				tradeSettleDel.setHReplaceCash(rs.getDouble("FHReplaceCash"));
				tradeSettleDel.setOcReplaceCash(rs.getDouble("FOCReplaceCash"));
				tradeSettleDel.setHcReplaceCash(rs.getDouble("FHCReplaceCash"));
				tradeSettleDel.setPrice(rs.getDouble("FPrice"));
				tradeSettleDel.setMarketValueDate(rs.getDate("FMarketValueDate"));

				if (rs.getDouble("FPortRate") != 0) {
					tradeSettleDel.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"), rs.getDouble("FPortRate")));
				} else {
					tradeSettleDel.setExchangeRate(0);
				}

				// 若交易结算明细关联表中没有相关申请编号的数据
				if (remaindAmount == 0) {
					tradeSettleDelRef = new ETFTradeSettleDetailRefBean();

					tradeSettleDelRef.setNum(num);
					// 剩余数量 = 交易计算明细表中相关申请编号对应的替代数量
					tradeSettleDelRef.setRemaindAmount(rs.getDouble("FReplaceAmount"));
					tradeSettleDelRef.setRefundDate(YssFun.parseDate("9998-12-31"));
					tradeSettleDelRef.setExRightDate(YssFun.parseDate("9998-12-31"));

					if (rs.getDouble("FPortRate") != 0) {
						tradeSettleDelRef.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"), rs.getDouble("FPortRate")));
					} else {
						tradeSettleDelRef.setExchangeRate(0);
					}
					
					// 应退合计（原币）= 0
					tradeSettleDelRef.setOcRefundSum(0);
					// 应退合计（本币）= 0
					tradeSettleDelRef.setHcRefundSum(0);
					// 可退替代款余额（原币） = 交易计算明细表中相关申请编号对应的可退替代款（原币）
					tradeSettleDelRef.setOCanRepCash(rs.getDouble("FOCReplaceCash"));
					// 可退替代款余额（本币） = 交易计算明细表中相关申请编号对应的可退替代款（本币）
					tradeSettleDelRef.setHCanRepCash(rs.getDouble("FHCReplaceCash"));
				} else {
					tradeSettleDelRef = (ETFTradeSettleDetailRefBean) hmRef.get(num + String.valueOf(refNum));
				}

				tradeSettleDelRef.setSettleMark("N");
				
				if (rs.getString("FBS").equals("B")) {
					tradeSettleDelRef.setDataDirection("1");
				}
				if (rs.getString("FBS").equals("S")) {
					tradeSettleDelRef.setDataDirection("-1");
				}

				if (tradeSettleDel.getAlTradeSettleDel() == null) {
					alTradeSettleDel = new ArrayList();
				}

				alTradeSettleDel.add(tradeSettleDelRef);

				tradeSettleDel.setAlTradeSettleDel(alTradeSettleDel);

				// 若为汇总数据，则将相关证券代码，申赎日期的汇总数据储存到hmSumMakeUpInfos中
				if (rs.getString("FStockholdercode").equals(" ")) {
					if (hmSumMakeUpInfos.get(securityCode + bs + buyDate.toString()) == null) {
						hmSumMakeUpInfos.put(securityCode + bs + buyDate.toString(), tradeSettleDel);
					}
				}

				// 若为汇总数据，则将相关证券代码，申赎日期的汇总数据储存到hmSHSumMakeUpInfos中
				if (rs.getString("FSecurityCode").equals(" ")) {
					if (hmSHSumMakeUpInfos.get(stockHolderCode + bs + buyDate.toString()) == null) {
						hmSHSumMakeUpInfos.put(stockHolderCode + bs + buyDate.toString(), tradeSettleDel);
					}
				}

				hmMakeUpInfo.put(num, tradeSettleDel);
			}

			return securityCodes;
		} catch (Exception e) {
			throw new YssException("获取需要补票的数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	private HashMap getSecurityInfo(String makeupSecurityCodes) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		HashMap hmHoliday = new HashMap();    
		String holidaysCode = "";// 节假日代码
		String portCode = "";// 组合代码
		String overType = "";//结转类型
		try {  
			strSql = " select * from " + pub.yssGetTableName("TB_ETF_PARAMHOILDAYS") + " where FPortCode in ("
					+ operSql.sqlCodes(this.portCodes) + ")";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				portCode = rs.getString("FPortCode");
				overType = rs.getString("FOverType");
				holidaysCode = rs.getString("FHolidaysCode");
				hmHoliday.put(portCode + "," + overType, holidaysCode);
			}

			return hmHoliday;
		} catch (Exception e) {
			throw new YssException("获取需要补票的证券出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 获取需要补票的交易数据
	 * 
	 * @throws YssException
	 */
	private String getTradeInfoOfMakeUp() throws YssException {
		String strSql = "";
		ResultSet rs = null;

		String securityCode = ""; // 证券代码
		double subFTradeamount = 0; // 交易数量

		TradeSubBean tradeSub = null;
		String secCodes = "";
		try {
			strSql = " select trade.*, tradeType.FAmountInd as FAmountInd from " 
				    + "( select FTradeAmount, FTradeMoney, FTotalCost, "
					+ " FTradeTypeCode, FBargainDate, FBargainTime, FSecurityCode from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FSecurityCode in (select distinct FSecurityCode from (select a.FSecuritycode,"
					+ "a.Fbuydate, b.FRemaindAmount as FRemaindAmount,b.FMakeUpDate as FMakeUpDate from "
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl")
					+ " a left join (select max(FMakeUpDate) as FMakeUpDate, FRemaindAmount,FNum,FRefNum from "
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FMakeUpDate < " + dbl.sqlDate(tradeDate)
					+ " group by FRemaindAmount, FNum, FRefNum) b on a.FNum = b.FNum) c "
					+ " where ((c.FRemaindAmount <> 0 and c.FMakeUpDate < " + dbl.sqlDate(tradeDate) + ") or "
					+ " FNum not in(select distinct FNum from "
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FMakeUpDate <" + dbl.sqlDate(tradeDate) 
					+ ")) and FBuyDate <= " + dbl.sqlDate(tradeDate) 
					+ " and FSecuritycode in(select distinct FSecuritycode from "
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl")
					+ " where FPortCode in ("
					+ operSql.sqlCodes(this.portCodes)
					+ "))) and FBargainDate = "
					+ dbl.sqlDate(tradeDate)
					+ " and FTradeTypeCode in ('01', '02')) trade "
					+ " left join "
					+ pub.yssGetTableName("Tb_Base_TradeType")
					+ " tradeType on trade.FTradeTypeCode = tradeType.Ftradetypecode order by FBargainTime";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				securityCode = rs.getString("FSecuritycode"); // 证券代码
				
				secCodes += securityCode + ",";
				
				subFTradeamount = rs.getDouble("FTradeAmount");// 成交数量
				
				if (subFTradeamount != 0) {
//					tradeSub = new TradeSubBean();
//					
//					tradeSub.setTradeAmount(rs.getDouble("FTradeAmount"));// 成交数量
//					tradeSub.setTradeMoney(rs.getDouble("FTradeMoney"));// 成交金额
//					tradeSub.setTotalCost(rs.getDouble("FTotalCost"));// 清算金额
//					tradeSub.setHandAmount(rs.getDouble("FTradeAmount"));// 未用于补票的数量
//					tradeSub.setAmountInd(rs.getInt("FAmountInd"));// 数量方向

					if (hmTradeInfo.get(securityCode + rs.getInt("FAmountInd")) != null) {
						tradeSub = (TradeSubBean) hmTradeInfo.get(securityCode + rs.getInt("FAmountInd"));
						
						tradeSub.setTradeAmount(tradeSub.getTradeAmount() + rs.getDouble("FTradeAmount"));
						tradeSub.setTradeMoney(tradeSub.getTradeMoney() + rs.getDouble("FTradeMoney"));
						tradeSub.setTotalCost(tradeSub.getTotalCost() + rs.getDouble("FTotalCost"));// 清算金额
						tradeSub.setHandAmount(tradeSub.getHandAmount() + rs.getDouble("FTradeAmount"));// 未用于补票的数量
					} else {
						tradeSub = new TradeSubBean();
						
						tradeSub.setTradeAmount(rs.getDouble("FTradeAmount"));// 成交数量
						tradeSub.setTradeMoney(rs.getDouble("FTradeMoney"));// 成交金额
						tradeSub.setTotalCost(rs.getDouble("FTotalCost"));// 清算金额
						tradeSub.setHandAmount(rs.getDouble("FTradeAmount"));// 未用于补票的数量
						tradeSub.setAmountInd(rs.getInt("FAmountInd"));// 数量方向
					}

					hmTradeInfo.put(securityCode + rs.getInt("FAmountInd"), tradeSub);
				}
			}
			
			if(secCodes.length() >= 1){
				secCodes = secCodes.substring(0, secCodes.length() - 1);
			}
			
			return secCodes;
		} catch (Exception e) {
			throw new YssException("获取需要补票的交易数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 获取相关申请编号的最大的关联编号
	 * 
	 * @param num
	 * @throws YssException
	 */
	private void getMaxRefNum() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String num = "";// 申请编号
		int refNum = 0;// 最大的关联编号
		try {
			strSql = " select max(FRefNum) as FRefNum,FNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FNum in (select distinct FNum from (select a.FSecuritycode,a.Fbuydate," 
					+ " b.FRemaindAmount as FRemaindAmount,b.FMakeUpDate as FMakeUpDate from "
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl") 
					+ " a left join (select max(FMakeUpDate) as FMakeUpDate,FRemaindAmount, "
					+ " FNum, FRefNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FMakeUpDate < " + dbl.sqlDate(tradeDate)
					+ " group by FRemaindAmount, FNum, FRefNum) b on a.FNum = b.FNum) c "
					+ " where ((c.FRemaindAmount <> 0 and c.FMakeUpDate < " + dbl.sqlDate(tradeDate) 
					+ ") or FNum not in(select distinct FNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FMakeUpDate < " + dbl.sqlDate(tradeDate) 
					+ ")) and FBuyDate <= " + dbl.sqlDate(tradeDate) 
					+ " and FNum in(select distinct FNum from " 
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl") + " where FPortCode in ("
					+ operSql.sqlCodes(this.portCodes) + "))) and FNum in(select distinct FNum from " 
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FMakeUpDate <> " + dbl.sqlDate(tradeDate) 
					+ " or FExRightDate <> " + dbl.sqlDate(tradeDate) + ") group by FNum ";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				refNum = rs.getInt("FRefNum");
				num = rs.getString("FNum");
				hmMaxRefNum.put(num, String.valueOf(refNum));
			}
		} catch (Exception e) {
			throw new YssException("获取相关申请编号的最大的关联编号出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 获取估值当天组合代码对应的一级市场代码区分买卖标志的篮子数
	 * 
	 * @return
	 * @throws YssException
	 */
	public HashMap getTotalETFTradeAmount() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		HashMap hmbasket = new HashMap();
		ETFParamSetBean paramSet = null;
		double basicRate = 0;// 基准比例
		String bs = "";// 买卖标志
		String securityCode = "";// 证券代码
		double sumTradeAmount = 0;// 汇总后的成交数量
		String portCode = "";// 组合代码
		double basketCount = 0;// 篮子数
		java.util.Date buyDate = null;
		String buydates = "";
		try {
			if(alBuyDate.size() != 0){
				for(int i = 0; i < alBuyDate.size(); i++){
					buyDate = (java.util.Date)alBuyDate.get(i);
					buydates += dbl.sqlDate(buyDate) + ",";
				}
				
				if(buydates.length() > 1){
					buydates = buydates.substring(0, buydates.length() - 1);
				}
			}
			
			if(buydates.equals("")){
				buydates = "''";
			}
			
			strSql = " select sum(FTradeAmount) as FTradeAmount,FSecurityCode,FMark,FPortCode,FBargainDate from " 
				    + pub.yssGetTableName("Tb_ETF_GHInterface")
					+ " where FBargainDate in( " + buydates + " ) and FOperType = '1stdcode'" + " and FPortCode in("
					+ operSql.sqlCodes(this.portCodes) + ") group by FSecurityCode,FMark,FPortCode,FBargainDate ";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bs = rs.getString("FMark");
				securityCode = rs.getString("FSecurityCode");
				sumTradeAmount = rs.getDouble("FTradeAmount");
				portCode = rs.getString("FPortCode");
				buyDate = rs.getDate("FBargainDate");

				paramSet = (ETFParamSetBean) etfParam.get(portCode);// 根据组合代码获取ETF参数设置
				if (paramSet != null) {
					basicRate = paramSet.getNormScale();
				}

				basketCount = Math.abs(YssD.div(sumTradeAmount, basicRate));// 篮子数

				hmbasket.put(buyDate.toString() + "\t" + portCode + "\t" + bs + "\t" + securityCode, 
						String.valueOf(basketCount));
			}
			return hmbasket;
		} catch (Exception e) {
			throw new YssException("根据组合代码查询ETF份额出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 对需要强制处理的数据执行强制处理操作
	 * 
	 * @throws YssException
	 */
	private void mustGetStock(HashMap hmHoliday) throws YssException {
		String num = "";// 申请编号
		double remaindAmount = 0;// 剩余数量
		String securityCode = "";// 证券代码
		String settleMark = "";// 清算标识
		java.util.Date buyDate = null;// 申赎日期
		String portCode = "";// 组合代码
		double hcReplaceCash = 0;// 可退替代款（本币）
		double hReplaceCash = 0;//替代金额（本币）
		double interest = 0;// 总派息
		double warrantCost = 0;// 权证价值
		double sumAmount = 0;// 总数量
		double price = 0;// 估值行情
		double replaceAmount = 0;//替代数量
		ETFTradeSettleDetailBean tradeSettleDel = null;
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;
		ETFTradeSettleDetailRefBean mustTradeSettleDelRef = null;
		ETFParamSetBean paramSet = null;
		int maxDealDayNum = 0;// 最长几日补票完成
		double refMakeUpAmount = 0;//补票数量
		double refUnitCost = 0;// 单位成本
		double refOPReplaceCash = 0;// 应付替代款（原币）
		double refHPReplaceCash = 0;// 应付替代款（本币）
		double refOCReplaceCash = 0;// 可退替代款（原币）发生额
		double refHCReplaceCash = 0;// 可退替代款（本币）发生额
		double refOCanRepCash = 0;// 可退替代款（原币）余额
		double refHCanRepCash = 0;// 可退替代款（本币）余额
		double refOMakeUpCost = 0;// 补票成本（原币）
		double refHMakeUpCost = 0;// 补票成本（本币）
		String refDataMark = "";// 数据标识
		String refDataDirection = "";// 数据方向
		boolean haveRightsInfo = false;// 表示是否有权益数据
		java.util.Date refExRightDate = null;// 除权日期
		double refRemaindAmount = 0;// 强制处理后的剩余数量
		double refTrueMakeUpAmount = 0;// 补票数量
		int maxRefNum = 0;// 最大的关联编号
		int refRefNum = 0;// 关联编号
		SearchMarketPrice searchMarketPrice = null;
		java.util.Date refRefundDate = null;// 退款日期
		ArrayList alTradeSettleDel = null;
		int dealReplace = 0;// 应付替代款结转
		String bs = "";// 买卖标志

		double exchangeRate = 0;// 汇率
		EachExchangeHolidays holiday = null;//节假日获取类
		java.util.Date marketValueDate = null;//行情日期
		java.util.Date mustMakeUpDate = null;//强制处理日期
		try {
			holiday = new EachExchangeHolidays();
			holiday.setYssPub(pub);

			searchMarketPrice = new SearchMarketPrice();
			searchMarketPrice.setYssPub(pub);

			for (int i = 0; i < alNum.size(); i++) {
				num = (String) alNum.get(i);// 获取申请编号

				// 根据申请编号获取需要补票的交易结算明细实体类
				tradeSettleDel = (ETFTradeSettleDetailBean) hmMakeUpInfo.get(num);

				if (tradeSettleDel != null) {
					alTradeSettleDel = tradeSettleDel.getAlTradeSettleDel();
					tradeSettleDelRef = (ETFTradeSettleDetailRefBean) alTradeSettleDel.get(0);

					bs = tradeSettleDel.getBs();// 买卖标志
					buyDate = tradeSettleDel.getBuyDate();// 申赎日期
					portCode = tradeSettleDel.getPortCode();// 组合代码
					securityCode = tradeSettleDel.getSecurityCode();// 证券代码
					hcReplaceCash = tradeSettleDel.getHcReplaceCash();// 可退替代款(本币)
					price = tradeSettleDel.getPrice();// 估值行情
					marketValueDate = tradeSettleDel.getMarketValueDate();//行情日期
					hReplaceCash = tradeSettleDel.getHReplaceCash();//替代金额
					replaceAmount = tradeSettleDel.getReplaceAmount();//替代数量

					remaindAmount = tradeSettleDelRef.getRemaindAmount();// 剩余数量
					settleMark = tradeSettleDelRef.getSettleMark();// 清算标识
					interest = tradeSettleDelRef.getInterest();// 总派息
					warrantCost = tradeSettleDelRef.getWarrantCost();// 权证价值
					sumAmount = tradeSettleDelRef.getSumAmount();// 总数量
					refRefundDate = tradeSettleDelRef.getRefundDate();// 退款日期
					refDataDirection = tradeSettleDelRef.getDataDirection();// 数据方向
					refTrueMakeUpAmount = tradeSettleDelRef.getMakeUpAmount();//补票数量
					
					exchangeRate = tradeSettleDelRef.getExchangeRate();// 汇率
					refOPReplaceCash = tradeSettleDelRef.getOpReplaceCash(); // 应付替代款（原币）
					refHPReplaceCash = tradeSettleDelRef.getHpReplaceCash(); // 应付替代款（本币）					
					refHCanRepCash = tradeSettleDelRef.getHCanRepCash();//可退替代款（本币）

					if (interest != 0 || warrantCost != 0 || sumAmount != 0) {
						haveRightsInfo = true;
					}

					// 若清算标识为未清算
					if (settleMark.equals("N") && remaindAmount != 0 && !securityCode.equals(" ")) {
						paramSet = (ETFParamSetBean) etfParam.get(portCode);// 根据组合代码获取ETF参数设置
						
						if (paramSet != null) {
							if (bs.equals("B")) {
								dealReplace = paramSet.getISGDealReplace();// 应付替代款结转
							}
							if (bs.equals("S")) {
								dealReplace = paramSet.getISHDealReplace();// 应付替代款结转
							}

							maxDealDayNum = paramSet.getLastestDealDayNum();// 最长几日补票完成
						}

						// 若证券代码为空 则为按股东代码汇总的数据 就不做处理
						if (securityCode.equals(" ")) {
							continue;
						}
						
						holiday.parseRowStr((String) hmHoliday.get(portCode + ",lastestdealdaynum")
								+"\t"+ maxDealDayNum +"\t" + buyDate.toString());
						
						mustMakeUpDate = YssFun.parseDate(holiday.getOperValue("getWorkDate"),"yyyy-MM-dd");// 格式化
						
						// （若估值当日有行情且 估值日期不大于强制处理日期）   或 
						//（估值当日没有行情 且 估值日期等于强制处理日期） 则执行强制处理
						if((marketValueDate != null && marketValueDate.equals(tradeDate) && !tradeDate.after(mustMakeUpDate)) 
								|| (marketValueDate != null && !marketValueDate.equals(tradeDate) && tradeDate.equals(mustMakeUpDate))){
							refUnitCost = price;

							refMakeUpAmount = remaindAmount;// 补票数量等于剩余数量

							refOMakeUpCost = YssD.mul(refMakeUpAmount, refUnitCost);// 强退成本（原币）
							
							refHMakeUpCost = YssD.mul(refOMakeUpCost, exchangeRate);// 强退成本（本币）
							
							// 若上次补票的可退替代款（本币）余额 不为零
							if (refHCanRepCash != 0) {
								// 则可退替代款本币发生额 （本币）= 上次补票的可退替代款余额（本币）
								refHCReplaceCash = refHCanRepCash;
							} else {
								// 则可退替代款本币发生额（本币） = 交易结算明细表中的可退替代款（本币）
								refHCReplaceCash = hcReplaceCash;
							}

							refOCReplaceCash = refHCReplaceCash;

							if(bs.equals("B")){
								// 应付替代款(本币)
								// = 替代金额 - 替代金额 × 补票数量 / 替代数量 - round(强退本币成本,2)
								refHPReplaceCash = YssD.sub(
										hReplaceCash, 
										YssD.div(
												YssD.mul(
														hReplaceCash, 
														refTrueMakeUpAmount), 
												replaceAmount),
												YssD.round(refHMakeUpCost,2));
								
								refOPReplaceCash = refHPReplaceCash;// 应付替代款(原币) = 应付替代款(本币)
							}else{
								refOPReplaceCash = refHCReplaceCash;// 应付替代款(原币)= 可退替代款
								refHPReplaceCash = refHCReplaceCash;// 应付替代款(本币)= 可退替代款
							}
							
							// 强制处理时的可退替代款(余额) = 0
							refOCanRepCash = 0;
							refHCanRepCash = 0;

							// 获取最大的关联编号
							if (hmMaxRefNum.get(num) != null) {
								maxRefNum = Integer.parseInt((String) hmMaxRefNum.get(num));
							}

							if (maxRefNum != 0) {
								refRefNum = maxRefNum + 1;
							} else {// 若交易结算关联明细表中没有申请编号为num的相关数据
								refRefNum = 1;
							}

							hmMaxRefNum.put(num, String.valueOf(refRefNum));// 设置当前的关联编号为最大的关联编号

							refDataMark = "1"; // 强制 - 1
							settleMark = "Y";// 已清算
							
							if(bs.equals("B")){
								holiday.parseRowStr((String) hmHoliday.get(portCode + ",sgdealreplace")
										+"\t"+ dealReplace +"\t" + YssFun.formatDate(tradeDate, "yyyy-MM-dd"));
							}
							
							if(bs.equals("S")){
								holiday.parseRowStr((String) hmHoliday.get(portCode + ",shdealreplace")
										+"\t"+ dealReplace +"\t" + YssFun.formatDate(tradeDate, "yyyy-MM-dd"));
							}
							
							refRefundDate = YssFun.parseDate(holiday.getOperValue("getWorkDate"),"yyyy-MM-dd");// 格式化

							refRemaindAmount = 0;// 强制处理后，剩余数量为零
							
							mustTradeSettleDelRef = new ETFTradeSettleDetailRefBean();

							mustTradeSettleDelRef.setRefNum(String.valueOf(refRefNum));
							mustTradeSettleDelRef.setNum(num);

							mustTradeSettleDelRef.setMakeUpDate(tradeDate);
							mustTradeSettleDelRef.setUnitCost(YssFun.roundIt(refUnitCost, 2));
							mustTradeSettleDelRef.setMakeUpAmount(YssFun.roundIt(refMakeUpAmount, 2));
							mustTradeSettleDelRef.setOpReplaceCash(YssFun.roundIt(refOPReplaceCash, 2));
							mustTradeSettleDelRef.setHpReplaceCash(YssFun.roundIt(refHPReplaceCash, 2));
							mustTradeSettleDelRef.setOcReplaceCash(YssFun.roundIt(refOCReplaceCash, 2));
							mustTradeSettleDelRef.setHcReplaceCash(YssFun.roundIt(refHCReplaceCash, 2));
							mustTradeSettleDelRef.setOCanRepCash(YssFun.roundIt(refOCanRepCash, 2));
							mustTradeSettleDelRef.setHCanRepCash(YssFun.roundIt(refHCanRepCash, 2));
							mustTradeSettleDelRef.setoMakeUpCost(YssFun.roundIt(refOMakeUpCost, 2));
							mustTradeSettleDelRef.sethMakeUpCost(YssFun.roundIt(refHMakeUpCost, 2));
							mustTradeSettleDelRef.setTradeUnitCost(0);
							mustTradeSettleDelRef.setFeeUnitCost(0);

							mustTradeSettleDelRef.setDataMark(refDataMark);
							mustTradeSettleDelRef.setSettleMark(settleMark);
							mustTradeSettleDelRef.setRefundDate(refRefundDate);
							mustTradeSettleDelRef.setRemaindAmount(refRemaindAmount);
							mustTradeSettleDelRef.setDataDirection(refDataDirection);
							mustTradeSettleDelRef.setExchangeRate(exchangeRate);

							if (!haveRightsInfo) {
								refExRightDate = YssFun.parseDate("9998-12-31");
								mustTradeSettleDelRef.setExRightDate(refExRightDate);
							}

							if (!alTradeSettleDel.contains(mustTradeSettleDelRef)) {
								alTradeSettleDel.add(mustTradeSettleDelRef);
							}
							tradeSettleDel.setAlTradeSettleDel(alTradeSettleDel);

							hmMakeUpInfo.put(num, tradeSettleDel);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("对需要强制处理的数据执行强制处理操作出错！", e);
		}
	}
	
	/**
	 * 计算相关证券申购日期和估值日期前一天之间有多少个交易日
	 * 
	 * @throws YssException
	 */
	public int calculateTradeDayNum(String securityCode, java.util.Date buyDate) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		int count = 0;// 交易日天数
		try {
			strSql = " select count(*) as FCount from " + pub.yssGetTableName("Tb_Data_MarketValue") 
			 	+ " where FSecurityCode = " + dbl.sqlString(securityCode) 
			 	+ " and FMktValueDate between " + dbl.sqlDate(buyDate) 
					+ " and " + dbl.sqlDate(YssFun.addDay(tradeDate, -1));

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				count = rs.getInt("FCount");
			}

			return count;
		} catch (Exception e) {
			throw new YssException("计算相关证券申购日期和估值日期前一天之间有多少个交易日出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 计算申购日期和估值日期一天之间有多少个交易日
	 * 
	 * @param buyDate
	 * @return
	 * @throws YssException
	 */
	public int calculateAllTradeDayNum(java.util.Date buyDate) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		int count = 0;// 交易日天数
		try {
			strSql = " select count(*) as FCount from (select distinct FMktValueDate from " + pub.yssGetTableName("Tb_Data_MarketValue")
					+ " where FMktValueDate between " + dbl.sqlDate(buyDate) + " and " + dbl.sqlDate(YssFun.addDay(tradeDate, -1)) + ")";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				count = rs.getInt("FCount");
			}

			return count;
		} catch (Exception e) {
			throw new YssException("计算申购日期和估值日期前一天之间有多少个交易日出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 将数据插入到交易结算明细关联表
	 * 
	 * @throws YssException
	 */
	private void insertIntoTradeSettleDetailRef() throws YssException {
		String strSql = "";
		PreparedStatement pst = null;
		Statement st = null;
		Iterator iterator = null;
		ETFTradeSettleDetailBean tradeSetDel = null;
		ETFTradeSettleDetailRefBean tradeSetDelRef = null;

		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ArrayList alTradeSettleDel = null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;

			st = conn.createStatement();
			
			// 在交易结算明细关联表中删除系统日期为估值日期且对应已选组合代码的申请编号的数据
			strSql = " delete from "
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FDate = " + dbl.sqlDate(tradeDate)//FValDate -> FDate  20101214 panjunfang modify 与表字段一致
					+ " and FNum in (select FNum from "
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl")
					+ " where FPortCode in(" + operSql.sqlCodes(portCodes)
					+ "))";

			dbl.executeSql(strSql);

			strSql = " insert into " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ "(FNum,FMakeUpDate,FUnitCost,FMakeUpAmount,FOPReplaceCash,FHPReplaceCash,FOCPReplaceCash,"
					+ "FHCPReplaceCash,FExRightDate,FSumAmount,FRealAmount,FInterest,FWarrantCost,FRemaindAmount,"
					+ "FOCRefundSum,FHCRefundSum,FOCanRepCash,FHCanRepCash,FRefundDate,FDataMark,FDataDirection,"
					+ "FSettleMark,FRefNum,FCreator,FCreateTime,FOMakeUpCost,FHMakeUpCost,FExchangeRate,FFactAmount,"
					+ "FTradeUnitCost,FFeeUnitCost,FDeleteMark,FDate)" //FValDate -> FDate  20101214 panjunfang modify 与表字段一致
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			if (hmMakeUpInfo == null || hmMakeUpInfo.size() == 0) {
				return;
			}

			pst = dbl.openPreparedStatement(strSql);

			iterator = hmMakeUpInfo.values().iterator();
			while (iterator.hasNext()) {
				tradeSetDel = (ETFTradeSettleDetailBean) iterator.next();
				alTradeSettleDel = tradeSetDel.getAlTradeSettleDel();

				if (alTradeSettleDel == null) {
					continue;
				}

				for (int i = 0; i < alTradeSettleDel.size(); i++) {
					tradeSetDelRef = (ETFTradeSettleDetailRefBean) alTradeSettleDel.get(i);

					pst.setString(1, tradeSetDelRef.getNum());
					if (tradeSetDelRef.getMakeUpDate() == null) {
						pst.setDate(2, YssFun.toSqlDate("9998-12-31"));
					} else {
						pst.setDate(2, YssFun.toSqlDate(tradeSetDelRef.getMakeUpDate()));
					}
					pst.setDouble(3, tradeSetDelRef.getUnitCost());
					pst.setDouble(4, YssFun.roundIt(tradeSetDelRef.getMakeUpAmount(), 4));
					pst.setDouble(5, YssFun.roundIt(tradeSetDelRef.getOpReplaceCash(), 4));
					pst.setDouble(6, YssFun.roundIt(tradeSetDelRef.getHpReplaceCash(), 4));
					pst.setDouble(7, YssFun.roundIt(tradeSetDelRef.getOcReplaceCash(), 4));
					pst.setDouble(8, YssFun.roundIt(tradeSetDelRef.getHcReplaceCash(), 4));
					if (tradeSetDelRef.getExRightDate() != null) {
						pst.setDate(9, YssFun.toSqlDate(tradeSetDelRef.getExRightDate()));
					} else {
						pst.setDate(9, YssFun.toSqlDate("9998-12-31"));
					}
					pst.setDouble(10, (int) tradeSetDelRef.getSumAmount());
					pst.setDouble(11, (int) tradeSetDelRef.getRealAmount());
					pst.setDouble(12, YssFun.roundIt(tradeSetDelRef.getInterest(), 4));
					pst.setDouble(13, YssFun.roundIt(tradeSetDelRef.getWarrantCost(), 4));
					pst.setDouble(14, (int) tradeSetDelRef.getRemaindAmount());
					pst.setDouble(15, YssFun.roundIt(tradeSetDelRef.getOcRefundSum(), 4));
					pst.setDouble(16, YssFun.roundIt(tradeSetDelRef.getHcRefundSum(), 4));
					pst.setDouble(17, YssFun.roundIt(tradeSetDelRef.getOCanRepCash(), 4));
					pst.setDouble(18, YssFun.roundIt(tradeSetDelRef.getHCanRepCash(), 4));
					if (tradeSetDelRef.getRefundDate() == null) {
						pst.setDate(19, YssFun.toSqlDate("9998-12-31"));
					} else {
						pst.setDate(19, YssFun.toSqlDate(tradeSetDelRef.getRefundDate()));
					}
					pst.setString(20, tradeSetDelRef.getDataMark());
					pst.setString(21, tradeSetDelRef.getDataDirection());
					pst.setString(22, tradeSetDelRef.getSettleMark());
					pst.setString(23, tradeSetDelRef.getRefNum());
					pst.setString(24, pub.getUserCode());
					pst.setString(25, YssFun.formatDatetime(new java.util.Date()));
					pst.setDouble(26, tradeSetDelRef.getoMakeUpCost());
					pst.setDouble(27, tradeSetDelRef.gethMakeUpCost());
					pst.setDouble(28, tradeSetDelRef.getExchangeRate());
					pst.setDouble(29, tradeSetDelRef.getFactAmount());
					pst.setDouble(30, tradeSetDelRef.getTradeUnitCost());
					pst.setDouble(31, tradeSetDelRef.getFeeUnitCost());
					pst.setString(32, tradeSetDelRef.getDeleteMark());
					pst.setDate(33, YssFun.toSqlDate(tradeDate));	
					
					pst.addBatch();
				}
			}
			
			pst.executeBatch();
			
//			strSql = " delete from " + pub.yssGetTableName("tb_etf_tradstldtlref") + " where FNum in (select FNum from "
//			+ pub.yssGetTableName("tb_etf_tradestldtl") + " where FSecurityCode = ' '  and FPortCode = " 
//			+ dbl.sqlString(this.portCodes) + ") and FValDate = " + dbl.sqlDate(tradeDate);

//			dbl.executeSql(strSql);
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("将数据插入到交易结算明细关联表出错！", e);
		} finally {
			dbl.closeStatementFinal(pst, st);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * 根据ETF参数设置更新剩余数量为零 数据的清算标志
	 * 
	 * @throws YssException
	 */
	private void updateSettleMark() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String portCode = "";// 组合代码
		String securityCode = "";// 证券代码
		java.util.Date buyDate = null;// 申赎日期
		java.util.Date canMakeUpDate = null;// 允许补票的日期
		String num = "";// 申请编号
		int refNum = 0;// 关联编号
		int dealDayNum = 0;// 申购后几个交易日内补票完成
		int tradeDayNum = 0;// 相关证券在申购日和估值日之间的交易天数
		ETFParamSetBean paramSet = null;
		ArrayList alUpdateSettleMark = null;
		Statement st = null;
		Connection con = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		int dealReplace = 0;// 应付替代款结转天数
		BaseOperDeal operDeal = null;
		java.util.Date refundDate = null;// 日期
		String securityCodes = "";
		HashMap hmHoliday = null;
		int startDateNum = 0;
		String bs = "";// 买卖标志
		try {
			operDeal = new BaseOperDeal();
			operDeal.setYssPub(pub);

			strSql = " select * from (select a.*,b.FBuyDate,b.FPortCode,b.FSecurityCode,b.FBS from (select * from "
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + " where FRemaindAmount = 0 and FSettleMark = 'N') a left join "
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl") + " b on a.FNum = b.FNum) where FPortCode in (" + operSql.sqlCodes(this.portCodes)
					+ ")";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				securityCodes += rs.getString("FSecurityCode") + ",";
			}

			if (securityCodes.length() >= 1) {
				securityCodes = securityCodes.substring(0, securityCodes.length() - 1);
			}

			hmHoliday = getSecurityInfo(securityCodes);

			dbl.closeResultSetFinal(rs);
			rs = null;

			st = con.createStatement();

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				portCode = rs.getString("FPortCode");
				securityCode = rs.getString("FSecurityCode");
				bs = rs.getString("FBS");
				buyDate = rs.getDate("FBuyDate");
				num = rs.getString("FNum");
				refNum = rs.getInt("FRefNum");
				paramSet = (ETFParamSetBean) etfParam.get(portCode);// 根据组合代码获取ETF参数设置
				dealDayNum = paramSet.getDealDayNum();// 申购后几个交易日内补票完成
				startDateNum = paramSet.getBeginSupply();// 从几个工作日开始补票

				if (securityCode.equals(" ")) {
					continue;
				}
				// 根据从几个工作日开始补票和申赎日期得到可以开始补票的日期
				canMakeUpDate = operDeal.getWorkDay((String) hmHoliday.get(portCode + ",beginsupply"), buyDate, startDateNum);

				if (canMakeUpDate.equals(buyDate)) {
					tradeDayNum = calculateTradeDayNum(securityCode, YssFun.addDay(buyDate, 1));
				} else {
					tradeDayNum = calculateTradeDayNum(securityCode, canMakeUpDate);
				}

				// 相关证券在申购日和估值日之间的交易天数
				// tradeDayNum = calculateTradeDayNum(securityCode,
				// canMakeUpDate);

				// 若实际的交易天数等于ETF设置中的交易天数，且剩余数量 = 0
				if (dealDayNum == tradeDayNum) {
					if (bs.equals("B")) {
						dealReplace = paramSet.getISGDealReplace();// 申购应付替代款结转天数
						refundDate = operDeal.getWorkDay((String) hmHoliday.
								get(portCode + ",sgdealreplace"), buyDate, dealReplace);// 退款日期
					}
					if (bs.equals("S")) {
						dealReplace = paramSet.getISHDealReplace();// 赎回应付替代款结转天数
						refundDate = operDeal.getWorkDay((String) hmHoliday.
								get(portCode + ",shdealreplace"), buyDate, dealReplace);// 退款日期
					}

					refundDate = YssFun.parseDate(YssFun.formatDate(refundDate, "yyyy-MM-dd"));// 格式化
					
					if (alUpdateSettleMark == null) {
						alUpdateSettleMark = new ArrayList();
					}
					alUpdateSettleMark.add(num + "\t" + String.valueOf(refNum) + "\t" + YssFun.formatDate(refundDate, "yyyy-MM-dd"));
				}
			}

			con.setAutoCommit(false);
			bTrans = true;

			String nums = "";
			String[] splitNums = null;
			if (alUpdateSettleMark == null || alUpdateSettleMark.size() == 0) {
				return;
			}

			for (int i = 0; i < alUpdateSettleMark.size(); i++) {
				nums = (String) alUpdateSettleMark.get(i);
				splitNums = nums.split("\t");
				if (splitNums.length >= 3) {
					num = splitNums[0];
					refNum = Integer.parseInt(splitNums[1]);
					refundDate = YssFun.parseDate(splitNums[2]);
				}

				strSql = " update " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + " set FSettleMark = 'Y',FRefundDate = " + dbl.sqlDate(refundDate)
						+ " where FNum = " + dbl.sqlString(num) + " and FRefNum = " + refNum;

				st.addBatch(strSql);
			}

			st.executeBatch();

			con.commit();
			bTrans = false;
			con.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("根据ETF参数设置更新剩余数量为零 数据的清算标志出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(con, bTrans);
		}
	}
	
	/**
	 * 插入Tb_XXX_ETF_SubTrade表的操作
	 * @throws YssException
	 */
	private void InsertIntoETFSubTrade()throws YssException{
		ApartETFSupplyAnaDoInvest etfSupplyAndDoInvest = null;
		try{
			etfSupplyAndDoInvest = new ApartETFSupplyAnaDoInvest();
			etfSupplyAndDoInvest.setYssPub(pub);

			etfSupplyAndDoInvest.initOperManageInfo(this.tradeDate,this.portCodes);
			etfSupplyAndDoInvest.apartETFSupplyInverst();
		}
		catch(Exception e){
			throw new YssException("将数据插入到ETF交易子表出错！", e);
		}
	}
	
	/**
	 * 获取相关证券，相关申赎日期的最大申请编号
	 * 
	 * @throws YssException
	 */
	private void getMaxNumInfo() throws YssException {
		String strSql = "";
		ResultSet rs = null;// 声明结果集
		String num = "";// 申请编号
		String securityCode = "";// 证券代码
		String bs = "";// 买卖标志
		try {
			strSql = " select max(FNum) as FNum,FSecurityCode,FBs from (select a.FSecuritycode,a.FNum,"
					+ "a.Fbuydate, a.FBs, b.FRemaindAmount as FRemaindAmount,b.FMakeUpDate as FMakeUpDate from " 
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl")
					+ " a left join (select max(FMakeUpDate) as FMakeUpDate,FRemaindAmount,FNum,FRefNum from "
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + " where FMakeUpDate < " 
					+ dbl.sqlDate(tradeDate)
					+ " group by FRemaindAmount, FNum, FRefNum) b on a.FNum = b.FNum) c " 
					+ " where ((c.FRemaindAmount <> 0 and FMakeUpDate < " 
					+ dbl.sqlDate(tradeDate) + ") or "
					+ " FNum not in(select distinct FNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef")
					+ " where FMakeUpDate < " + dbl.sqlDate(tradeDate) 
					+ ") and FBuyDate <= " + dbl.sqlDate(tradeDate) 
					+ ") and FNum not in (select distinct FNum from " 
					+ pub.yssGetTableName("Tb_ETF_TradStlDtlRef") 
					+ " where FSettleMark = 'Y' and FDate <> " + dbl.sqlDate(tradeDate)  //FValDate -> FDate  20101214 panjunfang modify 与表字段一致
					+ ") and FNum in(select distinct FNum from " 
					+ pub.yssGetTableName("Tb_ETF_TradeStlDtl") 
					+ " where FPortCode in (" + operSql.sqlCodes(this.portCodes) 
					+ ")) group by FSecurityCode,FBs ";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				num = rs.getString("FNum");// 申请编号
				securityCode = rs.getString("FSecurityCode");// 证券代码
				bs = rs.getString("FBS");// 买卖标志

				hmMaxNum.put(securityCode + bs, num);
			}
		} catch (Exception e) {
			throw new YssException("获取相关证券，申购或赎回的最大申请编号出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
}
