package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

//凭证生成方案设置 Tb_Vch_Project
public class VchProjectBean extends BaseDataSettingBean implements IDataSetting {
	private String projectCode = ""; // 生成方案代码
	private String projectName = ""; // 生成方案名称
	private int exeOrderCode = 0; // 执行序号代码;
	private int excuteBuild = 0; // 执行生成
	private int excuteCheck = 0; // 执行审核
	private int excuteInsert = 0; // 执行导入
	private String desc = ""; // 描述
	private int handCheck = 0; // 手工
	private String oldProjectCode = "";
	private VchProjectBean filterType = null;
	private String sRecycled = ""; // 增加对回收站的处理 by leeyu 2008-10-21 BUG:0000491
	private String sAssetGroupCode = "";		//added by liubo.Story #1916.组合群代码

	private String sAssetGroupName = "";		//added by liubo.Story #1916.组合群名称

	public String getsAssetGroupCode() {
		return sAssetGroupCode;
	}

	public void setsAssetGroupCode(String sAssetGroupCode) {
		this.sAssetGroupCode = sAssetGroupCode;
	}
	
	public String getsAssetGroupName() {
		return sAssetGroupName;
	}

	public void setsAssetGroupName(String sAssetGroupName) {
		this.sAssetGroupName = sAssetGroupName;
	}

	private String sMutil = "";//add by yanghaiming 20100708 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 

	public VchProjectBean() {
	}

	private String builerFilter() {
		String reSql = "";
		if (this.filterType != null) {
			reSql = " where 1=1";
			/**shashijie 2012-7-2 STORY 2475 */
			if (this.filterType.projectCode != null
					&& this.filterType.projectCode.trim().length() > 0) {
				reSql += " and a.FProjectCode like '"
						+ this.filterType.projectCode.replaceAll("'", "''")
						+ "%'";
			}
			if (this.filterType.projectName != null
					&& this.filterType.projectName.trim().length() > 0) {
				reSql += " and a.FProjectName like '"
						+ this.filterType.projectName.replaceAll("'", "''")
						+ "%'";
			}
			if (this.filterType.desc != null
					&& this.filterType.desc.trim().length() > 0) {
				reSql += " and a.FDesc like '"
						+ this.filterType.desc.replaceAll("'", "''") + "%'";
			}
			/**end*/
			// 2008.07.13 蒋锦 修改 修改筛选条件 999 代表所有 BUG：0000290
			if (this.filterType.exeOrderCode != 999) {
				reSql += " and a.FExeOrderCode ="
						+ this.filterType.exeOrderCode;
			}
			if (this.filterType.excuteBuild == 0
					|| this.filterType.excuteBuild == 1) {
				reSql += " and a.FExBuild =" + this.filterType.excuteBuild;
			}
			if (this.filterType.excuteCheck == 0
					|| this.filterType.excuteCheck == 1) {
				reSql += " and a.FExCheck =" + this.filterType.excuteCheck;
			}
			if (this.filterType.excuteInsert == 0
					|| this.filterType.excuteInsert == 1) {
				reSql += " and a.FExInsert =" + this.filterType.excuteInsert;
			}
			if (this.filterType.checkStateId == 1) {
				reSql = reSql + " and a.FCheckState = 1 ";
			}
		}
		return reSql;
	}

	private void setVchAttr(ResultSet rs) throws SQLException {
		this.projectCode = rs.getString("FProjectCode");
		this.projectName = rs.getString("FProjectName");
		this.desc = rs.getString("FDesc");
		this.exeOrderCode = rs.getInt("FExeOrderCode");
		this.excuteBuild = rs.getInt("FExBuild");
		this.excuteCheck = rs.getInt("FExCheck");
		this.excuteInsert = rs.getInt("FExInsert");
		this.handCheck = rs.getInt("FHandCheck");
		super.setRecLog(rs);
	}

	/**
	 * getListViewData1
	 * 
	 * @return String
	 */
	public String getListViewData1() throws YssException {
		Connection conn = null;
		boolean bTrans = false;
		ResultSet rs = null;
		ResultSet rsSerialNumber = null;
		String sqlStr = "";
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sVocStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try {
			sHeader = getListView1Headers();
			sqlStr = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,"
					+ " d.FVocName as FExBuildName, e.FVocName as FExCheckName, f.FVocName as FExInsertName from "
					+ pub.yssGetTableName("Tb_Vch_Project")
					+ " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "
					+ " left join Tb_Fun_Vocabulary d on "
					+ dbl.sqlToChar("a.FExBuild")
					+ " = d.FVocCode and d.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_VCH_EXBUILD)
					+ " left join Tb_Fun_Vocabulary e on "
					+ dbl.sqlToChar("a.FExCheck")
					+ " = e.FVocCode and e.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_VCH_EXCHECK)
					+ " left join Tb_Fun_Vocabulary f on "
					+ dbl.sqlToChar("a.FExInsert")
					+ " = f.FVocCode and f.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_VCH_EXINSERT)
					+ builerFilter()
					+ " order by a.FExeOrderCode,a.FCheckState, a.FCreateTime desc, a.FCheckTime desc";
			
			String sqlGetSerialNumber = "";
			String MaxOrderCode="";
			sqlGetSerialNumber = " select max(a.fexeordercode) as MaxOrderCode from "
					+ pub.yssGetTableName("Tb_Vch_Project")
					+ " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "
					+ " left join Tb_Fun_Vocabulary d on "
					+ dbl.sqlToChar("a.FExBuild")
					+ " = d.FVocCode and d.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_VCH_EXBUILD)
					+ " left join Tb_Fun_Vocabulary e on "
					+ dbl.sqlToChar("a.FExCheck")
					+ " = e.FVocCode and e.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_VCH_EXCHECK)
					+ " left join Tb_Fun_Vocabulary f on "
					+ dbl.sqlToChar("a.FExInsert")
					+ " = f.FVocCode and f.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_VCH_EXINSERT)
					+ builerFilter()
					+ " order by a.FExeOrderCode,a.FCheckState, a.FCreateTime desc, a.FCheckTime desc";
			
			rsSerialNumber=dbl.openResultSet(sqlGetSerialNumber);
//  		    MaxOrderCode=rsSerialNumber.getString("MaxOrderCode");
			while (rsSerialNumber.next()){
				MaxOrderCode=rsSerialNumber.getString("MaxOrderCode");
			}
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols()))
						.append(YssCons.YSS_LINESPLITMARK);
				setVchAttr(rs);
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
			VocabularyBean vocabulary = new VocabularyBean();
			vocabulary.setYssPub(pub);
			sVocStr = vocabulary.getVoc(YssCons.YSS_VCH_EXBUILD + ","
					+ YssCons.YSS_VCH_EXCHECK + "," + YssCons.YSS_VCH_EXINSERT);
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc"
					+ sVocStr+"MaxOrderCode"+MaxOrderCode;
		} catch (Exception e) {
			throw new YssException("获取凭证生成方案设置出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs); // 关闭游标资源 modify by sunkey 20090604
											// MS00472:QDV4上海2009年6月02日01_B
			dbl.closeResultSetFinal(rsSerialNumber);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * getListViewData2
	 * 
	 * @return String
	 */
	public String getListViewData2() throws YssException {
		Connection conn = null;
		boolean bTrans = false;
		ResultSet rs = null;
		String sqlStr = "";
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sVocStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try {
			sHeader = "方案代码\t方案名称";
			sqlStr = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName"
					+ " from "
					+ pub.yssGetTableName("Tb_Vch_Project")
					+ " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "
					+ " where a.FCheckState = 1 order by a.FExeOrderCode";
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {
				// bufShow.append(super.buildRowShowStr(rs,
				// this.getListView1ShowCols())).
				// append(YssCons.YSS_LINESPLITMARK);
				bufShow.append(rs.getString("FProjectCode")).append(
						YssCons.YSS_ITEMSPLITMARK1);
				bufShow.append(rs.getString("FProjectName")).append(
						YssCons.YSS_LINESPLITMARK);
				setVchAttr(rs);
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
			throw new YssException("获取凭证生成方案设置出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * getListViewData3
	 * 
	 * @return String
	 */
	//modified by liubo.Story #1916
	//================================
	public String getListViewData3() throws YssException{
		Connection conn = null;
		boolean bTrans = false;
		ResultSet rs = null;
		ResultSet rsSerialNumber = null;
		String sqlStr = "";
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sVocStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try {
			sHeader = getListView1Headers() + "\t组合群代码";
			String[] sAssetGroupCode = getAssdeGroup().split("\t");
			sqlStr = "select allData.* from (";
			for(int i = 0;i < sAssetGroupCode.length;i++)
			{
				sqlStr = sqlStr + " select a.*," + dbl.sqlString(sAssetGroupCode[i]) + " as FAssetGroupCode,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,"
						+ " d.FVocName as FExBuildName, e.FVocName as FExCheckName, f.FVocName as FExInsertName from "
						+ "Tb_" + sAssetGroupCode[i] + "_Vch_Project"
						+ " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
						+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "
						+ " left join Tb_Fun_Vocabulary d on "
						+ dbl.sqlToChar("a.FExBuild")
						+ " = d.FVocCode and d.FVocTypeCode = "
						+ dbl.sqlString(YssCons.YSS_VCH_EXBUILD)
						+ " left join Tb_Fun_Vocabulary e on "
						+ dbl.sqlToChar("a.FExCheck")
						+ " = e.FVocCode and e.FVocTypeCode = "
						+ dbl.sqlString(YssCons.YSS_VCH_EXCHECK)
						+ " left join Tb_Fun_Vocabulary f on "
						+ dbl.sqlToChar("a.FExInsert")
						+ " = f.FVocCode and f.FVocTypeCode = "
						+ dbl.sqlString(YssCons.YSS_VCH_EXINSERT)
						+ builerFilter()
						+ "  union";
			}
			sqlStr = sqlStr.substring(0,sqlStr.length() - 5);
			sqlStr = sqlStr + ") allData order by allData.FAssetGroupCode,allData.FProjectCode";
			
			String sqlGetSerialNumber = "";
			String MaxOrderCode="";
			sqlGetSerialNumber = " select max(a.fexeordercode) as MaxOrderCode from "
					+ pub.yssGetTableName("Tb_Vch_Project")
					+ " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "
					+ " left join Tb_Fun_Vocabulary d on "
					+ dbl.sqlToChar("a.FExBuild")
					+ " = d.FVocCode and d.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_VCH_EXBUILD)
					+ " left join Tb_Fun_Vocabulary e on "
					+ dbl.sqlToChar("a.FExCheck")
					+ " = e.FVocCode and e.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_VCH_EXCHECK)
					+ " left join Tb_Fun_Vocabulary f on "
					+ dbl.sqlToChar("a.FExInsert")
					+ " = f.FVocCode and f.FVocTypeCode = "
					+ dbl.sqlString(YssCons.YSS_VCH_EXINSERT)
					+ builerFilter()
					+ " order by a.FExeOrderCode,a.FCheckState, a.FCreateTime desc, a.FCheckTime desc";
			
			rsSerialNumber=dbl.openResultSet(sqlGetSerialNumber);
//  		    MaxOrderCode=rsSerialNumber.getString("MaxOrderCode");
			while (rsSerialNumber.next()){
				MaxOrderCode=rsSerialNumber.getString("MaxOrderCode");
			}
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols())).append("\t" + rs.getString("FAssetGroupCode"))
						.append(YssCons.YSS_LINESPLITMARK);
				setVchAttr(rs);
				this.sAssetGroupCode = rs.getString("FAssetGroupCode");
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
			VocabularyBean vocabulary = new VocabularyBean();
			vocabulary.setYssPub(pub);
			sVocStr = vocabulary.getVoc(YssCons.YSS_VCH_EXBUILD + ","
					+ YssCons.YSS_VCH_EXCHECK + "," + YssCons.YSS_VCH_EXINSERT);
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc"
					+ sVocStr + "MaxOrderCode" + MaxOrderCode;
		} catch (Exception e) {
			throw new YssException("获取凭证生成方案设置出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs); // 关闭游标资源 modify by sunkey 20090604
											// MS00472:QDV4上海2009年6月02日01_B
			dbl.closeResultSetFinal(rsSerialNumber);
			dbl.endTransFinal(conn, bTrans);
		}

	}
	//================================

	/**
	 * getListViewData4
	 * 
	 * @return String
	 */
	public String getListViewData4() {
		return "";
	}

	/**
	 * addSetting
	 * 
	 * @return String
	 */
	public String addSetting() throws YssException {
		Connection conn = null;
		ResultSet rs = null;
		boolean bTrans = false;
		String strSql = "";
		try {
			conn = dbl.loadConnection();
			strSql = " select * from " + pub.yssGetTableName("Tb_Vch_Project")
					+ " where FExeOrderCode =" + (exeOrderCode)
					+ " and FcheckState<>2"; // add by ly 添加判断用于处理执行编号相同的问题
												// 080528
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				throw new YssException("系统中已经存在执行编号为【" + exeOrderCode
						+ "】编号,请选择其他的执行编号");
			}
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Vch_Project")
					+ " (FProjectCode,FProjectName,FExeOrderCode,FExBuild,FExCheck,"
					+ "FExInsert,FDesc,FCheckState,FHandCheck,FCreator,FCreateTime) values("
					+ dbl.sqlString(this.projectCode) + ","
					+ dbl.sqlString(this.projectName) + "," + this.exeOrderCode
					+ "," + (this.excuteBuild == 0 ? 0 : this.excuteBuild)
					+ "," + (this.excuteCheck == 0 ? 0 : this.excuteCheck)
					+ "," + (this.excuteInsert == 0 ? 0 : this.excuteInsert)
					+ "," + dbl.sqlString(this.desc) + ","
					+ ("1") + ","//edit by yanghaiming 20100708 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 
					+ this.handCheck + ","
					/**add---huhuichao 2013-10-10 BUG  80552 在凭证方案设置时候点击修改会报系统错误*/
					+ (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ","
					/**end---huhuichao 2013-10-10 BUG  80552*/
					+ "'" + YssFun.formatDatetime(new java.util.Date()) + "')";
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("新增凭证生成方案设置出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
		return "";
	}

	/**
	 * checkInput
	 * 
	 * @param btOper
	 *            byte
	 */
	public void checkInput(byte bType) throws YssException {
		//edit by yanghaiming 20100708
		if(bType != YssCons.OP_EDIT && bType != YssCons.OP_DEL){
			dbFun.checkInputCommon(bType, pub.yssGetTableName("Tb_Vch_Project"),
					"FProjectCode", this.projectCode, this.oldProjectCode);
		}
	}

	/**
	 * checkSetting
	 */
	public void checkSetting() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			// =====增加对回收站的处理功能， by leeyu 2008-10-21 BUG:0000491
			conn.setAutoCommit(bTrans);
			String[] arrData = sRecycled.split("\r\n");
			for (int i = 0; i < arrData.length; i++) {
				if (arrData[i].length() == 0) {
					continue;
				}
				this.parseRowStr(arrData[i]);
				strSql = "update " + pub.yssGetTableName("Tb_Vch_Project")
						+ " set FCheckState = " + this.checkStateId + ","
						+ " FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ "," + " FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date())
						+ "' where FProjectCode="
						+ dbl.sqlString(this.projectCode);
				// conn.setAutoCommit(false);
				dbl.executeSql(strSql);
			}
			// =====2008-10-21
			bTrans = true;
			// dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核凭证生成方案设置出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * delSetting
	 */
	public void delSetting() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			strSql = "delete " + pub.yssGetTableName("Tb_Vch_Project")//edit by yanghaiming 20100708 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 
//					+ " set FCheckState=" + this.checkStateId + ","
//					+ " FCreator=" + dbl.sqlString(this.checkUserCode + " ")
//					+ "," + " FCreateTime="
//					+ dbl.sqlString(this.checkTime + " ")
					+ " where FProjectCode=" + dbl.sqlString(this.projectCode);
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除凭证生成方案设置出错", e);
		}
	}

	/**
	 * deleteRecycleData 完善回收站的处理功能，by leeyu 2008-10-21 BUG:0000491
	 */
	public void deleteRecycleData() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(bTrans);
			String[] arrData = sRecycled.split("\r\n");
			for (int i = 0; i < arrData.length; i++) {
				if (arrData[i].length() == 0) {
					continue;
				}
				this.parseRowStr(arrData[i]);
				strSql = "delete " + pub.yssGetTableName("Tb_Vch_Project")
						+ " where FProjectCode="
						+ dbl.sqlString(this.projectCode);
				dbl.executeSql(strSql);
			}
			bTrans = true;
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		} catch (Exception e) {
			throw new YssException("审核凭证生成方案设置出错", e);
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
		Connection conn = null;
		ResultSet rs = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			strSql = " select * from " + pub.yssGetTableName("Tb_Vch_Project")
					+ " where FExeOrderCode =" + (exeOrderCode)
					+ " and FProjectCode<>" + dbl.sqlString(oldProjectCode)
					+ " and FcheckState<>2"; // add by ly 添加判断用于处理执行编号相同的问题
												// 080528
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				throw new YssException("系统中已经存在执行编号为【" + exeOrderCode
						+ "】编号,请选择其他的执行编号");
			}
			strSql = "update " + pub.yssGetTableName("Tb_Vch_Project")
					+ " set FProjectCode=" + dbl.sqlString(this.projectCode)
					+ "," + " FProjectName=" + dbl.sqlString(this.projectName)
					+ "," + " FExeOrderCode=" + this.exeOrderCode + ","
					+ " FExBuild=" + this.excuteBuild + "," + " FExCheck="
					+ this.excuteCheck + "," + " FExInsert="
					+ this.excuteInsert + "," + " FDesc="
					+ dbl.sqlString(this.desc) + "," + " FHandCheck = "
					+ this.handCheck + " where FProjectCode="
					+ dbl.sqlString(this.oldProjectCode);
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("修改凭证生成方案设置出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
		return "";
	}

	/**
	 * getAllSetting
	 * 
	 * @return String
	 */
	public String getAllSetting() {
		return "";
	}

	/**
	 * getSetting
	 * 
	 * @return IDataSetting
	 */
	public IDataSetting getSetting() {
		return null;
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
	 * buildRowStr
	 * 
	 * @return String
	 */
	public String buildRowStr() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.projectCode).append("\t");
		buf.append(this.projectName).append("\t");
		buf.append(this.exeOrderCode).append("\t");
		buf.append(this.excuteBuild).append("\t");
		buf.append(this.excuteCheck).append("\t");
		buf.append(this.excuteInsert).append("\t");
		buf.append(this.desc).append("\t");
		buf.append(this.checkStateId).append("\t");
		buf.append(this.handCheck).append("\t");
		buf.append(this.sAssetGroupCode).append("\t");	//added by liubo.Story #1916
		buf.append(this.sAssetGroupName).append("\t");	//added by liubo.Story #1916
		buf.append(super.buildRecLog());
		return buf.toString();
	}

	/**
	 * getOperValue
	 * 
	 * @param sType
	 *            String
	 * @return String
	 */
	public String getOperValue(String sType) throws YssException{
		String result = "";
    	if (sType.equalsIgnoreCase("update")){//add by yanghaiming 20100708 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 
    		result = this.doUpdateProjects(this.sMutil);
    		this.updateExeorderCode();
    	}
        return "";
	}

	/**
	 * parseRowStr
	 * 
	 * @param sRowStr
	 *            String
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		String sTmpStr = "";
		
		try {
			if(sRowStr.indexOf("\b\f") > 0)//edit by yanghaiming 20100708 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 
			{
				this.sMutil = sRowStr.split("\b\f")[1];
			}else{
				if (sRowStr.trim().length() == 0) {
					return;
				}
				if (sRowStr.indexOf("\r\f") >= 0) {
					sTmpStr = sRowStr.split("\r\f")[0];
				} else {
					sTmpStr = sRowStr;
					reqAry = sTmpStr.split("\t");
				}
				sRecycled = sTmpStr; // 增加对回收站的处理功能 by leeyu 2008-10-21 BUG:0000491
				//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
	            if(reqAry == null)
	            	return ;
	            //---end---
				this.projectCode = reqAry[0];
				if (reqAry[0].length() == 0) {
					this.projectCode = " ";
				}
				this.projectName = reqAry[1];
				if (reqAry[1].length() == 0) {
					this.projectName = " ";
				}
				this.exeOrderCode = Integer.parseInt(reqAry[2]);
				this.excuteBuild = Integer.parseInt(reqAry[3]);
				this.excuteCheck = Integer.parseInt(reqAry[4]);
				this.excuteInsert = Integer.parseInt(reqAry[5]);
				this.desc = reqAry[6];
				this.oldProjectCode = reqAry[7];
				if (reqAry[7].length() == 0) {
					this.oldProjectCode = " ";
				}
				this.checkStateId = Integer.parseInt(reqAry[8]);
				this.handCheck = YssFun.toInt(reqAry[9]);
				this.sAssetGroupCode = reqAry[10];	//added by liubo.Story #1916
				this.sAssetGroupName = reqAry[11];	//added by liubo.Story #1916
				super.parseRecLog();
				if (sRowStr.indexOf("\r\t") >= 0) {
					if (this.filterType == null) {
						this.filterType = new VchProjectBean();
						this.filterType.setYssPub(pub);
					}
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			}
		} catch (Exception e) {
			throw new YssException("解析凭证生成方案设置出错!");
		}
	}

	/**
	 * getBeforeEditData
	 * 
	 * @return String
	 */
	public String getBeforeEditData() {
		return "";
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setExcuteBuild(int excuteBuild) {
		this.excuteBuild = excuteBuild;
	}

	public void setExcuteCheck(int excuteCheck) {
		this.excuteCheck = excuteCheck;
	}

	public void setExcuteInsert(int excuteInsert) {
		this.excuteInsert = excuteInsert;
	}

	public void setExeOrderCode(int exeOrderCode) {
		this.exeOrderCode = exeOrderCode;
	}

	public void setFilterType(VchProjectBean filterType) {
		this.filterType = filterType;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setHandCheck(int handCheck) {
		this.handCheck = handCheck;
	}

	public VchProjectBean getFilterType() {
		return filterType;
	}

	public int getExeOrderCode() {
		return exeOrderCode;
	}

	public int getExcuteInsert() {
		return excuteInsert;
	}

	public int getExcuteCheck() {
		return excuteCheck;
	}

	public int getExcuteBuild() {
		return excuteBuild;
	}

	public String getDesc() {
		return desc;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public String getProjectName() {
		return projectName;
	}

	public int getHandCheck() {
		return handCheck;
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
	
	//add by yanghaiming 20100707 MS01319 QDV4赢时胜（上海）2010年6月21日01_A 
	private String doUpdateProjects(String sMutilRowStr) throws YssException {
    	PreparedStatement pst = null;
        String strSql = "";
        String[] arrMutilAttr = sMutilRowStr.split("\r\f");
        ResultSet rs = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "UPDATE " + pub.yssGetTableName("Tb_Vch_Project") + " SET FEXEORDERCODE = ? " + " WHERE FPROJECTCODE = ?";
            pst = dbl.openPreparedStatement(strSql);
            
            for(int i = 0; i < arrMutilAttr.length; i++){
            	pst.setInt(1, Integer.parseInt(arrMutilAttr[i].split("\t")[1]));
            	pst.setString(2, arrMutilAttr[i].split("\t")[0]);
            	pst.addBatch();
            }
            pst.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }
	//add by yanghaiming 20100707 MS01319 QDV4赢时胜（上海）2010年6月21日01_A  将执行需要按从小到大的顺序重新编号
	private void updateExeorderCode() throws YssException {
		int num = 0;
		Connection conn = null;
		ResultSet rs = null;
		boolean bTrans = false;
		PreparedStatement pst = null;
		String strSql = "";
		try {
			
			conn = dbl.loadConnection();
			strSql = " select FPROJECTCODE,fexeordercode from " + pub.yssGetTableName("Tb_Vch_Project")
					+ " where FcheckState = 1 group by FPROJECTCODE,fexeordercode order by fexeordercode"; 
			rs = dbl.openResultSet(strSql);
			conn.setAutoCommit(false);
            bTrans = true;
            strSql = "UPDATE " + pub.yssGetTableName("Tb_Vch_Project") + " SET FEXEORDERCODE = ? " + " WHERE FPROJECTCODE = ?";
            pst = dbl.openPreparedStatement(strSql);
			while (rs.next()) {
				pst.setInt(1, num);
            	pst.setString(2, rs.getString("FPROJECTCODE"));
            	pst.addBatch();
            	num ++;
			}
			pst.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新凭证生成方案设置出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * add baopingping #story 1167 20110717
	 * 查询当前表中所有组合群
	 * return ResultSet
	 * @throws YssException 
	 */
	public String getAssdeGroup() throws YssException{
		ResultSet rs=null;
		String sql=null;
		String FAssetGroupCode="";
		try{
			sql="select * from Tb_Sys_AssetGroup where FAssetGroupCode = '" + pub.getAssetGroupCode() + "' order by FAssetGroupCode";
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				FAssetGroupCode+=rs.getString("FAssetGroupCode")+"\t";
			}
			return FAssetGroupCode;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
