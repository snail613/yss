package com.yss.main.operdata.moneycontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class TrialMoneyBean extends BaseDataSettingBean implements IDataSetting {

	private boolean bShow = false;

	private TrialMoneyBean filtertype = null;
	private String sRecycled = ""; // 保存未解析前的字符串
	private String multAuditString = ""; // 批量处理

	private String num = "";// 编号
	private String portCode = "";// 投资组合代码
	private String portName = "";// 投资组合名称
	private String exchangeCode = "";// 交易所代码
	private String exchangeName = "";// 交易证券名称
	private Date bargainDate = null;// 成交日期
	private Date settleDate = null;// 结算日期
	private double trialMoney;// 试算金额
	private String desc = "";// 描述说明

	private String oldNum = "";// 编号
	private String oldPortCode = "";// 投资组合代码
	private String oldPortName = "";// 投资组合名称
	private String oldExchangeCode = "";// 交易所代码
	private String oldExchangeName = "";// 交易证券名称
	private Date oldBargainDate = null;// 成交日期
	private Date oldSettleDate = null;// 结算日期
	private double oldTrialMoney;// 试算金额
	private String oldDesc = "";// 描述说明

	public TrialMoneyBean getFilterType() {
		return this.filtertype;
	}

	public void setFilterType(TrialMoneyBean filtertype) {
		this.filtertype = filtertype;
	}

	public void setBShow(boolean bshow) {
		this.bShow = bshow;
	}

	public boolean getBShow() {
		return this.bShow;
	}

	public String getNum() {
		return this.num;
	}

	public String getPortcode() {
		return this.portCode;
	}

	public String getPortName() {
		return this.portName;
	}

	public String getExchangeCode() {
		return this.exchangeCode;
	}

	public String getExchangeName() {
		return this.exchangeName;
	}

	public Date getBargainDate() {
		return this.bargainDate;
	}

	public Date getSettleDate() {
		return this.settleDate;
	}

	public double getTrialMoney() {
		return this.trialMoney;
	}

	public String getDesc() {
		return this.desc;
	}

	public String getOldNum() {
		return this.oldNum;
	}

	public String getOldPortcode() {
		return this.oldPortCode;
	}

	public String getOldPortName() {
		return this.oldPortName;
	}

	public String getOldExchangeCode() {
		return this.oldExchangeCode;
	}

	public String getOldExchangeName() {
		return this.oldExchangeName;
	}

	public Date getOldBargainDate() {
		return this.oldBargainDate;
	}

	public Date getOldSettleDate() {
		return this.oldSettleDate;
	}

	public double getOldTrialMoney() {
		return this.oldTrialMoney;
	}

	public String getOldDesc() {
		return this.oldDesc;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public void setPortCode(String portcode) {
		this.portCode = portcode;
	}

	public void setPortName(String portname) {
		this.portName = portname;
	}

	public void setExchangeCode(String exchangecode) {
		this.exchangeCode = exchangecode;
	}

	public void setExchangeName(String exchangename) {
		this.exchangeName = exchangename;
	}

	public void setBargainDate(Date bargainDate) {
		this.bargainDate = bargainDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	public void setTrialMoney(double trialmoney) {
		this.trialMoney = trialmoney;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setOldNum(String num) {
		this.oldNum = num;
	}

	public void setOldPortCode(String portcode) {
		this.oldPortCode = portcode;
	}

	public void setOldPortName(String portname) {
		this.oldPortName = portname;
	}

	public void setOldExchangeCode(String exchangecode) {
		this.oldExchangeCode = exchangecode;
	}

	public void setOldExchangeName(String exchangename) {
		this.oldExchangeName = exchangename;
	}

	public void setOldBargainDate(Date bargainDate) {
		this.oldBargainDate = bargainDate;
	}

	public void setOldSettleDate(Date settleDate) {
		this.oldSettleDate = settleDate;
	}

	public void setOldTrialMoney(double trialmoney) {
		this.trialMoney = trialmoney;
	}

	public void setOldDesc(String desc) {
		this.oldDesc = desc;
	}

	private TrialMoneyBean trialmoneybean = null;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	public void parseRowStr(String sRowStr) throws YssException {
		if (trialmoneybean == null) {
			trialmoneybean = new TrialMoneyBean();
			trialmoneybean.setYssPub(pub);
		}
		String reqAry[] = null;
		String sTmpStr = "";

		String sMutiAudit = ""; // 批量处理的数据
		try {

			if (sRowStr.trim().length() == 0) {
				return;
			}

			if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
				sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1]; // 得到的是从前台传来需要审核与反审核的批量数据
				multAuditString = sMutiAudit; // 保存在全局变量中
				sRowStr = sRowStr.split("\f\n\f\n\f\n")[0]; // 前台传来的要更新的一些数据
			}

			if (sRowStr.indexOf("\r\t") >= 0) {
				sTmpStr = sRowStr.split("\r\t")[0];
			} else {
				sTmpStr = sRowStr;
			}

			sRecycled = sRowStr; // 把未解析的字符串先赋给sRecycled
			reqAry = sTmpStr.split("\t");

			this.num = reqAry[0];
			this.portCode = reqAry[1];
			this.portName = reqAry[2];
			this.exchangeCode = reqAry[3];
			this.exchangeName = reqAry[4];
			this.bargainDate = YssFun
					.parseDate(reqAry[5].trim().length() == 0 ? "9998-12-31"
							: reqAry[5]);
			this.settleDate = YssFun
					.parseDate(reqAry[6].trim().length() == 0 ? "9998-12-31"
							: reqAry[6]);
			this.trialMoney = Double.parseDouble(reqAry[7]);
			this.desc = reqAry[8].replaceAll("【Enter】", "\r\n");

			this.oldNum = reqAry[9];
			this.oldPortCode = reqAry[10];
			this.oldPortName = reqAry[11];
			this.oldExchangeCode = reqAry[12];
			this.oldExchangeName = reqAry[13];
			this.oldBargainDate = YssFun
					.parseDate(reqAry[14].trim().length() == 0 ? "9998-12-31"
							: reqAry[14]);
			this.oldSettleDate = YssFun
					.parseDate(reqAry[15].trim().length() == 0 ? "9998-12-31"
							: reqAry[15]);
			this.oldTrialMoney = Double.parseDouble(reqAry[16]);
			this.oldDesc = reqAry[17].replaceAll("【Enter】", "\r\n");

			this.checkStateId = YssFun.toInt(reqAry[18]);

			if (reqAry[19].equalsIgnoreCase("true")) {
				this.bShow = true;
			} else {
				this.bShow = false;
			}

			super.parseRecLog();

			if (sRowStr.indexOf("\r\t") >= 0) {
				if (this.filtertype == null) {
					this.filtertype = new TrialMoneyBean();
					this.filtertype.setYssPub(pub);
				}
				this.filtertype.parseRowStr(sRowStr.split("\r\t")[1]);
			}
		} catch (Exception e) {
			throw new YssException("解析交易数据出错！", e);
		}
	}

	public String buildRowStr() throws YssException {

		StringBuffer buf = new StringBuffer();

		buf.append(this.num).append("\t");
		buf.append(this.portCode).append("\t");
		buf.append(this.portName).append("\t");
		buf.append(this.exchangeCode).append("\t");
		buf.append(this.exchangeName).append("\t");
		buf.append(format.format(this.bargainDate)).append("\t");
		buf.append(format.format(this.settleDate)).append("\t");
		buf.append(this.trialMoney).append("\t");
		buf.append(this.desc).append("\t");

		buf.append(super.buildRecLog());

		return buf.toString();
	}

	public String addSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false;// 代表事务是否开始
		String nowDate = "";
		Connection conn = dbl.loadConnection();
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			nowDate = YssFun.formatDate(new java.util.Date(),
					YssCons.YSS_DATETIMEFORMAT).substring(0, 8);
			this.num = "T"
					+ nowDate
					+ "00000"
					+ dbFun.getNextInnerCode(pub
							.yssGetTableName("Tb_Data_DivineTrialMoney"), dbl
							.sqlRight("FNum", 6), "000001",
							" where FNum like 'T" + nowDate + "%'", 1);
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Data_DivineTrialMoney")
					+ " (FNUM,FPORTCODE,FEXCHANGECODE,FBARGAINDATE,FSETTLEDATE,FDESC,FCREATOR,FCREATETIME,FCHECKSTATE,FTRIALMONEY)"
					+ " values(" + dbl.sqlString(this.num) + ","
					+ dbl.sqlString(this.portCode) + ","
					+ dbl.sqlString(this.exchangeCode) + ","
					+ dbl.sqlDate(this.bargainDate) + ","
					+ dbl.sqlDate(this.settleDate) + ","
					+dbl.sqlString(this.desc)+","
					+ dbl.sqlString(this.creatorCode) + ","
					+ dbl.sqlString(this.creatorTime) + ","
					+ dbl.sqlString("0")
					+ "," + this.trialMoney+")";
			dbl.executeSql(strSql);

			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);

			return "";
		} catch (Exception e) {
			throw new YssException("新增试算金额数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper, pub
				.yssGetTableName("Tb_Data_DivineTrialMoney"), "FNum", this.num,
				this.oldNum);

	}

	public void checkSetting() throws YssException {
		String strSql = ""; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			if ((sRecycled != null && !sRecycled.equalsIgnoreCase("")))// 判断传来的内容是否为空
			{
				arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql = "update "
							+ pub.yssGetTableName("Tb_Data_DivineTrialMoney")
							+ " set fcheckstate = case fcheckstate when 0 then 1 else 0 end"
							+ ",fcheckuser = "
							+ dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = '"
							+ YssFun.formatDatetime(new java.util.Date()) + "'"
							+ " where FNum = " + dbl.sqlString(this.num);
					dbl.executeSql(strSql);
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核试算金额数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}

	}

	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String strSql = "";
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = "update "
					+ pub.yssGetTableName("Tb_Data_DivineTrialMoney")
					+ " set FCheckState = 2 " + ", FCheckUser = null "
					+ ", FCheckTime = null " + " where FNum = "
					+ dbl.sqlString(this.num);

			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除试算金额数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public void deleteRecycleData() throws YssException {
		String strSql = ""; // 定义一个放SQL语句的字符串
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		// 获取一个连接
		Connection conn = dbl.loadConnection();
		try {
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
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
							+ pub.yssGetTableName("Tb_Data_DivineTrialMoney")
							+ " where FNum = " + dbl.sqlString(this.num);

					dbl.executeSql(strSql);
				}
			}

			conn.commit(); // 提交事物
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("清除试算金额数据信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}

	}

	public String editSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = "update "
					+ pub.yssGetTableName("Tb_Data_DivineTrialMoney")
					+ " set FPORTCODE = " + dbl.sqlString(this.portCode)
					+ " , FEXCHANGECODE=" + dbl.sqlString(this.exchangeCode)
					+" ,Fbargaindate="+dbl.sqlDate(this.bargainDate)
					+",Fsettledate="+dbl.sqlDate(this.settleDate)
					+ " ,FDESC=" + dbl.sqlString(this.desc) + " , FCREATOR="
					+ dbl.sqlString(this.creatorCode) +", FCHECKUSER="
					+ dbl.sqlString(this.checkUserCode)+ ", FTRIALMONEY="
					+ this.trialMoney + " where FNum = "
					+ dbl.sqlString(this.oldNum);

			dbl.executeSql(strSql);

			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return "";
		} catch (Exception e) {
			throw new YssException("修改试算金额数据交易信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public String getOperValue(String sType) throws YssException {
		String sResult = "";
		try {
			// 批量审核/反审核/删除
			if (sType.equalsIgnoreCase("multrialMoney")) { // 判断是否要进行批量审核与反审核
				if (multAuditString.length() > 0) { // 判断批量审核与反审核的内容是否为空
					return this.auditMutli(this.multAuditString); // 执行批量审核/反审核
				}
			}
			return sResult;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	public String auditMutli(String sMutilRowStr) throws YssException {
		Connection conn = null; // 建立一个数据库连接
		String sqlStr = ""; // 创建一个字符串
		PreparedStatement psmt1 = null;
		boolean bTrans = true; // 建一个boolean变量，默认自动回滚
		String[] multAudit = null; // 建一个字符串数组

		try {
			conn = dbl.loadConnection(); // 和数据库进行连接
			// 审核、反审核、删除交易数据
			sqlStr = "update "
					+ pub.yssGetTableName("Tb_Data_DivineTrialMoney")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FNum = ? "; // 更新数据库审核与未审核的SQL语句

			psmt1 = conn.prepareStatement(sqlStr); // 执行SQL语句

			if (multAuditString.length() > 0) {
				multAudit = sMutilRowStr.split("\f\f\f\f"); // 拆分从前台传来的listview里面的条目
				if (multAudit.length > 0) { // 判断传来的审核与反审核条目数量可大于0
					for (int i = 0; i < multAudit.length; i++) { // 循环遍历这些条目
						TrialMoneyBean trialmoneybean=new TrialMoneyBean();
						trialmoneybean.setYssPub(pub); // 设置一些基础信息
						trialmoneybean.parseRowStr(multAudit[i]); // 解析前台传来的单个条目信息
						psmt1.setString(1, trialmoneybean.num);
						psmt1.addBatch();
					}
				}
				conn.setAutoCommit(false); // 设置不自动回滚，这样才能开启事物
				psmt1.executeBatch();
				conn.commit(); // 提交事物
				bTrans = false;
			}
		} catch (Exception e) {
			throw new YssException("批量审核试算金额数据出错!");
		}finally
		{
			dbl.closeStatementFinal(psmt1);
		}
		return "";
	}

	public String getListViewData1() throws YssException {
		String strSql = ""; // 定义一个存放sql语句的字符串
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		ResultSet rs = null;
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try {
			sHeader = this.getListView1Headers();

			strSql = "select a.*, b.fportname,c.fexchangename,d.fusername as FCreatorName,e.fusername as FCheckUserName from "
					+ pub.yssGetTableName("tb_data_divinetrialmoney  a")
					+ " left join (select m.fportcode, m.fportname from "
					+ pub.yssGetTableName("tb_para_portfolio m")
					//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//					+ "  join (select Max(fstartdate) as fstartdate, fportcode from "
//					+ pub.yssGetTableName("tb_para_portfolio")
					//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
					//----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
					+ " where fcheckstate = 1 "
					+ ") b on a.fportcode = b.fportcode left join (select fexchangecode,fexchangename from "
					//----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
					+ pub.yssGetTableName("Tb_Base_Exchange")
					+ " where fcheckstate=1 ) c on a.fexchangecode = c.fexchangecode"
					+ " left join (select fusercode, fusername from "
					+ pub.yssGetTableName("Tb_Sys_UserList")
					+ " ) d on a.fcreator =d.fusercode left join (select fusercode, fusername from "
					+ pub.yssGetTableName("Tb_Sys_UserList")
					+ " ) e on a.fcheckuser =e.fusercode"
					+ " where "
					+ buildFilterStr("a")
					+ " order by a.FCheckState, a.FCreateTime desc";

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols()))
						.append(YssCons.YSS_LINESPLITMARK);

				this.setResultSetAttr(rs);

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
			throw new YssException("获取试算交易数据出错！" + "\r\n" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException,
			YssException {

		this.num = rs.getString("Fnum");
		this.portCode = rs.getString("Fportcode");
		this.portName = rs.getString("FportName");
		this.exchangeCode = rs.getString("FexchangeCode");
		this.exchangeName = rs.getString("fexchangename");
		this.bargainDate = rs.getDate("FbargainDate");
		this.settleDate = rs.getDate("FsettleDate");
		this.trialMoney = rs.getDouble("FtrialMoney");
		this.desc = rs.getString("FDesc");

		super.setRecLog(rs);
	}

	/**
	 * 生成筛选条件子句
	 * 
	 * @param prefix
	 * @return
	 * @throws YssException
	 */
	public String buildFilterStr(String prefix) throws YssException {
		String str = "";

		try {
			ArrayList alCon = new ArrayList();

			alCon.add(" 1=1 ");

			if (this.filtertype != null) {
				TrialMoneyBean filter = this.filtertype;

				if (filter.getBShow() == false) {
					alCon.add(" 1=2 ");
				}

				if (prefix == null) {
					prefix = "";
				} else if (!prefix.trim().endsWith(".")) {
					prefix += ".";
				}

				if (!YssFun.formatDate(filter.getBargainDate())
						.equalsIgnoreCase("9998-12-31")) {
					alCon.add(prefix + "FBargainDate = "
							+ dbl.sqlDate(filter.getBargainDate()));
				}

				if (!YssFun.formatDate(filter.getSettleDate())
						.equalsIgnoreCase("9998-12-31")) {
					alCon.add(prefix + "FSettleDate = "
							+ dbl.sqlDate(filter.getSettleDate()));
				}

				if (filter.getPortcode() != null
						&& filter.getPortcode().trim().length() != 0) {
					alCon.add(prefix + "FPortCode in ("
							+ dbl.sqlString(filter.getPortcode().trim()) + ")");
				}

				if (filter.getExchangeCode() != null
						&& filter.getExchangeCode().trim().length() > 0) {
					alCon.add(prefix + "FExchangeCode = "
							+ dbl.sqlString(filter.getExchangeCode().trim()));
				}

				if (filter.getTrialMoney() > 0) {
					alCon.add(prefix + "FTrialMoney = "
							+ filter.getTrialMoney());
				}

				if (filter.getDesc() != null
						&& filter.getDesc().trim().length() > 0) {
					alCon.add(prefix + "FDesc = "
							+ dbl.sqlString(filter.getDesc().trim()));
				}
			}

			str = YssFun.join((String[]) alCon.toArray(new String[] {}),
					" and ");
		} catch (Exception e) {
			throw new YssException("生成筛选条件子句出错！", e);
		}

		return str;
} 

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
