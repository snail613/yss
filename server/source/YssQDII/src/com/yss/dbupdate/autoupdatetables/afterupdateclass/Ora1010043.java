package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.*;
import java.util.HashMap;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by songjie 2011.07.18
 * BUG 2274 QDV4建信2011年7月14日01_B
 * 在用户设置表中添加FUserID字段
 * @author 宋洁
 *
 */
public class Ora1010043 extends BaseDbUpdate {
	public Ora1010043(){
		
	}
	
	/**
     * add by songjie 2011.07.18
     * BUG 2274 QDV4建信2011年7月14日01_B
     * 在用户设置表中添加FUserID字段
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0043 更新出错！", ex);
		}
	}
	
	/**
     * add by songjie 2011.07.18
     * BUG 2274 QDV4建信2011年7月14日01_B
     * 在用户设置表中添加FUserID字段
	 */
	public void updateTable(HashMap hmInfo) throws YssException{
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
			
			if(!dbl.yssSequenceExist("SEQ_SYS_USERID")){
				strSql = " create sequence SEQ_SYS_USERID minvalue 1" +
                         " maxvalue 99999 start with 1 increment by 1 " + 
                         " nocache order";
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
				updTables.append("SEQ_SYS_USERID");
			}
			
			if(existsTabColumn_Ora(pub.yssGetTableName("tb_sys_userlist"),"FUserID")){
				strSql = " alter table tb_sys_userlist add fuserid number(5) ";
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
				
				strSql = " update tb_sys_userlist set FUserID = SEQ_SYS_USERID.NextVal ";
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
				
				updTables.append("tb_sys_userlist");
			}

            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0043 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
