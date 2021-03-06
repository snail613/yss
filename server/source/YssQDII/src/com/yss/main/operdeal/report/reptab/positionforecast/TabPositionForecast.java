package com.yss.main.operdeal.report.reptab.positionforecast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.base.BaseAPOperValue;
import com.yss.commeach.EachRateOper;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.reptab.positionforecast.pojo.PositionForecastBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.util.YssUtil;


/*****************************************************
 * MS01669 QDV4工银2010年8月25日05_A  
 * 工银头寸预测表
 * @author jiangshichao 2010.10.28
 *
 */
public class TabPositionForecast extends BaseAPOperValue {

	// ~ 前台传过来的参数
	private java.util.Date dDate;//估算日期
	private String portCode = "";//组合代码
    private boolean isContain = false;//其他币种待进款是否加入预测数
    private BaseOperDeal settingOper;
    
    //private static final int ForecastDate_begin = 1;
    //private static final int ForecastDate_end = 3;
    // 487 QDV4工银2011年01月07日01_AB by qiuxufeng 20110308 工银头寸表需改为按工作日预测未来头寸
    // 改为普通变量，通过节假日重新赋值
    private int ForecastDate_begin = 1;
    private int ForecastDate_end = 3;
    // 487 QDV4工银2011年01月07日01_AB by qiuxufeng 20110308 工银头寸表需改为按工作日预测未来头寸
    
    private class Asset {
    	
    	java.sql.Date dForecastDate;        //估算日期
    	String sCashCode ;                  //账户代码
    	//String sType ;                      //金额、余额标志
    	//String sCuryCode;                   //货币代码
    	double money ;                      //金额_原币 
    	double portMoney;                   //金额_本币
    	//int iInOut;                         //流入流出标识
    	
    	public Asset(){
    		
    	}
    }
    
	// =====================================================================================//
	public TabPositionForecast() {

	}

	// =====================================================================================//

	public void init(Object bean) throws YssException {
		String reqAry[] = null;
		String reqAry1[] = null;
		String sRowStr = (String) bean;
		if (sRowStr.trim().length() == 0) {
			return;
		}
		reqAry = sRowStr.split("\n");
		reqAry1 = reqAry[0].split("\r");
		this.dDate = YssFun.toDate(reqAry1[1]);
		reqAry1 = reqAry[1].split("\r");
		this.portCode = reqAry1[1];
		reqAry1 = reqAry[2].split("\r");
		if (reqAry1[1].equalsIgnoreCase("0")) { // 若为0，则只查询已生成的报表数据
			this.isContain = true;
		} else { 
			this.isContain = false;
		}
	}

	public Object invokeOperMothed() throws YssException {
		
		try {
			// 487 QDV4工银2011年01月07日01_AB by qiuxufeng 20110308 工银头寸表需改为按工作日预测未来头寸
			BaseOperDeal deal = new BaseOperDeal();
            deal.setYssPub(pub);
            this.checkHoliday(this.dDate, "CHGZ"); // 检查是否设置了节假日，节假日代码固定为 CHGZ
            ForecastDate_end = YssFun.dateDiff(this.dDate,
                                               deal.getWorkDay("CHGZ", this.dDate, 3)); // 未来第三个工作日和当日相差的天数
            // 487 QDV4工银2011年01月07日01_AB by qiuxufeng 20110308 end
			processDealData();
			// ------------------------------------------------------------
		} catch (YssException ex) {
			throw new YssException(ex.getMessage());
		}
		return "";
	}

	// =====================================================================================//

	/**
	 * 执行从各个报表获取数据的动作
	 * 
	 * @throws YssException
	 */
	private void processDealData() throws YssException {

		HashMap cashInMap = null;      //待进款
		HashMap cashOutMap = null;     //待出款
		HashMap cashAdjustMap = null;  //其他款
		HashMap balanceMap = null;     //账面余额
		HashMap valueMap = null;
		
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		try {
			conn.setAutoCommit(false);
		    bTrans = true;
		    
		    cashInMap = initCashInMap();
		    cashOutMap = initCashOutMap();
		    cashAdjustMap = initCashAdjustMap();
		    balanceMap = initBalanceMap();
		    valueMap = setResultValue(cashInMap,cashOutMap,cashAdjustMap,balanceMap);
		    deleteForecastPositionData();
		    insertToForecastPositionData(valueMap);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	// =====================================================================================//
	/**
	 * 将数据封装放入HashMap中。
	 * 
	 * @param valueMap
	 *            HashMap
	 * @param rs
	 *            ResultSet
	 * @throws YssException
	 */
	private HashMap setResultValue(HashMap cashInMap,HashMap cashOutMap,HashMap cashAdjustMap,HashMap balanceMap)
			throws YssException {
        
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		PositionForecastBean positionForecast = null;
		//add by yanghaiming  20101107
		PositionForecastBean pstForecast = null;
		String key = "";
		//add by yanghaiming 20101107 
		HashMap positionForecastMap = null;
		java.util.Date dForecastDate = null; //预测日期
		double dBal =0;                      //账面余额_原币
		double dPortBal = 0;                 //账面余额_本位币
		double dCashInMoney = 0;             //待进款_原币
		double dCashInPortMoney = 0;         //待进款_本位币
		double dCashOutMoney = 0;            //待出款_原币
		double dCashOutPortMoney = 0;        //待出款_本位币
		double dAdjustPortMoeny = 0;         //其他款_本位币
		double dAdjustMoney =0;              //其他款_原币
		double dForecastBal = 0;             //预测款_原币
		double dForecastPortBal = 0;         //预测款_本位币
		
		Asset  balanceBean = null;
		Asset  cashInBean = null;
		Asset  cashOutBean = null;
		Asset  cashAdjustBean = null;
		
		int iForecastDays = 0;

		
		if(cashInMap == null && cashOutMap == null && cashAdjustMap == null && balanceMap==null ){
			return null;
		}

		positionForecastMap = new HashMap();
		
		try {
			
            
			buf.append(" select fcashacccode as FCASHCODE, fcurycode as FCURYCODE from "+pub.yssGetTableName("tb_para_cashaccount"));
			buf.append(" where fcheckstate = 1 and facctype <> '04' and fstate = 0 "); //账户限制条件：已审核，账户类型不包括虚拟账户，账户状态为可用
			buf.append(" and fportcode = "+dbl.sqlString(this.portCode));
			buf.append(" and fstartdate <="+dbl.sqlDate(this.dDate) );
			buf.append(" group by fcashacccode, fcurycode");
			
			rs = dbl.openResultSet(buf.toString());
			//循环账户
			while (rs.next()) {
				
				iForecastDays =0;
				//循环预测天数
				for(iForecastDays = ForecastDate_begin;iForecastDays<=ForecastDate_end;iForecastDays++){
					
					dForecastDate = YssFun.addDay(this.dDate,iForecastDays);
					
					//edit by yanghaiming 20101107 变量的值需在每次循环前归0
					dBal = 0;
					dPortBal = 0;
					dCashInMoney = 0;
					dCashInPortMoney = 0;
					dCashOutMoney = 0;
					dCashOutMoney = 0;
					dAdjustMoney = 0;
					dAdjustPortMoeny = 0;
					//edit by yanghaiming 20101107 变量的值需在每次循环前归0
					/**************************************************************
					 * 1. 账面余额 取的是前一日的对应账户之和
					 *        T+1 日 的账面余额 ，取得是 T日对应的账户余额
					 *  注意： 由于T+1 还没有余额，所以T+2日的余额取的是T+1日该账户对应的预测数
					 */
					//edit by yanghaiming 20101107 T+2日之后的账面余额取前一日的预测数
					if(iForecastDays == 1){
						if(balanceMap == null){
							dBal = 0;
							dPortBal = 0;
						}else{
							balanceBean = (Asset) balanceMap.get(YssFun.formatDate(this.dDate)+"\f\f"+ rs.getString("FCASHCODE"));//edit by yanghaiming 20101107 取的是T日的余额
							if (iForecastDays == ForecastDate_begin) {
									
								dBal = balanceBean == null?0:balanceBean.money;
								dPortBal = balanceBean == null?0:balanceBean.portMoney;
									
							} else {
								dBal = balanceBean == null?0:balanceBean.money;
								dPortBal = balanceBean == null?0:balanceBean.portMoney;
									
							}
						}
					}else{
						key = YssFun.formatDate(YssFun.addDay(dForecastDate,-1))+"\f\f"+rs.getString("FCASHCODE");//取得前一日的预测数
						pstForecast = (PositionForecastBean)positionForecastMap.get(key);
						if(pstForecast == null){
							dBal = 0;
							dPortBal = 0;
						}else{
							dBal = pstForecast.getForecastBal();
							dPortBal = pstForecast.getForecastPortBal();
						}
					}
					//edit by yanghaiming 20101107 T+2日之后的账面余额取前一日的预测数
					
					//~ 2.待进款
					if(cashInMap == null){
						dCashInMoney = 0;
						dCashInPortMoney = 0;
					}else{
						cashInBean = (Asset) cashInMap.get(YssFun.formatDate(dForecastDate)+"\f\f"+ rs.getString("FCASHCODE"));
						
						dCashInMoney = cashInBean==null?0:cashInBean.money;
						dCashInPortMoney = cashInBean==null?0:cashInBean.portMoney;
					}
					
					
					//~ 3. 待出款
					if(cashOutMap == null){
						dCashOutMoney = 0;
						dCashOutPortMoney = 0;
					}else{
						cashOutBean = (Asset)cashOutMap.get(YssFun.formatDate(dForecastDate)+"\f\f"+rs.getString("FCASHCODE"));
						dCashOutMoney = cashOutBean==null?0:cashOutBean.money;
						dCashOutPortMoney = cashOutBean==null?0:cashOutBean.portMoney;
					}
					
					
					//~ 4.其他款：如果没有调增金额的赋默认值0.
					if(cashAdjustMap == null){
						dAdjustMoney = 0;
						dAdjustPortMoeny = 0;
					}else{
						cashAdjustBean = (Asset)cashAdjustMap.get(YssFun.formatDate(dForecastDate)+"\f\f"+rs.getString("FCASHCODE"));
					    dAdjustMoney = cashAdjustBean==null?0:cashAdjustBean.money;
					    dAdjustPortMoeny = cashAdjustBean==null?0:cashAdjustBean.portMoney;
					}
					
					
					
					//~ 4. 预测数    人民币待进款加入预测数，其他币种待进款项是否加入预测数可选择
					if(rs.getString("FCURYCODE").equalsIgnoreCase("CNY")){
						dForecastBal =  dBal + dCashInMoney - dCashOutMoney - dAdjustPortMoeny;
						dForecastPortBal = dForecastBal ;
					}else{
						dForecastBal = dBal + (isContain?dCashInMoney:0) - dCashOutMoney - dAdjustMoney;
						dForecastPortBal = dPortBal + (isContain?dCashInPortMoney:0) - dCashOutPortMoney - dAdjustPortMoeny;
					}
				
					
					//==================================================================================================
					//组合下的所有账户数据（账面金额、待进款、待出款、其他、预测数）都为0，那么不显示该账户
					if(dBal==0 && dCashInMoney ==0 && dCashOutMoney ==0 && dAdjustMoney ==0){
						continue;
					}
					positionForecast = new PositionForecastBean();
					positionForecast.setDealDate(YssFun.toSqlDate(this.dDate));  //估算日期
					positionForecast.setForecastDate(YssFun.toSqlDate(dForecastDate)); //预测日期
					positionForecast.setPortCode(this.portCode); //组合代码
					positionForecast.setCashAccCode(rs.getString("FCASHCODE")); //账户代码
					positionForecast.setCuryCode(rs.getString("FCURYCODE")); //币种代码
					
					//账面余额
					positionForecast.setBal(dBal);
					positionForecast.setPortBal(dPortBal);
					positionForecast.setForecastInMoney(dCashInMoney);
					positionForecast.setForecastInPortMoney(dCashInPortMoney);
					positionForecast.setForecastOutMoney(dCashOutMoney);
					positionForecast.setForecastOutPortMoney(dCashOutPortMoney);
					positionForecast.setdOtherMoney(dAdjustMoney);
					positionForecast.setForecastBal(dForecastBal);
					positionForecast.setForecastPortBal(dForecastPortBal);
					
				
					positionForecastMap.put(YssFun.formatDate(dForecastDate)+"\f\f"+rs.getString("FCASHCODE"), positionForecast);
					
					
				}
			}
			
			return positionForecastMap;
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 将数据插入数据库
	 * 
	 * @param valueMap
	 *            HashMap
	 * @throws YssException
	 */
	private void insertToForecastPositionData(HashMap valueMap)
			throws YssException {
		if (null == valueMap || valueMap.isEmpty()) {
			return;
		}
		PositionForecastBean positionForecast = null;
		Object object = null;
		PreparedStatement prst = null;
		String sqlStr = "insert into "
				+ pub.yssGetTableName("Tb_rep_forecastposition")
				+ " (FDATE,FPORTCODE,FCASHCODE,FCURYCODE,FFORECASTDATE,FBAL,FPORTBAL, "
				+ " FFORECASTINMONEY,FFORECASTINPORTMONEY,FFORECASTOUTMONEY,FFORECASTOUTPORTMONEY," 
				+ " FOTHERMONEY,FFORECASTBAL,FFORECASTPORTBAL)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			prst = dbl.openPreparedStatement(sqlStr);
			Iterator it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				positionForecast = (PositionForecastBean) valueMap.get((String) it.next());
				
				prst.setDate(1, YssFun.toSqlDate(this.dDate));
				prst.setString(2, this.portCode);
				prst.setString(3, positionForecast.getCashAccCode());
				prst.setString(4, positionForecast.getCuryCode());
				prst.setDate(5, positionForecast.getForecastDate());
				
				prst.setDouble(6, YssFun.roundIt(positionForecast.getBal(), 2));
				prst.setDouble(7, YssFun.roundIt(positionForecast.getPortBal(), 2));
				prst.setDouble(8, YssFun.roundIt(positionForecast.getForecastInMoney(),2));
				prst.setDouble(9, YssFun.roundIt(positionForecast.getForecastInPortMoney(), 2));
				prst.setDouble(10, YssFun.roundIt(positionForecast.getForecastOutMoney(), 2));
				prst.setDouble(11, YssFun.roundIt(positionForecast.getForecastOutPortMoney(),2));
				prst.setDouble(12, YssFun.roundIt(positionForecast.getdOtherMoney(), 2));
				prst.setDouble(13, YssFun.roundIt(positionForecast.getForecastBal(), 2));
				prst.setDouble(14, YssFun.roundIt(positionForecast.getForecastPortBal(),2));
				
				prst.executeUpdate();
			}
		} catch (Exception e) {
			throw new YssException("【统计工银头寸预测表时，插入工银头寸预测数据出错......】" + e.getMessage());
		} finally {
			dbl.closeStatementFinal(prst);
		}
	}

	/**
	 * 从tb_rep_DaysSettlement表中按要求删除相关数据
	 * 
	 * @throws YssException
	 */
	private void deleteForecastPositionData() throws YssException {
		String sqlStr = "Delete from "
				+ pub.yssGetTableName("tb_rep_forecastposition")
				+ " where FDate = "+ dbl.sqlDate(this.dDate)
				+ " and FPORTCODE ="+ dbl.sqlString(this.portCode);
		try {
			dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("【统计工银头寸预测表时，删除工银头寸预测数据出错......】" + e.getMessage());
		}

	}
	
	
	private HashMap initCashInMap() throws YssException {

		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		HashMap cashInMap = null;
		boolean doInitFlag = true;//用于控制HashMap的创建
		Asset asset = null;
		double dBaseRate = 0;
        double dPortRate = 0;
        EachRateOper rateOper = null; //新建获取利率的通用类
		
		try {
            buf.append(" select b.fforecastdate,b.fcashcode,b.FMoney,c.fcurycode from (select fforecastdate,fcashcode,sum(FMoney) as FMoney from ");
            buf.append(" (select FFactSettleDate as fforecastdate ,FFactSettleMoney as FMoney ,FFactCashAccCode as fcashcode from  "+pub.yssGetTableName("tb_data_subtrade")+" a ");
            buf.append(" where fcheckstate = 1 and ftradetypecode in ('02', '10', '24', '42', '79') and fportcode ="+dbl.sqlString(this.portCode));
            buf.append(" and exists (select fsecuritycode  from "+pub.yssGetTableName("tb_para_security")+" b where fcatcode in ('RE','EQ','DR') and fcheckstate=1 and a.fsecuritycode = b.fsecuritycode) ");
            buf.append(" and FFactSettleDate between "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_begin))+" and "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_end)));
            buf.append(" union select FSettleDate as fforecastdate,fsellmoney as FMoney,fcashacccode as fcashcode from "+pub.yssGetTableName("tb_ta_trade"));
            buf.append(" where fcheckstate = 1 and fselltype in (00, 01, 04, 06) and fportcode ="+dbl.sqlString(this.portCode));
            buf.append(" and FSettleDate between "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_begin))+" and "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_end)));
            buf.append(" union select fbsettledate as fforecastdate, fbmoney as FMoney, fbcashacccode as fcashcode from  "+pub.yssGetTableName("tb_data_RateTrade"));
            buf.append(" where fcheckstate = 1 and fportcode ="+dbl.sqlString(this.portCode));
            buf.append(" and fbsettledate between "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_begin))+" and "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_end)));
            buf.append(" )group by fforecastdate,fcashcode)b");
            buf.append(" left join (select fcashacccode,fcurycode from "+pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate=1 group by fcashacccode,fcurycode)c");
            buf.append(" on b.fcashcode = c.fcashacccode");
            
			rs = dbl.openResultSet(buf.toString());
			
			while(rs.next()){
				//懒加载机制，如果要用到，才进行初始化
				if(doInitFlag){
					cashInMap = new HashMap();
					rateOper = new EachRateOper();
					rateOper.setYssPub(pub);
					doInitFlag = false;
				}
				//获取指定日期基础汇率、组合汇率。如果当天没有汇率则取与指定日期最近那天的汇率
				dBaseRate = this.getSettingOper().getCuryRate(this.dDate,rs.getString("FCuryCode"),this.portCode,YssOperCons.YSS_RATE_BASE);
				rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), this.portCode);
           	    dPortRate = rateOper.getDPortRate();
           	   //初始化内部类
				asset = new Asset();
				asset.dForecastDate = rs.getDate("fforecastdate");
				asset.sCashCode = rs.getString("fcashcode");
				asset.money = rs.getDouble("FMoney");
				asset.portMoney = this.getSettingOper().calPortMoney(rs.getDouble("FMoney"),dBaseRate, dPortRate,rs.getString("FCuryCode"), this.dDate, this.portCode);
				//存储到HashMap中，键：估算日期+账户代码
				cashInMap.put(YssFun.formatDate(asset.dForecastDate)+"\f\f"+asset.sCashCode, asset);
			}
			
			return cashInMap;
		} catch (Exception e) {
			throw new YssException("【统计工银头寸预测表时，初始化待进款出错......】"
					+ e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	private HashMap initCashOutMap() throws YssException {

		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		HashMap cashOutMap = null;
		boolean doInitFlag = true;//用于控制HashMap的创建
		Asset asset = null;
		double dBaseRate = 0;
        double dPortRate = 0;
        EachRateOper rateOper = null; //新建获取利率的通用类
		try {
			    buf.append(" select b.fforecastdate,b.fcashcode,b.FMoney,c.fcurycode from (select fforecastdate,fcashcode,sum(FMoney) as FMoney from ");
			    buf.append(" (select FFactSettleDate as fforecastdate ,FFactSettleMoney as FMoney ,FFactCashAccCode as fcashcode from "+pub.yssGetTableName("tb_data_subtrade")+" a");
			    buf.append(" where fcheckstate = 1 and ftradetypecode in ('01', '03', '04', '05', '08','25','40','78') and fportcode = "+dbl.sqlString(this.portCode));
			    buf.append(" and exists (select fsecuritycode  from "+pub.yssGetTableName("tb_para_security")+" b where fcatcode in ('RE','EQ','DR') and fcheckstate=1 and a.fsecuritycode = b.fsecuritycode)");
			    buf.append(" and FFactSettleDate between "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_begin))+" and "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_end)));
			    buf.append(" union select FSettleDate as fforecastdate,fsellmoney as FMoney,fcashacccode as fcashcode from "+pub.yssGetTableName("tb_ta_trade"));
			    buf.append(" where fcheckstate = 1  and fselltype in ('02', '03', '05', '07') and fportcode = "+dbl.sqlString(this.portCode));
			    buf.append(" and FSettleDate between "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_begin))+" and "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_end)));
			    buf.append(" union select fsettledate as fforecastdate, fsmoney+fscuryfee as FMoney, fscashacccode as fcashcode from "+pub.yssGetTableName("tb_data_RateTrade"));
			    buf.append(" where fcheckstate = 1  and fportcode = "+dbl.sqlString(this.portCode));
			    buf.append(" and fsettledate between "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_begin))+" and "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_end)));
			    buf.append(" union select fbsettledate as fforecastdate, fbcuryfee as FMoney, fbcashacccode as fcashcode from "+pub.yssGetTableName("tb_data_RateTrade"));
			    buf.append(" where fcheckstate = 1 and fbportcode ="+dbl.sqlString(this.portCode));
			    buf.append(" and fbsettledate between "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_begin))+" and "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_end)));
			    buf.append(" )group by fforecastdate,fcashcode)b");
			    buf.append(" left join (select fcashacccode,fcurycode from "+pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate=1 group by fcashacccode,fcurycode)c ");
			    buf.append(" on b.fcashcode = c.fcashacccode");
			    
				rs = dbl.openResultSet(buf.toString());
				
				while(rs.next()){
					//懒加载机制，如果要用到，才进行初始化
					if(doInitFlag){
						cashOutMap = new HashMap();
						rateOper = new EachRateOper();
						rateOper.setYssPub(pub);
						doInitFlag = false;
					}
					//获取指定日期基础汇率、组合汇率。如果当天没有汇率则取与指定日期最近那天的汇率
					dBaseRate = this.getSettingOper().getCuryRate(this.dDate,rs.getString("FCuryCode"),this.portCode,YssOperCons.YSS_RATE_BASE);
					rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), this.portCode);
	           	    dPortRate = rateOper.getDPortRate();
	           	    //初始化内部类
					asset = new Asset();
					asset.dForecastDate = rs.getDate("fforecastdate");
					asset.sCashCode = rs.getString("fcashcode");
					asset.money = rs.getDouble("FMoney");
					asset.portMoney = this.getSettingOper().calPortMoney(rs.getDouble("FMoney"),dBaseRate, dPortRate,rs.getString("FCuryCode"), this.dDate, this.portCode);
					//存储到HashMap中，键：估算日期+账户代码
				    cashOutMap.put(YssFun.formatDate(asset.dForecastDate)+"\f\f"+asset.sCashCode, asset);
				}
			return cashOutMap;
		} catch (Exception e) {
			throw new YssException("【统计工银头寸预测表时，初始化待出款出错......】"+ e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	private HashMap initCashAdjustMap() throws YssException {
		double dBaseRate = 0;
        double dPortRate = 0;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		HashMap cashAdjustMap = null;
		boolean doInitFlag = true;//用于控制HashMap的创建
		Asset asset = null;
		try {
			
            buf.append(" select fforecastdate,fcashcode,fcurycode,fadjustmoney ");
            buf.append(" from "+pub.yssGetTableName("tb_data_adjustmoney"));
            buf.append(" where fcheckstate=1 ");
            buf.append(" and fportcode="+dbl.sqlString(this.portCode));
            buf.append(" and fforecastdate between "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_begin))+" and "+dbl.sqlDate(YssFun.addDay(this.dDate,ForecastDate_end)));
            
			rs = dbl.openResultSet(buf.toString());
			
			while(rs.next()){
				//懒加载机制，如果要用到，才进行初始化
				if(doInitFlag){
					rateOper = new EachRateOper();
					rateOper.setYssPub(pub);
					cashAdjustMap = new HashMap();
					doInitFlag = false;
				}
				//获取指定日期基础汇率、组合汇率。如果当天没有汇率则取与指定日期最近那天的汇率
				dBaseRate = this.getSettingOper().getCuryRate(this.dDate,rs.getString("FCuryCode"),this.portCode,YssOperCons.YSS_RATE_BASE);
				rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), this.portCode);
           	    dPortRate = rateOper.getDPortRate();
             	//初始化内部类
				asset = new Asset();
				asset.dForecastDate = rs.getDate("fforecastdate");
				asset.sCashCode = rs.getString("fcashcode");
				asset.money = rs.getDouble("fadjustmoney");
				asset.portMoney = this.getSettingOper().calPortMoney(rs.getDouble("fadjustmoney"),dBaseRate, dPortRate,rs.getString("FCuryCode"), this.dDate, this.portCode);
				//存储到HashMap中，键：估算日期+账户代码              
				cashAdjustMap.put(YssFun.formatDate(asset.dForecastDate)+"\f\f"+asset.sCashCode, asset);
			}
			
			return cashAdjustMap;
		} catch (Exception e) {
			throw new YssException("【统计工银头寸预测表时，初始化其他款出错......】"
					+ e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/*************************************
	 * 账面余额初始化
	 * 根据组合和估算日期查询现金库存表
	 */
	private HashMap initBalanceMap() throws YssException {
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		HashMap balanceMap = null;
		boolean doInitFlag = true;//用于控制HashMap的创建
		Asset asset = null;
		try {
			/**shashijie 2012-5-22 BUG 4614 */
			buf.append(" select fcashacccode, FAccBalance, FBaseCuryRate,FPortCuryRate ");
			buf.append(" from "+pub.yssGetTableName("tb_stock_cash"));
			buf.append(" where fcheckstate = 1");
			buf.append(" and fportcode ="+dbl.sqlString(this.portCode));
			buf.append(" and fstoragedate ="+dbl.sqlDate(this.dDate));
			/**end*/
			
			rs = dbl.openResultSet(buf.toString());
			while(rs.next()){
				if(doInitFlag){
					balanceMap = new HashMap();
					doInitFlag = false;
				}
				asset = new Asset();
				asset.dForecastDate = YssFun.toSqlDate(this.dDate);
				asset.sCashCode = rs.getString("fcashacccode");
				asset.money = rs.getDouble("FAccBalance");
				/**shashijie 2012-5-22 BUG 4614 组合货币余额不直接取,改用汇率计算得出 */
				double portMoney = YssD.div(YssD.mul(asset.money, rs.getDouble("FBaseCuryRate"))
						,rs.getDouble("FPortCuryRate"));
				asset.portMoney = portMoney;
				/**end*/
				
				balanceMap.put(YssFun.formatDate(asset.dForecastDate)+"\f\f"+asset.sCashCode, asset);
			}
			return balanceMap;
		} catch (Exception e) {
			throw new YssException("【统计工银头寸预测表时，初始化账面余额出错......】"
					+ e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
    
	//获取指定日期汇率
	public BaseOperDeal getSettingOper() {
        if (settingOper == null) {
            if (pub != null) {
                settingOper.setYssPub(pub);
            }
            settingOper = (BaseOperDeal) pub.getOperDealCtx().getBean(
                "baseoper");
        }
        return settingOper;
    }
	
	/**
     * add by qiuxufeng 20110316 487 QDV4工银2011年01月07日01_AB
     * 检查当前的年份是否设置了节假日
     * strYear 表示当前日期的年份
     */
    public void checkHoliday(java.util.Date dDate,String strHoliday) throws YssException{
    	
    	ResultSet rsTemp = null;
    	
    	try{
    		String strYear = String.valueOf(YssFun.getYear(dDate));
        
    		String strSql = "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = " 
    			+ dbl.sqlString(strHoliday) + " and FCheckState=1 " 
    			+ "and FDate >= to_date('" + strYear + "-01-01','yyyy-MM-dd') "
    			+ "and FDate <= to_date('" + strYear + "-12-31','yyyy-MM-dd') ";
        
    		rsTemp = dbl.openResultSet(strSql);
    		if (!rsTemp.next()){
    			dbl.closeResultSetFinal(rsTemp);
    			throw new YssException(" 节假日群【" + strHoliday + "】没有【"+ strYear + "】年的节假日信息！");
    		}
    	}
    	catch(Exception e){
    		throw new YssException(e.getMessage());
    	}
    	finally{
    		dbl.closeResultSetFinal(rsTemp);
    	}
    }
}
