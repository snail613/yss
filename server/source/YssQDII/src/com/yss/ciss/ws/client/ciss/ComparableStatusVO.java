package com.yss.ciss.ws.client.ciss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for comparableStatusVO complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="comparableStatusVO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="execName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "comparableStatusVO", propOrder = { "execName", "msgDate",
		"msgType", "proCode" })
public class ComparableStatusVO {

	protected String execName;
	protected String msgDate;
	protected String msgType;
	protected String proCode;

	/**
	 * Gets the value of the execName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExecName() {
		return execName;
	}

	/**
	 * Sets the value of the execName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExecName(String value) {
		this.execName = value;
	}

	/**
	 * Gets the value of the msgDate property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMsgDate() {
		return msgDate;
	}

	/**
	 * Sets the value of the msgDate property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMsgDate(String value) {
		this.msgDate = value;
	}

	/**
	 * Gets the value of the msgType property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMsgType() {
		return msgType;
	}

	/**
	 * Sets the value of the msgType property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMsgType(String value) {
		this.msgType = value;
	}

	/**
	 * Gets the value of the proCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProCode() {
		return proCode;
	}

	/**
	 * Sets the value of the proCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProCode(String value) {
		this.proCode = value;
	}

}
