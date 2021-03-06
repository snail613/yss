package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;
/**************************************************
 * @author  zhaoxianlin   20121205  #story 3208 删除抵押物信息设置  抵押物补交数据 菜单条及权限
 */
public class Ora1010056sp3em1 extends BaseDbUpdate{
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			deleteMenubar(); 
			deleteRightType();
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0056sp3更新出错！", ex);
		}
	}
	/**add by zhaoxianlin 20121205 删除抵押物信息设置及抵押物补交数据 菜单条 银华卖空  */
	private void deleteMenubar() throws YssException{
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = " delete from tb_fun_menubar where fbarcode in ('collaterat','collateraladd')  ";					
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
	/**add by zhaoxianlin 20121205 删除抵押物信息设置及抵押物补交数据 权限类型  银华卖空  */
	private void deleteRightType() throws YssException{
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = " delete from Tb_Sys_RightType where frighttypecode in ('FrmCollateral','FrmCollateralAdd')  ";					
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除权限类型出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
}
