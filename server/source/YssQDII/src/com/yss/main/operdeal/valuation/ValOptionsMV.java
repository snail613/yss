package com.yss.main.operdeal.valuation;

import java.sql.*;
import java.util.*;

import com.yss.commeach.*;
import com.yss.main.cashmanage.*;
import com.yss.main.operdata.*;
import com.yss.main.operdata.futures.OptionsFIFOStorageAddValueAdmin;
import com.yss.main.operdata.futures.pojo.OptionsFIFOStorageAddValueBean;
import com.yss.main.parasetting.*;
import com.yss.main.storagemanage.*;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;

/**
 * <P> xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
 * 
 * <p>Title:by xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持 </p>
 *
 * <p>Description: 此类产生期权的估值增值的应收应付,以及资金调拨数据</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ValOptionsMV extends BaseValDeal {
    private String securityCodes = ""; //证券代码
    private String sRelaNums = ""; //编号
    private String sAccCashCode = ""; //保存变动保证金账户
    private boolean analy1 = false; //分析代码1
    private boolean analy2 = false; //分析代码2
    private ArrayList alFIFOStorageApp = new ArrayList();//期权先入先出估值增值余额表
    public ValOptionsMV() {
    }

    /**
     * 资产估值方法
     * @param mtvBeans ArrayList
     * @return HashMap
     * @throws YssException
     */
    public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {
        HashMap yesDateaddValue = null; //保存昨日估值增值
        double theDateStorageAmount = 0; //保存今天库存数量
        double theDateAddValue = 0; //保存今日原币估值增值
        double theDateBaseAddValue = 0; //保存今日基础货币估值增值
        double theDatePortAddValue = 0; //保存今日组合货币估值增值

        double theDateSaleAddValue = 0; //保存当日交易原币估值增值
        double theDateSaleBaseAddValue = 0; //保存当日交易基础货币的估值增值
        double theDateSalePortAddValue = 0; //保存当日交易组合货币的估值增值

        //double []theDatePrice=null;//获取今天市价和估值的 基础汇率，组合汇率
        SecRecPayBalBean yesSecRecPay = null; //初始化证券应收应付

        String sql = "";
        ResultSet rs = null;
        MTVMethodBean vMethod = null; //估值方法
        HashMap hmResult = new HashMap(); //返回值
        HashMap hmFIFOResult = new HashMap();//先入先出核算时保存数据的hash表
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        double dPortRate = 0; //组合汇率
        double dBaseRate = 0; //基础汇率
        double dMarketPrice = 0; //期权市价
        double dAimPrice = 0; //标的物价格
        double dIndexPrice = 0; //当日指数价格
        double dTSecuturyMatketPrice = 0;//标的证券的行情价格
        SecPecPayBean secPay = null;
        SecPecPayBean secPecPay = null;
        String sCatCode = ""; //保存品种子类型
        String key = "";
        String sKey = "";
        StringBuffer buf = new StringBuffer();
        BaseStgStatDeal cashstgstat = null; //库存基类
        String sBailMoneyTransferType = "";//通用参数获取的保证金结转方式
        String sAccountType = "";//通用参数获取期权核算方式
        double theDayAddValueTotal = 0;//先入先出法保存每笔证券的当天估值增值余额
        OptionsFIFOStorageAddValueAdmin fifoAddValueAdmin = null;
        double dStoCost =0; //库存成本 add by jiangshichao 
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Stock"); //判断证券库存表是否有分析代码
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Stock");
            
            //通过组合代码获取期权保证金结算方式
            sBailMoneyTransferType = getAccountTypeBy(this.portCode,"bailcarrytype");
            
            //通过组合代码获取期权核算方式
            sAccountType = getAccountTypeBy(this.portCode,"OptionAccountType");
            
            //获取昨日估值增值
            yesDateaddValue = getYesSecRecPay(this.dDate, this.portCode);
            //获取今天的期权市价
            //theDatePrice = getMarketPrice();
            //获取今天库存数量
            //sql = getTheDateTradeAmount(this.dDate, this.portCode);

            for (int i = 0; i < mtvBeans.size(); i++) { //循环估值方法
                vMethod = (MTVMethodBean) mtvBeans.get(i);  
                sql = "select stock.*,options.ftradetypecode,options.fTsecuritycode,options.fmultiple,indexdata.fclosedvalue," +
                " options.FExercisePrice,rate.fexrate1,market.FClosingPrice as FClosingPrice1,market.FMktValueDate, market2.FClosingPrice as FClosingPrice2," +//modify by nimengjing 2010.12.8 BUG #540 期权资产估值，报缺少字段的错误 
                " market2.FMktValueDate as FMktValueDate2,optioncost.FOriginalAddValue," +
                " optioncost.FBaseAddValue,optioncost.FPortAddValue,cal.fcashacccode,Fstate from(select " +
                " stock1.*,s.fexchangecode as FExCode,s.fsubcatcode,(case when stock1.fstorageamount>=0 then '02' else '04' end)as FState from " +
                pub.yssGetTableName("tb_stock_security")//证券库存表
                + " stock1 join(select * from "+
                pub.yssGetTableName("tb_para_security")+//左关联证券信息表，取出交易所代码
                " where FCheckState = 1) s on stock1.fsecuritycode =s.fsecuritycode"+")" +
                " stock join (select * from " +
                pub.yssGetTableName("tb_para_optioncontract") +//期权信息表
                " where FCheckState = 1) options on options.FOptionCode =stock.fsecuritycode" +
                " join (select FLinkCode from " +
                pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                " where FCheckState = 1 and FMtvCode=" +
                dbl.sqlString(vMethod.getMTVCode()) +
                ") b on stock.Fsecuritycode = b.FLinkCode" +
                //-------------------------xuqiji 20100421-----------------------------//
                //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                //" left join (select m3.* from (select max(FMktValueDate) as FMktValueDate,FSecurityCode from "+
                //pub.yssGetTableName("Tb_Data_MarketValue") +//行情表
                //" where FCheckState = 1 "+" and FMktValueDate <=" + dbl.sqlDate(this.dDate) + " and FMktSrcCode = "+ dbl.sqlString(vMethod.getMktSrcCode())
                //+ " group by FSecurityCode) m4" +
                //" left join (select * from "+pub.yssGetTableName("Tb_Data_MarketValue") +//行情表
                //" where FCheckState = 1 and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
                //") m3 on m4.FSecurityCode = m3.FSecurityCode and m4.FMktValueDate = m3.FMktValueDate"+
                " left join (select * from "+tmpMarketValueTable + " where FMktSrcCode="+dbl.sqlString(vMethod.getMktSrcCode()) +
                //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                " ) market2 on options.fTsecuritycode = market2.FSecurityCode"+
                //----------------------------------end--------------------------------//
                " left join (select t2.* ,oo.FOptionCode from (select max(FDate) as FDate, FIndexCode from " +
                pub.yssGetTableName("Tb_Data_Index") +//指数表
                " where FCheckState = 1 and FDate <= " +
                dbl.sqlDate(this.dDate) + " group by FIndexCode) t1" +
                " left join (select * from " +
                pub.yssGetTableName("Tb_Data_Index") +//质数表
                " where FCheckState = 1) t2 " +
                " on t1.FDate = t2.FDate and t1.FIndexCode =t2.FIndexCode" +
                " left join (select * from " + pub.yssGetTableName("tb_para_optioncontract") +//期权信息表
                " where FCheckState = 1) oo on t2.findexcode =oo.FTSecurityCode" +
                " ) indexdata on stock.fsecuritycode =indexdata.FOptionCode" +
                " left join (select e2.* from (select max(FExRateDate) as FExRateDate, FCuryCode from " +
                pub.yssGetTableName("tb_Data_ExchangeRate") +//汇率信息表
                " where FCheckState = 1 and FExRateDate <= " +
                dbl.sqlDate(this.dDate) + " group by FCuryCode) e1" +
                " left join (select * from " +
                pub.yssGetTableName("tb_Data_ExchangeRate") +//汇率信息表
                " where FCheckState = 1) e2 on e1.FExRateDate =e2.FExRateDate and e1.FCuryCode =" +
                " e2.FCuryCode) rate on stock.FCuryCode =rate.FCuryCode" +
                //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                //" left join (select m2.* from (select max(FMktValueDate) as FMktValueDate,FSecurityCode" +
                //" from " + pub.yssGetTableName("Tb_Data_MarketValue") + " where FCheckState = 1 and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode())+//行情表
                //" and FMktValueDate <=" + dbl.sqlDate(this.dDate) + " group by FSecurityCode) m1" +
                //" left join (select mar.*,"+ vMethod.getMktPriceCode() + " as FClosingPrice1 from " +
                //pub.yssGetTableName("Tb_Data_MarketValue") +//行情表
                //" mar where FCheckState = 1 and FMktSrcCode ="+ dbl.sqlString(vMethod.getMktSrcCode()) + ") m2 on m1.FSecurityCode =m2.FSecurityCode" +
                //" and m1.FMktValueDate =m2.FMktValueDate) market on stock.fsecuritycode =market.FSecurityCode" +
                " left join (select mar.*,"+ vMethod.getMktPriceCode() + " as FClosingPrice1 from "+tmpMarketValueTable + " mar where FMktSrcCode="+dbl.sqlString(vMethod.getMktSrcCode()) +
                " ) market on stock.fsecuritycode =market.FSecurityCode" +
                //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                
                " left join (select sum(opc.FOriginalAddValue) as FOriginalAddValue,sum(opc.FBaseAddValue) as FBaseAddValue," +
                " sum(opc.FPortAddValue) as FPortAddValue,opc.FSecurityCode,opc.FPortCode," +
                " opc.FCloseNum from " +
                //edit by songjie 资产估值报错：未明确到列，结果集中有两个FPortCode字段，改为去掉trade.FPortCode，
                "(select opcost.*,trade.FSecurityCode," +
                " trade.FCloseNum,sFNum from " + pub.yssGetTableName("Tb_Data_OptionsCost") +//期权成本和估值增值表，取出当天的估值增值
                " opcost  join (select a.* ,a.FNUM||a.FCloseNum as sFNum from " + pub.yssGetTableName("TB_Data_Optionstraderela") +//期权交易关联表
                " a where FCheckState = 1 and FBargainDate =" + dbl.sqlDate(this.dDate) +
                " ) trade on opcost.fnum =trade.sFNum where opcost.FCheckState = 1 )opc group by opc.FSecurityCode," +
                " opc.FPortCode,opc.FCloseNum)optioncost" +
                " on stock.FSecurityCode =optioncost.FSecurityCode and stock.FPortCode =optioncost.FPortCode" +
                (analy2 ? " and stock.FAnalysisCode2 =optioncost.FBrokerCode " : "") +
                (analy1 ? " and stock.FAnalysisCode1 =optioncost.FInvMgrCode " : "") +
                " and optioncost.FCloseNum=FState" +
                //xuqiji 20090810 QDV4招商证券2009年07月06日01_A  MS00562 期权和期货结算估值的保证金账户需要独立的界面让用户指定
                " join (select * from " +
                pub.yssGetTableName("Tb_Data_OptionsValCal")+//期权和期货保证金账户设置表，取出现金账户
                " where FCheckState = 1 and FMarkType=0 ) cal" +
                " on cal.fexchagecode = stock.FExCode  and cal.FPortCode =stock.FPortCode " +
                //------------------------------end 20090810-------------------------------------------------//
                " where stock.fcheckstate = 1 and stock.FStorageDate =" +
                dbl.sqlDate(this.dDate) + " and stock.FPortCode =" + dbl.sqlString(this.portCode)+ " and stock.fStorageAmount <> 0 ";
                //-------------------------先入先出资产估值的SQL语句------------------------------//
                if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){
                	sql = " select a.*,a.fcurycost as FStorageCost,b.fexchangecode,b.fsubcatcode,b.ftradecury as FCuryCode,c.ftradetypecode,c.fTsecuritycode,c.fmultiple," + 
                		  " c.FExercisePrice,market2.FClosingPrice as FClosingPrice2,market2.FMktValueDate as FMktValueDate2," + 
                		  " indexdata.fclosedvalue,rate.fexrate1,market.FClosingPrice as FClosingPrice1,market.FMktValueDate," +//modify by nimengjing 2010.12.8 BUG #540 期权资产估值，报缺少字段的错误 
                		  " e.foriginaladdvalue,e.fbaseaddvalue," +						  
						   //---MS01572 QDV4赢时胜（深圳）2010年08月06日01_A  add by jiangshichao 2010.08.14 -----------------------------
                		  " e.fportaddvalue,f.FCuryValue,f.FBaseCuryValue,f.FPortCuryValue,cal.fcashacccode,g.FAnalysisCode1,g.FAnalysisCode2,g.FAttrClsCode,h.ftradetypecode as opertypecode from " +
                		  pub.yssGetTableName("Tb_Data_OptiFIFOStock") + 
                		  " a join (select * from " + pub.yssGetTableName("tb_para_security") + 
                		  " where FCheckState = 1 and FCatCode = 'FP') b on a.FSecurityCode = b.fsecuritycode " +
                		  " join (select * from " + pub.yssGetTableName("tb_para_optioncontract") +
                		  " where FCheckState = 1) c on a.FSecurityCode = c.fOptioncode" +
                		  " join (select FLinkCode from " + pub.yssGetTableName("Tb_Para_MTVMethodLink") + 
                		  " where FCheckState = 1 and FMtvCode = " + dbl.sqlString(vMethod.getMTVCode()) +" ) d on a.Fsecuritycode = d.FLinkCode" +
                		  //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                		  //" left join (select m3.* from (select max(FMktValueDate) as FMktValueDate,FSecurityCode from " +
                		  //pub.yssGetTableName("Tb_Data_MarketValue") +
                		  //" where FCheckState = 1 and FMktValueDate <= " + dbl.sqlDate(this.dDate) + " and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) + 
                		  //" group by FSecurityCode) m4 left join (select * from " + pub.yssGetTableName("Tb_Data_MarketValue") + 
                		  //" where FCheckState = 1 and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
                		  //" ) m3 on m4.FSecurityCode = m3.FSecurityCode and m4.FMktValueDate = m3.FMktValueDate) market2 on c.fTsecuritycode = market2.FSecurityCode " +
                		  " left join (select * from "+tmpMarketValueTable + " where FMktSrcCode="+dbl.sqlString(vMethod.getMktSrcCode()) +
                		  " ) market2 on c.fTsecuritycode = market2.FSecurityCode "+
                		  //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                		  " left join (select t2.*, oo.FOptionCode from (select max(FDate) as FDate, FIndexCode from " +
                		  pub.yssGetTableName("Tb_Data_Index") + 
                		  " where FCheckState = 1 and FDate <= " + dbl.sqlDate(this.dDate) + " group by FIndexCode) t1 " + 
                		  " left join (select * from " + pub.yssGetTableName("Tb_Data_Index") + " where FCheckState = 1) t2 on t1.FDate = t2.FDate " +
                		  " and t1.FIndexCode = t2.FIndexCode left join (select * from " + pub.yssGetTableName("tb_para_optioncontract") +
                		  " where FCheckState = 1) oo on t2.findexcode = oo.FTSecurityCode) indexdata on a.fsecuritycode = indexdata.FOptionCode " +
                		  " left join (select e2.* from (select max(FExRateDate) as FExRateDate, FCuryCode from " +
                		  pub.yssGetTableName("tb_Data_ExchangeRate") + 
                		  " where FCheckState = 1 and FExRateDate <= " + dbl.sqlDate(this.dDate) + " group by FCuryCode) e1 " + 
                		  " left join (select * from " + pub.yssGetTableName("tb_Data_ExchangeRate") + 
                		  " where FCheckState = 1) e2 on e1.FExRateDate = e2.FExRateDate and e1.FCuryCode = e2.FCuryCode) rate on b.ftradecury = rate.FCuryCode " +
                		  //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                		  //" left join (select m2.* from (select max(FMktValueDate) as FMktValueDate, FSecurityCode from " +
                		  //pub.yssGetTableName("Tb_Data_MarketValue") +
                		  //" where FCheckState = 1 and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) + " and FMktValueDate <= " + dbl.sqlDate(this.dDate) +
                		  //" group by FSecurityCode) m1 left join (select mar.*, FClosingPrice as FClosingPrice1 from " +
                		  //pub.yssGetTableName("Tb_Data_MarketValue") + " mar " +
                		  //" where FCheckState = 1 and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) + 
                		  //" ) m2 on m1.FSecurityCode = m2.FSecurityCode and m1.FMktValueDate = m2.FMktValueDate) market on a.fsecuritycode = market.FSecurityCode " +
                		  " left join (select * from "+tmpMarketValueTable + " where FMktSrcCode="+dbl.sqlString(vMethod.getMktSrcCode()) +
                		  " ) market on a.fsecuritycode = market.FSecurityCode "+ //modify by jiangshichao 2011.07.11 这里通过关联期权代码，关联出的是期权价格
                		  //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                		  " left join (select * from " + pub.yssGetTableName("tb_data_optionscost") + " ) e on a.FNum = e.FSetNum and a.fstoragedate = e.fdate" +
                		  " left join (select * from " + pub.yssGetTableName("Tb_Data_OptiFIFOAppStk") + 
                		  " where FStorageDate = " + dbl.sqlDate(YssFun.addDay(this.dDate,-1)) + " and FPortCode = " + dbl.sqlString(this.portCode) +
                		  " ) f on a.FNum = f.FNum " +
                		  " join (select * from " + pub.yssGetTableName("Tb_Data_OptionsValCal") +
                		  " where FCheckState = 1 and FMarkType = 0) cal on cal.fexchagecode = b.fexchangecode and cal.FPortCode = a.FPortCode " +
                		  " join (select * from " + pub.yssGetTableName("tb_stock_security")+
                		  " where FCheckState = 1 and FStorageDate = " + dbl.sqlDate(this.dDate) +
                		  " and FPortCode = " + dbl.sqlString(this.portCode) +
                		  " ) g on a.fsecuritycode = g.fsecuritycode and a.fportcode = g.fportcode " +
                		  //--- MS01572 QDV4赢时胜（深圳）2010年08月06日01_A  add by jiangshichao 2010.08.14 -----
						  // 通过判断库存数量的正负来区分是买入还是卖出，而先入先出的核算方式的数量都是为正的所以这里通过匹配交易类型处理库存数量方向 ----
						  " left join ( select fnum,ftradetypecode from " + pub.yssGetTableName("tb_data_optionstrade") + " where fcheckstate=1 ) h on a.fnum = h.fnum  "+
                		  " where a.fcheckstate = 1 and a.FStorageDate = " + dbl.sqlDate(this.dDate) +
                		  " and a.FPortCode = " + dbl.sqlString(this.portCode) + " and a.fStorageAmount <> 0" +
                		  " order by a.fnum,a.fsecuritycode,a.fportcode";
                }
                //--------------------------end-----------------------------------------------//
                rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    sAccCashCode = rs.getString("fcashacccode"); //现金账户
                    
                    if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){
                    	if(rs.getString("opertypecode").equalsIgnoreCase("02") ){ 
                    		theDateStorageAmount = YssD.mul(rs.getDouble("FStorageAmount"), -1);
                    		dStoCost = YssD.mul(rs.getDouble("FStorageCost"), -1);
                    	}else{
                    		theDateStorageAmount = rs.getDouble("FStorageAmount"); //今天的库存
                    		dStoCost = rs.getDouble("FStorageCost");
                    	}
                    }else{
                        theDateStorageAmount = rs.getDouble("FStorageAmount"); //今天的库存
                        dStoCost = rs.getDouble("FStorageCost");
                    }
                    
                    dBaseRate = rs.getDouble("fexrate1"); //基础汇率
                    dMarketPrice = rs.getDouble("FClosingPrice1"); //期权市价
  
                    dAimPrice = rs.getDouble("FExercisePrice"); //期权标的物价格
                    dIndexPrice = rs.getDouble("fclosedvalue"); //当日指数价格
                    sCatCode = rs.getString("FSubcatcode"); //品种子类型
                    dTSecuturyMatketPrice = rs.getDouble("FClosingPrice2");//标的证券行情

                    
                    theDateSaleAddValue = rs.getDouble("FOriginalAddValue"); //当日交易原币估值增值
                    theDateSaleBaseAddValue = rs.getDouble("FBaseAddValue"); //当日交易基础货币估值增值
                    theDateSalePortAddValue = rs.getDouble("FPortAddValue"); //当日交易组合货币估值增值

                    key = rs.getString("FPortCode") + "\f" + rs.getString("FSecurityCode") + "\f" +
                        (analy1 ? rs.getString("FAnalysisCode1") : "") + "\f" + (analy2 ? rs.getString("FAnalysisCode2") : "");
                    if(vMethod.getMTVMethod().equalsIgnoreCase("0")){//市值法估值
                    	if(sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                    		key += "\f" + "02";
                    	}else{
                    		key += "\f" + "09";
                    	}
                        secPay = new SecPecPayBean();
                        mktPrice=new  ValMktPriceBean();//估值行情
                        if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                        	yesSecRecPay = (SecRecPayBalBean) yesDateaddValue.get(key);
                        }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                        	if(yesSecRecPay == null){
                        		yesSecRecPay = new SecRecPayBalBean();
                        		yesSecRecPay.setDVBal(rs.getDouble("FCuryValue"));
                        		yesSecRecPay.setDVBaseBal(rs.getDouble("FBaseCuryValue"));
                        		yesSecRecPay.setDVPortBal(rs.getDouble("FPortCuryValue"));
                        	}else{
                        		yesSecRecPay.setDVBal(rs.getDouble("FCuryValue"));
                        		yesSecRecPay.setDVBaseBal(rs.getDouble("FBaseCuryValue"));
                        		yesSecRecPay.setDVPortBal(rs.getDouble("FPortCuryValue"));
                        	} 
                        }
                        
                        //用通用方法，获取组合汇率
                        rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), this.portCode,
                            vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                            vMethod.getPortRateSrcCode(), vMethod.getPortRateCode());
                        dPortRate = rateOper.getDPortRate(); //获取组合汇率

                        //基础和组合汇率
                        secPay.setBaseCuryRate(dBaseRate);
                        secPay.setPortCuryRate(dPortRate);
                        //业务日期
                        secPay.setTransDate(this.dDate);

                    	//原币成本估值增值(余额)=期权收盘价*持仓合约数量*放大倍数(或者每手股数)-持仓成本(期权金)
                        theDayAddValueTotal = YssD.round(
                								YssD.sub(
                										YssD.mul(dMarketPrice,
                												theDateStorageAmount,
                												rs.getDouble("FMultiple")),
                												dStoCost),2);
                       
                        //原币成本估值增值(发生额)=(期权收盘价*持仓合约数量*放大倍数(或者每手股数)-持仓成本(期权金))-昨日成本估值增值余额+当日卖入交易的原币估值增值
                        theDateAddValue = 
                        	YssD.round(
                        		YssD.add(
                        				YssD.sub(
                								YssD.sub(
                										YssD.mul(dMarketPrice,
                												theDateStorageAmount,
                												rs.getDouble("FMultiple")),
                												dStoCost),
												yesSecRecPay == null ? 0 : yesSecRecPay.getDVBal()), 
										theDateSaleAddValue),2);                      	
                        
                        //原币成本增值
                        secPay.setMoney(theDateAddValue);
                        //原币管理成本增值=(标的指数价格-当日指数价格)*库存数量*放大倍数-昨日管理成本估值增值
                        secPay.setMMoney(theDateAddValue);
                        //原币估值增值=(标的指数价格-当日指数价格)*库存数量*放大倍数--昨日估值增值+当日买入的原币估值增值
                        secPay.setVMoney(theDateAddValue);
                        //---------------------------------------------------
                        //-----------------------------基础货币增值------------
                        //基础货币成本估值增值=原币成本估值增值*基础汇率
                        theDateBaseAddValue = YssD.round(this.getSettingOper().calBaseMoney(secPay.getMoney(), secPay.getBaseCuryRate()),2);

                        secPay.setBaseCuryMoney(theDateBaseAddValue);
                        //基础货币管理成本估值增值=原币管理成本估值增值*基础汇率
                        secPay.setMBaseCuryMoney(theDateBaseAddValue);
                        //基础货币估值增值=原币估值增值*基础汇率+当日卖出基础货币估值增值
                        secPay.setVBaseCuryMoney(theDateBaseAddValue);
                        //---------------------------------------------------
                        //------------------------------组合货币增值------------
                        //组合货币成本估值增值=原币成本估值增值*基础汇率/组合汇率
                        theDatePortAddValue = YssD.round(this.getSettingOper().calPortMoney(secPay.getMoney(),
                            secPay.getBaseCuryRate(), secPay.getPortCuryRate(), rs.getString("FCuryCode"),
                            this.dDate, this.portCode),2);

                        secPay.setPortCuryMoney(theDatePortAddValue);
                        //组合货币管理成本估值增值=原币管理成本估值增值*基础汇率/组合汇率
                        secPay.setMPortCuryMoney(theDatePortAddValue);
                        //组合货币估值增值=原币估值增值*基础汇率/组合汇率 + 当日卖出组合货币估值增值
                        secPay.setVPortCuryMoney(theDatePortAddValue);
                        //-----------------------------------------------------
                        if(!sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                            secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV); //调拨类型09
                            secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_MV); //调拨子类型09FP01
                        }else{
                            secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec); //调拨类型06应收款项
                            secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_REC); //调拨子类型06FP01应收期权收益
                        }
                        secPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                        secPay.setStrPortCode(rs.getString("FPortCode"));
                        secPay.setBrokerCode(rs.getString("FAnalysisCode2"));
                        secPay.setInvMgrCode(rs.getString("FAnalysisCode1"));
                        secPay.setStrCuryCode(rs.getString("FCuryCode"));
                        secPay.setAttrClsCode(rs.getString("FAttrClsCode"));

                        secPay.checkStateId = 1;

                        mktPrice.setValDate(this.dDate);//估值日期
                        mktPrice.setSecurityCode(secPay.getStrSecurityCode());//证券代码
                        mktPrice.setPortCode(portCode);//组合代码
                        mktPrice.setPrice(dMarketPrice);//期权行情
                        mktPrice.setOtPrice1(dIndexPrice);//指数行情
                        mktPrice.setMtvCode(vMethod.getMTVCode()); //设置估值方法
                        mktPrice.setValType("OptionsMV"); //设置估值类型为普通类型的证券，与估值界面上的代码相一致。
                        if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                        	sKey = secPay.getStrSecurityCode() + "\f" +
                            (this.invmgrSecField.length() != 0 ?
                             (secPay.getInvMgrCode() + "\f") : "") +
                            (this.brokerSecField.length() != 0 ?
                             (secPay.getBrokerCode() + "\f") : "") +
                            secPay.getStrSubTsfTypeCode() + "\f" +
                            (secPay.getAttrClsCode().length() == 0 ? " " :
                             secPay.getAttrClsCode());
                        	hmResult.put(sKey, secPay);
                        }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                        	sKey = rs.getString("FNum") + "\f" +secPay.getStrSecurityCode() + "\f" +
                            (this.invmgrSecField.length() != 0 ?
                             (secPay.getInvMgrCode() + "\f") : "") +
                            (this.brokerSecField.length() != 0 ?
                             (secPay.getBrokerCode() + "\f") : "") +
                            secPay.getStrSubTsfTypeCode() + "\f" +
                            (secPay.getAttrClsCode().length() == 0 ? " " :
                             secPay.getAttrClsCode());
                        	hmResult.put(sKey, secPay);
                        }
                        //保证金每日结转时，产生收入02冲减应收款项06，并产生资金调拨，否则，只会产生估值增值09
                        if(sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                        	secPecPay = (SecPecPayBean) secPay.clone();
                        	secPecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income); //调拨类型02收入
                            secPecPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_SR); //调拨子类型为02FP01，其余数据和secPay一样
                            if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                            	sKey = secPay.getStrSecurityCode() + "\f" +
                                (this.invmgrSecField.length() != 0 ?
                                 (secPay.getInvMgrCode() + "\f") : "") +
                                (this.brokerSecField.length() != 0 ?
                                 (secPay.getBrokerCode() + "\f") : "") +
                                secPay.getStrSubTsfTypeCode() + "\f" +
                                (secPay.getAttrClsCode().length() == 0 ? " " :
                                 secPay.getAttrClsCode());
                            	hmResult.put(sKey, secPay);
                            }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                            	sKey = rs.getString("FNum") + "\f" +secPay.getStrSecurityCode() + "\f" +
                                (this.invmgrSecField.length() != 0 ?
                                 (secPay.getInvMgrCode() + "\f") : "") +
                                (this.brokerSecField.length() != 0 ?
                                 (secPay.getBrokerCode() + "\f") : "") +
                                secPay.getStrSubTsfTypeCode() + "\f" +
                                (secPay.getAttrClsCode().length() == 0 ? " " :
                                 secPay.getAttrClsCode());
                            	hmResult.put(sKey, secPay);
                            }
                        }
                        
                        hmValPrice.put(mktPrice.getSecurityCode(),mktPrice);
                        if(sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                        	saveCashTransferData(secPecPay); //产生资金调拨，
                        }
                    }else if(vMethod.getMTVMethod().equalsIgnoreCase("4")){//如果估值方法是内在价值法“4”
                    	if(rs.getString("FSUBCATCODE").equalsIgnoreCase("FP01")){//股指期权
                    		dTSecuturyMatketPrice = dIndexPrice; 
                    	}
                    	if (rs.getString("FTradeTypeCode").equalsIgnoreCase("call")) { //期权认购
                            if (theDateStorageAmount > 0) { //买入认购状态
                                key += "\f" + "09";
                                secPay = new SecPecPayBean();
                                mktPrice=new  ValMktPriceBean();//估值行情
                                if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                                	yesSecRecPay = (SecRecPayBalBean) yesDateaddValue.get(key);
                                }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                                	if(yesSecRecPay == null){
                                		yesSecRecPay = new SecRecPayBalBean();
                                		yesSecRecPay.setDVBal(rs.getDouble("FCuryValue"));
                                		yesSecRecPay.setDVBaseBal(rs.getDouble("FBaseCuryValue"));
                                		yesSecRecPay.setDVPortBal(rs.getDouble("FPortCuryValue"));
                                	}else{
                                		yesSecRecPay.setDVBal(rs.getDouble("FCuryValue"));
                                		yesSecRecPay.setDVBaseBal(rs.getDouble("FBaseCuryValue"));
                                		yesSecRecPay.setDVPortBal(rs.getDouble("FPortCuryValue"));
                                	} 
                                }

                                //用通用方法，获取组合汇率
                                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), this.portCode,
                                    vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                    vMethod.getPortRateSrcCode(), vMethod.getPortRateCode());
                                dPortRate = rateOper.getDPortRate(); //获取组合汇率

                                //基础和组合汇率
                                secPay.setBaseCuryRate(dBaseRate);
                                secPay.setPortCuryRate(dPortRate);

                                //业务日期
                                secPay.setTransDate(this.dDate);
                                // 原币成本估值增值当天余额=(((正股收盘价-行权价)>0?(正股收盘价-行权价)* 合约数*每合约代表的正股数量:0))-期权成本
                                theDayAddValueTotal = YssD.round(YssD.sub(
                                				YssD.sub(dTSecuturyMatketPrice,dAimPrice) > 0 ?YssD.mul(YssD.sub(dTSecuturyMatketPrice,dAimPrice),theDateStorageAmount,rs.getDouble("FMultiple")):0,
                                						Math.abs(rs.getDouble("FStorageCost"))),2);
                                //--------------------原币增值----------------------
                                //估值增值=((正股收盘价-行权价)>0?(正股收盘价-行权价)* 合约数*每合约代表的正股数量:0))-期权成本 - 昨日成本估值增值额 + 当日卖出的原币估值增值
                                theDateAddValue = YssD.round(YssD.add(
                                		YssD.sub(
                                				YssD.sub(dTSecuturyMatketPrice,dAimPrice) > 0 ?YssD.mul(YssD.sub(dTSecuturyMatketPrice,dAimPrice),theDateStorageAmount,rs.getDouble("FMultiple")):0,
                                				YssD.add(
                                						Math.abs(rs.getDouble("FStorageCost")),yesSecRecPay == null ? 0 : yesSecRecPay.getDVBal())),
                                						theDateSaleAddValue),2);
                                
                                secPay.setMoney(theDateAddValue);
                                secPay.setMMoney(theDateAddValue);
                                secPay.setVMoney(theDateAddValue);
                                //---------------------------------------------------
                                //-----------------------------基础货币增值------------
                                //基础货币成本估值增值=原币成本估值增值*基础汇率
                                theDateBaseAddValue = YssD.round(this.getSettingOper().calBaseMoney(secPay.getMoney(), secPay.getBaseCuryRate()),2);

                                secPay.setBaseCuryMoney(theDateBaseAddValue);
                                //基础货币管理成本估值增值=原币管理成本估值增值*基础汇率
                                secPay.setMBaseCuryMoney(theDateBaseAddValue);
                                //基础货币估值增值=原币估值增值*基础汇率+当日卖出基础货币估值增值
                                secPay.setVBaseCuryMoney(theDateBaseAddValue);
                                //---------------------------------------------------
                                //------------------------------组合货币增值------------
                                //组合货币成本估值增值=原币成本估值增值*基础汇率/组合汇率
                                theDatePortAddValue = YssD.round(this.getSettingOper().calPortMoney(secPay.getMoney(),
                                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(), rs.getString("FCuryCode"),
                                    this.dDate, this.portCode),2);

                                secPay.setPortCuryMoney(theDatePortAddValue);
                                //组合货币管理成本估值增值=原币管理成本估值增值*基础汇率/组合汇率
                                secPay.setMPortCuryMoney(theDatePortAddValue);
                                //组合货币估值增值=原币估值增值*基础汇率/组合汇率 + 当日卖出组合货币估值增值
                                secPay.setVPortCuryMoney(theDatePortAddValue);
                                //-----------------------------------------------------

                                secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV); //调拨类型09
                                secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_MV); //调拨子类型09FP01
                                secPay.setStrSecurityCode(rs.getString("FSecurityCode")); //证券代码
                                secPay.setStrPortCode(rs.getString("FPortCode")); //组合代码
                                secPay.setBrokerCode(rs.getString("FAnalysisCode2")); //分析代码1
                                secPay.setInvMgrCode(rs.getString("FAnalysisCode1")); //分析代码2
                                secPay.setStrCuryCode(rs.getString("FCuryCode")); //币种代码
                                secPay.setAttrClsCode(rs.getString("FAttrClsCode")); //所属分类

                                secPay.checkStateId = 1;

                                mktPrice.setValDate(this.dDate);//估值日期
                                mktPrice.setSecurityCode(secPay.getStrSecurityCode());//证券代码
                                mktPrice.setPortCode(portCode);//组合代码
                                //实际计算行情 = （正股行情-行权价）* 每手正股数量
                                mktPrice.setPrice(YssD.sub(dTSecuturyMatketPrice,dAimPrice) > 0 ?YssD.mul(YssD.sub(dTSecuturyMatketPrice,dAimPrice),rs.getDouble("FMultiple")):0);
                                mktPrice.setOtPrice1(dMarketPrice);//期权行情
                                mktPrice.setOtPrice2(dTSecuturyMatketPrice);//正股行情
                                mktPrice.setMtvCode(vMethod.getMTVCode()); //设置估值方法
                                mktPrice.setValType("OptionsMV"); //设置估值类型为普通类型的证券，与估值界面上的代码相一致。

                                if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                                	sKey = secPay.getStrSecurityCode() + "\f" +
                                    (this.invmgrSecField.length() != 0 ?
                                     (secPay.getInvMgrCode() + "\f") : "") +
                                    (this.brokerSecField.length() != 0 ?
                                     (secPay.getBrokerCode() + "\f") : "") +
                                    secPay.getStrSubTsfTypeCode() + "\f" +
                                    (secPay.getAttrClsCode().length() == 0 ? " " :
                                     secPay.getAttrClsCode());
                                	hmResult.put(sKey, secPay);
                                }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                                	sKey = rs.getString("FNum") + "\f" +secPay.getStrSecurityCode() + "\f" +
                                    (this.invmgrSecField.length() != 0 ?
                                     (secPay.getInvMgrCode() + "\f") : "") +
                                    (this.brokerSecField.length() != 0 ?
                                     (secPay.getBrokerCode() + "\f") : "") +
                                    secPay.getStrSubTsfTypeCode() + "\f" +
                                    (secPay.getAttrClsCode().length() == 0 ? " " :
                                     secPay.getAttrClsCode());
                                	hmResult.put(sKey, secPay);
                                }
                                hmValPrice.put(mktPrice.getSecurityCode(),mktPrice);

                            } else if (theDateStorageAmount < 0) { //卖出认购状态---1.插入证券应收应付：两种子类型：a.06FP01-期权收益 b.02FP01-期权收入，数据完全一样，2.产生一笔资金调拨
                            	if(sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                            		key += "\f" + "02";
                            	}else{
                            		key += "\f" + "09";
                            	}
                                secPay = new SecPecPayBean();
                                mktPrice=new  ValMktPriceBean();//估值行情
                                if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                                	yesSecRecPay = (SecRecPayBalBean) yesDateaddValue.get(key);
                                }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                                	if(yesSecRecPay == null){
                                		yesSecRecPay = new SecRecPayBalBean();
                                		yesSecRecPay.setDVBal(rs.getDouble("FCuryValue"));
                                		yesSecRecPay.setDVBaseBal(rs.getDouble("FBaseCuryValue"));
                                		yesSecRecPay.setDVPortBal(rs.getDouble("FPortCuryValue"));
                                	}else{
                                		yesSecRecPay.setDVBal(rs.getDouble("FCuryValue"));
                                		yesSecRecPay.setDVBaseBal(rs.getDouble("FBaseCuryValue"));
                                		yesSecRecPay.setDVPortBal(rs.getDouble("FPortCuryValue"));
                                	} 
                                }

                                //用通用方法，获取组合汇率
                                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), this.portCode,
                                    vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                    vMethod.getPortRateSrcCode(), vMethod.getPortRateCode());
                                dPortRate = rateOper.getDPortRate(); //获取组合汇率

                                //基础和组合汇率
                                secPay.setBaseCuryRate(dBaseRate);
                                secPay.setPortCuryRate(dPortRate);

                                //业务日期
                                secPay.setTransDate(this.dDate);
                                
                                // 原币成本估值增值当天余额=期权成本 -((正股收盘价-行权价)>0?(正股收盘价-行权价)* 合约数*每合约代表的正股数量:0))
                                theDayAddValueTotal = YssD.round(
                                		YssD.sub(Math.abs(rs.getDouble("FStorageCost")),
                                				YssD.sub(dTSecuturyMatketPrice,dAimPrice) > 0 ?YssD.mul(YssD.sub(dTSecuturyMatketPrice,dAimPrice),Math.abs(theDateStorageAmount),rs.getDouble("FMultiple")):0)
                                				,2);
                                
                                //--------------------原币增值----------------------                                
                                //估值增值=期权成本 -((正股收盘价-行权价)>0?(正股收盘价-行权价)* 合约数*每合约代表的正股数量:0)) - 昨日成本估值增值额 + 当日卖出的原币估值增值
                                theDateAddValue = YssD.round(YssD.add(
                                		YssD.sub(Math.abs(rs.getDouble("FStorageCost")),
                                				YssD.sub(dTSecuturyMatketPrice,dAimPrice) > 0 ?YssD.mul(YssD.sub(dTSecuturyMatketPrice,dAimPrice),Math.abs(theDateStorageAmount),rs.getDouble("FMultiple")):0,
                                				yesSecRecPay == null ? 0 : yesSecRecPay.getDVBal()),
                                						theDateSaleAddValue),2);

                                //原币成本增值
                                secPay.setMoney(theDateAddValue);
                                //原币管理成本增值=(当日指数价格-标的指数价格或上日指数价格)*数量*放大倍数-昨日管理成本估值增值余额
                                secPay.setMMoney(theDateAddValue);
                                //原币估值增值=(当日指数价格-标的指数价格或上日指数价格)*数量*放大倍数-昨日估值增值余额+当日买入交易的原币估值增值
                                secPay.setVMoney(theDateAddValue);
                                //---------------------------------------------------
                                //-----------------------------基础货币增值------------
                                //基础货币成本估值增值=原币成本估值增值*基础汇率
                                theDateBaseAddValue = YssD.round(this.getSettingOper().calBaseMoney(secPay.getMoney(), secPay.getBaseCuryRate()),2);

                                secPay.setBaseCuryMoney(theDateBaseAddValue);
                                //基础货币管理成本估值增值=原币管理成本估值增值*基础汇率
                                secPay.setMBaseCuryMoney(theDateBaseAddValue);
                                //基础货币估值增值=原币估值增值*基础汇率
                                secPay.setVBaseCuryMoney(theDateBaseAddValue);
                                //---------------------------------------------------
                                //------------------------------组合货币增值------------
                                //组合货币成本估值增值=原币成本估值增值*基础汇率/组合汇率
                                theDatePortAddValue = YssD.round(this.getSettingOper().calPortMoney(secPay.getMoney(),
                                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(), rs.getString("FCuryCode"),
                                    this.dDate, this.portCode),2);
                                secPay.setPortCuryMoney(theDatePortAddValue);
                                //组合货币管理成本估值增值=原币管理成本估值增值*基础汇率/组合汇率
                                secPay.setMPortCuryMoney(theDatePortAddValue);
                                //组合货币估值增值=原币估值增值*基础汇率/组合汇率
                                secPay.setVPortCuryMoney(theDatePortAddValue);
                                //-----------------------------------------------------

                                if(!sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                                    secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV); //调拨类型09
                                    secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_MV); //调拨子类型09FP01
                                }else{
                                    secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec); //调拨类型06应收款项
                                    secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_REC); //调拨子类型06FP01应收期权收益
                                }
                                secPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                                secPay.setStrPortCode(rs.getString("FPortCode"));
                                secPay.setBrokerCode(rs.getString("FAnalysisCode2"));
                                secPay.setInvMgrCode(rs.getString("FAnalysisCode1"));
                                secPay.setStrCuryCode(rs.getString("FCuryCode"));
                                secPay.setAttrClsCode(rs.getString("FAttrClsCode"));
                                secPay.checkStateId = 1;

                                mktPrice.setValDate(this.dDate);//估值日期
                                mktPrice.setSecurityCode(secPay.getStrSecurityCode());//证券代码
                                mktPrice.setPortCode(portCode);//组合代码
                                //实际计算行情 = （正股行情-行权价）* 每手正股数量
                                mktPrice.setPrice(YssD.sub(dTSecuturyMatketPrice,dAimPrice) > 0 ?YssD.mul(YssD.sub(dTSecuturyMatketPrice,dAimPrice),rs.getDouble("FMultiple")):0);
                                mktPrice.setOtPrice1(dMarketPrice);//期权行情
                                mktPrice.setOtPrice2(dTSecuturyMatketPrice);//正股行情
                                mktPrice.setMtvCode(vMethod.getMTVCode()); //设置估值方法
                                mktPrice.setValType("OptionsMV"); //设置估值类型为普通类型的证券，与估值界面上的代码相一致。

                                if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                                	sKey = secPay.getStrSecurityCode() + "\f" +
                                    (this.invmgrSecField.length() != 0 ?
                                     (secPay.getInvMgrCode() + "\f") : "") +
                                    (this.brokerSecField.length() != 0 ?
                                     (secPay.getBrokerCode() + "\f") : "") +
                                    secPay.getStrSubTsfTypeCode() + "\f" +
                                    (secPay.getAttrClsCode().length() == 0 ? " " :
                                     secPay.getAttrClsCode());
                                	hmResult.put(sKey, secPay);
                                }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                                	sKey = rs.getString("FNum") + "\f" +secPay.getStrSecurityCode() + "\f" +
                                    (this.invmgrSecField.length() != 0 ?
                                     (secPay.getInvMgrCode() + "\f") : "") +
                                    (this.brokerSecField.length() != 0 ?
                                     (secPay.getBrokerCode() + "\f") : "") +
                                    secPay.getStrSubTsfTypeCode() + "\f" +
                                    (secPay.getAttrClsCode().length() == 0 ? " " :
                                     secPay.getAttrClsCode());
                                	hmResult.put(sKey, secPay);
                                }
                                //保证金每日结转时，产生收入02冲减应收款项06，并产生资金调拨，否则，只会产生估值增值09
                                if(sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                                	secPecPay = (SecPecPayBean) secPay.clone();
                                	secPecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income); //调拨类型02收入
                                    secPecPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_SR); //调拨子类型为02FP01，其余数据和secPay一样
                                    if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                                    	sKey = secPay.getStrSecurityCode() + "\f" +
                                        (this.invmgrSecField.length() != 0 ?
                                         (secPay.getInvMgrCode() + "\f") : "") +
                                        (this.brokerSecField.length() != 0 ?
                                         (secPay.getBrokerCode() + "\f") : "") +
                                        secPay.getStrSubTsfTypeCode() + "\f" +
                                        (secPay.getAttrClsCode().length() == 0 ? " " :
                                         secPay.getAttrClsCode());
                                    	hmResult.put(sKey, secPay);
                                    }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                                    	sKey = rs.getString("FNum") + "\f" +secPay.getStrSecurityCode() + "\f" +
                                        (this.invmgrSecField.length() != 0 ?
                                         (secPay.getInvMgrCode() + "\f") : "") +
                                        (this.brokerSecField.length() != 0 ?
                                         (secPay.getBrokerCode() + "\f") : "") +
                                        secPay.getStrSubTsfTypeCode() + "\f" +
                                        (secPay.getAttrClsCode().length() == 0 ? " " :
                                         secPay.getAttrClsCode());
                                    	hmResult.put(sKey, secPay);
                                    }
                                }
                                
                                hmValPrice.put(mktPrice.getSecurityCode(),mktPrice);
                                if(sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                                	saveCashTransferData(secPecPay); //产生资金调拨，
                                }
                            } else {
                                continue;
                            }
                        } else { //期权认沽
                            if (theDateStorageAmount > 0) { //买入认沽
                                key += "\f" + "09";
                                secPay = new SecPecPayBean();
                                mktPrice=new  ValMktPriceBean();//估值行情
                                if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                                	yesSecRecPay = (SecRecPayBalBean) yesDateaddValue.get(key);
                                }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                                	if(yesSecRecPay == null){
                                		yesSecRecPay = new SecRecPayBalBean();
                                		yesSecRecPay.setDVBal(rs.getDouble("FCuryValue"));
                                		yesSecRecPay.setDVBaseBal(rs.getDouble("FBaseCuryValue"));
                                		yesSecRecPay.setDVPortBal(rs.getDouble("FPortCuryValue"));
                                	}else{
                                		yesSecRecPay.setDVBal(rs.getDouble("FCuryValue"));
                                		yesSecRecPay.setDVBaseBal(rs.getDouble("FBaseCuryValue"));
                                		yesSecRecPay.setDVPortBal(rs.getDouble("FPortCuryValue"));
                                	} 
                                }


                                //用通用方法，获取组合汇率
                                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), this.portCode,
                                    vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                    vMethod.getPortRateSrcCode(), vMethod.getPortRateCode());
                                dPortRate = rateOper.getDPortRate(); //获取组合汇率

                                //基础和组合汇率
                                secPay.setBaseCuryRate(dBaseRate);
                                secPay.setPortCuryRate(dPortRate);

                                //业务日期
                                secPay.setTransDate(this.dDate);
                                
                                // 原币成本估值增值当天余额=((行权价-正股收盘价)>0?(行权价-正股收盘价)* 合约数*每合约代表的正股数量:0))-期权成本
                                theDayAddValueTotal = YssD.round(
                                		YssD.sub(
                                				YssD.sub(dAimPrice,dTSecuturyMatketPrice) > 0 ?YssD.mul(YssD.sub(dAimPrice,dTSecuturyMatketPrice),theDateStorageAmount,rs.getDouble("FMultiple")):0,
                                						Math.abs(rs.getDouble("FStorageCost"))
                                						),2);
                                
                                //--------------------原币增值----------------------
                                //估值增值=((行权价-正股收盘价)>0?(行权价-正股收盘价)* 合约数*每合约代表的正股数量:0))-期权成本 - 昨日成本估值增值额 + 当日卖出的原币估值增值
                                theDateAddValue = YssD.round(YssD.add(
                                		YssD.sub(
                                				YssD.sub(dAimPrice,dTSecuturyMatketPrice) > 0 ?YssD.mul(YssD.sub(dAimPrice,dTSecuturyMatketPrice),theDateStorageAmount,rs.getDouble("FMultiple")):0,
                                				YssD.add(
                                						Math.abs(rs.getDouble("FStorageCost")),yesSecRecPay == null ? 0 : yesSecRecPay.getDVBal())),
                                						theDateSaleAddValue),2);
                                //原币成本增值
                                secPay.setMoney(theDateAddValue);
                                //原币管理成本增值=(标的指数价格-当日指数价格)*库存数量*放大倍数-昨日管理成本估值增值
                                secPay.setMMoney(theDateAddValue);
                                //原币估值增值=(标的指数价格-当日指数价格)*库存数量*放大倍数--昨日估值增值+当日买入的原币估值增值
                                secPay.setVMoney(theDateAddValue);
                                //---------------------------------------------------
                                //-----------------------------基础货币增值------------
                                //基础货币成本估值增值=原币成本估值增值*基础汇率
                                theDateBaseAddValue = YssD.round(this.getSettingOper().calBaseMoney(secPay.getMoney(), secPay.getBaseCuryRate()),2);

                                secPay.setBaseCuryMoney(theDateBaseAddValue);
                                //基础货币管理成本估值增值=原币管理成本估值增值*基础汇率
                                secPay.setMBaseCuryMoney(theDateBaseAddValue);
                                //基础货币估值增值=原币估值增值*基础汇率+当日卖出基础货币估值增值
                                secPay.setVBaseCuryMoney(theDateBaseAddValue);
                                //---------------------------------------------------
                                //------------------------------组合货币增值------------
                                //组合货币成本估值增值=原币成本估值增值*基础汇率/组合汇率
                                theDatePortAddValue = YssD.round(this.getSettingOper().calPortMoney(secPay.getMoney(),
                                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(), rs.getString("FCuryCode"),
                                    this.dDate, this.portCode),2);

                                secPay.setPortCuryMoney(theDatePortAddValue);
                                //组合货币管理成本估值增值=原币管理成本估值增值*基础汇率/组合汇率
                                secPay.setMPortCuryMoney(theDatePortAddValue);
                                //组合货币估值增值=原币估值增值*基础汇率/组合汇率 + 当日卖出组合货币估值增值
                                secPay.setVPortCuryMoney(theDatePortAddValue);
                                //-----------------------------------------------------

                                secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV); //调拨类型09
                                secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_MV); //调拨子类型09FP01
                                secPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                                secPay.setStrPortCode(rs.getString("FPortCode"));
                                secPay.setBrokerCode(rs.getString("FAnalysisCode2"));
                                secPay.setInvMgrCode(rs.getString("FAnalysisCode1"));
                                secPay.setStrCuryCode(rs.getString("FCuryCode"));
                                secPay.setAttrClsCode(rs.getString("FAttrClsCode"));

                                secPay.checkStateId = 1;

                                mktPrice.setValDate(this.dDate);//估值日期
                                mktPrice.setSecurityCode(secPay.getStrSecurityCode());//证券代码
                                mktPrice.setPortCode(portCode);//组合代码
                                //实际计算行情 = （行权价-正股行情）* 每手正股数量
                                mktPrice.setPrice(YssD.sub(dAimPrice,dTSecuturyMatketPrice) > 0 ?YssD.mul(YssD.sub(dAimPrice,dTSecuturyMatketPrice),rs.getDouble("FMultiple")):0);
                                mktPrice.setOtPrice1(dMarketPrice);//期权行情
                                mktPrice.setOtPrice2(dTSecuturyMatketPrice);//正股行情
                                mktPrice.setMtvCode(vMethod.getMTVCode()); //设置估值方法
                                mktPrice.setValType("OptionsMV"); //设置估值类型为普通类型的证券，与估值界面上的代码相一致。

                                if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                                	sKey = secPay.getStrSecurityCode() + "\f" +
                                    (this.invmgrSecField.length() != 0 ?
                                     (secPay.getInvMgrCode() + "\f") : "") +
                                    (this.brokerSecField.length() != 0 ?
                                     (secPay.getBrokerCode() + "\f") : "") +
                                    secPay.getStrSubTsfTypeCode() + "\f" +
                                    (secPay.getAttrClsCode().length() == 0 ? " " :
                                     secPay.getAttrClsCode());
                                	hmResult.put(sKey, secPay);
                                }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                                	sKey = rs.getString("FNum") + "\f" +secPay.getStrSecurityCode() + "\f" +
                                    (this.invmgrSecField.length() != 0 ?
                                     (secPay.getInvMgrCode() + "\f") : "") +
                                    (this.brokerSecField.length() != 0 ?
                                     (secPay.getBrokerCode() + "\f") : "") +
                                    secPay.getStrSubTsfTypeCode() + "\f" +
                                    (secPay.getAttrClsCode().length() == 0 ? " " :
                                     secPay.getAttrClsCode());
                                	hmResult.put(sKey, secPay);
                                }
                                hmValPrice.put(mktPrice.getSecurityCode(),mktPrice);

                            } else if (theDateStorageAmount < 0) { //卖出认沽
                            	if(sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                            		key += "\f" + "02";
                            	}else{
                            		key += "\f" + "09";
                            	}
                                secPay = new SecPecPayBean();
                                mktPrice=new  ValMktPriceBean();//估值行情
                                if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                                	yesSecRecPay = (SecRecPayBalBean) yesDateaddValue.get(key);
                                }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                                	if(yesSecRecPay == null){
                                		yesSecRecPay = new SecRecPayBalBean();
                                		yesSecRecPay.setDVBal(rs.getDouble("FCuryValue"));
                                		yesSecRecPay.setDVBaseBal(rs.getDouble("FBaseCuryValue"));
                                		yesSecRecPay.setDVPortBal(rs.getDouble("FPortCuryValue"));
                                	}else{
                                		yesSecRecPay.setDVBal(rs.getDouble("FCuryValue"));
                                		yesSecRecPay.setDVBaseBal(rs.getDouble("FBaseCuryValue"));
                                		yesSecRecPay.setDVPortBal(rs.getDouble("FPortCuryValue"));
                                	} 
                                }

                                //用通用方法，获取组合汇率
                                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), this.portCode,
                                    vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                    vMethod.getPortRateSrcCode(), vMethod.getPortRateCode());
                                dPortRate = rateOper.getDPortRate(); //获取组合汇率

                                //基础和组合汇率
                                secPay.setBaseCuryRate(dBaseRate);
                                secPay.setPortCuryRate(dPortRate);

                                //业务日期
                                secPay.setTransDate(this.dDate);
                                
                                
                                
                                
                                // 原币成本估值增值当天余额=期权成本 -((行权价-正股收盘价)>0?(行权价-正股收盘价)* 合约数*每合约代表的正股数量:0))
                                theDayAddValueTotal =YssD.round(
                                		YssD.sub(Math.abs(rs.getDouble("FStorageCost")),
                                				YssD.sub(dAimPrice,dTSecuturyMatketPrice) > 0 ?YssD.mul(YssD.sub(dAimPrice,dTSecuturyMatketPrice),Math.abs(theDateStorageAmount),rs.getDouble("FMultiple")):0)
                                						,2);
                                	
                                //--------------------原币增值----------------------
                                //估值增值=期权成本 -((行权价-正股收盘价)>0?(行权价-正股收盘价)* 合约数*每合约代表的正股数量:0)) - 昨日成本估值增值额 + 当日卖出的原币估值增值
                                theDateAddValue = YssD.round(YssD.add(
                                		YssD.sub(Math.abs(rs.getDouble("FStorageCost")),
                                				YssD.sub(dAimPrice,dTSecuturyMatketPrice) > 0 ?YssD.mul(YssD.sub(dAimPrice,dTSecuturyMatketPrice),Math.abs(theDateStorageAmount),rs.getDouble("FMultiple")):0,
                                				yesSecRecPay == null ? 0 : yesSecRecPay.getDVBal()),
                                						theDateSaleAddValue),2);
                                //原币成本增值
                                secPay.setMoney(theDateAddValue);
                                //原币管理成本增值=(标的指数价格-当日指数价格)*库存数量*放大倍数-昨日管理成本估值增值
                                secPay.setMMoney(theDateAddValue);
                                //原币估值增值=(标的指数价格-当日指数价格)*库存数量*放大倍数--昨日估值增值+当日买入的原币估值增值
                                secPay.setVMoney(theDateAddValue);
                                //---------------------------------------------------
                                //-----------------------------基础货币增值------------
                                //基础货币成本估值增值=原币成本估值增值*基础汇率
                                theDateBaseAddValue = YssD.round(this.getSettingOper().calBaseMoney(secPay.getMoney(), secPay.getBaseCuryRate()),2);

                                secPay.setBaseCuryMoney(theDateBaseAddValue);
                                //基础货币管理成本估值增值=原币管理成本估值增值*基础汇率
                                secPay.setMBaseCuryMoney(theDateBaseAddValue);
                                //基础货币估值增值=原币估值增值*基础汇率+当日卖出基础货币估值增值
                                secPay.setVBaseCuryMoney(theDateBaseAddValue);
                                //---------------------------------------------------
                                //------------------------------组合货币增值------------
                                //组合货币成本估值增值=原币成本估值增值*基础汇率/组合汇率
                                theDatePortAddValue = YssD.round(this.getSettingOper().calPortMoney(secPay.getMoney(),
                                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(), rs.getString("FCuryCode"),
                                    this.dDate, this.portCode),2);

                                secPay.setPortCuryMoney(theDatePortAddValue);
                                //组合货币管理成本估值增值=原币管理成本估值增值*基础汇率/组合汇率
                                secPay.setMPortCuryMoney(theDatePortAddValue);
                                //组合货币估值增值=原币估值增值*基础汇率/组合汇率 + 当日卖出组合货币估值增值
                                secPay.setVPortCuryMoney(theDatePortAddValue);
                                //-----------------------------------------------------
                                
                                if(!sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                                    secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV); //调拨类型09
                                    secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_MV); //调拨子类型09FP01
                                }else{
                                    secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec); //调拨类型06应收款项
                                    secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_REC); //调拨子类型06FP01应收期权收益
                                }
                                secPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                                secPay.setStrPortCode(rs.getString("FPortCode"));
                                secPay.setBrokerCode(rs.getString("FAnalysisCode2"));
                                secPay.setInvMgrCode(rs.getString("FAnalysisCode1"));
                                secPay.setStrCuryCode(rs.getString("FCuryCode"));
                                secPay.setAttrClsCode(rs.getString("FAttrClsCode"));

                                secPay.checkStateId = 1;

                                mktPrice.setValDate(this.dDate);//估值日期
                                mktPrice.setSecurityCode(secPay.getStrSecurityCode());//证券代码
                                mktPrice.setPortCode(portCode);//组合代码
                                //实际计算行情 = （行权价-正股行情）* 每手正股数量
                                mktPrice.setPrice(YssD.sub(dAimPrice,dTSecuturyMatketPrice) > 0 ?YssD.mul(YssD.sub(dAimPrice,dTSecuturyMatketPrice),rs.getDouble("FMultiple")):0);
                                mktPrice.setOtPrice1(dMarketPrice);//期权行情
                                mktPrice.setOtPrice2(dTSecuturyMatketPrice);//正股行情
                                mktPrice.setMtvCode(vMethod.getMTVCode()); //设置估值方法
                                mktPrice.setValType("OptionsMV"); //设置估值类型为普通类型的证券，与估值界面上的代码相一致。

                                if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                                	sKey = secPay.getStrSecurityCode() + "\f" +
                                    (this.invmgrSecField.length() != 0 ?
                                     (secPay.getInvMgrCode() + "\f") : "") +
                                    (this.brokerSecField.length() != 0 ?
                                     (secPay.getBrokerCode() + "\f") : "") +
                                    secPay.getStrSubTsfTypeCode() + "\f" +
                                    (secPay.getAttrClsCode().length() == 0 ? " " :
                                     secPay.getAttrClsCode());
                                	hmResult.put(sKey, secPay);
                                }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                                	sKey = rs.getString("FNum") + "\f" +secPay.getStrSecurityCode() + "\f" +
                                    (this.invmgrSecField.length() != 0 ?
                                     (secPay.getInvMgrCode() + "\f") : "") +
                                    (this.brokerSecField.length() != 0 ?
                                     (secPay.getBrokerCode() + "\f") : "") +
                                    secPay.getStrSubTsfTypeCode() + "\f" +
                                    (secPay.getAttrClsCode().length() == 0 ? " " :
                                     secPay.getAttrClsCode());
                                	hmResult.put(sKey, secPay);
                                }
                                //保证金每日结转时，产生收入02冲减应收款项06，并产生资金调拨，否则，只会产生估值增值09
                                if(sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                                	secPecPay = (SecPecPayBean) secPay.clone();
                                	secPecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income); //调拨类型02收入
                                    secPecPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_SR); //调拨子类型为02FP01，其余数据和secPay一样
                                    if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
                                    	sKey = secPay.getStrSecurityCode() + "\f" +
                                        (this.invmgrSecField.length() != 0 ?
                                         (secPay.getInvMgrCode() + "\f") : "") +
                                        (this.brokerSecField.length() != 0 ?
                                         (secPay.getBrokerCode() + "\f") : "") +
                                        secPay.getStrSubTsfTypeCode() + "\f" +
                                        (secPay.getAttrClsCode().length() == 0 ? " " :
                                         secPay.getAttrClsCode());
                                    	hmResult.put(sKey, secPay);
                                    }else if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                                    	sKey = rs.getString("FNum") + "\f" +secPay.getStrSecurityCode() + "\f" +
                                        (this.invmgrSecField.length() != 0 ?
                                         (secPay.getInvMgrCode() + "\f") : "") +
                                        (this.brokerSecField.length() != 0 ?
                                         (secPay.getBrokerCode() + "\f") : "") +
                                        secPay.getStrSubTsfTypeCode() + "\f" +
                                        (secPay.getAttrClsCode().length() == 0 ? " " :
                                         secPay.getAttrClsCode());
                                    	hmResult.put(sKey, secPay);
                                    }
                                }
                               
                                hmValPrice.put(mktPrice.getSecurityCode(),mktPrice);
                                if(sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                                	saveCashTransferData(secPecPay); //产生资金调拨，
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                    if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                    	setFIFOStorageApp(rs,theDayAddValueTotal);//期权先入先出估值增值余额表bean赋值
                    }
                }//end while
                dbl.closeResultSetFinal(rs);
            }//end for
            //统计现金库存-----------------------
            try {
                cashstgstat = (BaseStgStatDeal) pub.
                    getOperDealCtx().getBean("CashStorage");
                cashstgstat.setYssPub(pub);
                cashstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(this.portCode));
                
                //-------产生当天的期权先入先出估值增值余额数据，保存到期权先入先出估值增值余额表（Tb_XXX_Data_OptiFIFOAppStk）-----//
                if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO)){//先入先出
                	//if(alFIFOStorageApp.size() > 0){
                    	fifoAddValueAdmin = new OptionsFIFOStorageAddValueAdmin();
                    	fifoAddValueAdmin.setYssPub(pub);
                    	
                    	fifoAddValueAdmin.deleteData(this.portCode,this.dDate);
                    	fifoAddValueAdmin.savingData(alFIFOStorageApp);
                    //}
                	//先入先出资产估值时，产生每一笔交易证券的估值增值数据，最后要汇总保存到应收应付表中
                	Iterator it =  hmResult.values().iterator();
                	while(it.hasNext()){
                		secPay = (SecPecPayBean)it.next();
                		sKey = secPay.getStrSecurityCode() + "\f" +
                        (this.invmgrSecField.length() != 0 ?
                         (secPay.getInvMgrCode() + "\f") : "") +
                        (this.brokerSecField.length() != 0 ?
                         (secPay.getBrokerCode() + "\f") : "") +
                        secPay.getStrSubTsfTypeCode() + "\f" +
                        (secPay.getAttrClsCode().length() == 0 ? " " :
                         secPay.getAttrClsCode());
                		if(hmFIFOResult.containsKey(sKey)){
                			secPecPay = (SecPecPayBean)hmFIFOResult.get(sKey);
                			//原币成本增值
                			secPecPay.setMoney(YssD.add(secPay.getMoney(),secPecPay.getMoney()));
                			secPecPay.setMMoney(secPecPay.getMoney());
                			secPecPay.setVMoney(secPecPay.getMoney());
                			//---------------------------------------------------
                			//-----------------------------基础货币增值------------
                			secPecPay.setBaseCuryMoney(YssD.add(secPay.getBaseCuryMoney(),secPecPay.getBaseCuryMoney()));
                			secPecPay.setMBaseCuryMoney(secPecPay.getBaseCuryMoney());
                			secPecPay.setVBaseCuryMoney(secPecPay.getBaseCuryMoney());
                			//---------------------------------------------------
                			//------------------------------组合货币增值------------
                			secPecPay.setPortCuryMoney(YssD.add(secPay.getPortCuryMoney(),secPecPay.getPortCuryMoney()));
                			secPecPay.setMPortCuryMoney(secPecPay.getPortCuryMoney());
                			secPecPay.setVPortCuryMoney(secPecPay.getPortCuryMoney());
                			//-----------------------------------------------------
                		}else{
                			hmFIFOResult.put(sKey,secPay);
                		}
                	}//end while
                	if(hmResult.size() > 0){
                		hmResult.clear();
                	}
                	hmResult = hmFIFOResult;
                }//end if
                //--------------------------------------------end-------------------------------------------------//
                
            } catch (Exception e) {
                throw new YssException("统计现金库存出错！", e);
            }
            //------------------end--------------
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }finally{
           dbl.closeResultSetFinal(rs);
        }
        return hmResult;
    }

    /**
     * saveCashTransferData 产生资金调拨数据 插入到资金调拨表和子表中
     *
     * @param secPay SecPecPayBean
     */
    private void saveCashTransferData(SecPecPayBean secPay) throws YssException {
        CashTransAdmin cashtrans = null; //现金调拨操作实体类
        String filtersRelaNums = "";
        boolean bTrans = false;
        ArrayList cashTransData = new ArrayList();
        Connection conn = dbl.loadConnection();
        try {
            cashTransData = getCashTransData(secPay);//此方法主要是设置变量的值
            cashtrans = new CashTransAdmin();
            cashtrans.setYssPub(pub);
            cashtrans.addList(cashTransData);
            //增加事务控制和锁，以免在多用户同时处理时出现调拨编号重复
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer")); //给表加锁
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));
            //保存数据
            cashtrans.insert("", secPay.getEndDate(), this.dDate, secPay.getStrTsfTypeCode(), secPay.getStrSubTsfTypeCode()
                             , "", "", "", "", "", "",
                             secPay.getStrSecurityCode(), -1, "", secPay.getStrPortCode(), 0,
                             secPay.getInvMgrCode(), secPay.getBrokerCode(), "", true, "");
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * getCashTransData 此方法主要是设置变量的值，并返回
     *
     * @param secPay SecPecPayBean
     * @return ArrayList
     */
    private ArrayList getCashTransData(SecPecPayBean secPay) throws YssException {
        ArrayList curCashTransArr = new ArrayList();
        TransferBean transfer = null; //调拨类
        TransferSetBean transferset = null; //调拨子类
        ArrayList subtransfer = null;
        try {
            subtransfer = new ArrayList();
            transfer = setTransferAttr(secPay); //设置调拨数据
            transferset = setTransferSetAttr(secPay); //设置资金调拨子表
            subtransfer.add(transferset);
            transfer.setSubTrans(subtransfer);
            curCashTransArr.add(transfer);

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return curCashTransArr;
    }

    /**
     * setTransferSetAttr 设置资金调拨子表
     *
     * @param secPay SecPecPayBean
     * @param string String
     * @return TransferSetBean
     */
    private TransferSetBean setTransferSetAttr(SecPecPayBean secPay) throws YssException {
        TransferSetBean transferset = new TransferSetBean(); //资金子调拨
        double dBaseRate = 0; //基础汇率
        double dPortRate = 0; //组合汇率
        double money = 0.0; //调拨金额
        SecurityBean security = null; //证券bean
        try {
            dBaseRate = secPay.getBaseCuryRate(); //基础汇率
            dPortRate = secPay.getPortCuryRate(); //组合汇率

            security = new SecurityBean();
            security.setYssPub(pub);
            security.setSecurityCode(secPay.getStrSecurityCode()); //证券代码
            security.getSetting();

            transferset.setSPortCode(secPay.getStrPortCode()); //组合代码

            transferset.setSAnalysisCode1(secPay.getInvMgrCode()); //投资经理

            transferset.setSAnalysisCode2(secPay.getBrokerCode()); //券商代码

            transferset.setSCashAccCode(secPay.getStrCuryCode()); //现金账户代码
            money = secPay.getVMoney(); //调拨金额
            transferset.setIInOut(1);

            transferset.setDMoney(money); //调拨金额
            transferset.setDBaseRate(dBaseRate);
            transferset.setDPortRate(dPortRate);
            transferset.setSCashAccCode(sAccCashCode); //设置现金账户
            transferset.checkStateId = 1;

        } catch (Exception e) {
            throw new YssException("设置资金调拨子表数据出错！", e);
        }
        return transferset;

    }

    /**
     * setTransferAttr 设置调拨数据
     *
     * @param secPay SecPecPayBean
     * @param string String
     * @return TransferBean
     */
    private TransferBean setTransferAttr(SecPecPayBean secPay) throws YssException {
        TransferBean transfer = new TransferBean(); //资金调拨
        try {
            transfer.setDtTransferDate(secPay.getTransDate()); //调拨日期
            transfer.setDtTransDate(secPay.getTransDate()); //业务日期

            transfer.setStrTsfTypeCode(secPay.getStrTsfTypeCode()); //调拨类型
            transfer.setStrSubTsfTypeCode(secPay.getStrSubTsfTypeCode()); //调拨子类型

            transfer.setFRelaNum(secPay.getStrNum()); //编号
            transfer.setStrTradeNum(secPay.getStrNum());
            transfer.setStrSecurityCode(secPay.getStrSecurityCode()); //证券代码
            transfer.setSrcCashAccCode(sAccCashCode); //现金账户
            securityCodes += transfer.getStrSecurityCode() + ",";

            transfer.checkStateId = 1;
            sRelaNums += transfer.getFRelaNum() + ",";

        } catch (Exception e) {
            throw new YssException("设置调拨数据出错！", e);
        }
        return transfer;
    }

    /**
     * getOptionsTradeRela  获取昨日的估值增值余额
     *
     * @param dDate Date
     * @param portCodes String
     */
    private HashMap getYesSecRecPay(java.util.Date dDate, String portCodes) throws YssException {
        StringBuffer buff = null;
        SecRecPayBalBean yesSecRecPay = null; //证券应收应付库存
        HashMap addValue = new HashMap(); //保存数据
        ResultSet rs = null;
        try {
            buff = new StringBuffer();
            buff.append(" select * from ").append(pub.yssGetTableName("Tb_Stock_SecRecPay"));//应收应付库存表
            buff.append(" where FStorageDate=").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));
            buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(portCodes)).append(")");
            buff.append(" and FSubTsfTypeCode in('09FP01','02FP01')and FCheckState = 1");

            rs = dbl.queryByPreparedStatement(buff.toString()); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) { //以下为变量赋值
                yesSecRecPay = new SecRecPayBalBean();
                yesSecRecPay.setYssPub(pub);
                yesSecRecPay.setSPortCode(rs.getString("FPortCode"));//组合代码
                yesSecRecPay.setSAnalysisCode1(rs.getString("FAnalysisCode1"));//分析代码1
                yesSecRecPay.setSAnalysisCode2(rs.getString("FAnalysisCode2"));//分析代码2
                yesSecRecPay.setSSecurityCode(rs.getString("FSecurityCode"));//证券代码
                yesSecRecPay.setDBal(rs.getDouble("FBal"));//原币成本估值增值
                yesSecRecPay.setDBaseBal(rs.getDouble("FBaseCuryBalF"));//基础货币成本估值增值
                yesSecRecPay.setDPortBal(rs.getDouble("FPortCuryBalF"));//组合货币成本估值增值
                yesSecRecPay.setDVBal(rs.getDouble("FVBal"));//原币管理成本估值增值
                yesSecRecPay.setDVBaseBal(rs.getDouble("FVBaseCuryBal"));//基础货币管理成本估值增值
                yesSecRecPay.setDVPortBal(rs.getDouble("FVPortCuryBal"));//组合货币管理成本估值增值
                yesSecRecPay.setDMBal(rs.getDouble("FMBal"));
                yesSecRecPay.setDMBaseBal(rs.getDouble("FMBaseCuryBal"));
                yesSecRecPay.setDMPortBal(rs.getDouble("FMPortCuryBal"));
                yesSecRecPay.setSTsfTypeCode(rs.getString("FTsfTypeCode"));

                addValue.put(yesSecRecPay.getSPortCode() + "\f" + yesSecRecPay.getSSecurityCode() + "\f" +
                             (analy1 ? yesSecRecPay.getSAnalysisCode1() : "") + "\f" + (analy2 ? yesSecRecPay.getSAnalysisCode2() : "") + "\f"
                             + yesSecRecPay.getSTsfTypeCode()
                             , yesSecRecPay);
            }
        } catch (Exception e) {
            throw new YssException("从证券应收应付表表中获取昨日的估值增值余额出错！\r\t", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return addValue;
    }

    /**
     *设置删除条件
     * @return Object
     */
    public Object filterSecCondition() {
        SecPecPayBean secpay = new SecPecPayBean();
        secpay.setStrTsfTypeCode("09,06,02");
        secpay.setStrSubTsfTypeCode("09FP01,06FP01,02FP01"); //增加了期权的删除条件09FP01
        secpay.setInOutType(1); //方向
        return secpay;
    }
    /**
     * 通过组合代码获取期权保证金结算方式或者获取期权核算方式
     * @param sPortCode String：组合代码
     * @return String
     */
    private String getAccountTypeBy(String sPortCode,String sCtlParam) throws YssException {
    	java.util.Hashtable htAccountType = null;
    	String sResult ="";
    	try{
	        CtlPubPara pubPara = new CtlPubPara();
	        pubPara.setYssPub(pub);
	        htAccountType = pubPara.getOptionBailCarryType(sCtlParam);//通用参数获取期权保证金结转类型，默认-平仓结转
	        if(sCtlParam.equalsIgnoreCase("bailcarrytype")){
	        	sResult = YssOperCons.YSS_TYCS_BAILMONEY_PCTRANSFER;
		        String sTheDayFirstFIFO = (String) htAccountType.get(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER);//获取value值
				//MS01599 QDV4赢时胜（深圳）2010年8月17日01_B 期权结转方式设置影响期权日常业务的估值，导致估值增值计算不正确 --- 
		        //每日结转暂不支持，先注释
//		        if (sTheDayFirstFIFO != null && sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
//		            sResult = YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER;//每日结转
//		        }
	        }else{
	        	sResult = YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG;//移动加权
	        	String sTheDayFirstFIFO = (String) htAccountType.get(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO);//获取value值
		        if (sTheDayFirstFIFO != null && sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
		            sResult = YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO;//先入先出
		        }
	        }
    	}catch (Exception e) {
			throw new YssException("通过组合代码获取期权保证金结算方式或者获取期权核算方式出错！",e);
		}
        return sResult;
    }
    /**
     * 期权先入先出估值增值余额表bean赋值
     * @param rs
     * @param theDayAddValueTotal
     * @throws YssException
     */
    private void setFIFOStorageApp(ResultSet rs , double theDayAddValueTotal) throws YssException{
    	OptionsFIFOStorageAddValueBean fifoAddValueBean = null;
    	try{
    		fifoAddValueBean = new OptionsFIFOStorageAddValueBean();
    		fifoAddValueBean.setSNum(rs.getString("FNum"));
    		fifoAddValueBean.setSStorageDate(YssFun.formatDate(this.dDate,"yyyy-MM-dd"));
    		fifoAddValueBean.setSSecurityCode(rs.getString("FSecurityCode"));
    		fifoAddValueBean.setSPortCode(rs.getString("FPortCode"));
    		fifoAddValueBean.setDCuryValue(theDayAddValueTotal);
    		fifoAddValueBean.setDBaseCuryValue(YssD.round(YssD.mul(theDayAddValueTotal,rs.getDouble("FBaseCuryRate")),2));
    		fifoAddValueBean.setDPortCuryValue(YssD.round(YssD.div(YssD.mul(theDayAddValueTotal,rs.getDouble("FBaseCuryRate")),rs.getDouble("FPortCuryRate")),2));
    		fifoAddValueBean.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));
    		fifoAddValueBean.setDPortCuryRate(rs.getDouble("FPortCuryRate"));
    		
    		alFIFOStorageApp.add(fifoAddValueBean);
    		
    	}catch (Exception e) {
			throw new YssException("期权先入先出估值增值余额表bean赋值出错！",e);
		}
    }
}








