package com.yss.ciss.ws.client.ciss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for sendComparableStatusResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="sendComparableStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://qdii.services.ws.ciss.yss.com/}responseMsg" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendComparableStatusResponse", propOrder = { "_return" })
public class SendComparableStatusResponse {

	@XmlElement(name = "return")
	protected ResponseMsg _return;

	/**
	 * Gets the value of the return property.
	 * 
	 * @return possible object is {@link ResponseMsg }
	 * 
	 */
	public ResponseMsg getReturn() {
		return _return;
	}

	/**
	 * Sets the value of the return property.
	 * 
	 * @param value
	 *            allowed object is {@link ResponseMsg }
	 * 
	 */
	public void setReturn(ResponseMsg value) {
		this._return = value;
	}

}
