package com.yss.main.compliance;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.pojo.param.comp.*;
import com.yss.util.*;

public class CompRepBean
    extends BaseReportBean implements IClientReportView {
    private java.util.Date beginDate;
    private java.util.Date endDate;
    private String portCodes;
    private String portNames;
    private String compAction;

    public CompRepBean() {
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
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            reqAry = sRowStr.split("\t");
            this.beginDate = YssFun.toDate(reqAry[0]);
            this.endDate = YssFun.toDate(reqAry[1]);
            this.portCodes = reqAry[2];
            this.portNames = reqAry[3];
        } catch (Exception e) {
            throw new YssException("解析监控报表请求出错", e);
        }
    }

    /**
     * getReportData
     *
     * @param sReportType String
     * @return String
     */
    public String getReportData(String sReportType) throws YssException {
        String strSql = "";
        String strResult = "";
        ResultSet rs = null;
        int iDays = 0;
        String[] sPortCodeAry = null;
        String[] sPortNameAry = null;
        HashMap hmComp = new HashMap();
        YssCompDeal compParam = new YssCompDeal();
        java.util.Date dDate = null;
        StringBuffer buf = new StringBuffer();
        YssCompRep compRep = new YssCompRep();
        CompIndexBean compIndex = null;
        String sKey = "";
        try {
            iDays = YssFun.dateDiff(beginDate, endDate) + 1;
            sPortCodeAry = portCodes.split(",");
            sPortNameAry = portNames.split(",");
            IComplianceDeal compDeal = (IComplianceDeal) pub.getOperDealCtx().
                getBean("compliancedeal");

            buf.append(getReportHeaders(sReportType)).append(YssCons.
                YSS_LINESPLITMARK);

            for (int i = 0; i < sPortCodeAry.length; i++) {
                for (int j = 0; j < iDays; j++) {
                    dDate = YssFun.addDay(beginDate, j);
                    compParam.setDDate(dDate);
                    compParam.setPortCode(sPortCodeAry[i]);
                    compParam.setCompPoint("FEndOfDay");
                    compDeal.init(compParam);
                    compDeal.setYssPub(pub);
                    hmComp.putAll(compDeal.doCompliance());

                    strSql = "select a.*,b.FIndexTempName from " +
                        pub.yssGetTableName("Tb_Comp_Index") + " a " +
                        //------------------------------------------------------------
                        " left join (select ba.FIndexTempCode,FIndexTempName,ba.FStartDate from " +
                        pub.yssGetTableName("Tb_Comp_IndexTemplate") + " ba " +
                        " join (select FIndexTempCode, Max(FStartDate) as FStartDate from " +
                        pub.yssGetTableName("Tb_Comp_IndexTemplate") +
                        " where FCheckState = 1 group by FIndexTempCode) bb" +
                        " on ba.FIndexTempCode = bb.FIndexTempCode and ba.FStartDate = bb.FStartDate " +
                        ") b on a.FIndexTempCode = b.FIndexTempCode" +
                        //------------------------------------------------------------
                        " where a.FCheckState = 1 and a.FEndOfDay = 1 and a.FIndexTempCode in (select FSubCode from " +
                        pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                        " where FRelaType = 'Template' and FCheckState = 1" +
                        " and FPortCode = " + dbl.sqlString(sPortCodeAry[i]) + ")";
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        sKey = YssFun.formatDate(dDate, "yyyyMMdd") + "\f" +
                            sPortCodeAry[i] +
                            "\f" + rs.getString("FIndexTempCode") + "\f" +
                            rs.getString("FIndexCode");
                        compIndex = (CompIndexBean) hmComp.get(sKey);

                        compRep.setDDate(dDate);
                        compRep.setPortCode(sPortCodeAry[i]);
                        if (i < sPortNameAry.length) {
                            compRep.setPortName(sPortNameAry[
                                                i]);
                        }
                        compRep.setTemplateCode(rs.getString("FIndexTempCode") + "");
                        compRep.setTemplateName(rs.getString("FIndexTempName") + "");
                        compRep.setCompIndexCode(rs.getString("FIndexCode") + "");
                        compRep.setCompIndexName(rs.getString("FIndexName") + "");
                        if (compIndex != null) {
                            compRep.setCompWay("违规"); //为了与下单前监控的报表保持协议的统一，暂时这样写，因为日终监控没有监控方式
                            compRep.setCompResult("违规");
                        } else {
                            compRep.setCompWay("正常");
                            compRep.setCompResult("正常");
                        }
                        buf.append(compRep.buildRowStr()).append("\r\n");
                    }
                    dbl.closeResultSetFinal(rs);
                }
            }

            if (buf.toString().length() > 2) {
                strResult = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            return strResult;
        } catch (Exception e) {
            throw new YssException("获取日终监控数据出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getReportHeaders
     *
     * @param sReportType String
     * @return String
     */
    public String getReportHeaders(String sReportType) throws YssException {
        String reStr = "";
        String reStrKey = "";
        reStr = this.getReportHeaders1();
        reStrKey = this.getReportFields1();
        return reStr + YssCons.YSS_LINESPLITMARK + reStrKey;
    }
    
    //add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
    public String checkReportBeforeSearch(String sReportType){
    	return "";
    }

    /**shashijie 2011.04.07 STORY #805 头寸表应该预测T日到T+N-1日共N个工作日的头寸 */
	public String getSaveDefuntDay(String sRepotyType) throws YssException {
		return "";
	}
	
    public String GetBookSetName(String sPortCode) throws YssException
    {
    	return "";
    	
    }
}
