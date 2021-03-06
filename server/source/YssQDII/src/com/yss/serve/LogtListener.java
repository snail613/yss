package com.yss.serve;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.PropertyConfigurator;

import com.yss.util.LogTask;
import com.yss.util.MonitorExpTask;
import com.yss.util.YssCons;
import com.yss.util.YssUtil;
/**
 * 监听器，在web工程生命周期内运行，定时清理日志文件
 * @author guolongchao 20110917 STORY 1290 QDV4赢时胜(北京)2011年06月28日01_A
 */
public class LogtListener extends HttpServlet implements ServletContextListener 
{
	private static final long serialVersionUID = 1L;
	public static Timer timer = null;
	//---add by songjie 2012.11.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
	public static String YSS_MONITOR_EXP_TIME = ""; 
	public static String YSS_MONITOR_EXP_PATH = ""; 
	public int hour = 0;//小时
	public int minute = 0;//分钟
	public int second = 0;//秒
	//---add by songjie 2012.11.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
	
	/**
	 * 监听器初始化方法，工程启动时执行
	 */
	public void contextInitialized(ServletContextEvent event) {			
		if(readYsslog4j())//读取ysslog4j.properties文件,文件位于tomcat等应用服务器的根目录
		{
			timer = new Timer();//实例化定时器			
			Date dt1=new Date();	
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE));
			calendar.set(Calendar.HOUR_OF_DAY,0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Date dt2 =calendar.getTime();
			//Date dt2 = new Date(dt1.getYear()-1900,dt1.getMonth()-1,dt1.getDay());			
			long args=dt2.getTime()+24*60*60*1000-dt1.getTime();
			timer.schedule(new LogTask(timer), args, 24*60*60*1000);//每天0点调用定时任务
			//timer.schedule(new LogTask(timer), 5000, 50000);//5秒后调用定时任务,每隔50秒执行一次(测试使用,发布时将此行删除掉)	
		}
		//---add by songjie 2013.01.09 STORY #2343 QDV4建行2012年3月2日04_A start---//
		if(readMonitorPara()){
			timer = new Timer();//实例化定时器			
			//--- edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
			long args=30*60*1000;
			timer.schedule(new MonitorExpTask(timer), args, 30*60*1000);
			//--- edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 end---//
		}
		//---add by songjie 2013.01.09 STORY #2343 QDV4建行2012年3月2日04_A end---//
	}
	
	/**
	 * 监听器销毁方法
	 */
	public void contextDestroyed(ServletContextEvent event) {
		//定时器销毁
		//edit by songjie 2012.04.16 添加不为空的判断 BUG 4177 QDV4博时2012年03月29日02_B 添加非空判断
		if(timer != null){
			timer.cancel();		
		}
	}
	/**
	 * 读取ysslog4j.properties文件,文件位于tomcat等应用服务器的根目录(即与dbsetting.txt在同一目录之下)
	 * @return
	 */
	private boolean readYsslog4j()
	{
		try 
		{
			InputStream is=new FileInputStream("/ysslog4j.properties");
			Properties props = new Properties(); 
			props.load(is);		
			//创建对应的目录		
			//---edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A start---//	
			createDirectory((String)props.get("log4j.appender.D.File"),false);
			createDirectory((String)props.get("log4j.appender.E.File"),false);
			//---edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A end---//
			//将原来的参数{logsDir}设置为“”,目的：当用户登录连接系统时不再读取WEB-INF/log4j.properties
			System.setProperty("logsDir","");
			
			//保存至System中，删除日志文件时使用
			System.setProperty("log4j.appender.D.File",(String)props.get("log4j.appender.D.File"));
			System.setProperty("log4j.appender.E.File",(String)props.get("log4j.appender.E.File"));
			if(props.get("yss.log.timepoint")!=null)
			    System.setProperty("yss.log.timepoint",(String)props.get("yss.log.timepoint"));
			if(props.get("yss.log.timediatance")!=null)
			    System.setProperty("yss.log.timediatance",(String)props.get("yss.log.timediatance"));			
			//保存至System中，删除日志文件时使用--end			
			PropertyConfigurator.configure(props);		
			return true;			
		} 
		catch (FileNotFoundException e) 
		{
			//System.out.println("用户没有相关的ysslog4j.properties配置");
			return false;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}		
	}
	
	/**
	 * add by songjie 2012.11.12
	 * STORY #2343 QDV4建行2012年3月2日04_A
	 * 读取监控导出文件路径 以及 导出时间 参数
	 * @return
	 */
	private boolean readMonitorPara()
	{
		try 
		{
			//--- edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 start---//
			YSS_MONITOR_EXP_PATH = "C:\\ITISLOGS";
			YssUtil.initMoudle();
			return YssCons.YSS_EXP_MONITOR_FILE;		
			//--- edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001 end---//
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}	
	}
	
	/**
	 * 功能：创建目录结构，并创建对应的文件
	 * @param pathName 例如：1. F:/111/222/333/aaa/bbb/QDII.log(linux),
	 *                       2. F:\\111\\222\\333\\aaa\\bbb\\QDII.log(windows)
	 * isFolder 是否创建文件夹 add by songjie 2012.11.15 
	 */
	private void createDirectory(String pathName, boolean isFolder)
	{
		boolean flag=false;
		if(pathName!=null&&pathName.trim().length()>0)
		{
			//---edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A start---//
			if(isFolder){//创建文件夹
				File folder = new File(pathName);
				if(!folder.exists())
				{
					try{
						flag = folder.mkdirs();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}else{
			//---edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A end---//
				//分离出对应的目录和文件名
				String dirName=pathName.substring(0, pathName.lastIndexOf(File.separator));
				String fileName=pathName.substring(pathName.lastIndexOf(File.separator)+1,pathName.length());			
			
			File dirNameFile=new File(dirName);
			if(dirNameFile.exists())
			{
				if(dirNameFile.isDirectory())//如果文件存在,并且是目录
					flag=true;
				else//如果文件存在,但却是一个文件，也要创建和此文件名相同的目录
					flag=dirNameFile.mkdirs();
			}
			else//如果文件不存在
				flag=dirNameFile.mkdirs();
			
				if(flag)
				{
					File file=new File(dirName+File.separator+fileName);	
					try 
					{
						file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
			}
		}
	}
}
