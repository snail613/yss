package com.yss.main.operdeal.platform.pfsystem.operfunextend.pojo;

/**
 * <p>Title: 表 TB_PFSys_OperFunExtend 的实体类</p>
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
public class OperFunExtendPojo {
    private String extCode = ""; //配置代码
    private String extName = ""; //配置名称
    private String pubParaCode = ""; //参数编号
    private String linkModule = ""; //关联模块
    private String extScript = ""; //配置脚本
    private int enable; //是否可用
    private String desc = ""; //描述

    public OperFunExtendPojo() {
    }

    public String getDesc() {
        return desc;
    }

    public int getEnable() {
        return enable;
    }

    public String getExtCode() {
        return extCode;
    }

    public String getExtName() {
        return extName;
    }

    public String getExtScript() {
        return extScript;
    }

    public String getLinkModule() {
        return linkModule;
    }

    public String getPubParaCode() {
        return pubParaCode;
    }

    public void setPubParaCode(String pubParaCode) {
        this.pubParaCode = pubParaCode;
    }

    public void setLinkModule(String linkModule) {
        this.linkModule = linkModule;
    }

    public void setExtScript(String extScript) {
        this.extScript = extScript;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
