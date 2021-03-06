package com.yss.webServices.service.serviceImp;
import javax.jws.WebMethod;
import javax.jws.WebService;
import com.yss.webServices.service.SwiftMsgDeal;
import com.yss.webServices.service.serviceIf.ISwiftMsgService;

/**
 * webSerive的实现类
 * @author @author yh 2011.05.16 QDV411建行2011年04月19日01_A
 *
 */
@WebService(serviceName = "SwiftMessageService",endpointInterface = "com.yss.webServices.service.serviceIf.ISwiftMsgService",
	targetNamespace = "http://www.ysstech.com/QDII/SwiftServiceImp",portName = "SwiftServicePort")
public class SwiftMsgServiceImp implements ISwiftMsgService {
	
	SwiftMsgDeal swiftMsgDeal = null;
	/**
	 * 公布给客户机的方法。接受客户机发送的swift报文请求，进行处理
	 */
	@WebMethod
	public String setSwiftMsg(String swiftOrgMsg) {	
			String response = null;
			//实例化报文处理对象
			instantiateDealObj(swiftOrgMsg);
			//处理报文信息
			swiftMsgDeal.swiftMsgOper();
			//返回应答报文
			response= swiftMsgDeal.getReplyMsgXml().asXML();
		
		return response;
	}
	
	/**
	 * add by huangqirong 2012-04-12 story #2326
	 * 
	 * 公布给客户机的 获取费用的方法
	 * 
	 * */
	@WebMethod
	public String[][] getSwiftFeeMsg(String [][] dParameter){
		
		instantiateDealObj();		
		dParameter = swiftMsgDeal.swiftFeeMsg(dParameter);		
		return dParameter;
	}
	
	
	/**
	 * add by huangqirong 2012-04-12 story #2326
	 */
	private void instantiateDealObj()
	{
		swiftMsgDeal = new SwiftMsgDeal();
	}
	
	/**
	 * 实例化报文信息的处理对象
	 * @param swiftOrgMsg 原始请求报文
	 */
	private void instantiateDealObj(String swiftOrgMsg)
	{
		swiftMsgDeal = new SwiftMsgDeal(swiftOrgMsg);
	}
	

}
