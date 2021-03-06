package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010035 extends BaseDbUpdate {
	public Ora1010035(){
		
	}
	
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			alterTable(); //变更表结构  add by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
			updateCusconfig();//add by yanghaiming
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0035 更新出错！", ex);
		}
	}
	
	/**
	 * add by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
	 * @throws YssException
	 */
	public void alterTable() throws YssException {
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
			try {
				//如果存在此表
				if (dbl.yssTableExist("TB_PFSYS_INOUTCFG")) {
					
					if (dbl.yssTableExist("TB_PFSYS_INOUTCFG_BAK")){
						strSql = " drop table TB_PFSYS_INOUTCFG_BAK ";
						dbl.executeSql(strSql);
					}

					//创建表
					strSql = " create table TB_PFSYS_INOUTCFG_BAK ("
						  + "FINOUTCODE    VARCHAR2(20) not null,"
						  + "FINOUTNAME    VARCHAR2(50) not null,"
						  + "FOUTCFGSCRIPT CLOB not null,"
						  + "FINCFGSCRIPT  CLOB not null,"
						  + "FDESC         VARCHAR2(100),"
						  + "FCHECKSTATE   NUMBER(1) not null,"
						  + "FCREATOR      VARCHAR2(20) not null,"
						  + "FCREATETIME   VARCHAR2(20) not null,"
						  + "FCHECKUSER    VARCHAR2(20),"
						  + "FCHECKTIME    VARCHAR2(20), "
						  + "constraint TB_PFSYS_INOUTCFG_BAK primary key (FINOUTCODE))";
					dbl.executeSql(strSql);
					
					strSql = " insert into TB_PFSYS_INOUTCFG_BAK select * from TB_PFSYS_INOUTCFG";
					dbl.executeSql(strSql);
					
					strSql = " drop table TB_PFSYS_INOUTCFG";
					dbl.executeSql(strSql);
				
//					//给表重命名
//					strSql = " alter table TB_PFSYS_INOUTCFG rename to TB_PFSYS_INOUTCFG_BAK ";
//					dbl.executeSql(strSql);
//					
//					//删除原来的约束
//					strSql = " alter table TB_PFSYS_INOUTCFG_BAK drop constraint PK_TB_PFSYS_INOUTCFG";
//					dbl.executeSql(strSql);
//					
//					//增加新的约束
//					strSql = " alter table TB_PFSYS_INOUTCFG_BAK " 
//						  + "add constraint PK_TB_PFSYS_INOUTCFG_BAK primary key (FINOUTCODE)";
//					dbl.executeSql(strSql);
					
					//创建表
					strSql = " create table TB_PFSYS_INOUTCFG ("
						  + "FINOUTCODE    VARCHAR2(20) not null,"
						  + "FINOUTNAME    VARCHAR2(50) not null,"
						  + "FOUTCFGSCRIPT CLOB not null,"
						  + "FINCFGSCRIPT  CLOB not null,"
						  + "FDESC         VARCHAR2(100),"
						  + "FCHECKSTATE   NUMBER(1) not null,"
						  + "FCREATOR      VARCHAR2(20) not null,"
						  + "FCREATETIME   VARCHAR2(20) not null,"
						  + "FCHECKUSER    VARCHAR2(20),"
						  + "FCHECKTIME    VARCHAR2(20), "
						  + "constraint PK_TB_PFSYS_INOUTCFG primary key (FINOUTCODE))";
					dbl.executeSql(strSql);
					
					strSql = " insert into TB_PFSYS_INOUTCFG select * from TB_PFSYS_INOUTCFG_BAK";
					dbl.executeSql(strSql);
					
				}
			}catch (Exception e) {
				throw new YssException("版本1.0.1.0035 变更表结构出错！", e);
			}
			finally {
	            dbl.endTransFinal(conn, bTrans);
	        }
	 }
	
	private void updateCusconfig() throws YssException {
		String sql = "";
		try {
			if (dbl.yssTableExist(pub.yssGetTableName("tb_dao_cusconfig"))) {
				sql = " update " + pub.yssGetTableName("tb_dao_cusconfig") + 
					  " set FEXCELPWD='0' where FFILETYPE = 'xls'";
				dbl.executeSql(sql);
			}
		} catch (Exception ex) {
			throw new YssException("更新自定义接口设置xls历史数据出错！", ex);
		}
	}
}
