package com.yss.main.account.accsetting.pojo;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: 系统表字典</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author CTQ
 * @version 1.0
 */
public class TableDictBean
    extends BaseDataSettingBean {
    private String tableCode = ""; //系统表代码
    private String tableName = ""; //系统表名称
    private String fieldCode = ""; //字段代码
    private String fieldName = ""; //字段名称
    private String fieldDesc = ""; //字段描述

    public TableDictBean() {
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTableCode() {
        return tableCode;
    }

    public String getTableName() {
        return tableName;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 从字符串中获取字段值
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }

            reqAry = sRowStr.split("\t");
            this.tableCode = reqAry[0];
            this.tableName = reqAry[1];
            this.fieldCode = reqAry[2];
            this.fieldName = reqAry[3];
            this.fieldDesc = reqAry[4];
        } catch (Exception e) {
            throw new YssException("解析系统表字典信息出错", e);
        }
    }
}
