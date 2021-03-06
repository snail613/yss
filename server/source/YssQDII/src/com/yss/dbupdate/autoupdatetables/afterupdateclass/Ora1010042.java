package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.util.HashMap;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.YssException;

/**
 * add by songjie 2011.06.25 
 * BUG 2116 光大证券2011年6月20日04_B
 * 给用户赋权限时，不显示整行都是灰色的菜单
 * @author 宋洁
 *
 */
public class Ora1010042 extends BaseDbUpdate {
	public Ora1010042(){
		
	}
	
	/**
     * add by songjie 2011.06.25 
     * BUG 2116 光大证券2011年6月20日04_B
     * 给用户赋权限时，不显示整行都是灰色的菜单
	 */
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateTable(hmInfo); //删除菜单条中操作类新改代码为空的数据
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0042 更新出错！", ex);
		}
	}
	
	/**
     * add by songjie 2011.06.25 
     * BUG 2116 光大证券2011年6月20日04_B
     * 给用户赋权限时，不显示整行都是灰色的菜单
	 */
	public void updateTable(HashMap hmInfo) throws YssException{
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
			
			if(dbl.yssTableExist("TB_FUN_MENUBAR_bak")){
				strSql = "DROP TABLE TB_FUN_MENUBAR_bak";
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
			}
			
			strSql = "create table TB_FUN_MENUBAR_bak " +
					" as (select * from TB_FUN_MENUBAR)";
			sqlInfo.append(strSql);
			dbl.executeSql(strSql);
			
			updTables.append("TB_FUN_MENUBAR_bak");
			
			if(dbl.yssTableExist("TB_FUN_MENUBAR")){
				
				strSql = "delete from tb_fun_menubar where fbarcode not in("
						+"'estimatedata','ratetrade','trialmoney','capitalallocation',"
						+"'balance','etfratebatch','etfparamset','shareconvert',"
						+"'etfbookrep','etfdeliverydate','ETFOperation','OnlineUserManager',"
						+"'accountdetail','Eletransfer','crossAssetGrpCommRep','reconciliation',"
						+"'RecGenerator','RecView','RecCheck','ICBCrepexport','adjustMoney',"
						+"'positionforecast','OffAcct','indexfuturesbook','collaterat',"
						+"'securitylend','securityLend','tradesecuritylend','collateraladd',"
						+"'SecurityLendRep','compexview','tabledict','forwardtrade',"
						+"'chinabank','bankfee','bankdeposits','remitinout','netshare',"
						+"'foreignexchange','operportfolio','unrealisedAdjust','fundright',"
						+"'businessdeal','ExpInformation','passcomplex','nffixed',"
						+"'nfbnyinterface','nfimpinfo','futustadata','tataildifadjust',"
						+"'groupdata','option','optionstradeset','bailmoneyaccset',"
						+"'apartbond','cashconsider','compareGZKM','rightBrow',"
						+"'curbsecbusiness','purchasetrade','RateSpeciesType','BrokerRate',"
						+"'Interface','cnstock','bondrateset','interbankbondtrade',"
						+"'newissue','devolvetrustee','openFund','newshareprice',"
						+"'valuationdays','swiftOutput','stockoption','optionbailchange',"
						+"'futureshedgingset','stockCheckGY','balanceCheckZH','reppretreat',"
						+"'monetaryfundset','monetaryfundins','stockCheckResult','securitycodechange',"
						+"'datacenter','batchtransferdata','bondparameter','coststorage',"
						+"'navBeginPeriod','purbond','repexport','moneycontrol',"
						+"'cnymoneycontroltab','exratedata','moneycontroltab','TA',"
						+"'tradedata','exchangerate','marketsource','OperDeal','trade',"
						+"'bondinterest','AssetGroupSet','tradesettle','cashbook',"
						+"'securitybook','dayfinish','storagemanage','incomecalculate',"
						+"'mtvmethod','valuationreq','compgrade','compattr','comprep',"
						+"'repcustom','repgroup','bookset','vchattr','setvchds',"
						+"'opfun','creditleveldict','datacreditlevel','incomepaid',"
						+"'dictSet','impinfo','GuessValue','interfacedeal','taselnet',"
						+"'taselltype','tacashacclink','FrmRateTrade','Spring','forward',"
						+"'tatradesettleview','frmreceiver','purchase','frmta',"
						+"'droptemptable','currencyway','Version','CommonParams',"
						+"'navdata','securitybooknew','vchproject','compresult',"
						+"'compindexcfg','FlowSet','ConfigParamOut','pubparaset',"
						+"'exschproject','pubpara','inoutcfg','pubParaCfgInfo',"
						+"'trustee','currency','storagestat','orginfo','comstockpool',"
						+"'BusinessSet','limitedSecurity','summaryAdjust','swift',"
						+"'swiftset','swiftInput','futurebailchange','compareGroupSet',"
						+"'compareex','compare','investStorage','clset','pubinfo',"
						+"'rightinfo','voucher','vchtpl','portsetlink','SubjectDict',"
						+"'voucherbuild','vchdata','investpaycat','investpayrec',"
						+"'investpay','compindex','vchinter','daogroupset','portcls',"
						+"'pretreat','cusconfig','taoperation','taporttype',"
						+"'tacashsettle','tafeelink','tatrade','exphxnavtofa',"
						+"'indexfutures','frmcommand','calcinsmetic','frmbnyinterface',"
						+"'stockholder','fwmktvalue','CommonParamsAttr','frmSecByExchange',"
						+"'cashbooknew','investbook','sysface','facecfg','vchbuildproject',"
						+"'compportindexlink','valcompareresult','operplatform','schproject',"
						+"'ConfigParamIn','vchcheck','operfunextend','valcompare','future',"
						+"'futuretrade','frmFillBialSet','commonData','daoDataOutOnTime',"
						+"'roleSet','rightSet','department','brokerSet','assignee','tradeSeat',"
						+"'sector','SecurityStorage','customsubcategory','subcategory','accounttype',"
						+"'cashaccount','securityissuser','period','tradefeelink','ordermaintenance',"
						+"'region','repdatasource','reportview','dataindex','indexsource','index',"
						+"'depositduration','saving','deopistinterest','RightType','OperationType',"
						+"'OperationData','transfertype','storagecfg','cashmanage','cashtransfer',"
						+"'security','refInvoke','base','country','area','exchange','set',"
						+"'manager','portfolio','interface','sysfun','system','passAmend',"
						+"'userSet','menubarSet','position','investmanager','keeper','language',"
						+"'warrantor','operdata','holidays','analysis','category',"
						+"'subaccounttype','CashStorage','bank','fee','attributeclass',"
						+"'orderadmin','fixinterest','performula','confirm','exratesource',"
						+"'BonusShare','rightsissue','marketvalue','dividend','subtransfer',"
						+"'cashacclink','settlecenter','accbook','datadict','vocabularyType',"
						+"'secrecpay','cashsecpay','valuation','report','compliance',"
						+"'indextemp','compmanager','repformat','repparamctlgrp','logview',"
						+"'rounding','tradetype','catinfo','vchAssistantSetting','cgt',"
						+"'repfunction','dayfinishlog','cpivalue')"
						+" and (FOperTypeCode is null or FOperTypeCode = 'null')";
				
				sqlInfo.append(strSql);
				dbl.executeSql(strSql);
				
				updTables.append("TB_FUN_MENUBAR");
			}

            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("1.0.1.0042 更新表数据出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
