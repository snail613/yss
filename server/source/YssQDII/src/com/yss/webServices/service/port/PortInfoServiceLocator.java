/**
 * PortInfoServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.yss.webServices.service.port;

public class PortInfoServiceLocator extends org.apache.axis.client.Service implements com.yss.webServices.service.port.PortInfoService {

    public PortInfoServiceLocator() {
    }


    public PortInfoServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PortInfoServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PortInfo
    private java.lang.String PortInfo_address = "http://localhost:8096/Second/services/PortInfo";

    public java.lang.String getPortInfoAddress() {
        return PortInfo_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PortInfoWSDDServiceName = "PortInfo";

    public java.lang.String getPortInfoWSDDServiceName() {
        return PortInfoWSDDServiceName;
    }

    public void setPortInfoWSDDServiceName(java.lang.String name) {
        PortInfoWSDDServiceName = name;
    }

    public com.yss.webServices.service.port.PortInfo_PortType getPortInfo() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PortInfo_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPortInfo(endpoint);
    }

    public com.yss.webServices.service.port.PortInfo_PortType getPortInfo(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.yss.webServices.service.port.PortInfoSoapBindingStub _stub = new com.yss.webServices.service.port.PortInfoSoapBindingStub(portAddress, this);
            _stub.setPortName(getPortInfoWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPortInfoEndpointAddress(java.lang.String address) {
        PortInfo_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.yss.webServices.service.port.PortInfo_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.yss.webServices.service.port.PortInfoSoapBindingStub _stub = new com.yss.webServices.service.port.PortInfoSoapBindingStub(new java.net.URL(PortInfo_address), this);
                _stub.setPortName(getPortInfoWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("PortInfo".equals(inputPortName)) {
            return getPortInfo();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://com.yss.service", "PortInfoService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://com.yss.service", "PortInfo"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PortInfo".equals(portName)) {
            setPortInfoEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
