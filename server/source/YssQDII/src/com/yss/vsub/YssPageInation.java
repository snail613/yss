package com.yss.vsub;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;

import com.yss.dsub.BaseBean;
import com.yss.pojo.sys.YssPageInationBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**'
 * 系统查询大批量数据时采用分页处理模式，此类为实现类,实现视图的创建并返回查询用的SQL
 * 原理如下：
 * 首先根据外部传过来的带有条件的SQL语句创建一个查询视图，视图中保存有行号
 * 查询视图中的数据，按已审核、未审核、回收站状态并出查询的SQL（若系统不区分已审核与未审核页时，已审核与未审核并在一个页中统计）
 * QDV4赢时胜上海2009年12月21日06_B MS00884
 * @author leeyu
 *
 */
public class YssPageInation extends BaseBean {
	public YssPageInation(){
	}
	private YssPageInationBean pageInationBean=null;
	private String sRealViewName=""; 					//创建视图的名称
	private final String sRowNum="FRowNum_SQ";			//固定行号字段
	private final String sRowType="FPageInation_Type_SQ";//行级类型 新加 合并太平版本优化调整 by leeyu 20100813
	
	/**
	 * 创建视图过程(内部方法)
	 */
	private void createQueryProcedure() throws YssException{
		ResultSet rs1 = null;//shashijie 2011.05.31,BUG1977
		ResultSet rs2 = null;//shashijie 2011.05.31,BUG1977
		ResultSet rs3 = null;//shashijie 2011.05.31,BUG1977
		ResultSet rs4 = null;//shashijie 2011.05.31,BUG1977
		StringBuffer buf=new StringBuffer();
		try{
			if(dbl.dbType==YssCons.DB_ORA){
				//buf.append("create or replace view ").append(sRealViewName).append(" as ");
				//buf.append("(select querys.* from (");
				//buf.append(pageInationBean.getsQuerySQL()).append(") querys)");
				//这里调整为创建表，表比视图速度快些  合并太平版本优化调整 by leeyu 20100813
				//--- add by songjie 2013.04.26 BUG 7636 QDV4赢时胜(上海)2013年04月26日01_B start---//
				if(dbl.yssTableExist(sRealViewName)){
					buf.append("drop table ").append(sRealViewName);
					dbl.executeSql(buf.toString());
					buf.setLength(0);
				}
				if(dbl.yssViewExist(sRealViewName)){
					buf.append("drop view ").append(sRealViewName);
					dbl.executeSql(buf.toString());
					buf.setLength(0);
				}
				//--- add by songjie 2013.04.26 BUG 7636 QDV4赢时胜(上海)2013年04月26日01_B end---//
				if(dbl.yssProcedureExist(sRealViewName)){
					buf.append("drop procedure ").append(sRealViewName);
					/**shashijie ,2011-10-12 , STORY 1698*/
					dbl.executeSql(buf.toString());
					/**end*/
					buf.setLength(0);
				}
				/**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
				if (pageInationBean.getsTableName().equals("SubTrade_TaTrade")
						|| pageInationBean.getsTableName().equals("SubTrade_TaTrade2")) {//针对结算页面创建新表
//					buf.append("create table ").append(sRealViewName).append(" as ");
					buf.append(" create or replace procedure ").append(sRealViewName).append(" (jsStartPage in number,jsEndPage in number,");
					buf.append(" wjsStartPage in number,wjsEndPage in number, hzStartPage in number,hzEndPage in number,");    
					buf.append(" ysStartPage in number,ysEndPage in number,p_Cursor out YssQueryPackage.Yss_cursor,r_Cursor out YssQueryPackage.Yss_cursor)");
					buf.append(" is BEGIN ");
					buf.append(" OPEN  r_Cursor For select count(1) as FCount, FSettleState from (").append(pageInationBean.getsQuerySQL()).append(") group by FSettleState order by FSettleState asc;");
					buf.append(" OPEN p_Cursor FOR SELECT * FROM (");
					buf.append(" select * from ( ");
					buf.append(" select querys0.*,rowNum as ").append(sRowNum).append(" from (");
					buf.append(pageInationBean.getsQuerySQL()).append(") querys0 where querys0.FSettleState=0");				
					buf.append(") where ").append(sRowNum).append(" between wjsStartPage and wjsEndPage ");
					buf.append(" union all " );
					buf.append(" select * from ( ");
					buf.append("select querys1.*,rowNum as ").append(sRowNum).append(" from (");
					buf.append(pageInationBean.getsQuerySQL()).append(") querys1 where querys1.FSettleState=1");				
					buf.append(") where ").append(sRowNum).append(" between jsStartPage and jsEndPage ");
					buf.append(" union all " );
					buf.append(" select * from ( ");
					buf.append("select querys2.*,rowNum as ").append(sRowNum).append(" from (");
					buf.append(pageInationBean.getsQuerySQL()).append(") querys2 where querys2.FSettleState=2");
					buf.append(") where ").append(sRowNum).append(" between hzStartPage and hzEndPage ");
					buf.append(" union all " );
					buf.append(" select * from ( ");
					buf.append("select querys3.*,rowNum as ").append(sRowNum).append(" from (");
					buf.append(pageInationBean.getsQuerySQL()).append(") querys3 where querys3.FSettleState=3");
					buf.append(") where ").append(sRowNum).append(" between ysStartPage and ysEndPage ");
					buf.append(" ); end ").append(sRealViewName).append(";");//针对结算页面创建新表
					//System.out.println(buf.toString());
				} else {
				/**end*/
					if(!pub.getSysCheckState()){
						buf.append(" create or replace procedure ").append(sRealViewName).append(" (auditStartPage in number,auditEndPage in number,unAuditStartPage in number,");    
						buf.append(" unAuditEndPage in number,recycledStartPage in number,recycledEndPage in number,p_Cursor out YssQueryPackage.Yss_cursor,r_Cursor out YssQueryPackage.Yss_cursor)");
						buf.append(" is BEGIN ");
						buf.append(" OPEN  r_Cursor For select count(1) as FCount, FCheckState from (").append(pageInationBean.getsQuerySQL()).append(") group by FCheckState order by FCheckState asc;");
						buf.append(" OPEN p_Cursor FOR SELECT * FROM (");
						buf.append(" select * from ( ");
						buf.append(" select querys0.*,rowNum as ").append(sRowNum).append(" from (");
						buf.append(pageInationBean.getsQuerySQL()).append(") querys0 where querys0.FCheckState in (0,1)");	
						buf.append(") where ").append(sRowNum).append(" between unAuditStartPage and unAuditEndPage ");
						buf.append(" union all ");
						buf.append(" select * from ( ");
						buf.append(" select querys2.*,rowNum as ").append(sRowNum).append(" from (");
						buf.append(pageInationBean.getsQuerySQL()).append(") querys2 where querys2.FCheckState=2");	
						buf.append(" ) where ").append(sRowNum).append(" between recycledStartPage and recycledEndPage ");
						buf.append(" ); end ").append(sRealViewName).append(";");
					}else
					{
						buf.append(" create or replace procedure ").append(sRealViewName).append(" (auditStartPage in number,auditEndPage in number,unAuditStartPage in number,");    
						buf.append(" unAuditEndPage in number,recycledStartPage in number,recycledEndPage in number,p_Cursor out YssQueryPackage.Yss_cursor,r_Cursor out YssQueryPackage.Yss_cursor)");
						buf.append(" is BEGIN ");
						buf.append(" OPEN  r_Cursor For select count(1) as FCount, FCheckState from (").append(pageInationBean.getsQuerySQL()).append(") group by FCheckState order by FCheckState asc;");
						buf.append(" OPEN p_Cursor FOR SELECT * FROM (");
						buf.append(" select * from ( ");
						buf.append(" select querys0.*,rowNum as ").append(sRowNum).append(" from (");
						buf.append(pageInationBean.getsQuerySQL()).append(") querys0 where querys0.FCheckState=0");	
						buf.append(") where ").append(sRowNum).append(" between unAuditStartPage and unAuditEndPage ");
						buf.append(" union all " );
						buf.append(" select * from ( ");
						buf.append(" select querys1.*,rowNum as ").append(sRowNum).append(" from (");
						buf.append(pageInationBean.getsQuerySQL()).append(") querys1 where querys1.FCheckState=1");	
						buf.append(") where ").append(sRowNum).append(" between auditStartPage and auditEndPage ");
						buf.append(" union all ");
						buf.append(" select * from ( ");
						buf.append(" select querys2.*,rowNum as ").append(sRowNum).append(" from (");
						buf.append(pageInationBean.getsQuerySQL()).append(") querys2 where querys2.FCheckState=2");	
						buf.append(") where ").append(sRowNum).append(" between recycledStartPage and recycledEndPage ");
						buf.append(" ); end ").append(sRealViewName).append(";");
					}
				}
				dbl.executeSql(buf.toString());//创建新表
//				buf.setLength(0);
//				
//				/**shashijie ,2011.05.31 BUG1977筛选时数据条数显示不完整，不能按照查询字段“交易券商”进行筛选 */
//				rs1 = dbl.getUserTabColumns(sRealViewName, sRowNum);
//				//如果主键列可为空,则设置成不可为空
//				if ("Y".equalsIgnoreCase(rs1.getString("NullAble"))) {
//					String strsql = "ALTER TABLE "+sRealViewName+" MODIFY "+sRowNum+" NOT NULL ";
//					dbl.executeSql(strsql);
//				}
//				rs2 = dbl.getUserTabColumns(sRealViewName, "FCheckState");
//				if ("Y".equalsIgnoreCase(rs2.getString("NullAble"))) {
//					String strsql = "ALTER TABLE "+sRealViewName+" MODIFY FCheckState NOT NULL ";
//					dbl.executeSql(strsql);
//				}
//				dbl.closeResultSetFinal(rs1,rs2);
//				//判断表中是否存在主键,有则先删除
//				rs3 = dbl.getUserConsColumns(sRealViewName, "");
//				if (rs3.next()) {
//					dbl.executeSql("ALTER TABLE "+sRealViewName+" DROP CONSTRAINT "+rs3.getString("constraint_name")+" CASCADE ");
//					/**Oracle 10g  删除主键时，不能把主键对应的索引一起删除。这是Oracle 10g 的一个Bug,所以这里还要删除索引*/
//					if(dbl.getTableByConstaintKey(rs3.getString("constraint_name")).trim().length()!=0){
//						dbl.executeSql("drop index "+rs3.getString("constraint_name"));
//					}
//				}
//				dbl.closeResultSetFinal(rs3);
//				/**shashijie 2011.05.31,BUG1977*/
//				if (pageInationBean.getsTableName().equals("SubTrade_TaTrade")
//						|| pageInationBean.getsTableName().equals("SubTrade_TaTrade2")) {
//					rs4 = dbl.getUserTabColumns(sRealViewName, "FSettleState");
//					if ("Y".equalsIgnoreCase(rs4.getString("NullAble"))) {
//						String strsql = "ALTER TABLE "+sRealViewName+" MODIFY FSettleState NOT NULL ";
//						dbl.executeSql(strsql);
//					}
//					dbl.closeResultSetFinal(rs4);
//					//针对交易结算与TA交易结算页面添加主键,按编号与结算状态作主键
//					buf.append("alter table ").append(sRealViewName);
//					buf.append(" add constraint PK_").append(YssFun.right(sRealViewName, 27));
//					buf.append(" primary key(").append(sRowNum).append(",FSettleState").append(")");//按编号与结算状态作主键
//					dbl.executeSql(buf.toString());
//				} else {
//					//添加主键字段
//					buf.append("alter table ").append(sRealViewName);
//					//edit by songjie 2011.03.30 BUG:1595 QDV4上海(39上线测试)2011年03月29日01_B
//					buf.append(" add constraint PK_").append(YssFun.right(sRealViewName, 27));
//					buf.append(" primary key(").append(sRowNum).append(",FCheckState").append(")");//按编号与审核状态作主键
//					dbl.executeSql(buf.toString());
//				}
				/**end*/
				//合并太平版本优化调整 by leeyu 20100813
			}else if(dbl.dbType==YssCons.DB_DB2){
				
			}else if(dbl.dbType==YssCons.DB_SQL){
				
			}else{
				//不做任何操作
			}
			System.out.println(this.sRealViewName);
		}catch(Exception ex){
			dbl.closeResultSetFinal(rs1,rs2,rs3,rs4);
			throw new YssException("创建分页查询存储过程出错:" + this.sRealViewName ,ex);
		}finally{
			buf = null;
		}
	}
	
	/**
	 * 设置视图的名称
	 */
	private void setViewName(){
		sRealViewName="VQ_"+
		pageInationBean.getsTableName()+
		"_"+
		//delete by songjie 2011.07.18 BUG 2274 QDV4建信2011年7月14日01_B
		//pub.getUserCode();
		//edit by songjie 2013.04.26 BUG 7636 QDV4赢时胜(上海)2013年04月26日01_B
		pub.getUserID();//修改为根据表名 和 用户ID设置视图名称
	}
	
	/**
	 * 返回查询用的SQL语句
	 */
	public ResultSet buildQuerySQL() throws YssException{
		ResultSet rs =null;
		String sqlStr="";
		int auditPage = 0;
		int unAuditPage = 0;
		int recycledPage = 0;
		int queryPages = 0;
		CallableStatement proc=null;
		try{
			if(pageInationBean==null) return null;
			setViewName();
			if(pageInationBean.isbCreateView()){
				createQueryProcedure();
			}
			if(dbl.dbType==YssCons.DB_ORA){
				if (pageInationBean.getsTableName().equals("SubTrade_TaTrade")
						|| pageInationBean.getsTableName().equals("SubTrade_TaTrade2")) {//针对交易结算页面的分页
					sqlStr = "{call " + sRealViewName + "(?,?,?,?,?,?,?,?,?,?)}";
				}else
				{
					sqlStr = "{call " + sRealViewName + "(?,?,?,?,?,?,?,?)}";
				}
				/**end*/
			}else if(dbl.dbType==YssCons.DB_DB2){
				
			}else if(dbl.dbType==YssCons.DB_SQL){
				
			}else{
				//不做任何操作
			}
			
			//modify by jsc 20121019  
			//由应用服务器管理连接池,用的是连接池提供的数据库驱动,所以这里需要强转
			CallableStatement tempProc = dbl.loadConnection().prepareCall(sqlStr);
	    	proc= dbl.WeblogicCastToOracle(tempProc);
	    	if(proc==null)
	    	{
	    		proc = tempProc; //modified by yeshenghong bug7485  20130409
	    	}
	    	if (pageInationBean.getsTableName().equals("SubTrade_TaTrade")
					|| pageInationBean.getsTableName().equals("SubTrade_TaTrade2")) {//针对结算页面创建新表
	    		proc.setInt(1, (pageInationBean.getiSettleCurrPage()-1)*pageInationBean.getPageCount()+1);            
	        	proc.setInt(2, pageInationBean.getiSettleCurrPage()*pageInationBean.getPageCount());
	        	proc.setInt(3, (pageInationBean.getiSettleNoCurrPage()-1)*pageInationBean.getPageCount()+1);  
	        	proc.setInt(4, pageInationBean.getiSettleNoCurrPage()*pageInationBean.getPageCount());            
	        	proc.setInt(5, (pageInationBean.getiSettleBackCurrPage()-1)*pageInationBean.getPageCount()+1);
	        	proc.setInt(6, pageInationBean.getiSettleBackCurrPage()*pageInationBean.getPageCount());            
	        	proc.setInt(7, (pageInationBean.getiSettleDelayCurrPage()-1)*pageInationBean.getPageCount()+1);    
	        	proc.setInt(8, pageInationBean.getiSettleDelayCurrPage()*pageInationBean.getPageCount());  
	        	proc.registerOutParameter(9, OracleTypes.CURSOR); 
	        	proc.registerOutParameter(10, OracleTypes.CURSOR);
	    		
	    	}else
	    	{
	    		proc.setInt(1, (pageInationBean.getiAuditCurrPage()-1)*pageInationBean.getPageCount()+1);            
	        	proc.setInt(2, pageInationBean.getiAuditCurrPage()*pageInationBean.getPageCount());  
	        	proc.setInt(3, (pageInationBean.getiUnAuditCurrPage()-1)*pageInationBean.getPageCount()+1);            
	        	proc.setInt(4, pageInationBean.getiUnAuditCurrPage()*pageInationBean.getPageCount());
	        	proc.setInt(5, (pageInationBean.getiRecycledCurrPage()-1)*pageInationBean.getPageCount()+1);            
	        	proc.setInt(6, pageInationBean.getiRecycledCurrPage()*pageInationBean.getPageCount());         
	        	proc.registerOutParameter(7, OracleTypes.CURSOR);
	        	proc.registerOutParameter(8, OracleTypes.CURSOR);   
	    	}
	    	proc.execute();            
//	    	ResultSet rs=null;            
	    	//int total_number=proc.getInt(3);            
//	    	rs=(ResultSet)proc.getObject(4);
	    	
//			sqlStr="select count(1) as FCount,FCheckState from " +sRealViewName+ " group by FCheckState order by FCheckState asc";
			//(pub.getSysCheckState()?"1":"0,1")
	    	dbl.setProcStmt(proc);
			if (pageInationBean.getsTableName().equals("SubTrade_TaTrade")
					|| pageInationBean.getsTableName().equals("SubTrade_TaTrade2")) {//获取页面数结果集
				rs = (ResultSet)proc.getObject(10);
				setSettlePageCount(rs);
				return (ResultSet)proc.getObject(9);
			}
			else
			{
				rs = (ResultSet)proc.getObject(8);
				while(rs.next())
				{
					if(rs.getInt("FCheckState")==0)
					{
						unAuditPage = rs.getInt("FCount");
					}
					if(rs.getInt("FCheckState")==1)
					{
						auditPage = rs.getInt("FCount");
					}
					if(rs.getInt("FCheckState")==2)
					{
						recycledPage = rs.getInt("FCount");
					}
				}
				dbl.closeResultSetFinal(rs);
			}
			
			
			if (pageInationBean.getiAuditPageCount()==-1) {
				//(pub.getSysCheckState()?"1":"0,1") False 的话 包含审核和未审核
				if(!pub.getSysCheckState())
				{
					auditPage += unAuditPage;
				}
				queryPages = (int)Math.ceil((double)auditPage/pageInationBean.getPageCount());
				if(queryPages!=0)
				{
					pageInationBean.setiAuditPageCount(queryPages);
				}
				if(!pub.getSysCheckState()){
					pageInationBean.setiUnAuditPageCount(1);
				}
				if(pageInationBean.getiAuditPageCount()==0)
					pageInationBean.setiAuditPageCount(1);
			}
			if (pageInationBean.getiUnAuditPageCount()==-1) {
				queryPages = (int)Math.ceil((double)unAuditPage/pageInationBean.getPageCount());
				if(queryPages!=0){
					pageInationBean.setiUnAuditPageCount(queryPages);
				}
				if(pageInationBean.getiUnAuditPageCount()==0)
					pageInationBean.setiUnAuditPageCount(1);
			}
			if (pageInationBean.getiRecycledPageCount()==-1) {
				queryPages = (int)Math.ceil((double)recycledPage/pageInationBean.getPageCount());
				if(queryPages!=0){
					pageInationBean.setiRecycledPageCount(queryPages);
				}
				if(pageInationBean.getiRecycledPageCount()==0)
					pageInationBean.setiRecycledPageCount(1);
			}
//			/**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
//			if (pageInationBean.getsTableName().equals("SubTrade_TaTrade")
//					|| pageInationBean.getsTableName().equals("SubTrade_TaTrade2")) {//针对交易结算页面的分页
//				
//			}
//			/**end*/
			
			// select * from tab where rownum between (icurrPage-1)*iMaxCount+1 and (icurrPage*iMaxCount)
//			if(dbl.dbType==YssCons.DB_ORA){
//				//合并太平版本优化调整 by leeyu 20100813
//				//buf.append("").append("select auditv.* from (").append("select a.*,RowNum as ").append(sRowNum).append(" from ").append(sRealViewName).append(" a where a.FCheckState in("+(pub.getSysCheckState()?"1":"1,0")+")").append(") auditv where auditv.").append(sRowNum).append(" between ").append((pageInationBean.getiAuditCurrPage()-1)*pageInationBean.getPageCount()+1).append(" and ").append(pageInationBean.getiAuditCurrPage()*pageInationBean.getPageCount());
//				//if(pub.getSysCheckState())
//				//	buf.append(" union ").append("select unauditv.* from (").append("select u.*,RowNum as ").append(sRowNum).append(" from ").append(sRealViewName).append(" u where u.FCheckState = 0) unauditv where unauditv.").append(sRowNum).append(" between ").append((pageInationBean.getiUnAuditCurrPage()-1)*pageInationBean.getPageCount()+1).append(" and ").append(pageInationBean.getiUnAuditCurrPage()*pageInationBean.getPageCount());
//				//buf.append(" union ").append("select recycledv.* from (").append("select r.*,RowNum as ").append(sRowNum).append(" from ").append(sRealViewName).append(" r where r.FCheckState = 2) recycledv where recycledv.").append(sRowNum).append(" between ").append((pageInationBean.getiRecycledCurrPage()-1)*pageInationBean.getPageCount()+1).append(" and ").append(pageInationBean.getiRecycledCurrPage()*pageInationBean.getPageCount());
//				//合并太平版本优化调整 by leeyu 20100813
////				buf.append("").append("select auditv.* from (").append("select a.*,'auditv' as ").append(sRowType).append(" from ").append(sRealViewName).append(" a where a.FCheckState in("+(pub.getSysCheckState()?"1":"1,0")+")").append(") auditv where auditv.").append(sRowNum).append(" between ").append((pageInationBean.getiAuditCurrPage()-1)*pageInationBean.getPageCount()+1).append(" and ").append(pageInationBean.getiAuditCurrPage()*pageInationBean.getPageCount());
////				if(pub.getSysCheckState())
////					buf.append(" union all ").append("select unauditv.* from (").append("select u.*,'unauditv' as ").append(sRowType).append(" from ").append(sRealViewName).append(" u where u.FCheckState = 0) unauditv where unauditv.").append(sRowNum).append(" between ").append((pageInationBean.getiUnAuditCurrPage()-1)*pageInationBean.getPageCount()+1).append(" and ").append(pageInationBean.getiUnAuditCurrPage()*pageInationBean.getPageCount());
////				buf.append(" union all ").append("select recycledv.* from (").append("select r.*,'recycledv' as ").append(sRowType).append(" from ").append(sRealViewName).append(" r where r.FCheckState = 2) recycledv where recycledv.").append(sRowNum).append(" between ").append((pageInationBean.getiRecycledCurrPage()-1)*pageInationBean.getPageCount()+1).append(" and ").append(pageInationBean.getiRecycledCurrPage()*pageInationBean.getPageCount());
////				//合并太平版本优化调整 by leeyu 20100813
////				/**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
//				buf.append("{call ").append(sRealViewName).append("(?,?,?,?,?,?,?)}");
//				if (pageInationBean.getsTableName().equals("SubTrade_TaTrade")
//						|| pageInationBean.getsTableName().equals("SubTrade_TaTrade2")) {//针对交易结算页面的分页
//					String Sql = "{call " + sRealViewName + "(?,?,?,?,?,?,?,?,?)}";
//					return Sql;
//				}
//				/**end*/
//			}else if(dbl.dbType==YssCons.DB_DB2){
//				
//			}else if(dbl.dbType==YssCons.DB_SQL){
//				
//			}else{
//				//不做任何操作
//			}	
    		return (ResultSet)proc.getObject(7);
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	//setting Method
	public void setPageInationBean(YssPageInationBean pageInationBean) {
		this.pageInationBean = pageInationBean;
	}
	//add by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B
	private void createQueryTable() throws YssException{
		StringBuffer buf=new StringBuffer();
		try{
			if(dbl.dbType==YssCons.DB_ORA){
				if(dbl.yssTableExist(sRealViewName)){
					buf.append("drop table ").append(sRealViewName);
					/**shashijie ,2011-10-12 , STORY 1698*/
					dbl.executeSql(dbl.doOperSqlDrop(buf.toString()));
					/**end*/
					buf.setLength(0);
				}
				buf.append("create table ").append(sRealViewName).append(" as ");
				buf.append("(select querys.*,rowNum as ").append(sRowNum).append(" from (");
				buf.append(pageInationBean.getsQuerySQL()).append(") querys)");
				dbl.executeSql(buf.toString());//创建新表
				buf.setLength(0);
				//添加主键字段
				buf.append("alter table ").append(sRealViewName);
				buf.append(" add constraint PK_").append(sRealViewName);
				buf.append(" primary key(").append(sRowNum).append(")");
				dbl.executeSql(buf.toString());
			}else if(dbl.dbType==YssCons.DB_DB2){
				
			}else if(dbl.dbType==YssCons.DB_SQL){
				
			}else{
				//不做任何操作
			}
		}catch(Exception ex){
			throw new YssException("创建分页查询表出错",ex);
		}finally{
			buf = null;
		}
	}
	public String buildQuerySQL1() throws YssException{
		StringBuffer buf =new StringBuffer();
		ResultSet rs =null;
		String sqlStr="";
		try{
			if(pageInationBean==null) return "";
			setViewName();
			if(pageInationBean.isbCreateView()){
				createQueryTable();
			}
			sqlStr="select ceil(count(1)/"+pageInationBean.getPageCount()+") as FCount from "+sRealViewName;
			rs =dbl.openResultSet(sqlStr);
			if(rs.next()){
				pageInationBean.setiUnAuditPageCount(rs.getInt("FCount"));
			}
			if(pageInationBean.getiUnAuditPageCount()==0){
				pageInationBean.setiUnAuditPageCount(1);
			}			
			// select * from tab where rownum between (icurrPage-1)*iMaxCount+1 and (icurrPage*iMaxCount)
			if(dbl.dbType==YssCons.DB_ORA){				
				buf.append("").append(" select * from ").append(sRealViewName).append(" where ").append(sRowNum).append(" between ").append((pageInationBean.getiUnAuditCurrPage()-1)*pageInationBean.getPageCount()+1).append(" and ").append(pageInationBean.getiUnAuditCurrPage()*pageInationBean.getPageCount());
			}else if(dbl.dbType==YssCons.DB_DB2){
				
			}else if(dbl.dbType==YssCons.DB_SQL){
				
			}else{
				//不做任何操作
			}
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return buf.toString();
	}
	//-----------------
	
	//add by fangjiang 2010.11.16 bug 254
	public String buildQuerySQL1(int begin,int end) throws YssException{
		StringBuffer buf =new StringBuffer();
		ResultSet rs =null;
		String sqlStr="";
		try{
			if(pageInationBean==null) return "";
			setViewName();
			if(pageInationBean.isbCreateView()){
				createQueryTable();
			}
			if(dbl.dbType==YssCons.DB_ORA){				
				buf.append("").append(" select * from ").append(sRealViewName).append(" where ").append(sRowNum).append(" between ")
					.append(begin).append(" and ").append(end);
			}else if(dbl.dbType==YssCons.DB_DB2){
				
			}else if(dbl.dbType==YssCons.DB_SQL){
				
			}else{
				//不做任何操作
			}
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return buf.toString();
	}
	
	/**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
	private void setSettlePageCount(ResultSet rs) throws Exception {
		int jsPages = 0;
		int wjsPages = 0;
		int hzPages = 0;
		int ysPages = 0;
		int queryPages = 0;
//		sqlStr="SELECT + count(1) AS FCount, FSettleState FROM ("+this.sRealViewName+") group by FSettleState order by FSettleState asc";
//		rs =dbl.openResultSet(sqlStr);
		while(rs.next())
		{
			if(rs.getInt("FSettleState")==0)//结算
			{
				wjsPages = rs.getInt("FCount");
			}
			if(rs.getInt("FSettleState")==1)//未结算
			{
				jsPages = rs.getInt("FCount");
			}
			if(rs.getInt("FSettleState")==2)//回转
			{
				hzPages = rs.getInt("FCount");
			}
			if(rs.getInt("FSettleState")==3)//延时
			{
				ysPages = rs.getInt("FCount");
			}
		}
		dbl.closeResultSetFinal(rs);
		if (pageInationBean.getiSettlePageCount()==-1) { // 结算
			queryPages = (int)Math.ceil((double)jsPages/pageInationBean.getPageCount());
			if(queryPages!=0){
				pageInationBean.setiSettlePageCount(queryPages);
			}
			if(pageInationBean.getiSettlePageCount()==0)
				pageInationBean.setiSettlePageCount(1);
		}
		if (pageInationBean.getiSettleNoPageCount()==-1) { //未结算
			queryPages = (int)Math.ceil((double)wjsPages/pageInationBean.getPageCount());
			if(queryPages!=0){
				pageInationBean.setiSettleNoPageCount(queryPages);
			}
			if(pageInationBean.getiSettleNoPageCount()==0)
				pageInationBean.setiSettleNoPageCount(1);
		}
		if (pageInationBean.getiSettleBackPageCount()==-1) { //回转
			queryPages = (int)Math.ceil((double)hzPages/pageInationBean.getPageCount());
			if(queryPages!=0){
				pageInationBean.setiSettleBackPageCount(queryPages);
			}
			if(pageInationBean.getiSettleBackPageCount()==0)
				pageInationBean.setiSettleBackPageCount(1);
		}
		if (pageInationBean.getiSettleDelayPageCount()==-1) { //延时
			queryPages = (int)Math.ceil((double)ysPages/pageInationBean.getPageCount());
			if(queryPages!=0){
				pageInationBean.setiSettleDelayPageCount(queryPages);
			}
			if(pageInationBean.getiSettleDelayPageCount()==0)
				pageInationBean.setiSettleDelayPageCount(1);
		}
	}
	
//	/**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
//	private String getSettleSql() {
//		String sqlString = "";
//		sqlString = 
//			"SELECT settle.* FROM (SELECT a.*, 'settle' AS "+sRowType+" FROM "+sRealViewName+" a " +
//				"WHERE a.FSettleState in (1)) settle " +
//				"WHERE settle."+sRowNum+" BETWEEN "+
//				((pageInationBean.getiSettleCurrPage()-1)*pageInationBean.getPageCount()+1)+
//						" AND "+pageInationBean.getiSettleCurrPage()*pageInationBean.getPageCount()+""
//		+" UNION ALL "+
//		"SELECT settleNo.* FROM (SELECT a.*, 'settleNo' AS "+sRowType+" FROM "+sRealViewName+" a " +
//				"WHERE a.FSettleState in (0)) settleNo " +
//				"WHERE settleNo."+sRowNum+" BETWEEN "+
//				((pageInationBean.getiSettleNoCurrPage()-1)*pageInationBean.getPageCount()+1)+
//						" AND "+pageInationBean.getiSettleNoCurrPage()*pageInationBean.getPageCount()+""
//		+" UNION ALL "+
//		"SELECT settleDelay.* FROM (SELECT a.*, 'settleDelay' AS "+sRowType+" FROM "+sRealViewName+" a " +
//				"WHERE a.FSettleState in (2)) settleDelay " +
//				"WHERE settleDelay."+sRowNum+" BETWEEN "+
//				((pageInationBean.getiSettleBackCurrPage()-1)*pageInationBean.getPageCount()+1)+
//						" AND "+pageInationBean.getiSettleBackCurrPage()*pageInationBean.getPageCount()+""
//		+" UNION ALL "+
//		"SELECT settleBack.* FROM (SELECT a.*, 'settleBack' AS "+sRowType+" FROM "+sRealViewName+" a " +
//				"WHERE a.FSettleState in (3)) settleBack " +
//				"WHERE settleBack."+sRowNum+" BETWEEN "+
//				((pageInationBean.getiSettleDelayCurrPage()-1)*pageInationBean.getPageCount()+1)+
//						" AND "+pageInationBean.getiSettleDelayCurrPage()*pageInationBean.getPageCount()+"";
//		return sqlString;
//	}
	
	
	
	
}
