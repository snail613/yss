package com.yss.main.parasetting.pojo;

import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.util.*;

public class MonetaryFundBean
    extends BaseDataSettingBean {
    private String securityCode = ""; //基金代码
    private String securityName = ""; //基金名称
    private String closedType = ""; //结转类型
    private String closedTypeName = ""; //结转类型名称
    private String interestType = ""; //计息方式
    private String interestTypeName = ""; //计息方式名称
    private String desc = ""; //描述
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    private java.util.Date startDate; //开始日期
    private java.util.Date endDate; //结束日期
    private String portCodes = "";
    private MonetaryFundBean filterType = null;
    private String recycled;
    private String oldSecurityCode = "";
    public MonetaryFundBean() {
    }

    public String getClosedType() {
        return closedType;
    }

    public String getDesc() {
        return desc;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getSecurityName() {
        return securityName;
    }

    public MonetaryFundBean getFilterType() {
        return filterType;
    }

    public String getRecycled() {
        return recycled;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public String getClosedTypeName() {
        return closedTypeName;
    }

    public String getOldSecurityCode() {
        return oldSecurityCode;
    }

    public String getInterestType() {
        return interestType;
    }

    public String getInterestTypeName() {
        return interestTypeName;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getPortCodes() {
        return portCodes;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setClosedType(String closedType) {
        this.closedType = closedType;
    }

    public void setFilterType(MonetaryFundBean filterType) {
        this.filterType = filterType;
    }

    public void setRecycled(String recycled) {
        this.recycled = recycled;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public void setClosedTypeName(String closedTypeName) {
        this.closedTypeName = closedTypeName;
    }

    public void setOldSecurityCode(String oldSecurityCode) {
        this.oldSecurityCode = oldSecurityCode;
    }

    public void setInterestTypeName(String interestTypeName) {
        this.interestTypeName = interestTypeName;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public void setMonetaryFundArrIncludeAssetGroup(ResultSet rs) throws YssException {
        try {
            setMonetaryFundArr(rs);
            this.assetGroupCode = rs.getString("FAssetGroupCode");
            this.assetGroupName = rs.getString("FAssetGroupName");
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        }
    }

    public void setMonetaryFundArr(ResultSet rs) throws YssException {
        try {
            this.securityCode = rs.getString("FSecurityCode");
            this.securityName = rs.getString("FSecurityName");
            this.closedType = rs.getString("FClosedType");
            this.closedTypeName = rs.getString("FCloseTypeName");
            this.interestType = rs.getString("FInterestType");
            this.interestTypeName = rs.getString("FInterestTypeName");
            this.desc = rs.getString("FDesc");
            super.setRecLog(rs);
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(securityCode).append(YssCons.YSS_ITEMSPLITMARK1); ;
        buf.append(securityName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(closedType).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(closedTypeName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(interestType).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(interestTypeName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(assetGroupCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(assetGroupName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(desc).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            recycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            securityCode = reqAry[0];
            securityName = reqAry[1];
            closedType = reqAry[2];
            interestType = reqAry[3];
            if (YssFun.isDate(reqAry[4])) {
                if (!reqAry[4].equalsIgnoreCase("0001-01-01")) {
                    this.startDate = YssFun.parseDate(reqAry[4]);
                }
            }
            if (YssFun.isDate(reqAry[5])) {
                if (!reqAry[5].equalsIgnoreCase("0001-01-01")) {
                    this.endDate = YssFun.parseDate(reqAry[5]);
                }
            }
            portCodes = reqAry[6];
            assetGroupCode = reqAry[7];
            assetGroupName = reqAry[8];
            desc = reqAry[9];
            checkStateId = YssFun.toInt(reqAry[10]);
            oldSecurityCode = reqAry[11];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new MonetaryFundBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析货币基金信息出错", e);
        }
    }

}
