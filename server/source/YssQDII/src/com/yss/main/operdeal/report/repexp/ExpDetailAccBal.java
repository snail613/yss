package com.yss.main.operdeal.report.repexp;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import java.sql.*;
import com.yss.base.BaseAPOperValue;

//获取明细帐中的结金额
public class ExpDetailAccBal
    extends BaseAPOperValue implements IYssConvert {
    private String fullNum; //FNum + FSubNum
    private java.util.Date beginDate;
    private java.util.Date dDate;
    private String cashAccCode;
    private String portCode;

    public ExpDetailAccBal() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        dResult = this.getDetailAccBal();
        return dResult;
    }

    public double getDetailAccBal() throws YssException {
        double dResult = 0;
        String strSql = "";
        ResultSet rs = null;
        double dEveBal = 0;
        String sNum = "";
        String sSubNum = "";
        boolean bFlag = false;
        try {
            strSql = "select sum(FAccBalance) as FAccBalance from (select FAccBalance,FCashAccCode from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                " where FCheckState = 1 and FPortCode = " +
                dbl.sqlString(portCode) +
                " and " + operSql.sqlStoragEve(beginDate) +
                ")a join (select FCashAccCode from " +
                pub.yssGetTableName("tb_para_cashaccount") +
                " where FCashAccCode = " + dbl.sqlString(this.cashAccCode) +
                " and FCheckState = 1) b on a.FCashAccCode = b.FCashAccCode";

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dEveBal = rs.getDouble("FAccBalance");
            }
            dbl.closeResultSetFinal(rs);
            dResult = dEveBal;
//         strSql = "select a.FNum,FSubNum,FMoney*FInOut as FMoney from " +
//               pub.yssGetTableName("Tb_Cash_Transfer") +
//               " a join (select b1.* from " +
//               pub.yssGetTableName("Tb_Cash_SubTransfer") +
//               " b1  join (select * from " +
//               pub.yssGetTableName("Tb_Para_Cashaccount") +
//               " where FCheckState = 1 and FCashAccCode = " +
//               dbl.sqlString(this.cashAccCode) +
//               ") b2 on b1.Fcashacccode = b2.FCashAccCode" +
//               " where b1.FCheckState = 1 and b1.FPortCode = " +
//               dbl.sqlString(portCode) +
//               ")b on a.Fnum = b.FNum where a.FCheckState = 1 and FTransferDate = " +
//               dbl.sqlDate(dDate) + " order by FTransferDate,FInOut";
            strSql = "select x. *," + dbl.sqlIsNull("y.FInMoney", "0") + " as FInMoney," +
                dbl.sqlIsNull("z.FOutMoney", "0") + " as FOutMoney " +
                " from (select a.FNum,b.FSubNum,b.FInOut,a.FNum || FSubNum as FFullNum,FTransferDate,b.FCashAccCode from " +
                pub.yssGetTableName("Tb_Cash_Transfer") + " a join (select b1. * from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") + " b1 join (select * from " +
                pub.yssGetTableName("Tb_Para_Cashaccount") + " where FCheckState = 1 and " +
                " FCashAccCode = " + dbl.sqlString(this.cashAccCode) + ") b2 on b1.Fcashacccode = b2.FCashAccCode" +
                " where b1.FCheckState = 1 and b1.FPortCode = " +
                dbl.sqlString(portCode) + ") b on a.Fnum = b.FNum" +
                " where (FTransferDate between " + dbl.sqlDate(beginDate) + " and "
                + dbl.sqlDate(dDate) + ") and a.FCheckState = 1) x left join" +
                " (select FNum,FSubNum,FMoney*FInOut as FInMoney from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FInOut = -1) y on x.FNum = y.FNum and x.FSubNum = y.FSubNum " +
                " left join (select FNum,FSubNum,FMoney*FInOut as FOutMoney from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FInOut = 1) z on x.FNum = z.FNum and x.FSubNum = z.FSubNum " +
                " left join (select * from " + pub.yssGetTableName("Tb_Para_Cashaccount") +
                " where FCheckState = 1) c on x.FCashAccCode = c.FCashAccCode " +
                "  order by FTransferDate, FInOut desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (bFlag) {
                    break;
                }
                dResult = YssD.add(dResult, rs.getDouble("FInMoney"));
                dResult = YssD.add(dResult, rs.getDouble("FOutMoney"));
//            dResult = YssD.sub(dResult, rs.getDouble("FOutMoney"));
                if ( (rs.getString("FNum") + rs.getString("FSubNum")).equalsIgnoreCase(this.fullNum)) {
                    bFlag = true;
                }
            }
//         if (rs.next()) {
//            dResult = rs.getDouble("FMoney");
//         }
//         dResult = YssD.add(dEveBal, dResult);
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

//
    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRowStrAry = sRowStr.split("\t");
        this.fullNum = sRowStrAry[0];
        this.beginDate = YssFun.toDate(YssFun.formatDate(sRowStrAry[1],
            "yyyy-MM-dd"));
        this.dDate = YssFun.toDate(YssFun.formatDate(sRowStrAry[2], "yyyy-MM-dd"));
        this.cashAccCode = sRowStrAry[3];
        this.portCode = sRowStrAry[4];
        this.dDate = YssFun.toDate(YssFun.formatDate(sRowStrAry[5], "yyyy-MM-dd"));
//      this.num = sRowStrAry[0];
//      this.subNum = sRowStrAry[1];
//      this.dDate = YssFun.toDate(YssFun.formatDate(sRowStrAry[2],"yyyy-MM-dd"));
//      this.portCode = sRowStrAry[3];
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
