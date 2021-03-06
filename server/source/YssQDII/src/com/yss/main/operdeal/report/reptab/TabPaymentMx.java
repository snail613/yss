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
 * 太平资产用表     06費用支出明細表
 * MS01748 QDV4太平2010年09月16日02_A  
 * 太平标准月报18张报表，需要保存，并能批量导出 
 * @author benson 2010.10.14
 *
 */
public class TabPaymentMx extends BaseAPOperValue {

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
	private class PaymentMxBean {
		// ~ 报表属性
		private String sIvPayCatCode = "";        //运营收支品种
		private String sportCode = "";            //组合代码
		private java.sql.Date dDate = null;       //日期
		private String sCuryCode = "";            //货币代码
		
		private double dMoney = 0;                //货币
		private double dBaseMoney = 0;            //港币等值金额
		private double dBaseCuryRate = 0;         //汇率          

		public void PaymentMxBean() {
		}

	}

	// =====================================================================================//
	public TabPaymentMx() {

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
		try {
			conn.setAutoCommit(false);
		    bTrans = true;
		    deletePaymentMxData();
		    getPaymentMx(valueMap);
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
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_rep_PaymentMx"))) {
				return;
			} else {
				strSql = "create table "
						+ pub.yssGetTableName("Tb_rep_PaymentMx")
						+ " (FIVPAYCATCODE  VARCHAR2(40),"
                        + " FMONEY  NUMBER(22),"
                        + " FCURYCODE  VARCHAR2(40),"
                        + " FBASECURYRATE  NUMBER(22,5),"
                        + " FBASEMONEY  NUMBER(22,2),"
                        //+ " FPORTCODE  VARCHAR2(40)"
                        //edit by qiuxufeng 20101116
                        + " FPORTCODE  VARCHAR2(40),"
                        + " FDATE  DATE)";

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

		PaymentMxBean paymentMx = null;
		int count = 1;
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		if (null == rs) {
			return;
		}

		try {
			while (rs.next()) {
				paymentMx = new PaymentMxBean();
				paymentMx.sIvPayCatCode = rs.getString("FIVPAYCATCODE");
				paymentMx.sportCode = rs.getString("FPORTCODE");
				paymentMx.dDate = rs.getDate("FDATE");
				paymentMx.sCuryCode = rs.getString("FCURYCODE");
				
				paymentMx.dMoney = rs.getDouble("FMONEY");
				paymentMx.dBaseMoney = rs.getDouble("FBASEMONEY");
				paymentMx.dBaseCuryRate = rs.getDouble("FBASECURYRATE");

				valueMap.put(count + "", paymentMx);
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
	private void insertToPaymentMxData(HashMap valueMap)
			throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		PaymentMxBean paymentMx = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				+ pub.yssGetTableName("Tb_rep_PaymentMx")
				+ " (FIVPAYCATCODE, FCURYCODE,FPortCode,FDate, "
				+ " FMONEY,FBASEMONEY,FBASECURYRATE)"
				+ " values(?,?,?,?,?,?,?)";
		try {
			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				paymentMx = (PaymentMxBean) valueMap.get((String) it.next());
				
				prst.setString(1, paymentMx.sIvPayCatCode);
				prst.setString(2, paymentMx.sCuryCode);
				prst.setString(3, paymentMx.sportCode);
				prst.setDate(4, paymentMx.dDate);
				
				prst.setDouble(5, YssFun.roundIt(paymentMx.dMoney, 2));
				prst.setDouble(6, YssFun.roundIt(paymentMx.dBaseMoney, 2));
				prst.setDouble(7, YssFun.roundIt(paymentMx.dBaseCuryRate,2));
				
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
	private void deletePaymentMxData() throws YssException {
		String sqlStr = "Delete from "
				+ pub.yssGetTableName("Tb_rep_PaymentMx")
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

	private void getPaymentMx(HashMap valueMap)throws YssException{
		ResultSet rs = null;
		String query = "";
		try{
			if(valueMap.isEmpty()){
				valueMap.clear();
			}
//			query = " select a.FIVPAYCATCODE, a.fivpaycatname,  FMoney,a.fcurycode, FBaseCuryRate,round(FMoney * fbasecuryrate,2) as FBaseMoney ,A.FPORTCODE " +
//					" from (select FSubTsfTypeCode as FIvPayCatCode,FSubTsfTypeName as FIvPayCatName,sum(FMoney * -1 * a1.finout) as FMoney,FCuryCode,Fportcode," +
//					" a1.fbasecuryrate from "+pub.yssGetTableName("Tb_Cash_SubTransfer")+" a1 join (select FNum, FSubTsfTypeCode from " +pub.yssGetTableName("Tb_Cash_Transfer")+
//					" where FCheckState = 1 and FDataSource = 0 and FTsfTypeCode = '03' and FTsfTypeCode = '03' and FTransferDate between "+dbl.sqlDate(this.dBeginDate)+" and " +
//					dbl.sqlDate(this.dEndDate)+" ) a2 on a1.FNum = a2.FNum left join (select FCashAccCode, FCuryCode from  "+pub.yssGetTableName("tb_para_cashaccount")+
//					" where FCheckState = 1) a3 on a1.FCashAccCode =a3.FCashAccCode left join (select FSubTsfTypeCode, FSubTsfTypeName from Tb_Base_SubTransferType  where " +
//					" FCheckState = 1) a4 on a2.FSubTsfTypeCode =a4.FSubTsfTypeCode where FCheckState = 1 and FPortCode = "+dbl.sqlString(this.portCode)+" group by " +
//					" FSubTsfTypeCode, FSubTsfTypeName, FCuryCode, Fportcode,fbasecuryrate union all select ivp.FIvPayCatCode as FIvPayCatCode,invest.FIvPayCatName as FIvPayCatName," +
//					" ivp.FMoney as FMoney,ivp.FCuryCode as FCuryCode,ivp.Fportcode,fbasecuryrate  from (select FIvpaycatCode,FCuryCode,sum(FMoney) as FMoney,Fportcode,fbasecuryrate" +
//					" from  "+pub.yssGetTableName("tb_data_investpayrec")+" where FCheckState = 1 and FTsfTypeCode in ('07','98') and FPortCode ="+dbl.sqlString(this.portCode)+"and " +
//					" FTransDate between "+dbl.sqlDate(this.dBeginDate)+" and "+ dbl.sqlDate(this.dEndDate)+" group by FIvpaycatcode, FCuryCode, Fportcode,fbasecuryrate) ivp left join " +
//					" (select FivPayCatCode, FIvPayCatName from tb_base_investpaycat  where FCheckState = 1) invest on ivp.FIvPayCatCode =invest.FIvPayCatCode) a";
			
			//edit by qiuxufeng 20101122 增加查询日期字段和修改未明确定义列错误
			query = " select a.FIVPAYCATCODE, a.fivpaycatname,  FMoney,a.fcurycode, FBaseCuryRate,round(FMoney * fbasecuryrate,2) as FBaseMoney ,A.FPORTCODE,a.FTransferDate as fdate " +
				" from (select a2.FSubTsfTypeCode as FIvPayCatCode,FSubTsfTypeName as FIvPayCatName,sum(FMoney * -1 * a1.finout) as FMoney,FCuryCode,Fportcode," +
				" a1.fbasecuryrate, FTransferDate from "+pub.yssGetTableName("Tb_Cash_SubTransfer")+" a1 join (select FNum, FSubTsfTypeCode, FTransferDate from " +pub.yssGetTableName("Tb_Cash_Transfer")+
				" where FCheckState = 1 and FDataSource = 0 and FTsfTypeCode = '03' and FTsfTypeCode = '03' and FTransferDate between "+dbl.sqlDate(this.dBeginDate)+" and " +
				dbl.sqlDate(this.dEndDate)+" ) a2 on a1.FNum = a2.FNum left join (select FCashAccCode, FCuryCode from  "+pub.yssGetTableName("tb_para_cashaccount")+
				" where FCheckState = 1) a3 on a1.FCashAccCode =a3.FCashAccCode left join (select FSubTsfTypeCode, FSubTsfTypeName from Tb_Base_SubTransferType  where " +
				" FCheckState = 1) a4 on a2.FSubTsfTypeCode =a4.FSubTsfTypeCode where FCheckState = 1 and FPortCode = "+dbl.sqlString(this.portCode)+" group by " +
				" a2.FSubTsfTypeCode, FSubTsfTypeName, FCuryCode, Fportcode,fbasecuryrate, FTransferDate union all select ivp.FIvPayCatCode as FIvPayCatCode,invest.FIvPayCatName as FIvPayCatName," +
				" ivp.FMoney as FMoney,ivp.FCuryCode as FCuryCode,ivp.Fportcode,fbasecuryrate, FTransDate as FTransferDate  from (select FIvpaycatCode,FCuryCode,sum(FMoney) as FMoney,Fportcode,fbasecuryrate, FTransDate" +
				" from  "+pub.yssGetTableName("tb_data_investpayrec")+" where FCheckState = 1 and FTsfTypeCode in ('07','98') and FPortCode ="+dbl.sqlString(this.portCode)+"and " +
				" FTransDate between "+dbl.sqlDate(this.dBeginDate)+" and "+ dbl.sqlDate(this.dEndDate)+" group by FIvpaycatcode, FCuryCode, Fportcode,fbasecuryrate, FTransDate) ivp left join " +
				" (select FivPayCatCode, FIvPayCatName from tb_base_investpaycat  where FCheckState = 1) invest on ivp.FIvPayCatCode =invest.FIvPayCatCode) a";

			rs = dbl.openResultSet(query);
			setResultValue(valueMap,rs);
			insertToPaymentMxData(valueMap);
		}catch(Exception e){
			throw new YssException();
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
}
