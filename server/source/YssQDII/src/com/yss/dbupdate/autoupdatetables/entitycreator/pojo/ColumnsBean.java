package com.yss.dbupdate.autoupdatetables.entitycreator.pojo;

/**
 * <p>Title: 表 TB_Fun_Columns 的实体类</p>
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
public class ColumnsBean {
    private String FTABLENAME = "";
    private String FCLOUMNNAME = "";
    private String FCOLUMNID = "";
    private String FDATATYPE = "";
    private String FDATALENGTH = "";
    private String FDATAPRECISION = "";
    private String FDATASCALE = "";
    private String FNULLABLE = "";
    private String FDEFULTVALUE = "";
    private String FINSERTSCRIPT = "";
    public ColumnsBean() {
    }

    public String getFCLOUMNNAME() {
        return FCLOUMNNAME;
    }

    public void setFCLOUMNNAME(String FCLOUMNNAME) {
        this.FCLOUMNNAME = FCLOUMNNAME;
    }

    public String getFCOLUMNID() {
        return FCOLUMNID;
    }

    public String getFDATALENGTH() {
        return FDATALENGTH;
    }

    public String getFDATAPRECISION() {
        return FDATAPRECISION;
    }

    public String getFDATASCALE() {
        return FDATASCALE;
    }

    public String getFDATATYPE() {
        return FDATATYPE;
    }

    public String getFDEFULTVALUE() {
        return FDEFULTVALUE;
    }

    public String getFNULLABLE() {
        return FNULLABLE;
    }

    public String getFTABLENAME() {
        return FTABLENAME;
    }

    public void setFTABLENAME(String FTABLENAME) {
        this.FTABLENAME = FTABLENAME;
    }

    public void setFNULLABLE(String FNULLABLE) {
        this.FNULLABLE = FNULLABLE;
    }

    public void setFDEFULTVALUE(String FDEFULTVALUE) {
        this.FDEFULTVALUE = FDEFULTVALUE;
    }

    public void setFDATATYPE(String FDATATYPE) {
        this.FDATATYPE = FDATATYPE;
    }

    public void setFDATASCALE(String FDATASCALE) {
        this.FDATASCALE = FDATASCALE;
    }

    public void setFDATAPRECISION(String FDATAPRECISION) {
        this.FDATAPRECISION = FDATAPRECISION;
    }

    public void setFDATALENGTH(String FDATALENGTH) {
        this.FDATALENGTH = FDATALENGTH;
    }

    public void setFCOLUMNID(String FCOLUMNID) {
        this.FCOLUMNID = FCOLUMNID;
    }

    public String getFINSERTSCRIPT() {
        return FINSERTSCRIPT;
    }

    public void setFINSERTSCRIPT(String FINSERTSCRIPT) {
        this.FINSERTSCRIPT = FINSERTSCRIPT;
    }
}
