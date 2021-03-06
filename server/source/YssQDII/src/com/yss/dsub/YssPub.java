package com.yss.dsub;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.util.*;
import com.yss.commeach.EachPubDataOper;
import com.yss.dbupdate.BaseDbUpdate;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: 用于session bean，处理全局变量
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Ysstech
 * </p>
 * 
 * @author alex
 * @version 1.0
 */

public final class YssPub {
	public YssPub() {
	}

	// 辅助变量
	private DbBase dbLink = null; // DbBase类实例
	
	//---add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A start---//
	private DbBase dbLinkBLog = null;//日志数据库链接
	public final void setDbLinkBLog(DbBase link) {
		dbLinkBLog = link;
	}

	public final DbBase getDbLinkBLog() {
		return dbLinkBLog;
	}
	//---add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A end---//
	
	public final void setDbLink(DbBase link) {
		dbLink = link;
	}

	public final DbBase getDbLink() {
		return dbLink;
	}

	// 客户端信息
	public String clientGateway = null;
	public String clientPCName = null;
	public String clientPCAddr = null;
	public String clientMacAddr = null;

	public String clientAddr = null; // 客户端地址

	// 以下为用户登录后加载的全局变量

	// 组合信息
	private String prefixTB = ""; // 表前缀
	// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB 蒋世超 添加 2009.12.24 --------------
	private int prefixYear = 0; // 年份前缀
	// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB end
	// ---------------------------------
	private String baseCury = ""; // 货币
	private String assetGroupCode = ""; // 组合群代码
	private String assetGroupName = ""; // 组合群名称
	private int portfolioNum = 0; // 最大基金个数
	private java.util.Date beginDate = null; // 开始日期
	private boolean locked = false; // 是否锁定
	private String baseRateSrcCode = "";
	private String baseRateCode = "";
	private String portRateSrcCode = "";
	private String portRateCode = "";

	// 用户信息
	private String userCode = ""; // 用户代码
	//add by songjie 2011.07.18 BUG 2274 QDV4建信2011年7月14日01_B
	private String userID = "";//用户ID
	private String userName = ""; // 用户名称
	private String userDesc = ""; // 用户描述
	private String userPass = ""; // 用户密码
	private int userPassLen = 0; // 用户密码长度
	private int userPassCount = 0; // 密码容错次数
	private int iPageCount = 100; // 每页固定行数 xuqiji 20100319
									// QDV4赢时胜上海2009年12月21日06_B MS00884
	private java.util.Date userDate = null; // 用户启效日期
	private boolean userLocked = false; // 用户是否被锁定
	private String userMenuCodes = ""; // 用户可用菜单
	private String userMenubarCodes = ""; // 用户可用菜单条
	private String userPassLevel = ""; // linjunyun 需求MS00016 2008-11-12 密码的安全性
	private int userPassValidTime = 0; // linjunyun 需求MS00016 2008-11-12 密码的有效时间
	private java.util.Date userLastLoginDate = null;// 用户上一次登录系统的时间.add by
													// xuxuming,20091109.MS00776.要根据上次登录时间来达到“N天不登录就锁定用户”的目的。
	private HashMap onlineUser;

	private String webRoot;
	
	private String webRootRealPath;//应用程序的绝对路径 BUG7549 panjunfang add 20130428

	private ApplicationContext baseSettingCtx;
	private ApplicationContext paraSettingCtx;
	private ApplicationContext funSettingCtx;
	private ApplicationContext orderAdminCtx;
	private ApplicationContext dataInterfaceCtx;
	private ApplicationContext cashManagerCtx;
	private ApplicationContext operDataCtx;
	private ApplicationContext operdealCtx;
	private ApplicationContext accbookCtx;
	private ApplicationContext settlementCtx;
	private ApplicationContext dayFinishCtx;
	private ApplicationContext cusReportCtx;
	private ApplicationContext voucherCtx;
	private ApplicationContext pretfunCtx;
	private ApplicationContext platformCtx; // 新增系统平台功能 by liyu 080408
	private ApplicationContext taOperationCtx; // 新增TA处理平台 by liyu 080421
	private ApplicationContext complianceCtx; // 新增监控处理 by liyu 080421
	private ApplicationContext accountCtx; // 财务模块 add by ctq 090609
	// ====add by xuxuming,20090928.MS00717.点击业务资料中的交易关联系统报错===
	private ApplicationContext storManageCtx;// 库存管理模块
	// =========end=======================================================
	protected boolean bSysCheckState = true; // 是否审核
	protected boolean bSysSelfCheck = false; // 是否能审核自己维护的信息
	protected boolean bSysOtherCheck = false; // 是否能反审核其他用户审核的信息
	protected boolean bSysAudietOwn = false;//add by guolongchao 20120426 添加自审功能 （明细到各个菜单）    QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc
	
	private Hashtable htPubParams = null; // 公共数据跨组合群参数QDV4建行2008年12月25日01_A
											// MS00131 byleeyu 20090204
	private Object cacheTag; // 用于存到Session的对象

	// ----- MS00003 QDV4.1-参数布局散乱不便操作
	private Hashtable flowTable = null;

	private String dblbl = "";// xuqiji 20100311
								// 数据库连接文件dbsetting.txt的读取头文件的标识类似于"[db_yssimsas]"
								// MS01008 QDV4广发2010年3月3日01_B
								// 无法实现同一个WAR包上实现多个数据库的连接
	
	//add by guolongchao 20110905 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现    
	private boolean isBrown=false;//读数完成后是否浏览数据，默认情况：不浏览数据-------
	private String tabMainCode = "";//浏览页面对应的后台javaBean中查询listview中使用的sql语句的主表名称,可以是多张主表
	//---add by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A start---//
	private String dealStatus = "";//处理状态
	
	private String sessionId="";//story 2188 add by zhouwei 20120606 session的ID
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}
	//---add by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A end---//
	public boolean isBrown() {
		return isBrown;
	}
	public void setBrown(boolean isBrown) {
		this.isBrown = isBrown;
	}
	
	public String getTabMainCode() {
		return tabMainCode;
	}

	public void setTabMainCode(String tabMainCode) {
		this.tabMainCode = tabMainCode;
	}
	//add by guolongchao 20110905 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现  ---end
	
	public void setFlow(Hashtable flow) {
		this.flowTable = flow;
	}

	public Hashtable getFlow() {
		return this.flowTable;
	}

	// --------------------------------------//
	// #174、#281 QDV4农行2010年10月29日02_B panjufang modify 20101118 Suse
	// Linux环境下报下标越界，参照北京同事在中行V3.0的处理办法，将"\f"调整为"|"，至于为何作此修改，原因暂不详
	// 后台解析也由"\f"调整为"|"
	public final void setClienPCAddr(String clientInfo) {
		String sAddr[] = clientInfo.split("\\|", -1);
		this.clientPCName = sAddr[0];
		this.clientPCAddr = sAddr[1];
		this.clientMacAddr = sAddr[2];
	}

	// begin MS01536 在系统设置中增加一【在线用户管理】模块 QDV4赢时胜上海2010年07月30日01_A
	// 2010.9.29----------------
	public final void setClienPCName(String Name) {
		this.clientPCName = Name;
	}

	// end-- MS01536 在系统设置中增加一【在线用户管理】模块 QDV4赢时胜上海2010年07月30日01_A
	// 2010.9.29----------------

	public final String clientAddr() { // 客户端完整名称
		return this.clientPCName + "@" + this.clientPCAddr;
	}

	public ApplicationContext getBaseSettingCtx() {
		if (baseSettingCtx == null) {
		    //调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428
			baseSettingCtx = new FileSystemXmlApplicationContext(
					this.getAppContextPath("basesetting.xml"));
		}
		return baseSettingCtx;
	}

	public ApplicationContext getPretFunCtx() {
		if (pretfunCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428
			pretfunCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("pretfun.xml"));
		}
		return pretfunCtx;
	}

	public ApplicationContext getVoucherCtX() {
		if (voucherCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			voucherCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("voucher.xml"));
		}
		return voucherCtx;
	}

	public void setBaseSettingCtx(ApplicationContext baseSettingCtx) {
		this.baseSettingCtx = baseSettingCtx;
	}

	public ApplicationContext getParaSettingCtx() {
		if (paraSettingCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			paraSettingCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("parasetting.xml"));
		}
		return paraSettingCtx;
	}

	public ApplicationContext getFunSettingCtx() {
		if (funSettingCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			funSettingCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("funsetting.xml"));
		}
		return funSettingCtx;
	}

	public ApplicationContext getOrderAdminCtx() {
		if (orderAdminCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			orderAdminCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("orderadmin.xml"));
		}
		return orderAdminCtx;
	}

	public ApplicationContext getDataInterfaceCtx() {
		if (dataInterfaceCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			dataInterfaceCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("datainterface.xml"));
		}
		return dataInterfaceCtx;
	}

	public ApplicationContext getCashManagerCtx() {
		if (cashManagerCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			cashManagerCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("cashmanager.xml"));
		}
		return cashManagerCtx;
	}

	// ===============add by xuxuming,20090928.MS00717,点击业务资料中的交易关联系统报错
	// ===========
	/**
	 * 得到库存管理模块对应的ApplicationContext
	 * 
	 * @return ApplicationContext
	 */
	public ApplicationContext getStorManageCtx() {
		if (storManageCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			storManageCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("storagemanage.xml"));
		}
		return storManageCtx;
	}

	public void setStorManageCtx(ApplicationContext storManageCtx) {
		this.storManageCtx = storManageCtx;
	}

	// ==================end==================================================================
	// ============add by
	// xuxuming,20091209.MS00851====================================
	public void setPretFunCtx(ApplicationContext pretFunCtx) {
		this.pretfunCtx = pretFunCtx;
	}

	// ===========================end=========================================
	public ApplicationContext getOperDataCtx() {
		if (operDataCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			operDataCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("operdata.xml"));
		}
		return operDataCtx;
	}

	public void setOperDataCtx(ApplicationContext operDataCtx) {
		this.operDataCtx = operDataCtx;
	}

	public ApplicationContext getOperDealCtx() {
		if (operdealCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			operdealCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("operdeal.xml"));
		}
		return operdealCtx;
	}

	// 财务模块 add by ctq 20090609
	public ApplicationContext getAccountCtx() {
		if (accountCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			accountCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("account.xml"));
		}
		return complianceCtx;
	}

	public void setAccBookCtx(ApplicationContext accbookCtx) {
		this.accbookCtx = accbookCtx;
	}

	public ApplicationContext getAccBookCtx() {
		if (accbookCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			accbookCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("accbook.xml"));
		}
		return accbookCtx;
	}

	public void setSettlementCtx(ApplicationContext settlementCtx) {
		this.settlementCtx = settlementCtx;
	}

	public ApplicationContext getSettlementCtx() {
		if (this.settlementCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			this.settlementCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("settlement.xml"));
		}
		return this.settlementCtx;
	}

	public void setDayFinsihCtx(ApplicationContext dayFinishCtx) {
		this.dayFinishCtx = dayFinishCtx;
	}

	public ApplicationContext getDayFinishCtx() {
		if (this.dayFinishCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			this.dayFinishCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("dayfinish.xml"));
		}
		return this.dayFinishCtx;
	}

	public ApplicationContext getCusReportCtx() {
		if (cusReportCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			cusReportCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("cusreport.xml"));
		}
		return cusReportCtx;
	}

	public ApplicationContext getPlatform() {
		if (platformCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			platformCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("platform.xml"));
		}
		return platformCtx;
	}

	public ApplicationContext getTaOperationCtx() {
		if (taOperationCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			taOperationCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("taoperation.xml"));
		}
		return taOperationCtx;
	}

	public ApplicationContext getComplianceCtx() {
		if (complianceCtx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428		
			complianceCtx = new FileSystemXmlApplicationContext(
					getAppContextPath("compliance.xml"));
		}
		return complianceCtx;
	}

	public void setCusReportCtx(ApplicationContext cusReportCtx) {
		this.cusReportCtx = cusReportCtx;
	}

	public void setParaSettingCtx(ApplicationContext paraSettingCtx) {
		this.paraSettingCtx = paraSettingCtx;
	}

	public String getWebRoot() {
		return webRoot;
	}

	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	public final void setOnlineUser(HashMap onlineUser) {
		this.onlineUser = onlineUser;
	}

	public final HashMap getOnlineUser() {
		return this.onlineUser;
	}

	public final void setClientGateway(String clientGateway) {
		this.clientGateway = clientGateway;
	}

	public final String getClientGateway() {
		return this.clientGateway;
	}

	public final String getAssetGroupCode() {
		return this.assetGroupCode;
	}
	/**
	 *  add by yh 增加组合群代码的set方法，以便直接在后台设置pub变量的当前组合群代码 yh 2011.05.25 QDV411建行2011年04月19日01_A
	 * @param assetGroupCode
	 */
	public void setAssetGroupCode(String assetGroupCode) {
		this.assetGroupCode = assetGroupCode;
	}

	public final String getAssetGroupName() {
		return this.assetGroupName;
	}

	public final String getBaseCury() {
		return this.baseCury;
	}

	// ---- QDV4上海2010年12月10日02_A---
	private HashMap portBaseCury = null;// 用于储存组合群下的组合对应的基础货币代码

	/**
	 * 把组合对应的基础货币存放在一个集合中
	 *  QDV4上海2010年12月10日02_A
	 *  lidaolong 2011.01.24
	 * @param portCode
	 * @return
	 */
	public final String getPortBaseCury(String portCode) {
		if (portBaseCury==null){//当出现不登录主系统时，该变量就为null
			return this.baseCury;
		}
		if (portBaseCury.get(portCode) != null
				&& !portBaseCury.get(portCode).equals("")) {
			return (String) portBaseCury.get(portCode);
		} else {
			return this.baseCury;
		}
	}

	/**
	 * 修改集合中的元素
	 *  QDV4上海2010年12月10日02_A
	 *  lidaolong 2011.01.25
	 * @param portCode
	 * @param curyCode
	 */
	public final void addPortBaseCury(String portCode,String curyCode) {
		
		if (portBaseCury != null){
			portBaseCury.put(portCode, curyCode);
		}		 
	}
	/**
	 * 删除集合中的元素
	 *  QDV4上海2010年12月10日02_A
	 *  lidaolong
	 * @param portCode
	 */
	public final void delPortBaseCury(String portCode){
		portBaseCury.remove(portCode);
	}
	// ---lidaolong 2011.01.24--

	/**
	 * add by songjie 2012.10.23 
	 * BUG 5901 QDV4赢时胜(上海开发部)2012年10月10日01_B
	 * 获取跨组合群对应的组合代码 和基础货币数据
	 * @throws YssException
	 */
	public void getPortCuryInfo() throws YssException{
		String strSql = "";
		ResultSet rs = null;
		try{
			strSql = " select FPortCode,FCuryCode from Tb_" + this.getPrefixTB() + "_Para_Portfolio where FCheckState = 1";
			rs = dbLink.openResultSet(strSql);
			while(rs.next()){
				this.delPortBaseCury(rs.getString("FPortCode"));
				this.addPortBaseCury(rs.getString("FPortCode"), rs.getString("FCuryCode"));
			}
		}catch(Exception e){
			throw new YssException("获取组合数据出错！");
		}
		/**shashijie 2012-11-16 BUG 6205 调度方案执行存在游标越界的问题*/
		finally{
			dbLink.closeResultSetFinal(rs);
		}
		/**end shashijie 2012-11-16 BUG */
	}
	
	public final java.util.Date getBeginDate() {
		return this.beginDate;
	}

	public final String getClientAddr() {
		return this.clientAddr;
	}

	public final String getClientMacAddr() {
		return this.clientMacAddr;
	}

	public final String getClientPCAddr() {
		return this.clientPCAddr;
	}

	public final String getClientPCName() {
		return this.clientPCName;
	}

	public final boolean isLocked() {
		return locked;
	}

	public final int getPortfolioNum() {
		return portfolioNum;
	}

	public final String getPrefixTB() {
		return prefixTB;
	}

	public final String getUserCode() {
		return userCode;
	}
	
	//---add by songjie 2011.07.18 BUG 2274 QDV4建信2011年7月14日01_B---//
	public final String getUserID() {
		return userID;
	}
	//---add by songjie 2011.07.18 BUG 2274 QDV4建信2011年7月14日01_B---//

	public final java.util.Date getUserDate() {
		return userDate;
	}

	public java.util.Date getUserLastLoginDate() {
		return userLastLoginDate;
	}

	public void setUserLastLoginDate(java.util.Date userLastLoginDate) {
		this.userLastLoginDate = userLastLoginDate;
	}

	public final String getUserDesc() {
		return userDesc;
	}

	public final boolean isUserLocked() {
		return userLocked;
	}

	public final String getUserMenubarCodes() {
		return userMenubarCodes;
	}

	// linjunyun 需求MS00016 2008-11-12 密码的安全性
	public final String getUserPassLevel() {
		return userPassLevel;
	}

	// linjunyun 需求MS00016 2008-11-12 密码有效时间
	public final int getUserPassValidTime() {
		return userPassValidTime;
	}

	public final String getUserMenuCodes() {
		return userMenuCodes;
	}

	public final String getUserName() {
		return userName;
	}

	public final String getUserPass() {
		return userPass;
	}

	public final int getUserPassLen() {
		return userPassLen;
	}

	public final int getUserPassCount() {
		return userPassCount;
	}

	public Object getCacheTag() {
		return cacheTag;
	}

	public String getBaseRateCode() {
		return baseRateCode;
	}

	public String getBaseRateSrcCode() {
		return baseRateSrcCode;
	}

	public ApplicationContext getVoucherCtx() {
		return voucherCtx;
	}

	public String getPortRateCode() {
		return portRateCode;
	}

	public String getPortRateSrcCode() {
		return portRateSrcCode;
	}

	/**
	 * QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
	 * 
	 * @return Hashtable
	 */
	public Hashtable getHtPubParams() {
		if (htPubParams == null || htPubParams.size() == 0) { // 当变量中的参数为空或无内容时就取一次
			setHtPubParams();
		}
		return htPubParams;
	}

	public void setUserMenubarCodes(String sUserMenubarCodes) {
		this.userMenubarCodes = sUserMenubarCodes;
	}

	// linjunyun 需求MS00016 2008-11-12 密码的安全性
	public void setUserPassLevel(String sUserPassLevel) {
		this.userPassLevel = sUserPassLevel;
	}

	// linjunyun 需求MS00016 2008-11-12 密码的安全性
	public void setUserPassValidTime(int sUserPassValidTime) {
		this.userPassValidTime = sUserPassValidTime;
	}

	public void setUserMenuCodes(String sUserMenuCodes) {
		this.userMenuCodes = sUserMenuCodes;
	}

	public void setCashManagerCtx(ApplicationContext cashManagerCtx) {
		this.cashManagerCtx = cashManagerCtx;
	}

	public void setFunSettingCtx(ApplicationContext funSettingCtx) {
		this.funSettingCtx = funSettingCtx;
	}

	public void setOrderAdminCtx(ApplicationContext orderAdminCtx) {
		this.orderAdminCtx = orderAdminCtx;
	}

	public void setDataInterfaceCtx(ApplicationContext dataInterfaceCtx) {
		this.dataInterfaceCtx = dataInterfaceCtx;
	}

	public void setCacheTag(Object cacheTag) {
		this.cacheTag = cacheTag;
	}

	public void setBaseRateCode(String baseRateCode) {
		this.baseRateCode = baseRateCode;
	}

	public void setBaseRateSrcCode(String baseRateSrcCode) {
		this.baseRateSrcCode = baseRateSrcCode;
	}

	public void setVoucherCtx(ApplicationContext voucherCtx) {
		this.voucherCtx = voucherCtx;
	}

	public void setPortRateCode(String portRateCode) {
		this.portRateCode = portRateCode;
	}

	public void setPortRateSrcCode(String portRateSrcCode) {
		this.portRateSrcCode = portRateSrcCode;
	}

	public void setTaOperationCtx(ApplicationContext taOperationCtx) {
		this.taOperationCtx = taOperationCtx;
	}

	public void setPlatformCtx(ApplicationContext platformCtx) {
		this.platformCtx = platformCtx;
	}

	public void setComplianceCtx(ApplicationContext complianceCtx) {
		this.complianceCtx = complianceCtx;
	}

	public void setAccountCtx(ApplicationContext ctx) {
		this.accountCtx = ctx;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	
	//---add by songjie 2011.07.18 BUG 2274 QDV4建信2011年7月14日01_B---//
	public void setUserID(String userID) {
		this.userID = userID;
	}
	//---add by songjie 2011.07.18 BUG 2274 QDV4建信2011年7月14日01_B---//

	public void setPrefixTB(String prefixTB) {
		this.prefixTB = prefixTB;
	}

	public final boolean getSysCheckState() {
		return bSysCheckState;
	}

	public void setbSysCheckState(boolean bSysCheckState) {
		this.bSysCheckState = bSysCheckState;
	}

	public final boolean getSysSelfCheck() {
		return bSysSelfCheck;
	}
    
	//add by guolongchao 20120426 添加自审功能 （明细到各个菜单）    QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc----start
	public final boolean getSysAudietOwn() {
		return bSysAudietOwn;
	}
	//add by guolongchao 20120426 添加自审功能 （明细到各个菜单）    QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc----end
	
	public final boolean getSysOtherCheck() {
		return bSysOtherCheck;
	}

	// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB 蒋世超 添加 2009.12.24 ----------------
	public int getPrefixYear() {
		return prefixYear;
	}

	public void setPrefixYear(int sYear) {
		this.prefixYear = sYear;
	}

	// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB end
	// ---------------------------------

	public void yssLogon(String assetGroupCode, String userCode)
			throws YssException {
		ResultSet rstmp = null;
		try {
			if (assetGroupCode != null) {//增加一个判断，当不登录主系统，拿我们系统当服务器时会用到 hukun 2013.5.9
				rstmp = dbLink
						.openResultSet("select * from Tb_Sys_AssetGroup where FAssetGroupCode='"
								+ assetGroupCode + "'");
				if (rstmp.next()) {
					this.assetGroupCode = rstmp.getString("FAssetGroupCode");
					// prefixTB=rstmp.getString("FAssetGroupCode");
					prefixTB = rstmp.getString("FTabPrefix") != null ? rstmp
							.getString("FTabPrefix") : "";
					assetGroupName = rstmp.getString("FAssetGroupName");
					baseCury = rstmp.getString("FBaseCury");
					locked = rstmp.getBoolean("FLocked");
					portfolioNum = rstmp.getInt("FMaxNum");
					beginDate = rstmp.getDate("FStartDate");
					this.bSysCheckState = rstmp.getBoolean("FSysCheck");
					this.baseRateSrcCode = rstmp.getString("FBaseRateSrcCode")
							+ "";
					this.baseRateCode = rstmp.getString("FBaseRateCode") + "";
					this.portRateSrcCode = rstmp.getString("FPortRateSrcCode")
							+ "";
					this.portRateCode = rstmp.getString("FPortRateCode") + "";
					// 添加参数 【用户能审核自己录入的信息】 -- BugID:MS00010 --by sunkey 20081111
					// 先判断该字段是否存在，因为更新表是在用户登陆后，而这个字段在登录窗口加载的时候就要取
					// 如果不存在该字段的话，将会报错，因此要在该字段存在的情况下才取，否则不操作。
					if (dbLink.isFieldExist(rstmp, "FCHECKSELF")) {
						this.bSysSelfCheck = rstmp.getString("FCheckSelf")
								.equalsIgnoreCase("yes") ? true : false;
					}
					// add by guolongchao 20120426 添加自审字段FAuditOwn
					// QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc-----start
					if (dbLink.isFieldExist(rstmp, "FAuditOwn")) {
						this.bSysAudietOwn = rstmp.getString("FAuditOwn")
								.equalsIgnoreCase("yes") ? true : false;
					}
					// add by guolongchao 20120426 添加自审字段FAuditOwn
					// QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc-----end
				}
				dbLink.closeResultSetFinal(rstmp); // modify by sunkey 20090602
				// MS00472:QDV4上海2009年6月02日01_B
			}

			if (userCode != null) {//增加一个判断，当不登录主系统，拿我们系统当服务器时会用到 hukun 2013.5.9
				rstmp = dbLink
						.openResultSet("select * from Tb_Sys_userList where FUserCode='"
								+ userCode + "'");
				if (rstmp.next()) {
					this.userCode = rstmp.getString("FUserCode");
					userName = rstmp.getString("FUserName");
					userDesc = rstmp.getString("Fmemo");
					// userPass = rstmp.getString("Fpass"); 2007-11-18 删除 蒋锦
					// 因为此语句不起任何作用，在使用DB2数据库时还会报错
					userPassLen = rstmp.getInt("FpassLen");
					userPassCount = rstmp.getInt("FpassCount");
					userDate = rstmp.getDate("FpassDate");
					userLocked = rstmp.getBoolean("FLocked");
					userMenuCodes = rstmp.getString("FmenusCode");
					userMenubarCodes = rstmp.getString("FmenuBarsCode");
					// xuqiji 20100319 QDV4赢时胜上海2009年12月21日06_B MS00884
					if (dbLink.isFieldExist(rstmp, "FPageShow")) {
						if (rstmp.getInt("FPageShow") < 100) {
							this.iPageCount = 100;
						} else if (rstmp.getInt("FPageShow") > 2000) {
							this.iPageCount = 2000;
						} else {
							this.iPageCount = rstmp.getInt("FPageShow");
						}
					}
					// --------------------------end
					// 20100319--------------------//
					// linjunyun 需求MS00016 2008-11-12 密码的安全性和密码有效时间存入pub
					// 先判断该字段是否存在，因为更新表是在用户登陆后，而这个字段在登录窗口加载的时候就要取
					// 如果不存在该字段的话，将会报错，因此要在该字段存在的情况下才取，否则不操作。
					// 因为两个字段是在同一版本加入的 只要一个存在，则另一个也存在,所以只判断一个就ok了
					if (dbLink.isFieldExist(rstmp, "FPassLevel")) {
						userPassLevel = rstmp.getString("FPassLevel");
						userPassValidTime = rstmp.getInt("FValidTime");
					}
					// ===add by
					// xuxuming,20091109.MS00776.获取用户上次登录系统时间==========
					if (dbLink.isFieldExist(rstmp, "FLastLoginDate")) {
						userLastLoginDate = rstmp.getDate("FLastLoginDate");
					}
					// =========end==================================
					// ---add by songjie 2011.07.18 BUG 2274
					// QDV4建信2011年7月14日01_B---//
					if (dbLink.isFieldExist(rstmp, "FUserID")) {
						userID = rstmp.getString("FUserID");
					}
					// ---add by songjie 2011.07.18 BUG 2274
					// QDV4建信2011年7月14日01_B---//
				}
			}
			
			dbLink.closeResultSetFinal(rstmp); // modify by sunkey 20090602
												// MS00472:QDV4上海2009年6月02日01_B
		} catch (SQLException se) {
			throw new YssException("获取套账全局数据出错，请重新登录！", se);
		} finally {
			dbLink.closeResultSetFinal(rstmp);
		}
	}
	//bug 3654  by zhouwei 20120120 QDV4泰达2012年01月16日01_B 重设组合群的属性（可补充）
	public void yssResetAttr(String assetGroupCode) throws YssException{
		ResultSet rs=null;
		try{
			rs = dbLink
			.openResultSet("select * from Tb_Sys_AssetGroup where FAssetGroupCode='"
					+ assetGroupCode + "'");
			while(rs.next()){
				this.bSysCheckState = rs.getBoolean("FSysCheck");//状态审核
				locked = rs.getBoolean("FLocked");//锁定
				//添加参数 【用户能审核自己录入的信息】
				if (dbLink.isFieldExist(rs, "FCHECKSELF")) {
					this.bSysSelfCheck = rs.getString("FCheckSelf")
							.equalsIgnoreCase("yes") ? true : false;
				}
			}
		}catch (Exception e) {
			throw new YssException("获取当前组合群的属性状态出错！", e);
		} finally {
			dbLink.closeResultSetFinal(rs);
		}
	}
	/**
	 * add by songjie 2011.07.18
	 * BUG 2274 QDV4建信2011年7月14日01_B
	 * 根据用户代码获取用户ID
	 * @return
	 * @throws YssException
	 */
	public void yssGetUserID() throws YssException{
		String strSql = "";
		ResultSet rs = null;
		try{
			strSql = " select * from Tb_sys_userList where FUserCode = " + 
			dbLink.sqlString(userCode);
			rs = dbLink.openResultSet(strSql);
			while(rs.next()){
				if(dbLink.isFieldExist(rs, "FUserID")){
					userID = rs.getString("FUserID");
				}
			}
		}catch(Exception e){
			throw new YssException("获取用户ID出错！", e);
		}finally{
			dbLink.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 设置组合的基础货币，存在一个集合中
	 * 'QDV4上海2010年12月10日02_A
	 * lidaolong 2010.01.25
	 * @throws YssException
	 */
	public void setPortBaseCury() throws YssException{
		ResultSet rstmp = null;
		try{
		//------ QDV4上海2010年12月10日02_A-----
		rstmp = dbLink.openResultSet("select * from Tb_" + assetGroupCode + "_Para_Portfolio");
		portBaseCury = new HashMap();
		while (rstmp.next()) {
		    this.portBaseCury.put(rstmp.getString("FPortCode"), rstmp.getString("FCuryCode"));
		}
		dbLink.closeResultSetFinal(rstmp);
		//-----add by lidaolong 2011.01.24---
	} catch (SQLException se) {
		throw new YssException("获取套账全局数据出错，请重新登录！", se);
	} finally {
		dbLink.closeResultSetFinal(rstmp);
	}
	}
	/**
	 * 给数据库自动跟新使用的获取表名方法，只给表名增加组合群前缀，不更改公共表的表名 2009-06-30 蒋锦 添加 MS00002
	 * QDV4.1赢时胜（上海）2009年4月20日02_A
	 * 
	 * @param sTableName
	 *            String
	 * @return String
	 */
	public String yssGetTableNameForUpdTables(String sTableName) {
		if (sTableName.toLowerCase().indexOf("tb_temp") < 0
				&& !sTableName.toUpperCase().startsWith("A")
				&& this.getPrefixTB().trim().length() != 0
				&& !sTableName.toLowerCase().startsWith("tb_sys")
				&& !sTableName.toLowerCase().startsWith("tb_pfsys")
				&& !sTableName.toLowerCase().startsWith("tb_base")
				&& !sTableName.toLowerCase().startsWith("tb_fun")) {
			if (YssFun.left(sTableName, 3).equalsIgnoreCase("Tb_")) {
				return YssFun.left(sTableName, 3) + this.getPrefixTB()
						+ YssFun.right(sTableName, sTableName.length() - 2);
			} else if (YssFun.left(sTableName, 6).equalsIgnoreCase("PK_Tb_")) {
				return YssFun.left(sTableName, 6) + this.getPrefixTB()
						+ YssFun.right(sTableName, sTableName.length() - 5);
			} else {
				return this.getPrefixTB() + sTableName;
			}
		} else {
			return sTableName;
		}
	}
	
	
	public String yssGetTableName(String sTableName){
		String sTempTableName="";
		sTempTableName=yssGetTbPrefixName(sTableName);
		//add by guolongchao STORY 1285  读数完成后显示浏览界面
		//总体实现思路：通过返回的表名中带上FCreateTime为当前时间点的条件来浏览刚刚读完的数据
		//isBrown：此变量在数据处理导入时设置为true，此变量存放在pub对象当中
		//tabMainCode：此变量为系统表名，在菜单条设置中设置，在数据处理导入时为此变量赋值，此变量存放在pub对象当中
		if(isBrown==true&&tabMainCode.indexOf(sTableName)>=0)
		{
			SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");			
			return " ( select * from "+sTempTableName+" where FCreateTime like '"+format1.format(new java.util.Date())+"%' )";	
		}
		return sTempTableName;
	}
	
	
	/**
	 * 获取带前缀的表名 临时表(Tb_Temp_XXXXBak)不加前缀 对 tb_sys,tb_pfsys,tb_base,tb_fun 的表不加前缀
	 * 
	 * @param sTableName
	 *            String
	 * @return String
	 */
	//update by guolongchao 20110915 STORY 1285  修改方法名称，使原方法能够正常使用
	public String yssGetTbPrefixName(String sTableName) {
		if (sTableName.toLowerCase().indexOf("tb_temp") < 0
				&& !sTableName.toUpperCase().startsWith("A")
				&& // 屏蔽A开头的财务数据表fazmm20071128
				this.getPrefixTB().trim().length() != 0
				&& !sTableName.toLowerCase().startsWith("tb_sys")
				&& !sTableName.toLowerCase().startsWith("tb_pfsys")
				&& !sTableName.toLowerCase().startsWith("tb_base")
				&& !sTableName.toLowerCase().startsWith("tb_fun")) {
			// =============MS00131 QDV4建行2008年12月25日01_A 根据通用参数来选择用哪张表
			if (sTableName.equalsIgnoreCase("tb_para_security")) {
				try { // xuqiji 2009 0415 MS00352 新建组合群时能够自动创建对应的一套表
					if (dbLink.yssTableExist("tb_para_security")) { // xuqiji
																	// 20090415
																	// MS00352
																	// 新建组合群时能够自动创建对应的一套表
						if (htPubParams == null
								|| htPubParams.get("security") == null) { // 当变量中的参数为空时就取一次
							setHtPubParams();
						}
						if (((String) htPubParams.get("security"))
								.equalsIgnoreCase("true")) { // 若变量中的参数为真就返回视图
							return "V_Base_Security";
						}
						// ---------------------------------xuqiji 20090415
						// MS00352 新建组合群时能够自动创建对应的一套表
					} else {
						return YssFun.left(sTableName, 3)
								+ this.getPrefixTB()
								+ YssFun.right(sTableName,
										sTableName.length() - 2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// -------------------------------------xuqiji 2009 0415 MS00352
				// 新建组合群时能够自动创建对应的一套表 ----------
			} else if (sTableName.equalsIgnoreCase("tb_data_marketvalue")) {
				try { // xuqiji 2009 0415 MS00352 新建组合群时能够自动创建对应的一套表
					if (dbLink.yssTableExist("tb_data_marketvalue")) { // xuqiji
																		// 2009
																		// 0415
																		// MS00352
																		// 新建组合群时能够自动创建对应的一套表
						if (htPubParams == null
								|| htPubParams.get("marketvalue") == null) { // 当变量中的参数为空时就取一次
							setHtPubParams();
						}
						if (((String) htPubParams.get("marketvalue"))
								.equalsIgnoreCase("true")) { // 若变量中的参数为真就返回视图
							return "V_Base_MarketValue";
						}
						// ---------------------------------xuqiji 20090415
						// MS00352 新建组合群时能够自动创建对应的一套表
					} else {
						return YssFun.left(sTableName, 3)
								+ this.getPrefixTB()
								+ YssFun.right(sTableName,
										sTableName.length() - 2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// -------------------------------------xuqiji 2009 0415 MS00352
				// 新建组合群时能够自动创建对应的一套表
			} else if (sTableName.equalsIgnoreCase("tb_data_exchangerate")) {
				try { // xuqiji 2009 0415 MS00352 新建组合群时能够自动创建对应的一套表
					if (dbLink.yssTableExist("tb_data_exchangerate")) { // xuqiji
																		// 2009
																		// 0415
																		// MS00352
																		// 新建组合群时能够自动创建对应的一套表
						if (htPubParams == null
								|| htPubParams.get("exchangerate") == null) { // 当变量中的参数为空时就取一次
							setHtPubParams();
						}
						if (((String) htPubParams.get("exchangerate"))
								.equalsIgnoreCase("true")) { // 若变量中的参数为真就返回视图
							return "V_Base_ExchangeRate";
						}
						// -------------------------------------xuqiji 2009 0415
						// MS00352 新建组合群时能够自动创建对应的一套表
					} else {
						return YssFun.left(sTableName, 3)
								+ this.getPrefixTB()
								+ YssFun.right(sTableName,
										sTableName.length() - 2);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// -------------------------------------xuqiji 2009 0415 MS00352
				// 新建组合群时能够自动创建对应的一套表
			}
			// ======================== MS00131
			if (YssFun.left(sTableName, 3).equalsIgnoreCase("Tb_")) {
				return YssFun.left(sTableName, 3) + this.getPrefixTB()
						+ YssFun.right(sTableName, sTableName.length() - 2);
			} else if (YssFun.left(sTableName, 6).equalsIgnoreCase("PK_Tb_")) {
				return YssFun.left(sTableName, 6) + this.getPrefixTB()
						+ YssFun.right(sTableName, sTableName.length() - 5);
			} else {
				return this.getPrefixTB() + sTableName;
			}
			// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB 蒋世超 添加 2009.12.24
			// --------------
		}
		if (sTableName.toUpperCase().startsWith("A")) {// 处理A开头的财务数据表
			String tabName = sTableName.toUpperCase(); // 屏蔽前台传进来表名大小写差异，这里将表名统一处理成小写
			if (YssFun.left(tabName, 6).equalsIgnoreCase("A<SET>")) {
				sTableName = "A" + prefixTB + sTableName.substring(6);
				return sTableName;
			} else if (YssFun.left(tabName, 12)
					.equalsIgnoreCase("A<YEAR><SET>")) {
				// 如果没有初始化日期，这默认年份是当前系统时间的年份
				if (this.prefixYear == 0) {
					this.prefixYear = Calendar.getInstance().get(Calendar.YEAR);
				}
				sTableName = "A" + prefixYear + prefixTB
						+ sTableName.substring(12);
				return sTableName;
			} else if (YssFun.left(tabName, 7).equalsIgnoreCase("A<YEAR>")) {// 年份前缀的财务报表
				sTableName = "A" + prefixYear + sTableName.substring(7);
				return sTableName;
			} else {
				return sTableName;
			}
			// --- MS00878 QDV4赢时胜上海2009年12月21日01_AB end
			// ---------------------------------
		} else {
			return sTableName;
		}
	}

	/**
	 * 获取带前缀的表名,不判断表是否存在 临时表(Tb_Temp_XXXXBak)不加前缀 对
	 * tb_sys,tb_pfsys,tb_base,tb_fun,及A开头的财务表 的表不加前缀
	 * 
	 * @param 无前缀的表名
	 * @param 组合群代码
	 * @return String
	 */
	public String yssGetTableName(String sTableName, String sAssetGroupCode)
			throws YssException {
		String PrefixTB = this.getPrefixTB(); // 默认表的前缀
		ResultSet rs = null;
		String sqlStr = "";
		try {
			sqlStr = "select FAssetGroupCode,FAssetGroupName,FTabPreFix  from tb_sys_assetgroup "
					+ " where FLocked=0 and FTabInd=1 and FAssetGroupCode="
					+ dbLink.sqlString(sAssetGroupCode);
			rs = dbLink.openResultSet(sqlStr);
			if (rs.next()) {
				PrefixTB = rs.getString("FTabPreFix");
			}
			rs.close();
			if (sTableName.toLowerCase().indexOf("tb_temp") < 0
					&& !sTableName.toUpperCase().startsWith("A")
					&& PrefixTB.length() != 0
					&& !sTableName.toLowerCase().startsWith("tb_sys")
					&& !sTableName.toLowerCase().startsWith("tb_pfsys")
					&& !sTableName.toLowerCase().startsWith("tb_base")
					&& !sTableName.toLowerCase().startsWith("tb_fun")) {
				if (YssFun.left(sTableName, 3).equalsIgnoreCase("Tb_")) {
					return YssFun.left(sTableName, 3) + PrefixTB
							+ YssFun.right(sTableName, sTableName.length() - 2);
				} else if (YssFun.left(sTableName, 6)
						.equalsIgnoreCase("PK_Tb_")) {
					return YssFun.left(sTableName, 6) + PrefixTB
							+ YssFun.right(sTableName, sTableName.length() - 5);
				} else {
					return PrefixTB + sTableName;
				}
			} else {
				return sTableName;
			}
		} catch (Exception ex) {
			throw new YssException("通过组合群代码【" + sAssetGroupCode + "】取系统表名失败");
		} finally {
			dbLink.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取不带组合群代码的表名 2009-03 蒋锦 BUG MS00352 QDV4赢时胜（上海）2009年4月7日01_A 表结构自动更新
	 * 
	 * @param strTableName
	 *            String：表名
	 * @return String
	 */
	public String yssGetUnPrefixTableName(String strTableName) {
		if (!strTableName.substring(0, 7).equalsIgnoreCase("tb_temp")
				&& !strTableName.toUpperCase().startsWith("A")
				&& // 屏蔽A开头的财务数据表fazmm20071128
				!strTableName.substring(0, 6).equalsIgnoreCase("tb_sys")
				&& !strTableName.substring(0, 8).equalsIgnoreCase("tb_pfsys")
				&& !strTableName.substring(0, 7).equalsIgnoreCase("tb_base")
				&& !strTableName.substring(0, 6).equalsIgnoreCase("tb_fun")
				&& !strTableName.substring(0, 6).equalsIgnoreCase(
						"tb_vch_subattr")
				&& !strTableName.substring(0, 7).equalsIgnoreCase("tb_data")
				&& this.prefixTB.trim().equals(strTableName.substring(3, 6))
				&& !strTableName.equalsIgnoreCase("TB_HBBROKER_DATA")
				&& !strTableName.equalsIgnoreCase("TB_BROKER_DATA")
				&& !strTableName.equalsIgnoreCase("TB_BLOOMBERG_DATA")) {
			if (YssFun.left(strTableName, 2).equalsIgnoreCase("Tb")) {
				return YssFun.left(strTableName, 2)
						+ YssFun.right(strTableName, strTableName.length() - 6);
			}
		} else if (strTableName.substring(0, 7).equalsIgnoreCase("tb_data")
				|| strTableName.substring(0, 6).equalsIgnoreCase(
						"tb_vch_subattr")
				|| strTableName.substring(0, 6).equalsIgnoreCase("tb_fun")
				|| strTableName.substring(0, 7).equalsIgnoreCase("tb_base")
				|| strTableName.substring(0, 8).equalsIgnoreCase("tb_pfsys")
				|| strTableName.substring(0, 6).equalsIgnoreCase("tb_sys")
				|| strTableName.substring(0, 7).equalsIgnoreCase("tb_temp")) {
			return strTableName;
		}
		return "";
	}

	/**
	 * 获取不带组合群代码的主键名 2009-03 蒋锦 BUG MS00352 QDV4赢时胜（上海）2009年4月7日01_A 表结构自动更新
	 * 
	 * @param strPKName
	 *            String：主键名称
	 * @return String
	 */
	public String yssGetUnPrefixPKName(String strPKName) {
		if (!strPKName.substring(3, 10).equalsIgnoreCase("tb_temp")
				&& this.prefixTB.trim().equals(strPKName.substring(6, 9))
				&& !strPKName.substring(3, 9).equalsIgnoreCase("tb_sys")
				&& !strPKName.substring(3, 11).equalsIgnoreCase("tb_pfsys")
				&& !strPKName.substring(3, 10).equalsIgnoreCase("tb_base")
				&& !strPKName.substring(3, 9).equalsIgnoreCase("tb_fun")
				&& !strPKName.substring(0, 3).equalsIgnoreCase("sys")) {
			return YssFun.left(strPKName, 5)
					+ YssFun.right(strPKName, strPKName.length() - 9);
		} else if (strPKName.substring(0, 3).equalsIgnoreCase("sys")
				|| strPKName.substring(3, 9).equalsIgnoreCase("tb_fun")
				|| strPKName.substring(3, 10).equalsIgnoreCase("tb_base")
				|| strPKName.substring(3, 11).equalsIgnoreCase("tb_pfsys")
				|| strPKName.substring(3, 9).equalsIgnoreCase("tb_sys")
				|| strPKName.substring(3, 10).equalsIgnoreCase("tb_temp")) {
			return strPKName;
		}
		return "";
	}

	/**
	 * 设置htPubParams参数的方法 QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
	 */
	private void setHtPubParams() {
		try {
			if (htPubParams == null) {
				htPubParams = new Hashtable();
			}
			EachPubDataOper EachOper = new EachPubDataOper();
			EachOper.setYssPub(this);
			htPubParams = EachOper.getHtPubParams();
		} catch (Exception ex) {
		}
	}

	// -------------xuqiji 20100319 QDV4赢时胜上海2009年12月21日06_B
	// MS00884--------------//
	public int getIPageCount() {
		return iPageCount;
	}

	public void setIPageCount(int pageCount) {
		iPageCount = pageCount;
	}

	// --------------------------------end
	// 20100319------------------------------------//
	// -xuqiji 20100311 MS01008 QDV4广发2010年3月3日01_B
	// 无法实现同一个WAR包上实现多个数据库的连接-----//
	public String getDblbl() {
		return dblbl;
	}

	public void setDblbl(String dblbl) {
		this.dblbl = dblbl;
	}
	// --------------------------end 20100311---------------------//

	public void setWebRootRealPath(String webRootRealPath) {
		this.webRootRealPath = webRootRealPath;
	}

	public String getWebRootRealPath() {
		return webRootRealPath;
	}
	
	/**
	 * 返回spring 配置文件的绝对路径
	 * 调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
	 * BUG7549 panjunfang modify 20130428
	 * @return
	 * @throws YssException
	 */
	private String getAppContextPath(String sAppContext) {
		String sAppContextPath = "";
		try{
			//weblogic用war包部署情况下会获取到NULL,所以此处加以判断
			if(null == webRootRealPath){
				sAppContextPath = YssUtil.getAppConContextPath(sAppContext);
			} else {
				//操作系统文件分隔符 
				String fileSeparator = System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\";
				sAppContextPath = "file:" + webRootRealPath + fileSeparator
						+ "cfg" + fileSeparator + sAppContext;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sAppContextPath;
	} 
}
