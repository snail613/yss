package com.yss.webServices.client.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

/**
 * @author huangqirong 2012.11.01 Story #3227 GCS���Թ��߿ͻ���
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * <p>
 * An example of how this class may be used:
 * 
 * <pre>
 * TestToolService service = new TestToolService();
 * TestToolService portType = service.getTestToolPort();
 * portType.dealTestDataMsg(...);
 * </pre>
 * 
 * </p>
 * 
 */
@WebServiceClient(name = "TestToolService", targetNamespace = "http://www.ysstech.com/QDII/ServiceTestTool", wsdlLocation = "http://localhost:8083/YssQDII/ServiceTestTool?wsdl")
public class TestToolService_Service extends Service {

	private final static URL TESTTOOLSERVICE_WSDL_LOCATION;
	private final static Logger logger = Logger
			.getLogger(com.yss.webServices.client.test.TestToolService_Service.class
					.getName());

	static {
		URL url = null;
		try {
			URL baseUrl;
			baseUrl = com.yss.webServices.client.test.TestToolService_Service.class
					.getResource(".");
			url = new URL(baseUrl,
					"http://localhost:8083/YssQDII/ServiceTestTool?wsdl");
		} catch (MalformedURLException e) {
			logger
					.warning("Failed to create URL for the wsdl Location: 'http://localhost:8083/YssQDII/ServiceTestTool?wsdl', retrying as a local file");
			logger.warning(e.getMessage());
		}
		TESTTOOLSERVICE_WSDL_LOCATION = url;
	}

	public TestToolService_Service(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public TestToolService_Service() {
		super(TESTTOOLSERVICE_WSDL_LOCATION, new QName(
				"http://www.ysstech.com/QDII/ServiceTestTool",
				"TestToolService"));
	}

	/**
	 * 
	 * @return returns TestToolService
	 */
	@WebEndpoint(name = "TestToolPort")
	public TestToolService getTestToolPort() {
		return super.getPort(new QName(
				"http://www.ysstech.com/QDII/ServiceTestTool", "TestToolPort"),
				TestToolService.class);
	}

}
