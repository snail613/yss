package com.yss.webServices.client.swiftClientAuto;

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

	private final static QName _NetworkServices_QNAME = new QName(
			"http://services.ws.ciss.yss.com/", "NetworkServices");
	private final static QName _NetworkServicesResponse_QNAME = new QName(
			"http://services.ws.ciss.yss.com/", "NetworkServicesResponse");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: com.yss.ciss.ws.client
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link NetworkServicesResponse }
	 * 
	 */
	public NetworkServicesResponse createNetworkServicesResponse() {
		return new NetworkServicesResponse();
	}

	/**
	 * Create an instance of {@link NetworkServices }
	 * 
	 */
	public NetworkServices createNetworkServices() {
		return new NetworkServices();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link NetworkServices }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "http://services.ws.ciss.yss.com/", name = "NetworkServices")
	public JAXBElement<NetworkServices> createNetworkServices(
			NetworkServices value) {
		return new JAXBElement<NetworkServices>(_NetworkServices_QNAME,
				NetworkServices.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link NetworkServicesResponse }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "http://services.ws.ciss.yss.com/", name = "NetworkServicesResponse")
	public JAXBElement<NetworkServicesResponse> createNetworkServicesResponse(
			NetworkServicesResponse value) {
		return new JAXBElement<NetworkServicesResponse>(
				_NetworkServicesResponse_QNAME, NetworkServicesResponse.class,
				null, value);
	}

}
