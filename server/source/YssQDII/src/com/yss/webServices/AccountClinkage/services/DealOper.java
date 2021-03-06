/**   
* @Title: 
* @Package com.yss.webServices.AccountClinkage.services
* @Description: TODO( ) 
* @author
* @date 2013-5-9 下午03:45:18 
* @version V4.0   
*/
package com.yss.webServices.AccountClinkage.services;

import java.io.File;

import javax.jws.WebMethod;
import javax.jws.WebService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.yss.webServices.AccountClinkage.AbsService;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.IService;

/** 
 * @ClassName:  
 * @Description: TODO(  ) 
 * @author KR 
 * @date 2013-5-9 下午03:45:18 
 *  add by huangqirong 2013-05-09 story #3871 需求北京-[建设银行]QDII系统[高]20130419001
 */
@WebService(serviceName = "AccountClinkageService",endpointInterface = "com.yss.webServices.AccountClinkage.IService",
		targetNamespace = "http://www.ysstech.com/QDII/AccountClinkageService",portName = "AccountClinkageServicePort")
public class DealOper implements IService  {
	
	/**
	 * 主入口	 
	 * */
	@WebMethod
	public String doDeal(String datas){ //这里的参数有待改进可能用数组
		ApplicationContext ctx = null;  //应用程序上下文
		Document document = null; 		//Xml文档
		Object obj = null ;				//Object临时对象
		AbsService service = null;		//抽象的service
		String txtcode = "";			//交易码	
		String strOsFile = "";			//路径
		String result = "";				//返回结果

		document = Console.parseXml(datas); //转换成Xml文件
		
		if(document != null){
			Element ele = document.getRootElement().element("head").element("txcode");			
			if(ele != null){
				txtcode = ele.getText();
				try {
	         		if(System.getProperty("os.name").toLowerCase().indexOf("linux") > -1){
	         			strOsFile = "file:";
	         		}
	         		String webRoot = this.getClass().getClassLoader().getResource("/").toString();	//应用程序上下文路径
	         		if(webRoot.indexOf("WEB-INF") > -1){
						webRoot = webRoot.substring(0, webRoot.indexOf("WEB-INF"));
						
						if(webRoot.indexOf("/") > -1 ){
							if(webRoot.endsWith("/"))
						        ctx = new FileSystemXmlApplicationContext(strOsFile + webRoot + "cfg/webserviceBean.xml");
							else
								ctx = new FileSystemXmlApplicationContext(strOsFile + webRoot + "/cfg/webserviceBean.xml");
						} else{
							if(webRoot.endsWith(File.separator)){
						        ctx = new FileSystemXmlApplicationContext(strOsFile + webRoot +
						        		"cfg" + File.separator + "webserviceBean.xml");
						    }else{
								ctx = new FileSystemXmlApplicationContext(strOsFile + webRoot + 
										File.separator + "cfg"+ File.separator + "webserviceBean.xml");
						    }
						}
					}
				} catch (Exception e) {
					System.out.println("获取系统路径：/cfg/webserviceBean.xml 下的Bean文件出错 ：" + e.getMessage());
				}
				if(ctx != null){
					try {
						obj = ctx.getBean(txtcode);
						if(obj instanceof AbsService){			//判断对象
							service = (AbsService ) obj ;							
										//设置Pub
							service.setTxcode(txtcode); 		//设置应答码
							result = service.doDealOper(document.asXML()); //调用处理数据  需要生成客户端进行调用b
						}
					} catch (Exception e) {
						System.out.println("处理WebService出错：" + e.getMessage());
					}
				}
			}
		}
		return result;
	}	
}
