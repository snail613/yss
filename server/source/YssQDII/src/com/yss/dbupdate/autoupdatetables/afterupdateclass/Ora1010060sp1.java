package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**add---liubo 2013-5-22 STORY 3975 更新60sp1版本*/
public class Ora1010060sp1 extends BaseDbUpdate
{

	public void doUpdate(HashMap hmInfo) throws YssException 
	{
		try 
		{
			/**add---liubo 2013-5-22 STORY 3975  变更“电子对账对账报文处理信息表（TDzbbinfo）”的主键约束，改为联合主键*/
			updateTDzbbinfo(hmInfo);
			/**end---liubo 2013-5-22 STORY 3975*/
		} 
		catch (Exception ex) 
		{
			throw new YssException("版本 1.0.1.0060sp1更新出错！", ex);
		}
	}

	/**add---liubo 2013-5-22 STORY 3975  变更“电子对账对账报文处理信息表（TDzbbinfo）”的主键约束，改为联合主键*/
	private void updateTDzbbinfo(HashMap hmInfo) throws YssException
	{
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		StringBuffer updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			conn.setAutoCommit(false);
			bTrans = true;
			
			if(dbl.yssTableExist("TDZBBINFO"))
			{
				//删除已有主键约束
				strSql = "alter table TDZBBINFO drop constraint PK_TDZBBINFO";
				
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				//若存在唯一索引，也要删除。避免后面在创建主键约束是报错
				strSql = "select * from user_indexes where Index_Name = 'PK_TDZBBINFO'";
				rs = dbl.queryByPreparedStatement(strSql);
				if(rs.next())
				{
					strSql = "drop index PK_TDZBBINFO";
					dbl.executeSql(strSql);
					sqlInfo.append(strSql);
				}
				dbl.closeResultSetFinal(rs);
				
				//创建新的联合主键约束
				strSql = "alter table TDZBBINFO add constraint PK_TDZBBINFO primary key (FSN,FFileType)";

				dbl.executeSql(strSql);
				sqlInfo.append(strSql);

				updTables.append("TDZBBINFO");
			}

			conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
			
		}
		catch(Exception ye)
		{
			throw new YssException();
		}
		finally
		{
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
		}
		
		
	}
}
