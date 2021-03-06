package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.util.HashMap;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;


public class Ora1010032 extends BaseDbUpdate {
	public Ora1010032() {
    }
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			deleteVoc();
			updateRightsIssue();
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0032 更新出错！", ex);
		}
	}
	
	
	/**
	 * add by wangzuochun  2010.08.11  MS01462    进入库存信息配置，新建时,选择库存类型下拉框,有两个相同的运营收支款  QDV4赢时胜(测试)2010年7月20日1_B 
	 * @方法名：deleteVoc
	 * @参数：
	 * @返回类型：void
	 * @说明：更新tb_para_optioncontract表
	 */
	private void deleteVoc() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			
			strSql = " delete  from tb_fun_vocabulary "
				   + " where fvoctypecode = 'scg_type' and fvoccode = 'investpayrec'";		
			
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除词汇出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	/**
	 * add by yanghaiming  2010.08.19  更新配股权益历史数据   交易方式为22 权证送配
	 * @方法名：updateRightsIssue
	 * @参数：
	 * @返回类型：void
	 * @说明：更新Tb_Data_RightsIssue表
	 */
	private void updateRightsIssue() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			
			strSql = "update " + 
				pub.yssGetTableName("Tb_Data_RightsIssue") +
				" set FTRADECODE = '22' ,FTRADENAME = '权证送配' where FTRADECODE is null";		
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新配股权益出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
}
