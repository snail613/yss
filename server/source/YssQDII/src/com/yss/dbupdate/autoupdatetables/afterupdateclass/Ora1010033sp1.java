package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**************************************************
 * @author  fangjiang  2010.10.13  MS01847 权限设置中，综合业务菜单有两个
 */
public class Ora1010033sp1 extends BaseDbUpdate {
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			deleteMenubar();
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0033sp1 更新出错！", ex);
		}
	}
		
	private void deleteMenubar() throws YssException{
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = " delete from tb_fun_menubar where fbarcode = 'integrated' ";					
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除菜单条出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	
}
