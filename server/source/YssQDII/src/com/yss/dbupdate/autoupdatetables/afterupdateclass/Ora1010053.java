package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

public class Ora1010053 extends BaseDbUpdate {

	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateDevTrusteeInfo(hmInfo);//add by zhouwei 20120424 债券转托管数据更新
			updateStatisticalParameters(hmInfo);// add by zhangjun 2012-05-21 招行统计参数表
			updateCommond(hmInfo);//add by zhangjun 2012-05-22 BUG#4451
			createETFTableIndex(hmInfo);//added by panjunfang STORY #1789 恒指ETF
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0053 更新出错！", ex);
		}
	}
	
	/**
	 * ETF台帐表添加索引
	 * @param hmInfo
	 * @throws YssException
	 */
	private void createETFTableIndex(HashMap hmInfo)  throws YssException{
		String sql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try{
			//TB_ETF_STANDINGBOOK
			//1.判断索引是否存在，不存在则创建
			if(dbl.getTableIndexKey("ID_TB_"+pub.getAssetGroupCode()+"_ETF_STANDINGBOOK").equalsIgnoreCase("")){				
				sql = "create index ID_TB_"+pub.getAssetGroupCode()+"_ETF_STANDINGBOOK on "+
				      pub.yssGetTableName("TB_ETF_STANDINGBOOK")+" (FDATE)";
				dbl.executeSql(sql);
				sqlInfo.append(sql);
				updTables.append("TB_ETF_STANDINGBOOK");
			}      
		}catch (Exception e) {
			throw new YssException("台帐表创建索引出错！", e);
		}
	}

	/** 
	 * add by zhouwei 20120424  
	 * 债券转托管数据的转出或转入债券为银行间债券的，所属分类为空
	* @Title: updateDevTrusteeInfo 
	* @Description: TODO
	* @param @param hmInfo
	* @param @throws YssException    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public void updateDevTrusteeInfo(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		ResultSet rs=null;
		PreparedStatement pst=null;
		try{
			strSql = "CREATE OR REPLACE PACKAGE YssQueryPackage AS TYPE Yss_cursor IS REF CURSOR; end YssQueryPackage;";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			strSql="select * from "+pub.yssGetTableName("TB_DATA_DEVTRUSTBOND");
			rs=dbl.openResultSet(strSql);
			if(!dbl.isFieldExist(rs, "FInATTRCLSCODE")){//字段不存在，更新表字段
				dbl.closeResultSetFinal(rs);
				strSql="alter table "+pub.yssGetTableName("TB_DATA_DEVTRUSTBOND")+" add  (FInINVESTTYPE VARCHAR2(20) default 'C', FInATTRCLSCODE VARCHAR2(20) default ' ')";
				dbl.executeSql(strSql);//增加转入投资和所属分类
				conn.setAutoCommit(bTrans);
	            bTrans = true;
	            strSql="select a.FNUM,a.FSECURITYCODE,a.FINSECURITYCODE,"
	            	  +"case when b.FEXCHANGECODE='CY' then ' ' else a.FATTRCLSCODE end as FATTRCLSCODE," //转出债券为银行间
	            	  +"case when c.FEXCHANGECODE='CY' then ' ' else a.FATTRCLSCODE end as FInATTRCLSCODE,"//转入债券为银行间
	            	  //如果转入债券不是银行间债券，需要把转出投资类型复制到转入投资类型
	            	  +"case when c.FEXCHANGECODE='CY' then a.FInINVESTTYPE else a.FINVESTTYPE end as FInINVESTTYPE," 
	            	  +"a.FINVESTTYPE as FINVESTTYPE"
	            	  +" from "+pub.yssGetTableName("TB_DATA_DEVTRUSTBOND")
	                  +" a left join "+pub.yssGetTableName("Tb_Para_Security") + " b"
	                  +" on a.FSecurityCode=b.FSecurityCode"
	                  +" left join "+pub.yssGetTableName("Tb_Para_Security") + " c"
	                  +" on a.FInSecurityCode=c.FSecurityCode";
	            rs=dbl.openResultSet(strSql);
	            strSql="update "+pub.yssGetTableName("TB_DATA_DEVTRUSTBOND")
	                  +" set FATTRCLSCODE=?,FINVESTTYPE=?,FInINVESTTYPE=?,FInATTRCLSCODE=?"
	                  +" where FNUM=?";
	            pst=dbl.getPreparedStatement(strSql);
	            while(rs.next()){           	
	            	pst.setString(1, rs.getString("FATTRCLSCODE"));
	            	pst.setString(2, rs.getString("FINVESTTYPE"));
	            	pst.setString(3, rs.getString("FInINVESTTYPE"));
	            	pst.setString(4, rs.getString("FInATTRCLSCODE"));
	            	pst.setString(5, rs.getString("fnum"));
	            	pst.addBatch();
	            }
	           pst.executeBatch();
	    	   sqlInfo.append(strSql);
		       updTables.append("TB_DATA_DEVTRUSTBOND");		    
	           conn.commit();
	           conn.setAutoCommit(bTrans);
	           bTrans = false;
			}     
			
		}catch(Exception e){
			throw new YssException("1.0.1.0053 版本更新出错！", e);
		}finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeStatementFinal(pst);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		}
	}
	
	/**
	 * add by zhangjun 2012-05-21 招商银行统计参数
	 * @param hmInfo
	 * @throws YssException
	 */
	public void updateStatisticalParameters(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try{
            conn.setAutoCommit(bTrans);
            bTrans = true;
            if(!dbl.yssTableExist("StatisticalParameters") )//表存在先删除
            {
    			strSql = " create table StatisticalParameters " +
    				 "( FYYFH  VARCHAR2(50) not null," +
    				 " FTZGLR  VARCHAR2(20) not null," + 
   			   		 " FWTR       VARCHAR2(100), " +
   			   		 " FSTR       VARCHAR2(100)," +
   			   		 " FZCLB1     VARCHAR2(50) not null," +
   			   		 " FZCLB2     VARCHAR2(50) not null," +
   			   		 " FZCLB3     VARCHAR2(50) not null,  " +
   			   		 " FTZGLFS    NUMBER(1) not null,"+
   			   	     " FXSQD      NUMBER(1) not null," +
   			   	     " FXSDM      VARCHAR2(50)," +
   			   	     " FYYLB1     VARCHAR2(50) not null," + 
   			   		 " FYYLB2     VARCHAR2(50) not null," +
   			   		 " FTGZH      VARCHAR2(100) not null," +
   			   		 " FSTARTDATE DATE not null," +
   			   		 " FENDDATE   DATE not null," +
   			   		 " FTGFL      NUMBER(30,12)," +
   			   		 " FSETCODE   VARCHAR2(22) not null," +
   			   		 " FLYFH      VARCHAR2(50) not null)";
               sqlInfo.append(strSql);
               dbl.executeSql(strSql);
               strSql = "alter table StatisticalParameters add constraint PK_StatisticalParameters primary key (FSETCODE)";
               sqlInfo.append(strSql);
               dbl.executeSql(strSql);
               updTables.append("StatisticalParameters");		
               
            }
            //dbl.closeResultSetFinal(rs);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0053 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * zhangjun 2012-05-22 BUG#4451
	 * @param hmInfo
	 * @throws YssException
	 */
	public void updateCommond(HashMap hmInfo) throws YssException{
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try{
            conn.setAutoCommit(bTrans);
            bTrans = true;
            if(!dbl.yssTableExist(pub.yssGetTableName("TB_data_commondModel")) )//表存在先删除
            {
    			strSql = " create table " + pub.yssGetTableName("TB_data_commondModel") +
    					"( FCommmondCODE VARCHAR2(20) not null," +
    					"FCommmondNAME VARCHAR2(50) not null," +
    					"FROWS          NUMBER(5) default 0 not null," +
    					"FCOLS          NUMBER(5) default 0 not null," +
    					"FFIXROWS       NUMBER(5) default 0 not null," +
    					"FFIXCOLS       NUMBER(5) default 0 not null," +
    					"FRCSIZE        VARCHAR2(2000)," +
    					"FMERGE         VARCHAR2(1000)," +
    					"FBALFMT        VARCHAR2(500)," +
    					"FPRINT         VARCHAR2(200)," +
    					"FBEANID        VARCHAR2(30)," +
    					"FREPTYPE       VARCHAR2(20) not null," +
    					"FCTLGRPCODE    VARCHAR2(20)," +
    					"FDESC          VARCHAR2(100)," +
    					"FCHECKSTATE    NUMBER(1) not null," +
    					"FCREATOR       VARCHAR2(20) not null," +
    					"FCREATETIME    VARCHAR2(20) not null," +
    					"FCHECKUSER     VARCHAR2(20)," +
    					"FCHECKTIME     VARCHAR2(20)," +
    					"FTITLTFORMAT   VARCHAR2(100) )" ;
               sqlInfo.append(strSql);
               dbl.executeSql(strSql);
               strSql = "alter table " + pub.yssGetTableName("TB_data_commondModel")+" add constraint PK_" +
               			pub.yssGetTableName("TB_data_commondModel")+" primary key (FCommmondCODE)";
               sqlInfo.append(strSql);
               dbl.executeSql(strSql);
               updTables.append("TB_data_commondModel");	
               
               
               strSql = " insert into " + pub.yssGetTableName("TB_data_commondModel") + 
               			"(FCommmondCODE ,FCommmondNAME ,FROWS ,FCOLS ,FFIXROWS,FFIXCOLS, FRCSIZE ,FMERGE ,FBALFMT, FPRINT,FBEANID ,FREPTYPE, " +
               			"FCTLGRPCODE,FDESC ,FCHECKSTATE    ,FCREATOR ,FCREATETIME,FCHECKUSER,FCHECKTIME,FTITLTFORMAT ) " +
               			" select FRepFormatCode ,FRepFormatName ,FROWS ,FCOLS ,FFIXROWS,FFIXCOLS, FRCSIZE ,FMERGE ,FBALFMT, FPRINT,FBEANID , " +
               			"FREPTYPE,FCTLGRPCODE,FDESC ,FCHECKSTATE    ,FCREATOR ,FCREATETIME,FCHECKUSER,FCHECKTIME,FTITLTFORMAT "+
               			"from " + pub.yssGetTableName("Tb_Rep_Format") +  " where freptype='CashCommand' " ;
               sqlInfo.append(strSql);
               dbl.executeSql(strSql);
               updTables.append("TB_data_commondModel");
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0053 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
