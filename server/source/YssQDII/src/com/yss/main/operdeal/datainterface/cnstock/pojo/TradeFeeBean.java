package com.yss.main.operdeal.datainterface.cnstock.pojo;

import com.yss.util.YssCons;
import com.yss.util.YssException;

/**
 * QDV4.1赢时胜（上海）2009年4月20日11_A
 * MS00011
 * TB_PUB_DAO_TradeFee 交易费用计算方式表的实体类
 * create by songjie
 * 2009-06-19
 */
public class TradeFeeBean {
    public TradeFeeBean() {
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getTradeDetails() {
        return tradeDetails;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public String getTradeSum() {
        return tradeSum;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setTradeDetails(String tradeDetails) {
        this.tradeDetails = tradeDetails;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public void setTradeSum(String tradeSum) {
        this.tradeSum = tradeSum;
    }

    /**
     * 组装数据
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(transform(tradeDetails)).append(YssCons.YSS_ITEMSPLITMARK1); //按成交明细计算
        buf.append(transform(tradeNum)).append(YssCons.YSS_ITEMSPLITMARK1); //按申请编号计算
        buf.append(transform(tradeSum)).append(YssCons.YSS_ITEMSPLITMARK1); //按成交汇总计算
        buf.append("null");
        return buf.toString();
    }

    /**
     * 转换按成交明细计算
     */
    public String transform(String key) {
        if (key == null || key.length() == 0) {
            return "";
        }
        String[] rmpAry = key.split(",");
        String value = "";
        for (int i = 0; i < rmpAry.length; i++) {
            value += k2v(rmpAry[i]) + "\f\f";
        }
        if (value.length() > 2) {
            value = value.substring(0, value.length() - 2);
        }
        return value;
    }

    /**
     * key 转 value
     * @param key String
     * @return String
     */
    public String k2v(String key) {
        if ("01".equals(key)) {
            return "上海经手费";
        } else if ("02".equals(key)) {
            return "上海证管费";
        } else if ("03".equals(key)) {
            return "上海过户费";
        } else if ("04".equals(key)) {
            return "上海印花税";
        } else if ("05".equals(key)) {
            return "上海佣金";
        } else if ("06".equals(key)) {
            return "深圳经手费";
        } else if ("07".equals(key)) {
            return "深圳证管费";
        } else if ("08".equals(key)) {
            return "深圳过户费";
        } else if ("09".equals(key)) {
            return "深圳印花税";
        } else if ("10".equals(key)) {
            return "深圳佣金";
        } else if ("11".equals(key)) {
            return "上海结算费";
        } else if ("12".equals(key)) {
            return "深圳结算费";
        } else {
            return ""; //未查到返回空
        }
    }

    private String assetGroupCode = null;
    private String portCode = null;
    private String tradeDetails = null;
    private String tradeNum = null;
    private String tradeSum = null;

}
