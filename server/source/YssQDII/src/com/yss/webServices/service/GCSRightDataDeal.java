package com.yss.webServices.service;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Element;

import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.YssD;
import com.yss.util.YssFun;
import com.yss.vsub.YssDbOperSql;

/**
 * 权益数据处理,处理webService接受到的GCS数据
 * 
 * @author huangqirong 2012.12.17 Stroy #2327
 * 
 */
public class GCSRightDataDeal  extends SwiftMsgDeal {
	
	
	/**
	 * 处理权益接口数据
	 * */
	public void dealRightData(List<Element> bizData ) {

		PreparedStatement pst = null;
		String sql = "";
		Element tradeData = null;
		
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(this.getYssPub());
		YssDbOperSql operSql = new YssDbOperSql(this.getYssPub());
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String sDate = formatter.format(date);
		try {			
			if (bizData.size() > 0) {				
				this.getConnection().setAutoCommit(true);				
				GCSTradeDataDeal tradeDeal = new GCSTradeDataDeal();
				tradeDeal.setConnection(this.getConnection());
				tradeDeal.setYssPub(this.getYssPub());
				
				for (int i = 0; i < bizData.size(); i++) {
					tradeData = bizData.get(i);					
					String gcsAssetID = tradeData.elementText("FsetCode"); //资产代码
					String gcsBusiness = tradeData.elementText("FQyLx");//业务类型
					String gcsTradeDate = tradeData.elementText("FQyCqr");//除权日即交易日期
					
					//标签值为空 则不读入
					if(gcsAssetID == null || gcsAssetID.trim().length() == 0)
						continue;
					if(gcsBusiness == null || gcsBusiness.trim().length() == 0)
						continue;
					if(gcsTradeDate == null || gcsTradeDate.trim().length() == 0)
						continue;
					
					String [] groupAndPort = this.getPortCodeBySetCode(gcsAssetID);
					
					if(groupAndPort[1].trim().length() > 0){
							/*06分发派息
							 *06分发派息
							 *17债券兑付
							 *07送股
							 */
							if("GPPX".equalsIgnoreCase(gcsBusiness) || "ZQDX".equalsIgnoreCase(gcsBusiness) 
									|| "ZQDF".equalsIgnoreCase(gcsBusiness) || "SG".equalsIgnoreCase(gcsBusiness) 
									|| "PG".equalsIgnoreCase(gcsBusiness)){

								if(gcsBusiness == null || gcsBusiness.trim().length() == 0)
									continue;

								/**
								 * 填塞组合群前缀
								 * */

								this.getYssPub().setAssetGroupCode(groupAndPort[0]);
								this.getYssPub().setPrefixTB(groupAndPort[0]);
								this.getYssPub().setPortBaseCury();

								/**
								 * 交易所代码判断  Dict_Market_GCS
								 * */
								String exchangeCode = tradeData.elementText("FSzSh"); //交易所代码
								if(exchangeCode == null || exchangeCode.trim().length() == 0)
									continue;

								Hashtable<String, String> exchangeCodes = this.getMarket(groupAndPort[0], "Dict_Market_GCS");
								String qdExchangeCode = exchangeCodes.get(exchangeCode);
								qdExchangeCode = (qdExchangeCode == null || qdExchangeCode.trim().length() == 0) ? exchangeCode : qdExchangeCode ;									 

								int markCount = this.getCountbySql("select count(*) as FMarkCount from tb_" + groupAndPort[0] + "_Dao_Dict tdd " +
												" where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fsrcconent = " + this.getYssPub().getDbLink().sqlString(exchangeCode), "FMarkCount");

								if(markCount != 1){
									this.setReplyCode("1");
									if(this.getReplyRemark() == null || this.getReplyRemark().trim().length() == 0)
										this.setReplyRemark("导入失败:QDII系统(资产代码" +gcsAssetID+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode) ;
									else
										this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码" +gcsAssetID+ ")接口字典Dict_Market_GCS中无法识别市场标志" + exchangeCode) ;
									continue;
								}
								
								markCount = this.getCountbySql("select count(*) as FMarkCount from tb_" + groupAndPort[0] + "_Dao_Dict tdd " +
												" where tdd.fdictcode = 'Dict_Market_GCS' and tdd.fcnvconent = " +
												" (select FCnvconent from tb_" + groupAndPort[0] + "_Dao_Dict where fdictcode = 'Dict_Market_GCS'" +
												" and fsrcconent = " + this.getYssPub().getDbLink().sqlString(exchangeCode) +")", "FMarkCount");
								
								if(markCount != 1){
									this.setReplyCode("1");
									if(this.getReplyRemark() == null ||this.getReplyRemark().trim().length() == 0)
										this.setReplyRemark("导入失败:QDII系统(资产代码" +gcsAssetID+ ")接口字典Dict_Market_GCS中无法识别市场标志"+exchangeCode) ;
									else
										this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码" +gcsAssetID+ ")接口字典Dict_Market_GCS中无法识别市场标志"+exchangeCode) ;
									continue;
								}

								/**
								 * 成交证券  代码查询
								 * */
								String  markCode= tradeData.elementText("FZqDm"); //上市代码
								if( markCode == null || markCode.trim().length() == 0 )
									continue;
								
								String securityCode = "";
								
								int securityCount = this.getCountbySql("select count(*) as FSecurityCount from tb_" + groupAndPort[0] + "_para_security " +
													" where FCheckState=1 and FMarketCode=" + this.getYssPub().getDbLink().sqlString(markCode)+ 
													" and FExchangeCode = " + this.getYssPub().getDbLink().sqlString(qdExchangeCode), "FSecurityCount");
								
								if(securityCount != 1){									
									this.setReplyCode("1");
									if(this.getReplyRemark()== null ||this.getReplyRemark().trim().length() == 0)
										this.setReplyRemark("导入失败:QDII系统(资产代码" +gcsAssetID+ ")证券信息设置表中无法识别上市代码为" + markCode + "交易所为" + exchangeCode + "的证券。") ;
									else
										this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码" +gcsAssetID+ ")证券信息设置表中无法识别上市代码为" + markCode + "交易所为" + exchangeCode + "的证券。") ;
									continue;
								}
								
								securityCode = this.getDatabySql("select * from tb_" + groupAndPort[0] + "_para_security " +
														" where FCheckState = 1 and FMarketCode = " + this.getYssPub().getDbLink().sqlString(markCode)+
														" and FExchangeCode = " + this.getYssPub().getDbLink().sqlString(qdExchangeCode), "FSECURITYCODE");

								if("SG".equalsIgnoreCase(gcsBusiness)){
									
									securityCount = this.getCountbySql( " select count(*) as FCT from Tb_" + groupAndPort[0] + "_Data_BonusShare tdbs " +
																		" where tdbs.FSSecurityCode = " + this.getYssPub().getDbLink().sqlString(securityCode) + 
																		" and tdbs.fexrightdate =  " + this.getYssPub().getDbLink().sqlDate(this.toDate(gcsTradeDate)) +
																		" and tdbs.fcheckstate = 1 " , "FCT");
									
									if(securityCount != 1){
										this.setReplyCode("1");
										if(this.getReplyRemark()== null ||this.getReplyRemark().trim().length() == 0)
											this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID + ")送股权益设置表中无法识别证券代码为" + securityCode + "的送股权益设置信息。") ;
										else
											this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"  + gcsAssetID + ")送股权益设置表中无法识别证券代码为" + securityCode + "的送股权益设置信息。") ;
										continue;
									}
									
									securityCode = this.getDatabySql(" select tdbs.FTSecurityCode as FSECURITYCODE from Tb_" + groupAndPort[0] + "_Data_BonusShare tdbs " +
											" where tdbs.FSSecurityCode = " + this.getYssPub().getDbLink().sqlString(securityCode) + 
											" and tdbs.fexrightdate =  " + this.getYssPub().getDbLink().sqlDate(this.toDate(gcsTradeDate)) +
											" and tdbs.fcheckstate = 1 " , "FSECURITYCODE");
									
								}else if("PG".equalsIgnoreCase(gcsBusiness)){
									securityCount = this.getCountbySql( " select count(*) as FCT from Tb_" + groupAndPort[0] + "_Data_RightsIssue tdbs " +
											" where tdbs.FSecurityCode = " + this.getYssPub().getDbLink().sqlString(securityCode) + 
											" and tdbs.fexrightdate =  " + this.getYssPub().getDbLink().sqlDate(this.toDate(gcsTradeDate)) +
											" and tdbs.fcheckstate = 1 " , "FCT");
		
									if(securityCount != 1){
										this.setReplyCode("1");
										if(this.getReplyRemark()== null ||this.getReplyRemark().trim().length() == 0)
											this.setReplyRemark("导入失败:QDII系统(资产代码" + gcsAssetID + ")送股权益设置表中无法识别证券代码为" + securityCode + "的送股权益设置信息。") ;
										else
											this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码"  + gcsAssetID + ")送股权益设置表中无法识别证券代码为" + securityCode + "的送股权益设置信息。") ;
										continue;
									}
									
									securityCode = this.getDatabySql(" select tdbs.FTSecurityCode as FSECURITYCODE from Tb_" + groupAndPort[0] + "_Data_RightsIssue tdbs " +
											" where tdbs.FSecurityCode = " + this.getYssPub().getDbLink().sqlString(securityCode) + 
											" and tdbs.fexrightdate =  " + this.getYssPub().getDbLink().sqlDate(this.toDate(gcsTradeDate)) +
											" and tdbs.fcheckstate = 1 " , "FSECURITYCODE");
								}
								
								String investType = tradeData.elementText("FTzbzHK"); //投资类型

								if("F".equalsIgnoreCase(investType))
									investType = "S"; //可供出售
								else if("C".equalsIgnoreCase(investType))
									investType = "F"; //持至到期
								else		//("J".equalsIgnoreCase(investType)) J 或 其它
									investType = "C"; //交易类
								
								String cs = tradeData.elementText("FCS"); //	GCS钱/券标识
								
								/**
								 * 证券收付标识  = 交易类型 即 权益类型
								 * */
								String gcsSecurityIdenti = tradeData.elementText("FQyLx");
								if(gcsSecurityIdenti == null || gcsSecurityIdenti.trim().length() == 0)
									continue;
								String tradeType = "" ;
								if("GPPX".equalsIgnoreCase(gcsSecurityIdenti) || "ZQDX".equalsIgnoreCase(gcsSecurityIdenti))
									tradeType = "06" ;	//06分发派息
								else if("ZQDF".equalsIgnoreCase(gcsSecurityIdenti))
									tradeType = "17" ;	//17债券兑付
								else if("SG".equalsIgnoreCase(gcsSecurityIdenti))
									tradeType = "07" ;	//07送股
								else if("PG".equalsIgnoreCase(gcsSecurityIdenti)){
									if(!"H".equalsIgnoreCase(exchangeCode) && !"S".equalsIgnoreCase(exchangeCode))
										tradeType = "22" ;	//22权证送配(境外)
									else if(("H".equalsIgnoreCase(exchangeCode) || "S".equalsIgnoreCase(exchangeCode)) && "C".equalsIgnoreCase(cs))
										tradeType = "23" ;	//23配股缴款(境内)
									else if(("H".equalsIgnoreCase(exchangeCode) || "S".equalsIgnoreCase(exchangeCode)) && "S".equalsIgnoreCase(cs))
										tradeType = "08" ;	//08配股(境内)
								}
								
								/**
								 * 资金账号
								 * */
								String gcsGoundAcount = tradeData.elementText("FQyTzKm");
								if(gcsGoundAcount == null || gcsGoundAcount.trim().length() == 0)
									continue;
								String cashAcountCode = "";
								
								int cashAcccount = this.getCountbySql(" select count(*) as FCT from tb_" + groupAndPort[0] + "_para_cashaccount tpc " +
																	  " where tpc.fbankaccount = " + this.getYssPub().getDbLink().sqlString(gcsGoundAcount) + 
																	  " and tpc.fcheckstate =1 ", "FCT");
								
								if(cashAcccount != 1){
									this.setReplyCode("1");
									if(this.getReplyRemark() == null || this.getReplyRemark().trim().length() == 0)
										this.setReplyRemark("导入失败:QDII系统(资产代码" +gcsAssetID+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。") ;
									else
										this.setReplyRemark(this.getReplyRemark() + "\nQDII系统(资产代码" +gcsAssetID+ ")现金帐户设置表中没有/有多个银行帐号为" + gcsGoundAcount + "的现金帐户。") ;
									continue;									
								}								
								
								cashAcountCode = this.getDatabySql(" select FCASHACCCODE from tb_" + groupAndPort[0] + "_para_cashaccount tpc " +
										  " where tpc.fbankaccount = " + this.getYssPub().getDbLink().sqlString(gcsGoundAcount) + 
										  " and tpc.fcheckstate =1 ", "FCASHACCCODE");
								
								String curyCode = ""; //币种
								
								curyCode = this.getDatabySql("select FCuryCode from tb_" + groupAndPort[0] + "_para_cashaccount tpc " +
												  " where tpc.fcashacccode = " + this.getYssPub().getDbLink().sqlString(cashAcountCode) + 
												  " and tpc.fcheckstate = 1 ", "FCuryCode"); 
																
								/**
								 * 交收日期  即 结算日期
								 * */
								String gcsJSDate = tradeData.elementText("FJkJzr");
								
								/**
								 * 成交价格
								 * */
								String gcsTradePrice = tradeData.elementText("FQyJg");
								
								if("06".equalsIgnoreCase(tradeType)) 		//分红
									gcsTradePrice ="0" ;
								else if("17".equalsIgnoreCase(tradeType)){	//债券兑付
									gcsTradePrice = this.getDatabySql(" select FFaceValue from Tb_" + groupAndPort[0] + "_Para_FixInterest tpfi " +
													  " where tpfi.fcheckstate =1 " +
													  " and tpfi.fsecuritycode = " + this.getYssPub().getDbLink().sqlString(securityCode), "FFaceValue");
								}else if("07".equalsIgnoreCase(tradeType))	//送股
									gcsTradePrice = "0" ;
								else if("08".equalsIgnoreCase(tradeType))	//配股
									gcsTradePrice = "" + gcsTradePrice ;
								else if("22".equalsIgnoreCase(tradeType))	//22权证送配(境外)
									gcsTradePrice = "0" ;
								else if("23".equalsIgnoreCase(tradeType))	//23配股缴款(境内)
									gcsTradePrice = "" + gcsTradePrice ;
								
								
								/**
								 * 成交数量
								 * */
								String gcsTradeAmount = tradeData.elementText("FKC");
								
								if("06".equalsIgnoreCase(tradeType)) 		//分红
									gcsTradeAmount ="0";
								else if("17".equalsIgnoreCase(tradeType))	//债券兑付
									gcsTradeAmount = "" + gcsTradeAmount ;
								else if("07".equalsIgnoreCase(tradeType))	//送股
									gcsTradeAmount = "" + tradeData.elementText("FQY");
								else if("08".equalsIgnoreCase(tradeType))	//配股
									gcsTradeAmount = "" + tradeData.elementText("FQY");
								else if("22".equalsIgnoreCase(tradeType))	//22权证送配(境外)
									gcsTradeAmount =  "" + tradeData.elementText("FQY");
								else if("23".equalsIgnoreCase(tradeType))	//23配股缴款(境内)
									gcsTradeAmount = "" + YssD.div(Double.parseDouble(tradeData.elementText("FQY")), Double.parseDouble(gcsTradePrice)) ;
								
																
								/**
								 * 成交金额
								 * */
								String gcsTradeMoney = tradeData.elementText("FQY");								
								
								if("06".equalsIgnoreCase(tradeType)) //分红
									gcsTradeMoney = "0" ;
								else if("17".equalsIgnoreCase(tradeType))	//债券兑付  成交价格 * 成交数量
									gcsTradeMoney = "" + YssD.mul(Double.parseDouble(gcsTradePrice), Double.parseDouble(gcsTradeAmount));
								else if("07".equalsIgnoreCase(tradeType))	//送股
									gcsTradeMoney = "0";
								else if("08".equalsIgnoreCase(tradeType))	//配股
									gcsTradeMoney = "" + (Double.parseDouble(gcsTradePrice) * Double.parseDouble(gcsTradeAmount));
								else if("22".equalsIgnoreCase(tradeType)) 	//22权证送配(境外)
									gcsTradeMoney = "0";
								else if("23".equalsIgnoreCase(tradeType)) 	//23配股缴款(境内)
									gcsTradeMoney = "" + gcsTradeMoney ;
								
								/**
								 * 利息
								 * */
								String gcsInterest = tradeData.elementText("FQY");
								
								if("06".equalsIgnoreCase(tradeType)) //分红
									gcsInterest = "" + gcsInterest ;
								else if("17".equalsIgnoreCase(tradeType))	//债券兑付   实收实付 - 成交金额
									gcsInterest = "" + YssD.sub(Double.parseDouble(gcsInterest), YssD.mul(Double.parseDouble(gcsTradePrice), Double.parseDouble(gcsTradeAmount))) ;
								else if("07".equalsIgnoreCase(tradeType))	//送股
									gcsInterest = "0" ;
								else if("08".equalsIgnoreCase(tradeType))	//配股
									gcsInterest = "0" ;
								else if("22".equalsIgnoreCase(tradeType))	//22权证送配(境外)
									gcsInterest = "0" ;
								else if("23".equalsIgnoreCase(tradeType))	//23配股缴款(境内)
									gcsInterest = "0" ;
								
								/**
								 * 佣金
								 * */
								String gcsBrokerageMoney = "0";

								/**
								 * 税费
								 * */
								String gcsTaxFee = "0";								

								/**
								 * 其它费用
								 * */
								String gcsOtherFee = "0";								

								/**
								 * 交收金额  实收实付
								 * */
								String gcsTotalCost = tradeData.elementText("FQY");

								if("06".equalsIgnoreCase(tradeType)) //分红
									gcsTotalCost = "" + YssD.add(Double.parseDouble(gcsTradeMoney) , Double.parseDouble(gcsInterest));
								else if("17".equalsIgnoreCase(tradeType))	//债券兑付  成交价格 * 成交数量
									gcsTotalCost = "" + gcsTotalCost ;
								else if("07".equalsIgnoreCase(tradeType))	//送股
									gcsTotalCost = "" + YssD.add(Double.parseDouble(gcsTradeMoney) , Double.parseDouble(gcsInterest));
								else if("08".equalsIgnoreCase(tradeType))	//配股
									gcsTotalCost = "" + YssD.add(Double.parseDouble(gcsTradeMoney) , Double.parseDouble(gcsInterest));
								else if("22".equalsIgnoreCase(tradeType))
									gcsTotalCost = "" + YssD.add(Double.parseDouble(gcsTradeMoney) , Double.parseDouble(gcsInterest));
								else if("23".equalsIgnoreCase(tradeType))
									gcsTotalCost = "" + YssD.add(Double.parseDouble(gcsTradeMoney) , Double.parseDouble(gcsInterest));
								
								/**
								 * 交易券商  Dict_Brokercode_GCS
								 * */
								String gcsBrokerCode = " ";
								String brokerCode = " ";								
								
								/**
								 * 交易席位代码  Dict_TradeSeat_GCS
								 **/
								String seatCode = " ";
								
								/**
								 * 附言
								 * */
								String gcsPs = " ";
								
								String tradeNums = this.getDatabySql(" select WMSYS.WM_CONCAT(FNum) as FNum from tb_" + groupAndPort[0] + "_Data_SubTrade dst " +
												   " where dst.fportcode = " + this.getYssPub().getDbLink().sqlString(groupAndPort[1]) +
												   " and dst.fbargaindate = " + this.getYssPub().getDbLink().sqlDate(gcsTradeDate) +
												   " and dst.ftradetypecode = " + this.getYssPub().getDbLink().sqlString(tradeType) +
												   " and dst.fsecuritycode = " + this.getYssPub().getDbLink().sqlString(securityCode) , "FNum");
								
								if(tradeNums.trim().length() > 0){	//存在历史数据 先删除	
									/**
									 * 交易数据已结算产生资金调拨主表和字表 数据处理
									 * **/
									String cashTranNums = this.getDatabySql("select WMSYS.WM_CONCAT(FNum) as FNum from tb_" + groupAndPort[0] + "_cash_transfer tctf " +
															" where tctf.ftradenum in( " + operSql.sqlCodes(tradeNums)+ ")" , "FNum");
									if(cashTranNums.trim().length() > 0 ){											
										tradeDeal.executeSql("delete from tb_" + groupAndPort[0] + "_cash_subtransfer where FNum in (" + operSql.sqlCodes(cashTranNums) + ")");
										tradeDeal.executeSql("delete from tb_" + groupAndPort[0] + "_cash_transfer where FNum in (" + operSql.sqlCodes(cashTranNums) + ")");
									}
									tradeDeal.executeSql("delete from tb_" + groupAndPort[0] + "_data_subTrade where FNum in(" + operSql.sqlCodes(tradeNums) + ")");
								}								
									
								/**
								 * 不存在则直接新增
								 * */
								/*,FFEECODE4,FTRADEFEE4,FFEECODE5,FTRADEFEE5, FFEECODE6,FTRADEFEE6,FFEECODE7,FTRADEFEE7,FFEECODE8,FTRADEFEE8*/
								sql = " insert into Tb_" + groupAndPort[0] + "_Data_SubTrade " +
								"(FNUM,FSECURITYCODE,FPORTCODE ,FBROKERCODE ,FINVMGRCODE,FTRADETYPECODE ,FCASHACCCODE,FATTRCLSCODE," +
								"FRATEDATE, FBARGAINDATE ,FBARGAINTIME,FSETTLEDATE ,FSETTLETIME ,FMATUREDATE,FMATURESETTLEDATE,FFACTCASHACCCODE," +
								"FFACTSETTLEMONEY,FEXRATE,FFACTBASERATE,FFACTPORTRATE,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION," +
								"FOLDALLOTAMOUNT,FALLOTFACTOR,FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBAILMONEY,FFEECODE1," +
								"FTRADEFEE1,FFEECODE2,FTRADEFEE2,FFEECODE3,FTRADEFEE3,FTOTALCOST," + "FCOST,FMCOST," +
								"FVCOST,FBASECURYCOST,FMBASECURYCOST,FVBASECURYCOST,FPORTCURYCOST,FMPORTCURYCOST," +
								"FVPORTCURYCOST,FSETTLESTATE,FFACTSETTLEDATE,FSETTLEDESC,FORDERNUM,FDATASOURCE,FDATABIRTH,FSETTLEORGCODE," +
								"FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME," + //FETFBALAACCTCODE,FETFBALASETTLEDATE,FETFBALAMONEY,FETFCASHALTERNAT," +
								"FSEATCODE,FSTOCKHOLDERCODE,FDS," + //FSPLITNUM ,FINVESTTYPE ,FDEALNUM
								"FSECURITYDELAYSETTLESTATE,FBROKERIDCODE," + //FAPPDATE,FJKDR,FRECORDDATE ,FSETTLEORGIDCODE,FDIVDENDTYPE,FBROKERIDCODETYPE,FSETTLEORGIDCODETYPE
								"FHANDCOSTSTATE,"+ //FCLEARINGBROKERCODETYPE,FClearingBrokercode,FCLEARINGACCOUNT,FBSDATE,FCANRETURNMONEY,FBEFORESECURITYCODE
								"FINVESTTYPE) values(?,?,?,?,?,?,?,?," +//FMTREPLACEDATE
															  "?,?,?,?,?,?,?,?," +
															  "?,?,?,?,?,?,?,?," +
															  "?,?,?,?,?,?,?,?," +
															  "?,?,?,?,?,?,?,?," +
															  "?,?,?,?,?,?," +
															  "?,?,?,?,?,?,?,?," +
															  "?,?,?,?,?,?," +
															  "?,?,?," +
															  "?,?," +
															  "?," +
															  "?)";
								
								pst = this.getConnection().prepareStatement(sql);								
								pst.setString(1, tradeDeal.getSubTradeNum(groupAndPort[0], gcsTradeDate, tradeType));//交易编号
								pst.setString(2, securityCode);				//证券代码
								pst.setString(3, groupAndPort[1]);			//组合
								pst.setString(4, brokerCode);				//券商
								pst.setString(5, " ");						//投资经理
								pst.setString(6, tradeType);				//交易类型
								pst.setString(7, cashAcountCode);			//现金账户
								pst.setString(8," ");						//所属分类								
								pst.setDate(9, new java.sql.Date(this.toDate(gcsTradeDate).getTime()));		//汇率日期
								pst.setDate(10, new java.sql.Date(this.toDate(gcsTradeDate).getTime()));		//成交日期
								pst.setString(11, "00:00:00");				//成交时间
								pst.setDate(12, new java.sql.Date(this.toDate(gcsJSDate).getTime()));			//结算日期
								pst.setString(13, "00:00:00");				//结算时间
								pst.setDate(14, new java.sql.Date(this.toDate("9998-12-31").getTime()));		//到期日期
								pst.setDate(15, new java.sql.Date(this.toDate("9998-12-31").getTime()));		//到期结算日期
								pst.setString(16, cashAcountCode);			//实际结算账户
								pst.setDouble(17, YssFun.toDouble(gcsTotalCost));									//实际结算金额
								pst.setDouble(18, 1);						//兑换汇率
								pst.setDouble(19 , operDeal.getCuryRate(this.toDate(gcsTradeDate), curyCode ,groupAndPort[1] , "base"));						//实际基础汇率
								pst.setDouble(20 , operDeal.getCuryRate(this.toDate(gcsTradeDate), curyCode ,groupAndPort[1] , "port"));
								pst.setInt(21 , 1);		//是否自动结算
								pst.setDouble(22 , operDeal.getCuryRate(this.toDate(gcsTradeDate), curyCode ,groupAndPort[1] , "port"));
								pst.setDouble(23 , operDeal.getCuryRate(this.toDate(gcsTradeDate), curyCode ,groupAndPort[1] , "base"));
								pst.setDouble(24, 100);		//分配比例
								pst.setDouble(25, YssFun.toDouble(gcsTradeAmount));		//原始分配数量
								pst.setDouble(26, 1);		//分配因子
								pst.setDouble(27, YssFun.toDouble(gcsTradeAmount));	//交易数量
								pst.setDouble(28, YssFun.toDouble(gcsTradePrice));	//交易价格
								pst.setDouble(29, YssFun.toDouble(gcsTradeMoney));	//交易金额
								pst.setDouble(30, YssFun.toDouble(gcsInterest)); 	//应收利息
								pst.setDouble(31, 0);	//保证金
								pst.setString(32, "");	//费用代码1
								pst.setDouble(33, Double.parseDouble(gcsBrokerageMoney));	//费用1为 佣金
								pst.setString(34, "");		//费用代码2
								pst.setDouble(35, Double.parseDouble(gcsTaxFee));	//费用2 税费
								pst.setString(36, "");	//费用3	
								pst.setDouble(37, Double.parseDouble(gcsOtherFee)); //其他费用
								pst.setDouble(38, Double.parseDouble(gcsTotalCost)); //交收金额
								
								//其它费用
								pst.setDouble(39, 0);
								pst.setDouble(40, 0);
								pst.setDouble(41, 0);
								pst.setDouble(42, 0);
								pst.setDouble(43, 0);
								pst.setDouble(44, 0);
								pst.setDouble(45, 0);
								pst.setDouble(46, 0);
								pst.setDouble(47, 0);
								
								pst.setInt(48, 0); //结算状态
								pst.setDate(49, new java.sql.Date(this.toDate(gcsJSDate).getTime())); //结算日期
								pst.setString(50, " "); //结算描述
								pst.setString(51, " "); //订单编号
								pst.setInt(52, 1);		//数据来源
								pst.setString(53, " "); //交易来源
								pst.setString(54 , " "); //结算机构代码
								pst.setString(55, gcsPs); //描述 
								pst.setInt(56, 1);   //审核状态
								pst.setString(57, "GCS"); //创建人
								pst.setString(58, YssFun.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));//创建时间
								pst.setString(59, "GCS"); //审核人
								pst.setString(60, YssFun.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));//审核时间
								pst.setString(61, seatCode); //席位代码
								pst.setString(62 , " "); //股东代码
								pst.setString(63 ,"ZD_QY"); //操作类型  DS  权益 
								pst.setInt(64,0); //延迟交割标识
								pst.setString(65, gcsBrokerCode); // 券商代码
								pst.setInt(66, 0) ; //手动修改成本标志
								pst.setString(67, investType); //投资类型
								
								pst.executeUpdate();
							}
						}
					}
				}
				this.getConnection().commit();
				this.getConnection().setAutoCommit(false);		
			
		}catch (Exception e) {
			this.setReplyCode("1"); //处理失败标志
			System.out.println("处理GCS行动接口出错：" + e.getStackTrace());
		}finally{
			if(pst != null){
				try {
					pst.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
			if(SwiftMsgDeal.msgReturnClient.get("JJQYXX") != null && SwiftMsgDeal.msgReturnClient.get("JJQYXX").length() > 0){
				String msg = SwiftMsgDeal.msgReturnClient.get("JJQYXX");
				
				SwiftMsgDeal.msgReturnClient.put("JJQYXX", msg + "\n\n["+sDate+"]\tGCS行动接口，数据已接收。" );
			} else {
				SwiftMsgDeal.msgReturnClient.put("JJQYXX", "["+sDate+"]\tGCS行动接口，数据已接收。");
			}
		}
	}
	
}
