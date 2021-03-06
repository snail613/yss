package com.yss.pojo.dayfinish;

import java.util.Date;

import com.yss.dsub.BaseBean;
import com.yss.main.dao.IYssConvert;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class ScotDefrayBean extends BaseBean implements IYssConvert {
    private String YearMonth = "";//库存年月
    private java.util.Date StorageDate;//库存日期
    private String PortCode = "";//组合代码
    private String PortName = "";//组合名称
    private String CashAccCode = "";//现金帐户
    private String CashAccName = "";//现金帐户名称
    private String CuryCode = "";//币种代码
    private String CuryName = "";//币种名称
    private String AnalysisCode1 = "";//分析代码1
    private String AnalysisName1 = "";//分析名称1
    private String AnalysisCode2 = "";//分析代码2
    private String AnalysisName2 = "";//分析名称2
    private String AnalysisCode3 = "";//分析代码3
    private String AnalysisName3 = "";//分析名称3
    private String TsfTypeCode = "";//账户类型
    private String TsfTypeName = "";//账户类型名称
    private String SubTsfTypeCode = "";//账户子类型
    private String SubTsfTypeName = "";//账户子类型名称
    private int iStorageState = 0;//库存状态
    private double Bal = 0;//原币余额
    private double BaseBal = 0;//基础币余额
    private double PortBal = 0;//组合币余额
    private String IsOnlyColumns = "0";
    private String AttrClsCode = "";//属性分类
    private String AttrClsName = "";//属性分类名称
    
    private String bBegin; //是否为期初数

	private ScotDefrayBean filterType;
	private double dBaseRate = 1;//基础汇率
	private double dPortRate = 1;//组合汇率
	private String sIsDif = " "; //通过描述标识尾差数据
	
	private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
	
    private Date PaidDate = new Date();//支付日期
    private String ChangePortCode = "";//下方组合
    private String ChangeCashAccCode = "";//下放现金账户
    private String BalMoney = "";//应付税金
    private String RealMoney = "";//实付金额
    private String AdjustMoney = "";//调整金额
    
    
    
	/**拼接参数*/
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
        buf.append(this.CashAccCode).append("\t");
        buf.append(this.CashAccName).append("\t");
        buf.append(YssFun.formatDate(this.StorageDate)).append("\t");
        buf.append(this.Bal).append("\t");
        buf.append(this.TsfTypeCode).append("\t");
        buf.append(this.TsfTypeName).append("\t");
        buf.append(this.SubTsfTypeCode).append("\t");
        buf.append(this.SubTsfTypeName).append("\t");
        buf.append(this.CuryCode).append("\t");
        buf.append(this.PortCode).append("\t");
        buf.append(this.PortName).append("\t");
        buf.append(this.dBaseRate).append("\t");
        buf.append(this.dPortRate).append("");
        return buf.toString();
	}
	
	public String getOperValue(String sType) throws YssException {
		
		return null;
	}
	
	/**解析参数*/
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\t");
            
            this.CashAccCode = reqAry[0];
            if (!reqAry[1].equalsIgnoreCase("")) {
            	this.StorageDate = YssFun.toDate(reqAry[1]);
            }
            this.Bal = Double.valueOf(reqAry[2]).doubleValue();
            this.TsfTypeCode = reqAry[3];
            this.SubTsfTypeCode = reqAry[4];
            this.CuryCode = reqAry[5];
            this.PortCode = reqAry[6];
            if (!reqAry[7].equalsIgnoreCase("")) {
                this.PaidDate = YssFun.toDate(reqAry[7]);
            }
            this.ChangePortCode = reqAry[8];
            this.ChangeCashAccCode = reqAry[9];
            this.BalMoney = reqAry[10];
            //若实际支付金额为空,默认等于应付金额
            this.RealMoney = reqAry[11].trim().length()>0 ? reqAry[11] : reqAry[10];
            this.AdjustMoney = reqAry[12].trim().length()>0 ? reqAry[12] : "0";
            this.dBaseRate = Double.valueOf(reqAry[13]).doubleValue();
            this.dPortRate = Double.valueOf(reqAry[14]).doubleValue();
        } catch (Exception e) {
            throw new YssException("解析收益支付请求信息出错\r\n" + e.getMessage(), e);
        }
	}

	
	public String getYearMonth() {
		return YearMonth;
	}

	public void setYearMonth(String yearMonth) {
		YearMonth = yearMonth;
	}

	public java.util.Date getStorageDate() {
		return StorageDate;
	}

	public void setStorageDate(java.util.Date storageDate) {
		StorageDate = storageDate;
	}

	public String getPortCode() {
		return PortCode;
	}

	public void setPortCode(String portCode) {
		PortCode = portCode;
	}

	public String getPortName() {
		return PortName;
	}

	public void setPortName(String portName) {
		PortName = portName;
	}

	public String getCashAccCode() {
		return CashAccCode;
	}

	public void setCashAccCode(String cashAccCode) {
		CashAccCode = cashAccCode;
	}

	public String getCashAccName() {
		return CashAccName;
	}

	public void setCashAccName(String cashAccName) {
		CashAccName = cashAccName;
	}

	public String getCuryCode() {
		return CuryCode;
	}

	public void setCuryCode(String curyCode) {
		CuryCode = curyCode;
	}

	public String getCuryName() {
		return CuryName;
	}

	public void setCuryName(String curyName) {
		CuryName = curyName;
	}

	public String getAnalysisCode1() {
		return AnalysisCode1;
	}

	public void setAnalysisCode1(String analysisCode1) {
		AnalysisCode1 = analysisCode1;
	}

	public String getAnalysisName1() {
		return AnalysisName1;
	}

	public void setAnalysisName1(String analysisName1) {
		AnalysisName1 = analysisName1;
	}

	public String getAnalysisCode2() {
		return AnalysisCode2;
	}

	public void setAnalysisCode2(String analysisCode2) {
		AnalysisCode2 = analysisCode2;
	}

	public String getAnalysisName2() {
		return AnalysisName2;
	}

	public void setAnalysisName2(String analysisName2) {
		AnalysisName2 = analysisName2;
	}

	public String getAnalysisCode3() {
		return AnalysisCode3;
	}

	public void setAnalysisCode3(String analysisCode3) {
		AnalysisCode3 = analysisCode3;
	}

	public String getAnalysisName3() {
		return AnalysisName3;
	}

	public void setAnalysisName3(String analysisName3) {
		AnalysisName3 = analysisName3;
	}

	public String getTsfTypeCode() {
		return TsfTypeCode;
	}

	public void setTsfTypeCode(String tsfTypeCode) {
		TsfTypeCode = tsfTypeCode;
	}

	public String getTsfTypeName() {
		return TsfTypeName;
	}

	public void setTsfTypeName(String tsfTypeName) {
		TsfTypeName = tsfTypeName;
	}

	public String getSubTsfTypeCode() {
		return SubTsfTypeCode;
	}

	public void setSubTsfTypeCode(String subTsfTypeCode) {
		SubTsfTypeCode = subTsfTypeCode;
	}

	public String getSubTsfTypeName() {
		return SubTsfTypeName;
	}

	public void setSubTsfTypeName(String subTsfTypeName) {
		SubTsfTypeName = subTsfTypeName;
	}

	public int getiStorageState() {
		return iStorageState;
	}

	public void setiStorageState(int iStorageState) {
		this.iStorageState = iStorageState;
	}

	public double getBal() {
		return Bal;
	}

	public void setBal(double bal) {
		Bal = bal;
	}

	public double getBaseBal() {
		return BaseBal;
	}

	public void setBaseBal(double baseBal) {
		BaseBal = baseBal;
	}

	public double getPortBal() {
		return PortBal;
	}

	public void setPortBal(double portBal) {
		PortBal = portBal;
	}

	public String getIsOnlyColumns() {
		return IsOnlyColumns;
	}

	public void setIsOnlyColumns(String isOnlyColumns) {
		IsOnlyColumns = isOnlyColumns;
	}

	public String getAttrClsCode() {
		return AttrClsCode;
	}

	public void setAttrClsCode(String attrClsCode) {
		AttrClsCode = attrClsCode;
	}

	public String getAttrClsName() {
		return AttrClsName;
	}

	public void setAttrClsName(String attrClsName) {
		AttrClsName = attrClsName;
	}

	public String getbBegin() {
		return bBegin;
	}

	public void setbBegin(String bBegin) {
		this.bBegin = bBegin;
	}

	public ScotDefrayBean getFilterType() {
		return filterType;
	}

	public void setFilterType(ScotDefrayBean filterType) {
		this.filterType = filterType;
	}

	public double getdBaseRate() {
		return dBaseRate;
	}

	public void setdBaseRate(double dBaseRate) {
		this.dBaseRate = dBaseRate;
	}

	public double getdPortRate() {
		return dPortRate;
	}

	public void setdPortRate(double dPortRate) {
		this.dPortRate = dPortRate;
	}

	public String getsIsDif() {
		return sIsDif;
	}

	public void setsIsDif(String sIsDif) {
		this.sIsDif = sIsDif;
	}

	public String getAssetGroupCode() {
		return assetGroupCode;
	}

	public void setAssetGroupCode(String assetGroupCode) {
		this.assetGroupCode = assetGroupCode;
	}

	public String getAssetGroupName() {
		return assetGroupName;
	}

	public void setAssetGroupName(String assetGroupName) {
		this.assetGroupName = assetGroupName;
	}

	public Date getPaidDate() {
		return PaidDate;
	}

	public void setPaidDate(Date paidDate) {
		PaidDate = paidDate;
	}

	public String getChangePortCode() {
		return ChangePortCode;
	}

	public void setChangePortCode(String changePortCode) {
		ChangePortCode = changePortCode;
	}

	public String getChangeCashAccCode() {
		return ChangeCashAccCode;
	}

	public void setChangeCashAccCode(String changeCashAccCode) {
		ChangeCashAccCode = changeCashAccCode;
	}

	public String getBalMoney() {
		return BalMoney;
	}

	public void setBalMoney(String balMoney) {
		BalMoney = balMoney;
	}

	public String getRealMoney() {
		return RealMoney;
	}

	public void setRealMoney(String realMoney) {
		RealMoney = realMoney;
	}

	public String getAdjustMoney() {
		return AdjustMoney;
	}

	public void setAdjustMoney(String adjustMoney) {
		AdjustMoney = adjustMoney;
	}





}
