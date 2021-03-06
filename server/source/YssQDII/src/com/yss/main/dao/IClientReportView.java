package com.yss.main.dao;

import com.yss.dsub.*;
import com.yss.util.*;

public interface IClientReportView
    extends IYssConvert {
    public void setYssPub(YssPub pub);

    public String getReportHeaders(String sReportType) throws YssException;
    
  //20110516 Added by liubo #850  从前台获得一个组合代号，返回组合代号关联的套帐名称 
    public String getReportData(String sReportType) throws YssException;
    
    public String GetBookSetName(String sPortCode) throws YssException;
    
    public String checkReportBeforeSearch(String sReportType) throws YssException;  //add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
    
    /**shashijie 2011.04.07 STORY #805 头寸表应该预测T日到T+N-1日共N个工作日的头寸 */
    public String getSaveDefuntDay(String sRepotyType) throws YssException;
    /**end*/
    
}
