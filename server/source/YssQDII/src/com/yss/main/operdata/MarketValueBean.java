package com.yss.main.operdata;

import java.sql.*; //MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009.04.08
import java.util.*;

import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>
 * Title: MarketValueBean
 * </p>
 * <p>
 * Description: 行情数据
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

public class MarketValueBean extends BaseDataSettingBean implements
		IDataSetting {
	private String strMktSrcCode = ""; // 行情来源代码
	private String strMktSrcName = ""; // 行情来源名称
	private String strSecurityCode = ""; // 证券代码
	private String strSecurityName = ""; // 证券名称
	private String strPortCode = ""; // 组合代码
	private String strPortName = ""; // 组合名称
	private String strMktValueDate = "1900-01-01"; // 行情日期
	private String strMktValueTime = "00:00:00"; // 行情时间
	private double dblBargainAmount; // 总成交数量
	private double dblBargainMoney; // 总成交金额
	private double dblYClosePrice; // 昨日收盘价
	private double dblOpenPrice; // 今日开盘价
	private double dblTopPrice; // 最高价
	private double dblLowPrice; // 最低价
	private double dblClosingPrice; // 收盘价
	private double dblAveragePrice; // 平均价
	private double dblNewPrice; // 最新价
	private double dblMktPrice1; // 行情备用1
	private double dblMktPrice2; // 行情备用2
	private String strDesc = ""; // 行情描述
//	private String isOnlyColumns = "0"; // 在初始登陆时是否只显示列，不查询数据
	private String sRecycled = ""; // 保存未解析前的字符串
	private String multAuditString = "";
	private String marketStatus = ""; // 新增 行情状态，by leeyu 2008-10-17 BUG:0000486
	private String sAssetGroupCode = ""; // QDV4建行2008年12月25日01_A MS00131
	private String sAssetGroupName = ""; // added by liubo.Story #1770

	// byleeyu 20090204
	private String sOldAssetGroupCode = ""; // QDV4建行2008年12月25日01_A MS00131
	// byleeyu 20090204

	private String strOldMktSrcCode = "";
	private String strOldPortCode = "";
	private String strOldSecurityCode = "";
	private String strOldMktValueDate = "1900-01-01";
	private String strOldMktValueTime = "00:00:00";
	private HashMap price = null; // MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by
	// songjie 2009.04.08 用于储存所有的行情价格
	private MarketValueBean filterType;

	private int strCheckState = 0; // MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by
	// songjie 2009.04.08 审核状态

	// --add by songjie 20090814 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A--//
	private String startDate = null;// 接口处理界面的开始日期
	private String endDate = null;// 接口处理界面的结束日期
	private SingleLogOper logOper;
	// --add by songjie 20090814 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A--//
    private String Group;//add baopingping #story 1167 添加一个字段用来接收前台传来选择信息
    private String Iscopy;//add baopingping #story 1167 添加一个字段用来接收前台传来选择信息是否覆盖
	public String getIscopy() {
		return Iscopy;
	}

	public void setIscopy(String iscopy) {
		Iscopy = iscopy;
	}

	public String getGroup() {
		return Group;
	}

	public void setGroup(String group) {
		Group = group;
	}

	public MarketValueBean() {
	}
	
	public String getsAssetGroupName() {
		return sAssetGroupName;
	}

	public void setsAssetGroupName(String sAssetGroupName) {
		this.sAssetGroupName = sAssetGroupName;
	}

	// --MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie 2009.04.08
	/**
	 * price的set方法
	 * 
	 * @param price
	 *            HashMap
	 */
	public void setPrice(HashMap price) {
		this.price = price;
	}

	/**
	 * price的get方法
	 * 
	 * @param price
	 *            HashMap
	 */
	public HashMap getPrice() {
		return price;
	}

	// --MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie 2009.04.08

	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
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
				} else {
					sTmpStr = sRowStr;
				}
				sRecycled = sRowStr; // 把未解析的字符串先赋给sRecycled
				reqAry = sTmpStr.split("\t");
				this.strMktSrcCode = reqAry[0];
				this.strSecurityCode = reqAry[1];
				this.strMktValueDate = reqAry[2];
				this.strMktValueTime = reqAry[3];
				this.strPortCode = reqAry[4];
				if (YssFun.isNumeric(reqAry[5])) {
					this.dblBargainAmount = Double.parseDouble(reqAry[5]);
				}
				if (YssFun.isNumeric(reqAry[6])) {
					this.dblBargainMoney = Double.parseDouble(reqAry[6]);
				}
				if (YssFun.isNumeric(reqAry[7])) {
					this.dblYClosePrice = Double.parseDouble(reqAry[7]);
				}
				if (YssFun.isNumeric(reqAry[8])) {
					this.dblOpenPrice = Double.parseDouble(reqAry[8]);
				}
				if (YssFun.isNumeric(reqAry[9])) {
					this.dblTopPrice = Double.parseDouble(reqAry[9]);
				}
				if (YssFun.isNumeric(reqAry[10])) {
					this.dblLowPrice = Double.parseDouble(reqAry[10]);
				}
				if (YssFun.isNumeric(reqAry[11])) {
					this.dblClosingPrice = Double.parseDouble(reqAry[11]);
				}
				if (YssFun.isNumeric(reqAry[12])) {
					this.dblAveragePrice = Double.parseDouble(reqAry[12]);
				}
				if (YssFun.isNumeric(reqAry[13])) {
					this.dblNewPrice = Double.parseDouble(reqAry[13]);
				}
				if (YssFun.isNumeric(reqAry[14])) {
					this.dblMktPrice1 = Double.parseDouble(reqAry[14]);
				}
				if (YssFun.isNumeric(reqAry[15])) {
					this.dblMktPrice2 = Double.parseDouble(reqAry[15]);
				}
				// ------ modify by nimengjing 2010.12.02 BUG #535
				// 指数行情设置界面描述字段中存在回车符时，清除/还原报错
				if (reqAry[16] != null) {
					if (reqAry[16].indexOf("【Enter】") >= 0) {
						this.strDesc = reqAry[16].replaceAll("【Enter】", "\r\n");
					} else {
						this.strDesc = reqAry[16];
					}
				}
				// ----------------- BUG #533 ----------------//
				this.checkStateId = Integer.parseInt(reqAry[17]);
				this.strOldMktSrcCode = reqAry[18];
				this.strOldSecurityCode = reqAry[19];
				this.strOldMktValueDate = reqAry[20];
				this.strOldMktValueTime = reqAry[21];
				this.strOldPortCode = reqAry[22];
				this.isOnlyColumns = reqAry[23];
				this.marketStatus = reqAry[24]; // 新增 行情状态 by leeyu 2008-10-17
				this.sAssetGroupCode = reqAry[25]; // //QDV4建行2008年12月25日01_A
				this.sAssetGroupName = reqAry[26];		//added by liubo.Story #1770
				// MS00131 byleeyu 20090204
				this.sOldAssetGroupCode = reqAry[27]; // QDV4建行2008年12月25日01_A
				this.Group=reqAry[28];//add by baopingping #story 1167 20110718  是否进行跨组合操作
				this.Iscopy=reqAry[29];//add by baopingping #story 1167 20110718  是否进行覆盖
				super.parseRecLog();
				// MS00131 byleeyu
				// 20090204
				super.parseRecLog();
				if (sRowStr.indexOf("\r\t") >= 0) {
					if (this.filterType == null) {
						this.filterType = new MarketValueBean();
						this.filterType.setYssPub(pub);
					}
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			} else {
				if (sRowStr.indexOf("\r\t") >= 0) {
					sTmpStr = sRowStr.split("\r\t")[0];
				} else {
					sTmpStr = sRowStr;
				}
				sRecycled = sRowStr; // 把未解析的字符串先赋给sRecycled
				reqAry = sTmpStr.split("\t");
				this.strMktSrcCode = reqAry[0];
				this.strSecurityCode = reqAry[1];
				this.strMktValueDate = reqAry[2];
				this.strMktValueTime = reqAry[3];
				this.strPortCode = reqAry[4];
				if (YssFun.isNumeric(reqAry[5])) {
					this.dblBargainAmount = Double.parseDouble(reqAry[5]);
				}
				if (YssFun.isNumeric(reqAry[6])) {
					this.dblBargainMoney = Double.parseDouble(reqAry[6]);
				}
				if (YssFun.isNumeric(reqAry[7])) {
					this.dblYClosePrice = Double.parseDouble(reqAry[7]);
				}
				if (YssFun.isNumeric(reqAry[8])) {
					this.dblOpenPrice = Double.parseDouble(reqAry[8]);
				}
				if (YssFun.isNumeric(reqAry[9])) {
					this.dblTopPrice = Double.parseDouble(reqAry[9]);
				}
				if (YssFun.isNumeric(reqAry[10])) {
					this.dblLowPrice = Double.parseDouble(reqAry[10]);
				}
				if (YssFun.isNumeric(reqAry[11])) {
					this.dblClosingPrice = Double.parseDouble(reqAry[11]);
				}
				if (YssFun.isNumeric(reqAry[12])) {
					this.dblAveragePrice = Double.parseDouble(reqAry[12]);
				}
				if (YssFun.isNumeric(reqAry[13])) {
					this.dblNewPrice = Double.parseDouble(reqAry[13]);
				}
				if (YssFun.isNumeric(reqAry[14])) {
					this.dblMktPrice1 = Double.parseDouble(reqAry[14]);
				}
				if (YssFun.isNumeric(reqAry[15])) {
					this.dblMktPrice2 = Double.parseDouble(reqAry[15]);
				}
				// ------ modify by nimengjing 2010.12.02 BUG #535
				// 指数行情设置界面描述字段中存在回车符时，清除/还原报错
				if (reqAry[16] != null) {
					if (reqAry[16].indexOf("【Enter】") >= 0) {
						this.strDesc = reqAry[16].replaceAll("【Enter】", "\r\n");
					} else {
						this.strDesc = reqAry[16];
					}
				}
				// ----------------- BUG #533 ----------------/
				this.checkStateId = Integer.parseInt(reqAry[17]);
				this.strOldMktSrcCode = reqAry[18];
				this.strOldSecurityCode = reqAry[19];
				this.strOldMktValueDate = reqAry[20];
				this.strOldMktValueTime = reqAry[21];
				this.strOldPortCode = reqAry[22];
				this.isOnlyColumns = reqAry[23];
				this.marketStatus = reqAry[24]; // 新增 行情状态 by leeyu 2008-10-17
				this.sAssetGroupCode = reqAry[25]; // QDV4建行2008年12月25日01_A
				this.sAssetGroupName = reqAry[26];	//addedd by liubo.Story #1770
				// MS00131 byleeyu 20090204
				this.sOldAssetGroupCode = reqAry[27]; // QDV4建行2008年12月25日01_A
				this.Group=reqAry[28];//add baopingping #story 1167 接收前台传来选择信息
				this.Iscopy=reqAry[29];//add baopingping #story 1167 接收前台传来选择信息是否覆盖
				super.parseRecLog();
				// MS00131 byleeyu
				// 20090204
				super.parseRecLog();
				if (sRowStr.indexOf("\r\t") >= 0) {
					if (this.filterType == null) {
						this.filterType = new MarketValueBean();
						this.filterType.setYssPub(pub);
					}
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			}
		} catch (Exception e) {
			throw new YssException("解析行情数据请求信息出错", e);
		}
	}

	/**
	 * buildRowStr 返回行情数据信息
	 * 
	 * @return String
	 */
	public String buildRowStr() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.strMktSrcCode).append("\t");
		buf.append(this.strMktSrcName).append("\t");
		buf.append(this.strSecurityCode).append("\t");
		buf.append(this.strSecurityName).append("\t");
		buf.append(this.strMktValueDate).append("\t");
		buf.append(this.strMktValueTime).append("\t");
		buf.append(this.strPortCode).append("\t");
		buf.append(this.strPortName).append("\t");
		buf.append(this.dblBargainAmount).append("\t");
		buf.append(this.dblBargainMoney).append("\t");
		buf.append(this.dblYClosePrice).append("\t");
		buf.append(this.dblOpenPrice).append("\t");
		buf.append(this.dblTopPrice).append("\t");
		buf.append(this.dblLowPrice).append("\t");
		buf.append(this.dblClosingPrice).append("\t");
		buf.append(this.dblAveragePrice).append("\t");
		buf.append(this.dblNewPrice).append("\t");
		buf.append(this.dblMktPrice1).append("\t");
		buf.append(this.dblMktPrice2).append("\t");
		buf.append(this.strDesc).append("\t");
		buf.append(this.marketStatus).append("\t"); // 新增 行情状态 by leeyu
		// 2008-10-17
		buf.append(this.sAssetGroupCode).append("\t"); // QDV4建行2008年12月25日01_A
		buf.append(this.sAssetGroupName).append("\t");	//added by liubo.Story #1770
		// MS00131 byleeyu
		// 20090204
		buf.append(super.buildRecLog());
		return buf.toString();
	}

	/**
	 * 新增一条行情数据信息
	 * 
	 * @throws YssException
	 * @return String
	 */
	public String addSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String strSql = "";
		boolean bIsMarketValuePub = false; // 定义变量存放通用参数 MS00131
		// QDV4建行2008年12月25日01_A byleeyu
		// 20090205
		try {
			bIsMarketValuePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("marketvalue"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			strSql =
			// "insert into " + pub.yssGetTableName("Tb_Data_MarketValue") +
			"insert into "
					+ (bIsMarketValuePub ? "Tb_Base_MarketValue" : pub
							.yssGetTableName("Tb_Data_MarketValue"))
					+ // 根据参数决定是用哪张表 MS00131
					"(FMktSrcCode, FSecurityCode, FMktValueDate, FMktValueTime, FPortCode, "
					+ " FBargainAmount, FBargainMoney, FYClosePrice, FOpenPrice, FTopPrice, "
					+ " FLowPrice, FClosingPrice, FAveragePrice, FNewPrice, FMktPrice1, FMktPrice2,FMarketStatus, FDesc,FDataSource,"
					+ // 新增 行情状态 by leeyu 2008-10-17
					// " FCheckState, FCreator, FCreateTime,FCheckUser) " +
					" FCheckState, FCreator, FCreateTime,FCheckUser"
					+ (bIsMarketValuePub ? ",FASSETGROUPCODE" : " ")
					+ ") "
					+ // 若是新表还要加上这个字段 MS00131
					" values("
					+ dbl.sqlString(this.strMktSrcCode)
					+ ","
					+ dbl.sqlString(this.strSecurityCode)
					+ ","
					+ dbl.sqlDate(this.strMktValueDate)
					+ ","
					+ dbl.sqlString(this.strMktValueTime)
					+ ","
					+ dbl.sqlString((this.strPortCode.length() == 0) ? " "
							: this.strPortCode)
					+ ","
					+ this.dblBargainAmount
					+ ","
					+ this.dblBargainMoney
					+ ","
					+ this.dblYClosePrice
					+ ","
					+ this.dblOpenPrice
					+ ","
					+ this.dblTopPrice
					+ ","
					+ this.dblLowPrice
					+ ","
					+ this.dblClosingPrice
					+ ","
					+ this.dblAveragePrice
					+ ","
					+ this.dblNewPrice
					+ ","
					+ this.dblMktPrice1
					+ ","
					+ this.dblMktPrice2
					+ ","
					+ dbl.sqlString(this.marketStatus)
					+ ","
					+ // 新增 行情状态 by leeyu 2008-10-17
					dbl.sqlString(this.strDesc)
					+ ",1,"
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ","
					+ dbl.sqlString(this.creatorCode)
					+ ","
					+ dbl.sqlString(this.creatorTime)
					+ ","
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode))
					+ (bIsMarketValuePub ? ("," + dbl.sqlString(sAssetGroupCode
							.trim().length() > 0 ? sAssetGroupCode : " ")) : "")
					+ // 若是新表，需给这个字段赋值 MS00131 QDV4建行2008年12月25日01_A by leeyu
					// 20090205
					")";

			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return buildRowStr();
		} catch (Exception e) {
			throw new YssException("新增行情数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * checkInput 数据验证
	 * 
	 * @param btOper
	 *            byte
	 */
	public void checkInput(byte btOper) throws YssException {
		if (((String) pub.getHtPubParams().get("marketvalue"))
				.equalsIgnoreCase("true")) { // 当通用参数传过来的值为真时再给这个变量赋值 MS00131
			dbFun.checkInputCommon(
							btOper, 
							"Tb_Base_MarketValue",
							"FMktSrcCode,FSecurityCode,FMktValueDate,"
									+ "FMktValueTime,FPortCode,FASSETGROUPCODE",
							this.strMktSrcCode
									+ ","
									+ this.strSecurityCode
									+ ","
									+ this.strMktValueDate
									+ ","
									+ this.strMktValueTime
									+ ","
									+ ((this.strPortCode.length() == 0) ? " "
											: this.strPortCode) + ","
									+ sAssetGroupCode,
							this.strOldMktSrcCode
									+ ","
									+ this.strOldSecurityCode
									+ ","
									+ this.strOldMktValueDate
									+ ","
									+ this.strOldMktValueTime
									+ ","
									+ ((this.strOldPortCode.length() == 0) ? " "
											: this.strOldPortCode) + ","
									+ sOldAssetGroupCode);

		} else { // 若不是跨组合群处理，则还是用原来的方法来处理 by leeyu 20090205 MS00131
			
			String[] sAssetGroup = null;
			if ("".equals(sAssetGroupCode.trim())) {
				dbFun.checkInputCommon(btOper,
						pub.yssGetTableName("Tb_Data_MarketValue"),
						"FMktSrcCode,FSecurityCode,FMktValueDate,"
								+ "FMktValueTime,FPortCode",
						this.strMktSrcCode
								+ ","
								+ this.strSecurityCode
								+ ","
								+ this.strMktValueDate
								+ ","
								+ this.strMktValueTime
								+ ","
								+ ((this.strPortCode.length() == 0) ? " "
										: this.strPortCode),
						this.strOldMktSrcCode
								+ ","
								+ this.strOldSecurityCode
								+ ","
								+ this.strOldMktValueDate
								+ ","
								+ this.strOldMktValueTime
								+ ","
								+ ((this.strOldPortCode
										.length() == 0) ? " "
										: this.strOldPortCode));
			} else {

				sAssetGroup =  sAssetGroupCode.split(",");
				for (int i = 0; i < sAssetGroup.length; i++) {
					try {
						dbFun
								.checkInputCommon(
										btOper,
										"Tb_" + sAssetGroup[i]
												+ "_Data_MarketValue",
										"FMktSrcCode,FSecurityCode,FMktValueDate,"
												+ "FMktValueTime,FPortCode",
										this.strMktSrcCode
												+ ","
												+ this.strSecurityCode
												+ ","
												+ this.strMktValueDate
												+ ","
												+ this.strMktValueTime
												+ ","
												+ ((this.strPortCode.length() == 0) ? " "
														: this.strPortCode),
										this.strOldMktSrcCode
												+ ","
												+ this.strOldSecurityCode
												+ ","
												+ this.strOldMktValueDate
												+ ","
												+ this.strOldMktValueTime
												+ ","
												+ ((this.strOldPortCode
														.length() == 0) ? " "
														: this.strOldPortCode));

					} catch (Exception e) {
						throw new YssException("组合群【"
								+ sAssetGroup[i]
								+ "】已存在行情来源为【"
								+ this.strMktSrcCode
								+ "】,交易证券为【"
								+ this.strSecurityCode
								+ "】,行情日期为【"
								+ this.strMktValueDate
								+ "】,行情时间为【"
								+ this.strMktValueTime
								+ "】,投资组合为【"
								+ ((this.strPortCode.length() == 0) ? " "
										: this.strPortCode) + "】的数据！");
					}
				}
			}
		}
	}

	/**
	 * 修改时间：2008年3月27号 修改人：单亮 原方法功能：只能处理行情数据的审核和未审核的单条信息。
	 * 新方法功能：可以处理行情数据审核、未审核、和回收站的还原功能、还可以同时处理多条信息
	 * 新方法功能：可以处理行情数据审核、未审核、和回收站的还原功能、还可以同时处理多条信息
	 * 
	 * @throws YssException
	 */
	public void checkSetting() throws YssException {
		// 修改后的代码
		// --------------begin
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		boolean bIsMarketValuePub = false; // 定义变量存放通用参数 MS00131
		// QDV4建行2008年12月25日01_A byleeyu 20090205
		Connection conn = dbl.loadConnection();
		//add by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A
		String[] assetGroupCodes = null;
		try {
			bIsMarketValuePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("marketvalue"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			conn.setAutoCommit(false);
			bTrans = true;
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != null&&(!sRecycled.equalsIgnoreCase("")) ) {
				arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
					if(this.sAssetGroupCode.trim().length() > 0){//添加跨组合群还原功能
						assetGroupCodes = this.sAssetGroupCode.trim().split(",");
						for(int j = 0; j < assetGroupCodes.length; j++){
							strSql = "update "
								+ (bIsMarketValuePub ? "Tb_Base_MarketValue" : "Tb_" + assetGroupCodes[j] + "_Data_MarketValue")
								+ // 根据参数决定用哪张表 MS00131
								" set FCheckState = " + this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
								+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))
								+ " where FMktSrcCode = "
								// fanghaoln 20100305 MS00999 QDV4华夏2010年02月24日01_B
								+ dbl.sqlString((this.strMktSrcCode.length() == 0) ? " ": this.strMktSrcCode)
								+ " and FPortCode="
								+ dbl.sqlString((this.strPortCode.length() == 0) ? " " : this.strPortCode)
								+ " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode)
								+ " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate)
								+ " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime)
								+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" 
								// 添加上FASSETGROUPCODE主键字段
								+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : ""); 
						
							// QDV4建行2008年12月25日01_A MS00131 by leeyu
							dbl.executeSql(strSql);
						}
					}else{
						strSql = "update "
							+ (bIsMarketValuePub ? "Tb_Base_MarketValue" : pub.yssGetTableName("Tb_Data_MarketValue"))
							// 根据参数决定用哪张表 MS00131
							+ " set FCheckState = " + this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))
							+ " where FMktSrcCode = "
							// fanghaoln 20100305 MS00999 QDV4华夏2010年02月24日01_B
							+ dbl.sqlString((this.strMktSrcCode.length() == 0) ? " ": this.strMktSrcCode)
							+ " and FPortCode="
							+ dbl.sqlString((this.strPortCode.length() == 0) ? " " : this.strPortCode)
							+ " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode)
							+ " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate)
							+ " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime)
							+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" 
							// 添加上FASSETGROUPCODE主键字段
							+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : ""); 
						
						// QDV4建行2008年12月25日01_A MS00131 by leeyu
						dbl.executeSql(strSql);
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
				}
			}
			// 如果sRecycled为空，而strMktSrcCode不为空，则按照strMktSrcCode来执行sql语句
			else if (strMktSrcCode != null&&(!strMktSrcCode.equalsIgnoreCase(""))) {
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
				if(this.sAssetGroupCode.trim().length() > 0){//添加跨组合群还原功能
					assetGroupCodes = this.sAssetGroupCode.trim().split(",");
					for(int j = 0; j < assetGroupCodes.length; j++){
						strSql = "update "
							+ (bIsMarketValuePub ? "Tb_Base_MarketValue" : "Tb_" + assetGroupCodes[j] + "_Data_MarketValue")
							// 根据参数决定用哪张表 MS00131
							+ " set FCheckState = " + this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FMktSrcCode = "
							// fanghaoln 20100305 MS00999 QDV4华夏2010年02月24日01_B
							+ dbl.sqlString((this.strMktSrcCode.length() == 0) ? " " : this.strMktSrcCode)
							+ " and FPortCode="
							+ dbl.sqlString((this.strPortCode.length() == 0) ? " " : this.strPortCode)
							+ " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode)
							+ " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate)
							+ " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime)
							+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" 
							// 添加上FASSETGROUPCODE主键字段
							+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : ""); 
						
						// QDV4建行2008年12月25日01_A MS00131 by leeyu
						dbl.executeSql(strSql);
					}
				}else{
					strSql = "update "
						+ (bIsMarketValuePub ? "Tb_Base_MarketValue" : pub.yssGetTableName("Tb_Data_MarketValue"))
						// 根据参数决定用哪张表 MS00131
						+ " set FCheckState = " + this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'"
						+ " where FMktSrcCode = "
						// fanghaoln 20100305 MS00999 QDV4华夏2010年02月24日01_B
						+ dbl.sqlString((this.strMktSrcCode.length() == 0) ? " " : this.strMktSrcCode)
						+ " and FPortCode="
						+ dbl.sqlString((this.strPortCode.length() == 0) ? " " : this.strPortCode)
						+ " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode)
						+ " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate)
						+ " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime)
						+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" 
						// 添加上FASSETGROUPCODE主键字段
						+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : ""); 
					
					// QDV4建行2008年12月25日01_A MS00131 by leeyu
					dbl.executeSql(strSql);
				}
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核行情数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		// ---------------------end

	}

	/**
	 * 删除一条行情数据信息,即放入回收站
	 * 
	 * @throws YssException
	 */
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		boolean bIsMarketValuePub = false; // 定义变量存放通用参数 MS00131
		// QDV4建行2008年12月25日01_A byleeyu
		// 20090205
		String strSql = "";
		try {
			bIsMarketValuePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("marketvalue"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			// strSql = "update " + pub.yssGetTableName("Tb_Data_MarketValue") +
			strSql = "update "
					+ (bIsMarketValuePub ? "Tb_Base_MarketValue" : pub
							.yssGetTableName("Tb_Data_MarketValue"))
					+ // 根据参数决定用哪张表 MS00131
					" set FCheckState = "
					+ this.checkStateId
					+ ", FCheckUser = "
					+ dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "'"
					+ " where FMktSrcCode = "
					+ dbl.sqlString(this.strMktSrcCode)
					+ " and FPortCode="
					+ dbl.sqlString((this.strPortCode.length() == 0) ? " "
							: this.strPortCode)
					+ " and FSecurityCode="
					+ dbl.sqlString(this.strSecurityCode)
					+ " and FMktValueDate="
					+ dbl.sqlDate(this.strMktValueDate)
					+ " and FMktValueTime="
					+ dbl.sqlString(this.strMktValueTime)
					+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" + dbl
							.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode
									: " "))
							: ""); // 添加上FASSETGROUPCODE主键字段
			// QDV4建行2008年12月25日01_A MS00131 by leeyu
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除行情数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * editOperData 编辑一条行情数据信息
	 * 
	 * @return String
	 */
	public String editSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		boolean bIsMarketValuePub = false; // 定义变量存放通用参数 MS00131
		// QDV4建行2008年12月25日01_A byleeyu
		// 20090205
		String strSql = "";
		try {
			bIsMarketValuePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("marketvalue"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			// strSql = "update " + pub.yssGetTableName("Tb_Data_MarketValue") +
			strSql = "update "
					+ (bIsMarketValuePub ? "Tb_Base_MarketValue" : pub
							.yssGetTableName("Tb_Data_MarketValue"))
					+ // 根据参数决定用哪张表 MS00131
					" set FMktSrcCode = "
					+ dbl.sqlString(this.strMktSrcCode)
					+ ", FSecurityCode = "
					+ dbl.sqlString(this.strSecurityCode)
					+ ",FPortCode = "
					+ dbl.sqlString((this.strPortCode.length() == 0) ? " "
							: this.strPortCode)
					+ ",FMktValueDate = "
					+ dbl.sqlDate(this.strMktValueDate)
					+ ",FMktValueTime = "
					+ dbl.sqlString(this.strMktValueTime)
					+ ", FBargainAmount = "
					+ this.dblBargainAmount
					+ ", FBargainMoney ="
					+ this.dblBargainMoney
					+ ", FTopPrice="
					+ this.dblTopPrice
					+ ", FYClosePrice = "
					+ this.dblYClosePrice
					+ ",FOpenPrice = "
					+ this.dblOpenPrice
					+ ", FLowPrice = "
					+ this.dblLowPrice
					+ ", FClosingPrice = "
					+ this.dblClosingPrice
					+ ", FAveragePrice = "
					+ this.dblAveragePrice
					+ ", FNewPrice = "
					+ this.dblNewPrice
					+ ", FMktPrice1 = "
					+ this.dblMktPrice1
					+ ", FMktPrice2 = "
					+ this.dblMktPrice2
					+ ", FDesc = "
					+ dbl.sqlString(this.strDesc)
					+ ", FCreator = "
					+ dbl.sqlString(this.creatorCode)
					+ " , FCreateTime = "
					+ dbl.sqlString(this.creatorTime)
					+ ",FMarketStatus ="
					+ dbl.sqlString(this.marketStatus)
					+ // 新增 行情状态 by leeyu 2008-10-17
					(bIsMarketValuePub ? (", FASSETGROUPCODE=" + dbl
							.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode
									: " "))
							: "")
					+ // 添加上FASSETGROUPCODE主键字段 QDV4建行2008年12月25日01_A MS00131 by
					// leeyu
					" where FMktSrcCode = "
					+ dbl.sqlString(this.strOldMktSrcCode)
					+ " and FPortCode = "
					+ dbl.sqlString((this.strOldPortCode.length() == 0) ? " "
							: this.strOldPortCode)
					+ " and FSecurityCode = "
					+ dbl.sqlString(this.strOldSecurityCode)
					+ " and FMktValueDate = "
					+ dbl.sqlDate(this.strOldMktValueDate)
					+ " and FMktValueTime = "
					+ dbl.sqlString(this.strOldMktValueTime)
					+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" + dbl
							.sqlString(sOldAssetGroupCode.trim().length() > 0 ? sOldAssetGroupCode
									: " "))
							: ""); // 添加上FASSETGROUPCODE主键字段
			// QDV4建行2008年12月25日01_A MS00131 by leeyu
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return buildRowStr();
		} catch (Exception e) {
			throw new YssException("修改行情数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	/**
	 * 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() throws YssException {
		String sResult = "";
		if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
		if (this.filterType != null) {
			sResult = " where 1=1 ";
			if (this.filterType.isOnlyColumns.equals("1")) {
				sResult = sResult + " and 1 = 2";
				return sResult;
			}
			if (this.filterType.strMktSrcCode.length() != 0) {
				sResult = sResult + " and a.FMktSrcCode like '"
						+ filterType.strMktSrcCode.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.strPortCode.length() != 0) {
				sResult = sResult + " and a.FPortCode like '"
						+ filterType.strPortCode.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.strSecurityCode.length() != 0) {
				sResult = sResult + " and a.FSecurityCode like '"
						+ filterType.strSecurityCode.replaceAll("'", "''")
						+ "%'";
			}
			//update by guolongchao 20110920 STORY 1285 将默认的时间1900-01-01排除掉
			if (this.filterType.strMktValueDate.length() != 0 && !this.filterType.strMktValueDate.equals("1900-01-01")
					&& !this.filterType.strMktValueDate.equals("9998-12-31")) {
				sResult = sResult + " and a.FMktValueDate = "
						+ dbl.sqlDate(filterType.strMktValueDate);
			}
			if (!this.filterType.strMktValueTime.equalsIgnoreCase("00:00:00") &&
				//edit by songjie 2011.06.22 BUG 2023 QDV4富国2011年5月31日01_B
				!this.filterType.strMktValueTime.equalsIgnoreCase("0:00:00")) {
				sResult = sResult + " and a.FMktValueTime = "
						+ dbl.sqlString(filterType.strMktValueTime);
			}
			if (this.filterType.dblBargainAmount > 0) {
				sResult = sResult + " and a.FBargainAmount ="
						+ filterType.dblBargainAmount;
			}
			if (this.filterType.dblBargainMoney > 0) {
				sResult = sResult + " and a.FBargainMoney ="
						+ filterType.dblBargainMoney;
			}
			if (this.filterType.dblYClosePrice > 0) {
				sResult = sResult + " and a.FYClosePrice = "
						+ filterType.dblYClosePrice;
			}
			if (this.filterType.dblOpenPrice > 0) {
				sResult = sResult + " and a.FOpenPrice = "
						+ filterType.dblOpenPrice;
			}
			if (this.filterType.dblLowPrice > 0) {
				sResult = sResult + " and a.FLowPrice = "
						+ filterType.dblLowPrice;
			}
			if (this.filterType.dblTopPrice > 0) {
				sResult = sResult + " and a.FTopPrice = "
						+ filterType.dblTopPrice;
			}
			if (this.filterType.dblClosingPrice > 0) {
				sResult = sResult + " and a.FClosingPrice = "
						+ filterType.dblClosingPrice;
			}
			if (this.filterType.dblAveragePrice > 0) {
				sResult = sResult + " and a.FAveragePrice = "
						+ filterType.dblAveragePrice;
			}
			if (this.filterType.dblNewPrice > 0) {
				sResult = sResult + " and a.FNewPrice = "
						+ filterType.dblNewPrice;
			}
			if (this.filterType.dblMktPrice1 > 0) {
				sResult = sResult + " and a.FMktPrice1 = "
						+ filterType.dblMktPrice1;
			}
			if (this.filterType.dblMktPrice2 > 0) {
				sResult = sResult + " and a.FMktPrice2 = "
						+ filterType.dblMktPrice2;
			}
			if (this.filterType.strDesc.length() != 0) {
				sResult = sResult + " and a.FDesc like '"
						+ filterType.strDesc.replaceAll("'", "''") + "%'";
			}
			// =====================新增 行情状态 by leeyu 2008-10-17
			if (filterType.marketStatus != null
					&& filterType.marketStatus.length() != 0) {
				sResult += " and a.FMarketStatus like '"
						+ filterType.marketStatus.replaceAll("'", "''") + "%'";
			}
			// ======================2008-10-17
			if (((String) pub.getHtPubParams().get("marketvalue"))
					.equalsIgnoreCase("true")) { // 当通用参数传过来的值为真时再给这个变量赋值
				if (this.filterType.sAssetGroupCode.trim().length() != 0) {
					sResult = sResult + " and a.FASSETGROUPCODE ='"
							+ filterType.sAssetGroupCode.replaceAll("'", "''")
							+ "'";
				}
			} // QDV4建行2008年12月25日01_A MS00131 by leeyu 20090204

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
		String sDateStr = "";
		String strSql = "";
		ResultSet rs = null;
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		boolean bIsMarketValuePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
		// MS00131 byleeyu 20090204
		try {
			sHeader = this.getListView1Headers();
			bIsMarketValuePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("marketvalue"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			// fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A 20100708
			// 优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
			if (this.filterType.isOnlyColumns.equals("1")&&!(pub.isBrown())) {
				return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
						+ "\r\f" + this.getListView1ShowCols() + "\r\f"
						+ yssPageInationBean.buildRowStr() + "\r\f";// QDV4赢时胜上海2010年03月15日06_B
				// MS00884
				// by xuqiji
			}
			// --------------------------------------end
			// MS01310--------------------------------------------------------
			strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
					+ " d.FPortName, e.FSecurityName, f.FMktSrcName " +
					" ,ass.FAssetGroupName as FAssetGroupName" + 
					// " from " + pub.yssGetTableName("Tb_Data_MarketValue") +
					// " a " +
					" from "
					+ (bIsMarketValuePub ? pub.yssGetTableName("Tb_Base_MarketValue"): //update by guolongchao 20110920 STORY 1285 对Tb_Base_MarketValue表添加当前时间点限制
						  pub.yssGetTableName("Tb_Data_MarketValue"))
					+ " a "
					+ // 根据参数选择表 MS00131 byleeyu 20090205
					" left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +

	                " left join (select FAssetGroupCode,FAssetGroupName from tb_sys_AssetGroup) ass on a.FAssetGroupCode = ass.FAssetGroupCode"	//added by liubo.Story #1770
					+ " left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据
					//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//					+ pub.yssGetTableName("Tb_Para_Portfolio")
//					+ " where FStartDate <= "
//					+ dbl.sqlDate(new java.util.Date())
					//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
					+ " select FPortCode, FPortName, FStartDate from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FCheckState = 1) d on a.FPortCode = d.FPortCode "//
					+ " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode "
					+ " left join (select FMktSrcCode, FMktSrcName from "
					+ pub.yssGetTableName("Tb_Para_MarketSource")
					+ ") f on a.FMktSrcCode = f.FMktSrcCode "
					+
					// buildFilterSql() +
					// " order by a.FCheckState, a.FCreateTime desc";
					buildFilterSql()
					
				//modified by liubo.Story #1770
				//==================================
//					+ (bIsMarketValuePub ? (" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE='"
//							+ pub.getAssetGroupCode() + "') ")
//							: " ") + // 若是新表还要添加上条件 by leeyu 20090205 MS00131
//					" order by a.FCheckState, a.FCreateTime desc";
					+ (bIsMarketValuePub ? (" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like '%" + pub.getAssetGroupCode() + "%') "): " ") + // 若是新表还要添加上条件 by leeyu 20090205 MS00131
					" order by a.FCheckState, a.FCreateTime desc";
				//=====================end=============
			
			// QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("MarketValue");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
			while (rs.next()) {
				//modified by liubo.Story #1770.
				//用tmpAssetGroupName变量保存组合群名称，然后使用基类重载的buildRowShowStr方法将组合群名称插入ListView的数据中
				//================================
				String tmpAssetGroupName = this.getGroupNameFromGroupCode(rs.getString("FASSETGROUPCODE"));	
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols(),tmpAssetGroupName))
						.append(YssCons.YSS_LINESPLITMARK);

				//================end================
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
			System.out.println();
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f"
					+ yssPageInationBean.buildRowStr() + "\r\f";// QDV4赢时胜上海2010年03月15日06_B
			// MS00884 by
			// xuqiji
		} catch (Exception e) {
			throw new YssException("获取行情数据信息出错！", e);
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
		String sDateStr = "";
		String strSql = "";
		ResultSet rs = null;
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		boolean bIsMarketValuePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
		// MS00131 byleeyu 20090204
		try {
			sHeader = "证券名称\t行情来源\t行情日期\t行情时间\t所属组合\t行情描述";
			bIsMarketValuePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("marketvalue"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
				+ " d.FPortName, e.FSecurityName, f.FMktSrcName " +
				// " from " + pub.yssGetTableName("Tb_Data_MarketValue") +
				// " a " +
				" from "
				+ (bIsMarketValuePub ? "Tb_Base_MarketValue" : pub
						.yssGetTableName("Tb_Data_MarketValue"))
				+ " a "
				+ // 根据参数选择哪张表 MS00131 by leeyu QDV4建行2008年12月25日01_A
				" left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
				+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
				+ " left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据
				//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//				+ pub.yssGetTableName("Tb_Para_Portfolio")
//				+ " where FStartDate <= "
//				+ dbl.sqlDate(new java.util.Date())
				//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
				+ "select FPortCode, FPortName, FStartDate from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
				+ pub.yssGetTableName("Tb_Para_Portfolio")
				+ " where FCheckState = 1) d on a.FPortCode = d.FPortCode "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
				+ " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from "
				+ pub.yssGetTableName("Tb_Para_Security")
				+ " where FStartDate <= "
				+ dbl.sqlDate(new java.util.Date())
				+ " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate from "
				+ pub.yssGetTableName("Tb_Para_Security")
				+ ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode "
				+ " left join (select FMktSrcCode, FMktSrcName from "
				+ pub.yssGetTableName("Tb_Para_MarketSource")
				+ ") f on a.FMktSrcCode = f.FMktSrcCode "
				+ ((buildFilterSql().length() == 0) ? " where "
						: buildFilterSql() + " and ")
						
				//modified by liubo.Story #1770
				//===============================
//				+ (bIsMarketValuePub ? ("(a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE='"
//						+ pub.getAssetGroupCode() + "')")
				+ (bIsMarketValuePub ? ("(a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like '%" + pub.getAssetGroupCode() + "%')")
				//===============end================
						
						: " and ") + // 若新表需添加上查询条件 QDV4建行2008年12月25日01_A
				// MS00131 by leeyu 20090205
				" a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append((rs.getString("FSecurityName") + "").trim())
						.append("\t");
				bufShow.append((rs.getString("FMktSrcName") + "").trim())
						.append("\t");
				bufShow
						.append(
								(YssFun.formatDate(rs.getDate("FMktValueDate"))))
						.append("\t");
				bufShow.append((rs.getString("FMktValueTime") + "").trim())
						.append("\t");
				bufShow.append((rs.getString("FPortName") + "").trim()).append(
						"\t");
				bufShow.append(
						YssFun.left((rs.getString("FDesc") + "").trim(), 40))
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
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
		} catch (Exception e) {
			throw new YssException("获取行情数据信息出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * getListViewData3 edit by songjie 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
	 * 
	 * @return String
	 */
	public String getListViewData3() throws YssException {
		String strSql = null;// 用于储存sql语句
		ResultSet rs = null;// 声明结果集
		String resultInfo = "noInfo";
		try {
			// 添加行情来源字段作为查询条件 edit by songjie 2009-09-21
			strSql = " select distinct FMktValueDate from "
					+ pub.yssGetTableName("Tb_Data_MarketValue")
					+ " where FMktValueDate between " + dbl.sqlDate(startDate)
					+ " and " + dbl.sqlDate(endDate)
					+ " and FMktSrcCode = 'CG' ";
			rs = dbl.openResultSet(strSql);// 在行情数据中查询行情日期在接口处理界面输入的开始日期和结束日期之间的数据
			while (rs.next()) {
				resultInfo = "haveInfo";// 表示有行情数据
			}
			return resultInfo;
		} catch (Exception e) {
			throw new YssException("在查询行情数据表中查询相关交易日期的行情数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * getOperData
	 */
	public void getOperData() {
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

	public void setStorageAttr(ResultSet rs) throws SQLException {

		super.setRecLog(rs);
	}

	/**
	 * saveMutliOperData
	 * 
	 * @param sMutilRowStr
	 *            String
	 * @return String
	 */
	public String saveMutliOperData(String sMutilRowStr) throws YssException {
		return "";
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException,
			YssException {
		this.strMktSrcCode = rs.getString("FMktSrcCode") + "";
		this.strMktSrcName = rs.getString("FMktSrcName") + "";
		this.strSecurityCode = rs.getString("FSecurityCode") + "";
		this.strSecurityName = rs.getString("FSecurityName") + "";
		this.strMktValueDate = YssFun.formatDate(rs.getDate("FMktValueDate"));
		this.strMktValueTime = rs.getString("FMktValueTime") + "";
		this.strPortCode = rs.getString("FPortCode").trim() + "";
		this.strPortName = rs.getString("FPortName") + "";
		this.dblBargainAmount = rs.getDouble("FBargainAmount");
		this.dblBargainMoney = rs.getDouble("FBargainMoney");
		this.dblYClosePrice = rs.getDouble("FYClosePrice");
		this.dblOpenPrice = rs.getDouble("FOpenPrice");
		this.dblTopPrice = rs.getDouble("FTopPrice");
		this.dblLowPrice = rs.getDouble("FLowPrice");
		this.dblClosingPrice = rs.getDouble("FClosingPrice");
		this.dblAveragePrice = rs.getDouble("FAveragePrice");
		this.dblNewPrice = rs.getDouble("FNewPrice");
		this.dblMktPrice1 = rs.getDouble("FMktPrice1");
		this.dblMktPrice2 = rs.getDouble("FMktPrice2");
		this.strDesc = rs.getString("FDesc") + "";
		this.marketStatus = rs.getString("FMarketStatus") + ""; // 新增 行情状态 by
		
		//modified by liubo.Story #1770
		//============================
		// leeyu
		// 2008-10-17
//		if (((String) pub.getHtPubParams().get("marketvalue"))
//				.equalsIgnoreCase("true")) { // 当通用参数传过来的值为真时再给这个变量赋值
			this.sAssetGroupCode = rs.getString("FASSETGROUPCODE"); // QDV4建行2008年12月25日01_A
			// MS00131
			// by leeyu
			// 20090205
//		}
		//==========end==================
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
		// -------------------------彭鹏 20080328 新增批量审核功能----------------------//
		if (sType!=null&&sType.equalsIgnoreCase("multauditMarketValue")) {
			if (multAuditString.length() > 0) {
				//modify baopingping #story 1167 20110720 添加对跨组合群的处理
				if(this.Group.equalsIgnoreCase("ok")){
					String FAssetGroupCode=this.getAssdeGroup();
			    	String[] GroupCode=null;
			    	String ss="";
			    	System.out.println(this.strMktSrcCode);
					if(FAssetGroupCode!=null)
					{
					   GroupCode=FAssetGroupCode.split("\t");
					 //modified by yeshenghong for CCB SAFE CHECK 20120918
						for(int i=0;i<GroupCode.length;i++){
						 this.auditData(this.multAuditString,GroupCode[i]);// 跨组合执行批量审核/反审核
						}
					}
				}else{//------------end---------
					return this.auditMutli(this.multAuditString); // 执行批量审核/反审核
				}
				
			}
		}
		// ------------------------------------------------------------------------//
		// --add by songjie 20080814 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
		// ---//
		if (sType!=null&&sType.indexOf("checkHQInfo") != -1) {
			if (!sType.equals("") && sType.split("/t").length >= 2
					&& (sType.split("/t")[1]).split("\f\f").length >= 2) {
				// delete by songjie 2009.12.21 QDII国内：MS00858
				// QDV4赢时胜(上海)2009年12月09日07_B
				// startDate =
				// (sType.split("/t")[1]).split("\f\f")[0].substring(0,8);
				// endDate =
				// (sType.split("/t")[1]).split("\f\f")[0].substring(0,8);
				// delete by songjie 2009.12.21 QDII国内：MS00858
				// QDV4赢时胜(上海)2009年12月09日07_B
				// add by songjie 2009.12.21 QDII国内：MS00858
				// QDV4赢时胜(上海)2009年12月09日07_B
				startDate = YssFun.formatDate((sType.split("/t")[1])
						.split("\f\f")[0], "yyyy-MM-dd");
				endDate = YssFun.formatDate((sType.split("/t")[1])
						.split("\f\f")[0], "yyyy-MM-dd");
				// add by songjie 2009.12.21 QDII国内：MS00858
				// QDV4赢时胜(上海)2009年12月09日07_B
			}
			return getListViewData3();// 返回查询后的结果
		}
		// --add by songjie 20080814 国内：MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
		// ---//
		if (sType != null && sType.equalsIgnoreCase("getPubParamMarketvalue")) { // QDV4建行2008年12月25日01_A
			// MS00131
			// byleeyu
			// 20090204
			return (String) pub.getHtPubParams().get("marketvalue"); // 获取行情跨组合群处理的通用参数的数据
		}		
		return "";
	}

	// -------------------------彭鹏 20080328 新增批量审核功能----------------------
	public String auditMutli(String sMutilRowStr) throws YssException {
		Connection conn = dbl.loadConnection();
		MarketValueBean data = null;
		boolean bTrans = false;
		boolean bIsMarketValuePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
		// MS00131 byleeyu 20090204
		String[] multAudit = null;
		String strSql = "";
		try {
			if (multAuditString.length() > 0) {
				bIsMarketValuePub = Boolean.valueOf(
						(String) pub.getHtPubParams().get("marketvalue"))
						.booleanValue(); // 获取PUB中参数的值 MS00131
				multAudit = sMutilRowStr.split("\f\f\f\f");
				if (multAudit.length > 0) {
					for (int i = 0; i < multAudit.length; i++) {
		            	//modified by liubo.Story #1770
		            	//解析前台传入的组合群代码，根据组合群代码分别拼装成需要的数据表，然后进行操作
		            	//===========================
						data = new MarketValueBean();
						data.setYssPub(pub);
						data.parseRowStr(multAudit[i]);
						String[] sGroupCode = ("".equals(data.getSAssetGroupCode().trim()) ? pub.getAssetGroupCode() : data.getSAssetGroupCode()).split(",");
                        for (int j = 0;j<sGroupCode.length;j++)
                        {
						strSql = "update "
//								+ (bIsMarketValuePub ? "Tb_Base_MarketValue"
//										: pub
//												.yssGetTableName("Tb_Data_MarketValue"))
//								+ // 根据参数决定是用哪张表 MS00131 by leeyu
								+ ("".equals(sGroupCode[j].trim()) ? pub.yssGetTableName("Tb_Data_MarketValue") : "Tb_" + sGroupCode[j] + "_Data_MarketValue")								
								+ " set FCheckState = "
								+ this.checkStateId
								+ ", FCheckUser = "
								+ dbl.sqlString(pub.getUserCode())
								+ ", FCheckTime = '"
								+ YssFun.formatDatetime(new java.util.Date())
								+ "'"
								+ " where FMktSrcCode = "
								+
								// fanghaoln 20100305 MS00999
								// QDV4华夏2010年02月24日01_B
								dbl
										.sqlString((data.strMktSrcCode.length() == 0) ? " "
												: data.strMktSrcCode)
								+
								// ------------------------end
								// ---------------------------------------------
								" and FPortCode="
								+ dbl
										.sqlString((data.strPortCode.length() == 0) ? " "
												: data.strPortCode)
								+ " and FSecurityCode="
								+ dbl.sqlString(data.strSecurityCode)
								+ " and FMktValueDate="
								+ dbl.sqlDate(data.strMktValueDate)
								+ " and FMktValueTime="
								+ dbl.sqlString(data.strMktValueTime)
								+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" + dbl
										.sqlString(data.sAssetGroupCode.trim()
												.length() > 0 ? data.sAssetGroupCode
												: " "))
										: " "); // 添加上FASSETGROUPCODE主键字段
						// QDV4建行2008年12月25日01_A MS00131
						// by leeyu
						conn.setAutoCommit(false);
						bTrans = true;
						dbl.executeSql(strSql);
						conn.commit();
						bTrans = false;
						conn.setAutoCommit(true);
						// ---增加批量删除的日志记录功能----guojianhua add 20100906-------//
						logOper = SingleLogOper.getInstance();
						data = this;
						if (this.checkStateId == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (this.checkStateId == 1) {
							data.checkStateId = 1;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						} else if (this.checkStateId == 0) {
							data.checkStateId = 0;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						}
						// -----------------------------------------//
					}
                    //================end=====================
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("删除行情数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return "";
	}

	// ---------------------------------------------------------------------------

	/**
	 * getBeforeEditData
	 * 
	 * @return String
	 */
	public String getBeforeEditData() throws YssException {
		MarketValueBean befEditBean = new MarketValueBean();
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
					+ " d.FPortName, e.FSecurityName, f.FMktSrcName "
					+ " from "
					+ pub.yssGetTableName("Tb_Data_MarketValue")
					+ " a "
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
					+ " left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据
					//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//					+ pub.yssGetTableName("Tb_Para_Portfolio")
//					+ " where FStartDate <= "
//					+ dbl.sqlDate(new java.util.Date())
					//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
					+ " select FPortCode, FPortName, FStartDate from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FCheckState = 1) d on a.FPortCode = d.FPortCode "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
					+ " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode "
					+ " left join (select FMktSrcCode, FMktSrcName from "
					+ pub.yssGetTableName("Tb_Para_MarketSource")
					+ ") f on a.FMktSrcCode = f.FMktSrcCode "
					+ " where  a.FMktSrcCode ="
					+ dbl.sqlString(this.strOldMktSrcCode)
					+ " and a.FSecurityCode="
					+ dbl.sqlString(this.strOldSecurityCode)
					+ " and a.FMktValueDate="
					+ dbl.sqlDate(this.strOldMktValueDate)
					+ " and a.FMktValueTime="
					+ dbl.sqlString(this.strOldMktValueTime)
					+ (this.strOldPortCode.length() > 0 ? "and a.FPortCode="
							+ dbl.sqlString(this.strOldPortCode) : "and 1=1")
					+ " order by a.FCheckState, a.FCreateTime desc";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				befEditBean.strMktSrcCode = rs.getString("FMktSrcCode") + "";
				befEditBean.strMktSrcName = rs.getString("FMktSrcName") + "";
				befEditBean.strSecurityCode = rs.getString("FSecurityCode")
						+ "";
				befEditBean.strSecurityName = rs.getString("FSecurityName")
						+ "";
				befEditBean.strMktValueDate = YssFun.formatDate(rs
						.getDate("FMktValueDate"));
				befEditBean.strMktValueTime = rs.getString("FMktValueTime")
						+ "";
				befEditBean.strPortCode = rs.getString("FPortCode").trim() + "";
				befEditBean.strPortName = rs.getString("FPortName") + "";
				befEditBean.dblBargainAmount = rs.getDouble("FBargainAmount");
				befEditBean.dblBargainMoney = rs.getDouble("FBargainMoney");
				befEditBean.dblYClosePrice = rs.getDouble("FYClosePrice");
				befEditBean.dblOpenPrice = rs.getDouble("FOpenPrice");
				befEditBean.dblTopPrice = rs.getDouble("FTopPrice");
				befEditBean.dblLowPrice = rs.getDouble("FLowPrice");
				befEditBean.dblClosingPrice = rs.getDouble("FClosingPrice");
				befEditBean.dblAveragePrice = rs.getDouble("FAveragePrice");
				befEditBean.dblNewPrice = rs.getDouble("FNewPrice");
				befEditBean.dblMktPrice1 = rs.getDouble("FMktPrice1");
				befEditBean.dblMktPrice2 = rs.getDouble("FMktPrice2");
				befEditBean.strDesc = rs.getString("FDesc") + "";
				befEditBean.marketStatus = rs.getString("FMarketStatus") + ""; // 新增
				// 行情状态
				// by
				// leeyu
				// 2008-10-17

			}
			return befEditBean.buildRowStr();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	/**
	 * getListViewData4
	 * 
	 * @return String
	 */
	public String getListViewData4() {
		return "";
	}

	/**
	 * saveMutliSetting
	 * add baopingping 20110715 #story 1167
	 * 跨组合 添加，删除，修改，复制，审核的入口
	 * @param sMutilRowStr
	 * @return String
	 * @throws YssException 
	 */
	public String saveMutliSetting(String flag) throws YssException {
		String msg="";
		//modified by liubo.Story #1770
		//================================
//		String FAssetGroupCode=Mark.getAssdeGroup();
    	String[] GroupCode=null;
//		if(FAssetGroupCode!=null)
//		{
//		   GroupCode=FAssetGroupCode.split("\t");
//		}
    	if (flag.equalsIgnoreCase("copy")||flag.equalsIgnoreCase("add")){
    		checkInput("".equals(this.sAssetGroupCode.trim()) ? YssCons.OP_ADD : YssCons.OP_MutliAdd);
    	}else if(flag.equalsIgnoreCase("edit")){
    		checkInput("".equals(this.sAssetGroupCode.trim()) ? YssCons.OP_EDIT : YssCons.OP_MutliEdit);
    	}

    	GroupCode = ("".equals(this.sAssetGroupCode.trim()) ? pub.getAssetGroupCode() : this.sAssetGroupCode).split(",");
    	//===============end==================
		for(int i=0;i<GroupCode.length;i++)
		{
			String tableName=this.getTable(GroupCode[i]);
			if(flag.equalsIgnoreCase("add")){
			if(this.Iscopy.equalsIgnoreCase("yes")){
			if(tableName.equalsIgnoreCase("none"))
			{
				this.adddata(GroupCode[i]);
			}else{
				this.editData(GroupCode[i]);
			}
			}else{
				if(tableName.equalsIgnoreCase("none"))
				{
					this.adddata(GroupCode[i]);
				}
			}
			}else if(flag.equalsIgnoreCase("edit"))
			{
				this.editData(GroupCode[i]);
		    }else if(flag.equalsIgnoreCase("copy")){
				if(this.Iscopy.equalsIgnoreCase("yes")){
					if(tableName.equalsIgnoreCase("none")){
						this.copyData(GroupCode[i]);
					}else{
						this.editData(GroupCode[i]);
					}
				}else{
				  if(tableName.equalsIgnoreCase("none"))
				  {
				   this.copyData(GroupCode[i]);
				  }else{
					  //this.editData(GroupCode[i]);
				  }
				}
			}else if(flag.equalsIgnoreCase("checked")){
				msg+=this.getGroup(GroupCode[i]);
			}
		}
		if(msg.endsWith(",")){
			msg = msg.substring(0, msg.length()-1);
		}
			return msg;
	}

	/**
	 * getSetting
	 * 
	 * @return IDataSetting
	 */
	public IDataSetting getSetting() {
		return null;
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
	 * 从回收站删除数据，即从数据库彻底删除数据
	 * 
	 * @throws YssException
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		boolean bIsMarketValuePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
		// MS00131 byleeyu 20090204
		// 获取一个连接
		Connection conn = dbl.loadConnection();
		//add by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A
		String[] assetGroupCodes = null;//保存组合群数组
		try {
			bIsMarketValuePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("marketvalue"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
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
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
					if(this.sAssetGroupCode.trim().length() > 0){//添加跨组合群清除功能
						assetGroupCodes = this.sAssetGroupCode.trim().split(",");
						for(int j = 0 ; j < assetGroupCodes.length; j++){
							// 根据参数决定用哪张表 MS00131 QDV4建行2008年12月25日01_A by leeyu 20090205
							strSql = "delete from "+ (bIsMarketValuePub ? "Tb_Base_MarketValue" : 
									"Tb_" + assetGroupCodes[j] + "_Data_MarketValue")
									+ " where FMktSrcCode = "
									// fanghaoln 20100305 MS00999 QDV4华夏2010年02月24日01_B
									+ dbl.sqlString((this.strMktSrcCode.length() == 0) ? " " : this.strMktSrcCode)
									// ------------------------end
									+ " and FPortCode=" + dbl.sqlString((this.strPortCode.length() == 0) ? " " : this.strPortCode)
									+ " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode)
									+ " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate)
									+ " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime)
									+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" 
									// 添加上FASSETGROUPCODE主键字段
									+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : " "); 
							
							// QDV4建行2008年12月25日01_A MS00131 by leeyu 执行sql语句
							dbl.executeSql(strSql);							
						}
					}else{
						// 根据参数决定用哪张表 MS00131 QDV4建行2008年12月25日01_A by leeyu 20090205
						strSql = "delete from "+ (bIsMarketValuePub ? "Tb_Base_MarketValue" : 
								pub.yssGetTableName("Tb_Data_MarketValue"))
								+ " where FMktSrcCode = "
								// fanghaoln 20100305 MS00999 QDV4华夏2010年02月24日01_B
								+ dbl.sqlString((this.strMktSrcCode.length() == 0) ? " " : this.strMktSrcCode)
								// ------------------------end
								+ " and FPortCode=" + dbl.sqlString((this.strPortCode.length() == 0) ? " " : this.strPortCode)
								+ " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode)
								+ " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate)
								+ " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime)
								+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" 
								// 添加上FASSETGROUPCODE主键字段
								+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : " "); 
						
						// QDV4建行2008年12月25日01_A MS00131 by leeyu 执行sql语句
						dbl.executeSql(strSql);						
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
				}
			}
			// sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
			else if (strMktSrcCode != "" && strMktSrcCode != null) {
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
				if(this.sAssetGroupCode.trim().length() > 0){//添加跨组合群清除功能
					assetGroupCodes = this.sAssetGroupCode.trim().split(",");
					for(int j = 0 ; j < assetGroupCodes.length; j++){
						// 根据参数决定用哪张表 MS00131 QDV4建行2008年12月25日01_A by leeyu 20090205
						strSql = "delete from " + (bIsMarketValuePub ? "Tb_Base_MarketValue" : 
							    "Tb_" + assetGroupCodes[j] + "_Data_MarketValue")
								+ " where FMktSrcCode = "
								// fanghaoln 20100305 MS00999 QDV4华夏2010年02月24日01_B
								+ dbl.sqlString((this.strMktSrcCode.length() == 0) ? " " : this.strMktSrcCode)
								// ------------------------end
								+ " and FPortCode = "
								+ dbl.sqlString((this.strPortCode.length() == 0) ? " " : this.strPortCode)
								+ " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode)
								+ " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate)
								+ " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime)
								+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" 
								// 添加上FASSETGROUPCODE主键字段
								+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : " "); 
						
						// QDV4建行2008年12月25日01_A MS00131 by leeyu 执行sql语句
						dbl.executeSql(strSql);						
					}
				}else{
					// 根据参数决定用哪张表 MS00131 QDV4建行2008年12月25日01_A by leeyu 20090205
					strSql = "delete from " + (bIsMarketValuePub ? "Tb_Base_MarketValue" : 
						    pub.yssGetTableName("Tb_Data_MarketValue"))
							+ " where FMktSrcCode = "
							// fanghaoln 20100305 MS00999 QDV4华夏2010年02月24日01_B
							+ dbl.sqlString((this.strMktSrcCode.length() == 0) ? " " : this.strMktSrcCode)
							// ------------------------end
							+ " and FPortCode = "
							+ dbl.sqlString((this.strPortCode.length() == 0) ? " " : this.strPortCode)
							+ " and FSecurityCode=" + dbl.sqlString(this.strSecurityCode)
							+ " and FMktValueDate=" + dbl.sqlDate(this.strMktValueDate)
							+ " and FMktValueTime=" + dbl.sqlString(this.strMktValueTime)
							+ (bIsMarketValuePub ? (" and FASSETGROUPCODE=" 
							// 添加上FASSETGROUPCODE主键字段
							+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : " "); 
					
					// QDV4建行2008年12月25日01_A MS00131 by leeyu 执行sql语句
					dbl.executeSql(strSql);					
				}
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("清除数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	// ================新增 by leeyu 2008-10-17 行情状态
	public void setMarketStatus(String MarketStatus) {
		this.marketStatus = MarketStatus;
	}

	/**
	 * //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
	 * 
	 * @param sAssetGroupCode
	 *            String
	 */
	public void setSAssetGroupCode(String sAssetGroupCode) {
		this.sAssetGroupCode = sAssetGroupCode;
	}

	/**
	 * //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
	 * 
	 * @param sOldAssetGroupCode
	 *            String
	 */
	public void setSOldAssetGroupCode(String sOldAssetGroupCode) {
		this.sOldAssetGroupCode = sOldAssetGroupCode;
	}

	// --MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie 2009.04.08
	public void setStrMktSrcCode(String strMktSrcCode) {
		this.strMktSrcCode = strMktSrcCode;
	}

	public void setStrSecurityCode(String strSecurityCode) {
		this.strSecurityCode = strSecurityCode;
	}

	public void setStrMktValueDate(String strMktValueDate) {
		this.strMktValueDate = strMktValueDate;
	}

	public void setStrMktValueTime(String strMktValueTime) {
		this.strMktValueTime = strMktValueTime;
	}

	public void setStrPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}

	public void setDblBargainAmount(double dblBargainAmount) {
		this.dblBargainAmount = dblBargainAmount;
	}

	public void setDblOpenPrice(double dblOpenPrice) {
		this.dblOpenPrice = dblOpenPrice;
	}

	public void setDblClosingPrice(double dblClosingPrice) {
		this.dblClosingPrice = dblClosingPrice;
	}

	public void setDblBargainMoney(double dblBargainMoney) {
		this.dblBargainMoney = dblBargainMoney;
	}

	public void setMultAuditString(String multAuditString) {
		this.multAuditString = multAuditString;
	}

	public void setDblYClosePrice(double dblYClosePrice) {
		this.dblYClosePrice = dblYClosePrice;
	}

	public void setDblTopPrice(double dblTopPrice) {
		this.dblTopPrice = dblTopPrice;
	}

	public void setDblNewPrice(double dblNewPrice) {
		this.dblNewPrice = dblNewPrice;
	}

	public void setDblMktPrice2(double dblMktPrice2) {
		this.dblMktPrice2 = dblMktPrice2;
	}

	public void setDblMktPrice1(double dblMktPrice1) {
		this.dblMktPrice1 = dblMktPrice1;
	}

	public void setDblLowPrice(double dblLowPrice) {
		this.dblLowPrice = dblLowPrice;
	}

	public void setDblAveragePrice(double dblAveragePrice) {
		this.dblAveragePrice = dblAveragePrice;
	}

	public void setStrDesc(String strDesc) {
		this.strDesc = strDesc;
	}

	public void setStrCheckState(int strCheckState) {
		this.strCheckState = strCheckState;
	}

	// --MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie 2009.04.08

	public String getMarketStatus() {
		return marketStatus;
	}

	/**
	 * //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
	 * 
	 * @return String
	 */
	public String getSAssetGroupCode() {
		return sAssetGroupCode;
	}

	/**
	 * //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
	 * 
	 * @return String
	 */
	public String getSOldAssetGroupCode() {
		return sOldAssetGroupCode;
	}

	// --MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie 2009.04.08
	public String getStrMktSrcCode() {
		return strMktSrcCode;
	}

	public String getStrSecurityCode() {
		return strSecurityCode;
	}

	public String getStrMktValueDate() {
		return strMktValueDate;
	}

	public String getStrMktValueTime() {
		return strMktValueTime;
	}

	public String getStrPortCode() {
		return strPortCode;
	}

	public double getDblBargainAmount() {
		return dblBargainAmount;
	}

	public double getDblOpenPrice() {
		return dblOpenPrice;
	}

	public double getDblClosingPrice() {
		return dblClosingPrice;
	}

	public double getDblBargainMoney() {
		return dblBargainMoney;
	}

	public String getMultAuditString() {
		return multAuditString;
	}

	public double getDblYClosePrice() {
		return dblYClosePrice;
	}

	public double getDblTopPrice() {
		return dblTopPrice;
	}

	public double getDblNewPrice() {
		return dblNewPrice;
	}

	public double getDblMktPrice2() {
		return dblMktPrice2;
	}

	public double getDblMktPrice1() {
		return dblMktPrice1;
	}

	public double getDblLowPrice() {
		return dblLowPrice;
	}

	public double getDblAveragePrice() {
		return dblAveragePrice;
	}

	public String getStrDesc() {
		return strDesc;
	}

	public int getStrCheckState() {
		return strCheckState;
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
	// --MS00006-QDV4.1赢时胜上海2009年2月1日05_A add by songjie 2009.04.08
	// ==========================2008-10-17
	/**
	 * add baopingping #story 1167 20110717
	 * 查询当前表中所有组合群
	 * return ResultSet
	 * @throws YssException 
	 */
	public String getAssdeGroup() throws YssException{
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
	/**
	 * add baopingping #story 1167 20110717
	 * 给所有组合群下添加一条行情数据
	 * return ResultSet
	 * @throws YssException 
	 */
	public String adddata(String FAssetGroupCode) throws YssException
	{
		Connection conn = dbl.loadConnection();
		PreparedStatement pst=null;
		boolean bTrans = false;
		String strSql = "";
		 try{
		    conn.setAutoCommit(false);
		    boolean bIsMarketValuePub = false; // 定义变量存放通用参数 MS00131
			bIsMarketValuePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("marketvalue"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			strSql =
			// "insert into " + pub.yssGetTableName("Tb_Data_MarketValue") +
			"insert into "
					+ ("TB_"+FAssetGroupCode+"_Data_MarketValue")+
					 // 根据参数决定是用哪张表 MS00131
					"(FMktSrcCode, FSecurityCode, FMktValueDate, FMktValueTime, FPortCode, "
					+ " FBargainAmount, FBargainMoney, FYClosePrice, FOpenPrice, FTopPrice, "
					+ " FLowPrice, FClosingPrice, FAveragePrice, FNewPrice, FMktPrice1, FMktPrice2,FMarketStatus, FDesc,FDataSource,"
					+ // 新增 行情状态 by leeyu 2008-10-17
					// " FCheckState, FCreator, FCreateTime,FCheckUser) " +
					" FCheckState, FCreator, FCreateTime,FCheckUser"
					+ ",FAssetGroupCode"		//added by liubo.Story #1770
					+ ") "
					+ // 若是新表还要加上这个字段 MS00131
					" values("
					+ dbl.sqlString(this.strMktSrcCode)
					+ ","
					+ dbl.sqlString(this.strSecurityCode)
					+ ","
					+ dbl.sqlDate(this.strMktValueDate)
					+ ","
					+ dbl.sqlString(this.strMktValueTime)
					+ ","
					+ dbl.sqlString((this.strPortCode.length() == 0) ? " "
							: this.strPortCode)
					+ ","
					+ this.dblBargainAmount
					+ ","
					+ this.dblBargainMoney
					+ ","
					+ this.dblYClosePrice
					+ ","
					+ this.dblOpenPrice
					+ ","
					+ this.dblTopPrice
					+ ","
					+ this.dblLowPrice
					+ ","
					+ this.dblClosingPrice
					+ ","
					+ this.dblAveragePrice
					+ ","
					+ this.dblNewPrice
					+ ","
					+ this.dblMktPrice1
					+ ","
					+ this.dblMktPrice2
					+ ","
					+ dbl.sqlString(this.marketStatus)
					+ ","
					+ // 新增 行情状态 by leeyu 2008-10-17
					dbl.sqlString(this.strDesc)
					+ ",1,"
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ","
					+ dbl.sqlString(this.creatorCode)
					+ ","
					+ dbl.sqlString(this.creatorTime)
					+ ","
					+ dbl.sqlString(pub.getSysCheckState() ? " " : dbl
							.sqlString(this.creatorCode))+
					 "," + dbl.sqlString(("".equals(this.sAssetGroupCode.trim()) ? " " : this.sAssetGroupCode)) +	//added by liubo.Story #1770
					")";
			
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return buildRowStr();
            } catch (Exception e) 
            {
                  throw new YssException("添加行情数据出错！",e);
            }finally
            {
            	dbl.endTransFinal(conn, bTrans);
            }
		}
	
	/**
	 * add baopingping #story 1167 20110717
	 * 修改所有组合群下所选数据主键相同的行情数据
	 * return ResultSet
	 * @throws YssException 
	 */
	public String editData(String FAssetGroupCode) throws YssException
	{
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		boolean bIsMarketValuePub = false; // 定义变量存放通用参数 MS00131
		// QDV4建行2008年12月25日01_A byleeyu
		// 20090205
		String strSql = "";
        String[] sOldAssetGroup = null;
        String[] sNewAssetGroup = null;
		String sTmpAssetGroupCode = "";
		try {
			if (!sOldAssetGroupCode.equals(sAssetGroupCode))
       	 	{
		       	sOldAssetGroup = ("".equals(sOldAssetGroupCode.trim()) ? pub.getAssetGroupCode() : sOldAssetGroupCode).split(",");
		       	sNewAssetGroup = ("".equals(sAssetGroupCode.trim()) ? pub.getAssetGroupCode() : sAssetGroupCode).split(",");
       		 
	       		 for (int i = 0;i < sOldAssetGroup.length;i++)
	       		 {
					strSql = "delete from "
						
					+ (bIsMarketValuePub ? "Tb_Base_MarketValue"
						: ("tb_"+sOldAssetGroup[i]+"_data_MarketValue"))
					+ 
					" where FMktSrcCode = "
					+
					dbl
							.sqlString((this.strOldMktSrcCode.length() == 0) ? " "
									: this.strOldMktSrcCode)
					+
					// ------------------------end
					// ---------------------------------------------
					" and FPortCode="
					+ dbl.sqlString((this.strOldPortCode.length() == 0) ? " "
									: this.strOldPortCode)
					+ " and FSecurityCode="
					+ dbl.sqlString(this.strOldSecurityCode)
					+ " and FMktValueDate="
					+ dbl.sqlDate(this.strOldMktValueDate)
					+ " and FMktValueTime="
					+ dbl.sqlString(this.strOldMktValueTime)
					+ " and FASSETGROUPCODE=" + dbl.sqlString((sOldAssetGroupCode.trim().length() > 0 ? sOldAssetGroupCode : " "));
					dbl.executeSql(strSql);
	       		 }
	       		for (int i = 0;i < sNewAssetGroup.length;i++)
	       		 {
		       		if (!FAssetGroupCode.equals(sTmpAssetGroupCode))
		       		{
		       			adddata(FAssetGroupCode);
		       			sTmpAssetGroupCode = FAssetGroupCode;
		       		}
	       		 }
	       		 
       	 	}
			else
			{
				conn.setAutoCommit(false);
				bIsMarketValuePub = Boolean.valueOf(
						(String) pub.getHtPubParams().get("marketvalue"))
						.booleanValue(); // 获取PUB中参数的值 MS00131
				// strSql = "update " + pub.yssGetTableName("Tb_Data_MarketValue") +
				strSql = "update "
						+  ("TB_"+FAssetGroupCode+"_Data_MarketValue")+
						 // 根据参数决定用哪张表 MS00131
						" set FMktSrcCode = "
						+ dbl.sqlString(this.strMktSrcCode)
						+ ", FSecurityCode = "
						+ dbl.sqlString(this.strSecurityCode)
						+ ",FPortCode = "
						+ dbl.sqlString((this.strPortCode.length() == 0) ? " "
								: this.strPortCode)
						+ ",FMktValueDate = "
						+ dbl.sqlDate(this.strMktValueDate)
						+ ",FMktValueTime = "
						+ dbl.sqlString(this.strMktValueTime)
						+ ", FBargainAmount = "
						+ this.dblBargainAmount
						+ ", FBargainMoney ="
						+ this.dblBargainMoney
						+ ", FTopPrice="
						+ this.dblTopPrice
						+ ", FYClosePrice = "
						+ this.dblYClosePrice
						+ ",FOpenPrice = "
						+ this.dblOpenPrice
						+ ", FLowPrice = "
						+ this.dblLowPrice
						+ ", FClosingPrice = "
						+ this.dblClosingPrice
						+ ", FAveragePrice = "
						+ this.dblAveragePrice
						+ ", FNewPrice = "
						+ this.dblNewPrice
						+ ", FMktPrice1 = "
						+ this.dblMktPrice1
						+ ", FMktPrice2 = "
						+ this.dblMktPrice2
						+ ", FDesc = "
						+ dbl.sqlString(this.strDesc)
						+ ", FCreator = "
						+ dbl.sqlString(this.creatorCode)
						+ " , FCreateTime = "
						+ dbl.sqlString(this.creatorTime)
						+ ",FMarketStatus ="
						+ dbl.sqlString(this.marketStatus)
						+ ",FAssetGroupCode =" + dbl.sqlString(this.sAssetGroupCode) + 	//added by liubo.Story #1770 
						" where FMktSrcCode = "
						//modified by liubo.Story #1770
						//=====================================
						+ dbl.sqlString(this.strOldMktSrcCode)
						+ " and FPortCode = "
						+ dbl.sqlString((this.strOldPortCode.length() == 0) ? " "
								: this.strOldPortCode)
						+ " and FSecurityCode = "
						+ dbl.sqlString(this.strOldSecurityCode)
						+ " and FMktValueDate = "
						+ dbl.sqlDate(this.strOldMktValueDate)
						+ " and FMktValueTime = "
						+ dbl.sqlString(this.strOldMktValueTime)
						+ " and FAssetGroupCode = " + dbl.sqlString(this.sOldAssetGroupCode)
						//===============end======================
						+" and FCheckState='0'"; // 添加上FASSETGROUPCODE主键字段
				// QDV4建行2008年12月25日01_A MS00131 by leeyu
				bTrans = true;
				dbl.executeSql(strSql);
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
			return buildRowStr();
		} catch (Exception e) {
			throw new YssException("修改行情数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * add baopingping #story 1167 20110717
	 * 删除 ，审核，反审核 所有组合群下所选数据主键相同的行情数据
	 * return ResultSet
	 * @throws YssException 
	 */
	public String  auditData(String sMutilRowStr,String FAssetGroupCode) throws YssException{
		boolean checkTrue=false;
    	if(this.checkStateId==2)
    	{
    		 checkTrue=true;
    	}
		Connection conn = dbl.loadConnection();
		MarketValueBean data = null;
		boolean bTrans = false;
		boolean bIsMarketValuePub = false; // 定义变量存放通用参数 QDV4建行2008年12月25日01_A
		// MS00131 byleeyu 20090204
		String[] multAudit = null;
		String strSql = "";
		try {
			if (multAuditString.length() > 0) {
				bIsMarketValuePub = Boolean.valueOf(
						(String) pub.getHtPubParams().get("marketvalue"))
						.booleanValue(); // 获取PUB中参数的值 MS00131
				multAudit = sMutilRowStr.split("\f\f\f\f");
				if (multAudit.length > 0) {
					for (int i = 0; i < multAudit.length; i++) {
						data = new MarketValueBean();
						data.setYssPub(pub);
						data.parseRowStr(multAudit[i]);
						strSql = "update "
							+ ("TB_"+FAssetGroupCode+"_Data_MarketValue")
								+ // 根据参数决定是用哪张表 MS00131 by leeyu
								" set FCheckState = "
								+ this.checkStateId
								+ ", FCheckUser = "
								+ dbl.sqlString(pub.getUserCode())
								+ ", FCheckTime = '"
								+ YssFun.formatDatetime(new java.util.Date())
								+ "'"
								+ " where FMktSrcCode = "
								+
								// fanghaoln 20100305 MS00999
								// QDV4华夏2010年02月24日01_B
								dbl
										.sqlString((data.strMktSrcCode.length() == 0) ? " "
												: data.strMktSrcCode)
								+
								// ------------------------end
								// ---------------------------------------------
								" and FPortCode="
								+ dbl
										.sqlString((data.strPortCode.length() == 0) ? " "
												: data.strPortCode)
								+ " and FSecurityCode="
								+ dbl.sqlString(data.strSecurityCode)
								+ " and FMktValueDate="
								+ dbl.sqlDate(data.strMktValueDate)
								+ " and FMktValueTime="
								+ dbl.sqlString(data.strMktValueTime)+
								(checkTrue ? " and FCheckState='0'" : " ");//add by baopingping #story 1167 20110720 只删除未审核的数据 
								
						// QDV4建行2008年12月25日01_A MS00131
						// by leeyu
						conn.setAutoCommit(false);
						bTrans = true;
						dbl.executeSql(strSql);
						conn.commit();
						bTrans = false;
						conn.setAutoCommit(true);
						// ---增加批量删除的日志记录功能----guojianhua add 20100906-------//
						logOper = SingleLogOper.getInstance();
						data = this;
						if (this.checkStateId == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (this.checkStateId == 1) {
							data.checkStateId = 1;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						} else if (this.checkStateId == 0) {
							data.checkStateId = 0;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						}
						// -----------------------------------------//
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("删除行情数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return "";

	}
	/**
	 * add baopingping #story 1167 20110717
	 * 查看数据库中是否存在这个组合的数据
	 * TableName 组合群代码
	 * return ResultSet
	 * @throws YssException 
	 */
	
	public String getTable(String TableName) throws YssException{
		ResultSet rs=null;
		String sql=null;
		String  name="none";
		try{
			sql="select * from TB_"+TableName+"_Data_MarketValue where FMktSrcCode='"+this.strMktSrcCode+"' and FSecurityCode='"+this.strSecurityCode+"'and FMktValueDate="+dbl.sqlDate(this.strMktValueDate)+" and " +
					" FPortCode="+ dbl
					.sqlString((this.strPortCode.length() == 0) ? " "
							: this.strPortCode);
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				name="full";
			}
			return name;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	 /**
	 * add baopingping #story 1167 20110717
	 * 给所有组合群下添加一条行情数据
	 * return ResultSet
	 */
    private String copyData(String FAssetGroupCode) throws YssException 
    {
    	Connection conn = dbl.loadConnection();
		PreparedStatement pst=null;
		boolean bTrans = false;
		String strSql = "";
		 try{
		    conn.setAutoCommit(false);
		    boolean bIsMarketValuePub = false; // 定义变量存放通用参数 MS00131
			bIsMarketValuePub = Boolean.valueOf(
					(String) pub.getHtPubParams().get("marketvalue"))
					.booleanValue(); // 获取PUB中参数的值 MS00131
			strSql =
			// "insert into " + pub.yssGetTableName("Tb_Data_MarketValue") +
			"insert into "
					+ ("TB_"+FAssetGroupCode+"_Data_MarketValue")+
					 // 根据参数决定是用哪张表 MS00131
					"(FMktSrcCode, FSecurityCode, FMktValueDate, FMktValueTime, FPortCode, "
					+ " FBargainAmount, FBargainMoney, FYClosePrice, FOpenPrice, FTopPrice, "
					+ " FLowPrice, FClosingPrice, FAveragePrice, FNewPrice, FMktPrice1, FMktPrice2,FMarketStatus, FDesc,FDataSource,"
					+ // 新增 行情状态 by leeyu 2008-10-17
					// " FCheckState, FCreator, FCreateTime,FCheckUser) " +
					" FCheckState, FCreator, FCreateTime,FCheckUser"	
					+ ",FAssetGroupCode"	//added by liubo.Story #1770
					+ ") "
					+ // 若是新表还要加上这个字段 MS00131
					" values("
					+ dbl.sqlString(this.strMktSrcCode)
					+ ","
					+ dbl.sqlString(this.strSecurityCode)
					+ ","
					+ dbl.sqlDate(this.strMktValueDate)
					+ ","
					+ dbl.sqlString(this.strMktValueTime)
					+ ","
					+ dbl.sqlString((this.strPortCode.length() == 0) ? " "
							: this.strPortCode)
					+ ","
					+ this.dblBargainAmount
					+ ","
					+ this.dblBargainMoney
					+ ","
					+ this.dblYClosePrice
					+ ","
					+ this.dblOpenPrice
					+ ","
					+ this.dblTopPrice
					+ ","
					+ this.dblLowPrice
					+ ","
					+ this.dblClosingPrice
					+ ","
					+ this.dblAveragePrice
					+ ","
					+ this.dblNewPrice
					+ ","
					+ this.dblMktPrice1
					+ ","
					+ this.dblMktPrice2
					+ ","
					+ dbl.sqlString(this.marketStatus)
					+ ","
					+ // 新增 行情状态 by leeyu 2008-10-17
					dbl.sqlString(this.strDesc)
					+ ",1,"
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ","
					+ dbl.sqlString(this.creatorCode)
					+ ","
					+ dbl.sqlString(this.creatorTime)
					+ ","
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode))+
					"," + ("".equals(this.sAssetGroupCode.trim()) ? " " : this.sAssetGroupCode) +	//added by liubo.Story #1770
					")";
			
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return buildRowStr();
            } catch (Exception e) 
            {
                  throw new YssException("复制行情数据出错！",e);
            }finally
            {
            	dbl.endTransFinal(conn, bTrans);
            }
    	
    }
    
    /**
	 * add baopingping #story 1167 20110717
	 * 查看数据库中是否存在这个组合的数据
	 * TableName 组合群代码
	 * return ResultSet
	 * @throws YssException 
	 */
	
	public String getGroup(String TableName) throws YssException{
		ResultSet rs=null;
		String sql=null;
		String  name="";
		try{
			sql="select * from TB_"+TableName+"_Data_MarketValue where Fcheckstate = 0 and FMktSrcCode='"+this.strMktSrcCode+"' and FSecurityCode='"+this.strSecurityCode+"'and FMktValueDate="+dbl.sqlDate(this.strMktValueDate)+" and " +
					" FPortCode="+ dbl
					.sqlString((this.strPortCode.length() == 0) ? " "
							: this.strPortCode);
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				name=TableName;
			}
			return (name.length() > 0 ? name + "," : name);
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * added by liubo. #story 1770.20111123
	 * 通过此方法，查询出类似“001,002”以逗号分隔开的组合群代号所代表的组合群代码，同样以逗号分隔开
	 * FAssetGroupCode 组合群代码
	 * return String
	 * @throws YssException 
	 */
	
	private String getGroupNameFromGroupCode(String FAssetGroupCode) throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		String[] groupCode = null;
		String requestGroupCode = "";
		try
		{
//			groupCode = ("".equals(FAssetGroupCode.trim()) ? pub.getAssetGroupCode() : FAssetGroupCode).split(",");
			groupCode = FAssetGroupCode.split(",");
			for (int i = 0;i<groupCode.length;i++)
			{
				requestGroupCode = requestGroupCode +"'" + groupCode[i] + "',";
			}
			
			strSql = "select * from tb_sys_AssetGroup where FAssetGroupCode in (" + requestGroupCode.substring(0,requestGroupCode.length() - 1) + ")";
			rs = dbl.openResultSet(strSql);
			while(rs.next())
			{
				sReturn = sReturn + rs.getString("FAssetGroupName") + ",";
			}
			
			return ("".equals(sReturn.trim()) ? "" : sReturn.substring(0, sReturn.length() - 1));
		
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
	}
	
    
    

}
