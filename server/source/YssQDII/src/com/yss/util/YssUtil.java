package com.yss.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.PropertyConfigurator;

import com.yss.dsub.DbBase;
import com.yss.dsub.YssPub;
import com.yss.pojo.sys.YssStatus;
import com.yss.serve.UserCheck;

/**
 *
 * <p>Title: </p>
 * <p>Description: 纯粹与web或者java相关的处理</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @author alex
 * @version 1.0
 */
public class YssUtil {
	
    public YssUtil() {
    }

    /**
     * 把日期字符串转换成dtpicker控件的<PARAM NAME="CurrentDate" VALUE="X X X">
     * 这样修改不会报错，并且速度最快，不会闪烁
     */
    public static final int dtpDateValue(String sDate) throws YssException {
        return YssFun.dateDiff(YssFun.parseDate("1899-12-30"),
                               YssFun.toDate(sDate));
    }

    /**
     * 以字节流方式获取request内的数据，返回字节数组 */
    public static final byte[] getBytesFromClient(HttpServletRequest request) throws
        IOException {
    	//modified by yeshenghong 20121015 建行风险检查，参照北京方法修改
		InputStream is = (InputStream) request.getInputStream();
		int size = request.getContentLength();
		int count = 0, lenread;
		if (size < 0)
			size = 0; // 避免下句出错
		byte[] buffer = new byte[size];
		byte[] buffertmp = new byte[size];
		while ((lenread = is.read(buffertmp)) != -1) {
			System.arraycopy(buffertmp, 0, buffer, count, lenread);
			count += lenread;
		}
		if (count == size)
			return buffer;
		// 考虑可能存在的提前结束情况
		byte[] buf = new byte[count];
		System.arraycopy(buffer, 0, buf, 0, count);
		return buf;
    }

    /**
     * 以字节流方式获取request内的数据，返回String */
    public static final String getBytesStringFromClient(HttpServletRequest
        request) throws IOException {
        return new String(getBytesFromClient(request), "GBK");
    }

    /**
     * 返回request对应的Web应用的Root部分地址，包含了/，用于提供相对路径以组成URL 
     * @throws MalformedURLException */
    public static final String getURLRoot(HttpServletRequest request,
                                          boolean bSlash) throws MalformedURLException {
    	
    	// add by jiangshichao 2011.09.22  weblogic 通过控制台部署war包，获取不到完整路径 
    	String serviceType = request.getSession().getServletContext().getServerInfo().toUpperCase();
    	//serviceType = "WEBLOGIC";
    	//modified by yeshenghong 20120504 BUG4366
    	String sReturnPath = "";	//20120319 added by liubo.此变量用于获取getAppPathForWeblogic方法的返回值
    	
    	if(serviceType.indexOf("WEBLOGIC")>-1){
    		//20120319 modified by liubo.Bug #4066
    		//在weblogic环境下，以文件夹的形式部署后台，cfg文件夹不会像war包一样被解压到weblogic根目录下的部署路径，若以weblogic的部署路径读取cfg文件夹下的配置文件时，就会报找不到路径的错误
    		//因为无法直接区分war包部署和文件夹部署，所以以weblogic下的部署路径+cfg的形式来判断路径是否存在。若路径存在，则为war包部署;若不存在，则为文件夹部署
    		//===================================
    		try {
//				return getAppPathForWeblogic(request);
    			sReturnPath = getAppPathForWeblogic(request);
    			File fPath = new File(sReturnPath + "/cfg");
    			if (fPath.exists())
    			{
    				return sReturnPath;
    			}
        	//===================end================
			} catch (YssException e) {
				e.printStackTrace();
			}
    	}
        StringBuffer surl = request.getRequestURL();
        String stmp = request.getServletPath();
        
        surl.setLength(surl.length() - stmp.length() + (bSlash ? 1 : 0));
        return surl.toString();
    }

    //add by jiangshichao 2011.09.22  weblogic 通过控制台部署war包，获取不到完整路径 
    public static String getAppPathForWeblogic(HttpServletRequest req) throws YssException{
    	try{
    		return req.getSession().getServletContext().getResource("/").getPath();
    	}catch(Exception e){
    		throw new YssException();
    	}
    }
    
    /**
     * 获取配置文件的绝对路径
	 * BUG7549 panjunfang add 20130428	
     * @param appConContextName 配置文件名
     * @return 配置文件绝对路径
     * @throws YssException
     */
    public static String getAppConContextPath(String appConContextName) throws YssException{
    	return getAppConContextPath(null, appConContextName);
    }
    
    /**
     * 获取配置文件的绝对路径
	 * BUG7549 panjunfang add 20130428	
     * @param path 当前WEB应用的绝对路径
     * @param appConContextName 配置文件名
     * @return
     * @throws YssException
     */
    public static String getAppConContextPath(String path, String appConContextName) throws YssException{
    	String sAppConContextPath = "";
    	String webRealPath = "";
    	String fileSeparator = "";
    	try{
    		//获取应用服务器操作系统的文件分隔符 
            fileSeparator = System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\";

    		if(null == path) {
    			//如下几种情况通过本Class所在的ClassLoader的ClassPath来获取WEB应用的绝对路径：
    			//1、如果没有传入当前WEB应用的绝对路径，则通过本Class所在的ClassLoader的ClassPath来获取绝对路径。
    			//2、weblogic环境下，通过控制台部署war包，在Servlet中通过getServletContext().getRealPath("")获取为NULL
    			//3、webservice 获取配置文件的绝对路径
    			webRealPath = getAbsolutePathByClassLoader();
    			fileSeparator = "/";//URL形式的拼接分隔符
    		} else {
    			webRealPath = "file:" + path;
    		}
    		
			if (!webRealPath.endsWith("\\") && !webRealPath.endsWith("/")) {
				webRealPath += fileSeparator;
			} 
    		
            sAppConContextPath = webRealPath + "cfg" + fileSeparator + appConContextName;
    	} catch(Exception e){
    		throw new YssException(e.getMessage());
    	}
    	return sAppConContextPath;
    }
 
    /** 
     * 获取本Class所在的ClassLoader的ClassPath来获取WEB应用的绝对路径。
     * URL形式的绝对路径，路径形式如：file:/D:/，物理绝对路径，路径形式如：D:\
     * BUG7549 panjunfang add 20130510	 
     * @return URL形式的绝对路径
     * @throws Exception 
     */  
    private static String getAbsolutePathByClassLoader() throws Exception {  
        String webPath = YssUtil.class.getClassLoader().getResource("/").toString();
        
        webPath = webPath.replaceAll("[\\\\\\/]WEB-INF[\\\\\\/]classes[\\\\\\/]?", "/");  
        webPath = webPath.replaceAll("[\\\\\\/]+", "/");  
      
        return webPath;
    }  

    public static final String getURLRoot(HttpServletRequest request) throws MalformedURLException {
        return getURLRoot(request, false);
    }

    /**
     * 把从客记端传来的表单中的内容放到哈希表（集合）中，在servlet中使用，并
     * 通过servlet把值给JavaBean中所使用的参数。
     * 使用方法为：
     *
     * 在客户端：
     *    Str=frmTest.txt.name & vbTab & frmTest.txt.value & vbCrlf
     *    Str=Str+frmTest.hid.name & vbTab & frmTest.hid.value & vbCrlf
     *    Str=Str+frmTest.cbx.name & vbTab & frmTest.cbx.value & vbCrlf
     *    Str=Str+frmTest.sel.name & vbTab & frmTest.sel.value & vbCrlf
     *    sd=psub.sendStringToServer(Str ,"http://localhost:8080/WebModule/servlet",,ss)
     *    组全表单内容，并使用XMLHTTP提交。
     *
     * 在(Servlet)中
     *    使用此方法获取表单中的值   String requestValue=YssUtil.getBytesStringFromClient(request);
     *     HashMap dd=getHashTableForRequest(requestValue)   intTableSize可以使用默认值为8,可不写。
     *     比如有一个在JAVABEAN中要使用的文本框表单名称为txtValue，值为12;则
     *     settxtValue()=dd.get("txtValue")
     *     则settxtValue()=12.
     *
     * 注意：要在使用取值(dd.get(XXX))前断定哈希表是否为空。否则会出错。
     *
     * @param strReq String
     * @return HashMap
     * author  (阿香)xuqingww
     */
    public static final HashMap getHashTableForRequest(String strReq) {
        String[] sCols;
        String[] sRows;

        HashMap hashmap = null;
        if (strReq.length() != 0) {
            hashmap = new HashMap();
            sCols = YssFun.split(strReq, YssCons.YSS_LINESPLITMARK);
            for (int i = 0; i < sCols.length; i++) {
                sRows = YssFun.split(sCols[i], "\t");
                if (sRows.length > 1) { //这里处理的是以字节流上传的参数，所以不用处理乱码
                    hashmap.put(sRows[0], sRows[1]);
                }
            }
        }
        return hashmap;
    }

    /**
     * 检查当前session与服务器是否连接，如果是，则返回ysspub对象，否则抛出异常
     * 本函数用于替代servlet中获取ysspub对象语句
     * @param request HttpServletRequest
     * @throws YssException
     * @return YssPub
     */
    public synchronized static final YssPub getSessionYssPub(HttpServletRequest request) throws
        YssException {
        Object obj = request.getSession().getAttribute(YssCons.SS_PUBLIC);
        if (obj == null) {
            throw new YssException("尚未登录系统或与服务器的连接已失效，请重新登录系统！");
        }
        //bug 3654 by zhouwei 20120119  对pub中的一些系统属性重新设定   QDV4泰达2012年01月16日01_B  start--------
        YssPub ysspub=(YssPub)obj;
        //if(ysspub.getAssetGroupCode()!=null && !ysspub.getAssetGroupCode().equals("") && !ysspub.getAssetGroupCode().equals(" ")){
        //	ysspub.yssResetAttr(ysspub.getAssetGroupCode());
        //}       
        //bug 3654 by zhouwei 20120119  对pub中的一些系统属性重新设定   QDV4泰达2012年01月16日01_B  end----------
        return ysspub;
    }

    public static final YssStatus getSessionYssStatus(HttpServletRequest request) throws
        YssException {
        Object obj = request.getSession().getAttribute(YssCons.SS_RUNSTATUS);
        if (obj == null) {
            throw new YssException("尚未登录系统或与服务器的连接已失效，请重新登录系统！");
        }
        return (YssStatus) obj;
    }

    /**
     * 检查当前session与服务器是否连接，如果是，则返回dbbase对象，否则抛出异常
     * 本函数用于替代servlet中获取dbbase对象语句
     * @param request HttpServletRequest
     * @throws YssException
     * @return DbBase
     */
    public static final DbBase getSessionDbBase(HttpServletRequest request) throws
        YssException {
        Object obj = request.getSession().getAttribute(YssCons.SS_DBLINK);
        if (obj == null) {
            throw new YssException("尚未登录系统或与服务器的连接已失效，请重新登录系统！");
        }
        return (DbBase) obj;
    }

    /**
     * STORY #2343 QDV4建行2012年3月2日04_A
     * add by songjie 2012.11.02 
     * @param request
     * @return
     * @throws YssException
     */
    public static final DbBase getSessionDbBaseBLog(HttpServletRequest request) throws
    YssException {
    Object obj = request.getSession().getAttribute(YssCons.SS_DBLINK_BLOG);
    if (obj == null) {
        throw new YssException("尚未登录系统或与服务器的连接已失效，请重新登录系统！");
    }
    return (DbBase) obj;
}
    
    /**
     * 把从客户端传递的字符串分解到数组当中去
     * @param strReq String
     * @return String[][]
     */
    public static final String[][] ClientData(String strReq) {
        boolean fLag = false;
        String[][] sData = null;
        String[] sCols;
        String[] sRows;
        if (strReq.length() != 0) {
            sCols = YssFun.split(strReq, YssCons.YSS_LINESPLITMARK);
            for (int i = 0; i < sCols.length - 1; i++) {
                sRows = YssFun.split(sCols[i], "\t");
                if (fLag == false) {
                    fLag = true;
                    sData = new String[sCols.length - 1][sRows.length - 1];
                }
                if (sRows.length > 1) {
                    for (int j = 0; j < sRows.length - 1; j++) {
                        sData[i][j] = sRows[j].toString();
                    }
                }
            }
        }

        return sData;
    }
    
    /*********************************
     * 1. 加载log4j配置文件
     * 2. 获取Web服务器的目录结构。设置成System属性，以便其他类调用
     * @param req
     * @param fileDir
     * @param fileName
     * @return
     * @throws YssException 
     */
    public static final void setSysProp(HttpServletRequest req) throws YssException{
    	String[] propertiesPath ;
    	String realPath = "";
    	String logsPath = "logs";
		try {
			String fileSeparator = System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\";//根据应用运行的系统环境获取文件分隔符  
			
			/********************************************************************
			 * 如果Web容器支持自动解压War包，则通过getRealPath() 获取应用所在路径。
			 * 如果Web容器不支持自动解压War包，则通过getResource("/").getPath() 获取应用所在路径。
			 * 
			 * 如果War包没解压，调用getRealPath()则直接返回null.
			 */
			realPath = req.getSession().getServletContext().getRealPath(req.getContextPath().substring(1));
			if(realPath == null){
				realPath = req.getSession().getServletContext().getResource("/").getPath();
			}
			
			
			/**********************************************************************
			 * 说明：系统将调试日志文件夹Ysstech_logs置于Web容器既有的日志文件夹下
			 *       这4种Web容器的日志文件除了Jboss 的日志文件名称是Log外，其他容器的日志文件夹名称都为Logs
			 *       路径截取方式：将获取服务器资源的绝对路径，把路径截取到Web容器日志文件夹同级目录下
			 *  Tomcat
			 *   部署路径：Tomcat安装目录下的webapps
			 *  WebLogic
			 *   部署路径：Weblogic安装目录下的user_projects\domains\base_domain\autodeploy
			 *  WebsPhere
			 *   部署路径：WebsPhere安装目录下的WebSphere\AppServer\profiles\AppSrv01\installedApps\
			 *  Jboss
			 *   部署路径：Jboss安装目录下的\server\default\deploy
			 */
			propertiesPath = realPath.split("webapps");//Tomcat容器截取方式
			if(propertiesPath.length==1){
				propertiesPath = realPath.split("user_projects");//Weblogic容器截取方式
			}if(propertiesPath.length == 1){
				propertiesPath = realPath.split("installedApps");//WebsPhere容器截取方式
			}
			if(propertiesPath.length==1){
				propertiesPath = realPath.split("tmp");//JBoss容器截取方式
				logsPath = "log";
			}if(propertiesPath.length==1){
				propertiesPath = realPath.split(":");//其他类型的则直接将Ysstech_Logs 文件夹置于应用同级目录
				propertiesPath[0]=propertiesPath[0]+":";
				logsPath = "";
			}
			System.setProperty("logsDir",propertiesPath[0]+fileSeparator+logsPath);
			createFold(System.getProperty("logsDir")+fileSeparator, "Ysstech_lOGS");
			
			InputStream is = req.getSession().getServletContext(). getResourceAsStream("/WEB-INF/log4j.properties"); 
			Properties props = new Properties(); 
			props.load(is);
			PropertyConfigurator.configure(props);
				

		} catch (Exception e) {
			throw new YssException("获取log4j.properties文件路径出错！！！");
		}
    }
    
    public static boolean createFold(String path, String folder) {
        File dirFile;
        /*File fScheduleLog;//无用注释
        String sFileName;
        boolean bFile = false;
        String fileSeparator = System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\";//文件分隔符*/
        try
        {
            dirFile = new File(path + folder);
            
            if(dirFile.exists()) {
                // folder exists
                System.out.println("The folder exists.");
                return false;
            } else {
                // folder not exists
                System.out.println("The folder do not exist,now trying to create a one...");
                // create folder
                if(dirFile.mkdir()) {
                    // success
                	//File file= new File(path + folder+fileSeparator+"QDII.log");
                	//File file1= new File(path + folder+fileSeparator+"error.log");//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                    System.out.println("Create successfully!");   
                    return true;
                } else {
                    // fail
                    System.out.println("Disable to make the folder,please check the disk is full or not.");
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
	/**20110704 Added by liubo.Story #1145
	 * 在服务器段查找是否存在某个文件或文件夹，若存在，则忽略；若不存在，则进行创建。
	 * @param sLogPath
	 *            String	文件或文件夹的路径
	 * 
	 * @throws YssException
	 */
    public static void initScheduleLog(String sLogPath) throws YssException
    {    	
    	try
    	{
            
            File fSLogPath=new File(sLogPath); 
            if (fSLogPath.exists())
            {
            	
            }
            else
            {
            	if (fSLogPath.mkdirs())
            	{
            		
            	}
            	else
            	{
            		throw new YssException("获取调度方案日志路径失败！");
            	}
            }
            //File fScheduleLog = new File(sLogPath + fileSeparator + "ScheduleLog" + "_" + sAssetGroup + "_" + sPortCode + ".log");
            //bout.close();
    	}
    	catch(Exception e)
    	{
    		throw new YssException(e);
    	}
    	   	   	
    }
    
	/**20110704 Added by liubo.Story #1145
	 * 在服务器端以组合群+组合为单位创建“调度方案执行”的LOG文件
	 * @param sLog
	 *            String	日志内容（以一行为单位）
	 * @param sLogPath
	 *            String	日志路径，精确到文件夹
	 * @param sAssetGroup
	 *            String	组合群代码
	 * @param sPortCode
	 *            String	组合代码
	 * @param IsOverride
	 *            String	确定是否对目标文档原先已存在的内容进行覆盖。1为覆盖，0为不覆盖
	 *            
	 * @throws YssException
	 */
    
    //20110711 modified by liubo.Story #1145.增加IsOverride变量用于确定是否对目标文档原先已存在的内容进行覆盖。1为覆盖，0为不覆盖
	public static void WriteScheduleLog(String sLog,String sLogPath,String sAssetGroup,String sPortCode,String sOperDate,String IsOverride) throws YssException
    {
    	try 
    	{
    		 	BufferedWriter bout = null;
    			initScheduleLog(sLogPath);
    		
    			String fileSeparator = System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\";//文件分隔符
    			if (IsOverride.equals("0"))
    			{
    				bout=new BufferedWriter(new FileWriter(new File(sLogPath + fileSeparator + "ScheduleLog" + "_" + sAssetGroup + "_" + sPortCode + "_Time-" + sOperDate + ".log"),true)); 
    			}
    			else
    			{
    				DelOldLog(sLogPath, "ScheduleLog" + "_" + sAssetGroup + "_" + sPortCode + "_Time-");
    				bout=new BufferedWriter(new FileWriter(new File(sLogPath + fileSeparator + "ScheduleLog" + "_" + sAssetGroup + "_" + sPortCode + "_Time-" + sOperDate + ".log"))); 
        			
    			}
    			bout.write("\r\n" + YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd HH:mm:ss") + "----------" + (sLog.equals("")?" ":sLog)); 
    			bout.close();
//        	bout.close(); 
        }catch(Exception e){ 
                throw new YssException(e.getMessage()); 
            } 
    }
    
	/**20110705 Added by liubo.Story #1145
	 * 从服务器端读取以组合群+组合为单位创建“调度方案执行”的LOG文件日志文件

	 * @param sLogPath
	 *            String	日志路径，精确到文件夹
	 * @param sLogName
	 *            String	日志文件名称
	 * @return String
	 * 
	 * @throws YssException
	 */
    public static String ReadScheduleLog(String sLogPath,String sLogName) throws YssException
    {
    	
    	try
    	{	
    		String sCurrently = "";
    		//无用注释
    		//String fileSeparator = System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\";
    		
    		//File fSLogPath=new File(sLogPath + fileSeparator + sLogName); 
    		
    		//20110712 modified by liubo.Story #1145
    		//搜索文件夹中是否存在格式为ScheduleLog_组合群代码_组合代码_Time-yyyy-mm-dd_hh-mm-ss.log的LOG文件。若存在，则将该文件做为需要读取的日志文件
    		File f=new File(sLogPath);
	    	if(f.isDirectory())
		    {
		    	File[] fList=f.listFiles();
		    	for(int j=0;j<fList.length;j++)
		    	{
		    		if(fList[j].getName().startsWith(sLogName))
			    	{
		    			sCurrently = fList[j].getPath();
			    	}
		    	}
		    }
	        if (!sCurrently.equals(""))
	        {
	        	
	        	BufferedReader br=new BufferedReader(new FileReader(sCurrently));

	    		String str="";

	    		String r=br.readLine();

	    		while(r!=null){

		    		str+=r+"/n";
		
		    		r=br.readLine();
		    		
	    		}
	    		
	    		return str;
	        }
	        else
	        {
	        	return "不存在属于该组合的日志文件！";
	        }
    	}
    	catch(Exception e)
    	{
    		throw new YssException(e.getMessage());
    	}
    	
    }
    
	/**20110712 Added by liubo.Story #1145
	 * 查询存放日志的文件夹，是否存在格式为ScheduleLog_组合群代码_组合代码_Time-yyyy-mm-dd_hh-mm-ss.log的LOG文件，若存在，则删除

	 * @param strPath
	 *            String	日志文件夹路径，精确到文件夹
	 * @param sFileNameFormat
	 *            String	日志的命名格式
	 * @return String
	 * 
	 * @throws YssException
	 */
    public static void DelOldLog(String strPath,String sFileNameFormat) throws YssException
    {
    	try
    	{
	    	File f=new File(strPath);
	    	if(f.isDirectory())
		    {
		    	File[] fList=f.listFiles();
		    	for(int j=0;j<fList.length;j++)
		    	{
		    		if(fList[j].getName().startsWith(sFileNameFormat))
			    	{
		    			fList[j].delete();
			    	}
		    	}
//		    	for(int j=0;j<fList.length;j++)
//		    	{
//		
//			    	if(fList[j].isFile())
//			    	{
//			    		System.out.println(fList[j].getPath());//把这话换成你要处理的语句.比如 fList[j].substring(fList[j].length-3,3)=="txt"
//			    	}
//		
//		    	}
		    }
    	}
    	catch(Exception e)
    	{
    		throw new YssException(e.getMessage());
    	}

    	} 
    /**去除字段中的特殊字符
	 * & < > ' \
	 * */
	public static final String replaceStrangeString( String rawString){
		if (rawString == null) return "";
		rawString.replace("&", "");
		rawString.replace("<", "");
		rawString.replace(">", "");
		rawString.replace("'", "");
		rawString.replace("\"", "");
		   
		   return rawString ;
	}

    
    /**
     * 解析系统模块设置文件，初始化功能模块设置
     *  add by jsc 20120521
     *  【凭证优化：通过读取配置文件来初始化】
     * @throws YssException
     */
    public static void initMoudle()throws YssException{
    	
    	 java.io.File ff = null;
         java.io.FileInputStream fi = null;
         Properties props = new Properties();
    	try{
    		
    		//20130229 modified by liubo.Story #3649
    		//农行IT要求服务器上不要放置任何多余文件，因此需要把ysstech_ModuleSet.properties配置文件放在S端的Web-inf文件夹下面
    		//在读文件时首先读web-inf文件夹，找不到该文件时再去找服务器端的根目录
    		//====================================
    		String sFilenName = File.separator + "ysstech_ModuleSet.properties";
    		
    		ff = new java.io.File(YssUtil.getPath(UserCheck.requestPath)+ "WEB-INF" + sFilenName);
    		if (!ff.exists())
    		{
    			ff = new java.io.File("/ysstech_ModuleSet.properties");
    		}
    		//================end====================
    		if(ff.exists()){
    			//如果存在系统模块设置文件
    			fi = new java.io.FileInputStream(ff);
    			props.load(fi);
    			//---edit by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
    			if(props.getProperty("vch.builer.mode") == null){
    				YssCons.YSS_VCH_BUILDER_MODE = "batch";
    			}else{
    				YssCons.YSS_VCH_BUILDER_MODE = props.getProperty("vch.builer.mode");
    			}
    			if(props.getProperty("vch.check.mode") == null){
    				YssCons.YSS_VCH_CHECK_MODE   = "batch";
    			}else{
    				YssCons.YSS_VCH_CHECK_MODE   = props.getProperty("vch.check.mode");
    			}
    		    if(props.getProperty("vch.doOutacc.mode") == null){
    		    	YssCons.YSS_VCH_DOOUTACC_MODE = "batch";
    		    }else{
    		    	YssCons.YSS_VCH_DOOUTACC_MODE = props.getProperty("vch.doOutacc.mode");
    		    }
    		    if(props.getProperty("fuContractMV.mode") == null){
    		    	YssCons.YSS_STOCK_INDEX_FUTURE_MV_MODE = "default";
    		    }else{
    		    	YssCons.YSS_STOCK_INDEX_FUTURE_MV_MODE = props.getProperty("fuContractMV.mode");
    		    }
    		    //---edit by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
    		    //add by songjie 2012.09.18 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A
    		    YssCons.YSS_EWCLUE_MODE = ((props.getProperty("ewClue.mode") == null) ||
    		    (props.getProperty("ewClue.mode") != null && 
    		     props.getProperty("ewClue.mode").equalsIgnoreCase("hide"))) ? 
    		    "hide" : "default";
    		    
    		    //20121126 added by liubo.Story #3057
    		    YssCons.YSS_DEFER_TIME = (props.getProperty("defer.defertime") == null ? "0" : props.getProperty("defer.defertime"));
    		    //20130220 added by liubo.Story #3517
    		    //获取外管局月报——QDII境内外币托管账户情况表的生成模式。若wgjrep.buildingmode节点为空，则默认获取建行模式
    		    //==================================
    		    YssCons.YSS_WGJRep_BuldingMode = (props.getProperty("wgjrep.buildingmode") == null ? "JH" : props.getProperty("wgjrep.buildingmode"));
    		    //================end==================
    		    
    		    /**Start 20130830 added by liubo.Story #4281.需求上海-[开发部]QDIIV4[低]20130729001
    		     * 设置控制业务日志的保存方式的变量，默认为3（只保存结果类型为“失败\提醒\终止”的日志数据）*/
    		    YssCons.YSS_BusinessLog_GenerateType = 
    		    	(props.getProperty("BusinessLog.GenerateType") == null ? "3" : props.getProperty("BusinessLog.GenerateType"));
    		    /**End 20130830 added by liubo.Story #4281.需求上海-[开发部]QDIIV4[低]20130729001*/
    		    
    		    //--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
    		    if(props.getProperty("expMonitorFile") == null){
    		    	YssCons.YSS_EXP_MONITOR_FILE = false;
    		    }else{
    		    	YssCons.YSS_EXP_MONITOR_FILE = Boolean.parseBoolean(props.getProperty("expMonitorFile"));
    		    }
    		    //--- add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 end---//
    		    /*added by yeshenghong 2013-6-7 Story 3958 */
    		    if(props.getProperty("cashAcc.vchDict.cashInBank") == null)
    		    {
    		    	YssCons.YSS_VCH_DICT_CashInBank = "YSS_DCT907";   
    		    }else
    		    {
    		    	YssCons.YSS_VCH_DICT_CashInBank = props.getProperty("cashAcc.vchDict.cashInBank");
    		    }
    		    if(props.getProperty("cashAcc.vchDict.otherReceivalbes") == null)
    		    {
    		    	YssCons.YSS_VCH_DICT_OtherReceivalbes = "YSS_DCT908";  
    		    }else
    		    {
    		    	YssCons.YSS_VCH_DICT_OtherReceivalbes = props.getProperty("cashAcc.vchDict.otherReceivalbes");
    		    }
    		    if(props.getProperty("cashAcc.vchDict.otherPayables") == null)
    		    {
    		    	YssCons.YSS_VCH_DICT_OtherPayables = "YSS_DCT909";
    		    }else
    		    {
    		    	YssCons.YSS_VCH_DICT_OtherPayables = props.getProperty("cashAcc.vchDict.otherPayables");
    		    }
    		    if(props.getProperty("cashAcc.vchDict.interestReceivalbe") == null)
    		    {
    		    	YssCons.YSS_VCH_DICT_Interest = "YSS_DCT910"; 
    		    }else
    		    {
    		    	YssCons.YSS_VCH_DICT_Interest = props.getProperty("cashAcc.vchDict.interestReceivalbe");
    		    }
    		    if(props.getProperty("cashAcc.vchDict.interestRevenue") == null)
    		    {
    		    	YssCons.YSS_VCH_DICT_Revenue = "YSS_DCT911"; 
    		    }else
    		    {
    		    	YssCons.YSS_VCH_DICT_Revenue = props.getProperty("cashAcc.vchDict.interestRevenue");
    		    }
    		    if(props.getProperty("investPayCat.vchDict.profitAndLossCost") == null)
    		    {
    		    	YssCons.YSS_VCH_DICT_ProfitLossCost = "YSS_DCT904"; 
    		    }else
    		    {
    		    	YssCons.YSS_VCH_DICT_ProfitLossCost = props.getProperty("investPayCat.vchDict.profitAndLossCost");
    		    }
    		    if(props.getProperty("investPayCat.vchDict.accrualAndDeferral") == null)
    		    {
    		    	YssCons.YSS_VCH_DICT_AccrualDeferral = "YSS_DCT905";   
    		    }else
    		    {
    		    	YssCons.YSS_VCH_DICT_AccrualDeferral = props.getProperty("investPayCat.vchDict.accrualAndDeferral");
    		    }
    		    if(props.getProperty("investPayCat.vchDict.voucherAbstract") == null)
    		    {
    		    	YssCons.YSS_VCH_DICT_VoucherAbstract = "YSS_DCT906";    
    		    }else
    		    {
    		    	YssCons.YSS_VCH_DICT_VoucherAbstract = props.getProperty("investPayCat.vchDict.voucherAbstract");
    		    }
    		    /*end by yeshenghong 2013-6-7 Story 3958 */
    		    
    		    
    		}else{//modified by  yeshenhong 20120912 BUG5568
    			//情景：用户将系统模块设置文件删除，登录时发现模式仍然没改变
    			//所以这里需要添加判断
    			YssCons.YSS_VCH_BUILDER_MODE = "batch";  //凭证生成模式  default 默认模式(老模式) batch 批量模式
    		    YssCons.YSS_VCH_CHECK_MODE   = "batch";  //凭证审核模式  default 默认模式(老模式) batch 批量模式
    		    YssCons.YSS_VCH_DOOUTACC_MODE = "batch";  //凭证导出模式  default 默认模式(老模式) batch 批量模式
  		        //add by songjie 2012.12.11 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
    		    YssCons.YSS_STOCK_INDEX_FUTURE_MV_MODE = "default";
				//add by songjie 2012.09.18 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A
    		    YssCons.YSS_EWCLUE_MODE = "hide";  

    		    //20121126 added by liubo.Story #3057
    		    YssCons.YSS_DEFER_TIME = "0";
    		    //20130220 added by liubo.Story #3517
    		    //获取外管局月报——QDII境内外币托管账户情况表的生成模式，默认建行模式
    		    //==================================
    		    YssCons.YSS_WGJRep_BuldingMode = "JH";
    		    //================end==================
    		    //add by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
    		    YssCons.YSS_EXP_MONITOR_FILE = false;
    		    /**Start 20130830 added by liubo.Story #4281.需求上海-[开发部]QDIIV4[低]20130729001
    		     * 为控制业务日志的保存方式的变量赋默认值*/
    		    YssCons.YSS_BusinessLog_GenerateType = "3";
    		    /**End 20130830 added by liubo.Story #4281.需求上海-[开发部]QDIIV4[低]20130729001*/
    		}
    		
    	}catch(Exception e){
    		throw new YssException(e.getMessage());
    	}
    }

	/**shashijie 2012-11-23 STORY 3187,3327 由于环境问题获得的服务器路径最后的"\"符号时有时没有,
	 * 这里做一下处理,判断路径后是否有"\"号,没有统一加上 */
	public static String getPath(String path) {
		if (path == null || path.trim().length()==0 ) {
			return "";
		}
		//获取最后一位
		String temp = path.substring(path.length()-1);
		if (!temp.equals(File.separator)) {
			path += File.separator;
		}
		return path;
	}

	/**shashijie 2013-3-25 STORY 3368 判断字符串是否为null或者空"",若是则返回true*/
	public static boolean isNullOrEmpty(String param) {
		boolean falg = false;
		try {
			if (param == null || param.trim().equals("")) {
				falg = true;
			}
		} catch (Exception e) {
			falg = false;
		} finally {

		}
		return falg;
	}
	
	/**
	 * add dongqingsong 2013-05-10 Story 3871 获取给定路径先properties文件中相应的key的值
	 * @param path  properties文件对应的路径 
	 * @param key   属性的名称
	 * @throws IOException
	 * @return  返会key对应的值
	 */
	public  String getPathInfo(String path,String key) throws IOException {
		Properties prop = new Properties();
		FileInputStream fis = null;
		String getPathValue=null;
		try {
			fis = new FileInputStream(path);
			prop.load(fis);
			getPathValue=(String)prop.getProperty(key);
			} catch (FileNotFoundException e) {  
				//System.out.println("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在");
	            e.printStackTrace();  
	        } catch (IOException e) { 
	        	//System.out.println("装载文件--->失败!");
	            e.printStackTrace();  
	        }  finally{
				fis.close();
	        }
	        return getPathValue;
		}
		
	/**
	 * add dongqingsong 2013-05-10 Story 3871 将dataSource的内容写入到path对应的文件中
	 * @param path  要写入的路径
	 * @param dataSource 要写入的内容
	 * @throws IOException
	 */
	public static void writeTxt(String path, String fileName, String dataSource , String charEnCode) throws IOException{
		File f = new File(path);
		if(!f.exists())
			f.mkdirs();
		FileOutputStream outputStream = null;
		File file = new File(path + fileName);
		if(file.exists())
			file.delete();
		outputStream = new FileOutputStream(file);
		outputStream.write(dataSource.toString().getBytes(charEnCode));
		outputStream.flush();
		outputStream.close();
	}
    
}
