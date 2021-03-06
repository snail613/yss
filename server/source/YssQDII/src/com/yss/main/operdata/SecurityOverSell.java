package com.yss.main.operdata;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataInterface;
import com.yss.util.YssException;

/**
 * @author zhangjun ,2011-11-11  STORY #1433 证券卖空业务处理
 */
public class SecurityOverSell extends BaseDataSettingBean {
	private String fNum = "";//交易编号
	private String FPortCode= "";         //组合代码
	private java.util.Date FLendDate = null; //借贷日期
	private String FBrokerCode = ""; //券商代码
	private String FSecurityCode = "";//证券代码
	private String FSecurityState = "";//证券状态
	private double FAmount ; //证券数量
	private double FLendRatio;//借贷利率
	private double FBailMoney;//保证金
	private String curyCode = "";
	
	public String getCuryCode() {
		return curyCode;
	}
	public void setCuryCode(String curyCode) {
		this.curyCode = curyCode;
	}
	public String getfNum() {
		return fNum;
	}
	public double getFLendRatio() {
		return FLendRatio;
	}
	
	public double getFBailMoney() {
		return FBailMoney;
	}
	public void setFBailMoney(double fBailMoney) {
		FBailMoney = fBailMoney;
	}
	public void setFLendRatio(double fLendRatio) {
		FLendRatio = fLendRatio;
	}
	public void setfNum(String fNum) {
		this.fNum = fNum;
	}
	public String getFPortCode() {
		return FPortCode;
	}
	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}
	public java.util.Date getFLendDate() {
		return FLendDate;
	}
	public void setFLendDate(java.util.Date fLendDate) {
		FLendDate = fLendDate;
	}
	public String getFBrokerCode() {
		return FBrokerCode;
	}
	public void setFBrokerCode(String fBrokerCode) {
		FBrokerCode = fBrokerCode;
	}
	public String getFSecurityCode() {
		return FSecurityCode;
	}
	public void setFSecurityCode(String fSecurityCode) {
		FSecurityCode = fSecurityCode;
	}
	public String getFSecurityState() {
		return FSecurityState;
	}
	public void setFSecurityState(String fSecurityState) {
		FSecurityState = fSecurityState;
	}
	public double getFAmount() {
		return FAmount;
	}
	public void setFAmount(double fAmount) {
		FAmount = fAmount;
	}
	
	public SecurityOverSell() {
		
	}
	
	
}
