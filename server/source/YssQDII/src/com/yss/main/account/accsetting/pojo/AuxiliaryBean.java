package com.yss.main.account.accsetting.pojo;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: 核算项目定义</p>
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
public class AuxiliaryBean
    extends BaseDataSettingBean {
    private String auxCode = ""; //核算项目代码
    private String auxName = ""; //核算项目名称
    private String auxTypeCode = ""; //核算项目类型代码
    private String auxTypeName = ""; //核算项目类型名称
    private String dataTableCode = ""; //数据表代码
    private String dataTableName = ""; //数据表名称
    private String filter = ""; //筛选条件
    private String codeFieldCode = ""; //代码字段代码
    private String codeFieldName = ""; //代码字段名称
    private String nameFieldCode = ""; //名称字段代码
    private String nameFieldName = ""; //名称字段名称
    private String descFieldCode = ""; //描述字段代码
    private String descFieldName = ""; //描述字段名称

    public String getAuxCode() {
        return auxCode;
    }

    public String getAuxName() {
        return auxName;
    }

    public String getAuxTypeCode() {
        return auxTypeCode;
    }

    public String getAuxTypeName() {
        return auxTypeName;
    }

    public String getCodeFieldCode() {
        return codeFieldCode;
    }

    public String getCodeFieldName() {
        return codeFieldName;
    }

    public String getDataTableCode() {
        return dataTableCode;
    }

    public String getDataTableName() {
        return dataTableName;
    }

    public String getDescFieldCode() {
        return descFieldCode;
    }

    public String getDescFieldName() {
        return descFieldName;
    }

    public String getFilter() {
        return filter;
    }

    public String getNameFieldCode() {
        return nameFieldCode;
    }

    public String getNameFieldName() {
        return nameFieldName;
    }

    public void setAuxCode(String auxCode) {
        this.auxCode = auxCode;
    }

    public void setAuxName(String auxName) {
        this.auxName = auxName;
    }

    public void setAuxTypeCode(String auxTypeCode) {
        this.auxTypeCode = auxTypeCode;
    }

    public void setAuxTypeName(String auxTypeName) {
        this.auxTypeName = auxTypeName;
    }

    public void setCodeFieldCode(String codeFieldCode) {
        this.codeFieldCode = codeFieldCode;
    }

    public void setCodeFieldName(String codeFieldName) {
        this.codeFieldName = codeFieldName;
    }

    public void setDataTableCode(String dataTableCode) {
        this.dataTableCode = dataTableCode;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    public void setDescFieldCode(String descFieldCode) {
        this.descFieldCode = descFieldCode;
    }

    public void setDescFieldName(String descFieldName) {
        this.descFieldName = descFieldName;
    }

    public void setNameFieldCode(String nameFieldCode) {
        this.nameFieldCode = nameFieldCode;
    }

    public void setNameFieldName(String nameFieldName) {
        this.nameFieldName = nameFieldName;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * 从记录集中获取字段值
     * @param rs ResultSet
     * @throws YssException
     */
    public void setValues(ResultSet rs) throws YssException {
        try {
            this.auxCode = rs.getString("FAuxCode");
            this.auxName = rs.getString("FAuxName");
            this.auxTypeCode = rs.getString("FAuxTypeCode");
            this.auxTypeName = rs.getString("FAuxTypeName");
            this.dataTableCode = rs.getString("FDataTableCode");
            this.dataTableName = rs.getString("FDataTableName");
            this.filter = rs.getString("FFilter");
            this.codeFieldCode = rs.getString("FCodeFieldCode");
            this.codeFieldName = rs.getString("FCodeFieldName");
            this.nameFieldCode = rs.getString("FNameFieldCode");
            this.nameFieldName = rs.getString("FNameFieldName");
            this.descFieldCode = rs.getString("FDescFieldCode");
            this.descFieldName = rs.getString("FDescFieldName");

            super.setRecLog(rs);
        } catch (SQLException sex) {
            throw new YssException(sex.toString());
        }
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

            this.auxCode = reqAry[0];
            this.auxName = reqAry[1];
            this.auxTypeCode = reqAry[2];
            this.auxTypeName = reqAry[3];
            this.dataTableCode = reqAry[4];
            this.dataTableName = reqAry[5];
            this.filter = reqAry[6];
            this.codeFieldCode = reqAry[7];
            this.codeFieldName = reqAry[8];
            this.nameFieldCode = reqAry[9];
            this.nameFieldName = reqAry[10];
            this.descFieldCode = reqAry[11];
            this.descFieldName = reqAry[12];

            super.parseRecLog();
        } catch (Exception e) {
            throw new YssException("解析系统表字典信息出错", e);
        }
    }

    /**
     * 将字段值合成字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.auxCode).append(YssCons.YSS_ITEMSPLITMARK1); ;
        buf.append(this.auxName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.auxTypeCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.auxTypeName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.dataTableCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.dataTableName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.filter).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.codeFieldCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.codeFieldName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.nameFieldCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.nameFieldName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.descFieldCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.descFieldName).append(YssCons.YSS_ITEMSPLITMARK1);

        buf.append(super.buildRecLog());
        return buf.toString();
    }
}
