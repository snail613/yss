package com.yss.main.syssetting;

/**
 * add by  yeshenghong story2446 20120410
 * <p>Title: HttpsConfigBean</p>
 * <p>Description: 系统连接设置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ysstech</p>
 * @author not attributable
 * @version 1.0
 */
public class HttpsConfigBean {
    
	private String httpURL;
	
	public String getHttpURL() {
		return httpURL;
	}
	public void setHttpURL(String httpURL) {
		this.httpURL = httpURL;
	}
	public String getHttpsURL() {
		return httpsURL;
	}
	public void setHttpsURL(String httpsURL) {
		this.httpsURL = httpsURL;
	}
	public String getEncrypt() {
		return encrypt;
	}
	public void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}
	private String httpsURL;
	private String encrypt;

}
