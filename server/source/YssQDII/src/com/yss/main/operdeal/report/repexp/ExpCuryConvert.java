package com.yss.main.operdeal.report.repexp;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.base.BaseAPOperValue;

public class ExpCuryConvert
    extends BaseAPOperValue implements IYssConvert {
    private java.util.Date dDate;
    private String curyCode;
    private double money;
    public ExpCuryConvert() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dBaseRate = 0;
        double dResult = 0;
        dBaseRate = this.getSettingOper().getCuryRate(dDate, curyCode, "", YssOperCons.YSS_RATE_BASE);
        dResult = YssD.mul(money, dBaseRate);
        return dResult;
    }

    /**
     * init
     *
     * @param bean BaseBean
     */
    public void init(Object bean) {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRowStrAry = sRowStr.split("\t");
        this.dDate = YssFun.toDate(YssFun.formatDate(sRowStrAry[0], "yyyy-MM-dd"));
        this.curyCode = sRowStrAry[1];
        if (YssFun.isNumeric(sRowStrAry[2])) {
            this.money = YssFun.toDouble(sRowStrAry[2]);
        }
    }

    /**
     * invokeOperMothed
     *
     * @return Object
     */
    public Object invokeOperMothed() {
        return "";
    }
}
