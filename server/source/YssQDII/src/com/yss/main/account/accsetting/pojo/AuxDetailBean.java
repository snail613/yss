package com.yss.main.account.accsetting.pojo;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: 核算项目明细</p>
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
public class AuxDetailBean
    extends BaseDataSettingBean {
    private String auxCode = ""; //核算项目代码
    private String itemCode = ""; //明细项目代码
    private String itemName = ""; //明细项目名称
    private String itemDesc = ""; //明细项目描述

    public AuxDetailBean() {
    }

    public String getAuxCode() {
        return auxCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public String getItemName() {
        return itemName;
    }

    public void setAuxCode(String auxCode) {
        this.auxCode = auxCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * 从记录集中获取字段值
     * 对应关系：AuxCode-FAuxCode;ItemCode-FItemCode;ItemName-FItemName;ItemDesc-FItemDesc
     * @param rs ResultSet
     * @throws YssException
     */
    public void setValues(ResultSet rs) throws YssException {
        try {
            this.auxCode = rs.getString("FAuxCode");
            this.itemCode = rs.getString("FItemCode");
            this.itemName = rs.getString("FItemName");
            this.itemDesc = rs.getString("FItemDesc");
        } catch (SQLException sex) {
            throw new YssException(sex.toString());
        }
    }

    /**
     * 从字符串中获取字段值
     * 顺序：AuxCode、ItemCode、ItemName、ItemDesc
     * 分隔：\t
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
            this.itemCode = reqAry[1];
            this.itemName = reqAry[2];
            this.itemDesc = reqAry[3];
        } catch (Exception e) {
            throw new YssException("解析核算项目明细项信息出错", e);
        }
    }

    /**
     * 将字段值合成字符串
     * 顺序：AuxCode、ItemCode、ItemName、ItemDesc
     * 分隔：\t
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.auxCode).append(YssCons.YSS_ITEMSPLITMARK1); ;
        buf.append(this.itemCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.itemName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.itemDesc).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append("null");

        return buf.toString();
    }
}
