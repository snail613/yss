package com.yss.main.operdeal.report.repexp;

import com.yss.main.dao.*;
import com.yss.dsub.BaseBean;
import com.yss.util.*;
import com.yss.dsub.YssPub;
import com.yss.base.*;

//风险调整金额
/*max[股票受托资产/市场投资表现考核资产*
 (市场投资表现调整金额+受托资产超额收益奖)*
 市场风险调整比例,0]*/
public class ExpRiskAdjustMoney
    extends BaseAPOperValue implements IYssConvert {

    private double trusteeAsset; //股票受托资产
    private double assessAsset; //市场投资表现考核资产
    private double adjustMoney; //市场投资表现调整金额
    private double excessAward; //受托资产超额收益奖
    private double riskAdjustRatio; //市场风险调整比例

    public ExpRiskAdjustMoney() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        try {
            if (this.assessAsset == 0) {
                dResult = 0;
            } else {
                dResult = YssD.add(this.adjustMoney, this.excessAward);
                dResult = YssD.mul(dResult, this.riskAdjustRatio);
                dResult = YssD.mul(dResult, this.trusteeAsset);
                dResult = YssD.div(dResult, this.assessAsset);
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("计算风险调整金额出错", e);
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
        this.trusteeAsset = YssFun.toDouble(sRowStrAry[0]);
        this.assessAsset = YssFun.toDouble(sRowStrAry[1]);
        this.adjustMoney = YssFun.toDouble(sRowStrAry[2]);
        this.excessAward = YssFun.toDouble(sRowStrAry[3]);
        this.riskAdjustRatio = YssFun.toDouble(sRowStrAry[4]);
    }
}
