package com.yss.main.operdeal.report.moneycontrol.pojo;

import java.util.Date;

/***
 * 20100805 MS01461 QDV4华安2010年7月20日01_A 
 * @author yanghaiming
 * 外币管控表pojo类
 */
public class MoneyControlBean implements Cloneable {
	private String forecast;//T日
	private Date fdate;//生成报表的日期
	private Date forecastdate;//T日所对应的日期
	private int finout;//0表示余额，-1表示流出，1表示流入
	private String weekday;//星期
	private int reporttype;//报表类型（0表示预估，1表示确认）
	private String fportcode;//组合代码
	private double hsbcUSD;//美元金额
	private double hsbcHKD;//港币金额
	private double hsbcSGD;//新加坡币金额
	private double hsbcTWD;//台币金额
	private double icbcUSD;//工行美元金额
	private double icbcHKD;//工行港币金额
	private double icbcCNY;//工行人民币金额
	private double hsbccHKD;//港币初始保证金
	
	public double getHsbccHKD() {
		return hsbccHKD;
	}
	public void setHsbccHKD(double hsbccHKD) {
		this.hsbccHKD = hsbccHKD;
	}
	public String getForecast() {
		return forecast;
	}
	public void setForecast(String forecast) {
		this.forecast = forecast;
	}
	public Date getFdate() {
		return fdate;
	}
	public void setFdate(Date fdate) {
		this.fdate = fdate;
	}
	public Date getForecastdate() {
		return forecastdate;
	}
	public void setForecastdate(Date forecastdate) {
		this.forecastdate = forecastdate;
	}
	public int getFinout() {
		return finout;
	}
	public void setFinout(int finout) {
		this.finout = finout;
	}
	public String getWeekday() {
		return weekday;
	}
	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}
	public int getReporttype() {
		return reporttype;
	}
	public void setReporttype(int reporttype) {
		this.reporttype = reporttype;
	}
	public String getFportcode() {
		return fportcode;
	}
	public void setFportcode(String fportcode) {
		this.fportcode = fportcode;
	}
	public double getHsbcUSD() {
		return hsbcUSD;
	}
	public void setHsbcUSD(double hsbcUSD) {
		this.hsbcUSD = hsbcUSD;
	}
	public double getHsbcHKD() {
		return hsbcHKD;
	}
	public void setHsbcHKD(double hsbcHKD) {
		this.hsbcHKD = hsbcHKD;
	}
	public double getHsbcSGD() {
		return hsbcSGD;
	}
	public void setHsbcSGD(double hsbcSGD) {
		this.hsbcSGD = hsbcSGD;
	}
	public double getHsbcTWD() {
		return hsbcTWD;
	}
	public void setHsbcTWD(double hsbcTWD) {
		this.hsbcTWD = hsbcTWD;
	}
	public double getIcbcUSD() {
		return icbcUSD;
	}
	public void setIcbcUSD(double icbcUSD) {
		this.icbcUSD = icbcUSD;
	}
	public double getIcbcHKD() {
		return icbcHKD;
	}
	public void setIcbcHKD(double icbcHKD) {
		this.icbcHKD = icbcHKD;
	}
	public double getIcbcCNY() {
		return icbcCNY;
	}
	public void setIcbcCNY(double icbcCNY) {
		this.icbcCNY = icbcCNY;
	}
	
	public Object clone(){
		MoneyControlBean o = null;
        try{
            o = (MoneyControlBean)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return o;

	}
}
