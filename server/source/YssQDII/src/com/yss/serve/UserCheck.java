package com.yss.serve;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.yss.commeach.EachPubDataOper;
import com.yss.dbupdate.CtlDbUpdate;
import com.yss.dsub.DbBase;
import com.yss.dsub.GenDBInfo;
import com.yss.dsub.UserRight;
import com.yss.dsub.YssLicence;
import com.yss.dsub.YssPrint;
import com.yss.dsub.YssPub;
import com.yss.log.OperateLogBean;
import com.yss.log.SingleLogOper;
import com.yss.main.syssetting.AssetGroupBean;
import com.yss.main.syssetting.HttpsConfigBean;
import com.yss.main.syssetting.PassComplexBean;
import com.yss.main.syssetting.RightBean;
import com.yss.main.syssetting.UserBean;
import com.yss.pojo.sys.YssStatus;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.util.YssType;
import com.yss.util.YssUtil;
/**
 * <p>Title: UserCheck </p>
 * <p>Description:用户登陆连接模块 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class UserCheck
    extends HttpServlet{
    // private static final String CONTENT_TYPE = "text/html; charset=GBK";
    //private Vector vctUser = null; //在线用户统计功能，只要储存session即可
    //private HashMap hashmapLog = null; //统计用户登录密码错误次数
    private int logInt = 0; //用户登录密码容错次数
    //private HashMap hmOnLineUser; //当前在线的用户   
    private static final int TIME_OUT = 6000; //客户端这么多秒没有ping服务器，就认为断线
    //private HashMap hmUserSet; //add by fangjiang 2010.11.17 BUG #438 同一个用户无法登录多个组合群
    /**shashijie 2012-3-14 STORY 2340 */
	public static String requestPath = "";//服务器路径
	/**end*/
	
	/**shashijie 2013-1-15 BUG 6657 一个数据库在升级，重新打开另一个客户端连接其他的库提示不能登录*/
	//private static String updatingInfo = null;
	private static HashMap updatingInfo = new HashMap();
	/**end shashijie 2013-1-15 BUG */
	
    //Initialize global variables
	private ApplicationContext ctx;
	//存储当前被强制退出的用户集合
    private HashMap hmEnforceUser= null;//add by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出
	private boolean isFirstLogin=false;//add by zhouwei 20120214 story 2188 标志是否第一次登陆 初始化预警系统
    public void init() throws ServletException {
//        vctUser = new Vector();
//        hashmapLog = new HashMap();
//        hmOnLineUser = new HashMap();
//        //hmUserSet = new HashMap();//add by fangjiang 2010.11.17 BUG #438 同一个用户无法登录多个组合群
        hmEnforceUser=new HashMap();//add by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出
    }

    //Process the HTTP Get request
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        request.setCharacterEncoding("GB2312"); //设置了以后，parameter就不要转编码了
        response.setContentType(YssCons.CONTENT_TEXT);
        OutputStream ou = (OutputStream) response.getOutputStream();
        //ChrStream ou=new ChrStream(response.getOutputStream());字符压缩的情况，暂时不考虑
        HttpSession session = request.getSession();
        String cmd = request.getParameter("cmd");
        String dblbl = request.getParameter("dblbl");
        String bSafeMode = request.getParameter("safemode");//MS01044 QDV4招商基金2010年3月19日03_A 对数据库配置文件进行加密设置   panjunfang modify 20100407 
        String sFlag = request.getParameter("flag");	//20120702 added by liubo.Story #2736.此变量用于判断前台进行ping操作时需不需要在控制台显示ping操作的信息
        String strUserCode = null;
        String set = null;
        String sLogInType = "";
        int iUserLog;
        DbBase dbtmp = null;
        //add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A
        DbBase dbtmpBLog = null;
        YssPub pub = null;
        //String 
        YssStatus runStatus = null;
        UserRight rights = null; //2007.12.27 因为 catch 中要使用 所以将 rights 的声明移到 try 外面
        
        SingleLogOper logOper = null;
        //add by chenjianxin 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB
        PassComplexBean passComplex = null;
        if(!isFirstLogin){
        	String wsUrl=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
        	com.yss.core.util.YssCons.webServiceUrl=wsUrl;
        	//获取加密方式的路径
        	com.yss.core.util.YssCons.DB_TEXT_PATH=request.getSession().getServletContext().getRealPath("")+File.separator+"WEB-INF"+File.separator+"dbsetting.txt";
        	this.isFirstLogin=true;
        }
        //============ 将日志写入到log文件    2010.02.08 add by jiangshichao ==================================== 
        String fileSeparator = System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\";//文件分隔符  
        String changeGroup=request.getParameter("changeGroup");//story 1898  by zhouwei 20111124  QDV4赢时胜(上海开发部)2011年11月18日01_A 切换组合群
        Logger log = null;
        //add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A
        String changeDataBase = request.getParameter("changeDbID");
                    try {
                    	//pub = YssUtil.getSessionYssPub(request);
                    	//--- STORY #939 错误日志会每天形成一个文档，而正常输出日志不会按日期生成相应文档  modify by jiangshichao 2011.06.18---//
                    	if(System.getProperty("logsDir")==null){
                    		YssUtil.setSysProp(request);
    						YssUtil.createFold(System.getProperty("logsDir")+fileSeparator, "Ysstech_lOGS");
                    	}
				        log = Logger.getLogger("D");
					} catch (YssException e) {
						e.printStackTrace();
					}
        //------------------------------------------------------------------
		String path = this.getServletContext().getRealPath("");//应用程序的绝对路径 BUG7549 panjunfang add 20130428
	    ///**Start---panjunfang 2013-11-18 BUG 83523 */
   		YssCons.YSS_WebRealPath = path;//保存web应用的绝对路径到静态变量，后续处理可通过该变量直接获取绝对路径。
   		/**End---panjunfang 2013-11-18 BUG 83523*/

        try {
        	/**shashijie 2012-11-22 BUG 6406 在测试QDII系统dbsetting加解密功能报错 */
        	requestPath = request.getSession().getServletContext().getRealPath("");
			/**end shashijie 2012-11-22 BUG 6406*/
            if (cmd.equalsIgnoreCase("yssping")) { //客户端通过发送这种请求保持在线 QDV4海富通2009年06月4日01_AB MS00478 by leeyu
                YssUtil.getSessionYssPub(request);
                if(sFlag == null || sFlag.trim().equals(""))	//20120702 added by liubo.Story #2736.判断前台进行ping操作时需不需要在控制台显示ping操作的信息
                {
	                //===================================================================================
	                log.info("★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆");
	                log.info("此次Ping的时间为:" + YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd HH:mm:ss"));
	                log.info("★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆");
	                //===================================================================================
                }
                
				// ===add by xuxuming,20091020.MS00752.自动ping后台代码的时候,需要在tomcat中打印出客户端的IP地址
				// QDV4海富通2009年10月14日01_AB
				pub = YssUtil.getSessionYssPub(request);
				if (pub != null) {
					if(sFlag == null || sFlag.trim().equals(""))	//20120702 added by liubo.Story #2736.判断前台进行ping操作时需不需要在控制台显示ping操作的信息
					{
						//===================================================================================
		                log.info("★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆");
		                log.info("发起Ping的主机IP是:" + pub.getClientPCAddr());
		                log.info("★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆");
		                //===================================================================================
					}
	                
					//System.out.println("发起Ping的主机IP是:" + pub.getClientPCAddr());
				}
				// ====end==============================================================
				
				/**Start 20131223 added by liubo.Bug #85825.QDV4赢时胜(上海)2013年12月18日01_B
				 * 根据当前ping服务端的操作来获取YssPub对象，当这个对象的某些值为空时，则表示被管理员从在线用户管理界面中断连接*/
				if (pub == null || (pub != null && 
						(pub.getClientPCAddr() == null || pub.getClientPCAddr().equals("") || 
						 pub.getClientMacAddr() == null || pub.getClientMacAddr().equals(""))))
				{
					YssCons.YSS_Connection_Status = false;
				}
				else
				{
					YssCons.YSS_Connection_Status = true;
				}
				/**End 20131223 added by liubo.Bug #85825.QDV4赢时胜(上海)2013年12月18日01_B*/
				
				ou.write("ok".getBytes());
                ou.close();
            } else if (cmd.equalsIgnoreCase("test")) {
                DbBase Dbase = new DbBase();
                //------ modify by wangzuochun 2010.09.13 MS01703    windows server 2008环境下，无法连接到数据库    QDV4赢时胜（深圳）2010年9月7日01_B    
                if("y".equalsIgnoreCase(bSafeMode)){//MS01044 QDV4招商基金2010年3月19日03_A 对数据库配置文件进行加密设置   panjunfang modify 20100407    
                //--------------MS01703-------------//
                	Dbase.loadConnectionSafeMode(dblbl);
                	Dbase.setbSafeMode(true);//add huangqirong 2012-09-07  bug #5553  加密模式登陆; 
                }else{              
                    Dbase.loadConnection(dblbl);//------ modify by wangzuochun 2010.12.10 BUG #624  多机连接同一个服务器，不同数据库，进行登录时，测试连接时报错，但是仍然能登陆系统。
                    Dbase.setbSafeMode(false);//add huangqirong 2012-09-07  bug #5553  非加密模式登陆; 
                }
                ou.write("ok".getBytes());
                ou.close();
            } else if (cmd.equalsIgnoreCase("safelogin")) {
            	if (ctx == null) {
				//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
				//BUG7549 panjunfang modify 20130428					
                    ctx = new FileSystemXmlApplicationContext(
        					YssUtil.getAppConContextPath(path,"httpscfg.xml"));
                }
            	HttpsConfigBean hcb = (HttpsConfigBean)ctx.getBean("httpsconfig");
            	if(hcb.getEncrypt().equals("1"))
            	{
            		ou.write(hcb.getHttpsURL().getBytes());
            	}else
            	{
            		ou.write("commonlogin".getBytes());
            	}
                ou.close();
            } //add by yeshenghong 20120410
            
            //20110801 added by liubo.Story #745
            //当当前操作系统用户名等于配置文件中存储的用户名时，不用登录界面，而是通过操作系统的用户和密码进行验证登录
            //**********************************
            else if(cmd.equalsIgnoreCase("fastlogin"))
            {
            	/**shashijie 2012-11-22 BUG 6406 在测试QDII系统dbsetting加解密功能报错 */
            	/**shashijie 2012-3-14 STORY 2340 */
            	//requestPath = request.getSession().getServletContext().getRealPath("");
				/**end*/
				/**end shashijie 2012-11-22 BUG 6406*/
            	
            	/**Start 20140218 added by liubo.Story #15113.需求上海-[YSS_SH]QDIIV4.0[高]20140127001
            	 * 在这个需求之后，域模式登录时，需要弹出登录框，并显示登录组合群和登录用户名。
            	 * 因此在这里先将YssPub和DbBase初始化掉，避免在之后的取组合群名和用户名的时候报这两个对象为空*/
            	dbtmp = (DbBase) session.getAttribute(YssCons.SS_DBLINK);
                if (dbtmp == null) {
                    pub = new YssPub(); //alex20050127改变pub创建时机，统一在这里一次创建
                    dbtmp = new DbBase();

                    session.setAttribute(YssCons.SS_DBLINK, dbtmp);
                    session.setAttribute(YssCons.SS_PUBLIC, pub);
                } else {
                    dbtmp = YssUtil.getSessionDbBase(request);
                    pub = YssUtil.getSessionYssPub(request);
                }
                /**End 20140218 added by liubo.Story #15113.需求上海-[YSS_SH]QDIIV4.0[高]20140127001*/

                pub.setDbLink(dbtmp);
                pub.setDbLinkBLog(dbtmpBLog);
            	
            	ou.write(Fastlogin(dblbl).getBytes());
            }
            //***************end******************
            else if (cmd.equalsIgnoreCase("getDBInfo")) {//获取加密的数据库配置信息MS01044 QDV4招商基金2010年3月19日03_A
            	GenDBInfo dbInfo = new GenDBInfo();
                ou.write(dbInfo.getDBInfo().getBytes());
                ou.close();
            } else if (cmd.equalsIgnoreCase("log_on")) {
                dbtmp = YssUtil.getSessionDbBase(request); //从session中取出DbBase实例
                pub = YssUtil.getSessionYssPub(request); //ysspub对象是统一和dbbase一起在登录页面建的
                
                //add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A
                dbtmpBLog = YssUtil.getSessionDbBaseBLog(request);
                
                strUserCode = (request.getParameter("usercode") == null ? null : new String(request.getParameter("usercode").getBytes("ISO-8859-1"),"GBK"));//modify by wangzuochun 2010.07.28 MS01494 当用户代码为中文时即使有权限也因权限判断问题无法登录 QDV4赢时胜上海2010年07月27日10_B 
                set = request.getParameter("set");
                
                sLogInType = request.getParameter("logintype");

                rights = new UserRight();
                YssType expired = new YssType();

                if (!YssOperCons.hashmapLog.containsKey(strUserCode)) { //在HASHMAP中定义新用户
                	YssOperCons.hashmapLog.put(strUserCode, "0");
                }
                if (set.length() == 0 || !YssFun.isNumeric(set)) {
                    set = "000";
                }

                rights.setDbLink(dbtmp);
//                logInt = rights.getLogCount(strUserCode);
                boolean changeAssetGroup = ("true".equalsIgnoreCase(request.getParameter("changeAssetGroup"))) ? true : false;  //add by fangjiang 2011.07.26 STORY #1167 
                
                //add by songjie 2012.12.12 STORY #2343 QDV4建行2012年3月2日04_A
                logOper = SingleLogOper.getInstance();

                alterOperLogTb(pub);	//20130128 added by liubo.Story #2839.在更新数据库之前就要为操作日志表加上FLOGDATA4字段，避免更新老版本的库（比58更低）出现FLOGDATA4字段无效的情况

                if (changeAssetGroup || rights.CheckPassword(strUserCode, set, request.getParameter("pass"), expired)) {  //modify by fangjiang 2011.07.26 STORY #1167 

			//20121130 added by liubo.海富通测试问题修改：组合群切换
			//从一个组合群切换到另一个没有权限的组合群时，增加一个登陆时已有的判断权限的逻辑：若切换的目标组合群无组合和组合群级权限，跳出无该组合群权限，无法登录的提示框
			//=============================
                	if (changeAssetGroup)
                	{
                		rights.getPassword(strUserCode,set,new java.util.Date());
                	}
			//==============end===============
                	YssOperCons.hashmapLog.remove(strUserCode); //从HASHMAP中移除；					
                    if (expired.getBoolean()) {
                        session.setAttribute("passExpired", "1");

                        //加入在线用户列表，如果该用户在不同机器上已登录，则不允许登录本机
                        //system参数目前传递的是客户机器名，结合remoteAddr（可能是网关地址）成为唯一机器标识
                    }
                    
                    pub.setClientGateway(request.getRemoteAddr());
                    pub.setClienPCAddr(request.getParameter("system"));
                    
                    //update by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----start
                    String enforce=request.getParameter("enforce");
                    String isModified = request.getParameter("isModified");
                    if(enforce==null||enforce.equals("false"))
                    {
                       //checkUserLogInfo(strUserCode, pub.clientAddr(),dblbl,dbtmp,log);//xuqiji 20100311 MS01008 QDV4广发2010年3月3日01_B 无法实现同一个WAR包上实现多个数据库的连接
                    	checkUserLogInfo(strUserCode, pub.clientAddr(),dbtmp);
                    }
                    else if(enforce.equals("true"))
                    {
                    	HttpSession sess=(HttpSession)YssOperCons.hmOnLineUser.get(strUserCode);
                    	YssPub ysspub=(YssPub) sess.getAttribute(YssCons.SS_PUBLIC); 
                    	hmEnforceUser.put(ysspub.getClientMacAddr()+"|"+sess.getId(),sess);
                    	YssOperCons.hmOnLineUser.remove(strUserCode);                   
                    }
                    //update by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-------end
                    
//                    if(updatingInfo!=null&&updatingInfo.startsWith("updating"))
//                    {
//                    	ou.write(updatingInfo.getBytes());//modified by yeshenghong 20120322 story2164 
//                    	return;
//                    }
                    if (ctx == null) {
						//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
						//BUG7549 panjunfang modify 20130428						
                        ctx = new FileSystemXmlApplicationContext(
            					YssUtil.getAppConContextPath(path,"httpscfg.xml"));
                    }
                	HttpsConfigBean hcb = (HttpsConfigBean)ctx.getBean("httpsconfig");
                	String commonUrl = hcb.getHttpURL();
                    if(hcb.getEncrypt().equals("1"))
                    {
                    	pub.setWebRoot(commonUrl);
                    }else
                    {
                    	pub.setWebRoot(YssUtil.getURLRoot(request));
                    }
                    pub.setWebRootRealPath(path);//BUG7549 panjunfang add 20130428
                    //System.out.println("usercount before add: "+ vctUser.size());
                    
                    //story 1898  by zhouwei 20111124  QDV4赢时胜(上海开发部)2011年11月18日01_A 标记 changeGroup=true 非更换组合群
                    if((changeGroup==null || changeGroup.equals("")) || (sLogInType != null && sLogInType.equals("fastlogin"))){
                    	//YssOperCons.vctUser.add(session);
                    	addLogonUser(strUserCode+"\t"+pub.getDbLink().getUrl()+"\t"+pub.getDbLink().getUser(), pub.clientAddr());
                    }  
                    //---------end------
                    //System.out.println("added:" + pub.clientAddr() + "\r\nusercount:" + vctUser.size());
             
                     pub.yssLogon(set, strUserCode);					
                    session.setAttribute(YssCons.SS_PUBLIC, pub);
                    session.setMaxInactiveInterval(TIME_OUT);
                    YssOperCons.hmOnLineUser.put(strUserCode, session);
                	pub.setOnlineUser(YssOperCons.hmOnLineUser);
                	//---------------add by zhaoxianlin 20120817 Story #2766 QDV4海富通2012年06月28日01_A
                	//密码重置或者首次登录系统提示修改密码时，点击“否”用户已在线，在此remove掉
                	if(isModified!=null&&isModified.equals("true")){
                		YssOperCons.hmOnLineUser.remove(strUserCode);
                	}
                	//---------end------
                    //----数据库版本的比对和更新-2007.12.11--蒋锦--------//
                	runStatus = YssUtil.getSessionYssStatus(request);
                    CtlDbUpdate ctlUpdate = new CtlDbUpdate();
                    ctlUpdate.setYssPub(pub);
                    ctlUpdate.setYssRunStatus(runStatus);
                    //MS01796 add by licai 20101019 TOOLS工具更新程序功能需求  
                    ctlUpdate.getOldVersionNum(set);
                    String versionBefUpdate=ctlUpdate.getVersionNum();
                    String newVersion = YssCons.YSS_AUTOUPDATE_VERSIONS[YssCons.YSS_AUTOUPDATE_VERSIONS.length-1][0];
                    YssUtil.initMoudle();
                    if(!newVersion.equals(versionBefUpdate))
                    {
                    	/**shashijie 2013-1-15 BUG 6657 一个数据库在升级，重新打开另一个客户端连接其他的库提示不能登录*/
                    	if(updatingInfo!=null)
                    	{
                    		//updatingInfo = "updating" + "\r\f" + strUserCode;//modified by  yeshenghong 20120322 story2164
                    		updatingInfo.put(dblbl,"updating" + "\r\f" + strUserCode);
                    	}
                    	boolean updated = ctlUpdate.updateTable(newVersion);
                    	//升级完毕后从集合中去除
                    	if (updatingInfo.containsKey(dblbl)) {
                    		updatingInfo.remove(dblbl);
						}
                    	/*if(updatingInfo!=null)
                    	{
                    		updatingInfo = null;
                    	}*/
                    	/**end shashijie 2013-1-15 BUG */
                    }
                    //MS01796 add by licai 20101019 TOOLS工具更新程序功能需求  ---end--
//                    ctlUpdate.updateEntry(set);
                     //---数据库版本的比对和更新-2007.12.11--蒋锦---end---//
                    //MS01796 add by licai 20101019 TOOLS工具更新程序功能需求  
					ctlUpdate.getOldVersionNum(set);
					String versionAftUpdate=ctlUpdate.getVersionNum();
					
					//add by guolongchao 20120426 若客户启用了自审功能,更新相应的表结构    QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc-----start
					if(!RightServer.version.equals(versionAftUpdate))					
						updateTableData(dbtmp);					
					//add by guolongchao 20120426 若客户启用了自审功能,更新相应的表结构    QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc-----end
					
					//story 1898  by zhouwei 20111124  QDV4赢时胜(上海开发部)2011年11月18日01_A 标记 changeGroup=true 
					//切换组合群不退出
                    if(changeGroup==null || changeGroup.equals("")){
                    	//比较升级前后系统版本号，如果没有更新，那么给返回客户端的版本号赋值为空串
    					if(isNeedUpdateCfgFiles(versionBefUpdate, versionAftUpdate)){	
    						String strRes=versionAftUpdate+"\r\f";
    						ou.write(strRes.getBytes());
    						ou.close();
    						return;
    					}
                    }  
                    //---------end------
									
//					ctlUpdate.copySource(set);//add by huangqirong 2011-09-08 story #1286

                    //add by guolongchao 20120401 STORY2356 QDV4赢时胜(上海开发部)2012年3月5日04_A  使用高版本客户端配置低版本war包登录时要求给出提示信息-----start
					String clientVersion=request.getParameter("clientVersion");
					if(clientVersion!=null&&!clientVersion.equals(RightServer.version))
                    {
						ou.write("当前WAR包和客户端版本不一致，请重新设置".getBytes());
						ou.close();
						return;
                    }
					//add by guolongchao 20120401 STORY2356 QDV4赢时胜(上海开发部)2012年3月5日04_A  使用高版本客户端配置低版本war包登录时要求给出提示信息-----end				
					
					//add by zhouwei 20120606 将sessionid与pub对象作为键值对保存到集合中 story 2188 预警系统
					pub.setSessionId(session.getId());
					//MS01796 add by licai end---  
                    //=========QDV4建行2008年12月25日01_A MS00131 =======//
					
					// ---- QDV4上海2010年12月10日02_A-------------
					pub.setPortBaseCury();
					// ---- QDV4上海2010年12月10日02_A---- lidaolong 2011.01.25--
					
                    EachPubDataOper eachPubData = new EachPubDataOper(); //增加系统登录时建表或建视图的方法
                    eachPubData.setYssPub(pub);
                    eachPubData.createPubDataView(); //系统在登录时创建公共数据表（证券信息表、行表表、汇率表的视图）by leeyu add 20090205
                    //==================MS00131==QDV4建行2008年12月25日01_A=====//
//                   DbTable dt = new DbTable();
//                   dt.setYssPub(pub);
//                   dt.createTempTable();
//                   dt.updateCommon(); //更新公共数据表
//                   dt.updateGroupOnly(set); //更新组合群数据表     
                    
                    //modify by fangjiang 2010.11.17 BUG #438 同一个用户无法登录多个组合群
                	/*String UserSetKey = strUserCode + "_" + set;
                    if (hmUserSet.containsKey(UserSetKey)) {
                    	//HttpSession onlieSession = (HttpSession) hmUserSet.get(UserSetKey);
				        //YssPub pub1 = (YssPub) onlieSession.getAttribute(YssCons.SS_PUBLIC);
                    	//throw new YssException("用户"+strUserCode+"已在IP地址为"+pub1.clientPCAddr+"上登录!");
				        throw new YssException("用户"+strUserCode+"已在组合群"+set+"上登录!");
                    } else {
                    	hmUserSet.put(UserSetKey, session);
                    }*/
                    //---------------------
                    
                    //检验通过，使客户端保存正确的用户列表（不存在的要删除）
                    //通过验证，返回必要的用户、组合群、用户权限等信息
                    String strReturn = "";
                    UserBean userBean = new UserBean();
                    userBean.setYssPub(pub);
                    String strUserInfo = userBean.getUser(strUserCode);

                    AssetGroupBean assetGroupBean = new AssetGroupBean();
                    assetGroupBean.setYssPub(pub);
                    String strAssetGroupInfo = assetGroupBean.getData_group(set);
                    RightBean rightBean = new RightBean();
                    rightBean.setYssPub(pub);
                    rightBean.setUserCode(strUserCode);
                    rightBean.setAssetGroupCode(set);
                    //fanghaoln 20090425 MS00010 QDV4.1 权限明细到组合
                    //-----------------------------------------------------------//
                    String strUserRight = rightBean.getWindowRight(); // 把组合群公用窗体的权限加下组合窗体的权限传到前台还增加了角色权限
                    //---------------------------------------------------------------------------------------------------
                    //fanghaoln 20090425 MS00010 QDV4.1 权限明细到组合
                    String strPortRight = rightBean.getFromPort(); // 返回窗体里选择条里面的组合权限并上角色权限返回到前台进行判断
                    //fanghaoln 20090425 MS00010 QDV4.1 权限明细到组合
                    String strGroupRight = rightBean.getGroupRight(); //返回组合群权限并上角色权限到前台进行判断
                    
                    //------add by wangzuochun 2009.12.1 MS00805 QDII估值系统行情资料权限控制不完善  QDV4华夏2009年11月05日01_B -------//
                    String strPubRight = rightBean.getPublicRight();
                    //---------------------------------------------//
                    
                    //------modify by wangzuochun 2009.12.1 MS00805 QDII估值系统行情资料权限控制不完善  QDV4华夏2009年11月05日01_B -------//
                     strUserRight = strUserRight + "/f/h" + strPortRight + "/f/h" + strGroupRight + "/f/h" + strPubRight; //把组合群公用窗体的权限和组合窗体的权限组合起来传到前台进行解析。
                    //------------------------------------------------------------------------------------------------//
                    //--改变登录时加载用户权限的方法 by caocheng 2009.03.19 MS0001 QDV4.1----------//

                    String strSysSetInfo = Boolean.toString(pub.getSysCheckState());
                    //linjunyun 需求MS00016 2008-11-12 计算当天和密码生效时间相差的天数
                    //如果有效天数是0，则密码一直有效
//                        String passValidInfo = Integer.toString(YssFun.dateDiff(pub.getUserDate(),
//                            YssFun.toSqlDate(new Date())) - pub.getUserPassValidTime());                    
//                        if (pub.getUserPassValidTime() == 0) {
//                            passValidInfo = "-";
//                        }
					// ===add by
                    
                    //---edit by chenjianxin 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB start---//
                    //=======add by xuxuming,20091109.MS00776,取出密码复杂性设置里的记录，在用户登录后要进行相关判断===========
                    passComplex = new PassComplexBean();
                    passComplex.setYssPub(pub);
                    passComplex.getSetting();//得到保存在库中的　各项密码复杂性指标;在登录时,有如下操作：1、密码是否过期（有效天数）；２、过期前N天提示；
                                               //３、N天不登录就锁定(只限制普通用户); ４、N次输入错误就锁定用户
                    //=============end=============================================================
                    //---edit by chenjianxin 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB end---//
                    
					// xuxuming,20091109.MS00776.登录后就判断用户有多久没有登录，N天不登录就锁定用户(只限制普通用户)====
					// 用户类型代码为 “0”，表明是 普通用户。需要进行以下验证
                    userBean.getUserById(strUserCode);//根据用户ID,取出用户信息
                    
                    //---add by songjie 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB start---//
                    boolean ifCheckNLock = false;//用于判断若用户N天没登陆  ，密码是否会被锁定
                    if(passComplex.getiFAllNoLimit() == 1){//若密码复杂度设置中的N天未登录锁定参数对所有类型的用户有效
                    	ifCheckNLock = true;
                    }else{
                    	//若密码复杂度设置中的N天未登录锁定参数只对普通用户有效
                    	if("0".equalsIgnoreCase(userBean.getUserTypeCode())){
                    		ifCheckNLock = true;
                    	}
                    }
                    //---add by songjie 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB end---//
                    
                    //edit by songjie 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB
					if (ifCheckNLock) {
						if (pub.getUserLastLoginDate() != null
								&& YssFun
										.formatDate(pub.getUserLastLoginDate()) != "9998-12-31"
								&& passComplex.getiFLockLimit() > 0) {
							if (YssFun.dateDiff(pub.getUserLastLoginDate(),
									YssFun.toSqlDate(new Date()))
									- passComplex.getiFLockLimit() > 0) {// N天没有登录
								rights.locklogUser(strUserCode);
								/*************************************************************
								 *  #1205 由于密码复杂度设置，用户长期不登陆，登陆后会被锁  
                                 *  add by jiangshichao 2011.03.24
                                 *  
                                 *  问题说明：管理员解锁时，会提示用户在线，无法解锁。
                                 *  这是因为用户异常退出，没有把异常退出的在线用户清除。
								 */
								pub.getOnlineUser().remove(strUserCode);
								throw new YssException("由于"
										+ passComplex.getiFLockLimit()
										+ "天没有登录系统，用户【" + strUserCode
										+ "】已被系统锁定！\r\n 如需使用请联系管理员，解除系统锁定。");
							}
						}
					}
					// =====end=====================
                  //====edit by xuxuming,20091109.MS00776.有效天数不再保存在用户表中，而是用单独的一张　密码复杂性　表　来保存=========
					java.util.Date dCheckDate = userBean.getFCheckDate(); // 上次修改密码日期
					String passValidInfo = "";
					if (passComplex.getiFValidTime() != 0) {
						if (dCheckDate != null) {// 为空时表明用户是还没有修改过密码
							passValidInfo = Integer.toString(YssFun.dateDiff(
									userBean.getFCheckDate(),// 此处获取用户上次修改密码的日期
									YssFun.toSqlDate(new Date()))
									- passComplex.getiFValidTime());// 有效天数是从passComplex中取得的
						} else {// 用户没有修改密码时，就按管理员设置用户时的启效日期来判断
							passValidInfo = Integer.toString(YssFun.dateDiff(
									pub.getUserDate(),// 此处获取密码起效日期
									YssFun.toSqlDate(new Date()))
									- passComplex.getiFValidTime());// 有效天数是从passComplex中取得的
						}
					} else {// 有效天数为0时，就不用作判断了
						passValidInfo = "-";
					}
                    //=========end========================================================================================
					//===========add by xuxuming,20091210.MS00854,首次登陆时需要修改密码,QDV4海富通2009年12月09日01_AB=======
					rights.setYssPub(pub);
					String sOldPassTem = rights.getOldPass(strUserCode);//根据用户ID获取历史密码，首次登录的用户是没有历史密码的
					String sFirstLogin = "";
//					if(!(sOldPassTem!=null&&sOldPassTem.trim()!="")){
//						sFirstLogin = "firstlogin";//首次登录，将些字符串传给前台
//					}
					
					 //modify huangqirong 2012-11-10 bug #6223
					//---------edit by zhaoxianlin 20120817 Story #2766 QDV4海富通2012年06月28日01_A
					if(passComplex.getIPassFaceShow() != 0&&sOldPassTem.trim().length()==0){
						sFirstLogin = "firstlogin";//首次登录，将些字符串传给前台
					}else{
						sFirstLogin = "";
					}
					
					//字段是否存在则增加
					if(!pub.getDbLink().isFieldExist(pub.getDbLink().openResultSet("select * from Tb_Sys_UserList where 1=0"), "FalreadyReset")){ 
						pub.getDbLink().executeSql("alter table Tb_Sys_UserList add FalreadyReset NUMBER(1) default 0");
		            }
					
					int isReset = userBean.getAlreadyReset(strUserCode) ; //是否有过重置
					
					if(sFirstLogin.trim().length() == 0){
						if(passComplex.getiFResetPwdChange()!=0&&sOldPassTem.trim().length()==0 && isReset== 0){	//add by zhaoxianlin 20120815 Story #2766 密码重置后强制修改密码 去掉 YssCons.YSS_USER_OLDPASS_COUNT==0的判断
							sFirstLogin = "";
						}else if(passComplex.getiFResetPwdChange()!=0&&sOldPassTem.trim().length()!=0&& isReset== 1){	//add by zhaoxianlin 20120815 Story #2766 密码重置后强制修改密码 YssCons.YSS_USER_OLDPASS_COUNT==0的判断
							sFirstLogin = "firstlogin";
						}						
					}
					//---end---
					//---------------------------end-------------------------------//
					//==============end=============================================
                    //添加【用户可以审核自己录入的数据】参数 -----BugID: MS00010  ----by sunkey 20081111
                    //linjunyun 需求MS00016 2008-11-12 添加当天和密码生效时间相差的天数

                    strReturn = strUserInfo + "\r\f" + strAssetGroupInfo + "\r\f"
                        + strUserRight + "\r\f" + strSysSetInfo + "\t" + pub.getSysSelfCheck() +"\t" + pub.getSysAudietOwn()+//add by guolongchao 20120426 添加自审功能 （明细到各个菜单）    QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc
                        "\r\f" + passValidInfo+
                        "\r\f" + passComplex.getiFExpirePrompt()+//edit by xuxuming,20091109.MS00776.过期前N天提示，将N传给前台
                        "\r\f" +sFirstLogin+ //将首次登录的标记传给前台
                        "\r\f" + String.valueOf(passComplex.getiFShowPwdChange()) + //20110803 added by liubo.Story 1233.将“密码过期强制修改密码”的值传回前台
                        //add by songjie 2012.09.18 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 
                        //判断是否隐藏调度方案执行界面预警执行选项框
                        "\r\f" + YssCons.YSS_EWCLUE_MODE +
                        
                        //20121126 added by liubo.Story #3057
                        //若前台不操作系统的时间，超过这个设置的值，则前台不需要再自动尝试连接后台（即断线重连功能）
                        //若设置的值为0，则不启用这个功能
                        "\r\f" + YssCons.YSS_DEFER_TIME
    					//add by songjie 2013.01.24 添加 YssCons.companyName
                        + "\r\f" + YssCons.companyName 
                        ;
                        
                    ou.write(strReturn.getBytes());
                    //-------------蒋锦  2007.12.27 将用户登录记入日志
                    rights.setModuleName(" ");
                    rights.setFunName(" ");
                    rights.setRefName(" ");
//                   OperateLogBean log = new OperateLogBean();
//                   log.setOperateType(15);
//                   log.setOperateResult("1");
//                   log.setYssPub(pub);
//                   log.insertLog(rights);
                    //---------------------------//
                    
                    //---add by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A start---//
                    OperateLogBean dfLog = new OperateLogBean();
                    dfLog.setYssPub(pub);
                    String logOnInfo = dfLog.getUserLogInfo(false);
                    rights.setASubData(logOnInfo);
                    //---add by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A end---//
                    
                    logOper.setIData(rights, 15, pub);
                    //====add by xuxuming,20091109.MS00776.登录系统后，要将登录日期保存到库中=========
                    userBean.saveUserLoginDate(strUserCode);
                    log.info("★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆");
                    log.info("【用户为"+ strUserCode +"使用IP地址为 "+request.getRemoteAddr()+"的客户端登录本系统,登录时间为:" + YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd HH:mm:ss")+"】"); //----- modify by wangzuochun  2010.07.28 MS01494 当用户代码为中文时即使有权限也因权限判断问题无法登录 QDV4赢时胜上海2010年07月27日10_B 
                	log.info("★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆");
                    //===============end================================================= 
                	//#795 核心业务系统客户端访问服务器端缺乏安全防范机制  add by jiangshichao 2011.05.19
                	if(pub.getWebRoot().split(":")[0].equalsIgnoreCase("https")){
                		verifyHttps();
                	}
                	//#795 核心业务系统客户端访问服务器端缺乏安全防范机制   end -----------------//
                } else {
                    //add by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A
                    OperateLogBean dfLog = new OperateLogBean();
                    dfLog.setYssPub(pub);
                    dfLog.getUserLogInfo(true);
                	
                	//---edit by chenjianxin 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB start---//
                    //=======add by xuxuming,20091109.MS00776,取出密码复杂性设置里的记录，在用户登录后要进行相关判断===========
                    passComplex = new PassComplexBean();
                    passComplex.setYssPub(pub);
                    passComplex.getSetting();//得到保存在库中的　各项密码复杂性指标;在登录时,有如下操作：1、密码是否过期（有效天数）；２、过期前N天提示；
                                               //３、N天不登录就锁定(只限制普通用户); ４、N次输入错误就锁
                    //---edit by chenjianxin 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB end---//
                	
//                    logInt = rights.getLogCount(strUserCode);//edit by xuxuming,MS00776.容错次数不再保存在用户表中，而是要从密码复杂性表中取得
                	logInt = passComplex.getiFLockError();// add by xuxuming,MS00776,取出容错次数
                    if (logInt > 0) {
                        iUserLog = YssFun.toInt(YssOperCons.hashmapLog.get(strUserCode).toString());
                        iUserLog++;
                        if (iUserLog >= logInt) {
                            rights.locklogUser(strUserCode);
                            YssOperCons.hashmapLog.remove(strUserCode);

                            throw new YssException("由于登录密码错误次数超过限制，用户【" + strUserCode +
                                "】已被系统锁定！\r\n 如需使用请联系管理员，解除系统锁定。");
                        } else {
                        	YssOperCons.hashmapLog.put(strUserCode, String.valueOf(iUserLog));
                        }
                    }
                    
                    //---add by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A start---//
                    pub.setClienPCAddr(request.getParameter("system"));
                    pub.yssLogon(set, strUserCode);
                    //---add by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A end---//
                    
                    session.setAttribute("logonInfo",
                                         strUserCode + "\t" + set + "\t");

					YssCons.YSS_Connection_Status = true;//20131223 added by liubo.Bug #85825.表示连接状态
					
                    ou.write("no".getBytes());
                }
            } else if (cmd.equalsIgnoreCase("log_info")) {
                if (session.getAttribute("logonInfo") != null) {
                    session.removeAttribute("logonInfo");
                }
                dbtmp = (DbBase) session.getAttribute(YssCons.SS_DBLINK);
                if (dbtmp == null) {
                    pub = new YssPub(); //alex20050127改变pub创建时机，统一在这里一次创建
                    dbtmp = new DbBase();

                    session.setAttribute(YssCons.SS_DBLINK, dbtmp);
                    session.setAttribute(YssCons.SS_PUBLIC, pub);
                } else {
                    dbtmp = YssUtil.getSessionDbBase(request);
                    pub = YssUtil.getSessionYssPub(request);
                }
                
                //---add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A start---//
                dbtmpBLog = (DbBase) session.getAttribute(YssCons.SS_DBLINK_BLOG);
                if (dbtmpBLog == null) {
                	dbtmpBLog = new DbBase();
                    session.setAttribute(YssCons.SS_DBLINK_BLOG, dbtmpBLog);
                } else {
                	dbtmpBLog = YssUtil.getSessionDbBaseBLog(request);
                }
                //---add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A end---//
                
                if (runStatus == null) {
                    runStatus = new YssStatus();
                    session.setAttribute(YssCons.SS_RUNSTATUS, runStatus);
                } else {
                    runStatus = YssUtil.getSessionYssStatus(request);
                }

                //---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A start---//
                if(changeDataBase != null && changeDataBase.equals("Y")){
                	dbtmp.setChangeDtBase(true);
                	//add by songjie 2013.01.17 STORY #2343 QDV4建行2012年3月2日04_A
                	dbtmpBLog.setChangeDtBase(true);
                }else{
                	dbtmp.setChangeDtBase(false);
                	//add by songjie 2013.01.17 STORY #2343 QDV4建行2012年3月2日04_A
                	dbtmpBLog.setChangeDtBase(false);
                }
                //---add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A end---//
                
                pub.setDbLink(dbtmp);
                //add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A
                pub.setDbLinkBLog(dbtmpBLog);
                pub.setDblbl(dblbl);//xuqiji 20100311 MS01008 QDV4广发2010年3月3日01_B 无法实现同一个WAR包上实现多个数据库的连接
                UserRight ur = new UserRight();
                ur.setDbLink(dbtmp);
                //------ modify by wangzuochun 2010.09.13 MS01703    windows server 2008环境下，无法连接到数据库    QDV4赢时胜（深圳）2010年9月7日01_B    
                if("y".equalsIgnoreCase(bSafeMode)){//如果配置了加密方式则调用加密连接方法 MS01044 QDV4招商基金2010年3月19日03_A 对数据库配置文件进行加密设置   panjunfang modify 20100407    
                //---------------MS01703--------------//
                	dbtmp.loadConnectionSafeMode(dblbl);
                	dbtmp.setbSafeMode(true); //add huangqirong 2012-09-07  bug #5553  加密模式登陆; 
                	//---delete by maxin bug86332
                	//---add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A start---//
//                	dbtmpBLog.loadConnectionSafeMode("[db_ysslog]");
//                	//--- add by songjie 2013.04.28 兴业银行BUG start---//
//                	//修改 加密模式（根据 dbsetting.properties 获取数据库链接）获取不到数据库链接 问题
                	dbtmpBLog.setbSafeMode(true);
//                	if(dbtmpBLog.loadConnection("[db_ysslog]") == null){
//                		dbtmpBLog.loadConnectionSafeMode(dblbl);
//                	}
//                	//--- add by songjie 2013.04.28 兴业银行BUG end---//
                	
                	//---add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A end---//
                }else{              
                	dbtmp.loadConnection(dblbl);
                	dbtmp.setbSafeMode(false); //add huangqirong 2012-09-07  bug #5553  非加密模式登陆; 
                	
                	//---add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A start---//
                	dbtmpBLog.loadConnection("[db_ysslog]");
                	if(dbtmpBLog.loadConnection("[db_ysslog]") == null){
                		dbtmpBLog.loadConnection(dblbl);
                	}
                	dbtmpBLog.setbSafeMode(false);
                	//---add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A end---//
                }
                
                //---add by songjie 2013.01.18 STORY #2343 QDV4建行2012年3月2日04_A start---//
                if(dblbl != null && dblbl.trim().length() != 0){
                	YssCons.DB_CONNECTTION_RECENTLY = dblbl;
                }
                //---add by songjie 2013.01.18 STORY #2343 QDV4建行2012年3月2日04_A end---//
                
                //add by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A
                dbtmp.setChangeDtBase(false);
                //add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A
                dbtmpBLog.setChangeDtBase(false);
                //add by zhouwei 20120530 story 2188 登录时将数据库标示存到session当中
                session.setAttribute(com.yss.core.util.YssCons.DB_CONNECTION_FLAG, dblbl);
                session.setAttribute(com.yss.core.util.YssCons.DB_CONNECTION, dbtmp.getConnection());
                
                //add by songjie 2012.11.02 STORY #2343 QDV4建行2012年3月2日04_A
                session.setAttribute(YssCons.DB_CONNECTION_BLOG, dbtmpBLog.getConnection());
                /*if (!dbtmp.yssTableExist("TB_SYS_AssetGroup")) {
                   if (dbtmp.dbType == YssCons.DB_ORA) {
                      Ora ora = new Ora(dbtmp);
                      ora.CreateCommon();
                   }
                   else if (dbtmp.dbType == YssCons.DB_SQL) {
                      Sql sql = new Sql(dbtmp);
                      sql.CreateCommon();
                   }
                   else if (dbtmp.dbType == YssCons.DB_DB2) {
                      DB2 db2 = new DB2(dbtmp);
                      db2.CreateCommon();
                   }
                             }*/
                //==================读许可权限文件
                YssLicence licence = new YssLicence();
                licence.setYssPub(pub);
                licence.loadLicence("/");
                //add by songjie 2013.01.23 获取fundacc.lic文件中的公司名称数据
                YssCons.companyName = licence.getSClientName();
                //edit by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A
                ou.write((licence.buildRowStr() + "\f\f" + dbtmp.getDataBaseID()).getBytes());
                //===================by leeyu 080902
                return;
            } else if (cmd.equalsIgnoreCase("log_off")) {
            	
            	dbtmp = YssUtil.getSessionDbBase(request); //从session中取出DbBase实例 add by wangzuochun 2010.09.15 MS01705    视图问题没有完全解决，生成净值表时仍会创建视图    QDV4建行2010年09月08日02_B    
            	strUserCode = (request.getParameter("usercode") == null ? null : new String(request.getParameter("usercode").getBytes("ISO-8859-1"),"GBK"));//add by wangzuochun 2010.07.28 MS01494 当用户代码为中文时即使有权限也因权限判断问题无法登录 QDV4赢时胜上海2010年07月27日10_B 
            	log.info("★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆");
                log.info("【用户为"+ strUserCode +"在IP地址为"+request.getRemoteAddr()+"的客户端 注销本系统，注销时间为:" + YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd HH:mm:ss")+" 】"); //----- modify by wangzuochun  2010.07.28 MS01494 当用户代码为中文时即使有权限也因权限判断问题无法登录 QDV4赢时胜上海2010年07月27日10_B					
            	log.info("★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆");
                String logBj = "false";
                String sId = null;
                
//                for (int i = YssOperCons.vctUser.size() - 1; i >= 0; i--) {
//                    sId = ( (HttpSession) YssOperCons.vctUser.get(i)).getId();
//                    if (sId == null || sId.equalsIgnoreCase(session.getId())) { //alex20050201有时候出现注销出错是因为sId==null
//                    	YssOperCons.vctUser.remove(i); //从在线列表中退出
//                        break;
//                    }
//                }
                //modify by jsc 20120913  中行登录程序没响应问题修改。
                removeOnlineUserFromKey(strUserCode+"\t"+dbtmp.getUrl()+"\t"+dbtmp.getUser());
                
                
                //strUserCode = request.getParameter("usercode"); //----- modify by wangzuochun  2010.07.28 MS01494 当用户代码为中文时即使有权限也因权限判断问题无法登录 QDV4赢时胜上海2010年07月27日10_B 
                
                //update by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----start
                /**shashijie 2012-6-6 BUG 4726 多个数据库ID登录系统随即循序退出时关闭系统 */
                if (!YssOperCons.hmOnLineUser.isEmpty() && YssOperCons.hmOnLineUser.containsKey(strUserCode)) {
                	sId = ((HttpSession)YssOperCons.hmOnLineUser.get(strUserCode)).getId();
				}
				/**end*/
                if(sId==null||sId.equalsIgnoreCase(session.getId()))
                {
                YssOperCons.hmOnLineUser.remove(strUserCode);
                } 
                String macAddr=request.getParameter("macAddr");
                if(macAddr!=null&&hmEnforceUser.containsKey(macAddr+"|"+session.getId()))
                {                	
                	hmEnforceUser.remove(macAddr+"|"+session.getId());                    
                }                
                //update by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----end
                
                //add by fangjiang 2010.11.17 bug BUG #438 同一个用户无法登录多个组合群
                /*set =(request.getParameter("set") == null ? null : new String(request.getParameter("set").getBytes("ISO-8859-1"),"GBK"));
                String userSetCode = strUserCode + "_" + set;
                hmUserSet.remove(userSetCode);*/
                //------------------
                /**shashijie 2012-03-08 BUG 4000 ,解决多客户端访问一个后台退出报异常BUG */
                dropVQViewTable(dbtmp,strUserCode,dblbl,bSafeMode);//删除当前用户的分页视图和表，add by wangzuochun 2010.09.15 MS01705    视图问题没有完全解决，生成净值表时仍会创建视图    QDV4建行2010年09月08日02_B    
                /**end*/
                session.invalidate(); //todo现在的问题是注销后显示登录报错, 如果不invalidate又有别的问题main.htm不存在。
                logBj = "true";
                
				YssCons.YSS_Connection_Status = false;//20131223 added by liubo.Bug #85825.表示中断状态
                
                ou.write(logBj.getBytes());
                
               
                return;
            } else if (cmd.equalsIgnoreCase("print")) {
                String op = request.getParameter("op");
                String param = YssUtil.getBytesStringFromClient(request);
                YssPrint print = new YssPrint();
                pub = YssUtil.getSessionYssPub(request);

                print.setYssPub(pub);
                if (op.equalsIgnoreCase("loadrptprintsetup")) { //记载报表打印设置
                    ou.write(print.loadRptPrintSetup(param).getBytes());
                } else if (op.equalsIgnoreCase("saverptprintsetup")) { //报错那报表打印设置
                    print.saveRptPrintSetup(param);
                }
            //edited by zhouxiang MS01536    在系统设置中增加一【在线用户管理】模块    20100910---
			} else if (cmd.equalsIgnoreCase("OnlineUserManager")) { // 查询在线的用户
				StringBuffer bufShow = new StringBuffer();
				StringBuffer bufAll = new StringBuffer();
				String squest = YssUtil.getBytesStringFromClient(request);
				String sHeader = "";
				String str = "";
				String showString = "";
				UserBean user = new UserBean();
				pub = YssUtil.getSessionYssPub(request);
				user.setYssPub(pub);
				ResultSet Onliners = null;
				String sId = null;
				sHeader = "用户ID\t用户名\t客户端机器名\t客户端IP地址";
				bufShow.append(sHeader).append("\r\f");
				if (squest.trim().length() > 0) { // 判断是否输入了查询条件
					if (YssOperCons.hmOnLineUser.containsKey(squest)) {
						String UserName = user.getUser(squest).split("\t")[1]; // 这里获取用户的名字
						HttpSession onlieSession = (HttpSession) YssOperCons.hmOnLineUser
								.get(squest);
						YssPub pub1 = (YssPub) onlieSession
								.getAttribute(YssCons.SS_PUBLIC);
						bufShow.append(squest).append("\t");
						bufShow.append(UserName).append("\t"); 
						bufShow.append(pub1.clientPCName).append("\t");
						bufShow.append(pub1.clientPCAddr).append("\f\f");
					} else {
						bufShow.append("\f\f");
					}
				} else { // 如果没有输入条件就查询出所有的用户
					if (YssOperCons.hmOnLineUser != null || !YssOperCons.hmOnLineUser.isEmpty()) {
						Set set1 = YssOperCons.hmOnLineUser.keySet(); // 将HASHMAP根据key序列化
						Iterator it = set1.iterator();
						while (it.hasNext()) {
							String key = (String) it.next(); // 这里获取用户的ID
							String UserName = user.getUser(key).split("\t")[1]; // 这里获取用户的名字
							HttpSession onlieSession = (HttpSession) YssOperCons.hmOnLineUser
									.get(key);
							YssPub pub1 = (YssPub) onlieSession
									.getAttribute(YssCons.SS_PUBLIC);
							bufShow.append(key);
							bufShow.append("\t");
							bufShow.append(UserName).append("\t");
							bufShow.append(pub1.clientPCName).append("\t");
							bufShow.append(pub1.clientPCAddr).append("\f\f");
						}
					}
				}
				showString = bufShow.toString().substring(0,
						bufShow.length() - 2);
				showString += "\r\fnull";
				ou.write(showString.getBytes());
			} else if (cmd.equalsIgnoreCase("interrupt")) {
				UserBean user = new UserBean();
				String squest = YssUtil.getBytesStringFromClient(request);
				HttpSession onlieSession = (HttpSession) YssOperCons.hmOnLineUser
						.get(squest);
				HttpSession interruptSession = (HttpSession) YssOperCons.hmOnLineUser
						.get(squest);
				if (squest.trim().length() > 0
						&& YssOperCons.hmOnLineUser.containsKey(squest)
						&& interruptSession.getId() == session.getId()) {
					throw new YssException("对不起，你不能中断自己,请确认重试!");
				}
				if (!YssOperCons.hmOnLineUser.containsKey(squest)) {
					throw new YssException("用户已下线!");
				}
				YssPub pub1 = (YssPub) onlieSession
						.getAttribute(YssCons.SS_PUBLIC);
				user.setYssPub(pub1);
				pub1.setClienPCName("被中断的用户名:" + squest);
				user.setModuleName("system");
				user.setFunName("OnlineUserManager");
				user.setRefName("19870805");
				if (squest.trim().length() > 0
						&& YssOperCons.hmOnLineUser.containsKey(squest)
						&& interruptSession.getId() != session.getId()) {
					String sId = null;

					/**Start 20131206 modified by liubo.Bug #85062.QDV4赢时胜(上海)2013年12月6日02_B
					 * 之前的以YssOperCons.vctUser.get(i)来取在线用户的方式有问题。
					 * 因为YssOperCons.vctUser的key值是以用户名+“\t”+数据库地址+“\t”+数据库名称，直接get(i)是肯定取不出value的*/
					for (int i = YssOperCons.vctUser.size() - 1; i >= 0; i--) {
//						sId = ((HttpSession) YssOperCons.vctUser.get(i)).getId();
						sId = (String)YssOperCons.vctUser.get(squest+"\t"+pub1.getDbLink().getUrl()+"\t"+pub1.getDbLink().getUser());
						
						if (sId == null
								|| sId.equalsIgnoreCase(interruptSession
										.getId())) {
							YssOperCons.vctUser.remove(squest+"\t"+pub1.getDbLink().getUrl()+"\t"+pub1.getDbLink().getUser()); // 从在线列表中退出
							break;
						}
					}
					/**End 20131206 modified by liubo.Bug #85062.QDV4赢时胜(上海)2013年12月6日02_B*/
					YssOperCons.hmOnLineUser.remove(squest);
					interruptSession.invalidate();
				}
				if(YssOperCons.hmOnLineUser.containsKey(squest)){
					user.parseLog();
					logOper = SingleLogOper.getInstance();
					logOper.setIData(user, 28, pub1,true); // 中断用户失败的日志28 代表：用户中断操作类型
				}
				if (!YssOperCons.hmOnLineUser.containsKey(squest)) {
					user.parseLog();
					logOper = SingleLogOper.getInstance();
					logOper.setIData(user, YssCons.OP_INTERRUPT, pub1); // 中断用户成功的日志
					throw new YssException("中断成功!");
				}
			} 
            //---------------------------end-------------------------------------------------
            /**shashijie 2011.03.14 TASK #2747::需要在系统界面"帮助"添加下拉菜单，以便看到版本信息 */
			else if (cmd.equalsIgnoreCase("getProductInfo")){
				ou.write(getProductInfo(request).getBytes());;
			}
          //add by huangqirong 2011-08-23 STORY #1314
			else if (cmd.equalsIgnoreCase("getpw")){
				pub = YssUtil.getSessionYssPub(request);
				rights=new UserRight(pub);
				String putInPassWord=request.getParameter("pw");
				String rightPassWord=rights.getPassword(new String(request.getParameter("usercode").getBytes("ISO-8859-1"),"GBK"),request.getParameter("assetgroupcode"));
				String result=putInPassWord.compareTo(rightPassWord)==0?"go":"notgo";
				ou.write(result.getBytes());
			}
           //--end---
			else if(cmd.equalsIgnoreCase("refreshVersion"))
			{
				/**shashijie 2013-1-15 BUG 6657  一个数据库在升级，重新打开另一个客户端连接其他的库提示不能登录*/
				if(updatingInfo!=null && updatingInfo.containsKey(dblbl))
                {
					//ou.write(updatingInfo.getBytes());//modified by yeshenghong 20120322 story2164
                	ou.write(((String)updatingInfo.get(dblbl)).getBytes());
                	return;
                }
				/**end shashijie 2013-1-15 BUG 6657 */
				
				set = request.getParameter("set");
				pub = YssUtil.getSessionYssPub(request);
				CtlDbUpdate ctlUpdate = new CtlDbUpdate();
                ctlUpdate.setYssPub(pub);
                //MS01796 add by licai 20101019 TOOLS工具更新程序功能需求  
                ctlUpdate.getOldVersionNum(set);
                String versionBefUpdate=ctlUpdate.getVersionNum();
                String newVersion = YssCons.YSS_AUTOUPDATE_VERSIONS[YssCons.YSS_AUTOUPDATE_VERSIONS.length-1][0];
                if(!newVersion.equals(versionBefUpdate)){
					int versionCount = 0;
	            	versionCount = ctlUpdate.getVersionCount();
	        		ou.write(("initial" + "\r\f" + versionCount).getBytes());
                }else
                {
                	ou.write(("initial" + "\r\f" + 0).getBytes());
                }
			}
            //add by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----start
			else if(cmd.equalsIgnoreCase("getEnforce"))
			{
				 String sId="";
				 String isEnforce="false";
				 String macAddr=request.getParameter("macAddr");	
				 strUserCode = (request.getParameter("usercode") == null ? null : new String(request.getParameter("usercode").getBytes("ISO-8859-1"),"GBK"));
	             if(macAddr!=null&&this.hmEnforceUser.containsKey(macAddr+"|"+session.getId()))
	             {   
	            	HttpSession sess=(HttpSession)YssOperCons.hmOnLineUser.get(strUserCode);
	            	if(sess!=null)
	            	{
	            		YssPub ysspub=(YssPub) sess.getAttribute(YssCons.SS_PUBLIC);   
	 	            	isEnforce="true\t"+ysspub.clientAddr();	              
	            	}	                  
	             }
	             ou.write(isEnforce.getBytes());
			     ou.close();
			     return;
			}
            //add by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出-----end
            
        } catch (Exception ye) { //这里不应该捕获doget能抛出的异常，不应该用Exception...考虑到可能有bean没有捕获其它异常，这里还是捕获所有。。。040528
            //设定一个header，客户端如发现这个header存在，则得到的数据是异常消息
        	/**shashijie 2013-1-15 BUG 6657 一个数据库在升级，重新打开另一个客户端连接其他的库提示不能登录*/
        	if(updatingInfo.containsKey(dblbl))
        	{
        		//updatingInfo = null;//modified by yeshenghong 20120322 story2164
        		updatingInfo.remove(dblbl);
        	}
        	/**end shashijie 2013-1-15 BUG 6657 */        	
        	//start modify huangqirong 2013-03-11 bug #7279
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            /*YssLicence licence = new YssLicence();
            licence.setYssPub(pub);
            try {
            	
            	licence.loadLicence("/");
			} catch (Exception e) {
				System.out.println("获取许可文件出错！");
			}*/
            //edit by songjie 2012.03.07 STORY #2279 QDV4大成基金2012年02月21日02_A
            //add by songjie 2013.01.24 如果报错  则 只返回错误信息
			ou.write(ye.getLocalizedMessage().getBytes()); //modify huangqirong 2013-02-27  bug #7123  注释掉 
			//delete by songjie 2013.01.24 如果报错  则 只返回错误信息
            //ou.write((ye.getLocalizedMessage()+ "\r\r\f" + licence.buildRowStr() + "\f\f" + dbtmp.getDataBaseID()).getBytes()); //------ modify by wangzuochun 2010.04.19  MS00977    登陆系统时，当“组合群代码”和“用户代码”输入错误时报错    QDV4赢时胜（测试）2010年04月14日03_B   //modify huangqirong 2013-02-27 bug #7123 解开注释
			//end  huangqirong 2012-11-26 bug #6314
			//end modify huangqirong 2013-03-11 bug #7279
            ou.close();
        }
        ou.close();
    }


	/**
     * 检查用户登录情况如果同一用户已经在不同机器上登录，则抛出异常
     * @param user String：用户代码
     * @param clientAddr String：客户端信息，目前是IP
     * @throws YssException
     * @return boolean：如果用户已经登录，返回true
     * @author xuqiji 20100311 MS01008 QDV4广发2010年3月3日01_B 无法实现同一个WAR包上实现多个数据库的连接

    private boolean checkUserLogInfo(String userCode, String clientAddr,String dblbl,DbBase dbBaseTmp,Logger log) throws
        YssException {
        HttpSession session = null;
        YssPub pub = null;
        int i;
        for (i = YssOperCons.vctUser.size() - 1; i >= 0; i--) {
        	log.info(userCode + ":log_On过程>>UserCheck行号：956");
            session = (HttpSession) YssOperCons.vctUser.get(i);
            log.info(userCode + ":log_On过程>>UserCheck行号：958");
            if (session == null || session.getId() == null) {
            	log.info(userCode + ":log_On过程>>UserCheck行号：960");
            	YssOperCons.vctUser.remove(i);
            	log.info(userCode + ":log_On过程>>UserCheck行号：962");
            } else {
                try {
                	log.info(userCode + ":log_On过程>>UserCheck行号：965");
                    pub = (YssPub) session.getAttribute(YssCons.SS_PUBLIC);
                    log.info(userCode + ":log_On过程>>UserCheck行号：967");
                } catch (Exception e) {
                	log.info(userCode + ":log_On过程>>UserCheck行号：969");
                	YssOperCons.vctUser.remove(i); //websphere下session失效不一定null，这样处理，避免错误
                	log.info(userCode + ":log_On过程>>UserCheck行号：971");
                    continue;
                }
                if (userCode.equalsIgnoreCase(pub.getUserCode())) { //当前用户
           //update by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出---------------------start
//                    if (!clientAddr.equalsIgnoreCase(pub.clientAddr())&& dblbl.equalsIgnoreCase(pub.getDblbl())) { //在其它机器上登录
//                    	throw new YssException("您已经在其它电脑上登录！\r\n\r\n要在本机登录，请先从" +
//                    	pub.clientAddr() + "上注销或退出");
//                    }
//                    break;
                	log.info(userCode + ":log_On过程>>UserCheck行号：981");
                    if(!clientAddr.equalsIgnoreCase(pub.clientAddr()))
                    {
//                    	log.info(userCode + ":log_On过程>>UserCheck行号：984");
//                    	DbBase dbBase=(DbBase) session.getAttribute(YssCons.SS_DBLINK);
//                    	log.info(userCode + ":log_On过程>>UserCheck行号：986");
//                    	if(dbBaseTmp.getUrl().equals(dbBase.getUrl())&&dbBaseTmp.getUser().equals(dbBase.getUser()))
//                    	{
                    		log.info(userCode + ":log_On过程>>UserCheck行号：989");
                    		throw new YssException("您已在"+pub.clientAddr()+"电脑上登录！\r\n\r\n确定要强制登录吗?");                    		
//                    	}
                    }
            //update by guolongchao 20120405 STORY #2440  同一用户多处登录时，跳出选择框，强制其他登录退出---------------------end
                }
                log.info(userCode + ":log_On过程>>UserCheck行号：995");
            }
        }
        return (i >= 0);
    }     */

	/**
     * 检查用户登录情况如果同一用户已经在不同机器上登录，则抛出异常
     * @param user String：用户代码
     * @param clientAddr String：客户端信息，目前是IP
     * @param dbBaseTmp DbBase: 用于跨库同名用户的判断
     * @throws YssException
     * @return boolean：如果用户已经登录，返回true
     * @author 中行被死锁问题处理   add by jsc 20120913
     */
    private void checkUserLogInfo(String userCode,String clientAddr,DbBase dbBaseTmp)throws YssException{
    	StringBuffer logOnUserKey = new StringBuffer();
    	logOnUserKey.append(userCode).append("\t").append(dbBaseTmp.getUrl()).append("\t").append(dbBaseTmp.getUser());
    	if(YssOperCons.vctUser.containsKey(logOnUserKey.toString())&& !YssOperCons.vctUser.containsValue(clientAddr)){
    		throw new YssException("您已在"+YssOperCons.vctUser.get(logOnUserKey.toString())+"电脑上登录！\r\n\r\n确定要强制登录吗?");  
    	}
    	
    }
    
    private void addLogonUser(String key,String value) throws YssException {
    	try{
    		if(!YssOperCons.vctUser.containsKey(key)){
    			YssOperCons.vctUser.put(key, value);
    		}
    		
    	}catch(Exception e){
    		throw new YssException("add to vctUser Error......");
    	}
    	
    }
    
    private synchronized void removeOnlineUserFromKey(String key) throws YssException{
    	try{
    		YssOperCons.vctUser.remove(key);
    	}catch(Exception e){
    		throw new YssException("removed online User from vctUser Error......");
    	}
    	
    	
    }
    //Process the HTTP Post request
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        doGet(request, response);
    }

    //Clean up resources
    public void destroy() {
    }
    
    /**
	 * add by wangzuochun 2010.09.15 MS01705    视图问题没有完全解决，生成净值表时仍会创建视图    QDV4建行2010年09月08日02_B    
	 * 当用户退出时，删除当前用户的分页视图和表；
	 * @方法名：dropVQViewTable
	 * @参数：
	 * @返回类型：void
	 */
	private void dropVQViewTable(DbBase dbl,String userCode,String dblbl,String bSafeMode) throws YssException {
		Connection conn = null;
		boolean bTrans = false;
		String viewSql = "";
		String tableSql = "";
		String dropSql = "";
		
		ResultSet viewRs = null;
		ResultSet tableRs = null;
		
		try {
			/**shashijie 2012-03-08 BUG 4000,解决多客户端访问一个后台退出报异常BUG */
			//---edit by songjie 2012.03.09 STORY #2279 QDV4大成基金2012年02月21日02_A start---//
			//添加 bSafeMode（是否为加密登录模式） 参数
			if("Y".equalsIgnoreCase(bSafeMode)){
				conn = dbl.loadConnectionSafeMode(dblbl);
				dbl.setbSafeMode(true);//add huangqirong 2012-09-07  bug #5553  加密模式登陆; 
			}else{
				conn = dbl.loadConnection(dblbl);
				dbl.setbSafeMode(false);//add huangqirong 2012-09-07  bug #5553  非加密模式登陆; 
			}
			//---edit by songjie 2012.03.09 STORY #2279 QDV4大成基金2012年02月21日02_A end---//
			/**end*/
			//---add by songjie 2012.05.23 BUG 4586 QDV4赢时胜(测试)2012年5月17日03_B start---//
			if(conn == null){
				return;
			}
			//---add by songjie 2012.05.23 BUG 4586 QDV4赢时胜(测试)2012年5月17日03_B end---//
			conn.setAutoCommit(false);
			bTrans = true;
			
			viewSql = " Select view_name from user_views " +
					  " where view_name like 'VQ_%' and view_name like " + dbl.sqlString("%_" + userCode.toUpperCase()) ;
			
			tableSql = " Select table_name from user_tables " +
			  		   " where table_name like 'VQ_%' and table_name like " + dbl.sqlString("%_" + userCode.toUpperCase())+
			  		   " or table_name like 'tb_temp_%' and table_name like "+dbl.sqlString("%_" + userCode.toUpperCase());//edited by zhouxiang 2010.11.5 #474::系统需增加对几类特定表的删除功能
			
			viewRs = dbl.openResultSet(viewSql);
			tableRs = dbl.openResultSet(tableSql);
			
			while(viewRs.next()){
				dropSql = "drop view " + viewRs.getString("View_Name");
				dbl.executeSql(dropSql);
			}
			
			while(tableRs.next()){
				/**shashijie 2010-11-20 STORY 1698 */
				dropSql = dbl.doOperSqlDrop("drop table " + tableRs.getString("Table_Name"));
				/**end*/
				dbl.executeSql(dropSql);
			}
			
			dbl.closeResultSetFinal(viewRs);
			dbl.closeResultSetFinal(tableRs);
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除VQ开头的用户视图和表出错", e);
		} finally {
			dbl.closeResultSetFinal(viewRs);
			dbl.closeResultSetFinal(tableRs);
		}
	}




	/**判断系统是否需要更新配置文件 moved to CtlDlUpdate Class, modified by yeshenghong 20120322 story 2164
	 * //MS01796 add by licai 20101019 TOOLS工具更新程序功能需求  
	 * @param versionBefUpdate
	 * @param versionAftUpdate
	 * @return 数据库更新前后版本发生变化的话，就执行更新配置文件，返回true
	 */
	private boolean isNeedUpdateCfgFiles(String versionBefUpdate,String versionAftUpdate){
		return !versionAftUpdate.equals(versionBefUpdate);
	}
	//MS01796 add by licai 20101019 TOOLS工具更新程序功能需求   end---
	
	/**shashijie 2011.03.14 TASK #2747::需要在系统界面"帮助"添加下拉菜单，以便看到版本信息 */
    private String getProductInfo(HttpServletRequest request) throws YssException{
    	String IssueDate = "";
    	Connection conn = null;
		String Sql = "";
		ResultSet rs = null;
		DbBase dbl = YssUtil.getSessionDbBase(request);
		YssPub pub = YssUtil.getSessionYssPub(request);
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			Sql = "SELECT MAX(FIssueDate) FIssueDate FROM tb_fun_version WHERE FAssetGroupCode = " + dbl.sqlString(pub.getPrefixTB()) ;
			rs = dbl.openResultSet(Sql);
			while(rs.next()){
				IssueDate = YssFun.formatDate(rs.getString("FIssueDate"),"yyyy-MM-dd");
			}
			dbl.closeResultSetFinal(rs);
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("获取更新日期出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return IssueDate;
	}
    
    /**
	 * added by liubo 2011.08.01.Story #745 
	 * 当当前操作系统用户名等于配置文件中存储的用户名时，不用登录界面，而是通过操作系统的用户和密码进行验证登录
	 * 该功能需要在DBSETTING文件的末尾添加一个字段，“YES”或者“NO”，YES表示启用该功能，NO表示不启用。若不添加或者添加了其他的值，则默认不启用
	 * @参数：sDbLbl String
	 * @返回类型：String
	 */
    public String Fastlogin(String sDbLbl) throws YssException
    {
    	String sResult = "No";
    	String[] pa = null;
    	int i = 0;
    	
    	if (sDbLbl == null || sDbLbl.length() == 0) {
            sDbLbl = "[db_yssimsas]";
        }

        try {
	        String ps=YssFun.loadTxtFile("/dbsetting.txt");			//读取dbsetting.txt文件
	        pa = ps.split(ps.indexOf("\r\n")>=0?"\r\n":"\n");
	        for (i = 0; i < pa.length; i++) { //星号注释
	            if (pa[i].trim().equalsIgnoreCase(sDbLbl)) 
	            {
	                break;
	            }
	        }
	        //若数组PA的长度-i小于等于6，表示[db_yssimsas]文件头下面的数据小于等于5行，即没有添加是否启用快速登录的字段。直接返回默认值
	        if (pa.length - i > 6)
	        {
	        	sResult = pa[i+6];
	        }
	        
        }
        catch(Exception ex)
        {
        	throw new YssException(ex.getMessage());
        }
    	
    	return sResult.length()<=0 || sResult==null?"NO":sResult;
    }
    
    /**
     * #795 核心业务系统客户端访问服务器端缺乏安全防范机制
     * 这个方法用于解决https不能用IP地址的形式直接访问
     */
    private void verifyHttps(){
        HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
        };
        
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
    
    //add by guolongchao 20120426 若客户启用了自审功能,更新相应的表结构    QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc
    private void updateTableData(DbBase dbBase) throws YssException{    	
    	Connection conn = null;
		String Sql = "";
		ResultSet rs = null;		
		try {
			conn = dbBase.loadConnection();
			conn.setAutoCommit(false);
			rs = dbBase.openResultSet(" select * from  TB_SYS_AssetGroup  where  fauditown='yes' and FCheckSelf='yes' ");
			if(rs.next())
			{
				dbBase.executeSql(" delete from  Tb_Sys_OperationType  where FType = 'system' and fopertypecode='auditOwn' ");  
				dbBase.executeSql(" insert into Tb_Sys_OperationType(fopertypecode,fopertypename,ftype) values('auditOwn','自审','system') ");  
				dbBase.executeSql(" update tb_fun_menubar set fopertypecode=fopertypecode||',auditOwn' where instr(fopertypecode,'audit')>0 and instr(fopertypecode,'auditOwn')=0 "); 			
			}			
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("更新tb_fun_menubar出错!", e);
		} finally {
			dbBase.closeResultSetFinal(rs);
		}		
	}

    //20130128 added by liubo.Story #2839.
    //在更新数据库之前就要为操作日志表加上FLOGDATA4字段，避免更新老版本的库（比58更低）出现FLOGDATA4字段无效的情况
    private void alterOperLogTb(YssPub pub) throws YssException
    {
		String strSql = "";
		boolean bTrans = false;
        ResultSet rs = null;
        try
        {
	    	rs = pub.getDbLink().queryByPreparedStatement("select * from tb_sys_operlog where 1 = 2");
	        if(!pub.getDbLink().isFieldExist(rs,"FLOGDATA4"))
	        {
		        strSql = "alter table tb_sys_operlog add FLOGDATA4 CLOB";
		        pub.getDbLink().executeSql(strSql);
	        }
        }
        catch(Exception ye)
        {
        	throw new YssException("创建日志表FLogData4字段出错：" + ye.getMessage());
        }
        finally
        {
        	pub.getDbLink().closeResultSetFinal(rs);
        }
    }
    
}
