package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.*;
import java.util.HashMap;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by panjunfang 2011.08.16
 *
 */
public class Ora1010043sp2 extends BaseDbUpdate {
	public Ora1010043sp2(){
		
	}
	
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			deleteVocabulary(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0043sp2 更新出错！", ex);
		}
	}
	
	/**
     * add by panjunfang 2011.08.16
     * STORY #1428 QDV411建行2011年07月26日01_A
     * 删除菜单fbarcode = ‘repexport’ 报表批量导出（为太平资产专有菜单，主流中不需要，因此在此删除）
     * 原有菜单“工银报表批量导出” 更名为“报表批量导出”（fbarcode = ‘ICBCrepexport’），用YssIMSASTool工具导出
	 */
	public void deleteVocabulary(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
        sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try{
            conn.setAutoCommit(bTrans);
            bTrans = true;

            strSql = "DELETE FROM TB_FUN_MENUBAR WHERE FBARCODE IN('repexport')";
			sqlInfo.append(strSql);
			dbl.executeSql(strSql);
			updTables.append("TB_FUN_MENUBAR");
			
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0043sp2 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
