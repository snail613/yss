package com.yss.main.operdeal.businesswork.pojo;

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
public class SecurityPojo {
    private String strSecuritycode; //证券代码
    private String strPortCode; //组合代码
    private double fbal;
    private double fmbal;
    private double fvbal;
    private double fbasecurybal;
    private double fmbasecurybal;
    private double fvbasecurybal;
    private double fportcurybal;
    private double fmportcurybal;
    private double fvportcurybal;
    private double fstorageamount;
    private String strFnum;

    public SecurityPojo() {
    }

    public double getFbal() {
        return fbal;
    }

    public double getFbasecurybal() {
        return fbasecurybal;
    }

    public double getFmbal() {
        return fmbal;
    }

    public double getFmbasecurybal() {
        return fmbasecurybal;
    }

    public double getFmportcurybal() {
        return fmportcurybal;
    }

    public double getFstorageamount() {
        return fstorageamount;
    }

    public double getFvbal() {
        return fvbal;
    }

    public double getFvbasecurybal() {
        return fvbasecurybal;
    }

    public double getFvportcurybal() {
        return fvportcurybal;
    }

    public String getStrSecuritycode() {
        return strSecuritycode;
    }

    public double getFportcurybal() {
        return fportcurybal;
    }

    public String getStrFnum() {
        return strFnum;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public void setFbal(double fbal) {
        this.fbal = fbal;
    }

    public void setFbasecurybal(double fbasecurybal) {
        this.fbasecurybal = fbasecurybal;
    }

    public void setFmbal(double fmbal) {
        this.fmbal = fmbal;
    }

    public void setFmbasecurybal(double fmbasecurybal) {
        this.fmbasecurybal = fmbasecurybal;
    }

    public void setFmportcurybal(double fmportcurybal) {
        this.fmportcurybal = fmportcurybal;
    }

    public void setFstorageamount(double fstorageamount) {
        this.fstorageamount = fstorageamount;
    }

    public void setFvbal(double fvbal) {
        this.fvbal = fvbal;
    }

    public void setFvbasecurybal(double fvbasecurybal) {
        this.fvbasecurybal = fvbasecurybal;
    }

    public void setFvportcurybal(double fvportcurybal) {
        this.fvportcurybal = fvportcurybal;
    }

    public void setStrSecuritycode(String strSecuritycode) {
        this.strSecuritycode = strSecuritycode;
    }

    public void setFportcurybal(double fportcurybal) {
        this.fportcurybal = fportcurybal;
    }

    public void setStrFnum(String strFnum) {
        this.strFnum = strFnum;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }
}
