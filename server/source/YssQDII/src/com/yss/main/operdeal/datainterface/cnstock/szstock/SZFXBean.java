package com.yss.main.operdeal.datainterface.cnstock.szstock;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.*;
import java.sql.*;
import java.util.HashMap;
import com.yss.main.operdeal.datainterface.cnstock.pojo.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.parasetting.SecurityBean;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 深圳发行库，储存新股新债的发行数据 用于将深圳发行库文件经过处理读入到交易接口清算库中 QDV4.1赢时胜（上海）2009年4月20日07_A
 * MS00007 created by leeyu 2009-06-04
 */
public class SZFXBean extends DataBase {
	HashMap hmSubAssetType = null; // 用于储存已选组合代码对应的资产子类型
	// add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
	HashMap hmParam = null;
	// add by songjie 210.03.25 MS00927 QDII4.1赢时胜上海2010年03月19日01_AB
	ArrayList alZqdm = new ArrayList();
	
	java.util.Date startDate = null; //add by huangqirong 2012-07-27 story #2760 获取 深交所中小企业板“003000－004999”证券代码区间的启用日期

	public SZFXBean() {
	}

	/**
	 * 实现此方法，将数据插入到指定的表中 by leeyu 20090629 edit by songjie 2010.02.24
	 * QDII国内：MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
	 * 
	 * @throws YssException
	 */
	public void inertData(HashMap hmParam) throws YssException {
		Connection conn = null;
		boolean bTrans = false;
		try {
			hmSubAssetType = new HashMap();// 用于储存已选组合代码对应的资产子类型
			// add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
			this.hmParam = hmParam;
			pubMethod.setYssPub(pub);
			hmSubAssetType = pubMethod.judgeAssetType(sPort, sDate);// 获取已选组合代码对应的资产子类型
			conn = dbl.loadConnection();
			conn.setAutoCommit(bTrans);
			bTrans = true;
			// 1：将临时表TMP_ZQBD的数据添加到SHZQBD表中。先按日期删除旧的数据，再执行插入操作
			insertFX();
			// 2：将SHZQBD表的数据添加到A001HZJKMX表中。先按日期与组合删除旧的数据，再执行插入操作
			insertHzJkMx(conn);

			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 数据插入到临时表的过程
	 * 
	 * @throws YssException
	 */
	private void insertFX() throws YssException {
		String sqlStr = "";
		try {
			// 1:删除掉深交所发行库中的数据
			sqlStr = "delete from SZFX where FDate=" + dbl.sqlDate(sDate);
			dbl.executeSql(sqlStr);
			// 2:将临时表tmp_sjsFX 数据插入到深交所股份库表SZFX中
			sqlStr = "insert into SZFX(FDATE,FXXWDM,FXZQDM,FXGDDM,FXYWLB,FXSFZH,FXWTXH,FXWTGS,FXQRGS,FXZJJE,FXBYBZ) "
					+ " select FXFSRQ,FXXWDM,FXZQDM,FXGDDM,FXYWLB,FXSFZH,FXWTXH,FXWTGS,FXQRGS,FXZJJE,FXBYBZ "
					+ " from tmp_sjsFX where IsDel='False' and FXFSRQ = "
					+ dbl.sqlDate(sDate);
			dbl.executeSql(sqlStr);
		} catch (Exception ex) {
			throw new YssException(ex.getMessage(), ex);
		} finally {

		}
	}

	/**
	 * 数据从临时表插入到接口汇总明细表的过程
	 * 
	 * @param conn
	 *            Connection
	 * @throws YssException
	 */
	private void insertHzJkMx(Connection conn) throws YssException {
		String sqlStr = "";
		String stockHolder = ""; // 股东代码
		String TradeSeat = ""; // 交易席位号
		String sInvestSign = ""; // 投资标志
		HashMap hmHolderSeat = null;
		SecTradejudge judgeBean = null;
		ResultSet rs = null;
		PreparedStatement stm = null;
		String zqdm = "";// add by songjie 2010.03.24 MS00927
							// QDII4.1赢时胜上海2010年03月19日01_AB
		try {
			// add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
			HashMap hmReadType = (HashMap) hmParam.get("hmReadType");
			ReadTypeBean readType = (ReadTypeBean) hmReadType.get(pub
					.getAssetGroupCode()
					+ " " + this.sPort);
			if (readType == null)
			{
				return ;
			}
			
			//add by huangqirong 2012-07-27 story #2760 获取 深交所中小企业板“003000－004999”证券代码区间的启用日期
			CtlPubPara ctlPub = new CtlPubPara();
			ctlPub.setYssPub(this.pub);
			this.startDate = ctlPub.getSJSZXQYStartSet("SJSZXQYStartSet", "DataInterface", "CTL_SJSZXQY", "DateTimePicker1");
			// --- end ---
			
			// add by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
			sqlStr = "insert into "
					+ pub.yssGetTableName("Tb_HzJkMx")
					+ "(FDate,FZqdm,FSzsh,FGddm,FJyxwh,FBs,FCjsl,FCjjg,FCjje,FYhs,FJsf,FGhf,FZgf,FYj,"
					+ " FGzlx,Fhggain,FZqbz,Fywbz,FSqbh,Fqtf,Zqdm,FJYFS,Ffxj,Findate,FTZBZ,FPortCode,FCreator,FCreateTime,FJKDM) "//edit by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//edit by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
			stm = conn.prepareStatement(sqlStr);
			// 1:取股东代码与席位号
			hmHolderSeat = getStockHolderAndSeat(sPort);
			String[] arrPort = sPort.split(",");
			for (int i = 0; i < arrPort.length; i++) {
				if (hmHolderSeat.get(arrPort[i]) != null) {
					TradeSeat = String.valueOf(hmHolderSeat.get(arrPort[i]))
							.split("\t")[0]; // 查找组合下的所有席位
					stockHolder = String.valueOf(hmHolderSeat.get(arrPort[i]))
							.split("\t")[1]; // 查找组合下的所有股东代码
				}
				// 2:插入新数据
				sqlStr = "select * from SZFX where FDate=" + dbl.sqlDate(sDate)
						+ " and FXXWDM in(" + operSql.sqlCodes(TradeSeat) + ")"
						+ " and FXGDDM in(" + operSql.sqlCodes(stockHolder)
						+ ")" + " and FXYWLB in('A3','A4','A6')"; // 根据日期与组合下的席位号来判断获取
				rs = dbl.openResultSet(sqlStr);
				while (rs.next()) {
					judgeBean = judgeSecurityTypeAndTradeType(rs, arrPort[i],
							rs.getString("FXXWDM"));
					if (!judgeBean.bInsert)
						continue;

					if (judgeBean.tradeSign.equalsIgnoreCase("ZQ_SZ")
							|| judgeBean.tradeSign.equalsIgnoreCase("ZQ_SZ_ZS")) {
						SecurityBean security = new SecurityBean();
						// edit by songjie 2010.02.26 MS00887
						// QDII4.1赢时胜上海2010年02月23日03_AB
						if (judgeBean.insideCode.startsWith("600")
								&& judgeBean.securitySign.equals("XG")
								&& (judgeBean.tradeSign.equals("ZQ_SZ_ZS") || judgeBean.tradeSign
										.equals("ZQ_SZ"))) {
							security.setSecurityCode(judgeBean.insideCode
									+ " CG");
						} else {
							security.setSecurityCode(judgeBean.insideCode
									+ " CS");
						}
						// edit by songjie 2010.02.26 MS00887
						// QDII4.1赢时胜上海2010年02月23日03_AB

						// add by songjie 2010.03.24 MS00927
						// QDII4.1赢时胜上海2010年03月19日01_AB
						zqdm = security.getSecurityCode();

						BaseOperDeal operDeal = new BaseOperDeal();
						operDeal.setYssPub(pub);
						// operDeal.getWorkDay(security.getStrHolidaysCode(),sDate,2);
						// stm.setDate(1,
						// YssFun.toSqlDate(operDeal.getWorkDay(security.getStrHolidaysCode(),sDate,2)));//如果为市值配售,业务日期为T+3日确认
						// edit by songjie 2009.09.11
						// 国内接口对应的证券数据的节假日代码统一为"HD-CN"
						// edit by songjie 2010.02.24 MS00879
						// QDII4.1赢时胜上海2010年02月10日02_AB
						stm.setDate(1, YssFun.toSqlDate(operDeal.getWorkDay(
								readType.getHolidaysCode(), sDate, 2)));// 如果为市值配售,业务日期为T+3日确认
					} else {
						stm.setDate(1, YssFun.toSqlDate(sDate));
					}
					// add by songjie 2010.02.25 MS00887
					// QDII4.1赢时胜上海2010年02月23日03_AB
					if (judgeBean.insideCode.startsWith("600")
							&& judgeBean.securitySign.equals("XG")
							&& (judgeBean.tradeSign.equals("ZQ_SZ_ZS") || judgeBean.tradeSign
									.equals("ZQ_SZ"))) {
						stm.setString(2, judgeBean.insideCode + " CG"); // 采用内部代码加交易所的方式
					} else {
						stm.setString(2, judgeBean.insideCode + " CS"); // 采用内部代码加交易所的方式
					}
					// add by songjie 2010.02.25 MS00887
					// QDII4.1赢时胜上海2010年02月23日03_AB

					stm.setString(3, "CS");
					stm.setString(4, rs.getString("FXGDDM"));
					stm.setString(5, rs.getString("FXXWDM"));
					if (judgeBean.sBS == null || judgeBean.sBS.equals("")) {
						judgeBean.sBS = " ";
					}
					stm.setString(6, judgeBean.sBS);
					stm.setDouble(7, judgeBean.tradeAmount);
					// add by songjie 2010.03.24 MS00927
					// QDII4.1赢时胜上海2010年03月19日01_AB
					if (judgeBean.tradeSign.equals("ZQ_SZ_ZS")
							|| judgeBean.tradeSign.equals("ZQ_SZ")) {
						stm.setDouble(8, getNewSharePrice(zqdm));// 获取新股价格
						// 成交金额 = 新股价格 ×确认股数
						stm.setDouble(9, YssD.mul(getNewSharePrice(zqdm),
								judgeBean.tradeAmount));
					} else {
						// add by songjie 2010.03.24 MS00927
						// QDII4.1赢时胜上海2010年03月19日01_AB
						stm.setDouble(8, YssD.div(judgeBean.tradeMoney,
								judgeBean.tradeAmount));
						stm.setDouble(9, judgeBean.tradeMoney);
						// add by songjie 2010.03.24 MS00927
						// QDII4.1赢时胜上海2010年03月19日01_AB
					}
					// add by songjie 2010.03.24 MS00927
					// QDII4.1赢时胜上海2010年03月19日01_AB

					stm.setDouble(10, judgeBean.stamptax);
					stm.setDouble(11, judgeBean.handleFee);
					stm.setDouble(12, judgeBean.transferFee);
					stm.setDouble(13, judgeBean.collectManageFee);
					stm.setDouble(14, judgeBean.commisionFee);
					stm.setDouble(15, 0); // 国债利息
					stm.setDouble(16, 0); // 回购收益
					stm.setString(17, judgeBean.securitySign); // 证券标志
					stm.setString(18, judgeBean.tradeSign); // 业务标志
					stm.setString(19, " "); // 申请编号
					stm.setDouble(20, 0); // 其他费
					stm.setString(21, judgeBean.oldCode); // 证券代码
					stm.setString(22, "PT"); // 交易方式
					stm.setDouble(23, judgeBean.riskPayment); // 风险金
					stm.setDate(24, YssFun.toSqlDate(sDate)); // 插入日期
					if (hmReadType != null
							&& hmReadType
									.get(assetGroupCode + " " + arrPort[i]) != null) {
						sInvestSign = String.valueOf(hmReadType
								.get(assetGroupCode + " " + arrPort[i]));
						if (sInvestSign.equalsIgnoreCase("01")) { // 交易类
							sInvestSign = "C";
						} else if (sInvestSign.equalsIgnoreCase("02")) { // 可供出售类
							sInvestSign = "S";
						} else { // 持有到期类
							sInvestSign = "F";
						}
					} else {
						sInvestSign = " ";
					}
					stm.setString(25, sInvestSign); // 投资标识
					stm.setString(26, arrPort[i]); // 组合代码
					stm.setString(27, pub.getUserCode());
					stm.setString(28, YssFun.formatDate(new java.util.Date(),
							"yyyyMMdd HH:mm:ss"));
					stm.setString(29,"SZFX");//add by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
					stm.addBatch();
				}
				// 关闭每次循环打开的游标
				dbl.closeResultSetFinal(rs);
				stm.executeBatch();
			} // end 循环组合

			//add by songjie 2010.03.24 MS00927 QDII4.1赢时胜上海2010年03月19日01_AB
			if(alZqdm.size() > 0){
				showUnSetZqdm();//提示用户未设置新股价格的证券代码信息
			}
			//add by songjie 2010.03.24 MS00927 QDII4.1赢时胜上海2010年03月19日01_AB
		} catch (Exception ex) {
			throw new YssException("执行深交所发行库表数据到接口明细表时出错！", ex);
		} finally {
			dbl.closeStatementFinal(stm);
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 根据业务类型调整证券标志与业务标志
	 * 
	 * @param rs
	 *            ResultSet 单条股份数据
	 * @param portCode
	 *            String 单个组合代码
	 * @param tradeSeat
	 *            String 席位代码
	 * @return SecTradejudge
	 * @throws YssException
	 */
	private SecTradejudge judgeSecurityTypeAndTradeType(ResultSet rs,
			String portCode, String tradeSeat) throws YssException {
		SecTradejudge judge = new SecTradejudge();
		String sYwlb = ""; // 业务类别
		String sNbdm = ""; // 内部代码
		String sWbdm = ""; // 外部代码
		String sOlddm = ""; // 旧代码

		String sZqbz = ""; // 证券标志
		String sYwbz = ""; // 业务标志
		String sBS = ""; // 买卖标志
		double dCjsl = 0; // 成交数量
		double dCjje = 0; // 成交金额

		boolean bInsert = true; // 是否执行插入操作,默认为执行插入
		FeeAttributeBean feeAttribute = new FeeAttributeBean();
		try {
			sYwlb = rs.getString("FXywlb") == null ? "" : rs
					.getString("FXywlb");
			sOlddm = rs.getString("FXZQDM");
			if (sYwlb.equalsIgnoreCase("A3")) {
				sZqbz = "XG";// 新股
				bInsert = true;
				if (rs.getString("FXzqdm").startsWith("16")
						|| rs.getString("FXzqdm").startsWith("50")
						|| rs.getString("FXzqdm").startsWith("18")
						|| rs.getString("FXzqdm").equalsIgnoreCase("159901")
						|| rs.getString("FXzqdm").equalsIgnoreCase("510050")) {
					bInsert = false;
				} else {
					if (rs.getDouble("FXZJJE") == 100.0D) {
						sZqbz = "XZ"; // 新债
						if (rs.getString("FXZQDM").startsWith("115")) {
							sYwbz = "ZQ_FLKZZ";// 可分离债中签
							sWbdm = rs.getString("FXZQDM").substring(0, 3)
									+ "***";
							bInsert = true;
						} else if (rs.getString("FXZQDM").startsWith("11")) {
							sYwbz = "ZQ_QYZQ";// 企业债中签
							sWbdm = rs.getString("FXZQDM").substring(0, 2)
									+ "****";
							bInsert = true;
						} else {
							sYwbz = "ZQ_KZZ";// 可转债中签
							bInsert = true;
						}
						dCjsl = rs.getDouble("FXQrgs"); // 确认股数
						dCjje = YssD.mul(rs.getDouble("FXZjje"), rs
								.getDouble("FXQrgs"));// 资金金额×确认股数
						sBS = "B";
					} else if (rs.getDouble("FXZJJE") == 1.0D) {
						sZqbz = "JJ"; // 基金
						if (rs.getString("FXZQDM").startsWith("1599")) {
							sYwbz = "RG_ETF";// ETF基金认购
							sWbdm = rs.getString("FXZQDM").substring(0, 4)
									+ "**";
							bInsert = true;
						} else {
							sYwbz = "RG_LOF";// LOF基金认购
							bInsert = true;
						}
						sBS = "B";
					} else if (rs.getDouble("FXZJJE") > 0) {
						sZqbz = "XG"; // 新股
						// sNbdm ="00";
						sBS = "B";
						if (rs.getString("FXZQDM").startsWith("07")  || rs.getString("FXZQDM").startsWith("37")) {//by wxl 增加对创业板股票的判断
							if (((String) hmSubAssetType.get(portCode))
									.equals("0102")
									&& pubMethod.getTradeSeatType(tradeSeat)
											.equalsIgnoreCase("index")) {
								sYwbz = "XGZF_ZS";// 指数新股增发
								bInsert = true;
							} else if (((String) hmSubAssetType.get(portCode))
									.equals("0103")
									&& pubMethod.getTradeSeatType(tradeSeat)
											.equalsIgnoreCase("index")) {
								bInsert = false;
							} else {
								sYwbz = "XGZF";// 新股增发
								bInsert = true;
								//------ add by wangzuochun 2010.05.07  MS01146   “新股增发（XGZF）”数据处理到业务资料表中数量、金额不对    QDV4国内（测试）2010年05月04日01_B   
								dCjsl = rs.getDouble("FXQrgs"); // 确认股数
								dCjje = YssD.mul(rs.getDouble("FXZjje"), rs
										.getDouble("FXQrgs"));// 资金金额×确认股数
								//--------------MS01146--------------//
							}
							sWbdm = rs.getString("FXZQDM").substring(0, 2)
									+ "****";
						} else {
							if (((String) hmSubAssetType.get(portCode))
									.equals("0102")
									&& pubMethod.getTradeSeatType(tradeSeat)
											.equalsIgnoreCase("index")) {
								sYwbz = "ZQ_ZS";// 指数新股中签
								bInsert = true;
							} else if (((String) hmSubAssetType.get(portCode))
									.equals("0103")
									&& pubMethod.getTradeSeatType(tradeSeat)
											.equalsIgnoreCase("index")) {
								bInsert = false;
							} else {
								sYwbz = "ZQ";// 新股中签
								bInsert = true;
							}
							//增加对创业板股票的处理
							if(rs.getString("FXZQDM").startsWith("30")|| rs.getString("FXZQDM").startsWith("37")||rs.getString("FXZQDM").startsWith("38")){
								sYwbz = sYwbz + " 3";
							}
							dCjsl = rs.getDouble("FXQrgs"); // 确认股数
							dCjje = YssD.mul(rs.getDouble("FXZjje"), rs
									.getDouble("FXQrgs"));// 资金金额×确认股数
						}
					} else if (rs.getDouble("FXZJJE") == 0.0D) {
						sZqbz = "XG"; // 新股

						sBS = "B";
						if (((String) hmSubAssetType.get(portCode))
								.equals("0102")
								&& pubMethod.getTradeSeatType(tradeSeat)
										.equalsIgnoreCase("index")) {
							sYwbz = "ZQ_SZ_ZS";// 指数市值配售上海中签
							bInsert = true;
						} else if (((String) hmSubAssetType.get(portCode))
								.equals("0103")
								&& pubMethod.getTradeSeatType(tradeSeat)
										.equalsIgnoreCase("index")) {
							bInsert = false;
						} else {
							sYwbz = "ZQ_SZ";// 市值配售上海中签
							bInsert = true;
						}
						//增加对创业板股票的处理
						if(rs.getString("FXZQDM").startsWith("30")|| rs.getString("FXZQDM").startsWith("37")||rs.getString("FXZQDM").startsWith("38")){
							sYwbz = sYwbz + " 3";
						}
						dCjsl = rs.getDouble("FXQrgs"); // 确认股数
						dCjje = YssD.mul(0, rs.getDouble("FXQrgs"));// 发行价格×确认股数
																	// <???发行价格>
					} else {
						bInsert = false;
					}
				}
			} else if (sYwlb.equalsIgnoreCase("A4")) {
				sZqbz = "XG"; // 新股
				sYwbz = "KPSL"; // 业务标志 可配售许可数据
				sBS = "B";
				bInsert = true;
			} else if (sYwlb.equalsIgnoreCase("A6")) {
				sZqbz = "QY";
				sYwbz = "PG"; // 业务标志 配股(PG)
				bInsert = true;
				if (rs.getString("FXzqdm").startsWith("16")
						|| rs.getString("FXzqdm").startsWith("50")
						|| rs.getString("FXzqdm").startsWith("18")
						|| rs.getString("FXzqdm").equalsIgnoreCase("159901")
						|| rs.getString("FXzqdm").equalsIgnoreCase("510050")) {
					bInsert = false;
				} else {
					dCjsl = 0; // 数量为0
					dCjje = YssD.mul(rs.getDouble("FXZjje"), rs
							.getDouble("FXQrgs"));// 资金金额×确认股数
					if (rs.getDouble("FXZJJE") == 100) {
						if (rs.getString("FXzqdm").startsWith("115")) {
							bInsert = false;
						} else if (rs.getString("FXzqdm").startsWith("11")) {
							bInsert = false;
						} else {
							sZqbz = "XZ"; // 新债
							sYwbz = "FK_KZZ";// 可转债中签返款
						}
						sBS = "S";
					} else {
						sZqbz = "XG";// 新股
						sYwbz = "FK";// 新股返款
						sBS = "S";

						//增加对创业板股票的处理
						if(rs.getString("FXZQDM").startsWith("30")|| rs.getString("FXZQDM").startsWith("37")||rs.getString("FXZQDM").startsWith("38")){
							sYwbz = sYwbz + " 3";
						}
					}
				}
			} else {
				bInsert = false;
			}
			pubXMLRead.setSZFX(sWbdm, sZqbz + " " + sYwbz);
			if (pubXMLRead.getSecSign() != null
					&& pubXMLRead.getSecSign().trim().length() > 0) {
				sZqbz = pubXMLRead.getSecSign();
			}
			if (pubXMLRead.getBusinessSign() != null
					&& pubXMLRead.getBusinessSign().trim().length() > 0) {
				sYwbz = pubXMLRead.getBusinessSign();
			}
			if (pubXMLRead.getConvertedSecCode() != null
					&& pubXMLRead.getConvertedSecCode().trim().length() > 0) {
				sNbdm = pubXMLRead.getConvertedSecCode();
				if (sNbdm.trim().length() > 0) {
					sNbdm = sNbdm.replaceAll("[*]", "");// 将全部的"*"号去掉
				}
			}
			if (sNbdm.trim().length() > 0) { // 先将内部代码转换完成
				sNbdm = sNbdm
						+ rs.getString("FXzqdm").substring(
								sNbdm.trim().length());
			} else {
				sNbdm = rs.getString("FXzqdm");
			}
			//modify by huangqirong 2012-07-27 story #2760 获取 深交所中小企业板“003000－004999”证券代码区间的启用日期
			java.util.Date tempDate = (java.util.Date)rs.getDate("FDate");
			
			if(this.startDate != null && YssFun.dateDiff(tempDate, this.startDate) > 0 ) {
			
				// delete by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
				// if (!sNbdm.startsWith("003")) { //若转换后的代码不为‘003***’，则转换为‘600***’
				// sNbdm = "600"+sNbdm.substring(3);
				// }
				// delete by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
				// add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
				
				if (sNbdm.startsWith("003") && sZqbz.equals("XG")
						&& (sYwbz.equals("ZQ_SZ_ZS") || sYwbz.equals("ZQ_SZ"))) {
					sNbdm = "600" + sNbdm.substring(3);
				}
				// add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
			}
			//---end---
			feeAttribute.setSecuritySign(sZqbz);
			feeAttribute.setBusinessSign(sYwbz);
			feeAttribute.setBs(sBS);
			feeAttribute.setZqdm(sNbdm);
			feeAttribute.setOldZqdm(sOlddm);
			feeAttribute.setPortCode(portCode);
			feeAttribute.setGsdm(tradeSeat);
			feeAttribute.setDate(sDate);
			feeAttribute.setCjje(dCjje);
			feeAttribute.setCjsl(dCjsl);
			calculateFee(feeAttribute);

			judge.insideCode = feeAttribute.getZqdm();
			judge.oldCode = feeAttribute.getOldZqdm();
			judge.bInsert = bInsert;
			judge.securitySign = feeAttribute.getSecuritySign();
			judge.tradeSign = feeAttribute.getBusinessSign();
			judge.sBS = feeAttribute.getBs();
			judge.tradeAmount = feeAttribute.getCjsl();
			judge.tradeMoney = feeAttribute.getCjje();
			judge.stamptax = feeAttribute.getFYhs(); // 印花税
			judge.handleFee = feeAttribute.getFJsf(); // 经手费
			judge.commisionFee = feeAttribute.getFYj(); // 佣金
			judge.collectManageFee = feeAttribute.getFZgf(); // 征管费
			judge.transferFee = feeAttribute.getFGhf(); // 过户费
			judge.riskPayment = feeAttribute.getFfxj(); // 风险金
		} catch (Exception ex) {
			throw new YssException(ex.getMessage(), ex);
		}
		return judge;
	}

	public void calculateFee(FeeAttributeBean feeAttribute) throws YssException {
	}

	/*
	 * 辅助类
	 */
	private class SecTradejudge {
		private SecTradejudge() {
		}

		boolean bInsert = false; // 是否插入到明细表，true 插入，false 跳过
		String securitySign = ""; // 证券标志
		String tradeSign = ""; // 业务标志
		String insideCode = ""; // 证券内部代码
		String oldCode = ""; // 旧代码
		String sBS = ""; // 买卖类型 B/S
		double tradeAmount = 0; // 成交数量
		double tradeMoney = 0; // 成交金额

		double stamptax = 0; // 印花税
		double handleFee = 0; // 经手费
		double commisionFee = 0; // 佣金
		double collectManageFee = 0; // 征管费
		double transferFee = 0; // 过户费
		double riskPayment = 0; // 风险金

	}

	/**
	 * add by songjie 
	 * 2010.03.24 
	 * MS00927 
	 * QDII4.1赢时胜上海2010年03月19日01_AB 
	 * 获取新股价格
	 * 
	 * modify by wangzuochun 2010.07.30  MS01490   按照QDII的固有模式，从股票所添加估值方法对应的价格来源获取    QDV4赢时胜上海2010年07月27日06_B  
	 * 需求变更修改此方法
	 * @param zqdm
	 * @throws YssException
	 */
	private double getNewSharePrice(String zqdm) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		ResultSet rsOne = null;
		ResultSet rsTwo = null;
		double price = 0;
		boolean haveInfo = false;
		try {
		   	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
			//根据组合代码查询组合设置中估值方法有没有设置
	
			strSql = " select db.* from (Select FPortCode from " 
				+ pub.yssGetTableName("Tb_Para_Portfolio")
				+ " where FCheckState = 1 ) da " 
			    + " join (select FPortCode,FRelaType,FSubCode,FRelaGrade from "
				+ pub.yssGetTableName("TB_para_portfolio_relaship")
				+ " where FRelatype = 'MTV' and FCheckState = 1) db on da.FPortCode = db.FPortCode " 
				+ " where db.fportcode = "
				+ dbl.sqlString(this.sPort)
				+ " order by db.frelagrade asc";   
			
			//end by lidaolong 
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				
				String strMtvCode = rs.getString("FSubCode");//取得估值方法代码
				if (strMtvCode != null && strMtvCode.trim().length() > 0){
					//根据组合设置中的估值方法代码查询估值方法链接中有没有此证券代码
					strSql = " select dd.*,dc.FMktSrcCode from (select db.* from (select FMtvCode, max(FStartDate) as FStartDate from "
							+ pub.yssGetTableName("tb_para_mtvmethod")
							+ " where FStartDate <= " + dbl.sqlDate(new java.util.Date())
							+ " and FCheckState = 1 group by FMtvCode) da "
							+ " join (select FMtvCode,FStartDate,FMktSrcCode from "
							+ pub.yssGetTableName("tb_para_mtvmethod")
							+ " where FCheckState = 1) db on da.FMtvCode = db.FMtvCode and da.FStartDate = db.FStartDate) dc "
							+ " join (select FMtvCode,FLinkCode from "
							+ pub.yssGetTableName("tb_para_mtvmethodlink")
							+ " where FCheckState = 1) dd on dc.FMtvCode = dd.FMtvCode "
							+ " where dd.FMtvcode = " + dbl.sqlString(strMtvCode)
							+ " and dd.FLinkCode = " + dbl.sqlString(zqdm);
					
					rsOne = dbl.openResultSet(strSql);
					if (rsOne.next()){
						String strMktSrcCode = rsOne.getString("FMktSrcCode"); //取得行情来源
						if (strMktSrcCode != null && strMktSrcCode.trim().length() > 0){
							//根据行情来源（价格来源），相关业务日期，指定的组合代码在新股价格设置表中查找 相关证券代码的新股价格
							strSql = " select * from "
									+ pub.yssGetTableName("TB_DATA_NEWSHAREPRICE")
									+ " where FZQDM = " + dbl.sqlString(zqdm) 
									+ " and FDate = " + dbl.sqlDate(this.sDate) 
									+ " and (FPortCode = " + dbl.sqlString(this.sPort) + " or FPortCode = "+ dbl.sqlString(" ") + ")"
									+ " and FSZSH= " + dbl.sqlString(strMktSrcCode) 
									+ " and FCheckState = 1 order by FPortCode desc";
							
							rsTwo = dbl.openResultSet(strSql);
							if (rsTwo.next()){
								price = rsTwo.getDouble("FXGJG"); //取得新股价格
								haveInfo = true;
								break;
							}
							dbl.closeResultSetFinal(rsTwo);
						}
					}
					dbl.closeResultSetFinal(rsOne);
				}
				
				//haveInfo = true;
				//price = rs.getDouble("FXGJG");
			}
			dbl.closeResultSetFinal(rs);

//			if (!haveInfo) {
//				dbl.closeResultSetFinal(rs);
//				rs = null;
//
//				//在新股价格设置表中查找相关业务日期 非指定组合代码 相关证券代码的新股价格
//				strSql = " select * from "
//						+ pub.yssGetTableName("TB_DATA_NEWSHAREPRICE")
//						+ " where FZQDM = " + dbl.sqlString(zqdm)
//						+ " and FDate = " + dbl.sqlDate(this.sDate)
//						+ " and FPortCode = ' ' and FCheckState = 1 ";
//
//				rs = dbl.openResultSet(strSql);
//				while (rs.next()) {
//					haveInfo = true;
//					price = rs.getDouble("FXGJG");
//				}
//			}

			if (!haveInfo) {
				if (!alZqdm.contains(zqdm)) {
					alZqdm.add(zqdm);
				}
			}

			return price;
		} catch (Exception e) {
			throw new YssException("获取新股价格出错！");
		}
	}

	/**
	 * add by songjie
	 * 2010.03.25
	 * MS00927
	 * QDII4.1赢时胜上海2010年03月19日01_AB
	 * 提示用户未设置新股价格的证券代码信息
	 * @throws YssException
	 */
	private void showUnSetZqdm() throws YssException {
		Iterator iterator = null;
		String zqdm = "";
		try {
			iterator = alZqdm.iterator();
			
			while(iterator.hasNext()){
				zqdm += (String)iterator.next() + ",";
			}
			
			if(zqdm.length() > 1){
				zqdm = zqdm.substring(0, zqdm.length() - 1);
			}
			
			if(!zqdm.equals("")){
				//------ modify by wangzuochun  2010.07.30  MS01490   按照QDII的固有模式，从股票所添加估值方法对应的价格来源获取    QDV4赢时胜上海2010年07月27日06_B  
				throw new YssException("请检查组合"+ this.sPort + "的组合设置中是否设置了估值方法以及这些估值方法链接中是否包含证券" + zqdm + " ！");
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
}
