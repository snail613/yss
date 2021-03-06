package com.yss.main.etfoperation.dealmakeup;

import java.util.ArrayList;
import java.util.HashMap;

import com.yss.main.etfoperation.etfaccbook.CreateAccBookRefData;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.util.YssException;

public class dealActualTimeBean extends CtlDealMakeUp {
	
	/**
	 * 构造函数
	 */
	public dealActualTimeBean() {
		
	}

	/**
	 * 根据实时补票的补票方式处理需要补票的数据
	 * @throws YssException
	 */
	public HashMap dealActualTime(ETFTradeSettleDetailBean tradeSettleDel)throws YssException{
		HashMap hmTransfer = new HashMap();
		try{
			hmTransfer.put("hmCurrentSGInfo", hmCurrentSGInfo);
			hmTransfer.put("hmTradeInfo", hmTradeInfo);
			hmTransfer.put("hmSumMakeUpInfos", hmSumMakeUpInfos);
			hmTransfer.put("hmMaxNum", hmMaxNum);
			hmTransfer.put("hmMakeUpInfo", hmMakeUpInfo);
			hmTransfer.put("hmMaxRefNum", hmMaxRefNum);

			return hmTransfer;
		}
		catch(Exception e){
			throw new YssException("根据实时补票的补票方式处理需要补票的数据出错！", e);
		}
	}
}
