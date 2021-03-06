package com.yss.main.operdeal.report.reptab.indexfuturesbook.pojo;

import java.sql.Date;

import com.yss.dsub.BaseBean;

/****************************************************
 * 股指期货台帐表
 * 股指期货库存：
 * @author benson
 *
 */
public class IndexFuturesStockBean extends BaseBean {

	private java.util.Date      dStockDate = null;//期货库存日期
    private String    sSecuritycode = "";         //合约代码
    private String    sPortCode = "";             //组合代码
    private String    sBrokerCode = "";           //券商代码
    private String    sFuType = "";               //开仓类型
    private double    dClosePositionRatio = 0;    //平仓比例
	private double    dCarryOverCost = 0;         //结转成本
	private double    dCarryOverMV = 0;           //结转估增
	private double    dClosePositionLossGain = 0; //平仓损益
	private double    dAmount = 0;                //持仓数量
	private double    dCost = 0;                  //持仓金额
	private double    dAdjustMV = 0;              //估值前价值调整
	private double    dTotalLossGain = 0;         //持仓损益合计
	private double    dDayLossGain = 0;           //当日盈亏
	private double    dSettlementMoney = 0;       //无负债结算
	private double    dAdjustMargins = 0;         //保证金调整
	private String    curyCode = "";              //币种
    private double    lossGain_day = 0;           //持仓损益(发生额）
    private String    marketCode = "";            //市场代码
	
	public String getMarketCode() {
		return marketCode;
	}


	public void setMarketCode(String marketCode) {
		this.marketCode = marketCode;
	}


	public double getLossGain_day() {
		return lossGain_day;
	}


	public void setLossGain_day(double lossGainDay) {
		lossGain_day = lossGainDay;
	}


	public String getCuryCode() {
		return curyCode;
	}


	public void setCuryCode(String curyCode) {
		this.curyCode = curyCode;
	}


	public java.util.Date getdStockDate() {
		return dStockDate;
	}


	public void setdStockDate(java.util.Date dDate) {
		this.dStockDate = dDate;
	}


	public String getsSecuritycode() {
		return sSecuritycode;
	}


	public void setsSecuritycode(String sSecuritycode) {
		this.sSecuritycode = sSecuritycode;
	}


	public String getsPortCode() {
		return sPortCode;
	}


	public void setsPortCode(String sPortCode) {
		this.sPortCode = sPortCode;
	}


	public String getsBrokerCode() {
		return sBrokerCode;
	}


	public void setsBrokerCode(String sBrokerCode) {
		this.sBrokerCode = sBrokerCode;
	}


	public String getsFuType() {
		return sFuType;
	}


	public void setsFuType(String sFuType) {
		this.sFuType = sFuType;
	}
	
	public double getdClosePositionRatio() {
		return dClosePositionRatio;
	}


	public void setdClosePositionRatio(double dClosePositionRatio) {
		this.dClosePositionRatio = dClosePositionRatio;
	}


	public double getdCarryOverCost() {
		return dCarryOverCost;
	}


	public void setdCarryOverCost(double dCarryOverCost) {
		this.dCarryOverCost = dCarryOverCost;
	}


	public double getdCarryOverMV() {
		return dCarryOverMV;
	}


	public void setdCarryOverMV(double dCarryOverMV) {
		this.dCarryOverMV = dCarryOverMV;
	}


	public double getdClosePositionLossGain() {
		return dClosePositionLossGain;
	}


	public void setdClosePositionLossGain(double dClosePositionLossGain) {
		this.dClosePositionLossGain = dClosePositionLossGain;
	}


	public double getdAmount() {
		return dAmount;
	}


	public void setdAmount(double dAmount) {
		this.dAmount = dAmount;
	}


	public double getdCost() {
		return dCost;
	}


	public void setdCost(double dCost) {
		this.dCost = dCost;
	}


	public double getdAdjustMV() {
		return dAdjustMV;
	}


	public void setdAdjustMV(double dAdjustMV) {
		this.dAdjustMV = dAdjustMV;
	}


	public double getdTotalLossGain() {
		return dTotalLossGain;
	}


	public void setdTotalLossGain(double dLossGain) {
		this.dTotalLossGain = dLossGain;
	}


	public double getdDayLossGain() {
		return dDayLossGain;
	}


	public void setdDayLossGain(double dDayLossGain) {
		this.dDayLossGain = dDayLossGain;
	}


	public double getdSettlementMoney() {
		return dSettlementMoney;
	}


	public void setdSettlementMoney(double dSettlementMoney) {
		this.dSettlementMoney = dSettlementMoney;
	}


	public double getdAdjustMargins() {
		return dAdjustMargins;
	}


	public void setdAdjustMargins(double dAdjustMargins) {
		this.dAdjustMargins = dAdjustMargins;
	}
	
	
	
	public IndexFuturesStockBean(){
		
	}
}
