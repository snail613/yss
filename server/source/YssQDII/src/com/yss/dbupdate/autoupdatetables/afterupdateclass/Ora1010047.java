package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.util.HashMap;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by songjie 2012.09.29
 * BUG 5867 QDV4赢时胜(上海)2012年09月26日04_B
 */
public class Ora1010047 extends BaseDbUpdate {

	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0047 更新出错！", ex);
		}
	}
	
	public void updateTable(HashMap hmInfo) throws YssException{
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
            
            //若调度方案设置中的手工选项未被勾选，即默认打开调度方案执行界面时，该调度方案应自动被勾选，
            //应更新 FAutoRun = 1
            if(!existsTabColumn_Ora(pub.yssGetTableName("Tb_PFOper_SchProject"), "FAutoRun")){
            	strSql = " update " + pub.yssGetTableName("Tb_PFOper_SchProject") +
            	" set FAutoRun = '1' where FHandCheck = 0 ";
            	dbl.executeSql(strSql);
            	
            	sqlInfo.append(strSql);
            	updTables.append("Tb_PFOper_SchProject");		
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0047 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
