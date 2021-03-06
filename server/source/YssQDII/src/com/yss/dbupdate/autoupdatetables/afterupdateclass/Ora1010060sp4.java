package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010060sp4 extends BaseDbUpdate  
{
	public void doUpdate(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		String strSql = "";
			
		try
		{
			
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
			updTables = (StringBuffer) hmInfo.get("updatetables");
			//组合分级设置历史数据的处理    分级模式   全部更新为不限
			strSql = " update  " + pub.yssGetTableName("tb_ta_portcls") + " set FPORTCLSSCHEMA = 'abLimited'" ;
			
			dbl.executeSql(strSql);	
			
			//删除重复词汇
			strSql = " delete from tb_fun_vocabulary where Fvoccode='leverageClass' and fvoctypecode = 'ClassAccMethod' " ;
			
			dbl.executeSql(strSql);	
			
			sqlInfo.append(strSql);
			updTables.append("tb_ta_portcls");
			/**add---huhuichao 2013-10-18 BUG  81248 资产估值后期货无法产生估值增值的资金调拨*/
			this.updateFutrueTable();
			/**end---huhuichao 2013-10-18 BUG  81248 */

		} 
		catch (Exception ex) 
		{
			throw new YssException("版本 1.0.1.0060sp4更新出错！", ex);
		}
	}

	/**
     * add by huhuichao 2013.10.18 bug 81248
     * 将期货交易数据插入到TMP表中
     * @throws YssException
     */
	public void updateFutrueTable() throws YssException {
		String strSql = "";
		try {
			strSql = " delete from "
					+ pub.yssGetTableName("Tb_Data_Futurestrade_Tmp");
			dbl.executeSql(strSql);
			strSql = " insert into "
					+ pub.yssGetTableName("Tb_Data_Futurestrade_Tmp")
					+ " (select * from "
					+ pub.yssGetTableName("Tb_Data_Futurestrade")+")";
			dbl.executeSql(strSql);
		} catch (Exception ex) {
			throw new YssException("更新期货交易关联表出错", ex);
		}
	}
}
