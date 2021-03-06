package com.yss.main.operdeal.report.netvalueviewpl;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.report.*;
import com.yss.util.*;

public class FixNetValueResultC
    extends BaseBuildCommonRep {
    public FixNetValueResultC() {
    }

    CommonRepBean repBean = null;
    private java.util.Date startDate = null; //期初日期
    private java.util.Date endDate = null; //期末日期
    private String sPort = ""; //组合代码
    private String dataIndex = ""; //
    private FixPub fixPub = null;

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
        StringBuffer buf = new StringBuffer();
        String strResult = "";
        StringBuffer strBuf = null;
        String setCode = "";
        String oneResult = "";
        double twoResult = 0.0;
        double threeResult = 0.0;
        ResultSet rs = null;
        String strSql = "";
        double inception = 0.0;
        java.util.Date inceptionDate = null;
        String firstRow = "";
        try {
            setCode = fixPub.getSetCode(sPort); //获取套帐代码
            inceptionDate = fixPub.getInceptionDate(sPort); //获取基金成立日期
            inception = fixPub.calInceptionMoney(inceptionDate, setCode); //获取基金成立日那天的基金单位净值
            firstRow = calFirstRow(sPort);
            buf.append(fixPub.buildRowCompResult(firstRow, "DsXXPL000203")).append(
                "\r\n");
            oneResult = YssFun.formatDate(endDate);

            twoResult = this.calTwoData(startDate, endDate, "", setCode,
                                        inception);
            threeResult = this.calThreeData(startDate, endDate, "", setCode,
                                            inception);
            strBuf = new StringBuffer();
            strBuf.append(oneResult).append(",");
            strBuf.append(YssFun.roundIt(twoResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(threeResult, 2) + "%");
            strBuf.append(" ").append(",");
            strBuf.append(" ").append(",");
            strBuf.append(" ").append(",");
            strBuf.append(" ").append(",");
            strBuf.append(" ").append(",");
            strBuf.append(" ").append(",");
            strBuf.append(" ").append(",");
            buf.append(fixPub.buildRowCompResult(strBuf.toString(), "DsXXPL000203")).
                append("\r\n");
            if (buf.toString().length() > 2) {
                strResult = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }
            return strResult;
        }

        catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public String calFirstRow(String portCode) throws YssException {
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        String shortName = "";
        try {
            buf.append("年份").append(",");
            buf.append(fixPub.getPortName(sPort)).append(",");
            buf.append("业绩比较基准收益率");
            return buf.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public double calTwoData(java.util.Date startDate, java.util.Date endDate,
                             String month,
                             String setCode, double inception) throws
        YssException {
        java.util.Date beginDate = null;
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
            beginNetValue = fixPub.getNetValue(beginDate, setCode);
            if (beginNetValue == 0) {
                beginNetValue = inception;
            }
            endNetValue = fixPub.getNetValue(endDate, setCode);
            if (beginNetValue != 0) {
                sResult = YssD.sub(YssD.div(endNetValue, beginNetValue), 1);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public double calThreeData(java.util.Date startDate, java.util.Date endDate,
                               String month,
                               String setCode, double inception) throws
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
                list.add(new Double(fixPub.getNetValue(tmpDate, setCode)));
                allResult = allResult +
                    fixPub.getNetValue(tmpDate, setCode);
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
        }
    }

}
