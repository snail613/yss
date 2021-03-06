package com.yss.main.dao;

import com.yss.dsub.*;
import com.yss.util.*;

public interface IClientOperRequest
    extends IYssConvert {
    public void setYssPub(YssPub pub);

    public String checkRequest(String sType) throws YssException;

    public String doOperation(String sType) throws YssException;
}
