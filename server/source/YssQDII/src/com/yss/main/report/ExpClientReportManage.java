package com.yss.main.report;

import java.util.HashMap;

import com.yss.base.BaseAPOperValue;
import com.yss.dsub.BaseBean;
import com.yss.main.dao.IBuildReport;
import com.yss.main.dao.IClientReportView;
import com.yss.main.dao.IOperValue;
import com.yss.main.funsetting.FlowBean;
import com.yss.pojo.sys.YssStatus;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.*;

import com.yss.main.operdeal.report.repexp.ExpClientReport;
import com.yss.main.operdeal.report.repfix.RepOverage.RepOverage;

public class ExpClientReportManage extends BaseBean implements IClientReportView{
	private static final Object anObject = null;
	//private BaseAPOperValue baseAPOper=null;//基类
	private String beanid="";
	private java.util.Date startDate=null;//开始日期
	private java.util.Date endDate=null;//截止日期
	private String portCode="";//组合代码
	private String portName="";//组合名称
	private String assetGroupCode = "";
	private String assetGroupName = "";
	private int portCount = 1;//组合群下组合个数
	private String DpCode="";//报表代码	
	private String DpName="";//报表名称
	private String fileName = "";//外部文件名字
	private String sCuryAssetGroupCode = "";
	private String sRepParam = "";
	private CommonRepBean commonRepBean = null;
	
	public ExpClientReportManage(){
	}

	/**
	 * 获取报表的数据
	 */
	public String getReportData(String sReportType) throws YssException {
		// TODO Auto-generated method stub
		IOperValue iOV  = (IOperValue) pub.getOperDealCtx().getBean(
				beanid);
		String reportData="";
		String strGuessValueReq  = "";
		
		try {
			iOV.setYssPub(pub);
			//工银财务估值表，需要以套帐号为单位进行生成。若生成财务股指标，需要修改原始提示信息中的报表名。 20110523 Modified by liubo #850
			if(DpCode.equals("RepGYTest") && sReportType.equalsIgnoreCase("GYexp") ) //modify by huangqirong 2012-05-24 bug #4542
			{	
				//add by huangqirong 2012-05-24 bug #4542
				if(!(this.assetGroupCode.equalsIgnoreCase(pub.getAssetGroupCode()))){
					sCuryAssetGroupCode = pub.getAssetGroupCode();
					pub.yssLogon(this.assetGroupCode, pub.getUserCode());
				}
				YssFinance getBookSet = new YssFinance();
				getBookSet.setYssPub(pub);
				String setId = getBookSet.getBookSetId(this.portCode);
				//---end---
				//modify huangqirong 2012-05-24 bug #4542
				DpName = "";             
				//DpName = this.GetBookSetName(portCode).equals("")?"":this.GetBookSetName(portCode) + "财务估值表";
				DpName = this.GetBookSetName(portCode);
				strGuessValueReq = YssFun.formatDate(startDate, "yyyy-MM-dd") + "\t"+setId+"\t***"; 
				//---end---
			}
			runStatus.appendSchRunDesc("  开始查询报表【"+DpName+"】的数据.......");
			if(sReportType.equalsIgnoreCase("fixexp")){
				
				//add by qiuxufeng 20101116 138 QDV4太平2010年09月16日02_A
				//导出报表前，查询持久化表数据到临时表中
				commonRepBean = new CommonRepBean();
				commonRepBean.setYssPub(pub);
				commonRepBean.parseRowStr(sRepParam);
				commonRepBean.getReportData("");
				//========end=========
				
				iOV.init(getParams());
				reportData = String.valueOf(iOV.getTypeValue(DpCode));
			}else if(sReportType.equalsIgnoreCase("GYexp")){
				if(!(this.assetGroupCode.equalsIgnoreCase(pub.getAssetGroupCode()))){
					sCuryAssetGroupCode = pub.getAssetGroupCode();
					pub.yssLogon(this.assetGroupCode, pub.getUserCode());
				}
				if(portCount>1){
				 //#2524 批量导表的命名规则希望将组合前的短杠改为下划线 modify by jiangshichao 2011.02.12 
					fileName = assetGroupName + DpName +"_"+ portName+"-"+YssFun.formatDate(startDate, "yyyyMMdd")+".xls";
				}else{
					fileName = assetGroupName + DpName +"-"+YssFun.formatDate(startDate, "yyyyMMdd")+".xls";
				}
				
				commonRepBean = new CommonRepBean();
				commonRepBean.setYssPub(pub);
				commonRepBean.parseRowStr(sRepParam);
				/**shashijie 2012-11-19 STORY 3187 余额表*/
				if (DpCode.equals("RepOverage")) {
					reportData = getRepOverage();
					return reportData;
				} else {
					commonRepBean.getReportData("");
				}
				/**end shashijie 2012-11-19 STORY */
				
				iOV.init(getParams());
				//20110524 Added by liubo 修改工银财务报表的导出数据取值方式，从取数据源改为直接调用财务估值表模块  #850
				if(DpCode.equals("RepGYTest")){
					ExpClientReport client = new ExpClientReport();
					client.setYssPub(pub);
					reportData = client.buildAllDataSource_GYTest("RepGYTest", strGuessValueReq);
				} else {
					reportData = String.valueOf(iOV.getTypeValue(DpCode));
				}
			}else{
				iOV.init(getParams());
				reportData = iOV.getOperStrValue();
			}	//工银财务估值表，需要以套帐号为单位进行生成，修改状态提示信息。 20110523 Modified by liubo #850

			if(sReportType.equalsIgnoreCase("GYexp")){
				if(DpCode.equals("RepGYTest"))
				{
					if( DpName.length() > 0)
					{	runStatus.appendSchRunDesc("    已成功拼接报表【"+DpName+"】的数据.......");
						runStatus.appendSchRunDesc("    报表【" + DpName + "】导出外部文件名为【"+DpName + YssFun.formatDate(startDate, "yyyyMMdd")+".xls" +"】......");
						runStatus.appendSchRunDesc("  报表【"+DpName+"】导出成功！\r\n");
					}
					else
					{
						runStatus.appendSchRunDesc("　　组合 【" +portName + "】无对应套帐信息，导出失败 ......  ");
						runStatus.appendSchRunDesc("  组合【"+portName+"】导出财务估值表失败！\r\n");
						reportData = "";
					}
				}
				else
				{	runStatus.appendSchRunDesc("    已成功拼接报表【"+DpName+"】的数据.......");
					runStatus.appendSchRunDesc("    报表【" + DpName + "】导出外部文件名为【"+fileName+"】......");
					runStatus.appendSchRunDesc("  报表【"+DpName+"】导出成功！\r\n");
										
				}
			}else{
				runStatus.appendSchRunDesc("    已成功拼接报表【"+DpName+"】的数据.......");
				runStatus.appendSchRunDesc("    报表【" + DpName + "】导出外部文件名为【" + portCode+"_"+DpName + ".xls】......");
				runStatus.appendSchRunDesc("  报表【"+DpName+"】导出成功！\r\n");
			}
			
			return reportData;
		} catch (Exception e) {
			runStatus.appendSchRunDesc("\r\n报表【"+DpName+"】查询失败！");
			throw new YssException(e.getMessage(),e);
		}finally{
			pub.yssLogon(sCuryAssetGroupCode, pub.getUserCode());
			sCuryAssetGroupCode = "";
		}
	}

	/**shashijie 2012-11-19 STORY 3187 余额表 */
	private String getRepOverage() throws YssException {
		//内容+格式
		String reportData = "";
		//报表内容
		RepOverage bRep = new RepOverage();
		bRep.setYssPub(pub);
		bRep.initBuildReport(commonRepBean);
		String repDate = bRep.buildReport("");
		//拼接格式
		ExpClientReport client = new ExpClientReport();
		client.setYssPub(pub);
		reportData = client.buildAllDataSource_RepOverage("RepOverage", repDate);
		return reportData;
	}

	public String getReportHeaders(String sReportType) throws YssException {
		// TODO Auto-generated method stub
		return null;
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
		String[] reqAry = null;
		String[] reqAry1 = null;
		try {
			if (sRowStr.trim().length() == 0) {
				return;
			}
			
			reqAry1 = sRowStr.split("\r\n");
			reqAry = reqAry1[0].split("\t");
	        this.sRepParam = reqAry1[1];
	         
	         this.DpCode = reqAry[0];
	         this.DpName = reqAry[1];
	         this.portCode = reqAry[2];
	         this.startDate = YssFun.toDate(reqAry[3]);
	         this.endDate = YssFun.toDate(reqAry[4]);
	         beanid = reqAry[5];
	         this.portName = reqAry[6];
	         this.assetGroupCode = reqAry[7];
	         this.assetGroupName = reqAry[8];
	         this.portCount = YssFun.isNumeric(reqAry[9])?YssFun.toInt(reqAry[9]):1;

		} catch (Exception e) {
        	 throw new YssException("解析出错", e);
        }
	}
	
	/**
	 * 处理这里的参数问题
	 * @return
	 */
	private HashMap getParams(){
		HashMap hm = new HashMap();
		hm.put("0", sRepParam);
		return hm;
	}
	
	//add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
    public String checkReportBeforeSearch(String sReportType){
    	
    	return "";
    }

    /**shashijie 2011.04.07 STORY #805 头寸表应该预测T日到T+N-1日共N个工作日的头寸 */
	public String getSaveDefuntDay(String sRepotyType) throws YssException {
		return "";
	}
	//20110516 Added by liubo #850  
	//modify by huangqirong 2012-05-24 bug #4542
	//从前台获得一个组合代号，返回组合代号关联的套帐名称 
	public String GetBookSetName(String sPortCode) throws YssException
	{
		String bookSetName = "";
		try {			
			if(!(this.assetGroupCode.equalsIgnoreCase(pub.getAssetGroupCode()))){
				sCuryAssetGroupCode = pub.getAssetGroupCode();
				pub.yssLogon(this.assetGroupCode, pub.getUserCode());
			}
			YssFinance getBookSet = new YssFinance();
			getBookSet.setYssPub(pub);
			bookSetName = getBookSet.getBookSetName(sPortCode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally{
			pub.yssLogon(sCuryAssetGroupCode, pub.getUserCode());
			sCuryAssetGroupCode = "";
		}
		return bookSetName;
	}
}
