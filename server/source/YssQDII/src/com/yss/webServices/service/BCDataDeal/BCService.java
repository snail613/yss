/**   
* @Title: IService.java 
* @Package com.yss.webServices.AccountClinkage 
* @Description: TODO( ) 
* @author KR
* @date 2013-5-9 下午03:09:14 
* @version V4.0   
*/
package com.yss.webServices.service.BCDataDeal;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


@WebService(name = "BCData", targetNamespace = "http://www.ysstech.com/QDII/BCData")
@SOAPBinding(style = SOAPBinding.Style.RPC,use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface BCService {	
	
	/**
	 * 是请求还是响应处理
	 * */
	@WebMethod
	public String doDeal(String datas);	
	
}
