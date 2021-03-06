package com.yss.dbupdate.autoupdatetables.entitycreator.pojo;

import java.util.*;

/**
 * <p>Title: 表 TB_Fun_CONSTRAINTS 的实体类</p>
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
public class ConstraintsBean {
    private String FConstraintName = "";
    private String FTableName = "";
    private String FConType = "";
    /**
     * 持有类 ConsColsBean 的引用
     */
    private ArrayList ConsCols;

    public ConstraintsBean() {
    }

    public String getFCONSTRAINTNAME() {
        return FConstraintName;
    }

    public String getFCONTYPE() {
        return FConType;
    }

    public String getFTABLENAME() {
        return FTableName;
    }

    public void setFCONSTRAINTNAME(String FConstraintName) {
        this.FConstraintName = FConstraintName;
    }

    public void setFCONTYPE(String FConType) {
        this.FConType = FConType;
    }

    public void setFTABLENAME(String FTableName) {
        this.FTableName = FTableName;
    }

    public ArrayList getConsCols() {
        return ConsCols;
    }

    public void setConsCols(ArrayList ConsCols) {
        this.ConsCols = ConsCols;
    }

}
