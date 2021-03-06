package com.yss.ciss.ws.service.serviceIf;


import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.yss.ciss.ws.service.*;
import com.yss.util.YssException;

@WebService(name = "WSforYssCISS", targetNamespace = "http://www.ysstech.com/QDII/CISSWSService")
@SOAPBinding(style = SOAPBinding.Style.RPC,use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)

//20120416 added by liubo.Story #2439
//响应托管系统请求的webservice的接口
public interface IWSforYssCISS 
{
	/**
	 * modify huangqirong 2012-12-27
	 * 解决此WebService在Weblogic10.x下部署不支持报错 去掉 throws异常 
	 * */
	public ResponseMsg sendComparedResult(ComparedResultVO resultVO);//处理对账结果请求  
	public ResponseMsg sendLockStatus(LockStatusVO resultVO);//处理平台锁定\解锁
	//---end---
}
