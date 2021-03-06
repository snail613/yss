package com.yss.main.operdata;

import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class DataCreditLevelBean extends BaseDataSettingBean implements
		IDataSetting {
	private String securityCode = "";
	private String securityName = "";
	private String organCode = "";
	private String organName = "";
	private String creditLevelCode = "";
	private String creditLevelName = "";
	private String desc = "";
	private String oldSecurityCode = "";
	private Date levelDate = new java.util.Date();
	private Date oldLevelDate = new java.util.Date();
	private String oldOrganCode = "";
	private DataCreditLevelBean filterType = null;
	private String creditType = "";
	private String sRecycled = ""; // 保存未解析前的字符串 2008-5-19 单亮

	public String getSecurityName() {
		return securityName;
	}

	public String getDesc() {
		return desc;
	}

	public Date getLevelDate() {
		return levelDate;
	}

	public String getCreditLevelCode() {
		return creditLevelCode;
	}

	public String getCreditType() {
		return creditType;
	}

	public String getOldSecurityCode() {
		return oldSecurityCode;
	}

	public DataCreditLevelBean getFilterType() {
		return filterType;
	}

	public String getOrganCode() {
		return organCode;
	}

	public String getCreditLevelName() {
		return creditLevelName;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setOrganName(String organName) {
		this.organName = organName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setLevelDate(Date levelDate) {
		this.levelDate = levelDate;
	}

	public void setCreditLevelCode(String creditLevelCode) {
		this.creditLevelCode = creditLevelCode;
	}

	public void setCreditType(String creditType) {
		this.creditType = creditType;
	}

	public void setOldSecurityCode(String oldSecurityCode) {
		this.oldSecurityCode = oldSecurityCode;
	}

	public void setFilterType(DataCreditLevelBean filterType) {
		this.filterType = filterType;
	}

	public void setOrganCode(String organCode) {
		this.organCode = organCode;
	}

	public void setCreditLevelName(String creditLevelName) {
		this.creditLevelName = creditLevelName;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getOrganName() {
		return organName;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		String sTmpStr = "";
		try {
			if (sRowStr.trim().length() == 0) {
				return;
			}
			if (sRowStr.indexOf("\r\t") >= 0) {
				sTmpStr = sRowStr.split("\r\t")[0];
			} else {
				sTmpStr = sRowStr;
			}
			sRecycled = sRowStr; // 把未解析的字符串先赋给sRecycled
			reqAry = sTmpStr.split("\t");
			this.securityCode = reqAry[0];
			this.securityName = reqAry[1];
			this.creditType = reqAry[2];
			this.organCode = reqAry[3];
			this.organName = reqAry[4];
			this.creditLevelCode = reqAry[5];
			this.creditLevelName = reqAry[6];
			this.levelDate = YssFun.toDate(reqAry[7]);
			// ------ modify by nimengjing 2010.12.02 BUG #535
			// 指数行情设置界面描述字段中存在回车符时，清除/还原报错
			if (reqAry[8] != null) {
				if (reqAry[8].indexOf("【Enter】") >= 0) {
					this.desc = reqAry[8].replaceAll("【Enter】", "\r\n");
				} else {
					this.desc = reqAry[8];
				}
			}
			// ----------------- BUG #533 ----------------//
			this.oldSecurityCode = reqAry[9];
			this.checkStateId = Integer.parseInt(reqAry[10]);
			this.oldOrganCode = reqAry[11];
			this.oldLevelDate = YssFun.toDate(reqAry[12]);
			if (sRowStr.indexOf("\r\t") >= 0) {
				if (this.filterType == null) {
					this.filterType = new DataCreditLevelBean();
					this.filterType.setYssPub(pub);
				}
				if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			}

		} catch (Exception e) {
			throw new YssException("解析信用评级信息出错", e);
		}

	}

	public String getBeforeEditData() throws YssException {
		return "";
	}

	public String getOperValue(String sType) {
		return "";
	}

	public String addSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Data_CreditLevel")
					+ " (FSecurityCode,FCreditType,FOrganCode,FCreditLevel,"
					+ "FDesc,FCheckState,FCreator,FCreateTime,FCLevelDate) values("
					+ dbl.sqlString(this.securityCode) + " ,"
					+ dbl.sqlString(this.creditType) + " ,"
					+ dbl.sqlString(this.organCode) + " ,"
					+ dbl.sqlString(this.creditLevelCode) + " ,"
					+ dbl.sqlString(this.desc) + ", "
					+ (pub.getSysCheckState() ? "0" : "1") + " ,"
					+ dbl.sqlString(this.creatorCode + " ") + " ,"
					+ dbl.sqlString(this.creatorTime + " ") + ", "
					+ dbl.sqlDate(this.levelDate) + " )";
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return this.buildRowStr();
		} catch (Exception e) {
			throw new YssException("新增债券信用评级出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper, pub
				.yssGetTableName("Tb_Data_CreditLevel"),
				"FSecurityCode,FOrganCode,FCLevelDate", this.securityCode + ","
						+ this.organCode + ","
						+ YssFun.formatDate(this.levelDate, "yyyy-MM-dd"),
				this.oldSecurityCode + "," + this.oldOrganCode + ","
						+ YssFun.formatDate(this.oldLevelDate, "yyyy-MM-dd"));
	}

	/**
	 * 修改时间：2008年5月19号 修改人：单亮 原方法功能：只能处理期间连接的审核和未审核的单条信息。
	 * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
	 * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
	 * 
	 * @throws YssException
	 */
	public void checkSetting() throws YssException {
		// 修改前begin
		// String strSql = "";
		// boolean bTrans = false;
		// Connection conn = dbl.loadConnection();
		// try {
		// strSql = " update " + pub.yssGetTableName("Tb_Data_CreditLevel") +
		// " set FCheckState= " + this.checkStateId + ", " +
		// " FCreator='" + pub.getUserCode() + " ', " +
		// " FCreateTime='" + YssFun.formatDatetime(new java.util.Date()) +
		// " ' " +
		// " where FSecurityCode =" +
		// dbl.sqlString(this.oldSecurityCode) + " and FOrganCode=" +
		// dbl.sqlString(this.oldOrganCode) + " and FCLevelDate=" +
		// dbl.sqlDate(this.oldLevelDate);
		// conn.setAutoCommit(false);
		// bTrans = true;
		// dbl.executeSql(strSql);
		// conn.commit();
		// bTrans = false;
		// conn.setAutoCommit(true);
		// }
		// catch (Exception e) {
		// throw new YssException("审核债券信用评级出错", e);
		// }
		// finally {
		// dbl.endTransFinal(conn, bTrans);
		// }
		// end
		// 修改后的代码 begin
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();

		try {
			conn.setAutoCommit(false);
			bTrans = true;
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) {
				arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);

					strSql = " update "
							+ pub.yssGetTableName("Tb_Data_CreditLevel")
							+ " set FCheckState= " + this.checkStateId + ", "
							+ " FCreator='" + pub.getUserCode() + " ', "
							+ " FCreateTime='"
							+ YssFun.formatDatetime(new java.util.Date())
							+ " ' " + " where FSecurityCode ="
							+ dbl.sqlString(this.oldSecurityCode)
							+ " and FOrganCode="
							+ dbl.sqlString(this.oldOrganCode)
							+ " and FCLevelDate="
							+ dbl.sqlDate(this.oldLevelDate);

					dbl.executeSql(strSql);
				}
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核债券信用评级出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		// end

	}

	public void delSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "update " + pub.yssGetTableName("Tb_Data_CreditLevel")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = "
					+ dbl.sqlString(pub.getUserCode() + " ")
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FSecurityCode = "
					+ dbl.sqlString(this.oldSecurityCode) + " and FOrganCode="
					+ dbl.sqlString(this.oldOrganCode) + " and FCLevelDate="
					+ dbl.sqlDate(this.oldLevelDate);
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除债券信用评级出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	public String editSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "update "
					+ pub.yssGetTableName("Tb_Data_CreditLevel")
					+ " set "
					+ " FSecurityCode = "
					+ dbl.sqlString(this.securityCode)
					+ " ,"
					+ " FCreditType = "
					+ dbl.sqlString(this.creditType)
					+ " ,"
					+ " FOrganCode = "
					+ dbl.sqlString(this.organCode)
					+ " ,"
					+ " FCreditLevel = "
					+ dbl.sqlString(this.creditLevelCode)
					+ " ,"
					+ " FCheckState = "
					+ (pub.getSysCheckState() ? "0" : "1")
					+ " ,"
					+ " FDesc = "
					+ dbl.sqlString(this.desc)
					+ " ,"
					+ " FCLevelDate = "
					+ dbl.sqlDate(this.levelDate)
					+ " ,"
					+ " FCreator = '"
					+ (pub.getSysCheckState() ? " " : dbl
							.sqlString(this.creatorCode)
							+ " ")
					+ "' ,"
					+ " FCreateTime = "
					+ dbl.sqlString(this.creatorTime + " ")
					+ " ,"
					+ " FCheckUser = '"
					+ (pub.getSysCheckState() ? " " : dbl
							.sqlString(this.checkUserCode)
							+ " ") + "' ," + " FCheckTime = "
					+ dbl.sqlString(this.checkTime + " ")
					+ " where FSecurityCode ="
					+ dbl.sqlString(this.oldSecurityCode) + " and FOrganCode="
					+ dbl.sqlString(this.oldOrganCode) + " and FCLevelDate="
					+ dbl.sqlDate(this.oldLevelDate);
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return this.buildRowStr();
		} catch (Exception e) {
			throw new YssException("修改债券信用评级出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public String getAllSetting() throws YssException {
		return "";
	}

	public IDataSetting getSetting() throws YssException {
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		return "";
	}

	private String buildFilterSql() throws YssException {
		String strSql = "";
		if (this.filterType != null) {
			strSql = " where 1=1 ";
			if (this.filterType.securityCode != null
					&& this.filterType.securityCode.length() != 0) {
				strSql += " and a.FSecurityCode like '"
						+ this.filterType.securityCode.replaceAll("'", "''")
						+ "%'";
			}
			if (this.filterType.organCode != null
					&& this.filterType.organCode.length() != 0) {
				strSql += " and a.FOrganCode like '"
						+ this.filterType.organCode.replaceAll("'", "''")
						+ "%'";
			}
			if (this.filterType.creditLevelCode != null
					&& this.filterType.creditLevelCode.length() != 0) {
				strSql += " and a.FCreditLevel like '"
						+ this.filterType.creditLevelCode.replaceAll("'", "''")
						+ "%'";
			}
			if (this.filterType.desc != null
					&& this.filterType.desc.length() != 0) {
				strSql += " and a.FDesc like '"
						+ this.filterType.desc.replaceAll("'", "''") + "%'";
			}
			if (!this.filterType.levelDate.equals(YssFun.toDate("9998-12-31"))) {
				strSql += " and a.FCLevelDate = "
						+ dbl.sqlDate(filterType.levelDate);
			}

		} else {
			strSql += " where 1=1 ";
		}

		return strSql;
	}

	/*
	 * public String builderListViewData(String strSql) throws YssException {
	 * String sHeader = ""; String sShowDataStr = ""; String sAllDataStr = "";
	 * StringBuffer bufShow = new StringBuffer(); StringBuffer bufAll = new
	 * StringBuffer(); ResultSet rs = null; try { sHeader =
	 * this.getListView1Headers(); rs = dbl.openResultSet(strSql); while
	 * (rs.next()) { bufShow.append(super.buildRowShowStr(rs,
	 * this.getListView1ShowCols())). append(YssCons.YSS_LINESPLITMARK);
	 * 
	 * setSecurityAttr(rs);
	 * bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK); } if
	 * (bufShow.toString().length() > 2) { sShowDataStr =
	 * bufShow.toString().substring(0, bufShow.toString().length() - 2); }
	 * 
	 * if (bufAll.toString().length() > 2) { sAllDataStr =
	 * bufAll.toString().substring(0, bufAll.toString().length() - 2); }
	 * 
	 * return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
	 * this.getListView1ShowCols(); } catch (Exception e) { throw new
	 * YssException("获取债券信用评级设置出错", e); } finally { dbl.closeResultSetFinal(rs);
	 * }
	 * 
	 * }
	 */

	public void setSecurityAttr(ResultSet rs) throws SQLException {
		this.securityCode = rs.getString("FSecurityCode") + "";
		this.securityName = rs.getString("FSecurityName") + "";
		this.creditType = rs.getString("FCreditType") + "";
		this.organCode = rs.getString("FOrganCode") + "";
		this.organName = rs.getString("FOrganName") + "";
		this.creditLevelCode = rs.getString("FCreditLevel") + "";
		this.creditLevelName = rs.getString("FCreditLevelName") + "";
		this.levelDate = rs.getDate("FCLevelDate");
		this.desc = rs.getString("FDesc") + "";
		super.setRecLog(rs);
	}

	public String buildRowStr() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.securityCode).append("\t");
		buf.append(this.securityName).append("\t");
		buf.append(this.organCode).append("\t");
		buf.append(this.organName).append("\t");
		buf.append(this.creditLevelCode).append("\t");
		buf.append(this.creditLevelName).append("\t");
		buf.append(this.levelDate).append("\t");
		buf.append(this.desc).append("\t");
		buf.append(super.buildRecLog());
		return buf.toString();
	}

	public String getListViewData1() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sVocStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		String strSql = "";
		try {
			sHeader = this.getListView1Headers();
			// fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A 20100708
			// 优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
			// add by yangheng MS01310 分页查询
			if (this.filterType != null
					&& this.filterType.levelDate.equals(YssFun
							.toDate("1900-01-01"))) {
				return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
						+ "\r\f" + this.getListView1ShowCols() + "\r\f"
						+ yssPageInationBean.buildRowStr();// QDV4赢时胜上海2010年03月16日06_B
															// MS00884 by xuqiji
			}
			// --------------------------------------end
			// MS01310--------------------------------------------------------
			strSql = "select a.*,b.FAffCorpName as FOrganName , "
					+ "c.FSecurityName as FSecurityName , "
					+ "d.FCreditLevelName as FCreditLevelName , "
					+ "e.FUserName as FCreatorName, f.FUserName as FCheckUserName "
					+ " from "
					+ pub.yssGetTableName("Tb_Data_CreditLevel")
					+ " a"
					+ " left join ( select FAffCorpCode,FAffCorpName from "
					+
					// -----modify by wangzuochun 2009.09.10 MS00678
					// [债券信用评级]界面新增数据时没有对机构信息的状态进行筛选后增加，导致有异常记录
					// QDV4赢时胜上海2009年9月03日01_B
					// pub.yssGetTableName("Tb_Para_AffiliatedCorp") + ") b" +
					pub.yssGetTableName("Tb_Para_AffiliatedCorp")
					+ " t where FStartDate = (select max(FStartDate) from "
					+ pub.yssGetTableName("Tb_Para_AffiliatedCorp")
					+ " where t.FAffCorpCode = FAffCorpCode)) b"
					+
					// -------------------------------------------END
					// MS00678---------------------------------------------//
					" on a.FOrganCode = b.FAffCorpCode "
					+ " left join (select q.FSecurityCode as FSecurityCode,q.FSecurityName as FSecurityName from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " q join "
					+ "(select FSecurityCode,max(FStartDate) as FStartDate from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1 group by FSecurityCode) r "
					+ " on q.FSecurityCode = r.FSecurityCode and q.FStartDate = r.FStartDate) c "
					+ " on a.FSecurityCode = c.FSecurityCode"
					+ " left join (select FCreditLevelCode ,FCreditLevelName from "
					+ pub.yssGetTableName("Tb_Para_CreditLevelDict")
					+ ") d "
					+ " on a.FCreditLevel = d.FCreditLevelCode "
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCreator = e.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) f on a.FCheckUser = f.FUserCode"
					+ buildFilterSql() +
					// 2008-5-19 单亮
					// 修改前
					// " and a.FCheckstate <>2 order by c.FSecurityCode";
					"  order by c.FSecurityCode"; // 修改后的
			// QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("CreditLevel");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
			while (rs.next()) {
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols()))
						.append(YssCons.YSS_LINESPLITMARK);

				setSecurityAttr(rs);
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);

			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f"
					+ yssPageInationBean.buildRowStr();// QDV4赢时胜上海2010年03月16日06_B
														// MS00884 by xuqiji
		} catch (Exception e) {
			throw new YssException("获取债券信用评级出错：" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(dbl.getProcStmt());
		}
	}

	public String getListViewData2() throws YssException {
		return "";
	}

	public String getListViewData3() {
		return "";
	}

	public String getListViewData4() {
		return "";
	}

	public String getTreeViewData1() {
		return "";
	}

	public String getTreeViewData2() {
		return "";
	}

	public String getTreeViewData3() {
		return "";
	}

	/**
	 * 2008-5-19 单亮 删除回收站的数据，即彻底从数据库删除数据
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		// 获取一个连接
		Connection conn = dbl.loadConnection();
		try {
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != "" && sRecycled != null) {
				// 根据规定的符号，把多个sql语句分别放入数组
				arrData = sRecycled.split("\r\n");
				conn.setAutoCommit(false);
				bTrans = true;
				// 循环执行这些删除语句
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Data_CreditLevel")
							+ " where FSecurityCode = "
							+ dbl.sqlString(this.securityCode)
							+ " and FOrganCode="
							+ dbl.sqlString(this.organCode)
							+ " and FCLevelDate=" + dbl.sqlDate(this.levelDate);

					// 执行sql语句
					dbl.executeSql(strSql);
				}
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("清除数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	public String getTreeViewGroupData1() throws YssException {
		return "";
	}

	public String getTreeViewGroupData2() throws YssException {
		return "";
	}

	public String getTreeViewGroupData3() throws YssException {
		return "";
	}

	public String getListViewGroupData1() throws YssException {
		return "";
	}

	public String getListViewGroupData2() throws YssException {
		return "";
	}

	public String getListViewGroupData3() throws YssException {
		return "";
	}

	public String getListViewGroupData4() throws YssException {
		return "";
	}

	public String getListViewGroupData5() throws YssException {
		return "";
	}

}
