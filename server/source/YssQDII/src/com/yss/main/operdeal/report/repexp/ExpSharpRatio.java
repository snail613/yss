package com.yss.main.operdeal.report.repexp;

import com.yss.main.dao.IOperValue;
import com.yss.main.dao.IYssConvert;
import com.yss.dsub.BaseBean;
import com.yss.util.*;
import com.yss.base.*;

//计算股票投资夏普比例
//=(总体股票投资收益率-无风险收益率)/投资收益率标准差
public class ExpSharpRatio
    extends BaseAPOperValue implements IYssConvert {

    private double eqYield; //总体股票投资收益率
    private double noRiskYield; //无风险收益率
    private double yieldStandard; //投资收益率标准差

    public ExpSharpRatio() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        try {
            dResult = YssD.sub(this.eqYield, this.noRiskYield);
            if (this.yieldStandard == 0) {
                dResult = 0;
            } else {
                dResult = YssD.div(dResult, this.yieldStandard);
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("计算股票投资夏普比例出错", e);
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
        this.eqYield = YssFun.toDouble(sRowStrAry[0]);
        this.noRiskYield = YssFun.toDouble(sRowStrAry[1]);
        this.yieldStandard = YssFun.toDouble(sRowStrAry[2]);
    }
}
