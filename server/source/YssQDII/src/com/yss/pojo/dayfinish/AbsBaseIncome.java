package com.yss.pojo.dayfinish;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

abstract public class AbsBaseIncome
    extends BaseBean implements IYssConvert {
    public AbsBaseIncome() {
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {

    }

    public String buildShowStr() throws YssException {
        return "";
    }

    public String buildAllStr() throws YssException {
        return "";
    }

}
