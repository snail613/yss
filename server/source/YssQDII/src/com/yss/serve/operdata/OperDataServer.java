package com.yss.serve.operdata;

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
import com.yss.main.operdata.*;
import com.yss.main.parasetting.PurchaseBean;
import com.yss.main.voucher.VchAssistantSettingBean;
import com.yss.util.*;
import com.yss.pojo.sys.YssStatus;//xuqiji 20090814 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
import com.yss.pojo.sys.YssPageInationBean;//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu

public class OperDataServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
    //----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦 添加---
    private Hashtable userTable = null;
    //-----------------------------------------------
    //delete by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
    //private YssPageInationBean yssPageInationBean =null;//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
    //private String pageType="audit";//页类型，值为 audit,unaudit,recycled  QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
    private String auditNum="1",unAuditNum="1",recycledNum="1";//页码，值为整数  QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
    private String createQueryView="true";//创建查询视图，值为true,false (true为创建) QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
    
    /**shashijie 2011.03.18 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
    private String settleNum="1",settleNoNum="1",settleDelayNum="1",settleBackNum="1";
    /**~~~~~~~~~~~~end~~~~~~~~~~*/
    public OperDataServer() {
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
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110907 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110907 STORY 1285 读数完成后是否浏览数据          
        String[] Array=null;
        if(showType!=null)
        {
        	  showType=(new String(showType.getBytes("ISO-8859-1"),"gb2312")).trim();//add by baopingping STORY #1171 20110627 转码 
        	  Array=showType.split("\f");//add by baopingping STORY #1171 20110627 解析字符串
        }
       String sPageLine = request.getParameter("pageamount");//add by wuweiqi 20101122 添加显示行数参数  QDV4华夏2010年10月27日02_A
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
        IDataSetting bean = null;
        RightEquityBean rightbean = null;
        int operType = 0;
        //--------------------
        SingleLogOper logOper = null;
        //---- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦
        FlowBean flow = null;
        //------------------------------------
        YssStatus runStatus = null;//xuqiji 20090814 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
        try {
            pub = YssUtil.getSessionYssPub(request);
            runStatus = YssUtil.getSessionYssStatus(request);//xuqiji 20090814 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");
                ctx = new FileSystemXmlApplicationContext(
    					YssUtil.getAppConContextPath(path,"operdata.xml"));
                pub.setOperDataCtx(ctx);
            }
            obj = ctx.getBean(beanId.toLowerCase());
            if(sPageLine!= null && !sPageLine.equals(""))
            {
               pub.setIPageCount(Integer.parseInt(sPageLine));//add by wuweiqi 20101122 QDV4华夏2010年10月27日02_A
            }
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110914 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   
          //delete by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
//            yssPageInationBean =new YssPageInationBean();//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
//            yssPageInationBean.setYssPub(pub);//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
            //add baopingping #story 1167  20110719 解析前台传来的字符串
            String sTmpStr = "";
            String sRecycled = "";
            int count=0;
            int num=0;
            String[] reqAry = null;
            if(squest.length()!=0){
            	if (squest.indexOf("\r\t") >= 0) {
    				sTmpStr = squest.split("\r\t")[0];
    			} else {
    				sTmpStr = squest;
    			}
    			sRecycled = squest; // 把未解析的字符串先赋给sRecycled
    			reqAry = sTmpStr.split("\t");
    			for(int i=0;i<reqAry.length;i++){
    				if(reqAry[i].equalsIgnoreCase("ok")){
    					count=i;
    					break;
    				}
    			}
    		    for(int j=0;j<reqAry.length;j++){
        				if(reqAry[j].equalsIgnoreCase("yes")){
        					num=j;
        					break;
        				}
    				}
    			
            }
            //--------end---------
            if (obj instanceof IDataSetting) {
                bean = (IDataSetting) obj;
                //       IOperData bean = (IOperData) ctx.getBean(beanId.toLowerCase());
                bean.setYssPub(pub);
                ( (BaseBean) bean).setYssRunStatus(runStatus);//xuqiji 20090814 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                if (flag.equalsIgnoreCase("add")) {//modify  baopingping #story 1167 20110719 添加跨组合的增，删，改，审核和反审核处理
                	  bean.parseRowStr(squest);
                    if(reqAry[count].equalsIgnoreCase("ok")){
                    	bean.checkInput(YssCons.OP_ADD);
                      bean.saveMutliSetting(flag);
                    }else{
                      bean.parseRowStr(squest);
                      bean.checkInput(YssCons.OP_ADD);
                      operType = YssCons.OP_ADD;
                      //20120111 added by liubo.Story #1927
                      //维护模板设置界面，往主表中插入数据后（addSetting）需要向前台返回又后台自动生成的模板编号，以便插入明细表数据
                      //=======================================
                      if ("maintenancetpl".equals(beanId))
                      {
                    	  ou.write(bean.addSetting().getBytes());
                    	  return;
                      }
                      else
                      {
                    	  bean.addSetting();
                      }
                      //==================end=====================
                    }
                    //-----------------------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_ADD, pub);
                }
                //added by liubo.Story #1770
                //=============================
                else if (flag.equalsIgnoreCase("getAssetGroupList")) 
                {
                	DividendBean dividend = new DividendBean();
                	dividend.setYssPub(pub);
                	ou.write(dividend.getAssetGroupList().getBytes());
                }
                else if (flag.equalsIgnoreCase("getAssetGroupName"))
                {
                	DividendBean dividend = new DividendBean();
                	dividend.setYssPub(pub);
                	dividend.parseRowStr(squest);
                	ou.write(dividend.returnAssetGroupName().getBytes());
                }
                //=============end================
                
                //added by liubo.Story #1927
                //-------------------------------------------
                else if (flag.equalsIgnoreCase("dataDetail"))
                {
                	MaintenanceTemplateBean tpl = new MaintenanceTemplateBean();
                	tpl.setYssPub(pub);
                	tpl.parseRowStr(squest);
                	ou.write(tpl.AddTplDataDetail().getBytes());
                	
                }
                //------------------end-------------------------
                
                //added by liubo.Story #1927
                //+++++++++++++++++++++++++++++++++++++++++
                else if (flag.equalsIgnoreCase("getTimeInterval"))
                {
                	MaintenanceTemplateBean tpl = new MaintenanceTemplateBean();
                	tpl.setYssPub(pub);
                	tpl.parseRowStr(squest);
                	ou.write(tpl.getTimeInterval().getBytes());
                }
                //+++++++++++++++++end++++++++++++++++++++++++
                
                //added by liubo.Story #1927
                //-------------------------------------------
                else if (flag.equalsIgnoreCase("getTplDataDetail"))
                {
                	MaintenanceTemplateBean tpl = new MaintenanceTemplateBean();
                	tpl.setYssPub(pub);
                	ou.write(tpl.getTplDataDetail(showType).getBytes());
                	
                }
                //------------------end-------------------------
                
                else if (flag.equalsIgnoreCase("edit")) {
                    bean.parseRowStr(squest);
                    logOper = SingleLogOper.getInstance();
                    logOper.setBData(bean);
                    operType = YssCons.OP_EDIT;
                    if(reqAry[count].equalsIgnoreCase("ok")){
                    	bean.saveMutliSetting(flag);
                    }else{
                    bean.checkInput(YssCons.OP_EDIT);
                    bean.editSetting();
                    }
                    logOper.setIData(bean, YssCons.OP_EDIT, pub);

                } else if (flag.equalsIgnoreCase("del")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_DEL);
                    operType = YssCons.RS_DEL;
                    if(reqAry[count].equalsIgnoreCase("ok")){
                    	bean.saveMutliSetting(flag);
                    }else{
                    bean.delSetting();
                    }
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_DEL, pub);

                } else if (flag.equalsIgnoreCase("audit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_AUDIT);
                    operType = YssCons.OP_AUDIT;
                    if(reqAry[count].equalsIgnoreCase("ok")){
                    	bean.saveMutliSetting(flag);
                    }else{
                    bean.checkSetting();
                    }
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_AUDIT, pub);

                }else if(flag.equalsIgnoreCase("copy")){//add baopingping #story 1167 20110721 增加一个跨组合的复制操作
                	 bean.parseRowStr(squest);
                	 if(reqAry[count].equalsIgnoreCase("ok"))
                	 {
                	 bean.saveMutliSetting(flag);
                	 }
                	 else
                	 {
                		 if(reqAry[num].equalsIgnoreCase("yes")){
                			 bean.saveMutliSetting(flag);
                		 }else{
                			 bean.parseRowStr(squest);
                             bean.checkInput(YssCons.OP_ADD);
                			 bean.addSetting();
                		 }
                	 }
                }else if(flag.equalsIgnoreCase("checked")){
                	        bean.parseRowStr(squest);
                	        ou.write(bean.saveMutliSetting(flag).getBytes());
                	
           	   }//--------end--------
                else if (flag.equalsIgnoreCase("clear")) { //添加回收站清除功能
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
                else if (flag.equalsIgnoreCase("savamulti")) {
                    bean.saveMutliSetting(squest);
                    //----------------add by guojianhua 2010 09 29  完善日志-------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_ADD, pub);
                    //---------------------end-----------------
                } else if (flag.equalsIgnoreCase("opervalue")) {
                    bean.parseRowStr(squest);
                    ou.write(bean.getOperValue(showType).getBytes());
                }else if (flag.equalsIgnoreCase("saveallmulti")){//add by yanghaiming 20091210 MS00828 QDV4中保2009年11月24日01_A
                	ou.write(((TradeSubBean)bean).saveAllMutliSetting(squest));
                }else if(flag.equalsIgnoreCase("purchcode")){
                	//add by baopingping STORY #1171 20110627 
                	//查出期限代码
                	PurchaseBean purbean=new PurchaseBean();
                	purbean.setYssPub(pub);
                	String qurcode=Array[0].toString();
                	qurcode=(new String(qurcode.getBytes("ISO-8859-1"),"gb2312")).trim();//add by baopingping STORY #1171 20110627 转码 
                	ou.write(purbean.getQurcode(qurcode).getBytes());
                }
                else if(flag.equalsIgnoreCase("qortcode"))
                {//add by baopingping STORY #1171 20110627  
                	//查出参数设置的值
                	PurchaseBean purbean=new PurchaseBean();
                	String str="";
                	String txtValue="txtValue";
                	String txtDays1="txtDays1";
                	String txtDays="txtDays";
                	purbean.setYssPub(pub);
                	String qortcode=Array[1];
                	qortcode=(new String(qortcode.getBytes("ISO-8859-1"),"gb2312")).trim();//add by baopingping STORY #1171 20110627 转码 
                	String purName=Array[2].toString();
                	purName=(new String(purName.getBytes("ISO-8859-1"),"gb2312")).trim();////add by baopingping STORY #1171 20110627 转码 
                	if(txtValue.equalsIgnoreCase("txtValue"))
                	{
                		str+=purbean.getQurMoney(qortcode,txtValue,purName)+"\f";
                	}
                	if(txtDays1.equalsIgnoreCase("txtDays1"))
                	{
                		str+=purbean.getQurMoney(qortcode,txtDays1,purName)+"\f";
                	}
                	if(txtDays.equalsIgnoreCase("txtDays"))
                	{
                		str+=purbean.getQurMoney(qortcode,txtDays,purName);
                	}
                	ou.write(str.getBytes());
//------end------
                }else if(flag.equalsIgnoreCase("bailType")){//-----add  by zhaoxianlin 20121129 STORY #3371 取期货信息设置保证金类型
                	 String bailType="";
                	 bean.parseRowStr(squest);
                	 bailType = bean.getAllSetting();
                	 ou.write(bailType.getBytes());
                }
              //------end------
                if (showType != null && showType.length() > 0) {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, showType,pub).getBytes());//modify by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
                } else {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, flag,pub).getBytes());//modify by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
                }
            } else if (obj instanceof RightEquityBean) {
                rightbean = (RightEquityBean) obj;
                rightbean.setYssPub(pub);
                if (flag.equalsIgnoreCase("doOperation")) {
                    rightbean.parseRowStr(squest);
                    rightbean.doOperation(flag);
                    ou.write(rightbean.getListViewData1().getBytes());
                } else if (flag.equalsIgnoreCase("listviewType")) {
                    rightbean.parseRowStr(squest);
                    ou.write(rightbean.getListViewTypeData().getBytes());
                }
            }
        } catch (Exception ye) {
            try {
                logOper = SingleLogOper.getInstance();
                logOper.setIData(bean, operType, pub, true);
            } catch (YssException e) {
                e.printStackTrace();
            }

            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            ou.write(ye.getLocalizedMessage().getBytes()); //系统优化提示信息 调用新的异常处理模块 by 张军锋 2009.02.04 QDV4.1-BugNO:MS00004 指示信息的解析处理
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
	            pub.setBrown(false);//add by guolongchao 20110915 STORY 1285 将pub中的isBrown属性设为false;
	            pub.setTabMainCode("");//add by guolongchao 20110915 STORY 1285 将pub中的tabMainCode属性清空;
	            
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
        	//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
            sResult = bean.getListViewData1();
        }
        //用于获取前台选择框ListView控件的当前可用的数据
        else if (sShowType.equalsIgnoreCase("listview2")) {
        	/**shashijie 2011.03.17 */
        	if(bean instanceof BaseDataSettingBean){
        		if(createQueryView.equalsIgnoreCase("true"))
        			yssPageInationBean.setbCreateView(true);
        		else
        			yssPageInationBean.setbCreateView(false);
        		yssPageInationBean.setiAuditCurrPage(YssFun.toInt(auditNum));
        		yssPageInationBean.setiUnAuditCurrPage(YssFun.toInt(unAuditNum));
        		yssPageInationBean.setiRecycledCurrPage(YssFun.toInt(recycledNum));
        		/**shashijie 2011.03.19 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
        		yssPageInationBean.setiSettleCurrPage(YssFun.toInt(settleNum));
        		yssPageInationBean.setiSettleNoCurrPage(YssFun.toInt(settleNoNum));
        		yssPageInationBean.setiSettleDelayCurrPage(YssFun.toInt(settleDelayNum));
        		yssPageInationBean.setiSettleBackCurrPage(YssFun.toInt(settleBackNum));
        		/**end*/
        		((BaseDataSettingBean)bean).setYssPageInationBean(yssPageInationBean);
        	}
        	sResult = bean.getListViewData2();
        }
        //用于获取前台特殊ListView控件的数据
        else if (sShowType.equalsIgnoreCase("listview3")) {
            sResult = bean.getListViewData3();
        }
        //用于获取前台查询列表的ListView控件的所有的数据
        
        //20110902modified by liubo.Bug #2543
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
