package com.yss.dbupdate.autoupdatetables.entitycreator.pojo;

;

/**
 * <p>Title: 表 TB_FUN_ConsCols 的实体类</p>
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
public class ConsColsBean {
    private String FConstraintName = "";
    private String FTableName = "";
    private String FColumnName = "";
    private String FPosition = "";
    public ConsColsBean() {
    }

    public String getFCOLUMNNAME() {
        return FColumnName;
    }

    public String getFCONSTRAINTNAME() {
        return FConstraintName;
    }

    public String getFPOSITION() {
        return FPosition;
    }

    public String getFTABLENAME() {
        return FTableName;
    }

    public void setFCOLUMNNAME(String FColumnName) {
        this.FColumnName = FColumnName;
    }

    public void setFCONSTRAINTNAME(String FConstraintName) {
        this.FConstraintName = FConstraintName;
    }

    public void setFPOSITION(String FPosition) {
        this.FPosition = FPosition;
    }

    public void setFTABLENAME(String FTableName) {
        this.FTableName = FTableName;
    }
}
