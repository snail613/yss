package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.dsub.BaseBean;
import com.yss.util.YssException;
import java.sql.ResultSet;

public class ParaWithAccPaidDigit
    extends BaseBean {
    private String paraGroupCode = "";
    private String pubParaCode = "";
    private String cashAccCode = "";
    private String ctlGrpCode = "";
    private String ctlCode = "";

    public String getCtlCode() {
        return ctlCode;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public String getCtlGrpCode() {
        return ctlGrpCode;
    }

    public String getParaGroupCode() {
        return paraGroupCode;
    }

    public void setPubParaCode(String pubParaCode) {
        this.pubParaCode = pubParaCode;
    }

    public void setCtlCode(String ctlCode) {
        this.ctlCode = ctlCode;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setCtlGrpCode(String ctlGrpCode) {
        this.ctlGrpCode = ctlGrpCode;
    }

    public void setParaGroupCode(String paraGroupCode) {
        this.paraGroupCode = paraGroupCode;
    }

    public String getPubParaCode() {
        return pubParaCode;
    }

    public ParaWithAccPaidDigit() {
    }

    /**
     * 返回特定的控件值
     * @return Object
     * @throws YssException
     */
    public Object getSpeParaResult() throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValue = "";
        boolean isCheck = false;
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaId <> 0" +
                " group by  FParagroupCode,FPubParaCode,FParaId";
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) {
                resultValue = ""; //恢复初始值.
                isCheck = false;
                sqlStr =
                    "select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from (select *" +
                    " from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    " and FParaId = " +
                    grpRs.getInt("FParaId") +
                    ") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on " +
                    "  para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode";
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
                    if (rsTest.getString("FCtlCode").equalsIgnoreCase(this.
                        ctlCode)) { //如果是需要返回的控件，则先将控件值保存
                        resultValue = rsTest.getString("FCtlValue");
                    }
                    if (rsTest.getString("FCtlInd").equalsIgnoreCase("<CashAcc>")
                        &&
                        rsTest.getString("FCtlValue").split("[|]")[0].equalsIgnoreCase( //如果满足条件
                            cashAccCode)) {
                        isCheck = true;
                    }
                    if (resultValue.length() > 0 && isCheck) { //如果需要返回的控件的控件值有值,而且满足条件
                        reStr = resultValue;
                        break;
                    }
                }
                dbl.closeResultSetFinal(rsTest);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("获取通用参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }

    }

}
