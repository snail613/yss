package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.exceptionMappingType;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**shashijie 2012-11-22 BUG 6312 红利转投界面的价格做出了整数位不能大于2位的判断*/
public class Ora1010059 extends BaseDbUpdate {
	//add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
	public static final String lockTable = "0";
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			this.craeteTBLogType();	//20130121 added by liubo.Story #2839.手动创建日志类别表，并修改OPERLOG的表结构
			this.ToCreatePLTable();	//20130110 added by liubo.Story #3213.手动创建每日净值收益数据表
			this.createViewOfAssetGroupList();//20130122 added by liubo.Story #3213
			addSequence(hmInfo);
			//--- add by zhaoxian 20121205 #3208 ---start
			deleteMenubar(); 
			deleteRightType();
			//--- add by zhaoxian 20121205 #3208 ---end 
			this.modifyFields();	//add by huangqirong 2012-12-13 story #3333 更新接口字典的源内容字段的长度
			this.updateMenubarAndAuthority(hmInfo);//ADD BY YESHENGHONG 20121217 STORY2917
		    this.addDZFields(); 	//add by huangqirong 2012-12-14 story #3334 更新电子对账相关表结构
			this.updatePubPara(hmInfo);//add by yeshenghong story2917 20121222
			//---add by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
			synchronized(this.lockTable){
				updateInfo(hmInfo);
			}
			//---add by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
			
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0059更新出错！", ex);
		}
	}

	/**shashijie 2012-11-22 BUG 6312 红利转投界面的价格做出了整数位不能大于2位的判断  */
	private void addSequence(HashMap hmInfo) throws YssException {
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		String strSql = "";
		try {
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
			updTables = (StringBuffer) hmInfo.get("updatetables"); // 用于获取新建的表名

			//分红转头表,转投单价字段
			if (existsTabColumn_Ora("Tb_Data_DividendToInvest", "FPrice")){
				//原本是(30,2)的长度考虑预留大一点,但是维护部和测试部非要定义成(20,8),本人只要改了
				strSql = " alter table "+pub.yssGetTableName("Tb_Data_DividendToInvest")+" modify FPrice number(20,8) ";
				dbl.executeSql(strSql);
				
				sqlInfo.append(strSql);
				updTables.append("Tb_Data_DividendToInvest");
			}
			//分红转头表,确认金额字段
			if (existsTabColumn_Ora("Tb_Data_DividendToInvest", "FCONFIRMMONEY")){
				strSql = " alter table "+pub.yssGetTableName("Tb_Data_DividendToInvest")+" modify FConfirmMoney number(38,2) ";
				dbl.executeSql(strSql);
				
				sqlInfo.append(strSql);
				updTables.append("Tb_Data_DividendToInvest");
			}
			//分红转头表,调整金额字段
			if (existsTabColumn_Ora("Tb_Data_DividendToInvest", "FAdjustMoney")){
				strSql = " alter table "+pub.yssGetTableName("Tb_Data_DividendToInvest")+" modify FAdjustMoney number(38,2) ";
				dbl.executeSql(strSql);
				
				sqlInfo.append(strSql);
				updTables.append("Tb_Data_DividendToInvest");
			}
		} catch (Exception e) {
			throw new YssException("更改分红转头,转投单价字段出错");
		}
		
	}
	/**add by zhaoxian 20121205 删除抵押物信息设置及抵押物补交数据 菜单条 银华卖空  */
	private void deleteMenubar() throws YssException{
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = " delete from tb_fun_menubar where fbarcode in ('collaterat','collateraladd')  ";					
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除菜单条出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	/**add by zhaoxian 20121205 删除抵押物信息设置及抵押物补交数据 权限类型  银华卖空  */
	private void deleteRightType() throws YssException{
		Connection conn = null;
		String strSql = "";
		boolean bTrans = false;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = " delete from Tb_Sys_RightType where frighttypecode in ('FrmCollateral','FrmCollateralAdd')  ";					
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除权限类型出错", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	
	/**
	 * add by huangqirong 2012-12-13 story #3333  更新接口字典的源内容字段的长度
	 * */
	private void modifyFields() throws YssException{
		if(dbl.yssTableExist(pub.yssGetTableName("Tb_Dao_Dict"))){
			ResultSet rs = null;
			try {
				rs = dbl.openResultSet("select * from " + pub.yssGetTableName("Tb_Dao_Dict"));
				if(dbl.isFieldExist(rs, "FSRCCONENT")){
					dbl.executeSql("alter table " + pub.yssGetTableName("Tb_Dao_Dict") + " modify(FSRCCONENT VARCHAR2(100))");
				}
			} catch (Exception e) {				
				System.out.println("更新表结构：" + pub.yssGetTableName("Tb_Dao_Dict") + "出错！");
			}finally{
				dbl.closeResultSetFinal(rs);
			}
		}
	}
	
	/*
	 * add by yeshenghong 20121217 story 2917 更新菜单数据
	 * */
	private void updateMenubarAndAuthority(HashMap hmInfo) throws YssException 
	{
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		String strSql = "";
		try {
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
			updTables = (StringBuffer) hmInfo.get("updatetables"); // 用于获取新建的表名
			if(!dbl.yssTableExist("Tb_fun_navmenubar")){
				strSql = " create table TB_FUN_NAVMENUBAR " +
						 " (FBARCODE       VARCHAR2(20) not null, " +
						 "  FBARNAME       VARCHAR2(50) not null, " +
					     "  FBARGROUPCODE  VARCHAR2(20) not null, " +
						 "  FICONPATH      VARCHAR2(255), " +
						 "  FENABLED       NUMBER(38) not null, " +
						 "  FORDERCODE     VARCHAR2(50) not null, " +
						 "  FREFINVOKECODE VARCHAR2(20), " +
						 "  FOPERTYPECODE  VARCHAR2(400), " +
						 "  FDESC          VARCHAR2(100), " +
						 "  FRIGHTTYPE     VARCHAR2(20) not null, " +
						 "  FTABMAINCODE   VARCHAR2(200), " +
						 "  FTABMAINNAME   VARCHAR2(500))";
				dbl.executeSql(strSql);//create navigator menubar
				sqlInfo.append(strSql);
				strSql = " alter table TB_FUN_NAVMENUBAR add constraint PK_TB_FUN_NAVMENUBAR primary key (FBARCODE, FBARGROUPCODE)";
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				updTables.append("Tb_fun_navmenubar");
				this.updateAuthority(hmInfo);
			}
		} catch (Exception e) {
			throw new YssException("更新菜单条数据出错");
		}
	}
	
	/*
	 * add by yeshenghong 20121217 story 2917 更新权限数据
	 * */
	private void updateAuthority(HashMap hmInfo) throws YssException 
	{
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		String strSql = "";
		boolean bTrans = false;
        Connection conn = dbl.loadConnection();
		try {
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
			updTables = (StringBuffer) hmInfo.get("updatetables"); // 用于获取新建的表名
			//权限数据备份
			strSql = " create table tb_sys_userrightbak9 as select * from tb_sys_userright ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("tb_sys_userrightbak9");
			//菜单条数据备份
			strSql = " create table tb_fun_menubarbak9 as select * from tb_fun_menubar ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("tb_fun_menubarbak9");
			//创建权限数据临时表
			strSql = " create table TMP_SYS_USERRIGHT " +
					 " (FUSERCODE       VARCHAR2(20) not null, " +
					 " FRIGHTTYPE      VARCHAR2(20) not null, " +
					 " FASSETGROUPCODE VARCHAR2(3) not null, " +
					 " FRIGHTCODE      VARCHAR2(50) not null, " +
					 " FPORTCODE       VARCHAR2(20) not null, " +
					 " FRIGHTIND       VARCHAR2(50) not null, " +
					 " FOPERTYPES      VARCHAR2(70)) ";
			
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//添加临时表主键
			strSql = " alter table TMP_SYS_USERRIGHT add constraint PK_TMP_SYS_USERRIGHT " + 
					 " primary key (FUSERCODE, FASSETGROUPCODE, FRIGHTCODE, FPORTCODE, FRIGHTTYPE, FRIGHTIND) ";
			dbl.executeSql(strSql);
			updTables.append("TMP_SYS_USERRIGHT");
			//将原菜单的组合群级更改为公共级别
			strSql = " update tb_fun_menubar set frighttype = 'public' where fbarcode in " +
					" ('recyblebin','operportfolio','MaintenanceTpl','repformat','repparamctlgrp', " +
					" 'repcustom','repdatasource','repgroup','reportview','ICBCrepexport', " +
					" 'reppretreat','stockCheckGY','crossAssetGrpCommRep','accountdetail','repfunction', " +
					" 'whsyb','exratedata','compindexcfg','compliance','Prompting', " +
					" 'compgrade','indextemp','compattr','compindex','comstockpool', " +
					" 'dictSet','pretreat','impinfo','daogroupset','cusconfig', " +
					" 'compare','compareex','compareGroupSet','compexview','swiftset','nfimpinfo', " +
					" 'nfbnyinterface','Interface','BrokerRate','RateSpeciesType','pluginlog', " +
					" 'portsetlink','vchattr','SubjectDict','setvchds','vchtpl', " +
					" 'bookset','vchproject','vchAssistantSetting','facecfg','inoutcfg', " +
					" 'valcompare','pubparaset','operfunextend','pubParaCfgInfo','schproject', " +
					" 'pubpara','ConfigParamOut','ConfigParamIn','commonData','ExpInformation', " +
					" 'RecView','RecGenerator','RecCheck','pluginproduce','portPluginReal') ";
			conn.setAutoCommit(false);
            bTrans = true;
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("tb_fun_menubar");
			//将原菜单的组合群更改为组合级别
			strSql = " update tb_fun_menubar set frighttype = 'port' where fbarcode in " +
					 " ('ratetrade','TA','estimatedata','trialmoney','capitalallocation', " +
				     " 'frmbnyinterface','voucherbuild','vchdata','vchinter','vchbuildproject','vchcheck')";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("tb_fun_menubar");
					 
			//更改远期品种权限类型的barcode
			strSql = " update Tb_Sys_RightType set fmenubarcode = 'forwardmsg' where fmenubarcode = 'forward'";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("Tb_Sys_RightType");
			//更改证券信息维护权限类型的barcode
			strSql = " update  Tb_Sys_RightType set fmenubarcode = 'infor' where fmenubarcode = 'security' ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("Tb_Sys_RightType");
			
			//更改远期品种的barcode
			strSql = " update tb_sys_userright set frightcode = 'forwardmsg' where frightcode = 'forward' ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("tb_sys_userright");
			//更改证券信息维护的barcode
			strSql = " update tb_sys_userright set frightcode = 'infor' where frightcode = 'security' ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("tb_sys_userright");
			
			//更改角色权限远期品种的barcode BUG7243
			strSql = " update tb_sys_roleright set frightcode = 'forwardmsg' where frightcode = 'forward' ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("tb_sys_roleright");
			//更改角色权限证券信息维护的barcode BUG7243
			strSql = " update tb_sys_roleright set frightcode = 'infor' where frightcode = 'security' ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("tb_sys_roleright");
			
			
			//插入从组合群转为公共级别的权限数据到临时表中
			strSql = " insert into TMP_SYS_USERRIGHT  select distinct u1.fusercode,'public',' ' fassetgroupcode, " +
					 " u1.frightcode,' ' fportcode,u1.frightind,u2.fopertypes from tb_sys_userright u1 " +
					 " join (select * from tb_fun_menubar where frighttype = 'public' and fbargroupcode <> '[root]') m  " +
					 " on u1.frightcode = m.fbarcode " +
					 " join ( select fusercode, frightcode, max(fopertypes) as fopertypes from tb_sys_userright group by fusercode, frightcode " +
					 " ) u2 on u1.fusercode = u2.fusercode and u1.frightcode = u2.frightcode " + 
					 " where u1.frighttype = 'group' and u1.frightind = 'Right' ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TMP_SYS_USERRIGHT");
			//插入从组合群转为组合级别的权限数据到临时表中

			strSql = " insert into TMP_SYS_USERRIGHT " + 
					 " select distinct u1.fusercode,'port',u1.fassetgroupcode,u1.frightcode,u2.fportcode,u1.frightind,u1.fopertypes from tb_sys_userright u1 " + 
					 " join (select fusercode,fassetgroupcode,fportcode from  tb_sys_userright where frighttype = 'port') u2  " + 
					 " on u1.fusercode = u2.fusercode and u1.fassetgroupcode = u2.fassetgroupcode join  " + 
					 " (select * from tb_fun_menubar where frighttype = 'port' and fbargroupcode <> '[root]') m  on u1.frightcode = m.fbarcode " + 
					 " where u1.frighttype = 'group' and u1.frightind = 'Right' ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("TMP_SYS_USERRIGHT");
			//删除权限表中与临时权限表中主键重复的记录
			strSql = " delete from tb_sys_userright ur where exists (select 1 from TMP_SYS_USERRIGHT tmp where ur.FUSERCODE = tmp.FUSERCODE " + 
					 " and ur.FASSETGROUPCODE = tmp.FASSETGROUPCODE and ur.FRIGHTCODE = tmp.FRIGHTCODE " +
					 " and ur.FPORTCODE = tmp.FPORTCODE and ur.FRIGHTTYPE = tmp.FRIGHTTYPE and ur.FRIGHTIND = tmp.FRIGHTIND) ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//将临时权限表中的数据插入到权限表中
			strSql = " insert into tb_sys_userright select * from TMP_SYS_USERRIGHT ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			//删除组合群级别的权限数据
			strSql = " delete from tb_sys_userright where frighttype = 'group' and frightind in ('Right','Role') ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			updTables.append("tb_sys_userright");
			//删除菜单数据
			strSql = " delete from tb_fun_menubar ";
			dbl.executeSql(strSql);
			sqlInfo.append(strSql);
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			//删除菜单数据  add  by yeshenghong  上述删除语句存在未删除现象，导致残存历史数据，现在再删除一遍，确保历史数据删除 
//			strSql = " delete from tb_fun_menubar ";
//			dbl.executeSql(strSql);
//			updTables.append("tb_fun_menubar");
		} catch (Exception e) {
			throw new YssException("更新菜单条数据出错");
		} finally
		{
			dbl.endTransFinal(bTrans);
		}
	}
	
	/*
	 * add by yeshenghong 20121217 story 2917 更新通用业务参数
	 * */
	private void updatePubPara(HashMap hmInfo) throws YssException 
	{
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		ResultSet rs = null;
		String strSql = "";
		boolean bTrans = false;
        Connection conn = dbl.loadConnection();
		try {
			sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); // 用于获取执行的sql语句
			updTables = (StringBuffer) hmInfo.get("updatetables"); // 用于获取新建的表名
			strSql = " select * from " + pub.yssGetTableName("TB_PFOper_PUBPARA");
			rs = dbl.openResultSet(strSql);
			if(!dbl.isFieldExist(rs, "FBARGROUPCODE")){
				strSql = " alter table  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " add (FBARGROUPCODE  VARCHAR2(20)) "; 
				dbl.executeSql(strSql);//add bar group code 
				sqlInfo.append(strSql);
				// the codes bellow have the sames function which is to update the public parameters
				// so I haven't added anotations each row, please don't inform the leader secretly.
				conn.setAutoCommit(false);
	            bTrans = true;
				strSql = " update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'bond' where fpubparacode in ('IncomeFIStats','RGuess','costIncludeIntsZQ') ";  
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = " update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'buyback' where fpubparacode in ('ChangOrHand') ";
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = " update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'currenciesfund' where fpubparacode in ('Class_CalcFee','CtlIncomeBal') ";  
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);

				strSql = " update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'dayfinish' where fpubparacode in  " +
				  " ('CtlGuessUnitPara','CtlGuessUnitPara1','DayFinish_ValType','ExactGuessValue','GVColor',  " +
				  " 'Is_Gusspara','Is_Gusspara02','ItemTypeSet','NavDataParams','statNetMart', " +
				  " 'ParaGuessValueCheck','PortOrAsset','ShowTATotalPara','dayPercentChange',  " +
				  " 'finish_GrpShwoGz','finish_IsUseACTVInfo', 'finish_IsUseAmount', 'finish_JudgeGuess', " +  
				  " 'finish_MVIsRound2','finish_ParaFVT','finish_ParaSY','finish_cashliqu','finish_unit','JS_Para')  ";   
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
			    strSql = " update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'deposit' where fpubparacode in ( " +
				  " 'DayFinish_ICDigit','Para_StgCashSet','accPaidFXMode','autoCashpaying','depositProfession')  ";  
			    dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				 
				strSql = "  update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'domesticfund' where fpubparacode in ( " +
					" 'RightsRatioMethods','dontstatbondins','inner_purchaseBIC','inner_purchaseBnT','inner_purchaseEIC',  " +
					" 'inner_purchaseExT','inner_purchasePED','inner_purchaseWFee','intbakBankfee','intbakSettlefee',  " + 
					" 'intbakTradefee','interBankSelCost','interbankbond','interbankfee','interbankinsduty') ";   
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = " update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'forex' where fpubparacode in ( " +
				 " 'ParaOutAcc','ParaToAcc','RateTrade_mode', 'rateTradeBaseMoney')  ";
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = "  update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'forward' where fpubparacode in ('FMIsCalcn','远期外汇小数位设置') "; 
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = "   update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'fund' where fpubparacode in ( " +
				  	" 'ParaRoundStorage','fundmktvalue','mfIncomeNewWay','monetaryfund') ";  
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = " update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'futures' where fpubparacode in ( " +
					" 'AccountType','TA_IsCridet','TA_IsCridet_SP', 'TA_IsCridet_WH','TA_IsCridet_ZQ',   " +
					" 'BondAccountType', 'CommodityAccountType', 'IsFuturesRecPay','NavShowQHCost', 'RateAccountType','FuBbGzSf','PositionType')  ";
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = " update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'gradefund' where fpubparacode in ('PubParaUnitCls','paraClassAccMethod') "; 
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = "   update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'instruction' where fpubparacode in ( " +
						" 'CashType','PrintYnShowTitle','TA_ZDMode','templeteStyle','TaTradeDataSource','cashCommand_OutRate', " +
						" 'CashCommand_fee','commandCash_NewStyle','commandCash_Style','payeeSetter','setCommandHead') ";   
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = " update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'options' where fpubparacode in ( " +   
				 		" 'OptCostAccountSet', 'OptionAccountType', 'bailcarrytype')  "; 
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = " update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'otheropers' where fpubparacode in ('BSettingPlanDate') " ; 
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = "  update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'purchaseinterest' where fpubparacode in (  " +
				 	" 'TAInterestSource','TASellInterest','interestMode','redemptionFeeMoney')  "; 
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				
				strSql = "  update  " +pub.yssGetTableName("TB_PFOper_PUBPARA") + " set fbargroupcode = 'stock' where fpubparacode in ( " +
				 	" 'BRightsRatioMethods','BonusShareSet','SJSZXQYStartSet')  "; 
				dbl.executeSql(strSql);
				sqlInfo.append(strSql);
				conn.commit();
				conn.setAutoCommit(true);
				bTrans = false;
				updTables.append(pub.yssGetTableName("TB_PFOper_PUBPARA"));
			}
		} catch (Exception e) {
			throw new YssException("更新菜通用业务数据出错");
		}finally
		{
			dbl.endTransFinal(bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}
	
		/**
	 * add by huangqirong 2012-12-14 story #3334
	 * 电子对账相关表追加字段
	 * */
	private void addDZFields() throws YssException {
		ResultSet rs = null;
		try {
			dbl.getConnection().setAutoCommit(true);
			
			if(dbl.yssTableExist("TDZBALANCE")){				
				rs = dbl.openResultSet("select * from TDZBALANCE");
				if(!dbl.isFieldExist(rs, "F_J_TOLTAL_AMOUNT")){
					dbl.executeSql("alter table TDZBALANCE add F_J_TOLTAL_AMOUNT number(20,6) default 0 not null");					
				}				
				
				if(!dbl.isFieldExist(rs, "F_D_TOLTAL_AMOUNT")){
					dbl.executeSql("alter table TDZBALANCE add F_D_TOLTAL_AMOUNT number(20,6) default 0 not null");					
				}
				dbl.closeResultSetFinal(rs);//edit by songjie 2013.08.26 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B
			}
			
			if(dbl.yssTableExist("TDZRESULT")){
				rs = dbl.openResultSet("select * from TDZRESULT");
				if(!dbl.isFieldExist(rs, "Bjeth")){
					dbl.executeSql("alter table tdzresult add Bjeth  Decimal(20,6) default 0");
				}
				if(!dbl.isFieldExist(rs, "DjeTh")){
					dbl.executeSql("alter table tdzresult add DjeTh  Decimal(20,6) default 0");
				}
				if(!dbl.isFieldExist(rs, "BjeF")){
					dbl.executeSql("alter table tdzresult add BjeF  Decimal(20,6) default 0");
				}
				if(!dbl.isFieldExist(rs, "DjeF")){
					dbl.executeSql("alter table tdzresult add DjeF  Decimal(20,6) default 0");
				}
				if(!dbl.isFieldExist(rs, "BjeFi")){
					dbl.executeSql("alter table tdzresult add BjeFi  Decimal(20,6) default 0");
				}
				if(!dbl.isFieldExist(rs, "DjeFi")){
					dbl.executeSql("alter table tdzresult add DjeFi  Decimal(20,6) default 0");
				}				
				
				if(!dbl.isFieldExist(rs, "BjeT")){
					dbl.executeSql("alter table tdzresult add BjeT Decimal(18,2) default 0");
				}
				if(!dbl.isFieldExist(rs, "DjeT")){
					dbl.executeSql("alter table tdzresult add DjeT Decimal(18,2) default 0");
				}
				if(!dbl.isFieldExist(rs, "BjeO")){
					dbl.executeSql("alter table tdzresult add BjeO Decimal(18,2) default 0");
				}
				if(!dbl.isFieldExist(rs, "DjeO")){
					dbl.executeSql("alter table tdzresult add DjeO Decimal(18,2) default 0");
				}
				
				if(!dbl.isFieldExist(rs, "Bby1")){
					dbl.executeSql("alter table tdzresult add Bby1 varchar2(100) default ' '");
				}
				if(!dbl.isFieldExist(rs, "Dby1")){
					dbl.executeSql("alter table tdzresult add Dby1 varchar2(100) default ' '");
				}				
			}
			
		} catch (Exception e) {
			e.getStackTrace();
			System.out.println("更新电子对账相关表结构出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
     * add by songjie 2012.12.25 
     * BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
	 * @param hmInfo
	 * @throws YssException
	 */
	private void updateInfo(HashMap hmInfo)throws YssException {
		String strSql = "";
		StringBuffer sqlInfo = null;
		StringBuffer updTables = null;
		StringBuffer buff = null;
		ResultSet rs = null;
		String duration = "";//表类型
		
		sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        updTables = (StringBuffer) hmInfo.get("updatetables"); //用于获取新建的表名
		try{
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("tb_data_PreBonusShare"));
            buff.append(" ( ");
            buff.append(" FTSECURITYCODE  VARCHAR2(50)  NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FSSECURITYCODE  VARCHAR2(50)      NULL, ");
            buff.append(" FEXRIGHTDATE    DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NULL,");
            buff.append(" FPAYDATE        DATE              NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FCostOddRate    NUMBER(7,6)   DEFAULT 0     NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_PreBonusShare"));
            buff.append(" PRIMARY KEY (FTSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE,FPAYDATE) ");
            //-----------------------------------
            buff.append(" ) ON COMMIT PRESERVE ROWS");
			
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_PreBonusShare".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_PreBonusShare"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("tb_data_PreBonusShare")));
        			}
        			
                    updTables.append(pub.yssGetTableName("tb_data_PreBonusShare"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
                    
        		}
        	}else{
                updTables.append(pub.yssGetTableName("tb_data_PreBonusShare"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
			
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
        	buff.delete(0, buff.length());
        	
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("Tb_Data_PreCashConsider"));
            buff.append(" ( ");
            buff.append(" FSecurityCode   VARCHAR(50)   NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FExRightDate    DATE          NOT NULL,");
            buff.append(" FPayDate        DATE          NOT NULL,");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("Tb_Data_PreCashConsider"));
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("Tb_Data_PreCashConsider".toUpperCase()));
        	
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Data_PreCashConsider"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("Tb_Data_PreCashConsider")));
        			}

                    updTables.append(pub.yssGetTableName("Tb_Data_PreCashConsider"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("Tb_Data_PreCashConsider"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
        	buff.delete(0, buff.length());
        	
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("tb_data_PreDeflationBonus"));
            buff.append(" ( ");
            buff.append(" FTSECURITYCODE  VARCHAR2(50)  NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FSSECURITYCODE  VARCHAR2(50)      NULL, ");
            buff.append(" FEXRIGHTDATE    DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NULL,");
            buff.append(" FPAYDATE        DATE              NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_PreDeflation"));
            buff.append(" PRIMARY KEY (FTSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE,FPAYDATE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_PreDeflationBonus".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_PreDeflationBonus"))) { 
        				dbl.executeSql(" drop table " + pub.yssGetTableName("tb_data_PreDeflationBonus"));
        			}

                    updTables.append(pub.yssGetTableName("tb_data_PreDeflationBonus"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("tb_data_PreDeflationBonus"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
        	buff.delete(0, buff.length());
        	
       	 	buff=new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE  ").append(pub.yssGetTableName("tb_data_Predividend"));
            buff.append(" ( ");
            buff.append(" FSecurityCode   VARCHAR(50)   NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FDIVDENDTYPE    NUMBER(2)     NOT NULL, ");
            buff.append(" FCURYCODE       VARCHAR2(20)  DEFAULT ' ' NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FDIVIDENDDATE   DATE          NOT NULL,");
            buff.append(" FDISTRIBUTEDATE DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_Predividend"));
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FDIVDENDTYPE,FCURYCODE,FPORTCODE,FASSETGROUPCODE,FDISTRIBUTEDATE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_Predividend".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_Predividend"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("tb_data_Predividend")));
        			}

                    updTables.append(pub.yssGetTableName("tb_data_Predividend"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("tb_data_Predividend"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
        	buff.delete(0,buff.length());
        	
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("Tb_Data_PreMayApartBond"));
            buff.append(" ( ");
            buff.append(" FSecurityCode   VARCHAR(50)   NOT NULL, ");
            buff.append(" FTSecurityCode  VARCHAR(50)   NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FExRightDate    DATE          NOT NULL,");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FAccountType    NUMBER(2)     NOT NULL,");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("Tb_Data_PreMayApartBond"));
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("Tb_Data_PreMayApartBond".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Data_PreMayApartBond"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("Tb_Data_PreMayApartBond")));
        			}

                    updTables.append(pub.yssGetTableName("Tb_Data_PreMayApartBond"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("Tb_Data_PreMayApartBond"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			buff.delete(0, buff.length());
			
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("tb_data_PreRightsissue"));
            buff.append(" ( ");
            buff.append(" FSECURITYCODE   VARCHAR2(50)  NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FRICURYCODE     VARCHAR2(20)      NULL, ");
            buff.append(" FTSECURITYCODE  VARCHAR2(50)      NULL, ");
            buff.append(" FEXRIGHTDATE    DATE          NOT NULL,");
            buff.append(" FEXPIRATIONDATE DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NOT NULL,");
            buff.append(" FPAYDATE        DATE          NOT NULL,");
            buff.append(" FBEGINSCRIDATE  DATE          NOT NULL,");
            buff.append(" FENDSCRIDATE    DATE          NOT NULL,");
            buff.append(" FBEGINTRADEDATE DATE          NOT NULL,");
            buff.append(" FENDTRADEDATE   DATE          NOT NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FRIPRICE        NUMBER(18,4)  NOT NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append("FTradeCode     VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_PreRightsissue"));
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE,FPAYDATE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_PreRightsissue".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_PreRightsissue"))) { 
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("tb_data_PreRightsissue")));
        			}

                    updTables.append(pub.yssGetTableName("tb_data_PreRightsissue"));	
                    sqlInfo.append(buff.toString());
                    dbl.executeSql(buff.toString());
        		}
        	}else{
                updTables.append(pub.yssGetTableName("tb_data_PreRightsissue"));	
                sqlInfo.append(buff.toString());
                dbl.executeSql(buff.toString());
        	}
        	
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        	
			buff.delete(0, buff.length());
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_Integrated")){
				int maxNum = 0;
				strSql = " select max(FSubNum) as FSubNum from " + pub.yssGetTableName("Tb_Data_Integrated") + 
				" where subStr(FNum,0,1) <> 'E' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FSubNum") != null && YssFun.isNumeric(rs.getString("FSubNum"))){
						maxNum = Integer.parseInt(rs.getString("FSubNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum ++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_Integrated " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
                updTables.append("SEQ_" + pub.getPrefixTB() + "_Data_Integrated");	
                sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_CashPayRec")){
				int maxNum = 0;
				strSql = " select max(FNum) as FNum from " + pub.yssGetTableName("Tb_Data_CashPayRec") + 
				" where SUBSTR(FNum,0,3) <> 'SRP' and SUBSTR(FNum,0,3) <> 'CRP' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_CashPayRec " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
                updTables.append("SEQ_" + pub.getPrefixTB() + "_Data_CashPayRec");	
                sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_INVESTPAYREC")){
				int maxNum = 0;
				strSql = " select max(FNum) as FNum from " + pub.yssGetTableName("Tb_Data_INVESTPAYREC") + 
				" where SUBSTR(FNum,0,3) <> 'IPR' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_INVESTPAYREC " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
                updTables.append("SEQ_" + pub.getPrefixTB() + "_Data_INVESTPAYREC");	
                sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_SecRecPay")){
				int maxNum = 0;
				strSql = " select max(FNum) as FNum from " + pub.yssGetTableName("Tb_Data_SecRecPay") + 
				" where subStr(FNum,0,3) <> 'SRP' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_SecRecPay " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
                updTables.append("SEQ_" + pub.getPrefixTB() + "_Data_SecRecPay");	
                sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Vch_Data")){
				int maxNum = 0;
				strSql = " select max(FVchNum) as FNum from " + pub.yssGetTableName("Tb_Vch_Data") + 
				" where subStr(FVchNum,0,1) <> 'T' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Vch_Data " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
                updTables.append("SEQ_" + pub.getPrefixTB() + "_Vch_Data");	
                sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
		}catch(Exception e){
			throw new YssException("更新表、序列数据出错！",e);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		finally{
			dbl.closeResultSetFinal(rs);
		}
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
	}
	
	//20130110 added by liubo.Story #3213.
	//手动创建每日净值收益数据表（需求中原本是打算做成临时表。但是为了实现存储多组合群数据的功能，需要将接口预处理做成静态数据源，同时手动建表）
	private void ToCreatePLTable() throws YssException
	{
		String strSql = "";
        try
        {
        	if (!dbl.yssTableExist("TMP_PL_NAV"))
        	{
        		strSql = "create table TMP_PL_NAV " +
						 "( " +
						 " FID Numeric(2) not null, " +
						 " FDate Date not null, " +
						 " FFundName VARCHAR(50) not null, " +
						 " FFundCode VARCHAR(20) not null, " +
						 " FNav Numeric(16,4) not null, " +
						 " FLJNav Numeric(16,4) not null, " +
						 " FMWFSY Numeric(16,5) not null, " +
						 " FQRSYL Numeric(16,5) not null, " +
						 " FMWFSY1 Numeric(16,5) not null, " +
						 " FQRSYL1 Numeric(16,5) not null, " +
						 " FHB Numeric(1,0) not null)";
        		dbl.executeSql(strSql);
        	}
        }
        catch(Exception ye)
        {
        	throw new YssException("创建【每日净值收益数据表】出错：" + ye.getMessage());
        }
		
	}
	//20130121 added by liubo.Story #2839.手动创建日志类别表，并修改OPERLOG的表结构，添加FLogData4字段，存储某个窗体的某个空间修改前后的值的差异
	private void craeteTBLogType() throws YssException
	{
		String strSql = "";
		boolean bTrans = false;
        ResultSet rs = null;
		try
		{
	        rs = dbl.queryByPreparedStatement("select * from tb_sys_operlog where 1 = 2");
	        if(!dbl.isFieldExist(rs,"FLOGDATA4"))
	        {
		        strSql = "alter table tb_sys_operlog add FLOGDATA4 CLOB";
		        dbl.executeSql(strSql);
	        }
	        
	        Connection conn = dbl.loadConnection();
	        
	        if (!dbl.yssTableExist("Tb_Fun_LogType"))
	        {

        		strSql = "Create table Tb_Fun_LogType " +
						 "( " +
						 " FFunCode varchar2(50) default ' ' not null, " +
						 " FFunType varchar2(50) default ' ' not null, " +
						 " FReserve_1 varchar(100) default ' ', " +
						 " FReserve_2 varchar(100) default ' ', " +
						 " FReserve_3 varchar(100) default ' ' )";
        		dbl.executeSql(strSql);
	        
		        strSql = "delete Tb_Fun_LogType";
		        dbl.executeSql(strSql);
		        
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('portfolio','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('stockholder','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tradeSeat','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('investmanager','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('currency','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('currencyway','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('sector','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('CMBStaPara','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('investpay','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('operportfolio','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('valuationdays','参数维护类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('groupdata','参数维护类')	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('performula','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('rounding','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('period','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('fee','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tradefeelink','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('cashacclink','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('storagecfg','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('mtvmethod','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('depositduration','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('deopistinterest','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('cgt','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('insuranceSet','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('fixedfeecfg','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('DVPSetUp','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('security','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('fixinterest','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('cashaccount','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('index','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('indexfutures','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('option','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('stockoption','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('forward','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('purchase','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('monetaryfundset','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('collaterat','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('securitylend','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('securityissuser','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('manager','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('keeper','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('trustee','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('assignee','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('warrantor','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('bank','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('brokerSet','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('creditleveldict','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('frmreceiver','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('customsubcategory','参数维护类')  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('attributeclass','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('marketsource','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('exratesource','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('indexsource','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('taselnet','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('portcls','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('taporttype','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('taselltype','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tacashsettle','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tafeelink','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tacashacclink','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('fundDegree','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('etfparamset','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('ETFBrokerInfo','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('shareconvert','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('etfdeliverydate','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('repformat','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('repparamctlgrp','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('repcustom','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('repdatasource','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('repgroup','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('reppretreat','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('dictSet','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('pretreat','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('daogroupset','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('cusconfig','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('swiftset','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('Interface','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('BrokerRate','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('RateSpeciesType','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('region','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('country','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('area','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('exchange','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('category','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('subcategory','参数维护类')	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tradetype','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('holidays','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('analysis','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('accounttype','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('subaccounttype','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('transfertype','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('subtransfer','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('investpaycat','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('calcinsmetic','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('BusinessSet','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('actstfrela','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('Eletransfer','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('reconciliation','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('position','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('department','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('passcomplex','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('AssetGroupSet','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('PerInheritance','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('portsetlink','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('vchattr','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('SubjectDict','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('setvchds','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('vchtpl','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('bookset','参数维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('vchproject','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('vchAssistantSetting','参数维护类')";
		        dbl.executeSql(strSql); 
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('valcompare','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('portPluginReal','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('commandmodule','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('commandmodrela','参数维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('passAmend','账号操作类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('roleSet','账号操作类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('userSet','账号操作类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('rightSet','账号操作类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('OnlineUserManager','账号操作类')  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('rightBrow','账号操作类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('logview','账号操作类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('pluginlog','账号操作类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tradesettle','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tatradesettleview','财务处理类')  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('cashtransfer','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('saving','财务处理类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('frmcommand','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('businessdeal','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('incomecalculate','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('OperDeal','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('valuation','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('storagestat','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('valuationreq','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('incomepaid','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('GuessValue','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('navdata','财务处理类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tataildifadjust','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('compareGZKM','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('SecurityLendRep','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('etfbookrep','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('secoversellbook','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('reportview','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('stockCheckGY','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('balanceCheckZH','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('crossAssetGrpCommRep','财务处理类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('accountdetail','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('indexfuturesbook','财务处理类')   ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('whsyb','财务处理类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('ExRateEstimateRep','财务处理类')  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('moneycontroltab','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('cnymoneycontroltab','财务处理类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('voucherbuild','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('vchdata','财务处理类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('vchinter','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('vchbuildproject','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('vchcheck','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('exschproject','财务处理类')	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('valcompareresult','财务处理类')   ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('commonData','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('ExpInformation','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('RecView','财务处理类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('RecGenerator','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('RecCheck','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('financesys','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('pluginproduce','财务处理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('OperationData','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('secrecpay','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('securitycodechange','业务数据类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('cashsecpay','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('investpayrec','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('FrmRateTrade','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('frmSecByExchange','业务数据类')   ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('operdataentry','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('MaintenanceTpl','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('dataindex','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('fwmktvalue','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('marketvalue','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('exchangerate','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('bondinterest','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('cpivalue','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('datacreditlevel','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('monetaryfundins','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('newshareprice','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('BonusShare','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('DeflationBonus','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('rightsissue','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('dividend','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('cashconsider','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('apartbond','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('dividinvest','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('futuretrade','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('frmFillBialSet','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('futurebailchange','业务数据类')   ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('futureshedgingset','业务数据类')  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('forwardtrade','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('optionstradeset','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('optionbailchange','业务数据类')   ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('bailmoneyaccset','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('purchasetrade','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('interbankbondtrade','业务数据类') ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('newissue','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('devolvetrustee','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('openFund','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tradesecuritylend','业务数据类')  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('SecurityStorage','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('CashStorage','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('investStorage','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('frmta','业务数据类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tatrade','业务数据类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('fundright','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('etfratebatch','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('adjustMoney','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('ratetrade','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('exratedata','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('TA','业务数据类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('tradedata','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('estimatedata','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('trialmoney','业务数据类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('capitalallocation','业务数据类')  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('balance','业务数据类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('ICBCrepexport','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('reportsuperview','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('impinfo','数据交互类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('interfacedeal','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('exphxnavtofa','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('frmbnyinterface','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('daoDataOutOnTime','数据交互类')   ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('swiftOutput','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('swiftInput','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('datacenter','数据交互类')	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('nfimpinfo','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('nfbnyinterface','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('ConfigParamOut','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('ConfigParamIn','数据交互类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('compresult','监控管理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('compindexcfg','监控管理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('compportindexlink','监控管理类')  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('Prompting','监控管理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('compgrade','监控管理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('indextemp','监控管理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('compattr','监控管理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('compindex','监控管理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('comstockpool','监控管理类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('refInvoke','系统维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('menubarSet','系统维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('datadict','系统维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('vocabularyType','系统维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('Spring','系统维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('Version','系统维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('FlowSet','系统维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('droptemptable','系统维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('facecfg','系统维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('schproject','系统维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('pubpara','系统维护类') 		  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('inoutcfg','系统维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('pubparaset','系统维护类') 	  ";
		        dbl.executeSql(strSql);
		        strSql = "insert into Tb_Fun_LogType(FFunCode,FFunType) valueS('pubParaCfgInfo','系统维护类') 	  ";
		        dbl.executeSql(strSql);
	        }
	
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		}
		catch(Exception ye)
		{
			throw new YssException("初始化日志类别表出错：" + ye.getMessage());
		}
		finally
		{
			dbl.endTransFinal(bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}

	//20130122 added by liubo.Story #3213
	//创建组合群组合明细列表视图，当浏览“每日净值收益数据（证券时报）”时，报表的临时表数据会关联这个视图
	//临时表中的存储的跨组合群的数据就可以连接查询出符合条件的组合群中某个组合的管理人、主托管人、组合启用日期
    //=================================
	private void createViewOfAssetGroupList() throws YssException
	{
		String strSql = "";
		String sqlView = "";
		ResultSet rs = null;
		
		try
		{
			strSql = "select * from tb_sys_assetgroup order by FAssetGroupCode";
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next())
			{
				/**add---shashijie 2013-2-4 BUG 7030组合群复制报错的问题*/
				//循环组合群中若有以下任意一张表不存在则不拼接SQL,否则会报错
				if (!dbl.yssTableExist("Tb_" + rs.getString("FAssetGroupCode") + "_Para_AffiliatedCorp")//关联公司信息
						|| !dbl.yssTableExist("Tb_" + rs.getString("FAssetGroupCode") + "_para_portfolio")//组合设置
						|| !dbl.yssTableExist("Tb_" + rs.getString("FAssetGroupCode") + "_Para_Portfolio_Relaship")//组合设置关联
						){
					continue;
				}
				/**end---shashijie 2013-2-4 BUG 7030 */
				if(sqlView.trim().length() == 0)
				{
					sqlView = " select " + dbl.sqlString(rs.getString("FAssetGroupCode")) + " as FAssetGroupCode,a.Fportcode,a.fportname,a.fassetcode,a.finceptiondate, " +
							  " Nvl(b.faffcorpcode,' ') as managerCode,Nvl(b.FAffCorpName,' ') as managerName, " +
							  " Nvl(c.faffcorpcode, ' ') as trusteeCode,Nvl(c.FAffCorpName,' ') as trusteeName " +
							  " from tb_" + rs.getString("FAssetGroupCode") + "_para_portfolio a " +
							  " left join  " +
							  "( " +
							  " select a.FportCode,b.faffcorpcode,b.FAffCorpName from Tb_" + rs.getString("FAssetGroupCode") + "_Para_Portfolio_RelaShip a  " +
							  " left join Tb_" + rs.getString("FAssetGroupCode") + "_Para_AffiliatedCorp b " +
							  " on a.FSubCode = b.FAffCorpCode and a.FRelaType = 'Manager' " +
							  " where a.FRelaType = 'Manager' " +
							  " ) b on a.Fportcode = b.FPortCode " +
							  " left join  " +
							  "( " +
							  " select a.FportCode,c.faffcorpcode,c.FAffCorpName from Tb_" + rs.getString("FAssetGroupCode") + "_Para_Portfolio_RelaShip a  " +
							  " left join Tb_" + rs.getString("FAssetGroupCode") + "_Para_AffiliatedCorp c " +
							  " on a.FSubCode = c.FAffCorpCode and a.FRelaType = 'Trustee' and a.FRelaGrade = 'primary' " +
							  " where  (a.FRelaType = 'Trustee' and FRelaGrade = 'primary') " +
							  " ) c on a.Fportcode = c.FPortCode ";
				}
				else
				{
					  sqlView += " union all " +
					  			" select " + dbl.sqlString(rs.getString("FAssetGroupCode")) + " as FAssetGroupCode,a.Fportcode,a.fportname,a.fassetcode,a.finceptiondate, " +
								" Nvl(b.faffcorpcode,' ') as managerCode,Nvl(b.FAffCorpName,' ') as managerName, " +
								" Nvl(c.faffcorpcode, ' ') as trusteeCode,Nvl(c.FAffCorpName,' ') as trusteeName " +
								" from tb_" + rs.getString("FAssetGroupCode") + "_para_portfolio a " +
								" left join  " +
								"( " +
								" select a.FportCode,b.faffcorpcode,b.FAffCorpName from Tb_" + rs.getString("FAssetGroupCode") + "_Para_Portfolio_RelaShip a  " +
								" left join Tb_" + rs.getString("FAssetGroupCode") + "_Para_AffiliatedCorp b " +
								" on a.FSubCode = b.FAffCorpCode and a.FRelaType = 'Manager' " +
								" where a.FRelaType = 'Manager' " +
								" ) b on a.Fportcode = b.FPortCode " +
								" left join  " +
								"( " +
								" select a.FportCode,c.faffcorpcode,c.FAffCorpName from Tb_" + rs.getString("FAssetGroupCode") + "_Para_Portfolio_RelaShip a  " +
								" left join Tb_" + rs.getString("FAssetGroupCode") + "_Para_AffiliatedCorp c " +
								" on a.FSubCode = c.FAffCorpCode and a.FRelaType = 'Trustee' and a.FRelaGrade = 'primary' " +
								" where  (a.FRelaType = 'Trustee' and FRelaGrade = 'primary') " +
								" ) c on a.Fportcode = c.FPortCode ";
				}
			}
			if (sqlView.trim().length() > 0)
			{
				sqlView = "create or replace view VQ_AssetGroupList as " + sqlView;
				dbl.executeSql(sqlView);
			}
		}
		catch(Exception ye)
		{
			throw new YssException("生成组合群列表视图出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
	}
}
