package com.yss.serve.storagemanage;

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

public class StorageManageServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦 添加---
    private Hashtable userTable = null;
    //-----------------------------------------------
    //--add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B--//
    //private YssPageInationBean yssPageInationBean =null;   delete by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420
    private String auditNum="1",unAuditNum="1",recycledNum="1";//页码，值为整数  
    private String createQueryView="true";//创建查询视图，值为true,false (true为创建) 
    //--add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B--//
    //Initialize global variables
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
        //--add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B--//
        String sPageLine = request.getParameter("pageamount");
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据      
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
        //--add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B--//
        
        YssPub pub = null;
        //--------------------
        Object obj = null;
        IDataSetting bean = null;
        int operType = 0;
        //--------------------

        SingleLogOper logOper = null;

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
    					YssUtil.getAppConContextPath(path,"storagemanage.xml"));
                pub.setStorManageCtx(ctx);//add by xuxuming,20090928.MS00717,点击业务资料中的交易关联系统报错
            }
            obj = ctx.getBean(beanId.toLowerCase());
            
            //---add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B---//
            if(sPageLine!= null && !sPageLine.equals(""))
            {
            pub.setIPageCount(Integer.parseInt(sPageLine));//add by wuweiqi 20101122 QDV4华夏2010年10月27日02_A
            }
            //delete by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
//            yssPageInationBean =new YssPageInationBean();
//            yssPageInationBean.setYssPub(pub);
            //---add by songjie 2011.05.25 BUG 1836 QDV4赢时胜（测试）2011年4月28日04_B---//
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   
            if (obj instanceof IDataSetting) {
                bean = (IDataSetting) obj;
                //       IStorageManage bean = (IStorageManage) ctx.getBean(beanId.toLowerCase());
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
                    bean.checkInput(YssCons.OP_EDIT);//modified by yeshenghong 20120203 BUG3715现金库存中，在未审核中选中一条数据修改报错 修改时无需检查
                    
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
                    logOper.setIData(bean, operType, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("revert")) { // 添加回收站的还原功能
                    bean.parseRowStr(squest);
                    bean.checkSetting();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_REVERT;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, operType, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("savamulti")) {
                    bean.saveMutliSetting(squest);
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getOperValue(showType).getBytes());
                }

                if (showType != null && showType.length() > 0) {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, showType,pub).getBytes());//modify by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
                } else {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, flag,pub).getBytes());//modify by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
                }
            }
            //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.11 蒋锦------------
            userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
            //-------------------------------------------------
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
            //edit by songjie 2011.05.30 BUG 1990 QDV4赢时胜(测试)2011年5月26日01_B
            ou.write(ye.getLocalizedMessage().toString().getBytes());
            ou.close();
        }
        //-------- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦------------------------------
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

    /*
     * @param bean
     * @param sShowType
     * @param pub  add by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420
     * @return
     * @throws YssException
     */
    private String getData(IDataSetting bean, String sShowType,YssPub pub) throws
        YssException {
        String sResult = "";
        YssPageInationBean yssPageInationBean = new YssPageInationBean();
        yssPageInationBean.setYssPub(pub);
        
        //用于获取前台查询列表的ListView控件的当前可用的数据
        if (sShowType.equalsIgnoreCase("listview1")) {
        	//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
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
        	//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
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
