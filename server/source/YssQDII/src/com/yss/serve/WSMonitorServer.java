package com.yss.serve;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yss.dsub.YssPub;
import com.yss.util.YssCons;
import com.yss.util.YssUtil;
import com.yss.webServices.service.SwiftMsgDeal;
/**
 * 前台采用轮询的方式向后台发送请求，获取WebService服务机的运行状态，本Servlet用来拦截前台该类请求
 * @author yh 2011.05.16 QDV411建行2011年04月19日01_A
 */
public class WSMonitorServer extends HttpServlet {
	public void init(){
	}
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		OutputStream ou = null;
		String back = "";
		List<String> msg = null;
		StringBuffer buff = null;
		YssPub pub = null;
		try {
			pub = YssUtil.getSessionYssPub(request);
			request.setCharacterEncoding("GB2312");//设置请求的编码方式
			response.setContentType(YssCons.CONTENT_TEXT);
			ou = (OutputStream) response.getOutputStream();
	        String cmd = request.getParameter("cmd");
	        if(cmd.equalsIgnoreCase("WSmonitor")){
	        	//获取存放swift报文的静态hash表的数据
	        	if(!SwiftMsgDeal.msgCue.isEmpty()){
	        		buff = new StringBuffer();
	        		for(Object obj : SwiftMsgDeal.msgCue.keySet()){
	        			msg = (List<String>)SwiftMsgDeal.msgCue.get(obj);
	        			for(String temp : msg){
	        				buff.append(temp).append("\f\f");
	        			}
	        		}
	        		//清空消息
	        		SwiftMsgDeal.msgCue.clear();
	        		//移除最后一个间隔符
	        		buff.delete(buff.lastIndexOf("\f\f"), buff.length());
	        		back = buff.toString();
	        	}
	        }
	        //add by huangqirong 2012-04-22 story #2326 划款指令接口
	        else if(cmd.equalsIgnoreCase("monitorrequest")){
	        	if(SwiftMsgDeal.msgReturnClient.containsKey("QsTransfer")){
	        		if(SwiftMsgDeal.msgReturnClient.get("QsTransfer") != null 
	        				&& SwiftMsgDeal.msgReturnClient.get("QsTransfer").toString().trim().length()> 0){
	        			back = 	SwiftMsgDeal.msgReturnClient.get("QsTransfer").toString().trim();
	        			SwiftMsgDeal.msgReturnClient.put("QsTransfer",null); //清空消息
	        		}
	        	}
	        	//add by huangqirong 2012-10-27 Stroy #2328 交易指令接口
	        	if(SwiftMsgDeal.msgReturnClient.containsKey("JYZL")){
	        		if(SwiftMsgDeal.msgReturnClient.get("JYZL") != null 
	        				&& SwiftMsgDeal.msgReturnClient.get("JYZL").toString().trim().length()> 0){
	        			back = 	SwiftMsgDeal.msgReturnClient.get("JYZL").toString().trim();
	        			SwiftMsgDeal.msgReturnClient.put("JYZL",null); //清空消息
	        		}
	        	}
	        	//---end---
	        	//add by huangqirong 2012-12-19 Stroy #2327 行动接口
	        	if(SwiftMsgDeal.msgReturnClient.containsKey("JJQYXX")){
	        		if(SwiftMsgDeal.msgReturnClient.get("JJQYXX") != null 
	        				&& SwiftMsgDeal.msgReturnClient.get("JJQYXX").toString().trim().length()> 0){
	        			back = 	SwiftMsgDeal.msgReturnClient.get("JJQYXX").toString().trim();
	        			SwiftMsgDeal.msgReturnClient.put("JJQYXX",null); //清空消息
	        		}
	        	}
	        	//---end---
	        }
	        //---end---
	        ou.write(back.getBytes());
	        
		} catch (Exception ye) {
			response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            if(ou != null){ 
	            ou.write(ye.getLocalizedMessage().getBytes());
	            ou.close();
            }
            //---end---
		}
		finally{
			
		}
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException{
		doGet(request,response);
	}
	                                                                                                                                                                                                                                                    
}
