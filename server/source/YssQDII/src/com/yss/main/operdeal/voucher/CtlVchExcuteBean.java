package com.yss.main.operdeal.voucher;

import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.log.DayFinishLogBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.voucher.vchbuild.*;
import com.yss.main.operdeal.voucher.vchcheck.*;
import com.yss.main.operdeal.voucher.vchout.*;
import com.yss.main.voucher.*;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.vsub.*;

public class CtlVchExcuteBean
    extends BaseBean implements IClientOperRequest {
    public CtlVchExcuteBean() {
    }

    private String portCodes = "";
    private String beginDate = "";
    private String endDate = "";
    private String vchTypes = "";
    private String inVchTypes = ""; //内部获取凭证属性代码，用于以方案形式生成凭证用。
    private String beanId = ""; //执行的操作类型
    private String freeParams = ""; //自由定义参数
    private boolean isInData = false; //凭证属性是前台传递还是后台获取
	//edit by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	public SingleLogOper logOper;//private 改为 public 
	private byte operType;
	private String strInvokeType = "";		//20120412 added by liubo.Story #2192.调用类型.通过凭证检查界面调用此类，值为FrmVchCheck，其他情况下均为""
    
	//---add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	public boolean comeFromDD = false;
	public String logSumCode = "";
	//---add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	
	public String getPortCodes() {
        return portCodes;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getVchTypes() {
        return vchTypes;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setVchTypes(String vchTypes) {
        this.vchTypes = vchTypes;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public void setFreeParams(String freeParams) {
        this.freeParams = freeParams;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getBeanId() {
        return beanId;
    }

    public String getFreeParams() {
        return freeParams;
    }

    //public void setYssPub(YssPub pub) {
    //this.pub = pub;
    //}
    boolean isCheckCury=false;// add by lidaolong #665 凭证导入时检查凭证币种，弹出提示窗口
    public String doOperation(String sType) throws YssException {
        String reStr = "";
        //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        DayFinishLogBean df = new DayFinishLogBean();
        df.setYssPub(pub);
        String logSumCode = "";
        if(logOper == null){//添加空指针判断
        	logOper = SingleLogOper.getInstance();
        }
        Date logStartTime = null;//日志开始时间
        boolean isError = false;//是否为异常数据
        String errorInfo = "";//报错信息
        //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            if (sType.equalsIgnoreCase("build")) {
            	//edit by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
                doVchBuild(freeParams,"");
                reStr = "true";
            }
            if (sType.equalsIgnoreCase("audit")) {
                doAudit(freeParams);
                reStr = "true";
            }
            if (sType.equalsIgnoreCase("check")) {
		        //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	try{
            		logSumCode = df.getLogSumCodes();
                	logStartTime = new Date();//业务子项开始时间
                	operType=24;
                	this.setRefName("001215");//设置功能调用代码
                	this.setFunName("vchcheck");//设置功能模块代码
                	this.setModuleName("voucher");//设置模块代码
                	//---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
            		isCheckCury=true;
            		this.strInvokeType = "FrmVchCheck";	//20120412 added by liubo.Story #2192
            	
            		//edit by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                	doCheck(freeParams, logSumCode);
                
//              ------add by guojianhua 2010 09 28  MS01538    完善系统各界面操作的日志记录

                	logOper.setIData(this, operType, pub);
                	//-------------end-------------------
                	this.strInvokeType = "";	//20120412 added by liubo.Story #2192
                	reStr = "true";
            	}catch(Exception e){
            		try{
            			isError = true;
            		}catch(Exception ex){
            			ex.printStackTrace();
            		}
					//throw new YssException("凭证检查报错",e);	//modify by huangqirong 2012-11-09 bug #6181					
            	}finally{
				    //保存业务日志
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
            			if(logOper.getDayFinishLog().getAlPojo().size() == 0){//保存明细日志
                			logOper.setDayFinishIData(this, 24, " ", pub, isError, this.portCodes, 
                					YssFun.toDate(beginDate), YssFun.toDate(beginDate), 
                                	YssFun.toDate(endDate), " ", logStartTime, logSumCode, new Date());
            			}
            			//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
            			logOper.setDayFinishIData(this, 24, "sum", pub, isError, " ", 
            					YssFun.toDate(beginDate), YssFun.toDate(beginDate), 
                            	YssFun.toDate(endDate), " ", logStartTime, logSumCode, new Date());
            		}
            	}
            }
			if (sType.equalsIgnoreCase("outacc")) {
				try {
					// ---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					logSumCode = df.getLogSumCodes();
					logStartTime = new Date();// 业务子项开始时间
					// ---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//

					this.isInData = false;
					runStatus.appendRunDesc("VchRun", "开始导入前的凭证检查.. ...");

					// edit by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
					reStr = doCheck(freeParams, logSumCode); // 在导出之前检查
					runStatus.appendRunDesc("VchRun", "导入前的凭证检查完成！.. ...\r\n\r\n");
					if (reStr.equalsIgnoreCase("true")) {
						runStatus.appendRunDesc("VchRun", "开始导入凭证.. ...\r\n");
						// edit by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
						doOutacc(freeParams, logSumCode);
						runStatus.appendRunDesc("VchRun", "导入凭证完成！.. ...");
					}

					// ------add by guojianhua 2010 09 28 MS01538 完善系统各界面操作的日志记录
					operType = 25;
					this.setRefName("000217");
					this.setFunName("vchinter");
					this.setModuleName("voucher");
					logOper.setIData(this, operType, pub);
					// -------------end-------------------

					// ---2009.04.18 蒋锦 添加 流程控制中适用组合的处理---//
					// MS00003
					// 判断是否从流程中执行
					if (pub.getFlow() != null && pub.getFlow().keySet().contains(pub.getUserCode())) {
						// 插入已执行组合
						((FlowBean) pub.getFlow().get(pub.getUserCode())).setFPortCodes(portCodes);
					}
					// -----------------------------------------------//
					// ---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
				} catch (Exception e) {
					try {
						isError = true;
						reStr = "false"; //add by huangqirong 2012-11-09 bug #6181
						runStatus.appendRunDesc("VchRun", "导入凭证失败:" + e.getMessage());//add by huangqirong 2012-11-09 bug #6181
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} finally {
				    //保存业务日志
	        		//edit by songjie 2012.11.20 添加非空判断
	        		if(logOper != null){
	        			//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
	        			if(logOper.getDayFinishLog().getAlPojo().size() == 0){//保存明细日志
		        			logOper.setDayFinishIData(this, 25, " ", pub, isError, this.portCodes, 
		        					YssFun.toDate(beginDate), YssFun.toDate(beginDate), 
									YssFun.toDate(endDate), " ", logStartTime, logSumCode,
									new java.util.Date());
	        			}
	        			//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
	        			logOper.setDayFinishIData(this, 25, "sum", pub, isError, " ", 
	        					YssFun.toDate(beginDate), YssFun.toDate(beginDate), 
								YssFun.toDate(endDate), " ", logStartTime, logSumCode,
								new java.util.Date());
	        		}
				}
				// ---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            }
            if (sType.equalsIgnoreCase("outmdb")) {
                this.isInData = false;
                reStr = doOutMdb(freeParams);
            }
            if (sType.equalsIgnoreCase("project")) {
                this.isInData = true;
                reStr = doProject();
                //reStr = "true";
            }
            if (sType.equalsIgnoreCase("oldBuild")) {
            	try{
		        	//---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            		logSumCode = df.getLogSumCodes();
            		logStartTime = new Date();//业务子项开始时间
            		this.setFunName("voucherbuild");

            		//---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            	
            		//this.isInData = false;
            		// edit by qiuxufeng 20110304 BUG #1185 QDV4上海(38测试)2011年3月02日01_B
            		// 凭证生成界面为属性代码，设isInData为true，使内部获取模板代码
            		this.isInData = true;
                	java.util.Date beginDate = new java.util.Date();
                	runStatus.appendRunDesc("VchRun", "开始生成凭证.. ...");
                	
                	//edit by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                	doVchBuild(freeParams,logSumCode);
                
                	runStatus.appendRunDesc("VchRun",
                                        	"生成凭证完成！\r\n\r\n");
                	runStatus.appendRunDesc("VchRun",
                                        	"开始凭证检查.. ...");
                	if(YssCons.YSS_VCH_CHECK_MODE.equalsIgnoreCase("batch")){
                		//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                		reStr = doCheckBatch(freeParams, YssFun.toDate(this.beginDate), YssFun.toDate(this.endDate),logSumCode);
                	}else{
                		//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    	reStr = doCheck(freeParams ,logSumCode); //在导出之前检查
                	}
                
                	runStatus.appendRunDesc("VchRun",
                                        	"凭证检查完成！.. ...\r\n\r\n");
                	if (reStr.equalsIgnoreCase("true")) {
                    	runStatus.appendRunDesc("VchRun",
                    							"开始审核凭证.. ...\r\n");
                    	doAudit(freeParams);
                    	runStatus.appendRunDesc("VchRun",
                    							"审核凭证完成！.. ...");
                	}
                	runStatus.appendRunDesc("VchRun",
                			"耗时【"+YssFun.timeDiff(beginDate)+"】");
                
                	//------add by guojianhua 2010 09 28  MS01538    完善系统各界面操作的日志记录
                	operType=27;
                	this.setRefName("000215");
                	this.setFunName("voucherbuild");
                	this.setModuleName("voucher");
                	logOper.setIData(this, operType, pub);
                	//-------------end-------------------
                	//---2009.04.18 蒋锦 添加 流程控制中适用组合的处理---//
                	//MS00003
                	//判断是否从流程中执行
                	if (pub.getFlow() != null && pub.getFlow().keySet().contains(pub.getUserCode())) {
                    	//插入已执行组合
                    	( (FlowBean) pub.getFlow().get(pub.getUserCode())).setFPortCodes(
                    			portCodes);
                	}
                	//-----------------------------------------------//
            	}catch(Exception e){
            		try{
            			isError = true;
            			reStr = "false"; //add by huangqirong 2012-11-09 bug #6181
						runStatus.appendRunDesc("VchRun", "生成凭证失败:" + e.getMessage());//add by huangqirong 2012-11-09 bug #6181
            			//edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A 处理日志信息 除去特殊符号
						errorInfo = e.getMessage().replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", "");
            		}catch(Exception ex){
            			ex.printStackTrace();
            		}
            		//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
            		finally{//添加 finally 保证可以抛出异常
            			throw new YssException("生成凭证失败",e);
            		}
            		//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
            	}finally{
				    //保存业务日志
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null && logOper.getDayFinishLog().getAlPojo().size() == 0){
            			//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                        logOper.setDayFinishIData(this, 27, " ", pub, isError, this.portCodes, 
                                YssFun.toDate(beginDate), YssFun.toDate(beginDate), 
                                YssFun.toDate(endDate), errorInfo, logStartTime, 
                                logSumCode, new java.util.Date());//保存明细日志
                        //---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                        logOper.setDayFinishIData(this, 27, "sum", pub, isError, this.portCodes, 
                                YssFun.toDate(beginDate), YssFun.toDate(beginDate), 
                                YssFun.toDate(endDate), errorInfo, logStartTime, 
                                logSumCode, new java.util.Date());
            		}else{
            			//edit by songjie 2012.11.20 添加非空判断
            			if(logOper != null){
            				logOper.setDayFinishIData(this, 27, "sum", pub, isError, " ", 
            						YssFun.toDate(beginDate), YssFun.toDate(beginDate), 
            						YssFun.toDate(endDate), " ", logStartTime, 
            						logSumCode, new java.util.Date());
            			}
            		}

            	}
            }
            return reStr;
        } catch (Exception e) {
            reStr = "error"; //执行方案失败
            throw new YssException("凭证操作失败！", e); //sj modified 20090120 QDV4交银施罗德2009年01月09日02_B   BugId:MS00185，为了使提示信息更友好。
        }
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\r\f")[0].split("\t");

            this.beginDate = reqAry[0];
            this.endDate = reqAry[1];
            this.portCodes = reqAry[2]; //由，间隔
            //this.beanId = reqAry[3];
            this.freeParams = reqAry[3]; //由，间隔
        } catch (Exception e) {
            throw new YssException("凭证操作请求信息出错\r\n" + e.getMessage(), e);
        }

    }

    /**
     * 生成凭证
     * @throws YssException
     */
    /* public void doVchBuild(String vchTyps) throws YssException {
        //String tmpTypes = "";
        VoucherAdmin vchAdmin = null;
        try {
           //if (this.vchTypes.length() > 0) {
           //tmpTypes = this.vchTypes;
           //}
           //else if (this.inVchTypes.length() > 0) {
           //tmpTypes = this.inVchTypes;
           //}

           isExistGuessReport();
           vchAdmin = new VoucherAdmin();
           vchAdmin.setYssPub(pub);
//         vchAdmin.deleteVoucherWithNoCheck(tmpTypes, YssFun.toDate(this.beginDate),
//                                YssFun.toDate(this.endDate),
//                                this.portCodes);
           vchAdmin.deleteVoucher(vchTyps, YssFun.toDate(this.beginDate),
                                  YssFun.toDate(this.endDate), portCodes);
           //--------------------Multi--------------------------------//
           VchBuildMulti vchbuildM = new VchBuildMulti();
           vchbuildM.setYssPub(pub);
           vchbuildM.init(this.portCodes, this.beginDate, this.endDate,
                          vchTyps, this.freeParams);
           vchbuildM.setRunStatus(runStatus);
           vchbuildM.doVchBuild();
           //---------------------------------------------------------//

           //---------------------Single------------------------------//
           VchBuildSingle vchbuildS = new VchBuildSingle();
           vchbuildS.setYssPub(pub);
           vchbuildS.init(this.portCodes, this.beginDate, this.endDate,
                          vchTyps, this.freeParams);
           vchbuildS.setRunStatus(runStatus);
           vchbuildS.doVchBuild();
           //---------------------------------------------------------//
        }
        catch (Exception e) {
           throw new YssException("生成凭证失败！", e);//sj modified 20090120 QDV4交银施罗德2009年01月09日02_B   BugId:MS00185，为了使提示信息更友好。
        }
     }
     */

//   public void doAudit(String vchTyps) throws YssException {
//      //String tmpTypes = "";
//      VchDataBean vchData = null;
//      try {
//         //if (this.vchTypes.length() > 0) {
//         //tmpTypes = this.vchTypes;
//         //}
//         //else if (this.inVchTypes.length() > 0) {
//         //tmpTypes = this.inVchTypes;
//         //}
//         vchData = new VchDataBean();
//         vchData.setYssPub(pub);
//         //runStatus.appendRunDesc("VchRun","开始审核凭证... ...","      ");
//         vchData.exMultiChe(this.beginDate, this.endDate, this.portCodes,
//                            vchTyps);
//         //runStatus.appendRunDesc("VchRun","凭证审核完成\r\n");
//      }
//      catch (Exception e) {
//         throw new YssException("凭证检查出错！", e);
//      }
//   }

//   public String doCheck(String vchTyps) throws YssException {
//      //String tmpTypes = "";
//      String[] chkTypes = null;
//      String[] aryPorts = null;
//      String reStr = "true";
//      String tmpStr = "";
//      try {
//         //if (this.freeParams.length() > 0) {
//         //tmpTypes = this.freeParams;
//         //}
//         //if (this.inVchTypes.length() > 0) {
//         //tmpTypes = this.inVchTypes;
//         //}
//         chkTypes = new String[] {
//               "balance", "laccluy", "subjectex", "detail"};
//         aryPorts = this.portCodes.split(",");
//         for (int ports = 0; ports < aryPorts.length; ports++) {
//            //runStatus.appendRunDesc("VchRun","-----------------------------------------------------------");
//            runStatus.appendRunDesc("VchRun",
//                                    "    开始检查组合【" + aryPorts[ports] +
//                                    "】的凭证... ...");
//            for (int types = 0; types < chkTypes.length; types++) {
//               BaseVchCheck check = (BaseVchCheck) pub.getVoucherCtX().getBean(
//                     chkTypes[types]);
//               check.init(aryPorts[ports], this.beginDate, this.endDate,
//                          vchTyps, this.isInData);
//               check.setYssPub(pub);
//               check.setRunStatus(runStatus);
//               tmpStr = check.doCheck();
//               if (tmpStr.equalsIgnoreCase("false")) {
//                  reStr = tmpStr;
//               }
//            }
//            runStatus.appendRunDesc("VchRun",
//                                    "    完成检查组合【" + aryPorts[ports] +
//                                    "】的凭证... ...");
//            //runStatus.appendRunDesc("VchRun","-----------------------------------------------------------");
//         }
//         return reStr;
//      }
//      catch (Exception e) {
//         throw new YssException("凭证检查出错！", e);
//      }
//   }

//   public void doOutacc(String vchTyps) throws YssException {
//      //String tmpTypes = "";
//      try {
//         //if (this.freeParams.length() > 0) {
//         //tmpTypes = this.freeParams;
//         //}
//         //if (this.inVchTypes.length() > 0) {
//         //tmpTypes = this.inVchTypes;
//         //}
//
//         BaseVchOut outacc = new VchOutAcc();
//         outacc.setYssPub(pub);
//         outacc.init(this.portCodes, this.beginDate, this.endDate,
//                     vchTyps, this.isInData);
//         outacc.setYssRunStatus(runStatus);
//         outacc.doInsert();
//      }
//      catch (Exception e) {
//         throw new YssException("导入出错！", e);
//      }
//   }

//   public String doOutMdb(String vchTyps) throws YssException {
//      try {
//         BaseVchOut outMdb = new VchOutMdb();
//         outMdb.setYssPub(pub);
//         outMdb.init(this.portCodes, this.beginDate, this.endDate,
//                     vchTyps, this.isInData);
//         outMdb.setYssRunStatus(runStatus);
//         return outMdb.doInsert();
//      }
//      catch (Exception e) {
//         throw new YssException("导出出错！", e);
//      }
//   }

    public String doProject() throws YssException {
        String sqlStr = "";
        YssDbOperSql operSql = null;
        ResultSet rs = null;
        String canOutAcc = "";
        //-----MS00196 QDV4鹏华2009年1月16日01_B 的修改 sj modified 20090202
        java.util.Date beginDate = YssFun.toDate(this.beginDate);
        java.util.Date endDate = YssFun.toDate(this.endDate);
        int days = -1;
        String parseStr= "";
        VchChkCuryToAcc toAcc;
        //-----------------------------------------------------------------
  	    //---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
  	    String logInfo = null;//记录runStatus 相关内容
  	    Date logStartTime = null;//业务子项开始时间
  	    String logSumCode = "";//日志汇总编号
        DayFinishLogBean df = new DayFinishLogBean();
        String projectCode= "";//凭证方案代码
        if(logOper == null){//添加空指针判断
        	logOper = SingleLogOper.getInstance();
        }
        boolean isError = false;//判断是否为异常数据
        //---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            if (this.freeParams.length() > 0) {
                //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                df.setYssPub(pub);
                
                if(this.comeFromDD){
                	logSumCode = this.logSumCode;
                }else{
                	logSumCode = df.getLogSumCodes();
                    logStartTime = new Date();
                    this.setFunName("vchbuildproject");
                }
                
                //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            	
                operSql = new YssDbOperSql(pub);
                days = YssFun.dateDiff(beginDate, endDate); //MS00196 QDV4鹏华2009年1月16日01_B 的修改 sj modified 20090202
                for (int operDays = 0; operDays <= days; operDays++) { //MS00196 QDV4鹏华2009年1月16日01_B 的修改 sj modified 20090202
                    java.util.Date curDate = YssFun.addDay(beginDate, operDays); //MS00196 QDV4鹏华2009年1月16日01_B 的修改 sj modified 20090202

                    //20130312 added by liubo.Bug #7295
                    //按日期循环时要求加上当前的凭证日期的提示
                    //====================================
                    runStatus.appendRunDesc("VchRun", "开始执行日期【" + com.yss.util.YssFun.formatDate(curDate) + "】的凭证方案... ...\r\n");
                    //==================end==================
                    
                    sqlStr = "select * from " + pub.yssGetTableName("TB_Vch_Project") +
                        " where FCheckState = 1 and FProjectCode in (" +
                        operSql.sqlCodes(this.freeParams) + ")" +
                        " order by FExeOrderCode";
                    rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                    while (rs.next()) {
                    	//---add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    	logInfo = "";//日志详细信息
                    	projectCode = rs.getString("FProjectCode");//凭证执行方案名称
                    	logStartTime = new Date();//日志开始时间
                    	//---add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    	
                        this.inVchTypes = getVchAttrs(rs.getString("FProjectCode"));
                        
                        runStatus.appendRunDesc("VchRun", "开始执行方案【" + rs.getString("FProjectCode") + "】... ...\r\n");
                        
                        //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                        logInfo += "开始执行凭证方案【" + rs.getString("FProjectCode") + "】... ...\r\n";
                        
                        if (rs.getInt("FExBuild") == 1) { //执行生成
                            if (inVchTypes.length() > 0) {
                                //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                                logInfo += "  开始生成方案【" + rs.getString("FProjectCode") + "】的凭证... ...\r\n";
                            	
                                runStatus.appendRunDesc("VchRun", "  开始生成方案【" + rs.getString("FProjectCode") + "】的凭证... ...");
                                
                                //edit by songjie 2012.
                                doVchBuild(inVchTypes, curDate, curDate,""); //MS00196 QDV4鹏华2009年1月16日01_B 的修改 sj modified 20090202   执行一天的凭证生成
                                
                                //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                                logInfo += "  方案【" + rs.getString("FProjectCode") + "】的凭证生成完成！\r\n";
                                
                                runStatus.appendRunDesc("VchRun", "  方案【" + rs.getString("FProjectCode") + "】的凭证生成完成！");
                                //runStatus.appendRunDesc("VchRun",
                                //"***********************************************************");
                            } else {
                                throw new YssException("凭证方案设置出错！");
                            }
                        }
                        if (rs.getInt("FExCheck") == 1) { //执行检查,并审核
                            //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            logInfo += "  开始检查方案【" + rs.getString("FProjectCode") + "】的凭证... ...\r\n";
                            
                            runStatus.appendRunDesc("VchRun", "\r\n\r\n  开始检查方案【" + rs.getString("FProjectCode") + "】的凭证... ...");
                            //-------------------------------------------------------
                            if(YssCons.YSS_VCH_CHECK_MODE.equalsIgnoreCase("batch")){
                            	//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            	canOutAcc = doCheckBatch(inVchTypes, curDate, curDate,"");
                            }else{
                            	//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            	canOutAcc = doCheck(inVchTypes, curDate, curDate,""); ////MS00196 QDV4鹏华2009年1月16日01_B 的修改 sj modified 20090202 执行一天的凭证检查
                            }
                            
                            //返回是否检查通过
                            //-------------------------------------------------------
                            //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            logInfo +="  方案【" + rs.getString("FProjectCode") + "】的凭证检查完成！\r\n";
                            
                            runStatus.appendRunDesc("VchRun", "  方案【" + rs.getString("FProjectCode") + "】的凭证检查完成！");
                            //runStatus.appendRunDesc("VchRun",
                            //"***********************************************************");
                            if (canOutAcc.equalsIgnoreCase("false")) {
                                //----------------当检查有错误时，完成方案执行，不能向财务系统导入--------//
                                //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                                logInfo += "  方案【" + rs.getString("FProjectCode") + "】执行完成！\r\n";
                                
                                runStatus.appendRunDesc("VchRun", "方案【" + rs.getString("FProjectCode") + "】执行完成！");
                                runStatus.appendRunDesc("VchRun",
                                    "***********************************************************\r\n\r\n");
                                /**add---shashijie 2013-3-26 BUG 7371 关闭游标*/
								dbl.closeResultSetFinal(rs);
								/**end---shashijie 2013-3-26 BUG 7371*/
                                return "false";
                                //------------------------------------------------------------------
                            }
                            
                            //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            logInfo += "  开始审核方案【" + rs.getString("FProjectCode") + "】的凭证... ...\r\n";
                            
                            runStatus.appendRunDesc("VchRun", "\r\n\r\n  开始审核方案【" + rs.getString("FProjectCode") + "】的凭证... ...");
                            doAudit(inVchTypes, curDate, curDate); ////MS00196 QDV4鹏华2009年1月16日01_B 的修改 sj modified 20090202 执行一天的凭证审核
                            
                            //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            logInfo += "  方案【" + rs.getString("FProjectCode") + "】的凭证审核完成！\r\n";
                            
                            runStatus.appendRunDesc("VchRun", "  方案【" + rs.getString("FProjectCode") + "】的凭证审核完成！");
                            //runStatus.appendRunDesc("VchRun",
                            //"***********************************************************");

                        }
                        
                        //modify by jsc 20120522凭证模块优化 ，批量模式，将这些检查放到一起进行检查
                        if(YssCons.YSS_VCH_CHECK_MODE.equalsIgnoreCase("default")){
                        	//多币种检查  yeshenghong 20120515 BUG4611
                            toAcc = new VchChkCuryToAcc();
                            toAcc.setYssPub(pub);
    						toAcc.setYssRunStatus(runStatus);
                    		//20120412 modified by liubo.Story #2192
                    		//将strInvokeType（调用类型）传给调用的BaseVchCheck对象
                            //=================================
                            toAcc.init(getPortCodes(), this.beginDate, this.endDate, inVchTypes, true, this.strInvokeType);
                            //===============end=================
                            parseStr=this.beginDate+"\t"+ this.endDate+"\t"+getPortCodes()+"\t"+inVchTypes;
                            toAcc.parseStr(parseStr);
                            parseStr= toAcc.doCuryCheck();
                        }
                        
                        
                        if (rs.getInt("FExInsert") == 1) { //执行导入
                            //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            logInfo += "  开始导入方案【" + rs.getString("FProjectCode") + "】的凭证... ...\r\n";
                        	
                            runStatus.appendRunDesc("VchRun","\r\n\r\n  开始导入方案【" +
                                rs.getString("FProjectCode") + "】的凭证... ...\r\n");
                            //edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            doOutacc(inVchTypes, curDate, curDate, ""); ////MS00196 QDV4鹏华2009年1月16日01_B 的修改 sj modified 20090202 执行一天的凭证导入财务
                            
                            //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                            logInfo += "  导入方案【" + rs.getString("FProjectCode") + "】的凭证完成！\r\n";
                            
                            runStatus.appendRunDesc("VchRun","  导入方案【" +
                                rs.getString("FProjectCode") + "】的凭证完成！");
                            //runStatus.appendRunDesc("VchRun",
                            //"***********************************************************");
                        }
                        
                        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        logInfo += "凭证方案【" + rs.getString("FProjectCode") + "】执行完成！\r\n";
                        
                        this.setFunName("vchbuildproject");
            			//edit by songjie 2012.11.20 添加非空判断
            			if(logOper != null){
            				logOper.setDayFinishIData(this, 26,"凭证方案【" + rs.getString("FProjectCode")+"】",  
            						pub, false, this.portCodes, beginDate,curDate,endDate,
            						logInfo,logStartTime,logSumCode, new java.util.Date());
            			}
                    	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                        
                        runStatus.appendRunDesc("VchRun", "方案【" + rs.getString("FProjectCode") + "】执行完成！\r\n");
                        runStatus.appendRunDesc("VchRun", "***********************************************************\r\n\r\n");
                    }
                    //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
                    dbl.closeResultSetFinal(rs);

                    //20130312 added by liubo.Bug #7295
                    //按日期循环时要求加上当前的凭证日期的提示
                    //====================================
                    runStatus.appendRunDesc("VchRun", "日期【" + com.yss.util.YssFun.formatDate(curDate) + "】的凭证方案执行完毕... ...\r\n");
                    //==============end======================
                }
            }

            
            //------add by guojianhua 2010 09 28  MS01538    完善系统各界面操作的日志记录
            operType=26;
            this.setRefName("000597");
            this.setFunName("vchbuildproject");
            this.setModuleName("voucher");
            logOper = SingleLogOper.getInstance();
            logOper.setIData(this, operType, pub);
            //-------------end-------------------
            
            return canOutAcc;
        } catch (Exception e) {
        	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		isError = true;//设置为异常状态
        		
        		this.setFunName("vchbuildproject");//设置功能模块代码
    			//edit by songjie 2012.11.20 添加非空判断
    			if(logOper != null){
    				logOper.setDayFinishIData(this, 26,"凭证方案【" + projectCode +"】", pub, true, this.portCodes, 
    						YssFun.toDate(this.getBeginDate()),YssFun.toDate(this.getBeginDate()),
    						YssFun.toDate(this.getEndDate()),
    						//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
    						(logInfo + " \r\n凭证方案【" + projectCode + "】执行失败  \r\n " + e.getMessage())
    						.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),//处理日志信息 除去特殊符号
    						//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
    						logStartTime,logSumCode, new java.util.Date());
    			}
        		
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	finally{//添加 finally 保证可以抛出异常
            	throw new YssException("执行凭证方案出错！", e);
            }
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeResultSetFinal(rs);
            
            //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            //保存日志数据
			if(!this.comeFromDD){
    			//edit by songjie 2012.11.20 添加非空判断
    			if(logOper != null){
    				//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
    				if(logOper.getDayFinishLog().getAlPojo().size() == 0){//保存明细日志
        				logOper.setDayFinishIData(this, 26, " ", pub, isError, 
        						this.portCodes, beginDate, beginDate, endDate, " ", 
        						logStartTime, logSumCode, new java.util.Date());
    				}
    				//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
    				logOper.setDayFinishIData(this, 26, "sum", pub, isError, " ", 
    						beginDate, beginDate, endDate, " ", 
    						logStartTime, logSumCode, new java.util.Date());
    			}
            }
            //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        }
    }

    
    //add by lidaolong 20110408 #665 凭证导入时检查凭证币种，弹出提示窗口
    public String doCuryCheck() throws YssException, SQLException{
		String sqlStr = "";
		YssDbOperSql operSql = null;
		ResultSet rs = null;

		VchChkCuryToAcc toAcc = null;
		java.util.Date beginDate = YssFun.toDate(this.beginDate);
		java.util.Date endDate = YssFun.toDate(this.endDate);
		int days = -1;
		String parseStr = "";

		if (this.freeParams.length() > 0) {
			operSql = new YssDbOperSql(pub);
			days = YssFun.dateDiff(beginDate, endDate);
			for (int operDays = 0; operDays <= days; operDays++) {
				java.util.Date curDate = YssFun.addDay(beginDate, operDays);
				sqlStr = "select * from "
						+ pub.yssGetTableName("TB_Vch_Project")
						+ " where FCheckState = 1 and FProjectCode in ("
						+ operSql.sqlCodes(this.freeParams) + ")"
						+ " order by FExeOrderCode";
				rs = dbl.queryByPreparedStatement(sqlStr); // modify by
															// fangjiang
															// 2011.08.14 STORY
															// #788
				while (rs.next()) {
					this.inVchTypes = getVchAttrs(rs.getString("FProjectCode"));
					toAcc = new VchChkCuryToAcc();
					toAcc.setYssPub(pub);
					toAcc.setYssRunStatus(runStatus);
					// 20120412 modified by liubo.Story #2192
					// 将strInvokeType（调用类型）传给调用的BaseVchCheck对象
					// =================================
					toAcc.init(getPortCodes(), this.beginDate, this.endDate,
							inVchTypes, true, this.strInvokeType);
					// ===============end=================
					parseStr = this.beginDate + "\t" + this.endDate + "\t"
							+ getPortCodes() + "\t" + inVchTypes;
					toAcc.parseStr(parseStr);

					parseStr = toAcc.doCuryCheck();
					if (!"".equals(parseStr)) {
						/**add---shashijie 2013-3-26 BUG 7371 关闭游标*/
						dbl.closeResultSetFinal(rs);
						/**end---shashijie 2013-3-26 BUG 7371*/
						return parseStr;
					}
				}
				// add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
				dbl.closeResultSetFinal(rs);
			}
		}

		return "";
    }//end by lidaolong
    
    private String getVchAttrs(String projectCode) throws YssException {
        String sqlStr = "";
        String reStr = "";
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
            sqlStr = "select FAttrCode,FProjectCode from " +
                pub.yssGetTableName("TB_Vch_BuildLink") +
                " where FCheckState = 1 and FProjectCode = " +
                dbl.sqlString(projectCode);
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                buf.append(rs.getString("FAttrCode")).append(",");
            }
            if (buf.length() > 0) {
                reStr = buf.toString();
                reStr = reStr.substring(0, reStr.length() - 1);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("获取凭证属性出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 判断在凭证生成的日期段内，是否有财务估值表已生成并确认。sj modified 20090120 QDV4交银施罗德2009年01月09日02_B   BugId:MS00185
     * @void
     */
    private void isExistGuessReport() throws YssException {
        String sqlStr = "";
        String msgStr = "";
        ResultSet rs = null;
        sqlStr = "select FPortCode,FDate from " + pub.yssGetTableName("Tb_Rep_GuessValue") +
            " where FACCTCODE='C100' and FAcctLevel = 1 and FPortCode in (" + getPortID(this.portCodes, this.beginDate.substring(0, 4)) + ") and (FDate between " +
            dbl.sqlDate(this.beginDate) + " and " + dbl.sqlDate(this.endDate) + ")"; //获取日期段的相应组合的确认信息.
        try {
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
        } catch (Exception ex) {
            throw new YssException("获取财务估值表的确认信息出现异常!", ex);
        }
        try {
            while (rs.next()) { //将每个已确认的信息汇总起来
                msgStr += "\r\n" + "【" + getPortCode(rs.getString("FPortCode"), this.beginDate.substring(0, 4)) + "】组合," + //套帐号来获取相应的组合号。
                    YssFun.formatDate(rs.getDate("FDate"), "yyyy-MM-dd") +
                    "日的财务估值表已确认!";
            }
            if (msgStr.trim().length() > 0) { //若有确认的信息,就向前台提示.
                msgStr = msgStr + "\r\n若需重新生成凭证,请反确认上述财务估值表!";
                throw new YssException(msgStr);
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage()); //只获取抛出的信息.
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /** 20090120 sj modified  QDV4交银施罗德2009年01月09日02_B   BugId:MS00185
     * 此方法是根据财务中的帐号,年份来关联估值系统中的组合.从而取出组合代码
     * @param sPortID String
     * @param sYear String
     * @return String
     * @throws YssException
     */
    private String getPortCode(String sPortID, String sYear) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String sResult = "";
        try {
            sqlStr = "select * from " + pub.yssGetTableName("tb_para_portfolio") +
                " where FAssetCode = " +
                " (select FSetId from lsetlist where FsetCode=" +
                dbl.sqlString(sPortID) +
                " and FYear=" + dbl.sqlString(sYear) + " )";
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                sResult = rs.getString("FPortCode");
            }
        } catch (Exception ex) {
            throw new YssException("取组合信息出错,请检查设置是否正确", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * 以组合代码获取财务中的套帐代码  20090120 sj modified QDV4交银施罗德2009年01月09日02_B   BugId:MS00185
     * @param sPortCodes String
     * @param sYear String
     * @return String
     * @throws YssException
     */
    private String getPortID(String sPortCodes, String sYear) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String sResult = "";
        try {
            sqlStr = "select * from lsetlist where FSetId in  " +
                " (select FAssetCode from " + pub.yssGetTableName("tb_para_portfolio") +
                " where FCheckState = 1 and FPortCode in(" + operSql.sqlCodes(sPortCodes) + "))" +
                " and FYear = " + dbl.sqlString(sYear);
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                sResult += dbl.sqlString(rs.getString("FSetCode")) + ",";
            }
            if (sResult.trim().length() > 0) {
                sResult = sResult.substring(0, sResult.trim().length() - 1);
            }
        } catch (Exception ex) {
            throw new YssException("取组合信息出错,请检查设置是否正确", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * 获取单个组合的套装信息。
     * @param sPortCode String
     * @param sYear String
     * @return String
     * @throws YssException
     * MS00193 QDV4深圳2009年01月15日01_B
     */
    private String getPortIDSingle(String sPortCode, String sYear) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String sResult = "";
        try {
            sqlStr = "select * from lsetlist where FSetId in  " +
                " (select FAssetCode from " + pub.yssGetTableName("tb_para_portfolio") +
                " where FCheckState = 1 and FPortCode = " + dbl.sqlString(sPortCode) + ")" +
                " and FYear = " + dbl.sqlString(sYear);
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                sResult = rs.getString("FSetCode");
            }
        } catch (Exception ex) {
            throw new YssException("取组合信息出错,请检查设置是否正确", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * 判断是否本月已结帐
     * @return boolean
     * @throws YssException
     * MS00193 QDV4深圳2009年01月15日01_B
     */
    private boolean isClosed() throws YssException {
        boolean isClosed = false;
        String sqlStr = "";
        ResultSet rs = null;
        String year = this.beginDate.substring(0, 4); //获取年份
        String month = this.beginDate.substring(5, 7); //获取月份
        String[] sportCodes = this.portCodes.split(","); //获取组合
        try {
            for (int ports = 0; ports < sportCodes.length; ports++) { //循环组合
            	
            	//modified by liubo.
            	//在判断结账之前先判断财务系统中有无组合在某一年度的套帐。若不存在，则提示用户先设置
            	//===================================
            	String sPortIDSingle = getPortIDSingle(sportCodes[ports], year);
            	if (sPortIDSingle.trim().length() == 0)
            	{
            		throw new YssException("判断是否已结帐失败！\r\n请在财务系统中设置【" + sportCodes[ports] + "】组合的" + year + "年度的套帐！");
            	}
                sqlStr = "select * from A" + year +
                    YssFun.formatNumber(Long.parseLong(sPortIDSingle), "000") + "LCLOSE" + //其中的formatNumber是为了将套装号转化为类似001或011的表名。
                    " where FMonth = " + dbl.sqlString(month); //获取本月是否已结帐
            	//==============end=====================
                rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                if (rs.next()) {
                    isClosed = true; //已结帐，返回true
                    break; //获取有已经结帐的，就直接返回。
                } else {
                    isClosed = false; //未结帐，返回false
                }
            }
        } catch (Exception ex) {
        	throw new YssException("判断是否已结帐出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return isClosed;
    }

    /**
     * 判断财务系统中是否有已审核的凭证
     * @return boolean
     * @throws YssException
     * bug 3277 QDV4海富通2011年11月30日01_B.xls add by guolongchao 20111210
     */
    private boolean isExistCheckedVoucher() throws YssException {
        boolean flag = false;
        String sqlStr = "";
        ResultSet rs = null;
        String year = this.beginDate.substring(0, 4); //获取年份
        String[] sportCodes = this.portCodes.split(","); //获取组合
        try {
            for (int ports = 0; ports < sportCodes.length; ports++) //循环组合
            { 
            	//modified by liubo.
            	//在判断结账之前先判断财务系统中有无组合在某一年度的套帐。若不存在，则提示用户先设置
            	//===================================
            	String sPortIDSingle = getPortIDSingle(sportCodes[ports], year);
            	if (sPortIDSingle.trim().length() == 0)
            	{
            		throw new YssException("判断财务系统中是否有已审核的凭证失败！\r\n请在财务系统中设置【" + sportCodes[ports] + "】组合的" + year + "年度的套帐！");
            	}
                sqlStr = "select * from  A" + year + 
                         YssFun.formatNumber(Long.parseLong(sPortIDSingle), "000") + "FCWVCH a " + 
                         " where  a.fdate between "+dbl.sqlDate(this.beginDate)+" and "+dbl.sqlDate(this.endDate) +
                         " and a.fcheckr is not null and a.fcheckr <> ' ' and a.fpzly <> 'HD' ";  //modified by yeshenghong 20120327 yeshenghong BUG4108               
            	//===============end====================
                rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
                if (rs.next()) 
                {
                    flag = true; 
                    break; 
                } else 
                {
                    flag = false; 
                }
            }
        } catch (Exception ex) {
        	throw new YssException("判断财务系统中是否有已审核的凭证出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return flag;
    }
    
    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String checkRequest(String sType) throws YssException {
        return "";
    }

    //---------------以下的所有重新编写及重写的方法,是为了可以对日期的处理更加灵活.主要是为了完成 MS00196 QDV4鹏华2009年1月16日01_B 的修改 sj modified 20090202----
    /**
     * 重载方法,一次执行多日
     * @param vchTyps String
     * @throws YssException
     * MS00196 QDV4鹏华2009年1月16日01_B
     */
    //edit by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
    public void doVchBuild(String vchTyps, String logSumCode) throws YssException {
        doVchBuild(vchTyps, YssFun.toDate(this.beginDate), YssFun.toDate(this.endDate),logSumCode);
    }

    /**
     * 生成凭证的基本方法，需要向其传入起始日期
     * @param vchTyps String
     * @param beginDate Date
     * @param endDate Date
     * @throws YssException
     * MS00196 QDV4鹏华2009年1月16日01_B
     */
    //edit by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
    public void doVchBuild(String vchTyps, java.util.Date beginDate, java.util.Date endDate,String logSumCode) throws YssException {
        VoucherAdmin vchAdmin = null;
        try {
        	//在凭证生成之前判断财务系统中是否有已审核的凭证，若有已审核的，则向用户显示提示信息     bug3277  add by guolongchao 20111210
        	 if (isExistCheckedVoucher()) {               
                 throw new YssException("财务系统中已经有凭证被审核,不能再生成凭证!");
             }
//            //------在生成凭证之前先判断是否已结帐,若已结帐,则不能生成凭证 MS00193 QDV4深圳2009年01月15日01_B sj modified---//
            if (isClosed()) { //若已结帐
                String month = this.beginDate.substring(5, 7); //获取月份
                throw new YssException("对不起!" + month + "月份的帐已结,不能再生成凭证!若需再生成凭证,请完成反结帐操作!");
            }
            //-----------------------------------------------------------------------------------------------------//
            isExistGuessReport();
            vchAdmin = new VoucherAdmin();
            vchAdmin.setYssPub(pub);

            vchAdmin.deleteVoucher(vchTyps, beginDate,
                                   endDate, portCodes);
            //--------------------Multi--------------------------------//
            VchBuildMulti vchbuildM = new VchBuildMulti();
            vchbuildM.setYssPub(pub);
            
            //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            vchbuildM.setLogSumCode(logSumCode);//设置汇总日志编号
            vchbuildM.setFunName("voucherbuild");
            //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            
            vchbuildM.init(this.portCodes, YssFun.formatDate(beginDate, "yyyy-MM-dd"), YssFun.formatDate(endDate, "yyyy-MM-dd"),
                           vchTyps, this.freeParams);
            vchbuildM.setRunStatus(runStatus);
            vchbuildM.doVchBuild();
            //---------------------------------------------------------//

            //---------------------Single------------------------------//
            
            //~~~~ modify by jsc 20120522  凭证模块优化  start 
            
            if(YssCons.YSS_VCH_BUILDER_MODE.equalsIgnoreCase("batch")){
            	//单行取数批量模式
            	VchBuildSingleBatch vchBuildBatch = new VchBuildSingleBatch();
            	vchBuildBatch.setYssPub(pub);
            	vchBuildBatch.init(this.portCodes, YssFun.formatDate(beginDate, "yyyy-MM-dd"), YssFun.formatDate(endDate, "yyyy-MM-dd"),vchTyps, this.freeParams);
            	
                //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	vchBuildBatch.setLogSumCode(logSumCode);//设置汇总日志编号
            	vchBuildBatch.setFunName("voucherbuild");
            	//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            	
            	vchBuildBatch.setRunStatus(runStatus);
            	vchBuildBatch.doVchBuild();
            }else{
            	//单行取数旧模式
            	VchBuildSingle vchbuildS = new VchBuildSingle();
                vchbuildS.setYssPub(pub);
                vchbuildS.init(this.portCodes, YssFun.formatDate(beginDate, "yyyy-MM-dd"), YssFun.formatDate(endDate, "yyyy-MM-dd"),
                               vchTyps, this.freeParams);
                vchbuildS.setRunStatus(runStatus);
                
                //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                vchbuildS.setLogSumCode(logSumCode);//设置汇总日志编号
                vchbuildS.setFunName("voucherbuild");
                //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
                vchbuildS.doVchBuild();
                //---------------------------------------------------------//
                
                //add by lidaolong 20110411 #818 系统在选择这个选项后，要判断结转科目，是否原币有余额，有，才做凭证。
                doDelZeroVch(YssFun.formatDate(beginDate, "yyyyMMdd"), YssFun.formatDate(endDate, "yyyyMMdd"));           
                //end by lidaolong 20110411
            }
            
          //~~~~ modify by jsc 20120522  凭证模块优化  end 
        } catch (Exception e) {
            runStatus.clearRunDesc("VchRun"); //sj modified 20090210 QDV4交银施罗德2009年01月09日02_B   BugId:MS00185 为了在出现错误时清空操作信息。
            throw new YssException("生成凭证失败！", e); //sj modified 20090120 QDV4交银施罗德2009年01月09日02_B   BugId:MS00185，为了使提示信息更友好。
        }
    }

    
    
    
    
    /**
     * 删除系统中生成了借方和贷方全是0的凭证
     * add by lidaolong 20110411 #818 系统在选择这个选项后，要判断结转科目，是否原币有余额，有，才做凭证。
     * @throws YssException 
     */
    private void doDelZeroVch(String beginDate,String endDate) throws YssException{
    	ResultSet rs =null;
    	String strSql="";
    	boolean isDelVcl=true;//是否删除凭证,默认删除
    	String vchNum="";//凭证编号
    	String delVchNum="";//需要删除的凭证编号
    	int date = Integer.parseInt(beginDate);//查询的日期
    	try{
    		//遍历日期,取出所对应的凭证
    		for(int i=0;i<= (Integer.parseInt(endDate)-Integer.parseInt(beginDate));i++){
    			date =Integer.parseInt(beginDate) +i;
    			strSql="SELECT fvchnum,fbal, fsetbal FROM " +
					pub.yssGetTableName("Tb_Vch_DataEntity")+
					" WHERE fvchnum like 'T" +String.valueOf(date)+"%'"+
					" order by fvchnum";
    			
    			rs = dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);
    		  while(rs.next()){
    			  
    			  if(vchNum.equals("")){//首次进来
    				  vchNum = rs.getString("fvchnum");
    			  }
    			  if (!vchNum.equals(rs.getString("fvchnum"))){//当二者不相等时,那么上张凭证所有分录已分析完成
    		    				  
    				  if (isDelVcl){   					  
    					  delVchNum = delVchNum + vchNum +",";
    				  }
    				  
    				  isDelVcl=true;
    				  vchNum = rs.getString("fvchnum");//下一个凭证编号
    			  }
    			  
    			  //借贷只要有一个不为0,那么该凭证就不能删除
    			  if (rs.getDouble("fbal") != 0 || rs.getDouble("fsetbal") != 0){  				  
    				  isDelVcl = false;
    			  }
    			  
    			  if (rs.isLast()){
    				  vchNum = rs.getString("fvchnum");//最后一个凭证编号 
    			  }
    		  }//end while
    		 
              //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
              dbl.closeResultSetFinal(rs);
    		}//end for
    				
    		  if (isDelVcl){//判断最后一个凭证编号是否要删除		
				  delVchNum = delVchNum + vchNum +",";
			  }
    		
    		  //当需要有凭证删除时,删除该凭证
    		if(delVchNum.length()>0){ 
    			delVchNum =delVchNum.substring(0,delVchNum.length()-1);
    			
    			  //删除凭证分录数据
				  strSql ="delete from "+pub.yssGetTableName("Tb_Vch_DataEntity") 
				  			+" where fvchnum in("+operSql.sqlCodes(delVchNum)+")";
				  
				  dbl.executeSql(strSql);
				  
				  //删除凭证数据
				  strSql="delete from " + pub.yssGetTableName("Tb_Vch_Data")
				  			+" where fvchnum in("+operSql.sqlCodes(delVchNum)+")";
				  dbl.executeSql(strSql);
    		}
    		
    	}catch(Exception e){
    		throw new YssException("删除系统中生成了借方和贷方全是0的凭证出错!");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    /**
     * 审核凭证的基本方法，需要向其传入起始日期
     * @param vchTyps String
     * @throws YssException
     * MS00196 QDV4鹏华2009年1月16日01_B
     */
    public void doAudit(String vchTyps, java.util.Date beginDate, java.util.Date endDate) throws YssException {
        VchDataBean vchData = null;
        try {
            vchData = new VchDataBean();
            vchData.setYssPub(pub);
            vchData.exMultiChe(YssFun.formatDate(beginDate, "yyyy-MM-dd"), YssFun.formatDate(endDate, "yyyy-MM-dd"), this.portCodes,
                               vchTyps);
        } catch (Exception e) {
            throw new YssException("凭证检查出错！", e);
        }
    }

    /**
     * 重载的方法，一次执行多日
     * @param vchTyps String
     * @throws YssException
     * MS00196 QDV4鹏华2009年1月16日01_B
     */
    public void doAudit(String vchTyps) throws YssException {
        doAudit(vchTyps, YssFun.toDate(this.beginDate), YssFun.toDate(this.endDate));
    }

    
    /**
     *  批量检查模式
     * @param vchTyps
     * @param beginDate
     * @param endDate
     * @return
     * @throws YssException
     */
	//edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
    private String doCheckBatch(String vchTyps, java.util.Date beginDate, java.util.Date endDate, String logSumCode) throws YssException {
    	 String[] aryPorts = null;
    	 String[] chkTypes = null;
    	 String tmpStr;
         String reStr = "true";
    	try{
    		chkTypes = new String[] {"subandcur", "balance","curytoacc","auxiacc"}; 
    		aryPorts = this.portCodes.split(",");
            for (int ports = 0; ports < aryPorts.length; ports++) {
            	runStatus.appendRunDesc("VchRun","    开始检查组合【" + aryPorts[ports] +"】的凭证... ...");
            	for (int types = 0; types < chkTypes.length; types++) {
                    BaseVchCheck check = (BaseVchCheck) pub.getVoucherCtX().getBean(
                        chkTypes[types]);
                    check.init(aryPorts[ports], YssFun.formatDate(beginDate, "yyyy-MM-dd"), YssFun.formatDate(endDate, "yyyy-MM-dd"),
                    		vchTyps, this.isInData,this.strInvokeType);
                    check.setYssPub(pub);
                    check.setRunStatus(runStatus);
                    
                    //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    check.logSumCode = logSumCode;//汇总日志编号
                    check.logOper= this.logOper;//操作类型
                    check.setFunName("vchcheck");//设置功能模块代码
                    //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    
                    tmpStr = check.doCheck();
                    if (tmpStr.equalsIgnoreCase("false")) {
                        reStr = tmpStr;
                    }
                }
//            	VchChkBatch check = new VchChkBatch();
//            	check.init(aryPorts[ports], YssFun.formatDate(beginDate, "yyyy-MM-dd"), YssFun.formatDate(endDate, "yyyy-MM-dd"),vchTyps, this.isInData,this.strInvokeType);
//        				
//                check.setYssPub(pub);
//                check.setRunStatus(runStatus);
//                tmpStr = check.doCheck();
//                if (tmpStr.equalsIgnoreCase("false")) {
//                    reStr = tmpStr;
//                }
                runStatus.appendRunDesc("VchRun","    完成检查组合【" + aryPorts[ports] +"】的凭证... ...");
            }
           
        	return reStr;
    	}catch(Exception e ){
    		throw new YssException(e);
    	}
    	 
    }
    /**
     * 检查凭证的基本方法，需要传入日期
     * @param vchTyps String
     * @param beginDate Date
     * @param endDate Date
     * @return String
     * @throws YssException
     * MS00196 QDV4鹏华2009年1月16日01_B
     */
	 //edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
    public String doCheck(String vchTyps, java.util.Date beginDate, java.util.Date endDate,String logSumCode) throws YssException {
        String[] chkTypes = null;
        String[] aryPorts = null;
        String reStr = "true";
        String tmpStr = "";
        try {
            chkTypes = new String[] {
                //更改检查顺序：１：科目是否存在　２：科目是否为明细　３：科目币种设置　４：借贷是否平衡
                "subjectex", "detail", "cury", "laccluy", "balance"  //添加财务科目币种的检查 QDV4赢时胜（上海）2009年3月20日01_AB MS00332 by leeyu 20090325
                // , "curytoacc" //add by qiuxufeng 20101214 326 QDV4工银2010年11月23日01_A 增加同一凭证不能有多币种检查（结转损益、汇兑损益、外汇交易除外）
                //20120401 modified by liubo.Story #2192.增加auxiacc(科目辅助核算项设置检查)
                , "dicset","auxiacc" //add by qiuxufeng 20110117 390 QDV4赢时胜（上海）2010年12月08日02_A 检查凭证字典对应关系是否设置
            	}; 
            
         // add by lidaolong #665 凭证导入时检查凭证币种，弹出提示窗口
            if (isCheckCury){
            	chkTypes = new String[] { "subjectex", "detail", "cury", "laccluy", "balance","curytoacc","dicset","auxiacc"};	//20120401 modified by liubo.Story #2192.增加auxiacc(科目辅助核算项设置检查)
            }
            aryPorts = this.portCodes.split(",");
            for (int ports = 0; ports < aryPorts.length; ports++) {

                runStatus.appendRunDesc("VchRun",
                                        "    开始检查组合【" + aryPorts[ports] +
                                        "】的凭证... ...");
                for (int types = 0; types < chkTypes.length; types++) {
                    BaseVchCheck check = (BaseVchCheck) pub.getVoucherCtX().getBean(
                        chkTypes[types]);
                    check.init(aryPorts[ports], YssFun.formatDate(beginDate, "yyyy-MM-dd"), YssFun.formatDate(endDate, "yyyy-MM-dd"),
                    		//20120412 modified by liubo.Story #2192
                    		//将strInvokeType（调用类型）传给调用的BaseVchCheck对象
                    		//===============================
//                               vchTyps, this.isInData);
                    		vchTyps, this.isInData,this.strInvokeType);
            				//===============end================
                    check.setYssPub(pub);
                    check.setRunStatus(runStatus);
                    
                    //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    check.logSumCode = logSumCode;//汇总日志编号
                    check.logOper= this.logOper;//操作类型
                    check.setFunName("vchcheck");//功能模块代码
                    //---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    
                    tmpStr = check.doCheck();
                    if (tmpStr.equalsIgnoreCase("false")) {
                        reStr = tmpStr;
                    }
                }
                runStatus.appendRunDesc("VchRun",
                                        "    完成检查组合【" + aryPorts[ports] +
                                        "】的凭证... ...");
            }
            return reStr;
        } catch (Exception e) {
        	throw new YssException("凭证检查出错！", e);
        }
    }

    /**
     * 重载的方法，一次执行多日
     * @param vchTyps String
     * @return String
     * @throws YssException
     * MS00196 QDV4鹏华2009年1月16日01_B
     */
    //edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    public String doCheck(String vchTyps,String logSumCode) throws YssException {
		String sReturn = "";
    	if(YssCons.YSS_VCH_CHECK_MODE.equalsIgnoreCase("batch")){
	     	//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    		sReturn = doCheckBatch(vchTyps, YssFun.toDate(this.beginDate), YssFun.toDate(this.endDate),logSumCode);
        }else{
	    	//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        	sReturn = doCheck(vchTyps, YssFun.toDate(this.beginDate), YssFun.toDate(this.endDate),logSumCode); ////MS00196 QDV4鹏华2009年1月16日01_B 的修改 sj modified 20090202 执行一天的凭证检查
        }
    	return sReturn;
    }

    /**
     * 导入之财务的基本方法，需要传入日期
     * @param vchTyps String
     * @throws YssException
     * MS00196 QDV4鹏华2009年1月16日01_B
     */
    //edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
    public void doOutacc(String vchTyps, java.util.Date beginDate, java.util.Date endDate, String logSumCode) throws YssException {
    	
    	BaseVchOut outacc = null;
        try {
        	if(YssCons.YSS_VCH_DOOUTACC_MODE.equalsIgnoreCase("default")){
        		 outacc = new VchOutAcc();
        	}else{
        		 outacc = new VchOutAccBatch();
        	}
            
            outacc.setYssPub(pub);
            outacc.init(this.portCodes, YssFun.formatDate(beginDate, "yyyy-MM-dd"), YssFun.formatDate(endDate, "yyyy-MM-dd"),
                        vchTyps, this.isInData);
            outacc.setYssRunStatus(runStatus);
            
            //--- add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            outacc.logSumCode = logSumCode;//汇总日志编号
            outacc.logOper = this.logOper;//操作类型
            outacc.setFunName("vchinter");//功能模块代码
            //--- add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            
            outacc.doInsert();
        } catch (Exception e) {
            throw new YssException("导入出错！", e);
        }
    }

    /**
     * 重载的方法，一次执行多日
     * @param vchTyps String
     * @throws YssException
     * MS00196 QDV4鹏华2009年1月16日01_B
     */
	 //edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
    public void doOutacc(String vchTyps, String logSumCode) throws YssException {
	    //edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
        doOutacc(vchTyps, YssFun.toDate(this.beginDate), YssFun.toDate(this.endDate), logSumCode);
    }

    /**
     * 导出的基本方法，需要传入日期
     * @param vchTyps String
     * @return String
     * @throws YssException
     * MS00196 QDV4鹏华2009年1月16日01_B
     */
    public String doOutMdb(String vchTyps, java.util.Date beginDate, java.util.Date endDate) throws YssException {
        try {
            BaseVchOut outMdb = new VchOutMdb();
            outMdb.setYssPub(pub);
            outMdb.init(this.portCodes, YssFun.formatDate(beginDate, "yyyy-MM-dd"), YssFun.formatDate(endDate, "yyyy-MM-dd"),
                        vchTyps, this.isInData);
            outMdb.setYssRunStatus(runStatus);
            return outMdb.doInsert();
        } catch (Exception e) {
            throw new YssException("导出出错！", e);
        }
    }

    /**
     * 重载的方法,一次可执行多日
     * @param vchTyps String
     * @return String
     * @throws YssException
     * MS00196 QDV4鹏华2009年1月16日01_B
     */
    public String doOutMdb(String vchTyps) throws YssException {
        return doOutMdb(vchTyps, YssFun.toDate(this.beginDate), YssFun.toDate(this.endDate));
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------
}
