package com.yss.main.operdeal.report.repexp;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import java.sql.*;
import com.yss.base.BaseAPOperValue;

//根据比率公式代码计算息率
public class ExpIntRate
    extends BaseAPOperValue implements IYssConvert {
    private java.util.Date dDate;
    private String formulaCode;
    private double money;
    public ExpIntRate() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        dResult = getInterestRate(formulaCode, money, dDate);
        return dResult;
    }

    /**
     * getPerformulaRela
     *
     * @param dSumMoney double
     * @return PerformulaRelaBean
     */
    public double getInterestRate(String sFormulaCode,
                                  double dSumMoney,
                                  java.util.Date dDate) throws
        YssException {
        ResultSet rs = null;
        double dResult = 0;
        String strSql = "";
        try {
            strSql = "select y.* from " +
                " (select FFormulaCode,FCheckState as FChkState,max(FRangeDate) as FRangeDate, min(FRangeMoney) as FRangeMoney from " +
                pub.yssGetTableName("Tb_Para_Performula_Rela") +
                " where FRangeDate <= " +
                dbl.sqlDate(dDate) + " and FRangeMoney >= " +
                String.valueOf(dSumMoney) +
                " and FCheckState = 1 group by FFormulaCode,FCheckState) x join" +
                " (select * from " +
                pub.yssGetTableName("Tb_Para_Performula_Rela") +
                " where FFormulaCode = " + dbl.sqlString(sFormulaCode) +
                ") y" +
                " on x.FFormulaCode = y.FFormulaCode and x.FRangeDate = y.FRangeDate" +
                " and x.FRangeMoney = y.FRangeMoney";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dResult = rs.getDouble("FPerValue") * 100;
            } else {
                //如果金额超出了最大的金额范围，那么就取最大金额范围的那条记录
                dbl.closeResultSetFinal(rs);
                strSql = "select y.* from " +
                    " (select FFormulaCode,FCheckState as FChkState,max(FRangeDate) as FRangeDate, max(FRangeMoney) as FRangeMoney from " +
                    pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " where FRangeDate <= " +
                    dbl.sqlDate(dDate) + " and FRangeMoney <= " +
                    String.valueOf(dSumMoney) +
                    " and FCheckState = 1 group by FFormulaCode,FCheckState) x join" +
                    " (select * from " +
                    pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " where FFormulaCode = " + dbl.sqlString(sFormulaCode) +
                    ") y" +
                    " on x.FFormulaCode = y.FFormulaCode and x.FRangeDate = y.FRangeDate" +
                    " and x.FRangeMoney = y.FRangeMoney";
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    dResult = rs.getDouble("FPerValue") * 100;
                }
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("获取息率出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * init
     *
     * @param bean BaseBean
     */
    public void init(Object bean) {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
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
        String[] sRowStrAry = sRowStr.split("\t");
        this.formulaCode = sRowStrAry[0];
        this.money = YssFun.toDouble(sRowStrAry[1]);
        this.dDate = YssFun.toDate(YssFun.formatDate(sRowStrAry[2], "yyyy-MM-dd"));
    }

    /**
     * invokeOperMothed
     *
     * @return Object
     */
    public Object invokeOperMothed() {
        return "";
    }
}
