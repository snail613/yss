package com.yss.main.operdeal.stgstat;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.storagemanage.TAStorageBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class StgTAStorage
    extends BaseStgStatDeal {
    public StgTAStorage() {
    }

    /**
     * getStorageStatData
     *获得TA库存的数据 插入到TA库存表
     * @return ArrayList
     */
    public ArrayList getStorageStatData(java.util.Date dOperDate) throws
        YssException {
        String strSql = "", strTmpSql = "", strTmpSql1 = "", strTmpSql2 = "";
        ResultSet rs = null;
        TAStorageBean tastorage = null;
        String sPortCuryCode = "", sExRateSrcCode = "", sTradeCuryCode = "",
            sExRateCode = "";
        double cost = 0; //  成本(要减去损益平准金 和 未实现损益平准金)
        double baseCost = 0;
        double portCost = 0;
        ArrayList all = new ArrayList();
        String strError = "统计TA库存出错";

        String analy1; //判断是否需要用分析代码
        String analy2;

        try {
            if (bYearChange) {
                yearChange(dOperDate, portCodes);
            }

            boolean Tanaly1 = operSql.storageAnalysis("FAnalysisCode1",
                YssOperCons.YSS_KCLX_TA); //判断分析代码存不存在
            boolean Tanaly2 = operSql.storageAnalysis("FAnalysisCode2",
                YssOperCons.YSS_KCLX_TA);
            if (Tanaly1) {
                // modify by wangzuochun MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 
                //analy1 = "true";  
            	analy1 = "";
            	//-------MS00023------//
            } else {
                analy1 = "";
            }
            if (Tanaly2) {
            	// modify by wangzuochun MS00023 国内TA业务 QDV4.1赢时胜（上海）2009年4月20日23_A 
                //analy2 = "true";
            	analy2 = "";
            	//-------MS00023------//
            } else {
                analy2 = "";
            }
            /*analy1 = this.getSettingOper().getStorageAnalysisField(
                  YssOperCons.YSS_KCLX_TA, YssOperCons.YSS_KCPZ_InvMgr);
                     analy2 = this.getSettingOper().getStorageAnalysisField(
                  YssOperCons.YSS_KCLX_TA, YssOperCons.YSS_KCPZ_Exchange);*/

            if (this.portCodes.length() > 0) {
                strTmpSql = " and FPortCode in (" + this.operSql.sqlCodes(portCodes) + ")";//xuqiji 20091015 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                strTmpSql2 = " in (" + portCodes + ")";
            } else {
            	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
         
                    
                    strTmpSql = " and FPortCode in ( select FPortCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where  FCheckState = 1 and Fassetgroupcode='" +
                    pub.getAssetGroupCode() +
                    "' ) ";
                //end by lidaolong
            }

            if (YssFun.getMonth(dOperDate) == 1 &&
                YssFun.getDay(dOperDate) == 1) {
                strTmpSql1 = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dOperDate, "yyyy") + "00'";
            } else {
                strTmpSql1 = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dOperDate, "yyyy") + "00'" +
                    " and FStorageDate = " +
                    dbl.sqlDate(YssFun.addDay(dOperDate, -1));
            }

            strSql = "select  xx.*, yy.*, m.FPortCuryStorage, n.FTdPortCury," +
                dbl.sqlDate(dOperDate) +
                " as FOperDate from (Select  b.FPortCode as FPortCodeTrade" +
                (!analy1.equals("") ? ", b.FAnalysisCode1" : " ") +
                (!analy2.equals("") ? ", b.FAnalysisCode2" : " ") +
                " ,b.FCuryCode as FCuryCode" +
                " ,b.FConfimDate as FConfimDate" +
                ",b.FPortClsCode as FPortClsCodeTrade" +
                //" ,b.FSellNetCode as FSellNetCode " +
                /**shashijie 2011-10-31 STORY 1589 */
                ", Sum(" +
                dbl.sqlIsNull("b.FPortDegree", "0") +
                ") as FPortDegree "+//本位币份额
                /**end*/
                ", Sum(" +
                dbl.sqlIsNull("b.FSellAmount", "0") +
                ") as FSellAmount"+
                ",Sum("+dbl.sqlIsNull("b.fconvertnum","0") +")as fconvertnum"+//xuqiji 20091015 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                ", Sum(" +
                dbl.sqlIsNull("b.FSellMoney", "0") +
                ") as FSellMoney, sum(" +
                //成本的方向和数量的方向一样，所以这里乘的是数量方向
                dbl.sqlIsNull("b.FBaseSellMoney", "0") +
                ") as FBaseSellMoney,sum(" +
                dbl.sqlIsNull("b.FPortSellMoney", "0") + ") as FPortSellMoney," +
                "sum(" + dbl.sqlIsNull("b.FSellNotBal", "0") +
                ") as FSellNotBal,sum(" +
                dbl.sqlIsNull("b.FBaseSellNotBal", "0") +
                ") as FBaseSellNotBal,sum(" +
                dbl.sqlIsNull("b.FPortSellNotBal", "0") +
                ") as FPortSellNotBal, sum(" +
                dbl.sqlIsNull("b.FSellBal", "0") +
                ") as FSellBal,sum(" +
                dbl.sqlIsNull("b.FBaseSellBal", "0") +
                ") as FBaseSellBal,sum(" +
                dbl.sqlIsNull("b.FPortSellBal", "0") +
                ") as FPortSellBal," +
                "sum("+ dbl.sqlIsNull("b.FPaidInMOney", "0")+") as FPaidInMOney,"+
                "sum("+ dbl.sqlIsNull("b.FBasePaidInMOney", "0")+") as FBasePaidInMOney,"+
                "sum("+ dbl.sqlIsNull("b.FPortPaidInMOney", "0")+") as FPortPaidInMOney"+
                " from (" +
                //-------------------------------------------------获取TA交易数据表数据----------------------------------------------------------
                "select b1.fportcode,b1.fselltype,b1.fconfimdate,b1.fcurycode,b1.fanalysiscode1,b1.fanalysiscode2," +
                " b1.fanalysiscode3,b1.FPortClsCode, b2.FCashInd, b2.FAmountInd," +
                /**shashijie 2011-10-31 STORY 1589 */
                " b1.FPortdegree * FAmountInd as FPortdegree, "+//本位币份额
                /**end*/
                "b1.FSellAmount * FAmountInd as FSellAmount,b1.FSellMoney * FCashInd as FSellMoney," +
                //xuqiji 20091015 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                "b1.FSellMoney * FCashInd * FBaseCuryRate as FBaseSellMoney,b1.fconvertnum," +
                "case when b1.FPortCuryRate = 0 then 0 else b1.FSellMoney * b2.FCashInd * b1.FBaseCuryRate / " +
                dbl.sqlIsNull("b1.FPortCuryRate", "1") +
                " end as FPortSellMoney," +
                
                //20121102 modified by liubo.
                //TA交易数据中的已实现损益平准金、未实现损益平准金是已经被换算成了本位币金额
                //算原币时，要用本位币*组合汇率/基础汇率，得到原币金额。
                //算基础货币时，用本位币*组合汇率得到基础货币金额
                //======================================
//                " b1.FIncomeNotBal * FCashInd as FSellNotBal, b1.FIncomeNotBal * FCashInd * b1.FBaseCuryRate as FBaseSellNotBal," +
                " case when b1.FPortCuryRate = 0 then 0 else b1.FIncomeNotBal * b2.FCashInd / b1.FBaseCuryRate * NVL(b1.FPortCuryRate, 1) end as FSellNotBal," +
                " b1.FIncomeNotBal * FCashInd *  NVL(b1.FPortCuryRate, 1) as FBaseSellNotBal," + 
                //================end======================

                //20121102 modified by liubo.
                //TA交易数据中的已实现损益平准金、未实现损益平准金是已经被换算成了本位币金额
                //======================================
//                "case when b1.FPortCuryRate = 0 then 0 else b1.FIncomeNotBal * FCashInd * FBaseCuryRate /" +
//                dbl.sqlIsNull("b1.FPortCuryRate", "1") + " end " +
                " b1.FIncomeNotBal * FCashInd " +
                //================end======================
                " as FPortSellNotBal," +
                //20121102 modified by liubo.
                //TA交易数据中的已实现损益平准金、未实现损益平准金是已经被换算成了本位币金额
                //算原币时，要用本位币*组合汇率/基础汇率，得到原币金额。
                //算基础货币时，用本位币*组合汇率得到基础货币金额
                //======================================
//                "b1.FIncomeBal * FCashInd as FSellBal, b1.FIncomeBal * FCashInd * FBaseCuryRate as FBaseSellBal," +
                " case when b1.FPortCuryRate = 0 then 0 else b1.FIncomeBal * b2.FCashInd / b1.FBaseCuryRate * NVL(b1.FPortCuryRate, 1) end as FSellBal," +
                " b1.FIncomeBal * FCashInd *  NVL(b1.FPortCuryRate, 1) as FBaseSellBal," +
                //================end======================

                //20121102 modified by liubo.
                //TA交易数据中的已实现损益平准金、未实现损益平准金是已经被换算成了本位币金额
                //======================================
//                "case when b1.FPortCuryRate = 0 then 0 else b1.FIncomeBal * FCashInd * FBaseCuryRate /" +
//                dbl.sqlIsNull("b1.FPortCuryRate", "1") + " end " +
                " b1.FIncomeBal * FCashInd  " +
                //================end======================
                
                " as FPortSellBal,"+
                //story 2683 add by zhouwei 20120611 新增字段实收基金金额的统计
                
                /**Start 20130917 modified by liubo.Bug #9363.QDV4建行2013年09月03日02_B
                 * TA交易数据中的实收基金金额，已经被转换成了本位币
                 * 因此在统计库存时，应该用FPaidInMOney*组合汇率得到基础货币金额，用FPaidInMOney*组合汇率/基础汇率得到原币金额*/
//                "b1.FPaidInMOney * FAmountInd as FPaidInMOney,"+
//                "b1.FPaidInMOney * FAmountInd * FBaseCuryRate as FBasePaidInMOney,"+
//                "case when b1.FPortCuryRate = 0 then 0 else b1.FPaidInMOney * FAmountInd * FBaseCuryRate /"+
//                dbl.sqlIsNull("b1.FPortCuryRate", "1") +
//                " end as FPortPaidInMOney"
                /**Start 20130917 modified by liubo.Bug #9363.QDV4建行2013年09月03日02_B*/
                
                /**Start 20131010 modified by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001
                 * 根据通参“分级本币实收基金”设置的值进行计算，若选择了"申购时原币折算，赎回时移动加权"
                 * 实收基金的算法变为：原币实收基金=销售数量*｛TA份额成本中维护的分级份额成本｝
										本币实收基金，发生额即TA交易数据中的Fpaidinmoney。
                  	若选则的是“默认算法”，则按原先的计算方法进行*/
                (!getPaidInCalcType() ? 
	                " b1.FPaidInMOney * FAmountInd as FPortPaidInMOney," +
	                " b1.FPaidInMOney * FAmountInd * NVL(b1.FPortCuryRate, 1) as FBasePaidInMOney," +
	                " (case when b1.FPortCuryRate = 0 then 0 " +
	                " else b1.FPaidInMOney * FAmountInd * NVL(b1.FPortCuryRate, 1)/FBaseCuryRate  end) as FPaidInMOney"
                :
	                " b1.FPaidInMOney * FAmountInd * " +
	                " NVL(b1.FPortCuryRate, 1) as FBasePaidInMOney, " +
	                " b1.fpaidinmoney * FAmountInd as FPortPaidInMOney, " +
	                " b1.fsellamount * nvl(portcls.FDEGREECOST,1) * FAmountInd as fpaidinmoney "
                )
                
                +" from " +
                pub.yssGetTableName("Tb_TA_Trade") +
                " b1 " +
                " left join (select a.fportcode,a.fportclscode,a.fdegreecost from " + pub.yssGetTableName("tb_ta_classfunddegree") + " a " +
                " right join (select FportCode,FPortClsCode,max(FStartDate) as FStartDate " +
                " from " + pub.yssGetTableName("tb_ta_classfunddegree") + " where FStartDate <= " + dbl.sqlDate(dOperDate) +
                " group by FPortClsCode,FportCode) b " +
                " on a.fportcode = b.fportcode and a.fportclscode = b.fportclscode and a.FStartDate = b.FStartDate) portcls " +
                " on b1.fportclscode = portcls.FPortClsCode and b1.fportcode = portcls.FPortCode " +
                
                /**End 20131010 modified by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001*/
                
                //modify by jsc 20120724 分红转投损益平准金计算有问题。 因为上面的计算损益平准金是按照 FIncomeBal * FCashInd来统计的；
                //而【分红转投】的销售类型设置是 【资金：(0)无		数量: (1)流入】
                " join (select case when FSellTypeCode='08' then 1 else FCashInd end as FCashInd,FAmountInd,FSellTypeCode from " +
                pub.yssGetTableName("Tb_TA_SellType") +
                " where FCheckState = 1) b2 " +
                " on b1.FSellType = b2.FSellTypeCode and b1.fsellamount <> 0 and b1.fcheckstate = 1) b where b.FConfimDate = " +
                dbl.sqlDate(dOperDate) +
                strTmpSql +
                //如果为ETF库存统计，则不统计当日的交易数据 MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A
                //易方达、华夏ETF T+1确认申赎，因此要统计当日的交易数据 
                //panjunfang modify 20110814 STORY #1434 QDV4易方达基金2011年7月27日01_A
                //STORY #1789 QDV4中行2011年10月25日01_A
                (super.isBETFStat() && 
                		!YssOperCons.YSS_ETF_MAKEUP_ONE.equals(super.getStrETFStatType()) &&
                		!YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(super.getStrETFStatType()) &&
                		!YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE.equals(super.getStrETFStatType())? " and 1=2 " : " ") + 
                " group by b.FPortClsCode, b.FPortCode" +
                (!analy1.equals("") ? ", b.FAnalysisCode1" : " ") +
                (!analy2.equals("") ? ", b.FAnalysisCode2" : " ") +
                ", b.FConfimDate,b.FCuryCode) yy ";

            strSql = strSql +
                " full join ( select a.FYearMonth," + // a.FSellNetCode as FSellNetCodeStorage," +  //不要按照网点统计TA库存,如果是手工维护的，那么经常都是维护一笔记录的。fazmm20071030
                " a.FStorageDate, a.FPortCode as FPortCodeStorage,  a.FPortClsCode as FPortClsCodeStorage, a.FCuryCode as FTradeCury,  " +
                /**shashijie 2011-10-31 STORY 1589 */
                "sum(" + dbl.sqlIsNull("a.FPortStorageAmount", "0") +
                ") as FPortStorageAmount , " +//本位币库存数量
                /**end*/
                "sum(" + dbl.sqlIsNull("a.FStorageAmount", "0") +
                ") as FStorageAmount , " +
                "sum("+dbl.sqlIsNull("a.fconvertnum","0")+")as fSTconvertnum,"+//xuqiji 20091015 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                "sum(" + dbl.sqlIsNull("a.FCost", "0") +
                ") as FStorageCost , " +

                "sum(" + dbl.sqlIsNull("a.FBaseCuryCost", "0") +
                ") as FBaseCuryCost, " +

                "sum(" + dbl.sqlIsNull("a.FPortCuryCost", "0") +
                ") as FPortCuryCost, " +
                "sum(" + dbl.sqlIsNull("a.fcuryunpl", "0") +
                ") as FCuryUnPl," +
                "sum(" + dbl.sqlIsNull("a.fbasecuryunpl", "0") +
                ") as FBaseCuryUnPl," +
                "sum(" + dbl.sqlIsNull("a.fportcuryunpl", "0") +
                ") as FPortCuryUnPl," +
                "sum(" + dbl.sqlIsNull("a.FCuryPl", "0") +
                ") as FCuryPl," +
                "sum(" + dbl.sqlIsNull("a.fbasecurypl", "0") +
                ") as FBaseCuryPl," +
                "sum(" + dbl.sqlIsNull("a.fportcurypl", "0") +
                ") as FPortCuryPl," +
                "a.FPortCuryRate as FPortCuryRateStorage, a.FBaseCuryRate as FBaseCuryRateStorage " +
                (!analy1.equals("") ?
                 ("," + dbl.sqlIsNull("a.FAnalysisCode1", "' '") +
                  " as FAnalysisCodestock1 ") : " ") +
                (!analy2.equals("") ?
                 ("," + dbl.sqlIsNull("a.FAnalysisCode2", "' '") +
                  " as FAnalysisCodestock2 ") : " ") +

                " from " +
                pub.yssGetTableName("Tb_Stock_TA") +
                " a where FCheckState = 1 " + strTmpSql1 +
                " group by FYearMonth,FStorageDate,FPortCode,FPortClsCode,FCuryCode," +
                "FPortCuryRate,FBaseCuryRate" +
                (!analy1.equals("") ? ",FAnalysisCode1" : " ") +
                (!analy2.equals("") ? ",FAnalysisCode2" : " ") +
                ") xx on xx.FPortCodeStorage=yy.FPortCodeTrade" +
                " and xx.FPortClsCodeStorage=yy.FPortClsCodeTrade" +
                (!analy1.equals("") ?
                 " and xx.FAnalysisCodeStock1 = yy.FAnalysisCode1 " : " ") +
                (!analy2.equals("") ?
                 " and xx.FAnalysisCodeStock2 = yy.FAnalysisCode2 " : " ") +
                " and " +
                dbl.sqlDateAdd("xx.FStorageDate", "+1") +
                " = yy.FConfimDate and xx.FTradeCury = yy.FCuryCode ";
            strSql = strSql +
                " left join (select mb.* from (select FPortCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FStartDate <= " + dbl.sqlDate(dOperDate) +
                " and FCheckState = 1 group by FPortCode) ma join (select FPortCode, FPortName, FStartDate," +
                " FPortCury as FPortCuryStorage from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState =1 " + //xuqiji 20100409 MS01073 QDV4华夏2010年4月07日01_B 当日的政权持仓和TA持仓重复 
                ") mb on ma.FPortCode = mb.FPortCode and ma.FStartDate = mb.FStartDate) m on  xx.FPortCodeStorage = m.FPortCode" +
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
  
                " left join (select FPortCode, FPortName,FPortCury as FTdPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) n on yy.FPortCodeTrade = n.FPortCode";
            
            //end by lidaolong
            //rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788 9i下解析有问题
            rs = dbl.openResultSet(strSql); //modify by fangjiang 2011.12.09 bug 3342
            while (rs.next()) {
                tastorage = new TAStorageBean();
                tastorage.setStorageDate(dOperDate);
                tastorage.setAnalysisCode1(" ");
                tastorage.setAnalysisCode2(" ");
                tastorage.setAnalysisCode3(" ");
                //sTradeCuryCode = rs.getString("FTradeCury");	//add by huangqirong 2012-12-01 bug #6495 重新获取汇率
                //tastorage.setCuryCode(rs.getString("FTradeCury"));	//add by huangqirong 2012-12-01 bug #6495 重新获取汇率
//                tastorage.setBaseCuryRate(this.getSettingOper().getCuryRate(rs.getDate(
//                            "FOperDate"), sTradeCuryCode, tastorage.getPortCode(),
//                            YssOperCons.YSS_RATE_BASE));
//                
//                tastorage.setPortCuryRate(this.getSettingOper().getCuryRate(rs.getDate(
//			                "FOperDate"), sPortCuryCode, tastorage.getPortCode(),
//			                YssOperCons.YSS_RATE_PORT));  
                
                /**Start 20131009 modified by liubo.Bug #80677.QDV4海富通2013年09月30日01_B
                 * TA库存本位币成本现在保留的是四位小数，应该保留两位。
				        备注：财务系统保留的是两位，估值系统保留四位，有时会有一分钱的尾差。*/
                
                if ( (rs.getString("FPortCodeTrade") != null) //当前一天没有库存 而当天有交易
                    && rs.getString("FPortCodeStorage") == null) {
                    //tastorage.setSellNetCode(rs.getString(
                    //      "FSellNetCode"));
                    tastorage.setPortClsCode(rs.getString("FPortClsCodeTrade"));
                    tastorage.setPortCode(rs.getString("FPortCodeTrade"));
                    tastorage.setCuryCode(rs.getString("FCuryCode"));
                    sTradeCuryCode = rs.getString("FCuryCode"); //给sTradeCuryCode赋值，之前这个没赋值，导致基础汇率取不正确，杨文奇1204
                    // sPortCuryCode = rs.getString("FTdPortCury");
                    tastorage.setStorageAmount(rs.getDouble(
                        "FSellAmount"));
                    tastorage.setConvertMoney(rs.getDouble("fconvertnum"));//xuqiji 20091015 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                    //TA库存的成本，不包含未实现损益平准金和已实现损益平准金chenjia20070930
                    //story 2683 modify by  zhouwei 20120611 如果实收基金金额不为0，成本=昨日成本+实收基金金额
                    if(rs.getDouble("FPaidInMOney")!=0){
                    	 cost = rs.getDouble("FPaidInMOney");
                     tastorage.setCost(YssFun.roundIt(cost, 2));
                     baseCost = rs.getDouble("FBasePaidInMOney");
                     tastorage.setBaseCuryCost(YssFun.roundIt(baseCost,2));
                     portCost = rs.getDouble("FPortPaidInMOney") ;
                    }else{
                    //modify huangqirong bug #6495 2012-12-05  解开某些已注释的，注释某些需要注释的 
                    //20120911 modified by liubo.Bug #5589
                    //原算法在计算TA成本发生额时，计算赎回的发生额会遗漏掉赎回费，导致计算错误
                    //再次直接取销售数量
                    //同时直接以原币*基础汇率/组合汇率计算出本位币，以避免之前的累加算法计算本位币，漏掉赎回费而造成的原币本位币计算不平
                    //======================================
                     cost = rs.getDouble("FSellMoney") - rs.getDouble("FSellNotBal") - rs.getDouble("FSellBal");
                     //cost = rs.getDouble("FSellAmount");
                     tastorage.setCost(YssFun.roundIt(cost, 2));
                     baseCost = rs.getDouble(
                         "FBaseSellMoney") - rs.getDouble("FBaseSellNotBal") -
                         rs.getDouble("FBaseSellBal");
                     tastorage.setBaseCuryCost(YssFun.roundIt(baseCost,2));
                     portCost = rs.getDouble(
                         "FPortSellMoney") - rs.getDouble("FPortSellNotBal") -
                         rs.getDouble("FPortSellBal");
                     
                     //portCost = YssD.div(YssD.mul(cost,tastorage.getBaseCuryRate()), tastorage.getPortCuryRate());
                     //tastorage.setPortCuryCost(portCost);
                     //=================end=====================
                    }        
                    //---end--------------
                    //---end---
                    tastorage.setPortCuryCost(YssFun.roundIt(portCost,2));
                    tastorage.setCuryUnPl(rs.getDouble("FSellNotBal"));
                    tastorage.setBaseCuryUnpl(rs.getDouble("FBaseSellNotBal"));
                    tastorage.setPortCuryUnpl(rs.getDouble("FPortSellNotBal"));
                    tastorage.setCuryPl(rs.getDouble("FSellBal"));
                    tastorage.setBaseCuryPl(rs.getDouble("FBaseSellBal"));
                    tastorage.setPortCuryPl(rs.getDouble("FPortSellBal"));
                    if (!analy1.equals("")) {
                        if (rs.getString("FAnalysisCode1") != null &&
                            rs.getString("FAnalysisCode1").trim().length() != 0) {
                            tastorage.setAnalysisCode1(rs.getString(
                                "FAnalysisCode1"));
                        }
                    }
                    if (!analy2.equals("")) {
                        if (rs.getString("FAnalysisCode2") != null &&
                            rs.getString("FAnalysisCode2").trim().length() != 0) {
                            tastorage.setAnalysisCode2(rs.getString(
                                "FAnalysisCode2"));
                        }
                    }
                    /**shashijie 2011-10-31 STORY 1589 */
                    tastorage.setFPortStorageAmount(rs.getDouble("FPortdegree"));//本位币库存数量
                    /**end*/
                } else if ( (rs.getString("FPortCodeTrade") == null) //当前一天有库存 而没交易数据
                           && rs.getString("FPortCodeStorage") != null) {
                    //tastorage.setSellNetCode(rs.getString(
                    //      "FSellNetCodeStorage"));
                    tastorage.setPortClsCode(rs.getString("FPortClsCodeStorage"));
                    tastorage.setPortCode(rs.getString(
                        "FPortCodeStorage"));
                    tastorage.setCuryCode(rs.getString("FTradeCury"));//#1813 QDV4嘉实2011年04月26日01_B :: 库存统计时，报插入TA库存重复，违反唯一约束  panjunfang modify 20110426
                    sTradeCuryCode = rs.getString("FTradeCury");//#1813  库存统计时，报插入TA库存重复，违反唯一约束  panjunfang modify 20110426 取前一天库存币种，不能取组合货币
                    tastorage.setStorageAmount(rs.getDouble(
                        "FStorageAmount"));
                    tastorage.setConvertMoney(rs.getDouble("fSTconvertnum"));//xuqiji 20091015 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                    //TA库存的成本，不包含未实现损益平准金和已实现损益平准金chenjia20070930
                    //modify huangqirong bug #6495 2012-12-05  解开某些已注释的，注释某些需要注释的 
                    //20120911 modified by liubo.Bug #5589
                    //原算法在计算TA成本发生额时，计算赎回的发生额会遗漏掉赎回费，导致计算错误
                    //再次直接取销售数量
                    //同时直接以原币*基础汇率/组合汇率计算出本位币，以避免之前的累加算法计算本位币，漏掉赎回费而造成的原币本位币计算不平
                    //***************************************
                    cost = rs.getDouble("FStorageCost"); //- rs.getDouble("FCuryUnPl") - rs.getDouble("FCuryPl");
                    //cost = (rs.getDouble("FStorageCost") == rs.getDouble("FStorageAmount") ? rs.getDouble("FStorageCost") : rs.getDouble("FStorageAmount"));
                    
                    tastorage.setCost(YssFun.roundIt(cost, 2));
                    baseCost = rs.getDouble("FBaseCuryCost"); // -rs.getDouble("FBaseCuryUnPl") - rs.getDouble("FBaseCuryPl");
                    tastorage.setBaseCuryCost(YssFun.roundIt(baseCost,2));
                    portCost = rs.getDouble("FPortCuryCost"); // - rs.getDouble("FPortCuryUnPl") - rs.getDouble("FPortCuryPl");
                    tastorage.setPortCuryCost(YssFun.roundIt(portCost, 2));
					
                    //portCost = YssD.div(YssD.mul(cost,tastorage.getBaseCuryRate()), tastorage.getPortCuryRate());
                    //tastorage.setPortCuryCost(portCost);
                    //******************end*********************
                    //---end---
                    
                    tastorage.setCuryUnPl(rs.getDouble("FCuryUnPl"));
                    tastorage.setBaseCuryUnpl(rs.getDouble("FBaseCuryUnPl"));
                    tastorage.setPortCuryUnpl(rs.getDouble("FPortCuryUnPl"));
                    tastorage.setCuryPl(rs.getDouble("FCuryPl"));
                    tastorage.setBaseCuryPl(rs.getDouble("FBaseCuryPl"));
                    tastorage.setPortCuryPl(rs.getDouble("FPortCuryPl"));
                    if (!analy1.equals("")) {
                        if (rs.getString("FAnalysisCodeStock1") != null &&
                            rs.getString("FAnalysisCodeStock1").trim().length() != 0) {
                            tastorage.setAnalysisCode1(rs.getString(
                                "FAnalysisCodeStock1"));
                        }
                    }

                    if (!analy2.equals("")) {
                        if (rs.getString("FAnalysisCodeStock2") != null &&
                            rs.getString("FAnalysisCodeStock2").trim().length() != 0) {
                            tastorage.setAnalysisCode2(rs.getString(
                                "FAnalysisCodeStock2"));
                        }
                    }
                    /**shashijie 2011-10-31 STORY 1589 */
                    tastorage.setFPortStorageAmount(rs.getDouble("FPortStorageAmount"));//本位币库存数量
                    /**end*/
                } else if ( (rs.getString("FPortCodeStorage") != null) //两者都有
                           && rs.getString("FPortCodeTrade") != null) {
                    //tastorage.setSellNetCode(rs.getString(
                    //      "FSellNetCodeStorage"));
                    tastorage.setPortClsCode(rs.getString("FPortClsCodeStorage"));
                    tastorage.setPortCode(rs.getString(
                        "FPortCodeStorage"));
                    tastorage.setCuryCode(rs.getString("FCuryCode"));
                    sTradeCuryCode = rs.getString("FCuryCode");
                    sPortCuryCode = rs.getString("FPortCuryStorage");

                    tastorage.setStorageAmount(
                        YssD.add(rs.getDouble("FStorageAmount"),
                                 rs.getDouble("FSellAmount")));
                    tastorage.setConvertMoney(YssD.add(rs.getDouble("fconvertnum"),rs.getDouble("fSTconvertnum")));//xuqiji 20091015 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                    //story 2683 modify by  zhouwei 20120611 如果实收基金金额不为0，成本=昨日成本+实收基金金额
                    if(rs.getDouble("FPaidInMOney")!=0){
                    	cost = YssD.add(rs.getDouble("FStorageCost"),rs.getDouble("FPaidInMOney"));
	                    tastorage.setCost(YssFun.roundIt(cost, 2));
	                    //**Start---panjunfang 2013-9-12 BUG 9363 */
	                    //成本 = 昨日成本 + 当日销售数据的实收基金金额
	                    //区分原币、基础货币、本位币分别计算，昨日成本均取FStorageCost计算肯定错了
	                    baseCost = YssD.add(rs.getDouble("FBaseCuryCost"),rs.getDouble("FBasePaidInMOney"));
	                    tastorage.setBaseCuryCost(YssFun.roundIt(baseCost,2));
	                    portCost = YssD.add(rs.getDouble("FPortCuryCost"),rs.getDouble("FPortPaidInMOney"));
	                    //**END---panjunfang 2013-9-12 BUG 9363 */
                    }else{
                    	//modify huangqirong bug #6495  2012-12-05  解开某些已注释的，注释某些需要注释的 
                        //20120911 modified by liubo.Bug #5589
                        //原算法在计算TA成本发生额时，计算赎回的发生额会遗漏掉赎回费，导致计算错误
                        //再次直接取销售数量
                        //同时直接以原币*基础汇率/组合汇率计算出本位币，以避免之前的累加算法计算本位币，漏掉赎回费而造成的原币本位币计算不平
                        //======================================
	                    cost = YssD.sub(YssD.add(rs.getDouble("FStorageCost"),
	                                             rs.getDouble("FSellMoney")),
	                                    YssD.add(rs.getDouble("FSellNotBal"),
	                                             rs.getDouble("FSellBal")));
                    	//cost = YssD.add(rs.getDouble("FStorageCost") == rs.getDouble("FStorageAmount") ? rs.getDouble("FStorageCost") : rs.getDouble("FStorageAmount"),
                    	//		rs.getDouble("FSellAmount"));
	                    //YssD.add(rs.getDouble("FCuryUnPl"),rs.getDouble("FSellNotBal"),rs.getDouble("FCuryPl"),rs.getDouble("FSellBal")));
	                    tastorage.setCost(YssFun.roundIt(cost, 2));
	
	                    baseCost = YssD.sub(YssD.add(rs.getDouble("FBaseCuryCost"),
	                                                 rs.getDouble("FBaseSellMoney")),
	                                        YssD.add(rs.getDouble("FBaseSellNotBal"),
	                                                 rs.getDouble("FBaseSellBal")));
	                    //YssD.add(rs.getDouble("FBaseCuryUnPl"),rs.getDouble("FBaseSellNotBal"),rs.getDouble("FBaseCuryPl"),rs.getDouble("FBaseSellBal")));
	                    tastorage.setBaseCuryCost(YssFun.roundIt(baseCost,2));
	                    portCost = YssD.sub(YssD.add(rs.getDouble("FPortCuryCost"),
	                                                 rs.getDouble("FPortSellMoney")),
	                                        YssD.add(rs.getDouble("FPortSellNotBal"),
	                                                 rs.getDouble("FPortSellBal")));
//                    //YssD.add(rs.getDouble("FPortCuryUnPl"),rs.getDouble("FPortSellNotBal"),rs.getDouble("FPortCuryPl"), rs.getDouble("FPortSellBal")));

                    	//===============end=====================
	                   //portCost = YssD.div(YssD.mul(cost,tastorage.getBaseCuryRate()), tastorage.getPortCuryRate());
	                   //---end---
	                     tastorage.setPortCuryCost(YssFun.roundIt(portCost, 2));
                    }
                    tastorage.setPortCuryCost(YssFun.roundIt(portCost, 2));
                    tastorage.setCuryUnPl(YssD.add(rs.getDouble("FCuryUnPl"),
                        rs.getDouble("FSellNotBal")));
                    tastorage.setBaseCuryUnpl(YssD.add(rs.getDouble("FBaseCuryUnPl"),
                        rs.getDouble(
                            "FBaseSellNotBal")));
                    tastorage.setPortCuryUnpl(YssD.add(rs.getDouble("FPortCuryUnPl"),
                        rs.getDouble(
                            "FPortSellNotBal")));

                    tastorage.setCuryPl(YssD.add(rs.getDouble("FCuryPl"),
                                                 rs.getDouble("FSellBal")));
                    tastorage.setBaseCuryPl(YssD.add(rs.getDouble("FBaseCuryPl"),
                        rs.getDouble("FBaseSellBal")));
                    tastorage.setPortCuryPl(YssD.add(rs.getDouble("FPortCuryPl"),
                        rs.getDouble("FPortSellBal")));
                    tastorage.setAnalysisCode1(" ");

                    if (!analy1.equals("")) {
                        if (rs.getString("FAnalysisCode1") != null &&
                            rs.getString("FAnalysisCode1").trim().length() != 0) {
                            tastorage.setAnalysisCode1(rs.getString(
                                "FAnalysisCode1"));
                        }
                    }

                    if (!analy2.equals("")) {
                        if (rs.getString("FAnalysisCode2") != null &&
                            rs.getString("FAnalysisCode2").trim().length() != 0) {
                            tastorage.setAnalysisCode2(rs.getString(
                                "FAnalysisCode2"));
                        }
                    }
                    /**shashijie 2011-10-31 STORY 1589 */
                    //昨日库存 + 今日交易量
                    double storageAmount = YssD.add(rs.getDouble("FPortdegree"),rs.getDouble("FPortStorageAmount"));
                    tastorage.setFPortStorageAmount(storageAmount);//本位币库存数量
                    /**end*/
                } else {
                    continue;
                }
                //modify huangqirong bug #6495 解开注释 
                //这个币种还得考虑一下  2007-09-17
                tastorage.setBaseCuryRate(this.getSettingOper().
                                          getCuryRate(rs.getDate(
                                              "FOperDate"), sTradeCuryCode, tastorage.getPortCode(),
                    YssOperCons.YSS_RATE_BASE));
                tastorage.setPortCuryRate(this.getSettingOper().
                                          getCuryRate(rs.getDate(
                                              "FOperDate"), sPortCuryCode, tastorage.getPortCode(),
                    YssOperCons.YSS_RATE_PORT)); 
                //--- end --- 
                tastorage.setClsPortStorageAmount(getClsConvertStorageAmount(tastorage.getPortCode(), tastorage.getPortClsCode(),
                		 tastorage.getStorageAmount(),dOperDate)); //将分级组合折算后的库存数量放到all中 fangjiang 2012.02.23
                all.add(tastorage);
                
                /**End 20131009 modified by liubo.Bug #80677.QDV4海富通2013年09月30日01_B*/
            }
            dbl.closeResultSetFinal(rs); 
            return all; //返回一个ArrayList 存储的是TA库存的数据
        } catch (Exception ex) {
            throw new YssException("系统统计TA库存时出现异常!\n", ex); //by 曹丞 2009.01.22 统计TA库存异常 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**story 2253 add by zhouwei 20120222 
     * 根据组合，组合分级，库存数量，日期等获取折算的库存数量
     * @param portCode
     * @param portClsCode
     * @param storageAmount
     * @param date
     * @return
     * @throws YssException
     */
    public double getClsConvertStorageAmount(String portCode,String portClsCode,double storageAmount,Date date) throws YssException{
    	ResultSet rs=null;
    	String strSql="";
    	BigDecimal basicValue=new BigDecimal("0");
    	BigDecimal curValue=new BigDecimal("0");
    	BaseOperDeal deal = new BaseOperDeal();
    	deal.setYssPub(this.pub);
    	String basicClsPort = "";
    	try{
    		//根据分类核算方式的通参找到组合对应的分类级别
    		CtlPubPara pubPara=new CtlPubPara();
    		pubPara.setYssPub(pub);
    		Map portClsWayMap=pubPara.getClassAccMethod();
    		if(portClsWayMap.get(portCode)==null){
    			return 0;
    		}
    		String portClsWay= (String) portClsWayMap.get(portCode);
    		if (portClsWay == null)
    		{
    			return 1;
    		}
    		String[] arrClsPort=portClsWay.split("\f\t");
    		if(arrClsPort.length > 1){
    			basicClsPort = arrClsPort[1].split("[|]")[0];//基准分级组合
    		}
    		if(portClsWay==null || ("").equals(portClsWay) || basicClsPort.equals("")){//没有设置分级组合
    			return 0;
    		}
    		strSql="select a.fclassnetvalue as basicValue,b.fclassnetvalue as curValue from "+pub.yssGetTableName("TB_DATA_MultiClassNet")
    			  +" a left join (select * from "+pub.yssGetTableName("TB_DATA_MultiClassNet")+" where fcheckstate=1"
    			  +" and fcurycode="+dbl.sqlString(portClsCode)+") b"
    			  +" on a.fnavdate=b.fnavdate and a.fportcode=b.fportcode and a.ftype=b.ftype "
    			  +" where a.ftype='02' and a.fcheckstate=1"//02单位净值7
    			  +" and a.fnavdate="+dbl.sqlDate(deal.getWorkDay("CH+HK", date, -1))+" and a.fportcode="+dbl.sqlString(portCode)
    			  +" and a.fcurycode="+dbl.sqlString(basicClsPort);
    		rs=dbl.openResultSet(strSql);
    		if(rs.next()){
    			if(rs.getBigDecimal("basicValue") != null){
    				basicValue = YssD.roundD(rs.getBigDecimal("basicValue"), 9);
    			}else{
    				basicValue = new BigDecimal(getUnitCostByCury(portCode, basicClsPort));
    			}
    			if(rs.getBigDecimal("curValue") != null){
    				curValue = YssD.roundD(rs.getBigDecimal("curValue"),9);
    			}else{
    				curValue = new BigDecimal(getUnitCostByCury(portCode, portClsCode));
    			}    			
    		}
    		if(basicValue.compareTo(new BigDecimal("0"))==0){
    			basicValue = new BigDecimal("1");
    		}
    		if(curValue.compareTo(new BigDecimal("0"))==0){
    			curValue = new BigDecimal("1");
    		}
    		return YssD.round(
	    				         YssD.divD
	    				         (
   				        			  YssD.mulD
		    				          (	
						        		 YssD.mulD(storageAmount, 1),
		    				        	 curValue
						        	 )	 
					        		 ,
						        	 basicValue
				        		 ),
			        		     12
			        		 );
    	}catch (Exception ex) {
			throw new YssException("获取TA折算库存出错！", ex);
		}finally{
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
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_TA") +
                " where FYearMonth = " + dbl.sqlString(YearMonth) +
                " and FPortCode in( " + operSql.sqlCodes(portCode) + ")"; //添加当前组合为条件，防止在删除期初余额时删除掉了其他组合的 by leeyu 20090220 QDV4华夏2009年2月13日01_B MS00246
            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_TA") +
                "(FPORTCLSCODE,FYearMonth,FStorageDate,FPORTCODE," +
                "FCuryCode,FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3," +
                "FCOST,FSTORAGEAMOUNT,FPORTCURYCOST,FBASECURYCOST," +
                "FPORTCURYRATE,FBASECURYRATE,FCURYUNPL,FCURYPL,FPORTCURYUNPL," +
                "FPORTCURYPL,FBASECURYUNPL,FBASECURYPL,FCHECKSTATE,FSTORAGEIND," +
                "FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME" +
                /**shashijie 2011-10-31 STORY 1589 */
                ",FPortStorageAmount " +
                ",FfjzhzsStorageAmount " +  //分级组合折算后的库存数量，该字段要加 add by fangjiang 2012.02.21
                /**end*/
                " )" +
                "(select FPORTCLSCODE," + dbl.sqlString(YearMonth) +
                " as FYearMonth, " +
                dbl.sqlDate(new Integer(Year).toString() + "-01-01") +
                " as FStorageDate,FPORTCODE," +
                "FCuryCode,FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FCOST,FSTORAGEAMOUNT," +
                "FPORTCURYCOST,FBASECURYCOST,FPORTCURYRATE,FBASECURYRATE,FCURYUNPL," +
                "FCURYPL,FPORTCURYUNPL,FPORTCURYPL,FBASECURYUNPL,FBASECURYPL," +
                "FCHECKSTATE,FSTORAGEIND," +
                dbl.sqlString(pub.getUserCode()) +
                " as FCREATOR," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " as FCREATETIME," +
                dbl.sqlString(pub.getUserName()) + " as FCHECKUSER," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " as FCHECKTIME" +
                /**shashijie 2011-10-31 STORY 1589 */
                ",FPortStorageAmount " +
                /**end*/
                ",FfjzhzsStorageAmount " +  //分级组合折算后的库存数量，该字段要加 add by fangjiang 2012.02.21
                " from " + pub.yssGetTableName("Tb_Stock_TA") +
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
    
    public double getUnitCostByCury(String portCode, String portClsCode) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		double result = 1.0;	
		try {
			strSql = " select FDegreeCost from " + pub.yssGetTableName("Tb_TA_ClassFundDegree")
					 + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode)
					 + " and FPortClsCode = " + dbl.sqlString(portClsCode);		
			rs = dbl.openResultSet(strSql);			
			while (rs.next()) {				
				result = rs.getDouble("FDegreeCost");
			}							
		}
		catch(Exception e) {
			throw new YssException("查询份额成本出错", e);
		}
		finally {
            dbl.closeResultSetFinal(rs);
        }
		return result;
	}
    

	/**
	 * 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001
	 * 获取通参“分级本币实收基金”的值，并对这个值进行判断
	 * 选定的值为1，表示默认算法，返回false
	 * 选定的值为2，表示需要按“申购时原币折算，赎回时移动加权”进行计算，返回true
	 * 通参未设置的情况下，使用默认算法
	 * @return
	 * @throws YssException
	 */
	private boolean getPaidInCalcType() throws YssException
	{
		boolean bReturn = false;
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			strSql = "select * from " + pub.yssGetTableName("tb_pfoper_pubpara") + 
					 " where fpubparacode='CtlPaidInCalcType' and FCtlCode = 'cboCalcType'";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				if (rs.getString("FCtlValue").split(",")[0].equals("2"))
				{
					bReturn = true;
				}
			}
			
		}
		catch(Exception ye)
		{
			throw new YssException();
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return bReturn;
	}

}
