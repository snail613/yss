package com.yss.main.operdeal.valuation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.yss.commeach.EachRateOper;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.opermanage.ForwardExManage;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.MTVMethodBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/** shashijie 2011-08-11 STORY 1434 
 * 计算可退替代款估值增值，并生成相应的现金应收应付*/

public class ValETFBackCashMV
    extends BaseValDeal {
    public ValETFBackCashMV() {
    }

    /**
     * 计算可退替代款的估值增值，公式如下：
     * 每日发生额 = SUM{T日证券剩余数量 * [当日收盘价或最近收盘价 + （T日总派息 + T 日权证价值 – T日申购数量 * T日单位成本）/ （T日申购数量 + T 日权益总数量）]} – 昨日余额
     * 今日余额 = 昨日余额 + 今日发生额
     * @param mtvBeans ArrayList
     * @return HashMap
     * @throws YssException
     */
    public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {
        MTVMethodBean vMethod = null;
        CashPecPayBean CashRP = null;
        HashMap hmResult = new HashMap();
        String sKey = "";
        String strSql = "";
        String strCashAccCode = "";
        String strCuryCode = "";
        StringBuffer buff = null;
        ResultSet rs = null;
        double dTCost = 0;
        double dTotalCost = 0;
        double dTotalAmount = 0;
        double dMVPerSec = 0;//单个证券估值增值
        double dBaseMVPerSec = 0;//单个证券估值增值基础货币余额
        double dPortMVPerSec = 0;//单个证券估值增值组合币余额
        double dTotalMV = 0;//估值增值余额
        double dBaseTotalMV = 0;//估值增值基础货币余额
        double dPortTotalMV = 0;//估值增值组合币余额
        double dYesTotalMV = 0;//昨日估值增值余额
        double dYesBaseTotalMV = 0;//昨日估值增值基础货币余额
        double dYesPortTotalMV = 0;//昨日估值增值组合币余额
        EachRateOper rateOper = new EachRateOper(); //获取利率的通用类
        rateOper.setYssPub(pub);
        double dBaseRate = 1.0;
        double dPortRate = 1.0;
        HashMap hmSecTradeCuryRate = new HashMap();//存放成份股交易币种在估值日的汇率
        String secTradeCuryRateKey = "";
        String secTradeCuryRateValue = "";
        double secTradeCuryBaseRate = 1.0;
        double secTradeCuryPortRate = 1.0;
        boolean bHaveData = false;
        HashMap hMV = new HashMap();
        try{
    		ETFParamSetAdmin etfParamAdmin = new ETFParamSetAdmin();
    		etfParamAdmin.setYssPub(pub);
    		ETFParamSetBean etfParamBean = new ETFParamSetBean();
    		HashMap hm = new HashMap();
    		hm = etfParamAdmin.getETFParamInfo(portCode);
    		etfParamBean = (ETFParamSetBean) hm.get(portCode);
    		//暂定实时加均摊,单位补票的补票方式才处理可退替代款的估值增值
    		if(etfParamBean == null || (!etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)
    				&& !etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_ONE)
    				&& !etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ))){//shashijie 考虑易方达
    			return hmResult;
    		}
    		/**shashijie 2011-08-10 易方达也要计算可退替代款的估值增值 STORY 1434 */
    		if (etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_ONE)) {
    			calAccBookRefundMV();
    			operReduceMV();
    			hmResult = insertCashRecPay();

    			return hmResult;
			}
    		/**--------------end-----------*/
    		
            for (int i = 0; i < mtvBeans.size(); i++) {
                vMethod = (MTVMethodBean) mtvBeans.get(i);
                CashRP = new CashPecPayBean();
                strSql = "select a.*,b.FCashAccCode,CA.FCuryCode,p.fprice,se.FTradeCury," + 
                	"psh.fprice as fshprice,rash.FBaseRate as FSHBaseRate,rash.FPortRate as FSHPortRate from (" +
                	" select sum(FMakeupAmount) as FMakeupAmount,sum(FBBInterest) as FBBInterest," + 
                	"sum(FBBWarrantCost) as FBBWarrantCost,sum(FSumAmount) as FSumAmount,sum(FRemaindAmount) as FRemaindAmount," + 
                	" fportcode,fbuydate,fsecuritycode,fbs from (" + 
                	" select s.*,sb.fmaxdate from " + pub.yssGetTableName("Tb_ETF_StandingBook") +
                	" s join (select max(fdate) as fmaxdate,fnum from " + pub.yssGetTableName("Tb_ETF_StandingBook") +
                	" where fdate <= " + dbl.sqlDate(dDate) + 
                	" and FportCode = " + dbl.sqlString(portCode) + 
                	" group by fnum) sb" + 
                	" on sb.fmaxdate = s.fdate and s.fnum = sb.fnum" + 
                	" where s.FportCode = " + dbl.sqlString(portCode) + 
                	//获取没有补票完成的台帐数据，按券计算可退替代款估增，汇总股票明细数据
                	" and s.fstockholdercode = ' ' " + 
                	" and " + dbl.sqlIsNull("fremaindamount", "0") + 
                	//通过关联TA交易数据，将没有确认的申赎数据排除
                	" <> 0 ) group by fportcode,fbuydate,fsecuritycode,fbs) a " + 
                	" join (select distinct ftradedate,fportcode from " + pub.yssGetTableName("Tb_TA_Trade") + 
                	" where fconfimdate <= " + dbl.sqlDate(dDate) + 
                	" and fportcode = " + dbl.sqlString(portCode) + 
                	" and fselltype in('01','02') and fcheckstate = 1) ta on ta.ftradedate = a.fbuydate left join " +      
                    //关联ETF参数设置，获取该组合的现金账户及现金账户对应的币种
                    pub.yssGetTableName("Tb_ETF_Param") + " b on b.FPortCode = a.FPortCode " +
                    " left join (select ca1.FCashAccCode,ca1.FStartDate,ca2.FCuryCode from " +
                    " (select FCashAccCode, max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where FStartDate <= " + dbl.sqlDate(dDate) +
                    " and FCheckState = 1 and FState =0 group by FCashAccCode) ca1 join (select FCashAccCode,FStartDate,FCuryCode from " + 
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where FCheckState = 1 and FState =0) ca2 on ca2.FStartDate = ca1.FStartDate and ca2.FCashAccCode = ca1.FCashAccCode " +
                    ") CA on CA.FCashAccCode = b.FCashAccCode" +
                    //关联证券信息表，获取成份股对应的交易所币种
                    " left join (select FTradeCury, fsecuritycode from " + pub.yssGetTableName("Tb_Para_Security") + 
                    "  where FCheckState = 1) se on a.fsecuritycode = se.fsecuritycode " + 
                    //获取当日或最近收盘价
                    " left join (select * from " + pub.yssGetTableName("Tb_Data_Pretvalmktprice") + 
                    " where fportcode = " + dbl.sqlString(portCode) + " and fvaldate = " + dbl.sqlDate(dDate) + 
                    " and FCheckState = 1 ) p on p.fsecuritycode = a.FSecurityCode " + 
                    //获取申赎日期的收盘价
                    " left join (select * from " + pub.yssGetTableName("Tb_Data_Pretvalmktprice") + 
                    " where fportcode = " + dbl.sqlString(portCode) + 
                    " and FCheckState = 1 ) psh on psh.fsecuritycode = a.FSecurityCode and psh.fvaldate = a.fbuydate " + 
                    //获取申赎日汇率
                    " left join (select FBaseRate, FPortRate,FCuryCode as FSecCuryCode,FPortCode,FOTBaseRate1,FOTBaseRate2,FOTBaseRate3,FValDate from " + 
                    pub.yssGetTableName("Tb_Data_ValRate") + 
                    " where FCheckState = 1 and fportcode = " + dbl.sqlString(portCode) + 
                    " ) rash on a.fportcode = rash.fportcode  and se.FTradeCury = rash.FSecCuryCode and rash.FValDate = a.fbuydate";

                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                	bHaveData = true;
                	//获取估值日成份股交易币种对应的汇率
                	
                	secTradeCuryRateKey = rs.getString("FTradeCury");
                	if(hmSecTradeCuryRate.containsKey(secTradeCuryRateKey)){
                		secTradeCuryRateValue = (String)hmSecTradeCuryRate.get(secTradeCuryRateKey);
                		secTradeCuryBaseRate = Double.parseDouble(secTradeCuryRateValue.split(",")[0]);
                		secTradeCuryPortRate = Double.parseDouble(secTradeCuryRateValue.split(",")[1]);
                	}else{
                		secTradeCuryBaseRate = 1.0;
                		if (!rs.getString("FTradeCury").equalsIgnoreCase(pub.
                        		getPortBaseCury(portCode))) {
							secTradeCuryBaseRate = this.getSettingOper().getCuryRate(dDate,
											vMethod.getBaseRateSrcCode(),
											vMethod.getBaseRateCode(),
											vMethod.getPortRateSrcCode(),
											vMethod.getPortRateCode(),
											rs.getString("FTradeCury"), portCode,
											YssOperCons.YSS_RATE_BASE);
                		}
	                    rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"), portCode, 
	                    		vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                vMethod.getPortRateSrcCode(), vMethod.getPortRateCode()); //用通用方法，获取组合汇率
	                    secTradeCuryPortRate = rateOper.getDPortRate(); //获取组合汇率
	                    hmSecTradeCuryRate.put(secTradeCuryRateKey, secTradeCuryBaseRate + "," + secTradeCuryPortRate);
                	}
                	if (etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)) {
	                	//T日申购数量 * T日单位成本
	                    dTCost = YssD.mul(rs.getDouble("FMakeupAmount"),
	                    						YssD.round(YssD.div(
	                    										YssD.mul(rs.getDouble("fshprice"),rs.getDouble("FSHBaseRate")),
	                    											rs.getDouble("FSHPortRate")),2));
                	}else{
                		dTCost = YssD.mul(rs.getDouble("FMakeupAmount"),rs.getDouble("fshprice"),YssD.div(rs.getDouble("FSHBaseRate"),rs.getDouble("FSHPortRate")));
                		dTCost = YssD.round(dTCost, 2);
                	}
                    //T日总派息 + T 日权证价值 – T日申购数量 * T日单位成本
                    dTotalCost = YssD.add(rs.getDouble("FBBInterest"),rs.getDouble("FBBWarrantCost"),-dTCost);
                    //申购数量 + T 日权益总数量
                    dTotalAmount = YssD.add(rs.getDouble("FMakeupAmount"),rs.getDouble("FSumAmount"));

                    //证券剩余数量 * [当日收盘价或最近收盘价 * 基础汇率 /组合汇率 +（T日总派息 + T 日权证价值 – T日申购数量 * T日单位成本）/ （T日申购数量 + T 日权益总数量）]
                    dMVPerSec = YssD.round(YssD.mul(
                    							YssD.add(rs.getDouble("FRemaindAmount"),0),
                    							YssD.add(YssD.round(YssD.div(YssD.mul(rs.getDouble("FPrice"),secTradeCuryBaseRate),secTradeCuryPortRate),8),
                    									YssD.div(dTotalCost,dTotalAmount))),2);

                    dBaseRate = 1;
                    if (!rs.getString("FCuryCode").equalsIgnoreCase(pub.
                    		getPortBaseCury(portCode))) {//获取当日的基础汇率// QDV4上海2010年12月10日02_A lidaolong 2011.01.26
                        dBaseRate = this.getSettingOper().getCuryRate(dDate,
                            vMethod.getBaseRateSrcCode(),
                            vMethod.getBaseRateCode(),
                            vMethod.getPortRateSrcCode(),
                            vMethod.getPortRateCode(),
                            rs.getString("FCuryCode"), portCode,
                            YssOperCons.YSS_RATE_BASE);
                    }
                    dBaseMVPerSec = this.getSettingOper().calBaseMoney(dMVPerSec, dBaseRate); //计算基础货币金额

                    rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), portCode, vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                              vMethod.getPortRateSrcCode(), vMethod.getPortRateCode()); //用通用方法，获取组合汇率
                    dPortRate = rateOper.getDPortRate(); //获取组合汇率
                    
					// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
					// 则默认组合汇率为1
					if (dPortRate == 0) {
						dPortRate = 1;
					}
					// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
					// 则默认组合汇率为1
                    
                    dPortMVPerSec = this.getSettingOper().calPortMoney(dMVPerSec, dBaseRate,
                        dPortRate,
                        rs.getString("FCuryCode"),
                        dDate,
                        rs.getString("FPortCode")); //计算组合货币金额
                    
                    sKey = YssFun.formatDate(dDate) + "\t" + rs.getString("fportcode") + "\t" + rs.getDate("fbuydate") + "\t" 
						   + rs.getString("fbs") + "\t"  + "1" + "\t"  + rs.getString("fsecuritycode");
                    hMV.put(sKey, new Double(dMVPerSec));

                    dTotalMV += dMVPerSec;
                    dBaseTotalMV += dBaseMVPerSec;
                    dPortTotalMV += dPortMVPerSec;

                    strCashAccCode = rs.getString("FCashAccCode");
                    strCuryCode = rs.getString("FCuryCode");                    
                }
                dbl.closeResultSetFinal(rs);
                
                this.insertRefundMV(hMV,"1");
                //获取昨日可退替代款估值增值余额
                buff = new StringBuffer(200);
                buff.append("select FBal, FBaseCuryBal, FPortCuryBal,FCashAccCode,FCuryCode from ");
                buff.append(pub.yssGetTableName("Tb_Stock_CashPayRec"));
                buff.append(" where FTsfTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV));
                buff.append(" and FSubTsfTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETFBACKCASH_MV));
                buff.append(" and FPortCode = ").append(dbl.sqlString(portCode));
                buff.append(" and FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(dDate, -1)));
                buff.append(" and fcashacccode = (select fcashacccode from ");
                buff.append(pub.yssGetTableName("Tb_ETF_Param"));
                buff.append(" where fportcode = ").append(dbl.sqlString(portCode)).append(")");
                rs = dbl.openResultSet(buff.toString());
                if(rs.next()){
                    dYesTotalMV = rs.getDouble("FBal");
                    dYesBaseTotalMV = rs.getDouble("FBaseCuryBal");
                    dYesPortTotalMV = rs.getDouble("FPortCuryBal");
                    if(!bHaveData){
                    	strCashAccCode = rs.getString("FCashAccCode");
                    	strCuryCode = rs.getString("FCuryCode");
                    }
                }
                CashRP.setPortCode(portCode);
                CashRP.setTradeDate(dDate);
                CashRP.setCashAccCode(strCashAccCode);
                CashRP.setCuryCode(strCuryCode);
                CashRP.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV);
                CashRP.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETFBACKCASH_MV);
                CashRP.setMoney(YssD.sub(dTotalMV, dYesTotalMV));
                CashRP.setBaseCuryRate(dBaseRate);
                CashRP.setBaseCuryMoney(YssD.sub(dBaseTotalMV, dYesBaseTotalMV));
                CashRP.setPortCuryRate(dPortRate);
                CashRP.setPortCuryMoney(YssD.sub(dPortTotalMV, dYesPortTotalMV));
                CashRP.checkStateId = 1;

                sKey = portCode + "\f" + 
                    YssOperCons.YSS_ZJDBLX_MV + "\f" +
                    YssOperCons.YSS_ZJDBZLX_ETFBACKCASH_MV;
                hmResult.put(sKey, CashRP);
            }
            return hmResult;
        }catch(Exception e){
            throw new YssException("计算ETF可退替代款估值增值时出错！",e);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**shashijie 2011-08-12添加注视,产生现金应收应付数据前,为了删除相同类型的数据而添加条件*/
	public Object filterCashCondition() {
        CashPecPayBean cashpay = new CashPecPayBean();
        cashpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV);
        /**shashijie 2011-08-12 STORY 1434 */
        //删除的时候需要做like操作  胡昆  20070918
        cashpay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETFBACKCASH_MV+"%");
        /**end*/
        return cashpay;
    }

    /**
	 * 计算可退替代款估值增值,区分每日申赎
	 * @throws YssException
	 */
	private void calAccBookRefundMV() throws YssException {
		StringBuffer sb = null;
		ResultSet rs = null;
		HashMap hMV = new HashMap();
		String sKey = "";
		String sSumKey = "";
		double dTotalMV = 0;
		try{
			sb = new StringBuffer(500);
			sb.append("select t1.fnum,t1.fportcode,t1.fsecuritycode,t1.fbs,t1.fbuydate,t1.fbraketnum,t1.fhreplacecash,t1.funitcost,")
				.append("rate.fbaserate as fBuyBaseRate,rate.fportrate as fBuyPortRate,")
				/**add---huhuichao 2013-8-9 STORY  4276 博时：跨境ETF补充增加一类公司行动 */
				.append("se.ftradecury,p.fvaldate,p.fprice,ra.FBaseRate,ra.FPortRate,tr1.finterest," +
						"tr1.fwarrantcost,tr1.fotherright,")
				/**end---huhuichao 2013-8-9 STORY  4276*/	
				.append("t1.freplaceamount,case when tr1.fnum1 is null then t1.freplaceamount else tr1.fremaindamount end as fremaindamount")
				.append(" from ").append(pub.yssGetTableName("tb_etf_tradestldtl"))
				//关联TA表，只对已确认的申赎计算可退替代款估值增值
				.append(" t1 join (select ftradedate,case when fselltype = '01' then 'B' else 'S' end as fselltype,fportcode from ")
				.append(pub.yssGetTableName("tb_ta_trade"))
				.append(" where fportcode = ").append(dbl.sqlString(portCode))
				.append(" and fconfimdate <= ").append(dbl.sqlDate(dDate))
				.append(" and fselltype in('01','02') and fcheckstate = 1) ta ")
				.append(" on ta.ftradedate = t1.fbuydate and ta.fselltype = t1.fbs and ta.fportcode = t1.fportcode")
				.append(" left join (select fsecuritycode,FTradeCury from ").append(pub.yssGetTableName("tb_para_security"))
				.append(" where fcheckstate = 1) se on t1.fsecuritycode = se.fsecuritycode")//关联证券对应的交易币种以获取相应的汇率
				.append(" left join (select trin1.fnum as fnum1,trin1.fexrightdate,trin1.fremaindamount,")
				/**add---huhuichao 2013-8-9 STORY  4276 博时：跨境ETF补充增加一类公司行动 */
				.append("finterest,fwarrantcost,fotherright from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				/**end---huhuichao 2013-8-9 STORY  4276*/
				.append(" trin1 where trin1.fmakeupdate = ").append(dbl.sqlDate(YssFun.parseDate("99981231","yyyyMMdd")))	
				.append(" and exists(select fmaxexrightdate,fnum3 from (select fnum as fnum3 ,")
				.append("max(fexrightdate) as fmaxexrightdate from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" where fexrightdate <= ").append(dbl.sqlDate(dDate))
				.append(" group by fnum) trin2 where trin2.fnum3 = trin1.fnum and trin2.fmaxexrightdate = trin1.fexrightdate")
				.append(")) tr1 on t1.fnum = tr1.fnum1 left join (select distinct fnum as fnum2 from ")
				.append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" where fmakeupdate <> ").append(dbl.sqlDate(YssFun.parseDate("99981231","yyyyMMdd")))
				.append("and fmakeupdate <= ").append(dbl.sqlDate(dDate))
				.append(") tr2 on tr2.fnum2 = t1.fnum")
				.append(" left join (select fvaldate,fportcode,fsecuritycode,fprice from ")
				.append(pub.yssGetTableName("tb_data_pretvalmktprice"))
				.append(" where fportcode = ").append(dbl.sqlString(portCode))
				.append(" and fvaldate = ").append(dbl.sqlDate(dDate))
				.append(" and FCheckState = 1) p on p.fsecuritycode = t1.fsecuritycode")//获取当日或最近收盘价
				//关联出当日或最近汇率
				.append(" left join (select FBaseRate, FPortRate,FCuryCode as FSecCuryCode,FPortCode from ")
				.append(pub.yssGetTableName("Tb_Data_Pretvalrate"))
				.append(" where FCheckState = 1 and FValDate = ").append(dbl.sqlDate(dDate))
				.append(" and fportcode = ").append(dbl.sqlString(portCode))
				.append(" ) ra on ra.FSecCuryCode = se.ftradecury")
				//关联出申赎日期汇率
				.append(" left join (select FBaseRate, FPortRate,FCuryCode as FSecCuryCode,FPortCode,FValDate from ")
				.append(pub.yssGetTableName("Tb_Data_Pretvalrate"))
				.append(" where FCheckState = 1")
				.append(" and fportcode = ").append(dbl.sqlString(portCode))
				.append(" ) rate on rate.FSecCuryCode = se.ftradecury ")
				.append("and t1.fportcode = rate.fportcode and rate.FValDate = t1.fbuydate")				
				.append(" where t1.fbuydate < ").append(dbl.sqlDate(dDate))
				.append(" and t1.fportcode = ").append(dbl.sqlString(portCode))
				.append(" and tr2.fnum2 is null order by fbuydate, fbs, fsecuritycode");
				
			rs = dbl.openResultSet(sb.toString());
			while(rs.next()){
				sKey = YssFun.formatDate(dDate) + "\t" + rs.getString("fportcode") + "\t" + rs.getDate("fbuydate") + "\t" 
								+ rs.getString("fbs") + "\t"  + "1" + "\t"  + rs.getString("fsecuritycode");
				sSumKey = YssFun.formatDate(dDate) + "\t" + rs.getString("fportcode") + "\t" + rs.getDate("fbuydate") + "\t" 
								+ rs.getString("fbs") + "\t"  + "1" + "\t"  + " ";
				double dRate = YssD.div(rs.getDouble("FBaseRate"), rs.getDouble("FPortRate"));
				
				double dSingValue =  YssD.mul(rs.getDouble("fbraketnum"), 
										YssD.sub(
											YssD.add(
												YssD.mul(rs.getDouble("fremaindamount"), rs.getDouble("fprice"),dRate),
												YssD.mul(dRate, rs.getDouble("finterest")),
												YssD.mul(dRate, rs.getDouble("fwarrantcost")),
												/**add---huhuichao 2013-8-9 STORY  4276 博时：跨境ETF补充增加一类公司行动 */
												YssD.mul(dRate, rs.getDouble("fotherright"))
												/**end---huhuichao 2013-8-9 STORY  4276*/
												)
											,YssD.div(YssD.mul(rs.getDouble("freplaceamount"),
														rs.getDouble("funitcost"),
														rs.getDouble("fBuyBaseRate"))
														,rs.getDouble("fBuyPortRate"))));
				
				hMV.put(sKey, new Double(dSingValue));
				if(hMV.containsKey(sSumKey)){
					dTotalMV = ((Double)hMV.get(sSumKey)).doubleValue() + dSingValue;
					hMV.put(sSumKey, new Double(dTotalMV));
				}else{
					hMV.put(sSumKey, new Double(dSingValue));
				}
			}
			insertAccBookRefundMV(hMV,"1");
		} catch (Exception e) {
			throw new YssException("计算可退替代款估值增值出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	private void insertAccBookRefundMV(HashMap hMV,String sDirection) throws YssException{
		StringBuffer buff = null;//做拼接SQL语句
		Connection conn = null;//声明数据库连接
		PreparedStatement pst = null;//声明预处理
		boolean bTrans = true;//失误控制标识
		try{
			buff = new StringBuffer(500);//实例化
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置事物手动提交

			//插入数据前先删除数据，删除条件：估值日、组合
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_refundmv")).append(" t1 ");
			buff.append(" where t1.fvaldate = ").append(dbl.sqlDate(dDate));
			buff.append(" and t1.FPortCode = ").append(dbl.sqlString(portCode))
			.append(" and fdatadirection = ").append(dbl.sqlString(sDirection));
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_refundmv"));
			buff.append("(FVALDATE,FPORTCODE,FBUYDATE,FBS,FDATADIRECTION,FVALUE,FSECURITYCODE)");
			buff.append(" values(?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			Iterator iter = hMV.entrySet().iterator(); 
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    String str[] = ((String)entry.getKey()).split("\t");
			    pst.setDate(1, YssFun.toSqlDate(str[0]));
			    pst.setString(2, str[1]);
			    pst.setDate(3, YssFun.toSqlDate(str[2]));
			    pst.setString(4, str[3]);
			    pst.setString(5, str[4]);
			    
			    
			    double dValue = 0;
			    if(sDirection.equals("1")){
			    	//获取该笔申赎前一天的可退替代款估值增值余额，算出当天的发生额
			    	dValue = getValuePerDay((String)entry.getKey());
			    }
			    double dTotal = ((Double)entry.getValue()).doubleValue();
			    if(" ".equals(str[5])){
			    	dTotal = YssD.round(dTotal, 2);//如果是汇总值则保留两位小数
			    }
			    if(str[3].equals("S") && sDirection.equals("1")){
			    	dTotal = YssD.mul(dTotal,-1);
			    }
			    pst.setDouble(6, YssD.sub(dTotal,dValue));
			    pst.setString(7, str[5]);
			    pst.addBatch();
			} 
			pst.executeBatch();
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置事物为自动提交
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("计算可退替代款估值增值出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
		}
	}
	
	private void insertRefundMV(HashMap hMV,String sDirection) throws YssException{
		StringBuffer buff = null;//做拼接SQL语句
		Connection conn = null;//声明数据库连接
		PreparedStatement pst = null;//声明预处理
		boolean bTrans = true;//失误控制标识
		try{
			buff = new StringBuffer(500);//实例化
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置事物手动提交

			//插入数据前先删除数据，删除条件：估值日、组合
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_refundmv")).append(" t1 ");
			buff.append(" where t1.fvaldate = ").append(dbl.sqlDate(dDate));
			buff.append(" and t1.FPortCode = ").append(dbl.sqlString(portCode))
			.append(" and fdatadirection = ").append(dbl.sqlString(sDirection));
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_refundmv"));
			buff.append("(FVALDATE,FPORTCODE,FBUYDATE,FBS,FDATADIRECTION,FVALUE,FSECURITYCODE)");
			buff.append(" values(?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			Iterator iter = hMV.entrySet().iterator(); 
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    String str[] = ((String)entry.getKey()).split("\t");
			    pst.setDate(1, YssFun.toSqlDate(str[0]));
			    pst.setString(2, str[1]);
			    pst.setDate(3, YssFun.toSqlDate(str[2]));
			    pst.setString(4, str[3]);
			    pst.setString(5, str[4]);
			   
			    double dTotal = ((Double)entry.getValue()).doubleValue();
			   
			    pst.setDouble(6, dTotal);
			    pst.setString(7, str[5]);
			    pst.addBatch();
			} 
			pst.executeBatch();
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置事物为自动提交
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("计算可退替代款估值增值余额出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
		}
	}
	
	/**
	 * 获取前一日可退替代款估值增值余额
	 * 区分每日申赎
	 */
	private double getValuePerDay(String sKey ) throws YssException{
		double dValue = 0;
		ResultSet rs = null;
		StringBuffer buff = null;//做拼接SQL语句
		try{
			String[] sKeys = sKey.split("\t");
			buff = new StringBuffer(200);
			buff.append("select sum(fvalue) as fvalue from ").append(pub.yssGetTableName("tb_etf_refundmv"))
				.append(" where fvaldate < ").append(dbl.sqlDate(dDate))
				.append(" and fbuydate = ").append(dbl.sqlDate(sKeys[2]))
				.append(" and fportcode = ").append(dbl.sqlString(portCode))
				.append(" and fbs = ").append(dbl.sqlString(sKeys[3]))
				.append(" and FSECURITYCODE = ").append(dbl.sqlString(sKeys[5]))
				.append(" and fdatadirection = 1 ")
				.append(" order by fvaldate desc");
			
			rs = dbl.openResultSet(buff.toString());
			if(rs.next()){
				dValue = rs.getDouble("fvalue");
			}
		}catch (Exception e) {
			throw new YssException("计算可退替代款估值增值发生额出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dValue;
	}
	
	/**
	 * 汇总估值日当天可退替代款估值增值发生额，产生现金应收应付
	 * @throws YssException
	 */
	private HashMap insertCashRecPay() throws YssException{
		ResultSet rs = null;
		StringBuffer buff = null;//做拼接SQL语句
        CashPecPayBean CashRP = null;
        HashMap hmResult = new HashMap();
		try{
			buff = new StringBuffer(500);
			buff.append("select r.*,e.fcashacccode,c.fcurycode,p.FBaseRate,p.FPortRate from ")
				.append("(select sum(fvalue) as fvalue,fportcode,fbs,fdatadirection From ")
				.append(pub.yssGetTableName("tb_etf_refundmv"))
				.append(" where fvaldate = ").append(dbl.sqlDate(dDate))
				.append(" and fportcode = ").append(dbl.sqlString(portCode))
				.append(" and fsecuritycode = ' '")//取汇总值
				.append(" group by fportcode,fbs,fdatadirection ) r ")
				.append(" left join (select fportcode,FCashAccCode from ")
				.append(pub.yssGetTableName("tb_etf_param"))
				.append(" where fcheckstate = 1) e on e.fportcode = r.fportcode")
				.append(" left join (select fcashacccode,fcurycode from ")
				.append(pub.yssGetTableName("tb_para_cashaccount"))
				.append(" where fcheckstate = 1) c on c.fcashacccode = e.fcashacccode")
				.append(" left join (select FBaseRate, FPortRate,FCuryCode as FSecCuryCode,FPortCode from ")
				.append(pub.yssGetTableName("Tb_Data_Pretvalrate"))
				.append(" where FCheckState = 1 and FValDate = ").append(dbl.sqlDate(dDate))
				.append(" and fportcode = ").append(dbl.sqlString(portCode))
				.append(" ) p on p.FSecCuryCode = c.fcurycode");//获取当日或最近汇率
			
			rs = dbl.openResultSet(buff.toString());
			while(rs.next()){
                String sKey = portCode + "\f" + 
                				rs.getString("fbs") + "\f" +
                				rs.getString("fdatadirection");
				CashRP = getCashPecPayBean(	rs.getDouble("fvalue"),											
											rs.getString("fcashacccode"),
											rs.getString("fcurycode"),
											rs.getString("fbs"),
											rs.getString("fdatadirection"));
				
				hmResult.put(sKey, CashRP);
			}
			return hmResult;
		}catch (Exception e) {
			throw new YssException("生成可退替代款估值增值出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 补票当天冲减可退替代款估值增值
	 */
	private void operReduceMV() throws YssException {
		StringBuffer sb = null;
		ResultSet rs = null;
		HashMap hMV = new HashMap();
		String sKey = "";
		try{
			sb = new StringBuffer(500);
			sb.append("select distinct s.fbuydate,r.* from ").append(pub.yssGetTableName("tb_etf_standingbook"))
				.append(" s left join (select sum(fvalue) as fvalue,fbuydate,fportcode,fbs from ")
				.append(pub.yssGetTableName("TB_ETF_REFUNDMV"))
				.append(" where fdatadirection = 1 and fvaldate < ").append(dbl.sqlDate(dDate))
				.append(" and fportcode = ").append(dbl.sqlString(portCode))
				.append(" and fsecuritycode = ' '")//取汇总值
				.append(" group by fbuydate , fportcode ,fbs) r on r.fbuydate = s.fbuydate")
				.append(" where s.fmakeupdate1 = ").append(dbl.sqlDate(dDate))
				.append(" and s.fportcode = ").append(dbl.sqlString(portCode));
				
			rs = dbl.openResultSet(sb.toString());
			while(rs.next()){
				if(null == rs.getString("FBS") || rs.getString("FBS").trim().length() == 0 ){
					//panjunfang modify 20130427
					//如果没有关联出可退替代款估增的库存余额，则不处理，避免报错。
					//因为若补票在确认日就补卖券完成的话，是不会产生估值增值的，因此关联不出估值增值余额导致null错误
					continue;
				}
				sKey = YssFun.formatDate(dDate) + "\t" + rs.getString("fportcode") + "\t" + rs.getDate("fbuydate") + "\t" 
								+ rs.getString("fbs") + "\t"  + "-1" + "\t"  + " ";

				hMV.put(sKey, new Double(rs.getDouble("fvalue")));
			}
			insertAccBookRefundMV(hMV,"-1");
		} catch (Exception e) {
			throw new YssException("冲减可退替代款估值增值出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 设置现金应收应付属性
	 * @param money
	 * @param dBaseRate
	 * @param dPortRate
	 * @param strCashAccCode
	 * @param strCuryCode
	 * @param strBS
	 * @param iInOutType
	 * @return
	 */
	private CashPecPayBean getCashPecPayBean(double money,String strCashAccCode,
					String strCuryCode,String strBS,String iInOutType) throws YssException {
		CashPecPayBean CashRP = new CashPecPayBean();
		CashRP.setPortCode(portCode);//组合
        CashRP.setTradeDate(dDate);//日期
        CashRP.setCashAccCode(strCashAccCode);//现金账户代码
        CashRP.setCuryCode(strCuryCode);//货币代码
        CashRP.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV);//调拨类型
        CashRP.setSubTsfTypeCode((strBS.equals("B") ? YssOperCons.YSS_ZJDBZLX_ETFBACKCASH_MV_B : 
        								YssOperCons.YSS_ZJDBZLX_ETFBACKCASH_MV_S));//调拨子类型
        double dBaseCuryRate = this.getSettingOper().getCuryRate(dDate, 
        							strCuryCode, portCode, YssOperCons.YSS_RATE_BASE);//获取当日的基础汇率
        double dBaseValue = this.getSettingOper().calBaseMoney(money, dBaseCuryRate);//计算基础货币金额
        EachRateOper rateOper = new EachRateOper();     //新建获取利率的通用类
        rateOper.setYssPub(pub);
        rateOper.getInnerPortRate(dDate, strCuryCode, portCode);
        double dPortCuryRate = rateOper.getDPortRate();     //获取当日的组合汇率
        double dPortValue = this.getSettingOper().calPortMoney(money, dBaseCuryRate, 
        								dPortCuryRate, strCuryCode, dDate, portCode); //计算组合货币金额
        CashRP.setMoney(money);//原币金额
        CashRP.setBaseCuryRate(dPortCuryRate);//基础汇率
        CashRP.setBaseCuryMoney(dBaseValue);//基础货币金额
        CashRP.setPortCuryRate(dPortCuryRate);//组合汇率
        CashRP.setPortCuryMoney(dPortValue);//本位币金额
        CashRP.checkStateId = 1;//审核状态
        CashRP.setInOutType(Integer.parseInt(iInOutType));//方向
        return CashRP;
	}
}
