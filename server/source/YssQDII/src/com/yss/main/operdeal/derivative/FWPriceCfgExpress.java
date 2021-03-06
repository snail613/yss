package com.yss.main.operdeal.derivative;

import java.sql.*;
import java.util.*;

import com.yss.main.operdata.*;
import com.yss.pojo.param.derivative.*;
import com.yss.util.*;

//远期品种价格通过算法配置计算
public class FWPriceCfgExpress
    extends BaseDerivativeOper {
    private ArrayList aAlFwMarket;
    private ForwardTradeBean aFwTrade;
    private int curPeriod = 0; //当前的期限下标。
    public FWPriceCfgExpress() {
    }

    public void init(ArrayList alFwMarket, ForwardTradeBean fwTrade) {
        aAlFwMarket = alFwMarket;
        aFwTrade = fwTrade;
        this.sign = "(,),+,-,*,/";
        try {
            setForwardInfo(); //设置公式
            caluFactDay(this.aAlFwMarket); //对ArrayList中的数据进行相应的计算。
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double calcFWPrice() throws YssException {
        return this.calcFormulaDouble();
    }

    public Object getExpressValue(String sExpress, ArrayList alParams) throws
        YssException {
        Object objResult = null;
        if (sExpress.equalsIgnoreCase("PeriodIndex")) { //获取期间的下标
            objResult = new Integer(getPeriodIndex( (String) alParams.get(0)));
        }
        if (sExpress.equalsIgnoreCase("PeriodAttr")) { //获取期间的属性
            objResult = getPeriodAttr( ( (Integer) alParams.get(0)).intValue(),
                                      (String) alParams.get(1));
        }
        if (sExpress.equalsIgnoreCase("SumPeriod")) { //取期间的合计值
            objResult = new Double(sumPeriod( (String) alParams.get(0)
                                             ,
                                             Integer.parseInt( ( (String) alParams.
                get(1)))
                                             ,
                                             ( (Integer) alParams.get(2)).intValue()));
        }
        return objResult;
    }

    public Object getKeywordValue(String sKeyword) throws YssException {
        Object objResult = null;
        if (this.aFwTrade != null) {
            if (sKeyword.equalsIgnoreCase("SettleDate")) { //远期结算日
                objResult = this.aFwTrade.getSettleDate();
            } else if (sKeyword.equalsIgnoreCase("SpotDate")) { //即期日期
                if (this.aAlFwMarket != null && this.aAlFwMarket.size() > 0) {
                    objResult = ( (YssFwPrice)this.aAlFwMarket.get(curPeriod)).
                        getSpotDate();
                }
            } else if (sKeyword.equalsIgnoreCase("MatureDate")) { //到期日期
                objResult = this.aFwTrade.getMatureDate();
            }
            /*else if (sKeyword.equalsIgnoreCase("Magain"))//利率差
                      {
                 objResult = new Double(((YssFwPrice)this.aAlFwMarket.get(curPeriod)).getMargin());//直接从当前的期限下标为下标从ArrayList中获取
                                                                                                  //已经计算出的此期限的利差。
                      }*/
            else {
                objResult = sKeyword;
            }
        }
        return objResult;
    }

    //获取期间的下标
    private int getPeriodIndex(String sInd) throws YssException {
        int reInt = 0;
        int eqValue = getDepDur();
        if (this.aAlFwMarket != null && this.aAlFwMarket.size() > 0) {
            if (sInd.equalsIgnoreCase("Last")) {
                for (int i = 0; i < aAlFwMarket.size(); i++) {
                    if (eqValue > ( (YssFwPrice) aAlFwMarket.get(i)).getLimitDays()) {
                        continue;
                    } else {
                        reInt = i - 1;
                        break;
                    }
                }
                //邵宏伟20080117 ：
                //远期合约的到期日与即期相同的情况下，已经没有上一报价期间，应该直接使用即期价格估值。
                //因此不能再向前推期间，而是使用系统的最小索引。
                if (reInt < 0) {
                    reInt = 0;
                }
            } else if (sInd.equalsIgnoreCase("Next")) {
                for (int i = 0; i < aAlFwMarket.size(); i++) {
                    if (eqValue > ( (YssFwPrice) aAlFwMarket.get(i)).getLimitDays()) {
                        continue;
                    } else {
                        reInt = i;
                        break;
                    }
                }
                //邵宏伟20080117 ：
                //远期合约的到期日与即期相同的情况下，已经没有上一报价期间，应该直接使用即期价格估值。
                //因此不能再向前推期间，而是使用系统的最小索引。
                if (reInt == 0) {
                    reInt = 1;
                }
            }
        }
        return reInt;
    }

    //获取期间的一些属性
    private Object getPeriodAttr(int idx, String sInd) throws YssException {
        Object objResult = null;
        if (aAlFwMarket == null)
        {
        	return null;
        }
        YssFwPrice fwPrice = (YssFwPrice) aAlFwMarket.get(idx);
        if (this.aAlFwMarket != null && this.aAlFwMarket.size() > 0) {
            if (sInd.equalsIgnoreCase("Avg")) { //远期平均价
                objResult = new Double(YssD.div(YssD.add(fwPrice.getBuyPrice(),
                    fwPrice.getSellPrice()), 2));
            } else if (sInd.equalsIgnoreCase("Buy")) { //远期买价
                objResult = new Double(fwPrice.getBuyPrice());
            } else if (sInd.equalsIgnoreCase("Sell")) { //远期卖价
                objResult = new Double(fwPrice.getSellPrice());
            } else if (sInd.equalsIgnoreCase("AvgMagain")) { //远期平均价利率差
                YssFwPrice LastFwPrice = (YssFwPrice) aAlFwMarket.get(idx - 1);
                objResult = new Double(YssD.sub(fwPrice.getAvgPrice(),
                                                LastFwPrice.getAvgPrice()));
            } else if (sInd.equalsIgnoreCase("ValDay")) { //实际天数
                objResult = new Double(fwPrice.getFactDays());
            } else if (sInd.equalsIgnoreCase("BuyPriceMagain")) { //远期买价利率差
                YssFwPrice LastFwPrice = (YssFwPrice) aAlFwMarket.get(idx - 1);
                objResult = new Double(YssD.sub(fwPrice.getBuyPrice(),
                                                LastFwPrice.getBuyPrice()));
            } else if (sInd.equalsIgnoreCase("SellPriceMagain")) { //远期卖价利率差
                YssFwPrice LastFwPrice = (YssFwPrice) aAlFwMarket.get(idx - 1);
                objResult = new Double(YssD.sub(fwPrice.getSellPrice(),
                                                LastFwPrice.getSellPrice()));
            } else if (sInd.equalsIgnoreCase("BuyPointMagain")) { //点数买价利率差
                YssFwPrice LastFwPrice = (YssFwPrice) aAlFwMarket.get(idx - 1);
                objResult = new Double(YssD.sub(fwPrice.getBuyPoint(),
                                                LastFwPrice.getBuyPoint()));
            } else if (sInd.equalsIgnoreCase("SellPointMagain")) { //点数卖价利率差
                YssFwPrice LastFwPrice = (YssFwPrice) aAlFwMarket.get(idx - 1);
                objResult = new Double(YssD.sub(fwPrice.getSellPoint(),
                                                LastFwPrice.getSellPoint()));
            }
        }
        return objResult;
    }

    //取一些期间的合计值
    private double sumPeriod(String sInd, int iBeginIndex, int iEndIndex) throws
        YssException {
        double totalFactDays = 0.0;
        YssFwPrice fwPrice = null;
        if (this.aAlFwMarket != null && this.aAlFwMarket.size() > 0) {
            if (sInd.equalsIgnoreCase("ValDay")) {
                for (int i = iBeginIndex; i <= iEndIndex; i++) {
                    fwPrice = (YssFwPrice) aAlFwMarket.get(i);
                    totalFactDays = YssD.add(totalFactDays, fwPrice.getFactDays());
                }
            }
        }
        return totalFactDays;
    }

    private ArrayList caluFactDay(java.util.ArrayList arr) {
        double price = 0.0;
        ArrayList FwArr = arr;
        YssFwPrice forward = null;
        YssFwPrice LastForward = null;
        if (FwArr != null && FwArr.size() > 0) {
            double margin = 0.0;
            for (int j = 0; j < FwArr.size(); j++) {
                forward = (YssFwPrice) FwArr.get(j);
                if (j == 0) { //Spot Rate
                    forward.setMargin(0.0);
                    forward.setFactDays(0);
                } else {
                    LastForward = (YssFwPrice) FwArr.get(j - 1);
                    if (j == 1) {
                        forward.setFactDays(YssFun.dateDiff( (java.util.Date) forward.
                            getSpotDate() //实际天数
                            , (java.util.Date) forward.getFwDate())); //如果是第二条记录，则用远期日期减去即期日期。
                    } else {
                        if (j == FwArr.size() - 1) {
                            forward.setFactDays(0);
                        }
                        forward.setFactDays(YssFun.dateDiff( (java.util.Date)
                            LastForward.getFwDate() //实际天数
                            , (java.util.Date) forward.getFwDate())); //直接用两条远期日期相减
                    }
                }
            }

        }
        return FwArr;
    }

    private void setForwardInfo() throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            if (this.aFwTrade != null) {
                strSql = "select a.*, b.FFormula as FFormula from " +
                    "(select FSecurityCode,FCalcPriceMetic from " +
                    pub.yssGetTableName("Tb_Para_Forward")
                    + " where FSecurityCode = " +
                    dbl.sqlString(aFwTrade.getSecurityCode()) + ") a " +
                    " join (select FCIMCode,FFormula from Tb_Base_CalcInsMetic) b on a.FCalcPriceMetic = b.FCIMCode";
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    this.formula = rs.getString("FFormula");
                }
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private int getDepDur() throws
        YssException {
        int days = 0;
        try {
            if (this.aFwTrade != null && this.aAlFwMarket.size() > 0) {
                days = YssFun.dateDiff( ( (YssFwPrice) aAlFwMarket.get(curPeriod)).
                                       getSpotDate(), aFwTrade.getMatureDate());
            }
            return days;
        } catch (Exception e) {
            throw new YssException(e);
        }
    }
}
