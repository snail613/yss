package com.yss.pojo.param.comp;

import java.util.*;

import com.yss.dsub.*;
import com.yss.main.compliance.*;

public class YssCompDeal
    extends BaseBean {
    private String portCode = ""; //组合代码
    private String invMgrCode = ""; //投资经理
    private String brokerCode = ""; //券商代码
    private String securityCode = ""; //证券代码
    private String cashAccCode = ""; //帐户代码
    private String compPoint = ""; //监控点
    private java.util.Date dDate;

    private CompIndexBean compIndex;

    private String secTypes = ""; //证券品种类型
    private String accTypes = ""; //帐户类型

    private String curyCode = "";

//   private String compAction;//监控动作

    public void setCompPoint(String compPoint) {
        this.compPoint = compPoint;
    }

    public void setDDate(Date dDate) {
        this.dDate = dDate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setCompIndex(CompIndexBean compIndex) {
        this.compIndex = compIndex;
    }

    public void setSecTypes(String secTypes) {
        this.secTypes = secTypes;
    }

    public void setAccTypes(String accTypes) {
        this.accTypes = accTypes;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public String getCompPoint() {
        return compPoint;
    }

    public Date getDDate() {
        return dDate;
    }

    public String getPortCode() {
        return portCode;
    }

    public CompIndexBean getCompIndex() {
        return compIndex;
    }

    public String getSecTypes() {
        return secTypes;
    }

    public String getAccTypes() {
        return accTypes;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public YssCompDeal() {
    }
}
