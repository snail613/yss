package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;


public class Ora1010031sp1 extends BaseDbUpdate {
	public Ora1010031sp1() {
    }
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateFlow();
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0031sp1 更新出错！", ex);
		}
	}
	
	
	/**
	 * add by wangzuochun  MS01460   进入品种信息——指数期权信息设置，新建一条指数期权信息时，点击保存时报错    QDV4赢时胜(测试)2010年07月20日01_B 
	 * @方法名：updateFlow
	 * @参数：
	 * @返回类型：void
	 * @说明：更新tb_para_optioncontract表
	 */
	private void updateFlow() throws YssException {
		Connection conn = null;
		boolean bTrans = false;
		String altsql = "";
		
		ResultSet rs = null;
		try {
			conn = dbl.loadConnection();
			if (!existsTabColumn_Ora(pub.yssGetTableName("tb_para_optioncontract"), "FCatCode")) {
				altsql = "alter table " + pub.yssGetTableName("tb_para_optioncontract") + " drop column FCatCode";
				dbl.executeSql(altsql);
			}
			if (!existsTabColumn_Ora(pub.yssGetTableName("tb_para_optioncontract"), "FSubCatCode")) {
				altsql = "alter table " + pub.yssGetTableName("tb_para_optioncontract") + " drop column FSubCatCode";
				dbl.executeSql(altsql);
			}
			if (!existsTabColumn_Ora(pub.yssGetTableName("tb_para_optioncontract"), "FCuryCode")) {
				altsql = "alter table " + pub.yssGetTableName("tb_para_optioncontract") + " drop column FCuryCode";
				dbl.executeSql(altsql);
			}
			if (!existsTabColumn_Ora(pub.yssGetTableName("tb_para_optioncontract"), "FCountryCode")) {
				altsql = "alter table " + pub.yssGetTableName("tb_para_optioncontract") + " drop column FCountryCode";
				dbl.executeSql(altsql);
			}
			if (!existsTabColumn_Ora(pub.yssGetTableName("tb_para_optioncontract"), "FExchangeCode")) {
				altsql = "alter table " + pub.yssGetTableName("tb_para_optioncontract") + " drop column FExchangeCode";
				dbl.executeSql(altsql);
			}
			

			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新流程表出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
}
