package com.yss.ciss.ws.client.ciss;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the com.yss.ciss.ws.client package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _SendComparableStatus_QNAME = new QName(
			"http://qdii.services.ws.ciss.yss.com/", "sendComparableStatus");
	private final static QName _SendComparableStatusResponse_QNAME = new QName(
			"http://qdii.services.ws.ciss.yss.com/",
			"sendComparableStatusResponse");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: com.yss.ciss.ws.client
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link SendComparableStatusResponse }
	 * 
	 */
	public SendComparableStatusResponse createSendComparableStatusResponse() {
		return new SendComparableStatusResponse();
	}

	/**
	 * Create an instance of {@link SendComparableStatus }
	 * 
	 */
	public SendComparableStatus createSendComparableStatus() {
		return new SendComparableStatus();
	}

	/**
	 * Create an instance of {@link ResponseMsg }
	 * 
	 */
	public ResponseMsg createResponseMsg() {
		return new ResponseMsg();
	}

	/**
	 * Create an instance of {@link ComparableStatusVO }
	 * 
	 */
	public ComparableStatusVO createComparableStatusVO() {
		return new ComparableStatusVO();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link SendComparableStatus }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://qdii.services.ws.ciss.yss.com/", name = "sendComparableStatus")
	public JAXBElement<SendComparableStatus> createSendComparableStatus(
			SendComparableStatus value) {
		return new JAXBElement<SendComparableStatus>(
				_SendComparableStatus_QNAME, SendComparableStatus.class, null,
				value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link SendComparableStatusResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://qdii.services.ws.ciss.yss.com/", name = "sendComparableStatusResponse")
	public JAXBElement<SendComparableStatusResponse> createSendComparableStatusResponse(
			SendComparableStatusResponse value) {
		return new JAXBElement<SendComparableStatusResponse>(
				_SendComparableStatusResponse_QNAME,
				SendComparableStatusResponse.class, null, value);
	}

}
