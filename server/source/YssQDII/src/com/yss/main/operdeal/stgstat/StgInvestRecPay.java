package com.yss.main.operdeal.stgstat;

import java.util.ArrayList;
import java.sql.ResultSet;

import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.storagemanage.InvestPayRecBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.util.YssD;
import java.sql.Connection;

public class StgInvestRecPay
    extends BaseStgStatDeal {
    public StgInvestRecPay() {
    }

    public ArrayList getStorageStatData(java.util.Date dDate) throws
        YssException {
        String strSql = "", strTmpSql = "";
        ResultSet rs = null;
        InvestPayRecBean investrecpaybal = null;
        ArrayList all = new ArrayList();
//      java.util.Date dDate = null;
        String strError = "统计运营应收应付库存出错";

        boolean analy1; //判断是否需要用分析代码；杨
        boolean analy2;
        boolean analy3;

        try {
        	//MS01701 QDV4太平2010年09月03日01_AB 调整太平对应收应付库存采用累计法统计算法 by leeyu 20100909
        	boolean bTPVer =false;//区分是太平资产的库存统计还是QDII的库存统计,合并版本时调整
        	CtlPubPara pubPara = null;
        	pubPara =new CtlPubPara();
        	pubPara.setYssPub(pub);
        	String sPara =pubPara.getNavType();//通过净值表类型来判断
        	if(sPara!=null && sPara.trim().equalsIgnoreCase("new")){
        		bTPVer=false;//国内QDII统计模式
        	}else{
        		bTPVer=true;//太平资产统计模式
        	}
        	//MS01701 QDV4太平2010年09月03日01_AB by leeyu 20100909
            if (bYearChange) {
                yearChange(dDate, portCodes);
            }
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "InvestPayRec");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "InvestPayRec");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "InvestPayRec");

//         for (int j = 0; j <= YssFun.dateDiff(dStartDate, dEndDate); j++) {
//            dDate = YssFun.addDay(dStartDate, j);
            if (YssFun.getMonth(dDate) == 1 &&
                YssFun.getDay(dDate) == 1) {
                strTmpSql = " and fyearmonth ='" +
                    YssFun.formatDate(dDate, "yyyy") + "00'";
            } else {
                strTmpSql = " and fyearmonth <>'" +
                    YssFun.formatDate(dDate, "yyyy") + "00'" +
                    " and FStorageDate=" +
                    dbl.sqlDate(YssFun.addDay(dDate, -1));
            }
            strSql = "select a.*, b.*,e.*, p.*," + dbl.sqlDate(dDate) +//panjunfang add (p.*,) 20090907
                " as FOperDate from(" +
                " select FTransDate, FPortCode, FIVPayCatCode, FTsfTypeCode, " +
                dbl.sqlIsNull("FSubTsfTypeCode", "' '") +
                " as FSubTsfTypeCode, FCuryCode, " +
                (analy1 ? (dbl.sqlIsNull("FAnalysisCode1", "' '") +
                           " as FAnalysisCode1,") : " ") +
                (analy2 ? (dbl.sqlIsNull("FAnalysisCode2", "' '") +
                           " as FAnalysisCode2, ") : " ") +
                (analy3 ? (dbl.sqlIsNull("FAnalysisCode3", "' '") +
                           " as FAnalysisCode3,") : " ") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                 "nvl(fattrclscode,' ') as fattrclscode," +  
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//  
                " sum(FMoney) as FMoney, " +
                " sum(FBaseCuryMoney) as FBaseCuryMoney," +
                " sum(FPortCuryMoney) as FPortCuryMoney from " +
                //modify huangqirong 2013-02-04 story #3488
				" ( select FTransDate,FPortCode,FIVPayCatCode, " +
				//start add by huangqirong 2013-03-05 bug #7233
                " FAnalysisCode1," +
                " FAnalysisCode2, " +
                " FAnalysisCode3, " +
				//---end---
                " (case when FTsfTypeCode = '97' then '07' else FTsfTypeCode end ) as FTsfTypeCode, " +
                " (case when NVL(FSubTsfTypeCode, ' ') = '9707IV' then '07IV' else NVL(FSubTsfTypeCode,' ') end) as FSubTsfTypeCode," +
                " FCuryCode," +
                " nvl(fattrclscode, ' ') as fattrclscode, " +
                " FMoney as FMoney, " +
                " FBaseCuryMoney as FBaseCuryMoney," +
                " FPortCuryMoney as FPortCuryMoney from " +
                        pub.yssGetTableName("Tb_Data_InvestPayRec") +
                        " where FTransDate =" + dbl.sqlDate(dDate) +
                        " and FPortCode in (" + portCodes + ")" +
                        " and FCheckState = 1 and (FTsfTypeCode in (" +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) + "," +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay) + "," +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) + "," +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + "," +
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_PAYOUT) + "," + //国内预提待摊-支出 MS00017 QDV4.1赢时胜（上海）2009年4月20日17_A panjufang
                        dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) +  " , " +
                        dbl.sqlString(YssOperCons.Yss_ZJDBLX_SUPPLEMENT) + 
                        "))) " +
				//end huangqirong 2013-02-04 story #3488
                " group by FTransDate, FPortCode, FIVPayCatCode," +
                (analy1 ? "FAnalysisCode1," : " ") +
                (analy2 ? "FAnalysisCode2," : " ") +
                (analy3 ? "FAnalysisCode3," : " ") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " fattrclscode," +  
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//  
                " FTsfTypeCode, FSubTsfTypeCode, FCuryCode " +
                //--------------------------------------------------
                " ) a full join (select FStorageDate, FPortCode as FPortCode2, FIVPayCatCode as FIVPayCatCode2, FTsfTypeCode as FTsfTypeCode2, " +
                dbl.sqlIsNull("FSubTsfTypeCode", "' '") +
                " as FSubTsfTypeCode2, FCuryCode as FCuryCode2, " +
                (analy1 ? (dbl.sqlIsNull("FAnalysisCode1", "' '") +
                           " as FAnalysisCode12, ") : " ") +
                (analy2 ? (dbl.sqlIsNull("FAnalysisCode2", "' '") +
                           " as FAnalysisCode22, ") : " ") +
                (analy3 ? (dbl.sqlIsNull("FAnalysisCode3", "' '") +
                           " as FAnalysisCode32, ") : " ") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " nvl(fattrclscode,' ') as fattrclscode2," +  
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//             
                dbl.sqlIsNull("FBal", "0") + " as FBal, " +
                dbl.sqlIsNull("FBaseCuryBal", "0") + " as FBaseCuryBal, " +
                dbl.sqlIsNull("FPortCuryBal", "0") + " as FPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                " where FCheckstate=1 " + strTmpSql +
                " and FPortCode in (" + portCodes + ")" +
                " )b on a.FPortCode=b.FPortCode2 and a.FIVPayCatCode=b.FIVPayCatCode2 and a.FTsfTypeCode=b.FTsfTypeCode2 and a.FSubTsfTypeCode=b.FSubTsfTypeCode2 " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " and a.fattrclscode = b.fattrclscode2 " +  
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
               // add by fangjiang 2011.02.14 #2279 
                " and a.FCuryCode = b.FCuryCode2 " +
               //--------------------
                (analy1 ? " and a.FAnalysisCode1=b.FAnalysisCode12 " : " ") +
                (analy2 ? " and a.Fanalysiscode2=b.FAnalysisCode22 " : " ") +
                (analy3 ? " and a.FanalysisCode3=b.FAnalysisCode32 " : " ");
            strSql = strSql + " left join ( select FTransDate as FTransDate3, FPortCode as FPortCode3, FIVPayCatCode as FIVPayCatCode3, FTsfTypeCode as FTsfTypeCode3, " +
                dbl.sqlIsNull("FSubTsfTypeCode", "' '") +
                " as FSubTsfTypeCode3, FCuryCode as FCuryCode3, " +
                (analy1 ? (dbl.sqlIsNull("FAnalysisCode1", "' '") +
                           " as FAnalysisCode13,") : " ") +
                (analy2 ? (dbl.sqlIsNull("FAnalysisCode2", "' '") +
                           " as FAnalysisCode23, ") : " ") +
                (analy3 ? (dbl.sqlIsNull("FAnalysisCode3", "' '") +
                           " as FAnalysisCode33,") : " ") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " nvl(fattrclscode,' ') as fattrclscode3, " +  
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//                       
                " sum(-FMoney) as FMoney3, " +
                " sum(-FBaseCuryMoney) as FBaseCuryMoney3," +
                " sum(-FPortCuryMoney) as FPortCuryMoney3 from " +
                "(select FTransDate,FPortCode,FIVPayCatCode, FCuryCode," +
                (analy1 ? "FAnalysisCode1," : " ") +
                (analy2 ? "FAnalysisCode2," : " ") +
                (analy3 ? "FAnalysisCode3," : " ") +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " fattrclscode, " +  
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                "(case when FTsfTypeCode = '02' then '06' " +
                //=======添加国内预提待摊的处理 MS00017 QDV4.1赢时胜（上海）2009年4月20日17_A====
                //应收应付库存表中应收'06'与应收应付数据表中收入'16'对应上,把应收,应付运营收支款冲掉
                //处理结转的运营收支款，把应收,应付运营收支款冲掉,应收应付库存表中应收'06'，应付'07'与应收应付数据表中收入'02'，费用'03'对应上。
                " when FTsfTypeCode = '16' then '06' " +
                " when FTsfTypeCode = '03' then '07' end) as FTsfTypeCode," +
                //处理结转的运营收支款，把应收,应付运营收支款冲掉,应收应付库存表中应收'06'，应付'07'与应收应付数据表中收入'02'，费用'03'对应上。
                "(case when FSubTsfTypeCode = '02IV' then '06IV' " +
                " when FSubTsfTypeCode = '16IV' then '06IV' " +
                " when FSubTsfTypeCode = '03IV' then '07IV' end) as FSubTsfTypeCode," +
                "FMoney,FBaseCuryMoney,FPortCuryMoney,FCheckState from " +
                pub.yssGetTableName("Tb_Data_InvestPayRec") +
                "  where FTsfTypeCode in ('02', '03','16')) t" +
                //===========End MS00017 panjunfang add 20090630============================
                " where FTransDate =" + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes + ")" +
                " and FCheckState = 1 " +
                " group by FTransDate, FPortCode, FIVPayCatCode" +
              //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " ,fattrclscode " +  
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//  
                (analy1 ? ", FAnalysisCode1" : " ") +
                (analy2 ? ", FAnalysisCode2" : " ") +
                (analy3 ? ", FAnalysisCode3" : " ") +
                ", FTsfTypeCode, FSubTsfTypeCode, FCuryCode " +
                " ) e on e.FPortCode3=a.FPortCode and e.FIVPayCatCode3=a.FIVPayCatCode " +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " and e.fattrclscode3 = a.fattrclscode " +  
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//  
                // add by fangjiang 2011.02.14 #2279 
                " and a.FCuryCode = e.FCuryCode3 " +
               //--------------------
                (analy1 ? " and e.FAnalysisCode13=a.FAnalysisCode1 " : " ") +
                (analy2 ? " and e.Fanalysiscode23=a.FAnalysisCode2 " : " ") +
                (analy3 ? " and e.FanalysisCode33=a.FAnalysisCode3 " : " ") +
                " and e.FTsfTypeCode3=a.FTsfTypeCode and e.FSubTsfTypeCode3=a.FSubTsfTypeCode ";
            //start----panjunfang add 20090907 MS00017 QDV4.1赢时胜（上海）2009年4月20日17_A
            //运营应收应付收支款表中的支出'16'与运营应收应付收支库存表中的'06'应收款项对应，用'16'冲减'06'，即待摊费用计提后相应的运营应收款应减少
            //解决待摊在运营收支库存中应收应付的显示问题，自此待摊运营收支库存余额即为应收款余额
            strSql = strSql + " left join ( select FTransDate as FTransDate4, FPortCode as FPortCode4, FIVPayCatCode as FIVPayCatCode4, FTsfTypeCode as FTsfTypeCode4, " +
                dbl.sqlIsNull("FSubTsfTypeCode", "' '") +
                " as FSubTsfTypeCode4, FCuryCode as FCuryCode4, " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " nvl(fattrclscode,' ') as fattrclscode4, " +  
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                (analy1 ? (dbl.sqlIsNull("FAnalysisCode1", "' '") +
                           " as FAnalysisCode14,") : " ") +
                (analy2 ? (dbl.sqlIsNull("FAnalysisCode2", "' '") +
                           " as FAnalysisCode24, ") : " ") +
                (analy3 ? (dbl.sqlIsNull("FAnalysisCode3", "' '") +
                           " as FAnalysisCode34,") : " ") +
                " sum(-FMoney) as FMoney4, " +
                " sum(-FBaseCuryMoney) as FBaseCuryMoney4," +
                " sum(-FPortCuryMoney) as FPortCuryMoney4 from " +
                "(select FTransDate,FPortCode,FIVPayCatCode, FCuryCode," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " fattrclscode, " +  
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                (analy1 ? "FAnalysisCode1," : " ") +
                (analy2 ? "FAnalysisCode2," : " ") +
                (analy3 ? "FAnalysisCode3," : " ") +
                "(case when FTsfTypeCode = '16' then '06' end) as FTsfTypeCode," +
                "(case when FSubTsfTypeCode = '16IV' then '06IV' end) as FSubTsfTypeCode," +
                "FMoney,FBaseCuryMoney,FPortCuryMoney,FCheckState from " +
                pub.yssGetTableName("Tb_Data_InvestPayRec") +
                "  where FTsfTypeCode in ('16'))" +
                " where FTransDate =" + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes + ")" +
                " and FCheckState = 1 " +
                " group by FTransDate, FPortCode, FIVPayCatCode" +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " ,fattrclscode " +  
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                (analy1 ? ", FAnalysisCode1" : " ") +
                (analy2 ? ", FAnalysisCode2" : " ") +
                (analy3 ? ", FAnalysisCode3" : " ") +
                ", FTsfTypeCode, FSubTsfTypeCode, FCuryCode " +
                " ) p on b.FPortCode2=p.FPortCode4 and b.FIVPayCatCode2=p.FIVPayCatCode4 " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---// 
                " and b.fattrclscode2 = p.fattrclscode4 " +  
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                // add by fangjiang 2011.02.14 #2279 
                " and b.FCuryCode2 = p.FCuryCode4 " +
               //--------------------
                (analy1 ? " and b.FAnalysisCode12=p.FAnalysisCode14 " : " ") +
                (analy2 ? " and b.Fanalysiscode22=p.FAnalysisCode24 " : " ") +//调整为 p.FAnalysisCode24 合并版本时调整
                (analy3 ? " and b.FanalysisCode32=p.FAnalysisCode34 " : " ") +//调整为 p.FAnalysisCode34 合并版本时调整
                " and b.FTsfTypeCode2=p.FTsfTypeCode4 and b.FSubTsfTypeCode2=p.FSubTsfTypeCode4 ";
            //end---------panjunfang add 20090907

            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                investrecpaybal = new InvestPayRecBean();
                investrecpaybal.setDtStorageDate(rs.getDate("FOperDate"));
                // sYearMonth = YssFun.formatDate(rs.getDate("FOperDate"), "yyyyMM");
                if (rs.getDate("FTransDate") != null &&
                    rs.getDate("FStorageDate") == null) {
                    investrecpaybal.setSPortCode(rs.getString("FPortCode"));
                    investrecpaybal.setSIvPayCatCode(rs.getString("FIVPayCatCode"));
                    investrecpaybal.setAttrClsCode(rs.getString("Fattrclscode"));
                    investrecpaybal.setSAnalysisCode1(analy1 ? rs.getString(
                        "FAnalysisCode1") : " ");
                    investrecpaybal.setSAnalysisCode2(analy2 ? rs.getString(
                        "FAnalysisCode2") : " ");
                    investrecpaybal.setSAnalysisCode3(analy3 ? rs.getString(
                        "FAnalysisCode3") : " ");
                    investrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode"));
                    investrecpaybal.setSSubTsfTypeCode(rs.getString(
                        "FSubTsfTypeCode"));
                    investrecpaybal.setSCuryCode(rs.getString("FCuryCode"));
                } else {
                    investrecpaybal.setSPortCode(rs.getString("FPortCode2"));
                    investrecpaybal.setAttrClsCode(rs.getString("Fattrclscode2"));
                    investrecpaybal.setSIvPayCatCode(rs.getString(
                        "FIVPayCatCode2"));
                    investrecpaybal.setSAnalysisCode1(analy1 ? rs.getString(
                        "FAnalysisCode12") : " ");
                    investrecpaybal.setSAnalysisCode2(analy2 ? rs.getString(
                        "FAnalysisCode22") : " ");
                    investrecpaybal.setSAnalysisCode3(analy3 ? rs.getString(
                        "FAnalysisCode32") : " ");
                    investrecpaybal.setSTsfTypeCode(rs.getString("FTsfTypeCode2"));
                    investrecpaybal.setSSubTsfTypeCode(rs.getString(
                        "FSubTsfTypeCode2"));
                    investrecpaybal.setSCuryCode(rs.getString("FCuryCode2"));
                }
                //MS01701 QDV4太平2010年09月03日01_AB 调整太平对应收应付库存采用累计法统计算法,主流版本还是采用冲减算法 by leeyu 20100909
                if(!bTPVer){
                	investrecpaybal.setDBal(
                			YssD.add(YssD.add(rs.getDouble("FMoney"),
                					rs.getDouble("FMoney3")),
                					rs.getDouble("FMoney4"),//panjunfang add 20090907
                					rs.getDouble("FBal")));
                	investrecpaybal.setDBaseBal(YssD.add(YssD.add(rs.getDouble(
                    	"FBaseCuryMoney"),
                    	rs.getDouble("FBaseCuryMoney3")),
                    	rs.getDouble("FBaseCuryMoney4"),//panjunfang add 20090907
                    	rs.getDouble("FBaseCuryBal")));
                	investrecpaybal.setDPortBal(
                			YssD.add(YssD.add(rs.getDouble("FPortCuryMoney"),
                					rs.getDouble("FPortCuryMoney3")),
                					rs.getDouble("FPortCuryMoney4"),//panjunfang add 20090907
                					rs.getDouble("FPortCuryBal"))); //20071113，杨文奇
                }else{
                	investrecpaybal.setDBal(
                			YssD.add(rs.getDouble("FMoney"),                					
                					rs.getDouble("FBal")));
                	investrecpaybal.setDBaseBal(YssD.add(rs.getDouble(
                    	"FBaseCuryMoney"),                    	
                    	rs.getDouble("FBaseCuryBal")));
                	investrecpaybal.setDPortBal(
                			YssD.add(rs.getDouble("FPortCuryMoney"),                					
                					rs.getDouble("FPortCuryBal")));
                }
                //MS01701 QDV4太平2010年09月03日01_AB by leeyu 20100909
                all.add(investrecpaybal);
            }
            dbl.closeResultSetFinal(rs);
//         }
            return all;
        } catch (Exception ex) {
            throw new YssException("系统统计运营应收应付库存时出现异常!\n", ex); //by 曹丞 2009.01.22 统计运营应收应付库存异常 MS00004 QDV4.1-2009.2.1_09A
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
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Investpayrec") +
                " where FYearMonth = " + dbl.sqlString(YearMonth) +
                " and FPortCode in( " + operSql.sqlCodes(portCode) + ")"; //添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的 by leeyu 20090220 QDV4华夏2009年2月13日01_B MS00246;
            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_Investpayrec") +
                "(FIVPAYCATCODE,FYearMonth,FStorageDate,FPORTCODE," +
                "FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3," +
                "FTSFTYPECODE,FSUBTSFTYPECODE," +
                "FCURYCODE,FBAL,FBASECURYBAL," +
                "FPORTCURYBAL,FSTORAGEIND,FCheckState," +
                "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)" +
                "(select FIVPAYCATCODE," + dbl.sqlString(YearMonth) +
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
                " from " + pub.yssGetTableName("Tb_Stock_Investpayrec") +
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

}
