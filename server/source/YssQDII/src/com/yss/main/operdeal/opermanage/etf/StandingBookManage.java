package com.yss.main.operdeal.opermanage.etf;

import java.util.Date;

import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.util.YssException;
import com.yss.main.etfoperation.etfaccbook.*;

/**
 * 生成台帐
 * MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A 
 * @author panjunfang
 * 20091026 create
 */
public class StandingBookManage extends BaseOperManage {

	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.sPortCode = portCode;
		this.dDate = dDate;
	}

	public void doOpertion() throws YssException {
		crtStandingBook();
	}

	private void crtStandingBook() throws YssException {
		CtlETFAccBook ctlAccBook = null;		
		try{
			ctlAccBook = new CtlETFAccBook();
			ctlAccBook.setYssPub(pub);
			ctlAccBook.setPortCodes(sPortCode);
			ctlAccBook.setStartDate(dDate);
			ctlAccBook.setEndDate(dDate);
			ctlAccBook.setTradeDate(dDate);
			ctlAccBook.getOperValue("ETFValuation");
		}catch(Exception e){
			throw new YssException("生成台帐出错！",e);
		}
	}

}
