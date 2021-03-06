package com.yss.main.operdeal.bond.pojo;

import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseBean;

public class BondAssistBean extends BaseBean{
	//证券代码
	private String secCode = "";
	//计息截止日
	private Date bondEndDate = null;
	//是否调息
	private boolean changeRate = false;
	//票面利率
	private double pmll = 0.0;
	//剩余本金
	private double sybj = 0.0;
	//付息频率
	private int fxpl = 1;
	//计息公式
	private String jxgs = "";
	//付息公式
	private String fxgs = "";
	public HashMap<String, BondFactRateBean> getFactRateMap() {
		return factRateMap;
	}
	public void setFactRateMap(HashMap<String, BondFactRateBean> factRateMap) {
		this.factRateMap = factRateMap;
	}
	public HashMap<String, BondStockInfoBean> getStockInfoMap() {
		return stockInfoMap;
	}
	public void setStockInfoMap(HashMap<String, BondStockInfoBean> stockInfoMap) {
		this.stockInfoMap = stockInfoMap;
	}
	//总付息次数
	private int zfxcs = 1;
	//剩余付息次数
	private int syfxcs = 1;
	//本息和
	private double bxh = 0.0;
	//一年天数
	private int ynts = 1;
	//剩余天数（需考虑计息公式）
	private int syts = 1;
	//剩余天数（不用考虑计息公式）
	private int syts1 = 1;
	
	private HashMap<String, BondFactRateBean> factRateMap = null;
	private HashMap<String, BondStockInfoBean> stockInfoMap = null;
	
	public String getSecCode() {
		return secCode;
	}
	public void setSecCode(String secCode) {
		this.secCode = secCode;
	}
	public Date getBondEndDate() {
		return bondEndDate;
	}
	public void setBondEndDate(Date bondEndDate) {
		this.bondEndDate = bondEndDate;
	}
	public boolean isChangeRate() {
		return changeRate;
	}
	public void setChangeRate(boolean changeRate) {
		this.changeRate = changeRate;
	}
	public double getPmll() {
		return pmll;
	}
	public void setPmll(double pmll) {
		this.pmll = pmll;
	}
	public double getSybj() {
		return sybj;
	}
	public void setSybj(double sybj) {
		this.sybj = sybj;
	}
	public int getFxpl() {
		return fxpl;
	}
	public void setFxpl(int fxpl) {
		this.fxpl = fxpl;
	}
	public String getJxgs() {
		return jxgs;
	}
	public void setJxgs(String jxgs) {
		this.jxgs = jxgs;
	}
	public String getFxgs() {
		return fxgs;
	}
	public void setFxgs(String fxgs) {
		this.fxgs = fxgs;
	}
	public int getZfxcs() {
		return zfxcs;
	}
	public void setZfxcs(int zfxcs) {
		this.zfxcs = zfxcs;
	}
	public int getSyfxcs() {
		return syfxcs;
	}
	public void setSyfxcs(int syfxcs) {
		this.syfxcs = syfxcs;
	}
	public double getBxh() {
		return bxh;
	}
	public void setBxh(double bxh) {
		this.bxh = bxh;
	}
	public int getYnts() {
		return ynts;
	}
	public void setYnts(int ynts) {
		this.ynts = ynts;
	}
	public int getSyts() {
		return syts;
	}
	public void setSyts(int syts) {
		this.syts = syts;
	}
	public int getSyts1() {
		return syts1;
	}
	public void setSyts1(int syts1) {
		this.syts1 = syts1;
	}
}
