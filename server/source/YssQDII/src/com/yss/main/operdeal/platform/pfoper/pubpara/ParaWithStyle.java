package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.dsub.BaseBean;
import com.yss.util.YssException;
import java.sql.ResultSet;

/**
 * 是否需新划款指令格式
 * @param paraGroupCode String
 * @param pubParaCode String
 * @param ctlGrpCode String
 * @param ctlCode String
 * @throws YssException
 * @return String
 * add by yanghaiming 20091110 MS00804 QDV4中金2009年11月10日01_B
 */

public class ParaWithStyle
	extends BaseBean{
	private String paraGroupCode = "";
    private String pubParaCode = "";
    private String ctlGrpCode = "";
    private String ctlCode = "";
    
    public String getCtlCode() {
        return ctlCode;
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

    public void setCtlGrpCode(String ctlGrpCode) {
        this.ctlGrpCode = ctlGrpCode;
    }

    public void setParaGroupCode(String paraGroupCode) {
        this.paraGroupCode = paraGroupCode;
    }

    public String getPubParaCode() {
        return pubParaCode;
    }

    public ParaWithStyle() {
    }

    public String getSpeParaResult() throws YssException {
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValue = "";
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaId <> 0" +
                " group by  FParagroupCode,FPubParaCode,FParaId";
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) {
                resultValue = ""; //恢复初始值.
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
                        ctlCode)) { //获取控件的值
                        resultValue = rsTest.getString("FCtlValue");
                    }
                }
                dbl.closeResultSetFinal(rsTest);
            }
            return resultValue;
        } catch (Exception e) {
            throw new YssException("获取通用参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }

    }

}
