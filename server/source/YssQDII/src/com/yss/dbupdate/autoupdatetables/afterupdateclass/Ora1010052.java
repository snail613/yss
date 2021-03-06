package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010052 extends BaseDbUpdate {

	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateVocTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0052 更新出错！", ex);
		}
	}
	
	/**
	 * 删除无用菜单及对应功能调用
	 * @param hmInfo
	 * @throws YssException
	 */
	public void updateVocTable(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		
		ResultSet rs = null;//add by songjie 2013.08.26 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B
		try{
			
			strSql  =  " delete from tb_fun_vocabulary t where t.fvoctypecode = 'vch_Sign' and t.fvoccode not" +
					   " in ('0','1','2','3','4','5','6','7','8','10') ";
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlInfo.append(strSql);
            dbl.executeSql(strSql);
    			
			strSql = " update tb_fun_vocabulary t set t.fvoccode = '9' where t.fvoctypecode = 'vch_Sign' and t.fvoccode = '10' "; 
			sqlInfo.append(strSql);// add by yeshenghong 20120401 BUG4083
            dbl.executeSql(strSql);
            updTables.append("tb_fun_vocabulary");	
            
            conn.commit();
            //--- edit by songjie 2013.08.26 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            rs = dbl.openResultSet("select * from TB_BASE_ACTSTFRELA ");
            //--- edit by songjie 2013.08.26 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            if(dbl.yssTableExist("TB_BASE_ACTSTFRELA") && dbl.isFieldExist(rs, "FRELANUM") && !dbl.isFieldExist(rs, "FCHECKSTATE"))//已经更新51老版本
            {//已经更新51老版本的执行更新语句 如果更新51新版本 无需执行更新
//            	if(rs.next())//51老版本已经更新，且已导入数据
//            	{
//            		strSql = " alter  table TB_BASE_ACTSTFRELA add (FCHECKSTATE NUMBER(1),FCREATOR VARCHAR2(20),FCREATETIME VARCHAR2(20),FCHECKUSER VARCHAR2(20),FCHECKTIME VARCHAR2(20)) ";
//            		sqlInfo.append(strSql);// add by yeshenghong 20120416 BUG2402
//                    dbl.executeSql(strSql);
//                    conn.commit();
//                    strSql = " update table TB_BASE_ACTSTFRELA set FCheckState = 1, FCreator = '001', FCreateTime = '2012-04-15',FCheckUser = '001',FCheckTime = '2012-04-15'";
//                    sqlInfo.append(strSql);// add by yeshenghong 20120416 BUG2402
//                    dbl.executeSql(strSql);
//                    conn.commit();
//                    strSql = " alter table  tb_base_actstfrela modify fcheckstate not null ";
//                    sqlInfo.append(strSql);// add by yeshenghong 20120416 BUG2402
//                    dbl.executeSql(strSql);
//                    strSql = " alter table  tb_base_actstfrela modify fcreator not null ";
//                    sqlInfo.append(strSql);// add by yeshenghong 20120416 BUG2402
//                    dbl.executeSql(strSql);
//                    strSql = " alter table  tb_base_actstfrela modify fcreatetime not null ";
//                    sqlInfo.append(strSql);// add by yeshenghong 20120416 BUG2402
//                    dbl.executeSql(strSql);
//            	}else//51老版本已经更新，但未导入数据
//            	{
            		strSql  = "drop table TB_BASE_ACTSTFRELA";
        			sqlInfo.append(strSql);
        			dbl.executeSql(strSql);
        			
        			strSql = " create table TB_BASE_ACTSTFRELA ( FRELANUM  NUMBER(5) not null,FACCOUNTINGTYPE  VARCHAR2(20)," + 
       			   		 " FACCTYPECODE     VARCHAR2(20), FACCOUNTSUBTYPE  VARCHAR2(20), FACCSUBTYPECODE  VARCHAR2(20)," +
       			   		 " FSECURITYSHOW    NUMBER(1), FIVPAYCATSHOW    NUMBER(1), FCASHACCSHOW  NUMBER(1),"+
       			   	     " FSUBTSFTYPECODE  VARCHAR2(20), FSUBJECTTYPE     NUMBER(3), FDATASTYLE  VARCHAR2(20)," + 
       			   		 " FDATASTYLENAME   VARCHAR2(20), FSUBJECTTYPENAME VARCHAR2(20)," +
       			   		 " FCHECKSTATE      NUMBER(1) not null, FCREATOR  VARCHAR2(20) not null," +
       			   		 " FCREATETIME      VARCHAR2(20) not null, FCHECKUSER   VARCHAR2(20), FCHECKTIME VARCHAR2(20))";
                   sqlInfo.append(strSql);// add by yeshenghong 2012
                   dbl.executeSql(strSql);
                   strSql = "alter table TB_BASE_ACTSTFRELA add constraint PK_TB_BASE_ACTSTFRELA primary key (FRELANUM) ";
                   sqlInfo.append(strSql);
                   dbl.executeSql(strSql);
//            	}
            }
            updTables.append("tb_base_actstfrela");	
            strSql = " create or replace function get_val_facctcode(facctcode in varchar2) " +
                   	" return NUMBER is  Result NUMBER;  test number; begin " +
                    " Result := to_number(trim(substr(facctcode, INSTR(facctcode, '_', 1, 1) + 1," +
                    " INSTR(facctcode, ' ', 1, 1) - INSTR(facctcode, '_', 1, 1)))); " +
                    " return Result; EXCEPTION  WHEN others THEN  begin   return null; end; end get_val_facctcode; ";
            dbl.executeSql(strSql);
            sqlInfo.append(strSql);
            conn.commit();
            
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0052 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);//add by songjie 2013.08.26 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B
		}
	}
}
