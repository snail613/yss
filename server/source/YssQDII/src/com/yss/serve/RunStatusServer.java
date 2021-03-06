package com.yss.serve;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.yss.pojo.sys.*;
import com.yss.util.*;

public class RunStatusServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RunStatusServer() {
    }

    public void init() throws ServletException {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        request.setCharacterEncoding("GB2312"); //设置了以后，parameter就不要转编码了
        response.setContentType(YssCons.CONTENT_TEXT);
        OutputStream ou = (OutputStream) response.getOutputStream();
        String squest = YssUtil.getBytesStringFromClient(request);
        YssStatus runStatus = null;
        try {
            runStatus = YssUtil.getSessionYssStatus(request);
            runStatus.parseRowStr(squest);
            ou.write(runStatus.buildRowStr().getBytes());
            runStatus.clearRunDesc();
        } catch (Exception ye) {
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            ou.write(ye.toString().getBytes());
            ou.close();
        }
    }

    //Process the HTTP Post request
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        doGet(request, response);
    }

    //Clean up resources
    public void destroy() {
    }

}
