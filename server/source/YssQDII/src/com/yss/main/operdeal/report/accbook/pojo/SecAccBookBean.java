package com.yss.main.operdeal.report.accbook.pojo;

public class SecAccBookBean {

    private String sSettleMark = "";
    private String sSubNum = "";
    private String sKeyCode = "";
    private String sKeyName = "";
    private String sCuryCode = "";
    private String sCheckItems = "";
    private String sSettleDate = "";
    private String sBaseCuryCode = "";
    private int cashInd;

    private double dbInPutAmount; //流入数量
    private double dbTotalInPutAmount; //累计流入数量
    private double dbOutPutAmount; //流出数量
    private double dbTotalOutPutAmount; //累计流出数量
    private double dbBeginAmount; //期初数量
    private double dbTotalBeginAmount; //累计期初数量
    private double dbAmount; //期末数量
    private double dbTotalAmount; //累计期末数量
    private double dbUnSettledAmount; //未清算数量
    private double dbTotalUnSettledAmount; //累计未清算数量

    private double dbInPutTMoney; //流入（原币，买入金额）
    private double dbPortInPutTMoney; //组合货币流入（买入金额）
    private double dbTotalInPutTMoney; //累计流入（原币，买入金额）
    private double dbTotalPortInPutTMoney; //组合货币累计流入（买入金额）

    private double dbOutPutTMoney; //流出（原币，卖出金额）
    private double dbPortOutPutTMoney; //组合货币流出（卖出金额）
    private double dbTotalOutPutTMoney; //累计流出（原币，卖出金额）
    private double dbTotalPortOutPutTMoney; //组合货币累计流出（卖出金额）

    private double dbInPutMoney; //流入（原币，核算成本）
    private double dbInPutMMoney; //流入（原币，管理成本）
    private double dbInPutVMoney; //流入（原币，估值成本）
    private double dbPortInPutMoney; //组合货币流入（核算成本）
    private double dbPortInPutMMoney; //组合货币流入（管理成本）
    private double dbPortInPutVMoney; //组合货币流入（估值成本）

    private double dbTotalInPutMoney; //累计流入（原币，核算成本）
    private double dbTotalInPutMMoney; //累计流入（原币，管理成本）
    private double dbTotalInPutVMoney; //累计流入（原币，估值成本）
    private double dbTotalPortInPutMoney; //组合货币累计流入（核算成本）
    private double dbTotalPortInPutMMoney; //组合货币累计流入（管理成本）
    private double dbTotalPortInPutVMoney; //组合货币累计流入（估值成本）

    private double dbOutPutMoney; //流出（原币，核算成本）
    private double dbOutPutMMoney; //流出（原币，管理成本）
    private double dbOutPutVMoney; //流出（原币，估值成本）
    private double dbPortOutPutMoney; //组合货币流出（核算成本）
    private double dbPortOutPutMMoney; //组合货币流出（管理成本）
    private double dbPortOutPutVMoney; //组合货币流出（估值成本）

    private double dbTotalOutPutMoney; //累计流出（原币，核算成本）
    private double dbTotalOutPutMMoney; //累计流出（原币，管理成本）
    private double dbTotalOutPutVMoney; //累计流出（原币，估值成本）
    private double dbTotalPortOutPutMoney; //组合货币累计流出（核算成本）
    private double dbTotalPortOutPutMMoney; //组合货币累计流出（管理成本）
    private double dbTotalPortOutPutVMoney; //组合货币累计流出（估值成本）

    private double dbBeginBalance; //期初余额（原币，核算成本）
    private double dbBeginMBalance; //期初余额（原币，管理成本）
    private double dbBeginVBalance; //期初余额（原币，估值成本）
    private double dbBeginPortBalance; //组合货币期初余额（核算成本）
    private double dbBeginPortMBalance; //组合货币期初余额（管理成本）
    private double dbBeginPortVBalance; //组合货币期初余额（估值成本）

    private double dbTotalBeginBalance; //累计期初余额（原币，核算成本）
    private double dbTotalBeginMBalance; //累计期初余额（原币，管理成本）
    private double dbTotalBeginVBalance; //累计期初余额（原币，估值成本）
    private double dbTotalBeginPortBalance; //组合货币累计期初余额（核算成本）
    private double dbTotalBeginPortMBalance; //组合货币累计期初余额（管理成本）
    private double dbTotalBeginPortVBalance; //组合货币累计期初余额（估值成本）

    private double dbBalance; //期末余额（原币，核算成本）
    private double dbMBalance; //期末余额（原币，管理成本）
    private double dbVBalance; //期末余额（原币，估值成本）
    private double dbPortBalance; //组合货币期末余额（核算成本）
    private double dbPortMBalance; //组合货币期末余额（管理成本）
    private double dbPortVBalance; //组合货币期末余额（估值成本）

    private double dbTotalBalance; //累计期末余额（原币，核算成本）
    private double dbTotalMBalance; //累计期末余额（原币，管理成本）
    private double dbTotalVBalance; //累计期末余额（原币，估值成本）
    private double dbTotalPortBalance; //组合货币累计期末余额（核算成本）
    private double dbTotalPortMBalance; //组合货币累计期末余额（管理成本）
    private double dbTotalPortVBalance; //组合货币累计期末余额（估值成本）

    private double dbUnSettledMoney; //未清算金额（原币，核算成本）
    private double dbMUnSettledMoney; //未清算金额（原币，管理成本）
    private double dbVUnSettledMoney; //未清算金额（原币，估值成本）
    private double dbBaseUnSettledMoney; //基础货币未清算金额（核算成本）
    private double dbBaseMUnSettledMoney; //基础货币未清算金额（管理成本）
    private double dbBaseVUnSettledMoney; //基础货币未清算金额（估值成本）

    private double dbTotalUnSettledMoney; //累计未清算金额（原币，核算成本）
    private double dbTotalMUnSettledMoney; //累计未清算金额（原币，管理成本）
    private double dbTotalVUnSettledMoney; //累计未清算金额（原币，估值成本）
    private double dbTotalBaseUnSettledMoney; //基础货币累计未清算金额（核算成本）
    private double dbTotalBaseMUnSettledMoney; //基础货币累计未清算金额（管理成本）
    private double dbTotalBaseVUnSettledMoney; //基础货币累计未清算金额（估值成本）

    private double dbExchangeInDec; //汇兑损益
    private double dbTotalExchangeInDec; //累计汇兑损益
    private double dbExchangeCostInDec; //汇兑损益（核算成本）
    private double dbTotalExchangeCostInDec; //累计汇兑损益（核算成本）
    private double dbExchangeMCostInDec; //汇兑损益（管理成本）
    private double dbTotalExchangeMCostInDec; //累计汇兑损益（管理成本）
    private double dbExchangeVCostInDec; //汇兑损益（估值成本）
    private double dbTotalExchangeVCostInDec; //累计汇兑损益（估值成本）
    private double dbTodayRate; //最新汇率

    private double dbVaMoney; //估值增值（原币，核算成本）
    private double dbMVaMoney; //估值增值（原币，管理成本）
    private double dbVVaMoney; //估值增值（原币，估值成本）
    private double dbPortVaMoney; //组合货币估值增值（核算成本）
    private double dbPortMVaMoney; //组合货币估值增值（管理成本）
    private double dbPortVVaMoney; //组合货币估值增值（估值成本）

    private double dbTotalVaMoney; //累计估值增值（原币，核算成本）
    private double dbTotalMVaMoney; //累计估值增值（原币，管理成本）
    private double dbTotalVVaMoney; //累计估值增值（原币，估值成本）
    private double dbTotalPortVaMoney; //组合货币累计估值增值（核算成本）
    private double dbTotalPortMVaMoney; //组合货币累计估值增值（管理成本）
    private double dbTotalPortVVaMoney; //组合货币累计估值增值（估值成本）

    private double dBmvMoney; //市值（原币，核算成本）
    private double dBmMvMoney; //市值（原币，管理成本）
    private double dBvMvMoney; //市值（原币，估值成本）
    private double dbPortMvMoney; //组合货币市值（核算成本）
    private double dbPortMMvMoney; //组合货币市值（管理成本）
    private double dbPortVMvMoney; //组合货币市值（估值成本）

    private double dbTotalMvMoney; //累计市值（原币，核算成本）
    private double dbTotalMMvMoney; //累计市值（原币，管理成本）
    private double dbTotalVMvMoney; //累计市值（原币，估值成本）
    private double dbTotalPortMvMoney; //组合货币累计市值（核算成本）
    private double dbTotalPortMMvMoney; //组合货币累计市值（管理成本）
    private double dbTotalPortVMvMoney; //组合货币累计市值（估值成本）

    public String getSBaseCuryCode() {
        return this.sBaseCuryCode;
    }

    public String getSCheckItems() {
        return sCheckItems;
    }

    public String getSCuryCode() {
        return sCuryCode;
    }

    public String getSKeyCode() {
        return sKeyCode;
    }

    public String getSKeyName() {
        return sKeyName;
    }

    public String getSSettleDate() {
        return sSettleDate;
    }

    public String getSSettleMark() {
        return sSettleMark;
    }

    public String getSSubNum() {
        return sSubNum;
    }

    public void setSBaseCuryCode(String sBaseCuryCode) {
        this.sBaseCuryCode = sBaseCuryCode;
    }

    public void setSCheckItems(String sCheckItems) {
        this.sCheckItems = sCheckItems;
    }

    public void setSCuryCode(String sCuryCode) {
        this.sCuryCode = sCuryCode;
    }

    public void setSKeyCode(String sKeyCode) {
        this.sKeyCode = sKeyCode;
    }

    public void setSKeyName(String sKeyName) {
        this.sKeyName = sKeyName;
    }

    public void setSSettleDate(String sSettleDate) {
        this.sSettleDate = sSettleDate;
    }

    public void setSSettleMark(String sSettleMark) {
        this.sSettleMark = sSettleMark;
    }

    public void setSSubNum(String sSubNum) {
        this.sSubNum = sSubNum;
    }

    public double getDbAmount() {
        return dbAmount;
    }

    public void setDbAmount(double dbAmount) {
        this.dbAmount = dbAmount;
    }

    public double getDbVVaMoney() {
        return dbVVaMoney;
    }

    public double getDbVUnSettledMoney() {
        return dbVUnSettledMoney;
    }

    public double getDbTotalMVaMoney() {
        return dbTotalMVaMoney;
    }

    public double getDbBalance() {
        return dbBalance;
    }

    public double getDbPortBalance() {
        return dbPortBalance;
    }

    public double getDbPortInPutMMoney() {
        return dbPortInPutMMoney;
    }

    public double getDbPortInPutMoney() {
        return dbPortInPutMoney;
    }

    public double getDbPortInPutTMoney() {
        return dbPortInPutTMoney;
    }

    public double getDbPortInPutVMoney() {
        return dbPortInPutVMoney;
    }

    public double getDbPortMBalance() {
        return dbPortMBalance;
    }

    public double getDbBaseMUnSettledMoney() {
        return dbBaseMUnSettledMoney;
    }

    public double getDbPortMMvMoney() {
        return dbPortMMvMoney;
    }

    public double getDbPortMVaMoney() {
        return dbPortMVaMoney;
    }

    public double getDbPortMvMoney() {
        return dbPortMvMoney;
    }

    public double getDbPortOutPutMMoney() {
        return dbPortOutPutMMoney;
    }

    public double getDbPortOutPutMoney() {
        return dbPortOutPutMoney;
    }

    public double getDbPortOutPutTMoney() {
        return dbPortOutPutTMoney;
    }

    public double getDbPortOutPutVMoney() {
        return dbPortOutPutVMoney;
    }

    public double getDbBaseUnSettledMoney() {
        return dbBaseUnSettledMoney;
    }

    public double getDbPortVaMoney() {
        return dbPortVaMoney;
    }

    public double getDbPortVBalance() {
        return dbPortVBalance;
    }

    public double getDbPortVMvMoney() {
        return dbPortVMvMoney;
    }

    public double getDbBaseVUnSettledMoney() {
        return dbBaseVUnSettledMoney;
    }

    public double getDbPortVVaMoney() {
        return dbPortVVaMoney;
    }

    public double getDbBeginAmount() {
        return dbBeginAmount;
    }

    public double getDbBeginBalance() {
        return dbBeginBalance;
    }

    public double getDbBeginPortMBalance() {
        return dbBeginPortMBalance;
    }

    public double getDbBeginPortBalance() {
        return dbBeginPortBalance;
    }

    public double getDbBeginPortVBalance() {
        return dbBeginPortVBalance;
    }

    public double getDbBeginMBalance() {
        return dbBeginMBalance;
    }

    public double getDbBeginVBalance() {
        return dbBeginVBalance;
    }

    public double getDbExchangeCostInDec() {
        return dbExchangeCostInDec;
    }

    public double getDbExchangeInDec() {
        return dbExchangeInDec;
    }

    public double getDbExchangeMCostInDec() {
        return dbExchangeMCostInDec;
    }

    public double getDbExchangeVCostInDec() {
        return dbExchangeVCostInDec;
    }

    public double getDbInPutAmount() {
        return dbInPutAmount;
    }

    public double getDbInPutMMoney() {
        return dbInPutMMoney;
    }

    public double getDbInPutMoney() {
        return dbInPutMoney;
    }

    public double getDbInPutTMoney() {
        return dbInPutTMoney;
    }

    public double getDbInPutVMoney() {
        return dbInPutVMoney;
    }

    public double getDbMBalance() {
        return dbMBalance;
    }

    public double getDBmMvMoney() {
        return dBmMvMoney;
    }

    public double getDbMUnSettledMoney() {
        return dbMUnSettledMoney;
    }

    public double getDbMVaMoney() {
        return dbMVaMoney;
    }

    public double getDbOutPutAmount() {
        return dbOutPutAmount;
    }

    public double getDBmvMoney() {
        return dBmvMoney;
    }

    public double getDbOutPutMMoney() {
        return dbOutPutMMoney;
    }

    public double getDbOutPutMoney() {
        return dbOutPutMoney;
    }

    public double getDbOutPutTMoney() {
        return dbOutPutTMoney;
    }

    public double getDbOutPutVMoney() {
        return dbOutPutVMoney;
    }

    public double getDbTodayRate() {
        return dbTodayRate;
    }

    public double getDbTotalAmount() {
        return dbTotalAmount;
    }

    public double getDbTotalBalance() {
        return dbTotalBalance;
    }

    public double getDbTotalPortBalance() {
        return dbTotalPortBalance;
    }

    public double getDbTotalPortInPutMMoney() {
        return dbTotalPortInPutMMoney;
    }

    public double getDbTotalPortInPutMoney() {
        return dbTotalPortInPutMoney;
    }

    public double getDbTotalPortInPutTMoney() {
        return dbTotalPortInPutTMoney;
    }

    public double getDbTotalPortInPutVMoney() {
        return dbTotalPortInPutVMoney;
    }

    public double getDbTotalPortMBalance() {
        return dbTotalPortMBalance;
    }

    public double getDbTotalPortMMvMoney() {
        return dbTotalPortMMvMoney;
    }

    public double getDbTotalBaseMUnSettledMoney() {
        return dbTotalBaseMUnSettledMoney;
    }

    public double getDbTotalPortMVaMoney() {
        return dbTotalPortMVaMoney;
    }

    public double getDbTotalPortMvMoney() {
        return dbTotalPortMvMoney;
    }

    public double getDbTotalPortOutPutMMoney() {
        return dbTotalPortOutPutMMoney;
    }

    public double getDbTotalPortOutPutMoney() {
        return dbTotalPortOutPutMoney;
    }

    public double getDbTotalPortOutPutTMoney() {
        return dbTotalPortOutPutTMoney;
    }

    public double getDbTotalPortOutPutVMoney() {
        return dbTotalPortOutPutVMoney;
    }

    public double getDbTotalBaseUnSettledMoney() {
        return dbTotalBaseUnSettledMoney;
    }

    public double getDbTotalPortVaMoney() {
        return dbTotalPortVaMoney;
    }

    public double getDbTotalPortVBalance() {
        return dbTotalPortVBalance;
    }

    public double getDbTotalPortVMvMoney() {
        return dbTotalPortVMvMoney;
    }

    public double getDbTotalBaseVUnSettledMoney() {
        return dbTotalBaseVUnSettledMoney;
    }

    public double getDbTotalPortVVaMoney() {
        return dbTotalPortVVaMoney;
    }

    public double getDbTotalBeginAmount() {
        return dbTotalBeginAmount;
    }

    public double getDbTotalBeginBalance() {
        return dbTotalBeginBalance;
    }

    public double getDbTotalBeginPortBalance() {
        return dbTotalBeginPortBalance;
    }

    public double getDbTotalBeginPortMBalance() {
        return dbTotalBeginPortMBalance;
    }

    public double getDbTotalBeginPortVBalance() {
        return dbTotalBeginPortVBalance;
    }

    public double getDbTotalBeginMBalance() {
        return dbTotalBeginMBalance;
    }

    public double getDbTotalBeginVBalance() {
        return dbTotalBeginVBalance;
    }

    public double getDbTotalExchangeCostInDec() {
        return dbTotalExchangeCostInDec;
    }

    public double getDbTotalExchangeInDec() {
        return dbTotalExchangeInDec;
    }

    public double getDbTotalExchangeMCostInDec() {
        return dbTotalExchangeMCostInDec;
    }

    public double getDbTotalExchangeVCostInDec() {
        return dbTotalExchangeVCostInDec;
    }

    public double getDbTotalInPutAmount() {
        return dbTotalInPutAmount;
    }

    public double getDbTotalInPutMMoney() {
        return dbTotalInPutMMoney;
    }

    public double getDbTotalInPutMoney() {
        return dbTotalInPutMoney;
    }

    public double getDbTotalInPutTMoney() {
        return dbTotalInPutTMoney;
    }

    public double getDbTotalInPutVMoney() {
        return dbTotalInPutVMoney;
    }

    public double getDbTotalMBalance() {
        return dbTotalMBalance;
    }

    public double getDbTotalMMvMoney() {
        return dbTotalMMvMoney;
    }

    public double getDbTotalMUnSettledMoney() {
        return dbTotalMUnSettledMoney;
    }

    public double getDbTotalMvMoney() {
        return dbTotalMvMoney;
    }

    public double getDbTotalOutPutAmount() {
        return dbTotalOutPutAmount;
    }

    public double getDbTotalOutPutMMoney() {
        return dbTotalOutPutMMoney;
    }

    public double getDbTotalOutPutMoney() {
        return dbTotalOutPutMoney;
    }

    public double getDbTotalOutPutTMoney() {
        return dbTotalOutPutTMoney;
    }

    public double getDbTotalOutPutVMoney() {
        return dbTotalOutPutVMoney;
    }

    public double getDbTotalUnSettledAmount() {
        return dbTotalUnSettledAmount;
    }

    public double getDbTotalUnSettledMoney() {
        return dbTotalUnSettledMoney;
    }

    public double getDbTotalVaMoney() {
        return dbTotalVaMoney;
    }

    public double getDbTotalVBalance() {
        return dbTotalVBalance;
    }

    public double getDbTotalVMvMoney() {
        return dbTotalVMvMoney;
    }

    public double getDbTotalVUnSettledMoney() {
        return dbTotalVUnSettledMoney;
    }

    public double getDbTotalVVaMoney() {
        return dbTotalVVaMoney;
    }

    public double getDbUnSettledAmount() {
        return dbUnSettledAmount;
    }

    public double getDbUnSettledMoney() {
        return dbUnSettledMoney;
    }

    public double getDbVaMoney() {
        return dbVaMoney;
    }

    public double getDbVBalance() {
        return dbVBalance;
    }

    public double getDBvMvMoney() {
        return dBvMvMoney;
    }

    public void setDbVVaMoney(double dbVVaMoney) {
        this.dbVVaMoney = dbVVaMoney;
    }

    public void setDbVUnSettledMoney(double dbVUnSettledMoney) {
        this.dbVUnSettledMoney = dbVUnSettledMoney;
    }

    public void setDBvMvMoney(double dBvMvMoney) {
        this.dBvMvMoney = dBvMvMoney;
    }

    public void setDbVBalance(double dbVBalance) {
        this.dbVBalance = dbVBalance;
    }

    public void setDbVaMoney(double dbVaMoney) {
        this.dbVaMoney = dbVaMoney;
    }

    public void setDbUnSettledAmount(double dbUnSettledAmount) {
        this.dbUnSettledAmount = dbUnSettledAmount;
    }

    public void setDbUnSettledMoney(double dbUnSettledMoney) {
        this.dbUnSettledMoney = dbUnSettledMoney;
    }

    public void setDbTotalVVaMoney(double dbTotalVVaMoney) {
        this.dbTotalVVaMoney = dbTotalVVaMoney;
    }

    public void setDbTotalVUnSettledMoney(double dbTotalVUnSettledMoney) {
        this.dbTotalVUnSettledMoney = dbTotalVUnSettledMoney;
    }

    public void setDbTotalVMvMoney(double dbTotalVMvMoney) {
        this.dbTotalVMvMoney = dbTotalVMvMoney;
    }

    public void setDbTotalVBalance(double dbTotalVBalance) {
        this.dbTotalVBalance = dbTotalVBalance;
    }

    public void setDbTotalVaMoney(double dbTotalVaMoney) {
        this.dbTotalVaMoney = dbTotalVaMoney;
    }

    public void setDbTotalUnSettledMoney(double dbTotalUnSettledMoney) {
        this.dbTotalUnSettledMoney = dbTotalUnSettledMoney;
    }

    public void setDbTotalUnSettledAmount(double dbTotalUnSettledAmount) {
        this.dbTotalUnSettledAmount = dbTotalUnSettledAmount;
    }

    public void setDbTotalOutPutVMoney(double dbTotalOutPutVMoney) {
        this.dbTotalOutPutVMoney = dbTotalOutPutVMoney;
    }

    public void setDbTotalOutPutTMoney(double dbTotalOutPutTMoney) {
        this.dbTotalOutPutTMoney = dbTotalOutPutTMoney;
    }

    public void setDbTotalOutPutMoney(double dbTotalOutPutMoney) {
        this.dbTotalOutPutMoney = dbTotalOutPutMoney;
    }

    public void setDbTotalOutPutMMoney(double dbTotalOutPutMMoney) {
        this.dbTotalOutPutMMoney = dbTotalOutPutMMoney;
    }

    public void setDbTotalOutPutAmount(double dbTotalOutPutAmount) {
        this.dbTotalOutPutAmount = dbTotalOutPutAmount;
    }

    public void setDbTotalMvMoney(double dbTotalMvMoney) {
        this.dbTotalMvMoney = dbTotalMvMoney;
    }

    public void setDbTotalMVaMoney(double dbTotalMVaMoney) {
        this.dbTotalMVaMoney = dbTotalMVaMoney;
    }

    public void setDbTotalMUnSettledMoney(double dbTotalMUnSettledMoney) {
        this.dbTotalMUnSettledMoney = dbTotalMUnSettledMoney;
    }

    public void setDbTotalMMvMoney(double dbTotalMMvMoney) {
        this.dbTotalMMvMoney = dbTotalMMvMoney;
    }

    public void setDbTotalMBalance(double dbTotalMBalance) {
        this.dbTotalMBalance = dbTotalMBalance;
    }

    public void setDbTotalInPutVMoney(double dbTotalInPutVMoney) {
        this.dbTotalInPutVMoney = dbTotalInPutVMoney;
    }

    public void setDbTotalInPutTMoney(double dbTotalInPutTMoney) {
        this.dbTotalInPutTMoney = dbTotalInPutTMoney;
    }

    public void setDbTotalInPutMoney(double dbTotalInPutMoney) {
        this.dbTotalInPutMoney = dbTotalInPutMoney;
    }

    public void setDbTotalInPutMMoney(double dbTotalInPutMMoney) {
        this.dbTotalInPutMMoney = dbTotalInPutMMoney;
    }

    public void setDbTotalInPutAmount(double dbTotalInPutAmount) {
        this.dbTotalInPutAmount = dbTotalInPutAmount;
    }

    public void setDbTotalExchangeVCostInDec(double dbTotalExchangeVCostInDec) {
        this.dbTotalExchangeVCostInDec = dbTotalExchangeVCostInDec;
    }

    public void setDbTotalExchangeMCostInDec(double dbTotalExchangeMCostInDec) {
        this.dbTotalExchangeMCostInDec = dbTotalExchangeMCostInDec;
    }

    public void setDbTotalExchangeInDec(double dbTotalExchangeInDec) {
        this.dbTotalExchangeInDec = dbTotalExchangeInDec;
    }

    public void setDbTotalExchangeCostInDec(double dbTotalExchangeCostInDec) {
        this.dbTotalExchangeCostInDec = dbTotalExchangeCostInDec;
    }

    public void setDbTotalBeginVBalance(double dbTotalBeginVBalance) {
        this.dbTotalBeginVBalance = dbTotalBeginVBalance;
    }

    public void setDbTotalBeginMBalance(double dbTotalBeginMBalance) {
        this.dbTotalBeginMBalance = dbTotalBeginMBalance;
    }

    public void setDbTotalBeginPortVBalance(double dbTotalBeginBaseVBalance) {
        this.dbTotalBeginPortVBalance = dbTotalBeginBaseVBalance;
    }

    public void setDbTotalBeginPortMBalance(double dbTotalBeginBaseMBalance) {
        this.dbTotalBeginPortMBalance = dbTotalBeginBaseMBalance;
    }

    public void setDbTotalBeginPortBalance(double dbTotalBeginBaseBalance) {
        this.dbTotalBeginPortBalance = dbTotalBeginBaseBalance;
    }

    public void setDbTotalBeginBalance(double dbTotalBeginBalance) {
        this.dbTotalBeginBalance = dbTotalBeginBalance;
    }

    public void setDbTotalBeginAmount(double dbTotalBeginAmount) {
        this.dbTotalBeginAmount = dbTotalBeginAmount;
    }

    public void setDbTotalPortVVaMoney(double dbTotalBaseVVaMoney) {
        this.dbTotalPortVVaMoney = dbTotalBaseVVaMoney;
    }

    public void setDbTotalBaseVUnSettledMoney(double dbTotalBaseVUnSettledMoney) {
        this.dbTotalBaseVUnSettledMoney = dbTotalBaseVUnSettledMoney;
    }

    public void setDbTotalPortVMvMoney(double dbTotalBaseVMvMoney) {
        this.dbTotalPortVMvMoney = dbTotalBaseVMvMoney;
    }

    public void setDbTotalPortVBalance(double dbTotalBaseVBalance) {
        this.dbTotalPortVBalance = dbTotalBaseVBalance;
    }

    public void setDbTotalPortVaMoney(double dbTotalBaseVaMoney) {
        this.dbTotalPortVaMoney = dbTotalBaseVaMoney;
    }

    public void setDbTotalPortOutPutVMoney(double dbTotalBaseOutPutVMoney) {
        this.dbTotalPortOutPutVMoney = dbTotalBaseOutPutVMoney;
    }

    public void setDbTotalBaseUnSettledMoney(double dbTotalBaseUnSettledMoney) {
        this.dbTotalBaseUnSettledMoney = dbTotalBaseUnSettledMoney;
    }

    public void setDbTotalPortOutPutTMoney(double dbTotalBaseOutPutTMoney) {
        this.dbTotalPortOutPutTMoney = dbTotalBaseOutPutTMoney;
    }

    public void setDbTotalPortOutPutMoney(double dbTotalBaseOutPutMoney) {
        this.dbTotalPortOutPutMoney = dbTotalBaseOutPutMoney;
    }

    public void setDbTotalPortOutPutMMoney(double dbTotalBaseOutPutMMoney) {
        this.dbTotalPortOutPutMMoney = dbTotalBaseOutPutMMoney;
    }

    public void setDbTotalPortMvMoney(double dbTotalBaseMvMoney) {
        this.dbTotalPortMvMoney = dbTotalBaseMvMoney;
    }

    public void setDbTotalPortMVaMoney(double dbTotalBaseMVaMoney) {
        this.dbTotalPortMVaMoney = dbTotalBaseMVaMoney;
    }

    public void setDbTotalBaseMUnSettledMoney(double dbTotalBaseMUnSettledMoney) {
        this.dbTotalBaseMUnSettledMoney = dbTotalBaseMUnSettledMoney;
    }

    public void setDbTotalPortMMvMoney(double dbTotalBaseMMvMoney) {
        this.dbTotalPortMMvMoney = dbTotalBaseMMvMoney;
    }

    public void setDbTotalPortMBalance(double dbTotalBaseMBalance) {
        this.dbTotalPortMBalance = dbTotalBaseMBalance;
    }

    public void setDbTotalPortInPutVMoney(double dbTotalBaseInPutVMoney) {
        this.dbTotalPortInPutVMoney = dbTotalBaseInPutVMoney;
    }

    public void setDbTotalPortInPutTMoney(double dbTotalBaseInPutTMoney) {
        this.dbTotalPortInPutTMoney = dbTotalBaseInPutTMoney;
    }

    public void setDbTotalPortInPutMoney(double dbTotalBaseInPutMoney) {
        this.dbTotalPortInPutMoney = dbTotalBaseInPutMoney;
    }

    public void setDbTotalPortInPutMMoney(double dbTotalBaseInPutMMoney) {
        this.dbTotalPortInPutMMoney = dbTotalBaseInPutMMoney;
    }

    public void setDbTotalPortBalance(double dbTotalBaseBalance) {
        this.dbTotalPortBalance = dbTotalBaseBalance;
    }

    public void setDbTotalBalance(double dbTotalBalance) {
        this.dbTotalBalance = dbTotalBalance;
    }

    public void setDbTotalAmount(double dbTotalAmount) {
        this.dbTotalAmount = dbTotalAmount;
    }

    public void setDbTodayRate(double dbTodayRate) {
        this.dbTodayRate = dbTodayRate;
    }

    public void setDbOutPutVMoney(double dbOutPutVMoney) {
        this.dbOutPutVMoney = dbOutPutVMoney;
    }

    public void setDbOutPutTMoney(double dbOutPutTMoney) {
        this.dbOutPutTMoney = dbOutPutTMoney;
    }

    public void setDbOutPutMoney(double dbOutPutMoney) {
        this.dbOutPutMoney = dbOutPutMoney;
    }

    public void setDbOutPutMMoney(double dbOutPutMMoney) {
        this.dbOutPutMMoney = dbOutPutMMoney;
    }

    public void setDbOutPutAmount(double dbOutPutAmount) {
        this.dbOutPutAmount = dbOutPutAmount;
    }

    public void setDBmvMoney(double dBmvMoney) {
        this.dBmvMoney = dBmvMoney;
    }

    public void setDbMVaMoney(double dbMVaMoney) {
        this.dbMVaMoney = dbMVaMoney;
    }

    public void setDbMUnSettledMoney(double dbMUnSettledMoney) {
        this.dbMUnSettledMoney = dbMUnSettledMoney;
    }

    public void setDBmMvMoney(double dBmMvMoney) {
        this.dBmMvMoney = dBmMvMoney;
    }

    public void setDbMBalance(double dbMBalance) {
        this.dbMBalance = dbMBalance;
    }

    public void setDbInPutVMoney(double dbInPutVMoney) {
        this.dbInPutVMoney = dbInPutVMoney;
    }

    public void setDbInPutTMoney(double dbInPutTMoney) {
        this.dbInPutTMoney = dbInPutTMoney;
    }

    public void setDbInPutMoney(double dbInPutMoney) {
        this.dbInPutMoney = dbInPutMoney;
    }

    public void setDbInPutMMoney(double dbInPutMMoney) {
        this.dbInPutMMoney = dbInPutMMoney;
    }

    public void setDbInPutAmount(double dbInPutAmount) {
        this.dbInPutAmount = dbInPutAmount;
    }

    public void setDbExchangeVCostInDec(double dbExchangeVCostInDec) {
        this.dbExchangeVCostInDec = dbExchangeVCostInDec;
    }

    public void setDbExchangeMCostInDec(double dbExchangeMCostInDec) {
        this.dbExchangeMCostInDec = dbExchangeMCostInDec;
    }

    public void setDbExchangeInDec(double dbExchangeInDec) {
        this.dbExchangeInDec = dbExchangeInDec;
    }

    public void setDbExchangeCostInDec(double dbExchangeCostInDec) {
        this.dbExchangeCostInDec = dbExchangeCostInDec;
    }

    public void setDbBeginVBalance(double dbBeginVBalance) {
        this.dbBeginVBalance = dbBeginVBalance;
    }

    public void setDbBeginMBalance(double dbBeginMBalance) {
        this.dbBeginMBalance = dbBeginMBalance;
    }

    public void setDbBeginPortVBalance(double dbBeginBaseVBalance) {
        this.dbBeginPortVBalance = dbBeginBaseVBalance;
    }

    public void setDbBeginPortMBalance(double dbBeginBaseMBalance) {
        this.dbBeginPortMBalance = dbBeginBaseMBalance;
    }

    public void setDbBeginPortBalance(double dbBeginBaseBalance) {
        this.dbBeginPortBalance = dbBeginBaseBalance;
    }

    public void setDbBeginBalance(double dbBeginBalance) {
        this.dbBeginBalance = dbBeginBalance;
    }

    public void setDbBeginAmount(double dbBeginAmount) {
        this.dbBeginAmount = dbBeginAmount;
    }

    public void setDbPortVVaMoney(double dbBaseVVaMoney) {
        this.dbPortVVaMoney = dbBaseVVaMoney;
    }

    public void setDbBaseVUnSettledMoney(double dbBaseVUnSettledMoney) {
        this.dbBaseVUnSettledMoney = dbBaseVUnSettledMoney;
    }

    public void setDbPortVMvMoney(double dbBaseVMvMoney) {
        this.dbPortVMvMoney = dbBaseVMvMoney;
    }

    public void setDbPortVBalance(double dbBaseVBalance) {
        this.dbPortVBalance = dbBaseVBalance;
    }

    public void setDbPortVaMoney(double dbBaseVaMoney) {
        this.dbPortVaMoney = dbBaseVaMoney;
    }

    public void setDbBaseUnSettledMoney(double dbBaseUnSettledMoney) {
        this.dbBaseUnSettledMoney = dbBaseUnSettledMoney;
    }

    public void setDbPortOutPutVMoney(double dbBaseOutPutVMoney) {
        this.dbPortOutPutVMoney = dbBaseOutPutVMoney;
    }

    public void setDbPortOutPutMoney(double dbBaseOutPutMoney) {
        this.dbPortOutPutMoney = dbBaseOutPutMoney;
    }

    public void setDbPortOutPutTMoney(double dbBaseOutPutTMoney) {
        this.dbPortOutPutTMoney = dbBaseOutPutTMoney;
    }

    public void setDbPortOutPutMMoney(double dbBaseOutPutMMoney) {
        this.dbPortOutPutMMoney = dbBaseOutPutMMoney;
    }

    public void setDbPortMvMoney(double dbBaseMvMoney) {
        this.dbPortMvMoney = dbBaseMvMoney;
    }

    public void setDbPortMVaMoney(double dbBaseMVaMoney) {
        this.dbPortMVaMoney = dbBaseMVaMoney;
    }

    public void setDbBaseMUnSettledMoney(double dbBaseMUnSettledMoney) {
        this.dbBaseMUnSettledMoney = dbBaseMUnSettledMoney;
    }

    public void setDbPortMMvMoney(double dbBaseMMvMoney) {
        this.dbPortMMvMoney = dbBaseMMvMoney;
    }

    public void setDbPortMBalance(double dbBaseMBalance) {
        this.dbPortMBalance = dbBaseMBalance;
    }

    public void setDbPortInPutVMoney(double dbBaseInPutVMoney) {
        this.dbPortInPutVMoney = dbBaseInPutVMoney;
    }

    public void setDbPortInPutTMoney(double dbBaseInPutTMoney) {
        this.dbPortInPutTMoney = dbBaseInPutTMoney;
    }

    public void setDbPortInPutMoney(double dbBaseInPutMoney) {
        this.dbPortInPutMoney = dbBaseInPutMoney;
    }

    public void setDbPortInPutMMoney(double dbBaseInPutMMoney) {
        this.dbPortInPutMMoney = dbBaseInPutMMoney;
    }

    public void setDbPortBalance(double dbBaseBalance) {
        this.dbPortBalance = dbBaseBalance;
    }

    public void setDbBalance(double dbBalance) {
        this.dbBalance = dbBalance;
    }

    public SecAccBookBean() {
    }
}
