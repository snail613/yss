package com.yss.main.etfoperation.dealmakeup;

import java.util.ArrayList;
import java.util.HashMap;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.etfaccbook.CreateAccBookRefData;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFStockListBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class dealOffSetBalanceBean extends CtlDealMakeUp{
	
	/**
	 * 构造函数
	 */
	public dealOffSetBalanceBean() {

	}
	
	/**
	 * 根据轧差补票的补票方式处理需要补票的数据
	 * @throws YssException
	 */
	public HashMap dealOffSetBalance(ETFTradeSettleDetailBean tradeSettleDel, int time,HashMap hmSumMakeUpAmount)throws YssException{
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;
		String num = ""; // 申请编号
		String portCode = ""; // 组合代码
		String securityCode = ""; // 证券代码
		String bs = ""; // 买卖标志
		java.util.Date buyDate = null; // 申赎日期
		double replaceAmount = 0; // 替代数量
//		double oreplaceCash = 0; // 替代金额（原币）
		double hreplaceCash = 0;// 替代金额（本币）
//		double ocreplaceCash = 0; // 可退替代款（原币）
		double hcreplaceCash = 0; // 可退替代款（本币）
		double price = 0;//估值行情

		java.util.Date refMakeUpDate = null; // 补票日期
		double refUnitCost = 0; // 单位成本
		double refOMakeUpCost = 0;// 补票成本（原币）
		double refHMakeUpCost = 0;// 补票成本（本币）
		double refMakeUpAmount = 0; // 补票数量
		double refOPReplaceCash = 0; // 应付替代款（原币）
		double refHPReplaceCash = 0; // 应付替代款（本币）
		double refOCReplaceCash = 0; // 可退替代款 发生额（原币）
		double refHCReplaceCash = 0; // 可退替代款 发生额（本币）
		double refRemaindAmount = 0; // 剩余数量
		double refOCRefundSum = 0; // 应退合计（原币） 也叫做应付替代款（原币）合计
		double refHCRefundSum = 0; // 应退合计（本币） 也叫做应付替代款（本币）合计
		double refOCanRepCash = 0; // 可退替代款 余额（原币）
		double refHCanRepCash = 0; // 可退替代款 余额（本币）
		double refTradeUnitCost = 0;//成交单价
		double refFeeUnitCost = 0;//费用单价
		int refDataDirection = 0; // 数据方向 申购 -- 1 赎回 -- -1
		String refSettleMark = ""; // 清算标识 清算 -- Y 未清算 -- N
		java.util.Date refRefundDate = null;// 退款日期

		ETFParamSetBean paramSet = null;// ETF参数的实体类

		ArrayList alTradeInfo = null;// 用于储存相关证券的交易数据
		ArrayList alETFTradeInfo = null;//用于储存相关证券的ETF交易数据
		
		TradeSubBean tradeSub = null;// 交易子表实体类
		int startDateNum = 0;// 从几个工作日开始补票
		java.util.Date canMakeUpDate = null;// 可以开始补票的日期
		BaseOperDeal operDeal = null;
		double sumAmount = 0;// 总的交易数量
		int dealDayNum = 0;// 申购后几个交易日内补票完成
		int tradeDayNum = 0;// 相关证券在申购日和估值日之间的交易天数

		int maxRefNum = 0;// 相关申请编号对应的最大的关联编号
		int refRefNum = 0;// 关联编号

		int dealReplace = 0;// 应付替代款结转

		ArrayList alTradeSettleDel = null;
		double basketOfSG = 0;// 申购的篮子数
		double basketOfSH = 0;// 赎回的篮子数
		double makeUpBasketCount = 0;// 应补票的篮子数
		String oneGradeMktCode = "";//一级市场代码
		double braketNum = 0;//篮子数
		double amount = 0;//股票篮中的股票数量
		double sumTotalCost = 0;//清算金额
		int unitDigit = 0;//单位成本保留为数
		double exchangeRate = 0;//汇率
		int amountInd = 0;//数据方向
		ETFStockListBean stockList = null;
		CreateAccBookRefData createAccBook = null;
		HashMap hmTransfer = new HashMap();
		boolean canMakeUp = true;//能补票
		double factAmount = 0;//实际补票数量
		EachExchangeHolidays holiday = null;//节假日获取类
		double buyAmount = 0;//买入的交易数量
		double sellAmount = 0;//卖出的交易数量
		double buyTotalCost = 0;//买入的清算金额
		double sellTotalCost = 0;//卖出的清算金额
		double buyTradeMoney = 0;//买入的交易成本
		double sellTradeMoney = 0;//卖出的交易成本
		double buyFee = 0;//买入的交易费用
		double sellFee = 0;//卖出的交易费用
		double sumMakeUpAmount = 0;
		try{
			holiday = new EachExchangeHolidays();
			holiday.setYssPub(pub);
			
			operDeal = new BaseOperDeal();
			operDeal.setYssPub(pub);
			
			createAccBook = new CreateAccBookRefData();
			createAccBook.setYssPub(pub);
			createAccBook.setTradeDate(tradeDate);
			
			portCode = tradeSettleDel.getPortCode();// 组合代码
			paramSet = (ETFParamSetBean) etfParam.get(portCode);// 根据组合代码获取ETF参数设置
			
			securityCode = tradeSettleDel.getSecurityCode();// 证券代码
			
			if(hmSumMakeUpAmount != null && time == 2 && !securityCode.equals(" ")){
				sumMakeUpAmount = ((ETFTradeSettleDetailRefBean)hmSumMakeUpAmount.get(securityCode)).getMakeUpAmount();
			}
			
			buyDate = tradeSettleDel.getBuyDate();// 申赎日期
			replaceAmount = tradeSettleDel.getReplaceAmount();// 替代数量
			bs = tradeSettleDel.getBs();// 买卖标志

			if (paramSet != null) {

				startDateNum = paramSet.getBeginSupply();// 从几个工作日开始补票

				dealDayNum = paramSet.getDealDayNum();// 申购后几个交易日内补票完成

				oneGradeMktCode = paramSet.getOneGradeMktCode();//一级市场代码
				
				unitDigit = paramSet.getSUnitdigit();//单位成本保留为数
				
				// maxDealDayNum = paramSet.getLastestDealDayNum();//
				// 最长几日补票完成
				if (bs.equals("B")) {
					dealReplace = paramSet.getISGDealReplace();// 应付替代款结转
				}
				if (bs.equals("S")) {
					dealReplace = paramSet.getISHDealReplace();// 应付替代款结转
				}
			}
			
			if (bs.equals("B")) {
				refDataDirection = 1;// 若买卖标志为申购 则数据方向为1
			}
			if (bs.equals("S")) {
				refDataDirection = -1;// 买卖标志为赎回 则数据方向为-1
			}

			num = tradeSettleDel.getNum();
//			oreplaceCash = tradeSettleDel.getOReplaceCash();// 替代金额(原币)
			hreplaceCash = tradeSettleDel.getHReplaceCash();//替代金额（本币）
//			ocreplaceCash = tradeSettleDel.getOcReplaceCash();// 可退替代款(原币)
			hcreplaceCash = tradeSettleDel.getHcReplaceCash();// 可退替代款(本币)
			
			price = tradeSettleDel.getPrice();//估值行情
			braketNum = tradeSettleDel.getBraketNum();//篮子数
			
			// 获取相关的交易结算明细关联数据
			alTradeSettleDel = tradeSettleDel.getAlTradeSettleDel();

			tradeSettleDelRef = (ETFTradeSettleDetailRefBean) alTradeSettleDel.get(0);

			exchangeRate = tradeSettleDelRef.getExchangeRate();//汇率
			refRemaindAmount = tradeSettleDelRef.getRemaindAmount();// 剩余数量

			refOCRefundSum = tradeSettleDelRef.getOcRefundSum(); // 应退合计（原币）
			// 也叫做应付替代款（原币）合计
			refHCRefundSum = tradeSettleDelRef.getHcRefundSum(); // 应退合计（本币）
			// 也叫做应付替代款（本币）合计
			refOCanRepCash = tradeSettleDelRef.getOCanRepCash(); // 可退替代款余额（原币）
			refHCanRepCash = tradeSettleDelRef.getHCanRepCash(); // 可退替代款余额（本币）
			
			refOPReplaceCash = tradeSettleDelRef.getOpReplaceCash(); // 应付替代款（原币）
			refHPReplaceCash = tradeSettleDelRef.getHpReplaceCash(); // 应付替代款（本币）
			
			refRefundDate = tradeSettleDelRef.getRefundDate();// 退款日期

			// 若剩余数量 = 0 则为估值当日录入的申赎数据
			if (refRemaindAmount == 0) {
				refRemaindAmount = replaceAmount;// 则默认 补票数量 = 替代数量
			}

			// 所有证券在申购日和估值日之间的交易天数
			// allTradeDayNum = calculateAllTradeDayNum(buyDate);
			
 			if(securityCode.equals(" ")){
				hmTransfer.put("hmCurrentSGInfo", hmCurrentSGInfo);
				hmTransfer.put("hmTradeInfo", hmTradeInfo);
				hmTransfer.put("hmSumMakeUpInfos", hmSumMakeUpInfos);
				hmTransfer.put("hmMaxNum", hmMaxNum);
				hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
				hmTransfer.put("hmMaxRefNum", hmMaxRefNum);
				hmTransfer.put("hmSHSumMakeUpInfos", hmSHSumMakeUpInfos);
				hmTransfer.put("hmStockList", hmStockList);
				if(time == 2){
					hmTransfer.put("hmETFTradeInfo", hmETFTradeInfo);
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
				}
				
				tradeSettleDelRef.setDataDirection(String.valueOf(refDataDirection));// 数据方向
				tradeSettleDelRef.setRemaindAmount((int) refRemaindAmount);// 剩余数量
				tradeSettleDelRef.setNum(num);// 申请编号
				tradeSettleDelRef.setRefundDate(refRefundDate);// 退款日期
				tradeSettleDelRef.setDataMark("0");// 数据标识
				tradeSettleDelRef.setMakeUpDate(tradeDate);
				tradeSettleDelRef.setRefNum(String.valueOf(refRefNum));
				tradeSettleDelRef.setDataMark("0");

				return hmTransfer;
			}
			
			// 根据从几个工作日开始补票和申赎日期得到可以开始补票的日期
			canMakeUpDate = operDeal.getWorkDay((String) hmHoliday.get(portCode + ",beginsupply"), buyDate, startDateNum);

			//若可以补票的日期等于申购日
			if (canMakeUpDate.equals(buyDate)) {
				//则交易日 =  非申购日开始到估值日之间的交易日
				tradeDayNum = createAccBook.calculateTradeDayNum(securityCode, YssFun.addDay(buyDate, 1));
			} else {
				//若不等于申购日 则 交易日 = 开始补票的日期到估值日之间的交易日
				tradeDayNum = createAccBook.calculateTradeDayNum(securityCode, canMakeUpDate);
			}
			
			// 若开始补票的日期在估值日期之前 或 等于估值日期 则 可以补票
			if (canMakeUpDate.before(tradeDate) || canMakeUpDate.equals(tradeDate)) {
				//若证券代码为空，即为按股东代码汇总的数据，就不做处理
				
				// 获取证券代码对应的交易数据
				alTradeInfo = (ArrayList) hmTradeInfo.get(securityCode);
				
				if(time == 2 && hmETFTradeInfo != null){
					// 获取证券代码对应的ETF交易数据
					alETFTradeInfo = (ArrayList) hmETFTradeInfo.get(securityCode);
				}

				if(hmbasket.get(portCode + "\tS\t" + oneGradeMktCode) != null){
					basketOfSG = Double.parseDouble((String) hmbasket.get(portCode + "\tS\t" + oneGradeMktCode));//申购的篮子数
				}
				if(hmbasket.get(portCode + "\tB\t" + oneGradeMktCode) != null){
					basketOfSH = Double.parseDouble((String) hmbasket.get(portCode + "\tB\t" + oneGradeMktCode));//赎回的篮子数
				}
				
				// 净申购
				if (basketOfSG > basketOfSH) {
					// 应补票的篮子数 = 申购的篮子数 - 赎回的篮子数
					makeUpBasketCount = basketOfSG - basketOfSH;
				}
				
				// 净赎回
				if (basketOfSG < basketOfSH) {
					// 应补票的篮子数 = 赎回的篮子数 - 申购的篮子数
					makeUpBasketCount = basketOfSH - basketOfSG;
				}
				
				// ---获取估值当天总的明细到证券代码的交易数量----//
				if(alTradeInfo != null){
					for (int j = 0; j < alTradeInfo.size(); j++) {
						tradeSub = (TradeSubBean) alTradeInfo.get(j);// 得到明细的交易数据
						
						amountInd = tradeSub.getAmountInd();// 数量方向
						
						if(amountInd == 1){
							buyAmount += tradeSub.getTradeAmount();
							buyTradeMoney += tradeSub.getTradeMoney();
							buyTotalCost += tradeSub.getTotalCost();
						}
						else{
							sellAmount += tradeSub.getTradeAmount();
							sellTradeMoney += tradeSub.getTradeMoney();
							sellTotalCost += tradeSub.getTotalCost();
						}
						
						sumAmount += YssD.mul(tradeSub.getTradeAmount(), amountInd);// 汇总交易数量
						sumTotalCost += YssD.mul(tradeSub.getTotalCost(), amountInd);// 清算金额
					}
				}
				//----获取估值当天总的明细到证券代码的交易数量----//
				
				//----获取估值当天总的明细到证券代码的ETF交易数量----//
				if(time ==2 && alETFTradeInfo != null){
					for(int j = 0; j < alETFTradeInfo.size(); j++){
						tradeSub = (TradeSubBean) alETFTradeInfo.get(j);// 得到明细的ETF交易数据
						amountInd = tradeSub.getAmountInd();// 数量方向
						
						if(amountInd == 1){
							buyFee += tradeSub.getTradeFee();
						}
						else{
							sellFee += tradeSub.getTradeFee();
						}
					}
				}
				//----获取估值当天总的明细到证券代码的ETF交易数量----//
				
				//计算实际可配数量
				if(makeUpBasketCount != 0){
					factAmount = operDeal.cutDigit(YssD.div(sumAmount, makeUpBasketCount),0);
				}
				
				stockList = (ETFStockListBean) hmStockList.get(securityCode + " " + portCode);
				if (stockList != null) {
					amount = stockList.getAmount();//股票栏中的股票数量
				}
				
				//若申购的篮子数不等于赎回的篮子数
				if(basketOfSG != basketOfSH){
					//如果为净申购，
					if (basketOfSG > basketOfSH) {
						if(buyAmount == 0){					
							canMakeUp = false;
						}

						//成交单价 = round(买入总成本(不包含费用)/买入总数量,ETF参数设置中的单位成本保留位数)
						refTradeUnitCost = YssD.round(YssD.div(buyTradeMoney,buyAmount), unitDigit);
						
						if(time == 2){
						//费用单价 = 补票的总成交费用/估值当天的所有的补票数量，保留位数根据参数设置中成本保留位数设置
						refFeeUnitCost = YssD.round(YssD.div(buyFee, sumMakeUpAmount), unitDigit);
						}
						
						if(bs.equals("B")){
							//补票数量＝（赎回的篮子数*替代数量+买入数量）/申购篮子数;
							refMakeUpAmount = operDeal.cutDigit(YssD.div((
									YssD.mul(basketOfSH, amount) + buyAmount), basketOfSG),0);
							
							// 单位成本（原币）= 拿成交单价+费用单价
							refUnitCost = YssD.add(refTradeUnitCost, refFeeUnitCost);
						}
						else{
							//补票数量 = 股票栏中的股票数量
							refMakeUpAmount = amount;
							
							// 单位成本（原币）= 拿成交单价 - 费用单价
							refUnitCost = YssD.sub(refTradeUnitCost, refFeeUnitCost);
						}
			
//						// 单位成本 = 当天所有买入本支股票的总成本（含费用） / 本支股票所有买入的数量
//						refUnitCost = YssD.div(buyTotalCost, buyAmount);
					}
					
					// 净赎回
					//应补数量 = 替代数量;
					
					if (basketOfSG < basketOfSH) {
						if(sellAmount == 0){
							canMakeUp = false;
						}
						
						//成交单价 = round(卖出总成本(不包含费用)/卖出总数量,ETF参数设置中的单位成本保留位数)
						refTradeUnitCost = YssD.round(YssD.div(sellTradeMoney,sellAmount), unitDigit);
						
						if(time == 2){
						//费用单价 = 补票的总成交费用/估值当天的所有的补票数量，保留位数根据参数设置中成本保留位数设置
						refFeeUnitCost = YssD.round(YssD.div(sellFee, sumMakeUpAmount), unitDigit);
						}
						
						if(bs.equals("S")){
							//补票数量＝（申购的篮子数*替代数量+卖出数量）/赎回篮子数;
							refMakeUpAmount = operDeal.cutDigit(YssD.div((
									YssD.mul(basketOfSG, amount) + sellAmount), basketOfSH),0);
							
							if(time == 2){
								refUnitCost = YssD.sub(refTradeUnitCost, refFeeUnitCost);
							}
						}
						else{
							//补票数量 = 股票栏中的股票数量
							refMakeUpAmount = amount;
							
							if(time == 2){
								refUnitCost = YssD.add(refTradeUnitCost, refFeeUnitCost);
							}
						}
						
//						//单位成本 = 当天所有卖出本支股票的总成本（含费用） / 本支股票所有卖出的数量
//						refUnitCost = YssD.div(sellTotalCost, sellAmount);
					}
				}
				else{//若申购的篮子数等于赎回的篮子数
					refMakeUpAmount = amount;
					refUnitCost = price;//单位成本 = 最近的收盘价 
				}
				
				if(amount != 0 && refMakeUpAmount > amount){
					refMakeUpAmount = amount;
				}
				
				//补票数量 = 补票数量 × 篮子数
				refMakeUpAmount = YssD.mul(refMakeUpAmount, braketNum);
				
				if (time == 2) {
					// 剩余数量 = 剩余数量 - 补票数量
					refRemaindAmount -= refMakeUpAmount;

					refUnitCost = YssD.round(refUnitCost, unitDigit);

					// 补票成本（原币）：单位成本*补票数量
					refOMakeUpCost = YssD.mul(refUnitCost, refMakeUpAmount);

					// 补票成本（本币）：补票成本（原币）* 汇率，保留2位小数。
					refHMakeUpCost = YssD.mul(refOMakeUpCost, exchangeRate);
					
					// 若为申购
					if (bs.equals("B")) {
						// 应付替代款本币 = 替代金额 × 补票数量/替代数量- round(补票本币成本,2)
						refHPReplaceCash = YssD.sub(
								YssD.div(
										YssD.mul(
												hreplaceCash, 
												refMakeUpAmount), 
										replaceAmount), 
								YssD.round(refHMakeUpCost, 2));
					}
					// 若为赎回
					if (bs.equals("S")) {
						// 用当天的补票的总成本
						refHPReplaceCash = refHMakeUpCost;
					}

					// 应付替代款（本币）= 应付替代款（原币）× 汇率
					// refHPReplaceCash = YssD.mul(refOPReplaceCash,
					// exchangeRate);
					refOPReplaceCash = refHPReplaceCash;

					// 可退替代款（本币）发生额 = 申购赎回时的可退替代款（本币）* 补票数量/替代数量
					refHCReplaceCash = YssD.mul(hcreplaceCash, YssD.div(
							refMakeUpAmount, replaceAmount));

					refOCReplaceCash = refHCReplaceCash; // 可退替代款（原币）发生额

					refOCanRepCash = 0; // 可退替代款（原币）余额

					// 若有上次补票的数据 则 可退替代款（本币）余额 =
					// 上次补票的可退替代款（本币）余额 - 本次补票的可退替代款（本币）发生额
					if (refHCanRepCash != 0) {
						refHCanRepCash -= refHCReplaceCash;
					} else {// 可退替代款（本币）余额 = 交易结算明细表中的可退替代款（本币） -
							// 本次补票的可退替代款（本币）发生额
						refHCanRepCash = hcreplaceCash - refHCReplaceCash;
					}

					// refHCanRepCash = YssD.mul(ocreplaceCash, exchangeRate);
					// //可退替代款（本币）余额

					refOCRefundSum = 0;// 应退合计（本币）先直接赋值为0

					// refHCRefundSum = YssD.mul(refOCRefundSum, exchangeRate);
					refHCRefundSum = refOCRefundSum;

					// 获取最大的关联编号
					if (hmMaxRefNum.get(num) != null) {
						maxRefNum = Integer.parseInt((String) hmMaxRefNum
								.get(num));
					}

					if (maxRefNum != 0) {
						refRefNum = maxRefNum + 1;
					} else {// 若交易结算关联明细表中没有申请编号为num的相关数据
						refRefNum = 1;
					}

					if (hmMakeUpInfo.get(num) != null) {
						hmMaxRefNum.put(num, String.valueOf(refRefNum));// 设置当前的关联编号为最大的关联编号
					}

					// 清算标识 = 当剩余数量不为0时，清算标识为N，当剩余数量为0时，清算标识为Y。
					if (refRemaindAmount == 0) {
						if (tradeDayNum == dealDayNum) {// 同时实际的交易天数等于ETF设置中设置的交易天数
							refSettleMark = "Y";
							// 退款日期：默认为9998-12-31，当清算标识为Y，则根据ETF基本参数设置中
							// 应付替代款的结转天数，来推算退款日期。若ETF参数设置中设置在T + N日对
							// 应付替代款进行结转，则退款日期 =剩余数量等于0的日期+ N个工作日

							if (bs.equals("B")) {
								holiday.parseRowStr((String) hmHoliday
										.get(portCode + ",sgdealreplace")
										+ "\t"
										+ dealReplace
										+ "\t"
										+ buyDate.toString());
							}
							if (bs.equals("S")) {
								holiday.parseRowStr((String) hmHoliday
										.get(portCode + ",shdealreplace")
										+ "\t"
										+ dealReplace
										+ "\t"
										+ buyDate.toString());
							}
							refRefundDate = YssFun.parseDate(holiday
									.getOperValue("getWorkDate"), "yyyy-MM-dd");// 格式化
						} else {
							refSettleMark = "N";
						}
					} else {
						refSettleMark = "N";
					}
				}
				
				refMakeUpDate = tradeDate;
				
				tradeSettleDelRef.setDataDirection(String.valueOf(refDataDirection));// 数据方向
				tradeSettleDelRef.setSettleMark(refSettleMark);// 清算标识
				tradeSettleDelRef.setRemaindAmount((int) refRemaindAmount);// 剩余数量
				tradeSettleDelRef.setRefNum(String.valueOf(refRefNum));// 关联编号
				tradeSettleDelRef.setNum(num);// 申请编号
				tradeSettleDelRef.setRefundDate(refRefundDate);// 退款日期
				tradeSettleDelRef.setDataMark("0");// 数据标识
				tradeSettleDelRef.setFactAmount(factAmount);//实际补票数量
				
				if(time == 1){
					tradeSettleDelRef.setDeleteMark("D");//删除标志
				}
				if(time == 2 && tradeSettleDelRef.getDeleteMark().equals("D")){
					tradeSettleDelRef.setDeleteMark("");//删除标志
				}
				if(!canMakeUp){
					tradeSettleDelRef.setMakeUpDate(YssFun.parseDate("9998-12-31"));// 补票日期
				}
				else{
					tradeSettleDelRef.setMakeUpDate(refMakeUpDate);// 补票日期
				}

				if (canMakeUp) {
					tradeSettleDelRef.setUnitCost(refUnitCost);// 单位成本
					tradeSettleDelRef.setoMakeUpCost(YssFun.roundIt(refOMakeUpCost, 2));// 补票成本（原币）
					tradeSettleDelRef.sethMakeUpCost(YssFun.roundIt(refHMakeUpCost, 2));// 补票成本（本币）
					tradeSettleDelRef.setMakeUpAmount(refMakeUpAmount);// 补票数量
					tradeSettleDelRef.setOpReplaceCash(YssFun.roundIt(refOPReplaceCash, 2));// 应付替代款（原币）
					tradeSettleDelRef.setHpReplaceCash(YssFun.roundIt(refHPReplaceCash, 2));// 应付替代款（本币）
					tradeSettleDelRef.setOcReplaceCash(YssFun.roundIt(refOCReplaceCash, 2));// 可退替代款发生额（原币）
					tradeSettleDelRef.setHcReplaceCash(YssFun.roundIt(refHCReplaceCash, 2));// 可退替代款发生额（本币）
					tradeSettleDelRef.setOCanRepCash(YssFun.roundIt(refOCanRepCash, 2));// 可退替代款（原币）合计
					tradeSettleDelRef.setHCanRepCash(YssFun.roundIt(refHCanRepCash, 2));// 可退替代款（本币）合计
					tradeSettleDelRef.setOcRefundSum(YssFun.roundIt(refOCRefundSum, 2));// 应退合计（原币）
					tradeSettleDelRef.setHcRefundSum(YssFun.roundIt(refHCRefundSum, 2));// 应退合计（本币）
					tradeSettleDelRef.setTradeUnitCost(refTradeUnitCost);//成交单价
					
					if(time == 2){
						tradeSettleDelRef.setFeeUnitCost(refFeeUnitCost);//费用单价
					}

					alTradeSettleDel.set(0, tradeSettleDelRef);
					tradeSettleDel.setAlTradeSettleDel(alTradeSettleDel);

					hmMakeUpInfo.put(num, tradeSettleDel);
				}
			}
			else{
				hmMakeUpInfo.remove(num);
			}
			
			hmTransfer.put("hmCurrentSGInfo", hmCurrentSGInfo);
			hmTransfer.put("hmTradeInfo", hmTradeInfo);
			hmTransfer.put("hmSumMakeUpInfos", hmSumMakeUpInfos);
			hmTransfer.put("hmMaxNum", hmMaxNum);
			hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
			hmTransfer.put("hmMaxRefNum", hmMaxRefNum);
			hmTransfer.put("hmSHSumMakeUpInfos", hmSHSumMakeUpInfos);
			hmTransfer.put("hmStockList", hmStockList);
			
			if(time == 2){
				hmTransfer.put("hmETFTradeInfo", hmETFTradeInfo);
			}
			
			return hmTransfer;
		}
		catch(Exception e){
			throw new YssException("根据轧差补票的补票方式处理需要补票的数据出错！", e);
		}
	}
}
