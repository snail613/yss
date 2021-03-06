package com.yss.main.operdeal.datainterface.pojo;

import java.util.*;

/**
 * <p>Title: 此类用于保存与传递 CommonPretFun的参数</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CommonPrepFunBean {
    public CommonPrepFunBean() {
    }

    public String getTradeSeat() {
        return tradeSeat;
    }

    public String getPortCodes() {
        return portCodes;
    }

    public Object getObj() {
        return obj;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getCusCfgCode() {
        return cusCfgCode;
    }

    public String getCheck() {
        return check;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setAllData(String allData) {
        this.allData = allData;
    }

    public void setTradeSeat(String tradeSeat) {
        this.tradeSeat = tradeSeat;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setCusCfgCode(String cusCfgCode) {
        this.cusCfgCode = cusCfgCode;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public String getAllData() {
        return allData;
    }

    private String portCodes = ""; //组合
    private java.util.Date beginDate; //起始日期
    private java.util.Date endDate; //结束日期
    private String cusCfgCode; //当前的接口代码
    private String allData; //导入的数据
    private String tradeSeat; //交易席位
    private String check = ""; //审核状态
    private Object obj = null; //存放原CommonPretfun中的obj

}
