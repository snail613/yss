package com.yss.main.orderadmin;

import com.yss.util.*;

public class SimpleBean {
    private String code = "";
    private String name = "";
    private String value = "";
    private String fee = "";
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getCode() {
        return code;
    }

    public String getFee() {
        return fee;
    }

    public SimpleBean() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRowAry = sRowStr.split(YssCons.YSS_ITEMSPLITMARK2);
        this.code = sRowAry[0];
        this.name = sRowAry[1];
        this.value = sRowAry[2];
        if (sRowAry.length > 3) {
            this.fee = sRowAry[3];
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.code).append(YssCons.YSS_ITEMSPLITMARK2);
        buf.append(this.name).append(YssCons.YSS_ITEMSPLITMARK2);
        buf.append(this.value).append(YssCons.YSS_ITEMSPLITMARK2);
        buf.append(this.fee);
        return buf.toString();
    }

}
