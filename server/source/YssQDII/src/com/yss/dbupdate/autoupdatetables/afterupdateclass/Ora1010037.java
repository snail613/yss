package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010037 extends BaseDbUpdate {
	public Ora1010037(){
		
	}
	
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			alterTable(); //修改表 Tb_Para_Security 使FFaceAmount的默认值为1且不为null，并更新里面为空的记录更为默认值1 。lidaolong 
			updateForwardTrade();  //add by fangjiang 20101.01.12 STORY #262 #393
			dropNoUseTable();//add by licai 20110120 BUG #473 权限分配时，报错。
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0037 更新出错！", ex);
		}
	}
	/**
	 * 修改表 Tb_001_Para_Security 使FFaceAmount的默认值为1，并更新里面为空的记录更为默认值1 。
	 * QDV4南方东英2010年12月14日01
	 * lidaolong 20110114
	 * @throws YssException 
	 * 
	 */
	public void alterTable() throws YssException{
		
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
			try {
				//第一步：更新表，使为null的值为1
				strSql ="update "+pub.yssGetTableName("Tb_Para_Security")+" set fFaceAmount = 1 where fFaceAmount is null";
				
					dbl.executeSql(strSql);

					//第二步：修改表，设置默认值
					strSql ="alter table "+pub.yssGetTableName("Tb_Para_Security")+"  modify fFaceAmount number(18,4) default 1";
					dbl.executeSql(strSql);	
				
				//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao ---
				/******************************************************************************
				 * Oracle 10g  删除主键时，不能把主键对应的索引一起删除。这是Oracle 10g 的一个Bug
				 * 所以这里还要删除索引
				 */	
					
					
				rs = dbl.openResultSet("select * from "+pub.yssGetTableName("tb_stock_cash")+" where 1=2");
				if(!dbl.isFieldExist(rs, "FATTRCLSCODE")){
					
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_cash")+" add(FATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
					
					if (dbl.getTableConstaintKey(pub.yssGetTableName("TB_STOCK_CASH")))
					{
						dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_cash")+
									" drop constraint PK_"+pub.yssGetTableName("TB_STOCK_CASH")+" cascade");	
					}
					if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("TB_STOCK_CASH")).trim().length()!=0){
						dbl.executeSql("drop  index PK_"+pub.yssGetTableName("TB_STOCK_CASH"));	
					}
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_cash")+" add (constraint PK_"+pub.yssGetTableName("TB_STOCK_CASH")+
					" primary key (FCASHACCCODE, FYEARMONTH, FSTORAGEDATE, FPORTCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3,FATTRCLSCODE))");	
				}
				dbl.closeResultSetFinal(rs);
				
				rs = dbl.openResultSet("select * from "+pub.yssGetTableName("tb_stock_cashpayrec")+" where 1=2");
				if(!dbl.isFieldExist(rs, "FATTRCLSCODE")){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_cashpayrec")+" add(FATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
					
					if (dbl.getTableConstaintKey(pub.yssGetTableName("TB_STOCK_CASH")))
					{
						dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_cashpayrec")+
									" drop constraint PK_"+pub.yssGetTableName("TB_STOCK_CASHPAYREC")+" cascade");	
					}
					
					if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("TB_STOCK_CASHPAYREC")).trim().length()!=0){
						dbl.executeSql("drop  index PK_"+pub.yssGetTableName("TB_STOCK_CASHPAYREC"));
					}
	                dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_cashpayrec")+" add (constraint PK_"+pub.yssGetTableName("TB_STOCK_CASHPAYREC")+
					" primary key (FYEARMONTH, FSTORAGEDATE, FPORTCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FCASHACCCODE, FTSFTYPECODE, FSUBTSFTYPECODE, FATTRCLSCODE))");	

				}
				dbl.closeResultSetFinal(rs);
				
				rs = dbl.openResultSet("select * from "+pub.yssGetTableName("tb_stock_invest")+" where 1=2");
				if(!dbl.isFieldExist(rs, "FATTRCLSCODE")){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_invest")+" add(FATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
					
					if (dbl.getTableConstaintKey(pub.yssGetTableName("TB_STOCK_CASH")))
					{
						dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_invest")+
									" drop constraint PK_"+pub.yssGetTableName("TB_STOCK_INVEST")+" cascade");
					}
					
					if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("TB_STOCK_INVEST")).trim().length()!=0){
					  dbl.executeSql("drop  index PK_"+pub.yssGetTableName("TB_STOCK_INVEST"));
					}
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_invest")+" add (constraint PK_"+pub.yssGetTableName("TB_STOCK_INVEST")+
						" primary key (FIVPAYCATCODE, FYEARMONTH, FSTORAGEDATE, FPORTCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3,FATTRCLSCODE))");

				}
				dbl.closeResultSetFinal(rs);
				
				
				rs = dbl.openResultSet("select * from "+pub.yssGetTableName("tb_stock_investpayrec")+" where 1=2");
				if(!dbl.isFieldExist(rs, "FATTRCLSCODE")){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_investpayrec")+" add(FATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
					
					if (dbl.getTableConstaintKey(pub.yssGetTableName("TB_STOCK_CASH")))
					{
						dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_investpayrec")+
									" drop constraint PK_"+pub.yssGetTableName("TB_STOCK_INVESTPAYREC")+" cascade");
					}
					
					if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("TB_STOCK_INVESTPAYREC")).trim().length()!=0){
					  dbl.executeSql("drop  index PK_"+pub.yssGetTableName("TB_STOCK_INVESTPAYREC"));		
					}
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_stock_investpayrec")+" add (constraint PK_"+pub.yssGetTableName("TB_STOCK_INVESTPAYREC")+
					" primary key (FYEARMONTH, FSTORAGEDATE, FPORTCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FIVPAYCATCODE, FTSFTYPECODE, FSUBTSFTYPECODE,FATTRCLSCODE))");					

				}
				dbl.closeResultSetFinal(rs);
				
				rs = dbl.openResultSet("select * from "+pub.yssGetTableName("tb_data_cashpayrec")+" where 1=2");
				if(!dbl.isFieldExist(rs, "FATTRCLSCODE")){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_data_cashpayrec")+" add(FATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
				}
				dbl.closeResultSetFinal(rs);
				
				rs = dbl.openResultSet("select * from "+pub.yssGetTableName("tb_data_investpayrec")+" where 1=2");
				if(!dbl.isFieldExist(rs, "FATTRCLSCODE")){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_data_investpayrec")+" add(FATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
				}
				dbl.closeResultSetFinal(rs);
				
				rs = dbl.openResultSet("select * from "+pub.yssGetTableName("tb_data_ratetrade")+" where 1=2");
				if(!(dbl.isFieldExist(rs, "FATTRCLSCODE"))&& !(dbl.isFieldExist(rs, "FBATTRCLSCODE")) ){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_data_ratetrade")+" add(FATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_data_ratetrade")+" add(FBATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
				}
				dbl.closeResultSetFinal(rs);
				
				rs = dbl.openResultSet("select * from "+pub.yssGetTableName("tb_cash_savinginacc")+" where 1=2");
				if(!dbl.isFieldExist(rs, "FATTRCLSCODE") ){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_cash_savinginacc")+" add(FATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
				}
				dbl.closeResultSetFinal(rs);
				
				rs = dbl.openResultSet("select * from "+pub.yssGetTableName("tb_cash_savingoutacc")+" where 1=2");
				if(!dbl.isFieldExist(rs, "FATTRCLSCODE") ){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_cash_savingoutacc")+" add(FATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
				}
				dbl.closeResultSetFinal(rs);
				
				rs = dbl.openResultSet("select * from "+pub.yssGetTableName("tb_cash_subtransfer")+" where 1=2");
				if(!dbl.isFieldExist(rs, "FATTRCLSCODE") ){
					dbl.executeSql("alter table "+pub.yssGetTableName("tb_cash_subtransfer")+" add(FATTRCLSCODE   VARCHAR2(20) default ' ' not null)");
				}
				dbl.closeResultSetFinal(rs);
			}catch (Exception e) {
				throw new YssException("版本1.0.1.0037 变更表结构出错！", e);
			}
			finally {
	            dbl.endTransFinal(conn, bTrans);
	        }
	}
	
	/**
     * add by fangjiang 20101.01.12 STORY #262 #393
     * 由于远期外汇交易增加了平仓，在Tb_XXX_Data_ForwardTrade中增加了字段FTradeType
     * 需将FTradeType更新为'20'(正常交易）
     */
	private void updateForwardTrade() throws YssException {
		String sql = "";
		try {
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Data_ForwardTrade"))) {
				sql = " update " + pub.yssGetTableName("Tb_Data_ForwardTrade") + 
					  " set FTradeType='20' ";
				dbl.executeSql(sql);
			}
		} catch (Exception e) {
			throw new YssException("版本1.0.1.0037 更新表结构出错！", e);
		} 
	}
	
	/**add by licai 20110120 BUG #473 权限分配时，报错。
	 * 删除36版本创建的无用表TB_SYS_ROLERIGHT_bak
	 * @throws YssException
	 */
	private void dropNoUseTable()throws YssException{
		String sql=null;
		if(dbl.yssTableExist("TB_SYS_ROLERIGHT_BAK")){
			sql="DROP TABLE "+pub.yssGetTableName("TB_SYS_ROLERIGHT_BAK");
			try {
				dbl.executeSql(sql);
			} catch (Exception e) {
				throw new YssException("版本1.0.1.0037 更新表结构出错！",e);
			}
		}
	}
}
