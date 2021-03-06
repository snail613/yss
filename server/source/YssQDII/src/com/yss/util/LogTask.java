package com.yss.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimerTask的任务类，主要用于清除日志文件
 * @author guolongchao 20110917 STORY 1290 QDV4赢时胜(北京)2011年06月28日01_A
 */
public class LogTask extends TimerTask{	
	private Timer timer = null;
	
	public LogTask(){}
	public LogTask(Timer timer)
	{
		this.timer=timer;
	}
	
	public void run() {		
		//获取当前时间
		Calendar calendar = Calendar.getInstance();		
		if(calendar.get(Calendar.HOUR_OF_DAY) == 0)//每天0点执行清理日志的任务
		{
			System.out.println("~~~~~~~~~~~~~~~~~开始删除文件~~~~~~~~~~~~~~");
			deleteFiles(System.getProperty("log4j.appender.D.File"),System.getProperty("yss.log.timepoint"),System.getProperty("yss.log.timediatance"));
			deleteFiles(System.getProperty("log4j.appender.E.File"),System.getProperty("yss.log.timepoint"),System.getProperty("yss.log.timediatance"));
			System.out.println("~~~~~~~~~~~~~~~~~删除文件成功~~~~~~~~~~~~~~~");
		}
	}
	
	public Timer getTimer() {
		return timer;
	}
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	/**
	 * 功能：根据ysslog4j.properties文件配置的时间点，时间段删除对应的目录下的日志文件
	 * @param pathName   日志文件所在的路径
	 * @param timepoint  时间点（在此时间点之前的日志文件都将被删除）
	 * @param timediatance 时间段（在当前系统时间N天前的日志文件将被删除，N的值：timediatance）
	 */
	private  void deleteFiles(String pathName,String timepoint,String timediatance)
	{
		String tempDate="";//用于存放timediatance天前的日期
		if(pathName!=null&&pathName.trim().length()>0)
		{
			//分离出对应的目录和文件名(F:\\111\\222\\333\\DEBUG\\QDII.log)
			String dirName=pathName.substring(0, pathName.lastIndexOf(File.separator));			
			File dirNameFile=new File(dirName);
			if(dirNameFile.exists()&&dirNameFile.isDirectory())
			{
				if(timediatance!=null&&timediatance.trim().length()>0)			
				{
					tempDate=getPrefixDate(Integer.parseInt(timediatance));
				}
				File[] files=dirNameFile.listFiles();
				for(int i=0;i<files.length;i++)
				{
					String filename=files[i].getName();				
					//取得文件名中包含的日期
					String strDate=filename.substring(filename.lastIndexOf(".")+1, filename.length());
					strDate=strDate.replaceAll("-", "");
					if(timepoint!=null&&timepoint.trim().length()>0)	
					{
						timepoint=timepoint.replaceAll("-", "");
						if(strDate.compareTo(timepoint)<0)
						{
							files[i].delete();
							continue;
						}
					}
					if(tempDate!=null&&tempDate.trim().length()>0)
						if(strDate.compareTo(tempDate)<=0)
						{
							files[i].delete();
							continue;
						}		
				} 
			}
		}
	}
	/**
	 * 功能：获取当前时间N天前的一个日期，返回格式为：yyyyMMdd
	 * @param days  天数
	 * @return
	 */
	private String getPrefixDate(int days)
	{
		String res="";
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE));
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date dt =calendar.getTime();
		//20120619 modified by liubo.Bug #4753
		//以毫秒数来推算出起始删除日期不是很准确，原因不明。在这里改成以实际天数来推算
		//==================================
//		long args=dt.getTime()-days*24*60*60*1000;	
		Date newDate = YssFun.addDay(dt, -days);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//		res=format.format(new Date(args));
		res=format.format(newDate);
		//===============end===================
		return res;
	}
}
