package com.yss.main.operdeal.report.repfix;

import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.dsub.BaseBean;
import java.sql.ResultSet;
import com.yss.main.compliance.CompIndexBean;
import com.yss.pojo.param.comp.YssCompRep;
import java.util.HashMap;
import com.yss.pojo.param.comp.YssCompDeal;
import com.yss.util.YssReflection;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.util.YssFun;
import com.yss.main.dao.IComplianceDeal;
import com.yss.util.YssCons;
import com.yss.vsub.YssFinance;
import com.yss.util.YssD;

public class FixNetValueResult
    extends BaseBuildCommonRep {
    public FixNetValueResult() {
    }

    private TradeSubBean tdSub;
    private CommonRepBean repBean;
    String strShowColor = ""; //显示的颜色

    private java.util.Date endDate = null;
    private String oldDays = "";
    private String sPort = "";
    /**
     * buildReport
     *
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        sResult = buildResult(this.endDate, this.sPort, this.oldDays);
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
            this.oldDays = sReq[1].split("\r")[1];
            this.sPort = sReq[2].split("\r")[1];
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

    protected String buildResult(java.util.Date endDate, String sPort, String oldDays) throws
        YssException {
        //HashMap hmComp = new HashMap();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        //YssCompRep compRep = new YssCompRep();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        String sKey = "";
        String strResult = "";
        String str = "";
        StringBuffer strBuf = null;
        String[] days = null;
        java.util.Date beginDate = null;
        ResultSet beginRs = null;
        ResultSet endRs = null;
        double beginNetValue = 0.0;
        double endNetValue = 0.0;
        YssFinance finance = null;
        String portCode = "";

        try {

            days = oldDays.split(",");
            for (int i = 0; i < days.length; i++) {
                finance = new YssFinance();
                finance.setYssPub(pub);
                strBuf = new StringBuffer();
                if (Integer.parseInt(days[i]) < 0 || Integer.parseInt(days[i]) > 12) {
                    throw new YssException("输入的月份不合法");
                }
                if (days[i].equalsIgnoreCase("12")) {
                    strBuf.append("过去1年").append(",");
                } else {
                    strBuf.append("过去" + days[i] + "月").append(",");
                }

                beginDate = YssFun.addDay(endDate, Integer.parseInt(days[i]) - 1);
                portCode = finance.getCWSetCode(sPort); //获取套帐号
                strSql = " select * from " + pub.yssGetTableName("tb_rep_guessvalue") +
                    " where FDATE=" + dbl.sqlDate(beginDate) +
                    " and FPortCode=" + dbl.sqlString(portCode) +
                    " and FAcctCode=" + dbl.sqlString("9600");
                beginRs = dbl.openResultSet(strSql);
                while (beginRs.next()) {
                    beginNetValue = beginRs.getDouble("FStandardMoneyMarketValue");
                }
                strSql = " select * from " + pub.yssGetTableName("tb_rep_guessvalue") +
                    " where FDATE=" + dbl.sqlDate(endDate) +
                    " and FPortCode=" + dbl.sqlString(portCode) +
                    " and FAcctCode=" + dbl.sqlString("9600");
                endRs = dbl.openResultSet(strSql);
                while (endRs.next()) {
                    endNetValue = endRs.getDouble("FStandardMoneyMarketValue");
                }
                if (beginNetValue == 0) {
                    strBuf.append(0).append(",");
                } else {
                    strBuf.append(YssD.sub(YssD.div(endNetValue, beginNetValue), 1)).append(",");
                }
                strBuf.append("b").append(",");
                strBuf.append("c").append(",");
                strBuf.append("d").append(",");
                strBuf.append("e").append(",");
                strBuf.append("f");

                str = strBuf.toString();
                buf.append(this.buildRowCompResult(str)).append("\r\n");
            }
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
            dbl.closeResultSetFinal(beginRs);
            dbl.closeResultSetFinal(endRs);
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
            hmCellStyle = getCellStyles("DS000201");
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("DS000201") +
                " and FCheckState = 1 order by FOrderIndex";
            rs = dbl.openResultSet(strSql);
            for (int i = 0; i < sArry.length; i++) {
                sKey = "DS000201" + "\tDSF\t-1\t" + i;
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
        //---add by songjie 2011.04.27 资产估值报游标超出最大数错误---//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //---add by songjie 2011.04.27 资产估值报游标超出最大数错误---//
    }

}
