package com.yss.main.account.accsetting.pojo;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: 套账设置</p>
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
public class AccountSetBean
    extends BaseDataSettingBean {
    private String setCode = ""; //套账代码
    private String setName = ""; //套账名称
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String typeCode = ""; //套账类型
    private String typeName = ""; //套账类型
    private String startDate = ""; //启用日期
    private String codeLen = ""; //科目编码长度
    private int period; //会计期间
    private String curyCode = ""; //本位币代码
    private String curyName = ""; //本位币名称
    private int year; //会计年度
    private boolean existData = true; //是否存在数据

    private ArrayList periodArray = new ArrayList(); //会计期间列表
    private ArrayList linkSetArray = new ArrayList(); //关联套账列表

    public AccountSetBean() {
    }

    public String getCodeLen() {
        return codeLen;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public int getPeriod() {
        return period;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortName() {
        return portName;
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

    public String getTypeCode() {
        return typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getCuryName() {
        return curyName;
    }

    public int getYear() {
        return year;
    }

    public ArrayList getPeriodArray() {
        return periodArray;
    }

    public ArrayList getLinkSetArray() {
        return linkSetArray;
    }

    public boolean getExistData() {
        return existData;
    }

    public void setCodeLen(String codeLen) {
        this.codeLen = codeLen;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
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

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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
            this.portCode = rs.getString("FPortCode");
            this.portName = rs.getString("FPortName");
            this.typeCode = rs.getString("FTypeCode");
            this.typeName = rs.getString("FTypeName");
            this.startDate = rs.getString("FStartDate");
            this.codeLen = rs.getString("FCodeLen");
            this.period = rs.getInt("FPeriod");
            this.curyCode = rs.getString("FCuryCode");
            this.curyName = rs.getString("FCuryName");
            this.year = rs.getInt("FYear");

            super.setRecLog(rs);
        } catch (SQLException sex) {
            throw new YssException(sex.toString());
        }
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPeriodArray(ArrayList periodArray) {
        this.periodArray = periodArray;
    }

    public void setLinkSetArray(ArrayList LinkSetArray) {
        this.linkSetArray = LinkSetArray;
    }

    public void setExistData(boolean existData) {
        this.existData = existData;
    }

    /**
     * 从字符串中获取字段值
     * @param sRowStr String
     * @throws YssException
     */
    public void parseBasicInfoRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }

            reqAry = sRowStr.split("\t");

            this.setCode = reqAry[0];
            this.setName = reqAry[1];
            this.portCode = reqAry[2];
            this.portName = reqAry[3];
            this.typeCode = reqAry[4];
            this.typeName = reqAry[5];
            this.startDate = reqAry[6];
            this.codeLen = reqAry[7];
            this.period = Integer.parseInt(reqAry[8]);
            this.curyCode = reqAry[9];
            this.curyName = reqAry[10];
            this.year = Integer.parseInt(reqAry[11]);
            this.existData = reqAry[12].trim().equalsIgnoreCase("Y") ? true : false;

        } catch (Exception e) {
            throw new YssException("解析套账设置信息出错", e);
        }
    }

    /**
     * 从字符串中获取字段值
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        try {
            String[] arrData = sRowStr.split("\n\f");

            //转换基本套账设置信息
            if (arrData[0].trim().length() != 0) {
                parseBasicInfoRowStr(arrData[0]);
            }

            //转换会计期间列表数据
            periodArray.clear();
            if (arrData.length >= 2) {
                if (arrData[1].trim().length() != 0) {
                    String[] arrReq = arrData[1].split("\n\t");

                    for (int i = 0; i < arrReq.length; i++) {
                        if (arrReq[i].trim().length() != 0) {
                            AccountPeriodBean bean = new AccountPeriodBean();
                            bean.parseRowStr(arrReq[i]);
                            periodArray.add(bean);
                        }
                    }
                }
            }

            //转换关联套账列表数据
            linkSetArray.clear();
            if (arrData.length >= 3) {
                if (arrData[2].trim().length() != 0) {
                    String[] arrReq = arrData[2].split("\n\t");

                    for (int i = 0; i < arrReq.length; i++) {
                        if (arrReq[i].trim().length() != 0) {
                            AccountSetBean bean = new AccountSetBean();
                            bean.parseBasicInfoRowStr(arrReq[i]);
                            linkSetArray.add(bean);
                        }
                    }
                }
            }

            super.parseRecLog(); //不能放在parseBasicInfoRowStr方法中，否则转换关联套账时因为关联套账没有Pub而出错

        } catch (Exception e) {
            throw new YssException("解析套账设置信息出错", e);
        }
    }

    /**
     * 将字段值合成字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();

        buf.append(buildBasicInfoRowStr()).append("\n\f");

        //生成会计期间列表信息
        Object[] arrPeriod = periodArray.toArray();
        for (int i = 0; i < arrPeriod.length; i++) {
            buf.append( ( (AccountPeriodBean) arrPeriod[i]).buildRowStr()).append("\n\t");
        }

        buf.append("\n\f");

        //生成会计期间列表信息
        Object[] arrSet = linkSetArray.toArray();
        for (int i = 0; i < arrSet.length; i++) {
            buf.append( ( (AccountSetBean) arrSet[i]).buildBasicInfoRowStr()).append("\n\t");
        }

        buf.append("\n\fnull");

        return buf.toString();
    }

    /**
     * 只生成基础套账设置信息的字符串，不包含关联套账列表和会计期间列表
     * @return String
     * @throws YssException
     */
    public String buildBasicInfoRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.setCode).append(YssCons.YSS_ITEMSPLITMARK1); ;
        buf.append(this.setName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.portCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.portName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.typeCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.typeName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.startDate).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.codeLen).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.period).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.curyCode).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.curyName).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.year).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.existData ? "Y" : "N").append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(super.buildRecLog());
        return buf.toString();
    }
}
