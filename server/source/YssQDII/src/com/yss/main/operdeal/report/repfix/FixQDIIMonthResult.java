package com.yss.main.operdeal.report.repfix;

import com.yss.util.YssException;
import com.yss.base.BaseAPOperValue;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.util.*;

import java.util.HashMap;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * <p>Title: 《合格境内机构投资者境外证券投资月报表（一）》</p>
 *
 * <p>Description: 根据建行报表需求《合格境内机构投资者境外证券投资月报表（一）》创建，需要单独的数据表储存报表数据</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FixQDIIMonthResult
    extends BaseAPOperValue {
    private String domesticAcctCode = ""; //境内托管帐户
    private String foreignAcctCode = ""; //境外账户
    private String portCode = ""; //组合代码
    private java.util.Date beginDate; //期初日期
    private java.util.Date endDate; //期末日期
    private String exRateSrcCode; //汇率来源代码

    private String baseCury = ""; //基础货币
    private java.util.Date fundSetupDay; //基金成立日
    private Calendar caEndDate = new GregorianCalendar(); //以期末日期为当前日期的日历
    private Calendar caFundDate = new GregorianCalendar(); //以基金成立日期为当前日期的日历

    //哈希 Key
    private String keydeposits = "0101"; //银行存款
    private String keyMoneyMarketTool = "0102"; //货币市场工具
    private String keyBond = "0103"; //债券
    private String keyStock = "0104"; //股票
    private String keyFund = "0105"; //基金
    private String keyDerivative = "0106"; //衍生产品
    private String keyDemandDeposits = "020101"; //活期存款
    private String keyTimeDeposits = "020102"; //定期存款
    private String keyIncomeForeignAcct = "020201"; //境外证券投资外汇账户划入
    private String keyIncomeInterest = "020203"; //利息收入
    private String keyExpendForeignAcct = "020301"; //划往境外证券投资外汇账户
    private String keyExpendRedemption = "020303"; //支付赎回款
    private String keyFundDividend = "020304"; //基金分红
    private String keyTrusteeship = "020305"; //托管费
    private String keyCustodian = "020306"; //管理费
    private String keyCharges = "020307"; //各类手续费
    private String keyOtherExpend = "020308"; //其他支出

    public FixQDIIMonthResult() {
    }

    public void init(Object bean) throws YssException {

        String reqAry[] = null;
        String sRowStr = (String) bean;
        if (sRowStr.trim().length() == 0) {
            return;
        }
        reqAry = sRowStr.split("\n");
        domesticAcctCode = reqAry[0].split("\r")[1];
        foreignAcctCode = reqAry[1].split("\r")[1];
        portCode = reqAry[2].split("\r")[1];
        endDate = YssFun.toDate(reqAry[3].split("\r")[1]);
        exRateSrcCode = reqAry[4].split("\r")[1];

        //获取基础货币
        this.baseCury = pub.getPortBaseCury(portCode);// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A

        //获取基金成立日期
        PortfolioBean port = (PortfolioBean) pub.getParaSettingCtx().getBean(
            "portfolio");
        port.setYssPub(pub);
        port.setPortCode(this.portCode);
        port.getSetting();
        this.fundSetupDay = port.getInceptionDate();

        //设置日历，判断日期合法性
        caEndDate.setTime(this.endDate);
        caFundDate.setTime(this.fundSetupDay);
        beginDate = (new GregorianCalendar(caEndDate.get(Calendar.YEAR),
                                           caEndDate.get(Calendar.MONTH), 0)).
            getTime();
        //判断选中的期末日期是否在基金成立之前
        if (caEndDate.before(caFundDate)) {
            throw new YssException("对不起，您选择的报表期末日期在基金成立日期之前，请重新选择！");
        }
    }

    public Object invokeOperMothed() throws YssException {
        HashMap hmResult = new HashMap();
        createDataTable();
        getMonthInitValue(hmResult);
        getInvestmentMonthFinishValue(hmResult);
        getInvestmentMonthHappenValue(hmResult);
        getTrusteeshipAcctHappenValue(hmResult);
        getTrusteeshipAcctEndValue(hmResult);
        getTotalValue(hmResult);
        saveResult(hmResult);
        return null;
    }

    /**
     * 创建表 Tb_FixRep_JHMonth
     * @throws YssException
     */
    public void createDataTable() throws YssException {
        String tableName = "";
        String strSql = "";
        String groupCode = "";
        try {
            tableName = pub.yssGetTableName("Tb_FixRep_JHMonth");
            if (dbl.yssTableExist(tableName)) {
                return;
            }
            groupCode = pub.getAssetGroupCode();
            if (dbl.dbType == YssCons.DB_ORA) {
                strSql =
                    "CREATE TABLE " + tableName + "(\n" +
                    "    FBeginDate      DATE             NOT NULL,\n" +
                    "    FEndDate        DATE             NOT NULL,\n" +
                    "    FPortCode       VARCHAR2(20)     NOT NULL,\n" +
                    "    FOrderCode      VARCHAR2(100)    NOT NULL,\n" +
                    "    FKeyCode        VARCHAR2(100),\n" +
                    "    FKeyName        VARCHAR2(100),\n" +
                    "    FBeginValue     NUMBER(18, 4),\n" +
                    "    FHappenValue    NUMBER(18, 4),\n" +
                    "    FEndValue       NUMBER(18, 4),\n" +
                    "    CONSTRAINT PK_Tb_" + groupCode +
                    "_FixRep_JHMonth PRIMARY KEY (FBeginDate, FEndDate, FPortCode, FOrderCode)\n" +
                    ")";
            } else {
                strSql =
                    "CREATE TABLE " + tableName + "(\n" +
                    "    FBeginDate      DATE              NOT NULL,\n" +
                    "    FEndDate        DATE              NOT NULL,\n" +
                    "    FPortCode       VARCHAR(20)       NOT NULL,\n" +
                    "    FOrderCode      VARCHAR(100)      NOT NULL,\n" +
                    "    FKeyCode        VARCHAR(100),\n" +
                    "    FKeyName        VARCHAR(100),\n" +
                    "    FBeginValue     DECIMAL(18, 4),\n" +
                    "    FHappenValue    DECIMAL(18, 4),\n" +
                    "    FEndValue       DECIMAL(18, 4),\n" +
                    "    CONSTRAINT PK_Tb_" + groupCode +
                    "_JHMonth PRIMARY KEY (FBeginDate, FEndDate, FPortCode, FOrderCode)\n" +
                    ")";
            }
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("创建表 " + tableName + "出错！\r\n" + e.getMessage());
        }
    }

    /**
     * 将结果存入数据表
     * @param hmResult HashMap
     * @throws YssException
     */
    public void saveResult(HashMap hmResult) throws YssException {
        Connection conn = dbl.loadConnection();
        PreparedStatement pstmt = null;
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "DELETE FROM " + pub.yssGetTableName("Tb_FixRep_JHMonth") +
                " WHERE FBeginDate = " + dbl.sqlDate(this.beginDate) +
                " AND " + "TO_CHAR(FEndDate, 'yyyy-MM') =" +
                " TO_CHAR(" + dbl.sqlDate(this.endDate) + ", 'yyyy-MM')" +
                " AND FPortCode = " + dbl.sqlString(this.portCode);
            dbl.executeSql(strSql);
            strSql = "INSERT INTO " + pub.yssGetTableName("Tb_FixRep_JHMonth") +
                " VALUES(?,?,?,?,?,?,?,?,?)";
            pstmt = dbl.openPreparedStatement(strSql);
            Iterator it = hmResult.values().iterator();
            while (it.hasNext()) {
                MonthResultPojo pojo = (MonthResultPojo) it.next();
                pstmt.setDate(1, YssFun.toSqlDate(pojo.beginDate));
                pstmt.setDate(2, YssFun.toSqlDate(pojo.endDate));
                pstmt.setString(3, pojo.portCode);
                pstmt.setString(4, pojo.orderCode);
                pstmt.setString(5, pojo.keyCode);
                pstmt.setString(6, pojo.keyName);
                pstmt.setDouble(7, pojo.beginValue);
                pstmt.setDouble(8, pojo.happenValue);
                pstmt.setDouble(9, pojo.endValue);
                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存报表结果出错！\r\n" + e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(pstmt);
        }
    }

    /**
     * 获取合计数
     * @param hmResult HashMap
     * @throws YssException
     */
    public void getTotalValue(HashMap hmResult) throws YssException {
        ArrayList alTotal = new ArrayList();
        try {
            Iterator it = hmResult.keySet().iterator();
            MonthResultPojo total1 = new MonthResultPojo();
            total1.keyName = "合计";
            total1.keyCode = "total";
            MonthResultPojo total2 = new MonthResultPojo();
            total2.keyName = "合计";
            total2.keyCode = "total";
            MonthResultPojo total3 = new MonthResultPojo();
            total3.keyName = "合计";
            total3.keyCode = "total";
            MonthResultPojo total4 = new MonthResultPojo();
            total4.keyName = "合计";
            total4.keyCode = "total";
            while (it.hasNext()) {
                String keyValue = (String) it.next();
                if (keyValue.substring(0, 2).equalsIgnoreCase("01")) {
                    MonthResultPojo resultPojo = (MonthResultPojo) hmResult.get(
                        keyValue);
                    resultPojo.beginDate = this.beginDate;
                    resultPojo.endDate = this.endDate;
                    resultPojo.portCode = this.portCode;
                    resultPojo.orderCode = keyValue;
                    total1.beginDate = resultPojo.beginDate;
                    total1.endDate = resultPojo.endDate;
                    total1.portCode = resultPojo.portCode;
                    total1.orderCode = keyValue.substring(0, 2) + "total";
                    total1.beginValue += resultPojo.beginValue;
                    total1.happenValue += resultPojo.happenValue;
                    total1.endValue += resultPojo.endValue;

                    //hmResult.put(total1.orderCode, total1);
                    alTotal.add(total1);
                } else if (keyValue.substring(0, 4).equalsIgnoreCase("0201")) {
                    MonthResultPojo resultPojo = (MonthResultPojo) hmResult.get(
                        keyValue);
                    resultPojo.beginDate = this.beginDate;
                    resultPojo.endDate = this.endDate;
                    resultPojo.portCode = this.portCode;
                    resultPojo.orderCode = keyValue;
                    total2.beginDate = resultPojo.beginDate;
                    total2.endDate = resultPojo.endDate;
                    total2.portCode = resultPojo.portCode;
                    total2.orderCode = keyValue.substring(0, 4) + "total";
                    total2.beginValue += resultPojo.beginValue;
                    total2.happenValue += resultPojo.happenValue;
                    total2.endValue += resultPojo.endValue;

                    //hmResult.put(total2.orderCode, total2);
                    alTotal.add(total2);
                } else if (keyValue.substring(0, 4).equalsIgnoreCase("0202")) {
                    MonthResultPojo resultPojo = (MonthResultPojo) hmResult.get(
                        keyValue);
                    resultPojo.beginDate = this.beginDate;
                    resultPojo.endDate = this.endDate;
                    resultPojo.portCode = this.portCode;
                    resultPojo.orderCode = keyValue;
                    total3.beginDate = resultPojo.beginDate;
                    total3.endDate = resultPojo.endDate;
                    total3.portCode = resultPojo.portCode;
                    total3.orderCode = keyValue.substring(0, 4) + "total";
                    total3.beginValue += resultPojo.beginValue;
                    total3.happenValue += resultPojo.happenValue;
                    total3.endValue += resultPojo.endValue;

                    //hmResult.put(total2.orderCode, total2);
                    alTotal.add(total3);
                } else if (keyValue.substring(0, 4).equalsIgnoreCase("0203")) {
                    MonthResultPojo resultPojo = (MonthResultPojo) hmResult.get(
                        keyValue);
                    resultPojo.beginDate = this.beginDate;
                    resultPojo.endDate = this.endDate;
                    resultPojo.portCode = this.portCode;
                    resultPojo.orderCode = keyValue;
                    total4.beginDate = resultPojo.beginDate;
                    total4.endDate = resultPojo.endDate;
                    total4.portCode = resultPojo.portCode;
                    total4.orderCode = keyValue.substring(0, 4) + "total";
                    total4.beginValue += resultPojo.beginValue;
                    total4.happenValue += resultPojo.happenValue;
                    total4.endValue += resultPojo.endValue;
                    //hmResult.put(total2.orderCode, total2);
                    alTotal.add(total4);
                }
            }
            for (int i = 0; i < alTotal.size(); i++) {
                MonthResultPojo resultPojo = (MonthResultPojo) alTotal.get(i);
                hmResult.put(resultPojo.orderCode, resultPojo);
            }
        } catch (Exception e) {
            throw new YssException("计算合计数据出错！\r\n" + e.getMessage());
        }
    }

    /**
     * 获取境内托管账户情况的期末数
     * @param hmResult HashMap
     * @throws YssException
     */
    public void getTrusteeshipAcctEndValue(HashMap hmResult) throws YssException {
        try {
            Iterator it = hmResult.keySet().iterator();
            while (it.hasNext()) {
                String keyValue = (String) it.next();
                if (keyValue.substring(0, 2).equalsIgnoreCase("02")) {
                    MonthResultPojo resultPojo = (MonthResultPojo) hmResult.get(
                        keyValue);
                    resultPojo.endValue = resultPojo.beginValue +
                        resultPojo.happenValue;
                }
            }
        } catch (Exception e) {
            throw new YssException("获取投资情况期末数据出错！\r\n" + e.getMessage());
        }
    }

    /**
     * 获取境内托管账户情况的发生数
     * @param hmResult HashMap
     * @throws YssException
     */
    public void getTrusteeshipAcctHappenValue(HashMap hmResult) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            //----------------取活期和定期存款的发生数----------------//
            strSql =
                "SELECT SUM(FMarketValue) AS FMarketValue, FGradeType2 FROM (" +
                " SELECT FMarketValue * CASE WHEN a.FCuryCode <> " +
                dbl.sqlString(this.baseCury) +
                " THEN c.FExRate1 ELSE 1 END AS FMarketValue, FGradeType2" +
                " FROM (SELECT FNavDate, FCuryCode, FGradeType2, FMarketValue" +
                " FROM " + pub.yssGetTableName("TB_Data_NavData") +
                " WHERE FReTypeCode = 'Cash'" +
                " AND FInvMgrCode = 'total'" +
                " AND FGradeType2 IN (" + operSql.sqlCodes(this.domesticAcctCode) +
                ")" +
                " AND FDetail = 0" +
                " AND FPortCode = " + dbl.sqlString(this.portCode) +
                " AND FNavDate = " + dbl.sqlDate(this.endDate) + ") a" +
                " LEFT JOIN (SELECT FExRateSrcCode, FCuryCode, MAX(FExRateDate) AS FExRateDate" +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " WHERE FExRateDate <= " + dbl.sqlDate(this.endDate) +
                " AND FExRateSrcCode = " + dbl.sqlString(this.exRateSrcCode) +
                " AND FCheckState = 1" +
                " GROUP BY FExRateSrcCode, FCuryCode) b ON a.FCuryCode = b.FCuryCode" +
                " LEFT JOIN (SELECT FExRateSrcCode, FCuryCode, FExRateDate, FExRate1" +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " WHERE FCheckState = 1) c" +
                " ON b.FExRateSrcCode = c.FExRateSrcCode AND b.FCuryCode = c.FCuryCode AND b.FExRateDate = c.FExRateDate)" +
                " GROUP BY FGradeType2";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FGradeType2").equals("0101")) {
                    MonthResultPojo monthHappenValue = (MonthResultPojo) hmResult.
                        get(this.keyDemandDeposits);
                    monthHappenValue.happenValue = rs.getDouble("FMarketValue");
                } else if (rs.getString("FGradeType2").equals("0102")) {
                    MonthResultPojo monthHappenValue = (MonthResultPojo) hmResult.
                        get(this.keyTimeDeposits);
                    monthHappenValue.happenValue = rs.getDouble("FMarketValue");
                }
            }
            dbl.closeResultSetFinal(rs);
            //-----------------------------------------------------//
            //-----------境外证券投资外汇账户划入-----------//
            strSql = "SELECT SUM(FMoney * CASE WHEN a.FCuryCode <> " +
                dbl.sqlString(baseCury) +
                " THEN NVL(FExRate1, 1) ELSE 1 END) AS FMoney" +
                " FROM (SELECT FTransDate, FMoney, FCuryCode" +
                " FROM " + pub.yssGetTableName("Tb_Cash_Transfer") + " a" +
                " JOIN " + pub.yssGetTableName("TB_Cash_SubTransfer") +
                " b ON a.FNum = b.FNum" +
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " c ON b.FCashAccCode = c.FCashAccCode" +
                " WHERE a.FTransDate BETWEEN " + dbl.sqlDate(this.beginDate) +
                " AND " + dbl.sqlDate(this.endDate) +
                " AND b.FInOut = 1" +
                " AND b.FPortCode = " + dbl.sqlString(this.portCode) +
                " AND b.FCashAccCode IN (" +
                operSql.sqlCodes(this.foreignAcctCode) + ")" +
                " AND a.FCheckState = 1" +
                " AND b.FCheckState = 1) a" +
                " LEFT JOIN (SELECT FExRateSrcCode, FCuryCode, FExRateDate, FExRate1" +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " WHERE FCheckState = 1" +
                " AND FExRateSrcCode = " + dbl.sqlString(this.exRateSrcCode) +
                " ) b ON a.FCuryCode = b.FCuryCode AND a.FTransDate = b.FExRateDate";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                MonthResultPojo monthHappenValue = (MonthResultPojo) hmResult.
                    get(this.keyIncomeForeignAcct);
                monthHappenValue.happenValue = rs.getDouble("FMoney");
            }
            dbl.closeResultSetFinal(rs);
            //-------------------------------------------//
            //-----------------利息收入-----------------//
            strSql = "SELECT SUM(FMoney * CASE WHEN a.FCuryCode <> " +
                dbl.sqlString(baseCury) +
                " THEN NVL(FExRate1, 1) ELSE 1 END) AS FMoney" +
                " FROM (SELECT FTransDate, FMoney, FCuryCode" +
                " FROM " + pub.yssGetTableName("Tb_Data_CashPayRec") +
                " WHERE FTransDate BETWEEN " + dbl.sqlDate(this.beginDate) +
                " AND " + dbl.sqlDate(this.endDate) +
                " AND FPortCode = " + dbl.sqlString(this.portCode) +
                " AND FCashAccCode IN (" + operSql.sqlCodes(this.foreignAcctCode) +
                ")" +
                " AND FTsfTypeCode = '02'" +
                " AND FSubTsfTypeCode = '02DE'" +
                " AND FCheckState = 1) a" +
                " LEFT JOIN (SELECT FExRateSrcCode, FCuryCode, FExRateDate, FExRate1" +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " WHERE FCheckState = 1" +
                " AND FExRateSrcCode = " + dbl.sqlString(this.exRateSrcCode) +
                " ) b ON a.FCuryCode = b.FCuryCode AND a.FTransDate = b.FExRateDate";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                MonthResultPojo monthHappenValue = (MonthResultPojo) hmResult.get(this.keyIncomeInterest);
                monthHappenValue.happenValue = rs.getDouble("FMoney");
            }
            dbl.closeResultSetFinal(rs);
            //-----------------------------------------//
            //----------------取支付赎回款----------------//
            strSql = "SELECT SUM(a.FSettleMoney * CASE WHEN a.FCuryCode = " +
                dbl.sqlString(this.baseCury) + " THEN 1 ELSE " +
                dbl.sqlIsNull("b.FEXRATE1", "1") + " END) AS FSettleMoney" +
                " FROM (SELECT CASE WHEN FSettleMoney IS NULL THEN FSellMoney ELSE FSettleMoney END AS FSettleMoney, FCuryCode, FTradeDate" +
                " FROM " + pub.yssGetTableName("TB_TA_TRADE") +
                " WHERE FSETTLEDATE BETWEEN " + dbl.sqlDate(this.beginDate) +
                " AND " + dbl.sqlDate(this.endDate) +
                " AND FSELLTYPE = '02'" +
                " AND FCheckState = 1" +
                " AND FPORTCODE = " + dbl.sqlString(this.portCode) +
                " AND FSETTLESTATE = 1" +
                " AND FCASHACCCODE IN (" +
                operSql.sqlCodes(this.domesticAcctCode) + ")) a" +
                " LEFT JOIN (SELECT FEXRATESRCCODE, FCURYCODE, FEXRATEDATE, FEXRATE1" +
                " FROM " + pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
                " WHERE FEXRATESRCCODE = " + dbl.sqlString(this.exRateSrcCode) +
                " AND FCheckState = 1) b " +
                " ON a.FCURYCODE = b.FCURYCODE AND a.FTradeDate = b.FExRateDate";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                MonthResultPojo monthHappenValue = (MonthResultPojo) hmResult.
                    get(this.
                        keyExpendRedemption);
                monthHappenValue.happenValue = rs.getDouble("FSettleMoney");
            }
            dbl.closeResultSetFinal(rs);
            //------------------------------------------//
            //----------------取托管费、管理费----------------//
            strSql = "SELECT SUM(a.FMoney * CASE WHEN a.FCuryCode = " +
                dbl.sqlString(this.baseCury) + " THEN 1 ELSE " +
                dbl.sqlIsNull("b.FEXRATE1", "1") +
                " END) AS FMoney, FIVPayCatCode" +
                " FROM (SELECT FMoney, FCuryCode, FTransDate, FIVPayCatCode" +
                " FROM " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " WHERE FTransDate BETWEEN " + dbl.sqlDate(this.beginDate) +
                " AND " + dbl.sqlDate(this.endDate) +
                " AND FIVPayCatCode IN ('IV001', 'IV002')" +
                " AND FCheckState = 1" +
                " AND FPortCode = " + dbl.sqlString(this.portCode) + ") a" +
                " LEFT JOIN (SELECT FEXRATESRCCODE, FCURYCODE, FEXRATEDATE, FEXRATE1 " +
                " FROM " + pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
                " WHERE FEXRATESRCCODE = " + dbl.sqlString(this.exRateSrcCode) +
                " AND FCheckState = 1) b ON a.FCURYCODE = b.FCURYCODE" +
                " AND a.FTransDate = b.FExRateDate" +
                " GROUP BY FIVPayCatCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FIVPayCatCode").equalsIgnoreCase("IV001")) {
                    MonthResultPojo monthHappenValue = (MonthResultPojo) hmResult.
                        get(this.keyCustodian);
                    monthHappenValue.happenValue = rs.getDouble("FMoney");
                } else if (rs.getString("FIVPayCatCode").equalsIgnoreCase("IV002")) {
                    MonthResultPojo monthHappenValue = (MonthResultPojo) hmResult.
                        get(this.keyTrusteeship);
                    monthHappenValue.happenValue = rs.getDouble("FMoney");
                }
            }
            dbl.closeResultSetFinal(rs);
            //------------------------------------------//
            //-----------------取其他支出----------------//
            strSql = "SELECT SUM(b.FMoney * CASE WHEN c.FCuryCode <> " +
                dbl.sqlString(this.baseCury) +
                " THEN FEXRATE1 ELSE 1 END) AS FMoney" +
                " FROM (SELECT FNum, FTransDate" +
                " FROM " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " WHERE FTsfTypeCode = '03'" +
                " AND FSubTsfTypeCode = '0303'" +
                " AND FCheckState = 1" +
                " AND FTransDate BETWEEN " + dbl.sqlDate(this.beginDate) +
                " AND " + dbl.sqlDate(this.endDate) +
                " ) a JOIN (SELECT FMoney, FCashAccCode, FNum" + //缺少") a"
                " FROM " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " WHERE FCheckState = 1" +
                " AND FCashAccCode IN (" +
                operSql.sqlCodes(this.domesticAcctCode) +
                ")) b ON a.FNum = b.FNum" +
                " LEFT JOIN (SELECT FCuryCode, FCashAccCode FROM " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " ) c ON b.FCashAccCode = c.FCashAccCode" +
                " LEFT JOIN (SELECT FEXRATESRCCODE, FCURYCODE, FEXRATEDATE, FEXRATE1" +
                " FROM " + pub.yssGetTableName("TB_DATA_EXCHANGERATE") +
                " WHERE FEXRATESRCCODE = " + dbl.sqlString(this.exRateSrcCode) +
                " AND FCheckState = 1) d ON d.FCURYCODE = c.FCURYCODE" +
                " AND a.FTransDate = d.FExRateDate";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                MonthResultPojo monthHappenValue = (MonthResultPojo) hmResult.
                    get(this.keyOtherExpend);
                monthHappenValue.happenValue = rs.getDouble("FMoney");
            }
            //------------------------------------------//
        } catch (Exception e) {
            throw new YssException("获取境内托管帐户情况出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取投资情况的发生数
     * @param hmResult HashMap
     * @throws YssException
     */
    public void getInvestmentMonthHappenValue(HashMap hmResult) throws
        YssException {
        MonthResultPojo monthFinishValue = null;
        try {
            Iterator it = hmResult.values().iterator();
            while (it.hasNext()) {
                monthFinishValue = (MonthResultPojo) it.next();
                monthFinishValue.happenValue = monthFinishValue.endValue -
                    monthFinishValue.beginValue;
            }
        } catch (Exception e) {
            throw new YssException("获取投资情况发生数出错\r\n" + e.getMessage());
        }
    }

    /**
     * 获取投资情况的期末数
     * @param hmResult HashMap
     * @throws YssException
     */
    public void getInvestmentMonthFinishValue(HashMap hmResult) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        MonthResultPojo monthFinishValue = null;
        try {
            //----------------取银行存款期末值-----------------//
            strSql = "SELECT SUM(FMarketValue * CASE WHEN a.FCuryCode <> " +
                dbl.sqlString(this.baseCury) +
                " THEN c.FExRate1 ELSE 1 END) AS FMarketValue" +
                " FROM (SELECT FOrderCode, FMarketValue, FCuryCode, FNavDate" +
                " FROM " + pub.yssGetTableName("TB_Data_NavData") +
                " WHERE FReTypeCode = 'Cash'" +
                " AND FInvMgrCode = 'total'" +
                " AND FGradeType1 = '01'" +
                " AND FDetail = 3" +
                " AND FPortCode = " + dbl.sqlString(this.portCode) +
                " AND FNavDate = " + dbl.sqlDate(this.endDate) + ") a" +
                " LEFT JOIN (SELECT FExRateSrcCode, FCuryCode, MAX(FExRateDate) AS FExRateDate" +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " WHERE FExRateDate <= " + dbl.sqlDate(this.endDate) +
                " AND FCheckState = 1" +
                " AND FExRateSrcCode = " + dbl.sqlString(this.exRateSrcCode) +
                " GROUP BY FExRateSrcCode, FCuryCode) b ON a.FCuryCode = b.FCuryCode" +
                " LEFT JOIN (SELECT FExRateSrcCode, FCuryCode, FExRateDate, FExRate1" +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " WHERE FCheckState = 1) c" +
                " ON b.FExRateSrcCode = c.FExRateSrcCode AND b.FCuryCode = c.FCuryCode AND b.FExRateDate = c.FExRateDate";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                monthFinishValue = (MonthResultPojo) hmResult.get(this.keydeposits);
                monthFinishValue.endValue = rs.getDouble("FMarketValue");
            }
            dbl.closeResultSetFinal(rs);
            //-----------------------------------------------//
            //-------------取货币市场工具、债券、股票、基金期末值-----------------//
            strSql =
                "SELECT SUM(FMarketValue) AS FMarketValue, FGradeType1 FROM (" +
                " SELECT FMarketValue * CASE WHEN a.FCuryCode <> " +
                dbl.sqlString(this.baseCury) +
                " THEN c.FExRate1 ELSE 1 END AS FMarketValue, FGradeType1" +
                " FROM (SELECT FOrderCode, FMarketValue, FCuryCode, FNavDate, FGradeType1" +
                " FROM " + pub.yssGetTableName("TB_Data_NavData") +
                " WHERE FReTypeCode = 'Security'" +
                " AND FInvMgrCode = 'total'" +
                " AND FGradeType1 IN ('EQ', 'TR', 'FI', 'RE')" +
                " AND FDetail = 4" +
                " AND FPortCode = " + dbl.sqlString(this.portCode) +
                " AND FNavDate = " + dbl.sqlDate(this.endDate) + ") a" +
                " LEFT JOIN (SELECT FExRateSrcCode, FCuryCode, MAX(FExRateDate) AS FExRateDate" +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " WHERE FExRateDate <= " + dbl.sqlDate(this.endDate) +
                " AND FCheckState = 1" +
                " AND FExRateSrcCode = " + dbl.sqlString(this.exRateSrcCode) +
                " GROUP BY FExRateSrcCode, FCuryCode) b ON a.FCuryCode = b.FCuryCode" +
                " LEFT JOIN (SELECT FExRateSrcCode, FCuryCode, FExRateDate, FExRate1" +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " WHERE FCheckState = 1) c" +
                " ON b.FExRateSrcCode = c.FExRateSrcCode AND b.FCuryCode = c.FCuryCode AND b.FExRateDate = c.FExRateDate)" +
                " GROUP BY FGradeType1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FGradeType1").equalsIgnoreCase("RE")) {
                    monthFinishValue = (MonthResultPojo) hmResult.get(this.
                        keyMoneyMarketTool);
                    monthFinishValue.endValue = rs.getDouble("FMarketValue");
                } else if (rs.getString("FGradeType1").equalsIgnoreCase("FI")) {
                    monthFinishValue = (MonthResultPojo) hmResult.get(this.
                        keyBond);
                    monthFinishValue.endValue = rs.getDouble("FMarketValue");
                } else if (rs.getString("FGradeType1").equalsIgnoreCase("EQ")) {
                    monthFinishValue = (MonthResultPojo) hmResult.get(this.
                        keyStock);
                    monthFinishValue.endValue = rs.getDouble("FMarketValue");
                } else if (rs.getString("FGradeType1").equalsIgnoreCase("TR")) {
                    monthFinishValue = (MonthResultPojo) hmResult.get(this.
                        keyFund);
                    monthFinishValue.endValue = rs.getDouble("FMarketValue");
                }
            }
            dbl.closeResultSetFinal(rs);
            //--------------------------------------------------------//
            //-------------------取衍生产品期末值-----------------------//
            strSql = "SELECT SUM(FMarketValue * CASE WHEN a.FCuryCode <> " +
                dbl.sqlString(this.baseCury) +
                " THEN c.FExRate1 ELSE 1 END) AS FMarketValue" +
                " FROM (SELECT FOrderCode, FMarketValue, FCuryCode, FNavDate" +
                " FROM " + pub.yssGetTableName("TB_Data_NavData") +
                " WHERE FReTypeCode = 'Security'" +
                " AND FInvMgrCode = 'total'" +
                " AND FGradeType1 NOT IN ('EQ', 'TR', 'FI', 'RE')" +
                " AND FDetail = 4" +
                " AND FPortCode = " + dbl.sqlString(this.portCode) +
                " AND FNavDate = " + dbl.sqlDate(this.endDate) + ") a" +
                " LEFT JOIN (SELECT FExRateSrcCode, FCuryCode, MAX(FExRateDate) AS FExRateDate" +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " WHERE FExRateDate <= " + dbl.sqlDate(this.endDate) +
                " AND FExRateSrcCode = " + dbl.sqlString(this.exRateSrcCode) +
                " AND FCheckState = 1" +
                " GROUP BY FExRateSrcCode, FCuryCode) b ON a.FCuryCode = b.FCuryCode" +
                " LEFT JOIN (SELECT FExRateSrcCode, FCuryCode, FExRateDate, FExRate1" +
                " FROM " + pub.yssGetTableName("Tb_Data_ExchangeRate") +
                " WHERE FCheckState = 1) c" +
                " ON b.FExRateSrcCode = c.FExRateSrcCode AND b.FCuryCode = c.FCuryCode AND b.FExRateDate = c.FExRateDate";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                monthFinishValue = (MonthResultPojo) hmResult.get(this.
                    keyDerivative);
                monthFinishValue.endValue = rs.getDouble("FMarketValue");
            }
            //--------------------------------------------------------//
        } catch (Exception e) {
            throw new YssException("获取投资境况期末数出错\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取所有期初数
     * @param hmResult HashMap
     * @throws YssException
     */
    public void getMonthInitValue(HashMap hmResult) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            //判断是否是基金成立日期当月
            if (YssFun.addMonth(this.endDate, -1).compareTo(this.fundSetupDay) < 0) {
                //基金成立当月所有期初数为 0
                hmResult.put(this.keydeposits, new MonthResultPojo());
                hmResult.put(this.keyMoneyMarketTool, new MonthResultPojo());
                hmResult.put(this.keyBond, new MonthResultPojo());
                hmResult.put(this.keyStock, new MonthResultPojo());
                hmResult.put(this.keyFund, new MonthResultPojo());
                hmResult.put(this.keyDerivative, new MonthResultPojo());
                hmResult.put(this.keyDemandDeposits, new MonthResultPojo());
                hmResult.put(this.keyTimeDeposits, new MonthResultPojo());
                hmResult.put(this.keyIncomeForeignAcct, new MonthResultPojo());
                hmResult.put(this.keyIncomeInterest, new MonthResultPojo());
                hmResult.put(this.keyExpendForeignAcct, new MonthResultPojo());
                hmResult.put(this.keyExpendRedemption, new MonthResultPojo());
                hmResult.put(this.keyFundDividend, new MonthResultPojo());
                hmResult.put(this.keyTrusteeship, new MonthResultPojo());
                hmResult.put(this.keyCustodian, new MonthResultPojo());
                hmResult.put(this.keyCharges, new MonthResultPojo());
                hmResult.put(this.keyOtherExpend, new MonthResultPojo());
            } else {
                strSql = "SELECT FOrderCode, FEndValue FROM " +
                    pub.yssGetTableName("Tb_FixRep_JHMonth") +
                    " WHERE TO_CHAR(FEndDate, 'yyyy-MM')=" +
                    " TO_CHAR(" + dbl.sqlDate(this.beginDate) + ", 'yyyy-MM')" +
                    " AND FPortCode = " + dbl.sqlString(this.portCode) +
                    " AND FKeyCode is null";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    MonthResultPojo monthInitValue = new MonthResultPojo();
                    if (!rs.getString("FOrderCode").equals(this.keydeposits) &&
                        !rs.getString("FOrderCode").equals(this.keyDemandDeposits) &&
                        !rs.getString("FOrderCode").equals(this.keyTimeDeposits)) {
                        monthInitValue.beginValue = rs.getDouble("FEndValue");
                    }
                    hmResult.put(rs.getString("FOrderCode"), monthInitValue);
                }
            }
            if (hmResult.size() == 0) {
                throw new YssException("上月无报表，无法取上月期末数做作本月期初数");
            }
        } catch (Exception e) {
            throw new YssException("获取投资情况期初数出错！" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     *
     * <p>Title: 表 Tb_FixRep_JHMonth 的实体类</p>
     *
     * <p>Description: 表 Tb_FixRep_JHMonth 的实体类，用于装载 Tb_FixRep_JHMonth 的数据</p>
     *
     * <p>Copyright: Copyright (c) 2006</p>
     *
     * <p>Company: </p>
     *
     * @author not attributable
     * @version 1.0
     */
    class MonthResultPojo {
        public MonthResultPojo() {}

        public java.util.Date beginDate;
        public java.util.Date endDate;
        public String portCode = "";
        public String orderCode = "";
        public String keyCode = "";
        public String keyName = "";
        public double beginValue;
        public double happenValue;
        public double endValue;
    }

}
