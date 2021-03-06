package com.yss.main.operdeal.valuation;

import java.util.*;
import com.yss.main.parasetting.MTVMethodBean;
import com.yss.main.operdata.CashPecPayBean;
import java.sql.*;
import com.yss.util.*;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.commeach.EachRateOper;

/*
       获取估值存款信息的汇兑损益
       取数原则：
          1.获取所有有余额的帐户
          2.先统计当日库存(Tb_Stock_Cash)
          3.获取当日汇率，获取汇兑损益
 */

public class ValCashFX
    extends BaseValDeal {
    public ValCashFX() {
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
        CashPecPayBean payRate = null;
        ResultSet rs = null;
        String sKey = "";
        double dBaseRate = 1;
        double dPortRate = 1;
        double dPrice = 0;
        double dTmpMoney1 = 0;
        double dTmpMoney2 = 0;
        double dTmpMoney3 = 0;
        double dBaseMoney = 0;
        double dPortMoney = 0;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090417 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------

        try {
            //先统计当日现金库存
            //取消现金库存统计的注释 sunkey 20081124 BugID:MS00013
            //重新注释 sunkey 20081126 BugID:MS00013
//         BaseStgStatDeal stgstat = (BaseStgStatDeal) pub.
//               getOperDealCtx().getBean("CashStorage");
//         stgstat.setYssPub(pub);
//         stgstat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));

//            OperFunDealBean OperFun = (OperFunDealBean) pub.getOperDealCtx().
//                  getBean("operfun");
//            OperFun.setYssPub(pub);
//            operFun.calculateStorage(dDate, dDate, "'" + this.portCode + "'",
//                                     YssOperCons.YSS_KCLX_Cash, false);

            //     hmValRate = new HashMap();
            for (int i = 0; i < mtvBeans.size(); i++) {
                vMethod = (MTVMethodBean) mtvBeans.get(i);

                strSql = " select a.*,pay.FBal as FHDSYBal,pay.FPortCuryBal as FHDSYPortBal,pay.FBaseCuryBal as FHDSYBaseBal,b.FCuryCode," +
                     //#1476  因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误  add by jiangshichao 2011.03.16 --
                    " m.FPortCury from (select FCashAccCode, FStorageDate, FPortCode, FATTRCLSCODE," +
                    //#1476  因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误  end ------------------------------
                    dbl.sqlIsNull("FAccBalance", "0") + " as FAccBalance, " +
                    dbl.sqlIsNull("FBaseCuryBal") + " as FBaseCuryBal, " +
                    dbl.sqlIsNull("FPortCuryBal") +
                    " as FPortCuryBal" +
                    //判断是否配置分析代码，杨
                    //------ modify by wangzuochun 2010.08.31  MS01624    库存信息配置界面,将现金类配置分析代码三，估值报错    QDV4赢时胜(测试)2010年08月19日1_B   
                    (this.invmgrCashField.length() != 0 ? "," + this.invmgrCashField : " ") +
                    (this.catCashField.length() != 0 ? "," + this.catCashField : " ") +
                    //------------------------------MS01624-----------------------------------//
                    " from " +
                    pub.yssGetTableName("tb_stock_cash") +
                    " where fcheckstate=1 and FStorageDate=" + dbl.sqlDate(dDate) +
                    " and " + dbl.sqlRight("FYearMonth", "2") +
                    "<>'00' and FPortCode = " + dbl.sqlString(this.portCode) +
                    ") a " +
                    //---------------------------------------------------------
                    " left join (select * from " +
                    pub.yssGetTableName("Tb_Stock_CashPayRec") +
                    " where " + operSql.sqlStoragEve(dDate) +
                    " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " and FTsfTypeCode = " +
                    dbl.sqlString(YssOperCons.YSS_ZJDBLX_FX) +
                    " and FSubTsfTypeCode like '" + YssOperCons.YSS_ZJDBZLX_FX_Storage + "%'" + //取库存成本汇兑损益下面的所有汇兑损益 20070918 胡昆
                    " and FCheckState = 1)pay on a.FCashAccCode = pay.FCashAccCode " +
                    //#1476  因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误  add by jiangshichao 2011.03.16 --
                    " and a.FATTRCLSCODE = pay.FATTRCLSCODE "+
                    //#1476  因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误  end -----------------------------
                    //------ modify by wangzuochun 2010.08.31  MS01624    库存信息配置界面,将现金类配置分析代码三，估值报错    QDV4赢时胜(测试)2010年08月19日1_B   
                    (this.invmgrCashField.length() != 0 ?
                     " and a." + this.invmgrCashField + " = pay." + this.invmgrCashField  : " ") +
                    (this.catCashField.length() != 0 ?
                     " and a." + this.catCashField + " = pay." + this.catCashField  : " ") +
                     //------------------------------MS01624----------------------------------//
                     
                    //-应考虑重复记录，但鉴于之后不再使用启用日期，因此直接删除启用日期的判断-
                    //---modify by sunkey 20090615 BugNO:MS00413 QDV4赢时胜（上海）2009年4月24日03_B
                    " left join (select FCashAccCode, FCuryCode from " +
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    " Where FCheckState = 1 " +
                    ") b on a.FCashAccCode=b.FCashAccCode " +
                    //----------------------End MS00413--------------------------------
                
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
              
                    " left join (select FPortCode, FPortName, FPortCury  from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where  FCheckState = 1) m on a.FPortCode = m.FPortCode ";

                
                //end by lidaolong
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                int jj = 0;
                while (rs.next()) {
                    jj++;
                    //System.out.println(rs.getString("FCashAccCode"));
                    if (jj == 22) {
                        int ie = 0;
                    }
                    payRate = new CashPecPayBean();
                    payRate.setTradeDate(this.dDate);

                    payRate.setCashAccCode(rs.getString("FCashAccCode"));
                    payRate.setPortCode(rs.getString("FPortCode"));
                    //#1476  因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误  add by jiangshichao 2011.03.16 --
                    payRate.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
                    //#1476  因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误  end -----------------------------
                    payRate.setInvestManagerCode(this.invmgrCashField.length() != 0 ?
                                                 rs.getString(this.
                        invmgrCashField) : " ");
                    payRate.setCategoryCode(this.catCashField.length() != 0 ?
                                            rs.getString(this.catCashField) : " ");

                    if (rs.getString("FCuryCode") == null) {
                        throw new YssException("请检查现金帐户【" +
                                               rs.getString("FCashAccCode") +
                                               "】的币种设置!");
                    }
                    payRate.setCuryCode(rs.getString("FCuryCode"));
                    dBaseRate = 1;
                    if (!rs.getString("FCuryCode").equalsIgnoreCase(pub.getPortBaseCury(this.portCode))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                        dBaseRate = this.getSettingOper().getCuryRate(dDate,
                            vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                            vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                            rs.getString("FCuryCode"), this.portCode,
                            YssOperCons.YSS_RATE_BASE);
                    }

                    if (rs.getString("FPortCury") == null) {
                        throw new YssException("请检查投资组合【" +
                                               rs.getString("FPortCode") +
                                               "】的币种设置!");
                    }
//               dPortRate = this.getSettingOper().getCuryRate(dDate,
//                     vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
//                     vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
//                     rs.getString("FPortCury"), this.portCode,
//                     YssOperCons.YSS_RATE_PORT);	//彭彪20070929  取组合货币！！
                    //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 ---
                    rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), this.portCode, vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                              vMethod.getPortRateSrcCode(), vMethod.getPortRateCode()); //用通用方法，获取组合汇率
                    dPortRate = rateOper.getDPortRate(); //获取组合汇率
                    
					// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
					// 则默认组合汇率为1
					if (dPortRate == 0) {
						dPortRate = 1;
					}
					// V4.1_ETF:MS00002 add by songjie 2009.11.11 若取到的组合汇率为0
					// 则默认组合汇率为1
                    //------------------------------------------------------------


                    payRate.setBaseCuryRate(dBaseRate);
                    payRate.setPortCuryRate(dPortRate);

                    if (payRate.getCashAccCode().equals("10240")) {
                        int kkk = 0;
                    }

                    dTmpMoney1 = rs.getDouble("FAccBalance");
                    //计算基础货币的汇兑损益
                    dTmpMoney2 = rs.getDouble("FBaseCuryBal");
                    dBaseMoney = YssD.sub(this.getSettingOper().calBaseMoney(dTmpMoney1, dBaseRate), dTmpMoney2);
                    //  dBaseMoney = YssD.sub(YssD.mul(dTmpMoney1, dBaseRate),
                    //                      dTmpMoney2);
                    payRate.setBaseCuryMoney(YssD.sub(dBaseMoney,
                        rs.getDouble("FHDSYBaseBal")));

                    //计算组合货币的汇兑损益
                    dTmpMoney3 = rs.getDouble("FPortCuryBal");
                    dPortMoney = YssD.sub(this.getSettingOper().calPortMoney(dTmpMoney1, dBaseRate, dPortRate,
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCuryCode"), dDate, this.portCode),
                                          dTmpMoney3);
                    //  dPortMoney = YssD.sub(YssD.div(YssD.mul(dTmpMoney1,
                    //      dBaseRate), dPortRate), dTmpMoney3);
                    payRate.setPortCuryMoney(YssD.sub(dPortMoney,
                        rs.getDouble("FHDSYPortBal")));

                    payRate.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX);
                    payRate.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FX_Storage + "DE"); //调拨子类型中加入DE,因为这里只有只有一种  胡昆 20070918
                    payRate.checkStateId = 1;

                    sKey = payRate.getPortCode() + "\f" +
                        payRate.getCashAccCode() + "\f" +
                        (this.invmgrCashField.length() != 0 ?
                         (payRate.getInvestManagerCode() + "\f") : "") +
                        (this.catCashField.length() != 0 ?
                         (payRate.getCategoryCode() + "\f") : "") +
                        payRate.getSubTsfTypeCode()+"\f"+payRate.getStrAttrClsCode();//#1476  因为股票按属性分类，所以在现金库存里分属性，造成现金的汇兑损益出现错误  add by jiangshichao 2011.03.16 --
                    //      calculateAdjust(payRate);
                    hmResult.put(sKey, payRate);

                    hmValRate.put(payRate.getCuryCode(), payRate);
                }
                dbl.closeResultSetFinal(rs);
            }
            return hmResult;
        } catch (Exception e) {
            throw new YssException("系统进行资产估值,在执行存款计算汇兑损益时出现异常!" + "\n", e); //by 曹丞 2009.02.01 存款计算汇兑损益异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public Object filterCashCondition() {
        CashPecPayBean cashpay = new CashPecPayBean();
        cashpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_FX);
        cashpay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FX_Storage + "%"); //删除的时候需要做like操作  胡昆  20070918
        return cashpay;
    }

}
