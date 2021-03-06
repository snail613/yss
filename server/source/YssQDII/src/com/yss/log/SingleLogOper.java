package com.yss.log;

import java.sql.ResultSet;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class SingleLogOper {
    private OperateLogBean log = null;
    //private static SingleLogOper logOper = null;
    private DayFinishLogBean dayfinishlog=new DayFinishLogBean();//新增日终处理日志处理类 add by zhouxiang 2010.11.23 
    private YssPub cPub = null;

//   private IDataSetting iData = null;
    private Object iData = null;
    private IDataSetting bData = null;

    //---add by songjie 2012.10.16 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    public DayFinishLogBean getDayFinishLog(){
    	return dayfinishlog;
    }
    //---add by songjie 2012.10.16 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    
    //20130417 added by liubo.Story #3528
    //此变量用于控制在权益处理时，债券派息或债券兑付时，有数据生成，预警日志记录该条数据时，状态为“提醒”
    //值为0，表示不处理；值为1，表示需要将该条权益处理的日志数据的状态变为“提醒”
    //===============================
    private int iFixInterestCheck = 0;
    
    public int getFixInterestCheck() {
		return iFixInterestCheck;
	}
	public void setFixInterestCheck(int iFixInterestCheck) {
		this.iFixInterestCheck = iFixInterestCheck;
	}
    //===============end================
	
	/**
     * 设置日志内容对象
     * @param iData IDataSetting
     * @param sType int
     * @param pub YssPub
     * @throws YssException
     */
    public void setIData(Object iData, int sType, YssPub pub, boolean isError) throws
        YssException {
        //synchronized (log) { //同步
    	
            this.cPub = pub;
            
            /**Start 20140108 added by liubo.Bug #85825. QDV4赢时胜(上海)2013年12月18日01_B
             * 管理员对用户进行中断操作时，用户再次做操作可能就会在日志表里面取到NULL的PUB对象，这个时候全都会报500错
             * 因此这里需要判断一下*/
    		if (cPub == null)
    		{
    			return ;
    		}
    		/**Start 20140108 added by liubo.Bug #85825. QDV4赢时胜(上海)2013年12月18日01_B*/
    		
            this.iData = iData;
            log.setOperateType(sType);
            if (isError) {
                insertErrorLog();
            } else {
                insertLog();
            }
        //}
    }
//---------------------add by zhoxiang 产生对应日终处理的专属日志 2010.11.23 -------------------------
    /**
     * 设置日志内容对象 -日终处理
     * @param iData IDataSetting --一个Object 存储的是BaseBean的功能代码数据等
     * @param sType int//操作类型 比如业务处理,收益计提
     * @param pub YssPub//存储的是登陆的用户，用户IP， isError 是否是错误的日志
     * @param portcodes 日终处理日志类中的组合代码，begindate-enddate  业务区间 ,deTails 处理的项目明细 
     * @throws YssException
     */
    //edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logStartTime、logSumCode、logEndTime
    public void setDayFinishIData(Object iData, int sType,String operItem,YssPub pub,
    		boolean isError,String portCode,java.util.Date beginDate,
    		java.util.Date logDate,java.util.Date endDate,String deTails,
    		java.util.Date logStartTime,String logSumCode, java.util.Date logEndTime) throws
        YssException {
    		this.cPub = pub;
    		this.iData = iData;
    		this.dayfinishlog.setAccType("");//日终处理中的业务处理，资产估值，权益处理，库存统计是没有账户类型的
    		this.dayfinishlog.setLogDate(logDate);
            this.dayfinishlog.setOperType(sType);
            this.dayfinishlog.setPortcodes(portCode);
            this.dayfinishlog.setStartDate(beginDate);
            this.dayfinishlog.setEndDate(endDate);
            this.dayfinishlog.setItemDetail(deTails);
            this.dayfinishlog.setOperItem(operItem);
            
            //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            this.dayfinishlog.setBeginTime(logStartTime);//开始时间
            this.dayfinishlog.setEndTime(logEndTime);//结束时间
            this.dayfinishlog.setLogSumCode(logSumCode);//汇总日志编号
            //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            
            dayfinishlog.setYssPub(pub);// add by lidaolong #386 增加一个功能，能够自动支付管理费和托管费
            if (isError) {
                insertDayFinishErrorLog();
            } else {
                insertDayFinishLog();
            }
    }
	
	//edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logStartTime、logSumCode、logEndTime
    public void setDayFinishIData(Object iData, int sType,String operItem, YssPub pub, 
    		boolean isError,String portCode,java.util.Date beginDate,
    		java.util.Date logDate,java.util.Date endDate,String deTails,
    		String acctype ,java.util.Date logStartTime,String logSumCode, java.util.Date logEndTime) throws
    YssException {
		this.dayfinishlog.setAccType(acctype);//收益计提和收益支付需要账户类型的参数
		this.cPub = pub;
		this.iData = iData;
		this.dayfinishlog.setLogDate(logDate);
        this.dayfinishlog.setOperType(sType);//操作类型
        this.dayfinishlog.setPortcodes(portCode);
        this.dayfinishlog.setStartDate(beginDate);
        this.dayfinishlog.setEndDate(endDate);
        this.dayfinishlog.setOperItem(operItem);//处理项目
        this.dayfinishlog.setItemDetail(deTails);//处理子项目--处理明细
        this.dayfinishlog.setAccType(acctype);
        
        //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        this.dayfinishlog.setBeginTime(logStartTime);
        this.dayfinishlog.setEndTime(logEndTime);
        this.dayfinishlog.setLogSumCode(logSumCode);
        //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        
        if (isError) {
            insertDayFinishErrorLog();
        } else {
            insertDayFinishLog();
        }
}
    private void insertDayFinishLog() throws YssException {
    	if (iFixInterestCheck == 0)
    	{
    		dayfinishlog.setOperResultCode("1");
    	}
    	else
    	{
    		dayfinishlog.setOperResultCode("2");
    	}
    	
    	dayfinishlog.setYssPub(cPub);
        try {
        	dayfinishlog.insertLog(this.iData);
            this.iData = null;
        } catch (YssException ex) {
//         log.setOperateResult("0");
        	//---edit by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        	try {
        		insertDayFinishErrorLog();
        	}
        	catch (YssException ex1) {
        		throw new YssException("插入错误日志出错！", ex1);
        	}
        	//---edit by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        }
	}
	private void insertDayFinishErrorLog() throws YssException {
    	dayfinishlog.setOperResultCode("0");
    	dayfinishlog.setYssPub(cPub);
        try {
        	dayfinishlog.insertLog(this.iData);
        } catch (YssException ex1) {
            throw new YssException("插入错误日志出错！", ex1);
        }
	}
//---------------------end by zhoxiang 产生对应日终处理的专属日志 2010.11.23 -------------------------
	/**
     * 为正确的日志插入。
     * @param iData Object
     * @param sType int
     * @param pub YssPub
     * @throws YssException
     */
    public void setIData(Object iData, int sType, YssPub pub) throws YssException {
        setIData(iData, sType, pub, false);
    }

    /**
     * 设置修改前内容对象
     * @param bData IDataSetting
     * @throws YssException
     */
    public void setBData(IDataSetting bData) throws YssException {
        synchronized (log) { //同步
            this.bData = bData;
            getBeforeData();
        }
    }

    public Object getIData() {
        return iData;
    }

    public IDataSetting getBData() {
        return bData;
    }

    private SingleLogOper() {
        if (this.log == null) {
            this.log = new OperateLogBean();
        }
    }

    public static SingleLogOper getInstance() {
    	//取消单例模式
    	//由于日志编号改为从sequence获取，此处的单例就没必要了
    	SingleLogOper logOper = new SingleLogOper();
        return logOper; 
    }

    private void insertErrorLog() throws YssException {
        log.setOperateResult("0");
        log.setYssPub(cPub);
        try {
        	//add by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A
        	log.setYssPub(this.cPub);//set pub
            log.insertLog(this.iData);
        } catch (YssException ex1) {
            throw new YssException("插入错误日志出错！", ex1);
        }
    }

    /**
     * 调用日志对象，执行日志插入。
     * @throws YssException
     */
    private void insertLog() throws YssException {
        log.setOperateResult("1");
        log.setYssPub(cPub);
        try {
            log.insertLog(this.iData);
            this.iData = null;
        } catch (YssException ex) {
//         log.setOperateResult("0");
//         try {
//            log.insertLog(this.iData);
//         }
//         catch (YssException ex1) {
//            throw new YssException("插入错误日志出错！", ex1);
//         }
            insertErrorLog();
        }
    }

    /**
     * 获取修改前对象内容。
     * @throws YssException
     */
    private void getBeforeData() throws YssException {
        try {
            if (this.bData != null) {
                log.setBData(this.bData.getBeforeEditData());
                this.bData = null;
            } else {
                throw new YssException("无修改前数据!");
            }
        } catch (YssException ex2) {
            throw new YssException("获取修改前数据出错！", ex2);
        }
    }
    

}
