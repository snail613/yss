package com.yss.main.dayfinish;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Date;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.commeach.EachRateOper;
import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.cashmanage.CommandBean;
import com.yss.main.dao.*;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.operdata.InvestPayRecBean;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.linkInfo.BaseLinkInfoDeal;
import com.yss.main.operdeal.opermanage.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.operdeal.report.navrep.*;
import com.yss.main.operdeal.stgstat.*;
import com.yss.main.operdeal.valcheck.*;


//MS00006-QDV4.1赢时胜上海2009年2月1日05_A -QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29
import com.yss.main.operdeal.valuation.*;
import com.yss.main.taoperation.TaCashAccLinkBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.pojo.sys.YssStatus;
import com.yss.util.*;
import com.yss.main.operdeal.income.stat.*;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.parasetting.InvestPayBean;
import com.yss.main.parasetting.PeriodBean;
import com.yss.main.operdeal.invest.InvestCfgFormula;
import com.yss.manager.InvestPayAdimin;
import com.yss.manager.TAStorageAdmin;

/**
 *
 * <p>Title:处理资产估值 </p>
 *
 * <p>Description: 本类为处理资产估值、检查、业务处理的基础类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech</p>
 *
 * <p>Modify: 增加业务处理的功能 sj add 20090612 MS00014:QDV4.1赢时胜（上海）2009年4月20日14_A</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ValuationBean
    extends BaseBean implements IYssConvert, IClientOperRequest
    //--- sj modified 20090803 --------------
    ,IOperManage{//增加一个接口，用以执行业务处理
    //---------------------------------------
    private java.util.Date startDate;   //起始日期
    private java.util.Date endDate;     //终止日期u
    
    //20110713 added by liubo.Story #1145
    //****************************
    private String sNeedLog = "false";	//是否需要生成日志。当“调度方案执行”窗体调用此类时，需要生成LOG
	private String sOperTime = "";		//操作时间
	private String sLogPath = "";		//日志路径
	//***************end***************
	
	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	public String logSumCode = "";//日志汇总编号 
	public boolean comeFromDD = false;//判断是否通过调度方案调用
	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	
    //20110713 added by liubo.Story #1145
    //****************************
    public String getsLogPath() {
		return sLogPath;
	}

	public void setsLogPath(String sLogPath) {
		this.sLogPath = sLogPath;
	}

	public String getsOperTime() {
		return sOperTime;
	}

	public void setsOperTime(String sOperTime) {
		this.sOperTime = sOperTime;
	}

	public String getsNeedLog() {
		return sNeedLog;
	}

	public void setsNeedLog(String sNeedLog) {
		this.sNeedLog = sNeedLog;
	}
	//***************end***************
	

	private String portCodes = "";      //已选组合

    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    //--------------------------------------------------------------------------------

    private String valuationTypes = ""; //估值类型   在检查时作为检查类型
    private String valuationTypesName = ""; //估值类型名称
    private String sValCheckIsError = "";
	private boolean bETFValOnly = false;
    private String operManageTypes = "";        //因为操作类型
    private String operManageTypesName = "";    //因为操作类型名称
    //MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A sj ---------
    private boolean ifOperManager = true;//是否执行业务处理
    //--------------------------------------------------------

    protected HashMap hmValMethods;
	//edit by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A private 改为 public
	public SingleLogOper logOper;
	private int operType;
    public String getPortCodes() {
        return portCodes;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setValuationTypes(String valuationTypes) {
        this.valuationTypes = valuationTypes;
    }

    public void setValuationTypesName(String valuationTypesName) {
        this.valuationTypesName = valuationTypesName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getValuationTypes() {
        return valuationTypes;
    }

    public String getValuationTypesName() {
        return valuationTypesName;
    }

    public String getValCheckIsError() {
        return this.sValCheckIsError;
    }

    public ValuationBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     * 解析请求信息
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\t");
            this.startDate = YssFun.toDate(reqAry[0]);
            this.endDate = YssFun.toDate(reqAry[1]);
            this.portCodes = reqAry[2];
            this.valuationTypes = reqAry[3];
            this.valuationTypesName = reqAry[4];
            this.assetGroupCode = reqAry[5];
			if(reqAry[6].equals("true")){
                this.bETFValOnly = true;
            }
            this.operManageTypes = reqAry[7];       //业务类型
            this.operManageTypesName = reqAry[8];   //业务类型名称
            //--- sj modified 20090803 MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A-------------------------
            this.ifOperManager = new Boolean(reqAry[9]).booleanValue();
            //--------------------------------------------------
        } catch (Exception e) {
            throw new YssException("解析资产估值请求信息出错\r\n" + e.getMessage(), e);
        }
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 从后台加载出我们跨组合群的内容 资产估值做跨组合群检查
    public String checkRequest(String sType) throws YssException {
        String sAllGroup = "";                  //定义一个字符用来保存执行后的结果传到前台
        String sPrefixTB = pub.getPrefixTB();   //获取组合群表前缀
        String[] assetGroupCodes = this.assetGroupCode.split(YssCons.YSS_GROUPSPLITMARK);   //按组合群的解析符解析组合群代码
        String[] strPortCodes = this.portCodes.split(YssCons.YSS_GROUPSPLITMARK);           //按组合群的解析符解析组合代码
        try {
            //遍历组合群进行处理
            for (int i = 0; i < assetGroupCodes.length; i++) {
                this.assetGroupCode = assetGroupCodes[i];   //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode);       //修改公共变量的当前组合群代码
                this.portCodes = strPortCodes[i];           //得到一个组合群下的组合代码
                sAllGroup = this.getListViewGroupData5(sType);  //调用以前的执行方法
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            pub.setPrefixTB(sPrefixTB); //还原公共变的里的组合群代码
        }
        return sAllGroup; //把结果返回到前台进行显示
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 从后台加载出我们跨组合群的内容 资产估值跨组合群做统计
    public String doOperation(String sType) throws YssException {
        String sAllGroup = "";                  //定义一个字符用来保存执行后的结果传到前台
        String sPrefixTB = pub.getPrefixTB();   //保存当前的组合群代码
        String[] assetGroupCodes = this.assetGroupCode.split(YssCons.YSS_GROUPSPLITMARK);   //按组合群的解析符解析组合群代码
        String[] strPortCodes = this.portCodes.split(YssCons.YSS_GROUPSPLITMARK);           //按组合群的解析符解析组合代码
        try {
            if(bETFValOnly){//只进行ETF资产估值处理
                for (int i = 0; i < assetGroupCodes.length; i++) { //循环遍历每一个组合群
                    this.assetGroupCode = assetGroupCodes[i]; //得到一个组合群代码
                    pub.setPrefixTB(this.assetGroupCode); //修改公共变量的当前组合群代码
                    this.portCodes = strPortCodes[i]; //得到一个组合群下的组合代码
                    sAllGroup = this.doOperETFVal(sType); //调用单独的ETF资产估值处理方法
                }
            }else{//普通资产估值处理
                for (int i = 0; i < assetGroupCodes.length; i++) { //循环遍历每一个组合群
                    this.assetGroupCode = assetGroupCodes[i]; //得到一个组合群代码
                    pub.setPrefixTB(this.assetGroupCode); //修改公共变量的当前组合群代码
                    this.portCodes = strPortCodes[i]; //得到一个组合群下的组合代码
                    sAllGroup = this.doGroupOperation(sType); //调用以前的执行方法
                }
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            pub.setPrefixTB(sPrefixTB); //还原公共变的里的组合群代码
        }
        return sAllGroup; //把结果返回到前台进行显示
    }

    /**
     * 进行估值检查的入口
     */
    public String doValCheck() throws Exception {
        String[] checkType = null;      //用来保存检查类型的数组
        String[] strPre = null;         //用来保存需要检查的组合编号的数组
        String[] checkTypeName = null;  //用来保存检查类型说明的数组
        BaseValCheck check = null;      //估值检查的基类
        int iDays = 0;                  //保存需要检查的天数
        java.util.Date dTheDay = null;  //传入 doCheck 方法中的检查的当前日期
        String sReInfo;                 //如果检查完成则返回 “true”
        try {
            checkType = this.valuationTypes.split(",");
            strPre = this.portCodes.split(",");
            checkTypeName = this.valuationTypesName.split(",");
            iDays = YssFun.dateDiff(this.startDate, this.endDate);
            dTheDay = this.startDate;
            //---------------检查之前先统计证券和现金库存-----------------//、
            runStatus.appendValCheckRunDesc("\r\n    开始检查组合群【" +
                                            pub.getPrefixTB() +
                                            "】... ...");

            runStatus.appendValCheckRunDesc("\r\n开始统计证券库存... ...");
            BaseStgStatDeal secstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("SecurityStorage");
            secstgstat.setYssPub(pub);
            secstgstat.stroageStat(this.startDate, this.endDate,
                                   operSql.sqlCodes(portCodes));
            runStatus.appendValCheckRunDesc("\r\n统计证券统计完毕！");
            runStatus.appendValCheckRunDesc("\r\n开始统计现金库存... ...");
            secstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("CashStorage");
            secstgstat.setYssPub(pub);
            secstgstat.stroageStat(this.startDate, this.endDate,
                                   operSql.sqlCodes(portCodes));

            runStatus.appendValCheckRunDesc("\r\n统计现金统计完毕！");
            //--------------------------------------------------------//
            //做日期循环
           int iRingTypes =0, iRingPre=0,iRingDays=0;	//日终处理--日志报表，变量提出出来方便CATCH edited by zhouxiang 2010.12.14
           try{
        	for (iRingDays = 0; iRingDays <= iDays; iRingDays++) {
                runStatus.appendValCheckRunDesc("\r\n开始检查【" + YssFun.formatDate(dTheDay) +"】日数据... ...");
                //做组合循环
                for (iRingPre = 0; iRingPre < strPre.length; iRingPre++) {
                    runStatus.appendValCheckRunDesc("\r\n    开始检查组合【" + strPre[iRingPre] + "】... ...");
                    //作检查类型循环
                    for (iRingTypes= 0; iRingTypes < checkType.length;
                         iRingTypes++) {
                        runStatus.appendValCheckRunDesc("\r\n        开始进行检查【" + checkTypeName[iRingTypes] + "】... ...");
                        check = (BaseValCheck) pub.getOperDealCtx().getBean("Check" + checkType[iRingTypes]);
                        check.setYssPub(pub);
                        check.setYssRunStatus(runStatus);
                        //20110713 added by liubo.Story #1145
                        //***************************
                        check.setsNeedLog(this.sNeedLog);				//是否需要生成日志。默认为FALSE
                        check.setsAssetGroupCode(pub.getPrefixTB());	//做合群代码
                        check.setsPortCode(strPre[iRingPre]);			//组合代码
                        check.setsLogPath(sLogPath);					//日志生成路径
                        check.setsOperTime(sOperTime);					//操作时间
                        //*************end******************
                        check.doCheck(dTheDay, strPre[iRingPre]);
                        if (check.getIsError().equalsIgnoreCase("false")) {
                            this.sValCheckIsError = check.getIsError();
                        }
                        //add by zhouxiang 2010.12.9 日终处理--日志报表
                        //end by zhouxiang 2010.12.9 日终处理--日志报表
                        runStatus.appendValCheckRunDesc("\r\n        检查【" + checkTypeName[iRingTypes] + "】检查完毕！");
                    }
                    runStatus.appendValCheckRunDesc("\r\n    组合【" + strPre[iRingPre] + "】检查完毕！");
                    runStatus.appendValCheckRunDesc("\r\n    ****************************************");
                }
                runStatus.appendValCheckRunDesc("\r\n    组合群【" + pub.getPrefixTB() + "】检查完毕！");
                runStatus.appendValCheckRunDesc("\r\n★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
                dTheDay = YssFun.addDay(dTheDay, 1);
              }
            }catch(Exception ex){//日终处理--日志报表，变量提出出来方便CATCH edited by zhouxiang 2010.12.14
            	throw new YssException(ex.getMessage(),ex);
            }
            sReInfo = "true";
            return sReInfo;
        } catch (Exception e) {
            runStatus.appendValRunDesc("检查失败" + e.getMessage());
            throw new YssException("估值检查失败！\n", e); //by caocheng 2009.02.04 MS00004 QDV4.1-2009.2.1_09A 采用新异常处理机制后不用e.getMessage()
        }
    }

    /**
     * 净值统计。sj edit 20080627
     * @throws YssException
     */
    private void stgNetValue(String sportCode, java.util.Date dDate) throws
        YssException {
        CtlNavRep navrep = null;
        try {
        	
            navrep = new CtlNavRep();
            navrep.setPortCode(sportCode);
            navrep.setInvMgrCode("total"); //直接设置为total类型的投资经理。sj edit 20080627
            navrep.setDDate(dDate);
            navrep.setIsSelect(false);
            navrep.setYssPub(pub);
            //add by songjie 2012.10.17 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
            navrep.comeFromDD = true;//表示通过调度方案调用
            navrep.invokeOperMothed();
            
            stgNetValueByManager(sportCode,dDate);
        } catch (Exception e) {
            throw new YssException("按统计净值失败!\n", e);
        }
    }

    /******************************************************
     * MS01702 QDV4太平2010年09月07日01_AB 
     *  add by jiangshichao 2010.09.25
     *  
     *  运营应收设置如果设置了按月末计提，则在月末统计净值时按投资经理进行统计
     * @param sportCode
     * @param dDate
     * @throws YssException
     */
    private void stgNetValueByManager(String sportCode, java.util.Date dDate) throws YssException {
    	CtlNavRep navrep = null;
        ResultSet rs = null;
    	 try { 
             if(YssFun.formatDate(YssFun.addDay(dDate,1), "yyyy-MM-dd").equalsIgnoreCase(YssFun.formatDate(YssFun.addMonth(endDate, 1),"yyyy-MM")+"-01")){
            	//edit by licai 20110302 BUG #1171 月末进行资产估值后发现表tb_XXX_data_navdata中合计项数据重复 
             	String sql = "select fanalysiscode1 from "+pub.yssGetTableName("tb_para_investpay")+" where FpayOrigin=2 and FcheckState=1 and fportcode ="+dbl.sqlString(sportCode);
             	//edit by licai 20110302 BUG #1171 =====================================================end
             	rs = dbl.openResultSet(sql);
             	while(rs.next()){
             		 navrep = new CtlNavRep();
                     navrep.setPortCode(sportCode);
                     navrep.setInvMgrCode(rs.getString("fanalysiscode1")); //直接设置为total类型的投资经理。
                     navrep.setDDate(dDate);
                     navrep.setIsSelect(false);
                     navrep.setYssPub(pub);
                     navrep.invokeOperMothed();
             	}
             }
    	 } catch (Exception e) {
             throw new YssException("按统计净值失败!\n", e);
         }finally{
         	dbl.closeResultSetFinal(rs);
         }
    }
    
    /**
     * 从后台加载出我们跨组合群的内容
     * 修改人：fanghaoln
     * 修改人时间:20090512
     * BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String getListViewGroupData5(String sType) throws YssException {
        String sReInfo = "";
        try {
            if (sType.equalsIgnoreCase("valcheck")) {
                sReInfo = this.doValCheck(); // 从后台加载出我们跨组合群的内容
            }
        } catch (Exception e) {
            throw new YssException("", e);
        }
        return sReInfo; //返回执行后的结果集
    }


    /**
     * 业务处理的单独方法
     * @param operManageAry String[]
     * @param operManagerNameAry String[]
     * @param sPortCode String
     * @param dDate Date
     * @throws YssException
     */
	 //edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
    private void doManagerDeal(String[] operManageAry, String[] operManagerNameAry, String sPortCode, java.util.Date dDate,String logSumCode) throws YssException {
        String manageType = "";
        String portCode = "";
        BaseOperManage opermanage = null;
        runStatus.appendValRunDesc("    开始组合【" + sPortCode + "】的业务处理... ...\r\n");
        int types = 0;
 	    //---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
 	    String logInfo = null;//记录runStatus 相关内容
 	    Date logStartTime = null;//业务子项开始时间
     	//---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try{
        	//--- edit by songjie 2012.08.20 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	if(logOper == null){//添加空指针判断
        		logOper = SingleLogOper.getInstance();
        	}
        	this.setFunName("businessdeal");//设置功能调用代码
            operType = 14;
            //--- edit by songjie 2012.08.20 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            
            for (types = 0; types < operManageAry.length; types++) {
            	//---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            	logInfo = "        开始业务处理类型【" + operManagerNameAry[types] + "】... ...\r\n";
            	logStartTime = new Date();
            	//---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            	runStatus.appendValRunDesc("        开始业务处理类型【" + operManagerNameAry[types] + "】... ...\r\n");
            	manageType = operManageAry[types];
            	portCode = sPortCode;
            	opermanage = (BaseOperManage) pub.getOperDealCtx().getBean(manageType);
            	opermanage.setYssPub(pub);
            	opermanage.initOperManageInfo(dDate, portCode);
            	//add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
            	opermanage.comeFromBsnDeal = true;
            	opermanage.doOpertion();
            	//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A


            	//20130407 added by liubo.Story #3714
            	//将债券派息数据返回给前台
            	//runStatus.appendSchRunDesc表示返回给调度方案执行界面，runStatus.appendValRunDesc表示返回给业务处理和权益处理
            	//在调试过程中发现在进行业务处理时，runStatus的keyWord也是SchRun，原因不明，因此直接往两个界面的一起返回
            	//=========================================
    			if (!YssCons.sInterestInfoCollected.trim().equals(""))
    			{
    				runStatus.appendSchRunDesc("\r\n" + YssCons.sInterestInfoCollected + "\r\n", "\r\n");
    				
    				runStatus.appendValRunDesc(YssCons.sInterestInfoCollected + "\r\n");
    				
    				logInfo += YssCons.sInterestInfoCollected + "\r\n\r\n";
    				
    				YssCons.sInterestInfoCollected = "";
    				
    				logOper.setFixInterestCheck(1);
    				
    			}
            	//=====================end====================

            	
//                runStatus.appendValRunDesc(YssCons.sInterestInfoCollected);
            	logInfo += "        业务处理类型【" + operManagerNameAry[types] + "】统计完成"+opermanage.getMsg()+" \r\n";
            	runStatus.appendValRunDesc("        业务处理类型【" + operManagerNameAry[types] + "】统计完成"+opermanage.getMsg()+" \r\n");//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409
            	
            	//----------add by zhouxiang   2010.12.14 日终处理 日志报表-----
            	//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
            	//edit by songjie 2012.11.20 添加非空判断
            	if(logOper != null){
            		logOper.setDayFinishIData(this, operType,operManageAry[types], pub, false,sPortCode, 
            			this.startDate,dDate, this.endDate, logInfo, logStartTime,
            			logSumCode,new Date());
            	}
            	//----------end by zhouxiang   2010.12.14 日终处理 日志报表-----
            	
                logOper.setFixInterestCheck(0);	//20130417 added by liubo.Story #3528.还原SingleLogOper的iFixInterestCheck的值为默认值
            }
            
        runStatus.appendValRunDesc("    组合【" + sPortCode + "】业务处理完成\r\n");
        
        
        }catch(Exception e){
        	//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, operType, operManageAry[types], 
        				pub, true,sPortCode, this.startDate, dDate, this.endDate, 
        				//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
        				(logInfo + " \r\n 处理失败  \r\n" + e.getMessage())//处理日志信息 除去特殊符号
        				.replaceAll("\t", "").replaceAll("&", "").replace("\f\f", ""), 
        				//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
        				logStartTime, logSumCode, new Date()); 
        		}
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	//add by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	finally{//添加 finally 保证可以抛出异常
        		throw new YssException("业务处理出现异常！", e);
        	}
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        }
        //20130407 added by liubo.Story #3714
        //上次的债券派息处理完成之后，需要清空一遍sInterestInfoCollected变量
        //避免第二次执行的时候会把上次的债券信息也显示出来
        //===========================
        finally
        {
        	YssCons.sInterestInfoCollected = "";
            logOper.setFixInterestCheck(0);
        }
        //============end===============
    }

    /**
     * 从后台加载出我们跨组合群统计的内容
     * 修改人：fanghaoln
     * 修改人时间:20090512
     * BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String doGroupOperation(String sType) throws YssException {
        String sReInfo = "";
        ArrayList alMethods = null;
        HashMap hm = null;
        int operType = 0;
        int iDays = 0;
        java.util.Date dDate = null;
        String[] portAry = null;
        String[] valTypeAry = null;
        String[] valTypesNameAry = null;
        //--- sj add 20090612 MS00014 ------------
        String[] operManageAry = null;
        String[] operManagerNameAry = null;
        String manageType = "";
        BaseOperManage opermanage = null;
        //----QDV4.1赢时胜（上海）2009年4月20日14_A---
        String portCode = "";
        String valType = "";
        BaseStgStatDeal recStgStat = null;
        BaseStgStatDeal secstgstat = null;
        BaseStgStatDeal cashstgstat = null;
        /*added by yeshenghong 2013-6-9 Story 3759 */
        TAStorageAdmin taAdmin = null;
		/*end by yeshenghong 2013-6-9 Story 3759 */
        BaseValDeal valuation = null;
        HashMap hmValRate;
        HashMap hmValPrice;
		//--- edit by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        if(logOper == null){//添加空指针判断
        	logOper = SingleLogOper.getInstance();//日终处理--日志报表 edited by zhouxiang 2010.12.14
        }
		//--- edit by songjie 2012.10.12 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        boolean isReCalCost = false;    //是否重新计算库存成本。sj edit 20080927
        String reNavType = "";          //使用的净值表的类型.sj edit 20080927
        HashMap hmETFPorts = new HashMap();
        String strETFStatType = "";
        //add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A
        HashMap hmFeeSet = null;
        //add by fangjiang 2011.12.23 STORY #2020
        TaTradeBean ta = new TaTradeBean(); 
        ta.setYssPub(this.pub);
        String info = "";
        //---------end STORY #2020--------------
        
  	    //---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
  	    String logInfo = null;//记录runStatus 相关内容
  	    Date logStartTime = null;//业务子项开始时间
        DayFinishLogBean df = new DayFinishLogBean();
		//add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A set pub
        df.setYssPub(pub);
        boolean isError = false;//判断是否报错
      	//---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
        	//---add by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
            if(!this.comeFromDD){//若不是调度方案执行调用
            	if(logSumCode.trim().length() == 0){
					//获取汇总日志编号
            		logSumCode = df.getLogSumCodes();
            	}
            }
            //---add by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        	
        	//估值前先清除全局变量中的编号 多线程并行并发优化处理 by leeyu 20100507 合并太平版本
    	  	if(!YssGlobal.clearValNums){
        		  YssGlobal.clearValNums =true;
        		  YssGlobal.hmCashRecNums.clear();
        		  YssGlobal.hmSecRecNums.clear();    		  
        	 }
    		 //多线程并行并发优化处理 by leeyu 20100507 合并太平版本
            //MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29
            //用于判断技能同一个组合群组合下是否有与当前用户相同的操作类型,有的话就跳出提示信息
            YssGlobal.judgeIfUniqueUserAndPort(YssCons.YSS_OPER_VALUATION, portCodes, pub);
            hmValMethods = new HashMap();
            hmValPrice = new HashMap();
            hmETFPorts = this.getETFPort(this.portCodes);//获取ETF组合代码
            valTypeAry = this.valuationTypes.split(",");

            portAry = this.portCodes.split(",");

            valTypesNameAry = valuationTypesName.split(",");

            //------------- sj add 20090612 MS00014  业务类型 ----------------//
            operManageAry = operManageTypes.split(",");
            operManagerNameAry = operManageTypesName.split(",");
            //-----End MS00014 MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A---//

            iDays = YssFun.dateDiff(this.startDate, this.endDate);
            dDate = this.startDate;
            operType = 8;
            //-----------------------------通过通用参数的设置来判断相应的操作方式。--------//
            CtlPubPara pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            String reCalCost = pubpara.getReCalCost();
            if (reCalCost.equalsIgnoreCase("no")) { //是否计算库存成本。
                isReCalCost = false;
            } else { //其它情况默认为舍入2位.
                isReCalCost = true;
            }
            
            runStatus.appendValRunDesc("\r\n    开始统计组合群【" + pub.getPrefixTB() + "】... ...");

            //-------------------------------------------------------------------------//
            
            //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            df.setYssPub(pub);
            if(this.ifOperManager){
            	operType = 14;
            }
            

            //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            
            for (int i = 0; i <= iDays; i++) {
                runStatus.appendValRunDesc("【" + YssFun.formatDate(dDate) + "】开始统计... ...\r\n");
                //将当日行情放入临时表，可提高估值时的SQL执行语句的效率 合并版本
                DataPrepareBeforeVal prepareBean=new DataPrepareBeforeVal();
            	prepareBean.setYssPub(pub);
                String tmpMVTable = prepareBean.createMarketValueTable(dDate);//系统优化处理，生成行情临时表 by leeyu 20100524 合并太平版本
                for (int j = 0; j < portAry.length; j++) {
                	// add by fangjiang 2011.12.23 STORY #2020 
                	if(ta.checkTaTradeInfo(portAry[j], dDate)){
                		info += "组合【" + portAry[j] + "】在【" + YssFun.formatDate(dDate) + "】没有录入TA交易数据，请确认！... ...\r\n";
                	}
                	//------------end STORY #2020 ----------
                    //--- sj add 20090612 MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A  在资产估值之前进行。-//
                    if(this.ifOperManager){
                    	//edit by songjie 2012.08.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
                        doManagerDeal(operManageAry, operManagerNameAry, portAry[j], dDate, logSumCode); //调整业务处理的方式，将其进行单独处理。
                    }
                    //----------------------------------------------------------------------------------//
                    if (hmETFPorts.containsKey(portAry[j])) {//如果当前组合为ETF组合，则在进行普通估值前先处理ETF资产估值                                            
                    	//获取补票方式
                		ETFParamSetAdmin etfParamAdmin = new ETFParamSetAdmin();
                		etfParamAdmin.setYssPub(pub);
                		ETFParamSetBean etfParamBean = new ETFParamSetBean();
                		HashMap hmPara = etfParamAdmin.getETFParamInfo(portAry[j]);
                		etfParamBean = (ETFParamSetBean)hmPara.get(portAry[j]);
                		if(etfParamBean == null){
                			throw new YssException("组合【" + portAry[j] + "】对应的ETF参数设置不存在或未审核！");
                		}
                		strETFStatType = etfParamBean.getSupplyMode();
                		if(!strETFStatType.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_ONE) && 
                				!strETFStatType.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) &&
                				!strETFStatType.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)){//T日确认，etf净值表不统计当日申赎
                			doETFValuation(valTypeAry,valTypesNameAry,portAry[j],dDate,tmpMVTable,strETFStatType);//调用ETF资产估值方法 panjunfang modify 20101216 QDV4华宝2010年12月16日01_B
                            updateETFTradeData(portAry[j],dDate);//更新当日TA交易数据
                            creCashInsBalRP(portAry[j], dDate);//生成现金替代和现金差额的应收应付
                		}else{//T+1日确认（易方达、华夏）
                			if(strETFStatType.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)
                					|| strETFStatType.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)){
                				updateETFTradeData(portAry[j],dDate);//更新当日TA交易数据
                			}            			
                			creCashInsBalRP(portAry[j], dDate);//生成现金替代和现金差额的应收应付
                			doETFValuation(valTypeAry,valTypesNameAry,portAry[j],dDate,tmpMVTable,strETFStatType);//调用ETF资产估值方法 panjunfang modify 20101216 QDV4华宝2010年12月16日01_B
                		}  
                    }

                    secstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("SecurityStorage");
                    //add by songjie 2011.12.31 BUG 3491 QDV4海富通2011年12月22日01_B
                    //用于判断是在做什么操作时做的证券库存统计
                    secstgstat.setStgFrom("Valuation");
                    secstgstat.setYssPub(pub);
                    secstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(portAry[j]));

                    if (isReCalCost) {
                        secstgstat.adjustStorageCost(dDate, dDate,
                                                     operSql.sqlCodes(portAry[j])); //调整库存成本。sj edit 20080702
                    }
                    //add by songjie 2011.12.31 BUG 3491 QDV4海富通2011年12月22日01_B
                    secstgstat.setStgFrom("");
                    /*=========取消当日现金库存统计 sunkey 20081124 BugID:MS00013========================
                                         ===========因为下面进行库存统计的时候要进行资金调拨，如果这里处理的现金库存的统计，则资金调拨的资金将不被处理=================
                                         ===========撤销取消操作 ，在生成现金应收应付的时候进行统计 20081126 sunkey BugID:MS00013*/
                    cashstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("CashStorage");
                    cashstgstat.setYssPub(pub);
                    cashstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(portAry[j]));

                    hmValRate = new HashMap();
                    //估值类别业务处理
                    int k=0;					//日终处理--日志报表，放在try后面方便CATCH edited by zhouxiang 2010.12.14
                    //--start--add by zhouwei 20120404 根据证券管理费设置通参，计算证券的管理费
              	    stgSecManageFee(portAry[j],dDate);
              	    //--------end-----------------
                    /**shashijie 2012-12-6 无BUG与需求编号,资产估值后台报错但是前台未显示报错,导致许多问题,这里去掉try catch,潘君方要求 */
              	    //edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A
					try{
					/**end shashijie 2012-12-6 */
                    for (k=0; k < valTypeAry.length; k++) {
                    	//add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    	logStartTime = new Date();//开始时间
                    	
                    	//---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    	logInfo = "        开始统计业务类型【" + valTypesNameAry[k] + "】... ...\r\n";
                    	
                    	//---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    	
                        runStatus.appendValRunDesc("        开始统计业务类型【" +
                                                   valTypesNameAry[k] +
                                                   "】... ...\r\n");
                        valType = valTypeAry[k];
                        portCode = portAry[j];

                        valuation = (BaseValDeal) pub.getOperDealCtx().getBean(valType);

                        valuation.setYssPub(pub);

                        valuation.initValuation(dDate, portCode, valType, hmValRate, hmValPrice);
                        valuation.setTmpMarketValueTable(tmpMVTable);//将原设置的行情临时表设置到估值中去 by leeyu 20100524 合并太平版本
                        if (!this.hmValMethods.containsKey(portCode)) {
                            alMethods = valuation.getValuationMethods();
                            this.hmValMethods.put(portCode, alMethods);
                        }
                        valuation.setIsETFVal(false);
                        hm = valuation.getValuationCats( (ArrayList)this.hmValMethods.get(portCode));
                        sReInfo = valuation.saveValuationCats(hm).trim();
                        if(valTypesNameAry.length > k){
                        	//add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                        	logInfo += "        业务类型【" + valTypesNameAry[k] + "】统计完成\r\n";
                        	runStatus.appendValRunDesc("        业务类型【" + valTypesNameAry[k] + "】统计完成\r\n");
                        }
                        
                        //add by zhouxiang 2010.12.14 日终处理--日志报表
                        //edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                		//edit by songjie 2012.11.20 添加非空判断
                		if(logOper != null){
                			logOper.setDayFinishIData(this, 8,valTypeAry[k], pub, false, portAry[j], 
                        		this.startDate,dDate,this.endDate,
                        		logInfo,logStartTime,logSumCode, new Date());
                		}
                        //end by zhouxiang 2010.12.14 日终处理--日志报表
                    	}
                    /**shashijie 2012-12-6 无BUG与需求编号,资产估值后台报错但是前台未显示报错,导致许多问题,这里去掉try catch,潘君方要求 */
                    //---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
					}catch(Exception e){
                    	//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    	try{
                        	isError = true;//赋值为报错
                    		//edit by songjie 2012.11.20 添加非空判断
                    		if(logOper != null){
                    			logOper.setDayFinishIData(this, 8,valTypeAry[k], pub, true, portAry[j], 
                    				this.startDate,dDate,this.endDate,
                    				
                    				(logInfo + " \r\n 估值失败  \r\n " + e.getMessage())
                    				.replaceAll("\t", "").replaceAll("&", "").replace("\f\f", ""),
                    				
                    				logStartTime,logSumCode, new Date());
                    		}
                    	}catch(Exception ex){
                    		ex.printStackTrace();
                    	}finally{
                    		throw new YssException(e.getMessage(),e);
                    	}
                    	//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    }
					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                    /**end shashijie 2012-12-6 无BUG与需求编号*/
                    
                    valuation.insertValMktPrice();
                    valuation.insertValRate();

                    valuation.clearValPrice();
                    runStatus.appendValRunDesc("    开始统计【" + YssFun.formatDate(dDate) + "】库存... ...\r\n");
                    recStgStat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("CashPayRec");
                    recStgStat.setYssPub(pub);         
                    if(strETFStatType.length() > 0){
                    	recStgStat.setStrETFStatType(strETFStatType);
                    }
                    recStgStat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));

                    recStgStat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("SecRecPay");
                    recStgStat.setYssPub(pub);
                    recStgStat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));
                    runStatus.appendValRunDesc("    统计【" + YssFun.formatDate(dDate) + "】库存完成\r\n");

                    //加了一个TA统计库存
                    /**份额折算，删除Ta拆分数据  add by yeshenghong 20130509  story3759*/
                    if(this.judgeCommonFundShareDisCount(dDate, portCode)||this.judgeLeverGradeFundShareDisCount(dDate, portCode))
                    {
                    	this.deleteFundShareData(dDate, portCode);//重复估值时先删除数据
                    }
                    /** add by yeshenghong 20130509  story3759 -----end----- */
                    recStgStat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("TAStorage");
                    recStgStat.setYssPub(pub);
                    recStgStat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));
                    runStatus.appendValRunDesc("    统计【" + YssFun.formatDate(dDate) + "】库存完成\r\n");

                    //add by zhangfa 20100814 MS01446  证券变更业务,清除在证券库存统计时生成的新证券的T-1的库存数据
              	    emptyStock(dDate, portCodes); //modify by zhouwei 20120428 bug4367 //modify huangqirong 2012-07-06 bug #4940
              	    //---------------------------------------------------------------               	   
                    runStatus.appendValRunDesc("    开始统计【" + YssFun.formatDate(dDate) + "】净值... ...\r\n");
                    //------按新净值表的统计方法统计,并将相应的数据存入资产净值表中. 20080401 edit sj-----
                    stgNetValue(portCode, dDate);
                    //-----------------------------------------------------------------------------
                    stgMultiClassNetValue(portCode, dDate); //add by fangjiang 2011.10.26 STORY #1589
                    
                    
                    /**杠杆分级份额折算，产生Ta交易数据  add by yeshenghong 20130509  story3759*/
                    if(this.judgeCommonFundShareDisCount(dDate, portCode))
                    {
                    	taAdmin = new TAStorageAdmin();
                    	taAdmin.setYssPub(pub);
                    	taAdmin.createFundDivident(dDate, portCode);
                    	 //重新TA统计库存
	                    recStgStat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));
	                    //重新资产估值
	                    stgNetValue(portCode, dDate);
                    }
                    else if(this.judgeLeverGradeFundShareDisCount(dDate,portCode))
                    {
                    	taAdmin = new TAStorageAdmin();
                    	taAdmin.setYssPub(pub);
                    	taAdmin.leverGradeFundShareDisCount(dDate,portCode);//杠杆基金份额折算方法
	                    //重新TA统计库存
	                    recStgStat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));
	                    //重新资产估值
	                    stgNetValue(portCode, dDate);
	                    //重新计算杠杆分级的单位净值、累计净值等指标项
	                    stgMultiClassNetValue(portCode, dDate);
                    }
                    /** add by yeshenghong 20130509  story3759 -----end----- */
                    /** 现在【抵押物信息设置】界面已经去除 不需要提示 方法在此注释掉 modified by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务 */
                    //add by zhouxiang 2010.12.1 证券借贷 --资产估值--抵押物净值试算
                    //checkBLMortgageValue(runStatus,portCode,dDate);
                    //end by zhouxiang 2010.12.1 证券借贷 --资产估值--抵押物净值试算
                    /** -----end----- */
                    runStatus.appendValRunDesc("    统计【" + YssFun.formatDate(dDate) + "】净值完成\r\n");
                    //------------------------add by wuweiqi 两费进行计提   QDV4工银2010年12月22日01_A ---------------------------------------------//
                   //1.依据通用参数设置中设置的算法计提两费
                    portCode = portAry[j];
                    if(getAutoCharge(portCode))//判断是否要自动计提两费
                    { 
                      //1. 收益计提中未计提的两费重新计提
                      getDayIncomesFee(dDate,portCodes);
                      //2.重新统计运营库存
                      recStgStat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("InvestStorage");
                      recStgStat.setYssPub(pub);
                      recStgStat.stroageStat(dDate, dDate, operSql.sqlCodes(portCode));
                     //3.重新统计净值
                      stgNetValue(portCode, dDate);
                    }  
                    
                    //--------------------------end by wuweiqi 20110106---------------------------------------------//
                    runStatus.appendValRunDesc("    组合【" + portAry[j] + "】统计完成\r\n");
                    //---add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A---//
                    hmFeeSet = pubpara.getFeeSetOfCashCommand(portAry[j],dDate);//获取通用参数  - 费用划款指令设置
                    if(hmFeeSet != null && hmFeeSet.size() != 0){
                    	//根据费用划款指令设置生成相关费用的划款指令数据
                    	generateCashCommandOfFee(portAry[j],dDate,hmFeeSet);
                    }
                    //---add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A---//
                }
                runStatus.appendValRunDesc("【" + YssFun.formatDate(dDate) + "】统计完成\r\n");
                runStatus.appendValRunDesc("-----------------------------------------\r\n");
                dDate = YssFun.addDay(dDate, 1);
            }
            
            runStatus.appendValRunDesc("\r\n    组合群【" +pub.getPrefixTB() +"】统计完毕！");

            runStatus.appendValRunDesc("\r\n★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            //add by fangjiang 2011.12.23 STORY #2020 
            runStatus.appendValRunDesc("\r\n"); 
            runStatus.appendValRunDesc(info); 
            //--------

            logOper.setIData(this, 8, pub);
         

            sReInfo = "true";
            return sReInfo;

        } catch (Exception e) {
            try {
                logOper.setIData(this, operType, pub, true);
            } catch (YssException ex) {
                ex.printStackTrace();
            }
            //-----------------------------------------
            runStatus.appendValRunDesc("统计失败" + e.getMessage());
            throw new YssException("统计库存失败！\n", e); //添加异常信息描述 sunkey 2009.02.21 MS00004 QDV4.1-2009.2.1_09A
        } finally {
        	YssGlobal.clearValNums =false;//估值完成，将变量值改为假便于下一次估值前清空数据 by leeyu 20100521 合并太平版本
            YssGlobal.removeRefeUserInfo(YssCons.YSS_OPER_VALUATION, portCodes, pub); //MS00006-QDV4.1赢时胜上海2009年2月1日05_A   add by songjie 2009-04-29
            //add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
            //如果不是调度方案调用，则在资产估值模块批量插入业务日志，否则在调度方案执行完成后插入业务日志
            if(!this.comeFromDD){
				//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
            	if(logSumCode.trim().length() == 0){
            		logSumCode = df.getLogSumCodes();
            	}
            	if(logStartTime == null){
            		logStartTime = new Date();
            	}
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			if(logOper.getDayFinishLog().getAlPojo().size() == 0){
            			logOper.setDayFinishIData(this, operType, " ", pub, isError, this.portCodes, 
                    			this.startDate, dDate, this.endDate, " ", 
                    			logStartTime, logSumCode,new Date());
        			}
        			//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
        			
        			logOper.setDayFinishIData(this, operType, "sum", pub, isError, " ", 
            			this.startDate, dDate, this.endDate, " ", 
            			logStartTime, logSumCode,new Date());
        		}
            }
            
            this.comeFromDD = false;
        }
    }
    
    /**
     *story3759 份额折算 add by yeshenghong 20130519
     *
     *判断杠杆基金是否份额折算
     * @throws YssException 
     */
    private boolean judgeCommonFundShareDisCount(Date dDate,String portCode) throws YssException
    {
    	 ResultSet rs = null;
         String strSql = "";
         boolean discounted = false;
         try {
                 strSql = " SELECT * FROM " + pub.yssGetTableName("Tb_TA_FundRight") +
                 		 " WHERE FCHECKSTATE = 1 AND FRightType= 'Reinvest' AND FRightDate = " +
                 dbl.sqlDate(dDate) + " AND FPortCode = " + dbl.sqlString(portCode);
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 discounted = true;
                 }
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	return discounted;
    }
    
    /**
     *story3759 份额折算
     *add by yeshenghong 20130519
     *重复资产估值时先删除拆分数据
     * @throws YssException 
     */
    private void deleteFundShareData(Date dDate,String portCode) throws YssException
    {
         String strSql = "";
         try {
             // 插入数据前，先删除数据，条件： 日期和组合
    	 	 strSql = " delete from " + pub.yssGetTableName("Tb_TA_Trade")
				+ " where FTradeDate =" + dbl.sqlDate(dDate)
				+ " and FPortCode =" + dbl.sqlString(portCode) + " and FSellType = '09' ";
             dbl.executeSql(strSql);
         } catch (Exception e) {
             throw new YssException(e);
         } 
    }
    
 

    /**
     *story3759 份额折算
     *add by yeshenghong 20130519
     *判断杠杆基金是否份额折算
     * @throws YssException 
     */
    private boolean judgeLeverGradeFundShareDisCount(Date dDate,String portCode) throws YssException
    {
    	 ResultSet rs = null;
         String strSql = "";
         boolean discounted = false;
         try {
                 strSql = " select distinct t.fconversiondate from " + pub.yssGetTableName("tb_ta_levershare") + " t  " +
                 		" where FCheckState = 1 and t.fconversiondate =  " + dbl.sqlDate(dDate) + " and t.FPortcode = " + dbl.sqlString(portCode);
                 rs = dbl.openResultSet(strSql);
                 if (rs.next()) {
                	 discounted = true;
                 }
         } catch (Exception e) {
             throw new YssException(e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	return discounted;
    }
    
   
   
    
    
    /**
     * 根据费用划款指令设置生成相关费用的划款指令数据
     * add by songjie
     * 2011.05.13
     * 需求 759
     * QDV4工银2011年3月7日05_A
     * @param portCode
     * @param tradeDatem
     * @param hmFeeSet
     * @throws YssException
     */
    private void generateCashCommandOfFee(String portCode,Date tradeDate, HashMap hmFeeSet)throws YssException{
		Connection conn = null;
		boolean bTrans = false;
    	HashMap hmCashFeeOfGLF = null;
    	HashMap hmCashFeeOfTGF = null;
    	HashMap hmCashFeeOfFXJ = null;
    	Date startDateOfMonth = null;
    	String year = null;
    	String month = null;
    	BigDecimal feeOfGLF = new BigDecimal(0);//划款指令中的管理费金额
    	BigDecimal feeOfTGF = new BigDecimal(0);//划款指令中的托管费金额
    	BigDecimal feeOfFXJ = new BigDecimal(0);//划款指令中的风险金金额
    	CommandBean command = null;
    	try{
			BaseOperDeal baseOperDeal = new BaseOperDeal(); // 新建BaseOperDeal
			baseOperDeal.setYssPub(pub);
            year = YssFun.formatNumber(tradeDate.getYear() + 1900, "0000");//获取估值日年份
            month = YssFun.formatNumber(tradeDate.getMonth() + 1, "00");//获取估值日月份
    		if(hmFeeSet.get(portCode + ",管理费") != null){
    			hmCashFeeOfGLF = (HashMap)hmFeeSet.get(portCode + ",管理费");
                startDateOfMonth = baseOperDeal.getWorkDay((String)hmCashFeeOfGLF.get("Holiday"), YssFun.parseDate(year + "-" + month + "-01"), 0);
    			if(YssFun.dateDiff(startDateOfMonth, tradeDate) == 0){//若为估值日期为当月的第一个工作日
    				if("按季度生成".equals((String)hmCashFeeOfGLF.get("CreateCycle"))){//若费用相关的划款指令是按季度生成的
    					//若估值日月份为 1月、4月、7月、10月，则可生成相关费用的划款指令
    					if(tradeDate.getMonth() + 1 == 1 || tradeDate.getMonth() + 1 == 4 || 
    					   tradeDate.getMonth() + 1 == 7 || tradeDate.getMonth() + 1 == 10){
    						feeOfGLF = generateCashCommand(portCode,hmCashFeeOfGLF,tradeDate,"管理费");
    					}
    				}else{//若费用相关的划款指令是按月生成的
    					feeOfGLF = generateCashCommand(portCode,hmCashFeeOfGLF,tradeDate,"管理费");
    				}
    			}
    		} 
    		if(hmFeeSet.get(portCode + ",风险金") != null){
    			hmCashFeeOfFXJ = (HashMap)hmFeeSet.get(portCode + ",风险金");
                startDateOfMonth = baseOperDeal.getWorkDay((String)hmCashFeeOfFXJ.get("Holiday"), YssFun.parseDate(year + "-" + month + "-01"), 0);
    			if(YssFun.dateDiff(startDateOfMonth, tradeDate) == 0){//若为估值日期为当月的第一个工作日
    				if("按季度生成".equals((String)hmCashFeeOfFXJ.get("CreateCycle"))){//若费用相关的划款指令是按季度生成的
    					//若估值日月份为 1月、4月、7月、10月，则可生成相关费用的划款指令
    					if(tradeDate.getMonth() + 1 == 1 || tradeDate.getMonth() + 1 == 4 || 
    					   tradeDate.getMonth() + 1 == 7 || tradeDate.getMonth() + 1 == 10){
    						feeOfFXJ = generateCashCommand(portCode,hmCashFeeOfFXJ,tradeDate,"风险金");
    					}
    				}else{//若费用相关的划款指令是按月生成的
    					feeOfFXJ = generateCashCommand(portCode,hmCashFeeOfFXJ,tradeDate,"风险金");
    				}
    			}
    		}
    		if(hmFeeSet.get(portCode + ",托管费") != null){
    			hmCashFeeOfTGF = (HashMap)hmFeeSet.get(portCode + ",托管费");
                startDateOfMonth = baseOperDeal.getWorkDay((String)hmCashFeeOfTGF.get("Holiday"), YssFun.parseDate(year + "-" + month + "-01"), 0);
    			if(YssFun.dateDiff(startDateOfMonth, tradeDate) == 0){//若为估值日期为当月的第一个工作日
    				if("按季度生成".equals((String)hmCashFeeOfTGF.get("CreateCycle"))){//若费用相关的划款指令是按季度生成的
    					//若估值日月份为 1月、4月、7月、10月，则可生成相关费用的划款指令
    					if(tradeDate.getMonth() + 1 == 1 || tradeDate.getMonth() + 1 == 4 || 
    					   tradeDate.getMonth() + 1 == 7 || tradeDate.getMonth() + 1 == 10){
    						feeOfTGF = generateCashCommand(portCode,hmCashFeeOfTGF,tradeDate,"托管费");
    					}
    				}else{//若费用相关的划款指令是按月生成的
    					feeOfTGF = generateCashCommand(portCode,hmCashFeeOfTGF,tradeDate,"托管费");
    				}
    			}
    		}
    		
    		//风险金保留两位小数
    		feeOfFXJ = new BigDecimal(YssD.round(feeOfFXJ.doubleValue(), 2));
    		
    		if(hmFeeSet.get(portCode + ",管理费") != null){
    		    //管理费金额 = round(净值统计表中的管理费金额 - 风险金金额,2)
    			feeOfGLF = new BigDecimal(YssD.round(feeOfGLF.subtract(feeOfFXJ).doubleValue(), 2));
    		}
    		
    		//托管费保留两位小数
    		feeOfTGF = new BigDecimal(YssD.round(feeOfTGF.doubleValue(), 2));
    		
    		if(feeOfGLF.doubleValue() != 0 || feeOfTGF.doubleValue() != 0 || feeOfFXJ.doubleValue() != 0){
    			pub.setbSysCheckState(false);//设置审核状态为1
    		}
    		
    		DecimalFormat f = new DecimalFormat();
    		f.setMaximumFractionDigits(2);//设置格式化小数位的最大位数
    		
    		conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
    		
    		if(feeOfGLF.doubleValue() != 0){
    			command = new CommandBean();
    			command.setYssPub(pub);
    			command.setPayMoney(feeOfGLF.doubleValue());
    			command.setReMoney(feeOfGLF.doubleValue());
    			command.setCashUsage((String)hmCashFeeOfGLF.get("PortName") + " 应付管理费：" + f.format(feeOfGLF) +  "元");
    			command = command.setCommandInfo(hmCashFeeOfGLF,tradeDate);
    			command.deleteValGenerateData("管理费");//若重新估值，则删除之前估值已生成的管理费相关的划款指令数据
    			command.addSetting();//生成划款指令数据
    		}
    		
    		if(feeOfTGF.doubleValue() != 0){
    			command = new CommandBean();
    			command.setYssPub(pub);
    			command.setPayMoney(feeOfTGF.doubleValue());
    			command.setReMoney(feeOfTGF.doubleValue());
    			command.setCashUsage((String)hmCashFeeOfTGF.get("PortName") + " 应付托管费：" + f.format(feeOfTGF) +  "元");
    			command = command.setCommandInfo(hmCashFeeOfTGF,tradeDate);
    			command.deleteValGenerateData("托管费");//若重新估值，则删除之前估值已生成的托管费相关的划款指令数据
    			command.addSetting();//生成划款指令数据
    		}
    		if(feeOfFXJ.doubleValue() != 0){
    			command = new CommandBean();
    			command.setYssPub(pub);
    			command.setPayMoney(feeOfFXJ.doubleValue());
    			command.setReMoney(feeOfFXJ.doubleValue());
    			command.setCashUsage((String)hmCashFeeOfFXJ.get("PortName") + " 应付风险金：" + f.format(feeOfFXJ) +  "元");
    			command = command.setCommandInfo(hmCashFeeOfFXJ,tradeDate);
    			command.deleteValGenerateData("风险金");//若重新估值，则删除之前估值已生成的风险金相关的划款指令数据
    			command.addSetting();//生成划款指令数据
    		}
    		
    		conn.commit();
    		bTrans = false;
            conn.setAutoCommit(true);
    	}catch(Exception e){
    		throw new YssException("根据通用参数_费用划款指令设置生成划款指令出错！");
    	}finally{
    		dbl.endTransFinal(conn, bTrans);
    	}
    }
    
    /**
     * 根据费用划款指令设置生成相关费用的划款指令数据
     * add by songjie
     * 2011.05.13
     * 需求 759
     * QDV4工银2011年3月7日05_A
     * @param portCode
     * @param hmCashFeeSet
     * @param tradeDate
     * @throws YssException
     */
    private BigDecimal generateCashCommand(String portCode, HashMap hmCashFeeSet, Date tradeDate, String feeType) throws YssException{
    	GregorianCalendar cl = new GregorianCalendar();
    	String year = null;//估值日年份
    	String month = null;//估值日对应的上个月的月份
    	String maxDay = null;//上个月的最大天数
    	Date endDateOfLastMonth = null;//上个月的最后一个自然日
    	String strSql = null;
    	ResultSet rs = null;
    	BigDecimal fee = new BigDecimal(0);//费用金额
    	BigDecimal feeOfGLF = new BigDecimal(0);//管理费金额
    	BigDecimal feeOfTGF = new BigDecimal(0);//托管费金额
    	try{
            year = YssFun.formatNumber(tradeDate.getYear() + 1900, "0000");//获取估值日年份
            month = YssFun.formatNumber(tradeDate.getMonth(), "00");//获取估值日对应的上个月的月份
            
            if(tradeDate.getMonth() == 0){//若估值日为1月份的日期，则获取去年12月份最后一个自然日的净值数据
            	month = "12";
            	year = YssFun.formatNumber(tradeDate.getYear() + 1899, "0000");
            }
            cl.setTime(YssFun.parseDate(year + "-" + month + "-01"));
            maxDay = YssFun.formatNumber(cl.getActualMaximum(Calendar.DAY_OF_MONTH),"00");//获取上个月的最大天数
            endDateOfLastMonth = YssFun.parseDate(year + "-" + month + "-" + maxDay);//得到上个月的最后一个自然日
            //获取上月最后一个自然日净值统计表中的管理费金额 和 托管费金额
            strSql = " select distinct a.FKeyCode,a.FPortMarketValue from " +
            pub.yssGetTableName("Tb_Data_Navdata") + " a where FNavDate = " + dbl.sqlDate(endDateOfLastMonth) + 
            " and FReTypeCode = 'Invest' and (FKeyCode like 'IV001%' or FKeyCode like 'IV002%') and FPortCode = " + dbl.sqlString(portCode);
            rs = dbl.openResultSet(strSql);
            while(rs.next()){
            	if(rs.getString("FKeyCode").indexOf("IV001") != -1){
            		feeOfGLF = rs.getBigDecimal("FPortMarketValue");
            	}
            	if(rs.getString("FKeyCode").indexOf("IV002") != -1){
            		feeOfTGF = rs.getBigDecimal("FPortMarketValue");
            	}
            }
            if("管理费".equals(feeType)){
            	//净值统计表中的管理费金额
            	fee = feeOfGLF;
            }else if("风险金".equals(feeType)){
            	//风险金 = round(管理费金额  * 0.1,2)
//            	fee = new BigDecimal(YssD.round(feeOfGLF.multiply(new BigDecimal(0.1)).doubleValue(),2));
            	fee = new BigDecimal(YssD.round(feeOfGLF.multiply(BigDecimal.valueOf(0.1)).doubleValue(),2));//findbugs风险修改，new BigDecimal(0.1)会返回0.1000000000000000055511151231257827021181583404541015625的数值  胡坤 20120613
            }else if("托管费".equals(feeType)){
            	//净值统计表中的托管费金额
            	fee = feeOfTGF;
            }
            
            return fee;
    	}catch(Exception e){
    		throw new YssException("根据通用参数_费用划款指令设置计算费用对应的划款指令金额出错！");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /***
     * add by wuweiqi 20110110 QDV4工银2010年12月22日01_A
     * 依据通用参数设置的算法，在资产估值中进行自动计提两费
     * @param dDate
     * @param portCodes
     * @throws YssException
     */
   private void getDayIncomesFee(java.util.Date dDate,String portCodes) throws YssException {
        CtlPubPara pubpara = null;
        String resultPubpara = "";
        String sSelCodes="";
        ArrayList bondList = new ArrayList();
        try {
         	StatInvestFee st=new StatInvestFee();
        	st.setYssPub(pub);   
        	//------------------- 获取通用参数设置中通过利息算法计提两费界面中设置信息---------------------------------//
            pubpara=new CtlPubPara();
            pubpara.setYssPub(pub);
        	resultPubpara=pubpara.getInvestInfo(portCodes);
            if (resultPubpara.split("\t")[0].split("[|]").length == 0) {
                 throw new YssException("请在通用参数设置中完成对费用类型的设置!");
            }
            if ( (resultPubpara.split("\t")[1]).split("[|]").length == 0) {
                throw new YssException("请在通用参数设置中完成对费用计算公式的设置!");
            }
            sSelCodes = resultPubpara.split("\t")[0].split("[|]")[0]; 
            if(sSelCodes.equalsIgnoreCase("null")){
            	//edit by songjie 2012.08.01 STORY #2827 QDV4博时2012年07月26日01_A
            	 sSelCodes="IV001,IV002,IV103,YSS_STF";//STORY 2253 UPDATE BY ZHOUWEI 20120221 对于通用参数没设置的增加受托费
            }     
        	st.initIncomeStat(this.startDate, this.endDate, this.portCodes, sSelCodes);
        	//---------------------end 获取通用参数设置中信息--------------------------//
        	BaseIncomeStatDeal incomestat = (BaseIncomeStatDeal) pub.getOperDealCtx().getBean("statinvestfee");
            incomestat.setYssPub(pub);
            incomestat.initIncomeStat(this.startDate, this.endDate, this.portCodes, sSelCodes);
            bondList = st.getDayIncomes(dDate);
            incomestat.saveIncomes(bondList);//重新生成运营费用信息
        } catch (Exception e) {
            throw new YssException("系统自动计算运营收支两费时出现异常!" + "\n", e); 
        }
    }
    /***
     * add by wuweiqi 20101231 QDV4工银2010年11月1日01_A  
     * 验证是否需要自动计提运营两费
     * @return
     * @throws YssException
     */
       private boolean  getAutoCharge(String portAry)throws YssException{
           String strSql = null;//用于储存sql语句
           ResultSet rs = null;//声明结果集
           String FctlValue="";
           boolean strResult=false;
           String strCode="";//组合代码
           try{
           	strSql= " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                    " where FPUBPARACODE = 'AutoCharge' and FPARAID!=0 ";
           	rs=dbl.openResultSet(strSql);
	        while(rs.next()){
	        	 FctlValue = rs.getString("FCTLVALUE");
	           	 strCode=FctlValue.substring(0,FctlValue.indexOf("|"));
	           	 if(strCode.equals(portAry)){
	           		strResult = true;
	           		break;
	           	}
	          }
             return strResult;
           }
           catch(Exception e){
               throw new YssException("验证是否自动计提运营两费信息出错！", e);
           }
           finally{
               dbl.closeResultSetFinal(rs);
           }
      }
    private String doOperETFVal(String sType) throws YssException {
		int iDays = 0;
		java.util.Date dDate = null;
		String[] portAry = null;
		String[] valTypeAry = null;
		String[] valTypesNameAry = null;
		String strETFStatType = "";//补票方式
		try {
			hmValMethods = new HashMap();
			portAry = this.portCodes.split(",");
			valTypeAry = this.valuationTypes.split(",");
			valTypesNameAry = valuationTypesName.split(",");
                  
			iDays = YssFun.dateDiff(this.startDate, this.endDate);
            dDate = this.startDate;
            
            runStatus.appendValRunDesc("\r\n    开始统计组合群【" + pub.getPrefixTB() + "】... ...\r\n");
            
            for (int i = 0; i <= iDays; i++) {            	
            	runStatus.appendValRunDesc("【" + YssFun.formatDate(dDate) + "】开始统计... ...\r\n");        
                //将当日行情放入临时表，可提高估值时的SQL执行语句的效率 合并版本
                DataPrepareBeforeVal prepareBean=new DataPrepareBeforeVal();
            	prepareBean.setYssPub(pub);
                String tmpMVTable = prepareBean.createMarketValueTable(dDate);//系统优化处理，生成行情临时表 by leeyu 20100524 合并太平版本
            	for (int j = 0; j < portAry.length; j++) {            		
            		runStatus.appendValRunDesc("    组合【" + portAry[j] + "】开始统计... ...\r\n");  	
                	//获取补票方式
            		ETFParamSetAdmin etfParamAdmin = new ETFParamSetAdmin();
            		etfParamAdmin.setYssPub(pub);
            		ETFParamSetBean etfParamBean = new ETFParamSetBean();
            		HashMap hmPara = etfParamAdmin.getETFParamInfo(portAry[j]);
            		etfParamBean = (ETFParamSetBean)hmPara.get(portAry[j]);
            		if(etfParamBean == null){
            			throw new YssException("组合【" + portAry[j] + "】对应的ETF参数设置不存在或未审核！");
            		}
            		strETFStatType = etfParamBean.getSupplyMode();
            		if(!strETFStatType.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_ONE) && 
            				!strETFStatType.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)){//T日确认，etf净值表不统计当日申赎
            			doETFValuation(valTypeAry,valTypesNameAry,portAry[j],dDate,tmpMVTable,strETFStatType);//调用ETF资产估值方法 panjunfang modify 20101216 QDV4华宝2010年12月16日01_B
                        updateETFTradeData(portAry[j],dDate);//更新当日TA交易数据
                        creCashInsBalRP(portAry[j], dDate);//生成现金替代和现金差额的应收应付
            		}else{//T+1日确认（易方达、华夏）
            			if(strETFStatType.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)){
            				updateETFTradeData(portAry[j],dDate);//更新当日TA交易数据
            			}            			
            			creCashInsBalRP(portAry[j], dDate);//生成现金替代和现金差额的应收应付
            			doETFValuation(valTypeAry,valTypesNameAry,portAry[j],dDate,tmpMVTable,strETFStatType);//调用ETF资产估值方法 panjunfang modify 20101216 QDV4华宝2010年12月16日01_B
            		}                    
                    runStatus.appendValRunDesc("    组合【" + portAry[j] + "】统计完成\r\n");                    
            	}
                runStatus.appendValRunDesc("【" + YssFun.formatDate(dDate) + "】统计完成\r\n");
                runStatus.appendValRunDesc("-----------------------------------------\r\n");
            	dDate = YssFun.addDay(dDate, 1);
            }
            runStatus.appendValRunDesc("\r\n    组合群【" +pub.getPrefixTB() +"】统计完毕！");
            runStatus.appendValRunDesc("\r\n★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            
    		return "true";
			
		} catch (Exception e) {
			runStatus.appendValRunDesc("统计失败" + e.getMessage());
			throw new YssException("单独处理ETF资产估值失败！", e);
		}
	}
	
	/**
	 * ETF资产估值处理
	 * 
	 * @param valTypeAry
	 *            String[] ：估值类别代码
	 * @param valTypesNameAry
	 *            String[] ： 估值类别名称
	 * @param strPort
	 *            String ：当前组合代码
	 * @param strSupplyMode
	 *            String ：补票方式   panjunfang modify 20110810  STORY #1434 QDV4易方达基金2011年7月27日01_A
	 * @param date
	 *            Date ： 当前业务日期
	 */
	private void doETFValuation(String[] valTypeAry, String[] valTypesNameAry,
			String strPort, java.util.Date date, String tmpMVTable,
			String strSupplyMode) throws YssException {
        BaseStgStatDeal secstgstat = null;
        BaseStgStatDeal cashstgstat = null;
        BaseStgStatDeal recStgStat = null;
        BaseValDeal valuation = null;
        HashMap hmValRate = new HashMap();
        HashMap hmValPrice = new HashMap();
        HashMap hm = new HashMap();
        ArrayList alMethods = null;
        ArrayList alValType = null;
        String sReInfo = "";
        try{

            //证券库存统计
            secstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("SecurityStorage");
            secstgstat.setYssPub(pub);
            //add by songjie 2011.12.31 BUG 3491 QDV4海富通2011年12月22日01_B
            //用于判断是在做什么操作时做的证券库存统计
            secstgstat.setStgFrom("Valuation");
            secstgstat.setBETFStat(true);
            secstgstat.stroageStat(date, date, operSql.sqlCodes(strPort));
            //add by songjie 2011.12.31 BUG 3491 QDV4海富通2011年12月22日01_B
            secstgstat.setStgFrom("");

            //现金库存统计
            cashstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("CashStorage");
            cashstgstat.setYssPub(pub);
            cashstgstat.stroageStat(date, date, operSql.sqlCodes(strPort));

            //估值类别业务处理
            if(valTypeAry[valTypeAry.length-1].equals("IncomeFX")){//如果前台传过来的估值类别包含综合损益，则在处理综合损益前先进行可退替代款估值增值的计算
            	alValType = new ArrayList(Arrays.asList(valTypeAry));
            	alValType.remove(alValType.size() - 1);
            	alValType.add("ETFBackCashMV");
            	alValType.add("IncomeFX");            	
            } else {
            	alValType = new ArrayList(Arrays.asList(valTypeAry));
            	alValType.add("ETFBackCashMV");
            }
            for (int k = 0; k < alValType.size(); k++) {
                valuation = (BaseValDeal) pub.getOperDealCtx().getBean((String)alValType.get(k));
                valuation.setYssPub(pub);
                valuation.initValuation(date, strPort, (String)alValType.get(k), hmValRate, hmValPrice);
                valuation.setTmpMarketValueTable(tmpMVTable);//将原设置的行情临时表设置到估值中去  panjunfang modify 20101216 DV4华宝2010年12月16日01_B
                if (!this.hmValMethods.containsKey(strPort)) {
                    alMethods = valuation.getValuationMethods();
                    this.hmValMethods.put(strPort, alMethods);
                }
                valuation.setIsETFVal(true);
                hm = valuation.getValuationCats( (ArrayList)this.hmValMethods.get(strPort));
                sReInfo = valuation.saveValuationCats(hm).trim();
            }            
            valuation.addETFSecPrice();
            valuation.insertValMktPrice();
            valuation.insertValRate();

            //现金应收应付库存统计
            valuation.clearValPrice();
            recStgStat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("CashPayRec");
            recStgStat.setYssPub(pub);
            recStgStat.setBETFStat(true);
            recStgStat.setStrETFStatType(strSupplyMode);
            recStgStat.stroageStat(date, date, operSql.sqlCodes(strPort));

            //证券应收应付库存统计
            recStgStat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("SecRecPay");
            recStgStat.setYssPub(pub);
            recStgStat.stroageStat(date, date, operSql.sqlCodes(strPort));

            //TA库存统计
            recStgStat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("TAStorage");
            recStgStat.setYssPub(pub);
            recStgStat.setBETFStat(true);
            recStgStat.setStrETFStatType(strSupplyMode);
            recStgStat.stroageStat(date, date, operSql.sqlCodes(strPort));

            //调用ETF净值统计方法，并将数据插入到ETF净值表中
            stgETFNetValue(strPort, date);
            

        }catch(Exception e){
            throw new YssException("处理ETF资产估值出错！", e);
        }
    }
	/**
	 * 更新TA交易数据，包括：销售金额、基准金额、销售价格、未实现损益平准金、损益平准金
	 * @param strPort
	 * @param date
	 * @throws YssException
	 */
    private void updateETFTradeData(String strPort, Date date) throws YssException{
    	String strPsql = "";
		String strSql = "";
		double dETFUnitPrice = 0;//ETF净值表单位净值
		double dSellMoney = 0;//销售金额、基准金额
		double dIncomeNotBal = 0;//未实现损益平准金
		double dIncomeBal = 0;//损益平准金
		double dCashBal = 0;//现金差额
		double dConvertNum = 0;//份额折算金额
		double dCashRepAmount = 0;//现金替代金额（工银）
		double dTemp = 0.0;
		ResultSet rs = null;
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement psmt = null;
		YssPreparedStatement psmt = null;
        //=============end====================
		Connection conn = null;
		boolean bTrans = false;
		CtlPubPara pubpara = null;
		TaTradeBean taTrade = null;
		try{
            pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);

            taTrade = new TaTradeBean();
			taTrade.setYssPub(pub);
			
			conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            
            
           /* 
            * 获取现金差额使用费费率
            * MS00002
            * QDV4.1赢时胜（上海）2009年9月28日01_A
            * by 操忠虎   2009/11/24
            */
            double cashBalFeeRate=pubpara.getCashBalFeeRate(strPort); 
            
            /**
             * 加了一个字段：FCASHBALFEE
             * MS00002
             * QDV4.1赢时胜（上海）2009年9月28日01_A
             * by 操忠虎   2009/11/24
             */
			strPsql = "update " + pub.yssGetTableName("Tb_TA_Trade") + 
					" set FSellMoney = ?,FBeMarkMoney = ?,FIncomeNotBal = ?,FIncomeBal = ?,FCashBal = ?,FSellPrice = ?,FCONVERTNUM = ?,FCASHBALFEE=? ,FCashRepAmount = ? " + 
					" where FNum = ?";
			//modified by liubo.Story #2145
			//==============================
//			psmt = conn.prepareStatement(strPsql);
			psmt = dbl.getYssPreparedStatement(strPsql);
			//==============end================
			
			strSql = "select a.*,b.FPrice,c.FMVPortMarketValue,d.FFXPortMarketValue,e.FUnPLPortMarketValue,f.FTotalValue," + 
					" i.FUnitCashBal,k.FStockListValue,g.FNormScale,g.FSupplyMode,h.FConvertNum as FTotalConvertNum,m.FHCReplaceCash,n.FInsteadCashInc,m.FHReplaceCash, z.FTotalMoney from " + 
					"(select * from " + pub.yssGetTableName("Tb_TA_Trade") + 
					" where FCONFIMDATE = " + dbl.sqlDate(date) + 
					" and FPortCode = " + dbl.sqlString(strPort) + 
					" and FSellType in ('01','02') and FCheckState = 1" + 
					" ) a left join " + 
					" (select FPortCode,FPrice,fnavdate from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'Unit' " + 
					" and FPortCode = " + dbl.sqlString(strPort) + 
					" ) b on b.FPortCode = a.FPortCode and b.fnavdate = a.ftradedate " + 
					" left join (" + 
					" select FPortCode,FPortMarketValue as FMVPortMarketValue,fnavdate from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'MV' " + 
					" and FPortCode = " + dbl.sqlString(strPort) + 
					" ) c on c.FPortCode = a.FPortCode and c.fnavdate = a.ftradedate" + 
					" left join (select " ;
            if (pubpara.getInCome(strPort).equalsIgnoreCase("Yes")) { //计入已实现收益的情况
                strSql = strSql +
                    " 0 as FFXPortMarketValue,FPortCode,fnavdate from ";
            } else if (pubpara.getInCome(strPort).equalsIgnoreCase("No")) { //计入未实现收益的情况
                strSql = strSql +
                    " FPortMarketValue as FFXPortMarketValue,FPortCode,fnavdate from ";
            }
            //---------------------------------end
            else {
                strSql = strSql +
                    " 0 as FFXPortMarketValue,FPortCode,fnavdate from "; //默认已实现 sj edit 20080121
            }
			strSql = strSql + pub.yssGetTableName("Tb_ETF_NavData") +  
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'FX' " + 
					" and FPortCode = " + dbl.sqlString(strPort) + 
					" ) d on d.FPortCode = a.FPortCode and d.fnavdate = a.ftradedate" + 
					" left join (" + 
					" select FPortCode,FPortMarketValue as FUnPLPortMarketValue,fnavdate from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'UnPL' " + 
					" and FPortCode = " + dbl.sqlString(strPort) + 
					" ) e on e.FPortCode = a.FPortCode and e.fnavdate = a.ftradedate" + 
					" left join (" + 
					" select FPortCode,FPortMarketValue as FTotalValue,fnavdate from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'TotalValue' " + 
					" and FPortCode = " + dbl.sqlString(strPort) + 
					" ) f on f.FPortCode = a.FPortCode and f.fnavdate = a.ftradedate" + 
					//获取单位现金差额
					" left join (" + 
					" select FPortCode,FPortMarketValue as FUnitCashBal,fnavdate from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'UnitCashBal' " + 
					" and FPortCode = " + dbl.sqlString(strPort) + 
					" ) i on i.FPortCode = a.FPortCode  and i.fnavdate = a.ftradedate" + 
					//-----------------------
					//获取篮子估值
					" left join (" + 
					" select FPortCode , FPortMarketValue as FStockListValue,fnavdate from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FInvMgrCode = 'total' and FReTypeCode = 'Total' and FKeyCode = 'StockListVal' " + 
					" and FPortCode = " + dbl.sqlString(strPort) + 
					" ) k on k.FPortCode = a.FPortCode and k.fnavdate = a.ftradedate" + 
					//-------------------------------------------------------
					" left join (" + 
					" select FPortCode,FNormScale,FSupplyMode from " + pub.yssGetTableName("Tb_ETF_Param") + 
					" where FPortCode = " + dbl.sqlString(strPort) + 
					" ) g on g.FPortCode = a.FPortCode" + 
					" left join (select FPortClsCode,FConvertNum,FStorageDate from " + pub.yssGetTableName("Tb_Stock_Ta") + 
					" where FPortCode = " + dbl.sqlString(strPort) + 
					" ) h on h.FPortClsCode = a.FPortClsCode and h.FStorageDate = a.ftradedate" + 
					//----------------------获取TA申赎对应的可退替代款------------//arealw 华夏增加赎回现金替代（必须、部分）算法 2012-01-19。
					" left join (SELECT SUM(FHCReplaceCash) AS FHCReplaceCash,sum(FHReplaceCash) as FHReplaceCash, FBUYDATE,(case when FBS = 'B' then '01' else '02' end) as FBS,FPortCode from " + 
					pub.yssGetTableName("TB_ETF_TRADESTLDTL") + " WHERE FSecurityCode <> ' ' AND FStockHolderCode <> ' ' " + 
					" GROUP BY FBuyDate,FPortCode,FBS) m on a.ftradedate = m.fbuydate and m.FPortCode = a.FPortCode and m.FBS = a.FSellType" + 
					//-----------------------获取可退替代款估值增值
					" left join (select FPortCode, FPortMarketValue as FInsteadCashInc,fnavdate from " + pub.yssGetTableName("Tb_ETF_NavData") + 
					" where FInvMgrCode = 'total' and FReTypeCode = 'Cash' and FKeyCode like '%-" + YssOperCons.YSS_ZJDBZLX_ETFBACKCASH_MV + "'" + 
					" and FPortCode = " + dbl.sqlString(strPort) + 
					" ) n on n.FPortCode = a.FPortCode and n.fnavdate = a.ftradedate left join (select sum(FTotalMoney) as FTotalMoney, fportcode,fdate from "+
					pub.yssGetTableName("tb_ETF_StockList")+" where  freplacemark in ('2','6') and FCheckState = 1 and fsecuritycode <> '159900'"+
					" group by fportcode,fdate) z on z.FPortCode = a.FPortCode and  z.fdate = a.ftradedate";
					//arealw 华夏增加赎回现金替代（必须、部分）算法 2012-01-19。
			rs = dbl.openResultSet(strSql);
			while(rs.next()){		
				dCashRepAmount = rs.getDouble("FCashRepAmount");//现金替代
				dCashBal = YssD.mul(YssFun.roundIt(rs.getDouble("FUnitCashBal"),2), 
							YssD.div(rs.getDouble("FSellAmount"), rs.getDouble("FNormScale")));//现金差额 = 单位现金差额 * (销售数量 / 基准比例)
				dETFUnitPrice = YssFun.roundIt(rs.getDouble("FPrice"),15);//取ETF净值表中的单位净值
				//dETFUnitPrice = YssFun.roundIt(rs.getDouble("FPrice"),Integer.parseInt(pubpara.getCashUnit(strPort)));//取ETF净值表中的单位净值，并按照通用业务参数设置保留小数位
				if(rs.getString("FSupplyMode").equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) || 
						rs.getString("FSupplyMode").equals(YssOperCons.YSS_ETF_MAKEUP_SUB) ||
						rs.getString("FSupplyMode").equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)){
					if(rs.getString("FSellType").equals("02")){//赎回 TA销售金额 = 篮子估值 * 篮子数 + 现金差额
						//dSellMoney = YssD.add(YssD.mul(YssD.div(rs.getDouble("FSellAmount"),rs.getDouble("FNormScale")), YssD.round(rs.getDouble("FStockListValue"),2)), YssD.round(dCashBal, 2));
						//arealw 华夏增加赎回现金替代（必须、部分）算法 2012-01-19
						dSellMoney = YssD.add(YssD.mul(YssD.div(rs.getDouble("FSellAmount"),rs.getDouble("FNormScale")), YssD.round(rs.getDouble("FTotalMoney"),2)), YssD.add(YssD.round(dCashBal, 2),rs.getDouble("FHReplaceCash")));
					}else{//申购 TA销售金额 = 现金替代  + 现金差额 - 可退替代款
						dSellMoney = YssD.sub(YssD.add(dCashRepAmount, YssD.round(dCashBal, 2)),rs.getDouble("FHCReplaceCash"));
					}
				}else{
					dSellMoney = YssFun.roundIt(YssD.mul(rs.getDouble("FSellAmount"),dETFUnitPrice),2);//TA交易数据中的交易数量 * 销售价格
				}
				
				//以下获取股指期货、债券期货、外汇期货、商品期货估值增值
				dTemp = 0.0;
                if(pubpara.getParaValue("ISCredit", "selPort", "cboISCredit", strPort)){
            		dTemp = YssD.add(dTemp, taTrade.getQHGZ(strPort,rs.getDate("ftradedate"),"证券清算款_股指期货"));              	
                }
                if(pubpara.getParaValue("ISCredit_ZQ", "selPort", "cboISCredit", strPort)){
            		dTemp = YssD.add(dTemp, taTrade.getQHGZ(strPort,rs.getDate("ftradedate"),"证券清算款_债券期货"));              	
                }
                if(pubpara.getParaValue("ISCredit_WH", "selPort", "cboISCredit", strPort)){
            		dTemp = YssD.add(dTemp, taTrade.getQHGZ(strPort,rs.getDate("ftradedate"),"证券清算款_外汇期货"));              	
                }
                if(pubpara.getParaValue("ISCredit_SP", "selPort", "cboISCredit", strPort)){
            		dTemp = YssD.add(dTemp, taTrade.getQHGZ(strPort,rs.getDate("ftradedate"),"证券清算款_商品期货"));              	
                }
              //T日ETF净值表的（未实现损益平准金 + 估值增值合计值 + 汇兑损益合计值  + 可退替代款估值增值） / 资产净值 *  TA交易数据的基准金额
				dIncomeNotBal = YssD.round(YssD.mul(YssD.div(YssD.add(rs
						.getDouble("FMVPortMarketValue"), rs
						//.getDouble("FFXPortMarketValue"), rs
						.getDouble("FInsteadCashInc"),rs
						.getDouble("FUnPLPortMarketValue"),dTemp), rs
						.getDouble("FTotalValue")), dSellMoney),2);
				
				if(rs.getString("FSupplyMode").equals(YssOperCons.YSS_ETF_MAKEUP_NO)){//如果补票方式为”无补票“（工银）
					//计算预提交易收入
					taTrade.setStrSellNetCode(rs.getString("FSellNetCode"));
					taTrade.setStrSellTypeCode(rs.getString("FSellType"));
					taTrade.setStrCuryCode(rs.getString("FCuryCode"));
					taTrade.setDSellMoney(dSellMoney);
					taTrade.setDSellAmount(rs.getDouble("FSellAmount"));
					double dFee = taTrade.getTradeFee(rs.getString("FNum"));
					//-------------------------------------
					if(rs.getString("FSellType").equals("02")){
						dCashRepAmount = YssD.sub(dSellMoney,dFee);//通过将赎回款设置为替代金额的方式处理赎回款的结转
						dCashBal = 0;
					}else{
						dCashBal = YssD.sub(YssD.add(dSellMoney,dFee),dCashRepAmount);//现金差额 = 销售金额 + 预提交易收入 - 现金替代
					}
				}
				//份额折算金额 = T-1日TA库存的份额折算余额 * 申购净值（销售金额） / T 日资产净值
				dConvertNum = YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalConvertNum"), dSellMoney),
													rs.getDouble("FTotalValue")),2); 
				
				//计算已实现损益平准金
				double dtemp = YssD.add(YssD.mul(YssD.div(rs.getDouble("FSellAmount"),rs.getDouble("FNormScale")), YssD.round(rs.getDouble("FStockListValue"),2)), YssD.round(dCashBal, 2));//篮子估值 * 篮子数 + 现金差额
				if(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE.equals(rs.getString("FSupplyMode"))
						|| YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(rs.getString("FSupplyMode"))){
					dtemp = dSellMoney;
				}
				if(rs.getString("FSellType").equals("02")){//份额折算金额 * 资金方向 : 申购：1  赎回 -1 
					dConvertNum = -dConvertNum;
					dIncomeBal = YssD.sub(dtemp,rs.getDouble("FSellAmount"),dConvertNum,dIncomeNotBal);//已实现损益平准金 = 篮子估值 + 现金差额 - 销售数量  - 份额折算金额 – TA未实现损益平准金
				}else{
					dIncomeBal = YssD.sub(dtemp,rs.getDouble("FSellAmount"),-dConvertNum,dIncomeNotBal);//已实现损益平准金 = 篮子估值 + 现金差额 - 销售数量  - 份额折算金额 – TA未实现损益平准金
				}
				
				
				 
		         /* 计算现金差额使用费
		          	* MS00002
		            * QDV4.1赢时胜（上海）2009年9月28日01_A
		            * by 操忠虎   2009/11/24
		            */
				double cashBalFee=YssD.round(dCashBal,2)*cashBalFeeRate;					
				
				psmt.setDouble(1,dSellMoney);//设置销售金额
				psmt.setDouble(2,dSellMoney);//设置基准金额
				psmt.setDouble(3,dIncomeNotBal);//设置未实现损益平准金
				psmt.setDouble(4,dIncomeBal);//设置损益平准金
				psmt.setDouble(5,YssFun.roundIt(dCashBal,2));//设置现金差额
				psmt.setDouble(6,dETFUnitPrice);//设置销售价格为ETF净值表中的单位净值
				psmt.setDouble(7, dConvertNum);
				psmt.setDouble(8, YssD.round(cashBalFee,2));	//设置现金差额使用费
				psmt.setDouble(9, dCashRepAmount);
				psmt.setString(10,rs.getString("FNum"));
				
				psmt.addBatch();
			}
			
			psmt.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
			
		}catch(Exception e){
			throw new YssException("ETF组合【" + strPort + "】更新TA交易数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(psmt);
			dbl.endTransFinal(conn, bTrans);
		}
	}
    
    /**
     * 生成ETF现金替代和现金差额的应收应付
     * @param strPortCode
     * @param date
     * @throws YssException
     */
    private void creCashInsBalRP(String strPortCode, Date date) throws YssException {
        BaseOperManage opermanage = null;
        opermanage = (BaseOperManage) pub.getOperDealCtx().getBean("CashInsBal");
        opermanage.setYssPub(pub);
        opermanage.initOperManageInfo(date, strPortCode);
        opermanage.doOpertion();
    }

	/**
     * stgETFNetValue
     * 生成ETF净值表数据
     * @param strPort String
     * @param date Date
     */
    private void stgETFNetValue(String strPort, Date date) throws YssException{
        CtlNavRep navrep = null;
        try {
            navrep = new CtlNavRep();
            navrep.setPortCode(strPort);
            navrep.setInvMgrCode("total"); //直接设置为total类型的投资经理
            navrep.setDDate(date);
            navrep.setIsSelect(false);
            navrep.setYssPub(pub);
            navrep.invokeETFOperMethod();
        } catch (Exception e) {
            throw new YssException("生成ETF净值表数据失败!\n", e);
        }
    }

    /**
     * 从前台传进来的组合中筛选出ETF组合
     * @throws YssException
     */
    private HashMap getETFPort(String strPorts) throws YssException {
        StringBuffer bufSql = new StringBuffer();
        ResultSet rs = null;
        String[] portCodesAry = null;
        String strTemp = "";
        HashMap hmETFPorts = new HashMap();
        try{    	
        	portCodesAry = strPorts.split(",");
        	for(int i=0;i<portCodesAry.length;i++){
        		strTemp = strTemp + dbl.sqlString(portCodesAry[i]) + ",";
        	}
        	if(strTemp.length()>1){
        		strTemp = strTemp.substring(0,strTemp.length()-1);
        	}
            bufSql.append("SELECT FPortCode FROM ").append(pub.yssGetTableName("Tb_Para_Portfolio"));
            bufSql.append(" WHERE FPortCode IN ( " ).append(strTemp).append(") AND FAssetType = '01' AND FSubAssetType = '0106' ");
            rs = dbl.openResultSet(bufSql.toString());
            while(rs.next()){
                hmETFPorts.put(rs.getString("FPortCode"),rs.getString("FPortCode"));
            }
            return hmETFPorts;
        }catch(Exception e){
            throw new YssException("获取ETF组合出错！", e);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 继承于新接口的方法，用于执行业务处理
     * @param sType String
     * @return String
     * @throws YssException
     * MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    public String doOperManage(String sType) throws YssException {
          String sAllGroup = ""; //定义一个字符用来保存执行后的结果传到前台
          String sPrefixTB = pub.getPrefixTB(); //获取组合群表前缀
          String[] assetGroupCodes = this.assetGroupCode.split(YssCons.YSS_GROUPSPLITMARK); //按组合群的解析符解析组合群代码
          String[] strPortCodes = this.portCodes.split(YssCons.YSS_GROUPSPLITMARK); //按组合群的解析符解析组合代码
          try{
        	  //---edit by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
              if(logOper == null){//添加空指针判断
                  logOper = SingleLogOper.getInstance();
              }
              //---edit by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	  
              for (int i = 0; i < assetGroupCodes.length; i++) { //循环遍历每一个组合群
                  this.assetGroupCode = assetGroupCodes[i]; //得到一个组合群代码
                  pub.setPrefixTB(this.assetGroupCode); //修改公共变量的当前组合群代码
                  this.portCodes = strPortCodes[i]; //得到一个组合群下的组合代码
                  sAllGroup = this.operManage(); //调用以前的执行方法
              }
              //----------add by guojianhua  2010 09 13-----
              operType = 14;

              this.setFunName("businessdeal");
              this.setModuleName("dayfinish");
              this.setRefName("00008BD");
              logOper.setIData(this, operType, pub);
              //----------end-----------
          }
          catch(Exception e){
        	  //-----------add by guojianhua 2010 09 13------------
       	   try {
       		   operType=14;
                  logOper = SingleLogOper.getInstance();
                  this.setFunName("businessdeal");
                  this.setModuleName("dayfinish");
                  this.setRefName("00008BD");
                  logOper.setIData(this, operType, pub,true);
              } catch (YssException ex) {
                  ex.printStackTrace();
              }
              //------------end-------------------
              throw new YssException("执行业务处理出现异常！",e);
          }finally{
              pub.setPrefixTB(sPrefixTB);//还原公共变的里的组合群代码
          }
          return sAllGroup;//将结果返回到前台
    }

    /**
     * 执行业务处理
     * @return String
     * @throws Exception
     * MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
     */
    public String operManage() throws Exception {
       String[] operManageType = null;      //用来保存检查类型的数组
       String[] strPre = null;         //用来保存需要检查的组合编号的数组
       String[] operManageTypeName = null;  //用来保存检查类型说明的数组
       int iDays = 0;                  //保存需要检查的天数
       java.util.Date dTheDay = null;  //传入 doCheck 方法中的检查的当前日期
       String sReInfo;                 //如果检查完成则返回 “true”
       
       //---edit by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
       if(logOper == null){//添加空指针判断
    	   logOper = SingleLogOper.getInstance();//日终处理--日志报表 edited by zhouxiang 2010.12.14
       }
       //---edit by songjie 2012.09.28 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
       
       //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
       DayFinishLogBean df = new DayFinishLogBean();
       Date logStartTime = null;
	   //add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A
       boolean isError = false;//是否异常
       //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
       try {
    	   //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    	   df.setYssPub(pub);
    	   this.setFunName("businessdeal");//设置功能调用代码
    	   if(this.logSumCode.trim().length() == 0){
        	   logSumCode = df.getLogSumCodes();//日志汇总编号
    	   }
    	   //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    	   
           operManageType = this.operManageTypes.split(",");
           strPre = this.portCodes.split(",");
           operManageTypeName = this.operManageTypesName.split(",");
           iDays = YssFun.dateDiff(this.startDate, this.endDate);
           dTheDay = this.startDate;
           runStatus.appendValRunDesc("\r\n★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
           runStatus.appendValRunDesc("\r\n开始组合群【" +
                                           pub.getPrefixTB() +
                                           "】的业务处理... ...");
           //做日期循环
           try{
        	   //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	   logStartTime = new Date();
        	   operType = 14;
               //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	   
        	   for (int iRingDays = 0; iRingDays <= iDays; iRingDays++) {
                   runStatus.appendValRunDesc("\r\n   开始【" + YssFun.formatDate(dTheDay) +"】的业务处理... ...");
                   //做组合循环
                   for (int iRingPre = 0; iRingPre < strPre.length; iRingPre++) {
                	   //edit by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 logSumCode
                       doManagerDeal(operManageType,operManageTypeName,strPre[iRingPre],dTheDay, logSumCode);
                   }
                   
                   runStatus.appendValRunDesc("\r\n   【" + YssFun.formatDate(dTheDay) +"】的业务处理完成！");
                   runStatus.appendValRunDesc("\r\n    ****************************************");
                   dTheDay = YssFun.addDay(dTheDay, 1);
               }
           }catch(Exception ex){
        	   throw new YssException("业务处理失败！\n", ex);//add by songjie 2011.03.21 BUG:1159 QDV4赢时胜(测试)2011年2月25日03_B
           }
           runStatus.appendValRunDesc("\r\n组合群【" + pub.getPrefixTB() + "】的业务处理完毕！");
           runStatus.appendValRunDesc("\r\n★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
           sReInfo = "true";
           return sReInfo;
       } catch (Exception e) {
	       //add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A
    	   isError = true;//判断为异常
           //----------------end--------------
           runStatus.appendValRunDesc("业务处理失败" + e.getMessage());
           throw new YssException("业务处理失败！\n", e);
       } finally{
    	   //add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    	   if(!this.comeFromDD){//若不是通过调度方案调用业务处理，则插入汇总日志数据
       			//edit by songjie 2012.11.20 添加非空判断
       		    if(logOper != null){
        			//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
        			if(logOper.getDayFinishLog().getAlPojo().size() == 0){
            			logOper.setDayFinishIData(this, operType, " ", pub, isError, this.portCodes, 
                    			this.startDate, this.startDate, this.endDate, " ", 
                    			logStartTime, logSumCode,new Date());
        			}
        			
       		    	logOper.setDayFinishIData(this, operType, "sum", pub, isError," ", 
           	        this.startDate,this.startDate, this.endDate, " ", 
           	        logStartTime,logSumCode, new Date());
					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
       		    }
    	   }
    	   this.comeFromDD = false;
       }
   }

    /**
     * 
     * @方法名：emptyStock
     * @参数：dDate,portCodes
     * @返回类型：void
     * @说明：主要正对证券代码变更业务,估值完成后删去统计库存时,新股的T-1日人为增加的库存数据 add by zhangfa 20100814 MS01446
     */
    private void emptyStock(java.util.Date dDate,String portCodes)throws YssException{
 	   //1.清空证券库存
 	   emptySecurityStock(dDate,portCodes);
 	   //2.清空证券应收应付库存
 	   emptySecRecPays(dDate,portCodes);
    }
    
    /**
     * 
     * @方法名：emptySecRecPays
     * @参数：dDate,portCodes
     * @返回类型：void
     * @说明：主要正对证券代码变更业务,清空证券应收应付库存 add by zhangfa 20100814 MS01446
     */
    public void emptySecurityStock(java.util.Date dDate,String portCodes) throws YssException{
		boolean bTrans = false;
		String strSql = "";
		String dsql = "";
		ResultSet rs = null;
		Connection conn = dbl.loadConnection();
		try {
			bTrans = true;
			conn.setAutoCommit(false);

			strSql = "select fsecuritycodebefore,fsecuritycodeafter  from  "
					+ pub.yssGetTableName("Tb_Para_SecCodeChange")
					+ " where fbusinessdate=" + dbl.sqlDate(dDate)
					+ " and FCHECKSTATE =1";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				dsql = "delete from "
						+ pub.yssGetTableName("tb_stock_security")
						+ " where FSecurityCode="
						+ dbl.sqlString(rs.getString("fsecuritycodeafter"))
						+ " and FStorageDate="
						+ dbl.sqlDate(YssFun.addDay(dDate, -1))
						+" and FPortCode in(" + dbl.sqlString(portCodes) + ")";
				dbl.executeSql(dsql);
									
				//-start by dongqingsong 2013-07-05 BUG 8545 BUG8545证券代码变更业务处理后，又进行送股，资产估值后，证券库存中总是少其中一只券的库存 //
				//start by dongqingsong  证券代码不需要删除，库存为0即可
				//---add by songjie 2011.12.30 BUG 3491 QDV4海富通2011年12月22日01_B start---//
//				dsql = "delete from " + pub.yssGetTableName("tb_stock_security") 
//				+ " where FSecurityCode = " + dbl.sqlString(rs.getString("FSecurityCodeBefore"))
//				+ " and FStorageDate= " +  dbl.sqlDate(dDate) +" and FPortCode in(" + operSql.sqlCodes(portCodes) + ")";
//				dbl.executeSql(dsql);
				//---add by songjie 2011.12.30 BUG 3491 QDV4海富通2011年12月22日01_B end---//
				//-start by dongqingsong 2013-07-05 BUG 8545 BUG8545证券代码变更业务处理后，又进行送股，资产估值后，证券库存中总是少其中一只券的库存 //	
			}
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		} catch (Exception ex) {
			throw new YssException("删除变更后的证券库存出错", ex);
		} finally {

			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);

		}
    }
    /**
     * 
     * @方法名：emptySecurityStock
     * @参数：dDate,portCodes
     * @返回类型：void
     * @说明：主要正对证券代码变更业务,清空证券库存 add by zhangfa 20100814 MS01446
     */
    public void emptySecRecPays(java.util.Date dDate,String portCodes) throws YssException{
		boolean bTrans = false;
		String strSql = "";
		String dsql = "";
		ResultSet rs = null;
		Connection conn = dbl.loadConnection();
		try {
			bTrans = true;
			conn.setAutoCommit(false);

			strSql = "select fsecuritycodebefore,fsecuritycodeafter from  "
					+ pub.yssGetTableName("Tb_Para_SecCodeChange")
					+ " where fbusinessdate=" + dbl.sqlDate(dDate)
					+ " and FCHECKSTATE =1";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				dsql = "delete from "
						+ pub.yssGetTableName("Tb_Stock_SecRecPay")
						+ " where FSecurityCode="
						+ dbl.sqlString(rs.getString("fsecuritycodeafter"))
						+ " and FStorageDate="
						+ dbl.sqlDate(YssFun.addDay(dDate, -1))
						+" and FPortCode in(" + dbl.sqlString(portCodes) + ")";
				dbl.executeSql(dsql);				
				
				//---add by songjie 2011.12.30 BUG 3491 QDV4海富通2011年12月22日01_B start---//
				dsql = "delete from " + pub.yssGetTableName("Tb_Stock_SecRecPay") 
				+ " where FSecurityCode = " + dbl.sqlString(rs.getString("FSecurityCodeBefore"))				
				+ " and FStorageDate= " +  dbl.sqlDate(dDate) +" and FPortCode in(" + operSql.sqlCodes(portCodes) + ")";
				 
				dbl.executeSql(dsql);
				//---add by songjie 2011.12.30 BUG 3491 QDV4海富通2011年12月22日01_B end---//
			}
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		} catch (Exception ex) {
			throw new YssException("删除变更后的证券应收应付库存出错", ex);
		} finally {

			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);

		}
    }
	//--------------------------------------------------------------------------------- 
    //add by zhouxiang 2010.12.1 证券借贷资产估值---------------------------------------
    /**runStatus 要输出的信息
     * portAry   组合代码集合
     * dDate	  估值日期
     * @throws YssException 
     */
    private void checkBLMortgageValue(YssStatus runStatus, String portcode,java.util.Date dDate) throws YssException {
		String sBrokers=getBrokersByPortAry(portcode,dDate);//获取券商
		double dLendValueX=0;//借出净值
		double dMortgageY=0; //抵押物净值
		double dMortgageMAndN=0;//抵押物比例m+抵押物警戒值n
		if(sBrokers.trim().length()>0){
		for(int i=0;i<sBrokers.split(",").length;i++){//循环券商
			dLendValueX=YssD.add(dLendValueX,getLendValueX(sBrokers.split(",")[i],dDate));
			dMortgageY=YssD.add(dMortgageY, getLendValueY(sBrokers.split(",")[i],dDate));
			dMortgageMAndN=getLendValueMAndN(sBrokers.split(",")[i],dDate);
			if(YssD.div(dMortgageY, dLendValueX)<dMortgageMAndN){
				 runStatus.appendValRunDesc("     当前 组合："+portcode+",券商："+sBrokers+"的阙值过低，请进行抵押物补交 \r\n");
			}
			dLendValueX=0;dMortgageY=0;dMortgageMAndN=0;
		}
		}
	}

	

	private double getLendValueMAndN(String broker,java.util.Date dDate) throws YssException {
		double dratioMandN= 0;
		ResultSet rs = null;
		ResultSet rsSub=null;
		String strSql = "";
		String strSub="";
		try {
			strSql = "select a.fcollateralcode  from "
					+ pub.yssGetTableName("tb_data_seclendtrade")// 循环券商下的每一个借入的交易数据获取抵押物代码获取抵押物净值
					+ " a where a.fbargaindate = " + dbl.sqlDate(dDate)
					+ " and a.ftradetypecode = "
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)
					+ " and a.fbrokercode = " + dbl.sqlString(broker);
			rs = dbl.openResultSet(strSql);
			if(rs.next()) {
				strSub="select  distinct a.fcollateralcode, a.fcollateralratio as ratio,b.fcollateralwarmvaule as warvalue from "+pub.yssGetTableName("tb_data_seclendtrade")
					+" a  left join (select b.fcollateralcode,max(b.fcollateralwarmvaule) as fcollateralwarmvaule from "
					+pub.yssGetTableName("Tb_para_Collateral") 
					+" b where b.fcollateralcode = "+dbl.sqlString(rs.getString("fcollateralcode"))
					+" and b.fcheckstate = 1 group by fcollateralcode) b on a.fcollateralcode = b.fcollateralcode"
					+" where a.fbargaindate = "+dbl.sqlDate(dDate)
					+" and a.ftradetypecode = "+dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)
					+" and a.fbrokercode ="+dbl.sqlString(broker);
				rsSub=dbl.openResultSet(strSub);
				if(rsSub.next()){
					dratioMandN=YssD.add(rsSub.getDouble("ratio"), rsSub.getDouble("warvalue"));
				}
			}
		}catch (Exception e) {
			throw new YssException("计算抵押物净值出错");
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsSub);
		}
		return dratioMandN;
	}

	/**获取抵押物净值
	 * @param sbroker 券商
	 * @param dDate   日期
	 * @return
	 * @throws YssException 
	 */
    private double getLendValueY(String sbroker, Date dDate) throws YssException {
    	double dSingerTotalY = 0;// 单笔交易的抵押物净值Y
		ResultSet rs = null;
		String strSql = "";
		try {
			strSql = "select a.fcollateralcode  from "
					+ pub.yssGetTableName("tb_data_seclendtrade")// 循环券商下的每一个借入的交易数据获取抵押物代码获取抵押物净值
					+ " a where a.fbargaindate = " + dbl.sqlDate(dDate)
					+ " and a.ftradetypecode = "
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)
					+ " and a.fbrokercode = " + dbl.sqlString(sbroker);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				dSingerTotalY=YssD.add(dSingerTotalY,getMortgageByCollCode(rs.getString("fcollateralcode"),dDate));
			
			}
		}catch (Exception e) {
			throw new YssException("计算抵押物净值出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return dSingerTotalY;
	}

	/**说明：使用抵押物代码获取抵押物净值
	 * @param collateralcode	抵押物代码
	 * @param dDate				日期  
	 * @return
	 * @throws YssException 
	 */
    private double getMortgageByCollCode(String collateralcode, Date dDate) throws YssException {
		ResultSet rs=null;
		String strSql="";
		double dCollValueY=0;
		try{
			strSql = "select a.fcollateralcode,a.ftransfertype,a.fportcode from " + pub.yssGetTableName("tb_Data_CollateralAdd") + " a where  a.finout=1 and a.fcollateralcode="
					+ dbl.sqlString(collateralcode);
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				if(rs.getString("ftransfertype").equals("组合")){	  //抵押物净值是组合就取今日的组合净值
					dCollValueY=YssD.add(dCollValueY, getMortgageForPort(collateralcode,rs.getString("Fportcode"),dDate));
				}else if(rs.getString("ftransfertype").equals("证券")){//抵押物类型是证券就循环抵押物证券表获取数量*行情
					dCollValueY=YssD.add(dCollValueY, getMortgageSecurity(collateralcode,rs.getString("Fportcode"),dDate));
				}else if(rs.getString("ftransfertype").equals("现金")){//抵押物类型是现金就循环买一个账户获取本位币金额
					dCollValueY=YssD.add(dCollValueY, getMortgageCash(collateralcode,rs.getString("Fportcode"),dDate));
				}
			}
		}catch (Exception e) {
			throw new YssException("使用抵押物代码计算抵押物净值出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return dCollValueY;
	}



	private double getMortgageCash(String collateralcode, String portcode,
			Date dDate) throws YssException {
		ResultSet rs=null;
		ResultSet rsSub=null;
		String strSql="";
		String sSubSql="";
		double dCollValueCash=0;
		StgSecRecPay secRecPay=new StgSecRecPay();
		secRecPay.setYssPub(this.pub);
		try{
			strSql = "select distinct b.fportcurybal,a.fcashacccode,b.fportcode from "+pub.yssGetTableName("tb_data_collateralacc")
				+" a left join (select b1.fportcurybal,b1.fportcode,b1.fcashacccode from "+pub.yssGetTableName("tb_stock_cash")
				+" b1 where b1.fportcode = "+dbl.sqlString(portcode)
				+" and b1.fstoragedate = "+dbl.sqlDate(dDate)
				+") b on a.fcashacccode=b.fcashacccode  where a.fcollateralcode = "+dbl.sqlString(collateralcode)
				+" and a.finout = 1";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				dCollValueCash=YssD.add(dCollValueCash,rs.getDouble("fportcurybal"));
				}
		}catch (Exception e) {
			throw new YssException("使用抵押物代码计算抵押物净值出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return dCollValueCash;
	}

	private double getMortgageSecurity(String collateralcode, String portcode,
			Date dDate) throws YssException {
		ResultSet rs=null;
		ResultSet rsSub=null;
		String strSql="";
		String sSubSql="";
		double dCollValueSec=0;
		StgSecRecPay secRecPay=new StgSecRecPay();
		secRecPay.setYssPub(this.pub);
		try{
			strSql = " select a.fsecuritycode,a.famount from "
					+ pub.yssGetTableName("tb_data_collateralsec")
					+ " a where a.finout=1 and famount <> 0 and a.fcollateralcode="+dbl.sqlString(collateralcode);
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				double dMarketPirce=secRecPay.getValMktPriceBySecAndPort(portcode,rs.getString("fsecuritycode"),dDate);//获取估值行情
				dCollValueSec=YssD.add(dCollValueSec, YssD.mul(dMarketPirce, rs.getDouble("famount")));
				}
		}catch (Exception e) {
			throw new YssException("使用抵押物代码计算抵押物净值出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return dCollValueSec;
	}

	private double getMortgageForPort(String collateralcode, String portcode,
			Date dDate) throws YssException {
		ResultSet rs=null;
		String strSql="";
		double dCollValuePort=0;
		try{
			strSql = " select a.fportmarketvalue from "+pub.yssGetTableName("tb_data_NavData")
					+" a where FNAVDate = "+dbl.sqlDate(dDate)
					+" and FPortCode = "+dbl.sqlString(portcode)
					+" and FReTypeCode = 'Total' and FKeyCode = 'TotalValue' order by FOrderCode";
			rs = dbl.openResultSet(strSql);
			if(rs.next()){
				dCollValuePort=rs.getDouble("fportmarketvalue");
				}
		}catch (Exception e) {
			throw new YssException("使用抵押物代码计算抵押物净值出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return dCollValuePort;
	}

	/** 循环当天券商下的每一笔交易产生借入证券的净值
	 @return
	 * @throws YssException 
	 */
    private double getLendValueX(String sbroker, Date dDate) throws YssException {
		double dSingerTotalX = 0;// 单笔交易的借入净值X
		ResultSet rs = null;
		ResultSet subRs = null;
		String strSql = "";
		String strSub = "";
		try {
			strSql = "select a.fsecuritycode  from "
					+ pub.yssGetTableName("tb_data_seclendtrade")// 循环券商下的每一个借入的交易数据获取证券代码取借入本位币成本
					+ " a where a.fbargaindate = " + dbl.sqlDate(dDate)
					+ " and a.ftradetypecode = "
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow)
					+ " and a.fbrokercode = " + dbl.sqlString(sbroker);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				strSub = "select a.fportcurybal  from "
						+ pub.yssGetTableName("tb_stock_secrecpay")
						+ " a where a.fstoragedate = " + dbl.sqlDate(dDate)
						+ " and a.fanalysiscode2 =" + dbl.sqlString(sbroker)
						+ " and a.fsubtsftypecode = "
						+ dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BSC)
						+ " and a.fsecuritycode = "
						+ dbl.sqlString(rs.getString("fsecuritycode"));
				subRs=dbl.openResultSet(strSub);
				if(subRs.next()){
					dSingerTotalX=YssD.add(dSingerTotalX,subRs.getDouble("fportcurybal"));
				}
			}

		} catch (Exception e) {
			throw new YssException("计算借入净值出错");
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(subRs);
		}
		return dSingerTotalX;
	}

	private String getBrokersByPortAry(String portcode, java.util.Date dDate) {
		String sPort = portcode;
		/*for (int i = 0; i <= portAry.length; i++) {
			sPort = sPort + dbl.sqlString(portAry[i]) + ",";
		}
		sPort = sPort.substring(0, sPort.length() - 1);// 去除逗号
*/		ResultSet rs = null;
		String strSql = "";
		String sBrokers = "";
		try {
			strSql = "select distinct a.fanalysiscode2 as Broker from "
					+ pub.yssGetTableName("Tb_Stock_Security")
					+ " a join (select distinct FStorageDate,fsecuritycode,FCuryCode,fanalysiscode1,fanalysiscode2 from "
					+ pub.yssGetTableName("Tb_Stock_SecRecPay")
					+ " a1 where Fstoragedate = "
					+ dbl.sqlDate(dDate)
					+ " and a1.fportcode in ("
					+ dbl.sqlString(sPort)
					+ ") and a1.fsubtsftypecode="
					+ dbl.sqlString(YssOperCons.YSS_SECLEND_SUBDBLX_BSC)
					+ ") b on a.FStorageDate =b.FStorageDate and a.fsecuritycode =b.fsecuritycode   and a.FCuryCode = b.FCuryCode"
					+ " and a.fanalysiscode1 =b.fanalysiscode1 and a.fanalysiscode2 = b.fanalysiscode2"
					+ " where a.Fstoragedate = " + dbl.sqlDate(dDate)
					+ " and a.fportcode in (" + dbl.sqlString(sPort) + ")";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				sBrokers = sBrokers + rs.getString("Broker") + ",";
			}
		} catch (Exception e) {

		} finally {
			dbl.closeResultSetFinal(rs);
		}

		return getSubString(sBrokers);
	}

	/*
	 * 去除逗号的函数 * @param sSubString
	 * 
	 * @return
	 */
	public String getSubString(String sSubString) {
		if (sSubString.trim().length() > 0) {
			sSubString = sSubString.substring(0, sSubString.length() - 1);
		}
		return sSubString;
	}
	// end by zhouxiang 2010.12.1
	// 证券借贷资产估值---------------------------------------

	/**
     * 多class净值统计，add by fangjiang 2011.10.26 STORY #1589 
     * @throws YssException
     */
    private void stgMultiClassNetValue(String sportCode, java.util.Date dDate) throws
        YssException {
        CtlNavRep navrep = null;
        try {        	
            navrep = new CtlNavRep();
            navrep.setPortCode(sportCode);
            navrep.setDDate(dDate);
            navrep.setYssPub(pub);
            navrep.dealMultiClass();
        } catch (Exception e) {
            throw new YssException("多class净值统计失败!\n", e);
        }
    }
    
    /** 
     * add by zhouwei 20120405 计算证券的管理费用：昨日市值*费率，并重新统计运营应收应付库存
    * @Title: stgSecManageFee 
    * @Description: TODO
    * @param @param sportCode
    * @param @param dDate
    * @param @throws YssException    设定文件 
    * @return void    返回类型 
    * @throws 
    */
    private void stgSecManageFee(String sportCode, java.util.Date dDate) throws YssException{
    	ArrayList investList=new ArrayList();
    	ArrayList IVPayCatCodeList=new ArrayList();
    	String IVPayCatCode="";//收支品种
    	try{
    		investList=this.getListOfSecManageFee(sportCode, dDate,IVPayCatCodeList);//得到结果集
    		for(int i=0,count=IVPayCatCodeList.size();i<count;i++){
    			IVPayCatCode+=(String)IVPayCatCodeList.get(i)+",";
    		}
    		if(IVPayCatCode.length()>1){
    			IVPayCatCode=IVPayCatCode.substring(0,IVPayCatCode.length()-1);
    		}
    		InvestPayAdimin investAdmin=new InvestPayAdimin();
    		investAdmin.setYssPub(this.pub);
    		investAdmin.setList(investList);
    		//保存运营应收应付记录
    		investAdmin.insert(dDate, dDate,
    				YssOperCons.YSS_ZJDBLX_Pay, YssOperCons.YSS_ZJDBZLX_IV_Pay, IVPayCatCode,
    				sportCode, "", "","",0,"",YssOperCons.YSS_INVESTRECPAY_RELATYPE_SECMAFEE+",secValFee");
    	}catch (Exception e) {
    		throw new YssException("保存证券管理费用出错！", e);
		}
    }
    /** 
     * addby zhouwei 20120401 
     * 根据通参设置，得到应收证券管理费的集
    * @Title: getListOfSecManageFee 
    * @Description: TODO
    * @param @param sportCode
    * @param @param dDate
    * @param @return
    * @param @throws YssException    设定文件 
    * @return ArrayList    返回类型 
    * @throws 
    */
    private ArrayList getListOfSecManageFee(String sportCode, java.util.Date dDate,ArrayList IVPayCatCodeList) throws YssException{
    	String strSql="";
    	ResultSet rs=null;    	
    	ArrayList secList=new ArrayList();
    	InvestPayRecBean investPayRec = null;
    	double baseCuryRate = 0;
        double portCuryRate = 0;
        ArrayList investList=new ArrayList();
        String perExpCode="";//比率公式
        String roundCode="";//舍入条件
        String periodCode="";//期间代码
    	try{
   		 	 PeriodBean Period = new PeriodBean();
   		 	 Period.setYssPub(pub);
    		 CtlPubPara pubPara=new CtlPubPara();
             pubPara.setYssPub(this.pub);
             //获取证券代码和比率公式，舍入条件的对应关系
             Map secManageFeeMap=pubPara.getMapOfSecManageFeeSet(sportCode, secList);
             EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
             rateOper.setYssPub(pub);           
             int secCount=secList.size();
             for(int i=0;i<secCount;i++){
        		 String key=(String)secList.get(i);
        		 String[] arr=((String)secManageFeeMap.get(key)).split("\t");
        		 IVPayCatCodeList.add(arr[3]);
            	 //查询净值数据表中该证券昨日的市值，管理费用=昨日市值*费率
            	 strSql="select * from "+pub.yssGetTableName("tb_data_navdata")
            	       +" where FNAVDate="+dbl.sqlDate(YssFun.addDay(dDate, -1))
            	       +" and FPortCode="+dbl.sqlString(sportCode)
            	       +" and FKeyCode="+dbl.sqlString((String)secList.get(i));
            	 rs=dbl.openResultSet(strSql);
            	 if(rs.next()){
            		 investPayRec=new InvestPayRecBean();   
            		 baseCuryRate = this.getSettingOper().getCuryRate(dDate,
            	                rs.getString("FCuryCode"), sportCode,
            	                YssOperCons.YSS_RATE_BASE);

            	     rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
            	                                      sportCode);
            	     portCuryRate = rateOper.getDPortRate();
            		 investPayRec.setFIVPayCatCode(arr[3]);//收支品种代码
            		 investPayRec.setTradeDate(dDate);
            		 investPayRec.setPortCode(sportCode);
            		 investPayRec.setAnalysisCode1(" ");
            		 investPayRec.setTsftTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
            		 investPayRec.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_IV_Pay);
            		 investPayRec.setCuryCode(rs.getString("FCuryCode"));
            		 perExpCode=arr[0];
            		 roundCode=arr[1];
            		 double feeMoney=-
            			 this.getSettingOper().calMoneyByPerExp(perExpCode,
                                 roundCode, rs.getDouble("FMarketValue"),//市值
                                 dDate);
            		 periodCode=arr[2];
                     Period.setPeriodCode(periodCode);
                     if (Period.getPeriodCode() == null || Period.getPeriodCode().trim().equals("null") ||
                          Period.getPeriodCode().trim().equalsIgnoreCase("")) {
                          throw new YssException("请先维护" + periodCode +
                                                 "的期间设置");
                     }
                     Period.getSetting();//获取期间对象
                     //如果periodType类型是1，则是实际天数类型，需要取得当年的实际天数；
                     if (Period.getPeriodType() == 1) {
                     	//如果是闰年，实际天数为366
                     	if(YssFun.isLeapYear(dDate)) {
                     		feeMoney = YssD.div(feeMoney, 366); //费用除以期间设置中每年天数
                     	}
                     	//如果不是闰年，实际天数为365
                     	else{
                     		feeMoney = YssD.div(feeMoney, 365); //费用除以期间设置中每年天数
                     	}
                     }
                     else {
                    	 feeMoney = YssD.div(feeMoney, Period.getDayOfYear()); //费用除以期间设置中每年天数
                     }
                     feeMoney = this.getSettingOper().reckonRoundMoney(roundCode, feeMoney);//舍入操作 
                     if(feeMoney==0){
                    	 continue;
                     }
            		 investPayRec.setMoney(feeMoney);
            		 investPayRec.setBaseCuryRate(baseCuryRate);
            		 investPayRec.setPortCuryRate(portCuryRate);
            		 investPayRec.setBaseCuryMoney(this.getSettingOper().calBaseMoney(investPayRec.getMoney(),
     	                baseCuryRate, 2));
            		 investPayRec.setPortCuryMoney(this.getSettingOper().calPortMoney(investPayRec.getMoney(),
     	                baseCuryRate, portCuryRate,
     	                rs.getString("FCuryCode"), dDate, sportCode, 2));
            		 investPayRec.setRelaType(YssOperCons.YSS_INVESTRECPAY_RELATYPE_SECMAFEE);
            		 investPayRec.setCheckState(1);
            		 investList.add(investPayRec);
            	 }
            	 dbl.closeResultSetFinal(rs);            	
             }
             
             //add by guolongchao 20120503 博时每次资产估值时提固定一笔费用(根据开放日提)----start
        	 String secValFee = pubPara.getSecValFee(sportCode);
        	 if(secValFee!=null&&secValFee.trim().length()>0)
        	 {
        		 String[] fee=secValFee.split("\t");
        		 BaseOperDeal deal=new BaseOperDeal();
        		 deal.setYssPub(this.pub);
        		 boolean flag=deal.isWorkDay(fee[2], dDate, 0);//判断是否为工作日
        		 if(flag)
        		 {        			
        			 IVPayCatCodeList.add(fee[1]);//收支品种代码
            		 investPayRec=new InvestPayRecBean();   
                	 baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                			                   fee[4], sportCode,
                	                YssOperCons.YSS_RATE_BASE);
                	 rateOper.getInnerPortRate(dDate, fee[4],sportCode);
                	 portCuryRate = rateOper.getDPortRate();
                     investPayRec.setFIVPayCatCode(fee[1]);//收支品种代码
                	 investPayRec.setTradeDate(dDate);
                	 investPayRec.setPortCode(sportCode);
                	 investPayRec.setAnalysisCode1(" ");
                	 investPayRec.setTsftTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
                	 investPayRec.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_IV_Pay);
            		 investPayRec.setCuryCode(fee[4]);                		 
            		 investPayRec.setMoney(Double.parseDouble(fee[3]));//固定费用金额
            		 investPayRec.setBaseCuryRate(baseCuryRate);
            		 investPayRec.setPortCuryRate(portCuryRate);
            		 investPayRec.setBaseCuryMoney(this.getSettingOper().calBaseMoney(investPayRec.getMoney(), baseCuryRate, 2));
            		 investPayRec.setPortCuryMoney(this.getSettingOper().calPortMoney(investPayRec.getMoney(), baseCuryRate, portCuryRate,
            				 fee[4], dDate, sportCode, 2));
            		 investPayRec.setRelaType("secValFee");
            		 investPayRec.setCheckState(1);
            		 investList.add(investPayRec);        			            			 
        		 }
        	 }
        	//add by guolongchao 20120503 博时每次资产估值时提固定一笔费用(根据开放日提)----end
        	 
    	}catch (Exception e) {
    		 throw new YssException("计算证券管理费用出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);			
		}
    	return investList;
    }
}
