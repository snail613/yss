package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010060sp3 extends BaseDbUpdate  
{
	public void doUpdate(HashMap hmInfo) throws YssException {
		try 
		{
			/**Start 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001
			 * 更新“密码复杂度设置”表*/
			updatePassComplex(hmInfo);
			/**End 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001*/
			/*start by dongqingsong 2013-07-04 bug #8455  BUG8455电子划款指令设置中划款类型的长度太短*/
			updateTabledztype();
			updateCWdztype();
			/*end by dongqingsong 2013-07-04 bug #8455  BUG8455电子划款指令设置中划款类型的长度太短*/
		} 
		catch (Exception ex) 
		{
			throw new YssException("版本 1.0.1.0060sp3更新出错！", ex);
		}
	}
	
	/**
	 * 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001
	 * 更新密码复杂度设置的表结构，加入"重置初始密码"字段
	 * @param hmInfo
	 * @throws YssException
	 */
	private void updatePassComplex(HashMap hmInfo) throws YssException
	{
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		ResultSet rs = null;
		
		try
		{
			conn.setAutoCommit(false);
			bTrans = true;
			
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
			updTables = (StringBuffer) hmInfo.get("updatetables");
			
			strSql = "select * from Tb_Sys_PassComplex where 1 = 2";
			
			rs = dbl.openResultSet(strSql);
			
			if(!dbl.isFieldExist(rs, "FPwdReset"))
			{
				strSql = "alter table Tb_Sys_PassComplex add FPwdReset Varchar(50)";
				
				dbl.executeSql(strSql);		
				
				sqlInfo.append(strSql);
				updTables.append("Tb_Sys_PassComplex");
			}

			conn.commit();
	        conn.setAutoCommit(true);
	        bTrans = false;
		}
		catch(Exception ye)
		{
			throw new YssException(ye);
		}
        finally
        {
        	dbl.endTransFinal(conn, bTrans);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
	}
		/**
	 *add by dongqingsong 2013-07-04 bug #8455  BUG8455电子划款指令设置中划款类型的长度太短
	 * @author 修改 【tdztypecodepp】 表字段 [FHKTYPE] 的字段类型
	 */
	private void updateTabledztype() throws YssException {
		String strSql = "";
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		ResultSet rs = null;
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		try {
			
			if (dbl.yssTableExist("tdztypecodepp")) {
				//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
				rs = dbl.getUserTabColumns("tdztypecodepp","FHKTYPE");
				//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
				String dataType = rs.getString("DATA_TYPE");
				if(dataType.equals("VARCHAR2")){
					strSql = dbl.sqlAlterFieldModify("tdztypecodepp", "FHKTYPE", "varchar2(100)");
				}
				if(dataType.equals("NVARCHAR2")){
					strSql = dbl.sqlAlterFieldModify("tdztypecodepp", "FHKTYPE", "nvarchar2(100)");
				}
				
				
				dbl.executeSql(strSql);
			}
		} catch (Exception e) {
			throw new YssException("版本 1.0.1.0060sp3更新电子划款指令设置中划款类型的长度出错！", e);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		finally{
			dbl.closeResultSetFinal(rs);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
	}
	
	/**
	 *add by dongqingsong 2013-07-29 BUG8455电子划款指令设置中划款类型的长度太短
	 * @author 修改  财务系统中HKZL的划款类型的长度
	 */
	private void updateCWdztype() throws YssException {
		String strSql = "";
		String tableName ="";
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		ResultSet res= null;
		ResultSet rs = null;
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		try {
			//查询电子划款指令的套账表
			String setList = "select TABLE_NAME from user_tables tt where tt.TABLE_NAME  like 'A%JJHKZL'";
			//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			res = dbl.openResultSet(setList);
			//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			while(res.next()){
				tableName = res.getString("TABLE_NAME");
				if (dbl.yssTableExist(tableName)) {
				    //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
					rs = dbl.getUserTabColumns(tableName,"FHKTYPE");
					//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
					String dataType = rs.getString("DATA_TYPE");
					if(dataType.equals("VARCHAR2")){
						strSql = dbl.sqlAlterFieldModify(tableName, "FHKTYPE", "varchar2(100)");
					}
					if(dataType.equals("NVARCHAR2")){
						strSql = dbl.sqlAlterFieldModify(tableName, "FHKTYPE", "nvarchar2(100)");
					}
					//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
					dbl.closeResultSetFinal(rs);
					//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
					
					dbl.executeSql(strSql);
				}
			}
			
		} catch (Exception e) {
			throw new YssException("版本 1.0.1.0060sp3更新电子划款指令设置中划款类型的长度出错！", e);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		finally{
			dbl.closeResultSetFinal(res,rs);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
	}


}
