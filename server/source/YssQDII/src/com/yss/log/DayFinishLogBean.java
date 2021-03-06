package com.yss.log;
/***add by zhouxiang 2010.11.23 
 * 日终处理模块中的日志做单独的明细处理
 * 
 */

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import oracle.jdbc.*;
import oracle.sql.*;

import com.yss.dsub.BaseBean;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.dsub.YssPub;
import com.yss.main.dao.IDataSetting;
import com.yss.main.dao.IYssConvert;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.projects.para.set.pojo.BEN_PLUGIN_LOG;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

public class DayFinishLogBean extends BaseDataSettingBean implements
		IYssConvert, IDataSetting {

	private String logCode = "";//日志编号
    private java.util.Date logDate; //查询日志的日期
    private String logTime = "";//查询的时间
    private String operUserCode = "";//查询的用户ID
    //private String operUserName = "";//查询的用户名
    private String moduleCode = "";//模块代码
    //private String moduleName = "";//模块名称
    private String funCode = "";//功能代码
    //private String funName = "";//功能名称
    private int operType;//操作类型 增删查改
    //private String operTypeName = "";
    private String operResultCode = "";//操作结果 ，成功 失败
    //private String operResultName = "";
    private String refInvokeCode = "";//功能调用代码
    //private String refInvokeParam = "";//功能调用参数
    private String logData1 = "";
    private String logData2 = "";
    private String portcodes="";//组合代码
    private java.util.Date startDate ; //起始日期
    private java.util.Date endDate ; //结束日期
    //private String delList = "";
    private String operItem="";//操作项目 如：资产估值，业务处理
    private String itemDetail="";//项目明细
    private String accType=" ";//账户类型、子类型
    //---add by songjie 2012.08.15 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    private java.util.Date beginTime;//开始时间
    private java.util.Date endTime;//结束时间
    private String dealTime = "";//处理时间
	private static HashMap hmVoc = null;//用于保存 相关词汇代码、词汇名称
	private static HashMap hmMenu = null;//用于保存 相关菜单代码、菜单名称
	private String logSumCode = "";//汇总数据对应的编号
	private ArrayList alPojo = new ArrayList();//用于保存日志数据
	
	public void addAlPojo(BEN_PLUGIN_LOG pluginLog){
		alPojo.add(pluginLog);
	}
	
	public ArrayList getAlPojo(){
		return alPojo;
	}
	
    public void setLogSumCode(String logSumCode) {
		this.logSumCode = logSumCode;
	}
    
    public String getLogSumCode(){
    	return this.logSumCode;
    }

	public java.util.Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(java.util.Date beginTime) {
		this.beginTime = beginTime;
	}
	//获取结束时间
	public java.util.Date getEndTime() {
		return endTime;
	}
    //设置结束时间
	public void setEndTime(java.util.Date endTime) {
		this.endTime = endTime;
	}

	public String getDealTime() {
		return dealTime;
	}

	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	//---add by songjie 2012.08.15 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	
	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getLogCode() {
		return logCode;
	}

	public void setLogCode(String logCode) {
		this.logCode = logCode;
	}

	public java.util.Date getLogDate() {
		return logDate;
	}

	public void setLogDate(java.util.Date logDate) {
		this.logDate = logDate;
	}

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public String getOperUserCode() {
		return operUserCode;
	}

	public void setOperUserCode(String operUserCode) {
		this.operUserCode = operUserCode;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getFunCode() {
		return funCode;
	}

	public void setFunCode(String funCode) {
		this.funCode = funCode;
	}

	public int getOperType() {
		return operType;
	}

	public void setOperType(int operType) {
		this.operType = operType;
	}

	public String getOperResultCode() {
		return operResultCode;
	}

	public void setOperResultCode(String operResultCode) {
		this.operResultCode = operResultCode;
	}

	public String getRefInvokeCode() {
		return refInvokeCode;
	}

	public void setRefInvokeCode(String refInvokeCode) {
		this.refInvokeCode = refInvokeCode;
	}

	public String getLogData1() {
		return logData1;
	}

	public void setLogData1(String logData1) {
		this.logData1 = logData1;
	}

	public String getLogData2() {
		return logData2;
	}

	public void setLogData2(String logData2) {
		this.logData2 = logData2;
	}

	public String getPortcodes() {
		return portcodes;
	}

	public void setPortcodes(String portcodes) {
		this.portcodes = portcodes;
	}

	public java.util.Date  getStartDate() {
		return startDate;
	}

	public void setStartDate(java.util.Date  startDate) {
		this.startDate = startDate;
	}

	public java.util.Date  getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Date  endDate) {
		this.endDate = endDate;
	}

	public String getOperItem() {
		return operItem;
	}

	public void setOperItem(String operItem) {
		this.operItem = operItem;
	}

	public String getItemDetail() {
		return itemDetail;
	}

	public void setItemDetail(String itemDetail) {
		this.itemDetail = itemDetail;
	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub

	}

	public void insertLog(Object obj) throws YssException {
		ResultSet rs = null;
		Connection conn = null;
		//add by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
		boolean bTrans = false;
		//--- add by songjie 2012.06.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
		String dealT = "";//处理时长
		PortfolioBean port = new PortfolioBean();
		String assetType = "";//资产类型
		String assetTypeName = "";//资产类型名称
		String assetCode = "";//套帐代码
		String operTypeName = "";//操作类型名称
		BEN_PLUGIN_LOG pluginLog = null;
		String[] errorInfos = null;
		String errorInfo = "";
		//--- add by songjie 2012.06.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		try {
			/**
			 *  shashijie :BUG #1060 多条（五十条）债券信息同时进行计提利息时，报错。 现金计息也再次报错
			 */
			String[] accTypeValue = null;
			if (accType==null || accType.trim().equalsIgnoreCase("")) {
				//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
				accTypeValue = new String[itemDetail.split(YssCons.YSS_LINESPLITMARK).length];
			} else {
				//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
				accTypeValue = accType.split(YssCons.YSS_LINESPLITMARK);
			}
			/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~end 华丽的分割线~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
			BaseBean baseBean = (BaseBean) obj;
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			//add by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
			bTrans = true;
			String[] portList = this.portcodes.split(",");//组合代码
			//--- add by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符 start---//
			String[] itemDetail = this.itemDetail.split(YssCons.YSS_LINESPLITMARK);//明细代码
			if(this.operResultCode.equals("0") && itemDetail.length > 1){
				errorInfos = this.itemDetail.split("\r\n");
				if(errorInfos.length >1){
					itemDetail = errorInfos[0].split(YssCons.YSS_LINESPLITMARK);
					for(int k = 0; k < errorInfos.length ; k++){
						if(k != 0){
							errorInfo += "\r\n" + errorInfos[k];
						}
					}
				}
			}
			
			if(hmVoc == null){
				//获取词汇数据
				hmVoc = getVocName("prt_assettype,opertype,val_business,invest_opertype,dayfinishopertype,val_check");
			}
			if(hmMenu == null){
				hmMenu = getMenuBarInfo();//获取菜单条名称
			}
			
			dealT = YssFun.dateTimeDiff(beginTime, endTime);//计算处理时长
			operTypeName = (String)((HashMap)hmVoc.get("opertype")).get(this.operType + "");//操作类型名称
			//--- add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//

			for (int i = 0; i < portList.length; i++) {//循环组合
				//---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                port.setYssPub(pub);
                port.setPortCode(portList[i]);
                port.getSetting();//获取组合设置数据
                assetType = port.getAssetType();//资产类型代码
                assetTypeName = (String)((HashMap)hmVoc.get("prt_assettype")).get(assetType);//资产净值名称
                YssFinance cw = new YssFinance();
                cw.setYssPub(pub);
                assetCode = cw.getCWSetCode(portList[i]);//套帐代码
                //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
				for (int j = 0; j < itemDetail.length; j++) {//循环明细
		            //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					pluginLog = new BEN_PLUGIN_LOG();
					
					pluginLog.setOperDate(startDate);
					pluginLog.setOperTime(new java.util.Date());//创建时间
					pluginLog.setC_PRODUCT_CODE("4.0");//产品代码
					pluginLog.setC_PRODUCT_NAME("估值系统V4.0");//产品名称
					pluginLog.setC_EXECUTE_CODE("");//执行代码
					pluginLog.setC_EXECUTE_NAME("");//执行名称
					pluginLog.setC_EW_TYPENAME("业务检查");//操作类型 如业务检查
					pluginLog.setC_OPER_TYPENAME(operTypeName);//操作类型  如 估值处理 或 TA业务
					pluginLog.setCreatorCode(pub.getUserCode());//创建人
					pluginLog.setCreatorName(pub.getUserName());//创建时间
					pluginLog.setMacClientIP(pub.getClientPCAddr());//主机IP
					pluginLog.setMacClientName(pub.getClientPCName());//主机用户名
					pluginLog.setMacClientAddr(pub.getClientMacAddr());//主机地址
					pluginLog.setExecuteTime(dealT);//执行时长
					pluginLog.setExecuteStartDate(beginTime);//开始时间
					pluginLog.setExecuteEndDate(endTime);//结束时间
					pluginLog.setBussinessModule((String)hmMenu.get(baseBean.getFunName()));//业务模块
					
					if(this.operItem.equals("sum") && itemDetail[j].trim().length() > 0){
						//---edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A start---//
						pluginLog.setRefNum("[root]");//若为汇总数据，则refNum 应为 [root]
						pluginLog.setLogSumCode("sum:"+ this.logSumCode);
						//---edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A end---//
					}else{
						pluginLog.setRefNum(this.operItem.equals("sum")? "[root]" : ("sum:"+ this.logSumCode));
						pluginLog.setLogSumCode(this.operItem.equals("sum")? ("sum:"+ this.logSumCode) : "");
					}

					if(this.operItem.equals("sum")){//若为汇总数据
						pluginLog.setC_RESULT_TYPENAME(" ");
						pluginLog.setC_EXECUTE_STATE(" ");
						//edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A 
						//itemDetail[j] 改为 portList[i] 应用组合代码判断
						if(this.operItem.equals("sum") && portList[i].trim().length() > 0){
							pluginLog.setC_PORT_CODE(pub.getPrefixTB() + "-" + portList[i]);//组合群代码-组合代码  问题：取不到组合代码
						}else{
							pluginLog.setC_PORT_CODE(" ");//组合群代码-组合代码  问题：取不到组合代码
						}

						pluginLog.setAccType(" ");
						pluginLog.setC_RESULT_INFO(itemDetail[j]);//执行结果详细信息		
						pluginLog.setAssetType(" ");//组合设置对应的资产类型
						pluginLog.setBookSetCode(" ");//套帐代码
						if(this.operResultCode.equals("0")){
							pluginLog.setC_RESULT_TYPENAME("失败");//执行结果   正常  或  提醒
						}
					}else{
						if(this.operResultCode.equals("0"))
						{
							pluginLog.setC_RESULT_TYPENAME("失败");//执行结果   正常  或  提醒
						}
						//20130417 modified by liubo.Story #3528
						//operResultCode的值为2，设置C_RESULT_TYPENAME的值为提醒。
						//在业务日志界面，提醒状态的日志数据，字体颜色会改为蓝色
						//=========================
						else if (this.operResultCode.equals("2"))
						{
							pluginLog.setC_RESULT_TYPENAME("提醒");
						}
						else
						{
							pluginLog.setC_RESULT_TYPENAME("成功");//执行结果   正常  或  提醒
						}
						//===========end==============
						
						pluginLog.setC_EXECUTE_STATE("已完成");//执行状态  已完成  或 进行中
						
						if(baseBean.getFunName() == null){
							continue;
						}else{
							if(baseBean.getFunName().equalsIgnoreCase("OperDeal")){
								pluginLog.setBussinessSubModule(
										(String)((HashMap)hmVoc.get("invest_opertype")).get(this.operItem));//业务子模块
							}else if(baseBean.getFunName().equalsIgnoreCase("incomecalculate") ||
									 baseBean.getFunName().equalsIgnoreCase("storagestat") ||
									 baseBean.getFunName().equalsIgnoreCase("incomepaid") ||
									 baseBean.getFunName().equalsIgnoreCase("valuation")){
									pluginLog.setBussinessSubModule(
											(String)((HashMap)hmVoc.get("dayfinishopertype")).get(this.operItem));//业务子模块
							}else if(baseBean.getFunName().equalsIgnoreCase("voucherBuild") || //凭证生成
									 baseBean.getFunName().equalsIgnoreCase("executeSchedule") || //调度方案执行
									 baseBean.getFunName().equalsIgnoreCase("vchbuildproject") || //凭证方案执行
									 baseBean.getFunName().equalsIgnoreCase("GuessValue") || //财务估值表生成
									 baseBean.getFunName().equalsIgnoreCase("navdata") || //净值统计表生成
									 baseBean.getFunName().equalsIgnoreCase("vchcheck") || //凭证检查
									 baseBean.getFunName().equalsIgnoreCase("vchinter") || //凭证导出 
									 //---add by songjie 2012.12.31 STORY #2343 QDV4建行2012年3月2日04_A start---//
									 baseBean.getFunName().equalsIgnoreCase("interfacedeal") || //接口处理
									 baseBean.getFunName().equalsIgnoreCase("tradesettle") || //交易结算
									 baseBean.getFunName().equalsIgnoreCase("tatradesettleview")){ //TA交易结算
								     //---add by songjie 2012.12.31 STORY #2343 QDV4建行2012年3月2日04_A end---//
								pluginLog.setBussinessSubModule(this.operItem);//业务子模块
							}else if(baseBean.getFunName().equalsIgnoreCase("schproject")){//调度方案执行
								pluginLog.setBussinessSubModule("");//业务子模块
							} else{
								pluginLog.setBussinessSubModule(
										(String)((HashMap)hmVoc.get("val_business")).get(this.operItem));//业务子模块
							}
						}
						
						pluginLog.setC_PORT_CODE(pub.getPrefixTB() + "-" + portList[i]);//组合群代码-组合代码  问题：取不到组合代码
						if(accTypeValue.length <= j){
							pluginLog.setAccType("");
						}else{
							pluginLog.setAccType((accTypeValue[j] == null) ? "" : accTypeValue[j]);
						}
						
						if(baseBean.getFunName().equalsIgnoreCase("storagestat")){
							pluginLog.setC_RESULT_INFO(
							(String)((HashMap)hmVoc.get("dayfinishopertype")).get(this.operItem) + "...\r\n" + itemDetail[j]);//执行结果详细信息						
						}else{
							pluginLog.setC_RESULT_INFO(itemDetail[j] + errorInfo);//执行结果详细信息		
						}
						
						pluginLog.setAssetType(assetTypeName);//组合设置对应的资产类型
						pluginLog.setBookSetCode(assetCode);//套帐代码
					}
					
			        /**Start 20130901 added by liubo.Story #4281*/
					
					//当QDII估值系统的业务日志的保存模式为不保存任何数据时，则不保存任何业务日志数据
					if (!YssCons.YSS_BusinessLog_GenerateType.equals("1"))
					{
						//当QDII估值系统的业务日志的保存模式为只保存失败、提醒的数据时，
						//判断BEN_PLUGIN_LOG对象的getC_RESULT_TYPENAME方法
						//值为成功或者正常，则该条业务日志数据不进行保存
						//其他情况则需要进行保存
						if (YssCons.YSS_BusinessLog_GenerateType.equals("3"))
						{
							if (pluginLog.getC_RESULT_TYPENAME().equals("成功") || pluginLog.getC_RESULT_TYPENAME().equals("正常"))
							{
								
							}
							else
							{
								alPojo.add(pluginLog);
							}
						}
						//其他模式，即第二种，保存所有数据的模式。
						//该模式情况下，保存所有的业务日志数据
						else
						{
							alPojo.add(pluginLog);
						}
					}
					/**End 20130901 added by liubo.Story #4281*/
				}
			}
			
			if(this.operItem.equals("sum") || 
			   //---edit by songjie 2012.12.31 STORY #2343 QDV4建行2012年3月2日04_A start---//
			   (baseBean.getFunName() != null && //添加 接口处理的情况
			   (baseBean.getFunName().equalsIgnoreCase("storagestat")|| 
				
				baseBean.getFunName().equalsIgnoreCase("interfacedeal")))){
				//---edit by songjie 2012.12.31 STORY #2343 QDV4建行2012年3月2日04_A end---//
				saveList(alPojo);//保存日终处理日志数据到预警日志表
				alPojo.clear();
			}
			
			conn.commit();
			//add by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
			//add by songjie 2012.12.25 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
			dbl.endTransFinal(conn,bTrans);
		}
	}
	
	/**
	 * add by songjie 2012.08.28 
	 * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	 * @param endTime
	 * @throws YssException
	 */
	private void updateSumInfo(Date endTime) throws YssException{
		String strSql = "";
		ResultSet rs = null;
	    java.util.Date startTime = null;//开始时间
		try{
			strSql = " select D_EXECUTE_START from T_Plugin_Log where FLogSumCode = " + 
			dbl.sqlString("sum:" + logSumCode) + " and C_REF_NUM = '[root]'";
			
			//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换为日志数据库链接
			rs = dblBLog.openResultSet(strSql);
			if(rs.next()){
				startTime = YssFun.parseDate(rs.getDate("D_EXECUTE_START") + " " + rs.getTime("D_EXECUTE_START"),"yyyy-MM-dd hh:mm:ss");
			}
			//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换为日志数据库链接
			dblBLog.closeResultSetFinal(rs);
			rs = null;
			
			strSql = " update T_Plugin_Log set D_EXECUTE_END = " + dbl.sqlDate(endTime,true) + 
			" , C_EXECUTE_TIME = " + dbl.sqlString(YssFun.dateTimeDiff(startTime, endTime)) +
			((this.operResultCode.equals("0"))? " , C_RESULT_TYPENAME = '失败' " : "") +
			" where FLogSumCode = " + dbl.sqlString("sum:" + this.logSumCode) + " and C_REF_NUM = '[root]'";
			//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换为日志数据库链接
			dblBLog.executeSql(strSql);
		}catch(Exception e){
			throw new YssException(e.getMessage());
		}finally{
			//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换为日志数据库链接
			dblBLog.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2012.08.15
	 * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	 * 获取词汇名称
	 * @param FVocCode
	 * @return
	 * @throws YssException
	 */
	private HashMap getVocName (String FVocTypeCode) throws YssException{
    	ResultSet rs = null;
    	String strSql = "";
    	HashMap hmVoc = new HashMap();
    	HashMap hmSubVoc = null;
    	try{
    		strSql = " select FVocTypeCode, FVocCode, FVocName from Tb_Fun_Vocabulary where " + 
    		         " FVocTypeCode in( " + operSql.sqlCodes(FVocTypeCode) + " ) and FCheckState = 1 ";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			if(hmVoc.get(rs.getString("FVocTypeCode"))== null){
    				hmSubVoc = new HashMap();
    			}else{
    				hmSubVoc = (HashMap)hmVoc.get(rs.getString("FVocTypeCode"));
    			}
    			hmSubVoc.put(rs.getString("FVocCode"), rs.getString("FVocName"));
    			hmVoc.put(rs.getString("FVocTypeCode"), hmSubVoc);
    		}
    		
    		return hmVoc;
    	}catch(Exception e){
    		throw new YssException("获取词汇数据报错！");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
	}
	
	/**
	 * add by songjie 2012.08.15 
	 * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	 * @return
	 * @throws YssException
	 */
    public HashMap getMenuBarInfo() throws YssException {
   	 	String strSql = "";
        ResultSet rs = null;
        HashMap hmMenu = new HashMap();
        try {
            strSql = " select * from Tb_Fun_Menubar a left join Tb_Fun_RefInvoke b " + 
                     " on a.FRefInvokeCode = b.FRefInvokeCode where a.fbarcode='dayfinish' " +
					 //edit by songjie 2013.03.15 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
                     " or a.fbargroupcode in('dayfinish','operplatform','voucher','settlecenter') order by a.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	hmMenu.put(rs.getString("FBarCode"), rs.getString("FBarName"));
            }

            return hmMenu;
        } catch (Exception ex) {
            throw new YssException("获取菜单条数据出错!");
        } finally {
            dbl.closeResultSetFinal(rs); 
        }
   }
	
	/**
	 * add by songjie 2012.08.15
	 * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 
	 * @return
	 * @throws YssException
	 */
    private String getLogCodes()throws YssException{
    	ResultSet rs = null;
    	String logCode ="";
    	try{
    		rs = dbl.openResultSet(
    			 " select to_char(SEQ_WP_LOG.NextVal) as LogCode from dual ");
    		if(rs.next()){
    			logCode = rs.getString("LogCode");
    		}
    		return logCode;
    	}catch(Exception e){
    		throw new YssException ("获取 Sequence SEQ_WP_LOG 值 失败 ");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
	
    /**
     * add by songjie 2012.08.14
     * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
     * @param alPojo
     * @throws YssException
     */
	private void saveList(ArrayList alPojo) throws YssException {
		boolean bTrans = false; // 代表是否开始了事务
		PreparedStatement pst = null;// 预处理语句
		Connection conn = null; // 获取数据库连接
		String strSql = "";// 查询字符串
		BEN_PLUGIN_LOG pluginLog = null;
		ResultSet rs = null;
		PreparedStatement pst2 = null;// 预处理语句
		String logId = "";
		CLOB clob = null;
		HashMap hmLogInfo = new HashMap();
		try {
			//edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A
			//替换为日志数据库链接
			conn = dblBLog.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			
			strSql = "INSERT INTO T_PLUGIN_LOG " 
			    + "( " 
			    + "D_BUSSINESS_DATE, " 
			    + "D_OPER_DATE, "
				+ "C_PRODUCT_CODE, " 
				+ "C_PRODUCT_NAME, " 
				+ "C_EXECUTE_CODE, " 
				+ "C_EXECUTE_NAME, "
				+ "C_EW_TYPENAME, " 
				+ "C_OPER_TYPENAME, " 
				+ "C_EXECUTE_STATE, "
				+ "C_RESULT_TYPENAME, " 
				+ "C_PORT_CODE, "//组合群-组合
				+ "D_EXECUTE_START, "// 执行开始时间
				+ "D_EXECUTE_END, "// 执行结束时间
				+ "C_EXECUTE_TIME, "// 执行时长
				+ "C_BUSSINESS_MODULE, "// 业务模块
				+ "C_BUSSINESS_SUB_MODULE, "// 业务子模块
				+ "C_CREATOR_CODE, "// 创建人
				+ "C_CREATOR_NAME, "//
				+ "C_ASSET_TYPE, "// 资产类型
				+ "C_BOOKSET_CODE, "// 套账号
				+ "C_REF_NUM, "// 关联类型
				+ "C_ACC_TYPE, "// 现金类型
				+ "C_MAC_IP, "// ip
				+ "C_MAC_NAME, " 
				+ "C_MAC_ADDR, " 
				+ "C_RESULT_INFO, "
				+ "S_LOG_ID,"// sequeence
				+ "FLOGSUMCODE "
				+ " )"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," 
				+ "?,?,?,?,?,?,?,?,?,?,EMPTY_CLOB(),?,?)";
			//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换为日志数据库链接
			pst = dblBLog.openPreparedStatement(strSql);
			strSql = "update t_plugin_log set C_RESULT_INFO=? where S_LOG_ID=?";
			//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A  替换为日志数据库链接
			pst2 = dblBLog.openPreparedStatement(strSql);
			for (int i = 0; i < alPojo.size(); i++) {
				logId = getLogCodes();//获取日志编号

				pluginLog = (BEN_PLUGIN_LOG) alPojo.get(i);
				pst.setDate(1, YssFun.toSqlDate(pluginLog.getOperDate()));
				pst.setTimestamp(2, new java.sql.Timestamp(pluginLog.getOperTime().getTime()));
				pst.setString(3, pluginLog.getC_PRODUCT_CODE());
				pst.setString(4, pluginLog.getC_PRODUCT_NAME());
				pst.setString(5, pluginLog.getC_EXECUTE_CODE());
				pst.setString(6, pluginLog.getC_EXECUTE_NAME());
				pst.setString(7, pluginLog.getC_EW_TYPENAME());
				pst.setString(8, pluginLog.getC_OPER_TYPENAME());
				pst.setString(9, pluginLog.getC_EXECUTE_STATE());
				pst.setString(10, pluginLog.getC_RESULT_TYPENAME());
				pst.setString(11, pluginLog.getC_PORT_CODE());
				pst.setTimestamp(12, new java.sql.Timestamp(
						             pluginLog.getExecuteStartDate().getTime()));
				pst.setTimestamp(13, new java.sql.Timestamp(
						             pluginLog.getExecuteEndDate().getTime()));
				pst.setString(14, pluginLog.getExecuteTime());
				pst.setString(15, (pluginLog.getBussinessModule() == null || 
						          pluginLog.getBussinessModule().equals("")) ? 
						          " " : pluginLog.getBussinessModule());
				pst.setString(16, (pluginLog.getBussinessSubModule() == null || 
						          pluginLog.getBussinessSubModule().equals("")) ? 
						          " " : pluginLog.getBussinessSubModule());
				pst.setString(17, (pluginLog.getCreatorCode() == null || 
						          pluginLog.getCreatorCode().equals("")) ? 
								  " " : pluginLog.getCreatorCode());
				pst.setString(18, pluginLog.getCreatorName());
				pst.setString(19, pluginLog.getAssetType());
				pst.setString(20, pluginLog.getBookSetCode());
				pst.setString(21, (pluginLog.getRefNum().trim().length() == 0) ? logId : pluginLog.getRefNum());
				pst.setString(22, pluginLog.getAccType());
				pst.setString(23, ((pluginLog.getMacClientIP() == null || 
						          pluginLog.getMacClientIP().equals("") || 
						          pluginLog.getMacClientIP().equals("null")) ? 
						          pub.getClientPCAddr() : pluginLog.getMacClientIP()));
				pst.setString(24, ((pluginLog.getMacClientName() == null || 
						          pluginLog.getMacClientName().equals("") || 
						          pluginLog.getMacClientName().equals("null")) ? 
						          pub.getClientPCName() : pluginLog.getMacClientName()));
				pst.setString(25, ((pluginLog.getMacClientAddr() == null || 
						          pluginLog.getMacClientAddr().equals("") || 
						          pluginLog.getMacClientAddr().equals("null")) ? 
						          pub.getClientMacAddr() : pluginLog.getMacClientAddr()));
				pst.setString(26, logId);
				pst.setString(27, (pluginLog.getLogSumCode().trim().length() == 0) ? logId : pluginLog.getLogSumCode());
				
				pst.addBatch();
				
				//---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	            Logger log = Logger.getLogger("D");
	            log.info("--------------------------------- DayFinishLogID : " + logId + " ---------------------------------");
				//---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				//获取 logId对应的日志详细数据
	            hmLogInfo.put(logId, pluginLog.getC_RESULT_INFO());
			}
			
			pst.executeBatch();
			
			String key = "";
			Iterator iter = hmLogInfo.keySet().iterator();
			while(iter.hasNext()){
				key = (String)iter.next();
				strSql = "select C_RESULT_INFO,S_LOG_ID from T_PLUGIN_LOG where S_LOG_ID = " + key;
				//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A  替换为日志数据库链接
				rs = dblBLog.openResultSet(strSql);
				if (rs.next()) {
					long id = rs.getLong("S_LOG_ID");
					//	//STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞 add by jsc
					//clob = ( (OracleResultSet) rs).getCLOB("C_RESULT_INFO");
					//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A  替换为日志数据库链接
					clob = dblBLog.CastToCLOB(rs.getClob("C_RESULT_INFO"));
					clob.putString(1, (String)hmLogInfo.get(key));//根据logId获取日志详细数据
					
					pst2.setClob(1, clob);
					pst2.setLong(2, id);
					pst2.addBatch();
				}
				//edit by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A 替换为日志数据库链接
				dblBLog.closeResultSetFinal(rs);
			}

			pst2.executeBatch();
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new YssException("保存日志到数据库出错");
		} finally {
		    //---edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A start---//
			dblBLog.closeResultSetFinal(rs);
			dblBLog.closeStatementFinal(pst, pst2);
			dblBLog.endTransFinal(conn, bTrans);
			//---edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A end---//
		}
	}
	
	/**@author zhouxiang 使用组合代码获取组合代码-名字 如：GFund 返回“GFund-XX基金组合”
	 * @param string
	 * @return
	 * @throws YssException 
	 *//*
	private String getPortCodeAndNameByCode(String portcode) throws YssException {
		ResultSet rs=null;
		String sqlStr="";
		try{
			sqlStr = "select distinct a.fportcode||'-'||a.fportname as fportcode from "
					+ pub.yssGetTableName("tb_para_portfolio")
					+ " a where a.fcheckstate=1 and a.fportcode="
					+ dbl.sqlString(portcode);
			rs = dbl.openResultSet(sqlStr);
			if(rs.next()){
				return rs.getString("Fportcode");
			}
		}catch(Exception e){
			throw new YssException("生成组合代码-名称报错");
		}
		return portcode;
	}*/

	public String addSetting() throws YssException {
		
		return null;
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub

	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub

	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub

	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub

	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * add by songjie 2012.08.27
	 * STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
	 * 获取 Sequence SEQ_SYS_LOGCODE_SUM value
	 * @return
	 * @throws YssException
	 */
    public String getLogSumCodes ()throws YssException{
    	ResultSet rs = null;
    	String logCode ="";
    	try{
    		rs = dbl.openResultSet(" select to_char(SEQ_SYS_LOGCODE_SUM.NextVal) as LogCode from dual ");
    		
    		if(rs.next()){
    			logCode = rs.getString("LogCode");
    		}
    		return logCode;
    	}catch(Exception e){
    		throw new YssException ("获取Sequence SEQ_SYS_LOGCODE_SUM 值 失败 ");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
}
