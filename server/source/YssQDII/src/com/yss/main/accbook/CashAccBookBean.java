package com.yss.main.accbook;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import java.sql.*;
import com.yss.util.*;

import java.util.*;
import com.yss.pojo.sys.YssMapAdmin;

public class CashAccBookBean
    extends BaseReportBean implements IClientReportView, IKey {
    private String accBookDefine = "";
    private String accBookLink = "";
    private String accBookDefineName = "";
    private int settleType = 0;
    private String settleMark = "";
    private String accBookDefineAry[] = null;
    private String accBookDefineNameAry[] = null;
    private String accBookLinkAry[] = null;
    private String checkItems = "";
    private String keyCode = "";
    private String keyName = "";
    private String curyCode = "";
    private String curyName = "";
    private String bankAccount = "";
    private String accType = "";
    private String accSubType = "";
    private String settleDate = "";
    private String baseCuryCode = "";
    private String subNum = "";
    private int cashInd;
    private double inPutMoney;
    private double totalInPutMoney;
    private double outPutMoney;
    private double totalOutPutMoney;
    private double beginAccBalance;
    private double totalBeginAccBalance;
    private double accBalance;
    private double totalAccBalance;
    private double unSettledMoney;
    private double totalUnSettledMoney;
    private double availableMoney;
    private double totalAvailableMoney;
    private double baseInPutMoney;
    private double totalBaseInPutMoney;
    private double baseOutPutMoney;
    private double totalBaseOutPutMoney;
    private double beginBaseAccBalance;
    private double totalBeginBaseAccBalance;
    private double baseAccBalance;
    private double totalBaseAccBalance;
    private double baseUnSettledMoney;
    private double totalBaseUnSettledMoney;
    private double baseAvailableMoney;
    private double totalBaseAvailableMoney;
    private double inUndueMoney;
    private double outUndueMoney;
    private double baseInUndueMoney;
    private double baseOutUndueMoney;
    private double totalInUndueMoney;
    private double totalOutUndueMoney;
    private double totalBaseInUndueMoney;
    private double totalBaseOutUndueMoney;
    private String reportType = "";
    private java.util.Date beginDate;
    private java.util.Date endDate;
    private double exchangeInDec;
    private double totalExchangeInDec;
    private double todayRate;
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

    public void setTotalBaseAccBalance(double totalBaseAccBalance) {
        this.totalBaseAccBalance = totalBaseAccBalance;
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

    public void setAccType(String accType) {
        this.accType = accType;
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

    public void setTotalBaseOutPutMoney(double totalBaseOutPutMoney) {
        this.totalBaseOutPutMoney = totalBaseOutPutMoney;
    }

    public void setBaseInPutMoney(double baseInPutMoney) {
        this.baseInPutMoney = baseInPutMoney;
    }

    public void setBaseOutPutMoney(double baseOutPutMoney) {
        this.baseOutPutMoney = baseOutPutMoney;
    }

    public void setTotalAccBalance(double totalAccBalance) {
        this.totalAccBalance = totalAccBalance;
    }

    public void setAccBalance(double accBalance) {
        this.accBalance = accBalance;
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

    public void setAccSubType(String accSubType) {
        this.accSubType = accSubType;
    }

    public void setBaseAccBalance(double baseAccBalance) {
        this.baseAccBalance = baseAccBalance;
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

    public void setBeginBaseAccBalance(double beginBaseAccBalance) {
        this.beginBaseAccBalance = beginBaseAccBalance;
    }

    public void setBeginAccBalance(double beginAccBalance) {
        this.beginAccBalance = beginAccBalance;
    }

    public void setTotalBeginAccBalance(double totalBeginAccBalance) {
        this.totalBeginAccBalance = totalBeginAccBalance;
    }

    public void setSettleType(int settleType) {
        this.settleType = settleType;
    }

    public void setTotalBaseUnSettledMoney(double totalBaseUnSettledMoney) {
        this.totalBaseUnSettledMoney = totalBaseUnSettledMoney;
    }

    public void setTotalUnSettledMoney(double totalUnSettledMoney) {
        this.totalUnSettledMoney = totalUnSettledMoney;
    }

    public void setBaseAvailableMoney(double baseAvailableMoney) {
        this.baseAvailableMoney = baseAvailableMoney;
    }

    public void setUnSettledMoney(double unSettledMoney) {
        this.unSettledMoney = unSettledMoney;
    }

    public void setBaseUnSettledMoney(double baseUnSettledMoney) {
        this.baseUnSettledMoney = baseUnSettledMoney;
    }

    public void setTotalBeginBaseAccBalance(double totalBeginBaseAccBalance) {
        this.totalBeginBaseAccBalance = totalBeginBaseAccBalance;
    }

    public void setTotalAvailableMoney(double totalAvailableMoney) {
        this.totalAvailableMoney = totalAvailableMoney;
    }

    public void setSettleMark(String settleMark) {
        this.settleMark = settleMark;
    }

    public void setAvailableMoney(double availableMoney) {
        this.availableMoney = availableMoney;
    }

    public void setTotalBaseAvailableMoney(double totalBaseAvailableMoney) {
        this.totalBaseAvailableMoney = totalBaseAvailableMoney;
    }

    public void setSubNum(String subNum) {
        this.subNum = subNum;
    }

    public void setBaseInUndueMoney(double baseInUndueMoney) {
        this.baseInUndueMoney = baseInUndueMoney;
    }

    public void setOutUndueMoney(double outUndueMoney) {
        this.outUndueMoney = outUndueMoney;
    }

    public void setBaseOutUndueMoney(double baseOutUndueMoney) {
        this.baseOutUndueMoney = baseOutUndueMoney;
    }

    public void setInUndueMoney(double inUndueMoney) {
        this.inUndueMoney = inUndueMoney;
    }

    public void setTotalInUndueMoney(double totalInUndueMoney) {
        this.totalInUndueMoney = totalInUndueMoney;
    }

    public void setTotalBaseInUndueMoney(double totalBaseInUndueMoney) {
        this.totalBaseInUndueMoney = totalBaseInUndueMoney;
    }

    public void setTotalBaseOutUndueMoney(double totalBaseOutUndueMoney) {
        this.totalBaseOutUndueMoney = totalBaseOutUndueMoney;
    }

    public void setTotalOutUndueMoney(double totalOutUndueMoney) {
        this.totalOutUndueMoney = totalOutUndueMoney;
    }

    public void setExchangeInDec(double exchangeInDec) {
        this.exchangeInDec = exchangeInDec;
    }

    public void setTodayRate(double todayRate) {
        this.todayRate = todayRate;
    }

    public void setTotalExchangeInDec(double totalExchangeInDec) {
        this.totalExchangeInDec = totalExchangeInDec;
    }

    public void setCheckItems(String checkItems) {
        this.checkItems = checkItems;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public String getAccBookDefine() {
        return accBookDefine;
    }

    public double getTotalBaseAccBalance() {
        return totalBaseAccBalance;
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

    public String getAccType() {
        return accType;
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

    public double getTotalBaseOutPutMoney() {
        return totalBaseOutPutMoney;
    }

    public double getBaseInPutMoney() {
        return baseInPutMoney;
    }

    public double getBaseOutPutMoney() {
        return baseOutPutMoney;
    }

    public double getTotalAccBalance() {
        return totalAccBalance;
    }

    public double getAccBalance() {
        return accBalance;
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

    public String getAccSubType() {
        return accSubType;
    }

    public double getBaseAccBalance() {
        return baseAccBalance;
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

    public double getBeginBaseAccBalance() {
        return beginBaseAccBalance;
    }

    public double getBeginAccBalance() {
        return beginAccBalance;
    }

    public double getTotalBeginAccBalance() {
        return totalBeginAccBalance;
    }

    public int getSettleType() {
        return settleType;
    }

    public double getTotalBaseUnSettledMoney() {
        return totalBaseUnSettledMoney;
    }

    public double getTotalUnSettledMoney() {
        return totalUnSettledMoney;
    }

    public double getBaseAvailableMoney() {
        return baseAvailableMoney;
    }

    public double getUnSettledMoney() {
        return unSettledMoney;
    }

    public double getBaseUnSettledMoney() {
        return baseUnSettledMoney;
    }

    public double getTotalBeginBaseAccBalance() {
        return totalBeginBaseAccBalance;
    }

    public double getTotalAvailableMoney() {
        return totalAvailableMoney;
    }

    public String getSettleMark() {
        return settleMark;
    }

    public double getAvailableMoney() {
        return availableMoney;
    }

    public double getTotalBaseAvailableMoney() {
        return totalBaseAvailableMoney;
    }

    public String getSubNum() {
        return subNum;
    }

    public double getBaseInUndueMoney() {
        return baseInUndueMoney;
    }

    public double getOutUndueMoney() {
        return outUndueMoney;
    }

    public double getBaseOutUndueMoney() {
        return baseOutUndueMoney;
    }

    public double getInUndueMoney() {
        return inUndueMoney;
    }

    public double getTotalInUndueMoney() {
        return totalInUndueMoney;
    }

    public double getTotalBaseInUndueMoney() {
        return totalBaseInUndueMoney;
    }

    public double getTotalBaseOutUndueMoney() {
        return totalBaseOutUndueMoney;
    }

    public double getTotalOutUndueMoney() {
        return totalOutUndueMoney;
    }

    public double getExchangeInDec() {
        return exchangeInDec;
    }

    public double getTodayRate() {
        return todayRate;
    }

    public double getTotalExchangeInDec() {
        return totalExchangeInDec;
    }

    public String getCheckItems() {
        return checkItems;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public String getCuryName() {
        return curyName;
    }

    public CashAccBookBean() {
    }

    /**
     * parseRowStr
     * 解析现金台帐请求
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
            throw new YssException(" 解析现金台帐请求出错", e);
        }
    }

    /**
     * buildRowStr
     * 返回BGrid中一行数据
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();

        if (reportType.equalsIgnoreCase("sum")) {
            //计算期末余额与可用头寸
            this.accBalance = YssD.sub(YssD.add(this.beginAccBalance,
                                                this.inPutMoney), this.outPutMoney);
            this.availableMoney = YssD.sub(YssD.add(this.accBalance,
                this.inUndueMoney),
                                           this.outUndueMoney);

            this.beginBaseAccBalance = YssD.mul(this.beginAccBalance, todayRate);
            this.baseInPutMoney = YssD.mul(this.inPutMoney, todayRate);
            this.baseOutPutMoney = YssD.mul(this.outPutMoney, todayRate);
            this.baseInUndueMoney = YssD.mul(this.inUndueMoney, todayRate);
            this.baseOutUndueMoney = YssD.mul(this.outUndueMoney, todayRate);
            this.baseUnSettledMoney = YssD.mul(this.unSettledMoney, todayRate);
            this.baseAccBalance = YssD.sub(YssD.add(this.beginBaseAccBalance,
                this.baseInPutMoney),
                                           this.baseOutPutMoney);
            this.baseAvailableMoney = YssD.sub(YssD.add(this.baseAccBalance,
                this.baseInUndueMoney), this.baseOutUndueMoney);
//         this.baseAvailableMoney = YssD.mul(this.availableMoney,todayRate);
//         this.baseAccBalance = YssD.mul(this.accBalance,todayRate);
            buf.append(this.keyCode).append("\t");
            buf.append(this.keyName).append("\t");
            buf.append(this.bankAccount).append("\t");
            buf.append(this.curyName).append("\t");
            buf.append(this.curyCode).append("\t");
            buf.append(YssFun.formatNumber(this.beginAccBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.inPutMoney, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.outPutMoney, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.accBalance, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.inUndueMoney, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.outUndueMoney, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.unSettledMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.availableMoney, "#,##0.00")).
                append("\t");
            buf.append(this.baseCuryCode).append("\t");
            buf.append(YssFun.formatNumber(this.beginBaseAccBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseOutPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseAccBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseInUndueMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseOutUndueMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseUnSettledMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseAvailableMoney, "#,##0.00")).
                append("\t");
//         buf.append(settleMark);
        } else if (reportType.equalsIgnoreCase("detail")) {
            buf.append(this.keyCode).append("\t");
            buf.append(this.keyName).append("\t");
            buf.append(this.accType).append("\t");
            buf.append(this.accSubType).append("\t");
            buf.append(this.settleDate).append("\t");
            buf.append(this.curyCode).append("\t");
            buf.append(YssFun.formatNumber(this.inPutMoney, "#,##0.00")).append(
                "\t");
            buf.append(YssFun.formatNumber(this.outPutMoney, "#,##0.00")).append(
                "\t");
            buf.append(this.baseCuryCode).append("\t");
            buf.append(YssFun.formatNumber(this.baseInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.baseOutPutMoney, "#,##0.00")).
                append("\t");
            if (this.inPutMoney != 0) {
                this.exchangeInDec = YssD.sub(YssD.mul(this.inPutMoney, todayRate), this.baseInPutMoney);
            }
            if (this.outPutMoney != 0) {
                this.exchangeInDec = YssD.sub(YssD.mul(this.outPutMoney, todayRate), this.baseOutPutMoney);
            }
            buf.append(YssFun.formatNumber(this.exchangeInDec, "#,##0.00")).append("\t");
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
            buf.append("合计：\t\t\t\t\t");
            buf.append(YssFun.formatNumber(this.totalBeginAccBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalOutPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalAccBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalInUndueMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalOutUndueMoney, "#,##0.00")).
                append("\t");

            buf.append(YssFun.formatNumber(this.totalUnSettledMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalAvailableMoney, "#,##0.00")).
                append("\t");
            buf.append("\t");
            buf.append(YssFun.formatNumber(this.totalBeginBaseAccBalance,
                                           "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseOutPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseAccBalance, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseInUndueMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseOutUndueMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseUnSettledMoney,
                                           "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseAvailableMoney,
                                           "#,##0.00"));
        } else if (reportType.equalsIgnoreCase("detail")) {
            buf.append("合计：\t\t\t\t\t\t");
            buf.append(YssFun.formatNumber(this.totalInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalOutPutMoney, "#,##0.00")).
                append("\t");
            buf.append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseInPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalBaseOutPutMoney, "#,##0.00")).
                append("\t");
            buf.append(YssFun.formatNumber(this.totalExchangeInDec, "#,##0.00")).
                append("\t\t");
        }
        return buf.toString();
    }

    /**
     * getReportData
     * 响应获取现金台帐请求
     * @param sReportType String
     * @return String
     */
    public String getReportData(String sReportType) throws YssException {
        String strSql = "";
        HashMap hmResult = new HashMap();
        StringBuffer buf = new StringBuffer();
        IAccBookOper cashOper = (IAccBookOper) pub.getOperDealCtx().
            getBean("cashbookoper");
        ResultSet rs = null;
        this.keyCode = "";
        this.curyCode = "";
        try {
            if (sReportType.equalsIgnoreCase("loadacc")) {
                cashOper.setYssPub(pub);
                buf.append(getReportHeaders(sReportType)).append(YssCons.
                    YSS_LINESPLITMARK);

                strSql = cashOper.getBookSql(this.accBookDefine,
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
            throw new YssException("获取现金台帐出错");
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
        CashAccBookBean cashAccBook = null;
        Iterator iHashResult = null;
        YssMapAdmin mapAdmin = null;
        BaseComparator comparator = new BaseComparator();

        //结果排序
        mapAdmin = new YssMapAdmin(hmResult, comparator);
        iHashResult = mapAdmin.sortMap().iterator();
        this.totalInPutMoney = 0;
        this.totalOutPutMoney = 0;
        this.totalBeginAccBalance = 0;
        this.totalUnSettledMoney = 0;
        this.totalAvailableMoney = 0;
        this.totalAccBalance = 0;
        this.totalBaseInPutMoney = 0;
        this.totalBaseOutPutMoney = 0;
        this.totalBaseAccBalance = 0;
        this.totalBeginBaseAccBalance = 0;
        this.totalBaseUnSettledMoney = 0;
        this.totalBaseAvailableMoney = 0;
        this.totalExchangeInDec = 0;

        while (iHashResult.hasNext()) {
            cashAccBook = null;
            cashAccBook = (CashAccBookBean) iHashResult.next();
            //!cashAccBook.keyName.equalsIgnoreCase("null") &&
            todayRate = this.getSettingOper().getCuryRate(endDate, cashAccBook.getCuryCode(), "", YssOperCons.YSS_RATE_BASE);
            cashAccBook.setTodayRate(todayRate);
            if (!cashAccBook.getCuryCode().equalsIgnoreCase("null")) {
                bufAll.append(cashAccBook.buildRowStr()).append("\r\n");
                //计算合计值
                this.totalInPutMoney = YssD.add(this.totalInPutMoney,
                                                cashAccBook.getInPutMoney());
                this.totalOutPutMoney = YssD.add(this.totalOutPutMoney,
                                                 cashAccBook.getOutPutMoney());
                this.totalBaseInPutMoney = YssD.add(this.totalBaseInPutMoney,
                    cashAccBook.getBaseInPutMoney());
                this.totalBaseOutPutMoney = YssD.add(this.totalBaseOutPutMoney,
                    cashAccBook.getBaseOutPutMoney());
                if (cashAccBook.getReportType().equalsIgnoreCase("sum")) {
                    this.totalBeginAccBalance = YssD.add(this.totalBeginAccBalance,
                        cashAccBook.getBeginAccBalance());
                    this.totalAccBalance = YssD.add(this.totalAccBalance,
                        cashAccBook.getAccBalance());
                    this.totalUnSettledMoney = YssD.add(this.totalUnSettledMoney,
                        cashAccBook.getUnSettledMoney());
                    this.totalAvailableMoney = YssD.add(this.totalAvailableMoney,
                        cashAccBook.getAvailableMoney());

                    this.totalBaseAccBalance = YssD.add(this.totalBaseAccBalance,
                        cashAccBook.getBaseAccBalance());
                    this.totalBeginBaseAccBalance = YssD.add(this.
                        totalBeginBaseAccBalance,
                        cashAccBook.getBeginBaseAccBalance());
                    this.totalBaseUnSettledMoney = YssD.add(this.
                        totalBaseUnSettledMoney, cashAccBook.getBaseUnSettledMoney());
                    this.totalBaseAvailableMoney = YssD.add(this.
                        totalBaseAvailableMoney, cashAccBook.getBaseAvailableMoney());
                    this.totalInUndueMoney = YssD.add(this.totalInUndueMoney,
                        cashAccBook.getInUndueMoney());
                    this.totalOutUndueMoney = YssD.add(this.totalOutUndueMoney,
                        cashAccBook.getOutUndueMoney());
                    this.totalBaseInUndueMoney = YssD.add(this.totalBaseInUndueMoney,
                        cashAccBook.getBaseInUndueMoney());
                    this.totalBaseOutUndueMoney = YssD.add(this.
                        totalBaseOutUndueMoney, cashAccBook.getBaseOutUndueMoney());
                } else if (cashAccBook.getReportType().equalsIgnoreCase("detail")) {
                    this.totalExchangeInDec = YssD.add(this.totalExchangeInDec,
                        cashAccBook.getExchangeInDec());
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
        CashAccBookBean existCashAccBook = null;
        CashAccBookBean reCashAccBook = null;

        if (this.reportType.equalsIgnoreCase("sum")) {
            //为未结算数据赋值(HashMap)
            if (rs.getString("FCode") != null &&
                rs.getString("FCode").trim().length() != 0
                && rs.getString("FIsCleared").equalsIgnoreCase("0")) {
                reCashAccBook = new CashAccBookBean();
                reCashAccBook.setReportType(this.reportType);
                reCashAccBook.setKeyCode(rs.getString("FCode") + "");
                reCashAccBook.setCuryCode(rs.getString("FCuryCode") + "");
                reCashAccBook.setCuryName(rs.getString("FCuryName") + "");
                reCashAccBook.setBankAccount(rs.getString("FBankAccount") + "");

                strHmKey = reCashAccBook.getKeyCode() + "\f" +
                    reCashAccBook.getCuryCode();

                existCashAccBook = (CashAccBookBean) hmResult.get(strHmKey);
                if (existCashAccBook == null) {
                    reCashAccBook.setKeyName(rs.getString("FName") + "");
                    reCashAccBook.setBaseCuryCode(pub.getBaseCury());
                    reCashAccBook.setUnSettledMoney(rs.getDouble("FMoney"));
                    reCashAccBook.setBaseUnSettledMoney(rs.getDouble("FBaseMoney"));

                    hmResult.put(strHmKey, reCashAccBook);
                } else {
                    existCashAccBook.setUnSettledMoney(rs.getDouble("FMoney"));
                    existCashAccBook.setBaseUnSettledMoney(rs.getDouble("FBaseMoney"));
                }
            }

            //为帐户余额赋值(HashMap)
            if (rs.getString("FAccCode") != null &&
                rs.getString("FAccCode").trim().length() != 0) {
                reCashAccBook = new CashAccBookBean();
                reCashAccBook.setReportType(this.reportType);
                reCashAccBook.setKeyCode(rs.getString("FAccCode") + "");
                reCashAccBook.setCuryCode(rs.getString("FCsCuryCode") + "");
                reCashAccBook.setCuryName(rs.getString("FCsCuryName") + "");
                reCashAccBook.setBankAccount(rs.getString("FCsBankAccount") + "");

                strHmKey = reCashAccBook.getKeyCode() + "\f" +
                    reCashAccBook.getCuryCode();

                existCashAccBook = (CashAccBookBean) hmResult.get(strHmKey);
                if (existCashAccBook == null) {
                    reCashAccBook.setKeyName(rs.getString("FAccName") + "");
                    reCashAccBook.setBaseCuryCode(pub.getBaseCury());
                    reCashAccBook.setBeginAccBalance(rs.getDouble("FBeginAccBalance"));
                    reCashAccBook.setBeginBaseAccBalance(rs.getDouble("FBeginBaseAccBal"));

                    hmResult.put(strHmKey, reCashAccBook);
                } else {
                    existCashAccBook.setBeginAccBalance(rs.getDouble("FBeginAccBalance"));
                    existCashAccBook.setBeginBaseAccBalance(rs.getDouble("FBeginBaseAccBal"));
                }
            }

            //为累计流入流出赋值(HashMap)
            if (rs.getString("FCode") != null &&
                rs.getString("FCode").trim().length() != 0
                && !rs.getString("FIsCleared").equalsIgnoreCase("0")) {
                reCashAccBook = new CashAccBookBean();
                reCashAccBook.setReportType(this.reportType);
                reCashAccBook.setKeyCode(rs.getString("FCode") + "");
                reCashAccBook.setCuryCode(rs.getString("FCuryCode") + "");
                reCashAccBook.setCuryCode(rs.getString("FCsCuryCode") + "");
                reCashAccBook.setBankAccount(rs.getString("FBankAccount") + "");

                strHmKey = reCashAccBook.getKeyCode() + "\f" +
                    reCashAccBook.getCuryCode();

                existCashAccBook = (CashAccBookBean) hmResult.get(strHmKey);
                if (existCashAccBook == null) {
                    reCashAccBook.setKeyName(rs.getString("FName") + "");
                    reCashAccBook.setBaseCuryCode(pub.getBaseCury());
                    if (rs.getInt("FInOut") == 1) {
                        reCashAccBook.setInPutMoney(rs.getDouble("FMoney"));
                        reCashAccBook.setBaseInPutMoney(rs.getDouble("FBaseMoney"));
                        reCashAccBook.setInUndueMoney(rs.getDouble("FUndueMoney"));
                        reCashAccBook.setBaseInUndueMoney(rs.getDouble("FBaseUndueMoney"));
                    } else if (rs.getInt("FInOut") == -1) {
                        reCashAccBook.setOutPutMoney(rs.getDouble("FMoney"));
                        reCashAccBook.setBaseOutPutMoney(rs.getDouble("FBaseMoney"));
                        reCashAccBook.setOutUndueMoney(rs.getDouble("FUndueMoney"));
                        reCashAccBook.setBaseOutUndueMoney(rs.getDouble("FBaseUndueMoney"));
                    }

                    hmResult.put(strHmKey, reCashAccBook);
                } else {
                    if (rs.getInt("FInOut") == 1) {
                        existCashAccBook.setInPutMoney(rs.getDouble("FMoney"));
                        existCashAccBook.setBaseInPutMoney(rs.getDouble("FBaseMoney"));
                        existCashAccBook.setInUndueMoney(rs.getDouble("FUndueMoney"));
                        existCashAccBook.setBaseInUndueMoney(rs.getDouble("FBaseUndueMoney"));
                    } else if (rs.getInt("FInOut") == -1) {
                        existCashAccBook.setOutPutMoney(rs.getDouble("FMoney"));
                        existCashAccBook.setBaseOutPutMoney(rs.getDouble("FBaseMoney"));
                        existCashAccBook.setOutUndueMoney(rs.getDouble("FUndueMoney"));
                        existCashAccBook.setBaseOutUndueMoney(rs.getDouble("FBaseUndueMoney"));
                    }
                }
            }

            /*
                     //为累计流入赋值(HashMap)
                     if (rs.getString("FInCode") != null &&
                         rs.getString("FInCode").trim().length() != 0
                         && !rs.getString("FIsCleared").equalsIgnoreCase("0")) {
                        reCashAccBook = new CashAccBookBean();
                        //设置报表类型标志
                        reCashAccBook.setReportType(this.reportType);
                        reCashAccBook.setKeyCode(rs.getString("FInCode"));
                        reCashAccBook.setCuryCode(rs.getString("FInCury") + "");
                        reCashAccBook.setInPutMoney(rs.getDouble("FInMoney"));
             reCashAccBook.setBaseInPutMoney(rs.getDouble("FBaseInMoney"));

                        strHmKey = reCashAccBook.getKeyCode() + "\f" +
                              reCashAccBook.getCuryCode();
             existCashAccBook = (CashAccBookBean) hmResult.get(strHmKey);
                        if (existCashAccBook == null) {
                           reCashAccBook.setKeyName(rs.getString("FInName") + "");
                           reCashAccBook.setOutPutMoney(0);
                           reCashAccBook.setAccBalance(0);
                           reCashAccBook.setBaseCuryCode(pub.getBaseCury());
                           reCashAccBook.setBaseOutPutMoney(0);
                           reCashAccBook.setBaseAccBalance(0);

                           hmResult.put(strHmKey, reCashAccBook);
                        }
                        else {
                           existCashAccBook.setCuryCode(reCashAccBook.
                                                        getCuryCode());
                           existCashAccBook.setInPutMoney(reCashAccBook.
                                                          getInPutMoney());
                           existCashAccBook.setBaseInPutMoney(reCashAccBook.
             getBaseInPutMoney());
                        }
                     }

                     //为累计流出赋值(HashMap)
                     if (rs.getString("FOutCode") != null &&
                         rs.getString("FOutCode").trim().length() != 0
                         && !rs.getString("FIsCleared").equalsIgnoreCase("0")) {
                        reCashAccBook = new CashAccBookBean();
                        //设置报表类型标志
                        reCashAccBook.setReportType(this.reportType);
                        reCashAccBook.setKeyCode(rs.getString("FOutCode"));
                        reCashAccBook.setCuryCode(rs.getString("FOutCury") + "");
                        reCashAccBook.setOutPutMoney(rs.getDouble("FOutMoney"));
             reCashAccBook.setBaseOutPutMoney(rs.getDouble("FBaseOutMoney"));

                        strHmKey = reCashAccBook.getKeyCode() + "\f" +
                              reCashAccBook.getCuryCode();
             existCashAccBook = (CashAccBookBean) hmResult.get(strHmKey);
                        if (existCashAccBook == null) {
             reCashAccBook.setKeyName(rs.getString("FOutName") + "");
                           reCashAccBook.setInPutMoney(0);
                           reCashAccBook.setAccBalance(0);
                           reCashAccBook.setBaseCuryCode(pub.getBaseCury());
                           reCashAccBook.setBaseInPutMoney(0);
                           reCashAccBook.setBaseAccBalance(0);

                           hmResult.put(strHmKey, reCashAccBook);
                        }
                        else {
                           existCashAccBook.setCuryCode(reCashAccBook.
                                                        getCuryCode());
                           existCashAccBook.setOutPutMoney(reCashAccBook.
                                                           getOutPutMoney());
                           existCashAccBook.setBaseOutPutMoney(reCashAccBook.
                                 getBaseOutPutMoney());
                        }
                     }
             */
        } else if (this.reportType.equalsIgnoreCase("detail")) {
            //为累计流入赋值(HashMap)
            if (rs.getString("FCashAccCode") != null &&
                rs.getString("FCashAccCode").trim().length() != 0) { //&& !rs.getString("FIsCleared").equalsIgnoreCase("0")
                reCashAccBook = new CashAccBookBean();
                //设置报表类型标志
                reCashAccBook.setReportType(this.reportType);
                reCashAccBook.setKeyCode(rs.getString("FCashAccCode") + "");
                reCashAccBook.setSettleDate(YssFun.formatDate(rs.getDate(
                    "FTransferDate")));
                reCashAccBook.setSubNum(rs.getString("FSubNum"));
//            reCashAccBook.setInPutMoney(rs.getDouble("FMoney"));
//            reCashAccBook.setBaseInPutMoney(rs.getDouble("FBaseMoney"));
                reCashAccBook.setCuryCode(rs.getString("FCuryCode"));
                reCashAccBook.setSettleMark(rs.getString("FIsCleared"));

                strHmKey = reCashAccBook.getSettleMark() + "\f" +
                    reCashAccBook.getSubNum();

                existCashAccBook = (CashAccBookBean) hmResult.get(strHmKey);
                if (existCashAccBook == null) {
                    reCashAccBook.setKeyName(rs.getString("FCashAccName") + "");
                    reCashAccBook.setAccType(rs.getString("FAccTypeName") + "");
                    reCashAccBook.setAccSubType(rs.getString("FSubAccTypeName") + "");
                    reCashAccBook.setBaseCuryCode(pub.getBaseCury());
                    if (rs.getInt("FInOut") == 1) {
                        reCashAccBook.setInPutMoney(rs.getDouble("FMoney"));
                        reCashAccBook.setBaseInPutMoney(rs.getDouble("FBaseMoney"));
                    } else if (rs.getInt("FInOut") == -1) {
                        reCashAccBook.setOutPutMoney(rs.getDouble("FMoney"));
                        reCashAccBook.setBaseOutPutMoney(rs.getDouble("FBaseMoney"));
                    }
                    hmResult.put(strHmKey, reCashAccBook);
                } else {
                    if (rs.getInt("FInOut") == 1) {
                        existCashAccBook.setInPutMoney(rs.getDouble("FMoney"));
                        existCashAccBook.setBaseInPutMoney(rs.getDouble("FBaseMoney"));
                    } else if (rs.getInt("FInOut") == -1) {
                        existCashAccBook.setOutPutMoney(rs.getDouble("FMoney"));
                        existCashAccBook.setBaseOutPutMoney(rs.getDouble("FBaseMoney"));
                    }

                }
            }
            /*
                     //为累计流出赋值(HashMap)
                     if (rs.getString("FOutCashAccCode") != null &&
                         rs.getString("FOutCashAccCode").trim().length() != 0) { //&& !rs.getString("FIsCleared").equalsIgnoreCase("0")
                        reCashAccBook = new CashAccBookBean();
                        //设置报表类型标志
                        reCashAccBook.setReportType(this.reportType);
                        reCashAccBook.setKeyCode(rs.getString("FOutCashAccCode"));
                        reCashAccBook.setSettleDate(YssFun.formatDate(rs.getDate(
                              "FTransferDate")));
                        reCashAccBook.setOutPutMoney(rs.getDouble("FOutMoney"));
             reCashAccBook.setBaseOutPutMoney(rs.getDouble("FBaseOutMoney"));
                        //reCashAccBook.setSettleMark(rs.getString("FIsCleared"));

                        strHmKey = //reCashAccBook.getSettleMark() + "\f" +
                              reCashAccBook.getKeyCode() + "\f" +
                              reCashAccBook.getSettleDate();
             existCashAccBook = (CashAccBookBean) hmResult.get(strHmKey);
                        if (existCashAccBook == null) {
             reCashAccBook.setKeyName(rs.getString("FOutCashAccName") + "");
             reCashAccBook.setAccType(rs.getString("FOutAccTypeName") + "");
             reCashAccBook.setAccSubType(rs.getString("FOutSubAccTypeName") +
                                                       "");
             reCashAccBook.setCuryCode(rs.getString("FOutCuryCode"));
                           reCashAccBook.setInPutMoney(0);
                           reCashAccBook.setBaseCuryCode(pub.getBaseCury());
                           reCashAccBook.setBaseInPutMoney(0);

                           hmResult.put(strHmKey, reCashAccBook);
                        }
                        else {
                           existCashAccBook.setOutPutMoney(YssD.add(reCashAccBook.
                                 getOutPutMoney(),
                                 existCashAccBook.getOutPutMoney()));
             existCashAccBook.setBaseOutPutMoney(YssD.add(reCashAccBook.
                                 getBaseOutPutMoney(),
                                 existCashAccBook.getBaseOutPutMoney()));
                        }
                     }
             */
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
