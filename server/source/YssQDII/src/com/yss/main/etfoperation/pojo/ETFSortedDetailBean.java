package com.yss.main.etfoperation.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ETFSortedDetailBean {

	private Date bsDate = null; //��������
	private String bs = ""; //�����ʶ��B-�깺��S-���
	private String securityCode = "";//֤ȯ����
	
	private ETFTradeSettleDetailBean parrentDetailBean = null; //����������ϸ
	private List<ETFTradeSettleDetailBean> childDetailBeanList = null; //�Ӽ�������ϸ
	private ETFTradeSettleDetailBean sumChildDetailBean = null; //�����Ӽ�������ϸ
	
	public ETFTradeSettleDetailBean getSumChildDetailBean() {
		return sumChildDetailBean;
	}
	public void setSumChildDetailBean(ETFTradeSettleDetailBean sumChildDetailBean) {
		this.sumChildDetailBean = sumChildDetailBean;
	}
	public Date getBsDate() {
		return bsDate;
	}
	public void setBsDate(Date bsDate) {
		this.bsDate = bsDate;
	}
	public String getBs() {
		return bs;
	}
	public void setBs(String bs) {
		this.bs = bs;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public ETFTradeSettleDetailBean getParrentDetailBean() {
		return parrentDetailBean;
	}
	public void setParrentDetailBean(ETFTradeSettleDetailBean parrentDetailBean) {
		this.parrentDetailBean = parrentDetailBean;
	}
	public List<ETFTradeSettleDetailBean> getChildDetailBeanList() {
		return childDetailBeanList;
	}
	public void setChildDetailBeanList(List<ETFTradeSettleDetailBean> clildDetailBeanList) {
		this.childDetailBeanList = clildDetailBeanList;
	}
}
