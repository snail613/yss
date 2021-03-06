package com.yss.main.operdeal.report.repexp;

import com.yss.main.dao.*;
import com.yss.dsub.*;
import com.yss.util.*;
import java.util.ArrayList;
import java.sql.*;
import com.yss.base.BaseAPOperValue;

//计算投资收益率标准差
/*1.先规定一段期间作为基准，例如：1周，1个月，1年...
 2.求出在一段时间内，各个期间段的总体股票投资收益率(r1,r2,r3...r12)
 3.求出在这段内的平均收益率(rv=(r1+r2+r3+...r12)/12)
 4.证券标准差=开根号(((r1-rv)^2+(r2-rv)^2+(r3-rv)^2+...(r12-rv)^2)/T-1)*/

public class ExpYieldStandard
    extends BaseAPOperValue implements IYssConvert {

    private java.util.Date startDate;
    private java.util.Date endDate;
    private String mark;
    private String portCode;
    private String costType; //成本类型

    public ExpYieldStandard() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        double dTmp;
        double aveYield = 0; //期间段内平均收益率((r1+r2+r3+...r12)/12)
        //ArrayList yield = new ArrayList();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        ArrayList yieldStandard = this.dateSection();
        double[] dYield = new double[yieldStandard.size()];

        try {
            if (yieldStandard.size() > 0) {
                for (int i = 0; i < yieldStandard.size(); i++) {
                    dTmp = this.getYieldStandard(yieldStandard, i);
                    dYield[i] = dTmp;
                    aveYield += dTmp;
                }
                aveYield = YssD.div(aveYield, yieldStandard.size());
                for (int i = 0; i < yieldStandard.size(); i++) {
                    dResult += YssD.sub(dYield[i], aveYield) *
                        YssD.sub(dYield[i], aveYield);
                }
                if (yieldStandard.size() == 1) {
                    dResult = 0;
                } else {
                    dResult = YssD.div(dResult, (yieldStandard.size() - 1));
                    dResult = Math.sqrt(dResult);
                }
            }

            return dResult;
        } catch (Exception e) {
            throw new YssException("计算投资收益率标准差出错", e);
        }

    }

    protected double getYieldStandard(ArrayList yieldStandard, int i) throws
        YssException, SQLException {
        double dResult = 0;
        java.util.Date dStart;
        java.util.Date dEnd;
        String strSql = "";
        ResultSet rs = null;
        if (i != yieldStandard.size() - 1) {
            dStart = (java.util.Date) yieldStandard.get(i);
            dEnd = (java.util.Date) yieldStandard.get(i + 1);
            dEnd = YssFun.addDay(dEnd, -1);
        } else {
            dStart = (java.util.Date) yieldStandard.get(i);
            dEnd = this.endDate;
        }
        try {
            strSql = "select (case when FGP = 0 then 0 else (" +
                dbl.sqlIsNull("FSecStorageCostEnd", "0") + " + " +
                dbl.sqlIsNull("FBalMVEnd", "0") + " - (" +
                dbl.sqlIsNull("FSecStorageCost", "0") + " + " +
                dbl.sqlIsNull("FBalMV", "0") + ") + " +
                dbl.sqlIsNull("FTotalInOut", "0") +
                ") / FGP * 100 end) as FYield" +
                " from (select FGP,(case when " +
                dbl.sqlString(YssFun.formatDate(dStart, "MM-dd")) +
                " <> '01-01' then FSecStorageCost1" +
                " else FSecStorageCost2 end) as FSecStorageCost," +
                " FSecStorageCostEnd, FBalMV, FBalMVEnd, FTotalInOut from" +
                " (select sum(" + this.costType + ") as FSecStorageCost1,'1' as FRela from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " c1 join (select FSecurityCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCatCode = 'EQ' and FCheckState = 1) c2 " +
                "on c1.FSecurityCode = c2.FSecurityCode  where FCheckState = 1" +
                " and FStorageDate = " + dbl.sqlDate(dStart) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.formatDate(dStart, "yyyy") + "00") +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " group by FPortCode ) a " +
                //-----------------------------
                " left join (select sum(" + this.costType + ") as FSecStorageCost2,'1' as FRela from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " c1 join (select FSecurityCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCatCode = 'EQ' and FCheckState = 1) c2 on c1.FSecurityCode = c2.FSecurityCode" +
                " where FCheckState = 1 and  FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dStart, "yyyy") + "00") +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " group by FPortCode ) cs on a.FRela = cs.FRela" +
                //--------------------------------
                " left join (select sum(FMoney *FBaseCuryRate *FInOut) as FTotalInOut,'1' as FRela from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " g1 join (select * from (select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FCheckState = 1 and FTsfTypeCode = '01' and FSubTsfTypeCode = '0001' and FTransferDate between " +
                dbl.sqlDate(dStart) + " and " + dbl.sqlDate(dEnd) +
                " )) g2 on g1.FNum = g2.FNum where FCheckState = 1 and FAnalysisCode2 = 'EQ'" +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " group by FPortCode) g on a.FRela = g.FRela " +
                //--------------------------
                " left join (select sum(FMoney *FBaseCuryRate * FInOut *(FDateNum1 -FDateNum2) /FDateNum1) as FGP,'1' as FRela from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " i1 join (select FNum, FTransferDate,(" +
                dbl.sqlDate(dEnd) + " - " + dbl.sqlDate(dStart) + " + 1 )" +
                " as FDateNum1,( FTransferDate - " +
                dbl.sqlDate(dStart) + " + 1 ) as FDateNum2 from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FCheckState = 1 and FTsfTypeCode = '01' and  FSubTsfTypeCode = '0001' and FTransferDate between " +
                dbl.sqlDate(dStart) + " and " + dbl.sqlDate(dEnd) +
                ") i2 on i1.FNum = i2.FNum where FCheckState = 1 and FAnalysisCode2 = 'EQ' and FPortCode = " +
                dbl.sqlString(this.portCode) +
                " group by FPortCode ) i on a.FRela = i.FRela" +
                //--------------------------
                " left join (select sum(" + this.costType + ") as FSecStorageCostEnd,'1' as FRela from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " c1 join (select FSecurityCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCatCode = 'EQ' and FCheckState = 1) c2 " +
                "on c1.FSecurityCode = c2.FSecurityCode  where FCheckState = 1" +
                " and FStorageDate = " + dbl.sqlDate(dEnd) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.formatDate(dEnd, "yyyy") + "00") +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " group by FPortCode) j on a.FRela = j.FRela" +
                //------------------------
                " left join (select sum(FBaseCuryBal) as FBalMV,'1' as FRela from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " c1 join (select FSecurityCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCatCode = 'EQ' and FCheckState = 1) c2 on c1.FSecurityCode = c2.FSecurityCode " +
                "  where FCheckState = 1  and FStorageDate = " +
                dbl.sqlDate(dStart) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.formatDate(dStart, "yyyy") + "00") +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FTsfTypeCode = '09' and FSubTsfTypeCode = '09EQ' " +
                " group by FPortCode) k on a.FRela = k.FRela" +
                //------------------------
                " left join (select sum(FBaseCuryBal) as FBalMVEnd,'1' as FRela from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " c1 join (select FSecurityCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCatCode = 'EQ' and FCheckState = 1) c2 on c1.FSecurityCode = c2.FSecurityCode " +
                "  where FCheckState = 1  and FStorageDate = " +
                dbl.sqlDate(dEnd) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.formatDate(dStart, "yyyy") + "00") +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and FTsfTypeCode = '09' and FSubTsfTypeCode = '09EQ' " +
                " group by FPortCode) l on a.FRela = l.FRela)";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dResult = rs.getDouble("FYield");
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("计算投资收益率出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * init
     *
     * @param bean Object
     */
    public void init(Object bean) {
    }

    /**
     * invokeOperMothed
     *
     * @return Object
     */
    public Object invokeOperMothed() {
        return "";
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

    protected ArrayList dateSection() throws YssException {
        ArrayList alResult = new ArrayList();
        int i = 0;
        java.util.Date tmpDate;
        try {
            if (this.mark.equalsIgnoreCase("1")) {
                tmpDate = YssFun.toDate(YssFun.formatDate(this.startDate, "yyyy-MM") +
                                        "-01");
                i = YssFun.getMonth(this.endDate) - YssFun.getMonth(this.startDate);
                for (int j = 0; j <= i; j++) {
                    if (j == 0) {
                        alResult.add(this.startDate);
                    } else {
                        tmpDate = YssFun.addMonth(tmpDate, 1);
                        alResult.add(tmpDate);
                    }
                }
            } else if (this.mark.equalsIgnoreCase("0")) {
                java.util.Date date1 = this.endDate;
                java.util.Date date2 = this.startDate;
                int days = YssFun.dateDiff(date2, date1);
                int dayOfWeek = 0;
                alResult.add(this.startDate);
                date2 = YssFun.addDay(date2, 1);
                for (int j = 0; j < days; j++) {
                    dayOfWeek = YssFun.getWeekDay(date2);
                    if (dayOfWeek == 1) {
                        alResult.add(date2);
                    }
                    date2 = YssFun.addDay(date2, 1);
                }
            }
            return alResult;
        } catch (Exception e) {
            throw new YssException("返回区间段数组出错", e);
        }

    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] sRowStrAry = sRowStr.split("\t");
        this.startDate = YssFun.toDate(YssFun.formatDate(sRowStrAry[0],
            "yyyy-MM-dd"));
        this.endDate = YssFun.toDate(YssFun.formatDate(sRowStrAry[1],
            "yyyy-MM-dd"));
        this.mark = sRowStrAry[2];
        this.portCode = sRowStrAry[3];
        this.costType = sRowStrAry[4];
    }

}
