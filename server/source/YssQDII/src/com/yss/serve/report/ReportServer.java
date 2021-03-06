package com.yss.serve.report;

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
import com.yss.main.report.GuessValue;
import com.yss.main.operdeal.report.ExportBuildDataCommonRep;//add by guolongchao 20121006 STORY 1284
import com.yss.main.operdeal.report.repfix.MonthManageBean;
import com.yss.util.*;
import com.yss.pojo.sys.YssStatus;//报表批量导出功能的提示 by leeyu 20100705 合并太平版本代码

public class ReportServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦 添加---
    private Hashtable userTable = null;
    //-----------------------------------------------
    public ReportServer() {
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

        SingleLogOper logOper = null;
        //---- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦
        FlowBean flow = null;
      	YssStatus runStatus = null;//报表批量导出功能的提示 by leeyu 20100705 合并太平版本代码
        //------------------------------------
        try {
            //--------------------
            Object obj = null;
            // IDataSetting bean = null;
            int operType = 0;
            pub = YssUtil.getSessionYssPub(request);
         	runStatus = YssUtil.getSessionYssStatus(request);//报表批量导出功能的提示 by leeyu 20100705 合并太平版本代码
            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");
                ctx = new FileSystemXmlApplicationContext(
    					YssUtil.getAppConContextPath(path,"report.xml"));
                pub.setAccBookCtx(ctx);
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
                } else if (flag.equalsIgnoreCase("edit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_EDIT);
                    bean.editSetting();
                } else if (flag.equalsIgnoreCase("del")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_DEL);
                    bean.delSetting();
                } else if (flag.equalsIgnoreCase("audit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_AUDIT);
                    bean.checkSetting();
                } else if (flag.equalsIgnoreCase("savamulti")) {
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
                IClientReportView bean = (IClientReportView) obj;
                bean.setYssPub(pub);
            	((BaseBean)bean).setYssRunStatus(runStatus);//报表批量导出功能的提示 by leeyu 20100705 合并太平版本代码
                bean.parseRowStr(squest);
                if (flag.equalsIgnoreCase("reportdata")) {
                    //------  MS00003 QDV4.1-参数布局散乱不便操作 -----
                    flow = new FlowBean();
                    flow.setYssPub(pub);
                    flow.ctlFlowStateAndLogInFun(YssCons.YSS_FLOW_POINTSTATE_EXECUTION); //设置流程正在执行的状态.
                    //----------------------------------------------
                    ou.write(bean.getReportData(showType).getBytes());
                    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.11 蒋锦------------
                    userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
                    //-------------------------------------------------
                }   
                else if(flag.equalsIgnoreCase("exportdata")){//by zhouwei 2011-09-15 导出违规的监控结果
                	ou.write(bean.getReportData(showType).getBytes());
                	userTable.put(pub.getUserCode(), new Boolean(true));
                }
                else if (flag.equalsIgnoreCase("getbooksetname"))
                {
                	flow = new FlowBean();
                	flow.setYssPub(pub);
                    ou.write(bean.GetBookSetName(showType).getBytes());
                } 
                else if (flag.equalsIgnoreCase("reportheaders")) {
                    ou.write(bean.getReportHeaders(showType).getBytes());
                } else if (flag.equalsIgnoreCase("checkdata")) { //add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
                    ou.write(bean.checkReportBeforeSearch(squest).getBytes());
                }/**shashijie 2011.04.07 STORY #805 头寸表应该预测T日到T+N-1日共N个工作日的头寸	保留预估天数*/
                  else if (flag.equalsIgnoreCase("saveday")) {
                	bean.parseRowStr(squest);
                	ou.write(bean.getSaveDefuntDay(squest).getBytes());
				}/**end*/
                //add by huangqirong 2012-11-15 story #3272 添加getOperValue 方法的处理
                else if(flag.equalsIgnoreCase("opervalue")){
                	ou.write(bean.getOperValue(showType).getBytes());                	  
                }
                //---end---
                
                //20130222 added by liubo.Story #3517.
            	//客户要求外管局月报按模板导出的文件的命名规则为：日期+“-”+托管行代码
            	//这个文件名需要在后台生成，然后返回给前台
                else if (flag.equalsIgnoreCase("getRptExpFileName"))
                {
                	com.yss.main.operdeal.report.repfix.MonthManageBean month = new MonthManageBean();
                	month.setYssPub(pub);
                	month.parseRowStr(squest);
                	ou.write(month.getRepExpFileName().getBytes());
                }
                
            } else if (obj instanceof IGuessValueReport) { // liyu modify 1008 为了华夏现金头寸条与基金管理日报表
                IGuessValueReport bean = (IGuessValueReport) obj;
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("getParameter")) {
                    ou.write(bean.getParameter(squest).getBytes());
                } else if (flag.equalsIgnoreCase("getGuessValueReport")) {
                    ou.write(bean.getGuessValueReport(squest).getBytes());
                }else if(flag.equalsIgnoreCase("checkMarketvalue")){
                	ou.write(bean.checkMarketvalue(squest).getBytes());
                } 
                //20110715 added by liubo.Story #1194
                //生成净值统计表和财务估值表的未分配利润，返回给前台
                //***********************************
                else if (flag.equalsIgnoreCase("checkprofit")){
                	ou.write(bean.CheckUndistributedProfit(squest).getBytes());
                }
                //*****************end********************
                
                //20120228 added by liubo.Story #2248
                //通过前台传入的组合跟报表日期，获取每日确认表信息
                //******************************
                else if (flag.equalsIgnoreCase("getRecInfo")){
                	ou.write(bean.getReconcileInfo(squest).getBytes());
                }
                //**************end****************
                
                //20120228 added by liubo.Story #2248
                //通过前台传入的组合跟报表日期，操作每日确认表（插入或删除）
                //******************************
                else if (flag.equalsIgnoreCase("RecInfoOper")){
                	ou.write(bean.ReconcileInfoOperation(squest).getBytes());
                }
                //**************end****************

                //20120228 added by liubo.Story #2248
                //通过前台传入的组合跟报表日期，返回平台锁定/解锁联动信息
                //******************************
                else if (flag.equalsIgnoreCase("LockedStatus")){
                	ou.write(bean.GetLockedStatus(squest).getBytes());
                }
                //**************end****************

                //20120228 added by liubo.Story #2248
                //获取某段时间内有无确认任何一天的估值表
                //******************************
                else if (flag.equalsIgnoreCase("GetDayConfirms")){
                	ou.write(bean.GetDayConfirms(squest).getBytes());
                }
                //**************end****************

                //20120301 added by liubo.Story #2235
                //获取打印估值表时的表尾描述信息
                //******************************
                else if (flag.equalsIgnoreCase("GVReportDescription")){
                	ou.write(bean.GetGVReportDescription(squest).getBytes());
                }
                //**************end****************
                else if (flag.equalsIgnoreCase("buildGuessValueReport")) {
                    operType = YssCons.OP_BUILD;
                    //------  MS00003 QDV4.1-参数布局散乱不便操作 -----
                    flow = new FlowBean();
                    flow.setYssPub(pub);
                    flow.ctlFlowStateAndLogInFun(YssCons.YSS_FLOW_POINTSTATE_EXECUTION); //设置流程正在执行的状态.
                    //----------------------------------------------
                    ou.write(bean.buildGuessValueReport(squest).getBytes());
                    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.11 蒋锦------------
                    userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
                    //-------------------------------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, operType, pub);
                //add by zhouwei 20120305 检查多class的财务系统的资产净值与估值系统的净值是否相同
                } else if (flag.equalsIgnoreCase("checkSomeNetIndexs")) {
                	GuessValue gv=(GuessValue)bean;
                    ou.write(gv.checkSomeNetIndexs(squest).getBytes());
                    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.11 蒋锦------------
                    userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
                    //-------------------------------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, operType, pub);

                } else if (flag.equalsIgnoreCase("setDayConfirm")) { //日终处理
                    ou.write(bean.dayConfirm(squest).getBytes());
                    //-------------------------------------------
                    String[] sData = squest.split("\t");
                    if (sData.length > 3) {
                        if (sData[3].equalsIgnoreCase("1")) {
                            operType = YssCons.OP_DAYCON;
                        } else if (sData[3].equalsIgnoreCase("0")) {
                            operType = YssCons.OP_DAYUNCON;
                        }
                    }
//              OperateLogBean log = new OperateLogBean();
//              log.setOperateType(operType);
//              log.setOperateResult("1");
//              log.setYssPub(pub);
//              log.insertLog(bean);
                    //-----------------------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, operType, pub);
                } else if (flag.equalsIgnoreCase("getCashTCParameter")) {
                    ou.write(bean.getParameter(squest).getBytes());
                } else if (flag.equalsIgnoreCase("getCashTCReport")) {
                    ou.write(bean.getGuessValueReport(squest).getBytes());
                } else if (flag.equalsIgnoreCase("buildCashTCReport")) {
                    ou.write(bean.buildGuessValueReport(squest).getBytes());
                } else if (flag.equalsIgnoreCase("getHXParameter")) {
                    ou.write(bean.getParameter(squest).getBytes());
                } else if (flag.equalsIgnoreCase("getHXReport")) {
                    ou.write(bean.getGuessValueReport(squest).getBytes());
                } else if (flag.equalsIgnoreCase("buildHXReport")) {
                    ou.write(bean.buildGuessValueReport(squest).getBytes());
                }
                //add by fangjiang 2010.12.10 STORY #95 生成财务估值表时，与余额表进行比对，不一致需给出提示，并出报表
                //edit by songjie 2011.02.26 998  QDV4上海(37上线测试)2011年1月26日01_B
	            else if (flag.equalsIgnoreCase("checkbgApp")) {
	                ou.write(bean.checkbgApp(squest).getBytes());
	            }
                //edit by songjie 2011.02.26 998  QDV4上海(37上线测试)2011年1月26日01_B
                //-----------------
	            else if (flag.equalsIgnoreCase("checkRatio")) {//by guyichuan 2011.06.30 STORY #1183
	                ou.write(bean.checkRatio(squest).getBytes());
	            }
            }
            else if (obj instanceof ExportBuildDataCommonRep) {//add by guolongchao 20121006 STORY 1284
            	ExportBuildDataCommonRep bean = (ExportBuildDataCommonRep) obj;
                bean.setYssPub(pub);
                bean.parseRowStr(squest);
                if (flag.equalsIgnoreCase("bulidReport"))//生成报表保存至数据库
                	ou.write(bean.bulidReport().getBytes());
                //add by huangqirong 2012-01-13   STORY 1284
                else if(flag.equalsIgnoreCase("batchdeal")){//处理批量界面 跨组合群请求
                	ou.write(bean.getOperValue(showType).getBytes());
                }
                //---end---
            }
        } catch (Exception ye) {
            //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
            userTable.put(pub.getUserCode(), new Boolean(false)); //若操作失败，则将此结果放入容器中
            //-------------------------------------------------
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            //ou.write(ye.toString().getBytes());
            ou.write(ye.getLocalizedMessage().getBytes()); //系统优化提示信息 调用新的异常处理模块 by 张军锋 2009.02.04 QDV4.1-BugNO:MS00004 指示信息的解析处理
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
