package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.*;
import java.util.HashMap;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by guolongchao 2011.09.26
 * BUG 2771 QDV4建行2011年09月15日01_B.xls 
 * 当并发操作时，会提示插入日志表出错 *
 */
public class Ora1010045sp1 extends BaseDbUpdate {
	public Ora1010045sp1(){
		
	}
	
	/**
	 * add by guolongchao 2011.09.26
	 * BUG 2771 QDV4建行2011年09月15日01_B.xls 
	 * 当并发操作时，会提示插入日志表出错 *
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0045sp1 更新出错！", ex);
		}
	}
	
	/**
	 * add by guolongchao 2011.09.26
	 * BUG 2771 QDV4建行2011年09月15日01_B.xls 
	 * 当并发操作时，会提示插入日志表出错 *
	 */
	public void updateTable(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		ResultSet rs=null;
		String sTemp="";
        sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try{
			strSql=" select to_number(max(flogcode)) as flogcode from tb_sys_dayfinishlog";//查询出最大的系统日志表的主键ID
			rs=dbl.openResultSet(strSql);
			if(rs.next())
				sTemp=rs.getString("flogcode");			
			
            conn.setAutoCommit(bTrans);
            bTrans = true;			
			if(!dbl.yssSequenceExist("SEQ_SYS_LOGCODE")){
				sTemp=(sTemp!=null&&!sTemp.equals("")&&sTemp.length()>0)?sTemp.trim():"1";
				strSql = " create sequence SEQ_SYS_LOGCODE " +
						 "  minvalue 1" +
                         "  start with " +sTemp+
                         "  increment by 1 " + 
                         "  nocache order";
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
				updTables.append("SEQ_SYS_LOGCODE");
			}			
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0045sp1 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}
}
