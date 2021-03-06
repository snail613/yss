package com.yss.serve.settlement;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com</p>
 * @author not attributable
 * @version 1.0
 */

public class SettlementServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦 添加---
    private Hashtable userTable = null;
    //-----------------------------------------------


    //Initialize global variables
    public void init() throws ServletException {
        //----MS00003 QDV4.1-参数布局散乱不便操作 蒋锦 2009.03.10---
        userTable = new Hashtable(); //在servlet初始化时将此用户操作结果容器生成。
        //----------------------------------------
    }

    //Process the HTTP Get request
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        request.setCharacterEncoding("GB2312"); //设置了以后，parameter就不要转编码了
        response.setContentType(YssCons.CONTENT_TEXT);
        OutputStream ou = (OutputStream) response.getOutputStream();
        String squest = com.yss.util.YssUtil.getBytesStringFromClient(request);
        System.out.println(squest);
        String beanId = request.getParameter("cmd");
        String flag = request.getParameter("flag");
        String showType = request.getParameter("showtype");
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据      

        YssPub pub = null;
        //---- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦
        FlowBean flow = null;
        //------------------------------------
        try {
            pub = YssUtil.getSessionYssPub(request);
            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");
                ctx = new FileSystemXmlApplicationContext(
    					YssUtil.getAppConContextPath(path,"settlement.xml"));
                pub.setSettlementCtx(ctx);
            }
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   

            Object obj = ctx.getBean(beanId.toLowerCase());
            if (obj instanceof IClientOperRequest) {
                IClientOperRequest bean = (IClientOperRequest) obj;
                //        ISettlement bean = (ISettlement) ctx.getBean(beanId.toLowerCase());
                bean.setYssPub(pub);
                bean.parseRowStr(squest);
                bean.checkRequest(showType);
                //------  MS00003 QDV4.1-参数布局散乱不便操作 -----
                flow = new FlowBean();
                flow.setYssPub(pub);
                flow.ctlFlowStateAndLogInFun(YssCons.YSS_FLOW_POINTSTATE_EXECUTION); //设置流程正在执行的状态.
                //----------------------------------------------
                ou.write(bean.doOperation(flag).getBytes());
                //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
                userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
                //-------------------------------------------------
            }
        } catch (Exception ye) {
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            ou.write(ye.toString().getBytes());
            ou.close();
        }
        //-------- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦------------------------------
        finally {
            try {
                if ( pub!=null&&null != pub.getUserCode() && pub.getUserCode().length() > 0) {
                    flow = new FlowBean();
                    flow.setYssPub(pub);
                    flow.ctlFlowStateAndLog( (Boolean) userTable.get(pub.
                        getUserCode())); //操作流程状态和日志数据
                    userTable.remove(pub.getUserCode()); //清除个用户的信息.
                    pub.setBrown(false);//add by guolongchao 20110916 STORY 1285 将pub中的isBrown属性设为false;
                    pub.setTabMainCode("");//add by guolongchao 20110916 STORY 1285 将pub中的tabMainCode属性清空;
                }
            } catch (YssException ex) {
                ou.write(ex.getLocalizedMessage().getBytes());
            }
           
        }
        //------------------------------------------------------------------------
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
