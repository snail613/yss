package com.yss.serve.setting;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.basesetting.InvestPayCatBean;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class BaseSetting
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦添加---
    private Hashtable userTable = null;
    //----------------------------------------
    public BaseSetting() {
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
        //2008.02.19 蒋锦 添加
        String showType = request.getParameter("showtype");
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据   
        YssPub pub = null;
        String listFlag = "";
        String[] strRow;

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
    					YssUtil.getAppConContextPath(path,"basesetting.xml"));
                pub.setBaseSettingCtx(ctx);
            }
            
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   
          
            obj = ctx.getBean(beanId.toLowerCase());
            if (obj instanceof IDataSetting) {
                bean = (IDataSetting) obj;
                //    IBaseSetting bean = (IBaseSetting) ctx.getBean(beanId.toLowerCase());
                if (flag.indexOf(",") >= 0) {
                    strRow = flag.split(",");
                    flag = strRow[0];
                    listFlag = strRow[1];
                }
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("add")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_ADD);
                    operType = YssCons.OP_ADD;
                    bean.addSetting();

                    //-----------------------------------------
//               OperateLogBean log = new OperateLogBean();
//               log.setOperateType(YssCons.OP_ADD);

//               log.setOperateResult("1");
//               log.setYssPub(pub);
//               log.insertLog(bean);
                    //----------------------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_ADD, pub);

                    if (listFlag.equalsIgnoreCase("tree")) {
                        ou.write(bean.getTreeViewData1().getBytes());
                    } else {
                        ou.write(bean.getListViewData1().getBytes());
                    }

                } else if (flag.equalsIgnoreCase("edit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_EDIT);

                    //---------------------------------------------------------
//               OperateLogBean log = new OperateLogBean();
//               log.setBData( ( (IDataSetting) obj).getBeforeEditData());
                    //---------------------------------------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setBData(bean);
                    operType = YssCons.OP_EDIT;
                    bean.editSetting();

                    //-------------------------------------------------
//               log.setOperateType(YssCons.OP_EDIT);

//               log.setOperateResult("1");
//               log.setYssPub(pub);
//               log.insertLog(bean);
                    //-------------------------------------------------
                    logOper.setIData(bean, YssCons.OP_EDIT, pub);

                    if (listFlag.equalsIgnoreCase("tree")) {
                        ou.write(bean.getTreeViewData1().getBytes());
                    } else {
                        ou.write(bean.getListViewData1().getBytes());
                    }
                } else if (flag.equalsIgnoreCase("del")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_DEL);
                    operType = YssCons.RS_DEL;
                    bean.delSetting();

                    //-----------------------------------------
//               OperateLogBean log = new OperateLogBean();
//               log.setOperateType(YssCons.OP_DEL);

//               log.setOperateResult("1");
//               log.setYssPub(pub);
//               log.insertLog(bean);
                    //-----------------------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_DEL, pub);

                    if (listFlag.equalsIgnoreCase("tree")) {
                        ou.write(bean.getTreeViewData1().getBytes());
                    } else {
                        ou.write(bean.getListViewData1().getBytes());
                    }
                } else if (flag.equalsIgnoreCase("audit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_AUDIT);
                    operType = YssCons.OP_AUDIT;
                    //add by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A
                    pub.setDealStatus(flag);
                    bean.checkSetting();

                    //-----------------------------------------
//               OperateLogBean log = new OperateLogBean();
//               log.setOperateType(YssCons.OP_AUDIT);

//               log.setOperateResult("1");
//               log.setYssPub(pub);
//               log.insertLog(bean);
//-----------------------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_AUDIT, pub);

                    if (listFlag.equalsIgnoreCase("tree")) {
                        ou.write(bean.getTreeViewData1().getBytes());
                    } else {
                        ou.write(bean.getListViewData1().getBytes());
                    }

                } else if (flag.equalsIgnoreCase("clear")) { //添加回收站清除功能
                    bean.parseRowStr(squest);
                    bean.deleteRecycleData();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_CLEAR;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_CLEAR, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    
                    ou.write(bean.getListViewData1().getBytes()); //add by MaoQiwen  20080818  bug:0000355
                } else if (flag.equalsIgnoreCase("revert")) { // 添加回收站的还原功能
                    bean.parseRowStr(squest);
                    //add by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A
                    pub.setDealStatus(flag);
                    bean.checkSetting();
                    
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    operType = YssCons.OP_REVERT;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_REVERT, pub);
                    //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    
                    ou.write(bean.getListViewData1().getBytes());
                }

                else if (flag.equalsIgnoreCase("savamulti")) {
                    bean.saveMutliSetting(squest);
                }
                //--------2008.02.19 添加 蒋锦-----------//
                else if (flag.equalsIgnoreCase("opervalue")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getOperValue(showType).getBytes());
                }
                //--------------------------------------//
                else if (flag.equalsIgnoreCase("listview1")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getListViewData1().getBytes());
                } else if (flag.equalsIgnoreCase("listview2")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getListViewData2().getBytes());
                } else if (flag.equalsIgnoreCase("listview3")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getListViewData3().getBytes());
              //MS01354   add by zhangfa 20100712 MS01354    QDV4赢时胜(上海)2010年06月25日01_A         
                }else if (flag.equalsIgnoreCase("listview4")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getListViewData4().getBytes());
                }else if(flag.equalsIgnoreCase("listview5")){//add baopingping #story 1183 20110718 只要查出两费运营类数据
                	  bean.parseRowStr(squest);
                	  InvestPayCatBean IV=new InvestPayCatBean();
                	  IV.setYssPub(pub);
                      ou.write(IV.getListViewData5().getBytes());
                }
              //-------------------------------------------------------------------------------  

                else if (flag.equalsIgnoreCase("treeview1")) {
                    ou.write(bean.getTreeViewData1().getBytes());
                } else if (flag.equalsIgnoreCase("treeview2")) {
                    ou.write(bean.getTreeViewData2().getBytes());
                }
            }
        } catch (Exception ye) {

//         try {
//            //-----------------------------------------
//            OperateLogBean log = new OperateLogBean();
//            log.setOperateType(operType);
//            log.setOperateResult("0");
//            log.setYssPub(pub);
//            log.insertLog(bean);
//         }
//         catch (Exception e) {
//            e.printStackTrace();
//         }
            //-----------------------------------------
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
            //ou.write(ye.toString().getBytes());MS00007 delete by songjie 2009.03.30
            ou.write(ye.getLocalizedMessage().getBytes()); //MS00007 add by songjie 2009.03.30
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

    //Process the HTTP Post request
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        doGet(request, response);
    }

    //Clean up resources
    public void destroy() {
    }

}
