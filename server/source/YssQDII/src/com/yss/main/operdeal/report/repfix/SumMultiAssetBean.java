package com.yss.main.operdeal.report.repfix;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.core.util.YssD;
import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class SumMultiAssetBean extends BaseBuildCommonRep {
	protected CommonRepBean repBean;
	private String dOperDate = ""; // 开始日期
	private String sSetCodes = ""; // 套账号
	private String sTablePrefix = ""; // 财务系统表前缀
	private String sAcctCode = ""; // 输入的科目号
	private String sLevel = ""; // add by tongjun #story 20609
	private HashMap hmCellStyle = null;
	private String length_1 = "";

	public void initBuildReport(BaseBean bean) throws YssException {
		repBean = (CommonRepBean) bean;
		// 解析前台传入的条件字符串
		this.parseRowStr(this.repBean.getRepCtlParam());
	}

	public void parseRowStr(String sRowStr) throws YssException {
		try {
			if (sRowStr.equals("")) {
				return;
			}
			String reqAry[] = null;
			reqAry = sRowStr.split("\n"); // 这里是要获得参数

			this.dOperDate = reqAry[0].split("\r")[1];
			this.sSetCodes = reqAry[1].split("\r")[1];
			if (reqAry[2].split("\r")[0].equals("3")) {
				this.sAcctCode = reqAry[2].split("\r")[1].length() == 0 ? " "
						: reqAry[2].split("\r")[1];
			} else {
				this.sAcctCode = "";
			}
			if (reqAry.length > 3 || reqAry[2].split("\r")[0].equals("4")) {
				this.sLevel = reqAry[2].split("\r")[0].equals("4") == true ? reqAry[2]
						.split("\r")[1]
						: reqAry[3].split("\r")[1];
				// add by tongjun #story 20609
			} else {
				this.sLevel = "0";
			}
			if (this.sLevel.equals("0")) {
				length_1 = "<=20";
			}
			if (this.sLevel.equals("1")) {
				length_1 = "=4";
			}
			if (this.sLevel.equals("2")) {
				length_1 = "=6";
			}
			if (this.sLevel.equals("3")) {
				length_1 = "=8";
			}
			if (this.sLevel.equals("4")) {
				length_1 = "=14";
			}

		} catch (Exception ye) {
			throw new YssException("解析报表查询条件出错：" + ye.getMessage());
		}

	}

	private double[] getVchAccrualOfAcctCode(String sAcctCode,
			String sCuryCode, String sTabPrefix, String sAuxiAcc)
			throws YssException {
		double[] dReturn = new double[] { 0, 0, 0, 0 };
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "select a.FBal as FJBal,a.FBBal as FJBBal,b.FBal as FDBal,b.FBBal as FDBBal from "
					+ " (select FKmh,sum(FBal) as FBal,sum(FBBal) as FBBal from "
					+ sTabPrefix
					+ "fcwvch  "
					+ " where fkmh = "
					+ dbl.sqlString(sAcctCode)
					+ " and FJD = 'J' and FTerm = "
					+ YssFun.toInt(YssFun.formatDate(this.dOperDate, "MM"))
					+ " and FCYID = "
					+ dbl.sqlString(sCuryCode)
					+ " and FAuxiAcc = "
					+ dbl.sqlString(sAuxiAcc)
					+ " group by FKmh) a "
					+ " full join (select FKmh,sum(FBal) as FBal,sum(FBBal) as FBBal from "
					+ sTabPrefix
					+ "fcwvch "
					+ " where fkmh = "
					+ dbl.sqlString(sAcctCode)
					+ " and FJD = 'D' and FTerm = "
					+ YssFun.toInt(YssFun.formatDate(this.dOperDate, "MM"))
					+ " and FCYID = "
					+ dbl.sqlString(sCuryCode)
					+ " and FAuxiAcc = "
					+ dbl.sqlString(sAuxiAcc)
					+ " group by FKmh) b " + " on a.FKmh = b.FKmh";
			rs = dbl.queryByPreparedStatement(strSql);

			if (rs.next()) {
				dReturn[0] = rs.getDouble("FJBal");
				dReturn[1] = rs.getDouble("FJBBal");
				dReturn[2] = rs.getDouble("FDBal");
				dReturn[3] = rs.getDouble("FDBBal");
			}
		} catch (Exception ye) {
			throw new YssException("获取科目【" + sAcctCode + "】的本月发生额出错！", ye);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

		return dReturn;
	}

	public String getSetName(String sSetCode) throws YssException {
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		try {
			String sYear = YssFun.formatDate(this.dOperDate, "yyyy");

			strSql = " select * from lsetlist " + " where FYear = " + sYear
					+ " and FSetCode = " + YssFun.toInt(sSetCode);

			rs = dbl.queryByPreparedStatement(strSql);

			if (rs.next()) {
				sReturn = rs.getString("FSetName");
			}
		} catch (Exception ye) {
			throw new YssException("获取套账名称出错！", ye);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

		return sReturn;
	}

	private String getAcctMoneyDetail() throws YssException {
		StringBuffer bufReturn = new StringBuffer();
		StringBuffer bufTemp = null;
		double[] dAccrualList = new double[] { 0, 0, 0, 0 };
		StringBuffer bufSql = new StringBuffer();
		ResultSet rs = null;
		try {
			int iTerm = YssFun.toInt(YssFun.formatDate(this.dOperDate, "MM")) - 1;
			bufSql
					.append(
							"select Fmonth,Facctcode,sum(FStartbal) as FStartbal,sum(Fdebit) as Fdebit,sum(Fcredit) as Fcredit,"
									+ "sum(Facccredit) as Facccredit,sum(FendBal) as FendBal,sum(Fbstartbal) as Fbstartbal,sum(Fbdebit) as Fbdebit,")
					.append(
							"sum(FBcredit) as FBcredit,sum(Fbaccdebit) as Fbaccdebit ,sum(Fbacccredit) as  Fbacccredit,sum(Fbendbal) as Fbendbal "
									+ ",sum(FastartBAl) as FastartBAl ,sum(Fadebit) as Fadebit ,sum(Facredit) as Facredit ,sum(Faaccdebit) as Faaccdebit ,"
									+ "sum(Faacccredit) Faacccredit ,sum(Faendbal) as  Faendbal ,")
					.append(
							"  Facctname , FBaldc  ,sum(Fbeginningofyear) as Fbeginningofyear ,sum(Fbbbeginningofyear) as  Fbbbeginningofyear"
									+ " ,")
					.append(
							"sum(FJbal) as FJbal ,sum(FJbbal) as FJbbal ,sum(Fdbal) as Fdbal ,sum(Fdbbal) as Fdbbal from tmp_sum_MultiAsset")
					.append(" group by Fmonth,Facctcode,Facctname,FBaldc order by Facctcode");

			rs = dbl.queryByPreparedStatement(bufSql.toString());
			while (rs.next()) {
				if(rs.getDouble("FBeginningOfYear") == 0 && rs.getDouble("FBBBeginningOfYear") == 0 &&
						rs.getDouble("FJBal") == 0 && rs.getDouble("FJBBal") == 0 && rs.getDouble("FDBal") == 0 && rs.getDouble("FDBBal") ==0)
				{
					continue;
				}
				
				bufTemp = new StringBuffer();
				
				bufTemp.append(rs.getString("Facctname")).append("\t");
				//科目代码
				bufTemp.append(rs.getString("FAcctCode")).append("\t");
				//科目名称
				//年初本位币余额的借贷
				bufTemp.append(rs.getDouble("FBBBeginningOfYear") == 0 ? "平" : (rs.getInt("FBalDC") == 1 ? "借" : "贷")).append("\t");
				//年初本位币余额
				bufTemp.append(YssFun.formatNumber(rs.getDouble("FBBBeginningOfYear"),"###,###,###,##0.00")).append("\t"); //edit by tongjun BUg 107364
				//期初本位币余额的借贷
				bufTemp.append(rs.getDouble("FBEndBal") == 0 ? "平" : (rs.getInt("FBalDC") == 1 ? "借" : "贷")).append("\t");
				//期初本位币余额，即上个期间的余额
				bufTemp.append(YssFun.formatNumber(rs.getDouble("FBEndBal"),"###,###,###,##0.00")).append("\t");//edit by tongjun BUg 107364
				//本期本位币借方发生额
				bufTemp.append(YssFun.formatNumber(rs.getDouble("FJBBal"), "###,###,###,##0.00")).append("\t");
				//本期本位币贷方发生额
				bufTemp.append(YssFun.formatNumber(rs.getDouble("FDBBal"), "###,###,###,##0.00")).append("\t");
				//本年本位币累计借方发生额，即上个期间的借方累计发生额+本月发生额
				bufTemp.append(YssFun.formatNumber(rs.getDouble("FBAccDebit") + rs.getDouble("FJBBal"), "###,###,###,##0.00")).append("\t");
				//本年本位币累计贷方发生额，即上个期间的贷方累计发生额+本月发生额
				bufTemp.append(YssFun.formatNumber(rs.getDouble("FBAccCredit") + rs.getDouble("FDBBal"), "###,###,###,##0.00")).append("\t");
				//期末本位币余额的借贷
				bufTemp.append(rs.getDouble("FBEndBal") + rs.getDouble("FJBBal") - rs.getDouble("FDBBal") == 0 ? "平" : (rs.getInt("FBalDC") == 1 ? "借" : "贷")).append("\t");
				//期末本位币余额，即上个期间的期末余额+本月发生额
				bufTemp.append(YssFun.formatNumber(rs.getDouble("FBEndBal") + rs.getDouble("FJBBal") - rs.getDouble("FDBBal"), "###,###,###,##0.00"));

				bufReturn.append(buildRowCompResult(bufTemp.toString())).append("\r\n");
//				bufReturn.append(bufTemp.toString()).append("\r\n");

				bufTemp.setLength(0);
			}
		} catch (Exception ye) {
			throw new YssException(ye);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

		return bufReturn.toString();
	}

	public String buildReport(String sType) throws YssException {
		StringBuffer bufReturn = new StringBuffer();
		StringBuffer strsqlBuffer = new StringBuffer();
		StringBuffer bufTemp = null;
		String strSql = "";
		ResultSet rs = null;
		String[] sSetList = null;
		String sSetPrefix = "";

		try {
			sSetList = this.sSetCodes.split(",");
			int iTerm = YssFun.toInt(YssFun.formatDate(this.dOperDate, "MM")) - 1;
			hmCellStyle = getCellStyles("Rep_Sum_MultiAsset");
			createTmpTable();

			if (this.sAcctCode.trim().length() > 0
					&& (this.sAcctCode.equals("0") == false)) {
				sSetPrefix = this.sAcctCode.trim();
			} else {
				sSetPrefix = "";
			}
			for (int i = 0; i < sSetList.length; i++) {
				String sSetName = getSetName(sSetList[i]);

				sTablePrefix = "a" + YssFun.formatDate(this.dOperDate, "yyyy")
						+ sSetList[i];
				isExist(sTablePrefix);

				strSql = " select (case when a.FAcctcode is not null then a.Facctcode else b.Fkmh end) as FAcctCode, "
						+ " (case when a.Fcurcode is not null then a.Fcurcode else b.Fcyid end) as FCurCode "
						+ " from (select distinct FAcctCode,FCurCode from "
						+ sTablePrefix
						+ "lbalance "
						+ " where fmonth = "
						+ (YssFun
								.toInt(YssFun.formatDate(this.dOperDate, "MM")) - 1)
						+ (sSetPrefix.length() == 0 ? " and length(FAcctCode)"
								+ length_1 + ") a " : " and FAcctCode like '"
								+ sSetPrefix + "%' and "
								+ "length(Facctcode)" + length_1 + ") a ")
						+ " full join (select distinct FKmh,FCYID from "
						+ sTablePrefix
						+ "fcwvch "
						+ " where fterm = "
						+ (YssFun
								.toInt(YssFun.formatDate(this.dOperDate, "MM")))
						+ (sSetPrefix.length() == 0 ? " and length(FKmh)"
								+ length_1 + ") b " : " and FKmh like '"
								+ sSetPrefix + "%' and " + "length(FKmh)"
								+ length_1 + ") b ")
						+ " on a.facctcode = b.fkmh and a.fcurcode = b.fcyid "
						+ " order by FCurCode,facctcode";
				rs = dbl.queryByPreparedStatement(strSql);

				while (rs.next()) {
					insertTmp(rs.getString("FAcctCode"), sTablePrefix, rs
							.getString("FCurCode"), sSetName);
				}
				rs.close();
				
			}
			bufReturn.append(getAcctMoneyDetail());
			if(dbl.yssTableExist("tmp_sum_MultiAsset"))
				{strSql = "drop table tmp_sum_MultiAsset";
				dbl.executeSql(strSql);}
		} catch (Exception ye) {
			throw new YssException(ye);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

		return bufReturn.toString();
	}

	protected String buildRowCompResult(String str) throws YssException {
		String strSql = "";
		String strReturn = "";
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		String sKey = "";
		RepTabCellBean rtc = null;
		String[] sArry = null;
		try {
			sArry = str.split("\t");
			for (int i = 0; i < sArry.length; i++) {
				sKey = "Rep_Sum_MultiAsset" + "\tDSF\t-1\t" + i;
				if (hmCellStyle.containsKey(sKey)) {
					rtc = (RepTabCellBean) hmCellStyle.get(sKey);
					buf.append(rtc.buildRowStr()).append("\n");
				}
				buf.append(sArry[i]).append("\t");
			}
			if (buf.toString().trim().length() > 1) {
				strReturn = buf.toString().substring(0,
						buf.toString().length() - 1);
			}
			return strReturn + "\t\t";
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	private void insertTmp(String sAcctCode, String sTabPrefix,
			String sCuryCode, String sSetName) throws YssException {
		StringBuffer bufReturn = new StringBuffer();
		StringBuffer bufTemp = null;
		double[] dAccrualList = new double[] { 0, 0, 0, 0 };
		StringBuffer bufSql = new StringBuffer();
		ResultSet rs = null;
		try {
			int iTerm = YssFun.toInt(YssFun.formatDate(this.dOperDate, "MM")) - 1;
			bufSql.append("insert into tmp_sum_MultiAsset select * from (");
			bufSql.append(" select a.*,b.FAcctName,b.FBalDC,c.FCurName, ");
			bufSql.append(" d.FEndBal   as FBeginningOfYear, ");
			bufSql.append(" d.FBEndBal  as FBBBeginningOfYear, ");
			bufSql.append(" e.FCYID,e.FJBal,e.FJBBal,e.FDBal,e.FDBBal ");
			bufSql.append(" from " + sTabPrefix + "lbalance a ");
			bufSql.append(" left join " + sTabPrefix + "laccount b ");
			bufSql.append(" on a.Facctcode = b.Facctcode ");
			bufSql.append(" left join " + sTabPrefix + "lcurrency c ");
			bufSql.append(" on a.FCurCode = c.FCurCode ");
			bufSql
					.append(" left join (select FEndBal, FBEndBal, FCurCode, FAcctCode ");
			bufSql.append(" from " + sTabPrefix + "lbalance ");
			bufSql.append(" where FMonth = 0) d ");
			bufSql.append(" on a.FAcctCode = d.FAcctCode ");
			bufSql.append(" and a.FCurCode = d.FCurCode ");
			bufSql.append(" ");
			bufSql.append(" full join (select ");
			bufSql
					.append(" (case when a.FCYID is not null then a.FCYID else b.FCYID end ) as FCYID, ");
			bufSql
					.append(" ");
			bufSql
					.append(" a.FBal  as FJBal,a.FBBal as FJBBal,b.FBal  as FDBal,b.FBBal as FDBBal ");
			bufSql
					.append(" from (select FCYID,sum(FBal) as FBal, sum(FBBal) as FBBal from "
							+ sTabPrefix + "fcwvch ");
			bufSql.append(" where FJD = 'J' and FTerm = "
					+ YssFun.toInt(YssFun.formatDate(this.dOperDate, "MM")));
			bufSql.append(" and fdate <= " + dbl.sqlDate(this.dOperDate));
			bufSql.append(" and (fconfirmer <> ' ' or fconfirmer is null)");
			bufSql.append(" and FKmh like '" + sAcctCode + "%' ");
			bufSql.append(" group by FCYID) a ");
			bufSql
					.append(" full join (select FCYID, sum(FBal) as FBal, sum(FBBal) as FBBal ");
			bufSql.append(" from " + sTabPrefix + "fcwvch ");
			bufSql.append(" where FJD = 'D' and FTerm = "
					+ YssFun.toInt(YssFun.formatDate(this.dOperDate, "MM")));
			bufSql.append(" and fdate <= " + dbl.sqlDate(this.dOperDate));
			bufSql.append(" and (fconfirmer <> ' ' or fconfirmer is null)");
			bufSql.append(" and FKmh like '" + sAcctCode + "%' ");
			bufSql.append(" group by FCYID) b ");
			bufSql
					.append(" on a.FCYID = b.FCYID ) e ");
			bufSql
					.append(" on a.Fcurcode = e.FCYID ");
			bufSql.append(" where a.Fmonth = " + iTerm);
			bufSql.append(" and a.FAcctCode = " + dbl.sqlString(sAcctCode));
			bufSql.append(" and a.FCurCode = " + dbl.sqlString(sCuryCode))
					.append(" )");
			dbl.executeSql(bufSql.toString());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void isExist(String sSetCode) throws YssException {
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		try {

			strSql = " select * from " + sSetCode + "lbalance";

			rs = dbl.queryByPreparedStatement(strSql);

			if (!rs.next()) {

				throw new YssException("不存在【" + sSetCode + "lbalance" + "】！");
			}
		} catch (Exception ye) {
			throw new YssException("不存在该套帐！", ye);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	public void createTmpTable() throws YssException
    {
   		
   		StringBuffer strSql = new StringBuffer();
   		try {
   			if(!dbl.yssTableExist("tmp_sum_MultiAsset"))
   			{ strSql.append("create table tmp_sum_MultiAsset (Fmonth varchar(20),Facctcode varchar(20),Fcurcode varchar(20),FStartbal number(18,4), Fdebit number(18,4),")
   			.append("Fcredit number(18,4),Faccdebit number (18,4),Facccredit number(18,4),FendBal number(18,4),Fbstartbal number(18,4),Fbdebit number(18,4),")
   			.append("FBcredit number(18,4),Fbaccdebit number(18,4),Fbacccredit number(18,4),")
            .append("Fbendbal number(18,4),FastartBAl number(18,4),Fadebit number(18,4),")
            .append("Facredit number(18,4),Faaccdebit number(18,4),Faacccredit number(18,4),Faendbal number(18,4) ,")
            .append("Fisdetail varchar(1),Fauxiacc varchar(50),Facctname varchar(50),")
            .append("FBaldc varchar(20),Fcurname varchar(20),Fbeginningofyear number(18,4),")
            .append("Fbbbeginningofyear number(18,4),Fcyid varchar(20),")
            .append("FJbal number(18,4),FJbbal number(18,4),Fdbal number(18,4),Fdbbal number(18,4))");
         dbl.executeSql(strSql.toString());
   			}
   		
   		} catch (Exception ye) {
   			throw new YssException("创建多基金临时表出错！", ye);
   		} 

   	}
}
