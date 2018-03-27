package com.yss.webServices.service.BCDataDeal;

import java.io.File;
import java.io.FileInputStream;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.IService;

@WebService(serviceName = "BCData",endpointInterface = "com.yss.webServices.service.BCDataDeal.BCService",
		targetNamespace = "http://www.ysstech.com/YssQDII/BCDataService",portName = "BCDataPort")
public class BCDataDeal implements BCService {
		@WebMethod
		public String doDeal(String datas) {
			BCDataResponse zh = new BCDataResponse();
			String result= zh.WebServiceInterface(datas);
			return result;
			 
		}

}
