package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by huangqirong 2013-01-25 story #3488
 * */
public class Ora1010056sp7 extends BaseDbUpdate{
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			deleteMenubar(); //add by zhaoxianlin #story 3208 20121205
			deleteRightType();//add by zhaoxianlin #story 3208 20121205
			this.addFields();
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0056sp7更新出错！", ex);
		}
	}
	
	private void addFields(){
		ResultSet rs = null;
		try {
			rs = dbl.openResultSet("select * from " + pub.yssGetTableName("Tb_Para_InvestPay"));
			if(!dbl.isFieldExist(rs, "FLowerCurrencyCode")){
				dbl.executeSql("alter table " + pub.yssGetTableName("Tb_Para_InvestPay") + " add FLowerCurrencyCode VARCHAR2(20) default ' '");
			}
			
			if(!dbl.isFieldExist(rs, "FSupplementDates")){
				dbl.executeSql("alter table " + pub.yssGetTableName("Tb_Para_InvestPay") + " add FSupplementDates varchar2(1000)");
			}
			
		} catch (Exception e) {
			System.out.println("在投资运营收支设置增加字段出错：" + e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
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
