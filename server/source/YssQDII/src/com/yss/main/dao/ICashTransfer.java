package com.yss.main.dao;

import com.yss.dsub.*;
import com.yss.util.*;

public interface ICashTransfer {
    public void setYssPub(YssPub pub);

    public void init(BaseBean param) throws YssException;

    public String cashInTarget() throws YssException;

    public String cashOutTarget() throws YssException;
}
