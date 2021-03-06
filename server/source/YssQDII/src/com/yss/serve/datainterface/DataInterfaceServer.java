package com.yss.serve.datainterface;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.dao.*;
import com.yss.main.datainterface.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.datainterface.dataCenter.BaseDataCenter;
import com.yss.pojo.sys.*;
import com.yss.util.*;

public class DataInterfaceServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦 添加---
    private Hashtable userTable = null;
    //-----------------------------------------------

    public DataInterfaceServer() {
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

        //--------------------
        Object obj = null;
        IDataInterface daoBean = null;
        IDataSetting setBean = null;
        BaseDataCenter dataCenter = null;
        //--------------------
        //---- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦
        FlowBean flow = null;
        //------------------------------------

        YssStatus runStatus = null;
        //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        int operType = 0;
        SingleLogOper logOper = null;
        IDataSetting bean = null;
        //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {

            pub = YssUtil.getSessionYssPub(request);

            runStatus = YssUtil.getSessionYssStatus(request);

            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");

                ctx = new FileSystemXmlApplicationContext(
						YssUtil.getAppConContextPath(path,"datainterface.xml"));
                pub.setDataInterfaceCtx(ctx);
            }
            
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   
          
            obj = ctx.getBean(beanId.toLowerCase());
            if (obj instanceof IDataSetting) {
                setBean = (IDataSetting) obj;
                //      IDataInterface bean = (IDataInterface) ctx.getBean(beanId.toLowerCase());
                setBean.setYssPub(pub);
                if (flag.equalsIgnoreCase("add")) {
                    setBean.parseRowStr(squest);
                    setBean.checkInput(YssCons.OP_ADD);
                    setBean.addSetting();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_ADD;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(setBean, operType, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("edit")) {
                    setBean.parseRowStr(squest);
                    setBean.checkInput(YssCons.OP_EDIT);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    logOper = SingleLogOper.getInstance();
                    logOper.setBData(setBean);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    setBean.editSetting();
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_EDIT;
                    logOper.setIData(setBean, operType, pub);
					//---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("del")) {
                    setBean.parseRowStr(squest);
                    setBean.checkInput(YssCons.OP_DEL);
                    setBean.delSetting();
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_DEL;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(setBean, operType, pub);
					//---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("audit")) {
                    setBean.parseRowStr(squest);
                    setBean.checkInput(YssCons.OP_AUDIT);
                    setBean.checkSetting();
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_AUDIT;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(setBean, operType, pub);
					//---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("clear")) { //添加回收站清除功能
                    setBean.parseRowStr(squest);
                    setBean.deleteRecycleData();
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_CLEAR;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(setBean, operType, pub);
					//---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("revert")) { // 添加回收站的还原功能
                    setBean.parseRowStr(squest);
                    setBean.checkSetting();
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_REVERT;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(setBean, operType, pub);
					//---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("savamulti")) {
                    setBean.parseRowStr(squest);
                    setBean.saveMutliSetting(squest);
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    setBean.parseRowStr(squest);
                    ou.write(setBean.getOperValue(showType).getBytes());
                } 
                //---added by yangshaokai 2011.12.31 STORY 2007
                else if (flag.equalsIgnoreCase("getAssetGroupName")) {
                	DaoPretreatBean daoPretreatBean = new DaoPretreatBean();
                	daoPretreatBean.setYssPub(pub);
                	daoPretreatBean.parseRowStr(squest);
                	ou.write(daoPretreatBean.returnAssetGroupName().getBytes());
                }
                //------------------end---------------------
                if (showType != null && showType.length() > 0) {
                    setBean.parseRowStr(squest);
                    ou.write(getViewData(setBean, showType).getBytes());
                } else {
                    setBean.parseRowStr(squest);
                    ou.write(getViewData(setBean, flag).getBytes());
                }
            } else if (obj instanceof IDataInterface) {
                daoBean = (IDataInterface) obj;
                daoBean.setYssPub(pub);
                if (flag.equalsIgnoreCase("importdata")) {
                    daoBean.importData(squest);
                }
                //------------------------20071020  chenyibo   作用 导出和导入接口的配置参数
                else if (flag.equalsIgnoreCase("exportdata")) {
                    ou.write(daoBean.exportData(squest).getBytes());
                } else if (flag.equalsIgnoreCase("importparam")) {
                    daoBean.importData(squest);
                } else if (flag.equalsIgnoreCase("exportparam")) {
                    ou.write(daoBean.exportData(squest).getBytes());
                }
                //----QDV4赢时胜（深圳）2009年5月12日01_A MS00455 增加其他功能操作调用的方法 by leeyu 20090531
                else if (flag.equalsIgnoreCase("opervalue")) {
                    daoBean.parseRowStr(squest);
                    ou.write(daoBean.getOperValue(showType).getBytes());
                }
                //------------QDV4赢时胜（深圳）2009年5月12日01_A MS00455
            } else if (obj instanceof IClientOperRequest) {
			    //--- edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A  bean 改为 clientBean start---//
                IClientOperRequest clientBean = (IClientOperRequest) obj;
                clientBean.setYssPub(pub);
                ( (BaseBean) clientBean).setYssRunStatus(runStatus);
                if (flag.equalsIgnoreCase("check")) {
                	clientBean.parseRowStr(squest);
                    ou.write(clientBean.checkRequest(showType).getBytes());
                    ////add by lidaolong  #536 有关国内接口数据处理顺序的变更
                }else if (flag.equalsIgnoreCase("clearImpData")){
                	  ou.write(clientBean.checkRequest("clearImpData").getBytes());
                } //end by lidaolong
				//--- edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A  bean 改为 clientBean end---//
                else if (flag.equalsIgnoreCase("do")) {
				    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A  bean 改为 clientBean
                	clientBean.parseRowStr(squest);
                    //------  MS00003 QDV4.1-参数布局散乱不便操作 -----
                    flow = new FlowBean();
                    flow.setYssPub(pub);
                    flow.ctlFlowStateAndLogInFun(YssCons.YSS_FLOW_POINTSTATE_EXECUTION); //设置流程正在执行的状态.
                    if (pub.getFlow() != null && pub.getFlow().keySet().contains(pub.getUserCode())) {
                        ( (FlowBean) pub.getFlow().get(pub.getUserCode())).setFState(
                            YssCons.YSS_FLOW_POINTSTATE_EXECUTION);
                        //设置接口代码
						//edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A bean 改为 clientBean
                        ( (FlowBean) pub.getFlow().get(pub.getUserCode())).setFFunCode( ( (DaoInterfaceManageBean) clientBean).getCusConfigCode());
                        //设置执行的组合代码 2009.04.17 蒋锦 添加
						//edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A bean 改为 clientBean
                        ( (FlowBean) pub.getFlow().get(pub.getUserCode())).setFPortCodes( ( (DaoInterfaceManageBean) clientBean).getPorts());
                    }
                    //----------------------------------------------
					//edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A bean 改为 clientBean
                    ou.write(clientBean.doOperation(showType).getBytes());
                    //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
                    userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
                    //-------------------------------------------------
                } else if (flag.equalsIgnoreCase("opervalue")) {
				    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A bean 改为 clientBean
                	clientBean.parseRowStr(squest); //添加方法的解析数据功能 MS00032 by leeyu 2008-12-23
                    ou.write(clientBean.getOperValue(showType).getBytes());
                } else if (flag.equalsIgnoreCase("filemerger")) {//shashijie 2011.2.17 STORY #557 希望优化追加数据的功能 
                	DaoInterfaceManageBean setBean2 = new DaoInterfaceManageBean();
                	setBean2.setYssPub(pub);
                	setBean2.parseRowStr(squest);	// 获取合并文件名路径
                    ou.write(setBean2.getFileMerger().getBytes());
				}
                //         beanLog = bean;
            }else if (obj instanceof BaseDataCenter){ //MS01541 QDV4赢时胜上海2010年08月4日01_AB  数据中心 add by jiangshichao
            	if (flag.equalsIgnoreCase("do")){
            		dataCenter = (BaseDataCenter) obj;
                	dataCenter.setYssPub(pub);
                	dataCenter.parseRowStr(squest);
                	ou.write(dataCenter.impData().getBytes());
            	}else if(flag.equalsIgnoreCase("check")){
            		dataCenter = (BaseDataCenter) obj;
                	dataCenter.setYssPub(pub);
                	dataCenter.parseRowStr(squest);
                	ou.write(dataCenter.isDataConfirm().getBytes());
            	}
            }
        } catch (Exception ye) {
            try {
			    //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                logOper = SingleLogOper.getInstance();
                logOper.setIData(bean, operType, pub, true);
				//---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
                userTable.put(pub.getUserCode(), new Boolean(false)); //若操作失败，则将此结果放入容器中
                //-------------------------------------------------
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            //ou.write(ye.toString().getBytes());
            ou.write(ye.getLocalizedMessage().getBytes()); //系统优化提示信息 调用新的异常处理模块 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090525
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

    private String getViewData(IDataSetting bean, String sShowType) throws
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
