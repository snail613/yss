package com.yss.main.etfoperation.etfaccbook.timeandaverage; 

/**华夏ETF临时Bean 多用途,权益负责汇总,last值负责计算最后倒钆,其余负责保存数据
 * @author shashijie ,2011-11-17 下午02:46:16 STORY 1789
 */
public class TempETFUnitBean {
	
	private double tradeAmount = 0;//可以补票的证券数量
	private double totalCost = 0;//非指定交易的实收实付金额,已经乘以汇率,这里是本币金额
	private double unitCost = 0;//本币单位成本  = ( 可以补票数量  / 实收实付金额 )
	private double FRemainAmount = 0;//剩余数量
	private double dHcReplaceCash = 0;//补票可退替代款本币
	private double dHpReplaceCash = 0;//补票应付替代款本币
	private double dSumAmount = 0;//总权益总数量
	private double interest = 0;//总派息原币
	private double dBBinterest = 0;//总派息本币
	private double dRealAmount = 0;//总的权益实际数量
	private double warrantCost = 0;//总权证价值原币
	private double dBBWarrantCost = 0;//总权证价值本币
	private double MakeUpAmount = 0;//保存补票数量
	
	private double lastHcReplaceCash = 0;//汇总可退替代款（本币）除了最后一条数据
	
	private double lastHpReplaceCash = 0;//汇总应付替代款（本币）除了最后一条数据
	
	
	
	
	public double getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	public double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}
	public double getdHcReplaceCash() {
		return dHcReplaceCash;
	}
	public void setdHcReplaceCash(double dHcReplaceCash) {
		this.dHcReplaceCash = dHcReplaceCash;
	}
	public double getdHpReplaceCash() {
		return dHpReplaceCash;
	}
	public void setdHpReplaceCash(double dHpReplaceCash) {
		this.dHpReplaceCash = dHpReplaceCash;
	}
	public double getdBBinterest() {
		return dBBinterest;
	}
	public void setdBBinterest(double dBBinterest) {
		this.dBBinterest = dBBinterest;
	}
	public double getdBBWarrantCost() {
		return dBBWarrantCost;
	}
	public void setdBBWarrantCost(double dBBWarrantCost) {
		this.dBBWarrantCost = dBBWarrantCost;
	}
	public double getLastHcReplaceCash() {
		return lastHcReplaceCash;
	}
	public void setLastHcReplaceCash(double lastHcReplaceCash) {
		this.lastHcReplaceCash = lastHcReplaceCash;
	}
	public double getLastHpReplaceCash() {
		return lastHpReplaceCash;
	}
	public void setLastHpReplaceCash(double lastHpReplaceCash) {
		this.lastHpReplaceCash = lastHpReplaceCash;
	}
	public double getFRemainAmount() {
		return FRemainAmount;
	}
	public void setFRemainAmount(double fRemainAmount) {
		FRemainAmount = fRemainAmount;
	}
	public double getMakeUpAmount() {
		return MakeUpAmount;
	}
	public void setMakeUpAmount(double makeUpAmount) {
		MakeUpAmount = makeUpAmount;
	}
	public double getdSumAmount() {
		return dSumAmount;
	}
	public void setdSumAmount(double dSumAmount) {
		this.dSumAmount = dSumAmount;
	}
	public double getInterest() {
		return interest;
	}
	public void setInterest(double interest) {
		this.interest = interest;
	}
	public double getWarrantCost() {
		return warrantCost;
	}
	public void setWarrantCost(double warrantCost) {
		this.warrantCost = warrantCost;
	}
	public double getdRealAmount() {
		return dRealAmount;
	}
	public void setdRealAmount(double dRealAmount) {
		this.dRealAmount = dRealAmount;
	}
	
	
	
}

