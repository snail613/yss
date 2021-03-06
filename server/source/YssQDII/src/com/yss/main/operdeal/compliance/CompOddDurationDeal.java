package com.yss.main.operdeal.compliance;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.compliance.*;
import com.yss.main.dao.*;
import com.yss.pojo.param.comp.*;
import com.yss.util.*;

//计算帐户的平均剩余期限
public class CompOddDurationDeal
    extends BaseBean implements IComplianceDeal, IBuildReport {

    private java.util.Date dDate;
    private String portCode;
    private String curyCode;
    private CompIndexBean compIndex;

    private double compODDayNum; //监控剩余期限的天数
    private String compSign = ""; //监控剩余期限的符号

    private double sumHaveBaseMoney; //帐户持有总金额
    private double sumHaveBaseMoneyOD; //帐户（持有金额*到期天数）的总额

    private String secTypes = ""; //证券品种类型
    private String accTypes = ""; //帐户类型

    public CompOddDurationDeal() {

    }

    /**
     * doCompliance
     *
     * @return HashMap
     */
    public HashMap doCompliance() throws YssException {
        ArrayList alConds = null;
        HashMap hmResult = null;
        boolean bFlag = false;
        String sKey = "";
        String sWhereSql = "";
        double dODDayNum = 0;
        alConds = getCompConds(compIndex, 99, 99);
        sWhereSql = this.buildFilterSql(alConds);
        if (accTypes.length() > 0) {
            getAccDetailOD(sWhereSql);
        }
        if (secTypes.length() > 0) {
            if (secTypes.indexOf("FI") >= 0) {
                getFIDetailOD(sWhereSql);
            }
        }
        if (this.sumHaveBaseMoney != 0) {
            dODDayNum = YssD.div(this.sumHaveBaseMoneyOD,
                                 this.sumHaveBaseMoney);
        }
        if (compSign.equalsIgnoreCase(">")) {
            bFlag = dODDayNum > compODDayNum;
        } else if (compSign.equalsIgnoreCase(">=")) {
            bFlag = dODDayNum >= compODDayNum;
        } else if (compSign.equalsIgnoreCase("=")) {
            bFlag = dODDayNum == compODDayNum;
        } else if (compSign.equalsIgnoreCase("<")) {
            bFlag = dODDayNum < compODDayNum;
        } else if (compSign.equalsIgnoreCase("<=")) {
            bFlag = dODDayNum <= compODDayNum;
        }
        if (bFlag) {
            hmResult = new HashMap();
            sKey = YssFun.formatDate(dDate, "yyyyMMdd") + "\f" +
                this.portCode +
                "\f" + compIndex.getIndexTempCode() + "\f" +
                compIndex.getIndexCode();

            hmResult.put(sKey, compIndex);
        }
        return hmResult;
    }

    //获取某个监控指标的监控条件，根据监控类型、属性类型
    protected ArrayList getCompConds(CompIndexBean compIndex, int iAttrType,
                                     int iCompType) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        ArrayList alCompConds = new ArrayList();
        CompIndexCondBean compCond = null;
        try {
            strSql = "select a.*, b.FCompAttrName as FAttrName, c.FCompAttrName as FDenominaAttrName," +
                " d.FCreatorName, e.FCheckUserName from " +
                pub.yssGetTableName("Tb_Comp_IndexCondition");

            strSql = strSql + " a left join (select FCompAttrCode,FCompAttrName from " +
                pub.yssGetTableName("Tb_Comp_Attr") +
                ") b on a.FAttr = b.FCompAttrCode" +
                " left join (select FCompAttrCode,FCompAttrName from " +
                pub.yssGetTableName("Tb_Comp_Attr") +
                ") c on a.FDenominaAttr = c.FCompAttrCode";
            strSql = strSql + " left join (select FUserCode,FUserName as FCreatorName from Tb_Sys_UserList) d " +
                " on a.FCreator = d.FUserCode left join (select FUserCode,FUserName as FCheckUserName from Tb_Sys_UserList) e " +
                " on a.FCheckUser = e.FUserCode ";

            strSql = strSql + " where a.FCheckState = 1 and a.FIndexTempCode = " +
                dbl.sqlString(compIndex.getIndexTempCode()) +
                (iAttrType == 99 ? "" :
                 " and a.FAttrType = " + String.valueOf(iAttrType)) +
                (iCompType == 99 ? "" : " and a.FCompType = "
                 + String.valueOf(iCompType));

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                compCond = new CompIndexCondBean();
                compCond.setCompIndexConditionAttr(rs);
                alCompConds.add(compCond);
            }
            return alCompConds;
        } catch (Exception e) {
            throw new YssException("获取监控指标的监控条件出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String buildFilterSql(ArrayList alConds) throws YssException {
        String sWhereSql = "";
        CompIndexCondBean cond = null;
        YssCompAttrParam attrParam = null;
        YssCompAttrParam attrDenParam = null;
        IOperValue operValue = null;
        YssCompValueParam initParam = null;
        String[] sValueAry = null;
        String sValue = "";
        double dblOperValue = 0;
        boolean blTemp = true;

        try {
            if (alConds.size() > 0) {
                sWhereSql = " where ";
            }
            for (int i = 0; i < alConds.size(); i++) {
                //生成where语句
                blTemp = true;
                cond = (CompIndexCondBean) alConds.get(i);
                cond.setYssPub(pub);
                attrParam = cond.getAttrParam();
                if (attrParam.getRangeType().equalsIgnoreCase("per")) {
                    if (!cond.getSign().equalsIgnoreCase("with same")) {

                        if (cond.getCompType() == 2 || cond.getCompType() == 3) {
                            attrDenParam = cond.getAttrDenParam();
                            operValue = (IOperValue) pub.getOperDealCtx().
                                getBean(attrDenParam.getBeanId());
                            operValue.setYssPub(pub);
                            initParam = new YssCompValueParam();
                            initParam.setDDate(dDate);
                            initParam.setPortCode(portCode);
                            initParam.setCuryCode(pub.getPortBaseCury(portCode));// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                            operValue.init(initParam);
                            dblOperValue = operValue.getOperDoubleValue();
                            if (dblOperValue != 0) {
                                sWhereSql += " " + attrParam.getField() + "/" +
                                    dblOperValue + " " +
                                    cond.getSign() + " ";
                            } else {
                                sWhereSql += " 1 = 2 ";
                                blTemp = true;
                            }
                        } else {
                            sWhereSql += " " + attrParam.getField() + " " +
                                cond.getSign() + " ";
                        }

                        if (blTemp) {
                            if (cond.getSign().indexOf("in") >= 0) {
                                sValueAry = cond.getValue().split(",");
                                sValue = "";
                                for (int j = 0; j < sValueAry.length; j++) {
                                    sValue += "'" + sValueAry[j] + "'";
                                    if (j < sValueAry.length - 1) {
                                        sValue += ",";
                                    }
                                }
                                if (sValue.length() == 0) {
                                    sValue = "' '";
                                }
                                sWhereSql += " (" + sValue + ") ";
                            } else if (cond.getSign().equalsIgnoreCase("between")) {
                                sValueAry = cond.getValue().split(",");
                                if (sValueAry.length == 1) {
                                    sWhereSql += " " + sqlValue(attrParam.getDataType(),
                                        sValueAry[0]) + " and " +
                                        sqlValue(attrParam.getDataType(),
                                                 sValueAry[0]) + " ";
                                } else {
                                    sWhereSql += " " + sqlValue(attrParam.getDataType(),
                                        sValueAry[0]) + " and " +
                                        sqlValue(attrParam.getDataType(),
                                                 sValueAry[1]) + " ";
                                }
                            } else {
                                sWhereSql += " " + sqlValue(attrParam.getDataType(),
                                    cond.getValue()) + " ";
                            }
                        }

                        if (cond.getConRela().equalsIgnoreCase("end")) {
                            break;
                        }

                        if (i < alConds.size() - 1 &&
                            !cond.getConRela().equalsIgnoreCase("end")) {
                            sWhereSql += " " + cond.getConRela() + " ";
                        }
                    }
//               else {
//                  sWhereSql += " 1 = 1 ";
//
//                  if (cond.getConRela().equalsIgnoreCase("end")) {
//                     break;
//                  }
//
//                  if (i < alConds.size() - 1 &&
//                      !cond.getConRela().equalsIgnoreCase("end")) {
//                     sWhereSql += " " + cond.getConRela() + " ";
//                  }
//               }
                } else if (attrParam.getRangeType().equalsIgnoreCase("odday")) {
                    compSign = cond.getSign();
                    compODDayNum = YssFun.toNumber(cond.getValue());
                }
            }
            if (sWhereSql.equalsIgnoreCase(" where ")) {
                sWhereSql = "";
            }
            return sWhereSql;
        } catch (Exception e) {
            throw new YssException("设置监控条件SQL语句出错！ \n" + e.getMessage());
        }
    }

    protected String sqlValue(int iDataType, String sValue) {
        String reStr = "' '";
        if (iDataType == 0) {
            reStr = dbl.sqlString(sValue);
        } else if (iDataType == 1) {
            reStr = sValue;
        } else if (iDataType == 2) {
            reStr = sValue.replaceAll(",", "");
        } else if (iDataType == 3) {
            reStr = dbl.sqlDate(sValue);
        } else if (iDataType == 4) {
            reStr = sValue;
        }

        return reStr;
    }

    //获取债券的剩余期限
    //计算方式：
    //成本1*到期天数1(到期日期－当天日期)＋成本2*到期天数2(到期日期－当天日期)＋...../成本1＋成本2...
    protected String getFIDetailOD(String sWhereSql) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int iODDayNum = 0;
        double haveBaseMoney = 0;
        double dBaseRate = 0;

        try {
            strSql = " select * from (" +
                " select ck.*," +
                dbl.sqlIsNull("ck.FSecurityCode", "t.FSecurityCode") +
                " as FHSecurityCode," +
                dbl.sqlIsNull("ck.FPortCode", "t.FPortCode") + " as FHPortCode," +
                dbl.sqlIsNull("ck.FAnalysisCode1", "t.FInvMgrCode") +
                "  as FHInvMgrCode," +
                dbl.sqlIsNull("ck.FAnalysisCode2", "t.FBrokerCode") +
                "  as FHBrokerCode," +
                " ' ' as FHCatCode," +
                " (" + dbl.sqlIsNull("ck.FStorageAmount", "0") + " + " +
                dbl.sqlIsNull("t.FTradeAmount", "0") + ") as FHaveAmount," +
                " (" + dbl.sqlIsNull("ck.FStorageCost", "0") + " + " +
                dbl.sqlIsNull("t.FCost", "0") + ") as FHaveCost," +
                " (" + dbl.sqlIsNull("ck.FMStorageCost", "0") + " + " +
                dbl.sqlIsNull("t.FMCost", "0") + ") as FHaveMCost," +
                " (" + dbl.sqlIsNull("ck.FVStorageCost", "0") + " + " +
                dbl.sqlIsNull("t.FVCost", "0") + ") as FHaveVCost," +
                " (" + dbl.sqlIsNull("ck.FBaseCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FBaseCuryCost", "0") + ") as FHaveBaseCost," +
                " (" + dbl.sqlIsNull("ck.FMBaseCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FMBaseCuryCost", "0") + ") as FHaveMBaseCost," +
                " (" + dbl.sqlIsNull("ck.FVBaseCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FVBaseCuryCost", "0") + ") as FHaveVBaseCost," +
                " (" + dbl.sqlIsNull("ck.FPortCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FPortCuryCost", "0") + ") as FHavePortCost," +
                " (" + dbl.sqlIsNull("ck.FMPortCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FMPortCuryCost", "0") + ") as FHaveMPortCost," +
                " (" + dbl.sqlIsNull("ck.FVPortCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FVPortCuryCost", "0") + ") as FHaveVPortCost," +
                " 'Sec' as FHaveType from " +
                //----------------------------------------------------------------------------------
                " (select FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode," +
                " sum(FTradeAmount*FAmountInd) as FTradeAmount," +
                " sum(FAccruedinterest*FAmountInd) as FAccruedinterest, " +
                " sum(FCost*FAmountInd) as FCost, sum(FMCost*FAmountInd) as FMCost, sum(FVCost*FAmountInd) as FVCost," +
                " sum(FBaseCuryCost*FAmountInd) as FBaseCuryCost, sum(FMBaseCuryCost*FAmountInd) as FMBaseCuryCost,sum(FVBaseCuryCost*FAmountInd) as FVBaseCuryCost," +
                " sum(FPortCuryCost*FAmountInd) as FPortCuryCost, sum(FMPortCuryCost*FAmountInd) as FMPortCuryCost,sum(FVPortCuryCost*FAmountInd) as FVPortCuryCost from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " a left join (select * from Tb_Base_TradeType where FCheckState = 1) b " +
                " on a.FTradeTypeCode = b.FTradeTypeCode" +
                //------------------------------------------------------------------
                " where FBargainDate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and (a.FCheckState = 1 or (a.FCheckState = 3 and a.FCreator  = "
                + dbl.sqlString(pub.getUserCode()) + "))" +
                " group by FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode) t" +
                " full join " +
                //------------------------------------------------------------------
                " (select FSecurityCode,FPortCode,FStorageAmount,FStorageCost,FMStorageCost," +
                " FVStorageCost,FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FBaseCuryCost," +
                " FMBaseCuryCost,FVBaseCuryCost,FAnalysisCode1," +
                " FAnalysisCode2,FAnalysisCode3" +
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FPortCode = " + dbl.sqlString(portCode) +
                " and " + operSql.sqlStoragEve(dDate) +
                " and FCheckState = 1 )ck " +
                //------------------------------------------------------------------
                " on ck.FSecurityCode = t.FSecurityCode and " +
                " ck.FPortCode = t.FPortCode and ck.FAnalysisCode1 = t.FInvMgrCode" +
                " and ck.FAnalysisCode1 = t.FBrokerCode) k" +
                //------------------------------------------------------------------
                " join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 " +
                " group by FSecurityCode) ea " +
                " join (select FSecurityCode as FSecurityCode2, FStartDate, FTradeCury as FCuryCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCatCode = 'FI') eb " +
                " on ea.FSecurityCode = eb.FSecurityCode2 and ea.FStartDate = eb.FStartDate) e " +
                " on k.FHSecurityCode = e.FSecurityCode2 " +
                //------------------------------------------------------------------
                " join (select FSecurityCode as FSecurityCode3,FInsCashDate from " +
                pub.yssGetTableName("Tb_Para_FixInterest") +
                " where FCheckState = 1) f " +
                " on k.FHSecurityCode = f.FSecurityCode3 " +
                sWhereSql;
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FCuryCode"),
                    this.portCode, YssOperCons.YSS_RATE_BASE);

                haveBaseMoney = getSettingOper().calBaseMoney(rs.
                    getDouble("FHaveBaseCost"), dBaseRate);

                //      haveBaseMoney = YssD.mul(rs.getDouble("FHaveBaseCost"),
                //                             dBaseRate);
                iODDayNum = YssFun.dateDiff(dDate, rs.getDate("FInsCashDate"));
                this.sumHaveBaseMoney = YssD.add(sumHaveBaseMoney, haveBaseMoney);
                this.sumHaveBaseMoneyOD = YssD.add(YssD.mul(haveBaseMoney, iODDayNum),
                    sumHaveBaseMoneyOD);
            }
            return "";
        } catch (Exception e) {
            throw new YssException("获取债券的剩余期限出错\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取帐户的剩余期限
    //计算方式：
    //帐户持有金额1*到期天数1(到期日期－当天日期)＋帐户持有金额2*到期天数2(到期日期－当天日期)＋...../帐户持有金额1＋帐户持有金额2...
    protected String getAccDetailOD(String sWhereSql) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int iODDayNum = 0;
        double haveBaseMoney = 0;
        double dBaseRate = 0;
        try {
            strSql = " select k.*,e.* from (" +
                " select ck.*, " +
                dbl.sqlIsNull("ck.FCashAccCode", "t.FCashAccCode") +
                " as FHCashAccCode," +
                dbl.sqlIsNull("ck.FPortCode", "t.FPortCode") + " as FHPortCode," +
                dbl.sqlIsNull("ck.FAnalysisCode1", "t.FAnalysisCode1") +
                "  as FHInvMgrCode," +
                " ' ' as FHBrokerCode," +
                dbl.sqlIsNull("ck.FAnalysisCode2", "t.FAnalysisCode2") +
                "  as FHCatCode," +
                " 0 as FHaveAmount," +
                " (" + dbl.sqlIsNull("ck.FAccBalance", "0") + "+" +
                dbl.sqlIsNull("t.FMoney", "0") + ") as FHaveCost," +
                " 0 as FHaveMCost," +
                " 0 as FHaveVCost," +
                " (" + dbl.sqlIsNull("ck.FBaseCuryBal", "0") + "+" +
                dbl.sqlIsNull("t.FBaseMoney", "0") + ") as FHaveBaseCost," +
                " 0 as FHaveMBaseCost," +
                " 0 as FHaveVBaseCost," +
                " (" + dbl.sqlIsNull("ck.FPortCuryBal", "0") + "+" +
                dbl.sqlIsNull("t.FPortMoney", "0") + ") as FHavePortCost," +
                " 0 as FHaveMPortCost," +
                " 0 as FHaveVPortCost" +
                "  from " +
                //----------------------------------------------------------------------------------
                " (select FCashAccCode,FPortCode,FAnalysisCode1,FAnalysisCode2," +
                " sum(FMoney*FInOut) as FMoney,sum(FMoney*FBaseCuryRate*FInOut) as FBaseMoney," +
                " sum(FMoney*FBaseCuryRate/FPortCuryRate*FInOut) as FPortMoney from " +
                " (select a.FTransDate, a.FTransferDate, b.* from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " a join " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " b on a.FNum = b.FNum where (" + dbl.sqlDate(dDate) +
                " between FTransDate and FTransferDate) " +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and (a.FCheckState = 1 or (a.FCheckState = 3 and a.FCreator = " +
                dbl.sqlString(pub.getUserCode()) + ")))" + //取可用头寸
                " group by FCashAccCode,FPortCode,FAnalysisCode1,FAnalysisCode2) t full join" +
                //------------------------------------------------------------------
                " (select FCashAccCode,FPortCode,FAccBalance," +
                " FPortCuryBal,FBaseCuryBal,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3" +
                " from " + pub.yssGetTableName("Tb_Stock_Cash") +
                " where FPortCode = " + dbl.sqlString(portCode) +
                " and FCheckState = 1" +
                " and " + operSql.sqlStoragEve(dDate) +
                " )ck " +
                //------------------------------------------------------------------
                " on ck.FCashAccCode = t.FCashAccCode and " +
                " ck.FPortCode = t.FPortCode and ck.FAnalysisCode1 = t.FAnalysisCode1" +
                " and ck.FAnalysisCode2 = t.FAnalysisCode2) k" +
                //------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    
                
                " join (select FCashAccCode as FCashAccCode2,FMatureDate," +
                " FCuryCode, FAccType,FSubAccType from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where  FCheckState = 1 and  FAccType in (" + this.operSql.sqlCodes(accTypes) + ")) e " +

                //end by lidaolong
                " on k.FHCashAccCode = e.FCashAccCode2" + 
                sWhereSql;

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dBaseRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FCuryCode"),
                    this.portCode, YssOperCons.YSS_RATE_BASE);
                haveBaseMoney = YssD.mul(rs.getDouble("FHaveCost"), dBaseRate);
                if ( (rs.getString("FSubAccType") + "").equalsIgnoreCase("0101")) { //活期存款剩余期限按1天计算
                    iODDayNum = 1;
                } else {
                    if (rs.getDate("FMatureDate") != null) {
                        iODDayNum = YssFun.dateDiff(dDate, rs.getDate("FMatureDate"));
                    } else {
                        iODDayNum = 0;
                    }
                }
                this.sumHaveBaseMoney = YssD.add(sumHaveBaseMoney, haveBaseMoney);
                this.sumHaveBaseMoneyOD = YssD.add(YssD.mul(haveBaseMoney, iODDayNum),
                    sumHaveBaseMoneyOD);
            }
            return "";
        } catch (Exception e) {
            throw new YssException("获取帐户的剩余期限出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * init
     *
     * @param bean BaseBean
     */

    public void init(BaseBean bean) {
        YssCompDeal cd = (YssCompDeal) bean;
        this.portCode = cd.getPortCode();
        this.dDate = cd.getDDate();
        this.secTypes = cd.getSecTypes();
        this.accTypes = cd.getAccTypes();
        this.compIndex = cd.getCompIndex();
    }

    /**
     * buildReport
     *
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) {
        return "";
    }

//   protected String

    /**
     * initBuildReport
     *
     * @param bean BaseBean
     */
    public void initBuildReport(BaseBean bean) {
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
    
    //add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
    public String checkReportBeforeSearch(String sReportType){
    	return "";
    }
}
