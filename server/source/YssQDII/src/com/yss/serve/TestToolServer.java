package com.yss.serve;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.yss.util.YssCons;
import com.yss.webServices.client.test.TestToolService;
import com.yss.webServices.client.test.TestToolService_Service;

/**
 * add by huangqirong 2012-11-05 story #3227
 * 
 * */
public class TestToolServer extends HttpServlet{
	
	public TestToolServer() {
		// TODO Auto-generated constructor stub
	}	
	
	public void init() throws ServletException {

    }	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws
    ServletException, IOException {
		request.setCharacterEncoding("GB2312"); //设置了以后，parameter就不要转编码了
        response.setContentType(YssCons.CONTENT_TEXT);
        OutputStream ou = (OutputStream) response.getOutputStream();
        String squest = com.yss.util.YssUtil.getBytesStringFromClient(request);
        String cws = request.getParameter("sws");
        String stgws = request.getParameter("swsmethod");
        String tgws = request.getParameter("tgws");
        String tgwsmethod = request.getParameter("tgwsmethod");
        String filePath = new String(request.getParameter("filePath").getBytes("ISO-8859-1"),"GBK"); 
        String isClient = request.getParameter("isclient");
        
        String result = "";
        
        if("TestToolServer".equalsIgnoreCase(cws)){
        	if("dealTestDataMsg".equalsIgnoreCase(stgws)){
        		TestToolService_Service testService = new TestToolService_Service();
        		TestToolService testTool = testService.getTestToolPort();
        		result = testTool.dealTestDataMsg(squest,tgws,tgwsmethod ,filePath); 
        		ou.write(result.getBytes());
        		ou.close();
        	}        	
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
	}
	
	
	//Process the HTTP Post request
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        this.doGet(request, response);
    }

    //Clean up resources
    public void destroy() {
    	    	
    }
	
}
