package com.yss.main.operdeal.stgstat;

import java.util.*;
import java.sql.*;
import com.yss.main.storagemanage.InvestBean;
import com.yss.util.*;
import com.yss.main.parasetting.InvestPayBean;

public class StgInvest
    extends BaseStgStatDeal {
    public StgInvest() {
    }
    
    private boolean sTransition = false;		//added by liubo.Story #2139.判断是否为转换日期大于等于统计日期的预提项目

    public ArrayList getStorageStatData(java.util.Date dOperDate) throws
        YssException {
        String strSql = "", strTmpSql = "", strTmpSql1 = "";
//      java.util.Date dOperDate = null;
        String strError = "统计运营支付库存出错";
        ResultSet rs = null;
        InvestBean investstorage = null;
        ArrayList all = new ArrayList();
        
        boolean analy1; //判断是否需要用分析代码；杨
        boolean analy2;
        boolean analy3;

        String sKey = "";
        Hashtable hmEveStg = new Hashtable();
        Iterator iter = null;

        try {
            if (bYearChange) {
                yearChange(dOperDate, portCodes);
            }

            analy1 = operSql.storageAnalysis("FAnalysisCode1",
                                             YssOperCons.YSS_KCLX_InvestPayRec);
            analy2 = operSql.storageAnalysis("FAnalysisCode2",
                                             YssOperCons.YSS_KCLX_InvestPayRec);
            analy3 = operSql.storageAnalysis("FAnalysisCode3",
                                             YssOperCons.YSS_KCLX_InvestPayRec);

            if (portCodes.length() > 0) {
                strTmpSql = " and FPortCode in (" + portCodes + ")";
            } else {
            	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
          
            	strTmpSql = " and FPortCode in ( select FPortCode from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 and Fassetgroupcode='" +
                pub.getAssetGroupCode() +
                "') ";
                
                //end by lidaolong
            }
//         for (int j = 0; j <= YssFun.dateDiff(dStartDate, dEndDate); j++) {
//            dOperDate = YssFun.addDay(dStartDate, j);
            if (YssFun.getMonth(dOperDate) == 1 && //代表是期初
                YssFun.getDay(dOperDate) == 1) {
                strTmpSql1 = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dOperDate, "yyyy") + "00'";
            } else {
                strTmpSql1 = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dOperDate, "yyyy") + "00'" +
                    " and FStorageDate = " +
                    dbl.sqlDate(YssFun.addDay(dOperDate, -1));
            }
            //如果支付的汇率跟库存的汇率不一致时，会统计出两条记录，所以不能用汇率进行group by fazmm20071107
            strSql = "select  xx.*, yy.*," +
                dbl.sqlDate(dOperDate) +
                " as FOperDate " +
                " ,f.FIVType as FIVTYPE " +
                " from (Select b.FIVPayCatCode as FIVPayCatCode, b.FTsfTypeCode as FTsfTypeCode, b.FPortCode as FIVPayPortCode,b.FTransDate,b.FCuryCode as  FIVPayPortCury,b.FPayType as FPayType," +
                (analy1 ? (dbl.sqlIsNull(" b.FAnalysisCode1", "''") +
                           " as FAnalysisCode1 ,") : " ") +
                (analy2 ? (dbl.sqlIsNull(" b.FAnalysisCode2", "''") +
                           " as FAnalysisCode2 ,") : " ") +
                (analy3 ? (dbl.sqlIsNull(" b.FAnalysisCode3", "''") +
                           " as FAnalysisCode3 ,") : " ") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                "nvl(b.fattrclscode,' ')as fattrclscode,"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " Sum(" +
                dbl.sqlIsNull("b.FMoney", "0") +
                ") as FIVPayMoney, sum(" +
                dbl.sqlIsNull("b.FBaseCuryMoney", "0") +
                ") as FIVPayBaseCuryMoney, sum(" +
                dbl.sqlIsNull("b.FPortCuryMoney", "0") +
                ") as FIVPayPortCuryMoney from (" +
                "select b1.*,b2.FPayType from (" + //运营费用统计库存问题，运营费用支付时统计有问题fazmm20070918
                "select FIVPayCatCode,(case FTsfTypeCode when '02' then '06'" +
                "when '16' then '06'" +//MS00017  国内预提待摊
                "when '03' then '07'" +
                " when '97' then '07' " + //add by huangqirong 2013-02-02 stroy #3488 补差款 97转为 07才能统计完整
                "else FTsfTypeCode end) as FTsfTypeCode, " +
                "FPortCode, FTransDate, FCuryCode, " +
//---------------------按照配置获取分析代码 sj edit 20080805 bug 0000362 -------------------
                (analy1 ? "FAnalysisCode1," : " ") +
                (analy2 ? "FAnalysisCode2," : " ") +
                (analy3 ? "FAnalysisCode3," : " ") +
//---------------------------------------------------------------------------------------
                //" FAnalysisCode1," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " fattrclscode,"+
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                "(case FTsfTypeCode when '02' then -FMoney " +
                "when '16' then -FMoney " +//冲减待摊运营收支款
                "when '03' then -FMoney " +
                "else FMoney end) as FMoney, " +
                "(case FTsfTypeCode when '02' then -FBaseCuryMoney " +
                "when '16' then -FBaseCuryMoney " +//冲减待摊运营收支款
                "when '03' then -FBaseCuryMoney " +
                "else FBaseCuryMoney end) as FBaseCuryMoney," +
                "(case FTsfTypeCode when '02' then -FPortCuryMoney " +
                "when '16' then -FPortCuryMoney " +//冲减待摊运营收支款
                "when '03' then -FPortCuryMoney " +
                "else FPortCuryMoney end) as FPortCuryMoney," +
                "FBaseCuryRate,FPortCuryRate" +
                " from " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " where FTsfTypeCode in (" +
                //运营费用库存主表统计fazmm20070913
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_PAYOUT) + "," +//预提待摊-支出
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + 
                "," + dbl.sqlString(YssOperCons.Yss_ZJDBLX_SUPPLEMENT) + //add by huangqirong 2013-02-02 stroy #3488 补差款
                ") " +
                " and fcheckstate = 1 and ftransdate = " +
                dbl.sqlDate(dOperDate) + strTmpSql +
                ") b1 left join (select FPayType,FIVPayCatCode from Tb_Base_InvestPayCat where FCheckState=1) b2 on b1.FIVPayCatCode=b2.FIVPayCatCode ) b " +
                " group by b.FIVPayCatCode, b.FTsfTypeCode, b.FPortCode," +
                (analy1 ? "b.FAnalysisCode1," : " ") +
                (analy2 ? "b.FAnalysisCode2," : " ") +
                (analy3 ? "b.FAnalysisCode3," : " ") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                "b.fattrclscode,"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                "b.FTransDate,b.FCuryCode,b.FPayType) yy ";

            strSql = strSql +
                " full join ( select a.FYearMonth, a.FIVPayCatCode as FIVPayCatCodeStorage," +
                " a.FStorageDate, a.FPortCode as FPortCodeStorage, a.FCuryCode as FCuryCodeStorage,  " +
                dbl.sqlIsNull("a.FBal", "0") +
                " as FMoney , " +
                dbl.sqlIsNull("a.FBaseCuryBal", "0") +
                " as FBaseCuryMoney , " +
                dbl.sqlIsNull("a.FPortCuryBal", "0") +
                " as FPortCuryMoney " +
                //"a.FPortCuryRate as FPortCuryRateStorage, a.FBaseCuryRate as FBaseCuryRateStorage " +
                (analy1 ? ("," + dbl.sqlIsNull("a.FAnalysisCode1", "' '") +
                           " as FAnalysisCode1Storage") : " ") +
                (analy2 ? ("," + dbl.sqlIsNull("a.FAnalysisCode2", "' '") +
                           " as FAnalysisCode2Storage ") : " ") + //错误的sql语句，多了个逗号。sj edit 20080805 bug 0000362
                (analy3 ? ("," + dbl.sqlIsNull("a.FAnalysisCode3", "' '") +
                           " as FAnalysisCode3Storage ") : " ") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                           ",nvl(a.fattrclscode,' ') as fattrclscodestorage "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " from " +
                pub.yssGetTableName("Tb_Stock_Invest") +
                " a where FCheckState = 1 " + strTmpSql1 +
                //------------------ 屏蔽掉所有的金额为零的记录 sj 20080401  --------------------
                " and (a.FBal <> 0  or a.FBaseCuryBal <> 0 or a.FPortCuryBal <> 0) " +
                //---------------------------------------------------------------------------
                ") xx on xx.FIVPayCatCodeStorage=yy.FIVPayCatCode and xx.FPortCodeStorage=yy.FIVPayPortCode" +
                (analy1 ? " and xx.FAnalysisCode1Storage = yy.FAnalysisCode1 " :
                 " ") +
                (analy2 ? " and xx.FAnalysisCode2Storage = yy.FAnalysisCode2 " :
                 " ") +
                (analy3 ? " and xx.FAnalysisCode3Storage = yy.FAnalysisCode3 " :
                 " ") +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                 " and xx.fattrclscodestorage = yy.fattrclscode "+
              //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
              // add by fangjiang 2011.02.14 #2279
                 " and xx.FCuryCodeStorage = yy.FIVPayPortCury " +
              //---------------------------
                 
                //--------------获取运营的类型。sj edit 20080731 -------------------------
                " left join (select FIVPayCatCode,FPayType,FIVType from Tb_Base_InvestPayCat where FCheckState = 1) f on (yy.FIVPayCatCode = f.FIVPayCatCode or xx.FIVPayCatCodeStorage = f.Fivpaycatcode)";
            //---------------------------------------------------------------------
            //-------------屏蔽掉所有的金额为零的记录 sj 20080317 -------------------------//
            // " where xx.FMoney <> 0 or xx.FBaseCuryMoney <> 0 or xx.FPortCuryMoney <> 0";
            //---------------------------------------------------------------------------
            //" and " +
            //dbl.sqlDateAdd("xx.FStorageDate", "+1") +
            //" = yy.FTransDate ";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //investstorage = new InvestBean();
//            sKey = (rs.getString("FIVPayCatCode") == null ?
//                    rs.getString("FIVPayCatCodeStorage") :
//                    rs.getString("FIVPayCatCode")) + "\f" +
//                  (rs.getString("FIVPayPortCode") == null ?
//                   rs.getString("FPortCodeStorage") :
//                   rs.getString("FIVPayPortCode")) +
//                  (analy1 ?
//                   "\f" +
//                   (rs.getString("FAnalysisCode1") == null ?
//                    rs.getString("FAnalysisCode1Storage") :
//                    rs.getString("FAnalysisCode1")) : "");
                sKey = (rs.getString("FIVPayCatCode") == null ?
                        rs.getString("FIVPayCatCodeStorage") :
                        rs.getString("FIVPayCatCode")) + "\f" +
                    (rs.getString("FIVPayPortCode") == null ?
                     rs.getString("FPortCodeStorage") :
                     rs.getString("FIVPayPortCode")) +
                    (analy1 ?
                     "\f" +
                     (rs.getString("FAnalysisCode1") == null ?
                      rs.getString("FAnalysisCode1Storage") :
                      rs.getString("FAnalysisCode1")) : "") +
                    //--------添加了分析代码2的筛选条件 sj modified 20081218 MS00108---------
                    (analy2 ?
                     "\f" +
                     (rs.getString("FAnalysisCode2") == null ?
                      rs.getString("FAnalysisCode2Storage") :
                      rs.getString("FAnalysisCode2")) : "") +
                    //-------------------------------------------------------------
                    //--------MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified---------//
                    (analy3 ?
                     "\f" +
                     (rs.getString("FAnalysisCode3") == null ?
                      rs.getString("FAnalysisCode3Storage") :
                      rs.getString("FAnalysisCode3")) : "")+
                      //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                      "\f"+(rs.getString("fattrclscode") == null ?rs.getString("fattrclscodestorage"):rs.getString("fattrclscode"))
                     //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                     // add by fangjiang 2011.02.14 #2279
                     + "\f" + (rs.getString("FCuryCodeStorage") == null ? rs.getString("FIVPayPortCury") : rs.getString("FCuryCodeStorage"));
                	//---------------------------
                     
                //-------------------------------------------------------------------------------//


                if (hmEveStg.get(sKey) != null) {
                    investstorage = (InvestBean) hmEveStg.get(sKey);
                    if (((rs.getString("FTsfTypeCode")!=null&&rs.getString("FTsfTypeCode").equalsIgnoreCase("07")) || rs.getString("FTsfTypeCode").equalsIgnoreCase("06") ||
                    		(rs.getString("FTsfTypeCode")!=null&&rs.getString("FTsfTypeCode").equalsIgnoreCase("16"))) &&//国内业务中待摊收益计提产生的业务类型由07变更为16，此处保留07兼容历史数据
                    		//20120221 modified by liubo.Story #2139
                    		//在转换日期大于等于统计日期的预提转待摊的预提费用和待摊一样处理
                    		//======================================
//                        rs.getString("FIVTYPE").equalsIgnoreCase("DEFERREDFEE") &&//待摊对应的业务类型和词汇代码更改 panjunfang modify 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                    	IFTranFTransitionAvailable(rs.getString("FPortCodeStorage"),rs.getString("FIVPayCatCodeStorage"),rs.getDate("FOperDate"),rs.getString("FIVTYPE")) //&& rs.getInt("FPAYTYPE") == 0
                    	//====================end==================
                    	) { //如果是计提的预提待摊值,则乘以-1.sj edit 20080731
                    	
                    	if (!sTransition)
                    	{
	                        investstorage.setStrAccBalance(
	                            YssD.add(YssD.mul(YssD.add(rs.getDouble("FMoney"),
	                            rs.getDouble("FIVPayMoney")), -1),
	                                     YssFun.toDouble(investstorage.getStrAccBalance())) +
	                            "");
	
	                        investstorage.setStrBaseCuryBal(YssD.add(YssD.mul(YssD.add(rs.
	                            getDouble(
	                                "FBaseCuryMoney"),
	                            rs.getDouble("FIVPayBaseCuryMoney")), -1),
	                            YssFun.toDouble(investstorage.getStrBaseCuryBal())) +
	                            "");
	
	                        investstorage.setStrPortCuryBal(YssD.add(YssD.mul(YssD.add(rs.
	                            getDouble(
	                                "FPortCuryMoney"),
	                            rs.getDouble("FIVPayPortCuryMoney")), -1),
	                            YssFun.toDouble(investstorage.getStrPortCuryBal())) +
	                            "");
                    	}
                    	else
                    	{
                    		investstorage.setStrAccBalance(
    	                            YssD.add(YssD.mul(YssD.add(0,rs.getDouble("FIVPayMoney")
    	                            ), 1),
    	                                     YssFun.toDouble(investstorage.getStrAccBalance())) +
    	                            "");
    	
    	                    investstorage.setStrBaseCuryBal(YssD.add(YssD.mul(YssD.add(0,rs.getDouble("FIVPayBaseCuryMoney"))
    	                    		, 1),
    	                            YssFun.toDouble(investstorage.getStrBaseCuryBal())) +
    	                            "");
    	
    	                    investstorage.setStrPortCuryBal(YssD.add(YssD.mul(YssD.add(0,rs.getDouble("FIVPayPortCuryMoney")
    	                            ), 1),
    	                            YssFun.toDouble(investstorage.getStrPortCuryBal())) +
    	                            "");
                    	}
                    } else if (rs.getString("FTsfTypeCode")!=null&&rs.getString("FTsfTypeCode").equalsIgnoreCase("06") &&
                               rs.getString("FIVTYPE").equalsIgnoreCase("DEFERREDFEE") &&//待摊对应的词汇代码更改（ACPAY->DEFERREDFEE） panjunfang modify 20090911 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                               rs.getInt("FPAYTYPE") == 0) { //如果是预提待摊的总额，则不乘-1.sj edit 20080731
                        investstorage.setStrAccBalance(YssD.add(YssD.add(rs.getDouble(
                            "FMoney"),
                            rs.getDouble("FIVPayMoney")),
                            YssFun.toDouble(investstorage.
                                            getStrAccBalance())) + "");

                        investstorage.setStrBaseCuryBal(YssD.add(YssD.add(rs.
                            getDouble(
                                "FBaseCuryMoney"),
                            rs.getDouble("FIVPayBaseCuryMoney")),
                            YssFun.toDouble(investstorage.getStrBaseCuryBal())) +
                            "");

                        investstorage.setStrPortCuryBal(YssD.add(YssD.add(rs.
                            getDouble(
                                "FPortCuryMoney"),
                            rs.getDouble("FIVPayPortCuryMoney")),
                            YssFun.toDouble(investstorage.getStrPortCuryBal())) +
                            "");
                    }
                    hmEveStg.put(sKey, investstorage);
                } else {
                    investstorage = new InvestBean();
                    investstorage.setStrStorageDate(YssFun.formatDate(dOperDate,
                        "yyyy-MM-dd"));
                    if (rs.getDate("FStorageDate") == null && //前一天没有库存 当天有交易
                        rs.getDate("FTransDate") != null) {
                        investstorage.setStrIvPayCatCode(rs.getString("FIVPayCatCode"));
                        investstorage.setStrPortCode(rs.getString("FIVPayPortCode"));
                        investstorage.setStrCuryCode(rs.getString("FIVPayPortCury"));
                        if (rs.getString("FIVTYPE").equalsIgnoreCase("DEFERREDFEE") &&//待摊对应的业务类型更改，panjunfang modify 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                            rs.getInt("FPAYTYPE") == 0 &&
                            (rs.getString("FTsfTypeCode").equalsIgnoreCase("16") || rs.getString("FTsfTypeCode").equalsIgnoreCase("07"))) {//将计提的待摊值乘以-1，panjunfang modify 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A，国内业务中待摊收益计提产生的业务类型由07变更为16，此处保留07兼容历史数据
                            investstorage.setStrAccBalance(YssD.mul(rs.getDouble(
                                "FIVPayMoney"), -1) +
                                "");
                            investstorage.setStrBaseCuryBal(YssD.mul(rs.getDouble(
                                "FIVPayBaseCuryMoney"), -1) + "");
                            investstorage.setStrPortCuryBal(YssD.mul(rs.getDouble(
                                "FIVPayPortCuryMoney"), -1) + "");
                        } else {
                            investstorage.setStrAccBalance(rs.getDouble("FIVPayMoney") +
                                "");
                            investstorage.setStrBaseCuryBal(rs.getDouble(
                                "FIVPayBaseCuryMoney") + "");
                            investstorage.setStrPortCuryBal(rs.getDouble(
                                "FIVPayPortCuryMoney") + "");
                        }
                        investstorage.setStrFAnalysisCode1(analy1 ? rs.getString(
                            "FAnalysisCode1") : " ");
                        investstorage.setStrFAnalysisCode2(analy2 ? rs.getString(
                            "FAnalysisCode2") : " ");
                        investstorage.setStrFAnalysisCode3(analy3 ? rs.getString(
                            "FAnalysisCode3") : " ");
                      //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                        investstorage.setStrAttrClsCode(rs.getString("fattrclscode"));
                     //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    } else if ( ( (rs.getDate("FStorageDate") != null)
                                 && rs.getDate("FTransDate") != null) ||
                               ( (rs.getDate("FStorageDate") != null)
                                && rs.getDate("FTransDate") == null)) {
                        investstorage.setStrIvPayCatCode(rs.getString(
                            "FIVPayCatCodeStorage"));
                        investstorage.setStrPortCode(rs.getString("FPortCodeStorage"));
                        investstorage.setStrCuryCode(rs.getString("FCuryCodeStorage"));
                        if (rs.getString("FPayType") != null) {
                            if (rs.getString("FTsfTypeCode").equalsIgnoreCase("06") ||
                                rs.getString("FTsfTypeCode").equalsIgnoreCase("16") ||//待摊费用对应的调拨类型代码 panjunfang add 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                                rs.getString("FTsfTypeCode").equalsIgnoreCase("07")) { //应收款项或应付款项
                                if (rs.getString("FIVTYPE").equalsIgnoreCase("DEFERREDFEE") &&//待摊对应的业务类型更改，panjunfang modify 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                                    rs.getInt("FPAYTYPE") == 0 &&
                                    (rs.getString("FTsfTypeCode").equalsIgnoreCase("07") || rs.getString("FTsfTypeCode").equalsIgnoreCase("16"))) {//将计提的待摊值乘以-1，panjunfang modify 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A,国内业务中待摊收益计提产生的业务类型由07变更为16，此处保留07兼容历史数据
                                    investstorage.setStrAccBalance(YssD.add(rs.getDouble(
                                        "FMoney"),
                                        YssD.mul(rs.getDouble("FIVPayMoney"), -1)) +
                                        "");

                                    investstorage.setStrBaseCuryBal(YssD.add(rs.
                                        getDouble(
                                            "FBaseCuryMoney"),
                                        YssD.mul(rs.getDouble("FIVPayBaseCuryMoney"),
                                                 -1)) + "");

                                    investstorage.setStrPortCuryBal(YssD.add(rs.
                                        getDouble(
                                            "FPortCuryMoney"),
                                        YssD.mul(rs.getDouble("FIVPayPortCuryMoney"),
                                                 -1)) + "");
                                } else {
                                    investstorage.setStrAccBalance(YssD.add(rs.getDouble(
                                        "FMoney"),
                                        rs.getDouble("FIVPayMoney")) + "");
                                    //应该保存到基础货币库存中fazmm20070804
                                    investstorage.setStrBaseCuryBal(YssD.add(rs.
                                        getDouble(
                                            "FBaseCuryMoney"),
                                        rs.getDouble("FIVPayBaseCuryMoney")) + "");
                                    //应该保存到组合货币库存中fazmm20070804
                                    investstorage.setStrPortCuryBal(YssD.add(rs.
                                        getDouble(
                                            "FPortCuryMoney"),
                                        rs.getDouble("FIVPayPortCuryMoney")) + "");
                                }
                            } else if (rs.getString("FTsfTypeCode").equalsIgnoreCase(
                                "02") ||
                                       rs.getString("FTsfTypeCode").equalsIgnoreCase(
                                           "03")) { //收入或者是费用
                                investstorage.setStrAccBalance(YssD.sub(rs.getDouble(
                                    "FMoney"),
                                    rs.getDouble("FIVPayMoney")) + "");

                                investstorage.setStrBaseCuryBal(YssD.sub(rs.getDouble(
                                    "FBaseCuryMoney"),
                                    rs.getDouble("FIVPayBaseCuryMoney")) + "");

                                investstorage.setStrPortCuryBal(YssD.sub(rs.getDouble(
                                    "FPortCuryMoney"),
                                    rs.getDouble("FIVPayPortCuryMoney")) + "");

                            }
                        } else {
                            investstorage.setStrAccBalance(rs.getDouble("FMoney") + "");
                            investstorage.setStrBaseCuryBal(rs.getDouble(
                                "FBaseCuryMoney") + "");
                            investstorage.setStrPortCuryBal(rs.getDouble(
                                "FPortCuryMoney") + "");
                        }
                        //investstorage.setStrBaseCuryRate(rs.getDouble(
                        //      "FBaseCuryRateStorage") + "");
                        //investstorage.setStrPortCuryRate(rs.getDouble(
                        //      "FPortCuryRateStorage") + "");
                        investstorage.setStrFAnalysisCode1(analy1 ? rs.getString(
                            "FAnalysisCode1Storage") : " ");
                        investstorage.setStrFAnalysisCode2(analy2 ? rs.getString(
                            "FAnalysisCode2Storage") : " ");
                        investstorage.setStrFAnalysisCode3(analy3 ? rs.getString(
                            "FAnalysisCode3Storage") : " ");
                        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                        investstorage.setStrAttrClsCode(rs.getString("fattrclscodestorage"));
                     //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    }
                    hmEveStg.put(sKey, investstorage);
                    //all.add(investstorage);
                }
            }
            dbl.closeResultSetFinal(rs);
//         }
            iter = hmEveStg.keySet().iterator();
            while (iter.hasNext()) {
                sKey = (String) iter.next();
                investstorage = (InvestBean) hmEveStg.get(sKey);
                investstorage.setIntStorageState(0); //自动计算（未锁定）
                investstorage.checkStateId = 1;
                all.add(investstorage);
            }
            return all;
        } catch (Exception ye) {
            throw new YssException("系统进行运营库存统计时出现异常!\n", ye); //by 曹丞 2009.01.22 统计运营库存异常 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /***
     * add by wuweiqi 20110114 QDV4工银2010年12月22日01_A 
     * 针对资产估值后自动计提两费
     * 特殊说明这针对工银需求
     */
    public ArrayList getStorageStatData1(java.util.Date dOperDate) throws
    YssException {
    String strSql = "", strTmpSql = "", strTmpSql1 = "";
//  java.util.Date dOperDate = null;
    String strError = "统计运营支付库存出错";
    ResultSet rs = null;
    InvestBean investstorage = null;
    ArrayList all = new ArrayList();
    InvestPayBean investpay = new InvestPayBean();
    investpay.setYssPub(pub);

    boolean analy1; //判断是否需要用分析代码；杨
    boolean analy2;
    boolean analy3;

    String sKey = "";
    Hashtable hmEveStg = new Hashtable();
    Iterator iter = null;

    try {
        if (bYearChange) {
            yearChange(dOperDate, portCodes);
        }

        analy1 = operSql.storageAnalysis("FAnalysisCode1",
                                         YssOperCons.YSS_KCLX_InvestPayRec);
        analy2 = operSql.storageAnalysis("FAnalysisCode2",
                                         YssOperCons.YSS_KCLX_InvestPayRec);
        analy3 = operSql.storageAnalysis("FAnalysisCode3",
                                         YssOperCons.YSS_KCLX_InvestPayRec);

        if (portCodes.length() > 0) {
            strTmpSql = " and FPortCode in (" + portCodes + ")";
        } else {
        	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码

            strTmpSql = " and FPortCode in ( select FPortCode from " +
            pub.yssGetTableName("Tb_Para_Portfolio") +
            " where  FCheckState = 1 and Fassetgroupcode='" +
            pub.getAssetGroupCode() +
            "' ) ";

            
            //end by lidaolong
        }
//     for (int j = 0; j <= YssFun.dateDiff(dStartDate, dEndDate); j++) {
//        dOperDate = YssFun.addDay(dStartDate, j);
        if (YssFun.getMonth(dOperDate) == 1 && //代表是期初
            YssFun.getDay(dOperDate) == 1) {
            strTmpSql1 = strTmpSql + " and fyearmonth ='" +
                YssFun.formatDate(dOperDate, "yyyy") + "00'";
        } else {
            strTmpSql1 = strTmpSql + " and fyearmonth <>'" +
                YssFun.formatDate(dOperDate, "yyyy") + "00'" +
                " and FStorageDate = " +
                dbl.sqlDate(YssFun.addDay(dOperDate, -1));
        }
        //如果支付的汇率跟库存的汇率不一致时，会统计出两条记录，所以不能用汇率进行group by fazmm20071107
        strSql = "select  xx.*, yy.*," +
            dbl.sqlDate(dOperDate) +
            " as FOperDate " +
            " ,f.FIVType as FIVTYPE " +
            " from (Select b.FIVPayCatCode as FIVPayCatCode, b.FTsfTypeCode as FTsfTypeCode, b.FPortCode as FIVPayPortCode,b.FTransDate,b.FCuryCode as  FIVPayPortCury,b.FPayType as FPayType," +
            (analy1 ? (dbl.sqlIsNull(" b.FAnalysisCode1", "''") +
                       " as FAnalysisCode1 ,") : " ") +
            (analy2 ? (dbl.sqlIsNull(" b.FAnalysisCode2", "''") +
                       " as FAnalysisCode2 ,") : " ") +
            (analy3 ? (dbl.sqlIsNull(" b.FAnalysisCode3", "''") +
                       " as FAnalysisCode3 ,") : " ") +
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
            "nvl(b.fattrclscode,' ')as fattrclscode,"+
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            " Sum(" +
            dbl.sqlIsNull("b.FMoney", "0") +
            ") as FIVPayMoney, sum(" +
            dbl.sqlIsNull("b.FBaseCuryMoney", "0") +
            ") as FIVPayBaseCuryMoney, sum(" +
            dbl.sqlIsNull("b.FPortCuryMoney", "0") +
            ") as FIVPayPortCuryMoney from (" +
            "select b1.*,b2.FPayType from (" + //运营费用统计库存问题，运营费用支付时统计有问题fazmm20070918
            "select FIVPayCatCode,(case FTsfTypeCode when '02' then '06'" +
            "when '16' then '06'" +//MS00017  国内预提待摊
            "when '03' then '07'" +
            " when '97' then '07' " + //add by huangqirong 2013-02-02 stroy #3488 补差款 97转为 07才能统计完整
            "else FTsfTypeCode end) as FTsfTypeCode, " +
            "FPortCode, FTransDate, FCuryCode, " +
//---------------------按照配置获取分析代码 sj edit 20080805 bug 0000362 -------------------
            (analy1 ? "FAnalysisCode1," : " ") +
            (analy2 ? "FAnalysisCode2," : " ") +
            (analy3 ? "FAnalysisCode3," : " ") +
//---------------------------------------------------------------------------------------
            //" FAnalysisCode1," +
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
            " fattrclscode,"+
           //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            "(case FTsfTypeCode when '02' then -FMoney " +
            "when '16' then -FMoney " +//冲减待摊运营收支款
            "when '03' then -FMoney " +
            "else FMoney end) as FMoney, " +
            "(case FTsfTypeCode when '02' then -FBaseCuryMoney " +
            "when '16' then -FBaseCuryMoney " +//冲减待摊运营收支款
            "when '03' then -FBaseCuryMoney " +
            "else FBaseCuryMoney end) as FBaseCuryMoney," +
            "(case FTsfTypeCode when '02' then -FPortCuryMoney " +
            "when '16' then -FPortCuryMoney " +//冲减待摊运营收支款
            "when '03' then -FPortCuryMoney " +
            "else FPortCuryMoney end) as FPortCuryMoney," +
            "FBaseCuryRate,FPortCuryRate" +
            " from " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
            " where FTsfTypeCode in (" +
            //运营费用库存主表统计fazmm20070913
            dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + "," +
            dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + "," +
            dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + "," +
            dbl.sqlString(YssOperCons.YSS_ZJDBLX_PAYOUT) + "," +//预提待摊-支出
            dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + 
            "," + dbl.sqlString(YssOperCons.Yss_ZJDBLX_SUPPLEMENT) + //add by huangqirong 2013-02-02 stroy #3488 补差款
            ") " +
            " and fcheckstate = 1 and ftransdate = " +
            dbl.sqlDate(dOperDate) + strTmpSql +
            investpay.getAutoCharge(portCodes)+//add by wuweiqi 20110115 添加筛选条件两费不统计  QDV4工银2010年11月1日01_A  
            ") b1 left join (select FPayType,FIVPayCatCode from Tb_Base_InvestPayCat where FCheckState=1) b2 on b1.FIVPayCatCode=b2.FIVPayCatCode ) b " +
            " group by b.FIVPayCatCode, b.FTsfTypeCode, b.FPortCode," +
            (analy1 ? "b.FAnalysisCode1," : " ") +
            (analy2 ? "b.FAnalysisCode2," : " ") +
            (analy3 ? "b.FAnalysisCode3," : " ") +
           //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
            "b.fattrclscode,"+
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            "b.FTransDate,b.FCuryCode,b.FPayType) yy ";

        strSql = strSql +
            " full join ( select a.FYearMonth, a.FIVPayCatCode as FIVPayCatCodeStorage," +
            " a.FStorageDate, a.FPortCode as FPortCodeStorage, a.FCuryCode as FCuryCodeStorage,  " +
            dbl.sqlIsNull("a.FBal", "0") +
            " as FMoney , " +
            dbl.sqlIsNull("a.FBaseCuryBal", "0") +
            " as FBaseCuryMoney , " +
            dbl.sqlIsNull("a.FPortCuryBal", "0") +
            " as FPortCuryMoney " +
            //"a.FPortCuryRate as FPortCuryRateStorage, a.FBaseCuryRate as FBaseCuryRateStorage " +
            (analy1 ? ("," + dbl.sqlIsNull("a.FAnalysisCode1", "' '") +
                       " as FAnalysisCode1Storage") : " ") +
            (analy2 ? ("," + dbl.sqlIsNull("a.FAnalysisCode2", "' '") +
                       " as FAnalysisCode2Storage ") : " ") + //错误的sql语句，多了个逗号。sj edit 20080805 bug 0000362
            (analy3 ? ("," + dbl.sqlIsNull("a.FAnalysisCode3", "' '") +
                       " as FAnalysisCode3Storage ") : " ") +
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
            ",nvl(a.fattrclscode,' ') as fattrclscodestorage "+
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            " from " +
            pub.yssGetTableName("Tb_Stock_Invest") +
            " a where FCheckState = 1 " + strTmpSql1 +
            //------------------ 屏蔽掉所有的金额为零的记录 sj 20080401  --------------------
            " and (a.FBal <> 0  or a.FBaseCuryBal <> 0 or a.FPortCuryBal <> 0) " +
            //---------------------------------------------------------------------------
            ") xx on xx.FIVPayCatCodeStorage=yy.FIVPayCatCode and xx.FPortCodeStorage=yy.FIVPayPortCode" +
            (analy1 ? " and xx.FAnalysisCode1Storage = yy.FAnalysisCode1 " :
             " ") +
            (analy2 ? " and xx.FAnalysisCode2Storage = yy.FAnalysisCode2 " :
             " ") +
            (analy3 ? " and xx.FAnalysisCode3Storage = yy.FAnalysisCode3 " :
             " ") +
          //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
             " and xx.fattrclscodestorage = yy.fattrclscode "+
          //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
          // add by fangjiang 2011.02.14 #2279
             " and xx.FCuryCodeStorage = yy.FIVPayPortCury " +
          //---------------------------
            //--------------获取运营的类型。sj edit 20080731 -------------------------
            " join (select FIVPayCatCode,FPayType,FIVType from Tb_Base_InvestPayCat where FCheckState = 1 " + 
            " ) f on (yy.FIVPayCatCode = f.FIVPayCatCode or xx.FIVPayCatCodeStorage = f.Fivpaycatcode)";
        //---------------------------------------------------------------------
        //-------------屏蔽掉所有的金额为零的记录 sj 20080317 -------------------------//
        // " where xx.FMoney <> 0 or xx.FBaseCuryMoney <> 0 or xx.FPortCuryMoney <> 0";
        //---------------------------------------------------------------------------
        //" and " +
        //dbl.sqlDateAdd("xx.FStorageDate", "+1") +
        //" = yy.FTransDate ";
        /**shashijie 2012-5-9 STORY 2565  oracle 9i版本会报错,要装补丁*/
        rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
        //rs = dbl.openResultSet(strSql);
		/**end*/
        while (rs.next()) {
            //investstorage = new InvestBean();
//        sKey = (rs.getString("FIVPayCatCode") == null ?
//                rs.getString("FIVPayCatCodeStorage") :
//                rs.getString("FIVPayCatCode")) + "\f" +
//              (rs.getString("FIVPayPortCode") == null ?
//               rs.getString("FPortCodeStorage") :
//               rs.getString("FIVPayPortCode")) +
//              (analy1 ?
//               "\f" +
//               (rs.getString("FAnalysisCode1") == null ?
//                rs.getString("FAnalysisCode1Storage") :
//                rs.getString("FAnalysisCode1")) : "");
            sKey = (rs.getString("FIVPayCatCode") == null ?
                    rs.getString("FIVPayCatCodeStorage") :
                    rs.getString("FIVPayCatCode")) + "\f" +
                (rs.getString("FIVPayPortCode") == null ?
                 rs.getString("FPortCodeStorage") :
                 rs.getString("FIVPayPortCode")) +
                (analy1 ?
                 "\f" +
                 (rs.getString("FAnalysisCode1") == null ?
                  rs.getString("FAnalysisCode1Storage") :
                  rs.getString("FAnalysisCode1")) : "") +
                //--------添加了分析代码2的筛选条件 sj modified 20081218 MS00108---------
                (analy2 ?
                 "\f" +
                 (rs.getString("FAnalysisCode2") == null ?
                  rs.getString("FAnalysisCode2Storage") :
                  rs.getString("FAnalysisCode2")) : "") +
                //-------------------------------------------------------------
                //--------MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified---------//
                (analy3 ?
                 "\f" +
                 (rs.getString("FAnalysisCode3") == null ?
                  rs.getString("FAnalysisCode3Storage") :
                  rs.getString("FAnalysisCode3")) : "")+
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                  "\f"+(rs.getString("fattrclscode") == null ?rs.getString("fattrclscodestorage"):rs.getString("fattrclscode"))
                 //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
		         // add by fangjiang 2011.02.14 #2279
		            + "\f" + (rs.getString("FCuryCodeStorage") == null ? rs.getString("FIVPayPortCury") : rs.getString("FCuryCodeStorage"));
		       	//---------------------------
            //-------------------------------------------------------------------------------//


            if (hmEveStg.get(sKey) != null) {
                investstorage = (InvestBean) hmEveStg.get(sKey);
                if (((rs.getString("FTsfTypeCode")!=null&&rs.getString("FTsfTypeCode").equalsIgnoreCase("07")) || rs.getString("FTsfTypeCode").equalsIgnoreCase("06") ||
                		(rs.getString("FTsfTypeCode")!=null&&rs.getString("FTsfTypeCode").equalsIgnoreCase("16"))) &&//国内业务中待摊收益计提产生的业务类型由07变更为16，此处保留07兼容历史数据
                		//20120221 modified by liubo.Story #2139
                		//在转换日期大于等于统计日期的预提转待摊的预提费用和待摊一样处理
                		//======================================
//                    rs.getString("FIVTYPE").equalsIgnoreCase("DEFERREDFEE") &&//待摊对应的业务类型和词汇代码更改 panjunfang modify 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                	IFTranFTransitionAvailable(rs.getString("FPortCodeStorage"),rs.getString("FIVPayCatCodeStorage"),rs.getDate("FOperDate"),rs.getString("FIVTYPE")) //&& rs.getInt("FPAYTYPE") == 0
                	//====================end==================
                    ) {if (!sTransition)
                	{
                        investstorage.setStrAccBalance(
                            YssD.add(YssD.mul(YssD.add(rs.getDouble("FMoney"),
                            rs.getDouble("FIVPayMoney")), -1),
                                     YssFun.toDouble(investstorage.getStrAccBalance())) +
                            "");

                        investstorage.setStrBaseCuryBal(YssD.add(YssD.mul(YssD.add(rs.
                            getDouble(
                                "FBaseCuryMoney"),
                            rs.getDouble("FIVPayBaseCuryMoney")), -1),
                            YssFun.toDouble(investstorage.getStrBaseCuryBal())) +
                            "");

                        investstorage.setStrPortCuryBal(YssD.add(YssD.mul(YssD.add(rs.
                            getDouble(
                                "FPortCuryMoney"),
                            rs.getDouble("FIVPayPortCuryMoney")), -1),
                            YssFun.toDouble(investstorage.getStrPortCuryBal())) +
                            "");
                	}
                	else
                	{
                		investstorage.setStrAccBalance(
	                            YssD.add(YssD.mul(YssD.add(0,rs.getDouble("FIVPayMoney")
	                            ), 1),
	                                     YssFun.toDouble(investstorage.getStrAccBalance())) +
	                            "");
	
	                    investstorage.setStrBaseCuryBal(YssD.add(YssD.mul(YssD.add(0,rs.getDouble("FIVPayBaseCuryMoney"))
	                    		, 1),
	                            YssFun.toDouble(investstorage.getStrBaseCuryBal())) +
	                            "");
	
	                    investstorage.setStrPortCuryBal(YssD.add(YssD.mul(YssD.add(0,rs.getDouble("FIVPayPortCuryMoney")
	                            ), 1),
	                            YssFun.toDouble(investstorage.getStrPortCuryBal())) +
	                            "");
                	}
                } else if (rs.getString("FTsfTypeCode").equalsIgnoreCase("06") &&
                           rs.getString("FIVTYPE").equalsIgnoreCase("DEFERREDFEE") &&//待摊对应的词汇代码更改（ACPAY->DEFERREDFEE） panjunfang modify 20090911 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                           rs.getInt("FPAYTYPE") == 0) { //如果是预提待摊的总额，则不乘-1.sj edit 20080731
                    investstorage.setStrAccBalance(YssD.add(YssD.add(rs.getDouble(
                        "FMoney"),
                        rs.getDouble("FIVPayMoney")),
                        YssFun.toDouble(investstorage.
                                        getStrAccBalance())) + "");

                    investstorage.setStrBaseCuryBal(YssD.add(YssD.add(rs.
                        getDouble(
                            "FBaseCuryMoney"),
                        rs.getDouble("FIVPayBaseCuryMoney")),
                        YssFun.toDouble(investstorage.getStrBaseCuryBal())) +
                        "");

                    investstorage.setStrPortCuryBal(YssD.add(YssD.add(rs.
                        getDouble(
                            "FPortCuryMoney"),
                        rs.getDouble("FIVPayPortCuryMoney")),
                        YssFun.toDouble(investstorage.getStrPortCuryBal())) +
                        "");
                }
                hmEveStg.put(sKey, investstorage);
            } else {
                investstorage = new InvestBean();
                investstorage.setStrStorageDate(YssFun.formatDate(dOperDate,
                    "yyyy-MM-dd"));
                if (rs.getDate("FStorageDate") == null && //前一天没有库存 当天有交易
                    rs.getDate("FTransDate") != null) {
                    investstorage.setStrIvPayCatCode(rs.getString("FIVPayCatCode"));
                    investstorage.setStrPortCode(rs.getString("FIVPayPortCode"));
                    investstorage.setStrCuryCode(rs.getString("FIVPayPortCury"));
                    if (rs.getString("FIVTYPE").equalsIgnoreCase("DEFERREDFEE") &&//待摊对应的业务类型更改，panjunfang modify 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                        rs.getInt("FPAYTYPE") == 0 &&
                        (rs.getString("FTsfTypeCode").equalsIgnoreCase("16") || rs.getString("FTsfTypeCode").equalsIgnoreCase("07"))) {//将计提的待摊值乘以-1，panjunfang modify 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A，国内业务中待摊收益计提产生的业务类型由07变更为16，此处保留07兼容历史数据
                        investstorage.setStrAccBalance(YssD.mul(rs.getDouble(
                            "FIVPayMoney"), -1) +
                            "");
                        investstorage.setStrBaseCuryBal(YssD.mul(rs.getDouble(
                            "FIVPayBaseCuryMoney"), -1) + "");
                        investstorage.setStrPortCuryBal(YssD.mul(rs.getDouble(
                            "FIVPayPortCuryMoney"), -1) + "");
                    } else {
                        investstorage.setStrAccBalance(rs.getDouble("FIVPayMoney") +
                            "");
                        investstorage.setStrBaseCuryBal(rs.getDouble(
                            "FIVPayBaseCuryMoney") + "");
                        investstorage.setStrPortCuryBal(rs.getDouble(
                            "FIVPayPortCuryMoney") + "");
                    }
                    investstorage.setStrFAnalysisCode1(analy1 ? rs.getString(
                        "FAnalysisCode1") : " ");
                    investstorage.setStrFAnalysisCode2(analy2 ? rs.getString(
                        "FAnalysisCode2") : " ");
                    investstorage.setStrFAnalysisCode3(analy3 ? rs.getString(
                        "FAnalysisCode3") : " ");
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    investstorage.setStrAttrClsCode(rs.getString("fattrclscode"));
                 //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                } else if ( ( (rs.getDate("FStorageDate") != null)
                             && rs.getDate("FTransDate") != null) ||
                           ( (rs.getDate("FStorageDate") != null)
                            && rs.getDate("FTransDate") == null)) {
                    investstorage.setStrIvPayCatCode(rs.getString(
                        "FIVPayCatCodeStorage"));
                    investstorage.setStrPortCode(rs.getString("FPortCodeStorage"));
                    investstorage.setStrCuryCode(rs.getString("FCuryCodeStorage"));
                    if (rs.getString("FPayType") != null) {
                        if (rs.getString("FTsfTypeCode").equalsIgnoreCase("06") ||
                            rs.getString("FTsfTypeCode").equalsIgnoreCase("16") ||//待摊费用对应的调拨类型代码 panjunfang add 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                            rs.getString("FTsfTypeCode").equalsIgnoreCase("07")) { //应收款项或应付款项
                            if (rs.getString("FIVTYPE").equalsIgnoreCase("DEFERREDFEE") &&//待摊对应的业务类型更改，panjunfang modify 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                                rs.getInt("FPAYTYPE") == 0 &&
                                (rs.getString("FTsfTypeCode").equalsIgnoreCase("07") || rs.getString("FTsfTypeCode").equalsIgnoreCase("16"))) {//将计提的待摊值乘以-1，panjunfang modify 20090717 MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A,国内业务中待摊收益计提产生的业务类型由07变更为16，此处保留07兼容历史数据
                                investstorage.setStrAccBalance(YssD.add(rs.getDouble(
                                    "FMoney"),
                                    YssD.mul(rs.getDouble("FIVPayMoney"), -1)) +
                                    "");

                                investstorage.setStrBaseCuryBal(YssD.add(rs.
                                    getDouble(
                                        "FBaseCuryMoney"),
                                    YssD.mul(rs.getDouble("FIVPayBaseCuryMoney"),
                                             -1)) + "");

                                investstorage.setStrPortCuryBal(YssD.add(rs.
                                    getDouble(
                                        "FPortCuryMoney"),
                                    YssD.mul(rs.getDouble("FIVPayPortCuryMoney"),
                                             -1)) + "");
                            } else {
                                investstorage.setStrAccBalance(YssD.add(rs.getDouble(
                                    "FMoney"),
                                    rs.getDouble("FIVPayMoney")) + "");
                                //应该保存到基础货币库存中fazmm20070804
                                investstorage.setStrBaseCuryBal(YssD.add(rs.
                                    getDouble(
                                        "FBaseCuryMoney"),
                                    rs.getDouble("FIVPayBaseCuryMoney")) + "");
                                //应该保存到组合货币库存中fazmm20070804
                                investstorage.setStrPortCuryBal(YssD.add(rs.
                                    getDouble(
                                        "FPortCuryMoney"),
                                    rs.getDouble("FIVPayPortCuryMoney")) + "");
                            }
                        } else if (rs.getString("FTsfTypeCode").equalsIgnoreCase(
                            "02") ||
                                   rs.getString("FTsfTypeCode").equalsIgnoreCase(
                                       "03")) { //收入或者是费用
                            investstorage.setStrAccBalance(YssD.sub(rs.getDouble(
                                "FMoney"),
                                rs.getDouble("FIVPayMoney")) + "");

                            investstorage.setStrBaseCuryBal(YssD.sub(rs.getDouble(
                                "FBaseCuryMoney"),
                                rs.getDouble("FIVPayBaseCuryMoney")) + "");

                            investstorage.setStrPortCuryBal(YssD.sub(rs.getDouble(
                                "FPortCuryMoney"),
                                rs.getDouble("FIVPayPortCuryMoney")) + "");

                        }
                    } else {
                        investstorage.setStrAccBalance(rs.getDouble("FMoney") + "");
                        investstorage.setStrBaseCuryBal(rs.getDouble(
                            "FBaseCuryMoney") + "");
                        investstorage.setStrPortCuryBal(rs.getDouble(
                            "FPortCuryMoney") + "");
                    }
                    //investstorage.setStrBaseCuryRate(rs.getDouble(
                    //      "FBaseCuryRateStorage") + "");
                    //investstorage.setStrPortCuryRate(rs.getDouble(
                    //      "FPortCuryRateStorage") + "");
                    investstorage.setStrFAnalysisCode1(analy1 ? rs.getString(
                        "FAnalysisCode1Storage") : " ");
                    investstorage.setStrFAnalysisCode2(analy2 ? rs.getString(
                        "FAnalysisCode2Storage") : " ");
                    investstorage.setStrFAnalysisCode3(analy3 ? rs.getString(
                        "FAnalysisCode3Storage") : " ");
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                    investstorage.setStrAttrClsCode(rs.getString("fattrclscodestorage"));
                 //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                }
                hmEveStg.put(sKey, investstorage);
                //all.add(investstorage);
            }
        }
        dbl.closeResultSetFinal(rs);
//     }
        iter = hmEveStg.keySet().iterator();
        while (iter.hasNext()) {
            sKey = (String) iter.next();
            investstorage = (InvestBean) hmEveStg.get(sKey);
            investstorage.setIntStorageState(0); //自动计算（未锁定）
            investstorage.checkStateId = 1;
            all.add(investstorage);
        }
        return all;
    } catch (Exception ye) {
        throw new YssException("系统进行运营库存统计时出现异常!\n", ye); //by 曹丞 2009.01.22 统计运营库存异常 MS00004 QDV4.1-2009.2.1_09A
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
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Invest") +
                " where FYearMonth = " + dbl.sqlString(YearMonth) +
                " and FPortCode in( " + operSql.sqlCodes(portCode) + ")"; //添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的 by leeyu 20090220 QDV4华夏2009年2月13日01_B MS00246;
            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_Invest") +
                "(FIVPAYCATCODE,FYearMonth,FStorageDate,FPORTCODE," +
                "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3," +
                "FCURYCODE,FBAL ,FBASECURYRATE,FBASECURYBAL," +
                "FPORTCURYRATE,FPORTCURYBAL,FSTORAGEIND,FCheckState," +
                "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" +
                "(select FIVPAYCATCODE," + dbl.sqlString(YearMonth) +
                " as FYearMonth, " +
                dbl.sqlDate(new Integer(Year).toString() + "-01-01") +
                " as FStorageDate,FPORTCODE," +
                "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3," +
                "FCURYCODE,FBAL ,FBASECURYRATE,FBASECURYBAL," +
                "FPORTCURYRATE,FPORTCURYBAL,FSTORAGEIND,FCheckState," +
                dbl.sqlString(pub.getUserCode()) +
                " as FCREATOR," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " as FCREATETIME," +
                dbl.sqlString(pub.getUserName()) + " as FCHECKUSER," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " as FCHECKTIME" +
                " from " + pub.yssGetTableName("Tb_Stock_Invest") +
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

    /**
     * 统计库存之后将原币金额为 0 的数据的基础货币余额和组合货币余额都设为0
     * @param dDate Date
     * @throws YssException
     * MS00231 QDV4中保2009年02月05日03_B
     */
    protected void afterSaveStorage(java.util.Date dDate) throws YssException {
        Connection conn = dbl.loadConnection();
        String strUpdate = "";
        boolean bTrans = false;
        try {
            strUpdate = "UPDATE " + pub.yssGetTableName("Tb_Stock_Invest") +
                " SET FPORTCURYBAL = 0, " +
                " FBASECURYBAL = 0 " +
                " WHERE FStorageDate = " + dbl.sqlDate(dDate) +
                " AND FPortCode IN (" + this.portCodes + ")" +
                " AND " + dbl.sqlSubStr("FYearMonth", "5", "2") + " <> '00'" +
                " AND FBAL = 0"; //当原币金额为0时，对基础和组合金额进行清零。sj modified
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
    
    private boolean IFTranFTransitionAvailable(String sPortCode,String sIvCatCode,java.util.Date dDate,String sIVType) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;
    	
    	try
    	{
	    	if ("DEFERREDFEE".equals(sIVType))
	    	{
	    		sTransition = false;
	    		return true;
	    	}
	    	
	    	strSql = "select * from " + pub.yssGetTableName("tb_para_investpay") + " where FIVPAYCATCODE = " + dbl.sqlString(sIvCatCode) + 
	    			 " and FPORTCODE = " + dbl.sqlString(sPortCode) + " and FTRANSITIONDATE = " + dbl.sqlDate(dDate) + "";
	    	
	    	rs = dbl.queryByPreparedStatement(strSql);
	    	
	    	while(rs.next())
	    	{
	    		if(rs.getString("FTRANSITION").equals("1") && YssFun.dateDiff(rs.getDate("FTRANSITIONDATE"),dDate) >= 0)
	    		{
	    			sTransition = true;
	    			return true;
	    		}
	    	}

	    	return false;
    	}
    	catch(Exception ye)
    	{
    		throw new YssException(ye.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    }



}
