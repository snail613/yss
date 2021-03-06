package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/*
 * STORY 2007 #2007QDV411建行2011年12月09日01_A代码开发 
 * by yangshaokai 2011.12.31 对组合群字段数据的修改
 **/

public class Ora1010051sp1 extends BaseDbUpdate {
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateVocTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0051sp1 更新出错！", ex);
		}
	}

	public void updateVocTable(HashMap hmInfo) throws YssException {
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		ResultSet rs = null;
		String tStr = "";		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
		updTables = (StringBuffer) hmInfo.get("updatetables"); // 用于获取新建的表名
		ArrayList<String> tableList = new ArrayList<String>();
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			rs = dbl.openResultSet("SELECT TABLE_NAME FROM USER_TABLES where upper(table_name) like 'A%' and upper(table_name) like '%FCWVCH'");
		    while(rs.next()){
		    	tStr = rs.getString("TABLE_NAME");
		    	if(tStr!=null){
		    		tableList.add(tStr);
		    	}
		    }
		    dbl.closeResultSetFinal(rs);
		    if(tableList.size()>0)
		    {
				conn.setAutoCommit(bTrans);
				bTrans = true;
				
				for(Iterator iter=tableList.iterator();iter.hasNext();){
					String tableName = (String)iter.next();
					rs = dbl.openResultSet("select * from " + tableName + " where 1=2");
					if (dbl.yssTableExist(tableName)&&! dbl.isFieldExist(rs, "FConfirmer")) {
						strSql = " alter   table "   + tableName + " add (FConfirmer VARCHAR2(20))";
						dbl.executeSql(strSql);
						sqlInfo.append(strSql);
					}
					updTables.append(tableName);
					dbl.closeResultSetFinal(rs);
				}
			
				conn.commit();
				conn.setAutoCommit(bTrans);
				bTrans = false;
		    }
		} catch (Exception e) {
			throw new YssException("1.0.1.0051sp1 更新表数据出错！", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}
}
