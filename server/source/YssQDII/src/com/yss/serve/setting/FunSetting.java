package com.yss.serve.setting;

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
import com.yss.pojo.sys.YssPageInationBean;
import com.yss.util.*;

public class FunSetting
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦 添加---
    private Hashtable userTable = null;
    //-----------------------------------------------
    //add by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B 
    //delete by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
    //private YssPageInationBean yssPageInationBean =null;
    private String auditNum="1",unAuditNum="1",recycledNum="1";//页码，值为整数
    private String createQueryView="true";//创建查询视图，值为true,false (true为创建)
    //---------
    public FunSetting() {
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
        String beanId = request.getParameter("cmd");
        String flag = request.getParameter("flag");
        String showType = request.getParameter("showtype");
        String operCase = request.getParameter("operCase"); //此参数用来处理分支操作  MS00003 QDV4.1-参数布局散乱不便操作
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据   
        String sPageLine = request.getParameter("pageamount");//story 1840 by zhouwei 20111114 每页显示行数
        YssPub pub = null;
        //--------------------
        Object obj = null;
        IDataSetting bean = null;
        int operType = 0;
        //--------------------

        //--- MS00003 QDV4.1-参数布局散乱不便操作 ---
        FlowBean flow = null;
        //----------------------------------------
        SingleLogOper logOper = null;
        //add by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B        
        auditNum="1";
        unAuditNum="1";
        recycledNum="1";
        createQueryView="true";
        if(request.getParameter("auditnum")!=null)
        	auditNum=request.getParameter("auditnum");
        if(request.getParameter("unauditnum")!=null)
        	unAuditNum=request.getParameter("unauditnum");
        if(request.getParameter("recyclednum")!=null)
        	recycledNum=request.getParameter("recyclednum");
        if(request.getParameter("createquery")!=null)
        	createQueryView=request.getParameter("createquery");
        //-------------
        try {
            pub = YssUtil.getSessionYssPub(request);
            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");
                ctx = new FileSystemXmlApplicationContext(
    					YssUtil.getAppConContextPath(path,"funsetting.xml"));
                pub.setFunSettingCtx(ctx);
            }
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   
          //story 1840  by zhouwei 20111114
            if(sPageLine!= null && !sPageLine.equals(""))
            {
               pub.setIPageCount(Integer.parseInt(sPageLine));
            }
            obj = ctx.getBean(beanId.toLowerCase());
            //add by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B 
          //delete by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
//            yssPageInationBean =new YssPageInationBean();
//            yssPageInationBean.setYssPub(pub);
            //-----------
            if (obj instanceof IDataSetting) {
                bean = (IDataSetting) obj;
                bean.setYssPub(pub);

                if (flag.equalsIgnoreCase("add")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_ADD);
                    operType = YssCons.OP_ADD;
                    bean.addSetting();
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_ADD, pub);

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
                    operType = YssCons.OP_DEL;
                    bean.delSetting();
                    //-----------------------------------------
                    if (bean instanceof OperateLogBean) {} else {
                        logOper = SingleLogOper.getInstance();
                        logOper.setIData(bean, YssCons.OP_DEL, pub);
                    }
                    //-----------------------------------------

                } else if (flag.equalsIgnoreCase("check")) {
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
                } else if (flag.equalsIgnoreCase("revert")) { // 添加回收站的还原功能
                    bean.parseRowStr(squest);
                    bean.checkSetting();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_REVERT;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_REVERT, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("savamulti")) {
                    bean.saveMutliSetting(squest);
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getOperValue(showType).getBytes());

                }
                
                else if(flag.equalsIgnoreCase("ModifiedContent"))
                {
                	OperateLogBean oper = new OperateLogBean();
                	oper.setYssPub(pub);
                	ou.write(oper.getModifiedContent(squest).getBytes());
                	return;
                }
                
                //---- MS00003 QDV4.1-参数布局散乱不便操作 ----
                //author : 孙振、沈杰、蒋锦
                //流程相关的操作
                else if (flag.equalsIgnoreCase("flow")) {
                    //将当前操作的流程信息添加到pub对象
                    if (operCase.equalsIgnoreCase("addFlowPub")) {
                        bean.parseRowStr(squest);
                        Hashtable flowtable = pub.getFlow();
                        if (flowtable == null) {
                            flowtable = new Hashtable();
                        }
                        flowtable.put(pub.getUserCode(), bean);
                        pub.setFlow(flowtable);
                    }
                    //删除pub中的流程对象
                    if (operCase.equalsIgnoreCase("clearFlowPub")) {
                        //如果存在流程信息，将流程信息从pub中删除
                        if (pub.getFlow().containsKey(pub.getUserCode())) {
                            pub.getFlow().remove(pub.getUserCode());
                        }
                    }
                    //检查当前是否正在执行
                    else if (operCase.equalsIgnoreCase("flowcheck")) { //判断流程是否正在执行 2009.03.16 蒋锦 添加
                        Hashtable flowtable = pub.getFlow();
                        if (flowtable == null) {
                            return;
                        }
                        if (flowtable.keySet().contains(pub.getUserCode()) &&
                            ( (FlowBean) flowtable.get(pub.getUserCode())).getFState() ==
                            YssCons.YSS_FLOW_POINTSTATE_EXECUTION) {
                            throw new YssException("流程正在执行中，请稍候...");
                        }
                    }
                    //关闭流程或激活流程 add by sunkey 20090320
                    else if (operCase.equalsIgnoreCase("flowClose") ||
                             operCase.equalsIgnoreCase("flowActive")) {
                        flow = new FlowBean();
                        flow.setYssPub(pub);
                        //------  MS00003 QDV4.1-参数布局散乱不便操作 -----
                        flow.ctlFlowStateAndLogInFun(operCase.equalsIgnoreCase("flowClose") ?
                            YssCons.
                            YSS_FLOW_POINTSTATE_SUCCESS :
                            YssCons.YSS_FLOW_POINTSTATE_FALSE);
                    }
                }
                //------------------------------------------

                if (showType != null && showType.length() > 0) {
                    bean.parseRowStr(squest);
                    ou.write(this.getData(bean, showType,pub).getBytes());//modify by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
                } else {

                    bean.parseRowStr(squest);
                    ou.write(getData(bean, flag,pub).getBytes());//modify by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
                }
            }
        } catch (Exception ye) {
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
            ou.write(ye.getLocalizedMessage().getBytes());
            ou.close();
        }
        //-------- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦------------------------------
        finally {
            try {
                if (pub!=null&&null != pub.getUserCode() && pub.getUserCode().length() > 0) {
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

    private String getData(IDataSetting bean, String sShowType,YssPub pub) throws
        YssException {
        String sResult = "";
        
      //add by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
        YssPageInationBean yssPageInationBean = new YssPageInationBean();
        yssPageInationBean.setYssPub(pub); 
        //用于获取前台查询列表的ListView控件的当前可用的数据
        if (sShowType.equalsIgnoreCase("listview1")) {
        	//add by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B 
        	if(bean instanceof BaseDataSettingBean){
        		if(createQueryView.equalsIgnoreCase("true"))
        			yssPageInationBean.setbCreateView(true);
        		else
        			yssPageInationBean.setbCreateView(false);
        		yssPageInationBean.setiAuditCurrPage(YssFun.toInt(auditNum));
        		yssPageInationBean.setiUnAuditCurrPage(YssFun.toInt(unAuditNum));
        		yssPageInationBean.setiRecycledCurrPage(YssFun.toInt(recycledNum));
        		((BaseDataSettingBean)bean).setYssPageInationBean(yssPageInationBean);
        	}
        	//--------------------
            sResult = bean.getListViewData1();
        }
        //用于获取前台选择框ListView控件的当前可用的数据
        else if (sShowType.equalsIgnoreCase("listview2")) {
        	//story 1840 by zhouwei 20111114
        	if(bean instanceof BaseDataSettingBean){
        		if(createQueryView.equalsIgnoreCase("true"))
        			yssPageInationBean.setbCreateView(true);
        		else
        			yssPageInationBean.setbCreateView(false);
        		yssPageInationBean.setiAuditCurrPage(YssFun.toInt(auditNum));
        		yssPageInationBean.setiUnAuditCurrPage(YssFun.toInt(unAuditNum));
        		yssPageInationBean.setiRecycledCurrPage(YssFun.toInt(recycledNum));
        		((BaseDataSettingBean)bean).setYssPageInationBean(yssPageInationBean);
        	}
        	//---end----
            sResult = bean.getListViewData2();
        }
        //用于获取前台特殊ListView控件的数据
        else if (sShowType.equalsIgnoreCase("listview3")) {
            sResult = bean.getListViewData3();
        }
        //用于获取前台查询列表的ListView控件的所有的数据
        else if (sShowType.equalsIgnoreCase("listview4")) {

        	if(bean instanceof BaseDataSettingBean){
        		if(createQueryView.equalsIgnoreCase("true"))
        			yssPageInationBean.setbCreateView(true);
        		else
        			yssPageInationBean.setbCreateView(false);
        		yssPageInationBean.setiAuditCurrPage(YssFun.toInt(auditNum));
        		yssPageInationBean.setiUnAuditCurrPage(YssFun.toInt(unAuditNum));
        		yssPageInationBean.setiRecycledCurrPage(YssFun.toInt(recycledNum));
        		((BaseDataSettingBean)bean).setYssPageInationBean(yssPageInationBean);
        	}
            sResult = bean.getListViewData4();
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
