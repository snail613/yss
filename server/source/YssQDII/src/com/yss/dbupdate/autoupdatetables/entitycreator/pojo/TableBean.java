package com.yss.dbupdate.autoupdatetables.entitycreator.pojo;

import java.util.*;

/**
 *
 * <p>Title: 表 TB_FUN_AllTableName 的实体类</p>
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
public class TableBean {
    //表名
    private String FTableName = "";
    //表空间名
    private String FTableSpaceName = "";
    /**
     * 表的所有列，持有类 ColumnsBean 的引用
     */
    private ArrayList columns = new ArrayList();
    /**
     * 表的主键， 持有类 pkCons 的引用
     */
    private ConstraintsBean pkCons = null;

    public TableBean() {
    }

    public ArrayList getColumns() {
        return columns;
    }

    public ConstraintsBean getPkCons() {
        return pkCons;
    }

    public String getFTableSpaceName() {
        return FTableSpaceName;
    }

    public String getFTableName() {
        return FTableName;
    }

    public void setColumns(ArrayList columns) {
        this.columns = columns;
    }

    public void setPkCons(ConstraintsBean pkCons) {
        this.pkCons = pkCons;
    }

    public void setFTableSpaceName(String FTableSpaceName) {
        this.FTableSpaceName = FTableSpaceName;
    }

    public void setFTableName(String FTableName) {
        this.FTableName = FTableName;
    }
}
