package com.yss.main.operdeal.bond;

import com.yss.util.*;

public class BondMatureYield
    extends BaseBondOper {
    public BondMatureYield() {
    }

    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        dResult = getBondYield(intPl, dubMz, dubLv, dubQj, tradeDate, dateNow,
                               dateEnd);
        dResult = dResult * 100;
        return dResult;
    }

    public double getBondYield(double intPl, double dubMz, double dubLv,
                               double dubQj, java.util.Date dateNow,
                               java.util.Date dateStart, java.util.Date dateEnd) throws
        YssException {
        int intTs = 0;
        int intCs = 0;
        int intCount = 0;
        if (dateStart == null || dateNow == null || dateEnd == null) {
            return 0;
        }
        intTs = YssFun.dateDiff(dateNow,
                                getNearPayInsDate(dateStart, dateEnd, dateNow,
                                                  (int) intPl, "Next"));
        intCs = this.getPaidInsTime(dateStart, dateNow, (int) intPl);
        intCount = this.getPaidInsTime(dateStart, dateEnd, (int) intPl);
        return getBondYield(intPl, dubMz, dubLv, intTs, dubQj, intCs, intCount,
                            dateNow, dateEnd, 8);
    }

    /**
     *Jw20060104计算债券日收益率
     * 付息频率@param intPl double
     * 债券面值@param dubMz double
     * 票面年利息@param dubLv double
     * 付息天数@param intTs int
     * 债券全价@param dubQj double
     * 已付息次数@param intCs int
     * 总付息次数@param intCount int
     * 债券交易日@param dateNow Date
     * 债券到期日@param dateEnd Date
     * @throws YssException
     * @return double
     */
    //公式中付息天数为债券交割日到下一次付息日的实际天数
    //jw20070107增加小数位处理。
    public double getBondYield(double intPl, double dubMz, double dubLv,
                               int intTs, double dubQj,
                               int intCs, int intCount, java.util.Date dateNow,
                               java.util.Date dateEnd, int lDecs) throws
        YssException {
        double dubPv, dubFv, dubRetu = 0;
        int intN, intSyTs;
        if (intPl == 0) {
            return 0;
        }
        intN = intCount - intCs;
        intSyTs = YssFun.dateDiff(dateNow, dateEnd) + 1;
        //最后付息周期的附息债券（包括固定利率债券和浮动利率债券）、贴现债券和剩余流通期限在一年以内（含一年）的到期一次还本付息债券
        // 用剩余天数与365比较来判断剩余期限是否在一年以内（含一年）
        if (dubMz < 100 || (intN == 1 && intCount != 1) ||
            (intSyTs <= 365 && intCount == 1)) {
            //到期本息
            if (dubMz < 100) {
                dubFv = dubMz;
            } else if (intCount == 1) { //到期一次还本
                //dubFv = dubMz + intCount * dubLv / intPl;
                dubFv = YssD.add(dubMz, YssD.div(YssD.mul(intCount, dubLv), intPl));
            } else {
                //dubFv = dubMz + dubLv / intPl;
                dubFv = YssD.add(dubMz, YssD.div(dubLv, intPl));
            }
            /*dubRetu = YssFun.roundIt( (dubFv - dubQj) / dubQj /
             (intSyTs - YssFun.getLeapYears(dateNow, dateEnd)),
                                     lDecs);*/
            //dubRetu = YssFun.roundIt( (dubFv - dubQj) / dubQj /(intSyTs - YssFun.getLeapYears(dateNow, dateEnd)),lDecs);
            dubRetu = YssFun.roundIt(YssD.div(YssD.div(YssD.sub(dubFv, dubQj),
                dubQj), (YssD.sub(intSyTs, YssFun.getLeapYears(dateNow, dateEnd)))),
                                     lDecs);

        } else if (intSyTs > 365 && intCount == 1) { //用剩余天数与365比较来判断剩余期限是否在一年以上
            //dubRetu = YssD.pow( (dubMz + intCount * dubLv / intPl) / dubQj,(1.0 / (intSyTs - YssFun.getLeapYears(dateNow, dateEnd)))) - 1;

            dubRetu = YssD.pow(YssD.div(YssD.add(dubMz,
                                                 YssD.div(YssD.mul(intCount, dubLv),
                intPl)), dubQj),
                               YssD.div(1.0,
                                        YssD.sub(intSyTs,
                                                 YssFun.getLeapYears(dateNow,
                dateEnd)))) -
                1;

            dubRetu = YssFun.roundIt(dubRetu, lDecs);
        } else { //不处于最后付息周期的固定利率附息债券和浮动利率债券
            /*dubRetu = 0;
                    double dubStep = 0.1; //变化步长
                    boolean blnDz = false; //变化模式：true 递增,false 递减
                    double d1;
                    dubRetu = 0.1;
                    d1 = mathPv(intN, dubMz, dubLv, intTs, 0.00000001);
                    dubPv = mathPv(intN, dubMz, dubLv, intTs, dubRetu);
                    blnDz = dubPv > d1;
//      int intSum = 1 ;
                    while (YssFun.roundIt(dubPv, 2) != YssFun.roundIt(dubQj, 2)) {
//        intSum ++;
              if (dubPv > dubQj && blnDz || dubPv < dubQj && !blnDz) {
                dubRetu -= dubStep;
                dubStep /= 10;
              }
              dubRetu += dubStep;
              dubPv = mathPv(intN, dubMz, dubLv, intTs, dubRetu);
                    }
//      System.out.println(intSum);
             */
//     //****************Jw20060728修改负收率益及提高计算速度
//          double dubStep = 0.000001; //变化步长
//          boolean blnDz = false; //变化模式：true 递增,false 递减
//          //dubRetu = -000.9;
//          dubRetu=-0.00010001;
//          dubPv = mathPv(intN, dubMz, dubLv, intTs, dubRetu);
//          while (YssFun.roundIt(dubPv, 2) != YssFun.roundIt(dubQj, 2)) {
//            dubRetu=dubRetu+0.00000001;
//            dubPv = mathPv(intN, dubMz, dubLv, intTs, dubRetu);
//            if (YssFun.roundIt(dubPv, 2) > YssFun.roundIt(dubQj, 2)&& !blnDz){
//               dubRetu=dubRetu+dubStep;
//            }
//            else if(!blnDz){
//               dubRetu=dubRetu-dubStep;
//               blnDz=true;
//            }
//          }
//     //****************
              double dblMax = 0.04, dblMin = -0.01, dPv = 100;
            int i;
            while (Math.abs(dPv) > 0.001 && Math.abs(dblMin) < 0.02) {
                //dubRetu = (dblMax + dblMin) / 2;
                dubRetu = YssD.div(YssD.add(dblMax, dblMin), 2);
                dPv = -dubQj;
                for (i = 0; i < intN; i++) {
                    //dPv = dPv + dubLv / intPl / YssD.pow(1 + dubRetu,intTs +i * 365 / intPl);
                    dPv = YssD.add(dPv,
                                   YssD.div(YssD.div(dubLv, intPl),
                                            YssD.pow(YssD.add(1, dubRetu),
                        YssD.add(intTs,
                                 YssD.div(YssD.mul(i, 365), intPl)))));
                }
                i--;
                //dPv = dPv + dubMz /  YssD.pow(1 + dubRetu,intTs +i * 365);
                dPv = YssD.add(dPv,
                               YssD.div(dubMz,
                                        YssD.pow(YssD.add(1, dubRetu),
                                                 YssD.add(intTs, YssD.mul(i, 365)))));
                if (dPv < 0) {

                    //dblMax = (dblMax + dblMin) / 2;
                    dblMax = YssD.div(YssD.add(dblMax, dblMin), 2);
                } else {
                    dblMin = YssD.div(YssD.add(dblMax, dblMin), 2);
                }
            }
        }
        return YssFun.roundIt(dubRetu, lDecs);
    }

}
