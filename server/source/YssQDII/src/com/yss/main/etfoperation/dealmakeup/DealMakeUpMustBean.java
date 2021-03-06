package com.yss.main.etfoperation.dealmakeup;

import java.util.ArrayList;
import java.util.HashMap;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFStockListBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.operdata.TradeSubBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class DealMakeUpMustBean extends CtlDealMakeUp{

	public DealMakeUpMustBean() {
		
	}
	
	/**
	 * 根据轧差补票的补票方式处理需要补票的数据
	 * @throws YssException
	 */
	public HashMap dealMakeUp(ETFTradeSettleDetailBean tradeSettleDel)throws YssException{
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;
		String num = ""; // 申请编号
		String portCode = ""; // 组合代码
		String securityCode = ""; // 证券代码
		String bs = ""; // 买卖标志
		java.util.Date buyDate = null; // 申赎日期
		double replaceAmount = 0; // 替代数量
		double hreplaceCash = 0;// 替代金额（本币）
		double hcreplaceCash = 0; // 可退替代款（本币）

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
		int refDataDirection = 0; // 数据方向 申购 -- 1 赎回 -- -1
		String refSettleMark = ""; // 清算标识 清算 -- Y 未清算 -- N
		java.util.Date refRefundDate = null;// 退款日期

		ETFParamSetBean paramSet = null;// ETF参数的实体类

//		ArrayList alTradeInfo = null;// 用于储存相关证券的交易数据
		
		TradeSubBean tradeSub = null;// 交易子表实体类
		int startDateNum = 0;// 从几个工作日开始补票
		java.util.Date canMakeUpDate = null;// 可以开始补票的日期
		int maxDealDayNum = 0;//强制处理天数

		int maxRefNum = 0;// 相关申请编号对应的最大的关联编号
		int refRefNum = 0;// 关联编号

		int dealReplace = 0;// 应付替代款结转

		ArrayList alTradeSettleDel = null;
		double basketOfSG = 0;// 申购的篮子数
		double basketOfSH = 0;// 赎回的篮子数
		String oneGradeMktCode = "";//一级市场代码
		double braketNum = 0;//篮子数
		double amount = 0;//股票篮中的股票数量
		int unitDigit = 0;//单位成本保留为数
		double exchangeRate = 0;//汇率
//		int amountInd = 0;//数据方向
		ETFStockListBean stockList = null;
		HashMap hmTransfer = new HashMap();
		boolean canMakeUp = true;//能补票
		EachExchangeHolidays holiday = null;//节假日获取类
		double buyAmount = 0;//买入的交易数量
		double sellAmount = 0;//卖出的交易数量
		double buyTotalCost = 0;//买入的清算金额
		double sellTotalCost = 0;//卖出的清算金额
		java.util.Date marketValueDate = null;
		java.util.Date mustMakeUpDate = null;//强制处理日期
		double totalAmount = 0;//申购或赎回的证券代码相关的估值当天的需要补票的数量
		String maxNum = null;//申购或赎回的相关证券代码的最大的申请编号
		double remaindTradeAmount = 0;//可以用于补票的交易数量
		try{
			holiday = new EachExchangeHolidays();
			holiday.setYssPub(pub);
			
			portCode = tradeSettleDel.getPortCode();// 组合代码
			paramSet = (ETFParamSetBean) etfParam.get(portCode);// 根据组合代码获取ETF参数设置
			
			securityCode = tradeSettleDel.getSecurityCode();// 证券代码
			
			buyDate = tradeSettleDel.getBuyDate();// 申赎日期
			replaceAmount = tradeSettleDel.getReplaceAmount();// 替代数量
			bs = tradeSettleDel.getBs();// 买卖标志

			if (paramSet != null) {
				startDateNum = paramSet.getBeginSupply();// 从几个工作日开始补票

				oneGradeMktCode = paramSet.getOneGradeMktCode();//一级市场代码
				
				unitDigit = paramSet.getSUnitdigit();//单位成本保留为数
				
				maxDealDayNum = paramSet.getLastestDealDayNum();// 最长几日补票完成
				
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
			hreplaceCash = tradeSettleDel.getHReplaceCash();//替代金额（本币）
			hcreplaceCash = tradeSettleDel.getHcReplaceCash();// 可退替代款(本币)
			marketValueDate = tradeSettleDel.getMarketValueDate();//行情日期
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
			
 			if(securityCode.equals(" ")){
				hmTransfer.put("hmCurrentSGInfo", hmCurrentSGInfo);
				hmTransfer.put("hmTradeInfo", hmTradeInfo);
				hmTransfer.put("hmSumMakeUpInfos", hmSumMakeUpInfos);
				hmTransfer.put("hmMaxNum", hmMaxNum);
				hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
				hmTransfer.put("hmMaxRefNum", hmMaxRefNum);
				hmTransfer.put("hmSHSumMakeUpInfos", hmSHSumMakeUpInfos);
				hmTransfer.put("hmStockList", hmStockList);
				hmTransfer.put("hmTotalAmount", hmTotalAmount);
				
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
 			
			holiday.parseRowStr((String) hmHoliday.get(portCode + ",beginsupply")+ "\t"+ startDateNum+ "\t"+ buyDate.toString());
			
			// 根据从几个工作日开始补票和申赎日期得到可以开始补票的日期
			canMakeUpDate = YssFun.parseDate(holiday.getOperValue("getWorkDate"), "yyyy-MM-dd");// 格式化
				
			holiday.parseRowStr((String) hmHoliday.get(portCode + ",lastestdealdaynum")+ "\t"+ maxDealDayNum+ "\t"+ buyDate.toString());
			
			// 根据从几个工作日开始强制处理的天数得到强制处理的日期
			mustMakeUpDate = YssFun.parseDate(holiday.getOperValue("getWorkDate"), "yyyy-MM-dd");// 格式化
			
			if(marketValueDate == null || (marketValueDate != null && !marketValueDate.equals(tradeDate))){
				canMakeUp = false;//不能补票
			}
			
			// 若开始补票的日期在估值日期之前 或 等于估值日期 且 估值当天有行情 且估值日期不在强制处理日期之后 的情况下 则 可以补票
			if ((canMakeUpDate.before(tradeDate) || canMakeUpDate.equals(tradeDate)) 
				&& !tradeDate.after(mustMakeUpDate)) {
				
				// 获取证券代码对应的交易数据
				tradeSub = (TradeSubBean) hmTradeInfo.get(bs.equals("B")? securityCode + "1":securityCode + "-1");

				if(hmbasket.get(buyDate.toString() + "\t"+ portCode + "\tS\t" + oneGradeMktCode) != null){
					basketOfSG = Double.parseDouble((String) hmbasket.
							get(buyDate.toString() + "\t"+ portCode + "\tS\t" + oneGradeMktCode));//申购的篮子数
				}
				if(hmbasket.get(buyDate.toString() + "\t"+ portCode + "\tB\t" + oneGradeMktCode) != null){
					basketOfSH = Double.parseDouble((String) hmbasket.
							get(buyDate.toString() + "\t"+ portCode + "\tB\t" + oneGradeMktCode));//赎回的篮子数
				}
				
				stockList = (ETFStockListBean) hmStockList.get(securityCode + " " + portCode);
				
				if (stockList != null) {
					amount = stockList.getAmount();//股票栏中的股票数量
				}
				
				// ---获取估值当天总的明细到证券代码的交易数量----//
				if(tradeSub != null){				
					if(bs.equals("B")){
						buyAmount = tradeSub.getTradeAmount();
						buyTotalCost = tradeSub.getTotalCost();
					}
					else{
						sellAmount = tradeSub.getTradeAmount();
						sellTotalCost = tradeSub.getTotalCost();
					}
				}
				//----获取估值当天总的明细到证券代码的交易数量----//
				
				maxNum = (String)hmMaxNum.get(securityCode + bs);
				
				totalAmount = Double.parseDouble((String)hmTotalAmount.get(securityCode + bs));
				
				//若申购的篮子数不等于赎回的篮子数
				if(basketOfSG != basketOfSH){
					//如果为净申购
					if (basketOfSG > basketOfSH) {
						if(buyAmount == 0){					
							canMakeUp = false;//若没有交易数据 则不能补票
						}
						
						if(bs.equals("B")){
							if(tradeSub != null){
								remaindTradeAmount = tradeSub.getHandAmount(); // 获取明细的未用于补票的交易数量
								
								if(num.equals(maxNum)){//若为最大的相关申购的证券代码的申请编号，则轧差处理
									//补票数量 = 可用于补票的数量
									refMakeUpAmount = YssD.round(remaindTradeAmount, 0);	
								}
								//否则 可用于补票的数量 = round(当前需要补票的数量/
								//估值当天申购的相关证券代码的所有需要补票的数量×
								//当天的交易数据中买入的相关证券代码的数量,0); 四舍五入
								else{
									//补票数量 = 可用于补票的数量
									refMakeUpAmount = YssD.round(refRemaindAmount/totalAmount * buyAmount,0);
								}
								
								if(amount != 0 && refMakeUpAmount > amount * (basketOfSG - basketOfSH)){
									refMakeUpAmount = amount * (basketOfSG - basketOfSH);
								}
								
								tradeSub.setHandAmount(remaindTradeAmount - refMakeUpAmount);
							}
							
						    //补票单位成本（原币）是指当天所有买入本支股票的总成本/当天买入本支股票的数量
							refUnitCost = YssD.div(buyTotalCost,buyAmount);
						}
						else{
							canMakeUp = false;//若为净申购 则 买卖标志为赎回的话  就不能补票
						}
					}
					
					// 净赎回
					//应补数量 = 替代数量;
					if (basketOfSG < basketOfSH) {
						if(sellAmount == 0){
							canMakeUp = false;//若没有交易数据 则不能补票
						}
						if(bs.equals("S")){
							if(tradeSub != null){
								remaindTradeAmount = tradeSub.getHandAmount(); // 获取明细的未用于补票的交易数量
								
								if(num.equals(maxNum)){//若为最大的相关申购的证券代码的申请编号，则轧差处理
									//补票数量 = 可用于补票的数量
									refMakeUpAmount = YssD.round(remaindTradeAmount, 0);
								}
								//否则 可用于补票的数量 = round(当前需要补票的数量/
								//估值当天赎回的相关证券代码的所有需要补票的数量×
								//当天的交易数据中卖出的相关证券代码的数量,0); 四舍五入
								else{
									//补票数量 = 可用于补票的数量
									refMakeUpAmount = YssD.round(refRemaindAmount/totalAmount * sellAmount,0);
								}
								
								if(amount != 0 && refMakeUpAmount > amount * (basketOfSH - basketOfSG)){
									refMakeUpAmount = amount * (basketOfSH - basketOfSG);
								}
								
								tradeSub.setHandAmount(remaindTradeAmount - refMakeUpAmount);
							}
							
						    //补票单位成本（原币） = 当天所有卖出本支股票的总成本/当天卖出本支股票的数量
							refUnitCost = YssD.div(sellTotalCost,sellAmount);
						}
						else{
							canMakeUp = false;//若为净赎回 则 买卖标志为申购的话  就不能补票
						}
					}
				}
				else{//若申购的篮子数等于赎回的篮子数
					canMakeUp = false;//若为当天申购的篮子数等于赎回的篮子数 则不能补票
				}
				
				hmTradeInfo.put(securityCode, tradeSub);
				
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

				refOPReplaceCash = refHPReplaceCash;

				// 可退替代款（本币）发生额 = 申购赎回时的可退替代款（本币）* 补票数量/替代数量
				refHCReplaceCash = YssD.mul(hcreplaceCash, YssD.div(refMakeUpAmount, replaceAmount));

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
				
				refOCRefundSum = 0;// 应退合计（本币）先直接赋值为0

				refHCRefundSum = refOCRefundSum;

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

				// 清算标识 = 当剩余数量不为0时，清算标识为N，当剩余数量为0时，清算标识为Y。
				if (refRemaindAmount == 0) {
					refSettleMark = "Y";
					
					// 退款日期：默认为9998-12-31，当清算标识为Y，则根据ETF基本参数设置中
					// 应付替代款的结转天数，来推算退款日期。若ETF参数设置中设置在T + N日对
					// 应付替代款进行结转，则退款日期 =剩余数量等于0的日期+ N个工作日
					if (bs.equals("B")) {
						holiday.parseRowStr((String) hmHoliday.get(portCode + ",sgdealreplace")
								+ "\t" + dealReplace + "\t" + YssFun.formatDate(tradeDate, "yyyy-MM-dd"));
					}
					if (bs.equals("S")) {
						holiday.parseRowStr((String) hmHoliday.get(portCode + ",shdealreplace")
								+ "\t" + dealReplace + "\t" + YssFun.formatDate(tradeDate, "yyyy-MM-dd"));
					}
					
					refRefundDate = YssFun.parseDate(holiday.getOperValue("getWorkDate"), "yyyy-MM-dd");// 格式化
				} else {
					refSettleMark = "N";
				}
				
				refMakeUpDate = tradeDate;
				
				tradeSettleDelRef.setDataDirection(String.valueOf(refDataDirection));// 数据方向
				tradeSettleDelRef.setSettleMark(refSettleMark);// 清算标识
				tradeSettleDelRef.setRemaindAmount((int) refRemaindAmount);// 剩余数量
				tradeSettleDelRef.setRefNum(String.valueOf(refRefNum));// 关联编号
				tradeSettleDelRef.setNum(num);// 申请编号
				tradeSettleDelRef.setRefundDate(refRefundDate);// 退款日期
				tradeSettleDelRef.setDataMark("0");// 数据标识
				
				if(!canMakeUp){
					tradeSettleDelRef.setMakeUpDate(YssFun.parseDate("9998-12-31"));// 补票日期
					tradeSettleDelRef.setHCanRepCash(YssFun.roundIt(hcreplaceCash, 2));// 可退替代款（本币）合计
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
			hmTransfer.put("hmTotalAmount", hmTotalAmount);
			
			return hmTransfer;
		}
		catch(Exception e){
			throw new YssException("根据博时补票的方式处理需要补票的数据出错！", e);
		}
	}
}
