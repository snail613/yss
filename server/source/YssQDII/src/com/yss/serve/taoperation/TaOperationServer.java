package com.yss.serve.taoperation;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.pojo.sys.YssPageInationBean;
import com.yss.util.*;

public class TaOperationServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦 添加---
    private Hashtable userTable = null;
    //-----------------------------------------------
    //delete by jsc ，Servlet 会一直加载，这里分页的对象不应该作为实例变量定义  【BUG4310tgb-18的JVM内存溢出】
    //private YssPageInationBean yssPageInationBean =null;//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
    //private String pageType="audit";//页类型，值为 audit,unaudit,recycled  QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
    private String auditNum="1",unAuditNum="1",recycledNum="1";//页码，值为整数  QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
    private String createQueryView="true";//创建查询视图，值为true,false (true为创建) QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
	private SingleLogOper logOper;
	private byte operType;
	
	/**shashijie 2011.03.18 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
    private String settleNum="1",settleNoNum="1",settleDelayNum="1",settleBackNum="1";
    /**~~~~~~~~~~~~end~~~~~~~~~~*/
	
    public TaOperationServer() {
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
        String sPageLine = request.getParameter("pageamount");//add by wuweiqi 20101122 添加显示行数参数  QDV4华夏2010年10月27日02_A
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据      
        //QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu        
        auditNum="1";
        unAuditNum="1";
        recycledNum="1";
        createQueryView="true";
        
        /**shashijie 2011.03.18 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
        settleNum="1";settleNoNum="1";settleDelayNum="1";settleBackNum="1";
        if(request.getParameter("settleNum")!=null) 
        	settleNum = request.getParameter("settleNum");
        if(request.getParameter("settleNoNum")!=null) 
        	settleNoNum = request.getParameter("settleNoNum");
        if(request.getParameter("settleDelayNum")!=null) 
        	settleDelayNum = request.getParameter("settleDelayNum");
        if(request.getParameter("settleBackNum")!=null) 
        	settleBackNum = request.getParameter("settleBackNum");
        /**~~~~~~~~~~~~end~~~~~~~~~~*/
        
        if(request.getParameter("auditnum")!=null)
        	auditNum=request.getParameter("auditnum");
        if(request.getParameter("unauditnum")!=null)
        	unAuditNum=request.getParameter("unauditnum");
        if(request.getParameter("recyclednum")!=null)
        	recycledNum=request.getParameter("recyclednum");
        if(request.getParameter("createquery")!=null)
        	createQueryView=request.getParameter("createquery");
        //QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
        YssPub pub = null;
        //--------------------
        Object obj = null;
        //--------------------
        //---- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦
        FlowBean flow = null;
        //------------------------------------
		//add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        IDataSetting bean =  null;
        try {
            pub = YssUtil.getSessionYssPub(request);
            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");
                ctx = new FileSystemXmlApplicationContext(
    					YssUtil.getAppConContextPath(path,"taoperation.xml"));
                pub.setCashManagerCtx(ctx);
            }
            obj = ctx.getBean(beanId.toLowerCase());
            if(sPageLine!= null && !sPageLine.equals(""))
            {
            pub.setIPageCount(Integer.parseInt(sPageLine));//add by wuweiqi 20101122 QDV4华夏2010年10月27日02_A
            }
            //delete by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420
            //yssPageInationBean =new YssPageInationBean();//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
            //yssPageInationBean.setYssPub(pub);//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
            
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   

            if (obj instanceof IDataSetting) {
			    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                bean = (IDataSetting) obj;
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("add")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_ADD);
                    operType = YssCons.OP_ADD;
                    bean.addSetting();
                    //----------add by guojianhua 2010 09 08
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_ADD, pub);
                    //-------end------
                } else if (flag.equalsIgnoreCase("edit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_EDIT);
                    //---edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    logOper = SingleLogOper.getInstance();
                    logOper.setBData(bean);
					//---edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    operType = YssCons.OP_EDIT;
                    
                    bean.editSetting();
                    // ----------add by guojianhua 20100908
                    //add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    logOper.setIData(bean, operType, pub);
                    //-------end------
                } else if (flag.equalsIgnoreCase("del")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_DEL);
                    operType = YssCons.OP_DEL;
                    bean.delSetting();
                    //----------add by guojianhua 20100908
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_DEL, pub);
                    //-------end------
                } else if (flag.equalsIgnoreCase("audit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_AUDIT);
                    operType = YssCons.OP_AUDIT;
                    bean.checkSetting();
                    //----------add by guojianhua 20100908
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_AUDIT, pub);
                    //-------end------
                } else if (flag.equalsIgnoreCase("clear")) { //添加回收站清除功能
                    bean.parseRowStr(squest);
                    bean.deleteRecycleData();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_CLEAR;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, operType, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }

                else if (flag.equalsIgnoreCase("revert")) { // 添加回收站的还原功能
                    bean.parseRowStr(squest);
                    bean.checkSetting();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_REVERT;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, operType, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }

                else if (flag.equalsIgnoreCase("savamulti")) {

                    bean.saveMutliSetting(squest);
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getOperValue(showType).getBytes());
                }
                //20120611 added by liubo.Story #2683
                //=======================
                else if (flag.equalsIgnoreCase("PaidIn"))
                {
                	TaTradeBean ta = new TaTradeBean();
                	ta.setYssPub(pub);
                	ta.parseRowStr(squest);
                	ou.write(ta.getPaidUpFundsMoney().getBytes());
                }
                //==========end=============
                if (showType != null && showType.length() > 0) {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, showType,pub).getBytes());//modify by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420
                } else {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, flag,pub).getBytes());//modify by jsc 【BUG4310tgb-18的JVM内存溢出】 20120420
                }
            } else if (obj instanceof IClientOperRequest) {
			    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A bean 改为 beans
                IClientOperRequest beans = (IClientOperRequest) obj;
                beans.setYssPub(pub);
                // ( (BaseBean) bean).setYssRunStatus(runStatus);
                if (flag.equalsIgnoreCase("check")) {
                    beans.parseRowStr(squest);
                    ou.write(beans.checkRequest(showType).getBytes());
                } else if (flag.equalsIgnoreCase("do")) {
                    beans.parseRowStr(squest);
                    ou.write(beans.doOperation(showType).getBytes());
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    ou.write(beans.getOperValue(showType).getBytes());
                }
            }
            //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.11 蒋锦------------
            userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
            //-------------------------------------------------
        } catch (Exception ye) {
		    //--- add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
				logOper = SingleLogOper.getInstance();
                logOper.setIData(bean, operType, pub, true);
        	}catch(Exception e){
        		e.printStackTrace();
        	}
			//--- add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            ou.write(ye.getLocalizedMessage().getBytes());
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

    private String getData(IDataSetting bean, String sShowType,YssPub pub) throws
        YssException {
        String sResult = "";
        
        //add by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
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
            sResult = bean.getListViewData1();
        }
        //用于获取前台选择框ListView控件的当前可用的数据
        else if (sShowType.equalsIgnoreCase("listview2")) {
        	/**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
        	if(bean instanceof BaseDataSettingBean){
        		if(createQueryView.equalsIgnoreCase("true"))
        			yssPageInationBean.setbCreateView(true);
        		else
        			yssPageInationBean.setbCreateView(false);
        		yssPageInationBean.setiAuditCurrPage(YssFun.toInt(auditNum));
        		yssPageInationBean.setiUnAuditCurrPage(YssFun.toInt(unAuditNum));
        		yssPageInationBean.setiRecycledCurrPage(YssFun.toInt(recycledNum));
        		yssPageInationBean.setiSettleCurrPage(YssFun.toInt(settleNum));
        		yssPageInationBean.setiSettleNoCurrPage(YssFun.toInt(settleNoNum));
        		yssPageInationBean.setiSettleDelayCurrPage(YssFun.toInt(settleDelayNum));
        		yssPageInationBean.setiSettleBackCurrPage(YssFun.toInt(settleBackNum));
        		((BaseDataSettingBean)bean).setYssPageInationBean(yssPageInationBean);
        	}
    		/**end*/
            sResult = bean.getListViewData2();
        }
        //用于获取前台特殊ListView控件的数据
        else if (sShowType.equalsIgnoreCase("listview3")) {
            sResult = bean.getListViewData3();
        }
        //用于获取前台查询列表的ListView控件的所有的数据
        else if (sShowType.equalsIgnoreCase("listview4")) {
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
