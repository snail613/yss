package com.yss.main.operdeal.report.reptab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.base.BaseAPOperValue;
import com.yss.main.dayfinish.OffAcctBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class TabManagerFee extends BaseAPOperValue {
	// =====================================================================================//

	// ~ 前台传过来的参数
	private java.util.Date dBeginDate;
	private java.util.Date dEndDate;
	private String portCode;
	private String invmgrCode;
	private boolean isCreate; // 是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询

	/**
	 * <p>
	 * Title:
	 * </p>
	 * 内部类
	 */
	private class ManagerFeeBean {
		// ~ 报表属性
		private String sCatCode = "";        //品种
		private String sportCode = "";       //组合代码
		private java.sql.Date dDate = null;  //日期
		private String sInvmgrCode = "";     //投资经理代码
		private String sCuryCode = "";       //货币代码
		
		private double dBfBal = 0;           //
		private double dBaseBfBal = 0;       //
		private double dPaid = 0;            //当月支付管理费
		private double dBasePaid = 0;        //
		private double dMoney = 0;           //
		private double dBaseMoney = 0;       //
		private double dBal = 0;             //当月结余
		private double dBaseBal = 0;         //

		public void ManagerFeeBean() {
		}

	}

	// =====================================================================================//
	public TabManagerFee() {

	}

	// =====================================================================================//

	public void init(Object bean) throws YssException {
		String reqAry[] = null;
		String reqAry1[] = null;
		String sRowStr = (String) bean;
		if (sRowStr.trim().length() == 0) {
			return;
		}
		reqAry = sRowStr.split("\n");
//		reqAry1 = reqAry[0].split("\r");
//		this.dBeginDate = YssFun.toDate(reqAry1[1]);
//		reqAry1 = reqAry[1].split("\r");
//		this.dEndDate = YssFun.toDate(reqAry1[1]);
//		reqAry1 = reqAry[2].split("\r");
//		this.portCode = reqAry1[1];
//		reqAry1 = reqAry[3].split("\r");
//		this.invmgrCode = reqAry1[1];
//		reqAry1 = reqAry[4].split("\r");
//		if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0，则只查询已生成的报表数据
//			this.isCreate = false;
//		} else { // 生成报表
//			this.isCreate = true;
//		}
		//==================修改解析控件的值，前台控件值为空时不传值导致解析出错  edit by qiuxufeng 20101109 
		for (int i = 0; i < reqAry.length; i++) {
			reqAry1 = reqAry[i].split("\r");
			if(reqAry1[0].equalsIgnoreCase("1")) {
				this.dBeginDate = YssFun.toDate(reqAry1[1]);
			} else if(reqAry1[0].equalsIgnoreCase("2")) {
				this.dEndDate = YssFun.toDate(reqAry1[1]);
			} else if(reqAry1[0].equalsIgnoreCase("3")) {
				this.portCode = reqAry1[1];
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
		try {
			valueMap = new HashMap();
			createTable();
			if (this.isCreate) {
				//===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
		  	  	OffAcctBean offAcct = new OffAcctBean();
				offAcct.setYssPub(this.pub);
		  	  	String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
		  	  	String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
		  	  	if(!tmpInfo.trim().equalsIgnoreCase("")) {
		  	  		return "<OFFACCT>" + tmpInfo;
		  	  	}
		  	  	//=================end=================
				processGetSummary(valueMap);
			}
			// ------------------------------------------------------------
		} catch (YssException ex) {
			throw new YssException(ex.getMessage());
		}
		return "";
	}

	// =====================================================================================//

	/**
	 * 执行从各个报表获取数据的动作
	 * 
	 * @throws YssException
	 */
	private void processGetSummary(HashMap valueMap) throws YssException {

		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		// ------------------------------------
		try {
			conn.setAutoCommit(false);

			bTrans = true;

			deleteMangerFeeData();
			getMangerFee_EQ(valueMap);
			getMangerFee_DE(valueMap);
			getMangerFee_FI(valueMap);
			getMangerFee_TR(valueMap);
			getMangerFee_WT(valueMap);
			getMangerFee_TOTAL(valueMap);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (SQLException ex1) {
			throw new YssException(ex1.getMessage());
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	// =====================================================================================//
	/**
	 * 生成数据表
	 * @throws YssException
	 */
	private void createTable() throws YssException {
		String strSql = "";
		try {
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_rep_mangerfee"))) {
				return;
			} else {
				strSql = "create table "
						+ pub.yssGetTableName("Tb_rep_mangerfee")
						+ " ( FCATNAME  VARCHAR2(100),"
						+ " FPortCode varchar2(20),"
						+ " Fdate date,"
						+ " FINVMGRNAME  VARCHAR2(100),"
						+ " FCURYCODE  VARCHAR2(40),"
						+ " FBFBAL  NUMBER(22,2),"
						+ " FBASEBFBAL  NUMBER(22,2),"
						+ " FPAID  NUMBER(22,2),"
						+ " FBASEPAID  NUMBER(22,2),"
                        + " FMONEY  NUMBER(22,2),"
                        + " FBASEMONEY  NUMBER(22,2),"
                        + " FBAL  NUMBER(22,2),"
                        + " FBASEBAL  NUMBER(22,2) )";

				dbl.executeSql(strSql);
			}

		} catch (Exception e) {
			throw new YssException("创建应付管理费表出错！" + e.getMessage());
		}
	}

	/**
	 * 将数据封装放入HashMap中。
	 * 
	 * @param valueMap
	 *            HashMap
	 * @param rs
	 *            ResultSet
	 * @throws YssException
	 */
	private void setResultValue(HashMap valueMap, ResultSet rs)
			throws YssException {

		ManagerFeeBean managerFee = null;
		int count = 1;
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		if (null == rs) {
			return;
		}

		try {
			while (rs.next()) {
				managerFee = new ManagerFeeBean();
				managerFee.sCatCode = rs.getString("FCatName");
				managerFee.sportCode = rs.getString("FPortcode");
				managerFee.dDate = rs.getDate("fDate");
				managerFee.sInvmgrCode = rs.getString("FInvMgrName");
				managerFee.sCuryCode = rs.getString("FCURYCODE");
				
				managerFee.dBfBal = rs.getDouble("FBFBal");
				managerFee.dBaseBfBal = rs.getDouble("FBaseBFBal");
				managerFee.dPaid = rs.getDouble("FPaid");
				managerFee.dBasePaid = rs.getDouble("FBasePaid");
				managerFee.dMoney = rs.getDouble("FMoney");
				managerFee.dBaseMoney = rs.getDouble("FBaseMoney");
				managerFee.dBal = rs.getDouble("FBal");
				managerFee.dBaseBal = rs.getDouble("FBaseBal");

				valueMap.put(count + "", managerFee);
				count++;
			}
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		}
	}

	/**
	 * 将数据插入数据库
	 * 
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 */
	private void insertToMangerFeeData(HashMap valueMap)
			throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		ManagerFeeBean managerFee = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				+ pub.yssGetTableName("Tb_rep_mangerfee")
				+ " (FCATNAME, FPortCode, Fdate, FINVMGRNAME, FCURYCODE, "
				+ " FBFBAL, FBASEBFBAL, FPAID, FBASEPAID, FMONEY, FBASEMONEY, FBAL, FBASEBAL)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				managerFee = (com.yss.main.operdeal.report.reptab.TabManagerFee.ManagerFeeBean) valueMap.get((String) it.next());
				
				prst.setString(1, managerFee.sCatCode);
				prst.setString(2, managerFee.sportCode);
				prst.setDate(3, managerFee.dDate);
				prst.setString(4, managerFee.sInvmgrCode);
				prst.setString(5, managerFee.sCuryCode);
				
				prst.setDouble(6, YssFun.roundIt(managerFee.dBfBal, 2));
				prst.setDouble(7, YssFun.roundIt(managerFee.dBaseBfBal,2));
				prst.setDouble(8, YssFun.roundIt(managerFee.dPaid, 2));
				prst.setDouble(9, YssFun.roundIt(managerFee.dBasePaid, 2));
				prst.setDouble(10, YssFun.roundIt(managerFee.dMoney, 2));
				prst.setDouble(11, YssFun.roundIt(managerFee.dBaseMoney, 2));
				prst.setDouble(12, YssFun.roundIt(managerFee.dBal, 2));
				prst.setDouble(13, YssFun.roundIt(managerFee.dBaseBal, 2));

				prst.executeUpdate();
			}
		} catch (Exception e) {
			throw new YssException("插入當日買賣清算数据出错！" + e.getMessage());
		} finally {
			dbl.closeStatementFinal(prst);
		}
	}

	/**
	 * 从tb_rep_DaysSettlement表中按要求删除相关数据
	 * 
	 * @throws YssException
	 */
	private void deleteMangerFeeData() throws YssException {
		String sqlStr = "Delete from "
				+ pub.yssGetTableName("Tb_rep_mangerfee")
				+ " where FDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPORTCODE ="+ dbl.sqlString(this.portCode);
		try {
			dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("删除當日買賣清算数据出错！" + e.getMessage());
		}

	}

	
	private void getMangerFee_EQ(HashMap valueMap) throws YssException {

		ResultSet rs = null;
		String query = " select FTransDate as fDate, fportcode, FCatName, FInvMgrName,s.FCuryCode,FBFBal,FBFBal * FBaseCuryRate as FBaseBFBal,FPaid,FPaid * FBaseCuryRate as FBasePaid,FMoney,FMoney * FBaseCuryRate as FBaseMoney, "
				+ " FBFBal + FPaid + FMoney as FBal,round(FBFBal * FBaseCuryRate, 2) + round(FPaid * FBaseCuryRate, 2) + round(FMoney * FBaseCuryRate, 2) as FBaseBal from (select a.FTransDate,a.fportcode,c.FCatName, "
				+ " d.FInvMgrName,a.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FPortCode,FTransDate,FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, "
				+ " FAnalysisCode1 from  "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode,FPortCode,FTransDate) a left join (select "
				+ " FCuryCode, -1*sum(FBal) as FBFBal, FAnalySisCode3, FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ " where FIvPayCatCode = 'IV001' and (FStorageDate = "
				+ dbl.sqlDate(this.dBeginDate)
				+ "- 1 and substr(fyearmonth, 0, 4) = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ",'yyyy') or "
				+ dbl.sqlDate(this.dBeginDate)
				+ " = to_date(to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'-01'||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'00') and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FAnalysisCode3 <> ' ' and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b  on a.FAnalysisCode1 = b.FAnalysisCode1 and "
				+ " a.FAnalysisCode3 = b.FAnalysisCode3 left join (select FCuryCode, sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1  from "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e  on a.FAnalysisCode1 = e.FAnalysisCode1 and a.FAnalysisCode3 = e.FAnalysisCode3 "
				+ " left join tb_base_category c on a.FAnalysisCode3 = c.FCatCode left join " + pub.yssGetTableName("tb_para_investmanager") + " d on a.FAnalysisCode1 = d.FInvMgrCode where a.FAnalysisCode3 = 'EQ' union  select "
				+ " a.FTransDate, a.fportcode, c.FCatName, d.FInvMgrName, b.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FPortCode,FTransDate,FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, "
				+ " FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode,FPortCode,FTransDate) a right join (select FCuryCode, -1*sum(FBal) as FBFBal, "
				+ " FAnalySisCode3, FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ " where FIvPayCatCode = 'IV001' and (FStorageDate = "
				+ dbl.sqlDate(this.dBeginDate)
				+ " - 1 and "
				+ "substr(fyearmonth, 0, 4) = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ",'yyyy') or "
				+ dbl.sqlDate(this.dBeginDate)
				+ " = to_date(to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'-01'"
				+ "||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'00') and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FAnalysisCode3 <> ' ' and "
				+ " FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b on a.FAnalysisCode3 = b.FAnalysisCode3 and a.FAnalysisCode1 = b.FAnalysisCode1 left join (select FCuryCode, "
				+ " sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1 from  "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e "
				+ " on b.FAnalysisCode1 = e.FAnalysisCode1 and b.FAnalysisCode3 = e.FAnalysisCode3 left join tb_base_category c on b.FAnalysisCode3 = c.FCatCode left join "
				+ pub.yssGetTableName("tb_para_investmanager")
				+ " d on b.FAnalysisCode1 = d.FInvMgrCode where b.FAnalysisCode3 = 'EQ' ) s left join (select rate1.FCuryCode, FBaseRate / FPortRate as FBaseCuryRate from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " rate1 "
				+ " join (select max(FValDate) as FValDate, FCuryCode from  "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " where FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FValDate <= "
				+ dbl.sqlDate(this.dEndDate)
				+ " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ ") t on s.FCuryCode = t.FCuryCode  order by FCatName";
		try {
			if (valueMap.isEmpty()) {
				valueMap.clear();
			}

			rs = dbl.openResultSet(query);
			setResultValue(valueMap, rs);
			insertToMangerFeeData(valueMap);
		} catch (Exception e) {
			throw new YssException();
		} finally {
			valueMap.clear();
			dbl.closeResultSetFinal(rs);
		}
	}

	private void getMangerFee_FI(HashMap valueMap) throws YssException {

		ResultSet rs = null;
		String query = " select FTransDate as fDate, fportcode, FCatName, FInvMgrName,s.FCuryCode,FBFBal,FBFBal * FBaseCuryRate as FBaseBFBal,FPaid,FPaid * FBaseCuryRate as FBasePaid,FMoney,FMoney * FBaseCuryRate as FBaseMoney, "
				+ " FBFBal + FPaid + FMoney as FBal,round(FBFBal * FBaseCuryRate, 2) + round(FPaid * FBaseCuryRate, 2) + round(FMoney * FBaseCuryRate, 2) as FBaseBal from (select a.FTransDate, a.fportcode, c.FCatName, "
				+ " d.FInvMgrName,a.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FPortCode, FTransDate, FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, "
				+ " FAnalysisCode1 from  "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FPortCode, FTransDate) a left join (select "
				+ " FCuryCode, -1*sum(FBal) as FBFBal, FAnalySisCode3, FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ " where FIvPayCatCode = 'IV001' and (FStorageDate = "
				+ dbl.sqlDate(this.dBeginDate)
				+ "- 1 and substr(fyearmonth, 0, 4) = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ",'yyyy') or "
				+ dbl.sqlDate(this.dBeginDate)
				+ " = to_date(to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'-01'||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'00') and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FAnalysisCode3 <> ' ' and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b  on a.FAnalysisCode1 = b.FAnalysisCode1 and "
				+ " a.FAnalysisCode3 = b.FAnalysisCode3 left join (select FCuryCode, sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1  from "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e  on a.FAnalysisCode1 = e.FAnalysisCode1 and a.FAnalysisCode3 = e.FAnalysisCode3 "
				+ " left join tb_base_category c on a.FAnalysisCode3 = c.FCatCode left join " + pub.yssGetTableName("tb_para_investmanager") + " d on a.FAnalysisCode1 = d.FInvMgrCode where a.FAnalysisCode3 = 'FI' union  select "
				+ " a.FTransDate, a.fportcode, c.FCatName, d.FInvMgrName, b.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FPortCode, FTransDate, FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, "
				+ " FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FPortCode, FTransDate) a right join (select FCuryCode, -1*sum(FBal) as FBFBal, "
				+ " FAnalySisCode3, FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ " where FIvPayCatCode = 'IV001' and (FStorageDate = "
				+ dbl.sqlDate(this.dBeginDate)
				+ " - 1 and "
				+ "substr(fyearmonth, 0, 4) = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ",'yyyy') or "
				+ dbl.sqlDate(this.dBeginDate)
				+ " = to_date(to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'-01'"
				+ "||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'00') and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FAnalysisCode3 <> ' ' and "
				+ " FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b on a.FAnalysisCode3 = b.FAnalysisCode3 and a.FAnalysisCode1 = b.FAnalysisCode1 left join (select FCuryCode, "
				+ " sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1 from  "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e "
				+ " on b.FAnalysisCode1 = e.FAnalysisCode1 and b.FAnalysisCode3 = e.FAnalysisCode3 left join tb_base_category c on b.FAnalysisCode3 = c.FCatCode left join "
				+ pub.yssGetTableName("tb_para_investmanager")
				+ " d on b.FAnalysisCode1 = d.FInvMgrCode where b.FAnalysisCode3 = 'FI' ) s left join (select rate1.FCuryCode, FBaseRate / FPortRate as FBaseCuryRate from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " rate1 "
				+ " join (select max(FValDate) as FValDate, FCuryCode from  "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " where FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FValDate <= "
				+ dbl.sqlDate(this.dEndDate)
				+ " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ ") t on s.FCuryCode = t.FCuryCode  order by FCatName";
		try {
			if (valueMap.isEmpty()) {
				valueMap.clear();
			}

			rs = dbl.openResultSet(query);
			setResultValue(valueMap, rs);
			insertToMangerFeeData(valueMap);
		} catch (Exception e) {
			throw new YssException();
		} finally {
			dbl.closeResultSetFinal(rs);
			valueMap.clear();
		}
	}

	private void getMangerFee_TR(HashMap valueMap) throws YssException {

		ResultSet rs = null;
		String query = " select FTransDate as fDate, fportcode, FCatName, FInvMgrName,s.FCuryCode,FBFBal,FBFBal * FBaseCuryRate as FBaseBFBal,FPaid,FPaid * FBaseCuryRate as FBasePaid,FMoney,FMoney * FBaseCuryRate as FBaseMoney, "
				+ " FBFBal + FPaid + FMoney as FBal,round(FBFBal * FBaseCuryRate, 2) + round(FPaid * FBaseCuryRate, 2) + round(FMoney * FBaseCuryRate, 2) as FBaseBal from (select a.FTransDate, a.fportcode, c.FCatName, "
				+ " d.FInvMgrName,a.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FPortCode, FTransDate, FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, "
				+ " FAnalysisCode1 from  "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FPortCode, FTransDate) a left join (select "
				+ " FCuryCode, -1*sum(FBal) as FBFBal, FAnalySisCode3, FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ " where FIvPayCatCode = 'IV001' and FCheckState = 1 and (FStorageDate = "
				+ dbl.sqlDate(this.dBeginDate)
				+ "- 1 and substr(fyearmonth, 0, 4) = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ",'yyyy') or "
				+ dbl.sqlDate(this.dBeginDate)
				+ " = to_date(to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'-01'||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'00') and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FAnalysisCode3 <> ' '  group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b  on a.FAnalysisCode1 = b.FAnalysisCode1 and "
				+ " a.FAnalysisCode3 = b.FAnalysisCode3 left join (select FCuryCode, sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1  from "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e  on a.FAnalysisCode1 = e.FAnalysisCode1 and a.FAnalysisCode3 = e.FAnalysisCode3 "
				+ " left join tb_base_category c on a.FAnalysisCode3 = c.FCatCode left join " + pub.yssGetTableName("tb_para_investmanager") + " d on a.FAnalysisCode1 = d.FInvMgrCode where a.FAnalysisCode3 = 'TR' union  select "
				+ " a.FTransDate, a.fportcode, c.FCatName, d.FInvMgrName, b.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FPortCode, FTransDate, FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, "
				+ " FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = " 
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FPortCode, FTransDate) a right join (select FCuryCode, -1*sum(FBal) as FBFBal, "
				+ " FAnalySisCode3, FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ " where FIvPayCatCode = 'IV001'  and (FStorageDate = "
				+ dbl.sqlDate(this.dBeginDate)
				+ " - 1 and "
				+ "substr(fyearmonth, 0, 4) = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ",'yyyy') or "
				+ dbl.sqlDate(this.dBeginDate)
				+ " = to_date(to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'-01'"
				+ "||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'00') and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FAnalysisCode3 <> ' ' and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b on a.FAnalysisCode3 = b.FAnalysisCode3 and a.FAnalysisCode1 = b.FAnalysisCode1 left join (select FCuryCode, "
				+ " sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1 from  "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e "
				+ " on b.FAnalysisCode1 = e.FAnalysisCode1 and b.FAnalysisCode3 = e.FAnalysisCode3 left join tb_base_category c on b.FAnalysisCode3 = c.FCatCode left join "
				+ pub.yssGetTableName("tb_para_investmanager")
				+ " d on b.FAnalysisCode1 = d.FInvMgrCode where b.FAnalysisCode3 = 'TR' ) s left join (select rate1.FCuryCode, FBaseRate / FPortRate as FBaseCuryRate from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " rate1 "
				+ " join (select max(FValDate) as FValDate, FCuryCode from  "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " where FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FValDate <= "
				+ dbl.sqlDate(this.dEndDate)
				+ " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ ") t on s.FCuryCode = t.FCuryCode  order by FCatName";
		try {
			if (valueMap.isEmpty()) {
				valueMap.clear();
			}

			rs = dbl.openResultSet(query);
			setResultValue(valueMap, rs);
			insertToMangerFeeData(valueMap);
		} catch (Exception e) {
			throw new YssException();
		} finally {
			dbl.closeResultSetFinal(rs);
			valueMap.clear();
		}
	}

	private void getMangerFee_WT(HashMap valueMap) throws YssException {

		ResultSet rs = null;
		String query = " select FTransDate as fDate, fportcode, FCatName, FInvMgrName,s.FCuryCode,FBFBal,FBFBal * FBaseCuryRate as FBaseBFBal,FPaid,FPaid * FBaseCuryRate as FBasePaid,FMoney,FMoney * FBaseCuryRate as FBaseMoney, "
				+ " FBFBal + FPaid + FMoney as FBal,round(FBFBal * FBaseCuryRate, 2) + round(FPaid * FBaseCuryRate, 2) + round(FMoney * FBaseCuryRate, 2) as FBaseBal from (select a.FTransDate, a.fportcode, c.FCatName, "
				+ " d.FInvMgrName,a.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FPortCode, FTransDate, FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, "
				+ " FAnalysisCode1 from  "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FPortCode, FTransDate) a left join (select "
				+ " FCuryCode, -1*sum(FBal) as FBFBal, FAnalySisCode3, FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ " where FIvPayCatCode = 'IV001' and FCheckState = 1 and (FStorageDate = "
				+ dbl.sqlDate(this.dBeginDate)
				+ "- 1 and substr(fyearmonth, 0, 4) = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ",'yyyy') or "
				+ dbl.sqlDate(this.dBeginDate)
				+ " = to_date(to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'-01'||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'00') and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FAnalysisCode3 <> ' '  group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b  on a.FAnalysisCode1 = b.FAnalysisCode1 and "
				+ " a.FAnalysisCode3 = b.FAnalysisCode3 left join (select FCuryCode, sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1  from "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e  on a.FAnalysisCode1 = e.FAnalysisCode1 and a.FAnalysisCode3 = e.FAnalysisCode3 "
				+ " left join tb_base_category c on a.FAnalysisCode3 = c.FCatCode left join " + pub.yssGetTableName("tb_para_investmanager") + " d on a.FAnalysisCode1 = d.FInvMgrCode where a.FAnalysisCode3 = 'WT' union  select "
				+ " a.FTransDate, a.fportcode, c.FCatName, d.FInvMgrName, b.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FPortCode, FTransDate, FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, "
				+ " FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FPortCode, FTransDate) a right join (select FCuryCode, -1*sum(FBal) as FBFBal, "
				+ " FAnalySisCode3, FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ " where FIvPayCatCode = 'IV001' and (FStorageDate = "
				+ dbl.sqlDate(this.dBeginDate)
				+ " - 1 and "
				+ "substr(fyearmonth, 0, 4) = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ",'yyyy') or "
				+ dbl.sqlDate(this.dBeginDate)
				+ " = to_date(to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'-01'"
				+ "||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'00') and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FAnalysisCode3 <> ' ' and "
				+ " FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b on a.FAnalysisCode3 = b.FAnalysisCode3 and a.FAnalysisCode1 = b.FAnalysisCode1 left join (select FCuryCode, "
				+ " sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1 from  "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e "
				+ " on b.FAnalysisCode1 = e.FAnalysisCode1 and b.FAnalysisCode3 = e.FAnalysisCode3 left join tb_base_category c on b.FAnalysisCode3 = c.FCatCode left join "
				+ pub.yssGetTableName("tb_para_investmanager")
				+ " d on b.FAnalysisCode1 = d.FInvMgrCode where b.FAnalysisCode3 = 'WT' ) s left join (select rate1.FCuryCode, FBaseRate / FPortRate as FBaseCuryRate from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " rate1 "
				+ " join (select max(FValDate) as FValDate, FCuryCode from  "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " where FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FValDate <= "
				+ dbl.sqlDate(this.dEndDate)
				+ " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ ") t on s.FCuryCode = t.FCuryCode  order by FCatName";
		try {
			if (valueMap.isEmpty()) {
				valueMap.clear();
			}

			rs = dbl.openResultSet(query);
			setResultValue(valueMap, rs);
			insertToMangerFeeData(valueMap);
		} catch (Exception e) {
			throw new YssException();
		} finally {
			dbl.closeResultSetFinal(rs);
			valueMap.clear();
		}
	}

	private void getMangerFee_DE(HashMap valueMap) throws YssException {

		ResultSet rs = null;
		String query = " select FTransDate as fDate, fportcode, FCatName, FInvMgrName,s.FCuryCode,FBFBal,FBFBal * FBaseCuryRate as FBaseBFBal,FPaid,FPaid * FBaseCuryRate as FBasePaid,FMoney,FMoney * FBaseCuryRate as FBaseMoney, "
				+ " FBFBal + FPaid + FMoney as FBal,round(FBFBal * FBaseCuryRate, 2) + round(FPaid * FBaseCuryRate, 2) + round(FMoney * FBaseCuryRate, 2) as FBaseBal from (select a.FTransDate, a.fportcode, c.FCatName, "
				+ " d.FInvMgrName,a.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FPortCode, FTransDate, FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, "
				+ " FAnalysisCode1 from  "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FPortCode, FTransDate) a left join (select "
				+ " FCuryCode, -1*sum(FBal) as FBFBal, FAnalySisCode3, FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ " where FIvPayCatCode = 'IV001' and (FStorageDate = "
				+ dbl.sqlDate(this.dBeginDate)
				+ "- 1 and substr(fyearmonth, 0, 4) = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ",'yyyy') or "
				+ dbl.sqlDate(this.dBeginDate)
				+ " = to_date(to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'-01'||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'00') and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FAnalysisCode3 <> ' ' and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b  on a.FAnalysisCode1 = b.FAnalysisCode1 and "
				+ " a.FAnalysisCode3 = b.FAnalysisCode3 left join (select FCuryCode, sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1  from "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e  on a.FAnalysisCode1 = e.FAnalysisCode1 and a.FAnalysisCode3 = e.FAnalysisCode3 "
				+ " left join tb_base_category c on a.FAnalysisCode3 = c.FCatCode left join " + pub.yssGetTableName("tb_para_investmanager") + " d on a.FAnalysisCode1 = d.FInvMgrCode where a.FAnalysisCode3 = 'MK' union  select "
				+ " a.FTransDate, a.fportcode, c.FCatName, d.FInvMgrName, b.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FPortCode, FTransDate, FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, "
				+ " FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FPortCode, FTransDate) a right join (select FCuryCode, -1*sum(FBal) as FBFBal, "
				+ " FAnalySisCode3, FAnalysisCode1 from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ " where FIvPayCatCode = 'IV001' and (FStorageDate = "
				+ dbl.sqlDate(this.dBeginDate)
				+ " - 1 and "
				+ "substr(fyearmonth, 0, 4) = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ",'yyyy') or "
				+ dbl.sqlDate(this.dBeginDate)
				+ " = to_date(to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'-01'"
				+ "||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("
				+ dbl.sqlDate(this.dBeginDate)
				+ ", 'yyyy')||'00') and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FAnalysisCode3 <> ' ' and "
				+ " FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b on a.FAnalysisCode3 = b.FAnalysisCode3 and a.FAnalysisCode1 = b.FAnalysisCode1 left join (select FCuryCode, "
				+ " sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1 from  "
				+ pub.yssGetTableName("tb_data_investpayrec")
				+ " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e "
				+ " on b.FAnalysisCode1 = e.FAnalysisCode1 and b.FAnalysisCode3 = e.FAnalysisCode3 left join tb_base_category c on b.FAnalysisCode3 = c.FCatCode left join "
				+ pub.yssGetTableName("tb_para_investmanager")
				+ " d on b.FAnalysisCode1 = d.FInvMgrCode where b.FAnalysisCode3 = 'MK' ) s left join (select rate1.FCuryCode, FBaseRate / FPortRate as FBaseCuryRate from "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " rate1 "
				+ " join (select max(FValDate) as FValDate, FCuryCode from  "
				+ pub.yssGetTableName("tb_data_valrate")
				+ " where FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " and FValDate <= "
				+ dbl.sqlDate(this.dEndDate)
				+ " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ ") t on s.FCuryCode = t.FCuryCode  order by FCatName";
		try {
			if (valueMap.isEmpty()) {
				valueMap.clear();
			}

			rs = dbl.openResultSet(query);
			setResultValue(valueMap, rs);
			insertToMangerFeeData(valueMap);
		} catch (Exception e) {
			throw new YssException();
		} finally {
			dbl.closeResultSetFinal(rs);
			valueMap.clear();
		}
	}

	private void getMangerFee_TOTAL(HashMap valueMap) throws YssException {
	
	ResultSet rs = null;
	String query = " select FPortCode, FTransDate as fdate, '所有管理費Total(總額)' as FCatName, ' ' as FInvMgrName,'' as FCuryCode,'' as FBFBal,sum(FBFBal * FBaseCuryRate) as FBaseBFBal,'' as FPaid,sum(FPaid * FBaseCuryRate) as FBasePaid," +
			       " '' as FMoney,sum(FMoney * FBaseCuryRate) as FBaseMoney, '' as FBal," +
			       " sum(round(FBFBal * FBaseCuryRate, 2) + round(FPaid * FBaseCuryRate, 2) + round(FMoney * FBaseCuryRate, 2)) as FBaseBal from (select FTransDate, c.FCatName, " +
			       " d.FInvMgrName,a.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FTransDate, FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, " +
			       " FAnalysisCode1 from  "+pub.yssGetTableName("tb_data_investpayrec")+" where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "+dbl.sqlDate(this.dBeginDate)+
			       " and "+dbl.sqlDate(this.dEndDate)+" and FPortCode = "+dbl.sqlString(this.portCode)+" and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FTransDate) a left join (select " +
			       " FCuryCode, -1*sum(FBal) as FBFBal, FAnalySisCode3, FAnalysisCode1 from "+pub.yssGetTableName("tb_stock_invest")+" where FIvPayCatCode = 'IV001' and FCheckState = 1 and (FStorageDate = "+
			       dbl.sqlDate(this.dBeginDate)+"- 1 and substr(fyearmonth, 0, 4) = to_char("+dbl.sqlDate(this.dBeginDate)+",'yyyy') or "+dbl.sqlDate(this.dBeginDate)+" = to_date(to_char("+
			       dbl.sqlDate(this.dBeginDate)+", 'yyyy')||'-01'||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("+dbl.sqlDate(this.dBeginDate)+", 'yyyy')||'00') and FPortCode = " +
			       dbl.sqlString(this.portCode)+" and FAnalysisCode3 <> ' '  group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b  on a.FAnalysisCode1 = b.FAnalysisCode1 and " +
			       " a.FAnalysisCode3 = b.FAnalysisCode3 left join (select FCuryCode, sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1  from "+pub.yssGetTableName("tb_data_investpayrec")+
			       " where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+"  and FPortCode = "+
			       dbl.sqlString(this.portCode)+" and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) e  on a.FAnalysisCode1 = e.FAnalysisCode1 and a.FAnalysisCode3 = e.FAnalysisCode3 " +
			       " left join tb_base_category c on a.FAnalysisCode3 = c.FCatCode left join "+pub.yssGetTableName("tb_para_investmanager")+" d on a.FAnalysisCode1 = d.FInvMgrCode  union  select " +
			       " FTransDate, c.FCatName, d.FInvMgrName, b.FCuryCode,nvl(b.FBFBal, 0) as FBFBal,nvl(e.FPaid, 0) as FPaid,nvl(a.FMoney, 0) as FMoney from (select FTransDate, FCuryCode, -1*sum(FMoney) as FMoney, FAnalysisCode3, " +
			       " FAnalysisCode1 from "+pub.yssGetTableName("tb_data_investpayrec")+" where FIvPayCatCode = 'IV001' and FTsfTypeCode = '07' and FTransDate between "+dbl.sqlDate(this.dBeginDate)+" and "+
			       dbl.sqlDate(this.dEndDate)+" and FPortCode = " + dbl.sqlString(this.portCode) + " and FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FTransDate) a right join (select FCuryCode, -1*sum(FBal) as FBFBal, " +
			       " FAnalySisCode3, FAnalysisCode1 from "+pub.yssGetTableName("tb_stock_invest")+" where FIvPayCatCode = 'IV001' and FCheckState = 1 and (FStorageDate = "+dbl.sqlDate(this.dBeginDate)+" - 1 and " +
			       "substr(fyearmonth, 0, 4) = to_char("+dbl.sqlDate(this.dBeginDate)+",'yyyy') or "+dbl.sqlDate(this.dBeginDate)+" = to_date(to_char("+dbl.sqlDate(this.dBeginDate)+", 'yyyy')||'-01'" +
			       "||'-01', 'yyyy-MM-dd') and fyearmonth = to_char("+dbl.sqlDate(this.dBeginDate)+", 'yyyy')||'00') and FPortCode = "+dbl.sqlString(this.portCode)+" and FAnalysisCode3 <> ' ' and " +
			       " FCheckState = 1 group by FAnalysisCode3, FAnalysisCode1, FCuryCode) b on a.FAnalysisCode3 = b.FAnalysisCode3 and a.FAnalysisCode1 = b.FAnalysisCode1 left join (select FCuryCode, " +
			       " sum(FMoney) as FPaid, FAnalysisCode3, FAnalysisCode1 from  "+pub.yssGetTableName("tb_data_investpayrec")+" where FIvPayCatCode = 'IV001' and FTsfTypeCode = '03' and FTransDate between "+
			       dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" and FPortCode = "+dbl.sqlString(this.portCode)+"  group by FAnalysisCode3, FAnalysisCode1, FCuryCode, FTransDate) e " +
			       " on b.FAnalysisCode1 = e.FAnalysisCode1 and b.FAnalysisCode3 = e.FAnalysisCode3 left join tb_base_category c on b.FAnalysisCode3 = c.FCatCode left join "+pub.yssGetTableName("tb_para_investmanager")+
			       " d on b.FAnalysisCode1 = d.FInvMgrCode  ) s left join (select rate1.FPortCode as FPortCode, rate1.FCuryCode, FBaseRate / FPortRate as FBaseCuryRate from "+pub.yssGetTableName("tb_data_valrate")+" rate1 " +
			       " join (select max(FValDate) as FValDate, FCuryCode from  "+pub.yssGetTableName("tb_data_valrate")+" where FPortCode = "+dbl.sqlString(this.portCode)+" and FValDate <= "+dbl.sqlDate(this.dEndDate)+
			       " group by FCuryCode) rate2 on rate1.FCuryCode = rate2.FCuryCode and rate1.FValDate = rate2.FValDate where rate1.FPortCode = "+dbl.sqlString(this.portCode)+") t on s.FCuryCode = t.FCuryCode group by FPortCode, FTransDate";
	try{
		if(valueMap.isEmpty()){
			valueMap.clear();
		}

		rs = dbl.openResultSet(query);
		setResultValue(valueMap,rs);
		insertToMangerFeeData(valueMap);
	}catch(Exception e){
		throw new YssException();
	}finally{
		dbl.closeResultSetFinal(rs);
		valueMap.clear();
	}
}
}
