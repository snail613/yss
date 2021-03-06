package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010056 extends BaseDbUpdate{
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
		    addSequence();
			updateInfo(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0056更新出错！", ex);
		}
	}
	
	
	
	/**
	 *  add by jsc 2012.08.20
	 * 添加凭证序列
	 */
	private void addSequence()throws YssException {
	     
		 try{
		   if (!dbl.yssSequenceExist("SEQ_VCH_DATA")){
		      dbl.executeSql("create sequence SEQ_VCH_DATA minvalue 1 maxvalue 999999999 start with 1 increment by 1 cache 10");
		   }
		   
		 }catch(Exception e){
		   throw new YssException("添加序列 SEQ_VCH_DATA 出错");
		 }
	     
	}
	
	/**
	 * add by songjie 2012.08.02
	 * STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A
	 * 预警系统相关表结构
	 * @param hmInfo
	 * @throws YssException
	 */
	private void updateInfo(HashMap hmInfo)throws YssException {
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try{
			if(!dbl.yssSequenceExist("SEQ_WP_LOG"))//记录号从索引中取
			{
				strSql = " create sequence SEQ_WP_LOG minvalue 1 maxvalue 999999999999999999999999999" +
                         " start with 1 increment by 1 nocache order";
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssTableExist("T_PLUGIN")){
				strSql = " create table T_PLUGIN "+
                         "("+
                         " C_PLUGIN_CODE       VARCHAR2(50) not null, "+
                         " C_PLUGIN_NAME       VARCHAR2(50) not null, "+
                         " C_PLUGIN_TYPECODE   VARCHAR2(20) not null, "+
                         " C_PLUGIN_TYPENAME   VARCHAR2(50) not null, "+
                         " C_PLUGIN_RANGECODE  VARCHAR2(20) not null, "+
                         " C_PLUGIN_RANGENAME  VARCHAR2(50) not null, "+
                         " C_PLUGIN_DESC       VARCHAR2(200) not null, "+
                         " C_PLUGIN_ITEMS      VARCHAR2(2000), "+
                         " C_PLUGIN_CONDITION  VARCHAR2(2000), "+
                         " C_PLUGIN_RULE       VARCHAR2(50), "+
                         " C_PLUGIN_WARNTYPE   VARCHAR2(50), "+
                         " C_PLUGIN_RESULTTYPE VARCHAR2(50), "+
                         " C_PLUGIN_LOGIC      VARCHAR2(20), "+
                         " C_PLUGIN_RESULTSHOW VARCHAR2(20) not null, "+
                         " C_PLUGIN_URL        VARCHAR2(20), "+
                         " C_PLUGIN_PROPERTY   VARCHAR2(20), "+
                         " C_PLUGIN_INCLUDE    VARCHAR2(100), "+
                         " C_PLUGIN_WARNTIME   VARCHAR2(50) "+
                         ")";
				
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换数据库联接
			if(!dblBLog.yssTableExist("T_PLUGIN_LOG")){
				strSql = " create table T_PLUGIN_LOG "+
                         "("+
                         " D_BUSSINESS_DATE       DATE not null, " +
                         " D_OPER_DATE            DATE not null, " +
                         " C_PRODUCT_CODE         VARCHAR2(20) not null, " +
                         " C_PRODUCT_NAME         VARCHAR2(50) not null, " +
                         " C_EXECUTE_CODE         VARCHAR2(50), " +
                         " C_EXECUTE_NAME         VARCHAR2(50), " +
                         " C_EW_TYPENAME          VARCHAR2(50), " +
                         " C_OPER_TYPENAME        VARCHAR2(50), " +
                         " C_EXECUTE_STATE        VARCHAR2(20), " +
                         " C_RESULT_TYPENAME      VARCHAR2(50), " +
                         " C_RESULT_INFO          CLOB, " +
                         " C_PORT_CODE            VARCHAR2(20), " +
                         " D_EXECUTE_START        DATE, " +
                         " D_EXECUTE_END          DATE, " +
                         " C_EXECUTE_TIME         VARCHAR2(20), " +
                         " C_BUSSINESS_MODULE     VARCHAR2(50), " +
                         " C_BUSSINESS_SUB_MODULE VARCHAR2(100), " +
                         " C_CREATOR_CODE         VARCHAR2(20), " +
                         " C_CREATOR_NAME         VARCHAR2(50), " +
                         " C_ASSET_TYPE           VARCHAR2(50), " +
                         " C_BOOKSET_CODE         VARCHAR2(50), " +
                         " S_LOG_ID               NUMBER(27) not null, " +
                         " C_REF_NUM              VARCHAR2(20), " +
                         " C_MAC_IP               VARCHAR2(20), " +
                         " C_MAC_NAME             VARCHAR2(30), " +
                         " C_MAC_ADDR             VARCHAR2(200), " +
                         " C_ACC_TYPE             VARCHAR2(50) " +
                         ")";
				
				sqlInfo.append(strSql);
				//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换数据库联接
				dblBLog.executeSql(strSql);
				
				strSql = " alter table T_PLUGIN_LOG add constraint" +
				" T_PLUGIN_LOG_PK primary key (S_LOG_ID) ";
				
				sqlInfo.append(strSql);
				//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换数据库联接
				dblBLog.executeSql(strSql);
			}
			
			if(!dbl.yssTableExist("T_PORT")){
				strSql = " create table T_PORT "+
                         "("+
                         " C_PORT_CODE   VARCHAR2(20) not null, "+
                         " C_PORT_NAME   VARCHAR2(50) not null, "+
                         " C_PORT_P_CODE VARCHAR2(20) not null, "+
                         " C_PORT_P_NAME VARCHAR2(50) not null "+
                         ")";
				
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssTableExist("T_PORT_PLUGIN")){
				strSql = " create table T_PORT_PLUGIN "+
                         "("+
                         " C_PORT_CODE        VARCHAR2(20) not null, " +
                         " C_PLUGIN_CODE      VARCHAR2(50) not null, " + 
                         " C_PLUGIN_ITEM      VARCHAR2(50) not null, " +
                         " C_PLUGIN_CONDITION VARCHAR2(20), " +
                         " C_PLUGIN_VALUE     VARCHAR2(20), " +
                         " C_PLUGIN_LOGIC     VARCHAR2(5), " +
                         " C_PRODUCT_CODE     VARCHAR2(20) not null, " +
                         " C_THRESHOLD_DESC   VARCHAR2(100), " +
                         " C_CHECK_STATE      NUMBER(1) default 1 " +
                         ")";
				
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssTableExist("T_PRODUCT")){
				strSql = " create table T_PRODUCT "+
                         "("+
                         " C_PRODUCT_CODE VARCHAR2(20) not null, " +
                         " C_PRODUCT_NAME VARCHAR2(50) not null, " + 
                         " C_VERSION      VARCHAR2(20) not null, " +
                         " C_PRO_SYS      VARCHAR2(20) not null, " +
                         " C_PRO_CUSTOMER VARCHAR2(20) not null, " +
                         " C_PRO_DES      VARCHAR2(200) " +
                         ")";
				
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssTableExist("T_PRODUCT_STATE")){
				strSql = " create table T_PRODUCT_STATE "+
                         "("+
                         " C_PRODUCT_CODE  VARCHAR2(20) not null, " +
                         " C_PRODUCT_STATE VARCHAR2(20) " + 
                         ")";
				
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssTableExist("T_PROJECT")){
				strSql = " create table T_PROJECT "+
                         "("+
                         " C_PRODUCT_CODE VARCHAR2(20) not null, " +
                         " C_PROJECT_NAME VARCHAR2(50) not null, " + 
                         " C_PROJECT_DES  VARCHAR2(200), " +
                         " C_PRODUCT_NAME VARCHAR2(20) not null " + 
                         ")";
				
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0056更新表数据出错！",e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
