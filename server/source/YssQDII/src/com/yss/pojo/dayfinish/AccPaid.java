package com.yss.pojo.dayfinish;

import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class AccPaid
    extends BaseBean implements IYssConvert {

    private boolean isAll;
    private java.util.Date dDate;
    private String portCode;
    private String portName;
    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    //--------------------------------------------------------------------------------
    private String invmgrCode;
    private String invmgrName;
    private String catCode;
    private String catName;
    private String tsfTypeCode;
    private String tsfTypeName;
    private String SubTsfTypeCode;
    private String SubTsfTypeName;
    private String curyCode;
    private String curyName;
    private String cashAccCode;
    private String cashAccName;
    private java.util.Date mDate;
    private String numType;

    private double money; //付息金额
    private double baseMoney; //付息金额-基础货币
    private double portMoney; //付息金额-组合货币
    private double baseCuryRate; //基础汇率
    private double portCuryRate; //组合汇率

    private double balMoney; //修改前金额 sj 20071126 add
    private double lx;

    private String changeCashAccCode; //获取更改过的现金帐户。sj edit 20080924 bug:0000479
    private String changeCashAccName; //获取更改过的现金帐户名。yeshenghong edit 20120216 story 2076
    //--------------------------------------------------------------------------------//

    public String getChangeCashAccName() {
		return changeCashAccName;
	}

	public void setChangeCashAccName(String changeCashAccName) {
		this.changeCashAccName = changeCashAccName;
	}

	//MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj  --
    private String relaNum;//设置关联编号，此处添加是为了对定存的处理
    
    //------add by wangzuochun 2010.06.08  MS01166    普通定存，做“转出”交易后，原账户利息库存没有冲减掉    QDV4国内（测试）2010年05月10日02_AB   
    private String outCashAccCode = ""; //流出现金帐户
    
    private String attrClsCode = "";//所属分类 NO.125 用户需要对组合按资本类别进行子组合的分类
    
    /**
     * add by huangqirong 2012-04-17 story #2326
     * @throws  
     * */
    public String buildRowStr1() {
    	StringBuffer buf = new StringBuffer();
    	buf.append(YssFun.formatDate(this.dDate,"yyyy-MM-dd")).append("\t");    	
    	buf.append(this.portCode == null ? "" : this.portCode.trim()).append("\t");
    	buf.append(this.portName == null ? "" : this.portName.trim()).append("\t");
    	buf.append(this.invmgrCode == null ?"" : this.invmgrCode).append("\t");
    	buf.append(this.invmgrName == null ? "" : this.invmgrName).append("\t");
    	buf.append(this.catCode == null ? " " : this.catCode ).append("\t");
    	buf.append(this.catName == null ? "" : this.catName).append("\t");
    	buf.append(this.tsfTypeCode == null ? "" : this.tsfTypeCode).append("\t");
    	buf.append(this.tsfTypeName == null ? "" : this.tsfTypeName).append("\t");
    	buf.append(this.SubTsfTypeCode == null ? "" : this.SubTsfTypeCode).append("\t");
    	buf.append(this.SubTsfTypeName == null ? "" : this.SubTsfTypeName).append("\t");
    	buf.append(this.curyCode == null ? "" : this.curyCode).append("\t");
    	buf.append(this.curyName == null ? "" : this.curyName).append("\t");
    	buf.append(this.cashAccCode == null ? "" : this.cashAccCode).append("\t");
    	buf.append(this.cashAccName == null ? "": this.cashAccName).append("\t");
    	buf.append(this.changeCashAccCode == null ? "" : this.changeCashAccCode).append("\t");
    	//buf.append(this.changeCashAccName == null ? "" : this.changeCashAccName).append("\t");
    	buf.append(this.money).append("\t");
    	buf.append(this.baseMoney).append("\t");
    	buf.append(this.portMoney).append("\t");
    	buf.append(this.baseCuryRate).append("\t");
    	buf.append(this.portCuryRate).append("\t");
    	buf.append(this.balMoney == 0 ? this.money : this.balMoney).append("\t");
    	buf.append(this.lx == 0 ? this.money : this.lx).append("\t");
    	buf.append(YssFun.formatDate(this.dDate,"yyyy-MM-dd")).append("\t");    
    	buf.append(this.isAll).append("\tnull");
    	
    	return buf.toString();
    }    
    
    
   //所属分类 NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2011.01.17----------//
    public String getAttrClsCode() {
		return attrClsCode;
	}

	public void setAttrClsCode(String attrClsCode) {
		this.attrClsCode = attrClsCode;
	}
   //所属分类 NO.125 用户需要对组合按资本类别进行子组合的分类 2011.01.17 end ------------------------//
	
	
    public String getOutCashAccCode(){
    	return outCashAccCode;
    }
    
    public void setOutCashAccCode(String outCashAccCode){
    	this.outCashAccCode = outCashAccCode;
    }
    //----------------------MS01166--------------------//
    
    public String getRelaNum() {
        return relaNum;
    }

    public void setRelaNum(String relaNum) {
        this.relaNum = relaNum;
    }

    //--------------------------------------------------
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

    public boolean getIsAll() {
        return isAll;
    }

    public String getChangeCashAccCode() {
        return changeCashAccCode;
    }

    public void setChangeCashAccCode(String changeCashAccCode) {
        this.changeCashAccCode = changeCashAccCode;
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

    public String getCashAccCode() {
        return cashAccCode;
    }

    public String getCatCode() {
        return catCode;
    }

    public String getCashAccName() {
        return cashAccName;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public String getCatName() {
        return catName;
    }

    public String getSubTsfTypeName() {
        return SubTsfTypeName;
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

    public Date getDDate() {
        return dDate;
    }

    public Date getMatureDate() {
        return mDate;
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

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public void setCashAccName(String cashAccName) {
        this.cashAccName = cashAccName;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setSubTsfTypeName(String SubTsfTypeName) {
        this.SubTsfTypeName = SubTsfTypeName;
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

    public void setMatureDate(Date mDate) {
        this.mDate = mDate;
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

    public void setPortMoney(double portMoney) {
        this.portMoney = portMoney;
    }

    public void setBaseMoney(double baseMoney) {
        this.baseMoney = baseMoney;
    }

    public void setMDate(Date mDate) {
        this.mDate = mDate;
    }

    public void setBalMoney(double balMoney) {
        this.balMoney = balMoney;
    }

    public void setLx(double lx) {
        this.lx = lx;
    }

    public void setNumType(String numType) {
        this.numType = numType;
    }

    public String getPortName() {
        return portName;
    }

    public double getPortMoney() {
        return portMoney;
    }

    public double getBaseMoney() {
        return baseMoney;
    }

    public Date getMDate() {
        return mDate;
    }

    public double getBalMoney() {
        return balMoney;
    }

    public double getLx() {
        return lx;
    }

    public String getNumType() {
        return numType;
    }

    public AccPaid(YssPub pub) {
        this.setYssPub(pub);
    }

    public AccPaid() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        //fanghaoln 090526  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 增加组合群代码和组合群名称 这里加的是get的后面部分
        return this.autoBuildRowStr(
            "DDate\tPortCode\tPortName\tInvmgrCode\tInvmgrName\tCatCode\tCatName\t" +
            "TsfTypeCode\tTsfTypeName\tSubTsfTypeCode\tSubTsfTypeName\tCuryCode\tCuryName\t" +
            "CashAccCode\tCashAccName\tMoney;#,##0.####\tBaseMoney;#,##0.####\tPortMoney;#,##0.####\t" +
            "BaseCuryRate\tPortCuryRate\tMatureDate\tAssetGroupCode\tAssetGroupName\tChangeCashAccCode\tChangeCashAccName\tLx\t"); 
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

//        boolean analy1; 无用 删除 sunkey@Delete 20090904
//        boolean analy2;
//        boolean analy3;

        try {
            if (sRowStr != null && sRowStr.length() > 0) {
                //添加组合代码、组合群名称，同时调整长度为23为自动付息 fanghao 20090903
                if ( (sRowStr.split("\t")).length == 23) { //当是自动付息时,所有的数据是在后台生成的.它的解析数据与前台的不同,与xml中的数据相同。所以需要两种解析方式。sj edit 20080630
                    this.autoParseRowStr(
                        "DDate\tPortCode\tPortName\tInvmgrCode\tInvmgrName\tCatCode\tCatName\t" +
                        "TsfTypeCode\tTsfTypeName\tSubTsfTypeCode\tSubTsfTypeName\tCuryCode\tCuryName\t" +
                        "CashAccCode\tCashAccName\tMoney\tBaseMoney\tPortMoney\tBaseCuryRate\tPortCuryRate\tMDate\tAssetGroupCode\tAssetGroupName",
                        sRowStr);

                } else {
                    this.autoParseRowStr(
                        "DDate\tPortCode\tPortName\tInvmgrCode\tInvmgrName\tCatCode\tCatName\t" +
                        "TsfTypeCode\tTsfTypeName\tSubTsfTypeCode\tSubTsfTypeName\tCuryCode\tCuryName\t" +
                        "CashAccCode\tCashAccName\tChangeCashAccCode\tMoney\tBaseMoney\tPortMoney\tBaseCuryRate\tPortCuryRate\tBalMoney\tLx\tMDate",//edit by yanghaiming 20100416 MS00997  QDV4建行2010年02月23日01_B 增加mdate为业务日期
                        sRowStr);
                }
            }
        } catch (Exception e) {
            throw new YssException(e);
        }
    }
}
