package com.yss.main.etfoperation.etfaccbook.GcAndJqpj;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.pojo.ETFEquityBean;
import com.yss.main.etfoperation.pojo.ETFSubTradeBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * @author fangjiang 2013.01.08 STORY #3402
 * fangjiang 2013.04.15 STORY #3848 ETF台账在处理权益时要用税前比率
 *
 */
public class BookDao extends CtlETFAccBook {
	
	public List<ETFTradeSettleDetailBean> querySecBOrSDetailList(String bs) throws YssException{
		List<ETFTradeSettleDetailBean> secBOrSDetailList = new ArrayList<ETFTradeSettleDetailBean>();
		ResultSet rs = null;
		String sql = "";
		try{
			//编号处理
			StringBuffer tmpbuff = new StringBuffer();
			String strNumDate = "";//保存申请编号	
		    long sNum = 0;
			strNumDate = YssFun.formatDatetime(this.bsDate).substring(0, 8);
			strNumDate = strNumDate
					+ dbFun.getNextInnerCode(pub.yssGetTableName("Tb_ETF_TradeStlDtl"), 
							dbl.sqlRight("FNUM", 6), "000000", " where FNum like 'T"
							+ strNumDate + "%'", 1);
			strNumDate = "T" + strNumDate;
			String s = strNumDate.substring(9, strNumDate.length());
			sNum = Long.parseLong(s);
			//---end 编号处理 ---
			sql = this.getSecBOrSDetailSql(bs); 
			rs = dbl.openResultSet(sql);
			while(rs.next()){
				//编号处理
				sNum++;				
				for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
					tmpbuff.append("0");
				}					
				strNumDate = strNumDate.substring(0, 9) + tmpbuff.toString() + sNum;
				tmpbuff.delete(0, tmpbuff.length());
				//---end 编号处理 ---		
				ETFTradeSettleDetailBean secBSDetailBean = this.toBSDetailBean(rs,strNumDate);
				secBOrSDetailList.add(secBSDetailBean);								
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return secBOrSDetailList;
	}
	
	public HashMap<String,ETFTradeSettleDetailBean> querySecBOrSDetailMap(String bs) throws YssException{
		HashMap<String,ETFTradeSettleDetailBean> secBOrSDetailMap = new HashMap<String,ETFTradeSettleDetailBean>();
		ResultSet rs = null;
		String sql = "";
		try{
			//编号处理
			StringBuffer tmpbuff = new StringBuffer();
			String strNumDate = "";//保存申请编号	
		    long sNum = 0;
			strNumDate = YssFun.formatDatetime(this.bsDate).substring(0, 8);
			strNumDate = strNumDate
					+ dbFun.getNextInnerCode(pub.yssGetTableName("Tb_ETF_TradeStlDtl"), 
							dbl.sqlRight("FNUM", 6), "000000", " where FNum like 'T"
							+ strNumDate + "%'", 1);
			strNumDate = "T" + strNumDate;
			String s = strNumDate.substring(9, strNumDate.length());
			sNum = Long.parseLong(s);
			//---end 编号处理 ---
			sql = this.getSecBOrSDetailSql(bs); 
			rs = dbl.openResultSet(sql);
			while(rs.next()){
				//编号处理
				sNum++;				
				for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
					tmpbuff.append("0");
				}					
				strNumDate = strNumDate.substring(0, 9) + tmpbuff.toString() + sNum;
				tmpbuff.delete(0, tmpbuff.length());
				//---end 编号处理 ---	
				ETFTradeSettleDetailBean secBOrSDetailBean = this.toBSDetailBean(rs,strNumDate);
				String key = secBOrSDetailBean.getBs() + "\t" + secBOrSDetailBean.getSecurityCode();
		
				secBOrSDetailMap.put(key, secBOrSDetailBean);							
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return secBOrSDetailMap;
	}

	public LinkedHashMap<String, List<ETFTradeSettleDetailBean>> queryTzzBOrSDetail(String bs) throws YssException{		
		LinkedHashMap<String, List<ETFTradeSettleDetailBean>> tzzBOrSDetailMap = new LinkedHashMap<String, List<ETFTradeSettleDetailBean>>();
		List<ETFTradeSettleDetailBean> tzzBSDetailList = null;
		ResultSet rs = null;
		String sql = "";
		try{
			//编号处理
			StringBuffer tmpbuff = new StringBuffer();
			String strNumDate = "";//保存申请编号	
		    long sNum = 0;
			strNumDate = YssFun.formatDatetime(this.bsDate).substring(0, 8);
			strNumDate = strNumDate
					+ dbFun.getNextInnerCode(pub.yssGetTableName("Tb_ETF_TradeStlDtl"), 
							dbl.sqlRight("FNUM", 6), "000000", " where FNum like 'T"
							+ strNumDate + "%'", 1);
			strNumDate = "T" + strNumDate;
			String s = strNumDate.substring(9, strNumDate.length());
			sNum = Long.parseLong(s);
			//---end 编号处理 ---		
			sql = this.getTzzBOrSDetailSql(bs);
			rs = dbl.openResultSet(sql);
			while(rs.next()){
				//编号处理
				sNum++;				
				for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
					tmpbuff.append("0");
				}					
				strNumDate = strNumDate.substring(0, 9) + tmpbuff.toString() + sNum;
				tmpbuff.delete(0, tmpbuff.length());
				//---end 编号处理 ---		
				ETFTradeSettleDetailBean tzzBSDetailBean = this.toBSDetailBean(rs,strNumDate);
				String key = tzzBSDetailBean.getBs() + "\t" + tzzBSDetailBean.getSecurityCode();
				if(tzzBOrSDetailMap.containsKey(key)){
					tzzBSDetailList = tzzBOrSDetailMap.get(key);
					tzzBSDetailList.add(tzzBSDetailBean);
				}else{
					tzzBSDetailList = new ArrayList<ETFTradeSettleDetailBean>();
					tzzBSDetailList.add(tzzBSDetailBean);
					tzzBOrSDetailMap.put(key, tzzBSDetailList);
				}
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return tzzBOrSDetailMap;
	}
	
	public List<ETFTradeSettleDetailBean> queryBOrSDetailList(String bs) throws YssException{
		List<ETFTradeSettleDetailBean> bOrsDetailList = new ArrayList<ETFTradeSettleDetailBean>();
		ResultSet rs = null;
		String sql = "";
		try{
			//编号处理
			StringBuffer tmpbuff = new StringBuffer();
			String strNumDate = "";//保存申请编号	
		    long sNum = 0;
			strNumDate = YssFun.formatDatetime(this.bsDate).substring(0, 8);
			strNumDate = strNumDate
					+ dbFun.getNextInnerCode(pub.yssGetTableName("Tb_ETF_TradeStlDtl"), 
							dbl.sqlRight("FNUM", 6), "000000", " where FNum like 'T"
							+ strNumDate + "%'", 1);
			strNumDate = "T" + strNumDate;
			String s = strNumDate.substring(9, strNumDate.length());
			sNum = Long.parseLong(s);
			//---end 编号处理 ---
			sql = this.getBOrSDetailSql(bs); 
			rs = dbl.openResultSet(sql);
			while(rs.next()){
				//编号处理
				sNum++;				
				for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
					tmpbuff.append("0");
				}					
				strNumDate = strNumDate.substring(0, 9) + tmpbuff.toString() + sNum;
				tmpbuff.delete(0, tmpbuff.length());
				//---end 编号处理 ---		
				ETFTradeSettleDetailBean bOrsDetailBean = this.toBSDetailBean(rs,strNumDate);
				bOrsDetailList.add(bOrsDetailBean);								
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return bOrsDetailList;
	}		
	
	public String getCjbhDetailSql(String bs) throws YssException{
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append(" SELECT ");
		sqlBuffer.append(" jsmx.fbargainbs,jsmx.fseatnum,jsmx.fclearcode, ");
		sqlBuffer.append(" jsmx.fstockholdercode,jsmx.fsettleamount,h.* ");
		sqlBuffer.append(" From ").append(pub.yssGetTableName("tb_etf_JSMXInterface")).append(" jsmx ");
		sqlBuffer.append(" Join ");
		sqlBuffer.append(" (Select s.Fportcode,s.Famount,s.Ftotalmoney, ");
		sqlBuffer.append(" s.Fsecuritycode,Mk.Fprice,Se.Ftradecury ");
		sqlBuffer.append(" From ").append(pub.yssGetTableName("Tb_Etf_Stocklist")).append(" s ");
		sqlBuffer.append(" Left Join (Select FPortCode, FSecurityCode, FPrice ");
		sqlBuffer.append(" From ").append(pub.yssGetTableName("Tb_Data_Pretvalmktprice"));
		sqlBuffer.append(" Where Fcheckstate = 1 ");
		sqlBuffer.append("  And Fvaldate = ").append(dbl.sqlDate(this.bsDate));
		sqlBuffer.append(" And Fportcode = ").append(dbl.sqlString(this.portCodes));
		sqlBuffer.append(" ) Mk On s.Fsecuritycode = Mk.Fsecuritycode And s.Fportcode = Mk.Fportcode ");
		sqlBuffer.append(" Left Join (Select Ftradecury, Fsecuritycode ");
		sqlBuffer.append(" From ").append(pub.yssGetTableName("Tb_Para_Security"));
		sqlBuffer.append(" Where Fcheckstate = 1) Se On s.Fsecuritycode = Se.Fsecuritycode ");
		sqlBuffer.append(" Where s.Fcheckstate = 1  And s.Fportcode = ").append(dbl.sqlString(this.portCodes));
		sqlBuffer.append("  And s.Fdate = ").append(dbl.sqlDate(this.bsDate));
		sqlBuffer.append("  And s.Freplacemark in ('1','3','5')) h On jsmx.Fportcode = h.FPortcode ");
		sqlBuffer.append(" Where jsmx.Fcheckstate = 1 ");
		sqlBuffer.append("  And jsmx.Fportcode = ").append(dbl.sqlString(this.portCodes));
		sqlBuffer.append(" And jsmx.fbargaindate = ").append(dbl.sqlDate(this.bsDate));
		sqlBuffer.append(" And jsmx.frecordtype = '003' ");
		if(bs.trim().length()>0){
			sqlBuffer.append(" And jsmx.fbargainbs = ").append(dbl.sqlString(bs));;
		}
		
		return sqlBuffer.toString();
	}
	
	public String getTzzBOrSDetailSql(String bs) throws YssException{
		StringBuffer s1 = new StringBuffer();
		
		s1.append(" select fbargainbs,' ' as fseatnum,' ' as fclearcode,fstockholdercode, ");
		s1.append(" fsecuritycode,Fprice,Ftradecury,sum(fsettleamount) as fsettleamount, Famount, Ftotalmoney ");
		s1.append(" from ( ");
		
		StringBuffer s2 = new StringBuffer();
		
		s2.append(" ) a group by fbargainbs,fsecuritycode,fstockholdercode,Fprice,Ftradecury,Famount,Ftotalmoney ");
		s2.append(" order by fbargainbs,fsecuritycode,fstockholdercode ");
		
		return s1.toString() + this.getCjbhDetailSql(bs) + s2.toString();
	}
	
	public String getSecBOrSDetailSql(String bs) throws YssException{
		StringBuffer s1 = new StringBuffer();
		
		s1.append(" select fbargainbs,' ' as fseatnum,' ' as fclearcode,' ' as fstockholdercode, ");
		s1.append(" fsecuritycode,Fprice,Ftradecury,sum(fsettleamount) as fsettleamount, Famount, Ftotalmoney ");
		s1.append(" from ( ");
		
		StringBuffer s2 = new StringBuffer();
		
		s2.append(" ) a group by fbargainbs,fsecuritycode,Fprice,Ftradecury,Famount,Ftotalmoney ");
		s2.append(" order by fbargainbs,fsecuritycode ");
		
		return s1.toString() + this.getCjbhDetailSql(bs) + s2.toString();
	}
	
	//modify by fangjiang 2013.04.23 把席位、结算会员号存起来
	public String getBOrSDetailSql(String bs) throws YssException{
		
		StringBuffer s1 = new StringBuffer();
		s1.append(" select * from ( ");		
		
		s1.append(" select fbargainbs,' ' as fseatnum,' ' as fclearcode,' ' as fstockholdercode, ");
		s1.append(" fsecuritycode,Fprice,Ftradecury,sum(fsettleamount) as fsettleamount, Famount, Ftotalmoney ");
		s1.append(" from ( ");
		
		StringBuffer s2 = new StringBuffer();		
		s2.append(" ) a group by fbargainbs,fsecuritycode,Fprice,Ftradecury,Famount,Ftotalmoney ");
		
		StringBuffer s3 = new StringBuffer();		
		s3.append(" union all ");
		
		s3.append(" select fbargainbs,fseatnum,fclearcode,fstockholdercode, ");
		s3.append(" fsecuritycode,Fprice,Ftradecury,sum(fsettleamount) as fsettleamount, Famount, Ftotalmoney ");
		s3.append(" from ( ");
		
		StringBuffer s4 = new StringBuffer();		
		s4.append(" ) a group by fbargainbs,fsecuritycode,fstockholdercode,Fprice,Ftradecury,Famount,Ftotalmoney,fseatnum,fclearcode");
		
		s4.append(" ) order by fbargainbs, fsecuritycode, fstockholdercode ");
		
		return s1.toString() + this.getCjbhDetailSql(bs) + s2.toString() + s3.toString() + this.getCjbhDetailSql(bs) + s4.toString();
	}
	
	public ETFTradeSettleDetailBean toBSDetailBean(ResultSet rs, String num) throws YssException {
		ETFTradeSettleDetailBean bsDetailBean = new ETFTradeSettleDetailBean();
		try{		
			bsDetailBean.setNum(num);//申请编号				
			bsDetailBean.setPortCode(this.portCodes);//组合代码
			bsDetailBean.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
			bsDetailBean.setStockHolderCode(rs.getString("fstockholdercode"));//投资者
			bsDetailBean.setBrokerCode(rs.getString("fclearcode"));//券商
			bsDetailBean.setSeatCode(rs.getString("fseatnum"));//交易席位
			bsDetailBean.setBs(rs.getString("fbargainbs"));//台账类型
			bsDetailBean.setBuyDate(this.bsDate);//申赎日期
			//替代数量 = (申赎份额/参数设置中基准比例)* 股票篮文件中证券数量
			bsDetailBean.setReplaceAmount(YssD.mul(YssD.div(rs.getDouble("fsettleamount"),
					                      this.paramSet.getNormScale()),rs.getDouble("FAmount")));
			bsDetailBean.setBraketNum(YssD.div(rs.getDouble("fsettleamount"),this.paramSet.getNormScale()));//篮子数
			//单位成本 
			bsDetailBean.setUnitCost(rs.getDouble("FPrice"));
			//基础汇率
			double baseRate = this.paramSetAdmin.getExchangeRateValue(
					this.bsDate, this.paramSet.getBaseRateSrcSSCode(), this.paramSet.getBaseRateSSCode(), 
					rs.getString("Ftradecury"), this.portCodes, YssOperCons.YSS_RATE_BASE);
			//组合汇率
			double portRate = this.paramSetAdmin.getExchangeRateValue(
					this.bsDate, this.paramSet.getPortRateSrcSSCode(), this.paramSet.getPortRateSSCode(), 
					rs.getString("Ftradecury"), this.portCodes, YssOperCons.YSS_RATE_PORT);
			bsDetailBean.setExchangeRate(YssD.div(baseRate, portRate));		
			if(bsDetailBean.getBs().equalsIgnoreCase("B")){ //申购			
				//替代金额本币
				bsDetailBean.setHReplaceCash(YssD.round(YssD.mul(rs.getDouble("FTotalMoney"),
						bsDetailBean.getBraketNum()),2));			
				//可退替代款本币 = 替代金额本币 - round(替代数量 *单位成本 *汇率,2)
				double gpsz = YssD.round(YssD.mul(bsDetailBean.getReplaceAmount(),
						                          bsDetailBean.getUnitCost(),
						                          bsDetailBean.getExchangeRate()
						                          ), 2); //股票市值
				bsDetailBean.setHcReplaceCash(YssD.sub(bsDetailBean.getHReplaceCash(), gpsz));		
			}else{		
				//应退赎回款（本币） = round(替代数量 *单位成本 *汇率,2)
				double gpsz = YssD.round(YssD.mul(bsDetailBean.getReplaceAmount(),
							                      bsDetailBean.getUnitCost(),
							                      bsDetailBean.getExchangeRate()
							                      ), 2); //股票市值
				bsDetailBean.setHReplaceCash(gpsz);
				//可退替代款本币 = 应退赎回款（本币）- round(替代数量 *单位成本 *汇率,2)
				bsDetailBean.setHcReplaceCash(0);
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}
		return bsDetailBean;
	}
	
	//获得标识为必需替代股票的替代金额
	public double getTdOfBXGP() throws YssException {
		StringBuffer sqlBuffer = new StringBuffer();
		ResultSet rs = null;
		double tdOfBXGP = 0.0;
		try{
			sqlBuffer.append(" select sum(ftotalmoney) as ftotalmoney ");
			sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_etf_stocklist"));
			sqlBuffer.append(" where Fcheckstate = 1 ");
			sqlBuffer.append(" and Fportcode = ").append(dbl.sqlString(this.portCodes));
			sqlBuffer.append(" and fdate = ").append(dbl.sqlDate(this.bsDate));
			sqlBuffer.append(" and freplacemark in ('6') ");
			rs = dbl.openResultSet(sqlBuffer.toString());
			while(rs.next()){
				tdOfBXGP = rs.getDouble("ftotalmoney");
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return tdOfBXGP;
	}
	
	//获得总的替代金额（除了标识为必需替代股票的替代金额）
	public HashMap<String, Double> getSumChildBTdFromJsmx() throws YssException {
		StringBuffer sqlBuffer = new StringBuffer();
		ResultSet rs = null;
		HashMap<String, Double> tzzSumTdFromJsmxMap = new HashMap<String, Double>();
		try{
			sqlBuffer.append(" select fstockholdercode,sum(fsettleamount) as fsettleamount,sum(fclearmoney) as fclearmoney ");
			sqlBuffer.append("  from ").append(pub.yssGetTableName("tb_etf_JSMXInterface"));
			sqlBuffer.append(" where Fcheckstate = 1 ");
			sqlBuffer.append(" and Fportcode = ").append(dbl.sqlString(this.portCodes));
			sqlBuffer.append(" and fbargaindate = ").append(dbl.sqlDate(this.bsDate));
			sqlBuffer.append(" and frecordtype = '003' and fbargainbs = 'B' group by fstockholdercode");
			rs = dbl.openResultSet(sqlBuffer.toString());
			while(rs.next()){
				double lzs = YssD.div(rs.getDouble("fsettleamount"), this.paramSet.getNormScale());
				String key = rs.getString("fstockholdercode");
				double value = YssD.sub(rs.getDouble("fclearmoney"), YssD.mul(lzs, this.getTdOfBXGP()));
				tzzSumTdFromJsmxMap.put(key, value);
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return tzzSumTdFromJsmxMap;
	}
	
	public List<ETFTradeSettleDetailBean> queryDetailForEquity(Date bsBeginDate, Date bsEndDate) throws YssException {
		List<ETFTradeSettleDetailBean> detailList = new ArrayList<ETFTradeSettleDetailBean>();
		ResultSet rs = null;
		String sql = "";
		try{	
			sql = this.getDetailSqlForEquity(bsBeginDate, bsEndDate);
			rs = dbl.openResultSet(sql);			
			while(rs.next()){				
				ETFTradeSettleDetailBean detailBean = this.toDetailBean(rs);
				this.setTargetDelRefForEquity(detailBean, rs);
				detailList.add(detailBean);				
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return detailList;
	}
	
	public String getDetailSqlForEquity(Date bsBeginDate, Date bsEndDate) throws YssException{
		StringBuffer s1 = new StringBuffer();
		
		s1.append(" select t.*, ");
		s1.append(" ref1.fsummakeupamount, ");
		s1.append(" ref1.fsumHCPReplaceCash, ");
		s1.append(" ref2.fsumsumamount, ");		
		s1.append(" ref2.fsumrealamount, ");
		s1.append(" ref2.fsumBBInterest, ");
		s1.append(" ref2.fsumBBWarrantCost, ");		
		s1.append(" se.FTradeCury ");
		s1.append(" from ");
		
		StringBuffer s2 = new StringBuffer();
		
		s2.append(" where t.fbuydate between ").append(dbl.sqlDate(bsBeginDate));
		s2.append(" and ").append(dbl.sqlDate(bsEndDate));
		s2.append(" and t.fportcode = ").append(dbl.sqlString(this.portCodes));
		s2.append(" and nvl(t.FReplaceAmount,0) + nvl(ref2.fsumrealamount,0) - nvl(ref1.fsummakeupamount,0) > 0 ");
		s2.append(" order by t.fbuydate,t.FBS,t.Fsecuritycode,t.FStockHolderCode ");
				
		return s1.toString() + this.getDtlAndRefSql(this.tradeDate) +
		       this.getSecInfoSql() + s2.toString();
	} 
	
	public void setTargetDelRefForEquity(ETFTradeSettleDetailBean detailBean, ResultSet rs) throws YssException {
		try{
			ETFTradeSettleDetailRefBean detailRefBean = new ETFTradeSettleDetailRefBean();
			detailRefBean.setNum(detailBean.getNum());
			detailRefBean.setExRightDate(this.tradeDate);
			//基础汇率
			double baseRate = this.paramSetAdmin.getExchangeRateValue(
					this.tradeDate, this.paramSet.getBaseRateSrcSSCode(), this.paramSet.getBaseRateSSCode(), 
					rs.getString("Ftradecury"), this.portCodes, YssOperCons.YSS_RATE_BASE);
			//组合汇率
			double portRate = this.paramSetAdmin.getExchangeRateValue(
					this.tradeDate, this.paramSet.getPortRateSrcSSCode(), this.paramSet.getPortRateSSCode(), 
					rs.getString("Ftradecury"), this.portCodes, YssOperCons.YSS_RATE_PORT);
			detailRefBean.setRightRate(YssD.div(baseRate, portRate));
			detailBean.setTargetDelRef(detailRefBean);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public HashMap<String, ETFEquityBean> queryEquityInfo(Date exrightdate) throws YssException {
		HashMap<String, ETFEquityBean> equityMap = new HashMap<String, ETFEquityBean>();
		ETFEquityBean  equityBean = null;
		ResultSet rs = null;
		String sql = "";
		try{	
			sql = this.getEquitySql(exrightdate);
			rs = dbl.openResultSet(sql);
			while(rs.next()){				 
				equityBean = this.toEquityBean(rs);	
				String key = equityBean.getSecurityCode();
				equityMap.put(key, equityBean);								
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return equityMap;
	}
	
	public String getEquitySql(Date exrightdate) {
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append(" select a.*, b.FPrice as fpgprice, c.FPrice as fzgprice ");
		sqlBuffer.append(" from (select decode(a.fexrightdate, ");
		sqlBuffer.append(" null, ");
		sqlBuffer.append(" decode(b.fdividenddate, ");
		sqlBuffer.append(" null, ");
		sqlBuffer.append(" c.fexrightdate, ");
		sqlBuffer.append(" b.fdividenddate), ");
		sqlBuffer.append(" a.fexrightdate) as fexrightdate, ");
		sqlBuffer.append(" decode(a.fssecuritycode, ");
		sqlBuffer.append(" null, ");
		sqlBuffer.append(" decode(b.fsecuritycode, ");
		sqlBuffer.append(" null, ");
		sqlBuffer.append(" c.fsecuritycode, ");
		sqlBuffer.append(" b.fsecuritycode), ");
		sqlBuffer.append(" a.fssecuritycode) as fsecuritycode, ");
		sqlBuffer.append(" a.FPreTaxRatio as FsgTaxRatio, ");
		sqlBuffer.append(" b.FPreTaxRatio as ffhTaxRatio, ");
		sqlBuffer.append(" b.FCuryCode as FfhCuryCode, ");
		sqlBuffer.append(" c.FPreTaxRatio as fpgTaxRatio, ");
		sqlBuffer.append(" c.FTSecurityCode, ");
		sqlBuffer.append(" c.FRICuryCode as FpgCuryCode, ");
		sqlBuffer.append(" c.FRIPrice ");
		sqlBuffer.append(" from (select fssecuritycode, ");
		sqlBuffer.append(" ftsecuritycode, ");
		sqlBuffer.append(" fexrightdate, ");
		sqlBuffer.append(" FPreTaxRatio ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_data_bonusshare"));
		sqlBuffer.append(" where FCheckState = 1 ");
		sqlBuffer.append(" and fssecuritycode = ftsecuritycode) a ");
		sqlBuffer.append(" full join (select fsecuritycode, ");
		sqlBuffer.append(" FPreTaxRatio, ");
		sqlBuffer.append(" FCuryCode, ");
		sqlBuffer.append(" FDividendDate ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_data_dividend"));
		sqlBuffer.append(" where FCheckState = 1) b on a.FExrightDate = ");
		sqlBuffer.append(" b.FDividendDate ");
		sqlBuffer.append(" and a.FSSecurityCode = ");
		sqlBuffer.append(" b.fsecuritycode ");
		sqlBuffer.append(" full join (select fsecuritycode, ");
		sqlBuffer.append(" FTSecurityCode, ");
		sqlBuffer.append(" FRICuryCode, ");
		sqlBuffer.append(" FExrightDate, ");
		sqlBuffer.append(" FPreTaxRatio, ");
		sqlBuffer.append(" FRIPrice ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_data_rightsissue"));
		sqlBuffer.append(" where FCheckState = 1 ");
		sqlBuffer.append(" and FExpirationDate > FExrightDate ");
		sqlBuffer.append(" and fsecuritycode <> FTSecurityCode) c on a.FExrightDate = ");
		sqlBuffer.append(" c.FExrightDate ");
		sqlBuffer.append(" and a.FSSecurityCode = ");
		sqlBuffer.append(" c.fsecuritycode ");
		sqlBuffer.append(" where a.fexrightdate = ").append(dbl.sqlDate(exrightdate));
		sqlBuffer.append(" or b.fdividenddate = ").append(dbl.sqlDate(exrightdate));
		sqlBuffer.append(" or c.fexrightdate = ").append(dbl.sqlDate(exrightdate));
		sqlBuffer.append(" ) a ");
		sqlBuffer.append(" left join (Select FSecurityCode, FPrice ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_data_pretvalmktprice"));
		sqlBuffer.append(" Where Fcheckstate = 1 ");
		sqlBuffer.append(" and Fvaldate = ").append(dbl.sqlDate(this.tradeDate));
		sqlBuffer.append(" and Fportcode = ").append(dbl.sqlString(this.portCodes));
		sqlBuffer.append(" ) b on a.FTSecurityCode = b.FSecurityCode ");
		sqlBuffer.append(" left join (Select FSecurityCode, FPrice ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_data_pretvalmktprice"));
		sqlBuffer.append(" Where Fcheckstate = 1 ");
		sqlBuffer.append(" and Fvaldate = ").append(dbl.sqlDate(this.tradeDate));
		sqlBuffer.append(" and Fportcode = ").append(dbl.sqlString(this.portCodes));
		sqlBuffer.append(" ) c on a.FSecurityCode = c.FSecurityCode ");

		return sqlBuffer.toString();		                     	
	}
	
	public ETFEquityBean toEquityBean(ResultSet rs) throws YssException {
		ETFEquityBean equityBean = new ETFEquityBean();
		try{		
			equityBean.setRightDate(this.tradeDate);
			equityBean.setSecurityCode(rs.getString("Fsecuritycode"));
			equityBean.setSgTaxRatio(rs.getDouble("FsgTaxRatio"));
			equityBean.setFhTaxRatio(rs.getDouble("FfhTaxRatio"));
			equityBean.setFhCuryCode(rs.getString("FfhCuryCode"));
			equityBean.setPgTaxRatio(rs.getDouble("FpgTaxRatio"));
			equityBean.setPgCuryCode(rs.getString("FpgCuryCode"));
			equityBean.setPgPrice(rs.getDouble("FRIPrice"));
			equityBean.setPgHq(rs.getDouble("FpgPrice"));
			equityBean.setZgHq(rs.getDouble("FzgPrice"));
			equityBean.setPgSecurityCode(rs.getString("FTSecurityCode"));
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}
		return equityBean;
	}
	
	public List<ETFTradeSettleDetailBean> queryDetailForUpdateQz(Date bsBeginDate, Date bsEndDate) throws YssException {
		List<ETFTradeSettleDetailBean> detailList = new ArrayList<ETFTradeSettleDetailBean>();
		ResultSet rs = null;
		String sql = "";
		try{	
			sql = this.getDetailSqlForUpdateQz(bsBeginDate, bsEndDate);
			rs = dbl.openResultSet(sql);			
			while(rs.next()){				
				ETFTradeSettleDetailBean detailBean = this.toDetailBeanForUpdateQz(rs);
				this.setTargetDelRefForUpdateQz(detailBean, rs);
				detailList.add(detailBean);				
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return detailList;
	}
	
	public String getDetailSqlForUpdateQz(Date bsBeginDate, Date bsEndDate) throws YssException{
		StringBuffer s1 = new StringBuffer();
		
		s1.append(" select t.*, ");
		s1.append(" ref3.FExRightDate, ");
		s1.append(" ref3.FRightRate, ");
		s1.append(" ref3.FSumAmount, ");		
		s1.append(" ref3.FRealAmount, ");
		s1.append(" ref3.FBBInterest, ");
		s1.append(" ref3.FBBWarrantCost, ");
		s1.append(" ref3.FRemaindAmount, ");
		s1.append(" r.FTSecurityCode, ");
		s1.append(" r.FRICuryCode, ");
		s1.append(" r.FPreTaxRatio as FTaxRatio, ");
		s1.append(" r.FRIPrice, ");
		s1.append(" se.FTradeCury, ");
		s1.append(" MkPg.fprice as fpgprice, ");
		s1.append(" Mk.fprice as fzgprice ");
		s1.append(" from ");
	       
		
		StringBuffer s2 = new StringBuffer();
		
		s2.append(" where t.fbuydate between ").append(dbl.sqlDate(bsBeginDate));
		s2.append(" and ").append(dbl.sqlDate(bsEndDate));
		s2.append(" and t.fportcode = ").append(dbl.sqlString(this.portCodes));
		s2.append(" and nvl(t.FReplaceAmount,0) + nvl(ref2.fsumrealamount,0) - nvl(ref1.fsummakeupamount,0) > 0 ");
		s2.append(" order by t.fbuydate,t.FBS,t.Fsecuritycode,t.FStockHolderCode ");
				
		return s1.toString() + this.getDtlAndRefSql(this.tradeDate) + 
		       this.getQyRefSql(this.tradeDate) + this.getQzInfo() +
		       this.getSecInfoSql() + this.getPgHqSql(this.tradeDate) +
		       this.getHqSql(this.tradeDate) + s2.toString();
	} 
	
	public ETFTradeSettleDetailBean toDetailBeanForUpdateQz(ResultSet rs) throws YssException {
		ETFTradeSettleDetailBean bsDetailBean = new ETFTradeSettleDetailBean();
		try{
			bsDetailBean.setBuyDate(rs.getDate("FBuyDate"));//申赎日期
			bsDetailBean.setNum(rs.getString("FNum"));//申请编号				
			bsDetailBean.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
			bsDetailBean.setStockHolderCode(rs.getString("fstockholdercode"));//投资者			
			bsDetailBean.setBs(rs.getString("fbs")); //申赎标识						
			bsDetailBean.setReplaceAmount(rs.getDouble("FReplaceAmount"));//替代数量
			bsDetailBean.setBraketNum(rs.getDouble("FBraketNum"));//篮子数
			bsDetailBean.setHReplaceCash(rs.getDouble("FHReplaceCash")); //替代金额
			bsDetailBean.setHcReplaceCash(rs.getDouble("FHCReplaceCash"));//可退替代款
			
			ETFTradeSettleDetailRefBean bsDetailRefBean = new ETFTradeSettleDetailRefBean();
			bsDetailRefBean.setExRightDate(rs.getDate("FExRightDate")); //除权日期
			bsDetailRefBean.setRightRate(rs.getDouble("FRightRate")); //权益汇率
			bsDetailRefBean.setSumAmount(rs.getDouble("FSumAmount")); //总数量
			bsDetailRefBean.setRealAmount(rs.getDouble("FRealAmount"));//实际数量
			bsDetailRefBean.setBbinterest(rs.getDouble("FBBInterest"));//总派息（本币）
			bsDetailRefBean.setBbwarrantCost(rs.getDouble("FBBWarrantCost"));//总权证价值（本币）			
			bsDetailRefBean.setRemaindAmount(rs.getDouble("FRemaindAmount"));
			bsDetailBean.setTradeSettleDelRef(bsDetailRefBean);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}
		return bsDetailBean;
	}
	
	public void setTargetDelRefForUpdateQz(ETFTradeSettleDetailBean detailBean, ResultSet rs) throws YssException {
		try{
			ETFTradeSettleDetailRefBean detailRefBean = new ETFTradeSettleDetailRefBean();
			detailRefBean.setNum(detailBean.getNum());
			detailRefBean.setExRightDate(detailBean.getTradeSettleDelRef().getExRightDate());
			detailRefBean.setRefNum(
				    YssFun.formatDate(detailRefBean.getExRightDate(), "yyyyMMdd") + 
					YssFun.formatDate(this.tradeDate, "yyyyMMdd"));
			detailRefBean.setSumAmount(detailBean.getTradeSettleDelRef().getSumAmount());
			detailRefBean.setRealAmount(detailBean.getTradeSettleDelRef().getRealAmount());
			detailRefBean.setBbinterest(detailBean.getTradeSettleDelRef().getBbinterest());
			//基础汇率
			double baseRate = this.paramSetAdmin.getExchangeRateValue(
					this.tradeDate, this.paramSet.getBaseRateSrcSSCode(), this.paramSet.getBaseRateSSCode(), 
					rs.getString("Ftradecury"), this.portCodes, YssOperCons.YSS_RATE_BASE);
			//组合汇率
			double portRate = this.paramSetAdmin.getExchangeRateValue(
					this.tradeDate, this.paramSet.getPortRateSrcSSCode(), this.paramSet.getPortRateSSCode(), 
					rs.getString("Ftradecury"), this.portCodes, YssOperCons.YSS_RATE_PORT);
			detailRefBean.setRightRate(YssD.div(baseRate, portRate));
			//计算权证价值
			double zgHq = rs.getDouble("fzgprice");
			double pgPrice = rs.getDouble("FRIPrice");
			double pgHq = rs.getDouble("fpgprice");
			double pgTaxRatio = rs.getDouble("FTaxRatio");
			double warrantCost = 0.0;
			double bbWarrantCost = 0.0;
			if(pgHq > 0){
				warrantCost = YssD.mul(YssD.add(detailBean.getReplaceAmount(), detailBean.getTradeSettleDelRef().getRemaindAmount()), 
						               pgTaxRatio,
						               zgHq);
			}else{
				if(pgPrice < zgHq){
					warrantCost = YssD.mul(YssD.add(detailBean.getReplaceAmount(), detailBean.getTradeSettleDelRef().getRemaindAmount()), 
				                           pgTaxRatio,
				                           YssD.sub(zgHq, pgPrice));
				}
			}
			bbWarrantCost = YssD.mul(warrantCost, detailRefBean.getRightRate());
			bbWarrantCost = YssD.round(bbWarrantCost, 2);
			detailRefBean.setBbwarrantCost(bbWarrantCost);
			detailRefBean.setRemaindAmount(detailBean.getTradeSettleDelRef().getRemaindAmount());
			
			detailBean.setTargetDelRef(detailRefBean);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public HashMap<String, ETFTradeSettleDetailBean> querySecBOrSDetailForGc(String bs) throws YssException {
		HashMap<String, ETFTradeSettleDetailBean> secBDetailMap = new HashMap<String, ETFTradeSettleDetailBean>();
		ETFTradeSettleDetailBean secBDetailBean = null;
		ResultSet rs = null;
		String sql = "";
		try{	
			sql = this.getSecBOrSDetailSqlForGc(bs);
			rs = dbl.openResultSet(sql);
			while(rs.next()){				 
				secBDetailBean = this.toDetailBean(rs);	
				this.setTargetDelRefForGc(secBDetailBean, rs);
				String key = secBDetailBean.getSecurityCode();
				secBDetailMap.put(key, secBDetailBean);								
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return secBDetailMap;
	}
	
	public String getSecBOrSDetailSqlForGc(String bs) throws YssException{
		StringBuffer s1 = new StringBuffer();
		
		s1.append(" select t.*, ");
		s1.append(" ref1.fsummakeupamount, ");
		s1.append(" ref1.fsumHCPReplaceCash, ");
		s1.append(" ref2.fsumsumamount, ");		
		s1.append(" ref2.fsumrealamount, ");
		s1.append(" ref2.fsumBBInterest, ");
		s1.append(" ref2.fsumBBWarrantCost, ");	
		s1.append(" mk.FPrice, ");
		s1.append(" ra.FBaseRate, ");
		s1.append(" ra.FPortRate ");
		s1.append(" from ");
		
		StringBuffer s2 = new StringBuffer();
		
		s2.append(" where t.fbuydate = ").append(dbl.sqlDate(this.bsDate));
		s2.append(" and t.fportcode = ").append(dbl.sqlString(this.portCodes));
		s2.append(" and t.fbs = ").append(dbl.sqlString(bs));
		s2.append(" and t.FStockHolderCode = ' ' ");
				
		return s1.toString() + this.getDtlAndRefSql(this.tradeDate) +
		       this.getSecInfoSql() + this.getHqSql(this.bsDate) + 
		       this.getRateSql(this.bsDate) + s2.toString();
	}
	
	public String getDtlAndRefSql(Date d) {
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append(pub.yssGetTableName("tb_etf_tradestldtl")).append(" t ");
		
		sqlBuffer.append(" left join ");
		sqlBuffer.append(" (select fnum, ");
		sqlBuffer.append(" sum(nvl(fmakeupamount, 0)) as fsummakeupamount, ");
		sqlBuffer.append(" sum(nvl(FHCPReplaceCash,0)) as fsumHCPReplaceCash ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
		sqlBuffer.append(" where fmakeupdate <= ").append(dbl.sqlDate(d));
		sqlBuffer.append(" and to_char(fexrightdate, 'yyyymmdd') = '99981231' ");
		sqlBuffer.append(" group by fnum) ref1 on t.fnum = ref1.fnum ");
		
		sqlBuffer.append(" left join ");
		sqlBuffer.append(" (select a.fnum as fnum, ");
		sqlBuffer.append(" sum(nvl(FSumAmount, 0)) as fsumsumamount, ");
		sqlBuffer.append(" sum(nvl(frealamount, 0)) as fsumrealamount, ");
		sqlBuffer.append(" sum(nvl(FBBInterest, 0)) as fsumBBInterest, ");
		sqlBuffer.append(" sum(nvl(FBBWarrantCost, 0)) as fsumBBWarrantCost ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
		sqlBuffer.append(" a join ");
		sqlBuffer.append(" (select fnum, ");
		sqlBuffer.append(" fexrightdate, ");
		sqlBuffer.append(" max(to_date(substr(frefnum, 9), 'yyyymmdd')) as fdate ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
		sqlBuffer.append(" where to_char(fmakeupdate, 'yyyymmdd') = '99981231' ");
		sqlBuffer.append(" and to_date(substr(frefnum, 9), 'yyyymmdd') <= ").append(dbl.sqlDate(d));
		sqlBuffer.append(" group by fnum, fexrightdate) b on a.fnum = b.fnum ");
		sqlBuffer.append(" and a.fexrightdate = b.fexrightdate ");
		sqlBuffer.append(" and to_date(substr(a.frefnum, 9),'yyyymmdd') = b.fdate ");
		sqlBuffer.append(" group by a.fnum) ref2 on t.fnum = ref2.fnum ");
		
		return sqlBuffer.toString();
	}
	
	public String getQyRefSql(Date d) {
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append(" join ");
		sqlBuffer.append(" (select a.* ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
		sqlBuffer.append(" a join ");
		sqlBuffer.append(" (select fnum, ");
		sqlBuffer.append(" fexrightdate, ");
		sqlBuffer.append(" max(to_date(substr(frefnum, 9), 'yyyymmdd')) as fdate ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
		sqlBuffer.append(" where to_char(fmakeupdate, 'yyyymmdd') = '99981231' ");
		sqlBuffer.append(" and to_date(substr(frefnum, 9), 'yyyymmdd') < ").append(dbl.sqlDate(d));
		sqlBuffer.append(" group by fnum, fexrightdate) b on a.fnum = b.fnum ");
		sqlBuffer.append(" and a.fexrightdate = b.fexrightdate ");
		sqlBuffer.append(" and to_date(substr(a.frefnum, 9),'yyyymmdd') = b.fdate ");
		sqlBuffer.append(" ) ref3 on t.fnum = ref3.fnum ");
		
		return sqlBuffer.toString();
	}
	
	public String getQzInfo(){
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append(" join ");
		sqlBuffer.append(" (select fsecuritycode, ");
		sqlBuffer.append(" FTSecurityCode, ");
		sqlBuffer.append(" FRICuryCode, ");
		sqlBuffer.append(" FExrightDate, ");
		sqlBuffer.append(" FPreTaxRatio, ");
		sqlBuffer.append(" FRIPrice ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_data_rightsissue"));
		sqlBuffer.append(" where FCheckState = 1 ");
		sqlBuffer.append(" and FExpirationDate > FExrightDate ");
		sqlBuffer.append(" and fsecuritycode <> FTSecurityCode) r ");
		sqlBuffer.append(" on t.fsecuritycode = r.fsecuritycode ");
		sqlBuffer.append(" and ref3.fexrightdate = r.FExrightDate ");
		
		return sqlBuffer.toString();
	}
	
	public String getSecInfoSql() {
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append(" left join (select FTradeCury, fsecuritycode ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_para_security"));
		sqlBuffer.append(" where FCheckState = 1) se on t.fsecuritycode = se.fsecuritycode ");
		
		return sqlBuffer.toString();
	}
	
	public String getHqSql(Date d) {
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append(" left join (Select FSecurityCode, FPrice ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_data_pretvalmktprice"));
		sqlBuffer.append(" Where Fcheckstate = 1 ");
		sqlBuffer.append(" and Fvaldate = ").append(dbl.sqlDate(d));
		sqlBuffer.append(" and Fportcode = ").append(dbl.sqlString(this.portCodes));
		sqlBuffer.append(" ) Mk on t.Fsecuritycode = Mk.Fsecuritycode ");
		
		return sqlBuffer.toString();
	}
	
	public String getPgHqSql(Date d) {
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append(" left join (Select FSecurityCode, FPrice ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_data_pretvalmktprice"));
		sqlBuffer.append(" Where Fcheckstate = 1 ");
		sqlBuffer.append(" and Fvaldate = ").append(dbl.sqlDate(d));
		sqlBuffer.append(" and Fportcode = ").append(dbl.sqlString(this.portCodes));
		sqlBuffer.append(" ) MkPg on r.FTsecuritycode = MkPg.Fsecuritycode ");
		
		return sqlBuffer.toString();
	}
	
	public String getRateSql(Date d) {
		StringBuffer sqlBuffer = new StringBuffer();
				
		sqlBuffer.append(" left join (Select FBaseRate, FPortRate, FCuryCode ");
		sqlBuffer.append(" from ").append(pub.yssGetTableName("tb_data_pretvalrate"));
		sqlBuffer.append(" Where Fcheckstate = 1 ");
		sqlBuffer.append(" and Fvaldate = ").append(dbl.sqlDate(d));
		sqlBuffer.append(" and Fportcode = ").append(dbl.sqlString(this.portCodes));
		sqlBuffer.append(" ) ra on se.FTradeCury = ra.FCuryCode ");
		
		return sqlBuffer.toString();
	}
	
	public ETFTradeSettleDetailBean toDetailBean(ResultSet rs) throws YssException {
		ETFTradeSettleDetailBean bsDetailBean = new ETFTradeSettleDetailBean();
		try{
			bsDetailBean.setBuyDate(rs.getDate("FBuyDate"));//申赎日期
			bsDetailBean.setNum(rs.getString("FNum"));//申请编号				
			bsDetailBean.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
			bsDetailBean.setStockHolderCode(rs.getString("fstockholdercode"));//投资者			
			bsDetailBean.setBs(rs.getString("fbs")); //申赎标识						
			bsDetailBean.setReplaceAmount(rs.getDouble("FReplaceAmount"));//替代数量
			bsDetailBean.setBraketNum(rs.getDouble("FBraketNum"));//篮子数
			bsDetailBean.setHReplaceCash(rs.getDouble("FHReplaceCash")); //替代金额
			bsDetailBean.setHcReplaceCash(rs.getDouble("FHCReplaceCash"));//可退替代款
			
			ETFTradeSettleDetailRefBean bsDetailRefBean = new ETFTradeSettleDetailRefBean();
			bsDetailRefBean.setSumAmount(rs.getDouble("fsumsumamount")); //总数量
			bsDetailRefBean.setRealAmount(rs.getDouble("fsumrealamount"));//实际数量
			bsDetailRefBean.setBbinterest(rs.getDouble("fsumBBInterest"));//总派息（本币）
			bsDetailRefBean.setBbwarrantCost(rs.getDouble("fsumBBWarrantCost"));//总权证价值（本币）			
			bsDetailRefBean.setMakeUpAmount(rs.getDouble("fsummakeupamount"));//补票数量
			bsDetailRefBean.setHcReplaceCash(rs.getDouble("fsumHCPReplaceCash"));
			bsDetailBean.setTradeSettleDelRef(bsDetailRefBean);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}
		return bsDetailBean;
	}
	
	public void setTargetDelRefForGc(ETFTradeSettleDetailBean bsDetailBean, ResultSet rs) throws YssException {
		try{
			ETFTradeSettleDetailRefBean bsDetailRefBean = new ETFTradeSettleDetailRefBean();
			bsDetailRefBean.setNum(bsDetailBean.getNum());
			bsDetailRefBean.setMakeUpDate(this.bsDate);
			bsDetailRefBean.setUnitCost(rs.getDouble("FPrice"));
			bsDetailRefBean.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"), rs.getDouble("FPortRate")));
			bsDetailBean.setTargetDelRef(bsDetailRefBean);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public HashMap<String, List<ETFTradeSettleDetailBean>> queryTzzBSDetailForGc() throws YssException {
		HashMap<String, List<ETFTradeSettleDetailBean>> tzzBSDetailMap = new HashMap<String, List<ETFTradeSettleDetailBean>>();
		List<ETFTradeSettleDetailBean> tzzBSDetailList = null;
		ResultSet rs = null;
		String sql = "";
		try{	
			sql = this.getTzzBSDetailSqlForGc();
			rs = dbl.openResultSet(sql);			
			while(rs.next()){				
				ETFTradeSettleDetailBean tzzBSDetailBean = this.toDetailBean(rs);
				this.setTargetDelRefForGc(tzzBSDetailBean, rs);
				String key = tzzBSDetailBean.getBs() + "\t" + tzzBSDetailBean.getSecurityCode();
				if(tzzBSDetailMap.containsKey(key)){
					tzzBSDetailList = tzzBSDetailMap.get(key);
					tzzBSDetailList.add(tzzBSDetailBean);
				}else{
					tzzBSDetailList = new ArrayList<ETFTradeSettleDetailBean>();
					tzzBSDetailList.add(tzzBSDetailBean);
					tzzBSDetailMap.put(key, tzzBSDetailList);
				}
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return tzzBSDetailMap;
	}
	
	public String getTzzBSDetailSqlForGc() throws YssException{
		StringBuffer s1 = new StringBuffer();
		
		s1.append(" select t.*, ");
		s1.append(" ref1.fsummakeupamount, ");
		s1.append(" ref1.fsumHCPReplaceCash, ");
		s1.append(" ref2.fsumsumamount, ");		
		s1.append(" ref2.fsumrealamount, ");
		s1.append(" ref2.fsumBBInterest, ");
		s1.append(" ref2.fsumBBWarrantCost, ");	
		s1.append(" mk.FPrice, ");
		s1.append(" ra.FBaseRate, ");
		s1.append(" ra.FPortRate ");
		s1.append(" from ");
		
		StringBuffer s2 = new StringBuffer();
		
		s2.append(" where t.fbuydate = ").append(dbl.sqlDate(this.bsDate));
		s2.append(" and t.fportcode = ").append(dbl.sqlString(this.portCodes));
		s2.append(" and t.FStockHolderCode <> ' ' ");
		s2.append(" order by t.FBS,t.Fsecuritycode,t.FStockHolderCode ");
				
		return s1.toString() + this.getDtlAndRefSql(this.tradeDate) +
		       this.getSecInfoSql() + this.getHqSql(this.bsDate) + 
		       this.getRateSql(this.bsDate) + s2.toString();
	}
	
	public HashMap<Date, List<ETFTradeSettleDetailBean>> queryBSDetailForJqpj(Date bsBeginDate, Date bsEndDate) throws YssException {
		HashMap<Date, List<ETFTradeSettleDetailBean>> bsDetailMap = new LinkedHashMap<Date, List<ETFTradeSettleDetailBean>>();
		List<ETFTradeSettleDetailBean> bsDetailList = null;
		ResultSet rs = null;
		String sql = "";
		try{	
			sql = this.getBSDetailSqlForJqpj(bsBeginDate, bsEndDate);
			rs = dbl.openResultSet(sql);			
			while(rs.next()){				
				ETFTradeSettleDetailBean bsDetailBean = this.toDetailBean(rs);
				this.setTargetDelRefForJqpj(bsDetailBean, rs);
				Date key = bsDetailBean.getBuyDate();
				if(bsDetailMap.containsKey(key)){
					bsDetailList = bsDetailMap.get(key);
					bsDetailList.add(bsDetailBean);
				}else{
					bsDetailList = new ArrayList<ETFTradeSettleDetailBean>();
					bsDetailList.add(bsDetailBean);
					bsDetailMap.put(key, bsDetailList);
				}
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return bsDetailMap;
	}
	
	public String getBSDetailSqlForJqpj(Date bsBeginDate, Date bsEndDate) throws YssException{
		StringBuffer s1 = new StringBuffer();
		
		s1.append(" select t.*, ");
		s1.append(" ref1.fsummakeupamount, ");
		s1.append(" ref1.fsumHCPReplaceCash, ");
		s1.append(" ref2.fsumsumamount, ");		
		s1.append(" ref2.fsumrealamount, ");
		s1.append(" ref2.fsumBBInterest, ");
		s1.append(" ref2.fsumBBWarrantCost, ");	
		s1.append(" se.FTradeCury ");
		s1.append(" from ");
		
		StringBuffer s2 = new StringBuffer();
		
		s2.append(" where t.fbuydate between ").append(dbl.sqlDate(bsBeginDate));
		s2.append(" and ").append(dbl.sqlDate(bsEndDate));
		s2.append(" and t.fportcode = ").append(dbl.sqlString(this.portCodes));
		s2.append(" and nvl(t.FReplaceAmount,0) + nvl(ref2.fsumrealamount,0) - nvl(ref1.fsummakeupamount,0) > 0 ");
		s2.append(" order by t.fbuydate,t.FBS,t.Fsecuritycode,t.FStockHolderCode ");
				
		return s1.toString() + this.getDtlAndRefSql(this.tradeDate) +
		       this.getSecInfoSql() + s2.toString();
	} 
	
	public void setTargetDelRefForJqpj(ETFTradeSettleDetailBean bsDetailBean, ResultSet rs) throws YssException {
		try{
			ETFTradeSettleDetailRefBean bsDetailRefBean = new ETFTradeSettleDetailRefBean();
			bsDetailRefBean.setNum(bsDetailBean.getNum());
			bsDetailRefBean.setMakeUpDate(this.tradeDate);
			//基础汇率
			double baseRate = this.paramSetAdmin.getExchangeRateValue(
					this.tradeDate, this.paramSet.getBaseRateSrcBPCode(), this.paramSet.getBaseRateBPCode(), 
					rs.getString("Ftradecury"), this.portCodes, YssOperCons.YSS_RATE_BASE);
			//组合汇率
			double portRate = this.paramSetAdmin.getExchangeRateValue(
					this.tradeDate, this.paramSet.getPortRateSrcBPCode(), this.paramSet.getPortRateBPCode(), 
					rs.getString("Ftradecury"), this.portCodes, YssOperCons.YSS_RATE_PORT);
			bsDetailRefBean.setExchangeRate(YssD.div(baseRate, portRate));
			bsDetailBean.setTargetDelRef(bsDetailRefBean);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public List<ETFTradeSettleDetailBean> querySecBSDetailForQz(Date bsDate) throws YssException {
		List<ETFTradeSettleDetailBean> secBSDetailList = new ArrayList<ETFTradeSettleDetailBean>();
		ETFTradeSettleDetailBean secBSDetailBean = null;
		ResultSet rs = null;
		String sql = "";
		try{	
			sql = this.getSecBSDetailSqlForQz(bsDate);
			rs = dbl.openResultSet(sql);
			while(rs.next()){				 
				secBSDetailBean = this.toDetailBean(rs);	
				this.setTargetDelRefForQz(secBSDetailBean, rs);
				secBSDetailList.add(secBSDetailBean);						
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return secBSDetailList;
	}
	
	public String getSecBSDetailSqlForQz(Date bsDate) throws YssException{
		StringBuffer s1 = new StringBuffer();
		
		s1.append(" select t.*, ");
		s1.append(" ref1.fsummakeupamount, ");
		s1.append(" ref1.fsumHCPReplaceCash, ");
		s1.append(" ref2.fsumsumamount, ");		
		s1.append(" ref2.fsumrealamount, ");
		s1.append(" ref2.fsumBBInterest, ");
		s1.append(" ref2.fsumBBWarrantCost, ");	
		s1.append(" se.FTradeCury, ");
		s1.append(" mk.FPrice ");
		s1.append(" from ");
		
		StringBuffer s2 = new StringBuffer();
		
		s2.append(" where t.fbuydate = ").append(dbl.sqlDate(bsDate));
		s2.append(" and t.fportcode = ").append(dbl.sqlString(this.portCodes));
		s2.append(" and t.FStockHolderCode = ' ' ");
		s2.append(" and nvl(t.FReplaceAmount,0) + nvl(ref2.fsumrealamount,0) - nvl(ref1.fsummakeupamount,0) > 0 ");
				
		return s1.toString() + this.getDtlAndRefSql(this.tradeDate) +
		       this.getSecInfoSql() + this.getHqSql(this.tradeDate) + s2.toString();
	}
	
	public void setTargetDelRefForQz(ETFTradeSettleDetailBean bsDetailBean, ResultSet rs) throws YssException {
		try{
			ETFTradeSettleDetailRefBean bsDetailRefBean = new ETFTradeSettleDetailRefBean();
			bsDetailRefBean.setNum(bsDetailBean.getNum());
			bsDetailRefBean.setMakeUpDate(this.tradeDate);
			bsDetailRefBean.setUnitCost(rs.getDouble("FPrice"));
			//基础汇率
			double baseRate = this.paramSetAdmin.getExchangeRateValue(
					this.tradeDate, this.paramSet.getBaseRateSrcBPCode(), this.paramSet.getBaseRateBPCode(), 
					rs.getString("Ftradecury"), this.portCodes, YssOperCons.YSS_RATE_BASE);
			//组合汇率
			double portRate = this.paramSetAdmin.getExchangeRateValue(
					this.tradeDate, this.paramSet.getPortRateSrcBPCode(), this.paramSet.getPortRateBPCode(), 
					rs.getString("Ftradecury"), this.portCodes, YssOperCons.YSS_RATE_PORT);
			bsDetailRefBean.setExchangeRate(YssD.div(baseRate, portRate));
			bsDetailBean.setTargetDelRef(bsDetailRefBean);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public HashMap<String, List<ETFTradeSettleDetailBean>> queryTzzBSDetailForQz(Date bsDate) throws YssException {
		
		HashMap<String, List<ETFTradeSettleDetailBean>> tzzBSDetailMap = new HashMap<String, List<ETFTradeSettleDetailBean>>();
		List<ETFTradeSettleDetailBean> tzzBSDetailList = null;
		ResultSet rs = null;
		String sql = "";
		try{	
			sql = this.getTzzBSDetailSqlForQz(bsDate);
			rs = dbl.openResultSet(sql);			
			while(rs.next()){				
				ETFTradeSettleDetailBean tzzBSDetailBean = this.toDetailBean(rs);
				this.setTargetDelRefForQz(tzzBSDetailBean, rs);
				String key = tzzBSDetailBean.getBs() + "\t" + tzzBSDetailBean.getSecurityCode();
				if(tzzBSDetailMap.containsKey(key)){
					tzzBSDetailList = tzzBSDetailMap.get(key);
					tzzBSDetailList.add(tzzBSDetailBean);
				}else{
					tzzBSDetailList = new ArrayList<ETFTradeSettleDetailBean>();
					tzzBSDetailList.add(tzzBSDetailBean);
					tzzBSDetailMap.put(key, tzzBSDetailList);
				}
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return tzzBSDetailMap;
	}
	
	public String getTzzBSDetailSqlForQz(Date bsDate) throws YssException{
		StringBuffer s1 = new StringBuffer();
		
		s1.append(" select t.*, ");
		s1.append(" ref1.fsummakeupamount, ");
		s1.append(" ref1.fsumHCPReplaceCash, ");
		s1.append(" ref2.fsumsumamount, ");		
		s1.append(" ref2.fsumrealamount, ");
		s1.append(" ref2.fsumBBInterest, ");
		s1.append(" ref2.fsumBBWarrantCost, ");	
		s1.append(" se.FTradeCury, ");
		s1.append(" mk.FPrice ");
		s1.append(" from ");
		
		StringBuffer s2 = new StringBuffer();
		
		s2.append(" where t.fbuydate = ").append(dbl.sqlDate(bsDate));
		s2.append(" and t.fportcode = ").append(dbl.sqlString(this.portCodes));
		s2.append(" and t.FStockHolderCode <> ' ' ");
		s2.append(" and nvl(t.FReplaceAmount,0) + nvl(ref2.fsumrealamount,0) - nvl(ref1.fsummakeupamount,0) > 0 ");
		s2.append(" order by t.FBS,t.Fsecuritycode,t.FStockHolderCode ");
				
		return s1.toString() + this.getDtlAndRefSql(this.tradeDate) +
		       this.getSecInfoSql() + this.getHqSql(this.tradeDate) + s2.toString();
	}
	
	public HashMap<String, ETFSubTradeBean> queryBPData() throws YssException {
		HashMap<String, ETFSubTradeBean> bpDataMap = new HashMap<String, ETFSubTradeBean>();
		ETFSubTradeBean bpDataBean = null;
		ResultSet rs = null;
		String sql = "";
		try{	
			sql = this.getBPData();
			rs = dbl.openResultSet(sql);			
			while(rs.next()){				
				bpDataBean = this.toBPDataBean(rs);
				String key = "";
				if("01".equalsIgnoreCase(bpDataBean.getTradeCode())){
					key = "B" + "\t" + bpDataBean.getSecurityCode();
				}else{
					key = "S" + "\t" + bpDataBean.getSecurityCode();
				}								
				bpDataMap.put(key, bpDataBean);				
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			dbl.closeResultSetFinal(rs);
		}
		return bpDataMap;
	}
	
	public String getBPData() throws YssException{
		StringBuffer s1 = new StringBuffer();
		
		s1.append(" select t.fsecuritycode, ");
		s1.append(" t.ftradetypecode, ");
		s1.append(" s.ftradecury, ");
		s1.append(" sum(t.FTradeAmount) as FTradeAmount, ");
		s1.append(" sum(t.ftotalcost) as ftotalcost ");
		s1.append(" from ").append(pub.yssGetTableName("tb_data_subtrade"));
		s1.append(" t join (select fsecuritycode, ftradecury ");
		s1.append(" from ").append(pub.yssGetTableName("tb_para_security"));
		s1.append(" where fcheckstate = 1 ");
		s1.append(" ) s on t.fsecuritycode = s.fsecuritycode ");
		s1.append(" where t.fbargaindate = ").append(dbl.sqlDate(this.tradeDate));
		s1.append(" and t.fcheckstate = 1 and t.ftradetypecode in ('01','02') ");
		s1.append(" and t.fportcode = ").append(dbl.sqlString(this.portCodes));
		s1.append(" group by t.fsecuritycode, t.ftradetypecode, s.ftradecury ");
				
		return s1.toString();
	}
	
	public ETFSubTradeBean toBPDataBean(ResultSet rs) throws YssException {
		ETFSubTradeBean bpDataBean = new ETFSubTradeBean();
		try{
			bpDataBean.setSecurityCode(rs.getString("fsecuritycode"));
			bpDataBean.setTradeCode(rs.getString("ftradetypecode"));
			bpDataBean.setTradeAmount(rs.getDouble("FTradeAmount"));
			bpDataBean.setTotalCost(rs.getDouble("ftotalcost"));
			bpDataBean.setFz(rs.getDouble("ftotalcost"));
			bpDataBean.setFm(rs.getDouble("FTradeAmount"));
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}
		return bpDataBean;
	}
		
}
