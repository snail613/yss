package com.yss.main.operdeal.report.netvalueviewpl;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.cusreport.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.report.*;
import com.yss.util.*;
import com.yss.vsub.*;
import com.yss.main.operdeal.BaseOperDeal;

public class FixNetValueResultB
    extends BaseBuildCommonRep {
    public FixNetValueResultB() {
    }

    private CommonRepBean repBean;
    private java.util.Date startDate = null; //期初日期
    private java.util.Date endDate = null; //期末日期
    private String sPort = ""; //组合代码
    private String dataIndex = ""; //指数代码
    private String holidayCode = ""; //节假日代码
    private FixPub fixPub = null;
    double inception = 0.0;

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
        fixPub = new FixPub();
        fixPub.setYssPub(pub);
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
            this.holidayCode = sReq[4].split("\r")[1];

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

    protected String buildResult(java.util.Date startDate, java.util.Date endDate, String sPort) throws
        YssException {

        String strResult = "";
        String setCode = "";
        String firstRow = "";
        String oneResult = "";
        double twoResult = 0.0;
        double threeResult = 0.0;
        StringBuffer buf = new StringBuffer();
        StringBuffer strBuf = null;
        ResultSet rs = null;
        int days = 0;
        java.util.Date tmpStartDate = null;
        int day = 0;
        java.util.Date inceptionDate = null;
        int yearInception = 0;
        int yearEnd = 0;
        double zzl = 1; //增长率
        double inception2 = 0.0;
        double dEnd = 0.0;
        double dInception = 0.0;
        try {
            setCode = fixPub.getSetCode(sPort); //获取套帐代码
            inceptionDate = fixPub.getInceptionDate(sPort); //获取基金成立日期
            /**shashijie 2012-7-2 STORY 2475 */
            inception2 = fixPub.calInceptionMoney(inceptionDate, setCode); //获取基金成立日单位基金净值
            /**end*/
            firstRow = this.calFirstRow(sPort);
            buf.append(fixPub.buildRowCompResult(firstRow, "Ds0000202")).append("\r\n");
            setCode = fixPub.getSetCode(sPort);

            yearInception = YssFun.getYear(inceptionDate);
            yearEnd = YssFun.getYear(endDate);

            if (yearInception < yearEnd) {
                dEnd = fixPub.getNetValue(endDate, setCode);
                dInception = fixPub.getNetValue(inceptionDate, setCode);
                if (dInception != 0) {
                    zzl = YssD.mul(zzl, (YssD.div(dInception, dEnd)) + 1);
                }
            }
            days = YssFun.dateDiff(startDate, endDate);
            if (days == 0) {
                days = 1;
            }
            for (int i = 0; i <= days; i++) {
                if (days == 1) {
                    tmpStartDate = startDate;
                } else {
                    tmpStartDate = YssFun.addDay(startDate, i);
                }
                if (!this.isHoliday(tmpStartDate)) { //判断如果是节假日的话就不显示
                    day = YssFun.getDay(tmpStartDate);
                    strBuf = new StringBuffer();
                    oneResult = this.calDate(tmpStartDate) + "-" + day;
                    twoResult = 1;
                    twoResult = YssD.mul(twoResult,
                                         this.calTwoResult(tmpStartDate, setCode,
                        day) + 1) - 1;
                    threeResult = YssD.mul(twoResult,
                                           this.calThreeResult(tmpStartDate, setCode,
                        day) + 1) - 1;
                    strBuf.append(oneResult).append(",");
                    strBuf.append(YssFun.roundIt(twoResult, 2) + "%").append(",");
                    strBuf.append(YssFun.roundIt(threeResult, 2) + "%").append(",");
                    strBuf.append(" ").append(",");
                    strBuf.append(" ").append(",");
                    strBuf.append(" ").append(",");
                    strBuf.append(" ").append(",");
                    strBuf.append(" ");
                    buf.append(fixPub.buildRowCompResult(strBuf.toString(),
                        "Ds0000202")).append("\r\n");
                }
            }
            if (buf.toString().length() > 2) {
                strResult = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }
            return strResult;
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
        String portName = "";
        try {
            buf.append("时间").append(",");
            strSql = " select FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FPortCode=" + dbl.sqlString(portCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                portName = rs.getString("FPortName");
                if (portName == null) {
                    portName = "";
                }
                buf.append(portName).append(",");
            }
            buf.append("业绩比较基准收益率");
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("生产报表数据报错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String calDate(java.util.Date endDate) throws YssException {
        java.util.Date sResult = null;
        try {
            sResult = endDate;
            return YssFun.getYear(sResult) + "-" + YssFun.getMonth(sResult);
        } catch (Exception e) {
            throw new YssException("计算日期报错!");
        }
    }

    public double calTwoResult(java.util.Date tmpDate, String setCode, int day) throws
        YssException {
        int year = 0;
        int month = 0;
        double beginNetValue = 0.0;
        double endNetValue = 0.0;
        double jzzzl = 0.0; //累计净值增长率
        try {
            beginNetValue = fixPub.getNetValue(tmpDate, setCode);
            endNetValue = fixPub.getNetValue(this.endDate, setCode);
            if (beginNetValue != 0) {
                jzzzl = YssD.div(endNetValue, beginNetValue);
            }
            return jzzzl;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public double calThreeResult(java.util.Date tmpDate, String setCode,
                                 int day) throws YssException {

        int year = 0;
        int month = 0;
        double beginNetValue = 0.0;
        double endNetValue = 0.0;
        double jzzzl = 0.0; //累计净值增长率
        try {
            beginNetValue = fixPub.getIndexDate(tmpDate, this.dataIndex);
            endNetValue = fixPub.getIndexDate(this.endDate, this.dataIndex);
            if (beginNetValue != 0) {
                jzzzl = YssD.div(endNetValue, beginNetValue);
            }
            return jzzzl;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public boolean isHoliday(java.util.Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        boolean isHoliday = true;
        try {
            strSql =
                "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = " +
                dbl.sqlString(this.holidayCode) + " and FDate=" + dbl.sqlDate(dDate);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                isHoliday = true;
            } else {
                isHoliday = false;
            }
            return isHoliday;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
