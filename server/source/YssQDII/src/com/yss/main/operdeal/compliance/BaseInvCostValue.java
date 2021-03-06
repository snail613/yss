package com.yss.main.operdeal.compliance;

import java.sql.*;

import com.yss.base.*;
import com.yss.pojo.param.comp.*;
import com.yss.util.*;

public class BaseInvCostValue
    extends BaseAPOperValue {
    private java.util.Date dDate;
    private String portCode;
    private String curyCode;
    private String secTypes;
    private String cashTypes;

    public BaseInvCostValue() {
    }

    /**
     * getOperDoubleValue
     *
     * @return double
     */
    public double getOperDoubleValue() throws YssException {
        double dResult = 0;
        if (secTypes.length() > 0) {
            dResult = getSecInvCost(secTypes);
        }
        if (cashTypes.length() > 0) {
            dResult += getCashInvCost(cashTypes);
        }
        return dResult;
    }

    protected double getCashInvCost(String sCashTypes) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dResult = 0;
        try {

            strSql = " select * from (" +
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
                " 0 as FHaveVPortCost," +
                " 'Cash' as FHaveType from " +
                //------------------------------------------------------------------
                " (select FCashAccCode,FPortCode,FAnalysisCode1,FAnalysisCode2," +
                " sum(FMoney*FInOut) as FMoney,sum(FMoney*FBaseCuryRate*FInOut) as FBaseMoney," +
                " sum(FMoney*FBaseCuryRate/FPortCuryRate*FInOut) as FPortMoney from " +
                " (select a.FTransDate, a.FTransferDate, b.* from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " a join " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " b on a.FNum = b.FNum where (a.FCheckState = 1 or (a.FCheckState = 3 and a.FCreator = " +
                dbl.sqlString(pub.getUserCode()) + ")) and (" +
                " FTransferDate = " + dbl.sqlDate(dDate) +
                " )and FPortCode = " + dbl.sqlString(portCode) +
                " ) group by FCashAccCode,FPortCode,FAnalysisCode1,FAnalysisCode2)" +
                " t full join " +
                //------------------------------------------------------------------
                " (select FCashAccCode,FPortCode,FAccBalance," +
                " FPortCuryBal,FBaseCuryBal,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3" +
                " from " + pub.yssGetTableName("Tb_Stock_Cash") +
                " where FPortCode = " + dbl.sqlString(portCode) +
                " and FCheckState = 1" +
                " and " + operSql.sqlStoragEve(dDate) +
                " ) ck on ck.FCashAccCode = t.FCashAccCode and " +
                " ck.FPortCode = t.FPortCode and ck.FAnalysisCode1 = t.FAnalysisCode1" +
                " and ck.FAnalysisCode2 = t.FAnalysisCode2) k" +
                //------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                
                " join (select FCashAccCode as FCashAccCode2, FAccType from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where  FCheckState = 1 and  FAccType in (" + this.operSql.sqlCodes(sCashTypes) +
                ")) e " +
                                
                //end by lidaolong 
                " on k.FHCashAccCode = e.FCashAccCode2";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dResult = YssD.add(dResult, rs.getDouble("FHaveBaseCost"));
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected double getSecInvCost(String sSecTypes) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double FStorageCost = 0; //货币核算成本
        double FMStorageCost = 0; //管理成本
        double FVStorageCost = 0; //估值成本
        double FPortCuryCost = 0; //组合货币核算成本
        double FMPortCuryCost = 0; //组合基础货币管理成本
        double FVPortCuryCost = 0; //组合基础货币估值成本
        double FBaseCuryCost = 0; //基础货币核算成本
        double FMBaseCuryCost = 0; //基础货币管理成本
        double FVBaseCuryCost = 0; //基础货币估值成本

        try {
            strSql = "select a.*,b.* from " +
                "(select a1.* from (select sum(FCost*FAmountInd) as FTeCost,sum(FMCost*FAmountInd) as FTeMCost," +
                "sum(FVCost*FAmountInd) as FTeVCost,sum(FBaseCuryCost*FAmountInd) as FTeBaseCuryCost," +
                "sum(FMBaseCuryCost*FAmountInd) as FTeMBaseCuryCost,sum(FVBaseCuryCost*FAmountInd) as FTeVBaseCuryCost," +
                "sum(FPortCuryCost*FAmountInd) as FTePortCuryCost,sum(FMPortCuryCost*FAmountInd) as FTeMPortCuryCost," +
                "sum(FVPortCuryCost*FAmountInd) as FTeVPortCuryCost,FSecurityCode,FInvMgrCode,FBrokerCode,FPortCode from " +
                pub.yssGetTableName("tb_data_subtrade") + " b1 " +
                " left join (select * from Tb_Base_TradeType) b2 on b1.ftradetypecode = b2.ftradetypecode " +
                " where FBargainDate = " + dbl.sqlDate(dDate) +
                " and FPortCode =" + dbl.sqlString(portCode) +
                " and (b1.FCheckState = 1 or (b1.FCheckState = 3 and b1.FCreator = " +
                dbl.sqlString(pub.getUserCode()) +
                ")) group by FPortCode,FSecurityCode,FBrokerCode,FInvMgrCode) a1 join " +
                " (select FSecurityCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1 and FCatCode in (" +
                operSql.sqlCodes(sSecTypes) +
                " )) a2 on a1.FSecurityCode = a2.FSecurityCode)" +
                " a full join " +
                "(select b1.* from (select FSecurityCode, FStorageCost,FMStorageCost,FVStorageCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost," +
                "FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FPortCode,FAnalysisCode1,FAnalysisCode2 from " +
                pub.yssGetTableName("tb_stock_security") +
                " where " + operSql.sqlStoragEve(dDate) +
                " and FPortCode =" + dbl.sqlString(portCode) +
                " ) b1 join (select FSecurityCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1 and FCatCode in (" +
                operSql.sqlCodes(sSecTypes) +
                " )) b2 on b1.FSecurityCode = b2.FSecurityCode) b" +
                " on a.FSecurityCode = b.FSecurityCode and a.Fportcode = b.FPortcode " +
                " and a.FInvmgrCode = b.FAnalysisCode1 and a.FBrokerCode = b.FAnalysisCode2";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                FStorageCost = YssD.add(YssD.add(FStorageCost,
                                                 rs.getDouble("FStorageCost")),
                                        rs.getDouble("FTeCost"));
                FMStorageCost = YssD.add(YssD.add(FMStorageCost,
                                                  rs.getDouble("FMStorageCost")),
                                         rs.getDouble("FTeMCost"));
                FVStorageCost = YssD.add(YssD.add(FVStorageCost,
                                                  rs.getDouble("FVStorageCost")),
                                         rs.getDouble("FTeVCost"));
                FPortCuryCost = YssD.add(YssD.add(FPortCuryCost,
                                                  rs.getDouble("FPortCuryCost")),
                                         rs.getDouble("FTePortCuryCost"));
                FMPortCuryCost = YssD.add(YssD.add(FMPortCuryCost,
                    rs.getDouble("FMPortCuryCost")),
                                          rs.getDouble("FTeMPortCuryCost"));
                FVPortCuryCost = YssD.add(YssD.add(FVPortCuryCost,
                    rs.getDouble("FVPortCuryCost")),
                                          rs.getDouble("FTeVPortCuryCost"));
                FBaseCuryCost = YssD.add(YssD.add(FBaseCuryCost,
                                                  rs.getDouble("FBaseCuryCost")),
                                         rs.getDouble("FTeBaseCuryCost"));
                FMBaseCuryCost = YssD.add(YssD.add(FMBaseCuryCost,
                    rs.getDouble("FMBaseCuryCost")),
                                          rs.getDouble("FTeMBaseCuryCost"));
                FVBaseCuryCost = YssD.add(YssD.add(FVBaseCuryCost,
                    rs.getDouble("FVBaseCuryCost")),
                                          rs.getDouble("FTeVBaseCuryCost"));
            }
            return FBaseCuryCost;
        } catch (Exception e) {
            throw new YssException("取成本出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * init
     *
     * @param bean BaseBean
     */
    public void init(Object bean) {
        YssCompValueParam ov = (YssCompValueParam) bean;
        this.dDate = ov.getDDate();
        this.portCode = ov.getPortCode();
        this.curyCode = ov.getCuryCode();
        this.secTypes = ov.getSecTypes();
        this.cashTypes = ov.getCashTypes();
    }

    /**
     * invokeOperMothed
     *
     * @return Object
     */
    public Object invokeOperMothed() {
        return "";
    }

    /**
     * getOperStrValue
     *
     * @return String
     */
    public String getOperStrValue() {
        return "";
    }
}
