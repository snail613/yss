package com.yss.main.dao;

import com.yss.dsub.*;
import com.yss.util.*;

public interface IBuildReport {
    public void setYssPub(YssPub pub);

    public void initBuildReport(BaseBean bean) throws YssException;

    public String buildReport(String sType) throws YssException;

    public String saveReport(String sReport) throws YssException;
    
    public String checkReportBeforeSearch(String sReport) throws YssException; //add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
}
