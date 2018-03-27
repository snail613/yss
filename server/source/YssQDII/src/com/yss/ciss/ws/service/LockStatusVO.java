package com.yss.ciss.ws.service;

//20120416 added by liubo.Story #2439
//托管系统平台锁定\接触锁定请求信息的实体类

public class LockStatusVO {

	private String execName = "";		//对账人
	private String status = "";			//锁定状态
	private String proCode = "";		//资产代码
	private String startDate = "";		//数据日期
	private String sysName  = "";		//系统名称
	private String system = "";			//收件人
	private String msgType = "";		//消息类型
	
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getExecName() {
		return execName;
	}
	public void setExecName(String execName) {
		this.execName = execName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProCode() {
		return proCode;
	}
	public void setProCode(String proCode) {
		this.proCode = proCode;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getSysName() {
		return sysName;
	}
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	
	
	
	

}
