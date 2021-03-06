package com.yss.main.accbook;

import java.sql.*;
import java.util.*;
import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.main.dao.*;
import java.util.HashMap;
import com.yss.pojo.sys.YssMapAdmin;

public class SecAccBookBean
    extends BaseReportBean implements IClientReportView, IKey {

    private String accBookDefine = ""; //台帐定义
    private String accBookLink = ""; //查询链接
    private String accBookDefineName = ""; //台帐定义名称
    private int settleType = 0;
    private String settleMark = "";
    private String subNum = "";
    private String accBookDefineAry[] = null;
    private String accBookDefineNameAry[] = null;
    private String accBookLinkAry[] = null;
    private String keyCode = "";
    private String keyName = "";
    private String curyCode = "";
    private String checkItems = "";
    private String settleDate = "";
    private String baseCuryCode = "";
    private int cashInd;

    private double inPutAmount; //流入数量
    private double totalInPutAmount; //累计流入数量
    private double outPutAmount; //流出数量
    private double totalOutPutAmount; //累计流出数量
    private double beginAmount; //期初数量
    private double totalBeginAmount; //累计期初数量
    private double Amount; //期末数量
    private double totalAmount; //累计期末数量
    private double unSettledAmount; //未清算数量
    private double totalUnSettledAmount; //累计未清算数量

    private double inPutTMoney; //流入（原币，买入金额）
    private double baseInPutTMoney; //基础货币流入（买入金额）
    private double totalInPutTMoney; //累计流入（原币，买入金额）
    private double totalBaseInPutTMoney; //基础货币累计流入（买入金额）

    private double outPutTMoney; //流出（原币，卖出金额）
    private double baseOutPutTMoney; //基础货币流出（卖出金额）
    private double totalOutPutTMoney; //累计流出（原币，卖出金额）
    private double totalBaseOutPutTMoney; //基础货币累计流出（卖出金额）

    private double inPutMoney; //流入（原币，核算成本）
    private double inPutMMoney; //流入（原币，管理成本）
    private double inPutVMoney; //流入（原币，估值成本）
    private double baseInPutMoney; //基础货币流入（核算成本）
    private double baseInPutMMoney; //基础货币流入（管理成本）
    private double baseInPutVMoney; //基础货币流入（估值成本）

    private double totalInPutMoney; //累计流入（原币，核算成本）
    private double totalInPutMMoney; //累计流入（原币，管理成本）
    private double totalInPutVMoney; //累计流入（原币，估值成本）
    private double totalBaseInPutMoney; //基础货币累计流入（核算成本）
    private double totalBaseInPutMMoney; //基础货币累计流入（管理成本）
    private double totalBaseInPutVMoney; //基础货币累计流入（估值成本）

    private double outPutMoney; //流出（原币，核算成本）
    private double outPutMMoney; //流出（原币，管理成本）
    private double outPutVMoney; //流出（原币，估值成本）
    private double baseOutPutMoney; //基础货币流出（核算成本）
    private double baseOutPutMMoney; //基础货币流出（管理成本）
    private double baseOutPutVMoney; //基础货币流出（估值成本）

    private double totalOutPutMoney; //累计流出（原币，核算成本）
    private double totalOutPutMMoney; //累计流出（原币，管理成本）
    private double totalOutPutVMoney; //累计流出（原币，估值成本）
    private double totalBaseOutPutMoney; //基础货币累计流出（核算成本）
    private double totalBaseOutPutMMoney; //基础货币累计流出（管理成本）
    private double totalBaseOutPutVMoney; //基础货币累计流出（估值成本）

    private double beginBalance; //期初余额（原币，核算成本）
    private double beginMBalance; //期初余额（原币，管理成本）
    private double beginVBalance; //期初余额（原币，估值成本）
    private double beginBaseBalance; //基础货币期初余额（核算成本）
    private double beginBaseMBalance; //基础货币期初余额（管理成本）
    private double beginBaseVBalance; //基础货币期初余额（估值成本）

    private double totalBeginBalance; //累计期初余额（原币，核算成本）
    private double totalBeginMBalance; //累计期初余额（原币，管理成本）
    private double totalBeginVBalance; //累计期初余额（原币，估值成本）
    private double totalBeginBaseBalance; //基础货币累计期初余额（核算成本）
    private double totalBeginBaseMBalance; //基础货币累计期初余额（管理成本）
    private double totalBeginBaseVBalance; //基础货币累计期初余额（估值成本）

    private double Balance; //期末余额（原币，核算成本）
    private double MBalance; //期末余额（原币，管理成本）
    private double VBalance; //期末余额（原币，估值成本）
    private double BaseBalance; //基础货币期末余额（核算成本）
    private double BaseMBalance; //基础货币期末余额（管理成本）
    private double BaseVBalance; //基础货币期末余额（估值成本）

    private double totalBalance; //累计期末余额（原币，核算成本）
    private double totalMBalance; //累计期末余额（原币，管理成本）
    private double totalVBalance; //累计期末余额（原币，估值成本）
    private double totalBaseBalance; //基础货币累计期末余额（核算成本）
    private double totalBaseMBalance; //基础货币累计期末余额（管理成本）
    private double totalBaseVBalance; //基础货币累计期末余额（估值成本）

    private double unSettledMoney; //未清算金额（原币，核算成本）
    private double mUnSettledMoney; //未清算金额（原币，管理成本）
    private double vUnSettledMoney; //未清算金额（原币，估值成本）
    private double baseUnSettledMoney; //基础货币未清算金额（核算成本）
    private double baseMUnSettledMoney; //基础货币未清算金额（管理成本）
    private double baseVUnSettledMoney; //基础货币未清算金额（估值成本）

    private double totalUnSettledMoney; //累计未清算金额（原币，核算成本）
    private double totalMUnSettledMoney; //累计未清算金额（原币，管理成本）
    private double totalVUnSettledMoney; //累计未清算金额（原币，估值成本）
    private double totalBaseUnSettledMoney; //基础货币累计未清算金额（核算成本）
    private double totalBaseMUnSettledMoney; //基础货币累计未清算金额（管理成本）
    private double totalBaseVUnSettledMoney; //基础货币累计未清算金额（估值成本）

    private double exchangeInDec; //汇兑损益
    private double totalExchangeInDec; //累计汇兑损益
    private double exchangeCostInDec; //汇兑损益（核算成本）
    private double totalExchangeCostInDec; //累计汇兑损益（核算成本）
    private double exchangeMCostInDec; //汇兑损益（管理成本）
    private double totalExchangeMCostInDec; //累计汇兑损益（管理成本）
    private double exchangeVCostInDec; //汇兑损益（估值成本）
    private double totalExchangeVCostInDec; //累计汇兑损益（估值成本）
    private double todayRate; //最新汇率

    private double vaMoney; //估值增值（原币，核算成本）
    private double mVaMoney; //估值增值（原币，管理成本）
    private double vVaMoney; //估值增值（原币，估值成本）
    private double baseVaMoney; //基础货币估值增值（核算成本）
    private double baseMVaMoney; //基础货币估值增值（管理成本）
    private double baseVVaMoney; //基础货币估值增值（估值成本）

    private double totalVaMoney; //累计估值增值（原币，核算成本）
    private double totalMVaMoney; //累计估值增值（原币，管理成本）
    private double totalVVaMoney; //累计估值增值（原币，估值成本）
    private double totalBaseVaMoney; //基础货币累计估值增值（核算成本）
    private double totalBaseMVaMoney; //基础货币累计估值增值（管理成本）
    private double totalBaseVVaMoney; //基础货币累计估值增值（估值成本）

    private double mvMoney; //市值（原币，核算成本）
    private double mMvMoney; //市值（原币，管理成本）
    private double vMvMoney; //市值（原币，估值成本）
    private double baseMvMoney; //基础货币市值（核算成本）
    private double baseMMvMoney; //基础货币市值（管理成本）
    private double baseVMvMoney; //基础货币市值（估值成本）

    private double totalMvMoney; //累计市值（原币，核算成本）
    private double totalMMvMoney; //累计市值（原币，管理成本）
    private double totalVMvMoney; //累计市值（原币，估值成本）
    private double totalBaseMvMoney; //基础货币累计市值（核算成本）
    private double totalBaseMMvMoney; //基础货币累计市值（管理成本）
    private double totalBaseVMvMoney; //基础货币累计市值（估值成本）

    private String reportType = "";
    private java.util.Date beginDate;
    private java.util.Date endDate;
    public java.util.Date getEndDate() {
        return endDate;
    }

    public String getAccBookLink() {
        return accBookLink;
    }

    public java.util.Date getBeginDate() {
        return beginDate;
    }

    public void setAccBookDefine(String accBookDefine) {
        this.accBookDefine = accBookDefine;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    public void setAccBookLink(String accBookLink) {
        this.accBookLink = accBookLink;
    }

    public void setBeginDate(java.util.Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setTotalBaseInPutMoney(double totalBaseInPutMoney) {
        this.totalBaseInPutMoney = totalBaseInPutMoney;
    }

    public void setAccBookDefineNameAry(String[] accBookDefineNameAry) {
        this.accBookDefineNameAry = accBookDefineNameAry;
    }

    public void setAccBookLinkAry(String[] accBookLinkAry) {
        this.accBookLinkAry = accBookLinkAry;
    }

    public void setBaseCuryCode(String baseCuryCode) {
        this.baseCuryCode = baseCuryCode;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public void setOutPutMoney(double outPutMoney) {
        this.outPutMoney = outPutMoney;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setInPutMoney(double inPutMoney) {
        this.inPutMoney = inPutMoney;
    }

    public void setinPutMMoney(double inPutMMoney) {
        this.inPutMMoney = inPutMMoney;
    }

    public void setTotalBaseOutPutMoney(double totalBaseOutPutMoney) {
        this.totalBaseOutPutMoney = totalBaseOutPutMoney;
    }

    public void setBaseInPutMoney(double baseInPutMoney) {
        this.baseInPutMoney = baseInPutMoney;
    }

    public void setBaseOutPutMoney(double baseOutPutMoney) {
        this.baseOutPutMoney = baseOutPutMoney;
    }

    public void setTotalOutPutMoney(double totalOutPutMoney) {
        this.totalOutPutMoney = totalOutPutMoney;
    }

    public void setAccBookDefineName(String accBookDefineName) {
        this.accBookDefineName = accBookDefineName;
    }

    public void setAccBookDefineAry(String[] accBookDefineAry) {
        this.accBookDefineAry = accBookDefineAry;
    }

    public void setCashInd(int cashInd) {
        this.cashInd = cashInd;
    }

    public void setTotalInPutMoney(double totalInPutMoney) {
        this.totalInPutMoney = totalInPutMoney;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public void setSettleType(int settleType) {
        this.settleType = settleType;
    }

    public void setSettleMark(String settleMark) {
        this.settleMark = settleMark;
    }

    public void setBaseInPutMMoney(double baseInPutMMoney) {
        this.baseInPutMMoney = baseInPutMMoney;
    }

    public void setTotalBeginMBalance(double totalBeginMBalance) {
        this.totalBeginMBalance = totalBeginMBalance;
    }

    public void setTotalInPutMMoney(double totalInPutMMoney) {
        this.totalInPutMMoney = totalInPutMMoney;
    }

    public void setBeginBalance(double beginBalance) {
        this.beginBalance = beginBalance;
    }

    public void setTotalBeginBaseMBalance(double totalBeginBaseMBalance) {
        this.totalBeginBaseMBalance = totalBeginBaseMBalance;
    }

    public void setTotalMBalance(double totalMBalance) {
        this.totalMBalance = totalMBalance;
    }

    public void setBeginBaseBalance(double beginBaseBalance) {
        this.beginBaseBalance = beginBaseBalance;
    }

    public void setBeginBaseMBalance(double beginBaseMBalance) {
        this.beginBaseMBalance = beginBaseMBalance;
    }

    public void setBaseOutPutMMoney(double baseOutPutMMoney) {
        this.baseOutPutMMoney = baseOutPutMMoney;
    }

    public void setTotalBaseOutPutMMoney(double totalBaseOutPutMMoney) {
        this.totalBaseOutPutMMoney = totalBaseOutPutMMoney;
    }

    public void setOutPutMMoney(double outPutMMoney) {
        this.outPutMMoney = outPutMMoney;
    }

    public void setTotalBaseInPutMMoney(double totalBaseInPutMMoney) {
        this.totalBaseInPutMMoney = totalBaseInPutMMoney;
    }

    public void setTotalBeginBalance(double totalBeginBalance) {
        this.totalBeginBalance = totalBeginBalance;
    }

    public void setTotalBaseBalance(double totalBaseBalance) {
        this.totalBaseBalance = totalBaseBalance;
    }

    public void setBaseMBalance(double BaseMBalance) {
        this.BaseMBalance = BaseMBalance;
    }

    public void setBalance(double Balance) {
        this.Balance = Balance;
    }

    public void setMBalance(double MBalance) {
        this.MBalance = MBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public void setTotalBaseMBalance(double totalBaseMBalance) {
        this.totalBaseMBalance = totalBaseMBalance;
    }

    public void setInPutMMoney(double inPutMMoney) {
        this.inPutMMoney = inPutMMoney;
    }

    public void setBaseBalance(double BaseBalance) {
        this.BaseBalance = BaseBalance;
    }

    public void setTotalBeginBaseBalance(double totalBeginBaseBalance) {
        this.totalBeginBaseBalance = totalBeginBaseBalance;
    }

    public void setBeginMBalance(double beginMBalance) {
        this.beginMBalance = beginMBalance;
    }

    public void setTotalOutPutMMoney(double totalOutPutMMoney) {
        this.totalOutPutMMoney = totalOutPutMMoney;
    }

    public void setInPutAmount(double inPutAmount) {
        this.inPutAmount = inPutAmount;
    }

    public void setOutPutAmount(double outPutAmount) {
        this.outPutAmount = outPutAmount;
    }

    public void setAmount(double Amount) {
        this.Amount = Amount;
    }

    public void setBeginAmount(double beginAmount) {
        this.beginAmount = beginAmount;
    }

    public void setTotalInPutAmount(double totalInPutAmount) {
        this.totalInPutAmount = totalInPutAmount;
    }

    public void setTotalOutPutAmount(double totalOutPutAmount) {
        this.totalOutPutAmount = totalOutPutAmount;
    }

    public void setTotalBeginAmount(double totalBeginAmount) {
        this.totalBeginAmount = totalBeginAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setSubNum(String subNum) {
        this.subNum = subNum;
    }

    public void setTodayRate(double todayRate) {
        this.todayRate = todayRate;
    }

    public void setTotalBaseUnSettledMoney(double totalBaseUnSettledMoney) {
        this.totalBaseUnSettledMoney = totalBaseUnSettledMoney;
    }

    public void setTotalUnSettledMoney(double totalUnSettledMoney) {
        this.totalUnSettledMoney = totalUnSettledMoney;
    }

    public void setBaseMUnSettledMoney(double baseMUnSettledMoney) {
        this.baseMUnSettledMoney = baseMUnSettledMoney;
    }

    public void setUnSettledMoney(double unSettledMoney) {
        this.unSettledMoney = unSettledMoney;
    }

    public void setTotalBaseMUnSettledMoney(double totalBaseMUnSettledMoney) {
        this.totalBaseMUnSettledMoney = totalBaseMUnSettledMoney;
    }

    public void setBaseUnSettledMoney(double baseUnSettledMoney) {
        this.baseUnSettledMoney = baseUnSettledMoney;
    }

    public void setTotalMUnSettledMoney(double totalMUnSettledMoney) {
        this.totalMUnSettledMoney = totalMUnSettledMoney;
    }

    public void setMUnSettledMoney(double mUnSettledMoney) {
        this.mUnSettledMoney = mUnSettledMoney;
    }

    public void setExchangeInDec(double exchangeInDec) {
        this.exchangeInDec = exchangeInDec;
    }

    public void setUnSettledAmount(double unSettledAmount) {
        this.unSettledAmount = unSettledAmount;
    }

    public void setTotalUnSettledAmount(double totalUnSettledAmount) {
        this.totalUnSettledAmount = totalUnSettledAmount;
    }

    public void setTotalExchangeInDec(double totalExchangeInDec) {
        this.totalExchangeInDec = totalExchangeInDec;
    }

    public void setExchangeMCostInDec(double exchangeMCostInDec) {
        this.exchangeMCostInDec = exchangeMCostInDec;
    }

    public void setTotalBaseInPutTMoney(double totalBaseInPutTMoney) {
        this.totalBaseInPutTMoney = totalBaseInPutTMoney;
    }

    public void setTotalInPutTMoney(double totalInPutTMoney) {
        this.totalInPutTMoney = totalInPutTMoney;
    }

    public void setTotalOutPutTMoney(double totalOutPutTMoney) {
        this.totalOutPutTMoney = totalOutPutTMoney;
    }

    public void setTotalExchangeCostInDec(double totalExchangeCostInDec) {
        this.totalExchangeCostInDec = totalExchangeCostInDec;
    }

    public void setTotalBaseOutPutTMoney(double totalBaseOutPutTMoney) {
        this.totalBaseOutPutTMoney = totalBaseOutPutTMoney;
    }

    public void setExchangeCostInDec(double exchangeCostInDec) {
        this.exchangeCostInDec = exchangeCostInDec;
    }

    public void setBaseOutPutTMoney(double baseOutPutTMoney) {
        this.baseOutPutTMoney = baseOutPutTMoney;
    }

    public void setInPutTMoney(double inPutTMoney) {
        this.inPutTMoney = inPutTMoney;
    }

    public void setTotalExchangeMCostInDec(double totalExchangeMCostInDec) {
        this.totalExchangeMCostInDec = totalExchangeMCostInDec;
    }

    public void setOutPutTMoney(double outPutTMoney) {
        this.outPutTMoney = outPutTMoney;
    }

    public void setBaseInPutTMoney(double baseInPutTMoney) {
        this.baseInPutTMoney = baseInPutTMoney;
    }

    public void setOutPutVMoney(double outPutVMoney) {
        this.outPutVMoney = outPutVMoney;
    }

    public void setExchangeVCostInDec(double exchangeVCostInDec) {
        this.exchangeVCostInDec = exchangeVCostInDec;
    }

    public void setTotalBaseOutPutVMoney(double totalBaseOutPutVMoney) {
        this.totalBaseOutPutVMoney = totalBaseOutPutVMoney;
    }

    public void setTotalVUnSettledMoney(double totalVUnSettledMoney) {
        this.totalVUnSettledMoney = totalVUnSettledMoney;
    }

    public void setBeginBaseVBalance(double beginBaseVBalance) {
        this.beginBaseVBalance = beginBaseVBalance;
    }

    public void setBaseVBalance(double BaseVBalance) {
        this.BaseVBalance = BaseVBalance;
    }

    public void setVBalance(double VBalance) {
        this.VBalance = VBalance;
    }

    public void setTotalBaseVUnSettledMoney(double totalBaseVUnSettledMoney) {
        this.totalBaseVUnSettledMoney = totalBaseVUnSettledMoney;
    }

    public void setTotalBaseVBalance(double totalBaseVBalance) {
        this.totalBaseVBalance = totalBaseVBalance;
    }

    public void setTotalBeginBaseVBalance(double totalBeginBaseVBalance) {
        this.totalBeginBaseVBalance = totalBeginBaseVBalance;
    }

    public void setTotalVBalance(double totalVBalance) {
        this.totalVBalance = totalVBalance;
    }

    public void setTotalOutPutVMoney(double totalOutPutVMoney) {
        this.totalOutPutVMoney = totalOutPutVMoney;
    }

    public void setTotalBaseInPutVMoney(double totalBaseInPutVMoney) {
        this.totalBaseInPutVMoney = totalBaseInPutVMoney;
    }

    public void setBaseInPutVMoney(double baseInPutVMoney) {
        this.baseInPutVMoney = baseInPutVMoney;
    }

    public void setTotalInPutVMoney(double totalInPutVMoney) {
        this.totalInPutVMoney = totalInPutVMoney;
    }

    public void setBaseOutPutVMoney(double baseOutPutVMoney) {
        this.baseOutPutVMoney = baseOutPutVMoney;
    }

    public void setInPutVMoney(double inPutVMoney) {
        this.inPutVMoney = inPutVMoney;
    }

    public void setVUnSettledMoney(double vUnSettledMoney) {
        this.vUnSettledMoney = vUnSettledMoney;
    }

    public void setTotalExchangeVCostInDec(double totalExchangeVCostInDec) {
        this.totalExchangeVCostInDec = totalExchangeVCostInDec;
    }

    public void setTotalBeginVBalance(double totalBeginVBalance) {
        this.totalBeginVBalance = totalBeginVBalance;
    }

    public void setBeginVBalance(double beginVBalance) {
        this.beginVBalance = beginVBalance;
    }

    public void setBaseVUnSettledMoney(double baseVUnSettledMoney) {
        this.baseVUnSettledMoney = baseVUnSettledMoney;
    }

    public void setCheckItems(String checkItems) {
        this.checkItems = checkItems;
    }

    public void setTotalMMvMoney(double totalMMvMoney) {
        this.totalMMvMoney = totalMMvMoney;
    }

    public void setMvMoney(double mvMoney) {
        this.mvMoney = mvMoney;
    }

    public void setBaseMMvMoney(double baseMMvMoney) {
        this.baseMMvMoney = baseMMvMoney;
    }

    public void setTotalMvMoney(double totalMvMoney) {
        this.totalMvMoney = totalMvMoney;
    }

    public void setBaseMvMoney(double baseMvMoney) {
        this.baseMvMoney = baseMvMoney;
    }

    public void setMMvMoney(double mMvMoney) {
        this.mMvMoney = mMvMoney;
    }

    public void setTotalBaseMvMoney(double totalBaseMvMoney) {
        this.totalBaseMvMoney = totalBaseMvMoney;
    }

    public void setTotalBaseMMvMoney(double totalBaseMMvMoney) {
        this.totalBaseMMvMoney = totalBaseMMvMoney;
    }

    public void setTotalVMvMoney(double totalVMvMoney) {
        this.totalVMvMoney = totalVMvMoney;
    }

    public void setBaseVMvMoney(double baseVMvMoney) {
        this.baseVMvMoney = baseVMvMoney;
    }

    public void setTotalBaseVMvMoney(double totalBaseVMvMoney) {
        this.totalBaseVMvMoney = totalBaseVMvMoney;
    }

    public void setVMvMoney(double vMvMoney) {
        this.vMvMoney = vMvMoney;
    }

    public void setBaseVVaMoney(double baseVVaMoney) {
        this.baseVVaMoney = baseVVaMoney;
    }

    public void setVVaMoney(double vVaMoney) {
        this.vVaMoney = vVaMoney;
    }

    public void setMVaMoney(double mVaMoney) {
        this.mVaMoney = mVaMoney;
    }

    public void setTotalMVaMoney(double totalMVaMoney) {
        this.totalMVaMoney = totalMVaMoney;
    }

    public void setTotalBaseVaMoney(double totalBaseVaMoney) {
        this.totalBaseVaMoney = totalBaseVaMoney;
    }

    public void setTotalBaseMVaMoney(double totalBaseMVaMoney) {
        this.totalBaseMVaMoney = totalBaseMVaMoney;
    }

    public void setTotalBaseVVaMoney(double totalBaseVVaMoney) {
        this.totalBaseVVaMoney = totalBaseVVaMoney;
    }

    public void setBaseMVaMoney(double baseMVaMoney) {
        this.baseMVaMoney = baseMVaMoney;
    }

    public void setTotalVaMoney(double totalVaMoney) {
        this.totalVaMoney = totalVaMoney;
    }

    public void setTotalVVaMoney(double totalVVaMoney) {
        this.totalVVaMoney = totalVVaMoney;
    }

    public void setBaseVaMoney(double baseVaMoney) {
        this.baseVaMoney = baseVaMoney;
    }

    public void setVaMoney(double vaMoney) {
        this.vaMoney = vaMoney;
    }

    public String getAccBookDefine() {
        return accBookDefine;
    }

    public double getTotalBaseInPutMoney() {
        return totalBaseInPutMoney;
    }

    public String[] getAccBookDefineNameAry() {
        return accBookDefineNameAry;
    }

    public String[] getAccBookLinkAry() {
        return accBookLinkAry;
    }

    public String getBaseCuryCode() {
        return baseCuryCode;
    }

    public String getReportType() {
        return reportType;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public double getOutPutMoney() {
        return outPutMoney;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public double getInPutMoney() {
        return inPutMoney;
    }

    public double getInPutMMoney() {
        return inPutMMoney;
    }

    public double getTotalBaseOutPutMoney() {
        return totalBaseOutPutMoney;
    }

    public double getBaseInPutMoney() {
        return baseInPutMoney;
    }

    public double getBaseOutPutMoney() {
        return baseOutPutMoney;
    }

    public double getTotalOutPutMoney() {
        return totalOutPutMoney;
    }

    public String getAccBookDefineName() {
        return accBookDefineName;
    }

    public String[] getAccBookDefineAry() {
        return accBookDefineAry;
    }

    public int getCashInd() {
        return cashInd;
    }

    public double getTotalInPutMoney() {
        return totalInPutMoney;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public int getSettleType() {
        return settleType;
    }

    public String getSettleMark() {
        return settleMark;
    }

    public double getBaseInPutMMoney() {
        return baseInPutMMoney;
    }

    public double getTotalBeginMBalance() {
        return totalBeginMBalance;
    }

    public double getTotalInPutMMoney() {
        return totalInPutMMoney;
    }

    public double getBeginBalance() {
        return beginBalance;
    }

    public double getTotalBeginBaseMBalance() {
        return totalBeginBaseMBalance;
    }

    public double getTotalMBalance() {
        return totalMBalance;
    }

    public double getBeginBaseBalance() {
        return beginBaseBalance;
    }

    public double getBeginBaseMBalance() {
        return beginBaseMBalance;
    }

    public double getBaseOutPutMMoney() {
        return baseOutPutMMoney;
    }

    public double getTotalBaseOutPutMMoney() {
        return totalBaseOutPutMMoney;
    }

    public double getOutPutMMoney() {
        return outPutMMoney;
    }

    public double getTotalBaseInPutMMoney() {
        return totalBaseInPutMMoney;
    }

    public double getTotalBeginBalance() {
        return totalBeginBalance;
    }

    public double getTotalBaseBalance() {
        return totalBaseBalance;
    }

    public double getBaseMBalance() {
        return BaseMBalance;
    }

    public double getBalance() {
        return Balance;
    }

    public double getMBalance() {
        return MBalance;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public double getTotalBaseMBalance() {
        return totalBaseMBalance;
    }

    public double getBaseBalance() {
        return BaseBalance;
    }

    public double getTotalBeginBaseBalance() {
        return totalBeginBaseBalance;
    }

    public double getBeginMBalance() {
        return beginMBalance;
    }

    public double getTotalOutPutMMoney() {
        return totalOutPutMMoney;
    }

    public double getInPutAmount() {
        return inPutAmount;
    }

    public double getOutPutAmount() {
        return outPutAmount;
    }

    public double getAmount() {
        return Amount;
    }

    public double getBeginAmount() {
        return beginAmount;
    }

    public double getTotalInPutAmount() {
        return totalInPutAmount;
    }

    public double getTotalOutPutAmount() {
        return totalOutPutAmount;
    }

    public double getTotalBeginAmount() {
        return totalBeginAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getSubNum() {
        return subNum;
    }

    public double getTodayRate() {
        return todayRate;
    }

    public double getTotalBaseUnSettledMoney() {
        return totalBaseUnSettledMoney;
    }

    public double getTotalUnSettledMoney() {
        return totalUnSettledMoney;
    }

    public double getBaseMUnSettledMoney() {
        return baseMUnSettledMoney;
    }

    public double getUnSettledMoney() {
        return unSettledMoney;
    }

    public double getTotalBaseMUnSettledMoney() {
        return totalBaseMUnSettledMoney;
    }

    public double getBaseUnSettledMoney() {
        return baseUnSettledMoney;
    }

    public double getTotalMUnSettledMoney() {
        return totalMUnSettledMoney;
    }

    public double getMUnSettledMoney() {
        return mUnSettledMoney;
    }

    public double getExchangeInDec() {
        return exchangeInDec;
    }

    public double getUnSettledAmount() {
        return unSettledAmount;
    }

    public double getTotalUnSettledAmount() {
        return totalUnSettledAmount;
    }

    public double getTotalExchangeInDec() {
        return totalExchangeInDec;
    }

    public double getExchangeMCostInDec() {
        return exchangeMCostInDec;
    }

    public double getTotalBaseInPutTMoney() {
        return totalBaseInPutTMoney;
    }

    public double getTotalInPutTMoney() {
        return totalInPutTMoney;
    }

    public double getTotalOutPutTMoney() {
        return totalOutPutTMoney;
    }

    public double getTotalExchangeCostInDec() {
        return totalExchangeCostInDec;
    }

    public double getTotalBaseOutPutTMoney() {
        return totalBaseOutPutTMoney;
    }

    public double getExchangeCostInDec() {
        return exchangeCostInDec;
    }

    public double getBaseOutPutTMoney() {
        return baseOutPutTMoney;
    }

    public double getInPutTMoney() {
        return inPutTMoney;
    }

    public double getTotalExchangeMCostInDec() {
        return totalExchangeMCostInDec;
    }

    public double getOutPutTMoney() {
        return outPutTMoney;
    }

    public double getBaseInPutTMoney() {
        return baseInPutTMoney;
    }

    public double getOutPutVMoney() {
        return outPutVMoney;
    }

    public double getExchangeVCostInDec() {
        return exchangeVCostInDec;
    }

    public double getTotalBaseOutPutVMoney() {
        return totalBaseOutPutVMoney;
    }

    public double getTotalVUnSettledMoney() {
        return totalVUnSettledMoney;
    }

    public double getBeginBaseVBalance() {
        return beginBaseVBalance;
    }

    public double getBaseVBalance() {
        return BaseVBalance;
    }

    public double getVBalance() {
        return VBalance;
    }

    public double getTotalBaseVUnSettledMoney() {
        return totalBaseVUnSettledMoney;
    }

    public double getTotalBaseVBalance() {
        return totalBaseVBalance;
    }

    public double getTotalBeginBaseVBalance() {
        return totalBeginBaseVBalance;
    }

    public double getTotalVBalance() {
        return totalVBalance;
    }

    public double getTotalOutPutVMoney() {
        return totalOutPutVMoney;
    }

    public double getTotalBaseInPutVMoney() {
        return totalBaseInPutVMoney;
    }

    public double getBaseInPutVMoney() {
        return baseInPutVMoney;
    }

    public double getTotalInPutVMoney() {
        return totalInPutVMoney;
    }

    public double getBaseOutPutVMoney() {
        return baseOutPutVMoney;
    }

    public double getInPutVMoney() {
        return inPutVMoney;
    }

    public double getVUnSettledMoney() {
        return vUnSettledMoney;
    }

    public double getTotalExchangeVCostInDec() {
        return totalExchangeVCostInDec;
    }

    public double getTotalBeginVBalance() {
        return totalBeginVBalance;
    }

    public double getBeginVBalance() {
        return beginVBalance;
    }

    public double getBaseVUnSettledMoney() {
        return baseVUnSettledMoney;
    }

    public String getCheckItems() {
        return checkItems;
    }

    public double getTotalMMvMoney() {
        return totalMMvMoney;
    }

    public double getMvMoney() {
        return mvMoney;
    }

    public double getBaseMMvMoney() {
        return baseMMvMoney;
    }

    public double getTotalMvMoney() {
        return totalMvMoney;
    }

    public double getBaseMvMoney() {
        return baseMvMoney;
    }

    public double getMMvMoney() {
        return mMvMoney;
    }

    public double getTotalBaseMvMoney() {
        return totalBaseMvMoney;
    }

    public double getTotalBaseMMvMoney() {
        return totalBaseMMvMoney;
    }

    public double getTotalVMvMoney() {
        return totalVMvMoney;
    }

    public double getBaseVMvMoney() {
        return baseVMvMoney;
    }

    public double getTotalBaseVMvMoney() {
        return totalBaseVMvMoney;
    }

    public double getVMvMoney() {
        return vMvMoney;
    }

    public double getBaseVVaMoney() {
        return baseVVaMoney;
    }

    public double getVVaMoney() {
        return vVaMoney;
    }

    public double getMVaMoney() {
        return mVaMoney;
    }

    public double getTotalMVaMoney() {
        return totalMVaMoney;
    }

    public double getTotalBaseVaMoney() {
        return totalBaseVaMoney;
    }

    public double getTotalBaseMVaMoney() {
        return totalBaseMVaMoney;
    }

    public double getTotalBaseVVaMoney() {
        return totalBaseVVaMoney;
    }

    public double getBaseMVaMoney() {
        return baseMVaMoney;
    }

    public double getTotalVaMoney() {
        return totalVaMoney;
    }

    public double getTotalVVaMoney() {
        return totalVVaMoney;
    }

    public double getBaseVaMoney() {
        return baseVaMoney;
    }

    public double getVaMoney() {
        return vaMoney;
    }

    public SecAccBookBean() {
    }

    /**
     * parseRowStr
     * 解析证券台帐请求
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            reqAry = sRowStr.split("\t");
            this.accBookDefine = reqAry[0];
            this.accBookLink = reqAry[1];
            this.accBookDefineName = reqAry[2].replaceAll("->", "\f");
            this.beginDate = YssFun.toDate(reqAry[3]);
            this.endDate = YssFun.toDate(reqAry[4]);
            if (reqAry[5].length() > 0) {
                this.settleType = Integer.parseInt(reqAry[5]);
            }
            this.checkItems = reqAry[6];

            this.accBookDefineAry = this.accBookDefine.split(";");
            this.accBookLinkAry = this.accBookLink.split("\f");
            this.accBookDefineNameAry = this.accBookDefineName.split("\f");
            //设置报表类型标志
            if (accBookDefineAry != null && accBookLinkAry != null) {
                if (accBookDefineAry.length > accBookLinkAry.length - 1) {
                    this.reportType = "sum";
                } else {
                    this.reportType = "detail";
                }
            }
        } catch (Exception e) {
            throw new YssException("解析证券台帐请求出错", e);
        }
    }

    /**
     * buildRowStr
     * 返回BGrid中一行数据
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        if (reportType.equalsIgnoreCase("sum")) {
            //计算期末余额与可用头寸
            this.Amount = YssD.sub(YssD.add(this.beginAmount,
                                            this.inPutAmount), this.outPutAmount);
            this.VBalance = YssD.sub(YssD.add(this.beginVBalance,
                                              this.inPutVMoney), this.outPutVMoney);
            this.MBalance = YssD.sub(YssD.add(this.beginMBalance,
                                              this.inPutMMoney), this.outPutMMoney);
            this.Balance = YssD.sub(YssD.add(this.beginBalance,
                                             this.inPutMoney), this.outPutMoney);

            this.beginBaseMBalance = YssD.mul(this.beginMBalance, todayRate);
            this.baseInPutMMoney = YssD.mul(this.inPutMMoney, todayRate);
            this.baseOutPutMMoney = YssD.mul(this.outPutMMoney, todayRate);
            this.baseMUnSettledMoney = YssD.mul(this.mUnSettledMoney, todayRate);
            this.BaseMBalance = YssD.sub(YssD.add(this.beginBaseMBalance,
                                                  this.baseInPutMMoney),
                                         this.baseOutPutMMoney);

            this.beginBaseBalance = YssD.mul(this.beginBalance, todayRate);
            this.baseInPutMoney = YssD.mul(this.inPutMoney, todayRate);
            this.baseOutPutMoney = YssD.mul(this.outPutMoney, todayRate);
            this.baseUnSettledMoney = YssD.mul(this.unSettledMoney, todayRate);
            this.BaseBalance = YssD.sub(YssD.add(this.beginBaseBalance,
                                                 this.baseInPutMoney),
                                        this.baseOutPutMoney);

            this.beginBaseVBalance = YssD.mul(this.beginVBalance, todayRate);
            this.baseInPutVMoney = YssD.mul(this.inPutVMoney, todayRate);
            this.baseOutPutVMoney = YssD.mul(this.outPutVMoney, todayRate);
            this.baseVUnSettledMoney = YssD.mul(this.vUnSettledMoney, todayRate);
            this.BaseVBalance = YssD.sub(YssD.add(this.beginBaseVBalance,
                                                  this.baseInPutVMoney),
                                         this.baseOutPutVMoney);

            //算市值
            this.mMvMoney = YssD.add(this.MBalance, this.mVaMoney);
            this.baseMMvMoney = YssD.mul(this.mMvMoney, todayRate);
            this.mvMoney = YssD.add(this.Balance, this.vaMoney);
            this.baseMvMoney = YssD.mul(this.mvMoney, todayRate);
            this.vMvMoney = YssD.add(this.VBalance, this.vVaMoney);
            this.baseVMvMoney = YssD.add(this.vMvMoney, todayRate);

            buf.append(this.keyCode).append("\t");
            buf.append(this.keyName).append("\t");
            buf.append(YssFun.formatNumber(this.beginAmount, "#,##0")).
                append("\t");
            buf.append(YssFun.formatNumber(this.inPutAmount, "#,##0")).
                append("\t");
            buf.append(YssFun.formatNumber(this.outPutAmount, "#,##0")).
                append("\t");
            buf.append(YssFun.formatNumber(this.Amount, "#,##0")).
                append("\t");
            buf.append(YssFun.formatNumber(this.unSettledAmount, "#,##0")).
                append("\t");
            buf.append(this.curyCode).append("\t");
            buf.append(YssFun.formatNumber(this.beginMBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.inPutMMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.outPutMMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.MBalance, "#,##0.00")).
                append("\t");
            //  buf.append(YssFun.formatNumber(this.mMvMoney, "#,##0.00")).append("\t");
            buf.append(YssFun.formatNumber(this.mUnSettledMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.beginBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.inPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.outPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.Balance, "#,##0.00")).
                append("\t");
            //     buf.append(YssFun.formatNumber(this.mvMoney, "#,##0.00")).append("\t");
            buf.append(YssFun.formatNumber(this.unSettledMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.beginVBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.inPutVMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.outPutVMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.VBalance, "#,##0.00")).
                append("\t");
            //  buf.append(YssFun.formatNumber(this.vMvMoney, "#,##0.00")).append("\t");
            buf.append(YssFun.formatNumber(this.vUnSettledMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.mvMoney, "#,##0.00")).append("\t");
            buf.append(this.baseCuryCode).append("\t");
            //buf.append(YssFun.formatNumber(this.mvMoney,"#,##0.00")).append("\t");//----------
            buf.append(YssFun.formatNumber(this.beginBaseMBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseInPutMMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseOutPutMMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.BaseMBalance, "#,##0.00")).
                append("\t");
            //  buf.append(YssFun.formatNumber(this.baseMMvMoney, "#,##0.00")).append(
            //        "\t");
            buf.append(YssFun.formatNumber(this.baseMUnSettledMoney, "#,##0.00")).
                append("\t");
            //====================//
            buf.append(YssFun.formatNumber(this.beginBaseBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseOutPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.BaseBalance, "#,##0.00")).
                append("\t");
            // buf.append(YssFun.formatNumber(this.baseMvMoney, "#,##0.00")).append(
            //       "\t");
            buf.append(YssFun.formatNumber(this.baseUnSettledMoney, "#,##0.00")).
                append("\t");
            //===================//
            buf.append(YssFun.formatNumber(this.beginBaseVBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseInPutVMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseOutPutVMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.BaseVBalance, "#,##0.00")).
                append("\t");
            //   buf.append(YssFun.formatNumber(this.baseVMvMoney, "#,##0.00")).append(
            //         "\t");
            buf.append(YssFun.formatNumber(this.baseVUnSettledMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseMvMoney, "#,##0.00")).append("\t");
//       buf.append(settleMark);
        } else if (reportType.equalsIgnoreCase("detail")) {
            buf.append(this.keyCode).append("\t");
            buf.append(this.keyName).append("\t");
            buf.append(this.settleDate).append("\t");
            buf.append(YssFun.formatNumber(this.inPutAmount, "#,##0")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.outPutAmount, "#,##0")).append(
                "\t");
            buf.append(this.curyCode).append("\t");
            buf.append(YssFun.formatNumber(this.inPutTMoney, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.outPutTMoney, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.inPutMMoney, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.inPutMoney, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.inPutVMoney, "#,##0.00")).append(
                "\t");
            buf.append(this.baseCuryCode).append("\t");
            buf.append(YssFun.formatNumber(this.baseInPutTMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseOutPutTMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseInPutMMoney, "#,##0.00")).
                append(
                    "\t");
            buf.append(YssFun.formatNumber(this.baseInPutMoney, "#,##0.00")).
                append(
                    "\t");
            buf.append(YssFun.formatNumber(this.baseInPutVMoney, "#,##0.00")).
                append(
                    "\t");
            if (this.inPutMoney != 0) {
                this.exchangeInDec = YssD.sub(YssD.mul(this.inPutTMoney, todayRate),
                                              this.baseInPutTMoney);
            }
            if (this.outPutMoney != 0) {
                this.exchangeInDec = YssD.sub(YssD.mul(this.outPutTMoney, todayRate),
                                              this.baseOutPutTMoney);
            }
            this.exchangeCostInDec = YssD.sub(YssD.mul(this.inPutMoney, todayRate),
                                              this.baseInPutMoney);
            this.exchangeMCostInDec = YssD.sub(YssD.mul(this.inPutMMoney,
                todayRate), this.baseInPutMMoney);
            this.exchangeVCostInDec = YssD.sub(YssD.mul(this.inPutVMoney,
                todayRate), this.baseInPutVMoney);
            buf.append(YssFun.formatNumber(this.exchangeInDec, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.exchangeMCostInDec, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.exchangeCostInDec, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.exchangeVCostInDec, "#,##0.00")).
                append("\t");
            buf.append(this.settleMark).append("\t");
            buf.append(this.subNum);
        }
        return buf.toString();
    }

    /**
     * buildRowCalStr
     * 返回合计数据
     * @return String
     */
    public String buildRowCalStr() {
        StringBuffer buf = new StringBuffer();

        if (reportType.equalsIgnoreCase("sum")) {
            buf.append("合计：\t\t");
            buf.append(YssFun.formatNumber(this.totalBeginAmount, "#,##0")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalInPutAmount, "#,##0")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalOutPutAmount, "#,##0")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalAmount, "#,##0")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalUnSettledAmount, "#,##0")).
                append("\t\t");

            buf.append(YssFun.formatNumber(this.totalBeginMBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalInPutMMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalOutPutMMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalMBalance, "#,##0.00")).
                append("\t");
            //buf.append(YssFun.formatNumber(this.totalMMvMoney, "#,##0.00")).append(
            //      "\t");
            buf.append(YssFun.formatNumber(this.totalMUnSettledMoney, "#,##0.00")).
                append("\t");

            buf.append(YssFun.formatNumber(this.totalBeginBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalOutPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBalance, "#,##0.00")).
                append("\t");
            //buf.append(YssFun.formatNumber(this.totalMvMoney, "#,##0.00")).append(
            //      "\t");
            buf.append(YssFun.formatNumber(this.totalUnSettledMoney, "#,##0.00")).
                append("\t");

            buf.append(YssFun.formatNumber(this.totalBeginVBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalInPutVMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalOutPutVMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalVBalance, "#,##0.00")).
                append("\t");
            //buf.append(YssFun.formatNumber(this.totalVMvMoney, "#,##0.00")).append(
            //      "\t");
            buf.append(YssFun.formatNumber(this.totalVUnSettledMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalMvMoney, "#,##0.00")).append(
                "\t\t");

            //================
            buf.append(YssFun.formatNumber(this.totalBeginBaseMBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseInPutMMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseOutPutMMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseMBalance, "#,##0.00")).
                append("\t");
            // buf.append(YssFun.formatNumber(this.totalBaseMMvMoney, "#,##0.00")).
            //     append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseMUnSettledMoney, "#,##0.00")).
                append("\t");

            buf.append(YssFun.formatNumber(this.totalBeginBaseBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseOutPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseBalance, "#,##0.00")).
                append("\t");
            // buf.append(YssFun.formatNumber(this.totalBaseMvMoney, "#,##0.00")).
            //     append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseUnSettledMoney, "#,##0.00")).
                append("\t");

            buf.append(YssFun.formatNumber(this.totalBeginBaseVBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseInPutVMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseOutPutVMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseVBalance, "#,##0.00")).
                append("\t");
            // buf.append(YssFun.formatNumber(this.totalBaseVMvMoney, "#,##0.00")).
            //     append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseVUnSettledMoney, "#,##0.00")).append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseMvMoney, "#,##0.00"))
                .append("\t");

        } else if (reportType.equalsIgnoreCase("detail")) {
            buf.append("合计：\t\t\t");
            buf.append(YssFun.formatNumber(this.totalInPutAmount, "#,##0")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalOutPutAmount, "#,##0")).
                append("\t");
            buf.append("\t");
            buf.append(YssFun.formatNumber(this.totalInPutTMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalOutPutTMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalInPutMMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalInPutVMoney, "#,##0.00")).
                append("\t");
            buf.append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseInPutTMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseOutPutTMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseInPutMMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseInPutVMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalExchangeInDec, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalExchangeMCostInDec,
                                           "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalExchangeCostInDec, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalExchangeVCostInDec,
                                           "#,##0.00")).
                append("\t\t");
        }
        return buf.toString();
    }

    /**
     * getKey
     *
     * @return Object
     */
    public Object getKey() {
        String strHashKey = "";
        if (this.reportType.equalsIgnoreCase("sum")) {
            strHashKey = this.keyCode;
        } else {
            if (this.settleMark.equalsIgnoreCase("0")) {
                strHashKey = "1" + this.subNum;
            } else {
                strHashKey = "0" + this.subNum;
            }
        }
        return strHashKey;
    }

    /**
     * setKey
     *
     * @param obj Object
     */
    public void setKey(Object obj) {
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
     * getReportData
     * 响应获取证券台帐请求
     * @param sReportType String
     * @return String
     */
    public String getReportData(String sReportType) throws YssException {
        String strSql = "";
        HashMap hmResult = new HashMap();
        StringBuffer buf = new StringBuffer();
        IAccBookOper secOper = (IAccBookOper) pub.getOperDealCtx().
            getBean("securitybookoper");
        ResultSet rs = null;
        this.keyCode = "";
        this.curyCode = "";
        try {
            if (sReportType.equalsIgnoreCase("loadacc")) {
                secOper.setYssPub(pub);
                buf.append(getReportHeaders(sReportType)).append(YssCons.
                    YSS_LINESPLITMARK);

                strSql = secOper.getBookSql(this.accBookDefine,
                                            this.accBookLink,
                                            this.beginDate, this.endDate,
                                            this.settleType, this.checkItems);
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    setHaspMapResultAttr(rs, hmResult);
                }
                buf.append(getBGridSetStr(hmResult));
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("获取证券台帐出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getBGridSetStr
     * 获取设置BGrid的返回数据字符串
     * @param hmResult HashMap
     * @return String
     */
    public String getBGridSetStr(HashMap hmResult) throws YssException {
        StringBuffer bufAll = new StringBuffer();
        SecAccBookBean secAccBook = null;
        Iterator iHashResult = null;
        YssMapAdmin mapAdmin = null;
        BaseComparator comparator = new BaseComparator();

        //结果排序
        mapAdmin = new YssMapAdmin(hmResult, comparator);
        iHashResult = mapAdmin.sortMap().iterator();
        this.totalBeginAmount = 0;
        this.totalInPutAmount = 0;
        this.totalOutPutAmount = 0;
        this.totalAmount = 0;
        this.totalUnSettledAmount = 0;

        this.totalBeginMBalance = 0;
        this.totalInPutMMoney = 0;
        this.totalOutPutMMoney = 0;
        this.totalMUnSettledMoney = 0;
        this.totalMBalance = 0;
        this.totalMMvMoney = 0;

        this.totalBeginBalance = 0;
        this.totalInPutMoney = 0;
        this.totalOutPutMoney = 0;
        this.totalUnSettledMoney = 0;
        this.totalBalance = 0;
        this.totalMvMoney = 0;

        this.totalBeginVBalance = 0;
        this.totalInPutVMoney = 0;
        this.totalOutPutVMoney = 0;
        this.totalVUnSettledMoney = 0;
        this.totalVBalance = 0;
        this.totalVMvMoney = 0;

        this.totalBeginBaseMBalance = 0;
        this.totalBaseInPutMMoney = 0;
        this.totalBaseOutPutMMoney = 0;
        this.totalBaseMUnSettledMoney = 0;
        this.totalBaseMBalance = 0;
        this.totalBaseMMvMoney = 0;

        this.totalBeginBaseBalance = 0;
        this.totalBaseInPutMoney = 0;
        this.totalBaseOutPutMoney = 0;
        this.totalBaseUnSettledMoney = 0;
        this.totalBaseBalance = 0;
        this.totalBaseMvMoney = 0;

        this.totalBeginBaseVBalance = 0;
        this.totalBaseInPutVMoney = 0;
        this.totalBaseOutPutVMoney = 0;
        this.totalBaseVUnSettledMoney = 0;
        this.totalBaseVBalance = 0;
        this.totalBaseVMvMoney = 0;

        this.totalInPutTMoney = 0;
        this.totalOutPutTMoney = 0;
        this.totalBaseInPutTMoney = 0;
        this.totalBaseOutPutTMoney = 0;

        this.totalExchangeInDec = 0;
        this.totalExchangeCostInDec = 0;
        this.totalExchangeMCostInDec = 0;
        this.totalExchangeVCostInDec = 0;

        while (iHashResult.hasNext()) {
            secAccBook = null;
            secAccBook = (SecAccBookBean) iHashResult.next();
            //!cashAccBook.keyName.equalsIgnoreCase("null") &&
            todayRate = this.getSettingOper().getCuryRate(endDate, secAccBook.getCuryCode(), "", YssOperCons.YSS_RATE_BASE);
            secAccBook.setTodayRate(todayRate);
            if (!secAccBook.getCuryCode().equalsIgnoreCase("null")) {
                bufAll.append(secAccBook.buildRowStr()).append("\r\n");
                //计算合计值
                this.totalInPutAmount = YssD.add(this.totalInPutAmount,
                                                 secAccBook.getInPutAmount());
                this.totalOutPutAmount = YssD.add(this.totalOutPutAmount,
                                                  secAccBook.getOutPutAmount());
                if (secAccBook.getReportType().equalsIgnoreCase("sum")) {
                    this.totalBeginAmount = YssD.add(this.totalBeginAmount,
                        secAccBook.getBeginAmount());
                    this.totalUnSettledAmount = YssD.add(this.totalUnSettledAmount,
                        secAccBook.getUnSettledAmount());
                    this.totalAmount = YssD.add(this.totalAmount,
                                                secAccBook.getAmount());

                    this.totalInPutMoney = YssD.add(this.totalInPutMoney,
                        secAccBook.getInPutMoney());
                    this.totalOutPutMoney = YssD.add(this.totalOutPutMoney,
                        secAccBook.getOutPutMoney());
                    this.totalBeginBalance = YssD.add(this.totalBeginBalance,
                        secAccBook.getBeginBalance());
                    this.totalUnSettledMoney = YssD.add(this.totalUnSettledMoney,
                        secAccBook.getUnSettledMoney());
                    this.totalBalance = YssD.add(this.totalBalance,
                                                 secAccBook.getBalance());
                    this.totalMvMoney = YssD.add(this.totalMvMoney,
                                                 secAccBook.getMvMoney());

                    this.totalBeginMBalance = YssD.add(this.totalBeginMBalance,
                        secAccBook.getBeginMBalance());
                    this.totalInPutMMoney = YssD.add(this.totalInPutMMoney,
                        secAccBook.getInPutMMoney());
                    this.totalOutPutMMoney = YssD.add(this.totalOutPutMMoney,
                        secAccBook.getOutPutMMoney());
                    this.totalMUnSettledMoney = YssD.add(this.totalMUnSettledMoney,
                        secAccBook.getMUnSettledMoney());
                    this.totalMBalance = YssD.add(this.totalMBalance,
                                                  secAccBook.getMBalance());
                    this.totalMMvMoney = YssD.add(this.totalMMvMoney,
                                                  secAccBook.getMMvMoney());

                    this.totalBeginVBalance = YssD.add(this.totalBeginVBalance,
                        secAccBook.getBeginVBalance());
                    this.totalInPutVMoney = YssD.add(this.totalInPutVMoney,
                        secAccBook.getInPutVMoney());
                    this.totalOutPutVMoney = YssD.add(this.totalOutPutVMoney,
                        secAccBook.getOutPutVMoney());
                    this.totalVUnSettledMoney = YssD.add(this.totalVUnSettledMoney,
                        secAccBook.getVUnSettledMoney());
                    this.totalVBalance = YssD.add(this.totalVBalance,
                                                  secAccBook.getVBalance());
                    this.totalVMvMoney = YssD.add(this.totalVMvMoney,
                                                  secAccBook.getVMvMoney());

                    this.totalBeginBaseMBalance = YssD.add(this.
                        totalBeginBaseMBalance,
                        secAccBook.getBeginBaseMBalance());
                    this.totalBaseInPutMMoney = YssD.add(this.totalBaseInPutMMoney,
                        secAccBook.getBaseInPutMMoney());
                    this.totalBaseOutPutMMoney = YssD.add(this.totalBaseOutPutMMoney,
                        secAccBook.getBaseOutPutMMoney());
                    this.totalBaseMUnSettledMoney = YssD.add(this.
                        totalBaseMUnSettledMoney,
                        secAccBook.getBaseMUnSettledMoney());
                    this.totalBaseMBalance = YssD.add(this.totalBaseMBalance,
                        secAccBook.getBaseMBalance());
                    this.totalBaseMMvMoney = YssD.add(this.totalBaseMMvMoney,
                        secAccBook.getBaseMMvMoney());

                    this.totalBaseInPutMoney = YssD.add(this.totalBaseInPutMoney,
                        secAccBook.getBaseInPutMoney());
                    this.totalBaseOutPutMoney = YssD.add(this.totalBaseOutPutMoney,
                        secAccBook.getBaseOutPutMoney());
                    this.totalBeginBaseBalance = YssD.add(this.totalBeginBaseBalance,
                        secAccBook.getBeginBaseBalance());
                    this.totalBaseUnSettledMoney = YssD.add(this.
                        totalBaseUnSettledMoney,
                        secAccBook.getBaseUnSettledMoney());
                    this.totalBaseBalance = YssD.add(this.totalBaseBalance,
                        secAccBook.getBaseBalance());
                    this.totalBaseMvMoney = YssD.add(this.totalBaseMvMoney,
                        secAccBook.getBaseMvMoney());

                    this.totalBeginBaseVBalance = YssD.add(this.
                        totalBeginBaseVBalance,
                        secAccBook.getBeginBaseVBalance());
                    this.totalBaseInPutVMoney = YssD.add(this.totalBaseInPutVMoney,
                        secAccBook.getBaseInPutVMoney());
                    this.totalBaseOutPutVMoney = YssD.add(this.totalBaseOutPutVMoney,
                        secAccBook.getBaseOutPutVMoney());
                    this.totalBaseVUnSettledMoney = YssD.add(this.
                        totalBaseVUnSettledMoney,
                        secAccBook.getBaseVUnSettledMoney());
                    this.totalBaseVBalance = YssD.add(this.totalBaseVBalance,
                        secAccBook.getBaseVBalance());
                    this.totalBaseVMvMoney = YssD.add(this.totalBaseVMvMoney,
                        secAccBook.getBaseVMvMoney());
                } else if (secAccBook.getReportType().equalsIgnoreCase("detail")) {
                    this.totalInPutTMoney = YssD.add(this.totalInPutTMoney,
                        secAccBook.getInPutTMoney());
                    this.totalOutPutTMoney = YssD.add(this.totalOutPutTMoney,
                        secAccBook.getOutPutTMoney());
                    this.totalBaseInPutTMoney = YssD.add(this.totalBaseInPutTMoney,
                        secAccBook.getBaseInPutTMoney());
                    this.totalBaseOutPutTMoney = YssD.add(this.totalBaseOutPutTMoney,
                        secAccBook.getBaseOutPutTMoney());

                    this.totalInPutMoney = YssD.add(this.totalInPutMoney,
                        secAccBook.getInPutMoney());
                    this.totalInPutMMoney = YssD.add(this.totalInPutMMoney,
                        secAccBook.getInPutMMoney());
                    this.totalInPutVMoney = YssD.add(this.totalInPutVMoney,
                        secAccBook.getInPutVMoney());
                    this.totalBaseInPutMoney = YssD.add(this.totalBaseInPutMoney,
                        secAccBook.getBaseInPutMoney());
                    this.totalBaseInPutMMoney = YssD.add(this.totalBaseInPutMMoney,
                        secAccBook.getBaseInPutMMoney());
                    this.totalBaseInPutVMoney = YssD.add(this.totalBaseInPutVMoney,
                        secAccBook.getBaseInPutVMoney());

                    this.totalExchangeInDec = YssD.add(this.totalExchangeInDec,
                        secAccBook.getExchangeInDec());
                    this.totalExchangeCostInDec = YssD.add(this.
                        totalExchangeCostInDec,
                        secAccBook.getExchangeCostInDec());
                    this.totalExchangeMCostInDec = YssD.add(this.
                        totalExchangeMCostInDec,
                        secAccBook.getExchangeMCostInDec());
                    this.totalExchangeVCostInDec = YssD.add(this.
                        totalExchangeVCostInDec,
                        secAccBook.getExchangeVCostInDec());
                }
            }
        }
        bufAll.append(this.buildRowCalStr());
        return bufAll.toString();
    }

    /**
     * setHaspMapResultAttr
     * 将数据库查询结果集设置到HashMap中
     * @param rs ResultSet
     * @param hmResult HashMap
     */
    public void setHaspMapResultAttr(ResultSet rs, HashMap hmResult) throws
        SQLException, YssException {
        String strHmKey = "";
        SecAccBookBean existSecAccBook = null;
        SecAccBookBean reSecAccBook = null;

        if (this.reportType.equalsIgnoreCase("sum")) {

            //为未结算数据赋值(HashMap)
            if (rs.getString("FCuryCode") != null &&
                rs.getString("FCuryCode").trim().length() != 0 &&
                rs.getString("FIsCleared").equalsIgnoreCase("0")) {
                reSecAccBook = new SecAccBookBean();
                reSecAccBook.setReportType(this.reportType);
                reSecAccBook.setKeyCode(rs.getString("FCode") + "");
                reSecAccBook.setCuryCode(rs.getString("FCuryCode") + "");

                strHmKey = reSecAccBook.getKeyCode() + "\f" +
                    reSecAccBook.getCuryCode();
                existSecAccBook = (SecAccBookBean) hmResult.get(strHmKey);

                if (existSecAccBook == null) {
                    reSecAccBook.setKeyName(rs.getString("FName") + "");
                    reSecAccBook.setBaseCuryCode(pub.getBaseCury());
                    reSecAccBook.setUnSettledAmount(rs.getDouble("FTradeAmount"));
                    reSecAccBook.setMUnSettledMoney(rs.getDouble("FMCost"));
                    reSecAccBook.setBaseMUnSettledMoney(rs.getDouble(
                        "FMBaseCuryCost"));
                    reSecAccBook.setUnSettledMoney(rs.getDouble("FCost"));
                    reSecAccBook.setBaseUnSettledMoney(rs.getDouble("FBaseCuryCost"));
                    reSecAccBook.setVUnSettledMoney(rs.getDouble("FVCost"));
                    reSecAccBook.setBaseVUnSettledMoney(rs.getDouble(
                        "FVBaseCuryCost"));

                    hmResult.put(strHmKey, reSecAccBook);
                } else {
                    existSecAccBook.setUnSettledAmount(rs.getDouble("FTradeAmount"));
                    existSecAccBook.setMUnSettledMoney(rs.getDouble("FMCost"));
                    existSecAccBook.setBaseMUnSettledMoney(rs.getDouble(
                        "FMBaseCuryCost"));
                    existSecAccBook.setUnSettledMoney(rs.getDouble("FCost"));
                    existSecAccBook.setBaseUnSettledMoney(rs.getDouble(
                        "FBaseCuryCost"));
                    existSecAccBook.setVUnSettledMoney(rs.getDouble("FVCost"));
                    existSecAccBook.setBaseVUnSettledMoney(rs.getDouble(
                        "FVBaseCuryCost"));
                }
            }

            //为帐户余额赋值(HashMap)
            if (rs.getString("FCsCuryCode") != null &&
                rs.getString("FCsCuryCode").trim().length() != 0
                && rs.getString("FIsCleared").equalsIgnoreCase("1")) {
                reSecAccBook = new SecAccBookBean();
                reSecAccBook.setReportType(this.reportType);
                reSecAccBook.setKeyCode(rs.getString("FCode") + "");
                reSecAccBook.setCuryCode(rs.getString("FCsCuryCode") + "");

                strHmKey = reSecAccBook.getKeyCode() + "\f" +
                    reSecAccBook.getCuryCode();
                existSecAccBook = (SecAccBookBean) hmResult.get(strHmKey);
                if (existSecAccBook == null) {
                    reSecAccBook.setKeyName(rs.getString("FName") + "");
                    reSecAccBook.setBaseCuryCode(pub.getBaseCury());
                    reSecAccBook.setBeginAmount(rs.getDouble("FBeginAmount"));
                    reSecAccBook.setBeginMBalance(rs.getDouble("FBeginMBalance"));
                    reSecAccBook.setBeginBaseMBalance(rs.getDouble("FBeginBaseMBal"));
                    reSecAccBook.setBeginBalance(rs.getDouble("FBeginBalance"));
                    reSecAccBook.setBeginBaseBalance(rs.getDouble("FBeginBaseBal"));
                    reSecAccBook.setBeginVBalance(rs.getDouble("FBeginVBalance"));
                    reSecAccBook.setBeginBaseVBalance(rs.getDouble("FBeginBaseVBal"));

                    reSecAccBook.setMVaMoney(rs.getDouble("FMBalMV"));
                    reSecAccBook.setBaseMVaMoney(rs.getDouble("FBaseMBalMV"));
                    reSecAccBook.setVaMoney(rs.getDouble("FBalMV"));
                    reSecAccBook.setBaseVaMoney(rs.getDouble("FBaseBalMV"));
                    reSecAccBook.setVVaMoney(rs.getDouble("FVBalMV"));
                    reSecAccBook.setBaseVVaMoney(rs.getDouble("FBaseVBalMV"));

                    hmResult.put(strHmKey, reSecAccBook);
                } else {
                    existSecAccBook.setBeginAmount(rs.getDouble("FBeginAmount"));
                    existSecAccBook.setBeginMBalance(rs.getDouble("FBeginMBalance"));
                    existSecAccBook.setBeginBaseMBalance(rs.getDouble(
                        "FBeginBaseMBal"));
                    existSecAccBook.setBeginBalance(rs.getDouble("FBeginBalance"));
                    existSecAccBook.setBeginBaseBalance(rs.getDouble("FBeginBaseBal"));
                    existSecAccBook.setBeginVBalance(rs.getDouble("FBeginVBalance"));
                    existSecAccBook.setBeginBaseVBalance(rs.getDouble(
                        "FBeginBaseVBal"));

                    reSecAccBook.setMVaMoney(rs.getDouble("FMBalMV"));
                    reSecAccBook.setBaseMVaMoney(rs.getDouble("FBaseMBalMV"));
                    reSecAccBook.setVaMoney(rs.getDouble("FBalMV"));
                    reSecAccBook.setBaseVaMoney(rs.getDouble("FBaseBalMV"));
                    reSecAccBook.setVVaMoney(rs.getDouble("FVBalMV"));
                    reSecAccBook.setBaseVVaMoney(rs.getDouble("FBaseVBalMV"));

                }
            }

            //为累计流入流出赋值(HashMap)
            if (rs.getString("FCuryCode") != null &&
                rs.getString("FCuryCode").trim().length() != 0
                && rs.getString("FIsCleared").equalsIgnoreCase("1")) {
                reSecAccBook = new SecAccBookBean();
                reSecAccBook.setReportType(this.reportType);
                reSecAccBook.setKeyCode(rs.getString("FCode") + "");
                reSecAccBook.setCuryCode(rs.getString("FCuryCode") + "");

                strHmKey = reSecAccBook.getKeyCode() + "\f" +
                    reSecAccBook.getCuryCode();

                existSecAccBook = (SecAccBookBean) hmResult.get(strHmKey);
                if (existSecAccBook == null) {
                    reSecAccBook.setKeyName(rs.getString("FName") + "");
                    reSecAccBook.setBaseCuryCode(pub.getBaseCury());
                    if (rs.getInt("FInOut") == 1) {
                        reSecAccBook.setInPutAmount(rs.getDouble("FTradeAmount"));
                        reSecAccBook.setInPutMoney(rs.getDouble("FCost"));
                        reSecAccBook.setBaseInPutMoney(rs.getDouble("FBaseCuryCost"));
                        reSecAccBook.setInPutMMoney(rs.getDouble("FMCost"));
                        reSecAccBook.setBaseInPutMMoney(rs.getDouble("FMBaseCuryCost"));
                        reSecAccBook.setInPutVMoney(rs.getDouble("FVCost"));
                        reSecAccBook.setBaseInPutVMoney(rs.getDouble("FVBaseCuryCost"));

                        reSecAccBook.setMVaMoney(rs.getDouble("FMBalMV"));
                        reSecAccBook.setBaseMVaMoney(rs.getDouble("FBaseMBalMV"));
                        reSecAccBook.setVaMoney(rs.getDouble("FBalMV"));
                        reSecAccBook.setBaseVaMoney(rs.getDouble("FBaseBalMV"));
                        reSecAccBook.setVVaMoney(rs.getDouble("FVBalMV"));
                        reSecAccBook.setBaseVVaMoney(rs.getDouble("FBaseVBalMV"));

                    } else if (rs.getInt("FInOut") == -1) {
                        reSecAccBook.setOutPutAmount(rs.getDouble("FTradeAmount"));
                        reSecAccBook.setOutPutMoney(rs.getDouble("FCost"));
                        reSecAccBook.setBaseOutPutMoney(rs.getDouble("FBaseCuryCost"));
                        reSecAccBook.setOutPutMMoney(rs.getDouble("FMCost"));
                        reSecAccBook.setBaseOutPutMMoney(rs.getDouble(
                            "FMBaseCuryCost"));
                        reSecAccBook.setOutPutVMoney(rs.getDouble("FVCost"));
                        reSecAccBook.setBaseOutPutVMoney(rs.getDouble(
                            "FVBaseCuryCost"));

                        reSecAccBook.setMVaMoney(rs.getDouble("FMBalMV"));
                        reSecAccBook.setBaseMVaMoney(rs.getDouble("FBaseMBalMV"));
                        reSecAccBook.setVaMoney(rs.getDouble("FBalMV"));
                        reSecAccBook.setBaseVaMoney(rs.getDouble("FBaseBalMV"));
                        reSecAccBook.setVVaMoney(rs.getDouble("FVBalMV"));
                        reSecAccBook.setBaseVVaMoney(rs.getDouble("FBaseVBalMV"));

                    }

                    hmResult.put(strHmKey, reSecAccBook);
                } else {
                    if (rs.getInt("FInOut") == 1) {
                        existSecAccBook.setInPutAmount(rs.getDouble("FTradeAmount"));
                        existSecAccBook.setBaseInPutMMoney(rs.getDouble(
                            "FMBaseCuryCost"));
                        existSecAccBook.setBaseInPutMoney(rs.getDouble(
                            "FBaseCuryCost"));
                        existSecAccBook.setBaseInPutVMoney(rs.getDouble(
                            "FVBaseCuryCost"));
                        existSecAccBook.setInPutMMoney(rs.getDouble("FMCost"));
                        existSecAccBook.setInPutMoney(rs.getDouble("FCost"));
                        existSecAccBook.setInPutVMoney(rs.getDouble("FVCost"));

                        reSecAccBook.setMVaMoney(rs.getDouble("FMBalMV"));
                        reSecAccBook.setBaseMVaMoney(rs.getDouble("FBaseMBalMV"));
                        reSecAccBook.setVaMoney(rs.getDouble("FBalMV"));
                        reSecAccBook.setBaseVaMoney(rs.getDouble("FBaseBalMV"));
                        reSecAccBook.setVVaMoney(rs.getDouble("FVBalMV"));
                        reSecAccBook.setBaseVVaMoney(rs.getDouble("FBaseVBalMV"));

                    } else if (rs.getInt("FInOut") == -1) {
                        existSecAccBook.setOutPutAmount(rs.getDouble("FTradeAmount"));
                        existSecAccBook.setBaseOutPutMMoney(rs.getDouble(
                            "FMBaseCuryCost"));
                        existSecAccBook.setBaseOutPutMoney(rs.getDouble(
                            "FBaseCuryCost"));
                        existSecAccBook.setBaseOutPutVMoney(rs.getDouble(
                            "FVBaseCuryCost"));
                        existSecAccBook.setOutPutMMoney(rs.getDouble("FMCost"));
                        existSecAccBook.setOutPutMoney(rs.getDouble("FCost"));
                        existSecAccBook.setOutPutVMoney(rs.getDouble("FVCost"));

                        reSecAccBook.setMVaMoney(rs.getDouble("FMBalMV"));
                        reSecAccBook.setBaseMVaMoney(rs.getDouble("FBaseMBalMV"));
                        reSecAccBook.setVaMoney(rs.getDouble("FBalMV"));
                        reSecAccBook.setBaseVaMoney(rs.getDouble("FBaseBalMV"));
                        reSecAccBook.setVVaMoney(rs.getDouble("FVBalMV"));
                        reSecAccBook.setBaseVVaMoney(rs.getDouble("FBaseVBalMV"));

                    }
                }
            }

            /*
                      //为未结算数据赋值(HashMap)
                      if (rs.getString("FCode") != null &&
                rs.getString("FCode").trim().length() != 0
                && rs.getString("FIsCleared").equalsIgnoreCase("0")) {
               reSecAccBook = new SecAccBookBean();
               reSecAccBook.setReportType(this.reportType);
               reSecAccBook.setKeyCode(rs.getString("FCode") + "");
               reSecAccBook.setCuryCode(rs.getString("FCuryCode") + "");

               strHmKey = reSecAccBook.getKeyCode() + "\f" +
                     reSecAccBook.getCuryCode();
               existSecAccBook = (SecAccBookBean) hmResult.get(strHmKey);
               if (existSecAccBook == null) {
                  reSecAccBook.setKeyName(rs.getString("FName") + "");
                  reSecAccBook.setBaseCuryCode(pub.getBaseCury());
                  reSecAccBook.setUnSettledAmount(rs.getDouble("FTradeAmount"));
                  reSecAccBook.setMUnSettledMoney(rs.getDouble("FMCost"));
             reSecAccBook.setBaseMUnSettledMoney(rs.getDouble("FMBaseCuryCost"));
                  reSecAccBook.setUnSettledMoney(rs.getDouble("FCost"));
             reSecAccBook.setBaseUnSettledMoney(rs.getDouble("FBaseCuryCost"));
                  reSecAccBook.setVUnSettledMoney(rs.getDouble("FVCost"));
             reSecAccBook.setBaseVUnSettledMoney(rs.getDouble("FVBaseCuryCost"));

                  hmResult.put(strHmKey, reSecAccBook);
               }
               else {
             existSecAccBook.setUnSettledAmount(rs.getDouble("FTradeAmount"));
                  existSecAccBook.setMUnSettledMoney(rs.getDouble("FMCost"));
             existSecAccBook.setBaseMUnSettledMoney(rs.getDouble("FMBaseCuryCost"));
                  existSecAccBook.setUnSettledMoney(rs.getDouble("FCost"));
             existSecAccBook.setBaseUnSettledMoney(rs.getDouble("FBaseCuryCost"));
                  existSecAccBook.setVUnSettledMoney(rs.getDouble("FVCost"));
             existSecAccBook.setBaseVUnSettledMoney(rs.getDouble("FVBaseCuryCost"));
               }
                      }

                      //为帐户余额赋值(HashMap)
                      if (rs.getString("FAccCode") != null &&
                rs.getString("FAccCode").trim().length() != 0
                && rs.getString("FIsCleared").equalsIgnoreCase("1")) {
               reSecAccBook = new SecAccBookBean();
               reSecAccBook.setReportType(this.reportType);
               reSecAccBook.setKeyCode(rs.getString("FAccCode") + "");
               reSecAccBook.setCuryCode(rs.getString("FCsCuryCode") + "");

               strHmKey = reSecAccBook.getKeyCode() + "\f" +
                     reSecAccBook.getCuryCode();
               existSecAccBook = (SecAccBookBean) hmResult.get(strHmKey);
               if (existSecAccBook == null) {
                  reSecAccBook.setKeyName(rs.getString("FAccName") + "");
                  reSecAccBook.setBaseCuryCode(pub.getBaseCury());
                  reSecAccBook.setBeginAmount(rs.getDouble("FBeginAmount"));
                  reSecAccBook.setBeginMBalance(rs.getDouble("FBeginMBalance"));
             reSecAccBook.setBeginBaseMBalance(rs.getDouble("FBeginBaseMBal"));
                  reSecAccBook.setBeginBalance(rs.getDouble("FBeginBalance"));
                  reSecAccBook.setBeginBaseBalance(rs.getDouble("FBeginBaseBal"));
                  reSecAccBook.setBeginVBalance(rs.getDouble("FBeginVBalance"));
             reSecAccBook.setBeginBaseVBalance(rs.getDouble("FBeginBaseVBal"));

                  hmResult.put(strHmKey, reSecAccBook);
               }
               else {
                  existSecAccBook.setBeginAmount(rs.getDouble("FBeginAmount"));
             existSecAccBook.setBeginMBalance(rs.getDouble("FBeginMBalance"));
             existSecAccBook.setBeginBaseMBalance(rs.getDouble("FBeginBaseMBal"));
                  existSecAccBook.setBeginBalance(rs.getDouble("FBeginBalance"));
             existSecAccBook.setBeginBaseBalance(rs.getDouble("FBeginBaseBal"));
             existSecAccBook.setBeginVBalance(rs.getDouble("FBeginVBalance"));
             existSecAccBook.setBeginBaseVBalance(rs.getDouble("FBeginBaseVBal"));
               }
                      }

                      //为累计流入流出赋值(HashMap)
                      if (rs.getString("FCode") != null &&
                rs.getString("FCode").trim().length() != 0
                && !rs.getString("FIsCleared").equalsIgnoreCase("0")) {
               reSecAccBook = new SecAccBookBean();
               reSecAccBook.setReportType(this.reportType);
               reSecAccBook.setKeyCode(rs.getString("FCode") + "");
               reSecAccBook.setCuryCode(rs.getString("FCuryCode") + "");

               strHmKey = reSecAccBook.getKeyCode() + "\f" +
                     reSecAccBook.getCuryCode();

               existSecAccBook = (SecAccBookBean) hmResult.get(strHmKey);
               if (existSecAccBook == null) {
                  reSecAccBook.setKeyName(rs.getString("FName") + "");
                  reSecAccBook.setBaseCuryCode(pub.getBaseCury());
                  if (rs.getInt("FInOut") == 1) {
                     reSecAccBook.setInPutAmount(rs.getDouble("FTradeAmount"));
                     reSecAccBook.setInPutMoney(rs.getDouble("FCost"));
             reSecAccBook.setBaseInPutMoney(rs.getDouble("FBaseCuryCost"));
                     reSecAccBook.setInPutMMoney(rs.getDouble("FMCost"));
             reSecAccBook.setBaseInPutMMoney(rs.getDouble("FMBaseCuryCost"));
                     reSecAccBook.setInPutVMoney(rs.getDouble("FVCost"));
             reSecAccBook.setBaseInPutVMoney(rs.getDouble("FVBaseCuryCost"));
                  }
                  else if (rs.getInt("FInOut") == -1) {
                     reSecAccBook.setOutPutAmount(rs.getDouble("FTradeAmount"));
                     reSecAccBook.setOutPutMoney(rs.getDouble("FCost"));
             reSecAccBook.setBaseOutPutMoney(rs.getDouble("FBaseCuryCost"));
                     reSecAccBook.setOutPutMMoney(rs.getDouble("FMCost"));
             reSecAccBook.setBaseOutPutMMoney(rs.getDouble("FMBaseCuryCost"));
                     reSecAccBook.setOutPutVMoney(rs.getDouble("FVCost"));
             reSecAccBook.setBaseOutPutVMoney(rs.getDouble("FVBaseCuryCost"));
                  }

                  hmResult.put(strHmKey, reSecAccBook);
               }
               else {
                  if (rs.getInt("FInOut") == 1) {
                     existSecAccBook.setInPutAmount(rs.getDouble("FTradeAmount"));
             existSecAccBook.setBaseInPutMMoney(rs.getDouble("FMBaseCuryCost"));
             existSecAccBook.setBaseInPutMoney(rs.getDouble("FBaseCuryCost"));
             existSecAccBook.setBaseInPutVMoney(rs.getDouble("FVBaseCuryCost"));
                     existSecAccBook.setInPutMMoney(rs.getDouble("FMCost"));
                     existSecAccBook.setInPutMoney(rs.getDouble("FCost"));
                     existSecAccBook.setInPutVMoney(rs.getDouble("FVCost"));
                  }
                  else if (rs.getInt("FInOut") == -1) {
             existSecAccBook.setOutPutAmount(rs.getDouble("FTradeAmount"));
             existSecAccBook.setBaseOutPutMMoney(rs.getDouble("FMBaseCuryCost"));
             existSecAccBook.setBaseOutPutMoney(rs.getDouble("FBaseCuryCost"));
             existSecAccBook.setBaseOutPutVMoney(rs.getDouble("FVBaseCuryCost"));
                     existSecAccBook.setOutPutMMoney(rs.getDouble("FMCost"));
                     existSecAccBook.setOutPutMoney(rs.getDouble("FCost"));
                     existSecAccBook.setOutPutVMoney(rs.getDouble("FVCost"));
                  }
               }
                      }*/
        } else if (this.reportType.equalsIgnoreCase("detail")) {
            //为累计流入赋值(HashMap)
            if (rs.getString("FSecurityCode") != null &&
                rs.getString("FSecurityCode").trim().length() != 0) {
                reSecAccBook = new SecAccBookBean();
                //设置报表类型标志
                reSecAccBook.setReportType(this.reportType);
                reSecAccBook.setSubNum(rs.getString("FSubNum"));
                reSecAccBook.setSettleMark(rs.getString("FIsCleared"));

                strHmKey = reSecAccBook.getSettleMark() + "\f" +
                    reSecAccBook.getSubNum();

                existSecAccBook = (SecAccBookBean) hmResult.get(strHmKey);
                if (existSecAccBook == null) {
                    reSecAccBook.setKeyCode(rs.getString("FSecurityCode") + "");
                    reSecAccBook.setKeyName(rs.getString("FSecurityName") + "");
                    reSecAccBook.setSettleDate(YssFun.formatDate(rs.getDate(
                        "FBargainDate")));
                    reSecAccBook.setCuryCode(rs.getString("FCuryCode"));
                    reSecAccBook.setBaseCuryCode(pub.getBaseCury());
                    if (rs.getInt("FInOut") == 1) {
                        reSecAccBook.setInPutAmount(rs.getDouble("FTradeAmount"));
                        reSecAccBook.setInPutTMoney(rs.getDouble("FTotalCost"));
                        reSecAccBook.setBaseInPutTMoney(rs.getDouble("FBaseTotalCost"));
                    } else if (rs.getInt("FInOut") == -1) {
                        reSecAccBook.setOutPutAmount(rs.getDouble("FTradeAmount"));
                        reSecAccBook.setOutPutTMoney(rs.getDouble("FTotalCost"));
                        reSecAccBook.setBaseOutPutTMoney(rs.getDouble(
                            "FBaseTotalCost"));
                    }
                    reSecAccBook.setInPutMoney(rs.getDouble("FCost"));
                    reSecAccBook.setInPutMMoney(rs.getDouble("FMCost"));
                    reSecAccBook.setInPutVMoney(rs.getDouble("FVCost"));
                    reSecAccBook.setBaseInPutMoney(rs.getDouble("FBaseCuryCost"));
                    reSecAccBook.setBaseInPutMMoney(rs.getDouble("FMBaseCuryCost"));
                    reSecAccBook.setBaseInPutVMoney(rs.getDouble("FVBaseCuryCost"));
                    hmResult.put(strHmKey, reSecAccBook);
                } else {
                    if (rs.getInt("FInOut") == 1) {
                        existSecAccBook.setInPutAmount(rs.getDouble("FTradeAmount"));
                        existSecAccBook.setInPutTMoney(rs.getDouble("FTotalCost"));
                        existSecAccBook.setBaseInPutTMoney(rs.getDouble(
                            "FBaseTotalCost"));
                    } else if (rs.getInt("FInOut") == -1) {
                        existSecAccBook.setOutPutAmount(rs.getDouble("FTradeAmount"));
                        existSecAccBook.setOutPutTMoney(rs.getDouble("FTotalCost"));
                        existSecAccBook.setBaseOutPutTMoney(rs.getDouble(
                            "FBaseTotalCost"));
                    }
                    reSecAccBook.setInPutMoney(rs.getDouble("FCost"));
                    reSecAccBook.setInPutMMoney(rs.getDouble("FMCost"));
                    reSecAccBook.setInPutVMoney(rs.getDouble("FVCost"));
                    reSecAccBook.setBaseInPutMoney(rs.getDouble("FBaseCuryCost"));
                    reSecAccBook.setBaseInPutMMoney(rs.getDouble("FMBaseCuryCost"));
                    reSecAccBook.setBaseInPutVMoney(rs.getDouble("FVBaseCuryCost"));
                }
            }
        }
    }

    /**
     * getReportHeaders
     * 获取报表表头
     * @param sReportType String
     * @return String
     */
    public String getReportHeaders(String sReportType) {
        String reStr = "";
        String sHeadRowAry[] = null;
        String reStrKey = "";
        String reStrAry[] = null;
        int i = 0;
        if (sReportType.equalsIgnoreCase("initacc")) {
            reStr = this.getReportHeaders1();
            reStrKey = this.getReportFields1();
        } else if (sReportType.equalsIgnoreCase("loadacc")) {
            if (accBookDefineNameAry != null && accBookLinkAry != null) {
                if (accBookDefineNameAry.length > accBookLinkAry.length - 1) {
                    reStr = this.getReportHeaders1();
                    reStrKey = this.getReportFields1();
                } else {
                    reStr = this.getReportHeaders2();
                    reStrKey = this.getReportFields2();
                }
            } else {
                reStr = this.getReportHeaders1();
                reStrKey = this.getReportFields1();
            }
        }
        return reStr + YssCons.YSS_LINESPLITMARK + reStrKey;
    }
    
    //add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
    public String checkReportBeforeSearch(String sReportType){
    	return "";
    }

    /**shashijie 2011.04.07 STORY #805 头寸表应该预测T日到T+N-1日共N个工作日的头寸 */
	public String getSaveDefuntDay(String sRepotyType) throws YssException {
		return "";
	}
	
    public String GetBookSetName(String sPortCode) throws YssException
    {
    	return "";
    	
    }
}
