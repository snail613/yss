package com.yss.commeach;

import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.dsub.BaseBean;
import com.yss.vsub.YssFinance;

/// <summary>
/// 创建人：fanghaoln
/// 创建时间:20090430
/// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
///  这个类是为了通过套账代码得到组合代码
/// </summary>
public class EachPortCode
    extends BaseCommEach {
    public EachPortCode() {
    }

    /**
     * 此方法是继承BaseCommEach类里面getOperValue方法，通过传进来的套账代码得到组合代码
     * 创建人：fanghaoln
     * 创建时间:20090430
     * BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
     */
    public String getOperValue(String sType) throws YssException {
        YssFinance finance = new YssFinance(); //new 一个YssFinance类，这个类中有一个专门通过套账代码查组合代码的方法
        finance.setYssPub(pub); //设置公共信息
        String sPortCode = finance.getPortCode(sType); //调用方法得到组合代码
        return sPortCode; //返回组合代码
    }

}
