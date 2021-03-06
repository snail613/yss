/**
 * ProducePluginServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.yss.webServices.service.plugin;

public class ProducePluginServiceLocator extends org.apache.axis.client.Service implements com.yss.webServices.service.plugin.ProducePluginService {

    public ProducePluginServiceLocator() {
    }


    public ProducePluginServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ProducePluginServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ProducePlugin
    private java.lang.String ProducePlugin_address = "http://localhost:8096/Second/services/ProducePlugin";

    public java.lang.String getProducePluginAddress() {
        return ProducePlugin_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ProducePluginWSDDServiceName = "ProducePlugin";

    public java.lang.String getProducePluginWSDDServiceName() {
        return ProducePluginWSDDServiceName;
    }

    public void setProducePluginWSDDServiceName(java.lang.String name) {
        ProducePluginWSDDServiceName = name;
    }

    public com.yss.webServices.service.plugin.ProducePlugin_PortType getProducePlugin() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ProducePlugin_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getProducePlugin(endpoint);
    }

    public com.yss.webServices.service.plugin.ProducePlugin_PortType getProducePlugin(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.yss.webServices.service.plugin.ProducePluginSoapBindingStub _stub = new com.yss.webServices.service.plugin.ProducePluginSoapBindingStub(portAddress, this);
            _stub.setPortName(getProducePluginWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setProducePluginEndpointAddress(java.lang.String address) {
        ProducePlugin_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.yss.webServices.service.plugin.ProducePlugin_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.yss.webServices.service.plugin.ProducePluginSoapBindingStub _stub = new com.yss.webServices.service.plugin.ProducePluginSoapBindingStub(new java.net.URL(ProducePlugin_address), this);
                _stub.setPortName(getProducePluginWSDDServiceName());
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
        if ("ProducePlugin".equals(inputPortName)) {
            return getProducePlugin();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.yss.plugin", "ProducePluginService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.yss.plugin", "ProducePlugin"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ProducePlugin".equals(portName)) {
            setProducePluginEndpointAddress(address);
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
