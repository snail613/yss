package com.yss.main.operdeal.platform.pfoper.inoutcfg.pojo;

public class InSourceParamBean {
    private String sTmpTab = ""; //临时表名称
    private String sSysTab = ""; //系统表名称
    private String sDelCond = ""; //删除条件
    public InSourceParamBean() {
    }

    public String getSTmpTab() {
        return sTmpTab;
    }

    public String getSSysTab() {
        return sSysTab;
    }

    public void setSDelCond(String sDelCond) {
        this.sDelCond = sDelCond;
    }

    public void setSTmpTab(String sTmpTab) {
        this.sTmpTab = sTmpTab;
    }

    public void setSSysTab(String sSysTab) {
        this.sSysTab = sSysTab;
    }

    public String getSDelCond() {
        return sDelCond;
    }
}
