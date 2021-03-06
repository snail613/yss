package com.yss.pojo.param.comp;

import com.yss.dsub.*;
import com.yss.main.dao.*;

public class YssCommonRepCtl
    extends BaseBean implements IYssConvert {
    private String ctlValue;
    private String ctlIndex;
    private String ctlNameCode = ""; //增加控件名称,因为后台可能要根据控件名称取控件的相关参数 by leeyu
    public String getCtlValue() {
        return ctlValue;
    }

    public void setCtlIndex(String ctlIndex) {
        this.ctlIndex = ctlIndex;
    }

    public void setCtlValue(String ctlValue) {
        this.ctlValue = ctlValue;
    }

    public void setCtlNameCode(String ctlName) {
        this.ctlNameCode = ctlName;
    }

    public String getCtlIndex() {
        return ctlIndex;
    }

    public String getCtlNameCode() {
        return ctlNameCode;
    }

    public YssCommonRepCtl() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
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
        String reqAry[] = null;
        if (sRowStr.trim().length() == 0) {
            return;
        }

        reqAry = sRowStr.split("\r");
        this.ctlIndex = reqAry[0];
        this.ctlValue = reqAry[1];

        if (reqAry.length > 2) { // 判断, by leeyu
            this.ctlNameCode = reqAry[2];
        }

    }
}
