package com.yss.main.operdeal.platform.pfoper.scheduling.pojo;

/**
 *
 * <p>Title: </p>
 * <p>Description: �� Tb_PFOper_SchProject ��ʵ��</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SchProjectPojo {
    private String projectCode = "";
    private String projectName = "";
    private String funModules = "";
    private String attrCode = "";
    private String exeOrderCode = "";
    private String desc = "";
    public String getAttrCode() {
        return attrCode;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public String getExeOrderCode() {
        return exeOrderCode;
    }

    public String getFunModules() {
        return funModules;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setExeOrderCode(String exeOrderCode) {
        this.exeOrderCode = exeOrderCode;
    }

    public void setFunModules(String funModules) {
        this.funModules = funModules;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public SchProjectPojo() {
    }
}
