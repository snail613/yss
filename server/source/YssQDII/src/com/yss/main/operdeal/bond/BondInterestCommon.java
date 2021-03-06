package com.yss.main.operdeal.bond;

import com.yss.util.*;

/**
 *
 * <p>Title: BondInterestCommon</p>
 * <p>Description:普通方法计算债券利息  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */
public class BondInterestCommon
    extends BaseBondOper {
    public BondInterestCommon() {
    }

    public double calBondInterest() throws YssException {
        int iPickDay = 0, iPeriodDay = 0;
        double dAccPer100 = 0, dYesAccPer100 = 0;
        double dToday = 0, dYesInt = 0;

        iPickDay = YssFun.dateDiff(dateNow, dateEnd);
        iPeriodDay = this.getSettingOper().getPeriodYearDay(this.periodCode);
        dAccPer100 = this.getPickFiInterest(dubLv, iPeriodDay, this.dubFactor, iPickDay);
        dYesAccPer100 = this.getPickFiInterest(dubLv, iPeriodDay, this.dubFactor, iPickDay - 1);

        dToday = YssD.mul(this.dubAmount, dAccPer100);
        dYesInt = YssD.mul(this.dubAmount, dYesAccPer100);

        return YssD.sub(dToday, dYesInt);
    }

    //获取已计提的债券利息 修改人：胡昆 20070730
    private double getPickFiInterest(double dFaceRate, int iPeriodDay, double dFactor, int iPickDay) {
        double dResult = 0;
        if (dFactor == 0) {
            dFactor = 1;
        }
        dResult = YssD.div(dFaceRate, iPeriodDay);
        dResult = YssD.div(dResult, dFactor);
        dResult = YssD.mul(dResult, iPickDay);
        return dResult;
    }
}
