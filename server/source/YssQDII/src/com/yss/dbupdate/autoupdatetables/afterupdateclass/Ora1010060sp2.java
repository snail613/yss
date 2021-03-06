package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssCons;
import com.yss.util.YssException;

/**
 * add by songjie 2013.06.17 
 * STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002
 * 
 * @author 宋洁 修改 T_PLUGIN 表字段 C_PLUGIN_NAME 的字段类型
 */
public class Ora1010060sp2 extends BaseDbUpdate {

	/**
	 * add by songjie 2013.06.17 
	 * STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002
	 * 修改 T_PLUGIN 表字段 C_PLUGIN_NAME 的字段类型
	 * 
	 * @param hmInfo  用于保存数据库更新信息
	 * @throws YssException
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateTable(hmInfo);
			/**add---huhuichao 2013.06.24 STORY #3986  导航菜单条删除“财务系统”节点，删除对应的表数据 */
			deleteTableData();
			/**end---huhuichao 2013.06.24 STORY #3986  导航菜单条删除“财务系统”节点，删除对应的表数据*/

			/**Start 20130626 added by liubo.Bug #8446.QDV4赢时胜(上海开发)2013年6月26日06_B*/
			delOldLeverMenuBar();
			/**End 20130626 added by liubo.Bug #8446.QDV4赢时胜(上海开发)2013年6月26日06_B*/
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0060sp2更新出错！", ex);
		}
	}
	
	/**
	 * add by huhuichao 2013.06.24 STORY #3986  导航菜单条删除“财务系统”节点，删除对应的表数据
	 * 
	 * @throws YssException
	 */
	private void deleteTableData() throws YssException {
		String strSql = "";
		try {
			strSql = "delete from tb_fun_navmenubar where fbarcode='financesys'";
			dbl.executeSql(strSql);
		} catch (Exception e) {
			throw new YssException("删除导航菜单条的财务系统节点出错！", e);
		}
	}

	/**
	 * add by songjie 2013.06.17 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002
	 * 修改 T_PLUGIN 表字段 C_PLUGIN_NAME 的字段类型
	 * 
	 * @param hmInfo  用于保存数据库更新信息
	 * @throws YssException
	 */
	private void updateTable(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		String strSql = "";
		try {
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
			updTables = (StringBuffer) hmInfo.get("updatetables");

			if (dbl.yssTableExist("T_PLUGIN")) {
				strSql = "alter table T_PLUGIN modify C_PLUGIN_NAME varchar2(100)";

				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				updTables.append("T_PLUGIN");
			}
			
			//--- add by songjie 2013.07.25 修改 Tb_XXX_Data_Futurestrade_TMP 表 由会话级临时表 改为 一般表  start---//
			alterFuturesTradeTmp(sqlInfo, updTables);
			//--- add by songjie 2013.07.25 修改 Tb_XXX_Data_Futurestrade_TMP 表 由会话级临时表 改为 一般表  end---//
		} catch (Exception e) {
			throw new YssException("版本 1.0.1.0060sp2更新出错！", e);
		}

	}
	
	/**
	 * add by songjie 2013.07.25 
	 * 修改 Tb_XXX_Data_Futurestrade_TMP 表, 由会话级临时表 改为 一般表
	 * @param sqlInfo
	 * @param updTables
	 * @throws YssException
	 */
	private void alterFuturesTradeTmp(StringBuffer sqlInfo, StringBuffer updTables) throws YssException{
		StringBuffer buff = null;
		String strSql = "";
		ResultSet rs = null;
		String duration = "";
		boolean createNewTable = false;
		try{
			buff = new StringBuffer();
			
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Data_Futurestrade_TMP"))) { 
	        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
	        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
	        	dbl.sqlString(pub.yssGetTableName("Tb_Data_Futurestrade_TMP".toUpperCase()));
	        	rs = dbl.openResultSet(strSql);
	        	if(rs.next()){
	        		duration = rs.getString("DURATION");
	        		
	        		//如果为会话级的临时表，则删除该表，重新创建 表名相同、表类型为 一般表 的表结构
	        		if(duration != null && duration.equals("SYS$SESSION")){
	        			createNewTable = true;
	        		}
	        	}
	        	
	        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
	        	dbl.closeResultSetFinal(rs);
	        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			}
            
			if (createNewTable) {
				buff.append(" ALTER TABLE ").append( pub.yssGetTableName("Tb_Data_Futurestrade_TMP"))
       	 	 	.append(" DROP CONSTRAINT PK_").append(pub.yssGetTableName("TB_DATA_FUTTRADE_TMP"))
       	 	 	.append(" CASCADE ");
				
	            sqlInfo.append(buff.toString());
	            dbl.executeSql(buff.toString());
	            
	            buff.setLength(0);
				
	            buff.append(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("Tb_Data_Futurestrade_TMP")));
	            sqlInfo.append(buff.toString());
				dbl.executeSql(buff.toString());
				
				buff.setLength(0);
				
				buff.append(" CREATE TABLE ").append(pub.yssGetTableName("Tb_Data_FuturesTrade_TMP"));
				buff.append(" ( ");
				buff.append(" FNUM               VARCHAR2(20) not null, ");
				buff.append(" FSECURITYCODE      VARCHAR2(50) not null, ");
				buff.append(" FPORTCODE          VARCHAR2(50) not null, ");
				buff.append(" FBROKERCODE        VARCHAR2(100) not null, ");
				buff.append(" FINVMGRCODE        VARCHAR2(50) not null, ");
				buff.append(" FTRADETYPECODE     VARCHAR2(50) not null, ");
				buff.append(" FBEGBAILACCTCODE   VARCHAR2(50) not null, ");
				buff.append(" FCHAGEBAILACCTCODE VARCHAR2(50) not null, ");
				buff.append(" FBARGAINDATE       DATE not null, ");
				buff.append(" FBARGAINTIME       VARCHAR2(20) not null, ");
				buff.append(" FSETTLEDATE        DATE not null, ");
				buff.append(" FSETTLETIME        VARCHAR2(20), ");
				buff.append(" FSETTLETYPE        NUMBER(1) default 1 not null, ");
				buff.append(" FTRADEAMOUNT       NUMBER(18,4) not null, ");
				buff.append(" FTRADEPRICE        NUMBER(20,8) not null, ");
				buff.append(" FTRADEMONEY        NUMBER(18,4) not null, ");
				buff.append(" FBEGBAILMONEY      NUMBER(18,4) not null, ");
				buff.append(" FSETTLEMONEY       NUMBER(18,4) not null, ");
				buff.append(" FBASECURYRATE      NUMBER(20,15) not null, ");
				buff.append(" FPORTCURYRATE      NUMBER(20,15) not null, ");
				buff.append(" FSETTLESTATE       NUMBER(1) default 0 not null, ");
				buff.append(" FFEECODE1          VARCHAR2(20), ");
				buff.append(" FTRADEFEE1         NUMBER(18,4), ");
				buff.append(" FFEECODE2          VARCHAR2(20), ");
				buff.append(" FTRADEFEE2         NUMBER(18,4), ");
				buff.append(" FFEECODE3          VARCHAR2(20), ");
				buff.append(" FTRADEFEE3         NUMBER(18,4), ");
				buff.append(" FFEECODE4          VARCHAR2(20), ");
				buff.append(" FTRADEFEE4         NUMBER(18,4), ");
				buff.append(" FFEECODE5          VARCHAR2(20), ");
				buff.append(" FTRADEFEE5         NUMBER(18,4), ");
				buff.append(" FFEECODE6          VARCHAR2(20), ");
				buff.append(" FTRADEFEE6         NUMBER(18,4), ");
				buff.append(" FFEECODE7          VARCHAR2(20), ");
				buff.append(" FTRADEFEE7         NUMBER(18,4), ");
				buff.append(" FFEECODE8          VARCHAR2(20), ");
				buff.append(" FTRADEFEE8         NUMBER(18,4), ");
				buff.append(" FDESC              VARCHAR2(200), ");
				buff.append(" FCHECKSTATE        NUMBER(1) not null, ");
				buff.append(" FCREATOR           VARCHAR2(20) not null, ");
				buff.append(" FCREATETIME        VARCHAR2(20) not null, ");
				buff.append(" FCHECKUSER         VARCHAR2(20), ");
				buff.append(" FCHECKTIME         VARCHAR2(20)");
				buff.append(" )");
				sqlInfo.append(buff.toString());
				dbl.executeSql(buff.toString());

				buff.setLength(0);

				buff.append("alter table ").append(pub.yssGetTableName("Tb_Data_FuturesTrade_TMP")).append(
						" add constraint PK_").append(pub.yssGetTableName("TB_DATA_FUTTRADE_TMP")).append(
						" primary key (FNUM)");

				sqlInfo.append(buff.toString());
				dbl.executeSql(buff.toString());

				updTables.append(pub.yssGetTableName("Tb_Data_Futurestrade_TMP"));
			}
		}catch(Exception e){
			throw new YssException("修改 Tb_Data_Futurestrade_TMP 表类型出错！",e);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		finally{
        	dbl.closeResultSetFinal(rs);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
	}
	
	/**
	 * 20130626 added by liubo.Bug #8446.QDV4赢时胜(上海开发)2013年6月26日06_B
	 * 在60sp1版本中有新增一个菜单条代码为“LeverConversion”(杠杆分级份额折算设置)的窗体
	 * 在60sp2中，该窗体的菜单条代码被变更为了“FrmLeverConversion”，但是菜单条名称未做更改
	 * 若某个库60sp1、60sp2两个版本都进行了升级，就会出现LeverConversion、FrmLeverConversion共存的情况
	 * 因此在权限设置界面，做任何设置，都会报hashtable的键值重复（“杠杆分级份额折算设置”键有重复）
	 * 删除掉旧的菜单条（"LeverConversion"）就不会出现这种问题
	 * @throws YssException
	 */
	private void delOldLeverMenuBar() throws YssException
	{
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		
		try
		{
			
			conn.setAutoCommit(false);
			bTrans = true;
			
			strSql = "Delete from Tb_Fun_MenuBar where FBarCode = 'LeverConversion'";
			
			dbl.executeSql(strSql);
			
			conn.commit();
	        conn.setAutoCommit(true);
	        bTrans = false;
		}
		catch(Exception ye)
		{
			throw new YssException(ye);
		}
        finally
        {
        	dbl.endTransFinal(conn, bTrans);
        }
	}
	
	
}
