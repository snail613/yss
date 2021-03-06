package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010052sp1 extends BaseDbUpdate {

	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateVocTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0052sp1 更新出错！", ex);
		}
	}
	
	/**
	 * 删除无用菜单及对应功能调用
	 * @param hmInfo
	 * @throws YssException
	 */
	public void updateVocTable(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try{
			
			strSql  =  " delete from tb_fun_vocabulary t where t.fvoctypecode = 'vch_Sign' and t.fvoccode = '7' ";
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlInfo.append(strSql);
            dbl.executeSql(strSql);
    			
			strSql = " delete from tb_fun_vocabulary t where t.fvoctypecode = 'vch_ConReal' and t.fvoccode in('0','1')"; 
			sqlInfo.append(strSql);// add by yeshenghong 2012054 BUG4395
            dbl.executeSql(strSql);
            updTables.append("tb_fun_vocabulary");	
            
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0052sp1 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
