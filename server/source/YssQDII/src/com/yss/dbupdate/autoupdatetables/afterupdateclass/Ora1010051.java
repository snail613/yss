package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010051 extends BaseDbUpdate {

	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateVocTable(hmInfo);
			updateSomeTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0051 更新出错！", ex);
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
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //--- edit by songjie 2013.08.26 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            rs = dbl.openResultSet("select * from TB_BASE_ACTSTFRELA where 1=2");
            //--- edit by songjie 2013.08.26 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            if(dbl.yssTableExist("TB_BASE_ACTSTFRELA") &&! dbl.isFieldExist(rs, "FRELANUM"))//表存在先删除
            {
    			strSql  = "drop table TB_BASE_ACTSTFRELA";
    			sqlInfo.append(strSql);
    			dbl.executeSql(strSql);
    			
    			strSql = " create table TB_BASE_ACTSTFRELA ( FRELANUM  NUMBER(5) not null,FACCOUNTINGTYPE  VARCHAR2(20)," + 
   			   		 " FACCTYPECODE     VARCHAR2(20), FACCOUNTSUBTYPE  VARCHAR2(20), FACCSUBTYPECODE  VARCHAR2(20)," +
   			   		 " FSECURITYSHOW    NUMBER(1), FIVPAYCATSHOW    NUMBER(1), FCASHACCSHOW  NUMBER(1),"+
   			   	     " FSUBTSFTYPECODE  VARCHAR2(20), FSUBJECTTYPE     NUMBER(3), FDATASTYLE  VARCHAR2(20)," + 
   			   		 " FDATASTYLENAME   VARCHAR2(20), FSUBJECTTYPENAME VARCHAR2(20) )";
               sqlInfo.append(strSql);// add by yeshenghong 2012
               dbl.executeSql(strSql);
               strSql = "alter table TB_BASE_ACTSTFRELA add constraint PK_TB_BASE_ACTSTFRELA primary key (FRELANUM) ";
               sqlInfo.append(strSql);
               dbl.executeSql(strSql);
               updTables.append("TB_BASE_ACTSTFRELA");		
               
            }
            dbl.closeResultSetFinal(rs);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0051 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
			//--- add by songjie 2013.08.26 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeResultSetFinal(rs);
			//--- add by songjie 2013.08.26 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		}
	}
	/** 
	 * add by zhouwei 20120328 更新系统的利息算法
	* @Title: updateSomeTable 
	* @Description: TODO
	* @param @param hmInfo
	* @param @throws YssException    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public void updateSomeTable(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs=null;
		try{
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //修改债券信息设置中的利息算法
			strSql=" update "+pub.yssGetTableName("tb_para_fixinterest")+" set fcalcinsmeticday = 'A/365F',fcalcinsmeticbuy ='A/365F',fcalcinsmeticsell = 'A/365F'"
				  +" where fcalcinsmeticday like 'A/365F(每日%)%'";			
            sqlInfo.append(strSql);
            dbl.executeSql(strSql);	
			
			strSql=" update "+pub.yssGetTableName("tb_para_fixinterest")+" set fcalcinsmeticday = 'A/365', fcalcinsmeticbuy ='A/365' , fcalcinsmeticsell = 'A/365'"
			      +" where fcalcinsmeticday like 'A/365(每日%)%'";			
			sqlInfo.append(strSql);
			dbl.executeSql(strSql);
			
			strSql=" update "+pub.yssGetTableName("tb_para_fixinterest")+" set fcalcinsmeticday = 'A/A' , fcalcinsmeticbuy ='A/A' , fcalcinsmeticsell = 'A/A'"
		      	  +" where fcalcinsmeticday like 'A/A(每日%)%'";			
			sqlInfo.append(strSql);
			dbl.executeSql(strSql);	
			
			strSql=" update "+pub.yssGetTableName("tb_para_fixinterest")+" set fcalcinsmeticday = 'A/A-Bond' , fcalcinsmeticbuy ='A/A-Bond' , fcalcinsmeticsell = 'A/A-Bond'"
	      	      +" where fcalcinsmeticday like 'A/A-Bond(每日%)%'";			
		    sqlInfo.append(strSql);
		    dbl.executeSql(strSql);	
		    
		    strSql=" update "+pub.yssGetTableName("tb_para_fixinterest")+" set fcalcinsmeticday = 'A/360' , fcalcinsmeticbuy ='A/360' , fcalcinsmeticsell = 'A/360'"
		    		+" where fcalcinsmeticday like 'A/360(每日%)%'";			
		    sqlInfo.append(strSql);
		    dbl.executeSql(strSql);
		    updTables.append("tb_para_fixinterest");
		    //删除旧的利息算法
		    strSql="select * from tb_base_calcinsmetic where fcimcode = 'A/365F(每日)' or fcimcode = 'A/365(每日)' or fcimcode = 'A/A(每日)'"
		    	  +" or fcimcode = 'A/A-Bond(每日)'";
		    rs=dbl.openResultSet(strSql);
		    if(rs.next()){
		    	 strSql="delete from tb_base_calcinsmetic where fcimcode like 'A/365F%' or fcimcode like 'A/365%' "
				    	+" or fcimcode like 'A/360%' or fcimcode like 'A/A%' or fcimcode like 'A/A-Bond%'";
		    	 sqlInfo.append(strSql);
				 dbl.executeSql(strSql);
			     updTables.append("tb_base_calcinsmetic");
		    }		    
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0049sp3 更新表数据出错！", e);
		}finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
