package com.yss.webServices.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Element;

import com.yss.vsub.YssDbFun;
import com.yss.vsub.YssDbOperSql;
import com.yss.core.util.YssUtil;
import com.yss.main.operdata.RateTradeBean;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdata.overthecounter.DevolveTrusteeAdmin;
import com.yss.main.operdata.overthecounter.InterBankBondTradeAdmin;
import com.yss.main.operdata.overthecounter.OpenFundTradeAdmin;
import com.yss.main.operdata.overthecounter.PurchaseTradeAdmin;
import com.yss.main.operdata.overthecounter.pojo.DevolveTrusteeBean;
import com.yss.main.operdata.overthecounter.pojo.InterBankBondTradeBean;
import com.yss.main.operdata.overthecounter.pojo.PurchaseTradeBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.parasetting.SecurityBean;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 交易业务数据处理,处理webService接受到的GCS数据 *
 * 
 * @author huangqirong 2012.10.24 Stroy #2328
 * 
 */
public class GCSTradeDataDeal extends SwiftMsgDeal {

	public GCSTradeDataDeal() {
		String[] buys = new String[] { "501",// 场内买入股票
				"503",// 场内买入可转债
				"505",// 场内买入企业债
				"507",// 场内买入国债现劵
				"509",// 场内买入ETF基金
				"511",// 场内买入封闭式基金
				"517",// 场内买入权证
				"560",// 场内买入LOF基金
				"562",// 场内买入公司债
				"525",// 场内买入股票(大宗交易)
				"527",// 场内买入企业债（大宗交易）
				"576",// 场内买入国债（大宗交易）
				"591",// 场内买入公司债（大宗交易）
				"564",// 场内买入公司债(集中竞价)
				"573",// 场内买入国债现劵（固定收益平台）
				"513",// 场内买入企业债（固定收益平台）
				"593" // 场内买入国债（撮合）
		};

		for (int i = 0; i < buys.length; i++) {
			// if(!this.HtGNCNZQBUY.containsKey(buy)){
			this.HtGNCNZQBUY.put(buys[i], "01");
			// }
		}

		String[] sells = new String[] { "502",// 场内卖出股票
				"504",// 场内卖出可转债
				"506",// 场内卖出企业债
				"508",// 场内卖出国债现劵
				"510",// 场内卖出ETF基金
				"512",// 场内卖出封闭式基金
				"518",// 场内卖出权证
				"561",// 场内卖出LOF基金
				"563",// 场内卖出公司债
				"526",// 场内卖出股票(大宗交易)
				"528",// 场内卖出企业债（大宗交易）
				"577",// 场内卖出国债（大宗交易）
				"592",// 场内卖出公司债（大宗交易）
				"565",// 场内卖出公司债(集中竞价)
				"574",// 场内卖出国债现劵（固定收益平台）
				"514",// 场内卖出企业债（固定收益平台）
				"594" // 场内卖出国债
		};
		for (int i = 0; i < sells.length; i++) {
			this.HtGNCNZQSELL.put(sells[i], "02");
		}
	}

	/**
	 * 国内场内证券买卖
	 * */
	private HashMap<String, String> HtGNCNZQBUY = new HashMap<String, String>();
	private HashMap<String, String> HtGNCNZQSELL = new HashMap<String, String>();

	/**
	 * 处理交易指令数据 modify by huangqirong 2012-12-19 story #2328 调整部分日期格式处理和功能类处理方式
	 * alter table tb_001_data_subtrade add FGCSNum VARCHAR2(20) null
	 * */
	public void dealTradeData(List<Element> bizData) {

		Element tradeData = null;

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String sDate = formatter.format(date);
		int GCSDataCount = bizData.size(); // 总数据量
		int GCSQDDataCount = 0; // QD数据量
		int GCSQDInsertCount = 0; // 读入数据量
		int GCSQDNavNoInsert = 0; // 净值已确认未读入数量
		try {

			if (bizData.size() > 0) {

				this.getConnection().setAutoCommit(true);

				for (int i = 0; i < bizData.size(); i++) {
					tradeData = bizData.get(i);

					String gcsAssetID = tradeData.elementText("CUSTACT_NO"); // 资产代码
					String gcsBusiness = tradeData.elementText("TRANS_TYPE"); // 业务类型
					String gcsOrderDate = tradeData.elementText("TRAN_DATE"); // 指令接收日期
					String GCSNum = tradeData.elementText("TRANINST_ID"); // 指令编号

					if (GCSNum == null || GCSNum.trim().length() == 0)
						continue;

					GCSNum = gcsOrderDate + GCSNum; // GCS指令编号 = 指令接收日期 +
													// 指令编号(TRANINST_ID)

					String state = tradeData.elementText("STA"); // 状态

					if (gcsAssetID == null || gcsAssetID.trim().length() == 0)
						continue;
					if (gcsBusiness == null || gcsBusiness.trim().length() == 0)
						continue;
					if (gcsOrderDate == null
							|| gcsOrderDate.trim().length() == 0)
						continue;
					if (state == null || state.trim().length() == 0)
						continue;

					String[] groupAndPort = this
							.getPortCodeBySetCode(gcsAssetID);
					this.getYssPub().yssLogon(groupAndPort[0], null);

					if (groupAndPort[1].trim().length() > 0) {

						GCSQDDataCount++; // 通过资产代码识别的数据数目(即QD4.0下基金/资产的数据)；

						Date navDate = this
								.getDateTypeDataBySql(
										"select max(tdn.FNavDate) as FNavDate"
												+ " from tb_"
												+ groupAndPort[0]
												+ "_data_navdata tdn where tdn.FPortCode = "
												+ this
														.getYssPub()
														.getDbLink()
														.sqlString(
																groupAndPort[1])
												+ " and tdn.FInvmgrcode = 'total' and tdn.fretypecode ='confirm' ",
										"FNavDate");
						Date GCSTradeDate = this.toDate(gcsOrderDate);

						if (navDate == null
								|| YssFun.dateDiff(navDate, GCSTradeDate) > 0) {

							GCSQDInsertCount++; // 通过资产代码识别、净值表确认日期判断的数据数目；

							if ("27".equalsIgnoreCase(gcsBusiness)
									|| "28".equalsIgnoreCase(gcsBusiness)
									|| "29".equalsIgnoreCase(gcsBusiness)
									|| "30".equalsIgnoreCase(gcsBusiness)) { // 证券买卖
																				// 纯券买
																				// 纯券卖
																				// RVP
																				// DVP
								if (!this.dealSecurityBusiness(groupAndPort,
										tradeData, gcsAssetID, state, GCSNum))
									continue;
							} else if ("1".equalsIgnoreCase(gcsBusiness)
									|| "2".equalsIgnoreCase(gcsBusiness)) {// 银行间债券买卖
								if (!this.dealBankBondBusiness(groupAndPort,
										tradeData, gcsAssetID, state, GCSNum))
									;
								continue;
							} else if ("7".equalsIgnoreCase(gcsBusiness)) { // 债券转托管
								if (!this.dealBondTransferSupport(groupAndPort,
										tradeData, gcsAssetID, state, GCSNum))
									continue;
							} else if ("16".equalsIgnoreCase(gcsBusiness)) { // 外汇交易
								if (!this.dealRateTrade(groupAndPort,
										tradeData, gcsAssetID, state, GCSNum))
									continue;
							} else if (this.HtGNCNZQBUY
									.containsKey(gcsBusiness)
									|| this.HtGNCNZQSELL
											.containsKey(gcsBusiness)) { // 国内场内证券买卖
								if (!this.dealChinaFloorSecurityBusiness(
										groupAndPort, tradeData, gcsAssetID,
										state, GCSNum))
									continue;
							} else if ("540".equalsIgnoreCase(gcsBusiness)
									|| "541".equalsIgnoreCase(gcsBusiness)) { // 国内场内_回购
								if (!this.dealChinaFloorPurchase(groupAndPort,
										tradeData, gcsAssetID, state, GCSNum))
									continue;
							} else if ("3".equalsIgnoreCase(gcsBusiness)
									|| "4".equalsIgnoreCase(gcsBusiness)
									|| "5".equalsIgnoreCase(gcsBusiness)
									|| "6".equalsIgnoreCase(gcsBusiness)) { // 银行间债券回购
								if (!this.dealBanksPurchaseBuyBack(
										groupAndPort, tradeData, gcsAssetID,
										state, GCSNum))
									continue;
							} else if ("11".equalsIgnoreCase(gcsBusiness) // 开放式基金业务
									|| "12".equalsIgnoreCase(gcsBusiness)
									|| "13".equalsIgnoreCase(gcsBusiness)) {
								if (!this.dealOpenFundTrade(groupAndPort,
										tradeData, gcsAssetID, state, GCSNum))
									continue;
							} else if ("17".equalsIgnoreCase(gcsBusiness)
									|| "18".equalsIgnoreCase(gcsBusiness)
									|| "25".equalsIgnoreCase(gcsBusiness)
									|| "596".equalsIgnoreCase(gcsBusiness)
									|| "597".equalsIgnoreCase(gcsBusiness)) { // 境内转帐&银证转帐&保证金调整
								if (!this.dealTransferAndBondTrade(
										groupAndPort, tradeData, gcsAssetID,
										state, GCSNum))
									continue;
								/* 新股新债 - 申购 */
							} else if ("303".equalsIgnoreCase(gcsBusiness)
									|| "304".equalsIgnoreCase(gcsBusiness)
									|| "575".equalsIgnoreCase(gcsBusiness)
									|| "521".equalsIgnoreCase(gcsBusiness)
									|| "519".equalsIgnoreCase(gcsBusiness)) {
								if (!this.dealNewThighNewDebtApply(
										groupAndPort, tradeData, gcsAssetID,
										state, GCSNum))
									continue;
								/* 新股新债 - 中签 */
							} else if ("305".equalsIgnoreCase(gcsBusiness)
									|| "306".equalsIgnoreCase(gcsBusiness)
									|| "568".equalsIgnoreCase(gcsBusiness)
									|| "520".equalsIgnoreCase(gcsBusiness)
									|| "522".equalsIgnoreCase(gcsBusiness)) {
								this.dealNewThighNewDebtMidSign(groupAndPort,
										tradeData, gcsAssetID, state, GCSNum);
								/* 新股新债 - 返款 */
							} else if ("307".equalsIgnoreCase(gcsBusiness)
									|| "308".equalsIgnoreCase(gcsBusiness)
									|| "569".equalsIgnoreCase(gcsBusiness)
									|| "523".equalsIgnoreCase(gcsBusiness)) {
								this.dealNewThighNewDebtReturnMoney(
										groupAndPort, tradeData, gcsAssetID,
										state, GCSNum);
								/* 网下新股申购 */
							} else if ("10".equalsIgnoreCase(gcsBusiness)) {
								this.dealUnderNetNewThighApply(groupAndPort,
										tradeData, gcsAssetID, state, GCSNum);
								/* 场内基金认购 */
							} else if ("570".equalsIgnoreCase(gcsBusiness)
									|| "572".equalsIgnoreCase(gcsBusiness)
									|| "571".equalsIgnoreCase(gcsBusiness)) {
								if (this.dealFloorFundBuy(groupAndPort,
										tradeData, gcsAssetID, state, GCSNum))
									continue;
							}
						} else {
							GCSQDNavNoInsert++; // 净值已确认未读入数量
						}
					}
				}
				this.getConnection().commit();
				this.getConnection().setAutoCommit(false);
			}
		} catch (Exception e) {
			this.setReplyCode("1"); // 处理失败标志
			System.out.println("处理GCS交易接口出错：" + e.getMessage());
		} finally {
			if (SwiftMsgDeal.msgReturnClient.get("JYZL") != null
					&& SwiftMsgDeal.msgReturnClient.get("JYZL").length() > 0) {
				String msg = SwiftMsgDeal.msgReturnClient.get("JYZL");

				SwiftMsgDeal.msgReturnClient.put("JYZL", msg + "\n\n[" + sDate
						+ "]\tGCS交易指令接口，数据已接收。\n\n" + "总数据量：" + GCSDataCount
						+ "条\nQD数据量：" + GCSQDDataCount + "条\n读入数据量："
						+ GCSQDInsertCount + "条\n净值已确认未读入数量："
						+ GCSQDNavNoInsert + "条");
			} else {
				SwiftMsgDeal.msgReturnClient.put("JYZL", "[" + sDate
						+ "]\tGCS交易指令接口，数据已接收。\n\n" + "总数据量：" + GCSDataCount
						+ "条\nQD数据量：" + GCSQDDataCount + "条\n读入数据量："
						+ GCSQDInsertCount + "条\n净值已确认未读入数量："
						+ GCSQDNavNoInsert + "条");
			}
		}
	}

	/**
	 * 处理证券买卖数据
	 * */
	private boolean dealSecurityBusiness(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		String sql = "";
		PreparedStatement pst = null;
		boolean result = true;
		/**
		 * 填塞组合群前缀
		 * */

		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from "
						+ " tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 * 证券收付标识 = 交易类型
		 * */
		String gcsSecurityIdenti = tradeData.elementText("SECU_DIRE");
		if (gcsSecurityIdenti == null || gcsSecurityIdenti.trim().length() == 0)
			return false;
		String tradeType = "+".equalsIgnoreCase(gcsSecurityIdenti) ? "01"
				: "02";

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 交收日期
		 * */
		String gcsJSDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsJSDate == null || gcsJSDate.trim().length() == 0)
			return false;

		/**
		 * 成交价格
		 * */
		String gcsTradePrice = tradeData.elementText("DEAL_PRICE");

		if (gcsTradePrice == null)
			gcsTradePrice = "0";
		if (gcsTradePrice.trim().length() == 0)
			gcsTradePrice = "0";

		/**
		 * 成交数量
		 * */
		String gcsTradeAmount = tradeData.elementText("DEAL_QUAN");

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		/**
		 * 成交金额
		 * */
		String gcsTradeMoney = tradeData.elementText("DEAL_AMT");

		if (gcsTradeMoney == null)
			gcsTradeMoney = "0";
		if (gcsTradeMoney.trim().length() == 0)
			gcsTradeMoney = "0";

		/**
		 * 利息
		 * */
		String gcsInterest = tradeData.elementText("INTEREST");

		if (gcsInterest == null)
			gcsInterest = "0";
		if (gcsInterest.trim().length() == 0)
			gcsInterest = "0";

		Hashtable<String, String> dictFeeCodes = this.getMarket(
				groupAndPort[0], "Dict_Feecode_GCS");

		/**
		 * 佣金
		 * */
		String gcsBrokerageMoney = tradeData.elementText("COMM_FEE");

		if (gcsBrokerageMoney == null)
			gcsBrokerageMoney = "0";
		if (gcsBrokerageMoney.trim().length() == 0)
			gcsBrokerageMoney = "0";

		String brokerageCode = dictFeeCodes.get("FFeeCode1"); // 佣金费用代码

		/**
		 * 税费
		 * */
		String gcsTaxFee = tradeData.elementText("TAX_FEE");

		if (gcsTaxFee == null)
			gcsTaxFee = "0";
		if (gcsTaxFee.trim().length() == 0)
			gcsTaxFee = "0";

		String taxFeeCode = dictFeeCodes.get("FFeeCode2"); // 税费代码

		/**
		 * 其它费用
		 * */
		String gcsOtherFee = tradeData.elementText("OTHER_FEE");

		if (gcsOtherFee == null)
			gcsOtherFee = "0";
		if (gcsOtherFee.trim().length() == 0)
			gcsOtherFee = "0";

		String otherFeeCode = dictFeeCodes.get("FFeeCode3"); // 税费代码

		/**
		 * 交收金额
		 * */
		String gcsTotalCost = tradeData.elementText("DELIV_AMT");

		if (gcsTotalCost == null)
			gcsTotalCost = "0";
		if (gcsTotalCost.trim().length() == 0)
			gcsTotalCost = "0";

		/**
		 * 交易券商 Dict_Brokercode_GCS
		 * */
		String gcsBrokerCode = tradeData.elementText("TRADER_ID");
		// if(gcsBrokerCode == null || gcsBrokerCode.trim().length() == 0)
		// return false;
		String brokerCode = "";

		Hashtable<String, String> dictBrokerCodes = this.getMarket(
				groupAndPort[0], "Dict_Brokercode_GCS");
		brokerCode = dictBrokerCodes.get(gcsBrokerCode);
		brokerCode = (brokerCode == null || brokerCode.trim().length() == 0) ? gcsBrokerCode
				: brokerCode;

		/**
		 * 交易席位代码 Dict_TradeSeat_GCS
		 **/
		String gcsSeatCode = tradeData.elementText("SEAT_NO");
		// if(gcsSeatCode == null || gcsSeatCode.trim().length() == 0)
		// return false;
		String seatCode = "";

		Hashtable<String, String> dictSeatCodes = this.getMarket(
				groupAndPort[0], "Dict_TradeSeat_GCS");
		seatCode = dictSeatCodes.get(gcsSeatCode);
		seatCode = (seatCode == null || seatCode.trim().length() == 0) ? gcsSeatCode
				: seatCode;

		/**
		 * 附言
		 * */
		String gcsPs = tradeData.elementText("PS");

		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from " + " tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from " + " tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from " + " tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		/**
		 * 不存在则直接新增
		 * */
		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_Data_SubTrade "
				+ "(FNUM,FSECURITYCODE,FPORTCODE ,FBROKERCODE ,FINVMGRCODE,FTRADETYPECODE ,FCASHACCCODE,FATTRCLSCODE,"
				+ "FRATEDATE, FBARGAINDATE ,FBARGAINTIME,FSETTLEDATE ,FSETTLETIME ,FMATUREDATE,FMATURESETTLEDATE,FFACTCASHACCCODE,"
				+ "FFACTSETTLEMONEY,FEXRATE,FFACTBASERATE,FFACTPORTRATE,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,"
				+ "FOLDALLOTAMOUNT,FALLOTFACTOR,FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBAILMONEY,FFEECODE1,"
				+ "FTRADEFEE1,FFEECODE2,FTRADEFEE2,FFEECODE3,FTRADEFEE3,FTOTALCOST,"
				+ "FCOST,FMCOST,"
				+ "FVCOST,FBASECURYCOST,FMBASECURYCOST,FVBASECURYCOST,FPORTCURYCOST,FMPORTCURYCOST,"
				+ "FVPORTCURYCOST,FSETTLESTATE,FFACTSETTLEDATE,FSETTLEDESC,FORDERNUM,FDATASOURCE,FDATABIRTH,FSETTLEORGCODE,"
				+ "FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,"
				+ "FSEATCODE,FSTOCKHOLDERCODE,FDS,"
				+ "FSECURITYDELAYSETTLESTATE,FBROKERIDCODE,"
				+ "FHANDCOSTSTATE,"
				+ "FGCSNum) values(?,?,?,?,?,?,?,?,"
				+ // FMTREPLACEDATE
				"?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?," + "?,?,?," + "?,?," + "?," + "?)";
		try {
			pst = this.getConnection().prepareStatement(sql);
			pst.setString(1, ""
					+ this.getSubTradeNum(groupAndPort[0], gcsBargainDate,
							tradeType));// 交易编号
			pst.setString(2, securityCode); // 证券代码
			pst.setString(3, groupAndPort[1]); // 组合
			pst.setString(4, brokerCode); // 券商
			pst.setString(5, " "); // 投资经理
			pst.setString(6, tradeType); // 交易类型
			pst.setString(7, cashAcountCode); // 现金账户
			pst.setString(8, " "); // 所属分类
			pst.setDate(9, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 汇率日期
			pst.setDate(10, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 成交日期
			pst.setString(11, "00:00:00"); // 成交时间
			pst
					.setDate(12, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(13, "00:00:00"); // 结算时间
			pst.setDate(14, new java.sql.Date(this.toDate("9998-12-31")
					.getTime())); // 到期日期
			pst.setDate(15, new java.sql.Date(this.toDate("9998-12-31")
					.getTime())); // 到期结算日期
			pst.setString(16, cashAcountCode); // 实际结算账户
			pst.setDouble(17, Double.parseDouble(gcsTotalCost)); // 实际结算金额
			pst.setDouble(18, 1); // 兑换汇率
			pst.setDouble(19, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base")); // 实际基础汇率
			pst.setDouble(20, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setInt(21, 1); // 是否自动结算
			pst.setDouble(22, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setDouble(23, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base"));
			pst.setDouble(24, 100); // 分配比例
			pst.setDouble(25, Double.parseDouble(gcsTradeAmount)); // 原始分配数量
			pst.setDouble(26, 1); // 分配因子
			pst.setDouble(27, Double.parseDouble(gcsTradeAmount)); // 交易数量
			pst.setDouble(28, Double.parseDouble(gcsTradePrice)); // 交易价格
			pst.setDouble(29, Double.parseDouble(gcsTradeMoney)); // 交易金额
			pst.setDouble(30, Double.parseDouble(gcsInterest)); // 应收利息
			pst.setDouble(31, 0); // 保证金
			pst.setString(32, brokerageCode); // 费用代码1
			pst.setDouble(33, Double.parseDouble(gcsBrokerageMoney)); // 费用1为 佣金
			pst.setString(34, taxFeeCode); // 费用代码2
			pst.setDouble(35, Double.parseDouble(gcsTaxFee)); // 费用2 税费
			pst.setString(36, otherFeeCode); // 费用3
			pst.setDouble(37, Double.parseDouble(gcsOtherFee)); // 其他费用
			pst.setDouble(38, Double.parseDouble(gcsTotalCost)); // 交收金额
			pst.setDouble(39, 0);
			pst.setDouble(40, 0);
			pst.setDouble(41, 0);
			pst.setDouble(42, 0);
			pst.setDouble(43, 0);
			pst.setDouble(44, 0);
			pst.setDouble(45, 0);
			pst.setDouble(46, 0);
			pst.setDouble(47, 0);
			pst.setInt(48, 0); // 结算状态
			pst
					.setDate(49, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(50, " "); // 结算描述
			pst.setString(51, " "); // 订单编号
			pst.setInt(52, 1); // 数据来源
			pst.setString(53, " "); // 交易来源
			pst.setString(54, " "); // 结算机构代码
			pst.setString(55, gcsPs); // 描述
			pst.setInt(56, 1); // 审核状态
			pst.setString(57, "GCS"); // 创建人
			pst.setString(58, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 创建时间
			pst.setString(59, "GCS"); // 审核人
			pst.setString(60, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 审核时间
			pst.setString(61, seatCode); // 席位代码
			pst.setString(62, " "); // 股东代码
			pst.setString(63, "ZD_JK"); // 操作类型 //代表接口读入
			pst.setInt(64, 0); // 延迟交割标识
			pst.setString(65, gcsBrokerCode); // 券商代码
			pst.setInt(66, 0); // 手动修改成本标志
			pst.setString(67, GCSNum); // GCSnum

			pst.executeUpdate();
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("处理GCS证券买卖数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理GCS证券买卖数据出错：" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 银行间债券买卖
	 * */
	private boolean dealBankBondBusiness(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		String sql = "";
		PreparedStatement pst = null;
		boolean result = true;

		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());

		/**
		 * 填塞组合群前缀
		 * */

		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
			return false;
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 * 证券收付标识 = 交易类型
		 * */
		String gcsSecurityIdenti = tradeData.elementText("TRANS_TYPE");
		if (gcsSecurityIdenti == null || gcsSecurityIdenti.trim().length() == 0)
			return false;
		String tradeType = "";
		if ("1".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "01";
		} else if ("2".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "02";
		}

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 交收日期
		 * */
		String gcsJSDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsJSDate == null || gcsJSDate.trim().length() == 0)
			return false;

		/**
		 * 成交价格
		 * */
		// String gcsTradePrice = tradeData.elementText("DEAL_PRICE");
		// if(gcsTradePrice == null || gcsTradePrice.trim().length() == 0)
		// return false;

		/**
		 * 成交数量
		 * */
		String gcsTradeAmount = tradeData.elementText("DEAL_QUAN");

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		else if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		/**
		 * 成交金额
		 * */
		String gcsTradeMoney = tradeData.elementText("DEAL_AMT");

		if (gcsTradeMoney == null)
			gcsTradeMoney = "0";
		else if (gcsTradeMoney.trim().length() == 0)
			gcsTradeMoney = "0";

		/**
		 * 手续费
		 * */
		String gcsPorcessFee = tradeData.elementText("SXF_FEE");

		if (gcsPorcessFee == null)
			gcsPorcessFee = "0";
		else if (gcsPorcessFee.trim().length() == 0)
			gcsPorcessFee = "0";

		/*
		 * Hashtable<String, String> dictFeeCodes =
		 * this.getMarket(groupAndPort[0], "Dict_Feecode_GCS"); //费用代码字典配置
		 * 
		 * String porcessFeeCode= dictFeeCodes.get("FFee"); //手续费费用代码
		 */

		/**
		 * 其它费用
		 * */
		String gcsOtherFee = tradeData.elementText("OTHER_FEE"); // 结算费

		if (gcsOtherFee == null)
			gcsOtherFee = "0";
		else if (gcsOtherFee.trim().length() == 0)
			gcsOtherFee = "0";

		/**
		 * 交收金额
		 * */
		// String gcsTotalCost = tradeData.elementText("DELIV_AMT");
		// if(gcsTotalCost == null || gcsTotalCost.trim().length() == 0)
		// return false;

		InterBankBondTradeAdmin interBankBondTradeAdmin = new InterBankBondTradeAdmin();
		interBankBondTradeAdmin.setYssPub(this.getYssPub());
		InterBankBondTradeBean interBankBondTrade = new InterBankBondTradeBean();
		interBankBondTrade.setYssPub(this.getYssPub());
		interBankBondTrade.setStrBusTypeCode(tradeType); // 交易方式
		interBankBondTrade.setStrSecurityCode(securityCode);// 证券代码
		interBankBondTrade.setStrBargainDate(gcsBargainDate); // 成交日期
		interBankBondTrade.setDbTradeNum(Double.parseDouble(gcsTradeAmount)); // 交易数量
		interBankBondTrade.setStrPortCode(groupAndPort[1]); // 组合
		interBankBondTradeAdmin.setBondTrade(interBankBondTrade);

		String interest = ""; // 债券利息
		String clearMoney = ""; // 清算金额
		try {
			interest = interBankBondTradeAdmin.getOperValue("calBondInterest"); // 计算债券利息
			interBankBondTrade.parseRowStr(interest);
			interest = "" + interBankBondTrade.getDbBbondInterest(); // 获取利息
		} catch (Exception e) {
			System.out.println("计算GCS银行间债券利息出错：" + e.getMessage());
		}

		try {
			interBankBondTrade.setDbTradeMoney(Double
					.parseDouble(gcsTradeMoney)); // 成交金额
			interBankBondTrade.setDbBankFee(0); // 银行费
			interBankBondTrade.setDbPoundageFee(Double
					.parseDouble(gcsPorcessFee)); // 手续费
			interBankBondTrade.setDbSettlementFee(Double
					.parseDouble(gcsOtherFee)); // 结算费
			clearMoney = interBankBondTradeAdmin.getOperValue("calSettleMoney"); // 计算清算金额
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("计算GCS银行间债券结算金额出错：" + e.getMessage());
			return false;
		}

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from " + " tb_"
							+ groupAndPort[0]
							+ "_Data_IntBakBond tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_Data_IntBakBond where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from " + " tb_"
							+ groupAndPort[0]
							+ "_Data_IntBakBond tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_Data_IntBakBond where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		/**
		 * 不存在则直接新增
		 * */
		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_Data_IntBakBond "
				+ "(FNUM, FTRADETYPECODE, FSECURITYCODE, FPORTCODE, FBARGAINDATE, FSETTLEDATE, FINVESTTYPE, FINVMGRCODE, "
				+ " FATTRCLSCODE, FCASHACCCODE,"
				+ " FAFFCORPCODE, FPORTCURYRATE, FBASECURYRATE, FTRADEAMOUNT, FTRADEMONEY, FBONDINS, FFEE, FSETTLEFEE," +
				" FBANKFEE, "
				+ " FSETTLEMONEY,"
				+ " FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME,FGCSNUM)"
				+ // FIMPNUM, FSECISSUERCODE
				" values(?,?,?,?,?,?,?,?,?,?," + " ?,?,?,?,?,?,?,?,?,?,"
				+ " ?,?,?,?,?,?,?)";

		try {
			pst = this.getConnection().prepareStatement(sql);
			pst.setString(1, "" + this.getIntBakBondNum(gcsBargainDate));// 银行间债券交易编号
			pst.setString(2, tradeType); // 交易方式
			pst.setString(3, securityCode); // 证券代码
			pst.setString(4, groupAndPort[1]); // 组合
			pst.setDate(5, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 交易日期
			pst.setDate(6, new java.sql.Date(this.toDate(gcsJSDate).getTime())); // 结算日期
			pst.setString(7, "C"); // 投资类型
			pst.setString(8, " "); // 投资经理
			pst.setString(9, " "); // 所属分类
			pst.setString(10, cashAcountCode); // 现金账户

			pst.setString(11, " "); // 交易关联方
			pst.setDouble(12, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));// 组合汇率
			pst.setDouble(13, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base"));// 基础汇率
			pst.setDouble(14, Double.parseDouble(gcsTradeAmount)); // 数量
			pst.setDouble(15, Double.parseDouble(gcsTradeMoney)); // 交易金额
			pst.setDouble(16, Double.parseDouble(interest)); // 债券利息QD自行计算,计算方法参考现有界面中[债券利息]的取值方法。
			pst.setDouble(17, Double.parseDouble(gcsPorcessFee)); // 手续费
			pst.setDouble(18, Double.parseDouble(gcsOtherFee)); // 结算费
			pst.setDouble(19, 0); // 暂时为 0 银行费
			pst.setDouble(20, Double.parseDouble(clearMoney)); // 结算金额QD自行计算。(根据相关通参设置计算

			pst.setInt(21, 1); // 审核状态
			pst.setString(22, "GCS"); // 创建人
			pst.setString(23, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 创建时间
			pst.setString(24, "GCS");
			pst.setString(25, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 审核时间
			pst.setString(26, GCSNum); // GCSnum

			pst.executeUpdate();
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("处理GCS银行间债券买卖数据出错：" + e.getMessage());
			return false;
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理GCS证券买卖数据出错：" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 债券转托管
	 * */
	private boolean dealBondTransferSupport(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		String sql = "";
		boolean result = true;
		PreparedStatement pst = null;
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());

		/**
		 * 填塞组合群前缀
		 * */

		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
			return false;
		}

		String gcsTradeType = "bond_trusteeship"; // GCS 交易类型

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String securityCode = ""; // 成交证券
		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 * 转入证券
		 * */
		String gcsInExchange = tradeData.elementText("DELIV_NO"); // 交割场所 即 QD
																	// 托管市场
		if (gcsInExchange == null || gcsInExchange.trim().length() == 0)
			return false;

		String tmpGcsInExchange = exchangeCodes.get(gcsInExchange);
		tmpGcsInExchange = (tmpGcsInExchange == null || tmpGcsInExchange.trim()
				.length() == 0) ? gcsInExchange : tmpGcsInExchange;

		String gcsInSecurity = tradeData.elementText("IN_SECU_NO"); // 转入证券
		if (gcsInSecurity == null || gcsInSecurity.trim().length() == 0)
			return false;

		int securityCount1 = this.getCountbySql(
				"select count(*) as FSecurityCount from "
						+ " tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(gcsInSecurity)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink().sqlString(
								tmpGcsInExchange), "FSecurityCount");

		if (securityCount1 != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + gcsInSecurity + "交易所为"
						+ gcsInExchange + "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + gcsInSecurity
						+ "交易所为" + gcsInExchange + "的证券。");
			return false;
		}

		gcsInSecurity = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(gcsInSecurity)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(tmpGcsInExchange),
				"FSECURITYCODE");

		String gcsBargainDate = tradeData.elementText("TRANS_DATE"); // 成交日期
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		String gcsTradeAmount = tradeData.elementText("DEAL_QUAN"); // 成交数量

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		else if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		String gcsTradeMoney = tradeData.elementText("DEAL_AMT"); // 成交金额

		if (gcsTradeMoney == null)
			gcsTradeMoney = "0";
		else if (gcsTradeMoney.trim().length() == 0)
			gcsTradeMoney = "0";

		String tradeNum = "" + this.getBondTransFerNum(gcsBargainDate); // 债券转托管交易数据编号

		String bondIns = ""; // 转出利息

		DevolveTrusteeAdmin devolveTrustee = new DevolveTrusteeAdmin();
		devolveTrustee.setYssPub(this.getYssPub());

		DevolveTrusteeBean truestee = new DevolveTrusteeBean();
		truestee.setYssPub(this.getYssPub());
		truestee.setStrBargainDate(gcsBargainDate);
		truestee.setStrPortCode(groupAndPort[1]);
		truestee.setStrAttrClsCode(" ");
		truestee.setStrInvMgrCode(" ");
		truestee.setStrSecurityCode(securityCode);
		truestee.setDbOutAmount(Double.parseDouble(gcsTradeAmount));
		truestee.setStrTradeNo(tradeNum);
		devolveTrustee.setDevTrustee(truestee);

		try {
			bondIns = devolveTrustee.getOperValue("calBondInterest");
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("获取GCS债券转托管利息出错：" + e.getMessage());
		}

		String apprec = ""; // 转出估值增值

		try {
			apprec = devolveTrustee.getOperValue("calValInc");
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("获取GCS债券转托管估值增值出错：" + e.getMessage());
		}

		String discount = "0"; // 转出溢价

		// 币种
		String curyCode = this.getDatabySql("select * from tb_"
				+ groupAndPort[0] + "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FTradeCury");
		String baseCuryRate = "";
		String portCuryRate = "";
		try {
			baseCuryRate = ""
					+ operDeal.getCuryRate(this.toDate(gcsBargainDate),
							curyCode, groupAndPort[1], "base");// 基础汇率
			portCuryRate = ""
					+ operDeal.getCuryRate(this.toDate(gcsBargainDate),
							curyCode, groupAndPort[1], "port");// 组合汇率
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("获取GCS基础汇率或组合汇率出错：" + e.getMessage());
			return false;
		}

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from " + " tb_"
							+ groupAndPort[0]
							+ "_DATA_DEVTRUSTBOND tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_DATA_DEVTRUSTBOND where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from " + " tb_"
							+ groupAndPort[0]
							+ "_DATA_DEVTRUSTBOND tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_DATA_DEVTRUSTBOND where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		/**
		 * 不存在则直接新增
		 * */
		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_DATA_DEVTRUSTBOND "
				+ "(FNUM, FSECURITYCODE, FPORTCODE, FBARGAINDATE, FINVESTTYPE, "
				+ " FINVMGRCODE, FATTRCLSCODE, FOUTEXCHANGECODE, FINEXCHANGECODE, FPORTCURYRATE,"
				+ " FBASECURYRATE, FAMOUNT, FMONEY, FDISCOUNT, FBONDINS, "
				+ " FAPPREC, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER,"
				+ " FCHECKTIME, FBONDTRADETYPE, FINSECURITYCODE, FININVESTTYPE, FINATTRCLSCODE,FGCSNUM)"
				+ " values(?,?,?,?,?,?,?,?,?,?," + " ?,?,?,?,?,?,?,?,?,?,"
				+ " ?,?,?,?,?,?)";

		try {
			pst = this.getConnection().prepareStatement(sql);
			pst.setString(1, tradeNum);// 银行间债券交易编号
			pst.setString(2, securityCode); // 证券代码
			pst.setString(3, groupAndPort[1]); // 组合
			pst.setDate(4, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 交易日期
			pst.setString(5, "C"); // 投资类型
			pst.setString(6, " "); // 投资经理
			pst.setString(7, " "); // 所属分类
			pst.setString(8, qdExchangeCode); // 转出交易场所
			pst.setString(9, gcsInExchange); // 转入交易市场
			pst.setDouble(10, Double.parseDouble(portCuryRate)); // 组合汇率
			pst.setDouble(11, Double.parseDouble(baseCuryRate)); // 组合汇率
			pst.setDouble(12, Double.parseDouble(gcsTradeAmount)); // 成交数量
			pst.setDouble(13, Double.parseDouble(gcsTradeMoney)); // 成交金额
			pst.setDouble(14, Double.parseDouble(discount)); // 转出溢价
			pst.setDouble(15, Double.parseDouble(bondIns)); // 转出利息
			pst.setDouble(16, Double.parseDouble(apprec)); // 转出估值增值
			pst.setInt(17, 1); // 审核状态
			pst.setString(18, "GCS"); // 创建人
			pst.setString(19, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 创建时间
			pst.setString(20, "GCS");
			pst.setString(21, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 审核时间
			pst.setString(22, gcsTradeType); // 交易类型
			pst.setString(23, gcsInSecurity);
			pst.setString(24, "C"); // 转入投资类型
			pst.setString(25, " "); // 转入所属分类
			pst.setString(26, GCSNum); // GCSnum
			pst.executeUpdate();
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("处理GCS银行间债券买卖数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理GCS证券买卖数据出错：" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 外汇交易
	 * */
	private boolean dealRateTrade(String[] groupAndPort, Element tradeData,
			String gcsAssetID, String state, String GCSNum) {
		String sql = "";
		boolean result = true;
		PreparedStatement pst = null;
		String gcsBargainDate = tradeData.elementText("TRANS_DATE"); // 交易日期
		String gcsSettleDate = tradeData.elementText("FUND_DELIV_DATE"); // 交收日期

		String gcsSettlementMark = tradeData.elementText("FUND_DIRE"); // 交收标志 +
																		// -
		if (gcsSettlementMark == null || gcsSettlementMark.trim().length() == 0)
			return false;

		/**
		 * 账户A部分
		 * */
		String gcsCuryCodeA = tradeData.elementText("CURR"); // 货币符号A
		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		String curyCodeA = dictCurys.get(gcsCuryCodeA);
		curyCodeA = (curyCodeA == null || curyCodeA.trim().length() == 0) ? gcsCuryCodeA
				: curyCodeA;

		String gcsFoundA = tradeData.elementText("FUND_ACT_NO"); // 资金账号A

		if (gcsFoundA == null || gcsFoundA.trim().length() == 0)
			return false;

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsFoundA,
				curyCodeA);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsFoundA + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsFoundA
						+ "的现金帐户。");
			return false;
		}

		gcsFoundA = this.getCashAcc(groupAndPort[0], gcsFoundA, curyCodeA);

		String tradeMoneyA = tradeData.elementText("DEAL_AMT"); // 成交资金A

		/**
		 * 账户B部分
		 * */
		String gcsCuryCodeB = tradeData.elementText("FOREX_BUY_CURR"); // 货币符号B
		String curyCodeB = dictCurys.get(gcsCuryCodeB);
		curyCodeB = (curyCodeB == null || curyCodeB.trim().length() == 0) ? gcsCuryCodeB
				: curyCodeB;
		String gcsFoundB = tradeData.elementText("FOREX_BUY_FUND_ACT"); // 资金账号B
		if (gcsFoundB == null || gcsFoundB.trim().length() == 0)
			return false;

		cashAcccount = this.getAccCount(groupAndPort[0], gcsFoundB, curyCodeB);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsFoundB + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsFoundB
						+ "的现金帐户。");
			return false;
		}
		gcsFoundB = this.getCashAcc(groupAndPort[0], gcsFoundB, curyCodeA);
		String tradeMoneyB = tradeData.elementText("FOREX_BUY_AMT"); // 成交资金B

		// 兑换汇率
		double exCuryRate = 0;

		if ("+".equalsIgnoreCase(gcsSettlementMark)) {
			exCuryRate = YssD.round(YssD.div(Double.parseDouble(tradeMoneyB),
					Double.parseDouble(tradeMoneyA)), 13);
		} else if ("-".equalsIgnoreCase(gcsSettlementMark)) {
			exCuryRate = YssD.round(YssD.div(Double.parseDouble(tradeMoneyA),
					Double.parseDouble(tradeMoneyB)), 13);
		}

		// 基础 组合 金额
		RateTradeBean rateTrade = new RateTradeBean();
		rateTrade.setYssPub(this.getYssPub());
		rateTrade.setTradeDate(this.toDate(gcsBargainDate));
		rateTrade.setExCuryRate(exCuryRate);// 兑换汇率
		if ("-".equalsIgnoreCase(gcsSettlementMark)) {
			rateTrade.setSCashAccCode(gcsFoundA);
			rateTrade.setPortCode(groupAndPort[1]);
			rateTrade.setAnalysisCode1(" ");
			rateTrade.setAnalysisCode2(" ");
			rateTrade.setAnalysisCode3(" ");
			rateTrade.setSMoney(Double.parseDouble(tradeMoneyA));
			rateTrade.setSCuryCode(curyCodeA);

			rateTrade.setBCashAccCode(gcsFoundB);
			rateTrade.setPortCode(groupAndPort[1]);
			rateTrade.setAnalysisCode1(" ");
			rateTrade.setAnalysisCode2(" ");
			rateTrade.setAnalysisCode3(" ");
			rateTrade.setSMoney(Double.parseDouble(tradeMoneyB));
			rateTrade.setSCuryCode(curyCodeB);
		} else if ("+".equalsIgnoreCase(gcsSettlementMark)) {
			rateTrade.setSCashAccCode(gcsFoundB);
			rateTrade.setPortCode(groupAndPort[1]);
			rateTrade.setAnalysisCode1(" ");
			rateTrade.setAnalysisCode2(" ");
			rateTrade.setAnalysisCode3(" ");
			rateTrade.setSMoney(Double.parseDouble(tradeMoneyB));
			rateTrade.setSCuryCode(curyCodeB);

			rateTrade.setBCashAccCode(gcsFoundA);
			rateTrade.setPortCode(groupAndPort[1]);
			rateTrade.setAnalysisCode1(" ");
			rateTrade.setAnalysisCode2(" ");
			rateTrade.setAnalysisCode3(" ");
			rateTrade.setSMoney(Double.parseDouble(tradeMoneyA));
			rateTrade.setSCuryCode(curyCodeA);
		}

		try {
			rateTrade.parseRowStr(rateTrade.getOperValue("costfx"));
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("GCS计算外汇交易基础金额和组合金额时出错：" + e.getMessage());
		}

		// 基础金额
		String baseMoney = "" + rateTrade.getBaseMoney();

		// 组合金额
		String portMoney = "" + rateTrade.getPortMoney();

		/**
		 * 附言
		 * */
		String ps = tradeData.elementText("PS");
		if (ps != null && ps.trim().length() > 0)
			ps = ps.substring(0, 100);

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from " + " tb_"
							+ groupAndPort[0]
							+ "_data_ratetrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.FRateTradeNum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_ratetrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from " + " tb_"
							+ groupAndPort[0]
							+ "_data_ratetrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from " + " tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.FRateTradeNum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_ratetrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_data_ratetrade "
				+ " (FNUM,FPORTCODE,FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FBANALYSISCODE1,FBANALYSISCODE2," +
				" FBANALYSISCODE3,"
				+ " FBPORTCODE,FTRADETYPE,FCATTYPE,FBCASHACCCODE,FBCURYCODE,FBMONEY,FSCASHACCCODE,FSCURYCODE,FSMONEY,"
				+ " FTRADEDATE,FTRADETIME,FSETTLEDATE,FSETTLETIME,FEXCURYRATE,FBSETTLEDATE,FBSETTLETIME,"
				+ " FBCURYFEE,FSCURYFEE,FBASEMONEY,FPORTMONEY,FLONGCURYRATE,FRATEFX,"
				+ " FUPDOWN,FDATASOURCE,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,"
				+ " FRATETRADETYPE,FGCSNUM) values " + " (?,?,?,?,?,?,?,?,"
				+ "  ?,?,?,?,?,?,?,?,?" + "  ?,?,?,?,?,?,?," + "  ?,?,?,?,?,?,"
				+ "  ?,?,?,?,?,?,?,?," + "  ?,? )";
		try {
			pst = this.getConnection().prepareStatement(sql);
			pst.setString(1, this.getRateTradeNum(gcsSettleDate, "Tb_"
					+ groupAndPort[0] + "_data_ratetrade")); // 外汇交易编号
			pst.setString(2, groupAndPort[1]); // 组合
			pst.setString(3, " "); // 分析代码1
			pst.setString(4, " "); // 分析代码2
			pst.setString(5, " "); // 分析代码3
			pst.setString(6, " "); // 买入分析代码1
			pst.setString(7, " "); // 买入分析代码2
			pst.setString(8, " "); // 买入分析代码3
			pst.setString(9, groupAndPort[1]); // 买入组合
			pst.setInt(10, 0); // 交易方式
			pst.setInt(11, 0); // 所属分类
			if ("+".equalsIgnoreCase(gcsSettlementMark)) {
				pst.setString(12, gcsFoundA); // 买入
				pst.setString(13, curyCodeA);
				pst.setDouble(14, Double.parseDouble(tradeMoneyA));
				pst.setString(15, gcsFoundB); // 卖出
				pst.setString(16, curyCodeB);
				pst.setDouble(17, Double.parseDouble(tradeMoneyB));
			} else if ("-".equalsIgnoreCase(gcsSettlementMark)) {
				pst.setString(12, gcsFoundB); // 买入
				pst.setString(13, curyCodeB);
				pst.setDouble(14, Double.parseDouble(tradeMoneyB));
				pst.setString(15, gcsFoundA); // 卖出
				pst.setString(16, curyCodeA);
				pst.setDouble(17, Double.parseDouble(tradeMoneyA));
			}
			pst.setDate(18, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 交易日期
			pst.setString(19, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 交易时间
			pst.setDate(20, new java.sql.Date(this.toDate(gcsSettleDate)
					.getTime())); // 结算日期
			pst.setString(21, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 结算时间
			pst.setDouble(22, exCuryRate); // 兑换汇率
			pst.setDate(23, new java.sql.Date(this.toDate(gcsSettleDate)
					.getTime())); // 买入结算日期
			pst.setString(24, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 买入结算时间
			pst.setDouble(25, 0); // 交易费
			pst.setDouble(26, 0); // 交易费
			pst.setDouble(27, Double.parseDouble(baseMoney)); // 基础金额
			pst.setDouble(28, Double.parseDouble(portMoney)); // 组合金额
			pst.setDouble(29, 0);
			pst.setDouble(30, 0);
			pst.setDouble(31, 0);
			pst.setString(32, "ZD"); // ZD 为自动读取的数据
			pst.setString(33, ps); // 备注
			pst.setInt(34, 1); // 审核状态
			pst.setString(35, "GCS"); // 创建人
			pst.setString(36, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 创建时间
			pst.setString(37, "GCS");
			pst.setString(38, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 审核时间
			pst.setString(39, "2"); // 外汇类型
			pst.setString(40, GCSNum); // GCSnum
			pst.executeUpdate();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("GCS处理外汇交易数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理GCS证券买卖数据出错：" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 处理国内场内证券买卖数据
	 * */
	private boolean dealChinaFloorSecurityBusiness(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		String sql = "";
		PreparedStatement pst = null;
		boolean result = true;
		/**
		 * 填塞组合群前缀
		 * */

		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 * 证券收付标识 = 交易类型
		 * */
		String gcsSecurityIdenti = tradeData.elementText("SECU_DIRE");
		if (gcsSecurityIdenti == null || gcsSecurityIdenti.trim().length() == 0)
			return false;

		// 交易类型
		String tmpTradeType = tradeData.elementText("TRANS_TYPE");
		if (tmpTradeType == null || tmpTradeType.trim().length() == 0)
			return false;

		String tradeType = "";

		if (this.HtGNCNZQBUY.containsKey(tmpTradeType))
			tradeType = this.HtGNCNZQBUY.get(tmpTradeType);
		else if (this.HtGNCNZQSELL.containsKey(tmpTradeType))
			tradeType = this.HtGNCNZQSELL.get(tmpTradeType);

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 交收日期
		 * */
		String gcsJSDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsJSDate == null || gcsJSDate.trim().length() == 0)
			return false;

		/**
		 * 成交价格
		 * */
		String gcsTradePrice = tradeData.elementText("DEAL_PRICE");

		if (gcsTradePrice == null)
			gcsTradePrice = "0";
		else if (gcsTradePrice.trim().length() == 0)
			gcsTradePrice = "0";

		/**
		 * 成交数量
		 * */
		String gcsTradeAmount = tradeData.elementText("DEAL_QUAN");

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		else if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		/**
		 * 成交金额
		 * */
		String gcsTradeMoney = tradeData.elementText("DEAL_AMT");

		if (gcsTradeMoney == null)
			gcsTradeMoney = "0";
		else if (gcsTradeMoney.trim().length() == 0)
			gcsTradeMoney = "0";

		/**
		 * 应计利息
		 * */
		String gcsInterest = "";
		TradeSubBean tradeSub = new TradeSubBean();
		tradeSub.setYssPub(this.getYssPub());
		tradeSub.setBargainDate(gcsBargainDate);
		tradeSub.setTradeAmount(Double.parseDouble(gcsTradeAmount));
		tradeSub.setSecurityCode(securityCode);
		try {
			tradeSub.parseRowStr(tradeSub.getOperValue("Interest"));
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("GCS计算国内场内证券买卖数据的利息出错：" + e.getMessage());
		}
		gcsInterest = "" + tradeSub.getAccruedInterest();

		// Hashtable<String, String> dictFeeCodes =
		// this.getMarket(groupAndPort[0], "Dict_Feecode_GCS");

		/* 各种费用和金额 */

		// 费用代码 费用金额
		Hashtable<String, String> htFees = new Hashtable<String, String>();

		/**
		 * 佣金
		 * */
		String comm_Fee = tradeData.elementText("COMM_FEE");

		if (comm_Fee == null)
			comm_Fee = "0";
		else if (comm_Fee.trim().length() == 0)
			comm_Fee = "0";

		String comm_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHYJ"
				: "YSS_SZYJ";
		htFees.put(comm_FeeCode, comm_Fee);

		String jsf_Fee = tradeData.elementText("JSF_FEE"); // 经手费金额

		if (jsf_Fee == null)
			jsf_Fee = "0";
		else if (jsf_Fee.trim().length() == 0)
			jsf_Fee = "0";

		String jsf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHJS"
				: "YSS_SZJS";
		htFees.put(jsf_FeeCode, jsf_Fee);

		String yhs_Fee = tradeData.elementText("YHS_FEE"); // 印花税金额

		if (yhs_Fee == null)
			yhs_Fee = "0";
		else if (yhs_Fee.trim().length() == 0)
			yhs_Fee = "0";

		String yhs_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHYH"
				: "YSS_SZYH";
		htFees.put(yhs_FeeCode, yhs_Fee);

		String zgf_Fee = tradeData.elementText("ZGF_FEE"); // 证管费金额

		if (zgf_Fee == null)
			zgf_Fee = "0";
		else if (zgf_Fee.trim().length() == 0)
			zgf_Fee = "0";

		String zgf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHZG"
				: "YSS_SZZG";
		htFees.put(zgf_FeeCode, zgf_Fee);

		String ghf_Fee = tradeData.elementText("GHF_FEE"); // 过户费金额

		if (ghf_Fee == null)
			ghf_Fee = "0";
		else if (ghf_Fee.trim().length() == 0)
			ghf_Fee = "0";

		String ghf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHGH"
				: "YSS_SZGH";
		htFees.put(ghf_FeeCode, ghf_Fee);

		String sxf_Fee = tradeData.elementText("SXF_FEE"); // 手续费金额

		if (sxf_Fee == null)
			sxf_Fee = "0";
		else if (sxf_Fee.trim().length() == 0)
			sxf_Fee = "0";

		String sxf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHJSF"
				: "YSS_SZJSF";

		String fxf_Fee = tradeData.elementText("FXJ_FEE"); // 风险金金额

		if (fxf_Fee == null)
			fxf_Fee = "0";
		else if (fxf_Fee.trim().length() == 0)
			fxf_Fee = "0";

		// 其它费
		String otherFee = ""
				+ YssD.add(Double.parseDouble(sxf_Fee), Double
						.parseDouble(fxf_Fee));
		htFees.put(sxf_FeeCode, otherFee);

		/**
		 * 交收金额
		 * */
		// String gcsTotalCost = tradeData.elementText("DELIV_AMT");
		String gcsTotalCost = getTotalCost(htFees, groupAndPort[0],
				securityCode, tradeType, gcsInterest, gcsTradeMoney);
		// if(gcsTotalCost == null || gcsTotalCost.trim().length() == 0)
		// return false;

		/**
		 * 交易券商 Dict_Brokercode_GCS
		 * */
		String gcsBrokerCode = tradeData.elementText("TRADER_ID");
		// if(gcsBrokerCode == null || gcsBrokerCode.trim().length() == 0)
		// return false;
		String brokerCode = "";

		Hashtable<String, String> dictBrokerCodes = this.getMarket(
				groupAndPort[0], "Dict_Brokercode_GCS");
		brokerCode = dictBrokerCodes.get(gcsBrokerCode);
		brokerCode = (brokerCode == null || brokerCode.trim().length() == 0) ? gcsBrokerCode
				: brokerCode;

		/**
		 * 交易席位代码 Dict_TradeSeat_GCS
		 **/
		String gcsSeatCode = tradeData.elementText("SEAT_NO");
		// if(gcsSeatCode == null || gcsSeatCode.trim().length() == 0)
		// return false;
		String seatCode = "";

		Hashtable<String, String> dictSeatCodes = this.getMarket(
				groupAndPort[0], "Dict_TradeSeat_GCS");
		seatCode = dictSeatCodes.get(gcsSeatCode);
		seatCode = (seatCode == null || seatCode.trim().length() == 0) ? gcsSeatCode
				: seatCode;

		/**
		 * 附言
		 * */
		String gcsPs = tradeData.elementText("PS");

		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		/**
		 * 不存在则直接新增
		 * */
		/*
		 * ,FFEECODE4,FTRADEFEE4,FFEECODE5,FTRADEFEE5,
		 * FFEECODE6,FTRADEFEE6,FFEECODE7,FTRADEFEE7,FFEECODE8,FTRADEFEE8
		 */
		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_Data_SubTrade "
				+ "(FNUM,FSECURITYCODE,FPORTCODE ,FBROKERCODE ,FINVMGRCODE,FTRADETYPECODE ,FCASHACCCODE,FATTRCLSCODE,"
				+ "FRATEDATE, FBARGAINDATE ,FBARGAINTIME,FSETTLEDATE ,FSETTLETIME ,FMATUREDATE,FMATURESETTLEDATE," +
				 " FFACTCASHACCCODE,"
				+ "FFACTSETTLEMONEY,FEXRATE,FFACTBASERATE,FFACTPORTRATE,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE," +
						"FALLOTPROPORTION,"
				+ "FOLDALLOTAMOUNT,FALLOTFACTOR,FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBAILMONEY,FFEECODE1,"
				+ "FTRADEFEE1,FFEECODE2,FTRADEFEE2,FFEECODE3,FTRADEFEE3,FFEECODE4,FTRADEFEE4,FFEECODE5,FTRADEFEE5," +
						"FFEECODE6,FTRADEFEE6,"
				+ "FTOTALCOST,"
				+ "FCOST,FMCOST,"
				+ "FVCOST,FBASECURYCOST,FMBASECURYCOST,FVBASECURYCOST,FPORTCURYCOST,FMPORTCURYCOST,"
				+ "FVPORTCURYCOST,FSETTLESTATE,FFACTSETTLEDATE,FSETTLEDESC,FORDERNUM,FDATASOURCE,FDATABIRTH,FSETTLEORGCODE,"
				+ "FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,"
				+ // FETFBALAACCTCODE,FETFBALASETTLEDATE,FETFBALAMONEY,FETFCASHALTERNAT,"
					// +
				"FSEATCODE,FSTOCKHOLDERCODE,FDS,"
				+ // FSPLITNUM ,FINVESTTYPE ,FDEALNUM
				"FSECURITYDELAYSETTLESTATE,FBROKERIDCODE,"
				+ 
				"FHANDCOSTSTATE,"
				+ 
				"FGCSNum) values(?,?,?,?,?,?,?,?,"
				+ // FMTREPLACEDATE
				"?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?," + "?,?,?," + "?,?,"
				+ "?," + "?)";
		try {
			pst = this.getConnection().prepareStatement(sql);
			pst.setString(1, ""
					+ this.getSubTradeNum(groupAndPort[0], gcsBargainDate,
							tradeType));// 交易编号
			pst.setString(2, securityCode); // 证券代码
			pst.setString(3, groupAndPort[1]); // 组合
			pst.setString(4, brokerCode); // 券商
			pst.setString(5, " "); // 投资经理
			pst.setString(6, tradeType); // 交易类型
			pst.setString(7, cashAcountCode); // 现金账户
			pst.setString(8, " "); // 所属分类
			pst.setDate(9, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 汇率日期
			pst.setDate(10, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 成交日期
			pst.setString(11, "00:00:00"); // 成交时间
			pst
					.setDate(12, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(13, "00:00:00"); // 结算时间
			pst.setDate(14, new java.sql.Date(this.toDate("9998-12-31")
					.getTime())); // 到期日期
			pst.setDate(15, new java.sql.Date(this.toDate("9998-12-31")
					.getTime())); // 到期结算日期
			pst.setString(16, cashAcountCode); // 实际结算账户
			pst.setDouble(17, Double.parseDouble(gcsTotalCost)); // 实际结算金额
			pst.setDouble(18, 1); // 兑换汇率
			pst.setDouble(19, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base")); // 实际基础汇率
			pst.setDouble(20, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setInt(21, 1); // 是否自动结算
			pst.setDouble(22, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setDouble(23, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base"));
			pst.setDouble(24, 100); // 分配比例
			pst.setDouble(25, Double.parseDouble(gcsTradeAmount)); // 原始分配数量
			pst.setDouble(26, 1); // 分配因子
			pst.setDouble(27, Double.parseDouble(gcsTradeAmount)); // 交易数量
			pst.setDouble(28, Double.parseDouble(gcsTradePrice)); // 交易价格
			pst.setDouble(29, Double.parseDouble(gcsTradeMoney)); // 交易金额
			pst.setDouble(30, Double.parseDouble(gcsInterest)); // 应收利息
			pst.setDouble(31, 0); // 保证金

			pst.setString(32, comm_FeeCode); // 费用代码1
			pst.setDouble(33, Double.parseDouble(comm_Fee)); // 费用1为 佣金
			pst.setString(34, jsf_FeeCode); // 费用代码2
			pst.setDouble(35, Double.parseDouble(jsf_Fee)); // 费用2 经手费金额
			pst.setString(36, yhs_FeeCode); // 费用3
			pst.setDouble(37, Double.parseDouble(yhs_Fee)); // 印花税金额
			pst.setString(38, zgf_FeeCode); // 证管费金额
			pst.setDouble(39, Double.parseDouble(zgf_Fee));
			pst.setString(40, ghf_FeeCode); // 过户费金额
			pst.setDouble(41, Double.parseDouble(ghf_Fee));
			pst.setString(42, sxf_FeeCode);// 其它费用 手续费金额 + 风险金金额
			pst.setDouble(43, Double.parseDouble(otherFee));
			pst.setDouble(44, Double.parseDouble(gcsTotalCost)); // 交收金额
			pst.setDouble(45, 0);
			pst.setDouble(46, 0);
			pst.setDouble(47, 0);
			pst.setDouble(48, 0);
			pst.setDouble(49, 0);
			pst.setDouble(50, 0);
			pst.setDouble(51, 0);
			pst.setDouble(52, 0);
			pst.setDouble(53, 0);
			pst.setInt(54, 0); // 结算状态
			pst
					.setDate(55, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(56, " "); // 结算描述
			pst.setString(57, " "); // 订单编号
			pst.setInt(58, 1); // 数据来源
			pst.setString(59, " "); // 交易来源
			pst.setString(60, " "); // 结算机构代码
			pst.setString(61, gcsPs); // 描述
			pst.setInt(62, 1); // 审核状态
			pst.setString(63, "GCS"); // 创建人
			pst.setString(64, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 创建时间
			pst.setString(65, "GCS"); // 审核人
			pst.setString(66, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 审核时间
			pst.setString(67, seatCode); // 席位代码
			pst.setString(68, " "); // 股东代码
			pst.setString(69, "ZD_JK"); // 操作类型 //代表接口读入
			pst.setInt(70, 0); // 延迟交割标识
			pst.setString(71, gcsBrokerCode); // 券商代码
			pst.setInt(72, 0); // 手动修改成本标志
			pst.setString(73, GCSNum); // GCSnum

			pst.executeUpdate();
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("处理GCS国内场内证券买卖数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理GCS证券买卖数据出错：" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 国内场内回购数据处理
	 * */
	private boolean dealChinaFloorPurchase(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		String sql = "";
		PreparedStatement pst = null;
		boolean result = true;
		/**
		 * 填塞组合群前缀
		 * */

		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 * 证券收付标识 = 交易类型
		 * */
		String gcsSecurityIdenti = tradeData.elementText("SECU_DIRE");
		if (gcsSecurityIdenti == null || gcsSecurityIdenti.trim().length() == 0)
			return false;

		// 交易类型
		String tmpTradeType = tradeData.elementText("TRANS_TYPE");
		if (tmpTradeType == null || tmpTradeType.trim().length() == 0)
			return false;

		String tradeType = "";

		if (this.HtGNCNZQBUY.containsKey(tmpTradeType))
			tradeType = this.HtGNCNZQBUY.get(tmpTradeType);
		else if (this.HtGNCNZQSELL.containsKey(tmpTradeType))
			tradeType = this.HtGNCNZQSELL.get(tmpTradeType);

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 交收日期
		 * */
		String gcsJSDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsJSDate == null || gcsJSDate.trim().length() == 0)
			return false;

		/**
		 * 到期日期
		 * */
		Date matureDate = this.getMatureAddDate(gcsBargainDate, securityCode,
				"maturadate");

		/**
		 * 到期结算日
		 * */
		Date matureSettleDate = this.getMatureAddDate(gcsBargainDate,
				securityCode, "maturaSettledate");

		/**
		 * 成交价格
		 * */
		String gcsTradePrice = tradeData.elementText("DEAL_PRICE");

		if (gcsTradePrice == null)
			gcsTradePrice = "0";
		else if (gcsTradePrice.trim().length() == 0)
			gcsTradePrice = "0";

		/**
		 * 成交数量
		 * */
		String gcsTradeAmount = tradeData.elementText("DEAL_QUAN");

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		else if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		/**
		 * 成交金额
		 * */
		String gcsTradeMoney = tradeData.elementText("DEAL_AMT");

		if (gcsTradeMoney == null)
			gcsTradeMoney = "0";
		else if (gcsTradeMoney.trim().length() == 0)
			gcsTradeMoney = "0";

		/**
		 * 应计利息 回购到期金额
		 * */
		String gcsInterest = tradeData.elementText("REPO_MATU_AMT");

		if (gcsInterest == null)
			gcsInterest = "0";
		else if (gcsInterest.trim().length() == 0)
			gcsInterest = "0";

		// 应计利息 = 到期回购金额 - 成交金额
		gcsInterest = ""
				+ YssD.sub(Double.parseDouble(gcsInterest), Double
						.parseDouble(gcsTradeMoney));

		/* 各种费用及金额 */

		// 费用代码 费用金额
		Hashtable<String, String> htFees = new Hashtable<String, String>();

		/**
		 * 佣金
		 * */
		String comm_Fee = tradeData.elementText("COMM_FEE");

		if (comm_Fee == null)
			comm_Fee = "0";
		else if (comm_Fee.trim().length() == 0)
			comm_Fee = "0";

		String comm_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHYJ"
				: "YSS_SZYJ";

		htFees.put(comm_FeeCode, comm_Fee);

		String jsf_Fee = tradeData.elementText("JSF_FEE"); // 经手费金额

		if (jsf_Fee == null)
			jsf_Fee = "0";
		else if (jsf_Fee.trim().length() == 0)
			jsf_Fee = "0";

		String jsf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHJS"
				: "YSS_SZJS";

		htFees.put(jsf_FeeCode, jsf_Fee);

		String yhs_Fee = tradeData.elementText("YHS_FEE"); // 印花税金额

		if (yhs_Fee == null)
			yhs_Fee = "0";
		else if (yhs_Fee.trim().length() == 0)
			yhs_Fee = "0";

		String yhs_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHYH"
				: "YSS_SZYH";
		htFees.put(yhs_FeeCode, yhs_Fee);

		String zgf_Fee = tradeData.elementText("ZGF_FEE"); // 证管费金额

		if (zgf_Fee == null)
			zgf_Fee = "0";
		else if (zgf_Fee.trim().length() == 0)
			zgf_Fee = "0";

		String zgf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHZG"
				: "YSS_SZZG";
		htFees.put(zgf_FeeCode, zgf_Fee);

		String ghf_Fee = tradeData.elementText("GHF_FEE"); // 过户费金额

		if (ghf_Fee == null)
			ghf_Fee = "0";
		else if (ghf_Fee.trim().length() == 0)
			ghf_Fee = "0";

		String ghf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHGH"
				: "YSS_SZGH";
		htFees.put(ghf_FeeCode, ghf_Fee);

		String sxf_Fee = tradeData.elementText("SXF_FEE"); // 手续费金额

		if (sxf_Fee == null)
			sxf_Fee = "0";
		else if (sxf_Fee.trim().length() == 0)
			sxf_Fee = "0";

		String sxf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHJSF"
				: "YSS_SZJSF";

		String fxf_Fee = tradeData.elementText("FXJ_FEE"); // 风险金金额

		if (fxf_Fee == null)
			fxf_Fee = "0";
		else if (fxf_Fee.trim().length() == 0)
			fxf_Fee = "0";

		// 其它费
		String otherFee = ""
				+ YssD.add(Double.parseDouble(sxf_Fee), Double
						.parseDouble(fxf_Fee));
		htFees.put(sxf_FeeCode, otherFee);

		/**
		 * 交收金额 即实收实付
		 * */

		String gcsTotalCost = getTotalCost(htFees, groupAndPort[0],
				securityCode, tradeType, gcsInterest, gcsTradeMoney);

		/**
		 * 交易券商 Dict_Brokercode_GCS
		 * */
		String gcsBrokerCode = tradeData.elementText("TRADER_ID");
		// if(gcsBrokerCode == null || gcsBrokerCode.trim().length() == 0)
		// return false;
		String brokerCode = "";

		Hashtable<String, String> dictBrokerCodes = this.getMarket(
				groupAndPort[0], "Dict_Brokercode_GCS");
		brokerCode = dictBrokerCodes.get(gcsBrokerCode);
		brokerCode = (brokerCode == null || brokerCode.trim().length() == 0) ? gcsBrokerCode
				: brokerCode;

		/**
		 * 交易席位代码 Dict_TradeSeat_GCS
		 **/
		String gcsSeatCode = tradeData.elementText("SEAT_NO");
		// if(gcsSeatCode == null || gcsSeatCode.trim().length() == 0)
		// return false;
		String seatCode = "";

		Hashtable<String, String> dictSeatCodes = this.getMarket(
				groupAndPort[0], "Dict_TradeSeat_GCS");
		seatCode = dictSeatCodes.get(gcsSeatCode);
		seatCode = (seatCode == null || seatCode.trim().length() == 0) ? gcsSeatCode
				: seatCode;

		/**
		 * 附言
		 * */
		String gcsPs = tradeData.elementText("PS");

		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		/**
		 * 不存在则直接新增
		 * */
		/*
		 * ,FFEECODE4,FTRADEFEE4,FFEECODE5,FTRADEFEE5,
		 * FFEECODE6,FTRADEFEE6,FFEECODE7,FTRADEFEE7,FFEECODE8,FTRADEFEE8
		 */
		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_Data_SubTrade "
				+ "(FNUM,FSECURITYCODE,FPORTCODE ,FBROKERCODE ,FINVMGRCODE,FTRADETYPECODE ,FCASHACCCODE,FATTRCLSCODE,"
				+ "FRATEDATE, FBARGAINDATE ,FBARGAINTIME,FSETTLEDATE ,FSETTLETIME ,FMATUREDATE,FMATURESETTLEDATE," +
				 "FFACTCASHACCCODE,"
				+ "FFACTSETTLEMONEY,FEXRATE,FFACTBASERATE,FFACTPORTRATE,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE," +
				 "FALLOTPROPORTION,"
				+ "FOLDALLOTAMOUNT,FALLOTFACTOR,FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBAILMONEY," +
				 "FFEECODE1,"
				+ "FTRADEFEE1,FFEECODE2,FTRADEFEE2,FFEECODE3,FTRADEFEE3,FFEECODE4,FTRADEFEE4,FFEECODE5,FTRADEFEE5," +
				 "FFEECODE6,FTRADEFEE6,"
				+ "FTOTALCOST,"
				+ "FCOST,FMCOST,"
				+ "FVCOST,FBASECURYCOST,FMBASECURYCOST,FVBASECURYCOST,FPORTCURYCOST,FMPORTCURYCOST,"
				+ "FVPORTCURYCOST,FSETTLESTATE,FFACTSETTLEDATE,FSETTLEDESC,FORDERNUM,FDATASOURCE,FDATABIRTH,FSETTLEORGCODE,"
				+ "FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,"
				+ 
				"FSEATCODE,FSTOCKHOLDERCODE,FDS," + 
				"FSECURITYDELAYSETTLESTATE,FBROKERIDCODE,"
				+ 
				"FHANDCOSTSTATE," + 
				"FGCSNum) values(?,?,?,?,?,?,?,?," + 
				"?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?," + "?,?,?," + "?,?,"
				+ "?," + "?)";
		try {
			pst = this.getConnection().prepareStatement(sql);
			pst.setString(1, ""
					+ this.getSubTradeNum(groupAndPort[0], gcsBargainDate,
							tradeType));// 交易编号
			pst.setString(2, securityCode); // 证券代码
			pst.setString(3, groupAndPort[1]); // 组合
			pst.setString(4, brokerCode); // 券商
			pst.setString(5, " "); // 投资经理
			pst.setString(6, tradeType); // 交易类型
			pst.setString(7, cashAcountCode); // 现金账户
			pst.setString(8, " "); // 所属分类
			pst.setDate(9, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 汇率日期
			pst.setDate(10, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 成交日期
			pst.setString(11, "00:00:00"); // 成交时间
			pst
					.setDate(12, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(13, "00:00:00"); // 结算时间
			pst.setDate(14, new java.sql.Date(matureDate.getTime())); // 到期日期
			pst.setDate(15, new java.sql.Date(matureSettleDate.getTime())); // 到期结算日期
			pst.setString(16, cashAcountCode); // 实际结算账户
			pst.setDouble(17, Double.parseDouble(gcsTotalCost)); // 实际结算金额
			pst.setDouble(18, 1); // 兑换汇率
			pst.setDouble(19, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base")); // 实际基础汇率
			pst.setDouble(20, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setInt(21, 1); // 是否自动结算
			pst.setDouble(22, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setDouble(23, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base"));
			pst.setDouble(24, 100); // 分配比例
			pst.setDouble(25, Double.parseDouble(gcsTradeAmount)); // 原始分配数量
			pst.setDouble(26, 1); // 分配因子
			pst.setDouble(27, Double.parseDouble(gcsTradeAmount)); // 交易数量
			pst.setDouble(28, Double.parseDouble(gcsTradePrice)); // 交易价格
			pst.setDouble(29, Double.parseDouble(gcsTradeMoney)); // 交易金额
			pst.setDouble(30, Double.parseDouble(gcsInterest)); // 应收利息
			pst.setDouble(31, 0); // 保证金

			pst.setString(32, comm_FeeCode); // 费用代码1
			pst.setDouble(33, Double.parseDouble(comm_Fee)); // 费用1为 佣金
			pst.setString(34, jsf_FeeCode); // 费用代码2
			pst.setDouble(35, Double.parseDouble(jsf_Fee)); // 费用2 经手费金额
			pst.setString(36, yhs_FeeCode); // 费用3
			pst.setDouble(37, Double.parseDouble(yhs_Fee)); // 印花税金额
			pst.setString(38, zgf_FeeCode); // 证管费金额
			pst.setDouble(39, Double.parseDouble(zgf_Fee));
			pst.setString(40, ghf_FeeCode); // 过户费金额
			pst.setDouble(41, Double.parseDouble(ghf_Fee));
			pst.setString(42, sxf_FeeCode);// 其它费用 手续费金额 + 风险金金额
			pst.setDouble(43, Double.parseDouble(otherFee));
			pst.setDouble(44, Double.parseDouble(gcsTotalCost)); // 交收金额
			pst.setDouble(45, 0);
			pst.setDouble(46, 0);
			pst.setDouble(47, 0);
			pst.setDouble(48, 0);
			pst.setDouble(49, 0);
			pst.setDouble(50, 0);
			pst.setDouble(51, 0);
			pst.setDouble(52, 0);
			pst.setDouble(53, 0);
			pst.setInt(54, 0); // 结算状态
			pst
					.setDate(55, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(56, " "); // 结算描述
			pst.setString(57, " "); // 订单编号
			pst.setInt(58, 1); // 数据来源
			pst.setString(59, " "); // 交易来源
			pst.setString(60, " "); // 结算机构代码
			pst.setString(61, gcsPs); // 描述
			pst.setInt(62, 1); // 审核状态
			pst.setString(63, "GCS"); // 创建人
			pst.setString(64, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 创建时间
			pst.setString(65, "GCS"); // 审核人
			pst.setString(66, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 审核时间
			pst.setString(67, seatCode); // 席位代码
			pst.setString(68, " "); // 股东代码
			pst.setString(69, "ZD_JK"); // 操作类型 //代表接口读入
			pst.setInt(70, 0); // 延迟交割标识
			pst.setString(71, gcsBrokerCode); // 券商代码
			pst.setInt(72, 0); // 手动修改成本标志
			pst.setString(73, GCSNum); // GCSnum

			pst.executeUpdate();
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("处理GCS国内场内证券买卖数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理GCS证券买卖数据出错：" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 银行间债券回购
	 * */
	private boolean dealBanksPurchaseBuyBack(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		PreparedStatement pst = null;
		String sql = "";
		boolean result = true;
		/**
		 * 填塞组合群前缀
		 * */
		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		// 交易类型
		String tmpTradeType = tradeData.elementText("TRANS_TYPE");
		if (tmpTradeType == null || tmpTradeType.trim().length() == 0)
			return false;

		String tradeType = "";

		if ("3".equalsIgnoreCase(tmpTradeType)
				|| "5".equalsIgnoreCase(tmpTradeType))
			tradeType = "24";
		if ("4".equalsIgnoreCase(tmpTradeType)
				|| "6".equalsIgnoreCase(tmpTradeType))
			tradeType = "25";

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 交收日期
		 * */
		String gcsJSDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsJSDate == null || gcsJSDate.trim().length() == 0)
			return false;

		/**
		 * 回购到期日期
		 * */
		Date matureDate = this.getMatureAddDate(gcsBargainDate, securityCode,
				"maturadate");
		if (matureDate == null)
			return false;

		/**
		 * 回购到期结算日期
		 * */
		String matureSettleDate = tradeData.elementText("REPO_MATU_DATE");

		/**
		 * 成交金额
		 * */
		String gcsTradeMoney = tradeData.elementText("DEAL_AMT");

		if (gcsTradeMoney == null)
			gcsTradeMoney = "0";
		else if (gcsTradeMoney.trim().length() == 0)
			gcsTradeMoney = "0";

		/**
		 * 手续费
		 * */
		String sxFee = tradeData.elementText("SXF_FEE");

		if (sxFee == null)
			sxFee = "0";
		else if (sxFee.trim().length() == 0)
			sxFee = "0";

		/**
		 * 其他费用
		 * */
		String otherFee = tradeData.elementText("OTHER_FEE");

		if (otherFee == null)
			otherFee = "0";
		else if (otherFee.trim().length() == 0)
			otherFee = "0";

		/**
		 * 回购到期金额 ——> 用来计算回购收益
		 * */
		String gcsInterest = tradeData.elementText("REPO_MATU_AMT");

		if (gcsInterest == null)
			gcsInterest = "0";
		else if (gcsInterest.trim().length() == 0)
			gcsInterest = "0";

		// 应计利息 = 到期回购金额 - 成交金额
		gcsInterest = ""
				+ YssD
						.add(YssD.sub(Double.parseDouble(gcsInterest), Double
								.parseDouble(gcsTradeMoney)), Double
								.parseDouble(sxFee), Double
								.parseDouble(otherFee));

		/**
		 * 回购质押券数量（几只券）
		 * */
		String gcsTradeAmount = tradeData.elementText("REPO_MATU_CNT");

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		else if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		gcsTradeAmount = Double.parseDouble(gcsTradeAmount) == 1 ? "0" : "1";

		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_DATA_PURCHASE tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 回购关联表
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_data_purchaserela tctf "
								+ " where tctf.FNum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_data_purchaserela where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_DATA_PURCHASE where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_DATA_PURCHASE tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_data_purchaserela tctf "
								+ " where tctf.FNum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_data_purchaserela where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_DATA_PURCHASE where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_Data_Purchase (FNUM,FSECURITYCODE,FPORTCODE,FINVMGRCODE,FTRADETYPECODE,"
				+ "FCASHACCCODE,FBARGAINDATE,FBARGAINTIME,FSETTLEDATE,FMATUREDATE,FMATURESETTLEDATE,FPORTCURYRATE," +
				"FBASECURYRATE,"
				+ "FAFFCORPCODE,FTRADEMONEY,FPURCHASEGAIN,FTRADEHANDLEFEE,FBANKHANDLEFEE,FSETSERVICEFEE,FTOTALCOST," +
				"FDESC,FCHECKSTATE,"
				+ "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,FFIXNUM,FGCSNum"
				+ // FIMPNUM,FSECISSUERCODE
				") values(?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?)";

		try {
			pst = this.getConnection().prepareStatement(sql);
			String Num = this.getBanksPurchaseNum(groupAndPort[0],
					gcsBargainDate);
			pst.setString(1, "" + Num);// 交易编号
			pst.setString(2, securityCode); // 证券代码
			pst.setString(3, groupAndPort[1]); // 组合
			pst.setString(4, " "); // 投资经理
			pst.setString(5, tradeType); // 交易类型
			pst.setString(6, cashAcountCode); // 现金账户
			pst.setDate(7, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 成交日期
			pst.setString(8, "00:00:00"); // 成交时间
			pst.setDate(9, new java.sql.Date(this.toDate(gcsJSDate).getTime())); // 结算日期
			pst.setDate(10, new java.sql.Date(matureDate.getTime())); // 到期日期
			pst.setDate(11, new java.sql.Date(this.toDate(matureSettleDate)
					.getTime())); // 到期结算日期
			pst.setDouble(12, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setDouble(13, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base"));
			pst.setString(14, " "); // 关联交易方
			pst.setDouble(15, Double.parseDouble(gcsTradeMoney)); // 成交金额
			pst.setDouble(16, Double.parseDouble(gcsInterest)); // 回购收益
			pst.setDouble(17, Double.parseDouble(sxFee)); // 交易手续费
			pst.setDouble(18, Double.parseDouble("0")); // 银行手续费
			pst.setDouble(19, Double.parseDouble(otherFee)); // 结算服务费
			pst.setDouble(20, Double.parseDouble(gcsTradeMoney)); // 实收实付金额
			pst.setString(21, " ");
			pst.setInt(22, 1); // 审核状态
			pst.setString(23, "GCS"); // 创建人
			pst.setString(24, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 创建时间
			pst.setString(25, "GCS"); // 审核人
			pst.setString(26, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 审核时间
			pst.setString(27, gcsTradeAmount); // 抵用券数
			pst.setString(28, GCSNum);
			pst.executeUpdate();

			/**
			 * 回购冻结债券
			 * */
			List<Element> list = tradeData.elements("circle");

			if (list != null && list.size() > 0) {
				pst.close();
				sql = "insert into tb_"
						+ groupAndPort[0]
						+ "_data_purchaserela (FNUM,FSECURITYCODE,FPORTCODE,FDATE,FFREEZEAMOUNT) "
						+ " values (?,?,?,?,?)";
				pst = this.getConnection().prepareStatement(sql);
				for (int i = 0; i < list.size(); i++) {
					Element element = list.get(i);
					markCode = element.elementText("reposecuno");
					String secuQuan = element.elementText("secuQuan");
					securityCode = this.getDatabySql("select * from tb_"
							+ groupAndPort[0]
							+ "_para_security "
							+ " where FCheckState = 1 and FMarketCode = "
							+ this.getYssPub().getDbLink().sqlString(markCode)
							+ " and FExchangeCode = "
							+ this.getYssPub().getDbLink().sqlString(
									qdExchangeCode), "FSECURITYCODE");
					pst.setString(1, Num); // 关联主表编号
					pst.setString(2, securityCode); // 证券代码
					pst.setString(3, groupAndPort[1]);
					pst.setDate(4, new java.sql.Date(this
							.toDate(gcsBargainDate).getTime())); // 成交日期
					pst.setDouble(5, Double.parseDouble(secuQuan));
					pst.addBatch();
				}
				pst.executeBatch();
			}
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("处理GCS国内场内证券买卖数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 开放式基金业务
	 * */
	private boolean dealOpenFundTrade(String[] groupAndPort, Element tradeData,
			String gcsAssetID, String state, String GCSNum) {
		boolean result = true;
		// 交易类型
		String tmpTradeType = tradeData.elementText("TRANS_TYPE");
		if (tmpTradeType == null || tmpTradeType.trim().length() == 0)
			return false;

		if ("11".equalsIgnoreCase(tmpTradeType)
				|| "12".equalsIgnoreCase(tmpTradeType)) { // 申购认购
			if ("11".equalsIgnoreCase(state)) { // 首期交收成功
				return this.dealOpenFundTrade11(groupAndPort, tradeData,
						gcsAssetID, state, GCSNum);
			} else if ("10".equalsIgnoreCase(state)) { // 交收成功
				return this.dealOpenFundTrade10(groupAndPort, tradeData,
						gcsAssetID, state, GCSNum);
			} else if ("13".equalsIgnoreCase(state)) { // 注销
				return this.dealOpenFundTradeDelete(groupAndPort, tradeData,
						gcsAssetID, state, GCSNum, "");
			}
		} else if ("13".equalsIgnoreCase(tmpTradeType)) {
			if ("10".equalsIgnoreCase(state)) {
				return this.dealOpenFundTradeEdem(groupAndPort, tradeData,
						gcsAssetID, state, GCSNum);
			} else if ("13".equalsIgnoreCase(state)) {
				return this.dealOpenFundTradeDelete(groupAndPort, tradeData,
						gcsAssetID, state, GCSNum, "");
			}
		}
		return result;
	}

	/**
	 * 处理开放式基金赎回 处理 GCS状态为 11. 首期交收成功
	 * */
	private boolean dealOpenFundTrade11(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		PreparedStatement pst = null;
		String sql = "";
		boolean result = true;

		/**
		 * 填塞组合群前缀
		 * */
		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		// 交易类型
		String tmpTradeType = tradeData.elementText("TRANS_TYPE");
		if (tmpTradeType == null || tmpTradeType.trim().length() == 0)
			return false;

		String tradeType = "";

		if ("11".equalsIgnoreCase(tmpTradeType)) // 认购
			tradeType = "04";
		else if ("12".equalsIgnoreCase(tmpTradeType)) // 申购
			tradeType = "15";
		else if ("13".equalsIgnoreCase(tmpTradeType)) // 赎回
			tradeType = "16";

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期 / 申请日期 / 确认日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 成交金额
		 * */
		String gcsTradeMoney = tradeData.elementText("DEAL_AMT");

		if (gcsTradeMoney == null)
			gcsTradeMoney = "0";
		else if (gcsTradeMoney.trim().length() == 0)
			gcsTradeMoney = "0";

		if ("04".equalsIgnoreCase(tradeType)
				|| "15".equalsIgnoreCase(tradeType)) { // 认购 或 申购
			try {
				if ("11".equalsIgnoreCase(state)) { // 11. 首期交收成功
					String tradeNums = this
							.getDatabySql(
									"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
											+ groupAndPort[0]
											+ "_DATA_OPENFUNDTRADE tdst "
											+ " where FDataType in('apply') and FGCSNum = "
											+ this.getYssPub().getDbLink()
													.sqlString(GCSNum), "FNUM");

					if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
						this.deleteOpenFundData(groupAndPort[0], tradeNums,
								this.toDate(gcsBargainDate), this
										.toDate(gcsBargainDate), securityCode,
								groupAndPort[1], "", tradeType, false, "",
								"apply");
					}

					sql = "insert into TB_"
							+ groupAndPort[0]
							+ "_DATA_OPENFUNDTRADE ("
							+ " FNUM,FDATATYPE,FTRADETYPECODE,FPORTCODE,FSECURITYCODE,FBARGAINDATE,FINVESTTYPE," +
							"FINVMGRCODE,FAPPLYDATE,FAPPLYCASHACCCODE,"
							+ " FAPPLYMONEY,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,FGCSNum"
							+ ") values (?,?,?,?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,?,?" + ")";
					pst = this.getConnection().prepareStatement(sql);

					pst.setString(1, this.getOpenFundTradeNum(groupAndPort[0],
							gcsBargainDate)); // 开放式基金交易数据编号
					pst.setString(2, "apply"); // 数据类型 为申请数据
					pst.setString(3, tradeType); // 交易类型
					pst.setString(4, groupAndPort[1]); // 组合
					pst.setString(5, securityCode); // 证券代码
					pst.setDate(6, new java.sql.Date(this
							.toDate(gcsBargainDate).getTime())); // 成交日期
					pst.setString(7, "C"); // 投资类型
					pst.setString(8, " "); // 投资经理代码
					pst.setDate(9, new java.sql.Date(this
							.toDate(gcsBargainDate).getTime())); // 申请日期
					pst.setString(10, cashAcountCode); // 申请帐户
					pst.setDouble(11, Double.parseDouble(gcsTradeMoney)); // 申请金额
					pst.setInt(12, 1); // 审核状态
					pst.setString(13, "GCS"); // 创建人
					pst.setString(14, this.formatDate(new Date(),
							"yyyyMMdd HH:mm:ss"));// 创建时间
					pst.setString(15, "GCS"); // 审核人
					pst.setString(16, this.formatDate(new Date(),
							"yyyyMMdd HH:mm:ss"));// 审核时间
					pst.setString(17, GCSNum);
					pst.executeUpdate();
				}
			} catch (Exception e) {
				this.setReplyCode("1");// 设置处理结果标识
				System.out.println("生成开放式基金认购/申购业务数据或删除相关联数据时出错："
						+ e.getMessage());
			} finally {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 处理开放式基金赎回 处理 GCS状态为 10. 交收成功 13.注销
	 * */
	private boolean dealOpenFundTrade10(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		PreparedStatement pst = null;
		String sql = "";
		boolean result = true;

		/**
		 * 填塞组合群前缀
		 * */
		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		// 交易类型
		String tmpTradeType = tradeData.elementText("TRANS_TYPE");
		if (tmpTradeType == null || tmpTradeType.trim().length() == 0)
			return false;

		String tradeType = "";

		if ("11".equalsIgnoreCase(tmpTradeType)) // 认购
			tradeType = "04";
		else if ("12".equalsIgnoreCase(tmpTradeType)) // 申购
			tradeType = "15";
		else if ("13".equalsIgnoreCase(tmpTradeType)) // 赎回
			tradeType = "16";

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;

		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期 / 申请日期 / 确认日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 返款日期
		 * */
		String gcsReturnDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsReturnDate == null || gcsReturnDate.trim().length() == 0)
			return false;

		/**
		 * 实际交收数量
		 * */
		String gcsTradeAmount = tradeData.elementText("ACTU_DELIV_QUAN");

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		/**
		 * 其它费用
		 * */
		String gcsOtherFee = tradeData.elementText("OTHER_FEE");

		if (gcsOtherFee == null)
			gcsOtherFee = "0";
		if (gcsOtherFee.trim().length() == 0)
			gcsOtherFee = "0";

		/**
		 * 实际交收金额
		 * */
		String gcsFactTradeMoney = tradeData.elementText("ACTU_DELIV_AMT");

		if (gcsFactTradeMoney == null)
			gcsFactTradeMoney = "0";
		if (gcsFactTradeMoney.trim().length() == 0)
			gcsFactTradeMoney = "0";

		/**
		 * 认购 或 申购
		 * */
		if ("04".equalsIgnoreCase(tradeType)
				|| "15".equalsIgnoreCase(tradeType)) {
			try {
				if ("10".equalsIgnoreCase(state)) { // 10. 交收成功

					/**
					 * 查找是否有对应的申请数据
					 * */
					String applyDatas = this.getDatasbySql(" select * from TB_"
							+ groupAndPort[0] + "_DATA_OPENFUNDTRADE "
							+ " where FDataType = 'apply' and FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
							"FNum,FGCSNum,FBARGAINDATE,FAPPLYMONEY");
					if (applyDatas != null && applyDatas.trim().length() > 0) {
						String[] applyData = applyDatas.split(",");

						/**
						 * 删除存在的确认数据
						 * */
						String tradeNums = this
								.getDatabySql(
										"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
												+ groupAndPort[0]
												+ "_DATA_OPENFUNDTRADE tdst "
												+ " where FDataType in('return' , 'confirm') and FGCSNum = "
												+ this.getYssPub().getDbLink()
														.sqlString(GCSNum),
										"FNUM");

						if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
							this.deleteOpenFundData(groupAndPort[0], tradeNums,
									this.toDate(applyData[2].substring(0, 10)),
									this.toDate(applyData[2].substring(0, 10)),
									securityCode, groupAndPort[1], "",
									tradeType, false, "", "confirm,return");
						}

						sql = "insert into TB_"
								+ groupAndPort[0]
								+ "_DATA_OPENFUNDTRADE ("
								+ " FNUM,FDATATYPE,FTRADETYPECODE,FPORTCODE,FSECURITYCODE,FBARGAINDATE,FINVESTTYPE," +
								"FINVMGRCODE,FComfDate,"
								+ " FComfMoney,FComfAmount,FComfFee,FTradePrice,FCHECKSTATE,FCREATOR,FCREATETIME," +
								"FCHECKUSER,FCHECKTIME,FGCSNum"
								+ ") values (?,?,?,?,?,?,?,?,?,"
								+ "?,?,?,?,?,?,?,?,?,?" + ")";
						// String num =
						// this.getOpenFundTradeNum(groupAndPort[0],
						// gcsBargainDate);

						pst = this.getConnection().prepareStatement(sql);
						pst.setString(1, applyData[0]); // 开放式基金交易数据编号
						pst.setString(2, "confirm"); // 数据类型 为申请数据
						pst.setString(3, tradeType); // 交易类型
						pst.setString(4, groupAndPort[1]); // 组合
						pst.setString(5, securityCode); // 证券代码
						pst.setDate(6, new java.sql.Date(this.toDate(
								applyData[2].substring(0, 10)).getTime())); // 成交日期
						pst.setString(7, "C"); // 投资类型
						pst.setString(8, " "); // 投资经理代码
						pst.setDate(9, new java.sql.Date(this.toDate(
								gcsBargainDate).getTime())); // 确认日期
						pst.setDouble(10, YssD.sub(Double
								.parseDouble(gcsFactTradeMoney), Double
								.parseDouble(gcsOtherFee))); // 确认金额 = 实际交收金额 -
																// 其它费用
						pst.setDouble(11, Double.parseDouble(gcsTradeAmount));
						pst.setDouble(12, Double.parseDouble(gcsOtherFee)); // 确认交易费用
						pst.setDouble(13, YssD.div(YssD.sub(Double
								.parseDouble(gcsFactTradeMoney), Double
								.parseDouble(gcsOtherFee)), Double
								.parseDouble(gcsTradeAmount))); // 确认价格 = 确认金额 /
																// 交收数量
						pst.setInt(14, 1); // 审核状态
						pst.setString(15, "GCS"); // 创建人
						pst.setString(16, this.formatDate(new Date(),
								"yyyyMMdd HH:mm:ss"));// 创建时间
						pst.setString(17, "GCS"); // 审核人
						pst.setString(18, this.formatDate(new Date(),
								"yyyyMMdd HH:mm:ss"));// 审核时间
						pst.setString(19, GCSNum);

						pst.executeUpdate();

						sql = "insert into TB_"
								+ groupAndPort[0]
								+ "_DATA_OPENFUNDTRADE ("
								+ " FNUM,FDATATYPE,FTRADETYPECODE,FPORTCODE,FSECURITYCODE,FBARGAINDATE,FINVESTTYPE," +
								"FINVMGRCODE,FReturnDate,FRtnCashAccCode,"
								+ " FReturnFee,FReturnMoney,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,FGCSNum"
								+ ") values (?,?,?,?,?,?,?,?,?,"
								+ "?,?,?,?,?,?,?,?,?" + ")";

						pst = this.getConnection().prepareStatement(sql);
						pst.setString(1, applyData[0]); // 开放式基金交易数据编号
						pst.setString(2, "return"); // 数据类型 为申请数据
						pst.setString(3, tradeType); // 交易类型
						pst.setString(4, groupAndPort[1]); // 组合
						pst.setString(5, securityCode); // 证券代码
						pst.setDate(6, new java.sql.Date(this.toDate(
								applyData[2].substring(0, 10)).getTime())); // 成交日期
						pst.setString(7, "C"); // 投资类型
						pst.setString(8, " "); // 投资经理代码
						pst.setDate(9, new java.sql.Date(this.toDate(
								gcsReturnDate).getTime())); // 返款日期
						pst.setString(10, cashAcountCode); // 返还账户
						pst.setDouble(11, 0); // 退还费用
						pst.setDouble(12, YssD.sub(Double
								.parseDouble(applyData[3]), Double
								.parseDouble(gcsFactTradeMoney))); // 退换金额= 申请金额
																	// - 实际交收金额
						pst.setInt(13, 1); // 审核状态
						pst.setString(14, "GCS"); // 创建人
						pst.setString(15, this.formatDate(new Date(),
								"yyyyMMdd HH:mm:ss"));// 创建时间
						pst.setString(16, "GCS"); // 审核人
						pst.setString(17, this.formatDate(new Date(),
								"yyyyMMdd HH:mm:ss"));// 审核时间
						pst.setString(18, GCSNum);

						pst.executeUpdate();
					}
				}
			} catch (Exception e) {
				this.setReplyCode("1");// 设置处理结果标识
				System.out.println("生成开放式基金认购/申购业务数据或删除相关联数据时出错："
						+ e.getMessage());
			} finally {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 处理开放式基金 赎回 10. 交收成功
	 * */
	private boolean dealOpenFundTradeEdem(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		PreparedStatement pst = null;
		String sql = "";
		boolean result = true;

		/**
		 * 填塞组合群前缀
		 * */
		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		// 交易类型
		String tmpTradeType = tradeData.elementText("TRANS_TYPE");
		if (tmpTradeType == null || tmpTradeType.trim().length() == 0)
			return false;

		String tradeType = "";

		if ("11".equalsIgnoreCase(tmpTradeType)) // 认购
			tradeType = "04";
		else if ("12".equalsIgnoreCase(tmpTradeType)) // 申购
			tradeType = "15";
		else if ("13".equalsIgnoreCase(tmpTradeType)) // 赎回
			tradeType = "16";

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;

		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期 / 申请日期 / 确认日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 返款日期
		 * */
		String gcsReturnDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsReturnDate == null || gcsReturnDate.trim().length() == 0)
			return false;

		/**
		 * 实际交收数量
		 * */
		String gcsTradeAmount = tradeData.elementText("ACTU_DELIV_QUAN");

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		/**
		 * 其它费用
		 * */
		String gcsOtherFee = tradeData.elementText("OTHER_FEE");

		if (gcsOtherFee == null)
			gcsOtherFee = "0";
		if (gcsOtherFee.trim().length() == 0)
			gcsOtherFee = "0";

		/**
		 * 实际交收金额
		 * */
		String gcsFactTradeMoney = tradeData.elementText("ACTU_DELIV_AMT");

		if (gcsFactTradeMoney == null)
			gcsFactTradeMoney = "0";
		if (gcsFactTradeMoney.trim().length() == 0)
			gcsFactTradeMoney = "0";

		/**
		 * 赎回
		 * */
		if ("16".equalsIgnoreCase(tradeType)) {
			try {
				if ("10".equalsIgnoreCase(state)) { // 10. 交收成功
					/**
					 * 删除存在的确认数据
					 * */
					String tradeNums = this
							.getDatabySql(
									"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
											+ groupAndPort[0]
											+ "_DATA_OPENFUNDTRADE tdst "
											+ " where FDataType in('return' , 'confirm') and FGCSNum = "
											+ this.getYssPub().getDbLink()
													.sqlString(GCSNum), "FNUM");

					if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
						this.deleteOpenFundData(groupAndPort[0], tradeNums,
								this.toDate(gcsBargainDate), this
										.toDate(gcsBargainDate), securityCode,
								groupAndPort[1], "", tradeType, false, "",
								"confirm,return");
					}

					sql = "insert into TB_"
							+ groupAndPort[0]
							+ "_DATA_OPENFUNDTRADE ("
							+ " FNUM,FDATATYPE,FTRADETYPECODE,FPORTCODE,FSECURITYCODE,FBARGAINDATE,FINVESTTYPE," +
							  "FINVMGRCODE,FComfDate,FAPPLYCASHACCCODE,"
							+ " FComfMoney,FComfAmount,FComfFee,FTradePrice,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER," +
							  "FCHECKTIME,FGCSNum"
							+ ") values (?,?,?,?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,?,?,?,?,?" + ")";

					pst = this.getConnection().prepareStatement(sql);

					String num = this.getOpenFundTradeNum(groupAndPort[0],
							gcsBargainDate);
					
					pst.setString(1, num); // 开放式基金交易数据编号
					pst.setString(2, "confirm"); // 数据类型 为申请数据
					pst.setString(3, tradeType); // 交易类型
					pst.setString(4, groupAndPort[1]); // 组合
					pst.setString(5, securityCode); // 证券代码
					pst.setDate(6, new java.sql.Date(this
							.toDate(gcsBargainDate).getTime())); // 申请日期
					pst.setString(7, "C"); // 投资类型
					pst.setString(8, " "); // 投资经理代码
					pst.setDate(9, new java.sql.Date(this
							.toDate(gcsBargainDate).getTime())); // 确认日期
					pst.setString(10, cashAcountCode); // 申请账户
					pst.setDouble(11, YssD.sub(Double
							.parseDouble(gcsFactTradeMoney), Double
							.parseDouble(gcsOtherFee))); // 确认金额
					pst.setDouble(12, Double.parseDouble(gcsTradeAmount)); // 确认数量
					pst.setDouble(13, Double.parseDouble(gcsOtherFee)); // 确认交易费
					pst.setDouble(14, YssD.div(YssD.sub(Double
							.parseDouble(gcsFactTradeMoney), Double
							.parseDouble(gcsOtherFee)), Double
							.parseDouble(gcsTradeAmount))); // 确认价格
					pst.setInt(15, 1); // 审核状态
					pst.setString(16, "GCS"); // 创建人
					pst.setString(17, this.formatDate(new Date(),
							"yyyyMMdd HH:mm:ss"));// 创建时间
					pst.setString(18, "GCS"); // 审核人
					pst.setString(19, this.formatDate(new Date(),
							"yyyyMMdd HH:mm:ss"));// 审核时间
					pst.setString(20, GCSNum);

					pst.executeUpdate();

					sql = "insert into TB_"
							+ groupAndPort[0]
							+ "_DATA_OPENFUNDTRADE ("
							+ " FNUM,FDATATYPE,FTRADETYPECODE,FPORTCODE,FSECURITYCODE,FBARGAINDATE,FINVESTTYPE," +
							"FINVMGRCODE,FReturnDate,FRtnCashAccCode,"
							+ " FReturnFee,FReturnMoney,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,FGCSNum"
							+ ") values (?,?,?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,?,?,?,?" + ")";

					pst = this.getConnection().prepareStatement(sql);
					pst.setString(1, num); // 开放式基金交易数据编号
					pst.setString(2, "return"); // 数据类型 为申请数据
					pst.setString(3, tradeType); // 交易类型
					pst.setString(4, groupAndPort[1]); // 组合
					pst.setString(5, securityCode); // 证券代码
					pst.setDate(6, new java.sql.Date(this
							.toDate(gcsBargainDate).getTime())); // 申请日期
					pst.setString(7, "C"); // 投资类型
					pst.setString(8, " "); // 投资经理代码
					pst.setDate(9, new java.sql.Date(this.toDate(gcsReturnDate)
							.getTime())); // 返款日期
					pst.setString(10, cashAcountCode); // 返还账户
					pst.setDouble(11, 0); // 退还费用
					pst.setDouble(12, Double.parseDouble(gcsFactTradeMoney)); // 退换金额
					pst.setInt(13, 1); // 审核状态
					pst.setString(14, "GCS"); // 创建人
					pst.setString(15, this.formatDate(new Date(),
							"yyyyMMdd HH:mm:ss"));// 创建时间
					pst.setString(16, "GCS"); // 审核人
					pst.setString(17, this.formatDate(new Date(),
							"yyyyMMdd HH:mm:ss"));// 审核时间
					pst.setString(18, GCSNum);

					pst.executeUpdate();
				}
			} catch (Exception e) {
				this.setReplyCode("1");// 设置处理结果标识
				System.out
						.println("生成开放式基金赎回业务数据或删除相关联数据时出错：" + e.getMessage());
			} finally {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 删除开放式基金相关数据
	 * */
	private boolean dealOpenFundTradeDelete(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum,
			String dataType) {
		boolean result = true;

		/**
		 * 填塞组合群前缀
		 * */
		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		// 交易类型
		String tmpTradeType = tradeData.elementText("TRANS_TYPE");
		if (tmpTradeType == null || tmpTradeType.trim().length() == 0)
			return false;

		String tradeType = "";

		if ("11".equalsIgnoreCase(tmpTradeType)) // 认购
			tradeType = "04";
		else if ("12".equalsIgnoreCase(tmpTradeType)) // 申购
			tradeType = "15";
		else if ("13".equalsIgnoreCase(tmpTradeType)) // 赎回
			tradeType = "16";

		/**
		 * 交易日期 / 申请日期 / 确认日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		if ("13".equalsIgnoreCase(state)) { // 13. 注销
			try {
				String tradeNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
								+ groupAndPort[0]
								+ "_DATA_OPENFUNDTRADE tdst "
								+ " where FGCSNum = "
								+ this.getYssPub().getDbLink()
										.sqlString(GCSNum), "FNUM");
				if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
					this
							.deleteOpenFundData(groupAndPort[0], tradeNums,
									this.toDate(gcsBargainDate), this
											.toDate(gcsBargainDate),
									securityCode, groupAndPort[1], "",
									tradeType, false, "", dataType);
				}
			} catch (Exception e) {
				this.setReplyCode("1");// 设置处理结果标识
				System.out.println("删除开放式基金业务数据或相关数据出错：" + e.getMessage());
			}
		}
		return result; // 不进行新增处理
	}

	/**
	 * 境内转帐&银证转帐&保证金调整 业务
	 * */
	private boolean dealTransferAndBondTrade(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		String sql = "";
		boolean result = true;
		PreparedStatement pst = null;
		String gcsBargainDate = tradeData.elementText("TRANS_DATE"); // 交易日期
		String gcsSettleDate = tradeData.elementText("FUND_DELIV_DATE"); // 交收日期

		String gcsSettlementMark = tradeData.elementText("FUND_DIRE"); // 交收标志 +
																		// -
		if (gcsSettlementMark == null || gcsSettlementMark.trim().length() == 0)
			return false;

		// this.getYssPub().setPrefixTB(groupAndPort[0]);
		// this.getYssPub().setWebRootRealPath()
		// this.getServletContext().getRealPath("");

		/**
		 * 账户A部分
		 * */
		String gcsCuryCodeA = tradeData.elementText("CURR"); // 货币符号A
		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		String curyCodeA = dictCurys.get(gcsCuryCodeA);
		curyCodeA = (curyCodeA == null || curyCodeA.trim().length() == 0) ? gcsCuryCodeA
				: curyCodeA;

		String gcsFoundA = tradeData.elementText("FUND_ACT_NO"); // 资金账号A

		if (gcsFoundA == null || gcsFoundA.trim().length() == 0)
			return false;

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsFoundA,
				curyCodeA);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsFoundA + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsFoundA
						+ "的现金帐户。");
			return false;
		}

		gcsFoundA = this.getCashAcc(groupAndPort[0], gcsFoundA, curyCodeA);

		String tradeMoneyA = tradeData.elementText("DEAL_AMT"); // 成交资金A

		/**
		 * 账户B部分
		 * */
		String gcsCuryCodeB = tradeData.elementText("CURR"); // 货币符号B
		String curyCodeB = dictCurys.get(gcsCuryCodeB);
		curyCodeB = (curyCodeB == null || curyCodeB.trim().length() == 0) ? gcsCuryCodeB
				: curyCodeB;
		String gcsFoundB = tradeData.elementText("OPP_FUND_NO"); // 资金账号B
		if (gcsFoundB == null || gcsFoundB.trim().length() == 0)
			return false;

		cashAcccount = this.getAccCount(groupAndPort[0], gcsFoundB, curyCodeB);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsFoundB + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsFoundB
						+ "的现金帐户。");
			return false;
		}
		gcsFoundB = this.getCashAcc(groupAndPort[0], gcsFoundB, curyCodeA);
		String tradeMoneyB = tradeData.elementText("DEAL_AMT"); // 成交资金B 和 成交资金A
																// 一样

		// 兑换汇率
		double exCuryRate = 0;

		if ("+".equalsIgnoreCase(gcsSettlementMark)) {
			exCuryRate = YssD.round(YssD.div(Double.parseDouble(tradeMoneyB),
					Double.parseDouble(tradeMoneyA)), 13);
		} else if ("-".equalsIgnoreCase(gcsSettlementMark)) {
			exCuryRate = YssD.round(YssD.div(Double.parseDouble(tradeMoneyA),
					Double.parseDouble(tradeMoneyB)), 13);
		}

		// 基础 组合 金额
		RateTradeBean rateTrade = new RateTradeBean();
		rateTrade.setYssPub(this.getYssPub());
		rateTrade.setTradeDate(this.toDate(gcsBargainDate));

		rateTrade.setSettleDate(this.toDate(gcsSettleDate));
		rateTrade.setBSettleDate(this.toDate(gcsSettleDate));

		rateTrade.setExCuryRate(exCuryRate);// 兑换汇率

		// 如GCS资金收付标识为'-'，表流出，则GCS资金帐号对应QD流出的现金帐户；GCS对手方资金账号则对应QD流入现金帐户
		if ("-".equalsIgnoreCase(gcsSettlementMark)) {
			rateTrade.setSCashAccCode(gcsFoundA);
			rateTrade.setPortCode(groupAndPort[1]);
			rateTrade.setAnalysisCode1(" ");
			rateTrade.setAnalysisCode2(" ");
			rateTrade.setAnalysisCode3(" ");
			rateTrade.setSMoney(Double.parseDouble(tradeMoneyA));
			rateTrade.setSCuryCode(curyCodeA);

			rateTrade.setBCashAccCode(gcsFoundB);
			rateTrade.setPortCode(groupAndPort[1]);
			rateTrade.setAnalysisCode1(" ");
			rateTrade.setAnalysisCode2(" ");
			rateTrade.setAnalysisCode3(" ");
			rateTrade.setSMoney(Double.parseDouble(tradeMoneyB));
			rateTrade.setSCuryCode(curyCodeB);
		} else if ("+".equalsIgnoreCase(gcsSettlementMark)) {
			rateTrade.setSCashAccCode(gcsFoundB);
			rateTrade.setPortCode(groupAndPort[1]);
			rateTrade.setAnalysisCode1(" ");
			rateTrade.setAnalysisCode2(" ");
			rateTrade.setAnalysisCode3(" ");
			rateTrade.setSMoney(Double.parseDouble(tradeMoneyB));
			rateTrade.setSCuryCode(curyCodeB);

			rateTrade.setBCashAccCode(gcsFoundA);
			rateTrade.setPortCode(groupAndPort[1]);
			rateTrade.setAnalysisCode1(" ");
			rateTrade.setAnalysisCode2(" ");
			rateTrade.setAnalysisCode3(" ");
			rateTrade.setSMoney(Double.parseDouble(tradeMoneyA));
			rateTrade.setSCuryCode(curyCodeA);
		}

		try {
			rateTrade.getOperValue("costfx");
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("GCS计算外汇交易基础金额和组合金额时出错：" + e.getMessage());
		}

		// 基础金额
		String baseMoney = "" + rateTrade.getBaseMoney();

		// 组合金额
		String portMoney = "" + rateTrade.getPortMoney();

		/**
		 * 附言
		 * */
		String ps = tradeData.elementText("PS");
		if (ps != null && ps.trim().length() > 0)
			ps = ps.substring(0, 100);

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_ratetrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.FRateTradeNum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_ratetrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_ratetrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.FRateTradeNum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_ratetrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_data_ratetrade "
				+ " (FNUM,FPORTCODE,FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FBANALYSISCODE1,FBANALYSISCODE2," +
				"FBANALYSISCODE3,"
				+ " FBPORTCODE,FTRADETYPE,FCATTYPE,FBCASHACCCODE,FBCURYCODE,FBMONEY,FSCASHACCCODE,FSCURYCODE,FSMONEY,"
				+ " FTRADEDATE,FTRADETIME,FSETTLEDATE,FSETTLETIME,FEXCURYRATE,FBSETTLEDATE,FBSETTLETIME,"
				+ " FBCURYFEE,FSCURYFEE,FBASEMONEY,FPORTMONEY,FLONGCURYRATE,FRATEFX,"
				+ " FUPDOWN,FDATASOURCE,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,"
				+ " FRATETRADETYPE,FGCSNUM) values "
				+ " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		try {
			pst = this.getConnection().prepareStatement(sql);
			pst.setString(1, this.getRateTradeNum(gcsSettleDate, "Tb_"
					+ groupAndPort[0] + "_data_ratetrade")); // 外汇交易编号
			pst.setString(2, groupAndPort[1]); // 组合
			pst.setString(3, " "); // 分析代码1
			pst.setString(4, " "); // 分析代码2
			pst.setString(5, " "); // 分析代码3
			pst.setString(6, " "); // 买入分析代码1
			pst.setString(7, " "); // 买入分析代码2
			pst.setString(8, " "); // 买入分析代码3
			pst.setString(9, groupAndPort[1]); // 买入组合
			pst.setInt(10, 0); // 交易方式
			pst.setInt(11, 0); // 所属分类
			if ("+".equalsIgnoreCase(gcsSettlementMark)) {
				pst.setString(12, gcsFoundA); // 买入
				pst.setString(13, curyCodeA);
				pst.setDouble(14, Double.parseDouble(tradeMoneyA));
				pst.setString(15, gcsFoundB); // 卖出
				pst.setString(16, curyCodeB);
				pst.setDouble(17, Double.parseDouble(tradeMoneyB));
			} else if ("-".equalsIgnoreCase(gcsSettlementMark)) {
				pst.setString(12, gcsFoundB); // 买入
				pst.setString(13, curyCodeB);
				pst.setDouble(14, Double.parseDouble(tradeMoneyB));
				pst.setString(15, gcsFoundA); // 卖出
				pst.setString(16, curyCodeA);
				pst.setDouble(17, Double.parseDouble(tradeMoneyA));
			}
			pst.setDate(18, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 交易日期
			pst.setString(19, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 交易时间
			pst.setDate(20, new java.sql.Date(this.toDate(gcsSettleDate)
					.getTime())); // 结算日期
			pst.setString(21, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 结算时间
			pst.setDouble(22, exCuryRate); // 兑换汇率
			pst.setDate(23, new java.sql.Date(this.toDate(gcsSettleDate)
					.getTime())); // 买入结算日期
			pst.setString(24, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 买入结算时间
			pst.setDouble(25, 0); // 交易费
			pst.setDouble(26, 0); // 交易费
			pst.setDouble(27, Double.parseDouble(baseMoney)); // 基础金额
			pst.setDouble(28, Double.parseDouble(portMoney)); // 组合金额
			pst.setDouble(29, 0);
			pst.setDouble(30, 0);
			pst.setDouble(31, 0);
			pst.setString(32, "ZD"); // ZD 为自动读取的数据
			pst.setString(33, ps); // 备注
			pst.setInt(34, 1); // 审核状态
			pst.setString(35, "GCS"); // 创建人
			pst.setString(36, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 创建时间
			pst.setString(37, "GCS");
			pst.setString(38, this.formatDate(new Date(), "yyyyMMdd HH:mm:ss")); // 审核时间
			pst.setString(39, "2"); // 外汇类型
			pst.setString(40, GCSNum); // GCSnum
			pst.executeUpdate();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("GCS处理境内转帐&银证转帐&保证金调整数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理GCS境内转帐&银证转帐&保证金调整数据出错："
							+ e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 新股新债业务 申购
	 * */
	private boolean dealNewThighNewDebtApply(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		String sql = "";
		PreparedStatement pst = null;
		boolean result = true;
		/**
		 * 填塞组合群前缀
		 * */

		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 *交易类型
		 * */
		String gcsSecurityIdenti = tradeData.elementText("TRANS_TYPE");
		String tradeType = "";

		if ("303".equalsIgnoreCase(gcsSecurityIdenti)
				|| "304".equalsIgnoreCase(gcsSecurityIdenti)
				|| "575".equalsIgnoreCase(gcsSecurityIdenti)
				|| "521".equalsIgnoreCase(gcsSecurityIdenti)
				|| "519".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "40";
		} else if ("519".equalsIgnoreCase(gcsSecurityIdenti)
				|| "519".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "50";
		} else if ("305".equalsIgnoreCase(gcsSecurityIdenti)
				|| "306".equalsIgnoreCase(gcsSecurityIdenti)
				|| "568".equalsIgnoreCase(gcsSecurityIdenti)
				|| "522".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "41";
		} else if ("520".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "51";
		} else if ("307".equalsIgnoreCase(gcsSecurityIdenti)
				|| "308".equalsIgnoreCase(gcsSecurityIdenti)
				|| "569".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "42";
		} else if ("523".equalsIgnoreCase(gcsSecurityIdenti)) {
			// 根据由QD证券信息表中获取证券类型以判断 42 新股中签返款 或 52 新债中签返款
			// tradeType = "51";
		}

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 交收日期
		 * */
		String gcsJSDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsJSDate == null || gcsJSDate.trim().length() == 0)
			return false;

		/**
		 * 成交数量
		 * */
		String gcsTradeAmount = tradeData.elementText("DEAL_QUAN");

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		/**
		 * 成交金额
		 * */
		String gcsTradeMoney = tradeData.elementText("ACTU_DELIV_AMT");

		if (gcsTradeMoney == null)
			gcsTradeMoney = "0";
		if (gcsTradeMoney.trim().length() == 0)
			gcsTradeMoney = "0";

		/**
		 * 成交价格
		 * */
		String gcsTradePrice = ""
				+ YssD.div(Double.parseDouble(gcsTradeMoney), Double
						.parseDouble(gcsTradeAmount));

		if (gcsTradePrice == null)
			gcsTradePrice = "0";
		if (gcsTradePrice.trim().length() == 0)
			gcsTradePrice = "0";

		/**
		 * 交收金额
		 * */
		String gcsTotalCost = gcsTradeMoney;

		if (gcsTotalCost == null)
			gcsTotalCost = "0";
		if (gcsTotalCost.trim().length() == 0)
			gcsTotalCost = "0";

		/**
		 * 交易券商 Dict_Brokercode_GCS
		 * */
		String gcsBrokerCode = tradeData.elementText("TRADER_ID");

		String brokerCode = "";

		Hashtable<String, String> dictBrokerCodes = this.getMarket(
				groupAndPort[0], "Dict_Brokercode_GCS");
		brokerCode = dictBrokerCodes.get(gcsBrokerCode);
		brokerCode = (brokerCode == null || brokerCode.trim().length() == 0) ? gcsBrokerCode
				: brokerCode;

		/**
		 * 交易席位代码 Dict_TradeSeat_GCS
		 **/
		String gcsSeatCode = tradeData.elementText("SEAT_NO");
		String seatCode = "";

		Hashtable<String, String> dictSeatCodes = this.getMarket(
				groupAndPort[0], "Dict_TradeSeat_GCS");
		seatCode = dictSeatCodes.get(gcsSeatCode);
		seatCode = (seatCode == null || seatCode.trim().length() == 0) ? gcsSeatCode
				: seatCode;

		/**
		 * 附言
		 * */
		String gcsPs = tradeData.elementText("PS");
		if (gcsPs == null)
			gcsPs = "";
		if (gcsPs != null && gcsPs.trim().length() > 100)
			gcsPs = gcsPs.substring(0, 100);

		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());

		if ("13".equalsIgnoreCase(state)) {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		/**
		 * 不存在则直接新增
		 * */
		/*
		 * ,FFEECODE4,FTRADEFEE4,FFEECODE5,FTRADEFEE5,
		 * FFEECODE6,FTRADEFEE6,FFEECODE7,FTRADEFEE7,FFEECODE8,FTRADEFEE8
		 */
		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_Data_SubTrade "
				+ "(FNUM,FSECURITYCODE,FPORTCODE ,FBROKERCODE ,FINVMGRCODE,FTRADETYPECODE ,FCASHACCCODE,FATTRCLSCODE,"
				+ "FRATEDATE, FBARGAINDATE ,FBARGAINTIME,FSETTLEDATE ,FSETTLETIME ,FMATUREDATE,FMATURESETTLEDATE," +
				"FFACTCASHACCCODE,"
				+ "FFACTSETTLEMONEY,FEXRATE,FFACTBASERATE,FFACTPORTRATE,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE," +
				"FALLOTPROPORTION,"
				+ "FOLDALLOTAMOUNT,FALLOTFACTOR,FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBAILMONEY,FFEECODE1,"
				+ "FTRADEFEE1,FFEECODE2,FTRADEFEE2,FFEECODE3,FTRADEFEE3,FTOTALCOST,"
				+ "FCOST,FMCOST,"
				+ "FVCOST,FBASECURYCOST,FMBASECURYCOST,FVBASECURYCOST,FPORTCURYCOST,FMPORTCURYCOST,"
				+ "FVPORTCURYCOST,FSETTLESTATE,FFACTSETTLEDATE,FSETTLEDESC,FORDERNUM,FDATASOURCE,FDATABIRTH,FSETTLEORGCODE,"
				+ "FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,"
				+ // FETFBALAACCTCODE,FETFBALASETTLEDATE,FETFBALAMONEY,FETFCASHALTERNAT,"
					// +
				"FSEATCODE,FSTOCKHOLDERCODE,FDS,"
				+ // FSPLITNUM ,FINVESTTYPE ,FDEALNUM
				"FSECURITYDELAYSETTLESTATE,FBROKERIDCODE,"
				+ 
				"FHANDCOSTSTATE,"
				+ 
				"FGCSNum) values(?,?,?,?,?,?,?,?,"
				+ // FMTREPLACEDATE
				"?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?," + "?,?,?," + "?,?," + "?," + "?)";
		try {
			pst = this.getConnection().prepareStatement(sql);
			pst.setString(1, ""
					+ this.getSubTradeNum(groupAndPort[0], gcsBargainDate,
							tradeType));// 交易编号
			pst.setString(2, securityCode); // 证券代码
			pst.setString(3, groupAndPort[1]); // 组合
			pst.setString(4, brokerCode); // 券商
			pst.setString(5, " "); // 投资经理
			pst.setString(6, tradeType); // 交易类型
			pst.setString(7, cashAcountCode); // 现金账户
			pst.setString(8, " "); // 所属分类
			pst.setDate(9, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 汇率日期
			pst.setDate(10, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 成交日期
			pst.setString(11, "00:00:00"); // 成交时间
			pst
					.setDate(12, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(13, "00:00:00"); // 结算时间
			pst.setDate(14, new java.sql.Date(this.toDate("9998-12-31")
					.getTime())); // 到期日期
			pst.setDate(15, new java.sql.Date(this.toDate("9998-12-31")
					.getTime())); // 到期结算日期
			pst.setString(16, cashAcountCode); // 实际结算账户
			pst.setDouble(17, Double.parseDouble(gcsTotalCost)); // 实际结算金额
			pst.setDouble(18, 1); // 兑换汇率
			pst.setDouble(19, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base")); // 实际基础汇率
			pst.setDouble(20, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setInt(21, 1); // 是否自动结算
			pst.setDouble(22, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setDouble(23, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base"));
			pst.setDouble(24, 100); // 分配比例
			pst.setDouble(25, Double.parseDouble(gcsTradeAmount)); // 原始分配数量
			pst.setDouble(26, 1); // 分配因子
			pst.setDouble(27, Double.parseDouble(gcsTradeAmount)); // 交易数量
			pst.setDouble(28, Double.parseDouble(gcsTradePrice)); // 交易价格
			pst.setDouble(29, Double.parseDouble(gcsTradeMoney)); // 交易金额
			pst.setDouble(30, 0); // 应收利息
			pst.setDouble(31, 0); // 保证金
			pst.setString(32, " "); // 费用代码1
			pst.setDouble(33, 0); // 费用1为 佣金
			pst.setString(34, " "); // 费用代码2
			pst.setDouble(35, 0); // 费用2 税费
			pst.setString(36, " "); // 费用3
			pst.setDouble(37, 0); // 其他费用
			pst.setDouble(38, Double.parseDouble(gcsTotalCost)); // 交收金额
			pst.setDouble(39, 0);
			pst.setDouble(40, 0);
			pst.setDouble(41, 0);
			pst.setDouble(42, 0);
			pst.setDouble(43, 0);
			pst.setDouble(44, 0);
			pst.setDouble(45, 0);
			pst.setDouble(46, 0);
			pst.setDouble(47, 0);
			pst.setInt(48, 0); // 结算状态
			pst
					.setDate(49, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(50, " "); // 结算描述
			pst.setString(51, " "); // 订单编号
			pst.setInt(52, 1); // 数据来源
			pst.setString(53, " "); // 交易来源
			pst.setString(54, " "); // 结算机构代码
			pst.setString(55, gcsPs); // 描述
			pst.setInt(56, 1); // 审核状态
			pst.setString(57, "GCS"); // 创建人
			pst.setString(58, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 创建时间
			pst.setString(59, "GCS"); // 审核人
			pst.setString(60, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 审核时间
			pst.setString(61, seatCode); // 席位代码
			pst.setString(62, " "); // 股东代码
			pst.setString(63, "ZD_JK"); // 操作类型 //代表接口读入
			pst.setInt(64, 0); // 延迟交割标识
			pst.setString(65, gcsBrokerCode); // 券商代码
			pst.setInt(66, 0); // 手动修改成本标志
			pst.setString(67, GCSNum); // GCSnum

			pst.executeUpdate();
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("处理新股新债申购业务数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理新股新债申购数据出错：" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 新股新债业务 中签
	 * */
	private boolean dealNewThighNewDebtMidSign(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {

		String sql = "";
		PreparedStatement pst = null;
		boolean result = true;
		/**
		 * 填塞组合群前缀
		 * */

		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 *交易类型
		 * */
		String gcsSecurityIdenti = tradeData.elementText("TRANS_TYPE");
		String tradeType = "";
		if ("303".equalsIgnoreCase(gcsSecurityIdenti)
				|| "304".equalsIgnoreCase(gcsSecurityIdenti)
				|| "575".equalsIgnoreCase(gcsSecurityIdenti)
				|| "521".equalsIgnoreCase(gcsSecurityIdenti)
				|| "519".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "40";
		} else if ("519".equalsIgnoreCase(gcsSecurityIdenti)
				|| "519".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "50";
		} else if ("305".equalsIgnoreCase(gcsSecurityIdenti)
				|| "306".equalsIgnoreCase(gcsSecurityIdenti)
				|| "568".equalsIgnoreCase(gcsSecurityIdenti)
				|| "522".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "41";
		} else if ("520".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "51";
		} else if ("307".equalsIgnoreCase(gcsSecurityIdenti)
				|| "308".equalsIgnoreCase(gcsSecurityIdenti)
				|| "569".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "42";
		} else if ("523".equalsIgnoreCase(gcsSecurityIdenti)) {

		}

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 资金交收日期
		 * */
		String gcsJSDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsJSDate == null || gcsJSDate.trim().length() == 0)
			return false;

		/**
		 * 成交数量
		 * */
		String gcsTradeAmount = tradeData.elementText("DEAL_QUAN");

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		/**
		 * 成交金额
		 * */
		String gcsTradeMoney = tradeData.elementText("DEAL_AMT");

		if (gcsTradeMoney == null)
			gcsTradeMoney = "0";
		if (gcsTradeMoney.trim().length() == 0)
			gcsTradeMoney = "0";

		/**
		 * 成交价格
		 * */
		String gcsTradePrice = ""
				+ YssD.div(Double.parseDouble(gcsTradeMoney), Double
						.parseDouble(gcsTradeAmount));

		/**
		 * 利息
		 * */
		String gcsInterest = "0";

		if (gcsInterest == null)
			gcsInterest = "0";
		if (gcsInterest.trim().length() == 0)
			gcsInterest = "0";

		/**
		 * 佣金
		 * */
		String comm_Fee = tradeData.elementText("COMM_FEE");

		if (comm_Fee == null)
			comm_Fee = "0";
		else if (comm_Fee.trim().length() == 0)
			comm_Fee = "0";

		String comm_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHYJ"
				: "YSS_SZYJ";

		String jsf_Fee = tradeData.elementText("JSF_FEE"); // 经手费金额

		if (jsf_Fee == null)
			jsf_Fee = "0";
		else if (jsf_Fee.trim().length() == 0)
			jsf_Fee = "0";

		String jsf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHJS"
				: "YSS_SZJS";

		String yhs_Fee = tradeData.elementText("YHS_FEE"); // 印花税金额

		if (yhs_Fee == null)
			yhs_Fee = "0";
		else if (yhs_Fee.trim().length() == 0)
			yhs_Fee = "0";

		String yhs_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHYH"
				: "YSS_SZYH";

		String zgf_Fee = tradeData.elementText("ZGF_FEE"); // 证管费金额

		if (zgf_Fee == null)
			zgf_Fee = "0";
		else if (zgf_Fee.trim().length() == 0)
			zgf_Fee = "0";

		String zgf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHZG"
				: "YSS_SZZG";

		String ghf_Fee = tradeData.elementText("GHF_FEE"); // 过户费金额

		if (ghf_Fee == null)
			ghf_Fee = "0";
		else if (ghf_Fee.trim().length() == 0)
			ghf_Fee = "0";

		String ghf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHGH"
				: "YSS_SZGH";

		String sxf_Fee = tradeData.elementText("SXF_FEE"); // 手续费金额

		if (sxf_Fee == null)
			sxf_Fee = "0";
		else if (sxf_Fee.trim().length() == 0)
			sxf_Fee = "0";

		String sxf_FeeCode = "CG".equalsIgnoreCase(qdExchangeCode) ? "YSS_SHJSF"
				: "YSS_SZJSF";

		String fxf_Fee = tradeData.elementText("FXJ_FEE"); // 风险金金额

		if (fxf_Fee == null)
			fxf_Fee = "0";
		else if (fxf_Fee.trim().length() == 0)
			fxf_Fee = "0";

		// 其它费
		String otherFee = ""
				+ YssD.add(Double.parseDouble(sxf_Fee), Double
						.parseDouble(fxf_Fee));

		/**
		 * 交收金额 = 交易金额 + 所有费用
		 * */
		String gcsTotalCost = ""
				+ YssD.add(Double.parseDouble(gcsTradeMoney), Double
						.parseDouble(comm_Fee), Double.parseDouble(jsf_Fee),
						Double.parseDouble(yhs_Fee), Double
								.parseDouble(zgf_Fee), Double
								.parseDouble(ghf_Fee), Double
								.parseDouble(otherFee));

		if (gcsTotalCost == null)
			gcsTotalCost = "0";
		if (gcsTotalCost.trim().length() == 0)
			gcsTotalCost = "0";

		/**
		 * 交易券商 Dict_Brokercode_GCS
		 * */
		String gcsBrokerCode = tradeData.elementText("TRADER_ID");
		String brokerCode = "";

		Hashtable<String, String> dictBrokerCodes = this.getMarket(
				groupAndPort[0], "Dict_Brokercode_GCS");
		brokerCode = dictBrokerCodes.get(gcsBrokerCode);
		brokerCode = (brokerCode == null || brokerCode.trim().length() == 0) ? gcsBrokerCode
				: brokerCode;

		/**
		 * 交易席位代码 Dict_TradeSeat_GCS
		 **/
		String gcsSeatCode = tradeData.elementText("SEAT_NO");
		String seatCode = "";

		Hashtable<String, String> dictSeatCodes = this.getMarket(
				groupAndPort[0], "Dict_TradeSeat_GCS");
		seatCode = dictSeatCodes.get(gcsSeatCode);
		seatCode = (seatCode == null || seatCode.trim().length() == 0) ? gcsSeatCode
				: seatCode;

		/**
		 * 附言
		 * */
		String gcsPs = tradeData.elementText("PS");

		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		/**
		 * 不存在则直接新增
		 * */
		/*
		 * ,FFEECODE4,FTRADEFEE4,FFEECODE5,FTRADEFEE5,
		 * FFEECODE6,FTRADEFEE6,FFEECODE7,FTRADEFEE7,FFEECODE8,FTRADEFEE8
		 */
		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_Data_SubTrade "
				+ "(FNUM,FSECURITYCODE,FPORTCODE ,FBROKERCODE ,FINVMGRCODE,FTRADETYPECODE ,FCASHACCCODE,FATTRCLSCODE,"
				+ "FRATEDATE, FBARGAINDATE ,FBARGAINTIME,FSETTLEDATE ,FSETTLETIME ,FMATUREDATE,FMATURESETTLEDATE," +
				"FFACTCASHACCCODE,"
				+ "FFACTSETTLEMONEY,FEXRATE,FFACTBASERATE,FFACTPORTRATE,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE," +
				"FALLOTPROPORTION,"
				+ "FOLDALLOTAMOUNT,FALLOTFACTOR,FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBAILMONEY,FFEECODE1,"
				+ "FTRADEFEE1,FFEECODE2,FTRADEFEE2,FFEECODE3,FTRADEFEE3,FFEECODE4,FTRADEFEE4,FFEECODE5,FTRADEFEE5," +
				"FFEECODE6,FTRADEFEE6,"
				+ "FTOTALCOST,"
				+ "FCOST,FMCOST,"
				+ "FVCOST,FBASECURYCOST,FMBASECURYCOST,FVBASECURYCOST,FPORTCURYCOST,FMPORTCURYCOST,"
				+ "FVPORTCURYCOST,FSETTLESTATE,FFACTSETTLEDATE,FSETTLEDESC,FORDERNUM,FDATASOURCE,FDATABIRTH,FSETTLEORGCODE,"
				+ "FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,"
				+
				"FSEATCODE,FSTOCKHOLDERCODE,FDS,"
				+ // FSPLITNUM ,FINVESTTYPE ,FDEALNUM
				"FSECURITYDELAYSETTLESTATE,FBROKERIDCODE,"
				+ 
				"FHANDCOSTSTATE,"+ 
				"FGCSNum) values(?,?,?,?,?,?,?,?,"
				+ // FMTREPLACEDATE
				"?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?," + "?,?,?," + "?,?," + "?," + "?,?,?,?,?,?,?)";
		try {
			pst = this.getConnection().prepareStatement(sql);
			pst.setString(1, ""
					+ this.getSubTradeNum(groupAndPort[0], gcsBargainDate,
							tradeType));// 交易编号
			pst.setString(2, securityCode); // 证券代码
			pst.setString(3, groupAndPort[1]); // 组合
			pst.setString(4, brokerCode); // 券商
			pst.setString(5, " "); // 投资经理
			pst.setString(6, tradeType); // 交易类型
			pst.setString(7, cashAcountCode); // 现金账户
			pst.setString(8, " "); // 所属分类
			pst.setDate(9, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 汇率日期
			pst.setDate(10, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 成交日期
			pst.setString(11, "00:00:00"); // 成交时间
			pst
					.setDate(12, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(13, "00:00:00"); // 结算时间
			pst.setDate(14, new java.sql.Date(this.toDate("9998-12-31")
					.getTime())); // 到期日期
			pst.setDate(15, new java.sql.Date(this.toDate("9998-12-31")
					.getTime())); // 到期结算日期
			pst.setString(16, cashAcountCode); // 实际结算账户
			pst.setDouble(17, Double.parseDouble(gcsTotalCost)); // 实际结算金额
			pst.setDouble(18, 1); // 兑换汇率
			pst.setDouble(19, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base")); // 实际基础汇率
			pst.setDouble(20, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setInt(21, 1); // 是否自动结算
			pst.setDouble(22, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setDouble(23, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base"));
			pst.setDouble(24, 100); // 分配比例
			pst.setDouble(25, Double.parseDouble(gcsTradeAmount)); // 原始分配数量
			pst.setDouble(26, 1); // 分配因子
			pst.setDouble(27, Double.parseDouble(gcsTradeAmount)); // 交易数量
			pst.setDouble(28, Double.parseDouble(gcsTradePrice)); // 交易价格
			pst.setDouble(29, Double.parseDouble(gcsTradeMoney)); // 交易金额
			pst.setDouble(30, Double.parseDouble(gcsInterest)); // 应收利息
			pst.setDouble(31, 0); // 保证金
			pst.setString(32, comm_FeeCode); // 佣金
			pst.setDouble(33, Double.parseDouble(comm_Fee)); // 佣金
			pst.setString(34, jsf_FeeCode); // 经手费
			pst.setDouble(35, Double.parseDouble(jsf_Fee)); // 经手费
			pst.setString(36, yhs_FeeCode); // 印花税
			pst.setDouble(37, Double.parseDouble(yhs_Fee)); // 印花税
			pst.setString(38, zgf_FeeCode); // 证管费
			pst.setDouble(39, Double.parseDouble(zgf_Fee)); // 证管费
			pst.setString(40, ghf_FeeCode); // 过户费
			pst.setDouble(41, Double.parseDouble(ghf_Fee)); // 过户费
			pst.setString(42, sxf_FeeCode); // 经手费
			pst.setDouble(43, Double.parseDouble(otherFee)); // 经手费

			pst.setDouble(44, Double.parseDouble(gcsTotalCost)); // 交收金额
			pst.setDouble(45, 0);
			pst.setDouble(46, 0);
			pst.setDouble(47, 0);
			pst.setDouble(48, 0);
			pst.setDouble(49, 0);
			pst.setDouble(50, 0);
			pst.setDouble(51, 0);
			pst.setDouble(52, 0);
			pst.setDouble(53, 0);
			pst.setInt(54, 0); // 结算状态
			pst
					.setDate(55, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(56, " "); // 结算描述
			pst.setString(57, " "); // 订单编号
			pst.setInt(58, 1); // 数据来源
			pst.setString(59, " "); // 交易来源
			pst.setString(60, " "); // 结算机构代码
			pst.setString(61, gcsPs); // 描述
			pst.setInt(62, 1); // 审核状态
			pst.setString(63, "GCS"); // 创建人
			pst.setString(64, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 创建时间
			pst.setString(65, "GCS"); // 审核人
			pst.setString(66, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 审核时间
			pst.setString(67, seatCode); // 席位代码
			pst.setString(68, " "); // 股东代码
			pst.setString(69, "ZD_JK"); // 操作类型 //代表接口读入
			pst.setInt(70, 0); // 延迟交割标识
			pst.setString(71, gcsBrokerCode); // 券商代码
			pst.setInt(72, 0); // 手动修改成本标志
			pst.setString(73, GCSNum); // GCSnum

			pst.executeUpdate();
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("处理新股新债中签数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理新股新债中签数据出错：" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 新股新债 返款
	 * */
	private boolean dealNewThighNewDebtReturnMoney(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {

		String sql = "";
		PreparedStatement pst = null;
		boolean result = true;
		/**
		 * 填塞组合群前缀
		 * */

		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 *交易类型
		 * */
		String gcsSecurityIdenti = tradeData.elementText("TRANS_TYPE");
		String tradeType = "";
		if ("303".equalsIgnoreCase(gcsSecurityIdenti)
				|| "304".equalsIgnoreCase(gcsSecurityIdenti)
				|| "575".equalsIgnoreCase(gcsSecurityIdenti)
				|| "521".equalsIgnoreCase(gcsSecurityIdenti)
				|| "519".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "40";
		} else if ("519".equalsIgnoreCase(gcsSecurityIdenti)
				|| "519".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "50";
		} else if ("305".equalsIgnoreCase(gcsSecurityIdenti)
				|| "306".equalsIgnoreCase(gcsSecurityIdenti)
				|| "568".equalsIgnoreCase(gcsSecurityIdenti)
				|| "522".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "41";
		} else if ("520".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "51";
		} else if ("307".equalsIgnoreCase(gcsSecurityIdenti)
				|| "308".equalsIgnoreCase(gcsSecurityIdenti)
				|| "569".equalsIgnoreCase(gcsSecurityIdenti)) {
			tradeType = "42";
		} else if ("523".equalsIgnoreCase(gcsSecurityIdenti)) {
			String catCode = this.getDatabySql("select * from tb_"
					+ groupAndPort[0] + "_para_security tps "
					+ " where tps.fcheckstate = 1 and tps.fsecuritycode = "
					+ this.getYssPub().getDbLink().sqlString(securityCode),
					"FCatCode");
			/* 42 新股中签返款 或 52 新债中签返款 (根据由QD证券信息表中获取证券类型以判断) */
			if ("FI".equalsIgnoreCase(catCode))
				tradeType = "52";
			else
				tradeType = "42";
		}

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 交收日期
		 * */
		String gcsJSDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsJSDate == null || gcsJSDate.trim().length() == 0)
			return false;

		/**
		 * 成交数量
		 * */
		String gcsTradeAmount = tradeData.elementText("DEAL_QUAN");

		if (gcsTradeAmount == null)
			gcsTradeAmount = "0";
		if (gcsTradeAmount.trim().length() == 0)
			gcsTradeAmount = "0";

		/**
		 * 成交金额
		 * */
		String gcsTradeMoney = tradeData.elementText("ACTU_DELIV_AMT");

		if (gcsTradeMoney == null)
			gcsTradeMoney = "0";
		if (gcsTradeMoney.trim().length() == 0)
			gcsTradeMoney = "0";

		/**
		 * 成交价格
		 * */
		String gcsTradePrice = ""
				+ YssD.div(Double.parseDouble(gcsTradeMoney), Double
						.parseDouble(gcsTradeAmount));

		/**
		 * 利息
		 * */
		String gcsInterest = tradeData.elementText("INTEREST");

		if (gcsInterest == null)
			gcsInterest = "0";
		if (gcsInterest.trim().length() == 0)
			gcsInterest = "0";

		/**
		 * 实际交收金额
		 * */
		String gcsTotalCost = tradeData.elementText("ACTU_DELIV_AMT");

		if (gcsTotalCost == null)
			gcsTotalCost = "0";
		if (gcsTotalCost.trim().length() == 0)
			gcsTotalCost = "0";

		/**
		 * 交易券商 Dict_Brokercode_GCS
		 * */
		String gcsBrokerCode = tradeData.elementText("TRADER_ID");
		String brokerCode = "";

		Hashtable<String, String> dictBrokerCodes = this.getMarket(
				groupAndPort[0], "Dict_Brokercode_GCS");
		brokerCode = dictBrokerCodes.get(gcsBrokerCode);
		brokerCode = (brokerCode == null || brokerCode.trim().length() == 0) ? gcsBrokerCode
				: brokerCode;

		/**
		 * 交易席位代码 Dict_TradeSeat_GCS
		 **/
		String gcsSeatCode = tradeData.elementText("SEAT_NO");
		String seatCode = "";

		Hashtable<String, String> dictSeatCodes = this.getMarket(
				groupAndPort[0], "Dict_TradeSeat_GCS");
		seatCode = dictSeatCodes.get(gcsSeatCode);
		seatCode = (seatCode == null || seatCode.trim().length() == 0) ? gcsSeatCode
				: seatCode;

		/**
		 * 附言
		 * */
		String gcsPs = tradeData.elementText("PS");

		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_data_subtrade tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");

			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_cash_transfer tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_subtransfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
					this.executeSql("delete from tb_" + groupAndPort[0]
							+ "_cash_transfer where FNum in ("
							+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_data_subTrade where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		/**
		 * 不存在则直接新增
		 * */
		/*
		 * ,FFEECODE4,FTRADEFEE4,FFEECODE5,FTRADEFEE5,
		 * FFEECODE6,FTRADEFEE6,FFEECODE7,FTRADEFEE7,FFEECODE8,FTRADEFEE8
		 */
		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_Data_SubTrade "
				+ "(FNUM,FSECURITYCODE,FPORTCODE ,FBROKERCODE ,FINVMGRCODE,FTRADETYPECODE ,FCASHACCCODE,FATTRCLSCODE,"
				+ "FRATEDATE, FBARGAINDATE ,FBARGAINTIME,FSETTLEDATE ,FSETTLETIME ,FMATUREDATE,FMATURESETTLEDATE," +
				"FFACTCASHACCCODE,"
				+ "FFACTSETTLEMONEY,FEXRATE,FFACTBASERATE,FFACTPORTRATE,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE," +
				"FALLOTPROPORTION,"
				+ "FOLDALLOTAMOUNT,FALLOTFACTOR,FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBAILMONEY,FFEECODE1,"
				+ "FTRADEFEE1,FFEECODE2,FTRADEFEE2,FFEECODE3,FTRADEFEE3,FTOTALCOST,"
				+ "FCOST,FMCOST,"
				+ "FVCOST,FBASECURYCOST,FMBASECURYCOST,FVBASECURYCOST,FPORTCURYCOST,FMPORTCURYCOST,"
				+ "FVPORTCURYCOST,FSETTLESTATE,FFACTSETTLEDATE,FSETTLEDESC,FORDERNUM,FDATASOURCE,FDATABIRTH,FSETTLEORGCODE,"
				+ "FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,"
				+ 
				"FSEATCODE,FSTOCKHOLDERCODE,FDS,"
				+ // FSPLITNUM ,FINVESTTYPE ,FDEALNUM
				"FSECURITYDELAYSETTLESTATE,FBROKERIDCODE,"
				+ 
				"FHANDCOSTSTATE,"
				+ 
				"FGCSNum) values(?,?,?,?,?,?,?,?,"
				+ // FMTREPLACEDATE
				"?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?," + "?,?,?," + "?,?," + "?," + "?)";
		try {
			pst = this.getConnection().prepareStatement(sql);
			pst.setString(1, ""
					+ this.getSubTradeNum(groupAndPort[0], gcsBargainDate,
							tradeType));// 交易编号
			pst.setString(2, securityCode); // 证券代码
			pst.setString(3, groupAndPort[1]); // 组合
			pst.setString(4, brokerCode); // 券商
			pst.setString(5, " "); // 投资经理
			pst.setString(6, tradeType); // 交易类型
			pst.setString(7, cashAcountCode); // 现金账户
			pst.setString(8, " "); // 所属分类
			pst.setDate(9, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 汇率日期
			pst.setDate(10, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 成交日期
			pst.setString(11, "00:00:00"); // 成交时间
			pst
					.setDate(12, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(13, "00:00:00"); // 结算时间
			pst.setDate(14, new java.sql.Date(this.toDate("9998-12-31")
					.getTime())); // 到期日期
			pst.setDate(15, new java.sql.Date(this.toDate("9998-12-31")
					.getTime())); // 到期结算日期
			pst.setString(16, cashAcountCode); // 实际结算账户
			pst.setDouble(17, Double.parseDouble(gcsTotalCost)); // 实际结算金额
			pst.setDouble(18, 1); // 兑换汇率
			pst.setDouble(19, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base")); // 实际基础汇率
			pst.setDouble(20, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setInt(21, 1); // 是否自动结算
			pst.setDouble(22, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "port"));
			pst.setDouble(23, operDeal.getCuryRate(this.toDate(gcsBargainDate),
					curyCode, groupAndPort[1], "base"));
			pst.setDouble(24, 100); // 分配比例
			pst.setDouble(25, Double.parseDouble(gcsTradeAmount)); // 原始分配数量
			pst.setDouble(26, 1); // 分配因子
			pst.setDouble(27, Double.parseDouble(gcsTradeAmount)); // 交易数量
			pst.setDouble(28, Double.parseDouble(gcsTradePrice)); // 交易价格
			pst.setDouble(29, Double.parseDouble(gcsTradeMoney)); // 交易金额
			pst.setDouble(30, Double.parseDouble(gcsInterest)); // 应收利息
			pst.setDouble(31, 0); // 保证金
			pst.setString(32, " "); // 费用代码1
			pst.setDouble(33, 0); // 费用1为 佣金
			pst.setString(34, " "); // 费用代码2
			pst.setDouble(35, 0); // 费用2 税费
			pst.setString(36, " "); // 费用3
			pst.setDouble(37, 0); // 其他费用
			pst.setDouble(38, Double.parseDouble(gcsTotalCost)); // 交收金额
			pst.setDouble(39, 0);
			pst.setDouble(40, 0);
			pst.setDouble(41, 0);
			pst.setDouble(42, 0);
			pst.setDouble(43, 0);
			pst.setDouble(44, 0);
			pst.setDouble(45, 0);
			pst.setDouble(46, 0);
			pst.setDouble(47, 0);
			pst.setInt(48, 0); // 结算状态
			pst
					.setDate(49, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 结算日期
			pst.setString(50, " "); // 结算描述
			pst.setString(51, " "); // 订单编号
			pst.setInt(52, 1); // 数据来源
			pst.setString(53, " "); // 交易来源
			pst.setString(54, " "); // 结算机构代码
			pst.setString(55, gcsPs); // 描述
			pst.setInt(56, 1); // 审核状态
			pst.setString(57, "GCS"); // 创建人
			pst.setString(58, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 创建时间
			pst.setString(59, "GCS"); // 审核人
			pst.setString(60, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 审核时间
			pst.setString(61, seatCode); // 席位代码
			pst.setString(62, " "); // 股东代码
			pst.setString(63, "ZD_JK"); // 操作类型 //代表接口读入
			pst.setInt(64, 0); // 延迟交割标识
			pst.setString(65, gcsBrokerCode); // 券商代码
			pst.setInt(66, 0); // 手动修改成本标志
			pst.setString(67, GCSNum); // GCSnum

			pst.executeUpdate();
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("处理GCS证券买卖数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理GCS证券买卖数据出错：" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 网下新股新债 申购 中签 返款
	 * */
	private boolean dealUnderNetNewThighApply(String[] groupAndPort,
			Element tradeData, String gcsAssetID, String state, String GCSNum) {
		String sql = "";
		PreparedStatement pst = null;
		boolean result = true;

		/**
		 * 填塞组合群前缀
		 * */

		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 *交易类型
		 * */
		String gcsSecurityIdenti = tradeData.elementText("TRANS_TYPE");
		if (gcsSecurityIdenti == null || gcsSecurityIdenti.trim().length() == 0)
			return false;

		String tradeType = gcsSecurityIdenti;

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/**
		 * 交收日期
		 * */
		String gcsJSDate = tradeData.elementText("FUND_DELIV_DATE");
		if (gcsJSDate == null || gcsJSDate.trim().length() == 0)
			return false;

		/**
		 * 申购 实际交收金额
		 * */
		String gcsFactTradeMoney1 = tradeData.elementText("DELIV_AMT");

		if (gcsFactTradeMoney1 == null)
			gcsFactTradeMoney1 = "0";
		if (gcsFactTradeMoney1.trim().length() == 0)
			gcsFactTradeMoney1 = "0";

		/**
		 * 中签 实际成交金额 和数量
		 * */
		String gcsFactTradeMoney2 = tradeData.elementText("ACTU_DELIV_AMT");
		String gcsFactTradeAmount2 = tradeData.elementText("ACTU_DELIV_QUAN");

		if (gcsFactTradeMoney2 == null)
			gcsFactTradeMoney2 = "0";
		if (gcsFactTradeMoney2.trim().length() == 0)
			gcsFactTradeMoney2 = "0";

		if (gcsFactTradeAmount2 == null)
			gcsFactTradeAmount2 = "0";
		if (gcsFactTradeAmount2.trim().length() == 0)
			gcsFactTradeAmount2 = "0";

		/**
		 * 中签返款 交收金额 和 实际交收金额
		 * */
		String gcsTradeMoney3 = tradeData.elementText("DELIV_AMT");
		String gcsFactTradeMoney3 = tradeData.elementText("ACTU_DELIV_AMT");

		if (gcsTradeMoney3 == null)
			gcsTradeMoney3 = "0";
		if (gcsTradeMoney3.trim().length() == 0)
			gcsTradeMoney3 = "0";

		if (gcsFactTradeMoney3 == null)
			gcsFactTradeMoney3 = "0";
		if (gcsFactTradeMoney3.trim().length() == 0)
			gcsFactTradeMoney3 = "0";

		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());

		if ("13".equalsIgnoreCase(state)) {

			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_DATA_NEWISSUETRADE tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_Data_Integrated tctf "
								+ " where tctf.ftradenum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this
							.executeSql("delete from tb_"
									+ groupAndPort[0]
									+ "_Data_Integrated where FNumType = 'securitymanage' and FRelaNum in ("
									+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_DATA_NEWISSUETRADE where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
			return false; // 不进行新增处理
		} else {
			String tradeNums = this.getDatabySql(
					"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
							+ groupAndPort[0]
							+ "_DATA_NEWISSUETRADE tdst  where FGCSNum = "
							+ this.getYssPub().getDbLink().sqlString(GCSNum),
					"FNUM");
			if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
				YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

				/**
				 * 交易数据已结算产生资金调拨主表和字表 数据处理
				 * **/
				String cashTranNums = this.getDatabySql(
						"select WMSYS.WM_CONCAT(FNum) as FNum from tb_"
								+ groupAndPort[0] + "_Data_Integrated tctf "
								+ " where tctf.FNum in( "
								+ operSql.sqlCodes(tradeNums) + ")", "FNum");
				if (cashTranNums.trim().length() > 0) {
					this
							.executeSql("delete from tb_"
									+ groupAndPort[0]
									+ "_Data_Integrated where FNumType = 'securitymanage' and FRelaNum in ("
									+ operSql.sqlCodes(cashTranNums) + ")");
				}
				this.executeSql("delete from tb_" + groupAndPort[0]
						+ "_DATA_NEWISSUETRADE where FNum in("
						+ operSql.sqlCodes(tradeNums) + ")");
			}
		}

		/**
		 * 不存在则直接新增
		 * */
		sql = " insert into Tb_"
				+ groupAndPort[0]
				+ "_DATA_NEWISSUETRADE "
				+ " (FNUM,FTRADETYPECODE,FSECURITYCODE,FPORTCODE,FBARGAINDATE,FSECURITYTYPE,FINVESTTYPE,FINVMGRCODE," +
				"FATTRCLSCODE,FTRANSDATE,"
				+ " FMONEY,FBONDINS,FPRICEMONEY,FCASHACCCODE,FAMOUNT,FLOCKDAYS,FCHECKSTATE,FCREATOR,FCREATETIME,"
				+ " FCHECKUSER,FCHECKTIME,FDIRBALLOT,FGCSNum) "
				+ " values(?,?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?,?"
				+ ",?,?,?,?)";
		try {
			/* 生成交易编号 */
			String tradeNum = this.getNewIssueTradeNum(groupAndPort[0], this
					.toDate(gcsBargainDate));
			pst = this.getConnection().prepareStatement(sql);

			pst.setString(1, "" + tradeNum);// 交易编号
			pst.setString(2, "40"); // 40 新股申购 交易类型
			pst.setString(3, securityCode); // 证券代码
			pst.setString(4, groupAndPort[1]); // 组合
			pst.setDate(5, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 成交日期
			pst.setString(6, "GP"); // 证券类型
			pst.setString(7, "C"); // 投资类型
			pst.setString(8, " "); // 投资经理
			pst.setString(9, "PONS"); // 所属分类
			pst
					.setDate(10, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 业务日期
			pst.setDouble(11, Double.parseDouble(gcsFactTradeMoney1)); // 交易金额
			pst.setDouble(12, 0); // 债券利息
			pst.setDouble(13, 0); // 成本单价
			pst.setString(14, cashAcountCode); // 现金账户
			pst.setDouble(15, 0); // 数量
			pst.setDouble(16, 0); // 锁定天数
			pst.setInt(17, 1); // 审核状态
			pst.setString(18, "GCS"); // 创建人
			pst.setString(19, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 创建时间
			pst.setString(20, "GCS"); // 审核人
			pst.setString(21, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 审核时间
			pst.setDouble(22, 0);
			pst.setString(23, GCSNum); // GCSnum
			pst.addBatch();

			pst.setString(1, "" + tradeNum);// 交易编号
			pst.setString(2, "44"); // 44 网下中签 交易类型
			pst.setString(3, securityCode); // 证券代码
			pst.setString(4, groupAndPort[1]); // 组合
			pst.setDate(5, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 成交日期
			pst.setString(6, "GP"); // 证券类型
			pst.setString(7, "C"); // 投资类型
			pst.setString(8, " "); // 投资经理
			pst.setString(9, "PONS"); // 所属分类
			pst
					.setDate(10, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 业务日期
			pst.setDouble(11, Double.parseDouble(gcsFactTradeMoney2)); // 交易金额
			pst.setDouble(12, 0); // 债券利息
			pst.setDouble(13, 0); // 成本单价
			pst.setString(14, cashAcountCode); // 现金账户
			pst.setDouble(15, Double.parseDouble(gcsFactTradeAmount2)); // 数量
			pst.setDouble(16, 0); // 锁定天数
			pst.setInt(17, 1); // 审核状态
			pst.setString(18, "GCS"); // 创建人
			pst.setString(19, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 创建时间
			pst.setString(20, "GCS"); // 审核人
			pst.setString(21, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 审核时间
			pst.setDouble(22, 0);
			pst.setString(23, GCSNum); // GCSnum
			pst.addBatch();

			pst.setString(1, "" + tradeNum);// 交易编号
			pst.setString(2, "42"); // 42 中签返款 交易类型
			pst.setString(3, securityCode); // 证券代码
			pst.setString(4, groupAndPort[1]); // 组合
			pst.setDate(5, new java.sql.Date(this.toDate(gcsBargainDate)
					.getTime())); // 成交日期
			pst.setString(6, "GP"); // 证券类型
			pst.setString(7, "C"); // 投资类型
			pst.setString(8, " "); // 投资经理
			pst.setString(9, "PONS"); // 所属分类
			pst
					.setDate(10, new java.sql.Date(this.toDate(gcsJSDate)
							.getTime())); // 业务日期
			pst.setDouble(11, YssD.sub(Double.parseDouble(gcsTradeMoney3),
					Double.parseDouble(gcsFactTradeMoney3))); // 退款金额
			pst.setDouble(12, 0); // 债券利息
			pst.setDouble(13, 0); // 成本单价
			pst.setString(14, cashAcountCode); // 现金账户
			pst.setDouble(15, 0); // 数量
			pst.setDouble(16, 0); // 锁定天数
			pst.setInt(17, 1); // 审核状态
			pst.setString(18, "GCS"); // 创建人
			pst.setString(19, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 创建时间
			pst.setString(20, "GCS"); // 审核人
			pst.setString(21, YssFun
					.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));// 审核时间
			pst.setDouble(22, 0);
			pst.setString(23, GCSNum); // GCSnum
			pst.addBatch();

			pst.executeBatch();
		} catch (Exception e) {
			this.setReplyCode("1");// 设置处理结果标识
			System.out.println("处理GCS网下新股新债数据出错：" + e.getMessage());
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println("处理GCS网下新股新债数据出错：" + e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 场内基金认购
	 * */
	private boolean dealFloorFundBuy(String[] groupAndPort, Element tradeData,
			String gcsAssetID, String state, String GCSNum) {

		PreparedStatement pst = null;
		String sql = "";
		boolean result = true;

		/**
		 * 填塞组合群前缀
		 * */
		this.getYssPub().setAssetGroupCode(groupAndPort[0]);
		this.getYssPub().setPrefixTB(groupAndPort[0]);

		try {
			this.getYssPub().setPortBaseCury();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("设置GCS组合的基础货币报错！");
		}

		/**
		 * 交易所代码判断 Dict_Market_GCS
		 * */
		String exchangeCode = tradeData.elementText("EXCH_NO"); // 交易所代码
		if (exchangeCode == null || exchangeCode.trim().length() == 0)
			return false;

		Hashtable<String, String> exchangeCodes = this.getMarket(
				groupAndPort[0], "Dict_Market_GCS");
		String qdExchangeCode = exchangeCodes.get(exchangeCode);
		qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim()
				.length() == 0) ? exchangeCode : qdExchangeCode;

		int markCount = this
				.getCountbySql(
						"select count(*) as FMarkCount from tb_"
								+ groupAndPort[0]
								+ "_Dao_Dict tdd "
								+ " where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = "
								+ this.getYssPub().getDbLink().sqlString(
										exchangeCode), "FMarkCount");

		if (markCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode);
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")接口字典Dict_Market_GCS中无法识别市场标志"
						+ exchangeCode);
			return false;
		}

		/**
		 * 成交证券 代码查询
		 * */
		String markCode = tradeData.elementText("SECU_NO"); // 上市代码
		if (markCode == null || markCode.trim().length() == 0)
			return false;

		String securityCode = "";

		int securityCount = this.getCountbySql(
				"select count(*) as FSecurityCount from tb_"
						+ groupAndPort[0]
						+ "_para_security "
						+ " where FCheckState=1 and FMarketCode="
						+ this.getYssPub().getDbLink().sqlString(markCode)
						+ " and FExchangeCode = "
						+ this.getYssPub().getDbLink()
								.sqlString(qdExchangeCode), "FSecurityCount");

		if (securityCount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")证券信息设置表中没有上市代码为" + markCode + "交易所为" + exchangeCode
						+ "的证券。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")证券信息设置表中没有上市代码为" + markCode + "交易所为"
						+ exchangeCode + "的证券。");
			return false;
		}

		securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0]
				+ "_para_security "
				+ " where FCheckState = 1 and FMarketCode = "
				+ this.getYssPub().getDbLink().sqlString(markCode)
				+ " and FExchangeCode = "
				+ this.getYssPub().getDbLink().sqlString(qdExchangeCode),
				"FSECURITYCODE");

		/**
		 * 货币 Dict_Curycode_GCS
		 * */
		String gcsCuryCode = tradeData.elementText("CURR");
		if (gcsCuryCode == null || gcsCuryCode.trim().length() == 0)
			return false;
		String curyCode = "";

		Hashtable<String, String> dictCurys = this.getMarket(groupAndPort[0],
				"Dict_Curycode_GCS");
		curyCode = dictCurys.get(gcsCuryCode);

		curyCode = (curyCode == null || curyCode.trim().length() == 0) ? gcsCuryCode
				: curyCode;

		// 交易类型
		String tmpTradeType = tradeData.elementText("TRANS_TYPE");
		if (tmpTradeType == null || tmpTradeType.trim().length() == 0)
			return false;

		String tradeType = "";

		if ("570".equalsIgnoreCase(tmpTradeType)) // 申请
			tradeType = "apply";
		else if ("572".equalsIgnoreCase(tmpTradeType)) // 确认
			tradeType = "confirm";
		else if ("571".equalsIgnoreCase(tmpTradeType)) // 退回
			tradeType = "return";

		/**
		 * 资金账号
		 * */
		String gcsGoundAcount = tradeData.elementText("FUND_ACT_NO");
		if (gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
			return false;
		String cashAcountCode = "";

		int cashAcccount = this.getAccCount(groupAndPort[0], gcsGoundAcount,
				curyCode);

		if (cashAcccount != 1) {
			this.setReplyCode("1");
			if (this.getReplyRemark() == null
					|| this.getReplyRemark().trim().length() == 0)
				this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID
						+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。");
			else
				this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"
						+ gcsAssetID + ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount
						+ "的现金帐户。");
			return false;
		}

		cashAcountCode = this.getCashAcc(groupAndPort[0], gcsGoundAcount,
				curyCode);

		/**
		 * 交易日期 / 申请日期 / 确认日期
		 * */
		String gcsBargainDate = tradeData.elementText("TRANS_DATE");
		if (gcsBargainDate == null || gcsBargainDate.trim().length() == 0)
			return false;

		/* 认购申请 */
		if ("570".equalsIgnoreCase(tmpTradeType)) {
			/**
			 * 成交金额
			 * */
			String gcsTradeMoney = tradeData.elementText("DEAL_AMT");

			if (gcsTradeMoney == null)
				gcsTradeMoney = "0";
			else if (gcsTradeMoney.trim().length() == 0)
				gcsTradeMoney = "0";

			try {
				String tradeNums = this
						.getDatabySql(
								"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
										+ groupAndPort[0]
										+ "_DATA_OPENFUNDTRADE tdst "
										+ " where FDataType in('apply','return' , 'confirm') and FGCSNum = "
										+ this.getYssPub().getDbLink()
												.sqlString(GCSNum), "FNUM");

				if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
					this.deleteOpenFundData(groupAndPort[0], tradeNums, this
							.toDate(gcsBargainDate), this
							.toDate(gcsBargainDate), securityCode,
							groupAndPort[1], "", "04", // 交易类型为认购
							false, "", "apply,return,confirm");
				}

				sql = "insert into TB_"
						+ groupAndPort[0]
						+ "_DATA_OPENFUNDTRADE ("
						+ " FNUM,FDATATYPE,FTRADETYPECODE,FPORTCODE,FSECURITYCODE,FBARGAINDATE,FINVESTTYPE,FINVMGRCODE," +
						"FAPPLYDATE,FAPPLYCASHACCCODE,"
						+ " FAPPLYMONEY,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,FGCSNum"
						+ ") values (?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?"
						+ ")";
				pst = this.getConnection().prepareStatement(sql);

				pst.setString(1, this.getOpenFundTradeNum(groupAndPort[0],
						gcsBargainDate)); // 开放式基金交易数据编号
				pst.setString(2, tradeType); // 数据类型 为申请数据
				pst.setString(3, "04"); // 交易类型
				pst.setString(4, groupAndPort[1]); // 组合
				pst.setString(5, securityCode); // 证券代码
				pst.setDate(6, new java.sql.Date(this.toDate(gcsBargainDate)
						.getTime())); // 成交日期
				pst.setString(7, "C"); // 投资类型
				pst.setString(8, " "); // 投资经理代码
				pst.setDate(9, new java.sql.Date(this.toDate(gcsBargainDate)
						.getTime())); // 申请日期
				pst.setString(10, cashAcountCode); // 申请帐户
				pst.setDouble(11, Double.parseDouble(gcsTradeMoney)); // 申请金额
				pst.setInt(12, 1); // 审核状态
				pst.setString(13, "GCS"); // 创建人
				pst.setString(14, this.formatDate(new Date(),
						"yyyyMMdd HH:mm:ss"));// 创建时间
				pst.setString(15, "GCS"); // 审核人
				pst.setString(16, this.formatDate(new Date(),
						"yyyyMMdd HH:mm:ss"));// 审核时间
				pst.setString(17, GCSNum);
				pst.executeUpdate();
			} catch (Exception e) {
				this.setReplyCode("1"); // 设置处理结果标识
				System.out.println("生成场内基金认购返款的业务数据或删除相关联数据时出错："
						+ e.getMessage());
			} finally {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
			/* 认购确认 */
		} else if ("572".equalsIgnoreCase(tmpTradeType)) {
			String tradeAmount = tradeData.elementText("ACTU_DELIV_QUAN"); // 实际交收数量
			String taxFee = tradeData.elementText("TAX_FEE"); // 税费
			String commFee = tradeData.elementText("COMM_FEE"); // 佣金
			String otherFee = tradeData.elementText("OTHER_FEE"); // 其他费用
			String factTradeMoney = tradeData.elementText("ACTU_DELIV_AMT"); // 实际交收金额
			try {
				/**
				 * 查找是否有对应的申请数据
				 * */
				String applyDatas = this.getDatasbySql(" select * from TB_"
						+ groupAndPort[0] + "_DATA_OPENFUNDTRADE "
						+ " where FDataType = 'apply' and FGCSNum = "
						+ this.getYssPub().getDbLink().sqlString(GCSNum),
						"FNum,FGCSNum,FBARGAINDATE,FAPPLYMONEY");
				if (applyDatas != null && applyDatas.trim().length() > 0) {
					String[] applyData = applyDatas.split(",");

					/**
					 * 删除存在的确认数据
					 * */
					String tradeNums = this
							.getDatabySql(
									"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
											+ groupAndPort[0]
											+ "_DATA_OPENFUNDTRADE tdst "
											+ " where FDataType in('confirm') and FGCSNum = "
											+ this.getYssPub().getDbLink()
													.sqlString(GCSNum), "FNUM");

					if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
						this.deleteOpenFundData(groupAndPort[0], tradeNums,
								this.toDate(applyData[2].substring(0, 10)),
								this.toDate(applyData[2].substring(0, 10)), securityCode,
								groupAndPort[1], "", "04", // 交易类型为认购
								false, "", "confirm");
					}

					sql = "insert into TB_"
							+ groupAndPort[0]
							+ "_DATA_OPENFUNDTRADE ("
							+ " FNUM,FDATATYPE,FTRADETYPECODE,FPORTCODE,FSECURITYCODE,FBARGAINDATE,FINVESTTYPE," +
							"FINVMGRCODE,FComfDate,"
							+ " FComfMoney,FComfAmount,FComfFee,FTradePrice,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER," +
							"FCHECKTIME,FGCSNum"
							+ ") values (?,?,?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,?,?,?,?,?" + ")";

					pst = this.getConnection().prepareStatement(sql);
					pst.setString(1, applyData[0]); // 开放式基金交易数据编号
					pst.setString(2, tradeType); // 数据类型 为中签数据
					pst.setString(3, "04"); // 交易类型
					pst.setString(4, groupAndPort[1]); // 组合
					pst.setString(5, securityCode); // 证券代码
					pst.setDate(6, new java.sql.Date(this.toDate(
							applyData[2].substring(0, 10)).getTime())); // 成交日期
					pst.setString(7, "C"); // 投资类型
					pst.setString(8, " "); // 投资经理代码
					pst.setDate(9, new java.sql.Date(this
							.toDate(gcsBargainDate).getTime())); // 确认日期
					pst.setDouble(10, YssD.sub(Double
							.parseDouble(factTradeMoney), Double
							.parseDouble(otherFee)));// 确认金额 = 实际交收金额 - 其它费用
					pst
							.setDouble(
									11,
									Double
											.parseDouble((tradeAmount == null || tradeAmount
													.trim().length() == 0) ? "0"
													: tradeAmount));// 确认数量
					pst.setDouble(12, YssD.add(Double.parseDouble(taxFee),
							Double.parseDouble(commFee), Double
									.parseDouble(otherFee))); // 确认交易费用
					pst.setDouble(13, YssD.div(YssD.sub(Double
							.parseDouble(factTradeMoney), Double
							.parseDouble(otherFee)), Double
							.parseDouble((tradeAmount == null || tradeAmount
									.trim().length() == 0) ? "0"
											: tradeAmount))); // 确认价格 = 确认金额 / 交收数量
					pst.setInt(14, 1); // 审核状态
					pst.setString(15, "GCS"); // 创建人
					pst.setString(16, this.formatDate(new Date(),
							"yyyyMMdd HH:mm:ss"));// 创建时间
					pst.setString(17, "GCS"); // 审核人
					pst.setString(18, this.formatDate(new Date(),
							"yyyyMMdd HH:mm:ss"));// 审核时间
					pst.setString(19, GCSNum);

					pst.executeUpdate();
				}
			} catch (Exception e) {
				this.setReplyCode("1"); // 设置处理结果标识
				System.out.println("生成场内基金认购返款的业务数据或删除相关联数据时出错："
						+ e.getMessage());
			} finally {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}

			/* 认购退还 */
		} else if ("571".equalsIgnoreCase(tmpTradeType)) {
			String taxFee = tradeData.elementText("TAX_FEE"); // 税费
			String commFee = tradeData.elementText("COMM_FEE"); // 佣金
			String otherFee = tradeData.elementText("OTHER_FEE"); // 其他费用
			String factTradeMoney = tradeData.elementText("ACTU_DELIV_AMT"); // 实际交收金额
			String settlementDate = tradeData.elementText("FUND_DELIV_DATE"); // 资金交收日期
			try {
				/**
				 * 查找是否有对应的申请数据
				 * */
				String applyDatas = this.getDatasbySql(" select * from TB_"
						+ groupAndPort[0] + "_DATA_OPENFUNDTRADE "
						+ " where FDataType = 'apply' and FGCSNum = "
						+ this.getYssPub().getDbLink().sqlString(GCSNum),
						"FNum,FGCSNum,FBARGAINDATE,FAPPLYMONEY");
				if (applyDatas != null && applyDatas.trim().length() > 0) {
					String[] applyData = applyDatas.split(",");

					/**
					 * 删除存在的确认数据
					 * */
					String tradeNums = this
							.getDatabySql(
									"select WMSYS.WM_CONCAT(tdst.fnum) as FNUM from tb_"
											+ groupAndPort[0]
											+ "_DATA_OPENFUNDTRADE tdst "
											+ " where FDataType in('return' , 'confirm') and FGCSNum = "
											+ this.getYssPub().getDbLink()
													.sqlString(GCSNum), "FNUM");

					if (tradeNums.trim().length() > 0) { // 存在历史数据 先删除
						this.deleteOpenFundData(groupAndPort[0], tradeNums,
								this.toDate(applyData[2].substring(0, 10)), this
										.toDate(applyData[2].substring(0, 10)), securityCode,
								groupAndPort[1], "", "04", // 交易类型为认购
								false, "", "return");
					}

					sql = "insert into TB_"
							+ groupAndPort[0]
							+ "_DATA_OPENFUNDTRADE ("
							+ " FNUM,FDATATYPE,FTRADETYPECODE,FPORTCODE,FSECURITYCODE,FBARGAINDATE,FINVESTTYPE," +
							"FINVMGRCODE,FReturnDate,FRtnCashAccCode,"
							+ " FComfFee,FReturnMoney,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME,FGCSNum"
							+ ") values (?,?,?,?,?,?,?,?,?,"
							+ "?,?,?,?,?,?,?,?,?" + ")";

					pst = this.getConnection().prepareStatement(sql);
					pst.setString(1, applyData[0]); // 开放式基金交易数据编号
					pst.setString(2, "return"); // 数据类型 为退保数据
					pst.setString(3, "04"); // 交易类型
					pst.setString(4, groupAndPort[1]); // 组合
					pst.setString(5, securityCode); // 证券代码
					pst.setDate(6, new java.sql.Date(this.toDate(
							applyData[2].substring(0, 10)).getTime())); // 成交日期
					pst.setString(7, "C"); // 投资类型
					pst.setString(8, " "); // 投资经理代码
					pst.setDate(9, new java.sql.Date(this
							.toDate(settlementDate).getTime())); // 返款日期
					pst.setString(10, cashAcountCode); // 返还账户
					pst.setDouble(11, YssD.add(Double.parseDouble(taxFee),
							Double.parseDouble(commFee), Double
									.parseDouble(otherFee))); // 退还费用
					pst.setDouble(12, Double.parseDouble(factTradeMoney)); // 实际交收金额
					pst.setInt(13, 1); // 审核状态
					pst.setString(14, "GCS"); // 创建人
					pst.setString(15, this.formatDate(new Date(),
							"yyyyMMdd HH:mm:ss"));// 创建时间
					pst.setString(16, "GCS"); // 审核人
					pst.setString(17, this.formatDate(new Date(),
							"yyyyMMdd HH:mm:ss"));// 审核时间
					pst.setString(18, GCSNum);

					pst.executeUpdate();
				}
			} catch (Exception e) {
				this.setReplyCode("1"); // 设置处理结果标识
				System.out.println("生成场内基金认购返款的业务数据或删除相关联数据时出错："
						+ e.getMessage());
			} finally {
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * 获取日期类型的数据
	 * */
	private Date getDateTypeDataBySql(String sql, String field) {
		ResultSet rs = null;
		java.util.Date date = null;
		PreparedStatement psp = null;
		try {
			psp = this.getConnection().prepareStatement(sql);
			rs = psp.executeQuery();
			if (rs.next()) {
				if (rs.getDate(field) != null)
					date = new java.util.Date(rs.getDate(field).getTime());
			}
			rs.close();
			psp.close();
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return date;
	}

	/**
	 * 获取日期类型的数据
	 * */
	protected void executeSql(String sql) {
		Statement stat = null;
		try {
			stat = this.getConnection().createStatement();
			stat.execute(sql);
		} catch (Exception e) {
			System.out.print(e.getMessage());
		} finally {
			try {
				if (stat != null)
					stat.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取交易编号
	 * */
	protected String getSubTradeNum(String groupCode, String bargainDate,
			String settleType) {
		String fNumDate = "";
		String subNum = "";
		if (settleType.equalsIgnoreCase("01"))
			settleType = "200000";
		else if (settleType.equalsIgnoreCase("02"))
			settleType = "900000";

		YssDbFun dbFun = new YssDbFun(this.getYssPub());

		fNumDate = YssFun.formatDatetime(this.toDate(bargainDate)).substring(0,
				8);

		try {
			subNum = fNumDate
					+ dbFun.getNextInnerCode("Tb_" + groupCode + "_Data_Trade",
							this.getYssPub().getDbLink().sqlRight("FNUM", 6),
							settleType, " where FNum like 'T" + fNumDate
									+ settleType.substring(0, 1) + "%'", 1);
			subNum = "T" + subNum;

			subNum = subNum
					+ dbFun.getNextInnerCode("tb_" + groupCode
							+ "_Data_SubTrade", this.getYssPub().getDbLink()
							.sqlRight("FNUM", 5), "00000", " where FNum like '"
							+ subNum.replaceAll("'", "''") + "%'");
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("生成GCS交易数据编号出错：" + e.getMessage());
			return "";
		}
		return subNum;
	}

	/**
	 * 获取银行间债券交易编号
	 * */
	protected String getIntBakBondNum(String bargainDate) {
		String num = "";
		InterBankBondTradeAdmin interBankBondTradeAdmin = new InterBankBondTradeAdmin();
		interBankBondTradeAdmin.setYssPub(this.getYssPub());
		InterBankBondTradeBean interBankBondTrade = new InterBankBondTradeBean();
		// interBankBondTrade.setYssPub(this.getYssPub());
		interBankBondTrade.setStrBargainDate(this.formatDate(this
				.toDate(bargainDate), "yyyy-MM-dd"));
		interBankBondTradeAdmin.setBondTrade(interBankBondTrade);
		try {
			num = interBankBondTradeAdmin.getNum();
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("生成GCS银行间债券交易编号出错:" + e.getMessage());
			return "";
		}
		return num;
	}

	/**
	 * 生成转托数据交易编号
	 * */
	private String getBondTransFerNum(String bargaindate) {
		YssDbFun dbFun = new YssDbFun(this.getYssPub());
		String strNum = "";
		String strNumDate = YssFun.formatDatetime(this.toDate(bargaindate))
				.substring(0, 8);
		try {
			strNum = strNumDate
					+ dbFun.getNextInnerCode(this.getYssPub().yssGetTableName(
							"Tb_Data_DevTrustBond"), this.getYssPub()
							.getDbLink().sqlRight("FNUM", 9), "000000000",
							" where FNum like 'DTB" + strNumDate + "%'", 1);
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("生成GCS转托数据交易编号出错:" + e.getMessage());
		}
		strNum = "DTB" + strNum;

		return strNum;
	}

	/**
	 * 生成外汇交易编号
	 * */
	private String getRateTradeNum(String settleDate, String tabName) {
		YssDbFun dbFun = new YssDbFun(this.getYssPub());
		String num = "";
		String strNumberDate = this.formatDate(this.toDate(settleDate),
				YssCons.YSS_DATETIMEFORMAT).substring(0, 8);

		try {
			num = "T"
					+ strNumberDate
					+ dbFun.getNextInnerCode(tabName, this.getYssPub()
							.getDbLink().sqlRight("FNum", 6), "000001",
							" where FNum like 'T" + strNumberDate + "%'", 1);
		} catch (Exception e) {
			this.setReplyCode("1");
			System.out.println("生成GCs外汇交易编出错：" + e.getMessage());
		}
		return num;
	}

	/**
	 * 获取回购到期日期 dateType : maturadate , maturaSettledate 到期日期，到期结算日期
	 * */
	private Date getMatureAddDate(String bargainDate, String sercurityCode,
			String dateType) {
		Date matureDate = null; // 到期日期
		String tmpStr = "";
		int duraTion = 0;
		int durUnit = 0;

		TradeSubBean tradeSub = new TradeSubBean();
		tradeSub.setYssPub(this.getYssPub());
		tradeSub.setSecurityCode(sercurityCode);
		try {
			tmpStr = tradeSub.getOperValue("GetDurability"); // 获取证券期限

			if (tmpStr != null && tmpStr.indexOf("\t") > -1) {
				duraTion = Integer.parseInt(tmpStr.split("\t")[0]);
				durUnit = Integer.parseInt(tmpStr.split("\t")[1]);
				/**
				 * 转换QD系统 年月周日的常量标识转换
				 * */
				if (durUnit == 0) // 天
					durUnit = 5;
				else if (durUnit == 1) // 周
					durUnit = 3;
				else if (durUnit == 2) // 月
					durUnit = 2;
				else if (durUnit == 3) // 年
					durUnit = 1;

				matureDate = YssFun.addDate(this.toDate(bargainDate), duraTion,
						durUnit);
			}

			// 获取结算类型
			tmpStr = this.getDatasbySql("select * from "
					+ this.getYssPub().yssGetTableName("Tb_Para_Security")
					+ " where FSecurityCode = "
					+ this.getYssPub().getDbLink().sqlString(sercurityCode),
					"FSettleDayType,FSettleDays");
			if ("maturadate".equalsIgnoreCase(dateType)) {
				// 到期日期
				if (tmpStr != null && tmpStr.indexOf(",") > -1) {
					if ("0".equalsIgnoreCase(tmpStr.split(",")[0])) { // 结算类型为工作日
						tradeSub.setMatureDate(this.formatDate(matureDate,
								"yyyy-MM-dd")); // 到期日期

						if ("1".equalsIgnoreCase(tradeSub
								.getOperValue("isWorkDay"))) {
							String tmpMatureDate = tradeSub
									.getOperValue("getHolidays");

							if (tmpMatureDate != null)
								matureDate = this.toDate(tmpMatureDate);
						}
					}
				}
			} else if ("maturaSettledate".equalsIgnoreCase(dateType)) {
				// 到期结算日期
				if (tmpStr != null && tmpStr.indexOf(",") > -1) {

					matureDate = YssFun.addDate(this.toDate(this.formatDate(
							matureDate, "yyyy-MM-dd")), Integer.parseInt(tmpStr
							.split(",")[1]), 5);
					tradeSub.setMatureSettleDate(this.formatDate(matureDate,
							"yyyy-MM-dd"));
					if ("0".equalsIgnoreCase(tmpStr.split(",")[0])) { // 结算类型为工作日
						tradeSub.setMatureDate(this.formatDate(matureDate,
								"yyyy-MM-dd")); // 到期日期

						if ("1".equalsIgnoreCase(tradeSub
								.getOperValue("isWorkDay"))) { // 到期结算日期是否为工作日
							String tmpMatureDate = tradeSub
									.getOperValue("getHolidays");
							if (tmpMatureDate != null)
								matureDate = this.toDate(tmpMatureDate);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("GCS处理国内场内_回购获取回购到期日期出错：" + e.getMessage());
		}
		return matureDate;
	}

	/**
	 * 计算交易数据实收实付
	 * */
	private String getTotalCost(Hashtable<String, String> htFees,
			String groupAssetcode, String securitycode, String tradeType,
			String interest, String tradeMoney) {

		double nFeeMoney = 0;
		double totalMoney = 0;

		Enumeration<String> enumEration = htFees.keys();
		while (enumEration.hasMoreElements()) {
			String feeCode = enumEration.nextElement();
			String tmpFeeMoney = htFees.get(feeCode);
			totalMoney = YssD.add(totalMoney, Double.parseDouble(tmpFeeMoney));

			String isSettle = this.getDatabySql("select * from Tb_"
					+ groupAssetcode
					+ "_Para_Fee where Fcheckstate = 1 and FFeeCode = "
					+ this.getYssPub().getDbLink().sqlString(feeCode),
					"FIsSettle");
			// 不计入清算金的费用
			if ("0".equalsIgnoreCase(isSettle)) {
				nFeeMoney = YssD
						.add(nFeeMoney, Double.parseDouble(tmpFeeMoney));
			}
		}

		SecurityBean security = new SecurityBean();
		security.setYssPub(this.getYssPub());
		security.setSectorCode(securitycode);
		security.getSetting();
		if (security != null) {
			// 如果满足这个条件的话 那么实收金额 就是费用
			if ("FU".equalsIgnoreCase(security.getStrCategoryCode())
					&& "FU01"
							.equalsIgnoreCase(security.getStrSubCategoryCode())) {
				if (interest != null && interest.trim().length() > 0) {
					totalMoney = YssD.add(totalMoney, Double
							.parseDouble(interest)); // 实收实付加上利息
				}
			}
			return "" + totalMoney;
		}

		if (tradeMoney != null && tradeMoney.trim().length() > 0
				&& tradeType != null && tradeType.trim().length() > 0) {
			// 交易类型为认沽权证行权（31）的实收金额 实收金额 = 交易金额 - 费用
			if ("02".equalsIgnoreCase(tradeType)
					|| "61".equalsIgnoreCase(tradeType)
					|| "31".equalsIgnoreCase(tradeType)
					|| "24".equalsIgnoreCase(tradeType)
					|| "02SS".equalsIgnoreCase(tradeType)) {
				totalMoney = YssD.round(YssD.sub(
						Double.parseDouble(tradeMoney), totalMoney), 2);
			} else {
				totalMoney = YssD.round(YssD.add(
						Double.parseDouble(tradeMoney), totalMoney), 2);
			}
			if (security != null) {
				if ("RE".equalsIgnoreCase(security.getStrCategoryCode())) {
					if (interest != null && interest.trim().length() > 0)
						totalMoney = YssD.add(totalMoney, Double
								.parseDouble(interest));
				}
			}
		} else {
			totalMoney = 0;
		}

		if (nFeeMoney > 0) {
			String cashInd = this
					.getDatabySql(
							"select * from tb_base_tradetype tbt where tbt.ftradetypecode = "
									+ this.getYssPub().getDbLink().sqlString(
											tradeType), "FCashInd");
			if ("1".equalsIgnoreCase(cashInd)) {
				if ("31".equalsIgnoreCase(tradeType)) {
					totalMoney = YssD.add(totalMoney, nFeeMoney);
				}
			} else if ("-1".equalsIgnoreCase(cashInd)) {
				totalMoney = YssD.sub(totalMoney, nFeeMoney);
			}
		}
		return "" + totalMoney;
	}

	/**
	 * 获取银行间债券回购编号
	 * */
	private String getBanksPurchaseNum(String groupCode, String bargainDate) {
		YssDbFun dbFun = new YssDbFun(this.getYssPub());
		String strNumDate = YssFun.formatDatetime(this.toDate(bargainDate))
				.substring(0, 8);
		String num = "";
		try {
			num = strNumDate
					+ dbFun.getNextInnerCode("Tb_" + groupCode
							+ "_Data_Purchase", this.getYssPub().getDbLink()
							.sqlRight("FNUM", 6), "000000",
							" where FNum like 'T" + strNumDate + "%'", 1);
			num = "T" + num;
		} catch (Exception e) {
			System.out.println("获取银行间债券回购编号出错：" + e.getMessage());
		}
		return num;
	}

	/**
	 * 获取开放式基金交易数据编号
	 * */
	private String getOpenFundTradeNum(String groupCode, String bargainDate) {
		YssDbFun dbFun = new YssDbFun(this.getYssPub());
		String strNumDate = this.formatDate(this.parseDate(bargainDate,
				"yyyyMMdd"), "yyyyMMdd");
		String num = "";
		try {
			num = "OTC"
					+ strNumDate
					+ dbFun.getNextInnerCode(this.getYssPub().yssGetTableName(
							"Tb_Data_OpenFundTrade"), this.getYssPub()
							.getDbLink().sqlRight("FNUM", 6), "000000000",
							" where FNum like 'OTC" + strNumDate + "%'", 1);
		} catch (Exception e) {
			System.out.println("获取开放式基金交易数据编号出错：" + e.getMessage());
		}
		return num;
	}

	// 删除开放式基金交易数据的方法
	public void deleteOpenFundData(String groupCode, String sNums,
			java.util.Date beginTradeDate, java.util.Date endTradeDate,
			String sSecurityCode, String sPortCode, String sInvMgrCode,
			String sTradeType, boolean needNums, String sDsType, String dataType)
			throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String sWhereSql = "";
		String[] sSubNums = null;
		String nums = "";
		YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

		try {
			sWhereSql = this.buildWhereSql(sNums, beginTradeDate, endTradeDate,
					sSecurityCode, sPortCode, sInvMgrCode, sTradeType, sDsType,
					dataType);

			if (sWhereSql.trim().length() == 0
					|| sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
				return;
			}
			if (needNums) {
				if (sNums.length() > 0) {
					sSubNums = sNums.split(",");
					for (int i = 0; i < sSubNums.length; i++) {
						strSql = "select distinct FNum from "
								+ "Tb_"
								+ groupCode
								+ "_Data_OpenFundTrade"
								+ " where FNum like '"
								+ sSubNums[i].substring(0,
										sSubNums[i].length() - 9)
								+ "%' "
								+ (dataType.trim().length() > 0 ? " and FDataType in ("
										+ operSql.sqlCodes(this.getYssPub()
												.getDbLink()
												.sqlString(dataType)) + ")"
										: "");
						rs = this.getYssPub().getDbLink().openResultSet(strSql);
						while (rs.next()) {
							nums += rs.getString("FNum") + ",";
						}
						this.getYssPub().getDbLink().closeResultSetFinal(rs);
					}
				}

				if (nums.trim().length() > 0) {
					nums = operSql.sqlCodes(nums);
					strSql = "delete from "
							+ "Tb_"
							+ groupCode
							+ "_Data_OpenFundTrade"
							+ " where FNum in ("
							+ nums
							+ ") "
							+ (dataType.trim().length() > 0 ? " and FDataType in ("
									+ operSql.sqlCodes(this.getYssPub()
											.getDbLink().sqlString(dataType))
									+ ")"
									: "");
					this.getYssPub().getDbLink().executeSql(strSql);
				}
			} else {
				strSql = "select a.FNum as FZNum from " + "Tb_" + groupCode
						+ "_Data_OpenFundTrade" + " a " + sWhereSql;
				rs = this.getYssPub().getDbLink().openResultSet(strSql);
				while (rs.next()) {
					nums += rs.getString("FZNum") + ",";
				}
				this.getYssPub().getDbLink().closeResultSetFinal(rs);
				nums = operSql.sqlCodes(nums);
				// 国内权益处理---
				strSql = "delete from "
						+ "Tb_"
						+ groupCode
						+ "_Data_OpenFundTrade"
						+ " where FNum in ("
						+ nums
						+ ") "
						+ (dataType.trim().length() > 0 ? " and FDataType in ("
								+ operSql.sqlCodes(dataType) + ")" : "");
				this.getYssPub().getDbLink().executeSql(strSql);
			}

			if (nums.equals("''")) {
				return;
			}
// 若反审核开放式基金业务数据，则之前由该数据生成的综合业务数据、证券应收应付数据、现金应收应付数据、资金调拨数据,交易数据全部删除
			String strSub = " delete from " + "Tb_" + groupCode
					+ "_Data_Integrated" + " where FRelaNum in (" + nums
					+ ") and FNumType = 'openfund' ";
			this.getYssPub().getDbLink().executeSql(strSub);

			strSub = " delete from " + "Tb_" + groupCode + "_Data_Secrecpay"
					+ " where FRelaNum in (" + nums
					+ ") and FRelaType = 'openfund' ";
			this.getYssPub().getDbLink().executeSql(strSub);

			strSub = " delete from " + "Tb_" + groupCode + "_Data_Cashpayrec"
					+ " where FRelaNum in (" + nums
					+ ") and FRelaType = 'openfund' ";
			this.getYssPub().getDbLink().executeSql(strSub);

			strSub = " delete from " + "Tb_" + groupCode + "_Cash_Subtransfer"
					+ " where FNum in( select FNum from " + "tb_" + groupCode
					+ "_cash_transfer" + " where FRelaNum in (" + nums
					+ ") and FNumType = 'openfund' ) ";
			this.getYssPub().getDbLink().executeSql(strSub);

			strSub = " delete from " + "tb_" + groupCode + "_cash_transfer"
					+ " where FRelaNum in (" + nums
					+ ") and FNumType = 'openfund' ";
			this.getYssPub().getDbLink().executeSql(strSub);
			// 删除业务处理产生的交易数据
			strSql = "select a.FNum as FZNum,b.FNum as FSNum from "
					+ "Tb_"
					+ groupCode
					+ "_Data_SubTrade"
					+ " a left join (select * from "
					+ "Tb_"
					+ groupCode
					+ "_Data_Trade"
					+ ") b on a.FSecurityCode = b.FSecurityCode  and a.FBrokerCode = b.FBrokerCode"
					+ " and a.FInvMgrCode = b.FInvMgrCode and a.FBargainDate = b.FBargainDate " +
					" and a.ftradetypecode = b.ftradetypecode"
					+ " where a.ftradetypecode='39' and a.fdealnum in (" + nums
					+ ")";
			rs = this.getYssPub().getDbLink().openResultSet(strSql);
			String FZNum = "";
			String FSNum = "";
			while (rs.next()) {
				FZNum += rs.getString("FZNum") + ",";
				FSNum += rs.getString("FSNum") + ",";
			}
			FZNum = operSql.sqlCodes(FZNum);
			FSNum = operSql.sqlCodes(FSNum);
			strSub = "delete from " + "Tb_" + groupCode + "_Data_SubTrade"
					+ " where fnum in (" + FZNum + ")";
			this.getYssPub().getDbLink().executeSql(strSub);
			strSub = "delete from " + "Tb_" + groupCode + "_Data_Trade"
					+ " where fnum in (" + FSNum + ")";
			this.getYssPub().getDbLink().executeSql(strSub);
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			this.getYssPub().getDbLink().closeResultSetFinal(rs);
		}
	}

	/**
	 * 开放式基金查询条件
	 * */
	private String buildWhereSql(String sNums, java.util.Date beginTradeDate,
			java.util.Date endTradeDate, String sSecurityCode,
			String sPortCode, String sInvMgrCode, String sTradeType,
			String sDsType, String dataType) {
		YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());

		String sResult = " where 1=1 ";

		if (sNums.length() > 0) {
			sResult += " and a.FNum in(" + operSql.sqlCodes(sNums) + ")";
		}

		if (beginTradeDate != null && endTradeDate == null) {
			sResult += " and a.FBargainDate = "
					+ this.getYssPub().getDbLink().sqlDate(beginTradeDate);
		}

		if (beginTradeDate != null && endTradeDate != null) {
			sResult += " and a.FBargainDate between "
					+ this.getYssPub().getDbLink().sqlDate(beginTradeDate)
					+ " and "
					+ this.getYssPub().getDbLink().sqlDate(endTradeDate);
		}

		/*if (sSecurityCode.length() > 0) {
			sResult += " and a.FSecurityCode in("
					+ operSql.sqlCodes(sSecurityCode) + ")";
		}*/

		if (sPortCode.length() > 0) {
			sResult += " and a.FPortCode in( " + operSql.sqlCodes(sPortCode)
					+ ")";
		}

		if (sInvMgrCode.length() > 0) {
			sResult += " and a.FInvMgrCode = "
					+ this.getYssPub().getDbLink().sqlString(sInvMgrCode);
		}

		if (sTradeType.length() > 0) {
			sResult += " and a.FTradeTypeCode in( "
					+ operSql.sqlCodes(sTradeType) + ")";
		}

		if (sDsType.length() > 0) {
			if ("QY_CL".equalsIgnoreCase(sDsType)) {
				sResult += " and a.FDataBirth= "
						+ this.getYssPub().getDbLink().sqlString(sDsType);// 如果为权益处理时
																			// FDataBirth的值为QY_CL
			} else {
				sResult += " and (a.FDataBirth= "
						+ this.getYssPub().getDbLink().sqlString(sDsType)
						+ " or a.FDataBirth is null)";
			}
		}

		if (dataType != null && dataType.trim().length() > 0) {
			sResult += " and a.FDataType in ( " + operSql.sqlCodes(dataType)
					+ ")";
		}
		return sResult;
	}

	/**
	 * 获取往下新股新债编号
	 * */
	private String getNewIssueTradeNum(String groupCode, Date bargainDate) {
		String strNumberDate = "";
		String sNum = "";
		YssDbFun dbFun = new YssDbFun(this.getYssPub());
		try {
			strNumberDate = this.formatDate(bargainDate, "yyyyMMdd").substring(
					0, 8);
			sNum = "NSB"
					+ strNumberDate
					+ dbFun.getNextInnerCode(this.getYssPub().yssGetTableName(
							"Tb_Data_NewIssueTrade"), this.getYssPub()
							.getDbLink().sqlRight("FNum", 9), "000000000",
							" WHERE FBargainDate ="
									+ this.getYssPub().getDbLink().sqlDate(
											bargainDate));
		} catch (Exception e) {
			System.out.println("获取往下新股新债编号：" + e.getMessage());
		}
		return sNum;
	}
}
