package com.yss.pojo.param.reptab;

import com.yss.main.dao.*;
import com.yss.dsub.*;
import java.util.Date;
import com.yss.util.*;

public class YssYearAvgCost
    extends BaseBean implements IYssConvert {

    private String ctlIndex;
    private java.util.Date ctlDate; //�ؼ�����

    public void setCtlIndex(String ctlIndex) {
        this.ctlIndex = ctlIndex;
    }

    public void setCtlDate(Date ctlDate) {
        this.ctlDate = ctlDate;
    }

    public String getCtlIndex() {
        return ctlIndex;
    }

    public Date getCtlDate() {
        return ctlDate;
    }

    public YssYearAvgCost() {
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
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String reqAry1[] = null;
        if (sRowStr.trim().length() == 0) {
            return;
        }
        reqAry = sRowStr.split("\n");
        reqAry1 = reqAry[0].split("\r");
        this.ctlIndex = reqAry1[0];
        this.ctlDate = YssFun.toDate(reqAry1[1]);
    }
}
