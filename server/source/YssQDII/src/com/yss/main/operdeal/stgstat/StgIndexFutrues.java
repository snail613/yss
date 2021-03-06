package com.yss.main.operdeal.stgstat;

import java.sql.*;
import java.util.*;
import java.util.Date;

//---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
import com.yss.commeach.*;
import com.yss.main.operdata.futures.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.main.operdeal.businesswork.futures.futuresdistilldata.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.util.*;

/**
 * 
 * <p>
 * Title: 股指期货库存统计，插入综合业务业务
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class StgIndexFutrues extends BaseStgStatDeal {

	public StgIndexFutrues() {
	}

	public ArrayList getStorageStatData(java.util.Date dDate)
			throws YssException {
		getFuturesTradeRela(dDate, portCodes);
		//---xuqiji 20100511 期货套期保值库存统计 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A--//
		StgFuturesHedging stgHedging = new StgFuturesHedging();
		stgHedging.setYssPub(pub);
		String sPortCode = portCodes.replaceAll("'","");
		stgHedging.initStorageStat(dDate, dDate, sPortCode, false, false);//初始化变量值
		stgHedging.doHedgingManage(dDate);//入口方法
		//-----------------------end------------------------------------//
		return null;
	}

	/**
	 * 使用以逗号隔开的组合代码获取组合代码数组 MS00321 QDV4招商证券2009年03月17日01_A 2009-03-18 蒋锦 添加
	 * 
	 * @param sPortCodeStr
	 *            String：以逗号隔开的组合代码
	 * @return String[]：组合数组
	 * @throws YssException
	 */
	public String[] getPortArrBy(String sPortCodeStr) throws YssException {
		String[] arrPortCode = null;
		try {
			arrPortCode = sPortCodeStr.replaceAll("'", "").split(",");
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		}
		return arrPortCode;
	}

	/**
	 * 先入先出统计股指期货交易关联数据，产生资金调拨
	 * 
	 * @param dDate
	 *            Date
	 * @throws YssException
	 */
	private void getFuturesTradeRela(java.util.Date dDate, String sPortCodes)
			throws YssException {
		ArrayList alStockRela = new ArrayList();
		FuturesTradeRelaAdmin futTrdAdmin = null;
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		try {
			futTrdAdmin = new FuturesTradeRelaAdmin();
			futTrdAdmin.setYssPub(pub);

			bTrans = true;
			conn.setAutoCommit(false);
			futTrdAdmin.deleteData(dDate,sPortCodes);//add by xxm,MS00930,加上组合代码。不能全部删除

			// 计算开仓交易库存余额和平仓交易投资收益
			alStockRela = getOpenTradeAmountAndCloseTradeIncome(dDate,
					sPortCodes);
			futTrdAdmin.saveMutliSetting(alStockRela, conn);

			// 统计库存后，处理资金调拨 by sunkey 20081121 BugID:MS00013
			CashTransferDistill ctd = new CashTransferDistill();
			ctd.setYssPub(pub);
			ctd.setWorkDate(dDate);
			ctd.setPortCodes(sPortCodes);
			ctd.doOperation(""); // 处理资金调拨
			conn.commit();

			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 获取期货库存数量
	 * add by songjie 2012.12.07 
	 * STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
	 * @param dWorkDay
	 *            Date：业务日期
	 * @param sPortCodes
	 *            String：组合代码
	 * @return HashMap：以组合代码和证券代码为 Key，ArrayList 为 Value 的结果哈希表
	 * @throws YssException
	 */
	public HashMap getFutTradeStockOne(java.util.Date dWorkDay, String sPortCodes, String tsfTypeCode)
			throws YssException {
		HashMap hmResult = new HashMap();
		ArrayList alTrade = null;
		ResultSet rs = null;
		String strSql = "";
		try {
			// ---------获取日前一天库存余额-----------//
			strSql = " select * from " + pub.yssGetTableName("TB_Data_FutTradeRela")
			+ " WHERE FTransDate = " + dbl.sqlDate(YssFun.addDay(dWorkDay, -1))
			+ " AND FTsfTypeCode = " + dbl.sqlString(tsfTypeCode)
			+ " AND FStorageAmount <> 0 and FCloseNum = ' ' "
			+ " AND FPortCode = " + dbl.sqlString(sPortCodes)
			;
			rs = dbl.queryByPreparedStatement(strSql);
			while (rs.next()) {
				// Hash Key
				String sKey = rs.getString("FSecurityCode") + "\t" + rs.getString("FBrokerCode");//证券代码 + 券商代码
						
				alTrade = (ArrayList) hmResult.get(sKey);
				if (alTrade == null) {
					alTrade = new ArrayList();
				}
				FuturesTradeBean trade = new FuturesTradeBean();
				trade.setNum(rs.getString("FNUM"));
				trade.setBargainDate(YssFun.formatDate(rs
						.getDate("FTransDate")));
				trade.setSecurityCode(rs.getString("FNum"));
				trade.setPortCode(rs.getString("FPortCode"));
				trade.setBrokerCode(rs.getString("FBrokerCode"));
				trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
				trade.setTradeAmount(rs.getDouble("FTradeAmount"));
				trade.setBegBailMoney(rs.getDouble("FBegBailMoney"));
				trade.setCloseAmount(rs.getDouble("FStorageAmount"));
				trade.setLastAppMoney(rs.getDouble("FMoney"));
				// 组合代码和证券代码相同的交易记录存放在同一个 ArrayList 中
				alTrade.add(trade);
				hmResult.put(sKey, alTrade);
			}
		} catch (Exception e) {
			throw new YssException("获取期货库存数量出错！\r\n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return hmResult;
	}
	
	/**
	 * 获取期货库存数量
	 * 
	 * @param dWorkDay
	 *            Date：业务日期
	 * @param sPortCodes
	 *            String：组合代码
	 * @return HashMap：以组合代码和证券代码为 Key，ArrayList 为 Value 的结果哈希表
	 * @throws YssException
	 */
	public HashMap getFutTradeStock(java.util.Date dWorkDay, String sPortCodes, String tsfTypeCode)
			throws YssException {
		HashMap hmResult = new HashMap();
		ArrayList alTrade = null;
		ResultSet rs = null;
		String strSql = "";
		try {
			// ---------获取计算日前一天未完全平仓的交易数据，和库存数量余额-----------//
			strSql = "SELECT ft.*, ftr.FStorageAmount, ftr.FMoney"
					+ " FROM (SELECT FNum, FStorageAmount, FMoney" + " FROM "
					+ pub.yssGetTableName("TB_Data_FutTradeRela")
					+ " WHERE FTransDate = "
					+ dbl.sqlDate(YssFun.addDay(dWorkDay, -1))
					+ " AND FTsfTypeCode = " 
					+ dbl.sqlString(tsfTypeCode)        // modify by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A 
					+ " AND FStorageAmount <> 0) ftr" + " JOIN (SELECT *"
					//edit by songjie 2013.03.12 Futurestrade 改为 Futurestrade_Tmp
					+ " FROM " + pub.yssGetTableName("Tb_Data_Futurestrade_Tmp")
					+ " WHERE FCheckState = 1" + " AND FBargainDate <= "
					+ dbl.sqlDate(YssFun.addDay(dWorkDay, -1))
					+ " AND FPortCode = " + dbl.sqlString(sPortCodes)
					+ " AND FTradeTypeCode = '20') ft ON ftr.FNum = ft.FNum"
					+ " ORDER BY ft.FNum";
			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				// Hash Key
				String sKey = rs.getString("FPortCode") + "\t"
						+ rs.getString("FSecurityCode");
				alTrade = (ArrayList) hmResult.get(sKey);
				if (alTrade == null) {
					alTrade = new ArrayList();
				}
				FuturesTradeBean trade = new FuturesTradeBean();
				trade.setNum(rs.getString("FNUM"));
				trade.setBargainDate(YssFun.formatDate(rs
						.getDate("FBargainDate")));
				trade.setSecurityCode(rs.getString("FSecurityCode"));
				trade.setPortCode(rs.getString("FPortCode"));
				trade.setBrokerCode(rs.getString("FBrokerCode"));
				trade.setInvMgrCode(rs.getString("FInvMgrCode"));
				trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
				trade.setTradeAmount(rs.getDouble("FTradeAmount"));
				trade.setBegBailMoney(rs.getDouble("FBegBailMoney"));
				// 将交易关联数据中的库存数量放在交易记录中
				trade.setCloseAmount(rs.getDouble("FStorageAmount"));
				trade.setTradePrice(rs.getDouble("FTradePrice"));
				// 将昨日股值增值余额关联到交易，计算卖出估值增值时使用
				trade.setLastAppMoney(rs.getDouble("FMoney"));
				// 组合代码和证券代码相同的交易记录存放在同一个 ArrayList 中
				alTrade.add(trade);
				hmResult.put(sKey, alTrade);
			}
		} catch (Exception e) {
			throw new YssException("获取期货库存数量出错！\r\n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return hmResult;
	}

	/**
	 * 生成获取期货当日交易的 SQL 语句 使用先入先出法计算和使用当日优先先入先出法计算的排序方式不同， 当日优先先入先出需要将开仓记录排在前面
	 * MS00321 QDV4招商证券2009年03月17日01_A 2009-03-18 蒋锦 添加
	 * 
	 * @param dWorkDay
	 *            Date
	 * @param sPortCode
	 *            String
	 * @param sAccountType
	 *            String
	 * @return String
	 * @throws YssException
	 */
	private String createSQLForGetFutruesTrade(java.util.Date dWorkDay,
			String sPortCode, String sAccountType, String subCatCode) throws YssException {
		StringBuffer sqlBuf = new StringBuffer();
		try {
			if (sAccountType
					.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO)) {
				sqlBuf
						.append("SELECT a.*, m.*, idf.FMultiple, idf.FFUType, m.FCsPortCury, sec.FTradeCury FROM");
				sqlBuf.append(" (SELECT * ");
				sqlBuf.append(" FROM ");
				//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
				sqlBuf.append(pub.yssGetTableName("TB_Data_FuturesTrade_Tmp"));
				sqlBuf.append(" WHERE FBargainDate = " + dbl.sqlDate(dWorkDay));
				sqlBuf.append(" AND FCheckState = 1 ");
				sqlBuf.append(" AND FPortCode = ").append(
						dbl.sqlString(sPortCode));
				sqlBuf.append(" AND FTradeTypeCode = ").append(
						dbl.sqlString(YssOperCons.YSS_JYLX_KC));
				sqlBuf.append(" ORDER BY FNum DESC)a ");
				// add by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
				sqlBuf.append(" JOIN (SELECT * FROM ");
				sqlBuf.append(pub.yssGetTableName("Tb_Para_IndexFutures"));
				sqlBuf.append(" where fcheckstate = 1 and FSubCatCode = " );
				sqlBuf.append(dbl.sqlString(subCatCode));
				// ------------
				sqlBuf.append(" ) idf ON a.FSecurityCode = idf.FSecurityCode");
				sqlBuf.append(" LEFT JOIN (SELECT *");
				sqlBuf.append(" FROM ").append(
						pub.yssGetTableName("Tb_Para_Security"));
				sqlBuf.append(" ) sec ON sec.fsecuritycode = a.FSecurityCode");
				// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
		
				sqlBuf.append(" LEFT JOIN (SELECT FPortCode,FPortName,FPortCury as FCsPortCury ");				
				sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Para_Portfolio"));
				sqlBuf.append(" WHERE  FCheckState = 1) m ON a.FPortCode = m.FPortCode");
							
				//end by lidaolong 
				sqlBuf.append(" UNION ALL ");
				sqlBuf
						.append("SELECT a.*, m.*, idf.FMultiple, idf.FFUType, m.FCsPortCury, sec.FTradeCury FROM");
				sqlBuf.append(" (SELECT * ");
				sqlBuf.append(" FROM ");
				//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
				sqlBuf.append(pub.yssGetTableName("TB_Data_FuturesTrade_Tmp"));
				sqlBuf.append(" WHERE FBargainDate = " + dbl.sqlDate(dWorkDay));
				sqlBuf.append(" AND FCheckState = 1 ");
				sqlBuf.append(" AND FPortCode = ").append(
						dbl.sqlString(sPortCode));
				sqlBuf.append(" AND FTradeTypeCode = ").append(
						dbl.sqlString(YssOperCons.YSS_JYLX_PC));
				sqlBuf.append(" ORDER BY FNum ASC) a ");
				// add by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
				sqlBuf.append(" JOIN (SELECT * FROM ");
				sqlBuf.append(pub.yssGetTableName("Tb_Para_IndexFutures"));
				sqlBuf.append(" where fcheckstate = 1 and FSubCatCode = " );
				sqlBuf.append(dbl.sqlString(subCatCode));
				// ------------
				sqlBuf.append(" ) idf ON a.FSecurityCode = idf.FSecurityCode");
				sqlBuf.append(" LEFT JOIN (SELECT *");
				sqlBuf.append(" FROM ").append(
						pub.yssGetTableName("Tb_Para_Security"));
				sqlBuf.append(" ) sec ON sec.fsecuritycode = a.FSecurityCode");
				// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
				

				sqlBuf.append(" LEFT JOIN (SELECT FPortCode,FPortName,FPortCury as FCsPortCury ");
				sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Para_Portfolio"));
				sqlBuf.append(" WHERE  FCheckState = 1) m ON a.FPortCode = m.FPortCode ");

				
				//end by lidaolong 
			} else {
				sqlBuf
						.append("SELECT a.*, m.*, idf.FMultiple, idf.FFUType, m.FCsPortCury, sec.FTradeCury FROM");
				sqlBuf.append(" (SELECT * FROM ");
				//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
				sqlBuf.append(pub.yssGetTableName("TB_Data_FuturesTrade_Tmp"));
				sqlBuf.append(" WHERE FBargainDate = " + dbl.sqlDate(dWorkDay));
				sqlBuf.append(" AND FCheckState = 1");
				sqlBuf.append(" AND FPortCode = ").append(
						dbl.sqlString(sPortCode));
				sqlBuf.append(" ORDER BY FNum) a");
				// add by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A
				sqlBuf.append(" JOIN (SELECT * FROM ");
				sqlBuf.append(pub.yssGetTableName("Tb_Para_IndexFutures"));
				sqlBuf.append(" where fcheckstate = 1 and FSubCatCode = " );
				sqlBuf.append(dbl.sqlString(subCatCode));
				// ------------
				sqlBuf.append(" ) idf ON a.FSecurityCode = idf.FSecurityCode");
				sqlBuf.append(" LEFT JOIN (SELECT *");
				sqlBuf.append(" FROM ").append(
						pub.yssGetTableName("Tb_Para_Security"));
				sqlBuf.append(" ) sec ON sec.fsecuritycode = a.FSecurityCode");
				
				// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
				
		/*		sqlBuf.append(" LEFT JOIN (SELECT mb.*");
				sqlBuf
						.append(" FROM (SELECT FPortCode, MAX(FStartDate) as FStartDate");
				sqlBuf.append(" FROM "
						+ pub.yssGetTableName("Tb_Para_Portfolio"));
				sqlBuf.append(" WHERE FStartDate <= " + dbl.sqlDate(dWorkDay));
				sqlBuf.append(" AND FCheckState = 1");
				sqlBuf.append(" GROUP BY FPortCode) ma");
				sqlBuf
						.append(" JOIN (SELECT FPortCode,FPortName,FStartDate,FPortCury as FCsPortCury");
				sqlBuf.append(" FROM "
						+ pub.yssGetTableName("Tb_Para_Portfolio"));
				sqlBuf
						.append(" ) mb ON ma.FPortCode = mb.FPortCode AND ma.FStartDate = mb.FStartDate) m ON a.FPortCode = m.FPortCode order by FNum"); */// xuqiji
																																							// 20090428			
																																				// MS00404
																																							// 股指期货交易按照先进先出方式计算结果有误
				sqlBuf.append(" LEFT JOIN (SELECT FPortCode,FPortName,FStartDate,FPortCury as FCsPortCury ");
				sqlBuf.append(" FROM " + pub.yssGetTableName("Tb_Para_Portfolio"));
				sqlBuf.append(" WHERE  FCheckState = 1) m ON a.FPortCode = m.FPortCode order by FNum"); 
				//end by lidaolong 	
			}
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		}
		return sqlBuf.toString();
	}

	/**
	 * 使用组合代码获取期货核算类型 MS00321 QDV4招商证券2009年03月17日01_A 2009.03.18 蒋锦 添加
	 * 
	 * @param sPortCode
	 *            String：组合代码
	 * @param htPortAccountType
	 *            Hashtable：核算类型组合对
	 * @return String：核算类型
	 * @throws YssException
	 * modify by fangjiang 2010.08.27 MS01439 QDV4博时2010年7月14日02_A 
	 * modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
	 */
	private String getAccountTypeBy(String sPortCode,
			Hashtable htPortAccountType, int i) throws YssException {
		// modify by fangjiang 2010.08.27 MS01439 QDV4博时2010年7月14日02_A 
		// i = 0 时处理股指期货，i = 1 时处理债券期货，i = 2时处理外汇期货 , i = 3 时处理商品期货 //modify huangqirong 2012-08-21  商品期货
		String sAccountType = "";
		if(i == 0){ //股指期货
			// 默认使用先入先出
			sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO;
			String sTheDayFirstFIFO = "";
			String sModAvg = "";
			try {
				sTheDayFirstFIFO = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO);
				sModAvg = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG);
				if (sTheDayFirstFIFO != null
						&& sTheDayFirstFIFO.indexOf(sPortCode) != -1) {//edit by xuxuming,20091228.以前的代码在此处写反了
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO;
				} else if (sModAvg != null && sModAvg.indexOf(sPortCode) != -1) {//edit by xuxuming,20091228.以前的代码在此处写反了
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG;
				}
			} catch (Exception ex) {
				throw new YssException(ex.getMessage());
			}
		} else if (i == 1 || i==2 || i == 3){ //债券期货，外汇期货  , 商品期货 //modify huangqirong 2012-08-21  商品期货
			// 默认移动加权
			sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG;
			String sTheDayFirstFIFO = "";
			String sFIFO = "";
			try {
				sTheDayFirstFIFO = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO);
				sFIFO = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO);
				if (sTheDayFirstFIFO != null
						&& sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO;
				} else if (sFIFO != null && sFIFO.indexOf(sPortCode) != -1) {
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO;
				}
			} catch (Exception ex) {
				throw new YssException(ex.getMessage());
			}
		}
		return sAccountType;
		//-------------------
	}

	/**
	 * 获取开仓交易记录 MS00321 QDV4招商证券2009年03月17日01_A 2009.03.18 蒋锦 添加
	 * 
	 * @param outHmTrade
	 *            HashMap：储存所有交易记录的哈希表，在此有返回参数的作用
	 * @param rs
	 *            ResultSet：获取交易记录的结果集
	 * @param sAccountType
	 *            String：核算类型
	 * @throws YssException
	 */
	private void getFuturesOpenTrade(HashMap outHmTrade, ResultSet rs,
			String sAccountType) throws YssException {
		FuturesTradeBean trade = new FuturesTradeBean();
		ArrayList alTrade = null;
		try {
			trade.setNum(rs.getString("FNUM"));
			trade.setSecurityCode(rs.getString("FSecurityCode"));
			trade.setBargainDate(YssFun.formatDate(rs.getDate("FBargainDate")));
			trade.setPortCode(rs.getString("FPortCode"));
			trade.setBrokerCode(rs.getString("FBrokerCode"));
			trade.setInvMgrCode(rs.getString("FInvMgrCode"));
			trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
			trade.setTradeAmount(rs.getDouble("FTradeAmount"));
			trade.setCloseAmount(rs.getDouble("FTradeAmount"));
			trade.setTradePrice(rs.getDouble("FTradePrice"));
			trade.setBegBailMoney(rs.getDouble("FBegBailMoney"));
			trade.setTradeMoney(rs.getDouble("FTradeMoney"));
			alTrade = (ArrayList) outHmTrade.get(trade.getPortCode() + "\t"
					+ trade.getSecurityCode());
			if (alTrade == null) {
				alTrade = new ArrayList();
			}
			if (sAccountType
					.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO)) {
				alTrade.add(0, trade);
			} else {
				alTrade.add(trade);
			}
			outHmTrade.put(
					trade.getPortCode() + "\t" + trade.getSecurityCode(),
					alTrade);
		} catch (Exception ex) {
			throw new YssException("获取开仓记录出错！", ex);
		}
	}

	/**
	 * 平仓业务处理 计算平仓收益，和经过平仓后开仓交易的剩余库存数量 2009.03.18 蒋锦 修改 MS00321
	 * QDV4招商证券2009年03月17日01_A 添加了使用当日优先先入先出的核算方式
	 * 
	 * @param dWorkDay
	 *            Date：业务日期
	 * @param outHmTrade
	 *            HashMap：储存所有交易记录的哈希表，在此有同时返回参数的作用
	 * @param outHmTradeRela
	 *            HashMap：记录所有交易关联记录的哈希表，在此有同时返回参数的作用
	 * @param rs
	 *            ResultSet：获取交易记录的结果集
	 * @throws YssException
	 */
	public void closeBusinessProcessing(java.util.Date dWorkDay,
			HashMap outHmTrade, HashMap outHmTradeRela, ResultSet rs, 
			String tsfTypeCode1, String tsfTypeCode2, String tsfTypeCode3)
			throws YssException {
		ArrayList alTrade = null;
		try {
			// 取出平仓交易所对应的开仓交易
			alTrade = (ArrayList) outHmTrade.get(rs.getString("FPortCode")
					+ "\t" + rs.getString("FSecurityCode"));
			// 存放平仓的投资收益
			FuturesTradeRelaBean trdRela = new FuturesTradeRelaBean();
			// 本比平仓交易的交易数量
			// 每循环一笔开仓交易就减去相应的数量，直到平仓数量==0
			double dbCloseAmount = rs.getDouble("FTradeAmount");
			// 循环平仓交易对应的开仓交易数据，进行先入先出计算
			for (int i = 0; i < alTrade.size(); i++) {
				// 卖出估值增值的关联数据
				FuturesTradeRelaBean sellRela = null;

				FuturesTradeBean trade = (FuturesTradeBean) alTrade.get(i);
				// 如果平仓数量小于开仓交易的库存数量
				if (trade.getCloseAmount() >= dbCloseAmount) {
					trdRela.setNum(rs.getString("FNum"));
					trdRela.setPortCode(rs.getString("FPortCode"));
					trdRela.setTsfTypeCode(tsfTypeCode1); // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
					trdRela.setTransDate(dWorkDay);
					// 多头
					if (rs.getString("FFUType").equalsIgnoreCase("BuyAM")) {
						// 原币投资收益 = (平仓价格 - 开仓价格) * 平仓数量 * 放大倍数
						trdRela.setMoney(YssD.add(trdRela.getMoney(), YssD
								.round(YssD
										.mul(YssD.mul(YssD.sub(rs
												.getDouble("FTradePrice"),
												trade.getTradePrice()), rs
												.getDouble("FMultiple")),
												dbCloseAmount), 2)));
					}
					// 空头
					else {
						// 原币投资收益 = (开仓价格 - 平仓价格) * 平仓数量 * 放大倍数
						trdRela.setMoney(YssD.add(trdRela.getMoney(), YssD
								.round(YssD
										.mul(YssD.mul(YssD.sub(trade
												.getTradePrice(), rs
												.getDouble("FTradePrice")), rs
												.getDouble("FMultiple")),
												dbCloseAmount), 2)));
					}
					trdRela.setStorageAmount(trdRela.getStorageAmount()
							+ dbCloseAmount);

					// 计算卖出估值增值
					sellRela = getSellAppreciForFIFO(dWorkDay, trade, rs,
							dbCloseAmount, tsfTypeCode2, tsfTypeCode3); // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 

					// 开仓交易的库存数量 = 昨日库存 - 今日平仓数量
					trade.setCloseAmount(YssD.sub(trade.getCloseAmount(),
							dbCloseAmount));
					// 2009.03.18 蒋锦 修改 判断判断卖出估值增值不为空时才添加进哈希表
					if (sellRela != null) {
						outHmTradeRela.put(sellRela.getNum() + "\t"
								+ sellRela.getCloseNum(), sellRela);
					}
					break;
				}
				// 如果平仓数量大于开仓交易的库存数量
				else {
					// 平仓数量减去开仓数量
					dbCloseAmount = YssD.sub(dbCloseAmount, trade
							.getCloseAmount());
					trdRela.setNum(rs.getString("FNum"));
					trdRela.setTsfTypeCode(tsfTypeCode1); // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
					trdRela.setTransDate(dWorkDay);
					// 多头
					if (rs.getString("FFUType").equalsIgnoreCase("BuyAM")) {
						trdRela.setMoney(YssD.add(trdRela.getMoney(), YssD
								.round(YssD.mul(YssD.mul(YssD.sub(rs
										.getDouble("FTradePrice"), trade
										.getTradePrice()), rs
										.getDouble("FMultiple")), trade
										.getCloseAmount()), 2)));
					}
					// 空头
					else {
						trdRela.setMoney(YssD.add(trdRela.getMoney(), YssD
								.round(YssD.mul(YssD.mul(YssD.sub(trade
										.getTradePrice(), rs
										.getDouble("FTradePrice")), rs
										.getDouble("FMultiple")), trade
										.getCloseAmount()), 2)));
					}
					// 计算卖出估值增值
					sellRela = getSellAppreciForFIFO(dWorkDay, trade, rs, trade
							.getCloseAmount(), tsfTypeCode2, tsfTypeCode3); // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 

					trdRela.setStorageAmount(YssD.add(trdRela
							.getStorageAmount(), trade.getCloseAmount()));
					// 2009.03.18 蒋锦 修改 判断判断卖出估值增值不为空时才添加进哈希表
					//------ modify by wangzuochun 2010.03.31 MS01023   当天两笔平仓，收益表结转损益不正确   QDV4中金2010年03月14日01_B
					if (sellRela != null && (sellRela.getStorageAmount() > 0)) {
					//--------------------------MS01023---------------------------//
						outHmTradeRela.put(sellRela.getNum() + "\t"
								+ sellRela.getCloseNum(), sellRela);
					}
					// 开仓交易被完全平仓，库存数量为 0
					trade.setCloseAmount(0);
				}
			}
			trdRela.setSettleState(1);
			// ----------------计算投资收益的基础货币和组合货币金额-----------------//
			// 组合汇率
			double dBaseRate = 1;
			// 基础汇率
			double dPortRate = 1;
			if (!rs.getString("FTradeCury").equalsIgnoreCase(pub.getPortBaseCury(rs.getString("FPortCode")))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
				dBaseRate = this.getSettingOper().getCuryRate(dWorkDay,
						rs.getString("FTradeCury"), rs.getString("FPortCode"),
						YssOperCons.YSS_RATE_BASE);
			}
			// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090421 更改汇率获取方式 by
			// leeyu --
			EachRateOper eachOper = new EachRateOper();
			eachOper.setYssPub(pub);
			eachOper.getInnerPortRate(dWorkDay, rs.getString("FCsPortCury"), rs
					.getString("FPortCode"));
			dPortRate = eachOper.getDPortRate();
			// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090421 更改汇率获取方式 by
			// leeyu --
			trdRela.setBaseCuryRate(dBaseRate);
			trdRela.setPortCuryRate(dPortRate);
			// 基础货币估值增值
			trdRela.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
					trdRela.getMoney(), trdRela.getBaseCuryRate()));
			// 组合货币估值增值
			trdRela.setPortCuryMoney(this.getSettingOper().calPortMoney(
					trdRela.getMoney(), trdRela.getBaseCuryRate(),
					trdRela.getPortCuryRate(),
					// linjunyun 2008-11-25 bug:MS00011
					// 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
					rs.getString("FTradeCury"), dWorkDay,
					rs.getString("FPortCode")));
			// ----------------------------------------------------//
			outHmTradeRela.put(trdRela.getNum(), trdRela);
		} catch (Exception ex) {
			throw new YssException("处理平仓业务出错！", ex);
		}
	}

	/**
	 * add by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
	 * 获取期货昨日库存
	 * @param dWorkDay
	 *            Date
	 * @param sPortCode
	 *            String
	 * @return HashMap
	 * tsfTypeCode1 05FU 成本
	 * tsfTypeCode4 09FU 估值增值
	 * @throws YssException
	 */
	private HashMap getYesterdayStockOne(java.util.Date dWorkDay, String sPortCode, String tsfTypeCode1, String tsfTypeCode4)
			throws YssException {
		ResultSet rs = null;
		String strSql = "";
		HashMap hmStock = new HashMap();
		FuturesTradeRelaBean trdRela = null;
		try {
			strSql = "SELECT a.*, b.FPrice, d.FBaseRate, d.FPortRate, e.FMultiple FROM "
					+ pub.yssGetTableName("TB_Data_FutTradeRela")
					+ " a"
					+ " LEFT JOIN "
					+ pub.yssGetTableName("Tb_Data_ValMktPrice")
					+ " b"
					+ " ON a.FPortCode = b.FPortCode AND a.FTransDate = b.FValDate AND a.FNum = b.FSecurityCode"
					+ " LEFT JOIN "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " c"
					+ " ON a.FNum = c.FSecurityCode"
					+ " LEFT JOIN "
					+ pub.yssGetTableName("Tb_Data_ValRate")
					+ " d"
					+ " ON a.FPortCode = d.FPortCode AND a.FTransDate = d.FValDate AND c.FTradeCury = d.FCuryCode"
					+ " LEFT JOIN "
					+ pub.yssGetTableName("Tb_Para_IndexFutures")
					+ " e"
					+ " ON a.FNum = e.FSecurityCode"
					+ " WHERE a.FTSFTYPECODE = " 
					+ dbl.sqlString(tsfTypeCode1)   // modify by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A 
					+ " AND a.FTransDate = "
					+ dbl.sqlDate(YssFun.addDay(dWorkDay, -1))
					+ " AND a.FSettleState = 1"
					+ " AND a.FPortCode = "
					+ dbl.sqlString(sPortCode) + " AND a.FStorageAmount <> 0 AND A.FCLOSENUM = ' '";//edit by yanghaiming 20101112 QDV4深圳赢时胜2010年9月28日01_A
			rs = dbl.queryByPreparedStatement(strSql); // modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				trdRela = new FuturesTradeRelaBean();
				trdRela.setNum(rs.getString("FNum"));
				trdRela.setBailMoney(rs.getDouble("FBailMoney"));
				trdRela.setBaseCuryRate(rs.getDouble("FBaseRate"));
				trdRela.setPortCuryRate(rs.getDouble("FPortRate"));
				// trdRela.setMoney(YssD.mul(rs.getDouble("FStorageAmount"),
				// rs.getDouble("FPrice"), rs.getDouble("FMultiple")));
				trdRela.setMoney(rs.getDouble("FMoney"));
				// trdRela.setBaseCuryMoney(YssD.mul(trdRela.getMoney(),
				// trdRela.getBaseCuryRate()));
				trdRela.setBaseCuryMoney(rs.getDouble("FBaseCuryMoney"));
				// trdRela.setPortCuryMoney(YssD.div(trdRela.getBaseCuryMoney(),
				// trdRela.getPortCuryRate()));
				trdRela.setPortCuryMoney(rs.getDouble("FPortCuryMoney"));
				trdRela.setPortCode(rs.getString("FPortCode"));
				trdRela.setSettleState(rs.getInt("FSettleState"));
				trdRela.setStorageAmount(rs.getDouble("FStorageAmount"));
				trdRela.setTransDate(dWorkDay);
				trdRela.setTsfTypeCode(rs.getString("FTsfTypeCode"));
				trdRela.setBrokerCode(rs.getString("FBrokerCode"));
				hmStock.put(trdRela.getNum() + "\t" + rs.getString("FBrokerCode"), trdRela);
			}
			
			dbl.closeResultSetFinal(rs);
			
			//---add by songjie 2012.12.08 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
			Object o = null;
			strSql = " select * from " + pub.yssGetTableName("TB_Data_FutTradeRela") +
			" a where a.FTSFTYPECODE = " + dbl.sqlString(tsfTypeCode4) + " and a.FTransDate = " + dbl.sqlDate(YssFun.addDay(dWorkDay, -1)) +
			" AND a.FSettleState = 1 AND a.FPortCode = " + dbl.sqlString(sPortCode) + " AND a.FStorageAmount <> 0 AND A.FCLOSENUM = ' '";
			rs = dbl.queryByPreparedStatement(strSql); 
			while (rs.next()) {
				o = hmStock.get(rs.getString("FNum") + "\t" + rs.getString("FBrokerCode"));
				if(o != null){
					trdRela = (FuturesTradeRelaBean)o;
					trdRela.setValMoney(rs.getDouble("FMoney"));//估值增值余额（原币）
					trdRela.setValBaseMoney(rs.getDouble("FBaseCuryMoney"));//估值增值余额（基础货币）
					trdRela.setValPortMoney(rs.getDouble("FPortCuryMoney"));//估值增值余额（本位币）
					hmStock.put(rs.getString("FNum") + "\t" + rs.getString("FBrokerCode"), trdRela);
				}
			}
			//---add by songjie 2012.12.08 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
		} catch (Exception ex) {
			throw new YssException("获取期货昨日成本出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return hmStock;
	}
	
	/**
	 * 获取期货昨日库存
	 * 
	 * @param dWorkDay
	 *            Date
	 * @param sPortCode
	 *            String
	 * @return HashMap
	 * @throws YssException
	 */
	private HashMap getYesterdayStock(java.util.Date dWorkDay, String sPortCode, String tsfTypeCode)
			throws YssException {
		ResultSet rs = null;
		String strSql = "";
		HashMap hmStock = new HashMap();
		FuturesTradeRelaBean trdRela = null;
		try {
			strSql = "SELECT a.*, b.FPrice, d.FBaseRate, d.FPortRate, e.FMultiple FROM "
					+ pub.yssGetTableName("TB_Data_FutTradeRela")
					+ " a"
					+ " LEFT JOIN "
					+ pub.yssGetTableName("Tb_Data_ValMktPrice")
					+ " b"
					+ " ON a.FPortCode = b.FPortCode AND a.FTransDate = b.FValDate AND a.FNum = b.FSecurityCode"
					+ " LEFT JOIN "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " c"
					+ " ON a.FNum = c.FSecurityCode"
					+ " LEFT JOIN "
					+ pub.yssGetTableName("Tb_Data_ValRate")
					+ " d"
					+ " ON a.FPortCode = d.FPortCode AND a.FTransDate = d.FValDate AND c.FTradeCury = d.FCuryCode"
					+ " LEFT JOIN "
					+ pub.yssGetTableName("Tb_Para_IndexFutures")
					+ " e"
					+ " ON a.FNum = e.FSecurityCode"
					+ " WHERE a.FTSFTYPECODE = " 
					+ dbl.sqlString(tsfTypeCode)   // modify by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A 
					+ " AND a.FTransDate = "
					+ dbl.sqlDate(YssFun.addDay(dWorkDay, -1))
					+ " AND a.FSettleState = 1"
					+ " AND a.FPortCode = "
					+ dbl.sqlString(sPortCode) + " AND a.FStorageAmount > 0 AND A.FCLOSENUM = ' '";//edit by yanghaiming 20101112 QDV4深圳赢时胜2010年9月28日01_A
			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				trdRela = new FuturesTradeRelaBean();
				trdRela.setNum(rs.getString("FNum"));
				trdRela.setBailMoney(rs.getDouble("FBailMoney"));
				trdRela.setBaseCuryRate(rs.getDouble("FBaseRate"));
				trdRela.setPortCuryRate(rs.getDouble("FPortRate"));
				// trdRela.setMoney(YssD.mul(rs.getDouble("FStorageAmount"),
				// rs.getDouble("FPrice"), rs.getDouble("FMultiple")));
				trdRela.setMoney(rs.getDouble("FMoney"));
				// trdRela.setBaseCuryMoney(YssD.mul(trdRela.getMoney(),
				// trdRela.getBaseCuryRate()));
				trdRela.setBaseCuryMoney(rs.getDouble("FBaseCuryMoney"));
				// trdRela.setPortCuryMoney(YssD.div(trdRela.getBaseCuryMoney(),
				// trdRela.getPortCuryRate()));
				trdRela.setPortCuryMoney(rs.getDouble("FPortCuryMoney"));
				trdRela.setPortCode(rs.getString("FPortCode"));
				trdRela.setSettleState(rs.getInt("FSettleState"));
				trdRela.setStorageAmount(rs.getDouble("FStorageAmount"));
				trdRela.setTransDate(dWorkDay);
				trdRela.setTsfTypeCode(rs.getString("FTsfTypeCode"));
				trdRela.setBrokerCode(rs.getString("FBROKERCODE"));//modify by fangjiang 2012.10.19
				hmStock.put(trdRela.getNum() + "\t" + trdRela.getBrokerCode(), trdRela); //modify by fangjiang 2012.10.19
			}
		} catch (Exception ex) {
			throw new YssException("获取期货昨日成本出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return hmStock;
	}

	/**
	 * 获取期货今日库存 使用移动加权平均法计算，获取和计算今日成本，= 昨日成本+今日成本 add by songjie 2012.12.07
	 * STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
	 * @param hmStock
	 *            HashMap
	 * @param dWorkDay
	 *            Date
	 * @param sPortCode
	 *            String
	 * tsfTypeCode1  05FU  成本
	 * tsfTypeCode2  02FU  平仓收益
	 * tsfTypeCode3  19FU  卖出估值增值
	 * tsfTypeCode4  09FU  估值增值
	 * @throws YssException
	 */
	private HashMap getTodayStockOne(java.util.Date dWorkDay, String sPortCode, 
			String tsfTypeCode1, String subCatCode, String tsfTypeCode2, String tsfTypeCode3, String tsfTypeCode4)
			throws YssException {
		StringBuffer bufSql = new StringBuffer();
		FuturesTradeRelaBean trdRela = null;
		FuturesTradeRelaBean tradeReal = null;
		ResultSet rs = null;
		HashMap hmStock = null;
		EachRateOper eachOper = new EachRateOper();
		eachOper.setYssPub(pub);
		FuturesTradeRelaBean trdRelaIncome = null;
		FuturesTradeRelaBean tradeRela = null;
		FuturesTradeRelaBean sellRela = null;
		String sSecurityCode = "";
		double cost = 0;
		String strSql = "";
		double dbStorageAmount = 0;
		double dbStorageMoney = 0;
		double dbStorageBaseCuryMoney = 0;
		double dbStoragePortCuryMoney = 0;
		double sellValM = 0;
		double sellValMB = 0;
		double sellValMP = 0;
		try {
			hmStock = getYesterdayStockOne(dWorkDay, sPortCode, tsfTypeCode1,tsfTypeCode4);// 从期货交易数据关联表中获取昨日库存数据
			bufSql.append(" SELECT cs.*, b.FTradeCury, c.FPortCury, d.FSubCatCode, d.FMultiple FROM (");
			bufSql.append(" SELECT FNUM, FSecurityCode, FPortCode, FBROKERCODE, FOperType, FTradeTypeCode,FTradePrice,FBaseCuryRate,FPortCuryRate, ");
			bufSql.append(" (case when FTradeTypeCode = '01' then FBegBailMoney else -FBegBailMoney end) as FBegBailMoney,");
			bufSql.append(" (case when FTradeTypeCode = '01' then FTradeAmount else -FTradeAmount end) as FTradeAmount, ");
			bufSql.append(" (case when FTradeTypeCode = '01' then FTradeMoney else -FTradeMoney end) as FTradeMoney, ");
			bufSql.append(" (case when FTradeTypeCode = '01' then round(FTradeMoney * FBaseCuryRate, 2) ");
			bufSql.append(" else round(-FTradeMoney * FBaseCuryRate, 2) end) as FBaseCuryMoney, ");
			bufSql.append(" (case when FTradeTypeCode = '01' then round(FTradeMoney * FBaseCuryRate / FPortCuryRate, 2) ");
			bufSql.append(" else round(-FTradeMoney * FBaseCuryRate / FPortCuryRate, 2) end) as FPortCuryMoney ");
			bufSql.append(" FROM ").append(pub.yssGetTableName("TB_DATA_FutTradeSplit"));
			bufSql.append(" WHERE FBargainDate = ").append(dbl.sqlDate(dWorkDay));
			bufSql.append(" AND FCheckState = 1 ");
			bufSql.append(" AND FPortCode = ").append(dbl.sqlString(sPortCode));// 在期货交易数据拆分表中查找当日开仓数据 做汇总处理
			bufSql.append(" ) cs");
			bufSql.append(" LEFT JOIN (select FSecurityCode, FTradeCury from ");
			bufSql.append(pub.yssGetTableName("Tb_Para_Security"));
			bufSql.append(" ) b ON cs.FSecurityCode = b.FSecurityCode");
			bufSql.append(" LEFT JOIN (select FPortCode, FPortName, FPortCury from ");
			bufSql.append(pub.yssGetTableName("Tb_Para_Portfolio"));
			bufSql.append(" where  FCheckState = 1 ) c ON cs.FPortCode = c.FPortCode ");
			bufSql.append(" join ( select * from ");
			bufSql.append(pub.yssGetTableName("Tb_Para_Indexfutures"));
			bufSql.append(" where fcheckstate = 1 and FSubCatCode = ");
			bufSql.append(dbl.sqlString(subCatCode));
			bufSql.append(" ) d on cs.FSecurityCode = d.FSecurityCode");
			bufSql.append(" order by cs.FNum");			
			rs = dbl.queryByPreparedStatement(bufSql.toString());
			while (rs.next()) {
				dbStorageAmount = 0;
				dbStorageMoney = 0;
				dbStorageBaseCuryMoney = 0;
				dbStoragePortCuryMoney = 0;
				sellValM = 0;
				sellValMB = 0;
				sellValMP = 0;
				
				trdRela = (FuturesTradeRelaBean) hmStock.get(rs.getString("FSecurityCode") + "\t" + rs.getString("FBrokerCode"));
				tradeReal = new FuturesTradeRelaBean();

				if (rs.getString("FOperType").equals("20")){//开仓
					if (trdRela == null) {
						trdRela = new FuturesTradeRelaBean();
						trdRela.setNum(rs.getString("FSecurityCode"));
						trdRela.setBailMoney(rs.getDouble("FBegBailMoney"));// 初始保证金金额
						trdRela.setBaseCuryMoney(rs.getDouble("FBaseCuryMoney"));// 基础货币成交金额
						trdRela.setBaseCuryRate(this.getSettingOper().getCuryRate(dWorkDay, rs.getString("FTradeCury"),
								rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE));// 基础汇率
						trdRela.setMoney(rs.getDouble("FTradeMoney"));// 原币成交金额
						trdRela.setPortCode(rs.getString("FPortCode"));
						trdRela.setPortCuryMoney(rs.getDouble("FPortCuryMoney"));// 本位币成交金额
						eachOper.getInnerPortRate(dWorkDay, rs.getString("FPortCury"), rs.getString("FPortCode"));
						trdRela.setPortCuryRate(eachOper.getDPortRate());// 组合汇率
						trdRela.setSettleState(1);
						trdRela.setStorageAmount(rs.getDouble("FTradeAmount"));// 当日开仓库存数量
						trdRela.setTransDate(dWorkDay);
						trdRela.setTsfTypeCode(tsfTypeCode1);
						trdRela.setBrokerCode(rs.getString("FBrokerCode"));
					}else{
						trdRela.setBailMoney(YssD.add(trdRela.getBailMoney(), rs.getDouble("FBegBailMoney")));
						trdRela.setBaseCuryMoney(YssD.add(trdRela.getBaseCuryMoney(), rs.getDouble("FBaseCuryMoney")));
						trdRela.setBaseCuryRate(this.getSettingOper().getCuryRate(dWorkDay, rs.getString("FTradeCury"),
								rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE));
					
						eachOper.getInnerPortRate(dWorkDay, rs.getString("FTradeCury"), rs.getString("FPortCode"));
					
						trdRela.setPortCuryRate(eachOper.getDPortRate());
						trdRela.setMoney(YssD.add(trdRela.getMoney(), rs.getDouble("FTradeMoney")));
						trdRela.setPortCuryMoney(YssD.add(trdRela.getPortCuryMoney(), rs.getDouble("FPortCuryMoney")));
						trdRela.setStorageAmount(YssD.add(trdRela.getStorageAmount(), rs.getDouble("FTradeAmount")));
						trdRela.setTransDate(dWorkDay);
						trdRela.setBrokerCode(rs.getString("FBrokerCode"));
					}
					
					//----------------------------------------计算 开仓成本 start--------------------------------------//
					tradeReal.setNum(rs.getString("FSecurityCode"));
					tradeReal.setCloseNum(rs.getString("FNUM"));
					tradeReal.setBailMoney(rs.getDouble("FBegBailMoney"));
					tradeReal.setBaseCuryMoney(rs.getDouble("FBaseCuryMoney"));
					tradeReal.setBaseCuryRate(this.getSettingOper().getCuryRate(dWorkDay, rs.getString("FTradeCury"),
						rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE));
					tradeReal.setMoney(rs.getDouble("FTradeMoney"));
					tradeReal.setPortCode(rs.getString("FPortCode"));
					tradeReal.setPortCuryMoney(rs.getDouble("FPortCuryMoney"));
					tradeReal.setPortCuryRate(eachOper.getDPortRate());
					tradeReal.setSettleState(1);
					tradeReal.setStorageAmount(rs.getDouble("FTradeAmount"));
					tradeReal.setTransDate(dWorkDay);
					tradeReal.setTsfTypeCode(tsfTypeCode1);
					tradeReal.setBrokerCode(rs.getString("FBrokerCode"));
					if (!trdRela.getCloseNum().equalsIgnoreCase(" ")) {
						hmStock.put(tradeReal.getCloseNum(), tradeReal);
					}
					//----------------------------------------计算 开仓成本 end--------------------------------------//
				}else{//平仓
					if(trdRela == null){
						throw new YssException("【" + rs.getString("FSecurityCode") + "】没有持仓数据，不能做平仓处理！");
					}
					
					tradeRela = new FuturesTradeRelaBean();
					trdRelaIncome = new FuturesTradeRelaBean();
					
					tradeRela.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
					tradeRela.setNum(rs.getString("FSecurityCode"));
					tradeRela.setPortCode(rs.getString("FPortCode"));
					tradeRela.setPortCuryRate(rs.getDouble("FPortCuryRate"));
					tradeRela.setSettleState(1);
					tradeRela.setTransDate(dWorkDay);
					tradeRela.setTsfTypeCode(tsfTypeCode1);
					tradeRela.setCloseNum(rs.getString("FNum"));
					tradeRela.setBrokerCode(rs.getString("FBrokerCode"));
					//-------------------------------------计算平仓收益 start------------------------------------------//
					trdRelaIncome.setNum(rs.getString("FSecurityCode"));
					trdRelaIncome.setCloseNum(rs.getString("FNum"));
					trdRelaIncome.setBailMoney(rs.getDouble("FBegBailMoney"));
					trdRelaIncome.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
					trdRelaIncome.setPortCuryRate(rs.getDouble("FPortCuryRate"));
					trdRelaIncome.setPortCode(rs.getString("FPortCode"));
					trdRelaIncome.setSettleState(1);
					trdRelaIncome.setStorageAmount(rs.getDouble("FTradeAmount"));
					trdRelaIncome.setTsfTypeCode(tsfTypeCode2); // modify by fangjiang 2010.08.23 QDV4博时2010年7月14日02_A
					trdRelaIncome.setTransDate(dWorkDay);
					trdRelaIncome.setBrokerCode(rs.getString("FBrokerCode"));
					
					// 平仓成本 = round（实时库存成本/实时库存数量*平仓数量,2)）
					if(trdRela.getStorageAmount() != 0){
						cost = YssD.round(
								          YssD.mul(
								                   YssD.div(trdRela.getMoney(), 
								                		    trdRela.getStorageAmount()
								                		    ), 
								                   rs.getDouble("FTradeAmount")
								                   )
								          , 2);
					}
					
					if (trdRela.getStorageAmount() > 0) {// 若上笔的实时库存数量 > 0
						// 平仓收益 =平仓价格 * 放大倍数 * 平仓数量 - 平仓成本
						trdRelaIncome.setMoney(YssD.sub(
								                        YssD.mul(rs.getDouble("FTradePrice"), 
								                        		 rs.getDouble("FMultiple"), 
								                        		 Math.abs(rs.getDouble("FTradeAmount"))
								                        		 ), 
								                        Math.abs(cost)
								                        )
								               );
					} else {// 若上笔的实时库存数量 < 0
						// 空头平仓收益 = 平仓成本  - 平仓价格 * 放大倍数 * 平仓数量
						trdRelaIncome.setMoney(YssD.sub(Math.abs(cost), 
								                        YssD.mul(rs.getDouble("FTradePrice"), 
								                        		 rs.getDouble("FMultiple"), 
								                        		 Math.abs(rs.getDouble("FTradeAmount"))
								                        		 )
								                        )
								               );
					}
					// 基础平仓收益
					trdRelaIncome.setBaseCuryMoney(this.getSettingOper().calBaseMoney(trdRelaIncome.getMoney(),
							trdRelaIncome.getBaseCuryRate()));
					// 组合货币平仓收益
					trdRelaIncome.setPortCuryMoney(this.getSettingOper().calPortMoney(trdRelaIncome.getMoney(),
							trdRelaIncome.getBaseCuryRate(), trdRelaIncome.getPortCuryRate(), rs.getString("FTradeCury"),
							dWorkDay, rs.getString("FPortCode")));
					
					//-------------------------------------计算平仓收益 end------------------------------------------//
					
					//----------------------------------计算卖出估值增值  start--------------------------------------------------//
					sellRela = new FuturesTradeRelaBean();
					sellRela.setNum(rs.getString("FSecurityCode"));
					sellRela.setCloseNum(rs.getString("FNum"));
					sellRela.setPortCode(rs.getString("FPortCode"));
					sellRela.setTransDate(dWorkDay);
					sellRela.setTsfTypeCode(tsfTypeCode3);// 19FU - 卖出估值增值
					sellRela.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
					sellRela.setPortCuryRate(rs.getDouble("FPortCuryRate"));
					sellRela.setSettleState(1);
					sellRela.setBrokerCode(rs.getString("FBrokerCode"));

					//平仓估值增值（原币） = 估值增值实时余额（原币） * （（取绝对值）交易数量 / 实时库存数量）
					sellValM = YssD.mul(trdRela.getValMoney(), 
							            Math.abs(YssD.div(rs.getDouble("FTradeAmount"), 
							            		          trdRela.getStorageAmount()
							            		          )
							            		 )
							            );
					//平仓估值增值（基础货币） = 估值增值实时余额（基础货币） * （（取绝对值）交易数量 / 实时库存数量）
					sellValMB = YssD.mul(trdRela.getValBaseMoney(), 
				                         Math.abs(YssD.div(rs.getDouble("FTradeAmount"), 
		            		                               trdRela.getStorageAmount()
		            		                               )
		            		                      )
		                                 );
					
					//平仓估值增值（本位币） = 估值增值实时余额（本位币） * （（取绝对值）交易数量 / 实时库存数量）
					sellValMP = YssD.mul(trdRela.getValPortMoney(), 
                            			 Math.abs(YssD.div(rs.getDouble("FTradeAmount"), 
                            							   trdRela.getStorageAmount()
                                                           )
                                                  )
                                         );
					
					sellRela.setStorageAmount(rs.getDouble("FTradeAmount"));
					sellRela.setMoney(YssD.round(sellValM, 2));
					sellRela.setBaseCuryMoney(YssD.round(sellValMB,2));
					sellRela.setPortCuryMoney(YssD.round(sellValMP,2));
					//----------------------------------计算卖出估值增值  end--------------------------------------------------//
					
					//-------------------------------------计算平仓成本 start----------------------------------------//
					if(trdRela.getStorageAmount() != 0){
						// 平仓原币成本= round(上笔实时库存原币成本/上笔实时库存数量 * 交易数量,2)
						tradeRela.setMoney(cost);
						// 平仓基础货币成本= round(上笔实时库存基础货币成本/上笔实时库存数量 * 交易数量,2)
						tradeRela.setBaseCuryMoney(YssD.round(
							                                  YssD.mul(
							                            		       YssD.div(trdRela.getBaseCuryMoney(), 
							                            				        trdRela.getStorageAmount()
							                            				        ), 
							                            		       rs.getDouble("FTradeAmount")
							                            		       )
							                                  , 2)
							                       );
						// 平仓本位币成本= round(上笔实时库存本位币成本/上笔实时库存数量 * 交易数量,2)
						tradeRela.setPortCuryMoney(YssD.round(
							                              	  YssD.mul(
							                            		       YssD.div(trdRela.getPortCuryMoney(), 
							                            				        trdRela.getStorageAmount()
							                            				        ), 
							                            	           rs.getDouble("FTradeAmount")
							                            	           )
							                                  , 2)
							                   		);
					}
					
					tradeRela.setStorageAmount(rs.getDouble("FTradeAmount"));
					tradeRela.setBailMoney(rs.getDouble("FBegBailMoney"));
					
					// 如果平仓数量不等于库存数量
					if (trdRela.getStorageAmount() + rs.getDouble("FTradeAmount") != 0) {
						trdRela.setBailMoney(YssD.add(trdRela.getBailMoney(), tradeRela.getBailMoney()));
						trdRela.setMoney(YssD.add(trdRela.getMoney(), tradeRela.getMoney()));
						trdRela.setBaseCuryMoney(YssD.add(trdRela.getBaseCuryMoney(),tradeRela.getBaseCuryMoney()));
						trdRela.setPortCuryMoney(YssD.add(trdRela.getPortCuryMoney(), tradeRela.getPortCuryMoney()));
						trdRela.setStorageAmount(YssD.add(trdRela.getStorageAmount(), tradeRela.getStorageAmount()));
					} else { // 如果平仓数量等于库存数量
						trdRela.setBailMoney(0);
						trdRela.setMoney(0);
						trdRela.setBaseCuryMoney(0);
						trdRela.setPortCuryMoney(0);
						trdRela.setStorageAmount(0);
					}
					
					trdRela.setValMoney(YssD.sub(trdRela.getValMoney(),YssD.round(sellValM, 2)));
					trdRela.setValBaseMoney(YssD.sub(trdRela.getValBaseMoney(),YssD.round(sellValMB, 2)));
					trdRela.setValPortMoney(YssD.sub(trdRela.getValPortMoney(),YssD.round(sellValMP, 2)));
					
					// key - 证券代码 + 期货交易数据拆分表编号 + 调拨子类型（05FU01）， value - 交易成本
					hmStock.put(tradeRela.getNum() + "\t" + tradeRela.getCloseNum() + "\t" + 
							    tradeRela.getTsfTypeCode(),tradeRela);
					//-------------------------------------计算平仓成本 end----------------------------------------//
				}
				
				hmStock.put(trdRela.getNum() + "\t" + rs.getString("FBrokerCode"), trdRela);
				
				// key - 证券代码 + 期货交易数据拆分表编号 + 02FU 
				// value - 平仓收益数据
				if(trdRelaIncome != null){
					hmStock.put(trdRelaIncome.getNum() + "\t" + trdRelaIncome.getCloseNum() + 
							"\t" + trdRelaIncome.getTsfTypeCode(), trdRelaIncome);// 平仓收益
				}
				
				// key - 证券代码 + 期货交易数据拆分表编号 + 19FU
				// value - 卖出估值增值
				if (sellRela != null) {
					hmStock.put(sellRela.getNum() + "\t" + sellRela.getCloseNum() + 
							"\t" + sellRela.getTsfTypeCode(), sellRela);//卖出估值增值
				}
			}
		} catch (Exception ex) {
			throw new YssException("统计今日成本出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return hmStock;
	}

	/**
	 * 获取期货今日库存 使用移动加权平均法计算，获取和计算今日成本，= 昨日成本+今日成本
	 * 
	 * @param hmStock
	 *            HashMap
	 * @param dWorkDay
	 *            Date
	 * @param sPortCode
	 *            String
	 * @throws YssException
	 */
	private HashMap getTodayStock(java.util.Date dWorkDay, String sPortCode,String tsfTypeCode,String subCatCode)
			throws YssException {
		StringBuffer bufSql = new StringBuffer();
		FuturesTradeRelaBean trdRela = null;
		FuturesTradeRelaBean tradeReal = null;//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A
		ResultSet rs = null;
		HashMap hmStock = null;
		EachRateOper eachOper = new EachRateOper();
		eachOper.setYssPub(pub); // ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A
									// 20090421 更改汇率获取方式 by leeyu --
		try {
			hmStock = getYesterdayStock(dWorkDay, sPortCode, tsfTypeCode);
			//edit by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A  修改sql，加上FNUM字段
			bufSql.append(" SELECT cs.*, b.FTradeCury, c.FPortCury, d.FSubCatCode FROM ("); // add d.FSubCatCode by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A 
			bufSql
					.append(" SELECT FNUM, FSecurityCode, FPortCode, SUM(FTradeAmount) AS FTradeAmount, fbrokercode,");
			bufSql.append(" SUM(FTradeMoney) AS FTradeMoney, ");
			bufSql.append(" SUM(FBaseCuryMoney) AS FBaseCuryMoney, ");
			bufSql.append(" SUM(FPortCuryMoney) AS FPortCuryMoney, ");
			bufSql.append(" SUM(FBegBailMoney) AS FBegBailMoney ");
			bufSql
					.append(" FROM (SELECT FNUM, FSecurityCode, FPortCode, FTradeAmount, FTradeMoney, FBegBailMoney,");
			bufSql.append(" round(FTradeMoney * FBaseCuryRate, 2) AS FBaseCuryMoney, "); //modify by fangjiang BUG 2787 2011.10.14
			bufSql
					.append(" round(FTradeMoney * FBaseCuryRate / FPortCuryRate, 2) AS FPortCuryMoney, fbrokercode"); //modify by fangjiang BUG 2787 2011.10.14
			bufSql.append(" FROM ").append(
					//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
					pub.yssGetTableName("TB_Data_FuturesTrade_Tmp"));
			bufSql.append(" WHERE FBargainDate = ").append(
					dbl.sqlDate(dWorkDay));
			bufSql.append(" AND FCheckState = 1 ");
			bufSql.append(" AND FPortCode = ").append(dbl.sqlString(sPortCode));
			bufSql.append(" AND FTradeTypeCode = '20') a ");
			bufSql.append(" GROUP BY a.FSecurityCode, a.FPortCode, a.fbrokercode, a.FNUM) cs");
			bufSql.append(" LEFT JOIN (select FSecurityCode, FTradeCury from ");
			bufSql.append(pub.yssGetTableName("Tb_Para_Security"));
			bufSql.append(" ) b ON cs.FSecurityCode = b.FSecurityCode");
			// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
			// modify by fangjiang 2010.08.20 选取启用日期最大的组合
			/*bufSql.append(" LEFT JOIN (SELECT db.* FROM (select FPortCode, max(FStartDate) as FStartDate from ");
			bufSql.append(pub.yssGetTableName("Tb_Para_Portfolio"));
			bufSql.append(" where FStartDate <= ");
			bufSql.append(dbl.sqlDate(new java.util.Date()).toString());
			bufSql.append(" and FCheckState = 1 group by FPortCode) da join (select FPortCode, FPortName, FStartDate, FPortCury from ");
			bufSql.append(pub.yssGetTableName("Tb_Para_Portfolio"));
			bufSql.append(") db on da.FPortCode = db.FPortCode and da.FStartDate = db.FStartDate) c ON cs.FPortCode = c.FPortCode ");
		*/
			
			bufSql.append(" LEFT JOIN (select FPortCode, FPortName, FPortCury from ");
			bufSql.append(pub.yssGetTableName("Tb_Para_Portfolio"));
			bufSql.append(" where  FCheckState = 1 ) c ON cs.FPortCode = c.FPortCode ");
		
			
			// end by lidaolong
			//----------------------------------
			// add by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A 
			bufSql.append(" join ( select * from " );
			bufSql.append(pub.yssGetTableName("Tb_Para_Indexfutures"));
			bufSql.append(" where fcheckstate = 1 and FSubCatCode = " );
		    bufSql.append(dbl.sqlString(subCatCode));
		    bufSql.append( " ) d on cs.FSecurityCode = d.FSecurityCode" );
			//-------------------
			rs = dbl.queryByPreparedStatement(bufSql.toString()); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				trdRela = (FuturesTradeRelaBean) hmStock.get(rs
						.getString("FSecurityCode") + "\t" + rs.getString("fbrokercode")); //modify by fangjiang 2012.10.19
				tradeReal = new FuturesTradeRelaBean();
				if (trdRela == null) {
					trdRela = new FuturesTradeRelaBean();
					trdRela.setNum(rs.getString("FSecurityCode"));
					trdRela.setBailMoney(rs.getDouble("FBegBailMoney"));
					trdRela.setBaseCuryMoney(rs.getDouble("FBaseCuryMoney"));
					trdRela.setBaseCuryRate(this.getSettingOper().getCuryRate(
							dWorkDay, rs.getString("FTradeCury"),
							rs.getString("FPortCode"),
							YssOperCons.YSS_RATE_BASE));
					trdRela.setMoney(rs.getDouble("FTradeMoney"));
					trdRela.setPortCode(rs.getString("FPortCode"));
					trdRela.setPortCuryMoney(rs.getDouble("FPortCuryMoney"));
					// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090421 更改汇率获取方式
					// by leeyu --
					// trdRela.setPortCuryRate(this.getSettingOper().getCuryRate(
					// dWorkDay, rs.getString("FPortCury"),
					// rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT));
					eachOper.getInnerPortRate(dWorkDay, rs
							.getString("FPortCury"), rs.getString("FPortCode"));
					trdRela.setPortCuryRate(eachOper.getDPortRate());
					// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090421 更改汇率获取方式
					// by leeyu --
					trdRela.setSettleState(1);
					trdRela.setStorageAmount(rs.getDouble("FTradeAmount"));
					trdRela.setTransDate(dWorkDay);
					trdRela.setTsfTypeCode(tsfTypeCode); // modify by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A 
					trdRela.setBrokerCode(rs.getString("fbrokercode")); //modify by fangjiang 2012.10.19
				} else {
					trdRela.setBailMoney(YssD.add(trdRela.getBailMoney(), rs
							.getDouble("FBegBailMoney")));
					trdRela.setBaseCuryMoney(YssD
							.add(trdRela.getBaseCuryMoney(), rs
									.getDouble("FBaseCuryMoney")));
					trdRela.setBaseCuryRate(this.getSettingOper().getCuryRate(
							dWorkDay, rs.getString("FTradeCury"),
							rs.getString("FPortCode"),
							YssOperCons.YSS_RATE_BASE));
					// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090421 更改汇率获取方式
					// by leeyu --
					// trdRela.setPortCuryRate(this.getSettingOper().getCuryRate(
					// dWorkDay, rs.getString("FPortCury"),
					// rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT));
					eachOper
							.getInnerPortRate(dWorkDay, rs
									.getString("FTradeCury"), rs
									.getString("FPortCode"));
					trdRela.setPortCuryRate(eachOper.getDPortRate());
					// ---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A 20090421 更改汇率获取方式
					// by leeyu --
					trdRela.setMoney(YssD.add(trdRela.getMoney(), rs
							.getDouble("FTradeMoney")));
					trdRela.setPortCuryMoney(YssD
							.add(trdRela.getPortCuryMoney(), rs
									.getDouble("FPortCuryMoney")));
					trdRela.setStorageAmount(YssD.add(trdRela
							.getStorageAmount(), rs.getDouble("FTradeAmount")));
					trdRela.setTransDate(dWorkDay);
					trdRela.setBrokerCode(rs.getString("fbrokercode")); //modify by fangjiang 2012.10.19
				}
				hmStock.put(trdRela.getNum()+"\t"+trdRela.getBrokerCode(), trdRela); //modify by fangjiang 2012.10.19
				//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A
				tradeReal.setNum(rs.getString("FSecurityCode"));
				tradeReal.setCloseNum(rs.getString("FNUM"));
				tradeReal.setBailMoney(rs.getDouble("FBegBailMoney"));
				tradeReal.setBaseCuryMoney(rs.getDouble("FBaseCuryMoney"));
				tradeReal.setBaseCuryRate(this.getSettingOper().getCuryRate(
						dWorkDay, rs.getString("FTradeCury"),
						rs.getString("FPortCode"),
						YssOperCons.YSS_RATE_BASE));
				tradeReal.setMoney(rs.getDouble("FTradeMoney"));
				tradeReal.setPortCode(rs.getString("FPortCode"));
				tradeReal.setPortCuryMoney(rs.getDouble("FPortCuryMoney"));
//				eachOper.getInnerPortRate(dWorkDay, rs
//						.getString("FPortCury"), rs.getString("FPortCode"));
				tradeReal.setPortCuryRate(eachOper.getDPortRate());
				tradeReal.setSettleState(1);
				tradeReal.setStorageAmount(rs.getDouble("FTradeAmount"));
				tradeReal.setTransDate(dWorkDay);
				tradeReal.setTsfTypeCode(tsfTypeCode); 
				tradeReal.setBrokerCode(rs.getString("fbrokercode")); //modify by fangjiang 2012.10.19
				if(!trdRela.getCloseNum().equalsIgnoreCase(" ")){
					hmStock.put(tradeReal.getCloseNum(), tradeReal);
				}
				//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A
			}
		} catch (Exception ex) {
			throw new YssException("统计今日成本出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return hmStock;
	}

	/**
	 * 加权平均计算平仓收益, 2009-3-24 蒋锦 添加 MS00273 QDV4中金2009年02月27日01_A
	 * 
	 * @param sPortCode
	 *            String
	 * @param hmTradeRela
	 *            HashMap
	 * @param dWorkDay
	 *            Date
	 * @throws YssException
	 */
	private void getTradeRelaDateByModAvg(String sPortCode,
			HashMap hmTradeRela, java.util.Date dWorkDay, String tsfTypeCode, String subCatCode) throws YssException {
		ResultSet rs = null;
		String sqlStr = "";
		FuturesTradeRelaBean trdRela = null;
		FuturesTradeRelaBean tradeRela = null;//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A 
		FuturesTradeRelaBean trdRelaIncome = null;
		try {
			sqlStr = "SELECT a.*, b.FMultiple, b.FFUType,b.FSubCatCode, c.FTradeCury FROM " //add b.FSubCatCode by fangjiang 2010.08.23
					//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
				    + pub.yssGetTableName("TB_Data_FuturesTrade_Tmp") + " a"
					// modify by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A 
					+ " JOIN ( select FMultiple, FFUType, FSubCatCode, FSecurityCode from "
					+ pub.yssGetTableName("TB_Para_IndexFutures") 
					+ " where FCheckState = 1 and FSubCatCode = "
					+ dbl.sqlString(subCatCode)
					+ " ) b "
					// --------------------
					+ " ON a.FSecurityCode = b.FSecurityCode" + " LEFT JOIN "
					+ pub.yssGetTableName("tb_para_security") + " c"
					+ " ON a.FSecurityCode = c.FSecurityCode"
					+ " WHERE a.FTradeTypeCode = '21'"
					+ " AND a.FBargainDate = " + dbl.sqlDate(dWorkDay)
					+ " AND a.FCheckState = 1" + " AND a.FPortCode = "
					+ dbl.sqlString(sPortCode)
					+ " order by fnum ";
			rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				trdRela = (FuturesTradeRelaBean) hmTradeRela.get(rs
						.getString("FSecurityCode") + "\t" + rs.getString("fbrokercode")); //modify by fangjiang 2012.10.19
				//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A 
				tradeRela = new FuturesTradeRelaBean();
				if (trdRela == null)
				{
					continue;
				}
				tradeRela.setBaseCuryRate(trdRela.getBaseCuryRate());
				tradeRela.setNum(trdRela.getNum());
				tradeRela.setPortCode(trdRela.getPortCode());
				tradeRela.setPortCuryRate(trdRela.getPortCuryRate());
				tradeRela.setSettleState(trdRela.getSettleState());
				tradeRela.setTransDate(trdRela.getTransDate());
				tradeRela.setTsfTypeCode(trdRela.getTsfTypeCode());
				tradeRela.setCloseNum(rs.getString("FNum"));
				tradeRela.setBrokerCode(rs.getString("fbrokercode")); //modify by fangjiang 2012.10.19
				//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A 
				// trdRelaIncome =
				// (FuturesTradeRelaBean)hmTradeRela.get(rs.getString("FSecurityCode")
				// + "\t" + YssOperCons.YSS_ZJDBZLX_FU_Income);
				if (trdRela == null) {
					throw new YssException("无法计算股指期货交易【" + rs.getString("FNum")
							+ "】平仓收益, 没有库存！");
				}
				// if(trdRelaIncome == null){
				trdRelaIncome = new FuturesTradeRelaBean();
				// ------------平仓收益------------//
				trdRelaIncome.setNum(rs.getString("FSecurityCode"));
				trdRelaIncome.setCloseNum(rs.getString("FNum"));
				trdRelaIncome.setBailMoney(rs.getDouble("FBegBailMoney"));
				trdRelaIncome.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
				trdRelaIncome.setPortCuryRate(rs.getDouble("FPortCuryRate"));
				trdRelaIncome.setPortCode(rs.getString("FPortCode"));
				trdRelaIncome.setSettleState(1);
				trdRelaIncome.setStorageAmount(rs.getDouble("FTradeAmount"));
				trdRelaIncome.setTsfTypeCode(tsfTypeCode); // modify by fangjiang 2010.08.23 QDV4博时2010年7月14日02_A 
				trdRelaIncome.setTransDate(dWorkDay);
				trdRelaIncome.setBrokerCode(rs.getString("fbrokercode")); //modify by fangjiang 2012.10.19
				if (rs.getString("FFUType").equalsIgnoreCase("BuyAM")) {
					// xuqiji 20090810:QD招商证券2009年08月07日01_B MS00624 保证金多出两分钱
					// 多头平仓收益 =平仓价格 * 放大倍数 * 平仓数量 -round(成本总金额/库存数量*平仓数量,2)
					trdRelaIncome.setMoney(YssD.sub(YssD.mul(rs
							.getDouble("FTradePrice"), rs
							.getDouble("FMultiple"), rs
							.getDouble("FTradeAmount")), YssD.round(YssD.mul(
							YssD.div(trdRela.getMoney(), trdRela
									.getStorageAmount()), rs
									.getDouble("FTradeAmount")), 2)));
				} else {
					// 空头平仓收益 = round(成本总金额/库存数量*平仓数量,2) - 平仓价格 * 放大倍数 * 平仓数量
					trdRelaIncome.setMoney(YssD.sub(YssD.round(YssD.mul(
							YssD.div(trdRela.getMoney(), trdRela
									.getStorageAmount()), rs
									.getDouble("FTradeAmount")), 2), YssD.mul(
							rs.getDouble("FTradePrice"), rs
									.getDouble("FMultiple"), rs
									.getDouble("FTradeAmount"))));
				}
				// 基础平仓收益
				trdRelaIncome.setBaseCuryMoney(this.getSettingOper()
						.calBaseMoney(trdRelaIncome.getMoney(),
								trdRelaIncome.getBaseCuryRate()));
				// 组合货币平仓收益
				trdRelaIncome.setPortCuryMoney(this.getSettingOper()
						.calPortMoney(trdRelaIncome.getMoney(),
								trdRelaIncome.getBaseCuryRate(),
								trdRelaIncome.getPortCuryRate(),
								rs.getString("FTradeCury"), dWorkDay,
								rs.getString("FPortCode")));

				// ------------------更新成本--------------------//
				// 如果平仓数量不等于库存数量
				if (trdRela.getStorageAmount() != rs.getDouble("FTradeAmount")) {
					//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A 
					tradeRela.setMoney(YssD.round(
						YssD.mul(YssD.div(trdRela.getMoney(), trdRela
								.getStorageAmount()), rs
								.getDouble("FTradeAmount")), 2));
					tradeRela.setBaseCuryMoney(YssD.round(YssD.mul(YssD.div(
							trdRela.getBaseCuryMoney(), trdRela.getStorageAmount()), rs.getDouble("FTradeAmount")), 2));
					tradeRela.setPortCuryMoney(YssD.round(YssD.mul(YssD.div(
							trdRela.getPortCuryMoney(), trdRela.getStorageAmount()), rs.getDouble("FTradeAmount")), 2));
					//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A 
					// xuqiji 20090422 MS00396 股指期货计算移动加权收益及估值增值错误 ----------//
					trdRela
							.setBailMoney(YssD.round(YssD.sub(trdRela
									.getBailMoney(), rs
									.getDouble("FBegBailMoney")), 2));
					// -------edit by xuqiji 20090429：QDV4招商证券2009年04月28日01_B
					// MS00418
					// 股指期货交易23日估值净值统计表中多出1分钱------------------------------//

					// -------算法：今日原币库存成本=昨日原币库存成本-（昨日原币库存成本/昨日库存数量*今天交易数量）---------//
					trdRela.setMoney(YssD.sub(trdRela.getMoney(), YssD.round(
							YssD.mul(YssD.div(trdRela.getMoney(), trdRela
									.getStorageAmount()), rs
									.getDouble("FTradeAmount")), 2)));
					// -------算法：今日基础货币库存成本=昨日基础货币库存成本-（昨日基础货币库存成本/昨日库存数量*今天交易数量）---------//
					trdRela.setBaseCuryMoney(YssD.sub(trdRela
							.getBaseCuryMoney(), YssD.round(YssD.mul(YssD.div(
							trdRela.getBaseCuryMoney(), trdRela
									.getStorageAmount()), rs
							.getDouble("FTradeAmount")), 2)));
					// -------算法：今日组合货币库存成本=昨日组合货币库存成本-（昨日组合货币库存成本/昨日库存数量*今天交易数量）---------//
					trdRela.setPortCuryMoney(YssD.sub(trdRela
							.getPortCuryMoney(), YssD.round(YssD.mul(YssD.div(
							trdRela.getPortCuryMoney(), trdRela
									.getStorageAmount()), rs
							.getDouble("FTradeAmount")), 2)));
					// -------------------------------end 2009
					// 0429-----------------------------------------------------------------------------------------//
					trdRela.setStorageAmount(YssD.sub(trdRela
							.getStorageAmount(), rs.getDouble("FTradeAmount")));
					// -------------------------------end
					// 20090810----------------------------//
					
				} else { // 如果平仓数量等于库存数量
					//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A
					tradeRela.setMoney(trdRela.getMoney());
					tradeRela.setBaseCuryMoney(trdRela.getBaseCuryMoney());
					tradeRela.setPortCuryMoney(trdRela.getPortCuryMoney());
					//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A 
					trdRela.setBailMoney(0);
					trdRela.setMoney(0);
					trdRela.setBaseCuryMoney(0);
					trdRela.setPortCuryMoney(0);
					trdRela.setStorageAmount(0);
				}
				hmTradeRela.put(trdRelaIncome.getNum() + "\t"
						+ trdRelaIncome.getCloseNum(), trdRelaIncome);
				//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A 
				hmTradeRela.put(tradeRela.getNum() + "\t"
						+ tradeRela.getCloseNum() + "\t" + tradeRela.getTsfTypeCode(), tradeRela);
				//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A 
			}
		} catch (Exception ex) {
			throw new YssException("加权平均计算平仓收益出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 使用先入先出法计算平仓收益
	 * 
	 * @param sPortCode
	 *            String
	 * @param hmTrade
	 *            HashMap
	 * @param hmTradeRela
	 *            HashMap
	 * @param dWorkDay
	 *            Date
	 * @param sAccountType
	 *            String
	 * @throws YssException
	 */
	private void getTradeRelaDateByFIFO(String sPortCode, HashMap hmTrade,
			HashMap hmTradeRela, java.util.Date dWorkDay, 
			String sAccountType, String subCatCode, String tsfTypeCode1,  
			String tsfTypeCode2,  String tsfTypeCode3 )
			throws YssException {
		ResultSet rs = null;
		String strSql = "";
		try {
			// 查询当天的交易记录
			strSql = createSQLForGetFutruesTrade(dWorkDay, sPortCode,
					sAccountType, subCatCode); // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				// 开仓
				// 只获取交易数据
				if (rs.getString("FTradeTypeCode").equalsIgnoreCase("20")) {
					getFuturesOpenTrade(hmTrade, rs, sAccountType);
				}
				// 平仓
				// 计算平仓收益，和经过平仓后开仓交易的剩余库存数量
				else if (rs.getString("FTradeTypeCode").equalsIgnoreCase("21")) {
					closeBusinessProcessing(dWorkDay, hmTrade, hmTradeRela, rs, 
							tsfTypeCode1, tsfTypeCode2, tsfTypeCode3); // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
				}
			}
			dbl.closeResultSetFinal(rs);

		} catch (Exception ex) {
			throw new YssException("先入先出计算平仓收益出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 计算开仓交易的剩余数量、保证金和平仓交易的投资收益 平仓收益使用先入先出法进行计算，计算一条平仓收益时循环之前的开仓交易数据，先开仓先计算
	 * 计算平仓收益的同时得到开仓交易在经过平仓后的剩余库存，最后计算剩余的保证金
	 * modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
	 * modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
	 * @param dWorkDay
	 *            Date
	 * @param sPortCodes
	 *            String
	 * @param alMtvMethod
	 *            ArrayList
	 * @return ArrayList
	 * @throws YssException
	 */
	public ArrayList getOpenTradeAmountAndCloseTradeIncome(
			java.util.Date dWorkDay, String sPortCodes) throws YssException {
		/*String[] arrPortCodes = null;
		// 期货核算方式 MS00321 QDV4招商证券2009年03月17日01_A 2009.03.18 蒋锦 添加
		String sAccountType = "";
		// 存放组合、核算代码对
		Hashtable htPortAccountType = new Hashtable();
		// 存放前一天有库存的交易记录
		HashMap hmTrade = new HashMap();
		// 存放投资收益的交易关联记录
		HashMap hmTradeRela = new HashMap();
		// 方法的最终结果，存放估值增值和投资收益的交易关联数据
		ArrayList alResult = new ArrayList();
		try {
			hmTrade = getFutTradeStock(dWorkDay, sPortCodes);
			// 生成组合代码数组
			arrPortCodes = getPortArrBy(sPortCodes);

			CtlPubPara pubPara = new CtlPubPara();
			pubPara.setYssPub(pub);
//			htPortAccountType = pubPara.getFuturesAccountType();
			htPortAccountType = pubPara.getFurAccountType();//add by xuxuming,20091223.MS00886,无法用不同的方法对不同品种进行核算成本
			for (int iPortIndex = 0; iPortIndex < arrPortCodes.length; iPortIndex++) {
				// 获取核算类型				
				sAccountType = getAccountTypeBy(arrPortCodes[iPortIndex],
						htPortAccountType);
				if (sAccountType
						.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)) {
					// 使用加权平均法计算
					hmTradeRela.putAll(getTodayStock(dWorkDay,
							arrPortCodes[iPortIndex]));
					getTradeRelaDateByModAvg(arrPortCodes[iPortIndex],
							hmTradeRela, dWorkDay);
					getSellAppreciForModAvg(dWorkDay, hmTradeRela,
							arrPortCodes[iPortIndex]);
				} else {
					// 使用先入先出法计算
					getTradeRelaDateByFIFO(arrPortCodes[iPortIndex], hmTrade,
							hmTradeRela, dWorkDay, sAccountType);
					// ====add by xuxuming,20091117.MS00803
					// 股指期货交易，需要把交易记录插入到tb_XXX_data_futtraderela表======
					getTradeRelaToday(arrPortCodes[iPortIndex], hmTradeRela,
							dWorkDay, sAccountType);// 将当天开仓的数据存入hmTradeRela，以保存入交易关联表
					// ============end==========================================
				}
			}
			// 将进行过平仓的开仓交易的剩数量存入交易关联表
			Iterator it = hmTrade.values().iterator();
			while (it.hasNext()) {
				ArrayList alTradeRlut = (ArrayList) it.next();
				for (int i = 0; i < alTradeRlut.size(); i++) {
					FuturesTradeBean trade = (FuturesTradeBean) alTradeRlut
							.get(i);
					FuturesTradeRelaBean tradeRela = new FuturesTradeRelaBean();
					tradeRela.setNum(trade.getNum());
					tradeRela.setPortCode(trade.getPortCode());
					tradeRela.setTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU01_MV);
					tradeRela.setStorageAmount(trade.getCloseAmount());
					// 计算剩余保证金
					if (tradeRela.getStorageAmount() == trade.getTradeAmount()) {
						tradeRela.setBailMoney(trade.getBegBailMoney());
					} else {
						// ==MS00607 计算单位保证金的时候保留两位小数四舍五入
						tradeRela.setBailMoney(YssD.sub(
								trade.getBegBailMoney(), YssD.mul(YssD.round(
										YssD.div(trade.getBegBailMoney(), trade
												.getTradeAmount()), 2), YssD
										.sub(trade.getTradeAmount(), tradeRela
												.getStorageAmount()))));
						// ==End MS00607 QDV4中金2009年7月28日05_B
						// 期货变动保证金组合货币成本不等于组合货币市值 xuqiji 20090804==
					}
					tradeRela.setTransDate(dWorkDay);
					tradeRela.setSettleState(1);
					alResult.add(tradeRela);
				}
			}
			// 将平仓收益存入交易关联表
			it = hmTradeRela.values().iterator();
			while (it.hasNext()) {
				alResult.add((FuturesTradeRelaBean) it.next());
			}
		} catch (Exception e) {
			throw new YssException("计算期货平仓收益出错！\r\n" + e.getMessage());
		}
		return alResult;*/
		// 期货核算方式 MS00321 QDV4招商证券2009年03月17日01_A 2009.03.18 蒋锦 添加
		
		String sPortCode = sPortCodes.replaceAll("'","");
		ArrayList alResult = new ArrayList(); // 方法的最终结果，存放估值增值和投资收益的交易关联数据
		String sAccountType = "";            // 核算类型

		// ---add by songjie 2012.12.07 STORY #3371
		// 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
		boolean contractType = false;
		ParaWithPort para = new ParaWithPort();
		para.setYssPub(pub);
		contractType = para.getFutursPositionType(sPortCode);
		// ---add by songjie 2012.12.07 STORY #3371
		// 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
		for(int j=0; j<4; j++) {    // j=0时处理股指期货，j=1时处理债券期货, j=2时处理外汇期货  , i = 3 时处理商品期货 //modify huangqirong 2012-08-21  商品期货
			try {
				HashMap hmTradeRela = new HashMap(); // 存放投资收益的交易关联记录
				//---add by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
				HashMap hmTrade = null;
				if (contractType) {// 持仓模式：单边
					// 存放前一天有库存的交易记录 09FU 前一日的估值增值余额
					hmTrade= new HashMap();
					//hmTrade = getFutTradeStockOne(dWorkDay, sPortCode, YssOperCons.YSS_ZJDBZLX_FU[j][3]); // 获取开仓的昨日库存数据  可能用不到这段代码
					hmTradeRela.putAll(getTodayStockOne(dWorkDay, sPortCode, 
							YssOperCons.YSS_ZJDBZLX_FU[j][1],YssOperCons.YSS_FU[j],
							YssOperCons.YSS_ZJDBZLX_FU[j][0],YssOperCons.YSS_ZJDBZLX_FU[j][4],
							YssOperCons.YSS_ZJDBZLX_FU[j][3]));// 05FU：计算开仓成本
				} else {// 持仓模式：双边
					// ---add by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
					hmTrade = getFutTradeStock(dWorkDay, sPortCode, YssOperCons.YSS_ZJDBZLX_FU[j][3]); // 存放前一天有库存的交易记录
					CtlPubPara pubPara = new CtlPubPara();
					pubPara.setYssPub(pub);
					Hashtable htPortAccountType = pubPara.getFurAccountType(YssOperCons.YSS_FU_ACCOUT_TYPE[j]); // 存放组合、核算代码对
					sAccountType = getAccountTypeBy(sPortCode, htPortAccountType, j); //期货核算类型
				
					if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)) { // 使用移动加权平均法计算	
						hmTradeRela.putAll(getTodayStock(dWorkDay, sPortCode,
								YssOperCons.YSS_ZJDBZLX_FU[j][1], YssOperCons.YSS_FU[j]));
						getTradeRelaDateByModAvg(sPortCode, hmTradeRela, dWorkDay, 
								YssOperCons.YSS_ZJDBZLX_FU[j][0], YssOperCons.YSS_FU[j]);
						getSellAppreciForModAvg(dWorkDay, hmTradeRela, sPortCode,
								YssOperCons.YSS_ZJDBZLX_FU[j][3], YssOperCons.YSS_ZJDBZLX_FU[j][4]);
					} else {  // 使用先入先出法计算
						getTradeRelaDateByFIFO(sPortCode, hmTrade,	hmTradeRela, dWorkDay, sAccountType, 
								YssOperCons.YSS_FU[j], YssOperCons.YSS_ZJDBZLX_FU[j][0], 
								YssOperCons.YSS_ZJDBZLX_FU[j][3], YssOperCons.YSS_ZJDBZLX_FU[j][4] );
						// 将当天开仓的数据存入hmTradeRela，以保存入交易关联表
						getTradeRelaToday(sPortCode, hmTradeRela, dWorkDay, sAccountType, 
								YssOperCons.YSS_FU[j], YssOperCons.YSS_ZJDBZLX_FU[j][3], 
								YssOperCons.YSS_ZJDBZLX_FU[j][4]);
					}
				}
				// 将进行过平仓的开仓交易的剩余数量存入交易关联表
				Iterator it = hmTrade.values().iterator();
				while (it.hasNext()) {
					ArrayList alTradeRlut = (ArrayList) it.next();
					for (int i = 0; i < alTradeRlut.size(); i++) {
						FuturesTradeBean trade = (FuturesTradeBean) alTradeRlut.get(i);
						FuturesTradeRelaBean tradeRela = new FuturesTradeRelaBean();
						tradeRela.setNum(trade.getNum());
						tradeRela.setPortCode(trade.getPortCode());
						tradeRela.setTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU[j][3]);// 09FU
																					// 估值增值
						tradeRela.setStorageAmount(trade.getCloseAmount());
						// 计算剩余保证金
						if (tradeRela.getStorageAmount() == trade.getTradeAmount()) {
							tradeRela.setBailMoney(trade.getBegBailMoney());
						} else {
							// 剩余保证金 = 交易保证金 - round（交易保证金 / 交易数量，2） * （交易数量 -
							// 当日库存数量）
							tradeRela.setBailMoney(YssD.sub(trade.getBegBailMoney(), YssD.mul(YssD.round(YssD.div(trade
									.getBegBailMoney(), trade.getTradeAmount()), 2), YssD.sub(trade.getTradeAmount(),
									tradeRela.getStorageAmount()))));
						}
						tradeRela.setTransDate(dWorkDay);
						tradeRela.setSettleState(1);
						alResult.add(tradeRela);
					}
				}
				// 将平仓收益存入交易关联表
				it = hmTradeRela.values().iterator();
				while (it.hasNext()) {
					alResult.add((FuturesTradeRelaBean) it.next());
				}
			} catch (Exception e) {
				throw new YssException("计算期货平仓收益出错！\r\n" + e.getMessage());
			}
		}
		
		return alResult;
	}

	/**
	 * 平仓时，当天有开仓，要将当天开仓的数据保存到交易关联表
	 * 只需要保存被平掉的记录，
	 * 如：昨日库存为数量１００，今天开仓两笔：５０，５０.
     * 1.今天平仓　１００，此时　不用保存开仓数据；
 	 * 2.今天平仓150，此时，只用保存第一条开仓数据．
     * 3.今天平仓160，此时，两条开仓数据都要保存,第二笔保存数量为１０．
     * 4.今天平仓200，此时，两条平仓数据都要保存．
     * 以上保存都指：保存02FU01至关联表．
	 * modify by wangzuochun 2010.03.31  MS01023  当天两笔平仓，收益表结转损益不正确  QDV4中金2010年03月14日01_B 
	 * @param sPortCodes
	 *            ,组合代码
	 * @param hmTradeRela
	 *            ，保存交易关联数据
	 * @param dWorkDay
	 *            ，交易日期
	 * @param sAccountType
	 *            , 核算类型
	 * @throws YssException
	 */
	private void getTradeRelaToday(String sPortCodes, HashMap hmTradeRela,
			Date dWorkDay, String sAccountType, String subCatCode, 
			String tsfTypeCode1, String tsfTypeCode2) throws YssException {
		// getFuturesStore(dWorkDay,strPortCode);//得到库存
		// 存放前一天有库存的交易记录
		HashMap hmTrade = new HashMap();
		HashMap hmStock = new HashMap();
		//hmStock = getFutTradeStock(dWorkDay, dbl.sqlString(sPortCodes), tsfTypeCode1);// modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A  得到库存
		hmStock = getFutTradeStock(dWorkDay, sPortCodes, tsfTypeCode1);// modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A  得到库存
		ResultSet rs = null;
		String strSql = "";
		int dbCloseTotal = 0; //------ add by wangzuochun 2010.03.31  MS01023  当天两笔平仓，收益表结转损益不正确  QDV4中金2010年03月14日01_B
		try {
			
			// 查询当天的交易记录
			strSql = createSQLForGetFutruesTrade(dWorkDay, sPortCodes,
					sAccountType, subCatCode); // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			//------ add by wangzuochun 2010.03.31  MS01023  当天两笔平仓，收益表结转损益不正确  QDV4中金2010年03月14日01_B 
		    while (rs.next()){
		    	if (rs.getString("FTradeTypeCode").equalsIgnoreCase("21")) {
		    		dbCloseTotal += (int) rs.getDouble("FTradeAmount");// 平仓的数量
		        }
		    }
		    dbl.closeResultSetFinal(rs);
		    
		    rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
		    //-------------------------MS01023-----------------------//
			while (rs.next()) {
				// 开仓
				// 只获取交易数据
				if (rs.getString("FTradeTypeCode").equalsIgnoreCase("20")) {
					getFuturesOpenTrade(hmTrade, rs, sAccountType);// 获取开仓交易数据
				} else if (rs.getString("FTradeTypeCode")
						.equalsIgnoreCase("21")) {// 平仓
					//-------add by wangzuochun 2010.03.31  MS01023  当天两笔平仓，收益表结转损益不正确  QDV4中金2010年03月14日01_B 
					String strNum = rs.getString("FNum");
					int closeAmount = (int) rs.getDouble("FTradeAmount");// 平仓的数量
					Iterator it = hmTradeRela.values().iterator();
					while (it.hasNext()) {
						FuturesTradeRelaBean fTradeRela = (FuturesTradeRelaBean) it.next();
						String strCloseNum = fTradeRela.getCloseNum();
						if (strNum.equals(strCloseNum)){
							closeAmount -= (int)fTradeRela.getStorageAmount();
						}
					}
					//----------------------------MS01023----------------------//
					
					ArrayList alTrade = null;
					ArrayList alStock = null;
					//int dbCloseAmount = (int) rs.getDouble("FTradeAmount");// 平仓的数量 //modify by wangzuochun 去掉此行 MS01023
					// 取出平仓交易所对应的开仓交易
					alTrade = (ArrayList) hmTrade.get(rs.getString("FPortCode")
							+ "\t" + rs.getString("FSecurityCode"));
					alStock = (ArrayList) hmStock.get(rs.getString("FPortCode")
							+ // 取出库存数据
							"\t" + rs.getString("FSecurityCode"));

					
					int dbAllAmount = 0;// 库存总量
					if (alStock != null && alStock.size() > 0) {
						for (int i = 0; i < alStock.size(); i++) {
							FuturesTradeBean trade = (FuturesTradeBean) alStock
									.get(i);
							dbAllAmount = (int) YssD.add(dbAllAmount, trade
									.getCloseAmount());
						}
					}
					//------modify by wangzuochun 2010.03.31  MS01023  当天两笔平仓，收益表结转损益不正确  QDV4中金2010年03月14日01_B 
					//dbCloseAmount -= dbAllAmount;//优先平掉库存数量	//modify by wangzuochun 去掉此行 MS01023
					
					if (dbCloseTotal - dbAllAmount > 0 && closeAmount != 0) {// 只有当平仓数量比库存数量多时，才需要考虑今天开仓的数量.
						if (alTrade != null && alTrade.size() > 0) {
							for (int i = 0; i < alTrade.size(); i++) {
								FuturesTradeRelaBean tradeRela = new FuturesTradeRelaBean();
								FuturesTradeBean trade = (FuturesTradeBean) alTrade
										.get(i);
								
								//dbCloseAmount -= trade.getCloseAmount();// 减去当天开仓的数量
								//------
								closeAmount -= trade.getCloseAmount();// 减去当天开仓的数量
								//------
								tradeRela.setNum(trade.getNum());
								tradeRela.setCloseNum(rs.getString("FNum"));//add by xuxuming,20091124.将平仓交易的编号保存
								tradeRela.setPortCode(trade.getPortCode());
								tradeRela.setTsfTypeCode(tsfTypeCode2); //modify by fangjiang 2010.08.25  MS01439 QDV4博时2010年7月14日02_A 保存为“19FU01” add by xuxuming,20091124.
								tradeRela.setTransDate(dWorkDay);
								tradeRela.setSettleState(1);
								//if(dbCloseAmount>0){//表明平仓数量还没有被平完		
								if(closeAmount>0){//表明平仓数量还没有被平完
									tradeRela.setStorageAmount(trade
											.getCloseAmount());	//将被平掉的开仓数量保存	
									if (tradeRela != null) {//有数据时，将其保存
										hmTradeRela.put(tradeRela.getNum()
												+ "\t"
												+ tradeRela.getCloseNum(),
												tradeRela);
									}
								}else{//或者刚好平完，或者平仓数量已尽但开仓数量还有剩余,此时只保存平掉的数量
									//dbCloseAmount += trade.getCloseAmount();// 加上之前被减去的当天开仓的数量，得到最后一次平掉的数量
									closeAmount += trade.getCloseAmount();// 加上之前被减去的当天开仓的数量，得到最后一次平掉的数量
									
									//tradeRela.setStorageAmount(dbCloseAmount);
									tradeRela.setStorageAmount(closeAmount);
									if (tradeRela != null) {//有数据时，将其保存
										hmTradeRela.put(tradeRela.getNum()
												+ "\t"
												+ tradeRela.getCloseNum(),
												tradeRela);
									}
									break;//平仓数量已被平完，不必再循环了
								}
								
							}							
						}
					}//==end if.因为不存在　当天平仓数量>(昨日库存数量＋当天开仓数量),故不考虑ELSE情况
				}
			}
			dbl.closeResultSetFinal(rs);
		} catch (Exception ex) {
			throw new YssException("当天开仓数据保存到交易关联出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 使用移动加权平均计算卖出股指增值 2009-3-24 蒋锦 添加 MS00273 QDV4中金2009年02月27日01_A
	 * 
	 * @param dWorkDay
	 *            Date
	 * @param sSecurityCode
	 *            String
	 * @param sPortCode
	 *            String
	 * @return FuturesTradeRelaBean
	 * @throws YssException
	 */
	private void getSellAppreciForModAvg(java.util.Date dWorkDay,
			HashMap hmTradeRela, String sPortCode, String tsfTypeCode1, String tsfTypeCode2 ) throws YssException {
		FuturesTradeRelaBean sellRela = null;
		ResultSet rsApp = null;
		String strSql = "";
		double dbStorageAmount = 0;
		double dbStorageMoney = 0;
		double dbStorageBaseCuryMoney = 0;
		double dbStoragePortCuryMoney = 0;
		String sSecurityCode = "";
		String brokerCode = "";
		try {
			strSql = "SELECT *"
					+ " FROM (SELECT *"
					+ " FROM "
					+ pub.yssGetTableName("Tb_Data_Futtraderela")
					+ " WHERE FTransDate = "
					+ dbl.sqlDate(YssFun.addDay(dWorkDay, -1))
					+ " AND FTsfTypeCode = "
					+ dbl.sqlString(tsfTypeCode1) // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
					+ " AND FPortCode = "
					+ dbl.sqlString(sPortCode)
					+ " AND FSettleState = 1) a"
					+ " JOIN (SELECT FNum AS FCloseTradeNum, FTradeAmount, FPortCode, FSecurityCode, fbrokercode"
					+ " FROM " + pub.yssGetTableName("Tb_Data_Futurestrade_Tmp")
					+ " WHERE FCheckState = 1" + " AND FBargainDate = "
					+ dbl.sqlDate(dWorkDay) + " AND FTradeTypeCode = "
					+ dbl.sqlString(YssOperCons.YSS_JYLX_PC)
					+ " ) b ON a.FNum = b.FSecurityCode"
					+ " AND a.FPortCode = b.FPortCode"
					+ " and a.fbrokercode = b.fbrokercode" //modify by fangjiang 2012.10.19
					+ " ORDER BY FSecurityCode";
			rsApp = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			while (rsApp.next()) {
				// 如果本次的证券代码和上一条记录不同，获取估值增值库存数量				
				if (!(sSecurityCode + brokerCode).equalsIgnoreCase(rsApp.getString("FSecurityCode")+rsApp.getString("fbrokercode"))) { //modify by fangjiang 2012.10.19
					sSecurityCode = rsApp.getString("FSecurityCode");
					brokerCode = rsApp.getString("fbrokercode"); //modify by fangjiang 2012.10.19
					dbStorageAmount = rsApp.getDouble("FStorageAmount");
					dbStorageMoney = rsApp.getDouble("FMoney");
					dbStorageBaseCuryMoney = rsApp.getDouble("FBaseCuryMoney");
					dbStoragePortCuryMoney = rsApp.getDouble("FPortCuryMoney");
				}
				sellRela = new FuturesTradeRelaBean();
				sellRela.setNum(rsApp.getString("FSecurityCode"));
				sellRela.setCloseNum(rsApp.getString("FCloseTradeNum"));
				sellRela.setPortCode(rsApp.getString("FPortCode"));
				sellRela.setTransDate(dWorkDay);
				sellRela.setTsfTypeCode(tsfTypeCode2);
				sellRela.setBaseCuryRate(rsApp.getDouble("FBaseCuryRate"));
				sellRela.setPortCuryRate(rsApp.getDouble("FPortCuryRate"));
				sellRela.setSettleState(1);
				sellRela.setBrokerCode(rsApp.getString("FBROKERCODE"));
				// 2009-08-04 蒋锦 修改 卖出估值增值计算错误 MS00613 QD招商基金2009年08月03日01_B
				// 如果库存数量不为0，计算卖出估值增值，否则直接设置为0
				if (dbStorageAmount != 0) {
					// 如果库存数量小于等于交易数量，不用进行计算，直接取数据
					if (dbStorageAmount <= rsApp.getDouble("FTradeAmount")) {
						sellRela.setStorageAmount(dbStorageAmount);
						sellRela.setMoney(dbStorageMoney);
						sellRela.setBaseCuryMoney(dbStorageBaseCuryMoney);
						sellRela.setPortCuryMoney(dbStoragePortCuryMoney);
						dbStorageAmount = 0;
					} else {
						sellRela.setStorageAmount(rsApp
								.getDouble("FTradeAmount"));
						// xuqiji 20090422 MS00396 股指期货计算移动加权收益及估值增值错误
						// ----------//
						sellRela.setMoney(YssD.round(YssD.mul(sellRela
								.getStorageAmount(), YssD.div(rsApp
								.getDouble("FMoney"), rsApp
								.getDouble("FStorageAmount"))), 2));
						sellRela.setBaseCuryMoney(YssD.round(YssD.mul(sellRela
								.getStorageAmount(), YssD.div(rsApp
								.getDouble("FBaseCuryMoney"), rsApp
								.getDouble("FStorageAmount"))), 2));
						sellRela.setPortCuryMoney(YssD.round(YssD.mul(sellRela
								.getStorageAmount(), YssD.div(rsApp
								.getDouble("FPortCuryMoney"), rsApp
								.getDouble("FStorageAmount"))), 2));
						// xuqiji 20090422 MS00396 股指期货计算移动加权收益及估值增值错误
						// ----------//
						dbStorageAmount -= rsApp.getDouble("FTradeAmount");
						dbStorageMoney -= sellRela.getMoney();
						dbStorageBaseCuryMoney = sellRela.getBaseCuryMoney();
						dbStoragePortCuryMoney = sellRela.getPortCuryMoney();
					}
				} else {
					sellRela.setStorageAmount(0);
					sellRela.setMoney(0);
					sellRela.setBaseCuryMoney(0);
					sellRela.setPortCuryMoney(0);
				}
				if (sellRela != null) {
					hmTradeRela.put(sellRela.getNum() + "\t"
							+ sellRela.getCloseNum() + "\t"
							+ tsfTypeCode2, sellRela);
				}
			}
		} catch (Exception ex) {
			throw new YssException("计算期货卖出估值增值出错！\r\n" + ex.getMessage());
		}finally{
			dbl.closeResultSetFinal(rsApp);//关闭游标 by leeyu 20100909
		}
	}

	/**
	 * 使用先入先出法计算卖出估值增值
	 * 
	 * @param dWorkDay
	 *            Date
	 * @param trade
	 *            FuturesTradeBean
	 * @param rs
	 *            ResultSet
	 * @param dbSellAmount
	 *            double
	 * @return FuturesTradeRelaBean
	 * @throws YssException
	 */
	private FuturesTradeRelaBean getSellAppreciForFIFO(java.util.Date dWorkDay,
			FuturesTradeBean trade, ResultSet rs, double dbSellAmount,
			String tsfTypeCode2, String tsfTypeCode3)
			throws YssException {
		FuturesTradeRelaBean sellRela = null;
		ResultSet rsApp = null;
		String strSql = "";
		try {
			strSql = "SELECT * FROM "
					+ pub.yssGetTableName("TB_Data_FutTradeRela")
					+ " WHERE FNum = " + dbl.sqlString(trade.getNum())
					+ " AND FTransDate = "
					+ dbl.sqlDate(YssFun.addDay(dWorkDay, -1))
					+ " AND FTsfTypeCode = "
					+ dbl.sqlString(tsfTypeCode2); // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
			rsApp = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			if (rsApp.next()) {
				sellRela = new FuturesTradeRelaBean();
				sellRela.setNum(trade.getNum());
				sellRela.setPortCode(rs.getString("FPortCode"));
				sellRela.setCloseNum(rs.getString("FNum"));
				sellRela.setTransDate(dWorkDay);
				sellRela.setTsfTypeCode(tsfTypeCode3); // modify by fangjiang 2010.09.19 MS01439 QDV4博时2010年7月14日02_A 
				sellRela.setStorageAmount(dbSellAmount);
				if (dbSellAmount == trade.getCloseAmount()) {
					sellRela.setMoney(trade.getLastAppMoney());
				} else {
					// Start MS00607 计算价格的时候按照四舍五入保留2位小数
					// 卖出估值增值金额 = round(昨日估值增值金额/库存数量,2) * 卖出数量
//					double dbMoney = YssD.mul(YssD.round(YssD.div(rsApp
//							.getDouble("FMoney"), rsApp
//							.getDouble("FStorageAmount")), 2), dbSellAmount);
					// ====add by xuxuming,20091120.MS00821 ,期货计算平仓结转的估值增值错误
					// QDV4中金2009年11月19日01_B ========
					// ==========// 卖出估值增值金额 = round(昨日估值增值金额/库存数量 * 卖出数量,2)
					double dbMoney = YssD.round(YssD.mul(YssD.div(rsApp.getDouble("FMoney"), rsApp.getDouble("FStorageAmount")), dbSellAmount), 2);
					// ===============end======================================
					sellRela.setMoney(dbMoney);
					// End MS00607 xuqiji 20090804:QDV4中金2009年7月28日05_B
					// 期货变动保证金组合货币成本不等于组合货币市值
				}
				//-----20100619 蒋锦 添加 开仓交易的剩余估值增值要减掉已流出的估值增值-------//
				//MS01133 《QDV4深圳2010年04月28日01_A》
				trade.setLastAppMoney(YssD.sub(trade.getLastAppMoney(), sellRela.getMoney()));
				//----------------------------------------------------------------------//
				sellRela.setBaseCuryRate(rsApp.getDouble("FBaseCuryRate"));
				sellRela.setPortCuryRate(rsApp.getDouble("FPortCuryRate"));
				sellRela.setBaseCuryMoney(YssD.mul(sellRela.getMoney(),
						sellRela.getBaseCuryRate()));
				sellRela.setPortCuryMoney(YssD.div(sellRela.getBaseCuryMoney(),
						sellRela.getPortCuryRate()));
				sellRela.setSettleState(1);
			}
		} catch (Exception e) {
			throw new YssException("计算期货卖出估值增值出错！\r\n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rsApp);
		}
		return sellRela;
	}
}
