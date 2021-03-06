package com.yss.main.operdeal.report.repexp;

import com.yss.main.dao.*;
import com.yss.dsub.BaseBean;
import com.yss.util.*;
import com.yss.base.*;

//风险因子
//=1-股票投资夏普比例/基准夏普比率
public class ExpRiskGene
    extends BaseAPOperValue implements IYssConvert {

    private double sharpRatio; //股票投资夏普比例
    private double baseSharpRatio; //基准夏普比率

    public ExpRiskGene() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        try {
            if (this.baseSharpRatio != 0) {
                dResult = YssD.div(this.sharpRatio, this.baseSharpRatio);
                dResult = YssD.sub(1, dResult);
            } else {
                dResult = 1;
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("计算风险因子出错", e);
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
        this.sharpRatio = YssFun.toDouble(sRowStrAry[0]);
        this.baseSharpRatio = YssFun.toDouble(sRowStrAry[1]);
    }
}
