package com.yss.webServices.operdeal.plugin.valCheck;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import com.yss.core.util.YssCons;
import com.yss.main.operdeal.valcheck.BaseValCheck;
import com.yss.pojo.sys.YssStatus;
import com.yss.projects.para.set.pojo.BEN_PLUGIN_PRODUCE;
import com.yss.util.WarnPluginLoader;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.webServices.operdeal.WarnPluginBaseBean;
//story 1925 by zhouwei 估值检查
public class WpValucationCheck extends WarnPluginBaseBean {

	public void doOperation(){

		this.plugPro=(BEN_PLUGIN_PRODUCE) list.get(0);
		ResultSet rs=null;
		String curDate=YssFun.formatDate(plugPro.getOperDate_Begin(),"yyyy-MM-dd");
		String[] groupPorts=plugPro.getC_PORT_CODE().split("-");//组合-组合群
		String group=groupPorts[0];//组合群
		String port=groupPorts[1];//组合信息
		String reStr="";
		BEN_PLUGIN_PRODUCE pp=null;
		BaseValCheck check = null;      //估值检查的基类
		try{
			setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_NORMAL, "正常");
			Date date=plugPro.getOperDate_Begin();
			curDate=YssFun.formatDate(date);
			pub.setPrefixTB(group);
			//嵌入或独立调用时，两种初始化bean的方式 
			if(pub.getWebRoot()==null || pub.getWebRoot().equals("")){
				check=(BaseValCheck) WarnPluginLoader.getOperdealCtx().getBean(plugPro.getC_PLUGIN_CODE());
				pub.setWebRoot(WarnPluginLoader.webRoot);
				pub.setAssetGroupCode(group);
				pub.setPortBaseCury();
			}else{
				check = (BaseValCheck) pub.getOperDealCtx().getBean(plugPro.getC_PLUGIN_CODE());
			}
            check.setYssPub(pub);
            //记录检查结果的项
            YssStatus ys=new YssStatus();
            check.setYssRunStatus(ys);
            check.setPluginValue(plugPro.getC_PLUGIN_VALUE());
            check.doCheck(date, port);
            reStr=check.getCheckInfos().replace("\r", "");
            
            //20130620 modified by liubo.Story #3759
            //check.getIsError()的值为true，表示预警指标执行时发现违规数据
            //这时需要将插件对象的C_RESULT_STATE对象设置为警告，以便在业务日志和调度方案中能体现
            //===================================
            if(check.getIsError().equals("true")){
            	setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_WARN,reStr);	
            }					
            //===============end====================				
            
			this.getPortRelaInfo(group, port);
		}catch (Exception e) {
			//--- add by songjie 2012.09.14 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
			try{
				//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
				this.plugPro.setC_RESULT_INFO(e.getMessage().replaceAll("\t", "")
						.replaceAll("&", "").replaceAll("\f\f", ""));//处理日志信息 除去特殊符号
				//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
				this.plugPro.setC_RESULT_STATE("失败");
				reStr=check.getCheckInfos().replace("\r", "");
				if(check.getIsError().equals("false")){
					setPlugProInfoToClient("已完成", YssCons.RESULT_STATE_WARN,reStr);	
				}												
				this.getPortRelaInfo(group, port);
			}catch(Exception ex){
				System.out.println(ex.getMessage());
			}
			//--- add by songjie 2012.09.14 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
			log.error("估值检查【"+plugPro.getC_PLUGIN_NAME()+"】失败!");
			e.printStackTrace();
		}finally{
			dbl.closeResultSetFinal(rs);
			pub.setPrefixTB(this.prefixTag);
			if(this.sessionId.equals("")){
				try{
					dbl.closeConnection();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}		
		}
	
	}
	
}
