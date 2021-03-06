package com.yss.serve.dayfinish;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.dayfinish.*;
import com.yss.main.funsetting.*;
import com.yss.pojo.sys.*;
import com.yss.util.*;

/**
 *
 * <p>Title: DayFinishServer </p>
 * <p>Description: 日终处理 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DayFinishServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//----MS00003 QDV4.1-参数布局散乱不便操作 ---
    private Hashtable userTable = null;
    //----------------------------------------

    private ApplicationContext ctx;

    //Initialize global variables
    public void init() throws ServletException {
        //----MS00003 QDV4.1-参数布局散乱不便操作 ---
        userTable = new Hashtable(); //在servlet初始化时将此用户操作结果容器生成。
        //----------------------------------------
    }

    //Process the HTTP Get request
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        request.setCharacterEncoding("GB2312"); //设置了以后，parameter就不要转编码了
        response.setContentType(YssCons.CONTENT_TEXT);
        OutputStream ou = (OutputStream) response.getOutputStream();
        String squest = YssUtil.getBytesStringFromClient(request);
        String beanId = request.getParameter("cmd");
        String flag = request.getParameter("flag");
        String showType = request.getParameter("showtype");
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据      
        YssPub pub = null;
        YssStatus runStatus = null;
        //---- MS00003 QDV4.1-参数布局散乱不便操作
        FlowBean flow = null;
        //------------------------------------

        try {
            pub = YssUtil.getSessionYssPub(request);
            runStatus = YssUtil.getSessionYssStatus(request);
            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");

                ctx = new FileSystemXmlApplicationContext(
						YssUtil.getAppConContextPath(path,"dayfinish.xml"));
                pub.setDayFinsihCtx(ctx);
            }
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   
            Object obj = ctx.getBean(beanId.toLowerCase());
            if (obj instanceof IClientOperRequest) {
                IClientOperRequest bean = (IClientOperRequest) obj;
//      IDayFinish bean = (IDayFinish) ctx.getBean(beanId.toLowerCase());
                bean.setYssPub(pub);
                ( (BaseBean) bean).setYssRunStatus(runStatus);
                if (flag.equalsIgnoreCase("checkdata")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.checkRequest(showType).getBytes());
                } else if (flag.equalsIgnoreCase("dayfinish")) {
                    bean.parseRowStr(squest);
                    //------  MS00003 QDV4.1-参数布局散乱不便操作 -----
                    flow = new FlowBean();
                    flow.setYssPub(pub);
                    flow.ctlFlowStateAndLogInFun(YssCons.YSS_FLOW_POINTSTATE_EXECUTION); //设置流程正在执行的状态.
                    if (pub.getFlow() != null && pub.getFlow().keySet().contains(pub.getUserCode())) {
                        ( (FlowBean) pub.getFlow().get(pub.getUserCode())).setFState(
                            YssCons.YSS_FLOW_POINTSTATE_EXECUTION);
                        if (bean instanceof IncomeStatBean) {
                            ( (FlowBean) pub.getFlow().get(pub.getUserCode())).
                                setFFunCode( ( (IncomeStatBean) bean).getBeanid());
                            //设置执行的组合代码 2009.04.17 蒋锦 添加
                            ( (FlowBean) pub.getFlow().get(pub.getUserCode())).
                                setFPortCodes( ( (IncomeStatBean) bean).getPortCodes());
                        } else if (bean instanceof IncomePaidBean) {
                            ( (FlowBean) pub.getFlow().get(pub.getUserCode())).
                                setFFunCode( ( (IncomePaidBean) bean).getBeanid().
                                            split("\t")[0]);
                            //设置执行的组合代码 2009.04.17 蒋锦 添加
                            ( (FlowBean) pub.getFlow().get(pub.getUserCode())).setFPortCodes( ( (IncomePaidBean) bean).getStrPortCode());
                        } else if (bean instanceof ValuationBean) {
                            //设置执行的组合代码 2009.04.17 蒋锦 添加
                            ( (FlowBean) pub.getFlow().get(pub.getUserCode())).setFPortCodes( ( (ValuationBean) bean).getPortCodes());
                        }
                    }
                    //----------------------------------------------
                    ou.write(bean.doOperation(flag).getBytes());
                    //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
                    userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
                    //-------------------------------------------------
                } else if (flag.equalsIgnoreCase("opervalue")) {
                	/**shashijie 2012-11-29 STORY 3288 加上解析字符串 */
                	bean.parseRowStr(squest);
					/**end shashijie 2012-11-29 STORY */
                    ou.write(bean.getOperValue(showType).getBytes());
                }
                //--- sj modified 20090803 MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A--------------------------
                else if (flag.equalsIgnoreCase("opermanage")) { //新增业务处理的单独处理方式
                    bean.parseRowStr(squest);
                    IOperManage convertBean = null; //使用新建的接口
                    if (bean instanceof IOperManage) { //若为继承了新的接口
                        convertBean = (IOperManage) bean;//将实例转换成新建的接口
                        ou.write(convertBean.doOperManage("").getBytes());//执行业务处理
                    }//end if
                }//end if
                //---------------------------------------------------
            } else if (obj instanceof IClientListView && obj instanceof IYssConvert) {
                IClientListView bean = (IClientListView) obj;
                IYssConvert iconv = (IYssConvert) obj;
                BaseBean basebean = (BaseBean) obj;
                basebean.setYssPub(pub);
                if (showType != null && showType.length() > 0) {
                    iconv.parseRowStr(squest);
                    ou.write(getData(bean, showType).getBytes());
                } else {
                    iconv.parseRowStr(squest);
                    ou.write(getData(bean, flag).getBytes());
                }
            }
        } catch (Exception ye) {
            //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
            userTable.put(pub.getUserCode(), new Boolean(false)); //若操作失败，则将此结果放入容器中
            //-------------------------------------------------
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            // ou.write(ye.toString().getBytes());
            ou.write(ye.getLocalizedMessage().getBytes()); //调用getLocalizedMessage方法返回异常信息到前台 by caocheng 2009.01.20 QDV4.1-BugNO:MS00004
            ou.close();
        }
        //-------- MS00003 QDV4.1-参数布局散乱不便操作 ------------------------------
        finally {
        	if (pub != null)
	        	{
	            try {
	                if (null != pub.getUserCode() && pub.getUserCode().length() > 0) {
	                    flow = new FlowBean();
	                    flow.setYssPub(pub);
	                    flow.ctlFlowStateAndLog( (Boolean) userTable.get(pub.
	                        getUserCode())); //操作流程状态和日志数据
	                    userTable.remove(pub.getUserCode()); //清除个用户的信息.
	                }
	            } catch (YssException ex) {
	                ou.write(ex.getLocalizedMessage().getBytes());
	            }
	            pub.setBrown(false);//add by guolongchao 20110916 STORY 1285 将pub中的isBrown属性设为false;
	            pub.setTabMainCode("");//add by guolongchao 20110916 STORY 1285 将pub中的tabMainCode属性清空;
	        	}
        }
        //------------------------------------------------------------------------
    }

    private String getData(IClientListView bean, String sShowType) throws
        YssException {
        String sResult = "";
        //用于获取前台查询列表的ListView控件的当前可用的数据
        if (sShowType.equalsIgnoreCase("listview1")) {
            sResult = bean.getListViewData1();
        }
        //用于获取前台选择框ListView控件的当前可用的数据
        else if (sShowType.equalsIgnoreCase("listview2")) {
            sResult = bean.getListViewData2();
        }
        //用于获取前台特殊ListView控件的数据
        else if (sShowType.equalsIgnoreCase("listview3")) {
            sResult = bean.getListViewData3();
        }
        //用于获取前台查询列表的ListView控件的所有的数据
        else if (sShowType.equalsIgnoreCase("listview4")) {
//         sResult = bean.getListViewData4();
        }
        /// <summary>
        /// 修改人：panjunfang
        /// 修改时间:20090514
        /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
        /// 在这加的方法查出组合群代码和组合代码这是因为前台以前调用opervalue不在listview里面
        else if (sShowType.equalsIgnoreCase("grouplistview1")) {
            sResult = bean.getListViewGroupData1();
        } else if (sShowType.equalsIgnoreCase("grouplistview2")) {
            sResult = bean.getListViewGroupData2();
        } else if (sShowType.equalsIgnoreCase("grouplistview3")) {
            sResult = bean.getListViewGroupData3();
        } else if (sShowType.equalsIgnoreCase("grouplistview4")) {
            sResult = bean.getListViewGroupData4();
        } else if (sShowType.equalsIgnoreCase("grouplistview5")) {
            sResult = bean.getListViewGroupData5();
        }

        return sResult;
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
