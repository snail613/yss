package com.yss.pojo.param.comp;

import java.util.Date;
import com.yss.main.dao.*;
import com.yss.util.YssException;
import com.yss.util.YssFun;

;

public class YssCompRep
    implements IYssConvert {
    private java.util.Date dDate;
    private String portCode = "";
    private String portName = "";
    private String templateCode = "";
    private String templateName = "";
    private String compIndexCode = "";
    private String compIndexName = "";
    private String compResult = "";
    private String compWay = "";
    private String unPassHint = "";
    public String getCompResult() {
        return compResult;
    }

    public String getPortCode() {
        return portCode;
    }

    public Date getDDate() {
        return dDate;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getCompIndexCode() {
        return compIndexCode;
    }

    public String getPortName() {
        return portName;
    }

    public void setCompIndexName(String compIndexName) {
        this.compIndexName = compIndexName;
    }

    public void setCompResult(String compResult) {
        this.compResult = compResult;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setDDate(Date dDate) {
        this.dDate = dDate;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public void setCompIndexCode(String compIndexCode) {
        this.compIndexCode = compIndexCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setCompWay(String compWay) {
        this.compWay = compWay;
    }

    public void setUnPassHint(String unPassHint) {
        this.unPassHint = unPassHint;
    }

    public String getCompIndexName() {
        return compIndexName;
    }

    public String getCompWay() {
        return compWay;
    }

    public String getUnPassHint() {
        return unPassHint;
    }

    public YssCompRep() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(YssFun.formatDate(dDate)).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.templateCode).append("\t");
        buf.append(this.templateName).append("\t");
        buf.append(this.compIndexCode).append("\t");
        buf.append(this.compIndexName).append("\t");
        buf.append(this.compWay).append("\t");
        buf.append(this.compResult).append("\t");
        buf.append(this.unPassHint);
        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
    }
}
