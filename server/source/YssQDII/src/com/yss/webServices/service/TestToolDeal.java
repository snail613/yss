package com.yss.webServices.service;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.yss.webServices.client.gcs.StringArray;
import com.yss.webServices.client.gcs.StringArrayArray;
import com.yss.webServices.client.gcs.SwiftMessageService;
import com.yss.webServices.client.gcs.SwiftMessageService_Service;

/**
 * WebService服务端口的定义，提供对测试WebService数据的处理     
 * @author huangqirong 2012.11.01 Story #3227
 *
 */
public class TestToolDeal {
	
	public TestToolDeal() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 处理xml
	 * */
	public String dealTestDataMsg(String request, String tgws,String tgwsmethod, String filepath , String isClient){
		String result = "";
		
		try {
			if("swiftWS".equalsIgnoreCase(tgws)){
				SwiftMessageService_Service swiftService = new SwiftMessageService_Service();
				SwiftMessageService swift = swiftService.getSwiftServicePort();
				if("getSwiftFeeMsg".equalsIgnoreCase(tgwsmethod)){				
					String asXml = "";
					if("false".equalsIgnoreCase(isClient)){
						try {
							asXml = this.getSunJaxwsXml(filepath);
						} catch (Exception e) {
							result = result.trim().length() ==0
								? "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时读取xml数据文件出错：" + e.getMessage() 
								: result + "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时读取xml数据文件出错：" + e.getMessage();
							System.out.println(result.trim().length() ==0
								? "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时读取xml数据文件出错：" + e.getMessage() 
								: result + "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时读取xml数据文件出错：" + e.getMessage());
						}
					}else{
						asXml = request;
					}
					
					try {
						swift.getSwiftFeeMsg(this.getStringArray(asXml));
					} catch (Exception e) {
						result = result.trim().length() ==0
							? "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时数据处理出错：" + e.getMessage() 
							: result + "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时数据处理出错：" + e.getMessage();
						System.out.println(result.trim().length() ==0
							? "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时数据处理出错：" + e.getMessage() 
							: result + "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时数据处理出错：" + e.getMessage());
					}
					
				}else if("setSwiftMsg".equalsIgnoreCase(tgwsmethod)){
					String asXml = "";
					if("false".equalsIgnoreCase(isClient)){
						asXml = this.getSunJaxwsXml(filepath);
						try {
							
						} catch (Exception e) {
							result = result.trim().length() ==0
								? "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时读取xml数据文件出错：" + e.getMessage() 
								: result + "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时读取xml数据文件出错：" + e.getMessage();
							System.out.println(result.trim().length() ==0
								? "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时读取xml数据文件出错：" + e.getMessage() 
								: result + "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时读取xml数据文件出错：" + e.getMessage());
						}
						
					}else{
						asXml = request;
					}
					swift.setSwiftMsg(asXml);
				}
			}
		} catch (Exception e) {
			result = result.trim().length() ==0
							? "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时出错：" + e.getMessage() 
							: result + "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时出错：" + e.getMessage();
			System.out.println(result.trim().length() ==0
							? "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时出错：" + e.getMessage() 
							: result + "处理WebService：" + tgws + "的接口方法：" + tgwsmethod  + "时出错：" + e.getMessage());
		}
		return result;
	}
	
	
	/**
	 * 从xml中获取值封装成数组
	 * */
	private StringArrayArray getStringArray(String asXml){
		
		Element root = null;
		//String [][] param = null;
		Element data = null;
		
		StringArrayArray saat = new StringArrayArray();
		
		try {
			Document doc = DocumentHelper.parseText(asXml);
			root = doc.getRootElement();
			List<Element> list = root.elements("datainfo");
			//param = new String [list.size()][];
			for (int i = 0; i < list.size(); i++) {
				data = list.get(i);	
				StringArray sat = new StringArray();
				sat.getItem().add(data.elementText("FeedCondition"));
				sat.getItem().add(data.elementText("FeedValue"));				
				saat.getItem().add(sat);
			}
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return saat;
	}
	
	/**
	 * 获取xml 对象字符串
	 * */
	private String getSunJaxwsXml(String xmlFilePath){	
		SAXReader reader = new SAXReader();
        Document document = null;
        String docXmlText = "";
		try {
			File file = new File(xmlFilePath);
			if(file.exists()){
				try {
					document = reader.read(file);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}			
				docXmlText=document.asXML();
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
			System.out.println("读取xml出错！");
		}
		return docXmlText;
	}
}
