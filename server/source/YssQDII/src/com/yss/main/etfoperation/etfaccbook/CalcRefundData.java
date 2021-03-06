package com.yss.main.etfoperation.etfaccbook;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import com.yss.dsub.BaseBean;
import com.yss.dsub.YssPub;
import com.yss.main.etfoperation.*;
import com.yss.main.etfoperation.pojo.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.*;

/**
 * 计算台帐应退款估值增值，并插入台帐关联表
 * @author 蒋锦
 *
 */
public class CalcRefundData extends BaseBean {
	public CalcRefundData(YssPub pub) {
		setYssPub(pub);
	}
	
	private HashMap etfParams = null;
	//private ArrayList portCodesList = null;
	//private String num = "";//记录明细项的数据编号
	//private HashMap hmValRate = null;//估值汇率
	
	/**
	 * 入口方法，传入开始日期，结束日期，组合代码
	 * @param dStartDate
	 * @param dEndDate
	 * @param sPortCodes
	 * @throws YssException
	 */
	public void createAccBookRefundMVBy(java.util.Date dStartDate,
			                            java.util.Date dEndDate,
			                            String sPortCodes) throws YssException {
		ArrayList alSubBooks = null;//明细数据
		ArrayList alSumSubBooks = null;//汇总数据
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		try{
			ETFParamSetAdmin paramAdmin = new ETFParamSetAdmin();
			paramAdmin.setYssPub(pub);
			etfParams = paramAdmin.getETFParamInfo(sPortCodes);
			
			int iDays = YssFun.dateDiff(dStartDate, dEndDate);
			java.util.Date dTheDay = dStartDate;
			for (int iRingDays = 0; iRingDays <= iDays; iRingDays++) {
				alSubBooks = calcAccBookRefundMVBy(dTheDay, sPortCodes);
				
				conn.setAutoCommit(false);
				bTrans = true;
				
				deleteSubBookData(dTheDay, sPortCodes);
				insertSubBookData(alSubBooks);
				conn.commit();
				
				alSumSubBooks = calaSumAccBookDataBy(dTheDay, sPortCodes);
				insertSubBookData(alSumSubBooks);
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
				
				// 日期累加
				dTheDay = YssFun.addDay(dTheDay, iRingDays);
			}
			
		}catch(Exception ex){
			throw new YssException("生成 ETF 台帐应退款估值增值出错！", ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * 计算台帐子表的汇总数据
	 * @param dStartDate
	 * @param dEndDate
	 * @param sPortCodes
	 * @return
	 * @throws YssException
	 */
	private ArrayList calaSumAccBookDataBy(java.util.Date dTheDay,
			                               String sPortCodes) throws YssException {
		ArrayList alSumSubBookData = new ArrayList();
		String strSql = "";
		ResultSet rs = null;	
		try{
			strSql = "SELECT a.*, b.ftradenum,b.fportcode,b.FBs,b.fstockholdercode,b.fsecuritycode,b.FMarkType " +
				" FROM (SELECT SUM(FRateProLoss) AS FRateProLoss," +
				" SUM(FSumRefund) AS FSumRefund," + 
				" m.FGradeType1," +
				" m.FBuyDate," +
				" s.FExRateDate," +
				" m.FPortCode" +
				" FROM (SELECT fbuydate,fbs,fportcode,FGradeType1,FGradeType2,case when fsecuritycode is null then ' ' else fsecuritycode end as fsecuritycode,"+
				" case when fstockholdercode is null then ' ' else fstockholdercode end as fstockholdercode,"+
				" case when ftradenum is null then ' ' else ftradenum end as ftradenum,"+
				" case when fratetype is null then ' ' else fratetype end as fratetype"+
				" FROM " + pub.yssGetTableName("TB_ETF_STANDINGBOOK") + ") m" +
				" LEFT JOIN (SELECT * FROM " + pub.yssGetTableName("Tb_ETF_SubStandingBook") +
				" ) s ON m.fbuydate = s.fbuydate  and m.fbs = s.fbs and m.fportcode = s.fportcode" +
				" and m.fsecuritycode = s.fsecuritycode and m.fstockholdercode = s.fstockholdercode and m.ftradenum = s.ftradenum"+
				" and m.fratetype = s.fratetype WHERE s.FExRateDate = " + dbl.sqlDate(dTheDay) + 
				" and m.FGradeType2 is not null GROUP BY s.FExRateDate, m.FGradeType1, m.FBuyDate, m.FPortCode) a" +
				" LEFT JOIN (SELECT * FROM " + pub.yssGetTableName("TB_ETF_STANDINGBOOK") +
				" WHERE FGradeType2 IS NULL AND FPortCode IN(" + operSql.sqlCodes(sPortCodes) + ")) b ON a.FBuyDate = b.FBuyDate" +
				" AND a.FGradeType1 = b.FGradeType1";
			rs = dbl.openResultSet(strSql);
			
			while(rs.next()){
				SubStandingBookBean subSumBook = new SubStandingBookBean();//汇总数据
				
				subSumBook.setSumRefund(rs.getDouble("FSumRefund"));
				subSumBook.setExRateDate(rs.getDate("FExRateDate"));
				subSumBook.setPortCode(rs.getString("FPortCode"));
				subSumBook.setRateProLoss(rs.getDouble("FRateProLoss"));
				subSumBook.setExchangeRate(0);
				subSumBook.setBs(rs.getString("FBS"));
				subSumBook.setBuyDate(rs.getDate("FBuyDate"));
				subSumBook.setSecurityCode(rs.getString("FSecurityCode"));
				subSumBook.setRateType(" ");
				subSumBook.setStockHolderCode(rs.getString("FStockHolderCode") == null? " " : rs.getString("FStockHolderCode"));
				subSumBook.setTradeNum(rs.getString("FTradeNum") == null? " " : rs.getString("FTradeNum"));
				subSumBook.setMarkType(rs.getString("FMarktype")!= null?rs.getString("FMarktype"):" ");
				alSumSubBookData.add(subSumBook);
			}
		}catch(Exception e){
			throw new YssException("计算应退合计汇总数据出错！", e);
		} finally{
			dbl.closeResultSetFinal(rs);
		}
		return alSumSubBookData;
	}
	
	/**
	 * 凭借取数的SQL语句
	 * @param dTheDay
	 * @param sPortCode:如果传入空字符串则不将组合代码作为查询条件
	 * @return
	 * @throws YssException
	 */
	private String createSearchSQL(java.util.Date dTheDay, 
			                       String sPortCode) throws YssException{
		//没有确定换汇汇率的台帐数据就是需要计算汇兑损益的数据
		String strSql = 
			"SELECT bk.*, sec.FTradeCury, FPreExRate, FFactExRateValue, tom.FLastSumRefund" +
			" FROM (" +
			" select make.* from (SELECT a.FNum, a.FBs, a.FPortCode, a.FSecurityCode, " +
			" CASE WHEN a.FStockHolderCode IS NULL THEN ' ' ELSE a.FStockHolderCode END AS FStockHolderCode," +
			" a.FBrokerCode, a.FSeatCode, a.FMakeUpAmount, a.FUnitCost, a.FReplaceCash, a.FCanReplaceCash, " +
			" a.FExRightDate,a.FSumAmount,a.FRealAmount,a.FTotalInterest,a.FWarrantCost,a.FBBInterest, " +
			" a.FBBWarrantCost,a.FRightRate,a.FMakeUpDate1,a.FMakeUpAmount1,a.FMakeUpUnitCost1,a.FOMakeUpCost1, " +
			" a.FHMakeUpCost1,a.FMakeUpRepCash1,a.FCanMkUpRepCash1,a.FMakeUpDate2,a.FMakeUpAmount2, " +
			" a.FMakeUpUnitCost2,a.FOMakeUpCost2,a.FHMakeUpCost2,a.FMakeUpRepCash2,a.FCanMkUpRepCash2, " +
			" a.FMakeUpDate3,a.FMakeUpAmount3,a.FMakeUpUnitCost3,a.FOMakeUpCost3,a.FHMakeUpCost3, " +
			" a.FMakeUpRepCash3,a.FCanMkUpRepCash3,a.FMakeUpDate4,a.FMakeUpAmount4,a.FMakeUpUnitCost4, " +
			" a.FOMakeUpCost4,a.FHMakeUpCost4,a.FMakeUpRepCash4,a.FCanMkUpRepCash4,a.FMakeUpDate5, " +
			" a.FMakeUpAmount5,a.FMakeUpUnitCost5,a.FOMakeUpCost5,a.FHMakeUpCost5,a.FMakeUpRepCash5, " +
			" a.FCanMkUpRepCash5,a.FMustMkUpDate,a.FMustMkUpAmount,a.FMustMkUpUnitCost,a.FOMustMkUpCost, " +
			" a.FHMustMkUpCost,a.FMustMkUpRepCash,a.FMustCMkUpRepCash,a.FRemaindAmount,a.FSumReturn, " +
			" a.FRefundDate,a.FExchangeRate,a.FOrderCode,a.FGradeType1,a.FGradeType2,a.FGradeType3, " +
			" a.FExRate1,a.FExRate2,a.FExRate3,a.FExRate4,a.FExRate5,a.FMustExRate,a.FFactExRate, " +
			" a.FExRateDate,a.FCreator,a.FCreateTime,a.FFactAmount,a.FCashBal,a.FMarkType, " +
			" CASE WHEN a.FRateType IS NULL THEN ' ' ELSE a.FRateType END AS FRateType," + 
			" a.FTradeUnitCost1,a.FFeeUnitCost1,a.FTradeUnitCost2,a.FFeeUnitCost2, " +
			" a.FTradeUnitCost3,a.FFeeUnitCost3,a.FTradeUnitCost4,a.FFeeUnitCost4,a.FTradeUnitCost5, " +
			" a.FFeeUnitCost5,a.FMustTradeUnitCost,a.FMustFeeUnitCost," +
			" CASE WHEN a.FTradeNum IS NULL THEN ' ' ELSE a.FTradeNum END AS FTradeNum," +
			" CASE WHEN a.Fbs = 'S' AND b.Fsupplymode = '5' AND a.FRATETYPE = 'T+1' THEN 'B' " +
			//当补票类型为 6 的情况（博时基金），使用第一次补票日期来关联汇率。
			//添加了 FFaceBuyDate 插入台账子表的时候都使用实际的申赎日期
			" ELSE a.FBS END AS FRBS, CASE WHEN b.Fsupplymode = '6' THEN a.Fmakeupdate1 " +
			" ELSE a.Fbuydate END AS FBuydate, FBuyDate AS FFaceBuyDate FROM " + pub.yssGetTableName("TB_ETF_STANDINGBOOK") + " a " +
			" LEFT JOIN " + pub.yssGetTableName("TB_ETF_PARAM") + " b ON a.FPortCode = b.FPortCOde) make " +
			" WHERE make.FGradeType2 IS NOT NULL AND make.FBuyDate <= " + dbl.sqlDate(dTheDay) +  
			(sPortCode.trim().length() > 0? (" AND make.FPortCode = " + dbl.sqlString(sPortCode)) : "") + 
			") bk" +
			" LEFT JOIN (SELECT * FROM " + pub.yssGetTableName("Tb_ETF_BookExRateData") +
			" WHERE FEXRATEDATE < " + dbl.sqlDate(dTheDay) + 
			" ) rat ON bk.FPortCode = rat.FPortCode AND bk.FRBs = rat.FBookType AND bk.FBuyDate = rat.FBuyDate" +
			" LEFT JOIN (SELECT FSecurityCode, FTradeCury FROM " + pub.yssGetTableName("Tb_Para_Security") +
			" WHERE FCheckState = 1) sec ON bk.FSecurityCode = sec.FSecurityCode" +
			" LEFT JOIN (SELECT FPortCode," +
			" Round(FBaseRate / FPortRate, 15) AS FPreExRate," +
			" FCuryCode" +
			" FROM " + pub.yssGetTableName("Tb_Data_Pretvalrate") +
			" WHERE FValDate = " + dbl.sqlDate(dTheDay) +
			" ) pre ON bk.FPortCode = pre.FPortcode AND sec.FTradeCury = pre.FCuryCode" +
			" LEFT JOIN (SELECT FPortCode, FBuyDate, FExRateValue AS FFactExRateValue, FBookType" +
			" FROM " + pub.yssGetTableName("TB_ETF_BOOKEXRATEDATA") +
			" WHERE FCheckState = 1" +
			" AND FExRateDate = " + dbl.sqlDate(dTheDay) +
			" ) face ON face.FPortCode = bk.FPortCode AND face.FBuyDate = bk.FBuyDate AND face.FBookType = bk.FRBs " +
			" LEFT JOIN (SELECT sa.FRateType, sa.fsumrefund AS FLastSumRefund, sa.FPortCode, sa.FBuyDate, sa.FBs, sa.FSecurityCode, sa.FStockHolderCode, sa.FTradeNum" +
			" FROM " + pub.yssGetTableName("Tb_Etf_Substandingbook") + " sa" +
			" LEFT JOIN (SELECT FPortCode," +
			" MAX(FExRateDate) AS FExRateDate," +
			" FBuyDate, FBs, FSecurityCode, FStockHolderCode, FTradeNum, FRateType" +
			" FROM " + pub.yssGetTableName("Tb_Etf_Substandingbook") +
			" WHERE FExRateDate < " + dbl.sqlDate(dTheDay) +
			" GROUP BY FPortCode, FBuyDate, FBs, FSecurityCode, FStockHolderCode, FTradeNum, FRateType) sb" +
			" ON sa.FPortCode = sb.FPortCode AND sa.FBuyDate = sb.FBuyDate AND sa.FBs = sb.FBs AND sa.FSecurityCode = sb.FSecurityCode AND sa.FStockHolderCode = sb.FStockHolderCode AND sa.FTradeNum = sb.FTradeNum AND sa.FRateType = sb.FRateType" + 
			" WHERE sa.fexratedate = sb.FExRateDate) tom ON tom.FPortCode = bk.FPortCode AND tom.FBuyDate = bk.FFaceBuyDate AND tom.FBs = bk.FBs AND tom.FSecurityCode = bk.FSecurityCode AND tom.FStockHolderCode = bk.FStockHolderCode AND tom.FTradeNum = bk.FTradeNum AND tom.FRateType = bk.FRateType" +
			" AND bk.FPortCode = tom.FPortCode" +
			" WHERE rat.FPortCode IS NULL" +
			" Order BY FOrderCode DESC";
		
		return strSql;
	}

	
	/**
	 * 计算台帐生成日期范围内的台帐数据的汇兑损益和应退合计
	 * @param dStartDate
	 * @param dEndDate
	 * @param sPortCodes
	 * @return
	 * @throws YssException
	 */
	private ArrayList calcAccBookRefundMVBy(java.util.Date dTheDay,
            								String sPortCodes) throws YssException {
		ArrayList alSubBookData = new ArrayList();
		String strSql = "";
		ResultSet rs = null;
		BaseOperDeal operDeal = new BaseOperDeal();
        operDeal.setYssPub(pub);
		try{			
			
			strSql = createSearchSQL(dTheDay, "");
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				String sTradeCury = rs.getString("FTradeCury");
				SubStandingBookBean subBook = new SubStandingBookBean();//明细数据
				
				ETFParamSetBean paramSet = 
					(ETFParamSetBean)etfParams.get(rs.getString("FPortCode"));
				
				//如果当天是节假日，不再进行计算    取国内节假日群
				if(!dTheDay.equals(operDeal.getWorkDay(paramSet.getSHolidayCode(), dTheDay, 0))){
					continue;
				}
								
									
				BigDecimal bigRate = null;
				if(rs.getString("FFactExRateValue") != null){
					bigRate = rs.getBigDecimal("FFactExRateValue");
				} else if(rs.getString("FPreExRate") != null){
					bigRate = rs.getBigDecimal("FPreExRate");
				} else {
					throw new YssException("币种“" + sTradeCury + "”没有汇率,请检查汇率资料并重新估值！");
				}
				
				if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_SUB)){ //轧差处理方式
					//计算T日的应退合计
					if(dTheDay.equals(rs.getDate("FBuyDate"))){
						calcTDaySumRefund(rs, subBook, dTheDay);
					}else{//非T日的应退合计
						calcSumRefundBy(rs, subBook, paramSet, bigRate, dTheDay);
					}
				} else if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMESUB)){ //实时加轧差的处理方式
					if(dTheDay.equals(rs.getDate("FBuyDate"))){
						continue;
					}
					//计算首日的应退合计
					if(dTheDay.equals(
							operDeal.getWorkDay(
									(String)paramSet.getHoildaysRela().
										get(YssOperCons.YSS_ETF_OVERTYPE_DEALDAYNUM), 
											rs.getDate("FBuyDate"), paramSet.getDealDayNum()))){
						
						caleFirstDaySumRefundByTime(rs, subBook, bigRate, dTheDay);
					} else {
						calcSumRefundByTime(rs, subBook, paramSet, bigRate, dTheDay);
					}
				} else if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_MUST)){//轧差补票_强退的处理方式
					//计算T日的应退合计
					if(dTheDay.equals(rs.getDate("FBuyDate"))){
						calcTDaySumRefund(rs, subBook, dTheDay);
					}else{//非T日的应退合计
						calcSumRefundBy(rs, subBook, paramSet, bigRate, dTheDay);
					}
				}
				alSubBookData.add(subBook);
			}
		} catch(Exception ex){
			throw new YssException(ex.getMessage(), ex);
		} finally{
			dbl.closeResultSetFinal(rs);
		}
		return alSubBookData;
	}
	
	/**
	 * 计算业务日的赎回的应退合计
	 * @param rs
	 * @param bigRate
	 * @return
	 * @throws YssException
	 */
	private double caleSHTheDaySumRefund(ResultSet rs,
			                             BigDecimal bigRate) throws YssException{
		double dbSumRefund = 0;//应退合计
		double dbBuPiao = 0;//补票和强制处理的应退合计
		double dbFenHong = 0;//分红的应退合计
		double dbQuanzheng = 0;//权证的应退合计
		
		//实际应退合计 = 
		//总派息*第二次卖出数量/替代数量*业务日汇率+
		//总派息*强制处理数量/替代数量*业务日汇率+
		try{
			dbBuPiao = 
				YssD.add(
						YssD.round(
								YssD.mul(
										YssD.mul(
												rs.getDouble("FOMakeUpCost1"), 
												-1),
										Double.parseDouble(bigRate.toString())), 
								2), 
						YssD.round(
								YssD.mul(
										YssD.mul(
												rs.getDouble("FOMakeUpCost2"),
												-1),
										Double.parseDouble(bigRate.toString())),
								2),
						YssD.round(
								YssD.mul(
										YssD.mul(
												rs.getDouble("FOMustMkUpCost"), 
												-1), 
										Double.parseDouble(bigRate.toString())), 
								2));
			
			dbFenHong =
				YssD.add(
						YssD.round(
								YssD.mul(
										YssD.mul(
												rs.getDouble("FTotalInterest"), 
												-1), 
										YssD.div(//第二次补票数量和替代数量都有方向所以就不需要再乘一个方向了
												rs.getDouble("FMakeUpAmount2"), 
												rs.getDouble("FMakeUpAmount")), 
										Double.parseDouble(bigRate.toString())), 
								2), 
						YssD.round(
								YssD.mul(
										YssD.mul(
												rs.getDouble("FTotalInterest"), 
												-1), 
										YssD.div(//强制处理数量和替代数量都有方向所以就不需要再乘一个方向了
												rs.getDouble("FMustMkUpAmount"), 
												rs.getDouble("FMakeUpAmount")), 
										Double.parseDouble(bigRate.toString())), 
								2));
			dbQuanzheng = 
				YssD.add(
						YssD.round(
								YssD.mul(
										YssD.mul(
												rs.getDouble("FWarrantCost"), 
												-1), 
										YssD.div(//第二次补票数量和替代数量都有方向所以就不需要再乘一个方向了
												rs.getDouble("FMakeUpAmount2"), 
												rs.getDouble("FMakeUpAmount")), 
										Double.parseDouble(bigRate.toString())), 
								2), 
						YssD.round(
								YssD.mul(
										YssD.mul(
												rs.getDouble("FWarrantCost"), 
												-1), 
										YssD.div(//强制处理数量和替代数量都有方向所以就不需要再乘一个方向了
												rs.getDouble("FMustMkUpAmount"), 
												rs.getDouble("FMakeUpAmount")), 
										Double.parseDouble(bigRate.toString())), 
								2));
			dbSumRefund = YssD.add(dbBuPiao, dbFenHong, dbQuanzheng);
		} catch(Exception ex){
			throw new YssException(ex.getMessage(), ex);
		}
		return dbSumRefund;
	}
	
	/**
	 * 计算业务日申购的应退合计
	 * 分别计算权益、第一次补票、第二次补票和强制处理的应退款然后累加
	 * @param rs
	 * @param bigRate
	 * @return
	 * @throws YssException
	 */
	private double caleSGTheDaySumRefund(ResultSet rs, 
            							 BigDecimal bigRate) throws YssException{
		double dbSumRefund = 0;//应退合计
		double dbMarkup1 = 0;//第一次补票的应退款
		double dbMarkup2 = 0;//第二次补票的应退款
		double dbMustup = 0;//强制处理的应退款
		
		//实际应退合计 = 
		//替代金额本币 *（第一次补票数量/替代数量）- 补票总成本 * 业务日汇率 + 
		//（替代金额 - 总派息 * 业务日汇率 - 权证价值 * 业务日汇率）* （第二次补票数量 /（替代数量 + 权益总数量）） - 第二次补票总成本 * 业务日汇率 +
		//（替代金额 - 总派息 * 业务日汇率 - 权证价值 * 业务日汇率）* （强制处理数量 /（替代数量 + 权益总数量）） - 强制处理数量 * 业务日汇率 +
		//第一次卖出总成本原币*业务日汇率 + 第二次买出总成本原币*业务日汇率+强制处理总成本原币*业务日汇率
		try{
			
			dbMarkup1 = 
					YssD.sub(
					YssD.round(
							YssD.mul(
									rs.getDouble("FReplaceCash"), 
									YssD.div(
											rs.getDouble("FMakeUpAmount1"), 
											rs.getDouble("FMakeUpAmount"))),
							2),
					YssD.round(
							YssD.mul(
									 rs.getDouble("FOMakeUpCost1"), 
									 Double.parseDouble(bigRate.toString())),
							2));
			dbMarkup2 = 
					YssD.sub(
							YssD.round(
									YssD.mul(
										YssD.sub(
											rs.getDouble("FReplaceCash"),
											YssD.round(
													YssD.mul(
															rs.getDouble("FTotalInterest"), 
															Double.parseDouble(bigRate.toString())), 
													2),
											YssD.round(		
													YssD.mul(
															rs.getDouble("FWarrantCost"),
															Double.parseDouble(bigRate.toString())), 
													2)),
										YssD.div(
												rs.getDouble("FMakeUpAmount2"), 
												YssD.add(
														rs.getDouble("FMakeUpAmount"), 
														rs.getDouble("FSumAmount")))), 
									2),
							YssD.round(
									YssD.mul(
											rs.getDouble("FOMakeUpCost2"), 
											Double.parseDouble(bigRate.toString())),
									2));
			dbMustup = 
					YssD.sub(
							YssD.round(
									YssD.mul(
										YssD.sub(
											rs.getDouble("FReplaceCash"),
											YssD.round(
													YssD.mul(
															rs.getDouble("FTotalInterest"), 
															Double.parseDouble(bigRate.toString())), 
														2),
												YssD.round(		
														YssD.mul(
																rs.getDouble("FWarrantCost"),
																Double.parseDouble(bigRate.toString())), 
														2)),
											YssD.div(
													rs.getDouble("FMustMkUpAmount"), 
													YssD.add(
															rs.getDouble("FMakeUpAmount"), 
															rs.getDouble("FSumAmount")))), 
										2),
								YssD.round(		
										YssD.mul(
												rs.getDouble("FOMustMkUpCost"), 
												Double.parseDouble(bigRate.toString())), 
										2));
			
			dbSumRefund = YssD.add(dbMarkup1, dbMarkup2, dbMustup);
		} catch (Exception ex) {
			throw new YssException(ex.getMessage(), ex);
		}
		return dbSumRefund;
	}
	
	/**
	 * 计算非首日的汇兑损益和应退合计，在华宝就是T+1日以后
	 * @param rs
	 * @param subBook
	 * @param paramSet
	 * @param bigRate
	 * @param dTheDay
	 * @throws YssException
	 */
	private void calcSumRefundByTime(ResultSet rs, 
									 SubStandingBookBean subBook,
			                         ETFParamSetBean paramSet, 
			                         BigDecimal bigRate, 
			                         java.util.Date dTheDay) throws YssException {
		double dbSumRefund = 0;//应退合计
		double dbRateProLoss = 0;//汇兑损益
		try {
			//计算应退合计
			if(rs.getString("FBs").equalsIgnoreCase("B")){//申购
				dbSumRefund = caleSGTheDaySumRefund(rs, bigRate);
			} else {//赎回
				dbSumRefund = caleSHTheDaySumRefund(rs, bigRate);			
			}
			
			//计算汇兑损益
			dbRateProLoss = YssD.sub(dbSumRefund, rs.getDouble("FLastSumRefund"));
			
			subBook.setExchangeRate(Double.parseDouble(bigRate.toString()));
			subBook.setExRateDate(dTheDay);
			subBook.setPortCode(rs.getString("FPortCode"));
			subBook.setRateProLoss(dbRateProLoss);
			subBook.setSumRefund(dbSumRefund);
			subBook.setBs(rs.getString("FBS"));
			subBook.setBuyDate(rs.getDate("FFaceBuyDate"));
			subBook.setSecurityCode(rs.getString("FSecurityCode"));
			subBook.setRateType(rs.getString("FRateType") == null? " " : rs.getString("FRateType"));
			subBook.setStockHolderCode(rs.getString("FStockHolderCode") == null? " " : rs.getString("FStockHolderCode"));
			subBook.setTradeNum(rs.getString("FTradeNum") == null?" " : rs.getString("FTradeNum"));
			subBook.setMarkType(rs.getString("FMarktype")!= null?rs.getString("FMarktype"):" ");
		} catch (Exception ex) {
			throw new YssException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * 计算首日的汇兑损益和应退合计，
	 * 在华宝首日就是 T+1 日
	 * @param rs
	 * @param subBook
	 * @param dTheDay
	 * @throws YssException
	 */
	private void caleFirstDaySumRefundByTime(ResultSet rs, 
                                             SubStandingBookBean subBook, 
                                             BigDecimal bigRate,
                                             java.util.Date dTheDay) throws YssException{
		double dbSumRefund = 0;//应退合计
		double dbRateProLoss = 0;//汇兑损益
		double dbMarkup1 = 0;//第一次补票的应退款
		double dbMarkup2 = 0;//第二次补票的应退款
		double dbMustup = 0;//强制处理的应退款
		
		double dbBuPiao = 0;//补票和强制处理的应退合计
		double dbFenHong = 0;//分红的应退合计
		double dbQuanzheng = 0;//权证的应退合计
		try{
			if(rs.getString("FBs").equalsIgnoreCase("B")){//申购
				//业务日应退合计				
				dbSumRefund = caleSGTheDaySumRefund(rs, bigRate);
				
				dbMarkup1 = 
					YssD.sub(
							YssD.round(
									YssD.mul(
											rs.getDouble("FReplaceCash"), 
											YssD.div(
													rs.getDouble("FMakeUpAmount1"), 
													rs.getDouble("FMakeUpAmount"))),
									2),
							 YssD.round(
									 YssD.mul(
											 rs.getDouble("FOMakeUpCost1"), 
											 Double.parseDouble(rs.getBigDecimal("FExRate1").toString())), 
									 2));
				dbMarkup2 = 
					YssD.sub(
							YssD.round(
									YssD.mul(
										YssD.sub(
											rs.getDouble("FReplaceCash"),
											YssD.round(
													YssD.mul(
															rs.getDouble("FTotalInterest"), 
															Double.parseDouble(rs.getBigDecimal("FRightRate").toString())), 
													2),
											YssD.round(		
													YssD.mul(
															rs.getDouble("FWarrantCost"),
															Double.parseDouble(rs.getBigDecimal("FRightRate").toString())), 
													2)),
										YssD.div(
												rs.getDouble("FMakeUpAmount2"), 
												YssD.add(
														rs.getDouble("FMakeUpAmount"), 
														rs.getDouble("FSumAmount")))), 
									2),
							YssD.round(
									YssD.mul(
											rs.getDouble("FOMakeUpCost2"), 
											Double.parseDouble(rs.getBigDecimal("FExRate2").toString())), 
									2));
				dbMustup = 
					YssD.sub(
							YssD.round(
									YssD.mul(
										YssD.sub(
											rs.getDouble("FReplaceCash"),
											YssD.round(
													YssD.mul(
															rs.getDouble("FTotalInterest"), 
															Double.parseDouble(rs.getBigDecimal("FRightRate").toString())), 
													2),
											YssD.round(		
													YssD.mul(
															rs.getDouble("FWarrantCost"),
															Double.parseDouble(rs.getBigDecimal("FRightRate").toString())), 
													2)),
										YssD.div(
												rs.getDouble("FMustMkUpAmount"), 
												YssD.add(
														rs.getDouble("FMakeUpAmount"), 
														rs.getDouble("FSumAmount")))), 
									2),
							YssD.round(
									YssD.mul(
											rs.getDouble("FOMustMkUpCost"), 
											Double.parseDouble(rs.getBigDecimal("FMustExRate").toString())), 
									2));
				
				dbRateProLoss = 
					YssD.sub(dbSumRefund, dbMarkup1, dbMarkup2, dbMustup);
			}else{//赎回
				dbSumRefund = caleSHTheDaySumRefund(rs, bigRate);
				
				dbBuPiao = 
					YssD.add(
							YssD.round(
									YssD.mul(rs.getDouble("FHMakeUpCost1"), 
													-1), 
									2), 
							YssD.round(
									YssD.mul(rs.getDouble("FHMakeUpCost2"),
													-1),
									2),
							YssD.round(
									YssD.mul(rs.getDouble("FHMustMkUpCost"), 
													-1), 
									2));
				
				dbFenHong =
					YssD.add(
							YssD.round(
									YssD.mul(
											YssD.mul(
													rs.getDouble("FTotalInterest"), 
													-1), 
											YssD.div(//第二次补票数量和替代数量都有方向所以就不需要再乘一个方向了
													rs.getDouble("FMakeUpAmount2"), 
													rs.getDouble("FMakeUpAmount")), 
											Double.parseDouble(rs.getBigDecimal("FRightRate").toString())), 
									2), 
							YssD.round(
									YssD.mul(
											YssD.mul(
													rs.getDouble("FTotalInterest"), 
													-1), 
											YssD.div(//强制处理数量和替代数量都有方向所以就不需要再乘一个方向了
													rs.getDouble("FMustMkUpAmount"), 
													rs.getDouble("FMakeUpAmount")), 
											Double.parseDouble(rs.getBigDecimal("FRightRate").toString())), 
									2));
				dbQuanzheng = 
					YssD.add(
							YssD.round(
									YssD.mul(
											YssD.mul(
													rs.getDouble("FWarrantCost"), 
													-1), 
											YssD.div(//第二次补票数量和替代数量都有方向所以就不需要再乘一个方向了
													rs.getDouble("FMakeUpAmount2"), 
													rs.getDouble("FMakeUpAmount")), 
											Double.parseDouble(rs.getBigDecimal("FRightRate").toString())), 
									2), 
							YssD.round(
									YssD.mul(
											YssD.mul(
													rs.getDouble("FWarrantCost"), 
													-1), 
											YssD.div(//强制处理数量和替代数量都有方向所以就不需要再乘一个方向了
													rs.getDouble("FMustMkUpAmount"), 
													rs.getDouble("FMakeUpAmount")), 
											Double.parseDouble(rs.getBigDecimal("FRightRate").toString())), 
									2));
				
				dbRateProLoss = 
					YssD.sub(dbSumRefund, dbBuPiao, dbFenHong, dbQuanzheng);
			}
			subBook.setExchangeRate(Double.parseDouble(bigRate.toString()));
			subBook.setExRateDate(dTheDay);
			subBook.setPortCode(rs.getString("FPortCode"));
			subBook.setRateProLoss(dbRateProLoss);
			subBook.setSumRefund(dbSumRefund);
			subBook.setBs(rs.getString("FBS"));
			subBook.setBuyDate(rs.getDate("FFaceBuyDate"));
			subBook.setSecurityCode(rs.getString("FSecurityCode"));
			subBook.setRateType(rs.getString("FRateType") == null? " " : rs.getString("FRateType"));
			subBook.setStockHolderCode(rs.getString("FStockHolderCode") == null? " " : rs.getString("FStockHolderCode"));
			subBook.setTradeNum(rs.getString("FTradeNum") == null?" " : rs.getString("FTradeNum"));
			subBook.setMarkType(rs.getString("FMarktype")!= null?rs.getString("FMarktype"):" ");
		}catch(Exception ex){
			throw new YssException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * 计算非T日的应退合计和汇兑损益 
	 * 嘉实的计算方式  强制处理不计算汇兑损溢
	 * @param rs
	 * @param subBook
	 * @param paramSet
	 * @param bigRate
	 * @param dTheDay
	 * @throws YssException
	 */
	private void calcSumRefundBy(ResultSet rs, 
			                     SubStandingBookBean subBook,
			                     ETFParamSetBean paramSet,
			                     BigDecimal bigRate,
			                     java.util.Date dTheDay) throws YssException{
		double dbSumMakeCost = 0;
		String sMakeDateField = "FMakeUpDate";//补票日期字段名称
		//String sOMakeUpCostField = "FOMakeUpCost";//补票总成本字段名称(原币)
		//String sExRateField = "FExRate";//汇率字段名称
		String sMakeUpAmountField = "FMakeUpAmount";//补票数量字段
		String sMakeUpUnitCost = "FMakeUpUnitCost";//补票单位成本
		int iInout = 1;
		try{
			int iMakeNum = //补票次数
				paramSet.getDealDayNum() - paramSet.getBeginSupply() + 1;
			
			if(rs.getString("FBs").equalsIgnoreCase("S")){
				iInout = -1;
			}
			
			// 循环补票次数
			for (int i = 1; i <= iMakeNum; i++) {
				// 如果补票日期为空，就认为这次补票不存在，
				// 而且业务日期必须大于补票日期
				if (rs.getDate(sMakeDateField + i) == null ||
						!rs.getDate(sMakeDateField + i).before(dTheDay)) {
					continue;
				}
				
				
				//补票总成本本币 = 补票数量 * 补票单位成本（原币）* 业务日汇率 = 
				//               补票总成本（原币）* 业务日汇率
				dbSumMakeCost += 
					YssD.round(
							YssD.mul(
									 YssD.mulD(
											 rs.getDouble(sMakeUpAmountField + i) * iInout, 
									         rs.getDouble(sMakeUpUnitCost + i)),
									 bigRate),
							 2);
			}
			
			if(rs.getString("FBS").equalsIgnoreCase("B")){// 申购
				subBook.setSumRefund(
						YssD.sub(
								rs.getDouble("FReplaceCash"), 
								dbSumMakeCost, 
								rs.getDouble("FHMustMkUpCost")));
				
			} else {
				subBook.setSumRefund(
						YssD.add(
								dbSumMakeCost, 
								rs.getDouble("FHMustMkUpCost") * iInout));
			}
			
			subBook.setRateProLoss(
					YssD.sub(
							subBook.getSumRefund(), 
							rs.getDouble("FLastSumRefund")));
			
			subBook.setExchangeRate(Double.parseDouble(bigRate.toString()));
			subBook.setExRateDate(dTheDay);
			subBook.setPortCode(rs.getString("FPortCode"));
			subBook.setBs(rs.getString("FBS"));
			subBook.setBuyDate(rs.getDate("FFaceBuyDate"));
			subBook.setSecurityCode(rs.getString("FSecurityCode"));
			subBook.setRateType(rs.getString("FRateType") == null? " " : rs.getString("FRateType"));
			subBook.setStockHolderCode(rs.getString("FStockHolderCode") == null? " " : rs.getString("FStockHolderCode"));
			subBook.setTradeNum(rs.getString("FTradeNum") == null?" " : rs.getString("FTradeNum"));
			subBook.setMarkType(rs.getString("FMarktype")!= null?rs.getString("FMarktype"):" ");
		}catch(Exception ex){
			throw new YssException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * 计算 T 日应退合计 嘉实的计算方式  从 T 日起就开始计算应退合计
	 * @param rs
	 * @param subBook
	 * @throws YssException
	 */
	private void calcTDaySumRefund(ResultSet rs, 
			                       SubStandingBookBean subBook, 
			                       java.util.Date dTheDay) throws YssException{
		double dbSumRefund = 0;
		try{
			if(rs.getString("FBS").equalsIgnoreCase("B")){// 申购
				//申购T日 应退合计 = 
				//          替代金额（本币） - 第一次补票单位成本 * 第一次补票数量 * 补票汇率 - 强制处理成本（本币） =
				//          替代金额（本币） - 第一次补票成本（本币） - 强制处理成本（本币）
				dbSumRefund = YssD.sub(
						               rs.getDouble("FReplaceCash"), 
						               YssD.round(YssD.mul(
						            		          rs.getDouble("FMakeUpUnitCost1"), 
						            		          rs.getDouble("FMakeUpAmount1"),
						            		          rs.getDouble("FExRate1")), 
						                          2),
						               rs.getDouble("FHMustMkUpCost"));
			} else {//赎回
				//赎回T日应退合计 = 第一次补票单位成本 * 第一次补票数量 * 补票汇率 + 强制处理成本（本币）=
				//                第一次补票成本（本币） + 强制处理成本（本币）
				dbSumRefund = YssD.add(
						               YssD.round(YssD.mul(
						            		          rs.getDouble("FMakeUpUnitCost1"), 
						            		          rs.getDouble("FMakeUpAmount1") * -1,
						            		          rs.getDouble("FExRate1")), 
						                          2),
						               rs.getDouble("FHMustMkUpCost") * -1);
			}
			subBook.setBs(rs.getString("FBS"));
			subBook.setRateType(rs.getString("FRateType") == null? " " : rs.getString("FRateType"));
			subBook.setStockHolderCode(rs.getString("FStockHolderCode") == null? " " : rs.getString("FStockHolderCode"));
			subBook.setTradeNum(rs.getString("FTradeNum") == null?" " : rs.getString("FTradeNum"));
			subBook.setBuyDate(rs.getDate("FFaceBuyDate"));
			subBook.setSecurityCode(rs.getString("FSecurityCode"));
			subBook.setExchangeRate(rs.getDouble("FExRate1"));
			subBook.setExRateDate(dTheDay);
			subBook.setPortCode(rs.getString("FPortCode"));
			subBook.setMarkType(rs.getString("FMarktype")!= null?rs.getString("FMarktype"):" ");
			subBook.setRateProLoss(0);
			subBook.setSumRefund(dbSumRefund);
		}catch(Exception ex){
			throw new YssException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * 删除台帐子表数据
	 * @param dStartDate
	 * @param dEndDate
	 * @param sPortCodes
	 * @throws YssException
	 */
	private void deleteSubBookData(java.util.Date dTheDay,
                                   String sPortCodes) throws YssException{
		String strSql = "";
		try{
			strSql = "DELETE FROM " + pub.yssGetTableName("Tb_ETF_SubStandingBook") +
				" WHERE FExRateDate = " + dbl.sqlDate(dTheDay) + 
				" AND FPortCode IN(" + operSql.sqlCodes(sPortCodes) + ")";
			dbl.executeSql(strSql);
		}catch(Exception ex){
			throw new YssException("删除台帐子表数据出错！", ex);
		}
	}
	
	/**
	 * 插入台帐子表
	 * @param dStartDate
	 * @param dEndDate
	 * @param sPortCodes
	 * @param alSubBooks
	 * @throws YssException
	 */
	private void insertSubBookData(ArrayList alSubBooks) throws YssException{
		String strSql = "";
		PreparedStatement pst = null;
		try{	
			strSql = "INSERT INTO " + pub.yssGetTableName("Tb_ETF_SubStandingBook") +
				"(FBuyDate, FBs, FSecurityCode, FStockHolderCode, FTradeNum, FExRateDate, FPortCode, FRateType, FExRateValue, FRateProLoss, FSumRefund, FCreator, FCreateTime,FMarkType)" +
				"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			pst = dbl.openPreparedStatement(strSql);
			for(int i = 0; i < alSubBooks.size(); i++){
				SubStandingBookBean subBook = (SubStandingBookBean)alSubBooks.get(i);
				
				pst.setDate(1, YssFun.toSqlDate(subBook.getBuyDate()));
				pst.setString(2, subBook.getBs());
				pst.setString(3, subBook.getSecurityCode());
				pst.setString(4, subBook.getStockHolderCode());
				pst.setString(5, subBook.getTradeNum());
				pst.setDate(6, YssFun.toSqlDate(subBook.getExRateDate()));
				pst.setString(7, subBook.getPortCode());
				pst.setString(8, subBook.getRateType());
				pst.setDouble(9, subBook.getExchangeRate());
				pst.setDouble(10, subBook.getRateProLoss());
				pst.setDouble(11, subBook.getSumRefund());
				pst.setString(12, pub.getUserCode());
				pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
				pst.setString(14, subBook.getMarkType()!= null?subBook.getMarkType():" ");
				pst.addBatch();
				//System.out.println(i);
				//pst.executeUpdate();
				
			}	
			
			pst.executeBatch();
			
		} catch(Exception e){
			throw new YssException("将台帐子表数据插入数据库出错！", e);
		} finally{
			dbl.closeStatementFinal(pst);
		}
	}
	
	/**
	 * 获取估值汇率表中台帐生成日期范围内的所有币种的汇率，返回哈希表
	 * @param dStartDate
	 * @param dEndDate
	 * @param sPortCodes
	 * @return
	 * @throws YssException
	 */
//	private HashMap getValRateBy(java.util.Date dStartDate,
//								 java.util.Date dEndDate, 
//			                     String sPortCodes) throws YssException{
//		HashMap hmValRate = new HashMap();
//		String strSql = "";
//		ResultSet rs = null;
//		//int iDefer = 0;
//		try{
//			for(int i = 0; i < portCodesList.size(); i++){
//				String sPortCode = (String)portCodesList.get(i);
////				ETFParamSetBean paramSet = 
////					(ETFParamSetBean)etfParams.get(sPortCode);
//				
//				//iDefer = getDeferDays(paramSet);
//				
//				strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Data_PretValRate") +
//				" WHERE FValDate BETWEEN " + dbl.sqlDate(dStartDate) +
//				" AND " + dbl.sqlDate(dEndDate) +
//				" AND FPortCode = " + dbl.sqlString(sPortCode);
//				
//				rs = dbl.openResultSet(strSql);
//				while(rs.next()){
//					String sKey = sPortCode + "\t" + 
//						YssFun.formatDate(rs.getString("FValDate"), "yyyy-MM-dd") + "\t" +
//							rs.getString("FCuryCode");
//					hmValRate.put(
//							sKey,
//							YssD.divD(
//									rs.getDouble("FBaseRate"), 
//									rs.getDouble("FPortRate")));
//				}
//			}
//			
//		}catch(Exception ex){
//			throw new YssException ("获取估值汇率出错！", ex);
//		}finally{
//			dbl.closeResultSetFinal(rs);
//		}
//		return hmValRate;
//	}
	
//	private int getDeferDays(ETFParamSetBean paramSet){
//		//取申购延迟天数和赎回延迟天数中更长的一个作为 SQL 语句查询汇率的延迟天数，
//		//取保获取到所有天数的汇率
//		return
//			paramSet.getISGDealReplace() >= paramSet.getISHDealReplace()? 
//					paramSet.getISGDealReplace():paramSet.getISHDealReplace();
//	}
	
}
