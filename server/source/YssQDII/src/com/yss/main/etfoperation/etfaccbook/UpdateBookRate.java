package com.yss.main.etfoperation.etfaccbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 此类为更新台账报表汇率的操作类
 * 
 * @author xuqiji 20091102
 * 
 */
public class UpdateBookRate extends CtlETFAccBook {

	public UpdateBookRate() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 更新ETF基金换汇汇率
	 */
	public String UpdateChangeRate(String type) throws YssException {
		String sUpdateState = "true";
		String[] sType = null;
		StringBuffer buff = null;
		ResultSet rs = null;
		String[] sData = null;
		String[] sPortCode = null;
		PreparedStatement pst = null;
		Connection conn = dbl.loadConnection();
		boolean bTrans = true;
		try {
			buff = new StringBuffer(1000);
			sType = type.split("/t");// 解析前台传来的数据
			sData = sType[1].split("\f\f");
			conn.setAutoCommit(false);
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_bookexratedata"));
			buff.append(" where FPortCode in (").append(this.operSql.sqlCodes(sData[3])).append(")");
			buff.append(" and FBookType =").append(dbl.sqlString(sData[2]));
			buff.append(" and FBuyDate =").append(dbl.sqlDate(sData[1]));

			dbl.executeSql(buff.toString());
			buff.delete(0, buff.length());

			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_bookexratedata"));
			buff.append(" (FPortCode,FBookType,FBuyDate,FExRateDate,FStockholderCode,FSecurityCode,");
			buff.append(" FExRateValue,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime");
			buff.append(" )values(?,?,?,?,?,?,?,?,?,?,?,?)");

			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0, buff.length());
			sPortCode = sData[3].split(",");
			for (int i = 0; i < sPortCode.length; i++) {
				pst.setString(1, sPortCode[i]);
				pst.setString(2, sData[2]);
				pst.setDate(3, YssFun.toSqlDate(sData[1]));
				pst.setDate(4, YssFun.toSqlDate(sData[4]));
				pst.setString(5, " ");
				pst.setString(6, " ");
				pst.setDouble(7, Double.parseDouble(sData[0]));
				pst.setInt(8, 1);
				pst.setString(9, pub.getUserCode());
				pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
				pst.setString(11, pub.getUserCode());
				pst.setString(12, YssFun.formatDatetime(new java.util.Date()));

				pst.addBatch();
			}
			pst.executeBatch();
			conn.commit();
			bTrans = true;
			conn.setAutoCommit(true);
			
			//更新台帐表中的实际汇率
			//upBookFactRate(sData);
		} catch (Exception e) {
			throw new YssException("更新ETF基金换汇汇率出错！", e);
		} finally {
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
		}
		return sUpdateState;
	}

	/**
	 * 更新台帐表中的实际汇率
	 * 
	 * @throws YssException
	 * 
	 */
	public void upBookFactRate(String [] sData)throws YssException {
		ResultSet rs = null;
		Connection conn = null;
		boolean bTrans = true;
		StringBuffer buff = null;
		String[] type = null; 
		Statement st = null;
		try {
			conn = dbl.loadConnection();
			buff = new StringBuffer(1000);
			conn.setAutoCommit(false);
			
			buff.append(" select * from ").append(pub.yssGetTableName("tb_etf_bookexratedata"));
			buff.append(" where FPortCode in( ").append(this.operSql.sqlCodes(sData[3])).append(")");
			buff.append(" and FBookType = ").append(dbl.sqlString(sData[2]));
			buff.append(" and FBuyDate = ").append(dbl.sqlDate(sData[1]));
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());

			st = dbl.openStatement();
			while(rs.next()) {
				
				buff.append(" update ").append(pub.yssGetTableName("Tb_ETF_StandingBook"));
				buff.append(" set FFactExRate = ").append(rs.getDouble("FExRateValue"));
				buff.append(" , FExRateDate =").append(dbl.sqlDate(rs.getDate("FExRateDate")));
				buff.append(" where FPortCode =").append(dbl.sqlString(rs.getString("FPortCode")));
				buff.append(" and FBs =").append(dbl.sqlString(rs.getString("FBookType")));
				buff.append(" and FBuyDate = ").append(dbl.sqlDate(rs.getDate("FBuyDate")));

				st.addBatch(buff.toString());
				buff.delete(0, buff.length());
			}
			st.executeBatch();
			conn.commit();
			bTrans =false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新台帐表中的实际汇率出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
			dbl.closeStatementFinal(st);
		}
	}
}
