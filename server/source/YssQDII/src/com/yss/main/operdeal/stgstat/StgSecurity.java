package com.yss.main.operdeal.stgstat;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.yss.commeach.EachRateOper;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.dao.ICostCalculate;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.operdata.SecStockDetail;
import com.yss.main.operdata.TradeRelaBean;
import com.yss.main.operdeal.bond.BondInsCfgFormula;
import com.yss.main.operdeal.businesswork.BaseBusinWork;
import com.yss.main.operdeal.businesswork.SellTradeRelaCal;
import com.yss.main.operdeal.businesswork.futures.futuresdistilldata.SecurityStorageDistill;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.platform.pfoper.pubpara.innerparams.BaseInnerPubParamsDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.innerparams.InnerPubParamsWithPurchase;
import com.yss.main.parasetting.SecurityBean;
import com.yss.main.storagemanage.SecurityStorageBean;
import com.yss.manager.CashTransAdmin;
import com.yss.manager.SecRecPayAdmin;
import com.yss.pojo.cache.YssCost;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class StgSecurity
    extends BaseStgStatDeal {
    //----------- MS00269 QDV4中保2009年02月24日02_B -------------------------------------------------
    private boolean bondDigitF = false; //判断债券支付时的小数位是否为大于2位
   	private String unStgTradeTypeCode="";//添加不作统计的业务类型代码  by leeyu 20100417 QDV4中保2010年4月14日02_B MS01092  合并太平版本代码
    //---------------------------------

    //---- sj add 20090612 MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A -----------------------------
    private HashMap allPubParamsMap = null; //放置组合级别的通用参数设置的容器（国内）
    public HashMap getAllPubParamsMap() {
		return allPubParamsMap;
	}

	public void setAllPubParamsMap(HashMap allPubParamsMap) {
		this.allPubParamsMap = allPubParamsMap;
	}

	private BaseInnerPubParamsDeal innerPubParamsDeal = null;
    //--------------------------------------
    public StgSecurity() {
    }

    //把前日的库存存放到Map中
    public HashMap getEveStg(java.util.Date dDate) throws YssException {
        HashMap hmEveStg = new HashMap();
        SecurityStorageBean secstorage = null;

        boolean analy1;
        boolean analy2;
        boolean analy3;

        ResultSet rs = null;

        String sKey = "";

        String strSql = "";

        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");

            //添加投资类型 FInvestType的处理 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
            //添加实际利率 FEffectiveRate的处理 蒋锦 20090908 MS00656 :QDV4赢时胜(上海)2009年8月24日01_A
            strSql = "select a.*,b.FTradeCury as FCuryCode ,c.FBal as FBal, c.FPortCuryBal as FPortCuryBal, c.FBaseCuryBal as FBaseCuryBal from (" +
                "select FSecurityCode,FPortCode,FStorageAmount,FStorageCost,FMStorageCost,FVStorageCost," +
                " FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost," +
                " FBailMoney," +
                " FPortCuryRate as FPortYesRate,FBaseCuryRate as FBaseYesRate, " +
                " FCatType as FCatType,FAttrClsCode as FAttrClsCode,FInvestType as FInvestType, FEffectiveRate" +
                (analy1 ? ",FAnalysisCode1" : "") +
                (analy2 ? ",FAnalysisCode2" : "") +
                (analy3 ? ",FAnalysisCode3" : "") +
                " from " + pub.yssGetTableName("tb_stock_security") +
                " where FCheckState = 1 and FPortCode in (" + portCodes + ")" +
                /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
                * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//               	(statCodes.length()>0?" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")":"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
                (statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")" :"")+
                /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
                //-------------------------------------------------------------------------------------sj edit 080117
                //会出现有应收利息的库存但没有证券库存的情况
                //" and (FStorageAmount<>0 or FStorageCost<>0 or FMStorageCost<>0 or " +
                //" FVStorageCost<>0 or FPortCuryCost<>0 or FMPortCuryCost<>0 or FVPortCuryCost <> 0 " +
                //" or FBaseCuryCost<>0 or FMBaseCuryCost<>0 or FVBaseCuryCost<>0)" +
                " and " + operSql.sqlStoragEve(dDate) + ") a " +
                //----------------------------------------------
                " left join (select FSecurityCode, FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
               	" where FCheckState = 1 "+
               	/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               	* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//               	(statCodes.length()>0?" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")":"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
               	(statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")" :"") +
               	/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               	") b on a.FSecurityCode = b.FSecurityCode" +
                //-------------------------------------------------------------------------------------//sj edit 080117 增加对有无应收利息库存的判断
                //若有则保留。
                " left join (select FPortCode,FBal,FPortCuryBal,FBaseCuryBal,FCatType,FAttrClsCode,FSecurityCode,FInvestType" + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                (analy1 ? ",FAnalysisCode1" : "") +
                (analy2 ? ",FAnalysisCode2" : "") +
                (analy3 ? ",FAnalysisCode3" : "") +
                " from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " where FCheckState = 1 and" +
                " FPortCode in (" + portCodes + ") and " +
                /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
                * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//	            (statCodes.length()>0?" FSecurityCode in("+operSql.sqlCodes(statCodes)+") and ":"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
                (statCodes.length()>0? "(" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ") and ":"")+
                /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
                operSql.sqlStoragEve(dDate) +
                " and FSubTsfTypeCode in( '06FI'," +dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BSC)+",'60BI01'"+//借入成本和借入估增 edited by zhouxiang 2010.12.11
                ")) c on a.FSecurityCode = c.FSecurityCode" +
                " and a.FPortCode = c.FPortCode and a.FCatType = c.FCatType and a.FAttrClsCode = c.FAttrClsCode and a.FInvestType = c.FInvestType" + // 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                (analy1 ? " and a.FAnalysisCode1 = c.FAnalysisCode1" : "") +
                (analy2 ? " and a.FAnalysisCode2 = c.FAnalysisCode2" : "") +
                (analy3 ? " and a.FAnalysisCode3 = c.FAnalysisCode3" : "") +
                " where c.FBal <> 0 or c.FPortCuryBal<>0 or c.FBaseCuryBal <> 0" +
                //-----------添加了证券库存的数量金额判断以获取在证券或证券应收应付库存中有数量和金额的记录 sj add 20080121--//
                " or a.FStorageAmount<>0 or a.FStorageCost<>0 or a.FMStorageCost<>0 or " +
                " a.FVStorageCost<>0 or a.FPortCuryCost<>0 or a.FMPortCuryCost<>0 or a.FVPortCuryCost <> 0 " +
                " or a.FBaseCuryCost<>0 or a.FMBaseCuryCost<>0 or a.FVBaseCuryCost<>0";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //-------------by 曹丞 2009.01.22 检查有库存证券对应的币种代码是否正确 MS00004 QDV4.1-2009.2.1_09A-----------//
                if (rs.getString("FCuryCode") == null || rs.getString("FCuryCode").trim().length() == 0) {
                    throw new YssException("系统进行证券库存统计,在获取昨日库存信息时检查到代码为【" +
                                           rs.getString("FSecurityCode") + "】证券对应的币种信息不存在!" + "\n" +
                                           "请核查以下信息：" + "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                           "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                }
                //--------------------------------------------------------------------------------------------//
                secstorage = new SecurityStorageBean();

                //添加属性分类和投资类型的处理 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                    rs.getString("FAttrClsCode") + "\f" +   //属性分类
                    rs.getString("FInvestType");            //投资类型
                secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                    "yyyy-MM-dd"));
                secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                secstorage.setStrPortCode(rs.getString("FPortCode"));
                secstorage.setStrCuryCode(rs.getString("FCuryCode"));
                secstorage.setStrStorageAmount(rs.getDouble("FStorageAmount") + "");
                secstorage.setStrStorageCost(rs.getDouble("FStorageCost") + "");
                secstorage.setStrMStorageCost(rs.getDouble("FMStorageCost") + "");
                secstorage.setStrVStorageCost(rs.getDouble("FVStorageCost") + "");

                secstorage.setStrPortCuryCost(rs.getDouble("FPortCuryCost") + "");
                secstorage.setStrMPortCuryCost(rs.getDouble("FMPortCuryCost") + "");
                secstorage.setStrVPortCuryCost(rs.getDouble("FVPortCuryCost") + "");

                secstorage.setStrBaseCuryCost(rs.getDouble("FBaseCuryCost") + "");
                secstorage.setStrMBaseCuryCost(rs.getDouble("FMBaseCuryCost") + "");
                secstorage.setStrVBaseCuryCost(rs.getDouble("FVBaseCuryCost") + "");

                secstorage.setBailMoney(rs.getDouble("FBailMoney"));

                secstorage.setStrBaseCuryRate(rs.getDouble("FBaseYesRate") + "");
                secstorage.setStrPortCuryRate(rs.getDouble("FPortYesRate") + "");
                if (analy1) {
                    secstorage.setStrFAnalysisCode1(rs.getString("FAnalysisCode1"));
                } else {
                    secstorage.setStrFAnalysisCode1(" ");
                }
                if (analy2) {
                    secstorage.setStrFAnalysisCode2(rs.getString("FAnalysisCode2"));
                } else {
                    secstorage.setStrFAnalysisCode2(" ");
                }
                if (analy3) {
                    secstorage.setStrFAnalysisCode3(rs.getString("FAnalysisCode3"));
                } else {
                    secstorage.setStrFAnalysisCode3(" ");
                }
                secstorage.setAttrCode(rs.getString("FAttrClsCode"));
                secstorage.setInvestType(rs.getString("FInvestType"));          //投资类型
                secstorage.setCatType(" ");
                secstorage.setEffectiveRate(rs.getDouble("FEffectiveRate"));    //实际利率
                hmEveStg.put(sKey, secstorage);
            }
            return hmEveStg;
        } catch (Exception e) {
            throw new YssException("在获取昨日库存信息时出错!" + "\n", e); //列出导致获取昨日证券库存信息原因 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    
    /**
     * modify by fangjiang 2011.07.18 STORY #1353    
     */
    public void getStgForwardStatData(java.util.Date dDate,
                                      HashMap hmEveStg,
                                      boolean bAnaly1, boolean bAnaly2,
                                      boolean bAnaly3) throws YssException {
        String strSql = "";
        ResultSet rs = null;

        SecurityStorageBean secstorage = null;

        String sKey = "";

        double dBaseRate = 1;
        double dPortRate = 1;
        
        //add by fangjiang 20101.01.12 STORY #262 #393
        /*String offNum = "";   
		String offLimit = ""; 
		String offNum1 = "";   
		String offLimit1 = ""; 
		String tradeNum = "";
		String settleLimit = "";*/
		//-----------------
        try {
        	//modify by fangjiang 20101.01.12 STORY #262 #393  
        	
        	/*strSql = " select FOffNum from " + pub.yssGetTableName("Tb_Data_ForwardTrade") + 
        	" where FCheckState = 1 and FTradeType = '21' and FPortCode in (" +
            portCodes + ") and FTradeDate = " +dbl.sqlDate(dDate);
	        rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				offNum += operSql.sqlCodes(rs.getString("FOffNum")) + ",";
			}
			if(offNum.equals("")){
				offNum = "' '";
			}else{
				offNum = offNum.substring(0,offNum.length()-1);
			}
			offLimit = " where FNum in (" + offNum + ")";  
			dbl.closeResultSetFinal(rs);*/
			
			/*strSql = " select FOffNum from " + pub.yssGetTableName("Tb_Data_ForwardTrade") + 
        	" where FCheckState = 1 and FTradeType = '21' and FPortCode in (" +
            portCodes + ") and FTradeDate <= " +dbl.sqlDate(dDate);
	        rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				offNum1 += operSql.sqlCodes(rs.getString("FOffNum")) + ",";
			}
			if(offNum1.equals("")){
				offNum1 = "' '";
			}else{
				offNum1 = offNum1.substring(0,offNum1.length()-1);
			}
			offLimit1 = " and FNum not in (" + offNum1 + ")";  
			dbl.closeResultSetFinal(rs);*/
			
			/*strSql = " select a.FTradeNum as FTradeNum from (select * from " + pub.yssGetTableName("Tb_Data_FwTradeSettle") +
			" where FCheckState = 1 and FSettleDate < " +dbl.sqlDate(dDate) +
			" ) a join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
			" where FCheckState = 1 and FTradeType = '20') b on a.FTradeNum = b.Fnum " +
			" where b.FPortCode in (" +
            portCodes + ")";
	        rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				tradeNum += operSql.sqlCodes(rs.getString("FTradeNum")) + ",";
			}
			if(tradeNum.equals("")){
				tradeNum = "' '";
			}else{
				tradeNum = tradeNum.substring(0,tradeNum.length()-1);
			}
			settleLimit = " and FNum not in (" + tradeNum + ")"; 
			dbl.closeResultSetFinal(rs);*/	
        	      	
            strSql =
                "select  a.*, b.FPortCury,c.FTradeCury,d.FSaleCury,d.FBuyCury " +
                " from (Select a.FSecurityCode,FPortCode " +
                (bAnaly1 ? ", a.FAnalysisCode1 as FAnalysisCode1" : " ") +
                (bAnaly2 ? ", a.FAnalysisCode2 as FAnalysisCode2" : " ") +
                " ,sum(" +
                dbl.sqlIsNull("FTradeAmount*FTrustPrice", "0") +
                ") as FTradeAmount, sum(FBailMoney) as FBailMoney" +
                " ,Sum(" +
                //begin by zhouxiang 20100825 MS01522 结算金额替换成到期金额 增加生成远期外汇交易的费用资金调拨和到期处理的资金调拨   
                dbl.sqlIsNull("FMatureMoney*FTrustPrice", "0") +
                ") as FMatureMoney from " +
                
                " (select FSecurityCode,FPortCode,FTradeAmount,FMatureMoney,FBailMoney, 1 as FTrustPrice " +
                //---------------------------------end------------------------------------------------------------------------
                (bAnaly1 ? ",FAnalysisCode1" : " ") +
                (bAnaly2 ? ",FAnalysisCode2" : " ") +
                " from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                " where FTradeDate = " + dbl.sqlDate(dDate) + 
                " and fcheckstate = 1 and FTradeType = '20' and FPortCode in (" +
                portCodes +
                
                ") union select b.fsecuritycode as fsecuritycode, b.fportcode as fportcode," +
                "(case when FTradeCury = FBuyCury then FSCapMoney else FBCapMoney end) as FTradeAmount," +
                "(case when FTradeCury = FBuyCury then FBCapMoney else FSCapMoney end) as FMatureMoney," +
                "b.FBailMoney as FBailMoney, -1 as FTrustPrice" +
                (bAnaly1 ? ",FAnalysisCode1" : " ") +
                (bAnaly2 ? ",FAnalysisCode2" : " ") +
                " from (select * from " + pub.yssGetTableName("Tb_Data_FwTradeSettle") +
                " where FCheckState = 1) a join " +
                " (select FSecurityCode, FPortCode, FNum, FBailMoney, FAnalysisCode1, FAnalysisCode2 from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                " where FCheckState = 1 and FMatureDate > " + dbl.sqlDate(dDate) +
                " and FPortCode in (" +
                portCodes + " )) b on a.FTradeNum = b.FNum " +
                " join (select FSecurityCode, FBuyCury, FSaleCury from " + pub.yssGetTableName("Tb_Para_Forward") +
                " where FCheckState = 1) c on b.FSECURITYCODE = c.FSecurityCode " +
                " join (select FSecurityCode, FTradeCury from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) d on b.FSECURITYCODE = d.FSecurityCode " +
                " where a.FSettleDate = " + dbl.sqlDate(dDate) +
                
                " union select a.FSecurityCode as FSecurityCode, a.FPortCode as FPortCode," +
                " b.FAmount as FTradeAmount, b.FCost as FMatureMoney, a.FBailMoney as FBailMoney, -1 as FTrustPrice " +
                (bAnaly1 ? ",FAnalysisCode1" : " ") +
                (bAnaly2 ? ",FAnalysisCode2" : " ") +
                " from (select * from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                " where fcheckstate = 1 and FTradeType = '20' and FPortCode in (" +
                portCodes +
                ")) a join (select FOffNum, FAmount, FCost from " + pub.yssGetTableName("Tb_Data_ForwardTrade") + 
                " where fcheckstate = 1 and FTradeType = '21' and FTradeDate = " + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes +
                ")) b on a.FNum = b.FOffNum" +                
                       
                " union select a.FSecurityCode as FSecurityCode, a.FPortCode as FPortCode, a.FTradeAmount-nvl(b.FTradeAmount,0)-nvl(c.FTradeAmount,0) as FTradeAmount, " +
                " a.FMatureMoney-nvl(b.FMatureMoney,0)-nvl(c.FMatureMoney,0) as FMatureMoney, a.FBailMoney as FBailMoney, -1 as FTrustPrice " +
                (bAnaly1 ? ",FAnalysisCode1" : " ") +
                (bAnaly2 ? ",FAnalysisCode2" : " ") +
                " from " +
                " (select * from "
                + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                " where FMatureDate = " +
                dbl.sqlDate(dDate) + " and fcheckstate = 1 and FTradeType = '20' " + 
                " and FPortCode in (" + portCodes + ")" +
                " ) a left join (select FOffNum, sum(FAmount) as FTradeAmount, sum(FCost) as FMatureMoney from " +
                pub.yssGetTableName("Tb_Data_ForwardTrade") + " where fcheckstate = 1 and FTradeType = '21' and FTradeDate < " + dbl.sqlDate(dDate) +
                " group by FOffNum) b " +
                " on a.FNum = b.FOffNum " +
                " left join (select FTradeNum, " +
                " (case when FTradeCury = FBuyCury then FSCapMoney else FBCapMoney end) as FTradeAmount," +
                " (case when FTradeCury = FBuyCury then FBCapMoney else FSCapMoney end) as FMatureMoney " +
                " from (select FTradeNum, sum(FBCapMoney) as FBCapMoney, sum(FSCapMoney) as FSCapMoney from " + pub.yssGetTableName("Tb_Data_FwTradeSettle") +
                " where FCheckState = 1 and FSettleDate < " + dbl.sqlDate(dDate) +
                " group by FTradeNum) a join " +
                " (select FNum, FSecurityCode from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                " where FCheckState = 1 and FTradeType = '20' " +
                " ) b on a.FTradeNum = b.FNum " +
                " join (select FSecurityCode, FBuyCury, FSaleCury from " + pub.yssGetTableName("Tb_Para_Forward") +
                " where FCheckState = 1) c on b.FSECURITYCODE = c.FSecurityCode " +
                " join (select FSecurityCode, FTradeCury from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) d on b.FSECURITYCODE = d.FSecurityCode " +
                " ) c on a.FNum = c.FTradeNum" +
                
                " ) a " +
                " group by a.FSecurityCode, a.FPortCode " +
                (bAnaly1 ? ", a.FAnalysisCode1" : " ") +
                (bAnaly2 ? ", a.FAnalysisCode2" : " ") +
                ") a left join" +

                " (select FPortCode,FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) b on a.FPortCode = b.FPortCode" +

                " left join (select FSecurityCode,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode" +

                " left join (select FSecurityCode,FSaleCury,FBuyCury from " +
                pub.yssGetTableName("Tb_Para_Forward") +
                " where FCheckState = 1) d on a.FSecurityCode = d.FSecurityCode";
            //------------end STORY #262 #393----------------
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //-----------------------------增加币种信息有效性检查 by 曹丞 2009.01.20 MS00004 QDV4.1-2009.2.1_09A--------//
                if (rs.getString("FTradeCury") == null || rs.getString("FTradeCury").trim().length() == 0) {
                    throw new YssException("系统进行证券库存统计,在远期库存统计时检查到代码【" +
                                           rs.getString("FSecurityCode") +
                                           "】对应的币种信息不存在!" + "\n" + "请核查以下信息:" +
                                           "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                           "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                }
                //---------------------------------------------------------------------------------//
                dBaseRate = 1;
                dPortRate = 1;

                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FPortCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);
                //-----------------------------增加获汇率有效性检查 by 曹丞 2009.01.20 MS00004 QDV4.1-2009.2.1_09A--------------//
                if (dBaseRate == 0 || dPortRate == 0) {
                    throw new YssException("系统进行证券库存统计,在远期库存统计时检查到代码【" +
                                           rs.getString("FSecurityCode") +
                                           "】对应的汇率信息不存在!" + "\n" + "请核查以下信息:" +
                                           "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                           "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");
                }
                //---------------------------------------------------------------------------------//
                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (bAnaly2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                    " " + "\f" + "C";//------ modify wangzuochun 2010.05.24 MS01157    “投资类型”字段无法正常赋予默认值’C’导致库存统计报错    QDV4国内（测试）2010年05月05日04_B    

                if (hmEveStg.containsKey(sKey)) {
                    secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");

                    secstorage.setStrStorageAmount(YssD.add(YssFun.toDouble(
                        secstorage.getStrStorageAmount()),
                        rs.getDouble("FTradeAmount")) + "");
                    //begin by zhouxiang 20100825 MS01522 结算金额替换成到期金额 增加生成远期外汇交易的费用资金调拨和到期处理的资金调拨   
                    secstorage.setStrStorageCost(YssD.add(rs.getDouble(
                        "FMatureMoney"),
                        YssFun.toDouble(secstorage.getStrStorageCost())) + "");
                    secstorage.setStrMStorageCost(YssD.add(rs.getDouble(
                        "FMatureMoney"),
                        YssFun.toDouble(secstorage.getStrMStorageCost())) + "");
                    secstorage.setStrVStorageCost(YssD.add(rs.getDouble(
                        "FMatureMoney"),
                     //end  by zhouxiang 20100825 MS01522 结算金额替换成到期金额 增加生成远期外汇交易的费用资金调拨和到期处理的资金调拨   
                        YssFun.toDouble(secstorage.getStrVStorageCost())) + "");

                    secstorage.setStrBaseCuryCost(this.getSettingOper().
                                                  calBaseMoney(YssFun.toDouble(
                        secstorage.getStrStorageCost()), dBaseRate) + "");
                    secstorage.setStrMBaseCuryCost(this.getSettingOper().
                        calBaseMoney(YssFun.toDouble(
                            secstorage.getStrMStorageCost()), dBaseRate) + "");
                    secstorage.setStrVBaseCuryCost(this.getSettingOper().
                        calBaseMoney(YssFun.toDouble(
                            secstorage.getStrVStorageCost()), dBaseRate) + "");

                    secstorage.setStrPortCuryCost(this.getSettingOper().
                                                  calPortMoney(YssFun.toDouble(
                        secstorage.getStrStorageCost()), dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FTradeCury"), dDate, rs.getString("FPortCode")) +
                                                  "");
                    secstorage.setStrMPortCuryCost(this.getSettingOper().
                        calPortMoney(YssFun.toDouble(
                            secstorage.getStrMStorageCost()), dBaseRate, dPortRate,
                                     //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                     rs.getString("FTradeCury"), dDate, rs.getString("FPortCode")) +
                        "");
                    secstorage.setStrVPortCuryCost(this.getSettingOper().
                        calPortMoney(YssFun.toDouble(
                            secstorage.getStrVStorageCost()), dBaseRate, dPortRate,
                                     //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                     rs.getString("FTradeCury"), dDate, rs.getString("FPortCode")) +
                        "");

                    secstorage.setBailMoney(rs.getDouble("FBailMoney"));

                } else {
                    secstorage = new SecurityStorageBean();
                    secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
//               secstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
                    secstorage.setStrPortCode(rs.getString("FPortCode"));
                    secstorage.setStrCuryCode(rs.getString("FTradeCury"));
                    if (bAnaly1) {
                        secstorage.setStrFAnalysisCode1(rs.getString(
                            "FAnalysisCode1"));
                    } else {
                        secstorage.setStrFAnalysisCode1(" ");
                    }
                    if (bAnaly2) {
                        secstorage.setStrFAnalysisCode2(rs.getString(
                            "FAnalysisCode2"));
                    } else {
                        secstorage.setStrFAnalysisCode2(" ");
                    }
                    if (bAnaly3) {
                        secstorage.setStrFAnalysisCode3(rs.getString(
                            "FAnalysisCode3"));
                    } else {
                        secstorage.setStrFAnalysisCode3(" ");
                    }

                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");

                    secstorage.setStrStorageAmount(rs.getDouble("FTradeAmount") + "");
                    //edited by zhouxiang MS01522 结算金额替换成到期金额 增加生成远期外汇交易的费用资金调拨和到期处理的资金调拨   
                    secstorage.setStrStorageCost(rs.getDouble("FMatureMoney") +
                                                 "");
                    secstorage.setStrMStorageCost(rs.getDouble("FMatureMoney") +
                                                  "");
                    secstorage.setStrVStorageCost(rs.getDouble("FMatureMoney") +
                                                  "");
                    //end-- by zhouxiang MS01522 结算金额替换成到期金额 增加生成远期外汇交易的费用资金调拨和到期处理的资金调拨   
                    secstorage.setStrBaseCuryCost(this.getSettingOper().
                                                  calBaseMoney(YssFun.toDouble(
                        secstorage.getStrStorageCost()), dBaseRate) + "");
                    secstorage.setStrMBaseCuryCost(this.getSettingOper().
                        calBaseMoney(YssFun.toDouble(
                            secstorage.getStrMStorageCost()), dBaseRate) + "");
                    secstorage.setStrVBaseCuryCost(this.getSettingOper().
                        calBaseMoney(YssFun.toDouble(
                            secstorage.getStrVStorageCost()), dBaseRate) + "");

                    secstorage.setStrPortCuryCost(this.getSettingOper().
                                                  calPortMoney(YssFun.toDouble(
                        secstorage.getStrStorageCost()), dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FTradeCury"), dDate, rs.getString("FPortCode")) +
                                                  "");
                    secstorage.setStrMPortCuryCost(this.getSettingOper().
                        calPortMoney(YssFun.toDouble(
                            secstorage.getStrMStorageCost()), dBaseRate, dPortRate,
                                     //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                     rs.getString("FTradeCury"), dDate, rs.getString("FPortCode")) +
                        "");
                    secstorage.setStrVPortCuryCost(this.getSettingOper().
                        calPortMoney(YssFun.toDouble(
                            secstorage.getStrVStorageCost()), dBaseRate, dPortRate,
                                     //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                     rs.getString("FTradeCury"), dDate, rs.getString("FPortCode")) +
                        "");

                    secstorage.setBailMoney(rs.getDouble("FBailMoney"));

                    hmEveStg.put(sKey, secstorage);
                }
            }

        } catch (Exception e) {
            //  if (dBaseRate == 0 ||dPortRate == 0)
            throw new YssException("在统计远期库存时出现异常!" + "\n", e); //统计远期库存信息异常 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * 重载方法，普通库存统计时处理方法 xuqiji 20091207 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @param dDate 业务日期
     * @param hmEveStg 保存数据的hash表
     * @param bAnaly1 分析代码1
     * @param bAnaly2 分析代码2
     * @param bAnaly3 分析代码3
     * @return
     * @throws YssException
     */
    public Hashtable getStgTradeStatData(java.util.Date dDate, HashMap hmEveStg,
            boolean bAnaly1, boolean bAnaly2,
            boolean bAnaly3) throws YssException {
    	return getStgTradeStatData(dDate,  hmEveStg,
                 bAnaly1,  bAnaly2,
                 bAnaly3, this.portCodes);
    }
    public Hashtable getExRightsData(java.util.Date dDate, HashMap hmEveStg, 
    		          boolean bAnaly1, boolean bAnaly2,boolean bAnaly3,String sPortCode) throws YssException {
    	 ResultSet rs = null;
         String strSql = "";
         SecurityStorageBean secstorage = null;

         String sKey = "";

         double dBaseRate = 1;
         double dPortRate = 1;
         Hashtable amzFi = null;
         
         try{
        	 amzFi = new Hashtable();
        	 
        	 //--- Tb_Data_SubTrade获取业务数据,Tb_Para_Portfolio 获取组合货币，Tb_Para_Security获取交易货币 ----
        	 strSql ="select  a.*, b.FPortCury,c.FTradeCury from " +
        	 //-----------------------------------------------------------------------------------------------//
             " (select FSecurityCode,FPortCode,FInvestType" +
             (bAnaly1 ? ", FInvMgrCode" : " ") +(bAnaly2 ? ", FBrokerCode" : " ") +
             ",FAttrClsCode ," + 
             dbl.sqlIsNull("FBailMoney* -FCashInd", "0") +" as FBailMoney," +
             dbl.sqlIsNull("FTradeAmount*FAmountInd", "0") +" as FTradeAmount, " +
             dbl.sqlIsNull("FTradeMoney* -FCashInd", "0") +"as FTradeMoney, " +
             //成本的方向和数量的方向一样，所以这里乘的是数量方向
             dbl.sqlIsNull("FCost* -FCashInd") +"as FCost, " +
             dbl.sqlIsNull("FMCost* -FCashInd") +"as FMCost," +
             dbl.sqlIsNull("FVCost* -FCashInd") +"as FVCost, " +
             dbl.sqlIsNull("FBaseCuryCost* -FCashInd") +" as FBaseCuryCost, " +
             dbl.sqlIsNull("FMBaseCuryCost* -FCashInd") +" as FMBaseCuryCost, " +
             dbl.sqlIsNull("FVBaseCuryCost* -FCashInd") +" as FVBaseCuryCost, " +
             dbl.sqlIsNull("FPortCuryCost* -FCashInd") +" as FPortCuryCost, " +
             dbl.sqlIsNull("FMPortCuryCost* -FCashInd") +" as FMPortCuryCost, " +
             dbl.sqlIsNull("FVPortCuryCost* -FCashInd") +" as FVPortCuryCost, " +
             dbl.sqlIsNull("FTotalCost* -FCashInd", "0") +" as FTotalCost,'Natural' as FTradeState from " +
             " (select aa1.*,aa2.FCashInd,aa2.FAmountInd from " +
        	 " (select a1.FExrightDate,a2.* from "+
        	 " (select FExrightDate,Fsecuritycode from "+pub.yssGetTableName("tb_data_rightsissue")+" where " + 
        	 //edit by songjie 2012.08.17 STORY # 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A 
        	 //获取配股权益设置中 业务日期在 登记日 和缴款日之间 且 交易方式为  先缴款后除权、已审核 的数据
        	 dbl.sqlDate(dDate)+" between FRecordDate and FExpirationDate and FTradeCode = '0' and FCheckState = 1) a1"+
        	  " left join (select * from "+pub.yssGetTableName("tb_data_subtrade") +" where Fportcode in("+sPortCode+") and FTradeTypeCode ="+
        	  //edit by songjie 2012.08.17 STORY #2538 QDV4赢时胜(上海开发部)2012年04月21日01_A 获取已审核的 交易日期为业务日期的数据
        	  dbl.sqlString(YssOperCons.YSS_JYLX_PGJK)+" and FBargainDate ="+dbl.sqlDate(dDate)+" and FCheckState = 1 ) a2 on a1.Fsecuritycode = a2.Fsecuritycode )aa1"+
        	  " join (select * from Tb_Base_TradeType where FCheckState = 1) aa2 on aa1.FTradeTypeCode = aa2.FTradeTypeCode where " +
        	  " aa1.fcheckstate = 1 and aa1.FPortCode in ("+sPortCode+") and aa1.FTradeTypeCode ="+dbl.sqlString(YssOperCons.YSS_JYLX_PGJK)+") )a"+
                //--------------------------------------------------//
        	  
                " left join (select FPortCode,FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) b on a.FPortCode = b.FPortCode" +
                //--------------------------------------------------
                //edit by songjie 2012.08.17 STORY #2538 QDV4赢时胜(上海开发部)2012年04月21日01_A left join  改为  join
                " join (select FSecurityCode,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
                //edit by songjie 2012.08.17 STORY #2538 QDV4赢时胜(上海开发部)2012年04月21日01_A 
                //添加 交易所代码为 上交所、深交所 的判断条件
                " where FCheckState = 1 and FExchangeCode in('CG','CS')) c on a.FSecurityCode = c.FSecurityCode";
             //------------------------------------------------------
        	 
        	 rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
             while (rs.next()) {
                 if (rs.getString("FAttrClsCode") != null && rs.getString("FAttrClsCode").equalsIgnoreCase("19")) {
                     amzFi.put(rs.getString("FSecurityCode"),
                               rs.getString("FSecurityCode"));
                 }
                 dBaseRate = 1;
                 dPortRate = 1;
                 //-----------------------检查库存证券对应的币种代码是否正确 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A------//
                 if (rs.getString("FTradeCury") == null || rs.getString("FTradeCury").trim().length() == 0) { //获取交易信息异常
                     throw new YssException("系统进行证券库存统计,在获取交易信息时检查到代码为【" +
                                            rs.getString("FSecurityCode") +
                                            "】证券对应的交易币种信息不存在!"
                                            + "\n" + "请核查以下信息:" +
                                            "\n" + "1.【证券品种信息】中该证券信息是否存在且已审核!"
                                            + "\n" + "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                 }
                 //-------------------------------------------------------------------------------------------------------//

                 dBaseRate = this.getSettingOper().getCuryRate(dDate,
                     rs.getString("FTradeCury"),
                     rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                 dPortRate = this.getSettingOper().getCuryRate(dDate,
                     rs.getString("FPortCury"),
                     rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);

                 if (dBaseRate == 0 || dPortRate == 0) {
                     throw new YssException("系统进行证券库存统计,在获取交易信息时检查到代码为【" +
                                            rs.getString("FSecurityCode") +
                                            "】证券对应的汇率信息不存在!"
                                            + "\n" + "请核查以下信息:" +
                                            "\n" + "1.【证券品种信息】中该证券信息是否存在且已审核!"
                                            + "\n" + "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");

                 }

                 sKey = rs.getString("FSecurityCode") + "\f" +
                     rs.getString("FPortCode") +
                     (bAnaly1 ? "\f" + rs.getString("FInvMgrCode") : "") +
                     (bAnaly2 ? "\f" + rs.getString("FBrokerCode") : "") +
                     (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                     (rs.getString("FAttrClsCode") == null ||
                      rs.getString("FAttrClsCode").length() == 0 ? " " :
                      rs.getString("FAttrClsCode")) + "\f" + // 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                      rs.getString("FInvestType"); //获取投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                 if (hmEveStg.containsKey(sKey)) {
                     secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                     secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                         "yyyy-MM-dd"));
                     secstorage.setStrBaseCuryRate(dBaseRate + "");
                     secstorage.setStrPortCuryRate(dPortRate + "");
                     secstorage.setInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                     secstorage.setStrStorageAmount(YssD.add(YssFun.toDouble(
                         secstorage.getStrStorageAmount()),
                         rs.getDouble("FTradeAmount")) + "");
                     secstorage.setStrStorageCost(YssD.add(YssFun.toDouble(secstorage.
                         getStrStorageCost()),
                         rs.getDouble("FCost")) + "");
                     secstorage.setStrMStorageCost(YssD.add(YssFun.toDouble(
                         secstorage.getStrMStorageCost()),
                         rs.getDouble("FMCost")) + "");
                     secstorage.setStrVStorageCost(YssD.add(YssFun.toDouble(
                         secstorage.getStrVStorageCost()),
                         rs.getDouble("FVCost")) + "");

                     secstorage.setStrBaseCuryCost(YssD.add(YssFun.toDouble(
                         secstorage.getStrBaseCuryCost()),
                         rs.getDouble("FBaseCuryCost")) + "");
                     secstorage.setStrMBaseCuryCost(YssD.add(YssFun.toDouble(
                         secstorage.getStrMBaseCuryCost()),
                         rs.getDouble("FMBaseCuryCost")) + "");
                     secstorage.setStrVBaseCuryCost(YssD.add(YssFun.toDouble(
                         secstorage.getStrVBaseCuryCost()),
                         rs.getDouble("FVBaseCuryCost")) + "");

                     secstorage.setStrPortCuryCost(YssD.add(YssFun.toDouble(
                         secstorage.getStrPortCuryCost()),
                         rs.getDouble("FPortCuryCost")) + "");
                     secstorage.setStrMPortCuryCost(YssD.add(YssFun.toDouble(
                         secstorage.getStrMPortCuryCost()),
                         rs.getDouble("FMPortCuryCost")) + "");
                     secstorage.setStrVPortCuryCost(YssD.add(YssFun.toDouble(
                         secstorage.getStrVPortCuryCost()),
                         rs.getDouble("FVPortCuryCost")) + "");

                     secstorage.setBailMoney(YssD.add(secstorage.getBailMoney(),
                         rs.getDouble("FBailMoney")));
                     //add by songjie 2012.08.20 STORY #2538 QDV4赢时胜(上海开发部)2012年04月21日01_A
                     hmEveStg.put(sKey, secstorage);
                 } else {
                     secstorage = new SecurityStorageBean();
                     secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                     secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                         "yyyy-MM-dd"));
//                secstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
                     secstorage.setStrPortCode(rs.getString("FPortCode"));
                     secstorage.setStrCuryCode(rs.getString("FTradeCury"));
                     if (bAnaly1) {
                         secstorage.setStrFAnalysisCode1(rs.getString(
                             "FInvMgrCode"));
                     } else {
                         secstorage.setStrFAnalysisCode1(" ");
                     }
                     if (bAnaly2) {
                         secstorage.setStrFAnalysisCode2(rs.getString(
                             "FBrokerCode"));
                     } else {
                         secstorage.setStrFAnalysisCode2(" ");
                     }
                     if (bAnaly3) {
                         secstorage.setStrFAnalysisCode3(rs.getString(
                             "FAnalysisCode3"));
                     } else {
                         secstorage.setStrFAnalysisCode3(" ");
                     }
                     secstorage.setAttrCode(rs.getString("FAttrClsCode"));
                     secstorage.setInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                     secstorage.setStrBaseCuryRate(dBaseRate + "");
                     secstorage.setStrPortCuryRate(dPortRate + "");

                     secstorage.setStrStorageAmount(rs.getDouble("FTradeAmount") + "");
                     secstorage.setStrStorageCost(rs.getDouble("FCost") + "");
                     secstorage.setStrMStorageCost(rs.getDouble("FMCost") + "");
                     secstorage.setStrVStorageCost(rs.getDouble("FVCost") + "");

                     secstorage.setStrBaseCuryCost(rs.getDouble("FBaseCuryCost") + "");
                     secstorage.setStrMBaseCuryCost(rs.getDouble("FMBaseCuryCost") +
                         "");
                     secstorage.setStrVBaseCuryCost(rs.getDouble("FVBaseCuryCost") +
                         "");

                     secstorage.setStrPortCuryCost(rs.getDouble("FPortCuryCost") + "");
                     secstorage.setStrMPortCuryCost(rs.getDouble("FMPortCuryCost") +
                         "");
                     secstorage.setStrVPortCuryCost(rs.getDouble("FVPortCuryCost") +
                         "");

                     secstorage.setBailMoney(rs.getDouble("FBailMoney"));

                     hmEveStg.put(sKey, secstorage);
                 }
             }
         }catch(Exception e){
        	 throw new YssException("在统计配股缴款业务信息时出现异常!" + "\n", e); 
         }finally{
        	 dbl.closeResultSetFinal(rs);
         }
         return amzFi;
    }
   
    
   /**
    * 
    * @方法名：getStgTradeSecLendStatData
    * @参数：
    * @返回类型：Hashtable
    * @说明：证券借贷交易数据库存统计 add by zhangfa 20101116 证券借贷业务-库存统计  edited by zhouxiang 2010.12.7
    */
    public Hashtable getStgTradeSecLendStatData(java.util.Date dDate, HashMap hmEveStg,
            boolean bAnaly1, boolean bAnaly2,
            boolean bAnaly3,String sPortCode) throws YssException{
    	 ResultSet rs = null;
         String strSql = "";
         SecurityStorageBean secstorage = null;
         String sKey = "";
         double dBaseRate = 1;
         double dPortRate = 1;
    	Hashtable secLend=null;
    	
    	try{
    		secLend=new Hashtable();
    		/*“借入”数据	borrow	        借入送股BInPaySec	          借入归还	Rcb		借入归还送股	Rbsb	借入应付配股权证	BInPayOP	借入归还配股权证	Awrb	
			借出应收送股 BOutRecSec  借出召回送股	Mhlr	借出应收配股权证	BOutRecOP	借出召回配股权证	Lpwr*/			
    		//--edited by zhouxiang 2010.12.7
			/*String sqlTemp="(case when (m.FSecurityCode is not null and FTradeTypeCode = 'borrow') then 0 else FCashInd end)";*/
    		String sqlTemp="FCashInd";
    		strSql = " select a.*, b.FPortCury, c.FTradeCury  from (select a.FSecurityCode, a.FPortCode,a.FInvMgrCode, a.FBrokerCode,FAttrClsCode, "
					+ "  Sum(NVL(a.FTradeAmount * FAmountInd, 0)) as FTradeAmount,sum(NVL(a.FCost * "
					+sqlTemp+", 0)) as FCost,sum(NVL(a.FMCost * "+sqlTemp+", 0)) as FMCost,"
					+ " sum(NVL(a.FVCost * "+sqlTemp+", 0)) as FVCost,sum(NVL(a.FBaseCuryCost * "+sqlTemp
					+", 0)) as FBaseCuryCost,sum(NVL(a.FMBaseCuryCost * "+sqlTemp+", 0)) as FMBaseCuryCost,"
					+ " sum(NVL(a.FVBaseCuryCost * "+sqlTemp+", 0)) as FVBaseCuryCost,sum(NVL(a.FPortCuryCost * "+sqlTemp+", 0)) as FPortCuryCost,"
					+ " sum(NVL(a.FMPortCuryCost * "+sqlTemp+", 0)) as FMPortCuryCost, sum(NVL(a.FVPortCuryCost * "+sqlTemp+", 0)) as FVPortCuryCost,"
					+ " sum(NVL(a.FTotalCost * "+sqlTemp+", 0)) as FTotalCost, 'Natural' as FTradeState,'C' as FInvestType from (select a1.*, a2.FCashInd, a2.FAmountInd, sec.fcatcode"
					+ " from "
					+ pub.yssGetTableName("tb_DATA_SecLendTRADE")
					+ " a1 "
					+ "  join (select * from Tb_Base_TradeType where FCheckState = 1) a2 "
					+ " on a1.FTradeTypeCode =a2.FTradeTypeCode  join (select FSecurityCode, FCatCode from "
					+ pub.yssGetTableName("tb_para_security")
					+ " where FCheckState = 1) sec on a1.FSecurityCode = sec.FSecurityCode where a1.FBargainDate =  "+dbl.sqlDate(dDate)
					+ " and a1.FTradeTypeCode in ("
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)
				/*	+ ", "
					+ dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BInPaySec)*/
					+ ", "
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rcb)
					+ ", "
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rbsb)
					/*+ ","
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX__BInPayOP)*/
					+ ", "
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Awrb)
					/*+ ", "
					+ dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BOutRecSec)*/
				/*	+ ", "
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Mhlr)*/ //借出送股召回，借出召回送股不需要做处理了， 对库存无影响2011.01.06
					/*+ ", "
					+dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX__BOutRecOP)*/
				/*	+","
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Lpwr)*///借出召回权证也不需要对基本分页的库存做处理了， 无影响
				    + ") and a1.fcheckstate = 1 and FPortCode in ("
					+ operSql.sqlCodes(portCodes)
					+ ")) a "
					//借入交易数据当天没有卖出该笔证券则不需要统计成本进入基本库存分页 add by zhouxiang 
					/*+"left join (select FSecurityCode,FPortCode,FBrokerCode from "
					+pub.yssGetTableName("Tb_Data_SubTrade")
					+" c where c.fbargaindate ="+dbl.sqlDate(dDate)
					+" and c.ftradetypecode = '02') m on a.FSecurityCode =m.FSecurityCode and a.FPortCode = m.FPortCode"
					+" and a.FBrokerCode =m.FBrokerCode  "*/
					//借入交易数据当天没有卖出该笔证券则不需要统计成本进入基本库存分页 add by zhouxiang 
					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
					
			
					+ " group by a.FSecurityCode, a.FPortCode, a.FAttrClsCode,a.FInvMgrCode,a.FBrokerCode ) a left join (select FPortCode, FPortName, FPortCury from "			
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where  FCheckState = 1 ) b on a.FPortCode = b.FPortCode "
					+
					//end by lidaolong 
    		       " left join (select FSecurityCode, FTradeCury from "+pub.yssGetTableName("Tb_Para_Security")+" where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode";
    		rs=dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
    		while(rs.next()){
    		       //-----------------------检查库存证券对应的币种代码是否正确 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A------//
                if (rs.getString("FTradeCury") == null || rs.getString("FTradeCury").trim().length() == 0) { //获取交易信息异常
                    throw new YssException("系统进行证借贷券库存统计,在获取证借贷券交易信息时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的交易币种信息不存在!"
                                           + "\n" + "请核查以下信息:" +
                                           "\n" + "1.【证券品种信息】中该证券信息是否存在且已审核!"
                                           + "\n" + "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                }
                //-------------------------------------------------------------------------------------------------------//

                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FPortCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);

                if (dBaseRate == 0 || dPortRate == 0) {
                    throw new YssException("系统进行证借贷券库存统计,在获取证借贷券交易信息时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的汇率信息不存在!"
                                           + "\n" + "请核查以下信息:" +
                                           "\n" + "1.【证券品种信息】中该证券信息是否存在且已审核!"
                                           + "\n" + "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");

                }

                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("FInvMgrCode") : "") +
                    (bAnaly2 ? "\f" + rs.getString("FBrokerCode") : "") +
                    (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                    (rs.getString("FAttrClsCode") == null ||
                     rs.getString("FAttrClsCode").length() == 0 ? " " :
                     rs.getString("FAttrClsCode")) + "\f" + 
                     rs.getString("FInvestType"); 
                if (hmEveStg.containsKey(sKey)) {
                    secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");
                    secstorage.setInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                    secstorage.setStrStorageAmount(YssD.round(YssD.add(YssFun.toDouble(
                        secstorage.getStrStorageAmount()),
                        rs.getDouble("FTradeAmount")),2) + "");
                    secstorage.setStrStorageCost(YssD.round(YssD.add(YssFun.toDouble(secstorage.
                        getStrStorageCost()),
                        rs.getDouble("FCost")),2) + "");
                    secstorage.setStrMStorageCost(YssD.round(YssD.add(YssFun.toDouble(
                        secstorage.getStrMStorageCost()),
                        rs.getDouble("FMCost")),2) + "");
                    secstorage.setStrVStorageCost(YssD.round(YssD.add(YssFun.toDouble(
                        secstorage.getStrVStorageCost()),
                        rs.getDouble("FVCost")),2) + "");

                    secstorage.setStrBaseCuryCost(YssD.round(YssD.add(YssFun.toDouble(
                        secstorage.getStrBaseCuryCost()),
                        rs.getDouble("FBaseCuryCost")),2) + "");
                    secstorage.setStrMBaseCuryCost(YssD.round(YssD.add(YssFun.toDouble(
                        secstorage.getStrMBaseCuryCost()),
                        rs.getDouble("FMBaseCuryCost")),2) + "");
                    secstorage.setStrVBaseCuryCost(YssD.round(YssD.add(YssFun.toDouble(
                        secstorage.getStrVBaseCuryCost()),
                        rs.getDouble("FVBaseCuryCost")),2) + "");

                    secstorage.setStrPortCuryCost(YssD.round(YssD.add(YssFun.toDouble(
                        secstorage.getStrPortCuryCost()),
                        rs.getDouble("FPortCuryCost")),2) + "");
                    secstorage.setStrMPortCuryCost(YssD.round(YssD.add(YssFun.toDouble(
                        secstorage.getStrMPortCuryCost()),
                        rs.getDouble("FMPortCuryCost")),2) + "");
                    secstorage.setStrVPortCuryCost(YssD.round(YssD.add(YssFun.toDouble(
                        secstorage.getStrVPortCuryCost()),
                        rs.getDouble("FVPortCuryCost")),2) + "");



                } else {
                    secstorage = new SecurityStorageBean();
                    secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
//               secstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
                    secstorage.setStrPortCode(rs.getString("FPortCode"));
                    secstorage.setStrCuryCode(rs.getString("FTradeCury"));
                    if (bAnaly1) {
                        secstorage.setStrFAnalysisCode1(rs.getString(
                            "FInvMgrCode"));
                    } else {
                        secstorage.setStrFAnalysisCode1(" ");
                    }
                    if (bAnaly2) {
                        secstorage.setStrFAnalysisCode2(rs.getString(
                            "FBrokerCode"));
                    } else {
                        secstorage.setStrFAnalysisCode2(" ");
                    }
                    if (bAnaly3) {
                        secstorage.setStrFAnalysisCode3(rs.getString(
                            "FAnalysisCode3"));
                    } else {
                        secstorage.setStrFAnalysisCode3(" ");
                    }
                    secstorage.setAttrCode(rs.getString("FAttrClsCode"));
                    secstorage.setInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");

                    secstorage.setStrStorageAmount(rs.getDouble("FTradeAmount") + "");
                    secstorage.setStrStorageCost(rs.getDouble("FCost") + "");
                    secstorage.setStrMStorageCost(rs.getDouble("FMCost") + "");
                    secstorage.setStrVStorageCost(rs.getDouble("FVCost") + "");

                    secstorage.setStrBaseCuryCost(rs.getDouble("FBaseCuryCost") + "");
                    secstorage.setStrMBaseCuryCost(rs.getDouble("FMBaseCuryCost") +
                        "");
                    secstorage.setStrVBaseCuryCost(rs.getDouble("FVBaseCuryCost") +
                        "");

                    secstorage.setStrPortCuryCost(rs.getDouble("FPortCuryCost") + "");
                    secstorage.setStrMPortCuryCost(rs.getDouble("FMPortCuryCost") +
                        "");
                    secstorage.setStrVPortCuryCost(rs.getDouble("FVPortCuryCost") +
                        "");


                    hmEveStg.put(sKey, secstorage);
                }
            }
    	}catch (Exception e) {
            throw new YssException("在统计证券借贷交易信息时出现异常!" + "\n", e); 

        } finally {
            dbl.closeResultSetFinal(rs);
        }
    	
		return secLend;
    	
    	
    	
    }
    //---------------end-------------------------
    /**
     * 交易数据库存统计方法 xuqiji 20091207 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @param dDate 业务日期
     * @param hmEveStg
     * @param bAnaly1
     * @param bAnaly2
     * @param bAnaly3
     * @param sPortCode
     * @return
     * @throws YssException
     */
    public Hashtable getStgTradeStatData(java.util.Date dDate, HashMap hmEveStg,
                                         boolean bAnaly1, boolean bAnaly2,
                                         boolean bAnaly3,String sPortCode) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        SecurityStorageBean secstorage = null;

        String sKey = "";

        double dBaseRate = 1;
        double dPortRate = 1;
        Hashtable amzFi = null;
        try {
            amzFi = new Hashtable();
            strSql =
                "select  a.*, b.FPortCury,c.FTradeCury from (" +
                "select a.FSecurityCode,FPortCode, FInvestType" + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                (bAnaly1 ? ", a.FInvMgrCode" : " ") +
                (bAnaly2 ? ", a.FBrokerCode" : " ") +
                //------ 证券库存统计如果存在3个分析代码的情况没考虑到，add by wangzuochun 2010.010.16 MS01772    存信息配置将证券类设置分析代码三时，进行资产估值报错    QDV4赢时胜(测试)2010年09月19日1_B    
                (bAnaly3 ? ", a.FCatCode as FAnalysisCode3" : " ") + 
                //--------------MS01772---------------//
                " ,FAttrClsCode ,Sum(" +
                dbl.sqlIsNull("FBailMoney*FCashInd", "0") +
                ") as FBailMoney," +
                " Sum(" +
                dbl.sqlIsNull("a.FTradeAmount*FAmountInd", "0") +
                ") as FTradeAmount, Sum(" +
                dbl.sqlIsNull("a.FTradeMoney*FCashInd", "0") +
                ") as FTradeMoney, sum(" +
                //成本的方向和数量的方向一样，所以这里乘的是数量方向
                dbl.sqlIsNull("a.FCost*FAmountInd") +
                ") as FCost, sum(" +
                dbl.sqlIsNull("a.FMCost*FAmountInd") +
                ") as FMCost, sum(" +
                dbl.sqlIsNull("a.FVCost*FAmountInd") +
                ") as FVCost, sum(" +

                dbl.sqlIsNull("a.FBaseCuryCost*FAmountInd") +
                ") as FBaseCuryCost, sum(" +
                dbl.sqlIsNull("a.FMBaseCuryCost*FAmountInd") +
                ") as FMBaseCuryCost, sum(" +
                dbl.sqlIsNull("a.FVBaseCuryCost*FAmountInd") +
                ") as FVBaseCuryCost, sum(" +

                dbl.sqlIsNull("a.FPortCuryCost*FAmountInd") +
                ") as FPortCuryCost, sum(" +
                dbl.sqlIsNull("a.FMPortCuryCost*FAmountInd") +
                ") as FMPortCuryCost, sum(" +
                dbl.sqlIsNull("a.FVPortCuryCost*FAmountInd") +
                ") as FVPortCuryCost, sum(" +

                dbl.sqlIsNull("a.FTotalCost*FAmountInd", "0") +
                ") as FTotalCost,'Natural' as FTradeState from (" +
                "select a1.*,a2.FCashInd,a2.FAmountInd,sec.fcatcode from " + // modify by wangzuochun 2010.010.16 MS01772    存信息配置将证券类设置分析代码三时，进行资产估值报错    QDV4赢时胜(测试)2010年09月19日1_B    
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " a1 join (select * from Tb_Base_TradeType" +
                " where FCheckState = 1) a2 " +
                " on a1.FTradeTypeCode = a2.FTradeTypeCode " +
                //------ 从证券信息表中获取品种类型，modify by wangzuochun 2010.010.16 MS01772    存信息配置将证券类设置分析代码三时，进行资产估值报错    QDV4赢时胜(测试)2010年09月19日1_B    
                " join (select FSecurityCode,FCatCode from " + pub.yssGetTableName("tb_para_security") + 
                " where FCheckState = 1) sec on a1.FSecurityCode = sec.FSecurityCode " +
                //-------------------------------MS01772-------------------------------//
                " where a1.FBargainDate = " + dbl.sqlDate(dDate) +
				(unStgTradeTypeCode.length()>0?(" and a1.FTradeTypeCode not in("+operSql.sqlCodes(unStgTradeTypeCode)+")"):"")+//这里添加不统计的业务类型 by leeyu 20100417 QDV4中保2010年4月14日02_B MS01092 合并太平版本代码
                //--- S00014  QDV4.1赢时胜（上海）2009年4月20日14_A 除去回购的数据 ----------------------
                //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A 2009.07.03 蒋锦 中签返款和新股申购的数据也要去掉
                " and a1.FTradeTypeCode not in (" + dbl.sqlString(YssOperCons.YSS_JYLX_ZRE) + "," +
                dbl.sqlString(YssOperCons.YSS_JYLX_NRE) + "," +
                dbl.sqlString(YssOperCons.YSS_JYLX_REMR) + ","+dbl.sqlString(YssOperCons.YSS_JYLX_REMC) + ","+//add by zhouwei 20120523 bug 4284 买断式回购
                dbl.sqlString(YssOperCons.YSS_JYLX_XGSG) + "," +
                dbl.sqlString(YssOperCons.YSS_JYLX_ZQFK) + "," +
                dbl.sqlString(YssOperCons.YSS_JYLX_PGJK) + // add by jiangshichao 配股缴款业务也要去掉 
                ")" +
                //----------------------------------------------------------------------------------
                " and a1.fcheckstate = 1 and FPortCode in (" + portCodes + ")" +
                /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
                * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//               	(statCodes.length()>0?") and a1.FSecurityCode in("+operSql.sqlCodes(statCodes):"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
                (statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"a1.FSecurityCode",500) + ")":"")+
                /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
                
               	") a  " +
                " group by a.FSecurityCode, a.FPortCode, a.FAttrClsCode, a.FInvestType " + //投资类型 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                (bAnaly1 ? ", a.FInvMgrCode" : " ") +
                (bAnaly2 ? ", a.FBrokerCode" : " ") +
                //------ 证券库存统计如果存在3个分析代码的情况没考虑到，add by wangzuochun 2010.010.16 MS01772    存信息配置将证券类设置分析代码三时，进行资产估值报错    QDV4赢时胜(测试)2010年09月19日1_B    
                (bAnaly3 ? ", a.FCatCode" : " ") + 
                //--------------MS01772---------------//
                //------------------------------------------
                " union " +
                //--------------------------------------------------下段取回转的交易记录
                "select a.FSecurityCode,FPortCode,FInvestType " + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                (bAnaly1 ? ", a.FInvMgrCode" : " ") +
                (bAnaly2 ? ", a.FBrokerCode" : " ") +
                //------ 证券库存统计如果存在3个分析代码的情况没考虑到，add by wangzuochun 2010.010.16 MS01772    存信息配置将证券类设置分析代码三时，进行资产估值报错    QDV4赢时胜(测试)2010年09月19日1_B    
                (bAnaly3 ? ", a.FCatCode as FAnalysisCode3" : " ") + 
                //--------------MS01772---------------//
                " ,FAttrClsCode ,Sum(" +
                dbl.sqlIsNull("FBailMoney*FCashInd*-1", "0") +
                ") as FBailMoney," +
                " Sum(" +
                dbl.sqlIsNull("a.FTradeAmount*FAmountInd*-1", "0") +
                ") as FTradeAmount, Sum(" +
                dbl.sqlIsNull("a.FTradeMoney*FCashInd*-1", "0") +
                ") as FTradeMoney, sum(" +
                //成本的方向和数量的方向一样，所以这里乘的是数量方向
                dbl.sqlIsNull("a.FCost*FAmountInd*-1") +
                ") as FCost, sum(" +
                dbl.sqlIsNull("a.FMCost*FAmountInd*-1") +
                ") as FMCost, sum(" +
                dbl.sqlIsNull("a.FVCost*FAmountInd*-1") +
                ") as FVCost, sum(" +

                dbl.sqlIsNull("a.FBaseCuryCost*FAmountInd*-1") +
                ") as FBaseCuryCost, sum(" +
                dbl.sqlIsNull("a.FMBaseCuryCost*FAmountInd*-1") +
                ") as FMBaseCuryCost, sum(" +
                dbl.sqlIsNull("a.FVBaseCuryCost*FAmountInd*-1") +
                ") as FVBaseCuryCost, sum(" +

                dbl.sqlIsNull("a.FPortCuryCost*FAmountInd*-1") +
                ") as FPortCuryCost, sum(" +
                dbl.sqlIsNull("a.FMPortCuryCost*FAmountInd*-1") +
                ") as FMPortCuryCost, sum(" +
                dbl.sqlIsNull("a.FVPortCuryCost*FAmountInd*-1") +
                ") as FVPortCuryCost, sum(" +

                dbl.sqlIsNull("a.FTotalCost*FAmountInd*-1", "0") +
                ") as FTotalCost,'Cancel' as FTradeState from (" +
                "select a1.*,a2.FCashInd,a2.FAmountInd,sec.FCatCode from " + // modify by wangzuochun 2010.010.16 MS01772    存信息配置将证券类设置分析代码三时，进行资产估值报错    QDV4赢时胜(测试)2010年09月19日1_B    
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " a1 join (select * from Tb_Base_TradeType" +
               	//" where FCheckState = 1) a2 " +
               	" where FCheckState = 1 "+(unStgTradeTypeCode.length()>0?(" and FTradeTypeCode not in("+operSql.sqlCodes(unStgTradeTypeCode)+")"):"")+" ) a2 "+//这里添加不统计的业务类型 by leeyu 20100417 QDV4中保2010年4月14日02_B MS01092 合并太平版本代码
                " on a1.FTradeTypeCode = a2.FTradeTypeCode " +
                //------ 从证券信息表中获取品种类型，modify by wangzuochun 2010.010.16 MS01772    存信息配置将证券类设置分析代码三时，进行资产估值报错    QDV4赢时胜(测试)2010年09月19日1_B    
                " join (select FSecurityCode,FCatCode from " + pub.yssGetTableName("tb_para_security") + 
                " where FCheckState = 1) sec on a1.FSecurityCode = sec.FSecurityCode " +
                //-------------------------------MS01772-------------------------------//
                " where ((a1.FFactSettleDate = " + dbl.sqlDate(dDate) +
                " and FSettleState = 2) " + 
                " or (a1.FMatureDate = " +
                dbl.sqlDate(dDate) +
                " and (a1.FTradeTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_JYLX_ZRE) + //sj add 20071120 添加了回购的条件和结算时间
                " or a1.FTradeTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_JYLX_NRE) +
              //add by zhouwei 20120523 bug 4284 买断式回购
                " or a1.FTradeTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_JYLX_REMR) +
                " or a1.FTradeTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_JYLX_REMC) +
                " ))" +
                //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A 2009.07.03 蒋锦 中签返款和新股申购的数据也要去掉
                ") and a1.FTradeTypeCode not in (" +
                dbl.sqlString(YssOperCons.YSS_JYLX_XGSG) + "," +
                dbl.sqlString(YssOperCons.YSS_JYLX_ZQFK) + ")" +
                //----------------------------------------------------------------------------------
                " and a1.fcheckstate = 1 and FPortCode in (" +
                sPortCode +")"+
                /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
                * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//               	(statCodes.length()>0?") and a1.FSecurityCode in("+operSql.sqlCodes(statCodes):"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
                (statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"a1.FSecurityCode",500) + ")":"")+
                /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               	") a  " +
                " group by a.FSecurityCode, a.FPortCode, a.FAttrClsCode, a.FInvestType " + //投资类型 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                (bAnaly1 ? ", a.FInvMgrCode" : " ") +
                (bAnaly2 ? ", a.FBrokerCode" : " ") +
                //------ 证券库存统计如果存在3个分析代码的情况没考虑到，add by wangzuochun 2010.010.16 MS01772    存信息配置将证券类设置分析代码三时，进行资产估值报错    QDV4赢时胜(测试)2010年09月19日1_B    
                (bAnaly3 ? ", a.FCatCode" : " ") + 
                //--------------MS01772---------------//
                //---------------------------------------------------------
                ") a left join " +
                //------ modify by wangzuochun  2010.07.15  MS01441    新建买入证券的数据，库存统计后，证券库存的数量和成本会增倍    QDV4赢时胜(测试)2010年7月14日01_B    
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
             /*   " (select db.* from (select FPortCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FPortCode) da join (select FPortCode, FPortName, FStartDate, FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + 
                ") db on da.FPortCode = db.FPortCode and da.FStartDate = db.FStartDate) b on a.FPortCode = b.FPortCode " + 
                */
                
                " (select FPortCode, FPortName, FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) b on a.FPortCode = b.FPortCode " + 
                
                //end by lidaolong
                //-------------------------------------------- MS01441 -------------------------------------------//
                " left join (select FSecurityCode,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
               	" where FCheckState = 1 "+
               	/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               	* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//               	(statCodes.length()>0?" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")":"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
               	(statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")":"")+
               	/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               	") c on a.FSecurityCode = c.FSecurityCode";

            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getString("FAttrClsCode") != null && rs.getString("FAttrClsCode").equalsIgnoreCase("19")) {
                    amzFi.put(rs.getString("FSecurityCode"),
                              rs.getString("FSecurityCode"));
                }
                dBaseRate = 1;
                dPortRate = 1;
                //-----------------------检查库存证券对应的币种代码是否正确 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A------//
                if (rs.getString("FTradeCury") == null || rs.getString("FTradeCury").trim().length() == 0) { //获取交易信息异常
                    throw new YssException("系统进行证券库存统计,在获取交易信息时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的交易币种信息不存在!"
                                           + "\n" + "请核查以下信息:" +
                                           "\n" + "1.【证券品种信息】中该证券信息是否存在且已审核!"
                                           + "\n" + "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                }
                //-------------------------------------------------------------------------------------------------------//

                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FPortCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);

                if (dBaseRate == 0 || dPortRate == 0) {
                    throw new YssException("系统进行证券库存统计,在获取交易信息时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的汇率信息不存在!"
                                           + "\n" + "请核查以下信息:" +
                                           "\n" + "1.【证券品种信息】中该证券信息是否存在且已审核!"
                                           + "\n" + "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");

                }

                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("FInvMgrCode") : "") +
                    (bAnaly2 ? "\f" + rs.getString("FBrokerCode") : "") +
                    (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                    (rs.getString("FAttrClsCode") == null ||
                     rs.getString("FAttrClsCode").length() == 0 ? " " :
                     rs.getString("FAttrClsCode")) + "\f" + // 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                     rs.getString("FInvestType"); //获取投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                if (hmEveStg.containsKey(sKey)) {
                    secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");
                    secstorage.setInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                    secstorage.setStrStorageAmount(YssD.add(YssFun.toDouble(
                        secstorage.getStrStorageAmount()),
                        rs.getDouble("FTradeAmount")) + "");
                    secstorage.setStrStorageCost(YssD.add(YssFun.toDouble(secstorage.
                        getStrStorageCost()),
                        rs.getDouble("FCost")) + "");
                    secstorage.setStrMStorageCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMStorageCost()),
                        rs.getDouble("FMCost")) + "");
                    secstorage.setStrVStorageCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVStorageCost()),
                        rs.getDouble("FVCost")) + "");

                    secstorage.setStrBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrBaseCuryCost()),
                        rs.getDouble("FBaseCuryCost")) + "");
                    secstorage.setStrMBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMBaseCuryCost()),
                        rs.getDouble("FMBaseCuryCost")) + "");
                    secstorage.setStrVBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVBaseCuryCost()),
                        rs.getDouble("FVBaseCuryCost")) + "");

                    secstorage.setStrPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrPortCuryCost()),
                        rs.getDouble("FPortCuryCost")) + "");
                    secstorage.setStrMPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMPortCuryCost()),
                        rs.getDouble("FMPortCuryCost")) + "");
                    secstorage.setStrVPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVPortCuryCost()),
                        rs.getDouble("FVPortCuryCost")) + "");

                    secstorage.setBailMoney(YssD.add(secstorage.getBailMoney(),
                        rs.getDouble("FBailMoney")));
                } else {
                    secstorage = new SecurityStorageBean();
                    secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
//               secstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
                    secstorage.setStrPortCode(rs.getString("FPortCode"));
                    secstorage.setStrCuryCode(rs.getString("FTradeCury"));
                    if (bAnaly1) {
                        secstorage.setStrFAnalysisCode1(rs.getString(
                            "FInvMgrCode"));
                    } else {
                        secstorage.setStrFAnalysisCode1(" ");
                    }
                    if (bAnaly2) {
                        secstorage.setStrFAnalysisCode2(rs.getString(
                            "FBrokerCode"));
                    } else {
                        secstorage.setStrFAnalysisCode2(" ");
                    }
                    if (bAnaly3) {
                        secstorage.setStrFAnalysisCode3(rs.getString(
                            "FAnalysisCode3"));
                    } else {
                        secstorage.setStrFAnalysisCode3(" ");
                    }
                    secstorage.setAttrCode(rs.getString("FAttrClsCode"));
                    secstorage.setInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");

                    secstorage.setStrStorageAmount(rs.getDouble("FTradeAmount") + "");
                    secstorage.setStrStorageCost(rs.getDouble("FCost") + "");
                    secstorage.setStrMStorageCost(rs.getDouble("FMCost") + "");
                    secstorage.setStrVStorageCost(rs.getDouble("FVCost") + "");

                    secstorage.setStrBaseCuryCost(rs.getDouble("FBaseCuryCost") + "");
                    secstorage.setStrMBaseCuryCost(rs.getDouble("FMBaseCuryCost") +
                        "");
                    secstorage.setStrVBaseCuryCost(rs.getDouble("FVBaseCuryCost") +
                        "");

                    secstorage.setStrPortCuryCost(rs.getDouble("FPortCuryCost") + "");
                    secstorage.setStrMPortCuryCost(rs.getDouble("FMPortCuryCost") +
                        "");
                    secstorage.setStrVPortCuryCost(rs.getDouble("FVPortCuryCost") +
                        "");

                    secstorage.setBailMoney(rs.getDouble("FBailMoney"));
                    

                    hmEveStg.put(sKey, secstorage);
                }
            }
        } catch (Exception e) {
            throw new YssException("在统计交易信息时出现异常!" + "\n", e); //统计交易信息异常 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A

        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return amzFi;
    }
    
    /**
     * ETF资产估值处理时只统计主动投资的交易数据 xuqiji 20091207 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @param dDate 业务日期
     * @param hmEveStg 保存数据的hash表
     * @param bAnaly1 分析代码1
     * @param bAnaly2 分析代码2
     * @param bAnaly3 分析代码3 
     * @return
     * @throws YssException
     */
    public Hashtable getStgETFTradeStatData(java.util.Date dDate, HashMap hmEveStg,
            boolean bAnaly1, boolean bAnaly2,
            boolean bAnaly3,String sOtherCompanyPortCode) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        SecurityStorageBean secstorage = null;

        String sKey = "";

        double dBaseRate = 1;
        double dPortRate = 1;
        Hashtable amzFi = null;
        try {
            amzFi = new Hashtable();
            strSql =
                "select  a.*, b.FPortCury,c.FTradeCury from (" +
                "select a.FSecurityCode,' ' as FAttrClsCode,FPortCode " +
                (bAnaly1 ? ", a.FInvMgrCode" : " ") +
                (bAnaly2 ? ", a.FBrokerCode" : " ") +
                " ,0 as FBailMoney," +
                " Sum(" +
                dbl.sqlIsNull("a.FTradeAmount*FAmountInd", "0") +
                ") as FTradeAmount, Sum(" +
                dbl.sqlIsNull("a.FTradeMoney*FCashInd", "0") +
                ") as FTradeMoney, sum(" +
                //成本的方向和数量的方向一样，所以这里乘的是数量方向
                dbl.sqlIsNull("a.FCost*FAmountInd") +
                ") as FCost, sum(" +
                dbl.sqlIsNull("a.FCost*FAmountInd") +
                ") as FMCost, sum(" +
                dbl.sqlIsNull("a.FCost*FAmountInd") +
                ") as FVCost, sum(" +
                dbl.sqlIsNull("a.FBaseCuryCost*FAmountInd") +
                ") as FBaseCuryCost, sum(" +
                dbl.sqlIsNull("a.FBaseCuryCost*FAmountInd") +
                ") as FMBaseCuryCost, sum(" +
                dbl.sqlIsNull("a.FBaseCuryCost*FAmountInd") +
                ") as FVBaseCuryCost, sum(" +

                dbl.sqlIsNull("a.FPortCuryCost*FAmountInd") +
                ") as FPortCuryCost, sum(" +
                dbl.sqlIsNull("a.FPortCuryCost*FAmountInd") +
                ") as FMPortCuryCost, sum(" +
                dbl.sqlIsNull("a.FPortCuryCost*FAmountInd") +
                ") as FVPortCuryCost, sum(" +

                dbl.sqlIsNull("a.FTotalCost*FAmountInd", "0") +
                ") as FTotalCost,'Natural' as FTradeState from (" +
                "select a1.*,a2.FCashInd,a2.FAmountInd,' ' as FInvMgrCode,' ' as FBrokerCode from " +
                pub.yssGetTableName("Tb_ETF_SubTrade") +
                " a1 join (select * from Tb_Base_TradeType" +
                " where FCheckState = 1) a2 " +
                " on a1.FTradeTypeCode = a2.FTradeTypeCode where a1.FBargainDate = " +
                dbl.sqlDate(dDate) +
                /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
                * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//                (statCodes.length()>0?" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")":"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
                (statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")":"")+
                /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
                " and FPortCode in (" + sOtherCompanyPortCode +
                ") and FETFTradeWayCode = 'ACTIVE') a  " +
                " group by a.FSecurityCode, a.FPortCode " +
                ") a left join " +
                //--------------------------------------------------
                " (select FPortCode,FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) b on a.FPortCode = b.FPortCode" +
                //--------------------------------------------------
                " left join (select FSecurityCode,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode";

            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getString("FAttrClsCode") != null && rs.getString("FAttrClsCode").equalsIgnoreCase("19")) {
                    amzFi.put(rs.getString("FSecurityCode"),
                              rs.getString("FSecurityCode"));
                }
                dBaseRate = 1;
                dPortRate = 1;
                //-----------------------检查库存证券对应的币种代码是否正确 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A------//
                if (rs.getString("FTradeCury") == null || rs.getString("FTradeCury").trim().length() == 0) { //获取交易信息异常
                    throw new YssException("系统进行证券库存统计,在获取交易信息时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的交易币种信息不存在!"
                                           + "\n" + "请核查以下信息:" +
                                           "\n" + "1.【证券品种信息】中该证券信息是否存在且已审核!"
                                           + "\n" + "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                }
                //-------------------------------------------------------------------------------------------------------//

                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FPortCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);

                if (dBaseRate == 0 || dPortRate == 0) {
                    throw new YssException("系统进行证券库存统计,在获取交易信息时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的汇率信息不存在!"
                                           + "\n" + "请核查以下信息:" +
                                           "\n" + "1.【证券品种信息】中该证券信息是否存在且已审核!"
                                           + "\n" + "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");

                }

                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("FInvMgrCode") : "") +
                    (bAnaly2 ? "\f" + rs.getString("FBrokerCode") : "") +
                    (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                    (rs.getString("FAttrClsCode") == null ||
                     rs.getString("FAttrClsCode").length() == 0 ? " " :
                     rs.getString("FAttrClsCode"));
                if (hmEveStg.containsKey(sKey)) {
                    secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");

                    secstorage.setStrStorageAmount(YssD.add(YssFun.toDouble(
                        secstorage.getStrStorageAmount()),
                        rs.getDouble("FTradeAmount")) + "");
                    secstorage.setStrStorageCost(YssD.add(YssFun.toDouble(secstorage.
                        getStrStorageCost()),
                        rs.getDouble("FCost")) + "");
                    secstorage.setStrMStorageCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMStorageCost()),
                        rs.getDouble("FMCost")) + "");
                    secstorage.setStrVStorageCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVStorageCost()),
                        rs.getDouble("FVCost")) + "");

                    secstorage.setStrBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrBaseCuryCost()),
                        rs.getDouble("FBaseCuryCost")) + "");
                    secstorage.setStrMBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMBaseCuryCost()),
                        rs.getDouble("FMBaseCuryCost")) + "");
                    secstorage.setStrVBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVBaseCuryCost()),
                        rs.getDouble("FVBaseCuryCost")) + "");

                    secstorage.setStrPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrPortCuryCost()),
                        rs.getDouble("FPortCuryCost")) + "");
                    secstorage.setStrMPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMPortCuryCost()),
                        rs.getDouble("FMPortCuryCost")) + "");
                    secstorage.setStrVPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVPortCuryCost()),
                        rs.getDouble("FVPortCuryCost")) + "");

                    secstorage.setBailMoney(YssD.add(secstorage.getBailMoney(),
                        rs.getDouble("FBailMoney")));

                } else {
                    secstorage = new SecurityStorageBean();
                    secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
//               secstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
                    secstorage.setStrPortCode(rs.getString("FPortCode"));
                    secstorage.setStrCuryCode(rs.getString("FTradeCury"));
                    if (bAnaly1) {
                        secstorage.setStrFAnalysisCode1(rs.getString(
                            "FInvMgrCode"));
                    } else {
                        secstorage.setStrFAnalysisCode1(" ");
                    }
                    if (bAnaly2) {
                        secstorage.setStrFAnalysisCode2(rs.getString(
                            "FBrokerCode"));
                    } else {
                        secstorage.setStrFAnalysisCode2(" ");
                    }
                    if (bAnaly3) {
                        secstorage.setStrFAnalysisCode3(rs.getString(
                            "FAnalysisCode3"));
                    } else {
                        secstorage.setStrFAnalysisCode3(" ");
                    }
                    secstorage.setAttrCode(rs.getString("FAttrClsCode"));

                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");

                    secstorage.setStrStorageAmount(rs.getDouble("FTradeAmount") + "");
                    secstorage.setStrStorageCost(rs.getDouble("FCost") + "");
                    secstorage.setStrMStorageCost(rs.getDouble("FMCost") + "");
                    secstorage.setStrVStorageCost(rs.getDouble("FVCost") + "");

                    secstorage.setStrBaseCuryCost(rs.getDouble("FBaseCuryCost") + "");
                    secstorage.setStrMBaseCuryCost(rs.getDouble("FMBaseCuryCost") +
                        "");
                    secstorage.setStrVBaseCuryCost(rs.getDouble("FVBaseCuryCost") +
                        "");

                    secstorage.setStrPortCuryCost(rs.getDouble("FPortCuryCost") + "");
                    secstorage.setStrMPortCuryCost(rs.getDouble("FMPortCuryCost") +
                        "");
                    secstorage.setStrVPortCuryCost(rs.getDouble("FVPortCuryCost") +
                        "");

                    secstorage.setBailMoney(rs.getDouble("FBailMoney"));

                    hmEveStg.put(sKey, secstorage);
                }
            }
        } catch (Exception e) {
            throw new YssException("在统计ETF交易信息时出现异常!" + "\n", e); //统计交易信息异常 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A

        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return amzFi;
    }
    
    public void getStgSecExchangeStatData(java.util.Date dDate, HashMap hmEveStg,
                                          boolean bAnaly1, boolean bAnaly2,
                                          boolean bAnaly3) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        SecurityStorageBean secstorage = null;

        String sKey = "";

        double dBaseRate = 1;
        double dPortRate = 1;

        try {
            strSql = "select * from (" +
                "select FSecurityCode, FPortCode, '1' as FInOut " +
                (bAnaly1 ? ", FAnalysisCode1" : "") +
                (bAnaly2 ? ", FAnalysisCode2" : "") +
                (bAnaly3 ? ", FAnalysisCode3" : "") +
                " ,FAttrClsCode " + //sj add 20071204
                ", sum(FInAmount) as FAmount,sum(FExchangeCost) as FExchangeCost,sum(FMExchangeCost) as FMExchangeCost,sum(FVExchangeCost) as FVExchangeCost" +
                ", sum(FPortExchangeCost) as FPortExchangeCost,sum(FMPortExchangeCost) as FMPortExchangeCost,sum(FVPortExchangeCost) as FVPortExchangeCost" +
                ", sum(FBaseExchangeCost) as FBaseExchangeCost,sum(FMBaseExchangeCost) as FMBaseExchangeCost,sum(FVBaseExchangeCost) as FVBaseExchangeCost" +
                ", FExchangeDate " + //---添加了日期字段,用以判断日期.sj add 20080121
                " from " + pub.yssGetTableName("Tb_Data_SecExchangeIn") +
                " where FCheckState = 1 " +
                " and FExchangeDate = " + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes + ")" +
                " group by FSecurityCode, FPortCode, FAttrClsCode ,FExchangeDate " +
                (bAnaly1 ? ", FAnalysisCode1" : "") +
                (bAnaly2 ? ", FAnalysisCode2" : "") +
                (bAnaly3 ? ", FAnalysisCode3" : "") +
                //--------------------------------------------------------------
                " union " +
                //--------------------------------------------------------------
                "select FSecurityCode, FPortCode, '-1' as FInOut " +
                (bAnaly1 ? ", FAnalysisCode1" : "") +
                (bAnaly2 ? ", FAnalysisCode2" : "") +
                (bAnaly3 ? ", FAnalysisCode3" : "") +
                " ,FAttrClsCode " + //sj add 20071204
                ", sum(FOutAmount*-1) as FAmount,sum(FExchangeCost*-1) as FExchangeCost,sum(FMExchangeCost*-1) as FMExchangeCost,sum(FVExchangeCost*-1) as FVExchangeCost" +
                ", sum(FPortExchangeCost*-1) as FPortExchangeCost,sum(FMPortExchangeCost*-1) as FMPortExchangeCost,sum(FVPortExchangeCost*-1) as FVPortExchangeCost" +
                ", sum(FBaseExchangeCost*-1) as FBaseExchangeCost,sum(FMBaseExchangeCost*-1) as FMBaseExchangeCost,sum(FVBaseExchangeCost*-1) as FVBaseExchangeCost" +
                ", a2.FExchangeDate as FExchangeDate " + //---添加了日期字段,用以判断日期.join 流入证券表以获取日期sj add 20080121
                " from " + pub.yssGetTableName("Tb_Data_SecExchangeOut") +
                " a1 " +
                " join (select FNum,FExchangeDate from " +
                pub.yssGetTableName("Tb_Data_SecExchangeIn") +
                //---------添加了判断条件---sj edit 20080122---------------------------------------------------------------------
                " where FCheckState = 1 and FExchangeDate = " +
                dbl.sqlDate(dDate) + " and FPortCode in (" + portCodes + ")" +
                //-------------------------------------------------------------------------------------------------------------
                //") a2 on a1.FNum = a2.FNum" +
                ") a2 on a1.FInsecNum = a2.FNum" + // by leeyu 080808
                " where FCheckState = 1 " +
                " and FPortCode in (" + portCodes + ")" +
                " group by FSecurityCode, FPortCode, FAttrClsCode, FExchangeDate " +
                (bAnaly1 ? ", FAnalysisCode1" : "") +
                (bAnaly2 ? ", FAnalysisCode2" : "") +
                (bAnaly3 ? ", FAnalysisCode3" : "") + ") a" +
                //---------------------------------------------------------------
                " left join (select FSecurityCode,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) b on a.FSecurityCode = b.FSecurityCode" +
                //---------------------------------------------------------------
                " left join (select FPortCode,FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) c on a.FPortCode = c.FPortCode";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //----------------------增加币种信息有效性检查 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A--------//
                if (rs.getString("FTradeCury") == null || rs.getString("FTradeCury").trim().length() == 0) {
                    throw new YssException("系统进行证券库存统计,在证券兑换交易统计时检查到代码为【"
                                           + rs.getString("FSecurityCode") +
                                           "】证券对应的币种信息不存在!"
                                           + "\n" + "请核查以下信息：" +
                                           "\n" + "1.【证券品种信息】中该证券信息是否存在且已审核!" +
                                           "\n" + "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                }
                //------------------------------------------------------------------------------------------------//
                dBaseRate = 1;
                dPortRate = 1;

                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (bAnaly2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                    (rs.getString("FAttrClsCode") == null ||
                     rs.getString("FAttrClsCode").length() == 0 ? " " :
                     rs.getString("FAttrClsCode"));

                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FPortCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);
                //----------------------增加汇率信息有效性检查 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A---------//
                if (dBaseRate == 0 || dPortRate == 0) {
                    throw new YssException("系统进行证券库存统计,在证券兑换交易统计时检查到代码为【"
                                           + rs.getString("FSecurityCode") +
                                           "】证券对应的汇率信息不存在!"
                                           + "\n" + "请核查以下信息:" + "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                           "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");
                }
                //------------------------------------------------------------------------------------------------//
                if (hmEveStg.containsKey(sKey)) {
                    secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));

                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");

                    secstorage.setStrStorageAmount(YssD.add(YssFun.toDouble(
                        secstorage.getStrStorageAmount()),
                        rs.getDouble("FAmount")) + "");

                    secstorage.setStrStorageCost(YssD.add(YssFun.toDouble(secstorage.
                        getStrStorageCost()),
                        rs.getDouble("FExchangeCost")) + "");
                    secstorage.setStrMStorageCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMStorageCost()),
                        rs.getDouble("FMExchangeCost")) + "");
                    secstorage.setStrVStorageCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVStorageCost()),
                        rs.getDouble("FVExchangeCost")) + "");

                    secstorage.setStrBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrBaseCuryCost()),
                        rs.getDouble("FBaseExchangeCost")) + "");
                    secstorage.setStrMBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMBaseCuryCost()),
                        rs.getDouble("FMBaseExchangeCost")) + "");
                    secstorage.setStrVBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVBaseCuryCost()),
                        rs.getDouble("FVBaseExchangeCost")) + "");

                    secstorage.setStrPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrPortCuryCost()),
                        rs.getDouble("FPortExchangeCost")) + "");
                    secstorage.setStrMPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMPortCuryCost()),
                        rs.getDouble("FMPortExchangeCost")) + "");
                    secstorage.setStrVPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVPortCuryCost()),
                        rs.getDouble("FVPortExchangeCost")) + "");
                } else {
                    secstorage = new SecurityStorageBean();
                    secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
//               secstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
                    secstorage.setStrPortCode(rs.getString("FPortCode"));
                    secstorage.setStrCuryCode(rs.getString("FTradeCury"));
                    if (bAnaly1) {
                        secstorage.setStrFAnalysisCode1(rs.getString(
                            "FAnalysisCode1"));
                    } else {
                        secstorage.setStrFAnalysisCode1(" ");
                    }
                    if (bAnaly2) {
                        secstorage.setStrFAnalysisCode2(rs.getString(
                            "FAnalysisCode2"));
                    } else {
                        secstorage.setStrFAnalysisCode2(" ");
                    }
                    if (bAnaly3) {
                        secstorage.setStrFAnalysisCode3(rs.getString(
                            "FAnalysisCode3"));
                    } else {
                        secstorage.setStrFAnalysisCode3(" ");
                    }

                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");

                    secstorage.setStrStorageAmount(rs.getDouble("FAmount") + "");
                    secstorage.setStrStorageCost(rs.getDouble("FExchangeCost") + "");
                    secstorage.setStrMStorageCost(rs.getDouble("FMExchangeCost") +
                                                  "");
                    secstorage.setStrVStorageCost(rs.getDouble("FVExchangeCost") +
                                                  "");

                    secstorage.setStrBaseCuryCost(rs.getDouble("FBaseExchangeCost") +
                                                  "");
                    secstorage.setStrMBaseCuryCost(rs.getDouble("FMBaseExchangeCost") +
                        "");
                    secstorage.setStrVBaseCuryCost(rs.getDouble("FVBaseExchangeCost") +
                        "");

                    secstorage.setStrPortCuryCost(rs.getDouble("FPortExchangeCost") +
                                                  "");
                    secstorage.setStrMPortCuryCost(rs.getDouble("FMPortExchangeCost") +
                        "");
                    secstorage.setStrVPortCuryCost(rs.getDouble("FVPortExchangeCost") +
                        "");

                    secstorage.setBailMoney(0);

                    hmEveStg.put(sKey, secstorage);

                }
            }
        } catch (Exception e) {
            throw new YssException("在统计证券兑换交易时出现异常!" + "\n", e); //统计证券兑换交易信息异常 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A

        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 计算股票卖出估值增值
     * @throws YssException
     * 081010 edit by jc 分组合计算卖出估值增值，添加参数portCodes
     */
    private void getStockSellTradeRela(java.util.Date dDate, String portCodes) throws YssException {
        try {
            BaseBusinWork businWork = new SellTradeRelaCal();
            businWork.setYssPub(pub);
            businWork.setWorkDate(dDate);
            businWork.setPortCodes(portCodes);
            businWork.doOperation("");
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * 计算股指期货库存,插入综合业务
     * @param dDate Date
     * @throws YssException
     */
    private void getFuturesTradeStatData(java.util.Date dWorkDate, String sPortCodes) throws YssException {
        BaseBusinWork businWork = null;
        try {
        	//add by songjie 2013.03.11 将估值日已选组合对应的期货交易数据插入到TMP表中
        	insertFUTmp(dWorkDate,sPortCodes);
        	
            //---------- 提取综合业务
            businWork = new SecurityStorageDistill();
            businWork.setYssPub(pub);
            businWork.setWorkDate(dWorkDate);
            businWork.setPortCodes(sPortCodes);
            businWork.doOperation("");
            //-----------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }
    
    /**
     * add by sognjie 2013.03.08
     * 将估值日已选组合对应的期货交易数据插入到TMP表中
     * @throws YssException
     */
    private void insertFUTmp(java.util.Date dWorkDate, String sPortCodes) throws YssException{
    	String strSql = "";
        CtlPubPara ctlpubpara = new CtlPubPara();
        ctlpubpara.setYssPub(pub);
        HashMap hmFU = null;
        String portCode = sPortCodes.replaceAll("'", "");
        /**add---huhuichao 2013-10-18 BUG  81248 资产估值后期货无法产生估值增值的资金调拨*/
        String ctlPubPara ="";
        String[] ctlPubpara =null;
        String startDate="";
    	String selPorts="";
    	String yesOrNo="";
    	ResultSet rs = null;
        /**end---huhuichao 2013-10-18 BUG  81248*/
    	try{
    		strSql = " delete from " + pub.yssGetTableName("Tb_Data_Futurestrade_Tmp") + 
    		" where FBargainDate = " + dbl.sqlDate(dWorkDate) + " and FPortCode = " +
    		dbl.sqlString(portCode);
    		
    		dbl.executeSql(strSql);
    		
    		strSql = " insert into " + pub.yssGetTableName("Tb_Data_Futurestrade_Tmp") + 
    		" (select * from " + pub.yssGetTableName("Tb_Data_Futurestrade") + 
    		" where FBargainDate = " + dbl.sqlDate(dWorkDate) + " and FPortCode = " + 
    		dbl.sqlString(portCode) + ") ";
    		
    		dbl.executeSql(strSql);
    		/**add---huhuichao 2013-10-18 BUG  81248 资产估值后期货无法产生估值增值的资金调拨*/
    		ctlPubPara = ctlpubpara.getFUStBrokerTwo(dWorkDate,portCode);
    		//hmFU = ctlpubpara.getFUStBroker();
    		if(ctlPubPara != ""){
    		ctlPubpara = ctlPubPara.split("\t");
    		startDate = ctlPubpara[0];
    		selPorts = ctlPubpara[1];
    		yesOrNo = ctlPubpara[2];
    		}
    		//if(hmFU != null && hmFU.get(portCode) != null &&((String)hmFU.get(portCode)).equals("0"))
    		if(yesOrNo !=null && yesOrNo.equals("0"))
    		{
    			strSql = " update " + pub.yssGetTableName("Tb_Data_Futurestrade_Tmp") +
    			" set FBrokerCode = ' ' where FBargainDate = " + dbl.sqlDate(dWorkDate) + 
    			" and FPortCode = " + dbl.sqlString(portCode);
    			
    			dbl.executeSql(strSql);
    		}
    		if(yesOrNo !=null && yesOrNo.equals("0")){
    			strSql =  " select t.fbrokercode from "+pub.yssGetTableName("Tb_Data_Futurestrade_Tmp")+
    			" t where t.fbrokercode <> ' ' and t.fportcode = " + dbl.sqlString(portCode) + 
    			" and  t.fbargaindate >=  " + dbl.sqlDate(startDate) + " and t.fbargaindate < "+dbl.sqlDate(dWorkDate);
    			
    			rs=dbl.openResultSet(strSql);
    			if(rs.next()){
    				strSql = " update " + pub.yssGetTableName("Tb_Data_Futurestrade_Tmp") +
        			" set FBrokerCode = ' ' "+ " where  fbargaindate >=  " + dbl.sqlDate(startDate) +
        			" and fbargaindate < "+dbl.sqlDate(dWorkDate)+
        			" and FPortCode = " + dbl.sqlString(portCode);
    				dbl.executeSql(strSql);
    			}
    		}
    		/**end---huhuichao 2013-10-18 BUG  81248*/
    		
    	}catch(Exception e){
    		throw new YssException("插入股指期货交易数据临时表出错！");
    	}
    }
    
   /** 合并太平版本代码
    * 此方法专用于太平资产转货业务的证券库存统计 合并太平版本代码
    * by leeyu 20100419 QDV4中保2010年4月14日02_B MS01092
    * @param dDate
    * @return
    * @throws YssException
    */
   public ArrayList getPartInsideChangeGoodsStatData(java.util.Date dDate) throws YssException{
	   HashMap hmEveStg = null;
       boolean analy1 = false;
       boolean analy2 = false;
       boolean analy3 = false;
       ArrayList all = new ArrayList();
       Iterator iter = null;
       String sKey = "";
       SecurityStorageBean secstorage = null;
       try {
	    	   this.unStgTradeTypeCode =YssOperCons.YSS_JYLX_Sale;//这里不统计业务资料中的02交易类型的数据
	           hmEveStg = getEveStg(dDate);
	           analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
	           analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
	           analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
	
	           refreshTradeCost(dDate, dDate, portCodes);
	           getStgTradeStatData(dDate, hmEveStg, analy1, analy2, analy3);
	           String filterSql=" FInOutType =1 ";
	           getStgSecExchangeData(dDate, hmEveStg, analy1, analy2, analy3, false, filterSql);
	           iter = hmEveStg.keySet().iterator();
	           while (iter.hasNext()) {
	               sKey = (String) iter.next();
	               secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
	               secstorage.setIntStorageState(0); //自动计算（未锁定）
	               secstorage.checkStateId = 1;
	               all.add(secstorage);
	           }
           return all;
       } catch (Exception ex) {
           throw new YssException("统计证券库存，执行部分统计出错！", ex);
       }
   }
    /**
     * 提供债券收益计提所需的证券库存统计
     * 2009.04.25 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发
     * 只统计债券计息所须项
     * @param dDate Date：业务处理日期
     * @return ArrayList：应收应付数据
     * @throws YssException
     */
    public ArrayList getPartStorageStatData1(java.util.Date dDate) throws YssException {
        HashMap hmEveStg = null;
        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;
        ArrayList all = new ArrayList();
        Iterator iter = null;
        String sKey = "";
        SecurityStorageBean secstorage = null;
        try {
            hmEveStg = getEveStg(dDate);
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");

            //2009.08.31 蒋锦 添加 重新计算交易成本的方法，收益计提时需要使用此方法计算出当天买入和卖出的债券利息
            refreshTradeCost(dDate, dDate, portCodes);
            getStgTradeStatData(dDate, hmEveStg, analy1, analy2, analy3);
            getStgSecExchangeData(dDate, hmEveStg, analy1, analy2, analy3, false); //ly 20080204 增加新的证券成本兑换方式

            //更新实际利率 2009.09.04 蒋锦 MS00656:QDV4赢时胜(上海)2009年8月24日01_A
            refreshEffectiveRate(hmEveStg, dDate, analy1, analy2, analy3);

            iter = hmEveStg.keySet().iterator();
            while (iter.hasNext()) {
                sKey = (String) iter.next();
                secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                secstorage.setIntStorageState(0); //自动计算（未锁定）
                secstorage.checkStateId = 1;
                all.add(secstorage);
            }
            return all;
        } catch (Exception ex) {
            throw new YssException("统计证券库存，执行部分统计出错！", ex);
        }
    }
  //add by zhangfa 20100814 MS01446
    /**
     * modify huangqirong 2012-07-20 bug #4940 老券库存转入 新券库存
	 * @方法名：getSecNameExchangeData
	 * @参数：dDate,portCodes
	 * @返回类型：void
	 * @说明：主要正对证券代码变更业务,估值之前,T-1日人为增加新股的库存数据 add by zhangfa 20100814 MS01446
	 */
	public void getSecNameExchangeData(Date dDate, String portCodes) throws YssException {
		//保存证券库存数据
		saveSecurityStock(dDate, portCodes);
		//保存证券应收应付库存数据
		saveSecRecPays(dDate, portCodes);
	}
	/**
	 * 
	 * @方法名：saveSecRecPays
	 * @参数：dDate,portCodes
	 * @返回类型：void
	 * @说明：主要正对证券代码变更业务,人为增加新股T-1证券应收应付库存数据 add by zhangfa 20100814 MS01446
	 */
	 public void saveSecRecPays( java.util.Date dDate,String portCodes) throws YssException{
		// 保存数据之前,先删除旧数据
		// 保存变更后的证券应收应付库存
		boolean bTrans = false;
		String strSql = "";
		String insertSql = "";
		String dsql = "";
		Connection conn = dbl.loadConnection();
		ResultSet rs = null;
		ResultSet rsDetail = null;//add by zhaoxianlin 20130108 BUG #6791 QDV4建信基金2012年12月28日01_B
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement ps = null;
		YssPreparedStatement ps = null;
        //=============end====================
		//---add by songjie 2011.07.22 BUG 2276 QDV4建行2011年07月15日01_B start---//
		String catCode = "";// 品种类型代码
		String subTsfCode = "";//调拨子类型代码
		//---add by songjie 2011.07.22 BUG 2276 QDV4建行2011年07月15日01_B end---//
		try {
			bTrans = true;
			conn.setAutoCommit(false);
	            
	            dsql = "delete from "
					+ pub.yssGetTableName("Tb_Stock_SecRecPay")
					+ " where FSecurityCode in"+
					//edit by songjie 2011.12.02 BUG 3303 QDV4赢时胜(测试)2011年12月2日02_B 证券代码变更删除条件有误  没有根据 业务日期 和 审核状态筛选需要删除的数据
					"(select FSecurityCodeAfter from  "+ pub.yssGetTableName("Tb_Para_SecCodeChange")+" where FBUSINESSDATE = " + dbl.sqlDate(dDate) + " and FCheckState = 1)"
					+ "  and FStorageDate="
					+ dbl.sqlDate(YssFun.addDay(dDate, -1))+ " and FPortCode in(" + portCodes + ")";
	            dbl.executeSql(dsql);
	            
	        //---add by songjie 2011.07.22 BUG 2276 QDV4建行2011年07月15日01_B start---//    
			strSql = " select FCatCode from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FSecurityCode in ( select fsecuritycodeafter from "
					+ pub.yssGetTableName("Tb_Para_SecCodeChange")
					+ " where fbusinessdate =" + dbl.sqlDate(dDate) + ")";
			

			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				catCode = rs.getString("FCatCode");
				//---modified by zhaoxianlin 20130108 BUG #6791 QDV4建信基金2012年12月28日01_B 将注释范围内代码位置移至rs结果集内，以区分品种类型---Start//
				//---add by songjie 2011.07.22 BUG 2276 QDV4建行2011年07月15日01_B end---//    
	            strSql="select fsecuritycodebefore,fsecuritycodeafter ,b.*  from  "+
	                   pub.yssGetTableName("Tb_Para_SecCodeChange")+" a " +
	                   " join (select * from "+ pub.yssGetTableName("tb_stock_SecRecPay") 
	                   +" where FCHECKSTATE =1 and fstoragedate="+dbl.sqlDate(YssFun.addDay(dDate, -1))+
	                   "  and FPortCode in("+portCodes +") ) b on  a.fsecuritycodebefore = b.fsecuritycode "+
	                   " where fbusinessdate="+dbl.sqlDate(dDate)+" and a.FCHECKSTATE =1";
	            rsDetail=dbl.openResultSet_antReadonly(strSql);
              
	            if(!rsDetail.next()){
	            	return;
	            }
	            else{
	            	rsDetail.beforeFirst();
	            }
	            
			insertSql = "insert into "
					+ pub.yssGetTableName("Tb_Stock_SecRecPay")
					+ "(FYearMonth, FStorageDate, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FSecurityCode,"
					+ "FTsfTypeCode, FSubTsfTypeCode, FCuryCode, FBal, FMBal, FVBal, FBaseCuryBal, FMBaseCuryBal, FVBaseCuryBal, "
					+ " FPortCuryBal, FMPortCuryBal, FVPortCuryBal, FStorageInd, FCatType, FAttrClsCode,"
					+ " FCheckState, FCreator, FCreateTime, FCheckUser,FCheckTime,FBalF,FPortCuryBalF,FBaseCuryBalF) "
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//			ps=dbl.getPreparedStatement(insertSql); //modify by fangjiang 2011.08.14 STORY #788
			ps = dbl.getYssPreparedStatement(insertSql);
			//==============end================
			  while(rsDetail.next()){
				  //---add by songjie 2011.07.22 BUG 2276 QDV4建行2011年07月15日01_B start---//
				  subTsfCode = rsDetail.getString("FSubTsfTypeCode");
				  if(!YssFun.right(subTsfCode, 2).equals(catCode)){
					  continue;
				   //subTsfCode = subTsfCode.substring(0,subTsfCode.length() - 2) + catCode;
				  }
				  //---add by songjie 2011.07.22 BUG 2276 QDV4建行2011年07月15日01_B end---//
	               ps.setString(1, rsDetail.getString("FYearMonth"));
	                ps.setDate(2, rsDetail.getDate("FStorageDate"));
	                ps.setString(3, rsDetail.getString("FPortCode"));
	                ps.setString(4,rsDetail.getString("FAnalysisCode1"));
	                ps.setString(5,rsDetail.getString("FAnalysisCode2"));
	                ps.setString(6,rsDetail.getString("FAnalysisCode3"));
	                ps.setString(7, rsDetail.getString("fsecuritycodeafter"));
	                ps.setString(8, rsDetail.getString("FTsfTypeCode"));
	                //add by songjie 2011.07.22 BUG 2276 QDV4建行2011年07月15日01_B
	                ps.setString(9, subTsfCode);
	                //delete by songjie 2011.07.22 BUG 2276 QDV4建行2011年07月15日01_B
//	                ps.setString(9, rs.getString("FSubTsfTypeCode"));
	                ps.setString(10, rsDetail.getString("FCuryCode"));
	                ps.setDouble(11, rsDetail.getDouble("FBal"));
	                ps.setDouble(12, rsDetail.getDouble("FMBal"));
	                ps.setDouble(13, rsDetail.getDouble("FVBal"));
	                ps.setDouble(14, rsDetail.getDouble("FBaseCuryBal"));
	                ps.setDouble(15, rsDetail.getDouble("FMBaseCuryBal"));
	                ps.setDouble(16, rsDetail.getDouble("FVBaseCuryBal"));
	                ps.setDouble(17, rsDetail.getDouble("FPortCuryBal"));
	                ps.setDouble(18, rsDetail.getDouble("FMPortCuryBal"));
	                ps.setDouble(19, rsDetail.getDouble("FVPortCuryBal"));
	                ps.setInt(20, rsDetail.getInt("FStorageInd"));
	                ps.setString(21, rsDetail.getString("FCatType"));
	                ps.setString(22, rsDetail.getString("FAttrClsCode"));
	                ps.setInt(23, rsDetail.getInt("FCheckState"));
	                ps.setString(24, rsDetail.getString("FCreator"));
	                ps.setString(25, rsDetail.getString("FCreateTime"));
	                ps.setString(26,rsDetail.getString("FCheckUser"));
	                ps.setString(27,rsDetail.getString("FCheckTime"));
	                ps.setDouble(28, rsDetail.getDouble("FBalF"));
	                ps.setDouble(29, rsDetail.getDouble("FPortCuryBalF"));
	                ps.setDouble(30, rsDetail.getDouble("FBaseCuryBalF"));
	              
	                ps.executeUpdate();
	                
			  }
			  dbl.closeResultSetFinal(rsDetail);
			}
               //---modified by zhaoxianlin 20130108 BUG #6791 QDV4建信基金2012年12月28日01_B 将注释范围内代码位置移至rs结果集内，已区分品种类型---end//
			   conn.commit();
			   conn.setAutoCommit(true);
               bTrans = false; 
	        }catch (Exception ex) {
	                throw new YssException("保存变更后的证券应收应付库存出错", ex);
	            } finally {
	            	
	            	dbl.closeStatementFinal(ps);
	    			dbl.closeResultSetFinal(rs);
	                dbl.endTransFinal(conn, bTrans);
	                
	            }
	 }
	 /**
	  * 
	  * @方法名：saveSecurityStock
	  * @参数：dDate,portCodes
	  * @返回类型：void
	  * @说明：主要正对证券代码变更业务,人为增加新股T-1证券库存数据 add by zhangfa 20100814 MS01446
	  */
    public void saveSecurityStock( java.util.Date dDate,String portCodes) throws YssException{
    	//保存数据之前,先删除旧数据
    	//保存变更后的证券库存
    	boolean bTrans = false;
    	String strSql = "";
    	String insertSql="";
    	String dsql="";
    	Connection conn = dbl.loadConnection();
        ResultSet rs = null;	
        //modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement ps = null;
        YssPreparedStatement ps = null;
        //=============end====================
        try {
            
            
            dsql = "delete from "
				+ pub.yssGetTableName("Tb_Stock_Security")
				+ " where FSecurityCode in"+
				//edit by songjie 2011.12.02 BUG 3303 QDV4赢时胜(测试)2011年12月2日02_B 证券代码变更删除条件有误  没有根据 业务日期 和 审核状态筛选需要删除的数据
				"(select FSecurityCodeAfter from  "+ pub.yssGetTableName("Tb_Para_SecCodeChange")+ " where FBUSINESSDATE = " + dbl.sqlDate(dDate) + " and FCheckState = 1 ) "
				+ "  and FStorageDate="
				+ dbl.sqlDate(YssFun.addDay(dDate, -1))+ " and FPortCode in(" + portCodes + ")";
            dbl.executeSql(dsql);
            
            strSql = " select fsecuritycodebefore,fsecuritycodeafter,b.*  from  "+
                   pub.yssGetTableName("Tb_Para_SecCodeChange")+" a " +
                   " join (select * from "+ pub.yssGetTableName("tb_stock_security") 
                   +" where FCHECKSTATE =1 and fstoragedate="+dbl.sqlDate(YssFun.addDay(dDate, -1))+
                   "  and FPortCode in("+portCodes +")) b on  a.fsecuritycodebefore = b.fsecuritycode "+
                   " where fbusinessdate="+dbl.sqlDate(dDate)+" and a.FCHECKSTATE =1";
            rs = dbl.openResultSet_antReadonly(strSql);
            
            if(!rs.next()){
            	return ;
            }
            else{
            	rs.beforeFirst();
            }
            
            bTrans = true;
            conn.setAutoCommit(false);
            
			insertSql = "insert into "
					+ pub.yssGetTableName("Tb_Stock_Security")
					+ "(FSecurityCode,FYearMonth,FStorageDate, FPortCode,FCuryCode,FStorageAmount,FStorageCost, FFreezeAmount,"
					+ " FMStorageCost,FVStorageCost,FPortCuryRate,FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FBaseCuryRate,FBaseCuryCost,"
					+ "FMBaseCuryCost,FVBaseCuryCost,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FStorageInd,FCatType,FAttrClsCode,"
					+ " FCheckState, FCreator, FCreateTime,FCheckUser,FInvestType) "
					+" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//			ps=dbl.getPreparedStatement(insertSql)	; //modify by fangjiang 2011.08.14 STORY #788	
			ps = dbl.getYssPreparedStatement(insertSql);
			//==============end================
			
			
            
            while(rs.next()){
				
               ps.setString(1,rs.getString("fsecuritycodeafter") );
               ps.setString(2,rs.getString("FYearMonth") );
               ps.setDate(3, rs.getDate("FStorageDate"));
               ps.setString(4, rs.getString("FPortCode"));
               ps.setString(5, rs.getString("FCuryCode"));
               ps.setDouble(6, rs.getDouble("FStorageAmount"));
               ps.setDouble(7,rs.getDouble("FStorageCost"));
               ps.setDouble(8,rs.getDouble("FFreezeAmount"));
               ps.setDouble(9,rs.getDouble("FMStorageCost"));
               ps.setDouble(10,rs.getDouble("FVStorageCost"));
               ps.setDouble(11,rs.getDouble("FPortCuryRate"));
               ps.setDouble(12,rs.getDouble("FPortCuryCost"));
               ps.setDouble(13,rs.getDouble("FMPortCuryCost"));
               ps.setDouble(14,rs.getDouble("FVPortCuryCost"));
               ps.setDouble(15,rs.getDouble("FBaseCuryRate"));
               ps.setDouble(16,rs.getDouble("FBaseCuryCost"));
               ps.setDouble(17,rs.getDouble("FMBaseCuryCost"));
               ps.setDouble(18,rs.getDouble("FVBaseCuryCost"));
               ps.setString(19,rs.getString("FAnalysisCode1") );
               ps.setString(20,rs.getString("FAnalysisCode2") );
               ps.setString(21, rs.getString("FAnalysisCode3"));
               ps.setInt(22, rs.getInt("FStorageInd"));
               ps.setString(23,rs.getString("FCatType") );
               ps.setString(24, rs.getString("FAttrClsCode"));
               ps.setInt(25,rs.getInt("FCheckState") );
               ps.setString(26,rs.getString("FCreator") );
               ps.setString(27, rs.getString("FCreateTime"));
               ps.setString(28, rs.getString("FCheckUser"));
               ps.setString(29, rs.getString("FInvestType"));
               
               ps.executeUpdate();
               
            }
            conn.commit();
			conn.setAutoCommit(true);
            bTrans = false;  
        } catch (Exception ex) {
            throw new YssException("保存变更后的证券库存出错", ex);
        } finally {
        	
        	dbl.closeStatementFinal(ps);
			dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
            
        }
    }
    //MS01336 add by zhangfa 20100820
    private void  getDeleteBondInterestReceive(java.util.Date dDate) throws YssException{
		ResultSet rs = null;
		//String sql = "";//delete by songjie 2012.10.16 修改国内债券业务问题
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		SecPecPayBean secPay = null;
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement ps = null;
		YssPreparedStatement ps = null;
        //=============end====================
		
		SecRecPayAdmin secPayAdmin = new SecRecPayAdmin();
		EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
		double BaseCuryRate = 0;
		double PortCuryRate = 0;
		double dPortMoney = 0;
		//add by songjie 2012.10.16 修改国内债券业务问题
		StringBuffer sb = new StringBuffer();
    	 try{
    		 //---add by songjie 2012.10.16 修改国内债券业务问题  start---//
    		 //国内债券派息交易日应生成02FI债券利息收入数据，而不是结算日生成，
    		 //现改为若为国内交易所债券派息，则在交易日生成02FI数据
    		 //若为非国内交易所债券派息，则在结算日生成02FI数据
    		 //edit by songjie 2013.05.07 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 实收实付金额  改为  应计利息
    		 sb.append(" select a.FPortCode, a.FSecurityCode, a.FAccruedInterest, ")
    		 .append(" a.FInvMgrCode, a.FBrokerCode, a.FAttrClsCode, ")
    		 .append(" a.FInvestType, b.FTradeCury, b.FExchangeCode from (select * from ")
    		 .append(pub.yssGetTableName("Tb_Data_SubTrade"))
    		 .append(" where FBargainDate = " + dbl.sqlDate(YssFun.formatDate(dDate,"yyyy-MM-dd")))
    		 .append(" and FTradeTypeCode = '88' and FCheckState = 1 ")
    		 
    		 /**Start 20130709 modified by liubo.Story #4144.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001*/
    		 .append(" and FSettleState in (0,1)")
    		 /**End 20130709 modified by liubo.Story #4144.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001*/
    		 
    		 .append(" and FPortCode in (" + this.portCodes + ")) a ")
    		 .append(" join (select FSecurityCode, FTradeCury, FExchangeCode from ")
    		 .append(pub.yssGetTableName("Tb_Para_Security"))
    		 .append(" where FCheckState = 1 ")
    		 //--- delete by songjie 2012.05.10 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
    		 //债券派息不区分境内境外 都是交易日生成02FI债券利息收入数据
    		 //.append(" and FExchangeCode in ('CG', 'CS') ")
    		 //--- delete by songjie 2012.05.10 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
    		 .append(" ) b on a.FSecurityCode = b.FSecurityCode ");
    		 //--- delete by songjie 2012.05.10 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
    		 //债券派息不区分境内境外 都是交易日生成02FI债券利息收入数据
//    		 .append(" union all ")
//    		 //edit by songjie 2013.05.07 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 实收实付金额  改为  应计利息
//    		 .append(" select a1.FPortCode, a1.FSecurityCode, a1.FAccruedInterest, ")
//    		 .append(" a1.FInvMgrCode, a1.FBrokerCode, a1.FAttrClsCode, ")
//    		 .append(" a1.FInvestType, b1.FTradeCury, b1.FExchangeCode from (select * from ")
//    		 .append(pub.yssGetTableName("Tb_Data_SubTrade"))
//    		 .append(" where FSettleDate = " + dbl.sqlDate(YssFun.formatDate(dDate,"yyyy-MM-dd")))
//    		 .append(" and FTradeTypeCode = '88' and FCheckState = 1 ")
//    		 .append(" and FSettleState = 1 and FPortCode in (" + this.portCodes + ")) a1 ")
//    		 .append(" join (select FSecurityCode, FTradeCury, FExchangeCode from ")
//    		 .append(pub.yssGetTableName("Tb_Para_Security"))
//    		 .append(" where FCheckState = 1 and FExchangeCode not in ('CG', 'CS')) b1 ")
//    		 .append(" on a1.FSecurityCode = b1.FSecurityCode ")
    		 //--- delete by songjie 2012.05.10 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
    		 
    		 rs=dbl.queryByPreparedStatement(sb.toString());
    		 //---add by songjie 2012.10.16 修改国内债券业务问题  end---//
    		 
    		 //---delete by songjie 2012.10.16 修改国内债券业务问题  start---//
//    		 sql="select a.*,b.* from (select * from "+pub.yssGetTableName("Tb_Data_SubTrade")+" where FSettleDate="+dbl.sqlDate(YssFun.formatDate(dDate))
//    		    +" and FTradeTypeCode='88' and FCheckState=1 and FSettleState=1 "+" and FPortCode in( "+ this.portCodes +")  ) a "
//    		    +" join (select * from "+pub.yssGetTableName("Tb_Para_Security")+" where FCheckState=1 ) b on a.FSecurityCode=b.FSecurityCode";
//    		rs=dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
    		 //---delete by songjie 2012.10.16 修改国内债券业务问题  end---//
    				
    		while(rs.next()){
    			 secPay = new SecPecPayBean();
			        secPay.setStrPortCode(rs.getString("FPortCode"));       //组合代码
			        secPay.setStrSecurityCode(rs.getString("FSecurityCode"));
			        secPay.setStrCuryCode(rs.getString("FTradeCury"));       //货币代码
			        secPay.setInOutType(1);         //流入流出方向,默认为正方向
			        secPay.setTransDate(dDate) ;           //业务日期
			        secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
			        secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FI_Income);

			        //--- edit by songjie 2013.05.07 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 实收实付金额  改为  应计利息 start---//
			        secPay.setMoney(rs.getDouble("FAccruedInterest"));//核算金额
			        secPay.setMMoney(rs.getDouble("FAccruedInterest"));
			        secPay.setVMoney(rs.getDouble("FAccruedInterest"));
			        //--- edit by songjie 2013.05.07 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 实收实付金额  改为  应计利息 end---//
			        
					BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
							rs.getString("FTradeCury"), rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);// 获取当日的基础汇率

					secPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
							secPay.getMoney(), BaseCuryRate));
			        secPay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
							secPay.getMoney(), BaseCuryRate));
			        secPay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
							secPay.getMoney(), BaseCuryRate));
			        
					rateOper.setYssPub(pub);
					rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"), rs.getString("FPortCode"));
					PortCuryRate = rateOper.getDPortRate(); // 获取当日的组合汇率
					
					dPortMoney = this.getSettingOper().calPortMoney(
							secPay.getMoney(), BaseCuryRate, PortCuryRate, rs.getString("FTradeCury"),
							dDate, rs.getString("FPortCode")); // 计算组合货币金额
			        
			        secPay.setPortCuryMoney(dPortMoney);
			        secPay.setMPortCuryMoney(dPortMoney);
			        secPay.setVPortCuryMoney(dPortMoney);
			        
			        secPay.setInvMgrCode(rs.getString("FInvMgrCode"));
			        secPay.setBrokerCode(rs.getString("FBrokerCode"));
			        
			        secPay.setAttrClsCode(rs.getString("FAttrClsCode"));
			        secPay.setInvestType(rs.getString("FInvestType"));
			        secPay.setRelaNumType("BondInterest");//债券派息关联类型,用作重复估值时的删除条件,避免误删系统产生的其他同类数据
                    secPay.setTransDate(dDate);
                    secPay.setBaseCuryRate(BaseCuryRate);
                    secPay.setPortCuryRate(PortCuryRate);
                    
                    secPay.setCheckState(1);
                    
                    secPayAdmin.addList(secPay);
    		}
            secPayAdmin.setYssPub(pub);
            secPayAdmin.insert("",dDate, dDate, YssOperCons.YSS_ZJDBLX_Income , YssOperCons.YSS_ZJDBZLX_FI_Income,
            		           //edit by songjie 2012.10.15 资产估值重复生成 02FI 的债券利息收入数据
                               portCodes,"","","","", -99,true,1,false,"","","BondInterest");
    	 }catch (Exception e) {
 			throw new YssException("生成债券派息应收应付数据出错！", e);
 		} finally {
 			dbl.closeResultSetFinal(rs);//关闭游标 by leeyu 20100909
 			dbl.closeStatementFinal(ps);
 			dbl.endTransFinal(conn, bTrans);
 		}
    }
    //-------------------------------
	//--------------------------------------------------------------------
    public ArrayList getStorageStatData(java.util.Date dDate) throws
        YssException {
        ArrayList all = new ArrayList();
        ResultSet rs = null;

        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;

        HashMap hmEveStg = null;

        Iterator iter = null;
        String sKey = "";

        Hashtable amzFi = null;
        //add by zhangfa 证券借贷业务-库存统计 20101116
        Hashtable secLend=null;
        //--------------end--------------------------
        boolean bIsSellTradeRelaCal = false; //edit by jc 判断否计算卖出估值增值用
        
        SecurityStorageBean secstorage = null;
        
        String [] sDifferencePortCode = null;//xuqiji 20091207 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A 获取有条件的组合代码,主要是针对各家公司的补票方式不同而进行不同的处理方式
        // --- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 ---------------------------
        CtlPubPara pubPara1 = null;
		boolean bTPCost = false;// 区分是太平资产的库存统计还是QDII的库存统计,合并版本时调整
		String sPara_tp = "";
		// --- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 end-------------------------
        try {
       	//add by zhangfa 20100814 MS01446  证券变更业务,使得T-1日新股有库存
	  	    //edit by songjie 2011.12.31 BUG 3491 QDV4海富通2011年12月22日01_B
		    //判断是否是在资产估值时做的证券库存统计，
        	//若是的话，则生成变更后的证券对应的库存数据
        	
        	//modified by liubo.Bug #4878
        	//此前的4367BUG注释掉了此段代码，导致T-1日有新股时应收应付库存统计不对/
        	//================================
        	//modify by zhouwei 20120428 bug4367 
        	if(this.stgFrom.equals("Valuation"))
        	{
        		getSecNameExchangeData(dDate, portCodes);
        	}
        	//=================end===============
        //------------------------------------------------------------
        	
        // --- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 ---------------------------
        	pubPara1 = new CtlPubPara();
			pubPara1.setYssPub(pub);
			sPara_tp = pubPara1.getNavType();// 通过净值表类型来判断
			if (sPara_tp != null && sPara_tp.trim().equalsIgnoreCase("new")) {
				bTPCost = false;// 国内QDII统计模式
			} else {
				bTPCost = true;// 太平资产统计模式
			}
			if (bYearChange) {
				if (bTPCost) {
					// 太平资产年终结账方式：根据09、14、18报表产生虚拟账户的期初数和证券的特殊处理
					yearChange_TP(dDate, portCodes);
				} else {
					// QDII年终结账方式
					yearChange(dDate, portCodes);
				}
			}
		// --- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 end------------------------

            //081010 edit by jc 分组合从通用参数获取是否计算卖出估值增值
            String[] arryPort = YssFun.split(portCodes, ",");
            for (int i = 0; i < arryPort.length; i++) {
                CtlPubPara pubPara = new CtlPubPara();
                pubPara.setYssPub(pub);
                bIsSellTradeRelaCal = pubPara.getIsSellTradeRelaCal(arryPort[i]);
                if (bIsSellTradeRelaCal) {
                    //------------- 2008.08.22 蒋锦添加统计卖出估值增值--------------//
                    getStockSellTradeRela(dDate, arryPort[i]);
                    //------------------------------------------------------------//
                } else {
                    //删除卖出交易关联表和证券应收应付表中已有数据,保证在选择不计算卖出估值增值时，能正常统计
                    deleteDataByDate(dDate, arryPort[i]);
                }
            }
            //-----------------------------------081010 jc
            hmEveStg = getEveStg(dDate);
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");

            /**shashijie 2011.05.03 STORY #535 需修改指数调整后冲减的的相关类型*/
            createSecuritiesCount(dDate, hmEveStg, analy1, analy2, analy3,portCodes);
            /**end*/
            
            /**shashijie 2012-5-10 STORY 2565 易方达ETF联接基金投资ETF基金卖出估值增值 */
			doOperionAppraise(dDate,portCodes);
			/**end*/
            
            //重新计算交易成本
            if (bReCost) {
                refreshTradeCost(dDate, dDate, portCodes);
                refreshBLTradeCost(dDate, dDate, portCodes);//add by zhouxiang 2010.12.1 证券借贷重新计算成本
                //deleted by liubo.Bug #5051
                //==========================
//                refreshIntegratedSecurityCost(dDate, dDate, portCodes);//story 1936 by zhouwei 20111224 综合业务证券成本重新计算成本
                //==========end================
            }
            //-----------------统计股指期货库存-----------------//  核算成本
            getFuturesTradeStatData(dDate, portCodes);
            //------------------------------------------------//
            
            //----------------配股缴款业务在除权日才统计证券库存-----------//
            getExRightsData(dDate, hmEveStg, analy1, analy2, analy3,portCodes);  //story 2538 modify by zhouwei 20120521 配股缴款数据不入库存
            //-----------------------------------------------------------//
            //获取有条件的组合代码,主要是针对各家公司的补票方式不同而进行不同的处理方式  xuqiji 20091207 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
            if(this.isBETFStat()){
            	sDifferencePortCode = this.getDifferencePortCode();
            }
            if(this.isBETFStat()){
            	if(sDifferencePortCode[1].trim().length()>0){
            		amzFi = getStgETFTradeStatData(dDate, hmEveStg, analy1, analy2, analy3,sDifferencePortCode[1]);
            	}else if(sDifferencePortCode[0].trim().length()>0){
            		
            		amzFi = getStgTradeStatData(dDate, hmEveStg, analy1, analy2, analy3,sDifferencePortCode[0]);
            	}
            }else{
            	amzFi = getStgTradeStatData(dDate, hmEveStg, analy1, analy2, analy3);
            }
            //add by zhangfa 20101117 证券借贷业务-证券借贷库存统计
            //secLend=getStgTradeSecLendStatData(dDate, hmEveStg, analy1, analy2, analy3,this.portCodes); //modify by fangjiang 2011.11.23 story 1433
            //--------------end----------------------------------
            getAmortizationCost(dDate, hmEveStg, analy1, analy2, analy3, amzFi); //摊销冲抵成本。
            //MS01336 add vy zhangfa 20100820 冲减应收债券利息
            getDeleteBondInterestReceive(dDate);
            //-------------------------------
                                

            //--- sj modifid 20090618 ----------------------------------------------------
            getStgPurchaseStatData(dDate, hmEveStg, analy1, analy2, analy3); //回购交易的成本。
            //----------------------------------------------------------------------------

            getStgForwardStatData(dDate, hmEveStg, analy1, analy2, analy3);
            getStgSecExchangeStatData(dDate, hmEveStg, analy1, analy2, analy3);
            getStgTradeRelaData(dDate, hmEveStg, analy1, analy2, analy3); // sj 20071204 add
            getStgSecExchangeData(dDate, hmEveStg, analy1, analy2, analy3, true); //ly 20080204 增加新的证券成本兑换方式
            
            //---
            //getStgPurData(dDate, hmEveStg, analy1, analy2, analy3,this.portCodes);
            //--------------
            
            //--- sj add 20090907 --------------------------------------------------------
            getStgPurchaseBankData(dDate, hmEveStg, analy1, analy2, analy3); //冲减成本
            //----------------------------------------------------------------------------
            setPortStorageFreezeAmount(dDate, hmEveStg); //leeyu 20090110 处理送股后的冻结数量MS00125

            //更新实际利率 蒋锦 20090904 MS00656:QDV4赢时胜(上海)2009年8月24日01_A
            refreshEffectiveRate(hmEveStg, dDate, analy1, analy2, analy3);

            iter = hmEveStg.keySet().iterator();
            while (iter.hasNext()) {
                sKey = (String) iter.next();
                secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                secstorage.setIntStorageState(0); //自动计算（未锁定）
                secstorage.checkStateId = 1;
            	if(YssFun.toDouble(secstorage.getStrStorageAmount())==0)
            		//添加如果债券的数量为0处理应收应付的数据 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
            		stgSecurityFISecPay(secstorage);
            	    
            	    //如果回购的数量为0，则所有成本都清为零 
            	    stgSecurityRE(secstorage);//add by songjie 2011.10.10 BUG 2701 QDV4泰达2011年09月13日01_B
                all.add(secstorage);
            }
            return all;
        } catch (Exception e) {
            throw new YssException("系统进行库存统计,在统计股指期货库存时出现异常!\n", e); //by caocheng 2009.02.05统计股指期货库存异常信息
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**shashijie 2012-5-10 STORY 2565
	* @param dDate
	* @param portCodes*/
	private void doOperionAppraise(Date dDate, String portCodes) throws YssException {
		Map map = new HashMap();
		//赎回失败冲减卖出估值增值
		doInfoFailure(dDate,portCodes,map);
		//赎回卖出估值增值
		doInfoRedemption(dDate,portCodes,map);
		//卖出估值增值
		doInfoSell(dDate,portCodes,map);
		//生成证券应收应付数据
        createSecurities(map,dDate);
	}

	/**shashijie 2012-5-11 STORY 2565 生成证券应收应付数据 */
	private void createSecurities(Map map, Date dDate) throws YssException {
		//证券应收应付类
		SecRecPayAdmin secPayAdmin = new SecRecPayAdmin();
		secPayAdmin.setYssPub(pub);
		//组合
		String FPortCode = "";
		Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String sKey = (String) it.next();
            FPortCode = sKey.split(",")[1];//组合代码
            //应收应付Bean
    		SecPecPayBean secpecpay = (SecPecPayBean) map.get(sKey);
            secPayAdmin.addList(secpecpay);
            //System.out.println(sKey+"~~~~~~证券~~~~~"+map.get(sKey)+"~~~~~~~~~~~~~~~~~HashMap值");
        }
        if (!map.isEmpty()) {
            secPayAdmin.insert(dDate, dDate, YssOperCons.YSS_YWLX_ETFMC, 
            		YssOperCons.YSS_YWLX_ETFSHGZ+","+YssOperCons.YSS_YWLX_ETFSHSBGZ+","+
            		YssOperCons.YSS_YWLX_ETFMCGZ, FPortCode, -1);
		}
	}

	/**shashijie 2012-5-24 STORY 2565 ETF联接基金卖出估值增值 */
	private void doInfoSell(Date dDate, String portCodes, Map map) throws YssException {
		ResultSet rs = null;
		try {
			String query = getSellQuery(dDate,portCodes);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//获取赎回冲减估增
				String oldKey = rs.getString("FSecurityCode") + "," +rs.getString("FPortCode")+",2";
				SecPecPayBean secpec = (SecPecPayBean)map.get(oldKey);
				//key = 证券代码 + 组合代码 + 标示//表示是赎回失败的卖出估值增值
				String key = rs.getString("FSecurityCode") + "," +rs.getString("FPortCode")+",3";
				//计算卖出估增 = (T-1日库存估增 + T日赎回冲减估增) / (T-1库存数量 + T日流入数量(申赎+申赎冲减) + T日赎回冲减数量) * 赎回数量
				BigDecimal money = getMoney(new BigDecimal(0),//赎回卖出估增(倒钆)
						rs.getBigDecimal("Fbal"),//日库存估增
						secpec == null? new BigDecimal(0) : new BigDecimal(YssD.mul(secpec.getMoney(),-1)),//赎回冲减估增,这里要传正的
						rs.getBigDecimal("Fstorageamount"),//库存数量
						rs.getBigDecimal("Bamount"),//流入数量
						YssD.mulD(rs.getBigDecimal("Samount"),new BigDecimal(-1)),//赎回冲减数量,这里要传正的
						rs.getBigDecimal("FTradeAmount")//赎回数量
						);
				
				//证券应收应付Bean
	    		SecPecPayBean secpecpay = new SecPecPayBean();
	    		secpecpay.setYssPub(pub);
	            setSecPecPayBean(secpecpay,rs.getString("FSecurityCode"),
	            		money,rs.getString("FPortCode"),dDate,YssOperCons.YSS_YWLX_ETFMC,
	            		YssOperCons.YSS_YWLX_ETFMCGZ);
	            map.put(key, secpecpay);//存入map
			}
		} catch (Exception e) {
			throw new YssException("处理赎回卖出估值增值数据出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2012-5-24 STORY 2565 获取ETF联接,卖出交易数据*/
	private String getSellQuery(Date dDate, String portCodes) throws YssException {
		String query = 
			" Select a.*,"+
			" d.Fstorageamount,"+//库存数量
			" Nvl(d.Fbal, 0) Fbal,"+//日库存估增
			" Nvl(e.Bamount, 0) Bamount,"+//流入数量
			" Nvl(g.Samount, 0) Samount "+//赎回失败数量
			" From (Select A1.Fportcode," +
			" A1.Fsecuritycode," +
			" A1.Fbargaindate," +
			" A1.Ftradeamount,"+//卖出数量
			" A1.FTradetypeCode"+
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" A1"+
			" Where A1.Fcheckstate = 1" +
		    //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
			//" And A1.Fsettlestate = 1" +
			" And A1.Fbargaindate = "+dbl.sqlDate(dDate)+
			" And A1.Fportcode In ("+operSql.sqlCodes(portCodes)+")" +
			" And A1.Ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_JYLX_Sale)+") a" +
			//交易类型
			" Join (Select B1.Famountind, B1.Ftradetypecode" +
			" From Tb_Base_Tradetype B1" +
			" Where B1.Famountind = -1 ) b On a.Ftradetypecode = b.Ftradetypecode" +
			//证券
			" Join (Select F1.Fsecuritycode, F1.Fnormscale" +
			" From "+pub.yssGetTableName("Tb_Para_Security")+" F1" +
			" Where Nvl(F1.Fnormscale, 0) > 0) c On c.Fsecuritycode = a.Fsecuritycode" +
			//库存数量,估值增值
			" Join (Select Nvl(C1.Fstorageamount, 0) Fstorageamount," +
			" Nvl(D2.Fbal, 0) Fbal," +
			" C1.Fsecuritycode," +
			" C1.Fstoragedate" +
			" From (Select Sum(T1.Fstorageamount) Fstorageamount," +
			" T1.Fsecuritycode," +
			" T1.Fstoragedate" +
			" From "+pub.yssGetTableName("Tb_Stock_Security")+" T1" +
			" Where T1.Fcheckstate = 1" +
			/**add---huhuichao 2013-12-26 BUG #86469 系统在做库存统计的时候选择多个组合的时候报错 */
			/**add---huhuichao 2013-11-22 STORY  13644 卖出估值增值计算不正确*/
			" and T1.Fportcode in (" +operSql.sqlCodes(portCodes)+")" +
			/**end---huhuichao 2013-11-22 STORY  13644*/
			/**end---huhuichao 2013-12-26 BUG #86469*/
			" And T1.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1)) +
			" Group By T1.Fsecuritycode, T1.Fstoragedate) C1" +
			" Left Join (Select Sum(D1.Fbal) Fbal," +
			" D1.Fsecuritycode," +
			" D1.Fstoragedate" +
			" From "+pub.yssGetTableName("Tb_Stock_Secrecpay")+" D1" +
			" Where (D1.Fsubtsftypecode Like '09%' Or D1.Fsubtsftypecode Like '9905%' Or" +
			" D1.Fsubtsftypecode Like '9909%')" +
			/**add---huhuichao 2013-12-26 BUG #86469 系统在做库存统计的时候选择多个组合的时候报错 */
			/**add---huhuichao 2013-11-22 STORY  13644 卖出估值增值计算不正确*/
			" and D1.FPORTCODE in (" +operSql.sqlCodes(portCodes)+")" +
			/**end---huhuichao 2013-11-22 STORY  13644 */
			/**end---huhuichao 2013-12-26 BUG #86469  */
			" Group By D1.Fsecuritycode, D1.Fstoragedate) D2 On C1.Fsecuritycode = D2.Fsecuritycode"+
			" And c1.FStorageDate = d2.FStorageDate "+
			//Where C1.Fsecuritycode = '510990'
			
			" ) d On a.FSecurityCode = d.FSecurityCode " +
			//流入
			" Left Join (Select Sum(E1.Ftradeamount) Bamount, E1.Fsecuritycode" +
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" E1" +
			" Join Tb_Base_Tradetype b On E1.Ftradetypecode = b.Ftradetypecode" +
			" Where b.Famountind = 1 " +//数量流入
			"  And E1.Fcheckstate = 1" +
		    //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
			//" And E1.Fsettlestate = 1" +
			" And E1.Fbargaindate = "+dbl.sqlDate(dDate)+
			" And E1.Fportcode In ("+operSql.sqlCodes(portCodes)+")" +
			" Group By E1.Fsecuritycode) e On a.Fsecuritycode = e.Fsecuritycode" +
			//失败数量
			" Left Join (Select E1.Ftradeamount Samount," +
			" E1.Fsecuritycode," +
			" E1.Fportcode," +
			" E1.Fbargaindate" +
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" E1" +
			" Where E1.Ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_JYLX_ETFLJSHSB)+//赎回失败冲减
			" And E1.Fcheckstate = 1" +
		    //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
			//" And E1.Fsettlestate = 1" +
			" And E1.Fportcode In ("+operSql.sqlCodes(portCodes)+")" +
			" And E1.Fbargaindate = "+dbl.sqlDate(dDate)+
			" ) g On a.Fsecuritycode = g.Fsecuritycode " +
			" And a.Fportcode = g.Fportcode And a.Fbargaindate = g.Fbargaindate";
		return query;
	}

	/**shashijie 2012-5-11 STORY 2565 赎回卖出估值增值  */
	private void doInfoRedemption(Date dDate, String portCodes, Map map) throws YssException {
		ResultSet rs = null;
		try {
			String query = getSHMCGZZZQuery(dDate,portCodes);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//获取赎回冲减估增
				String oldKey = rs.getString("FSecurityCode") + "," +rs.getString("FPortCode")+",2";
				SecPecPayBean secpec = (SecPecPayBean)map.get(oldKey);
				//key = 证券代码 + 组合代码 + 标示//表示是赎回失败的卖出估值增值
				String key = rs.getString("FSecurityCode") + "," +rs.getString("FPortCode")+",1";
				//计算卖出估增 = (T-1日库存估增 + T日赎回冲减估增) / (T-1库存数量 + T日流入数量(申赎+申赎冲减) + T日赎回冲减数量) * 赎回数量
				BigDecimal money = getMoney(new BigDecimal(0),//赎回卖出估增(倒钆)
						rs.getBigDecimal("Fbal"),//日库存估增
						secpec == null? new BigDecimal(0) : new BigDecimal(YssD.mul(secpec.getMoney(),-1)),//赎回冲减估增,这里要传正的
						rs.getBigDecimal("Fstorageamount"),//库存数量
						rs.getBigDecimal("Bamount"),//流入数量
						YssD.mulD(rs.getBigDecimal("Samount"),new BigDecimal(-1)),//赎回冲减数量,这里要传正的
						rs.getBigDecimal("FTradeAmount")//赎回数量
						);
				
				//证券应收应付Bean
	    		SecPecPayBean secpecpay = new SecPecPayBean();
	    		secpecpay.setYssPub(pub);
	            setSecPecPayBean(secpecpay,rs.getString("FSecurityCode"),
	            		money,rs.getString("FPortCode"),dDate,YssOperCons.YSS_YWLX_ETFMC,
	            		YssOperCons.YSS_YWLX_ETFSHGZ);
	            map.put(key, secpecpay);//存入map
			}
		} catch (Exception e) {
			throw new YssException("处理赎回卖出估值增值数据出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2012-5-11 STORY 2565 赎回卖出估值增值 */
	private String getSHMCGZZZQuery(Date dDate, String portCodes) {
		String query = 
			" Select a.*,"+
			" d.Fstorageamount,"+//库存数量
			" Nvl(d.Fbal, 0) Fbal,"+//日库存估增
			" Nvl(e.Bamount, 0) Bamount,"+//流入数量
			" Nvl(g.Samount, 0) Samount "+//赎回失败数量
			" From (Select A1.Fportcode," +
			" A1.Fsecuritycode," +
			" A1.Fbargaindate," +
			" A1.Ftradeamount,"+//赎回数量
			" A1.FTradetypeCode"+
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" A1"+
			" Where A1.Fcheckstate = 1" +
		    //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
			//" And A1.Fsettlestate = 1" +
			" And A1.Fbargaindate = "+dbl.sqlDate(dDate)+
			" And A1.Fportcode In ("+operSql.sqlCodes(portCodes)+")" +
			" And A1.Ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_JYLX_ETFSH)+") a" +
			//交易类型
			" Join (Select B1.Famountind, B1.Ftradetypecode" +
			" From Tb_Base_Tradetype B1" +
			" Where B1.Famountind = -1 ) b On a.Ftradetypecode = b.Ftradetypecode" +
			//证券
			" Join (Select F1.Fsecuritycode, F1.Fnormscale" +
			" From "+pub.yssGetTableName("Tb_Para_Security")+" F1" +
			" Where Nvl(F1.Fnormscale, 0) > 0) c On c.Fsecuritycode = a.Fsecuritycode" +
			//库存数量,估值增值
			" Join (Select Nvl(C1.Fstorageamount, 0) Fstorageamount," +
			" Nvl(D2.Fbal, 0) Fbal," +
			" C1.Fsecuritycode," +
			" C1.Fstoragedate" +
			" From (Select Sum(T1.Fstorageamount) Fstorageamount," +
			" T1.Fsecuritycode," +
			" T1.Fstoragedate" +
			" From "+pub.yssGetTableName("Tb_Stock_Security")+" T1" +
			" Where T1.Fcheckstate = 1" +
			" And T1.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1)) +
			" and T1.Fportcode in (" + operSql.sqlCodes(portCodes) +
			" ) Group By T1.Fsecuritycode, T1.Fstoragedate) C1" +
			" Left Join (Select Sum(D1.Fbal) Fbal," +
			" D1.Fsecuritycode," +
			" D1.Fstoragedate" +
			" From "+pub.yssGetTableName("Tb_Stock_Secrecpay")+" D1" +
			" Where (D1.Fsubtsftypecode Like '09%' Or D1.Fsubtsftypecode Like '9905%' Or" +
			" D1.Fsubtsftypecode Like '9909%') and FPortCode in (" + operSql.sqlCodes(portCodes) +
			" ) Group By D1.Fsecuritycode, D1.Fstoragedate) D2 On C1.Fsecuritycode = D2.Fsecuritycode"+
			" And c1.FStorageDate = d2.FStorageDate "+
			//Where C1.Fsecuritycode = '510990'
			
			" ) d On a.FSecurityCode = d.FSecurityCode " +
			//流入
			" Left Join (Select Sum(E1.Ftradeamount) Bamount, E1.Fsecuritycode" +
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" E1" +
			" Join Tb_Base_Tradetype b On E1.Ftradetypecode = b.Ftradetypecode" +
			" Where b.Famountind = 1 " +//数量流入
			" And E1.Fcheckstate = 1" +
		    //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
			//" And E1.Fsettlestate = 1" +
			" And E1.Fbargaindate = "+dbl.sqlDate(dDate)+
			" And E1.Fportcode In ("+operSql.sqlCodes(portCodes)+")" +
			" Group By E1.Fsecuritycode) e On a.Fsecuritycode = e.Fsecuritycode" +
			//失败数量
			" Left Join (Select E1.Ftradeamount Samount," +
			" E1.Fsecuritycode," +
			" E1.Fportcode," +
			" E1.Fbargaindate" +
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" E1" +
			" Where E1.Ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_JYLX_ETFLJSHSB)+//赎回失败冲减
			" And E1.Fcheckstate = 1" +
		    //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
			//" And E1.Fsettlestate = 1" +
			" And E1.Fportcode In ("+operSql.sqlCodes(portCodes)+")" +
			" And E1.Fbargaindate = "+dbl.sqlDate(dDate)+
			" ) g On a.Fsecuritycode = g.Fsecuritycode " +
			" And a.Fportcode = g.Fportcode And a.Fbargaindate = g.Fbargaindate";
		return query;
	}

	/**shashijie 2012-5-10 STORY 2565  */
	private void doInfoFailure(Date dDate, String portCodes, Map map) throws YssException {
		ResultSet rs = null;
		try {
			String query = getChongjianQuery(dDate,portCodes);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//key = 证券代码 + 组合代码 + 标示//表示是赎回失败的卖出估值增值
				String key = rs.getString("FSecurityCode") + "," +rs.getString("FPortCode")+",2";
				//计算卖出估增 = (T-1日库存估增 + T日赎回冲减估增) / (T-1库存数量 + T日流入数量(申赎+申赎冲减) + T日赎回冲减数量) * 确认数量 (申请-失败)
				//失败数量是负的所以这里是加
				BigDecimal money = getMoney(rs.getBigDecimal("Fmoney"),//赎回卖出估增(倒钆)
						rs.getBigDecimal("Fbal"),//日库存估增
						YssD.mulD(rs.getBigDecimal("SHSBMoney"), new BigDecimal(-1)),//赎回冲减估增,这里要转正的
						rs.getBigDecimal("Fstorageamount"),//库存数量
						rs.getBigDecimal("Bamount"),//流入数量
						YssD.mulD(rs.getBigDecimal("SHSBAmount"), new BigDecimal(-1)),//赎回冲减数量,这里要转正的
						YssD.addD(rs.getBigDecimal("Samount"),rs.getBigDecimal("FTradeAmount"))//确认数量 (申请-失败)
						);
				
				//证券应收应付Bean
	    		SecPecPayBean secpecpay = new SecPecPayBean();
	    		secpecpay.setYssPub(pub);
	            setSecPecPayBean(secpecpay,rs.getString("FSecurityCode"),
	            		money,rs.getString("FPortCode"),dDate,YssOperCons.YSS_YWLX_ETFMC,
	            		YssOperCons.YSS_YWLX_ETFSHSBGZ);
	            map.put(key, secpecpay);//存入map
			}
		} catch (Exception e) {
			throw new YssException("处理赎回失败冲减卖出估值增值数据出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2012-5-11 STORY 2565 对象赋值
	* @param secPay
	* @param FSecurityCode 证券代码
	* @param money 卖出估值增值
	* @param FPortCode 组合
	* @param dDate 操作日
	* @param strTsfTypeCode 调拨类型
	* @param strSubTsfTypeCode 调拨子类型*/
	private void setSecPecPayBean(SecPecPayBean secPay, String FSecurityCode,
			BigDecimal money, String FPortCode, Date dDate, String strTsfTypeCode,
			String strSubTsfTypeCode) throws YssException {
		if (secPay==null) {
			return;
		}
        secPay.setStrPortCode(FPortCode);       //组合代码
        secPay.setStrSecurityCode(FSecurityCode);//证券代码
        String tradeCury = operFun.getSecCuryCode(FSecurityCode);//证券币种
        secPay.setAttrClsCode(" ");//所属分类
        secPay.setStrCuryCode(tradeCury);//货币代码
        secPay.setInOutType(1);//流入流出方向,默认为正方向
        secPay.setTransDate(dDate);//业务日期
        secPay.setStrTsfTypeCode(strTsfTypeCode);//调拨类型
        secPay.setStrSubTsfTypeCode(strSubTsfTypeCode);//调拨子类型
        
        secPay.setMoney(YssD.round(money,2));//核算金额
        secPay.setMMoney(YssD.round(money,2));
        secPay.setVMoney(YssD.round(money,2));
        
        // 获取当日的基础汇率
		double baseCuryRate = this.getSettingOper().getCuryRate(dDate,
				tradeCury, FPortCode, YssOperCons.YSS_RATE_BASE);
        
        double baseCuryMoney = YssD.mul(money.doubleValue(), baseCuryRate);//基础币余额
        //this.getSettingOper().calBaseMoney(secPay.getMoney(), BaseCuryRate);//计算基础基础货币余额
		secPay.setBaseCuryMoney(baseCuryMoney);
        secPay.setMBaseCuryMoney(baseCuryMoney);
        secPay.setVBaseCuryMoney(baseCuryMoney);
        
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
		rateOper.setYssPub(pub);
		rateOper.getInnerPortRate(dDate, tradeCury, FPortCode);
		double portCuryRate = rateOper.getDPortRate(); // 获取当日的组合汇率
		
		/*dPortMoney = this.getSettingOper().calPortMoney(
				secPay.getMoney(), BaseCuryRate, PortCuryRate, rs.getString("FTradeCury"),
				dDate, rs.getString("FPortCode"));*/// 计算组合货币金额
        
        double portCuryMoney = YssD.div(baseCuryMoney, portCuryRate);//组合币余额
        secPay.setPortCuryMoney(portCuryMoney);
        secPay.setMPortCuryMoney(portCuryMoney);
        secPay.setVPortCuryMoney(portCuryMoney);
        
        secPay.setTransDate(dDate);//业务日期
        secPay.setBaseCuryRate(baseCuryRate);//基础汇率
        secPay.setPortCuryRate(portCuryRate);//组合汇率
        
        secPay.setInvMgrCode(" ");//投资经理代码
        secPay.setBrokerCode(" ");//券商代码
        secPay.setCheckState(1);//状态
	}

	/**shashijie 2012-5-11 STORY 2565
	 * (T-1日库存估增 + T日赎回冲减估增) / (T-1库存数量 + T日流入数量(申赎+申赎冲减) + T日赎回冲减数量) * 确认数量 (申请+失败)
	* @param Fmoney 赎回卖出估增(倒钆)
	* @param Fbal 库存估增
	* @param SHCJGZ 赎回冲减估增
	* @param Fstorageamount 库存数量
	* @param Bamount 流入数量
	* @param SHCJSL 赎回冲减数量
	* @param yesAmount 确认数量 (申请+失败)
	* @return*/
	private BigDecimal getMoney(BigDecimal Fmoney, BigDecimal Fbal,
			BigDecimal SHCJGZ, BigDecimal Fstorageamount,
			BigDecimal Bamount, BigDecimal SHCJSL, BigDecimal yesAmount) {
		//单位卖出估值增值 
		BigDecimal unit = 
			YssD.divD(
				YssD.addD(Fbal, SHCJGZ), 
				YssD.addD(Fstorageamount, Bamount,SHCJSL));
		//乘以卖出数量
		BigDecimal sell = YssD.mulD(unit, yesAmount);
		//最后倒钆计算
		sell = YssD.subD(sell, Fmoney);
		return sell;
	}

	/**shashijie 2012-5-11 STORY 2565 生成冲减sql语句 */
	private String getChongjianQuery(Date dDate, String portCodes) {
		String query =
			" Select a.*," +
			" d.FStorageAmount," +//库存数量
			" Nvl(d.Fbal, 0) Fbal," +//日库存估增
			" Nvl(e.Bamount, 0) Bamount," +//流入数量
			" Nvl(f.Fmoney, 0) Fmoney," +//赎回卖出估增
			" Nvl(g.Samount,0) Samount " +//申请数量
			",Nvl(h.SHSBMoney, 0) SHSBMoney" +//赎回失败冲减估值增值
			",Nvl(i.SHSBAmount, 0) SHSBAmount" +//赎回失败冲减数量
			
			" From (Select A1.Fportcode," +
			" A1.Fsecuritycode," +
			" A1.Fbargaindate," +
			" A1.Fbsdate," +
			" A1.Ftradeamount," +//失败数量
			" A1.Ftradetypecode" +
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" A1 "+
			" Where A1.Fcheckstate = 1" +
		    //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
			//" And A1.Fsettlestate = 1" +
			" And A1.Fbargaindate = " +dbl.sqlDate(dDate)+
			" And A1.Fportcode In ("+operSql.sqlCodes(portCodes)+")" +
			" And A1.Ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_JYLX_ETFLJSHSB)+") a"+
			//交易类型
			" Join (Select B1.Famountind, B1.Ftradetypecode"+
			" From Tb_Base_Tradetype B1" +
			" Where B1.Famountind = -1" +
			" ) b On a.Ftradetypecode = b.Ftradetypecode"+
			//证券
			" Join (Select F1.Fsecuritycode, F1.Fnormscale" +
			" From "+pub.yssGetTableName("Tb_Para_Security")+" F1"+
			" Where Nvl(F1.Fnormscale, 0) > 0) c On c.Fsecuritycode =  a.Fsecuritycode"+
			//库存数量,估值增值
			" Join (Select Nvl(C1.Fstorageamount, 0) Fstorageamount," +
			" Nvl(D2.Fbal, 0) Fbal," +
			" C1.Fsecuritycode," +
			" C1.Fstoragedate" +
			" From (Select Sum(T1.Fstorageamount) Fstorageamount," +
			" T1.Fsecuritycode," +
			" T1.Fstoragedate" +
			" From "+pub.yssGetTableName("Tb_Stock_Security")+" T1" +
			" Where T1.Fcheckstate = 1" +
			" Group By T1.Fsecuritycode, T1.Fstoragedate) C1" +
			" Left Join (Select Sum(D1.Fbal) Fbal," +
			" D1.Fsecuritycode," +
			" D1.Fstoragedate" +
			" From "+pub.yssGetTableName("Tb_Stock_Secrecpay")+" D1" +
			" Where (D1.Fsubtsftypecode Like '09%' Or D1.Fsubtsftypecode Like '9905%' Or" +
			" D1.Fsubtsftypecode Like '9909%')" +
			" Group By D1.Fsecuritycode, D1.Fstoragedate) D2 On C1.Fsecuritycode = D2.Fsecuritycode"+
			" And c1.Fstoragedate = d2.Fstoragedate "+
			//Where C1.Fsecuritycode = '510990'
			" ) d On a.Fsecuritycode = d.Fsecuritycode And a.FBsdate = d.FStoragedate"+
			//流入
			" Left Join (Select Sum(E1.Ftradeamount) Bamount, E1.Fsecuritycode" +
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" E1"+
			" Join Tb_Base_Tradetype b On E1.Ftradetypecode = b.Ftradetypecode" +
			" Where b.Famountind = 1 "+//数量流入
			" And E1.Fcheckstate = 1" +
		    //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
			//" And E1.Fsettlestate = 1" +
			" And E1.Fbargaindate = "+dbl.sqlDate(dDate)+
			" And E1.Fportcode In ("+operSql.sqlCodes(portCodes)+")"+
			" Group By E1.Fsecuritycode) e On a.Fsecuritycode = e.Fsecuritycode"+
			//证券应收应付
			" Left Join (Select F1.Fmoney, F1.Fsecuritycode, F1.Fportcode,f1.FTransDate" +
			" From "+pub.yssGetTableName("Tb_Data_Secrecpay")+" F1"+
			" Where F1.Fportcode in ("+operSql.sqlCodes(portCodes)+")"+
			" And F1.Ftsftypecode = "+dbl.sqlString(YssOperCons.YSS_YWLX_ETFMC)+
			" And F1.Fsubtsftypecode = "+dbl.sqlString(YssOperCons.YSS_YWLX_ETFSHGZ)+ //赎回卖出估增
			" ) f On a.Fsecuritycode = f.Fsecuritycode" +
			" And a.Fportcode = f.Fportcode And a.Fbsdate = f.FTransDate"+
			//申请数量
			" Left Join (Select E1.Ftradeamount Samount, E1.Fsecuritycode,e1.fportcode,e1.FBargainDate" +
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" E1" +
			" Where e1.Ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_JYLX_ETFSH)+ //赎回
			" And E1.Fcheckstate = 1" +
		    //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
			//" And E1.Fsettlestate = 1" +
			" And E1.Fportcode In ("+operSql.sqlCodes(portCodes)+")"+
			" ) g On a.Fsecuritycode = g.Fsecuritycode" +
			" And a.Fportcode = g.Fportcode" +
			" And a.Fbsdate = g.FBargainDate "+
			//证券应收应付(赎回失败冲减卖出估增)
			" Left Join (Select F1.Fmoney SHSBMoney, F1.Fsecuritycode, F1.Fportcode, F1.Ftransdate"+
			" From "+pub.yssGetTableName("Tb_Data_Secrecpay")+" F1"+
			" Where F1.Fportcode = "+dbl.sqlString(portCodes)+
			" And F1.Ftsftypecode = "+dbl.sqlString(YssOperCons.YSS_YWLX_ETFMC)+
			" And F1.Fsubtsftypecode = "+dbl.sqlString(YssOperCons.YSS_YWLX_ETFSHGZ)+ //赎回失败冲减卖出估增
			" ) h On a.Fsecuritycode = h.Fsecuritycode And a.Fportcode = h.Fportcode And a.Fbsdate = h.Ftransdate"+
			//赎回失败冲减数量
			" Left Join (Select E1.Ftradeamount SHSBAmount," +
			" E1.Fsecuritycode," +
			" E1.Fportcode," +
			" E1.Fbargaindate" +
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" E1" +
			" Where E1.Ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_JYLX_ETFLJSHSB)+//赎回失败冲减
            " And E1.Fcheckstate = 1" +
            //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
            //" And E1.Fsettlestate = 1" +
            " And E1.Fportcode = "+dbl.sqlString(portCodes)+
            " ) i On a.Fsecuritycode = i.Fsecuritycode And a.Fportcode = i.Fportcode And a.Fbsdate = i.Fbargaindate"
			;
		return query;
	}  

	/**shashijie ,2011-5-3	STORY #535 需修改指数调整后冲减的的相关类型, 生成证券应收应付数据,计算卖出证券的估值增值*/
    private void createSecuritiesCount(Date dDate, HashMap hmEveStg,
			boolean analy1, boolean analy2, boolean analy3, String portCodes) throws YssException{
    	String portCode = portCodes.replaceAll("'", "");
    	HashMap map = new HashMap();//证券,所属分类是建,值是"原币,基币,组币"
		//统计交易数据子表
		tb_Data_SubTrade(dDate, analy1, analy2, analy3,portCode,map);
		//交易关联表
		Tb_Data_TradeRela(dDate, analy1, analy2, analy3,portCode,map);
		//综合业务表
		tb_Data_Integrated(dDate, analy1, analy2, analy3,portCode,map);
		//检查(交易数据,交易关联,综合业务)中的库存清仓证券,有则直接取昨日估曾,并从HashMap中替换
		queryOutDepository(dDate, analy1, analy2, analy3,portCode,map);
		//生成证券应收应付数据
        createSecurities(map,portCode,dDate);
	}
    
	/**shashijie ,2011-5-10 统计交易关联项卖出估值增值,STORY #535 需修改指数调整后冲减的的相关类型*/
    private void Tb_Data_TradeRela(Date dDate, boolean analy1, boolean analy2,
			boolean analy3, String portCode,HashMap map) throws YssException {
    	String strSql = "";
        ResultSet rs = null;
        try {
        	//获取清仓的证券('证券代码1','证券代码2'),并将清仓证券的卖出估值增值直接存入MAP中
        	String sql = getTradeRelaCuryBal(dDate,portCode);
        	String[] greValue = getSumbalBaseCuryBalPortCuryBal(dDate,portCode,map,sql);
			//如果库存清零则直接哭昨日估值增值,否则就通过公式计算
            strSql = getTradeRelaSql(dDate,portCode,greValue); //获取单笔(卖出,证券库存减少)的交易数据sql语句
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	processorTradeRela(rs, dDate ,portCode,map); 
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}

	/**shashijie ,2011-5-9 统计综合业务表卖出估值增值,STORY #535 需修改指数调整后冲减的的相关类型*/
    private void tb_Data_Integrated(Date dDate, boolean analy1, boolean analy2,
			boolean analy3, String portCode,HashMap map) throws YssException {
    	String strSql = "";
        ResultSet rs = null;
        try {
        	//获取清仓的证券('证券代码1','证券代码2'),并将清仓证券的卖出估值增值直接存入MAP中
        	String sql = getIntegratedPortCuryBal(dDate,portCode);
        	String[] greValue = getSumbalBaseCuryBalPortCuryBal(dDate,portCode,map,sql);
			//如果库存清零则直接哭昨日估值增值,否则就通过公式计算
            strSql = getIntegratedTradeSql(dDate,portCode,greValue); //获取单笔(卖出,证券库存减少)的交易数据sql语句
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	processorIntegratedTrade(rs, dDate ,portCode,map); 
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}

	/**shashijie ,2011-5-3 统计交易数据子表卖出估值增值,STORY #535 需修改指数调整后冲减的的相关类型*/
	private void tb_Data_SubTrade(Date dDate, boolean analy1,
			boolean analy2, boolean analy3, String portCode,HashMap map) throws YssException {
		String strSql = "";
        ResultSet rs = null;
        try {
        	//获取清仓的证券('证券代码1','证券代码2'),并将清仓证券的卖出估值增值直接存入MAP中
        	String sql = getSumBalStringValue(dDate,portCode);
        	String[] greValue = getSumbalBaseCuryBalPortCuryBal(dDate,portCode,map,sql);
			//如果库存清零则直接哭昨日估值增值,否则就通过公式计算
            strSql = getSubTradeSql(dDate,portCode,greValue); //获取单笔(卖出,证券库存减少)的交易数据sql语句
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	processorSubTrade(rs, dDate ,portCode,map); 
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}

	/**shashijie,2011-5-4 生成证券应收应付数据 ,STORY #535 需修改指数调整后冲减的的相关类型 */
	private void createSecurities(HashMap map,String portCode,Date dDate) throws YssException {
		//证券应收应付类
		SecRecPayAdmin secPayAdmin = new SecRecPayAdmin();
		secPayAdmin.setYssPub(pub);
		
		Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String sKey = (String) it.next();
            //应收应付Bean
    		SecPecPayBean secpecpay = new SecPecPayBean();
    		secpecpay.setYssPub(pub);
    		//生成
            setSecPecPayBean(secpecpay,sKey,map.get(sKey).toString(),portCode,dDate);
            secPayAdmin.addList(secpecpay);
            //System.out.println(sKey+"~~~~~~证券~~~~~"+map.get(sKey)+"~~~~~~~~~~~~~~~~~HashMap值");
        }
        if (!map.isEmpty()) {
        	/**shashijie 2012-4-19 BUG 4196 数据来源不筛选变成-1 */
            secPayAdmin.insert(dDate, dDate, "17", "1701", portCode, -1);
            /**end*/
		}
	}

	/**shashijie ,2011-5-7 (证券)应收应付对象赋值,STORY #535 需修改指数调整后冲减的的相关类型*/
	private void setSecPecPayBean(SecPecPayBean secPay, String securityCode,
			String money,String portCode,Date dDate) throws YssException {
		if (secPay==null) {
			return;
		}
        secPay.setStrPortCode(portCode);       //组合代码
        String[] security = securityCode.split(",");
        secPay.setStrSecurityCode(security[0]);//证券代码
        String tradeCury = operFun.getSecCuryCode(security[0]);//证券币种
        secPay.setAttrClsCode(security[1]);//所属分类
        secPay.setStrCuryCode(tradeCury);//货币代码
        secPay.setInOutType(1);//流入流出方向,默认为正方向
        secPay.setTransDate(dDate);//业务日期
        secPay.setStrTsfTypeCode("17");//调拨类型
        secPay.setStrSubTsfTypeCode("1701");//调拨子类型
        
        String[] moneyValue = money.split(",");
        double moneyD = Double.valueOf(moneyValue[0]).doubleValue();//原币余额
        secPay.setMoney(moneyD);//核算金额
        secPay.setMMoney(moneyD);
        secPay.setVMoney(moneyD);
        
        // 获取当日的基础汇率
		double baseCuryRate = this.getSettingOper().getCuryRate(dDate,
				tradeCury, portCode, YssOperCons.YSS_RATE_BASE);
        
        double baseCuryMoney = Double.valueOf(moneyValue[1]).doubleValue();//基础币余额
        //this.getSettingOper().calBaseMoney(secPay.getMoney(), BaseCuryRate);//计算基础基础货币余额
		secPay.setBaseCuryMoney(baseCuryMoney);
        secPay.setMBaseCuryMoney(baseCuryMoney);
        secPay.setVBaseCuryMoney(baseCuryMoney);
        
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
		rateOper.setYssPub(pub);
		rateOper.getInnerPortRate(dDate, tradeCury, portCode);
		double portCuryRate = rateOper.getDPortRate(); // 获取当日的组合汇率
		
		/*dPortMoney = this.getSettingOper().calPortMoney(
				secPay.getMoney(), BaseCuryRate, PortCuryRate, rs.getString("FTradeCury"),
				dDate, rs.getString("FPortCode"));*/// 计算组合货币金额
        
        double portCuryMoney = Double.valueOf(moneyValue[2]).doubleValue();//组合币余额
        secPay.setPortCuryMoney(portCuryMoney);
        secPay.setMPortCuryMoney(portCuryMoney);
        secPay.setVPortCuryMoney(portCuryMoney);
        
        secPay.setTransDate(dDate);//业务日期
        secPay.setBaseCuryRate(baseCuryRate);//基础汇率
        secPay.setPortCuryRate(portCuryRate);//组合汇率
        
        secPay.setInvMgrCode(" ");//投资经理代码
        secPay.setBrokerCode(" ");//券商代码
        secPay.setCheckState(1);//状态
        
	}

	/**shashijie ,2011-5-3 计算得出每笔卖出的估值增值,STORY #535 需修改指数调整后冲减的的相关类型*/
	private void processorSubTrade(ResultSet rs, Date dDate,
			String portCode,HashMap map) throws YssException {
		double[] curyBal = new double[4];//原币余额,基币余额,组币余额,昨日库存数量
        double FTradeAmountIn = 0D;//今日流入数量
        try {
        	String FSecurityCode = rs.getString("FSecurityCode");//证券代码
        	//获取3个币种的余额
            curyBal = getCuryBalValue(FSecurityCode,dDate,rs.getString("FAttrClsCode"));
            //获取今日流入数量
            FTradeAmountIn = getAllFTradeAmountIn(FSecurityCode,dDate,portCode);
            //流出证券估值增值=（昨日估值增值+昨日成本汇兑损益+昨日估值增值汇兑损益）*今日单笔流出数量/（昨日库存数量+今日流入数量）
            curyBal[0] = processorSecurity(curyBal[0],curyBal[3],rs.getDouble("FTradeAmount"),FTradeAmountIn);
            curyBal[1] = processorSecurity(curyBal[1],curyBal[3],rs.getDouble("FTradeAmount"),FTradeAmountIn);
            curyBal[2] = processorSecurity(curyBal[2],curyBal[3],rs.getDouble("FTradeAmount"),FTradeAmountIn);
            //存入map集合中
            String key = FSecurityCode + "," + rs.getString("FAttrClsCode");
            setMapToCuryBal(key,map,curyBal);
        } catch (Exception e) {
            throw new YssException(e);
        }
	}

	/**shashijie ,2011-5-9,计算得出每笔卖出的估值增值,STORY #535 需修改指数调整后冲减的的相关类型*/
	private void processorIntegratedTrade(ResultSet rs, Date dDate,
			String portCode, HashMap map) throws YssException {
        double[] curyBal = new double[4];//原币余额,基币余额,组币余额,昨日库存数量
        double FTradeAmountIn = 0D;//今日流入数量
        try {
        	String FSecurityCode = rs.getString("FSecurityCode");//证券代码
			//获取3个币种的余额
            curyBal = getCuryBalValue(FSecurityCode,dDate,rs.getString("FAttrClsCode"));
            //获取今日流入数量
            FTradeAmountIn = getAllIntegratedAmountIn(FSecurityCode,dDate,portCode);
            //流出证券估值增值=（昨日估值增值+昨日成本汇兑损益+昨日估值增值汇兑损益）*今日单笔流出数量/（昨日库存数量+今日流入数量）
            curyBal[0] = processorSecurity(curyBal[0],curyBal[3],rs.getDouble("FAmount"),FTradeAmountIn);
            curyBal[1] = processorSecurity(curyBal[1],curyBal[3],rs.getDouble("FAmount"),FTradeAmountIn);
            curyBal[2] = processorSecurity(curyBal[2],curyBal[3],rs.getDouble("FAmount"),FTradeAmountIn);
            //存入map集合中
            String key = FSecurityCode + "," + rs.getString("FAttrClsCode");
            setMapToCuryBal(key,map,curyBal);
        } catch (Exception e) {
            throw new YssException(e);
        } 
	}
	
	/**shashijie ,2011-5-10,计算得出每笔卖出的估值增值,STORY #535 需修改指数调整后冲减的的相关类型*/
	private void processorTradeRela(ResultSet rs, Date dDate, String portCode,
			HashMap map) throws YssException {
		double[] curyBal = new double[4];//原币余额,基币余额,组币余额,昨日库存数量
        double FTradeAmountIn = 0D;//今日流入数量
        try {
        	String FSecurityCode = rs.getString("FSecurityCode");//证券代码
			//获取3个币种的余额
            curyBal = getCuryBalValue(FSecurityCode,dDate,rs.getString("FAttrClsCode"));
            //获取今日流入数量
            FTradeAmountIn = getAllTradeRelaAmountIn(FSecurityCode,dDate,portCode);
            //流出证券估值增值=（昨日估值增值+昨日成本汇兑损益+昨日估值增值汇兑损益）*今日单笔流出数量/（昨日库存数量+今日流入数量）
            curyBal[0] = processorSecurity(curyBal[0],curyBal[3],rs.getDouble("FAmount"),FTradeAmountIn);
            curyBal[1] = processorSecurity(curyBal[1],curyBal[3],rs.getDouble("FAmount"),FTradeAmountIn);
            curyBal[2] = processorSecurity(curyBal[2],curyBal[3],rs.getDouble("FAmount"),FTradeAmountIn);
            //存入map集合中
            String key = FSecurityCode + "," + rs.getString("FAttrClsCode");
            setMapToCuryBal(key,map,curyBal);
        } catch (Exception e) {
            throw new YssException(e);
        } 
	}

	/**shashijie ,2011-5-10,设置map中的值(key:(证券代码,所属分类),value:原币,基币,组币)*/
	private void setMapToCuryBal(String key, HashMap map,double[] curyBal) {
		if (map.containsKey(key)) {
        	//如果该证券当天做多次卖出,则卖出估值增值累加
			setSubTradeMap(map,key,curyBal[0],curyBal[1],curyBal[2]);
		} else {
        	String greValue = curyBal[0] + "," + curyBal[1] + "," + curyBal[2];
        	map.put(key, greValue);
		}
	}

	/**shashijie,2011-5-10 ,获取,原币余额,基币余额,组币余额,昨日库存数量的数组*/
	private double[] getCuryBalValue(String FSecurityCode,Date dDate,String FAttrClsCode) throws YssException {
		double[] curyBal = new double[4];
		double bal = 0D;		//原币余额
        double baseCuryBal = 0D;//基币余额
        double portCuryBal = 0D;//组币余额
        double FStorageaMount = 0D;//昨日库存数量
        String strSql = "";
        ResultSet rs2 = null;
        try {
        	//获取昨日库存sql语句
            strSql = getTb_Stock_Security(FSecurityCode,dDate,FAttrClsCode); 
            rs2 = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs2.next()) {
            	FStorageaMount = rs2.getDouble("FStorageaMount");//昨日库存数量
            	bal = YssD.add(bal, rs2.getDouble("FBal"));		  		 //原币余额
            	baseCuryBal = YssD.add(baseCuryBal,rs2.getDouble("Fbasecurybal"));//基币余额
            	portCuryBal = YssD.add(portCuryBal,rs2.getDouble("FPortCuryBal"));//组币余额
            }
            curyBal[0] = bal;
            curyBal[1] = baseCuryBal;
            curyBal[2] = portCuryBal;
            curyBal[3] = FStorageaMount;
		} catch (Exception e) {
			throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs2);
        }
        return curyBal;
	}

	/**shashijie,2011-5-4  针对该证券当天做多次卖出,则卖出估值增值累加,STORY #535 需修改指数调整后冲减的的相关类型 */
	private void setSubTradeMap(HashMap map, String key, double bal,
			double baseCuryBal, double portCuryBal) {
		String mapValue = (String)map.get(key);
		String[] mapValueStrings = mapValue.split(",");
		
		double bal1 = YssD.add(Double.valueOf(mapValueStrings[0]).doubleValue(), bal);
		double baseCuryBal1 = YssD.add(Double.valueOf(mapValueStrings[1]).doubleValue(), baseCuryBal);
		double portCuryBal1 = YssD.add(Double.valueOf(mapValueStrings[2]).doubleValue(), portCuryBal);
		
		String greValue = bal1 + "," + baseCuryBal1 + "," + portCuryBal1;
		map.put(key, greValue);
	}

	/**shashijie ,2011-5-3 获取今日流入数量 ,STORY #535 需修改指数调整后冲减的的相关类型*/
	private double getAllFTradeAmountIn(String FSecurityCode, Date dDate,
			String portCode) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		double FTradeAmountIn = 0D;
		try {
			strSql = "select sum(a.FTradeAmount) FTradeAmount from "+pub.yssGetTableName("Tb_Data_SubTrade")+" a "+
					       " join Tb_Base_TradeType b on a.FTradeTypeCode = b.FTradeTypeCode "+
					       " where b.FAmountInd = 1 " +  //--数量流入
					       " and a.FCheckState = 1 " +
					       //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
					       //" and a.Fsettlestate = 1 " +
					       " and a.FBargainDate = "+dbl.sqlDate(dDate) +
					       " and a.FPortCode = "+dbl.sqlString(portCode) +
					       " and a.FSecurityCode = "+dbl.sqlString(FSecurityCode);
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
            	FTradeAmountIn = rs.getDouble("FTradeAmount");
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally{
            dbl.closeResultSetFinal(rs);
        }
		return FTradeAmountIn;
	}

	/**shashijie ,2011-5-9, 获取今日流入数量 ,STORY #535 需修改指数调整后冲减的的相关类型*/
	private double getAllIntegratedAmountIn(String fSecurityCode, Date dDate,
			String portCode) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		double FTradeAmountIn = 0D;
		try {
			strSql = "SELECT SUM(a.FAmount) FAmount FROM "+pub.yssGetTableName("Tb_Data_Integrated")+" a "+
				       " WHERE a.famount > 0 " + 
				       " AND a.FOperDate = "+dbl.sqlDate(dDate)+
				       " AND a.FPortCode = "+dbl.sqlString(portCode)+
				       " and a.FSecurityCode = " + dbl.sqlString(fSecurityCode) + //modify by fangjiang 2011.08.08 bug 2365
				       " AND a.FCheckState = 1 ";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
            	FTradeAmountIn = rs.getDouble("FAmount");
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally{
            dbl.closeResultSetFinal(rs);
        }
		return FTradeAmountIn;
	}

	/**shashijie,2011-5-10,获取今日流入数量 ,STORY #535 需修改指数调整后冲减的的相关类型*/
	private double getAllTradeRelaAmountIn(String fSecurityCode, Date dDate,
			String portCode) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		double FTradeAmountIn = 0D;
		try {
			strSql = "SELECT  SUM(a.FAmount) FAmount FROM "+pub.yssGetTableName("Tb_Data_TradeRela")+" a "+ 
			         " LEFT JOIN "+pub.yssGetTableName("Tb_Data_SubTrade")+" b on a.fnum = b.fnum "+
			         " WHERE a.FInOut = 1 "+
			         " AND a.FPortCode = "+dbl.sqlString(portCode)+ 
			         " AND a.FCheckState = 1 "+
			         " AND b.FBargainDate = "+dbl.sqlDate(dDate)+  
			         " AND b.FCheckState = 1 "+ 
			         //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
			         //" AND b.FSettleState = 1 "
			         "";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
            	FTradeAmountIn = rs.getDouble("FAmount");
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally{
            dbl.closeResultSetFinal(rs);
        }
		return FTradeAmountIn;
	}

	/**shashijie,2011-5-3 计算卖出估增,STORY #535 需修改指数调整后冲减的的相关类型
	 * @param bal 昨日估值增值(三币种)
	 * @param FStorageaMount 昨日库存数量
	 * @param FTradeAmount 单笔流出数量
	 * @param FTradeAmountIn 今日总的流入数量
	 * */
	private double processorSecurity(double bal, double FStorageaMount, double FTradeAmount,double FTradeAmountIn) {
		//流出证券估值增值=（昨日估值增值+昨日成本汇兑损益+昨日估值增值汇兑损益）*今日单笔流出数量/（昨日库存数量+今日流入数量）
		double securyCount = YssD.add(FStorageaMount, FTradeAmountIn);//（昨日库存数量+今日流入数量）
		double avgCount = YssD.div(FTradeAmount,securyCount);//今日单笔流出数量/总数量
		double value = YssD.mul(bal, avgCount);//总估值增值/总卖出数量
		return value;
	}

	/**shashijie ,2011-5-4  获取清仓的证券代码与所属分类,并将清仓证券的卖出估值增值直接存入MAP中*/
	private String[] getSumbalBaseCuryBalPortCuryBal(Date dDate,
			String portCode, HashMap map,String sql) throws YssException {
		ResultSet rs = null;
		String securityCode = "";//拼接的证券代码
		String attrClsCode = "";//拼接的所属分类
		String FAttrClsCode = "";//所属分类
		String FSecurityCode = "";//证券代码
		String grdValue = "";//三个币种的估值增值
		String key = "";//形式(key = 证券代码,所属分类)
		String[] secAttrCode = {"' '","' '"};
		try {
			rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				FSecurityCode = rs.getString("FSecurityCode");
				FAttrClsCode = rs.getString("FAttrClsCode");
				securityCode += dbl.sqlString(FSecurityCode) + " , ";
				attrClsCode = dbl.sqlString(FAttrClsCode) + " , ";
				grdValue = rs.getString("FBal")+","+rs.getString("Fbasecurybal")+","+rs.getString("FPortCuryBal");
				key = FSecurityCode + "," + FAttrClsCode;
				map.put(key, grdValue);
			}
			if (!grdValue.trim().equals("")) {
				securityCode = " ( " + securityCode.substring(0,securityCode.lastIndexOf(",")) + " ) ";
				attrClsCode = " ( " + attrClsCode.substring(0,attrClsCode.lastIndexOf(",")) + " ) ";
				secAttrCode[0] = securityCode;
				secAttrCode[1] = attrClsCode;
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return secAttrCode;
	}

	/**shashijie,2011-5-17,检查(交易数据,交易关联,综合业务)中的库存清仓证券,有则直接取昨日估曾,并从HashMap中替换,STORY #535 需修改指数调整后冲减的的相关类型*/
    private void queryOutDepository(Date dDate, boolean analy1, boolean analy2,
			boolean analy3, String portCode, HashMap map) throws YssException {
    	ResultSet rs = null;
		String sql = "";
		try {
			sql = getAllDepositoryOutAll(dDate,portCode);
            rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	getSumbalBaseCuryBalPortCuryBal(dDate, portCode, map, sql);
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally{
            dbl.closeResultSetFinal(rs);
        }
	}

    /**shashijie,2011-5-17,获取(交易数据,交易关联,综合业务)库存清仓,全部卖光的证券*/
    private String getAllDepositoryOutAll(Date dDate, String portCode) {
    	//--交易数据,交易关联,综合业务
		String sql = "select c.FSecurityCode,sum(c.FBal) FBal,sum(c.Fbasecurybal) Fbasecurybal,sum(c.FPortCuryBal) FPortCuryBal,c.FAttrClsCode " +
				//--库存数量
				  " from ( select t.FStorageAmount, " + 
				              " t.FSecurityCode, " +
				              " d.FBal, " +
				              " d.Fbasecurybal, " +
				              " d.FPortCuryBal, " +
				              " d.FAttrClsCode " +
				         " FROM (" +
					         "SELECT SUM(t1.FStorageAmount) FStorageAmount , t1.FSecurityCode, t1.FAttrClsCode "+
				                " FROM "+pub.yssGetTableName("Tb_Stock_Security")+" t1 "+
				                " WHERE t1.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1)) +
				                " AND t1.FPortCode = "+dbl.sqlString(portCode)+
				                " AND t1.FCheckState = 1 "+
				                " GROUP BY t1.FSecurityCode,t1.FAttrClsCode"+
				         ") t "+
				         " JOIN "+pub.yssGetTableName("Tb_Stock_SecRecPay")+" d on ( t.FSecurityCode = d.FSecurityCode and t.FAttrClsCode = d.FAttrClsCode ) "+
				         " WHERE d.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1))+ 
				         " AND d.FPortCode = "+dbl.sqlString(portCode)+
				         " AND d.FCheckState = 1 "+
				         " AND (d.FSubTsfTypeCode LIKE '09%' OR "+ 
				              " d.FSubTsfTypeCode LIKE '9905%' OR "+ 
				              " d.FSubTsfTypeCode LIKE '9909%') ) c "+ 
				//--卖出交易数据数量
				  " left join ( select v.FSecurityCode, sum(v.FTradeAmount) FTradeAmount ,v.FAttrClsCode "+ 
				        " from "+pub.yssGetTableName("Tb_Data_SubTrade")+" v "+
				        " left join Tb_Base_TradeType u on u.FTradeTypeCode = v.FTradeTypeCode "+ 
				        " where v.FPortCode = "+dbl.sqlString(portCode)+
				        "  and v.FBargainDate = "+dbl.sqlDate(dDate)+ 
				        "  and v.FCheckState = 1 "+ 
				        //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
				        //"  and v.FSettleState = 1 "+
				        "  and u.FAmountInd = -1 "+ 
				        " group by v.FSecurityCode,v.FAttrClsCode ) e on (c.FSecurityCode = e.FSecurityCode and c.FAttrClsCode = e.FAttrClsCode) "+
				//--买入交易数据数量
				  " left join ( select v.FSecurityCode, sum(v.FTradeAmount) FTradeAmount ,v.FAttrClsCode "+ 
				         " from "+pub.yssGetTableName("Tb_Data_SubTrade")+" v "+
				         " left join Tb_Base_TradeType u on u.FTradeTypeCode = v.FTradeTypeCode "+ 
				         " where v.FPortCode = "+dbl.sqlString(portCode)+ 
				         " and v.FBargainDate = "+dbl.sqlDate(dDate)+ 
				         " and v.FCheckState = 1 "+ 
					     //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
				         //" and v.FSettleState = 1 "+ 
				         " and u.FAmountInd = 1 "+ 
				         " group by v.FSecurityCode,v.FAttrClsCode ) f on ( c.FSecurityCode = f.FSecurityCode and c.FAttrClsCode = f.FAttrClsCode ) "+ 
				//--卖出交易关联数量
				  " left join ( select a.FSecurityCode , sum(a.FAmount) FAmount, a.FAttrClsCode from "+
				  			pub.yssGetTableName("Tb_Data_TradeRela")+" a "+
				         " left join "+pub.yssGetTableName("Tb_Data_SubTrade")+" b on a.fnum = b.fnum "+
				         " where a.FInOut = -1 "+ 
				         " and a.FPortCode = "+dbl.sqlString(portCode)+ 
				         " and a.FCheckState = 1 "+ 
				         " and b.FBargainDate = "+dbl.sqlDate(dDate)+ 
				         " and b.FCheckState = 1 "+ 
				         //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
				         //" and b.FSettleState = 1 "+ 
				         " group by a.FSecurityCode,a.FAttrClsCode ) g on (c.FSecurityCode = g.FSecurityCode and c.FAttrClsCode = g.FAttrClsCode ) "+
				//--买入交易关联数量
				  " left join ( select a.FSecurityCode , sum(a.FAmount) FAmount, a.FAttrClsCode from "+
				  			pub.yssGetTableName("Tb_Data_TradeRela")+" a "+ 
				         " left join "+pub.yssGetTableName("Tb_Data_SubTrade")+" b on a.fnum = b.fnum "+
				         " where a.FInOut = 1 "+ 
				         " and a.FPortCode = "+dbl.sqlString(portCode)+
				         " and a.FCheckState = 1 "+ 
				         " and b.FBargainDate = "+dbl.sqlDate(dDate)+  
				         " and b.FCheckState = 1 "+ 
				         //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
				         //" and b.FSettleState = 1 "+ 
				         " group by a.FSecurityCode,a.FAttrClsCode ) h on ( c.FSecurityCode = h.FSecurityCode and c.FAttrClsCode = h.FAttrClsCode ) "+
				//--卖出综合业务数量
				  " left join ( select a.FSecurityCode , sum(a.FAmount)*-1 FAmount, a.FAttrClsCode from "+
				  			pub.yssGetTableName("Tb_Data_Integrated")+" a "+
				         " where a.famount < 0 "+ 
				         " and a.FOperDate = "+dbl.sqlDate(dDate)+ 
				         " and a.FPortCode = "+dbl.sqlString(portCode)+
				         " and a.FCheckState = 1 "+
				         " group by a.FSecurityCode,a.FAttrClsCode ) i on ( c.FSecurityCode = i.FSecurityCode and c.FAttrClsCode = i.FAttrClsCode ) "+
				//--买入综合业务数量
				  " left join ( select a.FSecurityCode , sum(a.FAmount) FAmount, a.FAttrClsCode from "+
				  			pub.yssGetTableName("Tb_Data_Integrated")+" a "+
				         " where a.famount > 0 "+
				         " and a.FOperDate = "+dbl.sqlDate(dDate)+ 
				         " and a.FPortCode = "+dbl.sqlString(portCode)+
				         " and a.FCheckState = 1 "+
				         " GROUP BY a.FSecurityCode,a.FAttrClsCode ) j on ( c.FSecurityCode = j.FSecurityCode and c.FAttrClsCode = j.FAttrClsCode ) "+
				
				 " where ( (nvl(e.FTradeAmount,0) + nvl(g.FAmount,0) + nvl(i.FAmount,0)) "+
				       " - "+
				       " (nvl(f.FTradeAmount,0) + nvl(h.FAmount,0) + nvl(j.FAmount,0)) "+ 
				       " ) = c.FStorageAmount "+ 
				 " group by c.FSecurityCode,c.FAttrClsCode ";
		return sql;
	}

	/**shashijie ,2011-5-9 获取(综合业务)清仓的证券代码sql,STORY #535 需修改指数调整后冲减的的相关类型*/
	private String getIntegratedPortCuryBal(Date dDate, String portCode) {
		String sqlString = "SELECT c.FSecurityCode,SUM(c.FBal) FBal,SUM(c.Fbasecurybal) Fbasecurybal,SUM(c.FPortCuryBal) FPortCuryBal, c.FAttrClsCode "+
				//--库存数量
				  " FROM (SELECT t.FStorageAmount, " +
				               " t.FSecurityCode, " +
				               " d.FBal, " +
				               " d.Fbasecurybal, " +
				               " d.FPortCuryBal, " +
				               " d.FAttrClsCode "+
				          " FROM (" +
						          "SELECT SUM(t1.FStorageAmount) FStorageAmount , t1.FSecurityCode, t1.FAttrClsCode "+
					                " FROM "+pub.yssGetTableName("Tb_Stock_Security")+" t1 "+
					                " WHERE t1.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1)) +
					                " AND t1.FPortCode = "+dbl.sqlString(portCode)+
					                " AND t1.FCheckState = 1 "+
					                " GROUP BY t1.FSecurityCode,t1.FAttrClsCode"+
				          ") t "+
				          " JOIN "+pub.yssGetTableName("Tb_Stock_SecRecPay")+" d ON (t.FSecurityCode = d.FSecurityCode and t.FAttrClsCode = d.FAttrClsCode) " + 
				          " WHERE d.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1)) +
				          " AND d.FPortCode = "+dbl.sqlString(portCode)+
				          " AND d.FCheckState = 1 "+
				          " AND (d.FSubTsfTypeCode LIKE '09%' OR " +
				          "     d.FSubTsfTypeCode LIKE '9905%' OR " + 
				          "     d.FSubTsfTypeCode LIKE '9909%')) c " + 
				//--卖出数量
				  " LEFT JOIN (SELECT a.FSecurityCode , SUM(a.FAmount)*-1 FAmount,a.FAttrClsCode FROM "+pub.yssGetTableName("Tb_Data_Integrated")+" a "+
				         " WHERE a.famount < 0 "+
				         " AND a.FOperDate = "+dbl.sqlDate(dDate)+
				         " AND a.FPortCode = "+dbl.sqlString(portCode)+
				         " AND a.FCheckState = 1 "+
				         " GROUP BY a.FSecurityCode,a.FAttrClsCode) e ON (c.FSecurityCode = e.FSecurityCode and c.FAttrClsCode = e.FAttrClsCode) " + 
				//--买入数量
				  " LEFT JOIN (SELECT a.FSecurityCode , SUM(a.FAmount) FAmount,a.FAttrClsCode FROM "+pub.yssGetTableName("Tb_Data_Integrated")+" a "+
				         " WHERE a.famount > 0 "+ 
				         " AND a.FOperDate = "+dbl.sqlDate(dDate)+
				         " AND a.FPortCode = "+dbl.sqlString(portCode)+
				         " AND a.FCheckState = 1 "+
				         " GROUP BY a.FSecurityCode,a.FAttrClsCode) f ON (c.FSecurityCode = f.FSecurityCode and c.FAttrClsCode = f.FAttrClsCode) "+
				         
				 " WHERE (nvl(e.FAmount,0) - nvl(f.FAmount,0) ) = c.FStorageAmount "+ 
				 " GROUP BY c.FSecurityCode,c.FAttrClsCode ";
		return sqlString;
	}
	
	/**shashijie ,2011-5-4,获取今日清仓的证券代码以及它的昨日库存估值增值 ,STORY #535 需修改指数调整后冲减的的相关类型*/
	private String getSumBalStringValue(Date dDate, String portCode) {
		String sqlString = 
			"SELECT c.FSecurityCode,SUM(c.FBal) FBal,SUM(c.Fbasecurybal) Fbasecurybal,SUM(c.FPortCuryBal) FPortCuryBal,c.FAttrClsCode "+
				//--库存数量(昨日)
				  " FROM (SELECT t.FStorageAmount, "+
				               " t.FSecurityCode,"+
				               " d.FBal,"+
				               " d.Fbasecurybal,"+
				               " d.FPortCuryBal,"+
				               " d.FAttrClsCode "+
				  		 " FROM (" +
						  		"SELECT SUM(t1.FStorageAmount) FStorageAmount , t1.FSecurityCode, t1.FAttrClsCode "+
				                " FROM "+pub.yssGetTableName("Tb_Stock_Security")+" t1 "+
				                " WHERE t1.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1)) +
				                " AND t1.FPortCode = "+dbl.sqlString(portCode)+
				                " AND t1.FCheckState = 1 "+
				                " GROUP BY t1.FSecurityCode,t1.FAttrClsCode"+
				  		 "		) t "+
				         " JOIN "+pub.yssGetTableName("Tb_Stock_SecRecPay")+" d ON (t.FSecurityCode = d.FSecurityCode AND t.FAttrClsCode = d.FAttrClsCode ) "+
				         " WHERE d.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1)) +
				         " AND d.FPortCode = "+dbl.sqlString(portCode)+
				         " AND d.FCheckState = 1 "+
				         " AND (d.FSubTsfTypeCode LIKE '09%' OR "+
				               " d.FSubTsfTypeCode LIKE '9905%' OR "+
				               " d.FSubTsfTypeCode LIKE '9909%')) c "+
				//--卖出数量
				  " LEFT JOIN (SELECT v.FSecurityCode, sum(v.FTradeAmount) FTradeAmount , v.FAttrClsCode "+ 
				          " FROM "+pub.yssGetTableName("Tb_Data_SubTrade")+" v "+
				          " LEFT JOIN Tb_Base_TradeType u ON u.FTradeTypeCode = "+
				                                          " v.FTradeTypeCode "+
				         " WHERE v.FPortCode = "+dbl.sqlString(portCode) +
				         " AND v.FBargainDate = "+dbl.sqlDate(dDate) +
				         " AND v.FCheckState = 1 "+
				         //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
				         //" AND v.FSettleState = 1 "+
				         " AND u.FAmountInd = -1 "+
				         " GROUP BY v.FSecurityCode,v.FAttrClsCode ) e ON (c.FSecurityCode = e.FSecurityCode AND c.FAttrClsCode = e.FAttrClsCode ) "+ 
				//--买入数量
				  " LEFT JOIN (SELECT v.FSecurityCode, sum(v.FTradeAmount) FTradeAmount ,v.FAttrClsCode "+
				         " FROM "+pub.yssGetTableName("Tb_Data_SubTrade")+" v "+
				         " LEFT JOIN Tb_Base_TradeType u ON u.FTradeTypeCode = "+
				                                          " v.FTradeTypeCode "+
				         " WHERE v.FPortCode = "+dbl.sqlString(portCode) +
				         " AND v.FBargainDate = "+dbl.sqlDate(dDate) +
				         " AND v.FCheckState = 1 "+
				         //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
				         //" AND v.FSettleState = 1 "+
				         " AND u.FAmountInd = 1 "+
				         " GROUP BY v.FSecurityCode,v.FAttrClsCode) f ON ( c.FSecurityCode = f.FSecurityCode AND c.FAttrClsCode = f.FAttrClsCode ) "+ 
				         
				 "WHERE (nvl(e.FTradeAmount,0) - nvl(f.FTradeAmount,0) ) = c.FStorageAmount "+
				 "GROUP BY c.FSecurityCode,c.FAttrClsCode ";
		return sqlString;
	}

	/**shashijie,2011-5-10,获取交易关联的sql*/
	private String getTradeRelaCuryBal(Date dDate, String portCode) {
		String sql = "SELECT c.FSecurityCode,SUM(c.FBal) FBal,SUM(c.Fbasecurybal) Fbasecurybal,SUM(c.FPortCuryBal) FPortCuryBal,c.FAttrClsCode "+
				//--库存数量
				  " FROM (SELECT t.FStorageAmount, "+
				               " t.FSecurityCode, "+
				               " d.FBal, "+
				               " d.Fbasecurybal, "+
				               " d.FPortCuryBal, "+
				               " d.FAttrClsCode "+
				          " FROM (" +
					          "SELECT SUM(t1.FStorageAmount) FStorageAmount , t1.FSecurityCode, t1.FAttrClsCode "+
				                " FROM "+pub.yssGetTableName("Tb_Stock_Security")+" t1 "+
				                " WHERE t1.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1)) +
				                " AND t1.FPortCode = "+dbl.sqlString(portCode)+
				                " AND t1.FCheckState = 1 "+
				                " GROUP BY t1.FSecurityCode,t1.FAttrClsCode"+
				          ") t "+ 
				          " JOIN "+pub.yssGetTableName("Tb_Stock_SecRecPay")+" d ON (t.FSecurityCode = d.FSecurityCode AND t.FAttrClsCode = d.FAttrClsCode) "+ 
				          " WHERE d.FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1)) + 
				          " AND d.FPortCode = "+dbl.sqlString(portCode)+
				          " AND d.FCheckState = 1 "+
				          " AND (d.FSubTsfTypeCode LIKE '09%' OR "+
				              " d.FSubTsfTypeCode LIKE '9905%' OR "+ 
				              " d.FSubTsfTypeCode LIKE '9909%')) c "+ 
				//--卖出数量
				  " LEFT JOIN (SELECT a.FSecurityCode , SUM(a.FAmount) FAmount,a.FAttrClsCode FROM "+pub.yssGetTableName("Tb_Data_TradeRela")+" a "+
				         " LEFT JOIN "+pub.yssGetTableName("Tb_Data_SubTrade")+" b ON a.fnum = b.fnum "+
				         " WHERE a.FInOut = -1 "+ 
				         " AND a.FPortCode = "+dbl.sqlString(portCode)+
				         " AND a.FCheckState = 1 "+
				         " AND b.FBargainDate = "+dbl.sqlDate(dDate) +
				         " AND b.FCheckState = 1 "+ 
				         //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
				         //" AND b.FSettleState = 1 "+ 
				         " GROUP BY a.FSecurityCode,a.FAttrClsCode ) e ON (c.FSecurityCode = e.FSecurityCode and c.FAttrClsCode = e.FAttrClsCode) "+
				//--买入数量
				  " LEFT JOIN (SELECT a.FSecurityCode , sum(a.FAmount) FAmount,a.FAttrClsCode FROM "+pub.yssGetTableName("Tb_Data_TradeRela")+" a "+ 
				         " LEFT JOIN "+pub.yssGetTableName("Tb_Data_SubTrade")+" b ON a.fnum = b.fnum "+
				         " where a.FInOut = 1 "+ 
				         " AND a.FPortCode = "+dbl.sqlString(portCode)+
				         " AND a.FCheckState = 1 "+ 
				         " AND b.FBargainDate = "+dbl.sqlDate(dDate)+  
				         " AND b.FCheckState = 1 "+ 
				         //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
				         //" AND b.FSettleState = 1 "+ 
				         " GROUP BY a.FSecurityCode,a.FAttrClsCode ) f ON (c.FSecurityCode = f.FSecurityCode and c.FAttrClsCode = f.FAttrClsCode) "+ 
				         
				 " WHERE (NVL(e.FAmount,0) - NVL(f.FAmount,0) ) = c.FStorageAmount " + 
				 " GROUP BY c.FSecurityCode,c.FAttrClsCode ";
		return sql;
	}

	/**shashijie ,2011-5-3 获取昨日库存信息（昨日估值增值+昨日成本汇兑损益+昨日估值增值汇兑损益）,STORY #535 需修改指数调整后冲减的的相关类型*/
	private String getTb_Stock_Security(String FSecurityCode, Date dDate,String FAttrClsCode) {
		Date date = YssFun.addDay(dDate, -1);
		String sqlString = "SELECT c.FStorageaMount,d.FBal,d.Fbasecurybal,d.FPortCuryBal FROM " + 
			   " (" +
			   	" SELECT sum(t1.FStorageAmount) FStorageAmount , t1.FSecurityCode,t1.FAttrClsCode "+
	            " FROM "+pub.yssGetTableName("Tb_Stock_Security")+" t1 "+
	            " WHERE t1.FStorageDate = "+dbl.sqlDate(date) +
	            " AND t1.FSecurityCode = "+ dbl.sqlString(FSecurityCode) +
	            " AND t1.FAttrClsCode = "+ dbl.sqlString(FAttrClsCode) + 
	            " AND t1.FCheckState = 1 "+
	            " GROUP BY t1.FSecurityCode , t1.FAttrClsCode "+
			   ") c "+ 
		       " JOIN "+pub.yssGetTableName("Tb_Stock_SecRecPay")+" d ON c.FSecurityCode = d.FSecurityCode "+
		       " AND d.FStorageDate = "+ dbl.sqlDate(date) +
		       " AND d.FSecurityCode = "+ dbl.sqlString(FSecurityCode) +
		       " AND d.FAttrClsCode = "+ dbl.sqlString(FAttrClsCode) + 
		       " AND ( d.FSubTsfTypeCode LIKE '09%' OR d.FSubTsfTypeCode LIKE '9905%' OR d.FSubTsfTypeCode LIKE '9909%' )";
		return sqlString;
	}

	/**shashijie,2011-5-3 获取单笔(卖出,证券库存减少)的交易数据sql语句 */
	private String getSubTradeSql(Date dDate, String portCode,String[] greValue) {
		String sql = "SELECT a.FSecurityCode ,a.FTradeAmount, a.FAttrClsCode FROM " + 
						 pub.yssGetTableName("Tb_Data_SubTrade") + " a " + 
		       " JOIN Tb_Base_TradeType b ON a.FTradeTypeCode = b.FTradeTypeCode " + 
		       /**shashijie 2012-5-10 STORY 2565 这里排除ETF证券*/
		       " Join (Select F1.Fsecuritycode, F1.Fnormscale" +
		       " From "+pub.yssGetTableName("Tb_Para_Security")+
		       " F1 Where nvl(f1.fnormscale,0) <= 0 ) c On c.fsecuritycode = a.fsecuritycode "+
		       /**end*/
		       " WHERE b.FAmountInd = -1 " + //--数量流出 
		       " AND a.FCheckState = 1 " + 	 //--已审核
		       //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
		       //" AND a.Fsettlestate = 1 " +	 //--已结算
		       " AND a.FBargainDate = " + dbl.sqlDate(dDate) +
		       " AND a.FPortCode = " + dbl.sqlString(portCode);
		if (greValue!=null && greValue.length>=2) {
			sql += " AND (a.FSecurityCode NOT IN  " + greValue[0] +
					" OR a.FAttrClsCode NOT IN  " + greValue[1] +" ) ";
		}
		return sql;
	}

    /**shashijie,2011-5-9 获取单笔(卖出,证券库存减少)的综合业务sql语句*/
	private String getIntegratedTradeSql(Date dDate, String portCode,
			String[] greValue) {
		String sql = "SELECT a.FSecurityCode , (a.FAmount*-1) FAmount,a.FAttrClsCode FROM "+
				pub.yssGetTableName("Tb_Data_Integrated")+" a "+
		       " WHERE a.famount < 0 " + 
		       " AND a.FOperDate = "+dbl.sqlDate(dDate) + 
		       " AND a.FPortCode = "+dbl.sqlString(portCode) + 
		       " AND a.FCheckState = 1 ";
		if (greValue!=null && greValue.length>=2) {
			sql += " AND (a.FSecurityCode NOT IN  " + greValue[0]+
					" OR a.FAttrClsCode NOT IN " + greValue[1]+" ) ";
		}
		return sql;
	}

	/**shashijie ,2011-5-10,获取单笔卖出的交易关联证券sql语句*/
	private String getTradeRelaSql(Date dDate, String portCode, String[] greValue) {
		String sql = "SELECT a.FSecurityCode , a.FAmount FAmount, a.FAttrClsCode FROM "+
					pub.yssGetTableName("Tb_Data_TradeRela")+" a "+ 
		         " LEFT JOIN "+pub.yssGetTableName("Tb_Data_SubTrade")+" b ON a.fnum = b.fnum "+
		         " WHERE a.FInOut = -1 "+
		         " AND a.FPortCode = "+dbl.sqlString(portCode) + 
		         " AND a.FCheckState = 1 "+ 
		         " AND b.FBargainDate = "+dbl.sqlDate(dDate)+ 
		         " AND b.FCheckState = 1 "+ 
		         //有些客户是结算日期那天才点交易结算,不统一所以这里去除已结算的判断
		         /*" AND b.FSettleState = 1 "*/
		         "";
		if (greValue!=null && greValue.length>=2) {
			sql += " AND (a.FSecurityCode NOT IN  " + greValue[0]+
					" OR a.FAttrClsCode NOT IN " + greValue[1]+ " ) ";
		}
		return sql;
	}
	

	/**
     * 不计算卖出估值增值时删除关联数据
     * @throws YssException
     * 081010 edit by jc 分组合删除关联数据
     */
    private void deleteDataByDate(java.util.Date dDate, String portCode) throws YssException {
        String strSql = "";
        SecRecPayAdmin srpa = new SecRecPayAdmin();
        try {
            //删除卖出交易关联表中数据
            strSql = "delete from " +
                pub.yssGetTableName("TB_Data_TradeSellRela") +
                " where FNum in " +
                "(select FNum from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " where ftradetypecode = '" + YssOperCons.YSS_ZJDBLX_Income +
                "' and fbargaindate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + portCode + ")";
            dbl.executeSql(strSql);
            //删除证券应收应付表中数据
            srpa.setYssPub(pub);
//         srpa.delete("",dDate,dDate,"09,99", "09EQ,9909EQ,9905EQ", "", "",
//                     portCode, "", "", "", 0, -1);
            srpa.delete("", dDate, dDate, YssOperCons.YSS_ZJDBLX_MV + "," +
                        YssOperCons.YSS_ZJDBLX_FX,
                        YssOperCons.YSS_ZJDBZLX_EQ_MV + "," +
                        YssOperCons.YSS_ZJDBZLX_FX_EQ_MV + "," +
                        YssOperCons.YSS_ZJDBZLX_FX_EQ_Storage, "", "",
                        portCode, "", "", "", -99, -1); //MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    //因为在分红的除权日当天并不知道到帐日的汇率，所以在这里插入分红到帐的记录
    //处理方法：1.找出交易子表的交易类型为“分发派息”的记录
    //         2.再根据交易数据的结算日期取出前一日的汇率计算组合和基础货币金额
    //         3.插入到证券应收应付表，调拨类型为“收入”，调拨子类型为“股息收入”
    //备注：分红的现金流利用交易结算时产生的现金流  胡昆 20071009
    public void statDividendRev(java.util.Date dDate,
                                String sPortCode) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        double dBaseRate = 1;
        double dPortRate = 1;
        SecRecPayAdmin secPayAdmin = new SecRecPayAdmin();
        try {
            secPayAdmin.setYssPub(pub);
            strSql = "select a.*,b.*,c.FPortCury from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                //-------------------------------------------------
                " a left join (select FSecurityCode,FTradeCury,FCatCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) b" +
                " on a.FSecurityCode = b.FSecurityCode" +
                //-------------------------------------------------
                " left join (select FPortCode,FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) c" +
                " on a.FPortCode = c.FPortCode" +
                //-------------------------------------------------
                " where FCheckState = 1 and (FSettleDate = " +
                dbl.sqlDate(dDate) + " or FBargainDate = " + dbl.sqlDate(dDate) +
                " ) and a.FPortCode in (" + sPortCode + ")" +
                " and a.FTradeTypeCode = '06'";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                dBaseRate = rs.getDouble("FBaseCuryRate");
                dPortRate = rs.getDouble("FPortCuryRate");
				//添加投资类型的处理 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                /*
                 dBaseRate = this.getSettingOper().getCuryRate(YssFun.addDay(rs.
                      getDate("FSettleDate"), -1), rs.getString("FTradeCury"),
                      rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                 dPortRate = this.getSettingOper().getCuryRate(YssFun.addDay(rs.
                      getDate("FSettleDate"), -1), rs.getString("FPortCury"),
                      rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);
                 }
                 */
                if (YssFun.dateDiff(rs.getDate("FSettleDate"), dDate) == 0) { //到帐日期
                    //到帐日期的汇率也是采用除权日的汇率，便于冲减成本fazmm20071015
                    insertSecPecPay(rs.getDate("FSettleDate"),
                                    rs.getString("FPortCode"),
                                    rs.getString("FSecurityCode"),
                                    rs.getString("FInvMgrCode"),
                                    rs.getString("FBrokerCode"), "",
                                    "Dividend", rs.getDouble("FAccruedinterest"),
                                    dBaseRate, dPortRate, secPayAdmin, rs.getString("FTradeCury"),
                                    rs.getString("FAttrClsCode"), //属性分类代码
                                    rs.getString("FInvestType")); //投资类型
                } else { //除权日期
                    insertSecPecPay(rs.getDate("FBargainDate"),
                                    rs.getString("FPortCode"),
                                    rs.getString("FSecurityCode"),
                                    rs.getString("FInvMgrCode"),
                                    rs.getString("FBrokerCode"), "",
                                    "DividendRec", rs.getDouble("FAccruedinterest"),
                                    dBaseRate, dPortRate, secPayAdmin, rs.getString("FTradeCury"),
                                    rs.getString("FAttrClsCode"),   //属性分类代码
                                    rs.getString("FInvestType"));   //投资类型
                }
            }
            secPayAdmin.delete("", dDate, dDate, "02", "02DV%", "", "", sPortCode,
                               "", "", "", -99); //MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
            secPayAdmin.delete("", dDate, dDate, "06", "06DV%", "", "", sPortCode,
                               "", "", "", -99); //MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
            secPayAdmin.insert(dDate, dDate, "02,06", "02DV,06DV",
                               sPortCode, -99); //MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * refreshTradeCost
     *
     * @param dStartDate Date
     * @param dEndDate Date
     * @return double
     */
    public void refreshTradeCost(java.util.Date dStartDate,
                                 java.util.Date dEndDate, String sPortCode) throws
        YssException, SQLException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement pst = null;
        //=============end====================
        String strSql = "";
        ResultSet rs = null;
        YssCost cost = null;
        double dPhIncome = 0;
        double dBailMoney = 0;
        SecRecPayAdmin secPayAdmin = new SecRecPayAdmin();
        //----------- MS00269 QDV4中保2009年02月24日02_B --------
        CtlPubPara pubpara = null;
        boolean isFourDigit = false;
        //-----------------------------------------------------
        String message = ""; //------ add by wangzuochun 2010.10.13 MS01840    【资产估值】证券库存统计时报错    QDV4太平2010年10月11日（开发部）01_B   
        boolean calculatCost = false;//add by yanghaiming 20101206 
        boolean costIncludeInts=false;// add by zhouwei 20120416 新债中签，利息默认不入成本
        try {
        	//----------- 合并太平版本调整------------------------
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            String verTp = pubpara.getNavType();//通过净值表类型来判断
        	if(verTp!=null && verTp.trim().equalsIgnoreCase("new")){
        		isFourDigit=false;//国内QDII统计模式
        	}else{
        		isFourDigit=true;//太平资产统计模式
        	}
            //--------------合并太平版本调整-----------------------------------------
            ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
                "avgcostcalculate");
            secPayAdmin.setYssPub(pub);
//         conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " set FCost = ?," +
                " FMCost = ?, FVCost = ?, FBaseCuryCost = ?, FMBaseCuryCost = ?, FVBaseCuryCost = ?," +
                " FPortCuryCost = ?, FMPortCuryCost = ?, FVPortCuryCost = ? where FNum = ?";
			//modified by liubo.Story #2145
			//==============================
//            pst = conn.prepareStatement(strSql);
            pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
            strSql = "select * from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " a left join (select FFeeCode,FAccountingWay as FAW1 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") b on a.FFeeCode1 = b.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW2 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") c on a.FFeeCode2 = c.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW3 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") d on a.FFeeCode3 = d.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW4 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") e on a.FFeeCode4 = e.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW5 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f on a.FFeeCode5 = f.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW6 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") g on a.FFeeCode6 = g.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW7 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") h on a.FFeeCode7 = h.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW8 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") i on a.FFeeCode8 = i.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FSecurityCode as FSecurityCode_j,FCatCode,FSubCatCode,FTradeCury,FExchangeCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") j on a.FSecurityCode = j.FSecurityCode_j" +
                //--------------------------------------------------------
                " left join (select FSecurityCode as FSecurityCode_k,FMultiple,FBailType,FBailScale,FBailFix,ffutype from " +
                pub.yssGetTableName("Tb_Para_IndexFutures") +
                ") k on a.FSecurityCode = k.FSecurityCode_k" +
                " where (FBargainDate " +
                " between " + dbl.sqlDate(dStartDate) + " and " +
                dbl.sqlDate(dEndDate) +
                //V4.1_ETF:MS00002 2009-11-11 edit by songjie 修改了FPortCode in (" + sPortCode + 语句 以防报错
                /**shashijie 2011.06.23 STORY #974 按照华宝兴业ETF的产品规则，定义出符合该产品的功能需求*/
               	" ) and FCheckState = 1 and FPortCode in (" + operSql.sqlCodes(sPortCode) +//删除掉 FSecurity='988'，可能是谁开发测试时留下的 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
               	/**end*/
               	
               	/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               	* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//               	")"+(statCodes.length()>0?" and a.FSecurityCode in("+operSql.sqlCodes(statCodes)+")":"")+//如果证券代码不为空则作为条件  by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
               	")"+(statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"a.FSecurityCode",500) + ")" :"")+
               	/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               	(unStgTradeTypeCode.length()>0?(" and a.FTradeTypeCode not in("+operSql.sqlCodes(unStgTradeTypeCode)+")"):"")+//添加对不统计的交易类型处理 by leeyu 20100417 QDV4中保2010年4月14日02_B MS01092 合并太平版本代码
                " order by FNum";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	/**shashijie 2012-5-8 STORY 2565 ETF联接基金*/
				//如是一下一种交易类型则不计算成本
            	if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ETFSGou)//申购
            			|| rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ETFLJSGSB)//申购失败
						//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 start---//
            			|| rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ETFSGTK)//申购退款
            			|| rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ETFSGBK)//申购补款
						//---edit by songjie 2012.12.04 STORY #3342 需求北京-[中国银行]QDV4.0[高]20121127001 end---//
            			|| rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ETFSHTBK)//赎回退补款
            			//add by songjie 2012.08.20 STORY #2538 QDV4赢时胜(上海开发部)2012年04月21日01_A 配股则不重新计算成本
            			|| rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_PG)//配股
            			//add by songjie 2013.04.12 STORY #3365 需求北京-[中国银行]QDV4.0[中]20121203001
            			|| rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_PXRC)//分红派息（资本返还）
            	) {
					continue;
				}
				/**end*/
            	message = rs.getString("FTradeTypeCode");//------ add by wangzuochun 2010.10.13 MS01840    【资产估值】证券库存统计时报错    QDV4太平2010年10月11日（开发部）01_B   
                //-----------交易币有效性检查 by 曹丞 2009.01.22 MS00004 QDV4.1-2009.2.1_09A-------//
                if (rs.getString("FTradeCury") == null || rs.getString("FTradeCury").trim().length() == 0) {
                    throw new YssException("系统进行证券库存统计,在证券兑换交易统计时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的币种信息不存在!" + "\n" +
                                           "请核查以下信息：" + "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                           "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                }
                //---add by songjie 2012.04.12 STORY #2320 QDV4长信基金2012年02月28日01_A start---//
                if (rs.getString("FTradeTypeCode").equalsIgnoreCase(//不更新送股业务交易数据成本
                        YssOperCons.YSS_JYLX_SG)) {
                	continue;
                }
                //---add by songjie 2012.04.12 STORY #2320 QDV4长信基金2012年02月28日01_A end---//
                //------------------------------------------------------------------------//
                if (rs.getString("FTradeTypeCode").equalsIgnoreCase(
                    YssOperCons.YSS_JYLX_PX)) { //派息
                    //屏蔽统计派息的应收 2008.10.06 蒋锦
//               insertSecPecPay(rs.getDate("FBargainDate"),
//                               rs.getString("FPortCode"),
//                               rs.getString("FSecurityCode"),
//                               rs.getString("FInvMgrCode"),
//                               rs.getString("FBrokerCode"), "",
//                               YssOperCons.YSS_JYLX_PX,
//                               rs.getDouble("FAccruedinterest"),
//                               rs.getDouble("FBaseCuryRate"),
//                               rs.getDouble("FPortCuryRate"), secPayAdmin);
                } else {
                    //MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A 添加赎回、债转股业务也要转出成本
                    if ( (rs.getString("FTradeTypeCode") +
                          "").equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)
                         || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_OVERSELL_Buy)  //add by zhaoxianlin #story 3208
                        || (rs.getString("FTradeTypeCode") +
                            "").equalsIgnoreCase(YssOperCons.Yss_JYLX_ZQ) ||
                      //rs.getString("FTradeTypeCode").equalsIgnoreCase("23") ||
                      rs.getString("FTradeTypeCode").equalsIgnoreCase("21") ||
                      rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZZG) ||
                      rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_KZZHS) ||
                      rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_SH) || //卖出，债券兑付，配股权证冲减，期货平仓
                      /**shashijie 2012-5-7 STORY 2565 */
                      rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ETFSH)//赎回申请
                      /**end*/                     
                      || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REGOU_SSTATEExercis)  //story 2754 fangjiang add 2012.08.23
                      || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REGU_BSTATEExercis)   //story 2754 fangjiang add 2012.08.23                   
                    ) { 
                        //2009-07-03 蒋锦 修改 添加属性分类代码，获取库存成本时需要区分属性分类
                        //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A
                        costCal.initCostCalcutate(rs.getDate("FBargainDate"),
                                                  rs.getString("FPortCode"),
                                                  rs.getString("FInvMgrCode"),
                                                  rs.getString("FBrokerCode"),
                                                  rs.getString("FAttrClsCode"));
                        costCal.setYssPub(pub);
                        cost = costCal.getCarryCost(rs.getString("FSecurityCode"),
                            rs.getDouble("FTradeAmount"),
                            YssFun.left(rs.getString("FNum") +
                                        "", (rs.getString("FNum") + "").length() - 5),
                            rs.getDouble("FBaseCuryRate"),
                            rs.getDouble("FPortCuryRate"));
                  		costCal.roundCost(cost, isFourDigit? 4:2);//保留四位小数  QDV4中保2010年5月14日01_B by leeyu 20100520
                        insertSecPecPay(rs.getDate("FBargainDate"),
                                        rs.getString("FPortCode"),
                                        rs.getString("FSecurityCode"),
                                        rs.getString("FInvMgrCode"),
                                        rs.getString("FBrokerCode"), "",
                                        rs.getString("FTradeTypeCode"),
                                        rs.getDouble("FAccruedinterest"),
                                        rs.getDouble("FBaseCuryRate"),
                                        rs.getDouble("FPortCuryRate"), secPayAdmin,
                                        rs.getString("FTradeCury"),
                                        rs.getString("FAttrClsCode"),   //属性分类代码
                                        rs.getString("FInvestType"));   //投资类型
                        //add by zhouxiang 2010.12.7  证券借贷--交易数据如果借入卖空操作的话在证券借贷产生一笔买入的交易数据
                        //ProduceTradeBL(rs,cost);//如果前一日没有该笔证券的库存就需要产生一笔证券借贷的借入交易数据   modify by fangjiang 2011.11.21 story 1433
                        //end by zhouxiang 2010.12.7  证券借贷--交易数据如果借入卖空操作的话在证券借贷产生一笔买入的交易数据
                        //增加了股指期货的空投和多投的判断，用来处理收益 sun 080325
                        if (rs.getString("FTradeTypeCode").equalsIgnoreCase("21")) { //期货平仓计算期货的收益，插入资金调拨表
                            if (rs.getString("FFutype").equalsIgnoreCase("SellAM")) {
                                this.insertTransfer(rs.getDate("FBargainDate"),
                                    rs.getDate("FFactSettleDate"), "",
                                    rs.getString("FPortCode"),
                                    rs.getString("FCashAccCode"),
                                    rs.getString("FInvMgrCode"),
                                    rs.getString("FBrokerCode"),
                                    "", rs.getString("FTradeTypeCode"),
                                    YssD.mul(YssD.sub(rs.getDouble(
                                        "FTradeMoney"),
                                    cost.getCost()), -1),
                                    rs.getDouble("FBaseCuryRate"),
                                    rs.getDouble("FPortCuryRate"));
                            } else {
                                this.insertTransfer(rs.getDate("FBargainDate"),
                                    rs.getDate("FFactSettleDate"), "",
                                    rs.getString("FPortCode"),
                                    rs.getString("FCashAccCode"),
                                    rs.getString("FInvMgrCode"),
                                    rs.getString("FBrokerCode"),
                                    "", rs.getString("FTradeTypeCode"),
                                    YssD.sub(rs.getDouble("FTradeMoney"),
                                             cost.getCost()),
                                    rs.getDouble("FBaseCuryRate"),
                                    rs.getDouble("FPortCuryRate"));
                            }
                        }
                    }
                    /**shashijie 2012-5-7 STORY 2565 //处理赎回失败冲销*/
                    else if(rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ETFLJSHSB)){
                    	costCal.setYssPub(pub);
                    	//初始
                    	costCal.initCostCalcutate(rs.getDate("FBargainDate"),
  	                          rs.getString("FPortCode"),
  	                          " ",//分析代码1
  	                          " ",//分析代码2
  	                          " " //所属分类
  	                          );
                    	//处理
                    	cost = doOperionCost(costCal,
                    			rs.getString("FSecurityCode"),//证券
                    			rs.getString("FNum"),//编号
                    			rs.getDouble("FTradeAmount") //失败数量
                    			
                    			);
                    	costCal.roundCost(cost, 2);//保留二位小数
                    }
					/**end*/
                    else {
                    	double tradeMoney=rs.getDouble("FTradeMoney");
                    	//add by zhouwei 20120416 新债网上中签，根据通参设定，利息是否计入成本
                    	if(rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_XZWSZQ)){
                            costIncludeInts=pubpara.getCostIncludeInterestsOfZQ(rs.getString("FPortCode"));
                            if(!costIncludeInts){//不入成本
                            	tradeMoney=YssD.sub(tradeMoney, rs.getDouble("FAccruedinterest"));
                            }
                    	}
                    	//---------end-----------
                        cost = this.calCost(tradeMoney,
                                            rs.getDouble("FTradeFee1"),
                                            rs.getInt("FAW1"),
                                            rs.getDouble("FTradeFee2"),
                                            rs.getInt("FAW2"),
                                            rs.getDouble("FTradeFee3"),
                                            rs.getInt("FAW3"),
                                            rs.getDouble("FTradeFee4"),
                                            rs.getInt("FAW4"),
                                            rs.getDouble("FTradeFee5"),
                                            rs.getInt("FAW5"),
                                            rs.getDouble("FTradeFee6"),
                                            rs.getInt("FAW6"),
                                            rs.getDouble("FTradeFee7"),
                                            rs.getInt("FAW7"),
                                            rs.getDouble("FTradeFee8"),
                                            rs.getInt("FAW8"),
                                            rs.getDouble("FBaseCuryRate"),
                                            rs.getDouble("FPortCuryRate"),
                                            (rs.getDate("FRateDate") == null || (YssFun.formatDate(rs.getDate("FRateDate"), "yyyy-MM-dd")).equalsIgnoreCase("9998-12-31") ?
                                             rs.getDate("FBargainDate") : rs.getDate("FRateDate")), //如果汇率日期为null或为9998-12-31，则使用业务日期的值。sj modified 20081210
                                            rs.getString("FPortCode"),
                                            rs.getString("FTradeCury"));
                        //MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A 2009.08.29 蒋锦 添加新债网上中签 老股东配售
                        if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy) ||
                            rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_OVERSELL_Sale) ||  //add by zhaoxianlin #story 3208 20121126
                            rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_XZWSZQ) ||
                            rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZQLGDPS)) {
                        	//add by zhouwei 20120425 利息入成本，不需要产生06FI_B的证券应收应付数据
                        	if(rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_XZWSZQ)){
                                costIncludeInts=pubpara.getCostIncludeInterestsOfZQ(rs.getString("FPortCode"));
                                if(!costIncludeInts){//不入成本
                                	 if (rs.getDouble("FAccruedinterest") != 0) {
                                         insertSecPecPay(rs.getDate("FBargainDate"),
                                                         rs.getString("FPortCode"),
                                                         rs.getString("FSecurityCode"),
                                                         rs.getString("FInvMgrCode"),
                                                         rs.getString("FBrokerCode"), "",
                                                         YssOperCons.YSS_JYLX_Buy,
                                                         rs.getDouble("FAccruedinterest"),
                                                         rs.getDouble("FBaseCuryRate"),
                                                         rs.getDouble("FPortCuryRate"),
                                                         secPayAdmin,
                                                         rs.getString("FTradeCury"),
                                                         rs.getString("FAttrClsCode"),   //属性分类代码
                                                         rs.getString("FInvestType"));   //投资类型
                                     }
                                }
                        	}else{
                        		 if (rs.getDouble("FAccruedinterest") != 0) {
                                     insertSecPecPay(rs.getDate("FBargainDate"),
                                                     rs.getString("FPortCode"),
                                                     rs.getString("FSecurityCode"),
                                                     rs.getString("FInvMgrCode"),
                                                     rs.getString("FBrokerCode"), "",
                                                     YssOperCons.YSS_JYLX_Buy,
                                                     rs.getDouble("FAccruedinterest"),
                                                     rs.getDouble("FBaseCuryRate"),
                                                     rs.getDouble("FPortCuryRate"),
                                                     secPayAdmin,
                                                     rs.getString("FTradeCury"),
                                                     rs.getString("FAttrClsCode"),   //属性分类代码
                                                     rs.getString("FInvestType"));   //投资类型
                                 }
                        	}                         
                        }            
                        
                        /**Start 20130917 deleted by liubo.Bug #79701.*/
                        
                        //add by zhouwei 20120217 对于交易所为GS,CG的交易类型为买入的,国内接口导入的交易数据    start----------
//                    	if((rs.getString("FExchangeCode").equalsIgnoreCase("CS")  || rs.getString("FExchangeCode").equalsIgnoreCase("CG")) 
//                    			&& rs.getString("FTradeTypeCode").equals(YssOperCons.YSS_JYLX_Buy) && "1".equals(rs.getString("fjkdr"))){
//                    		continue;
//                    	}                    
                        /**End 20130917 deleted by liubo.Bug #79701.*/
                        
                    	//add by zhouwei 20120217 对于交易所为GS,CG的交易类型为买入的交易数据库存统计时，不去重新计算成本   end----------                   	
//                  else if ( (rs.getString("FCatCode").equalsIgnoreCase("FU") &&
//                             rs.getString("FSubCatCode").equalsIgnoreCase(
//                        "FU01")) &&
//                           (rs.getString("FTradeTypeCode").equalsIgnoreCase(
//                        "20") ||
//                            rs.getString("FTradeTypeCode").equalsIgnoreCase(
//                        "21"))) { //品种类型为"期货"并且品种子类型为"股指期货"并且交易类型为"认购期货"或者"认沽期货"
//                     dBailMoney = this.getSettingOper().calcFuturesBail(rs.
//                           getString("FBailType"), rs.getDouble("FTradeAmount"),
//                           rs.getDouble("FTradePrice"),
//                           rs.getDouble("FMultiple"),
//                                           rs.getDouble("FBailScale"),
//                                           rs.getDouble("FBailFix"));
//                  }注释原因：因为保证金计算出来后，可能被用户修改。
                        //把对回购的处理分散处理,详细的在方法中说明 sj edit 20080217
                        //else if (rs.getString("FTradeTypeCode").equalsIgnoreCase("24") ||
                        //rs.getString("FTradeTypeCode").equalsIgnoreCase("25")) { //add sj 20071116 回购的计算
                        //dPhIncome = rs.getDouble("FAccruedinterest");
                        //if (rs.getInt("FAW1") == 0) { //计入成本
                        //dPhIncome = YssD.sub(dPhIncome,
                        //rs.getDouble("FTradeFee1"));
                        //}
                        //if (rs.getInt("FAW2") == 0) { //计入成本
                        //dPhIncome = YssD.sub(dPhIncome,
                        //rs.getDouble("FTradeFee2"));
                        //}
                        //if (rs.getInt("FAW3") == 0) { //计入成本
                        //dPhIncome = YssD.sub(dPhIncome,
                        //rs.getDouble("FTradeFee3"));
                        //}
                        //if (rs.getInt("FAW4") == 0) { //计入成本
                        //dPhIncome = YssD.sub(dPhIncome,
                        //rs.getDouble("FTradeFee4"));
                        //}
                        //if (rs.getInt("FAW5") == 0) { //计入成本
                        //dPhIncome = YssD.sub(dPhIncome,
                        //rs.getDouble("FTradeFee5"));
                        //}
                        //if (rs.getInt("FAW6") == 0) { //计入成本
                        //dPhIncome = YssD.sub(dPhIncome,
                        //rs.getDouble("FTradeFee6"));
                        //}
                        //if (rs.getInt("FAW7") == 0) { //计入成本
                        //dPhIncome = YssD.sub(dPhIncome,
                        //rs.getDouble("FTradeFee7"));
                        //}
                        //if (rs.getInt("FAW8") == 0) { //计入成本
                        //dPhIncome = YssD.sub(dPhIncome,
                        //rs.getDouble("FTradeFee8"));
                        //}
                        //--------------为了在一个到期日期有两笔业务时，能够完整的插入，把应收应付的插入放入StgSecRecPay中的--
                        //--------------beforeSaveStorage方法中 sj 20080217 ----------------------------------------//
                        //insertSecPecPay(rs.getDate("FMatureDate"), //往证券应收应付款插入数据，回购收益
                        //rs.getString("FPortCode"),
                        //rs.getString("FSecurityCode"),
                        //rs.getString("FInvMgrCode"),
                        //rs.getString("FBrokerCode"), "",
                        //rs.getString("FTradeTypeCode"),
                        //dPhIncome,
                        //rs.getDouble("FBaseCuryRate"),
                        //rs.getDouble("FPortCuryRate"), secPayAdmin);
                        //secPayAdmin.delete("", rs.getDate("FMatureDate"),
                        //rs.getDate("FMatureDate"), "02,03",
                        //"02RE,03RE",
                        //rs.getString("FSecurityCode"), "",
                        //rs.getString("FPortCode"),
                        //rs.getString("FInvMgrCode"),
                        //rs.getString("FBrokerCode"), "", 0); //因为回购的到期日可能会在统计库存的日期范围之外，所以在这里加删除
                        //-------------------------------------------------------------------------------------------------------------------
                        //------------------资金调拨插入放入StgCash中的-----------------------------------------------//
                        //--------------beforeSaveStorage方法中 sj 20080217 ----------------------------------------//
                        //插入回购到期的资金调拨
                        //insertTransfer(rs.getDate("FMatureDate"),
                        //rs.getDate("FMatureSettleDate"),
                        //rs.getString("FNum"),
                        //rs.getString("FPortCode"),
                        //rs.getString("FCashAccCode"),
                        //rs.getString("FInvMgrCode"),
                        //rs.getString("FBrokerCode"), "",
                        //rs.getString("FTradeTypeCode"),
                        //YssD.add(rs.getDouble("FTradeMoney"),
                        //rs.getDouble("FAccruedinterest")),
                        //rs.getDouble("FBaseCuryRate"),
                        //rs.getDouble("FPortCuryRate"));
                        //---------------------------------------------------------------------------------------------
                        //}
                    }
                   //add by zhouwei 20120309 手动修改成本的数据，做库存统计时 不去重新计算成本
                 	if("1".equals(rs.getString("fhandcoststate"))){
                 		continue;
                 	}
                    pst.setDouble(1, cost.getCost());
                    pst.setDouble(2, cost.getMCost());
                    pst.setDouble(3, cost.getVCost());
                    pst.setDouble(4, cost.getBaseCost());
                    pst.setDouble(5, cost.getBaseMCost());
                    pst.setDouble(6, cost.getBaseVCost());
                    pst.setDouble(7, cost.getPortCost());
                    pst.setDouble(8, cost.getPortMCost());
                    pst.setDouble(9, cost.getPortVCost());
                    pst.setString(10, rs.getString("FNum"));
                    pst.executeUpdate();
                }
            }
            
//         secPayAdmin.insert(dStartDate, dEndDate, "06,07,02,03",
//                            "06FI_B,07FI_B,06EQ,02FI03,06DV", //加入06DV的子调拨类型 sj edit 20080219
//                            sPortCode, 0);
            //----------- MS00269 QDV4中保2009年02月24日02_B ------------------------
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            String digit = pubpara.getKeepFourDigit(); //获取通用参数中的小数保留位数
            if (digit.toLowerCase().equalsIgnoreCase("two")) { //两位
                isFourDigit = false;
            } else if (digit.toLowerCase().equalsIgnoreCase("four")) { //四位
                isFourDigit = true;
            }
            //---------------------------------------------------------------------
            //--增加了02FI_B的筛选条件。目的是为了以后用02FI_B替换07FI_B.为了可兼容历史数据，07FI_B的筛选条件保留.sj modified 20081229 MS00121
//         secPayAdmin.insert(dStartDate, dEndDate, "06,07,02,03",
//                            "06FI_B,07FI_B,06EQ,02FI03,06DV,02FI_B",
//                            sPortCode, 0);
//----------- MS00269 QDV4中保2009年02月24日02_B ---------------------------------------------------------------------------------------------------
            //2009.04.27 蒋锦 添加 增加证券应收应付的事务控制
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            conn.setAutoCommit(false);
            bTrans = true;
            secPayAdmin.insert(dStartDate, dEndDate, "06,07,02,03",
                               "06FI_B,07FI_B,06EQ,02FI03,06DV,02FI_B",
                            	sPortCode, -99, isFourDigit && bondDigitF ? true:false,statCodes);//设置小数位数，为4位还是2位。当有债券的卖出利息的小数位大于2，并且通用参数设置为舍入4位时，才保留四位小数。//添加证券代码作以条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
            //MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
//-----------------------------------------------------------------------------------------
            //---------------------------------------------------------------------------------------------------------------------
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
          //add by yanghaiming 20101206 QDV4赢时胜（深圳）2010年11月30日  如果通用参数（成本计算方式）设置成先入先出，则卖出成本重新计算
            String[] arryPort = YssFun.split(sPortCode, ",");
            for (int i = 0; i < arryPort.length; i++) {//循环组合代码，再判断是否使用先入先出法重新计算卖出成本
            	CtlPubPara pubPara = new CtlPubPara();
                pubPara.setYssPub(pub);
                calculatCost = pubPara.getCalculatCost(arryPort[i].replaceAll("'", ""));
                if(calculatCost){//如果为先入先出算法，则卖出成本需重新计算
                	refreshTradeCost1(dStartDate,arryPort[i].replaceAll("'", ""));
                }
            }
        } catch (Exception e) {
            //------ modify by wangzuochun 2010.10.13 MS01840    【资产估值】证券库存统计时报错    QDV4太平2010年10月11日（开发部）01_B   
            if (message != null && message.length() > 0)
            {
	        	if (YssOperCons.YSS_JYLX_PX.equals(message)) {
	                throw new YssException("在重新计算派息成本时出现异常!\n", e);
	            }
	            if (YssOperCons.YSS_JYLX_Sale.equals(message)) {
	                throw new YssException("在重新计算卖出成本时出现异常!\n", e);
	            }
	            if (YssOperCons.Yss_JYLX_ZQ.equals(message)) {
	                throw new YssException("在重新计算债券兑付成本时出现异常!\n", e);
	            }
	            if (YssOperCons.YSS_JYLX_PC.equals(message)) {
	                throw new YssException("在重新计算期货平仓成本时出现异常!\n", e);
	            }
	            if (YssOperCons.YSS_JYLX_PC.equals(message)) {
	                throw new YssException("在重新计算买入成本时出现异常!\n", e);
	            } else {
	                throw new YssException("在重新计算交易成本时出现异常!\n", e);
	            }
            }
            else{
            	throw new YssException("在重新计算交易成本时出现异常!\n", e);
            }
            //---------------------------MS01840----------------------------//
        } finally { // by 曹丞 2009.01.21 检查出重新计算某一类业务成本出错 MS00004 QDV4.1-2009.2.1_09A
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    /**shashijie 2012-5-7 STORY 2565 
	* @param FSecurityCode 证券
	* @param FNum 编号
	* @param FTradeAmount 失败数量
	* @return*/
	private YssCost doOperionCost(ICostCalculate costCal,String FSecurityCode,String FNum,
			double FTradeAmount) throws YssException {
		YssCost cost = null;//成本对象
		ResultSet rs = null;
		double costValue = 0;
		double FCost = 0;//赎回申请成本
		try {
			//获取赎回关联数据
			String query = getQuery(FNum,FSecurityCode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				//赎回申请成本
				cost = costCal.getCarryCost(
		    			rs.getString("FSecurityCode"),//证券
		                YssD.add(rs.getDouble("FTradeAmount"), FTradeAmount),//确认数量,(申请-失败,失败数量是负的所以这里用加)
		                //关联出来的赎回申请编号
		                YssFun.left(rs.getString("FNum") + "", (rs.getString("FNum") + "").length() - 5),
		                rs.getDouble("FBaseCuryRate"),//基础汇率
		                rs.getDouble("FPortCuryRate"),//组合汇率
		                YssFun.formatDate(rs.getDate("FBSDate"))//申赎日期
		                );
				//赎回申请成本
				FCost = rs.getDouble("FCost");
				//原币成本 = 赎回确认成本  - 赎回申请成本
				costValue = YssD.sub(YssD.round(cost.getCost(),2), FCost);
			}
			
//			//原币成本 = 赎回确认成本  - 赎回申请成本
//			double costValue = YssD.sub(YssD.round(cost.getCost(),2), FCost);
			//基础货币
			double baseCost = YssD.mul(costValue, rs.getDouble("FBaseCuryRate"));
			//组币成本
			double portCost = YssD.div(baseCost, rs.getDouble("FPortCuryRate"));
			//存入对象
			setYssCostValue(cost, YssD.round(costValue,2), YssD.round(baseCost,2) , YssD.round(portCost,2));
		} catch (NullPointerException ex) {//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
            throw new YssException(ex.getMessage());	
		} catch (Exception e) {
			throw new YssException("计算ETF联接基金成本出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return cost;
	}

	/**shashijie 2012-5-7 STORY 2565 
	* @param fNum
	* @param fSecurityCode
	* @return*/
	private String getQuery(String fNum, String fSecurityCode) {
		String query = 
			" Select b.Fnum, "+
			" b.Fsecuritycode, "+//证券
			" b.Ftradeamount, "+//确认数量
			" b.Fbasecuryrate, "+//基础汇率
			" b.Fportcuryrate, "+//组合汇率
			" b.Fbsdate , "+//申赎日期
			" b.FCost "+//原币成本
			" From  "+pub.yssGetTableName("Tb_Data_Subtrade")+" a "+
			" Join "+pub.yssGetTableName("Tb_Data_Subtrade")+" b On a.Fsecuritycode = b.Fsecuritycode "+
			" And a.Fbrokercode = b.Fbrokercode"+
			" And a.Fportcode = b.Fportcode"+
			" And a.Fstockholdercode = b.Fstockholdercode"+
			" And a.Fbsdate = b.Fbargaindate"+

			" Where a.Fsecuritycode = "+dbl.sqlString(fSecurityCode)+
			" And a.Fnum = "+dbl.sqlString(fNum)+
			" And b.Ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_JYLX_ETFSH);
		return query;
	}

	/**shashijie 2012-4-28 STORY 2565 成本对象赋值 */
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
	
	/* story 1936 重新计算综合业务数据的证券成本数据(流出)
     * zhouwei 20111223 QDV4赢时胜(上海开发部)2011年11月28日01_A
     * */
    public void refreshIntegratedSecurityCost(java.util.Date dStartDate,
            java.util.Date dEndDate, String sPortCode) throws YssException{   	
    	ResultSet rs=null;
    	YssCost cost = null;
    	CtlPubPara pubpara = null;
        boolean isFourDigit = false;
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement pst = null;
        //=============end====================
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
    	try{
    		 pubpara = new CtlPubPara();
             pubpara.setYssPub(pub);
    		 pubpara = new CtlPubPara();
             pubpara.setYssPub(pub);
             String verTp = pubpara.getNavType();//通过净值表类型来判断
         	 if(verTp!=null && verTp.trim().equalsIgnoreCase("new")){
         		isFourDigit=false;//国内QDII统计模式
         	 }else{
         		isFourDigit=true;//太平资产统计模式
         	 }
         	 
         	 
         	//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420
         	int count =1;
         	conn.setAutoCommit(false);
		    bTrans=true;
         	 
         	ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean("avgcostcalculate");
         	String strSql = "update " + pub.yssGetTableName("tb_data_Integrated") +
            " set FEXCHANGECOST = ?," +
            " FMEXCOST = ?, FVEXCOST = ?, FBASEEXCOST = ?, FMBASEEXCOST = ?, FVBASEEXCOST = ?," +
            " FPORTEXCOST = ?, FMPORTEXCOST = ?, FVPORTEXCOST = ? where FNum = ? and FSUBNUM=?";
			//modified by liubo.Story #2145
			//==============================
//         	pst=conn.prepareStatement(strSql);
         	pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
         	strSql="select *"
			  		      +" from "+pub.yssGetTableName("tb_data_Integrated")+" a where FEXCHANGECOST=0"//bug 4489 modify by zhouwei 20120524 对综合业务数据中成本为0的流出数据重新计算成本
			  		      +" and a.fcheckstate=1  and a.finouttype=-1"
			  		      +" and a.FPortCode in ("+operSql.sqlCodes(sPortCode)+")"
			  		      +" and a.FOperDate>="+dbl.sqlDate(dStartDate)
			  		      +" and a.FOperDate<="+dbl.sqlDate(dEndDate);
	          strSql+=" order by a.fnum,a.fsubnum";
		      rs=dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);
		      while(rs.next()){
			    	  costCal.initCostCalcutate(rs.getDate("FOperDate"),
	                          rs.getString("FPortCode"),
	                          rs.getString("FANALYSISCODE1").length()==0?" ":rs.getString("FANALYSISCODE1"),
	                          rs.getString("FANALYSISCODE2").length()==0?" ":rs.getString("FANALYSISCODE2"),
	                          rs.getString("FAttrClsCode"));
			    	  costCal.setYssPub(pub);
			    	  
			    	//modify huangqirong 2012-06-27 story #2727
					String isNeedTrade = "";  
					SecurityBean security = new SecurityBean();
					security.setYssPub(pub);
					String subType = security.getSecSubType(rs.getString("FSecurityCode"));
					String newIsNeed = pubpara.getAddAVG(rs.getString("FPortCode"), subType);
					
					if(newIsNeed.length() > 0){
						if(newIsNeed.indexOf(",") > -1)
			        		isNeedTrade = newIsNeed.split("\\,")[0];
			       	 	else if(newIsNeed.indexOf("|") > -1)
			       	 		isNeedTrade = newIsNeed.split("\\|")[0];     	 
					}else{             
						isNeedTrade= pubpara.getAvgCost(rs.getString("FPortCode"));
					}
			         //---end---
			            
			    	  if(isNeedTrade.equalsIgnoreCase("Yes(Integrated)")){//加权平均成本计算当天综合，关联交易
			    		  cost = costCal.getNewCarryCost(rs.getString("FSecurityCode"),
			  					  rs.getDouble("FAMOUNT"),
								  YssFun.left(rs.getString("FNum") +
								                "", (rs.getString("FNum") + "").length() - 5),
								  rs.getDouble("FBaseCuryRate"),
								  rs.getDouble("FPortCuryRate"));
			    	  }else{
			    		  cost = costCal.getCarryCost(rs.getString("FSecurityCode"),
			  					  rs.getDouble("FAMOUNT"),
								  YssFun.left(rs.getString("FNum") +
								                "", (rs.getString("FNum") + "").length() - 5),
								  rs.getDouble("FBaseCuryRate"),
								  rs.getDouble("FPortCuryRate"));
			    	  }			    	  
			    	  costCal.roundCost(cost, isFourDigit? 4:2);//保留四位小数
			    	  pst.setDouble(1, cost.getCost());
                      pst.setDouble(2, cost.getMCost());
                      pst.setDouble(3, cost.getVCost());
                      pst.setDouble(4, cost.getBaseCost());
                      pst.setDouble(5, cost.getBaseMCost());
                      pst.setDouble(6, cost.getBaseVCost());
                      pst.setDouble(7, cost.getPortCost());
                      pst.setDouble(8, cost.getPortMCost());
                      pst.setDouble(9, cost.getPortVCost());
                      pst.setString(10, rs.getString("FNum"));
                      pst.setString(11, rs.getString("FSUBNUM"));
                      pst.addBatch();
                    //add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420  start
                      
                      if(count == 500){
                    	  pst.executeBatch();
                    	  count = 1;
						  continue;
                      }

					  count++;
                    //add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420  end 
		      }
		      
		      pst.executeBatch();
		      conn.commit();
		      conn.setAutoCommit(true);
		      bTrans=false;
    	}catch(Exception e){
    		throw new YssException("重新计算综合业务证券成本出错"+e.getMessage(),e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		dbl.closeStatementFinal(pst);
    		dbl.endTransFinal(conn, bTrans);
    	}
    }
    
    /* story 1936 重新计算交易关联数据的成本数据(流出)
     * zhouwei 20111223 QDV4赢时胜(上海开发部)2011年11月28日01_A
     * */
    private void refreshTradeRelaCost(java.util.Date dStartDate,
            java.util.Date dEndDate, String sPortCode) throws YssException{   	
    	ResultSet rs=null;
    	YssCost cost = null;
    	CtlPubPara pubpara = null;
        boolean isFourDigit = false;
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement pst = null; 
        //=============end====================

        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        
   	 //add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 start 
   		int count = 1;

    	try{
    	//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 start 	
  	      conn.setAutoCommit(false);
  	      bTrans=true;
  	      
    		 pubpara = new CtlPubPara();
             pubpara.setYssPub(pub);
    		 pubpara = new CtlPubPara();
             pubpara.setYssPub(pub);
             String verTp = pubpara.getNavType();//通过净值表类型来判断
         	 if(verTp!=null && verTp.trim().equalsIgnoreCase("new")){
         		isFourDigit=false;//国内QDII统计模式
         	 }else{
         		isFourDigit=true;//太平资产统计模式
         	 }
         	ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean("avgcostcalculate");
         	String strSql = "update " + pub.yssGetTableName("TB_DATA_TRADERELA") +
            " set FCOST = ?, FMCOST = ?, FVCOST = ?, FBASECURYCOST = ?, FMBASECURYCOST = ?,FVBASECURYCOST=?" +
            " FPORTCURYCOST = ?, FMPORTCURYCOST = ?, FVPORTCURYCOST = ? where FNum = ?";
			//modified by liubo.Story #2145
			//==============================
//         	pst=conn.prepareStatement(strSql);
         	pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
         	strSql="select a.*,b.FBargainDate as FBargainDate,b.FBaseCuryRate,b.FPortCuryRate"
			  		      +" from "+pub.yssGetTableName("TB_DATA_TRADERELA")+" a where ((a.FRelaNum = ' ' and a.FNumType = ' ') or "
			  		      +" join (select * from "+pub.yssGetTableName("Tb_Data_SubTrade")+" b1"
			  		      +" where b1.FCheckState = 1) b on a.FNum = b.FNum"
			  		      +" where a.fcheckstate=1  and a.FINOUT=-1"
			  		      +" and a.FPortCode in ("+operSql.sqlCodes(sPortCode)+")"
			  		      +" and b.FBargainDate>="+dbl.sqlDate(dStartDate)
			  		      +" and b.FBargainDate<="+dbl.sqlDate(dEndDate);
	          strSql+=" order by a.fnum";
		      rs=dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);
		      while(rs.next()){
			    	  costCal.initCostCalcutate(rs.getDate("FBargainDate"),
	                          rs.getString("FPortCode"),
	                          rs.getString("FANALYSISCODE1").length()==0?" ":rs.getString("FANALYSISCODE1"),
	                          rs.getString("FANALYSISCODE2").length()==0?" ":rs.getString("FANALYSISCODE2"),
	                          rs.getString("FAttrClsCode"));
			    	  costCal.setYssPub(pub);
			    	  cost = costCal.getNewCarryCost(rs.getString("FSecurityCode"),
			    			  					  rs.getDouble("FAMOUNT"),
												  YssFun.left(rs.getString("FNum") +
												                "", (rs.getString("FNum") + "").length() - 5),
												  rs.getDouble("FBaseCuryRate"),
												  rs.getDouble("FPortCuryRate"));
			    	  costCal.roundCost(cost, isFourDigit? 4:2);//保留四位小数
			    	  pst.setDouble(1, cost.getCost());
                      pst.setDouble(2, cost.getMCost());
                      pst.setDouble(3, cost.getVCost());
                      pst.setDouble(4, cost.getBaseCost());
                      pst.setDouble(5, cost.getBaseMCost());
                      pst.setDouble(6, cost.getBaseVCost());
                      pst.setDouble(7, cost.getPortCost());
                      pst.setDouble(8, cost.getPortMCost());
                      pst.setDouble(9, cost.getPortVCost());
                      pst.setString(10, rs.getString("FNum"));
                      pst.addBatch();
                      
                      
                    //add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 start 
      				
      				if(count==500){
      					pst.executeBatch();
      					count = 1;
						continue;
      				}
      				
					count++;
      				//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 end
                      
		      }

              pst.executeBatch();
		      conn.commit();
		      conn.setAutoCommit(true);
		      bTrans=false;
    	}catch(Exception e){
    		throw new YssException("重新计算交易关联数据成本出错"+e.getMessage(),e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		dbl.closeStatementFinal(pst);
    		dbl.endTransFinal(conn, bTrans);
    	}
    }
    
    /**
     * @param rs  如果卖出的时候昨日没有库存则需要再证券借贷界面产生一笔借入的交易数据
     * @param cost 如果是昨日没有库存则将卖出数据的成本计算出来
     * @throws YssException edited by zhouxiang 
     * @throws SQLException 
     */
    private String ProduceTradeBL(ResultSet rs, YssCost cost) throws YssException, SQLException {
		String sqlStr = "";
		ResultSet rsSub = null;
		double dCost=0;
		double dBaseCost=0;
		double dPortCost=0;
		double dFlag = 0;// 操作标识 ,剩余库存数
		try {
			//modify by fangjiang 2011.08.29 BUG 2586
			sqlStr = "select sum(fstorageamount) as fstorageamount from "
					+ "(select fstorageamount from " + pub.yssGetTableName("tb_stock_security")
					+ " where FCheckstate = 1 and fstoragedate =" + dbl.sqlDate(YssFun.addDay(rs.getDate("FBargainDate"),-1))
					+ " and fsecuritycode = " + dbl.sqlString(rs.getString("FSecurityCode"))
					+ " and fportcode = " + dbl.sqlString(rs.getString("Fportcode"))
					+ " union "
					+ " select FTradeAmount as fstorageamount from " + pub.yssGetTableName("tb_data_subTrade") 
					+ " where Ftradetypecode = '01' and FCheckstate = 1 and FBargainDate = " + dbl.sqlDate(rs.getDate("FBargainDate"))
					+ " and fsecuritycode = " + dbl.sqlString(rs.getString("FSecurityCode"))
					+ " and fportcode = " + dbl.sqlString(rs.getString("Fportcode"))
					+ ")";
			//------------------------
			rsSub = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if (rsSub.next()) {// 如果有库存则计算库存余额数是否小于0， 小于0才有借入数量->有库存不做卖空处理
				dFlag =YssD.sub(rsSub.getDouble("fstorageamount"),rs.getDouble("FTradeAmount"));
				if(dFlag>=0){return null;}//昨日有库存数返回
				else{ dFlag=YssD.mul(dFlag, -1);}//返回借入数
			} else {// 否则没有昨日库存且有证券借贷信息设置数据就返回交易数
				sqlStr = "select * from "
						+ pub.yssGetTableName("Tb_Para_SecurityLend")// 必须有数据
						+ " a where a.fsecuritycode= "
						+ dbl.sqlString(rs.getString("FSecurityCode"))
						+ "and a.fcheckstate=1 and a.fbrokercode="
						+ dbl.sqlString(rs.getString("FBrokerCode"));
				rsSub = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
				if (rsSub.next()) {// 证券借贷信息设置中必须有对应证券和券商的设置才产生借入的交易数据
					dFlag=rs.getDouble("FTradeAmount");
				}else {return null;}//昨日没有库存数但无借贷设置信息也返回
			}
			dCost= YssD.mul(dFlag, rs
					.getDouble("FTradePrice"));
			dBaseCost = YssD.mul(dCost, rs
					.getDouble("FBaseCuryRate"));
			dPortCost = YssD.div(dBaseCost, rs
					.getDouble("FPortCuryRate"));
			cost.setCost(dCost);
			cost.setMCost(dCost);
			cost.setVCost(dCost);
			cost.setBaseCost(dBaseCost);
			cost.setBaseMCost(dBaseCost);
			cost.setBaseVCost(dBaseCost);
			cost.setPortCost(dPortCost);
			cost.setPortMCost(dPortCost);
			cost.setPortVCost(dPortCost);
		} catch (Exception e) {
			throw new YssException("卖出产生证券借贷交易数据出错");
		} finally {
			dbl.closeResultSetFinal(rsSub);
		}
		return null;
		
	}

	/**
     * 2009.08.10 蒋锦 添加属性分类代码
     * 2009.08.10 蒋锦 添加属性分类代码 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * @param dTransDate Date
     * @param sPortCode String
     * @param sSecurityCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param sTradeType String
     * @param dInterest double
     * @param dBaseRate double
     * @param dPortRate double
     * @param secPayAdmin SecRecPayAdmin
     * @param sCuryCode String
     * @param sAttrClsCode String:属性分类代码
     * @throws YssException
     */
    private void insertSecPecPay(java.util.Date dTransDate,
                                 String sPortCode, String sSecurityCode,
                                 String sAnalysisCode1,
                                 String sAnalysisCode2, String sAnalysisCode3,
                                 String sTradeType, double dInterest,
                                 double dBaseRate, double dPortRate,
                                 SecRecPayAdmin secPayAdmin,
                                 String sCuryCode,
                                 String sAttrClsCode,String sInvestType) throws //添加投资类型参数 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
        YssException {
        if (dInterest == 0) {
            return;
        }
        SecPecPayBean secpecpay = new SecPecPayBean();
        SecurityBean sec = new SecurityBean();
        secpecpay.setTransDate(dTransDate);
        secpecpay.setStrPortCode(sPortCode);
        secpecpay.setInvMgrCode(sAnalysisCode1);
        secpecpay.setBrokerCode(sAnalysisCode2);
        secpecpay.setStrSecurityCode(sSecurityCode);
        secpecpay.setAttrClsCode(sAttrClsCode);
        secpecpay.setInvestType(sInvestType);//设置投资类型 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
        secpecpay.setStrCuryCode(operFun.getSecCuryCode(sSecurityCode));
        secpecpay.setMoney(dInterest);
        secpecpay.setMMoney(dInterest);
        secpecpay.setVMoney(dInterest);
        //利用公有方法重新得到基础货币金额 sunny
        secpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
            dInterest, dBaseRate));
        secpecpay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
            dInterest, dBaseRate));
        secpecpay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
            dInterest, dBaseRate));
        secpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
            dInterest, dBaseRate, dPortRate,
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            sCuryCode, dTransDate, sPortCode));
        secpecpay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
            dInterest, dBaseRate, dPortRate,
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            sCuryCode, dTransDate, sPortCode));
        secpecpay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
            dInterest, dBaseRate, dPortRate,
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            sCuryCode, dTransDate, sPortCode));
        //MS00022    国内债券业务    QDV4.1赢时胜（上海）2009年4月20日22_A
        //2009.07.03 蒋锦 添加 新增债券交易类型
        if (sTradeType.equalsIgnoreCase("02") ||//卖出
            sTradeType.equalsIgnoreCase("58") ||//债转股
            sTradeType.equalsIgnoreCase("61")) { //可转债回售
            //------- 将07FI_B调整为02FI_B sj modified 20081229 MS00121 ------------
            secpecpay.setStrTsfTypeCode("02"); // 07-->02
            secpecpay.setStrSubTsfTypeCode("02FI_B"); // 07FI_B-->02FI_B
            //---------------------------------------------------------------------
        //MS00022    国内债券业务    QDV4.1赢时胜（上海）2009年4月20日22_A
        //2009.07.03 蒋锦 添加 新增债券交易类型 老股东配售也相当于买入，要记应收利息
        } else if (sTradeType.equalsIgnoreCase("01") || //买入
                   sTradeType.equalsIgnoreCase("51") || //新债网上中签
                   sTradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_ZQLGDPS)) { //债券老股东配售
            secpecpay.setStrTsfTypeCode("06");
            secpecpay.setStrSubTsfTypeCode("06FI_B");
        } else if (sTradeType.equalsIgnoreCase("17")) { //债券兑付
//         secpecpay.setStrTsfTypeCode("07");
//         secpecpay.setStrSubTsfTypeCode("07FI_B"); //债券兑付收入  把债券兑付时产生的利息记入卖出利息，不能记入利息收入，这会和支付债券利息产生冲突  胡坤 20070130（长盛测试修改）
            //------- 将07FI_B调整为02FI_B sj modified 20081229 MS00121 ------------
            secpecpay.setStrTsfTypeCode("02"); // 07-->02
            secpecpay.setStrSubTsfTypeCode("02FI_B"); // 07FI_B-->02FI_B
            //---------------------------------------------------------------------
            //----------- MS00269 QDV4中保2009年02月24日02_B -----------------------------------------
            if (this.getSettingOper().getRoundDigit(secpecpay.getMoney()) > 2) { //当卖出利息小数位数大于2位，则将boolean值设置为true
                bondDigitF = true; //设置为true
            }
            //--------------------------------------------------------------------------------------
        }
        //将统计证券库存06DV屏蔽 2008.10.06 蒋锦
//      else if (sTradeType.equalsIgnoreCase("06")) { //分红
//         secpecpay.setStrTsfTypeCode("06");
//         secpecpay.setStrSubTsfTypeCode("06DV" + sec.getStrCategoryCode());
//      }
        else if (sTradeType.equalsIgnoreCase("Dividend")) { //分红到帐
            sec.setYssPub(pub);
            sec.setSecurityCode(sSecurityCode);
            sec.getSetting();
            secpecpay.setStrTsfTypeCode("02");
            secpecpay.setStrSubTsfTypeCode("02DV" + sec.getStrCategoryCode());
            //if (sec.getStrCategoryCode().equalsIgnoreCase("EQ")){
            //   secpecpay.setStrSubTsfTypeCode("02DVEQ");
            //}else if (sec.getStrCategoryCode().equalsIgnoreCase("FI")){
            //   secpecpay.setStrSubTsfTypeCode("1003");
            //}
//         secpecpay.setStrSubTsfTypeCode("02" + sec.getStrCategoryCode());
        } else if (sTradeType.equalsIgnoreCase("DividendRec")) { //应收分红
            sec.setYssPub(pub);
            sec.setSecurityCode(sSecurityCode);
            sec.getSetting();
            secpecpay.setStrTsfTypeCode("06");
            secpecpay.setStrSubTsfTypeCode("06DV" + sec.getStrCategoryCode());
        } else if (sTradeType.equalsIgnoreCase("24")) { //sj add 20071116 正回购
            sec.setYssPub(pub);
            sec.setSecurityCode(sSecurityCode);
            //sec.getSetting();
            secpecpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee); //费用
            secpecpay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_RE_Fee);
        } else if (sTradeType.equalsIgnoreCase("25")) { //sj add 20071116 逆回购
            sec.setYssPub(pub);
            sec.setSecurityCode(sSecurityCode);
            //sec.getSetting();
            secpecpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income); //收入
            secpecpay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_RE_Income);
        }
        secpecpay.setBaseCuryRate(dBaseRate);
        secpecpay.setPortCuryRate(dPortRate);
        secpecpay.checkStateId = 1;

        secPayAdmin.addList(secpecpay);
//      if (sTradeType.equalsIgnoreCase("02")) { //  如果为卖出
//          secPayAdmin.insert(dTransDate, "07",
//                             "07FI_B", sPortCode, sAnalysisCode1,
//                             sAnalysisCode2, sSecurityCode, "",
//                             0);
//       }
//       else if (sTradeType.equalsIgnoreCase("01")) {//如果为买入
//          secPayAdmin.insert(dTransDate, "06",
//                             "06FI_B", sPortCode, sAnalysisCode1,
//                             sAnalysisCode2, sSecurityCode, "",
//                             0);
//       }
    }

    private YssCost calCost(double dTradeMoney, double dFee1, int iAw1,
                            double dFee2, int iAw2,
                            double dFee3, int iAw3, double dFee4, int iAw4,
                            double dFee5, int iAw5, double dFee6, int iAw6,
                            double dFee7, int iAw7, double dFee8, int iAw8,
                            double dBaseRate, double dPortRate,
                            java.util.Date dTransDate,
                            String sPortCode,
                            String sCuryCode) throws
        YssException {
        YssCost resultCost = new YssCost();
        double dCost = 0;
      //#1095 原币成本与基础货币、组合货币成本保留精度不一致，导致库存统计后得到证券库存成本不一致
      //--- modify by jiangshichao 2011.02.09 库存统计尾差调整     成本应该在（成交金额+费用）后再做四舍五入处理 ----
      dCost = dTradeMoney;
        if (iAw1 == 0) { //计入成本
            dCost = YssD.add(dCost, dFee1);
        }
        if (iAw2 == 0) { //计入成本
            dCost = YssD.add(dCost, dFee2);
        }
        if (iAw3 == 0) { //计入成本
            dCost = YssD.add(dCost, dFee3);
        }
        if (iAw4 == 0) { //计入成本
            dCost = YssD.add(dCost, dFee4);
        }
        if (iAw5 == 0) { //计入成本
            dCost = YssD.add(dCost, dFee5);
        }
        if (iAw6 == 0) { //计入成本
            dCost = YssD.add(dCost, dFee6);
        }
        if (iAw7 == 0) { //计入成本
            dCost = YssD.add(dCost, dFee7);
        }
        if (iAw8 == 0) { //计入成本
            dCost = YssD.add(dCost, dFee8);
        }
       //#1095 原币成本与基础货币、组合货币成本保留精度不一致，导致库存统计后得到证券库存成本不一致
      dCost = YssD.round(dCost, 2);
      
        resultCost.setCost(dCost);
        resultCost.setMCost(dCost);
        resultCost.setVCost(dCost);

        resultCost.setBaseCost(this.getSettingOper().calBaseMoney(dCost,
            dBaseRate));
        resultCost.setBaseMCost(this.getSettingOper().calBaseMoney(dCost,
            dBaseRate));
        resultCost.setBaseVCost(this.getSettingOper().calBaseMoney(dCost,
            dBaseRate));

        resultCost.setPortCost(this.getSettingOper().calPortMoney(dCost,
            dBaseRate, dPortRate,
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            sCuryCode, dTransDate, sPortCode));
        resultCost.setPortMCost(this.getSettingOper().calPortMoney(dCost,
            dBaseRate, dPortRate,
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            sCuryCode, dTransDate, sPortCode));
        resultCost.setPortVCost(this.getSettingOper().calPortMoney(dCost,
            dBaseRate, dPortRate,
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            sCuryCode, dTransDate, sPortCode));
        return resultCost;
    }

    private void insertTransfer(java.util.Date dTransDate,
                                java.util.Date dTransferDate,
                                String sFNum,
                                String sPortCode, String sCashAccCode,
                                String sAnalysisCode1,
                                String sAnalysisCode2, String sAnalysisCode3,
                                String sTradeType, double dMoney,
                                double dBaseRate, double dPortRate
        ) throws YssException {
        CashTransAdmin transferadmin = new CashTransAdmin();
        TransferBean transfer = setTransferAttr(dTransDate, dTransferDate,
                                                sTradeType, sFNum);
        TransferSetBean transferSet = setTransferSetAttr(sPortCode, sTradeType,
            sAnalysisCode1, sAnalysisCode2, sAnalysisCode3,
            sCashAccCode, dMoney, dBaseRate, dPortRate);
        transferadmin.addList(transfer, transferSet);
        transferadmin.setYssPub(pub);
        //transferadmin.insert();
//      transferadmin.insert(dTransferDate, dTransDate,
//                           "02,05", "02FU,05RE",
//                           "", 0);
        if (sTradeType.equalsIgnoreCase("24") || sTradeType.equalsIgnoreCase("25")) { //正回购或逆回购
            transferadmin.insert("", "", sFNum, "REMature");
        } else if (sTradeType.equalsIgnoreCase("21")) {
            transferadmin.insert(dTransferDate, dTransDate,
                                 "02", "02FU",
                                 "", 0);
        }
    }

    /**
     * sj 20071116 add 设置子调拨
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param sCashAccCode String
     * @param dMoney double
     * @param dBaseRate double
     * @param dPortRate double
     * @throws YssException
     * @return TransferSetBean
     */

    protected TransferSetBean setTransferSetAttr(String sPortCode,
                                                 String sTradeType,
                                                 String sAnalysisCode1,
                                                 String sAnalysisCode2,
                                                 String sAnalysisCode3,
                                                 String sCashAccCode,
                                                 double dMoney,
                                                 double dBaseRate,
                                                 double dPortRate) throws
        YssException {
        TransferSetBean transferset = new TransferSetBean();
        if (sTradeType.equalsIgnoreCase("24")) { //正回购
            transferset.setIInOut( -1); //流出
        } else if (sTradeType.equalsIgnoreCase("25") ||
                   sTradeType.equalsIgnoreCase("21")) { //逆回购
            transferset.setIInOut(1); //流入
        }
        transferset.setSPortCode(sPortCode);
        transferset.setSAnalysisCode1(sAnalysisCode1.length() > 0 ?
                                      sAnalysisCode1 : " ");
        transferset.setSAnalysisCode2(sAnalysisCode2.length() > 0 ?
                                      sAnalysisCode2 : " ");
        transferset.setSAnalysisCode3(sAnalysisCode3.length() > 0 ?
                                      sAnalysisCode3 : " ");
        transferset.setSCashAccCode(sCashAccCode);
        transferset.setDMoney(dMoney);
        transferset.setDBaseRate(dBaseRate);
        transferset.setDPortRate(dPortRate);
        transferset.checkStateId = 1;
        return transferset;
    }

    /**
     * sj 20071116 add 设置资金调拨
     * @param dTransDate Date
     * @param TsfTypeCode String
     * @param SubTsfTypeCode String
     * @return TransferBean
     */
    private TransferBean setTransferAttr(java.util.Date dTransDate,
                                         java.util.Date dTransferDate,
                                         String sTradeType,
                                         String sTradeNum) {
        TransferBean transfer = new TransferBean();
        transfer.setDtTransferDate(dTransferDate);
        transfer.setDtTransDate(dTransDate);
        transfer.setFRelaNum(sTradeNum);
        if (sTradeType.equalsIgnoreCase("24") || sTradeType.equalsIgnoreCase("25")) { //正回购或逆回购
            transfer.setStrTsfTypeCode("05"); //成本
            transfer.setStrSubTsfTypeCode("05RE"); //回购到期
            transfer.setFNumType("REMature"); //回购到期
        } else if (sTradeType.equalsIgnoreCase("21")) { //期货平仓
            transfer.setStrTsfTypeCode("02"); //收入
            transfer.setStrSubTsfTypeCode("02FU"); //期货收入
        }
        transfer.checkStateId = 1;
        return transfer;
    }


    /**
     * 统计交易关联项
     * 2009.07.20 蒋锦 修改
     * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 增加了ETF基金申购赎回和债转股业务的交易关联数据处理
     * @param dDate Date
     * @param hmEveStg HashMap
     * @param bAnaly1 boolean
     * @param bAnaly2 boolean
     * @param bAnaly3 boolean
     * @throws YssException
     */
    private void getStgTradeRelaData(java.util.Date dDate, HashMap hmEveStg,
                                     boolean bAnaly1, boolean bAnaly2,
                                     boolean bAnaly3) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        SecurityStorageBean secstorage = null;

        String sKey = "";

        double dBaseRate = 1;
        double dPortRate = 1;
        try {
            strSql =
                "select a.*,b.FTradeTypeCode,b.FTradeCury,b.FCatCode,b.FSubCatCode,b.FInvestType," + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                " b.FPortCode,b.FPortCury,b.FAttrClsCode,b.FBailMoney" +
                " from " +
                pub.yssGetTableName("Tb_Data_TradeRela") +
                //---------------------------------------
                //" a left join (select FNum,b1.FSecurityCode,FTradeTypeCode,FTradeCury,FCatCode," +
                " a  join (select FNum,b1.FSecurityCode,FTradeTypeCode,FTradeCury,FCatCode," + //更改为 join QDV4建行2009年3月5日02_B MS00288 modify by leeyu
                " FSubCatCode,b1.FPortCode,FPortCury,FAttrClsCode,FBailMoney,FInvestType" + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                ",FBargainDate,FSettleDate " + //添加业务日期与结算日期字段，下面要用到 QDV4建行2009年3月5日02_B MS00288 by leeyu
                " from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                //---------------------------------------
                " b1 left join (select * from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) b2 on b1.FSecurityCode = b2.FSecurityCode " +
                //---------------------------------------
                " left join (select * from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) b3 on b1.FPortCode = b3.FPortCode " +
                //---------------------------------------
                " where b1.FCheckState = 1)" +
                " b on a.FNum = b.FNum where a.FCheckState = 1" +
                " and a.FPortCode in(" + operSql.sqlCodes(portCodes) + ")" +
                /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
                * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//                (statCodes.length()>0?" and a.FSecurityCode in("+operSql.sqlCodes(statCodes)+")":"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
                (statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"a.FSecurityCode",500) + ")":"")+
                /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
                
                " and b.FBargainDate=" + dbl.sqlDate(dDate); 
               	//begin--zhouxiang MS01476 交易数据录入的成交日期与结算日期为不同日期，库存统计数量减少了交易关联中的两倍  修改目的：权证使用日期只使用交易日期的数据
                //" or b.FSettleDate =" + dbl.sqlDate(dDate) + ")"; //添加组合，日期的条件判断 权证数据处理 QDV4建行2009年3月5日02_B MS00288 by leeyu
            	//end---- MS01476 2010.7.26 end ------------------------------------------------------------------
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //-----------------------------增加币种信息有效性检查 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A------//
                if (rs.getString("FTradeCury") == null || rs.getString("FTradeCury").trim().length() == 0) {
                    throw new YssException("系统进行证券库存统计,在统计交易关联项时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的币种信息不存在!" + "\n" +
                                           "请核查以下信息：" + "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                           "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                }
                //----------------------------------------------------------------------------------------------//
                dBaseRate = 1;
                dPortRate = 1;

                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FPortCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);
                //----------------------------增加汇率信息有效性检查 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A---//
                if (dBaseRate == 0 || dPortRate == 0) {
                    throw new YssException("系统进行证券库存统计,在统计交易关联项时检查到代码为【" +
                                           rs.getString("FPortCode") +
                                           "】证券对应的汇率信息不存在!" + "\n"
                                           + "请核查以下信息：" + "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n"
                                           + "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");
                }
                //----------------------------------------------------------------------------------------------//
                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (bAnaly2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                    (rs.getString("FAttrClsCode") == null ||
                     rs.getString("FAttrClsCode").length() == 0 ? " " :
                     rs.getString("FAttrClsCode") + "\f" +  //属性分类代码
                     rs.getString("FInvestType"));          //投资类型字段
                int iInOut = rs.getInt("FInOut");//流动方向

                //MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
                //删除掉了期货的处理，期货的处理已经不在此进行了
                //2009.07.20 蒋锦 修改 考虑了昨日没有库存的情况
                if (hmEveStg.containsKey(sKey)) {
                	//fanghaoln 20100414 MS01084 QDV4工银2010年04月12日01_B 证券库存中数量和成本转出，根据不同的情况转出，不能叠加转出
                	if (rs.getString("FTradeTypeCode").equalsIgnoreCase("30") ||
                            rs.getString("FTradeTypeCode").equalsIgnoreCase("31")) { //权证行权 蒋锦 2008.01.14
                            //---------------把证券库存中的数量和成本转出
                            secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                            secstorage.setStrStorageAmount(YssD.sub(YssFun.toDouble(
                                secstorage.getStrStorageAmount()),
                                rs.getDouble("FAmount")) + "");
                            secstorage.setStrStorageCost(YssD.sub(YssFun.toDouble(
                                secstorage.
                                getStrStorageCost()),
                                rs.getDouble("FCost")) + "");
                            secstorage.setStrMStorageCost(YssD.sub(YssFun.toDouble(
                                secstorage.getStrMStorageCost()),
                                rs.getDouble("FMCost")) + "");
                            secstorage.setStrVStorageCost(YssD.sub(YssFun.toDouble(
                                secstorage.getStrVStorageCost()),
                                rs.getDouble("FVCost")) + "");

                            secstorage.setStrBaseCuryCost(YssD.sub(YssFun.toDouble(
                                secstorage.getStrBaseCuryCost()),
                                rs.getDouble("FBaseCuryCost")) + "");
                            secstorage.setStrMBaseCuryCost(YssD.sub(YssFun.toDouble(
                                secstorage.getStrMBaseCuryCost()),
                                rs.getDouble("FMBaseCuryCost")) + "");
                            secstorage.setStrVBaseCuryCost(YssD.sub(YssFun.toDouble(
                                secstorage.getStrVBaseCuryCost()),
                                rs.getDouble("FVBaseCuryCost")) + "");

                            secstorage.setStrPortCuryCost(YssD.sub(YssFun.toDouble(
                                secstorage.getStrPortCuryCost()),
                                rs.getDouble("FPortCuryCost")) + "");
                            secstorage.setStrMPortCuryCost(YssD.sub(YssFun.toDouble(
                                secstorage.getStrMPortCuryCost()),
                                rs.getDouble("FMPortCuryCost")) + "");
                            secstorage.setStrVPortCuryCost(YssD.sub(YssFun.toDouble(
                                secstorage.getStrVPortCuryCost()),
                                rs.getDouble("FVPortCuryCost")) + "");
                        }else{//根据不同的情况转出，不能叠加转出
                        	//---------------把证券库存中的数量和成本转出
                            secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                            secstorage.setStrStorageAmount(YssD.add(YssFun.toDouble(
                                secstorage.getStrStorageAmount()),
                                YssD.mul(rs.getDouble("FAmount"), iInOut)) + "");
                            secstorage.setStrStorageCost(YssD.add(YssFun.toDouble(
                                secstorage.
                                getStrStorageCost()),
                                YssD.mul(rs.getDouble("FCost"), iInOut)) + "");
                            secstorage.setStrMStorageCost(YssD.add(YssFun.toDouble(
                                secstorage.getStrMStorageCost()),
                                YssD.mul(rs.getDouble("FMCost"), iInOut)) + "");
                            secstorage.setStrVStorageCost(YssD.add(YssFun.toDouble(
                                secstorage.getStrVStorageCost()),
                                YssD.mul(rs.getDouble("FVCost"), iInOut)) + "");
                            secstorage.setStrBaseCuryCost(YssD.add(YssFun.toDouble(
                                secstorage.getStrBaseCuryCost()),
                                YssD.mul(rs.getDouble("FBaseCuryCost"), iInOut)) + "");
                            secstorage.setStrMBaseCuryCost(YssD.add(YssFun.toDouble(
                                secstorage.getStrMBaseCuryCost()),
                                YssD.mul(rs.getDouble("FMBaseCuryCost"), iInOut)) + "");
                            secstorage.setStrVBaseCuryCost(YssD.add(YssFun.toDouble(
                                secstorage.getStrVBaseCuryCost()),
                                YssD.mul(rs.getDouble("FVBaseCuryCost"), iInOut)) + "");

                            secstorage.setStrPortCuryCost(YssD.add(YssFun.toDouble(
                                secstorage.getStrPortCuryCost()),
                                YssD.mul(rs.getDouble("FPortCuryCost"), iInOut)) + "");
                            secstorage.setStrMPortCuryCost(YssD.add(YssFun.toDouble(
                                secstorage.getStrMPortCuryCost()),
                                YssD.mul(rs.getDouble("FMPortCuryCost"), iInOut)) + "");
                            secstorage.setStrVPortCuryCost(YssD.add(YssFun.toDouble(
                                secstorage.getStrVPortCuryCost()),
                                YssD.mul(rs.getDouble("FVPortCuryCost"), iInOut)) + "");

                                secstorage.setBailMoney(YssD.sub(secstorage.getBailMoney(),
                                    rs.getDouble("FBailMoney")));
                                //-------------------------------------------------------------    
                        }
                	//---------------------------------end ---MS01084---------------------------------------
                } else {					
                    secstorage = new SecurityStorageBean();
                    secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
                    secstorage.setStrPortCode(rs.getString("FPortCode"));
                    secstorage.setInvestType(rs.getString("FInvestType"));      //设置投资类型字段 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                    secstorage.setStrCuryCode(rs.getString("FTradeCury"));
                    if (bAnaly1) {
                        secstorage.setStrFAnalysisCode1(rs.getString("FAnalysisCode1"));
                    } else {
                        secstorage.setStrFAnalysisCode1(" ");
                    }
                    if (bAnaly2) {
                        secstorage.setStrFAnalysisCode2(rs.getString("FAnalysisCode2"));
                    } else {
                        secstorage.setStrFAnalysisCode2(" ");
                    }
                    if (bAnaly3) {
                        secstorage.setStrFAnalysisCode3(rs.getString("FAnalysisCode3"));
                    } else {
                        secstorage.setStrFAnalysisCode3(" ");
                    }
                    secstorage.setAttrCode(rs.getString("FAttrClsCode"));

                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");

                    secstorage.setStrStorageAmount(YssD.mul(rs.getDouble("FAmount"), iInOut) + "");
                    secstorage.setStrStorageCost(YssD.mul(rs.getDouble("FCost"), iInOut) + "");
                    secstorage.setStrMStorageCost(YssD.mul(rs.getDouble("FMCost"), iInOut) + "");
                    secstorage.setStrVStorageCost(YssD.mul(rs.getDouble("FVCost"), iInOut) + "");

                    secstorage.setStrBaseCuryCost(YssD.mul(rs.getDouble("FBaseCuryCost"), iInOut) + "");
                    secstorage.setStrMBaseCuryCost(YssD.mul(rs.getDouble("FMBaseCuryCost"), iInOut) + "");
                    secstorage.setStrVBaseCuryCost(YssD.mul(rs.getDouble("FVBaseCuryCost"), iInOut) + "");

                    secstorage.setStrPortCuryCost(YssD.mul(rs.getDouble("FPortCuryCost"), iInOut) + "");
                    secstorage.setStrMPortCuryCost(YssD.mul(rs.getDouble("FMPortCuryCost"), iInOut) + "");
                    secstorage.setStrVPortCuryCost(YssD.mul(rs.getDouble("FVPortCuryCost"), iInOut) + "");

                    hmEveStg.put(sKey, secstorage);
                }
            }
        } catch (Exception e) {
            throw new YssException("在统计交易关联项时出现异常!" + "\n", e); //统计交易关联项异常 by 曹丞 2009.01.29 MS00004 QDV4.1-2009.2.1_09A

        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void yearChange(java.util.Date dDate, String portCode) throws
        YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        String YearMonth = "";
        int Year;
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            YearMonth = YssFun.getYear(dDate) + "00";
            Year = YssFun.getYear(dDate);
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FYearMonth = " + dbl.sqlString(YearMonth) +
                " and FPORTCODE in(" + operSql.sqlCodes(portCode) + ")"; //添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的 by leeyu 20090220 QDV4华夏2009年2月13日01_B MS00246
            dbl.executeSql(strSql);

            //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A;
            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_Security") +
                "(FSECURITYCODE,FYearMonth,FStorageDate,FPORTCODE,FInvestType," +
                "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FATTRCLSCODE,FCATTYPE," +
                "FCURYCODE,FSTORAGEAMOUNT,FSTORAGECOST,FMSTORAGECOST,FVSTORAGECOST," +
                "FFREEZEAMOUNT,FBAILMONEY,FBASECURYRATE,FBASECURYCOST,FMBASECURYCOST," +
                "FVBASECURYCOST,FPORTCURYRATE,FPORTCURYCOST,FMPORTCURYCOST,FVPORTCURYCOST," +
                "FMARKETPRICE,FCHECKSTATE,FSTORAGEIND," +
                "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" +
                "(select FSECURITYCODE," + dbl.sqlString(YearMonth) +
                " as FYearMonth, " +
                dbl.sqlDate(new Integer(Year).toString() + "-01-01") +
                " as FStorageDate,FPORTCODE,FInvestType," +
                "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FATTRCLSCODE,FCATTYPE," +
                "FCURYCODE,FSTORAGEAMOUNT,FSTORAGECOST,FMSTORAGECOST,FVSTORAGECOST," +
                "FFREEZEAMOUNT,FBAILMONEY,FBASECURYRATE,FBASECURYCOST,FMBASECURYCOST," +
                "FVBASECURYCOST,FPORTCURYRATE,FPORTCURYCOST,FMPORTCURYCOST,FVPORTCURYCOST," +
                "FMARKETPRICE,FCHECKSTATE,FSTORAGEIND," +
                dbl.sqlString(pub.getUserCode()) +
                " as FCREATOR," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " as FCREATETIME," +
                dbl.sqlString(pub.getUserName()) + " as FCHECKUSER," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " as FCHECKTIME" +
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FYearMonth = " + dbl.sqlString( (Year - 1) + "12") +
                " and FStorageDate = " +
                dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31") +
                " and FPortCode in( " + portCode + ")" +
                ")";

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("年度结转错误!\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            //dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    
	public void yearChange_TP(java.util.Date dDate, String portCode)
			throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		String YearMonth = "";
		int Year;
		ResultSet rs = null;
		// QDV4太平2010年12月20日01_A 证券类年终结转时，核算成本和管理成本要调整为上年末市值 add by jiangshichao
		// 2010.12.25
		
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pst = null;
		YssPreparedStatement pst = null;
        //=============end====================
		String query = "";
		double dMarketValue = 0;
		double dBaseMarketValue = 0;
		double dPortMarketValue = 0;
		// QDV4太平2010年12月20日01_A 证券类年终结转时，核算成本和管理成本要调整为上年末市值 end
		
		//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420
		int count=1;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			YearMonth = YssFun.getYear(dDate) + "00";
			Year = YssFun.getYear(dDate);
			strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Security")
					+ " where FYearMonth = " + dbl.sqlString(YearMonth)
					+ " and FPORTCODE in(" + operSql.sqlCodes(portCode) + ")";// 添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的
																				// by
																				// leeyu
																				// 20090220
																				// QDV4华夏2009年2月13日01_B
																				// MS00246
			dbl.executeSql(strSql);
			
			/*********************************************************************************
			 * 需求编号:QDV4太平2010年12月20日01_A 需求描述：证券类年终结转时，核算成本和管理成本要调整为上年末市值
			 * 
			 * @author benson 2010.12.25
			 * 
			 *         核算成本(FStorageCost)和管理成本(FMStorageCost)由库存数据根据汇率和行情算出
			 *         原币金额=库存数量*行情， 基础货币和本位币金额根据汇率算出。 汇率，为年末汇率 ；行情数据为年末行情数据
			 */
			strSql = " insert into "
					+ pub.yssGetTableName("Tb_Stock_Security")
					+ "(FSecurityCode, FYearMonth, FStorageDate, FPortCode, FCuryCode, FStorageAmount, FStorageCost, FMStorageCost, FVStorageCost, FFreezeAmount,"
					+ " FBaseCuryRate, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, "
					+ " FPortCuryRate, FPortCuryCost, FMPortCuryCost, FVPortCuryCost, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3,FMarketPrice, FStorageInd,"
					+ " FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,"
					+ " FBailMoney,FCatType,FAttrClsCode)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			//modified by liubo.Story #2145
			//==============================
//			pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
			query = getYearChangeDataQuery(dDate, portCode);
			rs = dbl.openResultSet(query,ResultSet.TYPE_SCROLL_INSENSITIVE); //modify by fangjiang 2011.08.14 STORY #788

			while (rs.next()) {

				dMarketValue = YssFun.roundIt(YssD.mul(rs
						.getDouble("FSTORAGEAMOUNT"), rs.getDouble("FPrice")),
						4);
				dBaseMarketValue = YssFun.roundIt(YssD.mul(dMarketValue, rs
						.getDouble("fbaserate")), 4);
				dPortMarketValue = YssFun.roundIt(YssD.div(dBaseMarketValue, rs
						.getDouble("fportrate")), 4);

				pst.setString(1, rs.getString("FSECURITYCODE"));
				pst.setString(2, rs.getString("FYearMonth"));
				pst.setDate(3, rs.getDate("FStorageDate"));
				pst.setString(4, rs.getString("FPortCode"));
				pst.setString(5, rs.getString("FCURYCODE"));
				pst.setDouble(6, rs.getDouble("FSTORAGEAMOUNT"));

				// 原币的核算成本、管理成本 调整为上年末市值。 管理成本\核算成本 = 库存数量 * 年末收盘价
				pst.setDouble(7, dMarketValue);
				pst.setDouble(8, dMarketValue);
				pst.setDouble(9, rs.getDouble("FVStorageCost"));

				pst.setDouble(10, rs.getDouble("FFreezeAmount"));

				// 基础货币的核算成本、管理成本 调整为上年末市值。 管理成本\核算成本 = 原币市价 * 年末汇率
				pst.setDouble(11, rs.getDouble("FBaseCuryRate"));
				pst.setDouble(12, dBaseMarketValue);
				pst.setDouble(13, dBaseMarketValue);
				pst.setDouble(14, rs.getDouble("FVBaseCuryCost"));
				// 组合货币的核算成本、管理成本根据汇率算出 管理成本\核算成本 = 原币市价 * 年末汇率
				pst.setDouble(15, rs.getDouble("FPortCuryRate"));
				pst.setDouble(16, dPortMarketValue);
				pst.setDouble(17, dPortMarketValue);
				pst.setDouble(18, rs.getDouble("FVPortCuryCost"));

				pst.setString(19, rs.getString("FAnalysisCode1"));
				pst.setString(20, rs.getString("FAnalysisCode2"));
				pst.setString(21, rs.getString("FAnalysisCode3"));
				pst.setInt(22, 0);

				pst.setInt(23, rs.getInt("FStorageInd")); // 库存状态
				pst.setInt(24, rs.getInt("FCheckState"));
				pst.setString(25, pub.getUserCode());
				pst.setString(26, YssFun.formatDatetime(new java.util.Date()));
				pst.setString(27, pub.getUserCode());
				pst.setString(28, YssFun.formatDatetime(new java.util.Date()));
				pst.setDouble(29, rs.getDouble("FBailMoney"));
				pst.setString(30, rs.getString("FCatType"));// sj edit 20071204
															// 逻辑有误
				pst.setString(31, rs.getString("FAttrClsCode"));

				pst.addBatch();
				
				//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 start 
				
				if(count==500){
					pst.executeBatch();
					count = 1;
					continue;
				}
				
				count++;
				//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 end
				
			}
			pst.executeBatch();
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("年度结转错误!\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
		}

	}

	/*************************************************************************
	 * 需求编号: 1826::太平资产年终库存结转特殊处理 QDV4太平2010年12月20日01_A
	 * 需求描述：证券类年终结转时，核算成本和管理成本要调整为上年末市值 开发人员： jiangshichao 开发时间： 2010.12.25
	 * 
	 * @param dDate
	 * @param portCode
	 * @return
	 * @throws YssException
	 */
	private String getYearChangeDataQuery(java.util.Date dDate, String portCode)
			throws YssException {

		String query = "";
		String YearMonth = "";
		int Year;
		try {
			YearMonth = YssFun.getYear(dDate) + "00";
			Year = YssFun.getYear(dDate);

			query = " select a.*,b.FPrice,c.fbaserate,c.fportrate from " +
			// --- 获取证券年末库存数据 ------------------------------------//
					"(select FSECURITYCODE,"
					+ dbl.sqlString(YearMonth)
					+ " as FYearMonth, "
					+ dbl.sqlDate(new Integer(Year).toString() + "-01-01")
					+ " as FStorageDate,FPORTCODE,"
					+ " FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FATTRCLSCODE,FCATTYPE,"
					+ " FCURYCODE,FSTORAGEAMOUNT,FSTORAGECOST,FMSTORAGECOST,FVSTORAGECOST,"
					+ " FFREEZEAMOUNT,FBAILMONEY,FBASECURYRATE,FBASECURYCOST,FMBASECURYCOST,"
					+ " FVBASECURYCOST,FPORTCURYRATE,FPORTCURYCOST,FMPORTCURYCOST,FVPORTCURYCOST,"
					+ " FMARKETPRICE,FCHECKSTATE,FSTORAGEIND,"
					+ dbl.sqlString(pub.getUserCode())
					+ " as FCREATOR,"
					+ dbl
							.sqlString(YssFun
									.formatDatetime(new java.util.Date()))
					+ " as FCREATETIME,"
					+ dbl.sqlString(pub.getUserName())
					+ " as FCHECKUSER,"
					+ dbl
							.sqlString(YssFun
									.formatDatetime(new java.util.Date()))
					+ " as FCHECKTIME"
					+ " from "
					+ pub.yssGetTableName("Tb_Stock_Security")
					+ " where FYearMonth = "
					+ dbl.sqlString((Year - 1) + "12")
					+ " and FStorageDate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and FPortCode in( "
					+ portCode
					+ ")"
					+ ") a"
					+
					// --- 获取行情数据为年末行情数据
					// ---------------------------------------//
					" LEFT JOIN (SELECT b1.FValDate, b1.FPortCode, b1.FSecurityCode, b1.FPrice"
					+ " FROM "
					+ pub.yssGetTableName("TB_Data_ValMktPrice")
					+ " b1"
					+ " JOIN (SELECT MAX(FValDate) AS FValDate, FSecurityCode"
					+ " FROM "
					+ pub.yssGetTableName("TB_Data_ValMktPrice")
					+ " WHERE FValDate <= "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and FPortCode in ("
					+ operSql.sqlCodes(portCode)
					+ ")"
					+ " GROUP BY FSecurityCode) b2 ON b1.FValDate = b2.FValDate"
					+ " AND b1.FSecurityCode = b2.FSecurityCode"
					+ " WHERE FPortCode in ( "
					+ operSql.sqlCodes(portCode)
					+ ")"
					+ " ) b ON a.FSECURITYCODE = b.FSecurityCode "
					+ " and a.FPortCode = b.FPortCode"
					+
					// --- 获取汇率数据为年末估值汇率数据
					// ---------------------------------------//
					" LEFT JOIN (SELECT c1.FValDate, c1.FPortCode, c1.fcurycode, c1.fbaserate,c1.fportrate "
					+ " FROM " + pub.yssGetTableName("tb_data_valrate") + " c1"
					+ " JOIN (SELECT MAX(FValDate) AS FValDate, fcurycode"
					+ " FROM " + pub.yssGetTableName("tb_data_valrate")
					+ " WHERE FValDate <= "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and FPortCode in (" + operSql.sqlCodes(portCode) + ")"
					+ " GROUP BY fcurycode) c2 ON c1.FValDate = c2.FValDate"
					+ " AND c1.fcurycode = c2.fcurycode"
					+ " WHERE FPortCode in ( " + operSql.sqlCodes(portCode)
					+ ")" + " ) c ON a.FCURYCODE = c.fcurycode "
					+ " and a.FPortCode = c.FPortCode";

			return query;
		} catch (Exception e) {
			throw new YssException(" 获取年末数据出错......");
		}
	}

    
    
    
    
    /**
     * 获取储存在 ArrayList 中的交易关联数据
     * @param sTradeNum String: 交易编号
     * @param sPortCode String: 组合编号
     * @param sRelaType String: 关联类型
     * @throws YssException
     * @return ArrayList
     */
    public ArrayList getTradeRela(String sTradeNum, String sPortCode,
                                  String sRelaType) throws YssException {
        ArrayList arrBeans = new ArrayList();
        TradeRelaBean tradeRela = null;
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql =
                "SELECT * FROM " + pub.yssGetTableName("Tb_Data_TradeRela") +
                " WHERE FNum = " + dbl.sqlString(sTradeNum) +
                " AND FPortCode = " + dbl.sqlString(sPortCode) +
                " AND FRelaType = " + dbl.sqlString(sRelaType);
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                tradeRela = new TradeRelaBean();
                tradeRela.setSNum(sTradeNum);
                tradeRela.setSRelaType(sRelaType);
                tradeRela.setSPortCode(sPortCode);
                tradeRela.setSAnalysisCode1(rs.getString("FAnalysisCode1"));
                tradeRela.setSAnalysisCode2(rs.getString("FAnalysisCode2"));
                tradeRela.setSAnalysisCode3(rs.getString("FAnalysisCode3"));
                tradeRela.setSSecurityCode(rs.getString("FSecurityCode"));
                tradeRela.setDAmount(rs.getDouble("FAmount"));
                tradeRela.setDCost(rs.getDouble("FCost"));
                tradeRela.setDMCost(rs.getDouble("FMCost"));
                tradeRela.setDVCost(rs.getDouble("FVCost"));
                tradeRela.setDBaseCuryCost(rs.getDouble("FBaseCuryCost"));
                tradeRela.setDMBaseCuryCost(rs.getDouble("FMBaseCuryCost"));
                tradeRela.setDVBaseCuryCost(rs.getDouble("FVBaseCuryCost"));
                tradeRela.setDPortCuryCost(rs.getDouble("FPortCuryCost"));
                tradeRela.setDMPortCuryCost(rs.getDouble("FMPortCuryCost"));
                tradeRela.setDVPortCuryCost(rs.getDouble("FVPortCuryCost"));

                arrBeans.add(tradeRela);
            }
            return arrBeans;
        } catch (Exception e) {
            throw new YssException("获取交易关联信息出错!\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	//重载方法 合并太平版本代码
	public void getStgSecExchangeData(java.util.Date dDate,
                                      HashMap hmEveStg,
                                      boolean bAnaly1,
                                      boolean bAnaly2,
                                      boolean bAnaly3,
                                      boolean bTodayAmorti) throws YssException {
	   	getStgSecExchangeData(dDate,hmEveStg,bAnaly1,bAnaly2,bAnaly3,bTodayAmorti,""); //QDV4中保2010年4月14日02_B MS01092 by leeyu 20100419
   	}
    /**
     * 计算当日证券兑换
     * 添加生成交易变动溢折价的应收应付，添加判断是否统计每日摊销溢折价的形参
     * 2009.09.04 蒋锦 修改 MS00656 QDV4赢时胜(上海)2009年8月24日01_A
	 * 20091019- MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * 计算当日证券兑换 新表Tb_Data_Integrated by ly 080204
	 * @param dDate Date
     * @param hmEveStg HashMap
     * @param bAnaly1 boolean
     * @param bAnaly2 boolean
     * @param bAnaly3 boolean
     * @param alSecRecPay ArrayList
     * @param bTodayAmorti boolean：是否统计每日摊销的溢折价，true-统计，false-不统计
     * @throws YssException
     */
    public void getStgSecExchangeData(java.util.Date dDate,
                                      HashMap hmEveStg,
                                      boolean bAnaly1,
                                      boolean bAnaly2,
                                      boolean bAnaly3,
                                      boolean bTodayAmorti,
                                      String filterSql) throws YssException {//添加综合业务筛选条件 by leeyu 20100419 QDV4中保2010年4月14日02_B MS01092 合并太平版本代码
        ResultSet rs = null;
        String strSql = "";
        SecurityStorageBean secstorage = null;

        String sKey = "";

        double dBaseRate = 1;
        double dPortRate = 1;

        try {
            strSql = "select * from (" +
                "select FSecurityCode, FPortCode, FInOutType as FInOut, FInvestType" + //添加投资类型字段 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                (bAnaly1 ? ", FAnalysisCode1" : "") +
                (bAnaly2 ? ", FAnalysisCode2" : "") +
                (bAnaly3 ? ", FAnalysisCode3" : "") +
                " , FAttrClsCode " + ////MS00021   国内股票业务   QDV4.1赢时胜（上海）2009年4月20日21_A  综合业务表中已添加了属性分类字段 2009-07-07 蒋锦
                ", sum(FAmount) as FAmount,sum(FExchangeCost) as FExchangeCost,sum(FMExCost) as FMExchangeCost,sum(FVExCost) as FVExchangeCost" +
                ", sum(FPortExCost) as FPortExchangeCost,sum(FMPortExCost) as FMPortExchangeCost,sum(FVPortExCost) as FVPortExchangeCost" +
                ", sum(FBaseExCost) as FBaseExchangeCost,sum(FMBaseExCost) as FMBaseExchangeCost,sum(FVBaseExCost) as FVBaseExchangeCost" +
                ", FExchangeDate " +
                " from " + pub.yssGetTableName("Tb_Data_Integrated") +
                " ie "+
                " where FCheckState = 1 " +
                " and FOperDate = " + dbl.sqlDate(dDate) + // xuqiji 20090417  MS00386   综合业务针对兑换日期的几处修改 原因：此处改为业务日期
                " and FPortCode in (" + portCodes + ")" +
                /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
                * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//				(statCodes.length()>0?" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")":"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
                (statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")":"")+
                /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
                
                (filterSql.length()>0?(" and "+filterSql):"")+ //这里进一步添加筛选条件筛选综合业务中的相关数据 by leeyu 20100419 QDV4中保2010年4月14日02_B MS01092 合并太平版本代码
               //panjunfang modify 20120228 STORY #1434  来源为null的数据不能排除，ETF组合 只排除当日因申购赎回而发生的证券变动数据
               (super.isBETFStat() ? " and (FDataOrigin not in ('ETFBS') or FDataOrigin is null) " : " "); //如果为ETF库存统计，则排除当日因申购赎回而发生的证券变动数据 MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A
            if (!bTodayAmorti) {
                strSql = strSql +
                    " AND FTradeTypeCode <> " + dbl.sqlString(YssOperCons.YSS_JYLX_PADAMORT);   //交易类型不是溢折价摊销
            }
            
        	//modified by liubo.Bug #4878
        	//此前的4367BUG注释掉了此段代码，导致T-1日有新股时应收应付库存统计不对/
        	//================================
            //modify by zhouwei 20120428 bug4367证券变更后成本
            strSql = strSql+" and not exists (select * from  " + pub.yssGetTableName("Tb_Data_Integrated") + 
            		" where ie.fnum = fnum and ie.fsubnum = fsubnum and "+
            "ftradetypecode = '87' and frelanum = ' ' and finouttype = 1 "+
            "and FOperDate = " + dbl.sqlDate(dDate)+ " and FCheckState = 1 )";
        	//===============end=================
            strSql = strSql +
                " group by FSecurityCode, FPortCode, FExchangeDate,FInOutType,FAttrClsCode, FInvestType" + // 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                (bAnaly1 ? ", FAnalysisCode1" : "") +
                (bAnaly2 ? ", FAnalysisCode2" : "") +
                (bAnaly3 ? ", FAnalysisCode3" : "") + ") a" +
                //---------------------------------------------------------------
                " left join (select FSecurityCode,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
               	" where FCheckState = 1 "+
               	/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
               	* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//               	(statCodes.length()>0?" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")":"")+//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
               	(statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")":"")+
               	/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
               	") b on a.FSecurityCode = b.FSecurityCode" +
                //---------------------------------------------------------------
                " left join (select FPortCode,FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) c on a.FPortCode = c.FPortCode";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	/**shashijie 2013-1-29 BUG 7005 在综合业务中维护了证券代码变更数据，如有其他业务会造成证券代码变更业务出错*/
            	/**shashijie 2011-10-26 BUG 2995 在处理资产估值报错,若没有证券则不做证券兑换业务*/
            	if (rs.getString("FSecurityCode")==null || rs.getString("FSecurityCode").trim().equals("")) {
					continue ;
					//return ;//解决BUG 7005 这里把return改成continue
				}
            	/**end shashijie 2011-10-26 BUG 2995 */
				/**end shashijie 2013-1-29 BUG 7005 */
            	
                //------------增加币种信息检查 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A----//
                if (rs.getString("FTradeCury") == null || rs.getString("FTradeCury").trim().length() == 0) {
                    throw new YssException("系统进行证券库存统计,在统计综合业务项时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的币种信息不存在!" + "\n"
                                           + "请核查以下信息:" + "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核" + "\n" +
                                           "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                }
                //--------------------------------------------------------------------//

                dBaseRate = 1;
                dPortRate = 1;

                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (bAnaly2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                    (rs.getString("FAttrClsCode") == null ||
                     rs.getString("FAttrClsCode").length() == 0 ? " " :
                     rs.getString("FAttrClsCode") + "\f" +  // 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                     rs.getString("FInvestType"));//获取投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A

                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FPortCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);
                //------------增加汇率信息检查 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A--//
                if (dBaseRate == 0 || dPortRate == 0) {
                    throw new YssException("系统进行证券库存统计,在统计综合业务项时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的汇率信息不存在!" + "\n"
                                           + "请核查以下信息:" + "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                           "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");
                }
                //--------------------------------------------------------------------//

                if (hmEveStg.containsKey(sKey)) {
                    secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));

                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");
                    secstorage.setInvestType(rs.getString("FInvestType"));//设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                    //fanghaoln 20100511 MS01125 QDV4赢时胜上海2010年04月27日01_AB   
                    secstorage.setAttrCode(rs.getString("FAttrClsCode"));
                    //---------------------------end------------------------------
                    secstorage.setStrStorageAmount(YssD.add(YssFun.toDouble(
                        secstorage.getStrStorageAmount()),
                        rs.getDouble("FAmount")) + "");

                    secstorage.setStrStorageCost(YssD.add(YssFun.toDouble(secstorage.
                        getStrStorageCost()),
                        rs.getDouble("FExchangeCost")) + "");
                    secstorage.setStrMStorageCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMStorageCost()),
                        rs.getDouble("FMExchangeCost")) + "");
                    secstorage.setStrVStorageCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVStorageCost()),
                        rs.getDouble("FVExchangeCost")) + "");

                    secstorage.setStrBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrBaseCuryCost()),
                        rs.getDouble("FBaseExchangeCost")) + "");
                    secstorage.setStrMBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMBaseCuryCost()),
                        rs.getDouble("FMBaseExchangeCost")) + "");
                    secstorage.setStrVBaseCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVBaseCuryCost()),
                        rs.getDouble("FVBaseExchangeCost")) + "");

                    secstorage.setStrPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrPortCuryCost()),
                        rs.getDouble("FPortExchangeCost")) + "");
                    secstorage.setStrMPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrMPortCuryCost()),
                        rs.getDouble("FMPortExchangeCost")) + "");
                    secstorage.setStrVPortCuryCost(YssD.add(YssFun.toDouble(
                        secstorage.getStrVPortCuryCost()),
                        rs.getDouble("FVPortExchangeCost")) + "");
                } else {
                    secstorage = new SecurityStorageBean();
                    secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
//               secstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
                    secstorage.setStrPortCode(rs.getString("FPortCode"));
                    secstorage.setInvestType(rs.getString("FInvestType"));//设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                    //fanghaoln 20100511 MS01125 QDV4赢时胜上海2010年04月27日01_AB   
                    secstorage.setAttrCode(rs.getString("FAttrClsCode"));
                    //---------------------------end------------------------------
                    secstorage.setStrCuryCode(rs.getString("FTradeCury"));
                    if (bAnaly1) {
                        secstorage.setStrFAnalysisCode1(rs.getString(
                            "FAnalysisCode1"));
                    } else {
                        secstorage.setStrFAnalysisCode1(" ");
                    }
                    if (bAnaly2) {
                        secstorage.setStrFAnalysisCode2(rs.getString(
                            "FAnalysisCode2"));
                    } else {
                        secstorage.setStrFAnalysisCode2(" ");
                    }
                    if (bAnaly3) {
                        secstorage.setStrFAnalysisCode3(rs.getString(
                            "FAnalysisCode3"));
                    } else {
                        secstorage.setStrFAnalysisCode3(" ");
                    }

                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");

                    secstorage.setStrStorageAmount(rs.getDouble("FAmount") + "");
                    secstorage.setStrStorageCost(rs.getDouble("FExchangeCost") + "");
                    secstorage.setStrMStorageCost(rs.getDouble("FMExchangeCost") +
                                                  "");
                    secstorage.setStrVStorageCost(rs.getDouble("FVExchangeCost") +
                                                  "");

                    secstorage.setStrBaseCuryCost(rs.getDouble("FBaseExchangeCost") +
                                                  "");
                    secstorage.setStrMBaseCuryCost(rs.getDouble("FMBaseExchangeCost") +
                        "");
                    secstorage.setStrVBaseCuryCost(rs.getDouble("FVBaseExchangeCost") +
                        "");

                    secstorage.setStrPortCuryCost(rs.getDouble("FPortExchangeCost") +
                                                  "");
                    secstorage.setStrMPortCuryCost(rs.getDouble("FMPortExchangeCost") +
                        "");
                    secstorage.setStrVPortCuryCost(rs.getDouble("FVPortExchangeCost") +
                        "");

                    secstorage.setBailMoney(0);

                    //MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A  综合业务表中已添加了属性分类字段 2009-07-07 蒋锦
                    secstorage.setAttrCode(rs.getString("FAttrClsCode"));

                    hmEveStg.put(sKey, secstorage);
                }
            }
        } catch (Exception e) {
            throw new YssException("在统计综合业务项时出现异常!" + "\n", e); //统计综合业务出错 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A

        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 判断今日有买入的持有到期和可供出售的债券，重新计算实际利率
     * MS00656 QDV4赢时胜(上海)2009年8月24日01_A 2009.09.04 蒋锦 添加
     * @param hmSecStg HashMap：证券库存
     * @param dDate Date：业务日期
     * @param bAnaly1 boolean
     * @param bAnaly2 boolean
     * @param bAnaly3 boolean
     * @throws YssException
     */
    private void refreshEffectiveRate(HashMap hmSecStg,
                                      java.util.Date dDate,
                                      boolean bAnaly1,
                                      boolean bAnaly2,
                                      boolean bAnaly3) throws YssException{
        ResultSet rs = null;
        StringBuffer sqlBuf = new StringBuffer();
        BondInsCfgFormula insCfg = null;
        HashMap hmInsStartDate = new HashMap();
        double dbLastPAD = 0;
        String sKey = "";
        double dbFaceValue = 0;
        double dbInsFrequency = 0;
        SecurityStorageBean storageBean = null;
        double dbEffectiveRate = 0;
        try {
            insCfg = new BondInsCfgFormula();
            insCfg.setYssPub(pub);

            //查出当天有买入的“持有到期”和“可供出售”的交易数据（数量方向为流入）
            sqlBuf.append(" SELECT a.*, c.FFAceValue, c.FInsStartDate, c.FInsEndDate, c.FInsFrequency, c.FSubCatCode, c.FExchangeCode, c.FFaceRate ");
            sqlBuf.append(" FROM (SELECT FSecurityCode, FTradeTypeCode, FPortCode, ");
            sqlBuf.append(" FInvMgrCode AS FAnalysisCode1, FBrokerCode AS FAnalysisCode2, ");
            sqlBuf.append(" ' ' AS FAnalysisCode3, FAttrClsCode, FInvestType ");
            sqlBuf.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Subtrade"));
            sqlBuf.append(" WHERE FCheckState = 1 ");
            /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
            * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//            sqlBuf.append((statCodes.length()>0?" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")":""));//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
            sqlBuf.append((statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")":""));
            /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
            sqlBuf.append(" AND FBargainDate = ").append(dbl.sqlDate(dDate));
            sqlBuf.append(" AND FInvestType IN (" + dbl.sqlString(YssOperCons.YSS_INVESTTYPE_CYDQ) + "," +
                          dbl.sqlString(YssOperCons.YSS_INVESTTYPE_KGCS) +") ");
            sqlBuf.append(" UNION ");
            sqlBuf.append(" SELECT FSecurityCode, FTradeTYpeCode, FPortCode, FAnalysisCode1, ");
            sqlBuf.append(" FAnalysisCode2, FAnalysisCode3, FAttrClsCode, FInvestType ");
            sqlBuf.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Integrated"));
            sqlBuf.append(" WHERE FCheckState = 1 ");
            /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
            * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//            sqlBuf.append((statCodes.length()>0?" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")":""));//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
            sqlBuf.append((statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")":""));
            /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
            sqlBuf.append(" AND FOperDate = ").append(dbl.sqlDate(dDate));
            sqlBuf.append(" AND FInvestType IN (" + dbl.sqlString(YssOperCons.YSS_INVESTTYPE_CYDQ) + "," +
                          dbl.sqlString(YssOperCons.YSS_INVESTTYPE_KGCS) +")) a ");
            sqlBuf.append(" JOIN (SELECT FTradeTypeCode, FAmountInd ");
            sqlBuf.append(" FROM Tb_Base_TradeType ");
            sqlBuf.append(" WHERE FAmountInd = 1 ");
            sqlBuf.append(" AND FCheckState = 1) b ON a.FTradeTypeCode = b.FTradeTypeCode ");
            sqlBuf.append(" JOIN (SELECT fi.FSecurityCode, FFaceValue, FInsStartDate, FInsEndDate, FInsFrequency, FSubCatCode, FExchangeCode, FFaceRate");
            sqlBuf.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Fixinterest")).append(" fi ");
            sqlBuf.append(" LEFT JOIN ").append(pub.yssGetTableName("TB_Para_Security")).append(" se");
            sqlBuf.append(" ON fi.FSecurityCode = se.FSecurityCode");
            sqlBuf.append(" WHERE fi.FCheckState = 1) c ON a.FSecurityCode = c.FSecurityCode");

            rs = dbl.queryByPreparedStatement(sqlBuf.toString()); //modify by fangjiang 2011.08.14 STORY #788
            while(rs.next()){
                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (bAnaly2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "") + "\f" +
                    rs.getString("FAttrClsCode") + "\f" +
                    rs.getString("FInvestType");

                storageBean = (SecurityStorageBean)hmSecStg.get(sKey);
                if(storageBean == null){
                    return;
                }
                dbFaceValue = rs.getDouble("FFAceValue");
                dbInsFrequency = rs.getDouble("FInsFrequency");
                dbLastPAD =
                    YssD.sub(
                        YssFun.toDouble(storageBean.getStrStorageCost()),
                        YssD.round(
                            YssD.mul(
                                dbFaceValue,
                                YssFun.toDouble(storageBean.getStrStorageAmount())),
                            2));
                insCfg.getNextStartDateAndEndDate(dDate,
                                                  rs.getDate("FInsStartDate"),
                                                  rs.getDate("FInsEndDate"),
                                                  dbInsFrequency,
                                                  hmInsStartDate);
                dbEffectiveRate =
                    calEffectiveRate(dDate,
                                     dbLastPAD,
                                     YssFun.toDouble(storageBean.getStrStorageAmount()),
                                     dbFaceValue,
                                     rs.getDouble("FFaceRate"),
                                     dbInsFrequency,
                                     rs.getString("FSubCatCode"),
                                     rs.getString("FExchangeCode"),
                                     rs.getDate("FInsEndDate"),
                                     (java.util.Date) hmInsStartDate.get("InsStartDate"),
                                     (java.util.Date) hmInsStartDate.get("InsEndDate"));

                storageBean.setEffectiveRate(dbEffectiveRate);
            }
        } catch (Exception ex) {
            throw new YssException("计算债券实际利率出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 计算实际利率
     * MS00656 QDV4赢时胜(上海)2009年8月24日01_A 2009.09.04 蒋锦 添加
     * @param dDate Date：业务日期
     * @param dbLastPAD double：债券摊余成本
     * @param dbAmount double：债券库存数量
     * @param dbFaceValue double：债券面值
     * @param dbFaceRate double：债券票面利率
     * @param dbInsFrequency double：债券年付息频率
     * @param sSubCatCode String：债券品种类型
     * @param sExchangeCode String：债券交易说
     * @param dInsEndDate Date：债券到期日
     * @param dInsPeriodStartDate Date：本期计息起始日
     * @param dInsPeriodEndDate Date：本期计息截止日
     * @return double
     * @throws YssException
     */
    private double calEffectiveRate(java.util.Date dDate,
                                    double dbLastPAD,
                                    double dbAmount,
                                    double dbFaceValue,
                                    double dbFaceRate,
                                    double dbInsFrequency,
                                    String sSubCatCode,
                                    String sExchangeCode,
                                    java.util.Date dInsEndDate,
                                    java.util.Date dInsPeriodStartDate,
                                    java.util.Date dInsPeriodEndDate) throws YssException {
        double dbEffectiveRate = 0;
        int N = 0;      //摊销剩余天数
        double I = 0;   //票面日利率
        int iThisPeriodLastDays = 0;        //本期剩余计息天数
        boolean bLeapYear = false;          //闰年是否计息
        double dbErrorDigit = 0.00000001;   //插值误差保留位数
        int iEffRateDigit = 12;             //实际利率保留位数
        double P = 0;
        double dbMax = YssD.div(4,365);
        double dbMin = YssD.div(-1,365);
        try {
            P = dbLastPAD;
            //非银行间债券和非贴现债(FI04)2月29日不计息 也就是银行间债券和贴现摘2月29日是计息的
            if(sExchangeCode.equalsIgnoreCase(YssOperCons.YSS_JYSDM_YHJ) || sSubCatCode.equalsIgnoreCase("FI04")){
                bLeapYear = true;
            }
            //摊销剩余天数 = 到期日-业务日 +1 - （闰年计息? 0 : 业务日期和到期日期之间的闰年数[29号的天数]）
            N = YssFun.dateDiff(dDate, dInsEndDate) + 1 - (bLeapYear? 0 : YssFun.getLeapYears(dDate, dInsEndDate));

            //本期剩余天数 = 本期计息截止日 - 业务日 +1 - （闰年计息 ? 0 : 业务日期 to 本期计息截止日 之间的闰年数[29号的天数]）
            iThisPeriodLastDays = YssFun.dateDiff(dDate, dInsPeriodEndDate) + 1 - (bLeapYear? 0 : YssFun.getLeapYears(dDate, dInsPeriodEndDate));

            //票面日利率 = 债券票面利率 / 年付息频率 /(本期计息截止日-本期计息开始日 +1 - [本期闰年多出来的2月29日])
            I = YssD.div((dbFaceRate / dbInsFrequency),
                         YssFun.dateDiff(dInsPeriodStartDate, dInsPeriodEndDate) + 1 - (bLeapYear? 0 : YssFun.getLeapYears(dInsPeriodStartDate, dInsPeriodEndDate)));


            while (Math.abs(P) > dbErrorDigit) {
                //实际利率 =
                dbEffectiveRate = YssD.div(YssD.add(dbMax, dbMin), 2);

                //摊余成本/库存数量 * (1+实际利率)的摊销剩余天数次幂
                P = YssD.mul(YssD.div(dbLastPAD,dbAmount),Math.pow((1 + dbEffectiveRate),N));

                P += dbFaceValue * (dbEffectiveRate - I / 100) /
                    dbEffectiveRate *
                    (YssD.pow( (1 + dbEffectiveRate), N) - YssD.pow( (1 + dbEffectiveRate), (N - iThisPeriodLastDays)));
                if (P > 0) {
                    dbMax = (dbMax + dbMin) / 2;
                } else {
                    dbMin = (dbMax + dbMin) / 2;
                }
            }
        } catch (Exception ex) {
            throw new YssException("计算实际利率出错！", ex);
        }
        return YssD.round(dbEffectiveRate, iEffRateDigit);
    }

    public HashMap getAmortizationCost(java.util.Date dDate, HashMap hmEveStg,
                                       boolean bAnaly1, boolean bAnaly2,
                                       boolean bAnaly3,
                                       Hashtable amzFi) throws YssException {
        StringBuffer buf = new StringBuffer();
        SecurityStorageBean secstorage = null;
        double dBaseRate;
        double dPortRate;
        ResultSet rs = null;
        String sKey = "";
        String strSql = "";
        try {
            buf.append("select sto.*,rec.FTsfTypeCode as FTsfTypeCode,rec.FSubTsfTypeCode as FSubTsfTypeCode, ");
            buf.append(" rec.FSecurityCode as RFSecurityCode,");
            buf.append(" rec.FPortCode as RFPortCode,");
            buf.append(" rec.FAnalysisCode1 as RFAnalysisCode1,");
            buf.append(" rec.FAnalysisCode2 as RFAnalysisCode2,");
            buf.append(" rec.FAnalysisCode3 as RFAnalysisCode3,");
            buf.append(
                " rec.FMoney as FMoney,rec.FMMoney as FMMoney,rec.FVMoney as FVMoney,");
            buf.append(
                " rec.FBaseCuryMoney as FBaseCuryMoney,rec.FMBaseCuryMoney as FMBaseCuryMoney,");
            buf.append(
                " rec.FVBaseCuryMoney as FVBaseCuryMoney,rec.FPortCuryMoney as FPortCuryMoney,");
            buf.append(
                " rec.FMPortCuryMoney as FMPortCuryMoney,rec.FVPortCuryMoney as FVPortCuryMoney");
            buf.append(",rec.FAttrclsCode as RFAttrclsCode "); //直接获取应收应付数据中的所属分类。在之前的代码中HashTable中的Key中部存在所属分类。modify sj 20081114 暂无 bug编号
            buf.append(" from ");
            buf.append(" (select FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FSecurityCode,");
            buf.append("FTsfTypeCode,FSubTsfTypeCode,FCuryCode,FMoney,FMMoney,FVMoney,FBaseCuryRate,FBaseCuryMoney,FMBaseCuryMoney,");
            buf.append("FAttrclsCode,"); //直接获取应收应付数据中的所属分类。在之前的代码中HashTable中的Key中部存在所属分类。modify sj 20081114 暂无 bug编号
            buf.append("FVBaseCuryMoney,FPortCuryRate,FPortCuryMoney,FMPortCuryMoney,FVPortCuryMoney from ");
            buf.append(pub.yssGetTableName("Tb_data_secrecpay"));
            buf.append(" where FTsfTypeCode in ('20','21')");
            buf.append(" and FSubTsfTypeCode in ('20FI', '21FI') ");
            /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
            * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//            buf.append((statCodes.length()>0?" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")":""));//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
            buf.append((statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")":""));
            /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
            buf.append(" and FPortCode in (");
            buf.append(portCodes);
            buf.append(") and FCheckState = 1 and FTransDate = ");
            buf.append(dbl.sqlDate(dDate));
            buf.append(" ) rec ");
            buf.append(" left join (");
            buf.append(
                "select FSecurityCode,FStorageDate,FPortCode,FAnalysisCode1,FAnalysisCode2,");
            buf.append(" FAnalysisCode3,FCuryCode,FStorageAmount,FStorageCost,FMStorageCost,FVStorageCost,");
            buf.append(" FFreezeAmount,FBaseCuryRate,FBasecuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryRate,");
            buf.append(" FAttrClsCode,"); //增加了FAttrClsCode,需要增加此字段。
            buf.append(" FMPortcuryCost,FVPortCuryCost from  ");
            buf.append(pub.yssGetTableName("Tb_stock_security"));
            buf.append("  where ");
            buf.append(operSql.sqlStoragEve(dDate));
            /**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
            * 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//            buf.append((statCodes.length()>0?" and FSecurityCode in("+operSql.sqlCodes(statCodes)+")":""));//如果证券不为空用证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
            buf.append((statCodes.length()>0?" and (" + operSql.getNumsDetail(statCodes,"FSecurityCode",500) + ")":""));
            /**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
            buf.append(" and FPortCode in (");
            buf.append(portCodes);
            buf.append(")) sto ");

            buf.append(" on sto.FStorageDate = ");
            buf.append(dbl.sqlDate(YssFun.addDay(dDate, -1)));
            buf.append(" and sto.FPortCode =  rec.FPortCode ");
            if (bAnaly1) {
                buf.append("  and sto.FAnalysisCode1 = rec.FAnalysisCode1 ");
            } else {
                buf.append("");
            }
            if (bAnaly2) {
                buf.append("  and sto.FAnalysisCode2 = rec.FAnalysisCode2 ");
            } else {
                buf.append("");
            }

            if (bAnaly3) {
                buf.append("  and sto.FAnalysisCode3 = rec.FAnalysisCode3 ");
            } else {
                buf.append("");
            }

            buf.append(" and sto.FSecurityCode =  rec.FSecurityCode");

            strSql = buf.toString();
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                dBaseRate = 1;
                dPortRate = 1;

                dBaseRate = rs.getDouble("FBaseCuryRate");

                dPortRate = rs.getDouble("FPortCuryRate");

                sKey = rs.getString("RFSecurityCode") + "\f" +
                    rs.getString("RFPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("RFAnalysisCode1") : "") +
                    (bAnaly2 ? "\f" + rs.getString("RFAnalysisCode2") : "") +
                    (bAnaly3 ? "\f" + rs.getString("RFAnalysisCode3") : "") + "\f" +
                    (rs.getString("RFAttrclsCode") == null ||
                     rs.getString("RFAttrclsCode").length() == 0 ? rs.getString("FAttrclsCode") :
                     rs.getString("RFAttrclsCode")); //直接获取应收应付数据中的所属分类。在之前的代码中HashTable中的Key中部存在所属分类。modify sj 20081114 暂无 bug编号
                if (hmEveStg.containsKey(sKey)) {
                    secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
                    secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
                    secstorage.setStrBaseCuryRate(dBaseRate + "");
                    secstorage.setStrPortCuryRate(dPortRate + "");
                    if (rs.getString("FTsfTypeCode").equalsIgnoreCase(YssOperCons.
                        Yss_ZJDBLX_Discounts)) {
                        if (amzFi.containsKey(rs.getString("RFSecurityCode"))) {
                            secstorage.setStrStorageCost(YssD.add(rs.getDouble(
                                "FMoney"),
                                YssFun.toDouble(secstorage.getStrStorageCost())) +
                                "");
                            secstorage.setStrMStorageCost(YssD.add(rs.getDouble(
                                "FMMoney"),
                                YssFun.toDouble(secstorage.getStrMStorageCost())) +
                                "");
                            secstorage.setStrVStorageCost(YssD.add(rs.getDouble(
                                "FVMoney"),
                                YssFun.toDouble(secstorage.getStrVStorageCost())) +
                                "");

                            secstorage.setStrBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrStorageCost()), dBaseRate) + "");
                            secstorage.setStrMBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrMStorageCost()), dBaseRate) + "");
                            secstorage.setStrVBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrVStorageCost()), dBaseRate) + "");

                            secstorage.setStrPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")) +
                                "");
                            secstorage.setStrMPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrMStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")) +
                                "");
                            secstorage.setStrVPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrVStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")) +
                                "");
                        } else {
                            secstorage.setStrStorageCost(YssD.add(rs.getDouble(
                                "FMoney"), rs.getDouble("FStorageCost")) + "");
                            secstorage.setStrMStorageCost(YssD.add(rs.getDouble(
                                "FMMoney"),
                                rs.getDouble("FMStorageCost")) +
                                "");
                            secstorage.setStrVStorageCost(YssD.add(rs.getDouble(
                                "FVMoney"),
                                rs.getDouble("FVStorageCost")) +
                                "");
                            secstorage.setStrBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrStorageCost()), dBaseRate) + "");
                            secstorage.setStrMBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrMStorageCost()), dBaseRate) + "");
                            secstorage.setStrVBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrVStorageCost()), dBaseRate) + "");

                            secstorage.setStrPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")) +
                                "");
                            secstorage.setStrMPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrMStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")) +
                                "");
                            secstorage.setStrVPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrVStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")) +
                                "");
                        }
                    } else if (rs.getString("FTsfTypeCode").equalsIgnoreCase(
                        YssOperCons.Yss_ZJDBLX_Premium)) {
                        if (amzFi.containsKey(rs.getString("RFSecurityCode"))) {
                            secstorage.setStrStorageCost(YssD.sub(YssFun.toDouble(
                                secstorage.getStrStorageCost()), rs.getDouble(
                                    "FMoney")) + "");
                            secstorage.setStrMStorageCost(YssD.sub(
                                YssFun.toDouble(secstorage.getStrMStorageCost()),
                                rs.getDouble(
                                    "FMMoney")) + "");
                            secstorage.setStrVStorageCost(YssD.sub(
                                YssFun.toDouble(secstorage.getStrVStorageCost()),
                                rs.getDouble(
                                    "FVMoney")) + "");
                            secstorage.setStrBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrStorageCost()), dBaseRate) + "");
                            secstorage.setStrMBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrMStorageCost()), dBaseRate) + "");
                            secstorage.setStrVBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrVStorageCost()), dBaseRate) + "");

                            secstorage.setStrPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")) +
                                "");
                            secstorage.setStrMPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrMStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")) +
                                "");
                            secstorage.setStrVPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrVStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")) +
                                "");
                        } else {
                            secstorage.setStrStorageCost(YssD.sub(rs.getDouble(
                                "FStorageCost"), rs.getDouble(
                                    "FMoney")) + "");
                            secstorage.setStrMStorageCost(YssD.sub(
                                rs.getDouble("FMStorageCost"),
                                rs.getDouble(
                                    "FMMoney")) + "");
                            secstorage.setStrVStorageCost(YssD.sub(
                                rs.getDouble("FVStorageCost"),
                                rs.getDouble(
                                    "FVMoney")) + "");
                            secstorage.setStrBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrStorageCost()), dBaseRate) + "");
                            secstorage.setStrMBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrMStorageCost()), dBaseRate) + "");
                            secstorage.setStrVBaseCuryCost(this.getSettingOper().
                                calBaseMoney(YssFun.toDouble(
                                    secstorage.getStrVStorageCost()), dBaseRate) + "");

                            secstorage.setStrPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), rs.getDate("FStorageDate"), rs.getString("FPortCode")) +
                                "");
                            secstorage.setStrMPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrMStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), rs.getDate("FStorageDate"), rs.getString("FPortCode")) +
                                "");
                            secstorage.setStrVPortCuryCost(this.getSettingOper().
                                calPortMoney(YssFun.toDouble(
                                    secstorage.getStrVStorageCost()), dBaseRate,
                                             dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), rs.getDate("FStorageDate"), rs.getString("FPortCode")) +
                                "");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("在统计摊销业务项时出现异常!" + "\n", e); // 检查摊销业务异常 by 曹丞 2009.01.21 MS00004 QDV4.1-2009.2.1_09A

        } finally {
            dbl.closeResultSetFinal(rs); //之前没有关闭游标。sj modified 20090121
        }
        return hmEveStg;
    }

    /**
     * 处理送股冻结业务 MS00125  by leeyu 2009-01-07
     * @param dDate Date
     * @throws YssException
     */
    private void setPortStorageFreezeAmount(java.util.Date dDate, HashMap hmEveStg) throws YssException {
        String sqlStr = "";
        String sKey = "";
        double dFreezeAmount = 0;
        ResultSet rs = null;

        String[] arrAnalysisCode = new String[3];
        Hashtable htCheckPort = null; //根据冻结组合来取数据，这里取冻结组合

        CtlPubPara ctlPub = null;
        try {
            arrAnalysisCode[0] = operSql.storageAnalysisType("FAnalysisCode1", "Security");
            arrAnalysisCode[1] = operSql.storageAnalysisType("FAnalysisCode2", "Security");
            arrAnalysisCode[2] = operSql.storageAnalysisType("FAnalysisCode3", "Security");
            for (int i = 0; i < arrAnalysisCode.length; i++) {
                if (arrAnalysisCode[i].trim().length() == 0) {
                    continue;
                }
                if (arrAnalysisCode[i].equals("001")) {
                    arrAnalysisCode[i] = "FInvMgrCode";
                } else if (arrAnalysisCode[i].equals("002")) {
                    arrAnalysisCode[i] = "FBrokerCode";
                } else if (arrAnalysisCode[i].equals("003")) {
                    arrAnalysisCode[i] = "FExchangeCode";
                } else if (arrAnalysisCode[i].equals("004")) {
                    arrAnalysisCode[i] = "FCatCode";
                }
            }
            ctlPub = new CtlPubPara();
            ctlPub.setYssPub(pub);
            htCheckPort = ctlPub.getBondShareInfo(portCodes.replaceAll("'", ""));
            sqlStr = "select subTrade.*,'Freeze' as FType  " + ///* 在日期段内需要冻结的部分 */
                " from (select FTSecurityCode,FExRightDate  from " + pub.yssGetTableName("tb_data_bonusshare") + " " +
                " where FCheckState = 1  and " + dbl.sqlDate(dDate) + " between FExrightDate and  FPayDate - 1) bonusshare " +
                " join (select s1.*,s2.fexchangecode,s2.fcatcode from " + pub.yssGetTableName("tb_data_subtrade") +
                " s1 left join " + pub.yssGetTableName("tb_para_security") +
                " s2 on s1.FsecurityCode =s2.Fsecuritycode where s1.FCheckstate = 1 and s1.FTradeTypeCode = '" + YssOperCons.YSS_JYLX_SG + "') subTrade on subTrade.Fsecuritycode = " +
                " bonusshare.FTSecurityCode  and subTrade.Fbargaindate = bonusshare.FExRightDate " +
                " union all select subTrade.*,'Thaw' as FType  " + ///* 在支付日需要解冻的部分 */
                " from (select FTSecurityCode,FExRightDate from " + pub.yssGetTableName("tb_data_bonusshare") + " " +
                " where FCheckState = 1 and FPayDate = " + dbl.sqlDate(dDate) + ") bonusshare " +
                " join (select s1.*,s2.fexchangecode,s2.fcatcode from " + pub.yssGetTableName("tb_data_subtrade") +
                " s1 left join " + pub.yssGetTableName("tb_para_security") +
                " s2 on s1.FsecurityCode =s2.Fsecuritycode where s1.FCheckstate = 1 and s1.FTradeTypeCode = '" + YssOperCons.YSS_JYLX_SG + "') subTrade on subTrade.Fsecuritycode = " +
                " bonusshare.FTSecurityCode and subTrade.Fbargaindate = bonusshare.FExRightDate ";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                if (htCheckPort.get(rs.getString("FPortCode")) == null || (Boolean.valueOf(htCheckPort.get(rs.getString("FPortCode")).toString()).booleanValue()) == false) {
                    continue;
                }
                if (rs.getString("FType").equalsIgnoreCase("Freeze")) {
                    dFreezeAmount = rs.getDouble("FTradeAmount");
                } else if (rs.getString("FType").equalsIgnoreCase("Thaw")) {
                    dFreezeAmount = 0;
                }
                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (arrAnalysisCode[0].trim().length() > 0 ? ("\f" + rs.getString(arrAnalysisCode[0])) : "") +
                    (arrAnalysisCode[1].trim().length() > 0 ? ("\f" + rs.getString(arrAnalysisCode[1])) : "") +
                    (arrAnalysisCode[2].trim().length() > 0 ? ("\f" + rs.getString(arrAnalysisCode[2])) : "") +
                    "\f" + (rs.getString("FAttrClsCode") == null ||
                            rs.getString("FAttrClsCode").length() == 0 ? " " :
                            rs.getString("FAttrClsCode"));
                if (hmEveStg.containsKey(sKey)) {
                    SecurityStorageBean security = (SecurityStorageBean) hmEveStg.get(sKey);
                    security.setStrFreezeAmount(dFreezeAmount + "");
                    hmEveStg.put(sKey, security);
                }
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //-------以下为 MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A 的修改 -----------------------------------------------------------------------------------------------------------------------------------//
    /**
     * 对回购业务进行计算
     * @param dDate Date
     * @param hmEveStg HashMap
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @throws YssException
     * sj MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    private void getStgPurchaseStatData(java.util.Date dDate, HashMap hmEveStg, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        try {

            getPurchaseParams(); //获取组合级别的通用参数设置，现只有回购，故放在此处，当在其他的业务中有用的话，将其移出，并加入新的获取通用参数的方法。

            getStgPurchaseWithSubTrade(dDate, hmEveStg, analy1, analy2, analy3); //对业务资料中相关回购业务数据进行成本统计

        } catch (Exception e) {
            throw new YssException("统计回购的库存信息出现异常!", e);
        }
    }

    /**
     * 对业务资料中相关回购业务数据进行成本统计
     * @param dDate Date
     * @param hmEveStg HashMap
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @throws YssException
     * sj MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    private void getStgPurchaseWithSubTrade(java.util.Date dDate, HashMap hmEveStg, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = buildPurchaseSqlWithSubTrade(dDate); //拼装业务资料中相关回购数据sql语句
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                setSecurityStorage(rs, hmEveStg, dDate, analy1, analy2, analy3); //设置回购之相关数据
            }
        } catch (Exception e) {
            throw new YssException("统计业务资料中相关回购的库存信息出现异常!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 拼装业务资料中的回购业务数据
     * @param dDate Date 业务日期
     * @return String
     * sj MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    private String buildPurchaseSqlWithSubTrade(java.util.Date dDate) {
        StringBuffer buf = new StringBuffer();

        buf.append("SELECT PURCHASE.*, SECURITY.FSUBCATCODE, CASHACC.FCURYCODE AS FTRADECURY ");
        buf.append(" FROM (SELECT * FROM ");
        buf.append(pub.yssGetTableName("TB_DATA_SUBTRADE")); //获取业务资料
        buf.append(" WHERE fcheckstate=1 and FPORTCODE IN (");//edit by xuxuming,20091118.只查询已经审核的记录
        buf.append(this.portCodes);
        buf.append(") and FTradeTypeCode in (").append(dbl.sqlString(YssOperCons.YSS_JYLX_ZRE)).append(",").append(dbl.sqlString(YssOperCons.YSS_JYLX_NRE));
        //add by zhouwei 20120523 bug 4284 买断式回购
        buf.append(",").append(dbl.sqlString(YssOperCons.YSS_JYLX_REMR)).append(",").append(dbl.sqlString(YssOperCons.YSS_JYLX_REMC));
        buf.append(") AND FBARGAINDATE = ");
        buf.append(dbl.sqlDate(dDate));
        buf.append(") PURCHASE");
        buf.append("  LEFT JOIN (SELECT FSECURITYCODE, FCATCODE, FSUBCATCODE ");
        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("TB_PARA_SECURITY")); //获取对应的证券信息
        buf.append(" WHERE FCHECKSTATE = 1) SECURITY ON SECURITY.FSECURITYCODE = PURCHASE.FSECURITYCODE ");
        buf.append(" LEFT JOIN (SELECT FCASHACCCODE, FCURYCODE ");
        buf.append(" FROM ");
        buf.append(pub.yssGetTableName("TB_PARA_CASHACCOUNT")); //获取对应的账户信息
        buf.append(" WHERE FCHECKSTATE = 1) CASHACC ON CASHACC.FCASHACCCODE = PURCHASE.FCASHACCCODE");

        return buf.toString();
    }
    /**
     * 设置回购信息
     * @param rs ResultSet 包含回购信息的记录集
     * @param hmEveStg HashMap
     * @param dDate Date 业务日期
     * @param analy1 String 分析代码1
     * @param analy2 String 分析代码2
     * @param analy3 String 分析代码3
     * @throws YssException
     * sj MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    private void setSecurityStorage(ResultSet rs, HashMap hmEveStg, java.util.Date dDate, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        double cost = 0D;
        double dBaseRate = 1;
        double dPortRate = 1;
        String sKey = "";
        SecurityStorageBean secstorage = null;
        try {
            if (rs.getString("FTradeCury") == null || rs.getString("FTradeCury").trim().length() == 0) {
                throw new YssException("系统进行证券库存统计,在回购库存统计时检查到代码【" +
                                       rs.getString("FSecurityCode") +
                                       "】对应的币种信息不存在!" + "\n" + "请核查以下信息:" +
                                       "\n" +
                                       "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                       "2.【证券品种信息】中该证券交易币种项设置是否正确!");
            }

            dBaseRate = rs.getDouble("FBaseCuryRate"); //获取回购业务数据中的基础汇率
            if (dBaseRate == 0) { //若为0，重新计算
                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FTradeCury"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
            }
            dPortRate = rs.getDouble("FPortCuryRate"); //获取回购业务数据中的组合汇率
            if (dPortRate == 0) { //若为0，重新计算
                EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
                rateOper.setYssPub(pub);
                rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"),
                                          rs.getString("FPortCode"));
                dPortRate = rateOper.getDPortRate();
            }
            //-----------------------------增加获汇率有效性检查 by 曹丞 2009.01.20 MS00004 QDV4.1-2009.2.1_09A--------------//
            if (dBaseRate == 0 || dPortRate == 0) {
                throw new YssException("系统进行证券库存统计,在回购库存统计时检查到代码【" +
                                       rs.getString("FSecurityCode") +
                                       "】对应的汇率信息不存在!" + "\n" + "请核查以下信息:" +
                                       "\n" +
                                       "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                       "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");
            }
            sKey = rs.getString("FSecurityCode") + "\f" +
                rs.getString("FPortCode") +
                (analy1 ? "\f" + rs.getString("FInvMgrCode") :
                 "") +
                (analy2 ? "\f" + rs.getString("FBrokerCode") :
                 "") +
                (analy3 ? "\f" + " " : "") + "\f" +
                rs.getString("FAttrClsCode") + "\f" + // 2009-08-15 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                rs.getString("FInvestType"); //获取投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
            if (hmEveStg.containsKey(sKey)) { //当昨日已有
                secstorage = (SecurityStorageBean) hmEveStg.get(sKey); //获取
                secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                    "yyyy-MM-dd"));
                secstorage.setStrBaseCuryRate(Double.toString(dBaseRate));
                secstorage.setStrPortCuryRate(Double.toString(dPortRate));
                //  fanghaoln 20100511 MS01125 QDV4赢时胜上海2010年04月27日01_AB 
                secstorage.setAttrCode(rs.getString("FAttrClsCode")); //增加所属分类
                //-------------------------end ------MS01125----------------------
                secstorage.setInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                secstorage.setStrStorageAmount(Double.toString(YssD.add(YssFun.toDouble(
                    secstorage.getStrStorageAmount()),
                    rs.getDouble("FTradeAmount")))); //若在业务资料中的回购，则存在交易数量
                cost = calcPurchaseCost(rs); //算出原币成本

                secstorage.setStrStorageCost(Double.toString(YssD.add(cost,
                    YssFun.toDouble(secstorage.getStrStorageCost()))));
                secstorage.setStrMStorageCost(Double.toString(YssD.add(cost,
                    YssFun.toDouble(secstorage.getStrMStorageCost()))));
                secstorage.setStrVStorageCost(Double.toString(YssD.add(cost,
                    YssFun.toDouble(secstorage.getStrVStorageCost()))));

                calcPurchaseBaseCost(secstorage, dBaseRate); //计算基础金额
                calcPurchasePortCost(rs, dDate, secstorage, dBaseRate, dPortRate); //计算组合金额
            } else { //若今日新交易
                secstorage = new SecurityStorageBean(); //生成
                secstorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                secstorage.setStrStorageDate(YssFun.formatDate(dDate,
                    "yyyy-MM-dd"));

                secstorage.setStrPortCode(rs.getString("FPortCode"));
                //  fanghaoln 20100511 MS01125 QDV4赢时胜上海2010年04月27日01_AB 
                secstorage.setAttrCode(rs.getString("FAttrClsCode")); 
                //-------------------------end ------MS01125----------------------
                secstorage.setInvestType(rs.getString("FInvestType")); //设置投资类型 2009-08-15 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                secstorage.setStrCuryCode(rs.getString("FTradeCury")); //通过现金帐户来获取的交易货币
                if (analy1) {
                    secstorage.setStrFAnalysisCode1(rs.getString(
                        "FInvMgrCode"));
                } else {
                    secstorage.setStrFAnalysisCode1(" ");
                }
                if (analy2) {
                    secstorage.setStrFAnalysisCode2(rs.getString(
                        "FBrokerCode"));
                } else {
                    secstorage.setStrFAnalysisCode2(" ");
                }
                if (analy3) {
                    secstorage.setStrFAnalysisCode3(" ");
                } else {
                    secstorage.setStrFAnalysisCode3(" ");
                }

                secstorage.setStrBaseCuryRate(Double.toString(dBaseRate));
                secstorage.setStrPortCuryRate(Double.toString(dPortRate));
                secstorage.setStrStorageAmount(Double.toString(rs.getDouble("FTradeAmount"))); //若在业务资料中的回购，则存在交易数量
                cost = calcPurchaseCost(rs); //算出原币成本
                secstorage.setStrStorageCost(Double.toString(cost));
                secstorage.setStrMStorageCost(Double.toString(cost));
                secstorage.setStrVStorageCost(Double.toString(cost));

                calcPurchaseBaseCost(secstorage, dBaseRate); //计算基础金额
                calcPurchasePortCost(rs, dDate, secstorage, dBaseRate, dPortRate); //计算组合金额
                //--- 重新更新成本 --------------------------
                updatePurchaseSubTradeCost(secstorage,rs);
                //-----------------------------------------
                hmEveStg.put(sKey, secstorage);
            }
        } catch (Exception e) {
            throw new YssException("设置回购库存信息出现异常！", e);
        }
    }

    /**
     * 计算成本
     * @param rs ResultSet 包含成本的信息
     * @return double
     * @throws YssException
     * sj MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    private double calcPurchaseCost(ResultSet rs) throws YssException {
        double cost = 0D;
        boolean pubParams = false;
        try {
            pubParams = ( (Boolean) innerPubParamsDeal.getResultWithPortAndKey(rs.getString("FPortCode"), YssOperCons.YSS_INNER_PURCHASEEIC)).booleanValue();
            if (pubParams) { //交易所回购交易费用（包含佣金）入成本
                if (YssOperCons.YSS_JYLX_NRE.equalsIgnoreCase(rs.getString("FTradeTypeCode"))
                		|| YssOperCons.YSS_JYLX_REMR.equalsIgnoreCase(rs.getString("FTradeTypeCode"))) { //买入回购 -- 及逆回购。调整回购类型    add by zhouwei 20120523 bug 4284 买断式回购
                    cost = YssD.add(rs.getDouble("FTradeMoney"), YssD.add(rs.getDouble("FTradeFee1"), rs.getDouble("FTradeFee2"), rs.getDouble("FTradeFee3"), rs.getDouble("FTradeFee4"),
                        rs.getDouble("FTradeFee5"), rs.getDouble("FTradeFee6"), rs.getDouble("FTradeFee7"), rs.getDouble("FTradeFee8"))); //交易金额+交易费用+交易佣金
                } else if (YssOperCons.YSS_JYLX_ZRE.equalsIgnoreCase(rs.getString("FTradeTypeCode"))
                		 || YssOperCons.YSS_JYLX_REMC.equalsIgnoreCase(rs.getString("FTradeTypeCode"))) { //卖出回购 -- 及正回购。调整回购类型
                    cost = YssD.add(rs.getDouble("FTradeMoney"), YssD.add(rs.getDouble("FTradeFee1"), rs.getDouble("FTradeFee2"), rs.getDouble("FTradeFee3"), rs.getDouble("FTradeFee4"),
                        rs.getDouble("FTradeFee5"), rs.getDouble("FTradeFee6"), rs.getDouble("FTradeFee7"), rs.getDouble("FTradeFee8"))); //交易金额-交易费用-交易佣金  bug 4529   modify by zhouwei 20120510 正回购成本=交易金额+费用
                }
            } else { //不入成本
                cost = rs.getDouble("FTradeMoney"); //交易金额
            }
        } catch (Exception e) {
            throw new YssException("计算回购库存原币成本出现异常!", e);
        }
        return cost;
    }

    /**
     * 计算基础成本
     * @param secstorage SecurityStorageBean 之前设置的库存信息
     * @param dBaseRate double
     * @return double
     * @throws YssException
     * sj MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    private void calcPurchaseBaseCost(SecurityStorageBean secstorage, double dBaseRate) throws YssException {
        if (null != secstorage) {
            secstorage.setStrBaseCuryCost(Double.toString(this.getSettingOper().
                calBaseMoney(YssFun.toDouble(
                    secstorage.getStrStorageCost()), dBaseRate)));
            secstorage.setStrMBaseCuryCost(Double.toString(this.getSettingOper().
                calBaseMoney(YssFun.toDouble(
                    secstorage.getStrMStorageCost()), dBaseRate)));
            secstorage.setStrVBaseCuryCost(Double.toString(this.getSettingOper().
                calBaseMoney(YssFun.toDouble(
                    secstorage.getStrVStorageCost()), dBaseRate)));
        }
    }

    /**
     * 计算组合成本
     * @param rs ResultSet 包含组合成本的信息
     * @param dDate Date
     * @param secstorage SecurityStorageBean 之前设置的库存信息
     * @param dBaseRate double
     * @param dPortRate double
     * @return double
     * @throws YssException
     * sj MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    private void calcPurchasePortCost(ResultSet rs, java.util.Date dDate, SecurityStorageBean secstorage, double dBaseRate, double dPortRate) throws YssException {
        double portCuryCost = 0D;
        double mPortCuryCost = 0D;
        double vPortCuryCost = 0D;
        if (null != secstorage) {
            try {
                portCuryCost = this.getSettingOper().
                    calPortMoney(YssFun.toDouble(
                        secstorage.getStrStorageCost()),
                                 dBaseRate, dPortRate,
                                 //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                 rs.getString("FTradeCury"), dDate, rs.getString("FPortCode"));
                secstorage.setStrPortCuryCost(Double.toString(portCuryCost));

                mPortCuryCost = this.getSettingOper().
                    calPortMoney(YssFun.toDouble(
                        secstorage.getStrMStorageCost()),
                                 dBaseRate, dPortRate,
                                 //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                 rs.getString("FTradeCury"), dDate, rs.getString("FPortCode"));
                secstorage.setStrMPortCuryCost(Double.toString(mPortCuryCost));

                vPortCuryCost = this.getSettingOper().
                    calPortMoney(YssFun.toDouble(
                        secstorage.getStrVStorageCost()),
                                 dBaseRate, dPortRate,
                                 //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                 rs.getString("FTradeCury"), dDate, rs.getString("FPortCode"));
                secstorage.setStrVPortCuryCost(Double.toString(vPortCuryCost));
            } catch (SQLException ex) {
                throw new YssException("设置回购组合库存金额出现异常!", ex);
            }
        }
    }

    /**
     * 获取此组合的回购通用参数设置
     * @throws YssException
     * sj MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    private void getPurchaseParams() throws YssException {
        innerPubParamsDeal = new InnerPubParamsWithPurchase(); //在此处实例化回购的参数获取。
        innerPubParamsDeal.setYssPub(pub);
        innerPubParamsDeal.setAllPubParamsMap(this.allPubParamsMap); //在此处使用此全局变量，以便当其他函数调用时，不将以获取的参数覆盖
        innerPubParamsDeal.getAllPubParams(portCodes);
    }

    /**
     * 获取场外回购业务到期数据，用于冲减成本。
     * @param dDate Date
     * @param hmEveStg HashMap
     * @param bAnaly1 boolean
     * @param bAnaly2 boolean
     * @param bAnaly3 boolean
     * @throws YssException
     * MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    public void getStgPurchaseBankData(java.util.Date dDate, HashMap hmEveStg,
                                         boolean bAnaly1, boolean bAnaly2,
                                         boolean bAnaly3) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        SecurityStorageBean secstorage = null;
        String sKey = "";
        strSql = "SELECT PURCHASE.* ,"+
                 " SECSTORAGE.FATTRCLSCODE,SECSTORAGE.FINVESTTYPE,SECSTORAGE.FAMOUNT " +
                 ",SECSTORAGE.FEXCHANGECOST,SECSTORAGE.FMEXCOST,SECSTORAGE.FVEXCOST,SECSTORAGE.FPORTEXCOST,SECSTORAGE.FMPORTEXCOST," +
                 "SECSTORAGE.FVPORTEXCOST,SECSTORAGE.FBASEEXCOST,SECSTORAGE.FMBASEEXCOST,SECSTORAGE.FVBASEEXCOST" +
                 " FROM " +
                 "(SELECT * FROM " + pub.yssGetTableName("TB_DATA_PURCHASE") + " WHERE FCHECKSTATE = 1 AND FMATUREDATE = " + dbl.sqlDate(dDate) + ") PURCHASE " +//获取今日到期数据
                 " LEFT JOIN " +
                 "(SELECT * FROM " + pub.yssGetTableName("TB_DATA_INTEGRATED") + " WHERE FCHECKSTATE = 1  " +
                 " ) SECSTORAGE ON PURCHASE.FSECURITYCODE = SECSTORAGE.FSECURITYCODE AND PURCHASE.FPORTCODE = SECSTORAGE.FPORTCODE AND " +
                 " CASE WHEN PURCHASE.FINVMGRCODE IS NULL THEN ' ' ELSE PURCHASE.FINVMGRCODE END = SECSTORAGE.FANALYSISCODE1 " +
                 "AND PURCHASE.Fnum = SECSTORAGE.FRelaNum "; //alter by liuwei
     
        try{
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                sKey = rs.getString("FSecurityCode") + "\f" +
                    rs.getString("FPortCode") +
                    (bAnaly1 ? "\f" + rs.getString("FInvMgrCode") :
                     "") +
                    (bAnaly2 ? "\f" + " " :  //modify by wangzuochun 2010.12.13 BUG #572 银行间回购业务到期估值报错 
                     "") +
                    (bAnaly3 ? "\f" + " " : "") + "\f" +
                    rs.getString("FAttrClsCode") + "\f" +
                    rs.getString("FInvestType"); //到期数据的
                if (hmEveStg.containsKey(sKey)) {
                    secstorage = (SecurityStorageBean) hmEveStg.get(sKey); //获取

                    secstorage.setStrStorageAmount(Double.toString(YssD.sub(YssFun.toDouble(secstorage.getStrStorageAmount()),
                        rs.getDouble("FAMOUNT")))); //直接用综合业务中的数据进行冲减

                    secstorage.setStrStorageCost(Double.toString(YssD.sub(YssFun.toDouble(secstorage.getStrStorageCost()),
                        rs.getDouble("FEXCHANGECOST")))); //直接冲减
                    secstorage.setStrMStorageCost(Double.toString(YssD.sub(YssFun.toDouble(secstorage.getStrMStorageCost()),
                        rs.getDouble("FMEXCOST")))); //直接冲减
                    secstorage.setStrVStorageCost(Double.toString(YssD.sub(YssFun.toDouble(secstorage.getStrVStorageCost()),
                         rs.getDouble("FVEXCOST")))); //直接冲减

                    secstorage.setStrBaseCuryCost(Double.toString(YssD.sub(YssFun.toDouble(secstorage.getStrBaseCuryCost()),
                        rs.getDouble("FBASEEXCOST")))); //直接冲减
                    secstorage.setStrMBaseCuryCost(Double.toString(YssD.sub(YssFun.toDouble(secstorage.getStrMBaseCuryCost()),
                        rs.getDouble("FMBASEEXCOST")))); //直接冲减
                    secstorage.setStrVBaseCuryCost(Double.toString(YssD.sub(YssFun.toDouble(secstorage.getStrVBaseCuryCost()),
                        rs.getDouble("FVBASEEXCOST")))); //直接冲减

                    secstorage.setStrPortCuryCost(Double.toString(YssD.sub(YssFun.toDouble(secstorage.getStrPortCuryCost()),
                        rs.getDouble("FPORTEXCOST")))); //直接冲减
                    secstorage.setStrMPortCuryCost(Double.toString(YssD.sub(YssFun.toDouble(secstorage.getStrMPortCuryCost()),
                        rs.getDouble("FMPORTEXCOST")))); //直接冲减
                    secstorage.setStrVPortCuryCost(Double.toString(YssD.sub(YssFun.toDouble(secstorage.getStrVPortCuryCost()),
                        rs.getDouble("FVPORTEXCOST")))); //直接冲减
                }
            }
        }
        catch(Exception e){
            throw new YssException("冲减银行间回购成本出现异常！",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    
    public void getStgPurData(java.util.Date dDate, HashMap hmEveStg,
            boolean bAnaly1, boolean bAnaly2,
            boolean bAnaly3,String sPortCode) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		SecurityStorageBean secstorage = null;

		String sKey = "";

		double dBaseRate = 1;
		double dPortRate = 1;
		Hashtable amzFi = null;
		
		double cost = 0D;
		
		try {
			amzFi = new Hashtable();
			strSql = "select  a.*, b.FPortCury,c.FTradeCury from ("
					+ 
					// --------------------------------------------------下段取回转的交易记录
					"select a.FSecurityCode,FPortCode,FInvestType "
					//-------------------
					+ ",FTRADETYPECODE "
					//-------------------
					+ // 添加投资类型字段 2009-08-15 modify by wangzuochun MS00024
						// 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
					(bAnaly1 ? ", a.FInvMgrCode" : " ")
					+ (bAnaly2 ? ", a.FBrokerCode" : " ")
					+ " ,FAttrClsCode ,("
					+ dbl.sqlIsNull("FBailMoney*FCashInd*-1", "0")
					+ ") as FBailMoney,"
					+ " ("
					+ dbl.sqlIsNull("a.FTradeAmount*FAmountInd*-1", "0")
					+ ") as FTradeAmount, ("
					+ dbl.sqlIsNull("a.FTradeMoney*FCashInd*-1", "0")
					+ ") as FTradeMoney, ("
					+
					// 成本的方向和数量的方向一样，所以这里乘的是数量方向
					dbl.sqlIsNull("a.FCost*FAmountInd*-1")
					+ ") as FCost, ("
					+ dbl.sqlIsNull("a.FMCost*FAmountInd*-1")
					+ ") as FMCost, ("
					+ dbl.sqlIsNull("a.FVCost*FAmountInd*-1")
					+ ") as FVCost, ("
					+

					dbl.sqlIsNull("a.FBaseCuryCost*FAmountInd*-1")
					+ ") as FBaseCuryCost, ("
					+ dbl.sqlIsNull("a.FMBaseCuryCost*FAmountInd*-1")
					+ ") as FMBaseCuryCost, ("
					+ dbl.sqlIsNull("a.FVBaseCuryCost*FAmountInd*-1")
					+ ") as FVBaseCuryCost, ("
					+

					dbl.sqlIsNull("a.FPortCuryCost*FAmountInd*-1")
					+ ") as FPortCuryCost, ("
					+ dbl.sqlIsNull("a.FMPortCuryCost*FAmountInd*-1")
					+ ") as FMPortCuryCost, ("
					+ dbl.sqlIsNull("a.FVPortCuryCost*FAmountInd*-1")
					+ ") as FVPortCuryCost, ("
					+

					dbl.sqlIsNull("a.FTotalCost*FAmountInd*-1", "0")
					+ ") as FTotalCost," +
					//--------------------------------------
					"FTradeFee1*-1 as FTradeFee1," +
					"FTradeFee2*-1 as FTradeFee2," +
					"FTradeFee3*-1 as FTradeFee3," +
					"FTradeFee4*-1 as FTradeFee4," +
					"FTradeFee5*-1 as FTradeFee5," +
					"FTradeFee6*-1 as FTradeFee6," +
					"FTradeFee7*-1 as FTradeFee7," +
					"FTradeFee8*-1 as FTradeFee8," +
					
					//--------------------------------------
					" 'Cancel' as FTradeState from ("
					+ "select a1.*,a2.FCashInd,a2.FAmountInd from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " a1 join (select * from Tb_Base_TradeType"
					+ " where FCheckState = 1) a2 "
					+ " on a1.FTradeTypeCode = a2.FTradeTypeCode where ((a1.FFactSettleDate = "
					+ dbl.sqlDate(dDate)
					+ " and FSettleState = 2) or (a1.FMatureDate = "
					+ dbl.sqlDate(dDate)
					+ " and (a1.FTradeTypeCode = "
					+ dbl.sqlString(YssOperCons.YSS_JYLX_ZRE)
					+ // sj add 20071120 添加了回购的条件和结算时间
					" or a1.FTradeTypeCode = "
					+ dbl.sqlString(YssOperCons.YSS_JYLX_NRE)
					//add by zhouwei 20120523 bug 4284 买断式回购
					+" or a1.FTradeTypeCode = "
					+ dbl.sqlString(YssOperCons.YSS_JYLX_REMR)
					+" or a1.FTradeTypeCode = "
					+ dbl.sqlString(YssOperCons.YSS_JYLX_REMC)
					//-----end-------------
					+ " ))"
					+
					// MS00021 国内股票业务 QDV4.1赢时胜（上海）2009年4月20日21_A 2009.07.03 蒋锦
					// 中签返款和新股申购的数据也要去掉
					") "
					//") and a1.FTradeTypeCode not in ("
					//+ dbl.sqlString(YssOperCons.YSS_JYLX_XGSG)
					//+ ","
					//+ dbl.sqlString(YssOperCons.YSS_JYLX_ZQFK)
					//+ ")"
					+
					// ----------------------------------------------------------------------------------
					" and a1.fcheckstate = 1 and FPortCode in ("
					+ sPortCode
					+ ")) a  "
					//+ " group by a.FSecurityCode, a.FPortCode, a.FAttrClsCode, a.FInvestType "
					+ // 投资类型 2009-08-15 modify by wangzuochun MS00024 交易数据拆分
						// QDV4.1赢时胜（上海）2009年4月20日24_A
					(bAnaly1 ? ", a.FInvMgrCode" : " ")
					+ (bAnaly2 ? ", a.FBrokerCode" : " ")
					+
					// ---------------------------------------------------------
					") a left join "
					+
					// --------------------------------------------------
					" (select FPortCode,FPortCury from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where FCheckState = 1) b on a.FPortCode = b.FPortCode"
					+
					// --------------------------------------------------
					" left join (select FSecurityCode,FTradeCury from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode";

			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {
				if (rs.getString("FAttrClsCode") != null
						&& rs.getString("FAttrClsCode").equalsIgnoreCase("19")) {
					amzFi.put(rs.getString("FSecurityCode"), rs
							.getString("FSecurityCode"));
				}
				dBaseRate = 1;
				dPortRate = 1;
				// -----------------------检查库存证券对应的币种代码是否正确 by 曹丞 2009.01.21
				// MS00004 QDV4.1-2009.2.1_09A------//
				if (rs.getString("FTradeCury") == null
						|| rs.getString("FTradeCury").trim().length() == 0) { // 获取交易信息异常
					throw new YssException("系统进行证券库存统计,在获取交易信息时检查到代码为【"
							+ rs.getString("FSecurityCode")
							+ "】证券对应的交易币种信息不存在!" + "\n" + "请核查以下信息:" + "\n"
							+ "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n"
							+ "2.【证券品种信息】中该证券交易币种项设置是否正确!");
				}
				// -------------------------------------------------------------------------------------------------------//

				dBaseRate = this.getSettingOper().getCuryRate(dDate,
						rs.getString("FTradeCury"), rs.getString("FPortCode"),
						YssOperCons.YSS_RATE_BASE);
				dPortRate = this.getSettingOper().getCuryRate(dDate,
						rs.getString("FPortCury"), rs.getString("FPortCode"),
						YssOperCons.YSS_RATE_PORT);

				if (dBaseRate == 0 || dPortRate == 0) {
					throw new YssException("系统进行证券库存统计,在获取交易信息时检查到代码为【"
							+ rs.getString("FSecurityCode") + "】证券对应的汇率信息不存在!"
							+ "\n" + "请核查以下信息:" + "\n"
							+ "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n"
							+ "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");

				}

				sKey = rs.getString("FSecurityCode")
						+ "\f"
						+ rs.getString("FPortCode")
						+ (bAnaly1 ? "\f" + rs.getString("FInvMgrCode") : "")
						+ (bAnaly2 ? "\f" + rs.getString("FBrokerCode") : "")
						+ (bAnaly3 ? "\f" + rs.getString("FAnalysisCode3") : "")
						+ "\f"
						+ (rs.getString("FAttrClsCode") == null
								|| rs.getString("FAttrClsCode").length() == 0 ? " "
								: rs.getString("FAttrClsCode")) + "\f" + // 2009-08-15
																			// modify
																			// by
																			// wangzuochun
																			// MS00024
																			// 交易数据拆分
																			// QDV4.1赢时胜（上海）2009年4月20日24_A
						rs.getString("FInvestType"); // 获取投资类型 2009-08-15 add by
														// wangzuochun MS00024
														// 交易数据拆分
														// QDV4.1赢时胜（上海）2009年4月20日24_A
				if (hmEveStg.containsKey(sKey)) {
					secstorage = (SecurityStorageBean) hmEveStg.get(sKey);
					secstorage.setStrStorageDate(YssFun.formatDate(dDate,
							"yyyy-MM-dd"));
					secstorage.setStrBaseCuryRate(dBaseRate + "");
					secstorage.setStrPortCuryRate(dPortRate + "");
					secstorage.setInvestType(rs.getString("FInvestType")); // 设置投资类型
																			// 2009-08-15
																			// add
																			// by
																			// wangzuochun
																			// MS00024
																			// 交易数据拆分
																			// QDV4.1赢时胜（上海）2009年4月20日24_A
					secstorage.setStrStorageAmount(YssD.add(YssFun
							.toDouble(secstorage.getStrStorageAmount()), rs
							.getDouble("FTradeAmount"))
							+ "");
					
					cost = calcPurchaseCost(rs);
					
					secstorage.setStrStorageCost(YssD.add(YssFun
							.toDouble(secstorage.getStrStorageCost()), cost)
							+ "");
					secstorage.setStrMStorageCost(YssD.add(YssFun
							.toDouble(secstorage.getStrMStorageCost()), cost)
							+ "");
					secstorage.setStrVStorageCost(YssD.add(YssFun
							.toDouble(secstorage.getStrVStorageCost()), cost)
							+ "");

//					secstorage.setStrBaseCuryCost(YssD.add(YssFun
//							.toDouble(secstorage.getStrBaseCuryCost()), rs
//							.getDouble("FBaseCuryCost"))
//							+ "");
//					secstorage.setStrMBaseCuryCost(YssD.add(YssFun
//							.toDouble(secstorage.getStrMBaseCuryCost()), rs
//							.getDouble("FMBaseCuryCost"))
//							+ "");
//					secstorage.setStrVBaseCuryCost(YssD.add(YssFun
//							.toDouble(secstorage.getStrVBaseCuryCost()), rs
//							.getDouble("FVBaseCuryCost"))
//							+ "");
//
//					secstorage.setStrPortCuryCost(YssD.add(YssFun
//							.toDouble(secstorage.getStrPortCuryCost()), rs
//							.getDouble("FPortCuryCost"))
//							+ "");
//					secstorage.setStrMPortCuryCost(YssD.add(YssFun
//							.toDouble(secstorage.getStrMPortCuryCost()), rs
//							.getDouble("FMPortCuryCost"))
//							+ "");
//					secstorage.setStrVPortCuryCost(YssD.add(YssFun
//							.toDouble(secstorage.getStrVPortCuryCost()), rs
//							.getDouble("FVPortCuryCost"))
//							+ "");
					calcPurchaseBaseCost(secstorage, dBaseRate); //计算基础金额
	                calcPurchasePortCost(rs, dDate, secstorage, dBaseRate, dPortRate); //计算组合金额
					secstorage.setBailMoney(YssD.add(secstorage.getBailMoney(),
							rs.getDouble("FBailMoney")));

				} else {
					secstorage = new SecurityStorageBean();
					secstorage
							.setStrSecurityCode(rs.getString("FSecurityCode"));
					secstorage.setStrStorageDate(YssFun.formatDate(dDate,
							"yyyy-MM-dd"));
					// secstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
					secstorage.setStrPortCode(rs.getString("FPortCode"));
					secstorage.setStrCuryCode(rs.getString("FTradeCury"));
					if (bAnaly1) {
						secstorage.setStrFAnalysisCode1(rs
								.getString("FInvMgrCode"));
					} else {
						secstorage.setStrFAnalysisCode1(" ");
					}
					if (bAnaly2) {
						secstorage.setStrFAnalysisCode2(rs
								.getString("FBrokerCode"));
					} else {
						secstorage.setStrFAnalysisCode2(" ");
					}
					if (bAnaly3) {
						secstorage.setStrFAnalysisCode3(rs
								.getString("FAnalysisCode3"));
					} else {
						secstorage.setStrFAnalysisCode3(" ");
					}
					secstorage.setAttrCode(rs.getString("FAttrClsCode"));
					secstorage.setInvestType(rs.getString("FInvestType")); // 设置投资类型
																			// 2009-08-15
																			// add
																			// by
																			// wangzuochun
																			// MS00024
																			// 交易数据拆分
																			// QDV4.1赢时胜（上海）2009年4月20日24_A
					secstorage.setStrBaseCuryRate(dBaseRate + "");
					secstorage.setStrPortCuryRate(dPortRate + "");

					secstorage.setStrStorageAmount(rs.getDouble("FTradeAmount")
							+ "");
					secstorage.setStrStorageCost(rs.getDouble("FCost") + "");
					secstorage.setStrMStorageCost(rs.getDouble("FMCost") + "");
					secstorage.setStrVStorageCost(rs.getDouble("FVCost") + "");

					secstorage.setStrBaseCuryCost(rs.getDouble("FBaseCuryCost")
							+ "");
					secstorage.setStrMBaseCuryCost(rs
							.getDouble("FMBaseCuryCost")
							+ "");
					secstorage.setStrVBaseCuryCost(rs
							.getDouble("FVBaseCuryCost")
							+ "");

					secstorage.setStrPortCuryCost(rs.getDouble("FPortCuryCost")
							+ "");
					secstorage.setStrMPortCuryCost(rs
							.getDouble("FMPortCuryCost")
							+ "");
					secstorage.setStrVPortCuryCost(rs
							.getDouble("FVPortCuryCost")
							+ "");

					secstorage.setBailMoney(rs.getDouble("FBailMoney"));

					hmEveStg.put(sKey, secstorage);
				}
			}
		} catch (Exception e) {
			throw new YssException("在统计交易信息时出现异常!" + "\n", e); // 统计交易信息异常 by 曹丞
																// 2009.01.21
																// MS00004
																// QDV4.1-2009.2.1_09A

		} finally {
			dbl.closeResultSetFinal(rs);
		}
     }
    
    /**
     * 当回购的成本重新计算之后，将其更新到交易数据中去
     * @param security
     * @param rs
     * @throws YssException
     */
	private void updatePurchaseSubTradeCost(SecurityStorageBean security,
			ResultSet rs) throws YssException {
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pst = null;
		YssPreparedStatement pst = null;
        //=============end====================
		strSql = "update "
				+ pub.yssGetTableName("Tb_Data_SubTrade")
				+ " set FCost = ?,"
				+ " FMCost = ?, FVCost = ?, FBaseCuryCost = ?, FMBaseCuryCost = ?, FVBaseCuryCost = ?,"
				+ " FPortCuryCost = ?, FMPortCuryCost = ?, FVPortCuryCost = ? where FNum = ?";
		try {
			//modified by liubo.Story #2145
			//==============================
//			pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
			pst.setDouble(1, new Double(security.getStrStorageCost())
					.doubleValue());
			pst.setDouble(2, new Double(security.getStrMStorageCost())
					.doubleValue());
			pst.setDouble(3, new Double(security.getStrVStorageCost())
					.doubleValue());
			pst.setDouble(4, new Double(security.getStrBaseCuryCost())
					.doubleValue());
			pst.setDouble(5, new Double(security.getStrMBaseCuryCost())
					.doubleValue());
			pst.setDouble(6, new Double(security.getStrVBaseCuryCost())
					.doubleValue());
			pst.setDouble(7, new Double(security.getStrPortCuryCost())
					.doubleValue());
			pst.setDouble(8, new Double(security.getStrMPortCuryCost())
					.doubleValue());
			pst.setDouble(9, new Double(security.getStrVPortCuryCost())
					.doubleValue());
			pst.setString(10, rs.getString("FNum"));
			pst.executeUpdate();
		} catch (Exception e) {
			throw new YssException("在统计交易信息时出现异常!" + "\n", e);
		} finally {
			
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
	 * add by songjie 2011.10.10
	 * BUG 2701 QDV4泰达2011年09月13日01_B
	 * 若回购库存数量为零 则把成本清零
	 * @param security
	 * @throws YssException
	 */
	private void stgSecurityRE(SecurityStorageBean security) throws YssException{
		String strSql = "";
		ResultSet rs = null;
		try{
			strSql = " select FCatCode from " + pub.yssGetTableName("Tb_Para_security") + 
			" where FSecurityCode = " + dbl.sqlString(security.getStrSecurityCode());
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next())
			{
				if(rs.getString("FCatCode").equalsIgnoreCase("RE")
				&& Double.parseDouble(security.getStrStorageAmount())==0)
				{
					security.setStrBaseCuryCost("0");
					security.setStrMBaseCuryCost("0");
					security.setStrVBaseCuryCost("0");
					security.setStrPortCuryCost("0");
					security.setStrMPortCuryCost("0");
					security.setStrVPortCuryCost("0");
					security.setStrStorageCost("0");
					security.setStrMStorageCost("0");
					security.setStrVStorageCost("0");
				}
			}
		}catch(Exception e){
			throw new YssException("生成债券应收应付数据出错",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
    * 若当日卖出利息比当日的应收利息大且库存数量为0时，则添加一笔06的差额利息冲减卖出利息收入
    * by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
    * @param security
    * @throws YssException
    */
   private void stgSecurityFISecPay(SecurityStorageBean security) throws YssException{
	   SecRecPayAdmin secPayAdmin =new SecRecPayAdmin();
	   secPayAdmin.setYssPub(pub);
	   ResultSet rs =null;
	   String sql="";
	   String exitNum="";//QDV4中保2010年4月24日01_B by leeyu 20100425
	   try{
		   //QDV4中保2010年4月24日01_B by leeyu 20100425
		   sql=" select FNum from "+pub.yssGetTableName("Tb_Data_SecRecPay")+
				" where FNum in( select distinct FRelaNum from "+pub.yssGetTableName("Tb_Data_Integrated")+
				" where FNum in( select distinct FNum from "+pub.yssGetTableName("Tb_Data_Integrated")+
				" where FPortCode ="+dbl.sqlString(security.getStrPortCode())+
				" and FSecurityCode ="+dbl.sqlString(security.getStrSecurityCode())+
				" and FAnalysisCode1="+dbl.sqlString(security.getStrFAnalysisCode1().length()>0?security.getStrFAnalysisCode1():" ")+
				" and FAnalysisCode2="+dbl.sqlString(security.getStrFAnalysisCode2().length()>0?security.getStrFAnalysisCode2():" ")+
				" and FAttrClsCode="+dbl.sqlString(security.getAttrCode().length()>0?security.getAttrCode():" ")+
				(security.getInvestType()!=null&& security.getInvestType().trim().length()>0?" and FInvestType ="+dbl.sqlString(security.getInvestType()):"")+ //添加投资类型代码
				"  and FTradeTypeCode in('80','81') and FOperDate = "+dbl.sqlDate(security.getStrStorageDate())+
				" ) and FNumType='SecRecPay' )";
	       rs=dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
	       while(rs.next()){
	          exitNum+=rs.getString("FNum")+",";
	       }
	       if(exitNum==null ||exitNum.trim().length()==0)
	    	   return;
	       if(exitNum.endsWith(","))
	        exitNum =exitNum.substring(0,exitNum.length()-1);
	       dbl.closeResultSetFinal(rs);
	       secPayAdmin.insert("", YssFun.toDate(security.getStrStorageDate()), YssFun.toDate(security.getStrStorageDate()), "06", "06FI", security.getStrPortCode(), security.getStrFAnalysisCode1(), security.getStrFAnalysisCode2(), security.getStrSecurityCode(), security.getStrCuryCode(), -99, true, 0, true, exitNum,"","");//将综合业务中生成的应收应付数据的编号排除掉 by leeyu 20100425 QDV4中保2010年4月24日01_B
	       //QDV4中保2010年4月24日01_B by leeyu 20100425
		   sql="select sum(FBal)*(-1) as FBal,sum(FMBal)*(-1) as FMBal,sum(FVBal)*(-1) as FVBal,FSecurityCode,FPortCode,FStorageDate,FAnalysisCode1,FAnalySisCode2,FAnalySisCode3,FInvestType,"+//添加投资类型
		   		" FTsfTypeCode,FSubTsfTypeCode,FCatType,FAttrClsCode,FCuryCode from( "+
		   		" select FBal,FMBal,FVBal,FSecurityCode,FPortCode,FStorageDate+1 as FStorageDate,FAnalysisCode1,FAnalySisCode2,FAnalySisCode3,FInvestType,"+//调整为加一天，因为是前日库存数据 by leeyu
		   		" FTsfTypeCode,FSubTsfTypeCode,FCatType,FAttrClsCode,FCuryCode,'storage' as FType From "+pub.yssGetTableName("Tb_stock_secrecpay")+
		   		" where FcheckState=1 and FSecurityCode="+dbl.sqlString(security.getStrSecurityCode())+
		   		" and "+operSql.sqlStoragEve(YssFun.toDate(security.getStrStorageDate()))+
		   		" and FTsfTypeCode='06' and FSubTsfTypeCode='06FI' " +
		   		" and FAnalySisCode1="+dbl.sqlString(security.getStrFAnalysisCode1().length()>0?security.getStrFAnalysisCode1():" ") +
		   		" and FAnalySisCode2="+dbl.sqlString(security.getStrFAnalysisCode2().length()>0?security.getStrFAnalysisCode2():" ") +
		   		" and FAnalySisCode3="+dbl.sqlString(security.getStrFAnalysisCode3().length()>0?security.getStrFAnalysisCode3():" ") +
		   		" and FPortCode="+dbl.sqlString(security.getStrPortCode()) +
		   		" and FCatType="+dbl.sqlString(security.getCatType().length()>0?security.getCatType():" ") +
		   		" and FAttrClsCode="+dbl.sqlString(security.getAttrCode().length()>0?security.getAttrCode():" ") +
		   		(security.getInvestType()!=null&& security.getInvestType().trim().length()>0?" and FInvestType ="+dbl.sqlString(security.getInvestType()):"")+ //添加投资类型代码
		   		//panjunfang modify 20101223   QDV4太平2010年12月22日01_B----
		   		" and exists (select '1' from " + pub.yssGetTableName("Tb_Stock_Security") +
		   		" where FcheckState=1 and FSecurityCode="+dbl.sqlString(security.getStrSecurityCode())+
		   		" and "+operSql.sqlStoragEve(YssFun.toDate(security.getStrStorageDate()))+
		   		" and FAnalySisCode1="+dbl.sqlString(security.getStrFAnalysisCode1().length()>0?security.getStrFAnalysisCode1():" ") +
		   		" and FAnalySisCode2="+dbl.sqlString(security.getStrFAnalysisCode2().length()>0?security.getStrFAnalysisCode2():" ") +
		   		" and FAnalySisCode3="+dbl.sqlString(security.getStrFAnalysisCode3().length()>0?security.getStrFAnalysisCode3():" ") +
		   		" and FPortCode="+dbl.sqlString(security.getStrPortCode()) +
		   		" and FCatType="+dbl.sqlString(security.getCatType().length()>0?security.getCatType():" ") +
		   		" and FAttrClsCode="+dbl.sqlString(security.getAttrCode().length()>0?security.getAttrCode():" ") + 
		   		" and FStorageAmount <> 0)" +
		   		(security.getInvestType()!=null&& security.getInvestType().trim().length()>0?" and FInvestType ="+dbl.sqlString(security.getInvestType()):"")+ //添加投资类型代码
		   		//-----------end 20101223 ---------------
		   		" union all " +//并行优化,改为union all,原因是上下代码已经采用FType区分过 by leeyu 20100604
		   		" select -FMoney*FInOut as FBal,-FMMoney*FInOut as FMBal,-FVMoney*FInOut as FVBal,FSecurityCode,FPortCode,FTransDate as FStorageDate,FAnalysisCode1,FAnalySisCode2,FAnalySisCode3,FInvestType, "+//添加投资类型
		   		" '06' as FTsfTypeCode,'06FI' as FSubTsfTypeCode,FCatType,FAttrClsCode,FCuryCode,'02FI' as FType from "+pub.yssGetTableName("Tb_Data_Secrecpay")+
		   		" where FcheckState=1 and FSecurityCode="+dbl.sqlString(security.getStrSecurityCode())+
		   		" and FTransDate="+dbl.sqlDate(security.getStrStorageDate())+
		   		" and FTsfTypeCode='02' and FSubTsfTypeCode='02FI_B' "+
		   		" and FAnalySisCode1="+dbl.sqlString(security.getStrFAnalysisCode1().length()>0?security.getStrFAnalysisCode1():" ") +
		   		" and FAnalySisCode2="+dbl.sqlString(security.getStrFAnalysisCode2().length()>0?security.getStrFAnalysisCode2():" ") +
		   		" and FAnalySisCode3="+dbl.sqlString(security.getStrFAnalysisCode3().length()>0?security.getStrFAnalysisCode3():" ") +
		   		" and FPortCode="+dbl.sqlString(security.getStrPortCode()) +
		   		" and FCatType="+dbl.sqlString(security.getCatType().length()>0?security.getCatType():" ") +
		   		" and FAttrClsCode="+dbl.sqlString(security.getAttrCode().length()>0?security.getAttrCode():" ") +
		   		(security.getInvestType()!=null&& security.getInvestType().trim().length()>0?" and FInvestType ="+dbl.sqlString(security.getInvestType()):"")+ //添加投资类型代码
		   		//panjunfang add 20101209  加上当天买入的债券利息  QDV4太平2010年12月09日01_B
		   		" union all " +//并行优化,改为union all,原因是上下代码已经采用FType区分过 by leeyu 20100604
		   		" select FMoney*FInOut as FBal,FMMoney*FInOut as FMBal,FVMoney*FInOut as FVBal,FSecurityCode,FPortCode,FTransDate as FStorageDate,FAnalysisCode1,FAnalySisCode2,FAnalySisCode3,FInvestType, "+//添加投资类型
		   		" FTsfTypeCode,'06FI' as FSubTsfTypeCode,FCatType,FAttrClsCode,FCuryCode,'06' as FType from "+pub.yssGetTableName("Tb_Data_Secrecpay")+
		   		" where FcheckState=1 and FSecurityCode="+dbl.sqlString(security.getStrSecurityCode())+
		   		" and FTransDate="+dbl.sqlDate(security.getStrStorageDate())+
		   		" and FTsfTypeCode='06' and FSubTsfTypeCode='06FI_B' "+
		   		" and FAnalySisCode1="+dbl.sqlString(security.getStrFAnalysisCode1().length()>0?security.getStrFAnalysisCode1():" ") +
		   		" and FAnalySisCode2="+dbl.sqlString(security.getStrFAnalysisCode2().length()>0?security.getStrFAnalysisCode2():" ") +
		   		" and FAnalySisCode3="+dbl.sqlString(security.getStrFAnalysisCode3().length()>0?security.getStrFAnalysisCode3():" ") +
		   		" and FPortCode="+dbl.sqlString(security.getStrPortCode()) +
		   		" and FCatType="+dbl.sqlString(security.getCatType().length()>0?security.getCatType():" ") +
		   		" and FAttrClsCode="+dbl.sqlString(security.getAttrCode().length()>0?security.getAttrCode():" ") +
		   		(security.getInvestType()!=null&& security.getInvestType().trim().length()>0?" and FInvestType ="+dbl.sqlString(security.getInvestType()):"")+ //添加投资类型代码		   		
		   		//---------------------20101209 End----------------------
		   		" union all "+ //并行优化,改为union all,原因是上下代码已经采用FType区分过 by leeyu 20100604
		   		" select FMoney*FInOut as FBal,FMMoney*FInOut as FMBal,FVMoney*FInOut as FVBal,FSecurityCode,FPortCode,FTransDate as FStorageDate,FAnalysisCode1,FAnalySisCode2,FAnalySisCode3,FInvestType,"+//添加投资类型
		   		" FTsfTypeCode,FSubTsfTypeCode,FCatType,FAttrClsCode,FCuryCode,'06' as FType from "+pub.yssGetTableName("Tb_Data_secrecpay")+
		   		" where FcheckState=1 and FSecurityCode="+dbl.sqlString(security.getStrSecurityCode())+
		   		" and FTransDate="+dbl.sqlDate(security.getStrStorageDate())+
		   		" and FTsfTypeCode='06' and FSubTsfTypeCode='06FI' "+
		   		" and FAnalySisCode1="+dbl.sqlString(security.getStrFAnalysisCode1().length()>0?security.getStrFAnalysisCode1():" ") +
		   		" and FAnalySisCode2="+dbl.sqlString(security.getStrFAnalysisCode2().length()>0?security.getStrFAnalysisCode2():" ") +
		   		" and FAnalySisCode3="+dbl.sqlString(security.getStrFAnalysisCode3().length()>0?security.getStrFAnalysisCode3():" ") +
		   		" and FPortCode="+dbl.sqlString(security.getStrPortCode()) +
		   		" and FCatType="+dbl.sqlString(security.getCatType().length()>0?security.getCatType():" ") +
		   		" and FAttrClsCode="+dbl.sqlString(security.getAttrCode().length()>0?security.getAttrCode():" ") +
		   		(security.getInvestType()!=null&& security.getInvestType().trim().length()>0?" and FInvestType ="+dbl.sqlString(security.getInvestType()):"")+ //添加投资类型代码
		   		" ) where exists(select '1' from "+pub.yssGetTableName("Tb_Data_SecrecPay")+
		   		" where FcheckState=1 and FSecurityCode="+dbl.sqlString(security.getStrSecurityCode())+
		   		" and FTransDate="+dbl.sqlDate(security.getStrStorageDate())+
		   		" and FTsfTypeCode='02' and FSubTsfTypeCode='02FI_B' "+
		   		" and FAnalySisCode1="+dbl.sqlString(security.getStrFAnalysisCode1().length()>0?security.getStrFAnalysisCode1():" ") +
		   		" and FAnalySisCode2="+dbl.sqlString(security.getStrFAnalysisCode2().length()>0?security.getStrFAnalysisCode2():" ") +
		   		" and FAnalySisCode3="+dbl.sqlString(security.getStrFAnalysisCode3().length()>0?security.getStrFAnalysisCode3():" ") +
		   		" and FPortCode="+dbl.sqlString(security.getStrPortCode()) +
		   		" and FCatType="+dbl.sqlString(security.getCatType().length()>0?security.getCatType():" ") +
		   		" and FAttrClsCode="+dbl.sqlString(security.getAttrCode().length()>0?security.getAttrCode():" ") +
		   		(security.getInvestType()!=null&& security.getInvestType().trim().length()>0?" and FInvestType ="+dbl.sqlString(security.getInvestType()):"")+ //添加投资类型代码
		   		") group by FSecurityCode,FPortCode,FStorageDate,FAnalysisCode1,FAnalySisCode2,FAnalySisCode3,FTsfTypeCode,FSubTsfTypeCode,FCatType,FAttrClsCode,FCuryCode,FInvestType ";//添加投资类型
		   rs =dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
		   while(rs.next()){
			   SecPecPayBean secpecpay = new SecPecPayBean();
			   secPayAdmin.addList(secpecpay);
			   secpecpay.setTransDate(YssFun.toDate(security.getStrStorageDate()));
			      secpecpay.setStrPortCode(rs.getString("FPortCode"));
			      secpecpay.setInvMgrCode(security.getStrFAnalysisCode1().length()>0?security.getStrFAnalysisCode1():" ");
			      secpecpay.setBrokerCode(security.getStrFAnalysisCode2().length()>0?security.getStrFAnalysisCode2():" ");
			      secpecpay.setStrSecurityCode(rs.getString("FSecurityCode"));
			        secpecpay.setAttrClsCode(security.getAttrCode().length()>0?security.getAttrCode():" ");
			      secpecpay.setStrCuryCode(rs.getString("FCuryCode"));
			      secpecpay.setMoney(rs.getDouble("FBal"));
			      secpecpay.setMMoney(rs.getDouble("FMBal"));
			      secpecpay.setVMoney(rs.getDouble("FVBal"));
			      secpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
			    		  rs.getDouble("FBal"), YssFun.toDouble(security.getStrBaseCuryRate())));
			      secpecpay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
			    		  rs.getDouble("FMBal"), YssFun.toDouble(security.getStrBaseCuryRate())));
			      secpecpay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
			    		  rs.getDouble("FVBal"), YssFun.toDouble(security.getStrBaseCuryRate())));
			      secpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
			    		  rs.getDouble("FBal"), YssFun.toDouble(security.getStrBaseCuryRate()), YssFun.toDouble(security.getStrPortCuryRate()),
			    		  rs.getString("FCuryCode"), YssFun.toDate(security.getStrStorageDate()), rs.getString("FPortCode")));
			      secpecpay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
			    		  rs.getDouble("FMBal"), YssFun.toDouble(security.getStrBaseCuryRate()), YssFun.toDouble(security.getStrPortCuryRate()),
			    		  rs.getString("FCuryCode"), YssFun.toDate(security.getStrStorageDate()), rs.getString("FPortCode")));
			      secpecpay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
			    		  rs.getDouble("FVBal"), YssFun.toDouble(security.getStrBaseCuryRate()), YssFun.toDouble(security.getStrPortCuryRate()),
			    		  rs.getString("FCuryCode"), YssFun.toDate(security.getStrStorageDate()), rs.getString("FPortCode")));
			         secpecpay.setStrTsfTypeCode("06"); 
			         secpecpay.setStrSubTsfTypeCode("06FI");
			         secpecpay.setInvestType(rs.getString("FInvestType"));//添加投资类型
			         secpecpay.setBaseCuryRate(YssFun.toDouble(security.getStrBaseCuryRate()));
			         secpecpay.setPortCuryRate(YssFun.toDouble(security.getStrPortCuryRate()));
			         secpecpay.checkStateId = 1;
		   }
		   secPayAdmin.insert("", YssFun.toDate(security.getStrStorageDate()), YssFun.toDate(security.getStrStorageDate()), "06", "06FI", security.getStrPortCode(), security.getStrFAnalysisCode1(), security.getStrFAnalysisCode2(), security.getStrSecurityCode(), security.getStrCuryCode(), -99, true, 0, true, exitNum,"","");//将综合业务中生成的应收应付数据的编号排除掉 by leeyu 20100425 QDV4中保2010年4月24日01_B
		   //insert(YssFun.toDate(security.getStrStorageDate()), YssFun.toDate(security.getStrStorageDate()), "06", "06FI", security.getStrPortCode(), security.getStrFAnalysisCode1(), security.getStrFAnalysisCode1(), security.getStrSecurityCode(), security.getStrCuryCode(), -99);
	   }catch(Exception ex){
		   throw new YssException("生成债券应收应付数据出错",ex);
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
   }
   
   //先入先出重新计算交易成本  add by yanghaiming 20101209 QDV4赢时胜（深圳）2010年11月30日02_A
   public void refreshTradeCost1(java.util.Date dDate, String sPortCode) throws
           YssException, SQLException {
	   String strSql = "";
	   ResultSet rs = null;
	   boolean fAnalysisCode1 = false;
	   boolean fAnalysisCode2 = false;
	   HashMap secStockDetailMap = new HashMap(4096);;
	   try{
		  strSql = " select * from " + pub.yssGetTableName("Tb_Para_StorageCfg") +
  			       " where FCHECKSTATE = 1 and FSTORAGETYPE = 'Security'";
		  rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
		  if(rs.next()){
			 if(rs.getString("FANALYSISCODE1") != null && rs.getString("FANALYSISCODE1").length() > 0){
				 fAnalysisCode1 = true;
			 }
			 if(rs.getString("FANALYSISCODE2") != null && rs.getString("FANALYSISCODE2").length() > 0){
				 fAnalysisCode2 = true;
			 }
		  }
		  // modify by zhangjun 2011-11-30 STORY #1773 资本利得税计算逻辑需要修改
		  //insertSecurityDetail(dDate, sPortCode,fAnalysisCode1,fAnalysisCode2);//插入当日买入的交易数据至库存明细表
		  //----end----//		  
		  secStockDetailMap = getStockDetail(dDate, sPortCode);//获取明细库存hash表		  
		  updateStockDetailMap(dDate, sPortCode,secStockDetailMap);//送股和买入数量更新HashMap  add by zhangjun 2011-11-30  STORY #1773 资本利得税计算逻辑需要修改
		  updateSubTrade(dDate, sPortCode,secStockDetailMap);//更新交易成本（卖出）		     
		  //updateSubtradeForSecLend(dDate, sPortCode);//证券借贷卖空交易数据的‘卖出’成本更新  modify by fangjiang 2011.11.23 story 1433
		  insertStockDetail(dDate, sPortCode,secStockDetailMap,fAnalysisCode1,fAnalysisCode2);//插入明细库存
	   }catch (Exception e) {
           throw new YssException("先入先出重新计算成本出错！", e);
       } finally {
    	   dbl.closeResultSetFinal(rs);
       }
   }
   /**证券借贷卖空交易 卖出类型交易数据成本更新
    * @param dDate
    * @param sPortCode
 * @throws YssException 
 * @throws SQLException 
    */
   private void updateSubtradeForSecLend(Date dDate, String sPortCode) throws YssException, SQLException {
		//modified by liubo.Story #2145
       //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
       //=================================
//	   PreparedStatement pst = null;
	   YssPreparedStatement pst = null;
       //=============end====================

	   String strSql="";
	   Connection conn = dbl.loadConnection();
	   ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
       "avgcostcalculate");
	   YssCost cost = null;
	   boolean isFourDigit=false;
	   CtlPubPara pubpara=null;
	   ResultSet rs=null;
	   try{
		   cost=new YssCost();
		   pubpara = new CtlPubPara();
	       pubpara.setYssPub(pub);
	       String verTp = pubpara.getNavType();
		   if(verTp!=null && verTp.trim().equalsIgnoreCase("new")){
	      		isFourDigit=false;//国内QDII统计模式
	      	}else{
	      		isFourDigit=true;//太平资产统计模式
	      	}
		   
		   strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
	       " set FCost = ?," +
	       " FMCost = ?, FVCost = ?, FBaseCuryCost = ?, FMBaseCuryCost = ?, FVBaseCuryCost = ?," +
	       " FPortCuryCost = ?, FMPortCuryCost = ?, FVPortCuryCost = ? where FNum = ?";
			//modified by liubo.Story #2145
			//==============================
//		   pst = conn.prepareStatement(strSql);
		   pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
		   strSql="select * from "+pub.yssGetTableName("Tb_Data_SubTrade")+" a where a.fbargaindate="+dbl.sqlDate(dDate)
		   			+" and a.ftradetypecode='02' and a.fnum in (select frelanum from "+pub.yssGetTableName("tb_data_seclendtrade")
		   			+")";
		   rs=dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
		   while(rs.next()){
			    double dCost=YssD.round(YssD.mul(rs.getDouble("ftradeamount"), rs.getDouble("ftradeprice")),2);
			    double dBaseCost=YssD.round(YssD.mul(dCost, rs.getDouble("ffactbaserate")),2);
			    double dPortCost=YssD.round(YssD.div(dBaseCost, rs.getDouble("ffactportrate")),2);
			    cost.setCost(dCost);
			    cost.setMCost(dCost);
			    cost.setVCost(dCost);
			    cost.setBaseCost(dBaseCost);
			    cost.setBaseMCost(dBaseCost);
			    cost.setBaseVCost(dBaseCost);
			    cost.setPortCost(dPortCost);
			    cost.setPortMCost(dPortCost);
			    cost.setPortVCost(dPortCost);
			    
			   /* costCal.setYssPub(pub);
				costCal.roundCost(cost, isFourDigit ? 4 : 2);*/
			    
				pst.setDouble(1, cost.getCost());
				pst.setDouble(2, cost.getMCost());
				pst.setDouble(3, cost.getVCost());
				pst.setDouble(4, cost.getBaseCost());
				pst.setDouble(5, cost.getBaseMCost());
				pst.setDouble(6, cost.getBaseVCost());
				pst.setDouble(7, cost.getPortCost());
				pst.setDouble(8, cost.getPortMCost());
				pst.setDouble(9, cost.getPortVCost());
				pst.setString(10, rs.getString("FNum"));
				pst.executeUpdate();
		   }
		}catch(YssException e){
		   throw new YssException("卖空交易卖出成本计算错误!");
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
	   conn.setAutoCommit(false);
       conn.commit();
       conn.setAutoCommit(true);
	
}

//插入当日买入的交易数据至库存明细表 
   //add by yanghaiming 20101206 QDV4赢时胜（深圳）2010年11月30日02_A
   public void insertSecurityDetail(java.util.Date dDate, String sPortCode, boolean fAnalysisCode1, boolean fAnalysisCode2) throws
   			YssException, SQLException {
	   String strSql = "";
	   ResultSet rs = null;
	   boolean bTrans = false; //代表是否开始了事务
		//modified by liubo.Story #2145
       //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
       //=================================
//	   PreparedStatement pst = null;
	   YssPreparedStatement pst = null;
       //=============end====================

	   Connection conn = dbl.loadConnection();
	   
	 //add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 start 
		int count = 1;
	   try{
		   bTrans = true;
		   
		   strSql = "delete from " + pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") +
		   			" where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FBARGAINDATE = " + dbl.sqlDate(dDate);
		   dbl.executeSql(strSql);
		   strSql = "insert into " + pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") +
		   			" (FNUM,FSECURITYCODE,FBARGAINDATE,FPORTCODE,FINVMGRCODE,FBROKERCODE,FATTRCLSCODE," +
		   			"FINVESTTYPE,FTRADEAMOUNT,FCOST,FMCOST,FVCOST,FBASECURYCOST,FMBASECURYCOST,FVBASECURYCOST," +
		   			"FPORTCURYCOST,FMPORTCURYCOST,FVPORTCURYCOST) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//		   pst = conn.prepareStatement(strSql);
		   pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
		   strSql = "select * from " + pub.yssGetTableName("TB_DATA_SUBTRADE") + 
		   			" where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(sPortCode) +
		   			" and FBARGAINDATE = " + dbl.sqlDate(dDate) + " and FTRADETYPECODE in ('01','07')"; //modify by fangjiang 2011.05.18 STORY #845
		   rs = dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE); //modify by fangjiang 2011.08.14 STORY #788
		   while (rs.next()){
			   pst.setString(1, rs.getString("FNUM"));
			   pst.setString(2, rs.getString("FSECURITYCODE"));
			   pst.setDate(3, rs.getDate("FBARGAINDATE"));
			   pst.setString(4, rs.getString("FPORTCODE"));
			   pst.setString(5, fAnalysisCode1 ? rs.getString("FINVMGRCODE") : " ");
			   pst.setString(6, fAnalysisCode2 ? rs.getString("FBROKERCODE") : " ");
			   pst.setString(7, rs.getString("FATTRCLSCODE"));
			   pst.setString(8, rs.getString("FINVESTTYPE"));
			   pst.setDouble(9, rs.getDouble("FTRADEAMOUNT"));
			   pst.setDouble(10, rs.getDouble("FCOST"));
			   pst.setDouble(11, rs.getDouble("FMCOST"));
			   pst.setDouble(12, rs.getDouble("FVCOST"));
			   pst.setDouble(13, rs.getDouble("FBASECURYCOST"));
			   pst.setDouble(14, rs.getDouble("FMBASECURYCOST"));
			   pst.setDouble(15, rs.getDouble("FVBASECURYCOST"));
			   pst.setDouble(16, rs.getDouble("FPORTCURYCOST"));
			   pst.setDouble(17, rs.getDouble("FMPORTCURYCOST"));
			   pst.setDouble(18, rs.getDouble("FVPORTCURYCOST"));
			   pst.addBatch();
			   
			   
			 //add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 start 
			
				if(count==500){
					pst.executeBatch();
					count = 1;
					continue;
				}
				

					count++;
				//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 end
			   
		   }
		   pst.executeBatch();
		   conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
	   }catch (Exception e) {
           throw new YssException("插入当日买入明细库存出错！", e);
       } finally {
    	   dbl.closeResultSetFinal(rs);
    	   dbl.closeStatementFinal(pst);
           dbl.endTransFinal(conn, bTrans);
       }
   }
   
   //获取明细库存证券代码作为Key值   add by yanghaiming 20101207 QDV4赢时胜（深圳）2010年11月30日02_A
   public HashMap getStockDetail(java.util.Date dDate, String sPortCode) throws
		YssException, SQLException {
	   HashMap tempHash = new HashMap(4096);
	   ArrayList tempAry = new ArrayList();
	   String strSql = "";
	   ResultSet rs = null;
	   String key = "";
	   try{
		   //modify by zhangjun 2011-12-01   STORY #1773 资本利得税计算逻辑需要修改
		   /*strSql = "select * from (select * from " + pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") + " where FBARGAINDATE = " +
		   			dbl.sqlDate(dDate) + " and FPORTCODE = " + dbl.sqlString(sPortCode) + " union all (select * from " +
		   			pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") + " where FBARGAINDATE = (select max(FBARGAINDATE) from " + 
		   			pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") + " where FBARGAINDATE < " + dbl.sqlDate(dDate) + 
		   			" and FPORTCODE = " + dbl.sqlString(sPortCode) + ") and FTRADEAMOUNT > 0)) order by FSECURITYCODE,FNUM";*///根据证券代码，编号排序后就无需频繁操作hashmap了
		   //modify by fangjiang story 845 2011.05.21
		   strSql = "select * from " + pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") + 
		   			" where FBARGAINDATE = (select max(FBARGAINDATE) from " + 
  			        pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") + " where FBARGAINDATE < " + dbl.sqlDate(dDate) + 
  			        " and FPORTCODE = " + dbl.sqlString(sPortCode) + ") and FTRADEAMOUNT > 0 order by FPortCode, FSECURITYCODE, FNUM";
		   rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
		   while (rs.next()){	
			   //bug 4558 by zhouwei 20120518
			   key=rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE");
			   if(tempHash.containsKey(key)){
				   tempAry=(ArrayList) tempHash.get(key);
			   }else{
				   tempAry = new ArrayList();
			   }
//			   if(!key.equalsIgnoreCase(rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE"))){//如果证券代码为初始值或者证券代码与之前一条数据的证券代码不一致
//				   tempHash.put(rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE"), tempAry);
//				   tempAry = new ArrayList();
//			   }
			   SecStockDetail secStockDetail = new SecStockDetail();			   
			   secStockDetail.setfNum(rs.getString("FNUM"));
			   secStockDetail.setfSecurityCode(rs.getString("FSECURITYCODE"));
			   secStockDetail.setfBargainDate(rs.getDate("FBARGAINDATE"));
			   secStockDetail.setStrPortCode(rs.getString("FPORTCODE"));
			   secStockDetail.setInvMgrCodeName(rs.getString("FINVMGRCODE"));
			   secStockDetail.setBrokerCode(rs.getString("FBROKERCODE"));
			   secStockDetail.setAttrClsCode(rs.getString("FATTRCLSCODE"));
			   secStockDetail.setInvestType(rs.getString("FINVESTTYPE"));
			   secStockDetail.setAmount(rs.getDouble("FTRADEAMOUNT"));
			   secStockDetail.setCost(rs.getDouble("FCOST"));
			   secStockDetail.setmCost(rs.getDouble("FMCOST"));
			   secStockDetail.setvCost(rs.getDouble("FVCOST"));
			   secStockDetail.setBaseCost(rs.getDouble("FBASECURYCOST"));
			   secStockDetail.setBaseMCost(rs.getDouble("FMBASECURYCOST"));
			   secStockDetail.setBaseVCost(rs.getDouble("FVBASECURYCOST"));
			   secStockDetail.setPortCost(rs.getDouble("FPORTCURYCOST"));
			   secStockDetail.setPortMCost(rs.getDouble("FMPORTCURYCOST"));
			   secStockDetail.setPortVCost(rs.getDouble("FVPORTCURYCOST"));
			   tempAry.add(secStockDetail);
			   //key = rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE");
			   tempHash.put(key, tempAry);
		   }
		   //由于最后一只证券的集合不会被添加到hashmap中，因此在此处添加
//		   tempHash.put(key, tempAry);
		   return tempHash;
	   }catch (Exception e) {
           throw new YssException("获取明细库存出错！", e);
       } finally {
    	   dbl.closeResultSetFinal(rs);
       }
   }

   //add  by  zhangjun  2011-11-30  STORY #1773 资本利得税计算逻辑需要修改
   //送股和买入数量更新HashMap
   public void updateStockDetailMap(java.util.Date dDate, String sPortCode, HashMap tempHash) throws
   		YssException, SQLException {	         
       String strSql = "";
       ResultSet rs = null;  
       ResultSet rSet = null;
       ArrayList secAry = null;     
       ArrayList tempAry = null;
       double tAmount = 0;//存放送股数量    
	   double sumAmount = 0;//用于存放同一支证券总的借入数量
	   double pAmount = 0;//存放每次借入应得的送股数量
	   double tmpAmount = 0;
	   String key = "";   
       try{
    	   SecStockDetail secStockDetail = null;
    	   //送股数量更新HashMap
    	   strSql = " select * from " + pub.yssGetTableName("TB_DATA_SUBTRADE") + 
  				    " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(sPortCode) +
  			        " and FBARGAINDATE = " + dbl.sqlDate(dDate) + " and FTRADETYPECODE in ('07') order by FPortCode, FSecurityCode, FNum "; 
    	   rs = dbl.queryByPreparedStatement(strSql); 
    	   while(rs.next()){ 
    		   //bug 4558 add by zhouwei 20120518 送股记录添加到集合中
//    		   key=rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE");
//    		   if(tempHash.containsKey(key)){
//    			   tempAry=(ArrayList) tempHash.get(key);   			   
//    		   }else{
//    			   tempAry=new ArrayList();
//    		   }
//    		   secStockDetail = new SecStockDetail();			   
//			   secStockDetail.setfNum(rs.getString("FNUM"));
//			   secStockDetail.setfSecurityCode(rs.getString("FSECURITYCODE"));
//			   secStockDetail.setfBargainDate(rs.getDate("FBARGAINDATE"));
//			   secStockDetail.setStrPortCode(rs.getString("FPORTCODE"));
//			   secStockDetail.setInvMgrCodeName(rs.getString("FINVMGRCODE"));
//			   secStockDetail.setBrokerCode(rs.getString("FBROKERCODE"));
//			   secStockDetail.setAttrClsCode(rs.getString("FATTRCLSCODE"));
//			   secStockDetail.setInvestType(rs.getString("FINVESTTYPE"));
//			   secStockDetail.setAmount(rs.getDouble("FTRADEAMOUNT"));
//			   secStockDetail.setCost(rs.getDouble("FCOST"));
//			   secStockDetail.setmCost(rs.getDouble("FMCOST"));
//			   secStockDetail.setvCost(rs.getDouble("FVCOST"));
//			   secStockDetail.setBaseCost(rs.getDouble("FBASECURYCOST"));
//			   secStockDetail.setBaseMCost(rs.getDouble("FMBASECURYCOST"));
//			   secStockDetail.setBaseVCost(rs.getDouble("FVBASECURYCOST"));
//			   secStockDetail.setPortCost(rs.getDouble("FPORTCURYCOST"));
//			   secStockDetail.setPortMCost(rs.getDouble("FMPORTCURYCOST"));
//			   secStockDetail.setPortVCost(rs.getDouble("FVPORTCURYCOST"));
//			   tempAry.add(secStockDetail);
//			   tempHash.put(key, tempAry);
//    	   }
	    		secAry = (ArrayList)tempHash.get(rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE"));  		
	    		if(secAry !=null){	    			
    				tAmount = rs.getDouble("FTRADEAMOUNT");
    				for(int i = 0; i<secAry.size(); i++){
	    				secStockDetail = (SecStockDetail)secAry.get(i);
		    			sumAmount = YssD.add(sumAmount, secStockDetail.getAmount());
		    		}
		    		if(sumAmount == 0 ){
		    			break;
		    		}
		    		for(int i = 0; i < secAry.size(); i++ ){
		    			secStockDetail = (SecStockDetail)secAry.get(i);
		    			if(i < secAry.size() - 1){
			    			pAmount =  YssD.round
			    			           (
		    			        		   YssD.mul
		    			        		   (
	    			        				   YssD.div
	    			        				   (
				        						   secStockDetail.getAmount(), 
				        						   sumAmount
			        						   ), 
			        						   tAmount
				        				   ), 
	    			        		       2
				        		       );
			    			tmpAmount = YssD.add(tmpAmount, pAmount);
		    			}else{
		    				pAmount = YssD.sub(tAmount, tmpAmount);
		    			}
		    			secStockDetail.setAmount(YssD.add(secStockDetail.getAmount(), pAmount));
		    		}
		    		tmpAmount = 0;
		    		sumAmount = 0;		
	    		}	    				
	    	}
    	      
    	    strSql = " select * from " + pub.yssGetTableName("TB_DATA_SUBTRADE") + 
			         " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(sPortCode) +
		             " and FBARGAINDATE = " + dbl.sqlDate(dDate) + " and FTRADETYPECODE in ('01') order by FPortCode, FSecurityCode, FNum "; 
    	    rSet = dbl.queryByPreparedStatement(strSql); 
	    	while(rSet.next()){	    		 
	    		 //bug 4558 add by zhouwei 20120518 买入记录添加到集合中
    		    key=rSet.getString("FPortCode") + "\f" + rSet.getString("FSECURITYCODE");
    		    if(tempHash.containsKey(key)){
    			   tempAry=(ArrayList) tempHash.get(key);   			   
    		    }else{
    			   tempAry=new ArrayList();
    		    }
	    		secStockDetail = new SecStockDetail(); 
	    		secStockDetail.setfNum(rSet.getString("FNUM"));
				secStockDetail.setfSecurityCode(rSet.getString("FSECURITYCODE"));
				secStockDetail.setfBargainDate(rSet.getDate("FBARGAINDATE"));  //库存日期
				secStockDetail.setStrPortCode(rSet.getString("FPORTCODE"));
				secStockDetail.setInvMgrCodeName(rSet.getString("FINVMGRCODE")); //投资经理名称
				secStockDetail.setBrokerCode(rSet.getString("FBROKERCODE"));
				secStockDetail.setAttrClsCode(rSet.getString("FATTRCLSCODE")); //所属分类 
				secStockDetail.setInvestType(rSet.getString("FINVESTTYPE"));  //投资类型
				secStockDetail.setAmount(rSet.getDouble("FTRADEAMOUNT"));  //数量
				secStockDetail.setCost(rSet.getDouble("FCOST"));   //原币核算成本
				secStockDetail.setmCost(rSet.getDouble("FMCOST"));
				secStockDetail.setvCost(rSet.getDouble("FVCOST"));
				secStockDetail.setBaseCost(rSet.getDouble("FBASECURYCOST")); //基础货币核算成本
				secStockDetail.setBaseMCost(rSet.getDouble("FMBASECURYCOST"));
				secStockDetail.setBaseVCost(rSet.getDouble("FVBASECURYCOST"));
				secStockDetail.setPortCost(rSet.getDouble("FPORTCURYCOST"));  //组合货币核算成本
				secStockDetail.setPortMCost(rSet.getDouble("FMPORTCURYCOST"));
				secStockDetail.setPortVCost(rSet.getDouble("FVPORTCURYCOST"));
				tempAry.add(secStockDetail);
				tempHash.put(key, tempAry);
//	    		key = rSet.getString("FPortCode") + "\f" + rSet.getString("FSECURITYCODE");
//	    		
//	    		if(tempHash.containsKey(key)){
//	    			((ArrayList)tempHash.get(key)).add(secStockDetail);
//	    		}else{
//	    			tempAry.add(secStockDetail);
//	    			tempHash.put(key, tempAry);
//	    		}
    		}     		    		   
       } catch (Exception e) {
    	   throw new YssException("送股和买入数据更新HashMap时出现异常！", e);
       } finally {
    	   dbl.closeResultSetFinal(rs);
    	   dbl.closeResultSetFinal(rSet);
       }   
   }
   
   
   //更新交易数据卖出成本   add by yanghaiming 20101209 QDV4赢时胜（深圳）2010年11月30日02_A
   public void updateSubTrade(java.util.Date dDate, String sPortCode, HashMap tempHash) throws
		YssException, SQLException {
	   Connection conn = dbl.loadConnection();
       boolean bTrans = false; //代表是否开始了事务
   	   //modified by liubo.Story #2145
       //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
       //=================================	
//       PreparedStatement pst = null;
       YssPreparedStatement pst = null;
       //=============end====================
       String strSql = "";
       ResultSet rs = null;
       YssCost cost = null;
       CtlPubPara pubpara = null;
       boolean isFourDigit = false;
       String message = "";
       ArrayList secAry = null;
       SecStockDetail secStockDetail = new SecStockDetail();
       double sellAmount = 0;
       double scale = 0;//存放卖出数量小于先入的库存数量时，卖出数量/当比库存的比例
       try {
           pubpara = new CtlPubPara();
           pubpara.setYssPub(pub);
           String verTp = pubpara.getNavType();
       	if(verTp!=null && verTp.trim().equalsIgnoreCase("new")){
       		isFourDigit=false;//国内QDII统计模式
       	}else{
       		isFourDigit=true;//太平资产统计模式
       	}
           ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
               "avgcostcalculate");
           bTrans = true;
           strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
               " set FCost = ?," +
               " FMCost = ?, FVCost = ?, FBaseCuryCost = ?, FMBaseCuryCost = ?, FVBaseCuryCost = ?," +
               " FPortCuryCost = ?, FMPortCuryCost = ?, FVPortCuryCost = ? where FNum = ?";
			//modified by liubo.Story #2145
			//==============================
//           pst = conn.prepareStatement(strSql);
           pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
           strSql = "select * from " + pub.yssGetTableName("Tb_Data_SubTrade") +
               " a left join (select FFeeCode,FAccountingWay as FAW1 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") b on a.FFeeCode1 = b.FFeeCode" +
               " left join (select FFeeCode,FAccountingWay as FAW2 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") c on a.FFeeCode2 = c.FFeeCode" +
               " left join (select FFeeCode,FAccountingWay as FAW3 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") d on a.FFeeCode3 = d.FFeeCode" +
               " left join (select FFeeCode,FAccountingWay as FAW4 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") e on a.FFeeCode4 = e.FFeeCode" +
               " left join (select FFeeCode,FAccountingWay as FAW5 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") f on a.FFeeCode5 = f.FFeeCode" +
               " left join (select FFeeCode,FAccountingWay as FAW6 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") g on a.FFeeCode6 = g.FFeeCode" +
               " left join (select FFeeCode,FAccountingWay as FAW7 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") h on a.FFeeCode7 = h.FFeeCode" +
               " left join (select FFeeCode,FAccountingWay as FAW8 from " +
               pub.yssGetTableName("Tb_Para_Fee") +
               ") i on a.FFeeCode8 = i.FFeeCode" +
               " left join (select FSecurityCode as FSecurityCode_j,FCatCode,FSubCatCode,FTradeCury from " +
               pub.yssGetTableName("Tb_Para_Security") +
               ") j on a.FSecurityCode = j.FSecurityCode_j" +
               " left join (select FSecurityCode as FSecurityCode_k,FMultiple,FBailType,FBailScale,FBailFix,ffutype from " +
               pub.yssGetTableName("Tb_Para_IndexFutures") +
               ") k on a.FSecurityCode = k.FSecurityCode_k" +
               " where (FBargainDate = " +  dbl.sqlDate(dDate) +
              	" ) and FCheckState = 1 and FPortCode = " + dbl.sqlString(sPortCode) +
              	" and a.FHANDCOSTSTATE<>'1'"+//bug 4558 by zhouwei 20120521 手动修改的成本不重新计算
              	//---edit by songjie 2011.04.26 无法查出需重新计算成本的交易数据---//
              	" and a.FTradeTypeCode = '02' and not exists (select m.frelanum from "+pub.yssGetTableName("tb_data_seclendtrade")
              	+" m where a.FNum = m.FRelaNum) order by FPortCode, FSecurityCode, FNum";//这里只处理卖出交易的成本重新计算
                //---edit by songjie 2011.04.26 无法查出需重新计算成本的交易数据---//
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			while (rs.next()) {

				message = rs.getString("FTradeTypeCode");
				if (rs.getString("FTradeCury") == null
						|| rs.getString("FTradeCury").trim().length() == 0) {
					throw new YssException("系统进行证券库存统计,在证券兑换交易统计时检查到代码为【"
							+ rs.getString("FSecurityCode") + "】证券对应的币种信息不存在!"
							+ "\n" + "请核查以下信息：" + "\n"
							+ "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n"
							+ "2.【证券品种信息】中该证券交易币种项设置是否正确!");
				}
				secAry = (ArrayList)tempHash.get(rs.getString("FPortCode") + "\f" + rs.getString("FSECURITYCODE"));
				cost = new YssCost();
				sellAmount = rs.getDouble("FTRADEAMOUNT");
				//modify by zhangjun 2012-03-01 BUG3836调度方案执行界面执行一个月数据报错
				if(secAry != null){
					for(int i = 0; i<secAry.size(); i++){
						secStockDetail = (SecStockDetail)secAry.get(i);
						scale = YssD.div(sellAmount, secStockDetail.getAmount());
						if(sellAmount == 0){
							break;
						}
						if(sellAmount < secStockDetail.getAmount()){//如果当比卖出数量小于先入的当比库存
							//交易成本
							cost.setCost(cost.getCost() + scale * secStockDetail.getCost());
							cost.setMCost(cost.getMCost() + scale * secStockDetail.getmCost());
							cost.setVCost(cost.getVCost() + scale * secStockDetail.getvCost());
							cost.setBaseCost(cost.getBaseCost() + scale * secStockDetail.getBaseCost());
							cost.setBaseMCost(cost.getBaseMCost() + scale * secStockDetail.getBaseMCost());
							cost.setBaseVCost(cost.getBaseVCost() + scale * secStockDetail.getBaseVCost());
							cost.setPortCost(cost.getPortCost() + scale * secStockDetail.getPortCost());
							cost.setPortMCost(cost.getPortMCost() + scale * secStockDetail.getPortMCost());
							cost.setPortVCost(cost.getPortVCost() + scale * secStockDetail.getPortVCost());
							//剩余库存成本
							secStockDetail.setAmount(secStockDetail.getAmount() - sellAmount);
							secStockDetail.setCost(YssD.round(secStockDetail.getCost()*(1-scale),isFourDigit ? 4 : 2));
							secStockDetail.setmCost(YssD.round(secStockDetail.getmCost()*(1-scale),isFourDigit ? 4 : 2));
							secStockDetail.setvCost(YssD.round(secStockDetail.getvCost()*(1-scale),isFourDigit ? 4 : 2));
							secStockDetail.setBaseCost(YssD.round(secStockDetail.getBaseCost()*(1-scale),isFourDigit ? 4 : 2));
							secStockDetail.setBaseMCost(YssD.round(secStockDetail.getBaseMCost()*(1-scale),isFourDigit ? 4 : 2));
							secStockDetail.setBaseVCost(YssD.round(secStockDetail.getBaseVCost()*(1-scale),isFourDigit ? 4 : 2));
							secStockDetail.setPortCost(YssD.round(secStockDetail.getPortCost()*(1-scale),isFourDigit ? 4 : 2));
							secStockDetail.setPortMCost(YssD.round(secStockDetail.getPortMCost()*(1-scale),isFourDigit ? 4 : 2));
							secStockDetail.setPortVCost(YssD.round(secStockDetail.getPortVCost()*(1-scale),isFourDigit ? 4 : 2));
							sellAmount = 0;
						}else if (sellAmount >= secStockDetail.getAmount()){
							//交易成本
							cost.setCost(cost.getCost() + secStockDetail.getCost());
							cost.setMCost(cost.getMCost() + secStockDetail.getmCost());
							cost.setVCost(cost.getVCost() + secStockDetail.getvCost());
							cost.setBaseCost(cost.getBaseCost() + secStockDetail.getBaseCost());
							cost.setBaseMCost(cost.getBaseMCost() + secStockDetail.getBaseMCost());
							cost.setBaseVCost(cost.getBaseVCost() + secStockDetail.getBaseVCost());
							cost.setPortCost(cost.getPortCost() + secStockDetail.getPortCost());
							cost.setPortMCost(cost.getPortMCost() + secStockDetail.getPortMCost());
							cost.setPortVCost(cost.getPortVCost() + secStockDetail.getPortVCost());
							//剩余库存成本清0
							sellAmount -= secStockDetail.getAmount();
							secStockDetail.setAmount(0);
							secStockDetail.setCost(0);
							secStockDetail.setmCost(0);
							secStockDetail.setvCost(0);
							secStockDetail.setBaseCost(0);
							secStockDetail.setBaseMCost(0);
							secStockDetail.setBaseVCost(0);
							secStockDetail.setPortCost(0);
							secStockDetail.setPortMCost(0);
							secStockDetail.setPortVCost(0);
						}
					
					}
				}
				costCal.setYssPub(pub);
				costCal.roundCost(cost, isFourDigit ? 4 : 2);
				pst.setDouble(1, cost.getCost());
				pst.setDouble(2, cost.getMCost());
				pst.setDouble(3, cost.getVCost());
				pst.setDouble(4, cost.getBaseCost());
				pst.setDouble(5, cost.getBaseMCost());
				pst.setDouble(6, cost.getBaseVCost());
				pst.setDouble(7, cost.getPortCost());
				pst.setDouble(8, cost.getPortMCost());
				pst.setDouble(9, cost.getPortVCost());
				pst.setString(10, rs.getString("FNum"));
				pst.executeUpdate();
			}
           pubpara = new CtlPubPara();
           pubpara.setYssPub(pub);
           String digit = pubpara.getKeepFourDigit(); //获取通用参数中的小数保留位数
           if (digit.toLowerCase().equalsIgnoreCase("two")) { //两位
               isFourDigit = false;
           } else if (digit.toLowerCase().equalsIgnoreCase("four")) { //四位
               isFourDigit = true;
           }
           conn.setAutoCommit(false);
           bTrans = true;
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
       } catch (Exception e) {
           //------ modify by wangzuochun 2010.10.13 MS01840    【资产估值】证券库存统计时报错    QDV4太平2010年10月11日（开发部）01_B   
           if (message != null && message.length() > 0)
           {
	            if (YssOperCons.YSS_JYLX_Sale.equals(message)) {
	                throw new YssException("在重新计算卖出成本时出现异常!\n", e);
	            }
           }
           else{
           	throw new YssException("在重新计算交易成本时出现异常!\n", e);
           }
           //---------------------------MS01840----------------------------//
       } finally { // by 曹丞 2009.01.21 检查出重新计算某一类业务成本出错 MS00004 QDV4.1-2009.2.1_09A
           dbl.closeResultSetFinal(rs);
           dbl.closeStatementFinal(pst);
           dbl.endTransFinal(conn, bTrans);
       }
   }
   
   //插入今日明细库存  add by yanghaiming 20101210 QDV4赢时胜（深圳）2010年11月30日02_A
	public void insertStockDetail(java.util.Date dDate, String sPortCode, HashMap tempHash,boolean fAnalysisCode1, boolean fAnalysisCode2) throws YssException, SQLException {
	   Connection conn = dbl.loadConnection();
       boolean bTrans = false; //代表是否开始了事务
   	   //modified by liubo.Story #2145
       //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
       //=================================
//       PreparedStatement pst = null;
       YssPreparedStatement pst = null;
       //=============end====================
       String strSql = "";
       ArrayList tempAry = null;
       SecStockDetail secStockDetail = new SecStockDetail();
       
  	 //add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 start 
  		int count = 1;
  		
       try{
    	   bTrans = true;
		   strSql = "delete from " + pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") +
		   			" where FPORTCODE = " + dbl.sqlString(sPortCode) + " and FBARGAINDATE = " + dbl.sqlDate(dDate);
		   dbl.executeSql(strSql);
		   strSql = "insert into " + pub.yssGetTableName("TB_STOCK_SECURITYDETAIL") +
  			" (FNUM,FSECURITYCODE,FBARGAINDATE,FPORTCODE,FINVMGRCODE,FBROKERCODE,FATTRCLSCODE," +
  			"FINVESTTYPE,FTRADEAMOUNT,FCOST,FMCOST,FVCOST,FBASECURYCOST,FMBASECURYCOST,FVBASECURYCOST," +
  			"FPORTCURYCOST,FMPORTCURYCOST,FVPORTCURYCOST) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//		   pst = conn.prepareStatement(strSql);
		   pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
		   Iterator it = tempHash.values().iterator();
		   while(it.hasNext()){
			   tempAry = (ArrayList)it.next();
			   //modify by zhangjun  
			   if (tempAry != null){
				   for (int i = 0; i<tempAry.size(); i++){
					   secStockDetail = (SecStockDetail)tempAry.get(i);
					   pst.setString(1, secStockDetail.getfNum());
					   pst.setString(2, secStockDetail.getfSecurityCode());
					   pst.setDate(3, YssFun.toSqlDate(dDate));
					   pst.setString(4, sPortCode);
					   pst.setString(5, fAnalysisCode1 ? secStockDetail.getInvMgrCodeName() : " ");
					   pst.setString(6, fAnalysisCode2 ? secStockDetail.getBrokerCode() : " ");
					   pst.setString(7, secStockDetail.getAttrClsCode());
					   pst.setString(8, secStockDetail.getInvestType());
					   pst.setDouble(9, secStockDetail.getAmount());
					   pst.setDouble(10, secStockDetail.getCost());
					   pst.setDouble(11, secStockDetail.getmCost());
					   pst.setDouble(12, secStockDetail.getvCost());
					   pst.setDouble(13, secStockDetail.getBaseCost());
					   pst.setDouble(14, secStockDetail.getBaseMCost());
					   pst.setDouble(15, secStockDetail.getBaseVCost());
					   pst.setDouble(16, secStockDetail.getPortCost());
					   pst.setDouble(17, secStockDetail.getPortMCost());
					   pst.setDouble(18, secStockDetail.getPortVCost());
					   pst.addBatch();
					   
					 //add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 start 
						
						if(count==500){
							pst.executeBatch();
							count = 1;
							continue;
						}

						count++;
						//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 end
				   }  
			   }			   
		   }
	       pst.executeBatch();
		   conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
       }catch (Exception e) {
           throw new YssException("插入扣除卖出成本后明细库存出错！", e);
       } finally {
    	   dbl.closeStatementFinal(pst);
           dbl.endTransFinal(conn, bTrans);
       }
   }
	
   /**add by zhouxiang 2010.12.1 计算证券借贷交易数据的借入成本 
    * @param dDate 开始日期
    * @param dDate2	结束日期
    * @param portCodes	证券代码
    * @throws YssException 
    * @throws SQLException 
    */
	 private void refreshBLTradeCost(Date dDate, Date dDate2, String portCodes) throws YssException, SQLException {
		Connection conn = dbl.loadConnection();
		// modified by liubo.Story #2145
		// 将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
		// =================================
		// PreparedStatement pst = null;
		YssPreparedStatement pst = null;
		// =============end====================
		String strSql = "";
		boolean bTrans = false;//shashijie 2013-2-21 BUG 7135 增加boolean变量
		ResultSet rs = null;
		double dCost = 0;
		//double dBaseCost = 0;
		//double dPortCost = 0;
		double dBaseRate = 0;
		double dPortRate = 0;
		YssCost ycost = new YssCost();
		String sCheckPara = "";// 成本计算参数控制
		try {
			/**add---shashijie 2013-2-21 BUG 7135 证券库存统计中证券借贷的处理存在游标越界的隐患*/
			bTrans = true;
			conn.setAutoCommit(false);
			/**end---shashijie 2013-2-21 BUG 7135 */
			strSql = "update "
					+ pub.yssGetTableName("tb_DATA_SecLendTRADE")
					+ " set FCost = ?,"
					+ " FMCost = ?, FVCost = ?, FBaseCuryCost = ?, FMBaseCuryCost = ?, FVBaseCuryCost = ?,"
					+ " FPortCuryCost = ?, FMPortCuryCost = ?, FVPortCuryCost = ? where FNum = ?";
			//modified by liubo.Story #2145
			//==============================
			//pst = conn.prepareStatement(strSql);
			pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
			strSql = "select distinct a.fnum,a.fsecuritycode,a.ftradeamount,a.ftradeprice,b.Fcurycode,a.fportcode," +
					"a.fbargaindate,a.ftradetypecode,a.fbrokercode from "
					+ pub.yssGetTableName("tb_DATA_SecLendTRADE")
					+ " a left join (select a1.Fcurycode,a1.fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")
					+" a1 join (select fcashacccode, max(Fstartdate) as fstartdate from "+pub.yssGetTableName("tb_para_cashaccount")
					+" where fstartdate <= "+dbl.sqlDate(new java.util.Date())
					+"  and fcheckstate = 1 group by fcashacccode) a2 on a1.fstartdate = a2.fstartdate"
					+" and a1.fcashacccode =a2.fcashacccode where a1.fcheckstate = 1 and a1.fstartdate <= "
					+ dbl.sqlDate(new java.util.Date())+") b on a.fcashacccode =b.fcashacccode where a.fportcode in (" + portCodes
					+ ") and a.ftradetypecode in( "
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)+","+dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rcb)
					+ ") and a.fcheckstate=1 and FBargainDate between " + dbl.sqlDate(dDate)
					+ " and " + dbl.sqlDate(dDate2);
			rs=dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			while(rs.next()){
				dBaseRate=this.getSettingOper().getCuryRate(rs.getDate("fbargaindate"), rs.getString("Fcurycode"), 
						rs.getString("fportcode"),YssOperCons.YSS_RATE_BASE);
				dPortRate=this.getSettingOper().getCuryRate(rs.getDate("fbargaindate"), rs.getString("Fcurycode"), 
						rs.getString("fportcode"),YssOperCons.YSS_RATE_PORT);
				if(rs.getString("ftradetypecode").equalsIgnoreCase(YssOperCons.YSS_SECLEND_JYLX_Borrow)){//借入操作直接计算成本
					dCost=YssD.round(YssD.mul(rs.getDouble("ftradeamount"), rs.getDouble("ftradeprice")), 2);
					ycost.setCost(dCost);
					ycost.setBaseCost(this.getSettingOper().calBaseMoney(dCost, dBaseRate));
					ycost.setPortCost(this.getSettingOper().calPortMoney(dCost, dBaseRate, dPortRate,  
							rs.getString("Fcurycode"), rs.getDate("fbargaindate"), rs.getString("fportcode")));
				}else if(rs.getString("ftradetypecode").equalsIgnoreCase(YssOperCons.YSS_SECLEND_JYLX_Rcb)){//如果是借入归还则移动加权平均法计算成本
					CtlPubPara pubPara = new CtlPubPara();
		        	pubPara.setYssPub(pub);
		        	sCheckPara = pubPara.getRateCalculateType("SecBLCostCalWay","port",1);//取参数一的组合内容
		        	if(sCheckPara.equalsIgnoreCase("1,1")){//参数没有设置
		        		BorrowLCostCalCarryAddValue(ycost,rs,dBaseRate,dPortRate);//移动加权
		        	}else{
		        		sCheckPara = pubPara.getRateCalculateType("SecBLCostCalWay","ComboBox1",1);//取参数一的组合内容
		        		if(sCheckPara.split(",")[0].equalsIgnoreCase("FirstInOut")){
		        			BorrowLCostCalFirstInOut(ycost,rs.getDate("fbargaindate"),rs.getString("fsecuritycode"),
		        					rs.getDouble("ftradeamount"),rs.getString("fportcode"),rs.getString("fbrokercode"));//先入先出												
		        		}else{
		        			BorrowLCostCalCarryAddValue(ycost,rs,dBaseRate,dPortRate);//移动加权
		        		}
		        	}
				}
				pst.setDouble(1,ycost.getCost());
				pst.setDouble(2,ycost.getCost());
				pst.setDouble(3,ycost.getCost());
				pst.setDouble(4,ycost.getBaseCost());
				pst.setDouble(5,ycost.getBaseCost());
				pst.setDouble(6,ycost.getBaseCost());
				pst.setDouble(7,ycost.getPortCost());
				pst.setDouble(8,ycost.getPortCost());
				pst.setDouble(9,ycost.getPortCost());
				pst.setString(10, rs.getString("fnum"));
				pst.executeUpdate();
			}
			/**add---shashijie 2013-2-21 BUG 7135 证券库存统计中证券借贷的处理存在游标越界的隐患*/
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			/**end---shashijie 2013-2-21 BUG 7135 */
		} catch (YssException e) {
			throw new YssException("重新计算证券借贷交易数据借入成本出错");
		} finally {
			/**add---shashijie 2013-2-21 BUG 7135 证券库存统计中证券借贷的处理存在游标越界的隐患*/
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
            /**end---shashijie 2013-2-21 BUG 7135 */
		}
 	}
	 
    /*** @param dCost edited by zhouxiang 证券借贷，，先入先出算法的实现：
     * @param dBaseCost   T 日 买入 1000			125价格
     * @param dPortCost	  T+1 日买入1000			100价格
     * @param date		  T+2日买入500			200价格
     * @param fsecuritycode   T+3日归还700 的计算方法：昨日库存是2500 ，先计算2500-700的成本，采用后进后出算法计算此1800的成本数，用昨日成本减去后进先出数就是先进先出成本
     * @param ftradeamount  此方法需要传入：先进先出日期，先进先出数量，先进先出的证券名称和组合代码
     * @param portcode 
     * @param brokercode 券商代码
     * @throws YssException 
     */
	private void BorrowLCostCalFirstInOut(YssCost ycost, java.sql.Date date, String fsecuritycode, double ftradeamount, String portcode, String brokercode) throws YssException {
		String sqlStr="";
		ResultSet rs=null;
		double dCost=ycost.getCost(); double dBaseCost=ycost.getBaseCost();
		double dPortCost=ycost.getPortCost();
		double dStockCost=0,dStockBaseCost=0,dStockPortCost=0;
		double dLastIn=0;//后进先出数
		try{
			sqlStr = "select a.famount,a.fbal,a.fbasecurybal,a.fportcurybal from "
					+ pub.yssGetTableName("tb_stock_secrecpay")
					+ " a where a.fstoragedate = " + dbl.sqlDate(YssFun.addDay(date, -1))
					+ " and a.fsecuritycode=" + dbl.sqlString(fsecuritycode)
					+ " and a.fportcode=" + dbl.sqlString(portcode)
					+ " and a.fsubtsftypecode = "
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BSC);
			rs=dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			if(rs.next()){
				dLastIn=YssD.sub(rs.getDouble("famount"),ftradeamount);//后进先出数=昨日库存数-先进先出的数量
				dStockCost=rs.getDouble("fbal");//取昨日余额
				dStockBaseCost=rs.getDouble("fbasecurybal");
				dStockPortCost=rs.getDouble("fportcurybal");
			}else{dCost=0;dBaseCost=0;dPortCost=0;}
			sqlStr="select a.ftradeamount,a.ftradeprice,a.fcost,a.fbasecurycost,a.fportcurycost,a.fbargaindate,a.fportcode,c.fcurycode from "+pub.yssGetTableName("tb_data_seclendtrade")
					+" a left join(select distinct b.fcurycode,b.fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")
					+" b where b.fcheckstate=1)  c on a.fcashacccode=c.fcashacccode"
					+" where a.fsecuritycode = "+dbl.sqlString(fsecuritycode)
					+" and a.fbrokercode = "+dbl.sqlString(brokercode)
					+" and a.fportcode ="+dbl.sqlString(portcode)
					+" and a.fbargaindate < "+dbl.sqlDate(date)
					+" and a.ftradetypecode="+dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)
					+" and a.fcheckstate=1  order by a.fcreatetime desc";
			rs=dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
			while(rs.next()){
				if(YssD.compare(dLastIn, rs.getDouble("ftradeamount"))>=0){//后进先出数大于单笔交易时成本就取交易成本相加
					dLastIn=YssD.sub(dLastIn,rs.getDouble("ftradeamount"));
					dCost=YssD.add(dCost,rs.getDouble("fcost"));//此处计算后进先出的成本
					dBaseCost=YssD.add(dBaseCost, rs.getDouble("fbasecurycost"));
					dPortCost=YssD.add(dPortCost, rs.getDouble("fportcurycost"));
				}else if(YssD.compare(dLastIn, rs.getDouble("ftradeamount"))<=0 && dLastIn >0){											//后进先出数小于单笔交易的时候，成本=后进数*交易价格
					dCost=YssD.add(dCost,YssD.mul(dLastIn,rs.getDouble("ftradeprice")));
					double dTmep=YssD.mul(YssD.div(rs.getDouble("fbasecurycost"), rs.getDouble("ftradeamount")), dLastIn);
					dBaseCost=YssD.add(dBaseCost,dTmep);
					dTmep=YssD.mul(YssD.div(rs.getDouble("fportcurycost"), rs.getDouble("ftradeamount")), dLastIn);
					dPortCost=YssD.add(dPortCost, dTmep);
					break;
				}else if( dLastIn <=0){
					dCost=0;
					dBaseCost=0;
					dPortCost=0;
					break;
				}
			}
			dCost=YssD.round(YssD.sub(dStockCost, dCost),2);
			dBaseCost=YssD.round(YssD.sub(dStockBaseCost, dBaseCost),2);
			dPortCost=YssD.round(YssD.sub(dStockPortCost, dPortCost),2);
			ycost.setCost(dCost);ycost.setBaseCost(dBaseCost);ycost.setPortCost(dPortCost);
		}catch(Exception e){
			throw new YssException("查询昨日借入库存计算后进先出数报错");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
     * @param dCost 移动加权算法计算借入归还
     * @param dBaseCost
     * @param dPortCost
     * @param rs
     * @param dPortRate 
     * @param dBaseRate 
     * @throws SQLException 
     * @throws YssException 
     */
	private void BorrowLCostCalCarryAddValue(YssCost ycost, ResultSet rs, double dBaseRate, double dPortRate) throws YssException {
		 String subStr="";
		 double dCost=ycost.getCost(); double dBaseCost=ycost.getBaseCost();
		 double dPortCost=ycost.getPortCost();
		 ResultSet rsSub=null;
		 try{
			 subStr = "select (case when a.famount=0 then 0 else (a.fbal / a.famount) end  ) as price,(case when a.famount=0 then 0 else(a.fbasecurybal / a.famount)end ) as basepirce,"
					+"(case when a.famount=0 then 0 else(a.fportcurybal / a.famount)end ) as portpirce"
					+ " from "
					+ pub.yssGetTableName("tb_stock_secrecpay")
					+ " a where fstoragedate = "
					+ dbl.sqlDate(YssFun.addDay(rs.getDate("fbargaindate"),-1))
					+ " and a.fsubtsftypecode = '10BSC' and a.fsecuritycode ="
					+ dbl.sqlString(rs.getString("fsecuritycode"));
			rsSub=dbl.queryByPreparedStatement(subStr); //modify by fangjiang 2011.08.14 STORY #788
			if(rsSub.next()){
				dCost=YssD.round(YssD.mul(rs.getDouble("ftradeamount"), rsSub.getDouble("price")),2);
				dBaseCost=YssD.round(YssD.mul(rs.getDouble("ftradeamount"), rsSub.getDouble("basepirce")),2);
				dPortCost=YssD.round(YssD.mul(rs.getDouble("ftradeamount"), rsSub.getDouble("portpirce")),2);
			}else{
				dCost=YssD.round(YssD.mul(rs.getDouble("ftradeamount"), rs.getDouble("ftradeprice")), 2);
				dBaseCost=this.getSettingOper().calBaseMoney(dCost, dBaseRate);
				dPortCost=this.getSettingOper().calPortMoney(dCost, dBaseRate, dPortRate,  rs.getString("Fcurycode"), rs.getDate("fbargaindate"), rs.getString("fportcode"));
			}
			ycost.setCost(dCost);ycost.setBaseCost(dBaseCost);ycost.setPortCost(dPortCost);
		}catch(Exception e){
			 throw new YssException("移动加权计算借入归还出错");
		 }finally{
			 dbl.closeResultSetFinal(rsSub);
		 }
		
		
	}
}
