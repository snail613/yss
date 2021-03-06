/**
 * ProducePluginSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.yss.webServices.service.plugin;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import com.yss.core.util.ParseParamsFromClient;
import com.yss.dsub.DbBase;
import com.yss.dsub.YssPub;
import com.yss.projects.para.set.pojo.BEN_PLUGIN;
import com.yss.util.WarnPluginLoader;
import com.yss.util.YssCons;
import com.yss.webServices.operdeal.WarnPluginBaseBean;

public class ProducePluginSoapBindingImpl implements com.yss.webServices.service.plugin.ProducePlugin_PortType{
    public java.lang.String doPlugin(java.lang.String inPara) throws java.rmi.RemoteException {  	
    	String reStr = null;
		String paramsArr[] = (String[])null;
		ArrayList list = null;
		BEN_PLUGIN plugin = null;
		String sessionId="";
		ParseParamsFromClient params = new ParseParamsFromClient();		
		YssPub pub=null;
		WarnPluginBaseBean obj=null;
		try{
			paramsArr = params.parseInitInfo(inPara);
			plugin = params.parsePlugin(paramsArr[0]);
			list = params.parseProduceInfo(paramsArr[1]);
			sessionId=paramsArr[2];
			//获取bean
			String bean=plugin.getPlugUrlMapping();
		    obj=(WarnPluginBaseBean)WarnPluginLoader.getApplicationContext().getBean(bean);
			if(!sessionId.equals("")){//QDII调用
				HttpSession session= (HttpSession) WarnPluginLoader.sessionMap.get(sessionId);
			    pub=(YssPub)session.getAttribute(YssCons.SS_PUBLIC);
			}else{//独立运行预警
				pub=new YssPub();
				DbBase dbase=new DbBase();
				dbase.getWsConnection("");
				pub.setDbLink(dbase);
				
			}
			obj.setYssPub(pub);
			obj.setPrefixTag(pub.getPrefixTB());
	    	//初始化成员变量
			obj.initBean(plugin, list,sessionId);			
			//业务处理
	    	obj.doOperation();
	    	//返回值组合
	    	reStr=obj.getPluginProduce().buildAttrReturnToClient();			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			pub.setPrefixTB(obj.getPrefixTag());
		}
		return reStr;
    }

}
