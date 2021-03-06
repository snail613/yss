package com.yss.main.etfoperation.dealmakeup;

import java.util.ArrayList;
import java.util.HashMap;

import com.yss.main.etfoperation.etfaccbook.CreateAccBookRefData;
import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.util.YssException;

public class CtlDealMakeUp extends CtlETFAccBook {
	protected HashMap hmHoliday = null;
	protected HashMap hmbasket = null;
	protected HashMap etfParam = null;
	protected HashMap hmCurrentSGInfo = null;
	protected HashMap hmTradeInfo = null;
	protected HashMap hmSumMakeUpInfos = null;
	protected HashMap hmMaxNum = null;
	protected HashMap hmMakeUpInfo = null;
	protected HashMap hmMaxRefNum = null;
	//protected java.util.Date tradeDate = null;//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
	protected ArrayList alNum = null;
	protected CreateAccBookRefData accBookRef = null;
	protected HashMap hmStockList = null;
	protected HashMap hmSHSumMakeUpInfos = null;
	protected HashMap hmETFTradeInfo = null;
	protected HashMap hmTotalAmount = null;
	
	public HashMap getHmTotalAmount() {
		return hmTotalAmount;
	}

	public void setHmTotalAmount(HashMap hmTotalAmount) {
		this.hmTotalAmount = hmTotalAmount;
	}

	public HashMap getHmETFTradeInfo() {
		return hmETFTradeInfo;
	}

	public void setHmETFTradeInfo(HashMap hmETFTradeInfo) {
		this.hmETFTradeInfo = hmETFTradeInfo;
	}

	public HashMap getHmSHSumMakeUpInfos() {
		return hmSHSumMakeUpInfos;
	}

	public void setHmSHSumMakeUpInfos(HashMap hmSHSumMakeUpInfos) {
		this.hmSHSumMakeUpInfos = hmSHSumMakeUpInfos;
	}

	public HashMap getHmStockList() {
		return hmStockList;
	}

	public void setHmStockList(HashMap hmStockList) {
		this.hmStockList = hmStockList;
	}

	public CreateAccBookRefData getAccBookRef() {
		return accBookRef;
	}

	public void setAccBookRef(CreateAccBookRefData accBookRef) {
		this.accBookRef = accBookRef;
	}

	/**
	 * 构造函数
	 */
	public CtlDealMakeUp() {
		
	}
	
	public HashMap getHmHoliday() {
		return hmHoliday;
	}

	public void setHmHoliday(HashMap hmHoliday) {
		this.hmHoliday = hmHoliday;
	}

	public HashMap getHmbasket() {
		return hmbasket;
	}

	public void setHmbasket(HashMap hmbasket) {
		this.hmbasket = hmbasket;
	}

	public HashMap getEtfParam() {
		return etfParam;
	}

	public void setEtfParam(HashMap etfParam) {
		this.etfParam = etfParam;
	}

	public HashMap getHmCurrentSGInfo() {
		return hmCurrentSGInfo;
	}

	public void setHmCurrentSGInfo(HashMap hmCurrentSGInfo) {
		this.hmCurrentSGInfo = hmCurrentSGInfo;
	}

	public HashMap getHmTradeInfo() {
		return hmTradeInfo;
	}

	public void setHmTradeInfo(HashMap hmTradeInfo) {
		this.hmTradeInfo = hmTradeInfo;
	}

	public HashMap getHmSumMakeUpInfos() {
		return hmSumMakeUpInfos;
	}

	public void setHmSumMakeUpInfos(HashMap hmSumMakeUpInfos) {
		this.hmSumMakeUpInfos = hmSumMakeUpInfos;
	}

	public HashMap getHmMaxNum() {
		return hmMaxNum;
	}

	public void setHmMaxNum(HashMap hmMaxNum) {
		this.hmMaxNum = hmMaxNum;
	}

	public HashMap getHmMakeUpInfo() {
		return hmMakeUpInfo;
	}

	public void setHmMakeUpInfo(HashMap hmMakeUpInfo) {
		this.hmMakeUpInfo = hmMakeUpInfo;
	}

	public HashMap getHmMaxRefNum() {
		return hmMaxRefNum;
	}

	public void setHmMaxRefNum(HashMap hmMaxRefNum) {
		this.hmMaxRefNum = hmMaxRefNum;
	}

	/*public java.util.Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(java.util.Date tradeDate) {
		this.tradeDate = tradeDate;
	}*/

	public ArrayList getAlNum() {
		return alNum;
	}

	public void setAlNum(ArrayList alNum) {
		this.alNum = alNum;
	}
	
	/**
	 * 处理需要补票的数据
	 * 
	 * @throws YssException
	 */
	public HashMap dealMakeUpInfo(int time, HashMap hmSumMakeUpAmount) throws YssException {
		ETFTradeSettleDetailBean tradeSettleDel = null;
		String num = ""; // 申请编号
		String portCode = ""; // 组合代码
		ETFParamSetBean paramSet = null;// ETF参数的实体类
		String makeUpMethod = null; // 补票方式
		DealSequenceBean sequence = null;
		DealAABean aa = null;
		dealActualTimeBean actualTime = null;
		dealOffSetBalanceBean offSetBalance = null;
		DealMakeUpMustBean dealBoshi = null;
		HashMap hmTransfer = new HashMap();
		try {
			for (int i = 0; i < alNum.size(); i++) {
				num = (String) alNum.get(i);// 获取申请编号

				// 根据申请编号获取需要补票的交易结算明细实体类
				tradeSettleDel = (ETFTradeSettleDetailBean) hmMakeUpInfo.get(num);

				if (tradeSettleDel != null) {
					portCode = tradeSettleDel.getPortCode();// 组合代码
					paramSet = (ETFParamSetBean) etfParam.get(portCode);// 根据组合代码获取ETF参数设置

					if (paramSet != null) {
						makeUpMethod = paramSet.getSupplyMode(); // 获取组合代码对应的补票方式
					} else {
						throw new YssException("请在ETF参数设置中设置" + portCode + "组合的相关参数！");
					}
					
					//若补票方式为先到先得
					if(makeUpMethod.equals("0")){
						sequence = new DealSequenceBean();
						sequence.setYssPub(pub);
						
						sequence.setEtfParam(etfParam);
						sequence.setHmHoliday(hmHoliday);
						sequence.setHmCurrentSGInfo(hmCurrentSGInfo);
						sequence.setHmbasket(hmbasket);
						sequence.setHmMakeUpInfo(hmMakeUpInfo);
						sequence.setHmMaxNum(hmMaxNum);
						sequence.setHmMaxRefNum(hmMaxRefNum);
						sequence.setHmStockList(hmStockList);
						sequence.setHmSumMakeUpInfos(hmSumMakeUpInfos);
						sequence.setHmTradeInfo(hmTradeInfo);
						sequence.setTradeDate(tradeDate);
						
						hmTransfer = sequence.dealSequence(tradeSettleDel);
					}
					
					//均摊
					if(makeUpMethod.equals("1"))
					{
						aa = new DealAABean();
						aa.setYssPub(pub);
						
						aa.setEtfParam(etfParam);
						aa.setHmHoliday(hmHoliday);
						aa.setHmCurrentSGInfo(hmCurrentSGInfo);
						aa.setHmbasket(hmbasket);
						aa.setHmMakeUpInfo(hmMakeUpInfo);
						aa.setHmMaxNum(hmMaxNum);
						aa.setHmMaxRefNum(hmMaxRefNum);
						aa.setHmStockList(hmStockList);
						aa.setHmSumMakeUpInfos(hmSumMakeUpInfos);
						aa.setHmTradeInfo(hmTradeInfo);
						aa.setTradeDate(tradeDate);
						
						hmTransfer = aa.dealAA(tradeSettleDel);
					}
					
					//实时补票
					if(makeUpMethod.equals("2")){
						actualTime = new dealActualTimeBean();
						actualTime.setYssPub(pub);
						
						actualTime.setEtfParam(etfParam);
						actualTime.setHmHoliday(hmHoliday);
						actualTime.setHmCurrentSGInfo(hmCurrentSGInfo);
						actualTime.setHmbasket(hmbasket);
						actualTime.setHmMakeUpInfo(hmMakeUpInfo);
						actualTime.setHmMaxNum(hmMaxNum);
						actualTime.setHmMaxRefNum(hmMaxRefNum);
						actualTime.setHmStockList(hmStockList);
						actualTime.setHmSumMakeUpInfos(hmSumMakeUpInfos);
						actualTime.setHmTradeInfo(hmTradeInfo);
						actualTime.setTradeDate(tradeDate);
						
						hmTransfer = actualTime.dealActualTime(tradeSettleDel);
					}
					
					//轧差补票
					if(makeUpMethod.equals("3")){
						offSetBalance = new dealOffSetBalanceBean();
						offSetBalance.setYssPub(pub);
						
						offSetBalance.setEtfParam(etfParam);
						offSetBalance.setHmHoliday(hmHoliday);
						offSetBalance.setHmCurrentSGInfo(hmCurrentSGInfo);
						offSetBalance.setHmbasket(hmbasket);
						offSetBalance.setHmMakeUpInfo(hmMakeUpInfo);
						offSetBalance.setHmMaxNum(hmMaxNum);
						offSetBalance.setHmMaxRefNum(hmMaxRefNum);
						offSetBalance.setHmStockList(hmStockList);
						offSetBalance.setHmSumMakeUpInfos(hmSumMakeUpInfos);
						offSetBalance.setHmTradeInfo(hmTradeInfo);
						offSetBalance.setTradeDate(tradeDate);
						offSetBalance.setHmSHSumMakeUpInfos(hmSHSumMakeUpInfos);
						offSetBalance.setHmETFTradeInfo(hmETFTradeInfo);
						
						hmTransfer = offSetBalance.dealOffSetBalance(tradeSettleDel,time,hmSumMakeUpAmount);
					}
					
					//若为博时的补票
					if(makeUpMethod.equals("6")){
						dealBoshi = new DealMakeUpMustBean();
						dealBoshi.setYssPub(pub);
						
						dealBoshi.setEtfParam(etfParam);
						dealBoshi.setHmHoliday(hmHoliday);
						dealBoshi.setHmCurrentSGInfo(hmCurrentSGInfo);
						dealBoshi.setHmbasket(hmbasket);
						dealBoshi.setHmMakeUpInfo(hmMakeUpInfo);
						dealBoshi.setHmMaxNum(hmMaxNum);
						dealBoshi.setHmMaxRefNum(hmMaxRefNum);
						dealBoshi.setHmStockList(hmStockList);
						dealBoshi.setHmSumMakeUpInfos(hmSumMakeUpInfos);
						dealBoshi.setHmTradeInfo(hmTradeInfo);
						dealBoshi.setTradeDate(tradeDate);
						dealBoshi.setHmSHSumMakeUpInfos(hmSHSumMakeUpInfos);
						dealBoshi.setHmTotalAmount(hmTotalAmount);
						
						hmTransfer = dealBoshi.dealMakeUp(tradeSettleDel);
					}
				}
				
				hmCurrentSGInfo = (HashMap)hmTransfer.get("hmCurrentSGInfo");
				hmTradeInfo = (HashMap)hmTransfer.get("hmTradeInfo");
				hmETFTradeInfo = (HashMap)hmTransfer.get("hmETFTradeInfo");
				hmSumMakeUpInfos = (HashMap)hmTransfer.get("hmSumMakeUpInfos");
				hmMaxNum = (HashMap)hmTransfer.get("hmMaxNum");
				hmMakeUpInfo = (HashMap)hmTransfer.get("hmMakeUpInfo");
				hmMaxRefNum = (HashMap)hmTransfer.get("hmMaxRefNum");
				hmSHSumMakeUpInfos = (HashMap)hmTransfer.get("hmSHSumMakeUpInfos");
				hmStockList = (HashMap)hmTransfer.get("hmStockList");
				hmTotalAmount = (HashMap)hmTransfer.get("hmTotalAmount");
			}
			
			hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
			hmTransfer.put("hmMaxRefNum", hmMaxRefNum);
			hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
			
			return hmTransfer;
		} catch (Exception e) {
			throw new YssException("处理需要补票的数据出错！", e);
		}
	}
}
