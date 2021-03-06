package com.yss.manager;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.operdata.*;
import com.yss.main.orderadmin.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

public class TradeDataAdmin extends BaseBean {
	private ArrayList bufList = new ArrayList();
	private Hashtable htTrade = new Hashtable();

	public TradeDataAdmin() {
	}

	public void addList(TradeSubBean tradeSub) {
		this.bufList.add(tradeSub);
	}

	public void addAll(ArrayList list) {
		this.bufList = list;
	}

	/**
	 * xuqiji 20090812 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理
	 * 
	 * @param sNums
	 *            String 编号
	 * @param beginTradeDate
	 *            Date 开始日期
	 * @param endTradeDate
	 *            Date 截止日期
	 * @param beginSettleDate
	 *            Date 开始结算日期
	 * @param endSettleDate
	 *            Date 截止结算日期
	 * @param sSecurityCode
	 *            String 证券代码
	 * @param sPortCode
	 *            String 组合代码
	 * @param sInvMgrCode
	 *            String 投资经理
	 * @param sBrokerCode
	 *            String 券商
	 * @param sTradeType
	 *            String 交易类型
	 * @param sCashAccCode
	 *            String 现金账户
	 * @param sAttrClsCode
	 *            String 投资类型
	 * @param sDsType
	 *            String 操作类型 界面上输入 其他业务资料数据用'HD_JK' FDataSouce=0，权益--'HD_QY'
	 *            ；接口：读入'ZD_JK' FDataSource=1 ，权益处理 'ZD_QY' FDataSource=1
	 * @return String
	 */
	private String buildWhereSql(String sNums, java.util.Date beginTradeDate,
			java.util.Date endTradeDate, java.util.Date beginSettleDate,
			java.util.Date endSettleDate, String sSecurityCode,
			String sPortCode, String sInvMgrCode, String sBrokerCode,
			String sTradeType, String sCashAccCode, String sAttrClsCode,
			String sDsType) {
		String sResult = " where 1=1 ";
		// 此方法sql语句拼接错误，xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015
		// 国内权益处理---
		if (sNums.length() > 0) {
			sResult += " and a.FNum in(" + operSql.sqlCodes(sNums) + ")";
		}
		if (beginTradeDate != null && endTradeDate == null
				&& beginSettleDate == null && endSettleDate == null) {
			sResult += " and a.FBargainDate = " + dbl.sqlDate(beginTradeDate);
		}
		if (beginTradeDate != null && endTradeDate != null) {
			sResult += " and a.FBargainDate between "
					+ dbl.sqlDate(beginTradeDate) + " and "
					+ dbl.sqlDate(endTradeDate);
		}
		if (beginSettleDate != null && endSettleDate != null) {
			sResult += " and a.FSettleDate  between "
					+ dbl.sqlDate(beginSettleDate) + " and "
					+ dbl.sqlDate(endSettleDate);
		}
		if (sSecurityCode.length() > 0) {
			sResult += " and a.FSecurityCode in("
					+ this.operSql.sqlCodes(sSecurityCode) + ")";
		}
		if (sCashAccCode.length() > 0) {
			sResult += " and a.FCashAccCode = " + dbl.sqlString(sCashAccCode);
		}
		if (sPortCode.length() > 0) {
			sResult += " and a.FPortCode in( "
					+ this.operSql.sqlCodes(sPortCode) + ")";// xuqiji 20100429
																// MS01134
																// 在现有的程序版本中增加指数期权及股票期权业务
		}
		if (sInvMgrCode.length() > 0) {
			sResult += " and a.FInvMgrCode = " + dbl.sqlString(sInvMgrCode);
		}
		if (sBrokerCode.length() > 0) {
			sResult += " and a.FBrokerCode = " + dbl.sqlString(sBrokerCode);
		}
		if (sTradeType.length() > 0) {
			sResult += " and a.FTradeTypeCode in( "
					+ this.operSql.sqlCodes(sTradeType) + ")";// xuqiji 20100429
																// MS01134
																// 在现有的程序版本中增加指数期权及股票期权业务
		}
		if (sAttrClsCode.length() > 0) {
			sResult += " and a.FAttrClsCode = " + dbl.sqlString(sAttrClsCode);
		}
		if (sDsType.length() > 0) {
			//modify by nimengjing 2011.2.17  BUG #1117权益处理时，系统把手工录入的分红数据给删除
			if ("HD_QY".equalsIgnoreCase(sDsType)) {
				sResult += " and a.FDs = " + dbl.sqlString(sDsType);//如果为权益处理时 FDs的值为HD_QY
			} else {
				sResult += " and (a.FDs = " + dbl.sqlString(sDsType)
						+ " or a.FDs is null)";
			}
			//end bug BUG #1117-------------------------------------------------
		}
		// -----------------------------end------------------------------------//
		return sResult;
	}

	/**
	 * 重载删除方法
	 * 
	 * @param beginTradeDate
	 *            Date 业务日期
	 * @param sSecurityCode
	 *            String 证券代码
	 * @param sPortCode
	 *            String 组合代码
	 * @param sInvMgrCode
	 *            String 投资经理
	 * @param sBrokerCode
	 *            String 券商
	 * @param sTradeType
	 *            String 交易类型
	 * @param needNums
	 *            boolean 是不是通过编号
	 * @throws YssException
	 *             异常 xuqiji 20090729 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理
	 */
	public void delete(java.util.Date beginTradeDate, String sSecurityCode,
			String sPortCode, String sInvMgrCode, String sBrokerCode,
			String sTradeType, boolean needNums, String sDsType)
			throws YssException {
		delete("", beginTradeDate, null, null, null, sSecurityCode, sPortCode,
				sInvMgrCode, sBrokerCode, sTradeType, "", "", needNums, sDsType);
	}

	public void delete(java.util.Date beginTradeDate, String sSecurityCode,
			String sInvMgrCode, String sBrokerCode, String sTradeType)
			throws YssException {
		delete("", beginTradeDate, null, null, null, sSecurityCode, "",
				sInvMgrCode, sBrokerCode, sTradeType, "", "", true);
	}

	public void delete(String sNums, java.util.Date beginTradeDate,
			String sSecurityCode, String sInvMgrCode, String sBrokerCode,
			String sTradeType) throws YssException {
		delete(sNums, beginTradeDate, null, null, null, sSecurityCode, "",
				sInvMgrCode, sBrokerCode, sTradeType, "", "", true);
	}

	public void delete(String sNums) throws YssException {
		delete(sNums, null, null, null, null, "", "", "", "", "", "", "", true);
	}

	/**
	 * 删除数据方法
	 * 
	 * @param sNums
	 *            String
	 * @param beginTradeDate
	 *            Date
	 * @param endTradeDate
	 *            Date
	 * @param beginSettleDate
	 *            Date
	 * @param endSettleDate
	 *            Date
	 * @param sSecurityCode
	 *            String
	 * @param sPortCode
	 *            String
	 * @param sInvMgrCode
	 *            String
	 * @param sBrokerCode
	 *            String
	 * @param sTradeType
	 *            String
	 * @param sCashAccCode
	 *            String
	 * @param sAttrClsCode
	 *            String
	 * @param needNums
	 *            boolean
	 * @param sDsType
	 *            String
	 * @throws YssException
	 *             xuqiji 20090824 删除条件错误 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015
	 *             国内权益处理
	 */
	public void delete(String sNums, java.util.Date beginTradeDate,
			java.util.Date endTradeDate, java.util.Date beginSettleDate,
			java.util.Date endSettleDate, String sSecurityCode,
			String sPortCode, String sInvMgrCode, String sBrokerCode,
			String sTradeType, String sCashAccCode, String sAttrClsCode,
			boolean needNums, String sDsType) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String sWhereSql = "";
		// MS01609 QDV4赢时胜(测试)2010年08月13日02_B add by jiangshichao 2010.09.17--
		String sWhereSql1 = "";
		String strSql1 = "";
		// MS01609 QDV4赢时胜(测试)2010年08月13日02_B end -----------------------------
		String[] sSubNums = null;
		String nums = "";
		String NumSub = "";
		try {
			sWhereSql = this.buildWhereSql(sNums, beginTradeDate, endTradeDate,
					beginSettleDate, endSettleDate, sSecurityCode, sPortCode,
					sInvMgrCode, sBrokerCode, sTradeType, sCashAccCode,
					sAttrClsCode, sDsType);

			if (sWhereSql.trim().length() == 0
					|| sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
				return;
			}
			if (needNums) {
				if (sNums.length() > 0) {
					sSubNums = sNums.split(",");
				}
				//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
	            if(sSubNums != null){
					for (int i = 0; i < sSubNums.length; i++) {
						strSql = "select distinct FNum from "
								+ pub.yssGetTableName("Tb_Data_Trade")
								+ " where FNum like '"
								+ sSubNums[i]
										.substring(0, sSubNums[i].length() - 5)
								+ "%'";
						rs = dbl.openResultSet(strSql);
						while (rs.next()) {
							nums += rs.getString("FNum") + ",";
						}
						dbl.closeResultSetFinal(rs);
					}
	            }
	            //---end---
				if (nums.trim().length() > 0) {
					nums = operSql.sqlCodes(nums);
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Data_Trade")
							+ " where FNum in (" + nums + ")" +
							// MS01354 add by zhangfa 20100719
							// QDV4赢时胜(上海)2010年06月25日01_A
							" and (FJKDR is null or FJKDR !='" + "1' )";
					// ------------------------------------------------------------------------
					dbl.executeSql(strSql);

				}
			} else {
				strSql = "select a.FNum as FZNum,b.FNum as FSNum from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " a left join (select * from "
						+ pub.yssGetTableName("Tb_Data_Trade")
						+ ") b on a.FSecurityCode = b.FSecurityCode  and a.FBrokerCode = b.FBrokerCode"
						+ " and a.FInvMgrCode = b.FInvMgrCode and a.FBargainDate = b.FBargainDate and a.ftradetypecode =b.ftradetypecode"
						+ // 去掉现金账户条件，增加交易方式条件，因为交易主表的现金账户可以为空
						sWhereSql;
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					nums += rs.getString("FZNum") + ",";
					// NumSub += rs.getString("FSNum") + ",";
				}
				dbl.closeResultSetFinal(rs);
				nums = operSql.sqlCodes(nums);
				// xuqiji 20090721 删除条件错误 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015
				// 国内权益处理---
				strSql = "delete from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FNum in (" + nums + ")" +
						// MS01354 add by zhangfa 20100719
						// QDV4赢时胜(上海)2010年06月25日01_A
						" and (FJKDR is null or FJKDR !='" + "1' )";
				// ------------------------------------------------------------------------

				// MS01609 QDV4赢时胜(测试)2010年08月13日02_B add by jiangshichao
				// 2010.09.17 --------------------------
				// 查询交易主表的交易编号时，不用组合代码做为过滤条件
				sWhereSql1 = this.buildWhereSql(sNums, beginTradeDate,
						endTradeDate, beginSettleDate, endSettleDate,
						sSecurityCode, "", sInvMgrCode, sBrokerCode,
						sTradeType, sCashAccCode, sAttrClsCode, sDsType);

				if (sWhereSql1.trim().length() == 0
						|| sWhereSql1.trim().equalsIgnoreCase("where 1=1")) {
					return;
				}

				strSql1 = "select a.FNum as FZNum,b.FNum as FSNum from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " a left join (select * from "
						+ pub.yssGetTableName("Tb_Data_Trade")
						+ ") b on a.FSecurityCode = b.FSecurityCode  and a.FBrokerCode = b.FBrokerCode"
						+ " and a.FInvMgrCode = b.FInvMgrCode and a.FBargainDate = b.FBargainDate and a.ftradetypecode =b.ftradetypecode"
						+ // 去掉现金账户条件，增加交易方式条件，因为交易主表的现金账户可以为空
						sWhereSql1;
				rs = dbl.openResultSet(strSql1);
				while (rs.next()) {
					NumSub += rs.getString("FSNum") + ",";
				}
				NumSub = operSql.sqlCodes(NumSub);

				// MS01609 QDV4赢时胜(测试)2010年08月13日02_B end
				// --------------------------------------------------------

				strSql1 = "delete from " + pub.yssGetTableName("Tb_Data_Trade")
						+ " where FNum in (" + NumSub + ")" +
						// MS01354 add by zhangfa 20100719
						// QDV4赢时胜(上海)2010年06月25日01_A
						" and (FJKDR is null or FJKDR !='" + "1' )";
				// ------------------------------------------end--------------------------------//
				dbl.executeSql(strSql);
				dbl.executeSql(strSql1);
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 重载删除方法
	 * 
	 * @param sNums
	 *            String 编号
	 * @param beginTradeDate
	 *            Date 开始交易日期
	 * @param endTradeDate
	 *            Date 截止交易日期
	 * @param beginSettleDate
	 *            Date 开始结算日期
	 * @param endSettleDate
	 *            Date 截止结算日期
	 * @param sSecurityCode
	 *            String 证券代码
	 * @param sPortCode
	 *            String 组合代码
	 * @param sInvMgrCode
	 *            String 投资经理
	 * @param sBrokerCode
	 *            String 券商
	 * @param sTradeType
	 *            String 交易类型
	 * @param sCashAccCode
	 *            String 现金账户
	 * @param sAttrClsCode
	 *            String 投资分类
	 * @param needNums
	 *            boolean 需要编号
	 * @throws YssException
	 *             xuqiji 20090824 删除条件错误 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015
	 *             国内权益处理
	 */
	public void delete(String sNums, java.util.Date beginTradeDate,
			java.util.Date endTradeDate, java.util.Date beginSettleDate,
			java.util.Date endSettleDate, String sSecurityCode,
			String sPortCode, String sInvMgrCode, String sBrokerCode,
			String sTradeType, String sCashAccCode, String sAttrClsCode,
			boolean needNums) throws YssException {
		delete(sNums, beginTradeDate, endTradeDate, beginSettleDate,
				endSettleDate, sSecurityCode, sPortCode, sInvMgrCode,
				sBrokerCode, sTradeType, sCashAccCode, sAttrClsCode, needNums,
				"");
	}

	public void insert(String sNums) throws YssException {
		insert(sNums, null, null, null, null, "", "", "", "", "", "", "", true,
				true);
	}

	public void insert(java.util.Date beginTradeDate,
			java.util.Date endTradeDate, String sPortCode, String sTradeType)
			throws YssException {
		insert("", beginTradeDate, endTradeDate, null, null, "", sPortCode, "",
				"", sTradeType, "", "", true, false);
	}

	/**
	 * 重载插入数据方法
	 * 
	 * @param beginTradeDate
	 *            Date 开始成交日期
	 * @param endTradeDate
	 *            Date 截止成交日期
	 * @param sSecurityCode
	 *            String 证券代码
	 * @param sPortCode
	 *            String 组合代码
	 * @param sTradeType
	 *            String 交易类型
	 * @param sDsType
	 *            操作类型
	 * @throws YssException
	 *             xuqiji 20090819 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015 国内权益处理
	 */
	public void insert(java.util.Date beginTradeDate,
			java.util.Date endTradeDate, String sPortCode, String sTradeType,
			String sDsType) throws YssException {
		insert("", beginTradeDate, endTradeDate, null, null, "", sPortCode, "",
				"", sTradeType, "", "", true, false, sDsType);
	}

	public void insert(String sNums, java.util.Date beginTradeDate,
			java.util.Date endTradeDate, java.util.Date beginSettleDate,
			java.util.Date endSettleDate, String sSecurityCode,
			String sPortCode, String sInvMgrCode, String sBrokerCode,
			String sTradeType, String sCashAccCode, String sAttrClsCode,
			boolean bAutoDel, boolean needNums, String sDsType)
			throws YssException {
		String strSql = "";
		int i = 0;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
//		PreparedStatement pstSub = null;
        YssPreparedStatement yssPst = null;
        YssPreparedStatement yssPstSub = null;
        //===============end==================
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String sFNum = "";
		String sTmpNum = "";
		String sFees = "";
		String[] sFee = null;
		TradeSubBean tradesub = null;
		TradeBean trade = null;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			if (bAutoDel) {
				delete(sNums, beginTradeDate, endTradeDate, beginSettleDate,
						endSettleDate, sSecurityCode, sPortCode, sInvMgrCode,
						sBrokerCode, sTradeType, sCashAccCode, sAttrClsCode,
						needNums, sDsType);
			}
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " (FNum,FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,FAttrClsCode,FBargainDate"
					+ ",FBargainTime,FSettleDate,FSettleTime,FMatureDate,FMatureSettleDate"
					+ // 2007.11.16 添加 蒋锦 添加字段 FMatureDate,FMatureSettleDate
					",FAutoSettle,FPortCuryRate,FBaseCuryRate,FAllotProportion,FOldAllotAmount,FAllotFactor"
					+ ",FTradeAmount,FTradePrice,FTradeMoney,FAccruedinterest,FBailMoney,FFeeCode1,FTradeFee1,FFeeCode2,FTradeFee2"
					+ ",FFeeCode3,FTradeFee3,FFeeCode4,FTradeFee4,FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6,FFeeCode7,FTradeFee7"
					+ ",FFeeCode8,FTradeFee8,FTotalCost,FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryCost"
					+ ",FMPortCuryCost,FVPortCuryCost,FSettleState,FOrderNum,FDATASOURCE,FDesc"
					+
					// 新增字段汇率日期 xuqiji 20090805 QDV4.1赢时胜（上海）2009年4月20日15_A
					// MS00015 国内权益处理
					",FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate,FRateDate,FFactSettleDate,FDs,"					
					// 新增字段 by liyu 1128
					// 储存字段FDataBirth的值 add by songjie 2012.3.26 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A
					+" FRecordDate,FDivdendType,fdealnum, FDataBirth) "+//by guyichuan 20110514 STORY #741 QDV4富国基金2011年3月2日01_A  story 1574 by zhouwei 20111109 fdealnum
					" values(?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?,?,?,?"
					+ ",?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?,?,?"
					+ ",?,?,?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?,?,?"
					+ ",?,?,?,?,?,?,?,?,?,?,?,?)";// 新增字段汇率日期 xuqiji 20090805
											// QDV4.1赢时胜（上海）2009年4月20日15_A
											// MS00015 国内权益处理--
//			pstSub = conn.prepareStatement(strSql);
			yssPstSub = dbl.getYssPreparedStatement(strSql);

			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Data_Trade")
					+ " (FNum,FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,FAttrClsCode,FBargainDate"
					+ " ,FBargainTime,FSettleDate,FSettleTime,FAutoSettle,FPortCuryRate,FBaseCuryRate,FAllotFactor,FTradeAmount"
					+ " ,FTradePrice,FTradeMoney,FUnitCost,FAccruedinterest,FBailMoney,FFeeCode1,FTradeFee1,FFeeCode2,FTradeFee2"
					+ " ,FFeeCode3,FTradeFee3,FFeeCode4,FTradeFee4,FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6,FFeeCode7,FTradeFee7"
					+ " ,FFeeCode8,FTradeFee8,FTotalCost,FOrderNum,FDesc"
					+ " ,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)"
					+ " values(" + "?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?"
					+ ",?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?,?,?"
					+ ",?,?,?,?,?" + ",?,?,?,?,?" + ")";
//			pst = conn.prepareStatement(strSql);
			yssPst = dbl.getYssPreparedStatement(strSql);
			if(sNums!=null && sNums.length() > 0 && sNums.indexOf(",")>0){
				sNums="";
			}
			for (i = 0; i < this.bufList.size(); i++) {
				tradesub = (TradeSubBean) bufList.get(i);
				// MS01354 add by zhangfa 20100719 QDV4赢时胜(上海)2010年06月25日01_A
				if (checkRightTrade(tradesub)) {
					continue;
				}
				// ------------------------------------------------------------------------
				yssPstSub.setString(1, sNums.length() > 0 ? sNums : tradesub
						.getNum());
				yssPstSub.setString(2, tradesub.getSecurityCode());
				yssPstSub.setString(3, tradesub.getPortCode());
				yssPstSub.setString(4, tradesub.getBrokerCode());
				yssPstSub.setString(5,
						tradesub.getInvMgrCode().trim().length() == 0 ? " "
								: tradesub.getInvMgrCode());
				yssPstSub.setString(6, tradesub.getTradeCode());
				yssPstSub.setString(7,
						tradesub.getCashAcctCode().trim().length() == 0 ? " "
								: tradesub.getCashAcctCode());
				yssPstSub.setString(8, tradesub.getAttrClsCode());
				yssPstSub.setDate(9, YssFun.toSqlDate(tradesub.getBargainDate()));
				// ----------------------------------------------------------
				yssPstSub.setString(10, tradesub.getBargainTime());
				yssPstSub.setDate(11, YssFun.toSqlDate(tradesub.getSettleDate()));
				yssPstSub.setString(12, tradesub.getSettleTime());
				// --------------------2007.11.16 添加 蒋锦 添加字段-----------------//
				yssPstSub.setDate(13, YssFun.toSqlDate(tradesub.getMatureDate()));
				yssPstSub.setDate(14, YssFun.toSqlDate(tradesub
						.getMatureSettleDate()));
				// ----------------------------------------------------------------//
				yssPstSub.setInt(15, YssFun.toInt(tradesub.getAutoSettle()));
				yssPstSub.setDouble(16, tradesub.getPortCuryRate());
				yssPstSub.setDouble(17, tradesub.getBaseCuryRate());
				yssPstSub.setDouble(18, tradesub.getAllotProportion());
				yssPstSub.setDouble(19, tradesub.getOldAllotAmount());
				yssPstSub.setDouble(20, tradesub.getAllotFactor());
				// -----------------------------------------------------------
				yssPstSub.setDouble(21, tradesub.getTradeAmount());
				yssPstSub.setDouble(22, tradesub.getTradePrice());
				yssPstSub.setDouble(23, tradesub.getTradeMoney());
				yssPstSub.setDouble(24, tradesub.getAccruedInterest());
				yssPstSub.setDouble(25, tradesub.getBailMoney());
				// pstSub.setDouble(24, 0.0); //暂时这样设置
				// ----------------------------------------------------------------
				sFee = this.operSql.buildSaveFeesSql(YssCons.OP_ADD,
						tradesub.getFees()).split(",");
				// ----------------------------------------------------------------
				for (int j = 0; j < sFee.length; j++) {
					yssPstSub
							.setString(26 + j, sFee[j].trim().replaceAll("'",
									""));
				}

				yssPstSub.setDouble(42, tradesub.getTotalCost());
				yssPstSub.setDouble(43, tradesub.getCost().getCost());
				yssPstSub.setDouble(44, tradesub.getCost().getMCost());
				yssPstSub.setDouble(45, tradesub.getCost().getVCost());
				yssPstSub.setDouble(46, tradesub.getCost().getBaseCost());
				yssPstSub.setDouble(47, tradesub.getCost().getBaseMCost());
				yssPstSub.setDouble(48, tradesub.getCost().getBaseVCost());
				yssPstSub.setDouble(49, tradesub.getCost().getPortCost());
				yssPstSub.setDouble(50, tradesub.getCost().getPortMCost());
				yssPstSub.setDouble(51, tradesub.getCost().getPortVCost());
				yssPstSub.setInt(52, Integer.parseInt(tradesub.getSettleState()));// 结算状态，现在修改所有调用此方法的都先赋值
																				// ，xuqiji
																				// 20090805
																				// QDV4.1赢时胜（上海）2009年4月20日15_A
																				// MS00015
																				// 国内权益处理
				yssPstSub.setString(53, tradesub.getOrderNum());
				yssPstSub.setInt(54, 0); // datasource
				yssPstSub.setString(55, tradesub.getDesc());
				// -----------------------------------------------------------------
				yssPstSub.setInt(56, tradesub.checkStateId); // FCheckState=3表示是用于监控临时存储的状态
				yssPstSub.setString(57, pub.getUserCode());
				yssPstSub.setString(58, YssFun
						.formatDatetime(new java.util.Date()));
				yssPstSub.setString(59, pub.getUserCode());
				yssPstSub.setString(60, YssFun
						.formatDatetime(new java.util.Date()));
				yssPstSub.setString(61, tradesub.getFactCashAccCode()); // 实际结算帐户
				yssPstSub.setDouble(62, tradesub.getFactSettleMoney()); // 实际结算金额
				yssPstSub.setDouble(63, tradesub.getExRate()); // 兑换汇率
				yssPstSub.setDouble(64, tradesub.getFactBaseRate());
				yssPstSub.setDouble(65, tradesub.getFactPortRate());
				// 新增字段汇率日期 xuqiji 20090805 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015
				// 国内权益处理
				yssPstSub.setDate(66, YssFun.toSqlDate(tradesub.getBargainDate()));// 为汇率日期
																				// 即成交日期
				yssPstSub.setDate(67, YssFun.toSqlDate(tradesub
						.getFactSettleDate()));// 实际结算日期
				// 操作类型：界面上输入：其他数据为：'HD_JK' FDataSouce=0，权益处理数据:'HD_QY'
				// FDataSouce=0
				// 接口：读入其他数据'ZD_JK' FDataSource=1 ，权益处理数据： 'ZD_QY' FDataSource=1
				yssPstSub.setString(68, tradesub.getDsType());
				// -------------------------------end---------------------------//
				//by guyichuan 20110514 STORY #741 QDV4富国基金2011年3月2日01_A 
				//---edit by songjie 2012.01.16 权益处理配股数据  报空指针异常 start---//
				if(tradesub.getStrRecordDate() != null && tradesub.getStrRecordDate().trim().length() > 0){
					yssPstSub.setDate(69, YssFun.toSqlDate(tradesub.getStrRecordDate()));
				}else{
					yssPstSub.setDate(69, YssFun.toSqlDate("9998-12-31"));
				}
				//---edit by songjie 2012.01.16 权益处理配股数据  报空指针异常 end---//
				yssPstSub.setInt(70, (tradesub.getStrDivdendType().equals("")?0:Integer.parseInt(tradesub.getStrDivdendType().trim())));
				//---------end-----Integer.parseInt(tradesub.getStrDivdendType().trim())
				yssPstSub.setString(71, tradesub.getFdealNum());//story 1574 by zhouwei 20111109 作为与开放式基金(data_openfundtrade)的关联编号使用
				
				//add by songjie 2012.03.26 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A 保存FDataBirth的值
				yssPstSub.setString(72, tradesub.getDataBirth());
				yssPstSub.executeUpdate();
			}
			collectToTrade(bufList, yssPst); // -----20071111
											// 注释,为了测试重载方法,里面重载了collectToTrade(bufList,....)
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new YssException(e.getMessage(), e);
		} finally {
			dbl.closeStatementFinal(yssPst);
			dbl.closeStatementFinal(yssPstSub);
		}
	}

	public void insert(String sNums, java.util.Date beginTradeDate,
			java.util.Date endTradeDate, java.util.Date beginSettleDate,
			java.util.Date endSettleDate, String sSecurityCode,
			String sPortCode, String sInvMgrCode, String sBrokerCode,
			String sTradeType, String sCashAccCode, String sAttrClsCode,
			boolean bAutoDel, boolean needNums) throws YssException {
		insert(sNums, beginTradeDate, endTradeDate, beginSettleDate,
				endSettleDate, sSecurityCode, sPortCode, sInvMgrCode,
				sBrokerCode, sTradeType, sCashAccCode, sAttrClsCode, bAutoDel,
				needNums, "");
	}

	/**
	 * sj 20071112 往库中插入数据
	 * 
	 * @throws YssException
	 */

	public void insertTable(YssPreparedStatement pst) throws YssException {
		TradeBean trade = null;
		String sFees = "";
		String[] sFee = null;
		String sTmpNum = "";
		int iNum = 0;
		boolean bFirst = true;
		HashMap htDiffDate = new HashMap(); // 根据 成交日期不同存各自不同的编号 by leeyu
		try {
			if (htTrade != null) {
				Iterator it = htTrade.values().iterator(); // 对哈希表的值进行编列

				while (it.hasNext()) {
					trade = (TradeBean) it.next(); // 插入主表的值
					// 不用判断是否为第一次，直接判断哈希表中是否存在，不存在就从数据库中去取 xuqiji 20090918
					// 国内权益业务
					if (htDiffDate.get(trade.getBargainDate()) == null) { // 当不同时重新再取
						sTmpNum = getNum(trade);
						htDiffDate.put(trade.getBargainDate(), sTmpNum);
					} else {
						// 如果能取到，取出这个编号
						sTmpNum = htDiffDate.get(trade.getBargainDate())
								.toString();
					}
					if (sTmpNum.trim().length() != 0 && sTmpNum.length() > 9) { // by
																				// liyu
																				// 080331
						iNum = YssFun.toInt(YssFun.right(sTmpNum, 6)); // 取后六位
						sTmpNum = YssFun.left(sTmpNum, 9); // 取出左9位
						iNum++;
						sTmpNum += YssFun.formatNumber(iNum, "000000");
						// 将新的编号放到哈希表中，覆盖原来的编号，保证下次取出的编号是最新的
						htDiffDate.put(trade.getBargainDate(), sTmpNum);
					}
					// ========add by
					// xuxuming,20090819.MS00635.QDV4嘉实2009年08月14日01_B============
					// MS01211 QDV4赢时胜（测试）2010年05月27日03_B panjunfang modify
					// 20100610 只有送股才需要将编号进行处理，否则会造成主键重复出错
					if (trade.getTradeCode() != null
							&& trade.getTradeCode().equals(
									YssOperCons.YSS_JYLX_SG)) {
						String sReplace = "8"; // 替换为
						sTmpNum = YssFun.left(sTmpNum, 9) + sReplace
								+ YssFun.right(sTmpNum, 5); // 将编号第10位统一替换成'8'
					}
					// ===============================================================================
					pst.setString(1, sTmpNum); // 将多次获取编号的方法改为只取一次,以提高效率
					pst.setString(2, trade.getSecurityCode());
					pst.setString(3, trade.getPortCode());
					pst.setString(4,
							trade.getBrokerCode().trim().length() == 0 ? " "
									: trade.getBrokerCode());
					pst.setString(5,
							trade.getInvMgrCode().trim().length() == 0 ? " "
									: trade.getInvMgrCode());
					pst.setString(6, trade.getTradeCode());
					pst.setString(7,
							trade.getCashAcctCode().trim().length() == 0 ? " "
									: trade.getCashAcctCode());
					pst.setString(8, trade.getAttrClsCode());
					pst.setDate(9, YssFun.toSqlDate(trade.getBargainDate()));
					pst.setString(10, trade.getBargainTime());
					pst.setDate(11, YssFun.toSqlDate(trade.getSettleDate()));
					pst.setString(12, trade.getSettleTime());
					pst.setInt(13, 0);
					pst.setDouble(14, trade.getPortCuryRate());
					pst.setDouble(15, trade.getBaseCuryRate());
					pst.setDouble(16, trade.getAllotFactor());
					pst.setDouble(17, trade.getTradeAmount());
					pst.setDouble(18, trade.getTradePrice());
					pst.setDouble(19, trade.getTradeMoney());
					pst.setDouble(20, 0);
					pst.setDouble(21, trade.getAccruedInterest());
					pst.setDouble(22, trade.getBailMoney());
					if (trade.getFees().length() > 0) { // sj edit 20071112
														// 如果有Fees的字符串（从前台传入），则这样解析
						// ----------------------------------------------------------------
						sFee = this.operSql.buildSaveFeesSql(YssCons.OP_ADD,
								trade.getFees()).split(",");
						// ----------------------------------------------------------------
						for (int j = 0; j < sFee.length; j++) {
							pst.setString(23 + j, sFee[j].trim().replaceAll(
									"'", ""));
						}
					} else { // sj edit 20071112 如果是在后台直接获取各个费用，则直接赋值
						pst.setString(23, trade.getFFeeCode1());
						pst.setDouble(24, trade.getFTradeFee1());
						pst.setString(25, trade.getFFeeCode2());
						pst.setDouble(26, trade.getFTradeFee2());
						pst.setString(27, trade.getFFeeCode3());
						pst.setDouble(28, trade.getFTradeFee3());
						pst.setString(29, trade.getFFeeCode4());
						pst.setDouble(30, trade.getFTradeFee4());
						pst.setString(31, trade.getFFeeCode5());
						pst.setDouble(32, trade.getFTradeFee5());
						pst.setString(33, trade.getFFeeCode6());
						pst.setDouble(34, trade.getFTradeFee6());
						pst.setString(35, trade.getFFeeCode7());
						pst.setDouble(36, trade.getFTradeFee7());
						pst.setString(37, trade.getFFeeCode8());
						pst.setDouble(38, trade.getFTradeFee8());
					}
					pst.setDouble(39, trade.getTotalCost());
					pst.setString(40, " "); // temp 订单编号
					pst.setString(41, " "); // temp 描述
					// -------------------------------------------------------------------
					pst.setInt(42, trade.checkStateId); // FCheckState=3表示是用于监控临时存储的状态
					pst.setString(43, pub.getUserCode());
					pst.setString(44, YssFun
							.formatDatetime(new java.util.Date()));
					pst.setString(45, pub.getUserCode());
					pst.setString(46, YssFun
							.formatDatetime(new java.util.Date()));
					pst.executeUpdate();
				}
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		} finally {
			dbl.closeStatementFinal(pst);
		}

	}

	// 汇总到交易主表
	public void collectToTrade() throws YssException {
		int i = 0;
		String sKey = "";
		TradeSubBean tradeSub = null;
		TradeBean trade = null;
		for (i = 0; i < this.bufList.size(); i++) {
			tradeSub = (TradeSubBean) bufList.get(i);
			sKey = tradeSub.getSecurityCode() + "\f" + tradeSub.getBrokerCode()
					+ "\f" + tradeSub.getInvMgrCode() + "\f"
					+ tradeSub.getTradeCode() + "\f"
					+ tradeSub.getBargainDate();
			if (htTrade.contains(sKey)) {
				trade = (TradeBean) htTrade.get(sKey);
				htTrade.remove(sKey);
				htTrade.put(sKey, sumFunc(trade, tradeSub)); // 把重新汇总的数据放入
			} else {
				trade = new TradeBean();
				htTrade.put(sKey, sumFunc(trade, tradeSub)); // 把重新汇总的数据放入
			}
		}
	}

	// 汇总的方法
	public TradeBean sumFunc(TradeBean trade, TradeSubBean subTrade)
			throws YssException {
		if (trade != null && subTrade != null) {
			trade.setTradeAmount(trade.getTradeAmount()
					+ subTrade.getTradeAmount()); // 汇总交易数量
			trade.setTradePrice(subTrade.getTradePrice());
			trade.setTradeMoney(trade.getTradeMoney()
					+ subTrade.getTradeMoney()); // 汇总交易金额
			trade.setUnitCost(0); // 汇总单位成本
			trade.setAccruedInterest(trade.getAccruedInterest()
					+ subTrade.getAccruedInterest()); // 汇总应计利息
			trade.setBailMoney(trade.getBailMoney() + subTrade.getBailMoney()); // 汇总保证金金额
			if (subTrade.getFees().length() > 0) { // 通过前台计算
				buildFees(trade, subTrade); // 汇总费用
			} else if (subTrade.getFTradeFee1() > 0) { // 如果是在后台从数据库获取数据来计算,则这样直接计算
				trade.setFFeeCode1(subTrade.getFFeeCode1());
				trade.setFFeeCode2(subTrade.getFFeeCode2());
				trade.setFFeeCode3(subTrade.getFFeeCode3());
				trade.setFFeeCode4(subTrade.getFFeeCode4());
				trade.setFFeeCode5(subTrade.getFFeeCode5());
				trade.setFFeeCode6(subTrade.getFFeeCode6());
				trade.setFFeeCode7(subTrade.getFFeeCode7());
				trade.setFFeeCode8(subTrade.getFFeeCode8());
				// ------------------------------------------
				trade.setFTradeFee1(trade.getFTradeFee1()
						+ subTrade.getFTradeFee1());
				trade.setFTradeFee2(trade.getFTradeFee2()
						+ subTrade.getFTradeFee2());
				trade.setFTradeFee3(trade.getFTradeFee3()
						+ subTrade.getFTradeFee3());
				trade.setFTradeFee4(trade.getFTradeFee4()
						+ subTrade.getFTradeFee4());
				trade.setFTradeFee5(trade.getFTradeFee5()
						+ subTrade.getFTradeFee5());
				trade.setFTradeFee6(trade.getFTradeFee6()
						+ subTrade.getFTradeFee6());
				trade.setFTradeFee7(trade.getFTradeFee7()
						+ subTrade.getFTradeFee7());
				trade.setFTradeFee8(trade.getFTradeFee8()
						+ subTrade.getFTradeFee8());
			}
			trade.setTotalCost(trade.getTotalCost() + subTrade.getTotalCost());
			trade.setBargainDate(subTrade.getBargainDate());
			trade.setBargainTime(subTrade.getBargainTime());
			trade.setSettleDate(subTrade.getSettleDate());
			trade.setSettleTime(subTrade.getSettleTime());
			trade.setBaseCuryRate(subTrade.getBaseCuryRate());
			trade.setPortCuryRate(subTrade.getPortCuryRate());
			trade.setAllotFactor(subTrade.getAllotFactor());
			// ------------------------------------------------
			trade.setSecurityCode(subTrade.getSecurityCode());
			trade.setBrokerCode(subTrade.getBrokerCode());
			trade.setInvMgrCode(subTrade.getInvMgrCode());
			// -----------------------------------------------
			trade.setPortCode(subTrade.getPortCode());
			trade.setTradeCode(subTrade.getTradeCode());
			trade.checkStateId = subTrade.checkStateId;// xuqiji 20090721
														// QDV4.1赢时胜（上海）2009年4月20日15_A
														// MS00015 国内权益处理
		}
		return trade;
	}

	public void buildFees(TradeBean trade, TradeSubBean subTrade)
			throws YssException {
		String sName = "";
		double dFeeMoney = 0;
		double dTotalFee = 0;
		StringBuffer buf = new StringBuffer();
		FeeBean fee = new FeeBean();
		fee.setYssPub(pub);
		String[] sSubFees = this.operSql.buildSaveFeesSql(YssCons.OP_ADD,
				subTrade.getFees()).split(",");
		String[] sFees = this.operSql.buildSaveFeesSql(YssCons.OP_ADD,
				trade.getFees()).split(",");
		for (int i = 0; i < 8; i++) {
			if (sSubFees[2 * i] != null || sFees[2 * i] != null) {
				fee.setFeeCode(sSubFees[2 * i]);
				fee.getSetting();
				sName = fee.getFeeName();
				if (sSubFees[2 * i + 1] != null) {
					dFeeMoney = YssFun.toDouble(sSubFees[2 * i + 1])
							+ YssFun.toDouble(sFees[2 * i + 1]);
				}
				dTotalFee = YssD.add(dTotalFee, dFeeMoney);
				buf.append(sSubFees[i]).append("\n");
				buf.append(sName).append("\n");
				buf.append(dFeeMoney).append("\n");
				buf.append(fee.buildRowStr().replaceAll("\t", "~")).append(
						"\f\n");
			}
		}
		buf.append("total").append("\n");
		buf.append("Total: ").append("\n");
		buf.append(dTotalFee).append("\n");
		fee.setAccountingWay("0"); // 不计入成本
		buf.append(fee.buildRowStr().replaceAll("\t", "~"));
		trade.setFees(buf.toString());

	}

	// 拆分计算费用
	public void sumFees(TradeBean trade, TradeSubBean subTrade)
			throws YssException {
		//Hashtable htFees = new Hashtable();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		String reFees = "";
		String[] Fees = null;
		String[] subFees = null;
		SimpleBean subSimple = null;
		SimpleBean simple = null;
		if (subTrade.getFees().length() > 0) {
			subFees = subTrade.getFees().split("\f\n");
			Fees = trade.getFees().split("\f\n");
			subSimple = new SimpleBean();
			simple = new SimpleBean();
			for (int i = 0; i < 8; i++) {
				if (subFees.length > i
						&& !subFees[i].split("\n")[0].equalsIgnoreCase("total")) {
					subSimple.parseRowStr(subFees[i]);
					if (Fees.length > 1) {
						simple.parseRowStr(Fees[i]);
					}

					simple.setValue(YssFun
							.formatNumber(YssFun.toDouble(simple.getValue())
									+ YssFun.toDouble(subSimple.getValue()),
									"#,##0.##"));
					simple.setCode(subSimple.getCode());
					simple.setName(subSimple.getName());
					if (subSimple.getFee().length() > 0) {
						simple.setFee(subSimple.getFee());
					}
					reFees += simple.buildRowStr() + "\f\n";
				}
			}
			reFees = reFees.substring(0, reFees.length() - 2);
			trade.setFees(reFees);
		}
	}

	// 获取编号
	private String getNum(TradeBean trade) throws YssException {
		String sFNum = "";
		sFNum = "T"
				+ YssFun.formatDatetime(YssFun.toDate(trade.getBargainDate()))
						.substring(0, 8)
				+ dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Trade"),
						dbl.sqlRight("FNUM", 6), "000001",
						" where FBargainDate = "
								+ dbl.sqlDate(trade.getBargainDate()));
		return sFNum;
	}

	/**
	 * sj 20071111 为子交易数据赋值
	 * 
	 * @param rs
	 *            ResultSet
	 * @param subTrade
	 *            TradeSubBean
	 * @throws YssException
	 * @return TradeSubBean
	 */

	public TradeSubBean setResult(ResultSet rs, TradeSubBean subTrade)
			throws YssException {
		try {
			if (rs != null) {
				subTrade.setBargainDate(YssFun.formatDate(rs
						.getDate("FBargainDate")));
				subTrade.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
				subTrade.setPortCuryRate(rs.getDouble("FPortCuryRate"));
				subTrade.setAllotFactor(rs.getDouble("FBAllotFactor"));
				subTrade.setSecurityCode(rs.getString("FSecurityCode"));
				subTrade.setBrokerCode(rs.getString("FBrokerCode"));
				subTrade.setInvMgrCode(rs.getString("FInvMgrCode"));
				subTrade.setTradeCode(rs.getString("FTradeTypeCode"));
				subTrade.setOldAllotAmount(rs.getDouble("FOldAllotAmount"));
				subTrade.setTradeAmount(rs.getDouble("FTradeAmount"));
				subTrade.setTradePrice(rs.getDouble("FTradePrice"));
				subTrade.setTradeMoney(rs.getDouble("FTradeMoney"));
				// no 单位成本
				subTrade.setAccruedInterest(rs.getDouble("FAccruedinterest"));
				subTrade.setBailMoney(rs.getDouble("FBailMoney"));
				// -------------------------------------------------因为是在后台赋值,所以直接获取各个费用直接赋值
				subTrade.setFTradeFee1(rs.getDouble("FTradeFee1"));
				subTrade.setFTradeFee2(rs.getDouble("FTradeFee2"));
				subTrade.setFTradeFee3(rs.getDouble("FTradeFee3"));
				subTrade.setFTradeFee4(rs.getDouble("FTradeFee4"));
				subTrade.setFTradeFee5(rs.getDouble("FTradeFee5"));
				subTrade.setFTradeFee6(rs.getDouble("FTradeFee6"));
				subTrade.setFTradeFee7(rs.getDouble("FTradeFee7"));
				subTrade.setFTradeFee8(rs.getDouble("FTradeFee8"));
				subTrade.setTotalCost(rs.getDouble("FTotalCost"));
				subTrade.checkStateId = rs.getInt("FCheckState");// xuqiji
																	// 20090721
																	// QDV4.1赢时胜（上海）2009年4月20日15_A
																	// MS00015
																	// 国内权益处理---
			}
			return subTrade;
		} catch (Exception e) {
			throw new YssException(e);
		}

	}

	/**
	 * 重载方法 sj 20071112 传入PreparedStatement,执行insertTable方法
	 * 20120112 modified by liubo.将传入的PreparedStatement对象变为YssPreparedStatement对象
	 * @param bufList
	 *            ArrayList
	 * @param trade
	 *            YssPreparedStatement
	 */

	public void collectToTrade(ArrayList bufList, YssPreparedStatement trade)
			throws YssException {
		collectToTrade(bufList);
		insertTable(trade); // 插入库
	}

	/**
	 * 方法中调用了collectToTrade的重载方法 sj 20071111
	 * 
	 * @param bufList
	 *            ArrayList
	 * @throws YssException
	 */
	public void collectToTrade(ArrayList bufList) throws YssException {
		// String sKey = "";
		TradeSubBean tradeSub = null;
		for (int i = 0; i < this.bufList.size(); i++) {
			tradeSub = (TradeSubBean) bufList.get(i);
			collectToTrade("", tradeSub.getSecurityCode(), tradeSub
					.getBrokerCode(), tradeSub.getInvMgrCode(), tradeSub
					.getTradeCode(), tradeSub.getBargainDate(), false); // 以key的值为where条件进行汇总
			// ,调用另一个重载的方法。传入一条空的FNum
		}
	}

	// ----- MS00799 QDV4中保2009年11月4日01_B 2009.11.13 蒋世超添加
	// 修正单笔结算反审核交易数据的bug.------------------
	public void collectToTrade(String num, String securityCode,
			String brokerCode, String invMgrCode, String tradeTypeCode,
			String bargainDate, boolean ifInsert) throws YssException {

		collectToTrade(num, securityCode, brokerCode, invMgrCode,
				tradeTypeCode, bargainDate, ifInsert, false);
	}

	// ----- MS00799 QDV4中保2009年11月4日01_B 2009.11.13 end
	// ------------------------------------------------------
	/**
	 * 重载方法,用group条件汇总 sj 20071111
	 * 
	 * @param String
	 * @throws YssException
	 * @被collectToTrade(ArrayList) 调用，也可独立使用
	 *                             添加了state条件，如果state为true则汇总主表数据后不改变该条主表数据的审核状态
	 *                             ,如果state为false,将汇总后数据的审核状态有审核变为未审核 蒋世超
	 *                             2009.11.16 MS00799 QDV4中保2009年11月4日01_B
	 */
	public void collectToTrade(String num, String securityCode,
			String brokerCode, String invMgrCode, String tradeTypeCode,
			String bargainDate, boolean ifInsert, boolean state)
			throws YssException {
		int i = 0;
		String sKey = "";
		TradeSubBean tradeSub = null;
		TradeBean trade = null;
		String strSql = "";
		String TNum = "";
		ResultSet rs = null;
		String sWhereSql = "";
		try {
			sKey = securityCode
					+ "\f"
					+ brokerCode
					+ "\f"
					+ invMgrCode
					+ "\f"
					+ tradeTypeCode
					+ "\f"
					+ bargainDate
					+ (num.length() > 0 ? ("\f" + num.substring(0,
							num.length() - 5)) : "");

			if (htTrade.contains(sKey)) {
				return; // 如果在哈希表中有，则说明以这个为key的记录已经汇总过了，直接返回，进行下一个记录的汇总
			}
			strSql = // "select distinct * from (" +
			" select FSecurityCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FBargainDate,FCheckState";// xuqiji
																									// 20090721
																									// QDV4.1赢时胜（上海）2009年4月20日15_A
																									// MS00015
																									// 国内权益处理---
			// ---- MS00813 QDV4中保2009年11月16日01_B 蒋世超
			// 2009.11.23-------------------------
			if (state) {
				// 发现进行单笔结算时，会将交易主表的订单号置空，这里加个判断如果是进行单笔结算，则查询语句添加订单号字段
				strSql += ",max(fordernum) as fordernum";// 添加订单号 蒋世超 2009.11.23
			}
			// ---- MS00813 end ------------------------------------------------
			strSql += ",sum("
					+ dbl.sqlIsNull("FOldAllotAmount", "0")
					+ ") as FOldAllotAmount"
					+ ",sum("
					+ dbl.sqlIsNull("FTradeAmount", "0")
					+ ") as FTradeAmount"
					+ ",avg("
					+ dbl.sqlIsNull("FTradePrice", "0")
					+ ") as FTradePrice"
					+ ",sum("
					+ dbl.sqlIsNull("FTradeMoney", "0")
					+ ") as FTradeMoney"
					+ ",avg("
					+ dbl.sqlIsNull("FAccruedinterest", "0")
					+ ") as FAccruedinterest"
					+ ",sum("
					+ dbl.sqlIsNull("FBailMoney", "0")
					+ ") as FBailMoney"
					+ ",sum("
					+ dbl.sqlIsNull("FTradeFee1", "0")
					+ ") as FTradeFee1"
					+ ",sum("
					+ dbl.sqlIsNull("FTradeFee2", "0")
					+ ") as FTradeFee2"
					+ ",sum("
					+ dbl.sqlIsNull("FTradeFee3", "0")
					+ ") as FTradeFee3"
					+ ",sum("
					+ dbl.sqlIsNull("FTradeFee4", "0")
					+ ") as FTradeFee4"
					+ ",sum("
					+ dbl.sqlIsNull("FTradeFee5", "0")
					+ ") as FTradeFee5"
					+ ",sum("
					+ dbl.sqlIsNull("FTradeFee6", "0")
					+ ") as FTradeFee6"
					+ ",sum("
					+ dbl.sqlIsNull("FTradeFee7", "0")
					+ ") as FTradeFee7"
					+ ",sum("
					+ dbl.sqlIsNull("FTradeFee8", "0")
					+ ") as FTradeFee8"
					+ ",sum("
					+ dbl.sqlIsNull("FTotalCost", "0")
					+ ") as FTotalCost,"
					+ " avg("
					+ dbl.sqlIsNull("FPortCuryRate", "0")
					+ ") as FPortCuryRate,"
					+ " avg("
					+ dbl.sqlIsNull("FBaseCuryRate", "0")
					+ ") as FBaseCuryRate,"
					+ " avg(FAllotFactor) as FBAllotFactor"
					+ " from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState <> 2 "
					+ (num.length() > 0 ? (" and FNum like '")
							+ num.substring(0, num.length() - 5) + "%'" : "")
					+ // 若传入了num值，则把它添加到where条件中
					(securityCode.length() > 0 ? " and FSecurityCode = "
							+ dbl.sqlString(securityCode) : "")
					+ // 当是删除操作时，无法获得下列5个字段的值，所以这样操作
					(brokerCode.length() > 0 ? " and FBrokerCode = "
							+ dbl.sqlString(brokerCode) : "")
					+ (invMgrCode.length() > 0 ? " and FInvMgrCode = "
							+ (invMgrCode.length() > 0 ? dbl
									.sqlString(invMgrCode) : dbl.sqlString(" "))
							: "")
					+ (tradeTypeCode.length() > 0 ? " and FTradeTypeCode = "
							+ dbl.sqlString(tradeTypeCode) : "")
					+ ((bargainDate.length() > 0 && !bargainDate
							.equalsIgnoreCase("1900-01-01")) ? " and FBargainDate = "
							+ dbl.sqlDate(bargainDate)
							: "")
					+ " group by FSecurityCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FBargainDate"
					+ " ,FPortCuryRate,FBaseCuryRate,FCheckState";// xuqiji
																	// 20090721
																	// QDV4.1赢时胜（上海）2009年4月20日15_A
																	// MS00015
																	// 国内权益处理---
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				tradeSub = new TradeSubBean();
				sKey = rs.getString("FSecurityCode")
						+ "\f"
						+ rs.getString("FBrokerCode")
						+ "\f"
						+ rs.getString("FInvMgrCode")
						+ "\f"
						+ rs.getString("FTradeTypeCode")
						+ "\f"
						+ YssFun.formatDate(rs.getDate("FBargainDate"))
						+ (num.length() > 0 ? ("\f" + num.substring(0, num
								.length() - 5)) : "");
				if (htTrade.containsKey(sKey)) {
					trade = (TradeBean) htTrade.get(sKey);
					htTrade.remove(sKey);
					htTrade.put(sKey, sumFunc(trade, setResult(rs, tradeSub))); // 把重新汇总的数据放入
					// ----- MS00799 QDV4中保2009年11月4日01_B 2009.11.13 蒋世超添加
					// 修正单笔结算反审核交易数据的bug. ------------------
					if (state) {
						trade.checkStateId = 1; // 审核状态的交易子表数据才可以进行结算，而交易子表的数据对应的交易主表数据也将是审核状态，所以这里赋默认值为审核态
						trade.setOrderNum(rs.getString("fordernum"));// MS00813
																		// QDV4中保2009年11月16日01_B
																		// 蒋世超2009.11.23
																		// 单笔结算时，重新汇总后业务资料的订单号赋回默认值
					}
					// ------MS00799 QDV4中保2009年11月4日01_B 2009.11.13 end
					// -------------------------------------------------------
				} else {
					trade = new TradeBean();
					htTrade.put(sKey, sumFunc(trade, setResult(rs, tradeSub))); // 把重新汇总的数据放入
					// ----- MS00799 QDV4中保2009年11月4日01_B 2009.11.13 蒋世超添加
					// 修正单笔结算反审核交易数据的bug. ------------------
					if (state) {
						trade.checkStateId = 1;// 审核状态的交易子表数据才可以进行结算，而交易子表的数据对应的交易主表数据也将是审核状态，所以这里赋默认值为审核态
						trade.setOrderNum(rs.getString("fordernum"));// MS00813
																		// QDV4中保2009年11月16日01_B
																		// 蒋世超
																		// 2009.11.23单笔结算时，重新汇总后业务资料的订单号赋回默认值
					}
					// ------MS00799 QDV4中保2009年11月4日01_B 2009.11.13 end
					// -------------------------------------------------------
				}
				if (ifInsert) {
					// insertTrade(num); //如果直接从前台，则插入库
					// ---- MS00813 QDV4中保2009年11月16日01_B 蒋世超
					// 2009.11.23-------------
					if (state) {
						insertTrade(num, true);
					} else {
						insertTrade(num); // 如果直接从前台，则插入库
					}
					// ---- MS00813 QDV4中保2009年11月16日01_B
					// end--------------------------
				}
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 *重载 MS00813 QDV4中保2009年11月16日01_B end
	 */
	public void insertTrade(String num) throws YssException {
		insertTrade(num, false);
	}

	/**
	 * 添加 state 2009.11.23 蒋世超 重载方法 ,单笔结算时 state = true MS00813
	 * QDV4中保2009年11月16日01_B end
	 */
	public void insertTrade(String num, boolean state) throws YssException {
		TradeBean trade = null;
		String sFees = "";
		String[] sFee = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
		//Connection conn = dbl.loadConnection();
		String strSql = "";
		String sTmpNum = "";
		int iNum = 0;
		boolean bFirst = true;
		HashMap htDiffDate = new HashMap(); // 根据不同的成交日期存不同的编号 by leeyu
		try {
			if (htTrade != null) {
				Iterator it = htTrade.values().iterator(); // 对哈希表的值进行编列
				strSql = "insert into "
						+ pub.yssGetTableName("Tb_Data_Trade")
						+ " (FNum,FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,FAttrClsCode,FBargainDate"
						+ " ,FBargainTime,FSettleDate,FSettleTime,FAutoSettle,FPortCuryRate,FBaseCuryRate,FAllotFactor,FTradeAmount"
						+ " ,FTradePrice,FTradeMoney,FUnitCost,FAccruedinterest,FBailMoney,FFeeCode1,FTradeFee1,FFeeCode2,FTradeFee2"
						+ " ,FFeeCode3,FTradeFee3,FFeeCode4,FTradeFee4,FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6,FFeeCode7,FTradeFee7"
						+ " ,FFeeCode8,FTradeFee8,FTotalCost,FOrderNum,FDesc"
						+ " ,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)"
						+ " values(" + "?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?"
						+ ",?,?,?,?,?,?,?,?,?" + ",?,?,?,?,?,?,?,?,?,?"
						+ ",?,?,?,?,?" + ",?,?,?,?,?" + ")";
//				pst = conn.prepareStatement(strSql);
				yssPst = dbl.getYssPreparedStatement(strSql);
				while (it.hasNext()) {
					trade = (TradeBean) it.next(); // 插入主表的值
					if (bFirst) {
						sTmpNum = getNum(trade); // 这里一次性取出,下面直接计算就行,这样可提高效率 by
													// liyu 080331
						htDiffDate.put(trade.getBargainDate(), sTmpNum);
						bFirst = false;
					}
					if (htDiffDate.get(trade.getBargainDate()) == null) {
						sTmpNum = getNum(trade);
						htDiffDate.put(trade.getBargainDate(), sTmpNum);
					}
					delete(num, YssFun.toSqlDate(trade.getBargainDate()), trade
							.getSecurityCode(), trade.getBrokerCode().trim()
							.length() == 0 ? " " : trade.getBrokerCode(), trade
							.getInvMgrCode().trim().length() == 0 ? " " : trade
							.getInvMgrCode(), trade.getTradeCode()); // 以六个条件为删除条件
					if (num.length() > 0) {
						yssPst.setString(1, num.substring(0, num.length() - 5));
					} else {
						// pst.setString(1, getNum(trade));
						if (sTmpNum.trim().length() > 0 && sTmpNum.length() > 9) {
							iNum = YssFun.toInt(YssFun.right(sTmpNum, 6));
							sTmpNum = YssFun.left(sTmpNum, 9);
							iNum++;
							sTmpNum += YssFun.formatNumber(iNum, "000000");
						}
						yssPst.setString(1, sTmpNum);
					}
					yssPst.setString(2, trade.getSecurityCode());
					yssPst.setString(3, trade.getPortCode());
					yssPst.setString(4,
							trade.getBrokerCode().trim().length() == 0 ? " "
									: trade.getBrokerCode());
					yssPst.setString(5,
							trade.getInvMgrCode().trim().length() == 0 ? " "
									: trade.getInvMgrCode());
					yssPst.setString(6, trade.getTradeCode());
					yssPst.setString(7,
							trade.getCashAcctCode().trim().length() == 0 ? " "
									: trade.getCashAcctCode());
					yssPst.setString(8, trade.getAttrClsCode());
					yssPst.setDate(9, YssFun.toSqlDate(trade.getBargainDate()));
					yssPst.setString(10, trade.getBargainTime());
					yssPst.setDate(11, YssFun.toSqlDate(trade.getSettleDate()));
					yssPst.setString(12, trade.getSettleTime());
					yssPst.setInt(13, 0);
					yssPst.setDouble(14, trade.getPortCuryRate());
					yssPst.setDouble(15, trade.getBaseCuryRate());
					yssPst.setDouble(16, trade.getAllotFactor());
					yssPst.setDouble(17, trade.getTradeAmount());
					yssPst.setDouble(18, trade.getTradePrice());
					yssPst.setDouble(19, trade.getTradeMoney());
					yssPst.setDouble(20, 0);
					yssPst.setDouble(21, trade.getAccruedInterest());
					yssPst.setDouble(22, trade.getBailMoney());
					if (trade.getFees().length() > 0) { // sj edit 20071112
														// 如果有Fees的字符串（从前台传入），则这样解析
						// ----------------------------------------------------------------
						sFee = this.operSql.buildSaveFeesSql(YssCons.OP_ADD,
								trade.getFees()).split(",");
						// ----------------------------------------------------------------
						for (int j = 0; j < sFee.length; j++) {
							yssPst.setString(23 + j, sFee[j].trim().replaceAll(
									"'", ""));
						}
					} else { // sj edit 20071112 如果是在后台直接获取各个费用，则直接赋值
						yssPst.setString(23, trade.getFFeeCode1());
						yssPst.setDouble(24, trade.getFTradeFee1());
						yssPst.setString(25, trade.getFFeeCode2());
						yssPst.setDouble(26, trade.getFTradeFee2());
						yssPst.setString(27, trade.getFFeeCode3());
						yssPst.setDouble(28, trade.getFTradeFee3());
						yssPst.setString(29, trade.getFFeeCode4());
						yssPst.setDouble(30, trade.getFTradeFee4());
						yssPst.setString(31, trade.getFFeeCode5());
						yssPst.setDouble(32, trade.getFTradeFee5());
						yssPst.setString(33, trade.getFFeeCode6());
						yssPst.setDouble(34, trade.getFTradeFee6());
						yssPst.setString(35, trade.getFFeeCode7());
						yssPst.setDouble(36, trade.getFTradeFee7());
						yssPst.setString(37, trade.getFFeeCode8());
						yssPst.setDouble(38, trade.getFTradeFee8());
					}
					yssPst.setDouble(39, trade.getTotalCost());
					// pst.setString(40, " "); //temp 订单编号
					// ---- MS00813 QDV4中保2009年11月16日01_B 蒋世超 2009.11.23
					// ----------------------------------------------------
					if (state) {
						yssPst.setString(40, trade.getOrderNum()); // 单笔结算时保留原有订单号，不应该置为空
																// 蒋世超 添加
																// 2009.11.23
					} else {
						yssPst.setString(40, " "); // temp 订单编号
					}
					// ---- MS00813 QDV4中保2009年11月16日01_B end
					yssPst.setString(41, " "); // temp 描述
					// -------------------------------------------------------------------
					yssPst.setInt(42, trade.checkStateId); // FCheckState=3表示是用于监控临时存储的状态
					yssPst.setString(43, pub.getUserCode());
					yssPst.setString(44, YssFun
							.formatDatetime(new java.util.Date()));
					yssPst.setString(45, pub.getUserCode());
					yssPst.setString(46, YssFun
							.formatDatetime(new java.util.Date()));
					yssPst.executeUpdate();
				}
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		} finally {
			dbl.closeStatementFinal(yssPst);
		}

	}

	// MS01354 add by zhangfa 20100720 QDV4赢时胜(上海)2010年06月25日01_A
	/**
	 * 检测是否存在通过接口导入相关的交易数据
	 */
	public boolean checkRightTrade(TradeSubBean tradesub) throws YssException {
		boolean flag = false;
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = " select fsecuritycode,fsettledate,fjkdr from  "
					+ pub.yssGetTableName("tb_data_subtrade")
					+ " where  fsecuritycode="
					+ dbl.sqlString(tradesub.getSecurityCode())
					+ " and fbargaindate="
					+ dbl.sqlDate(tradesub.getBargainDate())
					+ " and ftradetypecode="
					+ dbl.sqlString(tradesub.getTradeCode())
					+ " and FportCode=" + dbl.sqlString(tradesub.getPortCode())
					+ " and FJKDR ='1'";

			rs = dbl.openResultSet(strSql);

			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			throw new YssException("查询交易数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return flag;

	}
	// ----------------------------------------------------------------------

}
