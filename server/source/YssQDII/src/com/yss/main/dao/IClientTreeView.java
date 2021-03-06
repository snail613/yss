package com.yss.main.dao;

import com.yss.util.*;

public interface IClientTreeView {
    /**
     * getTreeViewData1 ：
     * 获取选择TreeView控件的数据
     * @return String
     */
    public String getTreeViewData1() throws YssException;

    /**
     * getTreeViewData2 ：
     * 获取选择TreeView控件的数据
     * @return String
     */
    public String getTreeViewData2() throws YssException;

    /**
     * getTreeViewData3 ：
     * 获取选择TreeView控件的数据
     * @return String
     */
    public String getTreeViewData3() throws YssException;

    /**
     *修改人  ：fanghaoln
     *修改时间 :20090512
     *BugNO   :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * 获取选择treeView控件的数据
     * @return String
     */
    public String getTreeViewGroupData1() throws YssException;

    public String getTreeViewGroupData2() throws YssException;

    public String getTreeViewGroupData3() throws YssException;

}
