package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by yeshenghong 2013-03-27 story #3736
 * */
public class Ora1010056sp8em1 extends BaseDbUpdate{
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			//add by yeshenghong story3715 20130320
			if(!dbl.yssTableExist("TB_NH_PAYFEEDATA"))
			{
				CreateNHPayFeeDataTable(hmInfo);
			}
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0056sp8em1更新出错！", ex);
		}
	}
	
	/**yeshenghong 2013-3-18 STORY 3715 */
	private void CreateNHPayFeeDataTable(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try {
			String strSql = " create table TB_NH_PAYFEEDATA ( " +
					"  ASSERTCODE VARCHAR2(20) not null, " +
					"  ASSERTNAME VARCHAR2(100) not null, " +
					"  FEEYEAR    NUMBER(4) not null, " +
					"  FEEMONTH   NUMBER(2) not null, " +
					"  FEETYPE    VARCHAR2(100) not null, " +
					"  FEED       NUMBER(24,8), " +
					"  FEEJ       NUMBER(24,8), " +
					"  FEEENDBAL  NUMBER(24,8), " +
					"  FEEPAYTYPE VARCHAR2(20) not null, " +
					"  FEEFLAG    VARCHAR2(1) not null, " +
					"  FEEBY      VARCHAR2(100) not null " +
					" )";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//主键
			strSql = " alter table TB_NH_PAYFEEDATA add constraint PK_TB_NH_PAYFEEDATA primary key (ASSERTCODE, FEEYEAR, FEEMONTH, FEETYPE, FEEBY) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TB_DAO_FILEMERGERNAME");
		} catch (Exception e) {
			throw new YssException("创建农行费用一览表数据表出错！");
		} 
	}
}
