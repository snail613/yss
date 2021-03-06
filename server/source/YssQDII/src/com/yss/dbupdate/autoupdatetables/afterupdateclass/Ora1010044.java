package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.*;
import java.util.HashMap;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by guolongchao 2011.08.24
 * 需求 1207 QDV4易方达2011年6月9日01_A_需求规格说明书.doc 
 * 删除通用参数：业务按成交顺序处理
 */
public class Ora1010044 extends BaseDbUpdate {
	public Ora1010044(){
		
	}
	
	/**
     * add by  guolongchao 2011.08.24
     * 需求 1207 QDV4易方达2011年6月9日01_A_需求规格说明书.doc
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateTable(hmInfo);
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0044 更新出错！", ex);
		}
	}
	
	/**
     * add by  guolongchao 2011.08.24
     * 需求 1207 QDV4易方达2011年6月9日01_A_需求规格说明书.doc
	 */
	public void updateTable(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
        sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		ResultSet rs=null;
		StringBuffer sb = new StringBuffer();
		String temp=null;
	    String[] alStr=null;
		Connection conn = dbl.loadConnection();
		try{
			strSql="select ftabprefix from tb_sys_assetgroup";//查询出所有的组合群的表前缀
			rs=dbl.openResultSet(strSql);
			while(rs.next())
			{
				sb.append(rs.getString("ftabprefix")+",");
			}
		    if(sb.length()>0)
		    	temp=sb.substring(0, sb.length()-1);
		    
            conn.setAutoCommit(bTrans);//事物开始
            bTrans = true;
			//删除控件组（控件组代码：CtlInterBankOrder）
			if(dbl.yssTableExist("Tb_PFSys_FaceCfgInfo")){						
				strSql ="delete from  Tb_PFSys_FaceCfgInfo where fctlgrpcode='CtlInterBankOrder'";	
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);			
				updTables.append("Tb_PFSys_FaceCfgInfo");
			}
			if(temp!=null&&temp.length()>0)
				alStr=temp.split(",");
			if(alStr!=null&&alStr.length>0)
			{
				for(int i=0;i<alStr.length;i++)
				{
					//删除跨组合群的通用参数：业务按成交顺序处理
					String tablename="TB_"+alStr[i].trim()+"_PFOper_PUBPARA";
					if(dbl.yssTableExist(tablename)){			
						strSql ="delete from "+tablename+" where fpubparacode='interbankorder'";	
						sqlInfo.append(strSql);
						dbl.executeSql(strSql);				
						updTables.append(tablename);
					}
				}
			}
            conn.commit();
            conn.setAutoCommit(bTrans);//事物结束
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0044 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}
}
