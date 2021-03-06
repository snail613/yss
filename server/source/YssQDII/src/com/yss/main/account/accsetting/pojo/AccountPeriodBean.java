package com.yss.main.account.accsetting.pojo;

import java.sql.*;
import java.text.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: 会计期间</p>
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
public class AccountPeriodBean
    extends BaseDataSettingBean {
    private String setCode = ""; //套账代码
    private String setName = ""; //套账名称
    private int year; //会计年度
    private int period; //会计期间
    private String startDate = ""; //起始日期
    private String endDate = ""; //截止日期
    private int closeStateCode; //结账状态代码
    private String closeStateName = ""; //结账状态名称
    private String status = ""; //标识会计期间的编辑状态
    private boolean existData = true; //是否存在数据
    private boolean keepVch = false; //是否保存损益结转凭证

    public AccountPeriodBean() {
    }

    public int getCloseStateCode() {
        return closeStateCode;
    }

    public String getCloseStateName() {
        return closeStateName;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getPeriod() {
        return period;
    }

    public String getSetCode() {
        return setCode;
    }

    public String getSetName() {
        return setName;
    }

    public String getStartDate() {
        return startDate;
    }

    public int getYear() {
        return year;
    }

    public boolean getExistData() {
        return existData;
    }

    public boolean isKeepVch() {
        return keepVch;
    }

    public String getStatus() {
        return status;
    }

    public void setCloseStateCode(int closeStateCode) {
        this.closeStateCode = closeStateCode;
    }

    public void setCloseStateName(String closeStateName) {
        this.closeStateName = closeStateName;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setExistData(boolean existData) {
        this.existData = existData;
    }

    public void setKeepVch(boolean keepVch) {
        this.keepVch = keepVch;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 从记录集中获取字段值
     * @param rs ResultSet
     * @throws YssException
     */
    public void setValues(ResultSet rs) throws YssException {
        try {
            this.setCode = rs.getString("FSetCode");
            this.setName = rs.getString("FSetName");
            this.year = rs.getInt("FYear");
            this.period = rs.getInt("FPeriod");
            Format format = new SimpleDateFormat("yyyy-MM-dd");
            this.startDate = format.format(rs.getDate("FStartDate"));
            this.endDate = format.format(rs.getDate("FEndDate"));
            this.closeStateCode = rs.getInt("FCloseState");
            if (closeStateCode == 0) {
                this.closeStateName = "未结账";
            } else {
                this.closeStateName = "已结账";
            }

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

            reqAry = sRowStr.split(YssCons.YSS_ITEMSPLITMARK1);

            this.setCode = reqAry[0];
            this.setName = reqAry[1];
            this.year = Integer.parseInt(reqAry[2]);
            this.period = Integer.parseInt(reqAry[3]);
            this.startDate = reqAry[4];
            this.endDate = reqAry[5];
            this.closeStateCode = Integer.parseInt(reqAry[6]);
            this.closeStateName = (closeStateCode == 0 ? "未结账" : "已结账");
            this.status = reqAry[8];

            this.existData = reqAry[9].trim().equalsIgnoreCase("T") ? true : false;
            this.keepVch = reqAry[10].trim().equalsIgnoreCase("T") ? true : false;

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
        buf.append(this.setCode).append(YssCons.YSS_ITEMSPLITMARK1); ;
        buf.append(this.setName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.year).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.period).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.startDate).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.endDate).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.closeStateCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(closeStateCode == 0 ? "未结账" : "已结账").append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.status).append(YssCons.YSS_ITEMSPLITMARK1);

        buf.append(this.existData ? "T" : "F").append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.keepVch ? "T" : "F").append(YssCons.YSS_ITEMSPLITMARK1);

        buf.append("null");

        return buf.toString();
    }
}
