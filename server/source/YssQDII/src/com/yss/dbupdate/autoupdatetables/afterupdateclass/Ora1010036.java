package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010036 extends BaseDbUpdate {
	public Ora1010036(){
		
	}
	
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			alterTable(hmInfo); //变更证券借贷信息设置表结构  add by panjunfang 2010.12.6 
			alterRoleRightTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0036 更新出错！", ex);
		}
	}
	

	/**
	 * 变更证券借贷信息设置表结构  add by panjunfang 2010.12.6 
	 * 由于需求理解上的偏差，35sp1中证券借贷信息设置表中的计息起始日应该为varchar类型，且不为主键，由于证券借贷业务刚发布，且为测试阶段，因此可以不用考虑表中的数据，可直接drop重建
	 * @throws YssException
	 */
	public void alterTable(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = null;
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
			try {
				//如果存在此表
				if (dbl.yssTableExist(pub.yssGetTableName("TB_PARA_SECURITYLEND"))) {
					strSql = " drop table " + pub.yssGetTableName("TB_PARA_SECURITYLEND");//先删除原有表
					sqlInfo.append(strSql).append("\n");
					dbl.executeSql(strSql);

					//创建表
					strSql = " create table " + pub.yssGetTableName("TB_PARA_SECURITYLEND")
						  + "(FSECURITYCODE    VARCHAR2(20) not null,"
						  + "FBROKERCODE    VARCHAR2(20) not null,"
						  + "FSTARTDATE		VARCHAR2(20) not null,"
						  + "FPERIODCODE  VARCHAR2(20) not null,"
						  + "FROUNDCODE   VARCHAR2(20) not null,"
						  + "FCHECKSTATE   NUMBER(1) not null,"
						  + "FCREATOR      VARCHAR2(20) not null,"
						  + "FCREATETIME   VARCHAR2(20) not null,"
						  + "FCHECKUSER    VARCHAR2(20),"
						  + "FCHECKTIME    VARCHAR2(20), "
						  + "constraint PK_" + pub.yssGetTableName("TB_PARA_SECURITYLEND") + " primary key (FSECURITYCODE,FBROKERCODE))";
					sqlInfo.append(strSql).append("\n");
					dbl.executeSql(strSql);	
				}
			}catch (Exception e) {
				throw new YssException("版本1.0.1.0036 变更表结构出错！", e);
			}
			finally {
	            dbl.endTransFinal(conn, bTrans);
	        }
	 }
	
	/**变更角色权限表主键 add by licai 20101215 BUG #473 权限分配时，报错。
	 * @param hmInfo
	 * @throws YssException
	 */
	private void alterRoleRightTable(HashMap hmInfo)throws YssException {
		String strSql ="";
		Connection conn = dbl.loadConnection();
		boolean bTrans = true;
		String strPKName = "";//主键
		StringBuffer sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
		ResultSet rs=null;
		try {
			conn.setAutoCommit(false);
			String tableName = pub.yssGetTableNameForUpdTables("TB_SYS_ROLERIGHT");
			if(!dbl.yssTableExist(tableName + "_bak")){
				// 对于旧表做好备份
				strSql=" create table "+tableName+"_bak as select * from "+tableName;
				sqlInfo.append(strSql).append("\n");
				dbl.executeSql(strSql);
			}
			strPKName = getIsNullPKByTableName_Ora(tableName);//获取主键
            if (strPKName != null && strPKName.trim().length() > 0) {
                //删除约束
            	strSql="ALTER TABLE "+tableName+" DROP CONSTRAINT " + strPKName+" CASCADE";
            	sqlInfo.append(strSql).append("\n");
            	dbl.executeSql(strSql);
                //删除索引
            	 deleteIndex(strPKName);
            }
            //删除其他UNIQUE约束
            strSql="select index_name as indexName FROM user_indexes WHERE table_name = upper('TB_SYS_ROLERIGHT') and table_type ='TABLE' and uniqueness='UNIQUE'";
            sqlInfo.append(strSql).append("\n");
            rs=dbl.openResultSet(strSql);
            while(rs.next()){
            	deleteIndex(rs.getString("indexName"));
            }
            dbl.closeResultSetFinal(rs);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            //添加主键
            dbl.executeSql("ALTER TABLE "+tableName+" ADD CONSTRAINT PK_" + tableName +
                           " PRIMARY KEY (FROLECODE,FRIGHTCODE,FOPERTYPES)");
		} catch (Exception e) {
			throw new YssException("版本1.0.1.0036 变更表结构出错！", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
		
	}
}
