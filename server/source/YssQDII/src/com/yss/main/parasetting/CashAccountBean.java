package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 * <p>
 * Title: CashAccountBean
 * </p>
 * <p>
 * Description: 现金帐户设置
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.ysstech.com
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class CashAccountBean extends BaseDataSettingBean implements
		IDataSetting {
	private String strCashAcctCode = ""; // 现金帐户代码
	private String strCashAcctName = ""; // 现金帐户名称

	private String strAcctTypeCode = ""; // 帐户类型代码
	private String strAcctTypeName = ""; // 帐户类型名称
	private String strSubAcctTypeCode = ""; // 帐户子类型代码
	private String strSubAcctTypeName = ""; // 帐户子类型名称
	private String strBankCode = ""; // 银行代码
	private String strBankName = ""; // 银行名称
	private String strBankAccount = ""; // 银行帐号
	private String strBankAccName="";	//银行账号名称  add by zhouxiang MS01629 2010.10.13 招商基金资金账户信息查询功能
	private String strCurrencyCode = ""; // 货币代码
	private String strCurrencyName = ""; // 货币名称
	private String strPortCode = ""; // 组合代码
	private String strPortName = ""; // 组合名称
	// BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
	private String assetGroupCode = ""; // 组合群代码
	private String assetGroupName = ""; // 组合群名称
	private boolean bOverGroup = false; // 判断是否跨组合群 MS00001
	// QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
	// panjunfang add 20090813
	// --------------------------------------------------------------------------------
	private String strAcctState = ""; // 帐户状态： 0-可用，1-冻结

	private int interestOrigin = 99; // 利息来源： 0-内部，1-外部，2-不计息
	private double fixRate; // 固定利率
	private int interestCycle = 99; // 计息周期： 0-按日计算，1-按月计算，2-按季计算，3-按年计算
	private int interestWay = 99; // 计息方式
	private String strFormulaCode = ""; // 利息公式代码
	private String strFormulaName = ""; // 利息公式名称
	private String strLoanCode="";      //贷款利息方式   by guyichuan 20110520 STORY #561 
	private String strLoanName="";		//贷款利息方式   by guyichuan 20110520 STORY #561 
	private String strPeriodCode = ""; // 期间代码
	private String strPeriodName = ""; // 期间名称
	private String strRoundCode = ""; // 舍入设置代码
	private String strRoundName = ""; // 舍入设置名称
	private String strDesc = ""; // 描述
	private String strIsOnlyColumns = "0"; // 是否显示数据

	private String strInvMgrCodeLink = ""; // 现金帐户连接投资经理
	private String strPortCodeLink = ""; // 现金帐户连接投资组合

	private String strDepDurCode = ""; // 期限代码
	private String strDepDurName = "";
	private int accAttr = 99;
	private String strOldCashAcctCode = "";
	private java.util.Date dtStartDate;
	private java.util.Date dtMatureDate = null;
	private java.util.Date dtOldStartDate;
	private CashAccountBean filterType;
	private String sRecycled = "";
	private String checkAccLinks = ""; // 批量审核 MS00179 QDV4建行2009年1月07日01_B
	// 2009.02.13 方浩
	private boolean checkSign = false; // 是否取已审核的记录
	// -------为获取相应时间段的帐户数据。 sj edit 20080624 ----//
	private java.util.Date dBeginDate = null;
	private java.util.Date dEndDate = null;
    private int count = 0;//MS01288 QDV4交银施罗德2010年6月10日01_A  add by jiangshichao 2010.07.02 
    private String recPayCode="";//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
    private String recPayName="";
    
    // add by jiangshichao 2012.02.28  存款利息税利率  start ----------------------------
    private String strInterTax = ""; //利息税利率公式 
    private String strInterTaxName = "";
    //add by yeshenghong 20120424 计息算法 story 2441
    private int interestAlg = 99 ;
    private String qsAccNum = "";
    private String qsAccName = "";

  //--------------add by zhaoxianlin 20121217 STORY #3384 外管局报表——QDII账户信息----start
    private java.util.Date dtOpenDate = null;//开户日期
    private String sAccSort = "";  //账户分类
  //---------------add by zhaoxianlin  20121217 STORY #3384 外管局报表——QDII账户信息-----end 
    
    private String filterConditions = ""; //add by huangqirong 2013-01-30 过滤条件
    
    /*added by yeshenghong 2013-6-7 Story 3958 */
    private String cashInBank = "";;
    
    private String otherReceivalbes = "";
    
    private String otherPayables = "";
    
    private String interestReceivalbe = "";
    
    private String interestRevenue = "";
    
    private String isAutoAcc = "0";//是否自动创建清算账户
    
    public String getIsAutoAcc() {
		return isAutoAcc;
	}

	public void setIsAutoAcc(String isAutoAcc) {
		this.isAutoAcc = isAutoAcc;
	}

	public String getCashInBank() {
		return cashInBank;
	}

	public void setCashInBank(String cashInBank) {
		this.cashInBank = cashInBank;
	}

	public String getOtherReceivalbes() {
		return otherReceivalbes;
	}

	public void setOtherReceivalbes(String otherReceivalbes) {
		this.otherReceivalbes = otherReceivalbes;
	}

	public String getOtherPayables() {
		return otherPayables;
	}

	public void setOtherPayables(String otherPayables) {
		this.otherPayables = otherPayables;
	}

	public String getInterestReceivalbe() {
		return interestReceivalbe;
	}

	public void setInterestReceivalbe(String interestReceivalbe) {
		this.interestReceivalbe = interestReceivalbe;
	}

	public String getInterestRevenue() {
		return interestRevenue;
	}

	public void setInterestRevenue(String interestRevenue) {
		this.interestRevenue = interestRevenue;
	}
	/*end by yeshenghong 2013-6-7 Story 3958 */
    
    public java.util.Date getDtOpenDate() {
		return dtOpenDate;
	}

	public void setDtOpenDate(java.util.Date dtOpenDate) {
		this.dtOpenDate = dtOpenDate;
	}

	public String getsAccSort() {
		return sAccSort;
	}

	public void setsAccSort(String sAccSort) {
		this.sAccSort = sAccSort;
	}

	public String getQsAccNum() {
		return qsAccNum;
	}

	public void setQsAccount(String qsAccount) {
		this.qsAccNum = qsAccount;
	}

	public String getQsAccName() {
		return qsAccName;
	}

	public void setQsAccName(String qsAccName) {
		this.qsAccName = qsAccName;
	}

	public int getInterestAlg() {
		return interestAlg;
	}

	public void setInterestAlg(int interestAlg) {
		this.interestAlg = interestAlg;
	}

	public String getStrInterTax() {
		return strInterTax;
	}

	public void setStrInterTax(String strInterTax) {
		this.strInterTax = strInterTax;
	}
	
	public String getstrInterTaxName(){
		return  strInterTaxName;
	}
	
	public void setStrInterTaxName(String strInterTaxName){
		this.strInterTaxName = strInterTaxName;
	}
   //存款利息税利率  end --------------------------------------------------------------------------
	
	public String getStrLoanName() {
		return strLoanName;
	}

	public String getStrLoanCode() {
		return strLoanCode;
	}

	public void setStrLoanCode(String strLoanCode) {
		this.strLoanCode = strLoanCode;
	}

	public void setStrLoanName(String strLoanName) {
		this.strLoanName = strLoanName;
	}
    public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	// -------------------------------------------------------
	// MS00179 QDV4建行2009年1月07日01_B 2009.02.17 方浩
	public void setCheckAccLinks(String checkAccLinks) {
		this.checkAccLinks = checkAccLinks;
	}

	public String getCheckAccLinks() {
		return checkAccLinks;
	}

	// ---------------------------------------------------------------------
	public java.util.Date getDtOldStartDate() {
		return dtOldStartDate;
	}

	public void setDepDurCode(String strDepDurCode) {
		this.strDepDurCode = strDepDurCode;
	}

	public String getDepDurCode(String strDepDurCode) {
		return strDepDurCode;
	}

	public String getDepDurName(String strDepDurName) {
		return strDepDurName;
	}

	public void setDepDurName(String strDepDurName) {
		this.strDepDurName = strDepDurName;
	}

	public void setDtOldStartDate(java.util.Date dtOldStartDate) {
		this.dtOldStartDate = dtOldStartDate;
	}

	public java.util.Date getDtStartDate() {
		return dtStartDate;
	}

	public void setDtStartDate(java.util.Date dtStartDate) {
		this.dtStartDate = dtStartDate;
	}

	public CashAccountBean getFilterType() {
		return filterType;
	}

	public void setFilterType(CashAccountBean filterType) {
		this.filterType = filterType;
	}

	public String getStrAcctState() {
		return strAcctState;
	}

	public void setStrAcctState(String strAcctState) {
		this.strAcctState = strAcctState;
	}

	public String getStrAcctTypeCode() {
		return strAcctTypeCode;
	}

	public void setStrAcctTypeCode(String strAcctTypeCode) {
		this.strAcctTypeCode = strAcctTypeCode;
	}

	public String getStrAcctTypeName() {
		return strAcctTypeName;
	}

	public void setStrAcctTypeName(String strAcctTypeName) {
		this.strAcctTypeName = strAcctTypeName;
	}

	public String getStrBankAccount() {
		return strBankAccount;
	}
	
	public void setStrBankAccount(String strBankAccount) {
		this.strBankAccount = strBankAccount;
	}
	//begin zhouxiang  2010.10.13 MS01629 招商基金资金账号信息查询功能---------------------------
	public String getStrBankAccName(){
		return strBankAccName;
	}
	public void setStrBankAccName(String strBankAccName){
		this.strBankAccName=strBankAccName;
	}
	//end-- zhouxiang  2010.10.13 MS01629 招商基金资金账号信息查询功能---------------------------
	public String getStrBankCode() {
		return strBankCode;
	}

	public void setStrBankCode(String strBankCode) {
		this.strBankCode = strBankCode;
	}

	public String getStrBankName() {
		return strBankName;
	}

	public void setStrBankName(String strBankName) {
		this.strBankName = strBankName;
	}

	public String getStrCashAcctCode() {
		return strCashAcctCode;
	}

	public void setStrCashAcctCode(String strCashAcctCode) {
		this.strCashAcctCode = strCashAcctCode;
	}

	public String getStrCashAcctName() {
		return strCashAcctName;
	}

	public void setStrCashAcctName(String strCashAcctName) {
		this.strCashAcctName = strCashAcctName;
	}

	public String getStrCurrencyCode() {
		return strCurrencyCode;
	}

	public void setStrCurrencyCode(String strCurrencyCode) {
		this.strCurrencyCode = strCurrencyCode;
	}

	public String getStrCurrencyName() {
		return strCurrencyName;
	}

	public void setStrCurrencyName(String strCurrencyName) {
		this.strCurrencyName = strCurrencyName;
	}

	public String getStrDesc() {
		return strDesc;
	}

	public void setStrDesc(String strDesc) {
		this.strDesc = strDesc;
	}

	public String getStrFormulaCode() {
		return strFormulaCode;
	}

	public void setStrFormulaCode(String strFormulaCode) {
		this.strFormulaCode = strFormulaCode;
	}

	public String getStrFormulaName() {
		return strFormulaName;
	}

	public void setStrFormulaName(String strFormulaName) {
		this.strFormulaName = strFormulaName;
	}

	public int getInterestCycle() {
		return interestCycle;
	}

	public void setInterestCycle(int interestCycle) {
		this.interestCycle = interestCycle;
	}

	public int getInterestOrigin() {
		return interestOrigin;
	}

	public void setInterestOrigin(int interestOrigin) {
		this.interestOrigin = interestOrigin;
	}

	public String getStrOldCashAcctCode() {
		return strOldCashAcctCode;
	}

	public void setStrOldCashAcctCode(String strOldCashAcctCode) {
		this.strOldCashAcctCode = strOldCashAcctCode;
	}

	public String getStrPeriodCode() {
		return strPeriodCode;
	}

	public void setStrPeriodCode(String strPeriodCode) {
		this.strPeriodCode = strPeriodCode;
	}

	public String getStrPeriodName() {
		return strPeriodName;
	}

	public void setStrPeriodName(String strPeriodName) {
		this.strPeriodName = strPeriodName;
	}

	public String getStrPortCode() {
		return strPortCode;
	}

	public void setStrPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}

	public String getStrPortName() {
		return strPortName;
	}

	public void setStrPortName(String strPortName) {
		this.strPortName = strPortName;
	}

	public String getStrRoundCode() {
		return strRoundCode;
	}

	public void setStrRoundCode(String strRoundCode) {
		this.strRoundCode = strRoundCode;
	}

	public String getStrRoundName() {
		return strRoundName;
	}

	public void setStrRoundName(String strRoundName) {
		this.strRoundName = strRoundName;
	}

	public String getStrSubAcctTypeCode() {
		return strSubAcctTypeCode;
	}

	public void setStrSubAcctTypeCode(String strSubAcctTypeCode) {
		this.strSubAcctTypeCode = strSubAcctTypeCode;
	}

	public String getStrSubAcctTypeName() {
		return strSubAcctTypeName;
	}

	public int getInterestWay() {
		return interestWay;
	}

	public boolean isCheckSign() {
		return checkSign;
	}

	public int getAccAttr() {
		return accAttr;
	}

	public java.util.Date getDBeginDate() {
		return dBeginDate;
	}

	public java.util.Date getDEndDate() {
		return dEndDate;
	}

	public void setStrSubAcctTypeName(String strSubAcctTypeName) {
		this.strSubAcctTypeName = strSubAcctTypeName;
	}

	public void setInterestWay(int interestWay) {
		this.interestWay = interestWay;
	}

	public void setCheckSign(boolean checkSign) {
		this.checkSign = checkSign;
	}

	public void setAccAttr(int accAttr) {
		this.accAttr = accAttr;
	}

	public void setDBeginDate(Date dBeginDate) {
		this.dBeginDate = dBeginDate;
	}

	public void setDEndDate(Date dEndDate) {
		this.dEndDate = dEndDate;
	}

	public void setInterestWay(byte interestWay) {
		this.interestWay = interestWay;
	}

	// ------ add by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求
	// QDV4南方2009年12月25日02_A ------//
	public void setBOverGroup(boolean bOverGroup) {
		this.bOverGroup = bOverGroup;
	}

	public boolean getBOverGroup() {
		return bOverGroup;
	}

	// ------ MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A ------//

	public CashAccountBean() {
	}

	/**
	 * parseRowStr 解析现金帐户信息维护请求
	 * 
	 * @param sRowStr
	 *            String
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
		String sTmpStr = "";
		try {
			if (sRowStr.equals("")) {
				return;
			}
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
			if (sRowStr.indexOf("\r\t") >= 0) {
				sTmpStr = sRowStr.split("\r\t")[0];
			} else {
				sTmpStr = sRowStr;
			}
			sRecycled = sRowStr;
			reqAry = sTmpStr.split("\t");
			this.strCashAcctCode = reqAry[0];
			this.strCashAcctName = reqAry[1];
			this.dtStartDate = YssFun.toDate(reqAry[2]);
			this.strAcctTypeCode = reqAry[3];
			this.strSubAcctTypeCode = reqAry[4];
			this.strBankCode = reqAry[5];
			this.strBankAccount = reqAry[6];
			this.strCurrencyCode = reqAry[7];
			this.strPortCode = reqAry[8];
			this.strAcctState = reqAry[9];
			if (YssFun.isNumeric(reqAry[10])) {
				this.interestOrigin = Integer.parseInt(reqAry[10]);
			}
			if (YssFun.isNumeric(reqAry[11])) {
				this.fixRate = Double.parseDouble(reqAry[11]);
			}
			if (YssFun.isNumeric(reqAry[12])) {
				this.interestCycle = Integer.parseInt(reqAry[12]);
			}
			this.strFormulaCode = reqAry[13]; 
			this.strPeriodCode = reqAry[14];
			this.strRoundCode = reqAry[15];
			
			//------ modify by wangzuochun 2011.06.03 BUG 2003 证券信息维护界面，维护一条证券信息，输入描述信息若含有回车符，清除/还原时报错 
            if (reqAry[16] != null ){
            	if (reqAry[16].indexOf("【Enter】") >= 0){
            		this.strDesc = reqAry[16].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.strDesc = reqAry[16];
            	}
            }
            //----------------- BUG 2003 ----------------//
			this.checkStateId = Integer.parseInt(reqAry[17]);
			this.strOldCashAcctCode = reqAry[18];
			this.dtOldStartDate = YssFun.toDate(reqAry[19]);
			this.strIsOnlyColumns = reqAry[20];
			if (reqAry.length > 22) {
				this.strInvMgrCodeLink = reqAry[21];
				this.strPortCodeLink = reqAry[22];
			}

			if (YssFun.isDate(reqAry[23])) {
				this.dtMatureDate = YssFun.toDate(reqAry[23]);
			}
			this.strDepDurCode = reqAry[24];
			if (YssFun.isNumeric(reqAry[25])) {
				this.interestWay = YssFun.toInt(reqAry[25]);
			}
			if (YssFun.isNumeric(reqAry[26])) {
				this.accAttr = YssFun.toInt(reqAry[26]);
			}
			if (YssFun.isDate(reqAry[27])) {
				if (!reqAry[27].equalsIgnoreCase("9998-12-31")) {
					this.dBeginDate = YssFun.parseDate(reqAry[27]);
				}
			}
			if (YssFun.isDate(reqAry[28])) {
				if (!reqAry[28].equalsIgnoreCase("9998-12-31")) {
					this.dEndDate = YssFun.parseDate(reqAry[28]); // replace 26
					// to 27 sj
					// edit
					// 20080728
				}
			}
			// -----------------------------------------------------------------------------------------
			// MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩
			this.checkAccLinks = reqAry[29]; // 如果是审核和反审核，则把所有的信息都放入了数组的22位
			this.checkAccLinks = this.checkAccLinks.replaceAll("\f", "\t"); // 为了便于使用通用的解析过程
			// --------------------------------------------------------------------------------------------
			// BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln
			// 20090512
			this.assetGroupCode = reqAry[30];
			this.assetGroupName = reqAry[31];
			// ---------------------------------------------------------------------------------------------
			this.strBankAccName=reqAry[32];//add by zhouxiang 2010.10.13 招商基金资金账号信息查询功能
			this.strLoanCode=reqAry[33];    //add by guyichuan 20110520 STORY #561
			this.recPayCode=reqAry[34];//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
		    
			this.strInterTax = reqAry[35];
			this.strInterTaxName = reqAry[36];
			if (YssFun.isNumeric(reqAry[37])) {
				this.interestAlg = YssFun.toInt(reqAry[37]);
			}
			this.qsAccNum = reqAry[38];
			this.qsAccName = reqAry[39];
			//--------------add by zhaoxianlin 20121217 STORY #3384 外管局报表——QDII账户信息----start
			this.sAccSort = reqAry[40];
			//---------------add by zhaoxianlin  20121217 STORY #3384 外管局报表——QDII账户信息-----end
			
			/*added by yeshenghong 2013-6-7 Story 3958 */
			this.cashInBank = reqAry[41];
            this.otherReceivalbes = reqAry[42];
            this.otherPayables = reqAry[43];
            this.interestReceivalbe = reqAry[44];
            this.interestRevenue = reqAry[45];
            this.isAutoAcc = reqAry[46];
            /*end by yeshenghong 2013-6-7 Story 3958 */
			
            //--------------add by zhaoxianlin 20121217 STORY #3384 外管局报表——QDII账户信息----start
			if (YssFun.isDate(reqAry[47])) {
				this.dtOpenDate = YssFun.toDate(reqAry[47]);
			}
			//---------------add by zhaoxianlin  20121217 STORY #3384 外管局报表——QDII账户信息-----end
			super.parseRecLog();
			if (sRowStr.indexOf("\r\t") >= 0) {
				if (this.filterType == null) {
					this.filterType = new CashAccountBean();
					this.filterType.setYssPub(pub);
				}
				if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			}
		} catch (Exception e) {
			throw new YssException("解析现金帐户信息维护出错", e);
		}
	}

	/**
	 * buildRowStr 获取数据字符串
	 * 
	 * @return String
	 */
	public String buildRowStr() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.strCashAcctCode.trim());
		buf.append("\t");
		buf.append(this.strCashAcctName.trim());
		buf.append("\t");
		buf.append(YssFun.formatDate(this.dtStartDate, YssCons.YSS_DATEFORMAT));
		buf.append("\t");
		buf.append(this.strAcctTypeCode.trim());
		buf.append("\t");
		buf.append(this.strAcctTypeName.trim());
		buf.append("\t");
		buf.append(this.strSubAcctTypeCode.trim());
		buf.append("\t");
		buf.append(this.strSubAcctTypeName.trim());
		buf.append("\t");
		buf.append(this.strBankCode.trim());
		buf.append("\t");
		buf.append(this.strBankName.trim());
		buf.append("\t");
		buf.append(this.strBankAccount.trim());
		buf.append("\t");
		buf.append(this.strCurrencyCode.trim());
		buf.append("\t");
		buf.append(this.strCurrencyName.trim());
		buf.append("\t");
		buf.append(this.strPortCode.trim());
		buf.append("\t");
		buf.append(this.strPortName.trim());
		buf.append("\t");
		buf.append(this.strAcctState.trim());
		buf.append("\t");
		buf.append(this.interestOrigin);
		buf.append("\t");
		buf.append(this.fixRate);
		buf.append("\t");
		buf.append(this.interestCycle);
		buf.append("\t");
		buf.append(this.strFormulaCode.trim());
		buf.append("\t");
		buf.append(this.strFormulaName.trim());
		buf.append("\t");
		//---add by guyichuan 20110520 STORY #561 增加贷款利息方式
		buf.append(this.strLoanCode.trim());
		buf.append("\t");
		buf.append(this.strLoanName.trim());
		buf.append("\t");
		//------end-STORY #561---------
		buf.append(this.strPeriodCode.trim());
		buf.append("\t");
		buf.append(this.strPeriodName.trim());
		buf.append("\t");
		buf.append(this.strRoundCode.trim());
		buf.append("\t");
		buf.append(this.strRoundName.trim());
		buf.append("\t");
		buf.append(this.strDesc.trim());
		buf.append("\t");
		buf.append(YssFun.formatDate(this.dtMatureDate));
		buf.append("\t");
		buf.append(this.interestWay).append("\t");
		buf.append(this.strDepDurCode).append("\t");
		buf.append(this.strDepDurName).append("\t");
		buf.append(this.accAttr).append("\t");
		// BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
		buf.append(this.assetGroupCode).append("\t");
		buf.append(this.assetGroupName).append("\t");
		// ---------------------------------------------------------------------------------------------
		buf.append(this.strBankAccName).append("\t");//add by zhouxiang MS01629 2010.10.13 招商基金资金账号信息查询功能
		//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
		buf.append(this.recPayCode).append("\t");
		buf.append(this.recPayName).append("\t");
		buf.append(this.strInterTax).append("\t");
		buf.append(this.strInterTaxName).append("\t");
		buf.append(this.interestAlg).append("\t");
		buf.append(this.qsAccNum).append("\t");
		buf.append(this.qsAccName).append("\t");
		//--start---modified by zhaoxianlin 20121217 STORY #3384 外管局报表
	    buf.append(this.sAccSort).append("\t");
		//--end---modified by zhaoxianlin 20121217 STORY #3384 外管局报表
	    
	    //---------------add by yeshenghong story 3958  20130607 ---------start
        buf.append(this.cashInBank).append("\t");
        buf.append(this.otherReceivalbes).append("\t");
        buf.append(this.otherPayables).append("\t");
        buf.append(this.interestReceivalbe).append("\t");
        buf.append(this.interestRevenue).append("\t");
        buf.append(this.isAutoAcc).append("\t");
        //---------------add by yeshenghong story 3958  20130607 -----end
      //--start---modified by zhaoxianlin 20121217 STORY #3384 外管局报表
		if(this.dtOpenDate!=null){
			buf.append(YssFun.formatDate(this.dtOpenDate)).append("\t");
		}
		//--end---modified by zhaoxianlin 20121217 STORY #3384 外管局报表
		buf.append(super.buildRecLog());
		return buf.toString();
	}

	/**
	 * checkInput 检查证券信息维护数据是否合法
	 * 
	 * @param btOper
	 *            byte
	 */
	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper, pub
				.yssGetTableName("Tb_Para_CashAccount"),
				//----edit by songjie 2011.03.14  不以启用日期作为查询主键数据的参数----//
				"FCashAccCode", this.strCashAcctCode,
				this.strOldCashAcctCode);
		        //----edit by songjie 2011.03.14  不以启用日期作为查询主键数据的参数----//

	}

	/**
	 * 
	 * 解析Port
	 * 
	 * @return String
	 */
	private String getPort(String str) {
		String[] str1 = str.split(",");
		String sRes = "";
		for (int i = 0; i < str1.length; i++) {
			sRes += "'" + str1[i] + "',";
		}
		return sRes.substring(0, sRes.length() - 1);
	}

	/**
	 * 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() throws YssException {
		String sResult = "";
		try {
			if (this.filterType != null) {
				sResult = " where 1=1 ";
				if (this.filterType.strIsOnlyColumns.equalsIgnoreCase("1")) {
					sResult = sResult + " and 1=2 ";
					return sResult;
				}
				if (this.filterType.checkStateId == 1) {
					sResult = sResult + " and a.FCheckState = 1 ";
				}
				if (this.checkSign == true) {
					sResult = sResult + " and a.FCheckState = 1 ";
				}
				if (this.filterType.strCashAcctCode.length() != 0) {
					sResult = sResult + " and a.FCashAccCode like '"
							+ filterType.strCashAcctCode.replaceAll("'", "''")
							+ "%'";
				}
				if (this.filterType.strCashAcctName.length() != 0) {
					sResult = sResult + " and a.FCashAccName like '"
							+ filterType.strCashAcctName.replaceAll("'", "''")
							+ "%'";
				}
				if (this.filterType.strAcctTypeCode.length() != 0) {
					sResult = sResult + " and a.FAccType like '"
							+ filterType.strAcctTypeCode.replaceAll("'", "''")
							+ "%'";
				}
				//modify by fangjiang 2010.12.06 STORY #97 协议存款业务需支持提前提取本金的功能
				if (this.filterType.strSubAcctTypeCode.length() != 0) {
					sResult = sResult + " and a.FSubAccType in (" 
                    		  +	operSql.sqlCodes(filterType.strSubAcctTypeCode) + ")";
				}
				//------------------------
				if (this.filterType.strBankCode.length() != 0) {
					sResult = sResult + " and a.FBankCode like '"
							+ filterType.strBankCode.replaceAll("'", "''")
							+ "%'";
				}
				if (this.filterType.strBankAccount.length() != 0) {
					sResult = sResult + " and a.FBankAccount like '"
							+ filterType.strBankAccount.replaceAll("'", "''")
							+ "%'";
				}
				if (this.filterType.strCurrencyCode.length() != 0) {
					sResult = sResult + " and a.FCuryCode like '"
							+ filterType.strCurrencyCode.replaceAll("'", "''")
							+ "%'";
				}

				if (this.filterType.dtStartDate != null
						&& !this.filterType.dtStartDate.equals(YssFun
								.toDate("9998-12-31"))
						&& !this.filterType.dtStartDate.equals(YssFun
								.toDate("1900-01-01"))) {
					sResult = sResult + " and a.FStartDate = "
							+ dbl.sqlDate(filterType.dtStartDate);
				}

				if (this.filterType.dtMatureDate != null
						&& !this.filterType.dtMatureDate.equals(YssFun
								.toDate("9998-12-31"))) {
					sResult = sResult + " and a.FMatureDate = "
							+ dbl.sqlDate(filterType.dtMatureDate);
				}

				if (this.filterType.strFormulaCode.length() != 0) {
					sResult = sResult + " and a.FFormulaCode like '"
							+ filterType.strFormulaCode.replaceAll("'", "''")
							+ "%'";
				}
				if (!this.filterType.strAcctState.equalsIgnoreCase("99")
						&& this.filterType.strAcctState.length() != 0) {
					sResult = sResult + " and a.FState ="
							+ filterType.strAcctState;
				}
				if (this.filterType.strPortCodeLink.length() != 0) { // strPortCode
					// replace
					// to
					// strPortCodelink
					sResult = sResult + " and (a.FPortCode in (" + // lzp modify
							// 2007 12.7
							// 现金库存调用现金帐户时出错
							// FPortCode的表指代不明
							// 改为a.FPortCode
							this.getPort(filterType.strPortCodeLink) + ") )";
				}
				if (this.filterType.strPortCode.length() != 0) {
					sResult = sResult + " and (a.FPortCode in ("
							+ this.getPort(filterType.strPortCode) + ") or "
							+ dbl.sqlIsNull("a.fportcode", "' '") + " = ' ')";
				} // lzp modify 20080122 收益记提时查询现金帐户时出错 FPortCode的表指代不明
				// 改为a.FPortCode
				if (this.filterType.strPeriodCode.length() != 0) {
					sResult = sResult + " and a.FPeriodCode like '"
							+ filterType.strPeriodCode.replaceAll("'", "''")
							+ "%'";
				}
				if (this.filterType.interestCycle != 99) {
					sResult = sResult + " and a.FInterestCycle = "
							+ this.filterType.interestCycle;
				}
				if (this.filterType.interestOrigin != 99) {
					sResult = sResult + " and a.FInterestOrigin = "
							+ this.filterType.interestOrigin;
				}

				if (this.filterType.strRoundCode.length() != 0) {
					sResult = sResult + " and a.FRoundCode like '"
							+ filterType.strRoundCode.replaceAll("'", "''")
							+ "%'";
				}
				if (this.filterType.strDesc.length() != 0) {
					sResult = sResult + " and a.FDesc like '"
							+ filterType.strDesc.replaceAll("'", "''") + "%'";
				}
				if (this.filterType.interestWay != 99) {
					sResult += " and a.FInterestWay ="
							+ this.filterType.interestWay;
				}
				if (this.filterType.accAttr != 99) {
					sResult += " and a.FAccAttr =" + this.filterType.accAttr;
				}
				if (this.filterType.strDepDurCode != null
						&& this.filterType.strDepDurCode.length() != 0) {
					sResult += " and a.FDepDurCode ="
							+ this.filterType.strDepDurCode;
				}
				//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
				if (this.filterType.recPayCode != null
						&& this.filterType.recPayCode.length() != 0) {
					sResult += " and a.FRECPAYCODE ="
							+ dbl.sqlString(this.filterType.recPayCode);
				}
				//---------------end---------
				if (this.filterType.interestAlg != 99) {
					sResult += " and a.FInterestAlg ="
							+ this.filterType.interestAlg;
				}
				if (this.filterType.qsAccNum.length() != 0) {
					sResult = sResult + " and a.FQSAccNum = "
							+ filterType.qsAccNum;
				}
			}
		} catch (Exception e) {
			throw new YssException("筛选现金帐户数据出错", e);
		}
		return sResult;
	}

	/**
	 * getAllSetting
	 * 
	 * @return String
	 */
	public String getAllSetting() {
		return "";
	}

	public String builderListViewData(String strSql) throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sVocStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		try {
			if (this.bOverGroup) { // MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
				// panjunfang add 20090813
				sHeader = this.getListView3Headers();
			} else {
				sHeader = this.getListView1Headers();
			}
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if (this.bOverGroup) { // MS00001
					// QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
					// panjunfang add 20090813
					bufShow.append(
							super.buildRowShowStr(rs, this
									.getListView3ShowCols())).append(
							YssCons.YSS_LINESPLITMARK);
				} else {
					bufShow.append(
							super.buildRowShowStr(rs, this
									.getListView1ShowCols())).append(
							YssCons.YSS_LINESPLITMARK);
				}
				setSecurityAttr(rs);
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

			VocabularyBean vocabulary = new VocabularyBean();
			vocabulary.setYssPub(pub);
			sVocStr = vocabulary.getVoc(YssCons.YSS_CNT_INTCYL + ","
					+ YssCons.YSS_CNT_INTORG + "," + YssCons.YSS_CNT_STATE
					+ "," + YssCons.YSS_PCA_INTERESTWAY + ","+ YssCons.Yss_Acc_ACCSORT + ","  //modified by zhaoxianlin 20121217 STORY #3384 外管局报表 增加账户分类
					+ YssCons.Yss_Acc_ACCATTR + "," + YssCons.YSS_PCA_INTERESTALG);
			if (this.bOverGroup) { // MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
				// panjunfang add 20090813
				return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
						+ "\r\f" + this.getListView3ShowCols() + "\r\f" + "voc"
						+ sVocStr;
			} else {
				return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
						+ "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc"
						+ sVocStr;
			}
		} catch (Exception e) {
			throw new YssException("获取现金帐户信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取现金帐户信息维护数据 此方法已被修改 修改时间：2008年2月23号 修改人：单亮
	 * 原方法的功能：查询出费用连接数据并以一定格式显示，但不能显示回收站的数据 新方法的功能：原功能的基础上，可以显示回收站的数据
	 * 修改原因：原方法能显示回收站的数据
	 */

	public String getListViewData1() throws YssException {
		String strSql = "";
		strSql = "select y.* from "
			    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//				+ "(select FCashAccCode,FCheckState,max(FStartDate) as FStartDate from "
//				+ pub.yssGetTableName("Tb_Para_CashAccount")
//				+ " "
//				+ " where FStartDate <= "
//				+ dbl.sqlDate(new java.util.Date())
//				+
//				// 修改后的代码
//				// ----------------------------
//				" group by FCashAccCode,FCheckState) x join"
			    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
				+
				// ----------------------------
				" (select a.*, l.FVocName as FStateValue, m.FVocName as FInterestCycleValue, n.FVocName as FInterestOriginValue,"            //story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
				+ "b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FAccTypeName, e.FSubAccTypeName,nn.FVOCNAME as FAccAttrName,rp.FRECEIVERNAME as frecpayname,"
				+ "f.FBankName,g.FCurrencyName, h.FPortName,j.FPeriodName,k.FRoundName, i.FFormulaName ,ii.FFormulaName as FLoanName ,xx.FDepDurName,nn.FVOCNAME as InterestWay, "
				+ " w.FFormulaName as FPerExpName, nnnn.FVOCName as FAccSortName, "
				/*added by yeshenghong 2013-6-8 Story 3958 */
				+ " d1.fcnvconent as FCashInBank, d2.fcnvconent as FReceivalbes, " 
                + " d3.fcnvconent as FPayables,d4.fcnvconent as FInterest, d5.fcnvconent as FRevenue from "  
                /*end by yeshenghong 2013-6-8 Story 3958 */
                //添加nnnn.FVOCcode  modified by zhaoxianlin 20121217 STORY #3384 外管局报表
				+ pub.yssGetTableName("Tb_Para_CashAccount") + "  a "
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
				//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
				+" left join (select FRECEIVERCODE,FRECEIVERNAME from "+pub.yssGetTableName("TB_PARA_RECEIVER")+" where fcheckstate=1 ) rp on rp.FRECEIVERCODE=a.FRECPAYCODE"
				//-------------end------------
				+ " left join "
				+ pub.yssGetTableName("Tb_Para_DepositDuration")
				+ " xx on a.FDepDurCode =xx.FDepDurCode "
				+ " left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) d on a.FAccType = d.FAccTypeCode"
				+ " left join (select FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType where FCheckState = 1) e on a.FSubAccType = e.FSubAccTypeCode"
				+ " left join (select FBankCode,FBankName from "
				+ pub.yssGetTableName("Tb_Para_Bank")
				+ " where FCheckState = 1) f on a.FBankCode = f.FBankCode"
				+ " left join (select FCuryCode as FCurrencyCode,FCuryName as FCurrencyName from "
				+ pub.yssGetTableName("Tb_Para_Currency")
				+ " where FCheckState = 1) g on a.FCuryCode = g.FCurrencyCode"
				+   " left join (select FFormulaCode,FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState=1) w on a.FPerExpCode=w.FFormulaCode" 
				+ " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from "
				+ pub.yssGetTableName("Tb_Para_Portfolio")
				+ " o where FCheckState = 1 "//edit by songjie 2011.03.14 不以最大的启用日期查询数据
				//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//				+ "(select FPortCode,max(FStartDate) as FStartDate from "
//				+ pub.yssGetTableName("Tb_Para_Portfolio")
//				+ " "
//				+ " where FStartDate <= "
//				+ dbl.sqlDate(new java.util.Date())
//				+ " and FCheckState = 1 and FASSETGROUPCODE = "
//				+ dbl.sqlString(pub.getAssetGroupCode())
//				+ " group by FPortCode) p "
				//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
				+ ") h on a.FPortCode = h.FPortCode"//edit by songjie 2011.03.14 不以最大的启用日期查询数据
				+ " left join (select FFormulaCode, FFormulaName from "
				+ pub.yssGetTableName("Tb_Para_Performula")
				+ " where FCheckState = 1) i on a.FFormulaCode = i.FFormulaCode"
				//by guyichuan 20110520 
				+ " left join (select FFormulaCode, FFormulaName from "
				+ pub.yssGetTableName("Tb_Para_Performula")
				+ " where FCheckState = 1) ii on a.FLoanCode = ii.FFormulaCode"
				//---end ----
				+ " left join (select FPeriodCode,FPeriodName from "
				+ pub.yssGetTableName("Tb_Para_Period")
				+ " where FCheckState = 1 ) j on a.FPeriodCode = j.FPeriodCode"
				+ " left join (select FRoundCode,FRoundName from "
				+ pub.yssGetTableName("Tb_Para_Rounding")
				+ "  where FCheckState = 1) k on a.FRoundCode = k.FRoundCode "
				/*added by yeshenghong 2013-6-8 Story 3958 */
				+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") 
				+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_CashInBank) + ") d1 "
				+ "  on a.fcashacccode = d1.findcode "
				+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
				+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherReceivalbes) + ") d2 "
				+ "  on a.fcashacccode = d2.findcode "
				+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
				+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherPayables) + ") d3 "
				+ "  on a.fcashacccode = d3.findcode "
				+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
				+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_Interest) + ") d4 "
				+ "  on a.fcashacccode = d4.findcode "
				+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")  
				+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_Revenue) + ") d5 "
				+ "  on a.fcashacccode = d5.findcode "
				/*end by yeshenghong 2013-6-8 Story 3958 */
				+ "  left join Tb_Fun_Vocabulary l on "
				+ dbl.sqlToChar("a.FState")
				+ " = l.FVocCode and l.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_CNT_STATE)
				+ " left join Tb_Fun_Vocabulary m on "
				+ dbl.sqlToChar("a.FInterestCycle")
				+ " = m.FVocCode and m.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_CNT_INTCYL)
				+ " left join Tb_Fun_Vocabulary n on "
				+ dbl.sqlToChar("a.FInterestOrigin")
				+ " = n.FVocCode and n.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_CNT_INTORG)
				+ " "
				+ " left join Tb_Fun_Vocabulary nn on "
				+ dbl.sqlToChar("a.FInterestWay")
				+ "= nn.FVocCode  and nn.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_PCA_INTERESTWAY)
				+ " "
				+ " left join Tb_Fun_Vocabulary nnn on "
				+ dbl.sqlToChar("a.FAccAttr")
				+ " = nnn.FVocCode  and nnn.FVocTypeCode = "
				+ dbl.sqlString(YssCons.Yss_Acc_ACCATTR)
				+ " "
				//--start---modified by zhaoxianlin 20121217 STORY #3384 外管局报表
				+ " left join Tb_Fun_Vocabulary nnnn on "
				+ dbl.sqlToChar("a.FAccSort")
				+ " = nnnn.FVocCode  and nnnn.FVocTypeCode = "
				+ dbl.sqlString(YssCons.Yss_Acc_ACCSORT)
				+ " "
				//--end---modified by zhaoxianlin 20121217 STORY #3384 外管局报表
				+ buildFilterSql()
				+ ") y "//edit by songjie 2011.03.14 不以最大的启用日期查询数据
				+ " order by y.FCheckState, y.FCreateTime desc";
		return this.builderListViewData(strSql);
	}

	/**
	 * getListViewData4 获取现金帐户设置的全部数据
	 * 
	 * @return String
	 */
	public String getListViewData4() throws YssException {
		String strSql = "";

		strSql = "select a.*,xx.FDepDurName, l.FVocName as FStateValue, m.FVocName as FInterestCycleValue, n.FVocName as FInterestOriginValue,"
				+ "b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FAccTypeName, e.FSubAccTypeName,rp.FRECEIVERNAME as frecpayname,"//story 1911 add by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
				+ " f.FBankName,g.FCurrencyName, h.FPortName, j.FPeriodName, i.FFormulaName,ii.FFormulaName as FLoanName, k.FRoundName,nnn.FVOCNAME as InterestWay,w.FFormulaName as FPerExpName,nn.FVOCName as FAccSortName, "  //add by zhaoxianlin stroy #3384 增加账户分类
				/*added by yeshenghong 2013-6-8 Story 3958 */
				+ " d1.fcnvconent as FCashInBank, d2.fcnvconent as FReceivalbes, " 
                + " d3.fcnvconent as FPayables,d4.fcnvconent as FInterest, d5.fcnvconent as FRevenue "  
                /*end by yeshenghong 2013-6-8 Story 3958 */
				+ " from "
				+ pub.yssGetTableName("Tb_Para_CashAccount")
				+ " a "
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
				//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
				+"  left join (select FRECEIVERCODE,FRECEIVERNAME from "+pub.yssGetTableName("TB_PARA_RECEIVER")+" where fcheckstate=1 ) rp on rp.FRECEIVERCODE=a.FRECPAYCODE"
				//-------------end------------
				+ " left join "
				+ pub.yssGetTableName("Tb_Para_DepositDuration")
				+ " xx on a.FDepDurCode =xx.FDepDurCode "
				+ " left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) d on a.FAccType = d.FAccTypeCode"
				+ " left join (select FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType where FCheckState = 1) e on a.FSubAccType = e.FSubAccTypeCode"
				+ " left join (select FBankCode,FBankName from "
				+ pub.yssGetTableName("Tb_Para_Bank")
				+ " where FCheckState = 1) f on a.FBankCode = f.FBankCode"
				+ " left join (select FCuryCode as FCurrencyCode,FCuryName as FCurrencyName from "
				+ pub.yssGetTableName("Tb_Para_Currency")
				+ " where FCheckState = 1) g on a.FCuryCode = g.FCurrencyCode"
				+ " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from "
				+ pub.yssGetTableName("Tb_Para_Portfolio")
				+ " o where FCheckState = 1 "//edit by songjie 2011.03.14 不以最大的启用日期查询数据
				//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//				+ "(select FPortCode,max(FStartDate) as FStartDate from "
//				+ pub.yssGetTableName("Tb_Para_Portfolio")
//				+ " "
//				+ " where FStartDate <= "
//				+ dbl.sqlDate(new java.util.Date())
//				+ " and FCheckState = 1 and FASSETGROUPCODE = "
//				+ dbl.sqlString(pub.getAssetGroupCode())
//				+ " group by FPortCode) p "
				//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
				+ ") h on a.FPortCode = h.FPortCode"//edit by songjie 2011.03.14 不以最大的启用日期查询数据
				+ " left join (select FFormulaCode, FFormulaName from "
				+ pub.yssGetTableName("Tb_Para_Performula")
				+ " where FCheckState = 1) i on a.FFormulaCode = i.FFormulaCode"
				//by guyichuan 20110520 STORY #561
				+ " left join (select FFormulaCode, FFormulaName from "
				+ pub.yssGetTableName("Tb_Para_Performula")
				//edit by songjie 2011.09.13 点击现金账户设置的全部显示按钮报错
				+ " where FCheckState = 1) ii on ii.FFormulaCode = a.FLoanCode"
				//--end-STORY #561------
				+ " left join (select FPeriodCode,FPeriodName from "
				+ pub.yssGetTableName("Tb_Para_Period")
				+ " where FCheckState = 1 ) j on a.FPeriodCode = j.FPeriodCode"
				+ " left join (select FRoundCode,FRoundName from "
				+ pub.yssGetTableName("Tb_Para_Rounding")
				+ " where FCheckState = 1) k on a.FRoundCode = k.FRoundCode"
				/*added by yeshenghong 2013-6-8 Story 3958 */
				+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") 
				+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_CashInBank) + ") d1 "
				+ "  on a.fcashacccode = d1.findcode "
				+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
				+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherReceivalbes) + ") d2 "
				+ "  on a.fcashacccode = d2.findcode "
				+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
				+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherPayables) + ") d3 "
				+ "  on a.fcashacccode = d3.findcode "
				+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
				+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_Interest) + ") d4 "
				+ "  on a.fcashacccode = d4.findcode "
				+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")  
				+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_Revenue) + ") d5 "
				+ "  on a.fcashacccode = d5.findcode "
				/*end by yeshenghong 2013-6-8 Story 3958 */
				+ " left join (select FFormulaCode,FFormulaName from "
				+ pub.yssGetTableName("Tb_Para_Performula")
				+ " where FCheckState = 1) w on FPerExpCode = w.FFormulaCode"
				+ " left join Tb_Fun_Vocabulary l on "
				+ dbl.sqlToChar("a.FState")
				+ " = l.FVocCode and l.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_CNT_STATE)
				+ " left join Tb_Fun_Vocabulary m on "
				+ dbl.sqlToChar("a.FInterestCycle")
				+ " = m.FVocCode and m.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_CNT_INTCYL)
				+ " left join Tb_Fun_Vocabulary n on "
				+ dbl.sqlToChar("a.FInterestOrigin")
				+ " = n.FVocCode and n.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_CNT_INTORG)
				//-----add by zhaoxianlin stroy #3384 
				+ " left join Tb_Fun_Vocabulary nnn on "
				+ dbl.sqlToChar("a.FInterestWay")
				+ " = nnn.FVocCode and nnn.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_PCA_INTERESTWAY)
				
				+ " left join Tb_Fun_Vocabulary nn on "
				+ dbl.sqlToChar("a.FAccSort")
				+ " = nn.FVocCode and nn.FVocTypeCode = "
				+ dbl.sqlString(YssCons.Yss_Acc_ACCSORT)
				//
				+ buildFilterSql()
				+ " order by a.FCheckState, a.FCreateTime desc";
		return this.builderListViewData(strSql);
	}

	/**
	 * getListViewData2 获取已审核的现金帐户信息维护数据
	 * 
	 * @return String
	 */
	public String getListViewData2() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		String strSql = "";
		try {
			//add by zhouxiang MS01629  2010.10.13 招商基金资金账号信息查询功能
			//edit by songjie 2011.03.15 不显示启用日期 
			sHeader = "帐户代码\t帐户名称\t组合代码\t组合名称\t币种代码\t币种名称\t帐户类型代码\t帐户类型名称\t类型明细代码\t类型明细名称\t开户银行代码\t开户银行名称\t银行帐号\t银行账号名称\t期限代码\t期限名称";
			if (this.strIsOnlyColumns.equalsIgnoreCase("0")) {
				strSql = "select y.* from "
					    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//						+ "(select FCashAccCode,max(FStartDate) as FStartDate from "
//						+ pub.yssGetTableName("Tb_Para_CashAccount")
//						+ " "
//						+ " where FStartDate <= "
//						+ dbl.sqlDate(new java.util.Date())
//						+ "and FCheckState = 1 and FState =0 group by FCashAccCode) x join"
					    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
						+ " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FAccTypeName, e.FSubAccTypeName,rp.FRECEIVERNAME as frecpayname,"//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
						+ "  f.FBankName,g.FCurrencyName, h.FPortName,j.FPeriodName, i.FFormulaName,ii.FFormulaName as FLoanName, k.FRoundName,xx.FDepDurName, "
						/*added by yeshenghong 2013-6-8 Story 3958 */
						+ " d1.fcnvconent as FCashInBank, d2.fcnvconent as FReceivalbes, " 
		                + " d3.fcnvconent as FPayables,d4.fcnvconent as FInterest, d5.fcnvconent as FRevenue "  
		                /*end by yeshenghong 2013-6-8 Story 3958 */
						+ " from "
						//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						+ " (select * from " + pub.yssGetTableName("Tb_Para_CashAccount") + " where FCheckState = 1 and FState = 0)"
						+ " a "
						+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
						+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
						//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
						+" left join (select FRECEIVERCODE,FRECEIVERNAME from "+pub.yssGetTableName("TB_PARA_RECEIVER")+" where fcheckstate=1 ) rp on rp.FRECEIVERCODE=a.FRECPAYCODE"
						//-------------end------------
						+ " left join "
						+ pub.yssGetTableName("Tb_Para_DepositDuration")
						+ " xx on a.FDepDurCode =xx.FDepDurCode "
						+ " left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) d on a.FAccType = d.FAccTypeCode"
						+ " left join (select FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType where FCheckState = 1) e on a.FSubAccType = e.FSubAccTypeCode"
						+ " left join (select FBankCode,FBankName from  "
						+ pub.yssGetTableName("Tb_para_Bank")
						+ " where FCheckState = 1) f on a.FBankCode = f.FBankCode"
						+ " left join (select FCuryCode as FCurrencyCode,FCuryName as FCurrencyName from "
						+ pub.yssGetTableName("Tb_Para_Currency")
						+ " where FCheckState = 1) g on a.FCuryCode = g.FCurrencyCode"
						+ " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						+ " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode())
						//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//						+ "(select FPortCode,max(FStartDate) as FStartDate from "
//						+ pub.yssGetTableName("Tb_Para_Portfolio")
//						+ " "
//						+ " where FStartDate <= "
//						+ dbl.sqlDate(new java.util.Date())
//						+ " and FCheckState = 1 and FASSETGROUPCODE = "
//						+ dbl.sqlString(pub.getAssetGroupCode())
//						+ " group by FPortCode) p "
						//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
						+ " ) h on a.FPortCode = h.FPortCode"//
						+ " left join (select FFormulaCode, FFormulaName from "
						+ pub.yssGetTableName("Tb_Para_Performula")
						+ " where FCheckState = 1) i on a.FFormulaCode = i.FFormulaCode"
						//by guyichuan 20110520 STORY #561
						+ " left join (select FFormulaCode, FFormulaName from "
						+ pub.yssGetTableName("Tb_Para_Performula")
						+ " where FCheckState = 1) ii on a.FLoanCode = ii.FFormulaCode"
						//---end-STORY #561--------
						+ " left join (select FPeriodCode, FPeriodName from "
						+ pub.yssGetTableName("Tb_Para_Period")
						+ " where FCheckState = 1) j on a.FPeriodCode = j.FPeriodCode"
						+ " left join (select FRoundCode,FRoundName from "
						+ pub.yssGetTableName("Tb_Para_Rounding")
						+ " where FCheckState = 1) k on a.FRoundCode = k.FRoundCode"
						/*added by yeshenghong 2013-6-8 Story 3958 */
						+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") 
						+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_CashInBank) + ") d1 "
						+ "  on a.fcashacccode = d1.findcode "
						+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
						+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherReceivalbes) + ") d2 "
						+ "  on a.fcashacccode = d2.findcode "
						+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
						+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherPayables) + ") d3 "
						+ "  on a.fcashacccode = d3.findcode "
						+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
						+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_Interest) + ") d4 "
						+ "  on a.fcashacccode = d4.findcode "
						+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")  
						+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_Revenue) + ") d5 "
						+ "  on a.fcashacccode = d5.findcode "
						/*end by yeshenghong 2013-6-8 Story 3958 */
						+ buildFilterSql() + this.filterConditions //modify huangqirong 2013-01-30 story #3510
						+ ") y "//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						 //add by huangqirong 2011-06-25 story #937
						//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
//						+" where y.FPortcode in (select distinct(tsu.fportcode) as fportcode from(select fportcode from Tb_Sys_Userright"
//  		        		+" where fusercode ="+dbl.sqlString(pub.getUserCode())					    
//  					    +" and frighttype = 'port'"
//  					    +" and FOPERTYPES like '%brow%'"
//  					    +" and frightcode = 'cashtransfer') tsu"
//  					    +" inner join "+pub.yssGetTableName("tb_Para_Portfolio")
//  					    +" tpp on tpp.fportcode=tsu.fportcode"
//  					    +" where tpp.fenabled=1"
//  					    +" and tpp.FCheckState=1)"
						//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
  					    //---end---
						+ " order by y.FCheckState, y.FCreateTime desc";

				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					bufShow.append((rs.getString("FCashAccCode") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FCashAccName") + "").trim())
							.append("\t");

					bufShow.append((rs.getString("FPortCode") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FPortName") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FCuryCode") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FCurrencyName") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FAccType") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FAccTypeName") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FSubAccType") + "").trim())
							.append("\t");
					bufShow.append(
							(rs.getString("FSubAccTypeName") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FBankCode") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FBankName") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FBankAccount") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FBankAccName") + "").trim())//add by zhouxiang MS01629 2010.10.13 招商基金资金账号信息查询功能
					.append("\t");
					//----delete by songjie 2011.03.15 不显示启用日期----//
//					bufShow.append(
//							(YssFun.formatDate(rs.getDate("FStartDate"),
//									YssCons.YSS_DATEFORMAT) + "").trim())
//							.append("\t");
					//----delete by songjie 2011.03.15 不显示启用日期----//
					bufShow.append((rs.getString("FDepDurCode") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FDepDurName") + "").trim())
							.append(YssCons.YSS_LINESPLITMARK);
					setSecurityAttr(rs);
					bufAll.append(this.buildRowStr()).append(
							YssCons.YSS_LINESPLITMARK);
				}
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
			throw new YssException("获取现金帐户信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * getListViewData3 获取现金帐户连接信息
	 * 
	 * @return String
	 */
	public String getListViewData3() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sVocStr = "";
		String sAllDataStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		String strSql = "";
		try {
			//edit by songjie 2011.03.15 不显示启用日期 
			sHeader = "帐户代码\t帐户名称\t组合代码\t组合名称\t币种代码\t币种名称\t帐户类型代码\t帐户类型名称\t类型明细代码\t类型明细名称\t开户银行代码\t开户银行名称\t银行帐号\t期限代码\t期限名称";
			if (this.strIsOnlyColumns.equalsIgnoreCase("0")) {

				strSql = "select y.* from "
					    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//						+ "(select FCashAccCode,max(FStartDate) as FStartDate from "
//						+ pub.yssGetTableName("Tb_Para_CashAccount")
//						+ " "
//						+ " where FStartDate <= "
//						+ dbl.sqlDate(new java.util.Date())
//						+ "and FCheckState = 1 and FState =0 group by FCashAccCode) x join"
					    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
						+ " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FAccTypeName, e.FSubAccTypeName,rp.FRECEIVERNAME as frecpayname,"
						+ "  f.FBankName,g.FCurrencyName, h.FPortName,j.FPeriodName, i.FFormulaName,ii.FFormulaName as FLoanName, k.FRoundName,xx.FDepDurName, "
						/*added by yeshenghong 2013-6-8 Story 3958 */
						+ " d1.fcnvconent as FCashInBank, d2.fcnvconent as FReceivalbes, " 
		                + " d3.fcnvconent as FPayables,d4.fcnvconent as FInterest, d5.fcnvconent as FRevenue "  
		                /*end by yeshenghong 2013-6-8 Story 3958 */
						+ " from "
						//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						+ "(select * from " + pub.yssGetTableName("Tb_Para_CashAccount") + " where FCheckState = 1 and FState = 0)"
						+ " a "
						+ (strSql.trim().length() > 0 ? strSql : "")
						+ // 现金帐户连接
						" left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
						+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
						//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
						+" left join (select FRECEIVERCODE,FRECEIVERNAME from "+pub.yssGetTableName("TB_PARA_RECEIVER")+" where fcheckstate=1 ) rp on rp.FRECEIVERCODE=a.FRECPAYCODE"
						//-------------end------------
						+ " left join "
						+ pub.yssGetTableName("Tb_Para_DepositDuration")
						+ " xx on a.FDepDurCode =xx.FDepDurCode "
						+ " left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) d on a.FAccType = d.FAccTypeCode"
						+ " left join (select FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType where FCheckState = 1) e on a.FSubAccType = e.FSubAccTypeCode"
						+ " left join (select FBankCode,FBankName from  "
						+ pub.yssGetTableName("Tb_para_Bank")
						+ " where FCheckState = 1) f on a.FBankCode = f.FBankCode"
						+ " left join (select FCuryCode as FCurrencyCode,FCuryName as FCurrencyName from "
						+ pub.yssGetTableName("Tb_Para_Currency")
						+ " where FCheckState = 1) g on a.FCuryCode = g.FCurrencyCode"
						+ " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						+ " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode())
						//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//						+ "(select FPortCode,max(FStartDate) as FStartDate from "
//						+ pub.yssGetTableName("Tb_Para_Portfolio")
//						+ " "
//						+ " where FStartDate <= "
//						+ dbl.sqlDate(new java.util.Date())
//						+ " and FCheckState = 1 and FASSETGROUPCODE = "
//						+ dbl.sqlString(pub.getAssetGroupCode())
//						+ " group by FPortCode) p "
						//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
						+ " ) h on a.FPortCode = h.FPortCode"//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						+ " left join (select FFormulaCode, FFormulaName from "
						+ pub.yssGetTableName("Tb_Para_Performula")
						+ " where FCheckState = 1) i on a.FFormulaCode = i.FFormulaCode"
						//by guyichuan 20110520 STORY #561
						+ " left join (select FFormulaCode, FFormulaName from "
						+ pub.yssGetTableName("Tb_Para_Performula")
						+ " where FCheckState = 1) ii on a.FLoanCode = ii.FFormulaCode"
						//---end-STORY #561--------
						+ " left join (select FPeriodCode, FPeriodName from "
						+ pub.yssGetTableName("Tb_Para_Period")
						+ " where FCheckState = 1) j on a.FPeriodCode = j.FPeriodCode"
						+ " left join (select FRoundCode,FRoundName from "
						+ pub.yssGetTableName("Tb_Para_Rounding")
						+ " where FCheckState = 1) k on a.FRoundCode = k.FRoundCode"
						/*added by yeshenghong 2013-6-8 Story 3958 */
						+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") 
						+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_CashInBank) + ") d1 "
						+ "  on a.fcashacccode = d1.findcode "
						+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
						+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherReceivalbes) + ") d2 "
						+ "  on a.fcashacccode = d2.findcode "
						+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
						+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherPayables) + ") d3 "
						+ "  on a.fcashacccode = d3.findcode "
						+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
						+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_Interest) + ") d4 "
						+ "  on a.fcashacccode = d4.findcode "
						+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")  
						+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_Revenue) + ") d5 "
						+ "  on a.fcashacccode = d5.findcode "
						/*end by yeshenghong 2013-6-8 Story 3958 */
						+ buildFilterSql()
						+ ") y "//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
//						 //add by huangqirong 2011-06-25 story #937
//						+" where y.FPortcode in (select distinct(tsu.fportcode) as fportcode from(select fportcode from Tb_Sys_Userright"
//  		        		+" where fusercode ="+dbl.sqlString(pub.getUserCode())					    
//  					    +" and frighttype = 'port'"
//  					    +" and FOPERTYPES like '%brow%'"
//  					    +" and frightcode = 'cashtransfer') tsu"
//  					    +" inner join "+pub.yssGetTableName("tb_Para_Portfolio")
//  					    +" tpp on tpp.fportcode=tsu.fportcode"
//  					    +" where tpp.fenabled=1"
//  					    +" and tpp.FCheckState=1)"
//  					    //---end---
						//---delete by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
						+ " order by y.FCheckState, y.FCreateTime desc";

				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					bufShow.append((rs.getString("FCashAccCode") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FCashAccName") + "").trim())
							.append("\t");

					bufShow.append((rs.getString("FPortCode") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FPortName") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FCuryCode") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FCurrencyName") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FAccType") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FAccTypeName") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FSubAccType") + "").trim())
							.append("\t");
					bufShow.append(
							(rs.getString("FSubAccTypeName") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FBankCode") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FBankName") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FBankAccount") + "").trim())
							.append("\t");
					//----delete by songjie 2011.03.15 不显示启用日期----//
//					bufShow.append(
//							(YssFun.formatDate(rs.getDate("FStartDate"),
//									YssCons.YSS_DATEFORMAT) + "").trim())
//							.append("\t");
					//----delete by songjie 2011.03.15 不显示启用日期----//
					bufShow.append((rs.getString("FDepDurCode") + "").trim())
							.append("\t");
					bufShow.append((rs.getString("FDepDurName") + "").trim())
							.append(YssCons.YSS_LINESPLITMARK);
					setSecurityAttr(rs);
					bufAll.append(this.buildRowStr()).append(
							YssCons.YSS_LINESPLITMARK);
				}
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}

			VocabularyBean vocabulary = new VocabularyBean();
			vocabulary.setYssPub(pub);
			sVocStr = vocabulary.getVoc(YssCons.YSS_CNT_INTCYL + ","
					+ YssCons.YSS_CNT_INTORG + "," + YssCons.YSS_CNT_STATE
					+ "," + YssCons.Yss_Acc_ACCATTR);

			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc"
					+ sVocStr;

		} catch (Exception e) {
			throw new YssException("获取现金帐户信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * getSetting
	 * 
	 * @return IParaSetting
	 */
	public IDataSetting getSetting() {
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "select a.*,b.FDepDurName from (select * from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where FCashAccCode = "
					+ dbl.sqlString(this.strCashAcctCode)
					//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//					+ " and FStartDate = (select max(FStartDate) from "
//					+ pub.yssGetTableName("Tb_Para_CashAccount")
//					+ " where FCashAccCode = "
//					+ dbl.sqlString(this.strCashAcctCode)
					//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
					+ ") a left join "//edit by songjie 2011.03.14 不以最大的启用日期查询数据
					+ pub.yssGetTableName("Tb_Para_DepositDuration")
					+ " b on a.fdepdurcode = b.fdepdurcode";
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				this.strCashAcctCode = rs.getString("FCashAccCode") + "";
				this.strCashAcctName = rs.getString("FCashAccName") + "";
				this.dtStartDate = rs.getDate("FStartDate");
				this.strAcctTypeCode = rs.getString("FAccType") + "";
				this.strSubAcctTypeCode = rs.getString("FSubAccType") + "";
				this.strBankCode = rs.getString("FBankCode") + "";
				this.strBankAccount = rs.getString("FBankAccount") + "";
				this.strCurrencyCode = rs.getString("FCuryCode") + "";
				this.strPortCode = rs.getString("FPortCode") + "";
				this.strAcctState = rs.getString("FState") + "";
				this.interestCycle = rs.getInt("FInterestCycle");
				this.interestOrigin = rs.getInt("FInterestOrigin");
				this.strFormulaCode = rs.getString("FFormulaCode") + "";
				this.strRoundCode = rs.getString("FRoundCode") + "";
				this.strPeriodCode = rs.getString("FPeriodCode") + "";
				this.strDesc = rs.getString("FDesc") + "";
				this.checkStateId = rs.getInt("FCheckState");
				this.checkStateName = YssFun.getCheckStateName(rs
						.getInt("FCheckState"));
				this.creatorCode = rs.getString("FCreator") + "";
				this.creatorTime = rs.getString("FcreateTime") + "";
				this.checkUserCode = rs.getString("FCheckUser") + "";
				this.checkTime = rs.getString("FCheckTime") + "";
				this.strDepDurCode = rs.getString("FDepDurCode") + "";
				this.strDepDurName = rs.getString("FDepDurName") + "";
				this.accAttr = rs.getInt("FAccAttr");
			}
		} catch (Exception e) {
			throw new YssException("获取现金帐户信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			return null;
		}
	}

	/**
	 * getTreeViewData1
	 * 
	 * @return String
	 */
    //modified by liubo.Story #1916
    //跨组合群的调度方案设置
    //==========================================
	public String getTreeViewData1() throws YssException{
		String strSql = "";
		
		String[] strAllAssetGroup = getAllAssetGroup().split("\t");
		
		strSql = "select * from (";
		for (int i = 0; i < strAllAssetGroup.length; i++)
		{
			strSql = strSql + " select y.*,'" + strAllAssetGroup[i] + "' as FAssetGroupCode from " + 
					" (select a.*, l.FVocName as FStateValue, m.FVocName as FInterestCycleValue, n.FVocName as FInterestOriginValue,"
					+ "b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FAccTypeName, e.FSubAccTypeName,nn.FVOCNAME as FAccAttrName,"
					+ "f.FBankName,g.FCurrencyName, h.FPortName,j.FPeriodName,k.FRoundName, i.FFormulaName ,ii.FFormulaName as FLoanName ,xx.FDepDurName,nn.FVOCNAME as InterestWay, "
					/*added by yeshenghong 2013-6-8 Story 3958 */
					+ " d1.fcnvconent as FCashInBank, d2.fcnvconent as FReceivalbes, " 
	                + " d3.fcnvconent as FPayables,d4.fcnvconent as FInterest, d5.fcnvconent as FRevenue "  
	                /*end by yeshenghong 2013-6-8 Story 3958 */
					+ " from "
					+ "Tb_" + strAllAssetGroup[i] + "_Para_CashAccount"
					+ "  a "
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
					+ " left join "
					+ "Tb_" + strAllAssetGroup[i] + "_Para_DepositDuration"
					+ " xx on a.FDepDurCode =xx.FDepDurCode "
					+ " left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) d on a.FAccType = d.FAccTypeCode"
					+ " left join (select FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType where FCheckState = 1) e on a.FSubAccType = e.FSubAccTypeCode"
					+ " left join (select FBankCode,FBankName from "
					+ "Tb_" + strAllAssetGroup[i] + "_Para_Bank"
					+ " where FCheckState = 1) f on a.FBankCode = f.FBankCode"
					+ " left join (select FCuryCode as FCurrencyCode,FCuryName as FCurrencyName from "
					+ "Tb_" + strAllAssetGroup[i] + "_Para_Currency"
					+ " where FCheckState = 1) g on a.FCuryCode = g.FCurrencyCode"
					+ " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from "
					+ "Tb_" + strAllAssetGroup[i] + "_Para_Portfolio"
					+ " o where FCheckState = 1 "
					+ ") h on a.FPortCode = h.FPortCode"
					+ " left join (select FFormulaCode, FFormulaName from "
					+ "Tb_" + strAllAssetGroup[i] + "_Para_Performula"
					+ " where FCheckState = 1) i on a.FFormulaCode = i.FFormulaCode"
					+ " left join (select FFormulaCode, FFormulaName from "
					+ "Tb_" + strAllAssetGroup[i] + "_Para_Performula"
					+ " where FCheckState = 1) ii on a.FLoanCode = ii.FFormulaCode"
					+ " left join (select FPeriodCode,FPeriodName from "
					+ "Tb_" + strAllAssetGroup[i] + "_Para_Period"
					+ " where FCheckState = 1 ) j on a.FPeriodCode = j.FPeriodCode"
					+ " left join (select FRoundCode,FRoundName from "
					+ "Tb_" + strAllAssetGroup[i] + "_Para_Rounding"
					+ "  where FCheckState = 1) k on a.FRoundCode = k.FRoundCode"
					/*added by yeshenghong 2013-6-8 Story 3958 */
					+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict") 
					+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_CashInBank) + ") d1 "
					+ "  on a.fcashacccode = d1.findcode "
					+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
					+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherReceivalbes) + ") d2 "
					+ "  on a.fcashacccode = d2.findcode "
					+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
					+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherPayables) + ") d3 "
					+ "  on a.fcashacccode = d3.findcode "
					+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")
					+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_Interest) + ") d4 "
					+ "  on a.fcashacccode = d4.findcode "
					+ "  left join (select findcode, fcnvconent from " + pub.yssGetTableName("tb_vch_dict")  
					+ "  where FCheckState = 1 and fportcode = ' ' and fdictcode = " + dbl.sqlString(YssCons.YSS_VCH_DICT_Revenue) + ") d5 "
					+ "  on a.fcashacccode = d5.findcode "
					/*end by yeshenghong 2013-6-8 Story 3958 */
					+ " left join Tb_Fun_Vocabulary l on "
					+ dbl.sqlToChar("a.FState")
					+ " = l.FVocCode and l.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_CNT_STATE)
					+ " left join Tb_Fun_Vocabulary m on "
					+ dbl.sqlToChar("a.FInterestCycle")
					+ " = m.FVocCode and m.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_CNT_INTCYL)
					+ " left join Tb_Fun_Vocabulary n on "
					+ dbl.sqlToChar("a.FInterestOrigin")
					+ " = n.FVocCode and n.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_CNT_INTORG)
					+ " "
					+ " left join Tb_Fun_Vocabulary nn on "
					+ dbl.sqlToChar("a.FInterestWay")
					+ "= nn.FVocCode  and nn.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_PCA_INTERESTWAY)
					+ " "
					+ " left join Tb_Fun_Vocabulary nnn on "
					+ dbl.sqlToChar("a.FAccAttr")
					+ " = nnn.FVocCode  and nnn.FVocTypeCode = "
					+ dbl.sqlString(YssCons.Yss_Acc_ACCATTR)
					+ " "
					+ buildFilterSql()
					+ " union";
		}
		
		strSql = strSql.substring(0,strSql.length() - 5);
    	strSql = strSql + ") allData order by allData.FAssetGroupCode,allData.FCreateTime";
		
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sVocStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		try {
			if (this.bOverGroup) { // MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
				// panjunfang add 20090813
				sHeader = this.getListView3Headers() + "\t组合群代码";
			} else {
				sHeader = this.getListView1Headers() + "\t组合群代码";
			}
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if (this.bOverGroup) { // MS00001
					// QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
					// panjunfang add 20090813
					bufShow.append(
							super.buildRowShowStr(rs, this
									.getListView3ShowCols())).append("\t" + rs.getString("FAssetGroupCode")).append(
							YssCons.YSS_LINESPLITMARK);
				} else {
					bufShow.append(
							super.buildRowShowStr(rs, this
									.getListView1ShowCols())).append("\t" + rs.getString("FAssetGroupCode")).append(
							YssCons.YSS_LINESPLITMARK);
				}
				setSecurityAttr(rs);
				this.assetGroupCode = rs.getString("FAssetGroupCode");
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

			VocabularyBean vocabulary = new VocabularyBean();
			vocabulary.setYssPub(pub);
			sVocStr = vocabulary.getVoc(YssCons.YSS_CNT_INTCYL + ","
					+ YssCons.YSS_CNT_INTORG + "," + YssCons.YSS_CNT_STATE
					+ "," + YssCons.YSS_PCA_INTERESTWAY + ","
					+ YssCons.Yss_Acc_ACCATTR + "," + YssCons.Yss_Acc_ACCSORT); //modified by zhaoxianlin 20121217 STORY #3384 外管局报表 增加账户分类词汇
			if (this.bOverGroup) { // MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
				// panjunfang add 20090813
				return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
						+ "\r\f" + this.getListView3ShowCols() + "\r\f" + "voc"
						+ sVocStr;
			} else {
				return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
						+ "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc"
						+ sVocStr;
			}
		} catch (Exception e) {
			throw new YssException("获取现金帐户信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
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
	 * saveMutliSetting
	 * 
	 * @param sMutilRowStr
	 *            String
	 * @return String
	 */
	public String saveMutliSetting(String sMutilRowStr) {
		return "";
	}

	/**
	 * addSetting
	 * 
	 * @return String
	 */
	public String addSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ ""
					+ " (FCashAccCode, FCashAccName,FStartDate, FAccType, FSubAccType, FBankCode, FBankAccount, FCuryCode, FMatureDate, "
					+ " FState, FPortCode, FFormulaCode, FRoundCode, FPeriodCode, FInterestCycle, FInterestOrigin,FFixRate, FDesc,"
					+ " FCheckState,FCreator,FCreateTime,FCheckUser,FInterestWay,FDepDurCode,FAccAttr,FbankaccName,FLoanCode,FRECPAYCODE,"
				    + " fperexpcode,finterestalg,FQSAccNum,FQSAccName,FACCSORT,FOpenDate,FAUTOACCLINK) values("//FAUTOACCLINK added by yeshenghong story3759 20130609
			    	  //add by zhouxiang MS01629  2010.10.13 招商基金资金账户查询功能
				      //modified by zhaoxianlin 20121217 STORY #3384 外管局报表 增加FACCSORT,FOpenDate 字段
					+ dbl.sqlString(this.strCashAcctCode)
					+ ","
					+ dbl.sqlString(this.strCashAcctName)
					+ ","
					+ dbl.sqlDate(this.dtStartDate)
					+ ","
					+ dbl.sqlString(this.strAcctTypeCode)
					+ ","
					+ dbl.sqlString(this.strSubAcctTypeCode)
					+ ","
					+ dbl.sqlString(this.strBankCode)
					+ ","
					+ dbl.sqlString(this.strBankAccount)
					+ ","
					+ dbl.sqlString(this.strCurrencyCode)
					+ ","
					+ dbl.sqlDate(this.dtMatureDate)
					+ ","
					+ this.strAcctState
					+ ","
					+ dbl.sqlString(this.strPortCode)
					+ ","
					+ dbl.sqlString(this.strFormulaCode)
					+ ","
					+ dbl.sqlString(this.strRoundCode)
					+ ","
					+ dbl.sqlString(this.strPeriodCode)
					+ ","
					+ this.interestCycle
					+ ","
					+ this.interestOrigin
					+ ","
					+ (this.interestOrigin != 3 ? 0 : this.fixRate)
					+ ","
					+ dbl.sqlString(this.strDesc)
					+ ","
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ","
					+ dbl.sqlString(this.creatorCode)
					+ ","
					+ dbl.sqlString(this.creatorTime)
					+ ","
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode)) + ","
					+ this.interestWay + ","
					+ dbl.sqlString(this.strDepDurCode) + "," + this.accAttr
					+","+dbl.sqlString(this.strBankAccName)//add by zhouxiang MS01629  2010.10.13 招商基金资金账户查询功能
					+","+dbl.sqlString(this.strLoanCode)   //add by guyichuan 20110520 STORY #561 新增贷款利息公式
					+","+dbl.sqlString(this.recPayCode)//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
					+","+dbl.sqlString(this.strInterTax)// add by jiangshichao 20120228 添加存款利息税率设置
					+","+this.interestAlg// add by yeshenghong 20120423  添加存款利息算法设置
					+","+dbl.sqlString(this.qsAccNum)
					+","+dbl.sqlString(this.qsAccName)
					//--start---modified by zhaoxianlin 20121217 STORY #3384 外管局报表
				    +","+dbl.sqlString(this.sAccSort)
					+","+dbl.sqlDate(this.dtOpenDate)
                    //--end---modified by zhaoxianlin 20121217 STORY #3384 外管局报表
					/*added by yeshenghong 2013-6-9 Story 3958 */
					+","+dbl.sqlString(this.isAutoAcc)
					/*end by yeshenghong 2013-6-9 Story 3958 */
					+ ")";
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			
		    /*added by yeshenghong 2013-6-8 Story 3958 */
			this.insertVchDict();
			
			if(this.isAutoAcc.equals("1"))
			{
				generateCashAccLink();
			}
			/*end by yeshenghong 2013-6-8 Story 3958 */
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		}

		catch (Exception e) {
			throw new YssException("增加现金帐户信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

		return null;
	}
	
	/*
	 *插入到凭证字典中
	 * add by yeshenghong 20130618 story 3958
	 */
	private void insertVchDict() throws YssException 
	{
		String strSql = "";
		try {
			/**start modify by huangqirong 2013-8-8 Bug #8985 现金账户界面新建现金账户报错  */
			if(this.cashInBank != null && this.cashInBank.trim().length() > 0){
				strSql = " insert into " + pub.yssGetTableName("tb_vch_dict") + "(FDICTCODE,FINDCODE,FPORTCODE,FDICTNAME,"
						+ " FCNVCONENT,FDESC, FSUBDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" 
						+ " select distinct fdictcode, " + dbl.sqlString(this.strCashAcctCode) + ", ' ',fdictname, " 
						+ dbl.sqlString(this.cashInBank) + ",FDESC," + dbl.sqlString(this.strCashAcctName) 
						+ ", FCheckState,FCreator," + dbl.sqlString(this.creatorTime) + ","
						+ " FCHECKUSER,FCHECKTIME  from " + pub.yssGetTableName("tb_vch_dict") + " where fdictcode = "  
						+ dbl.sqlString(YssCons.YSS_VCH_DICT_CashInBank) + " and fportcode = ' ' and rownum = 1 ";
				dbl.executeSql(strSql);
			}
			if(this.otherReceivalbes != null && this.otherReceivalbes.trim().length() > 0){
				strSql = " insert into " + pub.yssGetTableName("tb_vch_dict") + "(FDICTCODE,FINDCODE,FPORTCODE,FDICTNAME,"
						+ " FCNVCONENT,FDESC, FSUBDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" 
						+ " select distinct fdictcode, " + dbl.sqlString(this.strCashAcctCode) + ", ' ',fdictname, " 
						+ dbl.sqlString(this.otherReceivalbes) + ",FDESC," + dbl.sqlString(this.strCashAcctName) 
						+ ", FCheckState,FCreator," + dbl.sqlString(this.creatorTime) + ","
						+ " FCHECKUSER,FCHECKTIME from " + pub.yssGetTableName("tb_vch_dict") + " where fdictcode = "  
						+ dbl.sqlString(YssCons.YSS_VCH_DICT_OtherReceivalbes) + " and fportcode = ' ' and rownum = 1  ";
				dbl.executeSql(strSql);
			}
			if(this.otherPayables != null && this.otherPayables.trim().length() > 0){
				strSql = " insert into " + pub.yssGetTableName("tb_vch_dict") + "(FDICTCODE,FINDCODE,FPORTCODE,FDICTNAME,"
						+ " FCNVCONENT,FDESC, FSUBDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" 
						+ " select distinct fdictcode, " + dbl.sqlString(this.strCashAcctCode) + ", ' ',fdictname, " 
						+ dbl.sqlString(this.otherPayables) + ",FDESC," + dbl.sqlString(this.strCashAcctName) 
						+ ", FCheckState,FCreator," + dbl.sqlString(this.creatorTime) + ","
						+ " FCHECKUSER,FCHECKTIME from " + pub.yssGetTableName("tb_vch_dict") + " where fdictcode = " 
						+ dbl.sqlString(YssCons.YSS_VCH_DICT_OtherPayables) + " and fportcode = ' '  and rownum = 1 ";
				dbl.executeSql(strSql);
			}
			if(this.interestReceivalbe != null && this.interestReceivalbe.trim().length() > 0){
				strSql = " insert into " + pub.yssGetTableName("tb_vch_dict") + "(FDICTCODE,FINDCODE,FPORTCODE,FDICTNAME,"
						+ " FCNVCONENT,FDESC, FSUBDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" 
						+ " select distinct fdictcode, " + dbl.sqlString(this.strCashAcctCode) + ", ' ',fdictname, " 
						+ dbl.sqlString(this.interestReceivalbe) + ",FDESC," + dbl.sqlString(this.strCashAcctName) 
						+ ", FCheckState,FCreator," + dbl.sqlString(this.creatorTime) + ","
						+ " FCHECKUSER,FCHECKTIME from " + pub.yssGetTableName("tb_vch_dict") + " where fdictcode = " 
						+ dbl.sqlString(YssCons.YSS_VCH_DICT_Interest) + " and fportcode = ' '  and rownum = 1 ";
				dbl.executeSql(strSql);
			}
			if(this.interestRevenue != null && this.interestRevenue.trim().length() > 0){
				strSql = " insert into " + pub.yssGetTableName("tb_vch_dict") + "(FDICTCODE,FINDCODE,FPORTCODE,FDICTNAME,"
						+ " FCNVCONENT,FDESC, FSUBDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" 
						+ " select distinct fdictcode, " + dbl.sqlString(this.strCashAcctCode) + ", ' ',fdictname, " 
						+ dbl.sqlString(this.interestRevenue) + ",FDESC," + dbl.sqlString(this.strCashAcctName) 
						+ ", FCheckState,FCreator," + dbl.sqlString(this.creatorTime) + ","
						+ " FCHECKUSER,FCHECKTIME from " + pub.yssGetTableName("tb_vch_dict") + " where fdictcode = " 
						+ dbl.sqlString(YssCons.YSS_VCH_DICT_Revenue) + " and fportcode = ' '  and rownum = 1 ";
				dbl.executeSql(strSql);
			}
			/**end modify by huangqirong 2013-8-8 Bug #8985 现金账户界面新建现金账户报错*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new YssException("插入凭证字典出错", e);
		}
		
	}
	
	/**
	 * 增加现金帐户链接
	 * add by yeshenghong 2013-6-9 void
	 * story 3759
	 * @throws YssException 
	 * @throws  
	 */
	private void generateCashAccLink() throws  YssException {
		// TODO Auto-generated method stub
		String strSql = " delete from " + pub.yssGetTableName("Tb_Para_CashAccLink") + 
						" where FCASHACCCODE in (" + dbl.sqlString(this.strCashAcctCode) + "," +
						dbl.sqlString(this.strOldCashAcctCode) + ")";
		try {
			dbl.executeSql(strSql);//先删除
			strSql = " insert into " + pub.yssGetTableName("Tb_Para_CashAccLink") + "(FCASHACCCODE,FCURYCODE, " +
					" FSTARTDATE,FPORTCODE,FLINKLEVEL,FCHECKSTATE,FINVMGRCODE,FCATCODE,FSUBCATCODE,FBROKERCODE," +
					" FTRADETYPECODE,FEXCHANGECODE,FCREATOR ,FCREATETIME,FCHECKUSER,FCHECKTIME) values (" + 
					dbl.sqlString(this.strCashAcctCode) + "," + 
					dbl.sqlString(this.strCurrencyCode) + "," + 
					dbl.sqlDate(this.dtStartDate) + "," + 
					dbl.sqlString(this.strPortCode) + "," + 
					"0,1,' ',' ',' ',' ',' ',' '," +
					dbl.sqlString(this.creatorCode) + "," +
					dbl.sqlString(this.creatorTime) + "," + 
					dbl.sqlString(this.creatorCode) + "," +
					dbl.sqlString(this.creatorTime) + ")";
			dbl.executeSql(strSql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new YssException("增加现金帐户链接出错", e);
		}
	}

	/**
	 * editSetting
	 * 
	 * @return String
	 */
	public String editSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "update "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " set "
					+ "  FCashAccCode = "
					+ dbl.sqlString(this.strCashAcctCode)
					+ ", FCashAccName = "
					+ dbl.sqlString(this.strCashAcctName)
					+ ", FStartDate = "
					+ dbl.sqlDate(this.dtStartDate)
					+ ", FAccType = "
					+ dbl.sqlString(this.strAcctTypeCode)
					+ ", FSubAccType = "
					+ dbl.sqlString(this.strSubAcctTypeCode)
					+ ", FBankCode = "
					+ dbl.sqlString(this.strBankCode)
					+ ", FBankAccount = "
					+ dbl.sqlString(this.strBankAccount)
					//edited  by zhouxiang MS01629  2010.10.13 招商基金资金账户查询功能
					+ ", FbankAccName="
					+ dbl.sqlString(this.strBankAccName)
					//end--	  by zhouxiang MS01629  2010.10.13 招商基金资金账户查询功能
					+ ", FMatureDate = "
					+ dbl.sqlDate(this.dtMatureDate)
					+ ", FCuryCode = "
					+ dbl.sqlString(this.strCurrencyCode)
					+ ", FState = "
					+ this.strAcctState
					+ ", FAccAttr ="
					+ this.accAttr
					+ ", FPortCode = "
					+ dbl.sqlString(this.strPortCode)
					+ ", FFormulaCode = "
					+ dbl.sqlString(this.strFormulaCode)
					+ ", FRoundCode = "
					+ dbl.sqlString(this.strRoundCode)
					+ ", FPeriodCode = "
					+ dbl.sqlString(this.strPeriodCode)
					+ ", FInterestCycle = "
					+ this.interestCycle
					+ ", FInterestOrigin = "
					+ this.interestOrigin
					+ ", FFixRate = "
					+ (this.interestOrigin != 3 ? 0 : this.fixRate)
					+ ", FDesc = "
					+ dbl.sqlString(this.strDesc)
					+ ", FCheckState = "
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ", FCreator = "
					+ dbl.sqlString(this.creatorCode)
					+ ", FInterestWay ="
					+ this.interestWay
					+ ", FInterestAlg ="
					+ this.interestAlg
					+ ", FQSAccNum ="
					+ dbl.sqlString(this.qsAccNum)
					+ ", FQSAccName ="
					+ dbl.sqlString(this.qsAccName)
					+ ", FCreateTime = "
					+ dbl.sqlString(this.creatorTime)
					+ ", FDepDurCode = "
					+ dbl.sqlString(this.strDepDurCode)
					+ ", FCheckUser = "
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode))
				    + ", FLoanCode = "
					+ dbl.sqlString(this.strLoanCode)//add by guyichuan 20110520 STORY #561 新增贷款利息公式
					//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
					+",FRECPAYCODE="
					+dbl.sqlString(this.recPayCode)
						+",fperexpcode="
					+dbl.sqlString(this.strInterTax)
					//-----------------end-----------
					//--start---modified by zhaoxianlin 20121217 STORY #3384 外管局报表
					+",FACCSORT ="
					+dbl.sqlString(this.sAccSort)
						+",FOpenDate ="
					+dbl.sqlDate(this.dtOpenDate)
					//--end---modified by zhaoxianlin 20121217 STORY #3384 外管局报表
					/*added by yeshenghong 2013-6-9 Story 3958 */
					+",FAUTOACCLINK = " + dbl.sqlString(this.isAutoAcc)
					/*end by yeshenghong 2013-6-9 Story 3958 */
					+ " where FCashAccCode = "
					+ dbl.sqlString(this.strOldCashAcctCode)
					+ " and FSTARTDATE = " + dbl.sqlDate(this.dtOldStartDate);
			
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			
			/*added by yeshenghong 2013-6-8 Story 3958 */
			strSql = " delete from " + pub.yssGetTableName("tb_vch_dict") 
			   + " where findcode = " + dbl.sqlString(this.strCashAcctCode) + " and fportcode = ' ' "
			   + " and fdictcode in (" + dbl.sqlString(YssCons.YSS_VCH_DICT_CashInBank)
			   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherReceivalbes)
			   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherPayables)
			   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_Interest)
			   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_Revenue) + ")";
		
			dbl.executeSql(strSql);

			this.insertVchDict();
			
			if(this.isAutoAcc.equals("1"))
			{
				generateCashAccLink();
			}
			/*end by yeshenghong 2013-6-8 Story 3958 */
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		}

		catch (Exception e) {
			throw new YssException("修改现金帐户信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

		return null;

	}

	/**
	 * delSetting
	 */
	public void delSetting() throws YssException {
		//bug 3055 update by zhouwei 20111110 修改成批量删除的操作
		String strSql = "";
		String[] sChkAccLinkAry = null;
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();	
		try{
			//批量删除			
			bTrans = true;
			conn.setAutoCommit(false); // 手动开启一个事物
			this.setYssPub(pub); // 设置一些基础信息
			//edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
			if (!(checkAccLinks == null || checkAccLinks.equalsIgnoreCase(""))) { // 判断是否有批量数据
				sChkAccLinkAry = this.checkAccLinks.split("\r\n"); // 把选中的批量数据解析到单条入到数组中
				for (int i = 0; i < sChkAccLinkAry.length; i++) { // 循环遍历这个数组
					this.parseRowStr(sChkAccLinkAry[i]); // 把这个选择中的单个条目放到当前对象中
					strSql = "update "
							+ pub.yssGetTableName("Tb_Para_CashAccount")
							+ " set FCheckState = " + this.checkStateId
							+ ", FCheckUser = '" + pub.getUserCode()
							+ "', FCheckTime = '"
							+ YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FCashAccCODE = "
							+ dbl.sqlString(this.strCashAcctCode)
							+ " and FStartDate = "
							+ dbl.sqlDate(this.dtStartDate); // 更新的SQL语句
	
					dbl.executeSql(strSql); // 执行SQL语句
				}
				conn.setAutoCommit(false);
				bTrans = true;
				dbl.executeSql(strSql);
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
		}catch(Exception e){
			throw new YssException("删除现金帐户信息出错", e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
//		String strSql = "";
//		boolean bTrans = false; // 代表是否开始了事务
//		Connection conn = dbl.loadConnection();
//		try {
//			strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccount")
//					+ " set FCheckState = " + this.checkStateId
//					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
//					+ ", FCheckTime = '"
//					+ YssFun.formatDatetime(new java.util.Date())
//					+ "' where FCashAccCode = "
//					+ dbl.sqlString(this.strCashAcctCode)
//					+ " and FStartDate = " + dbl.sqlDate(this.dtStartDate);
//			conn.setAutoCommit(false);
//			bTrans = true;
//			dbl.executeSql(strSql);
//			conn.commit();
//			bTrans = false;
//			conn.setAutoCommit(true);
//		}
//
//		catch (Exception e) {
//			throw new YssException("删除现金帐户信息出错", e);
//		} finally {
//			dbl.endTransFinal(conn, bTrans);
//		}

	}

	/**
	 * 修改时间：2008年3月20号 修改人：单亮 原方法功能：只能处理期间连接的审核和未审核的单条信息。
	 * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息 修改后不影响原方法的功能
	 */
	public void checkSetting() throws YssException {
		// 修改后的代码
		// -----------begin
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		String[] sChkAccLinkAry = null;

		try {
			// ----------------------------------------------------------------------------------
			// MS00179 QDV4建行2009年1月07日01_B 2009.02.17 方浩
			// //批量审核与反审核和批量删除
			conn.setAutoCommit(false); // 手动开启一个事物
			bTrans = true;
			this.setYssPub(pub); // 设置一些基础信息
			//edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
			if (!(checkAccLinks == null || checkAccLinks.equalsIgnoreCase(""))) { // 判断是否有批量数据
				sChkAccLinkAry = this.checkAccLinks.split("\r\n"); // 把选中的批量数据解析到单条入到数组中
				for (int i = 0; i < sChkAccLinkAry.length; i++) { // 循环遍历这个数组
					this.parseRowStr(sChkAccLinkAry[i]); // 把这个选择中的单个条目放到当前对象中
					strSql = "update "
							+ pub.yssGetTableName("Tb_Para_CashAccount")
							+ " set FCheckState = " + this.checkStateId
							+ ", FCheckUser = '" + pub.getUserCode()
							+ "', FCheckTime = '"
							+ YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FCashAccCODE = "
							+ dbl.sqlString(this.strCashAcctCode)
							+ " and FStartDate = "
							+ dbl.sqlDate(this.dtStartDate); // 更新的SQL语句

					dbl.executeSql(strSql); // 执行SQL语句
				}
				// -------------------------------------------------------------------------------------------
				// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			//edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
			} else if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
				arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql = "update "
							+ pub.yssGetTableName("Tb_Para_CashAccount")
							+ " set FCheckState = " + this.checkStateId
							+ ", FCheckUser = '" + pub.getUserCode()
							+ "', FCheckTime = '"
							+ YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FCashAccCODE = "
							+ dbl.sqlString(this.strCashAcctCode)
							+ " and FStartDate = "
							+ dbl.sqlDate(this.dtStartDate);
					dbl.executeSql(strSql);
				}
		    //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
			} else if (strCashAcctCode != null && !strCashAcctCode.equalsIgnoreCase("")) {
				strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccount")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = '" + pub.getUserCode()
						+ "', FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date()) + "'"
						+ " where FCashAccCODE = "
						+ dbl.sqlString(this.strCashAcctCode)
						+ " and FStartDate = " + dbl.sqlDate(this.dtStartDate);
				dbl.executeSql(strSql);

			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核现金帐户信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		// -----------------------end
	}

	/**
	 * 为各项变量赋值
	 * 
	 */
	public void setSecurityAttr(ResultSet rs) throws SQLException {
		this.strCashAcctCode = rs.getString("FCashAccCode") + "";
		this.strCashAcctName = rs.getString("FCashAccName") + "";
		this.dtStartDate = rs.getDate("FStartDate");
		this.strAcctTypeCode = rs.getString("FAccType") + "";
		this.strAcctTypeName = rs.getString("FAccTypeName") + "";
		this.strSubAcctTypeCode = rs.getString("FSubAccType") + "";
		this.strSubAcctTypeName = rs.getString("FSubAccTypeName") + "";
		this.strBankCode = rs.getString("FBankCode") + "";
		this.strBankName = rs.getString("FBankName") + "";
		this.strBankAccount = rs.getString("FBankAccount") + "";
		this.strCurrencyCode = rs.getString("FCuryCode") + "";
		this.strCurrencyName = rs.getString("FCurrencyName") + "";
		this.strPortCode = rs.getString("FPortCode") + "";
		this.strPortName = rs.getString("FPortName") + "";
		this.strAcctState = rs.getString("FState") + "";
		this.interestCycle = rs.getInt("FInterestCycle");
		this.fixRate = rs.getDouble("FFixRate");
		this.interestOrigin = rs.getInt("FInterestOrigin");
		this.strFormulaCode = rs.getString("FFormulaCode") + "";
		this.strFormulaName = rs.getString("FFormulaName") + "";
		this.strRoundCode = rs.getString("FRoundCode") + "";
		this.strRoundName = rs.getString("FRoundName") + "";
		this.strPeriodCode = rs.getString("FPeriodCode") + "";
		this.strPeriodName = rs.getString("FPeriodName") + "";
		this.strDesc = rs.getString("FDesc") + "";
		this.dtMatureDate = rs.getDate("FMatureDate");
		this.interestWay = rs.getInt("FInterestWay");
		this.interestAlg = rs.getInt("FInterestAlg");
		this.qsAccNum = rs.getString("FQsAccNum");
		this.qsAccName = rs.getString("FQSAccName");
		this.strDepDurCode = rs.getString("FDepDurCode") + "";
		this.strDepDurName = rs.getString("FDepDurName") + "";
		this.accAttr = rs.getInt("FAccAttr");
		//by guyichuan 20110520 STORY #561
		this.strLoanCode = rs.getString("FLoanCode") + ""; 
		this.strLoanName = rs.getString("FLoanName") + "";
		//---end-STORY #561--------
		//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
		this.recPayCode=rs.getString("frecpayCode")+"";
		this.recPayName=rs.getString("frecpayName")+"";
		//--------------add by zhaoxianlin 20121217 STORY #3384 外管局报表——QDII账户信息----start
		this.sAccSort = rs.getString("FaccSort")+"";
		this.dtOpenDate = rs.getDate("FOpenDate");
		//---------------add by zhaoxianlin  20121217 STORY #3384 外管局报表——QDII账户信息-----end
		/*added by yeshenghong 2013-6-8 Story 3958 */
        if(dbl.isFieldExist(rs, "FCashInBank")&&dbl.isFieldExist(rs, "FReceivalbes")&&
           dbl.isFieldExist(rs, "FPayables")&&dbl.isFieldExist(rs, "FInterest")&&
           dbl.isFieldExist(rs, "FRevenue"))
        {
			this.cashInBank = rs.getString("FCashInBank");
			this.otherReceivalbes = rs.getString("FReceivalbes");
			this.otherPayables = rs.getString("FPayables");
			this.interestReceivalbe = rs.getString("FInterest");
			this.interestRevenue = rs.getString("FRevenue");
        }
        if(dbl.isFieldExist(rs, "FAUTOACCLINK"))
        {
        	this.isAutoAcc = rs.getString("FAUTOACCLINK");
        }
    	/*end by yeshenghong 2013-6-8 Story 3958 */
		    
		if(dbl.isFieldExist(rs, "FPerExpCode")&&dbl.isFieldExist(rs, "FPerExpName")){
			 this.strInterTax = rs.getString("FPerExpCode") + ""; //比率公式代码
		     this.strInterTaxName = rs.getString("FPerExpName") + ""; //比率公式名称
		}
		
		//-----------------END-----------
		// BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090605
		ResultSetMetaData rsmd = rs.getMetaData(); // 得到结果集里的内容
		for (int i = 1; i < rsmd.getColumnCount(); i++) { // 循环字段名称
			if (rsmd.getColumnName(i).equals("FASSETGROUPCODE")) { // 把字段名称进行对比看是否有当前字段名称
				this.assetGroupCode = rs.getString("fassetgroupcode") + ""; // 给组合群代码赋值
				this.assetGroupName = rs.getString("fassetgroupname") + ""; // 给组合群名称赋值
			}
		}
		// ---------------------------------------------------------------------------------------------
		this.strBankAccName=rs.getString("FbankAccName")+"";//add by zhouxiang MS01629 2010.10.13 招商基金资金账号信息查询功能
		super.setRecLog(rs);
	}

	/**
	 * getOperValue
	 * 
	 * @param sType
	 *            String
	 * @return String
	 */
	public String getOperValue(String sType) throws YssException {
		String strCuryCode = "";
		String str = ""; // 查询基础汇率的查询语句
		String reList5 = "";
		ResultSet rs = null;
		double baseRate = 0.0;
		double portRate = 0.0;
		try {
			if (sType.equalsIgnoreCase("listview5")) { // 在计息时获取相应时间段和组合的帐户数据。sj
				// edit 20080624
				str = "select show.*,h.fassetgroupcode, h.fassetgroupname from (select y.* from "
					    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//						+ // BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
//						// fanghaoln 20090512
//						"(select FCashAccCode,FCheckState,max(FStartDate) as FStartDate from "
//						+ pub.yssGetTableName("Tb_Para_CashAccount")
//						+ " "
//						+ " where FStartDate <= "
//						+ dbl.sqlDate(new java.util.Date())
//						+
//						// edit by songjie 2009.08.31 MS00616
//						// QDV4赢时胜(上海)2009年8月4日01_A
//						// 将利息来源不等于不计息作为查询条件, 使现金计息中不显示已设置为不计息的账户
//						"and FCheckState <> 2 and FInterestorigin <> 2 group by FCashAccCode,FCheckState) x join"
					    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
						+ " (select a.*, l.FVocName as FStateValue, m.FVocName as FInterestCycleValue, n.FVocName as FInterestOriginValue,"
						+ "b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FAccTypeName, e.FSubAccTypeName,rp.FRECEIVERNAME as frecpayname,"
						+ "f.FBankName,g.FCurrencyName, h.FPortName,j.FPeriodName,k.FRoundName, i.FFormulaName,ii.FFormulaName as FLoanName ,xx.FDepDurName,nn.FVOCNAME as InterestWay "
						+ ",w.FFormulaName as FPerExpName,nnn.FVOCName as FAccSortName  from "  //add by zhaoxianlin 20130111 story #3384 增加账户分类字段  现金计息是报错修改
						//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						+ " (select * from "+ pub.yssGetTableName("Tb_Para_CashAccount") + " where FCheckState <> 2 and FInterestorigin <> 2)"
						+ "  a "
						+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
						+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
						//story 1911 by zhouwei 20111226 关联收付款人 QDV4招商基金2011年11月22日01_A
						+" left join (select FRECEIVERCODE,FRECEIVERNAME from "+pub.yssGetTableName("TB_PARA_RECEIVER")+" where fcheckstate=1 ) rp on rp.FRECEIVERCODE=a.FRECPAYCODE"
						//-------------end------------
						+ " left join "
						+ pub.yssGetTableName("Tb_Para_DepositDuration")
						+ " xx on a.FDepDurCode =xx.FDepDurCode "
						//------ 为04加上单引号； modify by wangzuochun BUG #460 现金计息因字段类型问题报错 
                        + " join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1 and facctypecode <> '04' ) d " + //MS01637 QDV4太平2010年08月23日01_AB  add by jiangshichao 1.控制只显示当期有库存的账户并且为非虚拟账户（facctypecode=04）
                          "on a.FAccType = d.FAccTypeCode" 
						+ " left join (select FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType where FCheckState = 1) e on a.FSubAccType = e.FSubAccTypeCode"
						+ " left join (select FBankCode,FBankName from "
						+ pub.yssGetTableName("Tb_Para_Bank")
						+ " where FCheckState = 1) f on a.FBankCode = f.FBankCode"
						+ " left join (select FCuryCode as FCurrencyCode,FCuryName as FCurrencyName from "
						+ pub.yssGetTableName("Tb_Para_Currency")
						+ " where FCheckState = 1) g on a.FCuryCode = g.FCurrencyCode"
						+   " left join (select FFormulaCode,FFormulaName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState=1) w on a.FPerExpCode=w.FFormulaCode" 
						+ " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						+ " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getPrefixTB())
						//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//						+ "(select FPortCode,max(FStartDate) as FStartDate from "
//						+ pub.yssGetTableName("Tb_Para_Portfolio")
//						+ " "
//						+ " where FStartDate <= "
//						+ dbl.sqlDate(new java.util.Date())
//						+ " and FCheckState = 1 and FASSETGROUPCODE = "
//						+ dbl.sqlString(pub.getPrefixTB())
//						+ " group by FPortCode) p "
						//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
						//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						+ " ) h on a.FPortCode = h.FPortCode"
						+ " left join (select FFormulaCode, FFormulaName from "
						+ pub.yssGetTableName("Tb_Para_Performula")
						+ " where FCheckState = 1) i on a.FFormulaCode = i.FFormulaCode"
						//by guyichuan 20110520 STORY #561
						+ " left join (select FFormulaCode, FFormulaName from "
						+ pub.yssGetTableName("Tb_Para_Performula")
						+ " where FCheckState = 1) ii on a.FLoanCode = ii.FFormulaCode"
						//---end-STORY #561------
						+ " left join (select FPeriodCode,FPeriodName from "
						+ pub.yssGetTableName("Tb_Para_Period")
						+ " where FCheckState = 1 ) j on a.FPeriodCode = j.FPeriodCode"
						+ " left join (select FRoundCode,FRoundName from "
						+ pub.yssGetTableName("Tb_Para_Rounding")
						+ "  where FCheckState = 1) k on a.FRoundCode = k.FRoundCode"
						+ " left join Tb_Fun_Vocabulary l on a.FState = l.FVocCode and l.FVocTypeCode = "
						+ dbl.sqlString(YssCons.YSS_CNT_STATE)
						+ " left join Tb_Fun_Vocabulary m on a.FInterestCycle = m.FVocCode and m.FVocTypeCode = "
						+ dbl.sqlString(YssCons.YSS_CNT_INTCYL)
						+ " left join Tb_Fun_Vocabulary n on a.FInterestOrigin = n.FVocCode and n.FVocTypeCode = "
						+ dbl.sqlString(YssCons.YSS_CNT_INTORG)
						+ " "
						+ " left join Tb_Fun_Vocabulary nn on a.FInterestWay = nn.FVocCode  and nn.FVocTypeCode = "
						+ dbl.sqlString(YssCons.YSS_PCA_INTERESTWAY)
						+ " "
						//-----add by zhaoxianlin 20130111 story #3384 现金计息是报错修改---start-- //
						+ " left join Tb_Fun_Vocabulary nnn on a.FAccSort = nnn.FVocCode  and nnn.FVocTypeCode = "
						+ dbl.sqlString(YssCons.Yss_Acc_ACCSORT)
						+ " "
						//-----add by zhaoxianlin 20130111 story #3384 ----end ---//
						+ buildFilterSql()
						+ ") y "//edit by songjie 2011.03.14 不以最大的启用日期查询数据
						+ " order by y.FCheckState, y.FCreateTime desc ) show"
						+
						// --------- MS00245 QDV4交银施罗德2009年02月13日01_B sj
						// modified --------//
						" join (select distinct FCashAccCode from (select FCashAccCode from "
						+ // 获取唯一值
						// ---------------------------------------------------------------------//
						pub.yssGetTableName("tb_stock_Cash")
                       +" where FCheckState = 1 " 
                       +" and faccbalance<>0 " //MS01637 QDV4太平2010年08月23日01_AB  add by jiangshichao 控制只显示当期有库存的账户并且为非虚拟账户
                       + " and FPortCode in (" 
						+ this.operSql.sqlCodes(this.filterType.strPortCode)
						+ ") and FStorageDate between  "
						+
						// --------- MS00245 QDV4交银施罗德2009年02月13日01_B sj
						// modified -------//
						dbl.sqlDate(YssFun.addDay(this.filterType.dBeginDate,
								-1))
						+ " and "
						+ // 日期段应为前一天至结束日期，只要这一段时间内有库存帐户信息，就显示。
						// --------------------------------------------------------------------//
						dbl.sqlDate(this.filterType.dEndDate)
						+
						// --------- MS00245 QDV4交银施罗德2009年02月13日01_B sj
						// modified 合并资金调拨的数据 -------//
						" union select b.fcashacccode as FCashAccCode from "
						+ pub.yssGetTableName("tb_cash_transfer")
						+ " a,"
						+ pub.yssGetTableName("tb_cash_subtransfer")
						+ " b where "
						+ " b.FCheckState = 1 and b.FPortCode in ("
						+ this.operSql.sqlCodes(this.filterType.strPortCode)
						+ ") and a.FNum = b.FNum and a.FTransferDate between  "
						+ dbl.sqlDate(YssFun.addDay(this.filterType.dBeginDate,
								-1))
						+ " and "
						+ dbl.sqlDate(this.filterType.dEndDate)
						+ ") al) stock on show.FCashAccCode = stock.FCashAccCode "
						+
						// BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
						// fanghaoln 20090512
						// =====================================================================================
						" left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '"
						+ pub.getPrefixTB() + "' ";
				// ===============================================================================

				// -----------------------------------------------------------------------------------//
				reList5 = this.builderListViewData(str);
				return reList5;
			} else if (sType.equalsIgnoreCase("getRate")) {
				str = "select a.FCuryCode as FCuryCode,b.FPortCury as FPortCury,a.FPortCode as FPortCode from "
						+ "(select FCuryCode,FPortCode,FCashAccCode from "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ " where FCheckState = 1"
						+ " and FCashAccCode = "
						+ dbl.sqlString(this.strCashAcctCode)
						+ ") a join (select FPortCury,FPortCode from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						+ " where FCheckState = 1) b on a.FPortCode = b.FPortCode";
				rs = dbl.openResultSet(str);
				if (rs.next()) {
					baseRate = this.getSettingOper().getCuryRate(
							this.dtMatureDate,
							rs.getString("FCuryCode"),
							this.strPortCode.length() > 0 ? this.strPortCode
									: rs.getString("FPortCode"),
							YssOperCons.YSS_RATE_BASE);
					portRate = this.getSettingOper().getCuryRate(
							this.dtMatureDate,
							rs.getString("FPortCury"),
							this.strPortCode.length() > 0 ? this.strPortCode
									: rs.getString("FPortCode"),
							YssOperCons.YSS_RATE_PORT);
				}
				return Double.toString(baseRate) + "\t"
						+ Double.toString(portRate);
			} else if (sType.equalsIgnoreCase("curyCode")) { // byleeyu
				str = "select * from "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ " where FCashAccCode ="
						+ dbl.sqlString(this.strCashAcctCode);
				rs = dbl.openResultSet(str);
				while (rs.next()) {
					strCuryCode = rs.getString("FCuryCode");
				}
				return strCuryCode;
			} else if (sType.equalsIgnoreCase("cashPortCode")) { // add by fangjiang 2010.09.30 MS01794 MS01795 MS01805 
				String strCashPortCode = "";
				str = "select * from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where FCashAccCode ="
					+ dbl.sqlString(this.strCashAcctCode);
				rs = dbl.openResultSet(str);
				while (rs.next()) {
					strCashPortCode = rs.getString("FPortCode");					
				}
				return strCashPortCode;
			} 
			//add by huangqirong 2013-01-30 story #3510 现金账户 从Ta 交易数据中筛选
			else if(sType.indexOf("cashwithtatrade-") > -1){
				String portcode = sType.substring("cashwithtatrade-".length());
				this.filterConditions = " and a.FCashacccode in ( select FCashacccode from " + pub.yssGetTableName("tb_ta_trade") + 
											" ta where ta.fportcode = " + dbl.sqlString(portcode) + " group by ta.fcashacccode )" ;
				return this.getListViewData2();
			}
			// ---end ---
		} catch (Exception e) {
			throw new YssException("获取现金帐户信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return "";
	}

	/**
	 * getBeforeEditData
	 * 
	 * @return String
	 */
	public String getBeforeEditData() throws YssException {
		CashAccountBean befEditBean = new CashAccountBean();
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "select y.* from "
				    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//					+ "(select FCashAccCode,FCheckState,max(FStartDate) as FStartDate from "
//					+ pub.yssGetTableName("Tb_Para_CashAccount")
//					+ " "
//					+ " where FStartDate <= "
//					+ dbl.sqlDate(new java.util.Date())
//					+ "and FCheckState <> 2 group by FCashAccCode,FCheckState) x join"
				    //----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
					+ " (select a.*, l.FVocName as FStateValue, m.FVocName as FInterestCycleValue, n.FVocName as FInterestOriginValue,"
					+ "b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FAccTypeName, e.FSubAccTypeName,"
					+ "f.FBankName,g.FCurrencyName, h.FPortName,j.FPeriodName,k.FRoundName, i.FFormulaName,ii.FFormulaName as FLoanName,xx.FDepDurName "
					+ " from "
					//edit by songjie 2011.03.14 不以最大的启用日期查询数据
					+ " (select * from "+ pub.yssGetTableName("Tb_Para_CashAccount") + " where FCheckState <> 2)"
					+ "  a "
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
					+ " left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) d on a.FAccType = d.FAccTypeCode"
					+ " left join (select FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType where FCheckState = 1) e on a.FSubAccType = e.FSubAccTypeCode"
					+ " left join (select FBankCode,FBankName from "
					+ pub.yssGetTableName("Tb_Para_Bank")
					+ " where FCheckState = 1) f on a.FBankCode = f.FBankCode"
					+ " left join (select FCuryCode as FCurrencyCode,FCuryName as FCurrencyName from "
					+ pub.yssGetTableName("Tb_Para_Currency")
					+ " where FCheckState = 1) g on a.FCuryCode = g.FCurrencyCode"
					+ " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					//edit by songjie 2011.03.14 不以最大的启用日期查询数据
					+ " o where FCheckState = 1 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode())
					//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
//					+ "(select FPortCode,max(FStartDate) as FStartDate from "
//					+ pub.yssGetTableName("Tb_Para_Portfolio")
//					+ " "
//					+ " where FStartDate <= "
//					+ dbl.sqlDate(new java.util.Date())
//					+ " and FCheckState = 1 and FASSETGROUPCODE = "
//					+ dbl.sqlString(pub.getAssetGroupCode())
//					+ " group by FPortCode) p "
					//----delete by songjie 2011.03.14 不以最大的启用日期查询数据----//
					//edit by songjie 2011.03.14 不以最大的启用日期查询数据
					+ " ) h on a.FPortCode = h.FPortCode"
					+ " left join (select FFormulaCode, FFormulaName from "
					+ pub.yssGetTableName("Tb_Para_Performula")
					+ " where FCheckState = 1) i on a.FFormulaCode = i.FFormulaCode"
					//by guyichuan 20110520 STORY #561
					+ " left join (select FFormulaCode, FFormulaName from "
					+ pub.yssGetTableName("Tb_Para_Performula")
					+ " where FCheckState = 1) ii on a.FLoanCode = ii.FFormulaCode"
					//---end-STORY #561------
					+ " left join (select FPeriodCode,FPeriodName from "
					+ pub.yssGetTableName("Tb_Para_Period")
					+ " where FCheckState = 1 ) j on a.FPeriodCode = j.FPeriodCode"
					+ " left join (select FRoundCode,FRoundName from "
					+ pub.yssGetTableName("Tb_Para_Rounding")
					+ "  where FCheckState = 1) k on a.FRoundCode = k.FRoundCode"
					+ " left join Tb_Fun_Vocabulary l on "
					+ dbl.sqlToChar("a.FState")
					+ " = l.FVocCode and l.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_CNT_STATE)
					+ " left join Tb_Fun_Vocabulary m on "
					+ dbl.sqlToChar("a.FInterestCycle")
					+ " = m.FVocCode and m.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_CNT_INTCYL)
					+ " left join "
					+ pub.yssGetTableName("Tb_Para_DepositDuration")
					+ " xx on a.FDepDurCode =xx.FDepDurCode "
					+ " left join Tb_Fun_Vocabulary n on "
					+ dbl.sqlToChar("a.FInterestOrigin")
					+ " = n.FVocCode and n.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_CNT_INTORG)
					+ " "
					+ " where  FCashAccCode ="
					+ dbl.sqlString(this.strOldCashAcctCode)
					+ ") y "//edit by songjie 2011.03.14 不以最大的启用日期查询数据
					+ " order by y.FCheckState, y.FCreateTime desc";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				befEditBean.strCashAcctCode = rs.getString("FCashAccCode") + "";
				befEditBean.strCashAcctName = rs.getString("FCashAccName") + "";
				befEditBean.dtStartDate = rs.getDate("FStartDate");
				befEditBean.strAcctTypeCode = rs.getString("FAccType") + "";
				befEditBean.strAcctTypeName = rs.getString("FAccTypeName") + "";
				befEditBean.strSubAcctTypeCode = rs.getString("FSubAccType")
						+ "";
				befEditBean.strSubAcctTypeName = rs
						.getString("FSubAccTypeName")
						+ "";
				befEditBean.strBankCode = rs.getString("FBankCode") + "";
				befEditBean.strBankName = rs.getString("FBankName") + "";
				befEditBean.strBankAccount = rs.getString("FBankAccount") + "";
				befEditBean.strCurrencyCode = rs.getString("FCuryCode") + "";
				befEditBean.strCurrencyName = rs.getString("FCurrencyName")
						+ "";
				befEditBean.strPortCode = rs.getString("FPortCode") + "";
				befEditBean.strPortName = rs.getString("FPortName") + "";
				befEditBean.strAcctState = rs.getString("FState") + "";
				befEditBean.interestCycle = rs.getInt("FInterestCycle");
				befEditBean.interestOrigin = rs.getInt("FInterestOrigin");
				befEditBean.strFormulaCode = rs.getString("FFormulaCode") + "";
				befEditBean.strFormulaName = rs.getString("FFormulaName") + "";
				//by guyichuan 20110520 STORY #561
				befEditBean.strLoanCode = rs.getString("FLoanCode") + "";
				befEditBean.strLoanName = rs.getString("FLoanName") + "";
				//--end-STORY #561----
				befEditBean.strRoundCode = rs.getString("FRoundCode") + "";
				befEditBean.strRoundName = rs.getString("FRoundName") + "";
				befEditBean.strPeriodCode = rs.getString("FPeriodCode") + "";
				befEditBean.strPeriodName = rs.getString("FPeriodName") + "";
				befEditBean.strDesc = rs.getString("FDesc") + "";
				befEditBean.dtMatureDate = rs.getDate("FMatureDate");
				befEditBean.strDepDurCode = rs.getString("FDepDurCode") + "";
				befEditBean.strDepDurName = rs.getString("FDepDurName") + "";
				befEditBean.accAttr = rs.getInt("FAccAttr");
			}
			return befEditBean.buildRowStr();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs); // 关闭游标资源 modify by sunkey 20090604
			// MS00472:QDV4上海2009年6月02日01_B
		}
	}

	/**
	 * 从数据库彻底删除数据
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		// 获取一个连接
		Connection conn = dbl.loadConnection();
		try {
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != "" && sRecycled != null) {
				// 根据规定的符号，把多个sql语句分别放入数组
				arrData = sRecycled.split("\r\n");
				conn.setAutoCommit(false);
				bTrans = true;
				// 循环执行这些删除语句
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Para_CashAccount")
							+ " where FCashAccCode = "
							+ dbl.sqlString(this.strCashAcctCode)
							+ " and FStartDate = "
							+ dbl.sqlDate(this.dtStartDate);

					// 执行sql语句
					dbl.executeSql(strSql);
					/*added by yeshenghong 2013-6-8 Story 3958 */
					strSql = " delete from " + pub.yssGetTableName("tb_vch_dict") 
						   + " where findcode = " + dbl.sqlString(this.strCashAcctCode) + " and fportcode = ' ' "
						   + " and fdictcode in (" + dbl.sqlString(YssCons.YSS_VCH_DICT_CashInBank)
						   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherReceivalbes)
						   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_OtherPayables)
						   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_Interest)
						   + "," + dbl.sqlString(YssCons.YSS_VCH_DICT_Revenue) + ")";
					
					dbl.executeSql(strSql);
					/*end by yeshenghong 2013-6-8 Story 3958 */
				}
			}
			// sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
			else if (strCashAcctCode != "" && strCashAcctCode != null) {
				strSql = "delete from "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ " where FCashAccCode = "
						+ dbl.sqlString(this.strCashAcctCode)
						+ " and FStartDate = " + dbl.sqlDate(this.dtStartDate);
				// 执行sql语句
				dbl.executeSql(strSql);
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);

		}

		catch (Exception e) {
			throw new YssException("清除数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public java.util.Date getDtMatureDate() {
		return dtMatureDate;
	}

	public String getAssetGroupCode() {
		return assetGroupCode;
	}

	public String getAssetGroupName() {
		return assetGroupName;
	}

	public void setDtMatureDate(java.util.Date dtMatureDate) {
		this.dtMatureDate = dtMatureDate;
	}

	public void setAssetGroupCode(String assetGroupCode) {
		this.assetGroupCode = assetGroupCode;
	}

	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}

	public String getListViewGroupData1() throws YssException {
		return "";
	}

	// / <summary>
	// / 修改人：panjunfang
	// / 修改人时间:20090902
	// / MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
	// /
	// 如果为跨组合群，则在点击现金账户后面的放大镜按钮时加载出对应组合群的现金账户列表，例如：在日终处理-收益支付的债券利息收支中点击证券支付中现金账户的查找按钮应区别组合群加载出现金账户列表
	public String getListViewGroupData2() throws YssException {
		String strRe = "";// 存放返回到前台的字符串
		String sPrefixTB = pub.getPrefixTB(); // 保存当前的组合群代码即表前缀
		try {
			pub.setPrefixTB(this.assetGroupCode);// 将前台传过来的组合群代码设置为表前缀
			strRe = this.getListViewData2() + YssCons.YSS_GROUPSPLITMARK
					+ this.assetGroupCode;// 将该组合群及其所对应的现金账户列表返回至前台
		} catch (Exception e) {
			throw new YssException("获取现金帐户信息出错", e);
		} finally {
			pub.setPrefixTB(sPrefixTB);// 还原公共变的里的组合群代码即表前缀
		}
		return strRe;
	}

	public String getListViewGroupData3() throws YssException {
		String strRe = "";// 存放返回到前台的字符串
		String sPrefixTB = pub.getPrefixTB(); // 保存当前的组合群代码即表前缀
		try {
			pub.setPrefixTB(this.assetGroupCode);// 将前台传过来的组合群代码设置为表前缀
			strRe = this.getListViewData3() + YssCons.YSS_GROUPSPLITMARK
					+ this.assetGroupCode;// 将该组合群及其所对应的现金账户列表返回至前台
		} catch (Exception e) {
			throw new YssException("获取现金帐户信息出错", e);
		} finally {
			pub.setPrefixTB(sPrefixTB);// 还原公共变的里的组合群代码即表前缀
		}
		return strRe;
	}

	public String getListViewGroupData4() throws YssException {
		return "";
	}

	// / <summary>
	// / 修改人：fanghaoln
	// / 修改人时间:20090512
	// / BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
	// / 从后台加载出我们跨组合群的内容
	public String getListViewGroupData5() throws YssException {
		this.bOverGroup = true; // 跨组合群
		String sAllGroup = ""; // 定义一个字符用来保存执行后的结果传到前台
		String sPrefixTB = pub.getPrefixTB(); // 保存当前的组合群代码
		String[] assetGroupCodes = this.filterType.assetGroupCode
				.split(YssCons.YSS_GROUPSPLITMARK); // 按组合群的解析符解析组合群代码
		String[] strPortCodes = this.filterType.strPortCode
				.split(YssCons.YSS_GROUPSPLITMARK); // 按组合群的解析符解析组合代码
		try {
			for (int i = 0; i < assetGroupCodes.length; i++) { // 循环遍历每一个组合群
				this.assetGroupCode = assetGroupCodes[i]; // 得到一个组合群代码
				pub.setPrefixTB(this.assetGroupCode); // 修改公共变量的当前组合群代码
				this.filterType.strPortCode = strPortCodes[i]; // 得到一个组合群下的组合代码
				String sGroup = this.getOperValue("listview5"); // 调用以前的执行方法
				sAllGroup = sAllGroup + sGroup + YssCons.YSS_GROUPSPLITMARK; // 组合得到的结果集
			}
			if (sAllGroup.length() > 7) { // 去除尾部多余的组合群解析符
				sAllGroup = sAllGroup.substring(0, sAllGroup.length() - 7);
			}
		} catch (Exception e) {
			throw new YssException("获取现金帐户信息出错", e);
		} finally {
			pub.setPrefixTB(sPrefixTB); // 还原公共变的里的组合群代码
			this.bOverGroup = false;
		}
		return sAllGroup; // 把结果返回到前台进行显示
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

	/**
	 * 获取现金帐户信息代码
	 * add by lvhx 2010.06.24 MS01297 计息业务的明细通过业务日期和组合动态获取 QDV4赢时胜（深圳）2010年06月02日01_A 
	 */

	public String getIncomeTypeData() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		StringBuffer strResult = new StringBuffer();
		try {
			strSql = "select x.FCashAccCode from "
					+ "(select FCashAccCode," +
					/**shashijie 2011-10-10 BUG2857  在oracle11g2.0.2.版本下运行调度方案执行报错
					 * 子查询中用聚合函数需要严谨,否则高版本的数据库会报异常*/
					//" FCheckState," +
					/***/
					" max(FStartDate) as FStartDate from "
					+ pub.yssGetTableName("Tb_Para_CashAccount") + " "
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and fcheckstate = '1' "
					+ " group by FCashAccCode) x ";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				strResult.append(rs.getString("FCashAccCode")).append(",");
			}
			if (strResult.length() > 1) {
				strResult.delete(strResult.length() - 1, strResult.length());
			}
			return strResult.toString();
		} catch (Exception e) {
			throw new YssException("获取现金帐户信息代码出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
    /**
	 * 20111205 modified by liubo.Story #1916
	 * 查询当前库中所有组合群
	 * return ResultSet
	 * @throws YssException 
	 */
	public String getAllAssetGroup() throws YssException{
		ResultSet rs=null;
		String sql=null;
		String FAssetGroupCode="";
		try{
			sql="select * from Tb_Sys_AssetGroup order by FAssetGroupCode";
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				FAssetGroupCode+=rs.getString("FAssetGroupCode")+"\t";
			}
			return FAssetGroupCode;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
