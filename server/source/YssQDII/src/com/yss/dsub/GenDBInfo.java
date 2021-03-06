package com.yss.dsub;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.jasypt.util.text.BasicTextEncryptor;

import com.yss.util.YssException;

public final class GenDBInfo     
	implements HttpSessionBindingListener {
	
	private String strDriver = "";//连接驱动
	private String strURL = "";//数据库地址
	private String strUser = "";//用户名
	private String strPwd = "";//密码
	private String strDBType = "";//数据库类型
	private String strID = "";//连接标识
	private String strOldID = "";
	
	private File dbFile = null;//数据库配置文件

	public File getDbFile() {
		return dbFile;
	}

	public void valueBound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub	
	}

	public void valueUnbound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
	}
	public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        try {
            if (sRowStr.equals("")) {
                return;
            }
            reqAry = sRowStr.split("\t");
            this.strID = reqAry[0];
            if(this.strID.length() == 0){
            	this.strID = "[db_yssimsas]";
            }
            this.strDBType = reqAry[1];
            if(this.strDBType.equalsIgnoreCase("ora")){
                this.strDriver = "oracle.jdbc.driver.OracleDriver";
                this.strURL = "jdbc:oracle:thin:@" + reqAry[2];
            }
            this.strUser = reqAry[3];
            this.strPwd = reqAry[4];
            this.strOldID = reqAry[5];
        } catch (Exception e) {
            throw new YssException("解析数据库连接信息出错", e);
        }
	}
	
	public String getStrID() {
		return strID;
	}

	public void setStrID(String strID) {
		this.strID = strID;
	}

	/*
	 * 新建数据库连接配置
	 */
	public void addDBInfo() throws YssException {
		try{			
			dbFile = new File(File.separator + "dbsetting.properties");
			if(!dbFile.exists()){
				dbFile.createNewFile();
			}
			writeProperties(dbFile.getPath(), strID + "&&ConnID", strID);
			writeProperties(dbFile.getPath(), strID + "&&driver", strDriver);
			writeProperties(dbFile.getPath(), strID + "&&url", strURL);
			//20120315 modified by liubo.Bug #3993
            //使用密文加密dbsetting时，加密密码的同时也要加密用户名
			//===================================
//			writeProperties(dbFile.getPath(), strID + "&&user", strUser);
			String sUserNameEncypt = encrypt("yssusername", strUser);
			writeProperties(dbFile.getPath(), strID + "&&user", sUserNameEncypt);
			//===============end====================
			String strtemp = encrypt("ysspassword", strPwd);
			writeProperties(dbFile.getPath(), strID + "&&password", strtemp);
			writeProperties(dbFile.getPath(), strID + "&&dbtype", strDBType);
			
		}catch(Exception e){
			throw new YssException("保存数据库连接信息出错！", e);
		}
	}
	
	/**
	 * 修改数据库连接配置
	 * 采用先删后增
	 * @throws YssException
	 */
	public void editDBInfo() throws YssException {
		Properties props = new Properties();
		try{
			File dbFile = new File(File.separator + "dbsetting.properties");
			InputStream in = new BufferedInputStream(new FileInputStream(dbFile));
			props.load(in);
			props.remove(strOldID + "&&ConnID");
			props.remove(strOldID + "&&driver");
			props.remove(strOldID + "&&url");
			props.remove(strOldID + "&&user");
			props.remove(strOldID + "&&password");
			props.remove(strOldID + "&&dbtype");
			OutputStream fos = new FileOutputStream(dbFile);    
			props.store(fos, null);
			this.addDBInfo();
		}catch(Exception e){
			throw new YssException("修改数据库配置信息出错！", e);
		}
	}
	
	/**
	 * 删除一条数据库连接配置
	 * @throws YssException
	 */
	public void delDBInfo() throws YssException {
		Properties props = new Properties();
		try{
			File dbFile = new File(File.separator + "dbsetting.properties");
			InputStream in = new BufferedInputStream(new FileInputStream(dbFile));
			props.load(in);
			props.remove(strID + "&&ConnID");
			props.remove(strID + "&&driver");
			props.remove(strID + "&&url");
			props.remove(strID + "&&user");
			props.remove(strID + "&&password");
			props.remove(strID + "&&dbtype");
			OutputStream fos = new FileOutputStream(dbFile);    
			props.store(fos, null);
		}catch(Exception e){
			throw new YssException("删除数据库配置信息出错！", e);
		}
	}
	
	/*
	 * 获取数据库配置信息
	 */
	public String getDBInfo() throws YssException {
		String strBack = "";
		Properties props = new Properties();
		StringBuffer buff = new StringBuffer();
		try{
            File dbFile = new File(File.separator + "dbsetting.properties");
            if(!dbFile.exists()){
            	return "";
            	//throw new YssException("读取数据库配置文件出错！请先配置数据库连接！");
            }
			InputStream in = new BufferedInputStream(new FileInputStream(
					dbFile));
			props.load(in);
			Set keySet = props.keySet();
			Iterator ite = keySet.iterator();
			while(ite.hasNext()){
				String strkey = (String)ite.next();
				if(strkey.indexOf("&&ConnID") >= 0){
					strkey = strkey.split("&&ConnID")[0];
					this.strID = props.getProperty(strkey + "&&ConnID");
		            this.strDBType = props.getProperty(strkey + "&&dbtype");
		            this.strURL = props.getProperty(strkey + "&&url");
	    			//20120315 modified by liubo.Bug #3993
	                //使用密文加密dbsetting时，加密密码的同时也要加密用户名
	    			//===================================
//		            this.strUser = props.getProperty(strkey + "&&user");
		            this.strUser = this.decrypt("yssusername", props.getProperty(strkey + "&&user"));
	    			//=================end==================
		            this.strPwd = this.decrypt("ysspassword", props.getProperty(strkey + "&&password"));
		            if(this.strDBType.equalsIgnoreCase("ora")){
		            	this.strDBType = "oracle";
		            }
		            buff.append(this.strID).append("\t").append(this.strDBType).append("\t").append(this.strURL.split("@")[1]).append("\t")
		            	.append(this.strUser).append("\t").append(this.strPwd).append("\t").append("\f\f");
				}
			}
			if(buff.length() > 2){
				strBack = buff.substring(0,buff.length() - 2);
			}
		}catch(Exception e){
			throw new YssException("获取数据库配置信息出错！", e);
		}
		return strBack;
	}
	
	// 写入properties信息
	public void writeProperties(String filePath, String parameterName,
			String parameterValue) {
		Properties prop = new Properties();
		try {
			InputStream fis = new FileInputStream(filePath);
			// 从输入流中读取属性列表（键和元素对）
			prop.load(fis);
			// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
			OutputStream fos = new FileOutputStream(filePath);
			prop.setProperty(parameterName, parameterValue);
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			prop.store(fos, "Update value");
		} catch (IOException e) {
			System.err.println("Visit " + filePath + " for updating "
					+ parameterName + " value error");
		}
	}
	
	public String readValue(String filePath, String key) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					filePath));
			props.load(in);
			String value = props.getProperty(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String encrypt(String strKey,String strSRC){
        //加密    
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();    
        textEncryptor.setPassword(strKey);   
        String strNew = textEncryptor.encrypt(strSRC);   
        return strNew;
	}
	
	public String decrypt(String strKey,String strNew){
//      解密    
        BasicTextEncryptor textEncryptor2 = new BasicTextEncryptor();    
        textEncryptor2.setPassword(strKey);    
        String strSRC = textEncryptor2.decrypt(strNew);      
        return strSRC;
	}
}
