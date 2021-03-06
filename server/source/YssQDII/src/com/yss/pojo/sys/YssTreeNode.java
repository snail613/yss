package com.yss.pojo.sys;

public class YssTreeNode {
    private String code;
    private String name;
    private String parentCode;
    private String orderCode;
    public String getName() {
        return name;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getCode() {
        return code;
    }

    public YssTreeNode() {
    }
}
