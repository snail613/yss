package com.yss.main.operdeal.datainterface.dataCenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssCons;

public class ImpMultiClassFundNetValueDeal extends BaseDataCenter {

	private String msg = "";
	// ------add by hongqongbing 2013-12-19
	// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
	private String msgMultiClass = "";
	private StringBuffer sPortCodeBuf = new StringBuffer(); // 存放不包含“分级组合”的组合对应的组合代码
	private StringBuffer sMultiClassBuf = new StringBuffer();// 存放包含“分级基金数据”的组合
	private StringBuffer sMultiClassNoneBuf = new StringBuffer();// 存放不包含“分级基金数据”的组合
	private String sTmpStr = "";
	private int multiClassNum = 0; // 记录组合对应分级组合的个数，如果只有1个分级组合，表示该组合没有分级组合
	StringBuffer fundcodeBuf = new StringBuffer();// 存放组合对应的分级组合字段
	private int tmpNum = 0;
	// ------end by hongqongbing 2013-12-19
	// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
	public String impData() throws YssException {
		delData();
		return insertData();
	}

	public void delData() throws YssException {
		PreparedStatement pst = null;
		boolean bTrans = true;
		String delSql = "";
		StringBuffer delSqlBuf = new StringBuffer();
		String fundcodeString = "";
		String preTb = pub.getPrefixTB();
		try {
			fundcodeString = getQDIIFportclscode();
			
			delSqlBuf.append("delete from FUNDASSET_DT where FINANCEDATE between ");
			delSqlBuf.append(dbl.sqlDate(sStartDate));
			delSqlBuf.append(" and ");
			delSqlBuf.append(dbl.sqlDate(sEndDate));
			delSqlBuf.append(" and FUNDCODE in(");
			delSqlBuf.append(fundcodeString);
			delSqlBuf.append(")");
			delSql = delSqlBuf.toString();
			
			pst = openPreparedStatement(delSql);
			con.setAutoCommit(false);
			pst.execute();
			con.commit();
			bTrans = false;
			con.setAutoCommit(true);
		} catch (Exception e) {
			msg = "☆☆☆☆☆ 导入【分级基金净值数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心——分级基金净值数据接口：删除分级基金净值数据报错！！！】\t", e);
		} finally {
			pub.setPrefixTB(preTb);
			closeStatementFinal(pst);
			endTransFinal(con, bTrans);
		}

	}

	public String insertData() throws YssException {

		PreparedStatement pst = null;
		String insertSql = "";
		StringBuffer insertSqlbuf = new StringBuffer();
		boolean flag = false;
		boolean bTrans = true;
		String nowTime = "";
		ResultSet rsSrc = null;
		msg = "☆☆☆☆☆ 所选组合在该期间没有【分级基金净值数据】，请核对后再重新导入 ☆☆☆☆☆ \r\n";// 返回导入是否成功的提示;

		String sBeginDate = "";
		String sFinishDate = "";
		String strSql = "";
		String curGroup = "";
		String curPort = "";
		String preTb = pub.getPrefixTB();
		try {
			insertSqlbuf.append(" insert into FUNDASSET_DT (id,financedate,fundcode,fundnav,fundqty,purchaseamount,purchaseqty,redemptionamount,");
			insertSqlbuf.append(" redemptionqty,alrdyearn,unit_nav,totalbenefit,management_fee,sale_fee,total_nav,");
			insertSqlbuf.append(" auto_flag,imp_date,imp_by,reconcileddate,reconciledby,system_date,fundhldcost,");
			insertSqlbuf.append(" fundhldmktvalue,bouns, proclaim_bouns,bounsbyday) values");
			insertSqlbuf.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			insertSql = insertSqlbuf.toString();
			
			pst = openPreparedStatement(insertSql);

			int count = 0;
			for (int i = 0; i < tmpPortCodes.length; i++) {
				if (tmpPortCodes[i].indexOf("-") > 0) {
					curGroup = tmpPortCodes[i].split("-")[0];
					curPort = tmpPortCodes[i].split("-")[1];
					pub.setPrefixTB(curGroup);
				} else {
					curGroup = pub.getAssetGroupCode();
					curPort = tmpPortCodes[i];
				}

				rsSrc = getQDIIData(curPort);

				while (rsSrc.next()) {
					if (count == 0) {
						flag = true;
						sBeginDate = YssFun.formatDate(rsSrc.getDate("financedate"));
					}
					nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
							.format(new java.util.Date());
					pst.setDouble(1, rsSrc.getDouble("id"));
					pst.setDate(2, rsSrc.getDate("financedate"));
					pst.setString(3, rsSrc.getString("fundcode"));
					pst.setDouble(4, rsSrc.getDouble("fundnav"));
					pst.setDouble(5, rsSrc.getDouble("fundqty"));
					pst.setDouble(6, rsSrc.getDouble("purchaseamount"));
					pst.setDouble(7, rsSrc.getDouble("purchaseqty"));

					pst.setDouble(8, rsSrc.getDouble("redemptionamount"));
					pst.setDouble(9, rsSrc.getDouble("redemptionqty"));
					pst.setDouble(10, rsSrc.getDouble("alrdyearn"));
					pst.setDouble(11, rsSrc.getDouble("unit_nav"));
					pst.setDouble(12, rsSrc.getDouble("totalbenefit"));
					pst.setDouble(13, rsSrc.getDouble("management_fee"));
					pst.setDouble(14, rsSrc.getDouble("sale_fee"));
					pst.setDouble(15, rsSrc.getDouble("total_nav"));

					pst.setString(16, rsSrc.getString("auto_flag"));
					pst.setString(17, rsSrc.getString("imp_date"));
					pst.setString(18, rsSrc.getString("imp_by"));
					pst.setString(19, rsSrc.getString("reconcileddate"));
					pst.setString(20, rsSrc.getString("reconciledby"));
					pst.setString(21, rsSrc.getString("system_date"));

					pst.setDouble(22, rsSrc.getDouble("fundhldcost"));
					pst.setDouble(23, rsSrc.getDouble("fundhldmktvalue"));
					pst.setDouble(24, rsSrc.getDouble("bouns"));
					pst.setDouble(25, rsSrc.getDouble("proclaim_bouns"));
					pst.setDouble(26, rsSrc.getDouble("bounsbyday"));

					pst.addBatch();
					count++;
					tmpNum++;
					sFinishDate = YssFun.formatDate(rsSrc.getDate("financedate"));
				}
				if (tmpNum > 0) {
					sMultiClassBuf.append(tmpPortCodes[i]).append(", ");
				} else {
					sMultiClassNoneBuf.append(tmpPortCodes[i]).append(", ");
				}
				tmpNum = 0;
				dbl.closeResultSetFinal(rsSrc);
			}
			if (flag) {
				con.setAutoCommit(false);
				pst.executeBatch();
				con.commit();
				bTrans = false;
				con.setAutoCommit(true);
			}
			if (count > 0) {
				msg = "";
				msg += msgMultiClass;
				if (!sMultiClassNoneBuf.toString().equals("")) {
					sTmpStr = sMultiClassNoneBuf.toString().substring(0,
							sMultiClassNoneBuf.toString().length() - 2);
					msg += "所选组合【" + sTmpStr + "】没有分级基金净值数据\r\f";
				}

				if (sBeginDate.equalsIgnoreCase(sFinishDate)) {
					msg += "★★★★★ 导入【" + sBeginDate + "日 "
							+ "分级基金净值数据】成功 ★★★★★ \r\n";
				} else {
					msg += "★★★★★ 导入【" + sBeginDate + " 至 " + sFinishDate
							+ "日 分级基金净值数据】成功 ★★★★★ \r\n";
				}
			}
			return msg;
		} catch (Exception e) {
			msg = "☆☆☆☆☆ 导入【分级基金净值数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心接口 ——分级基金净值数据接口：插入分级基金净值数据出错......】\t"
					+ msg);
		} finally {
			pub.setPrefixTB(preTb);
			dbl.closeResultSetFinal(rsSrc);
			closeStatementFinal(pst);
			endTransFinal(con, bTrans);
		}
	}

	public ResultSet getQDIIData(String portCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		StringBuffer strSqlBuf = new StringBuffer();
		try {
			strSqlBuf.append("SELECT to_number(to_char(m1.fnavdate, 'yyyyMMdd') || m1.fcurycode) id,");
			strSqlBuf.append("m1.fnavdate financedate,m1.fcurycode fundcode,0 fundnav,m1.fclassnetvalue fundqty,0 purchaseamount,");
			strSqlBuf.append(" 0 purchaseqty,0 redemptionamount,0 redemptionqty,0 alrdyearn,m2.fclassnetvalue unit_nav,");
			strSqlBuf.append(" 0 totalbenefit,0 management_fee,0 sale_fee,m3.fclassnetvalue total_nav,'A' auto_flag,");
			strSqlBuf.append(" to_char(m1.fnavdate, 'yyyyMMdd') imp_date,'H00239' imp_by,'' reconcileddate,'' reconciledby,");
			strSqlBuf.append(" '' system_date,0 fundhldcost,0 fundhldmktvalue,0 bouns,0 proclaim_bouns,0 bounsbyday from ");
			strSqlBuf.append(pub.yssGetTableName("tb_data_multiclassnet"));
			strSqlBuf.append(" m1, ");
			strSqlBuf.append(pub.yssGetTableName("tb_data_multiclassnet"));
			strSqlBuf.append(" m2, ");
			strSqlBuf.append(pub.yssGetTableName("tb_data_multiclassnet"));
			strSqlBuf.append(" m3 ");
			strSqlBuf.append("WHERE m1.fnavdate = m2.fnavdate AND m2.fnavdate = m3.fnavdate AND m1.fcurycode = m2.fcurycode AND m2.fcurycode = m3.fcurycode ");
			strSqlBuf.append("AND m1.fnavdate between ");
			strSqlBuf.append(dbl.sqlDate(sStartDate));
			strSqlBuf.append(" and ");
			strSqlBuf.append(dbl.sqlDate(sEndDate));
			strSqlBuf.append("AND m1.fportcode = ");
			strSqlBuf.append(dbl.sqlString(portCode));
			strSqlBuf.append(" AND m1.ftype = '05' AND m2.ftype = '02' AND m3.ftype = '08' ");
			strSql = strSqlBuf.toString();
			
			rs = dbl.openResultSet(strSql);
			return rs;
		} catch (Exception e) {
			msg = "☆☆☆☆☆ 导入【分级基金净值数据】失败 ☆☆☆☆☆ \r\n";
			throw new YssException("【数据中心接口 ——分级基金净值数据接口：获取分级基金净值数据出错......】\t"
					+ msg);
		}
	}

	// 根据组合代码获取相应的分级组合代码
	public String getQDIIFportclscode() throws YssException {
		String strSql = "";
		String fundcodeString = "";
		String curGroup = "";
		String curPort = "";
		StringBuffer strSqlBuf = new StringBuffer();

		ResultSet rs = null;
		try {
			for (int i = 0; i < tmpPortCodes.length; i++) {
				if (tmpPortCodes[i].indexOf("-") > 0) {
					curGroup = tmpPortCodes[i].split("-")[0];
					curPort = tmpPortCodes[i].split("-")[1];
					pub.setPrefixTB(curGroup);
				} else {
					curGroup = pub.getAssetGroupCode();
					curPort = tmpPortCodes[i];
				}

				strSqlBuf.append("select fportclscode from ");
				strSqlBuf.append(pub.yssGetTableName("TB_TA_PortCls"));
				strSqlBuf.append(" where fportcode = ");
				strSqlBuf.append(dbl.sqlString(curPort));
				strSql = strSqlBuf.toString();

				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					// ------add by hongqongbing 2013-12-19
					// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
					multiClassNum++;
					// ------end by hongqongbing 2013-12-19
					// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
					fundcodeBuf.append("'")
							.append(rs.getString("fportclscode")).append("'")
							.append(",");
				}
				strSqlBuf.setLength(0);
				dbl.closeResultSetFinal(rs);
				// ------add by hongqongbing 2013-12-19
				// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
				if (multiClassNum < 2) {
					sPortCodeBuf.append(tmpPortCodes[i]).append(", ");
				}
				multiClassNum = 0;
				// ------end by hongqongbing 2013-12-19
				// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
			}
			// ------add by hongqongbing 2013-12-19
			// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
			if (!sPortCodeBuf.toString().equals("")) {
				String sPortCode = sPortCodeBuf.toString().substring(0,
						sPortCodeBuf.toString().length() - 2);
				msgMultiClass = "☆☆☆☆☆ 所选组合【" + sPortCode
						+ "】下，没有分级组合 ☆☆☆☆☆ \r\n";
			}
			// ------end by hongqongbing 2013-12-19
			// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
			// ------edit by hongqingbing 2013-12-19
			// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
			if (!fundcodeBuf.toString().equals("")) {
				fundcodeString = fundcodeBuf.toString().substring(0,
						fundcodeBuf.toString().length() - 1);
			}
			// ------end by hongqingbing 2013-12-19
			// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
			return fundcodeString;
		} catch (Exception e) {
			// ------edit by hongqongbing 2013-12-19
			// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
			throw new YssException(
					"【数据中心接口 ——分级基金净值数据接口：查找组合对应分级组合出错......】\r\n"
							+ e.getMessage());
			// ------end by hongqongbing 2013-12-19
			// Bug_85763_华安基金_在数据中心同时导出多笔分级基金数据时会出现问题
		} 
		finally {
			dbl.closeResultSetFinal(rs);			
		}
	}

}
