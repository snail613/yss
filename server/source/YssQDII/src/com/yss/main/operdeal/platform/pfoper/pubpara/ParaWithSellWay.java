package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.dsub.BaseBean;
import com.yss.util.YssException;
import java.sql.ResultSet;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ParaWithSellWay
    extends BaseBean {

    private String paraGroupCode = "";
    private String pubParaCode = "";
    private String tradeType = "";
    private String fCtlValue = "";

    public String getTradeType() {
        return tradeType;
    }

    public String getParaGroupCode() {
        return paraGroupCode;
    }

    public void setPubParaCode(String pubParaCode) {
        this.pubParaCode = pubParaCode;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public void setParaGroupCode(String paraGroupCode) {
        this.paraGroupCode = paraGroupCode;
    }

    public void setFCtlValue(String fCtlValue) {
        this.fCtlValue = fCtlValue;
    }

    public String getPubParaCode() {
        return pubParaCode;
    }

    public String getFCtlValue() {
        return fCtlValue;
    }

    public ParaWithSellWay() {
    }

    //----------------------------------------------------------------------------
    /**
     * 2008-4-28 单亮
     * 获取销售渠道
     * @return Object
     * @throws YssException
     */
    public Object getParaResult() throws YssException {
        String reStr = "";
        String sqlStr = "";
        String tempStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String[] reqAry = null;

        try {
            sqlStr = "select * from  " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FCtlCode = 'cboTAMode' and FCtlValue = " + dbl.sqlString(this.fCtlValue);
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) {
                sqlStr = "select fctlvalue from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                    " where fparaid =" + grpRs.getInt("FParaId") + " and fctlcode ='scSellPoints'";

                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
//              if (rsTest.getString("FCtlInd").equalsIgnoreCase("<Result>")) {
//                 resultValue = rsTest.getString("FCtlValue");
//              }
                    if (rsTest.getString("FCtlValue").indexOf("|") >= 0) {
                        reqAry = rsTest.getString("FCtlValue").replaceAll("[|]", "\t").split("\t"); //2008-5-7 单亮 将replace替换为replaceAll
                        tempStr = reqAry[0];
                        if (tempStr.length() != 0) {
                            reStr = tempStr;
                        }
                    }
                }
                if (reStr.length() > 0) {
                    break;
                }
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }
    }

}
