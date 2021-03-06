package com.yss.main.operdeal.report.repexp;

import com.yss.main.dao.*;
import com.yss.dsub.BaseBean;
import com.yss.util.*;
import com.yss.base.*;

//市场风险调整比例
/* V = 风险因子，V<10%，=0%
 10%<V<20%，=10%
 20%<V<40%，=20%
 40%<V<80%，=30%
 V>80%，=40%
 */
public class ExpRiskAdjustRatio
    extends BaseAPOperValue implements IYssConvert {

    private double riskGene; //风险因子

    public ExpRiskAdjustRatio() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        try {
            if (this.riskGene < 0.1) {
                dResult = 0;
            } else if (this.riskGene > 0.1 && this.riskGene < 0.2) {
                dResult = 10;
            } else if (this.riskGene > 0.2 && this.riskGene < 0.4) {
                dResult = 20;
            } else if (this.riskGene > 0.4 && this.riskGene < 0.8) {
                dResult = 30;
            } else if (this.riskGene > 0.8) {
                dResult = 40;
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("计算市场风险调整比例出错", e);
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
        this.riskGene = YssFun.toDouble(sRowStrAry[0]);
    }
}
