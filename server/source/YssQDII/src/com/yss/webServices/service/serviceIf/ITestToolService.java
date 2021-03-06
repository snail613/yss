package com.yss.webServices.service.serviceIf;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
/**
 * WebService服务端口的定义，提供对测试WebService数据的处理     
 * @author huangqirong 2012.11.01 story #3227
 *
 */
@WebService(name = "TestToolService", targetNamespace = "http://www.ysstech.com/QDII/ServiceTestTool")
@SOAPBinding(style = SOAPBinding.Style.RPC,use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ITestToolService{
	
	/**
	 * 公布给客户机的的方法
	 * */
	@WebMethod 
	public String dealTestDataMsg(String request, String tgws,String tgwsmethod ,String filepath ,String isClient);	
	
}
