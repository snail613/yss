package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by songjie 2012.09.10 
 * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 
 * 添加日志序列
 */
public class Ora1010057 extends BaseDbUpdate {
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			addSequence(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0057更新出错！", ex);
		}
	}

	private void addSequence(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		String strSql = "";
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		ResultSet rs = null;
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		try {
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
			updTables = (StringBuffer) hmInfo.get("updatetables"); // 用于获取新建的表名

			if (!dbl.yssSequenceExist("SEQ_SYS_LOGCODE_SUM")) {
				strSql = " create sequence SEQ_SYS_LOGCODE_SUM " + 
		         " minvalue 1 maxvalue 9999999999999999999 " + 
				 " start with 1 increment by 1 cache 20 order";
				
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				updTables.append("SEQ_SYS_LOGCODE_SUM");	
			}
			
			if (existsTabColumn_Ora("T_Plugin_Log", "FLogSumCode")){
				strSql = " alter table T_Plugin_Log add FLogSumCode varchar2(50) ";
				
				//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A
				dblBLog.executeSql(strSql);
				sqlInfo.append(strSql);
				updTables.append("T_Plugin_Log");
			}
			
			//add by huangqirong 2012-09-10 story #2822
			//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			rs = dbl.openResultSet("select * from " + pub.yssGetTableName("tb_Data_RateTrade"));	
			//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			if(!dbl.isFieldExist(rs, "FBAILTYPE")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_RateTrade") +" add FBAILTYPE VARCHAR2(20) default ' ' not null ");
			}
			if(!dbl.isFieldExist(rs, "FBAILSCALE")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_RateTrade") +" add FBAILSCALE NUMBER(18,12) ");
			}
			if(!dbl.isFieldExist(rs, "FBAILFIX")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_RateTrade") +" add FBAILFIX NUMBER(18,4) ");
			}
			if(!dbl.isFieldExist(rs, "FBAILCashCode")){
				dbl.executeSql("alter table " + pub.yssGetTableName("tb_Data_RateTrade") +" add FBAILCashCode varchar2(50) ");
			}			
			//---end---
			
		} catch (Exception e) {
			throw new YssException("添加序列 SEQ_SYS_LOGCODE_SUM 出错");
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		finally{
			dbl.closeResultSetFinal(rs);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		/**shashijie 2012-9-12 BUG 5571 删除调度方案设置中的错误数据 */
		deletePFOper_SchProject();
		/**end shashijie 2012-9-12 BUG */
	}

	/**shashijie 2012-9-12 BUG 5571 删除调度方案设置中的错误数据  */
	private void deletePFOper_SchProject() throws YssException {
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try {
			String query = "Delete From "+pub.yssGetTableName("Tb_Pfoper_Schproject")+" a" +
					" Where (a.Fhandcheck = 0 Or a.Fhandcheck Is Null)" +
					" And Instr(a.FATTRCODE,'pfoper_incometype','1') > 0" +
					" And a.ffunmodules = 'valcheck'";
			dbl.executeSql(query);
			conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		} catch (Exception e) {
			throw new YssException("删除调度方案设置中的错误数据出错");
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
