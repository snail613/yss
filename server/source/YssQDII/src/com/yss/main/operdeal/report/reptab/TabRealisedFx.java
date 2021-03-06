package com.yss.main.operdeal.report.reptab;

import com.yss.base.BaseAPOperValue;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.util.Iterator;

import com.yss.main.dayfinish.OffAcctBean;
import com.yss.main.operdeal.report.reptab.valrep.BondValRep;
import com.yss.util.YssFun;
import com.yss.util.YssException;
import java.sql.Connection;

/**
 * <p>
 * Title:
 * </p>
 * 计算已兑现资产本金增值/贬值分布(汇兑损益)
 * <p>
 * Description:
 * </p>
 * 此类只产生相应数据，要获取报表的值需要在数据源中配置获取。
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 *
 * @author not attributable
 * @version 1.0
 */
public class TabRealisedFx extends BaseAPOperValue {
	private java.util.Date dBeginDate;
	private java.util.Date dEndDate;

	private String portCode;
	private boolean isCreate = false;
	private static final int RealisedType_DE = 1; // 定存应收利息汇兑
	private static final int RealisedType_FI_SELL_OR_maturity = 2; // 、到期汇兑
	private static final int RealisedType_FI_PAIXI = 3; // 债券派息(汇兑
	private static final int RealisedType_EQ_SELL = 4; // 股票卖出汇兑
	private static final int RealisedType_TR_SELL = 5;// J基金卖出汇兑
	private static final int RealisedType_ExchangeRate = 6;// 换汇汇兑
	private static final int RealisedType_FEE_PAY = 7;// 支付应付费用汇兑
	private static final int RealisedType_Account_out = 8;// 现金账户流出汇兑
	private static final int RealisedType_Bonuses = 9;// 红利汇兑
	private static final int RealisedType_total = 10;// 汇总
	private static final int RealisedType_DE_Mature = 11;//定存到期

	/**
	 *
	 * <p>
	 * Title:
	 * </p>
	 * 未实现内部类
	 * <p>
	 * Description:
	 * </p>
	 * 用于获取已实现的相应信息
	 * <p>
	 * Copyright: Copyright (c) 2009
	 * </p>
	 *
	 * <p>
	 * Company:
	 * </p>
	 *
	 * @author not attributable
	 * @version 1.0
	 */
	private class RealisedBean {
		String Code; // 标示字段，用于排序
		String CCODE;// 代码
		String Name; // 项目名
		String CuryCode1; // 货币
		String CuryCode2; // 货币
		double Rate1;// 汇率
		double Rate2;// 汇率
		String Type1;
		String Type2;
		String Type3;
		String Type4;
		double value0;
		double value1;
		double value2;
		double value3;
		double value4;
		double value5;
		double value6;
		int RealisedType; // 纪录类型
		String portCode; // 组合
		java.sql.Date Date1;
		java.sql.Date Date2;
		java.sql.Date Date3;

		public void RealisedBean() {
		}
	}

	public TabRealisedFx() {
	}

	public void init(Object bean) throws YssException {
		String reqAry[] = null;
		String reqAry1[] = null;
		String sRowStr = (String) bean;
		if (sRowStr.trim().length() == 0) {
			return;
		}
		reqAry = sRowStr.split("\n");
//		reqAry1 = reqAry[0].split("\r");
//		dBeginDate = YssFun.toDate(reqAry1[1]);
//		reqAry1 = reqAry[1].split("\r");
//		dEndDate = YssFun.toDate(reqAry1[1]);
//		reqAry1 = reqAry[2].split("\r");
//		portCode = reqAry1[1];
//		
//		reqAry1 = reqAry[3].split("\r"); //此参数为新加入，需更新报表配置 if
//		if (reqAry1[1].equalsIgnoreCase("0")) {
//			//若为0，则只查询已生成的报表数据 
//			this.isCreate =false; 
//		} else { 
//			//生成报表 
//			this.isCreate = true; }
		
		//==================修改解析控件的值，前台控件值为空时不传值导致解析出错  edit by qiuxufeng 20101109 
		for (int i = 0; i < reqAry.length; i++) {
			reqAry1 = reqAry[i].split("\r");
			if(reqAry1[0].equalsIgnoreCase("1")) {
				dBeginDate = YssFun.toDate(reqAry1[1]);
			} else if(reqAry1[0].equalsIgnoreCase("2")) {
				dEndDate = YssFun.toDate(reqAry1[1]);
			} else if(reqAry1[0].equalsIgnoreCase("3")) {
				portCode = reqAry1[1];
			} else if(reqAry1[0].equalsIgnoreCase("4")) {
				if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0，则只查询已生成的报表数据
					this.isCreate = false;
				} else { // 生成报表
					this.isCreate = true;
				}
			}
		}
		//=========end=========
	}

	public Object invokeOperMothed() throws YssException {
		HashMap valueMap = null;
		valueMap = new HashMap();
		//getRealisedValue(valueMap);
		try {
			createTempRealised();
			if (isCreate) {
	    		 //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
				 OffAcctBean offAcct = new OffAcctBean();
					offAcct.setYssPub(this.pub);
				 String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
				 String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
				 if(!tmpInfo.trim().equalsIgnoreCase("")) {
					 return "<OFFACCT>" + tmpInfo;
				 }
				 //=================end=================
				getRealisedValue(valueMap);
			}
		} catch (YssException ex) {
			throw new YssException(ex.getMessage());
		}

		return "";
	}

	/**
	 * 创建用于存放未实现数据的表。
	 *
	 * @throws YssException
	 */
	private void createTempRealised() throws YssException {
		String strSql = "";
		try {
			if (dbl.yssTableExist(pub.yssGetTableName("tb_Data_Realised"))) {
				return;
			} else {
				strSql = "create table "
						+ pub.yssGetTableName("tb_Data_Realised")
						+ " (FCode varchar2(70) not null,"
						+ " FFCode varchar2(50)," + " FName varchar2(50),"
						+ " FCuryCode1 varchar2(20) ,"
						+ " FCuryCode2 varchar2(20) ,"
						+ " FRate1 number(18,7)," + " FRate2 number(18,7),"
						+ " FType1 varchar2(50)," + " FType2 varchar2(50),"
						+ " FType3 varchar2(50)," + " FType4 varchar2(50),"
						+ " Fvalue0 number(18,4)," + " Fvalue1 number(18,4),"
						+ " Fvalue2 number(18,4)," + " Fvalue3 number(18,4),"
						+ " Fvalue4 number(18,4)," + " Fvalue5 number(18,4),"
						+ " Fvalue6 number(18,4)," + " FPortCode varchar2(20),"
						+ " FDate1 Date ," + " FDate2 Date ,"
						+ " FDate3 Date ," + " FRealisedType number(2))";
				dbl.executeSql(strSql);
			}
		} catch (Exception e) {
			throw new YssException("生成临时已兑现资产本金表(汇兑损益)出错!");
		}
	}

	private void deleteFromTempRealised() throws YssException {
		//合并邵宏伟修改报表代码 xuqiji 20100608
		String sqlStr =" delete from " +
	                  pub.yssGetTableName("tb_Data_Realised") +
				      " where fportcode=" + dbl.sqlString(this.portCode) + " and fdate3=" + dbl.sqlDate(this.dEndDate);

		try {

			dbl.executeSql(sqlStr);
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		}
	}

	/**
	 * 将未实现的数据插入数据库
	 *
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 */
	private void insertToTempRealised(HashMap valueMap) throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		RealisedBean Realised = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				+ pub.yssGetTableName("tb_Data_Realised")
				+ "(FCode,FFCODE,FName,FCuryCode1,FCuryCode2,FRate1,FRate2,"
				+ "FType1,FType2,FType3,FType4,Fvalue0,Fvalue1,Fvalue2,Fvalue3,Fvalue4,"
				+ "Fvalue5,Fvalue6,FDate1,FDate2,FDate3,FPortCode,FRealisedType)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				Realised = (com.yss.main.operdeal.report.reptab.TabRealisedFx.RealisedBean) valueMap
						.get((String) it.next());
				prst.setString(1, Realised.Code);
				prst.setString(2, Realised.CCODE);
				prst.setString(3, Realised.Name);
				prst.setString(4, Realised.CuryCode1);
				prst.setString(5, Realised.CuryCode2);
				prst.setDouble(6, Realised.Rate1);
				prst.setDouble(7, Realised.Rate2);
				prst.setString(8, Realised.Type1);
				prst.setString(9, Realised.Type2);
				prst.setString(10, Realised.Type3);
				prst.setString(11, Realised.Type4);
				prst.setDouble(12, Realised.value0);
				prst.setDouble(13, Realised.value1);
				prst.setDouble(14, Realised.value2);
				prst.setDouble(15, Realised.value3);
				prst.setDouble(16, Realised.value4);
				prst.setDouble(17, Realised.value5);
				prst.setDouble(18, Realised.value6);
				prst.setDate(19, Realised.Date1);
				prst.setDate(20, Realised.Date2);
				prst.setDate(21, Realised.Date3);
				prst.setString(22, Realised.portCode);
				prst.setInt(23, Realised.RealisedType);
				prst.executeUpdate();
			}
		} catch (YssException ex) {
			throw new YssException("insert error！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		}
		//add by rujiangpeng 20100603打开多张报表系统需重新登录
		finally{
			dbl.closeStatementFinal(prst);
		}
	}

	/**
	 * 获取未实现的数据
	 * //合并邵宏伟修改报表代码 xuqiji 20100608
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 * @throws SQLException
	 */
	private void getRealisedValue(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		try {
			conn.setAutoCommit(false);
			bTrans = true;

			deleteFromTempRealised(); // 先删除已有的数据。

			getEachFixedDeposit(valueMap);//定存应收利息
			getEQSell(valueMap);// 卖出股票汇兑
			getTRSell(valueMap);// 卖出基金汇兑
			getFEE(valueMap); // 支付应付费用汇兑
			getMoneyOutExchange(valueMap);
			getRateExchange(valueMap);
			getBonusExchange(valueMap);
			getFixDepositMatureDetailNew(valueMap);
			insertToTempRealised(valueMap);
			valueMap.clear();
			getFIpayExchange(valueMap);// 债券派息汇兑
			getFIExchange(valueMap);
			insertToTempRealised(valueMap);

			getSumDEPay();
			getSumTRSELL();
			getSumEQSELL();
			getSumFee();
			getSumPayFi();
			getSumFI();
			getSumMoneyOut();
			getSumgetBonus();
			getSumRateExchange();
			getSumDEMature();
			getSumCurrentMonthValue();

			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (SQLException ex1) {
			throw new YssException(ex1.getMessage());
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 当月总汇兑。
	 *
	 * @throws YssException
	 */
	private void getSumCurrentMonthValue() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#ALL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_total
				+ " as FCode,'' as FFCODE,"
				+ " '当月已实现总汇兑' as FName,"
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as  FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, SUM(NVL(Fvalue6,0)) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType = 10 and " + " FDate3 = "
				+ dbl.sqlDate(dEndDate) + " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 获取债券到期、卖出汇兑汇总信息。
	 *
	 * @throws YssException
	 */
	private void getSumFI() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#FITOTAL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_DE
				+ " as FCode,'' AS FFCode,"
				+ " '到期汇兑汇总' as FName, "
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as FDate2 ,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, round(SUM(Fvalue6),4) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where  FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType =2 and " + " FDate3 = "
				+ dbl.sqlDate(dEndDate) + " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}


	/**
	 * 获取 换汇 汇总信息。
	 *
	 * @throws YssException
	 */
	private void getSumRateExchange() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#RATOTAL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_DE
				+ " as FCode,'' AS FFCode,"
				+ " '已兑现换汇汇兑汇总' as FName, "
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as FDate2 ,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, round(SUM(Fvalue6),4) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where  FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType = 6  and " + " FDate3 = "
				+ dbl.sqlDate(dEndDate) + " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总换汇信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取红利到帐汇兑的汇总信息。
	 *
	 * @throws YssException
	 */
	private void getSumgetBonus() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#BOTOTAL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_DE
				+ " as FCode,'' AS FFCode,"
				+ " '已兑现红利到帐汇兑汇总' as FName, "
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as FDate2 ,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, round(SUM(Fvalue6),4) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where  FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType = 9  and " + " FDate3 = "
				+ dbl.sqlDate(dEndDate) + " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总红利到帐汇兑信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取已兑现定存应收利息的汇总信息。
	 *
	 * @throws YssException
	 */
	private void getSumDEPay() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#DETOTAL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_DE
				+ " as FCode,'' AS FFCode,"
				+ " '已兑现定存应收利息汇兑汇总' as FName, "
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as FDate2 ,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, round(SUM(Fvalue6),4) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where  FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType =1 and " + " FDate3 = "
				+ dbl.sqlDate(dEndDate) + " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总定存应收利息信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取已兑现股票的汇总信息。
	 *
	 * @throws YssException
	 */
	private void getSumEQSELL() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#EQTOTAL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_total
				+ " as FCode,'' as FFCode,"
				+ " '已兑现股票汇兑汇总' as FName,"
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, SUM(NVL(Fvalue6,0)) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType =4 and " + " FDate3 = "
				+ dbl.sqlDate(dEndDate) + " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总股票卖出汇兑信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取已兑现 现金账户流出 的汇总信息。
	 *
	 * @throws YssException
	 */
	private void getSumMoneyOut() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#MOTOTAL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_total
				+ " as FCode,'' as FFCode, "
				+ " '已兑现现金流出汇兑汇总' as FName, "
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, SUM(NVL(Fvalue6,0)) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where  FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType =8 and " + " FDate3 = "
				+ dbl.sqlDate(dEndDate) + " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总现金流出汇兑信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取已兑现基金的汇总信息。
	 *
	 * @throws YssException
	 */
	private void getSumTRSELL() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#TRTOTAL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_total
				+ " as FCode,'' as FFCode, "
				+ " '已兑现基金汇兑汇总' as FName, "
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, SUM(NVL(Fvalue6,0)) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where  FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType =5 and " + " FDate3 = "
				+ dbl.sqlDate(dEndDate) + " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总基金卖出汇兑信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 获取支付应付费用的汇总信息。
	 *
	 * @throws YssException
	 */
	private void getSumFee() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#FEETOTAL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_total
				+ " as FCode,'' as FFCode,"
				+ " '支付应付费用汇兑汇总' as FName, "
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, SUM(NVL(Fvalue6,0)) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where  FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType =7 and " + " FDate3 = "
				+ dbl.sqlDate(dEndDate) + " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总支付应付费用汇兑信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 获取债券派息汇总信息。
	 *
	 * @throws YssException
	 */
	private void getSumPayFi() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#FPTOTAL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_total
				+ " as FCode,'' as FFCode,"
				+ " '债券派息汇兑汇总' as FName, "
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, SUM(NVL(Fvalue6,0)) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where  FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType =3 and " + " FDate3 = "
				+ dbl.sqlDate(dEndDate) + " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总债券派息汇兑信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 定存应收利息已兑现汇兑 -月明细
	 *
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 */
	private void getEachFixedDeposit(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}

		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FCuryCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_DE
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "fcashacccode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "rownum as FCode,"
				+ " fcashacccode as FFCODE,fcashaccname AS FName,fcurycode as FCuryCode1,' ' as FCuryCode2,"
				+ "null as FDate1,null as FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ "0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4,"
				+ "0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5,"
				+ "ffx AS Fvalue6,FPortCode,"
				+ RealisedType_DE
				+ " as FRealisedType from ("
				+ " select aa.fcashacccode, aa.fcashaccname, aa.fcurycode, aa.fportcode,sum(round(aa.fbal*bb.frate-aa.fportcurybal,2)) as ffx from ("
				+ " select t1.fnum, t1.fcashacccode,t1.fportcode,t1.fanalysiscode1,t1.fanalysiscode2,t1.fmaturedate,"
				+ "	t2.fcashaccname,t2.facctype,t2.fsubacctype,t2.fcurycode,t3.fbal,t3.fportcurybal "
				+ " from " + pub.yssGetTableName("tb_cash_savinginacc") + " t1 "
				+ " left join " + pub.yssGetTableName("tb_para_cashaccount") + " t2 on t1.fcashacccode = t2.fcashacccode and t1.fportcode=t2.fportcode "
				// 数据库升级生成报表报错，更改相关语句
				+ " left join ( select * from " + pub.yssGetTableName("tb_stock_cashpayrec") + " a "
				+ " where a.fyearmonth = (select max(fyearmonth) "
				+ " from " + pub.yssGetTableName("tb_stock_cashpayrec") + " t4, " + pub.yssGetTableName("tb_cash_savinginacc") + " t1 "
				+ " where t4.fstoragedate = case when to_char(t1.fmaturedate, 'dd') = '01' then t1.fmaturedate else t1.fmaturedate - 1 end "
				+ " and t4.fportcode = t1.fportcode and t4.fcashacccode = t1.fcashacccode and t4.fanalysiscode1 = t1.fanalysiscode1 "
				+ " and t4.fanalysiscode2 = t1.fanalysiscode2 and t4.fsubtsftypecode = '06DE')) t3 "
				+ " on t3.fstoragedate = case when to_char(t1.fmaturedate, 'dd') = '01' then t1.fmaturedate else t1.fmaturedate - 1 end "
				// + " on t3.fstoragedate = case when to_char(t1.fmaturedate,
				// 'dd') = '01' then t1.fmaturedate else t1.fmaturedate - 1 end
				// "
				+ " and t3.fportcode = t1.fportcode and t3.fcashacccode = t1.fcashacccode and t3.fanalysiscode1 = t1.fanalysiscode1 "
				+ " and t3.fanalysiscode2 = t1.fanalysiscode2 and t3.fsubtsftypecode = '06DE' "
				+ " where t1.fmaturedate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and t1.fportcode = "
				+ dbl.sqlString(this.portCode)
				+ " and t2.fsubacctype = '0102' and t2.fcurycode <> 'HKD' ) aa "
				+ " join (select t1.fnum, t1.fcashacccode,t1.fportcode,t1.fanalysiscode1,t1.fanalysiscode2,t1.fmaturedate,"
				+ " t2.fcashaccname,t2.facctype,t2.fsubacctype,t2.fcurycode,t6.fbaserate as frate "
				+ " from " + pub.yssGetTableName("tb_cash_savinginacc") + " t1 "
				+ " left join " + pub.yssGetTableName("tb_para_cashaccount") + " t2 on t1.fcashacccode = t2.fcashacccode and t1.fportcode=t2.fportcode "
				// 数据库升级生成报表报错，更改相关语句
				+ " left join (select * from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " a "
				+ " where a.fvaldate = (select max(fvaldate) from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " b, "
				+ pub.yssGetTableName("tb_cash_savinginacc") + " c where b.fcurycode = a.fcurycode and b.fportcode = a.fportcode and b.fvaldate <= c.fmaturedate)) t6 "
				+ "on t6.fcurycode = t2.fcurycode and t6.fportcode = t2.fportcode "
				+ " where t1.fmaturedate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and t1.fportcode = "
				+ dbl.sqlString(this.portCode)
				+ " and t2.fsubacctype = '0102' and t2.fcurycode <> 'HKD') bb on aa.fnum=bb.fnum"
				/*
				 * + "left join " + pub.yssGetTableName("tb_data_valrate") + "
				 * t6 on t6.fcurycode = t2.fcurycode and
				 * t6.fportcode=t1.fportcode " + " and t6.fvaldate =(select
				 * max(fvaldate) from " + pub.yssGetTableName("tb_data_valrate") + "
				 * t7 " + " where t7.fcurycode = t6.fcurycode and
				 * t7.fportcode=t6.fportcode and t7.fvaldate <= t1.fmaturedate)" + "
				 * where t1.fmaturedate between " + dbl.sqlDate(this.dBeginDate) + "
				 * and " + dbl.sqlDate(this.dEndDate) + " and t1.fportcode = " +
				 * dbl.sqlString(this.portCode) + " and t2.fsubacctype = '0102'
				 * and t2.fcurycode <> 'HKD') bb on aa.fnum=bb.fnum"
				 */
				+ " group by aa.fcashacccode, aa.fcashaccname, aa.fcurycode, aa.fportcode)";

		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			setResultValue(valueMap, rs);
		} catch (YssException ex) {
			throw new YssException("获取定存应收利息数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * 股票卖出已兑现汇兑 -月明细
	 *
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 */
	private void getEQSell(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FCuryCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_EQ_SELL
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "rownum"
				+ " as FCode,fsecurityCode as FFCode,fsecurityname as FName,"
				+ " fcurycode as FCuryCode1,' ' as FCuryCode2,"
				+ " fbargaindate as FDate1,"
				+ " null as Fdate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " fbasecuryrate as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " fcost as Fvalue0,fportcurycost as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, EQRATE AS Fvalue6, "
				+ " FPortCode ,"
				+ RealisedType_EQ_SELL
				+ " as FRealisedType"
				+ " from ("
				+ " select fbargaindate,FPORTCODE,fsecurityCode,fsecurityname,fcurycode,fcost,fbasecuryrate,fportcurycost,SellTotalCost-fportcurycost as EQRATE"
				+ " from (select dd.fbargaindate,dd.FPORTCODE, dd.fsecurityCode,jj.fsecurityname,"
				+ " cc.fcurycode, dd.fcost, dd.fbasecuryrate,round(dd.fcost*dd.fbasecuryrate,2) as SellTotalCost,"
				+ " round(dd.fportcurycost,2) as fportcurycost  from " + pub.yssGetTableName("tb_data_subtrade") + " dd"
				+ " inner join   (select *  from " + pub.yssGetTableName("tb_para_security") + " ps"
				+ "  where ps.fcheckstate = 1 and ps.fcatcode ='EQ' ) jj"
				+ " on jj.fsecuritycode=dd.fsecuritycode"
				+ "  inner join (select *  from " + pub.yssGetTableName("tb_para_cashaccount") + " pc"
				+ " where pc.fcheckstate = 1 and pc.fcurycode <> 'HKD' ) cc"
				+ " on cc.fportcode=dd.fportcode and cc.fcashacccode=dd.fcashacccode "
				+ " where FTradeTypeCode = '02' and dd.fcheckstate = 1  and dd.fportcode = "
				+ dbl.sqlString(this.portCode)
				+ " and dd.fbargaindate between "
				+ dbl.sqlDate(this.dBeginDate) + " and "
				+ dbl.sqlDate(dEndDate) + ")" + ") ";
		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			setResultValue(valueMap, rs);
		} catch (YssException ex) {
			throw new YssException("获取Equity数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * 基金卖出已兑现汇兑 -月明细
	 *
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 */
	private void getTRSell(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FCuryCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_TR_SELL
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "rownum"
				+ " as FCode,fsecurityCode as FFCode,fsecurityname as FName,"
				+ " fcurycode as FCuryCode1,' ' as FCuryCode2,"
				+ " fbargaindate as FDate1,"
				+ " null as FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " fbasecuryrate as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " fcost as Fvalue0,fportcurycost as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, TRRATE AS Fvalue6, "
				+ " FPortCode ,"
				+ RealisedType_TR_SELL
				+ " as FRealisedType"
				+ " from ("
				+ " select fbargaindate,FPORTCODE,fsecurityCode,fsecurityname,fcurycode,fcost,fbasecuryrate,fportcurycost,SellTotalCost-fportcurycost as TRRATE"
				+ " from (select dd.fbargaindate,dd.FPORTCODE, dd.fsecurityCode,jj.fsecurityname,"
				+ " cc.fcurycode, dd.fcost, dd.fbasecuryrate,dd.fcost*dd.fbasecuryrate as SellTotalCost,"
				+ " dd.fportcurycost from " + pub.yssGetTableName("tb_data_subtrade") + " dd"
				+ " inner join   (select *  from " + pub.yssGetTableName("tb_para_security") + " ps"
				+ "  where ps.fcheckstate = 1 and ps.fcatcode ='TR' ) jj"
				+ " on jj.fsecuritycode=dd.fsecuritycode"
				+ "  inner join (select *  from " + pub.yssGetTableName("tb_para_cashaccount") + " pc"
				+ " where pc.fcheckstate = 1 and pc.fcurycode <> 'HKD' ) cc"
				+ " on cc.fportcode=dd.fportcode and cc.fcashacccode=dd.fcashacccode "
				+ " where FTradeTypeCode = '02' and dd.fcheckstate = 1  and dd.fportcode = "
				+ dbl.sqlString(this.portCode)
				+ " and dd.fbargaindate between "
				+ dbl.sqlDate(this.dBeginDate) + " and "
				+ dbl.sqlDate(dEndDate) + ")" + ") ";
		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			setResultValue(valueMap, rs);
		} catch (YssException ex) {
			throw new YssException("获取基金卖出明细数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * 冲减应收应付已兑现汇兑 -月明细
	 * 包含：运营费用，证券清算款。冲减应收作正，冲减应付作负
	 *
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 */
	private void getFEE(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}

		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "fivpaycatcode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FCuryCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_FEE_PAY
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "rownum as FCode,"
				+ " fivpaycatcode as FFCode,fivpaycatname as FName,"
				+ " fcurycode as FCuryCode1,' ' as FCuryCode2,"
				+ " ftransdate as FDate1, null as FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " fexrate as FRate1,0 as FRate2,"
				+ " '' as FType1,'' as FType2,'' as FType3,'' as FType4,"
				+ " fmoney as Fvalue0,fportcurymoney as Fvalue1,"
				+ " 0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5,"
				+ " decode(ftsftypecode,'02',1,-1)*(round(fmoney*fexrate,2)-round(fportcurymoney,2)) AS Fvalue6,"
				+ " FPortCode,"
				+ RealisedType_FEE_PAY
				+ " as FRealisedType from ("
				+ " select t1.FPortCode,t1.fivpaycatcode,t4.fivpaycatname,t1.fcurycode,t1.ftsftypecode,t1.ftransdate,max(t2.fbaserate) as fexrate,sum(round(t1.fmoney,2)) as fmoney,sum(round(t1.fportcurymoney,2)) as fportcurymoney "
				+ " from " + pub.yssGetTableName("tb_data_investpayrec") + " t1 "
				+ " join Tb_Base_InvestPayCat t4 on t4.fivpaycatcode=t1.fivpaycatcode "
				// 数据库升级生成报表报错，更改相关语句
				+ " left join ( select * from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " a where a.fvaldate = ("
				+ " select max(b.fvaldate) from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " b, " + pub.yssGetTableName("tb_data_investpayrec") + " c"
				+ " where b.fcurycode = c.fcurycode and b.fportcode = a.fportcode and b.fvaldate <= c.ftransdate)) t2 "
				+ " on t2.fcurycode = t1.fcurycode and t2.fportcode = t1.fportcode "
				/*
				 * + "left join " + pub.yssGetTableName("tb_data_valrate") + "
				 * t2 on t2.fcurycode=t1.fcurycode and t2.fportcode=t1.fportcode " + "
				 * and t2.fvaldate=(select max(t3.fvaldate) from " +
				 * pub.yssGetTableName("tb_data_valrate") + " t3 " + " where
				 * t3.fcurycode=t1.fcurycode and t3.fportcode=t2.fportcode and
				 * t3.fvaldate<=t1.ftransdate) "
				 */
				+ " where t1.fportcode="
				+ dbl.sqlString(portCode)
				+ " and t1.ftransdate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(dEndDate)
				+ " and t1.ftsftypecode in ('02','03') and t1.fcheckstate=1 and t1.fcurycode<>'HKD' "
				+ " group by t1.FPortCode,t1.fivpaycatcode,t4.fivpaycatname,t1.fcurycode,t1.ftransdate,t1.ftsftypecode)"
				+ " where (round(fmoney*fexrate,2)-round(fportcurymoney,2))<>0"
				+ " union all "
				+ "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "Fcashacccode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FCuryCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_FEE_PAY
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "rownum as FCode,"
				+ " Fcashacccode as FFCode,Fcashaccname as FName,"
				+ " fcurycode as FCuryCode1,' ' as FCuryCode2,"
				+ " ftransdate as FDate1, null as FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " fexrate as FRate1,0 as FRate2,"
				+ " '' as FType1,'' as FType2,'' as FType3,'' as FType4,"
				+ " fmoney as Fvalue0,fportcurymoney as Fvalue1,"
				+ " 0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5,"
				+ " decode(ftsftypecode,'02',1,-1)*(round(fmoney*fexrate,2)-round(fportcurymoney,2)) AS Fvalue6,"
				+ " FPortCode,"
				+ RealisedType_FEE_PAY
				+ " as FRealisedType from ("
				+ " select t1.FPortCode,t1.Fcashacccode,t4.Fcashaccname,t1.fcurycode,t1.ftsftypecode,t1.ftransdate,max(t2.fbaserate) as fexrate,sum(round(t1.fmoney,2)) as fmoney,sum(round(t1.fportcurymoney,2)) as fportcurymoney "
				+ " from " + pub.yssGetTableName("tb_data_cashpayrec") + " t1 "
				+ " join " + pub.yssGetTableName("tb_para_cashaccount") + " t4 on t4.fcashacccode = t1.fcashacccode and t4.fportcode = t1.fportcode"
				// 数据库升级生成报表报错，更改相关语句
				+ " left join ( select * from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " a where a.fvaldate = ("
				+ " select max(b.fvaldate) from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " b, " + pub.yssGetTableName("tb_data_cashpayrec") + " c"
				+ " where b.fcurycode = c.fcurycode and b.fportcode = a.fportcode and b.fvaldate <= c.ftransdate and c.fportcode = "
				+ dbl.sqlString(this.portCode)
				+ " and c.ftransdate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and c.ftsftypecode in ('02','03') and c.fcheckstate = 1 and c.fcurycode <> 'HKD')) t2 "
				+ " on t2.fcurycode = t1.fcurycode and t2.fportcode = t1.fportcode "
				/*
				 * + " left join " + pub.yssGetTableName("tb_data_valrate") + "
				 * t2 on t2.fcurycode=t1.fcurycode and t2.fportcode=t1.fportcode " + "
				 * and t2.fvaldate=(select max(t3.fvaldate) from " +
				 * pub.yssGetTableName("tb_data_valrate") + " t3 " + " where
				 * t3.fcurycode=t1.fcurycode and t3.fportcode=t2.fportcode and
				 * t3.fvaldate<=t1.ftransdate) "
				 */
				+ " where t1.fportcode="
				+ dbl.sqlString(portCode)
				+ " and t1.ftransdate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(dEndDate)
				+ " and t1.ftsftypecode in ('02','03') and t1.fcurycode<>'HKD' and t1.fsubtsftypecode not in ('02DE','02DV') " // 不能包含应收存款利息和应收股息，这个两个有专门的地方计算
				+ " group by t1.FPortCode,t1.Fcashacccode,t4.Fcashaccname,t1.fcurycode,t1.ftransdate,t1.ftsftypecode)"
				+ " where (round(fmoney*fexrate,2)-round(fportcurymoney,2))<>0";

		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			setResultValue(valueMap, rs);
		} catch (YssException ex) {
			throw new YssException("获取支付应付费用明细数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * 获取定存到期的汇总信息。
	 *
	 * @throws YssException
	 */
	private void getSumDEMature() throws YssException {
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#DEDUETOTAL#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_DE_Mature
				+ " as FCode,'' AS FFCode,"
				+ " '已兑现定存到期汇兑汇总' as FName, "
				+ "' ' as FCuryCode1,' ' as FCuryCode2,"
				+ dbl.sqlDate(this.dBeginDate)
				+ " as FDate1,"
				+ " null as FDate2 ,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " 0 as FRate1,0 as FRate2,'' as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " 0 as Fvalue0,0 as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, round(SUM(Fvalue5),4) AS Fvalue6, "
				+ " FPortCode ," + RealisedType_total + " as FRealisedType "
				+ " from " + pub.yssGetTableName("tb_Data_Realised")
				+ " where  FPortCode = " + dbl.sqlString(this.portCode)
				+ " and FRealisedType = "  + RealisedType_DE_Mature
				+ " and fcode like '%detail' "
				+ " and  FDate3 = " + dbl.sqlDate(dEndDate)
				+ " group by FPortCode";
		ResultSet rs = null;
		HashMap valueMap = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			valueMap = new HashMap();
			setResultValue(valueMap, rs);
			insertToTempRealised(valueMap);
		} catch (Exception e) {
			throw new YssException("汇总定存应收利息信息数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/*
	 * 定存到期汇兑 @param valueMap HashMap @throws YssException
	 */
	private void getFixDepositMatureDetail(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}

		HashMap map = getFixDepositDetail(portCode, dBeginDate, dEndDate);

		Iterator iter = map.keySet().iterator();

		while(iter.hasNext())
		{
			String key = (String)iter.next();
			String value = (String)map.get(key);

			String[] keys = key.split("#");
		    String[] values = value.split("#");

		    //System.out.println(key+value);

			RealisedBean Realised = new RealisedBean();
			Realised.Code =  YssFun.formatDate(dEndDate, "yyyyMMdd")+"#"+portCode+"#"+key;
			Realised.CCODE = keys[0];
			Realised.Name = keys[0];
			Realised.CuryCode1 = "";
			Realised.CuryCode2 = "";
			Realised.Rate1 = Double.parseDouble(values[5]);
			Realised.Rate2 = 0;
			Realised.Type1 = keys[1];
			Realised.Type2 = keys[2];
			Realised.Type3 = keys[3];
			Realised.Type4 = "";
			Realised.value0 = 0;
			Realised.value1 = 0;
			Realised.value2 = 0;
			Realised.value3 = Double.parseDouble(values[0]);
			Realised.value4 = Double.parseDouble(values[1]);
			Realised.value5 = Double.parseDouble(values[2]);
			Realised.value6 = Double.parseDouble(values[3]);
			Realised.Date1 = Date.valueOf(values[4]);
			Realised.Date2 = null;
			Realised.Date3 = YssFun.toSqlDate(this.dEndDate);
			Realised.RealisedType = RealisedType_DE_Mature;
			Realised.portCode = this.portCode;
			valueMap.put(Realised.Code, Realised);

		}
		try {
			//setResultValue(valueMap, rs);
		} catch (Exception ex) {
			throw new YssException("获取换汇以兑现明细数据出错！", ex);
		}
	}
    //合并邵宏伟修改报表代码 xuqiji 20100608
   //邵宏伟---调整算法；现在的买入成本在VAL里面一次性计算好，直接获取就可
   private void getFixDepositMatureDetailNew(HashMap valueMap) throws YssException {
        ResultSet rs = null;

        try {
           String sql = null;


           sql = "select b.*,a.ftsftypecode,a.fsubtsftypecode from " + pub.yssGetTableName("tb_cash_transfer") + " a, " + pub.yssGetTableName("tb_data_cost") + " b "
               + " where a.fnum = b.fTransnum  and b.finout = -1 and b.fportcode = "  + dbl.sqlString(portCode)
               + " and b.ftransDate between " +  dbl.sqlDate(dBeginDate) + " and "
               +  dbl.sqlDate(dEndDate);
          rs = dbl.openResultSet(sql);
          while (rs.next()){
             RealisedBean Realised = new RealisedBean();
             double dubRate = 0;
             if ("0004".equalsIgnoreCase(rs.getString("fsubtsftypecode"))) {
                dubRate = rs.getDouble("FTransRate");
             }else {
                dubRate = rs.getDouble("FCuryRate");
             }
             Realised.Code =   YssFun.formatDate(dEndDate, "yyyyMMdd")+"#"+portCode+"#" + rs.getString("FCashAccCode")+"#"+rs.getString("fanalysiscode1")+"#"
                   +rs.getString("fanalysiscode2")+"#"+rs.getString("fanalysiscode3")+"#"+rs.getDate("FTransDate").toString() + "#detail";
             Realised.CCODE = rs.getString("FCashAccCode");
             Realised.Name = rs.getString("FCashAccCode");
             Realised.CuryCode1 = "";
             Realised.CuryCode2 = "";
             Realised.Rate1 = dubRate;  //
             Realised.Rate2 = 0;
             Realised.Type1 = rs.getString("fanalysiscode1");
             Realised.Type2 = rs.getString("fanalysiscode2");
             Realised.Type3 = rs.getString("fanalysiscode3");
             Realised.Type4 = "";
             Realised.value0 = 0;
             Realised.value1 = 0;
             Realised.value2 = 0;
             Realised.value3 = rs.getDouble("FCost") * -1; //流出原币
             Realised.value4 = YssFun.roundIt(rs.getDouble("FHkdCost")*-1,2); //l流出港币等值成本
             Realised.value5 = YssFun.roundIt((dubRate*rs.getDouble("FCost")-rs.getDouble("FHkdCost")) * -1,2); //以实现汇兑
             Realised.value6 = 0; //
             Realised.Date1 = rs.getDate("FTransDate");
             Realised.Date2 = null;
             Realised.Date3 = YssFun.toSqlDate(this.dEndDate);
             Realised.RealisedType = RealisedType_DE_Mature;
             Realised.portCode = this.portCode;
             valueMap.put(Realised.Code, Realised);
          }
         rs.getStatement().close();
        } catch (YssException ex) {
           throw new YssException("获取换汇以兑现明细数据出错！", ex);
        } catch (SQLException ex) {
           throw new YssException(ex.getMessage());
        } finally {
           dbl.closeResultSetFinal(rs);
        }
     }
     //--------------------------------end-------------------------//

   /**
    * 计算定存的成本和汇兑
    * 仅计算本期内存入或者到期的数据
    * @param portCode
    * @param startDate
    * @param endDate
    * @return
    * @throws YssException
    */

	private HashMap getFixDepositDetail(String portCode,java.util.Date startDate,java.util.Date endDate) throws YssException {
		ResultSet rs = null;
		HashMap htCost = new HashMap();
		HashMap htValue = new HashMap();
		HashMap htRate = new HashMap();

		try {
			String sql = null;
			String key = null;
			String lastKey = null;
			String value = null;

			//获取期初库存成本
			sql = "select * from " + pub.yssGetTableName("tb_stock_cost") + " k "
				+ " where k.fportcode = " + dbl.sqlString(portCode)
				+ " and k.fyearmonth = " + dbl.sqlString(YssFun.formatDate(startDate,"yyyy")+YssFun.formatNumber(YssFun.getMonth(endDate)-1, "00"));

			rs = dbl.openResultSet(sql);

			while(rs.next())
			{
				key = rs.getString("fcostacccode") + "#" + rs.getString("fanalysiscode1") + "#" + rs.getString("fanalysiscode2") + "#" + rs.getString("fanalysiscode3") + "#" ;
				value = rs.getDouble("faccbalance") + "#" + rs.getDouble("fbasecurybal") + "#" + rs.getDouble("fportcurybal") + "#";

				htCost.put(key, value);
			}

			dbl.closeResultSetFinal(rs);

			double bal = 0;
			double portBal = 0;
			double fx = 0;
			double unFx = 0;
			double tradeRate = 0;
			double matureRate = 0;
			double valRate = 0;
			double lastTotal = 0;
			double tranMoney = 0;
			String matureDate = "";
			String saveDate = "";

			//推导买入成本
			sql = " select aa.*,bb.fmoney,aa.finmoney+bb.fmoney as fnextinmoney from ( "
				+ "	select t.fportcode,t.fcashacccode, t.fsavingdate,t.fmaturedate,t.fanalysiscode1,t.fanalysiscode2,t.fanalysiscode3,t.finmoney,k.fcurycode "
				+ "	from " + pub.yssGetTableName("tb_cash_savinginacc") + " t "
				+ " left join " + pub.yssGetTableName("tb_para_cashaccount") + " k on t.fcashacccode=k.fcashacccode and t.fportcode=k.fportcode "
				+ "	where t.fportcode= " + dbl.sqlString(portCode)
				+ " and t.fbasecuryrate<>1 and t.fcheckstate=1 "
				+ "	and t.fmaturedate >= " + dbl.sqlDate(startDate)
				+ " and t.fsavingdate <= " + dbl.sqlDate(endDate)
				+ " order by t.fcashacccode,t.fanalysiscode1,t.fanalysiscode2,t.fanalysiscode3,t.fsavingdate,t.fmaturedate) aa "
				+ "	left join (select i.ftransferdate,j.fportcode,j.fcashacccode,j.fanalysiscode1,j.fanalysiscode2,j.fanalysiscode3,nvl(sum(j.fmoney),0) AS fmoney "
				+ "	from " + pub.yssGetTableName("tb_cash_transfer") + " i "
				+ "	join " + pub.yssGetTableName("Tb_Cash_Subtransfer") + " j on i.fnum=j.fnum "
				+ "	where i.fsubtsftypecode='02DE'"
				+ " group by i.ftransferdate,j.fportcode,j.fcashacccode,j.fanalysiscode1,j.fanalysiscode2,j.fanalysiscode3) bb "
				+ "	on aa.fportcode=bb.fportcode and aa.fcashacccode=bb.fcashacccode "
				+ "	and aa.fmaturedate=bb.ftransferdate and aa.fanalysiscode1=bb.fanalysiscode1 "
				+ "	and aa.fanalysiscode2=bb.fanalysiscode2 and aa.fanalysiscode3=bb.fanalysiscode3 "
				+ " order by aa.fcashacccode,aa.fanalysiscode1,aa.fanalysiscode2,aa.fanalysiscode3,aa.fsavingdate,aa.fmaturedate";

			rs = dbl.openResultSet(sql);

			while (rs.next()) {
				// 获取本条记录存款日的汇率
				if (htRate.containsKey(rs.getString("fcurycode") + "#"
						+ rs.getDate("fsavingdate").toString())) {
					tradeRate = ((Double) htRate.get(rs.getString("fcurycode")
							+ "#" + rs.getDate("fsavingdate").toString()))
							.doubleValue();
				} else {
					tradeRate = getExchangeRate(portCode, rs
							.getString("fcurycode"), rs.getDate("fsavingdate"));
					htRate.put(rs.getString("fcurycode") + "#"
							+ rs.getDate("fsavingdate").toString(), new Double(
							tradeRate));
				}

				key = rs.getString("fcashacccode") + "#"
						+ rs.getString("fanalysiscode1") + "#"
						+ rs.getString("fanalysiscode2") + "#"
						+ rs.getString("fanalysiscode3") + "#";

				saveDate = rs.getDate("fsavingdate").toString();

				if (!key.equalsIgnoreCase(lastKey)) {
					// 如果存在上个账户记录，则需要先保存上个记录的信息
					if (lastKey != null && lastKey.trim().length() != 0) {
						if (YssFun.dateDiff(YssFun.toDate(matureDate), endDate) >= 0) {
							value = bal
									+ "#"
									+ portBal
									+ "#"
									+ YssFun.roundIt(
											bal * matureRate - portBal, 2)
									+ "#" + unFx + "#" + matureDate + "#"
									+ YssFun.roundIt(matureRate, 5);

							htValue.put(lastKey+matureDate+"#"+"detail", value);

							//如果最后一笔存款本月内已到期，则计算已实现汇兑
							fx += YssFun.roundIt(bal * matureRate - portBal, 2);
							bal = 0;
							portBal = 0;
						}
						else
						{
							//如果最后一笔存款没有到期，则计算未实现汇兑
							unFx = YssFun.roundIt(bal * valRate - portBal, 2);
						}

						// 值的顺序为：原币成本-本币成本-已实现汇兑-未实现汇兑
						value = bal + "#" + portBal + "#" + fx + "#" + unFx
								+ "#" + matureDate + "#"
								+ YssFun.roundIt(tradeRate, 5);

						htValue.put(lastKey+"total", value);
					}

					//如果为新的账户，则先清空汇兑和库存数据
					fx = 0;
					unFx = 0;
					bal = 0;
					portBal = 0;
					matureDate = "";

					//获取账户的库存成本
					value = (String)htCost.get(key);

					if (value != null && value.trim().length() != 0) {
						bal = YssFun.roundIt(Double.parseDouble(value
								.split("#")[0]), 2);
						portBal = YssFun.roundIt(Double.parseDouble(value
								.split("#")[1]), 2);
					} else {
						// 如果为新建的定存，则将当前存入的金额作为库存成本
						bal = YssFun.roundIt(rs.getDouble("finmoney"), 2);
						portBal = YssFun.roundIt(bal * tradeRate, 2);
					}

				} else {
					// 如果存款日期不等于上个记录的到期日期，则说明上个记录已到期流出，此时需要计算流出的汇兑。
					if (!saveDate.equalsIgnoreCase(matureDate)) {
						value = bal + "#" + portBal + "#"
								+ YssFun.roundIt(bal * matureRate - portBal, 2)
								+ "#" + unFx + "#" + matureDate + "#"
								+ YssFun.roundIt(matureRate, 5);

						htValue.put(lastKey + matureDate + "#" + "detail",
								value);

						//汇兑等于原币成本*汇率-港币等值成本
						fx += YssFun.roundIt(bal * matureRate - portBal, 2);

						//如果为新建的定存，则将当前存入的金额作为库存成本
						bal = YssFun.roundIt(rs.getDouble("finmoney"),2);
						portBal = YssFun.roundIt(bal * tradeRate, 2);
					}
					else
					{
						//如果为1号，则判断是否数据拆分
						if(YssFun.formatDate(saveDate,"dd").equalsIgnoreCase("01"))
						{
							sql = "select sum(finmoney) finmoney from " + pub.yssGetTableName("tb_cash_savinginacc") + " t "
								+ " where t.fcheckstate=1 and t.fsavingdate=" + dbl.sqlDate(saveDate)
								+ " and t.fcashacccode=" + dbl.sqlString(rs.getString("fcashacccode"));

							ResultSet rsTmp = dbl.openResultSet(sql);

							if(rsTmp.next())
							{
								tranMoney = YssFun.roundIt(rsTmp.getDouble("finmoney") - lastTotal,2);
							}

							dbl.closeResultSetFinal(rsTmp);

							if(tranMoney > 0.01||tranMoney < -0.01)
							{
								//如果本期流入大于上期本息和，则有资金流入。小于，则有流出。相等，则为滚存。因为有时利息到账有一分钱的差异，所以此处用0.01做判断。
								tranMoney = YssFun.roundIt(rs.getDouble("finmoney"),2) - lastTotal;
								if(tranMoney > 0.01)
								{
									//资金流入的港币等值按交易日汇率计算
									bal += YssFun.roundIt(tranMoney,2);
									portBal += YssFun.roundIt(tranMoney * tradeRate, 2);
								}
								else if(tranMoney < -0.01)
								{
									value = -YssFun.roundIt(tranMoney,2) + "#" + -YssFun.roundIt(tranMoney * portBal / bal, 2) + "#" + YssFun.roundIt(-tranMoney * tradeRate + tranMoney * portBal / bal, 2) + "#" + unFx + "#" + matureDate + "#" + YssFun.roundIt(matureRate,5);

									htValue.put(lastKey+matureDate+"#"+"detail", value);

									//资金流出的港币等值按比例计算，需要计算已实现汇兑
									fx += YssFun.roundIt(-tranMoney * tradeRate + tranMoney * portBal / bal, 2);
									portBal += YssFun.roundIt(tranMoney * portBal / bal, 2);
									bal += YssFun.roundIt(tranMoney,2);

								}
							}
							else
							{
								//如果是拆分则获取账户的库存成本
								value = (String)htCost.get(key);

								if(value!=null && value.trim().length()!=0)
								{
									bal = YssFun.roundIt(Double.parseDouble(value.split("#")[0]),2);
									portBal = YssFun.roundIt(Double.parseDouble(value.split("#")[1]),2);
								}
								else
								{
									//如果为新建的定存，则将当前存入的金额作为库存成本
									bal = YssFun.roundIt(rs.getDouble("finmoney"),2);
									portBal = YssFun.roundIt(bal * tradeRate, 2);
								}
							}
						}
						else
						{
							//如果本期流入大于上期本息和，则有资金流入。小于，则有流出。相等，则为滚存。因为有时利息到账有一分钱的差异，所以此处用0.01做判断。
							tranMoney = YssFun.roundIt(rs.getDouble("finmoney") - lastTotal,2);
							if(tranMoney > 0.01)
							{
								//资金流入的港币等值按交易日汇率计算
								bal += YssFun.roundIt(tranMoney,2);
								portBal += YssFun.roundIt(tranMoney * tradeRate, 2);
							}
							else if(tranMoney < -0.01)
							{
								value = -YssFun.roundIt(tranMoney,2) + "#" + -YssFun.roundIt(tranMoney * portBal / bal, 2) + "#" + YssFun.roundIt(-tranMoney * tradeRate + tranMoney * portBal / bal, 2) + "#" + unFx + "#" + matureDate + "#" + YssFun.roundIt(tradeRate,5);

								htValue.put(lastKey+matureDate+"#"+"detail", value);

								//资金流出的港币等值按比例计算，需要计算已实现汇兑
								fx += YssFun.roundIt(-tranMoney * tradeRate + tranMoney * portBal / bal, 2);
								portBal += YssFun.roundIt(tranMoney * portBal / bal, 2);
								bal += YssFun.roundIt(tranMoney,2);
							}
						}
					}
				}

				//获取本条记录到期日的汇率
				if(htRate.containsKey(rs.getString("fcurycode") + "#" + rs.getDate("fmaturedate").toString()))
				{
					matureRate = ((Double)htRate.get(rs.getString("fcurycode") + "#" + rs.getDate("fmaturedate").toString())).doubleValue();
				}
				else
				{
					matureRate = getExchangeRate(portCode, rs.getString("fcurycode"),rs.getDate("fmaturedate"));//合并邵宏伟修改报表代码 xuqiji 20100608
					htRate.put(rs.getString("fcurycode") + "#" + rs.getDate("fmaturedate").toString(), new Double(matureRate));
				}

				//获取本条记录估值日的汇率
				if(htRate.containsKey(rs.getString("fcurycode") + "#" + YssFun.formatDate(endDate)))
				{
					valRate = ((Double)htRate.get(rs.getString("fcurycode") + "#" + YssFun.formatDate(endDate))).doubleValue();
				}
				else
				{
					valRate = getExchangeRate(portCode ,rs.getString("fcurycode"),endDate );//合并邵宏伟修改报表代码 xuqiji 20100608
					htRate.put(rs.getString("fcurycode") + "#" + YssFun.formatDate(endDate), new Double(valRate));
				}

				lastTotal =  YssFun.roundIt(rs.getDouble("fnextinmoney"),2);//本条记录本金加利息之和，如果下个记录的存款金额于此相等，则为原额滚存，否则就会有资金的流入流出

				matureDate = rs.getDate("fmaturedate").toString();//本条记录的到期日，供下个记录比较存款日期用

				lastKey = key;//保存本条记录的KEY，供下个记录比较用

			}

			dbl.closeResultSetFinal(rs);

		} catch (YssException ex) {
			throw new YssException("获取换汇以兑现明细数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

		return htValue;
	}

	/**
	 * //合并邵宏伟修改报表代码 xuqiji 20100608
	 * 获取交易日汇率
	 * @param cury
	 * @param date
	 * @return
	 * @throws YssException
	 */
	private double getExchangeRate(String portCode, String cury,java.util.Date date) throws YssException {
		ResultSet rs = null;
		double rate = 0;

		try {
			String sql = null;

			sql = "select * from " + pub.yssGetTableName("tb_data_valrate") + " t1 "
				+ " where t1.fcurycode=" + dbl.sqlString(cury)
				+ " and t1.fportcode=" + dbl.sqlString(portCode)
				+ " and t1.fvaldate=(select max(fvaldate) from " + pub.yssGetTableName("tb_data_valrate") + " t2 "
				+ " where t2.fcurycode=" + dbl.sqlString(cury)
				+ " and t2.fportcode=" + dbl.sqlString(portCode)
				+ "and t2.fvaldate<=" + dbl.sqlDate(date) + ")";

			rs = dbl.openResultSet(sql);

			if(rs.next())
			{
				rate = rs.getDouble("fbaserate");
			}

			dbl.closeResultSetFinal(rs);


		} catch (YssException ex) {
			throw new YssException("获取汇率数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

		return rate;
	}


	/*
	 * 换汇 已兑现汇兑 @param valueMap HashMap @throws YssException
	 */
	private void getRateExchange(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}

		//modify by ctq 20091221
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "buy.fcurycode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "sell.fcurycode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				//生成报表报未明确定义列，添加trade限定  版本合并（合并邵宏伟修改代码）
				+ "trade.FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_ExchangeRate
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "rownum as FCode,"
				+ "trade.fbcashacccode as FFCode,trade.fscashacccode as FName,"
				+ "buy.fcurycode as FCuryCode1,sell.fcurycode as FCuryCode2,"
				+ "trade.ftradedate as FDate1,null as FDate2,"
				+ dbl.sqlDate(dEndDate) + " as FDate3,"
				+ "round(trade.fportmoney / trade.fsmoney, 7) as FRate1,"
				+ "round(rate.fbaserate/rate.fportrate,7) as FRate2,"
				+ "'' as FType1,'' as FType2,'' as FType3,'' as FType4,"
				+ "trade.fbmoney as Fvalue0,trade.fsmoney as Fvalue1,"
				+ "round(trade.fportmoney , 7) as Fvalue2,"
				+ "0 AS Fvalue3,0 AS Fvalue4,"
				+ "0 AS Fvalue5,"
				+ "trade.fratefx AS Fvalue6,trade.fportcode,"
				+ RealisedType_ExchangeRate
				+ " as FRealisedType "
				+ "from " + pub.yssGetTableName("Tb_Data_RateTrade") + " trade "
				+ "join " + pub.yssGetTableName("tb_para_cashaccount") + " buy on trade.fbcashacccode=buy.fcashacccode "
				+ "join " + pub.yssGetTableName("tb_para_cashaccount") + " sell on trade.fscashacccode=sell.fcashacccode "
				+ "join " + pub.yssGetTableName("tb_data_valrate") + " rate on trade.fportcode=rate.fportcode "
				+ "and trade.ftradedate=rate.fvaldate and buy.fcurycode=rate.fcurycode "
				+ "where trade.ftradedate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(dEndDate)
				+ "and trade.fcheckstate=1 and trade.fportcode=" + dbl.sqlString(this.portCode);

		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			setResultValue(valueMap, rs);
		} catch (YssException ex) {
			throw new YssException("获取换汇以兑现明细数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/*
	 * 红利到帐 已兑现汇兑 @param valueMap HashMap @throws YssException
	 */
	private void getBonusExchange(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		String sqlStr = "select "
				+ dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd"))
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FCuryCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "FPortCode"
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ RealisedType_Bonuses
				+ dbl.sqlJN()
				+ "'#'"
				+ dbl.sqlJN()
				+ "rownum"
				+ " as FCode,fsecuritycode as FFCode,fsecurityname as FName,"
				+ " fcurycode as FCuryCode1,' ' as FCuryCode2,"
				+ " fbargaindate as FDate1,"
				+ " fsettledate as FDate2,"
				+ dbl.sqlDate(dEndDate)
				+ " as FDate3,"
				+ " chuquanRate as FRate1,payRate as FRate2,fcatcode as FType1,'' as FType2,'' as FType3,'' as FType4 ,"
				+ " ffactsettlemoney as Fvalue0,fportcurycost as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5, Huidui AS Fvalue6, "
				+ " FPortCode ,"
				+ RealisedType_Bonuses
				+ " as FRealisedType "
				+ " from ("
				//合并邵宏伟修改报表代码 xuqiji 20100608
				+ "select aa.fsecuritycode,aa.fsecurityname, aa.fportcode, aa.fcurycode, aa.fbargaindate,aa.fsettledate,aa.ffactsettlemoney, chuquanRate,"
				+ " bb.payRate , aa.fportcurycost, aa.fcatcode,"
				+ " case when aa.fcatcode='EQ' or aa.fcatcode = 'TR' THEN  "
				+ " round(aa.ffactsettlemoney*payRate,2)-round(chuquanRate*aa.ffactsettlemoney,2)  "
				+ "  else  round(aa.ffactsettlemoney* payRate,2)-aa.fportcurycost   end as Huidui "
				+ "  from (SELECT a.fsecuritycode, c.fsecurityname, a.fportcode, b.fcurycode, a.fbargaindate, a.fsettledate,"
				+ " a.ffactsettlemoney, a.fportcurycost, c.fcatcode, a.fbasecuryrate as chuquanRate, a.fnum ,  max(d.fvaldate) as daoRate "
				+ "  FROM (select s.fsecuritycode, s.fportcode, s.fbargaindate, s.fsettledate,  s.fportcurycost,"
				+ " s.ffactsettlemoney, s.fbasecuryrate, s.fcashacccode,s.fnum,  s.FCost "
				+ " from " + pub.yssGetTableName("tb_data_subtrade") + " s "
				+ " where s.fcheckstate = 1 and s.ftradetypecode = '06' and s.fportcode = "
				+ dbl.sqlString(this.portCode)
				+ " and s.fsettledate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and  "
				+ dbl.sqlDate(dEndDate)
				+ " ) a "
				+ "  join (select *  from  " + pub.yssGetTableName("tb_para_cashaccount") + " where fcheckstate = 1 "
				+ " and fcurycode <> 'HKD') b ON a.fcashacccode = b.fcashacccode "
				+ "  left join (select *  from  " + pub.yssGetTableName("tb_para_security") + "  where fcheckstate = 1 "
				+ "  and fcatcode in( 'EQ','TR')) c on a.fsecuritycode = c.fsecuritycode "
				+ "   left join " + pub.yssGetTableName("tb_data_valrate") + " d on d.fcurycode=b.fcurycode and d.fportcode=a.fportcode "
				+ "   where d.fvaldate<=a.fsettledate  group by a.fsecuritycode, "
				+ " c.fsecurityname, a.fportcode, b.fcurycode, a.fbargaindate,fportcurycost, a.fsettledate, "
				+ "  c.fcatcode, a.ffactsettlemoney,  a.fnum, a.fbasecuryrate  ) aa "
            + " left join (select FTradeNum,sct.FBaseCuryRate as payRate from "
            + pub.yssGetTableName("tb_cash_transfer") + " ct , " + pub.yssGetTableName("tb_cash_subtransfer")
            + " sct where ct.fnum = sct.fnum and  ct.fsubtsftypecode = '02DV'  ) bb "
            + " on aa.fnum = bb.FTradeNum "
				+
				//-----------------------------end-----------------------//
				") ";
		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			setResultValue(valueMap, rs);
		} catch (YssException ex) {
			throw new YssException("获取红利到账以兑现明细数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/*
	 * 现金账户流出 已兑现汇兑 @param valueMap HashMap @throws YssException
	 */
	private void getMoneyOutExchange(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}

		//合并邵宏伟修改报表代码 xuqiji 20100608
		String sqlStr = "select " + dbl.sqlString(YssFun.formatDate(dEndDate, "yyyyMMdd")) + dbl.sqlJN() + "'#'"
			+ dbl.sqlJN() + "t10.FCuryCode" + dbl.sqlJN() + "'#'"
			+ dbl.sqlJN() + "t10.FPortCode" + dbl.sqlJN() + "'#'"
			+ dbl.sqlJN() + RealisedType_Account_out + dbl.sqlJN() + "'#'"
			+ dbl.sqlJN() + "rownum as FCode,fcashacccode as FFCode,fcashaccname as FName,"
			+ " t10.fcurycode as FCuryCode1,' ' as FCuryCode2,"
			+ " ftransferdate as FDate1, null as FDate2,"
			+ dbl.sqlDate(dEndDate) + " as FDate3,"
			+ " fbaserate as FRate1,fbasecuryrate as FRate2,ftsftypecode as FType1,fsubtsftypecode as FType2,fanalysiscode1 as FType3,"
			+ "fanalysiscode2 as FType4 , round(fmoney,2) as Fvalue0,round(fmoney*fbasecuryrate,2) as Fvalue1,0 AS Fvalue2,0 AS Fvalue3,0 AS Fvalue4,0 AS Fvalue5,"
			+ "round(fmoney*fbaserate,2)-round(fmoney*fbasecuryrate,2)  AS Fvalue6, t10.FPortCode ,"
			+ RealisedType_Account_out + " as FRealisedType "
			+ " from ("
			+ " select t2.fcashacccode,t2.fportcode,t3.fcashaccname,t3.fcurycode,t8.fvalDate,t2.fbasecuryrate,"
			+ " t1.ftsftypecode,t1.fsubtsftypecode,t2.fanalysiscode1,t2.fanalysiscode2,t2.fmoney,t1.ftransferdate "
			+ " from " + pub.yssGetTableName("tb_cash_transfer") + " t1 "
			+ " join " + pub.yssGetTableName("tb_cash_subtransfer") + " t2 on t1.fnum = t2.fnum "
			+ " join " + pub.yssGetTableName("tb_para_cashaccount") + " t3 on t3.fportcode = t2.fportcode and t3.fcashacccode = t2.fcashacccode "
			+ " join (select t1.fnum, max(fvaldate) as fvaldate "
			+ " from " + pub.yssGetTableName("tb_cash_transfer") + " t1 "
			+ " join " + pub.yssGetTableName("tb_cash_subtransfer") + " t2 on t1.fnum = t2.fnum "
			+ " join " + pub.yssGetTableName("tb_para_cashaccount") + " t3 on t3.fportcode = t2.fportcode and t3.fcashacccode = t2.fcashacccode "
			+ " left join " + pub.yssGetTableName("tb_data_valrate") + " t6 on t6.fvaldate <= t1.ftransferdate and t6.fcurycode = t3.fcurycode and t6.fportcode=t2.fportcode "
			+ " where t3.fsubacctype='0101' and t2.finout = -1 "
			+ " and t2.fportcode = " + dbl.sqlString(this.portCode)
			+ " and t1.ftransferdate between " + dbl.sqlDate(this.dBeginDate) + "  and  " + dbl.sqlDate(dEndDate)
         + " and  FRateTradeNum is null "  //用于剔除换汇业务
			+ " and t3.fcurycode <> 'HKD' group by t1.fnum) t8 on t1.fnum = t8.fnum "
			+ " where t2.finout = -1) t10 "
			+ " left join " + pub.yssGetTableName("tb_data_valrate") + " t9 on t9.fcurycode=t10.fcurycode and t9.fportcode=t10.fportcode and t9.fvaldate=t10.fvaldate "
			+ " where round(t10.fbasecuryrate,5)<>round(t9.fbaserate,5) ";
			//------------------------end-----------------------------//
		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			setResultValue(valueMap, rs);
		} catch (YssException ex) {
			throw new YssException("获取现在帐户流出-明细-数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}



	/*
	 * 债券派息 已兑现汇兑 @param valueMap HashMap @throws YssException
	 */
	private void getFIpayExchange(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}

		 valueMap.clear();
		//定义变量
		 java.util.Date perpaydate;
		 java.util.Date paydate;
		 String sSecurity="";
		 String sSecurityName="";
		 String sbroker="";
		 String ivmanager="";
		 double payrate=0;
		 double  fbuylx=0;
		 double fbuyhkd=0;
		 double flixi=0;
		 double flxhkd=0;
		 String fcurycode="";
		ResultSet secrs = null;
		RealisedBean Realised = null;

		try{
			String strSql="select t3.ftransdate,t3.fportcode,t3.fsecuritycode,t5.fsecurityname, t3.fcurycode,t3.fbasecuryrate,"
				+ " t8.fstoragedate, t3.fanalysiscode1,t3.fanalysiscode2,t3.fmoney,t3.fportcurymoney,t8.fbal,t8.fportcurybal from ("
				+ " select t3.ftransdate,t3.fportcode,t3.fsecuritycode,t3.fcurycode,max(t3.fbasecuryrate) as fbasecuryrate,"
				+ " t3.fanalysiscode1,t3.fanalysiscode2,t3.fanalysiscode3,t3.fattrclscode,sum(t3.fmoney) as fmoney,sum(t3.fportcurymoney) as fportcurymoney "
				+ " from " + pub.yssGetTableName("tb_data_secrecpay") + " t3"
				+ " where t3.fsubtsftypecode='02FI' and t3.fportcode="+dbl.sqlString(this.portCode)
				+ " and t3.ftransdate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)
				+ " and t3.fcheckstate=1 "
				+ " group by t3.ftransdate,t3.fportcode,t3.fanalysiscode1,t3.fanalysiscode2,t3.fanalysiscode3,t3.fsecuritycode,"
				+ " t3.ftsftypecode,t3.fsubtsftypecode,t3.fcurycode,t3.fattrclscode ) t3 "
				+ " join " + pub.yssGetTableName("tb_para_security") + " t5 on t3.fsecuritycode=t5.fsecuritycode "
				//数据库升级导致生成报表报错，SQL语句调整   版本合并（合并邵宏伟修改代码）
                + " join " + pub.yssGetTableName("tb_stock_secrecpay") + " t8 " //去掉left
				+ " on t3.fsecuritycode=t8.fsecuritycode and t3.fportcode=t8.fportcode and t3.fattrclscode= t8.fattrclscode"
				+ " and t3.fanalysiscode1=t8.fanalysiscode1 and t3.fanalysiscode2=t8.fanalysiscode2 and t3.fanalysiscode3=t8.fanalysiscode3"
				+ " and t8.fstoragedate=case when to_char(t3.ftransdate, 'dd') = '01' then t3.ftransdate else t3.ftransdate - 1 end "
				+ " and t8.fsubtsftypecode='06FI' "
				+ " and t8.fyearmonth =(select max(fyearmonth) from " + pub.yssGetTableName("tb_stock_secrecpay") + " t4 "
				+ " where t4.fstoragedate = case when to_char(t3.ftransdate, 'dd') = '01' then t3.ftransdate else t3.ftransdate - 1 end "
				+ " and t4.fsecuritycode =t8.fsecuritycode and t4.fportcode = t8.fportcode "
				+ " and t4.fanalysiscode1 =t8.fanalysiscode1 and t4.fanalysiscode2 =t8.fanalysiscode2 "
				+ " and t8.fsubtsftypecode = '06FI') ";

			secrs = dbl.openResultSet(strSql);

			while (secrs.next()){
				double buy[]=new double[2];
				buy=getBuyLixi(secrs.getString("Fsecuritycode"),secrs.getString("fanalysiscode1"),secrs.getString("fanalysiscode2"),YssFun.toSqlDate(secrs.getDate("fstoragedate")));

			    Realised = new RealisedBean();
				Realised.Code =  YssFun.formatDate(dEndDate, "yyyyMMdd")+"#"+secrs.getString("fcurycode")+"#"+portCode+"#3#"+secrs.getString("fanalysiscode1")+"#"+secrs.getString("fanalysiscode2")+"#"+secrs.getString("Fsecuritycode");
				Realised.CCODE = secrs.getString("Fsecuritycode");
				Realised.Name = secrs.getString("Fsecurityname");
				Realised.CuryCode1 =secrs.getString("fcurycode");
				Realised.CuryCode2 = "";
				Realised.Rate1 = YssFun.roundIt(secrs.getDouble("fbasecuryrate"),5);
				Realised.Rate2 = 0;
				Realised.Type1 = secrs.getString("fanalysiscode1");
				Realised.Type2 = secrs.getString("fanalysiscode2");
				Realised.Type3 = "";
				Realised.Type4 = "";

				Realised.value0 = YssFun.roundIt(buy[0], 2);
				Realised.value1 = YssFun.roundIt(buy[1], 2);
				//当债券买入利息按照市值计算时，计算汇兑不需要将买入利息从应收利息中剔除   任智 20101115
				//Realised.value2 = YssFun.roundIt(secrs.getDouble("fbal"), 2)
				//		- YssFun.roundIt(buy[0], 2);
				Realised.value2 = secrs.getDouble("fbal");
				//Realised.value3 = YssFun.roundIt(secrs
				//		.getDouble("fportcurybal"), 2)
				//		- YssFun.roundIt(buy[1], 2);
				Realised.value3 = secrs.getDouble("fportcurybal");
				;

				Realised.value4 = 0;
				Realised.value5 = 0;
				Realised.value6 = YssFun.roundIt(Realised.value2*Realised.Rate1,2)-Realised.value3;
				Realised.Date1 = secrs.getDate("ftransdate");
				Realised.Date2 = null;
				Realised.Date3 = YssFun.toSqlDate(this.dEndDate);
				Realised.RealisedType = 3;
				Realised.portCode = this.portCode;
				valueMap.put(Realised.Code, Realised);
			}

		} catch (YssException ex) {
			throw new YssException("获取派息明细数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(secrs);
		}

	}

	/**
	 * 卖出债券/到期 已兑现汇兑
	 * //合并邵宏伟修改报表代码 xuqiji 20100608
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 */
	private void getFIExchange(HashMap valueMap) throws YssException {
		ResultSet rs = null;
		String sql = "";
		try{
			sql = "select t1.fsecuritycode,t2.fsecurityname,t1.fportcode,t1.fbrokercode,t1.finvmgrcode,t1.fattrclscode,t1.fbargaindate,t2.ftradecury,"
				+ " sum(t1.ftradeamount) as ftradeamount,sum(t1.fvcost) as fvcost,sum(t1.fvportcurycost) as fvportcurycost,sum(t3.fvmoney) as fvmoney,sum(t3.fvportcurymoney) as fvportcurymoney,avg(t4.fbaserate) as fbaserate, "
				+ " sum(round(t1.fvcost*t4.fbaserate,2)-round(t1.fvportcurycost,2)+ nvl(round(t3.fvmoney*t4.fbaserate,2),0)-nvl(round(t3.fvportcurymoney,2),0)) as fhd "
				+ " from " + pub.yssGetTableName("tb_data_subtrade") + " t1 "
				+ " join " + pub.yssGetTableName("tb_para_security") + " t2 on t1.fsecuritycode=t2.fsecuritycode "
				+ " left join " + pub.yssGetTableName("tb_data_secrecpay") + " t3 on t1.fsecuritycode=t3.fsecuritycode and t1.fportcode=t3.fportcode and t1.finvmgrcode=t3.fanalysiscode1 "
				+ " and t1.fbrokercode=t3.fanalysiscode2 and t1.faccruedinterest=t3.fvmoney and t3.fsubtsftypecode='02FI_B' "
				+ " left join " + pub.yssGetTableName("tb_data_valrate") + " t4 on t2.ftradecury=t4.fcurycode and t1.fportcode=t4.fportcode and t1.fbargaindate=t4.fvaldate "
				+ " where t1.fcheckstate=1 and t2.fcheckstate=1 and t2.fcatcode = 'FI' and t1.ftradetypecode in ('02','17') "
				+ " and t1.fportcode= " + dbl.sqlString(this.portCode)
				+ " and t1.fbargaindate between " + dbl.sqlDate(this.dBeginDate) + " and " + dbl.sqlDate(this.dEndDate)
				+ " group by t1.fsecuritycode,t2.fsecurityname,t2.ftradecury,t1.fportcode,t1.fbrokercode,t1.finvmgrcode,t1.fattrclscode,t1.fbargaindate";

			rs = dbl.openResultSet(sql);

			while(rs.next())
			{
				RealisedBean Realised = new RealisedBean();
				//向code中添加fbargaindate字段，以区分相同债券不同日期的数据信息  任智  20101115
				Realised.Code = YssFun.formatDate(dEndDate, "yyyyMMdd") + "#"
						+ rs.getString("ftradecury") + "#" + portCode + "#3#"
						+ rs.getString("finvmgrcode") + "#"
						+ rs.getString("fbrokercode") + "#"
						+ rs.getString("fattrclscode") + "#"
						+ rs.getString("fsecuritycode") + "#"
						+ rs.getDate("fbargaindate");
				Realised.CCODE = rs.getString("fsecuritycode");
				Realised.Name = rs.getString("fsecurityname");
				Realised.CuryCode1 = rs.getString("ftradecury");
				Realised.CuryCode2 = "";
				Realised.Rate1 = rs.getDouble("fbaserate");
				Realised.Rate2 = 0;
				Realised.Type1 = rs.getString("finvmgrcode");
				Realised.Type2 = rs.getString("fbrokercode");
				Realised.Type3 = rs.getString("fattrclscode");
				Realised.Type4 = "";
				Realised.value0 = rs.getDouble("fvcost");
				Realised.value1 = rs.getDouble("fvportcurycost");
				Realised.value2 = 0;
				Realised.value3 = 0;
				Realised.value4 = rs.getDouble("fvmoney");
				Realised.value5 = rs.getDouble("fvportcurymoney");
				Realised.value6 = rs.getDouble("fhd");
				Realised.Date1 = rs.getDate("fbargaindate");
				Realised.Date2 = null;
				Realised.Date3 = YssFun.toSqlDate(this.dEndDate);
				Realised.RealisedType = 2;
				Realised.portCode = this.portCode;
				valueMap.put(Realised.Code, Realised);
			}

			dbl.closeResultSetFinal(rs);

		}  catch (Exception ex) {
			throw new YssException(ex.getMessage());
		} finally {
			//dbl.closeResultSetFinal(ds);
			dbl.closeResultSetFinal(rs);
		}

	}
	/* 获取买入利息原币、港币等值*/

	private double[] getBuyLixi(String sSecurityCode,String Ivmanage ,String Broker,Date  endDate)throws YssException {


         BondValRep bond=new BondValRep();
         double dBal=0;
         double dPortBal=0;
         bond.setYssPub(pub);
         String SecurityCode = sSecurityCode;
		 HashMap hashmap=bond.getBoughtIntDetail(this.portCode,dBeginDate,endDate,SecurityCode);
		 Iterator iterator = hashmap.keySet().iterator();
		 String sKeyManageType ="";
		 String ManageStr=SecurityCode+"#"+Ivmanage+"#"+Broker;;
		   double[] buylx = new double[2];
		 while(iterator.hasNext()){

	  		 String sKey = (String) iterator.next();
	  	  	 sKeyManageType = sKey.split("#")[0] + "#" + sKey.split("#")[2] +  sKey.split("#")[3] ;
	  		 if(ManageStr.equals(sKeyManageType)){
	  			 String sTmp1 = (String) hashmap.get(sKey);
	  			 dBal += Double.parseDouble(sTmp1.split("#")[1]);
	  			 dPortBal += Double.parseDouble(sTmp1.split("#")[2]);
	  		 }
		   }

		 buylx[0]=dBal;
		 buylx[1]=dPortBal;

		return buylx;
	}
	/**
	 * 将未实现的数据封装放入HashMap中。
	 *
	 * @param valueMap
	 *            HashMap
	 * @param rs
	 *            ResultSet
	 * @throws YssException
	 */
	private void setResultValue(HashMap valueMap, ResultSet rs)
			throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		if (null == rs) {
			return;
		}

		RealisedBean Realised = null;
		try {
			while (rs.next()) {
				Realised = new RealisedBean();
				Realised.Code = rs.getString("FCode");
				Realised.CCODE = rs.getString("FFCode");
				Realised.Name = rs.getString("FName");
				Realised.CuryCode1 = rs.getString("FCuryCode1");
				Realised.CuryCode2 = rs.getString("FCuryCode2");
				Realised.Rate1 = rs.getDouble("FRate1");
				Realised.Rate2 = rs.getDouble("FRate2");
				Realised.Type1 = rs.getString("FType1");
				Realised.Type2 = rs.getString("FType2");
				Realised.Type3 = rs.getString("FType3");
				Realised.Type4 = rs.getString("FType4");
				Realised.value0 = rs.getDouble("Fvalue0");
				Realised.value1 = rs.getDouble("Fvalue1");
				Realised.value2 = rs.getDouble("Fvalue2");
				Realised.value3 = rs.getDouble("Fvalue3");
				Realised.value4 = rs.getDouble("Fvalue4");
				Realised.value5 = rs.getDouble("Fvalue5");
				Realised.value6 = rs.getDouble("Fvalue6");
				Realised.Date1 = rs.getDate("FDate1");
				Realised.Date2 = rs.getDate("FDate2");
				Realised.Date3 = rs.getDate("FDate3");
				Realised.RealisedType = rs.getInt("FRealisedType");
				Realised.portCode = rs.getString("FPortCode");
				valueMap.put(Realised.Code, Realised);
			}
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
}

