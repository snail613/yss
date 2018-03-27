package com.yss.main.operdata;

import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * @author Jason_K
 *
 */
public class TaInterestPecPayBean extends CashPecPayBean{
	
	private java.util.Date calculateDate;   //收益计提日期
	private java.util.Date beginDate;       //用于查询的开始日期
	private java.util.Date endDate;			//用于查询的结束日期
	
	
	public java.util.Date getCalculateDate() {
		return calculateDate;
	}
	public void setCalculateDate(java.util.Date calculateDate) {
		this.calculateDate = calculateDate;
	}
	public java.util.Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(java.util.Date beginDate) {
		this.beginDate = beginDate;
	}
	public java.util.Date getEndDate() {
		return endDate;
	}
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}
	
	
}
