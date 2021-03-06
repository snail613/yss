package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;
	/* story 1938 QDV4赢时胜(上海开发部)2011年11月28日01_A
	 * by zhouwei 对词汇进行更新 20111123
	 * */
public class Ora1010048 extends BaseDbUpdate {

	
	//by zhouwei 20111221 对词汇进行更新
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateVocTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0048 更新出错！", ex);
		}
	}
	public void updateVocTable(HashMap hmInfo) throws YssException{
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
            //删除业务处理中的分红转投业务（词汇）
            strSql=" delete from  tb_fun_vocabulary where fvoccode ='DividendInvest' and fvoctypecode='val_business'";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("tb_fun_vocabulary");			
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0048 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
