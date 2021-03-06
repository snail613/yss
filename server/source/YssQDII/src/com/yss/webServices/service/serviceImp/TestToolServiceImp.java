package com.yss.webServices.service.serviceImp;

import javax.jws.WebMethod;
import javax.jws.WebService;
import com.yss.webServices.service.TestToolDeal;
import com.yss.webServices.service.serviceIf.ITestToolService;

/**
 * webSerive的实现类
 * @author huangqirong 2012.11.01 story #3227
 *
 */
@WebService(serviceName = "TestToolService",endpointInterface = "com.yss.webServices.service.serviceIf.ITestToolService",
	targetNamespace = "http://www.ysstech.com/QDII/ServiceTestTool",portName = "TestToolPort")
public class TestToolServiceImp implements ITestToolService {
	
	private TestToolDeal toolDeal = null;
	
	/**
	 * 公布给客户机的方法 处理各种测试数据
	 */
	@WebMethod
	public String dealTestDataMsg(String request, String tgws,String tgwsmethod, String filepath , String isClient) {
			String response = null;
			if(this.toolDeal == null){
				this.toolDeal = new TestToolDeal();
			}
			response = this.toolDeal.dealTestDataMsg(request , tgws , tgwsmethod, filepath ,isClient);			
		return response;
	}
	
	
}
