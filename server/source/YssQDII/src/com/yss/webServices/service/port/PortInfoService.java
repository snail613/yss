/**
 * PortInfoService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.yss.webServices.service.port;

public interface PortInfoService extends javax.xml.rpc.Service {
    public java.lang.String getPortInfoAddress();

    public com.yss.webServices.service.port.PortInfo_PortType getPortInfo() throws javax.xml.rpc.ServiceException;

    public com.yss.webServices.service.port.PortInfo_PortType getPortInfo(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
