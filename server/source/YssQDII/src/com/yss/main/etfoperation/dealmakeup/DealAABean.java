package com.yss.main.etfoperation.dealmakeup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.main.etfoperation.etfaccbook.CreateAccBookRefData;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class DealAABean extends CtlDealMakeUp {
	
	/**
	 * 构造函数
	 */
	public DealAABean() {
		hmHoliday = super.getHmHoliday();
		hmbasket = super.getHmbasket();
		etfParam = super.getEtfParam();
		hmCurrentSGInfo = super.getHmCurrentSGInfo();
		hmTradeInfo = super.getHmTradeInfo();
		hmSumMakeUpInfos = super.getHmSumMakeUpInfos();
		hmMaxNum = super.getHmMaxNum();
		hmMakeUpInfo = super.getHmMakeUpInfo();
		hmMaxRefNum = super.getHmMaxRefNum();
		tradeDate = super.getTradeDate();
	}
	
	/**
	 * 根据均摊的补票方式处理需要补票的数据
	 * @throws YssException
	 */
	public HashMap dealAA(ETFTradeSettleDetailBean tradeSettleDel)throws YssException{
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;
		ETFTradeSettleDetailRefBean currentSGtser = null;
		ETFTradeSettleDetailRefBean sumTradeSettleDelRef = null;
		ETFTradeSettleDetailBean sumTradeSetDel = null;
		ETFTradeSettleDetailBean tradeSetDel = null;
		String num = ""; // 申请编号
		String portCode = ""; // 组合代码
		String securityCode = ""; // 证券代码
		String bs = ""; // 买卖标志
		java.util.Date buyDate = null; // 申赎日期
		double replaceAmount = 0; // 替代数量
		double oreplaceCash = 0; // 替代金额（原币）
		double hreplaceCash = 0; // 替代金额（本币）
		double ocreplaceCash = 0; // 可退替代款（原币）
		double hcreplaceCash = 0; // 可退替代款（本币）
		double price = 0;//估值行情
		java.util.Date marketValueDate = null;//行情日期

		java.util.Date refMakeUpDate = null; // 补票日期
		double refUnitCost = 0; // 单位成本
		double refOMakeUpCost = 0;// 补票成本（原币）
		double refHMakeUpCost = 0;// 补票成本（本币）
		double refMakeUpAmount = 0; // 补票数量
		double refOPReplaceCash = 0; // 应付替代款（原币）
		double refHPReplaceCash = 0; // 应付替代款（本币）
		double refOCReplaceCash = 0; // 可退替代款（原币）
		double refHCReplaceCash = 0; // 可退替代款（本币）
		double refSumAmount = 0; // 总数量
		double refInterest = 0; // 派息
		double refWarrantCost = 0; // 权证价值
		double refRemaindAmount = 0; // 剩余数量
		double refOCRefundSum = 0; // 应退合计（原币） 也叫做应付替代款（原币）合计
		double refHCRefundSum = 0; // 应退合计（本币） 也叫做应付替代款（本币）合计
		double refOCanRepCash = 0; // 可退替代款（原币）合计
		double refHCanRepCash = 0; // 可退替代款（本币）合计
		int refDataDirection = 0; // 数据方向 申购 -- 1 赎回 -- -1
		String refSettleMark = ""; // 清算标识 清算 -- Y 未清算 -- N
		java.util.Date refRefundDate = null;// 退款日期

		ETFParamSetBean paramSet = null;// ETF参数的实体类

		ArrayList alTradeInfo = null;// 用于储存相关证券的交易数据
		TradeSubBean tradeSub = null;// 交易子表实体类
		int startDateNum = 0;// 从几个工作日开始补票
		java.util.Date canMakeUpDate = null;// 可以开始补票的日期
		BaseOperDeal operDeal = null;
		double sumAmount = 0;// 总的交易数量
		double sumTradeAmount = 0;// 计算过程中用到的加总的交易数量
		double detailAmount = 0;// 明细的交易数量
		double remaindTradeAmount = 0;// 剩余的交易数量
		double detailTotalCost = 0;// 明细的清算金额
		double sumReplaceAmount = 0;// 总的替代数量
		double sumOMakeUpCost = 0;//总的补票成本（原币）
		double sumHMakeUpCost = 0;//总的补票成本（本币）
		double sumOPReplaceCash = 0;// 总的应付替代款（原币）
		double sumHPReplaceCash = 0;// 总的应付替代款（本币）
		double sumOCReplaceCash = 0;// 总的可退替代款（原币）
		double sumHCReplaceCash = 0;// 总的可退替代款（本币）

		double currentRealAmount = 0;// 估值当天对应的汇总的实际数量
		double beforeRemaindAmount = 0;// 补票前的剩余数量

		boolean haveRightsInfo = false;// 用于表示是否有权益数据
		boolean haveMakeUpInfos = false;// 表示是否有补票数据

		String maxNum = "";// 相关证券，相关申赎日期的最大的申请编号
		String sumNum = "";// 交易结算明细表中汇总数据的申请编号

		HashMap hmMXMakeUpCosts = new HashMap();
		HashMap hmMXMakeUpCost = null;
		Iterator iterator = null;

		double sumUnMaxOMakeUpCost = 0;// 加总的相关证券代码，申赎日期的非最大申请编号的补票成本（原币）
		double sumUnMaxHMakeUpCost = 0;// 加总的相关证券代码，申赎日期的非最大申请编号的补票成本（本币）
		double sumUnMaxOPReplaceCash = 0;// 加总的相关证券代码，申赎日期的非最大申请编号的应付替代款（原币）
		double sumUnMaxHPReplaceCash = 0;// 加总的相关证券代码，申赎日期的非最大申请编号的应付替代款（本币）
		double sumUnMaxOCReplaceCash = 0;// 加总的相关证券代码，申赎日期的非最大申请编号的可退替代款（原币）
		double sumUnMaxHCReplaceCash = 0;// 加总的相关证券代码，申赎日期的非最大申请编号的可退替代款（本币）
		ETFTradeSettleDetailRefBean unMaxTradeSetDetRef = null;

		int dealDayNum = 0;// 申购后几个交易日内补票完成
		int tradeDayNum = 0;// 相关证券在申购日和估值日之间的交易天数

		int maxRefNum = 0;// 相关申请编号对应的最大的关联编号
		int refRefNum = 0;// 关联编号

		int dealReplace = 0;// 应付替代款结转

		ETFTradeSettleDetailRefBean mxTradeSetDelRef = null;
		ArrayList alTradeSettleDel = null;
		ArrayList alTradeSetDel = null;
		boolean isLast = false;
		ArrayList alSumMakeUpTradeSetDel = null;
		ETFTradeSettleDetailRefBean sumdTradeSetDel = null;
		double totalMakeUpCost = 0;
		int amountInd = 0;//数量方向
		double exchangeRate = 0;
		CreateAccBookRefData createAccBook = null;
		HashMap hmTransfer = new HashMap();
		try{
			operDeal = new BaseOperDeal();
			operDeal.setYssPub(pub);
			
			createAccBook = new CreateAccBookRefData();
			createAccBook.setYssPub(pub);
			
			portCode = tradeSettleDel.getPortCode();// 证券代码
			paramSet = (ETFParamSetBean) etfParam.get(portCode);// 根据组合代码获取ETF参数设置

			if (paramSet != null) {

				startDateNum = paramSet.getBeginSupply();// 从几个工作日开始补票

				dealDayNum = paramSet.getDealDayNum();// 申购后几个交易日内补票完成

//				oneGradeMktCode = paramSet.getOneGradeMktCode();//一级市场代码
				
				// maxDealDayNum = paramSet.getLastestDealDayNum();//
				// 最长几日补票完成
				if (bs.equals("B")) {
					dealReplace = paramSet.getISGDealReplace();// 应付替代款结转
				}
				if (bs.equals("S")) {
					dealReplace = paramSet.getISHDealReplace();// 应付替代款结转
				}
			}
			
			securityCode = tradeSettleDel.getSecurityCode();// 证券代码
			buyDate = tradeSettleDel.getBuyDate();// 申赎日期
			replaceAmount = tradeSettleDel.getReplaceAmount();// 替代数量
			bs = tradeSettleDel.getBs();// 买卖标志

			if (bs.equals("B")) {
				refDataDirection = 1;// 若买卖标志为申购 则数据方向为1
			}
			if (bs.equals("S")) {
				refDataDirection = -1;// 买卖标志为赎回 则数据方向为-1
			}

			num = tradeSettleDel.getNum();
			oreplaceCash = tradeSettleDel.getOReplaceCash();// 替代金额(原币)
			hreplaceCash = tradeSettleDel.getHReplaceCash();// 替代金额(本币)
			ocreplaceCash = tradeSettleDel.getOcReplaceCash();// 可退替代款(原币)
			hcreplaceCash = tradeSettleDel.getHcReplaceCash();// 可退替代款(本币)
			price = tradeSettleDel.getPrice();//估值行情
			marketValueDate = tradeSettleDel.getMarketValueDate();//行情日期
			
			// 获取相关的交易结算明细关联数据
			alTradeSettleDel = tradeSettleDel.getAlTradeSettleDel();

			tradeSettleDelRef = (ETFTradeSettleDetailRefBean) alTradeSettleDel.get(0);
			exchangeRate = tradeSettleDelRef.getExchangeRate();//汇率
			refRemaindAmount = tradeSettleDelRef.getRemaindAmount();// 剩余数量

			// 若剩余数量 = 0 则为估值当日录入的申赎数据
			if (refRemaindAmount == 0) {
				refRemaindAmount = replaceAmount;// 则默认 补票数量 = 替代数量

				// 补票前的可退替代款(原币)合计 = 查询出的可退替代款(原币)
				refOCanRepCash = ocreplaceCash;

				// 补票前的可退替代款(本币)合计 = 查询出的可退替代款(本币)
				refHCanRepCash = hcreplaceCash;
			}

			beforeRemaindAmount = refRemaindAmount;// 补票前的剩余数量

			refInterest = tradeSettleDelRef.getInterest();// 派息
			refWarrantCost = tradeSettleDelRef.getWarrantCost();// 权证价值
			refSumAmount = tradeSettleDelRef.getSumAmount();// 总数量
			// refRealAmount = tradeSettleDelRef.getRealAmount();//实际数量

			if (refInterest != 0 || refWarrantCost != 0 || refSumAmount != 0) {
				haveRightsInfo = true;// 表示有权益数据
			}

			refOCRefundSum = tradeSettleDelRef.getOcRefundSum(); // 应退合计（原币）
			// 也叫做应付替代款（原币）合计
			refHCRefundSum = tradeSettleDelRef.getHcRefundSum(); // 应退合计（本币）
			// 也叫做应付替代款（本币）合计
			refOCanRepCash = tradeSettleDelRef.getOCanRepCash(); // 可退替代款（原币）合计
			refHCanRepCash = tradeSettleDelRef.getHCanRepCash(); // 可退替代款（本币）合计
			refRefundDate = tradeSettleDelRef.getRefundDate();// 退款日期

			if (refOCanRepCash == 0 && refHCanRepCash == 0) {
				// 补票前的可退替代款(原币)合计 = 查询出的可退替代款(原币)
				refOCanRepCash = ocreplaceCash;
				// 补票前的可退替代款(本币)合计 = 查询出的可退替代款(本币)
				refHCanRepCash = hcreplaceCash;
			}

			// 获取估值当天发生的送股的汇总数据，包括当天汇总的总数量和实际数量
			currentSGtser = (ETFTradeSettleDetailRefBean) hmCurrentSGInfo.get(num);

			if (currentSGtser != null) {
				// currentSumAmount = currentSGtser.getSumAmount();//
				// 估值当天对应的汇总的总数量
				currentRealAmount = currentSGtser.getRealAmount();// 估值当天对应的汇总的实际数量
			}

			// 所有证券在申购日和估值日之间的交易天数
			// allTradeDayNum = calculateAllTradeDayNum(buyDate);
			
			//若证券代码为空，即为按股东代码汇总的数据，就不做处理
			if(securityCode.equals(" ")){
				hmTransfer.put("hmCurrentSGInfo", hmCurrentSGInfo);
				hmTransfer.put("hmTradeInfo", hmTradeInfo);
				hmTransfer.put("hmSumMakeUpInfos", hmSumMakeUpInfos);
				hmTransfer.put("hmMaxNum", hmMaxNum);
				hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
				hmTransfer.put("hmMaxRefNum", hmMaxRefNum);
				
				return hmTransfer;
			}
			
			// 根据从几个工作日开始补票和申赎日期得到可以开始补票的日期
			canMakeUpDate = operDeal.getWorkDay((String) hmHoliday.get(securityCode), buyDate, startDateNum);

			if (canMakeUpDate.equals(buyDate)) {
				tradeDayNum = createAccBook.calculateTradeDayNum(securityCode, YssFun.addDay(buyDate, 1));
			} else {
				tradeDayNum = createAccBook.calculateTradeDayNum(securityCode, canMakeUpDate);
			}
			
			// 若开始补票的日期在估值日期之前 或 等于估值日期 则 可以补票
			if (canMakeUpDate.before(tradeDate) || canMakeUpDate.equals(tradeDate)) {
				// 获取证券代码对应的交易数据
				alTradeInfo = (ArrayList) hmTradeInfo.get(securityCode);

				// 获取需要补票的汇总数据
				tradeSetDel = (ETFTradeSettleDetailBean) hmSumMakeUpInfos.get(securityCode + bs + buyDate.toString());
				if (tradeSetDel != null) {
					sumNum = tradeSetDel.getNum();// 汇总数据对应的申请编号
					// 获取相关证券代码，申赎日期对应的汇总的替代数量
					alSumMakeUpTradeSetDel = tradeSetDel.getAlTradeSettleDel();
					sumdTradeSetDel = (ETFTradeSettleDetailRefBean) alSumMakeUpTradeSetDel.get(0);
					sumReplaceAmount = sumdTradeSetDel.getRemaindAmount();
					if (sumReplaceAmount == 0) {
						sumReplaceAmount = tradeSetDel.getReplaceAmount();
					}
				}

				// 获取相关证券代码，申赎日期对应的最大的申请编号
				maxNum = (String) hmMaxNum.get(securityCode + bs + buyDate.toString());
		
				if (alTradeInfo != null) {// 若有交易数据
					//---获取估值当天总的明细到证券代码的交易数量----//
					sumAmount = 0;
					for (int j = 0; j < alTradeInfo.size(); j++) {
						tradeSub = (TradeSubBean) alTradeInfo.get(j);// 得到明细的交易数据
						amountInd = tradeSub.getAmountInd();//数量方向
						sumAmount += YssD.mul(tradeSub.getTradeAmount(), amountInd);// 汇总交易数量
					}
					//---获取估值当天总的明细到证券代码的交易数量----//
					
					if((bs.equals("B") && sumAmount <= 0) || (bs.equals("S") && sumAmount >= 0)){
						hmTransfer.put("hmCurrentSGInfo", hmCurrentSGInfo);
						hmTransfer.put("hmTradeInfo", hmTradeInfo);
						hmTransfer.put("hmSumMakeUpInfos", hmSumMakeUpInfos);
						hmTransfer.put("hmMaxNum", hmMaxNum);
						hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
						hmTransfer.put("hmMaxRefNum", hmMaxRefNum);
						
						return hmTransfer;
					}
					
					// 若为汇总数据
					if (sumNum.equals(num)) {
						haveMakeUpInfos = true;// 则表示有补票数据
						// refDataMark = 0;// 数据标识为补票
						// 计算总的补票成本---------------------------------------------------------------------------------
						// 若总的交易数量大于需要补票的数量
						if (sumAmount > sumReplaceAmount) {
								// 循环明细的交易数据
								for (int j = 0; j < alTradeInfo.size(); j++) {
									tradeSub = (TradeSubBean) alTradeInfo.get(j);// 获取明细的交易子表实体类

//									detailAmount = tradeSub.getTradeAmount(); // 获取明细的交易数量
									detailTotalCost = tradeSub.getTotalCost(); // 获取明细的清算金额

									totalMakeUpCost += detailTotalCost;// 加总用于补票的清算金额
								}

								sumOMakeUpCost = YssD.mul(YssD.div(sumReplaceAmount, sumAmount), totalMakeUpCost);
						} else {
							// 循环明细的交易数据
							for (int j = 0; j < alTradeInfo.size(); j++) {
								tradeSub = (TradeSubBean) alTradeInfo.get(j);// 获取明细的交易子表实体类

//								detailAmount = tradeSub.getTradeAmount(); // 获取明细的交易数量
								detailTotalCost = tradeSub.getTotalCost(); // 获取明细的清算金额

								sumOMakeUpCost += detailTotalCost;// 加总用于补票的清算金额
							}

							sumTradeAmount = sumAmount;// 总的用于补票的交易数量
						}
						// 计算总的补票成本---------------------------------------------------------------------------------

						refMakeUpAmount = sumTradeAmount;
						refOMakeUpCost = sumOMakeUpCost;
						refRemaindAmount -= refMakeUpAmount;
					} else {
						refOMakeUpCost = 0;
						refMakeUpAmount = 0;
						// 若当前的申请编号等于当前证券代码，申赎日期的最大的申请编号，且汇总数据下有多条明细数据，则计算补票成本时，要轧差
						if (maxNum.equals(num)
								&& (Integer.parseInt(YssFun.right(sumNum, 6)) + 1 != Integer.parseInt(YssFun.right(maxNum, 6)))) {// 表示有多天明细数据

							isLast = true;

							sumTradeSetDel = (ETFTradeSettleDetailBean) hmMakeUpInfo.get(sumNum);// 查询汇总数据对应的交易结算明细实体类
							if (sumTradeSetDel != null) {
								alTradeSetDel = sumTradeSetDel.getAlTradeSettleDel();
								sumTradeSettleDelRef = (ETFTradeSettleDetailRefBean) alTradeSetDel.get(0);

								sumOMakeUpCost = sumTradeSettleDelRef.getoMakeUpCost();// 总的补票成本（原币）
								sumHMakeUpCost = sumTradeSettleDelRef.gethMakeUpCost();// 总的补票成本（本币）

								sumOPReplaceCash = sumTradeSettleDelRef.getOpReplaceCash();// 总的应付替代款（原币）
								sumHPReplaceCash = sumTradeSettleDelRef.getHpReplaceCash();// 总的应付替代款（本币）
								sumOCReplaceCash = sumTradeSettleDelRef.getOcReplaceCash();// 总的可退替代款（原币）
								sumHCReplaceCash = sumTradeSettleDelRef.getHcReplaceCash();// 总的可退替代款（本币）
							}

							hmMXMakeUpCost = (HashMap) hmMXMakeUpCosts.get(securityCode + buyDate.toString());

							if (hmMXMakeUpCost != null) {
								iterator = hmMXMakeUpCost.values().iterator();

								while (iterator.hasNext()) {
									unMaxTradeSetDetRef = (ETFTradeSettleDetailRefBean) iterator.next();

									// 加总 非最大的申请编号 的相关证券代码，申赎日期的补票成本（原币）
									sumUnMaxOMakeUpCost += unMaxTradeSetDetRef.getoMakeUpCost();
									
									// 加总 非最大的申请编号 的相关证券代码，申赎日期的补票成本（本币）
									sumUnMaxHMakeUpCost += unMaxTradeSetDetRef.gethMakeUpCost();

									// 加总的相关证券代码，申赎日期的非最大申请编号的应付替代款（原币）
									sumUnMaxOPReplaceCash += unMaxTradeSetDetRef.getOpReplaceCash();

									// 加总的相关证券代码，申赎日期的非最大申请编号的应付替代款（本币）
									sumUnMaxHPReplaceCash += unMaxTradeSetDetRef.getHpReplaceCash();

									// 加总的相关证券代码，申赎日期的非最大申请编号的可退替代款（原币）
									sumUnMaxOCReplaceCash += unMaxTradeSetDetRef.getOcReplaceCash();

									// 加总的相关证券代码，申赎日期的非最大申请编号的可退替代款（本币）
									sumUnMaxHCReplaceCash += unMaxTradeSetDetRef.getHcReplaceCash();
								}

								// 轧差出的补票成本 = 所有用于补票的清算金额 - 汇总的
								// 非最大的申请编号
								// 的相关证券代码，申赎日期的补票成本
								refOMakeUpCost = sumOMakeUpCost - sumUnMaxOMakeUpCost;
								
								refHMakeUpCost = sumHMakeUpCost - sumUnMaxHMakeUpCost;

								// 轧差出的应付替代款（原币） = 所有用于补票的应付替代款（原币）
								// -
								// 汇总的 非最大的申请编号
								// 的相关证券代码，申赎日期的应付替代款（原币）
								refOPReplaceCash = sumOPReplaceCash - sumUnMaxOPReplaceCash;

								// 轧差出的应付替代款（本币） = 所有用于补票的应付替代款（本币）
								// -
								// 汇总的 非最大的申请编号
								// 的相关证券代码，申赎日期的应付替代款（本币）
								refHPReplaceCash = sumHPReplaceCash - sumUnMaxHPReplaceCash;

								// 轧差出的可退替代款（原币） = 所有用于补票的可退替代款（原币）
								// -
								// 汇总的 非最大的申请编号
								// 的相关证券代码，申赎日期的可退替代款（原币）
								refOCReplaceCash = sumOCReplaceCash - sumUnMaxOCReplaceCash;

								// 轧差出的可退替代款（本币） = 所有用于补票的可退替代款（本币）
								// -
								// 汇总的 非最大的申请编号
								// 的相关证券代码，申赎日期的可退替代款（本币）
								refHCReplaceCash = sumHCReplaceCash - sumUnMaxHCReplaceCash;
							}
						} else {
							refOMakeUpCost = 0;
							refMakeUpAmount = 0;
							// 循环明细的交易数据
							for (int j = 0; j < alTradeInfo.size(); j++) {
								tradeSub = (TradeSubBean) alTradeInfo.get(j);// 获取明细的交易子表实体类
								amountInd = tradeSub.getAmountInd();//数量方向
								remaindTradeAmount = tradeSub.getHandAmount(); // 获取明细的未用于补票的交易数量
								detailAmount = tradeSub.getTradeAmount();// 交易数量

								// 若剩余的交易数量 = 0，则不执行补票步骤
								if (remaindTradeAmount == 0) {
									break;
								}

								detailTotalCost = tradeSub.getTotalCost(); // 获取明细的清算金额
								
								// 若该笔为申购  且 交易类型为买入 且 剩余补票数量 大于 剩余的交易数量
								if (((bs.equals("B") && amountInd == 1) || (bs.equals("S") && amountInd == -1)) 
										&& refRemaindAmount >= remaindTradeAmount) {
									tradeSub.setHandAmount(0);// 还未用于补票的数量
									alTradeInfo.set(j, tradeSub);// 更新交易数据

									// 更新相关证券代码对应的未用于补票的交易数据
									hmTradeInfo.put(securityCode, alTradeInfo);
									
									// 补票成本 = 之前的补票成本 + 本次补票的数量/总的交易数量 * 总的交易数据的清算金额
									refOMakeUpCost += YssD.mul(YssD.div(remaindTradeAmount, sumTradeAmount), sumOMakeUpCost);

									refMakeUpAmount += remaindTradeAmount;
									refRemaindAmount = refRemaindAmount - remaindTradeAmount;
									continue;
								}
								
								// （（若该笔为申购  且 交易类型为卖出）或（若该笔为赎回  且 交易类型为买入）） 且 剩余补票数量 大于 剩余的交易数量
								if (((bs.equals("B") && amountInd == -1) || (bs.equals("S") && amountInd == 1)) 
										&& refRemaindAmount >= remaindTradeAmount) {
									tradeSub.setHandAmount(0);// 还未用于补票的数量
									alTradeInfo.set(j, tradeSub);// 更新交易数据

									// 更新相关证券代码对应的未用于补票的交易数据
									hmTradeInfo.put(securityCode, alTradeInfo);
									
									// 补票成本 = 之前的补票成本 + 本次补票的数量/总的交易数量 * 总的交易数据的清算金额
									refOMakeUpCost -= YssD.mul(YssD.div(remaindTradeAmount, sumTradeAmount), sumOMakeUpCost);
									refMakeUpAmount -= remaindTradeAmount;
									refRemaindAmount += remaindTradeAmount;
									continue;
								}
								
								// 若剩余补票数量 等于 剩余的交易数量
								if (((bs.equals("B") && amountInd == 1) || (bs.equals("S") && amountInd == -1)) 
										/**shashijie 2012-7-2 STORY 2475 */
										&& YssD.sub(refRemaindAmount,remaindTradeAmount) == 0) {
										/**end*/
										
									tradeSub.setHandAmount(0);// 还未用于补票的数量
									alTradeInfo.set(j, tradeSub);// 更新交易数据

									// 更新相关证券代码对应的未用于补票的交易数据
									hmTradeInfo.put(securityCode, alTradeInfo);
									
									// 补票成本 = 之前的补票成本 + 本次补票的数量/总的交易数量 * 总的交易数据的清算金额
									refOMakeUpCost += YssD.mul(YssD.div(remaindTradeAmount, sumTradeAmount), sumOMakeUpCost);
									refMakeUpAmount += remaindTradeAmount;
									refRemaindAmount = 0;
									break;
								}
								
								// 若剩余补票数量 等于 剩余的交易数量
								if (((bs.equals("B") && amountInd == -1) || (bs.equals("S") && amountInd == 1)) 
										&& refRemaindAmount == remaindTradeAmount) {
									tradeSub.setHandAmount(0);// 还未用于补票的数量
									alTradeInfo.set(j, tradeSub);// 更新交易数据

									// 更新相关证券代码对应的未用于补票的交易数据
									hmTradeInfo.put(securityCode, alTradeInfo);
									
									// 补票成本 = 之前的补票成本 + 本次补票的数量/总的交易数量 * 总的交易数据的清算金额
									refOMakeUpCost -= YssD.mul(YssD.div(remaindTradeAmount, sumTradeAmount), sumOMakeUpCost);
									refMakeUpAmount -= remaindTradeAmount;
									refRemaindAmount += remaindTradeAmount;
									continue;
								}
								
								// 若剩余补票数量 小于 剩余的交易数量
								if (((bs.equals("B") && amountInd == 1) || (bs.equals("S") && amountInd == -1)) &&
										refRemaindAmount < remaindTradeAmount) {
									// 补票数量 等于 替代数量
									// 还未用于补票的交易数量
									tradeSub.setHandAmount(remaindTradeAmount - refRemaindAmount);
									alTradeInfo.set(j, tradeSub);// 更新交易数据

									// 更新相关证券代码对应的未用于补票的交易数据
									hmTradeInfo.put(securityCode, alTradeInfo);
									
									// 补票成本 = 之前的补票成本 + 本次补票的数量/总的交易数量 * 总的交易数据的清算金额
									refOMakeUpCost += YssD.mul(YssD.div(refRemaindAmount, sumTradeAmount), sumOMakeUpCost);
									refMakeUpAmount += refRemaindAmount;
									refRemaindAmount = 0;
									break;
								}
								
								// 若剩余补票数量 小于 剩余的交易数量
								if (((bs.equals("B") && amountInd == -1) || (bs.equals("S") && amountInd == 1)) &&
										refRemaindAmount < remaindTradeAmount) {
									refMakeUpAmount = refRemaindAmount;
									// 还未用于补票的交易数量
									tradeSub.setHandAmount(remaindTradeAmount - refRemaindAmount);
									alTradeInfo.set(j, tradeSub);// 更新交易数据

									// 更新相关证券代码对应的未用于补票的交易数据
									hmTradeInfo.put(securityCode, alTradeInfo);
									
									// 补票成本 = 之前的补票成本 - 剩余的交易数量/总的交易数量 * 总的交易数据的清算金额
									refOMakeUpCost -= YssD.mul(YssD.div(remaindTradeAmount, sumTradeAmount), sumOMakeUpCost);
									refMakeUpAmount -= remaindTradeAmount;
									refRemaindAmount += remaindTradeAmount;
									continue;
								}
							}
						}
					}
				}
			}else {// 若估值日期当天不能补票
				// 若估值日期当天有权益数据
				if (haveRightsInfo) {
					// 则补票日期为9998-12-31
					refMakeUpDate = YssFun.toDate("9998-12-31");
				} else {// 若估值当天不能补票 又没有补票数据 则不能将数据插入到交易结算明细关联表
					hmMakeUpInfo.remove(num);
					
					hmTransfer.put("hmCurrentSGInfo", hmCurrentSGInfo);
					hmTransfer.put("hmTradeInfo", hmTradeInfo);
					hmTransfer.put("hmSumMakeUpInfos", hmSumMakeUpInfos);
					hmTransfer.put("hmMaxNum", hmMaxNum);
					hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
					hmTransfer.put("hmMaxRefNum", hmMaxRefNum);
					
					return hmTransfer;
				}
			}
			
			
			refHMakeUpCost = refOMakeUpCost * exchangeRate;
			
			if (!isLast && alTradeInfo != null) {
				// 应付替代款（原币）=（替代金额（原币）-派息-权证价值）* 补票的数量/(替代数量+总数量) –
				// 本次补票的成本（含费用）
				refOPReplaceCash = YssD.div(YssD.mul(oreplaceCash - refInterest - refWarrantCost, refMakeUpAmount), replaceAmount
						+ refSumAmount)
						- refOMakeUpCost;

				// 应付替代款（本币）=（替代金额（本币）-派息-权证价值）* 补票的数量/(替代数量+总数量) –
				// 本次补票的成本（含费用）
				refHPReplaceCash = YssD.div(YssD.mul(hreplaceCash - refInterest - refWarrantCost, refMakeUpAmount), replaceAmount
						+ refSumAmount)
						- refHMakeUpCost;

				// 可退替代款（原币）= 补票的数量/(替代数量+总数量)*申购日的可退替代款的原币金额
				refOCReplaceCash = YssD.mul(YssD.div(refMakeUpAmount, replaceAmount + refSumAmount), ocreplaceCash);

				// 可退替代款（本币）= 补票的数量/(替代数量+总数量)*申购日的可退替代款的本币金额
				refHCReplaceCash = YssD.mul(YssD.div(refMakeUpAmount, replaceAmount + refSumAmount), hcreplaceCash);
			}

			// 若当前的申请编号不等于当前证券代码，申赎日期的最大的申请编号
			// 则将算出的补票成本储存到相关证券代码，相关申赎日期对应的哈希表中
			if (hmMakeUpInfo.get(num) != null && !maxNum.equals(num) && !sumNum.equals(num) && alTradeInfo != null) {
				// 用于储存相关证券代码，申赎日期对应的相关申请编号对应的非最大申请编号的补票成本
				hmMXMakeUpCost = (HashMap) hmMXMakeUpCosts.get(securityCode + buyDate.toString());

				mxTradeSetDelRef = new ETFTradeSettleDetailRefBean();

				
				mxTradeSetDelRef.setoMakeUpCost(refOMakeUpCost);
				mxTradeSetDelRef.sethMakeUpCost(refHMakeUpCost);
				mxTradeSetDelRef.setOcReplaceCash(refOCReplaceCash);
				mxTradeSetDelRef.setHcReplaceCash(refHCReplaceCash);
				mxTradeSetDelRef.setHpReplaceCash(refHPReplaceCash);
				mxTradeSetDelRef.setOpReplaceCash(refOPReplaceCash);

				if (hmMXMakeUpCost != null) {
					if (hmMXMakeUpCost.get(num) == null) {
						hmMXMakeUpCost.put(num, mxTradeSetDelRef);
					}
				} else {
					hmMXMakeUpCost = new HashMap();
					hmMXMakeUpCost.put(num, mxTradeSetDelRef);
				}

				hmMXMakeUpCosts.put(securityCode + buyDate.toString(), hmMXMakeUpCost);
			}

			// 获取最大的关联编号
			if (hmMaxRefNum.get(num) != null) {
				maxRefNum = Integer.parseInt((String) hmMaxRefNum.get(num));
			}

			if (maxRefNum != 0) {
				refRefNum = maxRefNum + 1;
			} else {// 若交易结算关联明细表中没有申请编号为num的相关数据
				refRefNum = 1;
			}

			if (hmMakeUpInfo.get(num) != null) {
				hmMaxRefNum.put(num, String.valueOf(refRefNum));// 设置当前的关联编号为最大的关联编号
				if (alTradeInfo != null) {
					// 单位成本 = 本次补票的成本（含费用）/补票数量。
					refUnitCost = YssD.div(refOMakeUpCost, refMakeUpAmount);

					refOCanRepCash -= refOCReplaceCash; // 可退替代款（原币）合计

					refHCanRepCash -= refHCReplaceCash; // 可退替代款（本币）合计
				}

				// 若（有交易数据 或 未停牌） 但没有补票操作 则补票日期等于估值日期
				if(price != 0 && marketValueDate.equals(tradeDate)){// 有交易数据
					refMakeUpDate = tradeDate;
				}
				else{
					hmMakeUpInfo.remove(num);
					
					hmTransfer.put("hmCurrentSGInfo", hmCurrentSGInfo);
					hmTransfer.put("hmTradeInfo", hmTradeInfo);
					hmTransfer.put("hmSumMakeUpInfos", hmSumMakeUpInfos);
					hmTransfer.put("hmMaxNum", hmMaxNum);
					hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
					hmTransfer.put("hmMaxRefNum", hmMaxRefNum);
					
					return hmTransfer;
				}

				// 若补票前的剩余数据大于补票后的（未添加当天送股的权益数据）剩余数量
				if (beforeRemaindAmount > refRemaindAmount) {
					haveMakeUpInfos = true;// 则表示有补票数据
					// refDataMark = 0;// 数据标识为补票
				}

				// 剩余数量 = 补票前的剩余数量 + 当天的权益的实际数量 - 当天的补票的总数量
				refRemaindAmount = beforeRemaindAmount + currentRealAmount - refMakeUpAmount;

				// 清算标识 = 当剩余数量不为0时，清算标识为N，当剩余数量为0时，清算标识为Y。
				if (refRemaindAmount == 0) {
					if (tradeDayNum == dealDayNum) {// 同时实际的交易天数等于ETF设置中设置的交易天数
						refSettleMark = "Y";
						// 退款日期：默认为9998-12-31，当清算标识为Y，则根据ETF基本参数设置中
						// 应付替代款的结转天数，来推算退款日期。若ETF参数设置中设置在T + N日对
						// 应付替代款进行结转，则退款日期 =剩余数量等于0的日期+ N个工作日

						refRefundDate = operDeal.getWorkDay((String) hmHoliday.get(securityCode), buyDate, dealReplace);
						refRefundDate = YssFun.parseDate(YssFun.formatDate(refRefundDate, "yyyy-MM-dd"));// 格式化
					}
				} else {
					refSettleMark = "N";
				}

				// 应退合计（原币）也叫做应付替代款（原币）合计
				refOCRefundSum += refOPReplaceCash;

				refHCRefundSum += refHPReplaceCash; // 应退合计（本币）

				tradeSettleDelRef.setDataDirection(String.valueOf(refDataDirection));// 数据方向
				tradeSettleDelRef.setSettleMark(refSettleMark);// 清算标识
				tradeSettleDelRef.setRemaindAmount((int) refRemaindAmount);// 剩余数量
				tradeSettleDelRef.setRefNum(String.valueOf(refRefNum));// 关联编号
				tradeSettleDelRef.setNum(num);// 申请编号
				tradeSettleDelRef.setRefundDate(refRefundDate);// 退款日期
				tradeSettleDelRef.setMakeUpDate(refMakeUpDate);// 补票日期
				tradeSettleDelRef.setDataMark("0");// 数据标识

				// 若有权益数据或有补票数据
				if (haveRightsInfo || haveMakeUpInfos) {
					tradeSettleDelRef.setUnitCost(YssFun.roundIt(refUnitCost, 4));// 单位成本
					tradeSettleDelRef.setoMakeUpCost(refOMakeUpCost);//补票成本（原币）
					tradeSettleDelRef.sethMakeUpCost(refHMakeUpCost);//补票成本（原币）
					tradeSettleDelRef.setMakeUpAmount(refMakeUpAmount);// 补票数量
					tradeSettleDelRef.setOpReplaceCash(YssFun.roundIt(refOPReplaceCash, 4));// 应付替代款（原币）
					tradeSettleDelRef.setHpReplaceCash(YssFun.roundIt(refHPReplaceCash, 4));// 应付替代款（本币）
					tradeSettleDelRef.setOcReplaceCash(YssFun.roundIt(refOCReplaceCash, 4));// 可退替代款发生额（原币）
					tradeSettleDelRef.setHcReplaceCash(YssFun.roundIt(refHCReplaceCash, 4));// 可退替代款发生额（本币）
					tradeSettleDelRef.setOCanRepCash(YssFun.roundIt(refOCanRepCash, 4));// 可退替代款（原币）合计
					tradeSettleDelRef.setHCanRepCash(YssFun.roundIt(refHCanRepCash, 4));// 可退替代款（本币）合计
					tradeSettleDelRef.setOcRefundSum(YssFun.roundIt(refOCRefundSum, 4));// 应退合计（原币）
					tradeSettleDelRef.setHcRefundSum(YssFun.roundIt(refHCRefundSum, 4));// 应退合计（本币）
				}

				alTradeSettleDel.set(0, tradeSettleDelRef);
				tradeSettleDel.setAlTradeSettleDel(alTradeSettleDel);

				hmMakeUpInfo.put(num, tradeSettleDel);
			}
			
			hmTransfer.put("hmCurrentSGInfo", hmCurrentSGInfo);
			hmTransfer.put("hmTradeInfo", hmTradeInfo);
			hmTransfer.put("hmSumMakeUpInfos", hmSumMakeUpInfos);
			hmTransfer.put("hmMaxNum", hmMaxNum);
			hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
			hmTransfer.put("hmMaxRefNum", hmMaxRefNum);
			
			return hmTransfer;
		}
		catch(Exception e){
			throw new YssException("根据均摊的补票方式处理需要补票的数据出错！", e);
		}
	}

}
