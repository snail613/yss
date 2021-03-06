package com.yss.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.yss.serve.LogtListener;

/**
 * add by songjie 2013.01.09
 * STORY #2343 QDV4建行2012年3月2日04_A
 * @author 宋洁
 *
 */
public class MonitorExpTask extends TimerTask{
	private Timer timer = null;
	
	public MonitorExpTask(){}
	public MonitorExpTask(Timer timer)
	{
		this.timer=timer;
	}
	
	public Timer getTimer() {
		return timer;
	}
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	/**
	 * 定时器任务：导出监控日志数据
	 */
	public void run() {		
		try {
			createFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成监控导出文件
	 */
	private void createFile() throws Exception{
		// 先判断文件夹是否存在，不存在就创建，再判断文件是否存在，
		//如果存在就删除，再创建新的监控日志文件。
		FileWriter fw = null;
		PrintWriter pw = null;
		String path = "";
		try {
			//edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			path = LogtListener.YSS_MONITOR_EXP_PATH;
			
			Map<String, String> map = System.getenv();
			String computerName = map.get("COMPUTERNAME");// 获取计算机名
			//edit by songjie 2013.03.05 STORY #3658 需求北京-[建行]QDIIV4[高]20130225001
			String fileName = path + "\\ITIS_QDII_" + computerName + "_30.txt";
			
			if (path != null && path.trim().length() > 0) {
				File dirNameFile = new File(path);
				if (!dirNameFile.exists() || (dirNameFile.exists() && !dirNameFile.isDirectory())) {
					dirNameFile.mkdirs();
				}
				File file = new File(fileName);
				if(file.exists() && !file.isDirectory()){
					file.delete();
				}
				
				file.createNewFile();
			
				fw = new FileWriter(fileName);
				pw = new PrintWriter(fw);
				
				MonitorExport me = new MonitorExport();
				me.writeMonitorFile(pw);
				
				pw.flush();
				fw.close();
				pw.close();
			}
		} catch (Exception e) {
			throw new Exception("创建文件出错");
		}
	}	
}
