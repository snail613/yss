package com.yss.serve.accbook;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class AccBookServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 ---
    private Hashtable userTable = null;
    //----------------------------------------

    public AccBookServer() {
    }

    public void init() throws ServletException {
        //----MS00003 QDV4.1-参数布局散乱不便操作 ---
        userTable = new Hashtable(); //在servlet初始化时将此用户操作结果容器生成。
        //----------------------------------------
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        request.setCharacterEncoding("GB2312"); //设置了以后，parameter就不要转编码了
        response.setContentType(YssCons.CONTENT_TEXT);
        OutputStream ou = (OutputStream) response.getOutputStream();
        String squest = com.yss.util.YssUtil.getBytesStringFromClient(request);
        String beanId = request.getParameter("cmd");
        String flag = request.getParameter("flag");
        String showType = request.getParameter("showtype");
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据      

        YssPub pub = null;

        //--------------------
        Object obj = null;
        IDataSetting bean = null;
        IClientReportView bean2 = null;
        int operType = 0;
        //--------------------
        //---- MS00003 QDV4.1-参数布局散乱不便操作
        FlowBean flow = null;
        //------------------------------------
        SingleLogOper logOper = null;
        try {
            pub = YssUtil.getSessionYssPub(request);
            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428	
                String path = this.getServletContext().getRealPath("");

                ctx = new FileSystemXmlApplicationContext(
						YssUtil.getAppConContextPath(path,"accbook.xml"));
                pub.setAccBookCtx(ctx);
            }
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   

            obj = ctx.getBean(beanId.toLowerCase());
            if (obj instanceof IDataSetting) {
                bean = (IDataSetting) obj;
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("add")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_ADD);
                    operType = YssCons.OP_ADD;
                    bean.addSetting();

                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_ADD, pub); //MS00153 QDV4南方2009年1月5日09_B 这里将具体的BEAN传到日志中处理，而不是Servlet leeyu 2009-1-13

                } else if (flag.equalsIgnoreCase("edit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_EDIT);

                    logOper = SingleLogOper.getInstance();
                    logOper.setBData(bean);
                    operType = YssCons.OP_EDIT;
                    bean.editSetting();
                    logOper.setIData(bean, YssCons.OP_EDIT, pub);
                } else if (flag.equalsIgnoreCase("del")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_DEL);
                    operType = YssCons.RS_DEL;
                    bean.delSetting();
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_DEL, pub);
                } else if (flag.equalsIgnoreCase("audit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_AUDIT);
                    operType = YssCons.OP_AUDIT;
                    bean.checkSetting();
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_AUDIT, pub);
                } else if (flag.equalsIgnoreCase("clear")) { //添加回收站清除功能
                    bean.parseRowStr(squest);
                    bean.deleteRecycleData();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_CLEAR;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_CLEAR, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }

                else if (flag.equalsIgnoreCase("revert")) { // 添加回收站的还原功能
                    bean.parseRowStr(squest);
                    bean.checkSetting();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_REVERT;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_REVERT, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }

                else if (flag.equalsIgnoreCase("savamulti")) {
                    bean.saveMutliSetting(squest);
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    bean.parseRowStr(squest);
//               ou.write(bean.getOperValue(showType).getBytes());
                }

                if (showType != null && showType.length() > 0) {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, showType).getBytes());
                } else {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, flag).getBytes());
                }
            } else if (obj instanceof IClientReportView) {
                bean2 = (IClientReportView) obj;
                bean2.setYssPub(pub);
                bean2.parseRowStr(squest);
                if (flag.equalsIgnoreCase("reportdata")) {
                    ou.write(bean2.getReportData(showType).getBytes());
                } else if (flag.equalsIgnoreCase("reportheaders")) {
                    ou.write(bean2.getReportHeaders(showType).getBytes());
                }
            }
            //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
            userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
            //-------------------------------------------------
        } catch (Exception ye) {
            //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
        	if(pub != null){
        		userTable.put(pub.getUserCode(), new Boolean(false)); //若操作失败，则将此结果放入容器中
        	}
            //-------------------------------------------------
            try {
                logOper = SingleLogOper.getInstance();
                logOper.setIData(bean, operType, pub, true);
            } catch (YssException e) {
                e.printStackTrace();
            }
            //-----------------------------------------
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            ou.write(ye.toString().getBytes());
            ou.close();
        }
        //-------- MS00003 QDV4.1-参数布局散乱不便操作 ------------------------------
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
            if (pub != null){
            	pub.setBrown(false);//add by guolongchao 20110916 STORY 1285 将pub中的isBrown属性设为false;
            	pub.setTabMainCode("");//add by guolongchao 20110916 STORY 1285 将pub中的tabMainCode属性清空;
            }
        }
        //------------------------------------------------------------------------
    }

    private String getData(IDataSetting bean, String sShowType) throws
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
        //用于获取前台查询列表的TreeView控件所有的数据
        else if (sShowType.equalsIgnoreCase("treeview1")) {
            sResult = bean.getTreeViewData1();
        }
        //用于获取前台选择框TreeView控件的当前可用的数据
        else if (sShowType.equalsIgnoreCase("treeview2")) {
            sResult = bean.getTreeViewData2();
        } else if (sShowType.equalsIgnoreCase("treeview3")) {
            sResult = bean.getTreeViewData3();
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
        } else if (sShowType.equalsIgnoreCase("grouptreeview1")) {
            sResult = bean.getTreeViewGroupData1();
        } else if (sShowType.equalsIgnoreCase("grouptreeview2")) {
            sResult = bean.getTreeViewGroupData2();
        } else if (sShowType.equalsIgnoreCase("grouptreeview3")) {
            sResult = bean.getTreeViewGroupData3();
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
