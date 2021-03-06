package com.yss.main.operdeal.stgstat;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.*;
import com.yss.main.storagemanage.*;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.commeach.EachRateOper;

public class StgCashRecPay
    extends BaseStgStatDeal {

    public StgCashRecPay() {
    }

    private HashMap getEveStg(java.util.Date dDate) throws
        YssException {
        HashMap hmEveStg = new HashMap();
        CashRecPayBalBean cashrecpaybal = null;
        String strError = "统计现金应收应付库存出错";
        boolean analy1;
        boolean analy2;
        boolean analy3;
        ResultSet rs = null;
        String sKey = "";
        String strSql = "";
        String strTmpSql = "";
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            strSql = "select a.*," + dbl.sqlDate(dDate) +
                " as FOperDate from(" +
                " select FStorageDate, FPortCode, FCashAccCode, FTsfTypeCode,FSubTsfTypeCode,FCuryCode" +
                (analy1 ? ",FAnalysisCode1" : "") +
                (analy2 ? ",FAnalysisCode2" : "") +
                (analy3 ? ",FAnalysisCode3" : "") + "," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                "fattrclscode,"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------// 
                dbl.sqlIsNull("FBal", "0") + " as FBal, " +
                dbl.sqlIsNull("FBaseCuryBal", "0") + " as FBaseCuryBal, " +
                dbl.sqlIsNull("FPortCuryBal", "0") + " as FPortCuryBal, fisdif from " + // 增加尾差数据标识字段 by qiuxufeng 20110125
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " where FCheckstate=1 and FPortCode in (" + portCodes +
                ") and (fbal <> 0 or fbaseCuryBal <> 0 or fportCuryBal <> 0 or fsubtsftypecode='07TD' or fsubtsftypecode='06TD')" +
                ( this.statCodes.trim().length() > 0 ? " and FCashAccCode in (" + operSql.sqlCodes(this.statCodes) + ")" : "") + //add by huangqirong 2013-04-15 bug #7545 选中的现金账户
                " and " + 
                operSql.sqlStoragEve(dDate) + ") a";//edit by xuxuming,20091204.MS00826  清算款数据也要查出来,即使金额为0
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                cashrecpaybal = new CashRecPayBalBean();
                sKey = rs.getString("FCashAccCode") + "\f" +
                    rs.getString("FTsfTypeCode") + "\f" +
                    rs.getString("FSubTsfTypeCode") + "\f" +
                    rs.getString("FPortCode") +
                    (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "")+
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    "\f"+rs.getString("fattrclscode");
                 //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                cashrecpaybal.setDtStorageDate(rs.getDate("FOperDate"));
                cashrecpaybal.setSPortCode(rs.getString("FPortCode"));
                cashrecpaybal.setSCashAccCode(rs.getString("FCashAccCode"));
                cashrecpaybal.setSAnalysisCode1( (analy1 ?
                                                  rs.getString("FAnalysisCode1") :
                                                  " "));
                cashrecpaybal.setSAnalysisCode2( (analy2 ?
                                                  rs.getString("FAnalysisCode2") :
                                                  " "));
                cashrecpaybal.setSAnalysisCode3( (analy3 ?
                                                  rs.getString("FAnalysisCode3") :
                                                  " "));
              //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                cashrecpaybal.setAttrClsCode(rs.getString("fattrclscode"));
             //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                cashrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                cashrecpaybal.setSSubTsfTypeCode(rs.getString(
                    "FSubTsfTypeCode"));
                cashrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
                cashrecpaybal.setDBal(rs.getDouble("FBal"));
                cashrecpaybal.setDBaseBal(rs.getDouble("FBaseCuryBal"));
                cashrecpaybal.setDPortBal(rs.getDouble("FPortCuryBal"));
                cashrecpaybal.setsIsDif(rs.getString("fisdif")); // 加上尾差数据标识字段，保证T+1日统计库存不会被置0 by qiuxufeng 20110125
                hmEveStg.put(sKey, cashrecpaybal);
            }
        } catch (Exception ex) {
            throw new YssException("系统进行现金应收应付库存统计,在统计昨日现金应收应付库存时出现异常!\n", ex); //by 曹丞 2009.01.22统计昨日现金应收应付库存异常 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }

        return hmEveStg;
    }

    /***
     * 本方法里使每个金额都乘以方向 by ly 080218
     */
    //方法中添加分析代码参数,优化系统  by leeyu 20100504 合并太平版本代码
    private ArrayList getCashRecPayData(java.util.Date dDate,boolean analy1,boolean analy2,boolean analy3) throws
        YssException {
        String strSql = "", strTmpSql = "";
        ResultSet rs = null;
        CashRecPayBalBean cashrecpaybal = null;
        ArrayList all = new ArrayList();
//      java.util.Date dDate = null;
        String strError = "统计现金应收应付库存出错";

      //boolean analy1; //判断是否需要用分析代码；杨//调整获取分析代码的方式,方法中添加分析代码参数,优化系统  by leeyu 20100504
      //boolean analy2;//调整获取分析代码的方式,方法中添加分析代码参数,优化系统  by leeyu 20100504
      //boolean analy3;//调整获取分析代码的方式,方法中添加分析代码参数,优化系统  by leeyu 20100504
        double baseMoney = 0;
        double portMoney = 0;
        String sKey = "";
        double dBaseRate = 1;
        double dPortRate = 1;
        HashMap hmEveStg = null;
        Iterator iter = null;
     //--- QDV4中保2010年1月11日01_B add by jiangshichao 2010.01.18--------
	      String sRecPayPara="";
	      CtlPubPara pubPara = new CtlPubPara();
	      pubPara.setYssPub(pub);
	      sRecPayPara = pubPara.getRecPayPara();//获取应收应付库存统计方式
	      double dScale = 0;
	      double dBaseMoney = 0;
	      double dPortMoney = 0;
	      //--- QDV4中保2010年1月11日01_B end ----------------------------------
	      
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
            if (bYearChange) {
            	if(bTPCost){
            		//太平资产年终结账方式：根据09、14、18报表产生虚拟账户的期初数和证券的特殊处理
            		yearChange_TP(dDate, portCodes);
            	}else{
            		//QDII年终结账方式
            		yearChange(dDate, portCodes);
            	}
            }
          //--- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 end------------------------
            
            //#1476 因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误   add by jiangshichao 2011.03.16-----//
            String fieldStr = "";
            if(bTPCost){
            	fieldStr = " nvl(fattrclscode,' ') as fattrclscode, ";
            }else{
            	fieldStr = " ' ' as fattrclscode, ";
            }
          //#1476 因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误   end----------//
            
            hmEveStg = getEveStg(dDate);
          //analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");//调整获取分析代码的方式,方法中添加分析代码参数,优化系统  by leeyu 20100504
          //analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");//调整获取分析代码的方式,方法中添加分析代码参数,优化系统  by leeyu 20100504
          //analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");//调整获取分析代码的方式,方法中添加分析代码参数,优化系统  by leeyu 20100504
            strSql = "select FTransDate, FPortCode, FCashAccCode, " +
                "FTsfTypeCode, " +
                //dbl.sqlIsNull("FSubTsfTypeCode", "' '") +
				//20091019- MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                " (case when FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ETF_QUITVALUESell) + //将应付替代款估值增值统计为应付替代款
                " then " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSell) + 
                " when FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ETF_QUITVALUEBuy) + 
                " then " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuy) + 
                " when FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMay) + //将现金替代（允许）应付款统计为应付替代款
                " then " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSell) + 
                //--edited by zhouxiang MS01301    新建买入定存业务，当天计提利息时会把该账户买入“所含利息”显示出来    
                "when FSubTsfTypeCode ="+dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DC_RecInterest)+
                "then "+dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest)+
                //--------------end------------------------
                " when FSubTsfTypeCode  is null then ' ' " +
                /**shashijie 2011-08-31 STORY 1327 挂账销账冲减*/
                " when FSubTsfTypeCode = '07XZ' then '07GZ' when FSubTsfTypeCode = '06XZ' then '06GZ' "+
                /**end*/
				//add by songjie 2012.03.28 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A
                " when FSubTsfTypeCode = '06DV_TZ' then '06DV'" + //分红转投调整金额冲减应收红利 STORY #2014 
                " else FSubTsfTypeCode end )" + 
                " as FSubTsfTypeCode, " +
                "FCuryCode, " +
                (analy1 ? dbl.sqlIsNull("FAnalysisCode1", "' '") +
                 " as FAnalysisCode1," : " ") +
                (analy2 ? dbl.sqlIsNull("FAnalysisCode2", "' '") +
                 " as FAnalysisCode2," : " ") +
                (analy3 ? dbl.sqlIsNull("FAnalysisCode3", "' '") +
                 " as FAnalysisCode3," : " ") +
                 fieldStr +//#1476 因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误   add by jiangshichao 2011.03.16
                " sum(FMoney*FInOut) as FMoney, " +
                " sum(FBaseCuryMoney*FInOut) as FBaseCuryMoney," +
//                " sum(FPortCuryMoney*FInOut) as FPortCuryMoney,'Stat' as FType from " +
                // 444 QDV4汇添富2010年12月21日01_A 20110121 通过描述标识尾差数据
                " sum(FPortCuryMoney*FInOut) as FPortCuryMoney,'Stat' as FType," +
                " FDesc from " +
                // 444 QDV4汇添富2010年12月21日01_A 20110121
                pub.yssGetTableName("Tb_Data_CashPayRec") +
                " where FTransDate =" + dbl.sqlDate(dDate) +
                ( this.statCodes.trim().length() > 0 ? " and FCashAccCode in (" + operSql.sqlCodes(this.statCodes) + ") " : "") + //add by huangqirong 2013-04-15 bug #7545 选中的现金账户
                " and FPortCode in (" + portCodes + ")" +
                " and (FMoney <> 0 or FBaseCuryMoney <> 0 or FPortCuryMoney <> 0)" +
                " and FCheckState = 1 and (FTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + ", " + //应收款项
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + ", " + //应付款项
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) + ", " + //汇兑损益
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) + //"," + //估值增值
                //dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + "," + //收入
                //dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + //费用
                ")) " +
                " group by FTransDate, FPortCode, FCashAccCode" +
                (analy1 ? ", FAnalysisCode1" : " ") +
                (analy2 ? ", FAnalysisCode2" : " ") +
                (analy3 ? ", FAnalysisCode3" : " ") +
                ", FTsfTypeCode, FSubTsfTypeCode, FCuryCode, " +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " fattrclscode" +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                // 444 QDV4汇添富2010年12月21日01_A 20110121 通过描述标识尾差数据
                ", FDesc " +
                // 444 QDV4汇添富2010年12月21日01_A 20110121
                //------------------------------------
                //" union " +
                " union all " +//并行优化,改为union all,原因是上下代码已经采用FType区分过 by leeyu 20100604 合并太平版本代码
                //------------------------------------
                "select FTransDate, FPortCode, FCashAccCode, " +
                "(case when FTsfTypeCode = '02' then '06' when FTsfTypeCode = '03' then '07' else FTsfTypeCode end) as FTsfTypeCode," +
                " (case when FTsfTypeCode = '02' then '06' " + dbl.sqlJN() +
                dbl.sqlSubStr("FSubTsfTypeCode", "3") +
                "  when FTsfTypeCode = '03' then '07'" + dbl.sqlJN() +
                dbl.sqlSubStr("FSubTsfTypeCode", "3") +
                " else FSubTsfTypeCode end) as FSubTsfTypeCode," +
                "FCuryCode, " +
                (analy1 ? dbl.sqlIsNull("FAnalysisCode1", "' '") +
                 " as FAnalysisCode1," : " ") +
                (analy2 ? dbl.sqlIsNull("FAnalysisCode2", "' '") +
                 " as FAnalysisCode2," : " ") +
                (analy3 ? dbl.sqlIsNull("FAnalysisCode3", "' '") +
                 " as FAnalysisCode3," : " ") +
                 fieldStr +////#1476 因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误   add by jiangshichao 2011.03.16
                " sum(-FMoney*FInOut) as FMoney, " +
                " sum(-FBaseCuryMoney*FInOut) as FBaseCuryMoney," +
//                " sum(-FPortCuryMoney*FInOut) as FPortCuryMoney,'Rush' as FType from " +
                // 444 QDV4汇添富2010年12月21日01_A 20110121 通过描述标识尾差数据
                " sum(-FPortCuryMoney*FInOut) as FPortCuryMoney,'Rush' as FType," +
                " FDesc from " +
                // 444 QDV4汇添富2010年12月21日01_A 20110121
                pub.yssGetTableName("Tb_Data_CashPayRec") +
                " where FTransDate =" + dbl.sqlDate(dDate) +
                ( this.statCodes.trim().length() > 0 ? " and FCashAccCode in (" + operSql.sqlCodes(this.statCodes) + ")" : "") + //add by huangqirong 2013-04-15 bug #7545 选中的现金账户
                " and FPortCode in (" + portCodes + ")" +
                " and (FMoney <> 0 or FBaseCuryMoney <> 0 or FPortCuryMoney <> 0)" +
                " and FCheckState = 1 and (FTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + "," + //收入
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + //费用
                ")) " +
                " group by FTransDate, FPortCode, FCashAccCode" +
                (analy1 ? ", FAnalysisCode1" : " ") +
                (analy2 ? ", FAnalysisCode2" : " ") +
                (analy3 ? ", FAnalysisCode3" : " ") +
                ", FTsfTypeCode, FSubTsfTypeCode, FCuryCode, " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " fattrclscode " +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                // 444 QDV4汇添富2010年12月21日01_A 20110121 通过描述标识尾差数据
                ", FDesc " +
                // 444 QDV4汇添富2010年12月21日01_A 20110121
                //--------- QDV4中保2010年1月11日01_B add by jiangshichao 2010.01.18 合并太平版本代码----------
                (sRecPayPara.equalsIgnoreCase("1")?" order by FType desc":""); //按资金的冲减状态进行排序
                //--------- QDV4中保2010年1月11日01_B end --------------------------------------;
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	String sIsDif = " ";
            	if(null != rs.getString("FDesc") && rs.getString("FDesc").indexOf("基金TA尾差调整数据") >= 0) {
            		sIsDif = "TZ";
            	}
                sKey = rs.getString("FCashAccCode") + "\f" +
                    rs.getString("FTsfTypeCode") + "\f" +
                    rs.getString("FSubTsfTypeCode") + "\f" +
                    rs.getString("FPortCode") +
                    (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "")+
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    "\f"+rs.getString("fattrclscode");
                   //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    ;
                if (hmEveStg.containsKey(sKey)) {
                    cashrecpaybal = (CashRecPayBalBean) hmEveStg.get(sKey);
                    cashrecpaybal.setDtStorageDate(rs.getDate("FTransDate"));
                    cashrecpaybal.setSPortCode(rs.getString("FPortCode"));
                    cashrecpaybal.setSCashAccCode(rs.getString("FCashAccCode"));
                    cashrecpaybal.setSAnalysisCode1( (analy1 ? rs.getString(
                        "FAnalysisCode1") : " "));
                    cashrecpaybal.setSAnalysisCode2( (analy2 ? rs.getString(
                        "FAnalysisCode2") : " "));
                    cashrecpaybal.setSAnalysisCode3( (analy3 ? rs.getString(
                        "FAnalysisCode3") : " "));
                    cashrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                    cashrecpaybal.setSSubTsfTypeCode(rs.getString(
                        "FSubTsfTypeCode"));
                    cashrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    cashrecpaybal.setAttrClsCode(rs.getString("fattrclscode"));
                   //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
					//--- QDV4中保2010年1月11日01_B add by jiangshichao 2010.01.14 合并太平版本代码-----------------------------
	               if (sRecPayPara.equalsIgnoreCase("1")&& rs.getString("FType").equalsIgnoreCase("Rush")){
	            	   dScale = YssD.div(rs.getDouble("FMoney"), cashrecpaybal.getDBal());
	            	   dBaseMoney = YssD.round(YssD.mul(cashrecpaybal.getDBaseBal(), dScale), 4); // 保留4位 modify by wangzuochun 2010.10.27 BUG #198 定存业务现金应收应付金额不对（QDV4太平2010年10月25日01_B） 
	            	   dPortMoney = YssD.round(YssD.mul(cashrecpaybal.getDPortBal(), dScale), 4); // 保留4位 modify by wangzuochun 2010.10.27 BUG #198 定存业务现金应收应付金额不对（QDV4太平2010年10月25日01_B） 
	            	   dBaseRate = YssD.round(YssD.div(dBaseMoney, rs.getDouble("FMoney")), 15);
	             	   dPortRate = YssD.round(YssD.div(dBaseMoney,dPortMoney), 15);  
             	   
	             	   cashrecpaybal.setDBaseRate(dBaseRate);//用移動加權計算出來的匯率去更新業務類別為【收入】數據的匯率。
	             	   cashrecpaybal.setDPortRate(dPortRate);

	             	   cashrecpaybal.setDBal(YssD.add(rs.getDouble("FMoney"), cashrecpaybal.getDBal()));
	             	   cashrecpaybal.setDBaseBal(YssD.add(dBaseMoney,cashrecpaybal.getDBaseBal()));
	             	   cashrecpaybal.setDPortBal( YssD.add(dPortMoney,cashrecpaybal.getDPortBal()));  
             	   
	             	  if(cashrecpaybal.getSTsfTypeCode().equalsIgnoreCase("06")){
	             		 CashPayRecStorageAdmin  cashPayRecStorageAdmin = new CashPayRecStorageAdmin();
	             		 cashPayRecStorageAdmin.setYssPub(pub);
	             		 cashPayRecStorageAdmin.updateAvgRate(cashrecpaybal,dBaseMoney<0?(-dBaseMoney):dBaseMoney,dPortMoney<0?(-dPortMoney):dPortMoney);
	             	  }
	               }else{
             		//--- QDV4中保2010年1月11日01_B end ----------------------------------------------------------
                    cashrecpaybal.setDBal(
                        YssD.add(rs.getDouble("FMoney"), cashrecpaybal.getDBal()));
                    cashrecpaybal.setDBaseBal(
                        YssD.add(rs.getDouble("FBaseCuryMoney"),
                                 cashrecpaybal.getDBaseBal()));
                    cashrecpaybal.setDPortBal(
                        YssD.add(rs.getDouble("FPortCuryMoney"),
                                 cashrecpaybal.getDPortBal()));
					}
	               	// 444 QDV4汇添富2010年12月21日01_A 20110121 通过描述标识尾差数据
	            	cashrecpaybal.setsIsDif(sIsDif);
	            	// 444 QDV4汇添富2010年12月21日01_A 20110121
                    hmEveStg.put(sKey, cashrecpaybal);
                } else {
                    cashrecpaybal = new CashRecPayBalBean();
                    cashrecpaybal.setDtStorageDate(dDate);
                    cashrecpaybal.setSPortCode(rs.getString("FPortCode"));
                    cashrecpaybal.setSCashAccCode(rs.getString("FCashAccCode"));
                    cashrecpaybal.setSAnalysisCode1( (analy1 ? rs.getString(
                        "FAnalysisCode1") : " "));
                    cashrecpaybal.setSAnalysisCode2( (analy2 ? rs.getString(
                        "FAnalysisCode2") : " "));
                    cashrecpaybal.setSAnalysisCode3( (analy3 ? rs.getString(
                        "FAnalysisCode3") : " "));
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    cashrecpaybal.setAttrClsCode(rs.getString("fattrclscode"));
                   //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    cashrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                    cashrecpaybal.setSSubTsfTypeCode(rs.getString(
                        "FSubTsfTypeCode"));
                    cashrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
                    cashrecpaybal.setDBal(
                        YssD.add(rs.getDouble("FMoney"), cashrecpaybal.getDBal()));
                    cashrecpaybal.setDBaseBal(
                        YssD.add(rs.getDouble("FBaseCuryMoney"),
                                 cashrecpaybal.getDBaseBal()));
                    cashrecpaybal.setDPortBal(
                        YssD.add(rs.getDouble("FPortCuryMoney"),
                                 cashrecpaybal.getDPortBal()));
                    // 444 QDV4汇添富2010年12月21日01_A 20110121 通过描述标识尾差数据
 	               	cashrecpaybal.setsIsDif(sIsDif);
 	               	// 444 QDV4汇添富2010年12月21日01_A 20110121
                    hmEveStg.put(sKey, cashrecpaybal);
                }
            }
            iter = hmEveStg.keySet().iterator();
            while (iter.hasNext()) {
                sKey = (String) iter.next();
                cashrecpaybal = (CashRecPayBalBean) hmEveStg.get(sKey);
                all.add(cashrecpaybal);
            }
            return all;
        } catch (Exception e) {
            throw new YssException("系统进行现金应收应付库存统计,在统计当日现金应收应付数据时出现异常!\n", e); //by 曹丞 2009.01.22 统计当日现金应收应付数据异常 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 20091019- MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @param dDate
     * @return
     * @throws YssException
     */
    private ArrayList getETFCashRecPayData(java.util.Date dDate,boolean analy1,boolean analy2,boolean analy3) throws YssException {//从外面传分析代码条件 合并太平版本代码调整
        String strSql = "";
        ResultSet rs = null;
        CashRecPayBalBean cashrecpaybal = null;
        ArrayList all = new ArrayList();
        //boolean analy1; //判断是否需要用分析代码；杨
        //boolean analy2;
        //boolean analy3;
        String sKey = "";
        HashMap hmEveStg = null;
        Iterator iter = null;
        //--- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 ---------------------------
        CtlPubPara pubPara = null;  
        boolean bTPCost =false;//区分是太平资产的库存统计还是QDII的库存统计,合并版本时调整
    	String sPara_tp = "";
    //--- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 end------------------------
     try {
    	
    	//--- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 ---------------------------
    	pubPara = new CtlPubPara();
        pubPara.setYssPub(pub);
    	sPara_tp =pubPara.getNavType();//通过净值表类型来判断
    	if(sPara_tp!=null && sPara_tp.trim().equalsIgnoreCase("new")){
    		bTPCost=false;//国内QDII统计模式
    	}else{
    		bTPCost=true;//太平资产统计模式
    	}
        if (bYearChange) {
        	if(bTPCost){
        		//太平资产年终结账方式：根据09、14、18报表产生虚拟账户的期初数和证券的特殊处理
        		yearChange_TP(dDate, portCodes);
        	}else{
        		//QDII年终结账方式
        		yearChange(dDate, portCodes);
        	}
        }
      //--- 合并太平资产年终结账需求 add by jiangshichao 2011.01.15 end------------------------
        
        //#1476 因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误   add by jiangshichao 2011.03.16-----//
        String fieldStr = "";
        if(bTPCost){
        	fieldStr = " nvl(fattrclscode,' ') as fattrclscode, ";
        }else{
        	fieldStr = " ' ' as fattrclscode, ";
        }
      //#1476 因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误   end----------//
        
            hmEveStg = getEveStg(dDate);
            //analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            //analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            //analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            strSql = "select FTransDate, a.FPortCode, a.FCashAccCode, " +
                "FTsfTypeCode, " +
                " (case when FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ETF_QUITVALUESell) + //将应付替代款估值增值统计为应付替代款
                " then " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSell) + 
                " when FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ETF_QUITVALUEBuy) + 
                " then " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuy) + 
                " when FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMay) + //将现金替代（允许）应付款统计为应付替代款
                " then " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSell) + 
                " when FSubTsfTypeCode  is null then ' ' else FSubTsfTypeCode end )" + 
                " as FSubTsfTypeCode, " +
                "a.FCuryCode, " +
                (analy1 ? dbl.sqlIsNull("FAnalysisCode1", "' '") +
                 " as FAnalysisCode1," : " ") +
                (analy2 ? dbl.sqlIsNull("FAnalysisCode2", "' '") +
                 " as FAnalysisCode2," : " ") +
                (analy3 ? dbl.sqlIsNull("FAnalysisCode3", "' '") +
                 " as FAnalysisCode3," : " ") +
                 fieldStr + 
                " sum(FMoney*FInOut) as FMoney, " +
                " sum(FBaseCuryMoney*FInOut) as FBaseCuryMoney," +
                " sum(FPortCuryMoney*FInOut) as FPortCuryMoney,'Stat' as FType from(select * from " +
                pub.yssGetTableName("Tb_Data_CashPayRec") +
                " where FTransDate =" + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes + ")" +
                " and (FMoney <> 0 or FBaseCuryMoney <> 0 or FPortCuryMoney <> 0)" +
                " and FCheckState = 1 and (FTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + ", " + //应收款项
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + ", " + //应付款项
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) + ", " + //汇兑损益
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) + //"," + //估值增值
                ")) " +
                " )a JOIN (SELECT * from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " WHERE FCheckState = 1 AND FSUBASSETTYPE = '0106') b ON a.FPortCode = b.FPortCode" +
                " JOIN (SELECT * FROM " + pub.yssGetTableName("TB_ETF_PARAM ") +
                " WHERE FCheckState = 1) c ON a.FPortCode = c.FPortCode" +
                " WHERE ((c.fsupplymode in ("+
                dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_TIMESUB) + 
                ") AND " +
                " a.FsubTsfTypeCode IN ("+
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_SGCanBackCash) + ", " + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_SHCanBackCash) + 
                " )AND a.FInOut <> 1) OR (c.fsupplymode NOT IN (" + 
                dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_TIMESUB)+ 
                ") AND a.FsubTsfTypeCode NOT IN(" +
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuy) + ", " + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSell) + ", " + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_SGCanBackCash) + ", " + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_SHCanBackCash) + 
                " )) OR (c.fsupplymode IN (" + dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_TIMESUB) +
                ") AND " +
                " a.FsubTsfTypeCode NOT IN ("+
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_SGCanBackCash) + ", " + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_SHCanBackCash) + 
                //易方达ETF申赎T+1日确认，确认日当天需在净值表一中统计T日申购现金差额、现金替代
                //其他补票方式由于是申赎当天确认，因此净值表一中不统计T日申购现金差额、现金替代
                //panjunfang modify 20110810 STORY #1434 QDV4易方达基金2011年7月27日01_A
                " )) OR c.fsupplymode in (" + dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_ONE) + 
                "," + dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) + 
                "," + dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ) + 
                " )) and ((c.fsupplymode not in("  + dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_ONE) + 
                "," + dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) + 
                " ) and  a.FSubTsfTypeCode not in (" + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPBuy) + ", " + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPBuy) + ", " + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPSell) + ", " + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPSell) + ", " + 
                dbl.sqlString(YssOperCons.YSS_ETF_CashTradeCost_SG) + "," + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustRPSell) + "," + 
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMay) + "," + 
                dbl.sqlString(YssOperCons.YSS_ETF_CashTradeCost_SH) + 
                " )) or (c.fsupplymode in("  + dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_ONE) + 
                "," + dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) +
                "," + dbl.sqlString(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ) +
                /**shashijie 2011-12-22 STORY 1789 ORACLE 11g bug*/
                "))) group by FTransDate, a.FPortCode, a.FCashAccCode" +
                /**end*/
                (analy1 ? ", FAnalysisCode1" : " ") +
                (analy2 ? ", FAnalysisCode2" : " ") +
                (analy3 ? ", FAnalysisCode3" : " ") +
                ", FTsfTypeCode, FSubTsfTypeCode, a.FCuryCode " + 
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " ,fattrclscode" +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                //------------------------------------
                " union " +
                //------------------------------------
                "select FTransDate, FPortCode, FCashAccCode, " +
                "(case when FTsfTypeCode = '02' then '06' when FTsfTypeCode = '03' then '07' else FTsfTypeCode end) as FTsfTypeCode," +
                " (case when FTsfTypeCode = '02' then '06' " + dbl.sqlJN() +
                dbl.sqlSubStr("FSubTsfTypeCode", "3") +
                "  when FTsfTypeCode = '03' then '07'" + dbl.sqlJN() +
                dbl.sqlSubStr("FSubTsfTypeCode", "3") +
                " else FSubTsfTypeCode end) as FSubTsfTypeCode," +
                "FCuryCode, " +
                (analy1 ? dbl.sqlIsNull("FAnalysisCode1", "' '") +
                 " as FAnalysisCode1," : " ") +
                (analy2 ? dbl.sqlIsNull("FAnalysisCode2", "' '") +
                 " as FAnalysisCode2," : " ") +
                (analy3 ? dbl.sqlIsNull("FAnalysisCode3", "' '") +
                 " as FAnalysisCode3," : " ") +
                 fieldStr + 
                " sum(-FMoney*FInOut) as FMoney, " +
                " sum(-FBaseCuryMoney*FInOut) as FBaseCuryMoney," +
                " sum(-FPortCuryMoney*FInOut) as FPortCuryMoney,'Rush' as FType from " +
                pub.yssGetTableName("Tb_Data_CashPayRec") +
                " where FTransDate =" + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes + ")" +
                " and (FMoney <> 0 or FBaseCuryMoney <> 0 or FPortCuryMoney <> 0)" +
                " and FCheckState = 1 and (FTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + "," + //收入
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + //费用
                ")) " +
//                " and FSubTsfTypeCode in ( " + 
//                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPBuyDone) + "," + 
//                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPBuyDone) + "," + 
//                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPSellDone) + "," + 
//                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPSellDone) + "," + 
//                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesDone) + 
//                " )" + 
                " group by FTransDate, FPortCode, FCashAccCode" +
                (analy1 ? ", FAnalysisCode1" : " ") +
                (analy2 ? ", FAnalysisCode2" : " ") +
                (analy3 ? ", FAnalysisCode3" : " ") +
                ", FTsfTypeCode, FSubTsfTypeCode, FCuryCode " + 
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " ,fattrclscode" ;
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
            rs = dbl.queryByPreparedStatement(strSql);
            while (rs.next()) {
                sKey = rs.getString("FCashAccCode") + "\f" +
                    rs.getString("FTsfTypeCode") + "\f" +
                    rs.getString("FSubTsfTypeCode") + "\f" +
                    rs.getString("FPortCode") +
                    (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "") + 
                    "\f"+rs.getString("fattrclscode");
                if (hmEveStg.containsKey(sKey)) {
                    cashrecpaybal = (CashRecPayBalBean) hmEveStg.get(sKey);
                    cashrecpaybal.setDtStorageDate(rs.getDate("FTransDate"));
                    cashrecpaybal.setSPortCode(rs.getString("FPortCode"));
                    cashrecpaybal.setSCashAccCode(rs.getString("FCashAccCode"));
                    cashrecpaybal.setSAnalysisCode1( (analy1 ? rs.getString(
                        "FAnalysisCode1") : " "));
                    cashrecpaybal.setSAnalysisCode2( (analy2 ? rs.getString(
                        "FAnalysisCode2") : " "));
                    cashrecpaybal.setSAnalysisCode3( (analy3 ? rs.getString(
                        "FAnalysisCode3") : " "));
                    cashrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                    cashrecpaybal.setSSubTsfTypeCode(rs.getString(
                        "FSubTsfTypeCode"));
                    cashrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
                    cashrecpaybal.setDBal(
                        YssD.add(rs.getDouble("FMoney"), cashrecpaybal.getDBal()));
                    cashrecpaybal.setDBaseBal(
                        YssD.add(rs.getDouble("FBaseCuryMoney"),
                                 cashrecpaybal.getDBaseBal()));
                    cashrecpaybal.setDPortBal(
                        YssD.add(rs.getDouble("FPortCuryMoney"),
                                 cashrecpaybal.getDPortBal()));
                    hmEveStg.put(sKey, cashrecpaybal);
                } else {
                    cashrecpaybal = new CashRecPayBalBean();
                    cashrecpaybal.setDtStorageDate(dDate);
                    cashrecpaybal.setSPortCode(rs.getString("FPortCode"));
                    cashrecpaybal.setSCashAccCode(rs.getString("FCashAccCode"));
                    cashrecpaybal.setSAnalysisCode1( (analy1 ? rs.getString(
                        "FAnalysisCode1") : " "));
                    cashrecpaybal.setSAnalysisCode2( (analy2 ? rs.getString(
                        "FAnalysisCode2") : " "));
                    cashrecpaybal.setSAnalysisCode3( (analy3 ? rs.getString(
                        "FAnalysisCode3") : " "));
                    cashrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                    cashrecpaybal.setSSubTsfTypeCode(rs.getString(
                        "FSubTsfTypeCode"));
                    cashrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
                    cashrecpaybal.setDBal(
                        YssD.add(rs.getDouble("FMoney"), cashrecpaybal.getDBal()));
                    cashrecpaybal.setDBaseBal(
                        YssD.add(rs.getDouble("FBaseCuryMoney"),
                                 cashrecpaybal.getDBaseBal()));
                    cashrecpaybal.setDPortBal(
                        YssD.add(rs.getDouble("FPortCuryMoney"),
                                 cashrecpaybal.getDPortBal()));
                    hmEveStg.put(sKey, cashrecpaybal);
                }
            }
            iter = hmEveStg.keySet().iterator();
            while (iter.hasNext()) {
                sKey = (String) iter.next();
                cashrecpaybal = (CashRecPayBalBean) hmEveStg.get(sKey);
                all.add(cashrecpaybal);
            }
            return all;
        } catch (Exception e) {
            throw new YssException("系统进行ETF现金应收应付库存统计,在统计当日现金应收应付数据时出现异常!\n", e); 
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
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Cashpayrec") +
                " where FYearMonth = " + dbl.sqlString(YearMonth) +
                " and FPortCode in( " + operSql.sqlCodes(portCode) + ")"; //添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的 by leeyu 20090220 QDV4华夏2009年2月13日01_B MS00246
            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_Cashpayrec") +
                "(FCASHACCCODE,FYearMonth,FStorageDate,FPORTCODE," +
                "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3," +
                "FTSFTYPECODE,FSUBTSFTYPECODE," +
                "FCURYCODE,FBAL,FBASECURYBAL," +
                "FPORTCURYBAL,FSTORAGEIND,FCheckState," +
                "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" +
                "(select FCASHACCCODE," + dbl.sqlString(YearMonth) +
                " as FYearMonth, " +
                dbl.sqlDate(new Integer(Year).toString() + "-01-01") +
                " as FStorageDate,FPORTCODE," +
                "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3," +
                "FTSFTYPECODE,FSUBTSFTYPECODE," +
                "FCURYCODE,FBAL,FBASECURYBAL," +
                "FPORTCURYBAL,FSTORAGEIND,FCheckState," +
                dbl.sqlString(pub.getUserCode()) +
                " as FCREATOR," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " as FCREATETIME," +
                dbl.sqlString(pub.getUserName()) + " as FCHECKUSER," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " as FCHECKTIME" +
                " from " + pub.yssGetTableName("Tb_Stock_Cashpayrec") +
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
			strSql = "delete from "
					+ pub.yssGetTableName("Tb_Stock_Cashpayrec")
					+ " where FYearMonth = " + dbl.sqlString(YearMonth)
					+ " and FPortCode in( " + operSql.sqlCodes(portCode) + ")";// 添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的
																				// by
																				// leeyu
																				// 20090220
																				// QDV4华夏2009年2月13日01_B
																				// MS00246
			dbl.executeSql(strSql);
			strSql = "insert into "
					+ pub.yssGetTableName("Tb_Stock_Cashpayrec")
					+ "(FCASHACCCODE,FYearMonth,FStorageDate,FPORTCODE,"
					+ "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,"
					+ "FTSFTYPECODE,FSUBTSFTYPECODE,"
					+ "FCURYCODE,FBAL,FBASECURYBAL,"
					+ "FPORTCURYBAL,FSTORAGEIND,FCheckState,"
					+ "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)"
					+ "(select FCASHACCCODE,"
					+ dbl.sqlString(YearMonth)
					+ " as FYearMonth, "
					+ dbl.sqlDate(new Integer(Year).toString() + "-01-01")
					+ " as FStorageDate,FPORTCODE,"
					+ "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,"
					+ "FTSFTYPECODE,FSUBTSFTYPECODE,"
					+ "FCURYCODE,FBAL,FBASECURYBAL,"
					+ "FPORTCURYBAL,FSTORAGEIND,FCheckState,"
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
					// 需求编号:QDV4太平2010年12月20日01_A add by jiangshichao 2010.12.27
					" ( select * from "
					+ pub.yssGetTableName("Tb_Stock_Cashpayrec")
					+ " a where FYearMonth = "
					+ dbl.sqlString((Year - 1) + "12") + " and FStorageDate = "
					+ dbl.sqlDate(new Integer(Year - 1).toString() + "-12-31")
					+ " and FPortCode in( " + portCode + ")"
					+ " and not exists (select fcashacccode from "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " b where fcheckstate=1 and fsubacctype ='0414' "
					+ " and FPortCode in (" + operSql.sqlCodes(portCode) + ")"
					+ " and a.fcashacccode = b.fcashacccode) ) )";
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);

			dealUnrealisedVirtualAccData(dDate, portCode);
		} catch (Exception e) {
			throw new YssException("年度结转错误!\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			// dbl.closeStatementFinal(pst);
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
					+ pub.yssGetTableName("Tb_Stock_Cashpayrec")
					+ "(FCASHACCCODE,FYearMonth,FStorageDate,FPORTCODE,"
					+ "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,"
					+ "FTSFTYPECODE,FSUBTSFTYPECODE,"
					+ "FCURYCODE,FBAL,FBASECURYBAL,"
					+ "FPORTCURYBAL,FSTORAGEIND,FCheckState,"
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
					+ " '99' as FTSFTYPECODE, "
					+ " case when c.fcatcode ='RecPay' then '9905OT' when c.fcatcode ='01' then '9905DE' else '9905'||c.fcatcode end as FSUBTSFTYPECODE,"
					+ " c.fcurycode as FCURYCODE,"
					+ " c.fbal as FBAL,c.fbasecurybal as FBASECURYBAL,"
					+ " c.fbasecurybal as FPORTCURYBAL,0 as FSTORAGEIND,1 as  FCheckState,"
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
					"处理现金应收应付库存年终结转时，从【14未兑现资产本金增值贬值分布表（汇兑）】取数作为虚拟账户的期初数时出错......");
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
    
    
    //统计现金应收应付表的库存
    /*private ArrayList getCashRecPayData(java.util.Date dDate) throws
          YssException {
       String strSql = "", strTmpSql = "";
       ResultSet rs = null;
       CashRecPayBalBean cashrecpaybal = null;
       ArrayList all = new ArrayList();
//      java.util.Date dDate = null;
       String strError = "统计现金应收应付库存出错";

       boolean analy1; //判断是否需要用分析代码；杨
       boolean analy2;
       boolean analy3;

       try {
//         for (int j = 0; j <= YssFun.dateDiff(dStartDate, dEndDate); j++) {
//            dDate = YssFun.addDay(dStartDate, j);

          analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
          analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
          analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

          if (YssFun.getMonth(dDate) == 1 &&
              YssFun.getDay(dDate) == 1) {
             strTmpSql = " and fyearmonth ='" +
                   YssFun.formatDate(dDate, "yyyy") + "00'";
          }
          else {
             strTmpSql = " and fyearmonth <>'" +
                   YssFun.formatDate(dDate, "yyyy") + "00'" +
                   " and FStorageDate=" +
                   dbl.sqlDate(YssFun.addDay(dDate, -1));
          }

          strSql = "select a.*, b.*, e.*, " + dbl.sqlDate(dDate) +
                " as FOperDate from(" +
                " select FTransDate, FPortCode, FCashAccCode, " +
                "FTsfTypeCode, " +
     dbl.sqlIsNull("FSubTsfTypeCode", "' '") + " as FSubTsfTypeCode, " +
                "FCuryCode, " +
                (analy1 ? dbl.sqlIsNull("FAnalysisCode1", "' '") +
                 " as FAnalysisCode1," : " ") +
                (analy2 ? dbl.sqlIsNull("FAnalysisCode2", "' '") +
                 " as FAnalysisCode2," : " ") +
                (analy3 ? dbl.sqlIsNull("FAnalysisCode3", "' '") +
                 " as FAnalysisCode3," : " ") +
                " sum(FMoney) as FMoney, " +
                " sum(FBaseCuryMoney) as FBaseCuryMoney," +
                " sum(FPortCuryMoney) as FPortCuryMoney from " +
                pub.yssGetTableName("Tb_Data_CashPayRec") +
                " where FTransDate =" + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes + ")" +
     " and (FMoney <> 0 or FBaseCuryMoney <> 0 or FPortCuryMoney <> 0) " +
                " or FSubTsfTypeCode like '06TD%' or FSubTsfTypeCode like '07TD%')" + //应收应付款可能会插入0
                " and FCheckState = 1 and (FTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + ", " + //应收利息
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + ", " + //应付利息
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) + ", " + //汇兑损益
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) + "" + //估值增值
                //dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + //收入  fazmm20071010 收入是通过后面的关联
                ")) " +
                " group by FTransDate, FPortCode, FCashAccCode" +
                (analy1 ? ", FAnalysisCode1" : " ") +
                (analy2 ? ", FAnalysisCode2" : " ") +
                (analy3 ? ", FAnalysisCode3" : " ") +
                ", FTsfTypeCode, FSubTsfTypeCode, FCuryCode " +
                " ) a full join " +
                "(select FStorageDate, FPortCode as FPortCode2, FCashAccCode as FCashAccCode2, FTsfTypeCode as FTsfTypeCode2, " +
                dbl.sqlIsNull("FSubTsfTypeCode", "' '") +
                " as FSubTsfTypeCode2, FCuryCode as FCuryCode2, " +
                (analy1 ? dbl.sqlIsNull("FAnalysisCode1", "' '") +
                 " as FAnalysisCode12, " : " ") +
                (analy2 ? dbl.sqlIsNull("FAnalysisCode2", "' '") +
                 " as FAnalysisCode22, " : " ") +
                (analy3 ? dbl.sqlIsNull("FAnalysisCode3", "' '") +
                 " as FAnalysisCode32, " : " ") +
                dbl.sqlIsNull("FBal", "0") + " as FBal, " +
                dbl.sqlIsNull("FBaseCuryBal", "0") + " as FBaseCuryBal, " +
                dbl.sqlIsNull("FPortCuryBal", "0") + " as FPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " where FCheckstate=1 " + strTmpSql +
                " and FPortCode in (" + portCodes +
                ") and (fbal <> 0 or fbaseCuryBal <> 0 or fportCuryBal <> 0 )" +
//               " and (FSubTsfTypeCode <> '06TD' and FSubTsfTypeCode <> '07TD')" +//应收未清算款和应付未清算款不能每日累计
//               " group by FStorageDate, FPortCode,FCashAccCode,FTsfTypeCode,FSubTsfTypeCode,FCuryCode" +
//               (analy1?",FAnalysisCode1":" ") +
//               (analy2?",FAnalysisCode2":" ") +
//               (analy3?",FAnalysisCode3":" ") +
                " )b on a.FPortCode=b.FPortCode2 and a.FCashAccCode=b.FCashAccCode2 and a.FTsfTypeCode=b.FTsfTypeCode2 and a.FSubTsfTypeCode=b.FSubTsfTypeCode2 " +
                (analy1 ? " and a.FAnalysisCode1=b.FAnalysisCode12 " : " ") +
                (analy2 ? " and a.Fanalysiscode2=b.FAnalysisCode22 " : " ") +
                (analy3 ? " and a.FanalysisCode3=b.FAnalysisCode32 " : " ");
          strSql = strSql + " left join ( select FTransDate as FTransDate3, FPortCode as FPortCode3, FCashAccCode as FCashAccCode3, FTsfTypeCode as FTsfTypeCode3, " +
                dbl.sqlIsNull("FSubTsfTypeCode", "' '") +
                " as FSubTsfTypeCode3, FCuryCode as FCuryCode3, " +
                (analy1 ? dbl.sqlIsNull("FAnalysisCode1", "' '") +
                 " as FAnalysisCode13," : " ") +
                (analy2 ? dbl.sqlIsNull("FAnalysisCode2", "' '") +
                 " as FAnalysisCode23, " : " ") +
                (analy3 ? dbl.sqlIsNull("FAnalysisCode3", "' '") +
                 " as FAnalysisCode33," : " ") +
                " sum(-FMoney) as FMoney3, " +
                " sum(-FBaseCuryMoney) as FBaseCuryMoney3," +
                " sum(-FPortCuryMoney) as FPortCuryMoney3 from " +
                " (select FTransDate,FPortCode,FCashAccCode," +
                (analy1 ? "FAnalysisCode1," : " ") +
                (analy2 ? "FAnalysisCode2," : " ") +
                (analy3 ? "FAnalysisCode3," : " ") +
                " FMoney,FBaseCuryMoney,FPortCuryMoney," +
                //---------------------------------以下是处理存款付息以及实收实付清算款
                //应收存款利息'06DE'和存款利息收入'1001'对上，便于取出记录，结转利息时将相应的金额减掉。
                //应收应付未清算款'06TD','07TD和'实收实付清算款'02TD','03TD对上'，便于取出记录，结转清算款时将相应的金额减掉。 胡昆  20070918
                //"02-收入" 跟 “06－应收款项”的资金调拨从第三位开始一样的 fazmm 20071010
                //"03-支出" 跟 “07－应付款项”的资金调拨从第三位开始一样的
                " (case when FTsfTypeCode = '02' then '06'" +
                "  when FTsfTypeCode = '03' then '07' else  FTsfTypeCode end) as FTsfTypeCode," + //处理结转的利息收入，把应收利息冲掉,应收应付库存表中应收利息'06'与应收应付数据表中利息收入'02'对应上。
                " (case when FTsfTypeCode = '02' then '06' " +  dbl.sqlJN() + dbl.sqlSubStr("FSubTsfTypeCode","3") +
                "  when FTsfTypeCode = '03' then '07'"  + dbl.sqlJN() +  dbl.sqlSubStr("FSubTsfTypeCode","3") +
                " else FSubTsfTypeCode end) as FSubTsfTypeCode," +
                //---------------------------------
                " FCuryCode,FCheckState from " +
                pub.yssGetTableName("Tb_Data_CashPayRec") +
                " where FTsfTypeCode in ('02', '03')) " +
                " where FTransDate =" + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes + ")" +
     " and (FMoney <> 0  or FBaseCuryMoney <> 0  or FPortCuryMoney <> 0) " +
                " and FCheckState = 1 " +
                " group by FTransDate, FPortCode, FCashAccCode," +
                (analy1 ? "FAnalysisCode1," : " ") +
                (analy2 ? "FAnalysisCode2," : " ") +
                (analy3 ? "FAnalysisCode3," : " ") +
                "FTsfTypeCode, FSubTsfTypeCode, FCuryCode " +
     " ) e on e.FPortCode3=b.FPortCode2 and e.FCashAccCode3=b.FCashAccCode2 " +
                (analy1 ? " and e.FAnalysisCode13=b.FAnalysisCode12 " : " ") +
                (analy2 ? " and e.Fanalysiscode23=b.FAnalysisCode22 " : " ") +
                (analy3 ? " and e.FanalysisCode33=b.FAnalysisCode32 " : " ") +
                " and e.FTsfTypeCode3=b.FTsfTypeCode2 and e.FSubTsfTypeCode3=b.FSubTsfTypeCode2 ";

          rs = dbl.openResultSet(strSql);
          while (rs.next()) {
             cashrecpaybal = new CashRecPayBalBean();
             cashrecpaybal.setDtStorageDate(rs.getDate("FOperDate"));
             if (rs.getDate("FTransDate") != null &&
                 rs.getDate("FStorageDate") == null) {
                cashrecpaybal.setSPortCode(rs.getString("FPortCode"));
                cashrecpaybal.setSCashAccCode(rs.getString("FCashAccCode"));
                cashrecpaybal.setSAnalysisCode1( (analy1 ?
     rs.getString("FAnalysisCode1") :
                                                  " "));
                cashrecpaybal.setSAnalysisCode2( (analy2 ?
     rs.getString("FAnalysisCode2") :
                                                  " "));
                cashrecpaybal.setSAnalysisCode3( (analy3 ?
     rs.getString("FAnalysisCode3") :
                                                  " "));
                cashrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                cashrecpaybal.setSSubTsfTypeCode(rs.getString(
                      "FSubTsfTypeCode"));
                cashrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
             }
             else {
                cashrecpaybal.setSPortCode(rs.getString("FPortCode2"));
                cashrecpaybal.setSCashAccCode(rs.getString("FCashAccCode2"));
                cashrecpaybal.setSAnalysisCode1( (analy1 ? rs.getString(
                      "FAnalysisCode12") : " "));
                cashrecpaybal.setSAnalysisCode2( (analy2 ? rs.getString(
                      "FAnalysisCode22") : " "));
                cashrecpaybal.setSAnalysisCode3( (analy3 ? rs.getString(
                      "FAnalysisCode32") : " "));
                cashrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode2"));
                cashrecpaybal.setSSubTsfTypeCode(rs.getString(
                      "FSubTsfTypeCode2"));
                cashrecpaybal.setSCuryCode(rs.getString("FCuryCode2"));
             }
             cashrecpaybal.setDBal(
                   YssD.add(YssD.add(rs.getDouble("FMoney"),
                                     rs.getDouble("FMoney3")),
                            rs.getDouble("FBal")));
             cashrecpaybal.setDBaseBal(
                   YssD.add(YssD.add(rs.getDouble("FBaseCuryMoney"),
                                     rs.getDouble("FBaseCuryMoney3")),
                            rs.getDouble("FBaseCuryBal")));
             cashrecpaybal.setDPortBal(
                   YssD.add(YssD.add(rs.getDouble("FPortCuryMoney"),
                                     rs.getDouble("FPortCuryMoney3")),
                            rs.getDouble("FPortCuryBal")));

             all.add(cashrecpaybal);
          }
          dbl.closeResultSetFinal(rs);
//         }
          return all; //返回一个集合 然后再统一插入现金应收应付库存表
       }
       catch (Exception ex) {
          throw new YssException(strError + "\r\n" + ex.getMessage(), ex);
       }
       finally {
          dbl.closeResultSetFinal(rs);
       }
        }*/

    //把资金调拨中发生但未实际调拨的资金和实际调拨的资金存入现金应收应付表  胡昆  20070912
    //项目            调拨类型                                    调拨子类型
    //应收款           06（应收未清算款）                          06TD（应收未清算款项）
    //应付款           07（应付未清算款）                          07TD（应付未清算款项）
    /*
       private void statUnAccData(java.util.Date dDate) throws YssException {
          String strSql = "";
          ResultSet rs = null;

          boolean analy1;
          boolean analy2;
          boolean analy3;

          CashPayRecAdmin prAdmin = new CashPayRecAdmin();
          CashPecPayBean cashpecpay = null;

          double dAvgBaseRate = 1; //平均基础汇率
          double dAvgPortRate = 1; //平均组合汇率

          double dBaseRate = 1;
          double dPortRate = 1;

          double dRecMoney = 0, dBaseRecMoney = 0, dPortRecMoney = 0;
          ;
          double dFactMoney = 0, dBaseFactMoney, dPortFactMoney = 0;

          PortfolioBean port = new PortfolioBean();

          try {
             analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
             analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
             analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

             prAdmin.setYssPub(pub);
             port.setYssPub(pub);
             //往现金应收应付表中插入应收应付的未清算款
             //先从资金调拨中取当日的应收应付款，再减去昨日的应收应付款余额  胡昆  20070918
             strSql = "select * from (" +
                   " select a.*,b.FCuryCode," +
                   " (case when FInOut=1 then '06TD' when FInOut=-1 then '07TD' end) as FSubTsfTypeCode from (" + //流入是应收清算款，流出是应付清算款
     "select FCashAccCode, FPortCode, FInOut, FMoney,FBaseMoney,FPortMoney" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
     " from (select FCashAccCode,FPortCode,FInOut,sum(FMoney) as FMoney," +
                   " sum(Round(FMoney*FBaseCuryRate,2)) as FBaseMoney," + //为了避免误差把Round放在外层  20070926
                   " sum(Round(FMoney*FBaseCuryRate/case when FPortCuryrate=0 then 1 else FPortCuryrate end,2)) as FPortMoney" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
                   " from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                   " a61 join (select FNum,FTsfTypeCode from " +
                   pub.yssGetTableName("Tb_Cash_Transfer") +
                   " where FCheckState = 1 and" +
     " FTransferDate >= " + dbl.sqlDate(dDate) + //把当日的流入流出也算上，统计库存时按照实收实付冲减
                   " and FTransDate <= " + dbl.sqlDate(dDate) +
                   " and FTsfTypeCode = '05'" + //只取调拨类型为“成本”  胡昆  20070918
                   ") a62 on a61.FNum = a62.FNum" +
     " where FPortCode in (" + portCodes + ") and FCheckState = 1" +
                   " group by FCashAccCode,FPortCode,FInOut" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
                   ")) a left join " +
     " (select * from " + pub.yssGetTableName("tb_para_cashaccount") +
     " where FCheckState = 1) b on a.FCashAccCode = b.FCashAccCode) x" +
                   " left join" +
                   //----------------------------------------------------------------以下取昨日的应收应付款余额 胡昆  20070918
                   " (select FCashAccCode,FSubTsfTypeCode," +
                   " FBal as FYesMoney,FBaseCuryBal as FYesBaseMoney,FPortCuryBal as FYesPortMoney" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
                   " from " + pub.yssGetTableName("tb_stock_cashpayrec") +
     " where FCheckState = 1 and FPortCode in (" + portCodes + ")" +
     " and FTsfTypeCode in ('06','07') and FSubTsfTypeCode in ('06TD','07TD')" +
                   " and " + operSql.sqlStoragEve(dDate) +
                   " ) y on x.FCashAccCode = y.FCashAccCode and x.FSubTsfTypeCode = y.FSubTsfTypeCode" +
                   (analy1 ? " and x.FAnalysisCode1=y.FAnalysisCode1" : "") +
                   (analy2 ? " and x.FAnalysisCode2=y.FAnalysisCode2" : "") +
                   (analy3 ? " and x.FAnalysisCode3=y.FAnalysisCode3" : "") +
                   //--------------------------------------------------------以下取前日库存，获取平均汇率
                   " left join (select FCashAccCode," +
                   " FAccBalance,FPortCuryBal,FBaseCuryBal,FPortCuryRate as FPortYesRate,FBaseCuryRate as FBaseYesRate" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
                   " from " + pub.yssGetTableName("tb_stock_cash") +
     " where FCheckState = 1 and FPortCode in (" + portCodes + ")" +
                   " and " + operSql.sqlStoragEve(dDate) +
                   " ) z on x.FCashAccCode = z.FCashAccCode " +
                   (analy1 ? " and x.FAnalysisCode1=z.FAnalysisCode1" : "") +
                   (analy2 ? " and x.FAnalysisCode2=z.FAnalysisCode2" : "") +
                   (analy3 ? " and x.FAnalysisCode3=z.FAnalysisCode3" : "") +
                   //------------------------------------------------------//以下取当日实收实付
                   " left join (select a.* from (" +
                   "select FCashAccCode, FPortCode, FInOut, " +
                   "FMoney as FFactMoney,FBaseMoney as FFactBaseMoney,FPortMoney as FFactPortMoney" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
     " from (select FCashAccCode,FPortCode,FInOut,sum(FMoney) as FMoney," +
                   " sum(Round(FMoney*FBaseCuryRate,2)) as FBaseMoney," +
                   " sum(Round(FMoney*FBaseCuryRate/case when FPortCuryrate=0 then 1 else FPortCuryrate end,2)) as FPortMoney" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
                   " from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                   " a61 join (select FNum,FTsfTypeCode from " +
                   pub.yssGetTableName("Tb_Cash_Transfer") +
                   " where FCheckState = 1 and" +
                   " FTransferDate = " + dbl.sqlDate(dDate) +
                   " and FTsfTypeCode = '05'" + //只取调拨类型为“成本”  胡昆  20070918
                   ") a62 on a61.FNum = a62.FNum" +
     " where FPortCode in (" + portCodes + ") and FCheckState = 1" +
                   " group by FCashAccCode,FPortCode,FInOut" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
                   ")) a left join " +
     " (select * from " + pub.yssGetTableName("tb_para_cashaccount") +
     " where FCheckState = 1) b on a.FCashAccCode = b.FCashAccCode" +
                   " ) w on x.FCashAccCode = w.FCashAccCode " +
                   (analy1 ? " and x.FAnalysisCode1=w.FAnalysisCode1" : "") +
                   (analy2 ? " and x.FAnalysisCode2=w.FAnalysisCode2" : "") +
                   (analy3 ? " and x.FAnalysisCode3=w.FAnalysisCode3" : "");
             rs = dbl.openResultSet(strSql);
             while (rs.next()) {
                cashpecpay = new CashPecPayBean();
                cashpecpay.setTradeDate(dDate);
                cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
                cashpecpay.setPortCode(rs.getString("FPortCode"));
                port.setPortCode(rs.getString("FPortCode"));
                port.getSetting();

                dAvgBaseRate = rs.getDouble("FBaseYesRate");
                dAvgPortRate = rs.getDouble("FPortYesRate");

                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                      rs.getString("FCuryCode"),
                      rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(dDate,
                      port.getCurrencyCode(),
                      rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);

//            if (rs.getInt("FInOut") == -1) { //如果是应付清算款
     dRecMoney = YssD.sub(rs.getDouble("FMoney"),rs.getDouble("FYesMoney"));
                cashpecpay.setMoney(YssD.sub(dRecMoney,rs.getDouble("FFactMoney"))); //当日应付款-当日实付款-昨日应收应付款余额 胡昆  20071005

     dBaseRecMoney = this.getSettingOper().calBaseMoney(dRecMoney, dBaseRate);
     dBaseFactMoney = this.getSettingOper().calBaseMoney(rs.getDouble(
                      "FFactMoney"), dAvgBaseRate);

                cashpecpay.setBaseCuryMoney(YssD.round(YssD.sub(dBaseRecMoney,dBaseFactMoney),2)); //当日应付款*当日汇率-当日实付款*平均汇率-昨日应收应付款余额 胡昆  20071005

                dPortRecMoney = this.getSettingOper().calPortMoney(dRecMoney, dBaseRate, dPortRate);
     dPortFactMoney = this.getSettingOper().calPortMoney(rs.getDouble(
                      "FFactMoney"), dAvgBaseRate, dAvgPortRate);

                cashpecpay.setPortCuryMoney(YssD.round(YssD.sub(dPortRecMoney,dPortFactMoney),2)); //当日应付款*当日汇率-当日实付款*平均汇率-昨日应收应付款余额 胡昆  20071005


//            }

//            if (rs.getInt("FInOut") == -1) { //如果是应付清算款
//               if (cashpecpay.getMoney() < 0) { //如果当日的应付清算款比昨日的应付清算款小，说明当日有清算款实际的流出
//                  dAvgBaseRate = YssD.div(rs.getDouble("FYesBaseMoney"),
//                                          rs.getDouble("FYesMoney"));
//                  dAvgPortRate = YssD.div(rs.getDouble("FYesBaseMoney"),
//                                          rs.getDouble("FYesPortMoney"));
//                  cashpecpay.setBaseCuryMoney(YssD.sub(getSettingOper().
//                        calBaseMoney(rs.getDouble("FMoney"), dAvgBaseRate),
//                        rs.getDouble("FYesBaseMoney"))); //按照平均汇率流出
//                  cashpecpay.setPortCuryMoney(YssD.sub(getSettingOper().
//                        calPortMoney(rs.getDouble("FMoney"), dAvgBaseRate,
//                                     dAvgPortRate),
//                        rs.getDouble("FYesPortMoney"))); //按照平均汇率流出
//
//               }
//               else {
//                  cashpecpay.setBaseCuryMoney(YssD.sub(rs.getDouble(
//                        "FBaseMoney"),
//                        rs.getDouble("FYesBaseMoney")));
//                  cashpecpay.setPortCuryMoney(YssD.sub(rs.getDouble(
//                        "FPortMoney"),
//                        rs.getDouble("FYesPortMoney")));
//               }
//            }
//            else {
//               cashpecpay.setBaseCuryMoney(YssD.sub(rs.getDouble("FBaseMoney"),
//                     rs.getDouble("FYesBaseMoney")));
//               cashpecpay.setPortCuryMoney(YssD.sub(rs.getDouble("FPortMoney"),
//                     rs.getDouble("FYesPortMoney")));
//            }
                cashpecpay.setCuryCode(rs.getString("FCuryCode"));
                if (analy1) {
     cashpecpay.setInvestManagerCode(rs.getString("FAnalysisCode1"));
                }
                else {
                   cashpecpay.setInvestManagerCode(" ");
                }
                if (analy2) {
                   cashpecpay.setCategoryCode(rs.getString("FAnalysisCode2"));
                }
                else {
                   cashpecpay.setCategoryCode(" ");
                }
                cashpecpay.setDataSource(0); //自动
     cashpecpay.setBaseCuryRate(YssD.div(cashpecpay.getBaseCuryMoney(),
                                                    cashpecpay.getMoney(), 8));
     cashpecpay.setPortCuryRate(YssD.div(cashpecpay.getBaseCuryMoney(),
     cashpecpay.getPortCuryMoney(),
                                                    8));
                cashpecpay.checkStateId = 1;
                if (rs.getInt("FInOut") == 1) {
                   cashpecpay.setTsfTypeCode("06"); //应收未清算款
                   cashpecpay.setSubTsfTypeCode("06TD"); //应收未清算款项
                }
                else if (rs.getInt("FInOut") == -1) {
                   cashpecpay.setTsfTypeCode("07"); //应付未清算款
                   cashpecpay.setSubTsfTypeCode("07TD"); //应付未清算款项
                }
                prAdmin.addList(cashpecpay);
             }
//         }
             dbl.closeResultSetFinal(rs);

             //往现金应收应付表中插入实收实付的未清算款   胡昆  20070918
             strSql = " select a.*,b.FCuryCode from (" +
     "select FCashAccCode, FPortCode, FInOut, FMoney,FBaseMoney,FPortMoney" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
     " from (select FCashAccCode,FPortCode,FInOut,sum(FMoney) as FMoney," +
                   " sum(Round(FMoney*FBaseCuryRate,2)) as FBaseMoney," +
                   " sum(Round(FMoney*FBaseCuryRate/case when FPortCuryrate=0 then 1 else FPortCuryrate end,2)) as FPortMoney" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
                   " from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                   " a61 join (select FNum,FTsfTypeCode from " +
                   pub.yssGetTableName("Tb_Cash_Transfer") +
                   " where FCheckState = 1 and" +
                   " FTransferDate = " + dbl.sqlDate(dDate) +
                   " and FTsfTypeCode = '05'" + //只取调拨类型为“成本”  胡昆  20070918
                   ") a62 on a61.FNum = a62.FNum" +
     " where FPortCode in (" + portCodes + ") and FCheckState = 1" +
                   " group by FCashAccCode,FPortCode,FInOut" +
                   (analy1 ? ",FAnalysisCode1" : "") +
                   (analy2 ? ",FAnalysisCode2" : "") +
                   (analy3 ? ",FAnalysisCode3" : "") +
                   ")) a left join " +
     " (select * from " + pub.yssGetTableName("tb_para_cashaccount") +
     " where FCheckState = 1) b on a.FCashAccCode = b.FCashAccCode";

             rs = dbl.openResultSet(strSql);
             while (rs.next()) {
                cashpecpay = new CashPecPayBean();
                cashpecpay.setTradeDate(dDate);
                cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
                cashpecpay.setPortCode(rs.getString("FPortCode"));
                cashpecpay.setMoney(rs.getDouble("FMoney"));
                cashpecpay.setBaseCuryMoney(rs.getDouble("FBaseMoney"));
                cashpecpay.setPortCuryMoney(rs.getDouble("FPortMoney"));
                cashpecpay.setCuryCode(rs.getString("FCuryCode"));
                if (analy1) {
     cashpecpay.setInvestManagerCode(rs.getString("FAnalysisCode1"));
                }
                else {
                   cashpecpay.setInvestManagerCode(" ");
                }
                if (analy2) {
                   cashpecpay.setCategoryCode(rs.getString("FAnalysisCode2"));
                }
                else {
                   cashpecpay.setCategoryCode(" ");
                }
                cashpecpay.setDataSource(0); //自动
     cashpecpay.setBaseCuryRate(YssD.div(cashpecpay.getBaseCuryMoney(),
                                                    cashpecpay.getMoney(), 8));
     cashpecpay.setPortCuryRate(YssD.div(cashpecpay.getBaseCuryMoney(),
     cashpecpay.getPortCuryMoney(),
                                                    8));
                cashpecpay.checkStateId = 1;
                if (rs.getInt("FInOut") == 1) {
                   cashpecpay.setTsfTypeCode("02"); //实收清算款
                   cashpecpay.setSubTsfTypeCode("02TD"); //实收清算款项
                }
                else if (rs.getInt("FInOut") == -1) {
                   cashpecpay.setTsfTypeCode("03"); //实付清算款
                   cashpecpay.setSubTsfTypeCode("03TD"); //实付清算款项
                }
                prAdmin.addList(cashpecpay);
             }
             dbl.closeResultSetFinal(rs);

             //找出当日交收的帐户并往现金应收应付表中插入应收应付款，金额为0。
             //目的是为了冲减应收应付款汇兑损益
//         strSql = "select a.*,b.FCuryCode from (" +
//               "select FCashAccCode, FPortCode, FInOut, FMoney,FBaseMoney,FPortMoney" +
//               (analy1?",FAnalysisCode1":"") +
//               (analy2?",FAnalysisCode2":"") +
//               (analy3?",FAnalysisCode3":"") +
//               " from (select FCashAccCode,FPortCode,FInOut,sum(FMoney) as FMoney," +
//               " sum(Round(FMoney*FBaseCuryRate,2)) as FBaseMoney," +
//               " sum(Round(FMoney*FBaseCuryRate/FPortCuryrate,2)) as FPortMoney" +
//               (analy1?",FAnalysisCode1":"") +
//               (analy2?",FAnalysisCode2":"") +
//               (analy3?",FAnalysisCode3":"") +
//               " from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
//               " a61 join (select FNum,FTsfTypeCode from " +
//               pub.yssGetTableName("Tb_Cash_Transfer") +
//               " where FCheckState = 1 and" +
//               " FTransferDate = " + dbl.sqlDate(dDate) +
//               ") a62 on a61.FNum = a62.FNum" +
//               " where FPortCode in (" + portCodes + ") and FCheckState = 1" +
//               " group by FCashAccCode,FPortCode,FInOut" +
//               (analy1?",FAnalysisCode1":"") +
//               (analy2?",FAnalysisCode2":"") +
//               (analy3?",FAnalysisCode3":"") +
//               ")) a left join " +
//               " (select * from " + pub.yssGetTableName("tb_para_cashaccount") +
//               " where FCheckState = 1) b on a.FCashAccCode = b.FCashAccCode";
//         rs = dbl.openResultSet(strSql);
//         while (rs.next()){
//            cashpecpay = new CashPecPayBean();
//            cashpecpay.setTradeDate(dDate);
//            cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
//            cashpecpay.setPortCode(rs.getString("FPortCode"));
//            cashpecpay.setMoney(0);
//            cashpecpay.setBaseCuryMoney(0);
//            cashpecpay.setPortCuryMoney(0);
//            cashpecpay.setCuryCode(rs.getString("FCuryCode"));
//            if (analy1){
//               cashpecpay.setInvestManagerCode(rs.getString("FAnalysisCode1"));
//            }else{
//               cashpecpay.setInvestManagerCode(" ");
//            }
//            if (analy2){
//               cashpecpay.setCategoryCode(rs.getString("FAnalysisCode2"));
//            }else{
//               cashpecpay.setCategoryCode(" ");
//            }
//            cashpecpay.setDataSource(0);//自动
//            cashpecpay.setBaseCuryRate(1);
//            cashpecpay.setPortCuryRate(1);
//            cashpecpay.checkStateId = 1;
//            if (rs.getInt("FInOut")==1){
//               cashpecpay.setTsfTypeCode("06");//应收未清算款
//               cashpecpay.setSubTsfTypeCode("06TD");//应收未清算款项
//            }else if (rs.getInt("FInOut")==-1){
//               cashpecpay.setTsfTypeCode("07");//应付未清算款
//               cashpecpay.setSubTsfTypeCode("07TD");//应付未清算款项
//            }
//            prAdmin.addList(cashpecpay);
//         }

     prAdmin.insert(dDate, "06,07,02,03", "06TD,07TD,02TD,03TD", portCodes,
                            0, false);
          }
          catch (Exception e) {
             throw new YssException(e);
          }
          finally {
             dbl.closeResultSetFinal(rs);
          }
       }
     */

    /**
     * ETF资产估值时统计当日证券清算款（只统计主动投资，结算日期肯定大于交易日期?）
     */
    private void statETFUnAccData(java.util.Date dDate,String sPortCode,boolean analy1,boolean analy2,boolean analy3) throws YssException {//从外面传分析代码，合并太平版本调整
        String strSql = "";
        ResultSet rs = null;

        //boolean analy1;
        //boolean analy2;
        //boolean analy3;

        CashPayRecAdmin prAdmin = new CashPayRecAdmin();
        CashPecPayBean cashpecpay = null;
        double dBaseRate = 1;
        double dPortRate = 1;

        PortfolioBean port = new PortfolioBean();
        //增加事物控制 add by jiangjin MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try{
            //analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            //analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            //analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

            prAdmin.setYssPub(pub);
            port.setYssPub(pub);
            
            strSql =
                "select x.* from (" + 
                " select a1.FCashAccCode, case when a1.FTradeTypeCode ='06' then 'DV' else 'TD' end as FType, a1.FPortCode, FTotalCost,FBargainDate," + 
                //MS00444 QDV4南方2009年05月10日02_B 添加交易类型的获取 sj
                " a1.FTradeTypeCode," +
                //-------------------------------------------------
                " FTradeAmount,FCost,FCost as FMCost,FCost as FVCost,FBaseCuryCost,FBaseCuryCost as FMBaseCuryCost,FBaseCuryCost as FVBaseCuryCost," + 
                " FPortCuryCost,FPortCuryCost as FMPortCuryCost,FPortCuryCost as FVPortCuryCost," +
                " FBaseCuryRate,FPortCuryRate,a2.FCashInd,a3.FCuryCode as FCashCuryCode, a4.FCatCode" +
                ",a4.FSecurityname as FSecurityName,a4.FSecurityCode as FSecurityCode " +
                " from " +
                //------------------从ETF交易子表中获取主动投资的数据----------------------------
                "(select * from " +
                pub.yssGetTableName("Tb_ETF_SubTrade") +
                " where FPortCode in (" + sPortCode + ")" +
                " and FBargainDate = " + dbl.sqlDate(dDate) +
                " and FETFTradeWayCode = 'ACTIVE'" + 
                ")" +
                //-----------------------------------------------------------------------------------------------------------------------------------
                " a1 left join (select * from Tb_Base_TradeType where FCheckState = 1) a2 on a1.FTradeTypeCode = a2.FTradeTypeCode" +
                //---------------------------------------------------------------
                //---xuqiji 20100714 MS01426    现金账户设置中设置启用日期和银行账号不一致    QDV4赢时胜(测试)2010年07月8日02_B --//
                " left join (select b.* from " +
                " (select max(FStartDate) as FStartDate, FCashAccCode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(dDate) +
                " group by FCashAccCode order by FCashAccCode) a " +
                " join (select * from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(dDate) +
                " ) b on a.fcashacccode = b.fcashacccode and a.FStartDate = b.FStartDate " +
                " ) a3 on a1.FCashAccCode = a3.FCashAccCode" +
                //---------------------------------------------------------------
                " left join (select * from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) a4 on a1.FSecurityCode = a4.FSecurityCode" +
                ") x";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while(rs.next()){
                //--------------------------by 曹丞2009.01.22	增加币种有效性检查 MS00004 QDV4.1-2009.2.1_09A----//
                if (rs.getString("FCashCuryCode") == null || rs.getString("FCashCuryCode").trim().length() == 0) {
                    throw new YssException("系统进行现金库存统计,在统计当日证券清算款和分红数据时检查到代码为【" +
                                           rs.getString("FCashAccCode") +
                                           "】的现金账户对应的货币信息不存在!" + "\n" +
                                           "请核查以下信息：" + "\n" +
                                           "1.【现金账户设置】中该账户信息是否存在且已审核!" + "\n" +
                                           "2.【现金账户设置】中该现金账户代码设置是否正确!");
                }
                //-----------------------------------------------------------------------------------//
                cashpecpay = new CashPecPayBean();
                cashpecpay.setTradeDate(dDate);
                cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
                cashpecpay.setPortCode(rs.getString("FPortCode"));
                port.setPortCode(rs.getString("FPortCode"));
                if (!port.getPortCode().equalsIgnoreCase(rs.getString("FPortCode"))) {
                    port.getSetting();
                }
                if (rs.getString("FCashCuryCode") == null || rs.getString("FCashCuryCode").trim().length() == 0) { //MS00173 QDV4中保2009年01月07日01_B  byleeyu2009-1-15
                    throw new YssException("证券代码为【" + rs.getString("FSecurityCode") + "】,交易日期为【" + YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd") + "】的业务资料的现金帐户为空，请先设置！");
                }
                dBaseRate = rs.getDouble("FBaseCuryRate");
                dPortRate = rs.getDouble("FPortCuryRate");
                cashpecpay.setInvestManagerCode(" ");
                if (analy2) {
                    cashpecpay.setCategoryCode(rs.getString("FCatCode"));
                } else {
                    cashpecpay.setCategoryCode(" ");
                }
                cashpecpay.setDataSource(0); //自动
                cashpecpay.setCuryCode(rs.getString("FCashCuryCode"));
                if (rs.getInt("FCashInd") == -1) { //现金方向流出
                    cashpecpay.setMoney(rs.getDouble("FTotalCost")); //因增加了回购的计算，其包括应收利息。所以金额的获取放入每个明晰。sj edit 20081201
                    cashpecpay.setBaseCuryMoney(this.getSettingOper().
                        calBaseMoney(rs.getDouble(
                            "FTotalCost"), dBaseRate));
                    cashpecpay.setPortCuryMoney(this.getSettingOper().
                        calPortMoney(rs.getDouble(
                            "FTotalCost"), dBaseRate, dPortRate,
                                     //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                     rs.getString("FCashCuryCode"), dDate,
                                     rs.getString("FPortCode")));
                    cashpecpay.setTsfTypeCode("07"); //应付未清算款
                    cashpecpay.setSubTsfTypeCode("07" + rs.getString("FType")); //应付未清算款项
                }else { //流入都按照当日汇率
                    cashpecpay.setMoney(rs.getDouble("FTotalCost")); //因增加了回购的计算，其包括应收利息。所以金额的获取放入每个明晰。sj edit 20081201
                    cashpecpay.setBaseCuryMoney(this.getSettingOper().
                        calBaseMoney(rs.getDouble(
                            "FTotalCost"), dBaseRate));
                    cashpecpay.setPortCuryMoney(this.getSettingOper().
                        calPortMoney(rs.getDouble(
                            "FTotalCost"), dBaseRate, dPortRate,
                                     //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                     rs.getString("FCashCuryCode"), dDate,
                                     rs.getString("FPortCode")));
                    cashpecpay.setTsfTypeCode("06"); //应付未清算款
                    cashpecpay.setSubTsfTypeCode("06" + rs.getString("FType")); //应付未清算款项
                }
                cashpecpay.setBaseCuryRate(dBaseRate);
                cashpecpay.setPortCuryRate(dPortRate);
                cashpecpay.checkStateId = 1;
                prAdmin.addList(cashpecpay);                
            }
            bTrans = true;
            conn.setAutoCommit(false);
            prAdmin.delete("", dDate, dDate, "06", "06TD%", "", "", portCodes, "",
                           "", "", 0);
            prAdmin.delete("", dDate, dDate, "07", "07TD%", "", "", portCodes, "",
                           "", "", 0);
            //2009.04.27 蒋锦 添加 这里先提交去掉 delete 中的锁
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            conn.commit();
            prAdmin.insert(dDate, "06,07",
                           "06TD,07TD,06DV,07DV", portCodes,
                           0, false);
            //处理事务 add by jiangjin MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }catch(Exception e){
        	throw new YssException("系统进行ETF现金库存统计,在统计当日证券清算款时出现异常!\n", e); 
        }finally{
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(bTrans);
        }
    }
    
    /**
     * add by songjie 2012.02.29
     * BUG 3902 QDV4招商基金2012年2月20日01_B
     * 若有已回转的数据，则生成反向的清算款数据  用于冲减  交易日生成的清算款数据
     * @param dDate
     * @param analy1
     * @param analy2
     * @param analy3
     * @throws YssException
     */
    private void dealRollBackData(CashPayRecAdmin prAdmin, java.util.Date dDate,boolean analy1,boolean analy2,boolean analy3) throws YssException{
        StringBuffer strSql = new StringBuffer();
        ResultSet rs = null;
        CashPecPayBean cashpecpay = null;
        double dBaseRate = 1;
        double dPortRate = 1;
    	try{
            
            strSql.append(" select x.* from (select a1.FCashAccCode, 'TD' as FType, a1.FPortCode, ");
            strSql.append(" FTotalCost, FBargainDate, FSettleDate, FFactSettleDate, a1.FInvMgrCode, ");
            strSql.append(" a1.FBrokerCode, a1.FTradeTypeCode, a1.FSettleState, fattrclscode, FTradeAmount, ");
            strSql.append(" FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, FPortCuryCost, ");
            strSql.append(" FMPortCuryCost, FVPortCuryCost, FBaseCuryRate, FPortCuryRate, a2.FCashInd, ");
            strSql.append(" a3.FCuryCode as FCashCuryCode, a4.FCatCode, a5.FInvMgrName as FInvMgrName, ");
            strSql.append(" a6.FBrokerName as FBrokerName, a4.FSecurityname as FSecurityName, ");
            strSql.append(" a4.FSecurityCode as FSecurityCode, a1.FMatureDate as FMatureDate, ");
            strSql.append(" a1.FMatureSettleDate as FMatureSettleDate, a1.FAccruedinterest as FAccruedinterest, ");
            strSql.append(" a1.FCatType as FCatType, a7.FPortCury from (select FCashAccCode,FPortCode,FTotalCost,FBargainDate, ");
            strSql.append(" FSettleDate,FFactSettleDate,FInvMgrCode,FBrokerCode,FTradeTypeCode,FSettleState, ");
            strSql.append(" FTradeAmount, FCost, FMCost, FVCost, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, ");
            strSql.append(" FPortCuryCost, FMPortCuryCost, FVPortCuryCost, FBaseCuryRate, FPortCuryRate,FMatureDate, ");
            strSql.append(" FMatureSettleDate, FAccruedinterest, FSecurityCode, nvl(fattrclscode, ' ') as fattrclscode, ");
            strSql.append(" ' ' as FCatType from " + pub.yssGetTableName("Tb_Data_SubTrade"));
            strSql.append(" where FCheckState = 1 and FPortCode in ( " + this.portCodes);
            strSql.append(" ) and ((FBargainDate <> FFactSettleDate) or (FMatureDate <> FMatureSettleDate)) ");
            strSql.append(" and FSettleState = 2 and FFactSettleDate =  " + dbl.sqlDate(dDate));
            strSql.append(" and FTradeTypeCode not in ('46', 'BInPayDid', 'Drb', 'LOutRecDid', 'Rlr', '06', '24', '25', '41', '51', '45', '62')) a1 ");
            strSql.append(" left join (select * from Tb_Base_TradeType where FCheckState = 1) a2 ");
            strSql.append(" on a1.FTradeTypeCode = a2.FTradeTypeCode ");
            strSql.append(" left join (select b.* from (select max(FStartDate) as FStartDate, FCashAccCode from ");
            strSql.append(pub.yssGetTableName("Tb_Para_CashAccount"));
            strSql.append(" where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(dDate));
            strSql.append(" group by FCashAccCode order by FCashAccCode) a join (select * from ");
            strSql.append(pub.yssGetTableName("Tb_Para_CashAccount"));
            strSql.append(" where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(dDate));
            strSql.append(" ) b on a.fcashacccode = b.fcashacccode and a.FStartDate = b.FStartDate) a3 ");
            strSql.append(" on a1.FCashAccCode = a3.FCashAccCode left join (select * from ");
            strSql.append(pub.yssGetTableName("Tb_Para_Security"));
            strSql.append(" where FCheckState = 1) a4 on a1.FSecurityCode = a4.FSecurityCode left join (select * from ");
            strSql.append(pub.yssGetTableName("Tb_Para_InvestManager"));
            strSql.append(" where FCheckState = 1) a5 on a1.FInvMgrCode = a5.FInvMgrCode left join (select * from ");
            strSql.append(pub.yssGetTableName("Tb_Para_Broker"));
            strSql.append(" where FCheckState = 1) a6 on a1.FBrokerCode = a6.FBrokerCode left join (select FPortCode, FPortCury from ");
            strSql.append(pub.yssGetTableName("tb_para_portfolio") + " where FCheckState = 1) a7 on a1.FPortCode = a7.FPortCode ) x ");

            rs = dbl.openResultSet(strSql.toString());
            while(rs.next()){
                if (rs.getString("FCashCuryCode") == null || rs.getString("FCashCuryCode").trim().length() == 0) {
                    throw new YssException("系统进行现金库存统计,在统计当日证券清算款和分红数据时检查到代码为【" +
                                           rs.getString("FCashAccCode") +"】的现金账户对应的货币信息不存在!" + "\n" +
                                           "请核查以下信息：" + "\n" +
                                           "1.【现金账户设置】中该账户信息是否存在且已审核!" + "\n" +
                                           "2.【现金账户设置】中该现金账户代码设置是否正确!");
                }

                cashpecpay = new CashPecPayBean();
                
                cashpecpay.setTradeDate(dDate);
                cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
                cashpecpay.setPortCode(rs.getString("FPortCode"));
                dBaseRate = rs.getDouble("FBaseCuryRate");
                dPortRate = rs.getDouble("FPortCuryRate");
                cashpecpay.setBaseCuryRate(dBaseRate);
                cashpecpay.setPortCuryRate(dPortRate);
                if (analy1) {
                    cashpecpay.setInvestManagerCode(rs.getString("FInvMgrCode"));
                } else {
                    cashpecpay.setInvestManagerCode(" ");
                }
                if (analy2) {
                    cashpecpay.setCategoryCode(rs.getString("FCATCODE"));
                } else {
                    cashpecpay.setCategoryCode(" ");
                }
                
                cashpecpay.setStrAttrClsCode(rs.getString("fattrclscode"));
                cashpecpay.setDataSource(0); //自动
                cashpecpay.setCuryCode(rs.getString("FCashCuryCode"));
                cashpecpay.setInOutType(-1);
                
                //当实际结算日期大于交易日期时，才产生数据
                if (YssFun.dateDiff(rs.getDate("FFactSettleDate"), dDate) == 0
                   && YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FFactSettleDate")) > 0) {                     	
                	cashpecpay.setMoney(rs.getDouble("FTotalCost"));
                    cashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(rs.getDouble("FTotalCost"), dBaseRate));
                    cashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(rs.getDouble("FTotalCost"), dBaseRate, dPortRate,
                                         rs.getString("FCashCuryCode"), dDate,
                                         rs.getString("FPortCode")));
                    if (rs.getInt("FCashInd") == -1) {
                        cashpecpay.setTsfTypeCode("07"); //应收款项
                        cashpecpay.setSubTsfTypeCode("07TD"); //应收清算款
                    }else{
                        cashpecpay.setTsfTypeCode("06"); //应付款项
                        cashpecpay.setSubTsfTypeCode("06TD"); //应付清算款
                    }
                }
                
                cashpecpay.checkStateId = 1;
                prAdmin.addList(cashpecpay);
            }
    	}catch(Exception e){
    		throw new YssException("处理回转数据出错!", e);
    	}finally{
            dbl.closeResultSetFinal(rs);
    	}
    }
    
    //修改了统计当日证券清算款的方式  胡昆  20071008
    //把原先从资金调拨中取数改成从交易子表中取，因为考虑到有可能延迟结算的问题
   	//方法中添加分析代码参数,优化系统  by leeyu 20100504
   	private void statUnAccData(java.util.Date dDate,boolean analy1,boolean analy2,boolean analy3) throws YssException {
        String strSql = "";
        ResultSet rs = null;

        //boolean analy1;//调整获取分析代码的方式,方法中添加分析代码参数,优化系统  by leeyu 20100504
        //boolean analy2;//调整获取分析代码的方式,方法中添加分析代码参数,优化系统  by leeyu 20100504
        //boolean analy3;//调整获取分析代码的方式,方法中添加分析代码参数,优化系统  by leeyu 20100504

        CashPayRecAdmin prAdmin = new CashPayRecAdmin();
        CashPecPayBean cashpecpay = null;
        //--- MS00444 QDV4南方2009年05月10日02_B 用于在生成回购数据时，克隆已有的对象 sj
        CashPecPayBean reCashpecpay = null;
        //------------------------------------------------------------------------
        //add by zhouxiang 2010.11.30证券借贷交易数据界面由于股利---没有交易结算， 此处冲减库存以后产生资金调拨
        CashTransAdmin transAdmin=new CashTransAdmin();
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        ArrayList alCashTrans=new ArrayList();//资金调拨
        //end by zhouxiang 2010.11.30证券借贷交易数据界面由于股利---没有交易结算， 此处冲减库存以后产生资金调拨
        double dBaseRate = 1;
        double dPortRate = 1;

        PortfolioBean port = new PortfolioBean();
        //增加事物控制 add by jiangjin MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //--- add by songjie 2013.01.30 STORY #3509 需求北京-[建设银行]QDIIV4.0[高]20130124002 start---//
        String tradeCuryCode = "";//证券信息设置中的交易货币
        String accCuryCode = "";//现金账户币种
        //--- add by songjie 2013.01.30 STORY #3509 需求北京-[建设银行]QDIIV4.0[高]20130124002 end---//
        try {
            //analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            //analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            //analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

            prAdmin.setYssPub(pub);
            transAdmin.setYssPub(pub);
            port.setYssPub(pub);
            
            //add by songjie 2012.02.29 BUG 3902 QDV4招商基金2012年2月20日01_B
            //根据已回转的交易数据生成反向的 冲减 清算款的现金应收应付数据
            dealRollBackData(prAdmin,dDate,analy1,analy2,analy3);

			strSql = "select x.* from ("
					+
					// " select a1.FCashAccCode, case when a1.FTradeTypeCode ='06' then 'DV' else 'TD' end as FType, a1.FPortCode, FTotalCost,FBargainDate,FSettleDate,FFactSettleDate,FInvMgrCode,a1.FBrokerCode,"
					// +
					//edited by zhouxiang 2010.11.30  证券借贷 库存统计 现金
					" select a1.FCashAccCode, case when a1.FTradeTypeCode = '06' then 'DV' when a1.FTradeTypeCode in ('BInPayDid','Drb') then  'BDID'"
                    +"when a1.FTradeTypeCode in ('LOutRecDid','Rlr') then 'LDID' when a1.FTradeTypeCode in ('202') then 'IDB' when a1.FTradeTypeCode in ('203') then 'IDS' else 'TD' end as FType, " //modify huangqirong 2012-05-12 story #2565 增加应收替代款-ETF申购
                    //end    by zhouxiang 2010.11.30  证券借贷 库存统计 现金
                    +"a1.FPortCode, FTotalCost,FBargainDate,FSettleDate,FFactSettleDate,a1.FInvMgrCode,a1.FBrokerCode,"
					+ // 这个地方指定投资经理代码取交易子表里的 leeyu MS00121
					// MS00444 QDV4南方2009年05月10日02_B 添加交易类型的获取 sj
					" a1.FTradeTypeCode, a1.FSettleState,fattrclscode,"//NO.125  用户需要对组合按资本类别进行子组合的分类
					+
					// -------------------------------------------------
					" FTradeAmount,FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryCost,FMPortCuryCost,FVPortCuryCost,"
					+ " FBaseCuryRate,FPortCuryRate,a2.FCashInd,a3.FCuryCode as FCashCuryCode, a4.FCatCode"
					//edit by songjie 2012.04.27 BUG 4432 QDV4赢时胜(测试)2012年04月27日03_B 添加 FSubCatCode
					//edit by songjie 2013.01.30 STORY #3509 需求北京-[建设银行]QDIIV4.0[高]20130124002 获取 证券信息设置中的交易货币
					+ ", a5.FInvMgrName as FInvMgrName,a6.FBrokerName as FBrokerName,a4.FSecurityname as FSecurityName,a4.FSecurityCode as FSecurityCode,a4.FSubCatCode as FSubcatCode, a4.Ftradecury as FSecTradeCury "
					+ ", a1.FMatureDate as  FMatureDate,a1.FMatureSettleDate as FMatureSettleDate,a1.FAccruedinterest as FAccruedinterest, a1.FCatType as FCatType" //modify by fangjiang 2011.08.09 BUG 2390
					+ " from "
					+
					// -----------------------调整交易子表的获取筛选位置，将其分成维持之前的处理和回购的到期日期和到期结算日期的处理两个方式处理。sj
					// modify 20081201 MS00063--//
					// add by zhouxiang 2010.11.29 证券借贷---库存统计--统计现金计息
					// 优化代码以便UNION 证券借贷交易数据表
					"(select FCashAccCode,FPortCode,FTotalCost,FBargainDate,FSettleDate,FFactSettleDate,FInvMgrCode,FBrokerCode,FTradeTypeCode,"
					+ "FSettleState,FTradeAmount,FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost, FPortCuryCost,FMPortCuryCost,"
					+ "FVPortCuryCost,FBaseCuryRate,FPortCuryRate,FMatureDate,FMatureSettleDate,FAccruedinterest, FSecurityCode," 
					+ "nvl(fattrclscode,' ') as fattrclscode, ' ' as FCatType from " //NO.125  用户需要对组合按资本类别进行子组合的分类 //modify by fangjiang 2011.08.09 BUG 2390
					// end by zhouxiang 2010.11.29 证券借贷---库存统计--统计现金计息
					// 优化代码以便UNION 证券借贷交易数据表
					+
					pub.yssGetTableName("Tb_Data_SubTrade")
					+" a where FCheckState = 1 and FPortCode in ("
					+ portCodes
					+ ")"+
					// ----MS00444 QDV4南方2009年05月10日02_B
					// 再次调整sql语句，对回购的处理在代码运行时处理。sj
					" and ((FBargainDate <> FFactSettleDate) "
					+ " or (FMatureDate <> FMatureSettleDate))"
					+ // 获取到期日期和到期结算日期不同的
					" and FSettleState <> 2 "+ " and ((FBargainDate = "
					+ dbl.sqlDate(dDate)+ " or FFactSettleDate = "
					+ dbl.sqlDate(dDate)+ ")"+ " or (FMatureDate = "
					+ dbl.sqlDate(dDate)+ " or FMatureSettleDate = "
					+ dbl.sqlDate(dDate)+ ")"
					+ // 在到期日期时也能获取数据进行处理
					//edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B
					//排除 新股流通业务
					//排除分红转投的分红派息数据
					") and FTradeTypeCode <> '46' and  FTradeTypeCode not in ('106','107','204','205')" +
					(this.statCodes.trim().length() > 0 ? " and FCashAccCode in (" + operSql.sqlCodes(this.statCodes) + " ) " : "" ) + //add by huangqirong 2013-04-15 bug #7545 选中的现金账户
					" and not exists (select FTradeNum from " + //modify huangqirong 2012-05-07 Story #2565 ETF联接基金过滤掉申购和赎回及失败时的数据
					pub.yssGetTableName("Tb_Data_DividendToInvest") +
					//edit by songjie 2012.09.18 结算日如果分红派息交易数据关联分红转投数据，则不生成02DV的现金应收应付数据
					" d where d.FCheckState = 1 and d.ftradenum = a.fnum and d.Fpaydate = " + dbl.sqlDate(dDate) + ") "
					//---edit by songjie 2012.03.03 STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A end---//
					//add by zhouxiang 2010.11.30 证券借贷 --库存统计--借贷股利 UNION 证券借贷交易数据表 汇率取的是空值， 使用现金账户和组合去索引
					+ "union all select a.fcashacccode,a.fportcode,ftradeprice,a.fbargaindate,a.fsettledate,a.fsettledate as FFactSettleDate,"//计息为价格，原有的计息借贷界面没有用0替代
					+ " a.finvmgrcode,a.fbrokercode,a.ftradetypecode,1 as fsettledate,a.ftradeamount,a.fcost,a.fmcost,a.fvcost,a.fbasecurycost,"
					+ " a.fmbasecurycost,a.fvbasecurycost,a.fportcurycost,a.fmportcurycost,a.fvportcurycost,(0) as FBaseCuryRate,(0) as FPortCuryRate,"
					+ " a.fperioddate as FMatureDate,a.fperioddate as FMatureSettleDate,0 as FAccruedinterest,a.fsecuritycode,nvl(fattrclscode,' ') as fattrclscode,  ' ' as FCatType"//NO.125  用户需要对组合按资本类别进行子组合的分类//modify by fangjiang 2011.08.09 BUG 2390
					+ " from "
					+ pub.yssGetTableName("tb_DATA_SecLendTRADE")
					+ " a  where FCheckState = 1 and FPortCode in ("
					+ portCodes
					+ ")"
					+ " and ftradetypecode in ("
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Drb)				//冲减借入股利的交易数据03BDID		
					+ ","
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rlr)				//冲减借出股利的交易数据04LDID
					+ ","
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_BInPayDid)		    //产生借入股利的交易数据07BDID
					+ ","
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_LOutRecDid)		//产生借出的股利交易数据06BDID
					+ ")"+ " and FBargainDate= "
					+ dbl.sqlDate(dDate)+
					//add by zhouxiang 2010.11.30 证券借贷 --库存统计--借贷股利 UNION 证券借贷交易数据表 汇率取的是空值， 使用现金账户和组合去索引
					// --------------------------------------------------------------------------------------------------------------------------------------------------------------
					//---add by songjie 2011.05.20 BUG 1955 QDV4博时2011年05月19日01_B 需生成期权交易数据交易日当天的应付清算款（现金应收应付）数据---//
					" union all select b.FCHAGEBAILACCTCODE as FCashAccCode, b.FPORTCODE, b.FSETTLEMONEY as FTotalCost, " +
					" b.FBARGAINDATE, b.FSETTLEDATE, b.FSETTLEDATE as FFactSettleDate, " +
					" b.FINVMGRCODE, b.FBROKERCODE, b.FTRADETYPECODE, b.FSETTLESTATE, " +
					" b.FTRADEAMOUNT, b.FSETTLEMONEY as FCost, b.FSETTLEMONEY as FMCost, " +
					" b.FSETTLEMONEY as FVCost, b.FSETTLEMONEY as FBaseCuryCost, " +
					" b.FSETTLEMONEY as FMBaseCuryCost, b.FSETTLEMONEY as FVBaseCuryCost, " +
					" b.FSETTLEMONEY as FPortCuryCost, b.FSETTLEMONEY as FMPortCuryCost, " +
					" b.FSETTLEMONEY as FVPortCuryCost, b.FBASECURYRATE, b.FPORTCURYRATE, " +
					" b.FSETTLEDATE as FMatureDate, b.FSETTLEDATE as FMatureSettleDate, " +
					" 0 as FAccruedinterest, b.FSECURITYCODE, ' ' as fattrclscode, 'OP' as FCatType" +
					" from " + //modify by fangjiang 2011.08.09 BUG 2390
					pub.yssGetTableName("TB_DATA_OPTIONSTRADE") + " b where b.Fcheckstate = 1 and b.FPortCode in (" + 
					operSql.sqlCodes(this.portCodes) + ") and (b.FBargainDate = " + dbl.sqlDate(dDate) + " or b.FSettleDate = " + dbl.sqlDate(dDate) + ")"+
					//---add by songjie 2011.05.20 BUG 1955 QDV4博时2011年05月19日01_B  需生成期权交易数据交易日当天的应付清算款（现金应收应付）数据---//					
					//---STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A 处理分红转投数据
					" union all select dti.fcurycashacccode as FCashAccCode," +
					"dti.fportcode,dti.fcjcost as FTotalCost,dti.fbusinessdate as FBargainDate," +
					"dti.fpaydate as FSettleDate,dti.fpaydate as FFactSettleDate,sub.finvmgrcode as FInvMgrCode,sub.fbrokercode as FBrokerCode," +
					"'06' as FTradeTypeCode,1 as FSettleState,0 as FTradeAmount," +
					"dti.fcjcost as FCost,dti.fcjcost as FMCost,dti.fcjcost as FVCost," + 
					"dti.fbasecjcost as FBaseCuryCost,dti.fbasecjcost as FMBaseCuryCost,dti.fbasecjcost as FVBaseCuryCost," +
					"dti.fportcjcost as FPortCuryCost,dti.fportcjcost as FMPortCuryCost,dti.fportcjcost as FVPortCuryCost," + 
					" case when dti.fcjcost = 0 then 0 else (dti.fbasecjcost / dti.fcjcost) end as FBaseCuryRate," +
					" case when dti.fportcjcost = 0 then 0 else (dti.fbasecjcost / dti.fportcjcost) end as FPortCuryRate," + 
					"sub.FMatureDate,sub.FMatureSettleDate,dti.freceivemoney as FAccruedinterest,dti.FSecurityCode," + 
					"nvl(sub.fattrclscode, ' ') as fattrclscode,' ' as FCatType from " + pub.yssGetTableName("Tb_Data_DividendToInvest") + 
					" dti left join (select fnum,finvmgrcode,fbrokercode,fattrclscode,FMatureDate,FMatureSettleDate from " +
					pub.yssGetTableName("tb_data_subtrade") + 
					") sub on sub.fnum = dti.ftradenum where dti.fcheckstate = 1 and dti.fportcode in (" + 
					operSql.sqlCodes(this.portCodes) + 
					") and dti.fpaydate = " + dbl.sqlDate(dDate) + 
					")"
					+
					// -----------------------------------------------------------------------------------------------------------------------------------
					" a1 left join (select * from Tb_Base_TradeType where FCheckState = 1) a2 on a1.FTradeTypeCode = a2.FTradeTypeCode"
					+
					// ---------------------------------------------------------------
					// ---xuqiji 20100714 MS01426 现金账户设置中设置启用日期和银行账号不一致
					// QDV4赢时胜(测试)2010年07月8日02_B --//
					" left join (select b.* from "
					+ " (select max(FStartDate) as FStartDate, FCashAccCode from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where FCheckState = 1 and FStartDate <= "
					+ dbl.sqlDate(dDate)
					+ " group by FCashAccCode order by FCashAccCode) a "
					+ " join (select * from "
					+ pub.yssGetTableName("Tb_Para_CashAccount")
					+ " where FCheckState = 1 and FStartDate <= "
					+ dbl.sqlDate(dDate)
					+ " ) b on a.fcashacccode = b.fcashacccode and a.FStartDate = b.FStartDate "
					+ " ) a3 on a1.FCashAccCode = a3.FCashAccCode"
					+
					// ---------------------------------------------------------------
					" left join (select * from "
					+ pub.yssGetTableName("Tb_Para_Security")
					+ " where FCheckState = 1) a4 on a1.FSecurityCode = a4.FSecurityCode"
					+
					// ---------------------------------------------------------------
					" left join (select * from "
					+ pub.yssGetTableName("Tb_Para_InvestManager")
					+ " where FCheckState = 1) a5 on a1.FInvMgrCode = a5.FInvMgrCode "
					+ " left join (select * from "
					+ pub.yssGetTableName("Tb_Para_Broker")
					+ " where FCheckState = 1) a6 on a1.FBrokerCode = a6.FBrokerCode "
					+
					// ---------------------------------------------------------------
					// " where a1.FPortCode in (" + portCodes +
					// ") and a1.FCheckState = 1" +
					// " and (FBargainDate = " + dbl.sqlDate(dDate) +
					// " or FFactSettleDate = " + dbl.sqlDate(dDate) +
					// //----------------------------------------------
					// " or a1.FMatureDate = " + dbl.sqlDate(dDate) +
					// " or a1.FMatureSettleDate = " + dbl.sqlDate(dDate) +
					// ")" +
					// " and (" +
					// "FBargainDate <> FFactSettleDate" +
					// " or  a1.FMatureDate <> a1.FMatureSettleDate )" +
					// //---------------------------------------------------------------
					// " and FSettleState <> 2" + //取结算状态不等于回转的，回转的交易就不能再进去清算款了
					") x";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //----- MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A --------------------------------------------------------------------
                if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZRE) || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_NRE) ||
                		rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMR) || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC) || //add by zhouwei 20120523 bug 4284 买断式回购
                    rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_WSZQ) || 
                    (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_XZWSZQ) && 
                    //edit by songjie 2012.04.27 BUG 4432 QDV4赢时胜(测试)2012年04月27日03_B 添加FI07、FI06判断
                    (rs.getString("FSubCatCode").equals("FI06") || rs.getString("FSubCatCode").equals("FI07"))) ||
                     //update by guolongchao 20111102 bug 3001 添加若为锁定,新债流通,不做处理
                    rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_SD)||rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_XZLT)) {
                    //2009.08.31 蒋锦 修改 如果是新股和新债的网上中签也不作处理 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                    continue; //若为回购业务，则不做处理。继续下一业务的处理
                }
                //-----------------------------------------------------------------------------------------------------------------------
                //--------------------------by 曹丞2009.01.22	增加币种有效性检查 MS00004 QDV4.1-2009.2.1_09A----//
                if (rs.getString("FCashCuryCode") == null || rs.getString("FCashCuryCode").trim().length() == 0) {
                    throw new YssException("系统进行现金库存统计,在统计当日证券清算款和分红数据时检查到代码为【" +
                                           rs.getString("FCashAccCode") +
                                           "】的现金账户对应的货币信息不存在!" + "\n" +
                                           "请核查以下信息：" + "\n" +
                                           "1.【现金账户设置】中该账户信息是否存在且已审核!" + "\n" +
                                           "2.【现金账户设置】中该现金账户代码设置是否正确!");
                }
                //-----------------------------------------------------------------------------------//
                cashpecpay = new CashPecPayBean();
                ArrayList alSubTrans = new ArrayList();//资金子调拨
                cashpecpay.setTradeDate(dDate);
                cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
                cashpecpay.setPortCode(rs.getString("FPortCode"));
                port.setPortCode(rs.getString("FPortCode"));
                String sTradeTypeCode=rs.getString("FTradeTypeCode");
                if (!port.getPortCode().equalsIgnoreCase(rs.getString("FPortCode"))) {
                    port.getSetting();
                }
              
                if (rs.getString("FCashCuryCode") == null || rs.getString("FCashCuryCode").trim().length() == 0) { //MS00173 QDV4中保2009年01月07日01_B  byleeyu2009-1-15
                    throw new YssException("证券代码为【" + rs.getString("FSecurityCode") + "】,交易日期为【" + YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd") + "】的业务资料的现金帐户为空，请先设置！");
                }
                dBaseRate = rs.getDouble("FBaseCuryRate");
                dPortRate = rs.getDouble("FPortCuryRate");
                cashpecpay.setBaseCuryRate(dBaseRate);//将汇率设置移到上面来，以便判断再设置汇率
                cashpecpay.setPortCuryRate(dPortRate);
                if (analy1) {
                    cashpecpay.setInvestManagerCode(rs.getString("FInvMgrCode"));
                } else {
                    cashpecpay.setInvestManagerCode(" ");
                }
                if (analy2) {
                    cashpecpay.setCategoryCode(rs.getString("FCATCODE"));
                } else {
                    cashpecpay.setCategoryCode(" ");
                }
                cashpecpay.setStrAttrClsCode(rs.getString("fattrclscode"));//NO.125  用户需要对组合按资本类别进行子组合的分类
                //add by zhouxiang 2010.11.30 证券借贷库存统计--股利（现金） //由于证券借贷界面没有汇率， 此处使用组合，币种获取汇率
                if (sTradeTypeCode
						.equalsIgnoreCase(YssOperCons.YSS_SECLEND_JYLX_Drb)
						|| sTradeTypeCode
								.equalsIgnoreCase(YssOperCons.YSS_SECLEND_JYLX_Rlr)
						|| sTradeTypeCode
								.equalsIgnoreCase(YssOperCons.YSS_SECLEND_JYLX_BInPayDid)
						|| sTradeTypeCode
								.equalsIgnoreCase(YssOperCons.YSS_SECLEND_JYLX_LOutRecDid)) {
					dBaseRate = this.getSettingOper().getCuryRate(dDate,
							rs.getString("FCashCuryCode"),
							rs.getString("FPortCode"),
							YssOperCons.YSS_RATE_BASE);
					dPortRate = this.getSettingOper().getCuryRate(dDate,
							port.getCurrencyCode(), rs.getString("FPortCode"),
							YssOperCons.YSS_RATE_PORT);
					cashpecpay.setCategoryCode(" ");
				}
                
            	//--- add by songjie 2013.01.30 STORY #3509 需求北京-[建设银行]QDIIV4.0[高]20130124002 start ---//
            	tradeCuryCode = rs.getString("FSecTradeCury");//获取证券信息设置表中的交易货币
            	accCuryCode = rs.getString("FCashCuryCode");//获取现金账户币种
            	//如果交易证券对应的交易货币 不等于  实际交易中 现金账户的币种，则根据现金账户的币种重新获取汇率
            	if(tradeCuryCode != null && accCuryCode != null && !tradeCuryCode.equals(accCuryCode)){
            		//edit by songjie 2013.02.05 BUG 7068 QDV4建行2013年02月4日01_B 业务日期 改为 成交日期
					dBaseRate = this.getSettingOper().getCuryRate(rs.getDate("FBargainDate"),
							rs.getString("FCashCuryCode"),
							rs.getString("FPortCode"),
							YssOperCons.YSS_RATE_BASE);
					//edit by songjie 2013.02.05 BUG 7068 QDV4建行2013年02月4日01_B 业务日期 改为 成交日期
					dPortRate = this.getSettingOper().getCuryRate(rs.getDate("FBargainDate"),
							port.getCurrencyCode(), rs.getString("FPortCode"),
							YssOperCons.YSS_RATE_PORT);
					
					cashpecpay.setBaseCuryRate(dBaseRate);
					cashpecpay.setPortCuryRate(dPortRate);
            	}
            	//--- add by songjie 2013.01.30 STORY #3509 需求北京-[建设银行]QDIIV4.0[高]20130124002 end ---//
                
                //end by zhouxiang 2010.11.30 证券借贷库存统计--股利（现金） 
                cashpecpay.setDataSource(0); //自动
                cashpecpay.setCuryCode(rs.getString("FCashCuryCode"));
                if (rs.getInt("FCashInd") == -1) { //现金方向流出
                    if ((YssFun.dateDiff(dDate, rs.getDate("FFactSettleDate")) > 0
                        && YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FFactSettleDate")) > 0 //MS00461 QDV4赢时胜（上海）2009年5月19日03_B 当实际结算日期大于交易日期时，才产生数据
                        && (YssFun.dateDiff(dDate, rs.getDate("FBargainDate"))==0))||sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_SECLEND_JYLX_BInPayDid)) { //延迟结算(这个时候业务数据中的实际结算日期和结算日期都为同一天，但是结算状态为3)
                    	
                        cashpecpay.setMoney(rs.getDouble("FTotalCost")); //因增加了回购的计算，其包括应收利息。所以金额的获取放入每个明晰。sj edit 20081201
                        cashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(rs.getDouble(
                                "FTotalCost"), dBaseRate));
                        cashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(rs.getDouble(
                                "FTotalCost"), dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCashCuryCode"), dDate,
                                         rs.getString("FPortCode")));
                        cashpecpay.setTsfTypeCode("07"); //应付未清算款
                        cashpecpay.setSubTsfTypeCode("07" + rs.getString("FType")); //应付未清算款项
                    } else if (YssFun.dateDiff(rs.getDate("FFactSettleDate"), dDate) == 0
                               && YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FFactSettleDate")) > 0 //MS00461 QDV4赢时胜（上海）2009年5月19日03_B 当实际结算日期大于交易日期时，才产生数据
                        ) { 
                    	
                    	/************************************************************
                    	 * MS01740 QDV4赢时胜深圳2010年9月14日01_B  
                    	 * 未进行交易结算便估值和延迟结算产生的问题  jiang shichao 2010.09.16
                    	 */
                    	//modify by fangjiang 2011.08.09 BUG 2390
                	    //1.  延迟结算(这个时候业务数据中的实际结算日期和结算日期都为同一天，但是结算状态为3)
                        if( YssFun.dateDiff(dDate, rs.getDate("FFactSettleDate"))==0 
                    	    && rs.getInt("FSettleState")!=1 
                    	    && !"OP".equalsIgnoreCase(rs.getString("FCatType"))
            		      ){
                    		  continue; 
                	    }
                        // --- MS01740 QDV4赢时胜深圳2010年9月14日01_B  未进行交易结算便估值和延迟结算产生的问题  end -----     
                        //-------------end BUG 2390---------------
                    	
                    	//当调拨日期是当前日期时，说明是实付款项，按照前日汇率流出
                        cashpecpay.setMoney(rs.getDouble("FTotalCost")); //因增加了回购的计算，其包括应收利息。所以金额的获取放入每个明晰。sj edit 20081201
                        cashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(rs.getDouble(
                                "FTotalCost"), dBaseRate));
                        cashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(rs.getDouble(
                                "FTotalCost"), dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCashCuryCode"), dDate,
                                         rs.getString("FPortCode")));
                        cashpecpay.setTsfTypeCode("03"); //实付未清算款
                        cashpecpay.setSubTsfTypeCode("03" + rs.getString("FType")); //实付未清算款项
                    }
                    //--------------当交易类型为回购时，在到期时会有一次反向的影式交易。所以与之前的类型相反 sj modify 20081201 MS00063-------//
                    //--- MS00444 QDV4南方2009年05月10日02_B 调整语句执行流程，重新启动一个if判断，而不是之前的一个if语句判断 sj ------------------------------------------------
                    if ( (rs.getString("FTradeTypeCode").equalsIgnoreCase("25") || rs.getString("FTradeTypeCode").equalsIgnoreCase("24")) && //先判断是否为回购 --以下判断雷同不重复注释
                        YssFun.dateDiff(rs.getDate("FMatureDate"), dDate) == 0 && //当日为到期日期
                        YssFun.dateDiff(rs.getDate("FMatureDate"), rs.getDate("FMatureSettleDate")) > 0) { ////到结算到期日期大于到期日期。
                        reCashpecpay = (CashPecPayBean) cashpecpay.clone(); //克隆已有对象，以便将生成的回购信息赋值
                        reCashpecpay.setMoney(YssD.add(rs.
                            getDouble("FAccruedinterest"),
                            rs.getDouble("FTotalCost"))); //金额为成本+应收利息
                        reCashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(YssD.add(rs.
                                                  getDouble("FAccruedinterest"),
                                                  rs.getDouble("FTotalCost")),
                                         dBaseRate)); //使用克隆对象赋值，以下雷同
                        reCashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(YssD.add(rs.
                                                  getDouble("FAccruedinterest"),
                                                  rs.getDouble("FTotalCost")),
                                         dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCashCuryCode"),
                                         dDate,
                                         rs.getString("FPortCode")));
                        reCashpecpay.setTsfTypeCode("06"); //应付未清算款
                        reCashpecpay.setSubTsfTypeCode("06" + rs.getString("FType")); //应付未清算款项
                    } else if ( (rs.getString("FTradeTypeCode").equalsIgnoreCase("25") || rs.getString("FTradeTypeCode").equalsIgnoreCase("24")) && //MS00444 QDV4南方2009年05月10日02_B 若为回购的类型
                               YssFun.dateDiff(rs.getDate("FMatureSettleDate"), dDate) == 0 && //若为到期结算日期，则生成
                               YssFun.dateDiff(rs.getDate("FMatureDate"), rs.getDate("FMatureSettleDate")) > 0) { //到结算到期日期不等于到期日期，则生成一笔收入。
                        reCashpecpay = (CashPecPayBean) cashpecpay.clone(); //克隆已有对象，以便将生成的回购信息赋值
                        reCashpecpay.setMoney(YssD.add(rs.
                            getDouble("FAccruedinterest"),
                            rs.getDouble("FTotalCost"))); //金额为成本+应收利息

                        reCashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(YssD.add(rs.
                                                  getDouble("FAccruedinterest"),
                                                  rs.getDouble("FTotalCost")),
                                         dBaseRate));
                        reCashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(YssD.add(rs.
                                                  getDouble("FAccruedinterest"),
                                                  rs.getDouble("FTotalCost")),
                                         dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCashCuryCode"),
                                         dDate,
                                         rs.getString("FPortCode")));
                        reCashpecpay.setTsfTypeCode("02"); //实付未清算款
                        reCashpecpay.setSubTsfTypeCode("02" + rs.getString("FType")); //实付未清算款项
                    }//add by zhouxiang 2010.11.30 证券借贷 --库存统计--现金应收应付股利--应收应付        
					else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_SECLEND_JYLX_Drb)) {// 如果为证券借贷股利计算----流出类型&&为证券借贷业务YSS_SECLEND_JYLX_Drb--
						reCashpecpay = (CashPecPayBean) cashpecpay.clone();
						//modify by fangjiang 2011.11.23 story 1433
						reCashpecpay.setMoney(rs.getDouble("FTotalCost")); 
						reCashpecpay.setBaseCuryMoney(this.getSettingOper()
								.calBaseMoney(
										rs.getDouble("FTotalCost"),
										dBaseRate));
						reCashpecpay.setPortCuryMoney(this.getSettingOper()
								.calPortMoney(
										rs.getDouble("FTotalCost"),
										dBaseRate, dPortRate,
										rs.getString("FCashCuryCode"), dDate,
										rs.getString("FPortCode")));
						//--------------------------------------------
						reCashpecpay.setTsfTypeCode("03");
						reCashpecpay.setSubTsfTypeCode("03"+ rs.getString("FType")); //证券借贷--实收借入股利   借入资金调拨流出
						//-------------资金调拨-----------------------------------------------------
						alSubTrans.clear();
						transfer = new TransferBean();
						transfer.setCheckStateId(1);
			            transfer.setDtTransDate(rs.getDate("FBargainDate"));
			            transfer.setDtTransferDate(rs.getDate("FSettleDate"));
			            transfer.setStrPortCode(rs.getString("FPortCode"));
			            transfer.setStrSecurityCode(rs.getString("FSecurityCode"));
			            transfer.setStrSubTsfTypeCode("07" + rs.getString("FType"));
			            transfer.setStrTsfTypeCode("07");
			            transfer.setFRelaNum(" ");
			            transfer.setFNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_SECBOWLEND);
			            transfer.setStrAttrClsCode(rs.getString("FCatcode"));
			           
			            transferset = new TransferSetBean();
			            transferset.setIInOut(-1);
			            transferset.setDBaseRate(dBaseRate);
			            transferset.setDPortRate(dPortRate);
			            transferset.setDMoney(rs.getDouble("FTotalCost"));
						if (analy1) {
							transferset.setSAnalysisCode1(rs
									.getString("FInvMgrCode"));
						} else {
							transferset.setSAnalysisCode1(" ");
						}
						if (analy2) {
							transferset.setSAnalysisCode2(rs
									.getString("FCatCode"));
						} else {
							transferset.setSAnalysisCode2(" ");
						}
			            transferset.setSAnalysisCode3(" ");
			            transferset.setSCashAccCode(rs.getString("FCashAccCode"));
			            transferset.setSPortCode(rs.getString("FPortCode"));
			            alSubTrans.add(transferset);
			            transfer.setSubTrans(alSubTrans);
			            alCashTrans.add(transfer);
			           //-------------资金调拨-----------------------------------------------------
					}//end by zhouxiang 2010.11.30 证券借贷 --库存统计--现金应收应付股利--应收应付
                    //----------------------------------------------------------------------------------------------------
                } else { //流入都按照当日汇率
                	//story 1574 add by zhouwei 20111107 交易类型未39 分红转投的交易数据在业务处理-开发是基金业务下产生现金应收应付，所以这里不生成
                	if(rs.getString("FTradeTypeCode").equalsIgnoreCase("39")){
                		continue;
                	}
                    if ((YssFun.dateDiff(dDate, rs.getDate("FFactSettleDate")) > 0
                        && YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FFactSettleDate")) > 0 //MS00461 QDV4赢时胜（上海）2009年5月19日03_B 当实际结算日期大于交易日期时，才产生数据
                        && (YssFun.dateDiff(dDate, rs.getDate("FBargainDate"))==0))||sTradeTypeCode
						.equals(YssOperCons.YSS_SECLEND_JYLX_LOutRecDid)) { //当实际日期不是当前日期时
                        cashpecpay.setMoney(rs.getDouble("FTotalCost")); //因增加了回购的计算，其包括应收利息。所以金额的获取放入每个明晰。sj edit 20081201
                        cashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(rs.getDouble(
                                "FTotalCost"), dBaseRate));
                        cashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(rs.getDouble(
                                "FTotalCost"), dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCashCuryCode"), dDate,
                                         rs.getString("FPortCode")));
                        cashpecpay.setTsfTypeCode("06"); //应付未清算款
                        cashpecpay.setSubTsfTypeCode("06" + rs.getString("FType")); //应付未清算款项
                    } else if (YssFun.dateDiff(rs.getDate("FFactSettleDate"), dDate) == 0
                               && YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FFactSettleDate")) > 0 //MS00461 QDV4赢时胜（上海）2009年5月19日03_B 当实际结算日期大于交易日期时，才产生数据
                        ) { //当调拨日期是当前日期时，说明是实收款项
                    	
                    	/************************************************************
                    	 * MS01740 QDV4赢时胜深圳2010年9月14日01_B  
                    	 * 未进行交易结算便估值和延迟结算产生的问题  jiang shichao 2010.09.16
                    	 */
                      
                    	//1.  延迟结算(这个时候业务数据中的实际结算日期和结算日期都为同一天，但是结算状态为3)
                          if((YssFun.dateDiff(dDate, rs.getDate("FFactSettleDate"))==0&& rs.getInt("FSettleState")!=1)){
                        		continue; 
                        	}
                        // --- MS01740 QDV4赢时胜深圳2010年9月14日01_B  未进行交易结算便估值和延迟结算产生的问题  end ----- 
                      	 
                        cashpecpay.setMoney(rs.getDouble("FTotalCost")); //因增加了回购的计算，其包括应收利息。所以金额的获取放入每个明晰。sj edit 20081201
                        cashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(cashpecpay.getMoney(), dBaseRate));
                        cashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(cashpecpay.getMoney(), dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCashCuryCode"), dDate,
                                         rs.getString("FPortCode")));
                        cashpecpay.setTsfTypeCode("02"); //实收未清算款
                        cashpecpay.setSubTsfTypeCode("02" + rs.getString("FType")); //实收未清算款项
                    }
                    //--------------当交易类型为回购时，在到期时会有一次反向的影式交易。所以与之前的类型相反 sj modify 20081201 MS00063-------//
                    //------  MS00444 QDV4南方2009年05月10日02_B 调整语句执行流程，重新启动一个if判断，而不是之前的一个if语句判断 sj ----------
                    // ---MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A 测试时发现的错误,一并加以修改---
                    if ( (rs.getString("FTradeTypeCode").equalsIgnoreCase("25") || rs.getString("FTradeTypeCode").equalsIgnoreCase("24")) && //先判断是否为回购 --以下判断雷同不重复注释
                    //---------------------------------------------------------------------------------------------------------------------
					//if (rs.getString("FCatCode").equalsIgnoreCase("RE") && //先判断是否为回购 --以下判断雷同不重复注释
                        YssFun.dateDiff(rs.getDate("FFactSettleDate"), dDate) >= 0 && //执行的当前日期要大于等于实际结算日期，避免在结算日时错误生成到期的数据 --以下判断雷同不重复注释
                        YssFun.dateDiff(dDate, rs.getDate("FMatureSettleDate")) > 0) {
                        reCashpecpay = (CashPecPayBean) cashpecpay.clone(); //克隆已有对象，以便将生成的回购信息赋值
                        reCashpecpay.setMoney(YssD.add(rs.
                            getDouble("FAccruedinterest"),
                            rs.getDouble("FTotalCost"))); //金额为成本+应收利息
                        reCashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(YssD.add(rs.
                                                  getDouble("FAccruedinterest"),
                                                  rs.getDouble("FTotalCost")),
                                         dBaseRate));
                        reCashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(YssD.add(rs.
                                                  getDouble("FAccruedinterest"),
                                                  rs.getDouble("FTotalCost")),
                                         dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCashCuryCode"),
                                         dDate,
                                         rs.getString("FPortCode")));
                        reCashpecpay.setTsfTypeCode("07"); //应付未清算款
                        reCashpecpay.setSubTsfTypeCode("07" + rs.getString("FType")); //应付未清算款项
                    } 
                    // ---MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A 测试时发现的错误,一并加以修改---
                    else if ( (rs.getString("FTradeTypeCode").equalsIgnoreCase("25") || rs.getString("FTradeTypeCode").equalsIgnoreCase("24")) &&
                    //---------------------------------------------------------------------------------------------------------------
					//else if (rs.getString("FCatCode").equalsIgnoreCase("RE") &&
                               YssFun.dateDiff(rs.getDate("FMatureSettleDate"), dDate) == 0 &&
                               YssFun.dateDiff(rs.getDate("FFactSettleDate"), dDate) >= 0) {
                        reCashpecpay = (CashPecPayBean) cashpecpay.clone();
                        reCashpecpay.setMoney(YssD.add(rs.
                            getDouble("FAccruedinterest"),
                            rs.getDouble("FTotalCost"))); //金额为成本+应收利息
                        reCashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(YssD.add(rs.
                                                  getDouble("FAccruedinterest"),
                                                  rs.getDouble("FTotalCost")),
                                         dBaseRate));
                        reCashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(YssD.add(rs.
                                                  getDouble("FAccruedinterest"),
                                                  rs.getDouble("FTotalCost")),
                                         dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCashCuryCode"),
                                         dDate,
                                         rs.getString("FPortCode")));
                        reCashpecpay.setTsfTypeCode("03"); //实付未清算款
                        reCashpecpay.setSubTsfTypeCode("03" + rs.getString("FType")); //实付未清算款项
                    }//add by zhouxiang 2010.11.30 证券借贷 --库存统计--现金应收应付股利//业务日期产生应收应付
					else if (sTradeTypeCode
							.equals(YssOperCons.YSS_SECLEND_JYLX_Drb)
							|| sTradeTypeCode
									.equals(YssOperCons.YSS_SECLEND_JYLX_Rlr)) {// 如果为证券借贷股利计算
						reCashpecpay = (CashPecPayBean) cashpecpay.clone();
						alSubTrans.clear();
						//modify by fangjiang 2011.11.23 story 1433
						reCashpecpay.setMoney(rs.getDouble("FTotalCost")); 
						reCashpecpay.setBaseCuryMoney(this.getSettingOper()
								.calBaseMoney(
										rs.getDouble("FTotalCost"),
										dBaseRate));
						reCashpecpay.setPortCuryMoney(this.getSettingOper()
								.calPortMoney(
										rs.getDouble("FTotalCost"),
										dBaseRate, dPortRate,
										// linjunyun 2008-11-25 bug:MS00011
										// 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
										rs.getString("FCashCuryCode"), dDate,
										rs.getString("FPortCode")));
						//----------------------
						reCashpecpay.setTsfTypeCode("02");
						reCashpecpay.setSubTsfTypeCode("02"+ rs.getString("FType")); // 证券借贷--实收借出股利
						
						//-----------------证券借贷----------资金调拨------------------2010.11.30 zhouxiang--------------------------
						transfer = new TransferBean();
			            transfer.setCheckStateId(1);
			            transfer.setDtTransDate(rs.getDate("FBargainDate"));
			            transfer.setDtTransferDate(rs.getDate("FSettleDate"));
			            transfer.setStrPortCode(rs.getString("FPortCode"));
			            transfer.setStrSecurityCode(rs.getString("FSecurityCode"));
			            transfer.setStrSubTsfTypeCode("06" + rs.getString("FType"));
			            transfer.setStrTsfTypeCode("06");
			            transfer.setFRelaNum(" ");
			            transfer.setFNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_SECBOWLEND);
			            transfer.setStrAttrClsCode(rs.getString("FCatcode"));
			           
			            transferset = new TransferSetBean();
			            transferset.setIInOut(1);
			            transferset.setDBaseRate(dBaseRate);
			            transferset.setDPortRate(dPortRate);
			            transferset.setDMoney(rs.getDouble("FTotalCost"));
						if (analy1) {
							transferset.setSAnalysisCode1(rs
									.getString("FInvMgrCode"));
						} else {
							transferset.setSAnalysisCode1(" ");
						}
						if (analy2) {
							transferset.setSAnalysisCode2(rs
									.getString("FCatCode"));
						} else {
							transferset.setSAnalysisCode2(" ");
						}
			            transferset.setSAnalysisCode3(" ");
			            transferset.setSCashAccCode(rs.getString("FCashAccCode"));
			            transferset.setSPortCode(rs.getString("FPortCode"));
			            alSubTrans.add(transferset);
			            transfer.setSubTrans(alSubTrans);
			            if(sTradeTypeCode.equals(YssOperCons.YSS_SECLEND_JYLX_Drb)){//借入时才做资金调拨 借出不需要
			            	alCashTrans.add(transfer);
			            }
			            //-----------------证券借贷----------资金调拨------------------2010.11.30 zhouxiang--------------------------
					}
                    //end by zhouxiang 2010.11.30 证券借贷 --库存统计--现金应收应付股利
                    //----------------------------------------------------------------------------------------------------
                }
                cashpecpay.checkStateId = 1;
                //--- MS00444 QDV4南方2009年05月10日02_B 若有回购的信息,则赋值 sj
                if (null != reCashpecpay) {
                    reCashpecpay.setBaseCuryRate(dBaseRate);
                    reCashpecpay.setPortCuryRate(dPortRate);
                    reCashpecpay.checkStateId = 1;
                }
                //----------------------------------------------------------
                //---------------当为分红时，在备注信息中加入相应的描述信息。sj modify 20081125 暂时 无bug QDV4中保2008年11月24日01_A -
                if (rs.getString("FType").equalsIgnoreCase("DV")) {
                    String sDesc = "";
                    sDesc += "【";
                    sDesc += rs.getDate("FBargainDate") == null ?
                        rs.getDate("FSettleDate") : rs.getDate("FBargainDate"); //调整为业务日期
                    sDesc += "】-";
                    sDesc += "【";
                    sDesc += rs.getString("FSecurityCode");
                    sDesc += rs.getString("FSecurityName") == null ||
                        rs.getString("FSecurityName").trim().length() == 0 ? "" :
                        "-" + rs.getString("FSecurityName");
                    sDesc += "】";
                    sDesc += "-分发派息";
                    sDesc += rs.getString("FInvMgrName") == null ||
                        rs.getString("FInvMgrName").trim().length() == 0 ? "" :
                        "-【" + rs.getString("FInvMgrName") + "】";
                    sDesc += rs.getString("FBrokerName") == null ||
                        rs.getString("FBrokerName").trim().length() == 0 ? "" :
                        "-【" + rs.getString("FBrokerName") + "】";
                    cashpecpay.setDesc(sDesc);
                }//add by zhouxiang 2010.11.30 证券借贷 --库存统计--现金应收应付股利---描述
                else if (rs.getString("FType").equalsIgnoreCase("BDID")||rs.getString("FType").equalsIgnoreCase("LDID")) {
                    String sDesc = "";
                    sDesc += "【";
                    sDesc += rs.getDate("FBargainDate") == null ?
                        rs.getDate("FSettleDate") : rs.getDate("FBargainDate"); //调整为业务日期
                    sDesc += "】-";
                    sDesc += "【";
                    sDesc += rs.getString("FSecurityCode");
                    sDesc += rs.getString("FSecurityName") == null ||
                        rs.getString("FSecurityName").trim().length() == 0 ? "" :
                        "-" + rs.getString("FSecurityName");
                    sDesc += "】";
                    sDesc += "-证券借贷 股利现金";
                    sDesc += rs.getString("FInvMgrName") == null ||
                        rs.getString("FInvMgrName").trim().length() == 0 ? "" :
                        "-【" + rs.getString("FInvMgrName") + "】";
                    sDesc += rs.getString("FBrokerName") == null ||
                        rs.getString("FBrokerName").trim().length() == 0 ? "" :
                        "-【" + rs.getString("FBrokerName") + "】";
                    cashpecpay.setDesc(sDesc);
                }//end by zhouxiang 2010.11.30 证券借贷 --库存统计--现金应收应付股利
                //------------------------------------------------------------------------------------------------------------
                prAdmin.addList(cashpecpay);
                transAdmin.addList(alCashTrans);//add by zhouxiang 2010.11.30
                //--- MS00444 QDV4南方2009年05月10日02_B 若有回购的信息,则添加至容器中 sj ---------
                if (null != reCashpecpay) {
                    prAdmin.addList(reCashpecpay);
                    reCashpecpay = null; //将克隆的对象重新null化，以防在再次循环时因判断条件错误赋值
                }
                //---------------------------------------------------------------------------
            }
            //2009.04.27 蒋锦 添加 添加事务控制
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            bTrans = true;
            conn.setAutoCommit(false);
            prAdmin.delete("", dDate, dDate, "02", "02TD%", this.statCodes, "", portCodes, "", //modify huangqirong 2013-04-16 bug #7545 增加现金账户参数
                           "", "", 0);
            prAdmin.delete("", dDate, dDate, "03", "03TD%", this.statCodes , "" , portCodes, "", //modify huangqirong 2013-04-16 bug #7545 增加现金账户参数
                           "", "", 0);
            prAdmin.delete("", dDate, dDate, "06", "06TD%", this.statCodes, "",  portCodes, "", //modify huangqirong 2013-04-16 bug #7545 增加现金账户参数
                           "", "", 0);
            prAdmin.delete("", dDate, dDate, "07", "07TD%", this.statCodes, "",  portCodes, "", //modify huangqirong 2013-04-16 bug #7545 增加现金账户参数
                           "", "", 0);
            prAdmin.delete("", dDate, dDate, "07,03,06,02", "DID%", this.statCodes, "", portCodes, "", //modify huangqirong 2013-04-16 bug #7545 增加现金账户参数
                    "", "", 0);
            //2009.04.27 蒋锦 添加 这里先提交去掉 delete 中的锁
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            conn.commit();
            prAdmin.insert(dDate, "06,07,02,03",
                           "06TD,07TD,02TD,03TD,06DV,07DV,02DV,03DV,07BDID,06LDID,03BDID,02LDID,06IDB,02IDB,06IDS,02IDS", this.statCodes, portCodes, //modify huangqirong 2012-05-12 story #2565 添加应收替代款-ETF申购   //modify huangqirong 2013-04-16 bug #7545 增加现金账户参数
                           0, false);
            transAdmin.insert("", null,
                    dDate,
                    "07,06", "",
                    "", "", "", "", "",
                    YssOperCons.YSS_SECRECPAY_RELATYPE_SECBOWLEND,
                    "",
                    0, this.statCodes, portCodes, //modify huangqirong 2013-04-16 bug #7545 增加现金账户参数
                    0, "", "", "", true, "");
            //处理事务 add by jiangjin MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("系统进行现金库存统计,在统计当日证券清算款和分红数据时出现异常!\n", e); //by 曹丞 2009.01.22 统计当日证券清算款和分红数据异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(bTrans);
        }
    }

    public ArrayList getStorageStatData(java.util.Date dDate) throws
        YssException {
        ArrayList reList = null;
		//调整获取分析代码的方式,优化系统  by leeyu 20100504
      	boolean analy1 = false;
      	boolean analy2 = false;
      	boolean analy3 = false;
      	try{
			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
			analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
			if (this.isBETFStat()) {// 如果为ETF资产净值统计，则不计算当日的清算款
				String[] sDifferencePortCode = this.getDifferencePortCode();
				statUnAccData(dDate, analy1, analy2, analy3);
				if (sDifferencePortCode[1].trim().length() > 0) {
					statETFUnAccData(dDate, sDifferencePortCode[1], analy1,
							analy2, analy3);
				}
				statUnOtherData(dDate, analy1, analy2, analy3);
				reList = getETFCashRecPayData(dDate, analy1, analy2, analy3);
			} else {
				/* start modify huangqirong 2013-04-17 存款利息和申购款利息 应收应付库存计入库存 */
				if("xjjt".equalsIgnoreCase(this.operType)){
					 String analysisCode1 = operSql.storageAnalysisType("FAnalysisCode1","Cash");	//add by huangqirong 2013-04-22 bug #7582 增加分析代码参数
					 String analysisCode2 = operSql.storageAnalysisType("FAnalysisCode2","Cash");	//add by huangqirong 2013-04-22 bug #7582 增加分析代码参数
					 CashPayRecStorageAdmin cashPayRecStorageAdmin = new CashPayRecStorageAdmin();
					 cashPayRecStorageAdmin.setYssPub(this.pub);
					 
					 reList = this.getAccInterest(dDate, analy1, analy2, analy3);
					 cashPayRecStorageAdmin.setAddList(reList);					 
					 cashPayRecStorageAdmin.insert(this.startDate, this.endDate, 
							 						"06,07", "06DE,07LI,07LXS_DE,06PF", 
							 						this.portCodes, analy1 ? analysisCode1 : "", analy2 ? analysisCode2 : "", this.statCodes, "", ""); //modify by huangqirong 2013-04-22 bug #7582 增加分析代码参数
					 reList = null;
				}else{
					statUnAccData(dDate, analy1, analy2, analy3);
					statUnOtherData(dDate, analy1, analy2, analy3);
					reList = getCashRecPayData(dDate, analy1, analy2, analy3);
				}
				/* end modify huangqirong 2013-04-17 存款利息和申购款利息 计入库存 */
			}
			return reList;
      	}catch(Exception ex){
      		throw new YssException(ex);
      	}
    }
    /**
     * add by huangqirong 2013-04-17 bug #7545  存款利息和申购款利息 计入应收应付库存
     * */
    private ArrayList getAccInterest(java.util.Date dDate,boolean analy1,boolean analy2,boolean analy3) throws YssException {

        String strSql = "", strTmpSql = "";
        ResultSet rs = null;
        CashRecPayBalBean cashrecpaybal = null;
        ArrayList all = new ArrayList();  
        String sKey = "";
        double dBaseRate = 1;
        double dPortRate = 1;
        HashMap hmEveStg = null;
        Iterator iter = null;
	    String sRecPayPara="";
	    CtlPubPara pubPara = new CtlPubPara();
	    pubPara.setYssPub(pub);
	    double dScale = 0;
	    double dBaseMoney = 0;
	    double dPortMoney = 0; 
	    StringBuffer curryDayRecPayStorages = new StringBuffer(); //当日部分库存的一个键
        try {        	
        	
            if (bYearChange) {
        		//太平资产年终结账方式：
        		//yearChange_TP(dDate, portCodes);        	
        		//QDII年终结账方式
        		yearChange(dDate, portCodes);
            }            
            // 因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误 -----//
            String fieldStr = "";
        	fieldStr = " ' ' as fattrclscode, ";
            hmEveStg = getEveStg(dDate);
            strSql = "select FTransDate, FPortCode, FCashAccCode, " +
                "FTsfTypeCode, " +				
                " (case when FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ETF_QUITVALUESell) + //将应付替代款估值增值统计为应付替代款
                " then " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSell) + 
                " when FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ETF_QUITVALUEBuy) + 
                " then " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesBuy) + 
                " when FSubTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMay) + //将现金替代（允许）应付款统计为应付替代款
                " then " + dbl.sqlString(YssOperCons.YSS_ZJDBZLX_ETF_InsteadDuesSell) + 
                //新建买入定存业务，当天计提利息时会把该账户买入“所含利息”显示出来    
                "when FSubTsfTypeCode ="+dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DC_RecInterest)+
                "then "+dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest)+
                " when FSubTsfTypeCode  is null then ' ' " +
                " when FSubTsfTypeCode = '07XZ' then '07GZ' when FSubTsfTypeCode = '06XZ' then '06GZ' "+                
                " when FSubTsfTypeCode = '06DV_TZ' then '06DV'" + //分红转投调整金额冲减应收红利 
                " else FSubTsfTypeCode end )" + 
                " as FSubTsfTypeCode, " +
                "FCuryCode, " +
                (analy1 ? dbl.sqlIsNull("FAnalysisCode1", "' '") +
                 " as FAnalysisCode1," : " ") +
                (analy2 ? dbl.sqlIsNull("FAnalysisCode2", "' '") +
                 " as FAnalysisCode2," : " ") +
                (analy3 ? dbl.sqlIsNull("FAnalysisCode3", "' '") +
                 " as FAnalysisCode3," : " ") +
                 fieldStr +
                " sum(FMoney*FInOut) as FMoney, " +
                " sum(FBaseCuryMoney*FInOut) as FBaseCuryMoney," +
                " sum(FPortCuryMoney*FInOut) as FPortCuryMoney,'Stat' as FType," +
                " FDesc from " +
                pub.yssGetTableName("Tb_Data_CashPayRec") +
                " where FTransDate =" + dbl.sqlDate(dDate) +
                ( this.statCodes.trim().length() > 0 ? " and FCashAccCode in (" + operSql.sqlCodes(this.statCodes) + ") " : "") +  //现金账户
                " and FPortCode in (" + portCodes + ")" +
                " and (FMoney <> 0 or FBaseCuryMoney <> 0 or FPortCuryMoney <> 0)" +
                " and FCheckState = 1 and FTsfTypeCode in ('06', '07') and FSubTsfTypeCode in ('06DE','07LI','07LXS_DE','06PF') " +  //存款利息税 申购款利息
                " group by FTransDate, FPortCode, FCashAccCode" +
                (analy1 ? ", FAnalysisCode1" : " ") +
                (analy2 ? ", FAnalysisCode2" : " ") +
                (analy3 ? ", FAnalysisCode3" : " ") +
                ", FTsfTypeCode, FSubTsfTypeCode, FCuryCode, " +
                " fattrclscode" +               
                ", FDesc " ;               
                
            rs = dbl.queryByPreparedStatement(strSql);
            while (rs.next()) {
            	String sIsDif = " ";
            	if(null != rs.getString("FDesc") && rs.getString("FDesc").indexOf("基金TA尾差调整数据") >= 0) {
            		sIsDif = "TZ";
            	}
                sKey = rs.getString("FCashAccCode") + "\f" +
                    rs.getString("FTsfTypeCode") + "\f" +
                    rs.getString("FSubTsfTypeCode") + "\f" +
                    rs.getString("FPortCode") +
                    (analy1 ? "\f" + rs.getString("FAnalysisCode1") : "") +
                    (analy2 ? "\f" + rs.getString("FAnalysisCode2") : "") +
                    (analy3 ? "\f" + rs.getString("FAnalysisCode3") : "")+
                    "\f"+rs.getString("fattrclscode");
                
                curryDayRecPayStorages.append(sKey).append("\r");
                
                if (hmEveStg.containsKey(sKey)) {
                    cashrecpaybal = (CashRecPayBalBean) hmEveStg.get(sKey);
                    cashrecpaybal.setDtStorageDate(rs.getDate("FTransDate"));
                    cashrecpaybal.setSPortCode(rs.getString("FPortCode"));
                    cashrecpaybal.setSCashAccCode(rs.getString("FCashAccCode"));
                    cashrecpaybal.setSAnalysisCode1( (analy1 ? rs.getString(
                        "FAnalysisCode1") : " "));
                    cashrecpaybal.setSAnalysisCode2( (analy2 ? rs.getString(
                        "FAnalysisCode2") : " "));
                    cashrecpaybal.setSAnalysisCode3( (analy3 ? rs.getString(
                        "FAnalysisCode3") : " "));
                    cashrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                    cashrecpaybal.setSSubTsfTypeCode(rs.getString(
                        "FSubTsfTypeCode"));
                    cashrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
                    
                    cashrecpaybal.setAttrClsCode(rs.getString("fattrclscode"));                   
	               if (sRecPayPara.equalsIgnoreCase("1")&& rs.getString("FType").equalsIgnoreCase("Rush")){
	            	   dScale = YssD.div(rs.getDouble("FMoney"), cashrecpaybal.getDBal());
	            	   dBaseMoney = YssD.round(YssD.mul(cashrecpaybal.getDBaseBal(), dScale), 4); 
	            	   dPortMoney = YssD.round(YssD.mul(cashrecpaybal.getDPortBal(), dScale), 4);
	            	   dBaseRate = YssD.round(YssD.div(dBaseMoney, rs.getDouble("FMoney")), 15);
	             	   dPortRate = YssD.round(YssD.div(dBaseMoney,dPortMoney), 15);  
             	   
	             	   cashrecpaybal.setDBaseRate(dBaseRate);//用移動加權計算出來的匯率去更新業務類別為【收入】數據的匯率。
	             	   cashrecpaybal.setDPortRate(dPortRate);

	             	   cashrecpaybal.setDBal(YssD.add(rs.getDouble("FMoney"), cashrecpaybal.getDBal()));
	             	   cashrecpaybal.setDBaseBal(YssD.add(dBaseMoney,cashrecpaybal.getDBaseBal()));
	             	   cashrecpaybal.setDPortBal( YssD.add(dPortMoney,cashrecpaybal.getDPortBal()));  
             	   
	             	  if(cashrecpaybal.getSTsfTypeCode().equalsIgnoreCase("06")){
	             		 CashPayRecStorageAdmin  cashPayRecStorageAdmin = new CashPayRecStorageAdmin();
	             		 cashPayRecStorageAdmin.setYssPub(pub);
	             		 cashPayRecStorageAdmin.updateAvgRate(cashrecpaybal,dBaseMoney<0?(-dBaseMoney):dBaseMoney,dPortMoney<0?(-dPortMoney):dPortMoney);
	             	  }
	               }else{
                    cashrecpaybal.setDBal(
                        YssD.add(rs.getDouble("FMoney"), cashrecpaybal.getDBal()));
                    cashrecpaybal.setDBaseBal(
                        YssD.add(rs.getDouble("FBaseCuryMoney"),
                                 cashrecpaybal.getDBaseBal()));
                    cashrecpaybal.setDPortBal(
                        YssD.add(rs.getDouble("FPortCuryMoney"),
                                 cashrecpaybal.getDPortBal()));
					}
	            	cashrecpaybal.setsIsDif(sIsDif);
                    hmEveStg.put(sKey, cashrecpaybal);
                } else {
                    cashrecpaybal = new CashRecPayBalBean();
                    cashrecpaybal.setDtStorageDate(dDate);
                    cashrecpaybal.setSPortCode(rs.getString("FPortCode"));
                    cashrecpaybal.setSCashAccCode(rs.getString("FCashAccCode"));
                    cashrecpaybal.setSAnalysisCode1( (analy1 ? rs.getString(
                        "FAnalysisCode1") : " "));
                    cashrecpaybal.setSAnalysisCode2( (analy2 ? rs.getString(
                        "FAnalysisCode2") : " "));
                    cashrecpaybal.setSAnalysisCode3( (analy3 ? rs.getString(
                        "FAnalysisCode3") : " "));
                    cashrecpaybal.setAttrClsCode(rs.getString("fattrclscode"));
                    cashrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                    cashrecpaybal.setSSubTsfTypeCode(rs.getString(
                        "FSubTsfTypeCode"));
                    cashrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
                    cashrecpaybal.setDBal(
                        YssD.add(rs.getDouble("FMoney"), cashrecpaybal.getDBal()));
                    cashrecpaybal.setDBaseBal(
                        YssD.add(rs.getDouble("FBaseCuryMoney"),
                                 cashrecpaybal.getDBaseBal()));
                    cashrecpaybal.setDPortBal(
                        YssD.add(rs.getDouble("FPortCuryMoney"),
                                 cashrecpaybal.getDPortBal()));
 	               	cashrecpaybal.setsIsDif(sIsDif);
                    hmEveStg.put(sKey, cashrecpaybal);
                }
            }
            
            String strTmp = curryDayRecPayStorages.toString() ;
            if(strTmp.length() > 0)
            	strTmp = strTmp.substring(0 , strTmp.length() - 1);
            
            iter = hmEveStg.keySet().iterator();
            while (iter.hasNext()) {
                sKey = (String) iter.next();
                if(strTmp.contains(sKey)){
	                cashrecpaybal = (CashRecPayBalBean) hmEveStg.get(sKey);
	                all.add(cashrecpaybal);
                }
            }
            return all;
        } catch (Exception e) {
            throw new YssException("存款利息和申购款利息 计入应收应付库存出现异常!\n", e); 
        } finally {
            dbl.closeResultSetFinal(rs);
      	}
    }
    
	/**
     * statUnTaData
     * 统计TA应收应付款
     * @param dDate Date
     * BugNo:0000469 1 edit by jc
     */
//   public void statUnTaData(java.util.Date dDate) throws YssException {
//      String strSql = "";
//      ResultSet rs = null;
//      double money;
//
//      boolean analy1;
//      boolean analy2;
//      boolean analy3;
//
//      CashPayRecAdmin prAdmin = new CashPayRecAdmin();
//      CashPecPayBean cashpecpay = null;
//
//      double dBaseRate = 1;
//      double dPortRate = 1;
//      try {
//         analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
//         analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
//         analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
//
//         prAdmin.setYssPub(pub);
//         strSql =
//               " select a1.*," +
//               " FTradeFee1 + FTradeFee2 + FTradeFee3 + FTradeFee4 + FTradeFee5 +" +
//               " FTradeFee6 + FTradeFee7 + FTradeFee8 as FTotalFee," +
//               " a2.FCashInd" +
//               " from " + pub.yssGetTableName("Tb_Ta_Trade") + " a1" +
//               " left join (select * from " + pub.yssGetTableName("Tb_Ta_Selltype") +
//               " where FCheckState = 1) a2" +
//               " on a1.FSellType = a2.FSellTypeCode" +
//               " where a1.FPortCode in (" + portCodes + ")" +
//               " and a1.FCheckState = 1" +
//               " and (FConfimDate = " + dbl.sqlDate(dDate) +
//               " or FSettleDate = " + dbl.sqlDate(dDate) + ")" +
//               " and FConfimDate <> FSettleDate";
//         rs = dbl.openResultSet(strSql);
//         while (rs.next()) {
//            cashpecpay = new CashPecPayBean();
//            cashpecpay.setTradeDate(dDate);
//            cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
//            cashpecpay.setPortCode(rs.getString("FPortCode"));
//
//            dBaseRate = rs.getDouble("FBaseCuryRate");
//            dPortRate = rs.getDouble("FPortCuryRate");
//            if (analy1) {
//               cashpecpay.setInvestManagerCode(rs.getString("FAnalysisCode1"));
//            }
//            else {
//               cashpecpay.setInvestManagerCode(" ");
//            }
//            if (analy2) {
//               cashpecpay.setCategoryCode(rs.getString("FAnalysisCode2"));
//            }
//            else {
//               cashpecpay.setCategoryCode(" ");
//            }
//            cashpecpay.setDataSource(0); //自动
//            cashpecpay.setCuryCode(rs.getString("FCuryCode"));
//
//            if (rs.getInt("FCashInd") == -1) { //现金方向流出
//               if (rs.getDouble("FSettleMoney") != 0) {
//                  money = rs.getDouble("FSettleMoney");
//               }
//               else {
//                  money = YssD.add(rs.getDouble("FSellMoney"),rs.getDouble("FTotalFee"));
//               }
//               cashpecpay.setMoney(money);
//               cashpecpay.setBaseCuryMoney(this.getSettingOper().
//                                           calBaseMoney(money, dBaseRate));
//               cashpecpay.setPortCuryMoney(this.getSettingOper().
//                                           calPortMoney(money, dBaseRate, dPortRate));
//               if (rs.getDate("FConfimDate").equals(dDate)) {
//                  cashpecpay.setTsfTypeCode("07"); //应付
//                  cashpecpay.setSubTsfTypeCode("07TA"); //应付
//               }
//               else {
//                  cashpecpay.setTsfTypeCode("03"); //费用
//                  cashpecpay.setSubTsfTypeCode("03TA"); //费用
//               }
//            }
//            else {  //现金方向流入
//               if (rs.getDouble("FSettleMoney") != 0) {
//                  money = rs.getDouble("FSettleMoney");
//               }
//               else {
//                  money = YssD.sub(rs.getDouble("FSellMoney"),rs.getDouble("FTotalFee"));
//               }
//               cashpecpay.setMoney(money);
//               cashpecpay.setBaseCuryMoney(this.getSettingOper().
//                                           calBaseMoney(money, dBaseRate));
//               cashpecpay.setPortCuryMoney(this.getSettingOper().
//                                           calPortMoney(money, dBaseRate, dPortRate));
//               if (rs.getDate("FConfimDate").equals(dDate)) {
//                  cashpecpay.setTsfTypeCode("06"); //应收
//                  cashpecpay.setSubTsfTypeCode("06TA"); //应收
//               }
//               else {
//                  cashpecpay.setTsfTypeCode("02"); //收入
//                  cashpecpay.setSubTsfTypeCode("02TA"); //收入
//               }
//            }
//            cashpecpay.setBaseCuryRate(dBaseRate);
//            cashpecpay.setPortCuryRate(dPortRate);
//            cashpecpay.checkStateId = 1;
//            prAdmin.addList(cashpecpay);
//         }
//         prAdmin.insert(dDate, "02,03,06,07", "02TA,03TA,06TA,07TA", portCodes, 0, false);
//      }
//      catch (Exception e) {
//         throw new YssException(e);
//      }
//      finally {
//         dbl.closeResultSetFinal(rs);
//      }
//   }

    /**
     * afterSaveStorage sj 20071108 add
     * 防止在现金应收应付库存中有相应的记录（帐户）但现金库存中没有相应的记录
     * 将现金库存中缺少的记录添加进去
     * @param dDate Date
     * @throws YssException
     */

    protected void afterSaveStorage(java.util.Date dDate) throws YssException {
        //System.out.println("afterSaveStorage");
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        CashStorageBean cashstorage = null;
        CashStorageAdmin cashAdmin = new CashStorageAdmin();
        boolean analy1;
        boolean analy2;
        boolean analy3;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------

        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            strSql = "select distinct a.FCashAccCode as AFCashAccCode" +
                ",a.FYearMonth as AFYearMonth" +
                ",a.FStoragedate as AFstoragedate,a.FportCode as AFportCode" +
                ",a.FCuryCode as AFCuryCode" +
                ",d.FPortCury as AFPortCury" +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " ,nvl(a.fattrclscode,' ') as fattrclscode "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                (analy1 ? ",a.FAnalysisCode1 as AFAnalysisCode1" : "") +
                (analy2 ? ",a.FAnalysisCode2 as AFAnalysisCode2" : "") +
                (analy3 ? ",a.FAnalysisCode3 as AFAnalysisCode3" : "") +
                ",(case when b.FCashAccCode is null then ' ' else b.FCashAccCode end) as BFCashAccCode " +
                " from (select * from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " where FPortCode in(" + portCodes + ") " +
                " and FTsfTypeCode in ('06','07') and FStorageDate = " +
                dbl.sqlDate(dDate) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.getYear(dDate) + "00") + //剔除期初数.sj edit 20080827 暂无bug
                ") a " +
                //--------------------------------------------------------------
                " left join (select * from " +
                pub.yssGetTableName("Tb_Stock_Cash") + " where FPortCode in(" +
                portCodes + ")" +
                " and FStorageDate = " + dbl.sqlDate(dDate) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.getYear(dDate) + "00") + //剔除期初数,增加日期筛选.sj edit 20080827 暂无bug
                ") b on a.FYearMonth = b.FYearMonth" +
                " and a.FStoragedate = b.FStoragedate and a.FPortCode = b.FPortCode and a.FCashAccCode = b.FCashAccCode " +
                (analy1 ? " and a.FAnalysisCode1 = b.FAnalysisCode1" : "") +
                (analy2 ? " and a.FAnalysisCode2 = b.FAnalysisCode2" : "") +
                (analy3 ? " and a.FAnalysisCode3 = b.FAnalysisCode3" : "") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " and a.fattrclscode = b.fattrclscode "+ 
                //-------------------------------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                
                " left join (select  FPortCode, FPortCury,FStorageInitDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +                            
                " where  FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +               
                " ) d on a.FPortCode = d.FPortCode";
        
            //end by lidaolong
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getString("BFCashAccCode").trim().length() == 0) {
                    cashstorage = new CashStorageBean();
                    cashstorage.setStrCashAccCode(rs.getString("AFCashAccCode"));
                    cashstorage.setStrStorageDate(YssFun.formatDate(rs.getDate(
                        "AFStoragedate"), "yyyy-MM-dd"));
                    cashstorage.setStrPortCode(rs.getString("AFPortCode"));
                    cashstorage.setStrCuryCode(rs.getString("AFCuryCode"));
                   //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    cashstorage.setStrAttrClsCode(rs.getString("fattrclscode"));
                   //--- NO.125 用户需要对组合按资本类别进行子组合的分类 end -------------------------------// 
                    if (analy1) {
                        cashstorage.setStrFAnalysisCode1(rs.getString(
                            "AFAnalysisCode1"));
                    } else {
                        cashstorage.setStrFAnalysisCode1(" ");
                    }
                    if (analy2) {
                        cashstorage.setStrFAnalysisCode2(rs.getString(
                            "AFAnalysisCode2"));
                    } else {
                        cashstorage.setStrFAnalysisCode2(" ");
                    }
                    if (analy3) {
                        cashstorage.setStrFAnalysisCode3(rs.getString(
                            "AFAnalysisCode3"));
                    } else {
                        cashstorage.setStrFAnalysisCode3(" ");
                    }
                    if (rs.getString("AFYearMonth").trim().length() > 0) {
                        cashstorage.setStrYearMonth(rs.getString("AFYearMonth"));
                    }
                    cashstorage.setStrAccBalance("0");
                    cashstorage.setStrBaseCuryBal("0");
                    cashstorage.setStrPortCuryBal("0");
                    cashstorage.setStrBaseCuryRate(YssFun.formatNumber(this.
                        getSettingOper().getCuryRate(dDate,
                        rs.getString("AFCuryCode"),
                        rs.getString("AFPortCode"),
                        YssOperCons.YSS_RATE_BASE),
                        "#,##0.##"));
//               cashstorage.setStrPortCuryRate(YssFun.formatNumber(this.
//                     getSettingOper().getCuryRate(dDate,
//                                                  rs.getString("AFPortCury"),
//                                                  rs.getString("AFPortCode"),
//                                                  YssOperCons.YSS_RATE_PORT),
//                     "#,##0.##"));
                    //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 --------------------------
                    rateOper.getInnerPortRate(dDate, rs.getString("AFCuryCode"), rs.getString("AFPortCode"));
                    cashstorage.setStrPortCuryRate(YssFun.formatNumber(rateOper.getDPortRate(),
                        "#,##0.##"));
                    //-----------------------------------------------------------------------------------
                    //cashAdmin.setYssPub(pub);
                    cashstorage.checkStateId = 1;
                    cashAdmin.addList(cashstorage);
                    // cashstorage.addSetting();
                    //cashAdmin.insert();
                }
            }
            cashAdmin.setYssPub(pub);
            cashAdmin.insert();
            //-------2008.06.18 蒋锦 添加 现金应收应付库存统计完成后 将原币金额为 0 的数据的基础货币余额和组合货币余额都设为0------//
            String strUpdate = "";
            strUpdate = "UPDATE  " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " SET FPortCuryBal = 0, FBaseCuryBal = 0" +
                " WHERE " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'" +
                " AND FPortCode IN (" + this.portCodes + ")" +
                " AND FStorageDate = " + dbl.sqlDate(dDate) +
                " AND FBal = 0" +
                " AND (FTsfTypeCode = '06' OR FTsfTypeCode = '07')" +
                // 444 QDV4汇添富2010年12月21日01_A by qiuxufeng 20110121 排除尾差调整的现金应收应付数据
                " and FIsDif <> 'TZ'";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strUpdate);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //---------------------------------------------------------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("系统进行现金应收应付库存统计,在统计现金应收应付库存工作完成之后的后续处理时出现异常!\n", e); //by 曹丞 2009.01.22 统计现金应收应付库存工作完成之后的后续处理存异常 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * add by songjie 2013.06.21 
     * STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001
     * "拼接TA应收应付款sql
     * @return
     * @throws YssException
     */
    private String buildTARecPaySql(java.util.Date dDate) throws YssException{
    	StringBuffer sb = new StringBuffer();
    	try{
            	sb.append(" select a1.*,")
            	  .append(" FTradeFee1 + FTradeFee2 + FTradeFee3 + FTradeFee4 + FTradeFee5 +")
            	  .append(" FTradeFee6 + FTradeFee7 + FTradeFee8 as FTotalFee,")
            	  .append(" a2.FCashInd,")
            	  .append(" b.FIsSettle as FTradeFee1Settle, ")
            	  .append(" c.FIsSettle as FTradeFee2Settle, ")
            	  .append(" d.FIsSettle as FTradeFee3Settle, ")
            	  .append(" e.FIsSettle as FTradeFee4Settle, ")
            	  .append(" f.FIsSettle as FTradeFee5Settle, ")
            	  .append(" g.FIsSettle as FTradeFee6Settle, ")
            	  .append(" h.FIsSettle as FTradeFee7Settle, ")
            	  .append(" i.FIsSettle as FTradeFee8Settle ")
            	  .append(" from ").append(pub.yssGetTableName("Tb_Ta_Trade")).append(" a1")
            	  .append(" left join (select * from ")
            	  .append(pub.yssGetTableName("Tb_Ta_Selltype"))
            	  .append(" where FCheckState = 1) a2")
            	  .append(" on a1.FSellType = a2.FSellTypeCode")
            	  .append(" left join (select FFeeCode,FIsSettle from ")
            	  .append(pub.yssGetTableName("Tb_Para_Fee"))
            	  .append(" where fcheckstate = 1) b on a1.ffeecode1 = b.ffeecode")
            	  .append(" left join (select FFeeCode,FIsSettle from ")
            	  .append(pub.yssGetTableName("Tb_Para_Fee"))
            	  .append(" where fcheckstate = 1) c on a1.ffeecode2 = c.ffeecode")
            	  .append(" left join (select FFeeCode,FIsSettle from ")
            	  .append(pub.yssGetTableName("Tb_Para_Fee"))
            	  .append(" where fcheckstate = 1) d on a1.ffeecode3 = d.ffeecode")
            	  .append(" left join (select FFeeCode,FIsSettle from ")
            	  .append(pub.yssGetTableName("Tb_Para_Fee"))
            	  .append(" where fcheckstate = 1) e on a1.ffeecode4 = e.ffeecode")
            	  .append(" left join (select FFeeCode,FIsSettle from ")
            	  .append(pub.yssGetTableName("Tb_Para_Fee"))
            	  .append(" where fcheckstate = 1) f on a1.ffeecode5 = f.ffeecode")
            	  .append(" left join (select FFeeCode,FIsSettle from ")
            	  .append(pub.yssGetTableName("Tb_Para_Fee"))
            	  .append(" where fcheckstate = 1) g on a1.ffeecode6 = g.ffeecode")
            	  .append(" left join (select FFeeCode,FIsSettle from ")
            	  .append(pub.yssGetTableName("Tb_Para_Fee"))
            	  .append(" where fcheckstate = 1) h on a1.ffeecode7 = h.ffeecode")
            	  .append(" left join (select FFeeCode,FIsSettle from ")
            	  .append(pub.yssGetTableName("Tb_Para_Fee"))
            	  .append(" where fcheckstate = 1) i on a1.ffeecode8 = i.ffeecode")
            	  .append(" where a1.FPortCode in (" + portCodes + ")")
            	  .append(" and a1.FCheckState = 1")
            	  .append(" and (FConfimDate = " + dbl.sqlDate(dDate))
            	  .append(" or FSettleDate = " + dbl.sqlDate(dDate) + ")")
            	  //如果资金方向为无，则不产生业务类型为06的数据； modify by wangzuochun 2009.08.17 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A
            	  .append(" and FConfimDate <> FSettleDate and a2.FCashInd <> '0'")
            	  //add by fangjiang 2011.04.06 BUG #1302  分红、分红转投、红利发放 已在业务处理中处理
            	  .append(" and a2.FSellTypeCode not in ('03', '08', '10') "); 
    		
    		return sb.toString();
    	}catch(Exception e){
    		throw new YssException("拼接TA应收应付款sql",e);
    	}
    } 
    
    /**
     * 统计除了证券清算款/红利之外的其他应收应付款
     * 统计 TA 应收应付 2008-10-23 蒋锦 修改 BUG：0000469
     * @param dDate Date
     * @throws YssException
     */
    private void statUnOtherData(java.util.Date dDate,boolean analy1,boolean analy2,boolean analy3) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double money;

        CashPayRecAdmin prAdmin = new CashPayRecAdmin();
        CashPecPayBean cashpecpay = null;

        double dBaseRate = 1;
        double dPortRate = 1;

        PortfolioBean port = new PortfolioBean();
        //增加事物控制 add by jiangjin MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String preCode = "";
        String redeemFeeFlag = "";
        //--- add by songjie 2013.06.21 STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001 start---//
        boolean delete03TA_Fee = false;//用于判断是否删除 03TA_FEE的数据
        //--- add by songjie 2013.06.21 STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001 end---//
        try {
            prAdmin.setYssPub(pub);
            port.setYssPub(pub);
            CtlPubPara pubPara = new CtlPubPara();
			pubPara.setYssPub(pub);//add by yeshenghong 20130313
            //--------------------------统计 TA 交易应收应付----------------------------//
            //2008.10.23 蒋锦 修改 BUG：0000469
			//--- edit by songjie 2013.06.21 STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001 start---//
			//获取TA交易数据中各个费用对应的费用设置 清算项参数
            rs = dbl.queryByPreparedStatement(buildTARecPaySql(dDate)); //modify by fangjiang 2011.08.14 STORY #788
            //--- edit by songjie 2013.06.21 STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001 end---//
            while (rs.next()) {
                cashpecpay = new CashPecPayBean();
                cashpecpay.setTradeDate(dDate);
                cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
                cashpecpay.setPortCode(rs.getString("FPortCode"));
                
                if(!preCode.equals(rs.getString("FPortCode")))//add by yeshenghong 多币种赎回费款分开显示
                {
                	preCode = rs.getString("FPortCode");

                	redeemFeeFlag = pubPara.getRedeemFeeMethod(preCode);

                }
                
                dBaseRate = rs.getDouble("FBaseCuryRate");
                dPortRate = rs.getDouble("FPortCuryRate");
                if (analy1) {
                    cashpecpay.setInvestManagerCode(rs.getString("FAnalysisCode1"));
                } else {
                    cashpecpay.setInvestManagerCode(" ");
                }
                if (analy2) {
                    cashpecpay.setCategoryCode(rs.getString("FAnalysisCode2"));
                } else {
                    cashpecpay.setCategoryCode(" ");
                }
                cashpecpay.setDataSource(0); //自动
                cashpecpay.setCuryCode(rs.getString("FCuryCode"));

                if (rs.getInt("FCashInd") == -1) { //现金方向流出
                    if (rs.getDouble("FSettleMoney") != 0) {
                        money = rs.getDouble("FSettleMoney");
                    } else {
                        //money = YssD.add(rs.getDouble("FSellMoney"), rs.getDouble("FTotalFee")); //modify huangqirong 2013-01-15 BUG# 6856 说是直接取销售金额
                    	money = rs.getDouble("FSellMoney"); //add by huangqirong 2013-01-15 BUG# 6856 说是直接取销售金额
                    }
                    cashpecpay.setMoney(money);
                    cashpecpay.setBaseCuryMoney(this.getSettingOper().
                                                calBaseMoney(money, dBaseRate));
                    cashpecpay.setPortCuryMoney(this.getSettingOper().
                                                calPortMoney(money, dBaseRate,
                        dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")));
                    if (rs.getDate("FConfimDate").equals(dDate)) {
                        //还原之前的处理模式 by leeyu 20090708 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 
                        cashpecpay.setTsfTypeCode("07"); //应付
                        //---edit by songjie 2012.10.25 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
                        if(rs.getString("FSellType").equals("05")){//若销售类型为转出
                        	cashpecpay.setSubTsfTypeCode("07TAZC"); //应付转出款
                        }else{
                        	cashpecpay.setSubTsfTypeCode("07TA"); //应付赎回款
                        }
                        //---edit by songjie 2012.10.25 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
                        //还原之前的处理模式 by leeyu 20090708 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 此业务有重新处理
                    } else {
                        //还原之前的处理模式 by leeyu 20090708 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 此业务有重新处理
                        cashpecpay.setTsfTypeCode("03"); //费用
                        //---edit by songjie 2012.10.25 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
                        if(rs.getString("FSellType").equals("05")){//若销售类型为转出
                        	cashpecpay.setSubTsfTypeCode("03TAZC"); //实付转出款
                        }else{
                        	cashpecpay.setSubTsfTypeCode("03TA"); //实付赎回款
                        }
                        //---edit by songjie 2012.10.25 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
                        
                        //还原之前的处理模式 by leeyu 20090708 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 此业务有重新处理
                    }
                } else { //现金方向流入
                    if (rs.getDouble("FSettleMoney") != 0) {
                        money = rs.getDouble("FSettleMoney");
                    } else {
                        //money = YssD.sub(rs.getDouble("FSellMoney"), rs.getDouble("FTotalFee"));//modify huangqirong 2013-01-15 BUG# 6856 说是直接取销售金额
                        money = rs.getDouble("FSellMoney"); //add by huangqirong 2013-01-15 BUG# 6856 说是直接取销售金额
                    }
                    cashpecpay.setMoney(money);
                    cashpecpay.setBaseCuryMoney(this.getSettingOper().
                                                calBaseMoney(money, dBaseRate));
                    cashpecpay.setPortCuryMoney(this.getSettingOper().
                                                calPortMoney(money, dBaseRate,
                        dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")));
                    if (rs.getDate("FConfimDate").equals(dDate)) {
                        //还原之前的处理模式 by leeyu 20090708 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 此业务有重新处理
                        cashpecpay.setTsfTypeCode("06"); //应收
                        //---edit by songjie 2012.10.25 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
                        if(rs.getString("FSellType").equals("04")){//若销售类型为 转入
                        	cashpecpay.setSubTsfTypeCode("06TAZR"); //应收转入款
                        }else{
                        	cashpecpay.setSubTsfTypeCode("06TA"); //应收申购款
                        }
                        //---edit by songjie 2012.10.25 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
                        
                        //还原之前的处理模式 by leeyu 20090708 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 此业务有重新处理
                    } else {
                        //还原之前的处理模式 by leeyu 20090708 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 此业务有重新处理
                        cashpecpay.setTsfTypeCode("02"); //收入
                        //---edit by songjie 2012.10.25 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 start---//
                        if(rs.getString("FSellType").equals("04")){//若销售类型为转入
                        	cashpecpay.setSubTsfTypeCode("02TAZR"); //转入款收入
                        }else{
                        	cashpecpay.setSubTsfTypeCode("02TA"); //申购款收入
                        }
                        //---edit by songjie 2012.10.25 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 end---//
                        
                        //还原之前的处理模式 by leeyu 20090708 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 此业务有重新处理
                    }
                }
                if(!redeemFeeFlag.equals("")&&rs.getString("FSellType").equals("02"))//add by yeshenghong 多币种赎回费款分开显示
                {
                	if(cashpecpay.getSubTsfTypeCode().equals("07TA")&& dbl.isFieldExist(rs, redeemFeeFlag))//添加判断 防止报错 yeshenghong 20130412
                	{
                		CashPecPayBean redeempay = (CashPecPayBean)cashpecpay.clone();
                		money = rs.getDouble(redeemFeeFlag);
                		redeempay.setSubTsfTypeCode("07TA_Fee");
                		redeempay.setMoney(money);
                		redeempay.setBaseCuryMoney(this.getSettingOper().
                                calBaseMoney(money, dBaseRate));
                		redeempay.setPortCuryMoney(this.getSettingOper().
                                calPortMoney(money, dBaseRate,
                                		dPortRate,
                                rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")));
                		
                		redeempay.setBaseCuryRate(dBaseRate);
                		redeempay.setPortCuryRate(dPortRate);
                		redeempay.checkStateId = 1;
                		prAdmin.addList(redeempay);
						//--- edit by songjie 2013.06.21 STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001 start---//
						//如果该费用设置的清算项设置为是，则 赎回款金额 = 实际结算金额 - 应付赎回费
                		if(rs.getInt(redeemFeeFlag + "Settle") == 1){
                			money = cashpecpay.getMoney() - money;
                			cashpecpay.setMoney(money);
                			cashpecpay.setBaseCuryMoney(this.getSettingOper().
                                                    calBaseMoney(money, dBaseRate));
                			cashpecpay.setPortCuryMoney(this.getSettingOper().
                                                    calPortMoney(money, dBaseRate,
                            dPortRate,
                            rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")));
                		}
						//--- edit by songjie 2013.06.21 STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001 end---//
                	}
                }
                
                if(cashpecpay.getSubTsfTypeCode().equals("03TA")&& dbl.isFieldExist(rs, redeemFeeFlag))//添加判断 防止报错   yeshenghong 20130412
            	{
					//--- edit by songjie 2013.06.21 STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001 start---//
                	money = rs.getDouble(redeemFeeFlag);
				    //如果该费用设置的清算项设置为是，则 赎回款金额 = 实际结算金额 - 应付赎回费,且结算日自动生成  03TA_Fee 冲减数据
            		if(rs.getInt(redeemFeeFlag + "Settle") == 1){
            			if(!delete03TA_Fee){
            				delete03TA_Fee = true;
            			}
                		CashPecPayBean redeempay = (CashPecPayBean)cashpecpay.clone();//add by yeshenghong bug7462 20130409
                		redeempay.setSubTsfTypeCode("03TA_Fee");
                		redeempay.setMoney(money);
                		redeempay.setBaseCuryMoney(this.getSettingOper().
                                calBaseMoney(money, dBaseRate));
                		redeempay.setPortCuryMoney(this.getSettingOper().
                                calPortMoney(money, dBaseRate,
                                		dPortRate,
                                rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")));
                		
                		redeempay.setBaseCuryRate(dBaseRate);
                		redeempay.setPortCuryRate(dPortRate);
                		redeempay.checkStateId = 1;
                		prAdmin.addList(redeempay);
            			
            			money = cashpecpay.getMoney() - money;
            			cashpecpay.setMoney(money);
            			cashpecpay.setBaseCuryMoney(this.getSettingOper().
                                                calBaseMoney(money, dBaseRate));
            			cashpecpay.setPortCuryMoney(this.getSettingOper().
                                                calPortMoney(money, dBaseRate,
                        dPortRate,
                        rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")));
            		}
					//--- edit by songjie 2013.06.21 STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001 end---//
            	}
                cashpecpay.setBaseCuryRate(dBaseRate);
                cashpecpay.setPortCuryRate(dPortRate);
                cashpecpay.checkStateId = 1;
                prAdmin.addList(cashpecpay);
            }
            //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.closeResultSetFinal(rs);
            //----------------------------------------------------------------------//

            strSql = "select * from (" +
                " select a.*,b.FCuryCode from (" + //流入是应收清算款，流出是应付清算款
                "select FCashAccCode, FPortCode, FInOut, FMoney,FBaseCuryRate,FPortCuryRate,FTransferDate,FTransDate,FTsfTypeCode ,Fsubtsftypecode" +//modify huangqirong 2012-05-07 story #2565
                (analy1 ? ",FAnalysisCode1" : "") +
                (analy2 ? ",FAnalysisCode2" : "") +
                (analy3 ? ",FAnalysisCode3" : "") +
                " from (select FCashAccCode,FPortCode,FInOut,FMoney,FBaseCuryRate,FPortCuryRate,FTransferDate,FTransDate,FTsfTypeCode,Fsubtsftypecode" +//modify huangqirong 2012-05-07 story #2565
                (analy1 ? ",FAnalysisCode1" : "") +
                (analy2 ? ",FAnalysisCode2" : "") +
                (analy3 ? ",FAnalysisCode3" : "") +
                " from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " a61 join (select b.FNum,b.FTsfTypeCode,b.FTransferDate,b.FTransDate ,b.Fsubtsftypecode from " + //modify huangqirong 2012-05-07 story #2565
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " b where b.FCheckState = 1 and" +
                " (b.FTransferDate = " + dbl.sqlDate(dDate) + //把当日的流入流出也算上，统计库存时按照实收实付冲减
                " or b.FTransDate = " + dbl.sqlDate(dDate) +
                ") and b.FTransDate < b.FTransferDate" +
                " and (b.FTradeNum is null or b.FTradeNum = '' or b.fsubtsftypecode in ('05CR','05CB'))" + //取交易编号为null的，这些数据都不是从交易数据中结算过来的  //modify huangqirong 2012-05-07 story #2565
                //------- modify by wangzuochun 2009.09.05 MS00671 债券付息后没有生成相应的现金应收应付 QDV4建行2009年9月02日02_B
                //-------调拨日期与业务日期不一致，在资产估值时，查出债券利息收益支付时产生的资金调拨，以便在下面产生相应的现金应收应付
                //--- edit by songjie 2013.06.09 BUG 8199 QDV4赢时胜(上海)2013年06月07日01_B start---//
                //添加 b.FNumType = 'Forward' 的情况  即可根据远期外汇交易数据生成的资金调拨数据生成现金应收应付数据
                " and ((b.FRelaNum is null or b.FRelaNum = '') or b.FNumType in ('BondPaid','Forward'))" + //取关联编号为null的，这些数据都不是从TA交易数据结算过来的  BugNo:0000469 1 edit by jc 
                //--- edit by songjie 2013.06.09 BUG 8199 QDV4赢时胜(上海)2013年06月07日01_B end---//
                ( this.statCodes.trim().length() > 0 ? " and b.fsrccashacc in (" + operSql.sqlCodes(this.statCodes) + ") " : "" ) + //add by huangqirong 2013-04-15 bug #7545 选中的现金账户
                //----------------------------------MS00671-------------------------------//
                ") a62 on a61.FNum = a62.FNum" +
                " where FPortCode in (" + portCodes + ") and FCheckState = 1" +
                ") t) a left join " +
                
                //---xuqiji 20100714 MS01426    现金账户设置中设置启用日期和银行账号不一致    QDV4赢时胜(测试)2010年07月8日02_B --//
                " (select b.* from " +
                " (select max(FStartDate) as FStartDate, FCashAccCode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(dDate) +
                " group by FCashAccCode order by FCashAccCode) a " +
                " join (select * from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(dDate) +
                " ) b on a.fcashacccode = b.fcashacccode and a.FStartDate = b.FStartDate " +
                " ) b on a.FCashAccCode = b.FCashAccCode) x";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //------------------by 曹丞 2009.01.22 增加币种有效性检查----------------------------------------//
                if (rs.getString("FCuryCode") == null || rs.getString("FCuryCode").trim().length() == 0) {
                    throw new YssException("系统进行现金库存统计,在统计除证券清算款和分红数据之外的其他应收应付款时,检查到代码为【" +
                                           rs.getString("FCashAccCode") +
                                           "】的现金账户对应的币种信息不存在!" + "\n" +
                                           "请核查以下信息：" + "\n" +
                                           "1.【现金账户设置】中该账户信息是否存在且已审核!" + "\n" +
                                           "2.【现金账户设置】中该现金账户代码项设置是否正确!");

                }
                //------------------------------------------------------------------------------------------//
                cashpecpay = new CashPecPayBean();
                cashpecpay.setTradeDate(dDate);
                cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
                cashpecpay.setPortCode(rs.getString("FPortCode"));
                port.setPortCode(rs.getString("FPortCode"));
                if (!port.getPortCode().equalsIgnoreCase(rs.getString("FPortCode"))) {
                    port.getSetting();
                }

                if (analy1) {
                    cashpecpay.setInvestManagerCode(rs.getString("FAnalysisCode1"));
                } else {
                    cashpecpay.setInvestManagerCode(" ");
                }
                if (analy2) {
                    cashpecpay.setCategoryCode(rs.getString("FAnalysisCode2"));
                } else {
                    cashpecpay.setCategoryCode(" ");
                }
                cashpecpay.setDataSource(0); //自动
                cashpecpay.setCuryCode(rs.getString("FCuryCode"));
                cashpecpay.setMoney(rs.getDouble("FMoney"));
                dBaseRate = rs.getDouble("FBaseCuryRate");
                dPortRate = rs.getDouble("FPortCuryRate");
//            dBaseRate = this.getSettingOper().getCuryRate(dDate,
//                  rs.getString("FCuryCode"),
//                  rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
//            dPortRate = this.getSettingOper().getCuryRate(dDate,
//                  port.getCurrencyCode(),
//                  rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);

                if (rs.getInt("FInOut") == -1) { //流出
                    if (YssFun.dateDiff(dDate, rs.getDate("FTransferDate")) > 0) { //当调拨日期不是当前日期时
                        if (YssFun.dateDiff(rs.getDate("FTransDate"),
                                            rs.getDate("FTransferDate")) > 0) { //当调拨日期比业务日期大,说明是应付款项,按照当日汇率计算
                            cashpecpay.setBaseCuryMoney(this.getSettingOper().
                                calBaseMoney(rs.getDouble(
                                    "FMoney"), dBaseRate));
                            cashpecpay.setPortCuryMoney(this.getSettingOper().
                                calPortMoney(rs.getDouble(
                                    "FMoney"), dBaseRate, dPortRate,
                                             //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                             rs.getString("FCuryCode"), dDate,
                                             rs.getString("FPortCode")));
                        }
                        cashpecpay.setTsfTypeCode("07"); //应付款
                        if (rs.getString("FTsfTypeCode").equalsIgnoreCase("01")) { //调拨类型为“内部调拨”
                            cashpecpay.setSubTsfTypeCode("07CE"); //应付换汇拆借款
                        } else if (rs.getString("FTsfTypeCode").equalsIgnoreCase("04")) { //调拨类型为“资本”
                            cashpecpay.setSubTsfTypeCode("07TA"); //应付赎回款
                        }
                        //add by huangqirong 2012-05-07 story #2565
                        else if(rs.getString("FTsfTypeCode").equalsIgnoreCase("05") && rs.getString("FSubTsfTypeCode").equalsIgnoreCase("05CR")){ 
                        	
                        	cashpecpay.setSubTsfTypeCode("07CR");
                        }else if(rs.getString("FTsfTypeCode").equalsIgnoreCase("05") && rs.getString("FSubTsfTypeCode").equalsIgnoreCase("05CB")){ 
                        	
                        	cashpecpay.setSubTsfTypeCode("07CB");
                        }
                        //---end---
                        else {
                            cashpecpay.setSubTsfTypeCode("07OT"); //应付其他款项
                        }
                    } else if (YssFun.dateDiff(rs.getDate("FTransferDate"), dDate) ==
                               0) { //当调拨日期是当前日期时，说明是实付款项，按照前日汇率流出
                        cashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(rs.getDouble(
                                "FMoney"), dBaseRate));
                        cashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(rs.getDouble(
                                "FMoney"), dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCuryCode"), dDate,
                                         rs.getString("FPortCode")));
                        cashpecpay.setTsfTypeCode("03");
                        if (rs.getString("FTsfTypeCode").equalsIgnoreCase("01")) { //调拨类型为“内部调拨”
                            cashpecpay.setSubTsfTypeCode("03CE"); //支付换汇拆借款
                        } else if (rs.getString("FTsfTypeCode").equalsIgnoreCase("04")) { //调拨类型为“资本”
                            cashpecpay.setSubTsfTypeCode("03TA"); //支付赎回款
                        }
                        //add by huangqirong 2012-05-07 story #2565
                        else if(rs.getString("FTsfTypeCode").equalsIgnoreCase("05") && rs.getString("FSubTsfTypeCode").equalsIgnoreCase("05CR")){ 
                        	
                        	cashpecpay.setSubTsfTypeCode("03CR");
                        }else if(rs.getString("FTsfTypeCode").equalsIgnoreCase("05") && rs.getString("FSubTsfTypeCode").equalsIgnoreCase("05CB")){ 
                        	
                        	cashpecpay.setSubTsfTypeCode("03CB");
                        }
                        //---end---
                        else {
                            cashpecpay.setSubTsfTypeCode("03OT"); //支付其他款项
                        }
                    }
                } else { //流入都按照当日汇率
                    //dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    //      rs.getString("FCuryCode"),
                    //      rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                    //dPortRate = this.getSettingOper().getCuryRate(dDate,
                    //      port.getCurrencyCode(),
                    //      rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);
                    if (YssFun.dateDiff(rs.getDate("FTransferDate"), dDate) ==
                        0) { //当调拨日期是当前日期时，说明是实收款项
                        cashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(rs.getDouble(
                                "FMoney"), dBaseRate));
                        cashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(rs.getDouble(
                                "FMoney"), dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCuryCode"), dDate,
                                         rs.getString("FPortCode")));
                        cashpecpay.setTsfTypeCode("02"); //实收未清算款
                        if (rs.getString("FTsfTypeCode").equalsIgnoreCase("01")) { //调拨类型为“内部调拨”
                            cashpecpay.setSubTsfTypeCode("02CE"); //实收换汇拆解款项
                        } else if (rs.getString("FTsfTypeCode").equalsIgnoreCase("04")) { //调拨类型为“资本”
                            cashpecpay.setSubTsfTypeCode("02TA"); //实收申购款
                        }
                        //add by huangqirong 2012-05-07 story #2565
                        else if(rs.getString("FTsfTypeCode").equalsIgnoreCase("05") && rs.getString("FSubTsfTypeCode").equalsIgnoreCase("05CB")){ 
                        	
                        	cashpecpay.setSubTsfTypeCode("02CB");
                        }
                        //---end---
                        //---add by songjie 2012.06.26 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A start---//
                        else if(rs.getString("FTsfTypeCode").equalsIgnoreCase("05") && rs.getString("FSubTsfTypeCode").equalsIgnoreCase("05CR")){ 
                        	
                        	cashpecpay.setSubTsfTypeCode("02CM");//实收现金替代款-ETF赎回
                        }
                        //---add by songjie 2012.06.26 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A end---//
                        else {
                            cashpecpay.setSubTsfTypeCode("02OT"); //实收其他款项
                        }
                    } else if (YssFun.dateDiff(rs.getDate("FTransDate"),
                                               rs.getDate("FTransferDate")) > 0) { //当调拨日期比业务日期大,说明是应收款项
                        cashpecpay.setBaseCuryMoney(this.getSettingOper().
                            calBaseMoney(rs.getDouble(
                                "FMoney"), dBaseRate));
                        cashpecpay.setPortCuryMoney(this.getSettingOper().
                            calPortMoney(rs.getDouble(
                                "FMoney"), dBaseRate, dPortRate,
                                         //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                         rs.getString("FCuryCode"), dDate,
                                         rs.getString("FPortCode")));
                        cashpecpay.setTsfTypeCode("06"); //应收款
                        if (rs.getString("FTsfTypeCode").equalsIgnoreCase("01")) { //调拨类型为“内部调拨”
                            cashpecpay.setSubTsfTypeCode("06CE"); //应收换汇拆借款项
                        } else if (rs.getString("FTsfTypeCode").equalsIgnoreCase("04")) { //调拨类型为“内部调拨”
                            cashpecpay.setSubTsfTypeCode("06TA"); //应收申购款
                        }
                        //add by huangqirong 2012-05-07 story #2565
                        else if(rs.getString("FTsfTypeCode").equalsIgnoreCase("05") && rs.getString("FSubTsfTypeCode").equalsIgnoreCase("05CB")){ 
                        	
                        	cashpecpay.setSubTsfTypeCode("06CB");
                        }
                        //---end---
                        //---add by songjie 2012.06.26 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A start---//
                        else if(rs.getString("FTsfTypeCode").equalsIgnoreCase("05") && rs.getString("FSubTsfTypeCode").equalsIgnoreCase("05CR")){ 
                        	cashpecpay.setSubTsfTypeCode("06CM");//应收现金替代款-ETF赎回
                        }
                        //---add by songjie 2012.06.26 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A end---//
                        else {
                            cashpecpay.setSubTsfTypeCode("06OT"); //应收其他款项
                        }
                    }
                }
                cashpecpay.setBaseCuryRate(dBaseRate);
                cashpecpay.setPortCuryRate(dPortRate);

                cashpecpay.checkStateId = 1;
                prAdmin.addList(cashpecpay);
            }
            //2009.04.27 蒋锦 添加 事务控制
            //MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            bTrans = true;
            conn.setAutoCommit(false);
			//20091019- MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
            //edit by songjie 2012.06.26 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A 添加06CM,02CM 作为删除条件
            prAdmin.insert(dDate, "06,07,02,03",
            		       //edit by songjie 2012.10.26 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 添加 6TAZR,07TAZC,02TAZR,03TAZC TA转入转出  + //多币种应付赎回费、款分为多项  添加07TA_Fee  20130312  yeshenghong
                           //--- edit by songjie 2013.06.21 STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001 start---//       
            		       "06CE,07CE,06OT,06TA,07OT,07TA,02CE,02TA,03CE,02OT,03OT,03TA,07CR,07CB,03CR,03CB,06CB,02CB," +
                           //如果没有生成03TA_Fee的数据，则 插入现金应收应付数据之前，不删除03TA_Fee的数据
                           "06CM,02CM,06TAZR,07TAZC,02TAZR,03TAZC,07TA_Fee" + (delete03TA_Fee ? ",03TA_Fee" : ""), //还原之前的代码处理  by leeyu 20090708 MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 此业务有重新处理 //add by huangqirong 2012-05-07 story #2565 ,07CR,07CB,03CR,03CB,06CB,02CB 添加ETF联接基金产生的应收应付
                           //--- edit by songjie 2013.06.21 STORY 4081 需求上海-[国泰基金]QDIIV4.0[紧急]20130617001 end---//       
                           this.statCodes, //add by huangqirong 2013-04-15 bug #7545 选中的现金账户参数
                           portCodes,//03TA_Fee add by yeshenghong 20130409 
                           0, false);
            //控制事物 add by jiangjin MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("系统进行现金库存统计,在统计除证券清算款和分红数据之外的其他应收应付款时出现异常!\n", e); //by 曹丞 2009.01.22 统计除证券清算款和分红数据之外的其他应收应付款时出现异常 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(bTrans);
        }
    }

    /**
     * 在统计之前向现金应收应付表中插入一些数据 sj add 20080217
     * @param dDate Date
     * @throws YssException
     */
    protected void beforeStatStorage(java.util.Date dDate) throws YssException {
        try {
            setRecFee(dDate);
            //此方法不起作用，注释掉 2008.10.06 蒋锦
            //rePairTransCash(dDate);
        } catch (Exception e) {
            throw new YssException("系统进行现金应收应付库存统计,在统计现金应收应付库存的前期处理时出现异常!\n", e); // by 曹丞 2009.01.22 统计现金应收应付库存的前期处理异常 MS00004 QDV4.1-2009.2.1_09A
        }
    }

    /**
     * 插入挂起的应收应付数据
     * @param dDate Date
     * @throws YssException
     */
    public void setRecFee(java.util.Date dDate) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        CashPayRecAdmin cashPayAdmin = new CashPayRecAdmin();
        //add by songjie 201.01.16 需求 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A 用于判断是否生成国内业务对应的费用数据
        boolean haveDomesticInfo = false;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Data_SubTrade") +
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
				//edit by songjie 201.01.16 需求 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A 添加 FExchangeCode
                " left join (select FSecurityCode as FSecurityCode_j,FCatCode,FSubCatCode,FExchangeCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") j on a.FSecurityCode = j.FSecurityCode_j" +
                //---xuqiji 20100714 MS01426    现金账户设置中设置启用日期和银行账号不一致    QDV4赢时胜(测试)2010年07月8日02_B --//
                " left join (select b.FCashAccCode as FCashAccCode_c,b.FCuryCode from " +
                " (select max(FStartDate) as FStartDate, FCashAccCode from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(dDate) +
                " group by FCashAccCode order by FCashAccCode) a " +
                " join (select * from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(dDate) +
                " ) b on a.fcashacccode = b.fcashacccode and a.FStartDate = b.FStartDate " +
                " ) CashAcc on a.FCashAccCode = CashAcc.FCashAccCode_c " +
                //----------------------------end------------------------//
                " where (FBargainDate " +
                " between " + dbl.sqlDate(dDate) + " and " +
                dbl.sqlDate(dDate) +
                " ) and FCheckState = 1 and FPortCode in (" + portCodes +
                ") order by FNum";

            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //------------------by 曹丞 2009.01.22 增加币种有效性检查 MS00004 QDV4.1-2009.2.1_09A------//
                if (rs.getString("FCuryCode") == null || rs.getString("FCuryCode").trim().length() == 0) {
                    throw new YssException("系统进行现金库存统计,在保存挂起的应收应付数据时检查到代码为【" +
                                           rs.getString("FCashAccCode") + "】的现金账户对应的货币信息不存在!" + "\n" +
                                           "请核查以下信息：" + "\n" +
                                           "1.【现金账户设置】中该账户信息是否存在且已审核!" + "\n" +
                                           "2.【现金账户设置】中该现金账户代码设置是否正确!" + "\n" +
                                           "3.若以上两种方法无效,请检查现金账户币种设置!");
                }
                //------------------------------------------------------------------------------------------//

                for (int i = 1; i <= 8; i++) {
                    //当承担者为 --挂入应收应付时，产生一笔应收应付记录 sj add 20080217
                    if (rs.getDouble("FAM" + i) == 2) {
                    	//edit by songjie 2012.01.16 STORY 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A 判断是否包含国内业务数据
                    	boolean haveDmt = setCashPecPay(rs.getDate("FBargainDate"),
                                      rs.getString("FPortCode"),
                                      rs.getString("FCashAccCode"),
                                      rs.getString("FInvMgrCode"),
                                      rs.getString("FBrokerCode"), "",
                                      rs.getString("FCuryCode"),
                                      rs.getDouble("FTradeFee" + i),
                                      rs.getDouble("FBaseCuryRate"),
                                      rs.getDouble("FPortCuryRate"),
                                      cashPayAdmin,
                                      //linjunyun 2008-11-25 bug:MS00011 增加汇率日期用于计算组合货币成本
                                      (rs.getDate("FRateDate") == null || (YssFun.formatDate(rs.getDate("FRateDate"), "yyyy-MM-dd")).equalsIgnoreCase("9998-12-31") ?
                                      //edit by songjie 2012.01.16 STORY 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A 
                                      //添加参数 "subtrade",rs.getString("FNum"),rs.getString("FExchangeCode")
                                      rs.getDate("FBargainDate") : rs.getDate("FRateDate")),"subtrade",rs.getString("FNum"),rs.getString("FExchangeCode")); //如果汇率日期为null或为9998-12-31，则使用业务日期的值。sj modified 20081210
                    	//---add by songjie 2012.01.16 STORY 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A 判断是否包含国内业务数据  start---//
                    	if(haveDmt){
                    		haveDomesticInfo = haveDmt;
                    	}
                    	//---add by songjie 2012.01.16 STORY 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A 判断是否包含国内业务数据  end---//
                    }
                }
            }
            //---add by songjie 2012.01.16 STORY 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A 判断是否包含国内业务数据  start---//
            if(haveDomesticInfo){//若包含国内业务生成的费用数据，则先根据关联类型删除数据
            	CashPayRecAdmin cashrecPay = new CashPayRecAdmin();
            	cashrecPay.setYssPub(pub);
            	cashrecPay.delete("", dDate, dDate, YssOperCons.YSS_ZJDBLX_Pay,
						YssOperCons.YSS_ZJDBLX_Pay + "FE", "", "",
						portCodes, "", "","", 0, 0, "","subtrade", "");
            }
            //---add by songjie 2012.01.16 STORY 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A 判断是否包含国内业务数据  end---//
            cashPayAdmin.setYssPub(pub);
            cashPayAdmin.insert(dDate, YssOperCons.YSS_ZJDBLX_Pay,
                                YssOperCons.YSS_ZJDBLX_Pay + "FE",
                                portCodes, 0, false);
            cashPayAdmin.getList().clear();//本次插入数据完后清空ArrayList，防止在下面的执行中重复插入数据 by leeyu 20100327
            //以下处理ETF基金普通估值时，要产生一笔 预提交易收入的应收应付数据 xuqiji 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
            if(!this.isBETFStat()){//如果不是ETF基金估值时
            	dbl.closeResultSetFinal(rs);
            	strSql = "select * from "
						+ pub.yssGetTableName("tb_ta_trade")
						+ " a left join (select FFeeCode,FAccountingWay as FAW1,FAssumeMan as FAM1 from "
						+ pub.yssGetTableName("Tb_Para_Fee")
						+ ") b on a.FFeeCode1 = b.FFeeCode"+
						// --------------------------------------------------------
						" left join (select FFeeCode,FAccountingWay as FAW2,FAssumeMan as FAM2 from "
						+ pub.yssGetTableName("Tb_Para_Fee")
						+ ") c on a.FFeeCode2 = c.FFeeCode"+
						// --------------------------------------------------------
						" left join (select FFeeCode,FAccountingWay as FAW3,FAssumeMan as FAM3 from "
						+ pub.yssGetTableName("Tb_Para_Fee")
						+ ") d on a.FFeeCode3 = d.FFeeCode"+
						// --------------------------------------------------------
						" left join (select FFeeCode,FAccountingWay as FAW4,FAssumeMan as FAM4 from "
						+ pub.yssGetTableName("Tb_Para_Fee")
						+ ") e on a.FFeeCode4 = e.FFeeCode"+
						// --------------------------------------------------------
						" left join (select FFeeCode,FAccountingWay as FAW5,FAssumeMan as FAM5 from "
						+ pub.yssGetTableName("Tb_Para_Fee")
						+ ") f on a.FFeeCode5 = f.FFeeCode"+
						// --------------------------------------------------------
						" left join (select FFeeCode,FAccountingWay as FAW6,FAssumeMan as FAM6 from "
						+ pub.yssGetTableName("Tb_Para_Fee")
						+ ") g on a.FFeeCode6 = g.FFeeCode"+
						// --------------------------------------------------------
						" left join (select FFeeCode,FAccountingWay as FAW7,FAssumeMan as FAM7 from "
						+ pub.yssGetTableName("Tb_Para_Fee")
						+ ") h on a.FFeeCode7 = h.FFeeCode"+
						// --------------------------------------------------------
						" left join (select FFeeCode,FAccountingWay as FAW8,FAssumeMan as FAM8 from "
						+ pub.yssGetTableName("Tb_Para_Fee")
						+ ") i on a.FFeeCode8 = i.FFeeCode"
						+ " join(select * from " + pub.yssGetTableName("tb_para_portfolio")
						+ " where FCheckState =1 and FSubAssetType ='0106' ) op on op.FPortCode = a.FPortCode" 
						// --------------------------------------------------------
						+ " where (a.FTradeDate " + " between "
						+ dbl.sqlDate(dDate) + " and " + dbl.sqlDate(dDate)
						+ " ) and a.FCheckState = 1 and a.FPortCode in ("
						+ portCodes + ") and a.FSellType in('01','02') order by a.FNum";
            	rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
	            while (rs.next()) {
	                if (rs.getString("FCuryCode") == null || rs.getString("FCuryCode").trim().length() == 0) {
	                    throw new YssException("系统进行现金库存统计,在保存挂起的应收应付数据时检查到代码为【" +
	                                           rs.getString("FCashAccCode") + "】的现金账户对应的货币信息不存在!" + "\n" +
	                                           "请核查以下信息：" + "\n" +
	                                           "1.【现金账户设置】中该账户信息是否存在且已审核!" + "\n" +
	                                           "2.【现金账户设置】中该现金账户代码设置是否正确!" + "\n" +
	                                           "3.若以上两种方法无效,请检查现金账户币种设置!");
	                }
	                //------------------------------------------------------------------------------------------//
	
	                for (int i = 1; i <= 8; i++) {
	                    //当承担者为 --挂入应收应付时，产生一笔应收应付记录 sj add 20080217
	                    if (rs.getDouble("FAM" + i) == 2) {
	                    	setTACashPecPay(rs.getDate("FTradeDate"),
	                                      rs.getString("FPortCode"),
	                                      rs.getString("FCashAccCode"),
	                                      rs.getString("FAnalysisCode1"),
	                                      rs.getString("FAnalysisCode2"), "",
	                                      rs.getString("FCuryCode"),
	                                      rs.getDouble("FTradeFee" + i),
	                                      rs.getDouble("FBaseCuryRate"),
	                                      rs.getDouble("FPortCuryRate"),
	                                      cashPayAdmin,
	                                      rs.getDate("FTradeDate"),
	                                      rs.getString("FSellType").equals("01")?YssOperCons.YSS_ETF_CashTradeCost_SG:YssOperCons.YSS_ETF_CashTradeCost_SH); 
	                    }
	                }
	            }
	            cashPayAdmin.setYssPub(pub);
	            cashPayAdmin.insert("",dDate,dDate, YssOperCons.YSS_ZJDBLX_Pay,
	                                YssOperCons.YSS_ETF_CashTradeCost_SG+","+YssOperCons.YSS_ETF_CashTradeCost_SH,"","",
	                                portCodes,"","","", 0,true, false,false,1,"","");
            }
            //--------------------end 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A-----------------------------------//
        } catch (Exception e) {
            throw new YssException("系统进行现金库存统计,在保存挂起的应收应付数据时出现异常!\n", e); // by 曹丞 2009.01.22 保存挂起的应收应付数据异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * ETF基金为应收应付数据赋值 xuqiji 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @param dTransDate 业务日期
     * @param sPortCode 组合代码
     * @param sCashAccCode 现金账户
     * @param sAnalysisCode1 分析代码1
     * @param sAnalysisCode2 分析代码2
     * @param sAnalysisCode3 分析代码3
     * @param sTradeCury 交易币种
     * @param dFee 金额
     * @param dBaseRate 基础汇率
     * @param dPortRate 组合汇率
     * @param cashPayAdmin 现金账户应收应付操作类
     * @param dRateDate 汇率日期
     * @throws YssException
     */
    private void setTACashPecPay(java.util.Date dTransDate, String sPortCode,String sCashAccCode, 
    		String sAnalysisCode1, String sAnalysisCode2,String sAnalysisCode3, String sTradeCury, double dFee,
			double dBaseRate, double dPortRate, CashPayRecAdmin cashPayAdmin,java.util.Date dRateDate,String sSubTsfTypeCode) throws YssException {
		if (dFee == 0) {
			return;
		}
		CashPecPayBean cashpecpay = new CashPecPayBean();
		cashpecpay.setTradeDate(dTransDate);
		cashpecpay.setPortCode(sPortCode);
		cashpecpay.setInvestManagerCode(sAnalysisCode1);
		cashpecpay.setBrokerCode(sAnalysisCode2);
		cashpecpay.setCashAccCode(sCashAccCode);
		cashpecpay.setCuryCode(sTradeCury);
		cashpecpay.setMoney(dFee);
		cashpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(dFee,
				dBaseRate));
		cashpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(dFee,
				dBaseRate, dPortRate,
				sTradeCury, dRateDate, sPortCode));
		cashpecpay.setBaseCuryRate(dBaseRate);
		cashpecpay.setPortCuryRate(dPortRate);
		cashpecpay.checkStateId = 1;
		cashpecpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); // 应付款项
		cashpecpay.setSubTsfTypeCode(sSubTsfTypeCode);//调拨子类型
		cashPayAdmin.addList(cashpecpay);
	}
    
    //edit by songjie 2012.01.13 需求 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A 添加参数 relaType, relaTypeNum, exchangeCode
    private boolean setCashPecPay(java.util.Date dTransDate,
                               String sPortCode, String sCashAccCode,
                               String sAnalysisCode1,
                               String sAnalysisCode2, String sAnalysisCode3,
                               String sTradeCury,
                               double dFee,
                               double dBaseRate, double dPortRate,
                               CashPayRecAdmin cashPayAdmin,
                               java.util.Date dRateDate,
                               String relaType,
                               String relaTypeNum,
                               String exchangeCode) throws
        YssException {
		//edit by songjie 2012.01.13 需求 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A
    	boolean haveDomesticInfo = false;
        if (dFee == 0) {
		    //edit by songjie 2012.01.13 需求 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A
            return haveDomesticInfo;
        }
        CashPecPayBean cashpecpay = new CashPecPayBean();
        cashpecpay.setTradeDate(dTransDate);
        cashpecpay.setPortCode(sPortCode);
        cashpecpay.setInvestManagerCode(sAnalysisCode1);
        cashpecpay.setBrokerCode(sAnalysisCode2);
        cashpecpay.setCashAccCode(sCashAccCode);
        cashpecpay.setCuryCode(sTradeCury);
        cashpecpay.setMoney(dFee);
        cashpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
            dFee, dBaseRate));
        cashpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
            dFee, dBaseRate, dPortRate,
            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
            sTradeCury, dRateDate, sPortCode));
        cashpecpay.setBaseCuryRate(dBaseRate);
        cashpecpay.setPortCuryRate(dPortRate);
        cashpecpay.checkStateId = 1;
        cashpecpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); //应付款项
        cashpecpay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay + "FE");
        //---add by songjie 2012.01.13 需求 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A start---//
        if("CG".equals(exchangeCode) || "CS".equals(exchangeCode)){
        	haveDomesticInfo = true;
        	cashpecpay.setRelaNumType(relaType);
        	cashpecpay.setRelaNum(relaTypeNum);
        }
        //---add by songjie 2012.01.13 需求 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A end---//
        cashPayAdmin.addList(cashpecpay);
		//add by songjie 2012.01.13 需求 2124 QDV4赢时胜(上海开发部)2012年01月12日01_A
        return haveDomesticInfo;
    }
    /**
     * 补充 资金调拨主表中数据 当调拨子表中有而调拨主表中没有的数据
     * @throws YssException
     */
    //此方法不起作用，注释掉 2008.10.06 蒋锦 修改
    /*private void rePairTransCash(java.util.Date dDate) throws YssException{
       Connection conn =null;
       String strSql="";
       boolean bTrans = false;
       try{
          conn =dbl.loadConnection();
          conn.setAutoCommit(bTrans);
          bTrans = true;
          strSql="insert into "+pub.yssGetTableName("tb_cash_transfer")+" (FNUM,FTSFTYPECODE,FTRANSFERDATE,FTRANSFERTIME,FTRANSDATE, "+
     " FDATASOURCE,FCHECKSTATE,FCREATOR,FCREATETIME,fcheckuser,fchecktime) "+
                " select a.FNum,"+dbl.sqlString(YssOperCons.YSS_ZJDBLX_AUTO)+","+dbl.sqlDateS(dbl.sqlSubStr("a.FNum","2","8"))+",'00:00:00',"+dbl.sqlDateS(dbl.sqlSubStr("a.FNum","2","8"))+", "+
                " 1,a.FCheckState,'admin',"+dbl.sqlSubStr("a.FNum","2","8")+dbl.sqlJN()+"' 00:00:00','admin',"+dbl.sqlSubStr("a.FNum","2","8")+dbl.sqlJN()+"' 00:00:00' from( "+
                " select distinct(su.FNum) as FNum,su.FCheckState "+
                " from "+pub.yssGetTableName("tb_cash_subtransfer")+" su "+
                " where not exists "+
                " (select 1 from "+pub.yssGetTableName("tb_cash_transfer")+" ca where ca.FNum = su.FNum) "+
                "  and "+dbl.sqlDateS(dbl.sqlSubStr("su.FNum","2","8"))+"="+dbl.sqlDate(dDate)+") a "+
                " where not exists (select 1 from "+pub.yssGetTableName("tb_cash_transfer")+" trans where a.FNum=trans.FNum) ";
          //dbl.executeSql(strSql);
          conn.commit();
          conn.setAutoCommit(bTrans);
          bTrans = false;
       }catch(Exception ex){
          try{
             bTrans = true;
             conn.rollback();
          }catch(Exception exs){
             throw new YssException(ex.toString());
          }finally{
             dbl.endTransFinal(conn,bTrans);
          }
       }
        }*/
}
