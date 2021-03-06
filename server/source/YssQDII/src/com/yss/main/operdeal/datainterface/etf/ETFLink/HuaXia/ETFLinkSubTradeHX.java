/**@author shashijie
*  @version 创建时间：2012-07-13 下午05:07:55 STORY 2727
*  类说明 华夏和易方达原本是公用一个类的,3D一起测试后发现华夏不能以券商区分SUM()所以新建此类不在和易方达公用一个类
*/
package com.yss.main.operdeal.datainterface.etf.ETFLink.HuaXia;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.yss.main.operdata.MarketValueBean;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.parasetting.MTVMethodBean;
import com.yss.pojo.cache.YssCost;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class ETFLinkSubTradeHX extends DataBase{

	private String FNum = "";//交易编号(流水号)
	private long Max = 0;//递增流水号
	
	/**shashijie 2012-07-13 STORY 2727 华夏ETF联接基金 */
	//申购业务的可收退补款 = ∑（T日股票篮中可以现金替代股票的申购替代金额 - 股票T日收盘价 * 股票数量）* 篮子数;
	private double SGCanReturnMoney = 0;
	//赎回业务的可收赎回款 = ∑（T日股票篮中可以现金替代股票的T日收盘价 * 股票篮中的个股数量）* 篮子数;
	private double SHCanReturnMoney = 0;
	//申购现金替代	=  ∑（T日股票篮现金替代金额） * 篮子数
	private double SGCashAlternat = 0;
	//赎回现金替代款  = ∑（T日股票篮中必须现金替代股票的赎回替代金额） * 篮子数；
	private double SHCashAlternat = 0;
	/**end*/
	
	/**shashijie 2012-07-13 STORY 2727 程序入口 */
	public void inertData() throws YssException {
		/**shashijie 2012-07-13 STORY 2727 华夏*/
		Date bargainDate = getBargainDate();
		//计算申赎可退替代款
		//doOperionHuaXiaCanReturnMoney(this.sDate,this.sPort, bargainDate);
		//计算申赎现金替代
		doOperionHuaXiaCashAlternat(this.sDate,this.sPort, bargainDate);
		/**end*/
		
		//add by songjie 2012.06.27 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
		insertETFLinkBook(bargainDate);//将JSMX中间表的数据处理到ETF连接台账表
		
		//获取今日申请数据集合
		List subBeanList = getTradeSubBeanList(this.sDate,this.sPort);
		
		//插入交易数据子表
		insertSubBeanList(subBeanList,this.sDate,this.sPort);
    }

	/**shashijie 2012-07-13 STORY 2727 华夏*/
	private void doOperionHuaXiaCashAlternat(Date dDate, String fPort, Date  bargainDate) throws YssException {
		ResultSet rs = null;
		double SG = 0;//申购
		double SH = 0;//赎回
		try {
			String query = getSSCashAltSql(bargainDate,fPort);
			rs = dbl.openResultSet(query);
			//这里没有篮子数,在后面乘以篮子数
			while (rs.next()) {
				//申购现金替代	=  ∑（T日股票篮现金替代金额） * 篮子数
				SG = rs.getDouble("SGFtotalmoney");
				//赎回现金替代款  = ∑（T日股票篮中必须现金替代股票的赎回替代金额） * 篮子数；
				SH = rs.getDouble("SHFtotalmoney");
			}
			SGCashAlternat = YssD.round(SG,2);
			SHCashAlternat = YssD.round(SH,2);
		} catch (Exception e) {
			throw new YssException("华夏计算申赎可退替代款出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2012-07-13 STORY 2727 华夏*/
	private String getSSCashAltSql(Date dDate, String fPort) {
		String query = "Select a.Fportcode," +
				" a.Fdate," +
				" a.Ftotalmoney As Sgftotalmoney," +
				" b.Ftotalmoney As Shftotalmoney" +
				" From (Select A1.Fportcode, A1.Fdate, Sum(A1.Ftotalmoney) Ftotalmoney" +
				" From "+pub.yssGetTableName("Tb_Etf_Stocklist")+" A1"+
				" Where A1.Fportcode = "+dbl.sqlString(fPort)+
				" And A1.Fdate = "+dbl.sqlDate(dDate)+
				" And A1.Flistedmarket <> 'XSHE'" +
				" Group By A1.Fportcode, A1.Fdate) a" +
				" left Join (Select A1.Fportcode, A1.Fdate, Sum(A1.Ftotalmoney) Ftotalmoney" +
				" From "+pub.yssGetTableName("Tb_Etf_Stocklist")+" A1" +
				" Where A1.Fportcode = "+dbl.sqlString(fPort)+
				" And A1.Fdate = "+dbl.sqlDate(dDate)+
				" And A1.Flistedmarket <> 'XSHE'" +
				" And A1.Freplacemark = '2'" +//必须现金替代
				" Group By A1.Fportcode, A1.Fdate) b On a.Fdate = b.Fdate" +
				" And a.Fportcode = b.Fportcode";
        return query;
	}

	private Date getBargainDate() throws YssException{
		String strSql = "";
		ResultSet rs = null;
		String holidaysCode = "";
		Date mktValueDate = null;
		try{
			BaseOperDeal baseOperDeal = new BaseOperDeal(); // 新建BaseOperDeal
			baseOperDeal.setYssPub(pub);
			
			strSql = " select FHolidaysCode,FSecurityCode from " + pub.yssGetTableName("Tb_Para_Security") + 
			" a where exists (select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
			" b where a.FSecurityCode = b.FAimETFCode and FportCode = " + dbl.sqlString(this.sPort) + ") ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				holidaysCode = rs.getString("FHolidaysCode");
			}
			
			dbl.closeResultSetFinal(rs);
			rs = null;
			
			mktValueDate = baseOperDeal.getWorkDay(holidaysCode, this.sDate, -1);
			return mktValueDate;
		}catch(Exception e){
			throw new YssException("获取ETF成交日期出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**edit by songjie 2012-08-09 STORY 2727 华夏ETF联接基金计算申赎可退替代款 */
	private void doOperionHuaXiaCanReturnMoney(Date dDate, String fPort, Date bargainDate,double basket) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		try {
			strSql = " select sum(a.Fprereturnmoney) as Fprereturnmoney, a.Ftradetypecode from  " +
			" (select Fprereturnmoney, FTradetypeCode, FConSignNum, FbargainDate, FPortcode from " +
			pub.yssGetTableName("Tb_Etf_Linkedbook") +
			" ) a join (Select FConSignNum, FbargainDate, FTradeTypeCode, FPortcode From " +
			pub.yssGetTableName("Tb_Etf_Jsmx") +
			" Where Fsecuritytype = 'JJ' And Fclearmark In ('276', ' ') And "+
			" Frecordtype in ('004') And Fcheckstate = 1 and Ftradeamount <> 0 " +
			" And Fdate = " + dbl.sqlDate(dDate) + 
			" And FPortCode in ( " + operSql.sqlCodes(fPort) +
			" )) b on a.FConSignNum = b.fconsignnum and a.FbargainDate = b.fbargaindate " +
			" and a.FTradeTypeCode = b.ftradetypecode and a.FPortcode = b.FPortcode " +
			" group by a.Ftradetypecode ";
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				if(rs.getString("FTradeTypeCode").equals("102")){
					SGCanReturnMoney = rs.getDouble("Fprereturnmoney");
				}else{
					SHCanReturnMoney = rs.getDouble("Fprereturnmoney");
				}
			}

		} catch (Exception e) {
			throw new YssException("华夏计算申赎可退替代款出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

//	/**shashijie 2012-07-13 STORY 2727 	T日股票篮中可以现金替代股票的申购替代金额 - 股票T日收盘价 * 股票数量
//	* @param Ftotalmoney 替代金额(总金额)
//	* @param Fclosingprice 收盘价
//	* @param Famount 数量
//	* @return*/
//	private double getSGCanReturnMoney(double Ftotalmoney,
//			double Fclosingprice, double Famount,double basket) {
//		double value = 0;
//		value = YssD.sub(Ftotalmoney,YssD.round(YssD.mul(Fclosingprice,Famount,basket), 2));
//		return value;
//	}
//
//	/**shashijie 2012-07-13 STORY 2727 */
//	private String getSSCanReturnMoneySql(Date dDate, String fPort, String baseRateCode, String baseRateSrcCode) throws YssException {
//		String query = "Select a.Fportcode,"+
//			" a.Fdate,"+
//			" a.Ftotalmoney,"+
//			" a.Fsecuritycode,"+
//			" a.Famount,"+
//			" round(b.Fclosingprice * ex."+ baseRateCode + ", 8) as FClosingPrice " +
//			" From (Select A1.Fportcode," +
//			" A1.Fdate," +
//			" A1.Ftotalmoney," +
//			" A1.Fsecuritycode," +
//			" A1.Famount" +
//			" From "+pub.yssGetTableName("Tb_Etf_Stocklist")+" A1"+
//			" Where A1.Fportcode = "+dbl.sqlString(fPort)+
//			" And A1.Fdate = "+dbl.sqlDate(dDate)+
//			" And A1.Freplacemark = '1'" +//可以现金替代
//			" ) a"+
//			" Join (Select B2.Fmktsrccode," +
//			" B2.Fsecuritycode," +
//			" B2.Fmktvaluedate," +
//			" B2.Fclosingprice" +
//			" From "+pub.yssGetTableName("Tb_Data_Marketvalue")+" B2" +
//			" Where B2.Fmktvaluedate = " +
//			" (Select Max(B1.Fmktvaluedate) Fmktvaluedate" +
//			" From "+pub.yssGetTableName("Tb_Data_Marketvalue")+" B1" +
//			" Where B1.Fmktvaluedate <= "+dbl.sqlDate(dDate)+
//			" And B1.Fmktsrccode = '001'" +//数据来源写死001,华夏ETF联接基金
//			" ) And B2.Fcheckstate = 1 ) b On a.Fsecuritycode = b.Fsecuritycode " +
//			" left join (select FSecurityCode,FtradeCury from " + 
//			pub.yssGetTableName("Tb_Para_Security") + 
//			" where FCheckState = 1) sec on sec.FSecurityCode = a.FSecurityCode " +
//			" left join(select " + baseRateCode + ", FCuryCode from " + 
//			pub.yssGetTableName("Tb_Data_ExchangeRate") + 
//			" where FCheckState = 1 and FExRateSrcCode = " + dbl.sqlString(baseRateSrcCode) + 
//			" and FExRateDate = (select max(FExRateDate) from " + 
//			pub.yssGetTableName("Tb_Data_ExchangeRate") + " where FExRateDate <= " + 
//			dbl.sqlDate(dDate) + ")) ex on ex.FCuryCode = sec.FTradeCury";
//		return query;
//	}

	/*
	 * add by songjie 2012.06.19
	 * STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
	 * 生成ETF联接基金台账的可退预估款数据（可退退补款、可退赎回款）
	 */
	private void insertETFLinkBook(Date bargainDate)throws YssException{
		String strSql = "";
		ResultSet rs = null;
		PreparedStatement ps = null; 
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		
		MarketValueBean marketValue = null;
		TradeSubBean tradeSub = null;
		ArrayList alListM = new ArrayList();
		ArrayList alBook = new ArrayList();
		
		double preReturnMoney = 0;
		String tradeTypeCode = "";
		double cashAlternat = 0;
//		String baseRateSrcCode = "";//基础汇率来源
//		String baseRateCode = "";//基础汇率字段
//		String mtkSrcCode = "";//行情来源代码
		StringBuffer sb = new StringBuffer();
		HashMap hmMtv = new HashMap();
		MTVMethodBean mtvMethod = null;
		double price = 0;//股票篮行情价
		try{
			if(!dbl.yssTableExist(pub.yssGetTableName("Tb_ETF_LinkedBook"))){
				return;
			}

			//---delete by songjie 2012.10.24 修改获取估值方法行情来源、汇率来源的sql start---//
//			strSql = " select * from " + pub.yssGetTableName("Tb_Para_MtvMethod") + 
//			         " mtv where exists (select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_Relaship") + 
//			         " rela where mtv.Fmtvcode = rela.fsubcode and FPortCode = " + dbl.sqlString(this.sPort) + 
//			         " and FRelaType = 'MTV' and FRelaGrade = '1') ";
//			rs = dbl.openResultSet(strSql);
//			while(rs.next()){
//				baseRateCode = rs.getString("FBaseRateCode");
//				baseRateSrcCode = rs.getString("FBaseRateSrcCode");
//				mtkSrcCode = rs.getString("FMktSrcCode");
//			}
			
//			strSql = " select sl.*, round(mv.FClosingPrice * ex." + baseRateCode + 
//			",8) as FClosingPrice from (select FPortCode,FSecurityCode,FAmount,FTotalMoney from " +
//			pub.yssGetTableName("Tb_ETF_StockList") + " where FDate = " + dbl.sqlDate(bargainDate) + 
//			" and FListedMarket like 'XHKG%' and FReplaceMark = '1' and FPortCode = " + dbl.sqlString(this.sPort) +
//			" and FCheckState = 1) sl left join (select FMktSrcCode, FSecurityCode, FMktValueDate, FClosingPrice from " +
//			pub.yssGetTableName("tb_data_marketvalue") + " where FMktValueDate = " + dbl.sqlDate(bargainDate) + 
//			" and FMktSrcCode = " + dbl.sqlString(mtkSrcCode) + " and FCheckState = 1) mv on sl.FSecurityCode = mv.FSecurityCode " +
//			" left join (select FSecurityCode,FTradeCury from " + pub.yssGetTableName("Tb_Para_security") + 
//			" where FCheckState = 1) sec on sec.FSecurityCode = sl.FSecurityCode " +
//			" left join ( select " + baseRateCode + ",FCuryCode from " + pub.yssGetTableName("Tb_Data_ExchangeRate") + 
//			" where FCheckState = 1 and FExRateSrcCode = " + dbl.sqlString(baseRateSrcCode) + 
//			" and FExRateDate = (select max(FExRateDate) from " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
//			" where FExRateDate <= " + dbl.sqlDate(bargainDate) +")) ex on ex.FCuryCode = sec.FTradeCury ";			
//			rs = dbl.openResultSet(strSql);
			//---delete by songjie 2012.10.24 修改获取估值方法行情来源、汇率来源的sql end---//
			
			//---add by songjie 2012.10.24 修改获取估值方法行情来源、汇率来源的sql start---//
			//获取股票篮成分股对应的估值方法参数
			sb.append(" select sl.*,ml.Fmtvcode, mtv.FMktSrcCode, mtv.Fmktpricecode,mtv.Fbaseratesrccode, ")
			.append(" mtv.Fbaseratecode from (select FPortCode, FSecurityCode from ")
			.append(pub.yssGetTableName("Tb_ETF_StockList"))
			.append(" where FDate = " + dbl.sqlDate(bargainDate))
			.append(" and FListedMarket like 'XHKG%' and FReplaceMark = '1' and FPortCode = ")
			.append(dbl.sqlString(this.sPort) + " and FCheckState = 1) sl ")
			.append(" left join (select FMtvCode, FLinkCode from ")
			.append(pub.yssGetTableName("Tb_Para_Mtvmethodlink"))
			.append(" ) ml on sl.FSecurityCode = ml.Flinkcode left join (select * from ")
			.append(pub.yssGetTableName("Tb_Para_MtvMethod"))
			.append(" ) mtv on mtv.Fmtvcode = ml.fmtvcode ");
			
			rs = dbl.openResultSet(sb.toString());
			while(rs.next()){
				mtvMethod = new MTVMethodBean();
				mtvMethod.setBaseRateCode(rs.getString("FBaseRateCode"));
				mtvMethod.setBaseRateSrcCode(rs.getString("FBaseRateSrcCode"));
				mtvMethod.setMktPriceCode(rs.getString("FMktPriceCode"));
				mtvMethod.setMktSrcCode(rs.getString("FMktSrcCode"));
				
				hmMtv.put(rs.getString("FSecurityCode"), mtvMethod);
			}
			
			dbl.closeResultSetFinal(rs);
			rs = null;
			
			//获取ETF股票篮可以现金替代的成分股证券代码、替代数量、替代金额、行情数据
			sb = new StringBuffer();
			sb.append(" select sl.*, mv.*, ex.* from (select FPortCode, FSecurityCode, FAmount, FTotalMoney from ")
			.append(pub.yssGetTableName("Tb_ETF_StockList"))
			.append(" where FDate = " + dbl.sqlDate(bargainDate))
			.append(" and FListedMarket like 'XHKG%' and FReplaceMark = '1' and FPortCode = ")
			.append(dbl.sqlString(this.sPort) + " and FCheckState = 1) sl ")
			.append(" left join (select FMtvCode, FLinkCode from ")
			.append(pub.yssGetTableName("Tb_Para_Mtvmethodlink"))
			.append(" where FCheckState = 1) ml on ml.Flinkcode = sl.FSecurityCode ")
			.append(" left join (select FMktSrcCode, FBASERATESRCCODE, FMtvCode from ")
			.append(pub.yssGetTableName("Tb_Para_MtvMethod"))
			.append(" where FCheckState = 1) mtv on mtv.Fmtvcode = ml.Fmtvcode ")
			.append(" left join (select FSecurityCode, FMktSrcCode, FYCLOSEPRICE, FOPENPRICE, FTOPPRICE, ")
			.append(" FLOWPRICE, FCLOSINGPRICE, FAVERAGEPRICE, FNEWPRICE, FMKTPRICE1, FMKTPRICE2 from ")
			.append(pub.yssGetTableName("tb_data_marketvalue"))
			.append(" where FMktValueDate = " + dbl.sqlDate(bargainDate))
			.append(" and FCheckState = 1) mv on sl.FSecurityCode = mv.FSecurityCode and mtv.Fmktsrccode = mv.FMktSrcCode ")
			.append(" left join (select FSecurityCode, FTradeCury from ")
			.append(pub.yssGetTableName("Tb_Para_security"))
			.append(" where FCheckState = 1) sec on sec.FSecurityCode = sl.FSecurityCode ")
			.append(" left join (select FCuryCode, FExRateSrcCode, FExRate1, FExRate2, ")
			.append(" FExRate3, FExRate4, FExRate5, FExRate6, FExRate7, FExRate8 from ")
			.append(pub.yssGetTableName("Tb_Data_ExchangeRate"))
			.append(" where FCheckState = 1 and FExRateDate = (select max(FExRateDate) from ")
			.append(pub.yssGetTableName("Tb_Data_ExchangeRate"))
			.append(" where FExRateDate <= " + dbl.sqlDate(bargainDate))
			.append(" )) ex on ex.FCuryCode = sec.FTradeCury and ex.FExRateSrcCode = mtv.fbaseratesrccode ");
			
			rs = dbl.openResultSet(sb.toString());
			//---add by songjie 2012.10.24 修改获取估值方法行情来源、汇率来源的sql end---//
			while(rs.next()){
				mtvMethod = (MTVMethodBean)hmMtv.get(rs.getString("FSecurityCode"));
				//edit by songjie 2012.10.24 修改行情价格的算法
				//行情价 = round(行情 * 汇率,8) 
				price = YssD.round(YssD.mul(rs.getDouble(mtvMethod.getMktPriceCode()), rs.getDouble(mtvMethod.getBaseRateCode())), 8);
				
				marketValue = new MarketValueBean();
				marketValue.setStrSecurityCode(rs.getString("FSecurityCode"));//证券代码
				marketValue.setStrPortCode("FPortCode");//组合代码
				marketValue.setDblClosingPrice(price);//收盘价//edit by songjie 2012.10.24 修改行情价格的算法
				marketValue.setDblBargainAmount(rs.getDouble("FAmount"));//股票篮数量
				marketValue.setDblBargainMoney(rs.getDouble("FTotalMoney"));//申购现金替代金额
				
				alListM.add(marketValue);
			}
			
			dbl.closeResultSetFinal(rs);
			rs = null;
			
			//获取ETF申购、赎回交易数据中的确认数据
			strSql = " select jsmx.*,sec.FNormScale from (select FSecurityCode1 as FSecurityCode, FConsignNum, FTradeTypeCode, " +
			" FPortCode, sum(FTradeAmount) as FTradeAmount, FBargainDate from " + pub.yssGetTableName("Tb_ETF_JSMX") + 
			" where FDate = " + dbl.sqlDate(this.sDate) + " and FPortCode = " + dbl.sqlString(this.sPort) +
			" and FRecordType = '004' group by FSecurityCode1, FConsignNum, FTradeTypeCode, FPortCode, FBargainDate) jsmx " +
			" left join (select FSecurityCode,FNormScale from " + pub.yssGetTableName("Tb_Para_Security") + 
			" where FCheckState = 1) sec on sec.FsecurityCode = jsmx.FSecurityCode " ;
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				tradeTypeCode = rs.getString("FTradeTypeCode");
				double basket = YssD.div(rs.getInt("FTradeAmount"), rs.getInt("FNormScale"));
				for(int j = 0; j < alListM.size(); j++){
					tradeSub = new TradeSubBean();
					marketValue = (MarketValueBean)alListM.get(j);
					
					preReturnMoney = 0;//可退退补款、赎回款
					
					if(tradeTypeCode.equals("102")){//若为申购数据
						//可收退补款 = （可以现金替代股票申购替代金额 - 股票T日收盘价 * 股票篮中的个股数量） * 篮子数；
						//可收退补款 = round(可以现金替代股票申购替代金额 * 篮子数,2) - round(股票T日收盘价 * 股票篮中的个股数量 * 篮子数,2)；
						cashAlternat = YssD.sub(YssD.round(YssD.mul(marketValue.getDblBargainMoney(),basket),2),
								YssD.round(YssD.mul(marketValue.getDblClosingPrice(), marketValue.getDblBargainAmount(),basket),2));

						//申购的现金替代金额 = 成分股可以现金申购替代金额 * 篮子数；
						preReturnMoney = YssD.mul(marketValue.getDblBargainMoney(), basket);
					} else if(tradeTypeCode.equals("103")){
						//可收赎回款 = round((T日股票篮中可以现金替代股票的T日收盘价 * 股票篮中的个股数量* 篮子数,2)；
						cashAlternat = YssD.round(YssD.mul(marketValue.getDblClosingPrice(),marketValue.getDblBargainAmount(),basket),2);
						preReturnMoney = 0;//若为赎回数据，则不计算现金替代金额 
					}
					
					tradeSub.setTradeCode(tradeTypeCode);//交易方式代码
					tradeSub.setSecurityCode(marketValue.getStrSecurityCode());//股票篮成分股证券代码
					tradeSub.setTradeAmount(YssD.mul(marketValue.getDblBargainAmount(), basket));//替代数量
					tradeSub.setFCanReturnMoney(preReturnMoney);//可退预估款
					tradeSub.setETFCashAlternat(YssD.round(cashAlternat, 2));//现金替代金额
					tradeSub.setBargainDate(YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd"));//成交日期
					tradeSub.setNum(rs.getString("FConsignNum"));//委托编号
					tradeSub.setPortCode(rs.getString("FPortCode"));//组合代码
					
					alBook.add(tradeSub);
				}
			}
			
			dbl.closeResultSetFinal(rs);
			rs = null;
			
			//根据成交日期、组合代码删除ETF联接基金台账数据
			strSql = " delete from " +pub.yssGetTableName("Tb_ETF_LinkedBook") + " where FDate = " + dbl.sqlDate(this.sDate) + 
			" and FPortCode = " + dbl.sqlString(this.sPort);
			dbl.executeSql(strSql);
			
			//插入ETF联接基金台账数据
			strSql = " insert into " + pub.yssGetTableName("Tb_ETF_LinkedBook") + 
			"(Fsecuritycode,FPortCode,FConsignNum,FBargainDate,FTradeAmount,FReplaceMoney,FPreReturnMoney,FTradeTypeCode,FDate)" +
			" values(?,?,?,?,?,?,?,?,?) ";
			
			conn.setAutoCommit(false);
			bTrans = true;
			
			ps = conn.prepareStatement(strSql);
			
			for(int i = 0; i < alBook.size(); i++){
				tradeSub = (TradeSubBean)alBook.get(i);
				
				ps.setString(1, tradeSub.getSecurityCode());//成分股证券代码
				ps.setString(2, tradeSub.getPortCode());//组合代码
				ps.setString(3, tradeSub.getNum());//委托编号
				ps.setDate(4, YssFun.toSqlDate(YssFun.toDate(tradeSub.getBargainDate())));//成交日期
				ps.setDouble(5, tradeSub.getTradeAmount());//替代数量
				//可退预估款（若为申购数据，则为可退退补款，若为赎回数据，则为可退赎回款）
				ps.setDouble(6, tradeSub.getFCanReturnMoney());
				ps.setDouble(7, tradeSub.getETFCashAlternat());//现金替代金额
				ps.setString(8, tradeSub.getTradeCode());//交易方式代码
				ps.setDate(9, YssFun.toSqlDate(this.sDate));//成交日期
				
				ps.addBatch();
			}
			
			ps.executeBatch();
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("将交收结果明细库数据处理到ETF联接基金台账表出错");
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(ps);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**shashijie 2012-07-13 STORY 2727 插入交易数据子表 */
	private void insertSubBeanList(List subBeanList,
			Date dDate, String fPort) throws YssException {
		if (subBeanList.isEmpty()) {
			return;
		}
		
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			//删除资金调拨
			deleteCashTransfer(dDate,fPort);
			//先删除
			String strSql = getDelete(dDate,fPort);
			dbl.executeSql(strSql);
			
			strSql = getInsert();//新增SQL
			ps = conn.prepareStatement(strSql);
			//批量增加
			for (int i = 0; i < subBeanList.size(); i++) {
				TradeSubBean trade = (TradeSubBean)subBeanList.get(i);
				//赋值
				setPreparedStatement(ps,trade);
				ps.executeUpdate();
			}
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("保存交易数据子表出错!",e);
		} finally {
			dbl.closeStatementFinal(ps);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**shashijie 2012-5-11 STORY 2727 删除资金调拨
	* @param dDate
	* @param conn 
	* @param fPort*/
	private void deleteCashTransfer(Date dDate, String fPort) throws YssException {
		String strSql = "";
		String FNum = getSubFNum(dDate, fPort);//获取交易数据编号
    	String strNum = getStrNum(FNum);//获取资金调拨编号
    	
        try {
        	//调拨子表
            strSql = "delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FNum In (" +operSql.sqlCodes(strNum)+" ) ";
            dbl.executeSql(strSql);
            //资调主表
            strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FTradeNum in ("+operSql.sqlCodes(FNum)+")";
            dbl.executeSql(strSql);
            
        } catch (Exception e) {
            throw new YssException("清除资金调拨信息出错\r\n", e);
        } finally {
            //dbl.endTransFinal(conn, true);
        }
	}

	/**shashijie 2012-5-11 STORY 2727 获取资金调拨编号 */
	private String getStrNum(String fNum) throws YssException {
		ResultSet rs = null;
		String fnum = "";//编号
		try {
			String query = " select FNum from "+pub.yssGetTableName("Tb_Cash_Transfer")+
				" where FTradeNum in ("+operSql.sqlCodes(fNum)+")";
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				fnum += rs.getString("FNum") + ",";
			}
		} catch (Exception e) {
			throw new YssException("获取交易数据编号出错\r\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		//去逗号
		if (!fnum.equals("")) {
			fnum = YssFun.getSubString(fnum);
		}
		return fnum;
	}

	/**shashijie 2012-5-11 STORY 2727 获取交易数据编号 */
	private String getSubFNum(Date dDate, String fPort) throws YssException {
		ResultSet rs = null;
		String fnum = "";//编号
		try {
			String query = " select a.FNum from " + pub.yssGetTableName("Tb_Data_SubTrade")+
				" a Where a.FBargainDate = " +dbl.sqlDate(dDate)+
				" And a.FPortCode = " +dbl.sqlString(fPort)+
				//申购,赎回类型随后补上
				" And a.FTradeTypeCode In ("+operSql.sqlCodes(
				YssOperCons.YSS_JYLX_ETFSGou+","+YssOperCons.YSS_JYLX_ETFSH+","+
				YssOperCons.YSS_JYLX_ETFLJSGSB+","+YssOperCons.YSS_JYLX_ETFLJSHSB
																)+
				")";
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				fnum += rs.getString("FNum") + ",";
			}
		} catch (Exception e) {
			throw new YssException("获取交易数据编号出错\r\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		//去逗号
		if (!fnum.equals("")) {
			fnum = YssFun.getSubString(fnum);
		}
		return fnum;
	}

	/**shashijie 2012-07-13 STORY 2727 给对象属性赋值
	* @param ps
	* @param trade*/
	private void setPreparedStatement(PreparedStatement ps, TradeSubBean trade) throws Exception {
		if (trade==null || trade.getNum().trim().equals("") ) {
			throw new YssException("赋值交易数据子表对象出错!");
		}
		ps.setString(1, trade.getNum());//编号
		ps.setString(2, trade.getSecurityCode());//证券代码
		ps.setString(3, trade.getPortCode());//组合代码
		ps.setString(4, trade.getBrokerCode());//券商代码
		ps.setString(5, trade.getInvMgrCode());//投资经理
		ps.setString(6, trade.getTradeCode());//交易方式
		ps.setString(7, trade.getCashAcctCode());//现金帐户
		ps.setString(8, trade.getAttrClsCode());//所属分类
		ps.setDate(9 ,YssFun.toSqlDate(YssFun.toDate(trade.getRateDate())));//汇率日期
		ps.setDate(10,YssFun.toSqlDate(YssFun.toDate(trade.getBargainDate())));//成交日期
		ps.setString(11, trade.getBargainTime());//成交时间
		ps.setDate(12,YssFun.toSqlDate(YssFun.toDate(trade.getSettleDate())));//结算日期
		ps.setString(13,trade.getSettleTime());//结算时间
		ps.setDate(14,YssFun.toSqlDate(YssFun.toDate(trade.getMatureDate())));//到期日期
		ps.setDate(15,YssFun.toSqlDate(YssFun.toDate(trade.getMatureSettleDate())));//到期结算日期
		ps.setString(16,trade.getFactCashAccCode());//实际结算帐户
		ps.setDouble(17,trade.getFactSettleMoney());//实际结算金额
		ps.setDouble(18,trade.getExRate());//兑换汇率
		ps.setDouble(19,trade.getFactBaseRate());//实际结算基础汇率//这个字段说明书中没有
		ps.setDouble(20,trade.getFactPortRate());//实际结算组合汇率
		ps.setString(21,trade.getAutoSettle());//自动结算
		ps.setDouble(22,trade.getPortCuryRate());//组合汇率
		ps.setDouble(23,trade.getBaseCuryRate());//基础汇率
		ps.setDouble(24,trade.getAllotProportion());//分配比例
		ps.setDouble(25,trade.getOldAllotAmount());//原始分配数量
		ps.setDouble(26,trade.getAllotFactor());//分配因子
		ps.setDouble(27,trade.getTradeAmount());//交易数量
		ps.setDouble(28,trade.getTradePrice());//交易价格
		ps.setDouble(29,trade.getTradeMoney());//交易金额
		ps.setDouble(30,trade.getAccruedInterest());//应计利息
		ps.setDouble(31,trade.getBailMoney());//保证金金额
		ps.setString(32,trade.getFFeeCode1());//费用代码1
		ps.setDouble(33,trade.getFTradeFee1());//交易费用1
		ps.setString(34,trade.getFFeeCode2());//费用代码2
		ps.setDouble(35,trade.getFTradeFee2());//交易费用2
		ps.setString(36,trade.getFFeeCode3());//费用代码3
		ps.setDouble(37,trade.getFTradeFee3());//交易费用3
		ps.setString(38,trade.getFFeeCode4());//费用代码4
		ps.setDouble(39,trade.getFTradeFee4());//交易费用4
		ps.setString(40,trade.getFFeeCode5());//费用代码5
		ps.setDouble(41,trade.getFTradeFee5());//交易费用5
		ps.setString(42,trade.getFFeeCode6());//费用代码6
		ps.setDouble(43,trade.getFTradeFee6());//交易费用6
		ps.setString(44,trade.getFFeeCode7());//费用代码7
		ps.setDouble(45,trade.getFTradeFee7());//交易费用7
		ps.setString(46,trade.getFFeeCode8());//费用代码8
		ps.setDouble(47,trade.getFTradeFee8());//交易费用8
		ps.setDouble(48,trade.getTotalCost());//实收实付金额
		ps.setDouble(49,trade.getCost().getCost());//原币核算成本
		ps.setDouble(50,trade.getCost().getMCost());//原币管理成本
		ps.setDouble(51,trade.getCost().getVCost());//原币估值成本
		ps.setDouble(52,trade.getCost().getBaseCost());//基础货币核算成本
		ps.setDouble(53,trade.getCost().getBaseMCost());//基础货币管理成本
		ps.setDouble(54,trade.getCost().getBaseVCost());//基础货币估值成本
		ps.setDouble(55,trade.getCost().getPortCost());//组合货币核算成本
		ps.setDouble(56,trade.getCost().getPortMCost());//组合货币管理成本
		ps.setDouble(57,trade.getCost().getPortVCost());//组合货币估值成本
		ps.setString(58,trade.getSettleState());//结算状态
		ps.setDate(59,YssFun.toSqlDate(YssFun.toDate(trade.getFactSettleDate())));//实际结算日期
		ps.setString(60,trade.getSettleDesc());//结算描述
		ps.setString(61,trade.getOrderNum());//订单编号
		ps.setInt(62,trade.getDataSource());//数据来源
		ps.setString(63,trade.getDataBirth());//交易来源
		ps.setString(64,"");//结算机构代码
		ps.setString(65,trade.getDesc());//描述
		ps.setInt(66, this.checkState.equalsIgnoreCase("true")?1:0);//审核状态
		ps.setString(67, pub.getUserCode());//创建人、修改人
        ps.setString(68, YssFun.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));//创建、修改时间
		ps.setString(69, this.checkState.equalsIgnoreCase("true")?pub.getUserCode():" ");//复核人
		ps.setString(70, this.checkState.equalsIgnoreCase("true")?
				YssFun.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"):" ");//复核时间
		ps.setString(71,trade.getETFBalaAcctCode());//ETF现金差额账户代码
		ps.setDate(72,YssFun.toSqlDate(YssFun.toDate(trade.getETFBalaSettleDate())));//ETF现金差额结算日期
		ps.setDouble(73,trade.getETFBalanceMoney());//ETF现金差额
		ps.setDouble(74,trade.getETFCashAlternat());//ETF现金替代
		ps.setString(75,trade.getTradeSeatCode());//席位代码
		ps.setString(76,trade.getStockholderCode());//股东代码
		ps.setString(77,"");//操作类型
		ps.setString(78,trade.getSplitNum());//拆分关联编号
		ps.setString(79,trade.getInvestType());//投资类型
		ps.setString(80,trade.getFdealNum());//开放式基金业务,编号	//这个字段说明书中没有
		ps.setDate(81,YssFun.toSqlDate(YssFun.toDate("1900-01-01")));//???    //这个字段说明书中没有
		ps.setString(82,trade.getJkdr());// 接口导入:0手工录入,1 接口导入    //这个字段说明书中没有
		ps.setDate(83,YssFun.toSqlDate(YssFun.toDate(trade.getStrRecordDate())));//登记日
		ps.setDouble(84,Double.valueOf(trade.getStrDivdendType()).doubleValue());//分红类型
		ps.setString(85,trade.getFSecurityDelaySettleState());//延迟交割标识
		ps.setString(86,"");//券商代码类型
		ps.setString(87,trade.getBrokerCode());//券商代码
		ps.setString(88,"");//结算结构代码类型
		ps.setString(89,"");//结算结构代码
		ps.setString(90,"");//结算券商代码类型
		ps.setString(91,"");//结算券商代码
		ps.setString(92,"");//结算账户代码
		ps.setString(93,trade.getCostIsHandEditState());//手动修改成本标示
		ps.setDate(94,YssFun.toSqlDate(YssFun.toDate(trade.getFBSDate())));//ETF申赎日期
		ps.setDouble(95,trade.getFCanReturnMoney());//ETF可退替代款
		ps.setDate(96,YssFun.toSqlDate(YssFun.toDate(trade.getMtReplaceDate())));//现金替代结算日期
	}

	/**shashijie 2012-07-13 STORY 2727 新增SQL语句 */
	private String getInsert() {
		String sql = " insert into "+
			pub.yssGetTableName("Tb_Data_SubTrade")+
			"(" +
			" FNUM," +//编号
			" FSECURITYCODE," +//证券代码
			" FPORTCODE," +//组合代码
			" FBROKERCODE," +//券商代码
			" FINVMGRCODE," +//投资经理
			" FTRADETYPECODE," +//交易方式
			" FCASHACCCODE," +//现金帐户
			" FATTRCLSCODE," +//所属分类
			" FRATEDATE," +//汇率日期
			" FBARGAINDATE," +//成交日期
			" FBARGAINTIME," +//成交时间
			" FSETTLEDATE," +//结算日期
			" FSETTLETIME," +//结算时间
			" FMATUREDATE," +//到期日期
			" FMATURESETTLEDATE," +//到期结算日期
			" FFACTCASHACCCODE," +//实际结算帐户
			" FFACTSETTLEMONEY," +//实际结算金额
			" FEXRATE," +//兑换汇率
			" FFACTBASERATE," +//实际结算基础汇率//这个字段说明书中没有
			" FFACTPORTRATE," +//实际结算组合汇率
			" FAUTOSETTLE, " +//自动结算
			" FPORTCURYRATE," +//组合汇率
			" FBASECURYRATE," +//基础汇率
			" FALLOTPROPORTION," +//分配比例
			" FOLDALLOTAMOUNT," +//原始分配数量
			" FALLOTFACTOR," +//分配因子
			" FTRADEAMOUNT," +//交易数量
			" FTRADEPRICE," +//交易价格
			" FTRADEMONEY," +//交易金额
			" FACCRUEDINTEREST," +//应计利息
			" FBAILMONEY," +//保证金金额
			" FFEECODE1," +//费用代码1
			" FTRADEFEE1," +//交易费用1
			" FFEECODE2," +//费用代码2
			" FTRADEFEE2," +//交易费用2
			" FFEECODE3," +//费用代码3
			" FTRADEFEE3," +//交易费用3
			" FFEECODE4," +//费用代码4
			" FTRADEFEE4," +//交易费用4
			" FFEECODE5," +//费用代码5
			" FTRADEFEE5," +//交易费用5
			" FFEECODE6," +//费用代码6
			" FTRADEFEE6," +//交易费用6
			" FFEECODE7," +//费用代码7
			" FTRADEFEE7," +//交易费用7
			" FFEECODE8," +//费用代码8
			" FTRADEFEE8," +//交易费用8
			" FTOTALCOST," +//实收实付金额
			" FCOST," +//原币核算成本
			" FMCOST," +//原币管理成本
			" FVCOST," +//原币估值成本
			" FBASECURYCOST," +//基础货币核算成本
			" FMBASECURYCOST," +//基础货币管理成本
			" FVBASECURYCOST," +//基础货币估值成本
			" FPORTCURYCOST," +//组合货币核算成本
			" FMPORTCURYCOST," +//组合货币管理成本
			" FVPORTCURYCOST," +//组合货币估值成本
			" FSETTLESTATE," +//结算状态
			" FFACTSETTLEDATE," +//实际结算日期
			" FSETTLEDESC," +//结算描述
			" FORDERNUM," +//订单编号
			" FDATASOURCE," +//数据来源
			" FDATABIRTH," +//交易来源
			" FSETTLEORGCODE," +//结算机构代码
			" FDESC," +//描述
			" FCHECKSTATE," +//审核状态//这个字段说明书中没有
			" FCREATOR," +//创建人、修改人
			" FCREATETIME," +//创建、修改时间
			" FCHECKUSER," +//复核人
			" FCHECKTIME," +//复核时间
			" FETFBALAACCTCODE," +//ETF现金差额账户代码
			" FETFBALASETTLEDATE," +//ETF现金差额结算日期
			" FETFBALAMONEY," +//ETF现金差额
			" FETFCASHALTERNAT," +//ETF现金替代
			" FSEATCODE," +//席位代码
			" FSTOCKHOLDERCODE," +//股东代码
			" FDS," +//操作类型
			" FSPLITNUM," +//拆分关联编号
			" FINVESTTYPE," +//投资类型
			" FDEALNUM," +//开放式基金业务,编号//这个字段说明书中没有
			" FAPPDATE," +//???    //这个字段说明书中没有
			" FJKDR," +// 接口导入:0手工录入,1 接口导入    //这个字段说明书中没有
			" FRECORDDATE," +//登记日
			" FDIVDENDTYPE," +//分红类型
			" FSECURITYDELAYSETTLESTATE," +//延迟交割标识
			" FBROKERIDCODETYPE," +//券商代码类型
			" FBROKERIDCODE," +//券商代码
			" FSETTLEORGIDCODETYPE," +//结算结构代码类型
			" FSETTLEORGIDCODE," +//结算结构代码
			" FCLEARINGBROKERCODETYPE," +//结算券商代码类型
			" FCLEARINGBROKERCODE," +//结算券商代码
			" FCLEARINGACCOUNT," +//结算账户代码
			" FHANDCOSTSTATE," +//手动修改成本标示
			" FBSDATE," +//ETF申赎日期
			" FCANRETURNMONEY," +//ETF可退替代款
			" FMtReplaceDate "+//现金替代结算日期
						
			")"+
			" Values (" +
			" ?,?,?,?,?,?,?,?,?,?" +//10
			",?,?,?,?,?,?,?,?,?,?" +//20
			",?,?,?,?,?,?,?,?,?,?" +//30
			",?,?,?,?,?,?,?,?,?,?" +//40
			",?,?,?,?,?,?,?,?,?,?" +//50
			",?,?,?,?,?,?,?,?,?,?" +//60
			",?,?,?,?,?,?,?,?,?,?" +//70
			",?,?,?,?,?,?,?,?,?,?" +//80
			",?,?,?,?,?,?,?,?,?,?" +//90
			",?,?,?,?,?,?)";
		return sql;
	}

	/**shashijie 2012-07-13 STORY 2727 删除交易数据子表sql
	* @return*/
	private String getDelete(Date dDate, String fPort) {
		String sqlString = " delete from " + pub.yssGetTableName("Tb_Data_SubTrade")+
			" a Where a.FBargainDate = " +dbl.sqlDate(dDate)+
			" And a.FPortCode = " +dbl.sqlString(fPort)+
			" And a.FDatasource = '1' " +
			//申购,赎回类型随后补上
			" And a.FTradeTypeCode In ("+operSql.sqlCodes(
			YssOperCons.YSS_JYLX_ETFSGou+","+YssOperCons.YSS_JYLX_ETFSH+","+
			YssOperCons.YSS_JYLX_ETFLJSGSB+","+YssOperCons.YSS_JYLX_ETFLJSHSB
															)+
			")";
		return sqlString;
	}

	/**shashijie 2012-07-13 STORY 2727 获取今日申请数据集合
	* @param dDate
	* @param fPort
	* @return*/
	private List getTradeSubBeanList(Date dDate, String fPort) throws YssException {
		ResultSet rs = null;
		List SubBean = new ArrayList();//存放交易数据对象
		double basket = 0;//篮子数
		java.util.Date bargainDate = null;
		try {
			String sql = getSqlToDay(dDate,fPort);
			rs = dbl.openResultSet(sql);
			while (rs.next()) {
				bargainDate = rs.getDate("FBarGainDate");//申购赎回日期
				//篮子数
				basket = YssD.div(rs.getDouble("FTradeAmount"), rs.getDouble("FNormScale"));
				doOperionHuaXiaCanReturnMoney(this.sDate,this.sPort, bargainDate,basket);
				//获取交易数据对象并赋值,(易方达)
				TradeSubBean suBean = getTradeSubBean(rs,fPort,"1");
				/**shashijie 2012-07-13 STORY 2727 华夏ETF联接基金 */
				if (rs.getString("FRecordType").equals("004")) {//记录类型004是华夏ETF联接基金数据
					setSubBean(suBean,rs);
				}
				/**end*/
				SubBean.add(suBean);//一般只有2条数据,B申购,S赎回
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return SubBean;
	}

	/**shashijie 2012-07-13 STORY 2727 处理华夏ETF联接基金,这里主要处理与易方达的区别部分*/
	private void setSubBean(TradeSubBean trade, ResultSet rs) throws Exception {
		//获取结算日期
		String settleDate = getSettleDate(rs.getString("FHolidaysCode"),YssFun.toDate(trade.getBargainDate()),
				rs.getInt("FSettleDays"),//延迟天数
				""//交易类型
				);
		trade.setSettleDate(settleDate); // 结算日期
		//现金差额结算日期
		String ETFBalaSettleDate = getSettleDate(rs.getString("FHolidaysCode"),YssFun.toDate(trade.getBargainDate()),
				rs.getInt("FBsdifferenceOver"),""
				);
		trade.setETFBalaSettleDate(ETFBalaSettleDate); // ETF 现金差额结算日期
		//现金替代结算日期
		String mtReplaceDate = "9998-12-31";
		
		//申购业务的可收退补款 = ∑（T日股票篮中可以现金替代股票的申购替代金额 - 股票T日收盘价 * 股票数量）* 篮子数
		if (trade.getTradeCode().equals(YssOperCons.YSS_JYLX_ETFSGou)){
			//这里要,乘以篮子数
			double SgCan = SGCanReturnMoney;
//			double SgCan = getETFBalanceMoney(SGCanReturnMoney, 
//					trade.getTradeAmount(), rs.getDouble("Fnormscale"),"",0);
			trade.setFCanReturnMoney(SgCan);//ETF可退替代款
			
			//申购现金替代	=  ∑（T日股票篮现金替代金额） * 篮子数
			double ETFCashAlternat = getETFBalanceMoney(SGCashAlternat,
					trade.getTradeAmount(),rs.getDouble("Fnormscale"),"",0);
			//ETF 现金替代
			trade.setETFCashAlternat(YssD.round(ETFCashAlternat,2));
			
			//现金替代结算日期
			mtReplaceDate = getSettleDate(rs.getString("FHolidaysCode"),YssFun.toDate(trade.getBargainDate()),
					rs.getInt("FReplaceOver"),//延迟天数
					""//交易类型
					);
			trade.setMtReplaceDate(mtReplaceDate);
		} else {
		//赎回业务的可收赎回款 = ∑（T日股票篮中可以现金替代股票的T日收盘价 * 股票篮中的个股数量）* 篮子数
			//这里要,乘以篮子数
			double ShCan = SHCanReturnMoney;
//			double ShCan = getETFBalanceMoney(SHCanReturnMoney,
//					trade.getTradeAmount(), rs.getDouble("Fnormscale"),"",0);
			trade.setFCanReturnMoney(ShCan);//ETF可退替代款
			
			//赎回现金替代款  = ∑（T日股票篮中必须现金替代股票的赎回替代金额） * 篮子数；
			double ETFCashAlternat = getETFBalanceMoney(SHCashAlternat,
					trade.getTradeAmount(),rs.getDouble("Fnormscale"),"",0);
			//ETF 现金替代
			trade.setETFCashAlternat(YssD.round(ETFCashAlternat,2));
			
			//现金替代结算日期
			mtReplaceDate = getSettleDate(rs.getString("FHolidaysCode"),YssFun.toDate(trade.getBargainDate()),
					rs.getInt("FMtReplaceOver"),//延迟天数
					""//交易类型
					);
			trade.setMtReplaceDate(mtReplaceDate);
		}
		
		//应付佣金
		trade.setFFeeCode1("应付佣金");
		trade.setFTradeFee1(rs.getDouble("FOtherMoney1"));
		//交易费用
		trade.setFFeeCode2("交易费用");
		BigDecimal FTradeFee2 = YssD.addD(rs.getBigDecimal("FStampTax"),
				rs.getBigDecimal("FhandleTax"),rs.getBigDecimal("FTransferTax"),rs.getBigDecimal("FCanalTax"),
				rs.getBigDecimal("FProcedureTax"),rs.getBigDecimal("FOtherMoney2")
				);
		FTradeFee2 = YssD.addD(FTradeFee2,rs.getBigDecimal("FOtherMoney3"));
		trade.setFTradeFee2(FTradeFee2.doubleValue());
		
		//实收实付金额 = 费用2(华夏暂时不考虑佣金)
		trade.setTotalCost(FTradeFee2.doubleValue());//投资总成本(实收实付)
		
		//成本 = 现金替代金额 + 现金差额 – 可收退补款
		YssCost cost = getYssCost(trade.getTradeCode(),//交易方式
				new BigDecimal(trade.getETFBalanceMoney()),//ETF 现金差额
				new BigDecimal(trade.getETFCashAlternat()),//ETF 现金替代
				new BigDecimal(trade.getFCanReturnMoney()),//ETF 退补款
				
				new BigDecimal(trade.getBaseCuryRate()),new BigDecimal(trade.getPortCuryRate()),//汇率
				"" ,//标识
				new BigDecimal(0),//原币成本
				new BigDecimal(0),//基础货币成本
				new BigDecimal(0) //组合货币成本
				);
		trade.setCost(cost);
	}

	/**shashijie 2012-07-13 STORY 2727 交易对象赋值 */
	private TradeSubBean getTradeSubBean(ResultSet rs,String FPort,String mark) throws Exception {
		TradeSubBean trade = new TradeSubBean();
		//trade.setYssPub(pub);
		//获取流水号
		getTradeNum(rs.getDate("FDate"),rs.getString("FBargainbs"),mark);
		String num = FNum;
		trade.setNum(num); // 交易拆分数据流水号
		
		trade.setInvestType("C");//投资类型
		trade.setSecurityCode(rs.getString("FSecurityCode1")); // 交易证券代码
		trade.setPortCode(FPort); // 组合代码
		trade.setBrokerCode(" "); // 券商代码
		trade.setInvMgrCode(" "); // 投资经理代码
		
		//获取交易方式代码
		String tradeCode = getTradeCode(rs.getString("FBargainbs"),mark);
		trade.setTradeCode(tradeCode);//交易类型
		
		//获取现金账户代码
		String cashAcctCode = getCashAcctCode(FPort,trade.getTradeCode(),
				rs.getDate("FDate"),rs.getString("FCurrencyCode"));
		trade.setCashAcctCode(cashAcctCode); // 现金帐户代码
		trade.setAttrClsCode(""); // 所属分类代码
		trade.setBargainDate(YssFun.formatDate(rs.getDate("FDate"))); // 成交日期
		trade.setBargainTime("00:00:00"); // 成交时间
		
		//获取结算日期
		String settleDate = getSettleDate(rs.getString("FHolidaysCode"),rs.getDate("FBargainDate"),
				rs.getInt("FReplaceOver"),//现金替代
				trade.getTradeCode()
				);
		trade.setSettleDate(settleDate); // 结算日期
		trade.setSettleTime("00:00:00"); // 结算时间
		trade.setRateDate(YssFun.formatDate(rs.getDate("FDate"))); // 汇率日期
		trade.setAutoSettle("0"); // 自动结算
		
		//获取汇率
		double baseCuryRate = getCuryRate(rs.getDate("FDate"),FPort,rs.getString("FCurrencyCode"),
				YssOperCons.YSS_RATE_BASE);
		trade.setBaseCuryRate(baseCuryRate); // 基础汇率
		double portCuryRate = getCuryRate(rs.getDate("FDate"), FPort, "", YssOperCons.YSS_RATE_PORT);
		trade.setPortCuryRate(portCuryRate); // 组合汇率
		trade.setFactBaseRate(baseCuryRate);//实际结算基础汇率//这个字段说明书中没有
		trade.setFactPortRate(portCuryRate);//实际结算组合汇率
		
		trade.setHandAmount(0); // 每手股数
		trade.setAllotProportion(0); // 分配比例
		trade.setOldAllotAmount(0); // 原始分配数量
		
		//获取交易数量
		double tradeAmount = getTradeAmount(rs.getDouble("Ftradeamount"),mark,
				mark.equals("2")?rs.getDouble("tradeamount"):0);
		trade.setTradeAmount(tradeAmount); // 交易数量
		trade.setTradePrice(rs.getDouble("Ftradeprice")); // 交易价格
		//获取成交金额
		double tradeMoney = getTradeMoney(trade.getTradeAmount(), trade.getTradePrice(), mark,
				mark.equals("2")?rs.getDouble("Ftrademoney"):0);
		trade.setTradeMoney(tradeMoney); // 交易总额
		trade.setAccruedInterest(0); // 应计利息
		trade.setAllotFactor(1); // 分配因子
		
		double FTotalCost = 0;
		trade.setTotalCost(FTotalCost); // 投资总成本(实收实付)
		trade.setDesc(""); // 交易描述
		trade.setSettleState("0"); // 结算状态,临时
		trade.setBailMoney(0); // 保证金金额
		
		trade.setFactor(1); // 报价因子
		trade.setFactCashAccCode(cashAcctCode); // 实际结算帐户
		trade.setFactSettleMoney(trade.getTotalCost()); // 实际结算金额
		trade.setExRate(1); // 兑换汇率
		
		trade.setFactSettleDate(trade.getSettleDate()); // 实际结算日期
		trade.setSettleDesc(""); // 结算描述
		trade.setDataSource(1);//数据来源
		
		trade.setETFBalaAcctCode(cashAcctCode); // ETF 现金差额结算帐户代码
		trade.setFBSDate(YssFun.formatDate(rs.getDate("FBargainDate")));//ETF申购日期
		//现金差额 = 单位现金差额 * 篮子数(交易数量/最小申赎份额)
		double ETFBalanceMoney = getETFBalanceMoney(rs.getDouble("Fdvalue"),
				//生成冲减数据时,数量应该传入的是确认数量而非倒钆数量
				mark.equals("2")?rs.getDouble("FTradeAmount"):trade.getTradeAmount(),
				rs.getDouble("Fnormscale"),mark,
				mark.equals("2")?rs.getDouble("FETFBalaMoney"):0);
		trade.setETFBalanceMoney(YssD.round(ETFBalanceMoney,2)); // ETF 现金差额
		//获取现金替代
		double ETFCashAlternat = getETFCashAlternat(rs.getDouble("FTotalMoney"),mark,
				mark.equals("2")?rs.getDouble("FETFCashalTernat"):0);
		//ETF 现金替代
		trade.setETFCashAlternat(YssD.round(ETFCashAlternat,2));
		
		//ETF申购可退替代款 = 现金替代 - 单位篮子市值 *篮子数(交易数量/最小申赎份额)
		//ETF赎回可退替代款 = 单位篮子市值 *篮子数(交易数量/最小申赎份额)
		double FCanReturnMoney = getFCanReturnMoney(
				mark.equals("2")?rs.getDouble("FTotalMoney"):trade.getETFCashAlternat(),
				rs.getDouble("Fmvalue"),
				//生成冲减数据时,数量应该传入的是确认数量而非倒钆数量
				mark.equals("2")?rs.getDouble("FTradeAmount"):trade.getTradeAmount(),
				rs.getDouble("Fnormscale"),mark ,
				mark.equals("2")?rs.getDouble("FCanreturnMoney"):0,
				trade.getTradeCode());
		trade.setFCanReturnMoney(YssD.round(FCanReturnMoney,2));//ETF可退替代款
		//现金差额结算日期
		String ETFBalaSettleDate = getSettleDate(rs.getString("FHolidaysCode"),rs.getDate("FBargainDate"),
				rs.getInt("Fbsdifferenceover"),""
				);
		trade.setETFBalaSettleDate(ETFBalaSettleDate); // ETF 现金差额结算日期
		
		//成本 = 现金差额 + 现金替代 - 退补款	:(申购,申购失败冲减)
		YssCost cost = getYssCost(trade.getTradeCode(),//交易方式
				new BigDecimal(trade.getETFBalanceMoney()),//ETF 现金差额
				new BigDecimal(trade.getETFCashAlternat()),//ETF 现金替代
				new BigDecimal(trade.getFCanReturnMoney()),//ETF 退补款
				
				new BigDecimal(trade.getBaseCuryRate()),new BigDecimal(trade.getPortCuryRate()),//汇率
				mark ,//标识
				new BigDecimal(mark.equals("2")? rs.getDouble("FCost"):0),//原币成本
				new BigDecimal(mark.equals("2")? rs.getDouble("FBasecuryCost"):0),//基础货币成本
				new BigDecimal(mark.equals("2")? rs.getDouble("FPortcuryCost"):0) //组合货币成本
				);
		trade.setCost(cost);
		
		trade.setTradeSeatCode(" "); // 席位代码
		trade.setStockholderCode(rs.getString("Fstockholdercode"));// 股东代码
		//证券延迟交割标示，0未延迟 ，1延迟
		trade.setFSecurityDelaySettleState("0");
		trade.setMatureDate("9998-12-31");//到期日期
		trade.setMatureSettleDate("9998-12-31");//到期结算日期
		
		//现金替代结算日期
		String mtReplaceDate = getSettleDate(rs.getString("FHolidaysCode"),YssFun.toDate(trade.getBargainDate()),
				rs.getInt("FReplaceOver"),//延迟天数
				""//交易类型
				);
		trade.setMtReplaceDate(mtReplaceDate);
		return trade;
	}

	/**shashijie 2012-5-2 STORY 2727 获取现金替代  */
	private double getETFCashAlternat(double Ftotalmoney, String mark,
			double Fetfcashalternat) {
		double CashAlternat = Ftotalmoney;
		//如果是生产冲减数据,倒钆计算
		if (mark.equals("2")) {
			CashAlternat = YssD.mul(YssD.sub(Fetfcashalternat, CashAlternat),-1);
		}
		return CashAlternat;
	}

	/**shashijie 2012-5-2 STORY 2727 获取成交金额 = 数量 * 成交价 */
	private double getTradeMoney(double tradeAmount, double tradePrice,
			String mark, double tradeMoney) {
		//数量*价格
		double money = YssD.round(
				YssD.mul(tradeAmount, tradePrice),2);
		//如果是生产冲减数据,倒钆计算
		if (mark.equals("2")) {
			money = YssD.mul(YssD.sub(tradeMoney,money),-1);
		}
		return money;
	}

	/**shashijie 2012-5-2 STORY 2727 获取交易数量 */
	private double getTradeAmount(double Ftradeamount, String mark, double tradeamount) {
		double amount = Ftradeamount;
		//如果是生成冲减数据,倒钆计算值
		if (mark.equals("2")) {
			amount = YssD.mul(YssD.sub(tradeamount, amount),-1);
		}
		return amount;
	}

	/**shashijie 2012-07-13 STORY 2727 获取交易方式代码
	* @param FBargainbs 申赎方式
	* @param mark 申请,确认 标识
	* @return*/
	private String getTradeCode(String FBargainbs,String mark) {
		String tradeCode = "";
		if (FBargainbs.equals("B") && mark.equals("1")) {//申购
			tradeCode = YssOperCons.YSS_JYLX_ETFSGou; //申购,赎回类型随后补上
		} else if(FBargainbs.equals("S") && mark.equals("1")){//赎回
			tradeCode = YssOperCons.YSS_JYLX_ETFSH; //申购,赎回类型随后补上
		}//申购失败
		else if (FBargainbs.equals("B") && mark.equals("2")) {
			tradeCode = YssOperCons.YSS_JYLX_ETFLJSGSB;
		}//赎回失败
		else if (FBargainbs.equals("S") && mark.equals("2")) {
			tradeCode = YssOperCons.YSS_JYLX_ETFLJSHSB;
		}
		return tradeCode;
	}

	/**shashijie 2012-07-13 STORY 2727  成本 
	* @param tradeCode 交易方式
	* @param ETFBalanceMoney 现金差额
	* @param ETFCashAlternat 现金替代
	* @param FCanReturnMoney 退补款
	* @param baseRate 基础汇率
	* @param portRate 组合汇率
	* @param FBSDate 申赎日期
	* @param fPort 组合
	* @param mark 申请确认标识
	* @return*/
	private YssCost getYssCost(String tradeCode, BigDecimal ETFBalanceMoney, 
			BigDecimal ETFCashAlternat, BigDecimal FCanReturnMoney, BigDecimal baseRate, BigDecimal portRate, 
			String mark, BigDecimal FCost,BigDecimal FBaseCost, BigDecimal FPortCost) throws YssException {
		YssCost yssCost = new YssCost();
		//原币成本
		BigDecimal cost = new BigDecimal(0);
		//基础成本
		BigDecimal baseCost = new BigDecimal(0);
		//组合成本
		BigDecimal portCost = new BigDecimal(0);
		try {
			//申购,申购失败冲减
			if (tradeCode.equals(YssOperCons.YSS_JYLX_ETFSGou) || tradeCode.equals(YssOperCons.YSS_JYLX_ETFLJSGSB)) {
				//原币成本
				cost = YssD.subD(YssD.addD(ETFBalanceMoney, ETFCashAlternat), FCanReturnMoney);
				//基础成本
				baseCost = YssD.mulD(cost, baseRate);
				//组合成本
				portCost = YssD.divD(baseCost, portRate);
				
				/*if (mark.equals("2")) {
					//原币成本
					cost = YssD.subD(FCost,cost);
					//基础成本
					baseCost = YssD.subD(FBaseCost,baseCost);
					//组合成本
					portCost = YssD.subD(FPortCost,portCost);
				}*/
				//对象赋值
				setYssCostValue(yssCost,YssD.round(cost, 2),YssD.round(baseCost,2),YssD.round(portCost,2));
			}
		} catch (Exception e2) {
			throw new YssException("获取成本出错!");
		} finally {

		}
		
		return yssCost;
	}

	/**shashijie 2012-07-13 STORY 2727 成本对象赋值 */
	private void setYssCostValue(YssCost yssCost,double cost, double baseCost, double portCost) {
		if (yssCost==null) {
			return;
		}
		//原币成本
		yssCost.setCost(cost);
		yssCost.setMCost(cost);
		yssCost.setVCost(cost);
		//基础成本
		yssCost.setBaseCost(baseCost);
		yssCost.setBaseMCost(baseCost);
		yssCost.setBaseVCost(baseCost);
		//组合成本
		yssCost.setPortCost(portCost);
		yssCost.setPortMCost(portCost);
		yssCost.setPortVCost(portCost);
	}

	/**shashijie 2012-07-13 STORY 2727 现金差额 = 单位现金差额 * 篮子数(交易数量/最小申赎份额)
	* @param Fdvalue 单位现金差额
	* @param tradeAmount 交易数量
	* @param Fnormscale 最小申赎份额
	* @param Fetfbalamoney 
	* @param mark 
	* @param Fetfbalamoney
	* @return*/
	private double getETFBalanceMoney(double Fdvalue, double tradeAmount,
			double Fnormscale, String mark, double Fetfbalamoney) {
		double ETFBalanceMoney = YssD.round(YssD.mul(Fdvalue, 
			YssD.div(tradeAmount, Fnormscale)),2);
		//如果是生产冲减数据,倒钆计算
		if (mark.equals("2")) {
			ETFBalanceMoney = YssD.mul(YssD.sub(Fetfbalamoney, ETFBalanceMoney),-1);
		}
		return ETFBalanceMoney;
	}

	/**shashijie 2012-07-13 STORY 2727   
	 * //ETF申购可退替代款 = 现金替代 - 单位篮子市值 *篮子数(交易数量/最小申赎份额)
	 * //ETF赎回可退替代款 = 			    单位篮子市值 *篮子数(交易数量/最小申赎份额)*/
	private double getFCanReturnMoney(double etfCashAlternat, double Fmvalue,
			double tradeAmount, double Fnormscale, String mark, double Fcanreturnmoney,
			String tradeTypeCode) {
		double FCanReturnMoney = 0;//可退替代款
		double Amount = YssD.div(tradeAmount, Fnormscale);//篮子数
		//客户没给反应,申购,赎回失败冲销,可退替代款计算过程中是否保留股票蓝市值的小数位,这里暂时先保留2位小数
		double MValue = YssD.round(YssD.mul(Fmvalue, Amount),2);//篮子市值
		
		//赎回,赎回失败
		if (tradeTypeCode.equals(YssOperCons.YSS_JYLX_ETFSH)
				|| tradeTypeCode.equals(YssOperCons.YSS_JYLX_ETFLJSHSB)) {
			
			//如果是生产冲减数据,倒钆计算
			if (mark.equals("2")) {
				MValue = YssD.mul(YssD.sub(Fcanreturnmoney, MValue),-1);
			}
			return MValue ;
		} else {
			//ETF可退替代款 = 现金替代 - 篮子市值
			FCanReturnMoney = YssD.sub(etfCashAlternat,MValue);
			
			//如果是生产冲减数据,倒钆计算
			if (mark.equals("2")) {
				FCanReturnMoney = YssD.mul(YssD.sub(Fcanreturnmoney, FCanReturnMoney),-1);
			}
		}
		return FCanReturnMoney;
	}

	/**shashijie 2012-07-13 STORY 2727 获取汇率
	* @param date 日期
	* @param fPort 组合
	* @param string 币种
	* @param yssRateBase 标识
	* @return*/
	private double getCuryRate(Date dDate, String FPort, String FCuryCode,
			String YSSRATEBASE) throws YssException {
		double rate = 0;
		try {
			rate = this.getSettingOper().getCuryRate(
					dDate, 
					FCuryCode.trim().equals("RMB") ? "CNY" : FCuryCode ,//币种
					FPort, 
					YSSRATEBASE);
		} catch (Exception e) {
			throw new YssException("获取汇率出错!");
		} finally {

		}
		return rate;
	}

	/**shashijie 2012-07-13 STORY 2727 获取结算日期
	* @param FHolidaysCode 节假日代码
	* @param dDate 日期
	* @param dayInt 延迟天数
	* @return*/
	private String getSettleDate(String FHolidaysCode, Date dDate , int dayInt,
			String tradeTypeCode) throws YssException {
		Date mDate = null;//工作日
		//赎回,赎回失败冲销日期为9998-12-31
		if (tradeTypeCode.equals(YssOperCons.YSS_JYLX_ETFSH)
				|| tradeTypeCode.equals(YssOperCons.YSS_JYLX_ETFLJSHSB)) {
			return "9998-12-31";
		}
		try {
			//公共获取工作日类
			BaseOperDeal operDeal = new BaseOperDeal();
	        operDeal.setYssPub(pub);
	        mDate = operDeal.getWorkDay(FHolidaysCode, dDate, dayInt);
		} catch (Exception e) {
			throw new YssException("获取结算日期出错!");
		} finally {

		}
        return YssFun.formatDate(mDate);

	}

	/**shashijie 2012-07-13 STORY 2727 获取现金账户代码
	* @return*/
	private String getCashAcctCode(String FPort,String tradeTypeCode,Date FDate,String FCuryCode
			) throws YssException {
		String strCashAccCode = "";//现金账户
		try {
			
			BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.getOperDealCtx().getBean("cashacclinkdeal");//账户链接
			cashacc.setYssPub(pub);
			cashacc.setLinkParaAttr(
					"", //投资经理
					FPort, //组合代码
					YssOperCons.YSS_ZQPZ_TR,//品种类型
					YssOperCons.YSS_ZQPZZLX_TR04,//品种子类型 TR04 ETF基金
					"",//交易所
					"",//券商代码
					tradeTypeCode, //交易类型
	        		FDate,//启用日期
	        		FCuryCode.trim().equals("RMB") ? "CNY" : FCuryCode //币种
	        		); 
			
			CashAccountBean caBean = cashacc.getCashAccountBean();//获取现金账户Bean
			strCashAccCode = caBean.getStrCashAcctCode(); //现金账户
		} catch (Exception e) {
			throw new YssException("现金账户获取不到!");
		} finally {
			
		}
		return strCashAccCode;
	}

	/**shashijie 2012-07-13 STORY 2727 获取交易数据流水号 */
	private void getTradeNum(java.sql.Date bargainDate,String FBs,String mark) throws YssException {
		try {
			//判断申赎拼接流水号
			String fNumType = "";
			if (FBs.equals("B")) {
				fNumType = "300000";//申购,申购失败
			} else {
				if (mark.equals("1")) {
					fNumType = "900000";//赎回
				} else {
					fNumType = "400000";//赎回失败
				}
			}
			
			
			//日期
			String strNumDate = YssFun.formatDatetime((YssFun.toSqlDate(bargainDate))).substring(0, 8);
			//同类别下最大编号
			FNum = "T" + strNumDate;
			//交易主表
			FNum += dbFun.getNextInnerCode(
					pub.yssGetTableName("Tb_Data_Trade"), 
					dbl.sqlRight("FNUM", 6), 
					fNumType, 
					" where FNum like 'T" + strNumDate + fNumType.substring(0, 1) + "%'", 
					1);
			//交易子表
			FNum += dbFun.getNextInnerCode(
					pub.yssGetTableName("Tb_Data_SubTrade"), 
					dbl.sqlRight("FNUM", 5), 
					"00000", 
					" where FNum like '" + FNum.replaceAll("'", "''") + "%'" );
			
			//递增
			String totle = FNum.substring(0,10);//编号头
			String tempNum = FNum.substring(10);//递增流水号
			long tmp = Long.valueOf(tempNum) + Max;//始终+递增
			FNum = totle + YssFun.formatNumber(tmp, "0000000000");
			//每次循环加1
			Max++;
		} catch (Exception e) {
			throw new YssException("获取交易数据流水号错误!");
		} finally {

		}
		//return FNum;
	}

	/**shashijie 2012-07-13 STORY 2727 获取当天申请数据SQL
	* @param dDate
	* @param fPort
	* @return*/
	private String getSqlToDay(Date dDate, String fPort) {
		String query = " " +
			" Select" +
			" f.Fholidayscode, " +//节假日
			" f.Fnormscale, " +//最小申赎份额
			" f.Fbsdifferenceover, " +//申购/赎回现金差额结转
			" f.Freplaceover, " +//申购现金替代结转
			" f.FSettleDays,"+//结算延迟天数
			" f.FMtReplaceOver,"+//赎回必须现金替代款结转
			" d.FCashBal As Fdvalue, " +//单位现金差额
			" d.FNetValue ," +//基金单位净值
			" NVL(e.FBraket,0) As Fmvalue, " +//篮子市值
			" a.Fportcode, " +//  组合代码
			" a.Ftradetypecode, " +//  业务类型
			" a.Fsecuritycode1, " +//  证券代码1
			" a.Fdate, " +//  数据日期
			" a.Fbargaindate, " +//  交易日期
			" a.Fbargainbs, " +//  买卖标志
			" a.Ffundsbar, " +//  资金账号
			" a.Fcurrencycode, " +//  币种
			/*" b.Fbrokercode, " +//  券商代码
			" b.Fseatcode, " +*///  席位代码
			" a.Fstockholdercode, " +// 股东代码
 
			" a.Ftradeamount, " +//  成交数量
			" a.Fsettleprice, " +//  结算价额
			" a.Ftradeprice, " +//  成交价格
			" a.Fclearmoney, " +//  清算金额
			" a.Ftotalmoney, " +//  实收实付
			" a.Fsettleamount, " +//  交收数量
			" a.Fstamptax, " +//  印花税
			" a.Fhandletax, " +// 经手费
			" a.Ftransfertax, " +// 过户费
			" a.Fcanaltax, " +//  证管费
			" a.Fproceduretax, " +//  手续费
			" a.Fothermoney1, " +// 其它金额1
			" a.Fothermoney2, " +// 其它金额2
			" a.Fothermoney3, " +//  其它金额3
			" a.Frecordtype "+//记录类型
			
			" From " +
 
			" ( Select " +
			" h.Fportcode, " +//  组合代码
			" h.Ftradetypecode," +//  业务类型
			" h.Fsecuritycode1, " +//  证券代码1
			" h.Fdate, " +//  数据日期
			" h.Fbargaindate, " +//  交易日期
			" h.Fbargainbs, " +//  买卖标志
			" h.Ffundsbar, " +//  资金账号
			" h.Fcurrencycode, " +//  币种
			" h.Fstockholdercode, " +// 股东代码
			//" h.Fseatnum," +//  席位代码
			" h.Frecordtype,"+//记录类型
			" Sum(h.Ftradeamount) Ftradeamount, " +//  成交数量
			" Sum(h.Fsettleprice) Fsettleprice, " +//  结算价额
			" Sum(h.Ftradeprice) Ftradeprice, " +//  成交价格
			" Sum(h.Fclearmoney) Fclearmoney, " +//  清算金额
			" Sum(h.Ftotalmoney) Ftotalmoney, " +//  实收实付
			" Sum(h.Fsettleamount) Fsettleamount, " +//  交收数量
			" Sum(h.Fstamptax) Fstamptax, " +//  印花税
			" Sum(h.Fhandletax) Fhandletax, " +// 经手费
			" Sum(h.Ftransfertax) Ftransfertax, " +// 过户费
			" Sum(h.Fcanaltax) Fcanaltax, " +//  证管费
			" Sum(h.Fproceduretax) Fproceduretax, " +//  手续费
			" Sum(h.Fothermoney1) Fothermoney1, " +// 其它金额1
			" Sum(h.Fothermoney2) Fothermoney2, " +// 其它金额2
			" Sum(h.Fothermoney3) Fothermoney3 " +//  其它金额3
			" From "+pub.yssGetTableName("Tb_Etf_Jsmx")+" h "+
  
			" Where h.Fsecuritytype = 'JJ'"+
			" And h.Fclearmark In ('276', ' ')"+
			" And h.Fdate = "+dbl.sqlDate(dDate)+
			" And h.Fcheckstate = 1"+
			" And h.FPortCode in ("+operSql.sqlCodes(fPort)+")"+
			//易方达002申请,华夏004确认数据
			" And h.Frecordtype in ('002','004')"+
			//add by songjie 2012.07.11 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
			" and h.Ftradeamount <> 0 " +
			" Group By "+
			" h.Fportcode, "+//  组合代码
			" h.Ftradetypecode, "+//  业务类型
			" h.Fsecuritycode1, "+//  证券代码1
			" h.Fdate, "+//  交易日期
			" h.Fbargainbs, "+//  买卖标志
			" h.Ffundsbar, "+//  资金账号
			" h.Fcurrencycode, "+//  币种  
			" h.Fbargaindate, "+//交易日期
			//" h.Fseatnum,"+//  席位代码
			" h.Fstockholdercode, "+// 股东代码
			" h.Frecordtype"+//记录类型
			
			" ) a "+
			
			//席位信息设置表
			/*" Join (Select A1.Fportcode, A5.Fseatnum, A5.Fbrokercode, A5.Fseatcode"+
			" From "+pub.yssGetTableName("Tb_Para_Portfolio")+" A1"+
			" Join (Select A2.Fsubcode, A2.Fportcode"+
			" From "+pub.yssGetTableName("Tb_Para_Portfolio_Relaship")+" A2"+
			" Where A2.Fportcode = "+dbl.sqlString(fPort)+
			" And A2.Frelatype = 'TradeSeat'" +
			" And A2.Fcheckstate = 1) A3 On A1.Fportcode = A3.Fportcode" +
			" Left Join (Select A4.Fseatcode, A4.Fseatnum, A4.Fbrokercode" +
			" From "+pub.yssGetTableName("Tb_Para_Tradeseat")+
			" A4) A5 On A3.Fsubcode = A5.Fseatcode" +
			" ) b On a.Fseatnum = b.Fseatnum"+*/
                                                                                       
			//财务估值表(单位差额)
			" Join (Select " +
			" b.FSecurityCode ,"+//证券代码
			" b.FBargainDate ,"+//交易日期
			" b.FNetValue ,"+//基金单位净值
            " b.FCashBal "+//现金差额
            " From "+pub.yssGetTableName("Tb_ETF_Difference")+" b "+
            /**shashijie 2012-6-28 STORY 2727 华夏ETF联接取与交易日同一天的单位现金差额 */
            /*" Where b.FBargainDate = "+dbl.sqlDate(dDate)+*/
            " ) d On a.FSecurityCode1 = d.FSecurityCode And a.FBargainDate = d.FBargainDate "+
			/**end*/
            
            /**shashijie 2012-6-27 STORY 2727 华夏的ETF联接基金不用到此表,所以这里改成 Left Join*/
            //财务估值表(篮子市值)
            " Left Join (Select" +
            /**end*/
            " b.FBraket ,"+//篮子估值
            " b.FDate ,"+//日期
            " b.FSecurityCode ,"+//证券代码
            " b.FCurrencyCode "+//币种
            " From "+pub.yssGetTableName("Tb_ETF_BraketMarket")+" b"+
          
            " Where b.Fdate = "+dbl.sqlDate(dDate)+
            " ) e On a.FSecurityCode1 = e.FSecurityCode "+
            //证券信息ETF基金
            " Left Join (Select F1.Fsecuritycode," +
            " F1.Fholidayscode," +
            " F1.Fnormscale," +
            " F1.Fbsdifferenceover," +
            " F1.FSettleDays,"+//STORY 2727 华夏
            " F1.FMtReplaceOver,"+//STORY 2727 华夏
            " F1.Freplaceover" +
            " From "+pub.yssGetTableName("Tb_Para_Security")+
            " F1) f On f.Fsecuritycode = a.Fsecuritycode1" +
            " Order By a.Fdate ";
		return query;
	}
	
	

}
