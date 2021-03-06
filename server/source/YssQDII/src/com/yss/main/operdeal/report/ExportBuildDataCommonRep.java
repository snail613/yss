package com.yss.main.operdeal.report;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.dsub.BaseBean;
import com.yss.dsub.YssPub;
import com.yss.main.cusreport.RepCustomBean;
import com.yss.main.cusreport.RepFormatBean;
import com.yss.main.dao.IBuildReport;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.parasetting.PortfolioSubBean;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/**
 * add by guolongchao 20120106 story #1284
 */
public class ExportBuildDataCommonRep extends BaseBuildCommonRep {	
	
	private String groupCode="";//组合群代码(表前缀)
	private String repGrpCode="";//报表组代码
	private String cusRepCode="";//自定义报表代码
	private String repCtlParam="";//报表参数
	
	private String oldPrefix = "";	//旧的组合群（表前缀） add by huangqirong 2012-01-013 story #1284
	
	public void parseRowStr(String sRowStr) throws YssException {	
		 String[] reqAry = null;
	     try {
             if (sRowStr.equals(""))
	                return;	            
	         reqAry = sRowStr.split("\f\t");
	         String[] temp = reqAry[0].split("-"); 
	         this.groupCode=temp[0];
	         this.repGrpCode=this.splitJoint(temp,0,temp.length-1);
	         this.cusRepCode=temp[temp.length-1];
	         if(reqAry.length > 1)
	        	 this.repCtlParam = reqAry[1];
	     }
	     catch (Exception e) {
	        throw new YssException("解析报表请求出错", e);
	     }
	}
	
	/*
	 * add by huangqirong 2012-01-07 story #1284
	 * 拼接数组字符串
	 * */
	private String splitJoint(String[] target, int begin, int end){
		String temp="";
		for (int i = begin; i < end; i++) {
			temp +=target[i]+"-"; 
		}
		if(temp.length()>0)
			temp = temp.substring(0,temp.length()-1);
		return temp;
	}
	
	public String bulidReport() throws YssException {				
		CommonRepBean commonRepBean=new CommonRepBean();
		commonRepBean.setRepCode(this.cusRepCode);//设定报表自定义代码
		commonRepBean.setRepCtlParam(this.repCtlParam);//设定报表查询所需的参数		
		String tabPrefix=pub.getPrefixTB();//将表前缀保存到tabPrefix中,当数据生成之后将pub中的表前缀改回原来的值
		try 
		{
			 BaseBuildCommonRep br=new BaseBuildCommonRep();	
			 pub.setPrefixTB(this.groupCode);//设定当前的表前缀,有可能是跨组合群操作 // modify huangqirong 2012-03-29 bug #4084
			 pub.setAssetGroupCode(this.groupCode); // modify huangqirong 2012-03-29 bug #4084
	         br.setYssPub(pub);
	         br.initBuildReport(commonRepBean);
	         br.buildReport2("");	
	        
	     } catch (Exception e) {
	         throw new YssException(e.getMessage());
	     }
	     finally{
	    	 pub.setPrefixTB(tabPrefix);//将表前缀改回到原来的值
	    	 pub.setAssetGroupCode(tabPrefix); // modify huangqirong 2012-03-29 bug #4084
	     }
		return "";
	}
	
	/*
	 * add by huangqirong 2012-01-10 story #1284 
	 * 修改表前缀
	 * */
	private void setPreFixTb(String tbFix){
		this.oldPrefix = pub.getPrefixTB();			
		if(!tbFix.equalsIgnoreCase(this.oldPrefix)){			
			pub.setPrefixTB(tbFix);
			pub.setAssetGroupCode(tbFix);
		}
	}
	
	/*
	 * add by huangqirong 2012-01-10 story #1284 
	 * 跨组合群入口
	 */
	public String getOperValue(String showType) throws YssException {
		String result ="";
		this.setPreFixTb(this.groupCode);	//修改表前缀
		try {
			if("getCtlGrpCode".equalsIgnoreCase(showType)){	//获取报表的报表参数控件
				RepCustomBean custom=new RepCustomBean();
				if(this.cusRepCode.trim().length() == 0)
					return result;
				custom.setCusRepCode(this.cusRepCode);
				custom.setYssPub(this.pub);
				result = custom.getOperValue(showType);
			}else if("getreportdata".equalsIgnoreCase(showType)){ //导出
				CommonRepBean repBean=new CommonRepBean();
				repBean.setRepCode(this.cusRepCode);
				repBean.setRepCtlParam(this.repCtlParam);
				this.initBuildReport(repBean);
				result = this.getReportData(this.cusRepCode);				
			}else if("repcustom".equalsIgnoreCase(showType)){
				RepCustomBean custom=new RepCustomBean();
				custom.setYssPub(this.pub);
				custom.setCusRepCode(this.cusRepCode);
				custom.getSetting();
				result = custom.buildRowStr();
			}else if("repformat".equalsIgnoreCase(showType)){
				RepFormatBean format=new RepFormatBean();
				format.setYssPub(this.pub);
				format.setRepCode(this.cusRepCode);
				format.getSetting();
				result = format.buildRowStr();
			}else if("portrelatype".equalsIgnoreCase(showType)){ //去组合的管理人，托管人
				PortfolioSubBean portfolio=new PortfolioSubBean();
				portfolio.setYssPub(this.pub);
				portfolio.setRelaType("Manager");
				portfolio.setPortCode(this.cusRepCode);
				String managers= portfolio.getListViewData1();
				
				String manager = managers.split("\r\f").length >1 ? managers.split("\r\f")[1] : ""; //管理人
				
				if(manager.length()>0){
					manager = manager.split("\f\f")[0].split("\t")[1];
				}else {
					manager = "";
				}
				
				portfolio.setRelaType("Trustee");
				String trustees = portfolio.getListViewData1();
				
				String trustee =trustees.split("\r\f").length >1 ? trustees.split("\r\f")[1] : ""; //主托管人
				if(trustee.length()>0){
					
					String [] tempTrustee = trustee.split("\f\f");
					
					for (String str : tempTrustee) {
						if(str.contains("主托管行")){
							trustee = str.split("\t")[1];
							break;
						}else{
							trustee="";
						}							
					}
				}else {
					trustee = "";
				}
				result = manager + "\t" +trustee;
			}else if("assetgroup".equalsIgnoreCase(showType)){
			        String strSql = "";
			        ResultSet rs = null;
			        try {
			            
			            strSql = "select FAssetGroupCode,FAssetGroupName from tb_sys_assetgroup where FAssetGroupCode="+dbl.sqlString(this.groupCode)+" and FLocked=0 and FSyscheck = 1";
			            rs = dbl.openResultSet(strSql);
			            if (rs.next()) {			                
			            	result = rs.getString("FAssetGroupName");			                           
			            }
			        }catch (Exception e) {
			        	throw new YssException("获取组合群信息出错", e);
					}finally{
						dbl.closeResultSetFinal(rs);
					}
			}else if("getrepcols".equalsIgnoreCase(showType)){//报表格式列数
				
				String sql="select FCols,FROWS from "+ pub.yssGetTableName("Tb_Rep_Format") + " where FREPFORMATCODE = "+dbl.sqlString(this.cusRepCode);
				result = this.getUserData(this.getFields(this.repCtlParam, ",") , sql);
				
			}
			else if(showType.indexOf("getDelayDate")>=0)//获取延迟天数------add by guolongchao 20120220 STORY 1284
			{
				String[] date=showType.split(":");
				if(date==null||date.length<2)
					return "";
				Date srcDate=YssFun.parseDate(date[1]);//分离出日期
				Date destDate=null;
				ResultSet rs = null;
				String sql="select delaydays,holidaycode,fformat from tb_"+this.groupCode+"_rep_filename where fcheckstate=1"
				            +" and FFileNameType='date' and frepcode="+dbl.sqlString(this.cusRepCode);
				rs=dbl.openResultSet(sql);
				if(rs.next())
				{
					//若节假日群未设置
					if(rs.getString("holidaycode")==null||rs.getString("holidaycode").trim().equalsIgnoreCase("null"))
					{
						destDate=YssFun.addDay(srcDate, rs.getInt("delaydays"));
					}
					else
					{
						BaseOperDeal baseOperDeal=new BaseOperDeal();
						baseOperDeal.setYssPub(pub);
						destDate=baseOperDeal.getWorkDay(rs.getString("holidaycode"), srcDate, rs.getInt("delaydays"));
					}
					result = YssFun.formatDate(destDate,rs.getString("fformat"));
				}
				dbl.closeResultSetFinal(rs);
				return result;
			}
			//add by huangqirong 2012-05-23 story #2484
			else if("changedbbase".equalsIgnoreCase(showType)){
				return pub.getDbLink().getUser() + "\t" + pub.getDbLink().getUrl()+"\t" + (pub.getDbLink().getChangeDtBase() ? "true" : "false");
			}
			//---end---
			
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{			
			this.setPreFixTb(this.oldPrefix);	//还原表前缀
		}
		return result;
	}
	
	/*
	 * 通用	执行sql
	 * */
	private String getUserData(String [] fields , String strSql) throws YssException {		
		StringBuffer result = new StringBuffer();
        ResultSet rs = null;
        try {
            rs = dbl.openResultSet(strSql); //当做SQL传过来
            if (rs.next()) {
            	for (int i = 0; i < fields.length; i++) {
					result.append(rs.getString(fields[i])+"\r");
				}
            }
            if(result.length()>0)
            	result.setLength(result.toString().length()-1);
        }catch (Exception e) {
        	throw new YssException("", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return result.toString();
	}
	
	
	public String [] getFields(String data , String regex){		
		return data.split(regex);
	}
	
	
	
}
