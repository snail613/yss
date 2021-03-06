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

public class TabDividend extends BaseAPOperValue {


	// ~ 前台传过来的参数
	private java.util.Date dBeginDate;
	private java.util.Date dEndDate;
	private String portCode;
	private String invmgrCode;
	private String brokercode;
	private boolean isCreate; // 是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询

	/**
	 * <p>
	 * Title:
	 * </p>
	 * 内部类
	 */
	private class DividendBean {
		// ~ 报表属性
		private String sSecurityCode = "";            //证券代码
		private String sPortCode = "";                //组合代码
		private java.sql.Date dDiViDendDate = null;   //除权日期
		private java.sql.Date dSettleDate = null;     //到帐日期
		private String sInvmgrCode = "";              //投资经理代码
		private String sCuryCode = "";                //货币代码
		private String sBrokerCode = "";              //券商代码
		
		private double dAccruedInterest = 0;          //原币应收利息
		private double dPortAccruedInterest = 0;      //组合货币应收利息
		
        public void DividendBean() {
		}

	}

	// =====================================================================================//
	public TabDividend() {

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
//		this.brokercode = reqAry1[1];
//		reqAry1 = reqAry[3].split("\r");
//		this.invmgrCode = reqAry1[1];
//		reqAry1 = reqAry[4].split("\r");
//		this.portCode = reqAry1[1];
//		reqAry1 = reqAry[5].split("\r");
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
				this.brokercode = reqAry1[1];
			} else if(reqAry1[0].equalsIgnoreCase("4")) {
				this.invmgrCode = reqAry1[1];
			} else if(reqAry1[0].equalsIgnoreCase("5")) {
				this.portCode = reqAry1[1];
			} else if(reqAry1[0].equalsIgnoreCase("6")) {
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

			if (!dbl.yssTableExist(pub.yssGetTableName("Tb_rep_divid"))) {
				throw new YssException("Divid表不存在！");
			}
			deleteDividData();
			dealDividData(valueMap);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (SQLException ex1) {
			throw new YssException(ex1.getMessage());
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	
	private void dealDividData(HashMap valueMap)throws YssException{
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		try{
		
			buf.append(" select subTrade.*,divide.*,Invmgr.Finvmgrname,broker.fbrokername,port.FPortName,security.FSecurityName, ");
			buf.append(" round(subTrade.FAccruedinterest * nvl(fexrate, 1), 2) as FPortAccruedInterest  from ");
			//---------------------------------------------------------------------------------------------------------------------//
			buf.append(" (select FSecurityCode,FPortCode,FBrokerCode,FInvmgrCode,FTradeTypeCode,FCashAccCode,FBargainDate, ");
			buf.append(" FFActSettleDate as FSettleDate,sum(FAccruedinterest) as FAccruedinterest,");
			buf.append(" round(sum(FAccruedinterest * FBaseCuryRate / FPortCuryRate),2) as FAccruedinterestB from "+pub.yssGetTableName("tb_data_subtrade"));
			//buf.append(" where FCheckState = 1 "+portCode==null?"":" and FPortCode ="+dbl.sqlString(this.portCode));
			buf.append(" where FCheckState = 1 " + (portCode==null?"":" and FPortCode ="+dbl.sqlString(this.portCode)));
			if(this.brokercode!=null){
				buf.append(" and FBrokerCode ="+dbl.sqlString(this.brokercode));
			}
			if(this.invmgrCode != null){
				buf.append(" and FInvMgrCode = "+dbl.sqlString(this.invmgrCode));
			}
			buf.append(" and FBargainDate <="+dbl.sqlDate(this.dEndDate)+"  and FTradeTypeCode = '06' and FFActsettledate >"+dbl.sqlDate(this.dEndDate));
			buf.append(" group by FSecurityCode,FPortCode,FBrokerCode,FInvmgrCode,FTradeTypeCode,FCashAccCode,FBargainDate,FFActSettleDate) subTrade");
			//---------------------------------------------------------------------------------------------------------------------//
			buf.append("  join ");
			buf.append(" (select distinct FSecurityCode as DFSecurityCode,FRecordDate,FCuryCode,FDividendDate,FDistributedate,FAffichedate from "+pub.yssGetTableName("Tb_Data_Dividend"));
			buf.append(" where FCheckState = 1) divide ");
			buf.append(" on subTrade.FSecurityCode =divide.DFSecurityCode and subTrade.Fbargaindate = divide.Fdividenddate ");
			//---------------------------------------------------------------------------------------------------------------------//
			buf.append(" left join ");
			buf.append(" (select FInvmgrCode, FInvmgrName from "+pub.yssGetTableName("Tb_Para_InvestManager")+" where FCheckState = 1) Invmgr ");
			buf.append(" on subTrade.Finvmgrcode =Invmgr.Finvmgrcode ");
			//---------------------------------------------------------------------------------------------------------------------//
			buf.append(" left join ");
			buf.append(" (select FBrokerCode, FBrokerName from "+pub.yssGetTableName("Tb_Para_Broker")+" where FCheckState = 1) broker ");
			buf.append("  on subTrade.Fbrokercode =broker.fbrokercode ");
			//---------------------------------------------------------------------------------------------------------------------//
			// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码

			buf.append(" left join ");
			buf.append(" (select FPortCode, FPortName from ");
			buf.append(pub.yssGetTableName("tb_para_portfolio"));
			buf.append("  where  FCheckState = 1 ) port ");
			buf.append(" on subTrade.FPortCode =port.FPortCode");
			
			//end by lidaolong
			//---------------------------------------------------------------------------------------------------------------------//
			buf.append(" left join ");
			buf.append(" (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from "+pub.yssGetTableName("Tb_Para_Security")+" o");
			buf.append(" join ");
			buf.append(" (select FSecurityCode, max(FStartDate) as FStartDate from  "+pub.yssGetTableName("Tb_Para_Security"));
			buf.append(" where FStartDate <= "+dbl.sqlDate(this.dEndDate)+" and FCheckState = 1 group by FSecurityCode) p ");
			buf.append(" on o.FSecurityCode =p.FSecurityCode and o.FStartDate =p.FStartDate) security ");
			buf.append(" on subTrade.FSecurityCode =security.FSecurityCode ");
			//---------------------------------------------------------------------------------------------------------------------//
			buf.append(" left join ");
			buf.append(" (select rate1.fcurycode, nvl(fexrate1, 1) as fexrate from "+pub.yssGetTableName("tb_data_exchangerate")+" rate1 ");
			buf.append(" right join ");
			buf.append(" (select max(fexratedate) fexratedate, fcurycode from "+pub.yssGetTableName("tb_data_exchangerate"));
			buf.append("  where fexratedate <= "+dbl.sqlDate(this.dEndDate)+" group by fcurycode) rate2 ");
			buf.append(" on rate1.fcurycode =rate2.fcurycode and rate1.fexratedate =rate2.fexratedate) q ");
			buf.append("  on divide.fcurycode = q.fcurycode");
			buf.append(" order by FBargainDate, FSecurityCode, FPortCode, FInvmgrCode");
			//---------------------------------------------------------------------------------------------------//
			rs = dbl.openResultSet(buf.toString());
			setResultValue(valueMap,rs);
			insertToDividData(valueMap);
		}catch(Exception e){
			throw new YssException("处理应收股息数据出错......"+e.getMessage());
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
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_rep_divid"))) {
				return;
			} else {
				strSql = "create table "
						+ pub.yssGetTableName("Tb_rep_divid")
						+ " (FSECURITYCODE  VARCHAR2(40),"
                        + " FPORTCODE  VARCHAR2(40),"
                        + " FCURYCODE  VARCHAR2(40),"
                        + " FINVMGRCODE  VARCHAR2(100),"
                        + " FBROKERCODE  VARCHAR2(400),"
                        + " FDIVIDENDDATE  DATE,"
                        + " FSETTLEDATE  DATE,"
                        + " FACCRUEDINTEREST  NUMBER(22,2),"
                        + " FPORTACCRUEDINTEREST  NUMBER(22,2))";

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

		DividendBean dividend = null;
		int count = 1;
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		if (null == rs) {
			return;
		}

		try {
			while (rs.next()) {
				dividend = new DividendBean();
				dividend.sSecurityCode = rs.getString("FSECURITYCODE");
				dividend.sBrokerCode = rs.getString("FBROKERCODE");
				dividend.sPortCode = rs.getString("FPORTCODE");
				dividend.sInvmgrCode = rs.getString("FINVMGRCODE");
				dividend.sCuryCode = rs.getString("FCURYCODE");
				
				dividend.dDiViDendDate = rs.getDate("FBARGAINDATE");
				dividend.dSettleDate = rs.getDate("FSETTLEDATE");
				
				dividend.dAccruedInterest = rs.getDouble("FACCRUEDINTEREST");
				dividend.dPortAccruedInterest = rs.getDouble("FPORTACCRUEDINTEREST");

				valueMap.put(count + "", dividend);
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
	private void insertToDividData(HashMap valueMap)
			throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		DividendBean dividend = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				+ pub.yssGetTableName("Tb_rep_divid")
				+ " (FSECURITYCODE,FINVMGRCODE,FCURYCODE,FPORTCODE,FBROKERCODE,"
				+ " FDIVIDENDDATE,FSETTLEDATE,FACCRUEDINTEREST,FPORTACCRUEDINTEREST)"
				+ " values(?,?,?,?,?,?,?,?,?)";
		try {
			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				dividend = (DividendBean)valueMap.get((String) it.next());
				
				prst.setString(1, dividend.sSecurityCode);
				prst.setString(2, dividend.sInvmgrCode);
				prst.setString(3, dividend.sCuryCode);
				prst.setString(4, dividend.sPortCode);
				prst.setString(5, dividend.sBrokerCode);
				
				prst.setDate(6, dividend.dDiViDendDate);
				prst.setDate(7, dividend.dSettleDate);
				
				prst.setDouble(8, YssFun.roundIt(dividend.dAccruedInterest, 2));
				prst.setDouble(9, YssFun.roundIt(dividend.dPortAccruedInterest,2));
				
				prst.executeUpdate();
			}
		} catch (Exception e) {
			throw new YssException("插入應收股息数据出错！" + e.getMessage());
		} finally {
			dbl.closeStatementFinal(prst);
		}
	}

	/**
	 * 从tb_rep_DaysSettlement表中按要求删除相关数据
	 * 
	 * @throws YssException
	 */
	private void deleteDividData() throws YssException {
		String sqlStr = "Delete from "
				+ pub.yssGetTableName("Tb_rep_divid")
//				+ " where FDIVIDENDDATE between "
//				+ dbl.sqlDate(this.dBeginDate)
//				+ " and "
//				+ dbl.sqlDate(this.dEndDate)
				//修改删除时的日期条件 edit by qiuxufeng 20101109 QDV4太平2010年09月16日02_A
				+ " where FDIVIDENDDATE <= "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and FSETTLEDATE > "
				+ dbl.sqlDate(this.dEndDate)
//				+ (this.portCode.equalsIgnoreCase("")?"":" and FPORTCODE ="+ dbl.sqlString(this.portCode))
//				+ (this.invmgrCode.equalsIgnoreCase("")?"":" and FINVMGRCODE ="+ dbl.sqlString(this.invmgrCode))
//				+ (this.brokercode.equalsIgnoreCase("")?"":" and FBROKERCODE ="+ dbl.sqlString(this.brokercode));
				//前台传值为空，获取无数据则为null，所以增加null的判断 edit by qiuxufeng 20101109 QDV4太平2010年09月16日02_A
				+ ((null == this.portCode || "" == this.portCode)?"":" and FPORTCODE ="+ dbl.sqlString(this.portCode))
				+ ((null == this.invmgrCode || "" == this.invmgrCode)?"":" and FINVMGRCODE ="+ dbl.sqlString(this.invmgrCode))
				+ ((null == this.brokercode || "" == this.brokercode)?"":" and FBROKERCODE ="+ dbl.sqlString(this.brokercode));
		try {
			dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("删除应收股息数据出错！" + e.getMessage());
		}

	}

}
