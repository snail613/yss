package com.yss.commeach;

import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.dsub.BaseBean;

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
public class BaseCommEach
    extends BaseBean implements IYssConvert {
    public BaseCommEach() {
    }

    /**
     * buildRowStr： 产生一行的数据
     *
     * @return String
     * @throws YssException
     * @todo Implement this com.yss.main.dao.IYssConvert method
     */
    public String buildRowStr() throws YssException {
        return "";
    }

    /**
     * getOperValue : 获取对象中特定变量的值
     *
     * @return String
     * @param sType String
     * @throws YssException
     * @todo Implement this com.yss.main.dao.IYssConvert method
     */
    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * parseRowStr： 解析一行数据，放入类的对应属性中
     *
     * @param sRowStr String：发送过来的一行请求
     * @throws YssException
     * @todo Implement this com.yss.main.dao.IYssConvert method
     */
    public void parseRowStr(String sRowStr) throws YssException {
    }
}
