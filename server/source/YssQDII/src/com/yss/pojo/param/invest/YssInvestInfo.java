package com.yss.pojo.param.invest;

import java.util.*;

/**
 * <p>Title: </p>
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
public class YssInvestInfo {
    private java.util.Date DDate;
    private double DIvPayCatValue; //����
    private String SIVPayCatCode = "";
    private String SPortCode = "";

    private String SAnalys1 = "";
    private String SAnalys2 = "";
    private String SAnalys3 = "";

    private String calcFomula = "";
    public YssInvestInfo() {
    }

    public Date getDDate() {
        return DDate;
    }

    public double getDIvPayCatValue() {
        return DIvPayCatValue;
    }

    public String getSIVPayCatCode() {
        return SIVPayCatCode;
    }

    public String getSPortCode() {
        return SPortCode;
    }

    public void setSPortCode(String SPortCode) {
        this.SPortCode = SPortCode;
    }

    public void setSIVPayCatCode(String SIVPayCatCode) {
        this.SIVPayCatCode = SIVPayCatCode;
    }

    public void setDIvPayCatValue(double DIvPayCatValue) {
        this.DIvPayCatValue = DIvPayCatValue;
    }

    public void setDDate(Date DDate) {
        this.DDate = DDate;
    }

    public String getSAnalys1() {
        return SAnalys1;
    }

    public String getSAnalys2() {
        return SAnalys2;
    }

    public String getSAnalys3() {
        return SAnalys3;
    }

    public void setSAnalys1(String SAnalys1) {
        this.SAnalys1 = SAnalys1;
    }

    public void setSAnalys2(String SAnalys2) {
        this.SAnalys2 = SAnalys2;
    }

    public void setSAnalys3(String SAnalys3) {
        this.SAnalys3 = SAnalys3;
    }

    public String getCalcFomula() {
        return calcFomula;
    }

    public void setCalcFomula(String calcFomula) {
        this.calcFomula = calcFomula;
    }

}
