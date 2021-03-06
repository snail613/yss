package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010049sp3 extends BaseDbUpdate {

	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateVocTable(hmInfo);
			updateSomeTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0049sp3 更新出错！", ex);
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
		try{
            conn.setAutoCommit(bTrans);
            bTrans = true;
            //删除无用菜单
			strSql = " delete from Tb_Fun_Menubar m where m.fbarcode in (" + 
						"'confirm','orderadmin','ordermaintenance'," + //订单管理模块
						"'trade'," + //业务资料－交易数据（第一个）
						"'language'," +//基础参数－语言设置
						"'cashbook'," +//台帐管理 － 现金台帐
						"'securitybook'," +//台帐管理 － 证券台帐
						"'cashbooknew'," +//台帐管理 － 现金台帐（New）
						"'securitybooknew'," +//台帐管理 － 证券台帐（New）
						"'RightType'," +//系统设置－权限类型设置
						"'OperationType'," +//系统设置－操作类型设置
						"'tabledict'," +//系统设置－系统功能－数据表字典设置
						"'dayfinishlog'," +//系统设置－系统功能－日终处理日志报表
						"'operfunextend')";//系统平台－通用业务扩展配置
			
            sqlInfo.append(strSql);
            dbl.executeSql(strSql);
			updTables.append("Tb_Fun_Menubar");			
			
            //删除菜单对应功能调用
			strSql = " delete from Tb_Fun_Refinvoke r where r.frefinvokecode in (" + 
						"'000068','000071'," + //订单管理模块
						"'000083'," + //业务资料－交易数据（第一个）
						"'00000C'," +//基础参数－语言设置
						"'000092'," +//台帐管理 － 现金台帐
						"'000093'," +//台帐管理 － 证券台帐
						"'0000300'," +//台帐管理 － 现金台帐（New）
						"'0000301'," +//台帐管理 － 证券台帐（New）
						"'000118'," +//系统设置－权限类型设置
						"'000119'," +//系统设置－操作类型设置
						"'00000921'," +//系统设置－系统功能－数据表字典设置
						"'A002'," +//系统设置－系统功能－日终处理日志报表
						"'000704')";//系统平台－通用业务扩展配置
			
            sqlInfo.append(strSql);
            dbl.executeSql(strSql);
			updTables.append("Tb_Fun_Refinvoke");			
			
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0049sp3 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
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
