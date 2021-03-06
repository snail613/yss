package com.yss.main.operdeal.report.reptab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.yss.base.BaseAPOperValue;
import com.yss.dsub.BaseBean;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

public class TabSellInterest extends BaseAPOperValue {

	private String startDate = ""; // 期初日期
	private String endDate = ""; // 期末日期
	private String portCode = ""; // 组合代码
	private String interestWay;// 计息方式 计头不计尾、计尾不计头
	private Double interestRate;// 计息利率
	private String interestDay;// 计息天数
	private String holidayCode = ""; // 节假日代码
	private Integer paraId = 0; // 参数值编号
	private String selNetList = "";// 网点集合

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public TabSellInterest() {
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
			if (reqAry1[2].equalsIgnoreCase("DateTimePicker1")) {
				this.startDate = reqAry1[1];
			}
			if (reqAry1[2].equalsIgnoreCase("DateTimePicker2")) {
				this.endDate = reqAry1[1];
			}
			if (reqAry1[2].equalsIgnoreCase("SelectControl1")) {
				this.portCode = reqAry1[1];
			}
		}
	}

	public Object invokeOperMothed() throws YssException {
		buildParaId();// 取通用参数类型编号
		getInterestType();// 取通用参数类型计息方式
		getInterestRate();// 取通用参数类型计息利率
		getInterestDay();// 取通用参数类型计息天数
		getHolidays();// 取通用参数类型节假日
		getNetList();// 获取通用参数类型网点集合
		createTmpTable();//取数并写入临时表
		return "";
	}

	/**
	 * 取TA申购计息表及TA交易表所需数据，并写入到临时表
	 * 
	 * @throws YssException
	 */
	public void createTmpTable() throws YssException {
		String strSql2 = "";
		String strSql3 = "";
		ResultSet rs2 = null;
		PreparedStatement ps = null;
		PreparedStatement pst = null;
		Connection conn = null;
		boolean bTrans = false;
		String strSql1 = "";
		ResultSet rs = null;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql1 = "select * from "
					+ pub.yssGetTableName("TB_data_interestpayrec")
					+ " where FTSFTYPECODE='06' and  FSUBTSFTYPECODE='06PF' and fportcode = '"
					+ portCode + "' and FTRANSDATE between "
					+ dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate);
			strSql2 = "select a.ftransdate,b.fsellNetCode,b.fsellMoney,b.FSettleDate,a.fportcurymoney from (select * from "
					+ pub.yssGetTableName("TB_data_interestpayrec")
					+ "  where ftransdate = ? "
					+ ") a inner join (select * from "
					+ pub.yssGetTableName("Tb_TA_Trade")
					+ " where fportcode = '"
					+ portCode
					+ "' and FSellType = '01' and FSELLNETCODE in ("
					+ selNetList
					+ ") and ? between FConfimDate  and FSettleDate) b on  "
					+ "  a.fportcode = b.fportcode "
					+ " and a.FCashAccCode=b.FCashAccCode";
			strSql3 = "insert into TMP_REP_XS_SGKLL"
					+ "(ftransdate,fsellNetCode,fsellMoney,fportcurymoney,FInterestRate,FInterestType) values(?,?,?,?,?,?)";
			ps = conn.prepareStatement(strSql3);
			rs = dbl.openResultSet(strSql1);
			while (rs.next()) {
				pst = conn.prepareStatement(strSql2);
				pst.setDate(1, rs.getDate("FTransdate"));
				pst.setDate(2, rs.getDate("FTransdate"));
				rs2 = pst.executeQuery();
				while (rs2.next()) {
					// 从结算日期，往前推延迟天数之前最近的工作日日期
					java.util.Date dWorkDate = null; // 结算日期
					java.util.Date dSettleDate = rs2.getDate("FSettleDate");
					// 判断计头不计尾，还是计尾部计头，计算实际结算日期和记息区间
					if (interestWay.equals("0")) { // 表示计头不计尾
						dSettleDate = YssFun.addDay(dSettleDate, -1);
						dWorkDate = this.getSettingOper().getWorkDay(
								holidayCode, rs2.getDate("FSettleDate"),
								Integer.parseInt(interestDay) * -1);
					} else {
						dWorkDate = this.getSettingOper().getWorkDay(
								holidayCode, rs2.getDate("FSettleDate"),
								Integer.parseInt(interestDay) * -1);
						dWorkDate = YssFun.addDay(dWorkDate, 1);
					}
					if (rs.getDate("FTransdate").compareTo(dWorkDate) >= 0
							&& rs.getDate("FTransdate").compareTo(dSettleDate) <= 0) {

						ps.setString(1, YssFun.formatDate(rs2
								.getDate("ftransdate")));
						ps.setString(2, rs2.getString("fsellNetCode"));
						ps.setDouble(3, rs2.getDouble("fsellMoney"));
						ps.setDouble(4, rs2.getDouble("fportcurymoney"));
						ps.setDouble(5, interestRate);
						ps.setString(6, "申购");
						ps.addBatch();
					}
				}
				// rs2.getStatement().close();
				dbl.closeResultSetFinal(rs2);
				// pst.addBatch();
			}
			ps.executeBatch();
			rs.getStatement().close();
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("获取TA申购款计息表数据出错： \n" + e.getMessage());
		} finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs2);
			dbl.closeStatementFinal(ps, pst);
		}
	}

	/**
	 * 解析通用参数类型设定表中组合信息
	 * 
	 * @return参数值编号
	 * @throws YssException
	 */
	private void buildParaId() throws YssException {
		String strTmp = "";
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		ResultSet rs1 = null;
		try {
			strTmp = "select FctlValue,FparaId from "
					+ pub.yssGetTableName("TB_PFOper_PUBPARA")
					+ " where fctlcode='selPort' and  FctlGrpCode ='TAInterestScale'";
			rs1 = dbl.openResultSet(strTmp);
			while (rs1.next()) {
				String ctlValue = rs1.getString("FCTLVALUE");
				Integer paraId = rs1.getInt("FparaId");
				ctlValue = ctlValue.substring(0, ctlValue.indexOf('|'));
				hm.put(paraId, ctlValue);
			}
			if (hm == null || hm.size() < 1) {
				throw new YssException("获取通用参数类型表参数值编号出错");
			}
			Iterator iterator = (Iterator) hm.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (value.equals(portCode)) {
					paraId = (Integer) key;
				}
			}
			rs1.close();
		} catch (Exception e) {
			throw new YssException("获取通用参数类型表参数值编号出错： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs1);
		}
	}

	/**
	 * 获取用参数类型表中的计息方式
	 * 
	 * @return
	 * @throws YssException
	 */
	private void getInterestType() throws YssException {
		String strSql = "";
		ResultSet rs2 = null;
		try {
			strSql = "select FctlValue from "
					+ pub.yssGetTableName("TB_PFOper_PUBPARA")
					+ " where fctlcode='cbxType' and  FctlGrpCode ='TAInterestScale' and Fparaid="
					+ paraId;
			rs2 = dbl.openResultSet(strSql); // 取得通用参数类型表中的计息方式
			while (rs2.next()) {
				String temp = rs2.getString("FCTLVALUE");
				interestWay = temp.substring(0, temp.indexOf(','));
			}
			rs2.close();
		} catch (Exception e) {
			throw new YssException("获取用参数类型表中的计息方式出错： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs2);
		}
	}

	private void getInterestRate() throws YssException {
		String strSql = "";
		ResultSet rs2 = null;
		try {
			strSql = "select FctlValue from "
					+ pub.yssGetTableName("TB_PFOper_PUBPARA")
					+ " where fctlcode='txtValue' and  FctlGrpCode ='TAInterestScale' and Fparaid="
					+ paraId;
			rs2 = dbl.openResultSet(strSql); // 取得通用参数类型表中的计息方式
			while (rs2.next()) {
				interestRate = rs2.getDouble("FCTLVALUE");
			}
			rs2.close();
		} catch (Exception e) {
			throw new YssException("获取计息利率出错： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs2);
		}
	}

	private void getInterestDay() throws YssException {
		String strSql = "";
		ResultSet rs2 = null;
		try {
			strSql = "select FctlValue from "
					+ pub.yssGetTableName("TB_PFOper_PUBPARA")
					+ " where fctlcode='txtDays' and  FctlGrpCode ='TAInterestScale' and Fparaid="
					+ paraId;
			rs2 = dbl.openResultSet(strSql); // 取得通用参数类型表中的计息天数
			while (rs2.next()) {
				interestDay = rs2.getString("FCTLVALUE");
			}
			rs2.close();
		} catch (Exception e) {
			throw new YssException("获取计息天数出错： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs2);
		}

	}

	private void getHolidays() throws YssException {
		String strSql = "";
		ResultSet rs2 = null;
		try {
			strSql = "select FctlValue from "
					+ pub.yssGetTableName("TB_PFOper_PUBPARA")
					+ " where fctlcode='selHolidays' and  FctlGrpCode ='TAInterestScale' and Fparaid="
					+ paraId;
			rs2 = dbl.openResultSet(strSql); // 取得通用参数类型表中的节假日
			while (rs2.next()) {
				String temp = rs2.getString("FCTLVALUE");
				holidayCode = temp.substring(0, temp.indexOf('|'));
			}
			rs2.close();
		} catch (Exception e) {
			throw new YssException("获取计息利率出错： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs2);
		}
	}

	private void getNetList() throws YssException {
		String strSql = "";
		ResultSet rs1 = null;
		try {
			strSql = "select a.FCTLVALUE from "
					+ pub.yssGetTableName("TB_PFOper_PUBPARA")
					+ " a where a.FCTLCODE='selNet' and FctlGrpCode ='TAInterestScale' and Fparaid="
					+ paraId;
			rs1 = dbl.openResultSet(strSql);
			if (rs1.next()) {
				String temp = rs1.getString("FCTLV" + "ALUE");
				String selNetStr = temp.substring(0, temp.indexOf('|'));
				selNetList = "'" + selNetStr.replace(",", "','") + "'";
			}
			rs1.close();
		} catch (Exception e) {
			throw new YssException("获取计息利率出错： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs1);
		}

	}

}
