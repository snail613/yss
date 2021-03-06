package com.yss.base;

import com.yss.main.dao.*;
import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.main.dao.IOperValue;

//该类做为IOperValue接口的适配器
public class BaseAPOperValue
    extends BaseBean implements IOperValue {
    public BaseAPOperValue() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        return 0.0;
    }

    /**
     * getOperStrValue
     *
     * @return String
     */
    public String getOperStrValue() throws YssException {
        return "";
    }

    /**
     * init
     *
     * @param bean Object
     */
    public void init(Object bean) throws YssException {
    }

    /**
     * invokeOperMothed
     *
     * @return Object
     */
    public Object invokeOperMothed() throws YssException {
        return "";
    }

    /**
     * getTypeValue
     *
     * @param sType String
     * @return Object
     */
    public Object getTypeValue(String sType) throws YssException {
        return "";
    }
}
