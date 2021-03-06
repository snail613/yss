package com.yss.main.operdeal.report.repexp;

import com.yss.main.dao.*;
import com.yss.dsub.BaseBean;
import com.yss.util.*;
import com.yss.base.*;

//计算基准夏普比例
//=(基准收益率-无风险收益率)/基准收益率标准差
public class ExpBaseSharpRatio
    extends BaseAPOperValue implements IYssConvert {

    private double BaseYield; //基准收益率
    private double noRiskYield; //无风险收益率
    private double baseStandard; //基准收益率标准差

    public ExpBaseSharpRatio() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        try {
            dResult = YssD.sub(this.BaseYield, this.noRiskYield);
            if (this.baseStandard == 0) {
                dResult = 0;
            } else {
                dResult = YssD.div(dResult, this.baseStandard);
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("计算基准夏普比例出错", e);
        }

    }

    /**
     * init
     *
     * @param bean Object
     */
    public void init(Object bean) {
    }

    /**
     * invokeOperMothed
     *
     * @return Object
     */
    public Object invokeOperMothed() {
        return "";
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
    public void parseRowStr(String sRowStr) {
        String[] sRowStrAry = sRowStr.split("\t");
        this.BaseYield = YssFun.toDouble(sRowStrAry[0]);
        this.noRiskYield = YssFun.toDouble(sRowStrAry[1]);
        this.baseStandard = YssFun.toDouble(sRowStrAry[2]);

    }
}
