package com.yss.main.operdeal.datainterface.etf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.commeach.EachRateOper;
import com.yss.main.dao.ICostCalculate;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.linkInfo.BaseLinkInfoDeal;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.taoperation.TaCashAccLinkBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.pojo.cache.YssCost;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.util.YssD;
import com.yss.util.YssUtil;

/**
 * <p>
 * Title:xuqiji 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 * </p>
 * <p>
 * Description:ETF基金接口导入过户库数据
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
public class ETFGHBean extends DataBase {
	public ETFGHBean() {
	}

	/**
	 * 接口导入数据的入口方法
	 * 
	 * @throws YssException
	 */
	public void inertData() throws YssException {
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = true;//事物控制标识
		ResultSet rs = null;//结果集
		PreparedStatement pstmt = null; // 声明PreparedStatement
		ResultSet rst = null;
		ResultSet rset = null;
		ResultSet rr = null;
		String sSql = "";
		long sNum = 0;//用于拼接交易编号
		String strNumDate = "";//保存交易编号
		String[] sParam = new String[3];//保存参数设置中获取的参数值
		int iLinkNum = 1;// 关联编号
		String sSecutrityCode = "";// 保存证券代码
		double dCashRepAmount = 0;// 现金替代金额
		YssCost cost = null;//声明成本类
		double baseMoney = 0;// 基础货币核算成本
		double portMoney = 0;// 组合货币核算成本
		double money = 0;// 原币核算成本
		Date CashBalanceDate = null;//现金差额结转日期
		Date CashReplaceDate = null;// 现金替代结转日期
		EachExchangeHolidays holiday = null;//节假日代码
		String strCashAcctCode = "";//现金账户
		String strSupplyMode = ""; // 补票方式
		Date confirmDate = null; //申赎确认日期
		//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
		OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
		try {
			//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
			integrateAdmin.setYssPub(pub);
			
			con.setAutoCommit(false);//设置为手动提交事物
			// 1.删除表Tb_ETF_GHInterface相关交易日期和组合代码的数据

			sSql = " delete from " + pub.yssGetTableName("Tb_ETF_GHInterface")
					+ " where FBargainDate = " + dbl.sqlDate(this.sDate)
					+ " and FPortCode in(" + operSql.sqlCodes(this.sPort) + ")";
			dbl.executeSql(sSql);

			// 查询出临时表tmp_etf_gh中的数据
			String queryTmpSql = " select gddm, gdxm, bcrq, cjbh, gsdm, cjsl, bcye, zqdm, sbsj, cjsj, "
					+ "cjjg, cjje, sqbh, bs, mjbh from tmp_etf_gh where BS <> ' ' order by cjbh";
			rs = dbl.openResultSet_antReadonly(queryTmpSql);

			// 2.向目标表Tb_ETF_GHInterface插入数据
			String insertGhSql = " insert into "
					+ pub.yssGetTableName("Tb_ETF_GHInterface")
					+ "(FPortCode,FStockholderCode,FBargainDate,FTradeNum,FSeatNum,FTradeAmount,FSecurityCode,FApplyTime,FBargainTime,FTradePrice,"
					+ "FTradeMoney,FAppNum,FMark,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDesc,FRelaNum,FOperType)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			pstmt = dbl.openPreparedStatement(insertGhSql);
			String[] arrPortCodes = this.sPort.split(","); // 拆分已选组合代码
			for (int i = 0; i < arrPortCodes.length; i++) {
				// 获取etf参数设置中的：一级市场代码。二级市场代码，资金代码
				sSql = " select * from " + pub.yssGetTableName("Tb_ETF_Param")
						+ " where FPortCode =" + dbl.sqlString(arrPortCodes[i]);
				ResultSet ret = dbl.openResultSet(sSql);
				if (ret.next()) {
					sParam[0] = ret.getString("FOneGradeMktCode");// 一级市场代码
					sParam[1] = ret.getString("FTwoGradeMktCode");// 二级市场代码
					sParam[2] = ret.getString("FCapitalCode");// 资金代码
					strSupplyMode = ret.getString("FSUPPLYMODE"); // 补票方式
				}
				dbl.closeResultSetFinal(ret);
				while (rs.next()) {
					pstmt.setString(1, arrPortCodes[i]); // 组合代码
					pstmt.setString(2, rs.getString("gddm")); // 股东代码
					pstmt.setDate(3, YssFun.toSqlDate(YssFun.parseDate(rs
							.getString("bcrq"), "yyyyMMdd"))); // 业务日期
					pstmt.setString(4, rs.getString("cjbh")); // 成交编号
					pstmt.setString(5, rs.getString("gsdm")); // 席位代码
					pstmt.setDouble(6, Double.parseDouble(rs.getString("cjsl"))); // 申赎数量
					if (rs.getString("zqdm").equals(sParam[0])
							|| rs.getString("zqdm").equals(sParam[1])
							|| rs.getString("zqdm").equals(sParam[2])) {
						pstmt.setString(7, rs.getString("zqdm")); // 证券代码
					} else {
						pstmt.setString(7, rs.getString("zqdm")); // 证券代码
					}
					pstmt.setString(8, rs.getString("sbsj")); // 申报时间
					pstmt.setString(9, rs.getString("cjsj")); // 成交时间
					pstmt.setDouble(10, Double.parseDouble(rs.getString("cjjg"))); // 成交价格
					pstmt.setDouble(11, Double.parseDouble(rs.getString("cjje"))); // 成交金额
					pstmt.setString(12, rs.getString("sqbh")); // 申请编号
					pstmt.setString(13, rs.getString("bs")); // 申赎标示
					pstmt.setInt(14, 1); // 审核状态
					pstmt.setString(15, pub.getUserCode()); // 创建人、修改人
					pstmt.setString(16, YssFun.formatDate(new java.util.Date())); // 创建、修改时间
					pstmt.setString(17, pub.getUserCode()); // 复核人
					pstmt.setString(18, YssFun.formatDate(new java.util.Date())); // 复核时间
					pstmt.setString(19, ""); // 描述
					pstmt.setInt(20, iLinkNum); // 关联编号
					if (rs.getString("zqdm").equalsIgnoreCase(sParam[0])) {
						iLinkNum++;
					}
					if (rs.getString("zqdm").equalsIgnoreCase(sParam[0])) {
						pstmt.setString(21, "1stdcode"); // 业务标志
					} else if (rs.getString("zqdm").equalsIgnoreCase(sParam[1])) {
						pstmt.setString(21, "2ndcode"); // 业务标志
					} else if (rs.getString("zqdm").equalsIgnoreCase(sParam[2])) {
						pstmt.setString(21, "cashcode"); // 业务标志
					} else {
						pstmt.setString(21, "seccode"); // 业务标志
					}
					pstmt.addBatch();
				}

				pstmt.executeBatch();

				dbl.closeStatementFinal(pstmt);
				dbl.closeResultSetFinal(rs);

				// 导入到过户表后，要去判断系统的证券信息表中是否有过户表中的那些证券信息，没有的话要给出提示
				sSql = " select gh.* from "
						+ pub.yssGetTableName("Tb_ETF_GHInterface")
						+ " gh "
						+ " where gh.fcheckstate = 1 and gh.fopertype = 'seccode' and gh.fportcode ="
						+ dbl.sqlString(arrPortCodes[i])
						+ "  and gh.fbargaindate ="
						+ dbl.sqlDate(this.sDate)
						+ " and not exists"
						+ " (select s.FSecurityCode from "
						+ pub.yssGetTableName("tb_para_security")
						+ " s "
						+ " where s.FCheckState = 1 and gh.fsecuritycode = s.fsecuritycode)";
				rset = dbl.openResultSet(sSql);
				while (rset.next()) {
					sSecutrityCode += rset.getString("fsecuritycode") + ",";
				}
//				if (sSecutrityCode.trim().length() > 0) {
//					throw new YssException("请导入过户库文件前在系统【证券信息设置】中录入过户库中的【"
//							+ sSecutrityCode + "】证券信息！");
//				}
				// 查询表Tb_ETF_GHInterface中的数据
				String queryGhSql = " select a.*,mk.fclosingprice from (select gh.*, se.ftradecury, pa.fmktsrccode  from "
						+ " (select sum(aa.ftradeamount) as FTotalAmount,aa.fportcode,aa.fbargaindate,aa.fsecuritycode,aa.fmark from "
						+ pub.yssGetTableName("Tb_ETF_GHInterface")//过户库
						+ " aa group by aa.fportcode,aa.fbargaindate,aa.fsecuritycode,aa.fmark) gh join (select FTradeCury, fsecuritycode from "
						+ pub.yssGetTableName("Tb_Para_Security")//证券信息表
						+ " where FcheckState = 1) se on gh.fsecuritycode = se.fsecuritycode "
						+ " join (select FPortCode, FMktSrcCode,FOneGradeMktCode,FTwoGradeMktCode,FCapitalCode from "
						+ pub.yssGetTableName("Tb_ETF_Param")//参数设置表
						+ " where FCheckState = 1) pa on gh.fportcode = pa.fportcode and gh.fsecuritycode <> pa.fonegrademktcode"
						+ " and gh.fsecuritycode <> pa.ftwogrademktcode and gh.fsecuritycode <> pa.fcapitalcode ) a"
						+ " left join (select mk2.* from(select max(FMktValueDate) as FMktValueDate,FSecurityCode from "
						+ pub.yssGetTableName("Tb_Data_MarketValue")//行情表
						+ " where FCheckState = 1 and FMktValueDate <= "
						+ dbl.sqlDate(this.sDate)
						+ " group by FSecurityCode ) mk1 join (select FClosingPrice,FMktSrcCode, FSecurityCode, fmktvaluedate from "
						+ pub.yssGetTableName("Tb_Data_MarketValue")//行情表
						+ " where FCheckState = 1 order by FMktValueDate desc) mk2 on mk1.fsecuritycode = mk2.fsecuritycode and mk1.fmktvaluedate = mk2.fmktvaluedate"
						+ ") mk on a.fmktsrccode = mk.FMktSrcCode and a.FSecurityCode = mk.fsecuritycode and a.FBargainDate = mk.FMktValueDate"
						+ " where fportcode = "
						+ dbl.sqlString(arrPortCodes[i])
						+ " and a.FBargainDate = " + dbl.sqlDate(this.sDate);
				rs = dbl.openResultSet_antReadonly(queryGhSql);
				//给表加锁
				dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Integrated"));
				dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_TA_Trade"));
				// 3.向证券变动表Tb_XXX_Data_Integrated中插入数据
				// 插入数据前，先删除数据，条件：日期，组合，和 数据来源类型
				sSql = " delete from "
						+ pub.yssGetTableName("Tb_Data_Integrated")
						+ " where FOperDate =" + dbl.sqlDate(this.sDate)
						+ " and FPortCode = " + dbl.sqlString(arrPortCodes[i])
						+ " and FDataOrigin = " + dbl.sqlString("ETFBS");
				dbl.executeSql(sSql);

				String insertDISql = " insert into "
						+ pub.yssGetTableName("Tb_Data_Integrated")
						+ "(FNum,FSubNum,FInOutType,FExchangeDate,FOperDate,FSecurityCode,FRelaNum,FNumType,"
						+ "FTradeTypeCode,FPortCode,FTsfTypeCode,FSubTsfTypeCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,"
						+ "FExchangeCost,FMExCost,FVExCost,FPortExCost,FMPortExCost,FVPortExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,"
						+ "FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDataOrigin)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
				pstmt = dbl.openPreparedStatement(insertDISql);
				ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx()
						.getBean("avgcostcalculate");
				if (rs.next()) {
					// 自动编号和交易子编号
					String sNewNum = "E"
							+ YssFun.formatDate(rs.getDate("FBargainDate"),
									"yyyyMMdd")
							+ dbFun.getNextInnerCode(pub
									.yssGetTableName("Tb_Data_Integrated"), dbl
									.sqlRight("FNUM", 6), "000001",
									" where FExchangeDate="
											+ dbl.sqlDate(rs
													.getDate("FBargainDate"))
											+ " or FExchangeDate="
											+ dbl.sqlDate("9998-12-31")
											+ " or FNum like 'E"
											+ YssFun.formatDate(rs
													.getDate("FBargainDate"),
													"yyyyMMdd") + "%'");
					String ss = sNewNum.substring(9, sNewNum.length());
					sNum = Long.parseLong(ss);
					rs.beforeFirst();
					while (rs.next()) {
						// --------------------拼接交易编号---------------------
						sNum++;
						String tmp = "";
						for (int t = 0; t < ss.length()
								- String.valueOf(sNum).length(); t++) {
							tmp += "0";
						}
						sNewNum = sNewNum.substring(0, 9) + tmp + sNum;
						// ------------------------end--------------------------//
						// 兑换方向
						int inOutType = 0;
						String tradeType = " ";
						String bs = rs.getString("FMark");//申购赎回类型
						if (bs.equalsIgnoreCase("s")) {//赎回
							inOutType = -1;
							tradeType = YssOperCons.YSS_JYLX_ETFSHH;
						} else if (bs.equalsIgnoreCase("b")) {//申购
							inOutType = 1;
							tradeType = YssOperCons.YSS_JYLX_ETFSG;
						}
						//基础汇率
						double baseCuryRate = this.getSettingOper()
								.getCuryRate(rs.getDate("FBargainDate"),
										rs.getString("FTradeCury"),
										arrPortCodes[i],
										YssOperCons.YSS_RATE_BASE);

						EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
						rateOper.setYssPub(pub);
						rateOper.getInnerPortRate(rs.getDate("FBargainDate"),
								rs.getString("FTradeCury"), arrPortCodes[i]);
						double portCuryRate = rateOper.getDPortRate();//组合汇率

						if (tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_ETFSHH)) {//赎回
							// --------------以下用移动加权计算成本--------------------------//
							costCal.initCostCalcutate(rs
									.getDate("FBargainDate"), rs
									.getString("FPortCode"), " ", " "," ");
							costCal.setYssPub(pub);
							cost = costCal.getCarryCost(rs
									.getString("FSecurityCode"), rs
									.getDouble("FTotalAmount"), " ",
									baseCuryRate, portCuryRate);
							costCal.roundCost(cost, 2);
							// ----------------------------------------------------------//
							money = YssD.mul(cost.getCost(), inOutType);//原币成本
							baseMoney = YssD.mul(cost.getBaseCost(), inOutType);//基础货币成本
							portMoney = YssD.mul(cost.getPortCost(), inOutType);//组合货币成本

						} else {
							//原币成本
							money = YssD.round(YssD.mul(rs
									.getDouble("FClosingPrice"), Double
									.parseDouble(rs.getString("FTotalAmount")),
									inOutType), 2);
							// 计算组合货币核算成本和基础货币成本
							baseMoney = YssD.round(YssD.mul(YssD.mul(rs.getDouble("FClosingPrice"),
										Double.parseDouble(rs.getString("FTotalAmount"))),
															baseCuryRate,
															inOutType), 2);
							portMoney = YssD.round(YssD.div(YssD.mul(YssD.mul(rs.getDouble("FClosingPrice"),
											Double.parseDouble(rs.getString("FTotalAmount"))),
															baseCuryRate,
															inOutType),
															portCuryRate), 2);
						}
						//delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
						//String sSubNum = sNewNum + YssFun.formatNumber(sNum + 1, "00000");
						pstmt.setString(1, sNewNum); // 自动编号
						//edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
						pstmt.setString(2, integrateAdmin.getKeyNum()); // 交易子编号
						pstmt.setInt(3, inOutType); // 兑换方向,判断Tb_XXX_ETF_GHInterface
													// 表中的字段FMark，‘B’为流入，‘S’为流出
						// pstmt.setString(4, YssOperCons.YSS_INVESTTYPE_JYX);
						// // 投资分类
						pstmt.setDate(4, rs.getDate("FBargainDate")); // 兑换日期,取Tb_XXX_ETF_GHInterface表中的字段FBargainDate，yyyy-MM-dd
						pstmt.setDate(5, rs.getDate("FBargainDate")); // 业务日期
						pstmt.setString(6, rs.getString("FSecurityCode")); // 证券代码
						// pstmt.setString(8, " "); // 所属分类
						pstmt.setString(7, " "); // 关联编号
						pstmt.setString(8, " "); // 编号类型
						pstmt.setString(9, tradeType); // 交易类型
						pstmt.setString(10, arrPortCodes[i]); // 组合
						pstmt.setString(11, " "); // 调拨类型代码
						pstmt.setString(12, " "); // 调拨子类型代码
						pstmt.setString(13, " "); // 分析代码1
						pstmt.setString(14, " "); // 分析代码2
						pstmt.setString(15, " "); // 分析代码3
						pstmt.setDouble(16, YssD.mul(Double.parseDouble(rs.getString("FTotalAmount")), inOutType)); // 兑换数量
						pstmt.setDouble(17, money); // 核算成本
						pstmt.setDouble(18, money); // 管理成本
						pstmt.setDouble(19, money); // 估值成本
						pstmt.setDouble(20, portMoney); // 组合货币核算成本
						pstmt.setDouble(21, portMoney); // 组合货币管理成本
						pstmt.setDouble(22, portMoney); // 组合货币估值成本
						pstmt.setDouble(23, baseMoney); // 基础货币核算成本
						pstmt.setDouble(24, baseMoney); // 基础货币管理成本
						pstmt.setDouble(25, baseMoney); // 基础货币估值成本
						pstmt.setDouble(26, baseCuryRate); // 基础汇率
						pstmt.setDouble(27, portCuryRate); // 组合汇率
						pstmt.setString(28, ""); // 描述
						pstmt.setString(29, ""); // 描述
						pstmt.setInt(30, 1); // 审核状态
						pstmt.setString(31, pub.getUserCode()); // 创建人、修改人
						pstmt.setString(32, YssFun.formatDatetime(new java.util.Date())); // 创建、修改时间
						pstmt.setString(33, pub.getUserCode()); // 复核人
						pstmt.setString(34, YssFun.formatDatetime(new java.util.Date())); // 复核时间
						pstmt.setString(35, "ETFBS"); // 数据来源类型
						pstmt.addBatch();
					}
					pstmt.executeBatch();
				}
				dbl.closeStatementFinal(pstmt);
				// 4.向TA 交易数据表Tb_XXX_TA_Trade中插入数据
				// 插入数据前，先删除数据，条件： 日期和组合
				sSql = " delete from " + pub.yssGetTableName("Tb_TA_Trade")
						+ " where FTradeDate =" + dbl.sqlDate(this.sDate)
						+ " and FPortCode =" + dbl.sqlString(arrPortCodes[i]);

				dbl.executeSql(sSql);

				String insertTASql = " insert into "
						+ pub.yssGetTableName("Tb_TA_Trade")
						+ "(FNum,FTradeDate,FMarkDate,FPortCode,FPortClsCode,FSellNetCode,FSellType,FCuryCode,FAnalysisCode1,FAnalysisCode2,"
						+ "FAnalysisCode3,FSellMoney,FBeMarkMoney,FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FCashAccCode,"
						+ "FConfimDate,FSettleDate,FSettleMoney,FPortCuryRate,FBaseCuryRate,FSettleState,FDesc,FCheckState,FCreator,"
						+ "FCreateTime,FCheckUser,FCheckTime,FConvertNum,FCashRepAmount,FCashBalanceDate,FCashReplaceDate,FCASHBAL)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
				pstmt = dbl.openPreparedStatement(insertTASql);

				sSql = " select aa.*,pf.*,nav.Fportmarketvalue,unit.fprice" 
						+ " from (select sum(FTradeAmount) as FTradeAmountAll,  fportcode, FCashAccCode, FMark,FNORMSCALE"
						+ " from (select gh.*, pa.fcashacccode,pa.FNORMSCALE from "
						+ pub.yssGetTableName("Tb_ETF_GHInterface")//过户库
						+ " gh join (select FOneGradeMktCode, fportcode, FCashAccCode,FNORMSCALE from "
						+ pub.yssGetTableName("Tb_ETF_Param")//参数设置表
						+ " where FCheckState = 1) pa on gh.fportcode = pa.fportcode and gh.fsecuritycode = pa.fonegrademktcode "
						+ " where gh.FPortCode = "
						+ dbl.sqlString(arrPortCodes[i])
						+ " and gh.fbargaindate ="
						+ dbl.sqlDate(this.sDate)
						+ " ) tt"
						+ " group by fportcode ,FCashAccCode, FMark,FNORMSCALE ) aa "
						+ " left join (select FPortCode as Fport, FPortCury,FAssetCode from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")//组合设置表
						+ " where FCheckState=1) pf on aa.Fportcode = pf.Fport" 
						+ " left join (select Fportmarketvalue,fportcode from " + pub.yssGetTableName("tb_etf_navdata")//获取前一日单位现金差额
						+ " where fnavdate = " + dbl.sqlDate(this.sDate)
						+ " and fkeycode = 'UnitCashBal') nav on nav.fportcode = aa.fportcode" 
						+ " left join (select FPrice,fportcode from " + pub.yssGetTableName("tb_etf_navdata")//获取前一日单位现金差额
						+ " where fnavdate = " + dbl.sqlDate(this.sDate)
						+ " and fkeycode = 'Unit') unit on unit.fportcode = aa.fportcode";

				rst = dbl.openResultSet(sSql);

				// --------------------拼接交易编号---------------------
				strNumDate = YssFun.formatDatetime(this.sDate).substring(0, 8);
				strNumDate = strNumDate
						+ dbFun.getNextInnerCode(pub
								.yssGetTableName("tb_ta_trade"), dbl.sqlRight(
								"FNUM", 6), "000000", " where FNum like 'T"
								+ strNumDate + "%'", 1);
				strNumDate = "T" + strNumDate;
				String s = strNumDate.substring(9, strNumDate.length());
				sNum = Long.parseLong(s);
				// --------------------------------end--------------------------//
				while (rst.next()) {
					//基础汇率
					double baseCuryRate = this.getSettingOper().getCuryRate(
							this.sDate, rst.getString("FPortCury"),
							arrPortCodes[i], YssOperCons.YSS_RATE_BASE);

					EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
					rateOper.setYssPub(pub);
					rateOper.getInnerPortRate(this.sDate, rst
							.getString("FPortCury"), arrPortCodes[i]);
					double portCuryRate = rateOper.getDPortRate();//组合汇率
					double dSellMoney = YssD.round(YssD.mul(rst.getDouble("FPrice"), 
							rst.getDouble("FTradeAmountAll")),2);
					// --------------------拼接交易编号---------------------
					sNum++;
					String tmp = "";
					for (int t = 0; t < s.length()
							- String.valueOf(sNum).length(); t++) {
						tmp += "0";
					}
					strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
					// ------------------------end--------------------------//
					// ------------------------处理现金替代金额-------------------//
					sSql = " select sum(FTrademoney) as FTotalMoney from "
							+ pub.yssGetTableName("Tb_ETF_GHInterface")//过户库
							+ " where FPortCode = "
							+ dbl.sqlString(arrPortCodes[i])
							+ " and FOperType = 'cashcode' and FBargaindate = "
							+ dbl.sqlDate(this.sDate) + " and FMark ="
							+ dbl.sqlString(rst.getString("FMark"))
							+ " group by FPortCode,Fmark";
					rr = dbl.openResultSet(sSql);
					if (rr.next()) {
						dCashRepAmount = rr.getDouble("FTotalMoney");// 现金替代金额
					}
					dbl.closeResultSetFinal(rr);
					// ----------------------------end-----------------------------//
					pstmt.setString(1, strNumDate); // 编号
					pstmt.setDate(2, YssFun.toSqlDate(this.sDate)); // 交易日期
					pstmt.setDate(3, YssFun.toSqlDate(this.sDate)); // 基准日期
					pstmt.setString(4, arrPortCodes[i]); // 组合代码
					pstmt.setString(5, rst.getString("FAssetCode")); // 组合分级代码
																		// 为基金代码
					pstmt.setString(6, " "); // 销售网点代码
					pstmt.setString(7, rst.getString("FMark").equalsIgnoreCase(
							"S") ? "01" : "02"); // 销售类型
					pstmt.setString(8, rst.getString("FPortCury")); // 销售货币
					pstmt.setString(9, " "); // 分析代码1
					pstmt.setString(10, " "); // 分析代码2
					pstmt.setString(11, " "); // 分析代码3
					//易方达申赎在T+1日确认 STORY #1434 QDV4易方达基金2011年7月27日01_A panjunfang modify 20110810
					if(strSupplyMode.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_ONE)){
						pstmt.setDouble(12, dSellMoney); // 销售金额(净金额)=单位净值*销售数量
						pstmt.setDouble(13, dSellMoney); // 基准金额
						pstmt.setDouble(14, rst.getDouble("FTradeAmountAll")); // 销售数量
						pstmt.setDouble(15, rst.getDouble("FPrice")); // 销售价格
			
					}else{
						pstmt.setDouble(12, 0); // 销售金额(净金额)=单位净值*销售数量
						pstmt.setDouble(13, 0); // 基准金额
						pstmt.setDouble(14, rst.getDouble("FTradeAmountAll")); // 销售数量
						pstmt.setDouble(15, 0); // 销售价格
						pstmt.setDouble(16, 0); // 未实现损益平准金
						pstmt.setDouble(17, 0); // 损益平准金
					}
					//-----------------获取现金账户，通过现金账户链接------------------------//
					 BaseLinkInfoDeal taCashAccOper = (BaseLinkInfoDeal) pub.
	                    getOperDealCtx().getBean(
	                        "TaCashLinkDeal");
	                taCashAccOper.setYssPub(pub);
	                TaCashAccLinkBean taCashAccLink = new TaCashAccLinkBean();
	                taCashAccLink.setSellNetCode(" ");
	                taCashAccLink.setPortClsCode(rst.getString("FAssetCode"));
	                taCashAccLink.setPortCode(arrPortCodes[i]);
	                taCashAccLink.setSellTypeCode(rst.getString("FMark").equalsIgnoreCase("S") ? "01" : "02");
	                taCashAccLink.setCuryCode(rst.getString("FPortCury"));
	                taCashAccLink.setStartDate(YssFun.formatDate(this.sDate));
	                taCashAccOper.setLinkAttr(taCashAccLink);
	                ArrayList reList = taCashAccOper.getLinkInfoBeans();
	                if (reList != null) {
	                	strCashAcctCode = ( (CashAccountBean) reList.get(0)).getStrCashAcctCode();
	                }
					//------------------end--------------------------------------------//
					pstmt.setString(18,strCashAcctCode.trim().length()>0?strCashAcctCode:" "); // 现金帐户
					if(strSupplyMode.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_ONE)){//易方达申赎在T+1日确认 STORY #1434 QDV4易方达基金2011年7月27日01_A panjunfang modify 20110810
						TaTradeBean ta = new TaTradeBean();
						ta.setYssPub(pub);
			            ta.setStrPortCode(arrPortCodes[i]);
			            ta.setSPortClsCode(rst.getString("FAssetCode"));
			            ta.setDTradeDate(YssFun.toSqlDate(this.sDate));
			            ta.setStrSellNetCode(" ");
			            ta.setStrAnalysisCode1(" ");
			            ta.setDSellAmount(rst.getDouble("FTradeAmountAll"));
			            ta.setBeMarkMoney(dSellMoney);
			            ta.getETFPL();
						confirmDate = ta.getConfirmDay(" ", rst.getString("FAssetCode"), arrPortCodes[i], 
										rst.getString("FMark").equalsIgnoreCase("S") ? "01" : "02", rst.getString("FPortCury"), this.sDate);

						pstmt.setDouble(16, ta.getDIncomeNotBal()); // 未实现损益平准金
						pstmt.setDouble(17, ta.getDIncomeBal()); // 损益平准金			
						pstmt.setDate(19, YssFun.toSqlDate(confirmDate)); // 确认日期
						pstmt.setDate(20, YssFun.toSqlDate(confirmDate)); // 结算日期
					}else{
						pstmt.setDate(19, YssFun.toSqlDate(this.sDate)); // 确认日期
						pstmt.setDate(20, YssFun.toSqlDate(this.sDate)); // 结算日期
					}
					pstmt.setDouble(21, 0); // 结算金额
					pstmt.setDouble(22, portCuryRate); // 组合汇率
					pstmt.setDouble(23, baseCuryRate); // 基础汇率
					pstmt.setInt(24, 0); // 结算状态
					pstmt.setString(25, ""); // 描述
					pstmt.setInt(26, 1); // 审核状态
					pstmt.setString(27, pub.getUserCode()); // 创建人、修改人
					pstmt.setString(28, YssFun.formatDatetime(new java.util.Date())); // 创建、修改时间
					pstmt.setString(29, pub.getUserCode()); // 复核人
					pstmt.setString(30, YssFun.formatDatetime(new java.util.Date())); // 复核时间
					pstmt.setDouble(31, 0);// 份额折算数量
					pstmt.setDouble(32, dCashRepAmount);// 现金替代金额
					// ----------------------以下处理现金差额结转日期和现金替代结转日期------------------------//
					sSql = "select a.*,b.FSGCashBalHoliday,c.FSHCashBalHoliday,d.FSGRepCashHoliday,e.FSHRepCashHoliday from " +
						" (select * from "+pub.yssGetTableName("Tb_ETF_Param") + " where FCheckState = 1 "+//参数设置表
						" and FPortCode = " + dbl.sqlString(arrPortCodes[i]) + 
						" ) a left join (select FPortCode,FHolidaysCode as FSGCashBalHoliday from " + pub.yssGetTableName("Tb_ETF_ParamHoildays") + //参数设置子表
						" where FOverType = 'sgbalanceover') b on b.FPortCode = a.FPortCode " + 
						" left join (select FPortCode,FHolidaysCode as FSHCashBalHoliday from " + pub.yssGetTableName("Tb_ETF_ParamHoildays") + 
						" where FOverType = 'shbalanceover') c on c.FPortCode = a.FPortCode " + 
						" left join (select FPortCode,FHolidaysCode as FSGRepCashHoliday from " + pub.yssGetTableName("Tb_ETF_ParamHoildays") + 
						" where FOverType = 'sgreplaceover') d on d.FPortCode = a.FPortCode " + 
						" left join (select FPortCode,FHolidaysCode as FSHRepCashHoliday from " + pub.yssGetTableName("Tb_ETF_ParamHoildays") + 
						" where FOverType = 'shreplaceover') e on e.FPortCode = a.FPortCode ";
					
					rr = dbl.openResultSet(sSql);
					String sRowStr="";
					holiday =new EachExchangeHolidays();
					holiday.setYssPub(pub);
					if(rr.next()){
						if(rst.getString("FMark").equalsIgnoreCase("S")){//申购
							sRowStr = (rr.getString("FSGCashBalHoliday") == null ? rr.getString("FHolidaysCode") : rr.getString("FSGCashBalHoliday")) + "\t" + rr.getInt("FSGBalanceOver") + "\t" + YssFun.formatDate(this.sDate);
						}else{//赎回
							sRowStr = (rr.getString("FSHCashBalHoliday") == null ? rr.getString("FHolidaysCode") : rr.getString("FSHCashBalHoliday")) + "\t" + rr.getInt("FSHBalanceOver") + "\t" + YssFun.formatDate(this.sDate);
						}
						holiday.parseRowStr(sRowStr);
						CashBalanceDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//现金差额结转日期
						if(rst.getString("FMark").equalsIgnoreCase("S")){//申购
							sRowStr = (rr.getString("FSGRepCashHoliday") == null ? rr.getString("FHolidaysCode") : rr.getString("FSGRepCashHoliday")) + "\t" + rr.getInt("FSGReplaceOver") + "\t" + YssFun.formatDate(this.sDate);
						}else{//赎回
							sRowStr = (rr.getString("FSHRepCashHoliday") == null ? rr.getString("FHolidaysCode") : rr.getString("FSHRepCashHoliday")) + "\t" + rr.getInt("FSHReplaceOver") + "\t" + YssFun.formatDate(this.sDate);
						}
						holiday.parseRowStr(sRowStr);
						CashReplaceDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//现金替代结转日期
					}
					dbl.closeResultSetFinal(rr);
					pstmt.setDate(33, (java.sql.Date) CashBalanceDate);// 现金差额结转日期
					pstmt.setDate(34, (java.sql.Date) CashReplaceDate);// 现金替代结转日期
					// ----------------------end-------------------------------------------------------//
					pstmt.setDouble(35, YssD.mul(rst.getDouble("Fportmarketvalue"),YssD.div(rst.getDouble("FTradeAmountAll"), rst.getDouble("FNORMSCALE"))));//现金差额
					pstmt.addBatch();
				}
				pstmt.executeBatch();

				con.commit(); // 提交事务
				bTrans = false;
				con.setAutoCommit(true); // 设置可以自动提交
			}
		} catch (Exception e) {
			throw new YssException("ETF 申赎过户数据导入出错！", e);
		} finally {
			dbl.endTransFinal(con, bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rst);
			dbl.closeResultSetFinal(rset);
			dbl.closeStatementFinal(pstmt);
			dbl.closeResultSetFinal(rr);
		}
	}
}
