package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.*;
import java.util.HashMap;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by songjie 2011.07.20
 * 需求 1282 QDV4博时基金2011年6月29日01_A
 * 在外汇交易表（Tb_XXX_Data_Ratetrade）中添加FRateTradeType字段
 * @author 宋洁
 *
 */
public class Ora1010042sp3 extends BaseDbUpdate {
	public Ora1010042sp3(){
		
	}
	
	/**
     * add by songjie 2011.07.20
     * 需求 1282 QDV4博时基金2011年6月29日01_A
     * 在外汇交易表（Tb_XXX_Data_Ratetrade）中添加FRateTradeType字段
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0042sp3 更新出错！", ex);
		}
	}
	
	/**
     * add by songjie 2011.07.20
     * 需求 1282 QDV4博时基金2011年6月29日01_A
     * 在外汇交易表（Tb_XXX_Data_Ratetrade）中添加FRateTradeType字段
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
			
			if(existsTabColumn_Ora(pub.yssGetTableName("Tb_Data_RateTrade"),"FRateTradeType")){
				strSql = " alter table " + pub.yssGetTableName("Tb_Data_RateTrade") + 
				" add FRateTradeType varchar2(2) default ' ' ";
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}else{
				strSql = " update " + pub.yssGetTableName("Tb_Data_RateTrade") + 
				" set FRateTradeType = '1' ";
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}

			updTables.append(pub.yssGetTableName("Tb_Data_RateTrade"));
			
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0042sp3 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
