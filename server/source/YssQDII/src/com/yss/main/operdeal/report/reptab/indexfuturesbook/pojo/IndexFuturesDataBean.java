package com.yss.main.operdeal.report.reptab.indexfuturesbook.pojo;

import java.sql.Date;

import com.yss.dsub.BaseBean;

/****************************************************
 *  股指期货台帐
 * @author benson
 *
 */
public class IndexFuturesDataBean extends BaseBean {

	//--------------------------------------------------------------
	
	private Date      dBargainDate = null;        //期货交易日期
    private String    sSecuritycode = "";         //合约代码
    private String    sPortCode = "";             //组合代码
    private String    sBrokerCode = "";           //券商代码
    private String    sFuType = "";               //开仓类型
    private String    sTradeType = "";            //交易类型
    private int       iMultiple = 0;              //合约乘数
    private double    dPrice = 0;                 //当日结算价
	private double    dOtPrice1 = 0;              //上日结算价
	private double    dAvgClosePositionPrice = 0; //平仓移动加权价
	private double    dAvgOpenPositionPrice = 0 ; //开仓移动加权价
	private int       iClosePositionAmount = 0;   //平仓数量
	private double    dClosePositionMoney = 0;    //平仓金额
	private double    dClosePositionFee = 0;      //平仓费用
	private int       iOpenPositionAmount = 0;    //开仓数量
	private double    dOpenPositionMoney = 0;     //开仓金额
	private double    dOpenPositionFee = 0;       //开仓费用
	private String    curyCode = "";              //币种
	private String    marketCode = "";            //市场代码
	//--------------------------------------------------------------
	



	public String getMarketCode() {
		return marketCode;
	}


	public void setMarketCode(String marketCode) {
		this.marketCode = marketCode;
	}


	public String getCuryCode() {
		return curyCode;
	}


	public void setCuryCode(String curyCode) {
		this.curyCode = curyCode;
	}


	public String getsSecuritycode() {
		return sSecuritycode;
	}


	public String getsTradeType() {
		return sTradeType;
	}


	public void setsTradeType(String sTradeType) {
		this.sTradeType = sTradeType;
	}


	public Date getdBargainDate() {
		return dBargainDate;
	}


	public void setdBargainDate(Date dBargainDate) {
		this.dBargainDate = dBargainDate;
	}


//	public double getdFeeRatio() {
//		return dFeeRatio;
//	}
//
//
//	public void setdFeeRatio(double dFeeRatio) {
//		this.dFeeRatio = dFeeRatio;
//	}


	public double getdAvgClosePositionPrice() {
		return dAvgClosePositionPrice;
	}


	public void setdAvgClosePositionPrice(double dAvgClosePositionPrice) {
		this.dAvgClosePositionPrice = dAvgClosePositionPrice;
	}


	public double getdAvgOpenPositionPrice() {
		return dAvgOpenPositionPrice;
	}


	public void setdAvgOpenPositionPrice(double dAvgOpenPositionPrice) {
		this.dAvgOpenPositionPrice = dAvgOpenPositionPrice;
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


	public int getiMultiple() {
		return iMultiple;
	}


	public void setiMultiple(int iMultiple) {
		this.iMultiple = iMultiple;
	}


	public double getdPrice() {
		return dPrice;
	}


	public void setdPrice(double dPrice) {
		this.dPrice = dPrice;
	}


	public double getdOtPrice1() {
		return dOtPrice1;
	}


	public void setdOtPrice1(double dOtPrice1) {
		this.dOtPrice1 = dOtPrice1;
	}


	public int getiClosePositionAmount() {
		return iClosePositionAmount;
	}


	public void setiClosePositionAmount(int iClosePositionAmount) {
		this.iClosePositionAmount = iClosePositionAmount;
	}


	public double getdClosePositionMoney() {
		return dClosePositionMoney;
	}


	public void setdClosePositionMoney(double dClosePositionMoney) {
		this.dClosePositionMoney = dClosePositionMoney;
	}


	public double getdClosePositionFee() {
		return dClosePositionFee;
	}


	public void setdClosePositionFee(double dClosePositionFee) {
		this.dClosePositionFee = dClosePositionFee;
	}


	public int getiOpenPositionAmount() {
		return iOpenPositionAmount;
	}


	public void setiOpenPositionAmount(int iOpenPositionAmount) {
		this.iOpenPositionAmount = iOpenPositionAmount;
	}


	public double getdOpenPositionMoney() {
		return dOpenPositionMoney;
	}


	public void setdOpenPositionMoney(double dOpenPositionMoney) {
		this.dOpenPositionMoney = dOpenPositionMoney;
	}


	public double getdOpenPositionFee() {
		return dOpenPositionFee;
	}


	public void setdOpenPositionFee(double dOpenPositionFee) {
		this.dOpenPositionFee = dOpenPositionFee;
	}


	
	
	//--------------------------------------------------------------
	
	public IndexFuturesDataBean (){
		
	}
}
