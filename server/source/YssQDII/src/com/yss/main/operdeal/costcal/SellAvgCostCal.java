package com.yss.main.operdeal.costcal;

import java.sql.*;

import com.yss.pojo.cache.*;
import com.yss.util.*;

public class SellAvgCostCal
    extends BaseCostCal {
    public SellAvgCostCal() {
    }

    public YssCost getCarryCost() throws YssException {
        String strSql = "";
        ResultSet rs = null;
//      double dAmount = 0;
        YssCost cost = new YssCost();
        String sInvmgrField = "";
        String sBrokerField = "";
        String sErrInfo = "";
        boolean bFlagHV = false; //是否进行了估值成本和核算成本的计算
        boolean bFlag = false; //是否在当天有交易
        int iTmpNum = 0;
        int iNum = 0;

//      operSql = new YssDbOperSql();
        try {
//         sInvmgrField = this.getSettingOper().getStorageAnalysisField(
//               YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
//         sBrokerField = this.getSettingOper().getStorageAnalysisField(
//               YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker);
            //先取出前一日的库存成本和数量
            strSql =
                "select FSecurityCode, sum(FStorageAmount) as FStorageAmount, " +
                " sum(FStorageCost) as FStorageCost,sum(FMStorageCost) as FMStorageCost,sum(FVStorageCost) as FVStorageCost," +
                " sum(FPortCuryCost) as FPortCuryCost,sum(FMPortCuryCost) as FMPortCuryCost,sum(FVPortCuryCost) as FVPortCuryCost," +
                " sum(FBaseCuryCost) as FBaseCuryCost,sum(FMBaseCuryCost) as FMBaseCuryCost,sum(FVBaseCuryCost) as FVBaseCuryCost, " +
                " 'M' as FCostType from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " where FSecurityCode = " + dbl.sqlString(securityCode) +
                " and " +
                operSql.sqlStoragEve(date) + " and FPortCode = " +
                dbl.sqlString(portCode);
            if (analysisCode1.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1 = " +
                    dbl.sqlString(analysisCode1);
            }
            if (analysisCode2.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2 = " +
                    dbl.sqlString(analysisCode2);
            }
            if (analysisCode3.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode3 = " +
                    dbl.sqlString(analysisCode3);
            }

            strSql = strSql + " and FCheckState = 1" +
                " group by FSecurityCode,'M' union " +
                //---------------------------------------------------------------
                "select FSecurityCode, sum(FStorageAmount) as FStorageAmount, " +
                " sum(FStorageCost) as FStorageCost,sum(FMStorageCost) as FMStorageCost,sum(FVStorageCost) as FVStorageCost," +
                " sum(FPortCuryCost) as FPortCuryCost,sum(FMPortCuryCost) as FMPortCuryCost,sum(FVPortCuryCost) as FVPortCuryCost," +
                " sum(FBaseCuryCost) as FBaseCuryCost,sum(FMBaseCuryCost) as FMBaseCuryCost,sum(FVBaseCuryCost) as FVBaseCuryCost, " +
                " 'HV' as FCostType from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " where FSecurityCode = " + dbl.sqlString(securityCode) +
                " and " +
                operSql.sqlStoragEve(date) + " and FPortCode = " +
                dbl.sqlString(portCode) +
                " and FCheckState = 1 group by FSecurityCode,'HV' ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FCostType").equalsIgnoreCase("M")) {
                    cost.setMAmount(rs.getDouble("FStorageAmount"));
                    cost.setMCost(rs.getDouble("FMStorageCost"));
                    cost.setBaseMCost(rs.getDouble("FMBaseCuryCost"));
                    cost.setPortMCost(rs.getDouble("FMPortCuryCost"));
                } else if (rs.getString("FCostType").equalsIgnoreCase("HV")) {
                    cost.setAmount(rs.getDouble("FStorageAmount"));
                    cost.setCost(rs.getDouble("FStorageCost"));
                    cost.setVCost(rs.getDouble("FVStorageCost"));
                    cost.setBaseCost(rs.getDouble("FBaseCuryCost"));
                    cost.setBaseVCost(rs.getDouble("FVBaseCuryCost"));
                    cost.setPortCost(rs.getDouble("FPortCuryCost"));
                    cost.setPortVCost(rs.getDouble("FVPortCuryCost"));
                }
            }
            dbl.closeResultSetFinal(rs);

            iNum = YssFun.toInt(YssFun.right(tradeNum, 6));
            sInvmgrField = this.getSettingOper().chooseAnalyVar(YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr, analysisCode1, analysisCode2, analysisCode3);
            sBrokerField = this.getSettingOper().chooseAnalyVar(YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker, analysisCode1, analysisCode2, analysisCode3);
            //取出当天交易的成本和数量
            strSql = "select FNum,FSecurityCode,a.FTradeTypeCode,(FTradeAmount*FAmountInd) as FTradeAmount," +
                " 0 as FCost, 0 as FBaseCost, 0 as FPortCost, " +
                " (FMCost*FAmountInd) as FMCost," +
                " (FMBaseCuryCost*FAmountInd) as FMBaseCost, (FMPortCuryCost*FAmountInd) as FMPortCost," +
                " 0 as FVCost, 0 as FVBaseCost, 0 as FVPortCost," +
                " FBaseCuryRate,FPortCuryRate,'M' as FCostType" +
                " from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " a left join " +
                " (select FTradeTypeCode,FAmountInd from Tb_Base_TradeType) b on a.FTradeTypeCode = b.FTradeTypeCode" +
                " where FSecurityCode = " + dbl.sqlString(securityCode) +
                " and FBargainDate = " + dbl.sqlDate(date);
//               " and FInvMgrCode = " + dbl.sqlString(invmgrCode) +
//               " and FBrokerCode = " + dbl.sqlString(brokerCode) +
            if (sInvmgrField.trim().length() > 0) {
                strSql = strSql + " and FInvMgrCode = " +
                    dbl.sqlString(this.getSettingOper().chooseAnalyVar(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr, analysisCode1, analysisCode2, analysisCode3));
            }
            if (sBrokerField.trim().length() > 0) {
                strSql = strSql + " and FBrokerCode = " +
                    dbl.sqlString(this.getSettingOper().chooseAnalyVar(
                        YssOperCons.YSS_KCLX_Security, YssOperCons.YSS_KCPZ_Broker, analysisCode1, analysisCode2, analysisCode3));
            }

            strSql = strSql + " and FPortCode = " + dbl.sqlString(portCode) +
                " and FCheckState = 1 union " +
                //---------------------------------------------------------------
                "select FNum,FSecurityCode,a.FTradeTypeCode,(FTradeAmount*FAmountInd) as FTradeAmount,(FCost*FAmountInd) as FCost," +
                " (FBaseCuryCost*FAmountInd) as FBaseCost, (FPortCuryCost*FAmountInd) as FPortCost," +
                " 0 as FMCost, 0 as FMBaseCost, 0 as FMPortCost," +
                " (FVCost*FAmountInd) as FVCost,(FVBaseCuryCost*FAmountInd) as FVBaseCost," +
                " (FVPortCuryCost*FAmountInd) as FVPortCost," +
                " FBaseCuryRate,FPortCuryRate,'HV' as FCostType" +
                " from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " a left join " +
                " (select FTradeTypeCode,FAmountInd from Tb_Base_TradeType) b on a.FTradeTypeCode = b.FTradeTypeCode" +
                " where FSecurityCode = " + dbl.sqlString(securityCode) +
                " and FBargainDate = " + dbl.sqlDate(date) +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and FCheckState = 1 order by FCostType,FNum";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
//            if (rs.getString("FSecurityCode").equalsIgnoreCase("2328")){
//               bFlag = true;
//            }
                iTmpNum = this.getTradeNum(rs.getString("FNum"));
//            bFlag = true;
                //核算成本，估值成本
                if (rs.getString("FCostType").equalsIgnoreCase("HV")) {
                    if (bFlagHV) {
                        continue;
                    }
//               if (rs.getString("FNum").equalsIgnoreCase(tradeNum)) {
                    if (iTmpNum >= iNum) {
                        //计算成本
                        bFlagHV = true;
                        if (cost.getAmount() != 0) {

                            cost.setCost(YssD.mul(YssD.div(cost.getCost(),
                                cost.getAmount()), amount));
                            cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(),
                                cost.getAmount()), amount));
                            cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(),
                                cost.getAmount()), amount));
                            cost.setVCost(YssD.mul(YssD.div(cost.getVCost(),
                                cost.getAmount()), amount));
                            cost.setBaseVCost(YssD.mul(YssD.div(cost.getBaseVCost(),
                                cost.getAmount()), amount));
                            cost.setPortVCost(YssD.mul(YssD.div(cost.getPortVCost(),
                                cost.getAmount()), amount));
                        }
                    } else {
                        //累计当天的成本和数量
                        cost.setAmount(YssD.add(cost.getAmount(),
                                                rs.getDouble("FTradeAmount")));
                        cost.setCost(YssD.add(cost.getCost(),
                                              rs.getDouble("FCost")));
                        cost.setVCost(YssD.add(cost.getVCost(),
                                               rs.getDouble("FVCost")));
                        cost.setBaseCost(YssD.add(cost.getBaseCost(),
                                                  rs.getDouble("FBaseCost")));
                        cost.setBaseVCost(YssD.add(cost.getBaseVCost(),
                            rs.getDouble("FVBaseCost")));
                        //组合货币金额＝交易金额*基础汇率/组合汇率
                        cost.setPortCost(YssD.add(cost.getPortCost(),
                                                  rs.getDouble("FPortCost")));
                        cost.setPortVCost(YssD.add(cost.getPortVCost(),
                            rs.getDouble("FVPortCost")));
                    }
                } else if (rs.getString("FCostType").equalsIgnoreCase("M")) { //管理成本
//               if (rs.getString("FNum").equalsIgnoreCase(tradeNum)) {
                    if (iTmpNum >= iNum) {
                        if (cost.getMAmount() != 0) {
                            cost.setMCost(YssD.mul(YssD.div(cost.getMCost(),
                                cost.getMAmount()), amount));
                            cost.setBaseMCost(YssD.mul(YssD.div(cost.getBaseMCost(),
                                cost.getMAmount()), amount));
                            cost.setPortMCost(YssD.mul(YssD.div(cost.getPortMCost(),
                                cost.getMAmount()), amount));
                        }
                        break;
                    } else {
                        cost.setMAmount(YssD.add(cost.getMAmount(),
                                                 rs.getDouble("FTradeAmount")));
                        cost.setMCost(YssD.add(cost.getMCost(), rs.getDouble("FMCost")));
                        cost.setBaseMCost(YssD.add(cost.getBaseMCost(),
                            rs.getDouble("FMBaseCost")));
                        cost.setPortMCost(YssD.add(cost.getPortMCost(),
                            rs.getDouble("FMPortCost")));

                    }
                }
            }
            if (!bFlagHV) {
                if (cost.getAmount() != 0) {

                    cost.setCost(YssD.mul(YssD.div(cost.getCost(),
                        cost.getAmount()), amount));
                    cost.setBaseCost(YssD.mul(YssD.div(cost.getBaseCost(),
                        cost.getAmount()), amount));
                    cost.setPortCost(YssD.mul(YssD.div(cost.getPortCost(),
                        cost.getAmount()), amount));
                    cost.setVCost(YssD.mul(YssD.div(cost.getVCost(),
                        cost.getAmount()), amount));
                    cost.setBaseVCost(YssD.mul(YssD.div(cost.getBaseVCost(),
                        cost.getAmount()), amount));
                    cost.setPortVCost(YssD.mul(YssD.div(cost.getPortVCost(),
                        cost.getAmount()), amount));
                    cost.setMCost(YssD.mul(YssD.div(cost.getMCost(), cost.getAmount()),
                                           amount));
                    cost.setBaseMCost(YssD.mul(YssD.div(cost.getBaseMCost(),
                        cost.getAmount()), amount));
                    cost.setPortMCost(YssD.mul(YssD.div(cost.getPortMCost(),
                        cost.getAmount()), amount));

                }

            }
            /*
                      if (!bFlagHV) {
               sErrInfo = "\r\n没有找到交易编号为【" + tradeNum + "】，组合代码为【" + portCode +
                     "】的交易记录，无法计算核算成本和估值成本！";
               throw new YssException(sErrInfo);
                      }
                      if (!bFlagM) {
               sErrInfo = "\r\n没有找到交易编号为【" + tradeNum + "】，组合代码为【" + portCode +
                     "】，投资经理代码为【" + invmgrCode + "】的交易记录，无法计算管理成本！";
               throw new YssException(sErrInfo);
                      }
             */
            return cost;
        } catch (Exception e) {
            throw new YssException("获取结转成本出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private int getTradeNum(String sNum) {
//      String s = "";
        sNum = YssFun.left(sNum, 15);
        sNum = YssFun.right(sNum, 6);
        return YssFun.toInt(sNum);
    }

}
