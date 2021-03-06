package com.yss.pojo.sys;

import java.util.*;

import com.yss.main.dao.*;

public class YssStatus
    implements IYssConvert {
    private HashMap hmRunDesc;
    private String runKey = "";
    private String runDesc = "";
    public String getRunKey() {
        return runKey;
    }

    public void setRunDesc(String runDesc) {
        this.runDesc = runDesc;
    }

    public void setRunKey(String runKey) {
        this.runKey = runKey;
    }

    public String getRunDesc() {
        return runDesc;
    }

    public YssStatus() {
        hmRunDesc = new HashMap();
    }

    public void appendRunDesc(String sRunKey, String sRunDesc, String sMark) {
        String sDesc = "";
        if (hmRunDesc.containsKey(sRunKey)) {
            sDesc = (String) hmRunDesc.get(sRunKey);
        }
        sDesc += sRunDesc + sMark;
        hmRunDesc.put(sRunKey, sDesc);
    }

    public void appendRunDesc(String sRunKey, String sRunDesc) {
        appendRunDesc(sRunKey, sRunDesc, "\r\n");
    }

    public void appendValRunDesc(String sRunDesc) {
        appendRunDesc("ValRun", sRunDesc, "\r\n");
    }

    /**
     * 用于业务平台——调度方案执行
     * 2008-04-07 蒋锦 添加
     * @param sRunDesc String
     */
    public void appendSchRunDesc(String sRunDesc) {
        appendRunDesc("SchRun", sRunDesc, "\r\n");
    }

    public void appendSchRunDesc(String sRunDesc, String sMark) {
        appendRunDesc("SchRun", sRunDesc, sMark);
    }

    /**
     * 用于日终处理——估值检查
     * 2008-04-07 蒋锦 添加
     * @param sRunDesc String
     */
    public void appendValCheckRunDesc(String sRunDesc) {
        appendRunDesc("ValCheckRun", sRunDesc, "\r\n");
    }

    public void clearRunDesc(String sRunKey) {
        if (hmRunDesc.containsKey(sRunKey)) {
            hmRunDesc.remove(sRunKey);
        }
    }

    public void clearRunDesc() {
        clearRunDesc(this.runKey);
    }

    public void clearValRunDesc() {
        clearRunDesc("ValRun");
    }

    /**
     * 2008-04-07 蒋锦 添加
     * 对应 appendSchRunDesc 添加方法的删除方法
     */
    public void clearSchRunDesc() {
        clearRunDesc("SchRun");
    }

    /**
     * 2008-04-07 蒋锦 添加
     * 对应 appendValCheckRunDesc 添加方法的删除方法
     */
    public void clearValCheckDesc() {
        clearRunDesc("ValCheckRun");
    }

    public String getRunDesc(String sRunKey) {
        String sResult = "";
        if (hmRunDesc.containsKey(sRunKey)) {
            sResult = (String) hmRunDesc.get(sRunKey);
        }
        return sResult;
    }

    public String getValRunDesc() {
        return getRunDesc("ValRun");
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(getRunDesc(runKey));
        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
        String[] sRowAry = sRowStr.split("\t");
        runKey = sRowAry[0];
    }
}
