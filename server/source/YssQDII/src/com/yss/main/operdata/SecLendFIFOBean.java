package com.yss.main.operdata;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataInterface;
import com.yss.util.YssException;

public class SecLendFIFOBean extends BaseDataSettingBean {
	
	private String num = "";
	private String relaNum= "";         
	private java.util.Date FLendDate = null; 
	private java.util.Date FSettleDate = null;
	private double FAmount ;
	
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getRelaNum() {
		return relaNum;
	}
	public void setRelaNum(String relaNum) {
		this.relaNum = relaNum;
	}
	public java.util.Date getFLendDate() {
		return FLendDate;
	}
	public void setFLendDate(java.util.Date fLendDate) {
		FLendDate = fLendDate;
	}
	public java.util.Date getFSettleDate() {
		return FSettleDate;
	}
	public void setFSettleDate(java.util.Date fSettleDate) {
		FSettleDate = fSettleDate;
	}
	public double getFAmount() {
		return FAmount;
	}
	public void setFAmount(double fAmount) {
		FAmount = fAmount;
	} 
}
