package com.yss.main.datainterface.compare;

import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.datainterface.compare.*;
import com.yss.util.*;
import com.yss.main.datainterface.DaoInterfaceManageBean;//by leeyu 20090727

/**
 * 接口核对功能的 控制类
 * QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090430
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
public class DaoCompManager
    extends DaoInterfaceManageBean implements IClientOperRequest {//更改继承类，不然流程执行时会报错 by leeyu 20090727
    private String sRowStrs = "";

    private DaoCompBase compBase = null;
    private DaoCompQuery compQue = null;
    public DaoCompManager() {
        compBase = new DaoCompImpInterface();
    }

    public String checkRequest(String sType) throws YssException {
        return "";
    }

    /**
     * 根据前台的请求执行DaoCompBase相应的方法
     * @param sType String
     * @return String 返回执行结果
     * @throws YssException
     */
    public String doOperation(String sType) throws YssException {
        String sResult = "";
        if (sType != null) {
            if (sType.equalsIgnoreCase("initLoad")) { //初始化时
                parseCompInitRowStr(sRowStrs);
                sResult = compBase.initLoadListView();
            } else if (sType.equalsIgnoreCase("CompAndQuery")) { //核对比较
                parseCompInitRowStr(sRowStrs);
                compBase.doPrepared(); //1做预处理
                sResult = compBase.getDataListView(); //2加载数据
            } else if (sType.equalsIgnoreCase("CompQuery")) { //查询数据
                parseCompInitRowStr(sRowStrs);
                sResult = compBase.getDataListView(); //加载数据
            } else if (sType.equalsIgnoreCase("SaveComp")) { //保存
                sResult = compBase.saveData(parseSaveRowStr(sRowStrs));
            }
        }
        return sResult;
    }

    /**
     * 初始化核对数据
     * @param sRowStr String
     * @throws YssException
     */
    private void parseCompInitRowStr(String sRowStr) throws YssException {
        compQue = new DaoCompQuery();
        compQue.setYssPub(pub);
        compQue.parseRowStr(sRowStr);
        compQue.getHtDynamic();
        compBase.setDaoCompQuery(compQue);
        compBase.init(compQue.getSCompCode(), compQue.getSCompType(),
                      compQue.getDStartDate(), compQue.getDEndDate(),
                      compQue.getHtDynamic());
    }

    private ArrayList parseSaveRowStr(String sRowStr) throws YssException {
        ArrayList alData = new ArrayList();
        String[] arrData;
        String[] arrPars = sRowStr.split("\f\f");
        compQue = new DaoCompQuery();
        compQue.setYssPub(pub);
        compQue.setDStartDate(YssFun.toDate(arrPars[0]));
        compQue.setDEndDate(YssFun.toDate(arrPars[1]));
        compQue.setSCompType(arrPars[2]);
        compQue.setSCompCode(arrPars[3]);
        compBase.setDaoCompQuery(compQue);
        compBase.init(compQue.getSCompCode(), compQue.getSCompType(),
                      compQue.getDStartDate(), compQue.getDEndDate(),
                      compQue.getHtDynamic());
        if(arrPars[4].equalsIgnoreCase("1")){//若值为1,则为已审核状态
            compBase.setCheckState("true");
        }else{
            compBase.setCheckState("false");
        }
        if (arrPars.length > 6) {//QDV4赢时胜（上海）2009年7月22日01_A MS00574 by leeyu 20090729 增加审核状态 调整序号
            arrData = arrPars[5].split("\r\n");//QDV4赢时胜（上海）2009年7月22日01_A MS00574 by leeyu 20090729 增加审核状态 调整序号
            for (int i = 0; i < arrData.length; i++) {
                alData.add(arrData[i]);
            }
        }
        return alData;
    }

    /**
     * 解析数据
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        this.sRowStrs = sRowStr;
        compBase.setYssPub(pub);
    }

    /**
     * 数据统一格式化
     * @return String
     * @throws YssException
     */
    public String buildRowStr() {//由于继承类更改了，所以要与继承类的方法一致 by leeyu 20090727
        return "";
    }

    /**
     * 获取一些方法
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String getOperValue(String sType) throws YssException {
        String sResult = "";

        return sResult;
    }

}
