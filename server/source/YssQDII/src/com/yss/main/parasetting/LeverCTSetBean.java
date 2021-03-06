package com.yss.main.parasetting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.yss.core.util.YssFun;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;

/** huhuichao 2013-05-16 STORY 3759 杠杠分级定期折算期间界面创建 */
public class LeverCTSetBean extends BaseDataSettingBean implements IDataSetting {
	private String strPortCode = "";// 投资组合代码
	private String strPortName = "";// 投资组合名称
	private String strStartDay;// 开始日期
	private String strEndDay;// 截止日期
	private String strDiscountDay;// 定期结算日期
	private String strOldDiscountDay;
	private String strOldStartDay;
	private String strOldEndDay;
	private String strOldPortCode = "";
	private String strIsOnlyColumns = "0"; // 在初始登陆时是否只显示列，不查询数据
	private LeverCTSetBean filterType;
	private String sRecycled = "";

	public String getStrPortCode() {
		return strPortCode;
	}

	public String getStrPortName() {
		return strPortName;
	}

	public String getStartDay() {
		return strStartDay;
	}

	public String getEndDay() {
		return strEndDay;
	}

	public String getDiscountDay() {
		return strDiscountDay;
	}

	public String getOldDiscountDay() {
		return strOldDiscountDay;
	}

	public String getOldStartDay() {
		return strOldStartDay;
	}

	public String getOldEndDay() {
		return strOldEndDay;
	}

	public String getStrOldPortCode() {
		return strOldPortCode;
	}

	public String getStrIsOnlyColumns() {
		return strIsOnlyColumns;
	}

	public void setStrPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}

	public void setStrPortName(String strPortName) {
		this.strPortName = strPortName;
	}

	public void setStartDay(String strstartDay) {
		this.strStartDay = strstartDay;
	}

	public void setEndDay(String strendDay) {
		this.strEndDay = strendDay;
	}

	public void setOldStartDay(String stroldStartDay) {
		this.strOldStartDay = stroldStartDay;
	}

	public void setOldEndDay(String stroldEndDay) {
		this.strOldEndDay = stroldEndDay;
	}

	public void setDiscountDay(String strdiscountDay) {
		this.strDiscountDay = strdiscountDay;
	}

	public void setOldDiscountDay(String stroldDiscountDay) {
		this.strOldDiscountDay = stroldDiscountDay;
	}

	public void setStrOldPortCode(String strOldPortCode) {
		this.strOldPortCode = strOldPortCode;
	}

	public void setStrIsOnlyColumns(String strIsOnlyColumns) {
		this.strIsOnlyColumns = strIsOnlyColumns;
	}

	/**
	 * huhuichao 2013-06-24 STORY 3759 返回sql语句
	 * 
	 * @return String
	 */
	public String returnSql(int num) throws YssException {
		String strSql = "";
		try {
			if (num == 1) {
				strSql = "select y.* from "
						+ "(select FPortCode from "
						+ pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD")
						+ " "
						+ "  group by FPortCode) x join"
						+ "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FPortName as FPortName"
						+ " from "
						+ pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD")
						+ " a "
						+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
						+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
						+ " left join (select FPortCode, FPortName from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						+ ") d on a.FPortCode = d.FPortCode"
						+ buildFilterSql()
						+ " order by FCheckState, FCreateTime desc, FCheckTime desc)"
						+ " y on x.FPortCode=y.FPortCode";
			}
			if (num == 2) {
				strSql = "select y.* from "
						+ "(select FPortCode from "
						+ pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD")
						+ " "
						+ " where FCheckState = 1 group by FPortCode) x join"
						+ "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FPortName as FPortName"
						+ " from "
						+ pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD")
						+ " a "
						+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
						+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
						+ " left join (select FPortCode, FPortName from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						+ ") d on a.FPortCode = d.FPortCode"
						+ buildFilterSql()
						+ " order by FCheckState, FCreateTime desc, FCheckTime desc)"
						+ " y on x.FPortCode=y.FPortCode";
			}
		} catch (Exception e) {
			throw new YssException("获取SQL语句出错！", e);
		}
		return strSql;
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 获取列表显示数据
	 * 
	 * @return String
	 */
	public String getListViewData1() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String strSql = "";
		ResultSet rs = null;
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try {
			sHeader = this.getListView1Headers();
			/**add---huhuichao 2013-6-24 STORY  3759  封装获取Sql的代码*/
			strSql = this.returnSql(1);
			/**end---huhuichao 2013-6-24 STORY  3759  封装获取Sql的代码*/
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols()))
						.append(YssCons.YSS_LINESPLITMARK);
				setResultSetAttr(rs);
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
					+ "\r\f" + this.getListView1ShowCols();
		} catch (Exception e) {
			throw new YssException("获取杠杆分级运作期信息出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() throws YssException {
		String sResult = "";
		if (this.filterType != null) {
			sResult = " where 1=1 ";
			if (this.filterType.strIsOnlyColumns != null
					&& this.filterType.strIsOnlyColumns.equals("1")) {
				sResult = sResult + " and 1 = 2 ";
				return sResult;
			}
			if (this.filterType.strPortCode != null
					&& this.filterType.strPortCode.length() != 0) {
				sResult = sResult + " and a.FPortCode like '%"
						+ filterType.strPortCode.replaceAll("'", "''") + "%'";
			}
		}
		return sResult;
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException,
			YssException {
		this.strPortCode = rs.getString("FPortCode") + "";
		this.strPortName = rs.getString("FPortName") + "";
		this.strStartDay = rs.getString("FStartDay") + "";
		this.strEndDay = rs.getString("FEndDay") + "";
		this.strDiscountDay = rs.getString("FDiscountDay") + "";
		super.setRecLog(rs);
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 获取表头为组合代码\t组合名称的已审核数据
	 * 
	 * @return String
	 */
	public String getListViewData2() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String strSql = "";
		ResultSet rs = null;
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try {
			sHeader = "组合代码\t组合名称";
			/**add---huhuichao 2013-6-24 STORY  3759  封装获取Sql的代码*/
			strSql = this.returnSql(2);
			/**end---huhuichao 2013-6-24 STORY  3759  封装获取Sql的代码*/
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append((rs.getString("FPortCode") + "").trim()).append(
						"\t");
				bufShow.append((rs.getString("FPortName") + "").trim()).append(
						YssCons.YSS_LINESPLITMARK);
				setResultSetAttr(rs);
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
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

		} catch (Exception e) {
			throw new YssException("获取杠杆分级运作期信息出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 getListViewData3
	 * 
	 * @return String
	 */
	public String getListViewData3() {
		return "";
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 getListViewData4
	 * 
	 * @return String
	 */
	public String getListViewData4() {
		return "";
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 增加
	 * 
	 * @return String
	 */
	public String addSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "insert into "
					+ pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD")
					+ "(FPortCode,FStartDay,FEndDay,FDiscountDAY,FCheckState,FCreator,FCreateTime,FCheckUser)"
					+ " values("
					+ dbl.sqlString(this.strPortCode)
					+ ", "
					+ "to_date("
					+ dbl.sqlString(this.strStartDay)
					+ ","
					+ "'yyyy-mm-dd')"
					+ ","
					+ "to_date("
					+ dbl.sqlString(this.strEndDay)
					+ ","
					+ "'yyyy-mm-dd')"
					+ ","
					+ "to_date("
					+ dbl.sqlString(this.strDiscountDay)
					+ ","
					+ "'yyyy-mm-dd')"
					+ ","
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ","
					+ dbl.sqlString(this.creatorCode)
					+ ", "
					+ dbl.sqlString(this.creatorTime)
					+ ","
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode)) + ")";
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("新增杠杆分级运作期信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return null;
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 检查数据是否合法
	 * 
	 * @param btOper
	 * @return byte
	 */
	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper, pub
				.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD"),
				"FPortCode,FStartDay,FEndDay,FDiscountDay", (this.strPortCode
						.length() == 0 ? " " : this.strPortCode)
						+ ","
						+ (this.strStartDay.length() == 0 ? " "
								: this.strStartDay)
						+ ","
						+ (this.strEndDay.length() == 0 ? " " : this.strEndDay)
						+ ","
						+ (this.strDiscountDay.length() == 0 ? " "
								: this.strDiscountDay), (this.strOldPortCode
						.length() == 0 ? " " : this.strOldPortCode)
						+ ","
						+ (this.strOldStartDay.length() == 0 ? " "
								: this.strOldStartDay)
						+ ","
						+ (this.strOldEndDay.length() == 0 ? " "
								: this.strOldEndDay)
						+ ","
						+ (this.strOldDiscountDay.length() == 0 ? " "
								: this.strOldDiscountDay));
	}

	/**
	 * huhuichao 2013-06-24 STORY 3759 返回sql语句
	 * 
	 * @return String
	 */
	public String backSql() throws YssException {
		String strSql = "";
		try {
			strSql = "update "
					+ pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date()) + "'"
					+ " where FPortCode = " + dbl.sqlString(this.strPortCode)
					+ " and FStartDay=" + dbl.sqlDate(this.strStartDay)
					+ " and FEndDay=" + dbl.sqlDate(this.strEndDay)
					+ " and FDiscountDay=" + dbl.sqlDate(this.strDiscountDay);
		} catch (Exception e) {
			throw new YssException("返回SQL语句出错", e);
		}
		return strSql;
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 还原回收站数据、审核、反审核
	 */
	public void checkSetting() throws YssException {
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
				arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					/**add---huhuichao 2013-6-24 STORY  3759  封装获取Sql的代码*/
					strSql = this.backSql();
					/**end---huhuichao 2013-6-24 STORY  3759  封装获取Sql的代码*/
					dbl.executeSql(strSql);
				}
			}
			// 如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
			else if (strOldPortCode != null
					&& !strOldPortCode.equalsIgnoreCase("")) {
				/**add---huhuichao 2013-6-24 STORY  3759  封装获取Sql的代码*/
				strSql = this.backSql();
				/**end---huhuichao 2013-6-24 STORY  3759  封装获取Sql的代码*/
				dbl.executeSql(strSql);
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核杠杆分级运作期信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 删除数据即放入回收站
	 */
	public void delSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			/**add---huhuichao 2013-6-24 STORY  3759  封装获取Sql的代码*/
			strSql = this.backSql();
			/**end---huhuichao 2013-6-24 STORY  3759  封装获取Sql的代码*/
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("刪除杠杆分级运作期信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 修改
	 * 
	 * @return String
	 */
	public String editSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "update "
					+ pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD")
					+ " set FPortCode = " + dbl.sqlString(this.strPortCode)
					+ ", FStartDay = " + "to_date("
					+ dbl.sqlString(this.strStartDay) + "," + "'yyyy-mm-dd')"
					+ ", FEndDay = " + "to_date("
					+ dbl.sqlString(this.strEndDay) + "," + "'yyyy-mm-dd')"
					+ ", FDiscountDay = " + "to_date("
					+ dbl.sqlString(this.strDiscountDay) + ","
					+ "'yyyy-mm-dd')" + ", FCheckUser = "
					+ dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date()) + "'"
					+ " where FPortCode = "
					+ dbl.sqlString(this.strOldPortCode) + " and FStartDay="
					+ "to_date(" + dbl.sqlString(this.strOldStartDay) + ","
					+ "'yyyy-mm-dd')" + " and FEndDay=" + "to_date("
					+ dbl.sqlString(this.strOldEndDay) + "," + "'yyyy-mm-dd')"
					+ " and FDiscountDay=" + "to_date("
					+ dbl.sqlString(this.strOldDiscountDay) + ","
					+ "'yyyy-mm-dd')";
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新杠杆分级运作期信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return null;
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 getAllSetting
	 * 
	 * @return String
	 */
	public String getAllSetting() {
		return "";
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 getSetting
	 * 
	 * @return IDataSetting
	 */
	public IDataSetting getSetting() {
		return null;
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 saveMutliSetting
	 * 
	 * @param sMutilRowStr
	 *            String
	 * @return String
	 */
	public String saveMutliSetting(String sMutilRowStr) {
		return "";
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 getTreeViewData1
	 * 
	 * @return String
	 */
	public String getTreeViewData1() {
		return "";
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 getTreeViewData2
	 * 
	 * @return String
	 */
	public String getTreeViewData2() {
		return "";
	}

	/**
	 * getTreeViewData3
	 * 
	 * @return String
	 */
	public String getTreeViewData3() {
		return "";
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 拼接字符串
	 * 
	 * @return String
	 */
	public String buildRowStr() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.strPortCode).append("\t");
		buf.append(this.strPortName).append("\t");
		buf.append(this.strStartDay).append("\t");
		buf.append(this.strEndDay).append("\t");
		buf.append(this.strDiscountDay).append("\t");
		buf.append(super.buildRecLog());
		return buf.toString();
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 getOperValue
	 * 
	 * @param sType
	 *            String
	 * @return String
	 */
	public String getOperValue(String sType) {
		return "";
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 解析字符串
	 * 
	 * @param sRowStr
	 *            String
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		String sTmpStr = "";
		try {
			if (sRowStr.trim().length() == 0) {
				return;
			}
			// <Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
			// 变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
			if (sRowStr.split("<Logging>").length >= 2) {
				this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
			}
			sRowStr = sRowStr.split("<Logging>")[0];
			if (sRowStr.indexOf("\r\t") >= 0) {
				sTmpStr = sRowStr.split("\r\t")[0];
			} else {
				sTmpStr = sRowStr;
			}
			sRecycled = sRowStr;
			reqAry = sTmpStr.split("\t");
			this.strPortCode = reqAry[0];
			this.strPortName = reqAry[1];
			if (!reqAry[2].equals(""))
				this.strStartDay = reqAry[2].substring(0, 10);
			if (!reqAry[3].equals(""))
				this.strEndDay = reqAry[3].substring(0, 10);
			if (!reqAry[4].equals(""))
				this.strDiscountDay = reqAry[4].substring(0, 10);
			this.checkStateId = Integer.parseInt(reqAry[5]);
			this.strOldPortCode = reqAry[6];
			this.strOldStartDay = reqAry[7].substring(0, 10);
			this.strOldEndDay = reqAry[8].substring(0, 10);
			this.strOldDiscountDay = reqAry[9].substring(0, 10);
			this.strIsOnlyColumns = reqAry[10];
			super.parseRecLog();
			if (sRowStr.indexOf("\r\t") >= 0) {
				if (this.filterType == null) {
					this.filterType = new LeverCTSetBean();
					this.filterType.setYssPub(pub);
				}
				this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
			}
		} catch (Exception e) {
			throw new YssException("解析杠杆分级运作期信息出错", e);
		}
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 获取修改前数据
	 * 
	 * @return String
	 */
	public String getBeforeEditData() throws YssException {
		LeverCTSetBean befLeverCTSetBean = new LeverCTSetBean();
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FPortName as FPortName"
					+ " from "
					+ pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD")
					+ " a "
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
					+ " left join (select FPortCode, FPortName from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ ") d on a.FPortCode = d.FPortCode"
					+ " where a.FPortCode="
					+ dbl.sqlString(this.strOldPortCode);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				befLeverCTSetBean.strPortCode = rs.getString("FPortCode") + "";
				befLeverCTSetBean.strPortName = rs.getString("FPortName") + "";
				befLeverCTSetBean.strStartDay = rs.getString("FStartDay") + "";
				befLeverCTSetBean.strEndDay = rs.getString("FEndDay") + "";
				befLeverCTSetBean.strDiscountDay = rs.getString("FDiscountDay");
			}
			return befLeverCTSetBean.buildRowStr();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	/**
	 * huhuichao 2013-05-16 STORY 3759 删除回收站的数，即从数据库彻底删除数据
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = "";
		String[] arrData = null;
		boolean bTrans = false; // 代表是否开始了事务
		// 获取一个连接
		Connection conn = dbl.loadConnection();
		try {
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != null && !sRecycled.equals("")) {
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
							+ pub.yssGetTableName("TB_TA_FIXEDDISCOUNTPERIOD")
							+ " where FPortCode = "
							+ dbl.sqlString(this.strPortCode)
							+ " and FStartDay=" + "to_date("
							+ dbl.sqlString(this.strStartDay) + ","
							+ "'yyyy-mm-dd')" + " and FEndDay=" + "to_date("
							+ dbl.sqlString(this.strEndDay) + ","
							+ "'yyyy-mm-dd')" + " and FDiscountDay="
							+ "to_date(" + dbl.sqlString(this.strDiscountDay)
							+ "," + "'yyyy-mm-dd')";
					// 执行sql语句
					dbl.executeSql(strSql);
				}
			}
			// sRecycled如果sRecycled为空，而StrOldIndexCode不为空，则按照StrOldIndexCode来执行sql语句
			else if (strOldPortCode != null && !strOldPortCode.equals("")) {
				strSql = "delete from " + pub.yssGetTableName("Tb_Para_Index")
						+ " where FPortCode = "
						+ dbl.sqlString(this.strOldPortCode)
						+ " and FStartDay=" + "to_date("
						+ dbl.sqlString(this.strStartDay) + ","
						+ "'yyyy-mm-dd')" + " and FEndDay=" + "to_date("
						+ dbl.sqlString(this.strEndDay) + "," + "'yyyy-mm-dd')"
						+ " and FDiscountDay=" + "to_date("
						+ dbl.sqlString(this.strDiscountDay) + ","
						+ "'yyyy-mm-dd')";
				dbl.executeSql(strSql);
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

	public String getListViewGroupData1() throws YssException {
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		return null;
	}

}
