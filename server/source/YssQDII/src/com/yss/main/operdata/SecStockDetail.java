package com.yss.main.operdata;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataInterface;
import com.yss.util.YssException;

public class SecStockDetail extends BaseDataSettingBean implements
		IDataInterface {
	private String fNum = "";//交易编号
	private String fSecurityCode = "";//证券代码
	private java.util.Date fBargainDate = null;//库存日期
	private String strPortCode= "";         //组合代码
	private String invMgrCodeName = ""; //投资经理名称
	private String brokerCode= "";              //券商代码
	private String attrClsCode = "";    //所属分类 
	private String investType = "";    //投资类型
	private double amount;          //数量
	private double cost; //原币核算成本
    private double mCost; //原币管理成本
    private double vCost; //原币估值成本
    private double baseCost; //基础货币核算成本
    private double baseMCost; //基础货币管理成本
    private double baseVCost; //基础货币估值成本
    private double portCost; //组合货币核算成本
    private double portMCost; //组合货币管理成本
    private double portVCost; //组合货币估值成本
	
	public String getfNum() {
		return fNum;
	}

	public void setfNum(String fNum) {
		this.fNum = fNum;
	}

	public String getfSecurityCode() {
		return fSecurityCode;
	}

	public void setfSecurityCode(String fSecurityCode) {
		this.fSecurityCode = fSecurityCode;
	}

	public java.util.Date getfBargainDate() {
		return fBargainDate;
	}

	public void setfBargainDate(java.util.Date fBargainDate) {
		this.fBargainDate = fBargainDate;
	}

	public String getStrPortCode() {
		return strPortCode;
	}

	public void setStrPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}

	public String getInvMgrCodeName() {
		return invMgrCodeName;
	}

	public void setInvMgrCodeName(String invMgrCodeName) {
		this.invMgrCodeName = invMgrCodeName;
	}

	public String getBrokerCode() {
		return brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public String getAttrClsCode() {
		return attrClsCode;
	}

	public void setAttrClsCode(String attrClsCode) {
		this.attrClsCode = attrClsCode;
	}

	public String getInvestType() {
		return investType;
	}

	public void setInvestType(String investType) {
		this.investType = investType;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getmCost() {
		return mCost;
	}

	public void setmCost(double mCost) {
		this.mCost = mCost;
	}

	public double getvCost() {
		return vCost;
	}

	public void setvCost(double vCost) {
		this.vCost = vCost;
	}

	public double getBaseCost() {
		return baseCost;
	}

	public void setBaseCost(double baseCost) {
		this.baseCost = baseCost;
	}

	public double getBaseMCost() {
		return baseMCost;
	}

	public void setBaseMCost(double baseMCost) {
		this.baseMCost = baseMCost;
	}

	public double getBaseVCost() {
		return baseVCost;
	}

	public void setBaseVCost(double baseVCost) {
		this.baseVCost = baseVCost;
	}

	public double getPortCost() {
		return portCost;
	}

	public void setPortCost(double portCost) {
		this.portCost = portCost;
	}

	public double getPortMCost() {
		return portMCost;
	}

	public void setPortMCost(double portMCost) {
		this.portMCost = portMCost;
	}

	public double getPortVCost() {
		return portVCost;
	}

	public void setPortVCost(double portVCost) {
		this.portVCost = portVCost;
	}

	public SecStockDetail(){
		
	}
	
	public String exportData(String sRequestStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void importData(String sRequestStr) throws YssException {
		// TODO Auto-generated method stub

	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub

	}

}
