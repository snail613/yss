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
/**************************************************
 * 太平资产用表     07收入明細表
 * MS01748 QDV4太平2010年09月16日02_A  
 * 太平标准月报18张报表，需要保存，并能批量导出 
 * @author benson 2010.10.14
 *
 */
public class TabIncomeMx extends BaseAPOperValue {

	// ~ 前台传过来的参数
	private java.util.Date dBeginDate;
	private java.util.Date dEndDate;
	private String portCode;
	private boolean isCreate; // 是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询

	/**
	 * <p>
	 * Title:
	 * </p>
	 * 内部类
	 */
	private class IncomeMxBean {
		// ~ 报表属性
		private String sCashAccCode = "";         //运营收支品种
		private String sSecurityCode = "";        //证券代码   
		private java.sql.Date dDate = null;       //日期
		private String sPortCode = "";            //组合代码
		
		private double dBal = 0;                //货币
		private double dPortBal = 0;            //港币等值金额
		private double dAvgBaseCuryRate = 0;         //汇率          

		public void IncomeMxBean() {
		}

	}

	// =====================================================================================//
	public TabIncomeMx() {

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
		reqAry1 = reqAry[0].split("\r");
		this.dBeginDate = YssFun.toDate(reqAry1[1]);
		reqAry1 = reqAry[1].split("\r");
		this.dEndDate = YssFun.toDate(reqAry1[1]);
		reqAry1 = reqAry[2].split("\r");
		this.portCode = reqAry1[1];
		reqAry1 = reqAry[3].split("\r");
		if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0，则只查询已生成的报表数据
			this.isCreate = false;
		} else { // 生成报表
			this.isCreate = true;
		}
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
				processDealData(valueMap);
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
	private void processDealData(HashMap valueMap) throws YssException {

		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		try {
			conn.setAutoCommit(false);
		    bTrans = true;
            deleteIncomeMxData();
            
            getInterestIncome(valueMap);
            getBondInterestIncome(valueMap);
            getStockInterestIncome(valueMap);
            
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
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
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_rep_IncomeMx"))) {
				return;
			} else {
				strSql = "create table "
						+ pub.yssGetTableName("Tb_rep_IncomeMx")
						+ " (FCashAccCode  VARCHAR2(40),"
                        + " FSECURITYCODE  VARCHAR2(40),"
                        + " FBal  NUMBER(22,2),"
                        + " FAVGBASECURYRATE  NUMBER(22,5),"
                        + " FPORTBAL  NUMBER(22,2)," 
                        + " FPORTCODE VARCHAR2(40)," 
                        + " FDATE DATE)";

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

		IncomeMxBean incomeMx = null;
		int count = 1;
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		if (null == rs) {
			return;
		}

		try {
			while (rs.next()) {
				incomeMx = new IncomeMxBean();
				//edit by qiuxufeng 20101119 修改获取的字段名
				//incomeMx.sCashAccCode = rs.getString("FIVPAYCATCODE");
				incomeMx.sCashAccCode = rs.getString("FCashAccCode");
				//incomeMx.sPortCode = rs.getString("FPORTCODE");
				incomeMx.sPortCode = this.portCode;
				incomeMx.dDate = rs.getDate("FDATE");
				//incomeMx.sSecurityCode = rs.getString("FCURYCODE");
				incomeMx.sSecurityCode = rs.getString("FSecurityCode");
				
				//incomeMx.dBal = rs.getDouble("FMONEY");
				incomeMx.dBal = rs.getDouble("FBal");
				//incomeMx.dPortBal = rs.getDouble("FBASEMONEY");
				incomeMx.dPortBal = rs.getDouble("FPortBal");
				//incomeMx.dAvgBaseCuryRate = rs.getDouble("FBASECURYRATE");
				incomeMx.dAvgBaseCuryRate = rs.getDouble("FAvgBaseCuryRate");

				valueMap.put(count + "", incomeMx);
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
	private void insertToIncomeMxData(HashMap valueMap)
			throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		IncomeMxBean incomeMx = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				+ pub.yssGetTableName("Tb_rep_IncomeMx")
				+ " (FCashAccCode, FSECURITYCODE,FPortCode,FDate, "
				+ " FBal,FPORTBAL,FAVGBASECURYRATE)"
				+ " values(?,?,?,?,?,?,?)";
		try {
			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				incomeMx = (IncomeMxBean) valueMap.get((String) it.next());
				
				prst.setString(1, incomeMx.sCashAccCode);
				prst.setString(2, incomeMx.sSecurityCode);
				prst.setString(3, incomeMx.sPortCode);
				prst.setDate(4, incomeMx.dDate);
				
				prst.setDouble(5, YssFun.roundIt(incomeMx.dBal, 2));
				prst.setDouble(6, YssFun.roundIt(incomeMx.dPortBal, 2));
				prst.setDouble(7, YssFun.roundIt(incomeMx.dAvgBaseCuryRate,2));
				
				prst.addBatch();
			}
			prst.executeBatch();
		} catch (Exception e) {
			throw new YssException("插入當日買賣清算数据出错！" + e.getMessage());
		} finally {
			valueMap.clear();//add by qiuxufeng 每次插入完数据清空
			dbl.closeStatementFinal(prst);
		}
	}

	/**
	 * 从tb_rep_DaysSettlement表中按要求删除相关数据
	 * 
	 * @throws YssException
	 */
	private void deleteIncomeMxData() throws YssException {
		String sqlStr = "Delete from "
				+ pub.yssGetTableName("Tb_rep_IncomeMx")
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
	
    /**********************************
     * 利息收入_港币
     * @param valueMap
     * @throws YssException
     */
	private void getInterestIncome(HashMap valueMap) throws YssException{
		ResultSet rs = null;
		String query = "";
		try{
			if(valueMap.isEmpty()){
				valueMap.clear();
			}
			//edit by qiuxufeng 增加查询日期字段
			query = " select a1.FCashAccCode,' ' as FSecurityCode,sum(FMoney) as FBal, sum(round(FMoney * FBaseCuryRate, 2)) / sum(FMoney) as FAvgBaseCuryRate, " +
					//" sum(round(FMoney * FBaseCuryRate, 2)) as FPortBal, a2.FCuryCode from (select FCashaccCode, FInOut * FMoney as FMoney, FBaseCuryRate from " +
					" sum(round(FMoney * FBaseCuryRate, 2)) as FPortBal, a2.FCuryCode, a1.FTransferDate as fdate from (select FCashaccCode, FInOut * FMoney as FMoney, FBaseCuryRate, a12.FTransferDate from " +
					pub.yssGetTableName("tb_cash_subtransfer")+" a11 join "+pub.yssGetTableName("tb_cash_transfer")+" a12 on a11.FNum = a12.FNum " +
					" where a12.FTransferDate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" and a11.FPortCode = "+dbl.sqlString(this.portCode)+
					" and a12.FTsfTypeCode = '02' and a12.FSubTsfTypeCode = '02DE' and a12.FCheckState = 1) a1  join (select FCashAccCode, FCashAccName, FCuryCode from "+
					pub.yssGetTableName("tb_para_cashaccount")+" where FCheckState = 1) a2 on a1.FCashAccCode = a2.FCashAccCode  group by a2.FCuryCode, a1.FCashAccCode, " +
					//" a2.FCashAccName having sum(FMoney) <> 0 order by a2.FCuryCode, a1.FCashAccCode ";
					" a2.FCashAccName, a1.FTransferDate having sum(FMoney) <> 0 order by a2.FCuryCode, a1.FCashAccCode ";
	
			rs = dbl.openResultSet(query);
			setResultValue(valueMap,rs);
			insertToIncomeMxData(valueMap);
		}catch(Exception e){
			throw new YssException();
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	 /**********************************
     * 债券利息收入_港币
     * @param valueMap
     * @throws YssException
     */
	private void getBondInterestIncome(HashMap valueMap) throws YssException{
		ResultSet rs = null;
		String query = "";
		try{
			if(valueMap.isEmpty()){
				valueMap.clear();
			}
//			query = " select a1.FCashAccCode,a1.FSecurityCode,a2.FCuryCode,sum(FMoney) as FBal,sum(round(FMoney * FBaseCuryRate, 2)) / sum(FMoney) as FAvgBaseCuryRate," +
//					" sum(round(FMoney * FBaseCuryRate, 2)) as FPortBal from (select FCashAccCode, sum(FAccruedInterest) as FMoney,FBaseCuryRate, FSecurityCode from " +
//					pub.yssGetTableName("tb_data_subtrade")+" where FBargainDate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" and " +
//					" FPortCode = "+dbl.sqlString(this.portCode)+" and FTradeTypeCode in ('02', '06', '17') and FCheckState = 1 and FAccruedInterest > 0 group by " +
//					" FCashAccCode, FBaseCuryRate, FSecurityCode union select FCashAccCode, sum(FInout*FMoney) as FMoney, FBaseCuryRate, FSecurityCode from " +
//					pub.yssGetTableName("tb_cash_subtransfer")+"  a11 join "+pub.yssGetTableName("tb_cash_transfer")+" a12 on a11.FNum = a12.FNum and a11.FCheckState = 1" +
//					" and a12.FCheckState = 1 and a12.FTransferDate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" and a11.FPortCode = " +
//					dbl.sqlString(this.portCode)+" and a12.FSubTsfTypeCode = '02FI' group by FCashAccCode, FBaseCuryRate, FSecurityCode ) a1  join (select FCashAccCode, " +
//					" FCashAccName, FCuryCode from "+pub.yssGetTableName("tb_para_cashaccount")+"  where FCheckState = 1) a2 on a1.FCashAccCode = a2.FCashAccCode join (select " +
//					" FSecurityCode, FSecurityName from "+pub.yssGetTableName("tb_para_security")+" where FCheckState = 1 and FCatCode = 'FI') a3 on a1.FSecurityCode =a3.FSecurityCode" +
//					" group by a2.FCuryCode, a1.FCashAccCode, a1.FSecurityCode, a3.fSecurityName having sum(FMoney) <> 0 order by a1.FCashAccCode, a1.FSecurityCode, a3.FSecurityName, " +
//					" a2.FCuryCode";
			//edit by qiuxufeng 20101119 增加获取日期字段
			query = " select a1.FCashAccCode,a1.FSecurityCode,a2.FCuryCode,sum(FMoney) as FBal,sum(round(FMoney * FBaseCuryRate, 2)) / sum(FMoney) as FAvgBaseCuryRate," +
					" sum(round(FMoney * FBaseCuryRate, 2)) as FPortBal, a1.FTransferDate as fdate from (select FCashAccCode, sum(FAccruedInterest) as FMoney,FBaseCuryRate, FSecurityCode, FBargainDate as FTransferDate from " +
					pub.yssGetTableName("tb_data_subtrade")+" where FBargainDate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" and " +
					" FPortCode = "+dbl.sqlString(this.portCode)+" and FTradeTypeCode in ('02', '06', '17') and FCheckState = 1 and FAccruedInterest > 0 group by " +
					" FCashAccCode, FBaseCuryRate, FSecurityCode, FBargainDate union select FCashAccCode, sum(FInout*FMoney) as FMoney, FBaseCuryRate, FSecurityCode, a12.FTransferDate from " +
					pub.yssGetTableName("tb_cash_subtransfer")+"  a11 join "+pub.yssGetTableName("tb_cash_transfer")+" a12 on a11.FNum = a12.FNum and a11.FCheckState = 1" +
					" and a12.FCheckState = 1 and a12.FTransferDate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" and a11.FPortCode = " +
					dbl.sqlString(this.portCode)+" and a12.FSubTsfTypeCode = '02FI' group by FCashAccCode, FBaseCuryRate, FSecurityCode, FTransferDate ) a1  join (select FCashAccCode, " +
					" FCashAccName, FCuryCode from "+pub.yssGetTableName("tb_para_cashaccount")+"  where FCheckState = 1) a2 on a1.FCashAccCode = a2.FCashAccCode join (select " +
					" FSecurityCode, FSecurityName from "+pub.yssGetTableName("tb_para_security")+" where FCheckState = 1 and FCatCode = 'FI') a3 on a1.FSecurityCode =a3.FSecurityCode" +
					" group by a2.FCuryCode, a1.FCashAccCode, a1.FSecurityCode, a3.fSecurityName, a1.FTransferDate, a1.FTransferDate having sum(FMoney) <> 0 order by a1.FCashAccCode, a1.FSecurityCode, a3.FSecurityName, " +
					" a2.FCuryCode";
	
			rs = dbl.openResultSet(query);
			setResultValue(valueMap,rs);
			insertToIncomeMxData(valueMap);
		}catch(Exception e){
			throw new YssException();
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	 /**********************************
     * 股息收入_港币
     * @param valueMap
     * @throws YssException
     */
	private void getStockInterestIncome(HashMap valueMap) throws YssException{
		ResultSet rs = null;
		String query = "";
		try{
			if(valueMap.isEmpty()){
				valueMap.clear();
			}
//			query = " select a1.FCashAccCode, a1.FSecurityCode,  a2.FCuryCode,sum(FMoney) as FBal, sum(round(FMoney * FBaseCuryRate, 2)) / sum(FMoney) as FAvgBaseCuryRate, " +
//					" sum(round(FMoney * FBaseCuryRate, 2)) as FPortBal from (select FCashAccCode,FSecurityCode,  FInOut * FMoney as FMoney,FBaseCuryRate  from " +
//					pub.yssGetTableName("tb_cash_subtransfer")+" a11 join "+pub.yssGetTableName("tb_cash_transfer")+" a12 on a11.FNum = a12.FNum where a12.FTransferDate between " +
//					dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" and a11.FPortCode = "+dbl.sqlString(this.portCode)+" and a12.FTsfTypeCode = '02' and " +
//					" a12.FSubTsfTypeCode = '02DV' and a12.FCheckState = 1 and length(FSecurityCode) > 0) a1 join (select FCashAccCode, FCashAccName, FCuryCode from " +
//					pub.yssGetTableName("tb_para_cashaccount")+" where FCheckState = 1) a2 on a1.FCashAccCode = a2.FCashAccCode left join (select FSecurityCode, FSecurityName " +
//					" from "+pub.yssGetTableName("tb_para_security")+"  where FCheckState = 1) a3 on a1.FSecurityCode =a3.FSecurityCode group by a1.FCashAccCode,a2.FCuryCode, " +
//					" a1.FSecurityCode,a3.FSecurityName having sum(FMoney) <> 0 order by a1.FCashAccCode, a2.FCuryCode,a1.FSecurityCode,a3.FSecurityName";
			//edit by qiuxufeng 20101119 增加获取日期字段
			query = " select a1.FCashAccCode, a1.FSecurityCode,  a2.FCuryCode,sum(FMoney) as FBal, sum(round(FMoney * FBaseCuryRate, 2)) / sum(FMoney) as FAvgBaseCuryRate, " +
					" sum(round(FMoney * FBaseCuryRate, 2)) as FPortBal, a1.FTransferDate as fdate from (select FCashAccCode,FSecurityCode,  FInOut * FMoney as FMoney,FBaseCuryRate, a12.FTransferDate from " +
					pub.yssGetTableName("tb_cash_subtransfer")+" a11 join "+pub.yssGetTableName("tb_cash_transfer")+" a12 on a11.FNum = a12.FNum where a12.FTransferDate between " +
					dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" and a11.FPortCode = "+dbl.sqlString(this.portCode)+" and a12.FTsfTypeCode = '02' and " +
					" a12.FSubTsfTypeCode = '02DV' and a12.FCheckState = 1 and length(FSecurityCode) > 0) a1 join (select FCashAccCode, FCashAccName, FCuryCode from " +
					pub.yssGetTableName("tb_para_cashaccount")+" where FCheckState = 1) a2 on a1.FCashAccCode = a2.FCashAccCode left join (select FSecurityCode, FSecurityName " +
					" from "+pub.yssGetTableName("tb_para_security")+"  where FCheckState = 1) a3 on a1.FSecurityCode =a3.FSecurityCode group by a1.FCashAccCode,a2.FCuryCode, " +
					" a1.FSecurityCode,a3.FSecurityName, a1.FTransferDate having sum(FMoney) <> 0 order by a1.FCashAccCode, a2.FCuryCode,a1.FSecurityCode,a3.FSecurityName";

			rs = dbl.openResultSet(query);
			setResultValue(valueMap,rs);
			insertToIncomeMxData(valueMap);
		}catch(Exception e){
			throw new YssException();
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
