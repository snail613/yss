package com.yss.main.dao;

import com.yss.dsub.*;
import com.yss.util.*;

public interface IDataInterface
    extends IYssConvert {

    public void setYssPub(YssPub pub) throws YssException;

    public void importData(String sRequestStr) throws YssException;

    public String exportData(String sRequestStr) throws YssException;

}
