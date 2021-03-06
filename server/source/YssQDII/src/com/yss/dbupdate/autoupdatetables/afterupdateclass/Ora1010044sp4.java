package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010044sp4 extends BaseDbUpdate {
	
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			//alterTable(); 		
			DataInterfaceInfo();
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0044sp4 更新出错！", ex);
		}
	}
	
	/**
	 * story 1342
	 * fangjaing 2011.09.14
	 * @throws YssException
	 */
	public void alterTable() throws YssException{
		
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
		try{
			if(dbl.yssTableExist(pub.yssGetTableName("TB_DATA_OPTIONSCOST"))){
				if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("TB_DATA_OPTIONSCOST")).trim().length()!=0){
					dbl.executeSql("alter table "+pub.yssGetTableName("TB_DATA_OPTIONSCOST")+" drop constraint PK_"+pub.yssGetTableName("TB_DATA_OPTIONSCOST")+" cascade");
				}				
				if(dbl.getTableByConstaintKey("PK_"+pub.yssGetTableName("TB_DATA_OPTIONSCOST")).trim().length()!=0){
					dbl.executeSql("drop index PK_"+pub.yssGetTableName("TB_DATA_OPTIONSCOST"));
				}
				dbl.executeSql("alter table "+pub.yssGetTableName("TB_DATA_OPTIONSCOST")+" add (constraint PK_"+pub.yssGetTableName("TB_DATA_OPTIONSCOST")+
				" primary key (FNUM, FSETNUM))");

			}								
		}catch(Exception ex) {
			throw new YssException("版本1.0.1.0044sp4变更表结构出错！", ex);
		}
		finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
/**
 * added by liubo 2011.09.14
 * Story #1313
 * 升级版本时预先将所有的数据接口数据按用户和组合的顺序向TB_SYS_USERRIGHT表进行插入，以达到当前已存在的接口默认显示的目的
 */
	public void DataInterfaceInfo() throws YssException
	{
        
		String strSql = "";
		ResultSet rsUserDetail = null;
//		ResultSet rsAssetGroupDetail = null;
		ResultSet rsDataInterfaceDetail = null;
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;//add by songjie 2012.01.31 BUG 3723 QDV4赢时胜(上海)2012年01月20日03_B
		try 
		{
			conn.setAutoCommit(false);
			
			//---add by songjie 2012.01.31 BUG 3723 QDV4赢时胜(上海)2012年01月20日03_B start---//
			rs = dbl.getUserTabColumns("TB_SYS_USERRIGHT" , "FRIGHTIND");
			if(!rs.getString("DATA_LENGTH").equals("50")){
				strSql = "alter table TB_SYS_USERRIGHT modify fRightInd varchar2(50)";
				dbl.executeSql(strSql);
			}
			//---add by songjie 2012.01.31 BUG 3723 QDV4赢时胜(上海)2012年01月20日03_B end---//
			
			if(dbl.yssTableExist(pub.yssGetTableName("TB_Dao_Group")))
			{
				
				strSql = "Select * from tb_sys_userlist";
				rsUserDetail = dbl.openResultSet(strSql);
				
				strSql = "delete TB_SYS_USERRIGHT where fRightInd = 'DataInterface'  and FAssetGroupCode = '" + pub.getPrefixTB() + "'";
				dbl.executeSql(strSql);
				
				while(rsUserDetail.next())
				{
					strSql  = "select * from " + pub.yssGetTableName("TB_Dao_Group") + " where FGroupType = 'Cus'";
					rsDataInterfaceDetail = dbl.openResultSet(strSql);
					
					while(rsDataInterfaceDetail.next())
					{	
						strSql = "insert into tb_sys_userright(FUserCode,FRightType,FAssetGroupCode,FRightCode,FPortCode,FRightInd,FOperTypes)" +
						" values('" + rsUserDetail.getString("FUserCode") + "','Group','" + pub.getPrefixTB() + 
						"','" + pub.getPrefixTB() + "-" + rsDataInterfaceDetail.getString("FGroupCode") + "',' ','DataInterface','')";
						
						dbl.executeSql(strSql);
						
					}
					
					dbl.closeResultSetFinal(rsDataInterfaceDetail);

				}
				
				dbl.closeResultSetFinal(rsUserDetail);
			}
			conn.commit();
			conn.setAutoCommit(true);
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			//edit by songjie 2012.01.31 BUG 3723 QDV4赢时胜(上海)2012年01月20日03_B
			dbl.closeResultSetFinal(rsUserDetail,rsDataInterfaceDetail, rs);
			dbl.endTransFinal(conn, false);
		}
	}

}
