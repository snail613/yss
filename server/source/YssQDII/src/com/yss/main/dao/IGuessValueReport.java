package com.yss.main.dao;

import com.yss.dsub.*;
import com.yss.util.*;

public interface IGuessValueReport {
    public void setYssPub(YssPub pub) throws YssException;

    public String getParameter(String parameter) throws YssException;

    public String getGuessValueReport(String parameter) throws YssException;

    public String buildGuessValueReport(String parameter) throws YssException;

    public String dayConfirm(String parameter) throws YssException; //日终处理
    
    public String checkMarketvalue(String squest) throws YssException;//核对资产净值
    
    public String checkbgApp(String squest) throws YssException;  //add by fangjiang 2010.12.10 STORY #95 生成财务估值表时，与余额表进行比对，不一致需给出提示，并出报表
    
    public String checkRatio(String squest) throws YssException; //add by guyichuan 2011.06.30 符合本位币市值占净值比就给出提示
    
    public String CheckUndistributedProfit(String sRequest) throws YssException;  //20110715 added by liubo.Story #1194.核对财务估值表和净值统计表的未分配利润

    public String getReconcileInfo(String sParam) throws YssException;		//20120228 added by liubo.Story #2248.通过前台传入的组合跟报表日期，获取每日确认表信息

    public String ReconcileInfoOperation(String sParam) throws YssException;	//20120228 added by liubo.Story #2248.通过前台传入的组合跟报表日期，操作每日确认表（插入或删除）

    public String GetLockedStatus(String sParam) throws YssException;			//20120228 added by liubo.Story #2248.通过前台传入的组合跟报表日期，返回平台锁定/解锁联动信息

    public String GetDayConfirms(String sParam) throws YssException;			//20120301 added by liubo.Story #2248.获取某段时间内有无确认任何一天的估值表

    public String GetGVReportDescription(String sParam) throws YssException;
}
