package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.*;
import java.util.*;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by songjie 2013.02.18 
 * BUG 7102 QDV4招商银行2013年02月17日01_B
 * 将表字段名为 FSECURITYCODE 的字段长度扩充为 varchar2(50)
 * @author 宋洁
 *
 */
public class Ora1010059sp1 extends BaseDbUpdate  {
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			modifyColumn(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0059sp1更新出错！", ex);
		}
	}
	
	/**
	 * add by songjie 2013.02.18 
	 * BUG 7102 QDV4招商银行2013年02月17日01_B
	 * 将表字段名为 FSECURITYCODE 的字段长度扩充为 varchar2(50)
	 * @param hmInfo
	 * @throws YssException
	 */
	private void modifyColumn(HashMap hmInfo)throws YssException {
		String strSql = "";
		ResultSet rs = null;
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		Statement stat = null;
		Connection conn = null;
		int i = 0;
		String assetGroupCode = "";
		ArrayList al = new ArrayList();
		String tableName = "";
		boolean bTrans = true;
		try{
			conn = dbl.loadConnection();
			stat = conn.createStatement();
			
			conn.setAutoCommit(false);
			
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
	        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
			
			strSql = " select 'alter table '|| TABLE_NAME || ' modify FSecurityCode varchar2(50)' as EXESTR,TABLE_NAME FROM " +
					 " user_tab_columns WHERE COLUMN_NAME = 'FSECURITYCODE' and Data_length < 50 and INSTR(TABLE_NAME,'BIN$') = 0";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				i++;
				if(dbl.yssTableExist(rs.getString("TABLE_NAME"))){
					stat.addBatch(rs.getString("EXESTR"));
				
					updTables.append(rs.getString("TABLE_NAME"));	
					sqlInfo.append(rs.getString("EXESTR"));
				}
			}
			if(i > 0){
				stat.executeBatch();
			}
			
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeResultSetFinal(rs);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			
			strSql = " select distinct FAssetGroupCode from Tb_sys_assetGroup ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				al.add(rs.getString("FAssetGroupCode"));
			}
			
			dbl.closeResultSetFinal(rs);
			
			for(int j = 0; j < al.size(); j++){
				assetGroupCode = (String)al.get(j);
				tableName = "tb_" + assetGroupCode + "_VCH_DATAENTITY";
				if(dbl.yssTableExist(tableName)){
					strSql = " alter table " + tableName + " modify FASSISTANT VARCHAR2(50) ";
					dbl.executeSql(strSql);
	                sqlInfo.append(strSql);
	                
					strSql = " alter table " + tableName + " modify FRESUME VARCHAR2(100) ";
					dbl.executeSql(strSql);
					sqlInfo.append(strSql);
					
					updTables.append(tableName);	
				}
			}
			
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("版本 1.0.1.0059sp1更新出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(stat);
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
