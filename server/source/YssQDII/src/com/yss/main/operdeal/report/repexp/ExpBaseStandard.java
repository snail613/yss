package com.yss.main.operdeal.report.repexp;

import com.yss.main.dao.*;
import com.yss.dsub.*;
import com.yss.util.*;
import java.util.ArrayList;
import java.sql.*;
import com.yss.base.BaseAPOperValue;

//计算基准收益率标准差
/* 1.先规定一段期间作为基准，例如：1周，1个月，1年...
 2.求出在一段时间内，各个期间段的基准收益率(r1,r2,r3...r12)
 3.求出在这段内的平均收益率(rv=(r1+r2+r3+...r12)/12)
 4.证券标准差=开根号(((r1-rv)^2+(r2-rv)^2+(r3-rv)^2+...(r12-rv)^2)/T-1)
 */
public class ExpBaseStandard
    extends BaseAPOperValue implements IYssConvert {

    private java.util.Date startDate;
    private java.util.Date endDate;
    private String mark;
    private String portCode;
    private String costType; //成本类型

    public ExpBaseStandard() {
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
                    dTmp = this.getBaseStandard(yieldStandard, i);
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
            throw new YssException("计算基准收益率标准差出错", e);
        }
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

    protected double getBaseStandard(ArrayList yieldStandard, int i) throws
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

            strSql = "select (case when (" + dbl.sqlIsNull("FSecStorageCost", "0") +
                " + " + dbl.sqlIsNull("FCashStorageCost", "0") +
                ") * FBeginIndex + " + dbl.sqlIsNull("FIndexInOutDif", "0") +
                " = 0 then 0  else ((( " +
                dbl.sqlIsNull("FSecStorageCost", "0") + " + " +
                dbl.sqlIsNull("FCashStorageCost", "0") + " + " +
                dbl.sqlIsNull("FTotalInOut", "0") + ") * FEndIndex -  (" +
                dbl.sqlIsNull("FSecStorageCost", "0") + " + " +
                dbl.sqlIsNull("FCashStorageCost", "0") +
                ") * FBeginIndex - " + dbl.sqlIsNull("FIndexInOut", "0") +
                ") / " +
                " ((" + dbl.sqlIsNull("FSecStorageCost", "0") + " + " +
                dbl.sqlIsNull("FCashStorageCost", "0") + ") * FBeginIndex + " +
                dbl.sqlIsNull("FIndexInOutDif", "0") + ") * 100) end) " +
                " as FBaseYield from " +
                //-----------------------
                " (select (case when " +
                dbl.sqlString(YssFun.formatDate(dStart, "MM-dd")) +
                " <> '01-01' then FSecStorageCost1 else FSecStorageCost2 end) as FSecStorageCost," +
                "(case when " + dbl.sqlString(YssFun.formatDate(dStart, "MM-dd")) +
                " <> '01-01' then FCashStorageCost1 else FCashStorageCost2 end) as FCashStorageCost," +
                "FTotalInOut, FBeginIndex, FEndIndex, FIndexInOut, FIndexInOutDif from " +
                //-----------------------
                "(select sum(" + this.costType + ") as FSecStorageCost1,1   as FRela from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " c1 join (select FSecurityCode from " +
                pub.yssGetTableName("Tb_Para_Security") + " where FCatCode = 'EQ' and FCheckState = 1) c2 on c1.FSecurityCode = c2.FSecurityCode " +
                " where FCheckState = 1 and FStorageDate = " +
                dbl.sqlDate(dStart) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.formatDate(dStart, "yyyy") + "00") +
                " and FPortCode = " +
                dbl.sqlString(this.portCode) + " ) a " +
                //-----------------------
                " left join (select sum(" + this.costType + ") as FSecStorageCost2, 1 as FRela from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " c1 join (select FSecurityCode from " +
                pub.yssGetTableName("Tb_Para_Security") + " where FCatCode = 'EQ' and FCheckState = 1) c2 on c1.FSecurityCode = c2.FSecurityCode " +
                " where FCheckState = 1 and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dStart, "yyyy") + "00") +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " ) cs on a.FRela = cs.FRela " +
                //-----------------------
                " left join (select sum(FMoney * FBaseCuryRate *  FInOut) as FTotalInOut,1 as FRela from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " g1 join (select * from (select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") + " where FCheckState = 1 and FTsfTypeCode = '01' and FSubTsfTypeCode ='0001' and FTransferDate between " +
                dbl.sqlDate(dStart) + " and " + dbl.sqlDate(dEnd) +
                " )) g2 on g1.FNum = g2.FNum where FCheckState = 1 and " +
                " FAnalysisCode2 = 'EQ' and FPortCode = " +
                dbl.sqlString(this.portCode) +
                " ) g on a.FRela = g.FRela " +
                //------------------------
                " left join (select sum(FBaseCuryBal) as FCashStorageCost1,1   as FRela from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                " where FCheckState = 1 and FStorageDate = " +
                dbl.sqlDate(dStart) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.formatDate(dStart, "yyyy") + "00") +
                " and FAnalysisCode2 = 'EQ' and FPortCode = " +
                dbl.sqlString(this.portCode) + " ) h on a.FRela = h.FRela " +
                //-----------------------
                " left join (select sum(FBaseCuryBal) as FCashStorageCost2,1   as FRela from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                " where FCheckState = 1 and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dStart, "yyyy") + "00") +
                " and FAnalysisCode2 = 'EQ' and FPortCode = " +
                dbl.sqlString(this.portCode) +
                " ) hs on a.FRela = hs.FRela " +
                //-----------------------
                " left join (select FClosedValue as FBeginIndex,1 as FRela from " +
                pub.yssGetTableName("Tb_Data_Index") +
                " a join (select FIndexCode from " +
                pub.yssGetTableName("Tb_Para_Index") + " where FCheckState = 1 and FExchangeCode = 'HK') b on a.FIndexCode = b.FIndexCode " +
                " where FDate = " + dbl.sqlDate(dStart) +
                " and  FCheckState = 1) e on a.FRela = e.FRela " +
                //---------------------------
                " left join (select FClosedValue as FEndIndex,1 as FRela from " +
                pub.yssGetTableName("Tb_Data_Index") +
                " a join (select FIndexCode from " +
                pub.yssGetTableName("Tb_Para_Index") +
                " where FCheckState = 1 and FExchangeCode = 'HK') b on a.FIndexCode = b.FIndexCode " +
                " where FDate = " + dbl.sqlDate(dStart) +
                " and  FCheckState = 1) f on a.FRela = f.FRela " +
                //--------------------------
                " left join (select sum(FMoney * FBaseCuryRate * FInOut * FClosedValue * (FDateNum1 -FDateNum2) /FDateNum1) as FIndexInOutDif," +
                " Nvl(sum(FMoney *FBaseCuryRate *FInOut * FClosedValue), 0) as FIndexInOut,1 as FRela from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " i1 join (select FNum,FTransferDate,(" +
                dbl.sqlDate(dEnd) + " - " + dbl.sqlDate(dStart) +
                " + 1 ) as FDateNum1, ( FTransferDate - " +
                dbl.sqlDate(dStart) + " + 1 ) as FDateNum2 from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FCheckState = 1 and FTsfTypeCode = '01' and  FSubTsfTypeCode = '0001' and FTransferDate between " +
                dbl.sqlDate(dStart) + " and " + dbl.sqlDate(dEnd) +
                ") i2 on i1.FNum = i2.FNum " +
                " left join (select FClosedValue,FDate from " +
                pub.yssGetTableName("Tb_Data_Index") +
                " a join (select FIndexCode from " +
                pub.yssGetTableName("Tb_Para_Index") + " where FCheckState = 1 and FExchangeCode = 'HK') b on a.FIndexCode = b.FIndexCode " +
                " where FCheckState = 1) i3 on i2.FTransferDate = i3.FDate " +
                " where FCheckState = 1 and FAnalysisCode2 = 'EQ' and FPortCode = " +
                dbl.sqlString(this.portCode) +
                " ) i on a.FRela = i.FRela)";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dResult = rs.getDouble("FBaseYield");
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("计算基准收益率出错", e);
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
