package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.dsub.BaseBean;
import com.yss.util.YssException;
import java.util.Hashtable;
import java.util.ArrayList;

public class BasePubParaDeal
    extends BaseBean {
    protected String pubParaCode = ""; //参数编号
    protected String paraGroupCode = ""; //参数组编号
    protected java.util.Hashtable equalsValues = null; //判断所需的值，以key值为标识。
    protected java.util.Hashtable inds = null; //控件标识。
    protected String resultInd = ""; //反回值的控件标识
    public void setPubParaCode(String pubParaCode) {
        this.pubParaCode = pubParaCode;
    }

    public void setEqualsValues(Hashtable equalsValues) {
        this.equalsValues = equalsValues;
    }

    public void setParaGroupCode(String paraGroupCode) {
        this.paraGroupCode = paraGroupCode;
    }

    public void setInds(Hashtable inds) {
        this.inds = inds;
    }

    public void setResultInd(String resultInd) {
        this.resultInd = resultInd;
    }

    public String getPubParaCode() {
        return pubParaCode;
    }

    public Hashtable getEqualsValues() {
        return equalsValues;
    }

    public String getParaGroupCode() {
        return paraGroupCode;
    }

    public Hashtable getInds() {
        return inds;
    }

    public String getResultInd() {
        return resultInd;
    }

    public BasePubParaDeal() {
    }

    //---------------------------------------------------------------------------
    /**
     * 获取符合条件的参数值
     * @throws YssException
     * @return Object
     */
    protected Object getParaResult() throws YssException {
        return null;
    }

    /**
     * 解析判决条件
     * @throws YssException
     * @return ArrayList
     */
    protected ArrayList parseValues() throws YssException {
        return null;
    }

    /**
     * 解析符合条件的参数值，因为可能返回的值并不是一个我们需要的单个值。例如combox的值就是两个值得窜连。
     * @throws YssException
     * @return Object
     */
    protected Object parseResults(String results) throws YssException {
        return null;
    }
}
