package com.yss.main.operdeal.report.repexp;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import java.sql.*;
import com.yss.base.BaseAPOperValue;

//获取明细帐中的结金额
public class ExpSettlorAccBal
    extends BaseAPOperValue implements IYssConvert {
    private String num;
    private String portCode;

    public String getPortCode() {
        return portCode;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public String getNum() {
        return num;
    }

    public ExpSettlorAccBal() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        dResult = this.getSettlorAccBal();
        return dResult;
    }

    public double getSettlorAccBal() throws YssException {
        double dResult = 0;
        String strSql = "";
        ResultSet rs = null;
        double dInitBal = 0;

        try {
            strSql = "select FINCEPTIONASSET from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dInitBal = rs.getDouble("FINCEPTIONASSET");
            }
            dbl.closeResultSetFinal(rs);
            if (num != null && num.length() > 0) {
                strSql = "select sum(FMoney*FInOut) as FMoney from " +
                    pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " a join (select * from " +
                    pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FTSFTypeCode = '04'" +
                    " and FCheckState = 1 " +
                    ")b on a.Fnum = b.FNum where a.FCheckState = 1 and FPortCode = " +
                    dbl.sqlString(portCode) +
                    " and a.FNum || FSubNum <= " + dbl.sqlString(num);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    dResult = rs.getDouble("FMoney");
                }
            }
            dResult = YssD.add(dInitBal, dResult);
            return dResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
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
        this.num = sRowStrAry[0];
        this.portCode = sRowStrAry[1];
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
