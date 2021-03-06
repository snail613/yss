package com.yss.serve.setting;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.context.*;
import org.springframework.context.support.*;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.parasetting.InvestPayBean;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.syssetting.AssetGroupBean;
import com.yss.pojo.sys.YssPageInationBean;
import com.yss.util.*;

public class ParaSetting extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;
	// ----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦添加---
	private Hashtable userTable = null;
	// ----------------------------------------
	// add by fangjiang 2010.11.13 bug 254
	//delete by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
	//private YssPageInationBean yssPageInationBean = null;
	private String auditNum = "1", unAuditNum = "1", recycledNum = "1";// 页码，值为整数
	private String createQueryView = "true";// 创建查询视图，值为true,false (true为创建)
	// ---------
	
	public ParaSetting() {
	}

	public void init() throws ServletException {
		// ----MS00003 QDV4.1-参数布局散乱不便操作 蒋锦 2009.03.10---
		userTable = new Hashtable(); // 在servlet初始化时将此用户操作结果容器生成。
		// ----------------------------------------
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("GB2312"); // 设置了以后，parameter就不要转编码了
		response.setContentType(YssCons.CONTENT_TEXT);
		OutputStream ou = (OutputStream) response.getOutputStream();
		String squest = com.yss.util.YssUtil.getBytesStringFromClient(request);
		System.out.println(squest);
		String beanId = request.getParameter("cmd");
		String flag = request.getParameter("flag");
		String showType = request.getParameter("showtype");
		//add by songjie 20110127 BUG:1000 QDV4赢时胜(测试)2011年01月26日1_B
		String sPageLine = request.getParameter("pageamount");
		String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
		String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据    
		
		//add by guolongchao 20120119 STORY 1284  跨组合群加载数据--------------start
		  String tabPrefix = request.getParameter("tabPrefix");  
		  String currentTabPrefix="";//pub中保存的登录表前缀
		//add by guolongchao 20120119 STORY 1284  跨组合群加载数据--------------end
		YssPub pub = null;
		
		// --------------------
		Object obj = null;
		IDataSetting bean = null;
		int operType = 0;
		// --------------------
		SingleLogOper logOper = null;
		// ---- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦
		FlowBean flow = null;
		// ------------------------------------
		// add by fangjiang 2010.11.13 bug 254
		auditNum = "1";
		unAuditNum = "1";
		recycledNum = "1";
		createQueryView = "true";
		if (request.getParameter("auditnum") != null)
			auditNum = request.getParameter("auditnum");
		if (request.getParameter("unauditnum") != null)
			unAuditNum = request.getParameter("unauditnum");
		if (request.getParameter("recyclednum") != null)
			recycledNum = request.getParameter("recyclednum");
		if (request.getParameter("createquery") != null)
			createQueryView = request.getParameter("createquery");
		// -------------
		try {
			pub = YssUtil.getSessionYssPub(request);
			if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
	            String path = this.getServletContext().getRealPath("");
				ctx = new FileSystemXmlApplicationContext(
						YssUtil.getAppConContextPath(path,"parasetting.xml"));
				pub.setParaSettingCtx(ctx);
			}
			//add by guolongchao 20120119 STORY 1284  跨组合群加载数据--------------start
			 currentTabPrefix=pub.getPrefixTB();
			 if(tabPrefix!=null&&tabPrefix.length()>0 && !currentTabPrefix.equals(tabPrefix)) //modify huangqirong 2012-08-13 story #2831 
			        pub.setPrefixTB(tabPrefix);
			//add by guolongchao 20120119 STORY 1284  跨组合群加载数据--------------end
			obj = ctx.getBean(beanId.toLowerCase());
			
			//add by songjie 2011.01.27 BUG:1000 QDV4赢时胜(测试)2011年01月26日1_B
            if(sPageLine!= null && !sPageLine.equals("")){
            pub.setIPageCount(Integer.parseInt(sPageLine));
            }
            //add by songjie 2011.01.27 BUG:1000 QDV4赢时胜(测试)2011年01月26日1_B
            
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   
            
			// add by fangjiang 2010.11.13 bug 254
          //delete by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
//			yssPageInationBean = new YssPageInationBean();
//			yssPageInationBean.setYssPub(pub);
			// -----------
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

					/**shashijie 2012-5-29 BUG 4668 */
					logOper = SingleLogOper.getInstance();
					logOper.setBData(bean);  
					/**end*/
					//modify by zhangjun 2011-12-30 Story #1273
					operType = YssCons.OP_EDIT;
					bean.editSetting();

					logOper.setIData(bean, YssCons.OP_EDIT, pub);
				} else if (flag.equalsIgnoreCase("del")) {
					bean.parseRowStr(squest);
					bean.checkInput(YssCons.OP_DEL);
					//edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
					operType = YssCons.OP_DEL;
					bean.delSetting();

					logOper = SingleLogOper.getInstance();
					logOper.setIData(bean, YssCons.OP_DEL, pub);
				} else if(flag.startsWith("create")){
					bean.parseRowStr(squest);
					bean.checkInput(YssCons.OP_ADD);
					operType = YssCons.OP_ADD;
					bean.getOperValue(flag);
					logOper = SingleLogOper.getInstance();
					logOper.setIData(bean, YssCons.OP_ADD, pub);
				}else if(flag.startsWith("judge")){
					PortfolioBean portfolioBean = new PortfolioBean();
					String  status = portfolioBean.getFlag(flag);
					if(status.equals("already"))
					{
						ou.write("already".getBytes());
					}
					
				}
				else if(flag.equalsIgnoreCase("audit")) {
					bean.parseRowStr(squest);
					operType = YssCons.OP_AUDIT;
					bean.checkSetting();
					logOper = SingleLogOper.getInstance();
					logOper.setIData(bean, YssCons.OP_AUDIT, pub);

				} 
				//added by liubo.Story #1770
				//在此需求中提到的几个模块，在选择组合时需要通过所选择的组合群进行限定
				//===============================
				else if (flag.equalsIgnoreCase("getPortCode"))
				{
					PortfolioBean folio = new PortfolioBean();
					folio.setYssPub(pub);
					ou.write(folio.getPortCodeByAssetGroupCode(showType).getBytes());
				}
				//============end===================
				else if (flag.equalsIgnoreCase("clear")) { // 添加回收站清除功能
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
					bean.parseRowStr(squest);
					bean.saveMutliSetting(squest);
				} else if (flag.equalsIgnoreCase("opervalue")) {
					bean.parseRowStr(squest);
					
		            //20120314 added by liubo.Bug #3933.实现投资运营收支界面的分页显示
		            //============================
					if (bean instanceof InvestPayBean)
					{
						YssPageInationBean yssPageInationBean = new YssPageInationBean();
						yssPageInationBean.setYssPub(pub);
						if (createQueryView.equalsIgnoreCase("true"))
							yssPageInationBean.setbCreateView(true);
						else
							yssPageInationBean.setbCreateView(false);
						yssPageInationBean.setiAuditCurrPage(YssFun.toInt(auditNum));
						yssPageInationBean
								.setiUnAuditCurrPage(YssFun.toInt(unAuditNum));
						yssPageInationBean.setiRecycledCurrPage(YssFun
								.toInt(recycledNum));
						((BaseDataSettingBean) bean)
								.setYssPageInationBean(yssPageInationBean);
						
						yssPageInationBean = null;
					}
		            //==============end=============
					
					ou.write(bean.getOperValue(showType).getBytes());
				} else if (flag.equalsIgnoreCase("allsetting")) {
					bean.parseRowStr(squest);
					ou.write(bean.getAllSetting().getBytes());
				} 

				if (showType != null && showType.length() > 0) {
					bean.parseRowStr(squest);
					ou.write(getData(bean, showType,pub).getBytes());//modify by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
				} else {					
					bean.parseRowStr(squest);
					ou.write(getData(bean, flag,pub).getBytes());//modify by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
				}
			}else if(obj instanceof AssetGroupBean){//add by guyichuan 20110609 STORY #897
				//STORY #897 需要根据是否选择“多组合群处理”来查询组合群
				AssetGroupBean groupbean=(AssetGroupBean)obj;
				groupbean.setYssPub(pub);
				groupbean.parseRowStr(squest);
				ou.write(getData(groupbean, flag).getBytes());
			}//--end-STORY #897---
		} catch (Exception ye) {
			try {
				logOper = SingleLogOper.getInstance();
				logOper.setIData(bean, operType, pub, true);
			} catch (YssException e) {
				e.printStackTrace();
			}
			// -----------------------------------------

			response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
			// 输出错误信息
			response.resetBuffer();
			// ou.write(ye.toString().getBytes()); MS00008 delete by 宋洁
			// 2009-03-06
			ou.write(ye.getLocalizedMessage().getBytes()); // MS00008 add by 宋洁
															// 2009-03-06
			ou.close();
		}
		// -------- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10
		// 蒋锦------------------------------
		finally {
			try {
				if (pub!=null&&null != pub.getUserCode() && pub.getUserCode().length() > 0) {
					flow = new FlowBean();
					flow.setYssPub(pub);
					flow.ctlFlowStateAndLog((Boolean) userTable.get(pub
							.getUserCode())); // 操作流程状态和日志数据
					userTable.remove(pub.getUserCode()); // 清除个用户的信息.
					pub.setBrown(false);//add by guolongchao 20110916 STORY 1285 将pub中的isBrown属性设为false;
					pub.setTabMainCode("");//add by guolongchao 20110916 STORY 1285 将pub中的tabMainCode属性清空;
					if(tabPrefix != null && !tabPrefix.equals(currentTabPrefix)) //add by huangqirong 2012-08-13 story #2831
						pub.setPrefixTB(currentTabPrefix);//add by guolongchao 20120119 STORY 1284     将pub中的表前缀替换为原来的值
					
				}
			} catch (YssException ex) {
				ou.write(ex.getLocalizedMessage().getBytes());
			}
			
			
		}
		// ------------------------------------------------------------------------
	}

	private String getData(IDataSetting bean, String sShowType,YssPub pub)
			throws YssException {
		String sResult = "";
		
		//add by jsc 20120424 【BUG4310tgb-18的JVM内存溢出】
        YssPageInationBean yssPageInationBean = new YssPageInationBean();
        yssPageInationBean.setYssPub(pub);
        
		// 用于获取前台查询列表的ListView控件的当前可用的数据
		if (sShowType.equalsIgnoreCase("listview1")) {
			// add by fangjiang 2010.11.13 bug 254
			if (bean instanceof BaseDataSettingBean) {
				if (createQueryView.equalsIgnoreCase("true"))
					yssPageInationBean.setbCreateView(true);
				else
					yssPageInationBean.setbCreateView(false);
				yssPageInationBean.setiAuditCurrPage(YssFun.toInt(auditNum));
				yssPageInationBean
						.setiUnAuditCurrPage(YssFun.toInt(unAuditNum));
				yssPageInationBean.setiRecycledCurrPage(YssFun
						.toInt(recycledNum));
				((BaseDataSettingBean) bean)
						.setYssPageInationBean(yssPageInationBean);
			}
			// --------------------
			sResult = bean.getListViewData1();
		}
		// 用于获取前台选择框ListView控件的当前可用的数据
		else if (sShowType.equalsIgnoreCase("listview2")) {
			sResult = bean.getListViewData2();
		}
		// 用于获取前台特殊ListView控件的数据
		else if (sShowType.equalsIgnoreCase("listview3")) {
			// add by fangjiang 2010.11.13 bug 254
			if (bean instanceof BaseDataSettingBean) {
				if (createQueryView.equalsIgnoreCase("true"))
					yssPageInationBean.setbCreateView(true);
				else
					yssPageInationBean.setbCreateView(false);
				yssPageInationBean.setiAuditCurrPage(YssFun.toInt(auditNum));
				yssPageInationBean
						.setiUnAuditCurrPage(YssFun.toInt(unAuditNum));
				yssPageInationBean.setiRecycledCurrPage(YssFun
						.toInt(recycledNum));
				((BaseDataSettingBean) bean)
						.setYssPageInationBean(yssPageInationBean);
			}
			// --------------------
			sResult = bean.getListViewData3();
		}
		// 用于获取前台查询列表的ListView控件的所有的数据
		else if (sShowType.equalsIgnoreCase("listview4")) {
			// add by nimengjing 2010 11 26 bug 490
			if (bean instanceof BaseDataSettingBean) {
				if (createQueryView.equalsIgnoreCase("true"))
					yssPageInationBean.setbCreateView(true);
				else
					yssPageInationBean.setbCreateView(false);
				yssPageInationBean.setiAuditCurrPage(YssFun.toInt(auditNum));
				yssPageInationBean
						.setiUnAuditCurrPage(YssFun.toInt(unAuditNum));
				yssPageInationBean.setiRecycledCurrPage(YssFun
						.toInt(recycledNum));
				((BaseDataSettingBean) bean)
						.setYssPageInationBean(yssPageInationBean);
			}
			// ----------------------bug 490-----------------------
			sResult = bean.getListViewData4();
		}
		// 用于获取前台查询列表的TreeView控件所有的数据
		else if (sShowType.equalsIgnoreCase("treeview1")) {
			sResult = bean.getTreeViewData1();
		}
		// 用于获取前台选择框TreeView控件的当前可用的数据
		else if (sShowType.equalsIgnoreCase("treeview2")) {
			sResult = bean.getTreeViewData2();
		} else if (sShowType.equalsIgnoreCase("treeview3")) {
			sResult = bean.getTreeViewData3();
		}
		// / <summary>
		// / 修改人：fanghaoln
		// / 修改时间:20090512
		// / BugNO :MS00001 QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
		// / 在这加的方法查出组合群代码和组合代码这是因为前台以前调用opervalue不在listview里面
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
	//add by guyichuan 20110608 STORY #897 
	private String getData(AssetGroupBean bean, String sShowType)
	throws YssException {
		String sResult = "";
		if("listview1".equalsIgnoreCase(sShowType)){
			sResult=bean.getListViewData1();
		}
		//add by huangqirong 2011-06-20 story #1284
		else if(sShowType.equalsIgnoreCase("getallgroup")){
			AssetGroupBean groupBean=(AssetGroupBean)bean;
			sResult=groupBean.getListViewGroups();			
		}
		//---end---
		return sResult;
	}

	// Process the HTTP Post request
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	// Clean up resources
	public void destroy() {
	}

}
