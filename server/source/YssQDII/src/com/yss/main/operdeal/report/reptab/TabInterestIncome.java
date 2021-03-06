package com.yss.main.operdeal.report.reptab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.base.BaseAPOperValue;
import com.yss.main.dayfinish.OffAcctBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/****************************************************
 * <p>Title: 【17 利息收入分布表】</p>
 *
 * <p>Description:获取各个之前生成的报表中的数据，加以处理获取相应的值</p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * <p>Company:ysstech </p>
 *
 * @author jiangshichao
 * @version 1.0
 */
public class TabInterestIncome extends BaseAPOperValue {

	// ~ 前台传过来的参数
	private java.util.Date dBeginDate;
	private java.util.Date dEndDate;
	private String portCode;
	private boolean isCreate; // 是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询
	int count = 1;//add by qiuxufeng 存放在hashmap的key值

	/**
	 * <p>
	 * Title:
	 * </p>
	 * 内部类
	 */
	private class InterestIncomeBean {
		// ~ 报表属性
		private String sportCode = "";            //组合代码
		private String sCaption = "";
		private java.sql.Date dDate = null;       //日期
		
		private double dAllMoney = 0;                //货币   

		public void InterestIncomeBean() {
		}

	}

	// =====================================================================================//
	public TabInterestIncome() {

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
		double PortCost = 0;
		double Accumulated = 0;
		double monthCost = 0;
		double netFirst = 0;
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		// --- sj modified 20090707 第2个净值数据和管理费
		double netSecond = 0D;
		double manageFee = 0D;
		// ------------------------------------
		try {
			conn.setAutoCommit(false);
		} catch (SQLException ex) {
		}
		bTrans = true;

		if (!dbl.yssTableExist(pub.yssGetTableName("tb_Data_Summary"))) {
			throw new YssException("Summary表不存在！");
		}
		deleteInterestIncomeData();
		getInterestIncomeData(valueMap);//add by qiuxufeng 获取数据到持久化表中 QDV4太平2010年09月16日02_A
		//insertToInterestIncomeData(valueMap);

		// ----------------------------------------------------------------------
		try {
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
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_rep_InterestIncome"))) {
				return;
			} else {
				strSql = "create table "
						+ pub.yssGetTableName("Tb_rep_InterestIncome")
						+ " (FCAPTION  VARCHAR2(74)," 
	                    + " FALLMONEY  NUMBER(22,2),"
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

		InterestIncomeBean interestIncome = null;
//		int count = 1;
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		if (null == rs) {
			return;
		}

		try {
			while (rs.next()) {
				interestIncome = new InterestIncomeBean();
				interestIncome.sportCode = rs.getString("FPORTCODE");
				interestIncome.dDate = rs.getDate("FDATE");
				interestIncome.sCaption = rs.getString("FCAPTION");
				
				interestIncome.dAllMoney = rs.getDouble("FALLMONEY");

				valueMap.put(count + "", interestIncome);
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
	private void insertToInterestIncomeData(HashMap valueMap)
			throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		InterestIncomeBean interestIncome = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				+ pub.yssGetTableName("Tb_rep_InterestIncome")
				+ " (FPortCode,FDate,FCAPTION,FALLMONEY)"
				+ " values(?,?,?,?)";
		try {
			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				interestIncome = (InterestIncomeBean)valueMap.get((String) it.next());
				
				prst.setString(1, interestIncome.sportCode);
				prst.setDate(2, interestIncome.dDate);
				
				prst.setString(3, interestIncome.sCaption);
				prst.setDouble(4, YssFun.roundIt(interestIncome.dAllMoney, 2));
				
//				prst.executeUpdate();
				prst.addBatch();
			}
			prst.executeBatch();
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
	private void deleteInterestIncomeData() throws YssException {
		String sqlStr = "Delete from "
				+ pub.yssGetTableName("Tb_rep_InterestIncome")
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

	private void getInterestIncomeData(HashMap valueMap)throws YssException{
		ResultSet rs = null;
		String query = "";
		try{
			if(valueMap.isEmpty()){
				valueMap.clear();
			}
//			query = " select FCaption,FAllMoney from (select '1' as FOrder,'INCOME CASH RECEIVED (現金收入－利息)' as FCaption,NVL(sum(FPortBal),0) as FAllMoney from " +
//					" (select sum(FPortBal) as FPortBal from (select a1.FCashAccCode,a2.FCashAccName,sum(FMoney) as FBal,sum(round(FMoney * FBaseCuryRate,2)) as FPortBal," +
//					" a2.FCuryCode from (select FCashaccCode, FInOut * FMoney as FMoney, FBaseCuryRate from "+pub.yssGetTableName("tb_cash_subtransfer")+" a11 join " +
//					pub.yssGetTableName("tb_cash_transfer")+" a12 on a11.FNum = a12.FNum where a12.FTransferDate between "+dbl.sqlDate(this.dBeginDate)+" and "+
//					dbl.sqlDate(this.dEndDate)+" and a11.FPortCode = "+dbl.sqlString(this.portCode)+" and a12.FTsfTypeCode = '02' and a12.FSubTsfTypeCode = '02DE' and " +
//					" a12.FCheckState = 1) a1 join (select FCashAccCode, FCashAccName, FCuryCode from "+pub.yssGetTableName("tb_para_cashaccount")+" where FCheckState = 1) " +
//					" a2 on a1.FCashAccCode = a2.FCashAccCode group by a2.FCuryCode, a1.FCashAccCode, a2.FCashAccName   having sum(FMoney) <> 0 order by a2.FCuryCode, a1.FCashAccCode )) " +
//					" union select '2' as FOrder,'INCOME CASH DIVIDEND (現金收入－股息)' as FCaption,NVL(FPortBal,0) as FAllMoney from (select sum(round(FMoney * FBaseCuryRate,2)) as " +
//					" FPortBal from (select FInOut * FMoney as FMoney, FBaseCuryRate from "+pub.yssGetTableName("tb_cash_subtransfer")+" a11 join tb_<Group>_cash_transfer a12 on " +
//					" a11.FNum = a12.FNum where a12.FTransferDate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" and a11.FPortCode = "+dbl.sqlString(this.portCode)+
//					" and a12.FTsfTypeCode = '02' and a12.FSubTsfTypeCode = '02DV' and a12.FCheckState = 1 and length(FSecurityCode) > 0) a1  having sum(FMoney) <> 0  ) " +
//					" union select '4' as FOrder,'INTEREST BOUGHT(买入利息)' as Caption,(select (danyue-shangyue-buyinterest) as portinterest from (select nvl(sum(round(FPortCuryCost,2)),0) " +
//					" as danyue ,1 as a from Tb_<Group>_Stock_PurBond  u where u.fcheckstate=1 and  u.fstoragedate=add_months("+dbl.sqlDate(this.dBeginDate)+",1) and u.fportcode="+dbl.sqlString(this.portCode)+" ) a "+
//					" left join (select nvl(sum(round(FPortCuryCost,2)),0) as shangyue,1 as a from Tb_<Group>_Stock_PurBond c where c.fcheckstate=1 and c.fstoragedate="+dbl.sqlDate(this.dBeginDate)+"and " +
//					" c.fportcode="+dbl.sqlString(this.portCode)+" ) b  on a.a=b.a  left join ( select nvl(sum(round(round(f.faccruedinterest,2)*f.fbasecuryrate,2)),0) as buyinterest ,1 as a from " +
//					pub.yssGetTableName("tb_data_subtrade")+" f  join tb_<Group>_para_security k on f.fsecuritycode=k.fsecuritycode  where f.fcheckstate=1 and  k.fcheckstate=1 and k.fcatcode='FI' and " +
//					" f.fportcode="+dbl.sqlString(this.portCode)+" and f.ftradetypecode='01' and f.fbargaindate between  "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" )c on a.a=c.a " +
//					" ) as Accruedinterest  from dual union select '5' as FOrder,'DIVIDEND CHANGE (应收利息/红利变动)' as FCaption, DIVIDEND  as FAllMoney from (select aa-bb as DIVIDEND from (select 1 as a, " +
//					" sum(round(ffactsettlemoney*cc.fbasecuryrate,2)) as aa from "+pub.yssGetTableName("tb_data_subtrade")+" cc where cc.fportcode = "+dbl.sqlString(this.portCode)+" and cc.fcheckstate=1 " +
//					" and cc.ftradetypecode = '06' and cc.fbargaindate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" ) aa left join (select 1 as a, NVL(sum(" +
//					" round(ffactsettlemoney*cc.fbasecuryrate,2)),0) as bb from "+pub.yssGetTableName("tb_data_subtrade")+" cc where cc.fportcode = "+dbl.sqlString(this.portCode)+" and cc.fcheckstate=1 and " +
//					" cc.ftradetypecode = '06' and cc.fFActsettledate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" )bb on  aa.a=bb.a) union select '3' as FOrder,'INCOME CASH " +
//					"RECEIVABLE(应收现金收入)' as FCaption,sum(FRec) as FAllMoney from( select round((sum(round(FLXVBal,2))-sum(round(FBFLXBal,2))),2) as Frec from (select * from tb_data_PortfolioVal where  " +
//					" FPORTCODE="+dbl.sqlString(this.portCode)+" and FValDate="+dbl.sqlDate(this.dEndDate)+" and   forder like 'FI__%'  and  forder like '%__total%' ) union select round(sum(FLXVBal),2)-" +
//					"round(sum(FBFLXBal),2) as FRec from ( select FLXVBal,FBFLXBal from tb_data_PortfolioVal where  FPORTCODE="+dbl.sqlString(this.portCode)+" and FValDate="+dbl.sqlDate(this.dEndDate)+" and " +
//					" forder like '%0102%'   and   forder like '%total%' ) union select FBaseCuryBal as FRec from (select sum(FMoney) as FBaseCuryBal from (select sum(ROUND(FAccruedInterest*TRA.FBASECURYRATE,2)) " +
//					" as FMoney from "+pub.yssGetTableName("tb_data_subtrade")+" tra where FBargainDate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+" and FPortCode = "+
//					dbl.sqlString(this.portCode)+" and tra.fcheckstate=1 and exists (select * from "+pub.yssGetTableName("tb_para_security")+" par where fcatcode='FI' and par.fcheckstate=1 AND tra.fsecuritycode=fsecuritycode)" +
//					" and FTradeTypeCode in ('02', '17') and FAccruedInterest > 0 union  select sum(round(FInout*FMoney*FBaseCuryRate,2)) as FMoney from "+pub.yssGetTableName("tb_cash_subtransfer")+" a11 join "+pub.yssGetTableName("tb_cash_transfer")+
//					" a12 on a11.FNum = a12.FNum and a11.FCheckState = 1 and a12.FCheckState = 1 and a12.FTransferDate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate)+"  and a11.FPortCode = "+dbl.sqlString(this.portCode)+
//					" and a12.FSubTsfTypeCode = '02FI' )))) order by FOrder";
			//edit by qiuxufeng 将每天的利息收入存放入持久化表
			count = 1;//初始化key为1
			int days = YssFun.dateDiff(this.dBeginDate, this.dEndDate);
			for(int i = 0; i < days; i++) {
				Date tmpDate = YssFun.addDay(this.dBeginDate, i);
				query = " select " + dbl.sqlDate(tmpDate) + " as FDate, " + dbl.sqlString(this.portCode) + " as FPortCode, FCaption,FAllMoney from (select '1' as FOrder,'INCOME CASH RECEIVED (現金收入－利息)' as FCaption,NVL(sum(FPortBal),0) as FAllMoney from " +
						" (select sum(FPortBal) as FPortBal from (select a1.FCashAccCode,a2.FCashAccName,sum(FMoney) as FBal,sum(round(FMoney * FBaseCuryRate,2)) as FPortBal," +
						" a2.FCuryCode from (select FCashaccCode, FInOut * FMoney as FMoney, FBaseCuryRate from "+pub.yssGetTableName("tb_cash_subtransfer")+" a11 join " +
						pub.yssGetTableName("tb_cash_transfer")+" a12 on a11.FNum = a12.FNum where a12.FTransferDate between "+dbl.sqlDate(tmpDate)+" and "+
						dbl.sqlDate(tmpDate)+" and a11.FPortCode = "+dbl.sqlString(this.portCode)+" and a12.FTsfTypeCode = '02' and a12.FSubTsfTypeCode = '02DE' and " +
						" a12.FCheckState = 1) a1 join (select FCashAccCode, FCashAccName, FCuryCode from "+pub.yssGetTableName("tb_para_cashaccount")+" where FCheckState = 1) " +
						" a2 on a1.FCashAccCode = a2.FCashAccCode group by a2.FCuryCode, a1.FCashAccCode, a2.FCashAccName   having sum(FMoney) <> 0 order by a2.FCuryCode, a1.FCashAccCode )) " +
						" union select '2' as FOrder,'INCOME CASH DIVIDEND (現金收入－股息)' as FCaption,NVL(FPortBal,0) as FAllMoney from (select sum(round(FMoney * FBaseCuryRate,2)) as " +
						" FPortBal from (select FInOut * FMoney as FMoney, FBaseCuryRate from "+pub.yssGetTableName("tb_cash_subtransfer")+" a11 join "+pub.yssGetTableName("tb_cash_transfer")+" a12 on " +
						" a11.FNum = a12.FNum where a12.FTransferDate between "+dbl.sqlDate(tmpDate)+" and "+dbl.sqlDate(tmpDate)+" and a11.FPortCode = "+dbl.sqlString(this.portCode)+
						" and a12.FTsfTypeCode = '02' and a12.FSubTsfTypeCode = '02DV' and a12.FCheckState = 1 and length(FSecurityCode) > 0) a1  having sum(FMoney) <> 0  ) " +
						" union select '4' as FOrder,'INTEREST BOUGHT(买入利息)' as Caption,(select (danyue-shangyue-buyinterest) as portinterest from (select nvl(sum(round(FPortCuryCost,2)),0) " +
						" as danyue ,1 as a from "+pub.yssGetTableName("Tb_Stock_PurBond")+" u where u.fcheckstate=1 and  u.fstoragedate=add_months("+dbl.sqlDate(tmpDate)+",1) and u.fportcode="+dbl.sqlString(this.portCode)+" ) a "+
						" left join (select nvl(sum(round(FPortCuryCost,2)),0) as shangyue,1 as a from "+pub.yssGetTableName("Tb_Stock_PurBond")+" c where c.fcheckstate=1 and c.fstoragedate="+dbl.sqlDate(tmpDate)+"and " +
						" c.fportcode="+dbl.sqlString(this.portCode)+" ) b  on a.a=b.a  left join ( select nvl(sum(round(round(f.faccruedinterest,2)*f.fbasecuryrate,2)),0) as buyinterest ,1 as a from " +
						pub.yssGetTableName("tb_data_subtrade")+" f  join "+pub.yssGetTableName("tb_para_security")+" k on f.fsecuritycode=k.fsecuritycode  where f.fcheckstate=1 and  k.fcheckstate=1 and k.fcatcode='FI' and " +
						" f.fportcode="+dbl.sqlString(this.portCode)+" and f.ftradetypecode='01' and f.fbargaindate between  "+dbl.sqlDate(tmpDate)+" and "+dbl.sqlDate(tmpDate)+" )c on a.a=c.a " +
						" ) as Accruedinterest  from dual union select '5' as FOrder,'DIVIDEND CHANGE (应收利息/红利变动)' as FCaption, DIVIDEND  as FAllMoney from (select aa-bb as DIVIDEND from (select 1 as a, " +
						" sum(round(ffactsettlemoney*cc.fbasecuryrate,2)) as aa from "+pub.yssGetTableName("tb_data_subtrade")+" cc where cc.fportcode = "+dbl.sqlString(this.portCode)+" and cc.fcheckstate=1 " +
						" and cc.ftradetypecode = '06' and cc.fbargaindate between "+dbl.sqlDate(tmpDate)+" and "+dbl.sqlDate(tmpDate)+" ) aa left join (select 1 as a, NVL(sum(" +
						" round(ffactsettlemoney*cc.fbasecuryrate,2)),0) as bb from "+pub.yssGetTableName("tb_data_subtrade")+" cc where cc.fportcode = "+dbl.sqlString(this.portCode)+" and cc.fcheckstate=1 and " +
						" cc.ftradetypecode = '06' and cc.fFActsettledate between "+dbl.sqlDate(tmpDate)+" and "+dbl.sqlDate(tmpDate)+" )bb on  aa.a=bb.a) union select '3' as FOrder,'INCOME CASH " +
						"RECEIVABLE(应收现金收入)' as FCaption,sum(FRec) as FAllMoney from( select round((sum(round(FLXVBal,2))-sum(round(FBFLXBal,2))),2) as Frec from (select * from tb_data_PortfolioVal where  " +
						" FPORTCODE="+dbl.sqlString(this.portCode)+" and FValDate="+dbl.sqlDate(tmpDate)+" and   forder like 'FI__%'  and  forder like '%__total%' ) union select round(sum(FLXVBal),2)-" +
						"round(sum(FBFLXBal),2) as FRec from ( select FLXVBal,FBFLXBal from tb_data_PortfolioVal where  FPORTCODE="+dbl.sqlString(this.portCode)+" and FValDate="+dbl.sqlDate(tmpDate)+" and " +
						" forder like '%0102%'   and   forder like '%total%' ) union select FBaseCuryBal as FRec from (select sum(FMoney) as FBaseCuryBal from (select sum(ROUND(FAccruedInterest*TRA.FBASECURYRATE,2)) " +
						" as FMoney from "+pub.yssGetTableName("tb_data_subtrade")+" tra where FBargainDate between "+dbl.sqlDate(tmpDate)+" and "+dbl.sqlDate(tmpDate)+" and FPortCode = "+
						dbl.sqlString(this.portCode)+" and tra.fcheckstate=1 and exists (select * from "+pub.yssGetTableName("tb_para_security")+" par where fcatcode='FI' and par.fcheckstate=1 AND tra.fsecuritycode=fsecuritycode)" +
						" and FTradeTypeCode in ('02', '17') and FAccruedInterest > 0 union  select sum(round(FInout*FMoney*FBaseCuryRate,2)) as FMoney from "+pub.yssGetTableName("tb_cash_subtransfer")+" a11 join "+pub.yssGetTableName("tb_cash_transfer")+
						" a12 on a11.FNum = a12.FNum and a11.FCheckState = 1 and a12.FCheckState = 1 and a12.FTransferDate between "+dbl.sqlDate(tmpDate)+" and "+dbl.sqlDate(tmpDate)+"  and a11.FPortCode = "+dbl.sqlString(this.portCode)+
						" and a12.FSubTsfTypeCode = '02FI' )))) order by FOrder";
				rs = dbl.openResultSet(query);
				setResultValue(valueMap,rs);
			}
			insertToInterestIncomeData(valueMap);
		}catch(Exception e){
			throw new YssException();
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	
}
