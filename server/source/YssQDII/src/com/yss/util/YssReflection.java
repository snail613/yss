package com.yss.util;

import java.lang.reflect.*;

public class YssReflection {
    public YssReflection() {
    }

    public static Object getPropertyValue(Object tagObj, String sPropName) throws
        YssException {
        Method method = null;
        Class ownerClass = null;
        Object reObj = null;
        //-----------------2008.04.18 ���� ����-------------------//
        Object[] obj = new Object[0];
        Class[] cls = new Class[0];
        //-------------------------------------------------------//
        try {
            ownerClass = tagObj.getClass();
            method = ownerClass.getMethod("get" + sPropName, cls);
            reObj = method.invoke(tagObj, obj);
            return reObj;
        } catch (Exception e) {
            throw new YssException(e);
        }
    }
}
