package com.yss.serve.platform;

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
import com.yss.main.platform.pfoper.commondata.*;
import com.yss.main.platform.pfoper.scheduling.SchProjectBean;
import com.yss.pojo.sys.*;
import com.yss.util.*;

public class PlatFormServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦 添加---
    private Hashtable userTable = null;
    //-----------------------------------------------

    public PlatFormServer() {
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
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据      
        YssPub pub = null;
        YssStatus runStatus = null;

        SingleLogOper logOper = null;
        //---- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦
        FlowBean flow = null;
        //------------------------------------
        //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        IDataSetting bean = null;
        int operType = 0;
        //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            Object obj = null;
            pub = YssUtil.getSessionYssPub(request);
            //2008.05.13 蒋锦 添加
            runStatus = YssUtil.getSessionYssStatus(request);
            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");
                ctx = new FileSystemXmlApplicationContext(
    					YssUtil.getAppConContextPath(path,"platform.xml"));
                pub.setPlatformCtx(ctx);
            }
            
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   

            obj = ctx.getBean(beanId.toLowerCase());
            if (obj instanceof IDataSetting) {
			    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                bean = (IDataSetting) obj;
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("add")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_ADD);
                    //modify by fangjiang 2012.01.10 BUG 3567
                    if("schproject".equalsIgnoreCase(beanId)){
                    	ou.write(bean.addSetting().getBytes());
                    }else{
                    	bean.addSetting();
                    }                    
                    //--------------
                    //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_ADD;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, operType, pub);
					//---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("edit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_EDIT);
                    //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    logOper = SingleLogOper.getInstance();
                    logOper.setBData(bean);
                    operType = YssCons.OP_EDIT;
                    //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    //modify by fangjiang 2012.01.10 BUG 3567
                    if("schproject".equalsIgnoreCase(beanId)){
                    	ou.write(bean.editSetting().getBytes());
                    }else{
                    	bean.editSetting();
                    }
                    //--------------
                    //add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    logOper.setIData(bean, operType, pub);
                } else if (flag.equalsIgnoreCase("del")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_DEL);
                    bean.delSetting();
                    //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_DEL;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, operType, pub);
					//---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("audit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_AUDIT);
                    bean.checkSetting();
                    //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_AUDIT;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, operType, pub);
					//---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }
                //added by liubo.Story #1916.
                //通过前台传递的调度方案代码和组合群代码，获取该调度方案代码所包含的接口代码
                //==================================
                else if (flag.equalsIgnoreCase("getCusConfigCode"))
                {
                	SchProjectBean project = new SchProjectBean();
                	project.setYssPub(pub);
                	//edit by songjie 2012.05.24 BUG 4651 QDV4赢时胜(上海)2012年05月23日02_B
                	ou.write(project.getCusConfigCode(squest).getBytes());
                }
                //================end==================
                else if(flag.equalsIgnoreCase("checkProjectCode"))
                {
                	SchProjectBean project = new SchProjectBean();
                	project.setYssPub(pub);
                	ou.write(project.checkProjectCode(showType).getBytes());
                }
                else if (flag.equalsIgnoreCase("savamulti")) {
                    bean.saveMutliSetting(squest);
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getOperValue(showType).getBytes());
                } else if (flag.equalsIgnoreCase("clear")) { //添加回收站清除功能
                    bean.parseRowStr(squest);
                    bean.deleteRecycleData();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_CLEAR, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("revert")) { // 添加回收站的还原功能
                    bean.parseRowStr(squest);
                    bean.checkSetting();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_REVERT, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }
                if (showType != null && showType.length() > 0) {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, showType).getBytes());
                } else {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, flag).getBytes());
                }
            } else if (obj instanceof IClientOperRequest) {
			    //edit by songjie 2012.08.14 bean 改为 beans
                IClientOperRequest beans = (IClientOperRequest) obj;
                beans.setYssPub(pub);
                ( (BaseBean) beans).setYssRunStatus(runStatus);
                if (flag.equalsIgnoreCase("check")) {
                	beans.parseRowStr(squest);
                    ou.write(beans.checkRequest(showType).getBytes());
                } else if (flag.equalsIgnoreCase("do")) {
                	beans.parseRowStr(squest);
                    ou.write(beans.doOperation(showType).getBytes());
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    ou.write(beans.getOperValue(showType).getBytes());
                }
            } else if (obj instanceof IClientReportView) {
			    //edit by songjie 2012.08.14 bean 改为 beans
                IClientReportView beans = (IClientReportView) obj;
                beans.setYssPub(pub);
                beans.parseRowStr(squest);
                if (flag.equalsIgnoreCase("reportdata")) {
                    ou.write(beans.getReportData(showType).getBytes());
                } else if (flag.equalsIgnoreCase("reportheaders")) {
                    ou.write(beans.getReportHeaders(showType).getBytes());
                }
            } else if (obj instanceof IDataInterface) {
		     	//edit by songjie 2012.08.14 bean 改为 beans
                IDataInterface beans = (IDataInterface) obj;
                beans.setYssPub(pub);
                if (flag.equalsIgnoreCase("export")) {
                    ou.write(beans.exportData(squest).getBytes());
                } else if (flag.equalsIgnoreCase("import")) {
                	beans.importData(squest);
                }
            } else if (obj instanceof IGuessValueReport) { // liyu modify 1008 为了华夏现金头寸条与基金管理日报表
                //edit by songjie 2012.08.14 bean 改为 beans
				IGuessValueReport beans = (IGuessValueReport) obj;
                beans.setYssPub(pub);
                if (flag.equalsIgnoreCase("getParameter")) {
                    ou.write(beans.getParameter(squest).getBytes());
                } else if (flag.equalsIgnoreCase("getGuessValueReport")) {
                    ou.write(beans.getGuessValueReport(squest).getBytes());
                } else if (flag.equalsIgnoreCase("buildGuessValueReport")) {
                    operType = YssCons.OP_BUILD;
                    ou.write(beans.buildGuessValueReport(squest).getBytes());

                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(beans, operType, pub);
                } else if (flag.equalsIgnoreCase("setDayConfirm")) { //日终处理
                    ou.write(beans.dayConfirm(squest).getBytes());
                    //-------------------------------------------
                    String[] sData = squest.split("\t");
                    if (sData.length > 3) {
                        if (sData[3].equalsIgnoreCase("1")) {
                            operType = YssCons.OP_DAYCON;
                        } else if (sData[3].equalsIgnoreCase("0")) {
                            operType = YssCons.OP_DAYUNCON;
                        }
                    }

                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(beans, operType, pub);

                } else if (flag.equalsIgnoreCase("getCashTCParameter")) {
                    ou.write(beans.getParameter(squest).getBytes());
                } else if (flag.equalsIgnoreCase("getCashTCReport")) {
                    ou.write(beans.getGuessValueReport(squest).getBytes());
                } else if (flag.equalsIgnoreCase("buildCashTCReport")) {
                    ou.write(beans.buildGuessValueReport(squest).getBytes());
                } else if (flag.equalsIgnoreCase("getHXParameter")) {
                    ou.write(beans.getParameter(squest).getBytes());
                } else if (flag.equalsIgnoreCase("getHXReport")) {
                    ou.write(beans.getGuessValueReport(squest).getBytes());
                } else if (flag.equalsIgnoreCase("buildHXReport")) {
                    ou.write(beans.buildGuessValueReport(squest).getBytes());
                }
            } else {
                //其他方式处理，by leeyu 0000426
				//edit by songjie 2012.08.14 bean 改为 beans
                CommonDataOper beans = new CommonDataOper();
                beans.setYssPub(pub);
                if (flag.equalsIgnoreCase("getlistData")) {
                    beans.parseRowStr(squest);
                    ou.write(beans.getListViewData(showType).getBytes());
                } else if (flag.equalsIgnoreCase("do")) {
                    beans.parseRowStr(squest);

                    ou.write(beans.doOperation(showType).getBytes());
                }
            }
        } catch (Exception ye) {
		    //---add by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
                logOper = SingleLogOper.getInstance();
                logOper.setIData(bean, operType, pub, true);
        	}catch(Exception e){
        		e.printStackTrace();
        	}
			//---add by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
          //modify by zhangfa 20100827 MS01656    新建两条相同的数据，点击确定保存时系统会报错    QDV4赢时胜(测试)2010年08月25日04_B  
            //ou.write(ye.toString().getBytes());
            ou.write(ye.getLocalizedMessage().getBytes());
          //------------------------------------------------------------------------------------------------------------------  
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
