package com.yss.main.operdeal.report.netvalueviewpl;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.cusreport.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.report.*;
import com.yss.util.*;
import com.yss.vsub.*;

public class FixNetValueResultA
    extends BaseBuildCommonRep {
    public FixNetValueResultA() {
    }

    CommonRepBean repBean = null;
    private java.util.Date startDate = null; //期初日期
    private java.util.Date endDate = null; //期末日期
    private String sPort = ""; //组合代码
    private String dataIndex = ""; //指数行情代码
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
                                 java.util.Date endDate,
                                 String sPort) throws
        YssException {
        String month = "";
        StringBuffer buf = new StringBuffer();
        String strResult = "";
        StringBuffer strBuf = null;
        YssFinance finance = null;
        String setCode = "";
        String oneResult = "";
        double twoResult = 0.0;
        double threeResult = 0.0;
        double fourResult = 0.0;
        double fiveResult = 0.0;
        double sixResult = 0.0;
        double sevenResult = 0.0;
        String finalStr = "";
        ResultSet rs = null;
        String strSql = "";
        int startYear = 0;
        int endYear = 0;
        int numYear = 0;
        double inception = 0.0;
        java.util.Date inceptionDate = null;
        try {
            setCode = fixPub.getSetCode(sPort); //获取套帐
            inceptionDate = fixPub.getInceptionDate(sPort); //获取基金成立日期
            inception = fixPub.calInceptionMoney(inceptionDate, setCode); //获取基金成立日那里天的基金单位净值

            oneResult = "过去3个月";
            twoResult = this.calTwoData(startDate, endDate, "3", setCode, inception);
            threeResult = this.calThreeData(startDate, endDate, "3", setCode, inception);
            fourResult = this.calFourData(startDate, endDate, "3", setCode, inception);
            fiveResult = this.calFiveData(startDate, endDate, "3", setCode, inception);
            sixResult = YssD.sub(twoResult, fourResult);
            sevenResult = YssD.sub(threeResult, fiveResult);
            strBuf = new StringBuffer();
            strBuf.append(oneResult).append(",");
            strBuf.append(YssFun.roundIt(twoResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(threeResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(fourResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(fiveResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(sixResult, 2) + "%").append(",");
            strBuf.append(YssFun.roundIt(sevenResult, 2) + "%");
            buf.append(fixPub.buildRowCompResult(strBuf.toString(), "Ds0000201")).append("\r\n");
            /*
                startYear=YssFun.getYear(startDate);
                endYear=YssFun.getYear(endDate);

                if((endYear-startYear)>0)
                {
                  for(int i=1;i<=endYear-startYear;i++)
                  {
                     oneResult="过去"+i+"年";
                     twoResult = this.calTwoData(startDate,endDate, "", setCode,inception);
                     threeResult = this.calThreeData(startDate,endDate, "", setCode,inception);
                     fourResult= this.calFourData(startDate,endDate, "", setCode,inception);
                     fiveResult= this.calFourData(startDate,endDate, "", setCode,inception);
                     sixResult=YssD.sub(twoResult,fourResult);
                     sevenResult=YssD.sub(threeResult,fiveResult);
                     strBuf = new StringBuffer();
                     strBuf.append(oneResult).append(",");
                     strBuf.append(YssFun.roundIt(twoResult,2)+"%").append(",");
                     strBuf.append(YssFun.roundIt(threeResult,2)+"%").append(",");
                     strBuf.append(YssFun.roundIt(fourResult,2)+"%").append(",");
                     strBuf.append(YssFun.roundIt(fiveResult,2)+"%").append(",");
                     strBuf.append(YssFun.roundIt(sixResult,2)+"%").append(",");
                     strBuf.append(YssFun.roundIt(sevenResult,2)+"%");
                     buf.append(fixPub.buildRowCompResult(strBuf.toString(),"Ds0000201")).append("\r\n");
                  }
                }
             */
            finalStr = this.calFinalData(endDate, month, setCode, inception);
            buf.append(fixPub.buildRowCompResult(finalStr, "Ds0000201")).append("\r\n");
            if (buf.toString().length() > 2) {
                strResult = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }
            return strResult;
        } catch (Exception e) {
            throw new YssException("获取报表数据报错!", e);
        }
    }

    public double calTwoData(java.util.Date startDate, java.util.Date endDate, String month,
                             String setCode, double inception) throws
        YssException {
        double beginNetValue = 0.0;
        double endNetValue = 0.0;
        double sResult = 0.0;
        try {
            beginNetValue = fixPub.getNetValue(startDate, setCode);
            if (beginNetValue == 0) {
                beginNetValue = inception;
            }
            endNetValue = fixPub.getNetValue(endDate, setCode);
            if (beginNetValue != 0) {
                sResult = YssD.div(endNetValue, beginNetValue);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取第二列数据报错!", e);
        }
    }

    public double calThreeData(java.util.Date startDate, java.util.Date endDate, String month,
                               String setCode, double inception) throws YssException {
        int days = 0;
        java.util.Date tmpDate = null;
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
            throw new YssException("计算第四列报错!", e);
        }
    }

    public double calFourData(java.util.Date startDate, java.util.Date endDate, String month,
                              String setCode, double inception) throws YssException {
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
            beginNetValue = fixPub.getIndexDate(beginDate, this.dataIndex);
            if (beginNetValue == 0) {
                beginNetValue = inception;
            }
            endNetValue = fixPub.getIndexDate(endDate, this.dataIndex);
            if (beginNetValue != 0) {
                sResult = YssD.sub(YssD.div(endNetValue, beginNetValue), 1);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取数据报错", e);
        }
    }

    public double calFiveData(java.util.Date startDate, java.util.Date endDate, String month,
                              String setCode, double inception) throws YssException {
        int days = 0;
        java.util.Date tmpDate = null;
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
                list.add(new Double(fixPub.getIndexDate(tmpDate, this.dataIndex)));
                allResult = allResult +
                    fixPub.getIndexDate(tmpDate, this.dataIndex);
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
            throw new YssException("获取数据报错!", e);
        }
    }

    public String calFinalData(java.util.Date endDate, String month, String setCode, double inception) throws YssException {
        StringBuffer strBuf = new StringBuffer();
        java.util.Date inceptionDate = null;
        double endNetValue = 0.0;
        double oneResult = 0.0;
        double twoResult = 0.0;
        double threeResult = 0.0;
        double fourResult = 0.0;
        double fiveResult = 0.0;
        double sixResult = 0.0;
        double sevenResult = 0.0;
        try {
            inceptionDate = fixPub.getInceptionDate(sPort);
            inception = fixPub.getNetValue(inceptionDate, setCode);
            endNetValue = fixPub.getNetValue(endDate, setCode);
            if (endNetValue != 0) {
                oneResult = YssD.div(endNetValue, inception);
            }
            twoResult = this.calTwoData(startDate, endDate, month, setCode, inception);
            fiveResult = YssD.sub(oneResult, threeResult);
            sixResult = YssD.sub(twoResult, fourResult);
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
        }
    }
}
