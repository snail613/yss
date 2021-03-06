package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/*
 * STORY 2007 #2007QDV411建行2011年12月09日01_A代码开发 
 * by yangshaokai 2011.12.31 对组合群字段数据的修改
 **/

public class Ora1010050 extends BaseDbUpdate {
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateVocTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0050 更新出错！", ex);
		}
	}

	public void updateVocTable(HashMap hmInfo) throws YssException {
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		ResultSet rs = null;
		String selSql = "";
		String tStr = "";
		StringBuffer tBuffer = new StringBuffer();
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
		updTables = (StringBuffer) hmInfo.get("updatetables"); // 用于获取新建的表名
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			conn.setAutoCommit(bTrans);
			bTrans = true;
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Dao_Pretreat"))) {
				//---add by songjie 2012.03.14 BUG 4001 QDV4建行2012年3月8日01_B start---//
				strSql = " alter table " + pub.yssGetTableName("Tb_Dao_Pretreat") + 
				" modify FMGroupShare varchar2(200) ";
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				//---add by songjie 2012.03.14 BUG 4001 QDV4建行2012年3月8日01_B end---//
				
				strSql = " update " + pub.yssGetTableName("Tb_Dao_Pretreat")
						+ " set FMGroupShare = '' where FMGroupShare = 'false'";
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);

				selSql = "select fAssetGroupCode from tb_sys_assetgroup";
				rs = dbl.openResultSet(selSql);
				while (rs.next()) {
					tBuffer.append(rs.getString("fAssetGroupCode"))
							.append(",");
				}
				tStr = tBuffer.substring(0, tBuffer.length() - 1);
				
				strSql = " update " + pub.yssGetTableName("Tb_Dao_Pretreat")
						+ " set FMGroupShare = " + dbl.sqlString(tStr)
						+ "where FMGroupShare = 'true'";

				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
			}
			updTables.append("tb_dao_pretreat");
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		} catch (Exception e) {
			throw new YssException("1.0.1.0050 更新表数据出错！", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}
}
