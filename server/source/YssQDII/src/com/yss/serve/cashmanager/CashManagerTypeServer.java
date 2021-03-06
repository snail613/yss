package com.yss.serve.cashmanager;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.cashmanage.CommandBean;
import com.yss.main.cusreport.RepFormatBean;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.pojo.sys.YssPageInationBean;
import com.yss.util.*;

public class CashManagerTypeServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 ---
    private Hashtable userTable = null;
    //----------------------------------------

    //--------------------------------------
    private SingleLogOper logOper = null; //MS00226 QDV4华宝兴业2009年2月4日01_B sj modified
    //--------------------------------------
    //delete by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
    //private YssPageInationBean yssPageInationBean =null;//QDV4赢时胜上海2010年03月18日06_B MS00884 by xuqiji
    private String auditNum="1",unAuditNum="1",recycledNum="1";//页码，值为整数  QDV4赢时胜上海2010年03月18日06_B MS00884 by xuqiji
    private String createQueryView="true";//创建查询视图，值为true,false (true为创建) QDV4赢时胜上海2010年03月18日06_B MS00884 by xuqiji
    public CashManagerTypeServer() {
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
        String sPageLine = request.getParameter("pageamount");//add by wuweiqi 20101122 添加显示行数参数  QDV4华夏2010年10月27日02_A
        //QDV4赢时胜上海2010年03月18日06_B MS00884 by xuqiji     
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
        //QDV4赢时胜上海2010年03月18日06_B MS00884 by leeyu
        YssPub pub = null;
        //--------------------
        Object obj = null;
        int operType = 0;
        //--------------------
        //---- MS00003 QDV4.1-参数布局散乱不便操作
        FlowBean flow = null;
        //------------------------------------
        try {
            pub = YssUtil.getSessionYssPub(request);
            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");
                ctx = new FileSystemXmlApplicationContext(
						YssUtil.getAppConContextPath(path,"cashmanager.xml"));
                pub.setCashManagerCtx(ctx);
            }
            obj = ctx.getBean(beanId.toLowerCase());
            if(sPageLine!= null && !sPageLine.equals(""))
            {
            pub.setIPageCount(Integer.parseInt(sPageLine));//add by wuweiqi 20101122 QDV4华夏2010年10月27日02_A
            }
            //delete by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
//            yssPageInationBean =new YssPageInationBean();//QDV4赢时胜上海2010年03月18日06_B MS00884 by xuqiji
//            yssPageInationBean.setYssPub(pub);//QDV4赢时胜上海2010年03月18日06_B MS00884 by xuqiji
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   

            if (obj instanceof IDataSetting) {
                IDataSetting bean = (IDataSetting) obj;

                bean.setYssPub(pub);
                //begin by zhouxiang MS01628 2010.09.19关于招商基金需求之电子指令功能   
                if("Command".equalsIgnoreCase(beanId)){
                	if (flag.equalsIgnoreCase("send")) {
                		operType=YssCons.OP_SEND;
            		} else if(flag.equalsIgnoreCase("backout")) {
            			operType=YssCons.OP_CANCEL;//操作日志的类型
            		}else if(flag.equals("ModifyStutes")){
            			operType=YssCons.OP_EDIT;//操作日志的类型
            		}
                	CommandBean command=(CommandBean)bean;
                	command.parseRowStr(squest);
                	ou.write(command.eleTransferDeal(flag).getBytes());
                }
                //end--------------------------------------------------------------
                if (flag.equalsIgnoreCase("add")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_ADD);
                    bean.addSetting();
                    //-------------MS00226 QDV4华宝兴业2009年2月4日01_B sj modified 增加新增操作的日志 --//
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_ADD, pub);

                } else if (flag.equalsIgnoreCase("edit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_EDIT);

                    //-------------MS00226 QDV4华宝兴业2009年2月4日01_B sj modified 增加修改操作的日志 --//
                    logOper = SingleLogOper.getInstance();
                    logOper.setBData(bean);
                    operType = YssCons.OP_EDIT;
                    //-------------------------------------------------------------//
                    bean.editSetting();
                    //-------------------------------------------------
                    //-------------MS00226 QDV4华宝兴业2009年2月4日01_B 增加修改操作的日志 --//
                    logOper.setIData(bean, YssCons.OP_EDIT, pub);
                    //-------------------------------------------------------------//
                } else if (flag.equalsIgnoreCase("deltype")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_DEL);
                    bean.delSetting();
                    //-------------MS00226 QDV4华宝兴业2009年2月4日01_B 增加删除操作的日志 --//
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_DEL, pub);

                } else if (flag.equalsIgnoreCase("audit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_AUDIT);
                    bean.checkSetting();
                    //-------------MS00226 QDV4华宝兴业2009年2月4日01_B 增加删除操作的日志 --//
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
                } else if(flag.equalsIgnoreCase("addtype")) {
                	//---add by liuxiaojun 20130726 story 4094   start---//
                	bean.parseRowStr(squest);
//                	bean.checkInput(YssCons.OP_ADD);
                	bean.addSetting();
                } else if(flag.equalsIgnoreCase("deltype")) {
                	bean.parseRowStr(squest);
                	bean.delSetting();
                	//---add by liuxiaojun 20130726 story 4094   end---//
                } else if (flag.equalsIgnoreCase("savamulti")) {

                    bean.saveMutliSetting(squest);
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getOperValue(showType).getBytes());
                }
                //story 1645 by zhouwei 20111216 QDII工银2011年9月13日10_A
                else if (flag.equalsIgnoreCase("getformat")) {
                    bean.parseRowStr(squest);
                    bean.getSetting();
                    com.yss.main.cashmanage.CommandModuleBean comMod=(com.yss.main.cashmanage.CommandModuleBean)bean;
                    ou.write(comMod.buildRowStr().getBytes());
                }
                //--------end--------------------
                if (showType != null && showType.length() > 0) {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, showType,pub).getBytes());//add by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
                } else {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, flag,pub).getBytes());//add by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
                }
            } else if (obj instanceof IClientOperRequest) {
                IClientOperRequest bean = (IClientOperRequest) obj;
                bean.setYssPub(pub);
                // ( (BaseBean) bean).setYssRunStatus(runStatus);
                if (flag.equalsIgnoreCase("check")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.checkRequest(showType).getBytes());
                } else if (flag.equalsIgnoreCase("do")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.doOperation(showType).getBytes());
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    ou.write(bean.getOperValue(showType).getBytes());
                }
            }
            //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.11 蒋锦------------
            userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
            //-------------------------------------------------
        } catch (Exception ye) {
            //----------------增加对错误操作的日志处理 MS00226 QDV4华宝兴业2009年2月4日01_B -----//
            if (obj instanceof IDataSetting) {
                IDataSetting bean = (IDataSetting) obj;
                
            }
            //-----------------------------------------------------------------------------//
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            ou.write(ye.getLocalizedMessage().getBytes()); //modify by wangzuochun 2010.11.18 BUG #446 新增划款指令，提示信息不友好，且新增失败时，不产生资金调拨和不保存划款指令。 
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
            if(pub != null){
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
        if (sShowType.equalsIgnoreCase("addtype")) {
            sResult = bean.getListViewData1();
        }else if (sShowType.equalsIgnoreCase("listview3")){
        	sResult = bean.getListViewData3();
        }
        //用于获取前台选择框ListView控件的当前可用的数据
        else if (sShowType.equalsIgnoreCase("listview2")) {
            sResult = bean.getListViewData2();
        }
        else if (sShowType.equalsIgnoreCase("deltype")) {
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
