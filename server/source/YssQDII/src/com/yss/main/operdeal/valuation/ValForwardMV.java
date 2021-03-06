package com.yss.main.operdeal.valuation;

import java.util.*;
import com.yss.util.*;
import com.yss.main.operdata.*;
import com.yss.main.parasetting.*;
import java.sql.*;
import com.yss.pojo.param.derivative.YssFwPrice;
import com.yss.dsub.BaseComparator;
import com.yss.main.operdeal.bond.BaseBondOper;
import com.yss.main.operdeal.derivative.BaseDerivativeOper;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;

/**
 * <p>Title: </p>
 * <p>Description:远期外汇交易的估值 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @author hukun
 * @version 1.0
 */

public class ValForwardMV
    extends BaseValDeal {
    public ValForwardMV() {
    }

    /**
     * getValuationCats
     *
     * @param mtvBeans ArrayList
     * @return HashMap
     */
    public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {
        String strSql = "";
        MTVMethodBean vMethod = null;
        ResultSet rs = null;
        ResultSet subrs = null;
        int iFactor = 1; //报价因子
        String sKey = "", sCatCode = "";
        HashMap hmResult = new HashMap();
        double dBaseRate = 1;
        double dPortRate = 1;
        double dMarketPrice = 0;
        boolean analy1;
        boolean analy2;
        boolean analy3;
        YssFwPrice forward = null;
        YssFwPrice secForward = null;
        java.util.ArrayList FwArr = null;
        java.util.Map FwMap = null;
        SecPecPayBean secPecPay = null;

        ForwardTradeBean fwTrade = null;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            for (int i = 0; i < mtvBeans.size(); i++) {
                vMethod = (MTVMethodBean) mtvBeans.get(i);
                strSql =
                    "select a.*, b.FTradeDate as FTradeDate, b.FMatureDate as FMatureDate, " +
                    " b.FSettleDate as FSettleDate, c.FBeanId as FBeanId, c.FFormula as FFormula, c.FBuyCury as FBuyCury, c.FSaleCury as FSaleCury, m.FPortCury as FPortCury " +
                    ", e.FBal as FBal, e.FMBal as FMBal, e.FVBal as FVBal,e.FCuryCode as FCuryCode" +
                    ", mk.FCsMarketPrice,mk.FMktValueDate from (" +
                    " select astor.FSecurityCode as FSecurityCode, astor.FPortCode as FPortCode,astor.FStorageAmount as FStorageAmount," +
                    " astor.FStorageCost as FStorageCost, astor.FMStorageCost as FMStorageCost, astor.FVStorageCost as FVStorageCost," +
                    " astor.FFreezeAmount as FFreezeAmount, astor.FPortCuryCost as FPortCuryCost, astor.FMPortCuryCost as FMPortCuryCost," +
                    " astor.FVPortCuryCost as FVPortCuryCost, astor.FBaseCuryCost as FBaseCuryCost, astor.FMBaseCuryCost as FMBaseCuryCost," +
                    " astor.FVBaseCuryCost as FVBaseCuryCost,  astor.FAttrClsCode as FAttrClsCode, asec.FCatCode as FCatCode, asec.FTradeCury as FTradeCury" +
                    " , asec.FMarketCode as FMarketCode, asec.FFactor as FFactor" +
                    (analy1 ? ",astor.FAnalysisCode1 as FAnalysisCode1" : "") +
                    (analy2 ? ",astor.FAnalysisCode2 as FAnalysisCode2" : "") +
                    (analy3 ? ",astor.FAnalysisCode3 as FAnalysisCode3" : "") +
                    " from (select FSecurityCode,FPortCode,FStorageDate,FYearMonth,FCatType,FAttrClsCode" +
                    ",sum(" +
                    dbl.sqlIsNull("FStorageAmount", "0") + ") as FStorageAmount" +
                    ",sum(" +
                    dbl.sqlIsNull("FStorageCost", "0") + ") as FStorageCost" +
                    ",sum(" +
                    dbl.sqlIsNull("FMStorageCost", "0") + ") as FMStorageCost" +
                    ",sum(" +
                    dbl.sqlIsNull("FVStorageCost", "0") + ") as FVStorageCost" +
                    ",sum(" +
                    dbl.sqlIsNull("FFreezeAmount", "0") + ") as FFreezeAmount" + //冻结数量
                    ",sum(" +
                    dbl.sqlIsNull("FPortCuryCost", "0") + ") as FPortCuryCost" +
                    ",sum(" +
                    dbl.sqlIsNull("FMPortCuryCost", "0") + ") as FMPortCuryCost" +
                    ",sum(" +
                    dbl.sqlIsNull("FVPortCuryCost", "0") + ") as FVPortCuryCost" +
                    ",sum(" +
                    dbl.sqlIsNull("FBaseCuryCost", "0") + ") as FBaseCuryCost" +
                    ",sum(" +
                    dbl.sqlIsNull("FMBaseCuryCost", "0") + ") as FMBaseCuryCost" +
                    ",sum(" +
                    dbl.sqlIsNull("FVBaseCuryCost", "0") + ") as FVBaseCuryCost" +
                    (analy1 ? ",FAnalysisCode1" : "") +
                    (analy2 ? ",FAnalysisCode2" : "") +
                    (analy3 ? ",FAnalysisCode3" : "") +
                    " from " + pub.yssGetTableName("Tb_stock_security") +
                    " where FStorageDate = " +
                    dbl.sqlDate(dDate) + 
                    " and FPortCode = " + dbl.sqlString(this.portCode) + //modify by fangjiang 2011.11.10 BUG 3080 
                    " group by FSecurityCode,FPortCode,FStorageDate,FYearMonth,FCatType,FAttrClsCode" +
                    (analy1 ? ",FAnalysisCode1" : "") +
                    (analy2 ? ",FAnalysisCode2" : "") +
                    (analy3 ? ",FAnalysisCode3" : "") +
                    ") astor " +
                    " join (select FSecurityCode,FCatCode,FTradeCury,FMarketCode,FFactor from " +
                    pub.yssGetTableName("Tb_Para_Security") +
                    " where FCheckState = 1) asec on astor.FSecurityCode = asec.FSecurityCode ) a " +
                    " join (select FSecurityCode,FPortCode,FTradeDate,FMatureDate,FSettleDate" +
                    (analy1 ? ",FAnalysisCode1" : "") +
                    (analy2 ? ",FAnalysisCode2" : "") +
                    (analy3 ? ",FAnalysisCode3" : "") +
                    " from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                    " where FMatureDate >= " + dbl.sqlDate(dDate) +
                    " and FCheckState = 1) b on a.FSecurityCode = b.FSecurityCode" +
                    " and a.FPortCode = b.FPortCode" +
                    (analy1 ? " and a.FAnalysisCode1 = b.FAnalysisCode1" : "") +
                    (analy2 ? " and a.FAnalysisCode2 = b.FAnalysisCode2" : "") +
                    (analy3 ? " and a.FAnalysisCode3 = b.FAnalysisCode3" : "") +
                    " join (select FSecurityCode,FFormula,FBeanId,FBuyCury,FSaleCury from (select FSecurityCode,FCalcPriceMetic,FBuyCury,FSaleCury from " +
                    pub.yssGetTableName("Tb_Para_Forward") +
                    " where FCheckState = 1) c1 left join (select FCIMCode,FSPICode,FFormula from Tb_Base_CalcInsMetic where FCheckState = 1) c2 on c1.FCalcPriceMetic = c2.FCIMCode" +

                    " left join (select FSICode,FBeanId from TB_FUN_SPINGINVOKE ) c3 on c2.FSPICode = c3.FSICode) c on a.FSecurityCode = c.FSecurityCode " +
                    //-------------------------------------------------------------------------------------------------------------------------------------------
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
              
                    " left join (select FPortCode, FPortName, FPortCury from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where  FCheckState = 1 ) m on  a.FPortCode = m.FPortCode" +
                    
                    
                    //end by lidaolong
                    //-------------------------------------------------------------------------------------------------------------------------------------------
                    " left join (select * from " +
                    pub.yssGetTableName("Tb_Stock_SecRecPay") +
                    " where FCheckState = 1 and " + operSql.sqlStoragEve(dDate) +
                    " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " and FTsfTypeCode = " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_MV) +
                    ") e on a.FSecurityCode = e.FSecurityCode and a.FPortCode = e.FPortCode and a.FAttrClsCode=e.FAttrClsCode " + // add by leeyu 添加相关的属性分类代码到这里 BUG：000437
                    (analy1 ? " and a.FAnalysisCode1 = e.FAnalysisCode1" : "") +
                    (analy2 ? " and a.FAnalysisCode2 = e.FAnalysisCode2" : "") +
                    (analy3 ? " and a.FAnalysisCode3 = e.FAnalysisCode3" : "") +
                    //------------------------------------------------------------
                    //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                    //" left join ( select mk2.FCsMarketPrice, mk2.FSecurityCode,mk1.FMktValueDate from " +
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
                    " left join ( select "+vMethod.getMktPriceCode() +" as FCsMarketPrice, FSecurityCode,FMktValueDate from " +
                    tmpMarketValueTable + " where FMktSrcCode="+dbl.sqlString(vMethod.getMktSrcCode()) +
                    //系统优化，这里采用预先行情表数据，可提高SQL语句执行速度 by leeyu 20100829
                    " ) mk on a.FSecurityCode = mk.FSecurityCode ";
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    //邵宏伟20080118 - 增加对NULL的判断。如果是新增的远期是没有历史行情的。必须要检查行情为空的状态
                    java.util.Date mktValueDate = null;
                    //-------------edit by yanghaiming 20100412 MS00945 QDV4工银2010年1月19日01_A -----------
                    CtlPubPara ctlPubPara = new CtlPubPara();
					ctlPubPara.setYssPub(pub);
                    if(ctlPubPara.getFMIsCalcn().equalsIgnoreCase("0")){
                    	dMarketPrice = rs.getDouble("FCsMarketPrice");
                        mktValueDate = rs.getDate("FMktValueDate");
                    }else{
	                    if (rs.getDate("FMktValueDate") != null &&
	                        YssFun.dateDiff(rs.getDate("FMktValueDate"), dDate) == 0) { //如果当天在行情表中就有行情就用当天录入的行情
	                        dMarketPrice = rs.getDouble("FCsMarketPrice");
	                        mktValueDate = rs.getDate("FMktValueDate");
	                    } else { //通过远期行情表中的行情计算价格
	                        java.util.Date[] dateArg = new java.util.Date[1];
	                        dMarketPrice = getFwPrice(rs, vMethod, dateArg); //获取价格
	                        if (dateArg.length >= 1) {
	                            mktValueDate = dateArg[0];
	                        }
	                    }
                    }
                    if (dMarketPrice != 0) {
                        secPecPay = new SecPecPayBean();
                        mktPrice = new ValMktPriceBean();
                        secPecPay.setTransDate(dDate);
                        secPecPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                        secPecPay.setStrPortCode(rs.getString("FPortCode"));
                        if (analy1) {
                            secPecPay.setInvMgrCode(rs.getString("FAnalysisCode1"));
                        } else {
                            secPecPay.setInvMgrCode(" ");
                        }
                        if (analy2) {
                            secPecPay.setBrokerCode(rs.getString("FAnalysisCode2"));
                        } else {
                            secPecPay.setBrokerCode(" ");
                        }
                        if (rs.getString("FTradeCury") == null) {
                            throw new YssException("请检查证券品种【" +
                                rs.getString("FSecurityCode") +
                                "】的交易币种设置！");
                        }

                        secPecPay.setStrCuryCode(rs.getString("FTradeCury"));
                        secPecPay.setAttrClsCode(rs.getString("FAttrClsCode")); //sj add 20071204
                        iFactor = rs.getInt("FFactor");

                        dBaseRate = 1;
                        if (!rs.getString("FTradeCury").equalsIgnoreCase(pub.
                        		getPortBaseCury(this.portCode))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                            dBaseRate = this.getSettingOper().getCuryRate(dDate,
                                vMethod.getBaseRateSrcCode(),
                                vMethod.getBaseRateCode(),
                                vMethod.getPortRateSrcCode(),
                                vMethod.getPortRateCode(),
                                rs.getString("FTradeCury"), this.portCode, //取基础汇率时，要录入交易货币。杨文奇。2008-3-5
                                YssOperCons.YSS_RATE_BASE);
                        }

                        if (rs.getString("FPortCury") == null) {
                            throw new YssException("请检查投资组合【" +
                                rs.getString("FPortCode") +
                                "】的币种设置！");
                        }
                        dPortRate = this.getSettingOper().getCuryRate(dDate,
                            vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                            vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                            rs.getString("FPortCury"), this.portCode,
                            YssOperCons.YSS_RATE_PORT);

						// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
						// 则默认组合汇率为1
						if (dPortRate == 0) {
							dPortRate = 1;
						}
						// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
						// 则默认组合汇率为1
                        
                        sCatCode = rs.getString("FCatCode");

                        secPecPay.setBaseCuryRate(dBaseRate);
                        secPecPay.setPortCuryRate(dPortRate);
                        secPecPay.setMktPrice(dMarketPrice);
                        if (secPecPay.getStrSecurityCode().equalsIgnoreCase("941")) {
                            int y = 0;
                        }
                        valFWSecs(rs, secPecPay, mktPrice, dMarketPrice);
                        secPecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV);
                        secPecPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV +
                            sCatCode);
                        secPecPay.checkStateId = 1;
                        sKey = secPecPay.getStrSecurityCode() + "\f" +
                            (analy1 ? (rs.getString("FAnalysisCode1") + "\f") : "") +
                            (analy2 ? (rs.getString("FAnalysisCode2") + "\f") : "") +
                            (analy3 ? (rs.getString("FAnalysisCode3") + "\f") : "") +
                            secPecPay.getStrSubTsfTypeCode() + "\f" +
                            (secPecPay.getAttrClsCode() == null ||
                             secPecPay.getAttrClsCode().length() == 0 ? " " :
                             secPecPay.getAttrClsCode());
                        hmResult.put(sKey, secPecPay);
                        //------------- MS00265 QDV4建行2009年2月23日01_B  -----
                        mktPrice.setValType("ValForwardMV"); //设置估值类型为远期品种浮动盈亏，与估值界面上的代码相一致。
                        //-----------------------------------------------------

                        //2008.07.14 蒋锦 修改 使用行情日期代替估值日期
                        mktPrice.setValDate(mktValueDate);
                        mktPrice.setSecurityCode(secPecPay.getStrSecurityCode());
                        mktPrice.setPortCode(portCode);
                        mktPrice.setPrice(dMarketPrice);
                        hmValPrice.put(mktPrice.getSecurityCode(), mktPrice);

                    }
                }
                dbl.closeResultSetFinal(rs); //close rs 20080716 sj
            }
            return hmResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public Object filterSecCondition() {
        SecPecPayBean secPecpay = new SecPecPayBean();
        secPecpay.setStrTsfTypeCode("09"); //调拨类型：汇兑损益
        secPecpay.setStrSubTsfTypeCode("09FW"); //调拨子类型：远期外汇汇兑损益
        return secPecpay;
    }

    public ForwardTradeBean setFwTrade(ResultSet rs) throws SQLException {
        ForwardTradeBean fwTrade = new ForwardTradeBean();
        fwTrade.setSecurityCode(rs.getString("FSecurityCode"));
        fwTrade.setTradeDate(rs.getDate("FTradeDate"));
        fwTrade.setMatureDate(rs.getDate("FMatureDate"));
        fwTrade.setSettleDate(rs.getDate("FSettleDate"));
        return fwTrade;
    }

    private double getFwPrice(ResultSet rs, MTVMethodBean method, java.util.Date[] arg) throws
        SQLException, YssException {
        java.util.Map FwMap = new java.util.HashMap();
        java.util.ArrayList FwArr = new java.util.ArrayList();
        String strSql = "";
        ResultSet subrs = null;
        BaseDerivativeOper derivative = null;
        Comparator compare = new BaseComparator();
        ForwardTradeBean fwTrade = null;
        double dMarketPrice = 0;
        try {
            int Dates = 0;
            strSql =
                "select a.*,b.FDurUnit as FDurUnit,b.FDuration as FDuration from " +
                "(select * from " +
                pub.yssGetTableName("Tb_Data_FWMktValue") + " where " +
                "FSecurityCode = " + dbl.sqlString(rs.getString("FMarketCode")) +
                " and FMktValueDate = " +
                "(select max(FMktValueDate) as FMktValueDate from " +
                pub.yssGetTableName("Tb_Data_FWMktValue") +
                " where FMktValueDate <=" + dbl.sqlDate(dDate) +
                ") and (FPortCode = " + dbl.sqlString(rs.getString("FPortCode"))
                + " or FPortCode = ' ') and FMktSrcCode = " +
                dbl.sqlString(method.getMktSrcCode()) +
                ") a " +
                " left join (select FDepDurCode,FDurUnit,FDuration from " +
                pub.yssGetTableName("Tb_Para_DepositDuration") +
                " ) b on a.FDepDurCode = b.FDepDurCode ";
            subrs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (subrs.next()) {
                YssFwPrice forward = new YssFwPrice();
                forward.setQuoteDate(dDate); //报价日期
                forward.setSpotDate(subrs.getDate("FSpotDate")); //即期日期
                forward.setFwDate(subrs.getDate("FFWDate")); //远期日期
                //---------------------------------------------------------
                //shw20080220－日期比较方式不对，不能由系统来计算天数，要按照实际远期报价包含的日期。比如半年期的系统计算是180,实际上是存在有180-186天等情况的。
                Dates = YssFun.dateDiff(subrs.getDate("FSpotDate"), subrs.getDate("FFWDate"));
                /*if (subrs.getInt("FDurUnit") == 0) { //Day
                   Dates = subrs.getInt("FDuration");
                             }
                             else if (subrs.getInt("FDurUnit") == 1) { //Week
                   Dates = subrs.getInt("FDuration") * 7;
                             }
                             else if (subrs.getInt("FDurUnit") == 2) { //Month
                   Dates = subrs.getInt("FDuration") * 30;
                             }
                             else if (subrs.getInt("FDurUnit") == 3) { //Year
                   Dates = subrs.getInt("FDuration") * 360;
                             }*/
                //--------------------------------------------------------
                forward.setLimitDays(Dates); //期限天数
                forward.setBuyPrice(subrs.getDouble("FBuyPrice")); //远期买价
                forward.setSellPrice(subrs.getDouble("FSellPrice")); //远期卖价
                forward.setBuyPoint(subrs.getDouble("FBuyPoint")); //点数买价
                forward.setSellPoint(subrs.getDouble("FSellPoint")); //点数卖价
                forward.setAvgPrice(YssD.div(YssD.add(subrs.getDouble("FBuyPrice")
                    ,
                    subrs.getDouble("FSellPrice")),
                                             2.0)); //远期平均价
                arg[0] = subrs.getDate("FMktValueDate");
                FwMap.put(Integer.toString(Dates), forward);
            }
            if (FwMap != null && FwMap.size() > 0) {
            	//edit by yanghaiming 20100208 MS00945 QDV4工银2010年1月19日01_A
            	if(FwMap.size() == 1){
            		throw new YssException("没有足够的远期行情信息，请导入完整的远期行情数据后再进行估值！");
            	}else{
	                FwArr.addAll(FwMap.values());
	                Collections.sort(FwArr, compare);
	                //dMarketPrice = caluPrice(FwMap);
	
	                //------------------------by caocheng 2009.02.01 增加远期价格计算公式检查 MS00004 QDV4.1-2009.2.1_09A--------------------//
	                if (rs.getString("FBeanId") == null || rs.getString("FBeanId").trim().length() <= 0) {
	                    throw new YssException("请设置【基础参数模块的Spring调用】,引用BeanIdondInsCfgFormula!");
	                }
	                //------------------------------------------------------------------------------------//
	                if (rs.getString("FBeanId") != null &&
	                    rs.getString("FBeanId").length() > 0) {
	                    derivative = (BaseDerivativeOper) pub.getOperDealCtx().getBean(
	                        rs.
	                        getString("FBeanId"));
	                }
	                fwTrade = setFwTrade(rs); //设置远期交易的数据，三个日期。
	                if (derivative != null) { //开始计算价格
	                    derivative.setYssPub(pub);
	                    derivative.init(FwArr, fwTrade);
	                    /**shashijie 2011.03.11 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数**/
	                    derivative.setFPortCode(this.portCode);
	                    derivative.setFCuryCode(rs.getString("FSaleCury"));
	                    /**~~~~~~~~~~end~~~~~~~~~~~~~~*/
	                    dMarketPrice = derivative.calcFormulaDouble();
	                }
            	}
            }
            return dMarketPrice;
        } catch (Exception e) {
            throw new YssException("系统进行资产估值,在获取远期品种价格时出现异常!" + "\n", e); // by 曹丞 2009.02.01 获取远期品种价格异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(subrs);
        }
    }

    //对远期品种估值
    private void valFWSecs(ResultSet rs, SecPecPayBean secPay,
                           ValMktPriceBean mktPrice, double dMarketPrice) throws
        YssException {
        double dTmpMoney = 0;
        double dTmpMValue = 0;
        double dTmpAmount = 0;
        boolean bIsRound = false;
        try {
            //--------------2008.09.04 蒋锦 添加 从通用参数获取计算市值时是否四舍五入两位小数---------------//
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            bIsRound = pubPara.getMVIsRound();
            //---------------------------------------------------------------------------------------//

            //设置原币核算成本估值增值
            dTmpAmount = rs.getDouble("FStorageAmount");
            //是否四舍五入
            if (bIsRound) {
                //2008-08-18 蒋锦 添加 YssD.round 函数 round 市值 BUG:0000416
                dTmpMValue = YssD.round(YssD.mul(dTmpAmount,
                                                 YssD.div(dMarketPrice,
                    rs.getInt("FFactor"))), 2);
            } else {
                dTmpMValue = YssD.mul(dTmpAmount,
                                      YssD.div(dMarketPrice,
                                               rs.getInt("FFactor")));
            }
            if (dMarketPrice == 0) {
                dTmpMValue = dTmpMoney;
            }
            //当记帐币种等于买入币种时，采用成本-市值计算，当记帐币种等于卖出币种时，采用市值-成本计算  胡坤  2008-01-29
            if (rs.getString("FTradeCury").equalsIgnoreCase(rs.getString(
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

            } else if (rs.getString("FTradeCury").equalsIgnoreCase(rs.getString(
                "FSaleCury"))) { //如果证券币种等于卖出币种
                dTmpMoney = rs.getDouble("FStorageCost"); //原币成本
                secPay.setMoney(YssD.sub(YssD.sub(dTmpMValue, dTmpMoney), //市值-成本
                                         rs.getDouble("FBal"))); //-前日估值增值余额

                dTmpMoney = rs.getDouble("FMStorageCost"); //原币成本
                secPay.setMMoney(YssD.sub(YssD.sub(dTmpMValue, dTmpMoney), //市值-成本
                                          rs.getDouble("FMBal"))); //-前日估值增值余额

                dTmpMoney = rs.getDouble("FVStorageCost"); //原币成本
                secPay.setVMoney(YssD.sub(YssD.sub(dTmpMValue, dTmpMoney), //市值-成本
                                          rs.getDouble("FVBal"))); //-前日估值增值余额
            }

            mktPrice.setOtPrice1(YssD.div(rs.getDouble("FStorageCost"),
                                          rs.getDouble("FStorageAmount"), 4));

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
                rs.getString("FTradeCury"), this.dDate, this.portCode));
            secPay.setVPortCuryMoney(this.getSettingOper().calPortMoney(
                secPay.getVMoney(),
                secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                rs.getString("FTradeCury"), this.dDate, this.portCode));
            secPay.setMPortCuryMoney(this.getSettingOper().calPortMoney(
                secPay.getMMoney(),
                secPay.getBaseCuryRate(), secPay.getPortCuryRate(),
                //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                rs.getString("FTradeCury"), this.dDate, this.portCode));

        } catch (Exception e) {
            throw new YssException("系统资产估值,在执行远期品种估值时出现异常!" + "\n", e); //by caocheng 2009.02.01 远期估值异常信息 MS00004 QDV4.1-2009.2.1_09A
        }
    }

}
