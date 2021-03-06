package com.yss.pojo.param.comp;

import java.util.*;

import com.yss.dsub.*;

public class YssCompValueParam
    extends BaseBean {

//用于获取资产净值
    private java.util.Date dDate;
    private String portCode;
    private String curyCode;
    private String secTypes;
    private String cashTypes;
//   ************************
    public String getPortCode() {
        return portCode;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public void setDDate(Date dDate) {
        this.dDate = dDate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setCashTypes(String cashTypes) {
        this.cashTypes = cashTypes;
    }

    public void setSecTypes(String secTypes) {
        this.secTypes = secTypes;
    }

    public Date getDDate() {
        return dDate;
    }

    public String getCashTypes() {
        return cashTypes;
    }

    public String getSecTypes() {
        return secTypes;
    }

    public YssCompValueParam() {
    }
}
