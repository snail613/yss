/**   
* @Title: IService.java 
* @Package com.yss.webServices.AccountClinkage 
* @Description: TODO( ) 
* @author KR
* @date 2013-5-9 下午03:09:14 
* @version V4.0   
*/
package com.yss.webServices.AccountClinkage;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/** 
 * @ClassName: IService 
 * @Description: TODO(  ) 
 * @author KR 
 * @date 2013-5-9 下午03:09:14 
 *  add by huangqirong 2013-05-09 story #3871 需求北京-[建设银行]QDII系统[高]20130419001
 */
@WebService(name = "AccountClinkageService", targetNamespace = "http://www.ysstech.com/QDII/AccountClinkageService")
@SOAPBinding(style = SOAPBinding.Style.RPC,use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface IService {	
	
	/**
	 * 是请求还是响应处理
	 * */
	@WebMethod
	public String doDeal(String datas);	
	
}
