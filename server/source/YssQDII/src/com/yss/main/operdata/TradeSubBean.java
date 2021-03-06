package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.log.SingleLogOper;
import com.yss.main.compliance.CompIndexBean;
import com.yss.main.dao.ICashTransfer;
import com.yss.main.dao.IComplianceDeal;
import com.yss.main.dao.ICostCalculate;
import com.yss.main.dao.IDataSetting;
import com.yss.main.dao.ITradeOperation;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.operdeal.BaseFeeDeal;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.BaseTradeDeal;
import com.yss.main.operdeal.stgstat.StgCash;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.parasetting.FeeBean;
import com.yss.main.parasetting.FixInterestBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.main.settlement.TradeSettleBean;
import com.yss.main.syssetting.RightBean;
import com.yss.manager.SecRecPayAdmin;
import com.yss.manager.TradeDataAdmin;
import com.yss.pojo.cache.YssCost;
import com.yss.pojo.cache.YssFeeType;
import com.yss.pojo.param.comp.YssCompDeal;
import com.yss.pojo.param.comp.YssCompRep;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;

public class TradeSubBean extends BaseDataSettingBean implements IDataSetting {
	private String num = ""; // 交易拆分数据流水号
	private String securityCode = ""; // 交易证券代码
	private String securityName = ""; // 交易证券名称
	private String portCode = ""; // 组合代码
	private String portName = ""; // 组合名称
	private String brokerCode = ""; // 券商代码
	private String brokerName = ""; // 券商名称
	private String invMgrCode = ""; // 投资经理代码
	private String invMgrName = ""; // 投资经理名称
	private String tradeCode = ""; // 交易方式代码
	private String tradeName = ""; // 交易方式名称
	private String cashAcctCode = ""; // 现金帐户代码
	private String cashAcctName = ""; // 现金帐户名称
	private String attrClsCode = ""; // 所属分类代码
	private String attrClsName = ""; // 所属分类名称
	private String bargainDate = "1900-01-01"; // 成交日期
	private String bargainTime = "00:00:00"; // 成交时间
	private String endBargainDate = "9998-12-31"; // 成交日期的截止日期 by leeyu合中保的版本
	private String settleDate = "1900-01-01"; // 结算日期
	private String settleTime = "00:00:00"; // 结算时间
	// -------------------2007.11.16 添加 蒋锦 添加字段-------------------//
	private String MatureDate = "1900-01-01"; // 到期日期
	private String MatureSettleDate = "1900-01-01"; // 到期结算日期
	// -----------------------------------------------------------------//
	private String rateDate = "1900-01-01"; // 交易日期 edit by jc
	private String autoSettle = "0"; // 自动结算
	private double portCuryRate; // 组合汇率
	private double baseCuryRate; // 基础汇率
	private double factBaseRate; // 实际结算基础汇率
	private double factPortRate; // 实际结算组合汇率
	private double handAmount; // 每手股数
	private double allotProportion; // 分配比例
	private double oldAllotAmount; // 原始分配数量
	private double tradeAmount; // 交易数量
	private double tradePrice; // 交易价格
	private double tradeMoney; // 交易总额
	private double accruedInterest; // 应计利息
	private double allotFactor; // 分配因子
	private double totalCost; // 投资总成本
	private String orderNum = ""; // 订单代码
	private String desc = ""; // 交易描述
	//private String isOnlyColumns = ""; // 是否只读取列名的标志
	private String tailPortCode = ""; // 尾差组合代码
	private String tailPortName = ""; // 尾差组合名称
	private String portCuryCode = ""; // 组合货币代码
	private double haveAmount; // 持有股数/现金
	private String compPoint = ""; // 监控点
	private String settleState = ""; // 结算状态,临时
	private double bailMoney; // 保证金金额
	private String subType; // 交易拆分 拆分类型　合并中保版本
	private String fees = "";
	private String feeCode1 = "";
	// private String tradeFee1 = ""; 无用 删除 sunkey@Delete 20090825
	private YssCost cost = new YssCost(); // 成本
	private double factor; // 报价因子
	private String factCashAccCode = ""; // 实际结算帐户
	private String factCashAccName = "";
	private double factSettleMoney = 0; // 实际结算金额
	private double exRate = 1; // 兑换汇率
	private String factSettleDate = "9998-12-31"; // 实际结算日期
	private String settleDesc = ""; // 结算描述
	private String FFeeCode1 = ""; // 为了直接在后台进行汇总计算
	private String FFeeCode2 = "";
	private String FFeeCode3 = "";
	private String FFeeCode4 = "";
	private String FFeeCode5 = "";
	private String FFeeCode6 = "";
	private String FFeeCode7 = "";
	private String FFeeCode8 = "";

	private double FTradeFee1; // 为了直接在后台进行汇总计算
	private double FTradeFee2;
	private double FTradeFee3;
	private double FTradeFee4;
	private double FTradeFee5;
	private double FTradeFee6;
	private double FTradeFee7;
	private double FTradeFee8;

	private int dataSource;
	private String multAuditString = "";

	private String sMulRela = ""; // 保存关联表的内容 add by liyu 1202
	private String tmpSec = ""; // 合中保的版本
	private String sRecycled = "";
	private double FactRate = 0; // =合中保的版本

	// ---------------------2009-06-22 蒋锦 添加 -------------------------//
	// ETF 相关信息 MS00013:《QDV4.1赢时胜（上海）2009年4月20日13_A》国内基金业务
	private String ETFBalaAcctCode = ""; // ETF 结算帐户代码
	//---add by songjie 2012.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B start---//
	private String ETFBalaAcctName = ""; // ETF 结算账户名称
	
	/**shashijie 2012-4-27 STORY 2565 */
	private String FBSDate = "1900-01-01";//申赎日期
	private double FCanReturnMoney = 0;//可退替代款
	/**end*/
	
	public String getETFBalaAcctName() {
		return ETFBalaAcctName;
	}

	public void setETFBalaAcctName(String eTFBalaAcctName) {
		ETFBalaAcctName = eTFBalaAcctName;
	}
	//---add by songjie 2012.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B end---//
	private String ETFBalaSettleDate = "1900-01-01"; // ETF 现金差额结算日期
	private double ETFBalanceMoney = 0; // ETF 现金差额
	private double ETFCashAlternat = 0; // ETF 现金替代
	// -------------------------End MS00013-------------------------------//

	// ------------2009-07-07 蒋锦 添加 股东代码和席位设置-------------------//
	// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
	private String tradeSeatCode = ""; // 席位代码
	private String tradeSeatName = ""; // 席位名称
	private String stockholderCode = ""; // 股东代码
	private String stockholderName = ""; // 股东名称
	// --------------------------End MS00021------------------------------//

	// ----2009-07-15 add by wangzuochun 拆分关联编号和拆分交易数据资产类型----//
	// MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
	private String splitNum = ""; // 拆分关联编号
	private String investType = ""; // 拆分交易数据资产类型
	// ---------------------------End MS00024------------------------------//
	/*
	 * xuqiji 20090824 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理操作类型
	 * 界面上输入'HD_JK' FDataSouce=0，权益处理:'HD_QY' FDataSouce=0接口：读入'ZD_JK'
	 * FDataSource=1 ，权益处理 'ZD_QY' FDataSource=1
	 */
	private String dsType = "";
	// ---------------------------------end-------------------------------//
	// xuqiji 20090824 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理

	// --- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能 ---
	private String bargainDateEnd = "9998-12-31";
	private String matureDateEnd = "9998-12-31";
	private String rateDateEnd = "9998-12-31";

	// MS01354 add by zhangfa 20100719 QDV4赢时胜(上海)2010年06月25日01_A
	private String jkdr = "";// 接口导入:0手工录入,1 接口导入

	private SingleLogOper logOper = null;// 用于批量处理审核、反审核、删除与清除等功能日志

    //----by guyichuan 20110514 STORY #741 增加权益登记日，分红类型两个字段
    private String strRecordDate="1900-01-01";			//权益登记日
    private String strDivdendType="0";			//分红类型
    //---------end------------
	//story 1566 add by  zhouwei 20111017 证券延迟交割标示，0未延迟 ，1延迟
    private String FSecurityDelaySettleState;
    private String fdealNum;//story 1574 add by zhouwei 20111109
    //---add by songjie 2012.03.26 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A start---//
    private String dataBirth = "";//数据来源   
	
    //add by huangqirong 2012-10-25 story #2328
    private String GCSNum = ""; //GCS交易指令编号
    
    public String GetGCSNum(){
    	return this.GCSNum;
    }
    
    public void setGCSNum(String gcsNum){
    	this.GCSNum = gcsNum;
    }
    
    //---end---
    
	public String getDataBirth() {
		return dataBirth;
	}

	public void setDataBirth(String dataBirth) {
		this.dataBirth = dataBirth;
	}
	//---add by songjie 2012.03.26 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A end---//

	//add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A-------------start
    private String categoryCode;//添加品种类型        
    public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	//add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A-------------end
	private String costIsHandEditState="0";//add by zhouwei 20120308 成本是否经过手动修改 0未修改，1手动修改
	//---story 2727 add by zhouwei 20120619 必须现金替代结算日期  ETF链接基金 start---//
	private String mtReplaceDate="1900-01-01";
	
	public String getMtReplaceDate() {
		return mtReplaceDate;
	}

	public void setMtReplaceDate(String mtReplaceDate) {
		this.mtReplaceDate = mtReplaceDate;
	}
	//---story 2727 add by zhouwei 20120619 必须现金替代结算日期  ETF链接基金 end---//

	public String getFdealNum() {
		return fdealNum;
	}

	public void setFdealNum(String fdealNum) {
		this.fdealNum = fdealNum;
	}

	public String getJkdr() {
		return jkdr;
	}

	public String getStrRecordDate() {
		return strRecordDate;
	}

	public void setStrRecordDate(String strRecordDate) {
		this.strRecordDate = strRecordDate;
	}

	public String getStrDivdendType() {
		return strDivdendType;
	}

	public void setStrDivdendType(String strDivdendType) {
		this.strDivdendType = strDivdendType;
	}

	public void setJkdr(String jkdr) {
		this.jkdr = jkdr;
	}

	// -----------------------------------------------------------------------
	public String getBargainDateEnd() {
		return bargainDateEnd;
	}

	public void setBargainDateEnd(String bargainDateEnd) {
		this.bargainDateEnd = bargainDateEnd;
	}

	public String getMatureDateEnd() {
		return matureDateEnd;
	}

	public void setMatureDateEnd(String matureDateEnd) {
		this.matureDateEnd = matureDateEnd;
	}

	public String getRateDateEnd() {
		return rateDateEnd;
	}

	public void setRateDateEnd(String rateDateEnd) {
		this.rateDateEnd = rateDateEnd;
	}

	// --- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能 ---

	public void setDsType(String dsType) {
		this.dsType = dsType;
	}

	public String getDsType() {
		return dsType;
	}

	// --------------------------------end-------------------------------//

	private TradeSubBean filterType;

	// -----V4.1_ETF:MS00002 add by songjie 2009-11-01 ----//
	private int amountInd = 0;// 数量方向
	private double tradeFee = 0;// 交易费用

	public int getAmountInd() {
		return amountInd;
	}

	public void setAmountInd(int amountInd) {
		this.amountInd = amountInd;
	}

	public double getTradeFee() {
		return tradeFee;
	}

	public void setTradeFee(double tradeFee) {
		this.tradeFee = tradeFee;
	}

	// -----V4.1_ETF:MS00002 add by songjie 2009-11-01 ----//
	// ==add by
	// xuxuming,20090918.MS00700.债券应收和转货基本数据要相应，不用重新输入,QDV4中保2009年09月15日02_B=====
	public void setFilterType(TradeSubBean filterType) {
		this.filterType = filterType;
	}

	// ===end=================================================================================================
	public String getTradeCode() {
		return tradeCode;
	}

	public String getPortCode() {
		return portCode;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public String getCashAcctCode() {
		return cashAcctCode;
	}

	public String getBrokerCode() {
		return brokerCode;
	}

	public double getPortCuryRate() {
		return portCuryRate;
	}

	public String getBargainDate() {
		return bargainDate;
	}

	public double getTradeMoney() {
		return tradeMoney;
	}

	public String getNum() {
		return num;
	}

	public String getSettleTime() {
		return settleTime;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public double getAllotFactor() {
		return allotFactor;
	}

	public double getTradePrice() {
		return tradePrice;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public String getInvMgrCode() {
		return invMgrCode;
	}

	public String getAutoSettle() {
		return autoSettle;
	}

	public String getBargainTime() {
		return bargainTime;
	}

	public String getDesc() {
		return desc;
	}

	public double getTradeAmount() {
		return tradeAmount;
	}

	public double getAccruedInterest() {
		return accruedInterest;
	}

	public String getFees() {
		return fees;
	}

	public double getBaseCuryRate() {
		return baseCuryRate;
	}

	public String getSettleDate() {
		return settleDate;
	}

	/**
	 * 获取到期日期 添加日期：2007-11-16 蒋锦
	 * 
	 * @return String
	 */
	public String getMatureDate() {
		return this.MatureDate;
	}

	/**
	 * 获取到期结算日期 添加日期：2007-11-16 蒋锦
	 * 
	 * @return String
	 */
	public String getMatureSettleDate() {
		return this.MatureSettleDate;
	}

	public void setAttrClsCode(String attrClsCode) {
		this.attrClsCode = attrClsCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public void setCashAcctCode(String cashAcctCode) {
		this.cashAcctCode = cashAcctCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public void setPortCuryRate(double portCuryRate) {
		this.portCuryRate = portCuryRate;
	}

	public void setBargainDate(String bargainDate) {
		this.bargainDate = bargainDate;
	}

	public void setTradeMoney(double tradeMoney) {
		this.tradeMoney = tradeMoney;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public void setSettleTime(String settleTime) {
		this.settleTime = settleTime;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public void setAllotFactor(double allotFactor) {
		this.allotFactor = allotFactor;
	}

	public void setTradePrice(double tradePrice) {
		this.tradePrice = tradePrice;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public void setInvMgrCode(String invMgrCode) {
		this.invMgrCode = invMgrCode;
	}

	public void setAutoSettle(String autoSettle) {
		this.autoSettle = autoSettle;
	}

	public void setBargainTime(String bargainTime) {
		this.bargainTime = bargainTime;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setTradeAmount(double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public void setAccruedInterest(double accruedInterest) {
		this.accruedInterest = accruedInterest;
	}

	public void setFees(String fees) {
		this.fees = fees;
	}

	public void setBaseCuryRate(double baseCuryRate) {
		this.baseCuryRate = baseCuryRate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	/**
	 * 设置到期日期 添加日期：2007-11-16 蒋锦
	 * 
	 * @param MatureDate
	 *            String
	 */
	public void setMatureDate(String MatureDate) {
		this.MatureDate = MatureDate;
	}

	/**
	 * 设置到期结算日期 添加日期：2007-11-16 蒋锦
	 * 
	 * @param MatureSettleDate
	 *            String
	 */
	public void setMatureSettleDate(String MatureSettleDate) {
		this.MatureSettleDate = MatureSettleDate;
	}

	public void setOldAllotAmount(double oldAllotAmount) {
		this.oldAllotAmount = oldAllotAmount;
	}

	public void setAllotProportion(double allotProportion) {
		this.allotProportion = allotProportion;
	}

	public void setHandAmount(double handAmount) {
		this.handAmount = handAmount;
	}

//	public void setisOnlyColumns(String isOnlyColumns) {
//		this.isOnlyColumns = isOnlyColumns;
//	}

	public void setTailPortCode(String tailPortCode) {
		this.tailPortCode = tailPortCode;
	}

	public void setHaveAmount(double haveAmount) {
		this.haveAmount = haveAmount;
	}

	public void setCost(YssCost cost) {
		this.cost = cost;
	}

	public void setFactor(double factor) {
		this.factor = factor;
	}

	public void setCompPoint(String compPoint) {
		this.compPoint = compPoint;
	}

	public void setBailMoney(double bailMoney) {
		this.bailMoney = bailMoney;
	}

	public void setFTradeFee7(double FTradeFee7) {
		this.FTradeFee7 = FTradeFee7;
	}

	public void setFFeeCode5(String FFeeCode5) {
		this.FFeeCode5 = FFeeCode5;
	}

	public void setFFeeCode6(String FFeeCode6) {
		this.FFeeCode6 = FFeeCode6;
	}

	public void setFTradeFee4(double FTradeFee4) {
		this.FTradeFee4 = FTradeFee4;
	}

	public void setFTradeFee6(double FTradeFee6) {
		this.FTradeFee6 = FTradeFee6;
	}

	public void setFFeeCode3(String FFeeCode3) {
		this.FFeeCode3 = FFeeCode3;
	}

	public void setFFeeCode2(String FFeeCode2) {
		this.FFeeCode2 = FFeeCode2;
	}

	public void setFTradeFee8(double FTradeFee8) {
		this.FTradeFee8 = FTradeFee8;
	}

	public void setFFeeCode1(String FFeeCode1) {
		this.FFeeCode1 = FFeeCode1;
	}

	public void setFTradeFee5(double FTradeFee5) {
		this.FTradeFee5 = FTradeFee5;
	}

	public void setFFeeCode4(String FFeeCode4) {
		this.FFeeCode4 = FFeeCode4;
	}

	public void setFFeeCode7(String FFeeCode7) {
		this.FFeeCode7 = FFeeCode7;
	}

	public void setFTradeFee2(double FTradeFee2) {
		this.FTradeFee2 = FTradeFee2;
	}

	public void setFeeCode1(String feeCode1) {
		this.feeCode1 = feeCode1;
	}

	public void setFTradeFee3(double FTradeFee3) {
		this.FTradeFee3 = FTradeFee3;
	}

	public void setFTradeFee1(double FTradeFee1) {
		this.FTradeFee1 = FTradeFee1;
	}

	public void setPortCuryCode(String portCuryCode) {
		this.portCuryCode = portCuryCode;
	}

	public void setFactSettleMoney(double factSettleMoney) {
		this.factSettleMoney = factSettleMoney;
	}

	public void setFactCashAccName(String factCashAccName) {
		this.factCashAccName = factCashAccName;
	}

	public void setFactCashAccCode(String factCashAccCode) {
		this.factCashAccCode = factCashAccCode;
	}

	public void setExRate(double exRate) {
		this.exRate = exRate;
	}

	public void setSettleDesc(String settleDesc) {
		this.settleDesc = settleDesc;
	}

	public void setFactSettleDate(String factSettleDate) {
		this.factSettleDate = factSettleDate;
	}

	public void setFactPortRate(double factPortRate) {
		this.factPortRate = factPortRate;
	}

	public void setFactBaseRate(double factBaseRate) {
		this.factBaseRate = factBaseRate;
	}

	public void setSettleState(String settleState) {
		this.settleState = settleState;
	}

	public void setDataSource(int dataSource) {
		this.dataSource = dataSource;
	}

	// ====================合中保的版本
	public void setFactRate(double FactRate) {
		this.FactRate = FactRate;
	}

	// ======================

	public void setRateDate(String rateDate) {
		this.rateDate = rateDate;
	}

	public void setTradeSeatCode(String tradeSeatCode) {
		this.tradeSeatCode = tradeSeatCode;
	}

	public void setTradeSeatName(String tradeSeatName) {
		this.tradeSeatName = tradeSeatName;
	}

	public void setStockholderCode(String stockholderCode) {
		this.stockholderCode = stockholderCode;
	}

	public void setStockholderName(String stockholderName) {
		this.stockholderName = stockholderName;
	}

	// ----2009-07-15 add by wangzuochun ----//
	// MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
	public void setInvestType(String investType) {
		this.investType = investType;
	}

	public void setSplitNum(String splitNum) {
		this.splitNum = splitNum;
	}

	// ----------------------------------------------------//
	public void setETFCashAlternat(double ETFCashAlternat) {
		this.ETFCashAlternat = ETFCashAlternat;
	}

	public void setETFBalaSettleDate(String ETFBalaSettleDate) {
		this.ETFBalaSettleDate = ETFBalaSettleDate;
	}

	public void setETFBalanceMoney(double ETFBalanceMoney) {
		this.ETFBalanceMoney = ETFBalanceMoney;
	}

	public void setETFBalaAcctCode(String ETFBalaAcctCode) {
		this.ETFBalaAcctCode = ETFBalaAcctCode;
	}

	public void setFFeeCode8(String FFeeCode8) {
		this.FFeeCode8 = FFeeCode8;
	}

	public String getAttrClsCode() {
		return attrClsCode;
	}

	public double getOldAllotAmount() {
		return oldAllotAmount;
	}

	public double getAllotProportion() {
		return allotProportion;
	}

	public double getHandAmount() {
		return handAmount;
	}

//	public String getisOnlyColumns() {
//		return isOnlyColumns;
//	}

	public String getTailPortCode() {
		return tailPortCode;
	}

	public double getHaveAmount() {
		return haveAmount;
	}

	public YssCost getCost() {
		return cost;
	}

	public double getFactor() {
		return factor;
	}

	public String getCompPoint() {
		return compPoint;
	}

	public double getBailMoney() {
		return bailMoney;
	}

	public double getFTradeFee7() {
		return FTradeFee7;
	}

	public String getFFeeCode5() {
		return FFeeCode5;
	}

	public String getFFeeCode6() {
		return FFeeCode6;
	}

	public double getFTradeFee4() {
		return FTradeFee4;
	}

	public double getFTradeFee6() {
		return FTradeFee6;
	}

	public String getFFeeCode3() {
		return FFeeCode3;
	}

	public String getFFeeCode2() {
		return FFeeCode2;
	}

	public double getFTradeFee8() {
		return FTradeFee8;
	}

	public String getFFeeCode1() {
		return FFeeCode1;
	}

	public double getFTradeFee5() {
		return FTradeFee5;
	}

	public String getFFeeCode4() {
		return FFeeCode4;
	}

	public String getFFeeCode7() {
		return FFeeCode7;
	}

	public double getFTradeFee2() {
		return FTradeFee2;
	}

	public String getFeeCode1() {
		return feeCode1;
	}

	public double getFTradeFee3() {
		return FTradeFee3;
	}

	public double getFTradeFee1() {
		return FTradeFee1;
	}

	public String getPortCuryCode() {
		return portCuryCode;
	}

	public double getFactSettleMoney() {
		return factSettleMoney;
	}

	public String getFactCashAccName() {
		return factCashAccName;
	}

	public String getFactCashAccCode() {
		return factCashAccCode;
	}

	public double getExRate() {
		return exRate;
	}

	public String getSettleDesc() {
		return settleDesc;
	}

	public String getFactSettleDate() {
		return factSettleDate;
	}

	public double getFactPortRate() {
		return factPortRate;
	}

	public double getFactBaseRate() {
		return factBaseRate;
	}

	public String getSettleState() {
		return settleState;
	}

	public int getDataSource() {
		return dataSource;
	}

	public double getFactRate() {
		return FactRate;
	}

	public String getRateDate() {
		return rateDate;
	}

	public String getTradeSeatCode() {
		return tradeSeatCode;
	}

	public String getTradeSeatName() {
		return tradeSeatName;
	}

	public String getStockholderCode() {
		return stockholderCode;
	}

	public String getStockholderName() {
		return stockholderName;
	}

	// ----2009-07-15 add by wangzuochun ----//
	// MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
	public String getInvestType() {
		return investType;
	}

	public String getSplitNum() {
		return splitNum;
	}

	// -----------------------------------------//
	public double getETFCashAlternat() {
		return ETFCashAlternat;
	}

	public String getETFBalaSettleDate() {
		return ETFBalaSettleDate;
	}

	public double getETFBalanceMoney() {
		return ETFBalanceMoney;
	}

	public String getETFBalaAcctCode() {
		return ETFBalaAcctCode;
	}

	public String getFFeeCode8() {
		return FFeeCode8;
	}

	public TradeSubBean() {
	}

	/**
	 * 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() throws YssException {
		String sResult = "";
		if(pub.isBrown()==true) //add by guolongchao 20111010 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
		
		if (this.filterType != null) {			
			sResult = " where 1=1";
			if (this.filterType.isOnlyColumns.equals("1")&&pub.isBrown()==false) {//update by guolongchao 20110909 STORY 1285 
				sResult = sResult + " and 1=2 ";
				return sResult;
			}
			if (this.filterType.num.length() != 0) {
				sResult = sResult + " and a.FNum like '"
						+ filterType.num.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.portCode.length() != 0) {
				// -----------2009.05.20 蒋锦 添加 MS00002 QDV4赢时胜（上海）2009年4月20日02_A
				// 操作组合-----------//
				// 可选择多组合进行查询
				if (this.filterType.portCode.indexOf(",") != -1) {
					sResult = sResult + " and a.FPortCode IN ("
							+ operSql.sqlCodes(this.filterType.portCode) + ")";
				} else {
					sResult = sResult + " and a.FPortCode like '"
							+ filterType.portCode.replaceAll("'", "''") + "%'";
				}
			}
			if (this.filterType.tradeCode.length() != 0) {
				sResult = sResult + " and a.FTradeTypeCode = '"
						+ filterType.tradeCode.replaceAll("'", "''") + "'";
			}
			// ==================合中保的版本
			if (filterType.endBargainDate.length() > 0
					&& !filterType.endBargainDate.equals("9998-12-31")
					&& this.filterType.bargainDate.length() != 0
					&& !this.filterType.bargainDate.equals("9998-12-31")) {
				sResult = sResult + " and (a.FBargainDate between "
						+ dbl.sqlDate(filterType.bargainDate) + " and "
						+ dbl.sqlDate(filterType.endBargainDate) + ")";
				// ========================
			} else if (this.filterType.bargainDate.length() != 0
					&& !this.filterType.bargainDate.equals("9998-12-31")) {
				// --- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能 add by
				// jiangshichao---
				sResult = sResult + " and a.FBargainDate between "
						+ dbl.sqlDate(filterType.bargainDate) + " and "
						+ dbl.sqlDate(this.filterType.bargainDateEnd);
				// --- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能 end
				// ------------------
			}
			if (this.filterType.securityCode.length() != 0) {
				sResult = sResult + " and a.FSecurityCode like '"
						+ filterType.securityCode.replaceAll("'", "''") + "'"
				/* + "%'" */; // 2008.07.16 蒋锦 修改 暂时不使用通配符， 10G 数据库会报错
			}
			/*
			 * //Undo by xuxuming,20090921. 这个条件是错误的，所以去掉。 if
			 * (this.filterType.brokerCode.length() == 1) { sResult = sResult +
			 * " and FBrokerCode = " + filterType.brokerCode; }
			 */
			// =========add by xuxuming,20090921.MS00700=============
			if (this.filterType.brokerCode != null
					&& !"".equals(this.filterType.brokerCode.trim())) {
				sResult = sResult + " and FBrokerCode = "
						+ dbl.sqlString(filterType.brokerCode);
			}
			// =============================================

			if (this.filterType.invMgrCode != null
					&& !"".equals(this.filterType.invMgrCode.trim())) { // edit
				// by
				// xuxuming,20090929,MS00700
				sResult = sResult + " and a.FInvMgrCode like '"
						+ filterType.invMgrCode.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.cashAcctCode.length() != 0) {
				sResult = sResult + " and a.FCashAccCode like '"
						+ filterType.cashAcctCode.replaceAll("'", "''") + "%'";
			}
			// 2008.10.26 蒋锦 添加 trim()
			if (this.filterType.attrClsCode.trim().length() != 0) {
				sResult = sResult + " and a.FAttrClsCode like '"
						+ filterType.attrClsCode.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.settleDate.length() != 0
					&& !this.filterType.settleDate.equals("9998-12-31")
					&& !this.filterType.settleDate.equals("1900-01-01")) { // edit
				// by
				// xuxuming,20090921.MS00700.因为这个变量初始化为"1900-01-01",故要排除这个值
				sResult = sResult + " and a.FSettleDate = "
						+ dbl.sqlDate(filterType.settleDate);
			}
			// -----------------2007.11.16 添加 蒋锦 添加字段-------------------//
			if (this.filterType.MatureDate.length() != 0
					&& !this.filterType.MatureDate.equals("9998-12-31")
					&& !this.filterType.MatureDate.equals("1900-01-01")) { // edit
				// by
				// xuxuming,20090921.MS00700
				// 因为这个变量初始化为"1900-01-01",故要排除这个值
				// --- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能 add by
				// jiangshichao---
				sResult = sResult + " and a.FMatureDate between "
						+ dbl.sqlDate(filterType.MatureDate) + " and "
						+ dbl.sqlDate(this.filterType.matureDateEnd);
				// --- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能 end
				// ------------------
			}
			if (this.filterType.MatureSettleDate.length() != 0
					&& !this.filterType.MatureSettleDate.equals("9998-12-31")
					&& !this.filterType.MatureSettleDate.equals("1900-01-01")) { // edit
				// by
				// xuxuming,20090921.MS00700
				// 因为这个变量初始化为"1900-01-01",故要排除这个值
				sResult = sResult + " and a.FMatureSettleDate = "
						+ dbl.sqlDate(filterType.MatureSettleDate);
			}
			// ---------------------------------------------------------------//
			if (this.filterType.autoSettle.length() == 1&&pub.isBrown()==false) {//update by guolongchao 20110909 STORY 1285 
				sResult = sResult + " and a.FAutoSettle ="
						+ filterType.autoSettle;
			}
			// BugNo:0000480 edit by jc
			if (this.filterType.rateDate.length() != 0
					&& !this.filterType.rateDate.equals("9998-12-31")
					&& !this.filterType.rateDate.equals("1900-01-01")) { // edit
				// by
				// xuxuming,20090929.MS00700
				// 因为这个变量初始化为"1900-01-01",故要排除这个值
				// --- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能 add by
				// jiangshichao---
				sResult = sResult + " and a.FRateDate between "
						+ dbl.sqlDate(filterType.rateDate) + " and "
						+ dbl.sqlDate(this.filterType.rateDateEnd);
				// --- MS01113 QDV4华夏2010年04月21日01_A 业务资料界面增加可按区间段日期查询功能 end
				// ------------------
			}
			// ----------------------jc
			// --------2009-06-22 蒋锦 添加----------//
			// MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》 国内基金业务 2009.06.16 蒋锦 添加
			// ETF 相关信息
			if (this.filterType.ETFBalaAcctCode.length() != 0) {
				sResult = sResult + " AND a.FETFBalaAcctCode = "
						+ dbl.sqlString(this.filterType.ETFBalaAcctCode);
			}
			if (this.filterType.ETFBalaSettleDate.length() != 0
					&& !this.filterType.ETFBalaSettleDate.equals("9998-12-31")
					&& !this.filterType.rateDate.equals("1900-01-01")) {
				sResult = sResult + " AND FETFBalaSettleDate = "
						+ dbl.sqlDate(this.filterType.ETFBalaSettleDate);
			}
			// --------------------------------------//			
			
			//品种类型 add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A------start
			if (this.filterType.getCategoryCode() != null && this.filterType.getCategoryCode().length() != 0 && this.filterType.securityCode.length() == 0) 
			{
//				SecurityBean SecurityBean=new SecurityBean();
//				SecurityBean.setYssPub(this.pub);
//				SecurityBean.setStrCategoryCode(this.filterType.getCategoryCode());	
//				String SecurityCodes=SecurityBean.getOperValue("getSecurityCodes");
//				if(SecurityCodes!=null&&SecurityCodes.trim().length()>0)
//				      sResult = sResult + " and a.FSecurityCode in ("+SecurityCodes+")";
				
				sResult = sResult + " and a.FSecurityCode in ( select distinct FSecurityCode from  " +pub.yssGetTableName("Tb_Para_Security");
				sResult = sResult + " where FCatCode=" + dbl.sqlString(this.filterType.getCategoryCode())+" )";
			}
			//品种类型 add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A-------end			
		}
		return sResult;

	}

	/**
	 * getListViewData1
	 * 
	 * @return String
	 */
	public String getListViewData1() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		// ----2009-08-14 add by wangzuochun MS00024 交易数据拆分
		// QDV4.1赢时胜（上海）2009年4月20日24_A -----//
		String sVocStr = ""; // 词汇类型对照字符串
		// -----------------------------------------------------------------//
		String strSql = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
//		ITradeOperation tradeOper = (ITradeOperation) pub.getOperDealCtx()
//				.getBean("tradedeal");
		ITradeOperation tradeOper =new BaseTradeDeal();//bug 3183 guolongchao 20111207 将原来由Spring加载改为new,加快打开速度
		ResultSet rs = null;
		try {
			sHeader = this.getListView1Headers();
			tradeOper.setYssPub(pub);
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
			RightBean right = new RightBean();
			right.setYssPub(pub);
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
			// fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A 20100708
			// 优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
			//if (this.filterType.isOnlyColumns.equals("1")&&pub.isBrown()==false) {//update by guolongchao 20110909 STORY 1285 		
			if (this.filterType.isOnlyColumns.equals("1") &&!(pub.isBrown())) { 
				// ----2009-08-14 add by wangzuochun MS00024 交易数据拆分
				// QDV4.1赢时胜（上海）2009年4月20日24_A -----//
				VocabularyBean vocabulary = new VocabularyBean();
				vocabulary.setYssPub(pub);
				sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType);
				// -----------------------------------------------------------------------------//

				return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
						+ "\r\f" + this.getListView1ShowCols() + "\r\f"
						+ yssPageInationBean.buildRowStr() + "\r\f" + tmpSec
						+ "\r\fvoc" + sVocStr; // 合中保的版本; 添加投资类型的词汇， modify by
				// wangzuochun 2009.08.14
				// MS00024 交易数据拆分
				// QDV4.1赢时胜（上海）2009年4月20日24_A
				// //QDV4赢时胜上海2010年03月15日06_B
				// MS00884 by xuqiji
			}
			// --------------------------------------end
			// MS01310--------------------------------------------------------
			tradeOper.initTradeOperation(this.securityCode, this.brokerCode,
					this.invMgrCode, this.tradeCode, this.orderNum, YssFun
							.toDate(this.bargainDate), this.subType);
			
			
			
			strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
					+ " d.FPortName, e.FSecurityName, e.FHandAmount, f.FInvMgrName, g.FTradeTypeName, "
					+ " h.FBrokerName as FBrokerName, o.FCashAccName, p.FAttrClsName, e.FFactor,oo.FCashAccName as FFactCashAccName,e.FFactRate, ts.fseatname, sh.fstockholdername ,"
					//edit by songjie 2012.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 etf.fcashaccname
					+ " a1.FfeeName1,a2.FfeeName2, a3.FfeeName3,  a4.FfeeName4, a5.FfeeName5,a6.FfeeName6,a7.FfeeName7,a8.FfeeName8, etf.fcashaccname as FETFBalaAcctName "
					+ // 合中保的版本
					" from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " a"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"					
					+
					// ----------------------------------------------------------------------------------------------------
					// edit by yanghaiming 20100913 组合设置已去掉启用日期 MS01700
					// QDV411建行2010年09月07日01_A
					// " left join (select db.* from (select FPortCode, max(FStartDate) as FStartDate from "
					// +
					// pub.yssGetTableName("Tb_Para_Portfolio") +
					// " where FStartDate <= " + dbl.sqlDate(new
					// java.util.Date()) +
					// " and FCheckState = 1 group by FPortCode) da join (select FPortCode, FPortName, FStartDate from "
					// +
					// pub.yssGetTableName("Tb_Para_Portfolio") +
					// ") db on da.FPortCode = db.FPortCode and da.FStartDate = db.FStartDate) d on a.FPortCode = d.FPortCode "
					// +
					// ----edit by wuweiqi 20101209 QDV4华夏2010年10月27日04_A
					// 添加各种费用-----------------------//
					" left join (select FfeeName as FfeeName1,ffeecode from "
					+ pub.yssGetTableName("tb_para_fee")
					+ " ) a1 on a.ffeecode1 = a1.ffeecode "
					+ " left join (select FfeeName as FfeeName2,ffeecode from "
					+ pub.yssGetTableName("tb_para_fee")
					+ " ) a2 on a.ffeecode2 = a2.ffeecode "
					+ " left join (select FfeeName as FfeeName3,ffeecode from "
					+ pub.yssGetTableName("tb_para_fee")
					+ " ) a3 on a.ffeecode3 = a3.ffeecode "
					+ " left join (select FfeeName as FfeeName4,ffeecode from "
					+ pub.yssGetTableName("tb_para_fee")
					+ " ) a4 on a.ffeecode4 = a4.ffeecode "
					+ " left join (select FfeeName as FfeeName5,ffeecode from "
					+ pub.yssGetTableName("tb_para_fee")
					+ " ) a5 on a.ffeecode5 = a5.ffeecode "
					+ " left join (select FfeeName as FfeeName6,ffeecode from "
					+ pub.yssGetTableName("tb_para_fee")
					+ " ) a6 on a.ffeecode6 = a6.ffeecode "
					+ " left join (select FfeeName as FfeeName7,ffeecode from "
					+ pub.yssGetTableName("tb_para_fee")
					+ " ) a7 on a.ffeecode7 = a7.ffeecode "
					+ " left join (select FfeeName as FfeeName8,ffeecode from "
					+ pub.yssGetTableName("tb_para_fee")
					+ " ) a8 on a.ffeecode8 = a8.ffeecode "
					+
					// ----------------------------end by
					// wuweiqi-----------------------------------------//
					// modify by zhangfa 20101014 MS01850 启用日期引起的多比数据
					// QDV4上海(33测试)2010年10月11日01_B
					" left join (select FPortCode, FPortName , FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FCheckState = 1 ) d on a.FPortCode = d.FPortCode "
					+
					// -------------MS01850-------------------------------------------------------------------------
					// ----------------------------------------------------------------------------------------------------
					" left join (select eb.*,case when ec.FFactRate is null then 0 else ec.FFactRate end as FFactRate from (select FSecurityCode, max(FStartDate) as FStartDate from "
					+ // 合中保的版本
					pub.yssGetTableName("Tb_Para_Security")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount, FFactor from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate "
					+ " left join (select FSecurityCode,FFactRate from "
					+ pub.yssGetTableName("tb_para_fixinterest")
					+ " )ec on ea.FSecurityCode=ec.FSecurityCode "
					+ ") e on a.FSecurityCode = e.FSecurityCode "
					+ // 合中保的版本
					// ----------------------------------------------------------------------------------------------------
					// edit by yanghaiming 20100913 投资经理设置已去掉启用日期 MS01700
					// QDV411建行2010年09月07日01_A
					// " left join (select fb.* from (select FInvMgrCode, max(FStartDate) as FStartDate from "
					// +
					// pub.yssGetTableName("Tb_Para_InvestManager") +
					// " where FStartDate <= " + dbl.sqlDate(new
					// java.util.Date()) +
					// " and FCheckState = 1 group by FInvMgrCode) fa join (select FInvMgrCode, FInvMgrName, FStartDate from "
					// +
					// pub.yssGetTableName("Tb_Para_InvestManager") +
					// ") fb on fa.FInvMgrCode = fb.FInvMgrCode and fa.FStartDate = fb.FStartDate) f on a.FInvMgrCode = f.FInvMgrCode "
					// +
					" left join (select FInvMgrCode, FInvMgrName from "
					+ pub.yssGetTableName("Tb_Para_InvestManager")
					+ " where FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode "
					+
					// ----------------------------------------------------------------------------------------------------
					" left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode"
					+
					// ----------------------------------------------------------------------------------------------------
					// edit by yanghaiming 20100913 券商设置已去掉启用日期 MS01700
					// QDV411建行2010年09月07日01_A
					// " left join (select hb.* from (select FBrokerCode, max(FStartDate) as FStartDate from "
					// +
					// pub.yssGetTableName("Tb_Para_Broker") +
					// " where FStartDate <= " +
					// dbl.sqlDate(new java.util.Date()) +
					// " and FCheckState = 1 group by FBrokerCode) ha join (select FBrokerCode, FBrokerName, FStartDate from "
					// +
					// pub.yssGetTableName("Tb_Para_Broker") +
					// ") hb on ha.FBrokerCode = hb.FBrokerCode and ha.FStartDate = hb.FStartDate) h on a.FBrokerCode = h.FBrokerCode "
					// +
					" left join (select FBrokerCode, FBrokerName from "
					+ pub.yssGetTableName("Tb_Para_Broker")
					+ " where FCheckState = 1) h on a.FBrokerCode = h.FBrokerCode "
					+
					// ----------------------------------------------------------------------------------------------------
					// edit by yanghaiming 20100913 现金账户设置已去掉启用日期，此SQL需要优化
					// MS01700 QDV411建行2010年09月07日01_A
					// " left join (select ob.* from (select FCashAccCode, max(FStartDate) as FStartDate from "
					// +
					// pub.yssGetTableName("Tb_Para_CashAccount") +
					// " where FStartDate <= " + dbl.sqlDate(new
					// java.util.Date()) +
					// " and FCheckState = 1 group by FCashAccCode) oa join (select FCashAccCode, FCashAccName, FStartDate from "
					// +
					// pub.yssGetTableName("Tb_Para_CashAccount") +
					// ") ob on oa.FCashAccCode = ob.FCashAccCode and oa.FStartDate = ob.FStartDate) o on a.FCashAccCode = o.FCashAccCode "
					// +
					// ----------------------------------------------添加实际结算帐户
					// add liyu---------------------------------
					// " left join (select oc.* from (select FCashAccCode, max(FStartDate) as FStartDate from "
					// +
					// pub.yssGetTableName("Tb_Para_CashAccount") +
					// " where FStartDate <= " + dbl.sqlDate(new
					// java.util.Date()) +
					// " and FCheckState = 1 group by FCashAccCode) od join (select FCashAccCode, FCashAccName, FStartDate from "
					// +
					// pub.yssGetTableName("Tb_Para_CashAccount") +
					// ") oc on od.FCashAccCode = oc.FCashAccCode and od.FStartDate = oc.FStartDate) oo on a.FFactCashAccCode = oo.FCashAccCode "
					// +
					" left join (select FCashAccCode, FCashAccName from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where FCheckState = 1) o on a.FCashAccCode = o.FCashAccCode "
					+ " left join (select FCashAccCode, FCashAccName from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where FCheckState = 1) oo on a.FFactCashAccCode = oo.FCashAccCode "
					+
					// ----------------------------------------------------------------------------------------------------
					" left join (select FAttrClsCode,FAttrClsName from "
					+ pub.yssGetTableName("Tb_Para_AttributeClass")
					+ " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode "
					+
					// ---------2009-07-07 蒋锦 添加 股东代码和席位设置 MS00021 国内股票业务
					// QDV4.1赢时胜（上海）2009年4月20日21_A --//
					" LEFT JOIN (SELECT FSeatCode, FSeatName"
					+ " FROM "
					+ pub.yssGetTableName("Tb_Para_Tradeseat")
					+ " WHERE FCheckState = 1) ts ON a.fseatCode = ts.fseatcode"
					+ " LEFT JOIN (SELECT FStockholderCode, FStockholderName"
					+ " FROM "
					+ pub.yssGetTableName("Tb_Para_Stockholder")
					+ "  WHERE FCheckState = 1) sh ON a.fstockholdercode = sh.fstockholdercode"	
					//---add by songjie 2012.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B start---//
					+ " left join (select FCashAccCode, FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") 
					+ " where FCheckState = 1) etf on a.Fetfbalaacctcode = etf.Fcashacccode "
					//---add by songjie 2012.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B end---//
					// -----------------------------------------------------------------------------------------------------------//
					// buildFilterSql() +
					// " and FCheckState <> 2 order by a.FPortCode";
					// //2007.10.31 修改添加 and FCheckState <> 2 蒋锦
					// edit by yanghaiming ,将根据组合排序更改为根据创建时间排序
				//--modify by 黄啟荣 2011-06-01 story  #937 --用于系统查询出来的结果必须与用户的浏览权限一致。
				+buildFilterSql()
				//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
//					+" and a.FPortcode in (select distinct(tsu.fportcode) as fportcode from(select fportcode from Tb_Sys_Userright"
//  		        		+" where fusercode ="+dbl.sqlString(pub.getUserCode())					    
//  					    +" and frighttype = 'port'"
//  					    +" and FOPERTYPES like '%brow%'"
//  					    +" and frightcode = 'OperationData') tsu"
//  					    +" inner join "+pub.yssGetTableName("tb_Para_Portfolio")
//  					    +" tpp on tpp.fportcode=tsu.fportcode"
//  					    +" where tpp.fenabled=1"
//  					    +" and tpp.FCheckState=1)";
				//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
				//add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B
				+ " and a.FPortcode in (" + operSql.sqlCodes(right.getUserPortCodes("OperationData")) + ")";
				strSql+=" order by a.fcreatetime desc"; // 2008.03.17
				//---end---
			// 去掉FCheckState
			// 因为此处回收站需加载数据
			// by ly
			// QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("SubTrade");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
			while (rs.next()) {
				bufShow.append(rs.getString("FNum")).append("\t");
				// --- MS01639 QDV4太平2010年08月23日02_A add by jiangshichao ---//
				setResultSetAttr(rs);
				bufShow.append(
						YssFun.formatDate(rs.getDate("fbargaindate"),
								"yyyy-MM-dd")).append("\t");
				// --- MS01639 QDV4太平2010年08月23日02_A add by end ------------//
				bufShow.append(rs.getString("FPortCode")).append("\t");
				bufShow.append(rs.getString("FPortName")).append("\t");
				bufShow.append(rs.getString("FSecurityCode")).append("\t");
				bufShow.append(rs.getString("FSecurityName")).append("\t");

				bufShow.append(rs.getString("FTradeTypeCode")).append("\t"); // 添加交易类型的显示
				// QDV4南方2009年04月01日02_AB
				// MS00362
				// by
				// leeyu
				// 20090414
				bufShow.append(rs.getString("FTradeTypeName")).append("\t"); // 添加交易类型的显示
				// QDV4南方2009年04月01日02_AB
				// MS00362
				// by
				// leeyu
				// 20090414

				bufShow.append(rs.getString("FBrokerName")).append("\t");
				bufShow.append(rs.getString("FInvMgrName")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTotalCost"),
								"#,##0.##")).append("\t");
				this.haveAmount = tradeOper.getHave(rs.getString("FPortCode"));
				bufShow
						.append(
								YssFun
										.formatNumber(this.haveAmount,
												"#,##0.##")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FAllotProportion"),
								"##.#%")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FOldAllotAmount"),
								"#,##0.##")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTradeAmount"),
								"#,##0.####")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTradePrice"),
								"#,##0.####")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTradeMoney"),
								"#,##0.##")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FAccruedInterest"),
								"#,##0.##")).append("\t");
				bufShow.append(rs.getString("FCashAccCode")).append("\t");
				// -------------------lzp modify 2007 12.14 用来显示创建人修改人及时间
				// bufShow.append(rs.getString("FCashAccName")).append(YssCons.YSS_LINESPLITMARK);
				bufShow.append(rs.getString("FCashAccName")).append("\t");
				bufShow.append(rs.getString("FDataBirth")).append("\t"); // BugNo:0000447
				// edit
				// by
				// jc
				/**shashijie 2012-5-8 STORY 2565 ETF 联接基金*/
				if (rs.getDate("FMatureDate")==null) {
					bufShow.append(" ").append("\t");
				} else {
					bufShow.append(YssFun.formatDate(rs.getDate("FMatureDate"))).append("\t");
				}
				/**end*/
				// BugNo:0000463
				// edit
				// by
				// jc
				bufShow.append(
						YssFun.formatDate(
								rs.getDate("FFactSettleDate") == null ? rs
										.getDate("FSettleDate") : rs
										.getDate("FFactSettleDate"),
								"yyyy-MM-dd")).append("\t"); // by leeyu add
				// 2009-01-04
				// MS00129
				// 将实际结算日期添加上，若实际结算日期为空的话同，就取结算日期
				bufShow.append(this.creatorName.trim()).append("\t");
				bufShow.append(this.creatorTime.trim()).append("\t");
				bufShow.append(this.checkUserName.trim()).append("\t");
				bufShow.append(this.checkTime.trim()).append("\t");
				// ---------------------------------------------------------------------
				// ------add by wuweiqi 20101116 增加费用字段
				// QDV4华夏2010年10月27日04_A-------//
				bufShow.append(rs.getString("FfeeName1")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTRADEFEE1"),
								"#,##0.##")).append("\t");
				bufShow.append(rs.getString("FfeeName2")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTRADEFEE2"),
								"#,##0.##")).append("\t");
				bufShow.append(rs.getString("FfeeName3")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTRADEFEE3"),
								"#,##0.##")).append("\t");
				bufShow.append(rs.getString("FfeeName4")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTRADEFEE4"),
								"#,##0.##")).append("\t");
				bufShow.append(rs.getString("FfeeName5")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTRADEFEE5"),
								"#,##0.##")).append("\t");
				bufShow.append(rs.getString("FfeeName6")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTRADEFEE6"),
								"#,##0.##")).append("\t");
				bufShow.append(rs.getString("FfeeName7")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTRADEFEE7"),
								"#,##0.##")).append("\t");
				bufShow.append(rs.getString("FfeeName8")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTRADEFEE8"),
								"#,##0.##")).append(YssCons.YSS_LINESPLITMARK);
				// -------end by wuweiqi
				// 201001116----------------------------------------//
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
			// ----2009-08-14 add by wangzuochun MS00024 交易数据拆分
			// QDV4.1赢时胜（上海）2009年4月20日24_A -----//
			VocabularyBean vocabulary = new VocabularyBean();
			vocabulary.setYssPub(pub);
			sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType);
			// -----------------------------------------------------------------------------//

			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f"
					+ yssPageInationBean.buildRowStr() + "\r\f" + tmpSec
					+ "\r\fvoc" + sVocStr; // 合中保的版本; 添加投资类型的词汇， modify by
			// wangzuochun 2009.08.14 MS00024
			// 交易数据拆分
			// QDV4.1赢时胜（上海）2009年4月20日24_A
			// //QDV4赢时胜上海2010年03月15日06_B
			// MS00884 by xuqiji
		} catch (Exception e) {
			throw new YssException("获取交易拆分数据信息出错：" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(dbl.getProcStmt());
		}

	}

	/**
	 * getListViewData2
	 * 
	 * @return String
	 */
	public String getListViewData2() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String strSql = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		try {
			sHeader = "交易拆分流水号\t交易证券\t交易方式\t成交日期\t结算日期\t投资组合\t投资经理\t交易券商\t交易数量\t交易价格\t交易金额\t投资总成本\t现金帐户";
			strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
					+ " d.FPortName, e.FSecurityName, e.FHandAmount, f.FInvMgrName, g.FTradeTypeName, "
					+ " h.FBrokerName as FBrokerName, o.FCashAccName, p.FAttrClsName,oo.FCashAccName as FFactCashAccName,e.FFactRate , ts.fseatname, sh.fstockholdername "
					+ // fanghaoln 20100406 MS01064 QDV4赢时胜(测试)2010年04月01日03_B
					" ,ooo.FCashAccName as FETFBalaAcctName from "//edit by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " a "
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
					+
					// ----------------------------------------------------------------------------------------------------
					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
				
					" left join (select FPortCode, FPortName from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where  FCheckState = 1) d on a.FPortCode = d.FPortCode "
					+
					//end by lidaolong
					
					// ----------------------------------------------------------------------------------------------------
					" left join (select eb.*,case when ec.FFactRate is null then 0 else ec.FFactRate end as FFactRate from (select FSecurityCode, max(FStartDate) as FStartDate from "
					+ // 合中保的版本
					pub.yssGetTableName("Tb_Para_Security")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate "
					+ " left join (select FSecurityCode,FFactRate from "
					+ pub.yssGetTableName("tb_para_fixinterest")
					+ " )ec on ea.FSecurityCode=ec.FSecurityCode "
					+ ") e on a.FSecurityCode = e.FSecurityCode "
					+ // 合中保的版本
					// ----------------------------------------------------------------------------------------------------
					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
					/*" left join (select fb.* from (select FInvMgrCode, max(FStartDate) as FStartDate from "
					+ pub.yssGetTableName("Tb_Para_InvestManager")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1 group by FInvMgrCode) fa join (select FInvMgrCode, FInvMgrName, FStartDate from "
					+ pub.yssGetTableName("Tb_Para_InvestManager")
					+ ") fb on fa.FInvMgrCode = fb.FInvMgrCode and fa.FStartDate = fb.FStartDate) f on a.FInvMgrCode = f.FInvMgrCode "
					+*/
					" left join (select FInvMgrCode, FInvMgrName from "
					+ pub.yssGetTableName("Tb_Para_InvestManager")
					+ " where  FCheckState = 1 ) f on a.FInvMgrCode = f.FInvMgrCode "
					+
					
					//end by lidaolong 
					// ----------------------------------------------------------------------------------------------------
					" left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode"
					+
					// ----------------------------------------------------------------------------------------------------
					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
					/*" left join (select hb.* from (select FBrokerCode, max(FStartDate) as FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Broker")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1 group by FBrokerCode) ha join (select FBrokerCode, FBrokerName, FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Broker")
					+ ") hb on ha.FBrokerCode = hb.FBrokerCode and ha.FStartDate = hb.FStartDate) h on a.FBrokerCode = h.FBrokerCode "
					+*/
					" left join (select FBrokerCode, FBrokerName from "
					+ pub.yssGetTableName("Tb_Para_Broker")
					+ " where  FCheckState = 1 ) h on a.FBrokerCode = h.FBrokerCode "
					+
					
					//end by lidaolong
					// ----------------------------------------------------------------------------------------------------
					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
				/*	" left join (select ob.* from (select FCashAccCode, max(FStartDate) as FStartDate from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1 group by FCashAccCode) oa join (select FCashAccCode, FCashAccName, FStartDate from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ ") ob on oa.FCashAccCode = ob.FCashAccCode and oa.FStartDate = ob.FStartDate) o on a.FCashAccCode = o.FCashAccCode "
					+*/
					" left join (select FCashAccCode, FCashAccName from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where  FCheckState = 1 ) o on a.FCashAccCode = o.FCashAccCode "
					+
					//end by lidaolong
					// ----------------------------------------------------------------------------------------------------
					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
					/*" left join (select oc.* from (select FCashAccCode, max(FStartDate) as FStartDate from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1 group by FCashAccCode) od join (select FCashAccCode, FCashAccName, FStartDate from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ ") oc on od.FCashAccCode = oc.FCashAccCode and od.FStartDate = oc.FStartDate) oo on a.FFactCashAccCode = oo.FCashAccCode "
					+*/
					" left join (select FCashAccCode, FCashAccName from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where  FCheckState = 1 ) oo on a.FFactCashAccCode = oo.FCashAccCode "
					//---edit by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName start---//
					+ " left join (select FCashAccCode, FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount") 
					+ " where FCheckState = 1) ooo on a.Fetfbalaacctcode = ooo.FCashAccCode "
					//---edit by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName end---//
					+
					
					//end by lidaolong
					// ----------------------------------------------------------------------------------------------------
					" left join (select FAttrClsCode,FAttrClsName from "
					+ pub.yssGetTableName("Tb_Para_AttributeClass")
					+ " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode "
					+
					// fanghaoln 20100406 MS01064 QDV4赢时胜(测试)2010年04月01日03_B
					" LEFT JOIN (SELECT FSeatCode, FSeatName"
					+ " FROM "
					+ pub.yssGetTableName("Tb_Para_Tradeseat")
					+ // 解析的时候新加了两个字段，SQL语句里面却没有查出来相应的字段
					" WHERE FCheckState = 1) ts ON a.fseatCode = ts.fseatcode"
					+ " LEFT JOIN (SELECT FStockholderCode, FStockholderName"
					+ " FROM "
					+ pub.yssGetTableName("Tb_Para_Stockholder")
					+ "  WHERE FCheckState = 1) sh ON a.fstockholdercode = sh.fstockholdercode"
					+
					// ------------------------------------------------end
					// -----MS01064--------------------------------------------
					((buildFilterSql().length() == 0) ? " where "
							: buildFilterSql() + " and ")
					+ " a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append((rs.getString("FNum") + "").trim()).append("\t");
				bufShow.append((rs.getString("FSecurityCode") + "").trim())
						.append("\t");
				bufShow.append((rs.getString("FTradeTypeName") + "").trim())
						.append("\t");
				bufShow.append(
						YssFun.formatDate((rs.getString("FBargainDate"))))
						.append("\t");
				bufShow
						.append(
								YssFun
										.formatDate((rs
												.getString("FSettleDate"))))
						.append("\t");
				bufShow.append((rs.getString("FPortName") + "").trim()).append(
						"\t");
				bufShow.append((rs.getString("FInvMgrName") + "").trim())
						.append("\t");
				bufShow.append((rs.getString("FBrokerName") + "").trim())
						.append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTradeAmount"),
								"#,##0.##")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTradePrice"),
								"#,##0.##")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTradeMoney"),
								"#,##0.##")).append("\t");
				bufShow.append(
						YssFun.formatNumber(rs.getDouble("FTotalCost"),
								"#,##0.##")).append("\t");
				bufShow.append((rs.getString("FCashAccCode") + "").trim())
						.append(YssCons.YSS_LINESPLITMARK);

				setResultSetAttr(rs);
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}

			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols();
		} catch (Exception e) {
			throw new YssException("获取交易拆分数据信息出错：" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * getListViewData3 对交易数据进行自动拆分
	 * 
	 * @return String
	 */
	public String getListViewData3() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String strSql = "";
		double portIntrest = 0;
		String strOriginCashAccCode = "";
		String strOriginCashAccName = "";
		String strFatherNum = "";
		boolean blIsOnlyOnePort = false;
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		StringBuffer buf = new StringBuffer();
		ITradeOperation tradeOper = (ITradeOperation) pub.getOperDealCtx()
				.getBean("tradedeal");
		CashAccountBean cashaccount = null;
		BaseCashAccLinkDeal cashacclinkOper = (BaseCashAccLinkDeal) pub
				.getOperDealCtx().getBean("cashacclinkdeal");
		ResultSet rs = null;
		int iNum = 0;
		double dTotalAmount;
		double dCheckAmount = 0; // MS00125百分比处理，判断下面的数据是否全部分配下去。
		boolean bCheck = false; // MS00125
		try {
			strFatherNum = this.num;
			dTotalAmount = this.tradeAmount;
			strOriginCashAccCode = this.cashAcctCode;
			strOriginCashAccName = this.cashAcctName;
			sHeader = this.getListView3Headers();
			tradeOper.setYssPub(pub);
			cashacclinkOper.setYssPub(pub);
			// add by leeyu 20090723 QDV4中保2009年07月27日03_B MS00585
			SecurityBean security = new SecurityBean();
			security.setYssPub(pub);
			security.setSecurityCode(securityCode);
			security.getSetting();
			// add by leeyu 20090723 QDV4中保2009年07月27日03_B MS00585
			if (this.portCode.trim().length() > 0) {
				if (this.portCode.indexOf("*") == 0) { // 当进行订单制作的时候 选择了一个投资组合
					// 那么就只加载这个组合出来
					blIsOnlyOnePort = true;
					this.portCode = this.portCode.substring(1);
				}
				this.portCode = this.operSql.sqlCodes(this.portCode.trim());
				if (this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)) {
					bCheck = true; // 当从前台选择了组合传过来，说明是用组合拆分的
				}
				tradeOper.initTradeOperation(this.securityCode,
						this.brokerCode, this.invMgrCode, this.tradeCode,
						this.orderNum, YssFun.toDate(this.bargainDate),
						this.subType, this.portCode);

			} else {
				tradeOper.initTradeOperation(this.securityCode,
						this.brokerCode, this.invMgrCode, this.tradeCode,
						this.orderNum, YssFun.toDate(this.bargainDate),
						this.subType);
			}
			tradeOper.loadTradeData();
			// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
			/*strSql = "select a.FPortCode,c.FPortName,c.FPortCury from (select FPortCode, max(FStartDate) As FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FAssetGroupCode = "
					+ dbl.sqlString(pub.getAssetGroupCode())
					+ " and FCheckState = 1 and FEnabled = 1 group by FPortCode) a "
					+ " join (select FPortCode, FPortName,FPortCury, FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ ") c on a.FPortCode = c.FPortCode and a.FStartDate = c.FStartDate"
					+ " join (select FPortCode, max(FStartDate) As FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
					+ " where FRelaType = 'InvestManager'"
					+ " and FSubCode = "
					+ dbl.sqlString(this.invMgrCode)
					+ " and FCheckState = 1 group by FPortCode) b on a.FPortCode = b.FPortCode and a.FStartDate = b.FStartDate";
		*/
			strSql = "select a.FPortCode,a.FPortName,a.FPortCury from (select FPortCode, FPortName,FPortCury from "
				+ pub.yssGetTableName("Tb_Para_Portfolio")
				+ " where FAssetGroupCode = "
				+ dbl.sqlString(pub.getAssetGroupCode())
				+ " and FCheckState = 1 and FEnabled = 1) a "
				
				+ " join (select FPortCode  from "
				+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
				+ " where FRelaType = 'InvestManager'"
				+ " and FSubCode = "
				+ dbl.sqlString(this.invMgrCode)
				+ " and FCheckState = 1 ) b on a.FPortCode = b.FPortCode ";
			
			//end by lidaolong
			
			if (blIsOnlyOnePort) {
				strSql += " where a.FPortCode = '" + this.portCode +"'";
			}

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				buf.setLength(0);
				this.num = strFatherNum + YssFun.formatNumber(iNum, "00000");
				this.haveAmount = tradeOper.getHave(rs.getString("FPortCode"));
				this.portCode = rs.getString("FPortCode") + "";
				this.portName = rs.getString("FPortName") + "";
				this.portCuryCode = rs.getString("FPortCury") + "";
				this.allotProportion = tradeOper.getScale(rs
						.getString("FPortCode"), this.tailPortCode);
				// 当拆分的比例小于０或拆分比例小于０时 QDV4中保2009年03月13日03_B MS00317 by leeyu
				// 20090416
				// 合并太平版本时取消
				// if ( (this.allotProportion < 0 || this.allotProportion > 1)
				// && this.tailPortCode.length() > 0) {
				// //添加尾差帐户作为条件　QDV4赢时胜（上海）2009年4月28日04_B MS00422 by leeyu
				// 200905012
				// throw new YssException("拆分分配比例不在0%-100%之间,拆分失败！");
				// }
				this.oldAllotAmount = tradeOper.getOriginDistAmount(rs
						.getString("FPortCode"), dTotalAmount);
				this.tradeAmount = tradeOper.getTailWipedDistAmount(rs
						.getString("FPortCode"), this.tailPortCode,
						dTotalAmount, this.oldAllotAmount, this.tradePrice);
				this.portCuryRate = this.getSettingOper().getCuryRate(
						YssFun.toDate(this.bargainDate),
						(this.bargainTime == null) ? "" : this.bargainTime
								.trim(), this.portCuryCode, this.portCode,
						YssOperCons.YSS_RATE_BASE);
				// 获取基础汇率 by leeyu 20090723 QDV4中保2009年07月27日03_B MS00585
				this.baseCuryRate = this.getSettingOper().getCuryRate(
						YssFun.toDate(this.bargainDate),
						(this.bargainTime == null) ? "" : this.bargainTime
								.trim(), security.getTradeCuryCode(),
						this.portCode, YssOperCons.YSS_RATE_BASE);
				// by leeyu 20090723 QDV4中保2009年07月27日03_B MS00585
				this.portCuryRate = YssD.round(this.portCuryRate, 15); // 把汇率保留15位
				// 茶仕春
				// 20080130
				if (this.factor == 0) {
					this.tradeMoney = YssD.mul(this.tradeAmount,
							this.tradePrice);
				} else {
					this.tradeMoney = YssFun
							.roundIt(YssD.div(YssD.mul(this.tradeAmount,
									this.tradePrice), this.factor), 2);

				}
				portIntrest = this.accruedInterest * this.allotProportion;
				// 当订单为卖出时，分配数量比持有数量大时 QDV4中保2009年03月13日03_B MS00317 by leeyu
				// 20090416
				/*
				 * 邵宏伟 20100106 临时取消控制 合并太平版本代码 if
				 * (tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale) &&
				 * this.tradeAmount > this.haveAmount &&
				 * this.tailPortCode.length() > 0) { //
				 * 添加尾差帐户作为条件　QDV4赢时胜（上海）2009年4月28日04_B // MS00422 by // leeyu
				 * // 200905012 throw new YssException("组合代码【" + this.portCode +
				 * "】持有证券代码为【" + this.securityCode + "】的数量" +
				 * YssFun.formatNumber(haveAmount, "#0,000.00") + "比卖出分配的数量" +
				 * YssFun.formatNumber(tradeAmount, "#0,000.00") + "小，拆分失败!"); }
				 * // 当订单为买入时，交易金额比持有的金额大 QDV4中保2009年03月13日03_B MS00317 by leeyu
				 * // 20090416
				 * 
				 * if (tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy) &&
				 * YssD.round(YssD.mul(tradeMoney, baseCuryRate), 2) >
				 * this.haveAmount && this.tailPortCode.length() > 0) { //
				 * 添加尾差帐户作为条件　QDV4赢时胜（上海）2009年4月28日04_B // MS00422 by // leeyu
				 * // 200905012 // //by leeyu // 20090723 // 统一用基础货币(港币)金额比较计算
				 * // QDV4中保2009年07月27日03_B // MS00585 throw new
				 * YssException("组合代码【" + this.portCode + "】持有证券代码为【" +
				 * this.securityCode + "】的现金金额" +
				 * YssFun.formatNumber(haveAmount, "#0,000.00") + "比买入分配的现金金额" +
				 * YssFun.formatNumber(YssD.round(YssD.mul( tradeMoney,
				 * baseCuryRate), 2),// by leeyu // 20090723 //
				 * QDV4中保2009年07月27日03_B // MS00585 "#0,000.00") + "小，拆分失败!"); }
				 */
				// if (this.tradeMoney > 0 ) {
				// 邵宏伟-20100817-取消控制--说明：用户的证券存放方式造成的。部分组合使用托管行性质时，库存券商不等同于交易券商，所以要支持0库存数量获取现金账户
				// 合并太平版本代码
				if (strOriginCashAccCode.trim().length() == 0) {
					cashacclinkOper.setLinkParaAttr(this.invMgrCode,
							this.portCode, this.securityCode, this.brokerCode,
							this.tradeCode, YssFun.toDate(bargainDate));
					cashaccount = cashacclinkOper.getCashAccountBean();
					if (cashaccount != null) {
						this.cashAcctCode = cashaccount.getStrCashAcctCode();
						this.cashAcctName = cashaccount.getStrCashAcctName();
					} else {
						this.cashAcctCode = "";
						this.cashAcctName = "";
					}
				} else {
					this.cashAcctCode = strOriginCashAccCode;
					this.cashAcctName = strOriginCashAccName;
				}
				// } else { 邵宏伟-20100817-取消控制 合并太平版本代码
				// this.cashAcctCode = "";
				// this.cashAcctName = "";
				// }

				this.fees = "";
				this.totalCost = 0;
				this.checkStateId = 1;

				buf.append(rs.getString("FPortCode")).append("\t");
				buf.append(rs.getString("FPortName")).append("\t");
				buf.append(YssFun.formatNumber(this.haveAmount, "#,##0.##"))
						.append("\t");
				if (bCheck) {
					dCheckAmount = dCheckAmount + haveAmount; // 这里把数量加上来
					// MS00125
				}
				buf.append(YssFun.formatNumber(this.allotProportion, "##.#%"))
						.append("\t");
				buf
						.append(
								YssFun.formatNumber(this.oldAllotAmount,
										"#,##0.##")).append("\t");
				buf.append(YssFun.formatNumber(this.tradeAmount, "#,##0.##"))
						.append("\t");
				buf.append(YssFun.formatNumber(this.tradePrice, "#,##0.####"))
						.append("\t");
				buf.append(YssFun.formatNumber(this.tradeMoney, "#,##0.##"))
						.append("\t");
				buf.append(YssFun.formatNumber(portIntrest, "#,##0.##"))
						.append("\t");
				buf.append(this.cashAcctCode).append("\t");
				buf.append(this.cashAcctName);
				bufShow.append(buf.toString())
						.append(YssCons.YSS_LINESPLITMARK);

				this.allotProportion = this.allotProportion * 100;

				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
				iNum++;
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
			if (bCheck) {
				dCheckAmount = dTotalAmount - dCheckAmount; // 这里将数量减出来
			}
			/*---20100112邵宏伟 取消检查 合并太平版本代码
			if (dCheckAmount > 0.0 && this.tailPortCode.length() > 0) { //添加尾差帐户作为条件　QDV4赢时胜（上海）2009年4月28日04_B MS00422 by leeyu 200905012
			   throw new YssException("当前所勾选的组合的可用库存数量小于总的分配数量，请重新勾选组合或重新输入交易数量");
			}*/
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView3ShowCols();
		} catch (Exception e) {
			throw new YssException("获取交易拆分数据信息出错！", e); // 向前台抛时只抛异常的信息，不抛其他的信息
			// by leeyu 2009-01-04
			// MS00125
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * getListViewData4
	 * 
	 * @return String
	 */
	public String getListViewData4() {

		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		sHeader = this.getListView3Headers();
		return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f"
				+ this.getListView3ShowCols();
	}

	/**
	 * addSetting
	 * 
	 * @return String
	 */
	public String addSetting() throws YssException {
		String strSql = "";
		String subNum = "";
		String strNumDate = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		String fNumType = "000000";// add by yanghaiming 20100911 MS01718
		// QDV4赢时胜上海2010年08月05日01_AB 存放交易方式
		try {
			if (attrClsCode != null
					&& this.attrClsCode
							.equalsIgnoreCase(YssOperCons.Yss_JYLX_CYDQ)) { // 处理债券持有到期

				// 2008.10.26 蒋锦 修改 使用反射实例化
				FixInterestBean fixInterest = (FixInterestBean) pub
						.getParaSettingCtx().getBean("fixinterest");
				fixInterest.setYssPub(pub);
				fixInterest.setStrSecurityCode(this.securityCode);
				fixInterest.getSetting();

				// 2008.10.26 蒋锦 为公共信息赋值
				((BaseDataSettingBean) fixInterest).parseRecLog();
				if (fixInterest.getDStartDate() == null
						&& fixInterest.getDtIssueDate() == null) {
					throw new YssException("代码为:" + securityCode
							+ "债券信息不存在,请先补充!");
				}
				fixInterest.setDFactRate(this.FactRate);

				// 修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
				fixInterest.setStrIssuePrice(new java.math.BigDecimal(
						tradePrice));
				this.securityCode = fixInterest.addFixInterestCYDQ(fixInterest,
						YssFun.toDate(bargainDate));
				tmpSec = securityCode;
			}
			strNumDate = YssFun.formatDatetime(YssFun.toDate(this.bargainDate))
					.substring(0, 8);

			// edit by yanghaiming 20100911 MS01718 QDV4赢时胜上海2010年08月05日01_AB
			if (this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)) {// 买入
				fNumType = "200000";
			} else if (this.tradeCode
					.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)
					|| // 卖出
					this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_XJDJ)) {// 现金对价
				fNumType = "900000";
			} else if (this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)
					|| // 派息
					this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SG)
					|| // 送股
					this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PG)
					|| // 配股
					this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_QZSP)
					|| // 权证送配
					this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PGJK)
					|| // 配股缴款
					this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_ZQPX)) {// 债券派息
				fNumType = "100000";
			} else if (this.tradeCode.equalsIgnoreCase(YssOperCons.Yss_JYLX_ZQ)) {// 债券兑付
				fNumType = "800000";
			} else {
				fNumType = "000000";
			}
			this.num = strNumDate
					+ dbFun.getNextInnerCode(pub
							.yssGetTableName("Tb_Data_Trade"), dbl.sqlRight(
							"FNUM", 6), fNumType, " where FNum like 'T"
							+ strNumDate + fNumType.substring(0, 1) + "%'", 1);
			// edit by yanghaiming 20100911 MS01718 QDV4赢时胜上海2010年08月05日01_AB
			this.num = "T" + this.num;
			// --- MS00813 QDV4中保2009年11月16日01_B 蒋世超 添加 2009.11.27
			// -----------------------------------

			if (this.orderNum.equalsIgnoreCase("")) {// 手工录入业务资料时需要判断交易号的前15位与订单的成交编号是否重复
				this.num = updateTradeNum(this.num); // 修正手工录入和订单拆分的业务资料的交易号(Fnum)前15位代码重复问题
			}
			// --- MS00813 QDV4中保2009年11月16日01_B end
			// -----------------------------------------------------
			subNum = this.num
					+ dbFun.getNextInnerCode(pub
							.yssGetTableName("Tb_Data_SubTrade"), dbl.sqlRight(
							"FNUM", 5), "00000", " where FNum like '"
							+ this.num.replaceAll("'", "''") + "%'");
			
			/** modified by zhaoxianlin 20121107 STORY #3208 代码注释掉  现在不需要在【证券借贷交易数据】界面产生关联数据*/
			// add by zhouxiang 2010.12.8 昨日无库存， 今天卖空的交易数据要求在证券借贷产生借入的交易数据
//			if (this.tradeCode.equalsIgnoreCase("02SS")) { // modify by fangjiang 2011.11.21 STORY #1433 请在4.0系统开发卖空交易的支持功能
//				ProduceBLTradeAdd(subNum);
//			} else if (this.tradeCode.equalsIgnoreCase("01SS")) {// 判断昨日是否有借入数量有的情况下，买入最自动归还  modify by fangjiang 2011.11.21 STORY #1433 请在4.0系统开发卖空交易的支持功能
//				ProduceBLTradeRcb(subNum);
//			}
			// end by zhouxiang 2010.12.8 昨日无库存， 今天卖空的交易数据要求在证券借贷产生借入的交易数据
			/** -----end----- */
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE,FInvestType,"
					+ // 添加字段FInvestType, modify by wangzuochun 2009.07.22
					// MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
					" FCASHACCCODE,FSeatCode,FStockholdercode,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME,FRateDate,"
					+ // edit by jc 添加字段 FRateDate
					" FSETTLEDATE,FFactSettleDate,FMATUREDATE,FMATURESETTLEDATE,"
					+ // 2007.11.16 添加 蒋锦 添加字段 FMatureDate FMatureSettleDate
					" FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR,"
					+ " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBailMoney,"
					+ " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4,"
					+ " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8,"
					+ " FTotalCost, FOrderNum, FDesc, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser,"
					+ " FFactCashAccCode,FFactSettleMoney,FExRate,FSettleDesc,FFactBaseRate,FFactPortRate,"
					+ // add liyu 1127 新增字段
					//edit  by zhouwei STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
					" FETFBalaAcctCode, FETFBalaSettleDate, FETFBalaMoney, FETFCashAlternat ,FBSDATE,FCANRETURNMONEY,FMtReplaceDate)" //modify huangqirong 2012-05-02 story #2565 ETF 联接基金 
					+ // 2009.06.22 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》
					// 国内基金业务
					" values("
					+ dbl.sqlString(subNum)
					+ ","
					+ dbl.sqlString(this.securityCode)
					+ ","
					+ dbl.sqlString(this.portCode)
					+ ","
					+ dbl.sqlString(this.brokerCode)
					+ ","
					+ dbl.sqlString((this.invMgrCode.length() == 0) ? " "
							: this.invMgrCode)
					+ ","
					+ // 2007.11.09 修改 蒋锦 如果没有填入投资经理代码就用空格代替
					dbl.sqlString(this.tradeCode)
					+ ","
					+
					// ------ add by wangzuochun 2009.07.22 MS00024 交易数据拆分
					// QDV4.1赢时胜（上海）2009年4月20日24_A -------//
					dbl.sqlString(this.investType)
					+ ","
					+ // 投资类型
					// --------------------------------------------------------------------------------------------------//

					dbl.sqlString(this.cashAcctCode.length() == 0 ? " "
							: this.cashAcctCode)
					+ ","
					+
					// ------------2009-07-07 蒋锦 添加 股东代码和席位设置--------------//
					// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
					dbl.sqlString(this.tradeSeatCode)
					+ ","
					+ dbl.sqlString(this.stockholderCode)
					+ ","
					+
					// -------------------------------------------------------------//
					dbl.sqlString(this.attrClsCode)
					+ ","
					+ dbl.sqlDate(this.bargainDate)
					+ ","
					+ dbl.sqlString(this.bargainTime)
					+ ","
					+ dbl.sqlDate(this.rateDate)
					+ ","
					+ // edit by jc
					dbl.sqlDate(this.settleDate)
					+ ","
					+ dbl.sqlDate(this.factSettleDate)
					+ ","
					+ dbl.sqlDate(this.MatureDate)
					+ ","
					+ dbl.sqlDate(this.MatureSettleDate)
					+ ","
					+ dbl.sqlString(this.settleTime)
					+ ","
					+ this.autoSettle
					+ ","
					+ this.portCuryRate
					+ ","
					+ this.baseCuryRate
					+ ","
					+ this.allotProportion / 100
					+ ","
					+ this.oldAllotAmount
					+ ","
					+ this.allotFactor
					+ ","
					+ this.tradeAmount
					+ ","
					+ this.tradePrice
					+ ","
					+ this.tradeMoney
					+ ","
					+ this.accruedInterest
					+ ","
					+ this.bailMoney
					+ ","
					+ this.operSql.buildSaveFeesSql(YssCons.OP_ADD, this.fees)
					+ this.totalCost
					+ ","
					+ dbl.sqlString(this.orderNum)
					+ ","
					+ dbl.sqlString(this.desc)
					+ ",0,"
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ","
					+ dbl.sqlString(this.creatorCode)
					+ ","
					+ dbl.sqlString(this.creatorTime)
					+ ","
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode)) + ","
					+ dbl.sqlString(this.factCashAccCode) + ","
					+ this.factSettleMoney + "," + this.exRate + ","
					+ dbl.sqlString(this.settleDesc) + "," + this.factBaseRate
					+ "," + this.factPortRate + ","
					+ dbl.sqlString(this.ETFBalaAcctCode) + ","
					+ dbl.sqlDate(this.ETFBalaSettleDate) + ","
					+ this.ETFBalanceMoney + "," + this.ETFCashAlternat
					+ ","+dbl.sqlDate(this.FBSDate)  //add by huangqirong 2012-05-02 story #2565 联接基金需要用
					+ ","+this.FCanReturnMoney 			//add by huangqirong 2012-05-02 story #2565 联接基金需要用
					+ ","+dbl.sqlDate(this.mtReplaceDate)//story 2727 add by zhouwei 20120619 必须现金替代结算日期  ETF链接基金					
					+ ")";
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			//手动修改成本 保存 add by zhouwei 20120308
			if(this.costIsHandEditState.equals("1")){	//手动修改成本
				strSql="update "+ pub.yssGetTableName("Tb_Data_SubTrade")+" set ";
				strSql += " FCost = " + this.cost.getCost() +
				          ", FMCost = " + this.cost.getMCost() +
				          ", FVCost = " + this.cost.getVCost() +
				          ", FBaseCuryCost = " + this.cost.getBaseCost() +
				          ", FMBASECURYCOST = " + this.cost.getBaseMCost() + 
				          ", FVBASECURYCOST = " + this.cost.getBaseVCost() + 
				          ", FPORTCURYCOST = " + this.cost.getPortCost() + 
				          ", FMPORTCURYCOST = " + this.cost.getPortMCost() +
				          ", FVPORTCURYCOST = " + this.cost.getPortVCost()+
				          ", fhandcoststate="+dbl.sqlString(this.costIsHandEditState)+
				          " where fnum="+dbl.sqlString(subNum);
				dbl.executeSql(strSql);
			
			}
			if (sMulRela != null && sMulRela.trim().length() > 0) {
				TradeRelaBean rela = new TradeRelaBean();
				TradeSubBean trade = new TradeSubBean();
				trade.setYssPub(pub);
				trade.setTradeCode(this.tradeCode);
				rela.setYssPub(pub);
				rela.setSTradeNum(subNum);
				rela.setTradeSub(trade);
				rela.saveMutliSetting(sMulRela);
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			// ---------------------------------重新汇总主表数据
			TradeDataAdmin trade = new TradeDataAdmin();
			trade.setYssPub(pub);
			trade.collectToTrade(subNum, securityCode, brokerCode, invMgrCode,
					tradeCode, bargainDate, true); // boolean值为false时no作删除操作,新建时不用传入FNum
			// 改成true;

			// ---------------------------------
			// ----------MS00290 QDV4华夏2009年3月05日01_B -----------------------
			if (this.fees.indexOf("\t") > 0) { // 在日志操作时，\t会影响解析
				this.fees = this.fees.replaceAll("\t", "~"); // 转换
			}
			// ------------------------------------------------------------------
			return buildRowStr();
		} catch (Exception e) {
			throw new YssException("新增交易拆分数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	/****************************************************
	 * @author蒋世超 2009.11.27 更新内部码
	 * @param tradeNum
	 *            交易号码：T+年份+内部码
	 * @return
	 * @throws YssException
	 */
	public String updateTradeNum(String tradeNum) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		String num = "";
		String num_pre = "";
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Order_Confirm")
					+ " where ftradenum='" + tradeNum + "'";
			rs = dbl.openResultSet(strSql);
			/*********************************************************
			 * 核对生成的内部码与订单号是否重复，如果重复,则内部码自动加1
			 */
			if (rs.next()) {
				num_pre = tradeNum.substring(0, 9);
				num = tradeNum.substring(9);
				int iNum = Integer.parseInt(num);
				iNum = iNum + 1;
				tradeNum = tradeNum.substring(0, 9)
						+ YssFun.formatNumber(iNum, "000000");
			}

			return tradeNum;
		} catch (Exception se) {
			throw new YssException("获取记录集数量失败！", se);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/*
	 * checkInput
	 * 
	 * @param btOper byte
	 */
	public void checkInput(byte btOper) {
	}

	/**
	 * checkSetting
	 */
	public void checkSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String strSql = "";
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			if (sRecycled.trim().length() == 0) {
				strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date()) + "'"
						+ " where FNum = " + dbl.sqlString(this.num);
				dbl.executeSql(strSql);
				strSql = "update " + pub.yssGetTableName("Tb_Data_TradeRela")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date()) + "'"
						+ " where FNum = " + dbl.sqlString(this.num);
				dbl.executeSql(strSql);
				strSql = "update "
						+ pub.yssGetTableName("Tb_Data_TradeRelaSub")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date()) + "'"
						+ " where FNum = " + dbl.sqlString(this.num);
				dbl.executeSql(strSql);
			} else {
				String[] arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql = "update "
							+ pub.yssGetTableName("Tb_Data_SubTrade")
							+ " set FCheckState = " + this.checkStateId
							+ ", FCheckUser = "
							+ dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = '"
							+ YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FNum = " + dbl.sqlString(this.num);
					dbl.executeSql(strSql);
					/** modified by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
					// 卖空交易证券借贷数据同步add by zhouxiang 2010.12.8
					//if("01SS".equalsIgnoreCase(this.tradeCode) || "02SS".equalsIgnoreCase(this.tradeCode)){ //modify by fangjiang 2011.11.21 story 1433
					/*if (CheckYestodayStock(this) > 0
							|| CheckYesterdayBorrowAmount(this) >= 0) {*/
						//strSql = "update "
						//		+ pub.yssGetTableName("tb_data_seclendtrade")
						//		+ " set FCheckState = " + this.checkStateId
						//		+ ", FCheckUser = "
						//		+ dbl.sqlString(pub.getUserCode())
						//		+ ", FCheckTime = '"
						//		+ YssFun.formatDatetime(new java.util.Date())
						//		+ "' where frelanum ="
						//		+ dbl.sqlString(this.num);
					//	dbl.executeSql(strSql);
					//}
					// 卖空交易证券借贷数据同步add by zhouxiang 2010.12.8
					/** -----end----- */
					strSql = "update "
							+ pub.yssGetTableName("Tb_Data_TradeRela")
							+ " set FCheckState = " + this.checkStateId
							+ ", FCheckUser = "
							+ dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = '"
							+ YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FNum = " + dbl.sqlString(this.num);
					dbl.executeSql(strSql);
					strSql = "update "
							+ pub.yssGetTableName("Tb_Data_TradeRelaSub")
							+ " set FCheckState = " + this.checkStateId
							+ ", FCheckUser = "
							+ dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = '"
							+ YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FNum = " + dbl.sqlString(this.num);
					dbl.executeSql(strSql);
				}
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new YssException("审核交易拆分数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	// ------------------------检测需批量反审核的数据是否结算------------------------
	public String getCheckTradeSub(String sMutilRowStr) throws YssException {
		TradeSubBean data = null;
		String[] multAudit = null;
		String strSql = "";
		ResultSet rs = null;
		try {
			if (multAuditString.length() > 0) {
				multAudit = sMutilRowStr.split("\f\f\f\f");
				if (multAudit.length > 0) {
					for (int i = 0; i < multAudit.length; i++) {
						data = new TradeSubBean();
						data.setYssPub(pub);
						data.parseRowStr(multAudit[i]);
						strSql = "select FSettleState from "
								+ pub.yssGetTableName("Tb_Data_SubTrade")
								+ " where FNum like '" + data.num
								+ "%' and FSettleState = 1 and FCheckState = 1";
						rs = dbl.openResultSet(strSql);
						if (rs.next()) {
							return "1," + data.num;
						}
						dbl.closeResultSetFinal(rs); //关闭打开的游标 add by wangzuochun
					}
					return "0,0";
				}
			}
		} catch (Exception ex) {
			throw new YssException("检测需批量反审核的数据是否结算出错!");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return "";
	}

	// --------------------------------------------------------------------------

	// -------------------------彭鹏 20080328 新增批量审核功能----------------------
	public String auditMutli(String sMutilRowStr) throws YssException {
		Connection conn = null;
		String sqlStr = "";
		String sqlBl = "";// 证券借贷数据同步审核 add by zhouxiang 2010.12.8
		String strSql = "";
		java.sql.PreparedStatement psmt = null;
		java.sql.PreparedStatement psmtbl = null;// 证券借贷数据审核add by zhouxiang
		// 2010.12.8
		boolean bTrans = false;
		TradeSubBean data = null;
		String[] multAudit = null;
		int oldCheckStateId;
		oldCheckStateId = this.checkStateId;
		try {
			conn = dbl.loadConnection();
			sqlStr = "update " + pub.yssGetTableName("Tb_Data_SubTrade")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FNum = ? ";
			/** add by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
//			sqlBl = "update "
//					+ pub.yssGetTableName("tb_data_seclendtrade")// 卖空交易证券借贷数据同步add
//					// by
//					// zhouxiang
//					// 2010.12.8
//					+ " set FCheckState = " + this.checkStateId
//					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
//					+ ", FCheckTime = '"
//					+ YssFun.formatDatetime(new java.util.Date())
//					+ "' where frelanum = ? ";
			//psmtbl = conn.prepareStatement(sqlBl);
			/** -----end----- */
			psmt = conn.prepareStatement(sqlStr);
			if (multAuditString.length() > 0) {
				multAudit = sMutilRowStr.split("\f\f\f\f");
				if (multAudit.length > 0) {
					for (int i = 0; i < multAudit.length; i++) {

						data = new TradeSubBean();
						data = this;
						data.setYssPub(pub);
						data.parseRowStr(multAudit[i]);
						/*if (CheckYestodayStock(data) > 0
								|| CheckYesterdayBorrowAmount(data) >= 0) {// 卖空交易证券借贷数据同步add
*/						//if("01SS".equalsIgnoreCase(this.tradeCode) || "02SS".equalsIgnoreCase(this.tradeCode)){ //modify by fangjiang 2011.11.21 story 1433
						//modify by zhaoxianlin 20121107 STORY #3208
							// by
							// zhouxiang
							// 2010.12.8
						//	psmtbl.setString(1, data.num);
						//	psmtbl.addBatch();
						//}
						psmt.setString(1, data.num);
						psmt.addBatch();
						// ---增加批量删除的日志记录功能----guojianhua add 20100906-------//
						data.fees = this.fees.replaceAll("\t", "~");
						logOper = SingleLogOper.getInstance();
						if (oldCheckStateId == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (oldCheckStateId == 1) {
							data.checkStateId = 1;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						} else if (oldCheckStateId == 0) {
							data.checkStateId = 0;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						}
						// -----------------------------------------//

					}
				}
				conn.setAutoCommit(false);
				bTrans = true;
				psmt.executeBatch();
				//psmtbl.executeBatch();  modified by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
			strSql = "update " + pub.yssGetTableName("Tb_Data_TradeRela")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FNum = ?";
			// 2008.04.08 蒋锦 修改 错误的 SQL 语句字符串变量 （strSql）
			psmt = conn.prepareStatement(strSql);
			if (multAuditString.length() > 0) {
				multAudit = sMutilRowStr.split("\f\f\f\f");
				if (multAudit.length > 0) {
					for (int i = 0; i < multAudit.length; i++) {
						data = new TradeSubBean();
						data = this;
						data.setYssPub(pub);
						data.parseRowStr(multAudit[i]);
						psmt.setString(1, data.num);
						psmt.addBatch();
					}
				}
				conn.setAutoCommit(false);
				bTrans = true;
				psmt.executeBatch();
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
			strSql = "update " + pub.yssGetTableName("Tb_Data_TradeRelaSub")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FNum = ?";
			// 2008.04.08 蒋锦 修改 错误的 SQL 语句字符串变量 （strSql）
			psmt = conn.prepareStatement(strSql);
			if (multAuditString.length() > 0) {
				multAudit = sMutilRowStr.split("\f\f\f\f");
				if (multAudit.length > 0) {
					for (int i = 0; i < multAudit.length; i++) {
						data = new TradeSubBean();
						data = this;
						data.setYssPub(pub);
						data.parseRowStr(multAudit[i]);
						psmt.setString(1, data.num);
						psmt.addBatch();
					}
				}
				conn.setAutoCommit(false);
				bTrans = true;
				psmt.executeBatch();
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new YssException("批量审核凭证数据表出错!");
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return "";
	}

	// ---------------------------------------------------------------------------

	/**
	 * delSetting
	 */
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String strSql = "";
		try {
			strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FNum = " + dbl.sqlString(this.num);
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			strSql = "update " + pub.yssGetTableName("Tb_Data_TradeRela")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FNum = " + dbl.sqlString(this.num);
			dbl.executeSql(strSql);
			strSql = "update " + pub.yssGetTableName("Tb_Data_TradeRelaSub")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FNum = " + dbl.sqlString(this.num);
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			// ---------------------------------重新汇总主表数据
			TradeDataAdmin trade = new TradeDataAdmin();
			trade.setYssPub(pub);
			trade.collectToTrade(num, securityCode, brokerCode, invMgrCode,
					tradeCode, bargainDate, true); // boolean值为true时作删除操作

			// ---------------------------------

		} catch (Exception e) {
			throw new YssException("删除交易拆分数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	// ----- MS00799 QDV4中保2009年11月4日01_B 2009.11.13 蒋世超添加 修正单笔结算反审核交易数据的bug.
	// ------------------
	public String editSetting() throws YssException {
		return editSetting(false);
	}

	// -----MS00799 QDV4中保2009年11月4日01_B end
	// -------------------------------------------------------------------

	/**
	 * 重载方法 添加boolean型参数state， 如果state为true,则在汇总主表数据后，不会改变该条主表数据的审核状态。
	 * 如果State为false,则在汇总主表数据后，改变该条主表数据的审核状态，由审核状态--->未审核状态。 蒋世超 2009.11.16
	 * editSetting
	 * 
	 * @return String
	 */
	public String editSetting(boolean state) throws YssException {
		String strSql = "";
		String subNum = "";
		boolean bTrans = false; // 代表是否开始了事务
		// ----by xuqiji 20090409 MS00366 修改业务资料的交易日期后，业务资料的交易编号没有跟着修改-------
		String strBargainDate = null; // 保存成交日期
		String strSubTradeDate = null; // 保存交易拆分数据流水号中的日期
		String newNum = null; // 保存新的交易拆分数据流水号
		// ----------------------------End MS00366 by xuqiji
		// 20090409-------------------------------

		Connection conn = dbl.loadConnection();
		try {
			// --------by xuqiji 20090409 MS00366 修改业务资料的交易日期后，业务资料的交易编号没有跟着修改
			// ------------
			strBargainDate = YssFun.formatDatetime(
					YssFun.toDate(this.bargainDate)).substring(0, 8); // 得到成交日期
			strSubTradeDate = this.num.substring(1, 9); // 得到交易拆分数据流水号中的日期

			// 判断成交日期是否等于交易拆分数据流水号中的日期
			if (!strBargainDate.equals(strSubTradeDate)) {
				newNum = "T"
						+ strBargainDate
						+ dbFun.getNextInnerCode(pub
								.yssGetTableName("Tb_Data_Trade"), dbl
								.sqlRight("FNUM", 6), "000000",
								" where FNum like 'T" + strBargainDate + "%'",
								1);

				// 得到修改日期的交易拆分数据流水号
				newNum = newNum
						+ dbFun.getNextInnerCode(pub
								.yssGetTableName("Tb_Data_SubTrade"), dbl
								.sqlRight("FNUM", 5), "00000",
								" where FNum like '"
										+ newNum.replaceAll("'", "''") + "%'");
			}
			// ----by xuqiji 20090409 MS00366 修改业务资料的交易日期后，业务资料的交易编号没有跟着修改
			// -----------------------------
			strSql = "update "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " set FNUM= "
					+ (strBargainDate.equals(strSubTradeDate) == true ? dbl
							.sqlString(this.num) : dbl.sqlString(newNum))
					+ // 当修改成交日期时，交易拆分数据流水号中的日期要保持和成交日期一致 by xuqiji 20090409
					// MS00366 修改业务资料的交易日期后，业务资料的交易编号没有跟着修改
					",FSECURITYCODE = "
					+ dbl.sqlString(this.securityCode)
					+ ",FPORTCODE = "
					+ dbl.sqlString(this.portCode)
					+ ",FBROKERCODE = "
					+ (this.brokerCode == null || this.brokerCode.length() == 0 ? dbl
							.sqlString(" ")
							: dbl.sqlString(this.brokerCode))
					+ ",FINVMGRCODE = "
					+ dbl.sqlString(this.invMgrCode.trim().length() == 0 ? " "
							: this.invMgrCode)
					+ ",FTRADETYPECODE = "
					+ dbl.sqlString(this.tradeCode)
					+
					// ------ add by wangzuochun 2009.07.22 MS00024 交易数据拆分
					// QDV4.1赢时胜（上海）2009年4月20日24_A -------//
					",FInvestType = "
					+ dbl.sqlString(this.investType)
					+
					// -------------------------------------------------------------------------------------------------------//
					",FCASHACCCODE = "
					+ dbl.sqlString(this.cashAcctCode.length() == 0 ? " "
							: this.cashAcctCode)
					+
					// ------------2009-07-07 蒋锦 添加 股东代码和席位设置--------------//
					// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
					",fseatCode = "
					+ dbl.sqlString(this.tradeSeatCode)
					+ ",fstockholdercode = "
					+ dbl.sqlString(this.stockholderCode)
					+
					// --------------------------------------------------------------//
					",FATTRCLSCODE = "
					+ dbl.sqlString(this.attrClsCode)
					+ ",FRateDate = "
					+ dbl.sqlDate(this.rateDate)
					+ // edit by jc
					",FBARGAINDATE = "
					+ dbl.sqlDate(this.bargainDate)
					+ ",FBARGAINTIME = "
					+ dbl.sqlString(this.bargainTime)
					+ ",FSETTLEDATE = "
					+ dbl.sqlDate(this.settleDate)
					+ ",FFactSettleDate = "
					+ dbl.sqlDate(this.factSettleDate)
					+ ",FMATUREDATE = "
					+ dbl.sqlDate(this.MatureDate)
					+ // 2007.11.16 添加 蒋锦
					",FMATURESETTLEDATE = "
					+ dbl.sqlDate(this.MatureSettleDate)
					+ // 添加字段 FMATUREDATE FMATURESETTLEDATE
					",FFactCashAccCode = "
					+ dbl.sqlString(this.factCashAccCode)
					+ ",FFactSettleMoney = " + this.factSettleMoney
					+ ",FExRate=" + this.exRate + ",FSettleDesc="
					+ dbl.sqlString(this.settleDesc) + ",FSETTLETIME = "
					+ dbl.sqlString(this.settleTime) + ",FAUTOSETTLE = "
					+ this.autoSettle + ",FPORTCURYRATE = " + this.portCuryRate
					+ ",FBASECURYRATE = " + this.baseCuryRate
					+ ",FALLOTPROPORTION = " + this.allotProportion / 100
					+ ",FOLDALLOTAMOUNT = " + this.oldAllotAmount
					+ ",FALLOTFACTOR = " + this.allotFactor
					+ ",FTRADEAMOUNT = " + this.tradeAmount + ",FTRADEPRICE = "
					+ this.tradePrice + ",FTRADEMONEY = " + this.tradeMoney
					+ ",FBailMoney = " + this.bailMoney
					+ ",FACCRUEDINTEREST = " + this.accruedInterest
					+ ",FTOTALCOST = " + this.totalCost + ",FFactBaseRate = "
					+ this.factBaseRate + ",FFactPortRate = "
					+ this.factPortRate;
			// ---- MS00813 QDV4中保2009年11月16日01_B 蒋世超
			// 2009.11.23------------------------------------------------------------------
			//story 1574 add by zhouwei 20111109 对于分红转投的交易数据，成本是按照转投的金额来计算的
			if(this.tradeCode.equals("39")){			
				this.cost.setCost(this.tradeMoney);
				this.cost.setMCost(this.tradeMoney);
				this.cost.setVCost(this.tradeMoney);
				this.cost.setBaseCost(YssD.round(YssD.mul(this.tradeMoney,
						this.baseCuryRate), 2));
				this.cost.setBaseMCost(YssD.round(YssD.mul(this.tradeMoney,
						this.baseCuryRate), 2));
				this.cost.setBaseVCost(YssD.round(YssD.mul(this.tradeMoney,
						this.baseCuryRate), 2));
				if (this.portCuryRate != 0) {
					this.cost.setPortCost(YssD.round(YssD.div(this.cost
							.getBaseCost(), this.portCuryRate), 2));
					this.cost.setPortMCost(YssD.round(YssD.div(this.cost
							.getBaseMCost(), this.portCuryRate), 2));
					this.cost.setPortVCost(YssD.round(YssD.div(this.cost
							.getBaseVCost(), this.portCuryRate), 2));
				} else {
					this.cost.setPortCost(0);
					this.cost.setPortMCost(0);
					this.cost.setPortVCost(0);
				}
				strSql += ", FCost = " + this.cost.getCost() +
				          ", FMCost = " + this.cost.getMCost() +
				          ", FVCost = " + this.cost.getVCost() +
				          ", FBaseCuryCost = " + this.cost.getBaseCost() +
				          ", FMBASECURYCOST = " + this.cost.getBaseMCost() + 
				          ", FVBASECURYCOST = " + this.cost.getBaseVCost() + 
				          ", FPORTCURYCOST = " + this.cost.getPortCost() + 
				          ", FMPORTCURYCOST = " + this.cost.getPortMCost() +
				          ", FVPORTCURYCOST = " + this.cost.getPortVCost();
			
			}
			//手动修改成本 保存 add by zhouwei 20120308
			if(this.costIsHandEditState.equals("1")){	//手动修改成本						
				strSql += ", FCost = " + this.cost.getCost() +
				          ", FMCost = " + this.cost.getMCost() +
				          ", FVCost = " + this.cost.getVCost() +
				          ", FBaseCuryCost = " + this.cost.getBaseCost() +
				          ", FMBASECURYCOST = " + this.cost.getBaseMCost() + 
				          ", FVBASECURYCOST = " + this.cost.getBaseVCost() + 
				          ", FPORTCURYCOST = " + this.cost.getPortCost() + 
				          ", FMPORTCURYCOST = " + this.cost.getPortMCost() +
				          ", FVPORTCURYCOST = " + this.cost.getPortVCost()+
				          ", fhandcoststate="+dbl.sqlString(this.costIsHandEditState);
			
			}
			if (!state) {
				strSql += ",FORDERNUM = " + dbl.sqlString(this.orderNum); // 单笔结算时，发现会将订单号置为空，所以这里添加判断
				// 如果
				// 进行单笔结算时，不对订单号进行处理
				// 蒋世超添加
				// 2009.11.23
			}
			;
			// ---- MS00813 QDV4中保2009年11月16日01_B end
			// --------------------------------------------------------------
			strSql += ",FDESC = "
					+ dbl.sqlString(this.desc)
					+ ","
					+ this.operSql.buildSaveFeesSql(YssCons.OP_EDIT, this.fees)
					+ " FCREATOR = "
					+ dbl.sqlString(this.creatorCode)
					+ ",FCREATETIME = "
					+ dbl.sqlString(this.creatorTime)
					+ (settleState.equalsIgnoreCase("1") ? (",FSettleState =" + settleState)
							: "")
					+
					// 2009.06.22 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》
					// 国内基金业务
					",FETFBalaAcctCode = "
					+ dbl.sqlString(this.ETFBalaAcctCode)
					+ ",FETFBalaSettleDate = "
					+ dbl.sqlDate(this.ETFBalaSettleDate) + ",FETFBalaMoney = "
					+ this.ETFBalanceMoney + ",FETFCashAlternat = "
					+ this.ETFCashAlternat +
					 ", FBSDATE = " + dbl.sqlDate(this.FBSDate) //add by huangqirong 2012-05-02 story #2565 联接基金需要用
					+ " , FCANRETURNMONEY = " + this.FCanReturnMoney +//add by huangqirong 2012-05-02 story #2565 联接基金需要用
					// --------------------------------------------
					",FMtReplaceDate="+dbl.sqlDate(this.mtReplaceDate)+//story 2727 add by zhouwei 20120619 必须现金替代结算日期  ETF链接基金
					" where FNUM = " + dbl.sqlString(this.num);

			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			
			//---add by songjie 2012.10.09 BUG 5874 QDV4赢时胜(上海)2012年09月27日01_B start---//
			if(this.tradeCode.equalsIgnoreCase("02SS") || this.tradeCode.equalsIgnoreCase("01SS")){
				editSecLendTrade();
			}
			//---add by songjie 2012.10.09 BUG 5874 QDV4赢时胜(上海)2012年09月27日01_B end---//
			
			if (sMulRela != null && sMulRela.trim().length() > 0) {
				TradeRelaBean rela = new TradeRelaBean();
				TradeSubBean trade = new TradeSubBean();
				trade.setYssPub(pub);
				trade.setTradeCode(tradeCode);
				rela.setYssPub(pub);
				rela.setSTradeNum(num);
				rela.setTradeSub(trade);
				rela.saveMutliSetting(sMulRela);
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			// ---------------------------------重新汇总主表数据
			TradeDataAdmin trade = new TradeDataAdmin();
			trade.setYssPub(pub);
			// ----- MS00799 QDV4中保2009年11月4日01_B 2009.11.13 蒋世超添加
			// 修正单笔结算反审核交易数据的bug. ------------------
			if (state) {
				trade.collectToTrade(num, securityCode, brokerCode, invMgrCode,
						tradeCode, bargainDate, true, true);
			} else {
				trade.collectToTrade(num, securityCode, brokerCode, invMgrCode,
						tradeCode, bargainDate, true);// boolean值为true时作删除操作
			}
			// -----MS00799 QDV4中保2009年11月4日01_B end
			// ------------------------------------------------------------------

			// ---------------------------------
			// ----------MS00290 QDV4华夏2009年3月05日01_B -----------------------
			if (this.fees.indexOf("\t") > 0) { // 在日志操作时，\t会影响解析
				this.fees = this.fees.replaceAll("\t", "~"); // 转换
			}
			// ------------------------------------------------------------------
			return buildRowStr();
		} catch (Exception e) {
			throw new YssException("修改交易拆分数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	/**
	 * add by songjie 2012.10.09 
	 * BUG 5874 QDV4赢时胜(上海)2012年09月27日01_B
	 */
	private void editSecLendTrade()throws YssException{
		String strSql = "";
		ResultSet rs = null;
		String subNum = "";
		try{
			strSql = " select FNum from " + pub.yssGetTableName("Tb_Data_SecLendTrade") + " where FRelaNum = " + dbl.sqlString(this.num);
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				subNum = rs.getString("FNum");
			}
			
			if(this.tradeCode.equalsIgnoreCase("02SS")) {
				blTradeEdit(subNum,"borrow");
			} else {
				blTradeEdit(subNum,"Rcb");
			}
		}catch(Exception e){
			throw new YssException("更新证券借贷交易数据出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2012.10.09 
	 * BUG 5874 QDV4赢时胜(上海)2012年09月27日01_B
	 * @param subNum
	 * @param num
	 * @throws YssException
	 */
	private void blTradeEdit(String subNum, String tradeTypeCode) throws YssException{
		String strSql = "";
		double dCost = 0;//原币成本
		double dBaseCost = 0;//基础货币成本
		double dPortCost = 0;//组合货币成本
		try{
			dCost = YssD.mul(this.tradeAmount, this.tradePrice);
			dBaseCost = YssD.mul(dCost, this.baseCuryRate);
			dPortCost = YssD.div(dBaseCost, this.portCuryRate);

			strSql = " update " + pub.yssGetTableName("TB_DATA_SecLendTRADE") +
			" set FSecurityCode = " + dbl.sqlString(this.securityCode) +
			" ,FBARGAINDATE = " + dbl.sqlDate(this.bargainDate) +
			" ,FBARGAINTIME = " + dbl.sqlString(this.bargainTime) +
			" ,FTRADETYPECODE = " + dbl.sqlString(tradeTypeCode) +
			" ,FSETTLEDATE = " + dbl.sqlDate(this.settleDate) + 
			" ,FSETTLETIME = " + dbl.sqlString(this.settleTime) + 
			" ,FINVMGRCODE = " + ((this.invMgrCode.trim().length() > 0) ? 
					             dbl.sqlString(this.invMgrCode) : "' '")  +
			" ,FPORTCODE = " + dbl.sqlString(this.portCode) +
			" ,FBrokerCode = " + ((this.brokerCode.trim().length() > 0) ? 
					             dbl.sqlString(this.brokerCode) : "' '") +
			" ,FATTRCLSCODE = " + dbl.sqlString(this.attrClsCode) +
			" ,FCASHACCCODE = " + dbl.sqlString(this.cashAcctCode) +
			" ,FagreementType = '协商式'" + 
			" ,FTRADEAMOUNT = " + this.tradeAmount +
			" ,FTRADEPRICE = " + this.tradePrice +
			" ,FPeriodDate = " + dbl.sqlDate(this.bargainDate) + 
			" ,FLendRatio = 0, FPeriodCode = '', FFormulaCode = ' ' "+
			" ,FCost = " + dCost +
			" ,FMCost = " + dCost +
			" ,FVCost = " + dCost +
			" ,FBaseCuryCost = " + dBaseCost +
			" ,FMBaseCuryCost = " + dBaseCost +
			" ,FVBaseCuryCost = " + dBaseCost +
			" ,FPortCuryCost = " + dPortCost +
			" ,FMPortCuryCost = " + dPortCost +
			" ,FVPortCuryCost = " + dPortCost +
			" ,FCheckState = 0" + 
			" ,FCreator = " + dbl.sqlString(this.creatorCode) +
			" ,FCreateTime = " + dbl.sqlString(this.creatorTime) +
			" ,FCheckUser = ' '" + 
			" ,FRelaNum = " + dbl.sqlString(this.num) +
			" where FNum = " + dbl.sqlString(subNum);
			
			dbl.executeSql(strSql);
		}catch(Exception e){
			throw new YssException("更新证券借贷交易数据出错！");
		}
	}
	
	/***********************************************************
	 * MS01740 QDV4赢时胜深圳2010年9月14日01_B 未进行交易结算便估值和延迟结算产生的问题 add by jiangshichao
	 * 
	 * 根据结算中心修改的结算日期、结算账户、结算金额来更新交易子表中的实际结算日期、实际结算账户等
	 * 
	 * @throws YssException
	 */
	public void updateSetInfo() throws YssException {
		String strSql = "";
		String subNum = "";
		boolean bTrans = false; // 代表是否开始了事务
		// ----by xuqiji 20090409 MS00366 修改业务资料的交易日期后，业务资料的交易编号没有跟着修改-------
		String strBargainDate = null; // 保存成交日期
		String strSubTradeDate = null; // 保存交易拆分数据流水号中的日期
		String newNum = null; // 保存新的交易拆分数据流水号
		// String query = "";
		// String updateSql = "";
		// int SettleState = 0;
		// ResultSet rs = null;
		// ----------------------------End MS00366 by xuqiji
		// 20090409-------------------------------

		Connection conn = dbl.loadConnection();
		try {
			// --------by xuqiji 20090409 MS00366 修改业务资料的交易日期后，业务资料的交易编号没有跟着修改
			// ------------
			strBargainDate = YssFun.formatDatetime(
					YssFun.toDate(this.bargainDate)).substring(0, 8); // 得到成交日期
			strSubTradeDate = this.num.substring(1, 9); // 得到交易拆分数据流水号中的日期

			// 判断成交日期是否等于交易拆分数据流水号中的日期
			if (!strBargainDate.equals(strSubTradeDate)) {
				newNum = "T"
						+ strBargainDate
						+ dbFun.getNextInnerCode(pub
								.yssGetTableName("Tb_Data_Trade"), dbl
								.sqlRight("FNUM", 6), "000000",
								" where FNum like 'T" + strBargainDate + "%'",
								1);

				// 得到修改日期的交易拆分数据流水号
				newNum = newNum
						+ dbFun.getNextInnerCode(pub
								.yssGetTableName("Tb_Data_SubTrade"), dbl
								.sqlRight("FNUM", 5), "00000",
								" where FNum like '"
										+ newNum.replaceAll("'", "''") + "%'");
			}
			// query = "select FSettleState from " +
			// pub.yssGetTableName("Tb_Data_SubTrade") + " where FNUM = " +
			// dbl.sqlString(this.num);
			// rs = dbl.openResultSet(query);
			// while(rs.next()){
			// SettleState = rs.getInt("FSettleState");
			// }
			// if(SettleState ==3){
			// updateSql = ",FMATUREDATE = " +
			// dbl.sqlDate(YssFun.formatDate(this.factSettleDate).equalsIgnoreCase("9998-12-31")?this.MatureDate:this.factSettleDate)
			// + //2007.11.16 添加 蒋锦
			// ",FMATURESETTLEDATE = " +
			// dbl.sqlDate(YssFun.formatDate(this.factSettleDate).equalsIgnoreCase("9998-12-31")?this.MatureSettleDate:this.factSettleDate)
			// ; //添加字段 FMATUREDATE FMATURESETTLEDATE
			// }
			// ----by xuqiji 20090409 MS00366 修改业务资料的交易日期后，业务资料的交易编号没有跟着修改
			// -----------------------------
			strSql = "update "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " set FNUM= "
					+ (strBargainDate.equals(strSubTradeDate) == true ? dbl
							.sqlString(this.num) : dbl.sqlString(newNum))
					+ // 当修改成交日期时，交易拆分数据流水号中的日期要保持和成交日期一致 by xuqiji 20090409
					// MS00366 修改业务资料的交易日期后，业务资料的交易编号没有跟着修改
					",FFactSettleDate = " + dbl.sqlDate(this.factSettleDate)
					+ ",FFactCashAccCode = "
					+ dbl.sqlString(this.factCashAccCode)
					+ ",FFactSettleMoney = " + this.factSettleMoney
					+ ",FExRate=" + this.exRate + ",FSettleDesc="
					+ dbl.sqlString(this.settleDesc) + ",FFactBaseRate = "
					+ this.factBaseRate + ",FFactPortRate = "
					+ this.factPortRate +",fsecurityDelaySettleState="+this.FSecurityDelaySettleState    //story 1566 add by zhouwei 20111018 新增证券交割延迟标示
					+ " where FNUM = "
					+ dbl.sqlString(this.num);

			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			if (sMulRela != null && sMulRela.trim().length() > 0) {
				TradeRelaBean rela = new TradeRelaBean();
				TradeSubBean trade = new TradeSubBean();
				trade.setYssPub(pub);
				trade.setTradeCode(tradeCode);
				rela.setYssPub(pub);
				rela.setSTradeNum(num);
				rela.setTradeSub(trade);
				rela.saveMutliSetting(sMulRela);
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			// ---------------------------------重新汇总主表数据
			TradeDataAdmin trade = new TradeDataAdmin();
			trade.setYssPub(pub);
			// ----- MS00799 QDV4中保2009年11月4日01_B 2009.11.13 蒋世超添加
			// 修正单笔结算反审核交易数据的bug. ------------------
			trade.collectToTrade(num, securityCode, brokerCode, invMgrCode,
					tradeCode, bargainDate, true);// boolean值为true时作删除操作

			// ----------MS00290 QDV4华夏2009年3月05日01_B -----------------------
			if (this.fees.indexOf("\t") > 0) { // 在日志操作时，\t会影响解析
				this.fees = this.fees.replaceAll("\t", "~"); // 转换
			}

		} catch (Exception e) {
			throw new YssException("修改交易拆分数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			// dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * getAllSetting
	 * 
	 * @return String
	 */
	public String getAllSetting() {
		return "";
	}

	/**
	 * getSetting
	 * 
	 * @return IDataSetting
	 */
	public IDataSetting getSetting() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FNum = " + this.num;
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				this.securityCode = rs.getString("FSecurityCode") + "";
				this.portCode = rs.getString("FPortCode") + "";
				this.brokerCode = rs.getString("FBrokerCode") + "";
				this.invMgrCode = rs.getString("FInvMgrCode") + "";
				this.tradeCode = rs.getString("FTradeTypeCode") + "";
				this.cashAcctCode = rs.getString("FCashAccCode") + "";
				// ------------2009-07-07 蒋锦 添加 股东代码和席位设置--------------//
				// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
				this.tradeSeatCode = rs.getString("fseatCode") + "";
				this.stockholderCode = rs.getString("fstockholdercode") + "";
				// -------------------------------------------------------------//
				this.attrClsCode = rs.getString("FAttrClsCode") + "";
				this.bargainDate = rs.getDate("FBargainDate") + "";
				this.bargainTime = rs.getString("FBargainTime") + "";
				this.settleDate = rs.getDate("FSettleDate") + "";
				this.settleTime = rs.getString("FSettleTime") + "";
				// -------------------2007.11.16 添加
				// 蒋锦-----添加字段--------------------//
				this.MatureDate = rs.getDate("FMatureDate") + "";
				this.MatureSettleDate = rs.getDate("FMatureSettleDate") + "";
				// ----------------------------------------------------------------------//
				this.rateDate = rs.getDate("FRateDate") + ""; // edit by jc
				this.autoSettle = rs.getString("FAutoSettle") + "";
				this.portCuryRate = rs.getDouble("FPortCuryRate");
				this.baseCuryRate = rs.getDouble("FBaseCuryRate");
				this.allotProportion = rs.getDouble("FAllotProportion");
				this.oldAllotAmount = rs.getDouble("FOldAllotAmount");
				this.allotFactor = rs.getDouble("FAllotFactor");
				this.tradeAmount = rs.getDouble("FTradeAmount");
				this.tradePrice = rs.getDouble("FTradePrice");
				this.tradeMoney = rs.getDouble("FTradeMoney");
				this.accruedInterest = rs.getDouble("FAccruedInterest");
				this.totalCost = rs.getDouble("FTotalCost");
				this.orderNum = rs.getString("FOrderNum");
				this.desc = rs.getString("FDesc");
				this.factCashAccCode = rs.getString("FFactCashAccCode"); // add
				// liyu
				// 1127
				// 实际帐户
				this.factSettleMoney = rs.getDouble("FFactSettleMoney");
				this.exRate = rs.getDouble("FExRate");
				this.settleDesc = rs.getString("FSettleDesc");
				this.factSettleDate = rs.getString("FFactSettleDate");
				this.factSettleMoney = rs.getDouble("FFactSettleMoney");
				this.factBaseRate = rs.getDouble("FFactBaseRate");
				this.factPortRate = rs.getDouble("FFactPortRate");
				// ---------2009.06.22 蒋锦 添加 MS00013
				// 《QDV4.1赢时胜（上海）2009年4月20日13_A》 国内基金业务--------------//
				this.ETFBalaAcctCode = rs.getString("FETFBalaAcctCode");
				this.ETFBalaSettleDate = rs.getString("FETFBalaSettleDate") == null ? ""
						: YssFun.formatDate(rs.getDate("FETFBalaSettleDate"),
								"yyyy-MM-dd");
				this.ETFBalanceMoney = rs.getDouble("FETFBalaMoney");
				this.ETFCashAlternat = rs.getDouble("FETFCashAlternat");
				// -----------------------------------------------------------------------------------------------------//
				this.FBSDate = rs.getString("FBSDATE") == null ? "" : YssFun.formatDate(rs.getString("FBSDATE"),"yyyy-MM-dd");//add by huangqirong 2012-05-02 story #2565 联接基金需要用
				this.FCanReturnMoney = rs.getDouble("FCANRETURNMONEY");	//add by huangqirong 2012-05-02 story #2565 联接基金需要用
			}
			return this;
		}

		catch (Exception e) {
			throw new YssException("获取交易拆分数据信息出错：" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * getSetting 获取单个实例数据 by
	 * xuxuming,20090918.MS00700,债券应收和转货基本数据要相应，不用重新输入.QDV4中保2009年09月15日02_B
	 * 
	 * @return IDataSetting
	 */
	public IDataSetting getSettingByFilter() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Data_SubTrade")
					+ " a " + buildFilterSql();
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				this.securityCode = rs.getString("FSecurityCode") + "";
				this.portCode = rs.getString("FPortCode") + "";
				this.brokerCode = rs.getString("FBrokerCode") + "";
				this.invMgrCode = rs.getString("FInvMgrCode") + "";
				this.tradeCode = rs.getString("FTradeTypeCode") + "";
				this.cashAcctCode = rs.getString("FCashAccCode") + "";
				this.attrClsCode = rs.getString("FAttrClsCode") + "";
				this.bargainDate = rs.getDate("FBargainDate") + "";
				this.bargainTime = rs.getString("FBargainTime") + "";
				this.settleDate = rs.getDate("FSettleDate") + "";
				this.settleTime = rs.getString("FSettleTime") + "";
				this.MatureDate = rs.getDate("FMatureDate") + "";
				this.MatureSettleDate = rs.getDate("FMatureSettleDate") + "";
				this.rateDate = rs.getDate("FRateDate") + "";
				this.autoSettle = rs.getString("FAutoSettle") + "";
				this.portCuryRate = rs.getDouble("FPortCuryRate");
				this.baseCuryRate = rs.getDouble("FBaseCuryRate");
				this.allotProportion = rs.getDouble("FAllotProportion");
				this.oldAllotAmount = rs.getDouble("FOldAllotAmount");
				this.allotFactor = rs.getDouble("FAllotFactor");
				this.tradeAmount = rs.getDouble("FTradeAmount");
				this.tradePrice = rs.getDouble("FTradePrice");
				this.tradeMoney = rs.getDouble("FTradeMoney");
				this.accruedInterest = rs.getDouble("FAccruedInterest");
				this.totalCost = rs.getDouble("FTotalCost");
				this.orderNum = rs.getString("FOrderNum");
				this.desc = rs.getString("FDesc");
				this.factCashAccCode = rs.getString("FFactCashAccCode");
				this.factSettleMoney = rs.getDouble("FFactSettleMoney");
				this.exRate = rs.getDouble("FExRate");
				this.settleDesc = rs.getString("FSettleDesc");
				this.factSettleDate = rs.getString("FFactSettleDate");
				this.factSettleMoney = rs.getDouble("FFactSettleMoney");
				this.factBaseRate = rs.getDouble("FFactBaseRate");
				this.factPortRate = rs.getDouble("FFactPortRate");
			}
			return this;
		}

		catch (Exception e) {
			throw new YssException("获取交易拆分数据信息出错：" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * saveMutliSetting
	 * 
	 * @param sMutilRowStr
	 *            String
	 * @return String
	 */
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		return saveMutliOperData(sMutilRowStr, false, "", null); // 合中保的版本
	}

	public String saveMutliOperData(String sMutilRowStr, boolean bIsTrans,
			String iNum, TradeBean trade) throws YssException { // by leeyu
		// 合并中保的版本
		String[] sMutilRowAry = null;
		String[] sFee = null;
		boolean bTrans = false;
		String subNum = "";
		String sqlSubNums = "";
		String securityCode = "";
		PreparedStatement pstmt = null;
		Connection conn = dbl.loadConnection();
		String strSql = "";
		try {
			if (sMutilRowStr.length() == 0) {
				return "";
			}
			// ---------2007.11.14 修改
			// 蒋锦-------如果只是单纯的删除不需要保存，则解析字符串的长度不会大于20--------//
			if ((this.num = sMutilRowStr.split("\b\b")[0]).length() < 20) {
				sMutilRowAry = new String[0];
			} else {
				sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
				this.parseRowStr(sMutilRowAry[0]);
			}
			// ---------------------------------------------------------------------------------------------//
			if (iNum.length() > 0) {
				this.num = iNum;
			}

			if (this.num.length() == 0) {
				return "";
			}
			strSql = "delete from " + pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FNum like '" + this.num.replaceAll("'", "''")
					+ "%'";
			if (!bIsTrans) {
				conn.setAutoCommit(false);
				bTrans = true;
			}
			dbl.executeSql(strSql);

			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE,"
					+ " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME,"
					+ " FSETTLEDATE,FFactSettleDate,FMATUREDATE,FMATURESETTLEDATE,"
					+ // 2007.11.16 添加 蒋锦 添加字段 FMATUREDATE,FMATURESETTLEDATE
					" FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR,"
					+ " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,"
					+ " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4,"
					+ " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8,"
					+ " FTotalCost, FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, FPortCuryCost,"
					+ " FMPortCuryCost, FVPortCuryCost, FSettleState, FOrderNum,FBailMoney, FDesc, FDataSource, FCheckState, FCreator, FCreateTime,FCheckUser,"
					+ " FFactCashAccCode,FFactSettleMoney,FExRate,FSettleDesc,FFactBaseRate,FFactPortRate,FRateDate,"
					+ // edit by jc 添加字段FRateDate
					" FETFBalaAcctCode, FETFBalaSettleDate, FETFBalaMoney, FETFCashAlternat, fseatCode, fstockholdercode , FBSDATE , FCANRETURNMONEY)" //modify by huangqirong 2012-05-02 story #2565 联接基金需要用
					+ // 2009.06.22 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》
					// 国内基金业务
					" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//modify by huangqirong 2012-05-02 story #2565 联接基金需要用
			pstmt = conn.prepareStatement(strSql);

			for (int i = 0; i < sMutilRowAry.length; i++) {
				if (i > 0) {
					this.parseRowStr(sMutilRowAry[i]);
					if (iNum.length() > 0) {
						this.num = iNum;
					}
				}
				// =====================合中保的版本
				if (trade != null && trade.getAttrClsCode().equals("19")) { // 这里处理
					// 债券持有到期.
					securityCode = trade.getSecurityCode();
					this.attrClsCode = trade.getAttrClsCode();
				} else {
					securityCode = this.securityCode;
				}
				// ==========================
				if (this.num.trim().length() > 0) {
					buildFeesStr();
					calCost();
					subNum = this.num + YssFun.formatNumber(i, "00000");
					sqlSubNums += subNum + ",";
					pstmt.setString(1, subNum);
					pstmt.setString(2, securityCode); // ======合中保的版本
					pstmt.setString(3, this.portCode);
					pstmt.setString(4,
							(this.brokerCode.length() != 0 ? this.brokerCode
									: " "));
					pstmt.setString(5,
							this.invMgrCode.length() != 0 ? this.invMgrCode
									: " ");
					pstmt.setString(6, this.tradeCode);
					pstmt.setString(7, this.cashAcctCode.length() == 0 ? " "
							: this.cashAcctCode);
					pstmt.setString(8, this.attrClsCode);
					pstmt.setDate(9, YssFun.toSqlDate(YssFun
							.toDate(this.bargainDate)));
					pstmt.setString(10, this.bargainTime);
					pstmt.setDate(11, YssFun.toSqlDate(YssFun
							.toDate(this.settleDate)));
					pstmt.setDate(12, YssFun.toSqlDate(YssFun
							.toDate(this.settleDate)));
					pstmt.setDate(13, YssFun.toSqlDate(YssFun
							.toDate(this.MatureDate))); // 2007.11.16 添加 蒋锦
					pstmt.setDate(14, YssFun.toSqlDate(YssFun
							.toDate(this.MatureSettleDate))); // 添加字段 MatureDate
					// MatureSettleDate
					pstmt.setString(15, this.settleTime);
					pstmt.setString(16, this.autoSettle);
					pstmt.setDouble(17, this.portCuryRate);
					pstmt.setDouble(18, this.baseCuryRate);
					pstmt.setDouble(19, this.allotProportion / 100);
					pstmt.setDouble(20, this.oldAllotAmount);
					pstmt.setDouble(21, this.allotFactor);
					pstmt.setDouble(22, this.tradeAmount);
					pstmt.setDouble(23, this.tradePrice);
					pstmt.setDouble(24, this.tradeMoney);
					pstmt.setDouble(25, this.accruedInterest);
					sFee = this.operSql.buildSaveFeesSql(YssCons.OP_ADD,
							this.fees).split(",");
					for (int j = 0; j < sFee.length; j++) {
						pstmt.setString(26 + j, sFee[j].trim().replaceAll("'",
								""));
					}
					pstmt.setDouble(42, this.totalCost);
					pstmt.setDouble(43, this.cost.getCost());
					pstmt.setDouble(44, this.cost.getMCost());
					pstmt.setDouble(45, this.cost.getVCost());
					pstmt.setDouble(46, this.cost.getBaseCost());
					pstmt.setDouble(47, this.cost.getBaseMCost());
					pstmt.setDouble(48, this.cost.getBaseVCost());
					pstmt.setDouble(49, this.cost.getPortCost());
					pstmt.setDouble(50, this.cost.getPortMCost());
					pstmt.setDouble(51, this.cost.getPortVCost());
					pstmt.setInt(52, 0);
					pstmt.setString(53, this.orderNum);
					pstmt.setDouble(54, this.bailMoney); // alter by sunny
					pstmt.setString(55, this.desc);
					pstmt.setInt(56, 0);
					pstmt.setInt(57, this.checkStateId);
					// (pub.getSysCheckState() ? "0" : "1")
					pstmt.setString(58, this.creatorCode);
					pstmt.setString(59, this.creatorTime);
					pstmt.setString(60, pub.getSysCheckState() ? " "
							: this.creatorCode);
					pstmt.setString(61, this.factCashAccCode == null
							|| this.factCashAccCode.length() == 0 ? " "
							: this.factCashAccCode);// 合并太平版本调整,by leeyu
					// 20100812
					pstmt.setDouble(62, this.factSettleMoney);
					pstmt.setDouble(63, this.exRate);
					pstmt.setString(64, this.settleDesc);
					pstmt.setDouble(65, this.factBaseRate);
					pstmt.setDouble(66, this.factPortRate);
					pstmt.setDate(67, YssFun.toSqlDate(YssFun
							.toDate(this.rateDate))); // edit by jc
					pstmt.setString(68, this.ETFBalaAcctCode);
					pstmt.setDate(69, YssFun.toSqlDate(YssFun
							.toDate(this.ETFBalaSettleDate)));
					pstmt.setDouble(70, this.ETFBalanceMoney);
					pstmt.setDouble(71, this.ETFCashAlternat);
					pstmt.setString(72, this.tradeSeatCode);
					pstmt.setString(73, this.stockholderCode);
					pstmt.setDate(74, YssFun.toSqlDate(YssFun.toDate(this.FBSDate)));//add by huangqirong 2012-05-02 story #2565 联接基金需要用
					pstmt.setDouble(75, this.FCanReturnMoney);//add by huangqirong 2012-05-02 story #2565 联接基金需要用
					pstmt.executeUpdate();

					// 当把数据保存到交易数据子表的时候 要把利息插到应收应付表里面去 BY sunny
					SecRecPayAdmin secPayAdmin = new SecRecPayAdmin();
					secPayAdmin.setYssPub(pub);
					SecPecPayBean secpecpay = new SecPecPayBean();
					secpecpay.setYssPub(pub);
					secpecpay.setTransDate(YssFun.toSqlDate(YssFun
							.toDate(this.bargainDate)));
					secpecpay.setStrPortCode(this.portCode);
					secpecpay.setInvMgrCode(this.invMgrCode);
					secpecpay.setBrokerCode(this.brokerCode);
					secpecpay.setStrSecurityCode(this.securityCode);

					SecurityBean sec = new SecurityBean();
					sec.setYssPub(pub);
					// ==================合中保的版本
					// sec.setSecurityCode(securityCode);
					sec.setSecurityCode(this.securityCode);
					// =======================
					sec.getSetting();

					secpecpay.setStrCuryCode(sec.getTradeCuryCode());
					secpecpay.setMoney(this.accruedInterest);
					secpecpay.setMMoney(this.accruedInterest);
					secpecpay.setVMoney(this.accruedInterest);
					// 利用公有方法重新得到基础货币金额 sunny
					secpecpay.setBaseCuryMoney(this.getSettingOper()
							.calBaseMoney(this.accruedInterest,
									this.baseCuryRate));
					secpecpay.setMBaseCuryMoney(this.getSettingOper()
							.calBaseMoney(this.accruedInterest,
									this.baseCuryRate));
					secpecpay.setVBaseCuryMoney(this.getSettingOper()
							.calBaseMoney(this.accruedInterest,
									this.baseCuryRate));
					secpecpay.setPortCuryMoney(this.getSettingOper()
							.calPortMoney(
									this.accruedInterest,
									this.baseCuryRate,
									this.portCuryRate,
									// linjunyun 2008-11-25 bug:MS00011
									// 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
									this.tradeCode,
									YssFun.parseDate(this.rateDate),
									this.portCode));
					secpecpay.setMPortCuryMoney(this.getSettingOper()
							.calPortMoney(
									this.accruedInterest,
									this.baseCuryRate,
									this.portCuryRate,
									// linjunyun 2008-11-25 bug:MS00011
									// 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
									this.tradeCode,
									YssFun.parseDate(this.rateDate),
									this.portCode));
					secpecpay.setVPortCuryMoney(this.getSettingOper()
							.calPortMoney(
									this.accruedInterest,
									this.baseCuryRate,
									this.portCuryRate,
									// linjunyun 2008-11-25 bug:MS00011
									// 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
									this.tradeCode,
									YssFun.parseDate(this.rateDate),
									this.portCode));
					if (this.tradeCode.equalsIgnoreCase("02")) {
						// secpecpay.setStrTsfTypeCode("07");
						// secpecpay.setStrSubTsfTypeCode("07FI");
						// -----将卖出利息的代码07FI_B调整为02FI_B sj modified 20081229
						// MS00121 ----//
						secpecpay.setStrTsfTypeCode("02");
						secpecpay.setStrSubTsfTypeCode("02FI_B");
						// ----------------------------------------------------------------------//
					} else {
						secpecpay.setStrTsfTypeCode("06");
						secpecpay.setStrSubTsfTypeCode("06FI_B");
					}
					secpecpay.setBaseCuryRate(this.baseCuryRate);
					secpecpay.setPortCuryRate(this.portCuryRate);
					secpecpay.checkStateId = 0; // 审核状态为未审核
					secPayAdmin.addList(secpecpay);
					if (this.tradeCode.equalsIgnoreCase("02")) { // 如果为卖出
						// secPayAdmin.insert(YssFun.toDate(this.bargainDate),
						// "07",
						// "07FI_B", this.portCode, this.invMgrCode,
						// this.brokerCode, this.securityCode, "",
						// 0);
						// -----将卖出利息的代码07FI_B调整为02FI_B sj modified 20081229
						// MS00121 ----//
						secPayAdmin.insert(
								YssFun.toDate(this.bargainDate),
								"02,07", // 07 --> 02.保留07是为了对历史数据的处理
								"02FI_B,07FI_B", this.portCode,
								this.invMgrCode, // 07FI_B -->
								// 02FI_B。保留07FI_B是为了对历史数据的处理
								this.brokerCode, this.securityCode, "", 0);
						// ----------------------------------------------------------------------//
					} else if (this.tradeCode.equalsIgnoreCase("01")) { // 如果为买入
						secPayAdmin.insert(YssFun.toDate(this.bargainDate),
								"06", "06FI_B", this.portCode, this.invMgrCode,
								this.brokerCode, this.securityCode, "", 0);
					}
				}
				// ---------------------------------重新汇总主表数据
				// TradeDataAdmin trade = new TradeDataAdmin(); //新建时不用重新汇总
				// trade.setYssPub(pub);
				// trade.collectToTrade(subNum,securityCode,brokerCode,
				// invMgrCode,tradeCode,
				// bargainDate,true);//boolean值为true时作删除操作
				// ---------------------------------
			}
			if (sqlSubNums.length() > 1) {
				sqlSubNums = sqlSubNums.substring(0, sqlSubNums.length() - 1);
			}

			// strSql = "delete from " + pub.yssGetTableName("tb_cash_transfer")
			// +
			// " where FTradeNum in (" + sqlSubNums + ")";
			// dbl.executeSql(strSql);

			if (!bIsTrans) {
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}

			if (this.compPoint.length() > 0) {
				ICashTransfer operfun = (ICashTransfer) pub.getOperDealCtx()
						.getBean("tradesettle");
				operfun.setYssPub(pub);
				TradeSettleBean para = new TradeSettleBean();
				para.setCompTemp(true);
				para.setNums(sqlSubNums);
				operfun.init(para);
				operfun.cashInTarget();
			}

			return "";
		} catch (Exception e) {
			throw new YssException("保存交易拆分数据出错", e);
		} finally {
			dbl.closeStatementFinal(pstmt);
			if (!bIsTrans) {
				dbl.endTransFinal(conn, bTrans);
			}
		}
	}

	// 合并太平版本代码 批量保存数据的方法
	public int saveAllMutliSetting(String sMutilRowStr) throws YssException {
		try {
			CashAccountBean cashAccountBean = new CashAccountBean();
			StgCash stgCash = new StgCash();
			double dMoney = 0.0;
			double baseMoney = 0.0;
			double portMoney = 0.0;
			String[] reqAry = sMutilRowStr.split("\r\n");
			String brokerCodeStr = reqAry[reqAry.length - 1].toString().split(
					"\t")[0].toString();
			for (int i = 0; i < reqAry.length - 1; i++) {
				TradeSubBean tradeSubBean = new TradeSubBean();
				tradeSubBean.setYssPub(pub);
				StringBuffer str = new StringBuffer();
				// str.append("\r\t[null]\r\t");
				str.append(reqAry[i].toString());
				tradeSubBean.parseRowStr(str.toString());
				SecIntegratedBean secInteData = new SecIntegratedBean();
				SecIntegratedBean secInteFilterType = new SecIntegratedBean();
				secInteData.setYssPub(pub);
				StringBuffer tempBuf = new StringBuffer();
				secInteFilterType.setSTradeTypeCode("81");// 81:内部转货
				secInteFilterType.setSSecurityCode(tradeSubBean
						.getSecurityCode());
				secInteFilterType.setSOperDate(YssFun.formatDate(YssFun
						.toDate(tradeSubBean.getBargainDate()), "yyyy-MM-dd"));
				secInteFilterType.setSPortCode(tradeSubBean.getPortCode());
				// ---------邵宏伟20100126修改 增加所属分类和投资经理、券商
				secInteFilterType.setSAnalysisCode1(tradeSubBean
						.getInvMgrCode());
				secInteFilterType.setAttrClsCode(tradeSubBean.getAttrClsCode());
				secInteFilterType.setSAnalysisCode2(tradeSubBean
						.getBrokerCode());
				secInteFilterType.setInvestType(tradeSubBean.getInvestType());// 添加投资类型
				// by
				// leeyu
				// 20100812
				// 合并太平版本调整
				// ---------邵宏伟20100126修改 增加所属分类和投资经理

				secInteData.setFilterType(secInteFilterType);
				secInteData.getSetting(); // 如果综合业务表中已有记录，得到记录号，在后面SAVE中删除。
				secInteData.setSExchangeDate(YssFun.formatDate(YssFun
						.toDate(tradeSubBean.getBargainDate()), "yyyy-MM-dd"));
				secInteData.setSTradeTypeCode("81");// 81:内部转货
				// ---------邵宏伟20100122修改 增加描述和调拨类型
				String sDesc = "Transfer [" + tradeSubBean.getSecurityCode()
						+ "] Port [" + tradeSubBean.getPortCode()
						+ "] Manager[" + tradeSubBean.getInvMgrCode()
						+ "]  Broker from [" + tradeSubBean.getBrokerCode()
						+ "] to [" + brokerCodeStr + "]";
				secInteData.setDesc(sDesc);
				secInteData.setSTsfTypeCode("05");
				// secInteData.setSSubTsfTypeCode("05FI");
				secInteData.setInvestType(tradeSubBean.getInvestType());// 添加投资类型
				// by
				// leeyu
				// 20100812
				// 合并太平版本调整
				// ---------邵宏伟20100122修改
				String sSubTsfTypeCode = "05"
						+ getSubTsfTypeCode(tradeSubBean.getSecurityCode());
				secInteData.setSSubTsfTypeCode(sSubTsfTypeCode);
				tempBuf.append(secInteData.buildRowStrForParse()).append(
						"\r\t[null]\r\t");
				secInteData.setIInOutType(-1);// 流出
				secInteData.setSSecurityCode(tradeSubBean.getSecurityCode());
				secInteData.setSOperDate(YssFun.formatDate(YssFun
						.toDate(tradeSubBean.getBargainDate()), "yyyy-MM-dd"));
				secInteData.setSPortCode(tradeSubBean.getPortCode());
				secInteData.setSAnalysisCode1(tradeSubBean.getInvMgrCode());
				secInteData.setSAnalysisCode2(tradeSubBean.getBrokerCode());
				secInteData.setSAnalysisCode3(" ");
				secInteData.setDAmount(tradeSubBean.getTradeAmount()
						* secInteData.getIInOutType());
				// --邵宏伟20100127
				// 太平的成本是含费用的，转出成本是实际交收金额，但是债券的实际交收金额是含买入利息的，需要再剔除； start
				dMoney = (tradeSubBean.getTotalCost() - tradeSubBean
						.getAccruedInterest());
				secInteData.setDCost(dMoney * secInteData.getIInOutType());
				secInteData.setDMCost(dMoney * secInteData.getIInOutType());
				secInteData.setDVCost(dMoney * secInteData.getIInOutType());
				// --邵宏伟20100127
				// 太平的成本是含费用的，转出成本是实际交收金额，但是债券的实际交收金额是含买入利息的，需要再剔除； end
				stgCash.setYssPub(pub);
				// --邵宏伟20100127
				// 太平的成本是含费用的，转出成本是实际交收金额，但是债券的实际交收金额是含买入利息的，需要再剔除；
				baseMoney = stgCash.getSettingOper().calBaseMoney(dMoney,
						tradeSubBean.getBaseCuryRate());
				secInteData.setDBaseCost(baseMoney
						* secInteData.getIInOutType());
				secInteData.setDMBaseCost(baseMoney
						* secInteData.getIInOutType());
				secInteData.setDVBaseCost(baseMoney
						* secInteData.getIInOutType());
				cashAccountBean.setYssPub(pub);
				cashAccountBean.setStrCashAcctCode(tradeSubBean
						.getCashAcctCode());
				cashAccountBean.getSetting();
				// --邵宏伟20100127
				// 太平的成本是含费用的，转出成本是实际交收金额，但是债券的实际交收金额是含买入利息的，需要再剔除；
				portMoney = stgCash.getSettingOper().calPortMoney(dMoney,
						tradeSubBean.getBaseCuryRate(),
						tradeSubBean.getPortCuryRate(),
						cashAccountBean.getStrCurrencyCode(),
						YssFun.toDate(tradeSubBean.getBargainDate()),
						tradeSubBean.getPortCode());
				secInteData.setDPortCost(portMoney
						* secInteData.getIInOutType());
				secInteData.setDMPortCost(portMoney
						* secInteData.getIInOutType());
				secInteData.setDVPortCost(portMoney
						* secInteData.getIInOutType());
				secInteData.setDBaseCuryRate(tradeSubBean.getBaseCuryRate());
				secInteData.setDPortCuryRate(tradeSubBean.getPortCuryRate());

				secInteData.checkStateId = tradeSubBean.checkStateId;
				secInteData.creatorCode = tradeSubBean.creatorCode;
				secInteData.creatorTime = tradeSubBean.creatorTime;
				secInteData.checkUserCode = tradeSubBean.checkUserCode;
				secInteData.checkTime = tradeSubBean.checkTime != null ? tradeSubBean.checkTime
						: "";// 防止为空
				secInteData.setAttrClsCode(tradeSubBean.getAttrClsCode());
				tempBuf.append(secInteData.buildRowStrForParse())
						.append("\f\f");// 流出
				// 调整券商
				secInteData.setSAnalysisCode2(brokerCodeStr);
				secInteData.setIInOutType(1);
				secInteData.setDAmount(tradeSubBean.getTradeAmount()
						* secInteData.getIInOutType());
				// --邵宏伟20100127
				// 太平的成本是含费用的，转出成本是实际交收金额，但是债券的实际交收金额是含买入利息的，需要再剔除；
				secInteData.setDCost(dMoney * secInteData.getIInOutType());
				secInteData.setDMCost(dMoney * secInteData.getIInOutType());
				secInteData.setDVCost(dMoney * secInteData.getIInOutType());
				secInteData.setDBaseCost(baseMoney
						* secInteData.getIInOutType());
				secInteData.setDMBaseCost(baseMoney
						* secInteData.getIInOutType());
				secInteData.setDVBaseCost(baseMoney
						* secInteData.getIInOutType());
				secInteData.setDPortCost(portMoney
						* secInteData.getIInOutType());
				secInteData.setDMPortCost(portMoney
						* secInteData.getIInOutType());
				secInteData.setDVPortCost(portMoney
						* secInteData.getIInOutType());

				tempBuf.append(secInteData.buildRowStrForParse())
						.append("\r\t"); // 流入
//				if (tempBuf.toString() != "" && tempBuf.toString().trim().length() > 0) {
				if (!tempBuf.toString().equals("") && tempBuf.toString().trim().length() > 0) {//findbugs风险调整，比较对象的值要采用equals  胡坤 20120625
					tempBuf.append(sSubTsfTypeCode);
					tempBuf.append("\b\t");
					tempBuf.append(String.valueOf(tradeSubBean
							.getAccruedInterest()));
					secInteData.saveMutliSetting(tempBuf.toString(), true);
				}

			}
		} catch (Exception ex) {
			throw new YssException("批量转入出错！", ex);
		}
		return 1;
	}

	// 通过证券代码来获得币种
	public String getCuryCode(String strSecurityCode) throws YssException {
		ResultSet rs = null;
		String curyCode = "";
		try {
			String strSql = "select * from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FSecurityCode=" + dbl.sqlString(strSecurityCode);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				curyCode = rs.getString("FTradeCury"); // 彭鹏 2008.2.18 字段名写错
			}
			return curyCode;
		} catch (Exception e) {
			throw new YssException("得到货币代码出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}
	/**
	 * add by guyichuan  20110426  STORY #562 根据现金帐户代码代码获得现金帐户的币种
	 * @param strCashAcctCode
	 * @return
	 * @throws YssException
	 */
	public String getCashCuryCode(String strCashAcctCode) throws YssException {
		ResultSet rs = null;
		String curyCode = "";
		try {
			String strSql = "select * from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where FCashAccCode=" + dbl.sqlString(strCashAcctCode);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				curyCode = rs.getString("FCuryCode");
			}
			return curyCode;
		} catch (Exception e) {
			throw new YssException("得到货币代码出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}
	/**
	 * getTreeViewData1
	 * 
	 * @return String
	 */
	public String getTreeViewData1() {
		return "";
	}

	/**
	 * getTreeViewData2
	 * 
	 * @return String
	 */
	public String getTreeViewData2() {
		return "";
	}

	/**
	 * getTreeViewData3
	 * 
	 * @return String
	 */
	public String getTreeViewData3() {
		return "";
	}

	/**
	 * buildRowStr
	 * 
	 * @return String
	 */
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		buf.append(this.num).append("\t");
		buf.append(this.securityCode).append("\t");
		buf.append(this.securityName).append("\t");
		buf.append(this.portCode).append("\t");
		buf.append(this.portName).append("\t");
		buf.append(this.brokerCode).append("\t");
		buf.append(this.brokerName).append("\t");
		buf.append(this.invMgrCode).append("\t");
		buf.append(this.invMgrName).append("\t");
		buf.append(this.tradeCode).append("\t");
		buf.append(this.tradeName).append("\t");
		buf.append(this.cashAcctCode).append("\t");
		buf.append(this.cashAcctName).append("\t");
		buf.append(this.attrClsCode).append("\t");
		buf.append(this.attrClsName).append("\t");
		buf.append(this.bargainDate).append("\t");
		buf.append(this.bargainTime).append("\t");
		buf.append(this.settleDate).append("\t");
		buf.append(this.settleTime).append("\t");
		// --------------------2007.11.16 添加 蒋锦 添加字段---------------------//
		buf.append(this.MatureDate).append("\t");
		buf.append(this.MatureSettleDate).append("\t");
		// --------------------------------------------------------------------//
		buf.append(this.autoSettle).append("\t");
		buf.append(this.portCuryRate).append("\t");
		buf.append(this.baseCuryRate).append("\t");
		buf.append(this.handAmount).append("\t");
		buf.append(this.allotProportion).append("\t");
		buf.append(this.oldAllotAmount).append("\t");
		buf.append(this.tradeAmount).append("\t");
		buf.append(this.tradePrice).append("\t");
		buf.append(this.tradeMoney).append("\t");
		buf.append(this.accruedInterest).append("\t");
		buf.append(this.allotFactor).append("\t");
		buf.append(this.totalCost).append("\t");
		buf.append(this.haveAmount).append("\t");
		buf.append(this.orderNum).append("\t");
		buf.append(this.fees).append("\t");
		buf.append(this.desc).append("\t");
		buf.append(this.cost.buildRowStr()).append("\t");
		buf.append(this.bailMoney).append("\t");
		buf.append(this.factCashAccCode).append("\t");
		buf.append(this.factCashAccName).append("\t");
		buf.append(this.factSettleMoney).append("\t");
		buf.append(this.exRate).append("\t");
		buf.append(this.factSettleDate).append("\t");
		buf.append(this.settleDesc).append("\t");
		buf.append(this.factBaseRate).append("\t");
		buf.append(this.factPortRate).append("\t");
		buf.append(this.FactRate).append("\t");
		buf.append(this.rateDate).append("\t"); // edit by jc
		// -----2009.06.22 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》
		// 国内基金业务------//
		buf.append(this.ETFBalaAcctCode).append("\t");
		buf.append(this.ETFBalaSettleDate).append("\t");
		buf.append(this.ETFBalanceMoney).append("\t");
		buf.append(this.ETFCashAlternat).append("\t");
		// ----------------------------------------------------------------------------------------//
		// ------------2009-07-07 蒋锦 添加 股东代码和席位设置--------------//
		// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
		buf.append(this.tradeSeatCode).append("\t");
		buf.append(this.tradeSeatName).append("\t");
		buf.append(this.stockholderCode).append("\t");
		buf.append(this.stockholderName).append("\t");
		// -------------------------------------------------------------//
		// ------ 添加投资类型 add by wangzuochun 2009.07.22 MS00024 交易数据拆分
		// QDV4.1赢时胜（上海）2009年4月20日24_A -------//
		buf.append(this.investType).append("\t");
		// -------------------------------------------------------------------------------------------------------//
		//story 1566 add by zhouwei 20111017 组合延迟交割标示的数值
		buf.append(this.FSecurityDelaySettleState).append("\t");
		buf.append(this.costIsHandEditState).append("\t");
		//add by songjie 2012.4.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B
		buf.append(this.ETFBalaAcctName).append("\t");
		buf.append(this.FBSDate).append("\t");			//add by huangqirong 2012-05-02 story #2565 联接基金需要用
		buf.append(this.FCanReturnMoney).append("\t");	//add by huangqirong 2012-05-02 story #2565 联接基金需要用
		buf.append(this.mtReplaceDate).append("\t");//story 2727 add by zhouwei 20120619 必须现金替代结算日期  ETF链接基金
		buf.append(super.buildRecLog());
		return buf.toString();

	}

	protected void calCost() throws YssException {
		try {
			if (this.tradeCode.equalsIgnoreCase("02")
					|| this.tradeCode.equalsIgnoreCase("17")) { // 兑付
				ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx()
						.getBean("avgcostcalculate");
				costCal.setYssPub(pub);
				// 2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
				// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
				costCal.initCostCalcutate(YssFun.toDate(this.bargainDate),
						this.portCode, this.invMgrCode, this.brokerCode,
						this.attrClsCode);
				this.cost = costCal.getCarryCost(this.securityCode,
						this.tradeAmount, this.num, this.baseCuryRate,
						this.portCuryRate);
				costCal.roundCost(this.cost, 2);
			} else {
				this.cost = new YssCost();
				this.cost.setCost(YssD
						.sub(this.totalCost, this.accruedInterest));
				this.cost.setMCost(YssD.sub(this.totalCost,
						this.accruedInterest));
				this.cost.setVCost(YssD.sub(this.totalCost,
						this.accruedInterest));
				this.cost.setBaseCost(YssD.round(YssD.mul(YssD.sub(
						this.totalCost, this.accruedInterest),
						this.baseCuryRate), 2));
				this.cost.setBaseMCost(YssD.round(YssD.mul(YssD.sub(
						this.totalCost, this.accruedInterest),
						this.baseCuryRate), 2));
				this.cost.setBaseVCost(YssD.round(YssD.mul(YssD.sub(
						this.totalCost, this.accruedInterest),
						this.baseCuryRate), 2));
				if (this.portCuryRate != 0) {
					this.cost.setPortCost(YssD.round(YssD.div(this.cost
							.getBaseCost(), this.portCuryRate), 2));
					this.cost.setPortMCost(YssD.round(YssD.div(this.cost
							.getBaseMCost(), this.portCuryRate), 2));
					this.cost.setPortVCost(YssD.round(YssD.div(this.cost
							.getBaseVCost(), this.portCuryRate), 2));
				} else {
					this.cost.setPortCost(0);
					this.cost.setPortMCost(0);
					this.cost.setPortVCost(0);
				}
			}
		} catch (Exception e) {
			throw new YssException("获取成本信息出错");
		}
	}

	protected void buildFeesStr() throws YssException {
		ArrayList alFeeBeans = null;
		StringBuffer bufAll = new StringBuffer();
		FeeBean fee = null;
		double dblTotalFees;
		double dFeeMoney;
		YssFeeType feeType = null;
		try {
			if (this.tradeMoney > 0 && this.fees.length() == 0) {
				BaseOperDeal baseOper = this.getSettingOper();
				BaseFeeDeal feeOper = (BaseFeeDeal) pub.getOperDealCtx()
						.getBean("feedeal");
				baseOper.setYssPub(pub);
				feeOper.setYssPub(pub);
				feeOper.setFeeAttr(this.securityCode, this.tradeCode,
						this.portCode, this.brokerCode, this.tradeMoney);
				// feeOper.setFeeAttr(this.securityCode, this.tradeType,
				// this.portCode,
				// this.brokerCode, this.sumMoney);
				alFeeBeans = feeOper.getFeeBeans();
				dblTotalFees = 0;
				double settleFees = 0;
				if (alFeeBeans != null) {
					feeType = new YssFeeType();
					feeType.setMoney(this.tradeMoney);
					feeType.setInterest(this.accruedInterest);
					feeType.setAmount(this.tradeAmount);
					feeType.setCost(this.cost.getCost());

					for (int i = 0; i < alFeeBeans.size(); i++) {
						fee = (FeeBean) alFeeBeans.get(i);
						// dFeeMoney =
						// baseOper.calMoneyByPerExp(fee.getPerExpCode(),
						// fee.getRoundCode(), tradeMoney);
						dFeeMoney = baseOper.calFeeMoney(feeType, fee);
						// begin by zhouxiang MS01721 清算金额项为否，交易数据实收金额仍然加了费用
						// 2010.10.23
						if (fee.getIsSettle().equals("0")) {// 为‘0’则不加入清算金额，否则为1计入清算金额

						} else if (fee.getIsSettle().equals("1")) {
							settleFees = YssD.add(settleFees, dFeeMoney);
						}
						// end-- by zhouxiang MS01721 清算金额项为否，交易数据实收金额仍然加了费用
						// 2010.10.23
						dblTotalFees = YssD.add(dblTotalFees, dFeeMoney);
						bufAll.append(fee.getFeeCode()).append(
								YssCons.YSS_ITEMSPLITMARK2);
						bufAll.append(fee.getFeeName()).append(
								YssCons.YSS_ITEMSPLITMARK2);
						bufAll
								.append(
										YssFun.formatNumber(dFeeMoney,
												"###0.##")).append("\f\n");

					}
					if (bufAll.toString().length() > 2) {
						this.fees = bufAll.toString().substring(0,
								bufAll.toString().length() - 2);
						if (this.tradeCode.equalsIgnoreCase("02")) {
							this.totalCost = YssD.sub(tradeMoney, settleFees);// edited
							// by
							// zhouxiang
							// MS01721
							// 清算金额项为否，交易数据实收金额仍然加了费用
							// 2010.10.23
						} else {
							this.totalCost = YssD.add(tradeMoney, settleFees);// edited
							// by
							// zhouxiang
							// MS01721
							// 清算金额项为否，交易数据实收金额仍然加了费用
							// 2010.10.23
						}
						// this.totalCost = YssD.add(totalCost,
						// this.accruedInterest);
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("获取费用列表出错");
		}
	}

	public void loadFees(ResultSet rs) throws SQLException, YssException {
		String sName = "";
		double dFeeMoney = 0;
		double dTotalFee = 0;
		StringBuffer buf = new StringBuffer();
		FeeBean fee = new FeeBean();
		fee.setYssPub(pub);

		for (int i = 1; i <= 8; i++) {
			if (rs.getString("FFeeCode" + i) != null
					&& rs.getString("FFeeCode" + i).trim().length() > 0) {
				fee.setFeeCode(rs.getString("FFeeCode" + i));
				fee.getSetting();
				// ------ add by wangzuochun 2010.09.11 MS01708 交易结算未结算中进行结算时会报错
				// QDV4建行2010年09月08日01_B
				// ------ 根据交易子表中的费用代码去查费用设置中的费用，若费用不存在，则跳过此次循环；
				if (fee.getFeeCode() == null) {
					continue;
				}
				// ----------MS01708-----------//
				sName = fee.getFeeName();
				if (rs.getString("FTradeFee" + i) != null) {
					dFeeMoney = rs.getDouble("FTradeFee" + i);
				}
				dTotalFee = YssD.add(dTotalFee, dFeeMoney);
				buf.append(rs.getString("FFeeCode" + i)).append("\n");
				buf.append(sName).append("\n");
				buf.append(dFeeMoney).append("\n");
				buf.append(fee.buildRowStr().replaceAll("\t", "~")).append(
						"\f\n");
			}
		}
		// if (buf.toString().length() > 2) {
		buf.append("total").append("\n");
		buf.append("Total: ").append("\n");
		buf.append(dTotalFee).append("\n");
		fee.setAccountingWay("0"); // 不计入成本
		buf.append(fee.buildRowStr().replaceAll("\t", "~"));
		this.fees = buf.toString();
		// }
		// else {
		// this.fees = "";
		// }
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException,
			YssException {
		this.num = rs.getString("FNum") + "";
		this.securityCode = rs.getString("FSecurityCode") + "";
		this.securityName = rs.getString("FSecurityName") + "";
		
		this.portName = rs.getString("FPortName") + "";
		this.brokerCode = rs.getString("FBrokerCode") + "";
		this.brokerName = rs.getString("FBrokerName") + "";
		this.invMgrCode = rs.getString("FInvMgrCode") + "";
		this.invMgrName = rs.getString("FInvMgrName") + "";
		this.tradeCode = rs.getString("FTradeTypeCode") + "";
		this.tradeName = rs.getString("FTradeTypeName") + "";
		this.cashAcctCode = rs.getString("FCashAccCode") + "";
		this.cashAcctName = rs.getString("FCashAccName") + "";
		// ------------2009-07-07 蒋锦 添加 股东代码和席位设置--------------//
		// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
		this.tradeSeatCode = rs.getString("fseatCode") + "";
		this.tradeSeatName = rs.getString("FSeatName") + "";
		this.stockholderCode = rs.getString("FStockholderCode") + "";
		this.stockholderName = rs.getString("FStockholderName") + "";
		// --------------------------------------------------------------//
		// ------ add by wangzuochun 2009.07.22 MS00024 交易数据拆分
		// QDV4.1赢时胜（上海）2009年4月20日24_A -------//
		this.investType = rs.getString("FInvestType");
		// --------------------------------------------------------------------------------------------------//

		this.attrClsCode = rs.getString("FAttrClsCode") + "";
		this.attrClsName = rs.getString("FAttrClsName") + "";
		this.bargainDate = rs.getDate("FBargainDate") + "";
		this.bargainTime = rs.getString("FBargainTime") + "";
		this.settleDate = rs.getDate("FSettleDate") + "";
		this.settleTime = rs.getString("FSettleTime") + "";
		// ---------------------2007.11.16 添加 蒋锦 添加字段-----------------------//
		this.MatureDate = rs.getDate("FMatureDate") + "";
		this.MatureSettleDate = rs.getDate("FMatureSettleDate") + "";
		// -----------------------------------------------------------------------//
		
		this.autoSettle = rs.getString("FAutoSettle") + "";
		this.portCuryRate = rs.getDouble("FPortCuryRate");
		this.baseCuryRate = rs.getDouble("FBaseCuryRate");
		this.handAmount = rs.getDouble("FHandAmount");
		this.allotProportion = rs.getDouble("FAllotProportion") * 100;
		this.oldAllotAmount = rs.getDouble("FOldAllotAmount");
		this.allotFactor = rs.getDouble("FAllotFactor");
		this.tradeAmount = rs.getDouble("FTradeAmount");
		this.tradePrice = rs.getDouble("FTradePrice");
		this.tradeMoney = rs.getDouble("FTradeMoney");
		this.accruedInterest = rs.getDouble("FAccruedInterest");
		this.bailMoney = rs.getDouble("FBailMoney"); // alter by sunny
		this.totalCost = rs.getDouble("FTotalCost");
		this.orderNum = rs.getString("FOrderNum");
		loadFees(rs);
		this.desc = rs.getString("FDesc");
		super.setRecLog(rs);
		this.checkStateId = rs.getInt("FCheckState"); // 2007.11.01 修改 蒋锦
		this.factCashAccCode = rs.getString("FFactCashAccCode"); // add liyu
		// 1127 新增
		// 实际结算帐户
		this.factCashAccName = rs.getString("FFactCashAccName");
		this.factSettleMoney = rs.getDouble("FFactSettleMoney");
		this.factSettleDate = rs.getString("FFactSettleDate") == null ? ""
				: YssFun
						.formatDate(rs.getDate("FFactSettleDate"), "yyyy-MM-dd"); // 将日期取出,不要时间
		this.exRate = rs.getDouble("FExRate");
		this.settleDesc = rs.getString("FSettleDesc");
		
		this.FactRate = rs.getDouble("FFactRate"); // 合中保的版本
		// -----------2009.06.22 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》
		// 国内基金业务-------------//
		this.ETFBalaAcctCode = rs.getString("FETFBalaAcctCode");
		//add by songjie 212.04.12 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 获取 FETFBalaAcctName
		this.ETFBalaAcctName = rs.getString("FETFBalaAcctName");
		this.ETFBalaSettleDate = rs.getString("FETFBalaSettleDate") == null ? ""
				: YssFun.formatDate(rs.getDate("FETFBalaSettleDate"),
						"yyyy-MM-dd");
		this.ETFBalanceMoney = rs.getDouble("FETFBalaMoney");
		this.ETFCashAlternat = rs.getDouble("FETFCashAlternat");
		// ------------------------------------------------------------------------------------------------------//
		//add by huangqirong 2012-05-02 story #2565 联接基金需要用   
		if(dbl.isFieldExist(rs, "FBSDATE"))   //add by zhaoxianlin 20120808 BUG 5250 QDV4赢时胜（测试）2012年08月07日02_B
			this.FBSDate = rs.getString("FBSDATE") == null ? "" : YssFun.formatDate(rs.getDate("FBSDATE"), "yyyy-MM-dd");
		if(dbl.isFieldExist(rs, "FCANRETURNMONEY"))  //add by zhaoxianlin 20120808 BUG 5250 QDV4赢时胜（测试）2012年08月07日02_B
			this.FCanReturnMoney = rs.getDouble("FCANRETURNMONEY");
		//---end---
		this.rateDate = rs.getDate("FRateDate") + ""; // edit by jc
		this.portCode = rs.getString("FPortCode") + "";
		setAllCost(rs);
		//--start--STORY #562 by guyichuan 20110426 交易结算后资金调拨中基础汇率为实际结算货币兑基础货币的汇率
		 BaseOperDeal obj = new BaseOperDeal();
		 double dBaseRate=0.0;
		 obj.setYssPub(pub);
		 try{
			 dBaseRate = obj.getCuryRate(rs.getDate("FBargainDate"), this.getCashCuryCode(this.cashAcctCode),
				                                 portCode,
		                                         YssOperCons.YSS_RATE_BASE);//--end--
		 }catch(Exception e){
			 throw new YssException("获取基础汇率信息出错\r\n" + e.getMessage(), e);
		 }
		 //--- add by songjie 2013.07.16 BUG 8616 QDV4海富通2013年07月15日01_B start---//
		 this.factBaseRate = rs.getDouble("FFactBaseRate");
		 //--- add by songjie 2013.07.16 BUG 8616 QDV4海富通2013年07月15日01_B end---//
		 //--- delete by songjie 2013.07.16 BUG 8616 QDV4海富通2013年07月15日01_B start---//
		 //this.factBaseRate = dBaseRate;//STORY #562 by guyichuan 20110426   rs.getDouble("FFactBaseRate")
		 //--- delete by songjie 2013.07.16 BUG 8616 QDV4海富通2013年07月15日01_B end---//
		 this.factPortRate = rs.getDouble("FFactPortRate");
		 //---story 2727 add by zhouwei 20120619 必须现金替代结算日期  ETF链接基金 start---//
		 if(dbl.isFieldExist(rs, "FMtReplaceDate")){
			 this.mtReplaceDate=rs.getDate("FMtReplaceDate")==null?"9998-12-31":YssFun.formatDate(rs.getDate("FMtReplaceDate"));
		 }
		 //---story 2727 add by zhouwei 20120619 必须现金替代结算日期  ETF链接基金 end---//
		super.setRecLog(rs); // lzp add 2007 12.13
	}
	/**
	 * add by zhouwei 20120308
	 * 获取手动修改的成本  
	 * @param rs
	 * @throws SQLException
	 */
	private void setAllCost(ResultSet rs) throws SQLException{
		if(dbl.isFieldExist(rs, "fhandcoststate")){
			this.costIsHandEditState=rs.getString("fhandcoststate");//modified by yeshenghong 20120911 BUG5598
			if("1".equals(this.costIsHandEditState)){
//				this.costIsHandEditState=rs.getString("fhandcoststate");
				cost.setCost(rs.getDouble("FCOST"));
				cost.setMCost(rs.getDouble("FMCOST"));
				cost.setVCost(rs.getDouble("FVCOST"));
				cost.setBaseCost(rs.getDouble("FBASECURYCOST"));
				cost.setBaseMCost(rs.getDouble("FMBASECURYCOST"));
				cost.setBaseVCost(rs.getDouble("FVBASECURYCOST"));
				cost.setPortCost(rs.getDouble("FPORTCURYCOST"));
				cost.setPortMCost(rs.getDouble("FMPORTCURYCOST"));
				cost.setPortVCost(rs.getDouble("FVPORTCURYCOST"));
			}			
		}		
	}
	protected String compOrder(String sPortCodes, String sCompPoint)
			throws YssException {
		String[] sPortCodeAry = sPortCodes.split(",");
		YssCompDeal compParam = new YssCompDeal();
		HashMap hmComp = new HashMap();
		YssCompRep compRep = new YssCompRep();
		CompIndexBean compIndex = new CompIndexBean();
		String strSql = "";
		StringBuffer buf = new StringBuffer();
		ResultSet rs = null;
		String sKey = "";
		String strResult = "";

		try {
			IComplianceDeal comp = (IComplianceDeal) pub.getOperDealCtx()
					.getBean("compliancedeal");
			for (int i = 0; i < sPortCodeAry.length; i++) {
				compParam.setPortCode(sPortCodeAry[i]);
				compParam.setInvMgrCode(this.invMgrCode);
				compParam.setBrokerCode(this.brokerCode);
				compParam.setSecurityCode(this.securityCode);
				compParam.setDDate(YssFun.toDate(bargainDate));
				compParam.setCashAccCode(this.cashAcctCode);
				compParam.setCompPoint(sCompPoint);
				comp.init(compParam);
				comp.setYssPub(pub);
				hmComp.putAll(comp.doCompliance());

				strSql = "select a.*,b.FIndexTempName from "
						+ pub.yssGetTableName("Tb_Comp_Index")
						+ " a "
						+
						// ------------------------------------------------------------
						" left join (select ba.FIndexTempCode,FIndexTempName,ba.FStartDate from "
						+ pub.yssGetTableName("Tb_Comp_IndexTemplate")
						+ " ba "
						+ " join (select FIndexTempCode, Max(FStartDate) as FStartDate from "
						+ pub.yssGetTableName("Tb_Comp_IndexTemplate")
						+ " where FCheckState = 1 group by FIndexTempCode) bb"
						+ " on ba.FIndexTempCode = bb.FIndexTempCode and ba.FStartDate = bb.FStartDate "
						+ ") b on a.FIndexTempCode = b.FIndexTempCode"
						+
						// ------------------------------------------------------------
						" where a.FCheckState = 1 and a."
						+ sCompPoint
						+ " = 1  and a.FIndexTempCode in (select FSubCode from "
						+ pub.yssGetTableName("Tb_Para_Portfolio_Relaship")
						+ " where FRelaType = 'Template' and FCheckState = 1"
						+ " and FPortCode = " + dbl.sqlString(sPortCodeAry[i])
						+ ")";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					sKey = YssFun.formatDate(bargainDate, "yyyyMMdd") + "\f"
							+ sPortCodeAry[i] + "\f"
							+ rs.getString("FIndexTempCode") + "\f"
							+ rs.getString("FIndexCode");
					compIndex = (CompIndexBean) hmComp.get(sKey);
					if (compIndex != null) {
						compRep.setCompResult(compIndex.getBCompWay() + "");
						compRep.setDDate(YssFun.toDate(bargainDate));
						compRep.setPortCode(sPortCodeAry[i]);
						compRep.setTemplateCode(rs.getString("FIndexTempCode")
								+ "");
						compRep.setTemplateName(rs.getString("FIndexTempName")
								+ "");
						compRep.setCompIndexCode(rs.getString("FIndexCode")
								+ "");
						compRep.setCompIndexName(rs.getString("FIndexName")
								+ "");
						buf.append(compRep.buildRowStr()).append(
								YssCons.YSS_LINESPLITMARK);
					}
				}
				dbl.closeResultSetFinal(rs);
			}

			if (buf.toString().length() > 2) {
				strResult = buf.toString().substring(0,
						buf.toString().length() - 2);
			}

			if (strResult.length() == 0) {
				strResult = "true";
			}

			return strResult;
		} catch (Exception e) {
			throw new YssException("获取下单前监控结果出错： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	// ---------------------2008.2.14 彭鹏 BUG0000049
	// 未设置比率公式，则打开费用链接报错----------------------//
	public String getSelectFee() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		ArrayList arr = new ArrayList();
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		FeeBean fee = new FeeBean();
		fee.setYssPub(pub);
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sHeader = "费用名称\t费用金额"; // 标题
		double dFeeMoney;
		double dTotalFee = 0;
		DecimalFormat format = new DecimalFormat("#,##0.##");
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FNum = '" + this.num + "'";
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				for (int i = 1; i <= 8; i++) {
					if (rs.getString("FFeeCode" + i) != null
							&& rs.getString("FFeeCode" + i).trim().length() > 0) {
						fee.setFeeCode(rs.getString("FFeeCode" + i));
						fee.getSetting();
						if (fee != null) {
							arr.add(fee);
						}
						dFeeMoney = rs.getDouble("FTradeFee" + i);
						dTotalFee = YssD.add(dTotalFee, dFeeMoney);
						bufShow.append(fee.getFeeName()).append("\t");
						bufShow.append(format.format(dFeeMoney)).append(
								YssCons.YSS_LINESPLITMARK);
						bufAll.append(fee.getFeeCode()).append(
								YssCons.YSS_ITEMSPLITMARK2);
						bufAll.append(fee.getFeeName()).append(
								YssCons.YSS_ITEMSPLITMARK2);
						bufAll
								.append(
										YssFun.formatNumber(dFeeMoney,
												"###0.##")).append(
										YssCons.YSS_ITEMSPLITMARK2);
						bufAll.append(fee.buildRowStr()).append(
								YssCons.YSS_LINESPLITMARK);
					}
				}
			}
			if (arr.size() != 0) {
				bufShow.append("Total: ").append("\t");
				bufShow.append(format.format(dTotalFee)).append(
						YssCons.YSS_LINESPLITMARK);
				bufAll.append("total").append(YssCons.YSS_ITEMSPLITMARK2);
				bufAll.append("Total: ").append(YssCons.YSS_ITEMSPLITMARK2);
				bufAll.append(YssFun.formatNumber(dTotalFee, "###0.##"))
						.append(YssCons.YSS_ITEMSPLITMARK2);
				bufAll.append("").append(YssCons.YSS_LINESPLITMARK);
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}
			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	// ------------------------------------------------------------------------//

	/**
	 * getOperValue
	 * 
	 * @param sType
	 *            String
	 * @return String
	 */
	public String getOperValue(String sType) throws YssException {
		// MS01422 add by zhangfa 20100807 QDV411建行2010年07月08日01_A
		if (sType.indexOf("getFactSettleDate") != -1) {
			String[] temp = sType.split("/t");
			return this.getFactSettleDate(temp[1]);
		}
		// -----------------------------------------------------------------
		// ---------------------2008.2.14 彭鹏 BUG0000049
		// 未设置比率公式，则打开费用链接报错----------------------//
		if (sType.equalsIgnoreCase("getSelect")) {
			return this.getSelectFee();
		}
		// ------------------------------------------------------------------------//
		// ----------------------2008.3.30 彭鹏 BUG0000132
		// 添加批量审核功能------------------------//
		if (sType.equalsIgnoreCase("checkTradeSub")) {
			return this.getCheckTradeSub(this.multAuditString);
		}
		// -------------------------------------------------------------------------------------//
		// -------------------------彭鹏 20080328 新增批量审核功能----------------------//
		if (sType.equalsIgnoreCase("multauditTradeSub")) {
			if (multAuditString.length() > 0) {
				return this.auditMutli(this.multAuditString); // 执行批量审核/反审核
			}
		}
		// ------------------------------------------------------------------------//
		// ---------------------2008.2.18 彭鹏 BUG0000048
		// 业务数据界面控制问题----------------------//
		if (sType.equalsIgnoreCase("checkTradeCuryCode")) {
			return this.getCuryCode(this.securityCode);
		}
		// ------------------------------------------------------------------------//
		//-- by guyichuan 20110426  STORY #562 根据现金帐户代码获得现金帐户的币种
		if(sType.equalsIgnoreCase("checkCashCuryCode")){
			return this.getCashCuryCode(this.cashAcctCode);
		}
		if (sType.equalsIgnoreCase("HaveAmount")) {
			ITradeOperation tradeOper = (ITradeOperation) pub.getOperDealCtx()
					.getBean("tradedeal");
			tradeOper.setYssPub(pub);
			tradeOper.initTradeOperation(this.securityCode, this.brokerCode,
					this.invMgrCode, this.tradeCode, this.orderNum, YssFun
							.toDate(this.bargainDate), this.subType);
			((BaseTradeDeal) tradeOper).setBaseMoneyType(false);// 设置一下获取金额类型，这里按原币金额计算
			// by leeyu
			// 20090727
			// QDV4中保2009年07月27日04_B
			// MS00586
			((BaseTradeDeal) tradeOper).setCashAcctCode(this.cashAcctCode);// 设置一下现金帐户，这是取此现金帐户下的可用余额
			// by
			// leeyu
			// 20090727
			// QDV4中保2009年07月27日04_B
			// MS00586
			this.haveAmount = tradeOper.getHave(this.portCode);

		}
		// ========2009-1-5 MS00125 取单支证券下当日所有可用数量，此时组合代码为*
		if (sType.equalsIgnoreCase("HaveAllPortAmount")) {
			ITradeOperation tradeOper = (ITradeOperation) pub.getOperDealCtx()
					.getBean("tradedeal");
			tradeOper.setYssPub(pub);
			if (!this.portCode.equalsIgnoreCase("*allport*")) {
				this.portCode = operSql.sqlCodes(this.portCode);
			}
			tradeOper.initTradeOperation(this.securityCode, this.brokerCode,
					this.invMgrCode, this.tradeCode, this.orderNum, YssFun
							.toDate(this.bargainDate), this.subType,
					this.portCode);

			this.haveAmount = tradeOper.getHave(this.portCode);
			this.tradeAmount = tradeOper.getSecurityFreezeAmount(); // 暂时用这个字段传证券的冻结数量
			// MS00125
		}
		// ====2009-1-5
		if (sType.equalsIgnoreCase("CompOrder")) {
			return this.compOrder(this.portCode, this.compPoint);
		}
		// -------------2007.11.16 添加 蒋锦---得到回购期限----------------//
		if (sType.equalsIgnoreCase("GetDurability")) {
			return this.getDurability();
		}
		// --------------------------------------------------------------//
		if (sType.equalsIgnoreCase("DeleteItem")) {
			Connection conn = dbl.loadConnection();
			boolean bTrans = false;
			String strSql = "";
			try {
				conn.setAutoCommit(false);
				bTrans = true;

				strSql = "delete from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FCheckState = 3 and FCreator = "
						+ dbl.sqlString(pub.getUserCode());
				dbl.executeSql(strSql);

				strSql = "delete from "
						+ pub.yssGetTableName("Tb_Cash_Transfer")
						+ " where FCheckState = 3 and FCreator = "
						+ dbl.sqlString(pub.getUserCode());
				dbl.executeSql(strSql);

				strSql = "delete from "
						+ pub.yssGetTableName("Tb_Cash_SubTransfer")
						+ " where FCheckState = 3 and FCreator = "
						+ dbl.sqlString(pub.getUserCode());
				dbl.executeSql(strSql);

				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			} catch (Exception e) {
				throw new YssException("删除交易拆分数据信息出错", e);
			} finally {
				dbl.endTransFinal(conn, bTrans);
			}
		}
		if (sType.equalsIgnoreCase("TradeAmount")) {
			ITradeOperation tradeOper = (ITradeOperation) pub.getOperDealCtx()
					.getBean("tradedeal");
			tradeOper.setYssPub(pub);
			tradeOper.initTradeOperation(this.securityCode, this.brokerCode,
					this.invMgrCode, this.tradeCode, this.orderNum, YssFun
							.toDate(this.bargainDate), this.subType);

			this.tradeAmount = tradeOper.getTailWipedDistAmount(this.portCode,
					this.tailPortCode, this.tradeAmount, this.oldAllotAmount,
					this.tradePrice);
		}
		if (sType.equalsIgnoreCase("PortCuryRate")) {
			this.portCuryRate = this.getSettingOper()
					.getCuryRate(
							YssFun.toDate(this.bargainDate),
							(this.bargainTime == null) ? "" : this.bargainTime
									.trim(), this.portCuryCode, this.portCode,
							YssOperCons.YSS_RATE_PORT);
			this.portCuryRate = YssD.round(this.portCuryRate, 15); // 把汇率保留15位
			// 茶仕春
			// 20080130
		}
		if (sType.equalsIgnoreCase("beforesub")) {
			// 拆分前显示投资组合、持有股数/现金等相关信息
			return getInfoBeforeSub();
		}
		// ----------获取以前的成本---------2008.4.7------单亮------
		if (sType.equalsIgnoreCase("calculateOldCost")) {
			// if
			// (this.tradeCode.equalsIgnoreCase("02")||this.tradeCode.equalsIgnoreCase("17")||this.tradeCode.equalsIgnoreCase("21"))
			// {
			ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx()
					.getBean("avgcostcalculate");
			costCal.setYssPub(pub);
			// 2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
			// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
			costCal.initCostCalcutate(YssFun.toDate(this.bargainDate),
					this.portCode, this.invMgrCode, this.brokerCode,
					this.attrClsCode);
			this.cost = costCal.getCarryOldCost(this.securityCode,
					this.tradeAmount, this.num, this.baseCuryRate,
					this.portCuryRate);
			costCal.roundCost(this.cost, 2);
			// }
			return this.cost.buildRowStr();
		}
		// ------------------xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015
		// 国内权益处理--------------------------//
		if (sType.equalsIgnoreCase("rightCashConsideration")) { // 权益处理-现金对价，浏览成本是直接从交易关联表中获取数据
			StringBuffer buff = null; // 定义一个字符串用来来保存sql语句
			ResultSet rs = null; // 用来保存sql语句查出的结果集
			try {
				if (this.tradeCode.equalsIgnoreCase("85")
						|| this.tradeCode.equalsIgnoreCase("86") ||
						//edit by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A
						this.tradeCode.equalsIgnoreCase("07")// 交易类型为：现金对价‘85’，现金对价到账‘86’
				) {
					buff = new StringBuffer();
					buff.append(" select * from ");
					buff.append(pub.yssGetTableName("tb_data_traderela"));
					buff.append(" where FNum =")
							.append(dbl.sqlString(this.num));

					rs = dbl.openResultSet(buff.toString());
					buff.delete(0, buff.length());
					if (rs.next()) {
						// 以下为核算成本，管理成本，估值成本的原币、基础货币、组合货币赋值
						this.cost = new YssCost();
						this.cost.setCost(rs.getDouble("FCost"));
						this.cost.setMCost(rs.getDouble("FMCost"));
						this.cost.setVCost(rs.getDouble("FVCost"));
						this.cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
						this.cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));
						this.cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));
						this.cost.setPortCost(rs.getDouble("FPortCuryCost"));
						this.cost.setPortMCost(rs.getDouble("FMPortCuryCost"));
						this.cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
					} else {
						this.cost = new YssCost();
						this.cost.setCost(0);
						this.cost.setMCost(0);
						this.cost.setVCost(0);
						this.cost.setBaseCost(0);
						this.cost.setBaseMCost(0);
						this.cost.setBaseVCost(0);
						this.cost.setPortCost(0);
						this.cost.setPortMCost(0);
						this.cost.setPortVCost(0);
					}
				}
				return this.cost.buildRowStr();
			} catch (Exception e) {
				throw new YssException("获取交易关联表中成本数据出错！", e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}
		// --------------------------------------end-------------------------------------//
		/**shashijie 2012-5-7 STORY 2565 ETF联接基金*/
		if (sType.equalsIgnoreCase("getCostETF")) {
			if (this.tradeCode.equals(YssOperCons.YSS_JYLX_ETFSGou)
				|| this.tradeCode.equals(YssOperCons.YSS_JYLX_ETFSH)
				|| this.tradeCode.equals(YssOperCons.YSS_JYLX_ETFLJSGSB)
				|| this.tradeCode.equals(YssOperCons.YSS_JYLX_ETFLJSHSB)
				//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 start---//
				|| this.tradeCode.equals(YssOperCons.YSS_JYLX_ETFSGTK)
				|| this.tradeCode.equals(YssOperCons.YSS_JYLX_ETFSGBK)
				//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 end---//
				|| this.tradeCode.equals(YssOperCons.YSS_JYLX_ETFSHTBK)
				) {
				//赋值
				setCostValue();
				return this.cost.buildRowStr();
			}
		}
		/**end*/
		if (sType.equalsIgnoreCase("calculateCost")) {
			// 计算所有成本
			if (this.tradeCode.equalsIgnoreCase("02")
					|| this.tradeCode.equalsIgnoreCase("17")
					|| this.tradeCode.equalsIgnoreCase("21")) {
				ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx()
						.getBean("avgcostcalculate");
				costCal.setYssPub(pub);
				// 2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
				// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
				costCal.initCostCalcutate(YssFun.toDate(this.bargainDate),
						this.portCode, this.invMgrCode, this.brokerCode,
						this.attrClsCode);
				this.cost = costCal.getCarryCost(this.securityCode,
						this.tradeAmount, this.num, this.baseCuryRate,
						this.portCuryRate);
				costCal.roundCost(this.cost, 2);
			} else {
				this.cost = new YssCost();
				// 成本里面要把利息给减掉
				this.totalCost = this.totalCost - this.accruedInterest;
				this.cost.setCost(this.totalCost);
				this.cost.setMCost(this.totalCost);
				this.cost.setVCost(this.totalCost);
				this.cost.setBaseCost(YssD.round(YssD.mul(this.totalCost,
						this.baseCuryRate), 2));
				this.cost.setBaseMCost(YssD.round(YssD.mul(this.totalCost,
						this.baseCuryRate), 2));
				this.cost.setBaseVCost(YssD.round(YssD.mul(this.totalCost,
						this.baseCuryRate), 2));
				if (this.portCuryRate != 0) {
					this.cost.setPortCost(YssD.round(YssD.div(this.cost
							.getBaseCost(), this.portCuryRate), 2));
					this.cost.setPortMCost(YssD.round(YssD.div(this.cost
							.getBaseMCost(), this.portCuryRate), 2));
					this.cost.setPortVCost(YssD.round(YssD.div(this.cost
							.getBaseVCost(), this.portCuryRate), 2));
				} else {
					this.cost.setPortCost(0);
					this.cost.setPortMCost(0);
					this.cost.setPortVCost(0);
				}
			}
			return this.cost.buildRowStr();
		}

		// ------- add by wangzuochun 2009.08.04 MS00024 交易数据拆分
		// QDV4.1赢时胜（上海）2009年4月20日24_A --------//
		// ------ 检测前台所要拆分的数据是否是拆分后的数据，如果为拆分后的数据，则返回字符串true，否则返回字符串false
		// ------//
		if (sType.equalsIgnoreCase("checkSplitData")) {
			String strSql = "";
			ResultSet rs = null;
			try {
				strSql = "select FSplitNum from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FNum = '" + this.num + "'";
				rs = dbl.openResultSet(strSql); // 执行sql语句查出内容

				if (rs.next()) {
					if (rs.getString("FSplitNum") == null) {
						return "false";
					} else {
						if (rs.getString("FSplitNum").indexOf("T") == 0) {
							return "true";
						} else {
							return "false";
						}
					}
				}
			} catch (Exception e) {
				throw new YssException("检测前台所要拆分的数据是否是拆分后数据出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}
		// -----------------------------------------END
		// MS00024-----------------------------------------------//

		// / <summary>
		// / 修改人：fanghaoln
		// / 修改时间:20090401
		// / BugNO : MS00009 数据完整性控制 QDV4.1赢时胜上海2009年2月1日08_A
		// / 增加一个数据完整性提示功能
		// / </summary>
		if (sType.equalsIgnoreCase("beforeUnAudit")) { // 前台用于判断数据完整性
			String strSql = ""; // 定义一个字符串用来来保存sql语句
			ResultSet rs = null; // 用来保存sql语句查出的结果集
			try {
				strSql = "select FSettleState from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ // 查出交易数据子表里的内容
						" where FNum like '" + this.num
						+ "%' and FSettleState = 1 "; // 查询条件为结算状态,因为审核状态也要数据完整性所以不加审核状态条件做为查询条件
				rs = dbl.openResultSet(strSql); // 执行sql语句查出内容
				if (rs.next()) { // 如果这有结果说明已结算，不能执行反审核操作
					return "1"; // 向前台传1作为标识符
				} else {
					return "0"; // 在未结算状态，还有可能就是录入状态为空传前台一个标志。防止出错
				}
			} catch (Exception e) {
				throw new YssException("获取交易拆分数据出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}

		// / <summary>
		// / 修改人：panjunfang
		// / 修改时间:20090914
		// / BugNO : MS00009 数据完整性控制 QDV4.1赢时胜上海2009年2月1日08_A
		// / 用来对前台的批量审核反审核时判断数据完整性，并把不能反审核与审核的结果反回到前台
		// / </summary>
		if (sType.equalsIgnoreCase("beforeBatchUnAudit")) {
			String strSql = "";
			ResultSet rs = null;
			StringBuffer sbReturn = new StringBuffer();
			try {
				String[] strTmp = this.num.split("~n~");
				for (int i = 0; i < strTmp.length; i++) {
					strSql = "select FNum,FSettleState from "
							+ pub.yssGetTableName("Tb_Data_SubTrade")
							+ // 查出交易数据子表里的内容
							" where FNum like '" + strTmp[i]
							+ "%' and FSettleState = 1 "; // 查询条件为结算状态,因为审核状态也要数据完整性所以不加审核状态条件做为查询条件
					rs = dbl.openResultSet(strSql);
					//                 
					while (rs.next()) {
						sbReturn.append(rs.getString("FNum")).append(",");
					}
					dbl.closeResultSetFinal(rs); // 释放资源 add by nimengjing
													// 2011.2.16
				}
				if (sbReturn.toString().length() > 1) {
					return sbReturn.toString().substring(0,
							sbReturn.toString().length() - 1);// 将已审核的交易编号返回前台
				} else {
					return "";
				}
			} catch (Exception e) {
				throw new YssException("获取交易拆分数据出错", e);
			} finally {
				dbl.closeResultSetFinal(rs); // 释放资源
			}
		}

		// / <summary>
		// / 修改人：fanghaoln
		// / 修改时间:20090416
		// / BugNO : MS00009 数据完整性控制 QDV4.1赢时胜上海2009年2月1日08_A
		// / 增加一个数据完整性提示功能
		// /用来处理前台资金调拨的数据完整性提示
		// / </summary>
		if (sType.equalsIgnoreCase("mannyCheckData")) { // 前台用于判断数据完整性
			String strSql = ""; // 定义一个保存sql语句的字符串
			ResultSet rs = null; // 定义一个结果集用来保存sql查出的结果
			boolean isHave = false; // 这里用来判断净值表和估值表里面是否已结算
			try {
				String[] datas = this.settleDate.split("~n~"); // 把前台传来的日期分解出来进行判断
				for (int i = 0; i < datas.length; i++) { // 循环遍历出资金调拨里的业务日期
					strSql = "select FAcctCode from "
							+ pub.yssGetTableName("Tb_Rep_Guessvalue") + // 估值表里的数据
							" where fdate = " + dbl.sqlDate(datas[i]) + // 这里传来业务日期有两个，要循环遍历这两个日期
							// fanghaoln 20090619 MS00522
							// QDV4赢时胜（上海）2009年6月18日01_B 条件没有限制死，查出了错误数据
							" and facctlevel = '1' and facctcode='C100' "; // 查询条件在业务日期下估值表是否为结算状态
					rs = dbl.openResultSet(strSql);
					if (rs.next()) { // 如果这有结果说明已结算，不能执行新建操作
						isHave = true;
					} else {
						strSql = "select FKeyCode from "
								+ pub.yssGetTableName("Tb_Data_Navdata") + // 净值表里的数据
								" where fnavdate = " + dbl.sqlDate(datas[i]) + // 这里传来业务日期有两个，要循环遍历这两个日期
								" and FKeyCode = 'confirm' "; // 查询条件在业务日期下净值表是否为结算状态
						rs = dbl.openResultSet(strSql);
						if (rs.next()) { // 如果这有结果说明已结算，不能执行新建操作
							isHave = true;
						}
					}
				}
				if (isHave) { // 如果净值表和估值表里面已结算
					return "1"; // 向前台返回1
				} else {
					// 如果都没有结算的情况，查看是手工还是自动
					strSql = "select FDataSource from " + // 通过资金调拨表查出录入状态
							pub.yssGetTableName("Tb_Cash_Transfer") + // 资金调拨表
							" where FNum = '" + this.num + "'"; // 查询条件通过前台传来的交易编号
					rs = dbl.openResultSet(strSql);
					if (rs.next()) { // 分析查出的结果来判断是否是手工录入的还是生成的
						if (rs.getInt("FDataSource") == 0) { // 如果查出是手工,这里判断不直接返回是因为前人使用了0，1字符传前台，而手工自动对应的就是0，1。
							return "manual"; // 传给前台一个手工的标志
						} else {
							return "auto"; // 是自动传一个自动的标志
						}
					} else {
						return "0"; // 在未结算状态，还有可能就是录入状态为空传前台一个标志。防止出错
					}
				}
			} catch (Exception e) {
				throw new YssException("获取交易拆分数据出错", e);
			} finally {
				dbl.closeResultSetFinal(rs); // 释放资源
			}
		}

		/**
		 * xuxuming 20090807 MS00543:QDV4赢时胜（上海）2009年6月22日01_AB
		 * 根据前台传来的业务日期判断能否进行操作；如果对应日期在净值表中和估值表中已经确认了，则不能修改
		 * 接受前台传来的单个数据，但有多个日期要判断的情况
		 */
		if (sType.equalsIgnoreCase("OperDateChechData")) {
			// fanghaoln 20091225 MS00768 QDV4赢时胜（上海）2009年10月28日05_B 跨组合群数据完整性提示
			String[] allGroupPortCode = this.portCode
					.split(YssCons.YSS_GROUPSPLITMARK);
			String GroupCode = pub.getPrefixTB();
			if (allGroupPortCode.length > 1) {
				this.portCode = allGroupPortCode[0];
				pub.setPrefixTB(allGroupPortCode[1]);
			}
			String reslts = checkGuessNavStat();
			pub.setPrefixTB(GroupCode);
			return reslts;
			// ------------------------end
			// MS00768-----------------------------------------------------
		}
		// / <summary>
		// / 修改人：fanghaoln
		// / 修改时间:20090401
		// / BugNO : MS00009 数据完整性控制 QDV4.1赢时胜上海2009年2月1日08_A
		// / 增加一个数据完整性提示功能
		// /接受前台传来的单个数据，但有多个日期要判断的情况
		// / </summary>
		if (sType.equalsIgnoreCase("pubCheckData")) { // 前台用于判断数据完整性
			String strSql = "";
			ResultSet rs = null;
			try {
				String[] datas = this.settleDate.split("~n~"); // 把前台传来的日期分解出来进行判断
				for (int i = 0; i < datas.length; i++) { // 循环遍历前台传来的多个日期
					strSql = "select FAcctCode from "
							+ pub.yssGetTableName("Tb_Rep_Guessvalue")
							+ " where fdate = " + dbl.sqlDate(datas[i]) +
							// fanghaoln 20090619 MS00522
							// QDV4赢时胜（上海）2009年6月18日01_B 条件没有限制死，查出了错误数据
							" and facctlevel = '1' and facctcode='C100' "; // 查询条件在业务日期下估值表是否为结算状态
					rs = dbl.openResultSet(strSql);
					if (rs.next()) { // 如果这有结果说明已结算，不能执行新建操作
						return "1"; // 向前台传标号1
					} else {
						strSql = "select FKeyCode from "
								+ pub.yssGetTableName("Tb_Data_Navdata")
								+ " where fnavdate = " + dbl.sqlDate(datas[i])
								+ " and FKeyCode = 'confirm' "; // 查询条件在业务日期下净值表是否为结算状态
						rs = dbl.openResultSet(strSql);
						if (rs.next()) { // 如果这有结果说明已结算，不能执行新建操作
							return "1";
						}
					}
				}
				return "0"; // 在未结算状态，还有可能就是录入状态为空传前台一个标志。防止出错
			} catch (Exception e) {
				throw new YssException("获取交易拆分数据出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}

		// / <summary>
		// / 修改人：fanghaoln
		// / 修改时间:20090401
		// / BugNO : MS00009 数据完整性控制 QDV4.1赢时胜上海2009年2月1日08_A
		// / 增加一个数据完整性提示功能
		// /用来对前台的批量审核反审核时判断数据完整性，并把不能反审核与审核的结果反回到前台
		// / </summary>
		if (sType.equalsIgnoreCase("pubCheckDatas")) {
			String strSql = ""; // 定义一个字符串来保存sql语句
			String sback = ""; // 保存各个条目的交易编号，返回到前台用于区分
			ResultSet rs = null; // 保存sql语句查询出来的结果集.
			boolean isHave = false; // 由于有多个条目，用一个标致来判断净值表和估值表是否已结算，只要估值表和净值表一个已结算就为true
			try {
				String[] nums = this.num.split("~l~"); // 把前台传来的各条目解析出来
				String[] datas = this.settleDate.split("~l~"); // 解析出条目
				for (int i = 0; i < nums.length; i++) { // 遍历每一个条目
					String[] data = datas[i].split("~n~"); // 把每一个条目里的日期解析出来。
					for (int j = 0; j < data.length; j++) { // 这里分解出前台传来的多个日期的情况,业务资料就是这种情况
						strSql = "select FAcctCode from "
								+ pub.yssGetTableName("Tb_Rep_Guessvalue") + // 估值表的表名
								" where fdate = " + dbl.sqlDate(data[j]) +
								// fanghaoln 20090619 MS00522
								// QDV4赢时胜（上海）2009年6月18日01_B 条件没有限制死，查出了错误数据
								" and facctlevel = '1' and facctcode='C100' "; // 查询条件在业务日期下估值表是否为结算状态
						rs = dbl.openResultSet(strSql);
						if (rs.next()) { // 如果查到数据说明估值表已结算
							isHave = true; // 如果查询出结果来表明在当前日期下估值已产生
						}
						strSql = "select FKeyCode from "
								+ pub.yssGetTableName("Tb_Data_Navdata")
								+ // 新的净值表的表名
								" where fnavdate = " + dbl.sqlDate(data[j])
								+ " and FKeyCode = 'confirm' "; // 查询条件在业务日期下净值表是否为结算状态
						rs = dbl.openResultSet(strSql);
						if (rs.next()) { // 如果查到数据说明净值表已结算
							isHave = true;
						}
					}
					if (isHave) { // 只要估值表和净值表有一个已产生就结出数据完整性提示。
						sback = sback + nums[i] + "\r\n"; // 传到前台用来做系统提示
					}
				}
				return sback;
			} catch (Exception e) {
				throw new YssException("获取交易拆分数据出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}
		// / <summary>
		// / 修改人：fanghaoln
		// / 修改时间:20090401
		// / BugNO : MS00009 数据完整性控制 QDV4.1赢时胜上海2009年2月1日08_A
		// / 增加一个数据完整性提示功能.在日终处理里面如果已结算，则给出数据完整性提示
		// / </summary>
		if (sType.equalsIgnoreCase("checkBetweenData")) {
			String strSql = ""; // 定义一字符串来保存sql语句
			ResultSet rs = null; // 定义一个结果集来保存sql查询出来的结果
			String GroupCode = pub.getPrefixTB();// fanghaoln 20091225 MS00768
			// QDV4赢时胜（上海）2009年10月28日05_B
			// 跨组合群数据完整性提示
			try {
				// fanghaoln 20091225 MS00768 QDV4赢时胜（上海）2009年10月28日05_B
				// 跨组合群数据完整性提示
				String[] allGroupPortCode = this.portCode
						.split(YssCons.YSS_GROUPSPLITMARK);
				if (allGroupPortCode.length > 1) {
					this.portCode = allGroupPortCode[0];
					pub.setPrefixTB(allGroupPortCode[1]);
				}
				// ------------------------end
				// MS00768-----------------------------------------------------
				String[] datas = this.settleDate.split("~n~"); // 把前台传来的业务日期分解出来
				if (datas.length > 1) {
					strSql = "select FKeyCode from "
							+ pub.yssGetTableName("Tb_Data_Navdata") + // 对应的净值表
							" where fnavdate between " + // 前台传来的业务日期有两是一个区间的形式。所以用between
							// and的形式查出这个区间内的数据
							dbl.sqlDate(datas[0]) + // 把第一个日期的字符串形式转前日期形式
							" and " + dbl.sqlDate(datas[1]) + // 把后一个日期的字符串形式转换为日期形式
							" and FKeyCode = 'confirm' "+ // 查询条件在业务日期下净值表是否为结算状态
							/**shashijie 2012-6-13 BUG 4758 多组合的情况下，另一组合无法进行现金计息，两费计提的操作*/
							" And FPortCode = "+dbl.sqlString(this.portCode)+
							/**end*/
							" ";
					rs = dbl.openResultSet(strSql); // 执行sql语句放到定义的结果集里面去
					if (rs.next()) { // 如果这有结果说明已结算，不能执行反审核操作
						return "1"; // 如果查出结算就给前台传一个1作为标识说明在当前日期已经产生了净值表了
					} else {
						return "0"; // 在未结算状态，还有可能就是录入状态为空传前台一个标志。防止出错
					}
				}
			} catch (Exception e) {
				throw new YssException("获取交易拆分数据出错", e);
			} finally {
				pub.setPrefixTB(GroupCode);// fanghaoln 20091225 MS00768
				// QDV4赢时胜（上海）2009年10月28日05_B
				// 跨组合群数据完整性提示
				dbl.closeResultSetFinal(rs);
			}
		}
		if (sType.equalsIgnoreCase("getTradeSubInfo")) {
			String strSql = "";
			ResultSet rs = null;
			StringBuffer buf = new StringBuffer();
			try {
				strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
						+ " d.FPortName, e.FSecurityName, e.FHandAmount, f.FInvMgrName, g.FTradeTypeName, "
						+ " h.FBrokerName as FBrokerName, o.FCashAccName, p.FAttrClsName,oo.FCashAccName as FFactCashAccName,e.FFactRate,ts.fseatname, sh.fstockholdername "
						//edit by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName
						+ " ,ooo.Fcashaccname as FETFBalaAcctName from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " a "
						+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
						+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
						+
						// ----------------------------------------------------------------------------------------------------
						// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
						
					/*	" left join (select db.* from (select FPortCode, max(FStartDate) as FStartDate from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						+ " where FStartDate <= "
						+ dbl.sqlDate(new java.util.Date())
						+ " and FCheckState = 1 group by FPortCode) da join (select FPortCode, FPortName, FStartDate from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						+ ") db on da.FPortCode = db.FPortCode and da.FStartDate = db.FStartDate) d on a.FPortCode = d.FPortCode "
						+*/
						" left join (select FPortCode, FPortName from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						+ " where  FCheckState = 1) d on a.FPortCode = d.FPortCode "
						+
						
						// ----------------------------------------------------------------------------------------------------
						" left join (select eb.*,case when ec.FFactRate is null then 0 else ec.FFactRate end as FFactRate from (select FSecurityCode, max(FStartDate) as FStartDate from "
						+ // 合中保的版本
						pub.yssGetTableName("Tb_Para_Security")
						+ " where FStartDate <= "
						+ dbl.sqlDate(new java.util.Date())
						+ " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount from "
						+ pub.yssGetTableName("Tb_Para_Security")
						+ ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate "
						+ " left join (select FSecurityCode,FFactRate from "
						+ pub.yssGetTableName("tb_para_fixinterest")
						+ " )ec on ea.FSecurityCode=ec.FSecurityCode "
						+ ") e on a.FSecurityCode = e.FSecurityCode "
						+ // 合中保的版本
						// ----------------------------------------------------------------------------------------------------
						// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
						
						/*" left join (select fb.* from (select FInvMgrCode, max(FStartDate) as FStartDate from "
						+ pub.yssGetTableName("Tb_Para_InvestManager")
						+ " where FStartDate <= "
						+ dbl.sqlDate(new java.util.Date())
						+ " and FCheckState = 1 group by FInvMgrCode) fa join (select FInvMgrCode, FInvMgrName, FStartDate from "
						+ pub.yssGetTableName("Tb_Para_InvestManager")
						+ ") fb on fa.FInvMgrCode = fb.FInvMgrCode and fa.FStartDate = fb.FStartDate) f on a.FInvMgrCode = f.FInvMgrCode "
						+*/
						" left join (select FInvMgrCode, FInvMgrName from "
						+ pub.yssGetTableName("Tb_Para_InvestManager")
						+ " where FCheckState = 1 ) f on a.FInvMgrCode = f.FInvMgrCode "
						+
						//end by lidaolong 
						// ----------------------------------------------------------------------------------------------------
						" left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode"
						+
						// ----------------------------------------------------------------------------------------------------
						// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
						/*" left join (select hb.* from (select FBrokerCode, max(FStartDate) as FStartDate from "
						+ pub.yssGetTableName("Tb_Para_Broker")
						+ " where FStartDate <= "
						+ dbl.sqlDate(new java.util.Date())
						+ " and FCheckState = 1 group by FBrokerCode) ha join (select FBrokerCode, FBrokerName, FStartDate from "
						+ pub.yssGetTableName("Tb_Para_Broker")
						+ ") hb on ha.FBrokerCode = hb.FBrokerCode and ha.FStartDate = hb.FStartDate) h on a.FBrokerCode = h.FBrokerCode "
						+*/
						" left join (select FBrokerCode, FBrokerName from  "
						+ pub.yssGetTableName("Tb_Para_Broker")
						+ " where  FCheckState = 1) h on a.FBrokerCode = h.FBrokerCode "
						+
						
						//end by lidaolong 
						// ----------------------------------------------------------------------------------------------------
						// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
					/*	" left join (select ob.* from (select FCashAccCode, max(FStartDate) as FStartDate from "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ " where FStartDate <= "
						+ dbl.sqlDate(new java.util.Date())
						+ " and FCheckState = 1 group by FCashAccCode) oa join (select FCashAccCode, FCashAccName, FStartDate from "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ ") ob on oa.FCashAccCode = ob.FCashAccCode and oa.FStartDate = ob.FStartDate) o on a.FCashAccCode = o.FCashAccCode "
						+*/
						" left join (select FCashAccCode, FCashAccName from "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ " where FCheckState = 1) o on a.FCashAccCode = o.FCashAccCode "
						+
						//end by lidaolong
						// ----------------------------------------------------------------------------------------------------
						// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
						/*" left join (select od.* from (select FCashAccCode, max(FStartDate) as FStartDate from "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ " where FStartDate <= "
						+ dbl.sqlDate(new java.util.Date())
						+ " and FCheckState = 1 group by FCashAccCode) oc join (select FCashAccCode, FCashAccName, FStartDate from "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ ") od on oc.FCashAccCode = od.FCashAccCode and oc.FStartDate = od.FStartDate) oo on a.FFactCashAccCode = oo.FCashAccCode "
						+*/
						
						" left join (select FCashAccCode, FCashAccName from "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ " where  FCheckState = 1) oo on a.FFactCashAccCode = oo.FCashAccCode "
						//---add by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName start---//
						+ " left join (select FCashAccCode, FCashAccName from " + pub.yssGetTableName("Tb_Para_CashAccount")
					    + " where FCheckState = 1) ooo on a.Fetfbalaacctcode = ooo.FCashAccCode "
					    //---add by songjie 2012.04.17 BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B 添加 FETFBalaAcctName end---//
						+
						//end by lidaolong 
						// ----------------------------------------------------------------------------------------------------
						" left join (select FAttrClsCode,FAttrClsName from "
						+ pub.yssGetTableName("Tb_Para_AttributeClass")
						+ " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode "
						+
						// ---------2009-07-07 蒋锦 添加 股东代码和席位设置 MS00021 国内股票业务
						// QDV4.1赢时胜（上海）2009年4月20日21_A --//
						" LEFT JOIN (SELECT FSeatCode, FSeatName"
						+ " FROM "
						+ pub.yssGetTableName("Tb_Para_Tradeseat")
						+ " WHERE FCheckState = 1) ts ON a.fseatCode = ts.fseatcode"
						+ " LEFT JOIN (SELECT FStockholderCode, FStockholderName"
						+ " FROM "
						+ pub.yssGetTableName("Tb_Para_Stockholder")
						+ "  WHERE FCheckState = 1) sh ON a.fstockholdercode = sh.fstockholdercode"
						+
						// -----------------------------------------------------------------------------------------------------------//

						" where a.FNum = '" + this.num + "'";
				rs = dbl.openResultSet(strSql);
				if (rs.next()) {		
					setResultSetAttr(rs);
					//story 1566 add by zhouwei 20111017 取出延迟交割标示的数值
					this.FSecurityDelaySettleState=Integer.toString(rs.getInt("FSecurityDelaySettleState"));
					buf.append(this.buildRowStr());
				}

				if (buf.toString().length() > 0) {
					return buf.toString();
				} else {
					return "";
				}

			} catch (Exception e) {
				throw new YssException("获取交易拆分数据出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}
		if (sType.equalsIgnoreCase("Interest")) {
			this.accruedInterest = this.getSettingOper().getInterest(
					this.securityCode, this.tradeAmount,
					YssFun.toDate(this.bargainDate));
		}
		// add by nimengjing 2011.2.14 BUG #1087 当回购到期日为节假日时，系统不能正确产生交易数据到期日
		if (sType.equalsIgnoreCase("isWorkDay")) {// 判断是否为工作日
			BaseOperDeal operDeal = new BaseOperDeal();
			operDeal.setYssPub(pub);
			String strSql = "";
			ResultSet rs = null;
			boolean isWorkDay = false;
			try {
				strSql = "select FHolidaysCode  from "
						+ pub.yssGetTableName("Tb_Para_Security")
						+ " where  FSecurityCode="
						+ dbl.sqlString(this.securityCode);
				rs = dbl.openResultSet(strSql);
				if (rs.next()) {
					operDeal.checkHoliday(YssFun.toDate(this.MatureDate), rs.getString("FHolidaysCode"));//检查是否有节假日 add by nimengjing 2011.2.25 BUG #1153 没有设置节假日群，新建交易数据（回购类型）报错 
					operDeal.checkHoliday(YssFun.toDate(this.MatureDate), rs
							.getString("FHolidaysCode"));
					isWorkDay = operDeal.isWorkDay(rs
							.getString("FHolidaysCode"), YssFun
							.toDate(this.MatureDate), 0);
				}
				if (!isWorkDay) {
					return "1";
				}
				return "0";

			} catch (Exception e) {
				throw new YssException("获取交易拆分数据出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}
		if (sType.equalsIgnoreCase("getHolidays")) {// 获取工作日
			BaseOperDeal operDeal = new BaseOperDeal();
			String strSql = "";
			ResultSet rs = null;
			Date dDate = new Date();
			operDeal.setYssPub(pub);
			try {
				strSql = "select FHolidaysCode  from "
						+ pub.yssGetTableName("Tb_Para_Security")
						+ " where  FSecurityCode="
						+ dbl.sqlString(this.securityCode);
				rs = dbl.openResultSet(strSql);
				if (rs.next()) {
					operDeal.checkHoliday(YssFun.toDate(this.MatureDate), rs.getString("FHolidaysCode"));//检查是否有节假日 add by nimengjing 2011.2.25 BUG #1153 没有设置节假日群，新建交易数据（回购类型）报错 
					dDate = operDeal.getWorkDay(rs.getString("FHolidaysCode"),
							YssFun.toDate(this.MatureDate), 0);
				}
				return YssFun.formatDate(dDate);

			} catch (Exception e) {
				throw new YssException("获取交易拆分数据出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}
		// ---------------------end bug #1087----------------------------------
		if(sType.equalsIgnoreCase("costIncludeInterest")){//add by zhouwei 20120416  新债中签 利息是否入成本
			CtlPubPara pubPara=new CtlPubPara();
			pubPara.setYssPub(pub);
			if(pubPara.getCostIncludeInterestsOfZQ(this.portCode)){
				return "true";
			}else{
				return "false";
			}
		}
		//--- add by zhouwei 20120620 计算ETF现金替代结算日期 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A start---//
		if (sType.equalsIgnoreCase("calculateReplaceDate")) {
			BaseOperDeal operDeal = new BaseOperDeal();
			String strSql = "";
			ResultSet rs = null;
			Date dDate = new Date();
			operDeal.setYssPub(pub);
			try {
				strSql = "select FHolidaysCode,FMtReplaceOver,FREPLACEOVER  from "
						+ pub.yssGetTableName("Tb_Para_Security")
						+ " where  FSecurityCode="
						+ dbl.sqlString(this.securityCode);
				rs = dbl.openResultSet(strSql);
				if (rs.next()) {	
					int days=0;
					if(this.tradeCode.equals(YssOperCons.YSS_JYLX_ETFSGou)){//ETF申购
						days=rs.getInt("FREPLACEOVER");
					}else if(this.tradeCode.equals(YssOperCons.YSS_JYLX_ETFSH)){//ETF赎回
						days=rs.getInt("FMtReplaceOver");
					}
					dDate = operDeal.getWorkDay(rs.getString("FHolidaysCode"),
							YssFun.toDate(this.bargainDate), days);
				}
				return YssFun.formatDate(dDate);

			} catch (Exception e) {
				throw new YssException("获取计算ETF现金替代结算日期出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}
		//--- add by zhouwei 20120620 计算ETF现金替代结算日期 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A end---//
		//---add by songjie 2013.04.12 STORY #3365 需求北京-[中国银行]QDV4.0[中]20121203001 start---//
		if (sType.equalsIgnoreCase("getCost")) {
			setCostValue();
			return this.cost.getCost() + "";
		}
		//---add by songjie 2013.04.12 STORY #3365 需求北京-[中国银行]QDV4.0[中]20121203001 end---//
		return buildRowStr();

	}

	/**shashijie 2012-5-7 STORY 2565 成本赋值 */
	private void setCostValue() throws YssException {
		StringBuffer buff = null; // 定义一个字符串用来来保存sql语句
		ResultSet rs = null; // 用来保存sql语句查出的结果集
		try {
			buff = new StringBuffer();
			buff.append(" select * from ");
			buff.append(pub.yssGetTableName("tb_Data_SubTrade"));
			buff.append(" where FNum =").append(dbl.sqlString(this.num));

			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			if (rs.next()) {
				// 以下为核算成本，管理成本，估值成本的原币、基础货币、组合货币赋值
				this.cost = new YssCost();
				this.cost.setCost(rs.getDouble("FCost"));
				this.cost.setMCost(rs.getDouble("FMCost"));
				this.cost.setVCost(rs.getDouble("FVCost"));
				this.cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
				this.cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));
				this.cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));
				this.cost.setPortCost(rs.getDouble("FPortCuryCost"));
				this.cost.setPortMCost(rs.getDouble("FMPortCuryCost"));
				this.cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
			} else {
				this.cost = new YssCost();
				this.cost.setCost(0);
				this.cost.setMCost(0);
				this.cost.setVCost(0);
				this.cost.setBaseCost(0);
				this.cost.setBaseMCost(0);
				this.cost.setBaseVCost(0);
				this.cost.setPortCost(0);
				this.cost.setPortMCost(0);
				this.cost.setPortVCost(0);
			}
			
		} catch (Exception e) {
			throw new YssException("获取交易数据子表中成本数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	

	/**
	 * parseRowStr
	 * 
	 * @param sRowStr
	 *            String
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
		String sTmpStr = "";
		String sMutiAudit = "";
		try {
			if (sRowStr.trim().length() == 0) {
				return;
			}
			if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) { // 判断是否有批量数据，以用来批量处理审核/反审核
				// 2008-03-28
				sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];
				multAuditString = sMutiAudit;
				sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];
				if (sRowStr.indexOf("\r\t") >= 0) {
					sTmpStr = sRowStr.split("\r\t")[0];
					if (sRowStr.split("\r\t").length == 3) {
						sMulRela = sRowStr.split("\r\t")[2];
					}
				} else {
					sTmpStr = sRowStr;
				}
				sRecycled = sRowStr;
				reqAry = sTmpStr.split("\t");
				this.num = reqAry[0];
				this.securityCode = reqAry[1];
				this.portCode = reqAry[2];
				this.brokerCode = reqAry[3];
				this.invMgrCode = reqAry[4];
				this.tradeCode = reqAry[5];
				this.cashAcctCode = reqAry[6];
				this.cashAcctName = reqAry[7];
				this.attrClsCode = (reqAry[8].length() == 0 ? " " : reqAry[8]); // 081015
				// edit
				// by
				// jc
				// 默认为空格
				this.attrClsName = reqAry[9];
				this.bargainDate = reqAry[10];
				this.bargainTime = reqAry[11];
				this.settleDate = reqAry[12];
				this.settleTime = reqAry[13];
				// -----------------2007.11.16 添加 蒋锦 添加字段------------------//
				this.MatureDate = reqAry[14];
				this.MatureSettleDate = reqAry[15];
				// --------------------------------------------------------------//
				this.autoSettle = reqAry[16];
				if (reqAry[17].length() != 0) {
					this.portCuryRate = Double.parseDouble(reqAry[17]);
				}
				if (reqAry[18].length() != 0) {
					this.baseCuryRate = Double.parseDouble(reqAry[18]);
				}
				if (reqAry[19].length() != 0) {
					this.allotProportion = Double.parseDouble(reqAry[19]);
				}
				if (reqAry[20].length() != 0) {
					this.oldAllotAmount = Double.parseDouble(reqAry[20]);
				}
				if (reqAry[21].length() != 0) {
					this.tradeAmount = Double.parseDouble(reqAry[21]);
				}
				if (reqAry[22].length() != 0) {
					this.tradePrice = Double.parseDouble(reqAry[22]);
				}
				if (reqAry[23].length() != 0) {
					this.tradeMoney = Double.parseDouble(reqAry[23]);
				}
				if (reqAry[24].length() != 0) {
					this.accruedInterest = Double.parseDouble(reqAry[24]);
				}
				if (reqAry[25].length() != 0) {
					this.allotFactor = Double.parseDouble(reqAry[25]);
				}
				if (reqAry[26].length() != 0) {
					this.totalCost = Double.parseDouble(reqAry[26]);
				}
				this.orderNum = reqAry[27];
				this.tailPortCode = reqAry[28];
				this.portCuryCode = reqAry[29];
				this.desc = reqAry[30];
				this.fees = reqAry[31].replaceAll("~", "\t");
				if (YssFun.isNumeric(reqAry[32])) {
					this.factor = Double.parseDouble(reqAry[32]);
				}
				this.compPoint = reqAry[33];
				if (reqAry[34].length() != 0) {
					this.bailMoney = Double.parseDouble(reqAry[34]);
				}

				this.checkStateId = Integer.parseInt(reqAry[35]);
				this.isOnlyColumns = reqAry[36];
				this.cost.parseRowStr(reqAry[37]);
				this.factCashAccCode = reqAry[38];
				if (reqAry[38].length() == 0) {
					this.factCashAccCode = this.cashAcctCode; // 默认将现金帐户传给实际结算帐户
				}
				if (YssFun.isNumeric(reqAry[39])) {
					this.factSettleMoney = YssFun.toDouble(reqAry[39]);
				}
				if (YssFun.isNumeric(reqAry[40])) {
					this.exRate = Double.parseDouble(reqAry[40]);
				}
				if (YssFun.isDate(reqAry[41])) {
					this.factSettleDate = reqAry[41];
				}
				this.settleDesc = reqAry[42];
				if (YssFun.isNumeric(reqAry[43])) {
					this.factBaseRate = Double.parseDouble(reqAry[43]);
				}
				if (YssFun.isNumeric(reqAry[44])) {
					this.factPortRate = Double.parseDouble(reqAry[44]);
				}
				// ======合中保的版本
				if (YssFun.isDate(reqAry[45])) {
					this.endBargainDate = YssFun.formatDate(reqAry[45]);

				} // by leeyu
				if (YssFun.isNumeric(reqAry[46])) {
					this.FactRate = YssFun.toDouble(reqAry[46]);
				} // =============
				subType = reqAry[47];

				// QDII ETF 项目修改 edit by songjie 2009-12-07 获取数据出错
				// 由原先错误的reqAry[45]改为reqAry[48]
				if (reqAry.length >= 49 && reqAry[48] != null
						&& YssFun.isDate(reqAry[48])) {
					this.rateDate = reqAry[48];
				}
				// QDII ETF 项目修改 edit by songjie 2009-12-07 获取数据出错
				// 由原先错误的reqAry[45]改为reqAry[48]

				super.parseRecLog();
				if (sRowStr.indexOf("\r\t") >= 0) {
					if (this.filterType == null) {
						this.filterType = new TradeSubBean();
						this.filterType.setYssPub(pub);
					}
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			} else {
				if (sRowStr.indexOf("\r\t") >= 0) {
					sTmpStr = sRowStr.split("\r\t")[0];
					if (sRowStr.split("\r\t").length == 3) {
						sMulRela = sRowStr.split("\r\t")[2];
					}
				} else {
					sTmpStr = sRowStr;
				}
				sRecycled = sRowStr;
				reqAry = sTmpStr.split("\t");
				this.num = reqAry[0];
				this.securityCode = reqAry[1];
				this.portCode = reqAry[2];
				this.brokerCode = reqAry[3];
				this.invMgrCode = reqAry[4];
				this.tradeCode = reqAry[5];
				this.cashAcctCode = reqAry[6];
				this.cashAcctName = reqAry[7];
				this.attrClsCode = (reqAry[8].length() == 0 ? " " : reqAry[8]); // 081015
				// edit
				// by
				// jc
				// 默认为空格
				this.attrClsName = reqAry[9];
				this.bargainDate = reqAry[10];
				this.bargainTime = reqAry[11];
				this.settleDate = reqAry[12];
				this.settleTime = reqAry[13];
				// -----------------2007.11.16 添加 蒋锦 添加字段------------------//
				this.MatureDate = reqAry[14];
				this.MatureSettleDate = reqAry[15];
				// --------------------------------------------------------------//
				this.autoSettle = reqAry[16];
				if (reqAry[17].length() != 0) {
					this.portCuryRate = Double.parseDouble(reqAry[17]);
				}
				if (reqAry[18].length() != 0) {
					this.baseCuryRate = Double.parseDouble(reqAry[18]);
				}
				if (reqAry[19].length() != 0) {
					this.allotProportion = Double.parseDouble(reqAry[19]);
				}
				if (reqAry[20].length() != 0) {
					this.oldAllotAmount = Double.parseDouble(reqAry[20]);
				}
				if (reqAry[21].length() != 0) {
					this.tradeAmount = Double.parseDouble(reqAry[21]);
				}
				if (reqAry[22].length() != 0) {
					this.tradePrice = Double.parseDouble(reqAry[22]);
				}
				if (reqAry[23].length() != 0) {
					this.tradeMoney = Double.parseDouble(reqAry[23]);
				}
				if (reqAry[24].length() != 0) {
					this.accruedInterest = Double.parseDouble(reqAry[24]);
				}
				if (reqAry[25].length() != 0) {
					this.allotFactor = Double.parseDouble(reqAry[25]);
				}
				if (reqAry[26].length() != 0) {
					this.totalCost = Double.parseDouble(reqAry[26]);
				}
				this.orderNum = reqAry[27];
				this.tailPortCode = reqAry[28];
				this.portCuryCode = reqAry[29];
				this.desc = reqAry[30];
				this.fees = reqAry[31].replaceAll("~", "\t");
				if (YssFun.isNumeric(reqAry[32])) {
					this.factor = Double.parseDouble(reqAry[32]);
				}
				this.compPoint = reqAry[33];
				if (reqAry[34].length() != 0) {
					this.bailMoney = Double.parseDouble(reqAry[34]);
				}

				this.checkStateId = Integer.parseInt(reqAry[35]);
				this.isOnlyColumns = reqAry[36];
				this.cost.parseRowStr(reqAry[37]);
				this.factCashAccCode = reqAry[38];
				if (reqAry[38].length() == 0) {
					this.factCashAccCode = this.cashAcctCode; // 默认将现金帐户传给实际结算帐户
				}
				if (YssFun.isNumeric(reqAry[39])) {
					this.factSettleMoney = YssFun.toDouble(reqAry[39]);
				}
				if (YssFun.isNumeric(reqAry[40])) {
					this.exRate = Double.parseDouble(reqAry[40]);
				}
				if (YssFun.isDate(reqAry[41])) {
					this.factSettleDate = reqAry[41];
				}
				this.settleDesc = reqAry[42];
				if (YssFun.isNumeric(reqAry[43])) {
					this.factBaseRate = Double.parseDouble(reqAry[43]);
				}
				if (YssFun.isNumeric(reqAry[44])) {
					this.factPortRate = Double.parseDouble(reqAry[44]);
				}
				// ======合中保的版本
				if (YssFun.isDate(reqAry[45])) {
					this.endBargainDate = YssFun.formatDate(reqAry[45]);
				} // by leeyu
				if (YssFun.isNumeric(reqAry[46])) {
					this.FactRate = YssFun.toDouble(reqAry[46]);
				} // =============
				subType = reqAry[47];

				this.rateDate = reqAry[48]; // modify by wangzuochun 2010.04.09
				// MS01075 业务资料中汇率显示有问题
				// QDV4长信2010年04月8日01_B

				// -----2009.06.22 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》
				// 国内基金业务------//
				this.ETFBalaAcctCode = reqAry[49];
				//edit by songjie 2012.11.09 如果reqAry[50]长度不为零，再判断是否为日期格式的数据
				if (reqAry[50].length() > 0 && YssFun.isDate(reqAry[50])) {
					this.ETFBalaSettleDate = YssFun.formatDate(reqAry[50]);
				}
				if (YssFun.isNumeric(reqAry[51])) {
					this.ETFBalanceMoney = YssFun.toDouble(reqAry[51]);
				}
				if (YssFun.isNumeric(reqAry[52])) {
					this.ETFCashAlternat = YssFun.toDouble(reqAry[52]);
				}
				// ----------------------------------------------------------------------------------------//
				// ------------2009-07-07 蒋锦 添加 股东代码和席位设置--------------//
				// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
				this.tradeSeatCode = reqAry[53];
				this.tradeSeatName = reqAry[54];
				this.stockholderCode = reqAry[55];
				this.stockholderName = reqAry[56];
				// -------------------------------------------------------------//
				// ------ add by wangzuochun 2009.07.22 MS00024 交易数据拆分
				// QDV4.1赢时胜（上海）2009年4月20日24_A -------//
				this.investType = reqAry[57];
				// --------------------------------------------------------------------------------------------------//
				// -------------------------------------------------------------//
				this.bargainDateEnd = reqAry[58];
				this.matureDateEnd = reqAry[59];
				this.rateDateEnd = reqAry[60];
				// -------------------------------------------------------------//
			    //story 1566 add by zhouwei 20111018 新增证券交割延迟标示
				this.FSecurityDelaySettleState=reqAry[61];
				this.costIsHandEditState=reqAry[62];//add by zhouwei 20120308 成本修改标识
				if(reqAry!=null&&reqAry.length>=64)
				     this.categoryCode=reqAry[63];//品种类型 add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A
				
				//add by huangqirong 2012-05-02 story #2565 ETF联接基金相关
				this.FBSDate = reqAry[64] ;
				if (YssFun.isNumeric(reqAry[65]))
					this.FCanReturnMoney = YssFun.toDouble(reqAry[65]);
				//---end---				
				this.mtReplaceDate=reqAry[66];//story 2727 add by zhouwei 20120619 必须现金替代结算日期  ETF链接基金
				super.parseRecLog();
				if (sRowStr.indexOf("\r\t") >= 0) {
					if (this.filterType == null) {
						this.filterType = new TradeSubBean();
						this.filterType.setYssPub(pub);
					}
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			}
		} catch (Exception e) {
			throw new YssException("解析交易拆分数据设置请求出错", e);
		}
	}

	public String getInfoBeforeSub() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String strSql = "";
		String strFatherNum = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		StringBuffer buf = new StringBuffer();
		ITradeOperation tradeOper = (ITradeOperation) pub.getOperDealCtx()
				.getBean("tradedeal");
		BaseCashAccLinkDeal cashacclinkOper = (BaseCashAccLinkDeal) pub
				.getOperDealCtx().getBean("cashacclinkdeal");
		ResultSet rs = null;
		int iNum = 0;
		try {
			strFatherNum = this.num;
			sHeader = this.getListView3Headers();
			tradeOper.setYssPub(pub);
			cashacclinkOper.setYssPub(pub);
			tradeOper.initTradeOperation(this.securityCode, this.brokerCode,
					this.invMgrCode, this.tradeCode, this.orderNum, YssFun
							.toDate(this.bargainDate), this.subType);
			tradeOper.loadTradeData();
			// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
			
		/*	strSql = "select a.FPortCode,c.FPortName,c.FPortCury from (select FPortCode, max(FStartDate) As FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FAssetGroupCode = "
					+ dbl.sqlString(pub.getAssetGroupCode())
					+ " and FCheckState = 1 and FEnabled = 1 group by FPortCode) a "
					+ " join (select FPortCode, FPortName,FPortCury, FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ ") c on a.FPortCode = c.FPortCode and a.FStartDate = c.FStartDate"
					+ " join (select FPortCode, max(FStartDate) As FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
					+ " where FRelaType = 'InvestManager'"
					+ " and FSubCode = "
					+ dbl.sqlString(this.invMgrCode)
					+ " and FCheckState = 1 group by FPortCode) b on a.FPortCode = b.FPortCode and a.FStartDate = b.FStartDate";
*/
			strSql = "select a.FPortCode,a.FPortName,a.FPortCury from (select FPortCode, FPortName,FPortCury from "
				+ pub.yssGetTableName("Tb_Para_Portfolio")
				+ " where  FAssetGroupCode = "
				+ dbl.sqlString(pub.getAssetGroupCode())
				+ " and FCheckState = 1 and FEnabled = 1 ) a "
				
				+ " join (select FPortCode from "
				+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
				+ " where FRelaType = 'InvestManager'"
				+ " and FSubCode = "
				+ dbl.sqlString(this.invMgrCode)
				+ " and FCheckState = 1) b on a.FPortCode = b.FPortCode ";

			
			//end by lidaolong 
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				buf.setLength(0);
				this.num = strFatherNum + YssFun.formatNumber(iNum, "00000");
				this.haveAmount = tradeOper.getHave(rs.getString("FPortCode"));
				this.portCode = rs.getString("FPortCode") + "";
				this.portName = rs.getString("FPortName") + "";
				this.portCuryCode = rs.getString("FPortCury") + "";
				this.portCuryRate = this.getSettingOper().getCuryRate(
						YssFun.toDate(this.bargainDate),
						(this.bargainTime == null) ? "" : this.bargainTime
								.trim(), this.portCuryCode, this.portCode,
						YssOperCons.YSS_RATE_BASE);

				this.portCuryRate = YssD.round(this.portCuryRate, 15); // 把汇率保留15位
				// 茶仕春
				// 20080130

				this.fees = "";
				this.totalCost = 0;
				this.tradeAmount = 0;
				this.tradeMoney = 0;
				this.accruedInterest = 0;
				this.checkStateId = 1;

				buf.append(rs.getString("FPortCode")).append("\t");
				buf.append(rs.getString("FPortName")).append("\t");
				buf.append(YssFun.formatNumber(this.haveAmount, "#,##0.##"))
						.append("\t");
				buf.append(YssFun.formatNumber(this.allotProportion, "##.#%"))
						.append("\t");
				buf
						.append(
								YssFun.formatNumber(this.oldAllotAmount,
										"#,##0.##")).append("\t");
				buf.append(YssFun.formatNumber(this.tradeAmount, "#,##0.##"))
						.append("\t");
				buf.append(YssFun.formatNumber(this.tradePrice, "#,##0.####"))
						.append("\t");
				buf.append(YssFun.formatNumber(this.tradeMoney, "#,##0.##"))
						.append("\t");
				buf.append(
						YssFun.formatNumber(this.accruedInterest, "#,##0.##"))
						.append("\t");
				buf.append(this.cashAcctCode).append("\t");
				buf.append(this.cashAcctName);
				bufShow.append(buf.toString())
						.append(YssCons.YSS_LINESPLITMARK);

				this.allotProportion = this.allotProportion * 100;

				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
				iNum++;
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}

			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView3ShowCols();
		} catch (Exception e) {
			throw new YssException("初始交易拆分数据信息出错：" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * 日志中的修改前数据
	 * 
	 * @return String
	 * @throws YssException
	 *             MS00290 QDV4华夏2009年3月05日01_B
	 */
	public String getBeforeEditData() throws YssException {
		TradeSubBean beforeTradeSub = new TradeSubBean();
		String strSql = "";
		ResultSet rs = null;
		strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
				+ " d.FPortName, e.FSecurityName, e.FHandAmount, f.FInvMgrName, g.FTradeTypeName, "
				+ " h.FBrokerName as FBrokerName, o.FCashAccName, p.FAttrClsName, e.FFactor,oo.FCashAccName as FFactCashAccName,e.FFactRate, ts.fseatname, sh.fstockholdername "
				+ // 合中保的版本
				" from "
				+ pub.yssGetTableName("Tb_Data_SubTrade")
				+ " a "
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
				+
				// ----------------------------------------------------------------------------------------------------
				// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
			/*	" left join (select db.* from (select FPortCode, max(FStartDate) as FStartDate from "
				+ pub.yssGetTableName("Tb_Para_Portfolio")
				+ " where FStartDate <= "
				+ dbl.sqlDate(new java.util.Date())
				+ " and FCheckState = 1 group by FPortCode) da join (select FPortCode, FPortName, FStartDate from "
				+ pub.yssGetTableName("Tb_Para_Portfolio")
				+ ") db on da.FPortCode = db.FPortCode and da.FStartDate = db.FStartDate) d on a.FPortCode = d.FPortCode "
				+*/
				" left join (select FPortCode, FPortName from "
				+ pub.yssGetTableName("Tb_Para_Portfolio")
				+ " where FCheckState = 1 ) d on a.FPortCode = d.FPortCode "
				+
				
				//end by lidaolong 
				// ----------------------------------------------------------------------------------------------------
				" left join (select eb.*,case when ec.FFactRate is null then 0 else ec.FFactRate end as FFactRate from (select FSecurityCode, max(FStartDate) as FStartDate from "
				+ // 合中保的版本
				pub.yssGetTableName("Tb_Para_Security")
				+ " where FStartDate <= "
				+ dbl.sqlDate(new java.util.Date())
				+ " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, FHandAmount, FFactor from "
				+ pub.yssGetTableName("Tb_Para_Security")
				+ ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate "
				+ " left join (select FSecurityCode,FFactRate from "
				+ pub.yssGetTableName("tb_para_fixinterest")
				+ " )ec on ea.FSecurityCode=ec.FSecurityCode "
				+ ") e on a.FSecurityCode = e.FSecurityCode "
				+
				// ----------------------------------------------------------------------------------------------------
				// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
				/*" left join (select fb.* from (select FInvMgrCode, max(FStartDate) as FStartDate from "
				+ pub.yssGetTableName("Tb_Para_InvestManager")
				+ " where FStartDate <= "
				+ dbl.sqlDate(new java.util.Date())
				+ " and FCheckState = 1 group by FInvMgrCode) fa join (select FInvMgrCode, FInvMgrName, FStartDate from "
				+ pub.yssGetTableName("Tb_Para_InvestManager")
				+ ") fb on fa.FInvMgrCode = fb.FInvMgrCode and fa.FStartDate = fb.FStartDate) f on a.FInvMgrCode = f.FInvMgrCode "
				+*/
				" left join (select FInvMgrCode, FInvMgrName from "
				+ pub.yssGetTableName("Tb_Para_InvestManager")
				+ " where  FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode "
				+
				
				//end by lidaolong 
				// ----------------------------------------------------------------------------------------------------
				" left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode"
				+
				// ----------------------------------------------------------------------------------------------------
				// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
				/*" left join (select hb.* from (select FBrokerCode, max(FStartDate) as FStartDate from "
				+ pub.yssGetTableName("Tb_Para_Broker")
				+ " where FStartDate <= "
				+ dbl.sqlDate(new java.util.Date())
				+ " and FCheckState = 1 group by FBrokerCode) ha join (select FBrokerCode, FBrokerName, FStartDate from "
				+ pub.yssGetTableName("Tb_Para_Broker")
				+ ") hb on ha.FBrokerCode = hb.FBrokerCode and ha.FStartDate = hb.FStartDate) h on a.FBrokerCode = h.FBrokerCode "
				+*/
				
				" left join (select FBrokerCode, FBrokerName from "
				+ pub.yssGetTableName("Tb_Para_Broker")
				+ " where FCheckState = 1 ) h on a.FBrokerCode = h.FBrokerCode "
				+
				
				//end by lidaolong 
				// ----------------------------------------------------------------------------------------------------
				// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
				
				/*" left join (select ob.* from (select FCashAccCode, max(FStartDate) as FStartDate from "
				+ pub.yssGetTableName("Tb_Para_CashAccount")
				+ " where FStartDate <= "
				+ dbl.sqlDate(new java.util.Date())
				+ " and FCheckState = 1 group by FCashAccCode) oa join (select FCashAccCode, FCashAccName, FStartDate from "
				+ pub.yssGetTableName("Tb_Para_CashAccount")
				+ ") ob on oa.FCashAccCode = ob.FCashAccCode and oa.FStartDate = ob.FStartDate) o on a.FCashAccCode = o.FCashAccCode "
				+*/
				" left join (select FCashAccCode, FCashAccName from "
				+ pub.yssGetTableName("Tb_Para_CashAccount")
				+ " where FCheckState = 1 ) o on a.FCashAccCode = o.FCashAccCode "
				+
				//end by lidaolong 
				// --------------------------------------------------------------------------------------------------
				// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
			/*	" left join (select oc.* from (select FCashAccCode, max(FStartDate) as FStartDate from "
				+ pub.yssGetTableName("Tb_Para_CashAccount")
				+ " where FStartDate <= "
				+ dbl.sqlDate(new java.util.Date())
				+ " and FCheckState = 1 group by FCashAccCode) od join (select FCashAccCode, FCashAccName, FStartDate from "
				+ pub.yssGetTableName("Tb_Para_CashAccount")
				+ ") oc on od.FCashAccCode = oc.FCashAccCode and od.FStartDate = oc.FStartDate) oo on a.FFactCashAccCode = oo.FCashAccCode "
				+*/
				" left join (select FCashAccCode, FCashAccName from "
				+ pub.yssGetTableName("Tb_Para_CashAccount")
				+ " where  FCheckState = 1 ) oo on a.FFactCashAccCode = oo.FCashAccCode "
				+
				//end by lidaolong 
				// ----------------------------------------------------------------------------------------------------
				" left join (select FAttrClsCode,FAttrClsName from "
				+ pub.yssGetTableName("Tb_Para_AttributeClass")
				+ " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode "
				+
				// ---------2009-07-07 蒋锦 添加 股东代码和席位设置 MS00021 国内股票业务
				// QDV4.1赢时胜（上海）2009年4月20日21_A --//
				" LEFT JOIN (SELECT FSeatCode, FSeatName"
				+ " FROM "
				+ pub.yssGetTableName("Tb_Para_Tradeseat")
				+ " WHERE FCheckState = 1) ts ON a.fseatCode = ts.fseatcode"
				+ " LEFT JOIN (SELECT FStockholderCode, FStockholderName"
				+ " FROM "
				+ pub.yssGetTableName("Tb_Para_Stockholder")
				+ "  WHERE FCheckState = 1) sh ON a.fstockholdercode = sh.fstockholdercode"
				+
				// -----------------------------------------------------------------------------------------------------------//

				" where FNum = "
				+ dbl.sqlString(this.num)
				+ " order by a.FPortCode";
		try {
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				beforeTradeSub.num = rs.getString("FNum");
				beforeTradeSub.portCode = rs.getString("FPortCode");
				beforeTradeSub.portName = rs.getString("FPortName");
				beforeTradeSub.securityCode = rs.getString("FSecurityCode");
				beforeTradeSub.securityName = rs.getString("FSecurityName");
				beforeTradeSub.tradeCode = rs.getString("FTradeTypeCode");
				beforeTradeSub.tradeName = rs.getString("FTradeTypeName");
				beforeTradeSub.brokerCode = rs.getString("FBrokerCode");
				beforeTradeSub.brokerName = rs.getString("FBrokerName");
				beforeTradeSub.invMgrCode = rs.getString("FInvMgrCode");
				beforeTradeSub.invMgrName = rs.getString("FInvMgrName");
				beforeTradeSub.baseCuryRate = rs.getDouble("FFactBaseRate");
				beforeTradeSub.portCuryRate = rs.getDouble("FFactPortRate");
				beforeTradeSub.totalCost = rs.getDouble("FTotalCost");
				beforeTradeSub.allotProportion = rs
						.getDouble("FAllotProportion");
				beforeTradeSub.oldAllotAmount = rs.getDouble("FOldAllotAmount");
				beforeTradeSub.tradeAmount = rs.getDouble("FTradeAmount");
				beforeTradeSub.tradePrice = rs.getDouble("FTradePrice");
				beforeTradeSub.tradeMoney = rs.getDouble("FTradeMoney");
				beforeTradeSub.accruedInterest = rs
						.getDouble("FAccruedInterest");
				beforeTradeSub.cashAcctCode = rs.getString("FCashAccCode");
				beforeTradeSub.cashAcctName = rs.getString("FCashAccName");
				beforeTradeSub.MatureDate = rs.getString("FMatureDate");
				beforeTradeSub.factSettleDate = YssFun.formatDate(
						rs.getDate("FFactSettleDate") == null ? rs
								.getDate("FSettleDate") : rs
								.getDate("FFactSettleDate"), "yyyy-MM-dd");
				beforeTradeSub.settleDate = YssFun.formatDate(rs
						.getDate("FSettleDate"), "yyyy-MM-dd");
				beforeTradeSub.bargainDate = YssFun.formatDate(rs
						.getDate("FBargainDate"), "yyyy-MM-dd");
				beforeTradeSub.rateDate = rs.getDate("FRateDate") == null ? YssFun
						.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd")
						: YssFun.formatDate(rs.getDate("FRateDate"),
								"yyyy-MM-dd");
				beforeTradeSub.MatureDate = rs.getDate("FMatureDate") == null ? YssFun
						.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd")
						: YssFun.formatDate(rs.getDate("FMatureDate"),
								"yyyy-MM-dd");
				beforeTradeSub.MatureSettleDate = rs
						.getDate("FMatureSettleDate") == null ? YssFun
						.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd")
						: YssFun.formatDate(rs.getDate("FMatureSettleDate"),
								"yyyy-MM-dd");
				// -----2009.06.22 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》
				// 国内基金业务------//
				beforeTradeSub.ETFBalaAcctCode = rs
						.getString("FETFBalaAcctCode");
				beforeTradeSub.ETFBalaSettleDate = rs
						.getString("FETFBalaSettleDate") == null ? "" : YssFun
						.formatDate(rs.getDate("FETFBalaSettleDate"),
								"yyyy-MM-dd");
				beforeTradeSub.ETFBalanceMoney = rs.getDouble("FETFBalaMoney");
				beforeTradeSub.ETFCashAlternat = rs
						.getDouble("FETFCashAlternat");
				//add by huangqirong 2012-05-02 story #2565 联接基金需要用
				beforeTradeSub.FBSDate = rs.getString("FBSDATE") == null ? "" : YssFun.formatDate(rs.getDate("FBSDATE"),"yyyy-MM-dd");
				beforeTradeSub.FCanReturnMoney = rs.getDouble("FCANRETURNMONEY");
				//---end---
				beforeTradeSub.setYssPub(pub);
				beforeTradeSub.loadFees(rs);
			}
		} catch (Exception e) {
			throw new YssException("获取修改前数据出现异常！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return beforeTradeSub.buildRowStr();
	}

	// 获取配股权益中的相关信息
	public TradeSubBean setOthersub(String security, String bargainDate)
			throws YssException {
		TradeSubBean otherSub = null;
		ResultSet rs = null;
		String strSql = "";
		try {
			strSql = "select FSecurityCode, FTsecurityCode, FExRightDate, FPayDate, FRIPrice from "
					+ pub.yssGetTableName("Tb_Data_RightsIssue")
					+ " where FSecurityCode = "
					+ dbl.sqlString(security)
					+ " and "
					+ dbl.sqlDate(bargainDate)
					+ " between FExRightDate and FPayDate and FCheckState = 1";
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				otherSub = new TradeSubBean();
				otherSub.setSecurityCode(rs.getString("FTsecurityCode"));
				otherSub.setPortCode(this.portCode);
				otherSub.setBrokerCode(this.brokerCode);
				otherSub.setInvMgrCode(this.invMgrCode);
				otherSub.setTradeCode(this.tradeCode);
				otherSub.setCashAcctCode(this.cashAcctCode);
				otherSub.setAttrClsCode(this.attrClsCode);
				otherSub.setBargainDate(this.bargainDate);
				otherSub.setBargainTime(this.bargainTime);
				otherSub.setSettleDate(this.settleDate);
				otherSub.setSettleTime(this.settleTime);
				// -----------2007.11.16 添加 添加字段------------//
				otherSub.setMatureDate(this.MatureDate);
				otherSub.setMatureSettleDate(this.MatureSettleDate);
				// ---------------------------------------------//
				otherSub.setRateDate(this.rateDate); // edit by jc
				otherSub.setAutoSettle(this.autoSettle);
				otherSub.setPortCuryRate(this.portCuryRate);
				otherSub.setBaseCuryRate(this.baseCuryRate);
				otherSub.setAllotProportion(this.allotProportion);
				otherSub.setOldAllotAmount(this.oldAllotAmount);
				otherSub.setAllotFactor(this.allotFactor);
				otherSub.setTradeAmount(this.tradeAmount);
				otherSub.setTradePrice(rs.getDouble("FRIPrice"));
				otherSub.setTradeMoney(rs.getDouble("FRIPrice")
						* otherSub.getTradeAmount()); // 配股价格*交易数量
				otherSub.setAccruedInterest(this.accruedInterest);
				otherSub.setBailMoney(this.bailMoney);
				otherSub.setFees(this.fees);
				otherSub.setTotalCost(this.totalCost);
				otherSub.setCost(this.getCost()); // other cost need
				otherSub.setOrderNum(this.orderNum);
				otherSub.setDesc(this.desc);
			} else {
				otherSub = new TradeSubBean();
			}
			return otherSub;
		} catch (Exception e) {
			throw new YssException("初始交易拆分数据信息出错：" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 得回购债券的期限信息 添加日期：2007-11-16 蒋锦
	 * 
	 * @return String
	 */
	public String getDurability() throws YssException {
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		try {
			String strSql = "SELECT a.* " + "FROM "
					+ pub.yssGetTableName("Tb_Para_DepositDuration") + " a "
					+ "LEFT JOIN " + pub.yssGetTableName("Tb_Para_Purchase")
					+ " b ON a.FDepDurCode = b.FDepDurCode "
					+ "WHERE b.FSecurityCode = "
					+ dbl.sqlString(this.securityCode);
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				buf.append(rs.getInt("FDuration")).append("\t");
				buf.append(rs.getString("FDurUnit")).append("\t\null");
			}
			return buf.toString();
		} catch (Exception e) {
			throw new YssException("获取证券回购期限出错：" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * deleteRecycleData
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = "";
		Connection conn = null;
		boolean bTrans = false;
		String[] arrData = null;
		TradeSubBean data = null;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			arrData = sRecycled.split("\r\n");
			for (int i = 0; i < arrData.length; i++) {
				if (arrData[i].length() == 0) {
					continue;
				}
				this.parseRowStr(arrData[i]);
				strSql = "delete from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FNum = " + dbl.sqlString(this.num);
				dbl.executeSql(strSql);
				// add by zhouxiang 2010.12.8 昨日库存为空， 今日卖空交易
				if (CheckYestodayStock(this) > 0
						|| CheckYesterdayBorrowAmount(this) >= 0) {
					strSql = "delete from "
							+ pub.yssGetTableName("TB_DATA_SecLendTRADE")
							+ " where frelanum=" + dbl.sqlString(this.num);
					dbl.executeSql(strSql);
				}
				// add by zhouxiang 2010.12.8 昨日库存为空， 今日卖空交易
				strSql = "delete from "
						+ pub.yssGetTableName("Tb_Data_TradeRela")
						+ " where FNum = " + dbl.sqlString(this.num);
				dbl.executeSql(strSql);
				strSql = "delete from "
						+ pub.yssGetTableName("Tb_Data_TradeRelaSub")
						+ " where FNum = " + dbl.sqlString(this.num);
				dbl.executeSql(strSql);

				// ---增加清除回收站的日志记录功能----panjunfang add 20100820-------//
				data = new TradeSubBean();
				data = this;
				data.fees = this.fees.replaceAll("\t", "~");
				logOper = SingleLogOper.getInstance();
				logOper.setIData(data, YssCons.OP_CLEAR, pub);
				// -----------------------------------------//
			}
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		} catch (Exception e) {
			throw new YssException("清除数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * add by wangzuochun 2009.12.3 MS00767 对组合1的资产进行估值，提示财务估值表已经确认，不能估值
	 * QDV4赢时胜（上海）2009年10月28日04_B
	 * 
	 * @param sPortCode
	 * @return
	 * @throws YssException
	 */
	private String getPortID(String sPortCode) throws YssException {
		ResultSet rs = null;
		ResultSet rs2 = null;
		String sqlStr = "";
		String strSql = "";
		String strAssetCode = "";
		String strPortID = "";
		try {
			sqlStr = "select FAssetCode from "
					+ pub.yssGetTableName("tb_para_portfolio")
					+ " where FPortCode = " + dbl.sqlString(sPortCode);
			rs = dbl.openResultSet(sqlStr);
			if (rs.next()) {
				strAssetCode = rs.getString("FAssetCode");
				strSql = "Select FSetCode from Lsetlist where FSetID = "
						+ dbl.sqlString(strAssetCode);
				rs2 = dbl.openResultSet(strSql);
				if (rs2.next()) {
					strPortID = rs2.getString("FSetCode");
				}
				dbl.closeResultSetFinal(rs2);
			}
			dbl.closeResultSetFinal(rs2);
			dbl.closeResultSetFinal(rs);
		} catch (Exception ex) {
			throw new YssException("取组合信息出错,请检查设置是否正确", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rs2);
		}
		return strPortID;
	}

	// MS01422 add by zhangfa 20100807 QDV411建行2010年07月08日01_A
	public String getFactSettleDate(String num) throws YssException {
		String strReturn = "";
		String sql = "";
		ResultSet rs = null;
		try {
			if (num == null || num.length() == 0) {
				return "1900-01-01";
			}
			sql = "select Ffactsettledate  from  "
					+ pub.yssGetTableName("Tb_Data_SubTrade") + " where fnum="
					+ dbl.sqlString(num);
			rs = dbl.openResultSet(sql);
			while (rs.next()) {
				strReturn = rs.getDate("FFactSettleDate") + "";
			}
		} catch (Exception e) {
			throw new YssException("获取实际结算日期时出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs); // 释放资源
		}
		return strReturn;

	}

	// -----------------------------------------------------------------
	/**
	 * 检查操作的日期段内净值表和财务估值表是否被确认，适用于数据完整性控制 xuxuming 20090807
	 * MS00543:QDV4赢时胜（上海）2009年6月22日01_AB
	 * 
	 * modify by wangzuochun 2009.12.3 MS00767 对组合1的资产进行估值，提示财务估值表已经确认，不能估值
	 * QDV4赢时胜（上海）2009年10月28日04_B
	 * 
	 * @return String
	 */
	private String checkGuessNavStat() throws YssException {
		String strSql = ""; // 保存sql语句的字符串
		String strPortID = "";
		ResultSet rs = null; // 定义一个结果集用来保存sql查出的结果
		java.util.Date dResultDate = null; // 保存上面两个日期的较大者
		java.util.Date dMinDate = null; // 最小的业务日期
		String strReturn = "0"; // 最后的返回字符串，存储0表示前台传入的期间段内无财务估值表或净值表确认
		// modify by nimengjing 2011.2.16 BUG #1001 交易数据界面，同时反审核大数据量时（如：一千条数据）系统报错
		StringBuffer buf = new StringBuffer();
		try {
			String[] arrPortcode = null;
			if (this.portCode.indexOf(",") > 0) {
				arrPortcode = this.portCode.split(",");
			}
			if (arrPortcode != null) {
				for (int i = 0; i < arrPortcode.length; i++) {
					strPortID = getPortID(arrPortcode[i]);
					if (strPortID != null && strPortID.length() > 0) {
						buf = buf.append(strPortID).append(",");
					}
				}
			} else {
				strPortID = getPortID(this.portCode);
			}
			if (buf.length() > 1) {
				strPortID = buf.toString().substring(0, buf.length() - 1);
			}
			buf.delete(0, buf.length());//清除buf
			//edit by lidaolong  20110323  #798 需要将净值表和财务估值表的确认功能按组合区分，分别确认和反确认
			for (int j =0;j<strPortID.split(",").length;j++){
			// 从财务估值表和净值表中取出最大确认日期 fdate是财务估值表最大确认日期、fnavdate是净值表最大确认日期
			strSql = "SELECT a.fdate,b.fnavdate FROM (select max(fdate) as fdate from "
					+ pub.yssGetTableName("Tb_Rep_Guessvalue")
					+ " where facctlevel = '1' and facctcode='C100' and FPortCode in ("
					/*+ operSql.sqlCodes(strPortID) + ")" */
					+ operSql.sqlCodes(strPortID.split(",")[j]) + ")" 
					
		   // --------------------------------end bug #1001--------------------------------------
					+ ") a,"
					+ "(select max(fnavdate) as fnavdate from "
					+ pub.yssGetTableName("Tb_Data_Navdata")
					+ " where FKeyCode = 'confirm'";
			if (this.portCode != null && !"".equals(this.portCode.trim())) {// edit
				// by
				// xuxuming,20090929.考虑组合代码有可能为空的情况
				strSql += " and FPORTCODE in ('"
						/*+ operSql.sqlCodes(this.portCode) + ") ";*/ // 查询条件在前台选择的组合群下净值表是否为结算状态
					+portCode.split(",")[j]+"')";
			}
			// fanghaoln 20090901 MS00654 QD鹏华基金2009年08月25日01_B
			// " and FPORTCODE in (" + operSql.sqlCodes(this.portCode) +
			// ") " + //查询条件在前台选择的组合群下净值表是否为结算状态
			// =====================================================================================================
			strSql += " ) b";

			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				// 财务估值表确认日期：rs.getDate("FDate")；净值表确认日期:rs.getDate("fnavdate")
				// 1.如果净值表和财务估值表确认日期都不为null ，取二者中较大的日期
				if (rs.getDate("FDate") != null
						&& rs.getDate("fnavdate") != null) {
					dResultDate = YssFun.dateDiff(rs.getDate("FDate"), rs
							.getDate("fnavdate")) > 0 ? rs.getDate("fnavdate")
							: rs.getDate("FDate");
				} else {
					// 2如果净值表或财务估值表只有一个有确认日期，做以下处理
					if (rs.getDate("FDate") != null) {
						// a.如果财务估值表日期不为null，取财物估值表日期
						dResultDate = rs.getDate("FDate");
					} else if (rs.getDate("fnavdate") != null) {
						// 如果净值表日期不为null,取净值表日期
						dResultDate = rs.getDate("fnavdate");
					}
				}
			}
			
			//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
			dbl.closeResultSetFinal(rs);
			
			// 如果财务估值表和净值表都没确认，不执行处理，直接返回0
			if (dResultDate != null) {
				// 把前台传来的日期分解出来进行判断
				String[] datas = this.settleDate.split("~n~");
				// 循环遍历所有要处理的业务日期，取出最小的一个日期
				for (int i = 0; i < datas.length; i++) {
					if (!YssFun.isDate(datas[i])) {
						continue;
					}
					if (dMinDate == null) {
						dMinDate = YssFun.toDate(datas[i]);
					}
					// 如果dMinDate大于datas[i]，则将dMinDate赋值为datas[i]
					if (YssFun.dateDiff(YssFun.toDate(datas[i]), dMinDate) > 0) {
						dMinDate = YssFun.toDate(datas[i]);
					}
				}
				// 如果最小的业务日期比净值表、财务估值表确认日期小，返回1，提示最小日期到确认日期期间段已确认
				if (dMinDate != null
						&& YssFun.dateDiff(dMinDate, dResultDate) >= 0) {
					
				/*	strReturn = "1" + "," + dResultDate.toString() + ","
							+ YssFun.formatDate(dMinDate, "yyyy-MM-dd");*/
					//if(datas.length>1 && !datas[1].trim().equals("") && !datas[0].equals(datas[1])){
						throw new YssException("组合【"+portCode.split(",")[j]+"】对应的业务日期【"+YssFun.formatDate(dMinDate, "yyyy-MM-dd")+"】至【"+dResultDate.toString()+"】对应的所有业务已确认，业务数据不可执行当前操\r\n作，如必须操作该业务日期的业务请先反确认财务估值表和净值表！");
					//}else{
					//	throw new YssException("组合【"+portCode.split(",")[j]+"】对应的业务日期【"+datas[0]+"】对应的所有业务已确认，业务数据不可执行当前操\r\n作，如必须操作该业务日期的业务请先反确认财务估值表和净值表！");
					//}
				
				}
			}
		 }
		} catch (Exception e) {
			throw new YssException("获取财务估值表和净值表是否确认时出错！", e);
			//throw new YssException(e.getMessage(),e);
		} finally {
			dbl.closeResultSetFinal(rs); // 释放资源
		}
		return strReturn;
	}

	// 合并太平版本代码 获取品种类型
	public String getSubTsfTypeCode(String securityCode) throws YssException {
		ResultSet rs = null;
		String catCode = "";
		String strSql = "select FCATCODE from "
				+ pub.yssGetTableName("Tb_Para_Security")
				+ " where FSECURITYCODE = '" + securityCode + "'";
		try {
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				catCode = rs.getString("FCATCODE");
			}
			return catCode;
		} catch (SQLException e) {
			throw new YssException("获取数据出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	public String getTreeViewGroupData1() throws YssException {
		return "";
	}

	public String getTreeViewGroupData2() throws YssException {
		return "";
	}

	public String getTreeViewGroupData3() throws YssException {
		return "";
	}

	public String getListViewGroupData1() throws YssException {
		return "";
	}

	public String getListViewGroupData2() throws YssException {
		return "";
	}

	public String getListViewGroupData3() throws YssException {
		return "";
	}

	public String getListViewGroupData4() throws YssException {
		return "";
	}

	public String getListViewGroupData5() throws YssException {
		return "";
	}

	// add by zhouxiang 2010.12.8 证券借贷， 卖空交易 如果是卖出交易需要查询昨日是否有库存生成证券借贷借入的交易数据
	/**
	 * subNum2 交易编号放到证券借贷交易数据中关联
	 * 
	 * @throws YssException
	 */
	private boolean ProduceBLTradeAdd(String subNum2) throws YssException {
		/*double dProduceNum = 0;
		dProduceNum = CheckYestodayStock(this);
		if (dProduceNum <= 0) {
			return false;
		}*/ // modify by fangjiang 2011.11.21 STORY #1433 请在4.0系统开发卖空交易的支持功能
		String strNumDate = "";
		String strSql = "";
		String strSqlDel = "";
		String sNum = "";
		double dCost = YssD.mul(this.tradeAmount, this.tradePrice);
		double dBaseCost = YssD.mul(dCost, this.baseCuryRate);
		double dPortCost = YssD.div(dBaseCost, this.portCuryRate);
		try {
			Connection conn = dbl.loadConnection();
			strNumDate = YssFun.formatDatetime(YssFun.toDate(this.bargainDate))
					.substring(0, 8);
			sNum = strNumDate
					+ dbFun.getNextInnerCode(pub
							.yssGetTableName("tb_DATA_SecLendTRADE"), dbl
							.sqlRight("FNUM", 6), "000000",
							" where FNum like 'T" + strNumDate
									+ "000000".substring(0, 1) + "%'", 1);
			sNum = "T" + sNum;
			sNum = sNum
					+ dbFun.getNextInnerCode(pub
							.yssGetTableName("TB_DATA_SecLendTRADE"), dbl
							.sqlRight("FNUM", 5), "00000", " where FNum like '"
							+ sNum.replaceAll("'", "''") + "%'");
			/*
			 * strSqlDel = "delete from " +
			 * pub.yssGetTableName("TB_DATA_SecLendTRADE") +
			 * " a where a.fbargaindate=" + dbl.sqlDate(this.bargainDate) +
			 * "and  a.fsecuritycode=" + dbl.sqlString(this.securityCode) +
			 * " and a.fbrokercode=" + dbl.sqlString(this.brokerCode);
			 */
			strSql = "insert into "
					+ pub.yssGetTableName("TB_DATA_SecLendTRADE")
					+ " (FNUM ,FSecurityCode,FBARGAINDATE ,FBARGAINTIME,FTRADETYPECODE,FSETTLEDATE,FSETTLETIME,FINVMGRCODE,FPORTCODE,"
					+ "FBrokerCode,FATTRCLSCODE,FCASHACCCODE,FagreementType,FTRADEAMOUNT,FTRADEPRICE,FTOTALCOST,FCollateralCode,FcollateralRatio,"
					+ " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4,"
					+ " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8,"
					+ " FperiodDate,FlendRatio,FPeriodCode,FFormulaCode,"
					+ " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, FPortCuryCost,FMPortCuryCost, FVPortCuryCost,"
					+ " FCheckState,FCreator,FCreateTime,FCheckUser,FRelaNum)"
					+ " values("
					+ dbl.sqlString(sNum)
					+ ","
					+ dbl.sqlString(this.securityCode)
					+ ","
					+ dbl.sqlDate(this.bargainDate)
					+ ","
					+ dbl.sqlString(this.bargainTime)
					+ ","
					+ dbl.sqlString("borrow")
					+ ","
					+ dbl.sqlDate(this.settleDate)
					+ ","
					+ dbl.sqlString(this.settleTime)
					+ ","
					+ ((this.invMgrCode.trim().length() > 0) ? dbl
							.sqlString(this.invMgrCode) : "' '")
					+ ","
					+ dbl.sqlString(this.portCode)
					+ ","
					+ ((this.brokerCode.trim().length() > 0) ? dbl
							.sqlString(this.brokerCode) : "' '")
					+ ","
					+ dbl.sqlString(this.attrClsCode)
					+ ","
					+ dbl.sqlString(this.cashAcctCode)
					+ ","
					+ dbl.sqlString("协商式")
					+ ","
					+ this.tradeAmount
					+ ","
					+ this.tradePrice
					+ ",0,"
					+ "''"
					+ ","
					+ "0"
					+ ","
					+ "' ' , 0 , ' ' , 0 , ' ' , 0 ,' ' , 0 ,' ' , 0 ,' ' , 0 ,' ' , 0 ,' ' , 0 ,"
					+ dbl.sqlDate(this.bargainDate) + "," + "0" + "," + "''"
					+ "," + "' '" + "," + dCost + "," + dCost + "," + dCost
					+ "," + dBaseCost + "," + dBaseCost + "," + dBaseCost + ","
					+ dPortCost + "," + dPortCost + "," + dPortCost + "," + "0"
					+ "," + dbl.sqlString(this.creatorCode) + ","
					+ dbl.sqlString(this.creatorTime) + "," + "' ',"
					+ dbl.sqlString(subNum2) + " )";
			conn.setAutoCommit(false);
			/* dbl.executeSql(strSqlDel); */
			dbl.executeSql(strSql);
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("卖空交易产生证券借贷交易数据出错");
		} finally {
		}
		return true;
	}

	/**
	 * 查询卖出当天前一日是否有证券库存库存，是否需要对证券借贷交易界面数据操作，需要大于0,不需要小于0
	 * 
	 * @param tradeSubBean
	 * @throws YssException
	 */
	private double CheckYestodayStock(TradeSubBean tradeSubBean)
			throws YssException {
		if (!tradeSubBean.getTradeCode().equalsIgnoreCase("02")) {
			return -1;
		}
		String sqlStr = "";
		ResultSet rsSub = null;
		double dFlag = 0;// 操作标识 卖出余数=昨日库存-今日卖出数
		try {
			sqlStr = "select a.fstorageamount from "// 查询是否有昨日库存，如果有昨日库存就不产生借入交易数据
					+ pub.yssGetTableName("tb_stock_security")
					+ " a where a.fstoragedate ="
					+ dbl.sqlDate(YssFun.addDay(YssFun.toDate(tradeSubBean
							.getBargainDate()), -1))
					+ " and a.fsecuritycode = "
					+ dbl.sqlString(tradeSubBean.getSecurityCode())
					+ " and a.fportcode = "
					+ dbl.sqlString(tradeSubBean.getPortCode())
					+ "and  a.fstorageamount>0";
			rsSub = dbl.openResultSet(sqlStr);
			if (rsSub.next()) {
				return -1;
				/*
				 * dFlag =
				 * YssD.sub(rsSub.getDouble("fstorageamount"),tradeSubBean
				 * .getTradeAmount());//昨日库存减去卖空数量 if (dFlag > 0) {//有库存数直接退出不处理
				 * return -1; }else{ return YssD.mul(dFlag, -1); }//
				 * 如果卖出余数大于零则不需要产生借入交易数据
				 */} else {// 无库存直接返回交易数量
				sqlStr = "select * from "
						+ pub.yssGetTableName("Tb_Para_SecurityLend")// 必须有数据
						+ " a where a.fsecuritycode= "
						+ dbl.sqlString(tradeSubBean.getSecurityCode())
						+ "and a.fcheckstate=1 and a.fbrokercode="
						+ dbl.sqlString(tradeSubBean.getBrokerCode());
				rsSub = dbl.openResultSet(sqlStr);
				if (rsSub.next()) {
					return tradeSubBean.getTradeAmount();// 证券借贷信息设置中必须有对应证券和券商的设置才产生借入的交易数据
				} else {
					return -1;
				}
			}
		} catch (Exception e) {
			throw new YssException("卖出产生证券借贷交易数据出错");
		} finally {
			dbl.closeResultSetFinal(rsSub);
		}
	}

	/***
	 * @param subNum
	 *            add by zhouxiang 2010.12.9 证券借贷--昨日有借入数量则今日买入自动归还
	 * @throws YssException
	 */
	private boolean ProduceBLTradeRcb(String subNum) throws YssException {
		/*double dcheck = CheckYesterdayBorrowAmount(this);
		if (dcheck < 0) {
			return true;
		}*/ // modify by fangjiang 2011.11.21 STORY #1433 请在4.0系统开发卖空交易的支持功能
		String strNumDate = "";
		String strSql = "";
		String strSqlDel = "";
		String sNum = "";
		double dCost = YssD.mul(this.tradeAmount, this.tradePrice);
		double dBaseCost = YssD.mul(dCost, this.baseCuryRate);
		double dPortCost = YssD.div(dBaseCost, this.portCuryRate);
		try {
			Connection conn = dbl.loadConnection();
			strNumDate = YssFun.formatDatetime(YssFun.toDate(this.bargainDate))
					.substring(0, 8);
			sNum = strNumDate
					+ dbFun.getNextInnerCode(pub
							.yssGetTableName("tb_DATA_SecLendTRADE"), dbl
							.sqlRight("FNUM", 6), "000000",
							" where FNum like 'T" + strNumDate
									+ "000000".substring(0, 1) + "%'", 1);
			sNum = "T" + sNum;
			sNum = sNum
					+ dbFun.getNextInnerCode(pub
							.yssGetTableName("TB_DATA_SecLendTRADE"), dbl
							.sqlRight("FNUM", 5), "00000", " where FNum like '"
							+ sNum.replaceAll("'", "''") + "%'");

			/*
			 * strSqlDel = "delete from " +
			 * pub.yssGetTableName("TB_DATA_SecLendTRADE") +
			 * " a where a.fbargaindate=" + dbl.sqlDate(this.bargainDate) +
			 * "and  a.fsecuritycode=" + dbl.sqlString(this.securityCode) +
			 * " and a.fbrokercode=" + dbl.sqlString(this.brokerCode);
			 */
			strSql = "insert into "
					+ pub.yssGetTableName("TB_DATA_SecLendTRADE")
					+ " (FNUM ,FSecurityCode,FBARGAINDATE ,FBARGAINTIME,FTRADETYPECODE,FSETTLEDATE,FSETTLETIME,FINVMGRCODE,FPORTCODE,"
					+ "FBrokerCode,FATTRCLSCODE,FCASHACCCODE,FagreementType,FTRADEAMOUNT,FTRADEPRICE,FTOTALCOST,FCollateralCode,FcollateralRatio,"
					+ " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4,"
					+ " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8,"
					+ " FperiodDate,FlendRatio,FPeriodCode,FFormulaCode,"
					+ " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, FPortCuryCost,FMPortCuryCost, FVPortCuryCost,"
					+ " FCheckState,FCreator,FCreateTime,FCheckUser,FRelaNum)"
					+ " values("
					+ dbl.sqlString(sNum)
					+ ","
					+ dbl.sqlString(this.securityCode)
					+ ","
					+ dbl.sqlDate(this.bargainDate)
					+ ","
					+ dbl.sqlString(this.bargainTime)
					+ ","
					+ dbl.sqlString("Rcb")
					+ ","
					+ dbl.sqlDate(this.settleDate)
					+ ","
					+ dbl.sqlString(this.settleTime)
					+ ","
					+ ((this.invMgrCode.trim().length() > 0) ? dbl
							.sqlString(this.invMgrCode) : "' '")
					+ ","
					+ dbl.sqlString(this.portCode)
					+ ","
					+ ((this.brokerCode.trim().length() > 0) ? dbl
							.sqlString(this.brokerCode) : "' '")
					+ ","
					+ dbl.sqlString(this.attrClsCode)
					+ ","
					+ dbl.sqlString(this.cashAcctCode)
					+ ","
					+ dbl.sqlString("协商式")
					+ ","
					+ this.tradeAmount
					+ ","
					+ this.tradePrice
					+ ",0,"
					+ "''"
					+ ","
					+ "0"
					+ ","
					+ "' ' , 0 , ' ' , 0 , ' ' , 0 ,' ' , 0 ,' ' , 0 ,' ' , 0 ,' ' , 0 ,' ' , 0 ,"
					+ dbl.sqlDate(this.bargainDate) + "," + "0" + "," + "''"
					+ "," + "' '" + "," + dCost + "," + dCost + "," + dCost
					+ "," + dBaseCost + "," + dBaseCost + "," + dBaseCost + ","
					+ dPortCost + "," + dPortCost + "," + dPortCost + "," + "0"
					+ "," + dbl.sqlString(this.creatorCode) + ","
					+ dbl.sqlString(this.creatorTime) + "," + "' ',"
					+ dbl.sqlString(subNum) + " )";
			conn.setAutoCommit(false);
			/* dbl.executeSql(strSqlDel); */
			dbl.executeSql(strSql);
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("卖空交易产生证券借贷交易数据出错");
		} finally {
		}
		return false;
	}

	// end by zhouxiang 2010.12.8 证券借贷， 卖空交易 如果是卖出交易需要查询昨日是否有库存生成证券借贷借入的交易数据

	/**
	 * 昨日有借入数量， 当天的买入则自动产生归还数据 返回归还数=（当天买入数量-昨日借入数量）*（-1）
	 * 逻辑：昨日有借入数，否则返回-1;昨日借入数量-如果买入数量<0 则录入的数据错误无法智能冲减 返回-1
	 * ，昨日借入数量-如果买入数量=>0的时候才产生买入数量的归还数据
	 * 
	 * @throws YssException
	 */
	private double CheckYesterdayBorrowAmount(TradeSubBean tradeSubBean)
			throws YssException {
		if (!tradeSubBean.getTradeCode().equalsIgnoreCase("01")) {// 如果是买入则进入判断操作
			return -1;
		}
		String sqlStr = "";
		ResultSet rs = null;
		double dFlag = 0;// 昨日借入数量-今日买入数量
		try {
			sqlStr = "select a.famount from "
					+ pub.yssGetTableName("tb_stock_secrecpay")// 买入业务当天的前一日是否有借入数量
					// 有并且借入数量>买入数量返回正数.
					+ " a join( select * from "
					+ pub.yssGetTableName("Tb_Para_SecurityLend")
					+ " b where b.fsecuritycode= "
					+ dbl.sqlString(tradeSubBean.getSecurityCode())
					+ " and b.fcheckstate=1 and fbrokercode="
					+ dbl.sqlString(tradeSubBean.getBrokerCode())
					+ " ) c on a.fsecuritycode=c.fsecuritycode"
					+ "  where a.fstoragedate = "
					+ dbl.sqlDate(YssFun.addDay(YssFun.toDate(tradeSubBean
							.getBargainDate()), -1)) + " and a.fsecuritycode= "
					+ dbl.sqlString(tradeSubBean.getSecurityCode())
					+ " and a.fportcode="
					+ dbl.sqlString(tradeSubBean.getPortCode())
					+ " and a.fsubtsftypecode = '10BSC'and a.famount>0";
			rs = dbl.openResultSet(sqlStr);
			if (rs.next()) {
				dFlag = YssD.sub(rs.getDouble("famount"), tradeSubBean
						.getTradeAmount());// 昨日库存减去卖空数量
				if (dFlag < 0) {
					return -1;
				} else {
					return dFlag;
				}
			} else {
				return -1;
			} // 没有借入库存不处理
		} catch (Exception e) {
			throw new YssException("卖出产生证券借贷交易数据出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2012-4-27 STORY 2565 */
	public String getFSecurityDelaySettleState() {
		return FSecurityDelaySettleState;
	}

	public void setFSecurityDelaySettleState(String fSecurityDelaySettleState) {
		FSecurityDelaySettleState = fSecurityDelaySettleState;
	}
	/**end*/

	public double getFCanReturnMoney() {
		return FCanReturnMoney;
	}

	public void setFCanReturnMoney(double fCanReturnMoney) {
		FCanReturnMoney = fCanReturnMoney;
	}

	public String getFBSDate() {
		return FBSDate;
	}

	public void setFBSDate(String fBSDate) {
		FBSDate = fBSDate;
	}

	public String getCostIsHandEditState() {
		return costIsHandEditState;
	}

	public void setCostIsHandEditState(String costIsHandEditState) {
		this.costIsHandEditState = costIsHandEditState;
	}
}
