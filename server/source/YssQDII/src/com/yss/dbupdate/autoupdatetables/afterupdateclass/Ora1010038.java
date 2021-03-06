package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010038 extends BaseDbUpdate {

	public Ora1010038() {

	}
	
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			alterTable(); //QDV4上海2010年12月10日02_A。lidaolong 
			deleteVoc();//add by nimengjing 2011.2.15 BUG #1102 库存信息配置界面问题 
		
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0038 更新出错！", ex);
		}
	}
	/**
	 * add by nimengjing  2011.2.15  MS01462    BUG #1102 库存信息配置界面问题
	 * @方法名：deleteVoc
	 * @参数：
	 * @返回类型：void
	 * @说明：更新tb_para_optioncontract表
	 */
	private void deleteVoc() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			
			strSql = " delete  from tb_fun_vocabulary "
				   + " where fvoctypecode = 'scg_type' and fvoccode = 'investpayrec'";		
			
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除词汇出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	
	/**
	 * QDV4上海2010年12月10日02_A
	 * lidaolong 2011.01.24
	 * @throws YssException
	 */
	public void alterTable() throws YssException{
		
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
		try{
			if (existsTabColumn_Ora(pub.yssGetTableName("tb_para_portfolio"),"FCuryCode")){//判断表中这个字段是否存在
				
				strSql ="alter table "+pub.yssGetTableName("tb_para_portfolio")+"  add FCuryCode VARCHAR2(20)";
				dbl.executeSql(strSql);	
				
				//版本更新时，需要将组合设置中的基础货币初始值设置为组合群设置中的基础货币。
				strSql ="update " + pub.yssGetTableName("tb_para_portfolio") +
						" set FCuryCode = (select p.FBaseCury from TB_SYS_AssetGroup p where p.fassetgroupcode = '"+ pub.getAssetGroupCode()+"')";
				dbl.executeSql(strSql);	
			}
			
			//add by fangjiang #2279 2011.02.14
			if(dbl.yssTableExist(pub.yssGetTableName("tb_stock_invest"))){
				if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("TB_STOCK_INVEST")).trim().length()!=0){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_invest")+" drop constraint PK_"+pub.yssGetTableName("TB_STOCK_INVEST")+" cascade");
				}				
				if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("TB_STOCK_INVEST")).trim().length()!=0){
					dbl.executeSql("drop index PK_"+pub.yssGetTableName("TB_STOCK_INVEST"));
				}
				dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_invest")+" add (constraint PK_"+pub.yssGetTableName("TB_STOCK_INVEST")+
				" primary key (FIVPAYCATCODE, FYEARMONTH, FSTORAGEDATE, FPORTCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FATTRCLSCODE, FCuryCode))");
			}		
			
			if(dbl.yssTableExist(pub.yssGetTableName("tb_stock_investpayrec"))){
				if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("tb_stock_investpayrec")).trim().length()!=0){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_investpayrec")+" drop constraint PK_"+pub.yssGetTableName("TB_STOCK_INVESTPAYREC")+" cascade");
				}
				if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("TB_STOCK_INVESTPAYREC")).trim().length()!=0){
					dbl.executeSql("drop  index PK_"+pub.yssGetTableName("TB_STOCK_INVESTPAYREC"));		
				}
				dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_investpayrec")+" add (constraint PK_"+pub.yssGetTableName("TB_STOCK_INVESTPAYREC")+
				" primary key (FYEARMONTH, FSTORAGEDATE, FPORTCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FIVPAYCATCODE, FTSFTYPECODE, FSUBTSFTYPECODE, FATTRCLSCODE, FCuryCode))");
			}		
			//---------------------------
			
		}catch(Exception ex) {
			throw new YssException("版本1.0.1.0038 变更表结构出错！", ex);
		}
		finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
}
