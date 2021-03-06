package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**shashijie,2011-5-26 上午11:09:47 41版本更新数据库脚本*/
public class Ora1010041sp1 extends BaseDbUpdate {
	public Ora1010041sp1(){
		
	}
	
	/**入口方法*/
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateTable(); //修改表 TdzTypeCodePP表主键 
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0041 更新出错！", ex);
		}
	}
	
	/**shashijie 2011.05.26 BUG1914在划款类型维护界面上缺少划款指令名称字段*/
	public void updateTable() throws YssException{
		
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
		ResultSet Fhktype = null;
		ResultSet Fhktype2 = null;
		ResultSet rs3 = null;
			try {
				//更改TdzTypeCodePP表主键
				if(dbl.yssTableExist("TdzTypeCodePP")){
					//清空TdzTypeCodePP表(划款类型表)
					strSql ="DELETE FROM TdzTypeCodePP ";
					dbl.executeSql(strSql);
					//删除旧主键
					dbl.executeSql("ALTER TABLE TdzTypeCodePP DROP CONSTRAINT PK_TDzTypeCodePP CASCADE ");
					/**Oracle 10g  删除主键时，不能把主键对应的索引一起删除。这是Oracle 10g 的一个Bug,所以这里还要删除索引*/
					if(dbl.getTableByConstaintKey("PK_TDzTypeCodePP").trim().length()!=0){
						dbl.executeSql("drop index PK_TDzTypeCodePP");
					}
					//建立新主键
					dbl.executeSql("ALTER TABLE TdzTypeCodePP ADD (CONSTRAINT PK_TDzTypeCodePP PRIMARY KEY (FHKcode)) ");
				}
				dbl.closeResultSetFinal(rs);
				
				//Tb_XXX_Cash_Command划款指令表
				if(dbl.yssTableExist(pub.yssGetTableName("Tb_Cash_Command"))){
					rs = dbl.openResultSet("SELECT * FROM "+pub.yssGetTableName("Tb_Cash_Command")+" WHERE 1=2 ");
					if (!dbl.isFieldExist(rs, "FHKcode")) {
						//新增FHKcode划款类型代码字段
						dbl.executeSql("alter table "+pub.yssGetTableName("Tb_Cash_Command")+" add ( FHKcode VARCHAR2(100) default ' ' not null)");
					}
				}
				dbl.closeResultSetFinal(rs);
				
				//JjHkZl电子划款指令表
				strSql = "select * from " + pub.yssGetTableName("tb_vch_bookset")
						+ " where fcheckstate=1";
					rs = dbl.openResultSet(strSql);
					while (rs.next()) {
						String tableName = "A" + rs.getString("FBOOKSETCODE")
								+ "jjhkzl".toUpperCase();
						String PKCOM = "PK_"+tableName+"".toUpperCase();//主键名
						if (dbl.yssTableExist(tableName)) {
							//清空表
							dbl.executeSql("DELETE FROM "+tableName);
							Fhktype = dbl.getUserTabColumns(tableName,"Fhktype");
							Fhktype2 = dbl.getUserTabColumns(tableName,"Fhktype2");
							//修改表结构
							//如果为(不可输入空)
							if ("N".equalsIgnoreCase(Fhktype.getString("NullAble"))) {
								dbl.executeSql("alter table "+tableName+" modify Fhktype Varchar2(10) default ' ' null ");
							}
							//可输入空
							if ("Y".equalsIgnoreCase(Fhktype2.getString("NullAble"))) {
								dbl.executeSql("alter table "+tableName+" modify Fhktype2 Varchar2(50) default ' ' not null ");
							}
							dbl.closeResultSetFinal(Fhktype,Fhktype2);
							//删除旧主键
							rs3 = dbl.getUserConsColumns(tableName, PKCOM);
							if (rs3.next()) {
								dbl.executeSql("ALTER TABLE "+tableName+" DROP CONSTRAINT "+PKCOM+" CASCADE ");
								/**Oracle 10g  删除主键时，不能把主键对应的索引一起删除。这是Oracle 10g 的一个Bug,所以这里还要删除索引*/
								if(dbl.getTableByConstaintKey(PKCOM).trim().length()!=0){
									dbl.executeSql("drop index "+PKCOM);
								}
							}
							dbl.closeResultSetFinal(rs3);
							//建立新主键
							dbl.executeSql("ALTER TABLE "+tableName+" ADD (CONSTRAINT "+PKCOM+" PRIMARY KEY (FNum , FSH , SEQ_NO)) ");
						}
					}
				dbl.closeResultSetFinal(rs);
				
				//TdZhKreSult划款结果信息表
				if(dbl.yssTableExist("TdZhKreSult")){
					//清空TdZhKreSult表
					strSql ="DELETE FROM TdZhKreSult ";
					dbl.executeSql(strSql);
					//删除旧主键
					dbl.executeSql("ALTER TABLE TdZhKreSult DROP CONSTRAINT PK_TDZHKRESULT CASCADE ");
					/**Oracle 10g  删除主键时，不能把主键对应的索引一起删除。这是Oracle 10g 的一个Bug,所以这里还要删除索引*/
					if(dbl.getTableByConstaintKey("PK_TDZHKRESULT").trim().length()!=0){
						dbl.executeSql("drop index PK_TDZHKRESULT");
					}
					//建立新主键
					dbl.executeSql("ALTER TABLE TdZhKreSult ADD (CONSTRAINT PK_TDZHKRESULT PRIMARY KEY (SEQ_NO)) ");
				}
			}catch (Exception e) {
				throw new YssException("版本1.0.1.0041 变更表结构出错！", e);
			}
			finally {
	            dbl.endTransFinal(conn, bTrans);
	            dbl.closeResultSetFinal(rs,Fhktype,Fhktype2,rs3);
	        }
	}
	
	
}
