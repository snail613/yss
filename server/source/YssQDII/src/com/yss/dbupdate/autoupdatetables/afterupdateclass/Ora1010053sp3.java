package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * @author add by zhangjun 2012.06.13 Story#2459
 *
 */
public class Ora1010053sp3 extends BaseDbUpdate{

	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			
			updateStatisticalParameters(hmInfo);
			
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0053sp3更新出错！", ex);
		}
	}
	
	public void updateStatisticalParameters(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
		
		try{
			conn.setAutoCommit(bTrans);
            bTrans = true;
			if(dbl.yssTableExist("StatisticalParameters") )
			{	
				strSql = "alter table STATISTICALPARAMETERS modify FTZGLR VARCHAR2(100)";
	            sqlInfo.append(strSql);
	            dbl.executeSql(strSql);
			
				strSql = "alter table STATISTICALPARAMETERS modify FSETCODE NUMBER(22)";
	            sqlInfo.append(strSql);
	            dbl.executeSql(strSql);
	            
	            updTables.append("StatisticalParameters");	
			}
				
			conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0053sp3更新表数据出错！", e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
			
		}	
	}
		
		
}
