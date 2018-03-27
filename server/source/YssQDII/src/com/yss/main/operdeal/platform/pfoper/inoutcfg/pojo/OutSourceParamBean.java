package com.yss.main.operdeal.platform.pfoper.inoutcfg.pojo;

public class OutSourceParamBean {
    private String sTmpTabName = ""; //临时表名称
    private String sSqlSource = ""; //临时表的SQL语句,数据源
    private String sTmpData = ""; //临时表的数据 ,根据数据源查询出的数据
    public OutSourceParamBean() {
    }

    public String getSTmpTabName() {
        return sTmpTabName;
    }

    public String getSTmpData() {
        return sTmpData;
    }

    public void setSSqlSource(String sSqlSource) {
        this.sSqlSource = sSqlSource;
    }

    public void setSTmpTabName(String sTmpTabName) {
        this.sTmpTabName = sTmpTabName;
    }

    public void setSTmpData(String sTmpData) {
        this.sTmpData = sTmpData;
    }

    public String getSSqlSource() {
        return sSqlSource;
    }
}
