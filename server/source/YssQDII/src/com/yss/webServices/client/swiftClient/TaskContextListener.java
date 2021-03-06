package com.yss.webServices.client.swiftClient;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
/**
 * 监听器，在web工程生命周期内运行，定时执行发送任务的请求
 * @author yh 2011.05.25 QDV411建行2011年04月19日01_A
 *
 */
public class TaskContextListener extends HttpServlet implements
		ServletContextListener {
	
	public static Timer timer = null;
	/**
	 * 监听器初始化方法，工程启动时执行
	 */
	public void contextInitialized(ServletContextEvent event) {
		//实例化定时器
		timer = new Timer(true);
		System.out.println("定时器已启动");
		//调用定时任务,每一小时执行一次
		timer.schedule(new WSTask(timer), 0, 60*60*1000); 
		//timer.schedule(new WSTask(timer), 0, 1000); 

	}
	/**
	 * 监听器销毁方法
	 */
	public void contextDestroyed(ServletContextEvent event) {
		//定时器销毁
		//edit by songjie 2012.05.25 BUG 4177 QDV4博时2012年03月29日02_B 添加非空判断
		if(timer != null){
			timer.cancel();
		}
		System.out.println("定时器已销毁");

	}

}
