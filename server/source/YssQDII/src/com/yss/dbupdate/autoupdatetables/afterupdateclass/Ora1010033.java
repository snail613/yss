package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**************************************************
 * @author jiangshichao  2010.08.23
 * 太平资产版本合并表结构调整。
 * 1. 创建索引
 * 2. 把太平资产新建的表合并到主流版本中(太平资产部	分表命名不规范，通过更新文件更新会有问题，所以这里命名不规范的
 *    表通过脚本更新)
 */
public class Ora1010033 extends BaseDbUpdate{

	
	public Ora1010033(){
		
	}
	
	
	/**
	 * 入口方法
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			createIndex();
			createTable();
			updateIndexfutures();  // add by fangjiang 2010.09.03
			updateVocabulary();    // add by fangjiang 2010.09.03
			updateFlow(); //add by wangzuochun  2010.08.26  MS01611    日志浏览，选择任意一条数据点击浏览，会报错。    QDV4赢时胜(测试)2010年08月13日04_B    
			deleteVoc();
			updateCusConfig();
			updateUserRight(); //add by wangzuochun 2010.09.03 MS01592    系统升级后，用户在升级前拥有的权限在升级后会部分丢失。    QDV4赢时胜深圳2010年8月16日01_B 
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0033 更新出错！", ex);
		}
	}
	
	
	
	
	
	//创建索引
	public void createIndex() throws YssException{
		String sql = "";
		try{
			//TB_001_CASH_TRANSFER
			if(dbl.getTableIndexKey("ID_DATE_"+pub.getAssetGroupCode()+"_CASH_TRANSFER").equalsIgnoreCase("")){
				//1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
				if(!dbl.getTableIndexKey("ID_DATE_CASH_TRANSFER").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_CASH_TRANSFER");
				}
				sql = "create index ID_DATE_"+pub.getAssetGroupCode()+"_CASH_TRANSFER on "+
				      pub.yssGetTableName("TB_CASH_TRANSFER")+" (FTRANSFERDATE)";
				dbl.executeSql(sql);
			}
			//TB_001_DATA_CASHPAYREC
           if(dbl.getTableIndexKey("ID_DATE_"+pub.getAssetGroupCode()+"_DATA_CASHPAYREC").equalsIgnoreCase("")){
        	 //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_DATE_DATA_CASHPAYREC").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_DATA_CASHPAYREC");
				}
        	   sql = " create index ID_DATE_"+pub.getAssetGroupCode()+"_DATA_CASHPAYREC on "+
        	         pub.yssGetTableName("TB_DATA_CASHPAYREC")+" (FTRANSDATE)";
			   dbl.executeSql(sql);
			}
           //TB_001_DATA_INVESTPAYREC
           if(dbl.getTableIndexKey("ID_DATE_"+pub.getAssetGroupCode()+"_DATA_INVESTPAYREC").equalsIgnoreCase("")){
        	 //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_DATE_DATA_INVESTPAYREC").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_DATA_INVESTPAYREC");
				}
        	   
        	   sql = "create index ID_DATE_"+pub.getAssetGroupCode()+"_DATA_INVESTPAYREC on "+
			      pub.yssGetTableName("TB_DATA_INVESTPAYREC")+" (FTRANSDATE)";
			  dbl.executeSql(sql);
           }
           //TB_001_DATA_SECRECPAY
           if(dbl.getTableIndexKey("ID_DATE_"+pub.getAssetGroupCode()+"_DATA_SECRECPAY").equalsIgnoreCase("")){
        	 //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_DATE_DATA_SECRECPAY").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_DATA_SECRECPAY");
				}
        	   
        	   sql = "create index ID_DATE_"+pub.getAssetGroupCode()+"_DATA_SECRECPAY on "+
			      pub.yssGetTableName("TB_DATA_SECRECPAY")+" (FTRANSDATE)";
			  dbl.executeSql(sql);
			}
           //TB_001_DATA_SUBTRADE
           if(dbl.getTableIndexKey("ID_DATE_"+pub.getAssetGroupCode()+"_DATA_SUBTRADE").equalsIgnoreCase("")){
        	 //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_DATE_DATA_SUBTRADE").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_DATA_SUBTRADE");
				}
        	   
        	   sql = "create index ID_DATE_"+pub.getAssetGroupCode()+"_DATA_SUBTRADE on "+
			      pub.yssGetTableName("TB_DATA_SUBTRADE")+" (FBARGAINDATE)";
			  dbl.executeSql(sql);
			}
           //TB_001_STOCK_CASHPAYREC
           if(dbl.getTableIndexKey("ID_DATE_STOCK_CASHPAYREC").equalsIgnoreCase("")){
        	 //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_DATE_STOCK_CASHPAYREC").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_STOCK_CASHPAYREC");
				}
        	   if(dbl.getTableIndexKey("ID_DATE_"+pub.getAssetGroupCode()+"_STOCK_CASHPAYREC").equalsIgnoreCase("")){
        		   sql = "create index ID_DATE_"+pub.getAssetGroupCode()+"_STOCK_CASHPAYREC on "+
 			      pub.yssGetTableName("TB_STOCK_CASHPAYREC")+" (FSTORAGEDATE)";
        		   dbl.executeSql(sql);
        	   }
			}
           //TB_001_STOCK_INVEST
           if(dbl.getTableIndexKey("ID_DATE_"+pub.getAssetGroupCode()+"_STOCK_INVEST").equalsIgnoreCase("")){
        	 //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_DATE_STOCK_INVEST").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_STOCK_INVEST");
				}
        	   
        	   sql = "create index ID_DATE_"+pub.getAssetGroupCode()+"_STOCK_INVEST on "+
			      pub.yssGetTableName("TB_STOCK_INVEST")+" (FSTORAGEDATE)";
			  dbl.executeSql(sql);
			}
           //TB_001_STOCK_INVESTPAYREC
           if(dbl.getTableIndexKey("ID_DATE_"+pub.getAssetGroupCode()+"_STOCK_INVESTPAYREC").equalsIgnoreCase("")){
        	 //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_DATE_STOCK_INVESTPAYREC").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_STOCK_INVESTPAYREC");
				}
        	   
        	   sql = "create index ID_DATE_"+pub.getAssetGroupCode()+"_STOCK_INVESTPAYREC on "+
			      pub.yssGetTableName("TB_STOCK_INVESTPAYREC")+" (FSTORAGEDATE)";
			  dbl.executeSql(sql);
			}
           //TB_001_STOCK_SECRECPAY
           if(dbl.getTableIndexKey("ID_DATE_"+pub.getAssetGroupCode()+"_STOCK_SECRECPAY").equalsIgnoreCase("")){
        	 //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_DATE_STOCK_SECRECPAY").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_STOCK_SECRECPAY");
				}
        	   
        	   sql = "create index ID_DATE_"+pub.getAssetGroupCode()+"_STOCK_SECRECPAY on "+
			      pub.yssGetTableName("TB_STOCK_SECRECPAY")+" (FSTORAGEDATE)";
			  dbl.executeSql(sql);
			}
           //TB_001_STOCK_SECRECPAY
           if(dbl.getTableIndexKey("ID_YEARM_"+pub.getAssetGroupCode()+"_STOCK_SECRECPAY").equalsIgnoreCase("")){
        	 //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_YEARM_STOCK_SECRECPAY").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_YEARM_STOCK_SECRECPAY");
				}
        	   
        	   sql = "create index ID_YEARM_"+pub.getAssetGroupCode()+"_STOCK_SECRECPAY on "+
			      pub.yssGetTableName("TB_STOCK_SECRECPAY")+" (FYEARMONTH)";
			  dbl.executeSql(sql);
			}
           //TB_001_STOCK_SECURITY
           if(dbl.getTableIndexKey("ID_DATE_"+pub.getAssetGroupCode()+"_STOCK_SECURITY").equalsIgnoreCase("")){
        	   //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_DATE_STOCK_SECURITY").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_STOCK_SECURITY");
				}
        	   
        	   sql = "create index ID_DATE_"+pub.getAssetGroupCode()+"_STOCK_SECURITY on "+
			      pub.yssGetTableName("TB_STOCK_SECURITY")+" (FSTORAGEDATE)";
			  dbl.executeSql(sql);
			}
           //TB_001_STOCK_SECURITY
           if(dbl.getTableIndexKey("ID_YEARM_"+pub.getAssetGroupCode()+"_STOCK_SECURITY").equalsIgnoreCase("")){
        	   //1.判断是否为太平资产版本 ，太平资产原有索引命名不规范，删除原有太平资产的索引
        	   if(!dbl.getTableIndexKey("ID_YEARM_STOCK_SECURITY").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_YEARM_STOCK_SECURITY");
				}
        	   
        	   sql = "create index ID_YEARM_"+pub.getAssetGroupCode()+"_STOCK_SECURITY on "+
			      pub.yssGetTableName("TB_STOCK_SECURITY")+" (FYEARMONTH)";
			   dbl.executeSql(sql);
			}
          //---- 新增索引 --- 
          // Tb_001_Data_NavData
          if(dbl.getTableIndexKey("ID_Date_"+pub.getAssetGroupCode()+"_Data_NavData").equalsIgnoreCase("")){
        	  sql = "create index ID_Date_"+pub.getAssetGroupCode()+"_Data_NavData on "+ pub.yssGetTableName("Tb_Data_NavData")+" (fnavdate,fportcode) ";
        	  dbl.executeSql(sql);
          } 
          // tb_001_rep_guessvalue
          if(dbl.getTableIndexKey("ID_Date_"+pub.getAssetGroupCode()+"_rep_guessvalue").equalsIgnoreCase("")){
        	  sql = "create index ID_Date_"+pub.getAssetGroupCode()+"_rep_guessvalue on "+ pub.yssGetTableName("tb_rep_guessvalue")+" (fdate)";
        	  dbl.executeSql(sql);
          }
          //tb_001_stock_cash
          if(dbl.getTableIndexKey("ID_Date_"+pub.getAssetGroupCode()+"_Stock_Cash").equalsIgnoreCase("")){
        	  sql = "create index ID_Date_"+pub.getAssetGroupCode()+"_Stock_Cash on "+ pub.yssGetTableName("tb_stock_cash")+" (FStorageDate)";
        	  dbl.executeSql(sql);
          }
          //tb_001_stock_TA
          if(dbl.getTableIndexKey("ID_Date_"+pub.getAssetGroupCode()+"_Stock_TA").equalsIgnoreCase("")){
        	  sql = "create index ID_Date_"+pub.getAssetGroupCode()+"_Stock_TA on "+ pub.yssGetTableName("tb_Stock_TA")+" (FStorageDate)";
        	  dbl.executeSql(sql);
          }
          //Tb_001_Data_Trade
          if(dbl.getTableIndexKey("ID_Date_"+pub.getAssetGroupCode()+"_Data_Trade").equalsIgnoreCase("")){
        	  if(!dbl.getTableIndexKey("ID_DATE_DATA_TRADE").equalsIgnoreCase("")){
					dbl.executeSql("drop index ID_DATE_DATA_TRADE");
				}
        	  sql = "create index ID_Date_"+pub.getAssetGroupCode()+"_Data_Trade on "+ pub.yssGetTableName("tb_Data_Trade")+" (FBargainDate)";
        	  dbl.executeSql(sql);
          }          
		}catch (Exception e) {
			throw new YssException("版本1.0.1.0033 创建索引出错！", e);
		}
		
	}
	//创建表
	 public void createTable() throws YssException {

		String sql = "";
		try {
			if (!dbl.yssTableExist(pub.yssGetTableName("TB_DATA_REALISED"))) {
				
				sql = " create table "+pub.yssGetTableName("TB_DATA_REALISED")+
				      " (fcode         VARCHAR2(70) not null,"+
				      " ffcode        VARCHAR2(50),"+
				      " fname         VARCHAR2(50),"+
				      " fcurycode1    VARCHAR2(20),"+
				      " fcurycode2    VARCHAR2(20),"+
				      " frate1        NUMBER(18,7),"+
				      " frate2        NUMBER(18,7),"+
				      " ftype1        VARCHAR2(50),"+
				      " ftype2        VARCHAR2(50),"+
				      " ftype3        VARCHAR2(50),"+
				      " ftype4        VARCHAR2(50),"+
				      " fvalue0       NUMBER(18,4),"+
				      " fvalue1       NUMBER(18,4),"+
				      " fvalue2       NUMBER(18,4),"+
				      " fvalue3       NUMBER(18,4),"+
				      " fvalue4       NUMBER(18,4),"+
				      " fvalue5       NUMBER(18,4),"+
				      " fvalue6       NUMBER(18,4),"+
				      " fportcode     VARCHAR2(20),"+
				      " fdate1        DATE,"+
				      " fdate2        DATE,"+
				      " fdate3        DATE,"+
				      " frealisedtype NUMBER(2))";
				dbl.executeSql(sql);    
			}

			if (!dbl.yssTableExist(pub.yssGetTableName("TB_DATA_UNREALISED"))) {
				
				sql = " create table "+pub.yssGetTableName("TB_DATA_UNREALISED")+
				      " (fcode           VARCHAR2(70) not null,"+
				      " fname           VARCHAR2(50),"+
				      " fcatcode        VARCHAR2(20) not null,"+
				      " fsubcatcode     VARCHAR2(20),"+
				      " fcurycode       VARCHAR2(20) not null,"+
				      " fbal            NUMBER(18,4),"+
				      " fbasecurybal    NUMBER(18,4),"+
				      " fportcode       VARCHAR2(20),"+
				      " fdate           DATE not null,"+
				      " funrealisedtype NUMBER(1))";
				dbl.executeSql(sql);     
			}

			if (!dbl.yssTableExist(pub.yssGetTableName("TB_DATA_UNREALISEDSECREC"))) {
				
				 sql = " create table "+pub.yssGetTableName("TB_DATA_UNREALISEDSECREC")+
				       " ( fcode             VARCHAR2(70) not null,"+
				       " fname             VARCHAR2(50),"+
				       " fcatcode          VARCHAR2(20) not null,"+
				       " fsubcatcode       VARCHAR2(20) not null,"+
				       " fportcode         VARCHAR2(20) not null,"+
				       " fcurycode         VARCHAR2(20) not null,"+
				       " famount           NUMBER(18,4),"+
				       " ftotalcost        NUMBER(18,4),"+
				       " ftotalcostb       NUMBER(18,4),"+
				       " fbal              NUMBER(18,4),"+
				       " fbalb             NUMBER(18,4),"+
				       " fmaketprice       NUMBER(18,4),"+
				       " fmaketpriceb      NUMBER(18,4),"+
				       " fmaketvalue       NUMBER(18,4),"+
				       " fmaketvalueb      NUMBER(18,4),"+
				       " funrealisedmoney  NUMBER(18,4),"+
				       " funrealisedmoneyb NUMBER(18,4),"+
				       " fbondinterest     NUMBER(18,4),"+
				       " fbondinterestb    NUMBER(18,4))";
				dbl.executeSql(sql);     
			}

			if (!dbl.yssTableExist(pub.yssGetTableName("TB_SUMMARYUNTRADE"))) {
				sql = " create table "+pub.yssGetTableName("TB_SUMMARYUNTRADE")+
				      " ( fnum              VARCHAR2(20) not null,"+
				      " fbargaindate      DATE not null,"+
				      " fsettledate       DATE not null,"+
				      " fsecurityname     VARCHAR2(200) not null,"+
				      " fbrokername       VARCHAR2(200) not null,"+
				      " fcatcode          VARCHAR2(20) not null,"+
				      " ftradetypecode    VARCHAR2(20),"+
				      " fcurycode         VARCHAR2(20) not null,"+
				      " famount           NUMBER(18,4),"+
				      " fprice            NUMBER(18,4),"+
				      " fmoney            NUMBER(18,4),"+ 
				      " fbasecurymoney    NUMBER(18,4),"+
				      " fbasecuryrate     NUMBER(18,12),"+
				      " fportcuryrate     NUMBER(18,12),"+
				      " funitcost         NUMBER(18,4),"+
				      " fcost             NUMBER(18,4),"+
				      " fbasecurycost     NUMBER(18,4),"+
				      " faccint           NUMBER(18,4),"+
				      " fbasecuryaccint   NUMBER(18,4),"+
				      " fgainloss         NUMBER(18,4),"+
				      " fbasecurygainloss NUMBER(18,4),"+
				      " fsybasecurybal    NUMBER(18,4),"+
				      " fportcode         VARCHAR2(20))";
				dbl.executeSql(sql);    
			}
			//不规则的命名方式 不能通过更新文件更新表结构 
			if (!dbl.yssTableExist("TB_DATA_FUNDINOUT")) {
				sql = " create table TB_DATA_FUNDINOUT ( "+ 
			          " ftransdate      DATE,"+
				      " fccy            VARCHAR2(20),"+
				      " fdesc           VARCHAR2(200),"+
				      " finflow         NUMBER(18,2),"+ 
			          " finflowhkd      NUMBER(18,2),"+ 
				      " foutflow        NUMBER(18,2),"+
				      " foutflowhkd     NUMBER(18,2),"+
				      " fexrate         NUMBER(18,5),"+
				      " fportaccbalance NUMBER(18,2),"+ 
				      " fportcode       VARCHAR2(20),"+
				      " forder          VARCHAR2(100))";
			    dbl.executeSql(sql);
			}

			//不规则的命名方式 不能通过更新文件更新表结构 
			if (!dbl.yssTableExist("TB_VCH_SUBATTR")) {
				sql = " create table TB_VCH_SUBATTR ( "+ 
				      " fsubattrcode VARCHAR2(20) not null,"+
					  " fsubattrname VARCHAR2(200) not null,"+
					  " fsubtype     VARCHAR2(20) not null,"+
					  " fdesc        VARCHAR2(100),"+ 
				      " fcheckstate  NUMBER(1) not null,"+ 
					  " fcreator     VARCHAR2(20) not null,"+
					  " fcreatetime  VARCHAR2(20) not null,"+
					  " fcheckuser   VARCHAR2(20),"+
					  " fchecktime   VARCHAR2(20),"+ 
					  " constraint PK_TB_VCH_SUBATTR primary key (FSUBATTRCODE))";
				dbl.executeSql(sql);
			}
			
			//不规则的命名方式 不能通过更新文件更新表结构 
			if (!dbl.yssTableExist("TB_DATA_PORTFOLIOVAL")) {
				sql = " create table TB_DATA_PORTFOLIOVAL ( "+ 
				      " fsecuritycode        VARCHAR2(70),"+
					  " fsecurityname        VARCHAR2(250),"+
					  " fstorageamount       NUMBER(22,4),"+
					  " fcurycode            VARCHAR2(50),"+ 
				      " fbasecuryrate        NUMBER(20,15),"+ 
					  " fportcuryrate        NUMBER(20,15),"+
					  " ffactrate            NUMBER(20,15),"+
					  " finsstartdate        DATE,"+
					  " finsenddate          DATE,"+ 
					  " favgcost             NUMBER(22,4),"+
					  " fmarketprice         NUMBER(22,4),"+
					  " fvstoragecost        NUMBER(22,4),"+
					  " fboughtint           NUMBER(22,4),"+
				      " fmvalue              NUMBER(22,4),"+
				      " flxvbal              NUMBER(22,4),"+
				      " fbflxbal             NUMBER(22,4),"+
					  " fykvbal              NUMBER(22,4),"+
					  " fsyvbasecurybal      NUMBER(22,4),"+
				      " ffundallotproportion NUMBER(22,4),"+
				      " ftotalcost           NUMBER(22,4),"+
				      " fothercost           NUMBER(22,4),"+
					  " fvaldate             DATE not null,"+
					  " fportcode            VARCHAR2(20) not null,"+
				      " forder               VARCHAR2(200) not null,"+
				      " constraint PK_TB_DATA_PORTFOLIOVAL primary key (FORDER, FVALDATE, FPORTCODE))";
				dbl.executeSql(sql);
			}
			
		} catch (Exception e) {
			throw new YssException("版本1.0.1.0033 创建表结构出错！", e);
		}
	 }
	 
     /**
      * add by fangjiang 20100903 MS01439 QDV4博时2010年7月14日02_A 
      * 由于增加了债券期货，在Tb_XXX_Para_Indexfutures中增加了字段FSubCatCode
      * 需将原记录的字段FSubCateCode更新为'FU01'
      * @方法名：updateIndexfutures
      * @参数
      * @返回类型 void
      * @说明：更新Tb_XXX_Para_Indexfutures
      */
	private void updateIndexfutures() throws YssException {
		String sql = "";
		try {
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Para_Indexfutures"))) {
				sql = " update " + pub.yssGetTableName("Tb_Para_Indexfutures") + 
					  " set FSubCatCode='FU01' ";
				dbl.executeSql(sql);
			}
		} catch (Exception e) {
			throw new YssException("版本1.0.1.0033 更新表结构出错！", e);
		} 
	}
	 	
	/**
     * add by fangjiang 20100903 MS01439 QDV4博时2010年7月14日02_A 
     * 在资产股指页面中，将估值代码为IndexFutrueMV的估值类别由期货-股指期货浮动盈亏改为期货-期货浮动盈亏
     * @方法名：updateVocabulary
     * @参数
     * @返回类型 void
     * @说明：更新Tb_Fun_Vocabulary
     */
	private void updateVocabulary() throws YssException {
		String sql = "";
		try {
			if (dbl.yssTableExist("Tb_Fun_Vocabulary")) {
				sql = " update Tb_Fun_Vocabulary set fvocname='期货-期货浮动盈亏' where FVocTypeCode = 'val_content' and FVocCode = 'IndexFutruesMV' ";
				dbl.executeSql(sql);
			}
		} catch (Exception e) {
			throw new YssException("版本1.0.1.0033 更新表结构出错！", e);
		} 
	} 
	 	/**
		 * add by wangzuochun  2010.08.26  MS01611    日志浏览，选择任意一条数据点击浏览，会报错。    QDV4赢时胜(测试)2010年08月13日04_B    
		 * 由于parasetting.xml中远期品种信息设置的调用代码写错，导致插入到FRefInvokeCode字段的数据错误，在此进行更新处理；
		 * @方法名：updateFlow
		 * @参数：
		 * @返回类型：void
		 * @说明：更新tb_sys_operlog表
		 */
		private void updateFlow() throws YssException {
			Connection conn = null;
			boolean bTrans = false;
			String altsql = "";
			
			ResultSet rs = null;
			try {
				conn = dbl.loadConnection();
				if (!existsTabColumn_Ora("tb_sys_operlog", "FRefInvokeCode")) {
					altsql = " update tb_sys_operlog set FRefInvokeCode = '000362' " 
							+ " where FModuleCode = 'set' and FFunCode = 'forward'";
					dbl.executeSql(altsql);
				}

				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			} catch (Exception e) {
				throw new YssException("更新流程表出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
				dbl.endTransFinal(conn, bTrans);
			}
		}
		/**
		 * add by yangheng 20100830 MS01660 自定义接口存在重复文件类型 QDV4赢时胜（深圳）2010年8月27日01_B 
		 * 因为词汇设置重复设置了无效的文件后缀名，导致导出数据再导入会出现问题，在此删掉无用的文件后缀名
		 * @方法名：deleteVoc
		 * @参数
		 * @返回类型： void
		 * @说明：更新tb_fun_vocabulary
		 */
		private void deleteVoc() throws YssException{
			Connection conn = null;
			String strSql = "";
			boolean bTrans = false;
			try {
				conn = dbl.loadConnection();
				conn.setAutoCommit(false);
				bTrans = true;
				
				strSql = " delete  from tb_fun_vocabulary "
					   + " where fvoctypecode = 'dao_Inface_FileType' and (fvoccode = '0' or fvoccode = '1' or fvoccode = '2' or fvoccode = '3')";		
				
				dbl.executeSql(strSql);
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			} catch (Exception e) {
				throw new YssException("删除词汇出错", e);
			}finally{
				dbl.endTransFinal(conn,bTrans);
			}
		}
		/**
		 * add by yangheng 20100830 MS01660 自定义接口存在重复文件类型 QDV4赢时胜（深圳）2010年8月27日01_B 
		 * 在删除词汇中的无效后缀名需要更新自定义文件类型中文件类型
		 * @方法名：updateCusConfig
		 * @参数
		 * @返回类型 void
		 * @说明：更新Tb_Dao_CusConfig 
		 */
		private void updateCusConfig()throws YssException{
			Connection conn = null;
			boolean bTrans = false;
			String altsql = "";
			
			ResultSet rs = null;
			try
			{
				conn = dbl.loadConnection();
				//更新文件类型为txt格式但存为0的
				altsql = " update "+pub.yssGetTableName("Tb_Dao_CusConfig")+ " set ffiletype= 'txt' where ffiletype= '0'";
				dbl.executeSql(altsql);
				//更新文件类型为csv格式但存为1的
				altsql = " update "+pub.yssGetTableName("Tb_Dao_CusConfig")+ " set ffiletype= 'csv' where ffiletype= '1'";
				dbl.executeSql(altsql);
				//更新文件类型为xml格式但存为2的
				altsql = " update "+pub.yssGetTableName("Tb_Dao_CusConfig")+ " set ffiletype= 'xml' where ffiletype= '2'";
				dbl.executeSql(altsql);
				//更新文件类型为xls格式但存为3的
				altsql = " update "+pub.yssGetTableName("Tb_Dao_CusConfig")+ " set ffiletype= 'xls' where ffiletype= '3'";
				dbl.executeSql(altsql);
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
			catch (Exception e) {
				throw new YssException("更新自定义接口表出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
				dbl.endTransFinal(conn, bTrans);
			}
		}
		
		/**
		 * add by wangzuochun  2010.09.03  MS01611    MS01592    系统升级后，用户在升级前拥有的权限在升级后会部分丢失。    QDV4赢时胜深圳2010年8月16日01_B 
		 * 由于菜单条级别的调整，相应的权限也应该变更；
		 * @方法名：updateUserRight
		 * @参数：
		 * @返回类型：void
		 * @说明：更新tb_sys_userright表
		 */
		private void updateUserRight() throws YssException {
			Connection conn = null;
			boolean bTrans = false;
			String altsql = "";
			
			ResultSet rs = null;
			try {
				conn = dbl.loadConnection();
				//数据核对,数据核对配置,数据核对组设置,接口核对数据处理,组合设置 原来是组合级别 现在变更为组合群级别
				altsql = " update tb_sys_userright set frighttype = 'group'" +
						 " where frighttype = 'port' and frightcode in ('compare','compareex','compareGroupSet','compexview','portfolio')";
				dbl.executeSql(altsql);
				
				
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			} catch (Exception e) {
				throw new YssException("更用户权限表出错", e);
			} finally {
				dbl.closeResultSetFinal(rs);
				dbl.endTransFinal(conn, bTrans);
			}
		}
	 
}
