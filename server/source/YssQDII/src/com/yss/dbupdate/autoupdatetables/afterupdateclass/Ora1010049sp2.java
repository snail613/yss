package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010049sp2 extends BaseDbUpdate {

	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateVocTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0049sp2 更新出错！", ex);
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
            //删除自定义接口配置多余的文件类型
			strSql = " delete from Tb_Fun_Vocabulary where FVocTypeCode = " + 
			"'dao_Inface_FileType' and FVocCode in('0','1','2') ";
			
            sqlInfo.append(strSql);
            dbl.executeSql(strSql);
            
			updTables.append("Tb_Fun_Vocabulary");			
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0049sp2 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
