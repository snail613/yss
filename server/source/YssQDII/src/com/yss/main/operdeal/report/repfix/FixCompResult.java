package com.yss.main.operdeal.report.repfix;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.report.*;
import com.yss.main.report.CommonRepBean;
import com.yss.main.operdata.*;
import com.yss.util.YssException;
import com.yss.pojo.param.comp.*;
import java.util.*;
import com.yss.main.compliance.*;
import java.sql.*;
import com.yss.util.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.cusreport.*;

//交易验证后产生的监控结果报表
public class FixCompResult
    extends BaseBuildCommonRep {
    private TradeSubBean tdSub;
    private CommonRepBean repBean;
    String strShowColor = ""; //显示的颜色
    public FixCompResult() {
    }

    /**
     * buildReport
     *
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        sResult = compOrder(tdSub.getPortCode(), tdSub.getCompPoint());
        return sResult;
    }

    /**
     * initBuildReport
     *
     * @param bean BaseBean
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        repBean = (CommonRepBean) bean;
        tdSub = new TradeSubBean();
        tdSub.setYssPub(pub);
        tdSub.parseRowStr(repBean.getRepCtlParam());
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

    protected String compOrder(String sPortCodes, String sCompPoint) throws
        YssException {
        String[] sPortCodeAry = sPortCodes.split(",");
        YssCompDeal compParam = new YssCompDeal();
        HashMap hmComp = new HashMap();
        YssCompRep compRep = new YssCompRep();
        CompIndexBean compIndex = new CompIndexBean();
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        ResultSet rsPort = null;
        String sPortName = "";
        String sKey = "";
        String strResult = "";
        String strGrade = ""; //监控等级
        ResultSet rsColor = null; //得到监控等级相对应的颜色
        String strWarnColor = ""; //警告颜色
        String strForbidColor = ""; //禁止颜色
        String strPassColor = ""; //正常颜色
        try {
            IComplianceDeal comp = (IComplianceDeal) pub.getOperDealCtx().
                getBean("compliancedeal");
            for (int i = 0; i < sPortCodeAry.length; i++) {
                compParam.setPortCode(sPortCodeAry[i]);
                compParam.setInvMgrCode(tdSub.getInvMgrCode());
                compParam.setBrokerCode(tdSub.getBrokerCode());
                compParam.setSecurityCode(tdSub.getSecurityCode());
                compParam.setDDate(YssFun.toDate(tdSub.getBargainDate()));
                compParam.setCompPoint(sCompPoint);
                comp.init(compParam);
                comp.setYssPub(pub);
                hmComp.putAll(comp.doCompliance());
                strSql = "select * from " + pub.yssGetTableName("tb_para_portfolio") + " where FPortCode=" +
                    dbl.sqlString(sPortCodeAry[i]);
                rsPort = dbl.openResultSet(strSql);
                if (rsPort.next()) {
                    sPortName = rsPort.getString("FPortName");
                }

                strSql = "select a.*,b.FIndexTempName,o.FVocName as FBCompName,p.FVocName as FACompName from " +
                    pub.yssGetTableName("Tb_Comp_Index") + " a " +
                    //-----------------------------------------------------------
                    " left join Tb_Fun_Vocabulary o on a.FBcompWay = o.FVocCode and o.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_CI_BCOMPWAY) +
                    " left join Tb_Fun_Vocabulary p on a.FAcompWay = p.FVocCode and p.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_CI_ACOMPWAY) +
                    //------------------------------------------------------------
                    " join (select ba.FIndexTempCode,FIndexTempName,ba.FStartDate from " +
                    pub.yssGetTableName("Tb_Comp_IndexTemplate") + " ba " +
                    " join (select FIndexTempCode, Max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("Tb_Comp_IndexTemplate") +
                    " where FCheckState = 1 and FStartDate <= " +
                    dbl.sqlDate(compParam.getDDate()) + " group by FIndexTempCode) bb" +
                    " on ba.FIndexTempCode = bb.FIndexTempCode and ba.FStartDate = bb.FStartDate " +
                    ") b on a.FIndexTempCode = b.FIndexTempCode" +
                    //------------------------------------------------------------
                    " where a.FCheckState = 1 and a." + sCompPoint +
                    " = 1 and a.FIndexTempCode in (select FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FRelaType = 'Template' and FCheckState = 1" +
                    " and FPortCode = " + dbl.sqlString(sPortCodeAry[i]) + ")";
                rs = dbl.openResultSet(strSql);
                dbl.closeResultSetFinal(rsPort);
                while (rs.next()) {
                    sKey = YssFun.formatDate(tdSub.getBargainDate(), "yyyyMMdd") + "\f" +
                        sPortCodeAry[i] +
                        "\f" + rs.getString("FIndexTempCode") + "\f" +
                        rs.getString("FIndexCode");
                    compIndex = (CompIndexBean) hmComp.get(sKey);

                    compRep.setDDate(YssFun.toDate(tdSub.getBargainDate()));
                    compRep.setPortCode(sPortCodeAry[i]);
                    compRep.setPortName(sPortName);
//               if (i < sPortNameAry.length) compRep.setPortName(sPortNameAry[
//                     i]);
                    compRep.setTemplateCode(rs.getString("FIndexTempCode") + "");
                    compRep.setTemplateName(rs.getString("FIndexTempName") + "");
                    compRep.setCompIndexCode(rs.getString("FIndexCode") + "");
                    compRep.setCompIndexName(rs.getString("FIndexName") + "");
                    //可以通过监控等级 来获得其颜色
                    //  strGrade = rs.getString("FEndCompGrade");
                    /*  if(strGrade!=null)
                      {
                         strSql = "select * from " +
                               pub.yssGetTableName("Tb_Comp_Grade") +
                               " where FGradeCode=" + dbl.sqlString(strGrade);
                         rsColor = dbl.openResultSet(strSql);
                         if (rsColor.next()) {
                            strWarnColor = rsColor.getInt("FWarnColor") + ""; //警告颜色
                            strForbidColor = rsColor.getInt("FForbidColor") + ""; //禁止颜色
                            strPassColor = rsColor.getInt("FPassColor") + ""; //正常颜色
                         }
                      }*/
                    if (sCompPoint.equalsIgnoreCase("FBOrder")) { //下单前
                        compRep.setCompWay(rs.getString("FBCompName"));
                    } else if (sCompPoint.equalsIgnoreCase("FAOrder")) { //下单后
                        compRep.setCompWay(rs.getString("FACompName"));
                    }
                    if (compIndex != null) {
                        compRep.setCompResult("违规");
                        compRep.setUnPassHint(compIndex.getUnPassHint());
                        if (sCompPoint.equalsIgnoreCase("FBOrder")) { //下单前
                            if (compIndex.getBCompWay() == 1) { //监控方式为禁止
                                repBean.setDataIsTrue(false);
                                // strShowColor = strWarnColor;
                            } else {
                                //strShowColor = strForbidColor;
                            }
                        } else if (sCompPoint.equalsIgnoreCase("FAOrder")) { //下单后
                            if (compIndex.getACompWay() == 1) { //监控方式为禁止
                                repBean.setDataIsTrue(false);
                                //strShowColor = strForbidColor;
                            } else {
                                //strShowColor=strWarnColor;
                            }
                        }
                    } else {
                        compRep.setCompResult("正常");
                        // strShowColor=strPassColor;//把正常的颜色赋给要显示的颜色
                        compRep.setUnPassHint("");
                    }
                    buf.append(this.buildRowCompResult(compRep)).append("\r\n");
                    dbl.closeResultSetFinal(rsColor); //每次都要关掉打开的颜色记录集
                }
                dbl.closeResultSetFinal(rs);
            }

            if (buf.toString().length() > 2) {
                strResult = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (strResult.length() == 0) {
//            strResult = "true";
                repBean.setDataIsTrue(true);
            }

            return strResult;
        } catch (Exception e) {
            throw new YssException("获取下单前监控结果出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsPort);
        }
    }

    protected String buildRowCompResult(YssCompRep compRep) throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";
        RepTabCellBean rtc = null;
        try {
            hmCellStyle = getCellStyles("DS000104");
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("DS000104") +
                " and FCheckState = 1 order by FOrderIndex";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //获得样式
                sKey = "DS000104" + "\tDSF\t-1\t" +
                    rs.getString("FOrderIndex");
                if (hmCellStyle.containsKey(sKey)) {
                    //这里设置等级颜色 要根据等级 以及 等级中的严重程度来对颜色进行设置
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    /*if(this.strShowColor.indexOf("-") >= 0)
                                    {
                      this.strShowColor=this.strShowColor.substring(1,this.strShowColor.length()-1);
                                    }
                                    if (!YssFun.isNumeric(strShowColor)){
                       strShowColor = "0";
                                    }
                                    rtc.setBackColor(this.strShowColor);*/
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(YssReflection.getPropertyValue(compRep, rs.getString("FDsField")) +
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
