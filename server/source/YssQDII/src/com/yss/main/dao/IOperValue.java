package com.yss.main.dao;

import com.yss.dsub.*;
import com.yss.util.*;

public interface IOperValue {
    public void setYssPub(YssPub pub);

    public void init(Object bean) throws YssException;

    public double getOperDoubleValue() throws YssException;

    public String getOperStrValue() throws YssException;

    public Object invokeOperMothed() throws YssException;

    public Object getTypeValue(String sType) throws YssException;
}
