/**
 * PortInfoSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.yss.webServices.service.port;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import com.yss.dsub.DbBase;
import com.yss.dsub.YssPub;
import com.yss.util.WarnPluginLoader;
import com.yss.util.YssCons;

public class PortInfoSoapBindingImpl implements com.yss.webServices.service.port.PortInfo_PortType{
    public java.lang.String port(java.lang.String inPara) throws java.rmi.RemoteException { 	
    	DbBase dbl=null;
    	Connection conn=null;
    	ResultSet rs=null;
    	ResultSet rsPort=null;
    	String reStr="";
    	String sessionId="";
    	try{
    		String[] paras=inPara.split("\t");
    	    sessionId=paras[0];
    		String groupPorts=paras[1];
    		YssPub pub=null;
    		if(!sessionId.equals("")){//QDII调用
    			HttpSession session= (HttpSession) WarnPluginLoader.sessionMap.get(sessionId);
			    pub=(YssPub)session.getAttribute(YssCons.SS_PUBLIC);
			    dbl=pub.getDbLink();
			}else{//独立运行预警		
				dbl=new DbBase();
				dbl.getWsConnection("");				
			}
    		String sql="select FAssetGroupCode from TB_SYS_ASSETGROUP order by FAssetGroupCode asc";
    		rs=dbl.openResultSet(sql);
    		while(rs.next()){
    			String groupCode=rs.getString("FAssetGroupCode");
    			sql="select * from tb_"+groupCode.trim()+"_Para_Portfolio where fcheckstate=1";
    			rsPort=dbl.openResultSet(sql);
    			while(rsPort.next()){
    				if(groupPorts.equals("") || groupPorts.indexOf(groupCode+"-"+rsPort.getString("FPortCode"))>-1){
    					reStr+=groupCode+"-"+rsPort.getString("FPortCode")+"@"+rsPort.getString("FPortName")+"@4.0\t";
    				}
    			}
    			dbl.closeResultSetFinal(rsPort);
    		}
    		if(reStr.length()>1){
    			reStr=reStr.substring(0, reStr.length()-1);
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				dbl.closeResultSetFinal(rs);
				if(sessionId.equals("")){
					dbl.closeConnection();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return reStr;
    
    }

}
