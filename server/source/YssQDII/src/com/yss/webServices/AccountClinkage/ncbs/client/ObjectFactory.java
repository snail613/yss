package com.yss.webServices.AccountClinkage.ncbs.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the
 * com.yss.webServices.AccountClinkage.ncbs.client package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * add by huangqirong 2013-05-09 story #3871 需求北京-[建设银行]QDII系统[高]20130419001
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _ExcuteNCBSWebService_QNAME = new QName(
			"http://inf.webservice.service.ncbs.ccb.com/",
			"excuteNCBSWebService");
	private final static QName _ExcuteNCBSWebServiceResponse_QNAME = new QName(
			"http://inf.webservice.service.ncbs.ccb.com/",
			"excuteNCBSWebServiceResponse");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package:
	 * com.yss.webServices.AccountClinkage.ncbs.client
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link ExcuteNCBSWebService }
	 * 
	 */
	public ExcuteNCBSWebService createExcuteNCBSWebService() {
		return new ExcuteNCBSWebService();
	}

	/**
	 * Create an instance of {@link ExcuteNCBSWebServiceResponse }
	 * 
	 */
	public ExcuteNCBSWebServiceResponse createExcuteNCBSWebServiceResponse() {
		return new ExcuteNCBSWebServiceResponse();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link ExcuteNCBSWebService }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://inf.webservice.service.ncbs.ccb.com/", name = "excuteNCBSWebService")
	public JAXBElement<ExcuteNCBSWebService> createExcuteNCBSWebService(
			ExcuteNCBSWebService value) {
		return new JAXBElement<ExcuteNCBSWebService>(
				_ExcuteNCBSWebService_QNAME, ExcuteNCBSWebService.class, null,
				value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link ExcuteNCBSWebServiceResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://inf.webservice.service.ncbs.ccb.com/", name = "excuteNCBSWebServiceResponse")
	public JAXBElement<ExcuteNCBSWebServiceResponse> createExcuteNCBSWebServiceResponse(
			ExcuteNCBSWebServiceResponse value) {
		return new JAXBElement<ExcuteNCBSWebServiceResponse>(
				_ExcuteNCBSWebServiceResponse_QNAME,
				ExcuteNCBSWebServiceResponse.class, null, value);
	}

}
