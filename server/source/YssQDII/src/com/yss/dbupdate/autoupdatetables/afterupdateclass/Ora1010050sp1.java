package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/*
 * STORY 2007 #2007QDV411建行2011年12月09日01_A代码开发 
 * by yangshaokai 2011.12.31 对组合群字段数据的修改
 **/

public class Ora1010050sp1 extends BaseDbUpdate {
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateVocTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0050sp1 更新出错！", ex);
		}
	}

	public void updateVocTable(HashMap hmInfo) throws YssException {
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		ResultSet rs = null;
		String selSql = "";
		String tStr = "";		
		/**shashijie 2012-7-2 STORY 2475 */
		//StringBuffer tBuffer = new StringBuffer();
		/**end*/
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
		updTables = (StringBuffer) hmInfo.get("updatetables"); // 用于获取新建的表名
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			conn.setAutoCommit(bTrans);
			bTrans = true;
			if (!dbl.yssTableExist("DayClosed")) {
				strSql = "create table DayClosed " +
				"(" +
				 "FSetCode Varchar(20) not null," +
				 "FClosedDate Date not null," +
				 "Fbzm varchar(10) not null," +
				 "fdcbz int default 0 null," +
				 "fdcuser Varchar(20) default ' ' null," +
				 "fDCtime Date null," +
				 "fType Varchar2(50) null" +
				 ")";
				dbl.executeSql(strSql);

				sqlInfo.append(strSql);

				strSql = "alter table DayClosed add constraint PK_DayClosed primary key (FSetCode,FClosedDate,Fbzm,fdcbz,fType)";

				dbl.executeSql(strSql);

				sqlInfo.append(strSql);

			}
			updTables.append("DayClosed");		
			
			if (!dbl.yssTableExist("InfoResult")) {
				strSql = "create table InfoResult "+
						"( "+
						 "FSn varchar2(30) not null, "+
						 "FInfo varchar2(100) not null, "+
						 "fDCUser varchar2(50) default ' ' null, "+
						 "fTime Date not null, "+
						 "fType Varchar2(50) null "+
						")";
				
				dbl.executeSql(strSql);

				sqlInfo.append(strSql);
				
				strSql = "alter table InfoResult add constraint PK_InfoResult primary key (FSn,fTime)";

				dbl.executeSql(strSql);
				
				sqlInfo.append(strSql);
			}

			updTables.append("InfoResult");

			if (!dbl.yssTableExist("LockStatus")) {
				strSql = "create table LockStatus "+
						 "( "+
						 "FSn varchar2(30) not null, "+
						 "fStatus varchar2(20) not null, "+
						 "FSetCode varchar2(20) not null, "+
						 "fTime Date not null, "+
						 "fDcuser Varchar2(50) null "+
						 ")";
				
				dbl.executeSql(strSql);

				sqlInfo.append(strSql);
				
				strSql = "alter table LockStatus add constraint PK_LockStatus primary key (FSn,FSetCode,fTime)";

				dbl.executeSql(strSql);
				
				sqlInfo.append(strSql);
			}
			updTables.append("LockStatus");
			
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		} catch (Exception e) {
			throw new YssException("1.0.1.0050sp1 更新表数据出错！", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}
}
