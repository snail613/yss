package com.yss.main.etfoperation.etfaccbook;

import java.sql.*;
import java.util.*;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.etfoperation.pojo.StandingBookBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CreateAccBoook extends CtlETFAccBook{
	//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
	/*java.util.Date endDate = null;
	java.util.Date startDate = null;*/
	java.util.Date buyDate = null;
	/**
	 * 构造函数
	 */
	public CreateAccBoook() {
		
	}

	/*public java.util.Date getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}

	public java.util.Date getStartDate() {
		return startDate;
	}

	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}*/
	
	public void parseRowStr(String sRowStr)throws YssException {
		String[] reqAry = null;
		String[] reqDate = null;
		String buyDates = "";//已选的所有申赎日期
		try {
			if(sRowStr.equals("")){
				return;
			}
			
			if(sRowStr.indexOf("\f\f") != -1){
				reqAry = sRowStr.split("\f\f");
				
				if(reqAry.length >= 2){
					buyDates = reqAry[0];
					
					if(buyDates.indexOf(",") != -1){
						reqDate = buyDates.split(",");
						if(reqDate.length >= 2){
							this.startDate = YssFun.toDate(reqDate[0]);//开始的申赎日期
							this.endDate = YssFun.toDate(reqDate[1]);//结束的申赎日期
						}
					}
					this.portCodes = reqAry[1]; // 已选组合代码
				}	
				if(reqAry.length >= 3){
					// 台账类型 S -- 赎回 B -- 申购 ALL -- 全部
					if(reqAry[2].equalsIgnoreCase("B")){
						this.standingBookType = "B"; 
					}else if(reqAry[2].equalsIgnoreCase("S")){
						this.standingBookType = "S"; 
					}
				}
				if(reqAry.length >= 4){
					// ETF台账界面已选的证券代码
					this.securityCodes = reqAry[3]; 
				}
			}
		} catch (Exception e) {
			throw new YssException("解析台帐相关数据出错！", e);
		}
	}

	
	/**
	 * 在台帐生成和查询界面根据申赎日期生成ETF台帐
	 */
	public void generateStandingBook(java.util.Date tradeDate, HashMap hmMaxRightDate, String sType)throws YssException{
			String[] type = sType.split("/t");//解析前台传来的数据
			this.parseRowStr(type[1]);//用基类的方法解析数据
			buyDate = this.endDate;
			// 若要生成多个申赎日期的ETF台帐
			if (this.startDate != null && this.endDate != null) {
				subGenerateStandingBook(tradeDate, hmMaxRightDate);
				
				//计算台帐应退款估值增值
				CalcRefundData refundData = new CalcRefundData(pub);
				refundData.createAccBookRefundMVBy(startDate, endDate, portCodes);
			}
	}
	
	/**
	 * 在台帐生成和查询界面生成多个申赎日期的ETF台帐
	 * @throws YssException
	 */
	private void subGenerateStandingBook(java.util.Date tradeDate, HashMap hmMaxRightDate)throws YssException{
		if(this.startDate.before(buyDate)){
			subInsertIntoStandingBook(tradeDate,buyDate,this.portCodes,hmMaxRightDate);
			buyDate = YssFun.addDay(buyDate, -1);
			subGenerateStandingBook(tradeDate,hmMaxRightDate);
		}
		
		if(this.startDate.equals(buyDate)){
			subInsertIntoStandingBook(tradeDate,buyDate,this.portCodes,hmMaxRightDate);
		}
	}
	
	/**
	 * 结合交易结算明细表和交易结算明细关联表将数据插入到台帐表
	 * @throws YssException
	 */
	public void insertIntoStandingBook(java.util.Date tradeDate,String portCodes, HashMap hmMaxRightDate)throws YssException{
		String strSql = "";
		ResultSet rs = null;
		String num = "";//申请编号
		String allNum = "";//需要重新生成台帐的申请编号
		java.util.Date buyDate = null;//申赎日期
		String deleteDate = "";
		ArrayList alDeleteDate = new ArrayList();
		ETFParamSetAdmin paramSetAdmin = null;
		HashMap etfParam = null;
		ETFParamSetBean paramSet = null;// ETF参数的实体类
		String makeUpMode = "";
		try{
			//查询估值当天有补票和权益数据的申请编号
			strSql = " select distinct FNum from " + pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + 
			" where FMakeUpDate = " + dbl.sqlDate(tradeDate) + " and FNum in(select FNum from " + 
			pub.yssGetTableName("Tb_ETF_TradeStlDtl") + 
			" where FPortCode in(" + operSql.sqlCodes(portCodes) + "))";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				num = rs.getString("FNum");
				allNum += num + ",";
			}
			
			if(allNum.length() >= 1){
				allNum = allNum.substring(0,allNum.length() - 1);
			}
			
			dbl.closeResultSetFinal(rs);
			rs = null;
			
			if(allNum.equals("")){
				//根据申请编号获取相关的申赎日期
				strSql = " select distinct FBuyDate from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl") + 
				" where FBuyDate = " + dbl.sqlDate(tradeDate);
			}
			else{
				//根据申请编号获取相关的申赎日期
				strSql = " select distinct FBuyDate from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl") + 
				" where FNum in(" + operSql.sqlCodes(allNum) + ")";
			}

			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				buyDate = rs.getDate("FBuyDate");
				deleteDate += dbl.sqlDate(buyDate) + ",";
				alDeleteDate.add(buyDate);
			}
			
			if(deleteDate.length() >= 1){
				deleteDate = deleteDate.substring(0,deleteDate.length() - 1);
			}
		
			for(int i = 0; i < alDeleteDate.size(); i++){
				buyDate = (java.util.Date)alDeleteDate.get(i);
				subInsertIntoStandingBook(tradeDate ,buyDate, portCodes, hmMaxRightDate);
			}
			
			paramSetAdmin = new ETFParamSetAdmin();
			paramSetAdmin.setYssPub(pub);
			
			// 根据已选组合代码用于获取相关ETF参数数据
			etfParam = paramSetAdmin.getETFParamInfo(portCodes); 
			paramSet = (ETFParamSetBean) etfParam.get(portCodes);
			
			if (paramSet != null) {
				makeUpMode = paramSet.getSupplyMode();//补票方式
			}
			
			if(makeUpMode.equals("6")){
				updateSumReturn(tradeDate, portCodes);
			}
		}
		catch(Exception e){
			throw new YssException("结合交易结算明细表和交易结算明细关联表将数据插入到台帐表出错！", e);
		}
		finally{
			 dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 结合交易结算明细表和交易结算明细关联表将相关申赎日期的数据插入到台帐表
	 * @param buyDate
	 * @throws YssException
	 */
	public void subInsertIntoStandingBook(java.util.Date tradeDate,java.util.Date buyDate,String portCodes, HashMap hmMaxRightDate)throws YssException{
		String strSql = "";
		ResultSet rs = null;
		StandingBookBean standingBook = null;
		String bs = "";//买卖标志
		String portCode = "";//组合代码
		String securityCode = "";//证券代码
		String stockHolderCode = "";//股东代码
		String num = "";//申请编号
		double makeUpAmount = 0;//补票数量
		double unitCost = 0;//单位成本
		double hpReplaceCash = 0;//应付替代款（本币）
		double hcReplaceCash = 0;//可退替代款发生额（本币）
		java.util.Date makeUpDate = null;//补票日期
		double remaindAmount = 0;//剩余数量
		double hcRefundSum = 0;//应退合计（本币）
		double exchangeRate = 0;//汇率
		double oMakeUpCost = 0;//补票总成本（原币）
		double hMakeUpCost = 0;//补票总成本（本币）
		java.util.Date refundDate = null;//退款日期
		String dataMark = "";//数据标识
		ArrayList alNum = new ArrayList();
		java.util.Date maxExRightDate = null;//相关申请编号对应的最大的权益日期
		ETFTradeSettleDetailRefBean tradeSetDelRef = null;
		String settleMark = "";//清算标志
		int dataDirection = 0;// 数据方向
		int makeUpCount = 0;
		HashMap hmStandingBook = null;
		CreateAccBookRefData ctlETFAcc = null;
		ETFParamSetAdmin paramSetAdmin = null;
		HashMap etfParam = null;
		ETFParamSetBean paramSet = null;// ETF参数的实体类
		String bookTotalType = "";//汇总方式
		BaseOperDeal operDeal = null;
		String holidayCode = "";//节假日代码
		EachExchangeHolidays holiday = null;//节假日获取类
		HashMap changeRateData = new HashMap();//保存换汇汇率
		HashMap changeRateDate = new HashMap();//保存换汇日期
		double factAmount = 0;//实际补票数量
		double tradeUnitCost = 0;//成交单价
		double feeUnitCost = 0;//费用单价
		PreparedStatement pst = null;
		String makeUpMode = "";//补票方式
		try{
			holiday = new EachExchangeHolidays();
			holiday.setYssPub(pub);
			
			paramSetAdmin = new ETFParamSetAdmin();
			paramSetAdmin.setYssPub(pub);
			
			operDeal = new BaseOperDeal();
			operDeal.setYssPub(pub);
			
			ctlETFAcc = new CreateAccBookRefData();
			ctlETFAcc.setYssPub(pub);
			
			etfParam = paramSetAdmin.getETFParamInfo(portCodes); // 根据已选组合代码用于获取相关ETF参数数据
			
			hmStandingBook = new HashMap();
			
			//根据申赎日期删除台帐表中的相关数据
			strSql = " delete from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			" where FBuyDate = " + dbl.sqlDate(buyDate) + " and FPortCode in(" + 
			operSql.sqlCodes(portCodes) + ")";
			
			dbl.executeSql(strSql);
			
			strSql = " select * from " + pub.yssGetTableName("Tb_ETF_TradeStlDtl") + 
			" where FBuyDate = " + dbl.sqlDate(buyDate) +
			" and FPortCode in(" + operSql.sqlCodes(portCodes) + ")";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				bs = rs.getString("FBs");//买卖标志
				portCode = rs.getString("FPortCode");//组合代码
				securityCode = rs.getString("FSecurityCode");//证券代码
				stockHolderCode = rs.getString("FStockHolderCode");//股东代码
				num = rs.getString("FNum");//申请编号
				
				if(bs.equals("B")){
					dataDirection = 1;
				}
				if(bs.equals("S")){
					dataDirection = -1;
				}
				
				standingBook = new StandingBookBean();
				
				standingBook.setBuyDate(buyDate);
				standingBook.setBs(bs);
				standingBook.setPortCode(portCode);
				standingBook.setSecurityCode(securityCode);
				standingBook.setStockHolderCode(stockHolderCode);
				standingBook.setBrokerCode(rs.getString("FBrokerCode"));//券商代码
				standingBook.setSeatCode(rs.getString("FSeatCode"));//席位代码
				standingBook.setUnitCost(rs.getDouble("FUnitCost"));//单位成本
				standingBook.setMakeUpAmount(YssD.mul(rs.getDouble("FReplaceAmount"),dataDirection));//替代数量
				standingBook.setReplaceCash(YssD.mul(rs.getDouble("FHReplaceCash"), dataDirection));//替代金额(本币)
				standingBook.setCanReplaceCash(YssD.mul(rs.getDouble("FHCReplaceCash"), dataDirection));//可退替代款(本币)
				standingBook.setExchangeRate(rs.getDouble("FExchangeRate"));//汇率
				/**shashijie 2011.06.29 需求974 */
				standingBook.setTradeNum(rs.getString("FTradeNum"));//成交编号
				/**end*/
				alNum.add(num);
				hmStandingBook.put(num, standingBook);
			}
			
			dbl.closeResultSetFinal(rs);
			rs = null;
			
			//查询相关申请编号的补票和权益数据
			
			strSql = " select * from (select b.* from (select * from " + 
			pub.yssGetTableName("Tb_ETF_TradeStlDtl") + " where FBuyDate = " + 
			dbl.sqlDate(buyDate) + " and FSecurityCode <> ' ' and FPortCode in(" + 
			operSql.sqlCodes(portCodes) + ")) a left join " + 
			pub.yssGetTableName("Tb_ETF_TradStlDtlRef") + 
			" b on a.FNum = b.FNum) where FMakeUpDate <> to_date('99981231','yyyyMMdd') " + 
			" order by FNum, FRefNum ";

			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				num = rs.getString("FNum");//申请编号
				makeUpDate = rs.getDate("FMakeUpDate");//补票日期
				makeUpAmount = rs.getDouble("FMakeUpAmount");//补票数量
				unitCost = rs.getDouble("FUnitCost");//单位成本
				hpReplaceCash = rs.getDouble("FHPReplaceCash");//应付替代款（本币）
				hcReplaceCash = rs.getDouble("FHCPReplaceCash");//可退替代款发生额（本币）
				remaindAmount = rs.getDouble("FRemaindAmount");//剩余数量
				hcRefundSum = rs.getDouble("FHCRefundSum");//应退合计（本币）
				refundDate = rs.getDate("FRefundDate");// 退款日期
				dataMark = rs.getString("FDataMark");//数据标识
				settleMark = rs.getString("FSettleMark");//清算标志
				dataDirection = Integer.parseInt(rs.getString("FDataDirection"));//数据方向
				exchangeRate = rs.getDouble("FExchangeRate");//汇率
				oMakeUpCost = rs.getDouble("FOMakeUpCost");//补票总成本（原币）
				hMakeUpCost = rs.getDouble("FHMakeUpCost");//补票总成本（本币）
				factAmount = rs.getDouble("FFactAmount");//实际补票数量
				tradeUnitCost = rs.getDouble("FTradeUnitCost");//成交单价
				feeUnitCost = rs.getDouble("FFeeUnitCost");//费用单价
				
				standingBook = (StandingBookBean)hmStandingBook.get(num);

				if(standingBook != null){
					portCode = standingBook.getPortCode();
					paramSet = (ETFParamSetBean) etfParam.get(portCode);
					
					if(paramSet != null){
						holidayCode = paramSet.getSHolidayCode();
					}
					
					//设置剩余数据
					standingBook.setRemaindAmount(YssD.mul(remaindAmount, dataDirection));
					standingBook.setSumReturn(YssD.mul(hcRefundSum, dataDirection));
					
					if(settleMark.equals("Y")){
						standingBook.setRefundDate(refundDate);
					}
					
					if(dataMark.equals("0")){//补票数据
						
						makeUpCount = judgeMakeUpInfo(standingBook);
						
						if(makeUpCount == 1){
							standingBook.setMakeUpDate1(makeUpDate);
							standingBook.setMakeUpAmount1(YssD.mul(makeUpAmount, dataDirection));
							standingBook.setMakeUpUnitCost1(unitCost);
							standingBook.setMakeUpRepCash1(YssD.mul(hpReplaceCash, dataDirection));
							standingBook.setCanMkUpRepCash1(YssD.mul(hcReplaceCash, dataDirection));
							standingBook.setoMakeUpCost1(YssD.mul(oMakeUpCost, dataDirection));
							standingBook.sethMakeUpCost1(YssD.mul(hMakeUpCost, dataDirection));
							standingBook.setExRate1(exchangeRate);
							standingBook.setFactAmount(factAmount);
							standingBook.setTradeUnitCost1(tradeUnitCost);
							standingBook.setFeeUnitCost1(feeUnitCost);
						}
						if(makeUpCount == 2){
							standingBook.setMakeUpDate2(makeUpDate);
							standingBook.setMakeUpAmount2(YssD.mul(makeUpAmount, dataDirection));
							standingBook.setMakeUpUnitCost2(unitCost);
							standingBook.setMakeUpRepCash2(YssD.mul(hpReplaceCash, dataDirection));
							standingBook.setCanMkUpRepCash2(YssD.mul(hcReplaceCash, dataDirection));
							standingBook.setoMakeUpCost2(YssD.mul(oMakeUpCost, dataDirection));
							standingBook.sethMakeUpCost2(YssD.mul(hMakeUpCost, dataDirection));
							standingBook.setExRate2(exchangeRate);
							standingBook.setTradeUnitCost2(tradeUnitCost);
							standingBook.setFeeUnitCost2(feeUnitCost);
						}
						if(makeUpCount == 3){
							standingBook.setMakeUpDate3(makeUpDate);
							standingBook.setMakeUpAmount3(YssD.mul(makeUpAmount, dataDirection));
							standingBook.setMakeUpUnitCost3(unitCost);
							standingBook.setMakeUpRepCash3(YssD.mul(hpReplaceCash, dataDirection));
							standingBook.setCanMkUpRepCash3(YssD.mul(hcReplaceCash, dataDirection));
							standingBook.setoMakeUpCost3(YssD.mul(oMakeUpCost, dataDirection));
							standingBook.sethMakeUpCost3(YssD.mul(hMakeUpCost, dataDirection));
							standingBook.setExRate3(exchangeRate);
							standingBook.setTradeUnitCost3(tradeUnitCost);
							standingBook.setFeeUnitCost3(feeUnitCost);
						}
						if(makeUpCount == 4){
							standingBook.setMakeUpDate4(makeUpDate);
							standingBook.setMakeUpAmount4(YssD.mul(makeUpAmount, dataDirection));
							standingBook.setMakeUpUnitCost4(unitCost);
							standingBook.setMakeUpRepCash4(YssD.mul(hpReplaceCash, dataDirection));
							standingBook.setCanMkUpRepCash4(YssD.mul(hcReplaceCash, dataDirection));
							standingBook.setoMakeUpCost4(YssD.mul(oMakeUpCost, dataDirection));
							standingBook.sethMakeUpCost4(YssD.mul(hMakeUpCost, dataDirection));
							standingBook.setExRate4(exchangeRate);
							standingBook.setTradeUnitCost4(tradeUnitCost);
							standingBook.setFeeUnitCost4(feeUnitCost);
						}
						if(makeUpCount == 5){
							standingBook.setMakeUpDate5(makeUpDate);
							standingBook.setMakeUpAmount5(YssD.mul(makeUpAmount, dataDirection));
							standingBook.setMakeUpUnitCost5(unitCost);
							standingBook.setMakeUpRepCash5(YssD.mul(hpReplaceCash, dataDirection));
							standingBook.setCanMkUpRepCash5(YssD.mul(hcReplaceCash, dataDirection));
							standingBook.setoMakeUpCost5(YssD.mul(oMakeUpCost, dataDirection));
							standingBook.sethMakeUpCost5(YssD.mul(hMakeUpCost, dataDirection));
							standingBook.setExRate5(exchangeRate);
							standingBook.setTradeUnitCost5(tradeUnitCost);
							standingBook.setFeeUnitCost5(feeUnitCost);
						}
					}
					if (dataMark.equals("1")) {//强制处理数据
						standingBook.setMustMkUpDate(makeUpDate);
						standingBook.setMustMkUpAmount(YssD.mul(makeUpAmount, dataDirection));
						standingBook.setMustMkUpUnitCost(unitCost);
						standingBook.setMustMkUpRepCash(YssD.mul(hpReplaceCash, dataDirection));
						standingBook.setMustCMkUpRepCash(YssD.mul(hcReplaceCash, dataDirection));
						standingBook.setoMustMkUpCost(YssD.mul(oMakeUpCost, dataDirection));
						standingBook.sethMustMkUpCost(YssD.mul(hMakeUpCost, dataDirection));
						standingBook.setMustExRate(exchangeRate);
						standingBook.setMustTradeUnitCost(tradeUnitCost);
						standingBook.setMustFeeUnitCost(feeUnitCost);
					}
					
					holiday.parseRowStr(holidayCode+"\t"+ -1 +"\t" + refundDate.toString());
					// 换汇日期 = 退款日期 - 一个工作日
					standingBook.setExRateDate(YssFun.toSqlDate(holiday.getOperValue("getWorkDate")));
			
					hmStandingBook.put(num, standingBook);
				}
			}
			
			for(int i = 0; i < alNum.size(); i++){
				num = (String)alNum.get(i);
				
				//获取相关申请编号对应的最大的权益日期
				if(hmMaxRightDate != null){
					maxExRightDate = (java.util.Date)hmMaxRightDate.get(num);
				}
				
				//若有权益数据
				if(maxExRightDate != null){
					//获取相关权益数据
					tradeSetDelRef = ctlETFAcc.getMaxRightInfos(num, maxExRightDate);
				}
				
				//根据申请编号获取
				standingBook = (StandingBookBean)hmStandingBook.get(num);
				
				if(standingBook != null && maxExRightDate != null){
					bs = standingBook.getBs();
					
					if(bs.equals("B")){
						dataDirection = 1;
					}
					if(bs.equals("S")){
						dataDirection = -1;
					}
					
					standingBook.setExRightDate(maxExRightDate);
					
					if(tradeSetDelRef == null){
						standingBook.setSumAmount(0);
						standingBook.setRealAmount(0);
						standingBook.setTotalInterest(0);
						standingBook.setWarrantCost(0);
					}
					else{
						standingBook.setSumAmount(YssD.mul(tradeSetDelRef.getSumAmount(), dataDirection));
						standingBook.setRealAmount(YssD.mul(tradeSetDelRef.getRealAmount(), dataDirection));
						standingBook.setTotalInterest(YssD.mul(tradeSetDelRef.getInterest(), dataDirection));
						standingBook.setWarrantCost(YssD.mul(tradeSetDelRef.getWarrantCost(), dataDirection));
					}
					
					hmStandingBook.put(num, standingBook);
				}
			}
			
			dbl.closeResultSetFinal(rs);
			
			strSql = " select * from " + pub.yssGetTableName("tb_etf_bookexratedata") + " where FCheckState = 1";
			
			rs = dbl.openResultSet(strSql);
			String sKey="";
			while(rs.next()){
				sKey = rs.getString("FPortCode")+"\t"+rs.getString("FBookType") + "\t" + rs.getDate("FBuyDate");
				
				changeRateData.put(sKey,new java.lang.Double(rs.getDouble("FExRateValue")));
				changeRateDate.put(sKey,rs.getDate("FExRateDate"));
			}
			
			etfParam = paramSetAdmin.getETFParamInfo(portCodes); // 根据已选组合代码用于获取相关ETF参数数据
			
			strSql = " insert into " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			"(FBuyDate,FBs,FPortCode,FSecurityCode,FStockHolderCode,FBrokerCode,FSeatCode,"+
			"FMakeUpAmount,FUnitCost,FReplaceCash,FCanReplaceCash,FExRightDate,FSumAmount," +
			"FRealAmount,FTotalInterest,FWarrantCost,FMakeUpDate1,FMakeUpAmount1,FMakeUpUnitCost1," +
			"FMakeUpRepCash1,FCanMkUpRepCash1,FMakeUpDate2,FMakeUpAmount2,FMakeUpUnitCost2,FMakeUpRepCash2," +
			"FCanMkUpRepCash2,FMakeUpDate3,FMakeUpAmount3,FMakeUpUnitCost3,FMakeUpRepCash3,FCanMkUpRepCash3," +
			"FMakeUpDate4,FMakeUpAmount4,FMakeUpUnitCost4,FMakeUpRepCash4,FCanMkUpRepCash4,FMakeUpDate5," +
			"FMakeUpAmount5,FMakeUpUnitCost5,FMakeUpRepCash5,FCanMkUpRepCash5,FMustMkUpDate,FMustMkUpAmount," +
			"FMustMkUpUnitCost,FMustMkUpRepCash,FMustCMkUpRepCash,FRemaindAmount,FSumReturn,FRefundDate," +
			"FCreator,FCreateTime,FExchangeRate,FNum,FOMakeUpCost1,FHMakeUpCost1,FOMakeUpCost2,FHMakeUpCost2," + 
			"FOMakeUpCost3,FHMakeUpCost3,FOMakeUpCost4,FHMakeUpCost4,FOMakeUpCost5,FHMakeUpCost5,FOMustMkUpCost," + 
			"FHMustMkUpCost,FOrderCode,FGradeType1,FGradeType2,FGradeType3,FExRate1,FExRate2,FExRate3,FExRate4," + 
			"FExRate5,FMustExRate,FFactExRate,FExRateDate,FFactAmount,FTradeUnitCost1,FFeeUnitCost1," + 
			"FTradeUnitCost2,FFeeUnitCost2,FTradeUnitCost3,FFeeUnitCost3,FTradeUnitCost4,FFeeUnitCost4," +
			"FTradeUnitCost5,FFeeUnitCost5,FMustTradeUnitCost,FMustFeeUnitCost" +
			/**shashijie 2011.06.29 需求974 */
			" , FTradeNum ) " +
			/**end*/
			"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," 
			+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			pst = dbl.openPreparedStatement(strSql);
			
			for(int i = 0; i < alNum.size(); i++){
				num = (String)alNum.get(i);

				paramSet = (ETFParamSetBean) etfParam.get(portCode);// 根据组合代码获取ETF参数设置
				
				if (paramSet != null) {
					bookTotalType = paramSet.getSBookTotalType();//汇总方式
					makeUpMode = paramSet.getSupplyMode();//补票方式
				} else {
					throw new YssException("请在ETF参数设置中设置" + portCode + "组合的相关参数！");
				}
				
				//根据申请编号获取
				standingBook = (StandingBookBean)hmStandingBook.get(num);
				pst.setDate(1, YssFun.toSqlDate(standingBook.getBuyDate()));
				pst.setString(2, standingBook.getBs());
				pst.setString(3, standingBook.getPortCode());
				pst.setString(4, standingBook.getSecurityCode());
				pst.setString(5, standingBook.getStockHolderCode());
				pst.setString(6, standingBook.getBrokerCode());
				pst.setString(7, standingBook.getSeatCode());
				pst.setDouble(8, standingBook.getMakeUpAmount());
				pst.setDouble(9, standingBook.getUnitCost());
				pst.setDouble(10, standingBook.getReplaceCash());
				pst.setDouble(11, standingBook.getCanReplaceCash());
				
				if(standingBook.getExRightDate() == null){
					pst.setDate(12, YssFun.toSqlDate(YssFun.parseDate("9998-12-31")));
				}
				else{
					pst.setDate(12, YssFun.toSqlDate(standingBook.getExRightDate()));
				}
				
				pst.setDouble(13, standingBook.getSumAmount());
				pst.setDouble(14, standingBook.getRealAmount());
				pst.setDouble(15, standingBook.getTotalInterest());
				pst.setDouble(16, standingBook.getWarrantCost());
				
				if(standingBook.getMakeUpDate1() == null){
					pst.setDate(17, YssFun.toSqlDate(YssFun.parseDate("9998-12-31")));
				}
				else{
					pst.setDate(17, YssFun.toSqlDate(standingBook.getMakeUpDate1()));
				}
				
				pst.setDouble(18, standingBook.getMakeUpAmount1());
				pst.setDouble(19, standingBook.getMakeUpUnitCost1());
				pst.setDouble(20, standingBook.getMakeUpRepCash1());
				pst.setDouble(21, standingBook.getCanMkUpRepCash1());
				
				if(standingBook.getMakeUpDate2() == null){
					pst.setDate(22, YssFun.toSqlDate(YssFun.parseDate("9998-12-31")));
				}
				else{
					pst.setDate(22, YssFun.toSqlDate(standingBook.getMakeUpDate2()));
				}
				
				pst.setDouble(23, standingBook.getMakeUpAmount2());
				pst.setDouble(24, standingBook.getMakeUpUnitCost2());
				pst.setDouble(25, standingBook.getMakeUpRepCash2());
				pst.setDouble(26, standingBook.getCanMkUpRepCash2());
				
				if(standingBook.getMakeUpDate3() == null){
					pst.setDate(27, YssFun.toSqlDate(YssFun.parseDate("9998-12-31")));
				}
				else{
					pst.setDate(27, YssFun.toSqlDate(standingBook.getMakeUpDate3()));
				}

				pst.setDouble(28, standingBook.getMakeUpAmount3());
				pst.setDouble(29, standingBook.getMakeUpUnitCost3());
				pst.setDouble(30, standingBook.getMakeUpRepCash3());
				pst.setDouble(31, standingBook.getCanMkUpRepCash3());
				
				if(standingBook.getMakeUpDate4() == null){
					pst.setDate(32, YssFun.toSqlDate(YssFun.parseDate("9998-12-31")));
				}
				else{
					pst.setDate(32, YssFun.toSqlDate(standingBook.getMakeUpDate4()));
				}
				
				pst.setDouble(33, standingBook.getMakeUpAmount4());
				pst.setDouble(34, standingBook.getMakeUpUnitCost4());
				pst.setDouble(35, standingBook.getMakeUpRepCash4());
				pst.setDouble(36, standingBook.getCanMkUpRepCash4());
				
				if(standingBook.getMakeUpDate5() == null){
					pst.setDate(37, YssFun.toSqlDate(YssFun.parseDate("9998-12-31")));
				}
				else{
					pst.setDate(37, YssFun.toSqlDate(standingBook.getMakeUpDate5()));
				}

				pst.setDouble(38, standingBook.getMakeUpAmount5());
				pst.setDouble(39, standingBook.getMakeUpUnitCost5());
				pst.setDouble(40, standingBook.getMakeUpRepCash5());
				pst.setDouble(41, standingBook.getCanMkUpRepCash5());
				
				if(standingBook.getMustMkUpDate() == null){
					pst.setDate(42, YssFun.toSqlDate(YssFun.parseDate("9998-12-31")));
				}
				else{
					pst.setDate(42, YssFun.toSqlDate(standingBook.getMustMkUpDate()));
				}

				pst.setDouble(43, standingBook.getMustMkUpAmount());
				pst.setDouble(44, standingBook.getMustMkUpUnitCost());
				pst.setDouble(45, standingBook.getMustMkUpRepCash());
				pst.setDouble(46, standingBook.getMustCMkUpRepCash());
				pst.setDouble(47, standingBook.getRemaindAmount());
				if(!makeUpMode.equals("6")){
					sKey = standingBook.getPortCode()+"\t"+standingBook.getBs()+"\t"+YssFun.toSqlDate(standingBook.getBuyDate());
				}
				else{
					if(standingBook.getMakeUpDate1() == null){
						standingBook.setMakeUpDate1(YssFun.parseDate("9998-12-31"));
					}
					sKey = standingBook.getPortCode()+"\t"+standingBook.getBs()+"\t"+YssFun.toSqlDate(standingBook.getMakeUpDate1());
				}
				if(changeRateData.containsKey(sKey)){
					if(standingBook.getBs().equalsIgnoreCase("B")){
						//实际应退合计 = 替代金额（本币）- round(补票总成本（原币）* 实际汇率,2) - 强制处理总成本（本币）
						// = 替代金额（本币）- round(补票数量 × 单位成本 × 实际汇率,2) - 强制处理总成本（本币）
						pst.setDouble(48, 
								YssD.sub(
										standingBook.getReplaceCash(),
										YssD.round(
												YssD.mul(
														standingBook.getMakeUpAmount1(),
														standingBook.getMakeUpUnitCost1(),
										                Double.parseDouble(changeRateData.get(sKey).toString())
										                ),
										         2),
										standingBook.gethMustMkUpCost()));
					}else{
						//实际应退合计 = （round(补票总成本（原币）* 实际汇率,2) + 强制处理总成本（本币）） × -1
						// = （round(补票数量 × 单位成本 × 实际汇率,2) + 强制处理总成本（本币）） × -1
						pst.setDouble(48, 
								YssD.mul(
										YssD.add(
								             YssD.round(
								            		 YssD.mul(
								            				 standingBook.getMakeUpAmount1(),
								            				 standingBook.getMakeUpUnitCost1(),
								                             Double.parseDouble(changeRateData.get(sKey).toString())
								                             ),
							                        	2),
								standingBook.gethMustMkUpCost()),
								-1)
								);
					}
				}else{
					if(standingBook.getMakeUpAmount1() == 0){//若没有补票数据
						if(standingBook.getBs().equalsIgnoreCase("B")){
							//实际应退合计 = 替代金额（本币） - 强制处理总成本（本币）
							pst.setDouble(48, 
									YssD.sub(
											standingBook.getReplaceCash(),
											standingBook.gethMustMkUpCost()));
						}else{
							//实际应退合计 = 强制处理总成本（本币） × -1
							pst.setDouble(48, 
									YssD.mul(	
									standingBook.gethMustMkUpCost(),
									-1)
									);
						}
					}
					else{//若有补票数据 且没有实际汇率 则 应退合计等于0
						pst.setDouble(48, standingBook.getSumReturn());
					}
				}

				if(standingBook.getRefundDate() == null){
					pst.setDate(49, YssFun.toSqlDate(YssFun.parseDate("9998-12-31")));
				}
				else{
					pst.setDate(49, YssFun.toSqlDate(standingBook.getRefundDate()));
				}

				pst.setString(50, pub.getUserCode());
				pst.setString(51, YssFun.formatDatetime(new java.util.Date()));
				pst.setDouble(52, standingBook.getExchangeRate());
				pst.setString(53,num);
				pst.setDouble(54, standingBook.getoMakeUpCost1());
				pst.setDouble(55, standingBook.gethMakeUpCost1());
				pst.setDouble(56, standingBook.getoMakeUpCost2());
				pst.setDouble(57, standingBook.gethMakeUpCost2());
				pst.setDouble(58, standingBook.getoMakeUpCost3());
				pst.setDouble(59, standingBook.gethMakeUpCost3());
				pst.setDouble(60, standingBook.getoMakeUpCost4());
				pst.setDouble(61, standingBook.gethMakeUpCost4());
				pst.setDouble(62, standingBook.getoMakeUpCost5());
				pst.setDouble(63, standingBook.gethMakeUpCost5());
				pst.setDouble(64, standingBook.getoMustMkUpCost());
				pst.setDouble(65, standingBook.gethMustMkUpCost());
				
				//若按股票汇总
				if(bookTotalType.equals("stock")){
					pst.setString(66, standingBook.getSecurityCode() + "##" + standingBook.getStockHolderCode());//
					pst.setString(67, standingBook.getSecurityCode().trim());
					pst.setString(68, standingBook.getStockHolderCode().trim());
				}
				//若按投资者汇总
				if (bookTotalType.equals("investor")) {
					pst.setString(66, standingBook.getStockHolderCode() + "##" + standingBook.getSecurityCode());
					pst.setString(67, standingBook.getStockHolderCode().trim());
					pst.setString(68, standingBook.getSecurityCode().trim());
				}

				pst.setString(69, "");
				pst.setDouble(70, standingBook.getExRate1());
				pst.setDouble(71, standingBook.getExRate2());
				pst.setDouble(72, standingBook.getExRate3());
				pst.setDouble(73, standingBook.getExRate4());
				pst.setDouble(74, standingBook.getExRate5());
				pst.setDouble(75, standingBook.getMustExRate());
				if(changeRateData.containsKey(sKey)){
					if(!makeUpMode.equals("6")){
					pst.setDouble(76, java.lang.Double.parseDouble(changeRateData.get(sKey).toString()));
					}
					else{
						if((standingBook.getoMakeUpCost1() == 0 && standingBook.getoMustMkUpCost() != 0) 
								|| standingBook.getSecurityCode().equals(" ")){
							pst.setDouble(76, 0);
						}
						else{
							pst.setDouble(76, java.lang.Double.parseDouble(changeRateData.get(sKey).toString()));
						}
					}
				}else{
					if(!makeUpMode.equals("6")){
						pst.setDouble(76, standingBook.getFactExRate());
					}
					else{
						if((standingBook.getoMakeUpCost1() == 0 && standingBook.getoMustMkUpCost() != 0) 
								|| standingBook.getSecurityCode().equals(" ")){
							pst.setDouble(76, 0);
						}
						else{
							pst.setDouble(76, standingBook.getFactExRate());
						}
					}
				}
				if(changeRateDate.containsKey(sKey)){
					if(!makeUpMode.equals("6")){
						pst.setDate(77, YssFun.toSqlDate(changeRateDate.get(sKey).toString()));
					}
					else{
						//若有强制处理数据  没有补票数据 的话
						if((standingBook.getoMakeUpCost1() == 0 && standingBook.getoMustMkUpCost() != 0) 
								|| standingBook.getSecurityCode().equals(" ")){
							pst.setDate(77, null);
						}
						else{
							pst.setDate(77, YssFun.toSqlDate(changeRateDate.get(sKey).toString()));
						}
					}
				}else{
					if (!makeUpMode.equals("6")) {// 若补票方式不是轧差补票_强退
						pst.setDate(77, YssFun.toSqlDate((standingBook.getExRateDate() != null ? 
								standingBook.getExRateDate() : YssFun.toSqlDate("9998-12-31"))));
					} else {
						pst.setDate(77, null);
					}
				}
				
				pst.setDouble(78, standingBook.getFactAmount());
				pst.setDouble(79, standingBook.getTradeUnitCost1());
				pst.setDouble(80, standingBook.getFeeUnitCost1());
				pst.setDouble(81, standingBook.getTradeUnitCost2());
				pst.setDouble(82, standingBook.getFeeUnitCost2());
				pst.setDouble(83, standingBook.getTradeUnitCost3());
				pst.setDouble(84, standingBook.getFeeUnitCost3());
				pst.setDouble(85, standingBook.getTradeUnitCost4());
				pst.setDouble(86, standingBook.getFeeUnitCost4());
				pst.setDouble(87, standingBook.getTradeUnitCost5());
				pst.setDouble(88, standingBook.getFeeUnitCost5());
				pst.setDouble(89, standingBook.getMustTradeUnitCost());
				pst.setDouble(90, standingBook.getMustFeeUnitCost());
				/**shashijie 2011.06.29 需求974 */
				pst.setString(91, standingBook.getTradeNum());
				/**end*/
				pst.addBatch();
			}
			
			pst.executeBatch();
			
			if(makeUpMode.equals("6")){
				updateReMaindAmount(buyDate);//更新台帐无补票和强退的剩余数量
			}
			
			upDateSumInfo(buyDate, portCodes);//更新台帐汇总数据
		}
		catch(Exception e){
			throw new YssException("结合交易结算明细表和交易结算明细关联表将相关申赎日期的数据插入到台帐表出错！", e);
		}
		finally{
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 用于判断补票次数
	 * @param standingBook
	 * @return
	 */
	private int judgeMakeUpInfo(StandingBookBean standingBook){
		int i = 0;
		if(standingBook.getMakeUpDate1() == null){
			i = 1;
			return i;
		}
		if(standingBook.getMakeUpDate2() == null){
			i = 2;
			return i;
		}
		if(standingBook.getMakeUpDate3() == null){
			i = 3;
			return i;
		}
		if(standingBook.getMakeUpDate4() == null){
			i = 4;
			return i;
		}
		if(standingBook.getMakeUpDate5() == null){
			i = 5;
			return i;
		}
		return i;
	}
	
	/**
	 * 更新台帐汇总数据
	 * @param buyDate
	 * @param portCodes
	 * @throws YssException
	 */
	private void upDateSumInfo(java.util.Date buyDate,String portCodes)throws YssException{
		String strSql = "";
		ResultSet rs = null;
		StandingBookBean standingBook = null;
		ArrayList alUpDateSum = new ArrayList();
		Statement st = null;
		Connection conn = null;
		boolean bTrans = false; // 代表是否开始了事务
		try{
			strSql = 
			" select FBuyDate, FBs, FPortCode, FStockHolderCode, FExchangeRate, "+
			"sum(FMakeUpAmount) as FMakeUpAmount, sum(FReplaceCash) as FReplaceCash, " + 
			"sum(FCanReplaceCash) as FCanReplaceCash, sum(FSumAmount) as FSumAmount, " +
			"sum(FRealAmount) as FRealAmount, sum(FTotalInterest) as FTotalInterest, " + 
			"sum(FWarrantCost) as FWarrantCost, " + 
			"sum(FMakeUpAmount1) as FMakeUpAmount1, sum(FMakeUpRepCash1) as FMakeUpRepCash1, " + 
			"sum(FCanMkUpRepCash1) as FCanMkUpRepCash1, sum(FMakeUpAmount2) as FMakeUpAmount2, " +
			"sum(FMakeUpRepCash2) as FMakeUpRepCash2, sum(FCanMkUpRepCash2) as FCanMkUpRepCash2, " + 
			"sum(FMakeUpAmount3) as FMakeUpAmount3, sum(FMakeUpRepCash3) as FMakeUpRepCash3, " +
			"sum(FCanMkUpRepCash3) as FCanMkUpRepCash3, sum(FMakeUpAmount4) as FMakeUpAmount4, " +
			"sum(FMakeUpRepCash4) as FMakeUpRepCash4, sum(FCanMkUpRepCash4) as FCanMkUpRepCash4, " + 
			"sum(FMakeUpAmount5) as FMakeUpAmount5, sum(FMakeUpRepCash5) as FMakeUpRepCash5, " +
			"sum(FCanMkUpRepCash5) as FCanMkUpRepCash5, sum(FMustMkUpAmount) as FMustMkUpAmount, " +
			"sum(FMustMkUpRepCash) as FMustMkUpRepCash, sum(FMustCMkUpRepCash) as FMustCMkUpRepCash, " + 
			"sum(FRemaindAmount) as FRemaindAmount, sum(FSumReturn) as FSumReturn, " + 
			"sum(FOMakeUpCost1) as FOMakeUpCost1, sum(FHMakeUpCost1) as FHMakeUpCost1, " + 
			"sum(FOMakeUpCost2) as FOMakeUpCost2, sum(FHMakeUpCost2) as FHMakeUpCost2, " +
			"sum(FOMakeUpCost3) as FOMakeUpCost3, sum(FHMakeUpCost3) as FHMakeUpCost3, " +
			"sum(FOMakeUpCost4) as FOMakeUpCost4, sum(FHMakeUpCost4) as FHMakeUpCost4, " + 
			"sum(FOMakeUpCost5) as FOMakeUpCost5, sum(FHMakeUpCost5) as FHMakeUpCost5, " +
			"sum(FOMustMkUpCost) as FOMustMkUpCost, sum(FHMustMkUpCost) as FHMustMkUpCost from " + 
			pub.yssGetTableName("Tb_ETF_StandingBook") + " where FBuyDate = " + dbl.sqlDate(buyDate) + 
			" and FPortCode in(" + operSql.sqlCodes(portCodes) + 
			") and FSecurityCode <> ' ' group by FBuyDate, FBS, FPortCode, FStockHolderCode, FExchangeRate ";
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				standingBook = new StandingBookBean();
				
				standingBook.setBuyDate(rs.getDate("FBuyDate"));
				standingBook.setBs(rs.getString("FBS"));
				standingBook.setStockHolderCode(rs.getString("FStockHolderCode"));
				standingBook.setMakeUpAmount(rs.getDouble("FMakeUpAmount"));
				standingBook.setReplaceCash(rs.getDouble("FReplaceCash"));
				standingBook.setCanReplaceCash(rs.getDouble("FCanReplaceCash"));
				standingBook.setSumAmount(rs.getDouble("FSumAmount"));
				standingBook.setRealAmount(rs.getDouble("FRealAmount"));
				standingBook.setWarrantCost(rs.getDouble("FWarrantCost"));
				standingBook.setMakeUpAmount1(rs.getDouble("FMakeUpAmount1"));
				standingBook.setMakeUpRepCash1(rs.getDouble("FMakeUpRepCash1"));
				standingBook.setCanMkUpRepCash1(rs.getDouble("FCanMkUpRepCash1"));
				standingBook.setoMakeUpCost1(rs.getDouble("FOMakeUpCost1"));
				standingBook.sethMakeUpCost1(rs.getDouble("FHMakeUpCost1"));
				standingBook.setMakeUpAmount2(rs.getDouble("FMakeUpAmount2"));
				standingBook.setMakeUpRepCash2(rs.getDouble("FMakeUpRepCash2"));
				standingBook.setCanMkUpRepCash2(rs.getDouble("FCanMkUpRepCash2"));
				standingBook.setoMakeUpCost2(rs.getDouble("FOMakeUpCost2"));
				standingBook.sethMakeUpCost2(rs.getDouble("FHMakeUpCost2"));
				standingBook.setMakeUpAmount3(rs.getDouble("FMakeUpAmount3"));
				standingBook.setMakeUpRepCash3(rs.getDouble("FMakeUpRepCash3"));
				standingBook.setCanMkUpRepCash3(rs.getDouble("FCanMkUpRepCash3"));
				standingBook.setoMakeUpCost3(rs.getDouble("FOMakeUpCost3"));
				standingBook.sethMakeUpCost3(rs.getDouble("FHMakeUpCost3"));
				standingBook.setMakeUpAmount4(rs.getDouble("FMakeUpAmount4"));
				standingBook.setMakeUpRepCash4(rs.getDouble("FMakeUpRepCash4"));
				standingBook.setCanMkUpRepCash4(rs.getDouble("FCanMkUpRepCash4"));
				standingBook.setoMakeUpCost4(rs.getDouble("FOMakeUpCost4"));
				standingBook.sethMakeUpCost4(rs.getDouble("FHMakeUpCost4"));
				standingBook.setMakeUpAmount5(rs.getDouble("FMakeUpAmount5"));
				standingBook.setMakeUpRepCash5(rs.getDouble("FMakeUpRepCash5"));
				standingBook.setCanMkUpRepCash5(rs.getDouble("FCanMkUpRepCash5"));
				standingBook.setoMakeUpCost5(rs.getDouble("FOMakeUpCost5"));
				standingBook.sethMakeUpCost5(rs.getDouble("FHMakeUpCost5"));
				standingBook.setMustMkUpAmount(rs.getDouble("FMustMkUpAmount"));
				standingBook.setMustMkUpRepCash(rs.getDouble("FMustMkUpRepCash"));
				standingBook.setMustCMkUpRepCash(rs.getDouble("FMustCMkUpRepCash"));
				standingBook.setoMustMkUpCost(rs.getDouble("FOMustMkUpCost"));
				standingBook.sethMustMkUpCost(rs.getDouble("FHMustMkUpCost"));
				standingBook.setRemaindAmount(rs.getDouble("FRemaindAmount"));
				standingBook.setSumReturn(rs.getDouble("FSumReturn"));
//				standingBook.setRefundDate(rs.getDate("FRefundDate"));
//				standingBook.setExRateDate(rs.getDate("FExRateDate"));
				standingBook.setExchangeRate(rs.getDouble("FExchangeRate"));
				
				alUpDateSum.add(standingBook);
			}
			
			conn = dbl.loadConnection();
			st = conn.createStatement();
			
			conn.setAutoCommit(false);
			bTrans = true;
			
			for(int i = 0; i < alUpDateSum.size(); i++){
				standingBook = (StandingBookBean)alUpDateSum.get(i);
				
				strSql = " update " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
				" set FMakeUpAmount = " + standingBook.getMakeUpAmount() + 
				", FReplaceCash = " + standingBook.getReplaceCash() + 
				", FCanReplaceCash = " + standingBook.getCanReplaceCash() + 
				", FSumAmount = " + standingBook.getSumAmount() +
				", FRealAmount = " + standingBook.getRealAmount() + 
				", FWarrantCost = " + standingBook.getWarrantCost() + 
				", FMakeUpAmount1 = " + standingBook.getMakeUpAmount1() +
				", FMakeUpRepCash1 = " + standingBook.getMakeUpRepCash1() + 
				", FCanMkUpRepCash1 = " + standingBook.getCanMkUpRepCash1() + 
				", FOMakeUpCost1 = " + standingBook.getoMakeUpCost1() +
				", FHMakeUpCost1 = " + standingBook.gethMakeUpCost1() + 
				", FMakeUpAmount2 = " + standingBook.getMakeUpAmount2() + 
				", FMakeUpRepCash2 = " + standingBook.getMakeUpRepCash2() +
				", FCanMkUpRepCash2 = " + standingBook.getCanMkUpRepCash2() + 
				", FOMakeUpCost2 = " + standingBook.getoMakeUpCost2() + 
				", FHMakeUpCost2 = " + standingBook.gethMakeUpCost2() + 
				", FMakeUpAmount3 = " + standingBook.getMakeUpAmount3() + 
				", FMakeUpRepCash3 = " + standingBook.getMakeUpRepCash3() +
				", FCanMkUpRepCash3 = " + standingBook.getCanMkUpRepCash3() + 
				", FOMakeUpCost3 = " + standingBook.getoMakeUpCost3() + 
				", FHMakeUpCost3 = " + standingBook.gethMakeUpCost3() + 
				", FMakeUpAmount4 = " + standingBook.getMakeUpAmount4() + 
				", FMakeUpRepCash4 = " + standingBook.getMakeUpRepCash4() +
				", FCanMkUpRepCash4 = " + standingBook.getCanMkUpRepCash4() + 
				", FOMakeUpCost4 = " + standingBook.getoMakeUpCost4() + 
				", FHMakeUpCost4 = " + standingBook.gethMakeUpCost4() + 
				", FMakeUpAmount5 = " + standingBook.getMakeUpAmount5() + 
				", FMakeUpRepCash5 = " + standingBook.getMakeUpRepCash5() +
				", FCanMkUpRepCash5 = " + standingBook.getCanMkUpRepCash5() + 
				", FOMakeUpCost5 = " + standingBook.getoMakeUpCost5() + 
				", FHMakeUpCost5 = " + standingBook.gethMakeUpCost5() + 
				", FMustMkUpAmount = " + standingBook.getMustMkUpAmount() +
				", FMustMkUpRepCash = " + standingBook.getMustMkUpRepCash() +
				", FMustCMkUpRepCash = " + standingBook.getMustCMkUpRepCash() +
				", FOMustMkUpCost = " + standingBook.getoMustMkUpCost() +
				", FHMustMkUpCost = " + standingBook.gethMustMkUpCost() +
				", FRemaindAmount = " + standingBook.getRemaindAmount() +
				", FSumReturn = " + standingBook.getSumReturn() +
//				", FRefundDate = " + dbl.sqlDate(standingBook.getRefundDate()) +
//				(standingBook.getExRateDate() == null ? "" : ", FExRateDate = " + dbl.sqlDate(standingBook.getExRateDate())) +
				", FExChangeRate = " + standingBook.getExchangeRate() + 
				" where FBuyDate = " + dbl.sqlDate(buyDate) + 
				" and FBS = " + dbl.sqlString(standingBook.getBs()) + 
				" and FStockHolderCode = " + dbl.sqlString(standingBook.getStockHolderCode()) +
				" and FSecurityCode = ' ' ";
				
				st.addBatch(strSql);
			}
			
			st.executeBatch();
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		}
		catch(Exception e){
			throw new YssException("更新台帐汇总数据出错！", e);
		}
		finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * 用于更新台帐表的实际汇率和应退合计和换汇日期
	 * @param portCodes
	 * @throws YssException
	 */
	private void updateSumReturn(java.util.Date tradeDate, String portCodes)throws YssException{
		String strSql = "";
		ResultSet rs = null;
		ResultSet rs1 = null;
		java.util.Date makeUpDate = null;
		double exRate = 0;//实际汇率
		double sumReturn = 0;//应退合计
		String num = "";//申请编号
		java.util.Date exRateDate = null;//换汇日期
		java.util.Date beforeBuyDate = null;//更新数据的申赎日期
		ArrayList alBuyDate = new ArrayList();
		String portCode = "";
		String key = "";
		String[] keys = null;
		Connection con = dbl.loadConnection();
		Statement st = null;
		try{
			st = con.createStatement();
			
			//在ETF台帐换汇数据表中查询已选组合代码 和 换汇日期 为估值日期的补票日期和实际汇率
			strSql = " select * from " + pub.yssGetTableName("Tb_ETF_BookExRateData") + 
			" where FExRateDate = " + dbl.sqlDate(tradeDate) + 
			" and FPortCode in (" + operSql.sqlCodes(portCodes) + ")";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				makeUpDate = rs.getDate("FBuyDate");//补票日期
				exRate = rs.getDouble("FExRateValue");//实际汇率
				exRateDate = rs.getDate("FExRateDate");//换汇日期
				
				//在台帐表中查询剩余数量为零 应退合计为零  第一次补票日期为ETF台帐换汇数据表中的补票日期的数据
				strSql = " select * from " + pub.yssGetTableName("Tb_ETF_StandingBook") +
				" where FRemaindAmount = 0 and FMakeUpDate1 = " +
				dbl.sqlDate(makeUpDate);
				
				rs1 = dbl.openResultSet(strSql);
				
				while(rs1.next()){
					num = rs1.getString("FNum");//获取申请编号
				    beforeBuyDate = rs1.getDate("FBuyDate");//申赎日期
				    portCode = rs1.getString("FPortCode");//组合代码
				    
				    if(!alBuyDate.contains(beforeBuyDate.toString() + "\t" + portCode)){
				    	alBuyDate.add(beforeBuyDate.toString() + "\t" + portCode);
				    }
				    
					if(rs1.getString("FBS").equals("B")){
						//实际应退合计 = 替代金额（本币）- round(补票总成本（原币）* 实际汇率,2) - 强制处理总成本（本币）
						sumReturn = YssD.sub(
								rs1.getDouble("FReplaceCash"),
								YssD.round(
								YssD.mul(												
										rs1.getDouble("FMakeUpAmount1"),												
										rs1.getDouble("FMakeUpUnitCost1"),
										exRate
							             ),
							              2),
							    rs1.getDouble("FHMustMkUpCost"));
					}
					else{
						//实际应退合计 = round(补票总成本（原币）* 实际汇率,2) + 强制处理总成本（本币）
						sumReturn = YssD.mul(
								        YssD.add(
							              YssD.round(
							            		 YssD.mul(
							            				  rs1.getDouble("FMakeUpAmount1"),
							            				  rs1.getDouble("FMakeUpUnitCost1"),
                                                          exRate
							            		          ),
                                                    2),
                                                rs1.getDouble("FHMustMkUpCost")),
							              -1);
					}
					
					//在台长表中更新 剩余数量为0 应退合计为 0  第一次补票日期为ETF台帐换汇数据表中查询出的补票日期的实际汇率和应退合计
					strSql = " update " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
					         " set FFactExRate = " + exRate + ", FSumReturn = " + sumReturn + 
					         " , FExRateDate = " + dbl.sqlDate(exRateDate) +
							 " where FNum = " + dbl.sqlString(num);

					st.addBatch(strSql);
				}
			}
			
			st.executeBatch();
			
			if(alBuyDate.size() != 0){
				for(int i = 0; i < alBuyDate.size(); i++){
					key = (String)alBuyDate.get(i);
					keys = key.split("\t");
					if(keys.length >= 2){
						beforeBuyDate = YssFun.parseDate(keys[0]);
						portCode = keys[1];
						
						upDateSumInfo(beforeBuyDate, portCode);
					}
				}
			}
			
			//若剩余数量不为0 的话  应退合计就不计算
			strSql = " update " + pub.yssGetTableName("Tb_ETF_StandingBook") +
			" set FSumReturn = 0 where FRemaindAmount != 0";
			
			dbl.executeSql(strSql);
		}
		catch(Exception e){
			throw new YssException("更新台帐实际汇率和应退合计和换汇日期的数据出错！", e);
		}
		finally{
			dbl.closeResultSetFinal(rs,rs1);
			dbl.closeStatementFinal(st);
		}
	}
	
	/**
	 *  更新台帐无补票和强退的剩余数量
	 * @param buyDate
	 * @throws YssException
	 */
	private void updateReMaindAmount(java.util.Date buyDate)throws YssException{
		ResultSet rs = null;
		String strSql = "";
		Connection con = dbl.loadConnection();
		Statement st = null;
		try{
			st = con.createStatement();
			
			//若既没有补票数据 且没有强制处理数据 则 将剩余数量更新为申购或赎回ETF基金时的替代数量
			strSql = " select * from " + pub.yssGetTableName("Tb_ETF_StandingBook") + 
			" where FMakeUpDate1 = to_date('99981231','yyyyMMdd') and " +
			" FMustMkUpDate = to_date('99981231','yyyyMMdd') and FBuyDate = " + 
			dbl.sqlDate(buyDate) + " and FRemaindAmount = 0 ";
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				strSql = " update " + pub.yssGetTableName("Tb_ETF_StandingBook") +
				" set FReMaindAmount = " + rs.getDouble("FMakeUpAmount") + 
				" where FNum = " + dbl.sqlString(rs.getString("FNum"));
				
				st.addBatch(strSql);
			}
			
			st.executeBatch();
		}
		catch(Exception e){
			throw new YssException("更新台帐剩余数量的数据出错！", e);
		}
		finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(st);
		}
	}
}
