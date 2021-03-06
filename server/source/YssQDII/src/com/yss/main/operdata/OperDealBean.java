package com.yss.main.operdata;

import java.math.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.ibm.db2.jcc.c.db;
import com.yss.commeach.*;
import com.yss.dsub.*;
import com.yss.log.DayFinishLogBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.bond.BondInsCfgFormula;
import com.yss.main.operdeal.datainterface.cnstock.pojo.PublicMethodBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;
import com.yss.main.operdeal.rightequity.REBonusShare;
import com.yss.main.operdeal.rightequity.REDeflationBonus;
import com.yss.main.operdeal.rightequity.RERightIssue;
import com.yss.main.operdeal.rightequity.REDivdend;
import com.yss.main.operdeal.rightequity.REMayApartBond;
import com.yss.main.operdeal.rightequity.RECashConsideration;
import com.yss.main.operdeal.rightequity.RESecLendBonusShare;
import com.yss.main.operdeal.rightequity.RESecLendDivdend;
import com.yss.main.operdeal.rightequity.RESecLendRightIssue;

import com.yss.main.operdeal.stgstat.*;

/**
 * <p>
 * Title: OperDealBean
 * </p>
 * <p>
 * Description: 业务处理
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
public class OperDealBean extends BaseDataSettingBean implements IDataSetting {
	private String strOperStartDate = ""; // 业务起始日期
	private String strOperEndDate = ""; // 业务截止日期
	private String strPortCode = ""; // 组合代码"\t"间隔

	// BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
	private String assetGroupCode = ""; // 组合群代码
	private String assetGroupName = ""; // 组合群名称
	private String currPortCode = ""; // 当前处理的组合

	private String strOperType = ""; // 业务类别
	private static String strDealInfo = "no"; // "yes"有业务，"no"无业务
	private String strSecurity = "";

	private StringBuffer sbDealInfo = new StringBuffer(); // 权益处理状态提示信息
															// 2009-05-12
															// panjunfang add
															// MS00001
															// QDV4赢时胜（上海）2009年4月20日01_A

	// ---- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.11 蒋锦 添加
	private FlowBean flow = null;
	private Hashtable userTable = null;
	// xuqiji 20090804 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理
	private ArrayList rightData; // 保存交易子表中数据
	private ArrayList bankRightData;//保存到开放式基金交易表Data_OpenFundTrade story 1574 add by zhouwei 20111107
	private ArrayList tradeRealRightData; // 保存交易关联表中数据
	// -------------------------end-----------------------------------//
	// add by zhangfa 20101213 TASK #1474::证券借贷业务需求 - 权益处理
	private ArrayList rightLendData;

	// ------------------end 20101213-------------------------------
	
	//add by zhangjun 2012-02-13
	BaseStgStatDeal recStgStat = null;
	
	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	public String logSumCode = "";//汇总日志编号
	public boolean comeFromDD = false;//通过调度方案调用
	public SingleLogOper logOper = null;//设置日志实例
	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	
	//=======end ====================
	
	private String logInfo = "";
	
	public OperDealBean() {
		flow = new FlowBean();
		userTable = new Hashtable();
	}

	// ---------------End MS00003-----------------------

	public String getAssetGroupCode() {
		return assetGroupCode;
	}

	public String getAssetGroupName() {
		return assetGroupName;
	}

	public void setAssetGroupCode(String assetGroupCode) {
		this.assetGroupCode = assetGroupCode;
	}

	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}

	/**
	 * 通用的删除资金调拨的方法
	 * 
	 * @param tradeType
	 *            String 交易类型
	 * @param transDate
	 *            Date 交易日期
	 * @param portCodes
	 *            String
	 * @param securityCode
	 *            String
	 * @throws YssException
	 *             MS00284 QDV4赢时胜（上海）2009年3月05日02_B
	 */
	public void pubDelCashTransfer(String tradeType, java.util.Date transDate,
			String portCodes, String securityCode, String attrClsCode) throws // 添加所属分类字段
																				// QDV4中保2010年07月06日01_B
																				// by
																				// leeyu
																				// 20100707
																				// 合并太平版本调整
			YssException {
		String sqlStr = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			sqlStr = "delete from "
					+ pub.yssGetTableName("tb_cash_subtransfer")
					+ " subtransfer "
					+ " where FCheckState = 1 and FPortCode in ("
					+ operSql.sqlCodes(portCodes)
					+ ")"
					+ // modify by wangzuochun 2010.06.23 MS01342 债券兑付报错
						// QDV4赢时胜(测试)2010年6月23日1_B
					" and exists "
					+ " (select * from (select FNum from "
					+ pub.yssGetTableName("tb_data_subtrade")
					+ " where FSecurityCode = "
					+ dbl.sqlString(securityCode)
					+ " and FPortCode in ("
					+ operSql.sqlCodes(portCodes)
					+ ")"
					+ // modify by wangzuochun 2010.06.23 MS01342 债券兑付报错
						// QDV4赢时胜(测试)2010年6月23日1_B
					" and FTradeTypeCode = "
					+ dbl.sqlString(tradeType)
					+ (attrClsCode != null && attrClsCode.length() > 0 ? " and FAttrClsCode ="
							+ dbl.sqlString(attrClsCode)
							: " ")
					+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu 20100707
						// 合并太平版本调整
					" and FSettleState = 1 " + " and FBargainDate = "
					+ dbl.sqlDate(transDate) + ") subtrade " + " inner join"
					+ " (select FNum,FTradeNum from "
					+ pub.yssGetTableName("tb_cash_transfer")
					+ " where FCheckState = 1) transfer "
					+ " on subtrade.fnum = transfer.ftradenum "
					+ " where transfer.fnum = subtransfer.fnum)"; // 删除资金调拨子数据
			dbl.executeSql(sqlStr);
			sqlStr = "delete from "
					+ pub.yssGetTableName("tb_cash_transfer")
					+ " transfer "
					+ " where FCheckState = 1 "
					+ " and exists "
					+ " (select * from "
					+ pub.yssGetTableName("tb_data_subtrade")
					+ " subtrade "
					+ " where transfer.ftradenum = subtrade.fnum "
					+ " and FSecurityCode = "
					+ dbl.sqlString(securityCode)
					+ " and FPortCode in ("
					+ operSql.sqlCodes(portCodes)
					+ ")"
					+ // modify by wangzuochun 2010.06.23 MS01342 债券兑付报错
						// QDV4赢时胜(测试)2010年6月23日1_B
					" and FTradeTypeCode = "
					+ dbl.sqlString(tradeType)
					+ (attrClsCode != null && attrClsCode.length() > 0 ? " and FAttrClsCode ="
							+ dbl.sqlString(attrClsCode)
							: " ")
					+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu 20100707
						// 合并太平版本调整
					" and FSettleState = 1 " + " and FBargainDate = "
					+ dbl.sqlDate(transDate) + ")"; // 删除资金调拨数据
			dbl.executeSql(sqlStr);
			conn.commit();
			bTrans = true;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除资金调拨出错!" + "\n", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 删除已经结算的资金调拨数据。 参数中去掉证券代码作为条件，将参数更改为交易类型，组合，起始日期与结束日期 备注：此方法事物必须在外部进行控制
	 * QDV4中保2009年04月21日01_B MS00397 by leeyu 20090427
	 * 
	 * @param tradeType
	 *            String 交易类型
	 * @param dBeginDate
	 *            Date 业务开始日期
	 * @param dEndDate
	 *            Date 业务结束日期
	 * @param portCode
	 *            String 组合代码，多个以“，”分隔
	 * @throws YssException
	 */
	public void delCashTransfer(String tradeType, java.util.Date dBeginDate,
			java.util.Date dEndDate, String portCode) throws YssException {
		String sqlStr = "";
		ResultSet rs = null;
		String nums = "";
		StringBuffer buf = new StringBuffer();
		try {
			// ==========modify by leeyu QDV4中保2009年04月21日01_B MS00397
			// 20090427==
			// 1.去掉证券代码的判断条件
			// 2.去掉结算状态的判断条件 疑问：之前为什么要加上结算状态，难道不结算也会产生资金调拨？
			// 3.将单个日期替换为日期期间的处理
			sqlStr = "select FNum,FSecurityCode from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FTradeTypeCode = " + dbl.sqlString(tradeType)
					+ " and FPortCode in( " + portCode + ")"
					+ " and FBargainDate between " + dbl.sqlDate(dBeginDate)
					+ " and " + dbl.sqlDate(dEndDate);
			// ================End
			// MS00397==========================================
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {
				buf.append(rs.getString("FNum")).append(",");
			}
			if (buf.length() > 0) {
				nums = buf.substring(0, buf.length() - 1);
				buf.delete(0, buf.length());
			}
			if (tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)) {
				tradeType = YssOperCons.YSS_JYLX_Sale;
			}

			// 更改删除资金调拨子表的SQL，QDV4中保2009年04月21日01_B MS00397 20090427
			sqlStr = "delete from "
					+ pub.yssGetTableName("Tb_Cash_SubTransfer")
					+ " where FNum in (select FNum from "
					+ pub.yssGetTableName("Tb_Cash_Transfer")
					+ " where FTradeNum in(" + this.operSql.sqlCodes(nums)
					+ ") )";
			dbl.executeSql(sqlStr);

			// 更改删除资金调拨主表的SQL，QDV4中保2009年04月21日01_B MS00397 20090427
			sqlStr = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer")
					+ " where FTradeNum in (" + this.operSql.sqlCodes(nums)
					+ ") ";
			dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("删除资金调拨出错!" + "\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * addOperData xuqiji 20090804 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理
	 * 
	 * @return String
	 */
	public String addSetting() throws YssException {
		int iDays = 0; // 保存需要检查的天数
		java.util.Date dDate = null;
		//String secCode = new String(); //add by zhangjun story #1713
		//---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
		if(logOper == null){//添加空指针判断
			logOper = SingleLogOper.getInstance();// 日终处理--日志报表 zhouxiang 2010.12.14
		}
		java.util.Date logBeginDate = null;
//		String logInfo = "";
		logInfo = "";
		boolean isError = false;//判断是否报错
		//---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        Connection conn = null;
        boolean bTrans = true;
        //---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
		try {
			//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
			conn = dbl.loadConnection();// 获取连接
			conn.setAutoCommit(false); // 设置为手动打开连接
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_SubTrade")); // 给操作表加锁
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Trade")); // 给操作表加锁
			//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
			// 要进行处理的天数
			iDays = YssFun.dateDiff(YssFun.toDate(this.strOperStartDate),
					YssFun.toDate(this.strOperEndDate));
			// 处理的开始日期
			dDate = YssFun.toDate(this.strOperStartDate);
			String[] strPortCodes = this.strPortCode.split(","); // 按组合群的解析符解析组合代码
																	// xuqiji
																	// 20090813--------------
			String[] sOperType = this.strOperType.split(",");// xuqiji 20090814
																// QDV4.1赢时胜（上海）2009年4月20日15_A
																// MS00015
																// 国内权益处理
			// ------ MS00003 QDV4.1-参数布局散乱不便操作 -----
			flow = new FlowBean();
			flow.setYssPub(pub);
			flow.ctlFlowStateAndLogInFun(YssCons.YSS_FLOW_POINTSTATE_EXECUTION); // 设置流程正在执行的状态.
			// BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln
			// 20090512
			if (this.strOperType.length() > 0) {
				runStatus.appendValRunDesc("\r\n    开始统计组合群【"
						+ this.assetGroupCode + "】......");// xuqiji 20090814
															// QDV4.1赢时胜（上海）2009年4月20日15_A
															// MS00015 国内权益处理
			}
			
			//---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			DayFinishLogBean df = new DayFinishLogBean();
			logBeginDate = new java.util.Date();
			df.setYssPub(pub);
			if(!this.comeFromDD){
				logSumCode = df.getLogSumCodes();
			}
			//---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			
			for (int i = 0; i <= iDays; i++) {
				runStatus.appendValRunDesc("【" + YssFun.formatDate(dDate)
						+ "】开始统计... ...\r\n");
				for (int j = 0; j < strPortCodes.length; j++) {
					runStatus.appendValRunDesc("    组合【" + strPortCodes[j]
							+ "】开始统计... ...\r\n");
					for (int s = 0; s < sOperType.length; s++) {
						try {
							//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
							logBeginDate = new java.util.Date();
							
							if (sOperType[s].length() != 0
									&& sOperType[s].trim().equalsIgnoreCase(
											"BonusShare")) {
								//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
								logInfo = "开始处理"+ YssFun.formatDate(dDate)+ "【送股权益】......\r\n";
								// 送股权益
								// this.BonusShare();
								runStatus.appendValRunDesc("\r\n          开始处理"
										+ YssFun.formatDate(dDate)
										+ "【送股权益】......\r\n");
								REBonusShare bonusShare = new REBonusShare(); // 实例化对象
								bonusShare.setYssPub(pub); // 设置PUB
								if (rightData != null) {
									if (rightData.size() > 0) {
										rightData.clear();
									}
								}
								rightData = bonusShare.getDayRightEquitys(
										dDate, strPortCodes[j]); // 获取权益数据
								bonusShare.saveRightEquitys(rightData, dDate,
										strPortCodes[j]); // 保存权益数据到交易子表

								// add by zhangfa 20101213 TASK #1474::证券借贷业务需求
								// - 权益处理
								if (rightLendData != null) {
									if (rightLendData.size() > 0) {
										rightLendData.clear();
									}
								}
								RESecLendBonusShare bonusLendShare = new RESecLendBonusShare(); // 实例化对象
								bonusLendShare.setYssPub(pub); // 设置PUB
								rightLendData = bonusLendShare
										.getDayRightEquitys(dDate,
												strPortCodes[j]); // 获取权益数据
								bonusLendShare.saveRightEquitys(rightLendData,
										dDate, strPortCodes[j]); // 保存权益数据到证券借贷交易表
								// -----------end
								// 20101213-------------------------------------
								if (rightData.size() > 0 || rightLendData.size() > 0) {  // modify by zhangjun 2011-10-31 BUG2991权益处理状态显示不正确 
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "\r\n              "+ YssFun.formatDate(dDate)+ "【送股权益】处理完成\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "【送股权益】处理完成\r\n");
								} else {
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "无【送股权益】\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "无【送股权益】\r\n");
								}
								//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
								logOper.setDayFinishIData(this, 13,sOperType[s], pub, false,strPortCodes[j], 
										YssFun.toDate(this.strOperStartDate),dDate, YssFun.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										logInfo,logBeginDate,logSumCode, new java.util.Date());
							}
						} catch (Exception e) {
							//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
							try{
								//add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
								isError = true;//设置为 报错状态
								logOper.setDayFinishIData(this, 13, sOperType[s],pub, true, strPortCodes[j], 
										YssFun.toDate(this.strOperStartDate),dDate, YssFun.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
										(logInfo + " \r\n 处理送股权益失败 \r\n " + e.getMessage())//处理日志信息 除去特殊符号
										.replaceAll("\t", "").replaceAll("&", "").replace("\f\f", ""),
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
										logBeginDate,logSumCode, new java.util.Date());
							}catch(Exception ex){
								ex.printStackTrace();
							}
							//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
							//add by songjie 2011.03.22 495 QDV4赢时胜(测试)2011年01月08日1_AB 添加抛异常语句
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
							finally{//添加 finally 保证可以抛出异常
								throw new YssException("处理送股权益处理出错！",e);
							}
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
						}

						/**shashijie 2011-08-11 STORY 1434 ,缩股权益处理*/
						try {
							if (sOperType[s].length() != 0
									&& sOperType[s].trim().equalsIgnoreCase(
											"DeflationBonus")) {
								//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
								logInfo = "开始处理"+ YssFun.formatDate(dDate)+ "【缩股权益】......\r\n";
								// 送股权益
								// this.BonusShare();
								runStatus.appendValRunDesc("\r\n          开始处理"
										+ YssFun.formatDate(dDate)
										+ "【缩股权益】......\r\n");
								
								REDeflationBonus bonusLendShare = new REDeflationBonus(); // 实例化对象
								bonusLendShare.setYssPub(pub); // 设置PUB
								if (rightData != null) {
									if (rightData.size() > 0) {
										rightData.clear();
									}
								}
								rightData = bonusLendShare.getDayRightEquitys(
										dDate, strPortCodes[j]); // 获取权益数据
								bonusLendShare.saveRightEquitys(rightData, dDate,
										strPortCodes[j]); // 保存权益数据到交易子表
								
								// add by zhangfa 20101213 TASK #1474::证券借贷业务需求
								if (rightLendData != null) {
									if (rightLendData.size() > 0) {
										rightLendData.clear();
									}
								}
								RESecLendDivdend rDividend = new RESecLendDivdend(); // 实例化对象
								rDividend.setYssPub(pub); // 设置PUB
								rightLendData = rDividend.getDayRightEquitys(
										dDate, strPortCodes[j]); // 获取权益数据
								rDividend.saveRightEquitys(rightLendData,
										dDate, strPortCodes[j]); // 保存权益数据到证券借贷交易表
								//end zhangfa 20101213 TASK #1474::证券借贷业务需求
								
								if (rightData.size() > 0) {
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "【缩股权益】处理完成\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "【缩股权益】处理完成\r\n");
								} else {
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "无【缩股权益】\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "无【缩股权益】\r\n");
								}
								//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
								logOper.setDayFinishIData(this, 13,sOperType[s], pub, false,strPortCodes[j], 
										YssFun.toDate(this.strOperStartDate),dDate, YssFun.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										logInfo,logBeginDate,logSumCode, new java.util.Date());
							}
						} catch (Exception e) {
							//add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
							try{
								isError = true;//设置为报错状态
								logOper.setDayFinishIData(this, 13, sOperType[s],
										pub, true, strPortCodes[j], YssFun
										.toDate(this.strOperStartDate),
										dDate, YssFun.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
										(logInfo + " \r\n 处理缩股权益失败  \r\n " + e.getMessage())//处理日志信息 除去特殊符号
										.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
										logBeginDate,logSumCode, new java.util.Date());
							}catch(Exception ex){
								ex.printStackTrace();
							}finally{//添加 finally 保证可以抛出异常
								throw new YssException("处理缩股权益处理出错！",e);
							}
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
						}
						/**end*/
						try {
							if (sOperType[s].length() != 0
									&& sOperType[s].trim().equalsIgnoreCase(
											"RightsIssue")) {
								//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
								logInfo = "开始处理"+ YssFun.formatDate(dDate)+ "【配股权益】......\r\n";
								// 配股权益
								// this.RightsIssue();
								runStatus.appendValRunDesc("\r\n          开始处理"
										+ YssFun.formatDate(dDate)
										+ "【配股权益】......\r\n");
								RERightIssue rightsIssue = new RERightIssue(); // 实例化对象
								rightsIssue.setYssPub(pub); // 设置PUB
								if (rightData != null) {
									if (rightData.size() > 0) {
										rightData.clear();
									}
								}
								rightData = rightsIssue.getDayRightEquitys(
										dDate, strPortCodes[j]); // 获取权益数据
								rightsIssue.saveRightEquitys(rightData, dDate,
										strPortCodes[j]); // 保存权益数据到交易子表
								// add by zhangfa 20101213 TASK #1474::证券借贷业务需求
								// - 权益处理
								if (rightLendData != null) {
									if (rightLendData.size() > 0) {
										rightLendData.clear();
									}
								}
								RESecLendRightIssue rightsLendIssue = new RESecLendRightIssue(); // 实例化对象
								rightsLendIssue.setYssPub(pub); // 设置PUB
								rightLendData = rightsLendIssue
										.getDayRightEquitys(dDate,
												strPortCodes[j]); // 获取权益数据
								rightsLendIssue.saveRightEquitys(rightLendData,
										dDate, strPortCodes[j]); // 保存权益数据到证券借贷交易表
								// -----------end
								// 20101213-------------------------------------
								if (rightData.size() > 0 || rightLendData.size() > 0 ) { // modify by zhangjun 2011-10-31 BUG2991权益处理状态显示不正确 
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "【配股权益】处理完成\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "【配股权益】处理完成\r\n");
								} else {
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "无【配股权益】\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "无【配股权益】\r\n");
								}
								logOper.setDayFinishIData(this, 13,
										sOperType[s], pub, false,
										strPortCodes[j], YssFun
												.toDate(this.strOperStartDate),
										dDate, YssFun
												.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										logInfo,logBeginDate,logSumCode, new java.util.Date());
							}
						} catch (Exception e) {
							//add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
							try{
								isError = true;//设置为报错状态
								logOper.setDayFinishIData(this, 13, sOperType[s],
										pub, true, strPortCodes[j], YssFun
										.toDate(this.strOperStartDate),
										dDate, YssFun.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
										(logInfo + " \r\n 处理配股权益失败  \r\n " + e.getMessage())//处理日志信息 除去特殊符号
										.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
										logBeginDate,logSumCode, new java.util.Date());
							}catch(Exception ex){
								ex.printStackTrace();
							}finally{//添加 finally 保证可以抛出异常
								throw new YssException("处理配股权益处理出错！",e);
							}
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
						}
						try {
							if (sOperType[s].length() != 0
									&& sOperType[s].trim().equalsIgnoreCase(
											"Dividend")) {
								//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
								logInfo = "开始处理"+ YssFun.formatDate(dDate)+ "【股票分红权益】......\r\n";
								// 股票分红
								// this.Dividend();
								runStatus.appendValRunDesc("\r\n          开始处理"
										+ YssFun.formatDate(dDate)
										+ "【股票分红权益】......\r\n");
								REDivdend dividend = new REDivdend(); // 实例化对象
								dividend.setYssPub(pub); // 设置PUB
								if (rightData != null) {
									if (rightData.size() > 0) {
										rightData.clear();
									}
								}
								/**shashijie 2012-6-7 BUG 4733  */
				                //保存业务资料数据之前，先删除已经结算产生的资金调拨数据              
								dividend.delCashTransfer(YssOperCons.YSS_JYLX_PX,dDate,strPortCodes[j]);
								/**end*/
								rightData = dividend.getDayRightEquitys(dDate,
										strPortCodes[j]); // 获取权益数据
								dividend.saveRightEquitys(rightData, dDate,
										strPortCodes[j]); // 保存权益数据到交易子表
								
								//story 1574 add by zhouwei 20111108 将场外权益数据保存到开放式基金交易表
								if (bankRightData != null) {
									if (bankRightData.size() > 0) {
										bankRightData.clear();
									}
								}
								bankRightData=dividend.getBankDividendData(dDate, strPortCodes[j]);//获取场外权益数据
								dividend.saveBankRightEquitys(bankRightData, dDate, strPortCodes[j]);//保存到开放式基金交易表
								// add by zhangfa 20101213 TASK #1474::证券借贷业务需求
								// - 权益处理
								if (rightLendData != null) {
									if (rightLendData.size() > 0) {
										rightLendData.clear();
									}
								}
								RESecLendDivdend rDividend = new RESecLendDivdend(); // 实例化对象
								rDividend.setYssPub(pub); // 设置PUB
								rightLendData = rDividend.getDayRightEquitys(
										dDate, strPortCodes[j]); // 获取权益数据
								rDividend.saveRightEquitys(rightLendData,
										dDate, strPortCodes[j]); // 保存权益数据到证券借贷交易表
								// -----------end
								// 20101213-------------------------------------
								//story 1574 update by zhouwei 20111108
								if (rightData.size() > 0 || rightLendData.size() > 0 || bankRightData.size()>0) { // modify by zhangjun 2011-10-31 BUG2991权益处理状态显示不正确 
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "【股票分红权益】处理完成\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "【股票分红权益】处理完成\r\n");
									// MS01288 QDV4交银施罗德2010年6月10日01_A add by
									// jiangshichao 2010.07.02-------------
									if (dividend.getMsg().length() > 0) {
										// 太平资产版本调整 add by jiangshichao
										// 2010.08.25 ----------------------
										String[] temp = dividend.getMsg()
												.split(",");
										for (int k = 0; k < temp.length; k++) {
											if (sbDealInfo.toString().indexOf(
													temp[k]) == -1) {
												sbDealInfo
														.append("," + temp[k]);
											}
										}
										// 太平资产版本调整 add by jiangshichao
										// 2010.08.25 end -------------------
									}
									// MS01288 QDV4交银施罗德2010年6月10日01_A end
									// 2010.07.02 ----------------------------
								} else {
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "无【股票分红权益】\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "无【股票分红权益】\r\n");
								}
								logOper.setDayFinishIData(this, 13,
										sOperType[s], pub, false,//modify by nimengjing 2011.2.14 BUG #1070 在重新启动服务器后，进行股票分红的权益处理时，会报错 
										strPortCodes[j], YssFun
												.toDate(this.strOperStartDate),
										dDate, YssFun
												.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										logInfo,logBeginDate,logSumCode, new java.util.Date());
							}

						} catch (Exception e) {
							//add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
							try{
								isError = true;//设置为报错状态
								logOper.setDayFinishIData(this, 13, sOperType[s],
										pub, true, strPortCodes[j], YssFun
										.toDate(this.strOperStartDate),
										dDate, YssFun.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
										(logInfo + " \r\n 处理股票分红权益失败  \r\n " + e.getMessage())//处理日志信息 除去特殊符号
										.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
										logBeginDate,logSumCode, new java.util.Date());
							}catch(Exception ex){
								ex.printStackTrace();
							}finally{//添加 finally 保证可以抛出异常
								throw new YssException("处理股票分红权益处理出错！",e);
							}
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
						}
						try {
							if (sOperType[s].length() != 0
									&& sOperType[s].trim().equalsIgnoreCase(
											"MayApartBond")) {
								//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
								logInfo = "开始处理"+ YssFun.formatDate(dDate)+ "【可分离债送配权益】......\r\n";
								// 可分离债送配权益处理
								runStatus.appendValRunDesc("\r\n          开始处理"
										+ YssFun.formatDate(dDate)
										+ "【可分离债送配权益】......\r\n");
								REMayApartBond mayApartBond = new REMayApartBond(); // 实例化对象
								mayApartBond.setYssPub(pub); // 设置PUB
								if (rightData != null) {
									if (rightData.size() > 0) {
										rightData.clear();
									}
								}
								rightData = mayApartBond.getDayRightEquitys(
										dDate, strPortCodes[j]); // 获取交易子表中数据
								tradeRealRightData = mayApartBond
										.getTradeRealRightData(); // 获取交易关联数据
								mayApartBond.saveRightEquitys(rightData,
										tradeRealRightData, dDate,
										strPortCodes[j]); // 保存权益数据到交易子表和交易关联表中
								if (rightData.size() > 0) {
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "【可分离债送配权益】处理完成\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "【可分离债送配权益】处理完成\r\n");
								} else {
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "无【可分离债送配权益】\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "无【可分离债送配权益】\r\n");
								}
								logOper.setDayFinishIData(this, 13,
										sOperType[s], pub, false,
										strPortCodes[j], YssFun
												.toDate(this.strOperStartDate),
										dDate, YssFun
												.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										logInfo,logBeginDate,logSumCode, new java.util.Date());
							}
						} catch (Exception e) {
							//add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
							try{
								isError = true;//设置为报错状态
								logOper.setDayFinishIData(this, 13, sOperType[s],
										pub, true, strPortCodes[j], YssFun
											.toDate(this.strOperStartDate),
										dDate, YssFun.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
										(logInfo + " \r\n 处理可分离债送配权益失败  \r\n " + e.getMessage())//处理日志信息 除去特殊符号
										.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
										logBeginDate,logSumCode, new java.util.Date());
							}catch(Exception ex){
								ex.printStackTrace();
							}finally{//添加 finally 保证可以抛出异常
								throw new YssException("处理可分离债送配权益处理出错！",e);
							}
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
						}
						try {
							if (sOperType[s].length() != 0
									&& sOperType[s].trim().equalsIgnoreCase(
											"CashConsider")) {
								//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
								logInfo = "开始处理"+ YssFun.formatDate(dDate)+ "【现金对价权益】......\r\n";
								// 现金对价权益处理
								runStatus.appendValRunDesc("\r\n          开始处理"
										+ YssFun.formatDate(dDate)
										+ "【现金对价权益】......\r\n");
								RECashConsideration cashConsideration = new RECashConsideration(); // 实例化对象
								cashConsideration.setYssPub(pub); // 设置PUB
								if (rightData != null) {
									if (rightData.size() > 0) {
										rightData.clear();
									}
								}
								rightData = cashConsideration
										.getDayRightEquitys(dDate,
												strPortCodes[j]); // 获取交易子表中数据
								tradeRealRightData = cashConsideration
										.getTradeRealRightData(); // 获取交易关联数据
								cashConsideration.saveRightEquitys(rightData,
										tradeRealRightData, dDate,
										strPortCodes[j]); // 保存权益数据到交易子表和交易关联表中
								if (rightData.size() > 0) {
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "【现金对价权益】处理完成\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "【现金对价权益】处理完成\r\n");
								} else {
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "无【现金对价权益】\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "无【现金对价权益】\r\n");
								}
								logOper.setDayFinishIData(this, 13,
										sOperType[s], pub, false,
										strPortCodes[j], YssFun
												.toDate(this.strOperStartDate),
										dDate, YssFun
												.toDate(this.strOperEndDate),
									    //edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										logInfo,logBeginDate,logSumCode, new java.util.Date());
							}
						} catch (Exception e) {
							//add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
							try{
								isError = true;//设置为报错状态
								logOper.setDayFinishIData(this, 13, sOperType[s],
										pub, true, strPortCodes[j], YssFun
											.toDate(this.strOperStartDate),
										dDate, YssFun.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
										(logInfo + " \r\n 处理现金对价权益失败  \r\n " + e.getMessage())//处理日志信息 除去特殊符号
										.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
										logBeginDate,logSumCode, new java.util.Date());
							}catch(Exception ex){
								ex.printStackTrace();
							}finally{//添加 finally 保证可以抛出异常
								throw new YssException("处理现金对价权益处理出错！",e);
							}
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
						}
						
						//20130508 deleted by liubo.Story #3528
						//债券兑付功能被继承到了债券派息模块中，需要删除独立的债券兑付功能
						//******************************************
//						try {
//							if (sOperType[s].length() != 0
//									&& sOperType[s].trim().equalsIgnoreCase(
//											"FIInterest")) {
//								//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
//								logInfo = "开始处理"+ YssFun.formatDate(dDate)+ "【债券兑付权益】......\r\n";
//								// 债券兑付
//								runStatus.appendValRunDesc("\r\n          开始处理"
//										+ YssFun.formatDate(dDate)
//										+ "【债券兑付权益】......\r\n");
//								if (this.Interest(strPortCodes[j])) {
//									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
//									logInfo += "              " + YssFun.formatDate(dDate) + "【债券兑付权益】处理完成\r\n";
//									//add by zhouwei 20120418 债券兑付日，产生利息税的冲减数据 bug4320
//									insertFILXSSecPayRec(dDate,strPortCodes[j]);
//									runStatus.appendValRunDesc("              "
//											+ YssFun.formatDate(dDate)
//											+ "【债券兑付权益】处理完成\r\n");
//								} else {
//									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
//									logInfo += "              " + YssFun.formatDate(dDate) + "无【债券兑付权益】\r\n";
//									runStatus.appendValRunDesc("              "
//											+ YssFun.formatDate(dDate)
//											+ "无【债券兑付权益】\r\n");
//								}
//								logOper.setDayFinishIData(this, 13,
//										sOperType[s], pub, false,
//										strPortCodes[j], YssFun
//												.toDate(this.strOperStartDate),
//										dDate, YssFun
//												.toDate(this.strOperEndDate),
//										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
//										logInfo,logBeginDate,logSumCode, new java.util.Date());
//							}
//						} catch (Exception e) {
//							//add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
//							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
//							try{
//								isError = true;//设置为报错状态
//								logOper.setDayFinishIData(this, 13, sOperType[s],
//										pub, true, strPortCodes[j], YssFun
//											.toDate(this.strOperStartDate),
//										dDate, YssFun.toDate(this.strOperEndDate),
//										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
//										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
//										(logInfo + " \r\n 处理债券兑付权益失败  \r\n " + e.getMessage())//处理日志信息 除去特殊符号
//										.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
//										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
//										logBeginDate,logSumCode, new java.util.Date());
//							}catch(Exception ex){
//								ex.printStackTrace();
//							}finally{//添加 finally 保证可以抛出异常
//								throw new YssException("处理债券兑付权益处理出错！",e);
//							}
//							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
//						}
						//**********************end********************
						
						try {
							if (sOperType[s].length() != 0
									&& sOperType[s].trim().equalsIgnoreCase(
											"BondDividend")) {
								//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
								logInfo = "开始处理"+ YssFun.formatDate(dDate)+ "【债券派息权益】......\r\n";
								// 债券派息
								runStatus.appendValRunDesc("\r\n          开始处理"
										+ YssFun.formatDate(dDate)
										+ "【债券派息权益】......\r\n");
								//add by zhangjun 2012-02-10 story #1713
								//---TEST---------strSecurity = bAccrue(strPortCodes[j],dDate);
								if(!strSecurity.equals(null) && !strSecurity.equals("")){
									
									throw new YssException("请对先对以下债券做收益计提: \r\n " + strSecurity);
									
								}
								//库存统计：证券应收应付
								recStgStat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("SecRecPay");
				                recStgStat.setYssPub(pub);
				                recStgStat.stroageStat(dDate, dDate, operSql.sqlCodes(strPortCodes[j]));
								
								//===========end by zhangjun=====================================  str.equals("")
								//rSet.getString("FTaskMoneyCode") !=null &&  rSet.getString("FTaskMoneyCode").trim().length() != 0 
								StringBuffer secCode = new StringBuffer();
								if (this.bondDividendBefore(strPortCodes[j],
										dDate,secCode)) {
									if(secCode.toString() != null && secCode.toString().trim().length() != 0 ){
										//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										logInfo += "              " + "以下债券派息金额与收益计提中应收债券利息金额不一致:\r\n" +
										 secCode.toString() +"\r\n";
										runStatus.appendValRunDesc("              "
												+ "以下债券派息金额与收益计提中应收债券利息金额不一致:\r\n" + secCode.toString() +"\r\n");
									}
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "【债券派息权益】处理完成\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "【债券派息权益】处理完成\r\n");
								} else {
									//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
									logInfo += "              " + YssFun.formatDate(dDate) + "无【债券派息权益】\r\n";
									runStatus.appendValRunDesc("              "
											+ YssFun.formatDate(dDate)
											+ "无【债券派息权益】\r\n");
								}
								logOper.setDayFinishIData(this, 13,
										sOperType[s], pub, false,
										strPortCodes[j], YssFun
												.toDate(this.strOperStartDate),
										dDate, YssFun
												.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										logInfo,logBeginDate,logSumCode, new java.util.Date());
							}
							// ------------------------------------------------------------------
						} catch (Exception e) {
							//add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
							try{
								isError = true;//设置为报错状态
								logOper.setDayFinishIData(this, 13, sOperType[s],
										pub, true, strPortCodes[j], YssFun
											.toDate(this.strOperStartDate),
										dDate, YssFun.toDate(this.strOperEndDate),
										//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
										(logInfo + "\r\n 处理债券派息权益失败  \r\n " + e.getMessage())//处理日志信息 除去特殊符号
										.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
										//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
										logBeginDate,logSumCode, new java.util.Date());
							}catch(Exception ex){
								ex.printStackTrace();
							}finally{//添加 finally 保证可以抛出异常
								throw new YssException("处理债券派息权益处理出错！",e);
							}
							//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
						}
						/**
						 * //add by zhangfa 20101213 TASK #1474::证券借贷业务需求 - 权益处理
						 * if (sOperType[s].length() != 0 &&
						 * sOperType[s].trim().
						 * equalsIgnoreCase("SecLendBonusShare")) { //送股权益
						 * //this.BonusShare();
						 * runStatus.appendValRunDesc("\r\n          开始处理"
						 * +YssFun.formatDate(dDate)+"【证券借贷送股权益】......");
						 * RESecLendBonusShare bonusShare = new
						 * RESecLendBonusShare(); //实例化对象
						 * bonusShare.setYssPub(pub); //设置PUB
						 * if(rightData!=null){ if(rightData.size() > 0){
						 * rightData.clear(); } } rightData =
						 * bonusShare.getDayRightEquitys(dDate,
						 * strPortCodes[j]); //获取权益数据
						 * bonusShare.saveRightEquitys(rightData, dDate,
						 * strPortCodes[j]); //保存权益数据到证券借贷交易表
						 * if(rightData.size() > 0){
						 * runStatus.appendValRunDesc("              "
						 * +YssFun.formatDate(dDate)+"【证券借贷送股权益】处理完成\r\n");
						 * }else{
						 * runStatus.appendValRunDesc("              "+YssFun
						 * .formatDate(dDate)+"无【证券借贷送股权益】\r\n"); } }
						 * 
						 * if (sOperType[s].length() != 0 &&
						 * sOperType[s].trim().
						 * equalsIgnoreCase("SecLendDividend")) { //证券借贷股票分红
						 * //this.Dividend();
						 * runStatus.appendValRunDesc("\r\n          开始处理"
						 * +YssFun.formatDate(dDate)+"【证券借贷股票分红权益数据】......");
						 * RESecLendDivdend rDividend = new RESecLendDivdend();
						 * //实例化对象 rDividend.setYssPub(pub); //设置PUB
						 * if(rightData!=null){ if(rightData.size() > 0){
						 * rightData.clear(); } } rightData =
						 * rDividend.getDayRightEquitys(dDate, strPortCodes[j]);
						 * //获取权益数据 rDividend.saveRightEquitys(rightData, dDate,
						 * strPortCodes[j]); //保存权益数据到证券借贷交易表
						 * if(rightData.size() > 0){
						 * runStatus.appendValRunDesc("              "
						 * +YssFun.formatDate(dDate)+"【证券借贷股票分红权益数据】处理完成\r\n");
						 * }else{
						 * runStatus.appendValRunDesc("              "+YssFun
						 * .formatDate(dDate)+"无【证券借贷股票分红权益数据】\r\n"); } }
						 * 
						 * if (sOperType[s].length() != 0 &&
						 * sOperType[s].trim().
						 * equalsIgnoreCase("SecLendRightsIssue")) { //配股权益
						 * //this.RightsIssue();
						 * runStatus.appendValRunDesc("\r\n          开始处理"
						 * +YssFun.formatDate(dDate)+"【证券借贷配股权益】......");
						 * RESecLendRightIssue rightsIssue = new
						 * RESecLendRightIssue(); //实例化对象
						 * rightsIssue.setYssPub(pub); //设置PUB
						 * if(rightData!=null){ if(rightData.size() > 0){
						 * rightData.clear(); } } rightData =
						 * rightsIssue.getDayRightEquitys(dDate,
						 * strPortCodes[j]); //获取权益数据
						 * rightsIssue.saveRightEquitys(rightData, dDate,
						 * strPortCodes[j]); //保存权益数据到证券借贷交易表
						 * if(rightData.size() > 0){
						 * runStatus.appendValRunDesc("              "
						 * +YssFun.formatDate(dDate)+"【证券借贷配股权益】处理完成\r\n");
						 * }else{
						 * runStatus.appendValRunDesc("              "+YssFun
						 * .formatDate(dDate)+"无【证券借贷配股权益】\r\n"); } }
						 * //---------------end
						 * 20101213----------------------------------
						 * 
						 */
						// ----MS00003 QDV4.1-参数布局散乱不便操作 ------------
						// 设置执行的组合代码 2009.04.17 蒋锦 添加
						if (pub.getFlow() != null
								&& pub.getFlow().keySet().contains(
										pub.getUserCode())) {
							((FlowBean) pub.getFlow().get(pub.getUserCode()))
									.setFPortCodes(this.strPortCode.replaceAll(
											"\t", ","));
						}
						userTable.put(pub.getUserCode(), new Boolean(true)); // 当操作正确执行完成，则将正确的结果放入容器中
						// -------------------------------------------------
					}
					runStatus.appendValRunDesc("     组合【" + strPortCodes[j]
							+ "】统计完成\r\n");
				}
				runStatus.appendValRunDesc("【" + YssFun.formatDate(dDate)
						+ "】统计完成\r\n");
				dDate = YssFun.addDay(dDate, 1);
				runStatus
						.appendValRunDesc("\r\n-------------------------------------------------------");
			}
			
			runStatus.appendValRunDesc("\r\n    组合群【" + this.assetGroupCode
					+ "】业务处理完成\r\n");
			runStatus.appendValRunDesc("\r\n★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
			
			//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
		} catch (Exception e) {
			// ----MS00003 QDV4.1-参数布局散乱不便操作 ------------
			userTable.put(pub.getUserCode(), new Boolean(false)); // 若操作失败，则将此结果放入容器中
			// -------------------------------------------------
			throw new YssException(e);
		}
		// -------- MS00003 QDV4.1-参数布局散乱不便操作 ------------------------------
		finally {
			try {
				if (null != pub.getUserCode() && pub.getUserCode().length() > 0) {
					flow = new FlowBean();
					flow.setYssPub(pub);
					flow.ctlFlowStateAndLog((Boolean) userTable.get(pub
							.getUserCode())); // 操作流程状态和日志数据
					userTable.remove(pub.getUserCode()); // 清除个用户的信息.
				}
				
				//---add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
				//插入汇总业务日志
				if(!this.comeFromDD){
					//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
					if(logOper.getDayFinishLog().getAlPojo().size() == 0){
						logOper.setDayFinishIData(this, 13, " ", pub, isError,
								this.strPortCode, 
								YssFun.toDate(this.strOperStartDate), dDate, 
								YssFun.toDate(this.strOperEndDate), " ",
								logBeginDate,logSumCode, new java.util.Date());
					}
					//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
					logOper.setDayFinishIData(this, 13, "sum", pub, isError," ", 
							YssFun.toDate(this.strOperStartDate), dDate, 
							YssFun.toDate(this.strOperEndDate), " ",
							logBeginDate,logSumCode, new java.util.Date());
				}
				//---add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			} catch (YssException ex) {
				throw ex;
			}
			//add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
			dbl.endTransFinal(conn, bTrans);
		}
		// ------------------------------------------------------------------------
		// return "";
		return sbDealInfo.toString(); // MS00001 QDV4赢时胜（上海）2009年4月20日01_A
										// 返回权益处理状态信息 2009-05-12 panjunfang add
	}

	//add by zhangjun  story #1713
	private String bAccrue(String portCode, java.util.Date dDate)
		throws YssException {
		String strSql = "";
		String sql="";
		ResultSet rs = null;
		ResultSet rSet = null;
		String str = "";
		boolean isHave = false; // 是否有
		try{
			strSql = "select a.FSecurityCode , b.FSecurityName from  "+ pub.yssGetTableName("tb_para_fixinterest") + " a " +
			         " left join ( select fsecuritycode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") +  
	                 " ) b on a.fsecuritycode = b.fsecuritycode " + " where  a.FCheckState = 1";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				//检查是否已进行收益计提
	            sql = "select fsecuritycode from " + pub.yssGetTableName("tb_Stock_SecRecPay") + 
	            	  " where fsecuritycode = " + dbl.sqlString(rs.getString("FSecurityCode")) + " and FStorageDate = " + dbl.sqlDate(dDate) +
	            	  " and FPortCode = '" + portCode + "'";
	            rSet = dbl.openResultSet(sql);
	            if(!rSet.next()){
	            	str = str + rs.getString("fsecuritycode") +" " + rs.getString("fsecurityName") + "\r\n";
	            }
	            dbl.closeResultSetFinal(rSet);
			}
		    return str;
		}catch(Exception e){
			throw new YssException(e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	//============end======================================
	
	// add by zhangfa 20100728 MS01336 QDV4赢时胜2010年06月22日01_A  bAccrue
	private boolean bondDividendBefore(String portCode, java.util.Date dDate,StringBuffer secCode)
			throws YssException {
		
		YssCons.sInterestInfoCollected = "";
		
		String strSql = "";
		HashMap hmInteArgs = null;
		BondInsCfgFormula bicf = new BondInsCfgFormula();
		bicf.setYssPub(this.pub);
		ResultSet rs = null;
		boolean isHave = false; // 是否有
		String flag = "";
		String sCollectInfo = "";	//20130403 added by liubo.Story #3714.计算债券派息时，需要往前台状态栏中返回的债券派息数据的集合
		double dPayMoney = 0;	///20130403 added by liubo.Story #3528.当前计息期间，每张偿还金额
		//String strSec = new String(); //add by zhangjun  story 1713 
		try {
			//strSec = "";
			strSql = "select * from  "
//					+ pub.yssGetTableName("tb_para_fixinterest")
					+ pub.yssGetTableName("tb_para_InterestTime")
					+ " where  FCheckState = 1 and FISSUEDATE = " + dbl.sqlDate(dDate);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) 
			{				
				flag = flag
				+ bondDividend(portCode, rs.getString("FSecurityCode"),rs.getDouble("FPayMoney"),rs.getDate("FISSUEDATE"),YssFun.addDate(rs.getDate("FEXRIGHTDATE"), -1, Calendar.DAY_OF_YEAR),secCode,rs.getDate("FSETTLEDATE"),rs.getDate("FINSENDDATE")) + ",";
				sCollectInfo += this.sBondDivInfo;	//20130403 added by liubo.Story #3714.汇总债券派息信息
			}
			
			//20130507 added by liubo.Story #3528
			//债券兑付的业务整合到债券派息模块中
			//================================
			Interest(portCode);
			//==============end==================

			
			if (flag.indexOf("true") != -1) {
				isHave = true;
			}
			return isHave;
		} catch (Exception e) {
			throw new YssException("系统执行债券派息时出现异常！" + "\n", e);
		} finally {
			// 关闭记录集，提供 RollBack 的机会

			dbl.closeResultSetFinal(rs);
			
			//20130407 added by liubo.Story #3714
			//将汇总的债券派息信息放到全局变量中，权益处理完后往前台的状态栏中返回数据
			//==============================
			if(!sCollectInfo.trim().equals(""))
			{
				YssCons.sInterestInfoCollected += "本日以下债券派息：\r\n" + sCollectInfo + "\r\n~~~\r\n\r\n";
//				this.sbDealInfo.append("true");	//20131231 deleted by liubo.Bug #86326.如果保留这句，当日有派息时权益处理界面都会提示分红账户多币种的问题
			}
			else
			{
				YssCons.sInterestInfoCollected += "本日以下债券派息：\r\n" + "　　本日无债券派息业务。" + "\r\n~~~\r\n\r\n";
			}
			//==============end================

			//20130507 added by liubo.Story #3528
			//将汇总的债券兑付信息放到全局变量中，权益处理完后往前台的状态栏中返回数据
			//==============================
			if (!sBondDivInfo.trim().equals(""))
			{
				YssCons.sInterestInfoCollected += "本日以下债券兑付：\r\n" + sBondDivInfo + "\r\n~~~\r\n";
//				this.sbDealInfo.append("true");//20131231 deleted by liubo.Bug #86326.如果保留这句，当日有派息时权益处理界面都会提示分红账户多币种的问题
				
			}
			else
			{
				YssCons.sInterestInfoCollected += "本日以下债券兑付：\r\n" + "　　本日无债券还本兑付业务。" + "\r\n~~~\r\n";
			}
			//==============end================
		}
	}

	private String sBondDivInfo = "";
	// add by zhangfa 20100728 MS01336 QDV4赢时胜2010年06月22日01_A
	/**
	 * 
	 * @方法名：bondDividend
	 * @参数：portCode
	 * @返回类型：boolean
	 * @说明：债券派息
	 */
	private boolean bondDividend(String portCode, String securitycode,double dPayMoney,
			java.util.Date dDate ,java.util.Date dExRightDate ,StringBuffer secCode,java.util.Date dSettleDate,java.util.Date dIssueEndDate) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String sRowStr = "";
		Date theDate = null;// 操作日当天的是否是工作日
		EachExchangeHolidays holiday = null;// 节假日代码
		ResultSet drs = null;
		ResultSet rsStg = null;
		Connection conn = dbl.loadConnection();
		String strRightType = "";
		boolean bTrans = false; // 代表是否开始了事务
		boolean isHave = false; // 是否有
		long sNum = 0;// 为了产生的编号不重复
		boolean analy1; // 判断是否需要用分析代码
		boolean analy2;
		double cjje = 0.0;// 成交金额
		PublicMethodBean pubMethod = new PublicMethodBean();        
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pstSub = null;
        YssPreparedStatement pstSub = null;
        //=============end====================
		String strSqlSub = " ";
		CashAccountBean caBean = null;
		String strCashAccCode = " ";
		EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
		rateOper.setYssPub(pub);
		// 1.查询符合条件的债券
		// 2.根据债券代码,验证是否已经导入数据
		// 3.根据债券代码,获取债券利息数据

		try {
			
			sBondDivInfo = "";
			
			conn.setAutoCommit(false);
			bTrans = true;

			pubMethod.setYssPub(pub);
			BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub
					.getOperDealCtx().getBean("cashacclinkdeal");
			operFun.setYssPub(pub);
			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
			
			strRightType = YssOperCons.YSS_JYLX_ZQPX;

			strSql = "select s.*,f.*,p.* from ("
					+ "select (case when FStorageAmount is null then 0 else FStorageAmount end) +(case when ftradeamount is null then 0 else ftradeamount end )as FStorageAmount "
					+ ",FSecurityCode,FYearMonth,FStorageDate,"
					+ "FPortCode,FCatType,FAttrClsCode,FInvestType,FCuryCode,FStorageCost,FMStorageCost,"
					+ "FVStorageCost,FFreezeAmount,FPortCuryRate,FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FBaseCuryRate,"
					+ "FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FBailMoney,"
					+ "FMarketPrice,FEffectiveRate,FStorageInd,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime"
					+ " from (" + "select a.*,ftradeamount from  "
					+ pub.yssGetTableName("tb_para_fixinterest")
					+ "  d "
					+ " left join ( select * from  "
					+ pub.yssGetTableName("tb_stock_security")
					+ " where FStorageDate ="
					+ dbl.sqlDate(YssFun.addDay(dExRightDate, -1))
					+ " and FCheckState = 1  and FPortCode in ("
					+ operSql.sqlCodes(portCode)
					+ ") ) a"
					+ " on d.fsecuritycode=a.fsecuritycode  "
					+ "left join (select Fportcode,fsecuritycode, sum(case when FTradeTypeCode = '01' then ftradeamount else -ftradeamount end) as ftradeamount"
					+ " from "
					+ pub.yssGetTableName("tb_data_subtrade")
					+ " where FBargainDate ="
					+ dbl.sqlDate(YssFun.addDay(dDate, 0))
					+ " and FCheckState = 1 and FTradeTypeCode in ('01', '02') "
					+ " and FPortCode in ("
					+ operSql.sqlCodes(portCode)
					+ ") group by fsecuritycode,Fportcode  )b "
					+ "on  d.fsecuritycode=b.FSecurityCode   where d.FCheckState = 1"
					+ " ) ) s"
					+ " join (select * from "
					+ pub.yssGetTableName("tb_para_fixinterest")
					+ ") f  on s.FSecurityCode=f.FSecurityCode"
					+ " join (select * from "
					+ pub.yssGetTableName("tb_para_security ")
					+ "   where  FCheckState = 1) p  on p.FSecurityCode=s.FSecurityCode"
					+ "  where s.FSecurityCode=" + dbl.sqlString(securitycode);

			rs = dbl.openResultSet_antReadonly(strSql);

			// 操作子表
			strSqlSub = "insert into "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ "(FNum,FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,"
					+ "FBargainDate,FBarGainTime,FSettleDate, FSettleTime,"
					+ " FAutoSettle,FPortCuryRate,FBaseCuryRate,"
					+ " FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryCost,"
					+ " FMPortCuryCost, FVPortCuryCost, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser, "
					+ "FCheckTime,FFactSettleDate,FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate,FAccruedinterest "
					//edit by songjie 2011.01.12 BUG:711 南方东英2010年12月16日01_B
					+ ",FAllotProportion,FOldAllotAmount,FAllotFactor,FTradeAmount,FTradePrice,FTRADEMONEY,FTotalCost,FATTRCLSCODE) "
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			        //edit by songjie 2011.01.12 BUG:711 南方东英2010年12月16日01_B
			
			//modified by liubo.Story #2145
			//==============================
//			pstSub = conn.prepareStatement(strSqlSub);
			pstSub = dbl.getYssPreparedStatement(strSqlSub);	
			//==============end================
			// ------------------------------------------------------------------------------

			if (rs.next()) {
				cashacc.setYssPub(pub);
				cashacc.setLinkParaAttr((analy1 ? rs
						.getString("FAnalysisCode1") : " "), rs
						.getString("FPortCode"), rs.getString("FSecurityCode"),
						(analy2 ? rs.getString("FAnalysisCode2") : " "),
						strRightType, YssFun.addDay(dDate, -1));

				// --------------------拼接交易编号---------------------
				String strNumDate = YssFun.formatDate(dDate, "yyyyMMdd");
				strNumDate = strNumDate
						+ dbFun.getNextInnerCode(pub
								.yssGetTableName("Tb_Data_Trade"), dbl
								.sqlRight("FNUM", 6), "000000",
								" where FNum like 'T" + strNumDate + "%'", 1);
				strNumDate = "T" + strNumDate;
				strNumDate = strNumDate
						+ dbFun.getNextInnerCode(pub
								.yssGetTableName("Tb_Data_SubTrade"), dbl
								.sqlRight("FNUM", 5), "00000",
								" where FNum like '"
										+ strNumDate.replaceAll("'", "''")
										+ "%'");
				String s = strNumDate.substring(9, strNumDate.length());
				sNum = Long.parseLong(s);
				// ---------------------------end-----------------------//
				rs.beforeFirst();

				while (rs.next()) {
					// 国内业务
					// modify by zhangjun 2012-01-10 STORY #1713 取消仅处理国内业务的功能限制
					/*if ("CS".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))
							|| "CG".equalsIgnoreCase(rs
									.getString("FEXCHANGECODE"))
							|| "CY".equalsIgnoreCase(rs
									.getString("FEXCHANGECODE"))) {  */
					//=====end by zhangjun 2012-01-10========================
						
						//获取组合利率
						rateOper.getInnerPortRate(rs.getDate("FInsEndDate"), rs
								.getString("FTradeCury"), rs
								.getString("FPortCode"));

						double portCuryRate = rateOper.getDPortRate();
						double baseCuryRate = this.getSettingOper()
								.getCuryRate(dDate, rs.getString("FTradeCury"),
										rs.getString("FPortCode"),
										YssOperCons.YSS_RATE_BASE);

						// 为表加锁，以免在多用户同时处理时出现交易编号重复
						dbl.lockTableInEXCLUSIVE(pub
								.yssGetTableName("Tb_Data_SubTrade"));

						caBean = cashacc.getCashAccountBean();
						if (caBean != null) {
							strCashAccCode = caBean.getStrCashAcctCode();
						}

						// --------------------拼接交易编号---------------------
						sNum++;
						String tmp = "";
						for (int i = 0; i < s.length()
								- String.valueOf(sNum).length(); i++) {
							tmp += "0";
						}
						strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
						// ------------------------end--------------------------//

						holiday = new EachExchangeHolidays();// 实例化
						holiday.setYssPub(pub);// 设置pub
						// 拼接参数：节假日代码+当天偏离天数+操作日期
						sRowStr = rs.getString("FHolidaysCode") + "\t"
								+ rs.getInt("FSettleDays") + "\t"
								+ YssFun.formatDate(dDate);
						// 解析参数
						holiday.parseRowStr(sRowStr);
						theDate = YssFun.toSqlDate(holiday
								.getOperValue("getWorkDate"));// 获取当天的最近一个工作日

						// 检查是否有通过接口导入的交易数据
						TradeSubBean tradesub = new TradeSubBean();
						tradesub.setSecurityCode(rs.getString("FSecurityCode"));
						tradesub.setTradeCode(strRightType);
						tradesub.setBargainDate(YssFun.formatDate(dDate));
						
						/**Start 20130626 modified by liubo.Bug #8438.QDV4鹏华2013年06月26日01_B
						 * 之前为tradesub对象的结算日期赋值了theDate（操作日当天的是否是工作日）。
						 * 导致删除以结算日期作为删除条件之一，删除资金调拨数据时，无法正确执行，从而导致派息的资金调拨数据重复*/
						tradesub.setSettleDate(YssFun.formatDate(dSettleDate));
						/**End 20130626 modified by liubo.Bug #8438.QDV4鹏华2013年06月26日01_B*/
						
						if (checkRightTrade(tradesub)) {
							continue;
						}

						// 插入数据之前先删去非接口导入的旧数据
						strSql = "delete from "
								+ pub.yssGetTableName("tb_data_subtrade")
								+ "  where  fsecuritycode="
								+ dbl.sqlString(tradesub.getSecurityCode())
								
						/**Start 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B
						 * 删除债券派息的交易数据时，判断条件需要加入组合代码
						 * 避免多个组合投资一只债券，最后只会存在一条交易数据的情况*/
								+ " and FPortCode = " + dbl.sqlString(portCode)
						/**End 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B*/
								+ " and fbargaindate="
								+ dbl.sqlDate(tradesub.getBargainDate())
								+ " and ftradetypecode in "
								//20130425 modified by liubo.Story #3528
								//同时删除某只券生成的派息数据和自动还本数据
								//===================================
								+ "(" + dbl.sqlString(YssOperCons.YSS_JYLX_ZQPX) + "," + dbl.sqlString(YssOperCons.YSS_JYLX_ZQPX) + ")"
								//==============end===================== 
								+ "  and (FJKDR is null or FJKDR !='1' )";
						dbl.executeSql(strSql);

						// 若旧数据有资金调拨,则删除资金调拨
						// 下面添加删除结算数据的资金调拨的方法
						String cashNums = "";
						String sql = "select FNum from "
								+ pub.yssGetTableName("Tb_Cash_Transfer")
								+ " where FSecurityCode in("
								+ dbl.sqlString(tradesub.getSecurityCode())
								+ ") and FTransDate="
								+ dbl.sqlDate(tradesub.getBargainDate())
								+ "  and FTransferDate="
								+ dbl.sqlDate(tradesub.getSettleDate())
								+ "  and FTsfTypeCode='02' and FSubTsfTypeCode='02FI'"
								+ "  and FTradeNum is not null "
								+ "  and FCheckState=1";
						drs = dbl.openResultSet(sql);
						while (drs.next()) {
							cashNums += drs.getString("FNum") + ",";
						}
						drs.getStatement().close();
						//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
						drs.close();
						if (cashNums.length() > 0) {
							
							/**Start 20130821 added by liubo.Bug #9116.
							 * 在之前因为可能存在一个资金调拨编号，对应多个组合的债券利息的调拨子编号，
							 * 因为要确保不要删除当前组合之外的组合的债券利息数据，
							 * 因此删除资金调拨数据的时候没有去删除资金调拨主表，导致产生垃圾数据
							 * 在这里增加逻辑：
							 * 当资金调拨子表中，某一个资金调拨编下，存在当前所选组合之外其他组合的调拨子编号，则不删除资金调拨主表的数据
							 * 若不存在，则删除调拨主表的数据*/
							ResultSet rsTransfer = null;
							
							//查询调拨编号下，资金调拨子表中，不属于当前选择组合的数据
							strSql = "select * from " + pub .yssGetTableName("Tb_Cash_SubTransfer") + 
									 " where FNum in (" +
									 operSql.sqlCodes(cashNums) + ") and FPortCode <> " + dbl.sqlString(portCode);
							
							rsTransfer = dbl.queryByPreparedStatement(strSql);
							
							//若存在不属于当前选择组合的数据，则不删除调拨数据主表的中的数据
							//不存在则删除主表的数据
							if (!rsTransfer.next())
							{
								dbl.executeSql("delete from " + pub.yssGetTableName("Tb_Cash_Transfer")
										+ " where FNum in("
										+ operSql.sqlCodes(cashNums) + ")");
							}
							
							dbl.closeResultSetFinal(rsTransfer);
							/**End 20130821 added by liubo.Bug #9116.*/
							
							// 删除子表中的数据
							/**Start 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B
							 * 删除债券派息的资金调拨时，只根据组合代码删除子表数据
							 * 避免插入新资金调拨时，同时删除掉好几个组合的同一只债券的数据*/
//
							dbl
							.executeSql("delete from "
									+ pub
											.yssGetTableName("Tb_Cash_SubTransfer")
									+ " where FNum in ("
									+ operSql.sqlCodes(cashNums) + ") and FPortCode = " + dbl.sqlString(portCode));
							
							/**End 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B*/
						}

						// ------------------------------------------------------------------
						

			            /**Start 20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B
			             * 传入交易所代码，用于判断是否国内接口导入的数据*/
						pubMethod.checkDeal(rs.getString("FSecurityCode"),portCode, dDate, 
								YssD.div(Math.abs(rs.getInt("FStorageAmount")), 1),secCode,rs.getString("FExchangeCode"));
						
						cjje = pubMethod.getLX(rs.getString("FSecurityCode"),
								portCode, dDate, YssD.div(Math.abs(rs.getInt("FStorageAmount")), 1),rs.getString("FExchangeCode"));
						/**Start 20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B*/

						if (cjje == 999999999)
						{
							cjje = 0;
						}
						
						// ------------------------------------------end BUG
						// #710------------------------------------------------------
						pstSub.setString(1, strNumDate);
						pstSub.setString(2, rs.getString("FSecurityCode"));
						pstSub.setString(3, rs.getString("FPortCode"));
						pstSub.setString(4, (analy2 ? rs
								.getString("FAnalysisCode2") : " "));
						pstSub.setString(5, (analy1 ? rs
								.getString("FAnalysisCode1") : " "));
						pstSub.setString(6, strRightType);
						pstSub.setString(7, strCashAccCode);
						pstSub.setDate(8, YssFun.toSqlDate(dDate));
						pstSub.setString(9, "00:00:00");
						pstSub.setDate(10, YssFun.toSqlDate(dSettleDate));	//20130420 modified by liubo.Story #3528.结算日期取债券计息期间设置中的派息到账日期
						pstSub.setString(11, "00:00:00");
						pstSub.setInt(12, 1);
						pstSub.setDouble(13, portCuryRate);
						pstSub.setDouble(14, baseCuryRate);
						pstSub.setDouble(15, rs.getDouble("FStorageCost"));
						pstSub.setDouble(16, rs.getDouble("FMStorageCost"));
						pstSub.setDouble(17, rs.getDouble("FVStorageCost"));
						pstSub.setDouble(18, rs.getDouble("FBaseCuryCost"));
						pstSub.setDouble(19, rs.getDouble("FMBaseCuryCost"));
						pstSub.setDouble(20, rs.getDouble("FVBaseCuryCost"));
						pstSub.setDouble(21, rs.getDouble("FPortCuryCost"));
						pstSub.setDouble(22, rs.getDouble("FMPortCuryCost"));
						pstSub.setDouble(23, rs.getDouble("FVPortCuryCost"));

						pstSub.setDouble(24, 0);
						pstSub.setInt(25, 1);
						pstSub.setString(26, pub.getUserCode());
						pstSub.setString(27, YssFun
								.formatDatetime(new java.util.Date()));
						pstSub.setString(28, pub.getUserCode());
						pstSub.setString(29, YssFun
								.formatDatetime(new java.util.Date()));
						pstSub.setDate(30, YssFun.toSqlDate(dSettleDate));	//20130420 modified by liubo.Story #3528.实际结算日期取债券计息期间设置中的派息到账日期
						pstSub.setString(31, strCashAccCode);
						pstSub.setDouble(32, cjje);
						pstSub.setDouble(33, 1); // 兑换汇率
						pstSub.setDouble(34, baseCuryRate);
						pstSub.setDouble(35, portCuryRate);

						/**Start 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B
						 * 计算债券利息的方法，需要传入当前正在操作的组合的组合代码
						 * 以避免多个组合投资同一只债券，可能会取错组合的证券应收应付数据*/
						pstSub.setDouble(36, getInterestOnBonds(rs.getString("FSecurityCode"),dIssueEndDate,portCode));	//20130427 added by liubo.Story #3528
						/**Start 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B */
						pstSub.setDouble(37, 0);
						pstSub.setDouble(38, 0);
						pstSub.setDouble(39, 0);
						pstSub.setDouble(40, 0);
						pstSub.setDouble(41, 0);
						pstSub.setDouble(42, 0);
						pstSub.setDouble(43, cjje);
						//add by songjie 2011.01.12 BUG:711 南方东英2010年12月16日01_B
						pstSub.setString(44, rs.getString("FATTRCLSCODE"));

						pstSub.executeUpdate();
												

						//20130403 added by liubo.Story #3714
						//getLX方法返回的成交金额等于999999999时，表示在债券派息期间设置中的手动派发利息和自动派发利息均未设置
						//这是需要返回让用户在收益支付手动处理的提示
						//若不等于999999999，在返回债券派息金额的提示信息
						//================================
						if (cjje != 999999999)
						{
							sBondDivInfo = "           债券代码：" + rs.getString("FSecurityCode") + 
											"，债券名称：" + rs.getString("FSecurityName") + "\r\n" +
									   	   	"本期派息金额：" + YssFun.formatNumber(cjje, "#,##0.00") + "，冲减应收利息：" + 
									   	   	
								/**Start 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B
								 * 计算债券利息的方法，需要传入当前正在操作的组合的组合代码
								 * 以避免多个组合投资同一只债券，可能会取错组合的证券应收应付数据*/
									   	   	YssFun.formatNumber(getInterestOnBonds(rs.getString("FSecurityCode"),
									   	   	dIssueEndDate,portCode), "#,##0.00") + "\r\n";
							/**Start 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B*/
						}
						//===============end=================
						
						conn.commit();
						isHave = true;

					//} modify by zhangjun 2012-01-10 STORY #1713 取消仅处理国内业务的功能限制

				}
			}
			conn.setAutoCommit(true);
			bTrans = false;
			return isHave;

		} catch (Exception e) {
			strDealInfo = "false";
			throw new YssException("系统执行债券派息时出现异常！" + "\n", e);
		} finally {
			// 关闭记录集，提供 RollBack 的机会
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs,rsStg);
			dbl.closeStatementFinal(pstSub);

		}

	}

	/**Start 20130626 modified by liubo.Bug #8396.QDV4赢时胜(深圳)2013年6月24日01_B
	 * 计算债券利息的方法，需要传入当前正在操作的组合的组合代码
	 * 以避免多个组合投资同一只债券，可能会取错组合的证券应收应付数据*/
	
	//20130427 added by liubo.Story #3528
	//派息时声称的交易数据，应计利息为“到计息截止日的应收利息余额”
	//此方法用于计算应收利息
	private double getInterestOnBonds(String sSecurityCode,java.util.Date dDate,String sPortCode) throws YssException
	{
		double dReturn = 0;
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			strSql = "select * from " + pub.yssGetTableName("tb_stock_secrecpay") +
					 " where fstoragedate = " + dbl.sqlDate(dDate) +
					 " and FSecurityCode = " + dbl.sqlString(sSecurityCode) + " and FSubTSFTypeCode = '06FI'" + 
					 " and FPortCode = " + dbl.sqlString(sPortCode);
			
			rs = dbl.queryByPreparedStatement(strSql);
	
			if (rs.next())
			{
				dReturn = rs.getDouble("FBal");
			}
		}
		catch(Exception ye)
		{
			throw new YssException();
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return dReturn;
	}



	// ------------------------------------------------------------------
	/**
	 * 债券兑付
	 * 
	 * @param portCode
	 * @return
	 * @throws YssException
	 */
	private boolean Interest(String portCode) throws YssException {
		String strSql = "";
		String strSqlSub = "";
		ResultSet rs = null;
		ResultSet rsSub = null;
        
		//modified by liubo.Story #2145
	    //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
		//=================================
//		PreparedStatement pst = null;
//		PreparedStatement pstSub = null;
		YssPreparedStatement pst = null;
		YssPreparedStatement pstSub = null;
		//===============end==================
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strYearMonth = "";
		String strRightType = "";
		String strMaxNum = "";
		String strMaxNumSub = "";
		CashAccountBean caBean = null;
		String strCashAccCode = " ";
		SecPecPayBean sec = null;
		SecRecPayAdmin secRecPayAdmin = null;
		CashTransAdmin cashAdmin = null;
		
		PublicMethodBean pubMethod = new PublicMethodBean();        

		boolean analy1; // 判断是否需要用分析代码；杨
		boolean analy2;
		// ------MS00269 QDV4中保2009年02月24日02_B -------------------
		CtlPubPara pubpara = null;
		boolean isFourDigit = false;
		// -----------------------------------------------------------
		// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090415 --
		EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
		rateOper.setYssPub(pub);
		// -----------------------------------------------------------

		boolean isHave = false; // 是否有
		
		//add by yanghaiming 20110214 #461
		ResultSet rsCpi = null;
		String strCPISql = "";
		double baseCPI = 0;
		double cpiValue = 0;
		EachExchangeHolidays holiday = new EachExchangeHolidays();// 实例化
		holiday.setYssPub(pub);// 设置pub
		
		double dInterestBal = 0;		//20130507 added by liubo.Story #3528.应收利息余额
		double dTotalFPayMoney = 0;		//20130507 added by liubo.Story #3528.本期偿还本金金额
		double dTotalAmount = 0;		//20130507 added by liubo.Story #3528.兑付数量
		
		sBondDivInfo = "";
		
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub
					.getOperDealCtx().getBean("cashacclinkdeal");
			operFun.setYssPub(pub);
			strYearMonth = YssFun.left(this.strOperStartDate, 4) + "00";

			secRecPayAdmin = new SecRecPayAdmin();
			secRecPayAdmin.setYssPub(pub);

			pubMethod.setYssPub(pub);

			// ----------- MS00269 QDV4中保2009年02月24日02_B
			// ------------------------
			pubpara = new CtlPubPara();
			pubpara.setYssPub(pub);
			String digit = pubpara.getKeepFourDigit(); // 通过通用参数来获取是否为保留四位小数
			if (digit.toLowerCase().equalsIgnoreCase("two")) { // 若两位
				isFourDigit = false;
			} else if (digit.toLowerCase().equalsIgnoreCase("four")) { // 若四位
				isFourDigit = true;
			}
			// ----------------------------------------------------------------------

			// 操作主表
			strSql = " insert into "
					+ pub.yssGetTableName("Tb_Data_Trade")
					+ " (FNum,FSecurityCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,"
					+ " FBargainDate,FBarGainTime,FSettleDate,FSettleTime,FAutoSettle,FPortCuryRate,"
					+ " FBaseCuryRate,FAllotFactor,FTradeAmount,FTradePrice,FTradeMoney,FUnitCost,"
					+ " FAccruedinterest,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FTotalCost,FAttrClsCode)"
					+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu 20100707
						// 合并太平版本调整
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";// 添加所属分类字段
																					// QDV4中保2010年07月06日01_B
																					// by
																					// leeyu
																					// 20100707
																					// 合并太平版本调整

			//modified by liubo.Story #2145
			//==============================
//			pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
			
			// ------------------------------------------------------------------------------
			// 操作子表
			strSqlSub = "insert into "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ "(FNum,FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,"
					+ " FAllotProportion,FOldAllotAmount,FAllotFactor,FBargainDate,FBarGainTime,FSettleDate, FSettleTime,"
					+ " FAutoSettle,FPortCuryRate,FBaseCuryRate,FTradeAmount,FTradePrice,FTradeMoney,FAccruedinterest,"
					+ " FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryCost,"
					+ " FMPortCuryCost, FVPortCuryCost, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,FTotalCost,FFactSettleDate,FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate,FAttrClsCode) "
					+ // 增加三个字段 by liyu 1128 //添加所属分类字段 QDV4中保2010年07月06日01_B by
						// leeyu 20100707 合并太平版本调整
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";// 添加所属分类字段
																														// QDV4中保2010年07月06日01_B
																														// by
																														// leeyu
																														// 20100707
																														// 合并太平版本调整

			//modified by liubo.Story #2145
			//==============================
//			pstSub = conn.prepareStatement(strSqlSub);
			pstSub = dbl.getYssPreparedStatement(strSqlSub);
			//==============end================
			
			// ------------------------------------------------------------------------------

			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");

			strRightType = YssOperCons.Yss_JYLX_ZQ;
			strDealInfo = "no";
			strSql = "delete from " + pub.yssGetTableName("Tb_Data_Trade")
					+ " where FTradeTypeCode= " + dbl.sqlString(strRightType)
					+ " and FBargainDate between "
					+ dbl.sqlDate(this.strOperStartDate) + " and "
					+ dbl.sqlDate(this.strOperEndDate);
			dbl.executeSql(strSql);
			// -------------------------------------------操作主表----------------------------
			strSql = " select a.*,b.*,c.*,d.*, "
					+ " e.*, "
					+ " f.FSecurityName "
					+ " from"
					+ " (select FsecurityCode,FStorageDate" + ",FAttrClsCode"
					+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu 20100707
						// 合并太平版本调整
					(analy1 ? " ,FAnalysisCode1" : " ")
					+ (analy2 ? ",FAnalysisCode2" : " ")
					+ ", sum(FStorageCost) as FStorageCost,"
					+ "  sum(FMStorageCost)  as FMStorageCost, sum(FVStorageCost)  as FVStorageCost,"
					+ "  sum(FPortCuryCost)  as FPortCuryCost,sum(FMPortCuryCost) as FMPortCuryCost,"
					+ "  sum(FVPortCuryCost) as FVPortCuryCost,sum(FBaseCuryCost)  as FBaseCuryCost,"
					+ "  sum(FMBaseCuryCost) as FMBaseCuryCost,sum(FVBaseCuryCost) as FVBaseCuryCost,"
					+ "  sum(FStorageAmount) as FStorageAmount from "
					+ pub.yssGetTableName("tb_stock_security")
					+
					// fanghaoln 20100603 MS01161 QDV4赢时胜上海2010年05月05日05_B
					"  where FPortCode in ("
					+ operSql.sqlCodes(portCode)
					+ ")"
					+
					// ------------------end------MS01161--------------------------
					"  and FYearMonth<>'"
					+ strYearMonth
					+ "' and FCheckState=1"
					+ "  group by FsecurityCode"
					+ (analy1 ? ",FAnalysisCode1" : " ")
					+ (analy2 ? ",FAnalysisCode2" : " ")
					+ ",FStorageDate"
					+ ",FAttrClsCode "
					+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu 20100707
						// 合并太平版本调整
					" )a left join"
					+ "  (select  FsecurityCode"
					+ ",FAttrClsCode"
					+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu 20100707
						// 合并太平版本调整
					(analy1 ? ",FAnalysisCode1" : " ")
					+ (analy2 ? ",FAnalysisCode2" : " ")
					+ ",FStorageDate,"
					+ "  sum(FBal)  as FBal, sum(FMBal) as FMBal, sum(FVBal) as FVBal,"
					+ "  sum(FPortCuryBal)  as FPortCuryBal,sum(FMPortCuryBal) as FMPortCuryBal,"
					+ "  sum(FVPortCuryBal) as FVPortCuryBal,sum(FBaseCuryBal)  as FBaseCuryBal, "
					+ "  sum(FMBaseCuryBal) as FMBaseCuryBal,sum(FVBaseCuryBal) as FVBaseCuryBal"
					+ "  from "
					+ pub.yssGetTableName("tb_stock_secrecpay")
					+
					// fanghaoln 20100603 MS01161 QDV4赢时胜上海2010年05月05日05_B
					"  where FPortCode in ("
					+ operSql.sqlCodes(portCode)
					+ ")"
					+
					// ------------------end------MS01161--------------------------
					"  and FYearMonth<>'"
					+ strYearMonth
					+ "' and FCheckState=1"
					+ "  and FTsfTypeCode="
					+ dbl.sqlString("06")
					+ "  and FSubTsfTypeCode="
					+ dbl.sqlString("06FI")
					+ "  group by FsecurityCode"
					+ ",FAttrClsCode"
					+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu 20100707
						// 合并太平版本调整
					(analy1 ? ",FAnalysisCode1" : " ")
					+ (analy2 ? ",FAnalysisCode2" : " ")
					+ ",FStorageDate"
					+ " )b on b.fsecuritycode=a.FsecurityCode and b.FStorageDate="
					+
					// -- MS00304 QDV4中保2009年03月10日01_B
					// ----------------------------------------
					" a.FStorageDate "
					+ // 不再需要再减一天获取前天的债券利息，只需要获取昨天的债券利息。
					" and b.FAttrClsCode = a.FAttrClsCode "
					+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu 20100707
						// 合并太平版本调整
					// -----------------------------------------------------------------------------
					(analy1 ? " and b.FAnalysisCode1=a.FAnalysisCode1 " : " ")
					+ (analy2 ? " and b.FAnalysisCode2=a.FAnalysisCode2 " : " ")
					+ " join "
					+ "( select FsecurityCode,FFaceValue,FInsCashDate,FIssuePrice,FInsEndDate as InsEnd  from "
					+ // 添加发行价格,为贴现债的情况而加。sj edit 20080701
					pub.yssGetTableName("tb_para_fixinterest")
					+ " where  FCheckState=1"
					+ ")c on a.FsecurityCode=c.FsecurityCode "
					+ " join "
					+ " (select FsecurityCode,FTradeCury,FCatCode,FSubCatCode "
					+
					// ----添加报价因子,为了在计算金额时以此来除以数量.sj modified 20081217 MS00095
					// -----//
					" ,FFactor,FHolidaysCode"
					+ ",FExchangeCode "
					+
					// -----------------------------------------------------------------------------//
					" from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " )d on d.FsecurityCode=a.FsecurityCode"
					+ " join (select * from " + pub.yssGetTableName("tb_para_interesttime") + " where FExRightDate between " + dbl.sqlDate(this.strOperStartDate) + " and " + dbl.sqlDate(this.strOperEndDate)
					+ " and FCheckState = 1 "
					+ " and FID in (select min(FID) from " + pub.yssGetTableName("tb_para_interesttime") + " where FExRightDate between " + dbl.sqlDate(this.strOperStartDate) + " and " + dbl.sqlDate(this.strOperEndDate)
					+ " )) e "
					+ " on a.fsecuritycode = e.FsecurityCode and a.FStorageDate = e.FExRightDate - 1"
					+ " left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("tb_para_security") + ") f on a.FsecurityCode = f.FsecurityCode";
			rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
			if (rs.next()) {
				rs.beforeFirst();
				while (rs.next()) {
					// ----- MS00284 QDV4赢时胜（上海）2009年3月05日02_B
					// 使用通用资金调拨删除方法，删除资金调拨 ---//
					pubDelCashTransfer(YssOperCons.Yss_JYLX_ZQ, rs
							.getDate("FInsCashDate"), portCode,// fanghaoln
																// 20100603
																// MS01161
																// QDV4赢时胜上海2010年05月05日05_B
							rs.getString("FSecurityCode"), rs
									.getString("FAttrClsCode"));// 添加所属分类字段
																// QDV4中保2010年07月06日01_B
																// by leeyu
																// 20100707
																// 合并太平版本调整
					// -------------------------------------------------------------------------------------//
					
					//20130510 added by liubo.Story #3528
					//若本期偿还本金为0，即不做任何处理
					//=============================
					if (YssD.mul(rs.getDouble("FPayMoney"), rs.getDouble("FStorageAmount")) == 0)
					{
						continue;
					}
					//=============end================
					

					// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
					// 为表加锁，以免在多用户同时处理时出现交易编号重复
					dbl.lockTableInEXCLUSIVE(pub
							.yssGetTableName("Tb_Data_Trade"));

					// 合并太平版本调整 by leeyu 20100821
					strMaxNum = "T"
							+ YssFun.formatDate(rs.getDate("FInsCashDate"),
									"yyyyMMdd")
							+ dbFun.getNextInnerCode(pub
									.yssGetTableName("Tb_Data_Trade"), dbl
									.sqlRight("FNUM", 6),
							// "000001",
									"800001",// 编号按业务类型生成 by leeyu 201006702
												// QDV4中保2010年07月02日01_B
									" where FNum like 'T"
											+ YssFun.formatDate(rs
													.getDate("FInsCashDate"),
													"yyyyMMdd") +
											// "%'");
											"8%'");// 编号按业务类型生成 by leeyu
													// 201006702
													// QDV4中保2010年07月02日01_B
					// 合并太平版本调整 by leeyu 20100821
					pst.setString(1, strMaxNum);
					pst.setString(2, rs.getString("FSecurityCode"));
					pst.setString(3, (analy2 ? rs.getString("FAnalysisCode2")
							: " "));
					pst.setString(4, (analy1 ? rs.getString("FAnalysisCode1")
							: " "));
					pst.setString(5, strRightType);
					pst.setString(6, " ");
					pst.setDate(7, rs.getDate("FInsCashDate")); // 成交日期
					pst.setString(8, "00:00:00"); // 成交时间
										
					// 拼接参数：节假日代码+当天偏离天数+操作日期
					String sRowStr = rs.getString("FHolidaysCode") + "\t"
							+ 0 + "\t"
							+ YssFun.formatDate(rs.getDate("FInsCashDate"));
					// 解析参数
					holiday.parseRowStr(sRowStr);
					Date settleDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));
					
					pst.setDate(9, settleDate); // 结算日期
					pst.setString(10, "00:00:00"); // 结算时间
					pst.setInt(11, 1); // 自动结算
					pst.setDouble(12, 0);
					pst.setDouble(13, YssFun.roundIt(this.getSettingOper()
							.getCuryRate(rs.getDate("FInsCashDate"),
									rs.getString("FTradeCury"), "",
									YssOperCons.YSS_RATE_BASE), 12));

					pst.setDouble(14, 0);
//					if (rs.getString("FSubCatCode") != null
//							&& rs.getString("FSubCatCode").equalsIgnoreCase(
//									"FI04")) { // 如果是贴现债,则用发行价格进行计算。sj edit
//												// 20080701.
//						pst.setDouble(16, rs.getDouble("FIssuePrice")); // 交易价格
//						// ----添加报价因子,为了在计算金额时以此来除以数量,再乘以价格.sj modified 20081217
//						// MS00095 ------------------//
//						pst.setDouble(17, YssD.round(YssD.div(rs
//								.getDouble("FStorageAmount"), rs
//								.getDouble("FFactor"))
//								* rs.getDouble("FIssuePrice"), 2)); // 交易金额
//						// ------------------------------------------------------------------------------------------
//						pst.setDouble(18, rs.getDouble("FIssuePrice")); // 单位成本
//
//					} else { // 如果为非贴现债，则用票面金额计算。sj edit 20080701
//						pst.setDouble(16, rs.getDouble("FFaceValue")); // 交易价格
//						// ----添加报价因子,为了在计算金额时以此来除以数量,再乘以价格.sj modified 20081217
//						// MS00095 ------------------//
//						pst.setDouble(17, YssD.round(YssD.div(rs
//								.getDouble("FStorageAmount"), rs
//								.getDouble("FFactor"))
//								* rs.getDouble("FFaceValue"), 2)); // 交易金额
//						// ----------------------------------------------------------------------------------------------------
//						pst.setDouble(18, rs.getDouble("FFaceValue")); // 单位成本
//					}
					
//				　　成交日期 “除权日”
//				　　结算日期、实际结算日 “到帐日”
//				　　成交单价 “每张还本金额”
//				　　成交数量 如本计息期间为该债券的最后一个付息期间，则成交数量取“除权日前一自然日的库存数量”；否则，取0。(p.s.分期还本时库存数量不变)
//				　　成交金额 上步所得“偿还本金金额”
//				　　应计利息 固定为0.
//				　　实收金额 上步所得“偿还本金金额”
					//============================
					if(YssFun.dateDiff(rs.getDate("InsEnd"),rs.getDate("FInsEndDate")) >= 0)
					{
						dTotalAmount = rs.getDouble("FStorageAmount");
					}
					else
					{
						dTotalAmount = 0;
					}
					
					pst.setDouble(15, dTotalAmount); // 交易数量

		            /**Start 20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B
		             * 传入交易所代码，用于判断是否国内接口导入的数据*/
					dInterestBal = pubMethod.getLX(rs.getString("FSecurityCode"),
							portCode, rs.getDate("FISSUEDATE"), YssD.div(Math.abs(rs.getInt("FStorageAmount")), 1),
							rs.getString("FExchangeCode"));
					/**Start 20130928 modified by liubo.Bug #80200.QDV4赢时胜(上海)2013年09月25日04_B*/
										
					dTotalFPayMoney = YssD.mul(rs.getDouble("FPayMoney"), rs.getDouble("FStorageAmount"));
					
					pst.setDouble(16, rs.getDouble("FPayMoney")); 
					pst.setDouble(17, YssD.add(dInterestBal, dTotalFPayMoney)); 
					pst.setDouble(18, rs.getDouble("FFaceValue")); 
					
					// ----//----------- MS00269 QDV4中保2009年02月24日02_B
					// -------------
					pst.setDouble(19, 0); 
					// -----------------------------------------------------------------

					//==============end==============
					pst.setInt(20, 1); // 数据来源
					pst.setString(21, pub.getUserCode());
					pst.setString(22, YssFun
							.formatDatetime(new java.util.Date()));
					pst.setString(23, pub.getUserCode());
					pst.setString(24, YssFun
							.formatDatetime(new java.util.Date()));
					pst.setDouble(25, dTotalFPayMoney);
					pst.setString(26,
							rs.getString("FAttrClsCode") != null
									&& rs.getString("FAttrClsCode").trim()
											.length() > 0 ? rs
									.getString("FAttrClsCode") : " "); // 添加所属分类字段
																		// QDV4中保2010年07月06日01_B
																		// by
																		// leeyu
																		// 20100707
																		// 合并太平版本调整
					pst.executeUpdate();
					// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
					// 每次执行都提交将锁释放
					
					sBondDivInfo += "           债券代码：" + rs.getString("FSecurityCode") + "，债券名称：" + rs.getString("FSecurityName") + "\r\n" +
				   	   "本期偿还本金：" + dTotalFPayMoney + "，" +
				   	   "兑付数量：" + dTotalAmount + "\r\n";
					
					conn.commit();
					// ---------------------------------操作子表-------------------------------------
					strSqlSub = " select a.*,b.*,c.*,d.*,e.*,f.*, " 
							+ " g.FExRightDate,g.FInsEndDate,g.FSettleDate "
							+" from "
							+ " (select FsecurityCode,FStorageDate,FPortCode,FStorageCost,"
							+ "FAttrClsCode,"
							+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu
								// 20100707 合并太平版本调整
							"  FMStorageCost,FVStorageCost,"
							+ "  FPortCuryCost,FMPortCuryCost,"
							+ "  FVPortCuryCost,FBaseCuryCost,"
							+ "  FMBaseCuryCost,FVBaseCuryCost,"
							+
							// --- MS00283 QDV4赢时胜（上海）2009年3月05日01_B
							// 增加对分析代码的筛选，以便在有多条不同的分析代码时，能正确区分
							(analy1 ? "FAnalysisCode1," : " ")
							+ (analy2 ? "FAnalysisCode2," : " ")
							+
							// --------------------------------------------------------------------------------------------------
							"  FStorageAmount  from "
							+ pub.yssGetTableName("tb_stock_security")
							+ "  where FPortCode in ("
							+ operSql.sqlCodes(portCode)
							+ ")"
							+ // fanghaoln 20100603 MS01161
								// QDV4赢时胜上海2010年05月05日05_B
							// ---- MS00283 QDV4赢时胜（上海）2009年3月05日01_B
							// 增加对分析代码的筛选，以便在有多条不同的分析代码时，能正确区分
							(analy1 ? " and FAnalysisCode1 = "
									+ dbl.sqlString(rs
											.getString("FAnalysisCode1")) : " ")
							+ (analy2 ? " and FAnalysisCode2 = "
									+ dbl.sqlString(rs
											.getString("FAnalysisCode2")) : " ")
							+
							// ----------------------------------------------------------------------------------------------------
							"  and FYearMonth<>'"
							+ strYearMonth
							+ "'  and FsecurityCode="
							+ dbl.sqlString(rs.getString("FsecurityCode"))
							+ "  and FStorageDate="
							+ dbl.sqlDate(rs.getDate("FStorageDate"))
							+ " and FCheckState=1"
							+ (rs.getString("FAttrClsCode") != null ? " and FAttrClsCode="
									+ dbl.sqlString(rs
											.getString("FAttrClsCode"))
									: " ")
							+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu
								// 20100707 合并太平版本调整
							")a join"
							+ " (select FPortCode,FPortCury from "
							+ pub.yssGetTableName("Tb_Para_Portfolio")
							+ " )b on b.FPortCode=a.FPortCode"
							+ "  and a.FPortCode=b.FPortCode"
							+ "  left join(select FsecurityCode,FPortCode,FStorageDate,"
							+ "FAttrClsCode,"
							+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu
								// 20100707 合并太平版本调整
							// ---MS00283 QDV4赢时胜（上海）2009年3月05日01_B
							// 增加对分析代码的筛选，以便在有多条不同的分析代码时，能正确区分
							(analy1 ? "FAnalysisCode1," : " ")
							+ (analy2 ? "FAnalysisCode2," : " ")
							+
							// --------------------------------------------------------------------------------------------------
							"  FBal, FMBal, FVBal,"
							+ "  FPortCuryBal,FMPortCuryBal,"
							+ "  FVPortCuryBal,FBaseCuryBal, "
							+ "  FMBaseCuryBal,FVBaseCuryBal"
							+ "  from "
							+ pub.yssGetTableName("tb_stock_secrecpay")
							+ "  where FPortCode in ("
							+ operSql.sqlCodes(portCode)
							+ ")"
							+ // fanghaoln 20100603 MS01161
								// QDV4赢时胜上海2010年05月05日05_B
							"  and FYearMonth<>'"
							+ strYearMonth
							+ " ' and FCheckState=1"
							+ (rs.getString("FAttrClsCode") != null ? " and FAttrClsCode="
									+ dbl.sqlString(rs
											.getString("FAttrClsCode"))
									: " ")
							+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu
								// 20100707 合并太平版本调整
							"  and FTsfTypeCode="
							+ dbl.sqlString("06")
							+ "  and FSubTsfTypeCode="
							+ dbl.sqlString("06FI")
							+ "  and FsecurityCode="
							+ dbl.sqlString(rs.getString("FsecurityCode"))
							+ "  and FStorageDate="
							+ dbl.sqlDate(rs.getDate("FStorageDate"))
							+ // 因为上面的SQL已经对FStorageDate取了前一天，这里不能再减了 胡坤
								// 20080130（长盛测试修改）
							"  )c on c.FsecurityCode=a.FsecurityCode and c.FPortCode=a.FPortCode"
							+
							// ---MS00283 QDV4赢时胜（上海）2009年3月05日01_B
							// 增加对分析代码的筛选，以便在有多条不同的分析代码时，能正确区分
							(analy1 ? " and c.FAnalysisCode1=a.FAnalysisCode1 "
									: " ")
							+ (analy2 ? " and c.FAnalysisCode2=a.FAnalysisCode2 "
									: " ")
							+ " and c.FAttrClsCode = a.FAttrClsCode "
							+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu
								// 20100707 合并太平版本调整
							// --------------------------------------------------------------------------------------------------
							"  join "
							+ " (select FsecurityCode,FFaceValue,FInsCashDate,FIssuePrice,FBASECPI from "//add "FBASECPI"基础CPI by yanghaiming 20110212 #461
							+ // sj edit 20080701
							pub.yssGetTableName("tb_para_fixinterest")
							+ " where FCheckState=1"
							+ ")d on d.FsecurityCode=a.FsecurityCode "
							+ " join (select FsecurityCode,FTradeCury,FCatCode,FSubCatCode "
							+
							// ----添加报价因子,为了在计算金额时以此来除以数量.sj modified 20081217
							// MS00095 -----//
							" ,FFactor"
							+
							// -----------------------------------------------------------------------------//
							" from "
							+ // sj edit 20080701
							pub.yssGetTableName("Tb_Para_Security")
							+ " )e on e.FsecurityCode=a.FsecurityCode"
							+ " join "
							+ " (select  FsecurityCode,FPortCode,FStorageDate"
							+ ",FAttrClsCode"
							+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu
								// 20100707 合并太平版本调整
							(analy1 ? ",FAnalysisCode1" : " ")
							+ (analy2 ? ",FAnalysisCode2 " : " ")
							+ "  from "
							+ pub.yssGetTableName("tb_stock_security")
							+ " )f on f.FsecurityCode=a.FsecurityCode and f.FPortCode=a.FPortCode"
							+ " and f.FStorageDate=a.FStorageDate"
							+ " and f.FAttrClsCode=a.FAttrClsCode "
							+ // 添加所属分类字段 QDV4中保2010年07月06日01_B by leeyu
								// 20100707 合并太平版本调整
							// ---- MS00283 QDV4赢时胜（上海）2009年3月05日01_B
							// 增加对分析代码的筛选，以便在有多条不同的分析代码时，能正确区分
							(analy1 ? " and f.FAnalysisCode1=a.FAnalysisCode1 "
									: " ")
							+ (analy2 ? " and f.FAnalysisCode2=a.FAnalysisCode2 "
									: " ")
							+ " left join (select * from " + pub.yssGetTableName("tb_para_interesttime") 
							+ " where FCheckState = 1 " 
                            + " and FExRightDate between "
							+ dbl.sqlDate(this.strOperStartDate)
							+ " and "
							+ dbl.sqlDate(this.strOperEndDate) + ") g "
                            + " on g.Fsecuritycode = d.fsecuritycode";
					// ---------------------------------------------------------------------------------------------------
					rsSub = dbl.openResultSet(strSqlSub,
							ResultSet.TYPE_SCROLL_INSENSITIVE);

					if (rsSub.next()) {
						// 此句放在下面 QDV4中保2010年07月06日01_B by leeyu 20100707
						// 合并太平版本调整
						// cashacc.setYssPub(pub);
						// cashacc.setLinkParaAttr( (analy1 ?
						// rsSub.getString("FAnalysisCode1") :
						// " "),
						// rsSub.getString("FPortCode"),
						// rsSub.getString("FSecurityCode"),
						// (analy2 ?
						// rsSub.getString("FAnalysisCode2") :
						// " "),
						// strRightType,
						// YssFun.addDay(rsSub.getDate(
						// "FInsCashDate"), -1));
						// QDV4中保2010年07月06日01_B by leeyu 20100707
						rsSub.beforeFirst();
						while (rsSub.next()) {
							//--------------------add by yanghaiming 20110212 #461-----------------
							//获取浮动CPI和基础CPI
							if (rs.getString("FSubCatCode") != null
									&& rs.getString("FSubCatCode")
									.equalsIgnoreCase("FI13")){//FI13为抗通胀债券
								strCPISql = "select FCPIPRICE from " + pub.yssGetTableName("TB_DATA_CPIVALUE") +
											" where FCHECKSTATE = 1 and FCPIVALUEDATE = (select max(FCPIVALUEDATE) from " + pub.yssGetTableName("TB_DATA_CPIVALUE") +
											" where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(rsSub.getString("FPortCode")) + 
											" and FCPIVALUEDATE <= " + dbl.sqlDate(rs.getDate("FInsCashDate"))  + ") and FPORTCODE = " + dbl.sqlString(rsSub.getString("FPortCode"));
								rsCpi = dbl.openResultSet(strCPISql);
								if (rsCpi.next()){
									cpiValue = rsCpi.getDouble("FCPIPRICE");
								}else{
									dbl.closeResultSetFinal(rsCpi);
									strCPISql = "select FCPIPRICE from " + pub.yssGetTableName("TB_DATA_CPIVALUE") +
												" where FCHECKSTATE = 1 and FCPIVALUEDATE = (select max(FCPIVALUEDATE) from " + pub.yssGetTableName("TB_DATA_CPIVALUE") +
												" where FCHECKSTATE = 1 and FPORTCODE = ' ' and FCPIVALUEDATE <= " + dbl.sqlDate(rsSub.getDate("FInsCashDate")) + ")";
									rsCpi = dbl.openResultSet(strCPISql);
									if (rsCpi.next()){
										cpiValue = rsCpi.getDouble("FCPIPRICE");
									}else{
										throw new YssException("请设置【" +
												rsSub.getDate("FInsCashDate").toString() +
			                                       "】日期的浮动CPI！");
									}
									dbl.closeResultSetFinal(rsCpi);
								}
								baseCPI = rsSub.getDouble("FBASECPI");
							}
							//-------------------------add by yanghaiming 20110212 #461--------------------
							// 将上一句调整到这里，因为rsSub是多组合，这里应区分同支证券不同组合的情况，
							// QDV4中保2010年07月06日01_B by leeyu 20100707
							cashacc.setYssPub(pub);
							cashacc.setLinkParaAttr((analy1 ? rsSub
									.getString("FAnalysisCode1") : " "), rsSub
									.getString("FPortCode"), rsSub
									.getString("FSecurityCode"),
									(analy2 ? rsSub.getString("FAnalysisCode2")
											: " "), strRightType, YssFun
											.addDay(rsSub
													.getDate("FInsCashDate"),
													-1));
							// QDV4中保2010年07月06日01_B by leeyu 20100707 合并太平版本调整

							// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090415
							// --------------------------
							rateOper.getInnerPortRate(rs
									.getDate("FInsCashDate"), rsSub
									.getString("FTradeCury"), rsSub
									.getString("FPortCode"));
							double portCuryRate = rateOper.getDPortRate();
							// -----------------------------------------------------------------------------------

							double baseCuryRate = this.getSettingOper()
									.getCuryRate(rs.getDate("FInsCashDate"),
											rsSub.getString("FTradeCury"),
											rsSub.getString("FPortCode"),
											YssOperCons.YSS_RATE_BASE);

							// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A
							// 多用户并发优化
							// 为表加锁，以免在多用户同时处理时出现交易编号重复
							dbl.lockTableInEXCLUSIVE(pub
									.yssGetTableName("Tb_Data_SubTrade"));

							strMaxNumSub = strMaxNum
									+ dbFun
											.getNextInnerCode(
													pub
															.yssGetTableName("Tb_Data_SubTrade"),
													dbl.sqlRight("FNUM", 5),
													"00001",
													" where FNum like '"
															+ strMaxNum
																	.replaceAll(
																			"'",
																			"''")
															+ "%'");
							caBean = cashacc.getCashAccountBean();
							if (caBean != null) {
								strCashAccCode = caBean.getStrCashAcctCode();
							} else {
								// --- MS01741 QDV4赢时胜深圳2010年9月14日02_B
								// 权益处理进行债券兑付时，未提示现金账户未设置 add by jiangshichao
								// 2010.09.16
								throw new YssException(
										"系统执行债券兑付时出现异常！"
												+ "\n"
												+ "债券 【"
												+ rs.getString("FSecurityCode")
												+ "】兑付时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
							}
							// --- MS01741 QDV4赢时胜深圳2010年9月14日02_B
							// 权益处理进行债券兑付时，未提示现金账户未设置 end
							// -----------------------------
							strSqlSub = "delete from "
									+ pub.yssGetTableName("Tb_Data_SubTrade")
									+ " where FTradeTypeCode='"
									+ strRightType
									+ "' and FSecurityCode='"
									+ rsSub.getString("FSecurityCode")
									+ "' and FPortCode="
									+ dbl.sqlString(rsSub
											.getString("FPortCode"))
									+ (analy1 ? " and FInvMgrCode="
											+ dbl
													.sqlString(rsSub
															.getString("FAnalysisCode1"))
											: " ")
									+ (analy2 ? " and FBrokerCode = "
											+ dbl
													.sqlString(rsSub
															.getString("FAnalysisCode2"))
											: " ")
									+ " and FBargainDate="
									+ dbl
											.sqlDate(rsSub
													.getDate("FExRightDate"))
									+ (rsSub.getString("FAttrClsCode") != null ? " and FAttrClsCode="
											+ dbl.sqlString(rsSub
													.getString("FAttrClsCode"))
											: " ") + // 添加所属分类字段
														// QDV4中保2010年07月06日01_B
														// by leeyu 20100707
														// 合并太平版本调整
									" and FDataSource=0";
							dbl.executeSql(strSqlSub);
							pstSub.setString(1, strMaxNumSub);
							pstSub.setString(2, rsSub
									.getString("FSecurityCode"));
							pstSub.setString(3, rsSub.getString("FPortCode"));
							pstSub.setString(4, (analy2 ? rsSub
									.getString("FAnalysisCode2") : " "));
							pstSub.setString(5, (analy1 ? rsSub
									.getString("FAnalysisCode1") : " "));
							pstSub.setString(6, strRightType);
							pstSub.setString(7, strCashAccCode);
							pstSub.setDouble(8, 0);
							pstSub.setDouble(9, 0);
							pstSub.setDouble(10, 0);
							pstSub.setDate(11, rsSub.getDate("FExRightDate"));
							pstSub.setString(12, "00:00:00");							
							pstSub.setDate(13, settleDate);
							pstSub.setString(14, "00:00:00");
							pstSub.setInt(15, 1);
							pstSub.setDouble(16, portCuryRate);
							pstSub.setDouble(17, baseCuryRate);
//						　　成交日期 “除权日”
//						　　结算日期、实际结算日 “到帐日”
//						　　成交单价 “每张还本金额”
//						　　成交数量 如本计息期间为该债券的最后一个付息期间，则成交数量取“除权日前一自然日的库存数量”；否则，取0。(p.s.分期还本时库存数量不变)
//						　　成交金额 上步所得“偿还本金金额”
//						　　应计利息 固定为0.
//						　　实收金额 上步所得“偿还本金金额”
							//============================
							pstSub.setDouble(18, dTotalAmount);
							pstSub.setDouble(19,rs.getDouble("FPayMoney"));
							pstSub.setDouble(20,dTotalFPayMoney);
							pstSub.setDouble(21, 0); 
							//=============end===============
							pstSub.setDouble(22, rsSub
									.getDouble("FStorageCost"));
							pstSub.setDouble(23, rsSub
									.getDouble("FMStorageCost"));
							pstSub.setDouble(24, rsSub
									.getDouble("FVStorageCost"));
							pstSub.setDouble(25, rsSub
									.getDouble("FBaseCuryCost"));
							pstSub.setDouble(26, rsSub
									.getDouble("FMBaseCuryCost"));
							pstSub.setDouble(27, rsSub
									.getDouble("FVBaseCuryCost"));
							pstSub.setDouble(28, rsSub
									.getDouble("FPortCuryCost"));
							pstSub.setDouble(29, rsSub
									.getDouble("FMPortCuryCost"));
							pstSub.setDouble(30, rsSub
									.getDouble("FVPortCuryCost"));
							pstSub.setDouble(31, 0);
							pstSub.setInt(32, 1);
							pstSub.setString(33, pub.getUserCode());
							pstSub.setString(34, YssFun
									.formatDatetime(new java.util.Date()));
							pstSub.setString(35, pub.getUserCode());
							pstSub.setString(36, YssFun
									.formatDatetime(new java.util.Date()));
							
							pstSub.setDouble(37,dTotalFPayMoney); // 投资总成本
								
							pstSub.setDate(38, settleDate);
							pstSub.setString(39, strCashAccCode); // 实际结算帐户
							
							pstSub.setDouble(40, dTotalFPayMoney);
							pstSub.setDouble(41, 1); // 兑换汇率
							pstSub.setDouble(42, baseCuryRate);
							pstSub.setDouble(43, portCuryRate);
							pstSub.setString(44, rsSub
									.getString("FAttrClsCode") != null
									&& rsSub.getString("FAttrClsCode").trim()
											.length() > 0 ? rsSub
									.getString("FAttrClsCode") : " ");// 添加所属分类字段
																		// QDV4中保2010年07月06日01_B
																		// by
																		// leeyu
																		// 20100707
																		// 合并太平版本调整
							pstSub.executeUpdate();
							// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A
							// 多用户并发优化
							// 每次执行都提交将锁释放
							conn.commit();

							isHave = true;
						}
					}
					dbl.closeResultSetFinal(rsSub);
				}
				strDealInfo = "true";
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);

			return isHave;
		} catch (Exception e) {
			strDealInfo = "false";
			throw new YssException("系统执行债券兑付时出现异常！" + "\n", e); // by 曹丞
																// 2009.02.01
																// 债券兑付异常信息
																// MS00004
																// QDV4.1-2009.2.1_09A
		} finally {
			// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
			// 关闭记录集，提供 RollBack 的机会
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs, rsSub);
			dbl.closeStatementFinal(pst, pstSub);
		}
	}
	
	
	
	/** add by zhouwei 20120418 债券兑付日，产生债券利息税的冲减数据
	* @Title: insertFILXSSecPayRec 
	* @Description: TODO
	* @param @param date
	* @param @param portCode
	* @param @throws YssException    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	private void insertFILXSSecPayRec(java.util.Date dDate,String portCode) throws YssException{
		ResultSet rs=null;
		String sql="";
		SecPecPayBean secRecPay = null;
		ArrayList list=new ArrayList();
		try{
			//查询债券兑付日期到期的证券应收应付库存（调拨类型债券利息税）
			sql="select a.FInsCashDate,c.*,b.FTradeCury from "+pub.yssGetTableName("tb_para_fixinterest")
			   +" a left join (select FsecurityCode, FTradeCury, FCatCode, FSubCatCode from "+pub.yssGetTableName("Tb_Para_Security")
			   +" where fcheckstate=1) b on b.FsecurityCode = a.FsecurityCode inner join ( select FsecurityCode,FPortCode,FStorageDate,FAttrClsCode,FBal,FInvestType"
			   +" from "+pub.yssGetTableName("tb_stock_secrecpay")+" where FPortCode ="+dbl.sqlString(portCode)
			   +" and FCheckState = 1 and  FTsfTypeCode ="+dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay)
			   +" and FSubTsfTypeCode="+dbl.sqlString(YssOperCons.YSS_ZJDBZLX_LXS_FI)
			   +" and FStorageDate="+dbl.sqlDate(YssFun.addDay(dDate, -1))//兑付日期前一天库存
			   +" ) c on c.FsecurityCode=a.fsecuritycode and c.FStorageDate=a.FInsCashDate-1"
			   +"  where a.FInsCashDate="+dbl.sqlDate(dDate)+" and a.FCheckState = 1 and c.FBal<>0";
			rs=dbl.openResultSet(sql);
			while(rs.next()){
				secRecPay=new SecPecPayBean();
				setSecPayRecAttr(rs,secRecPay);
				list.add(secRecPay);
			}
			//保存证券应收应付数据
			SecRecPayAdmin secAdmin=new SecRecPayAdmin();
			secAdmin.setYssPub(pub);
			secAdmin.addList(list);
			secAdmin.insert("", dDate, dDate, YssOperCons.YSS_ZJDBLX_Pay, YssOperCons.YSS_ZJDBZLX_LXS_FI,
					portCode, "", "", "", "", 0, true, 0, true, "", "", "FIInterest");
		}catch (Exception e) {
			throw new YssException("生成冲减债券利息税数据出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/** add by zhouwei 20120418 对证券应收应付数据赋值
	* @Title: setSecPayRecAttr 
	* @Description: TODO
	* @param @param rs
	* @param @param secRecPay
	* @param @throws YssException    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	private void setSecPayRecAttr(ResultSet rs,SecPecPayBean secRecPay) throws YssException{
		try{
			EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
	        rateOper.setYssPub(pub);
			 //基础汇率
            double baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FInsCashDate"),
                rs.getString("FTradeCury"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            //组合汇率
            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
            rateOper.getInnerPortRate(rs.getDate("FInsCashDate"), rs.getString("FTradeCury"), rs.getString("FPortCode"));
            double portCuryRate = rateOper.getDPortRate();
            secRecPay.setStrSecurityCode(rs.getString("FsecurityCode"));
            secRecPay.setRelaNumType("FIInterest");//类型为债券兑付
            secRecPay.setStrCuryCode(rs.getString("FTradeCury"));
            secRecPay.setStrPortCode(rs.getString("FPortCode"));
            secRecPay.setTransDate(rs.getDate("FInsCashDate"));
            secRecPay.setBaseCuryRate(baseCuryRate);
            secRecPay.setPortCuryRate(portCuryRate);
            secRecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
	   		secRecPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_LXS_FI);
	   		secRecPay.setAttrClsCode(rs.getString("FAttrClsCode"));
	   		secRecPay.setInvestType(rs.getString("FInvestType"));
	   		secRecPay.setCheckState(1);
	   		secRecPay.setInOutType(1);
	   		secRecPay.setDataSource(0);
	   		secRecPay.setMoney(-rs.getDouble("FBal")); 
	   		secRecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(secRecPay.getMoney(), baseCuryRate)); 
	   		secRecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(secRecPay.getMoney(), baseCuryRate,
	        portCuryRate,
	        secRecPay.getStrCuryCode(),
	        secRecPay.getTransDate(),
	        secRecPay.getStrPortCode()));
	   		secRecPay.setMMoney(secRecPay.getMoney());
	   		secRecPay.setVMoney(secRecPay.getMoney());
	   		secRecPay.setMBaseCuryMoney(secRecPay.getBaseCuryMoney());
	   		secRecPay.setVBaseCuryMoney(secRecPay.getBaseCuryMoney());
	   		secRecPay.setMPortCuryMoney(secRecPay.getPortCuryMoney());
	   		secRecPay.setVPortCuryMoney(secRecPay.getPortCuryMoney());	 
		}catch (Exception e) {
			throw new YssException("证券应收应付对象赋值出错！", e);
		}
	}
	/**
	 * 送股权益
	 * 
	 * @throws YssException
	 */
	private boolean BonusShare(String portCode) throws YssException {
		String strSql = "";
		String strSqlSub = "";
		ResultSet rs = null;
		ResultSet rsSub = null;
		BigDecimal bigSecurityAmount = null; // 证券数量
		double dSecurityCost = 0; // 证券成本
		double dRight = 0; // 权益（主表）
		double dRightSub = 0; // 权益（子表）
		String strRightType = ""; // 权益类型
		String strCashAccCode = " "; // 现金帐户
		String strMaxNum = "", strMaxNumSub = "";
		String strYearMonth = "";
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pst = null;
//		PreparedStatement pstSub = null;
		YssPreparedStatement pst = null;
		YssPreparedStatement pstSub = null;
        //==============end===================
		CashAccountBean caBean = null;
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务

		boolean analy1; // 判断是否需要用分析代码；杨
		boolean analy2;

		// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090415 --
		EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
		rateOper.setYssPub(pub);

		boolean isHave = false; // 是否有送股权益

		try {
			conn.setAutoCommit(false);
			bTrans = true;
			BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub
					.getOperDealCtx().getBean("cashacclinkdeal");
			operFun.setYssPub(pub);
			strYearMonth = YssFun.left(this.strOperStartDate, 4) + "00";
			YssType lAmount = new YssType();

			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");

			// 操作主表
			strSql = " insert into "
					+ pub.yssGetTableName("Tb_Data_Trade")
					+ " (FNum,FSecurityCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FBargainDate,FBarGainTime,"
					+ " FSettleDate,FSettleTime,FAutoSettle,FTradeAmount,FTradePrice,FTradeMoney,"
					+ " FCashAccCode,FPortCuryRate,FBaseCuryRate,FAllotFactor,"
					+ " FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FAttrClsCode)"
					+ // 2010.01.07.增加属性分类，MS00903
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";// 增加一个字段，2010.01.07
																				// MS00903

			//modified by liubo.Story #2145
			//==============================
//			pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
			

			// 操作子表
			strSqlSub = "insert into "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ "(FNum, FSecurityCode, FPortCode, FBrokerCode, FInvMgrCode, FTradeTypeCode, FCashAccCode, "
					+ " FAllotProportion, FOldAllotAmount, FAllotFactor, FBargainDate, FBarGainTime, FSettleDate, FSettleTime,"
					+ " FAutoSettle, FPortCuryRate, FBaseCuryRate, FTradeAmount, FTradePrice, FTradeMoney, FAccruedinterest,"
					+ " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, FPortCuryCost,"
					+ // MS00903,增加属性分类,2010.01.07
					" FMPortCuryCost, FVPortCuryCost, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,FFactSettleDate,FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate,FAttrClsCode) "
					+ // 新增三个字段 by liyu 1128.
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";// 增加一个字段，2010.01.07
																														// MS00903

			//modified by liubo.Story #2145
			//==============================
//			pstSub = conn.prepareStatement(strSqlSub);
			pstSub = dbl.getYssPreparedStatement(strSql);
			//==============end================
			

			strRightType = YssOperCons.YSS_JYLX_SG; // strRightType = "07";
			strDealInfo = "no";

			// 操作主表
			// 2007.12.02 修改 蒋锦 考虑使用不同数据库的非空判断
			strSql = "select a.*,b.* from (select "
					+ dbl.sqlIsNull("FSSecurityCode", "FTSecurityCode")
					+ " as FSecurityCode1,"
					+ "FTSecurityCode,FRecordDate,FExRightDate,FPreTaxRatio, FRoundCode from "
					+ pub.yssGetTableName("Tb_Data_BonusShare")
					+ " where FExRightDate between "
					+ dbl.sqlDate(this.strOperStartDate)
					+ " and "
					+ dbl.sqlDate(this.strOperEndDate)
					+ " and FCheckState = 1) a join (select FSecurityCode,FStorageDate,FAttrClsCode"
					+ // 2010.01.07.MS00903.考虑到所属分类
					(analy1 ? ",FAnalysisCode1" : " ")
					+ (analy2 ? ",FAnalysisCode2" : " ")
					+ " from "
					+ pub.yssGetTableName("Tb_Stock_Security")
					+ " where FYearMonth<>'"
					+ strYearMonth
					+
					// fanghaoln 20100603 MS01161 QDV4赢时胜上海2010年05月05日05_B
					"' and FCheckState=1 and FSTORAGEAMOUNT>0 and FPortCode in ("
					+ operSql.sqlCodes(portCode)
					+ // edit by xuxuming,2010.01.18.加上库存数量大于0的条件 MS00952
						// 成分股调整日的第二天，库存数量为0的记录也会生成权益数据
					") group by FSecurityCode,FStorageDate,FAttrClsCode"
					+ // 2010.01.07.MS00903.考虑到所属分类
					(analy1 ? ",FAnalysisCode1" : " ")
					+ (analy2 ? ",FAnalysisCode2" : " ") + ") b"
					+
					// 2007.12.02 修改 蒋锦 考虑在不同数据库下日期型字段的加法运算
					" on a.fsecuritycode1 = b.fsecuritycode and a.FExRightDate = "
					+ dbl.SqlDateFiledAdd("b.FStorageDate", "1");
			rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);

			if (rs.next()) {
				strSql = "delete from " + pub.yssGetTableName("Tb_Data_Trade")
						+ " where FTradeTypeCode='" + strRightType
						+ "' and FSecurityCode='"
						+ rs.getString("FTSecurityCode") + "'"
						+ " and FBargainDate="
						+ dbl.sqlDate(rs.getDate("FExRightDate"));
				dbl.executeSql(strSql);
				conn.commit();
				rs.beforeFirst();
				while (rs.next()) {
					// ============add by xuxuming,2010.01.15.MS00932
					// 送股、分红权益处理时，生成业务资料属性分类没有做判断 送股时，先查询当天是否有指数调整信息。＝＝＝＝
					// ===============如果有调整，以调整之后的属性分类作为送股的属性分类＝＝＝＝＝＝＝＝＝＝＝＝＝＝
					String strSqlSec = "select * from "
							+ pub.yssGetTableName("Tb_Data_Integrated")
							+ " where FPortCode in ("
							+ operSql.sqlCodes(portCode)
							+ // fanghaoln 20100603 MS01161
								// QDV4赢时胜上海2010年05月05日05_B
							") and FSecurityCode='"
							+ rs.getString("FSecurityCode1")
							+ "' and FEXCHANGEDATE = "
							+ dbl.sqlDate(YssFun.addDay(rs
									.getDate("FStorageDate"), 1))
							+ " and FTradeTypeCode='101' and FInOutType='1'";// 只查询流入的数据。有这只证券的流入，则已流入的所属分类作为送股的属性分类
					ResultSet rsSec = null;
					String strSecAttrCls = "";
					rsSec = dbl.openResultSet(strSqlSec);
					if (rsSec.next()) {// 一天内，一只证券最多只有一笔'成分股转换'类型的流入数据，故用IF
						strSecAttrCls = rsSec.getString("FAttrClsCode");
					}
					rsSec.close();
					// =========================end==========================================
					dSecurityCost = operFun.getStorageCostTotal(
							YssFun.addDay(rs.getDate("FExRightDate"), -1), // 除权日前一天fazmm20071027
							rs.getString("FSecurityCode1"), (analy1 ? rs
									.getString("FAnalysisCode1") : " "),
							(analy2 ? rs.getString("FAnalysisCode2") : " "),
							"", "", lAmount);

					// QDV4嘉实基金20081107日02_D 需求 20081121 王晓光
					bigSecurityAmount = new BigDecimal(Double.toString(lAmount
							.getDouble()));
					if (bigSecurityAmount.compareTo(new BigDecimal(0.0)) > 0) {
						dRight = this.getSettingOper().reckonRoundMoney(
								rs.getString("FRoundCode") + "",
								YssD.mul(bigSecurityAmount, rs
										.getBigDecimal("FPreTaxRatio")));

						// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A
						// 多用户并发优化
						// 为表加锁，以免在多用户同时处理时出现交易编号重复
						dbl.lockTableInEXCLUSIVE(pub
								.yssGetTableName("Tb_Data_Trade"));
						strMaxNum = "T"
								+ YssFun.formatDate(rs.getDate("FExRightDate"),
										"yyyyMMdd")
								+ dbFun
										.getNextInnerCode(
												pub
														.yssGetTableName("Tb_Data_Trade"),
												dbl.sqlRight("FNUM", 6),
												"000001",
												" where FNum like 'T"
														+ YssFun
																.formatDate(
																		rs
																				.getDate("FExRightDate"),
																		"yyyyMMdd")
														+ "%'");
						// ========add by
						// xuxuming,20090819.MS00635.QDV4嘉实2009年08月14日01_B============
						String sReplace = "8"; // 替换为
						strMaxNum = YssFun.left(strMaxNum, 9) + sReplace
								+ YssFun.right(strMaxNum, 5); // 将编号第10位统一替换成'8'
						// ===============================================================================

						pst.setString(1, strMaxNum);
						pst.setString(2, rs.getString("FTSecurityCode"));
						pst.setString(3, analy2 ? rs
								.getString("FAnalysisCode2") : " ");
						pst.setString(4, analy1 ? rs
								.getString("FAnalysisCode1") : " ");
						pst.setString(5, strRightType);
						pst.setDate(6, rs.getDate("FExRightDate"));
						pst.setString(7, "00:00:00");
						pst.setDate(8, rs.getDate("FExRightDate"));
						pst.setString(9, "00:00:00");
						pst.setInt(10, 1);
						pst.setDouble(11, dRight);
						pst.setDouble(12, 0);
						pst.setDouble(13, 0);
						pst.setString(14, " ");
						pst.setDouble(15, 0);
						pst.setDouble(16, 0);
						pst.setDouble(17, 0);
						pst.setInt(18, 1);
						pst.setString(19, pub.getUserCode());
						pst.setString(20, YssFun
								.formatDatetime(new java.util.Date()));
						pst.setString(21, pub.getUserCode());
						pst.setString(22, YssFun
								.formatDatetime(new java.util.Date()));
						// ===========add by xuxuming,2010.01.15.MS00932
						// 送股、分红权益处理时，生成业务资料属性分类没有做判断 当天有指数信息调整，以调整后的类型作为送股的类型
						if (strSecAttrCls != null
								&& strSecAttrCls.trim().length() > 0) {
							pst.setString(23, strSecAttrCls);
						} else {
							pst.setString(23,
									rs.getString("FAttrClsCode") != null ? rs
											.getString("FAttrClsCode") : " ");// //2010.01.07.增加属性分类，MS00903
						}
						// =================end==========================================
						pst.executeUpdate();

						// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A
						// 多用户并发优化
						// 每次执行都提交将锁释放
						conn.commit();
					}

					// 操作子表
					// 2008.04.25 蒋锦 修改 添加 FPayDate 字段的查询
					strSqlSub = "select a.*, b.* from ( select "
							+ dbl.sqlIsNull("FSSecurityCode", "FTSecurityCode")
							+ " as FSecurityCode1,FTSecurityCode, FRecordDate, FEXRightDate, FPreTaxRatio, FRoundCode, FPayDate from "
							+ pub.yssGetTableName("Tb_Data_BonusShare")
							+ " where FExRightDate between "
							+ dbl.sqlDate(this.strOperStartDate)
							+ " and "
							+ dbl.sqlDate(this.strOperEndDate)
							+ " and FCheckState = 1) a join (select * from "
							+ pub.yssGetTableName("Tb_Stock_Security")
							+ " where FPortCode in ("
							+ operSql.sqlCodes(portCode)
							+ // fanghaoln 20100603 MS01161
								// QDV4赢时胜上海2010年05月05日05_B
							") and FSecurityCode = '"
							+ rs.getString("FSecurityCode1")
							+ "' and FStorageDate = "
							+ dbl.sqlDate(rs.getDate("FStorageDate"))
							+ (analy1 ? " and FAnalysisCode1 = "
									+ dbl.sqlString(rs
											.getString("FAnalysisCode1")) : " ")
							+ (analy2 ? " and FAnalysisCode2 = "
									+ dbl.sqlString(rs
											.getString("FAnalysisCode2")) : " ")
							+ " and FYearMonth<>'"
							+ strYearMonth
							+ "' and FCheckState=1 and FSTORAGEAMOUNT>0)b  on a.fsecuritycode1 = b.fsecuritycode and a.FExRightDate = "
							+ // edit by xuxuming,2010.01.18.加上库存数量大于0的条件
								// MS00952 成分股调整日的第二天，库存数量为0的记录也会生成权益数据
							dbl.SqlDateFiledAdd("b.FStorageDate", "1");

					rsSub = dbl.openResultSet(strSqlSub,
							ResultSet.TYPE_SCROLL_INSENSITIVE);

					if (rsSub.next()) {
						cashacc.setYssPub(pub);
						cashacc.setLinkParaAttr((analy1 ? rsSub
								.getString("FAnalysisCode1") : " "), rsSub
								.getString("FPortCode"), rsSub
								.getString("FTSecurityCode"), (analy2 ? rsSub
								.getString("FAnalysisCode2") : " "),
								strRightType, rsSub.getDate("FRecordDate"));

						strSqlSub = "delete from "
								+ pub.yssGetTableName("Tb_Data_SubTrade")
								+ " where FTradeTypeCode='"
								+ strRightType
								+ "' and FSecurityCode='"
								+ rsSub.getString("FTSecurityCode")
								+ "' and FPortCode="
								+ dbl.sqlString(rsSub.getString("FPortCode"))
								+
								// ------- MS00200 QDV4赢时胜上海2009年01月06日05_B sj
								// modified 添加删除的筛选条件 ----//
								(analy1 ? " and FINVMGRCODE = "
										+ dbl.sqlString(rsSub
												.getString("FAnalysisCode1"))
										: "")
								+ // 添加投资经理的筛选
								(analy2 ? " and FBROKERCODE = "
										+ dbl.sqlString(rsSub
												.getString("FAnalysisCode2"))
										: "")
								+ // 添加券商的筛选
								// -------------------------------------------------------------------------------//
								" and FBargainDate="
								+ dbl.sqlDate(rsSub.getDate("FExRightDate"))
								+ " and FDataSource = 0";
						;
						dbl.executeSql(strSqlSub);
						conn.commit();
						rsSub.beforeFirst();
						while (rsSub.next()) {
							dSecurityCost = operFun.getStorageCost(
									YssFun.addDay(
											rsSub.getDate("FExRightDate"), -1), // 除权日前一天fazmm20071027
									rsSub.getString("FSecurityCode1"), rsSub
											.getString("FPortCode"),
									(analy1 ? rsSub.getString("FAnalysisCode1")
											: " "),
									(analy1 ? rsSub.getString("FAnalysisCode2")
											: " "), "", "", lAmount, rsSub
											.getString("FAttrClsCode"));
							bigSecurityAmount = new BigDecimal(Double
									.toString(lAmount.getDouble())); // QDV4嘉实基金20081107日02_D
																		// 需求
																		// 20081121
																		// 王晓光
							if (bigSecurityAmount
									.compareTo(new BigDecimal(0.0)) > 0) {
								dRightSub = this
										.getSettingOper()
										.reckonRoundMoney(
												rsSub.getString("FRoundCode")
														+ "",
												YssD
														.mul(
																bigSecurityAmount,
																rsSub
																		.getBigDecimal("FPreTaxRatio")));
								caBean = cashacc.getCashAccountBean();
								if (caBean != null) {
									strCashAccCode = caBean
											.getStrCashAcctCode();
								}

								// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A
								// 20090415 --------------------------
								rateOper.getInnerPortRate(rsSub
										.getDate("FExRightDate"), rsSub
										.getString("FCuryCode"), rsSub
										.getString("FPortCode"));
								double portCuryRate = rateOper.getDPortRate();
								// -----------------------------------------------------------------------------------

								double baseCuryRate = this.getSettingOper()
										.getCuryRate(
												rsSub.getDate("FExRightDate"),
												rsSub.getString("FCuryCode"),
												rsSub.getString("FPortCode"),
												YssOperCons.YSS_RATE_BASE);

								// 2009-05-26 蒋锦 添加 MS00006
								// QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
								// 为表加锁，以免在多用户同时处理时出现交易编号重复
								dbl.lockTableInEXCLUSIVE(pub
										.yssGetTableName("Tb_Data_SubTrade"));
								strMaxNumSub = strMaxNum
										+ dbFun
												.getNextInnerCode(
														pub
																.yssGetTableName("Tb_Data_SubTrade"),
														dbl.sqlRight("FNUM", 5),
														"00001",
														" where FNum like '"
																+ strMaxNum
																		.replaceAll(
																				"'",
																				"''")
																+ "%'");

								pstSub.setString(1, strMaxNumSub);
								pstSub.setString(2, rsSub
										.getString("FTSecurityCode"));
								pstSub.setString(3, rsSub
										.getString("FPortCode"));
								pstSub.setString(4, (analy2 ? rsSub
										.getString("FAnalysisCode2") : " "));
								pstSub.setString(5, (analy1 ? rsSub
										.getString("FAnalysisCode1") : " "));
								pstSub.setString(6, strRightType);
								pstSub.setString(7, strCashAccCode);
								pstSub.setDouble(8, 0);
								pstSub.setDouble(9, 0);
								pstSub.setDouble(10, 0);
								pstSub.setDate(11, rsSub
										.getDate("FExRightDate"));
								pstSub.setString(12, "00:00:00");
								// 2008.04.27 蒋锦 修改 原来是取 FExRightDate
								pstSub.setDate(13, rsSub.getDate("FPayDate"));
								pstSub.setString(14, "00:00:00");
								pstSub.setInt(15, 1);
								// 2008.04.27 蒋锦 修改 汇率
								pstSub.setDouble(16, portCuryRate);
								pstSub.setDouble(17, baseCuryRate);
								pstSub.setDouble(18, dRightSub);
								pstSub.setDouble(19, 0);
								pstSub.setDouble(20, 0);
								pstSub.setDouble(21, 0);
								pstSub.setDouble(22, 0);
								pstSub.setDouble(23, 0);
								pstSub.setDouble(24, 0);
								pstSub.setDouble(25, 0);
								pstSub.setDouble(26, 0);
								pstSub.setDouble(27, 0);
								pstSub.setDouble(28, 0);
								pstSub.setDouble(29, 0);
								pstSub.setDouble(30, 0);
								pstSub.setDouble(31, 0);
								pstSub.setInt(32, 1);
								pstSub.setString(33, pub.getUserCode());
								pstSub.setString(34, YssFun
										.formatDatetime(new java.util.Date()));
								pstSub.setString(35, pub.getUserCode());
								pstSub.setString(36, YssFun
										.formatDatetime(new java.util.Date()));
								// 2008.04.27 蒋锦 修改 原来是取 FExRightDate
								pstSub.setDate(37, rsSub.getDate("FPayDate"));
								pstSub.setString(38, strCashAccCode); // 实际结算帐户
								pstSub.setDouble(39, 0); // 实际结算金额
								pstSub.setDouble(40, 1); // 兑换汇率
								pstSub.setDouble(41, baseCuryRate);
								pstSub.setDouble(42, portCuryRate);
								// ===========MS00932 送股、分红权益处理时，生成业务资料属性分类没有做判断
								// add by
								// xuxuming,2010.01.15.当天有指数信息调整，以调整后的类型作为送股的类型
								if (strSecAttrCls != null
										&& strSecAttrCls.trim().length() > 0) {
									pstSub.setString(43, strSecAttrCls);
								} else {
									pstSub
											.setString(
													43,
													rsSub
															.getString("FAttrClsCode") != null ? rsSub
															.getString("FAttrClsCode")
															: " ");// //2010.01.07.增加属性分类，MS00903
								}
								// =================end==========================================
								pstSub.executeUpdate();
								// 2009-05-26 蒋锦 添加 MS00006
								// QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
								// 每次执行都提交将锁释放
								conn.commit();

								isHave = true;
							}
						}
						strDealInfo = "true";
					}
					dbl.closeResultSetFinal(rsSub);
				}
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return isHave;
		} catch (Exception e) {
			strDealInfo = "false";
			throw new YssException("系统执行送股权益时出现异常！" + "\n", e); // by 曹丞
																// 2009.02.01
																// 送股权益异常信息
																// MS00004
																// QDV4.1-2009.2.1_09A
		} finally {
			dbl.closeResultSetFinal(rs, rsSub);
			dbl.closeStatementFinal(pst, pstSub);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 计算费用
	 * 
	 * @throws YssException
	 */
	private String buildFeesStr(String sSecCode, String sTradeType,
			String sPortCode, String sBrokerCode, double dMoney,
			double dAmount, double dCost, java.util.Date dDate)
			throws YssException {
		String fees = "";
		double dFeeMoney;
		FeeBean fee = null;
		YssFeeType feeType = null;
		ArrayList alFeeBeans = null;
		StringBuffer bufAll = new StringBuffer();
		try {
			BaseOperDeal baseOper = this.getSettingOper();
			BaseFeeDeal feeOper = (BaseFeeDeal) pub.getOperDealCtx().getBean(
					"feedeal");
			baseOper.setYssPub(pub);
			feeOper.setYssPub(pub);
			feeOper.setFeeAttr(sSecCode, sTradeType, sPortCode, sBrokerCode,
					dMoney);
			alFeeBeans = feeOper.getFeeBeans();
			if (alFeeBeans != null) {
				feeType = new YssFeeType();
				feeType.setMoney(dMoney);
				feeType.setInterest(-1);
				feeType.setAmount(dAmount);
				for (int i = 0; i < alFeeBeans.size(); i++) {
					fee = (FeeBean) alFeeBeans.get(i);
					dFeeMoney = baseOper.calFeeMoney(feeType, fee, dDate);
					bufAll.append(fee.getFeeCode()).append(
							YssCons.YSS_ITEMSPLITMARK2);
					bufAll.append(fee.getFeeName()).append(
							YssCons.YSS_ITEMSPLITMARK2);
					bufAll.append(YssFun.formatNumber(dFeeMoney, "###0.##"))
							.append("\f\n");
				}
				if (bufAll.toString().length() > 2) {
					fees = bufAll.toString().substring(0,
							bufAll.toString().length() - 2);
				}
			}
			return fees;
		} catch (Exception e) {
			throw new YssException("系统执行配股权益时出现异常！" + "\n" + "获取费用信息出错！\n");
		}

	}

	/**
	 * 配股权益
	 * 
	 * @throws YssException
	 */
	private boolean RightsIssue(String portCode) throws YssException {
		String strSql = "";
		String strSqlSub = "";
		ResultSet rs = null;
		ResultSet rsSub = null;
		BigDecimal bigSecurityAmount = null; // 证券数量
		double dSecurityCost = 0; // 证券成本
		double dRight = 0; // 权益（主表）
		double dRightSub = 0; // 权益（子表）
		String strRightType = ""; // 权益类型
		String strCashAccCode = " "; // 现金帐户
		String strMaxNum = "", strMaxNumSub = "";
		String strYearMonth = "";
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pst = null;
//		PreparedStatement pstSub = null;
		YssPreparedStatement pst = null;
		YssPreparedStatement pstSub = null;
        //===============end==================
		CashAccountBean caBean = null;
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String[] sFee = null;

		boolean analy1; // 判断是否需要用分析代码；杨
		boolean analy2;

		double dTradeMoney = 0;
		double dBaseRate = 1;
		double dPortRate = 1;
		// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090415 --
		EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
		rateOper.setYssPub(pub);
		// -----------------------------------------------------------

		boolean isHave = false; // 是否有配股权益

		try {
			conn.setAutoCommit(false);
			bTrans = true;
			BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub
					.getOperDealCtx().getBean("cashacclinkdeal");
			operFun.setYssPub(pub);
			strYearMonth = YssFun.left(this.strOperStartDate, 4) + "00";
			YssType lAmount = new YssType();
			String fees = "";

			// 操作主表
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Data_Trade")
					+ "(FNum, FSecurityCode, FBrokerCode, FInvMgrCode, FTradeTypeCode,FBargainDate, FBarGainTime,"
					+ "FSettleDate, FSettleTime,FAutoSettle,FTradeAmount, FTradePrice, FTradeMoney,"
					+ "FCashAccCode,FPortCuryRate,FBaseCuryRate,FAllotFactor,"
					+ " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4,"
					+ " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8,"
					+ " FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			//modified by liubo.Story #2145
			//==============================
//			pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================

			// 操作子表
			strSqlSub = "insert into "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ "(FNum, FSecurityCode, FPortCode, FBrokerCode, FInvMgrCode, FTradeTypeCode, FCashAccCode, "
					+ " FAllotProportion, FOldAllotAmount, FAllotFactor, FBargainDate, FBarGainTime, FSettleDate, FSettleTime,"
					+ " FAutoSettle, FPortCuryRate, FBaseCuryRate, FTradeAmount, FTradePrice, FTradeMoney, FAccruedinterest,"
					+ " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4,"
					+ " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8,"
					+ " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, FPortCuryCost,"
					+ " FMPortCuryCost, FVPortCuryCost, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,FFactSettleDate,FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate) "
					+ // 新增三个字段 by liyu 1128
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			//modified by liubo.Story #2145
			//==============================
//			pstSub = conn.prepareStatement(strSqlSub);
			pstSub = dbl.getYssPreparedStatement(strSqlSub);
			//==============end================

			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");

			strRightType = YssOperCons.YSS_JYLX_QZSP; // strRightType =
														// "08";//权证送配
			strDealInfo = "no";

			// 操作主表
			strSql = "select a.*,b.* from ( select FSecurityCode as FSecurityCode1,FTSecurityCode,"
					+ "FRecordDate,FExRightDate,FPayDate,FExpirationDate,FRIPrice,FPreTaxRatio,FRoundCode from "
					+ pub.yssGetTableName("Tb_Data_RightsIssue")
					+ " where FExRightDate between "
					+ dbl.sqlDate(this.strOperStartDate)
					+ " and "
					+ dbl.sqlDate(this.strOperEndDate)
					+ " and FCheckState = 1) a join (select FSecurityCode,FStorageDate"
					+ (analy1 ? ",FAnalysisCode1" : " ")
					+ (analy2 ? ",FAnalysisCode2" : " ")
					+ " from "
					+ pub.yssGetTableName("Tb_Stock_Security")
					+ " where FYearMonth<>'"
					+ strYearMonth
					+ "' and FCheckState = 1 and FSTORAGEAMOUNT>0 "
					+ // edit by xuxuming,2010.01.18.加上库存数量大于0的条件 MS00952
						// 成分股调整日的第二天，库存数量为0的记录也会生成权益数据
					// -------MS00285 QDV4赢时胜（上海）2009年3月05日03_B 加入组合的筛选条件
					// -----------------
					" and FPortCode in ("
					+ operSql.sqlCodes(this.strPortCode)
					+ ")"
					+
					// -----------------------------------------------------------------------------------
					" group by FSecurityCode,FStorageDate"
					+ (analy1 ? ",FAnalysisCode1" : " ")
					+ (analy2 ? ",FAnalysisCode2" : " ")
					+ " ) b "
					+ " on a.fsecuritycode1 = b.fsecuritycode and a.FExRightDate = "
					+ // 改为除权日前一天 20071028 胡昆
					dbl.sqlDateAdd("b.FStorageDate", "+1");

			rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
			if (rs.next()) {
				strSql = "delete from "
						+ pub.yssGetTableName("Tb_Data_Trade")
						+ " where FTradeTypeCode='"
						+ strRightType
						+
						// 去掉证券代码做为条件,这里不能判断有证券代码的情况 QDV4赢时胜（上海）2009年4月21日05_B
						// by leeyu 20090428
						// "' and FSecurityCode='" +
						// rs.getString("FTSecurityCode") +
						// xuqiji 20090601:QDV4赢时胜（上海）2009年5月5日01_B MS00436
						// 处理配股权益时在多组合、多分析代码的情况下会产生重复的业务资料 -------
						"' and FBargainDate between "
						+ dbl.sqlDate(this.strOperStartDate) + " and "
						+ dbl.sqlDate(this.strOperEndDate);
				// -------------------------------------------------end-------------------------------------------------//
				dbl.executeSql(strSql);
				// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
				// 先提交避免死锁
				conn.commit();
				rs.beforeFirst();
				while (rs.next()) {
					dSecurityCost = operFun.getStorageCostTotal(
							YssFun.addDay(rs.getDate("FExRightDate"), -1), // 改为除权日前一天
																			// 20071028
																			// 胡昆
							rs.getString("FSecurityCode"), (analy1 ? rs
									.getString("FAnalysisCode1") : " "),
							(analy2 ? rs.getString("FAnalysisCode2") : " "),
							"", "", lAmount);
					bigSecurityAmount = new BigDecimal(Double.toString(lAmount
							.getDouble())); // QDV4嘉实基金20081107日02_D 需求 20081121
											// 王晓光
					if (bigSecurityAmount.compareTo(new BigDecimal(0.0)) > 0) {
						dRight = this.getSettingOper().reckonRoundMoney(
								rs.getString("FRoundCode") + "",
								YssD.mul(bigSecurityAmount, rs
										.getBigDecimal("FPreTaxRatio"))); // QDV4嘉实基金20081107日02_D
																			// 需求
																			// 20081121
																			// 王晓光

						// -------2009-05-26 蒋锦 添加 MS00006
						// QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化--------//
						// 表加锁，以免在多用户同时处理时出现交易编号重复
						// 将获取费用的代码上移，避免建表语句将事务提交
						fees = buildFeesStr(rs.getString("FSecurityCode"),
								strRightType, " ", (analy2 ? rs
										.getString("FAnalysisCode2") : " "),
								YssD.mul(dRight, rs.getDouble("FRIPrice")),
								dRight, dSecurityCost, rs.getDate("FPayDate")); // 来获得费用
																				// 插入到交易数据表里面

						dbl.lockTableInEXCLUSIVE(pub
								.yssGetTableName("Tb_Data_Trade"));
						// ----------------------------------------------------------------------//
						strMaxNum = "T"
								+ YssFun.formatDate(rs.getDate("FExRightDate"),
										"yyyyMMdd")
								+ dbFun
										.getNextInnerCode(
												pub
														.yssGetTableName("Tb_Data_Trade"),
												dbl.sqlRight("FNUM", 6),
												"000001",
												" where FNum like 'T"
														+ YssFun
																.formatDate(
																		rs
																				.getDate("FExRightDate"),
																		"yyyyMMdd")
														+ "%'");

						pst.setString(1, strMaxNum);
						pst.setString(2, rs.getString("FTSecurityCode"));
						pst.setString(3, (analy2 ? rs
								.getString("FAnalysisCode2") : " "));
						pst.setString(4, (analy1 ? rs
								.getString("FAnalysisCode1") : " "));
						pst.setString(5, strRightType);
						pst.setDate(6, rs.getDate("FExRightDate"));
						pst.setString(7, "00:00:00");
						pst.setDate(8, rs.getDate("FPayDate"));
						pst.setString(9, "00:00:00");
						pst.setInt(10, 1);
						pst.setDouble(11, dRight);
						pst.setDouble(12, rs.getDouble("FRIPrice"));
						dTradeMoney = YssFun.roundIt(YssD.mul(dRight, rs
								.getDouble("FRIPrice")), 2);
						pst.setDouble(13, dTradeMoney);
						pst.setString(14, " ");
						pst.setDouble(15, 0);
						pst.setDouble(16, 0);
						pst.setDouble(17, 0);

						sFee = this.operSql.buildSaveFeesSql(YssCons.OP_ADD,
								fees).split(",");
						for (int j = 0; j < sFee.length; j++) {
							pst.setString(18 + j, sFee[j].trim().replaceAll(
									"'", ""));
						}

						pst.setInt(34, 1);
						pst.setString(35, pub.getUserCode());
						pst.setString(36, YssFun
								.formatDatetime(new java.util.Date()));
						pst.setString(37, pub.getUserCode());
						pst.setString(38, YssFun
								.formatDatetime(new java.util.Date()));
						pst.executeUpdate();
						// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A
						// 多用户并发优化
						// 每次执行都提交，将锁释放
						conn.commit();
					}

					// 操作子表
					strSqlSub = "select a.*, b.*,c.FTradeCury,d.FPortCury from ( select FSecurityCode as FSecurityCode1,FTSecurityCode, FRecordDate,"
							+ " FEXRightDate, FExpirationDate, FPayDate, FRIPrice, FPreTaxRatio, FRoundCode from "
							+ pub.yssGetTableName("Tb_Data_RightsIssue")
							+ " where FEXRightDate between "
							+ dbl.sqlDate(this.strOperStartDate)
							+ " and "
							+ dbl.sqlDate(this.strOperEndDate)
							+ " and FTSecurityCode ="
							+ dbl.sqlString(rs.getString("FTSecurityCode"))
							+ // 添加配股证券做为查询条件 QDV4赢时胜（上海）2009年4月21日05_B MS00409
								// by leeyu 20090428
							" and FCheckState = 1) a join (select * from "
							+ pub.yssGetTableName("Tb_Stock_Security")
							+ " where FPortCode in ("
							+ operSql.sqlCodes(portCode)
							+ // fanghaoln 20100603 MS01161
								// QDV4赢时胜上海2010年05月05日05_B
							") and FSecurityCode = '"
							+ rs.getString("FSecurityCode")
							+ "' and FStorageDate = "
							+ dbl.sqlDate(rs.getDate("FStorageDate"))
							+ (analy1 ? " and FAnalysisCode1 = "
									+ dbl.sqlString(rs
											.getString("FAnalysisCode1")) : " ")
							+ (analy2 ? " and FAnalysisCode2 = "
									+ dbl.sqlString(rs
											.getString("FAnalysisCode2")) : " ")
							+ " and FYearMonth<>'"
							+ strYearMonth
							+ "' and FCheckState=1 and FSTORAGEAMOUNT>0 )b  on a.fsecuritycode1 = b.fsecuritycode and a.FExRightDate = "
							+ // edit by xxm,2010.01.18.加上库存数量大于0的条件 MS00952
								// 成分股调整日的第二天，库存数量为0的记录也会生成权益数据
							dbl.sqlDateAdd("b.FStorageDate", "+1")
							+
							// ---------------------------------------------------------
							" left join (select FSecurityCode, FTradeCury from "
							+ pub.yssGetTableName("Tb_Para_Security")
							+ " where FCheckState = 1) c on a.FTSecurityCode = c.FSecurityCode"
							+
							// ---------------------------------------------------------
							" left join (select FPortCode, FPortCury from "
							+ pub.yssGetTableName("Tb_Para_Portfolio")
							+ " where FCheckState = 1) d on b.FPortCode = d.FPortCode";

					rsSub = dbl.openResultSet(strSqlSub,
							ResultSet.TYPE_SCROLL_INSENSITIVE);
					if (rsSub.next()) {
						cashacc.setYssPub(pub);
						cashacc.setLinkParaAttr(analy1 ? rsSub
								.getString("FAnalysisCode1") : " ", rsSub
								.getString("FPortCode"), rsSub
								.getString("FTSecurityCode"), (analy2 ? rsSub
								.getString("FAnalysisCode2") : " "),
								strRightType, rsSub.getDate("FExRightDate"));

						strSqlSub = "delete from "
								+ pub.yssGetTableName("Tb_Data_SubTrade")
								+ " where "
								+
								// ---- MS00285 QDV4赢时胜（上海）2009年3月05日03_B
								// 加入分析代码的筛选条件 -------------
								(analy1 ? " FInvMgrCode = "
										+ dbl.sqlString(rsSub
												.getString("FAnalysisCode1"))
										+ " and " : " ")
								+ (analy2 ? " FBrokerCode = "
										+ dbl.sqlString(rsSub
												.getString("FAnalysisCode2"))
										+ " and " : " ")
								+
								// ----------------------------------------------------------------------------
								" FTradeTypeCode='"
								+ strRightType
								+ "' and FSecurityCode='"
								+ rsSub.getString("FTSecurityCode")
								+
								// xuqiji 20090601:QDV4赢时胜（上海）2009年5月5日01_B
								// MS00436 处理配股权益时在多组合、多分析代码的情况下会产生重复的业务资料
								// -------
								"' and FPortCode in ("
								+ operSql.sqlCodes(portCode)
								+ ") "
								+ // fanghaoln 20100603 MS01161
									// QDV4赢时胜上海2010年05月05日05_B
								" and FBargainDate between "
								+ dbl.sqlDate(this.strOperStartDate) + " and "
								+ dbl.sqlDate(this.strOperEndDate)
								+ " and FDataSource=0";
						dbl.executeSql(strSqlSub);

						rsSub.beforeFirst();
						while (rsSub.next()) {
							dSecurityCost = operFun.getStorageCost(YssFun
									.addDay(rs.getDate("FExRightDate"), -1),
									rsSub.getString("FSecurityCode"), rsSub
											.getString("FPortCode"),
									(analy1 ? rsSub.getString("FAnalysisCode1")
											: " "),
									(analy2 ? rsSub.getString("FAnalysisCode2")
											: " "), "", "", lAmount, rsSub
											.getString("FAttrClsCode"));
							bigSecurityAmount = new BigDecimal(Double
									.toString(lAmount.getDouble())); // QDV4嘉实基金20081107日02_D
																		// 需求
																		// 20081121
																		// 王晓光

							if (bigSecurityAmount
									.compareTo(new BigDecimal(0.0)) > 0) {
								dRightSub = this
										.getSettingOper()
										.reckonRoundMoney(
												rsSub.getString("FRoundCode")
														+ "",
												YssD
														.mul(
																bigSecurityAmount,
																rsSub
																		.getBigDecimal("FPreTaxRatio")));
								fees = buildFeesStr(rsSub
										.getString("FTSecurityCode"),
										strRightType, rsSub
												.getString("FPortCode"), rsSub
												.getString("FAnalysisCode2"),
										YssD.mul(dRightSub, rsSub
												.getDouble("FRIPrice")),
										dRightSub, dSecurityCost, rsSub
												.getDate("FPayDate")); // 来获得费用
																		// 插入到交易数据子表里面
								caBean = cashacc.getCashAccountBean();
								if (caBean != null) {
									strCashAccCode = caBean
											.getStrCashAcctCode();
								}

								// 2009-05-26 蒋锦 添加 MS00006
								// QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
								// 表加锁，以免在多用户同时处理时出现交易编号重复
								dbl.lockTableInEXCLUSIVE(pub
										.yssGetTableName("Tb_Data_SubTrade"));

								strMaxNumSub = strMaxNum
										+ dbFun
												.getNextInnerCode(
														pub
																.yssGetTableName("Tb_Data_SubTrade"),
														dbl.sqlRight("FNUM", 5),
														"00001",
														" where FNum like '"
																+ strMaxNum
																		.replaceAll(
																				"'",
																				"''")
																+ "%'");

								dBaseRate = this.getSettingOper().getCuryRate(
										rsSub.getDate("FExRightDate"),
										rsSub.getString("FTradeCury"),
										rsSub.getString("FPortCode"),
										YssOperCons.YSS_RATE_BASE);
								// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A
								// 20090415 --------------------------
								rateOper.getInnerPortRate(rsSub
										.getDate("FExRightDate"), rsSub
										.getString("FTradeCury"), rsSub
										.getString("FPortCode"));
								dPortRate = rateOper.getDPortRate();
								// -----------------------------------------------------------------------------------
								pstSub.setString(1, strMaxNumSub);
								pstSub.setString(2, rsSub
										.getString("FTSecurityCode"));
								pstSub.setString(3, rsSub
										.getString("FPortCode"));
								pstSub.setString(4, (analy2 ? rsSub
										.getString("FAnalysisCode2") : " "));
								pstSub.setString(5, (analy1 ? rsSub
										.getString("FAnalysisCode1") : " "));
								pstSub.setString(6, strRightType);
								pstSub.setString(7, strCashAccCode);
								pstSub.setDouble(8, 0);
								pstSub.setDouble(9, 0);
								pstSub.setDouble(10, 0);
								pstSub.setDate(11, rsSub
										.getDate("FExRightDate"));
								pstSub.setString(12, "00:00:00");
								pstSub.setDate(13, rsSub.getDate("FPayDate"));
								pstSub.setString(14, "00:00:00");
								pstSub.setInt(15, 1);

								pstSub.setDouble(16, dPortRate);
								pstSub.setDouble(17, dBaseRate);
								pstSub.setDouble(18, dRightSub);
								pstSub.setDouble(19, 0);
								pstSub.setDouble(20, 0);
								pstSub.setDouble(21, 0);

								sFee = this.operSql.buildSaveFeesSql(
										YssCons.OP_ADD, fees).split(",");
								for (int m = 0; m < sFee.length; m++) {
									pstSub.setString(22 + m, sFee[m].trim()
											.replaceAll("'", ""));
								}
								pstSub.setDouble(38, 0); // 核算成本
								pstSub.setDouble(39, 0); // 管理成本
								pstSub.setDouble(40, 0); // 估值成本

								pstSub.setDouble(41, 0); // 基础货币核算成本
								pstSub.setDouble(42, 0); // 基础货币管理成本
								pstSub.setDouble(43, 0); // 基础货币估值成本
								pstSub.setDouble(44, 0); // 组合货币核算成本
								pstSub.setDouble(45, 0); // 组合货币管理成本
								pstSub.setDouble(46, 0); // 组合货币估值成本
								pstSub.setDouble(47, 0); // 数据来源:自动
								pstSub.setInt(48, 1);
								pstSub.setString(49, pub.getUserCode());
								pstSub.setString(50, YssFun
										.formatDatetime(new java.util.Date()));
								pstSub.setString(51, pub.getUserCode());
								pstSub.setString(52, YssFun
										.formatDatetime(new java.util.Date()));
								pstSub.setDate(53, rsSub.getDate("FPayDate"));
								pstSub.setString(54, strCashAccCode); // 实际结算帐户
								pstSub.setDouble(55, 0); // 实际结算金额
								pstSub.setDouble(56, 1); // 兑换汇率
								// //////2008.04.30 蒋锦 修改 使用除权日的汇率代替原来前一天的汇率
								pstSub.setDouble(57, dBaseRate);
								pstSub.setDouble(58, dPortRate);
								// /////////////////////////////////////////////////////////
								pstSub.executeUpdate();
								// 2009-05-26 蒋锦 添加 MS00006
								// QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
								// 每次执行都提交将锁释放
								conn.commit();

								isHave = true;
							}
						}
						strDealInfo = "true";
					}
					dbl.closeResultSetFinal(rsSub);
				}
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);

			return isHave;
		} catch (Exception e) {
			strDealInfo = "false";
			throw new YssException("系统执行配股权益时出现异常！", e); // by 曹丞 2009.02.01
															// 配股权益异常信息
		} finally {
			dbl.closeResultSetFinal(rs, rsSub);
			dbl.closeStatementFinal(pst);
			dbl.closeStatementFinal(pstSub);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 分红的权益处理 分发派息 新建的 此次处理交易主表数据与交易子表数据同步的问题，实现派息业务逻辑上的统一 add by leeyu
	 * 20090424 QDV4中保2009年04月21日01_B MS00397
	 * 
	 * @throws YssException
	 */
	private boolean Dividend(String portCode) throws YssException {
		// 派息处理采用单个组合进行处理
		String strSql = "";
		String strSqlSub = "";
		ResultSet rs = null;
		ResultSet rsSub = null;
		BigDecimal bigSecurityAmount = null; // 证券数量
		double dSecurityCost = 0; // 证券成本
		double dRightSub = 0; // 权益（子表）
		String strRightType = ""; // 权益类型
		String strCashAccCode = " "; // 现金帐户
		String strYearMonth = "";
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pst = null;
//		PreparedStatement pstSub = null;
		YssPreparedStatement pst = null;
		YssPreparedStatement pstSub = null;
        //=============end====================
		CashAccountBean caBean = null;
		Connection conn = dbl.loadConnection();

		double dBaseRate = 1;
		double dPortRate = 1;

		boolean bTrans = false; // 代表是否开始了事务

		boolean[] analy = new boolean[3]; // 分析代码

		String curCalCuryCode = "";
		String sTradeMaxNum = "", sSubTradeMaxNum = ""; // 交易主表编号、交易子表编号
		String sTmpTradeMaxNum = "000001", sTmpSubTradeMaxNum = "000001"; // 保存当日交易主表与子表的最大编号
		int iSubTradeNum = 0; // 交易子表编号，从0开始
		String sTradeKey = ""; // 交易key
		HashMap hmTradeKey = new HashMap(); // 用于存放条件相同的一类交易数据编号

		// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090415 --
		EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
		rateOper.setYssPub(pub);
		// -----------------------------------------------------------

		boolean isHave = false; // 是否有分红
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub
					.getOperDealCtx().getBean("cashacclinkdeal");
			operFun.setYssPub(pub);
			strYearMonth = YssFun.left(this.strOperStartDate, 4) + "00";
			YssType lAmount = new YssType();

			// 操作交易主表,分红的数据不算做成本，所以不能插入到交易金额字段，插入到应计利息字段 胡昆 20071009
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Data_Trade")
					+ "(FNum, FSecurityCode, FBrokerCode, FInvMgrCode, FTradeTypeCode,FBargainDate, FBarGainTime,"
					+ "FSettleDate, FSettleTime,FAutoSettle,FTradeAmount, FTradePrice, FTradeMoney,"
					+ "FCashAccCode,FPortCuryRate,FBaseCuryRate,FAllotFactor,"
					+ " FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,FAccruedinterest,FAttrClsCode)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//			pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================

			// 操作交易子表
			strSqlSub = "insert into "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ "(FNum, FSecurityCode, FPortCode, FBrokerCode, FInvMgrCode, FTradeTypeCode, FCashAccCode, "
					+ " FAllotProportion, FOldAllotAmount, FAllotFactor, FBargainDate, FBarGainTime, FSettleDate, FSettleTime,"
					+ " FAutoSettle, FPortCuryRate, FBaseCuryRate, FTradeAmount, FTradePrice, FTradeMoney, FAccruedinterest,"
					+ " FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, FPortCuryCost,"
					+ " FMPortCuryCost, FVPortCuryCost, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,FTotalCost,FFactSettleDate,FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate,FAttrClsCode) "
					+ // 新增三个字段 by liyu 1128
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; // 彭彪20071120
																														// 增加写入实际结算日期

			//modified by liubo.Story #2145
			//==============================
//			pstSub = conn.prepareStatement(strSqlSub);
			pstSub = dbl.getYssPreparedStatement(strSqlSub);
			//==============end================

			analy[0] = operSql.storageAnalysis("FAnalysisCode1", "Security");
			analy[1] = operSql.storageAnalysis("FAnalysisCode2", "Security");

			strRightType = YssOperCons.YSS_JYLX_PX; // strRightType = "06"; 分发派息
			strDealInfo = "no"; // 标识当日无分红业务
			// 此条SQL语句用于判断当日是否有业务,有业务就删除相关联的表数据
			strSql = "select a.*,b.* from (select FSecurityCode as FSecurityCode1, FRecordDate, FDividendDate, FDistributeDate, FPreTaxRatio, FRoundCode,FDivdendType,FcuryCode from "
					+ // 这里把类型与币种也传进去 by leeyu
					pub.yssGetTableName("Tb_Data_Dividend")
					+ " where FDividendDate between "
					+ dbl.sqlDate(this.strOperStartDate)
					+ " and "
					+ dbl.sqlDate(this.strOperEndDate)
					+ " and FCheckState = 1) a join (select FSecurityCode,FStorageDate"
					+ (analy[0] ? ",FAnalysisCode1" : " ")
					+ (analy[1] ? ",FAnalysisCode2" : " ")
					+ " ,FAttrClsCode "
					+ " from "
					+ pub.yssGetTableName("Tb_Stock_Security")
					+ " where FYearMonth<>'"
					+ strYearMonth
					+ "' and FPortCode in ("
					+ operSql.sqlCodes(portCode)
					+ // fanghaoln 20100603 MS01161 QDV4赢时胜上海2010年05月05日05_B
					") and FCheckState=1 and FSTORAGEAMOUNT>0 group by FSecurityCode,FStorageDate,FAttrClsCode "
					+ // edit by xuxuming,加上库存数量大于0的条件 MS00952
						// 成分股调整日的第二天，库存数量为0的记录也会生成权益数据
					(analy[0] ? ",FAnalysisCode1" : " ")
					+ (analy[1] ? ",FAnalysisCode2" : " ")
					+ ") b"
					+ " on a.fsecuritycode1 = b.fsecuritycode and "
					+ dbl.sqlDateFiledSub("a.FDividendDate", "1")
					+ " = b.FStorageDate";
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				strDealInfo = "yes"; // 表明当日有业务

				// 删除交易主表的数据，条件：按交易类型删除数据
				strSql = "delete from " + pub.yssGetTableName("Tb_Data_Trade")
						+ " where FTradeTypeCode= "
						+ dbl.sqlString(strRightType)
						+ " and FBargainDate between "
						+ dbl.sqlDate(this.strOperStartDate) + " and "
						+ dbl.sqlDate(this.strOperEndDate); // 将日期段内的所有相关数据删除
															// 20080718。//当某日有多笔业务,则原来的方式只能删除一条证券交易.sj
															// edit 20080715
															// bug:0000302
				dbl.executeSql(strSql);

				// 删除资金调拨的数据 不能在下面删除，因为删除资金调拨需要用到业务资料的编号，不能在删除业务资料后处理
				delCashTransfer(YssOperCons.YSS_JYLX_PX, YssFun
						.toDate(strOperStartDate), YssFun
						.toDate(strOperEndDate),
				// fanghaoln 20100603 MS01161 QDV4赢时胜上海2010年05月05日05_B
						portCode); // 这里删除资金调拨 QDV4中保2009年04月21日01_B MS00397

				// 添加交易子表的删除条件 by leeyu 20090422 QDV4中保2009年04月21日01_B MS00397
				strSqlSub = "delete from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FTradeTypeCode='"
						+ strRightType
						+ "' and FPortCode in("
						+ operSql.sqlCodes(portCode)
						+ ")"
						+ // 添加上分红组合//fanghaoln 20100603 MS01161
							// QDV4赢时胜上海2010年05月05日05_B
						" and FBargainDate between "
						+ dbl.sqlDate(this.strOperStartDate) + " and "
						+ dbl.sqlDate(this.strOperEndDate)
						+ " and FDataSource=0";
				dbl.executeSql(strSqlSub);
			}

			// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
			// 先提交一次避免死锁
			conn.commit();

			// 操作子表 根据分红表与昨日证券库存表关联分红数据
			strSqlSub = "select a.*, b.*,c.FTradeCury,d.FPortCury from ( "
					+ "select FSecurityCode as FSecurityCode1, FRecordDate, FDividendDate,FDivdendType ,"
					+ "FDistributeDate, FPreTaxRatio, FRoundCode,FCuryCode as FDividendCuryCode from "
					+ pub.yssGetTableName("Tb_Data_Dividend")
					+ " where FDividendDate between "
					+ dbl.sqlDate(this.strOperStartDate)
					+ " and "
					+ dbl.sqlDate(this.strOperEndDate)
					+ " and FCheckState = 1 ) a join (select * from "
					+ pub.yssGetTableName("Tb_Stock_Security")
					+ " where FPortCode in ("
					+ operSql.sqlCodes(portCode)
					+ // fanghaoln 20100603 MS01161 QDV4赢时胜上海2010年05月05日05_B
					") and FStorageDate between "
					+ dbl.sqlDate(YssFun.addDay(YssFun
							.toDate(this.strOperStartDate), -1))
					+ " and "
					+ dbl.sqlDate(YssFun.addDay(YssFun
							.toDate(this.strOperEndDate), -1))
					+ " and FYearMonth<>'"
					+ strYearMonth
					+
					// 添加对分红日期和库存日期的比较，避免期间段内重复处理 邵宏伟 200806 BugNO:MS00551
					// QDV4中保2009年06月25日02_B
					"' and FCheckState=1 and FSTORAGEAMOUNT>0 )b  on a.fsecuritycode1 = b.fsecuritycode and "
					+ // edit by xuxuming,2010.01.18.加上库存数量大于0的条件
					dbl.sqlDateFiledSub("a.FDividendDate", "1")
					+ " = b.FStorageDate"
					+
					// =================================End
					// MS00551=======================================================
					" left join (select FSecurityCode,FTradeCury from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FCheckState = 1 ) c on a.FSecurityCode1 = c.FSecurityCode"
					+ " left join (select FPortCode,FPortCury from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FCheckState = 1 ) d on b.FPortCode = d.FPortCode"
					+ " order by FSecurityCode1 " + // 关取证券信息表和组合表，取出交易货币和组合货币。杨文奇20071017
					// ---- sj modified 20090722 暂时还没bug编号 MS00617
					// QDV4中保2009年08月05日01_B--------------------------------------------
					",FAnalysisCode1,FAnalysisCode2";// 添加排序的条件，以获取正确的编号。增加了分析代码1、2的排序条件
			// ------------------------------------------------------------------------------------

			rsSub = dbl.openResultSet(strSqlSub);
			cashacc.setYssPub(pub);
			String strSecAttrCls = "";// add by xuxuming,2010.01.15.保存所属分类代码
			while (rsSub.next()) {
				// ============MS00932 送股、分红权益处理时，生成业务资料属性分类没有做判断 add by
				// xuxuming,2010.01.15.送股时，先查询当天是否有指数调整信息。＝＝＝＝
				// ===============如果有调整，以调整之后的属性分类作为送股的属性分类＝＝＝＝＝＝＝＝＝＝＝＝＝＝
				String strSqlSec = "select * from "
						+ pub.yssGetTableName("Tb_Data_Integrated")
						+ " where FPortCode in ("
						+ operSql.sqlCodes(portCode)
						+ // fanghaoln 20100603 MS01161 QDV4赢时胜上海2010年05月05日05_B
						") and FSecurityCode='"
						+ rs.getString("FSecurityCode1")
						+ "' and FEXCHANGEDATE = "
						+ dbl.sqlDate(YssFun.addDay(rs.getDate("FStorageDate"),
								1))
						+ " and FTradeTypeCode='101' and FInOutType='1'";// 只查询流入的数据。有这只证券的流入，则已流入的所属分类作为送股的属性分类
				ResultSet rsSec = null;
				rsSec = dbl.openResultSet(strSqlSec);
				if (rsSec.next()) {// 一天内，一只证券最多只有一笔'成分股转换'类型的流入数据，故用IF
					strSecAttrCls = rsSec.getString("FAttrClsCode");
				}
				rsSec.close();
				// =========================end==========================================
				// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
				curCalCuryCode = rsSub.getString("FDividendCuryCode");
				cashacc.setLinkParaAttr((analy[0] ? rsSub
						.getString("FAnalysisCode1") : " "), rsSub
						.getString("FPortCode"), rsSub
						.getString("FSecurityCode"), (analy[1] ? rsSub
						.getString("FAnalysisCode2") : " "), strRightType,
						rsSub.getDate("FRecordDate"), curCalCuryCode,
						YssOperCons.YSS_JYLX_PX); // by leeyu 用于处理多组合的问题 080711
				caBean = cashacc.getCashAccountBean();
				if (caBean != null) {
					strCashAccCode = caBean.getStrCashAcctCode();
				} else { // MS00173 当分红处理时没有现金帐户时提示用户 by leeyu 2009-01-09
					throw new YssException("系统执行分红权益时出现异常！" + "\n" + "【"
							+ rsSub.getString("FSecurityCode")
							+ "】证券分红权益处理时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
				}

				// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
				// 为表加锁，以免在多用户同时处理时出现交易编号重复
				dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Trade"));
				dbl.lockTableInEXCLUSIVE(pub
						.yssGetTableName("Tb_Data_SubTrade"));

				// ==End 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A
				// 多用户并发优化
				sTradeMaxNum = "";
				sSubTradeMaxNum = "";
				// 查询同一类业务的交易数据，注：这里不能有组合代码作为Key
				// FSecurityCode,FDividendDate,FDistributeDate,FDividendCuryCode,FAnalySisCode1,FAnalySisCode2,FAttrClsCode,FDivdendType
				sTradeKey = rsSub.getString("FSecurityCode")
						+ "\t"
						+ // 证券代码
						rsSub.getString("FDividendCuryCode")
						+ "\t"
						+ // 分红币种
						YssFun.formatDate(rsSub.getDate("FDividendDate"))
						+ "\t"
						+ // 除权日
						YssFun.formatDate(rsSub.getDate("FDistributeDate"))
						+ "\t"
						+ // 派息日
						rsSub.getString("FAttrClsCode")
						+ "\t"
						+ // 所属分类
						(analy[0] ? rsSub.getString("FAnalySisCode1") : " ")
						+ "\t"
						+ // 分析代码1
						(analy[1] ? rsSub.getString("FAnalySisCode2") : " ")
						+ "\t" + // 分析代码2
						rsSub.getString("FDivdendType") + "\t" + // 分红类型
						strRightType; // 交易类型
				// 先产生交易表编号
				// 如果交易数据不是同一类数据就得重新取一次编号
				if (hmTradeKey.get(sTradeKey) == null) {
					// 取出当前子表的最大编号
					sTmpSubTradeMaxNum = dbFun.getNextInnerCode(pub
							.yssGetTableName("Tb_Data_SubTrade"), dbl.sqlRight(
							dbl.sqlLeft("FNum", 15), 6), "000001",
							" where FNum like 'T"
									+ YssFun.formatDate(rsSub
											.getDate("FDividendDate"),
											"yyyyMMdd") + "%'", 1);
					// 取出当前主表的最大编号
					sTmpTradeMaxNum = dbFun.getNextInnerCode(pub
							.yssGetTableName("Tb_Data_Trade"), dbl.sqlRight(
							"FNum", 6), "000001", " where FNum like 'T"
							+ YssFun.formatDate(rsSub.getDate("FDividendDate"),
									"yyyyMMdd") + "%'", 1);
					// 如果表中子表的最大编号比主表的最大编号大，则取子表的最大编号做为sTradeMaxNum的编号，相反则取主表的最大编号为sTradeMaxNum的编号
					if (YssFun.toInt(sTmpSubTradeMaxNum) >= YssFun
							.toInt(sTmpTradeMaxNum)) {
						sTradeMaxNum = "T"
								+ YssFun.formatDate(rsSub
										.getDate("FDividendDate"), "yyyyMMdd")
								+ sTmpSubTradeMaxNum;
					} else {
						sTradeMaxNum = "T"
								+ YssFun.formatDate(rsSub
										.getDate("FDividendDate"), "yyyyMMdd")
								+ sTmpTradeMaxNum;
					}
					// 同时计算一下本次交易子表的最大编号
					sSubTradeMaxNum = dbFun.getNextInnerCode(pub
							.yssGetTableName("Tb_Data_SubTrade"), dbl.sqlRight(
							"FNUM", 5), "00001", " where FNum like '"
							+ sTradeMaxNum + "%'");
					iSubTradeNum = YssFun.toInt(sSubTradeMaxNum);
					hmTradeKey.put(sTradeKey, sTradeMaxNum); // 将交易编号放入集合中
				} else {
					sTradeMaxNum = (String) hmTradeKey.get(sTradeKey);
				}
				sSubTradeMaxNum = sTradeMaxNum
						+ YssFun.formatNumber(iSubTradeNum, "00000");
				dSecurityCost = operFun.getStorageCost(
						// 股票A更名为B后（综合业务中换股）股票B分红权益处理无法生成业务资料
						// 将原先取的库存日改为除息日
						rsSub.getDate("FStorageDate"), rsSub
								.getString("FSecurityCode"), rsSub
								.getString("FPortCode"), (analy[0] ? rsSub
								.getString("FAnalysisCode1") : " "),
						(analy[1] ? rsSub.getString("FAnalysisCode2") : " "),
						"", "", lAmount, rsSub.getString("FAttrClsCode"));

				bigSecurityAmount = new BigDecimal(Double.toString(lAmount
						.getDouble())); // QDV4嘉实基金20081107日02_D 需求 20081121 王晓光
				if (bigSecurityAmount.compareTo(new BigDecimal(0.0)) > 0) {
					dRightSub = this.getSettingOper().reckonRoundMoney(
							rsSub.getString("FRoundCode") + "",
							YssD.mul(bigSecurityAmount, rsSub
									.getBigDecimal("FPreTaxRatio"))); // QDV4嘉实基金20081107日02_D
																		// 需求
																		// 20081121
																		// 王晓光

					dBaseRate = this.getSettingOper().getCuryRate(
							rs.getDate("FDividendDate"),
							rsSub.getString("FDividendCuryCode"), // 分红币种改为和国内一样处理,现去掉了通用参数中的分红处理设置节点
																	// MS00174
																	// ly
																	// 2009-01-12
							rsSub.getString("FPortCode"),
							YssOperCons.YSS_RATE_BASE);
					rateOper.getInnerPortRate(rs.getDate("FDividendDate"),
							rsSub.getString("FDividendCuryCode"), rsSub
									.getString("FPortCode"));
					dPortRate = rateOper.getDPortRate();
					pstSub.setString(1, sSubTradeMaxNum); // 交易编号
					pstSub.setString(2, rsSub.getString("FSecurityCode")); // 证券代码
					pstSub.setString(3, rsSub.getString("FPortCode")); // 组合代码
					pstSub.setString(4, analy[1] ? rsSub
							.getString("FAnalysisCode2") : " "); // 券商
					pstSub.setString(5, analy[0] ? rsSub
							.getString("FAnalysisCode1") : " "); // 投资经理
					pstSub.setString(6, strRightType); // 交易类型
					pstSub.setString(7, strCashAccCode); // 现金账户
					pstSub.setDouble(8, 0); // 分配比例
					pstSub.setDouble(9, 0); // 原始分配数量
					pstSub.setDouble(10, 0); // 分配因子
					pstSub.setDate(11, rsSub.getDate("FDividendDate")); // 成交日期
					pstSub.setString(12, "00:00:00"); // 成交时间
					pstSub.setDate(13, rsSub.getDate("FDistributeDate")); // 结算日期
					pstSub.setString(14, "00:00:00"); // 结算时间
					pstSub.setInt(15, 1); // 自动结算，1本次交易参与自动结算流程
					pstSub.setDouble(16, YssFun.roundIt(dPortRate, 15)); // 组合汇率
					pstSub.setDouble(17, YssFun.roundIt(dBaseRate, 15)); // 基础汇率
					pstSub.setDouble(18, 0); // 数量
					pstSub.setDouble(19, 0); // 成交价格
					pstSub.setDouble(20, 0); // 分红金额
					pstSub.setDouble(21, dRightSub); // 把分红金额放入应计利息字段 胡昆
														// 20071009
					pstSub.setDouble(22, 0); // 原币核算成本
					pstSub.setDouble(23, 0); // 原币管理成本
					pstSub.setDouble(24, 0); // 原币估值成本
					pstSub.setDouble(25, 0); // 基础货币核算成本
					pstSub.setDouble(26, 0); // 基础货币管理成本
					pstSub.setDouble(27, 0); // 基础货币估值成本
					pstSub.setDouble(28, 0); // 组合货币核算成本
					pstSub.setDouble(29, 0); // 组合货币管理成本
					pstSub.setDouble(30, 0); // 组合货币估值成本
					pstSub.setDouble(31, 0); // 数据来源
					pstSub.setInt(32, 1); // 审核状态
					pstSub.setString(33, pub.getUserCode());
					pstSub.setString(34, YssFun
							.formatDatetime(new java.util.Date()));
					pstSub.setString(35, pub.getUserCode());
					pstSub.setString(36, YssFun
							.formatDatetime(new java.util.Date()));
					pstSub.setDouble(37, dRightSub);
					pstSub.setDate(38, rsSub.getDate("FDistributeDate")); // 彭彪20071120
																			// 增加写入实际结算日期
					pstSub.setString(39, strCashAccCode); // 实际结算帐户
					pstSub.setDouble(40, dRightSub); // 实际结算金额
					pstSub.setDouble(41, 1); // 兑换汇率
					pstSub.setDouble(42, YssFun.roundIt(dBaseRate, 15)); // 实际计算基础汇率
					pstSub.setDouble(43, YssFun.roundIt(dPortRate, 15)); // 实际结算组合汇率
					// ===========MS00932 送股、分红权益处理时，生成业务资料属性分类没有做判断 add by
					// xuxuming,2010.01.15.当天有指数信息调整，以调整后的类型作为分红的类型，出凭证要用
					if (strSecAttrCls != null
							&& strSecAttrCls.trim().length() > 0) {
						pstSub.setString(44, strSecAttrCls);
					} else {
						pstSub.setString(44,
								rsSub.getString("FAttrClsCode") != null
										&& rsSub.getString("FAttrClsCode")
												.length() > 0 ? rsSub
										.getString("FAttrClsCode") : " "); // 所属分类代码
					}
					// ==================end===========================
					pstSub.executeUpdate();
					conn.commit();
					iSubTradeNum++; // 编号自加

					isHave = true;
				}
			} // end while
			// close the resultset
			dbl.closeResultSetFinal(rsSub);

			// 取出子表所有关联分红的数据合计一下插入到主表中
			strSql = "select FSecurityCode,FBargainDate,FSettleDate,FBrokerCode,FInvMgrCode,FAttrClsCode,FTradeTypeCode,"
					+ dbl.sqlLeft("FNum", 15)
					+ " as FNum,"
					+ " sum(FAccruedinterest) as FAccruedinterest from "
					+ pub.yssGetTableName("TB_Data_SubTrade")
					+ " where FBargainDate between "
					+ dbl.sqlDate(this.strOperStartDate)
					+ " and "
					+ dbl.sqlDate(this.strOperEndDate)
					+ " and FDataSource = 0 and FCheckState = 1 and FTradeTypeCode= "
					+ dbl.sqlString(strRightType)
					+
					// fanghaoln 20100603 MS01161 QDV4赢时胜上海2010年05月05日05_B
					" and FPortCode in("
					+ operSql.sqlCodes(portCode)
					+ ")"
					+ // QDV4中保2009年08月05日01_B MS00617 by leeyu 20090806
						// 当无当前组合的权益处理时插入到业务数据表数据重复的问题
					" group by FSecurityCode,FBargainDate,FSettleDate,FBrokerCode,FInvMgrCode,FAttrClsCode,FTradeTypeCode,"
					+ dbl.sqlLeft("FNum", 15) + " order by FNum ";

			// 因为rs在上面有使用，因此使用前先进行关闭
			dbl.closeResultSetFinal(rs); // 关闭游标 modify by sunkey 20090602
											// MS00472:QDV4上海2009年6月02日01_B
			// =====add by
			// xuxuming,20090915.MS00697,手工新建分红数据，做当天权益处理时报错,QDV4银华2009年09月11日01_B===============
			HashMap hmTradNum = new HashMap(); // 用于保存交易主表中当天的交易编号
			String strTradNum = ""; // 查询当天主表中交易编号的SQL语句
			strTradNum = "select FNum from "
					+ pub.yssGetTableName("Tb_Data_Trade")
					+ " where FTradeTypeCode= " + dbl.sqlString(strRightType)
					+ " and FBargainDate between "
					+ dbl.sqlDate(this.strOperStartDate) + " and "
					+ dbl.sqlDate(this.strOperEndDate); // 将日期段内的交易编号
			rs = dbl.openResultSet(strTradNum);
			while (rs.next()) {
				String strTempFNum = "";// 交易编号
				strTempFNum = rs.getString("FNum");
				hmTradNum.put(strTempFNum, strTempFNum);// key 和 Value都 是交易编号
			}
			dbl.closeResultSetFinal(rs);// 关闭游标
			// ======end====================================================================================================

			rs = dbl.openResultSet(strSql);
			// 为表加锁，以免在多用户同时处理时出现交易编号重复
			// 2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Trade"));
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_SubTrade"));
			while (rs.next()) {
				// =====add by
				// xuxuming,20090915.MS00697,手工新建分红数据，做当天权益处理时报错,QDV4银华2009年09月11日01_B===
				if (hmTradNum.containsKey(rs.getString("FNum"))) {// 交易主表中已经存在该编号的数据，则不用添加
					continue;
				}
				// =====end===========================================================================================
				pst.setString(1, rs.getString("FNum")); // 编号
				pst.setString(2, rs.getString("FSecurityCode")); // 证券代码
				pst.setString(3, rs.getString("FBrokerCode")); // 券商
				pst.setString(4, rs.getString("FInvMgrCode")); // 投资经理
				pst.setString(5, strRightType); // 交易方式
				pst.setDate(6, rs.getDate("FBargainDate")); // 成交日期
				pst.setString(7, "00:00:00"); // 成交时间
				pst.setDate(8, rs.getDate("FSettleDate")); // 结算日期
				pst.setString(9, "00:00:00"); // 结算时间
				pst.setInt(10, 1); // 自动结算
				pst.setDouble(11, 0); // 交易数量
				pst.setDouble(12, 0); // 交易价格
				pst.setDouble(13, 0); // 交易金额
				pst.setString(14, " "); // 现金账户
				pst.setDouble(15, 0); // 组合汇率
				pst.setDouble(16, 0); // 基础汇率
				pst.setDouble(17, 0); // 分配因子
				pst.setInt(18, 1); // 审核状态
				pst.setString(19, pub.getUserCode());
				pst.setString(20, YssFun.formatDatetime(new java.util.Date()));
				pst.setString(21, pub.getUserCode());
				pst.setString(22, YssFun.formatDatetime(new java.util.Date()));
				pst.setDouble(23, rs.getDouble("FAccruedinterest")); // 应计利息
				// ===========MS00932 送股、分红权益处理时，生成业务资料属性分类没有做判断 add by
				// xuxuming,2010.01.15.当天有指数信息调整，以调整后的类型作为分红的类型，出凭证要用
				if (strSecAttrCls != null && strSecAttrCls.trim().length() > 0) {
					pst.setString(24, strSecAttrCls);
				} else {
					pst
							.setString(
									24,
									(rs.getString("FAttrClsCode") != null && rs
											.getString("FAttrClsCode").length() > 0) ? rs
											.getString("FAttrClsCode")
											: " "); // 所属分类代码
				}
				// =============end============================
				pst.addBatch();
			}
			pst.executeBatch(); // execute the batch
			dbl.closeResultSetFinal(rs); // close the resultset
			conn.commit(); // commit the transaction
			bTrans = false; // set rollback off
			conn.setAutoCommit(true); // open autocommit

			return isHave;
		} catch (Exception e) {
			strDealInfo = "false";
			sbDealInfo.append("\r\n股票分红权益处理失败！"); // MS00001
													// QDV4赢时胜（上海）2009年4月20日01_A
													// 2009-05-13 panjunfang add
			throw new YssException("系统执行分红权益时出现异常!" + "\n", e); // by 曹丞
																// 2009.02.01
																// 分红权益异常信息
																// MS00004
																// QDV4.1-2009.2.1_09A
		} finally {
			dbl.closeResultSetFinal(rsSub, rs);
			dbl.closeStatementFinal(pst, pstSub);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	private double getFeeMoney(String sSecCode, String sTradeType,
			String sPortCode, String sBrokerCode, double dMoney,
			java.util.Date dDate) throws YssException {
		BaseOperDeal baseOper = null;
		BaseFeeDeal feeOper = null;
		ArrayList alFeeBeans = null;
		FeeBean fee = null;
		double dResult = 0;
		double dFee = 0;
		try {
			baseOper = this.getSettingOper();
			feeOper = (BaseFeeDeal) pub.getOperDealCtx().getBean("feedeal");
			baseOper.setYssPub(pub);
			feeOper.setYssPub(pub);
			feeOper.setFeeAttr(sSecCode, sTradeType, sPortCode, sBrokerCode,
					dMoney);
			alFeeBeans = feeOper.getFeeBeans();
			for (int i = 0; i < alFeeBeans.size(); i++) {
				fee = (FeeBean) alFeeBeans.get(i);
				dFee = baseOper.calMoneyByPerExp(fee.getPerExpCode(), fee
						.getRoundCode(), dMoney, dDate);
				dResult = YssD.add(dResult, dFee);
			}
			return dResult;
		} catch (Exception e) {
			throw new YssException(e);
		}

	}

	/**
	 * buildRowStr
	 * 
	 * return String
	 */
	public String buildRowStr() {
		return "";
	}

	/**
	 * checkInput
	 * 
	 * @param btOper
	 *            byte
	 */
	public void checkInput(byte btOper) {
	}

	/**
	 * checkOperData
	 */
	public void checkSetting() {
	}

	/**
	 * delOperData
	 */
	public void delSetting() {
	}

	/**
	 * editOperData
	 * 
	 * @return String
	 */
	public String editSetting() {
		return "";
	}

	/**
	 * getListViewData1
	 * 
	 * @return String
	 */
	public String getListViewData1() throws YssException {
		return this.strDealInfo;
	}

	/**
	 * getListViewData2
	 * 
	 * @return String
	 */
	public String getListViewData2() throws YssException {
		String strSql = "";
		return "";
	}

	/**
	 * getListViewData3
	 * 
	 * @return String
	 */
	public String getListViewData3() {
		return "";
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

	/**
	 * parseRowStr 解析请求信息
	 * 
	 * @param sRowStr
	 *            String
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		try {
			if (sRowStr.trim().length() == 0) {
				return;
			}
			reqAry = sRowStr.split("\t");
			this.strOperStartDate = reqAry[0];
			this.strOperEndDate = reqAry[1];
			this.strPortCode = reqAry[2];
			this.assetGroupCode = reqAry[3]; // BugNO :MS00001
												// QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
												// fanghaoln 20090603
			this.strOperType = reqAry[4];
			super.parseRecLog();
		} catch (Exception e) {
			throw new YssException("解析业务处理请求信息出错!\n", e);
		}
	}

	/**
	 * saveMutliOperData
	 * 
	 * @param sMutilRowStr
	 *            String
	 * @return String
	 */
	public String saveMutliOperData(String sMutilRowStr) {
		return "";
	}

	public String getPortSql() {
		String strReturn = "";
		if (this.strPortCode.equals("*")) {
			strReturn = "select Fportcode from "
					+ pub.yssGetTableName("tb_para_portfolio")
					+ " where Fassetgroupcode='" + pub.getAssetGroupCode()
					+ "'";
		} else {
			strReturn = dbl.sqlString(currPortCode);
		}
		return strReturn;
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException,
			YssException {

	}

	// / <summary>
	// / 修改人：fanghaoln
	// / 修改人时间:20090512
	// / BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
	// / 从后台加载出我们跨组合群的内容
	// / <summary>
	public String getOperValue(String sType) throws YssException {
		String sAllGroup = ""; // 定义一个字符用来保存执行后的结果传到前台
		String sPrefixTB = pub.getPrefixTB(); // 保存当前的组合群代码

		String[] assetGroupCodes = this.assetGroupCode
				.split(YssCons.YSS_GROUPSPLITMARK); // 按组合群的解析符解析组合群代码
		String[] strPortCodes = this.strPortCode
				.split(YssCons.YSS_GROUPSPLITMARK); // 按组合群的解析符解析组合代码
		try {
			for (int i = 0; i < assetGroupCodes.length; i++) { // 循环遍历每一个组合群
				this.assetGroupCode = assetGroupCodes[i]; // 得到一个组合群代码
				pub.setPrefixTB(this.assetGroupCode); // 修改公共变量的当前组合群代码
				this.strPortCode = strPortCodes[i]; // 得到一个组合群下的组合代码
				sAllGroup = sAllGroup + this.addSetting(); // 调用以前的执行方法
				sbDealInfo.delete(0, sbDealInfo.length()); // 要把StringBuffer清一下，要不然结果会累加
			}
			return sAllGroup; // 把结果返回到前台进行显示
		} catch (Exception e) {
			throw new YssException("权益处理失败！", e);
		} finally {
			pub.setPrefixTB(sPrefixTB); // 还原公共变的里的组合群代码
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
	public IDataSetting getSetting() {
		return null;
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
	 * getBeforeEditData
	 * 
	 * @return String
	 */
	public String getBeforeEditData() {
		return "";
	}

	/**
	 * deleteRecycleData
	 */
	public void deleteRecycleData() {
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

	// MS01336 add by zhangfa 201000803 MS01336
	/**
	 * 检测是否存在通过接口导入相关的交易数据
	 */
	public boolean checkRightTrade(TradeSubBean tradesub) throws YssException {
		boolean flag = false;
		String strSql = "";
		ResultSet rs = null;
		try {

			strSql = "select fsecuritycode,fsettledate,fjkdr from  "
					+ pub.yssGetTableName("tb_data_subtrade")
					+ "  where  fsecuritycode="
					+ dbl.sqlString(tradesub.getSecurityCode())
					+ " and fbargaindate="
					+ dbl.sqlDate(tradesub.getBargainDate())
					+ " and ftradetypecode="
					+ dbl.sqlString(tradesub.getTradeCode())
					+ "  and FJKDR ='1'  ";
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			throw new YssException("存储配股权益处理出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return flag;

	}
	// ------------------------------------------------------------
}
