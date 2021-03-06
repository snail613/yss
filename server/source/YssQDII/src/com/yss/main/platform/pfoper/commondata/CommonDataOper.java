package com.yss.main.platform.pfoper.commondata;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: 通用数据处理操作的类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CommonDataOper
    extends BaseBean {
    private BaseCommonData commonData = null;
    private String sBean = "";
    private String strAllData = "";
    public CommonDataOper() {
    }

    public void init(String sBean, String fstrAllData) {
        commonData = (BaseCommonData) pub.getPlatform().getBean(sBean);
        commonData.setYssPub(pub);
        /**shashijie 2012-7-2 STORY 2475 */
        strAllData = fstrAllData;
		/**end*/
    }

    /**
     * 操作类
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String doOperation(String sType) throws YssException {
    	//modify by zhangfa 20100827 MS01656    新建两条相同的数据，点击确定保存时系统会报错    QDV4赢时胜(测试)2010年08月25日04_B    
    	String temp="";
		try {
			if (sType != null && sType.equalsIgnoreCase("add")) {
				commonData.parseRowStr(strAllData);
				commonData.checkInput(YssCons.OP_ADD);
				commonData.addSetting();
			} else if (sType != null && sType.equalsIgnoreCase("edit")) {
				commonData.parseRowStr(strAllData);
				commonData.checkInput(YssCons.OP_EDIT);
				commonData.editSetting();
			} else if (sType != null && sType.equalsIgnoreCase("del")) {
				commonData.parseRowStr(strAllData);
				commonData.delSetting();
			} else if (sType != null && sType.equalsIgnoreCase("saveMutl")) {
				commonData.saveMutliSetting(strAllData);
			} else {
				temp= getOperValue(sType);
				//modify by zhangfa 20100906 MS01691    打开业务平台-特定数据处理报错    
				return temp;
				//--------------------------------------------------------------
			}
			temp=getListViewData("listview1");
		} catch (Exception e) {
			throw	new YssException(e.getMessage());
		}
		return temp;
		//---------------------------------------------
    }

    /**
     * 数据加载显示的方法
     * @return String
     * @throws YssException
     */
    public String getListViewData(String listView) throws YssException {
        commonData.parseRowStr(strAllData);
        if (listView.equalsIgnoreCase("listview1")) {
            return commonData.getListViewData();
        }
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] arrReq = null;
        arrReq = sRowStr.split("\r\f\r");
        sBean = arrReq[0];
        strAllData = arrReq[1];
        init(sBean, strAllData);

    }

    public String getOperValue(String sType) throws YssException {
        return commonData.operValue(sType);
    }
}
