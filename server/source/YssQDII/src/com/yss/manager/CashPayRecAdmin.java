package com.yss.manager;

import com.yss.dsub.*;
import com.yss.main.operdata.*;
import com.yss.util.*;
import java.sql.*;
import java.util.*;

public class CashPayRecAdmin extends BaseBean {
	ArrayList addList = new ArrayList();
	String insertNum = "";
	String inCometypeNum = ""; // 收入类型的应收应付编号。sj modified 20090120
								// QDV4交银施罗德2009年01月4日02_B bugId:MS00141
	private Hashtable relaOrderNum = null; // 存放关联编号的容器.MS00141
											// QDV4交银施罗德2009年01月4日02_B sj
											// modified
	private String deleteNums = ""; // 查询删除时的应收应付编号 by leeyu 20090429
									// QDV4赢时胜（上海）2009年4月16日04_B MS00390

	public CashPayRecAdmin() {
	}

	// ------ add by wangzuochun 2010.08.27 MS01606 定存业务处理后，不能删除历史资金调拨数据
	// QDV4赢时胜(测试)2010年08月12日07_B
	public ArrayList getAddList() {
		return addList;
	}

	public void setAddList(ArrayList addList) {
		this.addList = addList;
	}

	// ----------------MS01606----------------//
	// deleteNums的只读属性方法 by leeyu 20090429 QDV4赢时胜（上海）2009年4月16日04_B MS00390
	public String getDeleteNums() {
		return deleteNums;
	}

	// ----存放关联编号的容器.MS00141 QDV4交银施罗德2009年01月4日02_B sj modified ----//
	public Hashtable getRelaOrderNum() {
		return relaOrderNum;
	}

	// --------------------------------------------------------------------------//

	public void addList(CashPecPayBean cashpecpay) {
		this.addList.add(cashpecpay);
	}

	public ArrayList getList() {
		return addList;
	}

	// -----sj modified 20090120 QDV4交银施罗德2009年01月4日02_B bugId:MS00141
	public String getInComeTypeNum() {
		return inCometypeNum;
	}

	public void setInComeTypeNum(String sInComeTypeNum) {
		this.inCometypeNum = sInComeTypeNum;
	}

	/**
	 * 重载的方法 添加舍入位数 by leeyu 20100827 合并太平版本调整
	 * 
	 * @param transDate
	 * @param tsfType
	 * @param subTsfType
	 * @param port
	 * @param datasource
	 * @param bInsertZero
	 * @param sRelaNum
	 * @param sRelaType
	 * @param bdigit4
	 * @throws YssException
	 *             MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A
	 */
	public void insert(java.util.Date transDate, String tsfType,
			String subTsfType, String port, int datasource,
			boolean bInsertZero, String sRelaNum, String sRelaType,
			boolean bdigit4) throws YssException {
		insert("", transDate, transDate, tsfType, subTsfType, "", "", port, "",
				"", "", datasource, true, bInsertZero, bdigit4, 0, sRelaNum,
				sRelaType);
	}

	// ----------------------------------------------------------------------
	/**
	 * 重载的方法
	 * 
	 * @param transDate
	 *            Date
	 * @param tsfType
	 *            String
	 * @param subTsfType
	 *            String
	 * @param port
	 *            String
	 * @param datasource
	 *            int
	 * @param bInsertZero
	 *            boolean
	 * @param sRelaNum
	 *            String
	 * @param sRelaType
	 *            String
	 * @throws YssException
	 *             MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A
	 */
	public void insert(java.util.Date transDate, String tsfType,
			String subTsfType, String port, int datasource,
			boolean bInsertZero, String sRelaNum, String sRelaType)
			throws YssException {
		insert("", transDate, transDate, tsfType, subTsfType, "", "", port, "",
				"", "", datasource, true, bInsertZero, false, 0, sRelaNum,
				sRelaType);
	}

	public void insert(java.util.Date transDate, String tsfType,
			String subTsfType, String port, int datasource, boolean bInsertZero)
			throws YssException {
		insert("", transDate, transDate, tsfType, subTsfType, "", "", port, "",
				"", "", datasource, true, bInsertZero, false, 0, "", ""); // MS00014
																			// QDV4.1赢时胜（上海）2009年4月20日14_A
	}

	/**
	 * add by huangqirong 2013-04-15 bug #7545 选中的现金账户
	 * */
	public void insert(java.util.Date transDate, String tsfType,
			String subTsfType, String sCashAccCode ,String port, int datasource, boolean bInsertZero)
			throws YssException {
		insert("", transDate, transDate, tsfType, subTsfType, sCashAccCode, "", port, "",
				"", "", datasource, true, bInsertZero, false, 0, "", ""); 
	}

	public void insert(java.util.Date transDate, String tsfType,
			String subTsfType, String port, String invMgr, String cat,
			String cashacc, String cury, int datasource) throws YssException {
		insert("", transDate, transDate, tsfType, subTsfType, cashacc, cury,
				port, invMgr, cat, "", datasource, true, false, false, 0, "",
				""); // MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A
	}

	public void insert(java.util.Date transDate, String tsfType,
			String subTsfType, String port, String invMgr, String cat,
			String cashacc, String cury, int datasource, boolean bdigit4)
			throws YssException {
		insert("", transDate, transDate, tsfType, subTsfType, cashacc, cury,
				port, invMgr, cat, "", datasource, true, false, bdigit4, 0, "",
				""); // MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A
	}

	public String getInsertNum() {
		return insertNum;
	}

	/**
	 * 重载拼接删除条件的方法 xuqiji 20091204 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
	 * 
	 * @param sNums
	 *            交易编号
	 * @param beginDate
	 *            业务起始日
	 * @param endDate
	 *            业务截止日
	 * @param sTsfTypeCode
	 *            调拨类型
	 * @param sSubTsfTypeCode
	 *            调拨子类型
	 * @param sCashAccCode
	 *            现金账户
	 * @param sCuryCode
	 *            币种
	 * @param sPortCode
	 *            组合代码
	 * @param sAnalysisCode1
	 *            分析代码1
	 * @param sAnalysisCode2
	 *            分析代码2
	 * @param sAnalysisCode3
	 *            分析代码3
	 * @param iDsInd
	 * @param iInOutType
	 *            方向
	 * @return
	 */
	public String buildWhereSql(String sNums, java.util.Date beginDate,
			java.util.Date endDate, String sTsfTypeCode,
			String sSubTsfTypeCode, String sCashAccCode, String sCuryCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, int iDsInd, int iInOutType
			// --- MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A
			, String sRelaNums, String sRelaTypes, String sAttrClsCode) {
		//edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B FDataOrigin = 0 表示为自动生成的数据
		String sResult = " where 1=1 and FDataOrigin = 0 ";
		if (sNums.length() > 0) {
			sResult += " and FNum in(" + operSql.sqlCodes(sNums) + ")";
		}
		if (beginDate != null && endDate != null) {
			sResult += " and (FTransDate between " + dbl.sqlDate(beginDate)
					+ " and " + dbl.sqlDate(endDate) + ")";
		}
		if (sTsfTypeCode.length() > 0) {
			if (sTsfTypeCode.indexOf(",") > 0) {
				sResult += " and FTsfTypeCode in ("
						+ operSql.sqlCodes(sTsfTypeCode) + ")";
			} else {
				sResult += " and FTsfTypeCode = " + dbl.sqlString(sTsfTypeCode);
			}
		}
		if (sSubTsfTypeCode.length() > 0) {
			if (sSubTsfTypeCode.indexOf(",") > 0) {
				sResult += " and FSubTsfTypeCode in ("
						+ operSql.sqlCodes(sSubTsfTypeCode) + ")";
			} else if (sSubTsfTypeCode.indexOf("%") > 0) {
				sResult += " and FSubTsfTypeCode like "
						+ operSql.sqlCodes(sSubTsfTypeCode);
			} else {
				sResult += " and FSubTsfTypeCode = "
						+ dbl.sqlString(sSubTsfTypeCode);
			}
		}
		if (iDsInd > -1) {
			sResult += " and FDataSource = " + iDsInd;
		}
		if (sCashAccCode.length() > 0) {
			sResult += " and FCashAccCode in ("
					+ operSql.sqlCodes(sCashAccCode) + ")";
		}
		if (sCuryCode.length() > 0) { // 为可删除多种条件 sj edit 20080123
			if (sCuryCode.indexOf(",") > 0) {
				sResult += " and FCuryCode in (" + operSql.sqlCodes(sCuryCode)
						+ ")";
			} else {
				sResult += " and FCuryCode = " + dbl.sqlString(sCuryCode);
			}
		}
		if (sPortCode.length() > 0) {
			sResult += " and FPortCode in (" + operSql.sqlCodes(sPortCode)
					+ ")";
		}
		if (sAnalysisCode1.length() > 0) { // 为可删除多种条件 sj edit 20080123
			if (sAnalysisCode1.indexOf(",") > 0) {
				sResult += " and FAnalysisCode1 in ("
						+ operSql.sqlCodes(sAnalysisCode1) + ")";
			} else {
				sResult += " and FAnalysisCode1 = "
						+ dbl.sqlString(sAnalysisCode1);
			}
		}
		if (sAnalysisCode2.length() > 0) {
			if (sAnalysisCode2.indexOf(",") > 0) {
				sResult += " and FAnalysisCode2 in ("
						+ operSql.sqlCodes(sAnalysisCode2) + ")";
			} else {
				sResult += " and FAnalysisCode2 = "
						+ dbl.sqlString(sAnalysisCode2);
			}
		}
		if (sAnalysisCode3.length() > 0) {
			sResult += " and FAnalysisCode3 = " + dbl.sqlString(sAnalysisCode3);
		}
		if (iInOutType != 0) {
			sResult += " and FInOut =" + iInOutType;// 添加按方向删除条件
		}
		// ---MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A
		// 2009.06.29 蒋锦 添加
		// 当输入的关联编号和关联编号类型都为空时查询这两字段位空的数据删除，
		// 否则将他们所谓查询条件代入
		if ((sRelaTypes == null || sRelaTypes.length() == 0)
				&& (sRelaNums == null || sRelaNums.length() == 0)) {
			// modify by nimengjing 2011.2.11 BUG #1058 做业务处理时会把收益支付产生的现金应收应付数据删除
			if ((sTsfTypeCode.length() >0)
					&& (sSubTsfTypeCode.length() >0)) {
				sResult += " and (FRelaType is null or FRelaType = '') and (FRelaNum is null or FRelaNum = '')";
			} else {
				if (sRelaTypes == null) {
					sResult += " and FRelaType is null ";
				} else {
					sResult += " and FRelaType = ''";
				}
				if (sRelaNums == null) {
					sResult += " and FRelaNum is null";
				} else {
					sResult += " and FRelaNum = ''";
				}
			}
			// ----------------------------------end bug #1058-------------------------------------------------------
		} else {
			if (!(sRelaTypes == null || sRelaTypes.length() == 0)) {
				sResult += " and FRelaType in (" + operSql.sqlCodes(sRelaTypes)
						+ ")";
			}
			if (!(sRelaNums == null || sRelaNums.length() == 0)) {
				// edited by zhouxiang MS01455
				if (sRelaNums.split("\t")[0].equals("isnull")) {
					// edited by zhouxiang MS01744 2010.10.18
					// 如果是isnull标记则将后面所有的关联编号加进入
					if (sRelaNums.split("\t").length > 1)
					// modify by zhangfa 20101229 BUG #763
					// 收益计提时，计提两遍，现金应收应付应收存款利息会重复
					{
						sResult += " and (FRelaNum in ("
								+ operSql.sqlCodes(sRelaNums.split("\t")[1])
								+ ") or (FRelaNum is null or FRelaNum = ''))";
					}
					// end-- by zhouxiang MS01744 2010.10.18
					// 如果是isnull标记则将后面所有的关联编号加进入
				} else {
					sResult += " and FRelaNum in ("
							+ operSql.sqlCodes(sRelaNums) + ")";
				}
				// end by zhouxiang MS01455
			}
		}
		// --- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2010.11.22 ---//
		/**shashijie 2012-7-2 STORY 2475 */
		if (sAttrClsCode != null && sAttrClsCode.trim().length() != 0) {
		/**end*/
			sResult += " and FATTRCLSCODE in ("
					+ operSql.sqlCodes(sAttrClsCode) + ")";
		}
		// --- NO.125 用户需要对组合按资本类别进行子组合的分类 end ------------------------------//
		return sResult;
	}

	/**
	 * 重载拼接删除条件 xuqiji 20091204 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
	 * 
	 * @param sNums
	 *            交易编号
	 * @param beginDate
	 *            业务起始日
	 * @param endDate
	 *            业务截止日
	 * @param sTsfTypeCode
	 *            调拨类型
	 * @param sSubTsfTypeCode
	 *            调拨子类型
	 * @param sCashAccCode
	 *            现金账户
	 * @param sCuryCode
	 *            币种
	 * @param sPortCode
	 *            组合代码
	 * @param sAnalysisCode1
	 *            分析代码1
	 * @param sAnalysisCode2
	 *            分析代码2
	 * @param sAnalysisCode3
	 *            分析代码3
	 * @param iDsInd
	 * @return
	 */
	public String buildWhereSql(String sNums, java.util.Date beginDate,
			java.util.Date endDate, String sTsfTypeCode,
			String sSubTsfTypeCode, String sCashAccCode, String sCuryCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, int iDsInd) {
		return buildWhereSql(sNums, beginDate, endDate, sTsfTypeCode,
				sSubTsfTypeCode, sCashAccCode, sCuryCode, sPortCode,
				sAnalysisCode1, sAnalysisCode2, sAnalysisCode3, iDsInd, 0, "",
				"", "");
	}

	public String loadCashPRNums(java.util.Date transferDate,
			String sTsfTypeCode, String sSubTsfTypeCode, String sCashAccCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2)
			throws YssException {
		return loadCashPRNums("", transferDate, sTsfTypeCode, sSubTsfTypeCode,
				sCashAccCode, "", sPortCode, sAnalysisCode1, sAnalysisCode2,
				"", 0);
	}

	public String loadCashPRNums(String sNums, java.util.Date transferDate,
			String sTsfTypeCode, String sSubTsfTypeCode, String sCashAccCode,
			String sCuryCode, String sPortCode, String sAnalysisCode1,
			String sAnalysisCode2, String sAnalysisCode3, int iDsInd)
			throws YssException {
		String sResult = "";
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "select FNum from "
					+ pub.yssGetTableName("Tb_Data_CashPayRec")
					+ this.buildWhereSql(sNums, transferDate, transferDate,
							sTsfTypeCode, sSubTsfTypeCode, sCashAccCode,
							sCuryCode, sPortCode, sAnalysisCode1,
							sAnalysisCode2, sAnalysisCode3, iDsInd);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				sResult += rs.getString("FNum") + ",";
			}
			// sResult = operSql.sqlCodes(sResult);
			return sResult;
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 重载的方法，可以设置金额是保留4位或是2位。sj edit 20080626.
	 * 
	 * @param beginDate
	 *            Date
	 * @param endDate
	 *            Date
	 * @param sTsfTypeCode
	 *            String
	 * @param sSubTsfTypeCode
	 *            String
	 * @param sCashAccCode
	 *            String
	 * @param sPortCode
	 *            String
	 * @param sAnalysisCode1
	 *            String
	 * @param sAnalysisCode2
	 *            String
	 * @param sAnalysisCode3
	 *            String
	 * @param iDsInd
	 *            int
	 * @param bdigit4
	 *            boolean
	 * @throws YssException
	 */
	public void insert(java.util.Date beginDate, java.util.Date endDate,
			String sTsfTypeCode, String sSubTsfTypeCode, String sCashAccCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, int iDsInd, boolean bdigit4)
			throws YssException {
		// edited by zhouxiang MS01455 先删后插操作预处理，
		// 收益计提利息将对应关联号（加载“sRelaNum”）的应收应付删除掉
		ArrayList CashList = this.getList();
		String sRelaNum = "isnull\t";
		String sAttrClsCodes = "";// add by jiangshichao 2011.01.12 NO.125
									// 用户需要对组合按资本类别进行子组合的分类
		for (int j = 0; j < CashList.size(); j++) {
			CashPecPayBean cashbean = (CashPecPayBean) CashList.get(j);
			sRelaNum += (cashbean.getRelaNum() == null || cashbean.getRelaNum()
					.length() == 0) ? "" : (cashbean.getRelaNum() + ",");// 添加判断，如果关联编号为空则不添加
																			// by
																			// leeyu
																			// 20100818
																			// 合并太平版本调整
			// add by jiangshichao 2011.01.12 NO.125 用户需要对组合按资本类别进行子组合的分类
			sAttrClsCodes += (cashbean.getStrAttrClsCode() == null || cashbean
					.getStrAttrClsCode().length() == 0) ? "" : (cashbean
					.getStrAttrClsCode() + ",");
			// add by jiangshichao 2011.01.12 NO.125 用户需要对组合按资本类别进行子组合的分类
		}
		// end-- by zhoxuiang MS01455 先删后插操作预处理，
		// 收益计提利息将对应关联号（加载“sRelaNum”）的应收应付删除掉
		insert("", beginDate, endDate, sTsfTypeCode, sSubTsfTypeCode,
				sCashAccCode, "", sPortCode, sAnalysisCode1, sAnalysisCode2,
				sAnalysisCode3, iDsInd, true, false, bdigit4, 0, sRelaNum, "",
				sAttrClsCodes); // MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A
	}

	public void insert(java.util.Date beginDate, java.util.Date endDate,
			String sTsfTypeCode, String sSubTsfTypeCode, String sCashAccCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, int iDsInd) throws YssException {
		insert("", beginDate, endDate, sTsfTypeCode, sSubTsfTypeCode,
				sCashAccCode, "", sPortCode, sAnalysisCode1, sAnalysisCode2,
				sAnalysisCode3, iDsInd, true, false, false, 0, "", ""); // MS00014
																		// QDV4.1赢时胜（上海）2009年4月20日14_A
	}

	/**
	 * 重载的方法
	 * 
	 * @param sNums
	 *            String
	 * @param beginDate
	 *            Date
	 * @param endDate
	 *            Date
	 * @param sTsfTypeCode
	 *            String
	 * @param sSubTsfTypeCode
	 *            String
	 * @param sCashAccCode
	 *            String
	 * @param sCuryCode
	 *            String
	 * @param sPortCode
	 *            String
	 * @param sAnalysisCode1
	 *            String
	 * @param sAnalysisCode2
	 *            String
	 * @param sAnalysisCode3
	 *            String
	 * @param iDsInd
	 *            int
	 * @throws YssException
	 *             MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A sj
	 */
	public void delete(String sNums, java.util.Date beginDate,
			java.util.Date endDate, String sTsfTypeCode,
			String sSubTsfTypeCode, String sCashAccCode, String sCuryCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, int iDsInd) throws YssException {

		delete(sNums, beginDate, endDate, sTsfTypeCode, sSubTsfTypeCode,
				sCashAccCode, sCuryCode, sPortCode, sAnalysisCode1,
				sAnalysisCode2, sAnalysisCode3, iDsInd, 0, "", "");
	}

	/**
	 * xuqiji MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A 重载删除方法
	 * 
	 * @param sNums
	 *            交易编号
	 * @param beginDate
	 *            业务起始日
	 * @param endDate
	 *            业务截止日
	 * @param sTsfTypeCode
	 *            调拨类型
	 * @param sSubTsfTypeCode
	 *            调拨子类型
	 * @param sCashAccCode
	 *            现金账户
	 * @param sCuryCode
	 *            币种
	 * @param sPortCode
	 *            组合代码
	 * @param sAnalysisCode1
	 *            分析代码1
	 * @param sAnalysisCode2
	 *            分析代码2
	 * @param sAnalysisCode3
	 *            分析代码3
	 * @param iDsInd
	 * @param iInOutType
	 *            方向
	 * @throws YssException
	 */
	public void delete(String sNums, java.util.Date beginDate,
			java.util.Date endDate, String sTsfTypeCode,
			String sSubTsfTypeCode, String sCashAccCode, String sCuryCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, int iDsInd, int iInOutType,
			String sRelaNums, String sRelaTypes) throws YssException {
		delete(sNums, beginDate, endDate, sTsfTypeCode, sSubTsfTypeCode,
				sCashAccCode, sCuryCode, sPortCode, sAnalysisCode1,
				sAnalysisCode2, sAnalysisCode3, iDsInd, 0, sRelaNums, sRelaTypes, " "); //------此BUG是由于添加了所属分类引起的 modify by wangzuochun 2011.02.21 BUG #1132 #1133 新股网上申购及中签返款业务处理多次，现金应收应付生成重复应收申购款 
	}

	/**
	 * @param sNums
	 *            交易编号
	 * @param beginDate
	 *            业务起始日
	 * @param endDate
	 *            业务截止日
	 * @param sTsfTypeCode
	 *            调拨类型
	 * @param sSubTsfTypeCode
	 *            调拨子类型
	 * @param sCashAccCode
	 *            现金账户
	 * @param sCuryCode
	 *            币种
	 * @param sPortCode
	 *            组合代码
	 * @param sAnalysisCode1
	 *            分析代码1
	 * @param sAnalysisCode2
	 *            分析代码2
	 * @param sAnalysisCode3
	 *            分析代码3
	 * @param iDsInd
	 * @param iInOutType
	 *            方向
	 * @param sAttrClsCode
	 *            所属分类 NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao
	 *            2010.11.22
	 * @throws YssException
	 */
	public void delete(String sNums, java.util.Date beginDate,
			java.util.Date endDate, String sTsfTypeCode,
			String sSubTsfTypeCode, String sCashAccCode, String sCuryCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, int iDsInd, int iInOutType,
			String sRelaNums, String sRelaTypes, String sAttrClsCode)
			throws YssException {
		String strSql = "";
		ResultSet rs = null; // 定义一个查询结果集 QDV4赢时胜（上海）2009年4月16日04_B MS00390
		// by leeyu 20090429
		try {
			// 在删除数据之前，将删除的编号取出来 QDV4赢时胜（上海）2009年4月16日04_B MS00390 by leeyu
			// 20090429
			strSql = "select FNum from "
					+ pub.yssGetTableName("Tb_Data_CashPayRec")
					+ this.buildWhereSql(sNums, beginDate, endDate,
							sTsfTypeCode, sSubTsfTypeCode, sCashAccCode,
							sCuryCode, sPortCode, sAnalysisCode1,
							sAnalysisCode2, sAnalysisCode3, iDsInd, iInOutType,
							sRelaNums, sRelaTypes, sAttrClsCode); // MS00014
																	// QDV4.1赢时胜（上海）2009年4月20日14_A
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				deleteNums += rs.getString("FNum") + ",";
			}
			// 之后再删除数据
			strSql = "delete from "
					+ pub.yssGetTableName("Tb_Data_CashPayRec")
					+ this.buildWhereSql(sNums, beginDate, endDate,
							sTsfTypeCode, sSubTsfTypeCode, sCashAccCode,
							sCuryCode, sPortCode, sAnalysisCode1,
							sAnalysisCode2, sAnalysisCode3, iDsInd, iInOutType,
							sRelaNums, sRelaTypes, sAttrClsCode); // MS00014
																	// QDV4.1赢时胜（上海）2009年4月20日14_A
			dbl.executeSql(strSql);
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs); // 添加关闭结果集的方法
			// QDV4赢时胜（上海）2009年4月16日04_B MS00390
			// by leeyu 20090429
		}
	}

	// /**
	// * 重载删除方法
	// * @param sNums 交易编号
	// * @param beginDate 业务起始日
	// * @param endDate 业务截止日
	// * @param sTsfTypeCode 调拨类型
	// * @param sSubTsfTypeCode 调拨子类型
	// * @param sCashAccCode 现金账户
	// * @param sCuryCode 币种
	// * @param sPortCode 组合代码
	// * @param sAnalysisCode1 分析代码1
	// * @param sAnalysisCode2 分析代码2
	// * @param sAnalysisCode3 分析代码3
	// * @param iDsInd 数据源
	// * @throws YssException
	// */
	// public void delete(String sNums, java.util.Date beginDate,
	// java.util.Date endDate,
	// String sTsfTypeCode, String sSubTsfTypeCode,
	// String sCashAccCode, String sCuryCode,
	// String sPortCode, String sAnalysisCode1,
	// String sAnalysisCode2, String sAnalysisCode3, int iDsInd) throws
	// YssException {
	// delete( sNums, beginDate,
	// endDate,
	// sTsfTypeCode, sSubTsfTypeCode,
	// sCashAccCode, sCuryCode,
	// sPortCode, sAnalysisCode1,
	// sAnalysisCode2, sAnalysisCode3, iDsInd,0);
	// }

	public void delete(String sNums) throws YssException {
		delete(sNums, null, null, "", "", "", "", "", "", "", "", -1, 0, "", ""); // MS00014
																					// QDV4.1赢时胜（上海）2009年4月20日14_A
	}

	public void insert() throws YssException {
		insert("", null, null, "", "", "", "", "", "", "", "", -1, false,
				false, false, 0, "", ""); // MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A
	}

	/*****
	 * xuqiji 20091204 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
	 * 
	 * @param sNums
	 * @param beginDate
	 * @param endDate
	 * @param sTsfTypeCode
	 * @param sSubTsfTypeCode
	 * @param sCashAccCode
	 * @param sCuryCode
	 * @param sPortCode
	 * @param sAnalysisCode1
	 * @param sAnalysisCode2
	 * @param sAnalysisCode3
	 * @param iDsInd
	 * @param bAutoDel
	 * @param bInsertZero
	 * @param bdigit4
	 * @param iInOutType
	 * @param sRelaNum
	 * @param sRelaType
	 * @throws YssException
	 */
	public void insert(String sNums, java.util.Date beginDate,
			java.util.Date endDate, String sTsfTypeCode,
			String sSubTsfTypeCode, String sCashAccCode, String sCuryCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, int iDsInd, boolean bAutoDel,
			boolean bInsertZero, boolean bdigit4, int iInOutType,
			String sRelaNum, String sRelaType) throws YssException {
		insert(sNums, beginDate, endDate, sTsfTypeCode, sSubTsfTypeCode,
				sCashAccCode, sCuryCode, sPortCode, sAnalysisCode1,
				sAnalysisCode2, sAnalysisCode3, iDsInd, bAutoDel, bInsertZero,
				bdigit4, iInOutType, sRelaNum, sRelaType, " ");
	}

	/**
	 * 重载插入方法
	 * 
	 * @param sNums
	 *            交易编号
	 * @param beginDate
	 *            业务起始日
	 * @param endDate
	 *            业务截止日
	 * @param sTsfTypeCode
	 *            调拨类型
	 * @param sSubTsfTypeCode
	 *            调拨子类型
	 * @param sCashAccCode
	 *            现金账户
	 * @param sCuryCode
	 *            币种
	 * @param sPortCode
	 *            组合代码
	 * @param sAnalysisCode1
	 *            分析代码1
	 * @param sAnalysisCode2
	 *            分析代码2
	 * @param sAnalysisCode3
	 *            分析代码3
	 * @param iDsInd
	 *            数据源
	 * @param bAutoDel
	 *            自动删除标识
	 * @param bInsertZero
	 * @param bdigit4
	 * @param iInOutType
	 *            方向
	 * @throws YssException
	 */
	public void insert(String sNums, java.util.Date beginDate,
			java.util.Date endDate, String sTsfTypeCode,
			String sSubTsfTypeCode, String sCashAccCode, String sCuryCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, int iDsInd, boolean bAutoDel,
			// MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A 添加了关联编号，关联编号类型
			boolean bInsertZero, boolean bdigit4, int iInOutType,
			String sRelaNum, String sRelaType, String sAttrClsCode)
	// ------------------------------------------------------------------------------------------------------------------------------------------------------
			throws YssException {
		synchronized (YssGlobal.objCashRecLock) {// 添加锁，将此部分锁起来，原因是防止生成重复的编号 by
													// leeyu 20100521 合并太平版本代码
			String strSql = "";
			CashPecPayBean cashpecpay = null;        
			//modified by liubo.Story #1757
	        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
	        //=================================
//	        PreparedStatement pst = null;
	        YssPreparedStatement yssPst = null;
	        //===============end==================
			//Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
			String sFNum = "";
			//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
			//HashMap htDiffDate = new HashMap(); // 存放不同日期的 Max FNum 值
			int i = 0;
			int iFNum = 0;
			try {

				// 2009.04.27 蒋锦 添加 为Tb_Data_SecRecPay加锁，避免多用户同时获取最大编号时出现编号重复
				// MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
				// edited by zhouxiang MS01301 新建买入定存业务，当天计提利息时会把该账户买入“所含利息”显示出来
				// 注释了加锁
				// dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_CashPayRec"));

				if (bAutoDel) {
					delete(sNums, beginDate, endDate, sTsfTypeCode,
							sSubTsfTypeCode, sCashAccCode, sCuryCode,
							sPortCode, sAnalysisCode1, sAnalysisCode2,
							sAnalysisCode3, iDsInd, iInOutType, sRelaNum,
							sRelaType, sAttrClsCode); // MS00014
														// QDV4.1赢时胜（上海）2009年4月20日14_A
				}

				strSql = "insert into "
						+ pub.yssGetTableName("Tb_Data_CashPayRec")
						+ "(FNum,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FTsfTypeCode"
						+ ",FSubTsfTypeCode,FCuryCode,FMoney,FBaseCuryRate,FBaseCuryMoney,FPortCuryRate,FPortCuryMoney"
						+ ",FDataSource,FStockInd"
						+
						// -------------------------------
						",FDesc"
						+
						// -------------------------------
						",FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FInOut"
						+
						// ---MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A--
						",FRelaNum,FRelaType"
						+ // 添加关联编号、编号类型
						// ------------------------------------------------
						// --- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao
						// 2010.11.22 ---//
						",FATTRCLSCODE"
						+
						// --- NO.125 用户需要对组合按资本类别进行子组合的分类 end
						// ------------------------------//
						//---edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B start---//
						",FDataOrigin)"//添加 FDataOrigin = 0 表示为自动生成
						+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)"; // MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A
						//---edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B start---// 
//				pst = conn.prepareStatement(strSql);
				yssPst = dbl.getYssPreparedStatement(strSql);

				for (i = 0; i < this.addList.size(); i++) {
					cashpecpay = (CashPecPayBean) addList.get(i);
					// 处理获取编号的问题,这里一天只获取一次编号 //合并太平版本代码
					// if (i == 0) {
					// sFNum = getNum(cashpecpay); //只取一次
					// htDiffDate.put(cashpecpay.getTradeDate(), sFNum);
					// }

					/**Start 20130819 modified by liubo.Bug #9099.QDV4鹏华基金2013年8月15日01_B
					 * 系统目前直接使用序列自动生成应收应付编号。因此用SRP开头的老式应收应付编号来做关联编号就不合适了
					 * 修改以前的逻辑，用新的应收应付编号作为关联编号*/
					
					sFNum = getKeyNum();	//直接从序列中获取应收应付编号，同时用这个编号作为关联编号
					
					if (YssGlobal.hmCashRecNums.get(cashpecpay.getTradeDate()) == null) {// 合并太平版本代码
						// 如果本次的日期与上一次的日期不同的话就得再取一次编号, by leeyu 080616
//						sFNum = getNum(cashpecpay);
						YssGlobal.hmCashRecNums.put(cashpecpay.getTradeDate(),
								sFNum);
					}
//					sFNum = (String) YssGlobal.hmCashRecNums.get(cashpecpay
//							.getTradeDate()); // 合并太平版本代码
					// 如果金额全部为零时 就不进行保存
					// 8-2
					if (cashpecpay.getMoney() == 0
							&& cashpecpay.getBaseCuryMoney() == 0
							&& cashpecpay.getPortCuryMoney() == 0
							&& !bInsertZero) {
						continue;
					}
					/*
					 * sFNum = "SRP" +
					 * YssFun.formatDatetime(cashpecpay.getTradeDate()).
					 * substring(0, 8) +
					 * dbFun.getNextInnerCode(pub.yssGetTableName(
					 * "Tb_Data_CashPayRec"), dbl.sqlRight("FNUM", 9),
					 * "000000001", " where FTransDate = " +
					 * dbl.sqlDate(cashpecpay.getTradeDate()));
					 */
//					if (sFNum.trim().length() > 0 && sFNum.length() > 11) {
//						iFNum = YssFun.toInt(YssFun.right(sFNum, 9)); // 取出后9位的长度
//						sFNum = YssFun.left(sFNum, 11); // 取出左边11位长度
//						iFNum++;
//						sFNum += YssFun.formatNumber(iFNum, "000000000");
//						YssGlobal.hmCashRecNums.put(cashpecpay.getTradeDate(),
//								sFNum);// 合并太平版本代码
//					}
					
					/**End 20130819 modified by liubo.Bug #9099.QDV4鹏华基金2013年8月15日01_B*/
					
					insertNum = sFNum;
					// ----sj modified 20090120 QDV4交银施罗德2009年01月4日02_B
					// bugId:MS00141
					// -----------------------------------------------------//
					if (null != cashpecpay.getTsfTypeCode()
							&& cashpecpay.getTsfTypeCode().equalsIgnoreCase(
									"02")
							&& cashpecpay.getRelaOrderNum().length() > 0) { // 若此调拨类型为收入类型的数据且有关联排序编号,则将其编号保留.以便在如现金支付时获取之.
					// this.inCometypeNum = insertNum;//保留单个编号，以便其他情况下获取
						if (null == this.relaOrderNum) { // 若关联编号的hashtable尚未建立，则建立。
							this.relaOrderNum = new Hashtable();
						}
						this.relaOrderNum.put(cashpecpay.getRelaOrderNum(),
								sFNum); // 以获取的应收应付的关联排序编号为key,放入应收应付编号。
					}
					// --------------------------------------------------------------------------------------------------------------------------//		
					//edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B 主键编号 通过 sequence 获取
					
					/**Start 20130819 modified by liubo.Bug #9099.QDV4鹏华基金2013年8月15日01_B
					 * 应收应付编号在For循环的最开始就已经生成了，存在sFNum中*/
//					yssPst.setString(1, getKeyNum());	
					yssPst.setString(1, sFNum);	
					/**End 20130819 modified by liubo.Bug #9099.QDV4鹏华基金2013年8月15日01_B*/
					
					yssPst.setDate(2, YssFun.toSqlDate(cashpecpay.getTradeDate()));
					yssPst.setString(3, cashpecpay.getPortCode());
					yssPst
							.setString(
									4,
									// fanghaoln 20100602 MS01155
									// QDV4国内（测试）2010年05月05日01_B
									(cashpecpay.getInvestManagerCode() != null && cashpecpay
											.getInvestManagerCode().length() > 0) ?
									// ---------------------------end-----------MS01155-------------------------------------------
									cashpecpay.getInvestManagerCode()
											: " "); // 彭彪20071018 无库存信息配置赋空值
					yssPst
							.setString(
									5,
									cashpecpay.getCategoryCode().length() != 0 ? cashpecpay
											.getCategoryCode()
											: " ");
					yssPst.setString(6, " ");
					yssPst.setString(7, cashpecpay.getCashAccCode());
					yssPst.setString(8, cashpecpay.getTsfTypeCode());
					yssPst.setString(9, cashpecpay.getSubTsfTypeCode());
					yssPst.setString(10, cashpecpay.getCuryCode());
					yssPst.setDouble(11, bdigit4 ? YssFun.roundIt(cashpecpay
							.getMoney(), 4) : YssFun.roundIt(cashpecpay
							.getMoney(), 2)); // 若需要保留4位小数，则保留。默认保留2位小数。sj edit
												// 20080626.
					yssPst.setDouble(12, YssFun.roundIt(cashpecpay
							.getBaseCuryRate(), 15)); // hxqdii
					yssPst.setDouble(13, bdigit4 ? YssFun.roundIt(cashpecpay
							.getBaseCuryMoney(), 4) : YssFun.roundIt(cashpecpay
							.getBaseCuryMoney(), 2));
					yssPst.setDouble(14, YssFun.roundIt(cashpecpay
							.getPortCuryRate(), 15)); // hxqdii
					yssPst.setDouble(15, bdigit4 ? YssFun.roundIt(cashpecpay
							.getPortCuryMoney(), 4) : YssFun.roundIt(cashpecpay
							.getPortCuryMoney(), 2));
					yssPst.setDouble(16, cashpecpay.getDataSource());
					yssPst.setDouble(17, cashpecpay.getStockInd());
					// ------------------------------------------
					yssPst.setString(18, cashpecpay.getDesc());
					// ------------------------------------------
					yssPst.setInt(19, cashpecpay.checkStateId); // fanghaoln
																// 20090625
																// MS00537
																// QDV4海富通2009年06月21日01_AB
																// 增加一个是否审核的功能
					yssPst.setString(20, pub.getUserCode());
					yssPst.setString(21, YssFun
							.formatDatetime(new java.util.Date()));
					yssPst.setString(22, pub.getUserCode());
					yssPst.setString(23, YssFun
							.formatDatetime(new java.util.Date()));
					yssPst.setInt(24, cashpecpay.getInOutType()); // 新增的字段
					// ----MS00014 QDV4.1赢时胜（上海）2009年4月20日14_A ------------
					yssPst.setString(25, cashpecpay.getRelaNum());
					yssPst.setString(26, cashpecpay.getRelaNumType());
					// --- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao
					// 2010.11.22 ---//
					yssPst.setString(27, cashpecpay.getStrAttrClsCode().trim()
							.length() == 0 ? " " : cashpecpay
							.getStrAttrClsCode());
					// --- NO.125 用户需要对组合按资本类别进行子组合的分类 end
					// ------------------------------//
					// ------------------------------------------------------------
					yssPst.executeUpdate();

				}
			} catch (Exception e) {
				throw new YssException("系统在保存现金应收应付数据时出现异常!" + "\n", e); // by
																			// 曹丞
																			// 2009.02.01
																			// 保存现金应收应付数据异常信息
																			// MS00004
																			// QDV4.1-2009.2.1_09A
			} finally {
				dbl.closeStatementFinal(yssPst);
			}
		}// 合并太平版本代码
	}

	/**
	 * 重载插入数据方法
	 * 
	 * @param sNums
	 *            交易编号
	 * @param beginDate
	 *            业务起始日
	 * @param endDate
	 *            业务截止日
	 * @param sTsfTypeCode
	 *            调拨类型
	 * @param sSubTsfTypeCode
	 *            调拨子类型
	 * @param sCashAccCode
	 *            现金账户
	 * @param sCuryCode
	 *            币种
	 * @param sPortCode
	 *            组合代码
	 * @param sAnalysisCode1
	 *            分析代码1
	 * @param sAnalysisCode2
	 *            分析代码2
	 * @param sAnalysisCode3
	 *            分析代码3
	 * @param iDsInd
	 *            数据源
	 * @param bAutoDel
	 *            自动删除标识
	 * @param bInsertZero
	 * @param bdigit4
	 * @throws YssException
	 */
	public void insert(String sNums, java.util.Date beginDate,
			java.util.Date endDate, String sTsfTypeCode,
			String sSubTsfTypeCode, String sCashAccCode, String sCuryCode,
			String sPortCode, String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, int iDsInd, boolean bAutoDel,
			boolean bInsertZero, boolean bdigit4) throws YssException {
		insert(sNums, beginDate, endDate, sTsfTypeCode, sSubTsfTypeCode,
				sCashAccCode, sCuryCode, sPortCode, sAnalysisCode1,
				sAnalysisCode2, sAnalysisCode3, iDsInd, bAutoDel, bInsertZero,
				bdigit4, 0, "", "");

	}

	// 添加获取最大编号的方法 by liyu 080330
	private String getNum(CashPecPayBean cashpecpay) throws YssException {
		String sFNum = "";
		try {
			sFNum = "SRP"
					+ YssFun.formatDatetime(cashpecpay.getTradeDate())
							.substring(0, 8)
					+ dbFun.getNextInnerCode(pub
							.yssGetTableName("Tb_Data_CashPayRec"), dbl
							.sqlRight("FNUM", 9), "000000001",
							" where FTransDate = "
									+ dbl.sqlDate(cashpecpay.getTradeDate()));

			return sFNum;
		} catch (Exception e) {
			throw new YssException("计算最大编号出错!" + "\n", e);
		}
	}
    
	/**
	 * add by songjie 2012.12.20 
	 * BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
	 * 获取主键编号
	 * @return
	 * @throws YssException
	 */
	public String getKeyNum() throws YssException{
		String num = "";
		String strSql = "";
		ResultSet rs = null;
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		int maxNum = 0;
		try{
			conn.setAutoCommit(false);
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_CashPayRec")){
				strSql = " select max(FNum) as FNum from " + pub.yssGetTableName("Tb_Data_CashPayRec") + 
				" where SUBSTR(FNum,0,3) <> 'SRP' and SUBSTR(FNum,0,3) <> 'CRP' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_CashPayRec " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
				dbl.executeSql(strSql);
			}
			
			strSql = " select trim(to_char(SEQ_" + pub.getPrefixTB() + 
			"_Data_CashPayRec.NextVal,'00000000000000000000')) as FNum from dual ";
			rs = dbl.openResultSet(strSql);
			if(rs.next()){
    			num = rs.getString("FNum");
    		}
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			
			return num;
		}catch(Exception e){
			throw new YssException("获取最大编号出错!\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
