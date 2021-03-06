package com.yss.serve.commeach;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.commeach.*;
import com.yss.dsub.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CommEachServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦添加---
    private Hashtable userTable = null;
    //----------------------------------------
    public CommEachServer() {
    }

    public void init() throws ServletException {
        //----MS00003 QDV4.1-参数布局散乱不便操作 蒋锦 2009.03.10---
        userTable = new Hashtable(); //在servlet初始化时将此用户操作结果容器生成。
        //----------------------------------------
    }

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

        //--------------------
        Object obj = null;
        BaseCommEach bean = null;
        //--------------------
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
						YssUtil.getAppConContextPath(path,"commeach.xml"));
//            pub.setParaSettingCtx(ctx);//屏蔽set方法，不再将上下文放入pub中，以防获取对象出现错误。sj modified
            }
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   

            obj = ctx.getBean(beanId.toLowerCase());
            if (obj instanceof BaseCommEach) {
                bean = (BaseCommEach) obj;
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("opervalue")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getOperValue(showType).getBytes());
                }
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
            	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (pub != null && null != pub.getUserCode() && pub.getUserCode().length() > 0) {
                    flow = new FlowBean();
                    flow.setYssPub(pub);
                    flow.ctlFlowStateAndLog( (Boolean) userTable.get(pub.
                        getUserCode())); //操作流程状态和日志数据
                    userTable.remove(pub.getUserCode()); //清除个用户的信息.
                }
            } catch (YssException ex) {
                ou.write(ex.getLocalizedMessage().getBytes());
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if(pub != null){
            	pub.setBrown(false);//add by guolongchao 20110916 STORY 1285 将pub中的isBrown属性设为false;
            	pub.setTabMainCode("");//add by guolongchao 20110916 STORY 1285 将pub中的tabMainCode属性清空;
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
