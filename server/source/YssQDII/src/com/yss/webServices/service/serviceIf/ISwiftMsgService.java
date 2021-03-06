package com.yss.webServices.service.serviceIf;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * WebService服务端口的定义，提供对swift报文数据的处理     
 * @author yh 2011.05.16 QDV411建行2011年04月19日01_A
 *
 */
@WebService(name = "SwiftMessageService", targetNamespace = "http://www.ysstech.com/QDII/SwiftService")
@SOAPBinding(style = SOAPBinding.Style.RPC,use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ISwiftMsgService {
	
	/**
	 * 服务提供的操作，接受swift报文数据，参数和返回值都为xml格式的字符串
	 * @param swiftMsg swift报文
	 * @return 处理结果
	 */
	@WebMethod
	public String setSwiftMsg(String swiftMsg);
	
	/**
	 * add by huangqirong 2012-04-12 story #2326
	 * 
	 * 公布给客户机的 获取费用的方法
	 * 
	 * */
	@WebMethod
	public String[][] getSwiftFeeMsg(String [][] dParameter);
	
}
