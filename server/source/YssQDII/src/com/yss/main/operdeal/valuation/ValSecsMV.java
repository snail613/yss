package com.yss.main.operdeal.valuation;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.bond.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.parasetting.*;
import com.yss.pojo.param.bond.*;
import com.yss.util.*;
import com.yss.main.operdeal.datainterface.cnstock.pojo.*;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.platform.pfoper.pubpara.PubParaBean;
import java.math.BigDecimal;

/*
    获取估值证券信息的估值增值
    取数原则：
       1.获取组合的估值方法链接中存在所有证券
       2.先统计当日库存，取当日日的库存(Tb_Stock_Security)
       3.根据估值方法获取当日行情，计算估值增值
    计算方法：
        原币估值增值 ＝ 库存数量×最新行情 － 原币库存成本 － 前日估值增值余额
        基础货币估值增值 ＝ 当日计提的原币估值增值 × 当日基础汇率

 */

public class ValSecsMV
    extends BaseValDeal {

    public ValSecsMV() {
    }

    /**
     * getValuationCats
     *
     * @param mtvBeans ArrayList
     * @return HashMap
     */
    public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {

        HashMap hmResult = new HashMap();
        String strSql = "";
        MTVMethodBean vMethod = null;
        SecPecPayBean payRate = null;
        SecPecPayBean payPrice = null;
        ResultSet rs = null;
        String sKey = "", sCatCode = "";
        double dBaseRate = 1;
        double dPortRate = 1;
        double dMarketPrice = 0;
        YssBondIns bondIns = null;
        BaseBondOper bondOper = null;
        int iFactor = 1; //报价因子
        StringBuffer buf = new StringBuffer();
        HashMap hmFundMktValueDate = null;
        HashMap hmInterBankBond = null; //银行间债券以成本市值孰低法估值的组合 2009.07.21 蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
        String sFundSqlWhere = "";  //排除使用昨日行情估值的基金的Where条件
        String sExchangeCode = "";  //使用昨日行情股指的基金所在的交易所
        //-----------------------------------
        ValMktPriceBean oldMktPrice = null; //用于获取之前行情来源中获取的行情数据。
        CtlPubPara pubpara = null;
        String priMarketPrice = "";
        //-----------------------------------
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------
        //2009.08.24 蒋锦 添加 行情日期 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
        java.util.Date dMktValueDate = null;
        //add by songjie 2010.03.17 MS00911 QDV4赢时胜（测试）2010年03月16日04_B
        HashMap hmPorts = null;
        
        //add by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
        HashMap hmZQRate = null;
        DataBase dataBase = null;
        BondInterestBean bondInterest = null;
        ArrayList alZQCodes = new ArrayList();
        ArrayList alZQInfo = new ArrayList();
        try {
        	
        	
        	 //----  #1176 同一个证券不同投资类型的持仓需要支持按照不同的公允价或成本进行估值 add by jiangshichao  -----//
            PubParaBean sParaBean = new PubParaBean();
            sParaBean.setYssPub(pub);
            String sParaCost = "";//1：成本法    0：市值法  默认市值法       STORY #1176 
            String sYhjBondVal = "";//1：净价    0：全价  默认值 为NONE  STORY #1156 
            //-------------------------------------------------------------------------- add by jiangshichao  end-//
        	
            //-----------------------------通过通用参数的设置来判断停牌状态的方式。20081020 bug 0000486---------//
            //-------和行情优先级
            CtlPubPara ctlpubpara = new CtlPubPara();
            ctlpubpara.setYssPub(pub);
            boolean isACTV = ctlpubpara.getIsUseACTVInfo(portCode);
            priMarketPrice = ctlpubpara.getPriMarketPrice();
            //获取银行间债券以成本市值孰低法估值的组合 2009.07.21 蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
            //如果以后要优化速度，这东西可以放到基类里面去
            hmInterBankBond = ctlpubpara.getInterBankBond();
            //add by songjie 2010.03.17 MS00911 QDV4赢时胜（测试）2010年03月16日04_B
            hmPorts = ctlpubpara.getInterBankInsDuty();
            //-------------------------------------------------------------------------------------//
            //----2009.06.22 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》  国内基金业务
            //查找使用前一日行情估值的基金所在的交易所
            hmFundMktValueDate = ctlpubpara.getFunMarketValueDate();
            if(hmFundMktValueDate != null){
                sExchangeCode = (String)hmFundMktValueDate.get(this.portCode);
                if(sExchangeCode != null && sExchangeCode.length() > 0){
                    sFundSqlWhere = " AND NOT Exists" +
                        " (SELECT 1" +
                        " FROM " + pub.yssGetTableName("TB_Para_Security") + " exse" +
                        " WHERE tmpse.FSECURITYCODE = exse.FSECURITYCODE" +
                        " AND exse.FEXCHANGECODE IN (" + operSql.sqlCodes(sExchangeCode) + ")" +
                        " AND exse.FCATCODE = 'TR')";
                }
            }
            //----------------------------------------------------------------------------------
            for (int i = 0; i < mtvBeans.size(); i++) {
                vMethod = (MTVMethodBean) mtvBeans.get(i);

                strSql = " select cs.*,FSaleCury,FBuyCury," +
                    //--------------------------------------------------edit by jc
                    "(FBal - (case when FAppreciation is null then 0 else FAppreciation end)) as FBal," +
                    "(FMBal - (case when FMAppreciation is null then 0 else FMAppreciation end)) as FMbal," +
                    "(FVBal - (case when FVAppreciation is null then 0 else FVAppreciation end)) as FVBal," +
                    "(FBaseCuryBal - (case when FBaseAppreciation is null then 0 else FBaseAppreciation end)) as FBaseCuryBal," +
                    "(FMBaseCuryBal - (case when FMBaseAppreciation is null then 0 else FMBaseAppreciation end)) as FMBaseCuryBal," +
                    "(FVBaseCuryBal - (case when FVBaseAppreciation is null then 0 else FVBaseAppreciation end)) as FVBaseCuryBal," +
                    "(FPortCuryBal - (case when FPortAppreciation is null then 0 else FPortAppreciation end)) as FPortCuryBal," +
                    "(FMPortCuryBal - (case when FMPortAppreciation is null then 0 else FMPortAppreciation end)) as FMPortCuryBal," +
                    "(FVPortCuryBal - (case when FVPortAppreciation is null then 0 else FVPortAppreciation end)) as FVPortCuryBal," +
                    //----------------------------------------------------------jc
                    //2008.11.14 蒋锦 添加
                    //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                    //储存保留8位小数的原币，基础货币，本位币金额,用于计算估值增值汇兑损益
                    " FBalF, FBaseCuryBalF, FPortCuryBalF, smv.FSMoney, smv.FSBaseCuryMoney, smv.FSPortCuryMoney,";
                //" mk.FCsMarketPrice, mk.FMktValueDate, m.FCsPortCury from (select a.FStorageDate, a.FSecurityCode as FCsSecurityCode," +
                //=============用参数来判断行情的停牌状态 by leeyu 2008-10-20  0000486=====
                if (isACTV) {
                    strSql += " mk.FCsMarketPrice, mk.FMktValueDate,mk.FMarketStatus, m.FCsPortCury from (select a.FStorageDate, a.FSecurityCode as FCsSecurityCode,"; //增加行情状态 by leeyu 2008
                } else {
                    strSql += " mk.FCsMarketPrice, mk.FMktValueDate, m.FCsPortCury from (select a.FStorageDate, a.FSecurityCode as FCsSecurityCode,";
                }
                strSql +=
                    //=======================2008-10-20//2009.07.09 蒋锦 添加库存冻结数量 QDV4.1赢时胜（上海）2009年4月20日21_A
                    " FStorageAmount, FFreezeAmount, FStorageCost, FMStorageCost, FVStorageCost," +
                    " FBaseCuryCost as FCsBaseCuryCost, FMBaseCuryCost as FCsMBaseCuryCost, FVBaseCuryCost as FCsVBaseCuryCost," +
                    " FPortCuryCost as FCsPortCuryCost, FMPortCuryCost as FCsMPortCuryCost, FVPortCuryCost as FCsVPortCuryCost" +
                    //判断是否配置分析代码，杨
                    (this.invmgrSecField.length() != 0 ?
                     ("," + this.invmgrSecField) : " ") +
                    (this.brokerSecField.length() != 0 ?
                     ("," + this.brokerSecField) : " ") +
                    ",a.FPortCode as FCsPortCode, a.FAttrClsCode as FAttrClsCode ,sec.FTradeCury as FCsCuryCode, sec.FExchangeCode, " +
                    //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType字段
                    " sec.FCatCode as FCsCatCode, sec.FFactor as FCsFactor,FCatCode,FSubCatCode,fi.FCalcPriceMetic,fi.FBeforeFaceRate,fi.FInsStartDate,fi.FInsEndDate,fi.FInsFrequency,fi.FFaceValue,fi.FQuoteWay," +//add by jiangshichao 2011.07.21 添加报价方式字段STORY 1156
                    //--- edit by songjie 2013.06.08 BUG 8301 QDV4南方2013年06月17日01_B start---//
					//添加 nit.FLockEndDate
                    " nit.FLockBeginDate,nit.FLockEndDate, FHolidaysCode, FInvestType from " + // wdy 添加表别名a//2009.07.09 蒋锦 添加锁定起始日期 节假日代码 QDV4.1赢时胜（上海）2009年4月20日21_A
                    //--- edit by songjie 2013.06.08 BUG 8301 QDV4南方2013年06月17日01_B end---//
//                    pub.yssGetTableName("tb_stock_security") + " a" +//delete by xuxuming,20091217.此处查询出证券库存所有数据后再去关联其它表，效率极底
                    //=====更改为，将查询证券库存的条件移到此处，查询出符合条件的记录再去关联其它表,edit by xuxuming,20091217===============
                    " ( select * from "+pub.yssGetTableName("tb_stock_security")+
                    " where FCheckState = 1 and FStorageDate=" +
                    dbl.sqlDate(dDate) +
                    " and " + dbl.sqlRight("FYearMonth", "2") +
                    "<>'00' and FPortCode = " + dbl.sqlString(this.portCode) +" ) a"+
                    //==============end,xuxuming===========================================
                    //------------------------------------------------------------
                  " join (select sb.* from (select /*+first_rows(1)*/FSecurityCode, max(FStartDate) as FStartDate from " +//添加oracle提示/*+first_row(1)*/byleeyu 20100613 合并太平版本代码
                    pub.yssGetTableName("tb_para_security") +
                    " where FCheckState=1 and FStartDate<= " +
                    dbl.sqlDate(dDate) +
                    " group by FSecurityCode ) sa join (select FSecurityCode, FSecurityName, FStartDate, FCatCode, FSubCatCode, FTradeCury,FFactor,FHolidaysCode, FExchangeCode from " +//2009.07.09 蒋锦 节假日代码的查询 QDV4.1赢时胜（上海）2009年4月20日21_A
                    pub.yssGetTableName("tb_para_security") +
                    //2009-06-24 蒋锦 修改 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》  国内基金业务
                    //使用昨日行情估值的单独处理 
                    //期权单独进行资产估值 xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
                    //modify by fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A   
                    " tmpse where FCheckState=1 and (FCatCode <> 'FU' and FSubCatCode <> 'FW01' and FCatCode <> 'FP') " + //期货和远期单独估值,配股的在下面处理 by leeyu 20090307 QDV4建行2009年3月5日02_B MS00288
                    sFundSqlWhere +
                    //南方那边需注意配股权证的估值方式fazmm20071120
                    //邵宏伟，你可以先不合并这个地方
                    //" where FCheckState=1 and FSubCatCode <> 'OP02' and FSubCatCode <> 'FU01'" + //期货和配股权证单独估值
                    //
                    " )sb on sa.FSecurityCode = sb.FSecurityCode and sa.FStartDate = sb.FStartDate " +
                    ") sec on a.FSecurityCode = sec.FSecurityCode" +
                    //------------------------------------------------------------
                    " join (select FLinkCode from " +
                    pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " where FCheckState = 1 and FMtvCode=" +
                    dbl.sqlString(vMethod.getMTVCode()) +
                    ") b on a.Fsecuritycode = b.FLinkCode" +
                    //2009-07-28 蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A 增加债券的查询字段
                    " left join (select FSecurityCode,FCalcPriceMetic,FBeforeFaceRate,FInsStartDate,FInsEndDate,FInsFrequency,FFaceValue,FQuoteWay from " + pub.yssGetTableName("Tb_Para_FixInterest") +//STORY 1156  addbyjiangshichao 2011.07.21 添加债券报价方式
                    " where FCheckState = 1) fi on a.Fsecuritycode = fi.FSecurityCode" +
                    //--------2009.7.9 蒋锦 添加 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A--获取锁定日期------------//
                    //根据需求，这条SQL只用于判断证券是否有锁定期
                    //--- edit by songjie 2013.06.08 BUG 8301 QDV4南方2013年06月17日01_B start---//
					//添加 FLockEndDate
                    " LEFT JOIN (SELECT DISTINCT FLockBeginDate,FLockEndDate,FInvMgrCode, fattrclscode, fsecuritycode" +
                    //--- edit by songjie 2013.06.08 BUG 8301 QDV4南方2013年06月17日01_B end---//
                    " FROM " + pub.yssGetTableName("Tb_Data_NewIssueTrade") +
                    " WHERE FCheckState = 1" +
                    " AND FPortCode = " + dbl.sqlString(portCode) +
                    " AND FTradeTypeCode = " + dbl.sqlString(YssOperCons.YSS_JYLX_SD) +
                    " AND FLockBeginDate <= " + dbl.sqlDate(dDate) +
                    " AND FLockEndDate >= " + dbl.sqlDate(dDate) +
                    ") nit ON a.fsecuritycode = nit.fsecuritycode AND a.fattrclscode = nit.Fattrclscode" +
                    (this.invmgrSecField.length() != 0 ? " AND a.FAnalysisCode1 = nit.FInvMgrCode" : "") +
                    //--------------------------------------------------------------------------------------------//
                    /*    //delete by xuxuming,20091127.将查询条件提到前面，放在join里面
                    " where a.FCheckState = 1 and a.FStorageDate=" +
                    dbl.sqlDate(dDate) +
                    " and " + dbl.sqlRight("a.FYearMonth", "2") +
                    "<>'00' and a.FPortCode = " + dbl.sqlString(this.portCode) +
                    */
                    ") cs " +
                    //------------------------------------------------------------
                    " left join (select * from " +
                    pub.yssGetTableName("Tb_Stock_SecRecPay") +
                    " where FCheckState = 1 and " + operSql.sqlStoragEve(dDate) +
                    " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " and FTsfTypeCode = " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
                    //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType字段
                    ") rec on cs.FCSSecurityCode = rec.FSecurityCode and cs.FattrClsCode= rec.FattrClsCode and cs.FInvestType = rec.FInvestType" +
                    (this.invmgrSecField.length() != 0 ?
                     " and cs." + this.invmgrSecField + " = rec.FAnalysisCode1 " : " ") +//edit by yanghaiming 20101109 此处的分析代码不可写死
                    (this.brokerSecField.length() != 0 ?
                     " and cs." + this.brokerSecField + " = rec.FAnalysisCode2 " : " ");//edit by yanghaiming 20101109 此处的分析代码不可写死
                //------------------------------------------------------------
                //" left join ( select mk2.FCsMarketPrice, mk2.FSecurityCode, mk1.FMktValueDate from " +
                //=============用参数来判断行情的停牌状态 by leeyu 2008-10-20  0000486=====
                if (isACTV) {
             		strSql+=" left join ( select mk2.FCsMarketPrice, mk2.FSecurityCode,mk2.FMarketStatus, mk2.FMktValueDate from "; //SQL优化 合并太平版本代码 
                } else {
             		strSql+=" left join ( select mk2.FCsMarketPrice, mk2.FSecurityCode, mk2.FMktValueDate from ";//SQL优化 合并太平版本代码 
                }
                strSql +=
                    //=================2008-10-20
					//SQL优化调整 合并太平版本代码 
                    //" (select max(FMktValueDate) as FMktValueDate, FSecurityCode from " +
                    //pub.yssGetTableName("Tb_Data_MarketValue") +
                    //" where FCheckState = 1" +
                    //" and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
                    //" and FMktValueDate <= " + dbl.sqlDate(dDate) +
                    //" group by FSecurityCode ) mk1 join (select " +
					//SQL优化调整 合并太平版本代码 
					" (select " +
                    vMethod.getMktPriceCode();
                //" as FCsMarketPrice,FSecurityCode, FMktValueDate  from " +
                //=============用参数来判断行情的停牌状态 by leeyu 2008-10-20  0000486=====
                if (isACTV) {
                    strSql += " as FCsMarketPrice,FSecurityCode, FMktValueDate,FMarketStatus  from "; //增加行情状态，by Leeyu 2008-10-20
                } else {
                    strSql += " as FCsMarketPrice,FSecurityCode, FMktValueDate  from ";
                }
                strSql +=
                    //=======================2008-10-20
					//SQL优化调整 合并太平版本代码 
                    //pub.yssGetTableName("Tb_Data_MarketValue") +
                    //" where FCheckState = 1 and FMktSrcCode = " +
                    //dbl.sqlString(vMethod.getMktSrcCode()) + ") mk2 " +
                    //" on mk1.FSecurityCode=mk2.FSecurityCode and mk1.FMktValueDate=mk2.FMktValueDate" +
					//SQL优化调整 合并太平版本代码 
					tmpMarketValueTable + " where FMktSrcCode="+dbl.sqlString(vMethod.getMktSrcCode())+" ) mk2 "; //SQL优化，对取行情数据的优化 by leeyu 20100421 合并太平资产版本调整
                  	strSql+=
                    " ) mk on cs.FCsSecurityCode = mk.FSecurityCode " +
                    //------------------------------------------------------------
                    
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                    
      
                    
                    " left join (select FPortCode, FPortName,FPortCury as FCsPortCury from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where  FCheckState = 1 ) m on  cs.FCsPortCode = m.FPortCode" +
                    
                    //end by lidaolong
                    //------------------------------------------------------------
                    " left join (select * from " +
                    pub.yssGetTableName("Tb_Para_Forward") +
                    " where FCheckState = 1) n on cs.FCsSecurityCode = n.FSecurityCode" +
                    //------------查询出当天流出的估值增值作为卖出估值增值-------------//
                    //2009-09-14 蒋锦 修改 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                    " LEFT JOIN (SELECT SUM(FMoney) AS FSMoney," +
                    " SUM(FBaseCuryMoney) AS FSBaseCuryMoney," +
                    " SUM(FPortCuryMoney) AS FSPortCuryMoney," +
                    " FSecurityCode, " +
                    (this.invmgrSecField.length() != 0 ?" FAnalysisCode1," : "") +
                    (this.brokerSecField.length() != 0 ?" FAnalysisCode2," : "") +
                    " FAttrClsCode, FInvestType" +//edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType字段
                    " FROM " + pub.yssGetTableName("Tb_Data_Secrecpay") +
                    " WHERE FCheckState = 1" +
                    " AND FTransDate = " + dbl.sqlDate(dDate) +
                    " AND FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
                    " AND FInOut = -1" +
                    " AND FPortCode = " + dbl.sqlString(portCode) +
                    " GROUP BY FTransDate, FPortCode, " +
                    (this.invmgrSecField.length() != 0 ?" FAnalysisCode1," : "") +
                    (this.brokerSecField.length() != 0 ?" FAnalysisCode2," : "") +
                    //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType字段
                    " FSecurityCode, FAttrClsCode,FTsfTypeCode,FInvestType) smv " +
                    //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType字段
                    " ON cs.FCSSecurityCode = smv.FSecurityCode AND cs.FAttrClsCode = smv.FAttrClsCode and cs.FInvestType = smv.FInvestType" +
                    (this.invmgrSecField.length() != 0 ? " AND cs." + this.invmgrSecField + " = smv.FAnalysisCode1" : "") +//edit by yanghaiming 20101109 此处的分析代码不可写死
                    (this.brokerSecField.length() != 0 ? " AND cs." + this.brokerSecField + " = smv.FAnalysisCode2" : "") +//edit by yanghaiming 20101109 此处的分析代码不可写死
                    //-----------------------------------------------------------//
                    //--------------------------------------------------edit by jc
                    //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType
                    " left join (select fb.fsecuritycode,fb.FattrClsCode, fb.FInvestType, " + //添加相关的属性分类代码到这里 BUG：000437
                    " sum(FAppreciation) as FAppreciation, " +
                    " sum(FMAppreciation) as FMAppreciation, " +
                    " sum(FVAppreciation) as FVAppreciation, " +
                    " sum(FBaseAppreciation) as FBaseAppreciation, " +
                    " sum(FMBaseAppreciation) as FMBaseAppreciation, " +
                    " sum(FVBaseAppreciation) as FVBaseAppreciation, " +
                    " sum(FPortAppreciation) as FPortAppreciation, " +
                    " sum(FMPortAppreciation) as FMPortAppreciation, " +
                    " sum(FVPortAppreciation) as FVPortAppreciation " +
                    " from " + pub.yssGetTableName("Tb_Data_TradeSellRela") +
                    " fa, " + pub.yssGetTableName("Tb_Data_SubTrade") + " fb " +
                    " where fa.fnum = fb.fnum and fa.fsubtsftypecode = '09EQ' " +
                    " and fb.fbargaindate = " + dbl.sqlDate(dDate) +
                    //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B 添加FInvestType
                    " group by fb.fsecuritycode,fb.FAttrClsCode, fb.FInvestType) ff on ff.fsecuritycode = cs.FCsSecurityCode and ff.FAttrClsCode=cs.FAttrClsCode and ff.FInvestType = cs.FInvestType "; //添加相关的属性分类代码到这里 BUG：000437
                //----------------------------------------------------------jc
                rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788
              //===add by xuxuming,2010.01.13.MS00902 指数信息调整后不能正确获取昨日估增余额=======
                HashMap hmRsSecOldStor=new HashMap();//保存昨日估增余额
                while(rs.next()){
                	String strKey = "";
                	String strValue="";
                	strKey=rs.getString("FStorageDate")+"\t"+rs.getString("FCSSecuritycode")+"\t"+rs.getString("FCSPortCode")+"\t"+rs.getString("FAttrClsCode");
                	strValue=rs.getDouble("FBal")+"\t"+rs.getDouble("FMBal")+"\t"+rs.getDouble("FVBal");
                	hmRsSecOldStor.put(strKey, strValue);
                }
                rs.beforeFirst();
                //==============end=============================================
                while (rs.next()) {
                	
                	 //----  STORY 1176 同一个证券不同投资类型的持仓需要支持按照不同的公允价或成本进行估值 add by jiangshichao  -------------------//
                    sParaCost = sParaBean.getGuess(this.portCode,rs.getString("FInvestType"));//1：成本法    0：市值法
                    sYhjBondVal = sParaBean.getGeussValue(this.portCode, rs.getString("FSubCatCode"), rs.getString("FExchangeCode"));//1：净价    0：全价  默认值 为NONE
                     //---- STORY 1176 同一个证券不同投资类型的持仓需要支持按照不同的公允价或成本进行估值 add by jiangshichao  end --------------//
                	
                    boolean isMarketPriceNull = false;
                    //现在使用的是行情日期存入股指行情表，但是由于锁定期债券股票的行情是通过计算得到，
                    //所以在没有录入行情的情况下第二天计算出的行情会将第一天计算的行情覆盖掉，所以当计算得到行情时要将行情日期修改为估值日期
                    //2009.08.24 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                    dMktValueDate = rs.getDate("FMktValueDate");
                    dMarketPrice = rs.getDouble("FCsMarketPrice");
                    //---------------sj modify 20081127 MS00024 ---------------------------------
                    if (rs.wasNull()) { //此列是否为sqlNull类型
                        isMarketPriceNull = true;
                    }
                    //--------------------------------------------------------------
                    if (rs.getString("FCalcPriceMetic") != null) {
                        dMarketPrice = this.getTodayMktPrice(dDate,
                            vMethod.getMktSrcCode(),
                            vMethod.getMktPriceCode(),
                            rs.getString("FCsSecurityCode"));
                        if (dMarketPrice != 0) {
                            bondIns = new YssBondIns();
                            bondIns.setInsType("ValPrice");
                            bondIns.setSecurityCode(rs.getString("FCsSecurityCode"));
                            bondIns.setPortCode(rs.getString("FCsPortCode"));
                            bondIns.setInsDate(this.dDate);
                            bondIns.setDisRate(dMarketPrice);
                            bondIns.setAnalysisCode1(this.invmgrSecField.length() != 0 ?
                                rs.getString(this.invmgrSecField) :
                                " ");
                            bondIns.setAnalysisCode2(this.invmgrSecField.length() != 0 ?
                                rs.getString(this.brokerSecField) :
                                " ");
                            bondOper = this.getSettingOper().getSpringRe(bondIns.
                                getSecurityCode(), "ValPrice");
                            bondOper.setYssPub(pub);
                            bondOper.init(bondIns);
                            dMarketPrice = bondOper.calBondInterest();
                        }
                    }
                    if (dMarketPrice == 0 && isMarketPriceNull && !rs.getString("FCatCode").equalsIgnoreCase("OP")) { //此处得判断，当为权证时不能跳出 QDV4建行2009年3月5日02_B MS00288 modify by leeyu
                        continue;
                    }
                    //2009.07.21 蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
                    //银行间债券的行情要处理一下
                    if(sParaCost.equalsIgnoreCase("0")&&rs.getString("FExchangeCode").equalsIgnoreCase(YssOperCons.YSS_JYSDM_YHJ) &&
                        rs.getString("FCatCode").equalsIgnoreCase("FI")){
                        //判断是否已成本市值孰低法估值
                        if(hmInterBankBond.get(portCode) != null){
                            double dbIntBankMktValue = YssD.div(rs.getDouble("FStorageCost"), rs.getDouble("FStorageAmount"));
                            if(dbIntBankMktValue < dMarketPrice){
                                dMarketPrice = dbIntBankMktValue;
                            }
                        }
                        
                        
                        /*****************************************************************************
                         * STORY #1156 银行间债券添加新的估值方法  add by jiangshichao 2011.07.21
                         * 
                         * 报价方式为净价(1-净价)，估值方式为全价，则原币市值=持有数量*(行情 + 利息)
                         * 报价方式为全价(0-全价)，估值方式为净价，则原币市值=持有数量*(行情 – 应计提总利息)
                         */
                        
                        if(rs.getString("FQuoteWay").equalsIgnoreCase("1") && sYhjBondVal.equalsIgnoreCase("0")){
                            BigDecimal bigInt100 = new BigDecimal(0);
                            BigDecimal bigBefInt100 = new BigDecimal(0);
                            if (hmIntAccPer100.get(rs.getString("FCsSecurityCode")) != null) {
                                bigInt100 = ( (BondInterestBean) hmIntAccPer100.get(rs.getString("FCsSecurityCode"))).getIntAccPer100();
                            } else{
                            	//add by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
                            	dataBase = new DataBase();
                            	dataBase.setYssPub(pub);
                            	//计算税前 税后 百元债券利息 因为无法判断买卖标志 所以 默认为  用买入计息设置的利息算法公式来计算百元债券利息
                                hmZQRate = dataBase.calculateZQRate(rs.getString("FCsSecurityCode"), dDate, "B", portCode);

                                //若不能在债券信息设置中找到当前债券的信息 则 提示用户维护当前债券的信息
                                if(((String)hmZQRate.get("haveInfo")).equals("false")){
                                	throw new YssException("请设置 " + rs.getString("FCsSecurityCode") + " 的相关债券信息！");
                                }
                                
                                //获取税后百元债券利息
                                bigInt100 = new BigDecimal((String)hmZQRate.get("GZLX"));
                                //获取税前百元债券利息
                                bigBefInt100 = new BigDecimal((String)hmZQRate.get("SQGZLX"));
                                
								if (!alZQCodes.contains(rs.getString("FCsSecurityCode"))) {
	                                bondInterest = new BondInterestBean();//新建债券利息实例
	                                bondInterest.setSecurityCode(rs.getString("FCsSecurityCode"));//设置证券代码
	                                bondInterest.setIntAccPer100(new BigDecimal((String)hmZQRate.get("SQGZLX")));//设置税前百元利息
	                                bondInterest.setSHIntAccPer100(new BigDecimal((String)hmZQRate.get("GZLX")));//设置税后百元利息
									
									alZQCodes.add(rs.getString("FCsSecurityCode"));
									alZQInfo.add(bondInterest);// 将债券利息实例添加到列表中
								}
								
                            }
                        		dMarketPrice = YssD.round(YssD.add(dMarketPrice, bigInt100.doubleValue()), 2);   
                        }else if(rs.getString("FQuoteWay").equalsIgnoreCase("0") && sYhjBondVal.equalsIgnoreCase("1")){
                        	BigDecimal bigInt100 = new BigDecimal(0);
                            BigDecimal bigBefInt100 = new BigDecimal(0);
                            if (hmIntAccPer100.get(rs.getString("FCsSecurityCode")) != null) {
                                bigInt100 = ( (BondInterestBean) hmIntAccPer100.get(rs.getString("FCsSecurityCode"))).getIntAccPer100();
                            } else{
                            	//add by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
                            	dataBase = new DataBase();
                            	dataBase.setYssPub(pub);
                            	//计算税前 税后 百元债券利息 因为无法判断买卖标志 所以 默认为  用买入计息设置的利息算法公式来计算百元债券利息
                                hmZQRate = dataBase.calculateZQRate(rs.getString("FCsSecurityCode"), dDate, "B", portCode);

                                //若不能在债券信息设置中找到当前债券的信息 则 提示用户维护当前债券的信息
                                if(((String)hmZQRate.get("haveInfo")).equals("false")){
                                	throw new YssException("请设置 " + rs.getString("FCsSecurityCode") + " 的相关债券信息！");
                                }
                                
                                //获取税后百元债券利息
                                bigInt100 = new BigDecimal((String)hmZQRate.get("GZLX"));
                                //获取税前百元债券利息
                                bigBefInt100 = new BigDecimal((String)hmZQRate.get("SQGZLX"));
                                
								if (!alZQCodes.contains(rs.getString("FCsSecurityCode"))) {
	                                bondInterest = new BondInterestBean();//新建债券利息实例
	                                bondInterest.setSecurityCode(rs.getString("FCsSecurityCode"));//设置证券代码
	                                bondInterest.setIntAccPer100(new BigDecimal((String)hmZQRate.get("SQGZLX")));//设置税前百元利息
	                                bondInterest.setSHIntAccPer100(new BigDecimal((String)hmZQRate.get("GZLX")));//设置税后百元利息
									
									alZQCodes.add(rs.getString("FCsSecurityCode"));
									alZQInfo.add(bondInterest);// 将债券利息实例添加到列表中
								}
								
                            }
                    		dMarketPrice = YssD.round(YssD.sub(dMarketPrice, bigInt100.doubleValue()), 2);   
                        }
                        
                        //--- STORY #1156 银行间债券添加新的估值方法  add by jiangshichao 2011.07.21  end ---------------
                        
                        
                    }
                    //2009.07.21 蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
                    if ((sParaCost.equalsIgnoreCase("0")&&rs.getString("FExchangeCode").equalsIgnoreCase(YssOperCons.YSS_JYSDM_SHJYS)) ||
                    		(sParaCost.equalsIgnoreCase("0")&&rs.getString("FExchangeCode").equalsIgnoreCase(YssOperCons.YSS_JYSDM_SZJYS))) {
                        //判断交易所利息税入了成本的公司债、企业债、可分离债以行情加利息税估值
                        if ( (rs.getString("FSubCatCode").equalsIgnoreCase("FI08") ||
                              rs.getString("FSubCatCode").equalsIgnoreCase("FI07") ||
                              rs.getString("FSubCatCode").equalsIgnoreCase("FI09"))) {
                            ExchangeBondBean exBond = (ExchangeBondBean) hmBondParams.get(pub.getPrefixTB() + " " +
                                this.portCode + " " + (String)hmExchangeIntFace.get(rs.getString("FExchangeCode")) + " " +
                                (String)hmCatCodeIntFace.get(rs.getString("FSubCatCode")));
                            if(exBond != null){
                                if (exBond.getInteDutyType().equalsIgnoreCase("00") || exBond.getInteDutyType().equalsIgnoreCase("02")) {
                                	//add by songjie 2010.03.17 MS00911 QDV4赢时胜（测试）2010年03月16日04_B
                                	if(hmPorts.get(rs.getString("FCsPortCode")) != null){
                                		//add by songjie 2010.03.17 MS00911 QDV4赢时胜（测试）2010年03月16日04_B
                                        BigDecimal bigInt100; //税后每百元票面利息
                                        BigDecimal bigBefInt100; //税前每百元票面利息
                                        if (hmIntAccPer100.get(rs.getString("FCsSecurityCode")) != null) {
                                        	//delete by songjie 2010.03.15 MS00896 QDV4赢时胜（上海）2010年02月05日01_B
//                                            bigInt100 = ( (BondInterestBean) hmIntAccPer100.get(rs.getString("FCsSecurityCode"))).getIntAccPer100();
                                        	//add by songjie 2010.03.15 MS00896 QDV4赢时胜（上海）2010年02月05日01_B
                                        	bigInt100 = ( (BondInterestBean) hmIntAccPer100.get(rs.getString("FCsSecurityCode"))).getSHIntAccPer100();
                                        	bigBefInt100 = ( (BondInterestBean) hmIntAccPer100.get(rs.getString("FCsSecurityCode"))).getIntAccPer100();
                                        	//add by songjie 2010.03.15 MS00896 QDV4赢时胜（上海）2010年02月05日01_B
                                        } else {
                                        	//add by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
                                        	dataBase = new DataBase();
                                        	dataBase.setYssPub(pub);
                                        	//计算税前 税后 百元债券利息 因为无法判断买卖标志 所以 默认为  用买入计息设置的利息算法公式来计算百元债券利息
                                            hmZQRate = dataBase.calculateZQRate(rs.getString("FCsSecurityCode"), dDate, "B", portCode);

                                            //若不能在债券信息设置中找到当前债券的信息 则 提示用户维护当前债券的信息
                                            if(((String)hmZQRate.get("haveInfo")).equals("false")){
                                            	throw new YssException("请设置 " + rs.getString("FCsSecurityCode") + " 的相关债券信息！");
                                            }
                                            
                                            //获取税后百元债券利息
                                            bigInt100 = new BigDecimal((String)hmZQRate.get("GZLX"));
                                            //获取税前百元债券利息
                                            bigBefInt100 = new BigDecimal((String)hmZQRate.get("SQGZLX"));
                                            
											if (!alZQCodes.contains(rs.getString("FCsSecurityCode"))) {
	                                            bondInterest = new BondInterestBean();//新建债券利息实例
	                                            bondInterest.setSecurityCode(rs.getString("FCsSecurityCode"));//设置证券代码
	                                            bondInterest.setIntAccPer100(new BigDecimal((String)hmZQRate.get("SQGZLX")));//设置税前百元利息
	                                            bondInterest.setSHIntAccPer100(new BigDecimal((String)hmZQRate.get("GZLX")));//设置税后百元利息
												
												alZQCodes.add(rs.getString("FCsSecurityCode"));
												alZQInfo.add(bondInterest);// 将债券利息实例添加到列表中
											}
											//add by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
											
											//delete by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
//                                            bigInt100 = new BigDecimal("0");
//                                            //add by songjie 2010.03.15 MS00896 QDV4赢时胜（上海）2010年02月05日01_B
//                                            bigBefInt100 = new BigDecimal("0");
                                            //delete by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
                                        }
                                        //delete by songjie 2010.03.15 MS00896 QDV4赢时胜（上海）2010年02月05日01_B
//                                        //计算税前每百元票面利息
//                                        HashMap hmDate = new HashMap();
//                                        BondInsCfgFormula cfgFor = new BondInsCfgFormula();
//                                        cfgFor.setYssPub(pub);
//                                        cfgFor.getNextStartDateAndEndDate(dDate, rs, hmDate);
//                                        bigBefInt100 = YssD.mulD(YssD.divD(rs.getBigDecimal("FBeforeFaceRate"), new BigDecimal("365")),
//                                            YssD.mulD(YssD.divD(rs.getBigDecimal("FFaceValue"), new BigDecimal("100")),
//                                                      new BigDecimal(YssFun.dateDiff( (java.util.Date) hmDate.get("InsStartDate"), dDate) + "")));
                                        //delete by songjie 2010.03.15 MS00896 QDV4赢时胜（上海）2010年02月05日01_B
                                        //edit by songjie 2010.03.17 MS00910 QDII4.1赢时胜上海2010年03月16日02_B
                                        double dbInsDuty = YssD.sub(bigBefInt100, bigInt100);
                                        dMarketPrice = YssD.add(dbInsDuty, dMarketPrice);   
                                        //add by songjie 2010.03.17 MS00911 QDV4赢时胜（测试）2010年03月16日04_B
                                        
                                        //add by yanghaiming 20091216 MS00871 赢时胜(上海)2009年12月11日05_B                                        
                                        //系统在加利息税计算行情或者减利息税计算行情的时候，计算出的估值行情保留2位小数
                                        //---edit by songjie 2012.03.20 根据交易所判断行情保留位数 start ---//
                                        if(YssOperCons.YSS_JYSDM_SHJYS.equals(rs.getString("FExchangeCode"))){
                                        	dMarketPrice = YssD.round(dMarketPrice, 2);//上交所债券行情保留2位
                                        }
                                        if(YssOperCons.YSS_JYSDM_SZJYS.equals(rs.getString("FExchangeCode"))){
                                        	dMarketPrice = YssD.round(dMarketPrice, 3);//深交所债券行情保留3位
                                        }
                                        //---edit by songjie 2012.03.20 根据交易所判断行情保留位数 end ---//
                                	}
                                	//add by songjie 2010.03.17 MS00911 QDV4赢时胜（测试）2010年03月16日04_B
                                }
                            }
                        }
                        //可转债的行情为全价，我们用净价估值，所以要减掉利息
                        else if (rs.getString("FSubCatCode").equalsIgnoreCase("FI06")) {
                            BigDecimal bigInt100;
                            
                            //add by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
                            BigDecimal bigBefInt100;
                            if (hmIntAccPer100.get(rs.getString("FCsSecurityCode")) != null) {
                                bigInt100 = ( (BondInterestBean) hmIntAccPer100.get(rs.getString("FCsSecurityCode"))).getIntAccPer100();
                            } else{
                            	//add by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
                            	dataBase = new DataBase();
                            	dataBase.setYssPub(pub);
                            	//计算税前 税后 百元债券利息 因为无法判断买卖标志 所以 默认为  用买入计息设置的利息算法公式来计算百元债券利息
                                hmZQRate = dataBase.calculateZQRate(rs.getString("FCsSecurityCode"), dDate, "B", portCode);

                                //若不能在债券信息设置中找到当前债券的信息 则 提示用户维护当前债券的信息
                                if(((String)hmZQRate.get("haveInfo")).equals("false")){
                                	throw new YssException("请设置 " + rs.getString("FCsSecurityCode") + " 的相关债券信息！");
                                }
                                
                                //获取税后百元债券利息
                                bigInt100 = new BigDecimal((String)hmZQRate.get("GZLX"));
                                //获取税前百元债券利息
                                bigBefInt100 = new BigDecimal((String)hmZQRate.get("SQGZLX"));
                                
								if (!alZQCodes.contains(rs.getString("FCsSecurityCode"))) {
	                                bondInterest = new BondInterestBean();//新建债券利息实例
	                                bondInterest.setSecurityCode(rs.getString("FCsSecurityCode"));//设置证券代码
	                                bondInterest.setIntAccPer100(new BigDecimal((String)hmZQRate.get("SQGZLX")));//设置税前百元利息
	                                bondInterest.setSHIntAccPer100(new BigDecimal((String)hmZQRate.get("GZLX")));//设置税后百元利息
									
									alZQCodes.add(rs.getString("FCsSecurityCode"));
									alZQInfo.add(bondInterest);// 将债券利息实例添加到列表中
								}
								//add by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
                            	
								//delete by songjie 2010.03.23 国内：MS00921 QDV4赢时胜（测试）2010年03月19日01_B
//                                bigInt100 = new BigDecimal("0");
                            }
                        	//add by zhouwei 20120419 当日无行情信息，去之前的估值行情信息 bug 4295
                            if(YssFun.dateDiff(dDate, rs.getDate("FMktValueDate"))==0){
                            	dMarketPrice = YssD.sub(new BigDecimal(dMarketPrice + ""), bigInt100);
                            }else{
                            	dMarketPrice=getValMarketValue(YssFun.addDay(dDate, -1), rs.getString("FCsSecurityCode"));
                            }
                            //add by yanghaiming 20091216 MS00871 赢时胜(上海)2009年12月11日05_B
                            //系统在加利息税计算行情或者减利息税计算行情的时候，计算出的估值行情保留2位小数
                            
                            
                            //~~~~~~~ 【BUG4824 深交所债券价格按3位行情估值 】 add by jsc 20120619  start 
                            /**
                             * 根据“深圳证券交易所交易规则（2011年修订）.pdf”第7页提到，2011-02-28开始，深交所债券价格按3位行情估值，
                             * 目前系统对于深圳可转债的处理时，在减去百元利息后，保留的行情价格为2位的，应当四舍五入保留3位。
                             */
                            if(YssOperCons.YSS_JYSDM_SZJYS.equals(rs.getString("FExchangeCode"))&&YssFun.dateDiff(YssFun.parseDate("2011-02-28"),dDate)>=0){
                            	dMarketPrice = YssD.round(dMarketPrice, 3);
                            }else{
                            	dMarketPrice = YssD.round(dMarketPrice, 2);
                            }
                          //~~~~~~~ 【BUG4824 深交所债券价格按3位行情估值 】 add by jsc 20120619  end 
                        }
                     }
                    //2009.7.9 蒋锦 添加 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A--
                    //当股票为非公开发行，有明确锁定期，该只股票的估值价格需要通过计算获得
                    if (sParaCost.equalsIgnoreCase("0")&& 
                        rs.getString("FAttrClsCode").equalsIgnoreCase(YssOperCons.YSS_SXFL_UNPONS) &&
                        rs.getDate("FLockBeginDate") != null && 
                        //--- edit by songjie 2013.06.08 BUG 8301 QDV4南方2013年06月17日01_B start---//
                        //添加条件： 业务日期 < 锁定结束日 且 行情日期 不等于 业务日期
                        YssFun.dateDiff(rs.getDate("FLockEndDate"), dDate) < 0 &&
                        YssFun.dateDiff(rs.getDate("FMktValueDate"),dDate) != 0) {
                    	//--- edit by songjie 2013.06.08 BUG 8301 QDV4南方2013年06月17日01_B end---//
                        dMarketPrice = getLockedPrice(rs, dMarketPrice);
                        //通过计算得到的行情使用估值日期存入估值行情表
                        dMktValueDate = dDate;
                    }
                    
                    //----  #1176 同一个证券不同投资类型的持仓需要支持按照不同的公允价或成本进行估值 add by jiangshichao  -------------------//
                    //需求人员沟通结果是以投资类型设置的估值方法优先级最高。
                    if(sParaCost.equalsIgnoreCase("1")){
                    	dMarketPrice = YssD.round(YssD.div(rs.getDouble("FStorageCost"), rs.getDouble("FStorageAmount")), 2);
                    }
                    //---- #1176 同一个证券不同投资类型的持仓需要支持按照不同的公允价或成本进行估值 add by jiangshichao  end --------------//
                    
                    //---add by songjie 2011.08.03 BUG 2306 QDV4中国银行2011年07月26日01_B--- start//
                    if(vMethod.getMTVMethod().equals("1")){
                    	dMarketPrice = YssD.div(rs.getDouble("FStorageCost"), rs.getDouble("FStorageAmount"));
                    }
                    //---add by songjie 2011.08.03 BUG 2306 QDV4中国银行2011年07月26日01_B--- end  //
                    //===========添加对配股证券的方法的处理 by leeyu 20090307 QDV4建行2009年3月5日02_B MS00288
                    if (rs.getString("FSubCatCode").equalsIgnoreCase("OP02")) {
                        payPrice = getValRightIssUeMV(rs.getString("FCsSecurityCode"), vMethod);
                        if (payPrice == null) {
                            continue; //判断若为空，就不继续执行 由于下面代码取行情也采用left join by leeyu 20090325 公共数据跨组合群参数QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
                        }
                    } else {
                        //------------------------------------------------------------------------                    	
                        payPrice = new SecPecPayBean();
                        mktPrice = new ValMktPriceBean();
                        payPrice.setTransDate(dDate);

                        //调试代码未去除  by yanghaiming 20101102
//                        if (rs.getString("FCsSecurityCode").equalsIgnoreCase("2914456Z LN")) {
//                            int ii = 0;
//                        }

                        payPrice.setStrSecurityCode(rs.getString("FCsSecurityCode"));
                        payPrice.setStrPortCode(rs.getString("FCsPortCode"));
                        payPrice.setInvMgrCode(this.invmgrSecField.length() != 0 ?
                                               rs.getString(this.invmgrSecField) :
                                               " ");
                        payPrice.setBrokerCode(this.brokerSecField.length() != 0 ?
                                               rs.getString(this.brokerSecField) :
                                               " ");

                        if (rs.getString("FCsCuryCode") == null) {
                            throw new YssException("请检查证券品种【" +
                                rs.getString("FCsSecurityCode") +
                                "】的交易币种设置！");
                        }

                        payPrice.setStrCuryCode(rs.getString("FCsCuryCode"));
                        payPrice.setAttrClsCode(rs.getString("FAttrClsCode")); //sj add 20071204
                        //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B
                        payPrice.setInvestType(rs.getString("FInvestType"));
                        iFactor = rs.getInt("FCsFactor");

                        dBaseRate = 1;
                        if (!rs.getString("FCsCuryCode").equalsIgnoreCase(pub.
                        		getPortBaseCury(this.portCode))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                                vMethod.getBaseRateSrcCode(),
                                vMethod.getBaseRateCode(),
                                vMethod.getPortRateSrcCode(),
                                vMethod.getPortRateCode(),
                                rs.getString("FCsCuryCode"), this.portCode,
                                YssOperCons.YSS_RATE_BASE);
                        }

                        if (rs.getString("FCsPortCury") == null) {
                            throw new YssException("请检查投资组合【" +
                                rs.getString("FCsPortCode") +
                                "】的币种设置！");
                        }
                        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 ---
                        rateOper.getInnerPortRate(dDate, rs.getString("FCsCuryCode"), this.portCode, vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                                  vMethod.getPortRateSrcCode(), vMethod.getPortRateCode()); //用通用方法，获取组合汇率
                        dPortRate = rateOper.getDPortRate(); //获取组合汇率
                        
                        //V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0 则默认组合汇率为1
                        if(dPortRate == 0){
                        	dPortRate = 1;
                        }
                        //V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0 则默认组合汇率为1
                        //------------------------------------------------------------
                        sCatCode = rs.getString("FCsCatCode");
                        payPrice.setBaseCuryRate(dBaseRate);
                        payPrice.setPortCuryRate(dPortRate);
                        payPrice.setMktPrice(dMarketPrice);
                      //调试代码未去除  by yanghaiming 20101102
//                        if (payPrice.getStrSecurityCode().equalsIgnoreCase("2914456Z LN")) {
//                            int y = 0;
//                        }
                        if (sCatCode.equalsIgnoreCase("FW")) {
                            valFWSecs(rs, payPrice, mktPrice);
                        } else {
                        	valCommonSecs(rs, payPrice, dMarketPrice,hmRsSecOldStor);//add by xuxuming,2010.01.12.将昨日估增余额放在HASHMAP中作为参数传入
                        }

                        payPrice.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV);
                        payPrice.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV +
                            sCatCode);
                        payPrice.checkStateId = 1;
                        //=============用参数来判断行情的停牌状态 by leeyu 2008-10-20  0000486=====
                        if (isACTV) {
                            mktPrice.setMarketStatus(rs.getString("FMarketStatus"));
                        }
                        //===========2008-10-20
                        //2008.07.14 蒋锦 修改 使用行情日期代替估值日期
                        mktPrice.setValDate(dMktValueDate);
                        mktPrice.setSecurityCode(payPrice.getStrSecurityCode());
                        mktPrice.setPortCode(portCode);
                        mktPrice.setPrice(YssD.div(dMarketPrice, iFactor, 12));
                        //-------------MS00272 QDV4赢时胜（上海）2009年2月26日01_B
                        mktPrice.setMtvCode(vMethod.getMTVCode()); //设置估值方法
                        //-----------------------------------------------------
                        //------------- MS00265 QDV4建行2009年2月23日01_B  -----
                        mktPrice.setValType("SecsMV"); //设置估值类型为普通类型的证券，与估值界面上的代码相一致。
                        //2009.7.9 蒋锦 添加 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A-- 估值行情中要包括属性分类代码
                        mktPrice.setAttrClsCode(rs.getString("FAttrClsCode"));
                    } //对普通证券的判断处理
                    //-----------------------------------------------------
                    if (priMarketPrice.toLowerCase().equalsIgnoreCase("valuation")) {
                    //2009.07.10 蒋锦 修改 估值行情表增加了属性分类作为主键，所以哈西Key也要添加属性分类
                        hmValPrice.put(mktPrice.getSecurityCode() + "\t" + mktPrice.getAttrClsCode(), mktPrice);
                        //--------MS00264  QDV4中保2009年02月24日01_B  将放置估值值的位置放入各自的通用参数选择项中，视各自的情况而定--//
                        sKey = payPrice.getStrSecurityCode() + "\f" +
                            (this.invmgrSecField.length() != 0 ?
                             (payPrice.getInvMgrCode() + "\f") : "") +
                            (this.brokerSecField.length() != 0 ?
                             (payPrice.getBrokerCode() + "\f") : "") +
                            payPrice.getStrSubTsfTypeCode() + "\f" +
                            (payPrice.getAttrClsCode().length() == 0 ? " " :
                             //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B
                             payPrice.getAttrClsCode()) + "\f" + payPrice.getInvestType();
                        hmResult.put(sKey, payPrice);
                        //--------------------------------------------------------------------------------------------------//

                    } else if (priMarketPrice.toLowerCase().equalsIgnoreCase("day")) {
                        //-----------------------------------若其它行情来源的行情比之前行情来源的行情数据更新，则代替此行情数据。sj edit 20080818 bug 0000418 --//
                        //2009.07.10 蒋锦 修改 估值行情表增加了属性分类作为主键，所以哈西Key也要添加属性分类
                        if (hmValPrice.get(mktPrice.getSecurityCode() + "\t" + mktPrice.getAttrClsCode()) != null) {
                            oldMktPrice = (ValMktPriceBean) hmValPrice.get(mktPrice.
                                getSecurityCode() + "\t" + mktPrice.getAttrClsCode());
                            if (YssFun.dateDiff(oldMktPrice.getValDate(),
                                                mktPrice.getValDate()) >= 0) {
                                hmValPrice.remove(oldMktPrice.getSecurityCode() + "\t" + oldMktPrice.getAttrClsCode());
                                hmValPrice.put(mktPrice.getSecurityCode() + "\t" + mktPrice.getAttrClsCode(), mktPrice);
                                sKey = payPrice.getStrSecurityCode() + "\f" +
                                    (this.invmgrSecField.length() != 0 ?
                                     (payPrice.getInvMgrCode() + "\f") : "") +
                                    (this.brokerSecField.length() != 0 ?
                                     (payPrice.getBrokerCode() + "\f") : "") +
                                    payPrice.getStrSubTsfTypeCode() + "\f" +
                                    (payPrice.getAttrClsCode().length() == 0 ? " " :
                                     //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B	
                                     payPrice.getAttrClsCode()) + "\f" + payPrice.getInvestType(); //采用上面的KEY统一处理 by leeyu 2009-1-16
                                hmResult.put(sKey, payPrice);
                            }
                        } else {
                            sKey = payPrice.getStrSecurityCode() + "\f" +
                                (this.invmgrSecField.length() != 0 ?
                                 (payPrice.getInvMgrCode() + "\f") : "") +
                                (this.brokerSecField.length() != 0 ?
                                 (payPrice.getBrokerCode() + "\f") : "") +
                                payPrice.getStrSubTsfTypeCode() + "\f" +
                                (payPrice.getAttrClsCode().length() == 0 ? " " :
                                 //edit by songjie 2011.07.19 BUG 2275 QDV4中国银行2011年07月14日01_B
                                 payPrice.getAttrClsCode()) + "\f" + payPrice.getInvestType(); //采用上面的KEY统一处理 by leeyu 2009-1-16
                            hmResult.put(sKey, payPrice);
                            //2009.07.10 蒋锦 修改 估值行情表增加了属性分类作为主键，所以哈西Key也要添加属性分类
                            hmValPrice.put(mktPrice.getSecurityCode() + "\t" + mktPrice.getAttrClsCode(), mktPrice);
                        }
//---------------------------------------------------------------------------------------------------------------------------//
                    }
                }
                dbl.closeResultSetFinal(rs);
                //2009-06-24 蒋锦 添加 基金的估值 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》  国内基金业务
                getTRSec(hmResult, vMethod, isACTV, sExchangeCode);
            }

            //-----------------获取估值的所有证券代码,用于保存估值结果的删除条件 胡昆 20071217
            Iterator iter = hmResult.values().iterator();
            while (iter.hasNext()) {
                buf.append( ( (SecPecPayBean) iter.next()).getStrSecurityCode()).append(",");
            }
            if (buf.length() > 0) {
                buf.setLength(buf.length() - 1);
            }
            valSecCodes = buf.toString();
            //---------------------------------------------------------------------
            
            //add by songjie 2010.03.23 MS00921 QDV4赢时胜（测试）2010年03月19日01_B
            if(alZQCodes.size() > 0){
            	//将债券利息表中没有的债券利息数据插入到表中
            	insertIntoBondInterest(alZQCodes,alZQInfo);
            }
            //add by songjie 2010.03.23 MS00921 QDV4赢时胜（测试）2010年03月19日01_B
            
            return hmResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /** 
     * add by zhouwei 20120418 获取估值行情
    * @Title: getValMarketValue 
    * @Description: TODO
    * @param @param valdate
    * @param @param valSecurityCode
    * @param @return
    * @param @throws YssException    设定文件 
    * @return double    返回类型 
    * @throws 
    */
    private double getValMarketValue(Date valdate,String valSecurityCode) throws YssException{
    	ResultSet rs=null;
    	String sql="";
    	double mkValue=0;
    	try{
    		sql="select a.FValDate,a.FPortCode,a.FSecurityCode,a.FPrice,a.FAttrClsCode from "+pub.yssGetTableName("TB_Data_ValMktPrice")
    		   +" a JOIN (SELECT MAX(FValDate) AS FValDate,FSecurityCode,FPortCode from "+pub.yssGetTableName("TB_Data_ValMktPrice")
    		   +" WHERE FValDate <="+dbl.sqlDate(valdate)+" and FPortCode ="+dbl.sqlString(portCode)//获取最大日期的估值行情
    		   +" and FSecurityCode="+dbl.sqlString(valSecurityCode)+"  group by  FSecurityCode,FPortCode) b ON a.FValDate =b.FValDate and a.FSecurityCode=b.FSecurityCode"
    		   +" and a.FPortCode=b.FPortCode";
    		rs=dbl.openResultSet(sql);
    		if(rs.next()){
    			mkValue=rs.getDouble("FPrice");
    		}
    	}catch (Exception e) {
			throw new YssException("获取证券的估值行情信息", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return mkValue;
    }
    /**
     * 将债券利息表中没有的债券利息数据插入到表中
     * add by songjie
     * 2010.03.23
     * MS00921
     * QDV4赢时胜（测试）2010年03月19日01_B
     * @throws YssException
     */
    private void insertIntoBondInterest(ArrayList alZQCodes,ArrayList alZQInfo) throws YssException {
        String strSql = "";//用于储存sql语句
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        PreparedStatement pstmt = null;//声明PreparedStatement
        BondInterestBean bondInterest = null;
        Iterator iterator = null;
        String zqdms = "";
        String zqdm = null;
   	    //add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 start 
   		int count = 1;
        try{
            iterator = alZQCodes.iterator();//获取迭代器

            while(iterator.hasNext()){
                zqdm = (String)iterator.next();//获取证券代码
                zqdms += zqdm + ",";//拼接证券代码
            }

            if(zqdms.length() >= 1){
                zqdms = zqdms.substring(0, zqdms.length() - 1);//去掉字符串最后逗号
            }

            con.setAutoCommit(false); //设置手动提交事务
            bTrans = true;

            //先在债券利息表中删除需要插入到债券利息表中的债券数据
            strSql = " delete from " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " where FSecurityCode in(" + operSql.sqlCodes(zqdms) +
                //edit by songjie 2010.03.18 MS00920 QDV4赢时胜（测试）2010年03月18日06_B
                ") and FRecordDate = " + dbl.sqlDate(dDate);

            dbl.executeSql(strSql);

            //添加数据到债券利息表中
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_BondInterest") +
                "(FSecurityCode,FRecordDate,FCurCpnDate,FNextCpnDate,FIntAccPer100," +
                "FIntDay,FSHIntAccPer100,FDataSource,FCheckState,FCreator,FCreateTime)" +
                "values(?,?,?,?,?,?,?,?,?,?,?)";

            pstmt=dbl.getPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788

            iterator = alZQInfo.iterator();
            while(iterator.hasNext()){
                bondInterest = (BondInterestBean)iterator.next();//获取债券利息实例

                pstmt.setString(1, bondInterest.getSecurityCode());//设置证券代码
                pstmt.setDate(2, YssFun.toSqlDate(dDate));//设置业务日期
                pstmt.setDate(3, YssFun.toSqlDate("9998-12-31"));
                pstmt.setDate(4, YssFun.toSqlDate("9998-12-31"));
                pstmt.setBigDecimal(5, bondInterest.getIntAccPer100());//设置税前百元利息
                pstmt.setInt(6, 0);
                pstmt.setBigDecimal(7, bondInterest.getSHIntAccPer100());//设置税后百元利息
                pstmt.setString(8, "HD");//表示是系统计算而得到的百元债券利息
                pstmt.setInt(9, 1);
                pstmt.setString(10, pub.getUserCode()); //创建人、修改人
                pstmt.setString(11, YssFun.formatDatetime(new java.util.Date())); //创建、修改时间

                pstmt.addBatch();
                

              //add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 start 
              				
              				if(count==500){
              					pstmt.executeBatch();
              					count = 1;
								continue;
              				}

							count++;
              				//add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420 end
                
            }

            pstmt.executeBatch();
            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        }
        catch(Exception e){
            throw new YssException("将数据插入到债券利息表时出错！",e);
        }
        finally{
            dbl.closeStatementFinal(pstmt);
            dbl.endTransFinal(con, bTrans); //关闭连接
        }
    }
	
    /**
     * 原来的普通证券的估值方法增加了一个参数，为了支持对以前方法的调用，重载了该方法,
     * 直接调用改变后的方法，新增的参数传入空值
     * @param rs
     * @param payPrice
     * @param dMarketPrice
     * @throws YssException
     */
        private void valCommonSecs(ResultSet rs, SecPecPayBean payPrice,
    			double dMarketPrice) throws YssException {
    		this.valCommonSecs(rs, payPrice, dMarketPrice, null);
    		
    	}

    /**
     * 使用前一天行情估值的基金的估值
     * @param hmResult HashMap：返回结果
     * @param vMethod MTVMethodBean：当前估值方法
     * @param isACTV boolean：使用使用行情状态
     * @param sExchangeCode String：使用前日行情的交易所
     * @throws YssException
     */
    private void getTRSec(HashMap hmResult,
                          MTVMethodBean vMethod,
                          boolean isACTV,
                          String sExchangeCode) throws YssException{
        String strSql = "";
        SecPecPayBean payPrice = null;
        ResultSet rs = null;
        String sKey = "", sCatCode = "";
        double dBaseRate = 1;
        double dPortRate = 1;
        double dMarketPrice = 0;
        int iFactor = 1; //报价因子
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------
        try {
            if(sExchangeCode == null || sExchangeCode.trim().length() == 0){
                return;
            }
            strSql = " select cs.*," +
                    " FBAL, FMBAL, FVBAL, FPORTCURYBAL, FMPORTCURYBAL, FVPORTCURYBAL, FBASECURYBAL, FMBASECURYBAL, FVBASECURYBAL,FBalF, FBaseCuryBalF, FPortCuryBalF," +
                    "smv.FSMoney, smv.FSBaseCuryMoney, smv.FSPortCuryMoney,";
                if (isACTV) {
                    strSql += " mk.FCsMarketPrice, mk.FMktValueDate,mk.FMarketStatus, m.FCsPortCury from (select a.FStorageDate, a.FSecurityCode as FCsSecurityCode,"; //增加行情状态 by leeyu 2008
                } else {
                    strSql += " mk.FCsMarketPrice, mk.FMktValueDate, m.FCsPortCury from (select a.FStorageDate, a.FSecurityCode as FCsSecurityCode,";
                }
                strSql +=
                    " FStorageAmount, FStorageCost, FMStorageCost, FVStorageCost," +
                    " FBaseCuryCost as FCsBaseCuryCost, FMBaseCuryCost as FCsMBaseCuryCost, FVBaseCuryCost as FCsVBaseCuryCost," +
                    " FPortCuryCost as FCsPortCuryCost, FMPortCuryCost as FCsMPortCuryCost, FVPortCuryCost as FCsVPortCuryCost" +
                    //判断是否配置分析代码，杨
                    (this.invmgrSecField.length() != 0 ?
                     ("," + this.invmgrSecField) : " ") +
                    (this.brokerSecField.length() != 0 ?
                     ("," + this.brokerSecField) : " ") +
                    ",a.FPortCode as FCsPortCode, a.FAttrClsCode as FAttrClsCode ,sec.FTradeCury as FCsCuryCode, " +
                    " sec.FCatCode as FCsCatCode, sec.FFactor as FCsFactor,FCatCode,FSubCatCode from " + // wdy 添加表别名a
                    pub.yssGetTableName("tb_stock_security") + " a" +
                    //------------------------------------------------------------
                    " join (select sb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("tb_para_security") +
                    " where FCheckState=1 and FStartDate<= " +
                    dbl.sqlDate(dDate) +
                    " group by FSecurityCode ) sa join (select FSecurityCode, FSecurityName, FStartDate, FCatCode, FSubCatCode, FTradeCury,FFactor from " +
                    pub.yssGetTableName("tb_para_security") +
                    //2009-06-24 蒋锦 修改 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》  国内基金业务
                    //只处理基金的
                    " where FCheckState=1 and FCatCode = 'TR' and FExchangeCode in (" + operSql.sqlCodes(sExchangeCode) + ")" +
                    " )sb on sa.FSecurityCode = sb.FSecurityCode and sa.FStartDate = sb.FStartDate " +
                    ") sec on a.FSecurityCode = sec.FSecurityCode" +
                    //------------------------------------------------------------
                    " join (select FLinkCode from " +
                    pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " where FCheckState = 1 and FMtvCode=" +
                    dbl.sqlString(vMethod.getMTVCode()) +
                    ") b on a.Fsecuritycode = b.FLinkCode" +
                    //-----------------------------------------------------------
                    " where a.FCheckState = 1 and a.FStorageDate=" +
                    dbl.sqlDate(dDate) +
                    " and " + dbl.sqlRight("a.FYearMonth", "2") +
                    "<>'00' and a.FPortCode = " + dbl.sqlString(this.portCode) +
                    ") cs " +
                    //------------------------------------------------------------
                    " left join (select * from " +
                    pub.yssGetTableName("Tb_Stock_SecRecPay") +
                    " where FCheckState = 1 and " + operSql.sqlStoragEve(dDate) +
                    " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " and FTsfTypeCode = " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
                    ") rec on cs.FCSSecurityCode = rec.FSecurityCode and cs.FattrClsCode= rec.FattrClsCode " +
                    (this.invmgrSecField.length() != 0 ?
                     " and cs.FAnalysisCode1 = rec.FAnalysisCode1 " : " ") +
                    (this.brokerSecField.length() != 0 ?
                     " and cs.FAnalysisCode2 = rec.FAnalysisCode2 " : " ");
                if (isACTV) {
                    strSql += " left join ( select mk2.FCsMarketPrice, mk2.FSecurityCode,mk2.FMarketStatus, mk2.FMktValueDate from ";
                } else {
                    strSql += " left join ( select mk2.FCsMarketPrice, mk2.FSecurityCode, mk2.FMktValueDate from ";
                }
                strSql +=
                	//系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                    //" (select max(FMktValueDate) as FMktValueDate, FSecurityCode from " +
                    //pub.yssGetTableName("Tb_Data_MarketValue") +
                    //" where FCheckState = 1" +
                    //" and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
                    //" and FMktValueDate < " + dbl.sqlDate(dDate) +
                    //" group by FSecurityCode ) mk1 join (select " +
                //start modify huangqirong 2013-04-19 bug #7476 取前一天的行情数据
                	"(select dmv2."+
                	//系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                    vMethod.getMktPriceCode();
                if (isACTV) {
                    strSql += " as FCsMarketPrice,dmv2.FSecurityCode, dmv2.FMktValueDate,dmv2.FMarketStatus  from "; //增加行情状态，by Leeyu 2008-10-20
                } else {
                    strSql += " as FCsMarketPrice,dmv2.FSecurityCode, dmv2.FMktValueDate  from ";
                }
                
                strSql += " (select max(FMktValueDate) as FMktValueDate, FSecurityCode, FMktSrcCode from " +  
                		  pub.yssGetTableName("Tb_Data_MarketValue") + " where FCheckState = 1 and FMktValueDate < " + dbl.sqlDate(dDate) +                		  
                          " and FMktSrcCode = "+dbl.sqlString(vMethod.getMktSrcCode()) + " group by FSecurityCode,FMktSrcCode ) dmv1 left join " + pub.yssGetTableName("Tb_Data_MarketValue") + 
                          " dmv2  on dmv1.FMktValueDate = dmv2.FMktValueDate " +
                          " and dmv1.FSecurityCode = dmv2.FSecurityCode " +
                          " and dmv1.FMktSrcCode = dmv2.FMktSrcCode ) mk2 " ;
                //end modify huangqirong 2013-04-19 bug #7476 取前一天的行情数据
                strSql +=
                	//系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                    //pub.yssGetTableName("Tb_Data_MarketValue") +
                    //" where FCheckState = 1 and FMktSrcCode = " +
                    //dbl.sqlString(vMethod.getMktSrcCode()) + ") mk2 " +
                    //" on mk1.FSecurityCode=mk2.FSecurityCode and mk1.FMktValueDate=mk2.FMktValueDate" +
                	//tmpMarketValueTable + " where FMktSrcCode="+dbl.sqlString(vMethod.getMktSrcCode())+" ) mk2 "+ //modify huangqirong 2013-04-23 bug #7476 注释掉
                	//系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                    " ) mk on cs.FCsSecurityCode = mk.FSecurityCode " +
                    //------------------------------------------------------------
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                    
          
                    " left join (select FPortCode, FPortName,FPortCury as FCsPortCury from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where  FCheckState = 1 ) m on  cs.FCsPortCode = m.FPortCode" +
                    
                    
                    //end by lidaolong
                    //------------查询出当天流出的估值增值作为卖出估值增值-------------//
                    //2009-09-14 蒋锦 修改 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                    " LEFT JOIN (SELECT SUM(FMoney) AS FSMoney," +
                    " SUM(FBaseCuryMoney) AS FSBaseCuryMoney," +
                    " SUM(FPortCuryMoney) AS FSPortCuryMoney," +
                    " FSecurityCode, " +
                    (this.invmgrSecField.length() != 0 ?" FAnalysisCode1," : "") +
                    (this.brokerSecField.length() != 0 ?" FAnalysisCode2," : "") +
                    " FAttrClsCode" +
                    " FROM " + pub.yssGetTableName("Tb_Data_Secrecpay") +
                    " WHERE FCheckState = 1" +
                    " AND FTransDate = " + dbl.sqlDate(dDate) +
                    " AND FTsfTypeCode = " + dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
                    " AND FInOut = -1" +
                    " AND FPortCode = " + dbl.sqlString(portCode) +
                    " GROUP BY FTransDate, FPortCode, " +
                    (this.invmgrSecField.length() != 0 ?" FAnalysisCode1," : "") +
                    (this.brokerSecField.length() != 0 ?" FAnalysisCode2," : "") +
                    " FSecurityCode, FAttrClsCode,FTsfTypeCode) smv " +
                    " ON cs.FCSSecurityCode = smv.FSecurityCode AND cs.FAttrClsCode = smv.FAttrClsCode" +
                    (this.invmgrSecField.length() != 0 ? " AND cs.FAnalysisCode1 = smv.FAnalysisCode1" : "") +
                    (this.brokerSecField.length() != 0 ? " AND cs.FAnalysisCode2 = smv.FAnalysisCode2" : "");
                    //-----------------------------------------------------------//
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    boolean isMarketPriceNull = false;
                    dMarketPrice = rs.getDouble("FCsMarketPrice");
                    //---------------sj modify 20081127 MS00024 ---------------------------------
                    if (rs.wasNull()) { //此列是否为sqlNull类型
                        isMarketPriceNull = true;
                    }

                    if (dMarketPrice == 0 && isMarketPriceNull) { //此处得判断，当为权证时不能跳出 QDV4建行2009年3月5日02_B MS00288 modify by leeyu
                        continue;
                    }

                    payPrice = new SecPecPayBean();
                    mktPrice = new ValMktPriceBean();
                    payPrice.setTransDate(dDate);

                  //调试代码未去除  by yanghaiming 20101102
//                    if (rs.getString("FCsSecurityCode").equalsIgnoreCase("2914456Z LN")) {
//                        int ii = 0;
//                    }

                    payPrice.setStrSecurityCode(rs.getString("FCsSecurityCode"));
                    payPrice.setStrPortCode(rs.getString("FCsPortCode"));
                    payPrice.setInvMgrCode(this.invmgrSecField.length() != 0 ?
                                           rs.getString(this.invmgrSecField) :
                                           " ");
                    payPrice.setBrokerCode(this.brokerSecField.length() != 0 ?
                                           rs.getString(this.brokerSecField) :
                                           " ");

                    if (rs.getString("FCsCuryCode") == null) {
                        throw new YssException("请检查证券品种【" +
                                               rs.getString("FCsSecurityCode") +
                                               "】的交易币种设置！");
                    }

                    payPrice.setStrCuryCode(rs.getString("FCsCuryCode"));
                    payPrice.setAttrClsCode(rs.getString("FAttrClsCode")); //sj add 20071204
                    iFactor = rs.getInt("FCsFactor");

                    dBaseRate = 1;
                    if (!rs.getString("FCsCuryCode").equalsIgnoreCase(pub.
                    		getPortBaseCury(this.portCode))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                        dBaseRate = this.getSettingOper().getCuryRate(dDate,
                            vMethod.getBaseRateSrcCode(),
                            vMethod.getBaseRateCode(),
                            vMethod.getPortRateSrcCode(),
                            vMethod.getPortRateCode(),
                            rs.getString("FCsCuryCode"), this.portCode,
                            YssOperCons.YSS_RATE_BASE);
                    }

                    if (rs.getString("FCsPortCury") == null) {
                        throw new YssException("请检查投资组合【" +
                                               rs.getString("FCsPortCode") +
                                               "】的币种设置！");
                    }
                    //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 ---
                    rateOper.getInnerPortRate(dDate, rs.getString("FCsCuryCode"), this.portCode, vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                              vMethod.getPortRateSrcCode(), vMethod.getPortRateCode()); //用通用方法，获取组合汇率
                    dPortRate = rateOper.getDPortRate(); //获取组合汇率
                    //------------------------------------------------------------
                    sCatCode = rs.getString("FCsCatCode");

                    payPrice.setBaseCuryRate(dBaseRate);
                    payPrice.setPortCuryRate(dPortRate);
                    payPrice.setMktPrice(dMarketPrice);
                  //调试代码未去除  by yanghaiming 20101102
//                    if (payPrice.getStrSecurityCode().equalsIgnoreCase("2914456Z LN")) {
//                        int y = 0;
//                    }
		            this.valCommonSecs(rs, payPrice, dMarketPrice);


                    payPrice.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV);
                    payPrice.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV +
                                                  sCatCode);
                    payPrice.checkStateId = 1;
                    //=============用参数来判断行情的停牌状态 by leeyu 2008-10-20  0000486=====
                    if (isACTV) {
                        mktPrice.setMarketStatus(rs.getString("FMarketStatus"));
                    }
                    //===========2008-10-20
                    //如果使用前一日行情估值，插入行情表不使用行情日期，使用估值日期，否则净值表可能取值错误
                    mktPrice.setValDate(dDate);
                    mktPrice.setSecurityCode(payPrice.getStrSecurityCode());
                    mktPrice.setPortCode(portCode);
                    mktPrice.setPrice(YssD.div(dMarketPrice, iFactor, 12));
                    //-------------MS00272 QDV4赢时胜（上海）2009年2月26日01_B
                    mktPrice.setMtvCode(vMethod.getMTVCode()); //设置估值方法
                    //-----------------------------------------------------
                    //------------- MS00265 QDV4建行2009年2月23日01_B  -----
                    mktPrice.setValType("SecsMV"); //设置估值类型为普通类型的证券，与估值界面上的代码相一致。
                    //2009.7.9 蒋锦 添加 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A-- 估值行情中要包括属性分类代码
                    mktPrice.setAttrClsCode(rs.getString("FAttrClsCode"));
                    //2009.07.10 蒋锦 修改 估值行情表增加了属性分类作为主键，所以哈西Key也要添加属性分类
                    hmValPrice.put(mktPrice.getSecurityCode() + "\t" + mktPrice.getAttrClsCode(), mktPrice);
                    //--------MS00264  QDV4中保2009年02月24日01_B  将放置估值值的位置放入各自的通用参数选择项中，视各自的情况而定--//
                    sKey = payPrice.getStrSecurityCode() + "\f" +
                        (this.invmgrSecField.length() != 0 ?
                         (payPrice.getInvMgrCode() + "\f") : "") +
                        (this.brokerSecField.length() != 0 ?
                         (payPrice.getBrokerCode() + "\f") : "") +
                        payPrice.getStrSubTsfTypeCode() + "\f" +
                        (payPrice.getAttrClsCode().length() == 0 ? " " :
                         payPrice.getAttrClsCode());
                    hmResult.put(sKey, payPrice);
                    //--------------------------------------------------------------------------------------------------//
                }

        } catch (Exception ex) {
            throw new YssException("计算基金估值增值出错！", ex);
        }
    }

    private double getTodayMktPrice(java.util.Date dDate, String sMktSrcCode,
                                    String sMktField, String sSecurityCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dResult = 0;
        try {
            strSql = " select " + sMktField + " as FMarketPrice from " +
                pub.yssGetTableName("Tb_Data_MarketValue") +
                " where FCheckState = 1" +
                " and FMktSrcCode = " + dbl.sqlString(sMktSrcCode) +
                " and FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                " and FMktValueDate = " + dbl.sqlDate(dDate);
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                dResult = rs.getDouble("FMarketPrice");
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //对远期品种估值
    private void valFWSecs(ResultSet rs, SecPecPayBean secPay, ValMktPriceBean mktPrice) throws
        YssException {
        double dTmpMoney = 0;
        double dTmpMValue = 0;
        double dTmpAmount = 0;

        try {
            //设置原币核算成本估值增值
            dTmpAmount = rs.getDouble("FStorageAmount");
            //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值
            dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
                                             YssD.div(rs.getDouble("FCsMarketPrice"),
                rs.getInt("FCsFactor"))), 2);
            if (rs.getDouble("FCsMarketPrice") == 0) {
                dTmpMValue = dTmpMoney;
            }
            if (rs.getString("FCsCuryCode").equalsIgnoreCase(rs.getString(
                "FBuyCury"))) { //如果证券币种等于买入币种
                dTmpMoney = rs.getDouble("FStorageCost"); //原币成本
                secPay.setMoney(YssD.sub(YssD.sub(dTmpMoney, dTmpMValue), //成本-市值
                                         rs.getDouble("FBal"))); //-前日估值增值余额

                dTmpMoney = rs.getDouble("FMStorageCost"); //原币成本
                secPay.setMMoney(YssD.sub(YssD.sub(dTmpMoney, dTmpMValue), //成本-市值
                                          rs.getDouble("FMBal"))); //-前日估值增值余额

                dTmpMoney = rs.getDouble("FVStorageCost"); //原币成本
                secPay.setVMoney(YssD.sub(YssD.sub(dTmpMoney, dTmpMValue), //成本-市值
                                          rs.getDouble("FVBal"))); //-前日估值增值余额

            } else if (rs.getString("FCsCuryCode").equalsIgnoreCase(rs.getString(
                "FSaleCury"))) { //如果证券币种等于卖出币种
                dTmpMoney = rs.getDouble("FStorageCost"); //原币成本
                secPay.setMoney(YssD.sub(YssD.div(YssD.sub(dTmpMoney, dTmpMValue),
                                                  rs.getDouble("FCsMarketPrice"),
                                                  2), //(成本-市值)/当日行情
                                         rs.getDouble("FBal"))); //-前日估值增值余额

                dTmpMoney = rs.getDouble("FMStorageCost"); //原币成本
                secPay.setMMoney(YssD.sub(YssD.div(YssD.sub(dTmpMoney,
                    dTmpMValue),
                    rs.getDouble("FCsMarketPrice"),
                    2), //(成本-市值)/当日行情
                                          rs.getDouble("FMBal"))); //-前日估值增值余额

                dTmpMoney = rs.getDouble("FVStorageCost"); //原币成本
                secPay.setVMoney(YssD.sub(YssD.div(YssD.sub(dTmpMoney,
                    dTmpMValue),
                    rs.getDouble("FCsMarketPrice"),
                    2), //(成本-市值)/当日行情
                                          rs.getDouble("FVBal"))); //-前日估值增值余额
            }

            mktPrice.setOtPrice1(YssD.div(rs.getDouble("FStorageCost"), rs.getDouble("FStorageAmount"), 4));

            //设置基础货币估值增值
            secPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                secPay.getMoney(),
                secPay.getBaseCuryRate()));
            secPay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
                secPay.getVMoney(),
                secPay.getBaseCuryRate()));
            secPay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
                secPay.getMMoney(),
                secPay.getBaseCuryRate()));

            //设置组合货币估值增值
            secPay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                secPay.getMoney(),
                secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                rs.getString("FCsCuryCode"), dDate, this.portCode));
            secPay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
                secPay.getVMoney(),
                secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                rs.getString("FCsCuryCode"), dDate, this.portCode));
            secPay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
                secPay.getMMoney(),
                secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                rs.getString("FCsCuryCode"), dDate, this.portCode));

        } catch (Exception e) {
            throw new YssException("系统资产估值,在执行远期资产估值时出现异常!" + "\n", e); //by 曹丞 2009.02.01 远期资产估值异常信息 MS00004 QDV4.1-2009.2.1_09A
        }
    }

    /**
     * 对普通证券的估值
     * @param rs ResultSet
     * @param secPay SecPecPayBean
     * @param dMarketPrice double
     * @param bMVRound boolean：2008.11.13 蒋锦 添加 获取计算估值增值是否进行舍入，true：Round 2，false Round 8
     * @throws YssException
     */
    private void valCommonSecs(ResultSet rs, SecPecPayBean secPay, double dMarketPrice,HashMap hmRsSecOldStor) throws  //add by xuxuming,2010.01.12.增加了一个参数
        YssException {
        double dTmpMoney = 0;
        double dTmpMValue = 0;
        double dTmpAmount = 0;
        boolean bIsRound = false;
        //2009.09.14 蒋锦 添加 流出的估值增值 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        double dSellMValue = 0;
        try {
            //--------------2008.09.04 蒋锦 添加 从通用参数获取计算市值时是否四舍五入两位小数---------------//
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            bIsRound = pubPara.getMVIsRound();
            //---------------------------------------------------------------------------------------//
            //---MS01021  QDV4南方2010年3月11日01_B 财务估值表里的净值与余额表里的成本加估值增值的和有尾差  add by jiangshichao 2010.03.26-----
            String para = pubPara.getSecRecRound();
            int digit = para.equalsIgnoreCase("0")?2:4;//默认小数点后保留2位有效数字
            //---MS01021  QDV4南方2010年3月11日01_B 财务估值表里的净值与余额表里的成本加估值增值的和有尾差 end----------------------------------
            //delete by songjie 2011.02.18 BUG:1031 QDV4工银2011年01月27日01_B
            //把MS01835对应的代码注释掉，若当日换股业务流出证券的库存为零，不把前一日该证券的证券应收应付库存数据清零
//            //add by fangjiang 2010.10.22 MS01835 QDV4赢时胜（深圳）2010年10月8日01_B 
//            dTmpAmount = rs.getDouble("FStorageAmount");
//            if (dTmpAmount == 0) {
//            	ResultSet rs1 = null;
//            	try {
//            		String securityCode = rs.getString("FCsSecurityCode");
//                	String sql = " select * from " + pub.yssGetTableName("Tb_Data_Integrated") 
//                				+ " where fcheckstate = 1 and FTradeTypeCode = '80' and finouttype = -1 and foperdate = "
//                				+ dbl.sqlDate(dDate) + " and FSecurityCode = " + dbl.sqlString(securityCode);
//                	rs1 = dbl.openResultSet(sql);
//                    while (rs1.next()) { //换股，流出股全部流出
//                    	String update_sql = " update " + pub.yssGetTableName("Tb_Stock_Secrecpay") + " set FBal = 0, FMBal = 0, FVBal = 0, "
//                    						+ " FPortCuryBal = 0, FMPortCuryBal = 0, FVPortCuryBal = 0, FBaseCuryBal = 0, FMBaseCuryBal = 0, FVBaseCuryBal = 0 "
//                    						+ " where FTsfTypeCode = '09' and FSubTsfTypeCode = '09EQ' and FSecurityCode = " + dbl.sqlString(securityCode)
//                    						+ " and FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate,-1));
//                    	dbl.executeSql(update_sql);
//                    	return;
//                    }
//            	} catch (Exception e) {
//            		throw new YssException("处理换股时出现异常!" + "\n", e);
//            	} finally {
//            		dbl.closeResultSetFinal(rs1);
//            	}
//            }           
            //--------------------------------------
            //设置原币核算成本估值增值
            dTmpMoney = rs.getDouble("FStorageCost"); //原币成本
            dTmpAmount = rs.getDouble("FStorageAmount");
            dSellMValue = rs.getDouble("FSMoney");
            if (bIsRound) {
                //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值 BUG:0000416
                dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
                                                 YssD.div(dMarketPrice,
                    rs.getInt("FCsFactor"))), 2);
            } else {
                dTmpMValue = YssD.mul(dTmpAmount,
                                      YssD.div(dMarketPrice,
                                               rs.getInt("FCsFactor")));
            }
            if (dMarketPrice == 0) {
                dTmpMValue = dTmpMoney;
            }
            
           
            
            
            //===add by xuxuming.2010.01.12.MS00902 指数信息调整后，当取不到昨日估增余额时，重新获取另一属性代码的估增余额==========
            String strAttrCode = rs.getString("FAttrClsCode");// 所属分类	
            double dFBal = 0;
            double dMBal = 0;
            double dVBal = 0;
            dFBal=rs.getDouble("FBal");
			dMBal=rs.getDouble("FMBal");
			dVBal=rs.getDouble("FVBal");
			if (strAttrCode != null
					&& (strAttrCode.equals("CEQ") || strAttrCode
							.equals("IDXEQ")) && hmRsSecOldStor !=null) {				
				if(dFBal==0&&dMBal==0&&dVBal==0){//都为零，表明首次调整后无昨日库存，取另一属性的昨日库存
					String strKey="";
					
					/**Start 20130702 modified by liubo.Bug #8308.QDV4建行2013年06月18日01_B
					 * 当CEQ和IDXEQ在指数信息调整界面做转换时，实际上产生的综合业务已经将证券库存和证券应收应付库存做了流入
					 * 在这里不需要再重复做取对方库存的动作*/
//					strKey=rs.getString("FStorageDate")+"\t"+rs.getString("FCSSecuritycode")+"\t"+rs.getString("FCSPortCode")+"\t"+(strAttrCode.equals("CEQ")?"IDXEQ":"CEQ");//取另一属性的值
					strKey=rs.getString("FStorageDate")+"\t"+rs.getString("FCSSecuritycode")+"\t"
										+rs.getString("FCSPortCode")+"\t"+strAttrCode;
					/**Start 20130702 modified by liubo.Bug #8308.QDV4建行2013年06月18日01_B*/
					String tmpRsBal=(String)hmRsSecOldStor.get(strKey);
					if(tmpRsBal!=null&&tmpRsBal.trim().length()>0){
						String[] bufRsBal = tmpRsBal.split("\t");
						dFBal=new Double(bufRsBal[0]).doubleValue();
						dMBal=new Double(bufRsBal[1]).doubleValue();
						dVBal=new Double(bufRsBal[2]).doubleValue();
					}
				}
			}
            //===========end====================================================
            //MS00757  QDV4华夏2009年10月19日01_B 伪差问题   fanghaoln 20091028 小数位数不一样产生误差
            if (rs.getString("FCatCode").equalsIgnoreCase("OP")&& rs.getString("FSubCatCode").equalsIgnoreCase("OP03")) { //今天的市值没有到两位昨天取了两位小数产生
                secPay.setMoney(YssD.sub(YssD.round(YssD.sub(dTmpMValue, dTmpMoney), 2), //市值-成本
                        rs.getDouble("FBal"))); //-前日估值增值余额
                secPay.setMoneyF(YssD.sub(YssD.round(YssD.sub(dTmpMValue, dTmpMoney), 2),
                        rs.getDouble("FBal")));
            }else{
	            //2009.09.14 蒋锦 添加 修改公式 市值-成本-(前日估值增值余额-流出估值增值) MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
	            secPay.setMoney(
	                YssD.sub(
	                    YssD.sub(dTmpMValue,
	                             dTmpMoney), //市值-成本
	                    YssD.sub(
	                        /*rs.getDouble("FBal")*/dFBal,           //edit by xuxuming,2010.01.12.MS00902
	                        dSellMValue))); //-(前日估值增值余额-流出估值增值)
            //-----------2008.11.13 蒋锦 添加-------------//
            //储存保留8位小数的原币
            //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
            secPay.setMoneyF(
                YssD.sub(
                    YssD.sub(dTmpMValue,
                             dTmpMoney),
                    YssD.sub(
                        rs.getDouble("FBal"),
                        dSellMValue)));
            //-------------------------------------------//
            }
            //设置原币估值成本估值增值
            dTmpMoney = rs.getDouble("FVStorageCost"); //原币成本
            dTmpAmount = rs.getDouble("FStorageAmount");
            if (bIsRound) {
                //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值 BUG:0000416
                dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
                                                 YssD.div(dMarketPrice,
                    rs.getInt("FCsFactor"))), 2);
            } else {
                dTmpMValue = YssD.mul(dTmpAmount,
                                      YssD.div(dMarketPrice,
                                               rs.getInt("FCsFactor")));
            }
            if (dMarketPrice == 0) {
                dTmpMValue = dTmpMoney;
            }
          //MS00757  QDV4华夏2009年10月19日01_B 伪差问题   fanghaoln 20091028 小数位数不一样产生误差
            if (rs.getString("FCatCode").equalsIgnoreCase("OP")&& rs.getString("FSubCatCode").equalsIgnoreCase("OP03")) { //今天的市值没有到两位昨天取了两位小数产生
                secPay.setVMoney(YssD.sub(YssD.round(YssD.sub(dTmpMValue, dTmpMoney), 2), //市值-成本
                        rs.getDouble("FVBal"))); //-前日估值增值余额
            }else{
            secPay.setVMoney(
                YssD.sub(
                    YssD.sub(dTmpMValue,
                             dTmpMoney),
                    YssD.sub(
                        /*rs.getDouble("FVBal")*/dVBal,//edit by xxm,MS00902
                        dSellMValue)));
            }
          //--------------------------------------end MS00757-------------------------------------------------------------------------------------------------
            //设置原币管理成本的估值增值
            dTmpMoney = rs.getDouble("FMStorageCost"); //原币成本
            dTmpAmount = rs.getDouble("FStorageAmount");
            if (bIsRound) {
                //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值 BUG:0000416
                dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
                                                 YssD.div(dMarketPrice,
                    rs.getInt("FCsFactor"))), 2);
            } else {
                dTmpMValue = YssD.mul(dTmpAmount,
                                      YssD.div(dMarketPrice,
                                               rs.getInt("FCsFactor")));
            }
            if (dMarketPrice == 0) {
                dTmpMValue = dTmpMoney;
            }
            //MS00757  QDV4华夏2009年10月19日01_B 伪差问题   fanghaoln 20091028 小数位数不一样产生误差
            if (rs.getString("FCatCode").equalsIgnoreCase("OP")&& rs.getString("FSubCatCode").equalsIgnoreCase("OP03")) { //今天的市值没有到两位昨天取了两位小数产生
                secPay.setMMoney(YssD.sub(YssD.round(YssD.sub(dTmpMValue, dTmpMoney), 2), //市值-成本
                        rs.getDouble("FMBal"))); //-前日估值增值余额
            }
            else{
            secPay.setMMoney(
                YssD.sub(
                    YssD.sub(dTmpMValue,
                             dTmpMoney),
                    YssD.sub(
                        /*rs.getDouble("FMBal")*/dMBal,//edit by xxm,MS00902
                        dSellMValue)));
            }
          //--------------------------------------end MS00757-------------------------------------------------------------------------------------------------
            if (dTmpAmount != 0) {
                //设置基础货币估值增值
                secPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                    secPay.getMoney(),
                    secPay.getBaseCuryRate()));
                //-----------2008.11.13 蒋锦 添加-------------//
                //储存保留8位小数的基础货币
                //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                double dbBaseMoney = this.getSettingOper().calBaseMoney(
                    secPay.getMoney(),
                    secPay.getBaseCuryRate(), 8);
                secPay.setBaseCuryMoneyF(dbBaseMoney);
                //--------------------------------------------//
                secPay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
                    secPay.getVMoney(),
                    secPay.getBaseCuryRate()));
                secPay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
                    secPay.getMMoney(),
                    secPay.getBaseCuryRate()));

                //设置组合货币估值增值
                secPay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                    secPay.getMoney(),
                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    rs.getString("FCsCuryCode"), dDate, this.portCode,digit));//modify by jiangshichao 2010.03.16 QDV4南方2010年3月11日01_B MS01021 组合货币的市值尾差问题，这里保留小数点4位有效数
                //-----------2008.11.13 蒋锦 添加-------------//
                //储存保留8位小数的本位币
                //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                double dbPortMoney = this.getSettingOper().calPortMoney(
                    secPay.getMoney(),
                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                    //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    rs.getString("FCsCuryCode"), dDate, this.portCode, 8);
                secPay.setPortCuryMoneyF(dbPortMoney);
                //--------------------------------------------//
                secPay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
                    secPay.getVMoney(),
                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    rs.getString("FCsCuryCode"), dDate, this.portCode,digit));//modify by jiangshichao 2010.03.16 QDV4南方2010年3月11日01_B MS01021 组合货币的市值尾差问题，这里保留小数点4位有效数
                secPay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
                    secPay.getMMoney(),
                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    rs.getString("FCsCuryCode"), dDate, this.portCode,digit));//modify by jiangshichao 2010.03.16 QDV4南方2010年3月11日01_B MS01021 组合货币的市值尾差问题，这里保留小数点4位有效数
            } else { //如果库存数量为0的情况下，直接冲减原来的估值增值余额 fazmm20070927
                //设置基础货币估值增值
            	//------ modify by wangzuochun 2010-06-18 MS01097    债券转托管，产生的原债券流入那笔负的估值增值基础货币、组合金额不对    QDV4国内（测试）2010年04月16日01_B    
            	if (getDevTrustBond(rs)){
            		secPay.setBaseCuryMoney(0);
                    secPay.setVBaseCuryMoney(0);
                    secPay.setMBaseCuryMoney(0);

                    //设置组合货币估值增值
                    secPay.setPortCuryMoney(0);
                    secPay.setVPortCuryMoney(0);
                    secPay.setMPortCuryMoney(0);

                    //---------2008.11.14 蒋锦 添加 保留8位小数的基础货币和本位币-------------//
                    //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                    secPay.setBaseCuryMoneyF(0);
                    secPay.setPortCuryMoneyF(0);
                    //-------------------------------------------------------------------//
            	}
            	else{
            		secPay.setBaseCuryMoney( -rs.getDouble("Fbasecurybal"));
                    secPay.setVBaseCuryMoney( -rs.getDouble("FVbasecurybal"));
                    secPay.setMBaseCuryMoney( -rs.getDouble("FMbasecurybal"));

                    //设置组合货币估值增值
                    secPay.setPortCuryMoney( -rs.getDouble("Fportcurybal"));
                    secPay.setVPortCuryMoney( -rs.getDouble("FVportcurybal"));
                    secPay.setMPortCuryMoney( -rs.getDouble("FMportcurybal"));

                    //---------2008.11.14 蒋锦 添加 保留8位小数的基础货币和本位币-------------//
                    //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                    secPay.setBaseCuryMoneyF( -rs.getDouble("FbasecurybalF"));
                    secPay.setPortCuryMoneyF( -rs.getDouble("FportcurybalF"));
                    //-------------------------------------------------------------------//
            	}
                //------ MS01097    债券转托管，产生的原债券流入那笔负的估值增值基础货币、组合金额不对    QDV4国内（测试）2010年04月16日01_B  -------//
            }
            
            //----- 如果计算市值时不进行四舍五入两位小数，则只对本币、基础货币计算是以中间过程来计算，而原币计算仍然已保留2位计算   add 2011.12.7 BUG3373 BUG3373建行年终结转在生成月末损益结转凭证时有问题
            if(!bIsRound){
            	 dTmpMoney = rs.getDouble("FStorageCost"); //原币成本
                 dTmpAmount = rs.getDouble("FStorageAmount");
                 dSellMValue = rs.getDouble("FSMoney");
                 
                 if (dMarketPrice == 0) {
                     dTmpMValue = dTmpMoney;
                 }else{
                	 dTmpMValue = YssD.round(YssD.mul(dTmpAmount,YssD.div(dMarketPrice,rs.getInt("FCsFactor"))), 2);
                 }
                 
                 
                 if (rs.getString("FCatCode").equalsIgnoreCase("OP")&& rs.getString("FSubCatCode").equalsIgnoreCase("OP03")) { //今天的市值没有到两位昨天取了两位小数产生
                     secPay.setMoney(YssD.sub(YssD.round(YssD.sub(dTmpMValue, dTmpMoney), 2), //市值-成本
                             rs.getDouble("FBal"))); //-前日估值增值余额
                     secPay.setMoneyF(YssD.sub(YssD.round(YssD.sub(dTmpMValue, dTmpMoney), 2),
                             rs.getDouble("FBal")));
                 }else{
     	            secPay.setMoney(YssD.sub(YssD.sub(dTmpMValue,dTmpMoney), //市值-成本
     	                    YssD.sub(dFBal,dSellMValue))); 
                 secPay.setMoneyF(YssD.sub(YssD.sub(dTmpMValue,dTmpMoney),
                            YssD.sub(rs.getDouble("FBal"),dSellMValue)));
            
                 }
                 
                 //设置原币估值成本估值增值
                 dTmpMoney = rs.getDouble("FVStorageCost"); //原币成本
                 dTmpAmount = rs.getDouble("FStorageAmount");

                 if (dMarketPrice == 0) {
                     dTmpMValue = dTmpMoney;
                 }else{
                	 dTmpMValue = YssD.round(YssD.mul(dTmpAmount,YssD.div(dMarketPrice,rs.getInt("FCsFactor"))), 2);
                 }
               //MS00757  QDV4华夏2009年10月19日01_B 伪差问题   fanghaoln 20091028 小数位数不一样产生误差
                 if (rs.getString("FCatCode").equalsIgnoreCase("OP")&& rs.getString("FSubCatCode").equalsIgnoreCase("OP03")) { //今天的市值没有到两位昨天取了两位小数产生
                     secPay.setVMoney(YssD.sub(YssD.round(YssD.sub(dTmpMValue, dTmpMoney), 2), //市值-成本
                             rs.getDouble("FVBal"))); //-前日估值增值余额
                 }else{
                 secPay.setVMoney(YssD.sub(YssD.sub(dTmpMValue,dTmpMoney),
                         YssD.sub(dVBal,dSellMValue)));
                 }
                 //设置原币管理成本的估值增值
                 dTmpMoney = rs.getDouble("FMStorageCost"); //原币成本
                 dTmpAmount = rs.getDouble("FStorageAmount");
                 
                 if (dMarketPrice == 0) {
                     dTmpMValue = dTmpMoney;
                 }else{
                	 dTmpMValue = YssD.round(YssD.mul(dTmpAmount,YssD.div(dMarketPrice,rs.getInt("FCsFactor"))), 2);
                 }
                
                 if (rs.getString("FCatCode").equalsIgnoreCase("OP")&& rs.getString("FSubCatCode").equalsIgnoreCase("OP03")) { //今天的市值没有到两位昨天取了两位小数产生
                     secPay.setMMoney(YssD.sub(YssD.round(YssD.sub(dTmpMValue, dTmpMoney), 2), //市值-成本
                             rs.getDouble("FMBal"))); //-前日估值增值余额
                 }
                 else{
                 secPay.setMMoney(YssD.sub(YssD.sub(dTmpMValue,dTmpMoney),
                         YssD.sub(dMBal,dSellMValue)));
                 }
                 
            }
            
            
            
        } catch (Exception e) {
            throw new YssException("系统资产估值,在执行普通证券估值时出现异常!" + "\n", e); //by 曹丞 2009.02.01 普通证券估值异常信息 MS00004 QDV4.1-2009.2.1_09A
        }
    }

    //此次修改，是将配股浮动盈亏的方法添加到证券的浮动盈亏方法中处理，原因是如果配股与证券的浮动盈亏单独估值的话，证券在估值时会重复删除掉0P的数据
    //此方法是从ValRightsIssueMV.java中复制过来，由李钰 整理 byleeyu 20090307 缺点是速度很慢 QDV4建行2009年3月5日02_B MS00288
    public SecPecPayBean getValRightIssUeMV(String sSecurityCode, MTVMethodBean vMethod) throws YssException {

        String strSql = "";
        //MTVMethodBean vMethod = null;
        ResultSet rs = null;
        int iFactor = 1; //报价因子
        String sKey = "", sCatCode = "";

        double dBaseRate = 1;
        double dPortRate = 1;
        double dMarketPrice = 0;

        double dTmpMoney = 0;
        double dTmpMValue = 0;
        double dTmpAmount = 0;

        SecPecPayBean secPay = null;

        HashMap hmResult = new HashMap();

        StringBuffer buf = new StringBuffer();
        boolean bIsRound = false;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------
        try {
            //--------------2008.09.04 蒋锦 添加 从通用参数获取计算市值时是否四舍五入两位小数---------------//
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            bIsRound = pubPara.getMVIsRound();
            //---------------------------------------------------------------------------------------//

//         for (int i = 0; i < mtvBeans.size(); i++) {
//            vMethod = (MTVMethodBean) mtvBeans.get(i);

            //edit by yanghaiming 20101101 QDV411建行2010年09月29日01_A
            strSql = " select cs.*,FBal,FMBal,FVBal,FBaseCuryBal,FMBaseCuryBal,FVBaseCuryBal,FPortCuryBal,FMPortCuryBal,FVPortCuryBal," +
                " mk.FCsMarketPrice,mkri.FRiMktPrice, mkri.FRiMktDate, mk.FMktValueDate, m.FPortCury from (select a.FStorageDate, a.FSecurityCode as FCsSecurityCode," +
                " FStorageAmount, FStorageCost, FMStorageCost, FVStorageCost," +
                " FBaseCuryCost as FCsBaseCuryCost, FMBaseCuryCost as FCsMBaseCuryCost, FVBaseCuryCost as FCsVBaseCuryCost," +
                " FPortCuryCost as FCsPortCuryCost, FMPortCuryCost as FCsMPortCuryCost, FVPortCuryCost as FCsVPortCuryCost," +
                " ri.FSecurityCode as FRiSecurityCode,ri.FBeginScriDate,ri.FEndScriDate,ri.FBeginTradeDate,ri.FEndTradeDate,ri.FRIPrice" +
                //判断是否配置分析代码，杨
                (this.invmgrSecField.length() != 0 ?
                 ("," + this.invmgrSecField) : " ") +
                (this.brokerSecField.length() != 0 ?
                 ("," + this.brokerSecField) : " ") +
                ",a.FPortCode as FCsPortCode,a.FAttrClsCode as FAttrClsCode, sec.FTradeCury as FCsCuryCode, sec.FCatCode as FCsCatCode, " + //sj 20071204 add new field for new key
                " sec.FSubCatCode as FCsSubCatCode, sec.FFactor as FCsFactor,FCatCode,FSubCatCode from " + // wdy 添加表别名a
//                pub.yssGetTableName("tb_stock_security") + " a" +//delete by xuxuming,20091217.此处查询出证券库存所有数据后再去关联其它表，效率极底
                //------------------------------------------------------------
              
                //=====更改为，将查询证券库存的条件移到此处，查询出符合条件的记录再去关联其它表,edit by xuxuming,20091217===============
                " ( select * from "+pub.yssGetTableName("tb_stock_security")+
                " where FCheckState = 1 and FStorageDate=" +
                dbl.sqlDate(dDate) +
                " and " + dbl.sqlRight("FYearMonth", "2") +
                "<>'00' and FPortCode = " + dbl.sqlString(this.portCode) +" and FSecurityCode = " + dbl.sqlString(sSecurityCode) +") a"+
                //==============end,xuxuming===========================================
                " left join (select FSecurityCode, FSecurityName, FStartDate, FCatCode, FSubCatCode, FTradeCury,FFactor from " +
                pub.yssGetTableName("tb_para_security") +
//                  " where FCheckState=1 and FCatCode = 'OP' and FSubCatCode = 'OP02') sec on a.FSecurityCode = sec.FSecurityCode" +
                " where FCheckState=1 and FCatCode = 'OP' and FSecurityCode=" + dbl.sqlString(sSecurityCode) + " ) sec on a.FSecurityCode = sec.FSecurityCode" +
                //------------------------------------------------------------
                " left join (select FLinkCode from " +
                pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                " where FCheckState = 1 and FMtvCode=" +
                dbl.sqlString(vMethod.getMTVCode()) +
                " and FLinkCode = " + dbl.sqlString(sSecurityCode) + ") b on a.Fsecuritycode = b.FLinkCode" +
                //------------------------------------------------------------
                " left join (select * from " +
                pub.yssGetTableName("Tb_Data_RightsIssue") +
                " where FCheckState = 1 and " + dbl.sqlDate(dDate) +
                " between FExRightDate and " +
                //sj 20090429 MS00368  QDV4交银施罗德2009年4月9日01_B 配股权益截止日小于业务资料中的认购行权日时估值会报错
//                 dbl.sqlDateAdd("FExpirationDate", "-1") +
                " FExpirationDate " + //缴款截至日可以等于除权日
                //-----------------------------------------------------------------------------------
                " and FTSecurityCode = " + dbl.sqlString(sSecurityCode) +") ri on a.Fsecuritycode = ri.FTSecurityCode" +
                //-----------------------------------------------------------
                /*                //delete by xuxuming,20091217.把查询条件提前。原先的程序将查询条件放在关联的外面，导致效率极低
                " where a.FCheckState = 1 and a.FStorageDate=" +
                dbl.sqlDate(dDate) +
                " and " + dbl.sqlRight("a.FYearMonth", "2") +
                "<>'00' and a.FPortCode = " + dbl.sqlString(this.portCode) +
                " and a.FSecurityCode =" + dbl.sqlString(sSecurityCode) +    */
                ") cs " +
                //------------------------------------------------------------
                " left join (select * from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " where FCheckState = 1 and " + operSql.sqlStoragEve(dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
                " and FSecurityCode = " + dbl.sqlString(sSecurityCode) + ") rec on cs.FCSSecurityCode = rec.FSecurityCode and cs.FAttrClsCode= rec.FAttrClsCode " + //add 添加相关的属性分类代码到这里 BUG：000437
                (this.invmgrSecField.length() != 0 ?
                 " and cs." + this.invmgrSecField + " = rec.FAnalysisCode1 " : " ") +//edit by yanghaiming 20101109 此处的分析代码不可写死
                (this.brokerSecField.length() != 0 ?
                 " and cs." + this.brokerSecField + " = rec.FAnalysisCode2 " : " ") +
                //------------------------------------------------------------这段是取配股的原股行情，当估值日期不在交易日期内时使用
                //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                //" left join ( select mk2.FCsMarketPrice as FRiMktPrice, mk2.FSecurityCode, mk1.FMktValueDate AS FRiMktDate from " +
                //" (select max(FMktValueDate) as FMktValueDate, FSecurityCode from " +
                //pub.yssGetTableName("Tb_Data_MarketValue") +
                //" where FCheckState = 1" +
                //" and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
                //" and FMktValueDate <= " + dbl.sqlDate(dDate) +
                //" group by FSecurityCode ) mk1 join (select " +
                //vMethod.getMktPriceCode() +
                //" as FCsMarketPrice,FSecurityCode, FMktValueDate  from " +
                //pub.yssGetTableName("Tb_Data_MarketValue") +
                //" where FCheckState = 1 and FMktSrcCode = " +
                //dbl.sqlString(vMethod.getMktSrcCode()) + ") mk2 " +
                //" on mk1.FSecurityCode=mk2.FSecurityCode and mk1.FMktValueDate=mk2.FMktValueDate" +
                " left join ( select " + vMethod.getMktPriceCode() + " as FRiMktPrice, FSecurityCode, FMktValueDate AS FRiMktDate from " +                
                tmpMarketValueTable + " where FMktSrcCode="+dbl.sqlString(vMethod.getMktSrcCode()) +
                //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                " ) mkri on cs.FRiSecurityCode = mkri.FSecurityCode " +
                //------------------------------------------------------------这段是取配股权证的行情
                //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                //" left join ( select mk2.FCsMarketPrice, mk2.FSecurityCode, mk1.FMktValueDate from " +
                //" (select max(FMktValueDate) as FMktValueDate, FSecurityCode from " +
                //pub.yssGetTableName("Tb_Data_MarketValue") +
                //" where FCheckState = 1" +
                //" and FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
                //" and FMktValueDate <= " + dbl.sqlDate(dDate) +
                //" group by FSecurityCode ) mk1 join (select " +
                //vMethod.getMktPriceCode() +
                //" as FCsMarketPrice,FSecurityCode, FMktValueDate  from " +
                //pub.yssGetTableName("Tb_Data_MarketValue") +
                //" where FCheckState = 1 and FMktSrcCode = " +
                //dbl.sqlString(vMethod.getMktSrcCode()) + ") mk2 " +
                //" on mk1.FSecurityCode=mk2.FSecurityCode and mk1.FMktValueDate=mk2.FMktValueDate" +
                " left join ( select "+ vMethod.getMktPriceCode() +" as FCsMarketPrice, FSecurityCode, FMktValueDate from " +
                tmpMarketValueTable + " where FMktSrcCode="+dbl.sqlString(vMethod.getMktSrcCode()) +
                //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                " and FSECURITYCODE = " + dbl.sqlString(sSecurityCode) + ") mk on cs.FCsSecurityCode = mk.FSecurityCode " +
                //------------------------------------------------------------
                " left join (select * from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1" +
                ") m on  cs.FCsPortCode = m.FPortCode " ;
//                +    //delete by xuxuming,20091218.去掉 这个条件，正股无行情时，权证有行情，也要查出来。MS00869
//                "where mkri.FRiMktDate is not null "; //这里添加对无行情的权证处理　QDV4建行2009年3月5日01_B MS00287 by leeyu 20090309

            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	//===========add by xuxuming,20091218.如果权证和正股都没有行情，则不估值 MS00869===============
                if(rs.getDate("FMktValueDate") == null &&rs.getDate("FRiMktDate") == null){
                	continue;//跳出，都没有行情，就不估值了
                }
                //=====================end=====================================================
                secPay = new SecPecPayBean();
                mktPrice = new ValMktPriceBean();
                secPay.setTransDate(dDate);

                secPay.setStrSecurityCode(rs.getString("FCsSecurityCode"));
               
                secPay.setStrPortCode(rs.getString("FCsPortCode"));
                secPay.setInvMgrCode(this.invmgrSecField.length() != 0 ?
                                     rs.getString(this.invmgrSecField) : " ");
                secPay.setBrokerCode(this.brokerSecField.length() != 0 ?
                                     rs.getString(this.brokerSecField) : " ");
                if (rs.getString("FCsCuryCode") == null) {
                    throw new YssException("请检查证券品种【" +
                                           rs.getString("FCsSecurityCode") +
                                           "】的交易币种设置！");
                }

                secPay.setStrCuryCode(rs.getString("FCsCuryCode"));
                secPay.setAttrClsCode(rs.getString("FAttrClsCode")); // sj add 20071204 for new key
                iFactor = rs.getInt("FCsFactor");

                dBaseRate = 1;
                if (!rs.getString("FCsCuryCode").equalsIgnoreCase(pub.
                		getPortBaseCury(this.portCode))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                    dBaseRate = this.getSettingOper().getCuryRate(dDate,
                        vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                        vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                        rs.getString("FCsCuryCode"), this.portCode,
                        YssOperCons.YSS_RATE_BASE);
                }

                if (rs.getString("FPortCury") == null) {
                    throw new YssException("请检查投资组合【" +
                                           rs.getString("FCsPortCode") +
                                           "】的币种设置！");
                }
//               dPortRate = this.getSettingOper().getCuryRate(dDate,
//                     vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
//                     vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
//                     rs.getString("FPortCury"), this.portCode,
//                     YssOperCons.YSS_RATE_PORT);
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 ---

                rateOper.getInnerPortRate(dDate, rs.getString("FCsCuryCode"), this.portCode, vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                          vMethod.getPortRateSrcCode(), vMethod.getPortRateCode()); //用通用方法，获取组合汇率
                dPortRate = rateOper.getDPortRate(); //获取组合汇率
                
				// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
				// 则默认组合汇率为1
				if (dPortRate == 0) {
					dPortRate = 1;
				}
				// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
				// 则默认组合汇率为1
				// ------------------------------------------------------------


                sCatCode = rs.getString("FCsCatCode");

                secPay.setBaseCuryRate(dBaseRate);
                secPay.setPortCuryRate(dPortRate);

                //--------------------2008.07.15 蒋锦 修改--------------------//
                //行情数据不能依靠权证的交易期间来进行判断是取正股行情还是取权证行情，
                //如果估值当天有权证行情就应该取权证的行情，否则要看正股行情日期和权证行情日期谁最接近估值日期，取最接近的行情
                //优先取配股权证的行情
                java.util.Date mktDate = null;       
                
//update by guolongchao STORY 2233  QDV4中国银行2012年2月13日01_A --------------------------------start 
                
//                if (rs.getDate("FMktValueDate") != null &&rs.getDate("FRiMktDate") != null &&    //MS00869 edit by xuxuming,20091218.加上非空判断
//                    rs.getDate("FMktValueDate").compareTo(rs.getDate(
//                        "FRiMktDate")) >= 0) {
//                    dMarketPrice = rs.getDouble("FCsMarketPrice");
//                    mktDate = rs.getDate("FMktValueDate");
//                } else if(rs.getDate("FMktValueDate") != null &&rs.getDate("FRiMktDate") == null){//add by xuxuming,MS00869.若正股在除权日之前没有行情资料，SQL语句取不到权证当日的行情 
//                	dMarketPrice = rs.getDouble("FCsMarketPrice");              //正股没有行情，权证本身有行情，取权证的行情
//                    mktDate = rs.getDate("FMktValueDate");
//                }
//                else 
//                {
//                    dMarketPrice = YssD.sub(rs.getDouble("FRiMktPrice"),
//                    		rs.getDouble("FRIPrice"));
//                    mktDate = rs.getDate("FRiMktDate");
//                }
                
                if(rs.getString("FRiSecurityCode") == null){
                	//如果配股权证没有关联出正股，说明配股权证进行了换股等处理。
                	//可能出现该情况的有：因为配股权证上市前为临时配股权证代码，上市后权益信息中权证代码调整为正式上市权证代码，
                	//同时将原有临时权证的库存通过换股处理转换成正式配股权证的库存来计算正式配股权证的估值增值
                	//而原有的临时配股权证因权益信息中的配股权证代码已经更换，因此会关联不出正股代码，从而会导致报错。
                	//易方达等客户在STORY 2233调整后会遇到此问题。
                	//panjunfang modify 20120628
                	continue;
                }else{
                    //当估值日期在配股权证的交易日期内
                    if (YssFun.dateDiff(rs.getDate("FBeginTradeDate"), dDate)>=0&&YssFun.dateDiff(dDate, rs.getDate("FEndTradeDate"))>=0) 
                    { 
                    	//若配股权证有行情,则取配股权证的行情来估值
                    	if(rs.getDate("FMktValueDate") != null && YssFun.dateDiff(rs.getDate("FMktValueDate"), dDate)>=0&&YssFun.dateDiff(rs.getDate("FBeginTradeDate"), rs.getDate("FMktValueDate"))>=0)
                    	{
                    		dMarketPrice = rs.getDouble("FCsMarketPrice");
                    		//mktDate = rs.getDate("FMktValueDate");
    						mktDate=dDate;//add by guolongchao 20120503 BUG4457配股权证估值行情获取错误 
                    	}
                    	else//若配股权证无行情,取正股的行情来估值
                    	{
                    		dMarketPrice = YssD.sub(rs.getDouble("FRiMktPrice"),rs.getDouble("FRIPrice"));
                            //mktDate = rs.getDate("FRiMktDate");
    						mktDate=dDate;//add by guolongchao 20120503 BUG4457配股权证估值行情获取错误 
                    	}
                    }
                    else //当估值日期不在配股权证的交易日期内，取正股的行情来估值
                    {
                        dMarketPrice = YssD.sub(rs.getDouble("FRiMktPrice"),rs.getDouble("FRIPrice"));
                        //mktDate = rs.getDate("FRiMktDate");
                        mktDate=dDate;//add by guolongchao 20120503 BUG4457配股权证估值行情获取错误 
                    }   
                }           
                //update by guolongchao STORY 2233  QDV4中国银行2012年2月13日01_A --------------------------------end  
                
//               if (YssFun.dateDiff(rs.getDate("FBeginTradeDate"), dDate) >= 0 && //包括除权日当日.sj edit 20080714
//                   YssFun.dateDiff(dDate, rs.getDate("FEndTradeDate")) >= 0) { //当估值日期在配股权证的交易日期内，取配股权证的行情来估值
//                  dMarketPrice = rs.getDouble("FCsMarketPrice");
//               }else{
//                  dMarketPrice = YssD.sub(rs.getDouble("FRiMktPrice"),rs.getDouble("FRIPrice"));//原股的市场价格-配股价格 bug Num 0000303，恢复原有编码。sj edit 20080714
//                  //dMarketPrice = rs.getDouble("FRiMktPrice");//在计算市值时只需要在之后减去前日的库存成本，不用再减一次配股价格。sj edit 20080407
//               }
                //------------------------------------------//
                //当配股权证的市价比配股价低时，该配股权证的估值增值应为0，不能为负数 胡坤 20080702
                if (dMarketPrice < 0) {
                    dMarketPrice = 0;
                }
                secPay.setMktPrice(dMarketPrice);
                //设置原币核算成本估值增值
                dTmpMoney = rs.getDouble("FStorageCost"); //原币成本
                dTmpAmount = rs.getDouble("FStorageAmount");
                //是否四舍五入
                if (bIsRound) {
                    //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值  BUG:0000416
                    dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
                        YssD.div(dMarketPrice,
                                 rs.getInt("FCsFactor"))), 2);
                } else {
                    dTmpMValue = YssD.mul(dTmpAmount,
                                          YssD.div(dMarketPrice,
                        rs.getInt("FCsFactor")));
                }
                if (dMarketPrice == 0) {
                    dTmpMValue = dTmpMoney;
                }
                //fanghaoln 20091117 MS00808 QDV4建行2009年11月12日01_B 保留两位小数使结果统一
//              secPay.setMoney(YssD.round(YssD.sub(YssD.sub(dTmpMValue, dTmpMoney), //市值-成本
//                                       rs.getDouble("FBal")),2)); //-前日估值增值余额
                secPay.setMoney(YssD.sub(YssD.sub(dTmpMValue, dTmpMoney),
                        rs.getDouble("FVBal")));
              //------------------------end MS00808--------------------------------------------


                //设置原币估值成本估值增值
                dTmpMoney = rs.getDouble("FVStorageCost"); //原币成本
                dTmpAmount = rs.getDouble("FStorageAmount");
                if (bIsRound) {
                    //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值 BUG:0000416
                    dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
                        YssD.div(dMarketPrice,
                                 rs.getInt("FCsFactor"))), 2);
                } else {
                    //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值 BUG:0000416
                    dTmpMValue = YssD.mul(dTmpAmount,
                                          YssD.div(dMarketPrice,
                        rs.getInt("FCsFactor")));
                }
                if (dMarketPrice == 0) {
                    dTmpMValue = dTmpMoney;
                }
                secPay.setVMoney(YssD.sub(YssD.sub(dTmpMValue, dTmpMoney),
                                          rs.getDouble("FVBal")));

                //设置原币管理成本的估值增值
                dTmpMoney = rs.getDouble("FMStorageCost"); //原币成本
                dTmpAmount = rs.getDouble("FStorageAmount");
                //是否四舍五入
                if (bIsRound) {
                    //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值 BUG:0000416
                    dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
                        YssD.div(dMarketPrice,
                                 rs.getInt("FCsFactor"))), 2);
                } else {
                    dTmpMValue = YssD.mul(dTmpAmount,
                                          YssD.div(dMarketPrice,
                        rs.getInt("FCsFactor")));
                }
                if (dMarketPrice == 0) {
                    dTmpMValue = dTmpMoney;
                }
                secPay.setMMoney(YssD.sub(YssD.sub(dTmpMValue, dTmpMoney),
                                          rs.getDouble("FMBal")));
                //设置基础货币估值增值
                secPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                    secPay.getMoney(),
                    secPay.getBaseCuryRate()));
                secPay.setVBaseCuryMoney(this.getSettingOper().calBaseMoney(
                    secPay.getVMoney(),
                    secPay.getBaseCuryRate()));
                secPay.setMBaseCuryMoney(this.getSettingOper().calBaseMoney(
                    secPay.getMMoney(),
                    secPay.getBaseCuryRate()));

                //设置组合货币估值增值
                secPay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                    secPay.getMoney(),
                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    secPay.getStrCuryCode(), dDate, this.portCode));
                secPay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
                    secPay.getVMoney(),
                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    secPay.getStrCuryCode(), dDate, this.portCode));
                secPay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
                    secPay.getMMoney(),
                    secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    secPay.getStrCuryCode(), dDate, this.portCode));

                secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV);
                secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV +
                                            sCatCode);
                secPay.checkStateId = 1;

                sKey = secPay.getStrSecurityCode() + "\f" +
                    (this.invmgrSecField.length() != 0 ?
                     (secPay.getInvMgrCode() + "\f") : "") +
                    (this.brokerSecField.length() != 0 ?
                     (secPay.getBrokerCode() + "\f") : "") +
                    secPay.getStrSubTsfTypeCode() + "\f" +
                    (secPay.getAttrClsCode().length() == 0 ? " " : secPay.getAttrClsCode());
                hmResult.put(sKey, secPay);

                //------ MS00265 QDV4建行2009年2月23日01_B ---------
                mktPrice.setValType("RightsIssueMV"); // 设置配股权限的估值类型，与估值界面上的代码相一致。
                //------------------------------------------------

                //2008.07.15 蒋锦 修改 取行情日期而不是估值日期
                mktPrice.setValDate(mktDate);
                mktPrice.setSecurityCode(secPay.getStrSecurityCode());
                mktPrice.setPortCode(portCode);
//               mktPrice.setPrice(rs.getDouble("FRiMktPrice"));
                mktPrice.setOtPrice1(rs.getDouble("FRIPrice"));
                //--------------------2008.07.15 蒋锦 修改--------------------//
                //行情数据不能依靠权证的交易期间来进行判断是取正股行情还是取权证行情，
                //如果估值当天有权证行情就应该取权证的行情，否则要看正股行情日期和权证行情日期谁最接近估值日期，取最接近的行情
                //优先取配股权证的行情
                //fanghaoln 20090826  MS00653  QDV4赢时胜（上海）2009年8月24日01_B 估值时，配股权证的估值行情没有除以报价因子
                mktPrice.setPrice(YssD.div(dMarketPrice, rs.getInt("FCsFactor")));
                //===========================MS00653 end====================================================================
                //2009.7.9 蒋锦 添加 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A-- 估值行情中要包括属性分类代码
                mktPrice.setAttrClsCode(rs.getString("FAttrClsCode"));
                //2009.07.10 蒋锦 修改 估值行情表增加了属性分类作为主键，所以哈西Key也要添加属性分类
                hmValPrice.put(mktPrice.getSecurityCode() + "\t" + mktPrice.getAttrClsCode(), mktPrice);
            }
            dbl.closeResultSetFinal(rs); //close rs 20080716 sj
            // }
            //-----------------获取估值的所有证券代码,用于保存估值结果的删除条件 胡昆 20071217
            Iterator iter = hmResult.values().iterator();
            while (iter.hasNext()) {
                buf.append( ( (SecPecPayBean) iter.next()).getStrSecurityCode()).append(",");
            }
            if (buf.length() > 0) {
                buf.setLength(buf.length() - 1);
            }
            valSecCodes = buf.toString();
//         //---------------------------------------------------------------------

            return secPay;
        } catch (Exception e) {
            throw new YssException("系统资产估值,在执行配股权证浮动盈亏计算时出现异常!" + "\n", e); //by 曹丞 2009.02.01 配股权证浮动盈亏计算异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //======================================================================

    /**
     * 计算非公开发行股票锁定期内行情
     * 蒋锦 添加 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A- 2009-07-10
     * 非公开法行有锁定期且行情价格高于初始取得成本的股票的估值价格使用公式：FV = C+(P-C)*((Dt-Dr)/Dt)
     * FV:为估值日该非公开发行股票的价值
     * C:为该非公开发行股票的初始取得成本
     * P:估值日在证券交易所上市交易的同一股票的市价
     * Dt:为该非公开发行股票锁定期所含的交易天数
     * Dr:为估值日剩余锁定期，即估值日至锁定期结束所含的交易天数（不含估值日当天）
     * 如果该只股票有多个锁定期则最终的估值价格使用：每一个锁定期计算出的估值价格之和/总的锁定数量
     * @param rs ResultSet
     * @param dMarketPrice double
     * @return double
     * @throws YssException
     */
    private double getLockedPrice(ResultSet rs,
                                  double dMarketPrice) throws YssException{
        double dbPrice = 0;
        String strSql = "";
        ResultSet rsLocked = null;
        double dbTmpMktValue = 0;
        double dbTmpPrice = 0;
        try {
            strSql = "SELECT FLockBeginDate, FLockEndDate, FLockDays, FPriceMoney, FAmount" +
                " FROM " + pub.yssGetTableName("Tb_Data_Newissuetrade") +
                " WHERE FCheckState = 1" +
                " AND FTradeTypeCode = " + dbl.sqlString(YssOperCons.YSS_JYLX_SD) +
                " AND FSecurityCode = " + dbl.sqlString(rs.getString("FCSSecurityCode")) +
                " AND FPortCode = " + dbl.sqlString(portCode) +
                " AND FAttrClsCode = " + dbl.sqlString(YssOperCons.YSS_SXFL_UNPONS) +
                " AND FLockBeginDate <= " + dbl.sqlDate(dDate) +
                " AND FLockEndDate >= " + dbl.sqlDate(dDate);
            rsLocked = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while(rsLocked.next()){
                double dbInitCost = rsLocked.getDouble("FPriceMoney"); //初始成本
                //如果行情价格大于初始成本，同时估值日在锁定期内，使用计算得出的价格，否则使用行情价格
                if(dMarketPrice > dbInitCost && dDate.compareTo(rsLocked.getDate("FLockEndDate")) <= 0){
                    int iLockDays = rsLocked.getInt("FLockDays"); //锁定天数
                    //估值日到锁定结束日期之间剩余的交易天数
                    int iDays = this.getSettingOper().workDateDiff(dDate, rsLocked.getDate("FLockEndDate"), rs.getString("FHolidaysCode"), 2);
                    double dbScale = YssD.div(YssD.sub(iLockDays, iDays), iLockDays);
                    dbTmpPrice = YssD.add(dbInitCost,
                                          YssD.mul(YssD.sub(dMarketPrice, dbInitCost), dbScale));
                } else {
                    dbTmpPrice = dMarketPrice;
                }
                dbTmpMktValue += (YssD.round(YssD.mul(dbTmpPrice, rsLocked.getDouble("FAmount")), 4));
            }
            dbPrice = YssD.round(YssD.div(dbTmpMktValue, rs.getDouble("FStorageAmount")), 2);
        } catch (Exception ex) {
            throw new YssException("计算非公开发行股票锁定期内行情出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rsLocked);
        }
        return dbPrice;
    }

    public Object filterSecCondition() {
        SecPecPayBean secpay = new SecPecPayBean();
//      secpay.setStrSecurityCode(valSecCodes);去除证券名称的筛选条件，使当天的估值数据的删除不以证券是否存在而不同。一并删除。sj modified 20081230 bugID:MS00122
        secpay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV);
		//fanghaoln 20090205 MS00996 QDV4赢时胜深圳2009年2月23日01_B  上面已经不对远期进行估值所以不要在删除远期产生的数据
		secpay.setStrSubTsfTypeCode("09EQ,09FI,09TR,09DR,09OP,09RT,09PN,09B_ST"); //添加了PN的类型。sj edit 20080814  //09B_ST(B股估值增值）panjunfang add 20100504
		//-----------------------------------end------------------------------------------------------------
        return secpay;
    }
    
    /**
     * add by wangzuochun 2010.06.18  MS01097    债券转托管，产生的原债券流入那笔负的估值增值基础货币、组合金额不对    QDV4国内（测试）2010年04月16日01_B    
     * @param rs
     * @return
     * @throws YssException
     */
    
    public boolean getDevTrustBond(ResultSet rs) throws YssException{
    	boolean hasBond = false;
    	java.util.Date dCurDate = null;
    	String strSecurityCode = "";
    	String strPortCode = "";
    	String strAttrClsCode = "";
    	String strExchangeCode = "";
    	String strInvMgrCode = "";
    	ResultSet rsBond = null;
    	
    	try{
    		
    		dCurDate = rs.getDate("FStorageDate");
			strSecurityCode = rs.getString("FCSSecurityCode");
			strPortCode = rs.getString("FCSPortCode");
			strAttrClsCode = rs.getString("FAttrClsCode");
			strExchangeCode = rs.getString("FExchangeCode");
			if (this.invmgrSecField.length() != 0) {
				strInvMgrCode = this.invmgrSecField;
			} else {
				strInvMgrCode = " ";
			}

			String strSql = "select * from "
					+ pub.yssGetTableName("Tb_Data_DevTrustBond")
					+ " bond"
					+ " join (select FSecurityCode as FParaSecurityCode, FCatCode from "
					+ pub.yssGetTableName("tb_para_security") + ") sec "
					+ " on bond.FSecurityCode = sec.FParaSecurityCode "
					+ " where fbargaindate = " + dbl.sqlDate(dCurDate)
					// modified by yeshenghong 20111111 BUG3104
					+ " and bond.FSecurityCode = " + dbl.sqlString(strSecurityCode)
					+ " and FPortCode = " + dbl.sqlString(strPortCode)
					+ " and FAttrClsCode = " + dbl.sqlString(strAttrClsCode)
					+ " and FOutExchangeCode = "
					+ dbl.sqlString(strExchangeCode) + " and FInvMgrcode = "
					+ dbl.sqlString(strInvMgrCode) + " and FCatCode = 'FI' and FCheckState = 1";
			rsBond = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			if (rsBond.next()) {
				hasBond = true;
			}
        	
    	}
    	catch (Exception e) {
            throw new YssException("查询当日是否有债券转托管出错！", e); 
        }
    	finally{
    		dbl.closeResultSetFinal(rsBond);
    	}
    	return hasBond;
    }
}
