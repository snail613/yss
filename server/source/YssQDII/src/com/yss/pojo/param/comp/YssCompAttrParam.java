package com.yss.pojo.param.comp;

import com.yss.dsub.*;
import com.yss.main.dao.*;

public class YssCompAttrParam
    extends BaseBean implements IYssConvert {
    private String field = "";
    private String beanId = "";
    private String rangeType = "";
    private int dataType;
    public String getBeanId() {
        return beanId;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public void setRangeType(String rangeType) {
        this.rangeType = rangeType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getField() {
        return field;
    }

    public String getRangeType() {
        return rangeType;
    }

    public int getDataType() {
        return dataType;
    }

    public YssCompAttrParam() {
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
        if (sRowStr.trim().length() == 0) {
            return;
        }
        String[] sRowAry = sRowStr.split("\r\n");
        this.field = sRowAry[0];
        if (sRowAry.length > 1) {
            this.beanId = sRowAry[1];
        }
        if (sRowAry.length > 2) {
            this.rangeType = sRowAry[2];
        }
    }
}
