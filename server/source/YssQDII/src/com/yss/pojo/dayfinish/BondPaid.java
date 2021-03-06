package com.yss.pojo.dayfinish;

import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class BondPaid
    extends BaseBean implements IYssConvert {

    private boolean isAll;
    private java.util.Date nDate;
    private java.util.Date dDate;
    private String securityCode;
    private String securityName;
    private String portCode;
    private String portName;
    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    //--------------------------------------------------------------------------------
    private String invmgrCode;
    private String invmgrName;
    private String brokerCode;
    private String brokerName;
    private String tsfTypeCode;
    private String tsfTypeName;
    private String SubTsfTypeCode;
    private String SubTsfTypeName;
    private String curyCode;
    private String curyName;
    private String cashAccCode;
    private String cashAccName;

    private double money; //付息金额
    private double mMoney; //管理金额
    private double vMoney; //估值金额
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率

    private double balMoney; //修改前金额
    private double balmMoney; //修改前管理金额
    private double balvMoney; //修改前估值金额
    
    //20130411 added by liubo.Story #3528.偿还本金
    //========================
    private double dPayMoney;

    public double getPayMoney() {
		return dPayMoney;
	}

	public void setPayMoney(double dPayMoney) {
		this.dPayMoney = dPayMoney;
	}
    //=============end===========

	private double lx; //应收利息
    private java.util.Date mDate;

    public Date getmDate() {
		return mDate;
	}

	public void setmDate(Date mDate) {
		this.mDate = mDate;
	}

	//--------------- 分离应收应付与资金调拨的分析代码。analysisCode为资金调拨的分析代码 sj 20081222 MS00114
    private String SAnalysisCode1 = "";
    private String SAnalysisCode2 = "";
    private String SAnalysisCode3 = "";
    //--------------------------------------------------------------------------------//
    //----2009-08-22 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -----//
    private String attrClsCode = "";
    private String attrClsName = "";
    private String investType = "";
    private String investTypeName = "";
    //----------------------------------------------------------//
    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public void setSAnalysisCode1(String SAnalysisCode1) {
        this.SAnalysisCode1 = SAnalysisCode1;
    }

    public void setSAnalysisCode2(String SAnalysisCode2) {
        this.SAnalysisCode2 = SAnalysisCode2;
    }

    public void setSAnalysisCode3(String SAnalysisCode3) {
        this.SAnalysisCode3 = SAnalysisCode3;
    }

    public String getSAnalysisCode1() {
        return SAnalysisCode1;
    }

    public String getSAnalysisCode2() {
        return SAnalysisCode2;
    }

    public String getSAnalysisCode3() {
        return SAnalysisCode3;
    }

//------------------------------------------------------------------------

    public boolean getIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    public String getSubTsfTypeCode() {
        return SubTsfTypeCode;
    }

    public String getCuryName() {
        return curyName;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getInvmgrName() {
        return invmgrName;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public String getSubTsfTypeName() {
        return SubTsfTypeName;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getTsfTypeName() {
        return tsfTypeName;
    }

    public double getMoney() {
        return money;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public Date getNextCpnDate() {
        return nDate;
    }

    public Date getDDate() {
        return dDate;
    }

    public String getTsfTypeCode() {
        return tsfTypeCode;
    }

    public String getInvmgrCode() {
        return invmgrCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setSubTsfTypeCode(String SubTsfTypeCode) {
        this.SubTsfTypeCode = SubTsfTypeCode;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setInvmgrName(String invmgrName) {
        this.invmgrName = invmgrName;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setSubTsfTypeName(String SubTsfTypeName) {
        this.SubTsfTypeName = SubTsfTypeName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void setTsfTypeName(String tsfTypeName) {
        this.tsfTypeName = tsfTypeName;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setNextCpnDate(Date nDate) {
        this.nDate = nDate;
    }

    public void setDDate(Date dDate) {
        this.dDate = dDate;
    }

    public void setTsfTypeCode(String tsfTypeCode) {
        this.tsfTypeCode = tsfTypeCode;
    }

    public void setInvmgrCode(String invmgrCode) {
        this.invmgrCode = invmgrCode;
    }

    public void setMMoney(double mMoney) {
        this.mMoney = mMoney;
    }

    public void setVMoney(double vMoney) {
        this.vMoney = vMoney;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setCashAccName(String cashAccName) {
        this.cashAccName = cashAccName;
    }

    public void setBalMoney(double balMoney) {
        this.balMoney = balMoney;
    }

    public void setBalvMoney(double balvMoney) {
        this.balvMoney = balvMoney;
    }

    public void setNDate(Date nDate) {
        this.nDate = nDate;
    }

    public void setBalmMoney(double balmMoney) {
        this.balmMoney = balmMoney;
    }

    public void setLx(double lx) {
        this.lx = lx;
    }
    //----2009-08-22 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -----//
    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public void setAttrClsName(String attrClsName) {
        this.attrClsName = attrClsName;
    }

    public void setInvestType(String investType) {
        this.investType = investType;
    }

    public void setInvestTypeName(String investTypeName) {
        this.investTypeName = investTypeName;
    }

    //------------------------------------------------//
    public String getPortName() {
        return portName;
    }

    public double getMMoney() {
        return mMoney;
    }

    public double getVMoney() {
        return vMoney;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public String getCashAccName() {
        return cashAccName;
    }

    public double getBalMoney() {
        return balMoney;
    }

    public double getBalvMoney() {
        return balvMoney;
    }

    public Date getNDate() {
        return nDate;
    }

    public double getBalmMoney() {
        return balmMoney;
    }

    public double getLx() {
        return lx;
    }
    //----2009-08-22 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -----//
    public String getAttrClsCode() {
        return attrClsCode;
    }

    public String getAttrClsName() {
        return attrClsName;
    }

    public String getInvestType() {
        return investType;
    }

    public String getInvestTypeName() {
        return investTypeName;
    }

    //-----------------------------------------//
    public BondPaid() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        return this.autoBuildRowStr("DDate\tSecurityCode\tSecurityName\tPortCode\tPortName\tAttrClsCode\tAttrClsName\tInvestType\tInvestTypeName\tCashAccCode\tCashAccName\tInvmgrCode\tInvmgrName\t" + //2009-08-22 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                                    "BrokerCode\tBrokerName\tTsfTypeCode\tTsfTypeName\tSubTsfTypeCode\tSubTsfTypeName\t" +
                                    //fanghaoln 090526  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 增加组合群代码和组合群名称 这里加的是get的后面部分
                                    "CuryCode\tCuryName\tMoney;#,##0.##\tMMoney;#,##0.##\tVMoney;#,##0.##\tBaseCuryRate\tPortCuryRate\tNextCpnDate\tAssetGroupCode\tAssetGroupName"
            );
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        //--------分离应收应付与资金调拨的分析代码。analysisCode为资金调拨的分析代码 sj 20081222 MS00114  -----------------------------------//
        this.autoParseRowStr("DDate\tSecurityCode\tSecurityName\tPortCode\tPortName\tAttrClsCode\tAttrClsName\tInvestType\tInvmgrCode\tInvmgrName\t" + //2009-08-22 modify by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                             "BrokerCode\tBrokerName\tSAnalysisCode1\tSAnalysisCode2\tTsfTypeCode\tTsfTypeName\tSubTsfTypeCode\tSubTsfTypeName\t" +
                             "CuryCode\tCuryName\tMoney\tMMoney\tVMoney\tBaseCuryRate\tPortCuryRate\tCashAccCode\tCashAccName" +
                             "\tBalMoney\tBalmMoney\tBalvMoney\tLx\tmDate\tIsAll\tPayMoney",//edit by yanghaiming 20100226 MS00997  QDV4建行2010年02月23日01_B 增加mdate为业务日期
                             sRowStr);

    }
}
