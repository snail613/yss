package com.yss.main.operdeal.report.reptab;

import com.yss.base.BaseAPOperValue;

import com.yss.main.dayfinish.OffAcctBean;
import com.yss.main.operdeal.report.reptab.valrep.BondValRep;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import java.util.HashMap;
import java.sql.*;
import java.util.Iterator;
import com.yss.util.YssD;
import java.lang.Math;

/*
 *
 * */
public class TabBankCurrency extends BaseAPOperValue {

	private java.util.Date dBeginDate;
	private java.util.Date dEndDate;
	private String portCode;
	private String cashcode;
	private String cashname;
	private static final int BankCurrency_Tilte = 1; // 标题
	private static final int BankCurrency_Detail = 2; // 明细
	private static final int BankCurrency_Total = 3; // 汇总

	private static final int BankCurrency_Frist = 11; // 第一部分
	private static final int BankCurrency_Second = 12; // 第二部分
	private static final int BankCurrency_Third = 13; // 第三部分

	
	// --- MS01748 QDV4太平2010年09月16日02_A add by jiangshichao 2010.10.13 -------
	private boolean isCreate; //是直接生成报表还是只是查询。 yes -- 生成、 no -- 查询
	// --- MS01748 QDV4太平2010年09月16日02_A end ----------------------------------
	
	private class BankCurrencyBean {
		String Fname;
		String jbal;
		String dbal;
		String transCode;
		String fdesc;
		String jdbal;
		String jybal;
		String forder;

		public void BankCurrencyBean() {
		}
	}

	public TabBankCurrency() {
	}

	public void init(Object bean) throws YssException {
		String reqAry[] = null;
		String reqAry1[] = null;

		String sRowStr = (String) bean;
		if (sRowStr.trim().length() == 0) {
			return;
		}
		reqAry = sRowStr.split("\n");
		for (int i = 0; i < reqAry.length; i++) {
			reqAry1 = reqAry[i].split("\r");
			if (reqAry1[2].equals("SelectControl1")) {
				this.portCode = reqAry1[1];
			}
			if (reqAry1[2].equals("DateTimePicker1")) {

				this.dBeginDate = YssFun.toDate(reqAry1[1]);
			}
			if (reqAry1[2].equals("DateTimePicker2")) {
				this.dEndDate = YssFun.toDate(reqAry1[1]);

			}
			if (reqAry1[2].equals("SelectControl2")) {
				this.cashname = reqAry1[1];

			}
			if (reqAry1[2].equals("SelectControl3")) {
				this.cashcode = reqAry1[1];

			}

			// --- MS01748 QDV4太平2010年09月16日02_A add by jiangshichao 2010.10.13 -------
			if(reqAry1[2].equals("ComboBox1")){
				this.isCreate = reqAry1[1].equalsIgnoreCase("0")?false:true;
			}
			// --- MS01748 QDV4太平2010年09月16日02_A end ----------------------------------
		}

	}

	public Object invokeOperMothed() throws YssException {
		// HashMap valueMap = null;
		createTempBankCurrency();
		// valueMap = new HashMap();
		try {
			// --- MS01748 QDV4太平2010年09月16日02_A add by jiangshichao 2010.10.13 -------
			if(isCreate){
	    		 //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
				 OffAcctBean offAcct = new OffAcctBean();
				 offAcct.setYssPub(this.pub);
				 String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
				 String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
				 if(!tmpInfo.trim().equalsIgnoreCase("")) {
					 return "<OFFACCT>" + tmpInfo;
				 }
				 //=================end=================
				getBankCurrencyValue();
			}
			// --- MS01748 QDV4太平2010年09月16日02_A end ----------------------------------
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
	private void createTempBankCurrency() throws YssException {
		String strSql = "";
		try {
			if (dbl.yssTableExist(pub.yssGetTableName("tb_rep_BankCurrency"))) {
				return;
			} else {
				strSql = "create table "
						+ pub.yssGetTableName("tb_rep_BankCurrency")
						+ " (Fname varchar2(50) ," + " fdesc varchar2(1000) ,"
						+ " jbal varchar2(50) ," + " dbal varchar2(50),"
						+ " jdbal varchar2(50)," + " jybal varchar2(50),"
						+ " transCode varchar2(50)," + " forder varchar2(100),"
				// --- MS01748 QDV4太平2010年09月16日02_A add by jiangshichao 2010.10.13 -------
				        + " FPortCode varchar2(20)," 
				        + " FDate Date not null)" ;
				// --- MS01748 QDV4太平2010年09月16日02_A end ----------------------------------
				dbl.executeSql(strSql);
			}
		} catch (Exception e) {
			throw new YssException("生成临时BankCurrency表出错!");
		}
	}

	private void deleteFromTempBankCurrency() throws YssException {
		String sqlStr = "Delete from  "
				+ pub.yssGetTableName("tb_rep_BankCurrency")+
				// --- MS01748 QDV4太平2010年09月16日02_A add by jiangshichao 2010.10.13 -------
				 " where FDate = " + dbl.sqlDate(this.dEndDate) + " and Fportcode =" + dbl.sqlString(portCode);
		        // --- MS01748 QDV4太平2010年09月16日02_A end ----------------------------------
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
	private void insertToTempBankCurrency(HashMap valueMap) throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		// valueMap.keySet()
		BankCurrencyBean BankCurrency = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				//+ pub.yssGetTableName("tb_temp_BankCurrency")
				//+ "(Fname,fdesc,jbal,dbal,jdbal,jybal,transCode,forder)"
		     	+ pub.yssGetTableName("tb_rep_BankCurrency")
			    + "(Fname,fdesc,jbal,dbal,jdbal,jybal,transCode,forder,fdate,fportcode)"
				+ " values(?,?,?,?,?,?,?,?,?,?)";
		try {

			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				BankCurrency = (com.yss.main.operdeal.report.reptab.TabBankCurrency.BankCurrencyBean) valueMap
						.get((String) it.next());
				prst.setString(1, BankCurrency.Fname);
				prst.setString(2, BankCurrency.fdesc);
				prst.setString(3, BankCurrency.jbal);
				prst.setString(4, BankCurrency.dbal);
				prst.setString(5, BankCurrency.jdbal);
				prst.setString(6, BankCurrency.jybal);
				prst.setString(7, BankCurrency.transCode);
				prst.setString(8, BankCurrency.forder);
				prst.setDate(9, YssFun.toSqlDate(this.dEndDate));
				prst.setString(10, this.portCode);
				prst.executeUpdate();
			}

		} catch (YssException ex) {
			throw new YssException("insert error", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		}
		// add by rujiangpeng 20100603打开多张报表系统需重新登录
		finally {
			dbl.closeStatementFinal(prst);
		}
	}

	/**
	 * 获取未实现的数据
	 * 
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 * @throws SQLException
	 */
	private void getBankCurrencyValue() throws YssException {

		deleteFromTempBankCurrency(); // 先删除已有的数据。
		getBanCurrencyDetail();

	}

	/**
	 * 获取现金账号明细
	 * 
	 * @throws YssException
	 */

	private ResultSet getCashAccDetail() throws YssException {
		ResultSet rs = null;
		String sqlStr = "";

		if (cashcode == null) {
			if (cashname == null) {
				/*sqlStr = " select * from (select fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")+" dd where dd.fportcode ="//按标准方式调用表，合并太平版本调整
						+ dbl.sqlString(this.portCode)
						+ " and  dd.fcheckstate=1  and dd.facctype='01' ) a "
						+ "  where (   exists (select * from "+pub.yssGetTableName("tb_stock_cash")+" c where c.fportcode="//按标准方式调用表，合并太平版本调整
						+ dbl.sqlString(this.portCode)
						+ "  and c.fcheckstate=1  "
						+ "  and c.fstoragedate="
						+ dbl.sqlDate(dBeginDate)
						+ "-1  and  "
						+ " a.fcashacccode=c.fcashacccode  and c.faccbalance<>0 )   or "
						+

						" exists ( select * from "+pub.yssGetTableName("tb_cash_subtransfer")+" s where s.fcheckstate=1 and s.fportcode="//按标准方式调用表，合并太平版本调整
						+ dbl.sqlString(this.portCode)
						+ " and s.fcashacccode=a.fcashacccode)    )";*/
				//对上面SQL语句的优化，合并太平版本调整 by leeyu 20100824
				sqlStr = "select *  from (select fcashacccode  from "
						+ pub.yssGetTableName("tb_para_cashaccount")
						+ " dd where dd.fportcode = "
						+ dbl.sqlString(this.portCode)
						+ " and dd.fcheckstate = 1 and dd.facctype = '01') a "
						+ " where exists (select '1' from (select FCashAccCode from "
						+ pub.yssGetTableName("tb_stock_cash")
						+ " c where c.fportcode = "
						+ dbl.sqlString(this.portCode)
						+ " and c.fcheckstate = 1 and c.fstoragedate = "
						+ dbl.sqlDate(dBeginDate)
						+ " - 1 and c.faccbalance <> 0 union all "
						+ " select FCashAccCode from "
						+ pub.yssGetTableName("tb_cash_subtransfer")
						+ " s where s.fcheckstate = 1 and s.fportcode = "
						+ dbl.sqlString(this.portCode)
						+ " and to_date(substr(fnum, 2, 8), 'yyyy-MM-dd') between " + dbl.sqlDate(dBeginDate) + " and " + dbl.sqlDate(dEndDate)//add by qiuxufeng 20101122 与原报表比对数据，修改查询语句以与原报表数据相同
						+ " ) b where a.FCashAccCode=b.FCashAccCode)";
				//合并太平版本调整 by leeyu 20100824

			} else {
				sqlStr = "select pc.fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")+" pc where ("+sqlCashName(cashname)//按标准方式调用表，合并太平版本调整
						+ ") and pc.fportcode="
						+ dbl.sqlString(this.portCode)
						+ " and pc.fcheckstate=1";
			}

		} else {
			if (cashname == null) {
				sqlStr = "select fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")+" pc where ("//按标准方式调用表，合并太平版本调整
						+ sqlCashCode(cashcode) + ") and  pc.fportcode="
						+ dbl.sqlString(this.portCode)
						+ " and pc.fcheckstate=1";
			} else {//------------modify bu guojianhua  2010 09 30 MS01746    01银行账户明细表，需要两个可选控件    QDV4太平2010年09月16日01_A    
				sqlStr = "select "
						//+ dbl.sqlString(cashcode).replaceAll(",", "','")
						+ "FCASHACCCODE from "+pub.yssGetTableName("tb_para_cashaccount")+" pc where ("+sqlCashName(cashname)
						+ ")  and ("+sqlCashCode(cashcode)
						+ ") and pc.fportcode=" + dbl.sqlString(this.portCode)
						+ " and pc.fcheckstate=1 ";
			}
			//---------------------end-----------
		}

		try {
			rs = dbl.openResultSet(sqlStr);

		} catch (YssException ex) {
			throw new YssException("现金账户数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		}

		return rs;

	}
	
	//edit by yanghaiming 20101022 不应该用or来连接多账户，直接用in取代
	//--------------add by guojianhua 2010 09 30  MS01746    01银行账户明细表，需要两个可选控件    QDV4太平2010年09月16日01_A    
    private String sqlCashName(String str){
    	String reqAry[] = null;
    	StringBuffer buf = new StringBuffer();
        reqAry = str.split(",");
        for(int i=0;i<reqAry.length;i++){
        	if(i == 0){
        		buf.append("pc.fbankaccount in ('"+reqAry[i]+"'");
        	}else{
        		buf.append("'"+reqAry[i]+"'");
        	}
        	if(i!=reqAry.length-1){
        		buf.append(",");
        	}else{
        		buf.append(")");
        	}
        }
        return buf.toString();
    }
    private String sqlCashCode(String str){
    	String reqAry[] = null;
    	StringBuffer buf = new StringBuffer();
        reqAry = str.split(",");
        for(int i=0;i<reqAry.length;i++){
        	if(i == 0){
        		buf.append("pc.FCASHACCCODE in ('"+reqAry[i]+"'");
        	}else{
        		buf.append("'"+reqAry[i]+"'");
        	}
        	if(i!=reqAry.length-1){
        		buf.append(",");
        	}else{
        		buf.append(")");
        	}
        }
        return buf.toString();
    }
	//---------------------------end--------------------
	/**
	 * 获取全部
	 * 
	 * @throws YssException
	 */
	private void getBanCurrencyDetail() throws YssException {
		ResultSet rsacc = null;
		rsacc = getCashAccDetail();
		  
	
		  
		HashMap valueMaps = null;
		valueMaps = new HashMap();
		Connection conn = dbl.loadConnection();
		ResultSet rs1 = null;
		try {
			// Statement stmt =
			// conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			while (rsacc.next()) {
				StringBuffer buf = new StringBuffer();
				String accountCode = rsacc.getString("fcashacccode");
				 buf.append( " select '  ' as A,'' AS B,'' AS C,'' AS D,'' AS E,'' AS F,'' AS Y,");
				 buf.append(" to_char(");
				 buf.append( dbl.sqlDate(dEndDate));
				 buf.append( ",'yyyyMMdd')||'##");
				 buf.append( accountCode);
				 buf.append( "##'||");
				 buf.append( BankCurrency_Frist);
				 buf.append( "||'##'||'0' AS G from dual ");
				 buf.append( " union all");
				 buf.append( " select * from ( ");
				 buf.append( "select '    项 目 代 码    ' AS A ,'    项 目 名 称     ' as B,'           ' AS C ,'              ' AS D, '               ' AS  E, '               ' AS F ,'                 ' as Y,");
				 buf.append( "to_char(");
				 buf.append( dbl.sqlDate(dEndDate));
				 buf.append( ",'yyyymmdd')||'##");
				 buf.append( accountCode);
				 buf.append( "##'||");
				 buf.append( BankCurrency_Frist);
				 buf.append( "||'##'||");
				 buf.append( BankCurrency_Tilte);
				 buf.append( " as G FROM DUAL ");
				 buf.append(" UNION ALL ");
				 buf.append( " select '");
				 buf.append(accountCode);
				 buf.append( "' as A,c.fcashaccname AS B,'' AS C ,'' AS D, '' AS  E, '' AS F,'' AS Y,");
				 buf.append( " to_char(");
				 buf.append( dbl.sqlDate(dEndDate));
				 buf.append( ",'yyyymmdd')||'##");
				 buf.append( accountCode);
				buf.append( "##'||");
					 buf.append( BankCurrency_Frist);
				 buf.append( "||'##'||");
				 buf.append( BankCurrency_Detail);
			 buf.append("  as G from dual ");
				 buf.append("  left join " + pub.yssGetTableName("tb_para_cashaccount") + " c ");
					 buf.append(" on '");
					 buf.append( accountCode);
					 buf.append( "'=c.fcashacccode   where c.fportcode=");
				buf.append( dbl.sqlString(this.portCode));
					 buf.append( " and c.fcheckstate=1  ");
					buf.append(" union all ");
			 //合并邵宏伟修改报表代码 xuqiji 20100608
			 buf.append( " SELECT '    调 拨 日 期    ' as A,'描    述' as B,'借    方' AS C ,'贷    方' AS D, '结    余' AS  E, '    调 拨 编 号    ' AS F,'' AS Y,");
					 buf.append( " to_char(");
			 buf.append(dbl.sqlDate(dEndDate));	
					buf.append(",'yyyymmdd')||'##");
							 buf.append( accountCode);
				 buf.append( "##'||");
						 buf.append( BankCurrency_Second);
								 buf.append( "||'##'||");
				 buf.append( BankCurrency_Tilte);
			 buf.append( "  as G  FROM DUAL ");
			 buf.append(" union all ");
			 buf.append(" select to_char(");
			 buf.append( dbl.sqlDate(dBeginDate));
			 buf.append(",'yyyy-MM-dd') as A,'承 上 结 余' AS B,'' AS C,'' AS D,to_char(round(nvl(faccbalance,0),2)) AS E  ,'' AS F,'' AS Y,");
			 buf.append( "  to_char(");
			 buf.append(dbl.sqlDate(dEndDate));
			 buf.append( ",'yyyymmdd')||'##");
			 buf.append( accountCode);
			 buf.append("##'||");
			 buf.append( BankCurrency_Second);
			 buf.append( "||'##'||");
			 buf.append( BankCurrency_Tilte);
			buf.append( "||");
			 buf.append( BankCurrency_Total);
			buf.append(" as G FROM  ( ");
					

			 buf.append(	" select  sum(sc.faccbalance)as faccbalance FROM " + pub.yssGetTableName("TB_STOCK_CASH") + " sc WHERE sc.fcheckstate=1 and sc.fportcode=");
			buf.append(dbl.sqlString(this.portCode));
			 buf.append( " and sc.fcashacccode='");
		 buf.append( accountCode);
		 buf.append( "'   and  sc.fstoragedate= case when to_char(");
			buf.append( dbl.sqlDate(dBeginDate));
		 buf.append( ",'MM')='01' then  sc.fstoragedate ");
		buf.append( "  else ");
	  buf.append( dbl.sqlDate(dBeginDate));
		buf.append( "-1   end ");
		buf.append( "  and  sc.fyearmonth=case when  to_char(");
		buf.append( dbl.sqlDate(dBeginDate));
		buf.append( ",'MM')='01' then to_char(");
		buf.append( dbl.sqlDate(dBeginDate));
		buf.append( ",'yyyy')||'00'   ");
		buf.append("  else sc.fyearmonth end )");
		 buf.append( " union all");
		 buf.append( " select '明细信息'as A,'' AS B,'' AS C,'' AS D,'' AS E  ,'' AS F,'' AS Y,");
		 buf.append( " to_char(");
		buf.append( dbl.sqlDate(dEndDate));
	  buf.append( ",'yyyymmdd')||'##");
	 buf.append(accountCode);
	buf.append( "##'||");
	buf.append( BankCurrency_Second);
		buf.append( "||'##'||");
			buf.append( BankCurrency_Tilte);
		buf.append( "||'##' as G FROM  DUAL ");
		buf.append( " union all ");
		//合并邵宏伟修改报表代码 xuqiji 20100608
		buf.append(" select to_char(ftransferdate,'yyyy-MM-dd') as A ,B,to_char(round(infmoney,2)) as C,TO_CHAR(round(outfmoney,2)) as D,to_char(round(infmoney,2)-round(outfmoney,2)) as E,FNUM AS F,'' AS Y,");
		buf.append( " to_char(");
	 buf.append( dbl.sqlDate(dEndDate));
	buf.append( ",'yyyymmdd')||'##");
	buf.append( accountCode);
   buf.append( "##'||");
		buf.append(BankCurrency_Second);
		 buf.append( "||'##'||");
				 buf.append(BankCurrency_Detail);
		buf.append( "||'##'||fnum||'##'||rownum as G FROM  ( ");
		buf.append( "  select af.fnum, cf.ftransferdate ,   tf.ftsftypename || sf.fsubtsftypename||' .'|| NVL(af.fdesc,'')   as B,");
	 buf.append( " sum(decode(af.finout, 1, af.fmoney, 0)) as infmoney, ");
	buf.append( "  sum(decode(af.finout, -1, af.fmoney, 0)) as outfmoney   ");
	 buf.append( "  from " + pub.yssGetTableName("tb_cash_transfer") + " cf ");
			 buf.append( "  join " + pub.yssGetTableName("tb_cash_subtransfer") + " af on cf.fnum = af.fnum ");
		 buf.append( "  left join Tb_Base_TransferType tf on tf.FTsfTypeCode=cf.ftsftypecode ");
		 buf.append( "  left join Tb_Base_SubTransferType sf on sf.fsubtsftypecode=cf.fsubtsftypecode ");
				 buf.append(" where cf.fcheckstate = 1 ");
			 buf.append( "   and af.fcheckstate = 1 ");
					 buf.append( "   and af.fportcode = ");
			 buf.append( dbl.sqlString(this.portCode));
			 buf.append( "   and cf.ftransferdate between ");
					 buf.append(dbl.sqlDate(dBeginDate));
							 buf.append(" and  ");
									 buf.append( dbl.sqlDate(this.dEndDate));
			 buf.append( "   and af.fcashacccode ='");
					 buf.append( accountCode);
							 buf.append("'");
									 buf.append( "  group by af.fnum, ftransferdate, af.fdesc, ftsftypename, fsubtsftypename ) ");//合并邵宏伟修改报表代码 xuqiji 20100608
											 buf.append(" UNION ALL ");
						

				 buf.append(" ( select '' as A,'' AS b,to_char(round(infmoney,2)) as  C,to_char(round(outfmoney,2)) as D,'' AS E, '' AS F,'' AS Y,");
					 buf.append("to_char(");
							 buf.append( dbl.sqlDate(dEndDate));
									 buf.append( ",'yyyymmdd')||'##");
				 buf.append(accountCode);
				 buf.append( "##'||");
						 buf.append( BankCurrency_Second);
								 buf.append( "||'##'||");
										 buf.append( BankCurrency_Total);
			 buf.append( "||'##' as G from ( ");
					
			buf.append("  select  SUM(infmoney) as infmoney,SUM(outfmoney) as outfmoney FROM  ( ");
					 buf.append(" select   sum(decode(af.finout, 1, af.fmoney, 0)) as infmoney, sum(decode(af.finout, -1, af.fmoney, 0)) as outfmoney   ");
							 buf.append("   from " + pub.yssGetTableName("tb_cash_transfer") + " cf ");
									 buf.append( "   join " + pub.yssGetTableName("tb_cash_subtransfer") + " af on cf.fnum = af.fnum ");
					 buf.append( "  left join Tb_Base_TransferType tf on tf.FTsfTypeCode=cf.ftsftypecode ");
							 buf.append( "  left join Tb_Base_SubTransferType sf on sf.fsubtsftypecode=cf.fsubtsftypecode ");
									 buf.append( " where cf.fcheckstate = 1 ");
											 buf.append( "   and af.fcheckstate = 1 ");
		 buf.append( "   and af.fportcode = ");
				 buf.append( dbl.sqlString(this.portCode));
		 buf.append( "  and cf.ftransferdate between ");
		 buf.append( dbl.sqlDate(dBeginDate));
		buf.append("  and  ");
				 buf.append( dbl.sqlDate(this.dEndDate));
						 buf.append(" and af.fcashacccode ='");
								 buf.append( accountCode);
		 buf.append("'");
				 buf.append( " group by af.fnum, ftransferdate, af.fdesc, ftsftypename, fsubtsftypename ");//合并邵宏伟修改报表代码 xuqiji 20100608


			 buf.append(" )  ) )");
					 buf.append(" union all ");
							 buf.append( " select '汇总信息' as A,'' AS B,'' AS C,'' AS D,'' AS E,'' AS F,'' AS Y,");
			 buf.append("to_char(");
			 buf.append( dbl.sqlDate(dEndDate));
					 buf.append( ",'yyyymmdd')||'##");
							 buf.append( accountCode);
									 buf.append( "##'||");
											 buf.append( BankCurrency_Third);
						 buf.append( "||'##'||");
								 buf.append( BankCurrency_Tilte);
										 buf.append( " as G FROM DUAL ");
												 buf.append(" UNION ALL");
				 buf.append( " select '户口结余' as A ,to_char(round(NVL(faccbalance,0),2)) AS B,'' AS C,'' AS D ,'' AS E,'' AS F,'' AS Y,");
						 buf.append( "to_char(");
								 buf.append(dbl.sqlDate(dEndDate));
										 buf.append( ",'yyyymmdd')||'##");
												 buf.append( accountCode);
			 buf.append("##'||");
					 buf.append( BankCurrency_Third);
							 buf.append( "||'##'||");
									 buf.append( BankCurrency_Detail);
											 buf.append( "||'##'||'1' as G FROM  ");
													 buf.append( " (select sum(s1.faccbalance) as faccbalance   from " + pub.yssGetTableName("tb_stock_cash") + " s1 where s1.fcheckstate=1 ");
															 buf.append( " and s1.fportcode=");
																	 buf.append(dbl.sqlString(this.portCode));
																			 buf.append(" and s1.fstoragedate=");
			 buf.append( dbl.sqlDate(this.dEndDate));
					 buf.append( " and s1.fcashacccode='");
							 buf.append( accountCode);
									 buf.append( "' )");
					

				 buf.append(	" union all");
						 buf.append( " select '汇率' as A ,to_char(fbaserate) AS B, '' AS C,'' AS D,'' AS E,'' AS F,'' AS Y,");//合并邵宏伟修改报表代码 xuqiji 20100608
								 buf.append( " to_char(");
										 buf.append( dbl.sqlDate(dEndDate));
				 buf.append( ",'yyyymmdd')||'##");
						 buf.append( accountCode);
								 buf.append( "##'||");
										 buf.append( BankCurrency_Third);
												 buf.append( "||'##'||");
				 buf.append( BankCurrency_Detail);
						 buf.append( "||'##'||'2' as G FROM ( ");
			     //合并邵宏伟修改报表代码 xuqiji 20100608
								 buf.append( " SELECT distinct v.fbaserate  FROM " + pub.yssGetTableName("tb_data_valrate") + " v , " + pub.yssGetTableName("tb_para_cashaccount") + " ca where v.fvaldate=");
										 buf.append( dbl.sqlDate(this.dEndDate));
												 buf.append(" and v.fcheckstate=1 and v.fportcode=");
						 buf.append( dbl.sqlString(this.portCode));
								 buf.append( " and ca.fcashacccode='");
										 buf.append( accountCode);
												 buf.append("' and v.fcurycode = ca.fcurycode ) ");
				 //--------------------------------end--------------------------//


		 buf.append(" union all ");
				 buf.append( " select '港币等值金额' as A ,to_char(round(nvl(hkdvalue,0),2)) AS B,'' AS C,'' AS D,'' AS E,'' AS F,'' AS Y,");
						 buf.append( " to_char(");
								 buf.append( dbl.sqlDate(dEndDate));
										 buf.append(",'yyyymmdd')||'##");
												 buf.append( accountCode);
			 buf.append( "##'||");
					 buf.append( BankCurrency_Third);
							 buf.append("||'##'||");
									 buf.append( BankCurrency_Detail);
											 buf.append("||'##'||'3' as G FROM ( ");
											 //合并邵宏伟修改报表代码 xuqiji 20100608
													 buf.append( " SELECT  sum(r.fbaserate * v.FACCBALANCE) as hkdvalue  FROM " + pub.yssGetTableName("Tb_Stock_Cash") + " v , " + pub.yssGetTableName("tb_data_valrate") + " r  where v.fstoragedate=");
				 buf.append(dbl.sqlDate(this.dEndDate));
						 buf.append( " and r.fvaldate = v.fstoragedate and r.fcurycode = v.fcurycode and v.fcheckstate=1 and v.fportcode=");
								 buf.append(dbl.sqlString(this.portCode));
										 buf.append( " and r.fportcode = v.fportcode and v.fcashacccode='");
												 buf.append( accountCode);
														 buf.append("')");
										   //------------------------------end------------------------//


			 buf.append("union all ");
					 buf.append( " select '银行' as A,FAffCorpName AS B,'' AS C,'' AS D,'' AS E,'' AS F,'' AS Y,");
							 buf.append(" to_char(");
									 buf.append( dbl.sqlDate(dEndDate));
											 buf.append( ",'yyyymmdd')||'##");
													 buf.append(accountCode);
			 buf.append("##'||");
					 buf.append( BankCurrency_Third);
							 buf.append( "||'##'||");
									 buf.append( BankCurrency_Detail);
											 buf.append("||'##'||'4' as G FROM (");
													 buf.append( " select e.FAffCorpName from " + pub.yssGetTableName("tb_para_cashaccount") + " d");
															 buf.append(" left join " + pub.yssGetTableName("tb_para_affiliatedcorp") + " e on d.fbankcode=e.FAffCorpCode");
																	 buf.append( "  where d.fcheckstate=1 and d.fcashacccode ='");
																			 buf.append( accountCode);
		 buf.append( "')");
				

				 buf.append(	" union all ");
						 buf.append(" select '户口类型' as A,fsubacctypename AS B,'' AS C,'' AS D,'' AS E,'' AS F,'' AS Y,");
								 buf.append("to_char(");
										 buf.append( dbl.sqlDate(dEndDate));
												 buf.append( ",'yyyymmdd')||'##");
				 buf.append( accountCode);
						 buf.append( "##'||");
								 buf.append( BankCurrency_Third);
										 buf.append( "||'##'||");
		 buf.append( BankCurrency_Detail);
				 buf.append( "||'##'||'5' as G  FROM ( ");
						 buf.append( " select jj.fsubacctypename from " + pub.yssGetTableName("tb_para_cashaccount") + " ac ");
								 buf.append( " left join Tb_Base_SubAccountType jj on ac.fsubacctype=jj.fsubacctypecode ");
										 buf.append( "  where jj.fcheckstate=1 and ac.fcheckstate=1 and ac.fcashacccode='");
		 buf.append( accountCode);
				 buf.append( "'");
						 buf.append( " and fportcode=");
								 buf.append( dbl.sqlString(this.portCode));
										 buf.append( "  )");
					

												 buf.append(	" union all ");
			 buf.append( " select '币值' as A ,fcurycode AS B,'' AS C,'' AS D,'' AS E,'' AS F,'' AS Y,");
					 buf.append("to_char(");
							 buf.append( dbl.sqlDate(dEndDate));
									 buf.append( ",'yyyymmdd')||'##");
											 buf.append( accountCode);
					 buf.append( "##'||");
							 buf.append( BankCurrency_Third);
									 buf.append( "||'##'||");
											 buf.append( BankCurrency_Detail);
													 buf.append( "||'##'||'6' as G FROM ( ");
			 buf.append( " SELECT distinct k.fcurycode FROM " + pub.yssGetTableName("TB_STOCK_CASH") + " k WHERE fcheckstate=1 AND fcashacccode='");
					 buf.append( accountCode);
							 buf.append("'");
									 buf.append( " and k.fportcode=");
											 buf.append(dbl.sqlString(this.portCode) + " ) )");

				try {
					rs1 = dbl.openResultSet(buf.toString());
					setResultValue(valueMaps, rs1);
					dbl.closeResultSetFinal(rs1);
					insertToTempBankCurrency(valueMaps);
					conn.commit();

					dbl.endTransFinal(conn, true); 

					valueMaps.clear();
					// stmt.close();

				} catch (YssException ex) {
					throw new YssException("获取现金帐户数据出错！", ex);
				} catch (SQLException ex) {
					throw new YssException(ex.getMessage());
				} finally {
					dbl.closeResultSetFinal(rs1);
				}
			}
		} catch (YssException e) {
			throw new YssException("获取现金帐户SQL出错！", e);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {

			dbl.closeResultSetFinal(rsacc);
		}
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
		BankCurrencyBean BankCurrency = null;
		try {
			int i = 0;
			double AQbanlce = 0;
			String temp = "";
			String orders;
			boolean ft = true;
			boolean fj = false;
			boolean hz = true;
			int j = 0;
			while (rs.next()) {
				BankCurrency = new BankCurrencyBean();
				orders = rs.getString("G");
				BankCurrency.forder = orders;
				BankCurrency.Fname = rs.getString("A");
				if ("##12##3".equals(orders.substring(orders.indexOf("##", 11),
						orders.indexOf("##", 11) + 7))) {
					ft = false;
				}
				if ("##13##1".equals(orders.substring(orders.indexOf("##", 11),
						orders.indexOf("##", 11) + 7))) {
					fj = true;
				}
				if (fj) {
					j++;
				}
				if (j > 1 && j<5) {
					if ("##13##2##1".equals(orders.substring(orders.indexOf(
							"##", 11), orders.indexOf("##", 11) + 10))) {
						if (rs.getString("B") == null) {
							BankCurrency.fdesc = rs.getString("B");
						} else {
							BankCurrency.fdesc = YssFun.formatNumber(Double
									.valueOf(rs.getString("B")).doubleValue(),
									"#,##0.00");
						}
					}
					if ("##13##2##2".equals(orders.substring(orders.indexOf(
							"##", 11), orders.indexOf("##", 11) + 10))) {
						if (rs.getString("B") == null) {
							BankCurrency.fdesc = rs.getString("B");
						} else {
							BankCurrency.fdesc = YssFun.formatNumber(Double
									.valueOf(rs.getString("B")).doubleValue(),
									"#,##0.#####");
						}

					}
					if ("##13##2##3".equals(orders.substring(orders.indexOf(
							"##", 11), orders.indexOf("##", 11) + 10))) {
						if (rs.getString("B") == null) {
							BankCurrency.fdesc = rs.getString("B");
						} else {
							BankCurrency.fdesc = YssFun.formatNumber(Double
									.valueOf(rs.getString("B")).doubleValue(),
									"#,##0.00");
						}

					}
				}else{
					BankCurrency.fdesc = rs.getString("B");
				}

				if (i > 5) {
					if (ft) {

						BankCurrency.jbal = YssFun.formatNumber(Double.valueOf(
								rs.getString("C")).doubleValue(), "#,##0.00");

						BankCurrency.dbal = YssFun.formatNumber(Double.valueOf(
								rs.getString("D")).doubleValue(), "#,##0.00");
					} else {
						if (hz) {
							if (rs.getString("C") == null) {
								BankCurrency.jbal = rs.getString("C");
							} else {
								BankCurrency.jbal = YssFun.formatNumber(Double
										.valueOf(rs.getString("C"))
										.doubleValue(), "#,##0.00");
							}
							if (rs.getString("D") == null) {
								BankCurrency.dbal = rs.getString("D");
							} else {
								BankCurrency.dbal = YssFun.formatNumber(Double
										.valueOf(rs.getString("D"))
										.doubleValue(), "#,##0.00");
							}

							hz = false;
						} else {
							BankCurrency.jbal = rs.getString("C");
							BankCurrency.dbal = rs.getString("D");
						}
					}
				} else {
					BankCurrency.jbal = rs.getString("C");
					BankCurrency.dbal = rs.getString("D");
				}

				if (i == 4) {
					AQbanlce += Double.valueOf(rs.getString("E")).doubleValue();

					BankCurrency.jdbal = YssFun.formatNumber(AQbanlce,
							"#,##0.00");
					BankCurrency.jybal = YssFun.formatNumber(AQbanlce,
							"#,##0.00");
				} else {
					if (i > 5) {
						if (ft) {
							temp = rs.getString("E");
							BankCurrency.jdbal = temp;
							AQbanlce += Double.valueOf(temp).doubleValue();
							AQbanlce = (double) Math.round(AQbanlce * 100) / 100;

							BankCurrency.jybal = YssFun.formatNumber(AQbanlce,
									"#,##0.00");
						} else {

							BankCurrency.jdbal = rs.getString("E");
							BankCurrency.jybal = rs.getString("Y");

						}
					} else {
						BankCurrency.jdbal = rs.getString("Y");
						BankCurrency.jybal = rs.getString("E");
					}

				}

				BankCurrency.transCode = rs.getString("F");

				valueMap.put(BankCurrency.forder, BankCurrency);
				i++;
			}
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
}
