package com.yss.main.dao;

import com.yss.util.*;

public interface IClientListView {
    /**
     * getListViewSetting1 ：
     * 获取选择listView控件的数据
     * @return String
     */
    public String getListViewData1() throws YssException;

    /**
     * getListViewData2 ：
     * 获取选择listView控件的数据
     * @return String
     */
    public String getListViewData2() throws YssException;

    /**
     * getListViewData2 ：
     * 获取选择listView控件的数据
     * @return String
     */
    public String getListViewData3() throws YssException;

    public String getListViewData4() throws YssException;
    /**
     *修改人  ：fanghaoln
     *修改时间 :20090512
     *BugNO   :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * 获取选择listView控件的数据
     * @return String
     */
    public String getListViewGroupData1() throws YssException;

    public String getListViewGroupData2() throws YssException;

    public String getListViewGroupData3() throws YssException;

    public String getListViewGroupData4() throws YssException;

    public String getListViewGroupData5() throws YssException;

}
