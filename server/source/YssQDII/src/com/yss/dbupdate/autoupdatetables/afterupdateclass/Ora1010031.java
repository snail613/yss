package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010031 extends BaseDbUpdate {
	public Ora1010031() {
    }
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			deleteVchProject();
			deleteSchProject();
			updateFlow();
			createCashCheck();
			dropOracleViews(hmInfo);//具体删除视图方法 xuqiji 20100712 MS01423 系统测试时对数据库进行静态检查，发现数据库QDII中有无效视图对象 QDV4建行2010年07月08日01_B 
			deletePubPara();//add by yanghaiming 20100713 删除通用业务类型设定中原有的收益计提节点下的项
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0031 更新出错！", ex);
		}
	}
	
	/***
	 * 凭证生成方案只保留历史已审核的有效数据
	 * yanghaiming add by 20100709
	 * @throws YssException
	 */
	private void deleteVchProject() throws YssException{
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = "delete " + pub.yssGetTableName("Tb_Vch_Project")
					+ " where FCHECKSTATE <> 1 ";
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除凭证生成方案设置出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	
	/***
	 * 调度方案只保留历史已审核的有效数据
	 * yanghaiming add by 20100709
	 * @throws YssException
	 */
	private void deleteSchProject() throws YssException{
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql =  "delete " + pub.yssGetTableName("Tb_PFOper_SchProject")
					+ " where FCHECKSTATE <> 1 ";
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除调度方案设置出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	/**
	 * MS01272  add by zhangfa  2010.07.11      QDV4招商基金2010年6月8日01_A
	 * @方法名：insertFlow
	 * @参数：
	 * @返回类型：void
	 * @说明：更新Tb_Fun_Flow表中的历史数据
	 */
	private void updateFlow() throws YssException {
		Connection conn = null;
		boolean bTrans = false;
		String altsql = "";
		String qsql = "";
		String upsql = "";
		String substr = "";
		StringBuffer sbf = new StringBuffer();
		ResultSet rs = null;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			if (existsTabColumn_Ora("TB_Fun_Flow", "FDaoGroup")) {
				altsql = "alter table Tb_Fun_Flow add FDaoGroup varchar2(4000)";
				dbl.executeSql(altsql);
			}

			qsql = "select fgroupcode from "
					+ pub.yssGetTableName("Tb_Dao_Group")
					+ " where fcheckstate = 1 order by FGroupCode";
			rs = dbl.openResultSet(qsql);
			while (rs.next()) {
				sbf.append(rs.getString("Fgroupcode")).append(",");
			}
			//modify by zhangfa MS01648    新建组合群，首次登录时报错，更新到31版本报错。    QDV4建行2010年08月25日02_B    
			if (sbf.length() > 0) {
				substr = sbf.substring(0, sbf.length() - 1);
				upsql = "update Tb_Fun_Flow set FDaoGroup = "
						+ dbl.sqlString(substr)
						+ " where fmenucode='interfacedeal' and FDaoGroup = ''";
				dbl.executeSql(upsql);

				conn.commit();
				bTrans = false;
			}
			//---------------------------------------------------------------------------------------------------
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新流程表出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	/**
	 * 删除数据库中无效视图
	 * xuqiji 20100712 MS01423 系统测试时对数据库进行静态检查，发现数据库QDII中有无效视图对象 QDV4建行2010年07月08日01_B  
	 * @param hmInfo
	 * @throws YssException 
	 */
	private void dropOracleViews(HashMap hmInfo) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
        StringBuffer sqlInfo = null;
        StringBuffer updTables = null;
        sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try{
			buff = new StringBuffer(200);
			buff.append(" select v.View_Name as viewname, o.Status from user_views v");
			buff.append(" left join (select * from user_objects where object_type = 'VIEW') o on v.VIEW_NAME =");
			buff.append(" o.OBJECT_NAME where o.Status <> 'VALID'");
			
			sqlInfo.append(buff.toString());
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			while(rs.next()){
				dbl.executeSql(" drop view " + rs.getString("viewname"));
				
				sqlInfo.append(" drop view " + rs.getString("viewname"));

				updTables.append(rs.getString("viewname"));
			}
			
		}catch (Exception e) {
			throw new YssException("删除数据库中无效视图出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	private void deletePubPara() throws YssException{
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql =  "delete " + pub.yssGetTableName("TB_PFOper_PUBPARA")
					+ " where FPARAGROUPCODE = 'Income' and (FPUBPARACODE = 'bond' or FPUBPARACODE = 'cash' or FPUBPARACODE = 'fee' or FPUBPARACODE = 'purchase') ";
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除调度方案设置出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	
	/**
	 * add by zhangfa MS01249
	 * @throws YssException 
	 * @方法名：createCashCheck
	 * @参数：
	 * @返回类型：
	 * @说明：创建现金库存核对表
	 */
	public void createCashCheck() throws YssException {
		Connection conn = null;
		boolean bTrans = false;
		String createStr = "";
		String pkStr = "";
		try {
			if (dbl.yssTableExist("Tb_temp_Cash_Stock_Check")) {
				return;
			}
			createStr ="create table Tb_temp_Cash_Stock_Check(Account_Number varchar2(30) ," +
					"Balance_Dif number(30,4) ,FAccBalance number(30,4),FAccBalanceJP number(30,4)," +
					"FAssetGroupCode varchar2(30) ,FCashAccCode varchar2(30) not null,FCashAccName varchar2(100)," +
					"FCuryCode  varchar2(30), FStorageDate Date not null,FCheckState number(1) not null," +
					"FCreator  varchar2(20) not null,FCreateTime  varchar2(20) not null," +
					"FCheckUser  varchar2(20),FCheckTime  varchar2(20))"; 
			dbl.executeSql(createStr);

			//pkStr = "alter table Tb_temp_Cash_Stock_Check add constraint" +
				//	" PK_Tb_temp_Cash_Stock_Check primary key (FStorageDate,FCashAccCode)";
			//dbl.executeSql(pkStr);
		} catch (Exception e) {
			throw new YssException("创建现金库存核对表出错！", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
