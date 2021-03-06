/**@author shashijie
*  @version 创建时间：2012-5-4 下午05:29:47 STORY 2565
*  类说明 ETF中登结算明细表实体Bean
*/
package com.yss.main.operdeal.datainterface.etf.ETFLink;

import java.util.Date;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import com.yss.util.YssFun;


public class JSMXInterfaceBean extends DataBase {
	
	private String FPortCode = "";//组合代码
	private Date FClearDate = YssFun.toDate("9998-12-31");//清算日期	
	private Date FSettleDate = YssFun.toDate("9998-12-31");//结算日期
	private String FMarketCode = "";//市场代码	
	private String FTradeTypeCode = "";//业务类型
	private String FClearMark = "";//清算标志
	private String FChangeType  = "";//变动类型
	private String FTradeNum = "";//成交编号	
	private String FSeatNum = "";//席位编号
	private String FClearCode = "";//清算编号
	private String FStockholderCode = "";//股东代码
	private String FSecurityCode1 = "";//证券代码1(2级市场代码)
	private String FSecurityCode2 = "";//证券代码2
	private double FTradeAmount = 0;//成交数量
	private double FSettlePrice = 0;//结算价额
	private double FTradePrice = 0;//成交价格
	private double FClearMoney = 0;//清算金额
	private double FTotalMoney = 0;//实收实付
	private String FDesc = "";//清算描述
	private Date FDate = YssFun.toDate("9998-12-31");//数据日期
	private String FRecordType = "";//记录类型
	private String FSecurityType = "";//证券类别
	private String FResultCode = "";//结果代码
	private Date FBargainDate = YssFun.toDate("9998-12-31");//交易日期
	private double FSettleAmount = 0;//交收数量
	private String FTransactionBs = "";//交易方式
	private String FReceiptsBs = "";//交收方式
	private String FReceiptsNum = "";//交收编号
	private String FApplicationNum = "";//申请编号
	private String FConsignNum = "";//委托编号
	private Date FOtherDate = YssFun.toDate("9998-12-31");//其它日期
	private String FConsignDate = "00:00:00";//委托时间
	private String FTradDate = "00:00:00";//成交时间
	private String FProfessionUnit = "";//业务单元2
	private String FSettleNum = "";//结算参与人的清算编号
	private String FClearNum = "";//托管银行的清算编号
	private String FCurrencyType = "";//流通类型
	private String FRightsType = "";//权益类别
	private String FExtensionYear = "";//挂牌年份
	private String FBargainBs = "";//买卖标志
	private String FFundsBar = ""; //资金账号
	private String FCurrencyCode = "";//币种
	private double FStampTax = 0;//印花税
	private double FhandleTax = 0;//经手费
	private double FTransferTax = 0;//过户费
	private double FCanalTax= 0;//证管费
	private double FProcedureTax = 0;//手续费
	private double FOtherMoney1 = 0;//其它金额1
	private double FOtherMoney2 = 0;//其它金额2
	private double FOtherMoney3 = 0;//其它金额3
	
	public JSMXInterfaceBean () throws YssException {
		/*FClearDate = YssFun.toDate("9998-12-31");//清算日期	
		FSettleDate = YssFun.toDate("9998-12-31");//结算日期
		FDate = YssFun.toDate("9998-12-31");//数据日期
		FBargainDate = YssFun.toDate("9998-12-31");//交易日期
		FOtherDate = YssFun.toDate("9998-12-31");*///其它日期
	}

	/**返回 fPortCode 的值*/
	public String getFPortCode() {
		return FPortCode;
	}

	/**传入fPortCode 设置  fPortCode 的值*/
	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}

	/**返回 fClearDate 的值*/
	public Date getFClearDate() {
		return FClearDate;
	}

	/**传入fClearDate 设置  fClearDate 的值*/
	public void setFClearDate(Date fClearDate) {
		FClearDate = fClearDate;
	}

	/**返回 fSettleDate 的值*/
	public Date getFSettleDate() {
		return FSettleDate;
	}

	/**传入fSettleDate 设置  fSettleDate 的值*/
	public void setFSettleDate(Date fSettleDate) {
		FSettleDate = fSettleDate;
	}

	/**返回 fMarketCode 的值*/
	public String getFMarketCode() {
		return FMarketCode;
	}

	/**传入fMarketCode 设置  fMarketCode 的值*/
	public void setFMarketCode(String fMarketCode) {
		FMarketCode = fMarketCode;
	}

	/**返回 fTradeTypeCode 的值*/
	public String getFTradeTypeCode() {
		return FTradeTypeCode;
	}

	/**传入fTradeTypeCode 设置  fTradeTypeCode 的值*/
	public void setFTradeTypeCode(String fTradeTypeCode) {
		FTradeTypeCode = fTradeTypeCode;
	}

	/**返回 fClearMark 的值*/
	public String getFClearMark() {
		return FClearMark;
	}

	/**传入fClearMark 设置  fClearMark 的值*/
	public void setFClearMark(String fClearMark) {
		FClearMark = fClearMark;
	}

	/**返回 fChangeType 的值*/
	public String getFChangeType() {
		return FChangeType;
	}

	/**传入fChangeType 设置  fChangeType 的值*/
	public void setFChangeType(String fChangeType) {
		FChangeType = fChangeType;
	}

	/**返回 fTradeNum 的值*/
	public String getFTradeNum() {
		return FTradeNum;
	}

	/**传入fTradeNum 设置  fTradeNum 的值*/
	public void setFTradeNum(String fTradeNum) {
		FTradeNum = fTradeNum;
	}

	/**返回 fSeatNum 的值*/
	public String getFSeatNum() {
		return FSeatNum;
	}

	/**传入fSeatNum 设置  fSeatNum 的值*/
	public void setFSeatNum(String fSeatNum) {
		FSeatNum = fSeatNum;
	}

	/**返回 fClearCode 的值*/
	public String getFClearCode() {
		return FClearCode;
	}

	/**传入fClearCode 设置  fClearCode 的值*/
	public void setFClearCode(String fClearCode) {
		FClearCode = fClearCode;
	}

	/**返回 fStockholderCode 的值*/
	public String getFStockholderCode() {
		return FStockholderCode;
	}

	/**传入fStockholderCode 设置  fStockholderCode 的值*/
	public void setFStockholderCode(String fStockholderCode) {
		FStockholderCode = fStockholderCode;
	}

	/**返回 fSecurityCode1 的值*/
	public String getFSecurityCode1() {
		return FSecurityCode1;
	}

	/**传入fSecurityCode1 设置  fSecurityCode1 的值*/
	public void setFSecurityCode1(String fSecurityCode1) {
		FSecurityCode1 = fSecurityCode1;
	}

	/**返回 fSecurityCode2 的值*/
	public String getFSecurityCode2() {
		return FSecurityCode2;
	}

	/**传入fSecurityCode2 设置  fSecurityCode2 的值*/
	public void setFSecurityCode2(String fSecurityCode2) {
		FSecurityCode2 = fSecurityCode2;
	}

	/**返回 fTradeAmount 的值*/
	public double getFTradeAmount() {
		return FTradeAmount;
	}

	/**传入fTradeAmount 设置  fTradeAmount 的值*/
	public void setFTradeAmount(double fTradeAmount) {
		FTradeAmount = fTradeAmount;
	}

	/**返回 fSettlePrice 的值*/
	public double getFSettlePrice() {
		return FSettlePrice;
	}

	/**传入fSettlePrice 设置  fSettlePrice 的值*/
	public void setFSettlePrice(double fSettlePrice) {
		FSettlePrice = fSettlePrice;
	}

	/**返回 fTradePrice 的值*/
	public double getFTradePrice() {
		return FTradePrice;
	}

	/**传入fTradePrice 设置  fTradePrice 的值*/
	public void setFTradePrice(double fTradePrice) {
		FTradePrice = fTradePrice;
	}

	/**返回 fClearMoney 的值*/
	public double getFClearMoney() {
		return FClearMoney;
	}

	/**传入fClearMoney 设置  fClearMoney 的值*/
	public void setFClearMoney(double fClearMoney) {
		FClearMoney = fClearMoney;
	}

	/**返回 fTotalMoney 的值*/
	public double getFTotalMoney() {
		return FTotalMoney;
	}

	/**传入fTotalMoney 设置  fTotalMoney 的值*/
	public void setFTotalMoney(double fTotalMoney) {
		FTotalMoney = fTotalMoney;
	}

	/**返回 fDesc 的值*/
	public String getFDesc() {
		return FDesc;
	}

	/**传入fDesc 设置  fDesc 的值*/
	public void setFDesc(String fDesc) {
		FDesc = fDesc;
	}

	/**返回 fDate 的值*/
	public Date getFDate() {
		return FDate;
	}

	/**传入fDate 设置  fDate 的值*/
	public void setFDate(Date fDate) {
		FDate = fDate;
	}

	/**返回 fRecordType 的值*/
	public String getFRecordType() {
		return FRecordType;
	}

	/**传入fRecordType 设置  fRecordType 的值*/
	public void setFRecordType(String fRecordType) {
		FRecordType = fRecordType;
	}

	/**返回 fSecurityType 的值*/
	public String getFSecurityType() {
		return FSecurityType;
	}

	/**传入fSecurityType 设置  fSecurityType 的值*/
	public void setFSecurityType(String fSecurityType) {
		FSecurityType = fSecurityType;
	}

	/**返回 fResultCode 的值*/
	public String getFResultCode() {
		return FResultCode;
	}

	/**传入fResultCode 设置  fResultCode 的值*/
	public void setFResultCode(String fResultCode) {
		FResultCode = fResultCode;
	}

	/**返回 fBargainDate 的值*/
	public Date getFBargainDate() {
		return FBargainDate;
	}

	/**传入fBargainDate 设置  fBargainDate 的值*/
	public void setFBargainDate(Date fBargainDate) {
		FBargainDate = fBargainDate;
	}

	/**返回 fSettleAmount 的值*/
	public double getFSettleAmount() {
		return FSettleAmount;
	}

	/**传入fSettleAmount 设置  fSettleAmount 的值*/
	public void setFSettleAmount(double fSettleAmount) {
		FSettleAmount = fSettleAmount;
	}

	/**返回 fTransactionBs 的值*/
	public String getFTransactionBs() {
		return FTransactionBs;
	}

	/**传入fTransactionBs 设置  fTransactionBs 的值*/
	public void setFTransactionBs(String fTransactionBs) {
		FTransactionBs = fTransactionBs;
	}

	/**返回 fReceiptsBs 的值*/
	public String getFReceiptsBs() {
		return FReceiptsBs;
	}

	/**传入fReceiptsBs 设置  fReceiptsBs 的值*/
	public void setFReceiptsBs(String fReceiptsBs) {
		FReceiptsBs = fReceiptsBs;
	}

	/**返回 fReceiptsNum 的值*/
	public String getFReceiptsNum() {
		return FReceiptsNum;
	}

	/**传入fReceiptsNum 设置  fReceiptsNum 的值*/
	public void setFReceiptsNum(String fReceiptsNum) {
		FReceiptsNum = fReceiptsNum;
	}

	/**返回 fApplicationNum 的值*/
	public String getFApplicationNum() {
		return FApplicationNum;
	}

	/**传入fApplicationNum 设置  fApplicationNum 的值*/
	public void setFApplicationNum(String fApplicationNum) {
		FApplicationNum = fApplicationNum;
	}

	/**返回 fConsignNum 的值*/
	public String getFConsignNum() {
		return FConsignNum;
	}

	/**传入fConsignNum 设置  fConsignNum 的值*/
	public void setFConsignNum(String fConsignNum) {
		FConsignNum = fConsignNum;
	}

	/**返回 fOtherDate 的值*/
	public Date getFOtherDate() {
		return FOtherDate;
	}

	/**传入fOtherDate 设置  fOtherDate 的值*/
	public void setFOtherDate(Date fOtherDate) {
		FOtherDate = fOtherDate;
	}

	/**返回 fConsignDate 的值*/
	public String getFConsignDate() {
		return FConsignDate;
	}

	/**传入fConsignDate 设置  fConsignDate 的值*/
	public void setFConsignDate(String fConsignDate) {
		FConsignDate = fConsignDate;
	}

	/**返回 fTradDate 的值*/
	public String getFTradDate() {
		return FTradDate;
	}

	/**传入fTradDate 设置  fTradDate 的值*/
	public void setFTradDate(String fTradDate) {
		FTradDate = fTradDate;
	}

	/**返回 fProfessionUnit 的值*/
	public String getFProfessionUnit() {
		return FProfessionUnit;
	}

	/**传入fProfessionUnit 设置  fProfessionUnit 的值*/
	public void setFProfessionUnit(String fProfessionUnit) {
		FProfessionUnit = fProfessionUnit;
	}

	/**返回 fSettleNum 的值*/
	public String getFSettleNum() {
		return FSettleNum;
	}

	/**传入fSettleNum 设置  fSettleNum 的值*/
	public void setFSettleNum(String fSettleNum) {
		FSettleNum = fSettleNum;
	}

	/**返回 fClearNum 的值*/
	public String getFClearNum() {
		return FClearNum;
	}

	/**传入fClearNum 设置  fClearNum 的值*/
	public void setFClearNum(String fClearNum) {
		FClearNum = fClearNum;
	}

	/**返回 fCurrencyType 的值*/
	public String getFCurrencyType() {
		return FCurrencyType;
	}

	/**传入fCurrencyType 设置  fCurrencyType 的值*/
	public void setFCurrencyType(String fCurrencyType) {
		FCurrencyType = fCurrencyType;
	}

	/**返回 fRightsType 的值*/
	public String getFRightsType() {
		return FRightsType;
	}

	/**传入fRightsType 设置  fRightsType 的值*/
	public void setFRightsType(String fRightsType) {
		FRightsType = fRightsType;
	}

	/**返回 fExtensionYear 的值*/
	public String getFExtensionYear() {
		return FExtensionYear;
	}

	/**传入fExtensionYear 设置  fExtensionYear 的值*/
	public void setFExtensionYear(String fExtensionYear) {
		FExtensionYear = fExtensionYear;
	}

	/**返回 fBargainBs 的值*/
	public String getFBargainBs() {
		return FBargainBs;
	}

	/**传入fBargainBs 设置  fBargainBs 的值*/
	public void setFBargainBs(String fBargainBs) {
		FBargainBs = fBargainBs;
	}

	/**返回 fFundsBar 的值*/
	public String getFFundsBar() {
		return FFundsBar;
	}

	/**传入fFundsBar 设置  fFundsBar 的值*/
	public void setFFundsBar(String fFundsBar) {
		FFundsBar = fFundsBar;
	}

	/**返回 fCurrencyCode 的值*/
	public String getFCurrencyCode() {
		return FCurrencyCode;
	}

	/**传入fCurrencyCode 设置  fCurrencyCode 的值*/
	public void setFCurrencyCode(String fCurrencyCode) {
		FCurrencyCode = fCurrencyCode;
	}

	/**返回 fStampTax 的值*/
	public double getFStampTax() {
		return FStampTax;
	}

	/**传入fStampTax 设置  fStampTax 的值*/
	public void setFStampTax(double fStampTax) {
		FStampTax = fStampTax;
	}

	/**返回 fhandleTax 的值*/
	public double getFhandleTax() {
		return FhandleTax;
	}

	/**传入fhandleTax 设置  fhandleTax 的值*/
	public void setFhandleTax(double fhandleTax) {
		FhandleTax = fhandleTax;
	}

	/**返回 fTransferTax 的值*/
	public double getFTransferTax() {
		return FTransferTax;
	}

	/**传入fTransferTax 设置  fTransferTax 的值*/
	public void setFTransferTax(double fTransferTax) {
		FTransferTax = fTransferTax;
	}

	/**返回 fCanalTax 的值*/
	public double getFCanalTax() {
		return FCanalTax;
	}

	/**传入fCanalTax 设置  fCanalTax 的值*/
	public void setFCanalTax(double fCanalTax) {
		FCanalTax = fCanalTax;
	}

	/**返回 fProcedureTax 的值*/
	public double getFProcedureTax() {
		return FProcedureTax;
	}

	/**传入fProcedureTax 设置  fProcedureTax 的值*/
	public void setFProcedureTax(double fProcedureTax) {
		FProcedureTax = fProcedureTax;
	}

	/**返回 fOtherMoney1 的值*/
	public double getFOtherMoney1() {
		return FOtherMoney1;
	}

	/**传入fOtherMoney1 设置  fOtherMoney1 的值*/
	public void setFOtherMoney1(double fOtherMoney1) {
		FOtherMoney1 = fOtherMoney1;
	}

	/**返回 fOtherMoney2 的值*/
	public double getFOtherMoney2() {
		return FOtherMoney2;
	}

	/**传入fOtherMoney2 设置  fOtherMoney2 的值*/
	public void setFOtherMoney2(double fOtherMoney2) {
		FOtherMoney2 = fOtherMoney2;
	}

	/**返回 fOtherMoney3 的值*/
	public double getFOtherMoney3() {
		return FOtherMoney3;
	}

	/**传入fOtherMoney3 设置  fOtherMoney3 的值*/
	public void setFOtherMoney3(double fOtherMoney3) {
		FOtherMoney3 = fOtherMoney3;
	}

}
