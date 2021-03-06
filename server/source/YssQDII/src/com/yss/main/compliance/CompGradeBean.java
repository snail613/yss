package com.yss.main.compliance;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class CompGradeBean extends BaseDataSettingBean implements IDataSetting {

	private String strGradeCode; // 等级代码
	private String strGradeName; // 等级名称
	private String strStartDate; // 起用日期
	private String strWarnColor; // 警告颜色
	private String strForbidColo; // 禁止颜色
	private String strPassColor; // 正常颜色
	private String strGradeDesc; // 描述
	private String hRecycled = ""; // 回收站 MS01263 QDV4赢时胜(测试)2010年6月2日4_B
									// 2010-06-22 fanghaoln
	private String strOldGradeCode;
	private String strOldStartDate;
	private CompGradeBean filterType;

	public CompGradeBean() {
	}

	/**
	 * 返回属性
	 * 
	 * @return String
	 */
	public String buildRowStr() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.strGradeCode.trim()).append("\t");
		buffer.append(this.strGradeName.trim()).append("\t");
		buffer.append(this.strStartDate.trim()).append("\t");
		buffer.append(this.strWarnColor.trim()).append("\t");
		buffer.append(this.strForbidColo.trim()).append("\t");
		buffer.append(this.strPassColor.trim()).append("\t");
		buffer.append(this.strGradeDesc.trim()).append("\t");
		buffer.append(super.buildRecLog());
		return buffer.toString();
	}

	/**
	 * 数据验证
	 * 
	 * @param btOper
	 *            byte
	 * @throws YssException
	 */
	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Comp_Grade"),
				//edit by songjie 2011.03.15 不以启用日期查询主键数据
				"FGradeCode", this.strGradeCode, this.strOldGradeCode);
	}

	public String getAllSetting() {
		return "";
	}

	/**
	 * 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() {
		String strResult = "";
		if (this.filterType != null) {
			strResult = " where 1=1 ";
			if (this.filterType.strGradeCode.length() != 0) {
				strResult = strResult + " and a.FGradeCode like '"
						+ filterType.strGradeCode.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.strGradeName.length() != 0) {
				strResult = strResult + " and a.FGradeName like '"
						+ filterType.strGradeName.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.strWarnColor.length() != 0) {
				strResult = strResult + " and a.FWarnColor ="
						+ filterType.strWarnColor;
			}
			if (this.filterType.strForbidColo.length() != 0) {
				strResult = strResult + " and a.FForbidColor ="
						+ filterType.strForbidColo;
			}
			if (this.filterType.strPassColor.length() != 0) {
				strResult = strResult + " and a.FPassColor ="
						+ filterType.strPassColor;
			}
			if (this.filterType.strStartDate.length() != 0
					&& !this.filterType.strStartDate.equals("9998-12-31") 
					//edit by songjie 2011.03.15 启用日期默认为1900-01-01
					&& !this.filterType.strStartDate.equals("1900-01-01")) {
				strResult = strResult + " and a.FStartDate <= "
						+ dbl.sqlDate(filterType.strStartDate);
			}
		}
		return strResult;
	}

	public String builderListViewData(String strSql) throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		try {
			sHeader = this.getListView1Headers();
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols()))
						.append(YssCons.YSS_LINESPLITMARK);

				setSectorAttr(rs);
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
			throw new YssException("获取监控等级信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * getListViewData1 获取监控信息数据
	 * 
	 * @return String
	 */
	public String getListViewData1() throws YssException {
		String strSql = "";
		strSql = "select y.* from "
			    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//				+ "(select FGradeCode,FCheckState,max(FStartDate) as FStartDate from "
//				+ pub.yssGetTableName("Tb_Comp_Grade")
//				+ " "
//				+ " where FStartDate <= "
//				+ dbl.sqlDate(new java.util.Date())
//				+ "and FCheckState <> 3 group by FGradeCode,FCheckState) x join"
			    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
				+ // edited by zhouxiang MS01356 监控范本删除数据后在回收站中无法显示被删除的数据
				" (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from "
				+ pub.yssGetTableName("Tb_Comp_Grade")
				+ " a "
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
				+ buildFilterSql()
				+ ") y "//edit by songjie 2011.03.15 不以最大的启用日期查询数据
				+ " order by y.FCheckState, y.FCreateTime desc";
		return this.builderListViewData(strSql);
	}

	/**
	 * getListViewData4 获取监控信息数据
	 * 
	 * @return String
	 */
	public String getListViewData4() throws YssException {
		String strSql = "";

		strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from "
				+ pub.yssGetTableName("Tb_Comp_Grade")
				+ " a "
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
				+ buildFilterSql()
				+ " order by  a.FCheckState, a.FCreateTime desc";
		return this.builderListViewData(strSql);
	}

	/**
	 * getListViewData2 获取监控信息数据
	 * 
	 * @return String
	 */
	public String getListViewData2() throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String strSql = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		try {
			sHeader = "等级代码\t等级名称\t等级颜色";//edit by songjie 2011.03.15 不显示启用日期
			strSql = "select y.* from "
				    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//					+ "(select FGradeCode,max(FStartDate) as FStartDate from "
//					+ pub.yssGetTableName("Tb_Comp_Grade")
//					+ " "
//					+ " where FStartDate <= "
//					+ dbl.sqlDate(new java.util.Date())
//					+ "and FCheckState = 1 group by FGradeCode) x join"
				    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
					+ " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from "
					+ pub.yssGetTableName("Tb_Comp_Grade")
					+ " a "
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
					+ ") y "//edit by songjie 2011.03.15 不以最大的启用日期查询数据
					+ " order by y.FCheckState, y.FCreateTime desc";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append((rs.getString("FGradeCode") + "").trim())
						.append("\t");
				bufShow.append((rs.getString("FGradeName") + "").trim())
						.append("\t");
				bufShow.append((rs.getString("FWarnColor") + "").trim())
						.append("\t");
				bufShow.append((rs.getString("FForbidColor") + "").trim())
						.append("\t");
				bufShow.append((rs.getString("FPassColor") + "").trim())
						.append(YssCons.YSS_LINESPLITMARK);//edit by songjie 2011.03.15 不显示启用日期
				//----delete by songjie 2011.03.15 不显示启用日期----//
//				bufShow.append(
//						(YssFun.formatDate(rs.getDate("FStartDate")) + "")
//								.trim()).append(YssCons.YSS_LINESPLITMARK);
				//----delete by songjie 2011.03.15 不显示启用日期----//
				setSectorAttr(rs);
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
			throw new YssException("获取监控信息数据", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	/**
	 * getListViewData3
	 * 
	 * @return String
	 */
	public String getListViewData3() {
		return "";
	}

	/**
	 * getSetting
	 * 
	 * @return IParaSetting
	 */
	public IDataSetting getSetting() {
		return null;
	}

	/**
	 * getTreeViewData1
	 * 
	 * @return String
	 */
	public String getTreeViewData1() {
		return "";
	}

	/**
	 * getTreeViewData2
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
	 * parseRowStr
	 * 
	 * @param sRowStr
	 *            String
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
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
			reqAry = sTmpStr.split("\t");
			this.hRecycled = sRowStr;// MS01263 QDV4赢时胜(测试)2010年6月2日4_B
										// 2010-06-22 fanghaoln
			this.strGradeCode = reqAry[0];
			this.strGradeName = reqAry[1];
			this.strStartDate = reqAry[2];
			this.strWarnColor = reqAry[3];
			this.strForbidColo = reqAry[4];
			this.strPassColor = reqAry[5];
			//----edit by songjie 2011.03.19 BUG:1467 QDV4赢时胜(测试)2011年03月15日02_B----//
			if (reqAry[6].indexOf("【Enter】") > -1) {
				this.strGradeDesc = reqAry[6].replaceAll("【Enter】", "\r\n");
			} else {
				this.strGradeDesc = reqAry[6];
			}
			//----edit by songjie 2011.03.19 BUG:1467 QDV4赢时胜(测试)2011年03月15日02_B----//
			this.checkStateId = Integer.parseInt(reqAry[7]);
			this.strOldGradeCode = reqAry[8];
			this.strOldStartDate = reqAry[9];
			super.parseRecLog();
			if (sRowStr.indexOf("\r\t") >= 0) {
				if (this.filterType == null) {
					this.filterType = new CompGradeBean();
					this.filterType.setYssPub(pub);
				}
				this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
			}
		} catch (Exception e) {
			throw new YssException("解析监控等级设置请求信息出错", e);
		}
	}

	/**
	 * saveMutliSetting
	 * 
	 * @param sMutilRowStr
	 *            String
	 * @return String
	 */
	public String saveMutliSetting(String sMutilRowStr) {
		return "";
	}

	/**
	 * saveSetting
	 * 
	 * @param btOper
	 *            byte
	 * @throws YssException
	 */
	/*
	 * public void saveSetting(byte btOper) throws YssException { Connection
	 * conn = dbl.loadConnection(); boolean bTrans = false; String strSql = "";
	 * try { if (btOper == YssCons.OP_ADD) { strSql = "insert into " +
	 * pub.yssGetTableName("Tb_Comp_Grade") +
	 * "(FGradeCode, FGradeName,FGradeColor, FDesc, " +
	 * " FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) " + " values("
	 * + dbl.sqlString(this.strGradeCode) + "," +
	 * dbl.sqlString(this.strGradeName) + "," + YssFun.toInt(this.strGradeColor)
	 * + "," + dbl.sqlString(this.strGradeDesc) + "," +
	 * dbl.sqlDate(this.strStartDate) + "," + (pub.getSysCheckState() ? "0" :
	 * "1") + "," + dbl.sqlString(this.creatorCode) + ", " +
	 * dbl.sqlString(this.creatorTime) + "," + (pub.getSysCheckState() ? "' '" :
	 * dbl.sqlString(this.creatorCode)) + ")"; } else if (btOper ==
	 * YssCons.OP_EDIT) { strSql = "update " +
	 * pub.yssGetTableName("Tb_Comp_Grade") + " set FGradeCode = " +
	 * dbl.sqlString(this.strGradeCode) + ", FGradeName = " +
	 * dbl.sqlString(this.strGradeName) + ",FDesc = " +
	 * dbl.sqlString(this.strGradeDesc) + ",FGradeColor = " +
	 * YssFun.toInt(this.strGradeColor) + ",FCheckState = " +
	 * (pub.getSysCheckState() ? "0" : "1") + ",FStartDate = " +
	 * dbl.sqlDate(this.strStartDate) + ",FCreator = " +
	 * dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
	 * dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
	 * (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
	 * " where FGradeCode = " + dbl.sqlString(this.strOldGradeCode) +
	 * " and FStartDate=" + dbl.sqlDate(this.strOldStartDate); } else if (btOper
	 * == YssCons.OP_DEL) { strSql = "update " +
	 * pub.yssGetTableName("Tb_Comp_Grade") + " set FCheckState = " +
	 * this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
	 * + ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'"
	 * + " where FGradeCode = " + dbl.sqlString(this.strGradeCode) +
	 * " and FStartDate=" + dbl.sqlDate(this.strStartDate);
	 * 
	 * } else if (btOper == YssCons.OP_AUDIT) { strSql = "update " +
	 * pub.yssGetTableName("Tb_Comp_Grade") + " set FCheckState = " +
	 * this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
	 * + ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'"
	 * + " where FGradeCode = " + dbl.sqlString(this.strGradeCode) +
	 * " and FStartDate=" + dbl.sqlDate(this.strStartDate); }
	 * conn.setAutoCommit(false); bTrans = true; dbl.executeSql(strSql);
	 * conn.commit(); bTrans = false; conn.setAutoCommit(true); } catch
	 * (Exception e) { throw new YssException("设置监控等级信息出错！", e); } finally {
	 * dbl.endTransFinal(conn, bTrans); } }
	 */
	public void setSectorAttr(ResultSet rs) throws SQLException {
		this.strGradeCode = rs.getString("FGradeCode") + "";
		this.strGradeName = rs.getString("FGradeName") + "";
		this.strWarnColor = rs.getString("FWarnColor") + "";
		this.strForbidColo = rs.getString("FForbidColor") + "";
		this.strPassColor = rs.getString("FPassColor") + "";
		this.strStartDate = YssFun.formatDate(rs.getDate("FStartDate"));
		this.strGradeDesc = rs.getString("FDesc") + "";
		super.setRecLog(rs);
	}

	/**
	 * addSetting
	 * 
	 * @return String
	 */

	public String addSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Comp_Grade")
					+ "(FGradeCode, FGradeName,FWarnColor,FForbidColor,FPassColor, FDesc, "
					+ " FStartDate,FCheckState,FCreator,FCreateTime,FCheckUser) "
					+ " values("
					+ dbl.sqlString(this.strGradeCode)
					+ ","
					+ dbl.sqlString(this.strGradeName)
					+ ","
					+ YssFun.toInt(this.strWarnColor)
					+ ","
					+ YssFun.toInt(this.strForbidColo)
					+ ","
					+ YssFun.toInt(this.strPassColor)
					+ ","
					+ dbl.sqlString(this.strGradeDesc)
					+ ","
					+ dbl.sqlDate(this.strStartDate)
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
			throw new YssException("增加监控等级设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return null;

	}

	/**
	 * checkSetting
	 */

	public void checkSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		String[] arrData = null;
		Connection conn = dbl.loadConnection();
		try {
			//edited by nimengjing 2010 11 25 bug#474 在回收站中选择多条数据，不能一次全部还原
			if (hRecycled != null && hRecycled.length() > 0) {
				arrData = hRecycled.split("\r\n");
			}
			
			//---add by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
			if(arrData == null){
				return;
			}
			//---add by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
			
			for (int i = 0; i < arrData.length; i++) {
				this.parseRowStr(arrData[i]);
			//----------------end bug#474--------------------------------------------------	
				strSql = "update " + pub.yssGetTableName("Tb_Comp_Grade")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date()) + "'"
						+ " where FGradeCode = "
						+ dbl.sqlString(this.strGradeCode) + " and FStartDate="
						+ dbl.sqlDate(this.strStartDate);

				conn.setAutoCommit(false);
				bTrans = true;
				dbl.executeSql(strSql);
			}

			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核监控等级设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	/**
	 * delSetting
	 */

	public void delSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			strSql = "update " + pub.yssGetTableName("Tb_Comp_Grade")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date()) + "'"
					+ " where FGradeCode = " + dbl.sqlString(this.strGradeCode)
					+ " and FStartDate=" + dbl.sqlDate(this.strStartDate);

			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除监控等级设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * editSetting
	 * 
	 * @return String
	 */

	public String editSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {

			strSql = "update "
					+ pub.yssGetTableName("Tb_Comp_Grade")
					+ " set FGradeCode = "
					+ dbl.sqlString(this.strGradeCode)
					+ ", FGradeName = "
					+ dbl.sqlString(this.strGradeName)
					+ ",FDesc = "
					+ dbl.sqlString(this.strGradeDesc)
					+ ",FWarnColor = "
					+ YssFun.toInt(this.strWarnColor)
					+ ", FForbidColor = "
					+ YssFun.toInt(this.strForbidColo)
					+ ", FPassColor = "
					+ YssFun.toInt(this.strPassColor)
					+ ",FCheckState = "
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ",FStartDate = "
					+ dbl.sqlDate(this.strStartDate)
					+ ",FCreator = "
					+ dbl.sqlString(this.creatorCode)
					+ ",FCreateTime = "
					+ dbl.sqlString(this.creatorTime)
					+ ",FCheckUser = "
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode))
					+ " where FGradeCode = "
					+ dbl.sqlString(this.strOldGradeCode) + " and FStartDate="
					+ dbl.sqlDate(this.strOldStartDate);

			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("修改监控等级设置信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return null;

	}

	/**
	 * getOperValue
	 * 
	 * @param sType
	 *            String
	 * @return String
	 */

	public String getOperValue(String sType) {
		return "";
	}

	/**
	 * getBeforeEditData
	 * 
	 * @return String
	 */
	public String getBeforeEditData() throws YssException {
		CompGradeBean befEditBean = new CompGradeBean();
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "select y.* from "
				    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//					+ "(select FGradeCode,FCheckState,max(FStartDate) as FStartDate from "
//					+ pub.yssGetTableName("Tb_Comp_Grade")
//					+ " "
//					+ " where FStartDate <= "
//					+ dbl.sqlDate(new java.util.Date())
//					+ "and FCheckState <> 2 group by FGradeCode,FCheckState) x join"
				    //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
					+ " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from "
					//edit by songjie 2011.03.15 不以最大的启用日期查询数据
					+ " (select * from "+ pub.yssGetTableName("Tb_Comp_Grade") + " where FCheckState <> 2)"
					+ " a "
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
					+ " where  FGradeCode ="
					+ dbl.sqlString(this.strOldGradeCode)
					+ ") y "//edit by songjie 2011.03.15 不以最大的启用日期查询数据
					+ " order by y.FCheckState, y.FCreateTime desc";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				befEditBean.strGradeCode = rs.getString("FGradeCode") + "";
				befEditBean.strGradeName = rs.getString("FGradeName") + "";
				befEditBean.strWarnColor = rs.getString("FWarnColor") + "";
				befEditBean.strForbidColo = rs.getString("FForbidColor") + "";
				befEditBean.strPassColor = rs.getString("FPassColor") + "";
				befEditBean.strStartDate = YssFun.formatDate(rs
						.getDate("FStartDate"));
				befEditBean.strGradeDesc = rs.getString("FDesc") + "";

			}
			return befEditBean.buildRowStr();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs); // 关闭游标资源 modify by sunkey 20090604
											// MS00472:QDV4上海2009年6月02日01_B
		}

	}

	/**
	 * deleteRecycleData
	 */
	/**
	 * bug MS01263 QDV4赢时胜(测试)2010年6月2日4_B 2010-06-22 fanghaoln 修改人：方浩
	 * 回收站的删除功能调用此方法deleteRecycleData() 从数据库删除数据，即彻底删除数据,可以多个一删除
	 * 
	 * @throws YssException
	 *             Time: 2010-06-22
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = ""; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		try {
			// 判断回收站是否为空，如果不为空则根据解析的字符串执行SQL语句删除数据
			if (hRecycled != null && hRecycled.length() != 0) {
				// 按照规定的解析规则对数据进行解析
				arrData = hRecycled.split("\r\n");
				conn.setAutoCommit(false);
				bTrans = true;
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Comp_Grade")
							+ " where FGradeCode = "
							+ dbl.sqlString(this.strGradeCode)
							+ " and FStartDate="
							+ dbl.sqlDate(this.strStartDate);
					/*
					 * strChildSql = "delete from " +
					 * pub.yssGetTableName("Tb_Base_ChildHoliday") +
					 * " where FHolidaysCode= " +
					 * dbl.sqlString(this.strHolidaysCode);
					 */
					dbl.executeSql(strSql);
					// dbl.executeSql(strChildSql);
					// 从数据库中彻底删除节假日子表对应数据 edit by jc
					// strSql = "delete from " +
					// pub.yssGetTableName("Tb_Base_ChildHoliday") +
					// " where FHolidaysCode = " +
					// dbl.sqlString(this.strHolidaysCode);
					// dbl.executeSql(strSql);
					// ---------------------------------------jc
				}
				conn.commit(); // 提交事物
				bTrans = false;
				conn.setAutoCommit(false);
			}
		} catch (Exception e) {
			throw new YssException("清除数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
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
