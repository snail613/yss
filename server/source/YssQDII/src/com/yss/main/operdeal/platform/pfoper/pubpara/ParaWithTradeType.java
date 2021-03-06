package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.dsub.BaseBean;
import com.yss.util.YssException;
import java.sql.ResultSet;

/**
 * 按照交易类型计算加权平均成本
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ParaWithTradeType
    extends BaseBean {

    private String paraGroupCode = "";
    private String pubParaCode = "";
    private String tradeType = "";
    private String ctlCode = "";
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

    public String getPubParaCode() {
        return pubParaCode;
    }

    public void setCltCode(String cltCode) {
        this.ctlCode = cltCode;
    }

    public ParaWithTradeType() {
    }

    /**
     * 返回特定的控件值 sj add 20080528
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
                    if (rsTest.getString("FCtlInd").equalsIgnoreCase("<tradetype>")
                        &&
                        rsTest.getString("FCtlValue").split("[|]")[0].equalsIgnoreCase( //如果满足条件
                            tradeType)) {
                        isCheck = true;
                    }
                    if (resultValue.length() > 0 && isCheck) { //如果需要返回的控件的控件值有值,而且满足条件
                        reStr = resultValue;
                        break;
                    }
                }
                //外层循环每循环一次，将重新产生一个游标，此处进行关闭 sunkey 20090112
                dbl.closeResultSetFinal(rsTest);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(rsTest, grpRs);
        }

    }

    //----------------------------------------------------------------------------
    public Object getParaResult() throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValue = "";
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                //" where FParagroupCode = " + dbl.sqlString(this.paraGroupCode) +
                " where FCtlGrpCode = " +
                " and FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaId <> 0" +
                " group by  FParagroupCode,FPubParaCode,FParaId";
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) {
                sqlStr =
                    "select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from (select *" +
                    " from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FParagroupCode = " + dbl.sqlString(this.paraGroupCode) +
                    " and FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    " and FParaId = " +
                    grpRs.getInt("FParaId") +
                    ") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on " +
                    "  para.FCtlGrpCOde = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode";
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
                    if (rsTest.getString("FCtlInd").equalsIgnoreCase("<Result>")) {
                        resultValue = rsTest.getString("FCtlValue");
                    }
                    if (rsTest.getString("FCtlInd").equalsIgnoreCase("<tradetype>")
                        &&
                        rsTest.getString("FCtlValue").split(",")[0].equalsIgnoreCase(
                            tradeType)) {
                        reStr = resultValue;
                    }
                }
                if (reStr.length() > 0) {
                    break;
                }
                
                dbl.closeResultSetFinal(rsTest); //add by fangjiang bug 3442 2011.01.11
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
