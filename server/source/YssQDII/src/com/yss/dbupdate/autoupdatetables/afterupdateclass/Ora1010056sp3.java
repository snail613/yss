package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010056sp3 extends BaseDbUpdate{
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateInfo(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0056sp3更新出错！", ex);
		}
	}
	
	
	/**
	 * add by yeshenghong bug6240
	 * @param hmInfo
	 * @throws YssException
	 */
	private void updateInfo(HashMap hmInfo)throws YssException {
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try{
			strSql  = " update " + pub.yssGetTableName("Tb_TA_ClassFundDegree") + " set FStartDate = to_date(to_char(FStartDate,'yyyy-MM-dd'),'yyyy-MM-dd') ";
			sqlInfo.append(strSql);
			dbl.executeSql(strSql);
			updTables.append(pub.yssGetTableName("Tb_TA_ClassFundDegree"));	
		}catch(Exception e){
			throw new YssException("1.0.1.0056sp3更新表数据出错！",e);
		}
	}
}
