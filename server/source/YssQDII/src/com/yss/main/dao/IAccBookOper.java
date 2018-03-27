package com.yss.main.dao;

import com.yss.dsub.*;
import com.yss.util.*;

public interface IAccBookOper {
    public void setYssPub(YssPub pub);

    public void setBookClassTable(String sBookDefine, int iShowType) throws
        YssException;

    public String getBookSql(String sBookDefine, String sBookLink,
                             java.util.Date dBeginDate,
                             java.util.Date dEndDate, int iShowType, String sCheckItems) throws
        YssException;

}
