package com.yss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

//story 2188 by zhouwei  20120113 装载预警bean的容器类             
//QDV4赢时胜(上海开发部)2012年2月3日02_A
public class WarnPluginLoader{
	//add by zhouwei 20120606 story 2188  存放sessionId与公共对象pub
	public static HashMap sessionMap=new HashMap();	
	
	public static HashMap classMap=new HashMap();
	private static ApplicationContext ac=null;
	private static ApplicationContext operdealCtx=null;
	
	public static String webRoot="";
	//获取ApplicationContext
	public static ApplicationContext getApplicationContext(){
		return ac;
	}
	//预警平台调用QD时，获取估值检查项的容器
	public static ApplicationContext getOperdealCtx(){
		return operdealCtx;
	}
	//根据路径管理预警插件类
	public static void setApplicationContext(String  path){
		try{
			String filePath=path + "/cfg/warn-plugin.xml";
			webRoot=path;
			if(ac==null){
         		//20121227 added by liubo.Bug #6545
         		//在Linux系统下，YssUtil.getURLRoot(request)方法将会获取到绝对路径
         		//在这种情况下，调用Spring框架的FileSystemXmlApplicationContext方法， 给他传值时，需要在传入的路径前，加上“file:”前缀，表示传入的是绝对路径
         		//在windows系统下，YssUtil.getURLRoot(request)方法将会获取到是域名下的路径，也就是相对路径，也就不存在上述情况，不能加“file:”前缀
         		//========================================
         		String strOsFile = "";
         		if(System.getProperty("os.name").toLowerCase().indexOf("linux") > -1)
         		{
         			strOsFile = "file:";
         		}   
				//兴业银行BUG 从现场版本合进 yeshenghong 20130425           		
         		if(System.getProperty("os.name").toLowerCase().indexOf("aix") > -1)
         		{
         			System.out.println("Here is "+System.getProperty("os.name").toLowerCase());
         			path = "/"+path;
         		}
				//---end 兴业银行BUG 从现场版本合进 yeshenghong 20130425
         		//====================end====================
				ac=new FileSystemXmlApplicationContext(strOsFile + path + "/cfg/warn-plugin.xml");
			}
			if(operdealCtx==null){
         		//20121227 added by liubo.Bug #6545
         		//在Linux系统下，YssUtil.getURLRoot(request)方法将会获取到绝对路径
         		//在这种情况下，调用Spring框架的FileSystemXmlApplicationContext方法， 给他传值时，需要在传入的路径前，加上“file:”前缀，表示传入的是绝对路径
         		//在windows系统下，YssUtil.getURLRoot(request)方法将会获取到是域名下的路径，也就是相对路径，也就不存在上述情况，不能加“file:”前缀
         		//========================================
         		String strOsFile = "";
         		if(System.getProperty("os.name").toLowerCase().indexOf("linux") > -1)
         		{
         			strOsFile = "file:";
         		}
         		//====================end====================
				operdealCtx=new FileSystemXmlApplicationContext(strOsFile + path + "/cfg/operdeal.xml");
			}
			read(filePath);
			//反射调用来实例化  插件类
			com.yss.core.util.YssCons.classMap=classMap;
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	//读取xml文件中的内容
	public static void read(String filePath) {  
			InputStream in=null;
			File file=null;
	        try {  
	            SAXReader reader = new SAXReader();  
	            file=new File(filePath);
	            in = new FileInputStream(file) ;  
	            Document doc = reader.read(in);  
	            Element root = doc.getRootElement();  
	            readNode(root, "");  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }finally{
	        	try{
	        		in.close();
	        	}catch (Exception e) {
	        		 e.printStackTrace(); 
				}
	        }  
	    }  
	@SuppressWarnings("unchecked")  
    public static void readNode(Element root, String prefix) {  
		try{
	        if (root == null) return;  
	        String key="";
	        String value="";
	        // 获取属性   
	        List<Attribute> attrs = root.attributes();  
	        if (attrs != null && attrs.size() > 0) {   
	            for (Attribute attr : attrs) {  
	            	if(attr.getName().equals("id")){
	            		key=attr.getValue();
	            	}
	            	if(attr.getName().equals("class")){
	            		value=attr.getValue();
	            		classMap.put(key, value);
	            	}
	            }  
	        }  
	        // 获取他的子节点   
	        List<Element> childNodes = root.elements();  
	        for (Element e : childNodes) {  
	            readNode(e, prefix);  
	        }  
		}catch (Exception e) {
			 e.printStackTrace(); 
		}
    }  
}
