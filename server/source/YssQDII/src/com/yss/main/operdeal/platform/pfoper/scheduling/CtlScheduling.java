package com.yss.main.operdeal.platform.pfoper.scheduling;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import com.yss.dsub.BaseBean;
import com.yss.log.DayFinishLogBean;
import com.yss.log.SingleLogOper;
import com.yss.main.basesetting.InvestPayCatBean;
import com.yss.main.dao.IClientOperRequest;
import com.yss.main.dayfinish.IncomeStatBean;
import com.yss.main.dayfinish.ValuationBean;
import com.yss.main.operdata.MonetaryFundInsAdmin;
import com.yss.main.operdata.OperDealBean;
import com.yss.main.operdata.TradeSecurityLendBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.platform.pfoper.pubpara.ParaWithPubBean;
import com.yss.main.operdeal.platform.pfoper.scheduling.pojo.SchProjectPojo;
import com.yss.main.operdeal.report.navrep.CtlNavRep;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.main.operdeal.voucher.CtlVchExcuteBean;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.parasetting.FixInterestBean;
import com.yss.main.parasetting.PurchaseBean;
import com.yss.main.report.GuessValue;
import com.yss.main.settlement.TATradeSettleBean;
import com.yss.main.settlement.TradeSettleBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.projects.para.set.admin.ADM_PLUGIN_PRODUCE;
import com.yss.projects.para.set.pojo.BEN_PLUGIN_PRODUCE;
import com.yss.util.WarnPluginLoader;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssUtil;
import com.yss.vsub.YssFinance;

/**
 * @author lc
 *
 */
public class CtlScheduling extends BaseBean implements IClientOperRequest {

	private String IsOverride = "";//20110711 added by liubo.Story #1145.用于判断是否需要在生成日志的时候覆盖日志文件原先已存在的内容（每一次操作的最开始都需要进行覆盖）

	private java.util.Date dStarDate;
	private java.util.Date dEndDate;
	private String sPortCodes = ""; // 组合代码 用逗号分割
	private String[] arrPort = null; // 组合代码
	private String sProjectCode = ""; // 调度方案代码
	private boolean bIsError = false; // 判断估值检查出违规数据时是否继续执行
	private String sOperTime = "";		//操作时间
	
	String sPortForLog = "";			//20110705 added by liubo.Story #1145  该变量用于获取即将进行处理的组合代码，以供生成日志时使用
	String sProjectForLog = "";			//20110705 added by liubo.Story #1145  该变量用于获取即将进行处理的调度方案代码，以供生成日志时使用
	String sTheDay = "";
	String sLogPath = "";
	String sCheckInfo = "";//add by jsc 20120416  【STORY #2406 希望在到帐日执行调度方案时，系统有提示说明当日有权益到账】
	/**shashijie 2011-09-19 STORY 1458 */
	private String assetGroupCode = "";//组合群代码
    /**end*/

	private boolean isEws=true;//story 2188 add by zhouwei 20120615 是否执行预警系统
	//--- add by songjie 2012.09.07 story 2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	private SingleLogOper logOper;//设置日志实例
	private String logSumCode = "";//日志编号
	private String[] transInfo = null; 
	//--- add by songjie 2012.09.07 story 2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	
	public String getsOperTime() {
		return sOperTime;
	}

	public void setsOperTime(String sOperTime) {
		this.sOperTime = sOperTime;
	}

	public CtlScheduling() {
	}

	public String buildRowStr() {
		return "";
	}

	public String getOperValue(String sType) {
		return "";
	}

	public String getIsOverride() {
		return IsOverride;
	}

	public void setIsOverride(String isOverride) {
		IsOverride = isOverride;
	}
	
	public void parseRowStr(String sReqStr) throws YssException {
		try {
			String[] reqAry = sReqStr.split("\t");
			
			//---add by songjie 2012.10.17 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			if(reqAry.length <= 5){//防止报数组越界错误
				transInfo = reqAry;
				return;
			}
			//---add by songjie 2012.10.17 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			
			this.dStarDate = YssFun.toDate(reqAry[0]);
			this.dEndDate = YssFun.toDate(reqAry[1]);
			if (reqAry[2].length() != 0) {
				this.arrPort = reqAry[2].split(",");
			}
			//add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A
			this.sPortCodes = reqAry[2];//获取组合代码
			this.sProjectCode = reqAry[3];
			this.bIsError = Boolean.valueOf(reqAry[4]).booleanValue();
			this.IsOverride = reqAry[5];
			//20110712 added by liubo.Story #1145
			//从前台传回的操作时间的格式为YYYY-MM-DD HH:MM:SS
			//这个时间需要用来做为文件名的一部分，“:”会被认为不合法。将空格改为“_”，将“:”改为“-”
			//***********************************
			this.sOperTime = reqAry[6].replaceAll(" ", "_");
			this.sOperTime = sOperTime.replaceAll(":", "-");
			//***************end***********************
			/**shashijie 2011-09-19 STORY 1458 */
			this.assetGroupCode = reqAry[7];//组合群代码
            /**end*/
			
			//---story 2188 add by zhouwei 20120615 控制调度方案执行时是否执行预警 start---//
			if(reqAry.length>8){
				this.isEws=Boolean.parseBoolean(reqAry[8]);
			}
			//---story 2188 add by zhouwei 20120615 控制调度方案执行时是否执行预警 end---//
			//---add by songjie 2012.10.16 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			if(reqAry.length > 9){//获取汇总日志编号
				this.logSumCode = reqAry[9];
			}
			//---add by songjie 2012.10.16 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		} catch (Exception e) {
			throw new YssException("解析调度方案执行请求出错！", e);
		}
	}

	public String checkRequest(String sType) {
		return "";
	}

	public String doOperation(String sType) throws YssException {
		String sRetInfo = "";
		try {
			if (sType.equalsIgnoreCase("scheduling")) {
				sRetInfo = this.doScheduling();
				  //add by lidaolong 20110408 #665 凭证导入时检查凭证币种，弹出提示窗口
			}else if(sType.equalsIgnoreCase("curyCheck")){
				sRetInfo = this.doCuryCheck();
			} //end by lidaooong
			else if (sType.equalsIgnoreCase("clearrundesc")) {
				runStatus.clearSchRunDesc();
				// runStatus.clearValCheckDesc();
				runStatus.clearValRunDesc();
				runStatus.clearRunDesc("VchRun");
			}
			//20110705 added by liubo.Story #1145
			//读取“调度方案执行”日志
			else if(sType.equalsIgnoreCase("showlog"))
			{
				showScheduleLog();
			}
			
			//---add by songjie 2012.10.16 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			else if(sType.equalsIgnoreCase("getLogSumCode")){//获取汇总日志编号
				return getLogSumCode();
			}else if(sType.equals("createSumLog")){//生成汇总日志数据
				createSumLog();
			}else if(sType.equals("judgeIfHaveLog")){//判断是否生成了预警检查相关日志
				return judgeIfHaveLog();
			}
			//---add by songjie 2012.10.16 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			
		}
		return sRetInfo;
	}
	
	/**
	 * add by songjie 2012.10.19
	 * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	 * 判断是否要跳出预警日志界面 如果有数据 则在执行完调度方案后就跳出业务日志界面
	 * 显示预警检查相关的业务日志数据，否则就跳出界面显示
	 */
	private String judgeIfHaveLog() throws YssException{
		String strSql = "";
		ResultSet rs = null;
		String show = "false";
		try{
			String logSumCode = this.transInfo[0];//汇总编号
			strSql = " select * from T_Plugin_Log where C_REF_NUM = 'sum:" + logSumCode + 
			"' and C_BUSSINESS_MODULE = '预警检查'" ;
			//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换数据库联接
			rs = dblBLog.openResultSet(strSql);
			if(rs.next()){
				show = "true";
			}
			
			return show;
		}catch(Exception e){
			throw new YssException("查询业务日志数据出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2012.10.17
	 * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	 * 生成汇总日志数据
	 * @throws YssException
	 */
	private void createSumLog() throws YssException{
		String strSql = "";
		String errorInfo = " ";
		try{
			java.util.Date businessDate = YssFun.parseDate(this.transInfo[0],"yyyy-MM-dd");//业务日期
			java.util.Date startTime = YssFun.parseDate(this.transInfo[1],"yyyy-MM-dd hh:mm:ss");//开始时间
			String logSumCode = this.transInfo[2];//汇总日志编号
			boolean isError = Boolean.valueOf(this.transInfo[3]);//是否有异常
			if(this.transInfo.length >= 5){
				errorInfo = this.transInfo[4];//异常信息
			}
			
			strSql = " delete from T_Plugin_Log where C_REF_NUM = '[root]' and FLOGSUMCODE = 'sum:" + logSumCode + "'";
			//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换数据库联接
			dblBLog.executeSql(strSql);
			
			this.logOper = SingleLogOper.getInstance();
			this.setFunName("exschproject");//调度方案执行
    		//edit by songjie 2012.11.20 添加非空判断
    		if(logOper != null){
    			logOper.setDayFinishIData(this, 36, "sum", pub, isError, " ", 
    					businessDate, businessDate, businessDate, errorInfo, 
            		    startTime, logSumCode, new java.util.Date());
    		}
		}catch(Exception e){
			throw new YssException("生成汇总日志数据出错！");
		}
	}
	
	/**
	 * add by songjie 2012.10.16
	 * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	 * 获取日志汇总编号
	 * @return
	 * @throws YssException
	 */
	private String getLogSumCode() throws YssException{
        DayFinishLogBean df = new DayFinishLogBean();
        df.setYssPub(pub);
		return df.getLogSumCodes();
	}
	
	public String doCuryCheck(){
		int iDays = 0;
		java.util.Date dTheDay = null;
		ArrayList aryProject = null;
		ArrayList aryVoucher = null;
		ArrayList aryProOrder = null;

		SchProjectPojo schProject = null;
		//String sRetInfo = "true";
									
		try {
			iDays = YssFun.dateDiff(this.dStarDate, this.dEndDate);
			dTheDay = this.dStarDate;
			for (int iRingDays = 0; iRingDays <= iDays; iRingDays++) {
				aryProject = this.getSchProject(this.sProjectCode);
				int iAryProjectLen = aryProject.size();
				for (int iPort = 0; iPort < this.arrPort.length; iPort++) {
					// 循环执行序号
					for (int i = 0; i < iAryProjectLen; i++) {
						aryProOrder = (ArrayList) aryProject.get(i);
						int iAryOrderLen = aryProOrder.size();
						aryVoucher = new ArrayList();
						// 循环方案中的组成部分
						for (int j = 0; j < iAryOrderLen; j++) {
							schProject = (SchProjectPojo) aryProOrder.get(j);
							if (schProject.getFunModules().equalsIgnoreCase(
									"vchproject")) {
								aryVoucher.add(schProject);
							}
						}// end for j

					}// end for i

					if (aryVoucher.size() > 0) {
						//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加参数 logSumCode
						String vchPjtRst = this.doVchCuryCheck(arrPort[iPort],
								dTheDay, aryVoucher,"");
						if (!vchPjtRst.equals("")) {
							return vchPjtRst ;
						}
					}
				}// end for iport

				dTheDay = YssFun.addDay(dTheDay, 1);
			}
		} catch (Exception ex) {

		}
		
		return "";
	}
	

	
	public String doScheduling() throws YssException {
		int iDays = 0;
		java.util.Date dTheDay = null;
		ArrayList aryProject = null;
		ArrayList aryProOrder = null;
		ArrayList aryIncome = null;
		ArrayList aryInvest = null;
		ArrayList aryValuation = null;
		ArrayList aryVoucher = null;
		ArrayList aryReport = null;
		ArrayList arySettle = null;
		ArrayList aryBusiness = null;// 增加业务处理 MS01179
										// QDV4赢时胜（深圳）2010年05月25日01_A panjunfang
										// add 20100525
		SchProjectPojo schProject = null;
		String sRetInfo = "true";//edit by licai 20101209 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
		String sValReturn = "";
		
		/**shashijie 2011-09-19 STORY 1458 */
		String sPrefixTB = pub.getPrefixTB(); //获取组合群表前缀
		pub.setPrefixTB(this.assetGroupCode); //修改公共变量的当前组合群代码
		//add by songjie 2012.10.23 BUG 5901 QDV4赢时胜(上海开发部)2012年10月10日01_B
		pub.getPortCuryInfo();//获取当前跨组合群对应的基础货币
		/**end*/
		
		sLogPath = System.getProperty("logsDir") + (System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\") + "QDII_ScheduleLog";
		//--- add by zhouwei 20120604 预警系统执行类 start---//
		ADM_PLUGIN_PRODUCE ewAmdin=new ADM_PLUGIN_PRODUCE();
		BEN_PLUGIN_PRODUCE beanEWS=new BEN_PLUGIN_PRODUCE();//存放执行信息的javabean
		com.yss.dbadmin.dbsql.DbBase dbase=new com.yss.dbadmin.dbsql.DbBase();
		com.yss.core.func.YssPub ewsPub=new com.yss.core.func.YssPub();
		//--- add by zhouwei 20120604 预警系统执行类 end---//
		java.util.Date countDate = null;
		
        //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        DayFinishLogBean df = new DayFinishLogBean();
        df.setYssPub(pub);
        java.util.Date logStartTime = null;
        logOper = SingleLogOper.getInstance();
        boolean isError = false;//判断是否报错
        //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		try {
	        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            if(this.logSumCode.trim().length() == 0){//获取汇总日志编号
            	logSumCode = df.getLogSumCodes();
            }
            logStartTime = new java.util.Date();//业务子项开始时间
            this.setFunName("exschproject");

            //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			
			//---add by zhouwei 20120604 预警系统执行类 start---//
			dbase.dbType = pub.getDbLink().getDBType();
			dbase.setConPub(pub.getDbLink().getConnection());
			ewsPub.setDbLink(dbase);//数据库连接
			ewsPub.setDblbl(pub.getDblbl());
//			ewsPub.setSessionId(pub.getSessionId());
			ewsPub.setSession((HttpSession) WarnPluginLoader.sessionMap.get(pub.getSessionId()));
			ewAmdin.setYssPub(ewsPub);
			//---add by zhouwei 20120604 预警系统执行类 end---//
			
			iDays = YssFun.dateDiff(this.dStarDate, this.dEndDate);
			dTheDay = this.dStarDate;
			
			runStatus.clearValCheckDesc();//add by jsc 20120416  【STORY #2406 希望在到帐日执行调度方案时，系统有提示说明当日有权益到账】
			// 循环日期
			
			for (int iRingDays = 0; iRingDays <= iDays; iRingDays++) {
				runStatus.appendSchRunDesc("日    期：【"
						+ YssFun.formatDate(dTheDay) + "】开始执行... ...");
				
				/**shashijie 2011-09-22 STORY 1458	*/
				runStatus.appendSchRunDesc("组 合 群：【"
						+ this.assetGroupCode + "】开始执行... ...");
				/**end*/
				
				aryProject = this.getSchProject(this.sProjectCode);
				int iAryProjectLen = aryProject.size();
				// 循环组合
				for (int iPort = 0; iPort < this.arrPort.length; iPort++) {
					countDate = new java.util.Date();
					// 循环执行序号
					sPortForLog = arrPort[iPort];//20110705 added by liubo.Story #1145  获取即将进行处理的组合代码
					/**shashijie 2011-09-23 STORY 1565 */
					//判断组合群下是否有该组合;有返回true
					if (!isHavePortCode(sPortForLog,this.assetGroupCode)) {
						continue;
					}
					/**end*/
					
					runStatus.appendSchRunDesc("组    合：【" + arrPort[iPort]
							+ "】开始执行... ...");
					
					for (int i = 0; i < iAryProjectLen; i++) {
						aryProOrder = (ArrayList) aryProject.get(i);
						int iAryOrderLen = aryProOrder.size();
						runStatus.appendSchRunDesc("调度方案：【"
								+ ((SchProjectPojo) aryProOrder.get(0))
										.getProjectCode() + "】" + YssFun.getTime() + "开始执行... ...\r\n"); //modify by fangjinag 2011.12.05 STORY #1890 

						sProjectForLog = ((SchProjectPojo) aryProOrder.get(0)).getProjectCode();	//20110705 added by liubo.Story #1145  获取即将进行处理的调度方案代码
						sTheDay = YssFun.formatDate(dTheDay);
						//20110705 added by liubo.Story #1145  生成日志的头三句的内容
						//*******************************************					
						
						YssUtil.WriteScheduleLog("日期：【" + sTheDay+ "】开始执行... ...", sLogPath, pub.getPrefixTB(),arrPort[iPort],this.sOperTime,IsOverride);
					
						YssUtil.WriteScheduleLog("组合：【" + sPortForLog+ "】开始执行... ...", sLogPath, pub.getPrefixTB(),arrPort[iPort],this.sOperTime,"0");
						
						YssUtil.WriteScheduleLog("调度方案：【" + sProjectForLog+ "】开始执行... ...", sLogPath, pub.getPrefixTB(),arrPort[iPort],this.sOperTime,"0");
						//*******************end**************************
						
						aryIncome = new ArrayList();
						aryInvest = new ArrayList();
						aryValuation = new ArrayList();
						aryVoucher = new ArrayList();
						aryReport = new ArrayList();
						arySettle = new ArrayList();
						aryBusiness = new ArrayList();
						// 循环方案中的组成部分
						for (int j = 0; j < iAryOrderLen; j++) {
							schProject = (SchProjectPojo) aryProOrder.get(j);
							if (schProject.getFunModules().equalsIgnoreCase(
									"bond")
									|| schProject.getFunModules()
											.equalsIgnoreCase("cash")
									|| schProject.getFunModules()
											.equalsIgnoreCase("Fee")
									|| schProject.getFunModules()
											.equalsIgnoreCase("purchase")) {

								aryIncome.add(schProject);
							} else if (schProject.getFunModules()
									.equalsIgnoreCase("invest")) {
								aryInvest.add(schProject);
							} else if (schProject.getFunModules()
									.equalsIgnoreCase("valcheck")
									|| schProject.getFunModules()
											.equalsIgnoreCase("valuation")) {
								aryValuation.add(schProject);
							} else if (schProject.getFunModules()
									.equalsIgnoreCase("vchproject")) {
								aryVoucher.add(schProject);
							} else if (schProject.getFunModules()
									.equalsIgnoreCase("report")) {
								aryReport.add(schProject);
							} else if (schProject.getFunModules()
									.equalsIgnoreCase("settletype")) {
								arySettle.add(schProject);

								// add by lvhx 2010.06.24 MS01297
								// 计息业务的明细通过业务日期和组合动态获取
								// QDV4赢时胜（深圳）2010年06月02日01_A
							} else if (schProject.getFunModules()
									.equalsIgnoreCase("incometype")) {
								aryIncome.add(schProject);

							} else if (schProject.getFunModules()
									.equalsIgnoreCase("business")) {// 业务处理
																	// MS01179
																	// QDV4赢时胜（深圳）2010年05月25日01_A
																	// panjunfang
																	// add
																	// 20100525
								aryBusiness.add(schProject);
							}

						}

						if (aryBusiness.size() > 0) {
							runStatus.appendSchRunDesc(
									"    【业务处理类】" + YssFun.getTime() + "开始执行... ...", ""); //modify by fangjinag 2011.12.05 STORY #1890 
							//edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
							this.doBusiness(arrPort[iPort], dTheDay,
									aryBusiness,logSumCode);
							runStatus
									.appendSchRunDesc("                  " + YssFun.getTime() + "执行完毕！"); //modify by fangjinag 2011.12.05 STORY #1890 
						}

						if (aryInvest.size() > 0) {
							runStatus.appendSchRunDesc(
									"    【权益处理类】" + YssFun.getTime() + "开始执行... ...", ""); //modify by fangjinag 2011.12.05 STORY #1890 
							//edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
							this.doInvest(arrPort[iPort], dTheDay, aryInvest,logSumCode);
							runStatus
									.appendSchRunDesc("                  " + YssFun.getTime() + "执行完毕！"); //modify by fangjinag 2011.12.05 STORY #1890 
						}
						if (arySettle.size() > 0) {
							runStatus.appendSchRunDesc("    【结算类】" + YssFun.getTime() + "开始执行... ...",
									""); //modify by fangjinag 2011.12.05 STORY #1890
							//edit by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A 添加汇总日志编号
							this.doSettle(arrPort[iPort], dTheDay, arySettle,logSumCode);
							runStatus
									.appendSchRunDesc("                      " + YssFun.getTime() + "执行完毕！");  //modify by fangjinag 2011.12.05 STORY #1890
						}
						if (aryIncome.size() > 0) {
							runStatus.appendSchRunDesc(
									"    【收益计提类】" + YssFun.getTime() + "开始执行... ...", ""); //modify by fangjinag 2011.12.05 STORY #1890
							//edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
							this.doIncomeState(arrPort[iPort], dTheDay, aryIncome, logSumCode);
							runStatus
									.appendSchRunDesc("                  " + YssFun.getTime() + "执行完毕！"); //modify by fangjinag 2011.12.05 STORY #1890
						}
						if (aryValuation.size() > 0) {
							runStatus.appendSchRunDesc(
									"    【日终估值类】" + YssFun.getTime() + "开始执行... ...", ""); //modify by fangjinag 2011.12.05 STORY #1890
							//--- story 2188 add by zhouwei 20120615 执行预警 start---//
							if(this.isEws){
								//证券和现金库存统计
					            BaseStgStatDeal secstgstat = (BaseStgStatDeal) pub.
					                getOperDealCtx().getBean("SecurityStorage");
					            secstgstat.setYssPub(pub);
					            secstgstat.stroageStat(dTheDay, dTheDay,
					                                   operSql.sqlCodes(arrPort[iPort]));
					            secstgstat = (BaseStgStatDeal) pub.
					                getOperDealCtx().getBean("CashStorage");
					            secstgstat.setYssPub(pub);
					            secstgstat.stroageStat(dTheDay, dTheDay,
		                                   operSql.sqlCodes(arrPort[iPort]));
					            //-------统计结束-----
								setEWBean(beanEWS);
								ewAmdin.initBean(beanEWS, null, "日终估值前",
										YssFun.isNumeric(YssCons.YSS_BusinessLog_GenerateType) ? 
												YssFun.toInt(YssCons.YSS_BusinessLog_GenerateType) : 3);
								
								//---add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
								ewAmdin.comeFromDD = true;
								ewAmdin.logSumCode = logSumCode;
								//---add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
								
								//执行预警插件
								ewAmdin.executeEWS(null, false);
								//add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
								
								if(!ewAmdin.isContinue()){
									throw new YssException("组合【"+ewAmdin.getM_produce().getC_PORT_CODE()+"】下的指标【"+ewAmdin.getM_produce().getC_PLUGIN_NAME()+"】出现警告！");
								}
							}	
							//--- story 2188 add by zhouwei 20120615 执行预警 end---//					
							sValReturn = this.doValuation(arrPort[iPort],
									//edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
									dTheDay, aryValuation,logSumCode);
							if (sValReturn.equalsIgnoreCase("false")) {
								// 使用 ` 号做为标识符
								
								runStatus
										.appendSchRunDesc("\r\n★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n        `估值检查发现违规数据！");
								runStatus
								.appendSchRunDesc(this.sCheckInfo);//add by jsc 20120416  【STORY #2406 希望在到帐日执行调度方案时，系统有提示说明当日有权益到账】
								if (this.bIsError) {
									runStatus
											.appendSchRunDesc("                  执行完毕！");
//									sRetInfo = "ValCheckfalse";
									sRetInfo = "false";//edit by licai 20101209 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
									return sRetInfo;
								}
							}
							runStatus
									.appendSchRunDesc("                  " + YssFun.getTime() + "执行完毕！"); //modify by fangjinag 2011.12.05 STORY #1890
							
							//---story 2188 add by zhouwei 20120615 预警 start---//
							if(this.isEws){//是否执行预警的判断应加在资产估值模块
								setEWBean(beanEWS);
								ewAmdin.initBean(beanEWS, null, " ",
										YssFun.isNumeric(YssCons.YSS_BusinessLog_GenerateType) ? 
												YssFun.toInt(YssCons.YSS_BusinessLog_GenerateType) : 3);
								
								//---add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
								ewAmdin.comeFromDD = true;
								ewAmdin.logSumCode = logSumCode;
								//---add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
								
								//执行预警插件
								ewAmdin.executeEWS(null, false);								
								if(!ewAmdin.isContinue()){
									throw new YssException("组合【"+ewAmdin.getM_produce().getC_PORT_CODE()+"】下的指标【"+ewAmdin.getM_produce().getC_PLUGIN_NAME()+"】出现警告！");
								}
							}
							//---story 2188 add by zhouwei 20120615 预警 end---//
						}
						if (aryVoucher.size() > 0) {
							runStatus.appendSchRunDesc("    " +  YssFun.getTime() + "【财务类】开始执行... ...",
									""); //modify by fangjinag 2011.12.05 STORY #1890
							//edit by licai 20101209 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
							String vchPjtRst=this.doVchProject(arrPort[iPort], dTheDay,
									//edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
									aryVoucher,logSumCode);
							if(vchPjtRst.equals("false")){
								runStatus
								.appendSchRunDesc("\r\n        `凭证检查发现违规数据！");
								sRetInfo="false";
							}
							//edit by licai 20101209 BUG #541 =====================================================================end
							runStatus
									.appendSchRunDesc("                      " + YssFun.getTime() + "执行完毕！"); //modify by fangjinag 2011.12.05 STORY #1890
						}
						if (aryReport.size() > 0) {
							runStatus.appendSchRunDesc(
									"    " + YssFun.getTime() +"【日终报表类】开始执行... ...", ""); //modify by fangjinag 2011.12.05 STORY #1890
							String checkResult=this
									.doDayReport(arrPort[iPort], dTheDay,
											//edit by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A
											aryReport,logSumCode);
							if(!checkResult.equals("") && !checkResult.equals("false")){//add by zhouwei 20120305 权益合计与资产净值不相等
								runStatus.appendSchRunDesc("\n[ReportWarn_start]"+checkResult+"[ReportWarn_end]");
							}else{
								runStatus
									.appendSchRunDesc("                  " + YssFun.getTime() + "执行完毕！"); //modify by fangjinag 2011.12.05 STORY #1890
							}
							
						/**Start 20130715 added by liubo.Story #4106.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001
						 * 在日终报表类中添加“日终报表后”执行预警的逻辑*/
							if(this.isEws)
							{//是否执行预警的判断应加在资产估值模块
								setEWBean(beanEWS);
								ewAmdin.initBean(beanEWS, null, "日终报表后",
										YssFun.isNumeric(YssCons.YSS_BusinessLog_GenerateType) ? 
												YssFun.toInt(YssCons.YSS_BusinessLog_GenerateType) : 3);
								
								ewAmdin.comeFromDD = true;
								ewAmdin.logSumCode = logSumCode;
								
								//执行预警插件
								ewAmdin.executeEWS(null, false);								
								if(!ewAmdin.isContinue())
								{
									throw new YssException("组合【"+ewAmdin.getM_produce().getC_PORT_CODE()+"】下的指标【"
											+ewAmdin.getM_produce().getC_PLUGIN_NAME()+"】出现警告！");
								}
							}
							
						/**End 20130715 added by liubo.Story #4106.需求深圳-(南方基金)QDII估值系统V4.0(高)20130621001*/
							
						}

						runStatus.appendSchRunDesc("调度方案：【"
								+ schProject.getProjectCode() + "】" + YssFun.getTime() + "执行结束！\r\n"); //modify by fangjinag 2011.12.05 STORY #1890
						//20110705 added by liubo.Story #1145  生成日志的调度方案执行完成后的内容（成功执行完成的情况）
						//**************************************
						YssUtil.WriteScheduleLog("执行完毕！", sLogPath, pub.getPrefixTB(),arrPort[iPort],this.sOperTime,"0");
						this.IsOverride = "0";
						YssUtil.WriteScheduleLog("调度方案：【" + sProjectForLog + "】执行结束！", sLogPath, pub.getPrefixTB(),arrPort[iPort],this.sOperTime,"0");
						//********************end***************************
					}
					runStatus.appendSchRunDesc("组    合：【" + arrPort[iPort]
							+ "】执行完毕！");
					//20110705 added by liubo.Story #1145  生成日志的组合执行完成后的内容（成功执行完成的情况）
					//**************************************
					YssUtil.WriteScheduleLog("组合：【" + sPortForLog + "】执行完毕！", sLogPath, pub.getPrefixTB(),arrPort[iPort],this.sOperTime,"0");
					//******************end************************
				}
				/**shashijie 2011-09-22 STORY 1458	*/
				runStatus.appendSchRunDesc("组 合 群：【"
						+ this.assetGroupCode + "】执行完毕！");
				/**end*/
				runStatus.appendSchRunDesc("日    期：【"
						+ sTheDay + "】执行完毕！");
				runStatus.appendSchRunDesc("耗时：【"
						+ YssFun.timeDiff(countDate) + "】！");
				runStatus
						.appendSchRunDesc("----------------------------------------\r\n");
				dTheDay = YssFun.addDay(dTheDay, 1);
				//20110705 added by liubo.Story #1145  生成日志的日期执行完成后的内容（成功执行完成的情况）
				//********************************************
				YssUtil.WriteScheduleLog("日期：【" + sTheDay+ "】执行完毕！", sLogPath, pub.getPrefixTB(),sPortForLog,this.sOperTime,"0");
				//********************end************************
			}
		
//			sRetInfo = "true";//edit by licai 20101209 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
		} catch (Exception e) {
			//add by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
			isError = true;//设置为 报错状态
			
			// fanghaoln 20100114 MS00929 QDV4中金2010年01月13日01_B
			String[] message = e.getMessage().split("&");

			if (message.length > 1) {
				String[] messagePZ = message[1].split("\r\n");
				runStatus.appendSchRunDesc(messagePZ[0]);
//				YssUtil.WriteScheduleLog(messagePZ[0], sLogPath, pub.getPrefixTB(),sPortForLog,this.sOperTime,"0");
				//20110705 added by liubo.Story #1145  生成日志的调度方案执行失败的内容（执行失败的的情况）
				//*************************************
				//*****************end****************************
			} else {
				// ------------------end--MS00929----------------------------
				runStatus.appendSchRunDesc(YssFun.getTime() + "执行失败！"); //modify by fangjinag 2011.12.05 STORY #1890
			}
			runStatus.appendSchRunDesc("执行失败！"+e.getMessage().replaceAll("\t", ""));//by guyichuan 2011.07.18 STORY #1236
			YssUtil.WriteScheduleLog(e.getMessage(), sLogPath, pub.getPrefixTB(),sPortForLog,this.sOperTime,"0");	
			YssUtil.WriteScheduleLog("调度方案：【" + sProjectForLog+ "】执行结束... ...", sLogPath, pub.getPrefixTB(),sPortForLog,this.sOperTime,"0");
			YssUtil.WriteScheduleLog("组合：【" + sPortForLog+ "】执行完毕... ...", sLogPath, pub.getPrefixTB(),sPortForLog,this.sOperTime,"0");
			YssUtil.WriteScheduleLog("日期：【" + sTheDay+ "】执行完毕... ...", sLogPath, pub.getPrefixTB(),sPortForLog,this.sOperTime,"0");
			
			throw new YssException(e.getMessage());
		} finally {
			/**shashijie 2011-09-19 STORY 1458 */
			pub.setPrefixTB(sPrefixTB);//还原公共变的里的组合群代码
			//add by songjie 2012.10.23 BUG 5901 QDV4赢时胜(上海开发部)2012年10月10日01_B
			pub.getPortCuryInfo();//获取当前跨组合群对应的基础货币
			/**end*/
			//--- add by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			//添加业务日志数据
    		//edit by songjie 2012.11.20 添加非空判断
    		if(logOper != null){
    			//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
    			if(logOper.getDayFinishLog().getAlPojo().size() == 0){//添加明细日志
        			logOper.setDayFinishIData(this, 36, " ", pub, isError, this.sPortCodes, 
        					this.dStarDate, this.dStarDate, this.dEndDate, " ", 
        					logStartTime, logSumCode, new java.util.Date());
    			}
    			//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
    			logOper.setDayFinishIData(this, 36, "sum", pub, isError, " ", 
    					this.dStarDate, this.dStarDate, this.dEndDate, " ", 
    					logStartTime, logSumCode, new java.util.Date());
    		}
			//--- add by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		}
		
		return sRetInfo;
	}
	
	/**
	 * add by zhouwei 2012.06.05
	 * STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A
	 */
	private void setEWBean(BEN_PLUGIN_PRODUCE ewsBean) throws YssException{
		try{
			ewsBean.setC_PORT_CODE(this.assetGroupCode+"-"+this.sPortForLog);
			ewsBean.setC_PRODUCE_CODE("4.0");
			ewsBean.setC_PLUGIN_PRODUCT("4.0");
			ewsBean.setOperDate_Begin(YssFun.parseDate(this.sTheDay));
			ewsBean.setOperDate_End(YssFun.parseDate(this.sTheDay));
		}catch (Exception ex) {
			throw new YssException("为预警系统Bean赋值出错！", ex);
		}
	}
	
	public ArrayList getSchProject(String sProjectCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		SchProjectPojo schPro = new SchProjectPojo();
		ArrayList aryProject = new ArrayList();
		ArrayList aryProOrder = new ArrayList();
		try {
			sProjectCode = sProjectCode.replaceAll(",", "','");
			sProjectCode = "'" + sProjectCode + "'";
			strSql = "SELECT * FROM "
					+ pub.yssGetTableName("Tb_PFOper_SchProject")
					+ " WHERE FProjectCode IN (" + sProjectCode + ")"
					+ " AND FCheckState = 1"
					+ " ORDER BY FExeOrderCode, FProjectCode, FFunModules";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if ((schPro.getProjectCode() + schPro.getExeOrderCode())
						.equalsIgnoreCase(rs.getString("FProjectCode")
								+ rs.getString("FExeOrderCode"))) {

					schPro = new SchProjectPojo();
					schPro.setProjectCode(rs.getString("FProjectCode"));
					schPro.setProjectName(rs.getString("FProjectName"));
					schPro.setFunModules(rs.getString("FFunModules"));
//					schPro.setAttrCode(dbl
//							.clobStrValue(rs.getClob("FAttrCode")));
					schPro.setAttrCode(getProjectCodeDetail(dbl.clobStrValue(rs.getClob("FAttrCode"))));
					schPro.setExeOrderCode(rs.getString("FExeOrderCode"));
					schPro.setDesc(rs.getString("FDesc") == null ? rs
							.getString("FDesc") : "");
					aryProOrder.add(schPro);
				} else {
					schPro = new SchProjectPojo();
					schPro.setProjectCode(rs.getString("FProjectCode"));
					schPro.setProjectName(rs.getString("FProjectName"));
					schPro.setFunModules(rs.getString("FFunModules"));
//					schPro.setAttrCode(dbl
//							.clobStrValue(rs.getClob("FAttrCode")));

					schPro.setAttrCode(getProjectCodeDetail(dbl.clobStrValue(rs.getClob("FAttrCode"))));
					schPro.setExeOrderCode(rs.getString("FExeOrderCode"));
					schPro.setDesc(rs.getString("FDesc") == null ? rs
							.getString("FDesc") : "");
					aryProOrder = new ArrayList();
					aryProOrder.add(schPro);
					aryProject.add(aryProOrder);
				}
			}
		} catch (Exception e) {
			throw new YssException("获取调度方案出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return aryProject;
	}

	//edit by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A 添加 logSumCode
	public void doSettle(String sPort, java.util.Date dCurDate,
			ArrayList arySettle,String logSumCode) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		StringBuffer buf = new StringBuffer(10000);
		StringBuffer bufNum = new StringBuffer(1000);
		try {
			int iAryLen = arySettle.size();
			for (int i = 0; i < iAryLen; i++) {
				SchProjectPojo schPro = (SchProjectPojo) arySettle.get(i);
				if (schPro.getAttrCode().length() == 0) {
					continue;
				}
				//String sParam = "";
				if (schPro.getFunModules().equalsIgnoreCase("settletype")) {
					String[] arrType = schPro.getAttrCode().split(",");
					for (int j = 0; j < arrType.length; j++) {
						// 做 TA 结算
						if (arrType[j].equalsIgnoreCase("TA Settle")) {
							buf.delete(0, buf.length());
							TaTradeBean taTrade = new TaTradeBean();
							taTrade.setYssPub(pub);
							//edited by zhouxiang MS01697    调度方案中的结算步骤增加一参数控制结算按成交日或结算日   20100911
							CtlPubPara pubPara=new CtlPubPara();
							pubPara.setYssPub(pub);
							String para=pubPara.getRateCalculateType("TATradeSettle","ComboBox1",1);  //modify by fangjiang 2010.11.15 BUG #284 调度方案中的TA交易结算存在一定问题 
							//------------------------end-------------------------------------------------------------
							strSql = "SELECT * FROM "
									+ pub.yssGetTableName("Tb_TA_Trade")
									+ " WHERE FCheckState = 1 AND FSettleState = 0 AND FPortCode = "
									+ dbl.sqlString(sPort)
									//edited by zhouxiang MS01697    调度方案中的结算步骤增加一参数控制结算按成交日或结算日   20100911
									+ " AND "+(para.equals("0,0")? " FConfimDate= " : "FsettleDate = ") //modify by fangjiang 2010.11.15 BUG #284 调度方案中的TA交易结算存在一定问题 
									//-------------------end------------------------------------------------------------------
									+ dbl.sqlDate(dCurDate);
							rs = dbl.openResultSet(strSql);
							while (rs.next()) {
								bufNum.append(rs.getString("FNum")).append(",");
								buf.append(rs.getString("FNum")).append("\t");
								buf.append(rs.getString("FSellType")).append(
										"\t");
								buf.append(rs.getString("FSellNetCode"))
										.append("\t");
								buf.append(rs.getString("FAnalysisCode1"))
										.append("\t");
								buf.append(rs.getString("FAnalysisCode2"))
										.append("\t");
								buf.append(rs.getString("FAnalysisCode3"))
										.append("\t");
								buf.append(rs.getString("FPortCode")).append(
										"\t");
								buf.append(rs.getString("FCashAccCode"))
										.append("\t");
								buf.append(
										rs.getDate("FMarkDate") == null ? ""
												: rs.getString("FMarkDate"))
										.append("\t");
								buf.append(rs.getDate("FTradeDate")).append(
										"\t");
								buf.append(rs.getDate("FConfimDate")).append(
										"\t");
								buf.append(rs.getDate("FSettleDate")).append(
										"\t");
								buf.append(rs.getString("FPortCuryRate"))
										.append("\t");
								buf.append(rs.getString("FBaseCuryRate"))
										.append("\t");
								buf.append(rs.getString("FSellAmount")).append(
										"\t");
								buf.append(rs.getString("FSellPrice")).append(
										"\t");
								buf.append(rs.getString("FSellMoney")).append(
										"\t");
								buf
										.append(
												rs.getString("FIncomeNotBal") == null ? ""
														: rs
																.getString("FIncomeNotBal"))
										.append("\t");
								buf.append(
										rs.getString("FIncomeBal") == null ? ""
												: rs.getString("FIncomeBal"))
										.append("\t");
								buf.append(rs.getString("FCuryCode")).append(
										"\t");
								buf.append("").append("\t"); // 描述的位置
								taTrade.loadFees(rs);
								buf.append(taTrade.getFees()).append("\t");
								buf.append(rs.getString("FCheckState")).append(
										"\t");
								buf.append("").append("\t"); // OldNum
								buf.append("1").append("\t");
								buf.append("9998-12-31").append("\t");
								buf.append("9998-12-31").append("\t");
								buf.append("").append("\t"); // fee
								buf.append(rs.getString("FSettleState"))
										.append("\t");
								buf.append(rs.getString("FPortClsCode"))
										.append("\t");
								// ----MS00314 QDV4南方2009年3月11日01_B
								// 添加新增加的几个字段的获取-------------------------------//
								/**add---huhuichao 2013-12-4  BUG #84694 资金调拨取数逻辑有误  */
								//系统的资金调拨的取数逻辑是当有实际结算金额时，以实际结算金额为准，当实际结算金额为0时，以销售金额为准
								//此处将获取到的值为null的结算金额设置为“0”，因为后面会有是否数值型的判断
								buf
										.append(
												rs.getString("FSettleMoney") == null ? 0
														: rs
																.getString("FSettleMoney"))
										.append("\t");
								/**end---huhuichao 2013-12-4 BUG #84694*/
								buf.append(
										null == rs.getDate("FConfimDate") ? rs
												.getDate("FSettleDate") : rs
												.getDate("FConfimDate"))
										.append("\t");
								buf.append(
										null == rs.getDate("FConfimDate") ? rs
												.getDate("FSettleDate") : rs
												.getDate("FConfimDate"))
										.append("\t");
								// fanghaoln 20100326 MS01025
								// QDV4南方2010年3月12日02_B------------------
								buf.append(rs.getDouble("FBeMarkMoney"))
										.append("\t");
								// ------------------------------------------------------------------------------------------//
								buf.append(rs.getDouble("Fsplitratio")).append(
										"\t");// 拆分比例
								buf.append(pub.getAssetGroupCode())
										.append("\t");// 由于在TA交易里面修改了业务增加了字段所在在调度方案里面也要相应增加字段
								buf.append("false").append("\t")
								
								//20120615 added by liubo.Bug #4823
								//调度方案执行中增加实收基金金额的字段
								//==============================
								.append(rs.getDouble("FPAIDINMONEY"))
								//==============end================
								/**shashijie 2012-9-17 BUG 5760 李萍,建行执行调度方案执行TA交易数据没有结算,但是手动结算没有问题*/
								.append("\t").append(rs.getDouble("FPortdegree"))
								/**end shashijie 2012-9-17 BUG */
								.append("\t\nnull")
								
								//20130606 added by liubo.Story #3759
								//调度方案中增加“折算类型”和“拆分前单位净值”
								//========================
								.append("\t").append(rs.getString("FCONVERTTYPE")).append("\t")
								.append(rs.getDouble("FSPLITNETVALUE"))
								//============end============
								
								.append( //modify by fangjiang 2011.11.21 BUG 3186
										"\r\n");
								// -------------end-----MS01025------------------------------------------
							}
							if (buf.length() > 0) {
								buf.append("\f\f").append(bufNum)
										.append("\f\f").append("do");
							} else {
								// 2008.07.29 蒋锦 修改 将 return 改为 continue
								// BUG：0000350
								// 在没有数据的情况下应该继续循环执行
								dbl.closeResultSetFinal(rs);
								continue;
							}
							TATradeSettleBean taSettle = (TATradeSettleBean) pub
									.getSettlementCtx()
									.getBean("tatradesettle");
							taSettle.setYssPub(pub);
							taSettle.parseRowStr(buf.toString());
							//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A start---//
							taSettle.logOper = this.logOper;//日志实例
							taSettle.logSumCode = this.logSumCode;//汇总日志编号
							taSettle.comeFromDD = true;//判断是由调度方案调用
							//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A end---//
							taSettle.doOperation("do");
							dbl.closeResultSetFinal(rs);
						}
						// 做交易结算
						else if (arrType[j].equalsIgnoreCase("Trade Settle")) {
							buf.delete(0, buf.length());
							/**shashijie 2012-7-20 STORY 2565 */
							//edited by zhouxiang MS01697    调度方案中的结算步骤增加一参数控制结算按成交日或结算日   20100911
							/*CtlPubPara pubPara=new CtlPubPara();
							pubPara.setYssPub(pub);
							String para=pubPara.getRateCalculateType("TradeSettle","ComboBox1",1);*/
							//------------------------end-------------------------------------------------------------
							
							//获取通用参数0,0表示设置为"是"的数据
							String para = getPara("TradeSettle","0,0",sPort);
							/**end*/
							
							strSql = "SELECT FNUM FROM "
									+ pub.yssGetTableName("Tb_Data_SubTrade")
									+ " WHERE FCheckState = 1 AND FSettleState = 0 AND FPortCode = "
									+ dbl.sqlString(sPort)
									//edited by zhouxiang MS01697    调度方案中的结算步骤增加一参数控制结算按成交日或结算日   20100911
									+ " AND "+(para.equals("0,0")? " fbargaindate= " : "FsettleDate = ")
									//-------------------end------------------------------------------------------------------
									+ dbl.sqlDate(dCurDate);
							rs = dbl.openResultSet(strSql);
							while (rs.next()) {
								buf.append(rs.getString("FNUM")).append("\t");
							}
							
							//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
							dbl.closeResultSetFinal(rs);
							
							if (buf.length() > 0) {
								buf.delete(buf.length() - 1, buf.length());
								buf.insert(0, "do\f\f");
							} else {
								// 2008.07.29 蒋锦 修改 将 return 改为 continue
								// BUG：0000350
								// 在没有数据的情况下应该继续循环执行
								continue;
							}
							TradeSettleBean settle = (TradeSettleBean) pub
									.getSettlementCtx().getBean("tradesettle");
							settle.setYssPub(pub);
							settle.parseRowStr(buf.toString());
							//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A start---//
							settle.logOper = this.logOper;//日志实例
							settle.logSumCode = this.logSumCode;//汇总日志编号
							settle.comeFromDD = true;//由调度方案调用
							//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A end---//
							settle.doOperation("settle");
						}
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("处理结算类出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2012-7-20 STORY 2565 */
	private String getPara(String Fpubparacode, String Fctlvalue, String sPort) throws YssException {
		String value = "";
		ResultSet rs = null;
		int row = 0;//记录数
		try {
			String query = getParaQuery(Fpubparacode,Fctlvalue);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//若有设置"是"并且没有设置组合,表示全局定义
				if (rs.getString("Fctlvalue").trim().equals("//|") && 
						rs.getString("FCtlCode").trim().equalsIgnoreCase("FportCode")) {
					value = "0,0";
				}
				//若有设置"是",并且组合也是当前组合
				else if (rs.getString("FCtlCode").trim().equalsIgnoreCase("FportCode") &&
						rs.getString("Fctlvalue").trim().indexOf(sPort) > -1) {
					value = "0,0";
				}
				row = rs.getRow();
			}
			//如果只有1条结果集说明是旧数据,也需要支持,返回0,0
			if (row == 1) {
				value = "0,0";
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}


	/**shashijie 2012-7-20 STORY 2565 */
	private String getParaQuery(String Fpubparacode, String Fctlvalue) {
		String query = "Select a.*" +
			" From "+pub.yssGetTableName("Tb_Pfoper_Pubpara")+" a" +
			" Where a.Fparaid In (Select Fparaid" +
			" From "+pub.yssGetTableName("Tb_Pfoper_Pubpara")+
			" Where Fpubparacode = "+dbl.sqlString(Fpubparacode)+
            " And Fparaid <> 0" +
            " And Fctlvalue = "+dbl.sqlString(Fctlvalue)+" ) " +
    		" And a.Fpubparacode = "+dbl.sqlString(Fpubparacode)+
    		" And a.Fparaid <> 0 ";
		return query;
	}
	
	//edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
	public void doIncomeState(String sPort, java.util.Date dCurDate,
			ArrayList aryIncome, String logSumCode) throws YssException {
		try {
			int iAryLen = aryIncome.size();
			for (int i = 0; i < iAryLen; i++) {
				SchProjectPojo schPro = (SchProjectPojo) aryIncome.get(i);
				if (schPro.getAttrCode().length() == 0) {
					continue;
				}
				String sParam = "";
				//String sParamPart = "";
				if (schPro.getFunModules().equalsIgnoreCase("bond")) {
					sParam = YssFun.formatDate(dCurDate) + "\n"
							+ YssFun.formatDate(dCurDate)
							+ "\n"
							+
							// fanghaoln 20100114 MS00929 QDV4中金2010年01月13日01_B
							// 跨组合群加了组合群代码的解析所以这里也加
							sPort + "\n" + schPro.getAttrCode() + "\n"
							+ "statbondinterest" + "\n" + pub.getPrefixTB()
							+ "\n" + " ";
				} else if (schPro.getFunModules().equalsIgnoreCase("cash")) {
					sParam = YssFun.formatDate(dCurDate) + "\n"
							+ YssFun.formatDate(dCurDate)
							+ "\n"
							+
							// fanghaoln 20100114 MS00929 QDV4中金2010年01月13日01_B
							// 跨组合群加了组合群代码的解析所以这里也加
							sPort + "\n" + schPro.getAttrCode() + "\n"
							+ "stataccinterest" + "\n" + pub.getPrefixTB()
							+ "\n" + " ";
				} else if (schPro.getFunModules().equalsIgnoreCase("Fee")) {
					sParam = YssFun.formatDate(dCurDate) + "\n"
							+ YssFun.formatDate(dCurDate)
							+ "\n"
							+
							// fanghaoln 20100114 MS00929 QDV4中金2010年01月13日01_B
							// 跨组合群加了组合群代码的解析所以这里也加
							sPort + "\n" + schPro.getAttrCode() + "\n"
							+ "statinvestfee" + "\n" + pub.getPrefixTB() + "\n"
							+ " ";
				} else if (schPro.getFunModules().equalsIgnoreCase("purchase")) {
					sParam = YssFun.formatDate(dCurDate) + "\n"
							+ YssFun.formatDate(dCurDate)
							+ "\n"
							+
							// fanghaoln 20100114 MS00929 QDV4中金2010年01月13日01_B
							// 跨组合群加了组合群代码的解析所以这里也加
							sPort + "\n" + schPro.getAttrCode() + "\n"
							+ "stataccpurchase" + "\n" + pub.getPrefixTB()
							+ "\n" + " ";
					// -----------------------------------------------end--MS00929---------------------------------------
					// add by lvhx 2010.06.24 MS01297 计息业务的明细通过业务日期和组合动态获取
					// QDV4赢时胜（深圳）2010年06月02日01_A
				} else if (schPro.getFunModules()
						.equalsIgnoreCase("incometype")) {
					String[] AttrCode = schPro.getAttrCode().split(",");
					// YssPub pub = null;
					

					for (int j = 0; j < AttrCode.length; j++) {
						if (AttrCode[j].equalsIgnoreCase("bond")) {
							FixInterestBean bean = new FixInterestBean();// 债券计息
							bean.setYssPub(pub);
							//--- add by songjie 2013.08.22 BUG 9160 QDV4赢时胜(上海开发)2013年8月21日01_B start---//
							bean.setSPortCode(sPort);//设置组合代码
							//--- add by songjie 2013.08.22 BUG 9160 QDV4赢时胜(上海开发)2013年8月21日01_B end---//
							
							sParam = YssFun.formatDate(dCurDate) + "\n"
									+ YssFun.formatDate(dCurDate) + "\n"
									//--- edit by songjie 2013.08.22 BUG 9160 QDV4赢时胜(上海开发)2013年8月21日01_B start---//
									//添加参数 dCurDate 用于获取有前一日库存的已审核债券代码
									+ sPort + "\n" + bean.getIncomeTypeData(dCurDate)
									//--- edit by songjie 2013.08.22 BUG 9160 QDV4赢时胜(上海开发)2013年8月21日01_B end---//
									+ "\n" + "statbondinterest" + "\n"
									+ pub.getPrefixTB() + "\n" + " ";
							
							IncomeStatBean incomeState = (IncomeStatBean) pub
							.getDayFinishCtx().getBean("incomestat");
					        incomeState.setYssPub(pub);
					        incomeState.parseRowStr(sParam);
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					        incomeState.logSumCode = logSumCode;//汇总日志编号
					        incomeState.comeFromDD = true;//是否通过调度方案调用
					        incomeState.logOper = this.logOper;//设置日志实例
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
					        incomeState.doOperation("dayfinish");
					 		
						} else if (AttrCode[j].equalsIgnoreCase("cash")) {
							CashAccountBean bean = new CashAccountBean();// 现金计息
							bean.setYssPub(pub);
							sParam = YssFun.formatDate(dCurDate) + "\n"
									+ YssFun.formatDate(dCurDate) + "\n"
									+ sPort + "\n" + bean.getIncomeTypeData()
									+ "\n" + "stataccinterest" + "\n"
									+ pub.getPrefixTB() + "\n" + "0"; //------ modify by wangzuochun 2010.08.14  MS01583    使用调度方案进行收益计提时没能产生直销申购款的计息数据，导致净值有差    QDV4赢时胜上海2010年08月10日01_B   
							
							IncomeStatBean incomeState = (IncomeStatBean) pub
							.getDayFinishCtx().getBean("incomestat");
					        incomeState.setYssPub(pub);
					        incomeState.parseRowStr(sParam);
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					        incomeState.logSumCode = logSumCode;//汇总日志编号
					        incomeState.comeFromDD = true;//是否通过调度方案调用
					        incomeState.logOper = this.logOper;//设置日志实例
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
					        incomeState.doOperation("dayfinish");
							
						} else if (AttrCode[j].equalsIgnoreCase("Fee")) {
							InvestPayCatBean bean = new InvestPayCatBean();// 两费计提
							bean.setYssPub(pub);
							sParam = YssFun.formatDate(dCurDate) + "\n"
									+ YssFun.formatDate(dCurDate) + "\n"
									+ sPort + "\n" + bean.getIncomeTypeData()
									+ "\n" + "statinvestfee" + "\n"
									+ pub.getPrefixTB() + "\n" + " ";
							
							IncomeStatBean incomeState = (IncomeStatBean) pub
							.getDayFinishCtx().getBean("incomestat");
					        incomeState.setYssPub(pub);
					        incomeState.parseRowStr(sParam);
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					        incomeState.logSumCode = logSumCode;//汇总日志编号
					        incomeState.comeFromDD = true;//是否通过调度方案调用
					        incomeState.logOper = this.logOper;//设置日志实例
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
					        incomeState.doOperation("dayfinish");
							
						} else if (AttrCode[j].equalsIgnoreCase("purchase")) {
							PurchaseBean bean = new PurchaseBean(); // 回购计息
							bean.setYssPub(pub);
							sParam = YssFun.formatDate(dCurDate) + "\n"
									+ YssFun.formatDate(dCurDate) + "\n"
									+ sPort + "\n" + bean.getIncomeTypeData()
									+ "\n" + "stataccpurchase" + "\n"
									+ pub.getPrefixTB() + "\n" + " ";
							
							IncomeStatBean incomeState = (IncomeStatBean) pub
							.getDayFinishCtx().getBean("incomestat");
					        incomeState.setYssPub(pub);
					        incomeState.parseRowStr(sParam);
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					        incomeState.logSumCode = logSumCode;//汇总日志编号
					        incomeState.comeFromDD = true;//是否通过调度方案调用
					        incomeState.logOper = this.logOper;//设置日志实例
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
					        incomeState.doOperation("dayfinish");
							
						} 
						/**
						* shashijie 2010.1.19 需求:STORY #113 证券借贷业务需求
						* TASK #2267::证券借贷业务需求 - 调度方案中收益计提增加证券借贷计息的处理
						*/
						else if(AttrCode[j].equalsIgnoreCase("bondInterest")){
							TradeSecurityLendBean bean = new TradeSecurityLendBean(); // 证券借贷计息
							bean.setYssPub(pub);
							sParam = YssFun.formatDate(dCurDate) + "\n"
									+ YssFun.formatDate(dCurDate) + "\n"
									+ sPort + "\n" + 
									/**传入证券代码  shashijie 2011-09-14 占时处理*/
//									bean.getIncomeTypeData(YssFun.formatDate(dCurDate),sPort).split(",\t")[0].split("\t")
									bean.getIncomeTypeData(YssFun.formatDate(dCurDate),sPort).split(",\t")[0].split("\t")[0]
									/**end*/
									+ "\n" + "statseclendinterest" + "\n"
									+ pub.getPrefixTB() + "\n" + " ";
							
							IncomeStatBean incomeState = (IncomeStatBean) pub.getDayFinishCtx().getBean("incomestat");
					        incomeState.setYssPub(pub);
					        incomeState.parseRowStr(sParam);
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					        incomeState.logSumCode = logSumCode;//汇总日志编号
					        incomeState.comeFromDD = true;//是否通过调度方案调用
					        incomeState.logOper = this.logOper;//设置日志实例
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
					        incomeState.doOperation("dayfinish");
						}
						/**end*/
						//sParam=sParam+sParamPart;
						/**shashijie 2011-09-14 BUG2633业务平台->调度方案设置中的收益计提类中没有基金万份收益计提 */
						else if(AttrCode[j].equalsIgnoreCase("IncomeStatBean")){
							MonetaryFundInsAdmin bean = new MonetaryFundInsAdmin();//基金万分收益计提
							bean.setYssPub(pub);
							sParam = YssFun.formatDate(dCurDate) + "\n"
									+ YssFun.formatDate(dCurDate) + "\n"
									//证券代码
									+ sPort + "\n" + bean.getIncomeTypeData(YssFun.formatDate(dCurDate),sPort)
									//BeanId
									+ "\n" + "statmonetaryfundins" + "\n"
									+ pub.getPrefixTB() + "\n" + " ";
							
							IncomeStatBean incomeState = (IncomeStatBean) pub.getDayFinishCtx().getBean("incomestat");
					        incomeState.setYssPub(pub);
					        incomeState.parseRowStr(sParam);
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					        incomeState.logSumCode = logSumCode;//汇总日志编号
					        incomeState.comeFromDD = true;//是否通过调度方案调用
					        incomeState.logOper = this.logOper;//设置日志实例
					        //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
					        incomeState.doOperation("dayfinish");
						}
						/**end*/
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("处理收益计提类出错！", e);
		}
	}

	//edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode、sPort、dCurDate
	public void doInvest(String sPort, java.util.Date dCurDate, ArrayList aryInvest, String logSumCode) throws YssException {        
		String[] arrInvType = null;
		OperDealBean operDeal = null;
		try {
			int iAryLen = aryInvest.size();
			for (int i = 0; i < iAryLen; i++) {
				SchProjectPojo schPro = (SchProjectPojo) aryInvest.get(i);
				if (schPro.getAttrCode() == null) {
					continue;
				}
				arrInvType = schPro.getAttrCode().split(",");
				
				// fanghaoln 20100114 MS00929 QDV4中金2010年01月13日01_B
				// 跨组合群加了组合群代码的解析所以这里也加
				String sParam = YssFun.formatDate(dCurDate) + "\t"
						+ YssFun.formatDate(dCurDate) + "\t" + sPort + "\t"
						+ pub.getPrefixTB() + "\t" + schPro.getAttrCode();
				// ------------------------------end
				// MS00929----------------------------
				operDeal = new OperDealBean();
				operDeal.setYssPub(pub);
				// fanghaoln 20100114 MS00929 QDV4中金2010年01月13日01_B
				operDeal.setYssRunStatus(this.runStatus);// runStatus为空报错
				// --------------------------end--MS00929-----------------------------
				operDeal.parseRowStr(sParam);
				//--- add by songjie 2012.08.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
				operDeal.setFunName("OperDeal");
				operDeal.logSumCode = logSumCode;//添加 logSumCode
				operDeal.comeFromDD = true;//判断通过调度方案调用
				operDeal.logOper = this.logOper;//设置日志实例
				//--- add by songjie 2012.08.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				
				operDeal.addSetting();
			}
		} catch (Exception e) {
			throw new YssException("处理权益处理类出错！", e);
		}
	}

	/**
	 * 执行业务处理 MS01179 QDV4赢时胜（深圳）2010年05月25日01_A panjunfang add 20100525
	 * 
	 * @param sPort
	 * @param dCurDate
	 * @param aryInvest
	 * @throws YssException
	 */
	//edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
	public void doBusiness(String sPort, java.util.Date dCurDate,
			ArrayList arrBusinesses, String logSumCode) throws YssException {
		ValuationBean valuation = null;
		try {
			int iAryLen = arrBusinesses.size();
			for (int i = 0; i < iAryLen; i++) {
				SchProjectPojo schPro = (SchProjectPojo) arrBusinesses.get(i);
				if (schPro.getAttrCode() == null) {
					continue;
				}
				String sParam = YssFun.formatDate(dCurDate)
						+ "\t"
						+ YssFun.formatDate(dCurDate)
						+ "\t"
						+ sPort
						+ "\tnull"
						+ "\tnull"
						+ "\t"
						+ pub.getPrefixTB()
						+ "\tnull"
						+ "\t"
						+ getValuationRequestStr(schPro.getAttrCode(),
								"business") + "\tnull";
				valuation = (ValuationBean) pub.getDayFinishCtx().getBean(
						"valuation");
				valuation.setYssPub(pub);
				valuation.setYssRunStatus(this.runStatus);
				valuation.parseRowStr(sParam);
				
				//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
				valuation.logSumCode = logSumCode;//添加 logSumCode
				valuation.comeFromDD = true;//表示通过调度方案调用
				valuation.logOper = this.logOper;//设置日志实例
				//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				
				valuation.doOperManage("");
			}
		} catch (Exception e) {
			throw new YssException("执行业务处理出错！", e);
		}
	}

	public String doValuation(String sPort, java.util.Date dCurDate,
			//edit by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
			ArrayList aryValuation, String logSumCode) throws YssException {
		SchProjectPojo schValCheck = null;
		SchProjectPojo schValuation = null;
		ValuationBean valuation = null;
		String sValCheckResult = "";
		try {
			for (int i = 0; i < aryValuation.size(); i++) {
				SchProjectPojo tmpSch = (SchProjectPojo) aryValuation.get(i);
				if (tmpSch.getFunModules().equalsIgnoreCase("valcheck")) {
					schValCheck = tmpSch;
				} else if (tmpSch.getFunModules().equalsIgnoreCase("valuation")) {
					schValuation = tmpSch;
				}
			}
			//edit by songjie 2012.09.18 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 如果设置预警执行属性值为 hide，则做估值检查
			if (schValCheck != null && schValCheck.getAttrCode().length() != 0 && YssCons.YSS_EWCLUE_MODE.equalsIgnoreCase("hide")) {
				// valuation = new ValuationBean(); //BugNo:0000438 edit by jc
				// 新new的实例有些属性为null，插入日志时会报错
				String sParam = YssFun.formatDate(dCurDate)
						+ "\t"
						+ YssFun.formatDate(dCurDate)
						+ "\t"
						+ sPort
						+ "\t"
						+
						// fanghaoln 20100114 MS00929 QDV4中金2010年01月13日01_B
						// 跨组合群加了组合群代码的解析所以这里也加
						getValuationRequestStr(schValCheck.getAttrCode(),
								"valcheck") + "\t" + pub.getPrefixTB()
						+ "\tnull" + "\tnull" + "\tnull" + "\tnull";
				// ---------------------------------------end----MS00929-------------------------------------------------------------------
				valuation = (ValuationBean) pub.getDayFinishCtx().getBean(
						"valuation"); // BugNo:0000438 edit by jc
				valuation.setYssPub(pub);
				valuation.setYssRunStatus(this.runStatus);
				valuation.parseRowStr(sParam);
				//20110713 added by liubo.Story #1145
				//以下几个变量用来生成调度方案执行的路径，其中setsNeedLog为true是表示需要生成日志，为FALSE时表示不需要
				//**************************************
				valuation.setsNeedLog("true");
				valuation.setsOperTime(sOperTime);
				valuation.setsLogPath(sLogPath);
				//*************end*******************************
				this.sCheckInfo=""; //add by jsc 20120416   【STORY #2406 希望在到帐日执行调度方案时，系统有提示说明当日有权益到账】
				valuation.doValCheck();
				sValCheckResult = valuation.getValCheckIsError();
				this.sCheckInfo = (String)this.runStatus.getRunDesc("ValCheckRun");//add by jsc 20120416  【STORY #2406 希望在到帐日执行调度方案时，系统有提示说明当日有权益到账】
				if (this.bIsError) {
					if (sValCheckResult.equalsIgnoreCase("false")) {
						
						return sValCheckResult;
					}
				}
			}
			if (schValuation != null
					&& schValuation.getAttrCode().length() != 0) {
				// valuation = new ValuationBean(); //BugNo:0000438 edit by jc
				// 新new的实例有些属性为null，插入日志时会报错
				String sParam = YssFun.formatDate(dCurDate)
						+ "\t"
						+ YssFun.formatDate(dCurDate)
						+ "\t"
						+ sPort
						+ "\t"
						+
						// fanghaoln 20100114 MS00929 QDV4中金2010年01月13日01_B
						// 跨组合群加了组合群代码的解析所以这里也加
						getValuationRequestStr(schValuation.getAttrCode(),
								"valuation") + "\t" + pub.getPrefixTB()
						+ "\tnull" + "\tnull" + "\tnull" + "\tnull";
				// ---------------------------------------end----MS00929-------------------------------------------------------------------
				valuation = (ValuationBean) pub.getDayFinishCtx().getBean(
						"valuation"); // BugNo:0000438 edit by jc
				valuation.setYssPub(pub);
				valuation.setYssRunStatus(this.runStatus);
				valuation.parseRowStr(sParam);
				//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
				valuation.comeFromDD = true;//是否通过调度方案调用
				valuation.logSumCode = logSumCode;//汇总日志编号
				valuation.logOper = this.logOper;//设置日志实例
				//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				valuation.doOperation("dayfinally");
			}
		} catch (Exception e) {
			throw new YssException("处理资产估值类出错！", e);
		}
		return sValCheckResult;
	}
    
	/**edit by licai 20101209 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
	 * @param sPort
	 * @param dCurDate
	 * @param aryVoucher
	 * @return 
	 * 需要增加返回"true"或"false"
	 * @throws YssException
	 */
	 //edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
	public String doVchProject(String sPort, java.util.Date dCurDate, ArrayList aryVoucher,String logSumCode) throws YssException {
		CtlVchExcuteBean vchExe = null;
		SchProjectPojo schPro = null;
		String vchPjtRst="true";//edit by licai 20101209 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
        //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        DayFinishLogBean df = new DayFinishLogBean();
        df.setYssPub(pub);
        java.util.Date logStartTime = new java.util.Date();
        if(logOper == null){//添加空指针判断
        	logOper = SingleLogOper.getInstance();
        }
        //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		try {
			int iAryLen = aryVoucher.size();
				for (int i = 0; i < iAryLen; i++) {
					schPro = (SchProjectPojo) aryVoucher.get(i);
					if (schPro.getAttrCode().length() == 0) {
						continue;
					}
					String sParam = YssFun.formatDate(dCurDate) + "\t"
						+ YssFun.formatDate(dCurDate) + "\t" + sPort + "\t"
						+ schPro.getAttrCode();
					vchExe = new CtlVchExcuteBean();
					vchExe.setRunStatus(runStatus);
					vchExe.setYssPub(pub);
					
					//---add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					vchExe.comeFromDD = true;//用于判断是通过调度方案执行调用凭证方案执行的
					vchExe.logSumCode = logSumCode;//汇总日志编号
					vchExe.logOper = this.logOper;//设置日志实例
					//---add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
					
					vchExe.parseRowStr(sParam);
					
					//add by licai 20101209 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
					String doOpRst=vchExe.doOperation("project");
					if(doOpRst.equals("false")){
					vchPjtRst="false";
				}
				//add by licai 20101209 BUG #541 =======================================================================end
			}

			//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加参数 logSumCode
			this.doVchCuryCheck(sPort, dCurDate, aryVoucher, logSumCode);
			
			return vchPjtRst;//addd by licai 20101209 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
		} catch (Exception e) {
			vchPjtRst="false";//add by licai 20101209 BUG #541 凭证方案执行时，如果有凭证科目未做到最明细，导入凭证到财务系统时会检查并给出提示 
			YssUtil.WriteScheduleLog(e.getLocalizedMessage(), sLogPath, pub.getPrefixTB(),sPortForLog,this.sOperTime,"0");
			throw new YssException("处理财务类出错！\n" + e.getMessage(), e);
		}
		
	}
	
	//edit by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加参数 logSumCode
	//add by lidaolong 20110408 #665 凭证导入时检查凭证币种，弹出提示窗口
	public String doVchCuryCheck(String sPort, java.util.Date dCurDate, ArrayList aryVoucher, String logSumCode) throws YssException {
		CtlVchExcuteBean vchExe = null;
		SchProjectPojo schPro = null;
		String vchPjtRst=""; 
		//---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        java.util.Date logStartTime = new java.util.Date();
        if(logOper == null){//设置日志实例
        	logOper = SingleLogOper.getInstance();
        }
        String logInfo = "";
        //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		try {
			int iAryLen = aryVoucher.size();
			for (int i = 0; i < iAryLen; i++) {
				//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
				logInfo = "";//日志详细信息
				
				schPro = (SchProjectPojo) aryVoucher.get(i);
				if (schPro.getAttrCode().length() == 0) {
					continue;
				}
				
				String sParam = YssFun.formatDate(dCurDate) + "\t"
						+ YssFun.formatDate(dCurDate) + "\t" + sPort + "\t"
						+ schPro.getAttrCode();
				vchExe = new CtlVchExcuteBean();
				vchExe.setRunStatus(runStatus);
				vchExe.setYssPub(pub);
				vchExe.parseRowStr(sParam);
			 
				//add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
				logInfo += "开始检查凭证方案【" + schPro.getProjectCode() + "】相关凭证是否有多币种... ...\r\n";
				
				String doOpRst=vchExe.doCuryCheck();
				
				//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
				logInfo += "检查完成\r\n";
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, 24, 
        					"凭证方案【" + schPro.getProjectCode() + "】检查",
        					pub, false, " ", dCurDate, dCurDate, dCurDate, 
        					logInfo, logStartTime, logSumCode, new java.util.Date());
        		}
				//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				
				if(!doOpRst.equals("")){
					return doOpRst;
				}
			
			}
		} catch (Exception e) {
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			try{
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, 24, 
        					"凭证方案【" + schPro.getProjectCode() + "】检查",
        					pub, true, " ", dCurDate, dCurDate, dCurDate, 
        					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
        					(logInfo + "\r\n凭证检查出现多币种异常\r\n" + e.getMessage())//处理日志信息 除去特殊符号
        					.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""), 
        					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
        					logStartTime, logSumCode,new java.util.Date());
        		}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
			finally{//添加 finally 保证可以抛出异常
				throw new YssException("检查一张凭证是否有多币种出错！");
			}
			//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
		}
		return vchPjtRst; 
	}//end by lidaolong
	
	//edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A 添加 logSumCode
	public String doDayReport(String sPort, java.util.Date dCurDate,
			ArrayList aryReport,String logSumCode) throws YssException {
		SchProjectPojo schPro = null;
		String[] sRepType = null;
		String sParam = "";
		String sBookCode = "";
		String reStr="";
		try {
			// 通过组合代码获取套帐代码
			YssFinance yssFin = new YssFinance();
			yssFin.setYssPub(pub);
			sBookCode = yssFin.getCWSetCode(sPort);

			int iAryLen = aryReport.size();
			for (int i = 0; i < iAryLen; i++) {
				schPro = (SchProjectPojo) aryReport.get(i);
				if (schPro.getAttrCode().length() == 0) {
					continue;
				}
				sRepType = schPro.getAttrCode().split(",");
				for (int j = 0; j < sRepType.length; j++) {
					if (sRepType[j].equalsIgnoreCase("guessvalue")) {
						if (sBookCode.length() == 0) {
							runStatus
									.appendValRunDesc("    财务估值表生成失败！没有找到与组合相关联的套帐！\r\n");
							continue;
						}
						sParam = YssFun.formatDate(dCurDate) + "\t" + sBookCode
								+ "\t" + "***";
						GuessValue guess = new GuessValue();
						guess.setYssPub(pub);
						
						//--- add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
						guess.logSumCode = logSumCode;//汇总日志编号
						guess.logOper = this.logOper;//设置日志实例
						//--- add by songjie 2012.09.21 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
						
						guess.buildGuessValueReport(sParam);
						reStr=guess.checkSomeNetIndexs(sParam);//add by zhouwei 20120305 检查权益合计与资产净值
						
						/**shashijie 2012-9-26 BUG 5814 通过"调度方案执行"生成财务估值表后，查询不到估值增值科目核对结果*/
						//判断通参中是否设置检查
						if (isPubPara(sPort)) {
							String parameter = YssFun.formatDate(dCurDate) + "\t" + //日期
							YssFun.toInt(sBookCode) + "\t" + //套帐
							"False";//是否修改市值
							
						/**Start 20130625 modified by liubo.Bug #8297.QDV4广发基金2013年6月17日01_B
						 * 用调度方案生成估值表时，估值表与余额表的比对如果检查出不匹配数据，需要在调度方案执行界面给出提示*/
							String sCheckResult = guess.checkbgApp(parameter);
							
							if (!sCheckResult.equals("0"))
							{
								runStatus
								.appendSchRunDesc("\r\n    ~~~估值增值与余额表核对不一致!" +
										"详细信息请到估值增值科目核对表中查看！！___\r\n");
							}
						/**End 20130625 modified by liubo.Bug #8297.QDV4广发基金2013年6月17日01_B */
							
						}
						/**end shashijie 2012-9-26 BUG */
						
					} else if (sRepType[j].equalsIgnoreCase("navdata")) {
						CtlNavRep nav = new CtlNavRep();
						nav.setYssPub(pub);
						nav.setDDate(dCurDate);
						nav.setPortCode(sPort);
						nav.setIsSelect(false);
						nav.setInvMgrCode("total");
						//---add by songjie 2012.09.24 STORY #2344 QDV4建行2012年3月2日05_A start---//
						nav.logSumCode = logSumCode;//汇总日志编号
						nav.logOper = this.logOper;//设置日志实例
						nav.setFunName("navdata");//设置功能调用代码
						//---add by songjie 2012.09.24 STORY #2344 QDV4建行2012年3月2日05_A end---//
						nav.invokeOperMothed();
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("处理日终报表类出错！", e);
		}
		return reStr;
	}

	/**shashijie 2012-9-26 BUG 5814 判断通参中是否设置不检查
	 * @return 若设置了不检查返回 false*/
	private boolean isPubPara(String portCode) throws Exception {
		boolean falg = true;
		ResultSet rs = null;
		try {
			ParaWithPubBean pubBean = new ParaWithPubBean();
			pubBean.setYssPub(pub);
			rs = pubBean.getResultSetByLike("ParaGuessValueCheck",
					"ParaGuessValue","ctlGrpGVCheck","selPort",portCode+"|%",
					"cboCheck");
			if (rs!=null && rs.next()) {
				String FCtlValue = rs.getString("FCtlValue");
				if (FCtlValue.split(",")[0].equals("1")) {
					falg = false;
				} else {
					falg = true;
				}
			}
		} catch (Exception e) {
			falg = true;
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return falg;
	}
	

	/**
	 * 获取日终估值和估值检查的查询字符串（类型名称、类型代码） 实际上是通过词汇代码查询词汇名称
	 * 
	 * @param sVocCode
	 *            String：词汇代码
	 * @param sType
	 *            String：判断是估值还是估值检查
	 * @throws YssException
	 * @return String
	 */
	public String getValuationRequestStr(String sVocCode, String sType)
			throws YssException {
		ResultSet rs = null;
		String sVocTypeCode = "";
		String strSql = "";
		String strCode = "";
		String strName = "";
		String sign = "false"; // add by wangzuochun 2010.08.14 
		//add by songjie 2012.12.19 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
		boolean sign1 = false; 
		try {
			sVocCode = sVocCode.replaceAll(",", "','");
			sVocCode = "'" + sVocCode + "'";
			if (sType.equalsIgnoreCase("valcheck")) {
				sVocTypeCode = dbl.sqlString("val_check");
			} else if (sType.equalsIgnoreCase("Valuation")) {
				sVocTypeCode = dbl.sqlString("val_content");
			} else if (sType.equalsIgnoreCase("business")) {// 业务处理 MS01179
															// QDV4赢时胜（深圳）2010年05月25日01_A
															// panjunfang add
															// 20100525
				sVocTypeCode = dbl.sqlString("val_business");
			}
			strSql = "SELECT FVocCode, FVocName FROM Tb_Fun_Vocabulary "
					+ "WHERE FVocTypeCode = " + sVocTypeCode
					+ "AND FVocCode IN (" + sVocCode + ")";
			rs = dbl.openResultSet(strSql);
			//------ modify by wangzuochun 2010.08.14  MS01528    调度方案执行第一次数据有误，第二次才正确    QDV4深圳赢时胜2010年8月2日01_B    
			while (rs.next()) {
				if ("IncomeFX".equals(rs.getString("FVocCode")) && "综合损益－汇兑损益".equals(rs.getString("FVocName"))){
					sign = "true";
				}
				//---add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
				else if("IndexFutruesMV".equals(rs.getString("FVocCode")) && "期货-期货浮动盈亏".equals(rs.getString("FVocName"))){
					sign1 = true;
				}
				//---add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
				else{
					strCode += rs.getString("FVocCode") + ",";
					strName += rs.getString("FVocName") + ",";
				}
			}
			if (sign == "true"){
				strCode = strCode + "IncomeFX" + ",";
				strName = strName + "综合损益-汇兑损益" + ",";
			}
			//---add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
			if(sign1){
				strCode = "IndexFutruesMV" + "," + strCode;
				strName = "期货-期货浮动盈亏" + "," + strName;
			}
			//---add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
			//---------------MS01528-----------------// 
			if (strCode.length() > 0) {
				strCode = strCode.substring(0, strCode.length() - 1);
				strName = strName.substring(0, strName.length() - 1);
			}
			return strCode + "\t" + strName;
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs); // close rs 20080716 sj
		}
	}
	
	/**20110705 Added by liubo.Story #1145
	 * 以前台传入的组合为单位读取以“ScheduleLog_”+组合群名+“_”+组合名的LOG文件，并将日志记载的内容传回显示在前台的状态文本框上，若查无该文件，则返回查无该文件
	 * 
	 * @throws YssException
	 */
	
	public void showScheduleLog() throws YssException
	{
		String sLog[];
		String sLogPath = System.getProperty("logsDir") + (System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\") + "QDII_ScheduleLog";
		
		for (int iPort = 0; iPort < this.arrPort.length; iPort++)
		{
			String sLogName = "ScheduleLog" + "_" + pub.getPrefixTB() + "_" + this.arrPort[iPort] + "_Time-";
			
			sLog = YssUtil.ReadScheduleLog(sLogPath, sLogName).split("/n");

			runStatus.appendSchRunDesc("\r\n");		
			runStatus.appendSchRunDesc("组合【" + this.arrPort[iPort] + "】");
			runStatus.appendSchRunDesc("-----------------------------------------------------"); 
			
			for (int i = 0;i < sLog.length;i++)
			{
				runStatus.appendSchRunDesc((sLog[i].startsWith("\t")?sLog[i].substring(1):sLog[i]));
			}
		}
	}

	public String getAssetGroupCode() {
		return assetGroupCode;
	}

	public void setAssetGroupCode(String assetGroupCode) {
		this.assetGroupCode = assetGroupCode;
	}

	public String getsPortCodes() {
		return sPortCodes;
	}

	public void setsPortCodes(String sPortCodes) {
		this.sPortCodes = sPortCodes;
	}

	/**判断组合群下是否有该组合,有则返回true
	 * @param portcode 组合代码
	 * @param protCtlCode 组合群
	 * @return
	 * @author shashijie ,2011-9-23 , STORY 1565
	 * @modified 
	 */
	private boolean isHavePortCode(String portcode,String protCtlCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = " select * From "+pub.yssGetTableName("Tb_Para_Portfolio")+" where FPortCode = "+
				dbl.sqlString(portcode)+" and FAssetGroupCode = "+dbl.sqlString(protCtlCode);
			rs = dbl.openResultSet(strSql);
	        if (rs.next()) {
	        	return true;
	        }else {
				return false;
			}
		}catch (SQLException e) {
			throw new YssException("判断组合群下是否有该组合报错!", e);
		}finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	
	//added by liubo.Story #1916
	//根据前台传回的调度方案代码 + “>>>” + 组合群代码，解析出调度方案代码和组合群代码，返回给前台改调度方案代码所包含的类别代码。
	public String getProjectCodeDetail(String sRequest) throws YssException
	{
		String sReturn = "";
		String[] sQueryResult = null;
		String[] sCusConfigDetail = null;
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
				sQueryResult = sRequest.split(",");
				
				for(int i = 0; i < sQueryResult.length; i++)
				{
					sCusConfigDetail = sQueryResult[i].split(">>>");
					sReturn = sReturn + sCusConfigDetail[0] + ",";
				}

			return (sReturn.length() > 0 ? sReturn.substring(0, sReturn.length() - 1) : "");
			
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
	}

}
