package com.yss.ciss.ws.service.client;

public class ComparedResultVO {
	private String url = "";			//托管系统显示明细访问地址
	private String execName = "";		//对账人
	private String startDate = "";		//对账起始时间
	private String proCode = "";		//资产代码
	private String proName = "";		//资产名称
	private String result = "";			//对账结果
	private String msgId = "";			//报文主键
	private String proType = "";		//资产类型
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
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getProCode() {
		return proCode;
	}
	public void setProCode(String proCode) {
		this.proCode = proCode;
	}
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getProType() {
		return proType;
	}
	public void setProType(String proType) {
		this.proType = proType;
	}

}
