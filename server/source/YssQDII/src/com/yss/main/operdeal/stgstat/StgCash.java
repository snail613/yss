package com.yss.main.operdeal.stgstat;

import java.util.*;
import java.sql.*;

import com.yss.commeach.EachRateOper;
import com.yss.main.storagemanage.CashStorageBean;
import com.yss.util.YssFun;
import com.yss.util.YssException;
import com.yss.util.YssD;
import com.yss.util.YssOperCons;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.income.stat.StatPurchaseIns;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.*;
import com.yss.manager.*;
import com.yss.main.storagemanage.*;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;

public class StgCash
    extends BaseStgStatDeal {
    public StgCash() {
    }

    //把前日的库存存放到Map中
    public HashMap getEveStg(java.util.Date dDate) throws YssException {
        HashMap hmEveStg = new HashMap();
        CashStorageBean cashstorage = null;

        boolean analy1;
        boolean analy2;
        boolean analy3;

        ResultSet rs = null;

        String sKey = "";

        String strSql = "";

        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

            strSql = "select a.*,b.FCuryCode from (" +
                "select FCashAccCode as AFCashAccCode,FPortCode as AFPortCode,FAccBalance,FPortCuryBal,FBaseCuryBal," +
                "FYearMonth as AFYearMonth,FStorageDate as AFStorageDate," +
                " FPortCuryRate as FPortYesRate,FBaseCuryRate as FBaseYesRate" +
                (analy1 ? ",FAnalysisCode1 as AFAnalysisCode1" : "") +
                (analy2 ? ",FAnalysisCode2 as AFAnalysisCode2" : "") +
                (analy3 ? ",FAnalysisCode3 as AFAnalysisCode3" : "") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                ",fattrclscode AS Afattrclscode"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//   
                " from " + pub.yssGetTableName("tb_stock_cash") +
                " where FCheckState = 1 and FPortCode in (" + portCodes + ")" +
                (statCodes.length()>0?(" and FCashAccCode in("+operSql.sqlCodes(statCodes)+")"):"")+//添加现金帐户做为条件实现部分统计 系统优化 by leeyu 20100622 合并太平版本代码
                " and (FAccBalance<>0 or FBaseCuryBal<>0 or FPortCuryBal<>0)" +
                " and " + operSql.sqlStoragEve(dDate) + ") a " +
                //----------------------------------------------
                " left join (select FCashAccCode as BFCashAccCode, FCuryCode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) b on a.AFCashAccCode = b.BFCashAccCode" +
                //----------------------------------------------
                " left join (select " +
                " sum(" + dbl.sqlIsNull("FBal", "0") +
                ") as FBal,sum(" + dbl.sqlIsNull("FPortCuryBal", "0") +
                ")as CFPortCuryBal,sum(" + dbl.sqlIsNull("FBaseCuryBal", "0") +
                ") as CFBaseCuryBal," +
                " FCashAccCode as CFCashAccCode,FPortCode as CFPortCode,FYearMonth as CFYearMonth,FStorageDate as CFStorageDate" +
                (analy1 ? ",FAnalysisCode1 as CFAnalysisCode1" : "") +
                (analy2 ? ",FAnalysisCode2 as CFAnalysisCode2" : "") +
                (analy3 ? ",FAnalysisCode3 as CFAnalysisCode3" : "") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                ",fattrclscode AS Cfattrclscode"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//   
                " from " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " where FSubTsfTypeCode like ('06%') and FPortCode in(" +
                portCodes + ") " +
               (statCodes.length()>0?(" and FCashAccCode in("+operSql.sqlCodes(statCodes)+")"):"")+//添加现金帐户做为条件实现部分统计 系统优化 by leeyu 20100622 合并太平版本代码
               " and "+ operSql.sqlStoragEve(dDate) +//系统优化处理,优化SQL by leeyu 20100617 合并太平版本代码
                " group by " +
                " FCashAccCode,FPortCode,FYearMonth,FStorageDate" +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                ",fattrclscode "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------// 
                (analy1 ? ",FAnalysisCode1" : "") +
                (analy2 ? ",FAnalysisCode2" : "") +
                (analy3 ? ",FAnalysisCode3" : "") +
                ") c on a.AFYearMonth = c.CFYearMonth" +
                " and a.AFStoragedate = c.CFStoragedate and a.AFPortCode = c.CFPortCode and a.AFCashAccCode = c.CFCashAccCode " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " and a.Afattrclscode = c.Cfattrclscode "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------// 
                (analy1 ? " and a.AFAnalysisCode1 = c.CFAnalysisCode1" : "") +
                (analy2 ? " and a.AFAnalysisCode2 = c.CFAnalysisCode2" : "") +
                (analy3 ? " and a.AFAnalysisCode3 = c.CFAnalysisCode3" : "") +
                "  where a.FAccBalance <> 0 or a.FBaseCuryBal <> 0 or a.FPortCuryBal <> 0 or c.FBal <> 0 or c.CFPortCuryBal <> 0 or c.CFBaseCuryBal <> 0";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                cashstorage = new CashStorageBean();
                sKey = rs.getString("AFCashAccCode") + "\f" +
                    rs.getString("AFPortCode") +
                    (analy1 ? "\f" + rs.getString("AFAnalysisCode1") : "") +
                    (analy2 ? "\f" + rs.getString("AFAnalysisCode2") : "") +
                    (analy3 ? "\f" + rs.getString("AFAnalysisCode3") : "")+
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    "\f"+rs.getString("Afattrclscode");
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                cashstorage.setStrStorageDate(YssFun.formatDate(dDate,
                    "yyyy-MM-dd"));
                cashstorage.setStrCashAccCode(rs.getString("AFCashAccCode"));
                cashstorage.setStrPortCode(rs.getString("AFPortCode"));
                cashstorage.setStrCuryCode(rs.getString("FCuryCode"));
                cashstorage.setStrAccBalance(rs.getDouble("FAccBalance") + "");
                cashstorage.setStrBaseCuryBal(rs.getDouble("FBaseCuryBal") + "");
                cashstorage.setStrPortCuryBal(rs.getDouble("FPortCuryBal") + "");
                cashstorage.setStrBaseCuryRate(rs.getDouble("FBaseYesRate") + "");
                cashstorage.setStrPortCuryRate(rs.getDouble("FPortYesRate") + "");
                if (analy1) {
                    cashstorage.setStrFAnalysisCode1(rs.getString("AFAnalysisCode1"));
                } else {
                    cashstorage.setStrFAnalysisCode1(" ");
                }
                if (analy2) {
                    cashstorage.setStrFAnalysisCode2(rs.getString("AFAnalysisCode2"));
                } else {
                    cashstorage.setStrFAnalysisCode2(" ");
                }
                if (analy3) {
                    cashstorage.setStrFAnalysisCode3(rs.getString("AFAnalysisCode3"));
                } else {
                    cashstorage.setStrFAnalysisCode3(" ");
                }
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                cashstorage.setStrAttrClsCode(rs.getString("Afattrclscode"));
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                hmEveStg.put(sKey, cashstorage);
            }
            return hmEveStg;
        } catch (Exception e) {
            throw new YssException("系统进行现金库存统计,在统计昨日库存信息时出现异常!\n", e); //by 曹丞 2009.01.22 统计昨日库存信息异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    //修改了统计现金库存的方式，先把前日库存放入Map中，再匹配当日的调拨，这样会影响一点效率，以后再慢慢优化  胡昆  20071008
    public ArrayList getStorageStatData(java.util.Date dDate) throws
        YssException {
        ArrayList all = new ArrayList();
        ResultSet rs = null;
        String strSql = "";
        CashStorageBean cashstorage = null;
       //----- QDV4中保2010年1月4日01_B  add by jiangshichao  2010.01.06 合并太平版本代码---------
       double dScale = 0;
       double dBaseMoney = 0;
       double dPortMoney = 0;
       String sPara="";
       CtlPubPara pubPara = new CtlPubPara();  //用户获取现金库存统计方式
       pubPara.setYssPub(pub);
       sPara = pubPara.getStgCashPara();
       //---- QDV4中保2010年1月4日01_B end --------------------------------------

        boolean analy1;
        boolean analy2;
        boolean analy3;

        double baseMoney = 0;
        double portMoney = 0;

        String sKey = "";

        double dBaseRate = 1;
        double dPortRate = 1;

        HashMap hmEveStg = null;

        Iterator iter = null;
        
      //---MS00809 QDV4建行2009年11月13日01_B 蒋世超  添加 2009.11.19 ----------------------------
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
      //---MS00809 QDV4建行2009年11月13日01_B end --------------------------------------------- 
        
      //---MS00866 QDV4建行2009年12月14日01_AB  蒋世超 添加 2009.12.16--------------------------- 
        pubPara = new CtlPubPara();
        pubPara.setYssPub(pub);
        String rateMode =pubPara.getRateMode();//获取外汇的估值汇率设置
      //---MS00866 QDV4建行2009年12月14日01_AB  end -------------------------------------------  
        
        
        
        //--- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 ---------------------------
        boolean bTPCost =false;//区分是太平资产的库存统计还是QDII的库存统计,合并版本时调整
    	String sPara_tp = "";
    	 //--- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 end------------------------
        try {
        	//--- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 ---------------------------
        	sPara_tp =pubPara.getNavType();//通过净值表类型来判断
        	if(sPara_tp!=null && sPara_tp.trim().equalsIgnoreCase("new")){
        		bTPCost=false;//国内QDII统计模式
        	}else{
        		bTPCost=true;//太平资产统计模式
        	}
        	//--- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 end------------------------
        	
            if (bYearChange) {
            	if(bTPCost){
            		//太平资产年终结账方式：根据09、14、18报表产生虚拟账户的期初数和证券的特殊处理
            		yearChange_TP(dDate, portCodes);
            	}else{
            		//QDII年终结账方式
            		yearChange(dDate, portCodes);
            	}
                
            }

          //#1476 因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误   add by jiangshichao 2011.03.16-----//
            String fieldStr = "";
            if(bTPCost){
            	fieldStr = ",nvl(fattrclscode,' ') as fattrclscode ";
            }else{
            	fieldStr = ",' ' as fattrclscode ";
            }
          //#1476 因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误   end----------//
            
            hmEveStg = getEveStg(dDate);
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

            strSql = " select a.*,b.FCuryCode,c.FPortCury from (" +
                "select FCashAccCode, FPortCode, FInOut, FMoney*FInOut as FMoney,FBaseCuryRate,FPortCuryRate," +
                "FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransDate" +
                (analy1 ? ",FAnalysisCode1" : "") +
                (analy2 ? ",FAnalysisCode2" : "") +
                (analy3 ? ",FAnalysisCode3" : "") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                ",fattrclscode"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------// 
                " from (select FCashAccCode,FPortCode,FInOut,FMoney,FTsfTypeCode,FSubTsfTypeCode," +
                " FBaseCuryRate,FPortCuryRate,FTransferDate,FTransDate" +
                (analy1 ? ",FAnalysisCode1" : "") +
                (analy2 ? ",FAnalysisCode2" : "") +
                (analy3 ? ",FAnalysisCode3" : "") +
                fieldStr+//#1476 因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误   add by jiangshichao 2011.03.16-----//
                " from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " a61 join (select FNum,FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransDate from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FCheckState = 1 and" +
                " FTransferDate = " + dbl.sqlDate(dDate) +
                ") a62 on a61.FNum = a62.FNum" +
                " where FPortCode in (" + portCodes + ") and FCheckState = 1" +
                (statCodes.length()>0?(" and a61.FCashAccCode in("+operSql.sqlCodes(statCodes)+")"):"")+//添加现金帐户做为条件实现部分统计 系统优化 by leeyu 20100622 合并太平版本代码
                ") t" +
                //group by FCashAccCode, FPortCode, FInOut,FTsfTypeCode,FSubTsfTypeCode
                ") a left join " +
                //--------------------------------------
                //-----xuqiji 20100712 MS01426 现金账户设置中设置启用日期和银行账号不一致  QDV4赢时胜(测试)2010年07月8日02_B   -----//
             
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
             
                " (select * from " 
                + pub.yssGetTableName("tb_para_cashaccount") +
                " where FCheckState = 1 ) b on a.FCashAccCode = b.FCashAccCode " +
                // end by lidaolong
                //------------------------------end -----------------------------------//
                //--------------------------------------
                
                //------ modify by wangzuochun  2010.07.16  MS01449    组合代码相同而启用日期不同的组合时，新建买入证券据，进行库存统计后，现金库存会增倍 QDV4赢时胜(测试)2010年7月15日01_B 
                //----------------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
        
                " left join (select FPortCode, FPortName, FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) c on a.FPortCode = c.FPortCode "
                
                //end by lidaolong
                //-------------------------------------------- MS01449 -------------------------------------------//
				//----- QDV4中保2010年1月4日01_B  add by jiangshichao  2010.01.06---------
                +(sPara.equalsIgnoreCase("1")?" order by finout desc ":""); //移动加权方式统计现金库存时，先算流入部分，再算流出.所以这里添加按资金流向的排序
             //----- QDV4中保2010年1月4日01_B  end ------------------------------------
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                dBaseRate = 1;
                dPortRate = 1;
                sKey = rs.getString("FCashAccCode") + "\f" +
                    rs.getString("FPortCode") +
                    (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "")+
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    "\f"+rs.getString("fattrclscode");
                   //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                if (hmEveStg.containsKey(sKey)) {
                    cashstorage = (CashStorageBean) hmEveStg.get(sKey);
                    cashstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
					//----- QDV4中保2010年1月4日01_B  add by jiangshichao  2010.01.06 合并太平版本代码---------
	               if(sPara.equalsIgnoreCase("1")&& rs.getInt("FInOut")==-1){
	            	  dScale = YssD.div(rs.getDouble("FMoney"), YssFun.toDouble(cashstorage.getStrAccBalance()));
	            	  dBaseMoney = YssD.round(YssD.mul(YssFun.toDouble(cashstorage.getStrBaseCuryBal()), dScale), 2);
	                  dPortMoney = YssD.round(YssD.mul(YssFun.toDouble(cashstorage.getStrPortCuryBal()), dScale), 2);
                  
	            	  dBaseRate = YssD.round(YssD.div(dBaseMoney, rs.getDouble("FMoney")), 15);
	            	  dPortRate = YssD.round(YssD.div(dBaseMoney,dPortMoney), 15);  
            	  
	            	  cashstorage.setStrAccBalance(YssD.add(YssFun.toDouble(cashstorage.getStrAccBalance()), rs.getDouble("FMoney")) +"");
	            	  cashstorage.setStrBaseCuryBal(YssD.add(YssFun.toDouble(cashstorage.getStrBaseCuryBal()), dBaseMoney) +"");
	            	  cashstorage.setStrPortCuryBal(YssD.add(YssFun.toDouble(cashstorage.getStrPortCuryBal()), dPortMoney) +"");
	            	  if(YssOperCons.YSS_ZJDBLX_InnerAccount.equalsIgnoreCase(rs.getString("FTsfTypeCode"))&&YssOperCons.YSS_ZJDBZLX_COST_RateTrade.equalsIgnoreCase(rs.getString("FSubTsfTypeCode"))
	                    		&& rateMode.equalsIgnoreCase("0")){//MS00866 QDV4建行2009年12月14日01_AB  蒋世超 添加 2009.12.15 
	                    	dBaseRate = this.getSettingOper().getCuryRate(dDate,
	                                rs.getString("FCuryCode"), rs.getString("FPortCode"),
	                                YssOperCons.YSS_RATE_BASE); 
	                    	 rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
	                    	 dPortRate = rateOper.getDPortRate();
	                    }
	               }else{
                   //----- QDV4中保2010年1月4日01_B  end -------------------------------------
                    //---MS00809 QDV4建行2009年11月13日01_B 蒋世超  添加 2009.11.19 外汇交易默认取调拨日期汇率进行估值----------------------------
                    if(YssOperCons.YSS_ZJDBLX_InnerAccount.equalsIgnoreCase(rs.getString("FTsfTypeCode"))&&YssOperCons.YSS_ZJDBZLX_COST_RateTrade.equalsIgnoreCase(rs.getString("FSubTsfTypeCode"))
                    		&& rateMode.equalsIgnoreCase("0")){//MS00866 QDV4建行2009年12月14日01_AB  蒋世超 添加 2009.12.15 
                    	dBaseRate = this.getSettingOper().getCuryRate(dDate,
                                rs.getString("FCuryCode"), rs.getString("FPortCode"),
                                YssOperCons.YSS_RATE_BASE); 
                    	 rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
                    	 dPortRate = rateOper.getDPortRate();
                    }else{
                      dBaseRate = rs.getDouble("FBaseCuryRate");
                      dPortRate = rs.getDouble("FPortCuryRate");
                    }
                    //---MS00809 QDV4建行2009年11月13日01_B end ----------------------------------------------
                    cashstorage.setStrAccBalance(YssD.add(YssFun.toDouble(
                        cashstorage.getStrAccBalance()), rs.getDouble("FMoney")) +
                                                 "");
                    cashstorage.setStrBaseCuryBal(YssD.add(YssFun.toDouble(
                        cashstorage.getStrBaseCuryBal()),
                        this.getSettingOper().calBaseMoney(rs.getDouble("FMoney"),
                        dBaseRate)) + "");
                    cashstorage.setStrPortCuryBal(YssD.add(YssFun.toDouble(
                        cashstorage.getStrPortCuryBal()),
                        this.getSettingOper().calPortMoney(rs.getDouble("FMoney"),
                        dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCuryCode"), rs.getDate("FTransferDate"), rs.getString("FPortCode"))) + "");
	               }
                    cashstorage.setStrBaseCuryRate(dBaseRate + "");
                    cashstorage.setStrPortCuryRate(dPortRate + "");
                } else {
                    cashstorage = new CashStorageBean();
                    cashstorage.setStrStorageDate(YssFun.formatDate(dDate,
                        "yyyy-MM-dd"));
                    cashstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
                    cashstorage.setStrPortCode(rs.getString("FPortCode"));
                    cashstorage.setStrCuryCode(rs.getString("FCuryCode"));
                    if (analy1) {
                        cashstorage.setStrFAnalysisCode1(rs.getString(
                            "FAnalysisCode1"));
                    } else {
                        cashstorage.setStrFAnalysisCode1(" ");
                    }
                    if (analy2) {
                        cashstorage.setStrFAnalysisCode2(rs.getString(
                            "FAnalysisCode2"));
                    } else {
                        cashstorage.setStrFAnalysisCode2(" ");
                    }
                    if (analy3) {
                        cashstorage.setStrFAnalysisCode3(rs.getString(
                            "FAnalysisCode3"));
                    } else {
                        cashstorage.setStrFAnalysisCode3(" ");
                    }
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    cashstorage.setStrAttrClsCode(rs.getString("fattrclscode"));
                 //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    //---MS00809 QDV4建行2009年11月13日01_B 蒋世超  添加 2009.11.19 外汇交易默认取调拨日期汇率进行估值----------------------------
                    if(YssOperCons.YSS_ZJDBLX_InnerAccount.equalsIgnoreCase(rs.getString("FTsfTypeCode"))&&YssOperCons.YSS_ZJDBZLX_COST_RateTrade.equalsIgnoreCase(rs.getString("FSubTsfTypeCode"))
                    		&& rateMode.equalsIgnoreCase("0")){	//MS00866 QDV4建行2009年12月14日01_AB  蒋世超 添加 2009.12.15 
                    	dBaseRate = this.getSettingOper().getCuryRate(dDate,
                                rs.getString("FCuryCode"), rs.getString("FPortCode"),
                                YssOperCons.YSS_RATE_BASE); 
                    	 rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
                    	 dPortRate = rateOper.getDPortRate();
                    }else{
                      dBaseRate = rs.getDouble("FBaseCuryRate");
                      dPortRate = rs.getDouble("FPortCuryRate");
                    }
                    //---MS00809 QDV4建行2009年11月13日01_B end ----------------------------------------------

                    cashstorage.setStrAccBalance(YssD.add(YssFun.toDouble(
                        cashstorage.getStrAccBalance()), rs.getDouble("FMoney")) +
                                                 "");
                    cashstorage.setStrBaseCuryBal(YssD.add(YssFun.toDouble(
                        cashstorage.getStrBaseCuryBal()),
                        this.getSettingOper().calBaseMoney(rs.getDouble("FMoney"),
                        dBaseRate)) + "");
                    cashstorage.setStrPortCuryBal(YssD.add(YssFun.toDouble(
                        cashstorage.getStrPortCuryBal()),
                        this.getSettingOper().calPortMoney(rs.getDouble("FMoney"),
                        dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCuryCode"), rs.getDate("FTransferDate"), rs.getString("FPortCode"))) + "");
                    cashstorage.setStrBaseCuryRate(dBaseRate + "");
                    cashstorage.setStrPortCuryRate(dPortRate + "");
                    hmEveStg.put(sKey, cashstorage);
                }

                //不能在此处将金额强制设置为0，因为如果前日余额＋当日现金发生额并不等于0，但是正好计算到某一条资金调拨记录时等于0了，这样就有可能出现前日余额＋当日现金发生额不等于当日现金余额的问题。胡昆  20080510
                //如果原币为0，那么基础货币金额，组合货币金额强制设置为0  胡昆  20071119
//            if (YssFun.toDouble(cashstorage.getStrAccBalance()) == 0) {
//               cashstorage.setStrBaseCuryBal("0");
//               cashstorage.setStrPortCuryBal("0");
//            }
            }

            iter = hmEveStg.keySet().iterator();
            while (iter.hasNext()) {
                sKey = (String) iter.next();
                cashstorage = (CashStorageBean) hmEveStg.get(sKey);
                cashstorage.setIntStorageState(0); //自动计算（未锁定）
                cashstorage.checkStateId = 1;
                all.add(cashstorage);
            }
            return all;
        } catch (Exception e) {
            throw new YssException("系统进行现金库存统计,在统计当日资金调拨时出现异常!\n", e); //by 曹丞 2009.01.22 统计当日资金调拨异常信息 MS00004 QDV4.1-2009.2.1_09A
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
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Cash") +
                " where FYearMonth = " + dbl.sqlString(YearMonth) +
                " and FPortCode in( " + operSql.sqlCodes(portCode) + ")"; //添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的 by leeyu 20090220 QDV4华夏2009年2月13日01_B MS00246
            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_Cash") +
                "(FCASHACCCODE,FYearMonth,FStorageDate,FPORTCODE," +
                "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3," +
                "FCURYCODE,FACCBALANCE,FBASECURYRATE,FBASECURYBAL," +
                "FPORTCURYRATE,FPORTCURYBAL,FSTORAGEIND,FCheckState," +
                "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" +
                "(select FCASHACCCODE," + dbl.sqlString(YearMonth) +
                " as FYearMonth, " +
                dbl.sqlDate(new Integer(Year).toString() + "-01-01") +
                " as FStorageDate,FPORTCODE," +
                "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3," +
                "FCURYCODE,FACCBALANCE,FBASECURYRATE,FBASECURYBAL," +
                "FPORTCURYRATE,FPORTCURYBAL,FSTORAGEIND,FCheckState," +
                dbl.sqlString(pub.getUserCode()) +
                " as FCREATOR," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " as FCREATETIME," +
                dbl.sqlString(pub.getUserName()) + " as FCHECKUSER," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " as FCHECKTIME" +
                " from " + pub.yssGetTableName("Tb_Stock_Cash") +
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
    
    //add by jiangshichao 2011.01.15 合并太平资产年终结账需求
	public void yearChange_TP(java.util.Date dDate, String portCode)
			throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		String YearMonth = "";
		int Year;
		ResultSet rs = null;
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			YearMonth = YssFun.getYear(dDate) + "00";
			Year = YssFun.getYear(dDate);
			strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Cash")
					+ " where FYearMonth = " + dbl.sqlString(YearMonth)
					+ " and FPortCode in( " + operSql.sqlCodes(portCode) + ")";// 添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的
																				// by
																				// leeyu
																				// 20090220
																				// QDV4华夏2009年2月13日01_B
																				// MS00246
			dbl.executeSql(strSql);
			// QDV4太平2010年12月20日01_A
			// 分别从9号报表，14号报表，18号报表取数据作为不同虚拟账户子类型的现金库存期初数数据。 add by jiangshichao
			// 2010.12.25
			insertVirtualAccData(dDate, portCode);
			// QDV4太平2010年12月20日01_A
			// 分别从9号报表，14号报表，18号报表取数据作为不同虚拟账户子类型的现金库存期初数数据。 end
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Stock_Cash")
					+ "(FCASHACCCODE,FYearMonth,FStorageDate,FPORTCODE,"
					+ "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,"
					+ "FCURYCODE,FACCBALANCE,FBASECURYRATE,FBASECURYBAL,"
					+ "FPORTCURYRATE,FPORTCURYBAL,FSTORAGEIND,FCheckState,"
					+ "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)"
					+ "(select FCASHACCCODE,"
					+ dbl.sqlString(YearMonth)
					+ " as FYearMonth, "
					+ dbl.sqlDate(new Integer(Year).toString() + "-01-01")
					+ " as FStorageDate,FPORTCODE,"
					+ "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,"
					+ "FCURYCODE,FACCBALANCE,FBASECURYRATE,FBASECURYBAL,"
					+ "FPORTCURYRATE,FPORTCURYBAL,FSTORAGEIND,FCheckState,"
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
					+
					// 需求编号:QDV4太平2010年12月20日01_A
					// '0411','0412','0414','0415'这几类虚拟账户根据09、14、18
					// 报表产生的为准 add by jiangshichao 2010.12.26
					" ( select * from "
					+ pub.yssGetTableName("Tb_Stock_Cash")
					+ " a where FYearMonth = "
					+ dbl.sqlString((Year - 1) + "12")
					+ " and FStorageDate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and FPortCode in( "
					+ portCode
					+ ")"
					+ " and not exists (select fcashacccode from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " b where fcheckstate=1 and fsubacctype in ('0411','0412','0414','0415') "
					+ " and FPortCode in (" + operSql.sqlCodes(portCode) + ")"
					+ " and a.fcashacccode = b.fcashacccode) " + ") )";

			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);

		} catch (Exception e) {
			throw new YssException("年度结转错误!\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			// dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/***********************************************************************
	 * 需求编号:QDV4太平2010年12月20日01_A 需求描述：分别从9号报表，14号报表，18号报表取数据作为
	 * 不同虚拟账户子类型的现金库存期初数数据。
	 * 
	 * @ author jiangshichao add by jiangshichao 2010.12.25
	 * 
	 * @throws YssException
	 */
	private void insertVirtualAccData(java.util.Date dDate, String portCode)
			throws YssException {

		try {
			// 1. 不存在该币种账户，那么在现金账户设置已审核界面新增该币种的账户数据。
			addVirtualAcc(dDate, portCode);

			dealFundInOutVirtualAccData(dDate, portCode);
			dealUnrealisedVirtualAccData(dDate, portCode);
			dealSummaryVirtualAccData(dDate, portCode);
		} catch (Exception e) {
			throw new YssException("生成虚拟账户期初数出错......");
		}
	}

	/*******************************************************************
	 * 
	 * 需求编号:QDV4太平2010年12月20日01_A 从9号报表 取数据作为虚拟账户子类型的现金库存期初数数据。
	 * （1）生成年初【账户类型04虚拟账户，账户子类型0415 Fund in out 】的现金库存数据。
	 * （2）生成年初【账户类型04虚拟账户，账户子类型0411前估值日投資成本 】的现金库存数据。
	 * 
	 * Tb_Data_FundInOut ftransdate 字段为12月31 FDESC = final(品种类型截取自该字段的最后两位)
	 * FPORTACCBALANCE 不为0 ---这个作为虚拟账户的原币库存金额(也作为基础、组合货币的金额)
	 * 
	 * tb_001_para_cashaccount 币种为港币 账户类型为虚拟账户 账户子类型为 0415、0411
	 * 
	 * @param dDate
	 * @param portCode
	 * @throws YssException
	 */
	private void dealFundInOutVirtualAccData(java.util.Date dDate,
			String portCode) throws YssException {

		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		String YearMonth = "";
		int Year;
		try {

			YearMonth = YssFun.getYear(dDate) + "00";
			Year = YssFun.getYear(dDate);

			// 1. 【09 报表 资金注入注出表 Tb_Data_FundInOut】 生成账户子类型 0415 的现金库存
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Stock_Cash")
					+ "(FCASHACCCODE,FYearMonth,FStorageDate,FPORTCODE,"
					+ "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,"
					+ "FCURYCODE,FACCBALANCE,FBASECURYRATE,FBASECURYBAL,"
					+ "FPORTCURYRATE,FPORTCURYBAL,FSTORAGEIND,FCheckState,"
					+ "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)"
					+ "(select c.fcashacccode as FCASHACCCODE,"
					+ dbl.sqlString(YearMonth)
					+ " as FYearMonth, "
					+ dbl.sqlDate(new Integer(Year).toString() + "-01-01")
					+ " as FStorageDate, c.fportcode as FPORTCODE,"
					+ " ' ' as FANALYSISCODE1,c.FCatType as FANALYSISCODE2,' ' as FANALYSISCODE3,"
					+ " c.fcurycode as FCURYCODE,"
					+ " c.FPORTACCBALANCE as FACCBALANCE,1 as FBASECURYRATE,c.FPORTACCBALANCE as FBASECURYBAL,"
					+ " 1 as FPORTCURYRATE,c.FPORTACCBALANCE as FPORTCURYBAL,0 as FSTORAGEIND,1 as  FCheckState,"
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
					+
					// -----------------------------------------------------------------------------------------//
					/*************************************************************
					 * 09报表取数原则： tb_data_fundinout FPORTACCBALANCE <> 0
					 * substr(fdesc,0,instr(fdesc,'-')-2)='Final' ftransdate =
					 * 年末 fportcode in(前台所选组合)
					 */
					" (select a1.*,a2.fcashacccode,a2.fcurycode from "
					+ " (select fportcode,substr(fdesc,instr(fdesc,'-')+2) as FCatType,FPORTACCBALANCE from tb_data_fundinout "
					+ " where FPORTACCBALANCE <> 0 and substr(fdesc,0,instr(fdesc,'-')-2)='Final'  and fportcode in ( "
					+ operSql.sqlCodes(portCode)
					+ ") "
					+ " and ftransdate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " )a1"
					+ " left join "
					+ " ( select fcashacccode,fportcode,fcurycode from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " where fcheckstate = 1 "
					+ "  and fportcode in ("
					+ operSql.sqlCodes(portCode)
					+ ") and facctype = '04' and fsubacctype ='0415' and fcurycode = 'HKD')a2 "
					+ " on a1.fportcode = a2.fportcode"
					+
					// -----------------------------------------------------------------------------------------//
					" union all "
					+

					" select b1.*,b2.fcashacccode,b2.fcurycode  from "
					+ " (select fportcode,substr(fdesc,instr(fdesc,'-')+2) as FCatType,FPORTACCBALANCE from tb_data_fundinout "
					+ " where FPORTACCBALANCE <> 0 and substr(fdesc,0,instr(fdesc,'-')-2)='Final'  and ftransdate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and fportcode in ( "
					+ operSql.sqlCodes(portCode)
					+ ") )b1 "
					+ " left join "
					+ " ( select fcashacccode,fportcode,fcurycode from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " where fcheckstate = 1 "
					+ " and fportcode in ("
					+ operSql.sqlCodes(portCode)
					+ ") and facctype = '04'    and fsubacctype ='0411' and fcurycode = 'HKD')b2"
					+ " on b1.fportcode = b2.fportcode) c" +
					// -----------------------------------------------------------------------------------------//
					")";

			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException(
					"处理现金库存年终结转时，从【09资金注入注出】取数作为虚拟账户的期初数时出错......");
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	/*******************************************************************
	 * 需求编号:QDV4太平2010年12月20日01_A 生成年初【账户类型04虚拟账户，账户子类型0414未兌現資產(滙兌)】的现金库存数据
	 * 
	 * 
	 * tb_001_Data_Unrealised ----> FCURYCODE,FPORTCODE fdate : 12月31日 FName :.
	 * Year-to-date difference
	 * 
	 * 注意： 1.
	 * 不存在该币种账户，那么在现金账户设置已审核界面新增该币种的账户数据。账户代码和名称的命名规则为：“组合代码”-“币种代码”未兌現資產(滙兌) 2.
	 * 品种类型为【FCATCODE】，假如没有相应的品种类型，该字段在界面上可不显示，可为空。 3.
	 * 原币金额：FBAL字段的值，假如同一币种、同一品种类型下有多条数据，需汇总数据后保存在一条数据中
	 * 基础货币金额：FBASECURYBAL字段的值，假如同一币种
	 * 、同一品种类型下有多条数据，需汇总数据后保存在一条数据中（注：该值等于组合货币金额，可直接取组合货币金额）
	 * tb_001_para_cashaccount ---->FCURYCODE,FPORTCODE 账户类型为虚拟账户 账户子类型为 0414
	 * 
	 * 
	 * @param dDate
	 * @param portCode
	 * @throws YssException
	 */
	private void dealUnrealisedVirtualAccData(java.util.Date dDate,
			String portCode) throws YssException {

		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		String YearMonth = "";
		int Year;
		try {
			YearMonth = YssFun.getYear(dDate) + "00";
			Year = YssFun.getYear(dDate);

			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Stock_Cash")
					+ "(FCASHACCCODE,FYearMonth,FStorageDate,FPORTCODE,"
					+ "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,"
					+ "FCURYCODE,FACCBALANCE,FBASECURYRATE,FBASECURYBAL,"
					+ "FPORTCURYRATE,FPORTCURYBAL,FSTORAGEIND,FCheckState,"
					+ "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)"
					+
					// -----------------------------------------------------------------------------//
					"(select c.fcashacccode as FCASHACCCODE,"
					+ dbl.sqlString(YearMonth)
					+ " as FYearMonth, "
					+ dbl.sqlDate(new Integer(Year).toString() + "-01-01")
					+ " as FStorageDate, c.fportcode as FPORTCODE,"
					+ " ' ' as FANALYSISCODE1,"
					+ " case when c.fcatcode ='RecPay' then 'OT' when c.fcatcode ='01' then 'DE' else c.fcatcode end as FANALYSISCODE2,"
					+ "  ' ' as FANALYSISCODE3,"
					+ " c.fcurycode as FCURYCODE,"
					+ " 0 as FACCBALANCE,0 as FBASECURYRATE,0 as FBASECURYBAL,"
					+ " 0 as FPORTCURYRATE,0 as FPORTCURYBAL,0 as FSTORAGEIND,1 as  FCheckState,"
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
					+
					// -----------------------------------------------------------------------------//
					" (select a.*,b.fcashacccode from "
					+
					// " (select a1.fportcode,a1.fcurycode,a1.fbal,a1.fbasecurybal,nvl(a2.fcatcode,' ') as fcatcode from "+
					" (select fportcode,fcurycode,fcatcode,sum(fbal) as fbal ,sum(fbasecurybal) as fbasecurybal from  "
					+ pub.yssGetTableName("tb_Data_Unrealised")
					+ " where fdate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and fname = '.  Year-to-date difference' and fbal<>0 and Funrealisedtype =4 "
					+ " and fportcode in ("
					+ operSql.sqlCodes(portCode)
					+ ") "
					+ " group by fportcode,fcurycode,fcatcode)a "
					+ " left join "
					+ " (select fcashacccode,fcurycode,fportcode from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " where fportcode in ("
					+ operSql.sqlCodes(portCode)
					+ ") and facctype = '04' and fsubacctype = '0414')b on  a.fcurycode = b.fcurycode and a.fportcode = b.fportcode)c )";
			// -----------------------------------------------------------------------------//
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException(
					"处理现金库存年终结转时，从【14未兑现资产本金增值贬值分布表（汇兑）】取数作为虚拟账户的期初数时出错......");
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/*********************************************************************
	 * 不存在该币种账户，那么在现金账户设置已审核界面新增该币种的账户数据。
	 * 
	 * @param dDate
	 * @param portCode
	 * @throws YssException
	 */
	private void addVirtualAcc(java.util.Date dDate, String portCode)
			throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		String YearMonth = "";
		int Year;
		boolean addFlag = false;
		ResultSet rs = null;
		try {
			YearMonth = YssFun.getYear(dDate) + "00";
			Year = YssFun.getYear(dDate);
			// ------------------------- 虚拟账户 创建
			// -----------------------------------------------------------------//
			// 1. 判断0414、0412、0415、0411 虚拟账户是否都存在
			strSql = " select * from "
					+
					// =======================================================================================//
					" (select a.*,b.fcashacccode from "
					+ " (select fportcode,fcurycode  from  "
					+ pub.yssGetTableName("tb_Data_Unrealised")
					+ " where fdate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and fname = '.  Year-to-date difference' and fportcode in("
					+ operSql.sqlCodes(portCode)
					+ ")"
					+ " group by fportcode,fcurycode)a"
					+ " left join "
					+ " (select * from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " where fportcode in("
					+ operSql.sqlCodes(portCode)
					+ ") and facctype = '04' "
					+ " and fsubacctype = '0414')b on a.fcurycode = b.fcurycode and a.fportcode = b.fportcode   "
					+
					// ----以上是根据第14张报表进行对虚拟账户0414是否存在进行判断
					// --------------------------------------//
					" union all "
					+
					// -----
					" select a1.*, a2.fcashacccode, a2.fcurycode from "
					+ " (select fportcode from tb_data_fundinout where FPORTACCBALANCE <> 0 and substr(fdesc, 0, instr(fdesc, '-') - 2) = 'Final' "
					+ "  and fportcode in ("
					+ operSql.sqlCodes(portCode)
					+ ") and ftransdate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ ") a1 "
					+ " left join "
					+ " (select fcashacccode, fportcode, fcurycode from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " where fcheckstate = 1 and fcurycode = 'HKD' "
					+ " and fportcode in ("
					+ operSql.sqlCodes(portCode)
					+ ") and facctype = '04' and fsubacctype = '0415' ) a2 on a1.fportcode = a2.fportcode "
					+
					// ----以上是根据第09张报表进行对虚拟账户0415是否存在进行判断
					// --------------------------------------//
					" union all "
					+
					// ---------------------------------------------------------------------------------------------//
					" select b1.*, b2.fcashacccode, b2.fcurycode from "
					+ " (select fportcode  from tb_data_fundinout where FPORTACCBALANCE <> 0 and substr(fdesc, 0, instr(fdesc, '-') - 2) = 'Final' "
					+ " and ftransdate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and fportcode in ("
					+ operSql.sqlCodes(portCode)
					+ ") )b1 "
					+ " left join "
					+ " (select fcashacccode, fportcode, fcurycode from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " where fcheckstate = 1 and fportcode in ("
					+ operSql.sqlCodes(portCode)
					+ ") "
					+ " and facctype = '04' and fsubacctype = '0411' and fcurycode = 'HKD') b2 on b1.fportcode = b2.fportcode "
					+
					// ----以上是根据第09张报表进行对虚拟账户0411是否存在进行判断
					// --------------------------------------//
					"union all "
					+
					// ---------------------------------------------------------------------------------------------//
					" select a.* ,b.fcashacccode,b.fcurycode from  "
					+ " (select fportcode from  "
					+ pub.yssGetTableName("tb_data_Summary")
					+ " where fportcode in ("
					+ operSql.sqlCodes(portCode)
					+ ") and fdate ="
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ "  and fcode='CurrAccumulated' and fbasecurybal<>0 )a   "
					+ " left join   "
					+ " (select fcashacccode,fcurycode,fportcode from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " where fcheckstate = 1 and fportcode  in ("
					+ operSql.sqlCodes(portCode)
					+ ") and fcurycode = 'HKD' and facctype = '04' and fsubacctype ='0412')b   "
					+ " on a.fportcode = b.fportcode  ) " +
					// ----以上是根据第18张报表进行对虚拟账户0412是否存在进行判断
					// --------------------------------------//
					" where fcashacccode is null";
			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				addFlag = true;
			}
			dbl.closeResultSetFinal(rs);

			if (addFlag) {
				// 如果不存在，则创建虚拟账户
				conn.setAutoCommit(false);
				bTrans = true;
				strSql = " insert into "
						+ pub.yssGetTableName("Tb_Para_CashAccount")
						+ "(FCashAccCode, FCashAccName,FStartDate, FAccType, FSubAccType, FBankCode, FBankAccount, FCuryCode, FMatureDate, "
						+ " FState, FPortCode, FFormulaCode, FRoundCode, FPeriodCode, FInterestCycle, FInterestOrigin,FFixRate, FDesc,"
						+ " FCheckState,FCreator,FCreateTime,FCheckUser,FCHECKTIME,FInterestWay,FDepDurCode,FAccAttr)"
						+
						// -----------------------------------------------------------------------------------------------------------------//
						" (select  "
						+ "  case  when c.fsubacctype ='0414' then c.fportcode||'-'||c.fcurycode||' 未兌現資產'  "
						+ "  when c.fsubacctype ='0411' then  c.fportcode||'-'||'HKD 投資成本' "
						+ "  when c.fsubacctype ='0412' then  c.fportcode||'-資產變動' "
						+ "  when c.fsubacctype ='0415' then  c.fportcode||'-'||'HKD資金流入/流出' "
						+ "  end as FCashAccCode,"
						+ "  case  when c.fsubacctype ='0414' then c.fportcode||'-'||c.fcurycode||' 未兌現資產(滙兌)'   "
						+ "  when c.fsubacctype ='0411' then  c.fportcode||'-'||'HKD 投資成本 '"
						+ "  when c.fsubacctype ='0412' then  c.fportcode||'-資產變動' "
						+ "  when c.fsubacctype ='0415' then  c.fportcode||'-'||'HKD資金流入/流出' "
						+ "  end as FCashAccName,"
						+
						// " c.fportcode||'-'||c.fcurycode||' 未兌現資產(滙兌)'  as FCashAccName,"
						// +
						" to_date('2007-1-1','yyyy-mm-dd') as FStartDate,'04' as FAccType,c.fsubacctype as  FSubAccType, '' as FBankCode, '' as FBankAccount,  "
						+ " case when c.fsubacctype ='0414' then c.fcurycode else 'HKD' end as FCuryCode, to_date('1900-1-1','yyyy-mm-dd') as FMatureDate,0 as FState, c.fportcode as FPortCode,'' as FFormulaCode, "
						+ " 'R001' as FRoundCode, '004' as FPeriodCode,0 as FInterestCycle, 0 as FInterestOrigin,0 as FFixRate,'' as FDesc,1 as FCheckState,"
						+ dbl.sqlString(pub.getUserCode())
						+ " as FCREATOR,"
						+ dbl.sqlString(YssFun
								.formatDatetime(new java.util.Date()))
						+ " as FCREATETIME,"
						+ dbl.sqlString(pub.getUserName())
						+ " as FCHECKUSER,"
						+ dbl.sqlString(YssFun
								.formatDatetime(new java.util.Date()))
						+ " as FCHECKTIME,"
						+ " 1 as FInterestWay,''as FDepDurCode,1 as FAccAttr "
						+ " from "
						+ " (select * from "
						+
						// =======================================================================================//
						" (select a.*,b.fcashacccode,'0414' as fsubacctype from "
						+ " (select fportcode,fcurycode  from  "
						+ pub.yssGetTableName("tb_Data_Unrealised")
						+ " where fdate = "
						+ dbl.sqlDate(new Integer(Year - 1).toString()
								+ "-12-31")
						+ " and fname = '.  Year-to-date difference' and fportcode in("
						+ operSql.sqlCodes(portCode)
						+ ")"
						+ " group by fportcode,fcurycode)a"
						+ " left join "
						+ " (select * from "
						+ pub.yssGetTableName("tb_para_cashaccount")
						+ " where fportcode in("
						+ operSql.sqlCodes(portCode)
						+ ") and facctype = '04' "
						+ " and fsubacctype = '0414')b on a.fcurycode = b.fcurycode and a.fportcode = b.fportcode   "
						+
						// ----以上是根据第14张报表进行对虚拟账户0414是否存在进行判断
						// --------------------------------------//
						" union all "
						+
						// -----
						" select a1.*, a2.fcashacccode, a2.fcurycode,'0415' as fsubacctype from "
						+ " (select fportcode from tb_data_fundinout where FPORTACCBALANCE <> 0 and substr(fdesc, 0, instr(fdesc, '-') - 2) = 'Final' "
						+ "  and fportcode in ("
						+ operSql.sqlCodes(portCode)
						+ ") and ftransdate = "
						+ dbl.sqlDate(new Integer(Year - 1).toString()
								+ "-12-31")
						+ ") a1 "
						+ " left join "
						+ " (select fcashacccode, fportcode, fcurycode from "
						+ pub.yssGetTableName("tb_para_cashaccount")
						+ " where fcheckstate = 1 and fcurycode = 'HKD' "
						+ " and fportcode in ("
						+ operSql.sqlCodes(portCode)
						+ ") and facctype = '04' and fsubacctype = '0415' ) a2 on a1.fportcode = a2.fportcode "
						+
						// ----以上是根据第09张报表进行对虚拟账户0415是否存在进行判断
						// --------------------------------------//
						" union all "
						+
						// ---------------------------------------------------------------------------------------------//
						" select b1.*, b2.fcashacccode, b2.fcurycode,'0411' as fsubacctype from "
						+ " (select fportcode  from tb_data_fundinout where FPORTACCBALANCE <> 0 and substr(fdesc, 0, instr(fdesc, '-') - 2) = 'Final' "
						+ " and ftransdate = "
						+ dbl.sqlDate(new Integer(Year - 1).toString()
								+ "-12-31")
						+ " and fportcode in ("
						+ operSql.sqlCodes(portCode)
						+ ") )b1 "
						+ " left join "
						+ " (select fcashacccode, fportcode, fcurycode from "
						+ pub.yssGetTableName("tb_para_cashaccount")
						+ " where fcheckstate = 1 and fportcode in ("
						+ operSql.sqlCodes(portCode)
						+ ") "
						+ " and facctype = '04' and fsubacctype = '0411' and fcurycode = 'HKD') b2 on b1.fportcode = b2.fportcode "
						+
						// ----以上是根据第09张报表进行对虚拟账户0411是否存在进行判断
						// --------------------------------------//
						"union all "
						+
						// ---------------------------------------------------------------------------------------------//
						" select a.* ,b.fcashacccode,b.fcurycode,'0412' as fsubacctype  from  "
						+ " (select fportcode from  "
						+ pub.yssGetTableName("tb_data_Summary")
						+ " where fportcode in ("
						+ operSql.sqlCodes(portCode)
						+ ") and fdate ="
						+ dbl.sqlDate(new Integer(Year - 1).toString()
								+ "-12-31")
						+ "  and fcode='CurrAccumulated' and fbasecurybal<>0 )a   "
						+ " left join   "
						+ " (select fcashacccode,fcurycode,fportcode from "
						+ pub.yssGetTableName("tb_para_cashaccount")
						+ " where fcheckstate = 1 and fportcode  in ("
						+ operSql.sqlCodes(portCode)
						+ ") and fcurycode = 'HKD' and facctype = '04' and fsubacctype ='0412')b   "
						+ " on a.fportcode = b.fportcode  ) " +
						// ----以上是根据第18张报表进行对虚拟账户0412是否存在进行判断
						// --------------------------------------//
						" where fcashacccode is null)c)";
				// " (select a.*,b.fcashacccode from " +
				// " (select fportcode,fcurycode from "+pub.yssGetTableName("tb_Data_Unrealised")+" where fdate = "
				// +dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")+
				// " and fname = '.  Year-to-date difference' and fportcode in ("+operSql.sqlCodes(portCode)+") group by fportcode,fcurycode )a "
				// +
				// " left join " +
				// " (select * from  "+pub.yssGetTableName("tb_para_cashaccount")+
				// " where fportcode in ("+operSql.sqlCodes(portCode)+") and facctype = '04' and fsubacctype = '0414')b on "
				// +
				// " a.fcurycode = b.fcurycode and a.fportcode = b.fportcode where fcashacccode is null)c )";

				dbl.executeSql(strSql);
				conn.commit();
				bTrans = false;
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			throw new YssException("处理【14未兑现资产本金增值贬值分布表（汇兑）】时创建虚拟账户出错......");
		} finally {
			if (addFlag) {
				dbl.endTransFinal(conn, bTrans);
			}
		}
	}

	/********************************************************************
	 * 需求编号:QDV4太平2010年12月20日01_A 生成年初【账户类型04虚拟账户，账户子类型0412 前期滚存金额】的现金库存数据。
	 * 
	 * tb_001_data_Summary fdate字段为12月31日 FCode字段为CurrAccumulated FPORTCODE
	 * 
	 * tb_001_para_cashaccount 币种为港币 账户类型为虚拟账户 账户子类型为0412
	 * 
	 * @param dDate
	 * @param portCode
	 * @throws YssException
	 */
	private void dealSummaryVirtualAccData(java.util.Date dDate, String portCode)
			throws YssException {

		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		String strSql = "";
		String YearMonth = "";
		int Year;
		try {
			YearMonth = YssFun.getYear(dDate) + "00";
			Year = YssFun.getYear(dDate);

			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Stock_Cash")
					+ "(FCASHACCCODE,FYearMonth,FStorageDate,FPORTCODE,"
					+ "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,"
					+ "FCURYCODE,FACCBALANCE,FBASECURYRATE,FBASECURYBAL,"
					+ "FPORTCURYRATE,FPORTCURYBAL,FSTORAGEIND,FCheckState,"
					+ "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)"
					+
					// -----------------------------------------------------------------//
					"(select c.fcashacccode as FCASHACCCODE,"
					+ dbl.sqlString(YearMonth)
					+ " as FYearMonth, "
					+ dbl.sqlDate(new Integer(Year).toString() + "-01-01")
					+ " as FStorageDate,c.fportcode as FPORTCODE,"
					+ " ' ' asFANALYSISCODE1,' ' as FANALYSISCODE2,' ' as FANALYSISCODE3,"
					+ " c.fcurycode as FCURYCODE,c.fbasecurybal FACCBALANCE,1 as FBASECURYRATE, c.fbasecurybal as FBASECURYBAL,"
					+ "  1 as FPORTCURYRATE, c.fbasecurybal as FPORTCURYBAL,0 as FSTORAGEIND,1 as FCheckState,"
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
					+

					// ----------------------------------------------------------------//
					" from "
					+ " (select a.* ,b.fcashacccode,b.fcurycode from "
					+ " (select fportcode,fbasecurybal,fattrclscode   from "
					+ pub.yssGetTableName("tb_data_Summary")
					+ " where fportcode in ("
					+ operSql.sqlCodes(portCode)
					+ ") and fdate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and fcode='CurrAccumulated'"
					+ "  and fbasecurybal<>0)a "
					+ " left join "
					+ " (select fcashacccode,fcurycode,fportcode from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " where fcheckstate = 1 and fportcode  in ("
					+ operSql.sqlCodes(portCode)
					+ ") and fcurycode = 'HKD' and facctype = '04' and fsubacctype ='0412')b"
					+ " on a.fportcode = b.fportcode)c " +
					// -----------------------------------------------------------------//
					")";

			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException(
					"处理现金库存年终结转时，从【18资金投资汇总表】取数作为虚拟账户的期初数时出错......");
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
    
    /**
     * 在统计之前将回购的资金调拨插入 sj 20080217
     * @param dDate Date
     * @throws YssException
     */
    protected void beforeStatStorage(java.util.Date dDate) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        StatPurchaseIns purchaseIncome = null;
        double exchageIncome = 0D;
        //---add by songjie 2011.06.03 BUG 2012 QDV4中银基金2011年5月31日02_B---//
        double dBaseRate = 1;
        double dPortRate = 1;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //---add by songjie 2011.06.03 BUG 2012 QDV4中银基金2011年5月31日02_B---//
        try {
			strSql = "select a.FSecurityCode,a.FAccruedinterest as FPurchaseGain,a.FPortCode as FPortCode,"
					+ " a.FTradeTypeCode as FTradeTypeCode,"//利息加上成交金额 by leeyu 20100327
					+ " a.FTradeMoney as FTradeMoney,"
					+ " a.FInvMgrCode as FInvMgrCode,"
					+ " a.FBrokerCode as FBrokerCode,"
					+ " a.FBaseCuryRate as FBaseCuryRate,"
					+ " a.FPortCuryRate as FPortCuryRate,"
					+ " a.FInvestType as FInvestType,"
					+ " a.FMatureDate as FMatureDate,"
					+ " a.FNum as FNum,"
					+ " a.FMatureSettleDate as FMatureSettleDate,"
					+ " a.FCashAccCode as FCashAccCode,"

					+ " a.FTradeFee1 as FTradeFee1,"
					+ " a.FTradeFee2 as FTradeFee2,"
					+ " a.FTradeFee3 as FTradeFee3,"
					+ " a.FTradeFee4 as FTradeFee4,"
					+ " a.FTradeFee5 as FTradeFee5,"
					+ " a.FTradeFee6 as FTradeFee6,"
					+ " a.FTradeFee7 as FTradeFee7,"
					+ " a.FTradeFee8 as FTradeFee8,"
					+
				//---edit by songjie 2011.06.03 BUG 2012 QDV4中银基金2011年5月31日02_B---//
            	"'exchange' as FType,port.Fportcury as FPortCury,sec.FTradeCury as FTradeCury from " + 
            	pub.yssGetTableName("Tb_Data_SubTrade") +
            	//---edit by songjie 2011.06.03 BUG 2012 QDV4中银基金2011年5月31日02_B---//
                " a left join (select FFeeCode,FAccountingWay as FAW1,FAssumeMan as FAM1 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") b on a.FFeeCode1 = b.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW2,FAssumeMan as FAM2 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") c on a.FFeeCode2 = c.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW3,FAssumeMan as FAM3 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") d on a.FFeeCode3 = d.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW4,FAssumeMan as FAM4 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") e on a.FFeeCode4 = e.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW5,FAssumeMan as FAM5 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f on a.FFeeCode5 = f.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW6,FAssumeMan as FAM6 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") g on a.FFeeCode6 = g.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW7,FAssumeMan as FAM7 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") h on a.FFeeCode7 = h.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW8,FAssumeMan as FAM8 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") i on a.FFeeCode8 = i.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FSecurityCode as FSecurityCode_j,FCatCode,FSubCatCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") j on a.FSecurityCode = j.FSecurityCode_j" +
                //--------------------------------------------------------
                //" left join (select FSecurityCode as FSecurityCode_k,FMultiple,FBailType,FBailScale,FBailFix from " +
                //pub.yssGetTableName("Tb_Para_IndexFutures") +
                //") k on a.FSecurityCode = k.FSecurityCode_k" +
                " left join (select FCashAccCode as FCashAccCode_c,FCuryCode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) CashAcc on a.FCashAccCode = CashAcc.FCashAccCode_c " +
                //---edit by songjie 2011.06.03 BUG 2012 QDV4中银基金2011年5月31日02_B---//
                " left join (select FPortcode, FPortCury from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) port on a.Fportcode = port.Fportcode " +
                " left join (select fsecuritycode, FTradeCury from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) sec on sec.fsecuritycode = a.fsecuritycode " +
                //---edit by songjie 2011.06.03 BUG 2012 QDV4中银基金2011年5月31日02_B---//
                " where (FMatureDate " +
                " between " + dbl.sqlDate(dDate) + " and " +
                dbl.sqlDate(dDate) +
                //edit by songjie 2011.06.16 资产估值报未明确到列错误
                " ) and FCheckState = 1 and a.FPortCode in (" + portCodes +
                ") order by FNum";

            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getString("FTradeTypeCode").equalsIgnoreCase("24") ||
                    rs.getString("FTradeTypeCode").equalsIgnoreCase("25")) {
                    //2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
                    //增加事务控制和锁，以免在多用户同时处理时出现调拨编号重复
                    conn.setAutoCommit(false);
                    bTrans = true;
                    
                    //---add by songjie 2011.06.03 BUG 2012 QDV4中银基金2011年5月31日02_B---//
                    dBaseRate = 1;
                    if (!rs.getString("FTradeCury").equalsIgnoreCase(pub.
                    		getPortBaseCury(rs.getString("FPortCode")))) {
                        dBaseRate = this.getSettingOper().getCuryRate(dDate,
                            "",
                            "",
                            "",
                            "",
                            rs.getString("FTradeCury"), rs.getString("FPortCode"),
                            YssOperCons.YSS_RATE_BASE);
                    }

                    if (rs.getString("FPortCury") == null) {
                        throw new YssException("请检查投资组合【" +
                        		rs.getString("FPortCode") +
                            "】的币种设置！");
                    }
                    //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 ---
                    rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"), 
                    		rs.getString("FPortCode"), "", "","", ""); //用通用方法，获取组合汇率
                    dPortRate = rateOper.getDPortRate(); //获取组合汇率
                    
                    if(dPortRate == 0){
                    	dPortRate = 1;
                    }
                    //---add by songjie 2011.06.03 BUG 2012 QDV4中银基金2011年5月31日02_B---//
//                    purchaseIncome = new StatPurchaseIns();
//                    purchaseIncome.setYssPub(pub);
//                    purchaseIncome.setPortCodes(rs.getString("FPortCode"));
//                    purchaseIncome.getPurchaseParams();
//                    
//                    exchageIncome = purchaseIncome.calcPurcchaseIncomeUtil(rs);
                    dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer"));
                    dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));
                    insertTransfer(rs.getDate("FMatureDate"),
                                   rs.getDate("FMatureSettleDate"),
                                   rs.getString("FNum"),
                                   rs.getString("FPortCode"),
                                   rs.getString("FCashAccCode"),
                                   rs.getString("FInvMgrCode"),
                                   rs.getString("FBrokerCode"), "",
                                   rs.getString("FTradeTypeCode"),
                                   YssD.add(rs.getDouble("FTradeMoney"),
                                		   rs.getDouble("FPurchaseGain")),
                    //---edit by songjie 2011.06.03 BUG 2012 QDV4中银基金2011年5月31日02_B---//
                                   dBaseRate,
                                   dPortRate);
                    //---edit by songjie 2011.06.03 BUG 2012 QDV4中银基金2011年5月31日02_B---//
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
            }
        } catch (Exception e) {
            throw new YssException("系统进行现金库存统计,在统计现金库存的前期处理工作时出现异常!\n", e); //by 曹丞 2009.01.22 统计现金库存的前期处理工作异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 统计库存之后将原币金额为 0 的数据的基础货币余额和组合货币余额都设为0
     * 2008-05-10 蒋锦
     * @param dDate Date
     * @throws YssException
     */
    protected void afterSaveStorage(java.util.Date dDate) throws YssException {
        Connection conn = dbl.loadConnection();
        String strUpdate = "";
        boolean bTrans = false;
        try {
            strUpdate = "UPDATE " + pub.yssGetTableName("Tb_Stock_Cash") +
                " SET FPortCuryBal = 0, " +
                " FBaseCuryBal = 0 " +
                " WHERE FStorageDate = " + dbl.sqlDate(dDate) +
                " AND FPortCode IN (" + this.portCodes + ")" +
                " AND " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'" +
                " AND FAccBalance = 0";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strUpdate);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 插入资金调拨 sj 20080217
     * @param dTransDate Date
     * @param dTransferDate Date
     * @param sFNum String
     * @param sPortCode String
     * @param sCashAccCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param sTradeType String
     * @param dMoney double
     * @param dBaseRate double
     * @param dPortRate double
     * @throws YssException
     */
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
        if (sTradeType.equalsIgnoreCase("24") || sTradeType.equalsIgnoreCase("25")) { //正回购或逆回购
            transferadmin.insert("", "", sFNum, "REMature");
        }
        //else if (sTradeType.equalsIgnoreCase("21")) {
        //transferadmin.insert(dTransferDate, dTransDate,
        //"02", "02FU",
        //"", 0);
    }

    /**
     * sj  add 设置资金调拨
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
     * sj add 设置子调拨
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

    /*
        public ArrayList getStorageStatData(java.util.Date dDate) throws
          YssException {
       String strSql = "", strTmpSql = "", strTmpSql1 = "", strTmpSql2 = "";
       ResultSet rs = null;
       CashStorageBean cashstorage = null;
       String sPortCuryCode = "", sExRateSrcCode = "", sTradeCuryCode = "",
             sExRateCode = "";
       String strError = "统计现金库存出错";
       ArrayList all = new ArrayList();

       boolean analy1; //判断是否需要用分析代码；杨
       boolean analy2;

       double baseMoney = 0;
       double portMoney = 0;

       String sKey = "";

       double dBaseRate = 1;
       double dPortRate = 1;

       try {
          analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
          analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");

          if (portCodes.length() > 0) {
             strTmpSql = " and FPortCode in (" + portCodes + ")";
             strTmpSql2 = " in (" + portCodes + ")";
          }
          else {
             strTmpSql = " and FPortCode in ( select db.FPortCode from (select FPortCode, max(FStartDate) as FStartDate from " +
                   pub.yssGetTableName("Tb_Para_Portfolio") +
                   " where FStartDate <= " + dbl.sqlDate(dDate) +
                   " and FCheckState = 1 and Fassetgroupcode='" +
                   pub.getAssetGroupCode() +
     "' group by FPortCode) da join (select FPortCode, FPortName, FStartDate from " +
                   pub.yssGetTableName("Tb_Para_Portfolio") +
     ") db on da.FPortCode = db.FPortCode and da.FStartDate = db.FStartDate ";

          }

//         for (int j = 0; j <= YssFun.dateDiff(dStartDate, dEndDate); j++) {
//            dDate = YssFun.addDay(dStartDate, j);
          BaseOperDeal baseOperBean = (BaseOperDeal) pub.getOperDealCtx().
                getBean("baseoper");
          baseOperBean.setYssPub(pub);
          if (YssFun.getMonth(dDate) == 1 &&
              YssFun.getDay(dDate) == 1) {
             strTmpSql1 = strTmpSql + " and fyearmonth ='" +
                   YssFun.formatDate(dDate, "yyyy") + "00'";
          }
          else {
             strTmpSql1 = strTmpSql + " and fyearmonth <>'" +
                   YssFun.formatDate(dDate, "yyyy") + "00'" +
                   " and FStorageDate = " +
                   dbl.sqlDate(YssFun.addDay(dDate, -1));
          }

          strSql = "select * from ( select FTransferDate, FTsfPortCode,FTsfCashAccCode,sum(FTsfMoney) as FTsfMoney," +
                " sum(Round(FTsfMoney*FTsfBaseCuryRate,2)) as FTsfBaseMoney, " +//算资金调拨的基础货币金额时保留2位小数  胡昆 20070907
                " sum(Round(FTsfMoney*FTsfBaseCuryRate/FTsfPortCuryrate,2)) as FTsfPortMoney " +//算资金调拨的组合货币金额时保留2位小数  胡昆 20070907
                (analy1 ? ",FTsfAnalysisCode1" : " ") +
                (analy2 ? ",FTsfAnalysisCode2" : " ") +
                " from(" +
     " select m.FTransferDate, n.* from (select FNum, FTransferDate from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FCheckState = 1 and FTransferDate=" +
                dbl.sqlDate(dDate) +
                ") m join(select FNum, FCashAccCode as FTsfCashAccCode, " +
                " FPortCode as FTsfPortCode" +
                (analy1 ? ("," + dbl.sqlIsNull("FAnalysisCode1", "' '") +
                           " as FTsfAnalysisCode1") : " ") +
                (analy2 ? ("," + dbl.sqlIsNull("FAnalysisCode2", "' '") +
                           " as FTsfAnalysisCode2") : " ") +
                ",FMoney * FInOut as FTsfMoney, " +
                " (case when FBaseCuryRate=0 then 1 else FBaseCuryRate end) as FTsfBaseCuryRate," +
                " (case when FPortCuryrate=0 then 1 else FPortCuryrate end) as FTsfPortCuryrate" +
                " from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FCheckState = 1 and FPortCode  " + strTmpSql2 +
                ") n  on m.FNum = n.FNum" +
                " ) group by FTransferDate,FTsfPortCode,FTsfCashAccCode" +
                (analy1 ? ",FTsfAnalysisCode1" : " ") +
                (analy2 ? ",FTsfAnalysisCode2" : " ") +
                " ) y ";

          strSql = strSql + " full join (select * from ( select " +
                dbl.sqlDate(dDate) +
     " as FOperDate, FCashAccCode, FStorageDate, FPortCode, FCuryCode, " +
                " sum(" + dbl.sqlIsNull("FAccBalance", "0") +
                ") as FAccBalance, " +
                dbl.sqlIsNull("a.FPortCuryRate", "1") +
                " as FPortCuryRate ," +
                " sum(" + dbl.sqlIsNull("a.FPortCuryBal", "0") +
                ") as FPortCuryBal, " +
                dbl.sqlIsNull("a.FBaseCuryRate", "1") +
                " as FBaseCuryRate, " +
                " sum(" + dbl.sqlIsNull("a.FBaseCuryBal", "0") +
                ") as FBaseCuryBal " +
                (analy1 ? ("," + dbl.sqlIsNull("a.FAnalysisCode1", "' '") +
                           " as FAnalysisCode1 ") : " ") +
                (analy2 ? ("," + dbl.sqlIsNull("a.FAnalysisCode2", "' '") +
                           " as FAnalysisCode2 ") : " ") +
                " from " + pub.yssGetTableName("Tb_Stock_Cash") +
                " a where FCheckState = 1 " + strTmpSql1 +
                //由于现有库存中的数据，都是存在分析代码的，若按这样的数据进行保存，会存在主键重复的情况。
                //所以我把数据SUM起来。这样SUM，没分析代码时同样适用。杨
                " group by FCashAccCode,FStorageDate,FPortCode,FCuryCode,FPortCuryRate,FBaseCuryRate" +
                (analy1 ? ",FAnalysisCode1" : " ") +
                (analy2 ? ",FAnalysisCode2" : " ") +
     " )) x on x.FCashAccCode=y.FTsfCashAccCode and x.FPortCode= y.FTsfPortCode " +
     (analy1 ? (" and x.FAnalysisCode1=y.FTsfAnalysisCode1 ") : " ") +
                (analy2 ? (" and x.FAnalysisCode2=y.FTsfAnalysisCode2") : " ");

          strSql = strSql + " left join (select jb.* from (select FCashAccCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 group by FCashAccCode) ja join (select FCashAccCode, FCashAccName, FStartDate, FCuryCode as FinCuryCode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                ") jb on ja.FCashAccCode = jb.FCashAccCode and ja.FStartDate = jb.FStartDate ) j on y.FTsfCashAccCode = j.FCashAccCode";

          strSql = strSql +
                " left join (select pb.* from (select FPortCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 group by FPortCode) pa join (select FPortCode, FPortName, FStartDate," +
                " FPortCury as FPortCuryStorage from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                ") pb on pa.FPortCode = pb.FPortCode and pa.FStartDate = pb.FStartDate) p on  x.FPortCode = p.FPortCode " +

                " left join (select qb.* from (select FPortCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 group by FPortCode) qa join (select FPortCode, FPortName, FStartDate," +
                " FPortCury as FTdPortCury, FBaseRateSrcCode as FTdExRateSrcCode, FBaseRateCode as FTdExRateCode from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                ") qb on qa.FPortCode = qb.FPortCode and qa.FStartDate = qb.FStartDate) q on y.FTsfPortCode = q.FPortCode";

          rs = dbl.openResultSet(strSql);
          while (rs.next()) {
             cashstorage = new CashStorageBean();
     cashstorage.setStrStorageDate(YssFun.formatDate(dDate, "yyyy-MM-dd"));
             if (rs.getDate("FStorageDate") == null &&
                 rs.getDate("FTransferDate") != null) {
                cashstorage = new CashStorageBean();
                //新帐户的资金调拨
                cashstorage.setStrAccBalance(YssFun.roundIt(rs.getDouble(
                      "FTsfMoney"), 2) + "");
                cashstorage.setStrCashAccCode(rs.getString("FTsfCashAccCode"));
                cashstorage.setStrStorageDate(YssFun.formatDate(rs.getDate(
                      "FTransferDate")));
                cashstorage.setStrPortCode(rs.getString("FTsfPortCode"));
                if (rs.getString("FInCuryCode") == null) {
                   throw new YssException("请检查现金帐户【" +
                                          rs.getString("FTsfCashAccCode") +
                                          "】的币种设置！");

                }
                cashstorage.setStrCuryCode(rs.getString("FInCuryCode"));
                sTradeCuryCode = rs.getString("FInCuryCode");
//               sExRateSrcCode = rs.getString("FTdExRateSrcCode");
                sPortCuryCode = rs.getString("FTdPortCury");
                dBaseRate = this.getSettingOper().getCuryRate(rs.getDate("FTransferDate"),sTradeCuryCode,rs.getString("FTsfPortCode"),YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(rs.getDate("FTransferDate"),sPortCuryCode,rs.getString("FTsfPortCode"),YssOperCons.YSS_RATE_PORT);
                // sExRateCode = rs.getString("FTdExRateCode");
//               if (sExRateSrcCode != null) {
//                  cashstorage.setStrBaseCuryRate(baseOperBean.getCuryRate(dDate, sTradeCuryCode,cashstorage.getStrPortCode(),YssOperCons.YSS_RATE_BASE) + "");
//                  cashstorage.setStrPortCuryRate(baseOperBean.getCuryRate(dDate,sPortCuryCode,cashstorage.getStrPortCode(),YssOperCons.YSS_RATE_PORT) + "");
//               }
                //基础汇率和组合汇率按照当日汇率进入库存，因为如果是证券清算款的话，可能在业务日期还不知道实际的调拨日期的汇率是多少  胡昆  20070928
                //cashstorage.setStrBaseCuryBal(this.getSettingOper().calBaseMoney(rs.getDouble("FTsfMoney"),dBaseRate)+"");
                //cashstorage.setStrPortCuryBal(this.getSettingOper().calPortMoney(rs.getDouble("FTsfMoney"),dBaseRate,dPortRate)+"");
                cashstorage.setStrBaseCuryBal(rs.getDouble("FTsfBaseMoney")+"");
                cashstorage.setStrPortCuryBal(rs.getDouble("FTsfPortMoney")+"");

                if (analy1) {
                   if (rs.getString("FTsfAnalysisCode1") == null) {
                      cashstorage.setStrFAnalysisCode1(" ");
                   }
                   else {
                      cashstorage.setStrFAnalysisCode1(rs.getString(
                            "FTsfAnalysisCode1"));
                   }
                }
                else {
                   cashstorage.setStrFAnalysisCode1(" ");
                }
                if (analy2) {
                   if (rs.getString("FTsfAnalysisCode2") == null) {
                      cashstorage.setStrFAnalysisCode2(" ");
                   }
                   else {
                      cashstorage.setStrFAnalysisCode2(rs.getString(
                            "FTsfAnalysisCode2"));
                   }
                }
                else {
                   cashstorage.setStrFAnalysisCode2(" ");
                }

                cashstorage.setStrFAnalysisCode3(" ");

                if (YssFun.toDouble(cashstorage.getStrAccBalance())==0) { //当原币金额不等于0计算基础货币金额和组合货币金额
                   //当原币金额等于0时，基础货币金额和组合货币金额可能不为0。
                   cashstorage.setStrBaseCuryBal("0");
                   cashstorage.setStrPortCuryBal("0");
                }
             }
             else if (rs.getDate("FStorageDate") != null &&
                      rs.getDate("FTransferDate") != null) {
                //已有帐户当日发生资金调拨
                cashstorage.setStrPortCode(rs.getString("FPortCode"));
                cashstorage.setStrCuryCode(rs.getString("FCuryCode"));
                sTradeCuryCode = rs.getString("FCuryCode"); //获得交易货币代码
                sPortCuryCode = rs.getString("FPortCuryStorage");
                cashstorage.setStrAccBalance(YssFun.roundIt(YssD.add(rs.
                      getDouble("FAccBalance"), rs.getDouble("FTsfMoney")), 2) +
                                             "");

                dBaseRate = this.getSettingOper().getCuryRate(rs.getDate("FTransferDate"),sTradeCuryCode,rs.getString("FPortCode"),YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(rs.getDate("FTransferDate"),sPortCuryCode,rs.getString("FPortCode"),YssOperCons.YSS_RATE_PORT);
                //基础汇率和组合汇率按照当日汇率进入库存，因为如果是证券清算款的话，可能在业务日期还不知道实际的调拨日期的汇率是多少 胡昆  20070928
                //这边有问题啊fazmm20071006
                baseMoney = YssD.add(rs.getDouble("FBaseCuryBal"),rs.getDouble("FTsfBaseMoney"));
                portMoney = YssD.add(rs.getDouble("FPortCuryBal"),rs.getDouble("FTsfPortMoney"));
                //baseMoney = YssD.add(rs.getDouble("FBaseCuryBal"),this.getSettingOper().calBaseMoney(rs.getDouble("FTsfMoney"),dBaseRate));
                //portMoney = YssD.add(rs.getDouble("FPortCuryBal"),this.getSettingOper().calPortMoney(rs.getDouble("FTsfMoney"),dBaseRate,dPortRate));

                if (YssFun.toDouble(cashstorage.getStrAccBalance())!=0) { //当原币金额不等于0计算基础货币金额和组合货币金额
                   cashstorage.setStrBaseCuryBal(baseMoney +"");
                   cashstorage.setStrPortCuryBal(portMoney + "");
                }
                else {
                   //当原币金额等于0时，基础货币金额和组合货币金额可能不为0。
                   //处理方式：1.把现金库存的基础货币金额和组合货币金额都设置为0
                   //         2.把冲减不掉的基础货币金额和组合货币金额在对应的现金应收应付库存中插入调拨类型为"汇兑损益调整金"的记录

                   cashstorage.setStrBaseCuryBal("0");
                   cashstorage.setStrPortCuryBal("0");
//                  if (baseMoney!=0 || portMoney!=0){
//                     CashPayRecStorageAdmin prsAdmin = new
//                           CashPayRecStorageAdmin();
//                     prsAdmin.setYssPub(pub);
//                     CashRecPayBalBean cashPay = new CashRecPayBalBean();
//                     cashPay.setSCashAccCode(rs.getString("FCashAccCdoe"));
//                     cashPay.setSPortCode(rs.getString("FPortCode"));
//                     cashPay.setSCuryCode(rs.getString("FCuryCode"));
//                     cashPay.setSAnalysisCode1(rs.getString("FAnalysisCode1"));
//                     cashPay.setSAnalysisCode2(rs.getString("FAnalysisCode2"));
//                     cashPay.setSAnalysisCode3(rs.getString("FAnalysisCode3"));
//                     cashPay.setSTsfTypeCode(YssOperCons.);
//                  }
//
                }
                if (analy1) {
                   if (rs.getString(
                         "FAnalysisCode1") == null) {
                      cashstorage.setStrFAnalysisCode1(" ");
                   }
                   else {
                      cashstorage.setStrFAnalysisCode1(rs.getString(
                            "FAnalysisCode1"));
                   }
                }
                else {
                   cashstorage.setStrFAnalysisCode1(" ");
                }
                if (analy2) {
                   if (rs.getString("FAnalysisCode2") == null) {
                      cashstorage.setStrFAnalysisCode2(" ");
                   }
                   else {
                      cashstorage.setStrFAnalysisCode2(rs.getString(
                            "FAnalysisCode2"));
                   }
                }
                else {
                   cashstorage.setStrFAnalysisCode2(" ");
                }

                cashstorage.setStrFAnalysisCode3(" ");
                cashstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
//               sExRateSrcCode = rs.getString("FExRateSrcCodeStorage");
//               sExRateCode = rs.getString("FExRateCodeStorage");
//               if (sExRateSrcCode != null) {
//                  cashstorage.setStrBaseCuryRate(baseOperBean.getCuryRate(
//                        dDate,
//                        sExRateSrcCode, sTradeCuryCode) + "");
//                  cashstorage.setStrPortCuryRate(baseOperBean.getCuryRate(
//                        dDate,
//                        sExRateSrcCode, sPortCuryCode) + "");
//                  cashstorage.setStrBaseCuryRate(baseOperBean.getCuryRate(dDate, sTradeCuryCode,cashstorage.getStrPortCode(),YssOperCons.YSS_RATE_BASE) + "");
//                  cashstorage.setStrPortCuryRate(baseOperBean.getCuryRate(dDate,sPortCuryCode,cashstorage.getStrPortCode(),YssOperCons.YSS_RATE_PORT) + "");
//               }

             }
             else {
                //没有发生资金调拨
                cashstorage.setStrPortCode(rs.getString("FPortCode"));
                cashstorage.setStrCuryCode(rs.getString("FCuryCode"));
                sTradeCuryCode = rs.getString("FCuryCode");
                cashstorage.setStrAccBalance(YssFun.roundIt(rs.getDouble(
                      "FAccBalance"),
                      2) + "");
                sPortCuryCode = rs.getString("FPortCuryStorage");
//               sExRateSrcCode = rs.getString("FExRateSrcCodeStorage");
//               sExRateCode = rs.getString("FExRateCodeStorage");
//               if (sExRateSrcCode != null) {
//                  cashstorage.setStrBaseCuryRate(baseOperBean.getCuryRate(
//                        dDate,
//                        sExRateSrcCode, sTradeCuryCode) + "");
//                  cashstorage.setStrPortCuryRate(baseOperBean.getCuryRate(
//                        dDate,
//                        sExRateSrcCode, sPortCuryCode) + "");
//                  cashstorage.setStrBaseCuryRate(baseOperBean.getCuryRate(dDate, sTradeCuryCode,cashstorage.getStrPortCode(),YssOperCons.YSS_RATE_BASE) + "");
//                  cashstorage.setStrPortCuryRate(baseOperBean.getCuryRate(dDate,sPortCuryCode,cashstorage.getStrPortCode(),YssOperCons.YSS_RATE_PORT) + "");
//               }

                cashstorage.setStrBaseCuryBal(YssFun.roundIt(rs.getDouble(
                      "FBaseCuryBal"),
                      2) + "");
                cashstorage.setStrPortCuryBal(YssFun.roundIt(rs.getDouble(
                      "FPortCuryBal"),
                      2) + "");
                if (analy1) {
                   if (rs.getString(
                         "FAnalysisCode1") == null) {
                      cashstorage.setStrFAnalysisCode1(" ");
                   }
                   else {
                      cashstorage.setStrFAnalysisCode1(rs.getString(
                            "FAnalysisCode1"));
                   }
                }
                else {
                   cashstorage.setStrFAnalysisCode1(" ");
                }
                if (analy2) {
                   if (rs.getString("FAnalysisCode2") == null) {
                      cashstorage.setStrFAnalysisCode2(" ");
                   }
                   else {
                      cashstorage.setStrFAnalysisCode2(rs.getString(
                            "FAnalysisCode2"));
                   }
                }
                else {
                   cashstorage.setStrFAnalysisCode2(" ");
                }

                cashstorage.setStrFAnalysisCode3(" ");
                cashstorage.setStrCashAccCode(rs.getString("FCashAccCode"));
                cashstorage.setStrBaseCuryRate(baseOperBean.getCuryRate(dDate,
                      sTradeCuryCode, cashstorage.getStrPortCode(),
                      YssOperCons.YSS_RATE_BASE) + "");
                cashstorage.setStrPortCuryRate(baseOperBean.getCuryRate(dDate,
                      sPortCuryCode, cashstorage.getStrPortCode(),
                      YssOperCons.YSS_RATE_PORT) + "");
                // opdate = rs.getDate("FOperDate");
             }

             all.add(cashstorage);
          }
          dbl.closeResultSetFinal(rs);

//         }
          return all;
       }

       catch (Exception ex) {
          throw new YssException(strError + "\r\n" + ex.getMessage(), ex);
       }
       finally {
          dbl.closeResultSetFinal(rs);
       }
        }
     */
}
