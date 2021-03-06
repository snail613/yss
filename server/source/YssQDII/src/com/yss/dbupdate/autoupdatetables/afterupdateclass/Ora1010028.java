package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.dsub.DbBase;
import com.yss.util.YssException;

/**
 * MS00839  QDV4华夏2009年11月26日01_A  
 * 
 * @author yanghaiming
 * 
 */
public class Ora1010028 extends BaseDbUpdate {
	
	public Ora1010028(){
		
	}
	public void doUpdate(HashMap hmInfo) throws YssException {
		try {
			updateTableStructure();
		} catch (Exception ex) {
			throw new YssException("版本 1.0.1.0028 更新出错！", ex);
		}
	}
	private void updateTableStructure() throws YssException{
		Connection conn = dbl.loadConnection();
		boolean bTrans = true;
		StringBuffer bufSql = new StringBuffer();
		/**shashijie 2012-7-2 STORY 2475 */
		//DbBase dbBase = new DbBase();
		/**end*/
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		ResultSet rs = null;
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		try {
			conn.setAutoCommit(false);
			//add by yanghaiming 20100414 MS00839 
			//关联机构设置
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_AffiliatedCorp")).append(" modify FAffCorpCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//席位设置
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_TradeSeat")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//收款人设置
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Receiver")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Receiver")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Receiver")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Receiver")).append(" modify FReceiverCode VARCHAR2(100)");//add 20100415
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//券商设置
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Broker")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//券商纽约银行信息设置
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_BrokerSubBny")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//资金调拨子表
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Cash_SubTransfer")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Cash_SubTransfer")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Cash_SubTransfer")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//交易数据主表
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_Trade")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//交易数据(子表)
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_SubTrade")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//已拆分交易数据(子表)
			bufSql.append("select * from user_tables where TABLE_NAME = UPPER('").append(pub.yssGetTableName("Tb_Data_SplitSubTrade")).append("')");
			//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			rs = dbl.openResultSet(bufSql.toString());
			//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			bufSql.delete(0, bufSql.length());
			if(rs.next()){
				bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_SplitSubTrade")).append(" modify FBrokerCode VARCHAR2(100)");
				dbl.executeSql(bufSql.toString());
				bufSql.delete(0, bufSql.length());
			}
			
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeResultSetFinal(rs);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			
			//交易关联表
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_TradeRela")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_TradeRela")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_TradeRela")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//交易关联子表
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_TradeRelaSub")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_TradeRelaSub")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_TradeRelaSub")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//外汇交易数据表
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_RateTrade")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_RateTrade")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_RateTrade")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_RateTrade")).append(" modify FBAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_RateTrade")).append(" modify FBAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_RateTrade")).append(" modify FBAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_RateTrade")).append(" modify FReceiverCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_RateTrade")).append(" modify FPayCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//远期外汇交易数据表
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_ForwardTrade")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_ForwardTrade")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_ForwardTrade")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//远期外汇交易帐户表
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_ForwardTradeAcc")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_ForwardTradeAcc")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_ForwardTradeAcc")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//证券应收应付款
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_SecRecPay")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_SecRecPay")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_SecRecPay")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//现金应收应付款
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_CashPayRec")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_CashPayRec")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_CashPayRec")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//运营应收应付收支款
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_InvestPayRec")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_InvestPayRec")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_InvestPayRec")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//库中无此字段bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Data_InvestPayRec")).append(" modify FBrokerCode VARCHAR2(100)");
			//期货交易数据表
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_Data_FuturesTrade")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//期权交易数据表
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_Data_OptionsTrade")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//期权交易关联表
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_Data_optionstraderela")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//股指期货初始保证金金额调整设置
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_DATA_FutureBailChange")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//期货保证金补交表
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_Data_FuturesFillBail")).append(" modify FCashAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_Data_FuturesFillBail")).append(" modify FCashAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_Data_FuturesFillBail")).append(" modify FCashAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_Data_FuturesFillBail")).append(" modify FBailAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_Data_FuturesFillBail")).append(" modify FBailAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_Data_FuturesFillBail")).append(" modify FBailAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//订单制作
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Order_Maintenance")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//订单确认
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Order_Confirm")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//证券库存
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_Security")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_Security")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_Security")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//现金库存
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_Cash")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_Cash")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_Cash")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//TA库存
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_TA")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_TA")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_TA")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//运营收支库存
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_Invest")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_Invest")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_Invest")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//证券应收应付款库存
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_SecRecPay")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_SecRecPay")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_SecRecPay")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//存款利息应收款库存
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_CashPayRec")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_CashPayRec")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_CashPayRec")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//运营应收应付收支库存
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_InvestPayRec")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_InvestPayRec")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Stock_InvestPayRec")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//费用承担方向
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_DAO_FeeWay")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//券商佣金利率设置 
			bufSql.append("SELECT UC.TABLE_NAME, UCC.COLUMN_NAME FROM USER_CONSTRAINTS UC, USER_CONS_COLUMNS UCC WHERE UC.CONSTRAINT_NAME = UCC.CONSTRAINT_NAME")
				.append(" AND UC.TABLE_NAME = UPPER('").append(pub.yssGetTableName("TB_DAO_BrokerRate")).append("') AND CONSTRAINT_TYPE = 'P'");
			rs = dbl.openResultSet(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			if(rs.next()){
				bufSql.append("alter table ").append(pub.yssGetTableName("TB_DAO_BrokerRate")).append(" drop constraint PK_").append(pub.yssGetTableName("TB_DAO_BrokerRate")).append(" cascade");//先删除主键
				dbl.executeSql(bufSql.toString());
				bufSql.delete(0, bufSql.length());
			}
			
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeResultSetFinal(rs);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_DAO_BrokerRate")).append(" modify FBrokerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("TB_DAO_BrokerRate")).append(" add constraint PK_").append(pub.yssGetTableName("TB_DAO_BrokerRate")).append(" primary key (FASSETGROUPCODE, FPORTCODE, FBROKERCODE, FSEATSITE, FSEATCODE, FSPECIESTYPE, FSTARTDATE)");//增加主键
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//TA分盘设置
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_TA_Assign")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_TA_Assign")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_TA_Assign")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//TA交易数据表
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_TA_Trade")).append(" modify FAnalysisCode1 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_TA_Trade")).append(" modify FAnalysisCode2 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_TA_Trade")).append(" modify FAnalysisCode3 VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			//add by yanghaiming 20100415 MS00839  QDV4华夏2009年11月26日01_A 
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Linkman")).append(" modify FRelaCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Manager")).append(" modify FManagerCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Keeper")).append(" modify FKeeperCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Trustee")).append(" modify FTrusteeCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Assignee")).append(" modify FAssigneeCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Warrantor")).append(" modify FWarrantorCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")).append(" modify FSubCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			bufSql.delete(0, bufSql.length());
			bufSql.append("alter table ").append(pub.yssGetTableName("Tb_Para_Bank")).append(" modify FBankCode VARCHAR2(100)");
			dbl.executeSql(bufSql.toString());
			
			conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

			
		}catch (Exception e) {
			throw new YssException("更新表结构出错！", e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeResultSetFinal(rs);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		}
		
	}
	
	
}
