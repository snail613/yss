package com.yss.main.operdeal.cashmanage;

import com.yss.base.BaseCalcFormula;
import com.yss.util.YssException;

/**
 * <p>Title: </p>
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
public class BaseCommandOper
    extends BaseCalcFormula {
    private Object initObj = null;
    public BaseCommandOper() {
    }

    public void init(Object obj) throws YssException {
        initObj = obj;
    }

    /**
     * calInvest
     *�������ֵ
     */
    public double calcInvest() throws YssException {
        return 0;
    }

}
