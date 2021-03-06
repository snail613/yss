package com.yss.serve;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.yss.dsub.BaseBean;
import com.yss.dsub.YssPub;
import com.yss.log.DayFinishLogBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.IClientOperRequest;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.FlowBean;
import com.yss.main.operdeal.voucher.vchcheck.BaseVchCheck;
import com.yss.main.operdeal.voucher.vchcheck.VchChkCuryToAcc;
import com.yss.main.voucher.VchAssistantSettingBean;
import com.yss.pojo.sys.YssStatus;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssUtil;

public class VoucherServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦 添加---
    private Hashtable userTable = null;
	private SingleLogOper logOper;
    //-----------------------------------------------

    public VoucherServer() {
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
        YssStatus runStatus = null;
        //--------------------
        Object obj = null;
        //--------------------
        //---- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦
        FlowBean flow = null;
        //------------------------------------
        int operType = 0;//add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        try {
            pub = YssUtil.getSessionYssPub(request);
            runStatus = YssUtil.getSessionYssStatus(request);
            if (ctx == null) {
				//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
				//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");
                ctx = new FileSystemXmlApplicationContext(
    					YssUtil.getAppConContextPath(path,"voucher.xml"));
                pub.setVoucherCtx(ctx);//add by xuxuming,20091209 MS00851
            }
            
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   

            obj = ctx.getBean(beanId.toLowerCase());
            if (obj instanceof IDataSetting) {
                IDataSetting bean = (IDataSetting) obj;
                bean.setYssPub(pub);

                if (flag.equalsIgnoreCase("add")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_ADD);
                    bean.addSetting();
                    //----------add by guojianhua 20100908
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_ADD, pub);
                    //-------end------
                } else if (flag.equalsIgnoreCase("edit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_EDIT);
                    
                    //--- add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    logOper = SingleLogOper.getInstance();
                    logOper.setBData(bean);
                    //--- add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    bean.editSetting();
                    //----------add by guojianhua 20100908
                    
                    logOper.setIData(bean, YssCons.OP_EDIT, pub);
                    //-------end------
                } else if (flag.equalsIgnoreCase("del")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_DEL);
                    bean.delSetting();
                    
                    //----------add by guojianhua 20100908
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_DEL, pub);
                    //-------end------
                } else if (flag.equalsIgnoreCase("audit")) {
                    bean.parseRowStr(squest);
                    bean.checkSetting();
                    
                    //----------add by guojianhua 20100908
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_AUDIT, pub);
                    //-------end------
                } else if (flag.equalsIgnoreCase("savamulti")) {
                    bean.parseRowStr(squest);
                    bean.saveMutliSetting(squest);
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getOperValue(showType).getBytes());
                } else if (flag.equalsIgnoreCase("clear")) { //添加回收站清除功能 20080811 bug:0000377
                    bean.parseRowStr(squest);
                    bean.deleteRecycleData();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_CLEAR;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_CLEAR, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                } else if (flag.equalsIgnoreCase("revert")) { // 添加回收站的还原功能 20080811 bug:0000377
                    bean.parseRowStr(squest);
                    bean.checkSetting();   
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_REVERT;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_REVERT, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                }
                //20110616 Added by liubo.Story #963
                //根据前台传入的辅助核算项代码和辅助核算项表名，查询并返回给前台辅助核算项名称
                //----------------------
                else if (flag.equalsIgnoreCase("getauxiaccname"))
                {
                	VchAssistantSettingBean assSetting = new VchAssistantSettingBean();
                	assSetting.setYssPub(pub);
                	assSetting.parseRowStr(squest);
                	ou.write(assSetting.ReturnAuxiAccName().getBytes());
                }
                //--------end-----------
                
                //20110616 Added by liubo.Story #963
                //根据前台传入的组合代码，查询并返回给前台套帐代号和套帐名称
                //----------------------------
                else if (flag.equalsIgnoreCase("checkbookset"))
                {
                	VchAssistantSettingBean assSetting = new VchAssistantSettingBean();
                	assSetting.setYssPub(pub);
                	ou.write(assSetting.BookSetCheck(showType).getBytes());
                }
                //--------------end-------------
                
                else if (flag.equalsIgnoreCase("checkasssettingForAudit"))
                {
                	VchAssistantSettingBean assSetting = new VchAssistantSettingBean();
                	assSetting.setYssPub(pub);
                	assSetting.parseRowStr(squest);
                	assSetting.checkAssSettingForAudit();
                }
                else if (flag.equalsIgnoreCase("checkasssettingForUnAudit"))
                {
                	VchAssistantSettingBean assSetting = new VchAssistantSettingBean();
                	assSetting.setYssPub(pub);
                	assSetting.parseRowStr(squest);
                	assSetting.checkAssSettingForUnAudit();
                }
                //20110616 Added by liubo.Story #963
                //根据前台传入的辅助核算项表名，查询并返回给前台辅助核算项目代码和项目名称
                //----------------------------
                else if (flag.equalsIgnoreCase("auxiaccsetcheck"))
                {
                	VchAssistantSettingBean assSetting = new VchAssistantSettingBean();
                	assSetting.setYssPub(pub);
                	ou.write(assSetting.AuxiaccsetCheck(showType).getBytes());
                }
                //-----------end------------------
                
                if (showType != null && showType.length() > 0) {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, showType).getBytes());
                } else {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, flag).getBytes());
                }
                //add by lidaolong  20110406 #665 凭证导入时检查凭证币种，弹出提示窗口
            }else if (obj instanceof BaseVchCheck){
            	if (flag.equalsIgnoreCase("checkCury")){
                	VchChkCuryToAcc toAcc = new VchChkCuryToAcc();
                    //---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                	DayFinishLogBean df = new DayFinishLogBean();
                    df.setYssPub(pub);
                	String logSumCode = df.getLogSumCodes();
                	toAcc.setFunName("vchinter");
                	toAcc.logSumCode = logSumCode;
                	toAcc.comeFromDAO = true;
                	//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                	toAcc.setYssPub(pub);
                	toAcc.parseStr(squest);
                	
                	ou.write(toAcc.doCuryCheck().getBytes());
                }//end by lidaolong
            } 
            else if (obj instanceof IClientOperRequest) {
                IClientOperRequest bean = (IClientOperRequest) obj;
                bean.setYssPub(pub);
                ( (BaseBean) bean).setYssRunStatus(runStatus);
                if (flag.equalsIgnoreCase("check")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.checkRequest(showType).getBytes());
                } else if (flag.equalsIgnoreCase("do")) {
                    bean.parseRowStr(squest);
                    //------  MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦-----
                    flow = new FlowBean();
                    flow.setYssPub(pub);
                    flow.ctlFlowStateAndLogInFun(YssCons.YSS_FLOW_POINTSTATE_EXECUTION); //设置流程正在执行的状态.
                    //----------------------------------------------
                    ou.write(bean.doOperation(showType).getBytes());
                    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.11 蒋锦------------
                    userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
                    //-------------------------------------------------
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    ou.write(bean.getOperValue(showType).getBytes());
                }
            }
        } catch (Exception ye) {
            //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
        	/**shashijie 2012-7-2 STORY 2475 */
        	if (pub!=null && pub.getUserCode() != null) {
        		userTable.put(pub.getUserCode(), new Boolean(false)); //若操作失败，则将此结果放入容器中
			}
            //-------------------------------------------------

            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            //通过getLocalizedMessage()方法将异常信息输出到前台，前台会对此异常进行解析 sunkey 20090204 QDV4.1-BugNO:MS00004 指示信息的解析处理
            ou.write(ye.getLocalizedMessage().getBytes());
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
