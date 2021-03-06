package com.yss.main.operdeal.valuation;

import java.util.*;
import com.yss.util.*;
import com.yss.main.operdata.*;
import java.sql.*;
import com.yss.main.parasetting.*;
import com.yss.commeach.EachRateOper;

public class ValForwardFX
    extends BaseValDeal {
    public ValForwardFX() {
    }

    /**
     * getValuationCats
     *
     * @param mtvBeans ArrayList
     * @return HashMap
     */
    public HashMap getValuationCats(ArrayList mtvBeans) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        MTVMethodBean vMethod = null;

        boolean analy1;
        boolean analy2;
        boolean analy3;

        CashPecPayBean cashPay = null;
        double dBaseRate = 1;
        double dPortRate = 1;

        double dCurMoney = 0; //通过当前汇率得到的本位币金额
        double dForwardMoney = 0; //通过远期汇率得到的本位币金额

        PortfolioBean port = new PortfolioBean();

        HashMap hmResult = new HashMap();
        String sKey = "";
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090417 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------

        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            port.setYssPub(pub);
            port.setPortCode(this.portCode);
            port.getSetting();

            for (int i = 0; i < mtvBeans.size(); i++) {
                vMethod = (MTVMethodBean) mtvBeans.get(i);

                strSql = "select a.*,b.* from " +
                    "(select FSCashAccCode,FSCuryCode,FBCuryCode," +
                    " sum(FBMoney) as FBMoney,sum(FSMoney) as FSMoney" +
                    (analy1 ? ",FAnalysisCode1" : "") +
                    (analy2 ? ",FAnalysisCode2" : "") +
                    (analy3 ? ",FAnalysisCode3" : "") +
                    " from " + pub.yssGetTableName("Tb_Data_RateTrade") +
                    " where FCheckState = 1 and FPortCode = " +
                    dbl.sqlString(portCode) +
                    " and (FSCuryCode = " + dbl.sqlString(port.getCurrencyCode()) +
                    " or FBCuryCode = " + dbl.sqlString(port.getCurrencyCode()) +
                    ")" + //只有卖出货币或者买入货币为组合货币才估值
                    " and FCatType = 1" + //取品种类型为Forward(远期外汇交易)
                    " and FTradeDate <= " + dbl.sqlDate(dDate) +
                    " and FSettleDate > " + dbl.sqlDate(dDate) + //估值日期大于等于交易日期并且小于结算日期
                    " group by FSCashAccCode,FSCuryCode,FBCuryCode" +
                    (analy1 ? ",FAnalysisCode1" : "") +
                    (analy2 ? ",FAnalysisCode2" : "") +
                    (analy3 ? ",FAnalysisCode3" : "") +
                    " ) a left join" +
                    //-------------------------------------以下取前一日的远期外汇汇兑损益的余额
                    "(select * from " +
                    pub.yssGetTableName("Tb_Stock_CashPayRec") +
                    " where " + operSql.sqlStoragEve(dDate) +
                    " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " and FTsfTypeCode = '99'" + //调拨类型：汇兑损益
                    " and FSubTsfTypeCode = '9916FW'" + //调拨子类型：远期外汇汇兑损益
                    " and FCheckState = 1) b on a.FSCashAccCode = b.FCashAccCode " +
                    (analy1 ? " and a.FAnalysisCode1 = b.FAnalysisCode1" : "") +
                    (analy2 ? " and a.FAnalysisCode2 = b.FAnalysisCode2" : "") +
                    (analy3 ? " and a.FAnalysisCode3 = b.FAnalysisCode3" : "");
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    cashPay = new CashPecPayBean();
                    cashPay.setTradeDate(this.dDate);

                    cashPay.setCashAccCode(rs.getString("FSCashAccCode")); //远期外汇汇兑损益做到卖出帐户上
                    cashPay.setCuryCode(rs.getString("FSCuryCode"));
                    cashPay.setPortCode(this.portCode);
                    cashPay.setInvestManagerCode( (analy1 ?
                        rs.getString("FAnalysisCode1") :
                        " "));
                    cashPay.setCategoryCode( (analy2 ? rs.getString("FAnalysisCode2") :
                                              " "));

                    if (rs.getString("FBCuryCode") == null) {
                        throw new YssException("请检查现金帐户【" +
                                               rs.getString("FCashAccCode") +
                                               "】的设置！");
                    }

                    if (port.getCurrencyCode() == null &&
                        port.getCurrencyCode().trim().length() == 0) {
                        throw new YssException("请检查投资组合【" +
                                               rs.getString("FPortCode") +
                                               "】的币种设置！");
                    }

//               dPortRate = this.getSettingOper().getCuryRate(dDate,
//                     vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
//                     vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
//                     port.getCurrencyCode(), this.portCode,
//                     YssOperCons.YSS_RATE_PORT);
                    //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 ---
                    rateOper.getInnerPortRate(dDate, rs.getString("FSCuryCode"), this.portCode, vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
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


                    if (rs.getString("FBCuryCode").equalsIgnoreCase(port.
                        getCurrencyCode())) { //当买入货币为组合货币时
                        dBaseRate = this.getSettingOper().getCuryRate(dDate,
                            vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                            vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                            rs.getString("FSCuryCode"), this.portCode,
                            YssOperCons.YSS_RATE_BASE); //取卖出货币的汇率
                        dCurMoney = this.getSettingOper().calPortMoney(rs.getDouble(
                            "FSMoney"), dBaseRate, dPortRate, //计算出卖出货币当日的本位币金额
                            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            rs.getString("FSCuryCode"), dDate, this.portCode);
                        dForwardMoney = rs.getDouble("FBMoney"); //远期本位币金额就是买入货币金额
                        cashPay.setPortCuryMoney(YssD.sub(dForwardMoney, dCurMoney)); //远期本位币金额-卖出货币当日的本位币金额得到远期外汇汇兑损益余额
                        cashPay.setPortCuryMoney(YssD.sub(cashPay.getPortCuryMoney(),
                            rs.getDouble("FPortCuryBal"))); //远期外汇汇兑损益余额-昨日远期外汇汇兑损益余额得到当日调整金额
                    } else if (rs.getString("FSCuryCode").equalsIgnoreCase(port.
                        getCurrencyCode())) { //当卖出货币为组合货币时
                        dBaseRate = this.getSettingOper().getCuryRate(dDate,
                            vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                            vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                            rs.getString("FBCuryCode"), this.portCode,
                            YssOperCons.YSS_RATE_BASE); //取买入货币的汇率
                        dCurMoney = this.getSettingOper().calPortMoney(rs.getDouble(
                            "FBMoney"), dBaseRate, dPortRate, //计算出买入货币当日的本位币金额
                            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            rs.getString("FBCuryCode"), dDate, this.portCode);
                        dForwardMoney = rs.getDouble("FSMoney"); //远期本位币金额就是卖出货币金额
                        cashPay.setPortCuryMoney(YssD.sub(dForwardMoney, dCurMoney)); //远期本位币金额-卖出货币当日的本位币金额得到远期外汇汇兑损益余额
                        cashPay.setPortCuryMoney(YssD.sub(cashPay.getPortCuryMoney(),
                            rs.getDouble("FPortCuryBal"))); //远期外汇汇兑损益余额-昨日远期外汇汇兑损益余额得到当日调整金额

                    }
                    cashPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(cashPay.getPortCuryMoney(), dPortRate)); //用组合货币金额*基础货币得到基础货币的远期外汇汇兑损益

                    cashPay.setPortCuryRate(dPortRate);
                    dBaseRate = this.getSettingOper().getCuryRate(dDate,
                        vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                        vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                        rs.getString("FSCuryCode"), this.portCode,
                        YssOperCons.YSS_RATE_BASE); //取卖出货币的汇率
                    cashPay.setBaseCuryRate(dBaseRate);

                    cashPay.setTsfTypeCode("99"); //调拨类型：汇兑损益
                    cashPay.setSubTsfTypeCode("9916FW"); //调拨子类型：远期外汇汇兑损益
                    cashPay.checkStateId = 1;

                    sKey = cashPay.getPortCode() + "\f" +
                        cashPay.getCashAccCode() +
                        "\f" +
                        (this.invmgrCashField.length() != 0 ?
                         (cashPay.getInvestManagerCode() + "\f") : "") +
                        (this.catCashField.length() != 0 ?
                         (cashPay.getCategoryCode() + "\f") : "") +
                        cashPay.getSubTsfTypeCode();
                    //      calculateAdjust(cashPay);
                    hmResult.put(sKey, cashPay);
                    hmValRate.put(cashPay.getCuryCode(), cashPay);
                }
                dbl.closeResultSetFinal(rs); //close rs 20080716 sj
            }
            return hmResult;
        } catch (Exception e) {
            throw new YssException("系统资产估值,在执行远期汇兑损益计算时出现异常!" + "\n", e); //by 曹丞 2009.02.01 远期汇兑损益异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public Object filterCashCondition() {
        CashPecPayBean cashpay = new CashPecPayBean();
        cashpay.setTsfTypeCode("99"); //调拨类型：汇兑损益
        cashpay.setSubTsfTypeCode("9916FW"); //调拨子类型：远期外汇汇兑损益
        return cashpay;
    }

}
