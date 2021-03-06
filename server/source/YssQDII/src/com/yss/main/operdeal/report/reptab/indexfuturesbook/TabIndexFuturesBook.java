package com.yss.main.operdeal.report.reptab.indexfuturesbook;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.base.BaseAPOperValue;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.reptab.indexfuturesbook.pojo.IndexFuturesDataBean;
import com.yss.main.operdeal.report.reptab.indexfuturesbook.pojo.IndexFuturesStockBean;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

//期货台帐表
public class TabIndexFuturesBook extends BaseAPOperValue {

	private java.util.Date dBeginDate;
	private java.util.Date dEndDate;
	private String portCode;
	private String brokeCode;
	private boolean isCreate; 
	private BaseOperDeal settingOper;
	//期货信息
	private class IndexFuturesBean{
		
		private String  sMarketCode = "";       //期货合约上市代码
		private int     iMulTiple = 0;          //合约乘数
		private String  sBailType = "";         //保证金类型：Fix-每手固定  Scale-比例
		private double  dBailScale = 0;         //保证金比例
		private double  dBailFix = 0;           //每手固定保证金
		private String sTradeCury = "";         //交易货币  预留字段(目前只针对中金所，以后如果也针对国外的则需要通过判断交易货币)
		private String sHolidaysCode = "";
		
		public IndexFuturesBean(){
			
		}
	}
	
	private class CarryOverbean{
		
		private Date      dDate = null;               //期货交易日期
	    private String    sSecuritycode = "";         //合约代码
	    private String    sPortCode = "";             //组合代码
	    private String    sBrokerCode = "";           //券商代码
	    private String    sFuType = "";               //开仓类型
	    private double    dCarryOverMV = 0;           
		private double    dAdjustMargins = 0;
		public CarryOverbean(){
			
		}
	}
	
	public TabIndexFuturesBook(){
		
	}
	
	// ============================================================================================
	public void init(Object bean) throws YssException {
		String reqAry[] = null;
		String reqAry1[] = null;

		String sRowStr = (String) bean;
		if (sRowStr.trim().length() == 0) {
			return;
		}
		reqAry = sRowStr.split("\n");
		reqAry1 = reqAry[0].split("\r");
		this.dBeginDate = YssFun.toDate(reqAry1[1]);
		reqAry1 = reqAry[1].split("\r");
		this.dEndDate = YssFun.toDate(reqAry1[1]);
		reqAry1 = reqAry[2].split("\r");
		this.portCode = reqAry1[1];
		if(reqAry.length==4){
			reqAry1 = reqAry[3].split("\r");
			if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0，则只查询已生成的报表数据
				this.isCreate = false;
			} else { // 生成报表
				this.isCreate = true;
			}	
			this.brokeCode = " ";
		}else{
			reqAry1 = reqAry[3].split("\r");
			this.brokeCode = reqAry1[1];
			reqAry1 = reqAry[4].split("\r");
			if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0，则只查询已生成的报表数据
				this.isCreate = false;
			} else { // 生成报表
				this.isCreate = true;
			}	
		}
	}

	public Object invokeOperMothed() throws YssException {
		HashMap valueMap = null;
		
		try{
			if(isCreate){
				processDealIndexFuturesBookData();
			}
		}catch(Exception e){
			throw new YssException("生成期货台帐报表出错......"+e.getMessage());
		}		

		return "";
	}

	
	public BaseOperDeal getSettingOper() {

        settingOper = new BaseOperDeal();
        settingOper.setYssPub(pub);
        return settingOper;
    }
	// ============================================================================================
	
	private void processDealIndexFuturesBookData()throws YssException{
		 
		 HashMap futuresInfoMap = null;
		 HashMap futuresStockMap = null;
		 HashMap futuresTradeDataMap = null;
		 HashMap FuturesStockDataMap = null;
		 
		 deleteIndexFuturesData();
		 deleteIndexFutureStock();
		 
		 futuresInfoMap = initFuturesInfo();
		 futuresStockMap = initFutureStockData();
		 futuresTradeDataMap = dealFuturesTradeData(futuresInfoMap,futuresStockMap);
		 FuturesStockDataMap = dealFuturesStockData(futuresInfoMap,futuresTradeDataMap,futuresStockMap);
		 
		 insertToIndexFuturesData(futuresTradeDataMap);
		 insertToIndexFutureStock(FuturesStockDataMap);
		 
	 }
	
	// 初始化期货基本信息
	 private HashMap initFuturesInfo()throws YssException{
		 
		 IndexFuturesBean futuresInfo = null;
		 ResultSet rs = null;
		 StringBuffer buf = new StringBuffer();
		 HashMap valueMap = null;
		 
		 int count =0;
		 try{
			 /*************************************************************************************
			  *  获取期货基本信息
			  *    查询出期货交易数据，
			  *    再通过期货代码查询出相应的期货的基本信息
			  */
			 buf.append(" select b.ffutype,b.fmultiple,b.fbailtype,b.fbailfix,b.fbailscale,a.fmarketcode,a.fholidayscode,a.ftradecury from ");
			 //--------------------------------------------------------------------------------------------------------------------------------------------//
			 buf.append(" (select fsecuritycode,fmarketcode,fholidayscode,ftradecury from  "+pub.yssGetTableName("tb_para_security"));
			 buf.append(" where fcheckstate=1 and fcatcode='FU')a ");  //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
			 buf.append(" join ");
			//--------------------------------------------------------------------------------------------------------------------------------------------//
			 buf.append(" (select fsecuritycode,ffutype,fmultiple,fbailtype,fbailfix,fbailscale from "+pub.yssGetTableName("Tb_Para_IndexFutures"));
			 buf.append(" where fcheckstate=1)b"); //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
			//--------------------------------------------------------------------------------------------------------------------------------------------//
			 buf.append(" on a.fsecuritycode = b.fsecuritycode");
			 
			 
			 rs = dbl.openResultSet(buf.toString());
			 while(rs.next()){
				 if(count ==0 ){
					 valueMap = new HashMap();
				 }
				 futuresInfo = new IndexFuturesBean();
				 
				 futuresInfo.sMarketCode = rs.getString("FMarketCode");
				 futuresInfo.sBailType = rs.getString("fbailtype");
				 futuresInfo.sTradeCury = rs.getString("ftradecury");
				 futuresInfo.iMulTiple = rs.getInt("fmultiple");
				 futuresInfo.dBailFix = rs.getDouble("fbailfix");
				 futuresInfo.dBailScale = rs.getDouble("fbailscale");
				 futuresInfo.sHolidaysCode = rs.getString("fholidayscode");
				 if(!valueMap.containsKey(rs.getString("FMarketCode"))){
					 valueMap.put(futuresInfo.sMarketCode, futuresInfo);
				 }
				 
				 count++;
			 }
			 
			 return valueMap ;
		 }catch(Exception e){
			 throw new YssException("生成期货台帐报表数据时,初始化期货信息出错......"+e.getMessage());
		 }finally{
			 dbl.closeResultSetFinal(rs);
		 }
	 }
	
	 /***************************************************************
	  * 取前一日期货台帐库存
	  * @return
	  * @throws YssException
	  */
	 private HashMap initFutureStockData() throws YssException {

		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		HashMap valueMap = null;
		//String sql = "";
		IndexFuturesStockBean futuresStock = null;
		int count = 0;
		String key = "";
		try {
            
			//dDate = YssFun.Get_WorkDay(this.dbl,YssFun.addDay(this.dBeginDate,-1),-1);
			//dDate = YssFun.Get_WorkDay(this.dbl,this.dBeginDate,-1);
			//sql = " (select max(fstoragedate) as fstoragedate from )"+pub.yssGetTableName("tb_rep_indexfuturestock");
			buf.append(" select * from "
					+ pub.yssGetTableName("tb_rep_indexfutureStock"));
			buf.append(" where fportcode = " + dbl.sqlString(this.portCode));
			buf.append(" and fstoragedate = " + dbl.sqlDate(YssFun.addDay(this.dBeginDate, -1)));
			if (this.brokeCode.trim().length() != 0) {
				buf.append(" and fbrokercode=" + dbl.sqlString(this.brokeCode));
			}
			rs = dbl.openResultSet(buf.toString());
			while (rs.next()) {
				if (count == 0) {
					valueMap = new HashMap();
				}
				key = YssFun.formatDate(rs.getDate("FSTORAGEDATE")) + "\f"
						+ rs.getString("FMarketCode") + "\f"
						+ rs.getString("FPORTCODE")
						+ rs.getString("FBROKERCODE") + "\f"
						+ rs.getString("FFUType");

				futuresStock = new IndexFuturesStockBean();

				futuresStock.setdStockDate(rs.getDate("FSTORAGEDATE"));
				futuresStock.setsSecuritycode(rs.getString("FSECURITYCODE"));
				futuresStock.setMarketCode(rs.getString("FMarketCode"));
				futuresStock.setsPortCode(rs.getString("FPORTCODE"));
				futuresStock.setsBrokerCode(rs.getString("FBROKERCODE"));
				futuresStock.setsFuType(rs.getString("FFUType"));

				futuresStock.setdClosePositionRatio(rs
						.getDouble("FClosePositionRatio"));
				futuresStock.setdCarryOverCost(rs.getDouble("FCarryOverCost"));
				futuresStock.setdClosePositionLossGain(rs
						.getDouble("FClosePositionLossGain"));
				futuresStock.setdCarryOverMV(rs.getDouble("FCarryOverMV"));
				futuresStock.setdAmount(rs.getDouble("FAmount"));
				futuresStock.setdCost(rs.getDouble("FCost"));
				futuresStock.setdTotalLossGain(rs.getDouble("FLossGain")); //持仓损益（余额）
				futuresStock.setLossGain_day(rs.getDouble("FLossGainDay")); //持仓损益（发生额）
				futuresStock.setdDayLossGain(rs.getDouble("FDayLossGain")); //当日盈亏
				futuresStock.setdAdjustMV(rs.getDouble("FAdjustMV"));
				futuresStock.setCuryCode(rs.getString("FCuryCode"));

				valueMap.put(key, futuresStock);
				count++;
			}
		} catch (Exception e) {

		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return valueMap;
	}
	 
	 /*************************************************************
	  * 初始化期货收盘价
	  * 
	  * 昨日收盘价也要获取，因为要进行开仓移动平均价的计算
	  * @return
	  * @throws YssException
	  */
	 private HashMap initFuturePrice()throws YssException{
		 
		 ResultSet rs = null;
		 HashMap valueMap = null;//Key:日期+\f+期货合约代码
		 String sKey = "";
		 StringBuffer buf = new StringBuffer();
		 boolean initMap =true; //用于控制HashMap的初始化
		 try{
		      
			 /********************************************************************************************
			  *  查询期货台帐行情：
			  *   行情表数据：前台的日期来查询&& 已审核  查询出系统内部码、行情日期、收盘价
			  *   
			  *   证券信息表：获取 期货合约代码、及系统内部代码
			  */
			 buf.append(" select b.fmarketcode,a.fmktvaluedate,a.fprice from ");
			 //--------------------------------------------------------------------------------------------------------------------------------//
			 buf.append(" (select fsecuritycode, fvaldate as fmktvaluedate, fprice  from "+pub.yssGetTableName("Tb_Data_valmktprice")+" a1");
			 buf.append(" where fcheckstate = 1 and fvaldate between "+dbl.sqlDate(YssFun.addDay(this.dBeginDate, -1))+" and "+dbl.sqlDate(this.dEndDate));
			 buf.append(" and exists ");
			 buf.append(" (select fsecuritycode from "+pub.yssGetTableName("tb_para_security")+" a2 where fcheckstate = 1  and fcatcode = 'FU'" );
			 buf.append(" and a1.fsecuritycode = a2.fsecuritycode))a "); //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
			 //-------------------------------------------------------------------------------------------------------------------------------//
			 buf.append(" join ");
			 buf.append(" (select fsecuritycode,fmarketcode from "+pub.yssGetTableName("tb_para_security")+" where fcheckstate=1 ");
			 buf.append(" and fcatcode='FU')b");  //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
			 //------------------------------------------------------------------------------------------------------------------------------//
			 buf.append(" on a.fsecuritycode = b.fsecuritycode");
			 
			 rs = dbl.openResultSet(buf.toString());
			 while(rs.next()){
				 if(initMap){
					 valueMap = new HashMap();
					 initMap = false;
				 }
				
				sKey = YssFun.formatDate(rs.getDate("fmktvaluedate"))+"\f"+rs.getString("fmarketcode"); 
				//期货合约的多头和空头结算价都是一样的，所以这里只要存一份就够了
				if(valueMap.containsKey(sKey)){
					continue;
				}
				valueMap.put(sKey, rs.getDouble("fprice")+"");				
			 }
		 }catch(Exception e){
			 throw new YssException("生成期货台帐数据时，初始化期货收盘价时出错......");
		 }finally{
			 dbl.closeResultSetFinal(rs);
		 }
		 
		 return valueMap;
	 }

	 
	 /*********************************
	  * 处理期货交易数据数据
	  *  开仓金额、开仓数量、开仓费用
	  *  平仓金额、平仓数量、平仓费用
	  *  收盘价、平仓移动平均价、开仓移动平均价等
	  *  
	  * @param futuresInfoMap  期货初始化信息
	  * @return 期货交易数据
	  * @throws YssException
	  */
	 private HashMap dealFuturesTradeData(HashMap futuresInfoMap,HashMap futureStockMap)throws YssException{
		 
		 IndexFuturesDataBean futuresTradeData = null;
		 IndexFuturesStockBean futuresStockData = null;
		 IndexFuturesBean futuresInfo = null;
		 ResultSet rs = null;
		 StringBuffer buf = new StringBuffer();
		 HashMap valueMap = null;
		 HashMap priceMap = null;
		 double yesterDayPrice =0;
		 String sTradeDatakey = "";
		 String sStockDataKey = "";
		 double dAvgClosePositionPrice =0;     //平仓移动加权平均价
		 double dAvgOpenPositionPrice =0;      //开仓仓移动加权平均价
		 java.util.Date dWorkDay = null;
		 int count =0;
		 try{
			 
			 if(futuresInfoMap == null){
				 //throw new YssException("请维护期货信息！");
				 return null;
			 }
			 
			 /****************************************************
			  *  获取期货交易信息
			  *    
			  */
			 buf.append(" select a.*,b.fmarketcode,b.ftradecury,c.ffutype,c.fmultiple,d.fprice from ");
			 //------------------------------------------------------------- 获取期货交易数据  ----------------------------------------------------//
			 buf.append(" (select fsecuritycode,fportcode,fbrokercode,ftradetypecode,fbargaindate,sum(ftradeamount) as ftradeamount,sum(ftrademoney) as ftrademoney, ");
			 //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
			 
			 /**start modify by huangqirong 2013-7-5 Bug #8449  还原成FuturesTrade*/
			 buf.append(" sum(nvl(ftradefee1,0)+nvl(ftradefee2,0)+nvl(ftradefee3,0)+nvl(ftradefee4,0)+nvl(ftradefee5,0)" +
				 		"+nvl(ftradefee6,0)+nvl(ftradefee7,0)+nvl(ftradefee8,0)) as ffee from "+
				 		pub.yssGetTableName("tb_data_futurestrade")+" a1");
			/**end modify by huangqirong 2013-7-5 Bug #8449 还原成FuturesTrade*/
			 
			 buf.append("  where fcheckstate = 1 and fportcode ="+dbl.sqlString(this.portCode));
			 buf.append(" and fbargaindate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate));
			 if(this.brokeCode.trim().length()!=0){
				 buf.append(" and fbrokercode = "+dbl.sqlString(this.brokeCode));
			 }
			 buf.append(" and exists (select fsecuritycode from "+pub.yssGetTableName("tb_para_security"));
			 buf.append(" a2 where fcheckstate=1 and fcatcode='FU' and a1.fsecuritycode=a2.fsecuritycode)"); //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
			 buf.append(" group by fsecuritycode,fportcode,fbargaindate,fbrokercode,ftradetypecode)a ");
			 //------------------------------------------------------------- 获取期货交易数据 end -------------------------------------------------//
			 buf.append(" left join");
			 buf.append(" (select fsecuritycode,fmarketcode,ftradecury from "+pub.yssGetTableName("tb_para_security"));
			 buf.append(" where fcheckstate=1 and fcatcode='FU')b"); //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
			 buf.append(" on a.fsecuritycode = b.fsecuritycode");
			//--------------------------------------------------------------------------------------------------------------------------------------//
			 buf.append(" left join");
			 buf.append(" (select fsecuritycode,ffutype,fmultiple from   "+pub.yssGetTableName("tb_para_indexfutures"));
			 buf.append(" where fcheckstate=1)c"); //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
			 buf.append(" on a.fsecuritycode=c.fsecuritycode");
			//--------------------------------------------------------------------------------------------------------------------------------------//
			 buf.append(" left join");
			 buf.append(" (select fsecuritycode, fvaldate as fmktvaluedate, fprice from "+pub.yssGetTableName("Tb_Data_valmktprice"));
			 buf.append("  where fcheckstate = 1 and fvaldate between "+dbl.sqlDate(this.dBeginDate)+" and "+dbl.sqlDate(this.dEndDate));
			 buf.append("  )d on a.fsecuritycode=d.fsecuritycode and a.fbargaindate=d.fmktvaluedate ");
			//--------------------------------------------------------------------------------------------------------------------------------------//
			 buf.append(" order by a.fbargaindate, c.ffutype, a.fsecuritycode");//按开仓类型排序 //------ modify by wangzuochun 2010.11.26 BUG #498 新增的“估值期货台账”功能，点击“生成”按钮时提示错误
			 
			rs = dbl.openResultSet(buf.toString());
			while(rs.next()){
				if(count ==0){
					valueMap = new HashMap();
					priceMap = this.initFuturePrice();
				}
				
				if(rs.getString("ffutype")==null){
					throw new YssException(" 请核对期货合约代码为【"+rs.getString("fsecuritycode")+"】的期货信息设置是否已维护!!!!");
				}
				
				futuresInfo = (IndexFuturesBean)futuresInfoMap.get(rs.getString("fmarketcode"));
				//交易数据Key
				sTradeDatakey = YssFun.formatDate(rs.getDate("fbargaindate"))+"\f"+rs.getString("fmarketcode")+"\f"+rs.getString("fportcode")
				                +rs.getString("fbrokercode")+"\f"+rs.getString("ffutype");
				
				//
				/*******************************************************************
				 * 国内期货既可以空头也可以多头。
				 * 
				 * 注意：这里费用是通过汇总明细交易费用而得，不是通过当日该期货合约的总的成交金额计算。
				 * 可能会有尾差问题，这块以后再做调整
				 */
				if(valueMap.containsKey(sTradeDatakey)){
					futuresTradeData = (IndexFuturesDataBean)valueMap.get(sTradeDatakey);
					
					if(rs.getString("ftradetypecode").equalsIgnoreCase("20")){//开仓
						//dWorkDay =this.getSettingOper().getWorkDay(futuresInfo.sHolidaysCode,rs.getDate("fbargaindate"),-1);
						//dWorkDay = YssFun.Get_WorkDay(this.dbl,rs.getDate("fbargaindate"),-1);
						dWorkDay = YssFun.addDay(rs.getDate("fbargaindate"), -1);
						sStockDataKey = YssFun.formatDate(dWorkDay)
								        + "\f"
								        + rs.getString("fmarketcode")
								        + "\f"
								        + rs.getString("fportcode")
								        + rs.getString("fbrokercode")
								        + "\f"
								        + rs.getString("ffutype");
						
						if(futureStockMap !=null){
							futuresStockData = (IndexFuturesStockBean) futureStockMap.get(sStockDataKey);
						}
						
						 
						// 多头开仓移动加权平均价 = (昨日持仓*昨日收盘价+当日多头开仓数×当日多头开仓结算价)/(昨日持仓合计数+当日多头开仓数)
						if (futuresStockData == null) {
							dAvgOpenPositionPrice = YssFun.roundIt(YssD.div(rs.getDouble("ftrademoney"),YssD.mul(rs.getDouble("ftradeamount"), futuresInfo.iMulTiple)), 12);
						} else {
							if(priceMap!=null){
								yesterDayPrice = YssFun.toDouble((String)priceMap.get(YssFun.formatDate(YssFun.addDay(rs.getDate("fbargaindate"), -1))+"\f"+rs.getString("fmarketcode")));
							}else{
								yesterDayPrice =0;
							}
							
							dAvgOpenPositionPrice = YssFun.roundIt(YssD.div(YssD.add(YssD.mul(futuresStockData.getdAmount(), yesterDayPrice,futuresInfo.iMulTiple), rs.getDouble("ftrademoney")),
									                                        YssD.mul(YssD.add(futuresStockData.getdAmount(), rs.getDouble("ftradeamount")), futuresInfo.iMulTiple)), 12);
						}
						futuresTradeData.setdAvgOpenPositionPrice(dAvgOpenPositionPrice);
						
						futuresTradeData.setdOpenPositionMoney(rs.getDouble("ftrademoney"));
						futuresTradeData.setiOpenPositionAmount(rs.getInt("ftradeamount"));
						futuresTradeData.setdOpenPositionFee(rs.getDouble("ffee"));
					}else if(rs.getString("ftradetypecode").equalsIgnoreCase("21")){//平仓
						
						//多头平仓移动加权平均价 = (当日多头平仓合计<当日多头平仓数×当日平仓结算价>)/(当日多头平仓数合计) <空头处理方式一致>
						dAvgClosePositionPrice = YssFun.roundIt(YssD.div( rs.getDouble("ftrademoney"),YssD.mul(rs.getInt("ftradeamount"), futuresInfo.iMulTiple)), 12);
						//dAvgClosePositionPrice = YssFun.roundIt(YssD.div( rs.getDouble("ftrademoney"),rs.getInt("ftradeamount")), 12);
						futuresTradeData.setdAvgClosePositionPrice(dAvgClosePositionPrice);
						
						futuresTradeData.setdClosePositionMoney(rs.getDouble("ftrademoney"));
						futuresTradeData.setiClosePositionAmount(rs.getInt("ftradeamount"));
						futuresTradeData.setdClosePositionFee(rs.getDouble("ffee"));
					}
				}else{
					futuresTradeData = new IndexFuturesDataBean();
					
					futuresTradeData.setdBargainDate(rs.getDate("fbargaindate"));
					futuresTradeData.setsSecuritycode(rs.getString("fsecuritycode"));
					futuresTradeData.setMarketCode(rs.getString("fmarketcode"));
					futuresTradeData.setsPortCode(rs.getString("fportcode"));
					futuresTradeData.setsBrokerCode(rs.getString("fbrokercode"));
					futuresTradeData.setsFuType(rs.getString("ffutype"));
					futuresTradeData.setiMultiple(rs.getInt("fmultiple"));
					if(rs.getString("ftradetypecode").equalsIgnoreCase("20")){//开仓
						
						//dWorkDay =this.getSettingOper().getWorkDay(futuresInfo.sHolidaysCode,rs.getDate("fbargaindate"),-1);
						//dWorkDay = YssFun.Get_WorkDay(this.dbl,rs.getDate("fbargaindate"),-1);
						dWorkDay = YssFun.addDay(rs.getDate("fbargaindate"), -1);
						sStockDataKey = YssFun.formatDate(dWorkDay)+"\f"+rs.getString("fmarketcode")+"\f"+rs.getString("fportcode")
		                                +rs.getString("fbrokercode")+"\f"+rs.getString("ffutype");
						if(futureStockMap !=null){
							futuresStockData = (IndexFuturesStockBean)futureStockMap.get(sStockDataKey);
						}
						
						//多头开仓移动加权平均价 =  (昨日持仓数*昨日收盘价*合约乘数+当日开仓金额)/(昨日持仓合计数+当日多头开仓数)*合约乘数
						if(futuresStockData == null){
							dAvgOpenPositionPrice = YssFun.roundIt(YssD.div(rs.getDouble("ftrademoney"),YssD.mul(rs.getDouble("ftradeamount"), futuresInfo.iMulTiple)), 12);
						}else{
							if(priceMap!=null){
								yesterDayPrice = YssFun.toDouble((String)priceMap.get(YssFun.formatDate(YssFun.addDay(rs.getDate("fbargaindate"), -1))+"\f"+rs.getString("fmarketcode")));
							}else{
								yesterDayPrice =0;
							}
							dAvgOpenPositionPrice = YssFun.roundIt(YssD.div(YssD.add(YssD.mul(futuresStockData.getdAmount(), yesterDayPrice,futuresInfo.iMulTiple), rs.getDouble("ftrademoney")),
                                    YssD.mul(YssD.add(futuresStockData.getdAmount(), rs.getDouble("ftradeamount")), futuresInfo.iMulTiple)), 12);
						}
						
						futuresTradeData.setdOpenPositionMoney(rs.getDouble("ftrademoney"));
						futuresTradeData.setiOpenPositionAmount(rs.getInt("ftradeamount"));
						futuresTradeData.setdOpenPositionFee(rs.getDouble("ffee"));
						futuresTradeData.setdAvgOpenPositionPrice(dAvgOpenPositionPrice);
						
					}else if(rs.getString("ftradetypecode").equalsIgnoreCase("21")){//平仓
						
						
						//多头平仓移动加权平均价 = (当日多头平仓合计<当日多头平仓数×当日平仓结算价>)/(当日多头平仓数合计) <空头处理方式一致>
						//这里的平仓移动加权平均价通过  当日平仓成交金额合计/(当日平仓数合计*合约乘数)
						dAvgClosePositionPrice = YssFun.roundIt(YssD.div( rs.getDouble("ftrademoney"),YssD.mul(rs.getInt("ftradeamount"), futuresInfo.iMulTiple)), 12);
						futuresTradeData.setdAvgClosePositionPrice(dAvgClosePositionPrice);
						
						futuresTradeData.setdClosePositionMoney(rs.getDouble("ftrademoney"));
						futuresTradeData.setiClosePositionAmount(rs.getInt("ftradeamount"));
						futuresTradeData.setdClosePositionFee(rs.getDouble("ffee"));
						
					}
					if(priceMap!=null){
						yesterDayPrice = YssFun.toDouble((String)priceMap.get(YssFun.formatDate(YssFun.addDay(rs.getDate("fbargaindate"), -1))+"\f"+rs.getString("fmarketcode")));
					}else{
						yesterDayPrice =0;
					}
					futuresTradeData.setdPrice(rs.getDouble("fprice"));
					futuresTradeData.setdOtPrice1(yesterDayPrice);
					futuresTradeData.setCuryCode(rs.getString("ftradecury"));
				}
				
				valueMap.put(sTradeDatakey,futuresTradeData);
				count++;
			}
			 return valueMap;
		 }catch(Exception e){
			 throw new YssException("生成期货台帐报表数据时，处理期货交易数据出错......"+e.getMessage());
		 }finally{
			 dbl.closeResultSetFinal(rs);
		 }
	 }

	 // 处理期货交易数据库存
	 private HashMap dealFuturesStockData(HashMap futuresInfoMap,HashMap futuresTradeDataMap,HashMap futureStockDataMap) throws YssException{
		 
		// ResultSet rs = null;
		// StringBuffer buf = new StringBuffer();
		 HashMap valueMap = null;
		 HashMap carryOverMap = null;
		 HashMap priceMap = null;
		 int idays = 0;
		 java.util.Date dDate = null;
		 java.util.Date dWorkDay = null;
		 Iterator it = null;
		 IndexFuturesBean  futuresInfo = null;
		 IndexFuturesDataBean  futuresData = null;
		 IndexFuturesStockBean futuresStock = null;
		 IndexFuturesStockBean futuresStockData = null;
		 CarryOverbean  carryOverBean = null;
		 String sKey= "";
		 String sKey1="";
		 String sCarryOverKey = "";
		 double dClosePositionRatio = 0;
		 double dCarryOverCost = 0;
		 double dPrice =0;
		 double dYesterDayPrice=0;
		 double dClosePositionLossGain = 0;
		 double dAvgClosePositionPrice =0;     //平仓移动加权平均价
		 double dAvgOpenPositionPrice =0;      //开仓仓移动加权平均价
		 double dCarryOverMV = 0;     
		 double dAmount = 0;
		 double dCost = 0 ;
		 double dAdjustMV = 0;
		 double dTotalLossGain = 0; //持仓损益(余额)
		 double ccsy_day = 0;       //持仓损益（发生额）
		 double dDayLossGain = 0; //当日盈亏
		 double dSettlementMoney = 0;
		 double dAdjustMargins = 0;
		 try{
			 
			 if(futuresInfoMap == null){
				 //throw new YssException("请维护期货信息！");
				 return null;
			 }else{
				 carryOverMap = new HashMap();
				 valueMap = new HashMap();
				 priceMap = initFuturePrice();
			 }
			 
			idays = YssFun.dateDiff(this.dBeginDate, this.dEndDate)+1;//包括起始日当天
			
			//按日期循环
			for(int i=0;i<idays;i++){
				//add by songjie 2012.10.10 BUG 5733 QDV4赢时胜(上海开发部)2012年9月14日02_B 初始化
				ccsy_day = 0;//初始化变量
				
				dDate = YssFun.addDay(this.dBeginDate, i);
				//----------------------------------------- 1. 对当天有交易数据的期货进行处理 --------------------------------------------------------//
				if(futuresTradeDataMap !=null){
					it = futuresTradeDataMap.keySet().iterator();
					while(it.hasNext()){
						carryOverBean = new CarryOverbean();
						futuresData = (IndexFuturesDataBean) futuresTradeDataMap.get((String) it.next());
						
						//这里对日期进行判断，避免重复处理
						if(YssFun.dateDiff(dDate, futuresData.getdBargainDate())!=0){
							continue;
						}
						
						/*********************************************************************************
                         *   增加对日期的判断
                         *   如果是跨期间的库存的取值不同
                         *    第一天的库存数据值是取自 库存表里的数据
                         *    第二天的库存数据就应该是从ValueMap取值的了，因为还没进行持久化
						 */
						if(YssFun.dateDiff(this.dBeginDate, futuresData.getdBargainDate())==0){
							futuresInfo = (IndexFuturesBean)futuresInfoMap.get(futuresData.getMarketCode());//期货基本信息
							//dWorkDay =this.getSettingOper().getWorkDay(futuresInfo.sHolidaysCode,dDate,-1);
							//dWorkDay = YssFun.Get_WorkDay(this.dbl,dDate,-1);
							dWorkDay = YssFun.addDay(dDate, -1);
							//这个键是昨日库存的键
							sKey = YssFun.formatDate(dWorkDay)+"\f"+futuresData.getMarketCode()+"\f"+futuresData.getsPortCode()
							       +futuresData.getsBrokerCode()+"\f"+futuresData.getsFuType();
							
							if(futureStockDataMap !=null){
								futuresStock = (IndexFuturesStockBean)futureStockDataMap.get(sKey);//期货交易数据
							}
							
							
							
							//--------------------------------------- 判断前一日是否有库存 ----------------------------------------------------------//
							if(futuresStock !=null){
								
								//--------------------------------------- 判断是否有平仓数据  -------------------------------------------------------//
								dAvgClosePositionPrice = 0;//先清空变量，不然发现赋值有问题
								dAvgOpenPositionPrice = 0;//先清空变量，不然发现赋值有问题
								dAvgClosePositionPrice = YssFun.roundIt(YssD.div( futuresData.getdClosePositionMoney(),YssD.mul(futuresData.getiClosePositionAmount(), futuresInfo.iMulTiple)), 12);     
								if(priceMap!=null){
									dYesterDayPrice = YssFun.toDouble((String)priceMap.get(YssFun.formatDate(futuresStock.getdStockDate())+"\f"+futuresStock.getMarketCode()));
								}else{
									dYesterDayPrice =0;
								}
								dAvgOpenPositionPrice = YssFun.roundIt(YssD.div(YssD.add(futuresStock.getdCost(), futuresData.getdOpenPositionMoney()),
							                                    YssD.mul(YssD.add(futuresStock.getdAmount(), futuresData.getiOpenPositionAmount()), futuresInfo.iMulTiple)), 12);
										
								if(futuresData.getiClosePositionAmount()!=0){
									//多头平仓比例 = 多头平仓数量 /(多头开仓数量 + 多头持仓数量)   空头平仓比例处理方式同上
									dClosePositionRatio = YssFun.roundIt(YssD.div(futuresData.getiClosePositionAmount(), YssD.add(futuresStock.getdAmount(),futuresData.getiOpenPositionAmount())),12);
									
									//多头结转成本 = (昨日多头持仓成本 + 今日多头开仓金额)*多头平仓比例
									dCarryOverCost = YssFun.roundIt(YssD.mul(dClosePositionRatio, YssD.add(futuresStock.getdCost(), futuresData.getdOpenPositionMoney())),2);
									
									/************************************************************
									 *  平仓损益：
									 *     多头  = -(多头开仓移动平均价 - 多头平仓移动平均价格)*合约乘数*当日多头平仓数
									 *     空头 = (空头开仓移动平均价格 - 空头平仓移动平均价格)*合约乘数*当日空头平仓数
									 *     
									 *     多头平仓移动加权平均价 = (当日多头平仓合计<当日多头平仓数×当日平仓结算价>)/(当日多头平仓数合计) <空头处理方式一致>
									 *     多头开仓移动加权平均价 =  (昨日持仓*昨日收盘价+当日多头开仓数×当日多头开仓结算价)/(昨日持仓合计数+当日多头开仓数)
									 */
									    
									if(futuresData.getsFuType().equalsIgnoreCase("BuyAM")){
										//多头
										dClosePositionLossGain = YssFun.roundIt(YssD.mul(YssD.sub(dAvgClosePositionPrice, dAvgOpenPositionPrice), futuresData.getiClosePositionAmount(), futuresInfo.iMulTiple), 2);
									}else if(futuresData.getsFuType().equalsIgnoreCase("SellAM")){
										//空头
										dClosePositionLossGain = YssFun.roundIt(YssD.mul(YssD.sub(dAvgOpenPositionPrice, dAvgClosePositionPrice), futuresData.getiClosePositionAmount(), futuresInfo.iMulTiple), 2);
									}
									
									/*************************************************************************
									 * 结转估增:
									 *  空头估值增值  =  昨日空头估值增值余额 * 当日平仓比例    (多头处理方式同上)
									 *  昨日空头估值增值余额 = 昨日持仓损益  + 昨日估值前价值调整
									 */
									dCarryOverMV = YssFun.roundIt(YssD.mul(YssD.add(futuresStock.getdTotalLossGain(), futuresStock.getdAdjustMV()), dClosePositionRatio), 2);
								}else{
									dClosePositionRatio = 0;
									dCarryOverCost = 0;
									dClosePositionLossGain = 0;
									dCarryOverMV = 0;
								}
								//--------------------------------------- 判断是否有平仓数据 end ----------------------------------------------------//
								
								dAmount = futuresStock.getdAmount()+futuresData.getiOpenPositionAmount()-futuresData.getiClosePositionAmount();
							    	
								//持仓成本 = 昨日持仓成本 + 当日开仓金额 - 结转成本
								dCost = YssFun.roundIt(futuresStock.getdCost() + futuresData.getdOpenPositionMoney() - dCarryOverCost, 2);
								
								/*********************************************************************
								 * 估值前价值调整 = 昨日估值前价值调整+昨日估值增值发生额-当日结转估值增值
								 * 
								 * 
								 */
								dAdjustMV = YssFun.roundIt(futuresStock.getdTotalLossGain()+futuresStock.getdAdjustMV()-dCarryOverMV,2);
								
								
								if(futuresData.getsFuType().equalsIgnoreCase("BuyAM")){
									//多头估值= 当日持仓市值 - 当日持仓成本-昨日多头估值
									dTotalLossGain = YssFun.roundIt(YssD.sub(YssD.mul(dAmount, futuresInfo.iMulTiple,futuresData.getdPrice()), dCost),2);
									ccsy_day = YssD.sub(dTotalLossGain,futuresStock.getdTotalLossGain());
								}else if(futuresData.getsFuType().equalsIgnoreCase("SellAM")){
									//多头估值= 当日持仓市值 - 当日持仓成本-昨日多头估值
									dTotalLossGain = YssFun.roundIt(YssD.sub(YssD.mul(dAmount, futuresInfo.iMulTiple,futuresData.getdPrice()), dCost),2);
									dTotalLossGain = YssD.mul(dTotalLossGain,-1);
									ccsy_day = YssD.sub(dTotalLossGain,futuresStock.getdTotalLossGain());
								}
								
								
								//当日盈亏 = 当日估值增值发生额(持仓损益) + 平仓损益 
								dDayLossGain = ccsy_day + dClosePositionLossGain;
								
								
								
								/********************************************************************
								 *  无负债结算(发生额) = ABS(当日多头持仓损益+当日空头持仓损益)
								 *  保证金结转(发生额) = ABS(当日收盘价*(空头当日持仓量合计+多头当日持仓量合计)*保证金比例*放大倍数-昨日收盘价*(空头昨日持仓量合计+多头昨日持仓量合计)*保证金比例*放大倍数)
								 */
								dSettlementMoney = YssFun.roundIt(dTotalLossGain,4);
								dYesterDayPrice = YssFun.toDouble((String)priceMap.get(YssFun.formatDate(YssFun.addDay(futuresData.getdBargainDate(), -1))+"\f"+futuresStock.getMarketCode()));
								//dAdjustMargins = YssFun.roundIt(YssD.mul(YssD.sub(futuresData.getdPrice(), dYesterDayPrice), YssD.sub(futuresData.getiOpenPositionAmount(), futuresData.getiClosePositionAmount()), futuresInfo.iMulTiple, futuresInfo.dBailScale), 4);
								if(futuresInfo.dBailFix > 0){
									dAdjustMargins = YssFun.roundIt(YssD.mul(YssD.sub(futuresData.getiOpenPositionAmount(), futuresData.getiClosePositionAmount()), futuresInfo.dBailFix), 2);
								}else{																	
									dAdjustMargins = YssD.mul(YssD.add(YssD.mul(futuresData.getdPrice(), YssD.sub(futuresData.getiOpenPositionAmount(), futuresData.getiClosePositionAmount())), YssD.mul(futuresStock.getdAmount(), YssD.sub(futuresData.getdPrice(), dYesterDayPrice))),futuresInfo.iMulTiple, futuresInfo.dBailScale);
								}								
							}else{
								//--------------------------------------- 判断是否有平仓数据  -------------------------------------------------------//
								dAvgClosePositionPrice = 0;//先清空变量，不然发现赋值有问题
								dAvgOpenPositionPrice = 0;//先清空变量，不然发现赋值有问题
								dAvgClosePositionPrice = YssFun.roundIt(YssD.div( futuresData.getdClosePositionMoney(),YssD.mul(futuresData.getiClosePositionAmount(), futuresInfo.iMulTiple)), 12);   
								dAvgOpenPositionPrice = YssFun.roundIt(YssD.div(futuresData.getdOpenPositionMoney(),YssD.mul(futuresData.getiOpenPositionAmount(), futuresInfo.iMulTiple)), 12);
								
								if(futuresData.getiClosePositionAmount()!=0){
									//多头平仓比例 = 多头平仓数量 /(多头开仓数量 + 多头持仓数量)   空头平仓比例处理方式同上
									dClosePositionRatio = YssFun.roundIt(YssD.div(futuresData.getiClosePositionAmount(),futuresData.getiOpenPositionAmount()),12);
									
									//多头结转成本 = (昨日多头持仓成本 + 今日多头开仓金额)*多头平仓比例
									dCarryOverCost = YssFun.roundIt(YssD.mul(dClosePositionRatio,  futuresData.getdOpenPositionMoney()),2);
									
									/************************************************************
									 *  平仓损益：
									 *     多头  = -(多头开仓移动平均价 - 多头平仓移动平均价格)*合约乘数*当日多头平仓数
									 *     空头 = (空头开仓移动平均价格 - 空头平仓移动平均价格)*合约乘数*当日空头平仓数
									 *     
									 *     多头平仓移动加权平均价 = (当日多头平仓合计<当日多头平仓数×当日平仓结算价>)/(当日多头平仓数合计) <空头处理方式一致>
									 *     多头开仓移动加权平均价 =  (昨日持仓*昨日收盘价+当日多头开仓数×当日多头开仓结算价)/(昨日持仓合计数+当日多头开仓数)
									 */
									dAvgClosePositionPrice = 0;//先清空变量，不然发现赋值有问题
									dAvgOpenPositionPrice = 0;//先清空变量，不然发现赋值有问题
									dAvgClosePositionPrice = YssFun.roundIt(YssD.div( futuresData.getdClosePositionMoney(),YssD.mul(futuresData.getiClosePositionAmount(), futuresInfo.iMulTiple)), 12);   
									dAvgOpenPositionPrice = YssFun.roundIt(YssD.div(futuresData.getdOpenPositionMoney(),YssD.mul(futuresData.getiOpenPositionAmount(), futuresInfo.iMulTiple)), 12);
									if(futuresData.getsFuType().equalsIgnoreCase("BuyAM")){
										//多头
										dClosePositionLossGain = YssFun.roundIt(YssD.mul(YssD.sub(dAvgClosePositionPrice, dAvgOpenPositionPrice), futuresData.getiClosePositionAmount(), futuresInfo.iMulTiple), 2);
									}else if(futuresData.getsFuType().equalsIgnoreCase("SellAM")){
										//空头
										dClosePositionLossGain = YssFun.roundIt(YssD.mul(YssD.sub(dAvgOpenPositionPrice, dAvgClosePositionPrice), futuresData.getiClosePositionAmount(), futuresInfo.iMulTiple), 2);
									}
									
									/*************************************************************************
									 * 结转估增:
									 *  空头估值增值  =  昨日空头估值增值余额 * 当日平仓比例    (多头处理方式同上)
									 *  当日估值增值 = 当日持仓损益 - 昨日持仓损益
									 */
									dCarryOverMV = 0;
								}else{
									dClosePositionRatio = 0;
									dCarryOverCost = 0;
									dClosePositionLossGain = 0;
									dCarryOverMV = 0;
								}
								//--------------------------------------- 判断是否有平仓数据 end ----------------------------------------------------//
								
								dAmount = futuresData.getiOpenPositionAmount()-futuresData.getiClosePositionAmount();
							    	
								//持仓成本 = 昨日持仓成本 + 当日开仓金额 - 结转成本
								dCost = YssFun.roundIt(futuresData.getdOpenPositionMoney() - dCarryOverCost, 2);
								
								if(futuresData.getsFuType().equalsIgnoreCase("BuyAM")){
									//多头估值= 当日持仓市值 - 当日持仓成本-昨日多头估值
									dTotalLossGain = YssFun.roundIt(YssD.sub(YssD.mul(dAmount, futuresInfo.iMulTiple,futuresData.getdPrice()), dCost),2);
									ccsy_day = YssD.sub(dTotalLossGain,0);
								}else if(futuresData.getsFuType().equalsIgnoreCase("SellAM")){
									//多头估值= 当日持仓市值 - 当日持仓成本-昨日多头估值
									dTotalLossGain = YssFun.roundIt(YssD.sub(YssD.mul(dAmount, futuresInfo.iMulTiple,futuresData.getdPrice()), dCost),2);
									dTotalLossGain = YssD.mul(dTotalLossGain,-1);
									ccsy_day = YssD.sub(dTotalLossGain,0);
								}								
								
								//当日盈亏 = 当日估值增值发生额(持仓损益) + 平仓损益 
								dDayLossGain = ccsy_day + dClosePositionLossGain;
								
								
								/*********************************************************************
								 * 估值前价值调整 = 昨日估值前价值调整+昨日估值增值发生额-当日结转估值增值
								 * 
								 */
								dAdjustMV = 0;
								
								/********************************************************************
								 *  无负债结算(发生额) = ABS(当日多头持仓损益+当日空头持仓损益)
								 *  保证金结转(发生额) = ABS(当日收盘价*(空头当日持仓量合计+多头当日持仓量合计)*保证金比例*放大倍数-昨日收盘价*(空头昨日持仓量合计+多头昨日持仓量合计)*保证金比例*放大倍数)
								 */
								dSettlementMoney = YssFun.roundIt(dTotalLossGain,4);
								if(futuresInfo.dBailFix > 0){
									dAdjustMargins = YssFun.roundIt(YssD.mul(YssD.sub(futuresData.getiOpenPositionAmount(), futuresData.getiClosePositionAmount()), futuresInfo.dBailFix), 2);
								}else{																	
									dAdjustMargins = YssFun.roundIt(YssD.mul(futuresData.getdPrice(), YssD.sub(futuresData.getiOpenPositionAmount(), futuresData.getiClosePositionAmount()), futuresInfo.iMulTiple, futuresInfo.dBailScale), 4);
								}
							
							}
						   //--------------------------------------- 判断前一日是否有库存 end -------------------------------------------------------//
							
						 	
						}else if(YssFun.dateDiff(this.dBeginDate, futuresData.getdBargainDate())>0){
							futuresInfo = (IndexFuturesBean)futuresInfoMap.get(futuresData.getMarketCode());//期货基本信息
							//dWorkDay =this.getSettingOper().getWorkDay(futuresInfo.sHolidaysCode,dDate,-1);
							//dWorkDay = YssFun.Get_WorkDay(this.dbl,dDate,-1);
							dWorkDay = YssFun.addDay(dDate, -1);
							//这个键是取昨日库存的键，所以日期是昨日。
							sKey = YssFun.formatDate(dWorkDay)+"\f"+futuresData.getMarketCode()+"\f"+futuresData.getsPortCode()
						       +futuresData.getsBrokerCode()+"\f"+futuresData.getsFuType();
						
						   futuresStock = (IndexFuturesStockBean)valueMap.get(sKey);//期货交易数据
						  
						   
						   
						 //--------------------------------------- 判断是否有平仓数据  -------------------------------------------------------//
						   dAvgClosePositionPrice = 0;//先清空变量，不然发现赋值有问题
						   dAvgOpenPositionPrice = 0;//先清空变量，不然发现赋值有问题
						   
							dAvgClosePositionPrice = YssFun.roundIt(YssD.div( futuresData.getdClosePositionMoney(),YssD.mul(futuresData.getiClosePositionAmount(), futuresInfo.iMulTiple)), 12);   
							if(futuresStock!=null){
								if(priceMap!=null){
									dYesterDayPrice = YssFun.toDouble((String)priceMap.get(YssFun.formatDate(futuresStock.getdStockDate())+"\f"+futuresStock.getMarketCode()));
								}else{
									dYesterDayPrice =0;
								}
								
								dAvgOpenPositionPrice = YssFun.roundIt(YssD.div(YssD.add(futuresStock.getdCost(), futuresData.getdOpenPositionMoney()),
							                                    YssD.mul(YssD.add(futuresStock.getdAmount(), futuresData.getiOpenPositionAmount()), futuresInfo.iMulTiple)), 12);
													
							}else{
								dAvgOpenPositionPrice = YssFun.roundIt(YssD.div(futuresData.getdOpenPositionMoney(),YssD.mul(futuresData.getiOpenPositionAmount(), futuresInfo.iMulTiple)), 12);
							}
							
							if(futuresData.getiClosePositionAmount()!=0){
								if(futuresStock!=null){
									//多头平仓比例 = 多头平仓数量 /(多头开仓数量 + 多头持仓数量)   空头平仓比例处理方式同上
									dClosePositionRatio = YssFun.roundIt(YssD.div(futuresData.getiClosePositionAmount(), YssD.add(futuresStock.getdAmount(),futuresData.getiOpenPositionAmount())),12);
									
									//多头结转成本 = (昨日多头持仓成本 + 今日多头开仓金额)*多头平仓比例
									dCarryOverCost = YssFun.roundIt(YssD.mul(dClosePositionRatio, YssD.add(futuresStock.getdCost(), futuresData.getdOpenPositionMoney())),2);
									
								}else{
									
									//多头平仓比例 = 多头平仓数量 /(多头开仓数量 + 多头持仓数量)   空头平仓比例处理方式同上
									dClosePositionRatio = YssFun.roundIt(YssD.div(futuresData.getiClosePositionAmount(),futuresData.getiOpenPositionAmount()),12);
									
									//多头结转成本 = (昨日多头持仓成本 + 今日多头开仓金额)*多头平仓比例
									dCarryOverCost = YssFun.roundIt(YssD.mul(dClosePositionRatio,  futuresData.getdOpenPositionMoney()),2);
								}
								
								/************************************************************
								 *  平仓损益：
								 *     多头  = -(多头开仓移动平均价 - 多头平仓移动平均价格)*合约乘数*当日多头平仓数
								 *     空头 = (空头开仓移动平均价格 - 空头平仓移动平均价格)*合约乘数*当日空头平仓数
								 *     
								 *     多头平仓移动加权平均价 = (当日多头平仓合计<当日多头平仓数×当日平仓结算价>)/(当日多头平仓数合计) <空头处理方式一致>
								 *     多头开仓移动加权平均价 =  (昨日持仓*昨日收盘价+当日多头开仓数×当日多头开仓结算价)/(昨日持仓合计数+当日多头开仓数)
								 */
								if(futuresData.getsFuType().equalsIgnoreCase("BuyAM")){
									//多头
									dClosePositionLossGain = YssFun.roundIt(YssD.mul(YssD.sub(dAvgClosePositionPrice, dAvgOpenPositionPrice), futuresData.getiClosePositionAmount(), futuresInfo.iMulTiple), 2);
								}else if(futuresData.getsFuType().equalsIgnoreCase("SellAM")){
									//空头
									dClosePositionLossGain = YssFun.roundIt(YssD.mul(YssD.sub(dAvgOpenPositionPrice, dAvgClosePositionPrice), futuresData.getiClosePositionAmount(), futuresInfo.iMulTiple), 2);
								}
								
								/*************************************************************************
								 * 结转估增:
								 *  空头估值增值  =  昨日空头估值增值余额 * 当日平仓比例    (多头处理方式同上)
								 *  当日估值增值 = 当日持仓损益 - 昨日持仓损益
								 */
								if(futuresStock !=null){
									dCarryOverMV = YssFun.roundIt(YssD.mul(YssD.add(futuresStock.getdTotalLossGain(), futuresStock.getdAdjustMV()), dClosePositionRatio), 2);
								}else{
									dCarryOverMV = 0;
								}
								
								
							}else{
								dClosePositionRatio = 0;
								dCarryOverCost = 0;
								dClosePositionLossGain = 0;
								dCarryOverMV = 0;
							}
							//--------------------------------------- 判断是否有平仓数据 end ----------------------------------------------------//
							
							if(futuresStock!=null){
								dAmount = futuresStock.getdAmount()+futuresData.getiOpenPositionAmount()-futuresData.getiClosePositionAmount();
								
								//持仓成本 = 昨日持仓成本 + 当日开仓金额 - 结转成本
								dCost = YssFun.roundIt(futuresStock.getdCost() + futuresData.getdOpenPositionMoney() - dCarryOverCost, 2);
								
								/*********************************************************************
								 * 估值前价值调整 = 昨日估值前价值调整+昨日估值增值发生额-当日结转估值增值
								 * 
								 */
								dAdjustMV = YssFun.roundIt(futuresStock.getdTotalLossGain()+futuresStock.getdAdjustMV()-dCarryOverMV,2);
								
							}else{
								dAmount = futuresData.getiOpenPositionAmount()-futuresData.getiClosePositionAmount();
								dCost = YssFun.roundIt(futuresData.getdOpenPositionMoney() - dCarryOverCost, 2);
								dAdjustMV = 0;
							}
							
							if(futuresData.getsFuType().equalsIgnoreCase("BuyAM")){
								//多头估值= 当日持仓市值 - 当日持仓成本-昨日多头估值
								dTotalLossGain = YssFun.roundIt(YssD.sub(YssD.mul(dAmount, futuresInfo.iMulTiple,futuresData.getdPrice()), dCost),2);
								//edit by songjie 2012.10.10 BUG 5733 QDV4赢时胜(上海开发部)2012年9月14日02_B 空指针异常
								if(futuresStock != null){//添加非空判断
									ccsy_day = YssD.sub(dTotalLossGain,futuresStock.getdTotalLossGain());
								}
							}else if(futuresData.getsFuType().equalsIgnoreCase("SellAM")){
								//多头估值= 当日持仓市值 - 当日持仓成本-昨日多头估值
								dTotalLossGain = YssFun.roundIt(YssD.sub(YssD.mul(dAmount, futuresInfo.iMulTiple,futuresData.getdPrice()), dCost),2);
								dTotalLossGain = YssD.mul(dTotalLossGain,-1);
								//edit by songjie 2012.10.10 BUG 5733 QDV4赢时胜(上海开发部)2012年9月14日02_B 空指针异常
								if(futuresStock != null){//添加非空判断
									ccsy_day = YssD.sub(dTotalLossGain,futuresStock.getdTotalLossGain());
								}
							}							
							
							//当日盈亏 = 当日估值增值发生额(持仓损益) + 平仓损益 
							dDayLossGain = ccsy_day + dClosePositionLossGain;
						 
							/********************************************************************
							 *  无负债结算(发生额) = ABS(当日多头持仓损益+当日空头持仓损益)
							 *  保证金结转(发生额) = ABS(当日收盘价*(空头当日持仓量合计+多头当日持仓量合计)*保证金比例*放大倍数-昨日收盘价*(空头昨日持仓量合计+多头昨日持仓量合计)*保证金比例*放大倍数)
							 */
							dSettlementMoney = YssFun.roundIt(dTotalLossGain,4);
							if(futuresStock!=null){
								
								dYesterDayPrice = YssFun.toDouble((String)priceMap.get(YssFun.formatDate(YssFun.addDay(futuresData.getdBargainDate(), -1))+"\f"+futuresStock.getMarketCode()));
								dAdjustMargins = YssD.mul(YssD.add(YssD.mul(futuresData.getdPrice(), YssD.sub(futuresData.getiOpenPositionAmount(), futuresData.getiClosePositionAmount())), YssD.mul(futuresStock.getdAmount(), YssD.sub(futuresData.getdPrice(), dYesterDayPrice))),futuresInfo.iMulTiple, futuresInfo.dBailScale);
							}else{
								dAdjustMargins = YssFun.roundIt(YssD.mul(futuresData.getdPrice(), YssD.sub(futuresData.getiOpenPositionAmount(), futuresData.getiClosePositionAmount()), futuresInfo.iMulTiple, futuresInfo.dBailScale), 4);
							}
							if(futuresInfo.dBailFix > 0){
								dAdjustMargins = YssFun.roundIt(YssD.mul(YssD.sub(futuresData.getiOpenPositionAmount(), futuresData.getiClosePositionAmount()), futuresInfo.dBailFix), 2);
							}
							
							//dAdjustMargins = YssFun.roundIt(YssD.mul(YssD.sub(futuresData.getdPrice(), dYesterDayPrice), YssD.sub(futuresData.getiOpenPositionAmount(), futuresData.getiClosePositionAmount()), futuresInfo.iMulTiple, futuresInfo.dBailScale), 4);
						}
	                   
						futuresStockData = new IndexFuturesStockBean();
						futuresStockData.setsSecuritycode(futuresData.getsSecuritycode());
						futuresStockData.setMarketCode(futuresData.getMarketCode());
						futuresStockData.setsPortCode(futuresData.getsPortCode());
						futuresStockData.setsBrokerCode(futuresData.getsBrokerCode());
						futuresStockData.setsFuType(futuresData.getsFuType());
						futuresStockData.setdStockDate(futuresData.getdBargainDate());
						
						futuresStockData.setdClosePositionRatio(dClosePositionRatio);
						futuresStockData.setdCarryOverCost(dCarryOverCost);
						futuresStockData.setdClosePositionLossGain(dClosePositionLossGain);
						futuresStockData.setdCarryOverMV(dCarryOverMV);
						futuresStockData.setdAmount(dAmount);
						futuresStockData.setdCost(dCost);
						futuresStockData.setdTotalLossGain(dTotalLossGain);
						futuresStockData.setdDayLossGain(dDayLossGain);
						futuresStockData.setLossGain_day(ccsy_day);
						futuresStockData.setdAdjustMV(dAdjustMV);
						futuresStockData.setCuryCode(futuresData.getCuryCode());
						futuresStockData.setdAdjustMargins(dAdjustMargins);
						
						//这个键是用来储存当日处理出来的键，所以日期是当日的
						sKey1 = YssFun.formatDate(dDate)+"\f"+futuresData.getMarketCode()+"\f"+futuresData.getsPortCode()
					       +futuresData.getsBrokerCode()+"\f"+futuresData.getsFuType();
						valueMap.put(sKey1, futuresStockData);
						
						
						sCarryOverKey = YssFun.formatDate(dDate)+"\f"+futuresData.getMarketCode()+"\f"+futuresData.getsPortCode()
					       +futuresData.getsBrokerCode();
						
						if(carryOverMap.containsKey(sCarryOverKey)){
							carryOverBean = (CarryOverbean)carryOverMap.get(sCarryOverKey);
							carryOverBean.dAdjustMargins += dAdjustMargins;
							carryOverBean.dCarryOverMV   += dSettlementMoney;
						}else{
							carryOverBean.sSecuritycode = futuresData.getMarketCode();
							carryOverBean.sBrokerCode = futuresData.getsBrokerCode();
							carryOverBean.sPortCode = futuresData.getsPortCode();
							carryOverBean.dDate = futuresData.getdBargainDate();
							carryOverBean.dAdjustMargins = dAdjustMargins;
							carryOverBean.dCarryOverMV   = dSettlementMoney;
						}
						carryOverMap.put(sCarryOverKey, carryOverBean);
					}
					//----------------------------------------- 对当天有交易数据的期货进行处理 end --------------------------------------------------------//
					
				}
				
				

				//----------------------------------------- 2.对当天没发生交易的期货进行库存数据统计  -------------------------------------------------//
				if(futureStockDataMap !=null){
					it = futureStockDataMap.keySet().iterator();
					while(it.hasNext()){
						
						futuresStock = (IndexFuturesStockBean) futureStockDataMap.get((String) it.next());//期货前一日库存
						

						//这里对日期进行判断，避免重复处理
						if(YssFun.dateDiff(dDate, futuresStock.getdStockDate())!=0){
							//continue;
						}
						
						carryOverBean = new CarryOverbean();
						sKey = YssFun.formatDate(YssFun.formatDate(dDate))+"\f"+futuresStock.getMarketCode()+"\f"+futuresStock.getsPortCode()
					           +futuresStock.getsBrokerCode()+"\f"+futuresStock.getsFuType();
						
						if(futuresTradeDataMap !=null){
							futuresData = (IndexFuturesDataBean)futuresTradeDataMap.get(sKey);//期货交易数据
						}
						
						if(futuresData != null){
							continue;
						}
						futuresInfo = (IndexFuturesBean)futuresInfoMap.get(futuresStock.getMarketCode());//期货基本信息
										
						
						dPrice = YssFun.toDouble((String)priceMap.get(YssFun.formatDate(dDate)+"\f"+futuresStock.getMarketCode()));
						dYesterDayPrice = YssFun.toDouble((String)priceMap.get(YssFun.formatDate(futuresStock.getdStockDate())+"\f"+futuresStock.getMarketCode()));
						dClosePositionRatio = 0;
						dCarryOverCost = 0;
						dClosePositionLossGain = 0;
						dCarryOverMV = 0;
						dAmount = futuresStock.getdAmount();
						dCost = YssFun.roundIt(futuresStock.getdCost(), 2);
						dAdjustMV = YssFun.roundIt(futuresStock.getdTotalLossGain()+futuresStock.getdAdjustMV()-dCarryOverMV,2);
						
						
						if(futuresStock.getsFuType().equalsIgnoreCase("BuyAM")){
							//多头估值= 当日持仓市值 - 当日持仓成本-昨日多头估值
							dTotalLossGain = YssFun.roundIt(YssD.sub(YssD.mul(dAmount, futuresInfo.iMulTiple,dPrice), dCost),2);
							ccsy_day = YssD.sub(dTotalLossGain,futuresStock.getdTotalLossGain());
						}else if(futuresStock.getsFuType().equalsIgnoreCase("SellAM")){
							//多头估值= 当日持仓市值 - 当日持仓成本-昨日多头估值
							dTotalLossGain = YssFun.roundIt(YssD.sub(YssD.mul(dAmount, futuresInfo.iMulTiple,dPrice), dCost),2);
							dTotalLossGain = YssD.mul(dTotalLossGain,-1);
							ccsy_day = YssD.sub(dTotalLossGain,futuresStock.getdTotalLossGain());
						}
											
						//当日盈亏 = 当日估值增值发生额(持仓损益) + 平仓损益 
						dDayLossGain = ccsy_day + 0;
						
						dSettlementMoney = YssFun.roundIt(dTotalLossGain ,4);
						
						futuresStockData = new IndexFuturesStockBean();
						
						futuresStockData.setdStockDate(dDate);
						futuresStockData.setsSecuritycode(futuresStock.getsSecuritycode());
						futuresStockData.setMarketCode(futuresStock.getMarketCode());
						futuresStockData.setsPortCode(futuresStock.getsPortCode());
						futuresStockData.setsBrokerCode(futuresStock.getsBrokerCode());
						futuresStockData.setsFuType(futuresStock.getsFuType());
						
						futuresStockData.setdClosePositionRatio(dClosePositionRatio);
						futuresStockData.setdCarryOverCost(dCarryOverCost);
						futuresStockData.setdClosePositionLossGain(dClosePositionLossGain);
						futuresStockData.setdCarryOverMV(dCarryOverMV);
						futuresStockData.setdAmount(dAmount);
						futuresStockData.setdCost(dCost);
						futuresStockData.setdTotalLossGain(dTotalLossGain);
						futuresStockData.setdDayLossGain(dDayLossGain);
						futuresStockData.setLossGain_day(ccsy_day);
						futuresStockData.setdAdjustMV(dAdjustMV);
						futuresStockData.setCuryCode(futuresStock.getCuryCode());
						futuresStockData.setdAdjustMargins(0);
						
						sKey = YssFun.formatDate(dDate)+"\f"+futuresStock.getMarketCode()+"\f"+futuresStock.getsPortCode()
				           +futuresStock.getsBrokerCode()+"\f"+futuresStock.getsFuType();
						
						valueMap.put(sKey, futuresStockData);
						
						
						sCarryOverKey = YssFun.formatDate(dDate)+"\f"+futuresStock.getMarketCode()+"\f"+futuresStock.getsPortCode()
					       +futuresStock.getsBrokerCode();
						
						if(carryOverMap.containsKey(sCarryOverKey)){
							//carryOverBean.dAdjustMargins += dAdjustMargins;
							carryOverBean = (CarryOverbean)carryOverMap.get(sCarryOverKey);
							carryOverBean.dCarryOverMV   += dSettlementMoney;
						}else{
							carryOverBean.sSecuritycode = futuresStockData.getMarketCode();
							carryOverBean.sBrokerCode = futuresStockData.getsBrokerCode();
							carryOverBean.sPortCode = futuresStockData.getsPortCode();
							carryOverBean.dDate = YssFun.toSqlDate(dDate);
							//carryOverBean.dAdjustMargins = dAdjustMargins;
							carryOverBean.dCarryOverMV   = dSettlementMoney;
						}
						carryOverMap.put(sCarryOverKey, carryOverBean);
						
					}
				}
			//------------------------------------------ 对当天没发生交易的期货进行库存数据统计  end-----------------------------------------------------//	
				
				
				
			//------------------------------------------ 3. 保证金和无负债结转都是针对期货合约的，所以这里进行汇总处理 ----------------------------------//	
				it = carryOverMap.keySet().iterator();
				while(it.hasNext()){
					carryOverBean = (CarryOverbean) carryOverMap.get((String) it.next());
					
					//这里添加对日期的判断，是避免跨日期段时重复处理无负债结转金额、绝对值。
					if(YssFun.dateDiff(dDate, carryOverBean.dDate)==0){  
						sKey = YssFun.formatDate(carryOverBean.dDate)+"\f"+carryOverBean.sSecuritycode+"\f"+carryOverBean.sPortCode
					           +carryOverBean.sBrokerCode+"\f"+"BuyAM";
						futuresStockData = (IndexFuturesStockBean)valueMap.get(sKey);
						if(futuresStockData!=null){
							futuresStockData.setdSettlementMoney(Math.abs(carryOverBean.dCarryOverMV));//无负债结转取绝对值
							//futuresStockData.setdAdjustMargins(Math.abs(carryOverBean.dAdjustMargins));//保证金取绝对值
							
							valueMap.put(sKey, futuresStockData);
						}
						
						
						sKey = YssFun.formatDate(carryOverBean.dDate)+"\f"+carryOverBean.sSecuritycode+"\f"+carryOverBean.sPortCode
				               +carryOverBean.sBrokerCode+"\f"+"SellAM";
					   futuresStockData = (IndexFuturesStockBean)valueMap.get(sKey);
					   if(futuresStockData!=null){
						   futuresStockData.setdSettlementMoney(Math.abs(carryOverBean.dCarryOverMV));
						   //futuresStockData.setdAdjustMargins(Math.abs(carryOverBean.dAdjustMargins));
						
						   valueMap.put(sKey, futuresStockData);
					   }
					   
						
					}
				}
			//------------------------------------------ 第三部分处理完毕  -----------------------------------------------------------------------------//		
			}
			 return valueMap;
		 }catch(Exception e){
			 throw new YssException("生成期货台帐报表数据时，处理期货库存数据出错......"+e.getMessage());
		 }finally{
			 
		 }
	 }
	 
	// ============================================================================================
	
	/**
	 * 将数据插入数据库
	 * 
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 */
	private void insertToIndexFuturesData(HashMap valueMap)
			throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		IndexFuturesDataBean indexFuturesBook = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				+ pub.yssGetTableName("tb_rep_indexfuturesData")
				+ " (FBargainDate,FSECURITYCODE ,FPORTCODE ,FBROKERCODE ,FFUType ,FMultiple ,FPrice,FOtPrice1," 
				+ "  FClosePositionAmount,FClosePositionMoney,FClosePositionFee,FOpenPositionAmount," 
				+ "  FOpenPositionMoney,FOpenPositionFee,FCuryCode,FMarketCode)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				indexFuturesBook = (IndexFuturesDataBean)valueMap.get((String) it.next());
				
				prst.setDate(1, indexFuturesBook.getdBargainDate());
				prst.setString(2,indexFuturesBook.getsSecuritycode());
				prst.setString(3,indexFuturesBook.getsPortCode());
				prst.setString(4,indexFuturesBook.getsBrokerCode());
				prst.setString(5,indexFuturesBook.getsFuType());
				prst.setInt(6,indexFuturesBook.getiMultiple());
				prst.setDouble(7,indexFuturesBook.getdPrice());
				prst.setDouble(8,indexFuturesBook.getdOtPrice1());
				prst.setInt(9,indexFuturesBook.getiClosePositionAmount());
				prst.setDouble(10,indexFuturesBook.getdClosePositionMoney());
				prst.setDouble(11,indexFuturesBook.getdClosePositionFee());
				prst.setInt(12,indexFuturesBook.getiOpenPositionAmount());
				prst.setDouble(13,indexFuturesBook.getdOpenPositionMoney());
				prst.setDouble(14,indexFuturesBook.getdOpenPositionFee());
				prst.setString(15,indexFuturesBook.getCuryCode());
				prst.setString(16,indexFuturesBook.getMarketCode());
				prst.executeUpdate();
			}
		} catch (Exception e) {
			throw new YssException("插入期货台帐数据出错！" + e.getMessage());
		} finally {
			dbl.closeStatementFinal(prst);
		}
	}

	private void deleteIndexFuturesData() throws YssException {
		String sqlStr = "Delete from "
				+ pub.yssGetTableName("tb_rep_indexfuturesData")
				+ " where FBargainDate between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPORTCODE ="+ dbl.sqlString(this.portCode)
				+ (brokeCode.trim().length()==0?"":" and FBROKERCODE="+dbl.sqlString(this.brokeCode));
		try {
			dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("生成期货台帐表数据时，删除期货台帐交易数据出错......" + e.getMessage());
		}

	}

	private void insertToIndexFutureStock(HashMap valueMap) throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		IndexFuturesStockBean indexFuturesStock = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				+ pub.yssGetTableName("tb_rep_indexfutureStock")
				+ " (FSTORAGEDATE,FSECURITYCODE ,FPORTCODE ,FBROKERCODE ,FFUType ,FClosePositionRatio,FCarryOverCost,FCarryOverMV,"
				+ "  FClosePositionLossGain,FAmount,FCost,FAdjustMV ,FLossGain,FDayLossGain,FSettlementMoney, FAdjustMargins,FCuryCode,FLOSSGAINDAY,FMarketCode)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				indexFuturesStock = (IndexFuturesStockBean) valueMap.get((String) it.next());

				prst.setDate(1, YssFun.toSqlDate(indexFuturesStock.getdStockDate()));
				prst.setString(2, indexFuturesStock.getsSecuritycode());
				prst.setString(3, indexFuturesStock.getsPortCode());
				prst.setString(4, indexFuturesStock.getsBrokerCode());
				prst.setString(5, indexFuturesStock.getsFuType());
				prst.setDouble(6, indexFuturesStock.getdClosePositionRatio());
				prst.setDouble(7, indexFuturesStock.getdCarryOverCost());
				prst.setDouble(8, indexFuturesStock.getdCarryOverMV());
				prst.setDouble(9, indexFuturesStock.getdClosePositionLossGain());
				prst.setDouble(10, indexFuturesStock.getdAmount());
				prst.setDouble(11, indexFuturesStock.getdCost());
				prst.setDouble(12, indexFuturesStock.getdAdjustMV());
				prst.setDouble(13, indexFuturesStock.getdTotalLossGain());
				prst.setDouble(14, indexFuturesStock.getdDayLossGain());
				prst.setDouble(15, indexFuturesStock.getdSettlementMoney());
				prst.setDouble(16, indexFuturesStock.getdAdjustMargins());
				prst.setString(17, indexFuturesStock.getCuryCode());
				prst.setDouble(18, indexFuturesStock.getLossGain_day());
				prst.setString(19, indexFuturesStock.getMarketCode());
				prst.executeUpdate();
			}
		} catch (Exception e) {
			throw new YssException("插入期货台帐数据出错！" + e.getMessage());
		} finally {
			dbl.closeStatementFinal(prst);
		}
	}
	
	private void deleteIndexFutureStock() throws YssException {
		String sqlStr = "Delete from "
				+ pub.yssGetTableName("tb_rep_indexfutureStock")
				+ " where FSTORAGEDATE between "
				+ dbl.sqlDate(this.dBeginDate)
				+ " and "
				+ dbl.sqlDate(this.dEndDate)
				+ " and FPORTCODE ="+ dbl.sqlString(this.portCode)
				+ (brokeCode.trim().length()==0?"":" and FBROKERCODE="+dbl.sqlString(this.brokeCode));
		try {
			dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("生成期货台帐表数据时，删除期货台帐库存数据出错......" + e.getMessage());
		}

	}
	
	
}
