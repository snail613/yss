package com.yss.main.dao;

import com.yss.util.*;

public interface IYssConvert {
    /**
     * parseRowStr：
     * 解析一行数据，放入类的对应属性中
     * @param sRowStr String：发送过来的一行请求
     */
    public void parseRowStr(String sRowStr) throws YssException;

    /**
     * buildRowStr：
     * 产生一行的数据
     * @return String
     */
    public String buildRowStr() throws YssException;

    /**
     * getOperValue :
     * 获取对象中特定变量的值
     * @return String
     */
    public String getOperValue(String sType) throws YssException;

}
