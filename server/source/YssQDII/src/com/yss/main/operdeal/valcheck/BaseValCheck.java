package com.yss.main.operdeal.valcheck;

import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;

public class BaseValCheck
    extends BaseBean {
    protected ArrayList arrError = new ArrayList(); //保存出错的帐户信息
    protected String sIsError = ""; //是否有检查到违规数据    
    //20110713 added by liubo.Story #1145
    //**************************
    protected String sNeedLog = "false";	//是否需要生成日志。当“调度方案执行”窗体调用此类时，需要生成LOG
    protected String sLogPath = "";			//日志路径
    
    protected String sOperTime = "";		//操作时间
	
    protected String sPortCode = "";		//组合代码

    protected String sAssetGroupCode = "";	//组合群代码
    //--- story 2630 add by zhouwei 20120614 保存检查结果信息 start---//
    protected String checkInfos="";
     
    protected String sPluginValue="";	//20130424 added by liubo.Story #3528.监控指标阀值
    
    public String getPluginValue() {
		return sPluginValue;
	}

	public void setPluginValue(String sPluginValue) {
		this.sPluginValue = sPluginValue;
	}

	public String getCheckInfos() {
		return checkInfos;
	}

	public void setCheckInfos(String checkInfos) {
		this.checkInfos = checkInfos;
	}
	//--- story 2630 add by zhouwei 20120614 保存检查结果信息 end---//

    public String getsOperTime() {
		return sOperTime;
	}

	public void setsOperTime(String sOperTime) {
		this.sOperTime = sOperTime;
	}

	public String getsAssetGroupCode() {
		return sAssetGroupCode;
	}

	public void setsAssetGroupCode(String sAssetGroupCode) {
		this.sAssetGroupCode = sAssetGroupCode;
	}
	
    public String getsPortCode() {
		return sPortCode;
	}

	public void setsPortCode(String sPortCode) {
		this.sPortCode = sPortCode;
	}

    public String getsLogPath() {
		return sLogPath;
	}

	public void setsLogPath(String sLogPath) {
		this.sLogPath = sLogPath;
	}

	public String getsNeedLog() {
		return sNeedLog;
	}

	public void setsNeedLog(String sNeedLog) {
		this.sNeedLog = sNeedLog;
	}
	
	//************end************************


    public BaseValCheck() {
    }

    public String getIsError() {
        return sIsError;
    }

    public ArrayList getErrorArray() {
        return arrError;
    }

    public String doCheck(Date dTheDay, String sPortCodes) throws Exception {
        return "";
    }
    
    //20110713 added by liubo.Story #1145
    //生成调度方案执行日志的语句内容
    public void writeLog(String sLog) throws YssException
    {
    	YssUtil.WriteScheduleLog(sLog, sLogPath, sAssetGroupCode, sPortCode, sOperTime, "0");
    }
    
}
