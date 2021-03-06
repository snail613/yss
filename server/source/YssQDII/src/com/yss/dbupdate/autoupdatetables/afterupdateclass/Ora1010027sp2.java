package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssCons;
import com.yss.util.YssException;
/**
 * 
 * @author xuqiji 20100330 海富通的数据库转移tb_pub_表到组合群表中
 *
 */
public class Ora1010027sp2 extends BaseDbUpdate{
	public Ora1010027sp2() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 更新数据库的入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			doChangeData(hmInfo);
			createBaseTable(hmInfo);//创建证券信息，行情，汇率的base表为了创建视图时不会出现错误
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0027sp2 更新出错！", ex);
		}
	}
	/**
	 * 创建证券信息，行情，汇率的base表为了创建视图时不会出现错误
	 * @param hmInfo
	 * @throws YssException 
	 */
	private void createBaseTable(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = null;
		StringBuffer buff = null;
		String sTabPKFiled ="";
		try{
			buff = new StringBuffer(200);
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
			
			if(!dbl.yssTableExist("tb_base_security")){//创建证券信息base表
				if(dbl.yssTableExist("tb_pub_para_security")){
					buff.append(" create table tb_base_security as select * from tb_pub_para_security");
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
					
					buff.append(" alter table tb_base_security add constraints PK_tb_base_security primary key(FSECURITYCODE, FASSETGROUPCODE)");
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
				}else{
					sTabPKFiled = this.buildTabPKFiled(pub.yssGetTableNameForUpdTables("tb_para_security"));
					buff.append(" create table tb_base_security as select * from ").append(pub.yssGetTableNameForUpdTables("tb_para_security"));
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
					
					buff.append(" alter table tb_base_security add constraints PK_tb_base_security primary key(").append(sTabPKFiled).append(")");
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
				}
			}
			if(!dbl.yssTableExist("TB_Base_MARKETVALUE")){//创建行情表
				if(dbl.yssTableExist("Tb_Pub_Data_MarketValue")){
					buff.append(" create table TB_Base_MARKETVALUE as select * from Tb_Pub_Data_MarketValue");
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
					
					buff.append(" alter table TB_Base_MARKETVALUE add constraints PK_TB_Base_MARKETVALUE primary key(FMKTSRCCODE, FSECURITYCODE, FMKTVALUEDATE, FMKTVALUETIME, FPORTCODE, FASSETGROUPCODE)");
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
				}else{
					sTabPKFiled = this.buildTabPKFiled(pub.yssGetTableNameForUpdTables("tb_data_marketvalue"));
					buff.append(" create table TB_Base_MARKETVALUE as select * from ").append(pub.yssGetTableName("tb_data_marketvalue"));
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
					
					buff.append(" alter table TB_Base_MARKETVALUE add constraints PK_TB_Base_MARKETVALUE primary key(").append(sTabPKFiled).append(")");
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
				}
			}
			if(!dbl.yssTableExist("Tb_Base_ExchangeRate")){//汇率表
				if(dbl.yssTableExist("Tb_Pub_Data_ExchangeRate")){
					buff.append(" create table Tb_Base_ExchangeRate as select * from Tb_Pub_Data_ExchangeRate");
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
					
					buff.append(" alter table Tb_Base_ExchangeRate add constraints PK_Tb_Base_ExchangeRate primary key(FEXRATESRCCODE, FCURYCODE, FMARKCURY, FEXRATEDATE, FEXRATETIME, FPORTCODE, FASSETGROUPCODE)");
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
				}else{
					sTabPKFiled = this.buildTabPKFiled(pub.yssGetTableNameForUpdTables("tb_Data_ExchangeRate"));
					buff.append(" create table Tb_Base_ExchangeRate as select * from ").append(pub.yssGetTableName("tb_Data_ExchangeRate"));
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
					
					buff.append(" alter table Tb_Base_ExchangeRate add constraints PK_Tb_Base_ExchangeRate primary key(").append(sTabPKFiled).append(")");
					sqlInfo.append(buff.toString());
					
					dbl.executeSql(buff.toString());
					buff.delete(0,buff.length());
				}
			}
			
		}catch (Exception e) {
			throw new YssException("创建证券信息，行情，汇率的base表为了创建视图时不会出现错误！",e);
		}
		
	}
	/**
	 * 转移数据的具体方法
	 * @param hmInfo
	 * @throws YssException 
	 */
	private void doChangeData(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = null;
		StringBuffer buff = null;
		String sTableName ="";
		ResultSet rs = null;
		boolean bTrans = true;
		Connection conn = null;
		String sTabFields = "";
		String sTabPKFiled ="";
		String sSql = "";
		String sAssetGroupTable = "";
		try{
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			buff = new StringBuffer(500);
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
			
			buff.append(" select table_name from user_all_tables where table_name like 'TB_PUB_%'");
			sqlInfo.append(buff.toString());
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			while(rs.next()){
				sTableName = rs.getString("table_name");
				sAssetGroupTable = "TB_"+pub.getAssetGroupCode()+sTableName.substring(6,sTableName.length());
				if(!dbl.yssTableExist(sAssetGroupTable)){
					continue;
				}
				if(this.existsTabColumn_Ora("TB_PUB_DAO_EXCHANGEBOND","fcommisiontyp")){
					dbl.executeSql(" alter table TB_PUB_DAO_EXCHANGEBOND add fcommisiontyp varchar2(100) null");
				}else if(this.existsTabColumn_Ora("TB_PUB_DATA_BONUSSHARE","FSTARTDATE")){
					dbl.executeSql(" alter table TB_PUB_DATA_BONUSSHARE add FSTARTDATE Date default Sysdate NOT NULL");
				}else if(this.existsTabColumn_Ora("Tb_Pub_Data_Rightsissue","FSTARTDATE")){
					dbl.executeSql(" alter table Tb_Pub_Data_Rightsissue add FSTARTDATE Date default Sysdate NOT NULL");
				}else if(this.existsTabColumn_Ora("TB_PUB_DATA_DIVIDEND","FSTARTDATE")){
					dbl.executeSql(" alter table TB_PUB_DATA_DIVIDEND add FSTARTDATE Date default Sysdate NOT NULL");
				}else if(this.existsTabColumn_Ora("TB_PUB_PARA_SECURITY","foperstyle,FMAINTAINMGR")){
					dbl.executeSql(" alter table TB_PUB_PARA_SECURITY add foperstyle varchar2(50) null");
					dbl.executeSql(" alter table TB_PUB_PARA_SECURITY add FMAINTAINMGR varchar2(50) null");
				}
				if(sTableName.equalsIgnoreCase("TB_PUB_DATA_PREBONUSSHARE")||sTableName.equalsIgnoreCase("TB_PUB_DATA_PRECASHCONSIDER")
						||sTableName.equalsIgnoreCase("TB_PUB_DATA_PREDIVIDEND")||sTableName.equalsIgnoreCase("TB_PUB_DATA_PREMAYAPARTBOND")
						||sTableName.equalsIgnoreCase("TB_PUB_DATA_PRERIGHTSISSUE")){
					continue;
				}
				sTabFields = getTabFields(sAssetGroupTable);//获取表的字段
				sTabPKFiled = buildTabPKFiled(sAssetGroupTable);//获取表的主键字段
				sSql = deleteSameData(sTabPKFiled,sTableName);//删除数据
				dbl.executeSql(sSql);
				
				if(sTabFields.endsWith(",")){
					sTabFields = sTabFields.substring(0,sTabFields.length() -1);
				}
				buff.append(" insert into ").append(sAssetGroupTable).append("(").append(sTabFields).append(")");
				buff.append(" select ").append(sTabFields).append(" from ").append(sTableName).append(" a ");
				sqlInfo.append(buff.toString());
				
				dbl.executeSql(buff.toString());
				buff.delete(0,buff.length());
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		}catch (Exception e) {
			throw new YssException(" 转移PUB表数据到组合群表中出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn,bTrans);
		}
	}
	/**
	 * 删除重复数据
	 * @param tabPKFiled 主键字段名
	 * @param sTableName 表名
	 * @throws YssException 
	 */
	 private String deleteSameData(String tabPKFiled,String sTableName) throws YssException { 
		String [] PKColumnName = null; 
		String sSql = "";
		try{
			PKColumnName = tabPKFiled.split(",");
			sSql = " delete from " + "TB_" + pub.getAssetGroupCode() + sTableName.substring(6,sTableName.length())
			 		+ " a where exists(select * from " + sTableName + " b where ";
			for(int i =0; i < PKColumnName.length;i++){
				sSql = sSql + " a." + PKColumnName[i] + " = b." + PKColumnName[i] + " and ";
			}
			if(sSql.endsWith(" and ")){
				sSql = sSql.substring(0,sSql.length() - 5);
			}
			sSql = sSql + ")";
		}catch (Exception e) {
			throw new YssException("删除重复数据！",e);
		}
		return sSql;
	}
	/**
	 * 此方法获取表的字段与字段类型
	 * @param sSql
	 * @return
	 * @throws YssException
	 */
	private String getTabFields(String sTableName) throws YssException {
		StringBuffer buf = new StringBuffer();
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		String sSql = "";
		try {
			sSql = " select * from " + sTableName;
			rs = dbl.openResultSet(sSql);
			rsmd = rs.getMetaData();
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				buf.append(rsmd.getColumnName(i + 1).toLowerCase()).append(",");
			}
		} catch (Exception e) {
			throw new YssException("获取表字段信息出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	      return buf.toString();
	}
	   /**
	   * 获取表的主键字段
	   **/
	   protected String buildTabPKFiled(String tableName) throws YssException{
		   ResultSet rs =null;
		   String sqlStr="";
		   String sPKField="";
		   try{
			   if(dbl.dbType == YssCons.DB_ORA){
				   sqlStr="select Column_Name as FPKField from User_Cons_Columns "+
				   " where table_Name=upper('"+tableName+"') and Position > 0";			   
			   }else if(dbl.dbType == YssCons.DB_DB2){
				   
			   }
			   rs =dbl.openResultSet(sqlStr);
			   while(rs.next()){
				   sPKField+=(rs.getString("FPKField")+",");
			   }
			   if(sPKField.endsWith(",")){
				   sPKField=sPKField.substring(0,sPKField.length()-1);
			   }
		   }catch(Exception ex){
			   throw new YssException("获取表【"+tableName+"】主键字段出错",ex);
		   }finally{
			   dbl.closeResultSetFinal(rs);
		   }
		   return sPKField;
	   }
}





















