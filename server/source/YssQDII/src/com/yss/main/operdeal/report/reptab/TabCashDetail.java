package com.yss.main.operdeal.report.reptab;

import com.yss.base.BaseAPOperValue;

import com.yss.main.dayfinish.OffAcctBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import java.util.HashMap;
import java.sql.*;
import java.util.Iterator;
import java.lang.Math;

;
/*
 * 计算货币分布表
 * */
public class TabCashDetail extends BaseAPOperValue {

	private java.util.Date dBeginDate;
	private java.util.Date dEndDate;
	private String portCode;
	private boolean isCreate;//是否生成报表 add by qiuxufeng 20101118 QDV4太平2010年09月16日03_A

	private class CashDetailBean {

		String tfDetail;//判断是否明细
		String CuryCode; //货币

		double fSACashEQ;//活期现金市值-股票
		double fSACashFI;//活期现金市值-债券
		double fSACashDE;//活期现金市值-货币
		double fSACashOP;//活期现金市值-窝轮
		double FSAcashFOF;//活期现金市值-FOF

		double fCashEQ;//定存现金市值-股票
		double fCashFI;//定存现金市值-债券
		double fCashDE;//定存现金市值-货币
		double fCashOP;//定存现金市值-窝轮
		double FcashFOF;//定存现金市值-FOF
		double FBal;//定存应收利息

		double fBrokerSecPay;//券商应收应付
		double invbalEQ;//应付管理费_股票
		double invbalFI; //应付管理费_债券
		double invbalFOF;//应付管理费_FOF
		double invbalWT;//应付管理费_窝轮
		double invbalDE;//应付管理费_货币市场

		double trubalEQ;//应付托管费_股票
		double trubalFI;//应付托管费_债券
		double trubalWT;//应付托管费_涡轮
		double trubalFOF;//应付托管费-FOF
		double trubalDE;//应付托管费-货币市场

		double fRecPayMoney;//未完成交易
		double fDivided;//应收股息
		double FEQMktValue;//股票市值
		double FFIMktValue;//债券市值
		double FFIInterest;//债券应收利息
		double FFOFMktValue;//基金市值
		double FOPMktValue;//窝轮市值

		double bxf;//超额表现费 //合并邵宏伟修改报表代码 xuqiji 20100608

		String portCode; //组合
		String Date;

		public void CashDetailBean() {
		}
	}

	public TabCashDetail() {
	}

	public void init(Object bean) throws YssException {
		String reqAry[] = null;
		String reqAry1[] = null;
		String sRowStr = (String) bean;
		if (sRowStr.trim().length() == 0) {
			return;
		}
//		reqAry = sRowStr.split("\n");
//		reqAry1 = reqAry[0].split("\r");
//		// this.dBeginDate = YssFun.toDate(reqAry1[1]);
//		//  reqAry1 = reqAry[1].split("\r");
//		this.dEndDate = YssFun.toDate(reqAry1[1]);
//		// reqAry1 = reqAry[2].split("\r");
//		reqAry1 = reqAry[1].split("\r");
//		this.portCode = reqAry1[1];
		
		//==================修改解析控件的值，前台控件值为空时不传值导致解析出错  edit by qiuxufeng 20101109 
		reqAry = sRowStr.split("\n");
		for (int i = 0; i < reqAry.length; i++) {
			reqAry1 = reqAry[i].split("\r");
			if(reqAry1[2].equalsIgnoreCase("DateTimePicker1")) {
				this.dBeginDate = YssFun.toDate(reqAry1[1]);
			}
			if(reqAry1[2].equalsIgnoreCase("DateTimePicker1")) {
				this.dEndDate = YssFun.toDate(reqAry1[1]);
			}
			if(reqAry1[2].equalsIgnoreCase("SelectControl1")) {
				this.portCode = reqAry1[1];
			}
			if(reqAry1[2].equalsIgnoreCase("ComboBox1")) {
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
		createTempCashDetail();
		valueMap = new HashMap();
		try {
	     	 //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
	         if(isCreate) {
	      	  	OffAcctBean offAcct = new OffAcctBean();
	     		offAcct.setYssPub(this.pub);
	      	  	String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
	      	  	String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
	      	  	if(!tmpInfo.trim().equalsIgnoreCase("")) {
	      	  		return "<OFFACCT>" + tmpInfo;
	      	  	}
	      	  	//=================end=================
				getCashDetailValue(valueMap);
	         }
		} catch (YssException ex) {
			throw new YssException(ex.getMessage());
		}
		//		      insertToTempUnrealised(valueMap);
		return "";
	}

	/**
	 * 创建用于存放未实现数据的表。
	 * //合并邵宏伟修改报表代码 xuqiji 20100608
	 * @throws YssException
	 */
	private void createTempCashDetail() throws YssException {
		String strSql = "";
		try {
			String a = pub.yssGetTableName("tb_temp_CashDetail");
			if (dbl.yssTableExist(pub.yssGetTableName("tb_temp_CashDetail"))) {
				return;
			} else {
				strSql = "create table "
						+ pub.yssGetTableName("tb_temp_CashDetail")
						+ " ( tfDetail varchar2(20) ,"
						+ " FCuryCode varchar2(20) ,"
						+ " fSACashEQ number(18,6),"
						+ " fSACashFI number(18,6),"
						+ " fSACashDE number(18,6),"
						+ " fSACashOP number(18,6),"
						+ " FSAcashFOF number(18,6),"

						+ " fCashEQ number(18,6),"
						+ " fCashFI number(18,6),"
						+ " fCashDE number(18,6),"
						+ " fCashOP number(18,6),"
						+ " FcashFOF number(18,6),"
						+ " FBal number(18,6),"

						+ " fBrokerSecPay number(18,6),"
						+ " invbalEQ number(18,6),"
						+ " invbalFI number(18,6),"
						+ " invbalFOF number(18,6),"
						+ " invbalWT number(18,6),"
						+ " invbalDE number(18,6),"

						+ " trubalEQ number(18,6),"
						+ " trubalFI number(18,6),"
						+ " trubalWT number(18,6),"
						+ " trubalFOF number(18,6),"
						+ " trubalDE number(18,6),"

						+ " bxf number(18,6),"
						+ " fRecPayMoney number(18,6),"
						+ " fDivided number(18,6),"
						+ " FEQMktValue number(18,6),"
						+ " FFIMktValue number(18,6),"
						+ " FFIInterest number(18,6),"
						+ " FOPMktValue number(18,6),"
						+ " FFOFMktValue number(18,6),"
						+ " FPortCode varchar2(20),"
						+ " FDate varchar2(30) )";
				dbl.executeSql(strSql);
			}
		} catch (Exception e) {
			throw new YssException("生成临时货币分布表出错!");
		}
	}

	private void deleteFromTempCashDetail() throws YssException {
		//合并邵宏伟修改报表代码 xuqiji 20100608
		String sqlStr = "Delete from  "	+ pub.yssGetTableName("tb_temp_CashDetail") + " where FPortCode = " + this.portCode;

		try {
			dbl.executeSql(sqlStr);

		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		}
	}

	/**
	 * 将未实现的数据插入数据库
	 * @param valueMap HashMap
	 * @throws YssException
	 */
	private void insertToTempCashDetail(HashMap valueMap) throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		CashDetailBean CashDetail = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				+ pub.yssGetTableName("tb_temp_CashDetail")
				+ "(tfDetail, FCuryCode,  fSACashEQ,    fSACashFI ,   fSACashDE, fSACashOP,  FSAcashFOF,    fCashEQ,    fCashFI,  fCashDE,  "
				+ "fCashOP  ,  FcashFOF,       FBal, fBrokerSecPay,    invbalEQ,  invbalFI,   invbalFOF,   invbalWT,   invbalDE, trubalEQ, "
				+ " trubalFI,  trubalWT,  trubalFOF,      trubalDE, fRecPayMoney, fDivided, FEQMktValue,FFIMktValue,FFIInterest,"
				+ " FOPMktValue,FFOFMktValue,FPortCode,FDate,bxf)" //合并邵宏伟修改报表代码 xuqiji 20100608
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; //合并邵宏伟修改报表代码 xuqiji 20100608
		try {

			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				CashDetail = (com.yss.main.operdeal.report.reptab.TabCashDetail.CashDetailBean) valueMap
						.get((String) it.next());

				prst.setString(1, CashDetail.tfDetail);
				prst.setString(2, CashDetail.CuryCode);
				prst.setDouble(3, CashDetail.fSACashEQ);
				prst.setDouble(4, CashDetail.fSACashFI);
				prst.setDouble(5, CashDetail.fSACashDE);
				prst.setDouble(6, CashDetail.fSACashOP);
				prst.setDouble(7, CashDetail.FSAcashFOF);

				prst.setDouble(8, CashDetail.fCashEQ);
				prst.setDouble(9, CashDetail.fCashFI);
				prst.setDouble(10, CashDetail.fCashDE);
				prst.setDouble(11, CashDetail.fCashOP);
				prst.setDouble(12, CashDetail.FcashFOF);
				prst.setDouble(13, CashDetail.FBal);

				prst.setDouble(14, CashDetail.fBrokerSecPay);
				prst.setDouble(15, CashDetail.invbalEQ);
				prst.setDouble(16, CashDetail.invbalFI);
				prst.setDouble(17, CashDetail.invbalFOF);
				prst.setDouble(18, CashDetail.invbalWT);
				prst.setDouble(19, CashDetail.invbalDE);

				prst.setDouble(20, CashDetail.trubalEQ);
				prst.setDouble(21, CashDetail.trubalFI);
				prst.setDouble(22, CashDetail.trubalWT);
				prst.setDouble(23, CashDetail.trubalFOF);
				prst.setDouble(24, CashDetail.trubalDE);

				prst.setDouble(25, CashDetail.fRecPayMoney);
				prst.setDouble(26, CashDetail.fDivided);
				prst.setDouble(27, CashDetail.FEQMktValue);
				prst.setDouble(28, CashDetail.FFIMktValue);
				prst.setDouble(29, CashDetail.FFIInterest);
				prst.setDouble(30, CashDetail.FOPMktValue);
				prst.setDouble(31, CashDetail.FFOFMktValue);
				prst.setString(32, CashDetail.portCode);
				prst.setString(33, CashDetail.Date);
				prst.setDouble(34, CashDetail.bxf);//合并邵宏伟修改报表代码 xuqiji 20100608

				prst.executeUpdate();
			}

		} catch (YssException ex) {
			throw new YssException("insert error", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		}
		//add by rujiangpeng 20100603打开多张报表系统需重新登录
		finally {
			dbl.closeStatementFinal(prst);
		}
	}

	/**
	 * 获取未实现的数据
	 * @param valueMap HashMap
	 * @throws YssException
	 * @throws SQLException
	 */
	private void getCashDetailValue(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		try {
			conn.setAutoCommit(false);
		} catch (SQLException ex) {
		}
		bTrans = true;
		deleteFromTempCashDetail(); //先删除已有的数据。
		try {
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (SQLException ex1) {
			throw new YssException(ex1.getMessage());
		}
		getCashDistributeDetail(valueMap);

		insertToTempCashDetail(valueMap);

		try {
			conn.commit();
			dbl.endTransFinal(conn, bTrans);

		} catch (SQLException ex2) {
			throw new YssException(ex2.getMessage());
		}
		valueMap.clear();
		getCashDistributetotal(valueMap);
		insertToTempCashDetail(valueMap);
		try {
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (SQLException ex1) {
			throw new YssException(ex1.getMessage());
		}
		valueMap.clear();

		getCashDistributeScale(valueMap);
		insertToTempCashDetail(valueMap);
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

	/**
	 * 获取货币分布明细信息。
	 * //合并邵宏伟修改报表代码 xuqiji 20100608
	 * @throws YssException
	 */

	private void getCashDistributeDetail(HashMap valueMap) throws YssException {
		String sqlStr = "select  "
				+ dbl.sqlString(this.portCode)
				+ " as  fportCode , "
				+ "0||a1.fcurycode as tfDetail ,"
				+ " a1.fcurycode as fcurycode, to_char("
				+ dbl.sqlDate(this.dEndDate)
				+ ",'yyyy-MM-dd') as fnavdate,  nvl(b.fSAaccbalanceEQ,0) as fSACashEQ,"
				+ " nvl(b.fSAaccbalanceFI,0) as fSACashFI, nvl(b.fSAaccbalanceDE,0) as fSACashDE, nvl(b.fSAaccbalanceOP,0) as fSACashOP,"
				+ " nvl(b.fSAaccbalanceFOF,0) as FSAcashFOF,  nvl(c.faccbalanceEQ,0) as fCashEQ, nvl(c.faccbalanceFI,0) as fCashFI,"
				+ " nvl(c.faccbalanceDE,0) as fCashDE,   nvl(c.faccbalanceOP,0) as fCashOP,   nvl(c.faccbalanceFOF,0) as FcashFOF,"
				+ " nvl(n.fbal,0) as fBrokerSecPay,  nvl(a.fRecPay,0) as fRecPayMoney, nvl(a.ffDivdend,0) as fDivided,nvl(m.FEQMktValue,0) as FEQMktValue,"
				+ " nvl(m.FFIMktValue,0) as FFIMktValue, nvl(a5.Fbal,0) as FFIInterest,nvl(m.FOPMktValue,0) as FOPMktValue,nvl(m.FFOFMktValue,0) as FFOFMktValue,"
				+ " nvl(cashpayrec.FBal,0) as FBal, nvl(investpayrec.invbalEQ*-1,0) as invbalEQ, nvl(investpayrec.invbalFI*-1,0) as invbalFI,"
				+ " nvl(investpayrec.invbalFOF*-1,0) as invbalFOF,nvl(investpayrec.invbalWT*-1,0) as invbalWT,  nvl(investpayrec.invbalDE*-1,0) as invbalDE,"
				+ " Nvl(trustee.trubalEQ*-1, 0) as trubalEQ,  Nvl(trustee.trubalFI*-1, 0) as trubalFI,  Nvl(trustee.trubalWT*-1, 0) as trubalWT,"
				+ " Nvl(trustee.trubalFOF*-1, 0) as trubalFOF,   Nvl(trustee.trubalDE*-1, 0) as trubalDE,nvl(bxf.fbal*-1,0) as bxf from "
				+
            /*
             未完成交易和应收股息
             */
				" (select * from   "
				+ pub.yssGetTableName("tb_para_currency")
				+ " ) a1 left join  "
				+ " (select fcurycode,sum(fRec) + sum(fPay) as fRecPay,sum(fDivdend) as ffDivdend  from "
				+ "(select b.fcurycode, 	ftradetypecode,"
				+ " SUM(decode(ftradetypecode, '01', (-1)*ftotalcost, 0)) as fPay,"
				+ "SUM(decode(ftradetypecode, '02', ftotalcost, 0)) as fRec,"
				+ " SUM(decode(ftradetypecode, '06', ftotalcost, 0)) as fDivdend,"
				+ " sum(a.ftotalcost) as ftotalcost from "
				+ "(select *  from     "
				+ pub.yssGetTableName("tb_data_subtrade")
				+ "    where fcheckstate = 1 and  fportcode = "
				+ dbl.sqlString(this.portCode)
				+ "  and "
				+ " fbargaindate <= "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and FFActSettleDate >"
				+ dbl.sqlDate(this.dEndDate)
				+ "  and "
				+ " ftradetypecode in ('01','02', '06')) a join (select * from  "
				+ pub.yssGetTableName("tb_para_cashaccount")
				+ "    ) b "
				+ "  on a.fcashacccode = b.fcashacccode 		 group by b.fcurycode,ftradetypecode) "
				+ "group by fcurycode) a on a1.fcurycode=a.fcurycode"
				+ " left join"

				/*活期*/
				+ " (select fcurycode,  SUM(decode(fanalysiscode2, 'EQ', faccbalance, 0)) as fSaaccbalanceEQ, "
				+ " SUM(decode(fanalysiscode2, 'FI', faccbalance, 0)) as fSaaccbalanceFI,"
				+ " SUM(decode(fanalysiscode2, 'WT', faccbalance, 0)) as fSaaccbalanceOP,"
				+ " (SUM(decode(fanalysiscode2, 'DR', faccbalance, 0)) + nvl(SUM(decode(fanalysiscode2, 'TR', faccbalance, 0)),0)) as fSaaccbalanceFOF,"
				+ " SUM(decode(fanalysiscode2, 'DE', faccbalance,'MK', faccbalance, 0)) as fSaaccbalanceDE FROM ("

				/*
				+ " select fcurycode, fanalysiscode2, sum(round(faccbalance*a.fbasecuryrate,2)) as faccbalance   from  "
				+ pub.yssGetTableName("tb_stock_cash")
				+ "  a  right join (select FcashAccCode,FAcctype From  "
				+ pub.yssGetTableName("Tb_Para_Cashaccount")
				+ "   where FAcctype <>'04'and Fsubacctype in('0101','0105'))b  "
				+ "  on a.fcashAccCode =b.FcashACcCode  	 where fstoragedate =  "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and "
				+ "  fportcode = "
				+ dbl.sqlString(this.portCode)
				+ "  and Fyearmonth <> to_char(  "
				+ dbl.sqlDate(this.dEndDate)
				+ ",'yyyy') || '00' and  "
				+ "  fanalysiscode2 in ('EQ', 'FI', 'WT','DR', 'DE','TR') "
				+ " group by fcurycode, fanalysiscode2  order by fcurycode" */

				+ " select fcurycode,substr(a.forder,11,2) as fanalysiscode2, sum(round(a.fmvalue,2)) as faccbalance"
				+ " from tb_data_PortfolioVal a "
				+ " where (FOrder like '01__0101%' or FOrder like '01__0105%')  and FOrder not like '%total'"
				+ " and a.FValDate = "
				+ dbl.sqlDate(this.dEndDate)
				+ " and a.FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " group by fcurycode,substr(a.forder,11,2)"

				+ " ) group by fcurycode) b on a1.fcurycode = b.fcurycode  "

				/*定期*/
				+ " left join  (select fcurycode, "
				+ " SUM(decode(fanalysiscode2, 'EQ', round(faccbalance,2), 0)) as faccbalanceEQ,"
				+ " SUM(decode(fanalysiscode2, 'FI', round(faccbalance,2), 0)) as faccbalanceFI,"
				+ " SUM(decode(fanalysiscode2, 'WT', round(faccbalance,2), 0)) as faccbalanceOP,"
				+ " (SUM(decode(fanalysiscode2, 'DR', round(faccbalance,2), 0))+ nvl(SUM(decode(fanalysiscode2, 'TR', round(faccbalance,2), 0)),0)) as faccbalanceFOF,"
				+ " SUM(decode(fanalysiscode2, 'DE', faccbalance,'MK', faccbalance, 0)) as faccbalanceDE"
				+ " FROM (select substr(a.forder,11,2) as fanalysiscode2,a.fcurycode, "
				+ " SUM(round(a.fmvalue,2)) as faccbalance"
				+ " from tb_data_portfolioval a  "
				+ " where a.FOrder like '01__0102%' and FOrder not like '%total' "
				+ " and a.FValDate ="
				+ dbl.sqlDate(this.dEndDate)
				+ " and a.FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ "  group by substr(a.forder,11,2),fcurycode) group by fcurycode) c on a1.fcurycode = c.fcurycode"

				/*债券应收*/
				/*
				+ " left join ("
				+ " select sum(round(fmarketvalue,2)) as fmarketvalue, sum(round(fportmarketvalue,2)) as fportmarketvalue, fcurycode"
				+ " from   "
				+ pub.yssGetTableName("tb_data_navdata")
				+ "    where fnavdate ="
				+ dbl.sqlDate(this.dEndDate)
				+ " and fportcode ="
				+ dbl.sqlString(this.portCode)
				+ "  "
				+ "  and fretypecode = 'Security' and fgradetype6 = '06FI' group by fcurycode) a5 on a1.fcurycode = a5.fcurycode"
				*/

				+ " left join ( select a.fcurycode, sum(round(FLXVBal, 2)+round(a.FBoughtInt, 2)) as Fbal from tb_data_portfolioval a "
				+ " where  a.FOrder like 'FI%'  and FOrder not like '%total'   "
				+ " and a.FValDate = " + dbl.sqlDate(this.dEndDate)
				+ " and a.FPortCode = "	+ dbl.sqlString(this.portCode)
				+ " group by fcurycode ) a5 on a1.fcurycode=a5.fcurycode "


				/*证券市值*/
				+ "  left join  (select  fcurycode,   SUM(decode(fgradetype1, 'EQ', fmarketvalue, 0)) as FEQMktValue, "
				+ "  SUM(decode(fgradetype1, 'FI', fmarketvalue, 0)) as FFIMktValue,SUM(decode(fgradetype1, 'WR', fmarketvalue, 0)) as FOPMktValue,SUM(decode(fgradetype1, 'TR', fmarketvalue, 0)) as FFOFMktValue "
				+ "  FROM (  SELECT   substr(forder,11,2)  as fgradetype1,  sum( round(FMvalue,2)) as fmarketvalue, cc.fcurycode "
				+ "  FROM tb_data_PortfolioVal cc  where  (cc.forder like 'FI%' or cc.forder like 'EQ%' or  cc.forder like 'TR%' or cc.forder like 'WR%') "
				+ "  and cc.forder not like '%total%' and cc.forder not like '%EQ98%'  and cc.fvaldate="  //不能包括红利
				+ dbl.sqlDate(this.dEndDate)
				+ " and cc.fportcode="
				+ dbl.sqlString(this.portCode)
				+ "   group by substr(forder,11,2) ,fcurycode ) "
				+ "  group by fcurycode) m on a1.fcurycode=m.fcurycode "

				/*q券商应收应付*/
				+ " left join  (select fcurycode,sum(fbal) as fbal ,sum(fportcurybal) as fportcurybal from (select r.fcurycode, r.fbal, round(r.fbal*q.fbaserate,2) as  fportcurybal from " + pub.yssGetTableName("tb_stock_invest")
				+ " r left join (select * from " + pub.yssGetTableName("tb_data_valrate") + " t where t.fportcode=" + dbl.sqlString(this.portCode)
				+ " and  t.fvaldate=" + dbl.sqlDate(this.dEndDate)
				+ " and t.fcheckstate=1 ) q on r.fcurycode=q.fcurycode and r.fportcode=q.fportcode "
				+ " where r.fstoragedate = " + dbl.sqlDate(this.dEndDate)
				+ " and r.fportcode = " + dbl.sqlString(this.portCode)
				+ " and  r.FIVPAYCATCODE = 'IV005' and r.Fyearmonth <> to_char(" + dbl.sqlDate(this.dEndDate) + ", 'yyyy') || '00' "
				+ " union all"
				+ " select r.fcurycode,r.fbal, round(r.fbal*q.fbaserate,2) as  fportcurybal from " + pub.yssGetTableName("tb_stock_cashpayrec")
				+ " r left join (select * from " + pub.yssGetTableName("tb_data_valrate") + " t where t.fportcode=" + dbl.sqlString(this.portCode)
				+ " and  t.fvaldate=" + dbl.sqlDate(this.dEndDate)
				+ " and t.fcheckstate=1 ) q on r.fcurycode=q.fcurycode and r.fportcode=q.fportcode "
				+ " where r.fstoragedate = " + dbl.sqlDate(this.dEndDate)
				+ " and ftsftypecode in ('06','07') "
	            + " and fsubtsftypecode not in ('06FI','06DE','06DV','06TD','07FI','07DE','07DV','07TD') "
				+ " and r.fportcode = " + dbl.sqlString(this.portCode)
				+ " and r.Fyearmonth <> to_char(" + dbl.sqlDate(this.dEndDate) + ", 'yyyy') || '00' "
				+ " ) group by fcurycode) n on n.fcurycode = a1.fcurycode  "

				/*超额表现费*/
				+ " left join  (select r.fcurycode,sum(r.fbal) as fbal, sum(round(r.fbal*q.fbaserate,2)) as  fportcurybal from " + pub.yssGetTableName("tb_stock_invest")
				+ " r left join (select * from " + pub.yssGetTableName("tb_data_valrate") + " t where t.fportcode=" + dbl.sqlString(this.portCode)
				+ " and  t.fvaldate=" + dbl.sqlDate(this.dEndDate)
				+ " and t.fcheckstate=1 ) q on r.fcurycode=q.fcurycode and r.fportcode=q.fportcode "
				+ " where r.fstoragedate = " + dbl.sqlDate(this.dEndDate)
				+ " and r.fportcode = " + dbl.sqlString(this.portCode)
				+ " and  r.FIVPAYCATCODE = 'IV007' and r.Fyearmonth <> to_char(" + dbl.sqlDate(this.dEndDate) + ", 'yyyy') || '00' "
				+ " group by r.fcurycode) bxf on bxf.fcurycode =a1.fcurycode  "

				/*定存应收利息*/
				+ " left join ( select   a.fcurycode,  sum(round(FLXVBal, 2))  as Fbal  from tb_data_portfolioval a "
				+ " where  a.FOrder like '01__0102%'  and FOrder not like '%total'   and a.FValDate = "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and  a.FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ "  group by fcurycode"
				+ " ) cashpayrec on a1.fcurycode=cashpayrec.fcurycode "


				/*当月管理费 取当月发生
				/*取应付管理费的当期余额*/
				+ " left join (select Fcurycode ,"
				+ " SUM(decode(fanalysiscode3, 'EQ', Fbal, 0)) as invbalEQ, "
				+ " SUM(decode(fanalysiscode3, 'FI', Fbal, 0)) as invbalFI, "
				+ " SUM(decode(fanalysiscode3, 'WT', Fbal, 0)) as invbalWT,"
				+ " (SUM(decode(fanalysiscode3, 'DR', Fbal, 0))+nvl(SUM(decode(fanalysiscode3, 'TR', Fbal, 0)),0)) as invbalFOF,"
				+ " SUM(decode(fanalysiscode3, 'DE', Fbal,'MK', Fbal, 0)) as invbalDE "
				+ "from " + pub.yssGetTableName("tb_stock_invest") + " where fstoragedate= "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and "
				+ " fportcode = "
				+ dbl.sqlString(this.portCode)
				+ " and Fivpaycatcode='IV001' "
				+ "group by Fcurycode ) investpayrec on a1.fcurycode=investpayrec.fcurycode "
				+

				"  left join (select Fcurycode,"
				+ "  SUM(decode(fanalysiscode3, 'EQ', Fbal, 0)) as trubalEQ,"
				+ " SUM(decode(fanalysiscode3, 'FI', Fbal, 0)) as trubalFI,"
				+ "SUM(decode(fanalysiscode3, 'WT', Fbal, 0)) as trubalWT,"
				+ " SUM(decode(fanalysiscode3, 'TR', Fbal, 0)) +  nvl(SUM(decode(fanalysiscode3, 'DR', Fbal, 0)),0) as trubalFOF,"
				+ " SUM(decode(fanalysiscode3, 'DE', Fbal,'MK', Fbal, 0)) as trubalDE "
				+ " from " + pub.yssGetTableName("tb_stock_invest") + " where fstoragedate = "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and fportcode ="
				+ dbl.sqlString(this.portCode)
				+ "  and Fivpaycatcode = 'IV002' group by Fcurycode) trustee on a1.fcurycode = trustee.Fcurycode "
				+

				" where nvl(b.fSAaccbalanceEQ,0) !=0 or nvl(b.fSAaccbalanceFI,0) !=0 or nvl(b.fSAaccbalanceDE,0) !=0 "
				+ "  or nvl(b.fSAaccbalanceOP,0) !=0 or nvl(b.fSAaccbalanceFOF,0) !=0 "
				+ "  or nvl(c.faccbalanceEQ,0) !=0 or nvl(c.faccbalanceFI,0) !=0 or nvl(c.faccbalanceDE,0) !=0 or "
				+ "  nvl(c.faccbalanceOP,0) !=0 or nvl(c.faccbalanceFOF,0) !=0 or nvl(n.fbal,0) !=0 "
				+ " or nvl(invbalEQ,0) !=0 or nvl(invbalFI,0) !=0 or nvl(invbalWT,0) !=0 or nvl(invbalFOF,0) !=0"
				+ " or nvl(invbalDE,0)!=0 or nvl(a.fRecPay,0)!=0 or nvl(a.ffDivdend,0)!=0 or "
				+ " nvl(m.FEQMktValue,0) !=0 or nvl(m.FFIMktValue,0) !=0 or nvl(a5.fbal,0) !=0 "
				+ " or nvl(m.FOPMktValue,0) !=0 or nvl(cashpayrec.FBal,0) !=0 ";

		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			setResultValue(valueMap, rs);
		} catch (YssException ex) {
			throw new YssException("获取货币分布数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/*
	 * 获取货币分布表的HKD汇总
	 * //合并邵宏伟修改报表代码 xuqiji 20100608
	 *
	 * */

	private void getCashDistributetotal(HashMap valueMap) throws YssException {

		String sqlStr = "select  "
				+ dbl.sqlString(this.portCode)
				+ " as  fportCode , "
				+ " '1HKD' as tfDetail , 'HKD' as fcurycode, 'HKD合计' as fnavdate  ,"
				+

				"  round(SUM(fSAaccbalanceEQ),2) as fSACashEQ, round(sum(fSAaccbalanceFI),2) as fSACashFI, "
				+ "  round(sum(fSAaccbalanceDE),2) as fSACashDE,  round(sum(fSAaccbalanceWT),2) as fSACashOP,"
				+ "  round(sum(fSAaccbalanceDR),2) as FSAcashFOF,  round(SUM(faccbalanceEQ),2) as fCashEQ,"
				+ "  round(sum(faccbalanceFI),2) as fCashFI, round(sum(faccbalanceDE),2) as fCashDE,"
				+ " round(sum(faccbalanceWT),2) as fCashOP, round(sum(faccbalanceDR),2) as FcashFOF,Round(SUM(cashpayrec.Fbasecurybal),2) as FBal,"
				+ " round(sum(n.fportcurybal),2) as fBrokerSecPay, round(SUM(a.fRecPay),2) as fRecPayMoney,"
				+ " round(sum(investpayrec.invbalEQ)*-1,2) as invbalEQ, round(sum(investpayrec.invbalFI)*-1,2) as invbalFI,"
				+ " round(sum(investpayrec.invbalFOF)*-1,2) as invbalFOF,  round(sum(investpayrec.invbalWT)*-1,2) as invbalWT,"
				+ "  round(sum(investpayrec.invbalDE)*-1,2) as invbalDE,round( SUM(trustee.trubalEQ)*-1,2) as trubalEQ,"
				+ "  round(SUM(trustee.trubalFI)*-1,2) as trubalFI, round(SUM(trustee.trubalWT)*-1,2) as trubalWT,"
				+ "  round(SUM(trustee.trubalFOF)*-1,2) as trubalFOF, round(SUM(trustee.trubalDE)*-1,2) as trubalDE, "
				+ " round(SUM(jj.ffDivdend),2) as fDivided, round(SUM(m.FEQMktValue) ,2) as FEQMktValue,"
				+ " round(SUM(m.FFIMktValue),2) as FFIMktValue, round(SUM(a5.lixi),2) as FFIInterest,"
				+ "  round(SUM(m.FOPMktValue),2) as FOPMktValue,round(SUM(m.FFOFMktValue),2) as FFOFMktValue,round(SUM(bxf.fportcurybal)*-1,2) as bxf "
				+

				"  from (select * from  "
				+ pub.yssGetTableName("tb_para_currency")
				+ " ) a1 "
				+ "  left join (select b.fcurycode, sum(round(fRecPay*c.fbaserate,2)) as fRecPay  from (select b.fcurycode, a.fportcode, ftradetypecode,"
				+ "  case when ftradetypecode=   '01' then (-1) * a.ftotalcost    else a.ftotalcost end  as fRecPay         "
				+ "   from (select *  from " + pub.yssGetTableName("tb_data_subtrade") + " where fportcode ="
				+ dbl.sqlString(this.portCode)
				+ " and fbargaindate <=    "
				+ dbl.sqlDate(this.dEndDate)
				+ "    and FFActSettleDate >  "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and ftradetypecode in ('01', '02')) a "
				+ "  join (select * from " + pub.yssGetTableName("tb_para_cashaccount") + ") b on a.fcashacccode = b.fcashacccode  ) b"
				+ "  left join (select * from " + pub.yssGetTableName("tb_data_valrate") + " v  where v.fvaldate =  "
				+ dbl.sqlDate(this.dEndDate)
				+ " and v.fportcode="
				+ dbl.sqlString(this.portCode)
				+ ") c on b.fcurycode =  c.fcurycode and b.fportcode=c.fportcode"
				+ " group by b.fcurycode) a on a1.fcurycode = a.fcurycode "
				+

				" left  join (select b.fcurycode, sum(round(ffactsettlemoney*c.fbaserate,2)) as ffDivdend "
				+ "  from (select b.fcurycode,a.fportcode, a.ffactsettlemoney  from (select *    from " + pub.yssGetTableName("tb_data_subtrade") + " where  fcheckstate=1 and fportcode ="
				+ dbl.sqlString(this.portCode)
				+ "   and fbargaindate <= "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and  FFActSettleDate > "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and ftradetypecode ='06') a   join (select * from " + pub.yssGetTableName("tb_para_cashaccount") + ") b on a.fcashacccode =  b.fcashacccode  ) b"
				+ "  left join (select * from " + pub.yssGetTableName("tb_data_valrate") + " v  where v.fvaldate = "
				+ dbl.sqlDate(this.dEndDate)
				+ " and v.fportcode="
				+ dbl.sqlString(this.portCode)
				+ ") "
				+ "  c on b.fcurycode = c.fcurycode and b.fportcode=c.fportcode group by b.fcurycode)  jj on jj.fcurycode=a1.fcurycode    "
				+

				/*活期*/
				" left join (select fcurycode, "
				+ " round(SUM(decode(fanalysiscode2,'EQ',fportcurybal , 0)), 2) as fSAaccbalanceEQ,"
				+ " round(SUM(decode(fanalysiscode2,'FI',fportcurybal,0)),2) as fSAaccbalanceFI,"
				+ " round(SUM(decode(fanalysiscode2,'WT',fportcurybal ,0)), 2) as fSAaccbalanceWT,"
				+ " round(SUM(decode(fanalysiscode2,'DR',fportcurybal,0)) + nvl(SUM(decode(fanalysiscode2,'TR', fportcurybal,0)),0),2) as fSAaccbalanceDR,"
				+ " round(SUM(decode(fanalysiscode2,'DE',fportcurybal,'MK',fportcurybal , 0)),2) as fSAaccbalanceDE"
				+ " FROM ( select sum(a.fmvalue) as fportcurybal ,"
				+ "fcurycode,substr(a.forder,11,2) as fanalysiscode2"
				+ "  from tb_data_PortfolioVal a    where (FOrder like '01__0101%total' or FOrder like '01__0105%total') "
				+ "  and a.FValDate = "
				+ dbl.sqlDate(this.dEndDate)
				+ " and a.FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " group by fcurycode,substr(a.forder,11,2))group by fcurycode) SA on a1.fcurycode = SA.fcurycode  "

				/*定存*/
				+ " left join (select fcurycode,"
				+ " round(SUM(decode(fanalysiscode2, 'EQ', fportcurybal,0)), 2) as faccbalanceEQ,"
				+ " round(SUM(decode(fanalysiscode2, 'FI', fportcurybal,0)),2) as faccbalanceFI,"
				+ " round(SUM(decode(fanalysiscode2,  'WT',fportcurybal ,0)), 2) as faccbalanceWT,"
				+ " round(SUM(decode(fanalysiscode2, 'DR',fportcurybal, 0)) + nvl( SUM(decode(fanalysiscode2,'TR',fportcurybal ,0)),0),2) as faccbalanceDR,"
				+ " round(SUM(decode(fanalysiscode2, 'DE', fportcurybal,'MK', fportcurybal , 0)), 2) as faccbalanceDE "
				+ " FROM (select substr(a.forder,11,2) as fanalysiscode2,a.fcurycode, "
				//+ " SUM(round(a.fmvalue,2)) as fportcurybal "
				+ " SUM(round(a.FTotalCost,2)) + SUM(round(a.FBoughtInt,2)) + SUM(round(a.FSyvBaseCuryBal,2)) as fportcurybal "
				+ " from tb_data_portfolioval a "
				+ " where a.FOrder like '01__0102%total'  and a.FValDate =" + dbl.sqlDate(this.dEndDate)
				+ " and a.FPortCode = " + dbl.sqlString(this.portCode)
				+ " group by substr(a.forder,11,2),fcurycode) group by fcurycode)c on a1.fcurycode = c.fcurycode "

				/*债券应收利息*/
				//------------------苏程辉 2009年10月30日  买入利息用交易日汇率，应收利息用估值日的汇率、、、取val表
				+ " left join (select a.FCuryCode, sum(round(a.FBoughtInt,2) + round(a.FLXVBal,2))  as Lixi from tb_data_portfolioval a "
				+ " where a.FOrder like 'FI__FI%total' "
				+ " and a.FValDate = "	+ dbl.sqlDate(this.dEndDate)
				+ " and a.FPortCode = " 	+ dbl.sqlString(this.portCode)
				+ " group by a.FCuryCode ) a5 on a1.fcurycode =  a5.fcurycode　"

				/*证券市值*/
				+ " left join (select fcurycode, SUM(decode(fgradetype1, 'EQ', fportmarketvalue, 0)) as FEQMktValue,"
				+ "  SUM(decode(fgradetype1, 'FI', fportmarketvalue, 0)) as FFIMktValue,SUM(decode(fgradetype1, 'WR', fportmarketvalue, 0)) as FOPMktValue,SUM(decode(fgradetype1, 'TR', fportmarketvalue, 0)) as FFOFMktValue  "
				+ " FROM ( "
				+ " SELECT SUM(FMarketValue) as fportmarketvalue, assent as fgradetype1,fcurycode FROM ("
				+ " select sum(round(a.FTotalCost,2) + round(a.FYKVBal,2) + round(a.FSyvBaseCuryBal,2)) as FMarketValue ,"
				//+ " select  sum(round(nvl(a.fmvalue,0), 2)) as FMarketValue ,"
				+ " substr(a.forder,1,2) as assent,a.fcurycode   from tb_data_portfolioval a  "
				+ " where (a.FOrder like 'FI__FI%total'  or FOrder like 'TR__TR%total%' or FOrder like 'EQ__EQ%total'   "
				//+ " OR FOrder like 'WR__WR%total' OR  FOrder like 'MM__MM%total' )  and FOrder not like 'EQ__EQ98%total'  "
				+ " OR FOrder like 'WR__WR%total' OR  FOrder like 'MM__MM%total' ) and forder not like '%EQ98%' "
				//+ " and a.FOrder not like 'FI__FI05%total'   and a.FValDate = "
				+ " and a.FValDate = " + dbl.sqlDate(this.dEndDate)
				+ "  and a.FPortCode = " + dbl.sqlString(this.portCode)
				+ "  group by  substr(a.forder,1,2) ,a.fcurycode    "
				/*
				+ " union   "
				+ " select NVL(sum(FMarketValue),2) as FMarketValue  ,'FI' AS assent, FCuryCode from ("
				+ " select  sum(NVL(a.FTotalCost,0))  + sum(NVL(a.FYKVBal,0)) + sum(NVL(a.FSyvBaseCuryBal,0)) as FMarketValue ,A.FCURYCODE "
				+ " from tb_data_portfolioval a   where a.FOrder like 'FI__FI05%total'  and a.FValDate = "
				+ dbl.sqlDate(this.dEndDate)
				+ " and a.FPortCode ="
				+ dbl.sqlString(this.portCode)
				+ " group by a.FCuryCode ) GROUP BY FCuryCode ) GROUP BY assent,fcurycode "
				*/
				+ " ) GROUP BY assent,fcurycode "
				+ " )group by fcurycode) m on a1.fcurycode = m.fcurycode "

				/*券商应收应付 --之前为直接取库存*/
				+ " left join  (select fcurycode , sum(fportcurybal) as fportcurybal from (select r.fcurycode, round(r.fbal*q.fbaserate,2) as  fportcurybal from " + pub.yssGetTableName("tb_stock_invest")
				+ " r left join (select * from " + pub.yssGetTableName("tb_data_valrate") + " t where t.fportcode=" + dbl.sqlString(this.portCode)
				+ " and  t.fvaldate=" + dbl.sqlDate(this.dEndDate)
				+ " and t.fcheckstate=1 ) q on r.fcurycode=q.fcurycode and r.fportcode=q.fportcode "
				+ " where r.fstoragedate = " + dbl.sqlDate(this.dEndDate)
				+ " and r.fportcode = " + dbl.sqlString(this.portCode)
				+ " and  r.FIVPAYCATCODE = 'IV005' and r.Fyearmonth <> to_char(" + dbl.sqlDate(this.dEndDate) + ", 'yyyy') || '00' "
				+ " union all"
				+ " select r.fcurycode, round(r.fbal*q.fbaserate,2) as  fportcurybal from " + pub.yssGetTableName("tb_stock_cashpayrec")
				+ " r left join (select * from " + pub.yssGetTableName("tb_data_valrate") + " t where t.fportcode=" + dbl.sqlString(this.portCode)
				+ " and  t.fvaldate=" + dbl.sqlDate(this.dEndDate)
				+ " and t.fcheckstate=1 ) q on r.fcurycode=q.fcurycode and r.fportcode=q.fportcode "
				+ " where r.fstoragedate = " + dbl.sqlDate(this.dEndDate)
				+ " and ftsftypecode in ('06','07') "
	            + " and fsubtsftypecode not in ('06FI','06DE','06DV','06TD','07FI','07DE','07DV','07TD') "
				+ " and r.fportcode = " + dbl.sqlString(this.portCode)
				+ " and r.Fyearmonth <> to_char(" + dbl.sqlDate(this.dEndDate) + ", 'yyyy') || '00' "
				+ " ) group by fcurycode) n on n.fcurycode =a1.fcurycode  "

				/*超额表现费*/
				+ " left join  (select r.fcurycode, sum(round(r.fbal*q.fbaserate,2)) as  fportcurybal from " + pub.yssGetTableName("tb_stock_invest")
				+ " r left join (select * from " + pub.yssGetTableName("tb_data_valrate") + " t where t.fportcode=" + dbl.sqlString(this.portCode)
				+ " and  t.fvaldate=" + dbl.sqlDate(this.dEndDate)
				+ " and t.fcheckstate=1 ) q on r.fcurycode=q.fcurycode and r.fportcode=q.fportcode "
				+ " where r.fstoragedate = " + dbl.sqlDate(this.dEndDate)
				+ " and r.fportcode = " + dbl.sqlString(this.portCode)
				+ " and  r.FIVPAYCATCODE = 'IV007' and r.Fyearmonth <> to_char(" + dbl.sqlDate(this.dEndDate) + ", 'yyyy') || '00' "
				+ " group by r.fcurycode) bxf on bxf.fcurycode =a1.fcurycode  "

				//定期存款利息
				+ " left join (select   a.fcurycode,  sum(round(FLXVBal, 2))   as Fbasecurybal  from tb_data_portfolioval a   where a.FOrder  like '01__0102%total%' "
				+ " and a.FValDate = "
				+ dbl.sqlDate(this.dEndDate)
				+ " and  a.FPortCode = "
				+ dbl.sqlString(this.portCode)
				+ " group by fcurycode) cashpayrec "
				+ " on a1.fcurycode = cashpayrec.fcurycode  "

				/*管理费*/
				//把没有带品种信息的管理费都加入FI里面
				+ " left join (select Fcurycode, SUM(decode(fanalysiscode3, 'EQ', Fbasecurybal, 0))  as invbalEQ,"
				+ "  SUM(decode(fanalysiscode3, 'FI', Fbasecurybal, 0)) as invbalFI,"
				+ "  SUM(decode(fanalysiscode3, 'WT', Fbasecurybal, 0)) as invbalWT,"
				+ " SUM(decode(fanalysiscode3, 'DR', Fbasecurybal, 0)) + SUM(decode(fanalysiscode3, 'TR', Fbasecurybal, 0)) as invbalFOF,"
				+ " SUM(decode(fanalysiscode3, 'DE', Fbasecurybal,'MK', Fbasecurybal, 0))  as invbalDE"
				+ "  from  "
				+ pub.yssGetTableName("tb_stock_invest")
				+ "  where "
				+ " fstoragedate = "
				+ dbl.sqlDate(this.dEndDate)
				+ " and fportcode = "
				+ dbl.sqlString(this.portCode)
				+ "  and"
				+ " Fivpaycatcode = 'IV001'  and fcheckstate=1  group by Fcurycode) investpayrec on a1.fcurycode = investpayrec.fcurycode "
				+ "  left join (select r.Fcurycode,"
				+ " SUM(decode(fanalysiscode3, 'EQ', Fbal*NVL(t.fbaserate, 1), 0)) as trubalEQ,"
				+ " SUM(decode(fanalysiscode3, 'FI', Fbal*NVL(t.fbaserate, 1), 0)) as trubalFI,"
				+ " SUM(decode(fanalysiscode3, 'WT', Fbal*NVL(t.fbaserate, 1), 0)) as trubalWT,"
				+ " SUM(decode(fanalysiscode3, 'DR', Fbal*NVL(t.fbaserate, 1), 0)) + nvl(SUM(decode(fanalysiscode3, 'TR', Fbal, 0)),0) as trubalFOF,"
				+ " SUM(decode(fanalysiscode3, 'DE', Fbal*NVL(t.fbaserate, 1),'MK', Fbal*NVL(t.fbaserate, 1), 0)) as trubalDE  from  ( select * from "
				+ pub.yssGetTableName("tb_stock_invest")
				+ "   where "
				+ " fstoragedate = "
				+ dbl.sqlDate(this.dEndDate)
				+ "  and fportcode = "
				+ dbl.sqlString(this.portCode)
				+ "  and "
				+ "  Fivpaycatcode = 'IV002' ) r left join (select * from " + pub.yssGetTableName("tb_data_valrate") + " where fcheckstate=1 and fportcode="
				+ dbl.sqlString(this.portCode)
				+ ") t on r.fcurycode = t.fcurycode and r.fportcode=t.fportcode "
				+ "  and r.fstoragedate = t.fvaldate  group by r.Fcurycode) trustee on a1.fcurycode =  trustee.Fcurycode  ";

		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			setResultValue(valueMap, rs);
		} catch (YssException ex) {
			throw new YssException("获取货币分布HKD汇总数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/*
	 * //合并邵宏伟修改报表代码 xuqiji 20100608
	 * 各个值的比例
	 * */
	private void getCashDistributeScale(HashMap valueMap) throws YssException {
		double total = 0;
		double ytotal = 0;
		String sqlStr1 = "select   (fSACashEQ + fSACashFI +fSACashDE  +fSACashOP  +FSAcashFOF  + fCashEQ +fCashFI "
				+ " + fCashDE + fCashOP +FcashFOF +FBal  +fBrokerSecPay +invbalEQ  +invbalFI  + invbalFOF  +invbalWT "
				+ " +invbalDE  +trubalEQ  + trubalFI +trubalWT +trubalFOF  +trubalDE +fRecPayMoney+fDivided +FEQMktValue "
				+ " + FFIMktValue + FFIInterest +FOPMktValue+ FFOFMktValue+bxf ) as total from  "
				+ pub.yssGetTableName("tb_temp_CashDetail")
				+ " where "
				+ "  tfDetail ='1HKD' and FPortcode = " + dbl.sqlString(this.portCode);
		ResultSet rs = null;
		try {
			rs = dbl.openResultSet(sqlStr1);
			while (rs.next()) {
				total = rs.getDouble("total");
			}
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		}finally {
			dbl.closeResultSetFinal(rs);//add by liuwei 中保打开多张报表需重新登录 20100604
		}
		ytotal = Math.round(total * 100) / 100;

		String sqlStr = "select  " + dbl.sqlString(this.portCode)
				+ " as  fportCode , " + "'2HKD' as tfDetail ,"
				+ "  '' AS fcurycode, ' '  as fnavdate, ROUND(fSACashEQ/"
				+ ytotal + " ,6)*100 AS fSACashEQ ," + "  ROUND(fSACashFI/"
				+ ytotal + ",6)*100 AS fSACashFI , ROUND(fSACashDE/" + ytotal
				+ ",6)*100 AS fSACashDE,ROUND(fSACashOP/" + ytotal
				+ ",6)*100 as  fSACashOP , " + "  ROUND(FSAcashFOF/" + ytotal
				+ ",6)*100 as FSAcashFOF,  ROUND( fCashEQ/" + ytotal
				+ ",6)*100 AS fCashEQ, ROUND(fCashFI/" + ytotal
				+ ",6)*100 as fCashFI ," + "  ROUND( fCashDE/" + ytotal
				+ ",6)*100 as fCashDE ,  ROUND(fCashOP/" + ytotal
				+ ",6)*100 as fCashOP,    ROUND(FcashFOF/" + ytotal
				+ ",6)*100  as FcashFOF," + "  ROUND(fBrokerSecPay/" + ytotal
				+ ",6)*100  as fBrokerSecPay,  ROUND(fRecPayMoney/" + ytotal
				+ ",6)*100  as fRecPayMoney, ROUND(fDivided/" + ytotal
				+ ",6)*100  as fDivided,ROUND(FEQMktValue/" + ytotal
				+ ",6)*100  as FEQMktValue," + "  ROUND(FFIMktValue/" + ytotal
				+ ",6)*100  as FFIMktValue, ROUND(FFIInterest/" + ytotal
				+ ",6)*100  as FFIInterest,ROUND(FOPMktValue/" + ytotal
				+ ",6)*100  as FOPMktValue,ROUND(FFOFMktValue/" + ytotal
				+ ",6)*100  as FFOFMktValue," + "  ROUND(FBal/" + ytotal
				+ ",6)*100   as FBal, ROUND(invbalEQ/" + ytotal
				+ ",6)*100 as invbalEQ, ROUND(invbalFI/" + ytotal
				+ ",6)*100 as invbalFI," + "  ROUND(invbalFOF/" + ytotal
				+ ",6)*100  as invbalFOF,ROUND(invbalWT/" + ytotal
				+ ",6)*100 as invbalWT, ROUND(invbalDE/" + ytotal
				+ ",6)*100 as invbalDE," + "  ROUND(trubalEQ/" + ytotal
				+ ",6)*100 as trubalEQ,  ROUND(trubalFI/" + ytotal
				+ ",6)*100 as trubalFI,  ROUND(trubalWT/" + ytotal
				+ ",6)*100  as trubalWT," + "  ROUND(trubalFOF/" + ytotal
				+ ",6)*100 as trubalFOF,   ROUND(bxf/" + ytotal
				+ ",6)*100 as bxf,   ROUND(trubalDE/" + ytotal
				+ ",6)*100   as trubalDE  FROM  "
				+ pub.yssGetTableName("tb_temp_CashDetail")
				+ "  where tfDetail='1HKD' and FPortcode = " + dbl.sqlString(this.portCode);
		rs = null;
		try {
			rs = dbl.openResultSet(sqlStr);
			setResultValue(valueMap, rs);
		} catch (YssException ex) {
			throw new YssException("获取货币分布HKD比例数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 将未实现的数据封装放入HashMap中。
	 * @param valueMap HashMap
	 * @param rs ResultSet
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
		CashDetailBean CashDistrubte = null;
		try {
			while (rs.next()) {
				CashDistrubte = new CashDetailBean();
				CashDistrubte.tfDetail = rs.getString("tfDetail");
				CashDistrubte.CuryCode = rs.getString("FCuryCode");
				CashDistrubte.fSACashEQ = rs.getDouble("fSACashEQ");
				CashDistrubte.fSACashFI = rs.getDouble("fSACashFI");
				CashDistrubte.fSACashDE = rs.getDouble("fSACashDE");
				CashDistrubte.fSACashOP = rs.getDouble("fSACashOP");
				CashDistrubte.FSAcashFOF = rs.getDouble("FSAcashFOF");

				CashDistrubte.fCashEQ = rs.getDouble("fCashEQ");
				CashDistrubte.fCashFI = rs.getDouble("fCashFI");
				CashDistrubte.fCashDE = rs.getDouble("fCashDE");
				CashDistrubte.fCashOP = rs.getDouble("fCashOP");
				CashDistrubte.FcashFOF = rs.getDouble("FcashFOF");
				CashDistrubte.FBal = rs.getDouble("FBal");

				CashDistrubte.fBrokerSecPay = rs.getDouble("fBrokerSecPay");
				CashDistrubte.invbalEQ = rs.getDouble("invbalEQ");
				CashDistrubte.invbalFI = rs.getDouble("invbalFI");
				CashDistrubte.invbalFOF = rs.getDouble("invbalFOF");
				CashDistrubte.invbalWT = rs.getDouble("invbalWT");
				CashDistrubte.invbalDE = rs.getDouble("invbalDE");

				CashDistrubte.trubalEQ = rs.getDouble("trubalEQ");
				CashDistrubte.trubalFI = rs.getDouble("trubalFI");
				CashDistrubte.trubalWT = rs.getDouble("trubalWT");
				CashDistrubte.trubalFOF = rs.getDouble("trubalFOF");
				CashDistrubte.trubalDE = rs.getDouble("trubalDE");
				CashDistrubte.fRecPayMoney = rs.getDouble("fRecPayMoney");
				CashDistrubte.fDivided = rs.getDouble("fDivided");
				CashDistrubte.FEQMktValue = rs.getDouble("FEQMktValue");
				CashDistrubte.FFIMktValue = rs.getDouble("FFIMktValue");
				CashDistrubte.FFIInterest = rs.getDouble("FFIInterest");
				CashDistrubte.FOPMktValue = rs.getDouble("FOPMktValue");
				CashDistrubte.FFOFMktValue = rs.getDouble("FFOFMktValue");
				CashDistrubte.Date = rs.getString("fnavdate");
				CashDistrubte.portCode = rs.getString("fportCode");
				CashDistrubte.bxf = rs.getDouble("bxf");//合并邵宏伟修改报表代码 xuqiji 20100608
				valueMap.put(CashDistrubte.tfDetail, CashDistrubte);
			}
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
}

