package com.yss.main.dao;

import com.yss.dsub.*;
import com.yss.util.*;

public interface IDataSetting
    extends IYssLogData, IYssConvert, IClientListView, IClientTreeView {

    public void setYssPub(YssPub pub);

    /**
     * checkInput : 验证要保存的设置信息
     * @param btOper byte ： 操作类型，见YssCons中的操作类型
     */
    public void checkInput(byte btOper) throws YssException;

    /**
     * saveSetting ：
     * 新增，修改，删除，审核设置信息
     * @param btOper byte ： 操作类型，见YssCons中的操作类型
     */
//   public void saveData(byte btOper) throws YssException;

    /**
     * addOperData：
     * 增加一条设置信息，先通过parseRowStr解析发送过来的请求，再通过类的属性增加到数据库中
     * @return String： 因为有些属性的值需要在后台进行计算，所以可能和发送过来的请求不一致，故这条信息返回给客户端。
     */
    public String addSetting() throws YssException;

    /**
     * editSetting：
     * 修改一条设置信息，先通过parseRowStr解析发送过来的请求，再通过类的属性修改到数据库中
     * @return String： 因为有些属性的值需要在后台进行计算，所以可能和发送过来的请求不一致，故这条信息返回给客户端。
     */
    public String editSetting() throws YssException;

    /**
     * delSetting : 删除一条设置信息
     */
    public void delSetting() throws YssException;

    /**
     * auditSetting : 审核一条设置信息
     */
    public void checkSetting() throws YssException;

    /**
     * saveMutliSetting ：
     * 多条设置信息同时保存
     * @param sMutilRowStr String ： 发送过来的多行请求
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException;

    /**
     * getSetting ：
     * 获取一条设置信息
     * @return ParaSetting
     */
    public IDataSetting getSetting() throws YssException;

    /**
     * getAllSetting ：
     * 获取所有的设置信息
     * @return String
     */
    public String getAllSetting() throws YssException;

    /**
     * getPartSetting ：
     * 根据指定条件，获取所有的设置信息
     * @return String
     */
//   public String getPartSetting() throws YssException;
    public void deleteRecycleData() throws YssException;

}
