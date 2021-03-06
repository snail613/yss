package com.yss.main.operdeal.businesswork.futures.futuresdistilldata;

import java.sql.*;

import com.yss.main.operdata.*;
import com.yss.main.operdeal.businesswork.*;
import com.yss.manager.*;
import com.yss.util.*;

public class CashRecDistill
    extends BaseBusinWork {
    public CashRecDistill() {
    }

    public String doOperation(String sType) throws YssException {
        try {
            getCashRecPayData(workDate, portCodes);
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        return "";
    }

    public void getCashRecPayData(java.util.Date dWorkDay, String sPortCodes) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        CashPecPayBean cashPay = null;
        CashPayRecAdmin cashAdmin = new CashPayRecAdmin();
        try {
            strSql = "SELECT a.*, b.*, c.FCuryCode" +
                " FROM (SELECT a.FNum, a.FTsfTypeCode, a.FTransDate, a.FStorageAmount, a.FBaseCuryRate," +
                " a.FPortCuryRate, a.FSettleState," +
                " a.FMoney - CASE" +
                " WHEN b.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " b.FMoney" +
                " END + CASE" +
                " WHEN c.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " c.FMoney" +
                " END AS FMoney," +
                " (a.FMoney - CASE" +
                " WHEN b.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " b.FMoney" +
                " END + CASE" +
                " WHEN c.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " c.FMoney" +
                " END) * a.FBaseCuryRate AS FBaseCuryMoney," +
                " (a.FMoney - CASE" +
                " WHEN b.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " b.FMoney" +
                " END + CASE" +
                " WHEN c.FMoney IS NULL THEN" +
                " 0" +
                " ELSE" +
                " c.FMoney" +
                " END) * a.FBaseCuryRate / a.FPortCuryRate AS FPortCuryMoney" +
                " FROM (SELECT *" +
                " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(dWorkDay) +
                " AND FTsfTypeCode = '09FU01'" +
                " AND FMoney <> 0) a" +
                " Left JOIN (SELECT FNum, FMoney" +
                " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(YssFun.addDay(dWorkDay, -1)) +
                " AND FTsfTypeCode = '09FU01') b ON a.FNum = b.FNum" +
                " LEFT JOIN (SELECT FNum, FMoney" +
                " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(dWorkDay) +
                " AND FTsfTypeCode = '19FU01') c ON a.FNum = c.FNum" +
                " UNION" +
                " SELECT FNum, FTsfTypeCode, FTransDate, FStorageAmount, FBaseCuryRate," +
                " FPortCuryRate, FSettleState, FMoney, FBaseCuryMoney, FPortCuryMoney" +
                " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(dWorkDay) +
                " AND FTsfTypeCode = '19FU01'" +
                " AND FMoney <> 0) a" +
                " JOIN " + pub.yssGetTableName("TB_Data_FuturesTrade") +
                " b ON a.FNum = b.FNum" +
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " c ON b.FChageBailAcctCode = c.FCashAccCode" +
                " WHERE a.FSettleState = 1" +
                " AND b.FPortCode IN (" + sPortCodes + ")";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                cashPay = new CashPecPayBean();
                cashPay.setPortCode(rs.getString("FPortCode"));
                cashPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                cashPay.setBrokerCode(rs.getString("FBrokerCode"));
                cashPay.setCashAccCode(rs.getString("FChageBailAcctCode"));
                cashPay.setCategoryCode(" ");
                cashPay.setInvestManagerCode(rs.getString("FInvMgrCode"));
                cashPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                cashPay.setSubTsfTypeCode("06FU01");
                cashPay.setTsfTypeCode("06");
                cashPay.setMoney(rs.getDouble("FMoney"));
                cashPay.setPortCuryMoney(rs.getDouble("FPortCuryMoney"));
                cashPay.setBaseCuryMoney(rs.getDouble("FBaseCuryMoney"));
                if (rs.getString("FTsfTypeCode").equalsIgnoreCase("09FU01")) {
                    cashPay.setInOutType(1);
                } else {
                    cashPay.setInOutType( -1);
                }
                cashPay.setTradeDate(rs.getDate("FTransDate"));
                cashPay.setCuryCode(rs.getString("FCuryCode"));
                cashAdmin.addList(cashPay);
            }
            cashAdmin.setYssPub(pub);
            cashAdmin.insert(dWorkDay, dWorkDay, "06", "06FU01", "", sPortCodes, "", "", "", 0);
        } catch (Exception e) {
            throw new YssException("获取期货应收应付数据出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
