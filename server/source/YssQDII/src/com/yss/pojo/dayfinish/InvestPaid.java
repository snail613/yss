package com.yss.pojo.dayfinish;

import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class InvestPaid
    extends BaseBean implements IYssConvert {
    private boolean isAll;
    private String IVPayCatCode;
    private String IVPayCatName;

    //------MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
    private String FeeType; // 运营收支品种类型名称
    private String ASetTypeName; // 资产类型名称
    //------End MS00017 add by wangzuochun 2009.06.24----------------
    private java.util.Date dDate;
    private String portCode;
    private String portName;
    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    //--------------------------------------------------------------------------------
    private String AnalysisCode1;
    private String AnalysisName1;
    private String AnalysisCode2;
    private String AnalysisName2;
    private String AnalysisCode3;
    private String AnalysisName3;
    //--------QDV4中保2008-11-4日01_B 添加现金类的分析代码解析-------
    private String AnalysisCode21;
    private String AnalysisName21;
    private String AnalysisCode22;
    private String AnalysisName22;
    //-----------------------------------------------------------
    //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码23处理 sj modified ------------//
    private String AnalysisCode23;
    private String AnalysisName23;
    //---------------------------------------------------------------------------------//
    private String SubTsfTypeCode;
    private String SubTsfTypeName;
    private String CuryCode;
    private String CuryName;
    private String tsfTypeCode;
    private String tsfTypeName;
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率
    private double money;
    private double BaseCuryMoney;
    private double PortCuryMoney;
    private int DataSource;
    private int StockInd;
    private java.util.Date mDate;
    public java.util.Date getmDate() {
		return mDate;
	}

	public void setmDate(java.util.Date mDate) {
		this.mDate = mDate;
	}

	private String CashAccCode;
    private String CashAccName;
    private int FPayType;
    private double balMoney;
    private double investMoney;
//----------------支付日期 -----------------------------------------------------//
    
    /**
     * add by huangqirong 2012-04-17 story #2326
     * */
    public String buildRowStr1() {
        StringBuffer buf = new StringBuffer();
        buf.append(YssFun.formatDate(this.dDate,"yyyy-MM-dd")).append("\t");
        buf.append(YssFun.formatDate(this.dDate,"yyyy-MM-dd")).append("\t");
        //buf.append(this.dDate).append("\t");
        buf.append(this.IVPayCatCode).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.AnalysisCode1).append("\t");
        buf.append(this.AnalysisCode2).append("\t");
        buf.append(this.AnalysisCode3).append("\t");
        buf.append(this.AnalysisCode21 == null ? this.AnalysisCode1 : this.AnalysisCode21).append("\t");
        buf.append(this.AnalysisCode22 == null ? " " : this.AnalysisCode22).append("\t");
        buf.append(this.AnalysisCode23 == null ? " " :  this.AnalysisCode23).append("\t");
        buf.append(this.FPayType).append("\t");
        buf.append(this.tsfTypeCode == null ?"02":"03").append("\t");
        buf.append(this.SubTsfTypeCode == null ?"02IV":"03IV").append("\t");
        buf.append(this.CuryCode).append("\t");
        buf.append(this.CashAccCode).append("\t");
        buf.append(this.money).append("\t");
        buf.append(this.BaseCuryMoney).append("\t");
        buf.append(this.PortCuryMoney).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(YssFun.formatDate(this.mDate, "yyyy-MM-dd")).append("\t");
        buf.append(this.balMoney).append("\t");
        buf.append(this.investMoney).append("\t");
        buf.append(YssFun.formatDate(this.dDate,"yyyy-MM-dd")).append("\tnull");        
        return buf.toString();
	}
   
    
    private java.util.Date pDate;

    public java.util.Date getPDate() {
        return pDate;
    }

    public void setPDate(java.util.Date pDate) {
        this.pDate = pDate;
    }

//------------------------------------------------------------------------------
    public int getPayType() {
        return FPayType;
    }

    public void setPayType(int FPayType) {
        this.FPayType = FPayType;
    }

    public String getCuryCode() {
        return CuryCode;
    }

    public void setCuryCode(String CuryCode) {
        this.CuryCode = CuryCode;
    }

    public String getCuryName() {
        return CuryName;
    }

    public void setCuryName(String CuryName) {
        this.CuryName = CuryName;
    }

    public String getCashAccCode() {
        return CashAccCode;
    }

    public void setCashAccCode(String CashAccCode) {
        this.CashAccCode = CashAccCode;
    }

    public String getCashAccName() {
        return CashAccName;
    }

    public void setCashAccName(String CashAccName) {
        this.CashAccName = CashAccName;
    }

    public int getStockInd() {
        return StockInd;
    }

    public void setStockInd(int StockInd) {
        this.StockInd = StockInd;
    }

    public int getDataSource() {
        return DataSource;
    }

    public void setDataSource(int DataSource) {
        this.DataSource = DataSource;
    }

    public boolean getIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    public String getIVPayCatCode() {
        return IVPayCatCode;
    }

    public void setIVPayCatCode(String IVPayCatCode) {
        this.IVPayCatCode = IVPayCatCode;
    }

    public void setIVPayCatName(String IVPayCatCode) {
        this.IVPayCatName = IVPayCatCode;
    }

    public String getIVPayCatName() {
        return IVPayCatName;
    }

    public String getSubTsfTypeCode() {
        return SubTsfTypeCode;
    }

    public void setSubTsfTypeCode(String SubTsfTypeCode) {
        this.SubTsfTypeCode = SubTsfTypeCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public String getSubTsfTypeName() {
        return SubTsfTypeName;
    }

    public void setSubTsfTypeName(String SubTsfTypeName) {
        this.SubTsfTypeName = SubTsfTypeName;
    }

    public String getTsfTypeName() {
        return tsfTypeName;
    }

    public void setTsfTypeName(String TsfTypeName) {
        this.tsfTypeName = TsfTypeName;
    }

    public void setTsfTypeCode(String tsfTypeCode) {
        this.tsfTypeCode = tsfTypeCode;
    }

    public String getTsfTypeCode() {
        return tsfTypeCode;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public void setBaseCuryRate(double BaseCuryRate) {
        this.baseCuryRate = BaseCuryRate;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public java.util.Date getDDate() {
        return dDate;
    }

    public double getBalMoney() {
        return balMoney;
    }

    public double getInvestMoney() {
        return investMoney;
    }

    public void setDDate(java.util.Date dDate) {
        this.dDate = dDate;
    }

    public void setBalMoney(double balMoney) {
        this.balMoney = balMoney;
    }

    public void setInvestMoney(double investMoney) {
        this.investMoney = investMoney;
    }

    public Date getMatureDate() {
        return mDate;
    }

    public void setMatureDate(Date mDate) {
        this.mDate = mDate;
    }

    public double getBaseCuryMoney() {
        return BaseCuryMoney;
    }

    public void setBaseCuryMoney(double BaseCuryMoney) {
        this.BaseCuryMoney = BaseCuryMoney;
    }

    public double getPortCuryMoney() {
        return PortCuryMoney;
    }

    public void setPortCuryMoney(double PortCuryMoney) {
        this.PortCuryMoney = PortCuryMoney;
    }

    public String getAnalysisCode1() {
        return AnalysisCode1;
    }

    public void setAnalysisCode1(String AnalysisCode1) {
        this.AnalysisCode1 = AnalysisCode1;
    }

    public String getAnalysisCode2() {
        return AnalysisCode2;
    }

    public void setAnalysisCode2(String AnalysisCode2) {
        this.AnalysisCode2 = AnalysisCode2;
    }

    public String getAnalysisCode3() {
        return AnalysisCode3;
    }

    public void setAnalysisCode3(String AnalysisCode3) {
        this.AnalysisCode3 = AnalysisCode3;
    }

    public String getAnalysisName1() {
        return AnalysisName1;
    }

    public void setAnalysisName1(String AnalysisName1) {
        this.AnalysisName1 = AnalysisName1;
    }

    public String getAnalysisName2() {
        return AnalysisName2;
    }

    public void setAnalysisName2(String AnalysisName2) {
        this.AnalysisName2 = AnalysisName2;
    }

    public String getAnalysisName3() {
        return AnalysisName3;
    }

    public void setAnalysisName3(String AnalysisName3) {
        this.AnalysisName3 = AnalysisName3;
    }

    public String getAnalysisCode21() {
        return AnalysisCode21;
    }

    public void setAnalysisCode21(String AnalysisCode21) {
        this.AnalysisCode21 = AnalysisCode21;
    }

    public String getAnalysisCode22() {
        return AnalysisCode22;
    }

    public void setAnalysisCode22(String AnalysisCode22) {
        this.AnalysisCode22 = AnalysisCode22;
    }

    //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码23处理 sj modified ------------//
    public String getAnalysisCode23() {
        return AnalysisCode23;
    }

    public void setAnalysisCode23(String AnalysisCode23) {
        this.AnalysisCode23 = AnalysisCode23;
    }

    //--------------------------------------------------------------------------------//
    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public String getASetTypeName() {
        return ASetTypeName;
    }

    public String getFeeType() {
        return FeeType;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public void setASetTypeName(String ASetTypeName) {
        this.ASetTypeName = ASetTypeName;
    }

    public void setFeeType(String FeeType) {
        this.FeeType = FeeType;
    }

    public InvestPaid() {
    }

    public String buildRowStr() throws YssException {
//      return this.autoBuildRowStr("DDate\tIVPayCatCode\tIVPayCatName\tPortCode\tPortName\tAnalysisCode1\tAnalysisName1\tPayType" +
//            "\tCuryCode\tCuryName\tCashAccCode\tCashAccName\tMoney;#,##0.##\tBaseCuryMoney;#,##0.##\tPortCuryMoney;#,##0.##\tBaseCuryRate\tPortCuryRate\tMatureDate");
        //-MS00237 QDV4中保2009年02月05日01_A 增加对分析代码2、3处理 sj modified --------//
		//------ MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A ***增加字段FeeType,ASetTypeName

        return this.autoBuildRowStr("DDate\tIVPayCatCode\tIVPayCatName\tFeeType\tPortCode\tPortName\tAnalysisCode1\tAnalysisName1\tAnalysisCode2\tAnalysisName2\tAnalysisCode3\tAnalysisName3\tPayType" +
                                    "\tASetTypeName\tCuryCode\tCuryName\tCashAccCode\tCashAccName\tMoney;#,##0.##\tBaseCuryMoney;#,##0.##\tPortCuryMoney;#,##0.##\tBaseCuryRate\tPortCuryRate\tMatureDate\tAssetGroupCode\tAssetGroupName");
        //---------------------------------------------------------------------------//
    }

    public String getOperValue(String sType) {
        return "";
    }
    		
    public void parseRowStr(String sRowStr) throws YssException {
        //----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified
        this.autoParseRowStr(
            "DDate\tPDate\tIVPayCatCode\tPortCode\tAnalysisCode1\tAnalysisCode2\tAnalysisCode3\tAnalysisCode21\tAnalysisCode22\tAnalysisCode23\tPayType\tTsfTypeCode\tSubTsfTypeCode\tCuryCode\tCashAccCode\tMoney\tBaseCuryMoney\tPortCuryMoney\tBaseCuryRate\tPortCuryRate\tMatureDate\tBalMoney\tInvestMoney\tmDate",//edit by yanghaiming 20100416 MS00997  QDV4建行2010年02月23日01_B 增加mdate为业务日期
            sRowStr);
        //-------------------------------------------------------------------
    }
}
