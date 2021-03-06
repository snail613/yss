package com.yss.webServices.operdeal.port;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import com.yss.dsub.BaseBean;
import com.yss.dsub.DbBase;
import com.yss.dsub.YssPub;
import com.yss.projects.act.PluginExecuteBase;
import com.yss.projects.para.set.pojo.BEN_PLUGIN;
import com.yss.projects.para.set.pojo.BEN_PLUGIN_PRODUCE;
import com.yss.util.WarnPluginLoader;
import com.yss.util.YssCons;

public class groupPortInfoImp extends BaseBean 
	implements  PluginExecuteBase{

	public void doOperation() {
		// TODO Auto-generated method stub

	}

	public BEN_PLUGIN_PRODUCE getExecuteResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getGroupPorts(HttpSession arg0, String arg1) {
		ResultSet rs=null;
		ResultSet rsPort=null;
		String reStr="";
    	try{
    		String groupPorts=arg1;
    		YssPub ysspub=(YssPub) arg0.getAttribute(YssCons.SS_PUBLIC);
    		this.setYssPub(ysspub);
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
			dbl.closeResultSetFinal(rs);
		}
		return reStr;
    
	}

	public void initBean(BEN_PLUGIN arg0, ArrayList arg1, HttpSession arg2) {
		// TODO Auto-generated method stub

	}

}
