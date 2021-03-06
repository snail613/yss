package com.yss.ciss.ws.service.client;


import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.yss.ciss.ws.service.*;
import com.yss.util.YssException;

@WebService(name = "WSforYssCISS", targetNamespace = "http://www.ysstech.com/QDII/CISSWSService")
@SOAPBinding(style = SOAPBinding.Style.RPC,use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)

public interface IWSforYssCISS 
{
	public ResponseMsg sendComparedResult(ComparedResultVO resultVO) throws YssException;
	public ResponseMsg sendLockStatus(LockStatusVO resultVO) throws YssException;

}
