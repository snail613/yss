package com.yss.webServices.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.yss.util.WarnPluginLoader;
import com.yss.util.YssException;
import com.yss.util.YssUtil;
//story 2188 by zhouwei 20120113 服务器启动时，装载预警beans   QDV4赢时胜(上海开发部)2012年2月3日02_A
public class BeansLoadingListener  implements ServletContextListener{

	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	//服务器启动时，装载bean
	public void contextInitialized(ServletContextEvent arg0) {
		String serviceType = arg0.getServletContext().getServerInfo().toUpperCase();
		//获取服务器路径
		String path = arg0.getServletContext().getRealPath("/");
		if(serviceType.indexOf("WEBLOGIC")>-1){
    		try {
    			path=this.getAppPathForWeblogic(arg0);
    			//--- add by songjie 2013.04.09 BUG 7354 QDV4建行2013年03月20日01_B start---//
    			File fPath = new File(path + "/cfg");
    			if (!fPath.exists())
    			{
    				path = arg0.getServletContext().getRealPath("/");
    			}
    			//--- add by songjie 2013.04.09 BUG 7354 QDV4建行2013年03月20日01_B end---//
			} catch (YssException e) {
				e.printStackTrace();
			}
    	} 	
		WarnPluginLoader.setApplicationContext(path);
	}
	//weblogin服务器获取路径
    private String getAppPathForWeblogic(ServletContextEvent arg0) throws YssException{ 	
    	try{
    		return arg0.getServletContext().getResource("/").getPath();
    	}catch(Exception e){
    		throw new YssException();
    	}
    }
}
