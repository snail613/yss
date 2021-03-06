package com.yss.main.operdeal.report.netvalueviewpl;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.cusreport.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.report.*;
import com.yss.util.*;

public class FixGips
    extends BaseBuildCommonRep {
    public FixGips() {
    }

    CommonRepBean repBean = null;
    private java.util.Date startDate = null;
    private java.util.Date endDate = null;
    private String sPort = "";
    private String dataIndex = "";

    /**
     * buildReport
     *
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        sResult = buildResult(this.startDate, this.endDate, this.sPort);
        return sResult;
    }

    /**
     * initBuildReport
     *
     * @param bean BaseBean
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        repBean = (CommonRepBean) bean;
        this.parse(repBean.getRepCtlParam());
    }

    public void parse(String str) throws YssException {
        String[] sReq = str.split("\n");
        try {
            this.endDate = YssFun.toDate(sReq[0].split("\r")[1]);
            this.sPort = sReq[1].split("\r")[1];
            this.startDate = YssFun.toDate(sReq[2].split("\r")[1]);
            this.dataIndex = sReq[3].split("\r")[1];
        } catch (Exception e) {
            throw new YssException("解析参数出错", e);
        }
    }

    /**
     * saveReport
     *
     * @param sReport String
     * @return String
     */
    public String saveReport(String sReport) {
        return "";
    }

    protected String buildResult(java.util.Date startDate,
                                 java.util.Date endDate, String sPort) throws
        YssException {
        String month = "";
        StringBuffer buf = new StringBuffer();
        String strResult = "";
        StringBuffer strBuf = null;
        String portCode = "";
        String oneResult = "";
        double twoResult = 0.0;
        double threeResult = 0.0;
        String finalStr = "";
        ResultSet rs = null;
        String strSql = "";
        int startYear = 0;
        int endYear = 0;
        int numYear = 0;
        double inception = 0.0;
        String reportDate = "";
        String header = "";
        double twoFee = 0.0;
        try {
            strSql = " select FSETCODE from lsetlist " +
                " where fsetid=(select fassetcode from " +
                pub.yssGetTableName("tb_para_portfolio") +
                " where FPORTCODE=" + dbl.sqlString(sPort) + ")";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                portCode = rs.getString("FSETCODE");
            }
            inception = this.calInceptionData(portCode);
            twoFee = this.calFee(startDate, endDate);

            reportDate = "报告日期:" + YssFun.getYear(endDate) +
                "年" + YssFun.getMonth(endDate) + "月" +
                YssFun.getDay(endDate) + "日";
            buf.append(this.buildRowCompResult(reportDate)).append("\r\n");

            header = this.calFirstRow(sPort);
            buf.append(this.buildRowCompResult(header)).append("\r\n");

            oneResult = "过去3个月";
            twoResult = this.calTwoData(startDate, endDate, "3", portCode,
                                        inception, twoFee);
            threeResult = this.calThreeData(startDate, endDate, "3", portCode,
                                            inception, twoFee);
            strBuf = new StringBuffer();
            strBuf.append(oneResult).append(",");
            strBuf.append(YssFun.roundIt(twoResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(threeResult, 2) + "%").append(",");
            buf.append(this.buildRowCompResult(strBuf.toString())).append("\r\n");

            startYear = YssFun.getYear(startDate);
            endYear = YssFun.getYear(endDate);
            if ( (endYear - startYear) > 0) {
                for (int i = 1; i <= endYear - startYear; i++) {
                    oneResult = "过去" + i + "年";
                    twoResult = this.calTwoData(startDate, endDate, "", portCode,
                                                inception, twoFee);
                    threeResult = this.calThreeData(startDate, endDate, "", portCode,
                        inception, twoFee);
                    strBuf = new StringBuffer();
                    strBuf.append(oneResult).append(",");
                    strBuf.append(YssFun.roundIt(twoResult, 2) + "%").append(",");
                    strBuf.append(YssFun.roundIt(threeResult, 2) + "%").append(",");
                    buf.append(this.buildRowCompResult(strBuf.toString())).append(
                        "\r\n");
                }
            }
            finalStr = this.calFinalData(endDate, month, portCode);
            buf.append(this.buildRowCompResult(finalStr)).append("\r\n");
            if (buf.toString().length() > 2) {
                strResult = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }
            if (strResult.length() == 0) {
                repBean.setDataIsTrue(true);
            }
            return strResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public double calTwoData(java.util.Date startDate, java.util.Date endDate,
                             String month,
                             String portCode, double inception, double twoFee) throws
        YssException {
        StringBuffer strBuf = new StringBuffer();
        java.util.Date beginDate = null;
        String strSql = "";
        ResultSet beginRs = null;
        ResultSet endRs = null;
        double beginNetValue = 0.0;
        double endNetValue = 0.0;
        double sResult = 0.0;
        try {
            if (month.length() > 0) {
                beginDate = YssFun.addDay
                    ( (YssFun.addMonth(endDate, -Integer.parseInt(month))), -1);
            } else {
                beginDate = startDate;
            }
            strSql = " select * from " +
                pub.yssGetTableName("tb_rep_guessvalue") +
                " where FDATE=" + dbl.sqlDate(beginDate) +
                " and FPortCode=" + dbl.sqlString(portCode) +
                " and FAcctCode=" + dbl.sqlString("9600");
            beginRs = dbl.openResultSet(strSql);
            while (beginRs.next()) {
                beginNetValue = beginRs.getDouble("FStandardMoneyMarketValue");
            }
            if (beginNetValue == 0) {
                beginNetValue = inception;
            }
            beginNetValue = YssD.add(beginNetValue, twoFee);
            strSql = " select * from " +
                pub.yssGetTableName("tb_rep_guessvalue") +
                " where FDATE=" + dbl.sqlDate(endDate) +
                " and FPortCode=" + dbl.sqlString(portCode) +
                " and FAcctCode=" + dbl.sqlString("9600");
            endRs = dbl.openResultSet(strSql);
            while (endRs.next()) {
                endNetValue = endRs.getDouble("FStandardMoneyMarketValue");
            }
            endNetValue = YssD.add(endNetValue, twoFee);
            if (beginNetValue != 0) {
                sResult = YssD.sub(YssD.div(endNetValue, beginNetValue), 1);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(beginRs);
            dbl.closeResultSetFinal(endRs);
        }
    }

    public double calThreeData(java.util.Date startDate, java.util.Date endDate,
                               String month,
                               String portCode, double inception, double twoFee) throws
        YssException {
        StringBuffer strBuf = new StringBuffer();
        java.util.Date beginDate = null;
        String strSql = "";
        ResultSet beginRs = null;
        ResultSet endRs = null;
        double beginNetValue = 0.0;
        double endNetValue = 0.0;
        double sResult = 0.0;
        try {
            if (month.length() > 0) {
                beginDate = YssFun.addDay
                    ( (YssFun.addMonth(endDate, -Integer.parseInt(month))), -1);
            } else {
                beginDate = startDate;
            }

            strSql = " select FTopValue from " +
                pub.yssGetTableName("Tb_Data_Index") +
                " where FDate=" + dbl.sqlDate(beginDate) +
                " and FIndexCode=" + dbl.sqlString(this.dataIndex);
            beginRs = dbl.openResultSet(strSql);
            while (beginRs.next()) {
                beginNetValue = beginRs.getDouble("FTopValue");
            }
            if (beginNetValue == 0) {
                beginNetValue = inception;
            }
            beginNetValue = YssD.add(beginNetValue, twoFee);
            strSql = " select FTopValue from " +
                pub.yssGetTableName("Tb_Data_Index") +
                " where FDate=" + dbl.sqlDate(endDate) +
                " and FIndexCode=" + dbl.sqlString(this.dataIndex);
            endRs = dbl.openResultSet(strSql);
            while (endRs.next()) {
                endNetValue = endRs.getDouble("FTopValue");
            }
            endNetValue = YssD.add(endNetValue, twoFee);
            if (beginNetValue != 0) {
                sResult = YssD.sub(YssD.div(endNetValue, beginNetValue), 1);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(beginRs);
            dbl.closeResultSetFinal(endRs);
        }

    }

    public double calFourData(java.util.Date startDate, java.util.Date endDate,
                              String month,
                              String portCode, double inception) throws
        YssException {
        StringBuffer strBuf = new StringBuffer();
        java.util.Date beginDate = null;
        String strSql = "";
        ResultSet beginRs = null;
        ResultSet endRs = null;
        double beginNetValue = 0.0;
        double endNetValue = 0.0;
        double sResult = 0.0;
        try {
            if (month.length() > 0) {
                beginDate = YssFun.addDay
                    ( (YssFun.addMonth(endDate, -Integer.parseInt(month))), -1);
            } else {
                beginDate = startDate;
            }

            strSql = " select FTopValue from " + pub.yssGetTableName("Tb_Data_Index") +
                " where FDate=" + dbl.sqlDate(beginDate) +
                " and FIndexCode=" + dbl.sqlString(this.dataIndex);
            beginRs = dbl.openResultSet(strSql);
            while (beginRs.next()) {
                beginNetValue = beginRs.getDouble("exponent");
            }
            if (beginNetValue == 0) {
                beginNetValue = inception;
            }
            strSql = " select FTopValue from " + pub.yssGetTableName("Tb_Data_Index") +
                " where FDate=" + dbl.sqlDate(endDate) +
                " and FIndexCode=" + dbl.sqlString(this.dataIndex);
            endRs = dbl.openResultSet(strSql);
            while (endRs.next()) {
                endNetValue = endRs.getDouble("exponent");
            }
            if (beginNetValue != 0) {
                sResult = YssD.sub(YssD.div(endNetValue, beginNetValue), 1);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(beginRs);
            dbl.closeResultSetFinal(endRs);
        }
    }

    public double calFiveData(java.util.Date startDate, java.util.Date endDate,
                              String month,
                              String portCode, double inception) throws
        YssException {
        int days = 0;
        java.util.Date tmpDate = null;
        String strSql = "";
        ResultSet rs = null;
        ArrayList list = new ArrayList();
        double allResult = 0.0;
        double tmpResult = 0.0;
        double sResult = 0.0;
        Iterator it = null;
        try {

            if (month.length() > 0) {
                tmpDate = YssFun.addMonth(endDate, -Integer.parseInt(month));
                days = YssFun.dateDiff(tmpDate, endDate);
            } else {
                tmpDate = startDate;
                days = YssFun.dateDiff(startDate, endDate);
            }
            for (int i = 1; i <= days; i++) {
                tmpDate = YssFun.addDay(tmpDate, i);
                strSql = " select FTopValue from " + pub.yssGetTableName("Tb_Data_Index") +
                    " where FDate=" + dbl.sqlDate(tmpDate) +
                    " and FIndexCode=" + dbl.sqlString(this.dataIndex);
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    list.add(new Double(rs.getDouble("FTopValue")));
                    allResult = allResult +
                        rs.getDouble("FTopValue");
                }
                dbl.closeResultSetFinal(rs);
            }

            it = list.iterator();
            while (it.hasNext()) {
                tmpResult = ( (Double) it.next()).doubleValue();
                sResult = sResult + Math.sqrt(YssD.div( (YssD.mul(
                    YssD.sub(tmpResult, YssD.div(allResult, days)),
                    YssD.sub(tmpResult, YssD.div(allResult, days)))), days - 1));
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String calFinalData(java.util.Date endDate, String month,
                               String portCode) throws YssException {
        String strSql = "";
        StringBuffer strBuf = new StringBuffer();
        ResultSet rs = null;
        java.util.Date inceptionDate = null;
        double inception = 0.0;
        double endNetValue = 0.0;
        double oneResult = 0.0;
        double twoResult = 0.0;
        double threeResult = 0.0;
        double fourResult = 0.0;
        double fiveResult = 0.0;
        double sixResult = 0.0;
        double sevenResult = 0.0;
        double twoFee = 0.0;
        try {
            twoFee = this.calFee(startDate, endDate);
            strSql = " select FInceptionDate from " +
                pub.yssGetTableName("tb_para_portfolio") + //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
                " where FPortCode=" + dbl.sqlString(sPort);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                inceptionDate = rs.getDate("FInceptionDate");
            }
            dbl.closeResultSetFinal(rs);
            strSql = " select * from " + pub.yssGetTableName("tb_rep_guessvalue") +
                " where FDATE=" + dbl.sqlDate(inceptionDate) +
                " and FPortCode=" + dbl.sqlString(portCode) +
                " and FAcctCode=" + dbl.sqlString("9600");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                inception = rs.getDouble("FStandardMoneyMarketValue");
            }
            inception = YssD.add(inception, twoFee);
            dbl.closeResultSetFinal(rs);
            strSql = " select * from " +
                pub.yssGetTableName("tb_rep_guessvalue") +
                " where FDATE=" + dbl.sqlDate(endDate) +
                " and FPortCode=" + dbl.sqlString(portCode) +
                " and FAcctCode=" + dbl.sqlString("9600");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                endNetValue = rs.getDouble("FStandardMoneyMarketValue");
            }
            endNetValue = YssD.add(endNetValue, twoFee);
            if (endNetValue != 0) {
                oneResult = YssD.div(inception, endNetValue);
            }
            twoResult = this.calTwoData(startDate, endDate, month, portCode,
                                        inception, twoFee);
            strBuf.append("自基金成立起至今").append(",");
            strBuf.append(YssFun.roundIt(oneResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(twoResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(threeResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(fourResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(fiveResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(sixResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(sevenResult, 2) + "%");
            return strBuf.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String calFirstRow(String portCode) throws YssException {
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        String shortName = "";
        try {
            buf.append("扣除费用前以人民币为单位的收益").append(",");
            strSql = " select FPortShortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FPortCode=" + dbl.sqlString(portCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                shortName = rs.getString("FPortShortName");
                if (shortName == null) {
                    shortName = "";
                }
                buf.append(shortName).append(",");
            }
            buf.append("MSCI World Index");
            return buf.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public double calInceptionData(String portCode) throws YssException {
        double inception = 0.0;
        String strSql = "";
        java.util.Date inceptionDate = null;
        ResultSet rs = null;
        try {
            strSql = " select FInceptionDate from " +
                pub.yssGetTableName("tb_para_portfolio") + //修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
                " where FPortCode=" + dbl.sqlString(sPort);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                inceptionDate = rs.getDate("FInceptionDate");
            }
            dbl.closeResultSetFinal(rs);
            strSql = " select * from " + pub.yssGetTableName("tb_rep_guessvalue") +
                " where FDATE=" + dbl.sqlDate(inceptionDate) +
                " and FPortCode=" + dbl.sqlString(portCode) +
                " and FAcctCode=" + dbl.sqlString("9600");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                inception = rs.getDouble("FStandardMoneyMarketValue");
            }
            return inception;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public double calFee(java.util.Date startDate, java.util.Date endDate) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        double sResult = 0.0;
        try {
            strSql = " select FKMH,sum(FBBAL)as FBAL from a2007001fcwvch a join (" +
                " select * from a2007001laccount where FAcctDetail=1" +
                " )b on b.FACCTCODE=a.FKMH WHERE substr(FKMH,0,4)='2206'" +
                " and 	Fdate between " + dbl.sqlDate(startDate) + " and " +
                dbl.sqlDate(endDate) +
                " and FJD='D' group by FKMH";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getDouble("FBAL");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    protected String buildRowCompResult(String str) throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";
        RepTabCellBean rtc = null;
        String[] sArry = null;
        try {
            sArry = str.split(",");
            hmCellStyle = getCellStyles("DsXXPLGIPS");
            //     strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
            //           " where FRepDsCode = " + dbl.sqlString("DsXXPLGIPS") +
            //           " and FCheckState = 1 order by FOrderIndex";
            //     rs = dbl.openResultSet(strSql);
            for (int i = 0; i < sArry.length; i++) {
                sKey = "DsXXPLGIPS" + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append(
                    "\t");
            }
            if (buf.toString().trim().length() > 1) {
                strReturn = buf.toString().substring(0,
                    buf.toString().length() - 1);
            }
            return strReturn + "\t\t";
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

}
