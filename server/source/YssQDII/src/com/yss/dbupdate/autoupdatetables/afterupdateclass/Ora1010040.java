package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;


import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;
import com.yss.util.YssFun;


public class Ora1010040 extends BaseDbUpdate {

	public Ora1010040() {

	}
	
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			
			ResultSet rs = null;
			String strSql = "select * from tb_fun_version where fvernum = '1.0.1.0040' and ffinish = 'Success'";
				
			rs = dbl.openResultSet(strSql);
				
			if(!rs.next()){
				// 先备份表数据 by leeyu 20110430
				backupRightTypeTab();
				updatePortSetRight();//组合设置有组合群级别权限变更为系统级别权限
				deleteVoc(); //删除词汇名称为‘传销’的词汇；
				updateRepGroup();
				delReportRoleRight();
				updateReportRight();
			}
			dbl.closeResultSetFinal(rs);
				
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0040 更新出错！", ex);
		}
	}
	
	/**
	 * backup table Tb_Sys_UserRight
	 * @throws YssException
	 */
	private void backupRightTypeTab() throws YssException {
		try {
			if (dbl.yssTableExist("tb_sys_userright_Bak40")) {
				dbl.executeSql("drop table tb_sys_userright_Bak40");
			}
			dbl.executeSql("create table tb_sys_userright_Bak40 as select * from tb_sys_userright");
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0040 更新出错！", ex);
		}
	}
	
	/**
	 * add by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
	 * 组合设置有组合群级别权限变更为系统级别权限
	 */
	private void updatePortSetRight() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		ResultSet rs=null;
		PreparedStatement ps=null;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			
			//更新用户权限
			strSql = " update TB_SYS_USERRIGHT set frighttype = 'system' " +
					" where fusercode = " + dbl.sqlString(pub.getUserCode()) + 
					" and fassetgroupcode = " + dbl.sqlString(pub.getAssetGroupCode()) + 
					" and frightcode = 'portfolio'";
			
			dbl.executeSql(strSql);
			
			//---add by songjie 2011.08.29 BUG 2493 QDV4海富通2011年08月19日03_B start---//
//			strSql = " insert into TB_SYS_USERRIGHT(FUSERCODE, FRIGHTTYPE, FASSETGROUPCODE, FRIGHTCODE, FPORTCODE, FRIGHTIND, FOPERTYPES ) " + 
//			         " select ur.FUserCode , 'system' as FRIGHTTYPE, FASSETGROUPCODE, 'portfolio' as FRIGHTCODE, " + 
//			         " ' ' as FPORTCODE, 'Right' as FRIGHTIND, rr.FOperTypes as FOPERTYPES " + 
//			         " from (select * from TB_SYS_USERRIGHT where FRightInd = 'Role' and FRightType = 'group' " + 
//			         " and FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + 
//			         ") ur left join (select FRolecode, FOperTypes from Tb_Sys_Roleright " + 
//			         " where FRightCode = 'portfolio') rr on ur.frightCode = rr.frolecode " + 
//			         " where rr.FOperTypes is not null and FUserCode not in " + 
//			         " (select FUserCode from TB_SYS_USERRIGHT where FRightCode = 'portfolio' and FUserCode = " + 
//			         dbl.sqlString(pub.getUserCode()) + ")";
//			
//			dbl.executeSql(strSql);	
			//---add by songjie 2011.08.29 BUG 2493 QDV4海富通2011年08月19日03_B end---//
			
			//---bug 3236 add by zhouwei 20111126 QDV4华泰证券2011年11月25日01_B 修正数据重复性造成主键违反唯一性的问题
			Map map=new HashMap();//刷选不同角色下聚合的权限
			String sql="insert into TB_SYS_USERRIGHT(FUSERCODE, FRIGHTTYPE, FASSETGROUPCODE, FRIGHTCODE, FPORTCODE, FRIGHTIND, FOPERTYPES ) values(?,?,?,?,?,?,?)";
			ps=conn.prepareStatement(sql);
			strSql=" select ur.FUserCode , 'system' as FRIGHTTYPE, FASSETGROUPCODE, 'portfolio' as FRIGHTCODE, " + 
	         " ' ' as FPORTCODE, 'Right' as FRIGHTIND, rr.FOperTypes as FOPERTYPES " + 
	         " from (select * from TB_SYS_USERRIGHT where FRightInd = 'Role' and FRightType = 'group' " + 
	         " and FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + 
	         ") ur left join (select FRolecode, FOperTypes from Tb_Sys_Roleright " + 
	         " where FRightCode = 'portfolio') rr on ur.frightCode = rr.frolecode " + 
	         " where rr.FOperTypes is not null and FUserCode not in " + 
	         " (select FUserCode from TB_SYS_USERRIGHT where FRightCode = 'portfolio' and FUserCode = " + 
	         dbl.sqlString(pub.getUserCode()) + ")";
			 rs=dbl.openResultSet(strSql);
			 while(rs.next()){
				 String FUserCode=rs.getString("FUserCode");
				 String FRIGHTTYPE=rs.getString("FRIGHTTYPE");
				 String FASSETGROUPCODE=rs.getString("FASSETGROUPCODE");
				 String FRIGHTCODE=rs.getString("FRIGHTCODE");
				 String FPORTCODE=rs.getString("FPORTCODE");
				 String FRIGHTIND=rs.getString("FRIGHTIND");
				 String FOPERTYPES=rs.getString("FOPERTYPES");
				 String key=FUserCode+"\t"+FRIGHTTYPE+"\t"+FASSETGROUPCODE+"\t"+FRIGHTCODE+"\t"+FPORTCODE+"\t"+FRIGHTIND;
				 if(map.containsKey(key)){
					 map.put(key, getMixofFOPERTYPES((String)map.get(key),FOPERTYPES));
				 }else{
					 map.put(key, FOPERTYPES);
				 }
			 }
			 dbl.closeResultSetFinal(rs);
			 Iterator it=map.entrySet().iterator();
			 while(it.hasNext()){
				 Entry en=(Entry) it.next();
				 String[] key=((String)en.getKey()).split("\t");
				 String value=(String)en.getValue();
				 ps.setString(1, key[0]);
				 ps.setString(2, key[1]);
				 ps.setString(3, key[2]);
				 ps.setString(4, key[3]);
				 ps.setString(5, key[4]);
				 ps.setString(6, key[5]);
				 ps.setString(7, value);
				 ps.addBatch();
			 }
			ps.executeBatch();
			//---bug 3236 end 20111126 QDV4华泰证券2011年11月25日01_B
			
			//删除相应的角色权限
			strSql = " delete from Tb_Sys_Roleright where frightcode = 'portfolio' ";
			
			dbl.executeSql(strSql);	
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("组合设置有组合群级别权限变更为系统级别权限出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeResultSetFinal(rs);//---bug 3236 add by zhouwei 20111126 QDV4华泰证券2011年11月25日01_B
			dbl.closeStatementFinal(ps); 
			
		}
	}
	//---bug 3236 add by zhouwei 20111126 QDV4华泰证券2011年11月25日01_B  得到角色权限的交集
	private String getMixofFOPERTYPES(String mix1,String mix2){
		String[] mix2s=mix2.split(",");
		for(int i=0;i<mix2s.length;i++){
			if(mix1.indexOf(mix2s[i].trim())==-1){
				mix1+=","+mix2s[i].trim();
			}
		}
		return mix1;
	}
	
	/**
	 * add by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
	 * 删除词汇名称为‘传销’的词汇；
	 */
	private void deleteVoc() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			
			//删除词汇名称为‘传销’的词汇；
			strSql = " delete from tb_fun_vocabulary where fvocname = '传销' ";
	
			dbl.executeSql(strSql);	
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除词汇名称为‘传销’的词汇出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	
	
	/**
	 * add by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
	 * 更新报表组表中的数据；
	 */
	private void updateRepGroup() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		
		String strGroupCode = "";
		ResultSet rs = null;
		ResultSet rsGroup = null;
		ResultSet rsTemp = null;
		ArrayList listGroup = new ArrayList();
		
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			
			strSql = " select FAssetGroupCode from tb_sys_assetgroup order by fassetgroupcode";
        	
        	rsGroup = dbl.openResultSet(strSql);
        	
        	while(rsGroup.next()){
        		strGroupCode = rsGroup.getString("FAssetGroupCode");
        		if (dbl.yssTableExist("Tb_" + strGroupCode + "_Rep_Group")){
        			listGroup.add(strGroupCode);
        		}
        	}
        	dbl.closeResultSetFinal(rsGroup);
        	
        	
        	for (int i = 0; i < listGroup.size(); i++){
        		strGroupCode = (String)listGroup.get(i);
        		
        		strSql = " select * from Tb_" + strGroupCode + "_Rep_Group" 
    					+ " where FRepGrpCode = '[" + strGroupCode + "]'";
    	
        		rsTemp = dbl.openResultSet(strSql);
            	
            	if (!rsTemp.next())
    	        {
            		strSql = " update Tb_" + strGroupCode + "_Rep_Group set FParentCode = "
		    				+ "'[" + strGroupCode + "]' where FParentCode = '[root]'" ;
		    		
		    		dbl.executeSql(strSql);
		    		
		    		
		    		strSql = " select FRepGrpCode,FOrderCode from Tb_" + strGroupCode + "_Rep_Group";
		    		
		    		rs = dbl.openResultSet(strSql);
		    		
		    		while(rs.next()){
		    			
		    			strSql = " update Tb_" + strGroupCode + "_Rep_Group set FOrderCode = "
								+ dbl.sqlString("000" + rs.getString("FOrderCode")) 
								+ " where FRepGrpCode = " + dbl.sqlString(rs.getString("FRepGrpCode"));
		    			
		    			dbl.executeSql(strSql);
		    		}
		    		dbl.closeResultSetFinal(rs);
		    		
		
		    		strSql = " insert into Tb_" + strGroupCode + "_Rep_Group" +
			               	 " (FRepGrpCode,FRepGrpName,FParentCode,FOrderCode,FCheckState,FCreator,FCreateTime)" +
			                 " values( " + dbl.sqlString("[" + strGroupCode + "]") +
			                 "," + dbl.sqlString(strGroupCode + "组合群报表组") +
			                 "," + dbl.sqlString("[root]") +
			                 "," + dbl.sqlString("000") +
			                 ",1" + 
			                 "," + dbl.sqlString("admin") +
			                 "," + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
			                 ")";
		        		
		        	dbl.executeSql(strSql);
            		
    	        }
            	dbl.closeResultSetFinal(rsTemp);
        	}	
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新报表组表中的数据", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeResultSetFinal(rs,rsGroup,rsTemp);
		}
	}
	
	/**
	 * add by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
	 * 先删除public,group,report类型的用户角色权限
	 * 再对public,group类型的用户角色权限进行转换
	 * 
	 * 组合群级别的用户角色权限必须为是当前组合群下所有组合角色权限的并集；
	 * 公共级别的用户角色权限必须为是所有组合群下所有组合角色权限的并集；
	 * 现在的角色权限都是从组合角色权限中带出来；
	 */
	private void delReportRoleRight() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			
			//删除所有用户，所有组合群report类型的用户角色权限
			//------ modify by wangzuochun BUG 2024 系统从V4.1.29.39_sp3版本升级到41版本时，系统报错
			strSql = " delete from tb_sys_userright where FRightType = 'report'" +
					 " and FRightInd = 'Role'";
			//------------------ BUG 2024 ----------------//
			
			dbl.executeSql(strSql);
			
			//删除public,group类型的用户角色权限
			strSql = " delete from tb_sys_userright where FRightInd = 'Role'" +
					" and FRightType in ('public','group')";
			
			dbl.executeSql(strSql);	
			
			//转换组合群级别角色权限数据
			//插入group类型的用户角色权限，group类型的用户角色权限是当前组合群下所有组合角色权限的并集；
			strSql = " insert into tb_sys_userright (fusercode,frighttype,fassetgroupcode,"
					+ " frightcode,frightind,fopertypes,FPortcode)" 
					+ " (select r.*, ' ' as FPortcode from (select fusercode,"
					+ " 'group' as frighttype,fassetgroupcode,frightcode,frightind,"
					+ " fopertypes from tb_sys_userright where FRightType = 'port' "
					+ " and frightind = 'Role' group by fusercode,frighttype,fassetgroupcode,"
					+ " frightcode,frightind,fopertypes) r)";
			
			dbl.executeSql(strSql);
			
			
			//转换公共级别角色权限数据
			//插入public类型的用户角色权限,public类型的用户角色权限是所有组合群下所有组合角色权限的并集；
			strSql = " insert into tb_sys_userright (fusercode,frighttype,frightcode,"
					+ " frightind,fopertypes,fassetgroupcode,FPortcode)" 
					+ " (select r.*,' ' as fassetgroupcode,' ' as FPortcode from (select fusercode,"
					+ " 'public' as frighttype,frightcode,frightind,fopertypes"
					+ " from tb_sys_userright where FRightType = 'port' "
					+ " and frightind = 'Role' group by fusercode,frighttype,frightcode,"
					+ " frightind,fopertypes) r)";
			
			dbl.executeSql(strSql);
			
			
			//转换报表级别角色权限数据
			//插入report类型的用户角色权限,report类型的用户角色权限是所有组合群下所有组合角色权限的并集；
			strSql = " insert into tb_sys_userright (fusercode,frighttype,frightcode,"
					+ " frightind,fopertypes,fassetgroupcode,FPortcode)" 
					+ " (select r.*,' ' as fassetgroupcode,' ' as FPortcode from (select fusercode,"
					+ " 'report' as frighttype,frightcode,frightind,fopertypes"
					+ " from tb_sys_userright where FRightType = 'port' "
					+ " and frightind = 'Role' group by fusercode,frighttype,frightcode,"
					+ " frightind,fopertypes) r)";
			
			dbl.executeSql(strSql);
			
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除public,group,report类型的用户角色权限出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	
	
	
	/**
	 * add by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
	 * 更新用户报表组权限；
	 */
	private void updateReportRight() throws YssException {
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		
		String strGroupCode = "";
		String strUserCode = "";
		ResultSet rs = null;
		ResultSet rsTemp = null;
		ArrayList listGroup = new ArrayList();
		ArrayList listUser = new ArrayList();
		
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			
			strSql = " select FAssetGroupCode from tb_sys_assetgroup order by fassetgroupcode";
        	
			rsTemp = dbl.openResultSet(strSql);
        	
        	while(rsTemp.next()){
        		strGroupCode = rsTemp.getString("FAssetGroupCode");
        		listGroup.add(strGroupCode);
        	}
        	dbl.closeResultSetFinal(rsTemp);
        	
        	
        	strSql = " select * from tb_sys_userlist order by fusercode";
        	
        	rsTemp = dbl.openResultSet(strSql);
        	
        	while(rsTemp.next()){
        		strUserCode = rsTemp.getString("fusercode");
        		listUser.add(strUserCode);
        	}
        	dbl.closeResultSetFinal(rsTemp);
        	
        	for (int i = 0; i < listUser.size(); i++) {
        		strUserCode = (String)listUser.get(i);
        		
        		for (int j = 0; j < listGroup.size(); j++) {
        			strGroupCode = (String)listGroup.get(j);
        			
        			strSql = " select * from tb_sys_userright " 
    					+ " where fusercode = " + dbl.sqlString(strUserCode)
    					+ " and fassetgroupcode = " + dbl.sqlString(strGroupCode)
    					+ " and frighttype = 'group' and frightcode = " + dbl.sqlString("[" + strGroupCode + "]")
    					+ " and fportcode = ' ' and frightind = 'Report'";
    	
        			rsTemp = dbl.openResultSet(strSql);
        			
        			if (!rsTemp.next()){
        				
        				strSql = " select * from tb_sys_userright " 
        					+ " where fusercode = " + dbl.sqlString(strUserCode)
        					+ " and fassetgroupcode = " + dbl.sqlString(strGroupCode)
        					+ " and frighttype = 'group' and frightcode <> '[root]'"
        					+ " and fportcode = ' ' and frightind = 'Report'";
        				
        				rs = dbl.openResultSet(strSql);
        				
        				if (rs.next()){
        					strSql = " insert into tb_sys_userright" +
			               	 " (FuserCode,frighttype,fassetgroupcode,frightcode,fportcode,frightind)" +
			                 " values( " + dbl.sqlString(strUserCode) +
			                 ",'group'," + dbl.sqlString(strGroupCode) +  
			                 "," + dbl.sqlString("[" + strGroupCode + "]") +
			                 ",' ','Report')";
        					
        					dbl.executeSql(strSql);
        				}
        				dbl.closeResultSetFinal(rs);
        			}
        			dbl.closeResultSetFinal(rsTemp);
        		}
        	}

			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新用户报表组权限", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeResultSetFinal(rs,rsTemp);
		}
	}
}
