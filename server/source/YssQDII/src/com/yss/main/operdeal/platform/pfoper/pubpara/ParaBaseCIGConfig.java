package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.dsub.*;
import java.sql.ResultSet;
import com.yss.util.YssException;

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
public class ParaBaseCIGConfig
    extends BaseBean {
    private String paraGroupCode = "";
    private String pubParaCode = "";

    private String ctlGrpCode = "";
    private String ctlCode = "";

    public ParaBaseCIGConfig() {
    }

    public String getCtlGrpCode() {
        return ctlGrpCode;
    }

    public String getCtlCode() {
        return ctlCode;
    }

    public String getParaGroupCode() {
        return paraGroupCode;
    }

    public String getPubParaCode() {
        return pubParaCode;
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

    public void setPubParaCode(String pubParaCode) {
        this.pubParaCode = pubParaCode;
    }

    /**
     * 返回特定的控件值
     * @return Object
     * @throws YssException
     */
    public Object getSpeParaResult() throws YssException {
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
                if (rsTest.next()) {
                    resultValue = rsTest.getString("FCtlValue");
                }
                dbl.closeResultSetFinal(rsTest);
            }
        } catch (Exception e) {
            throw new YssException("获取通用参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }
        //2008.10.26 蒋锦 修改 如果没有取到控件值则返回""
        return resultValue;
    }

}
