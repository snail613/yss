package com.yss.vsub;

import java.sql.*;

import com.yss.dsub.*;
//MS00278 QDV4中保2009年02月24日03_B
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.storagemanage.*;
import com.yss.util.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.commeach.EachRateOper;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;//MS00278 QDV4中保2009年02月24日03_B

public class YssOperFun {

    protected YssPub pub = null; //全局变量
    protected DbBase dbl = null; //数据连接已经处理

    public YssOperFun(YssPub ysspub) {
        setYssPub(ysspub);
    }

    public void setYssPub(YssPub ysspub) {
        pub = ysspub;
        dbl = ysspub.getDbLink();
    }

    public java.util.Date getMaxStgEveDate(String sStgTabName,
                                           java.util.Date dDate) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        java.util.Date tmpDate = null;
        try {
            strSql = "select max(FStorageDate) as FStorageDate from " +
                pub.yssGetTableName(sStgTabName) +
                " where FStorageDate <= " +
                dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                " and FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                tmpDate = rs.getDate("FStorageDate");
            }
            if (tmpDate == null) { //sj 20071123 edit 若传入的日期前没有库存，则返回传入的日期，防止报空引用错误
                tmpDate = dDate;
            }
            return tmpDate;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public double getStorageCost(java.util.Date dDate, String sSecurityCode,
                                 String sPortCode, String sCostType,
                                 YssType lAmount) throws
        YssException {
        return getStorageCost(dDate, sSecurityCode, sPortCode, "",
                              "", "", sCostType, lAmount, "");

    }

    public double getStorageCost(java.util.Date dDate, String sSecurityCode,
                                 String sPortCode, String sAnalysisCode1,
                                 String sCostType, YssType lAmount) throws
        YssException {
        return getStorageCost(dDate, sSecurityCode, sPortCode, sAnalysisCode1,
                              "", "", sCostType, lAmount, "");

    }

    public double getStorageCost(java.util.Date dDate, String sSecurityCode,
                                 String sPortCode, String sAnalysisCode1,
                                 String sAnalysisCode2,
                                 String sCostType, YssType lAmount) throws
        YssException {
        return getStorageCost(dDate, sSecurityCode, sPortCode, sAnalysisCode1,
                              sAnalysisCode2, "", sCostType, lAmount, "");

    }

    public double getStorageCost(java.util.Date dDate, String sSecurityCode,
                                 String sPortCode, String sAnalysisCode1,
                                 String sAnalysisCode2,
                                 String sAnalysisCode3, String sCostType,
                                 YssType lAmount, String attrCode) throws
        YssException {
        String strSql = "", strError = "", strTmpSql = "";
      	String sInCostStr="";//合并太平版本代码
        ResultSet rs = null;
        double dReCost = 0;
        try {
            if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
                strTmpSql = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dDate, "yyyy") + "00'";
            } else {
                strTmpSql = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dDate, "yyyy") + "00'" +
                    " and FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1));
            }
            if (sCostType.equalsIgnoreCase("M")) {
                strSql = "sum(" + dbl.sqlIsNull("FMStorageCost", "0") +
                    ") as FStorageCost";
            	sInCostStr=" sum(FMExCost ) as FStorageCost";//by leeyu 20100510 QDV4中保2010年5月8日02_B //合并太平版本代码
            } else if (sCostType.equalsIgnoreCase("V")) {
                strSql = "sum(" + dbl.sqlIsNull("FVStorageCost", "0") +
                    ") as FStorageCost";
            	sInCostStr=" sum(FVExCost ) as FStorageCost";//by leeyu 20100510 QDV4中保2010年5月8日02_B //合并太平版本代码
            } else {
                strSql = "sum(" + dbl.sqlIsNull("FStorageCost", "0") +
                    ") as FStorageCost";
            	sInCostStr=" sum(FExchangeCost ) as FStorageCost";//by leeyu 20100510 QDV4中保2010年5月8日02_B //合并太平版本代码
            }
            strSql = "select sum(FStorageAmount) as FSumStorageAmount, sum(FStorageCost) as FSumStorageCost from ( select sum(" +
                dbl.sqlIsNull("a.FStorageAmount", "0") +
                ") as FStorageAmount, " + strSql + " from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " a where FCheckState = 1 and fportcode='" + sPortCode +
                "' and FSecurityCode ='" + sSecurityCode + "' " + strTmpSql;

            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1='" + sAnalysisCode1 + "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2='" + sAnalysisCode2 + "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode3='" + sAnalysisCode3 + "'";
            }
            if (attrCode != null && attrCode.trim().length() > 0) {
                strSql = strSql + " and FAttrClsCode ='" + attrCode + "'";
            }

            strSql = strSql +
                " union all select sum( m.FTradeAmount * n.FAmountInd) as FStorageAmount," +
                " sum( m.FTotalCost*n.FCashInd ) as FStorageCost " +
                "from(select fTradeTypeCode, FTradeAmount, FTotalCost from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where fcheckstate=1 and fportcode='" + sPortCode +
                "' and FSecurityCode ='" + sSecurityCode + "' and FBargainDate=" +
                dbl.sqlDate(dDate);
            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FInvMgrCode='" + sAnalysisCode1 + "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FBrokerCode='" + sAnalysisCode2 + "'";
            }
            //xuqiji 20090630:QDV4交银施罗德2009年4月9日02_B  MS00369 股票A更名为B后（综合业务中换股）股票B分红权益处理无法生成业务资料
            strSql = strSql +
                " ) m join (select FTradeTypeCode, FCashInd, FAmountInd from " +
                " Tb_Base_TradeType where FCheckState=1)n on m.FTradeTypeCode = n.FTradeTypeCode";
            strSql = strSql +
                " union all select sum(FAmount) as FStorageAmount," +
                //" sum(FExchangeCost) as FStorageCost" +
				sInCostStr+ //这里获取选择中的成本 合并太平版本代码
                " from (select FSecurityCode, FAmount, FExchangeCost,FMExCost,FVExCost,FInOutType" + //这里需将几个成本都传进去，便于任意获取 合并太平版本代码
                " from " + pub.yssGetTableName("Tb_Data_Integrated") +
                " where fcheckstate = 1 and FPortCode="+dbl.sqlString(sPortCode)+
                " and FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                " and FOperDate = " + dbl.sqlDate(dDate);
            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1='" + sAnalysisCode1 + "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2='" + sAnalysisCode2 + "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode3='" + sAnalysisCode3 + "'";
            }
            strSql = strSql + "))t1";
            //--------------------------------end--------------------------//
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReCost = YssFun.roundIt(rs.getDouble("FSumStorageCost"), 2);
                lAmount.setDouble(YssFun.roundIt(rs.getDouble("FSumStorageAmount"),
                                                 0));
            }

            return dReCost;
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public double getStorageCostTotal(java.util.Date dDate, String sSecurityCode,
                                      String sAnalysisCode1,
                                      String sAnalysisCode2,
                                      String sAnalysisCode3, String sCostType,
                                      YssType lAmount) throws YssException {
        return getStorageCostTotal(dDate, sSecurityCode,
                                   sAnalysisCode1, sAnalysisCode2, sAnalysisCode3,
                                   sCostType, lAmount, "");
    }

    public double getStorageCostTotal(java.util.Date dDate, String sSecurityCode,
                                      String sAnalysisCode1,
                                      String sAnalysisCode2,
                                      String sAnalysisCode3, String sCostType,
                                      YssType lAmount, String sPortCode) throws
        YssException {
        return getStorageCostTotal(dDate, sSecurityCode,
                                   sAnalysisCode1,
                                   sAnalysisCode2,
                                   sAnalysisCode3, sCostType,
                                   lAmount, sPortCode, "");
    }

    /**
     * 重载方法 添加所属分类代码 by leeyu 20090424 QDV4中保2009年04月21日01_B MS00397
     * @param dDate Date 业务日期
     * @param sSecurityCode String 证券代码
     * @param sAnalysisCode1 String 分析代码1
     * @param sAnalysisCode2 String 分析代码2
     * @param sAnalysisCode3 String 分析代码3
     * @param sCostType String 成本类型
     * @param lAmount YssType 返回的数量值类型
     * @param sPortCode String 组合列表，多个用","分隔
     * @param sAttrClsCode String 所属分类代码
     * @return double 成本
     * @throws YssException
     */
    public double getStorageCostTotal(java.util.Date dDate, String sSecurityCode,
                                      String sAnalysisCode1,
                                      String sAnalysisCode2,
                                      String sAnalysisCode3, String sCostType,
                                      YssType lAmount, String sPortCode, String sAttrClsCode) throws
        YssException {
        //20070617  fazmm 增加按照组合获取库存成本功能
        String strSql = "", strError = "", strTmpSql = "";
        ResultSet rs = null;
        double dReCost = 0;
        try {
            if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
                strTmpSql = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dDate, "yyyy") + "00'";
            } else {
                strTmpSql = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dDate, "yyyy") + "00'" +
                    " and FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1));
            }
            if (sPortCode.length() != 0) {
                //strTmpSql = strTmpSql + " and FPortCode ='" + sPortCode + "'";
                strTmpSql = strTmpSql + " and FPortCode in(" + sPortCode + ")"; //这里改成按多个组合取值 QDV4中保2009年04月21日01_B MS00397 by leeyu 20090422
            }
            //添加上所属分类代码 by leeyu QDV4中保2009年04月21日01_B MS00397
            if (sAttrClsCode != null && sAttrClsCode.length() != 0) {
                strTmpSql = strTmpSql = " and FAttrClsCode = " + dbl.sqlString(sAttrClsCode); ;
            }
            if (sCostType.equalsIgnoreCase("M")) {
                strSql = "sum(" + dbl.sqlIsNull("FMStorageCost", "0") +
                    ") as FStorageCost";
            } else if (sCostType.equalsIgnoreCase("V")) {
                strSql = "sum(" + dbl.sqlIsNull("FVStorageCost", "0") +
                    ") as FStorageCost";
            } else {
                strSql = "sum(" + dbl.sqlIsNull("FStorageCost", "0") +
                    ") as FStorageCost";
            }
            strSql = "select sum(FStorageAmount) as FSumStorageAmount, sum(FStorageCost) as FSumStorageCost from ( select sum(" +
                dbl.sqlIsNull("a.FStorageAmount", "0") +
                ") as FStorageAmount, " + strSql + " from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " a where FCheckState = 1 " +
                " and FSecurityCode ='" + sSecurityCode + "' " + strTmpSql;

            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1='" + sAnalysisCode1 + "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2='" + sAnalysisCode2 + "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode3='" + sAnalysisCode3 + "'";
            }

            strSql = strSql +
                " union all select sum( m.FTradeAmount * n.FAmountInd) as FStorageAmount," +
                " sum( m.FTotalCost*n.FCashInd ) as FStorageCost " +
                "from(select fTradeTypeCode, FTradeAmount, FTotalCost from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where fcheckstate=1 " +
                " and FSecurityCode ='" + sSecurityCode + "' and FBargainDate=" +
                dbl.sqlDate(dDate);
            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FInvMgrCode='" + sAnalysisCode1 + "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FBrokerCode='" + sAnalysisCode2 + "'";
            }
            if (sPortCode.length() != 0) {
                //strSql = strSql + " and FPortCode='" + sPortCode + "'";
                strSql = strSql + " and FPortCode in (" + sPortCode + ")"; //这里改成按多个组合取值 by leeyu QDV4中保2009年04月21日01_B MS00397 20090422
            }
            //添加上所属分类代码 by leeyu QDV4中保2009年04月21日01_B MS00397
            if (sAttrClsCode != null && sAttrClsCode.length() != 0) {
                strTmpSql = strTmpSql = " and FAttrClsCode = " + dbl.sqlString(sAttrClsCode); ;
            }
            //xuqiji 20090630:QDV4交银施罗德2009年4月9日02_B  MS00369 股票A更名为B后（综合业务中换股）股票B分红权益处理无法生成业务资料
            strSql = strSql +
                " ) m join (select FTradeTypeCode, FCashInd, FAmountInd from " +
                " Tb_Base_TradeType where FCheckState=1)n on m.FTradeTypeCode = n.FTradeTypeCode";
            strSql = strSql +
                " union all select sum(FAmount * FInOutType) as FStorageAmount," +
                " sum(FExchangeCost * FInOutType) as FStorageCost" +
                " from (select FSecurityCode, FAmount, FExchangeCost,FInOutType" +
                " from " + pub.yssGetTableName("Tb_Data_Integrated") +
                " where fcheckstate = 1 and FPortCode ="+dbl.sqlString(sPortCode)+
                " and FSecurityCode = " +dbl.sqlString(sSecurityCode) +
                " and FOperDate = " + dbl.sqlDate(dDate);
            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1='" + sAnalysisCode1 + "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2='" + sAnalysisCode2 + "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode3='" + sAnalysisCode3 + "'";
            }
            strSql = strSql + "))t1";
            //--------------------------------end--------------------------//
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReCost = YssFun.roundIt(rs.getDouble("FSumStorageCost"), 2);
                lAmount.setDouble(YssFun.roundIt(rs.getDouble("FSumStorageAmount"),
                                                 0));
            }
            return dReCost;
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public double getCashAccBalance(java.util.Date dDate, String sCashAccCode,
                                    String sPortCode) throws YssException {
        return getCashAccBalance(dDate, sCashAccCode, sPortCode, "", "", "", "");
    }

    public double getCashAccBalance(java.util.Date dDate, String sCashAccCode,
                                    String sPortCode, String sAnalysisCode1) throws
        YssException {
        return getCashAccBalance(dDate, sCashAccCode, sPortCode, sAnalysisCode1,
                                 "", "", "");

    }

    public double getCashAccBalance(java.util.Date dDate, String sCashAccCode,
                                    String sPortCode, String sAnalysisCode1,
                                    String sAnalysisCode2) throws YssException {
        return getCashAccBalance(dDate, sCashAccCode, sPortCode, sAnalysisCode1,
                                 sAnalysisCode2, "", "");

    }

    public double getCashAccBalance(java.util.Date dDate, String sCashAccCode,
                                    String sPortCode, String sAnalysisCode1,
                                    String sAnalysisCode2, String sFLag) throws
        YssException {
        return getCashAccBalance(dDate, sCashAccCode, sPortCode, sAnalysisCode1,
                                 sAnalysisCode2, "", sFLag);

    }

    public double getCashAccBalance(java.util.Date dDate, String sCashAccCode,
                                    String sPortCode, String sAnalysisCode1,
                                    String sAnalysisCode2, String sAnalysisCode3,
                                    String sFLag) throws
        YssException {
        String strSql = "", strError = "", strTmpSql = "";
        ResultSet rs = null;
        double dReAccBalance = 0;
        try {
            if (sCashAccCode == null || sCashAccCode.length() == 0) {
                return 0;
            }
            if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
                strTmpSql = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dDate, "yyyy") + "00'";
            } else {
                strTmpSql = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dDate, "yyyy") + "00'" +
                    " and FStorageDate = (" +
                    " select max(FStorageDate) from " +
                    pub.yssGetTableName("Tb_Stock_Cash") +
                    " where FStorageDate <= " +
                    dbl.sqlDate(YssFun.addDay(dDate, -1)) + ")";

//            dbl.sqlDate(YssFun.addDay(dDate, -1))
            }
            strSql = "select sum(FAccBalance) as FSumAccBalance,sum(FBaseCuryBal) as FSumBaseCuryBal,sum(FPortCuryBal) as FSumPortCuryBal from (";
            strSql = strSql + " select sum(" + dbl.sqlIsNull("a.FAccBalance", "0") +
                ") as FAccBalance, sum(" + dbl.sqlIsNull("a.fbasecurybal", "0") +
                ") as FBaseCuryBal, sum(" + dbl.sqlIsNull("a.FPORTCURYBAL", "0") +
                ") as FPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                " a where FCheckState = 1 and FPortCode='" + sPortCode +
                "' and FCashAccCode ='" + sCashAccCode + "' " + strTmpSql;

            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1='" + sAnalysisCode1 + "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2='" + sAnalysisCode2 + "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0 &&
                !sAnalysisCode3.equals("null")) {
                strSql = strSql + " and FAnalysisCode3='" + sAnalysisCode3 + "'";
            }
            strSql = strSql +
                " union all select FMoney * FinOut,FMoney * FinOut * FBaseCuryRate," +
                " FMoney * FinOut * FBaseCuryRate/FPortCuryRate " +
                " from ( select FNum, FMoney, FInout," +
                " (case when FBaseCuryRate=0 then 1 else FBaseCuryRate end) as FBaseCuryRate, " +
                " (case when FPortCuryRate=0 then 1 else FPortCuryRate end) as FPortCuryRate from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FCheckState=1 and FCashAccCode ='" + sCashAccCode +
                "' and FPortCode='" + sPortCode + "'";
            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1='" + sAnalysisCode1 + "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2='" + sAnalysisCode2 + "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0 &&
                !sAnalysisCode3.equals("null")) {
                strSql = strSql + " and FAnalysisCode3='" + sAnalysisCode3 + "'";
            }
            strSql = strSql + ")b join ( select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FTransferDate = " +
                dbl.sqlDate(dDate) + " and FCheckState=1)c on b.FNum=c.FNum)";

            //  strSql = strSql + " ) ";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReAccBalance = YssFun.roundIt(rs.getDouble("FSumAccBalance"), 2);
                if (sFLag.equalsIgnoreCase(YssOperCons.YSS_BASE_MONEY)) {
                    dReAccBalance = YssFun.roundIt(rs.getDouble("FSumBaseCuryBal"),
                        2);
                } else if (sFLag.equalsIgnoreCase(YssOperCons.YSS_PORT_MONEY)) {
                    dReAccBalance = YssFun.roundIt(rs.getDouble("FSumPortCuryBal"),
                        2);
                }
            }

            return dReAccBalance;
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 重装的此方法，用于保留原有的功能不被改变
     * @param dDate Date
     * @param sCashAccCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @return CashStorageBean
     * @throws YssException
     * MS00538 QDV4海富通2009年06月21日02_AB 沈杰
     */
    public CashStorageBean getCashAccStg(java.util.Date dDate,
                                         String sCashAccCode,
                                         String sPortCode, String sAnalysisCode1,
                                         String sAnalysisCode2,
                                         String sAnalysisCode3) throws YssException {
        return getCashAccStg(dDate, sCashAccCode, sPortCode, sAnalysisCode1, sAnalysisCode2, sAnalysisCode3, true); //默认包括外汇交易的资金调拨
    }

    /**
     * 外汇交易专用的获取资金余额的方法
     * @param dDate Date
     * @param sCashAccCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @return CashStorageBean
     * @throws YssException
     * MS00538 QDV4海富通2009年06月21日02_AB 沈杰
     */
    public CashStorageBean getCashAccStgForRateTrade(java.util.Date dDate,
                                         String sCashAccCode,
                                         String sPortCode, String sAnalysisCode1,
                                         String sAnalysisCode2,
                                         String sAnalysisCode3) throws YssException{
       CashStorageBean reCashStg = null;
       CashStorageBean rateTrade = null;
       double bal = 0D;
       double baseBal = 0D;
       double portBal = 0D;
       //modify by fangjiang 2011.07.08 STORY #1280
       CtlPubPara pubpara = new CtlPubPara();
       pubpara.setYssPub(pub); 
       String strMode1 = pubpara.getRateTradeMode1();
       if (strMode1 != null && strMode1.length() > 0 && strMode1.equals("0")){
    	   reCashStg = getCashAccStg(dDate,sCashAccCode,sPortCode,sAnalysisCode1,sAnalysisCode2,sAnalysisCode3,true);//获取库存和一般的资金调拨
       } else {
    	   reCashStg = getCashAccStg(dDate,sCashAccCode,sPortCode,sAnalysisCode1,sAnalysisCode2,sAnalysisCode3,false);//获取库存和一般的资金调拨
    	   rateTrade = getTransferWithRateTrade(dDate,sCashAccCode,sPortCode,sAnalysisCode1,sAnalysisCode2,sAnalysisCode3);//获取划款的资金调拨
       }
       //---------------------
       if (null != reCashStg){//若有库存或资金调拨
          if (null != rateTrade){//若有划款的资金调拨
            bal = YssD.add(YssFun.toDouble(reCashStg.getStrAccBalance()),YssFun.toDouble(rateTrade.getStrAccBalance()));
            reCashStg.setStrAccBalance(new Double(bal).toString());//将库存和划款的资金调拨相加。

            baseBal = YssD.add(YssFun.toDouble(reCashStg.getStrBaseCuryBal()),YssFun.toDouble(rateTrade.getStrBaseCuryBal()));
            reCashStg.setStrBaseCuryBal(new Double(baseBal).toString());//基础

            portBal = YssD.add(YssFun.toDouble(reCashStg.getStrPortCuryBal()),YssFun.toDouble(rateTrade.getStrPortCuryBal()));
            reCashStg.setStrPortCuryBal(new Double(portBal).toString());//组合
          }
       }
       return reCashStg;
    }

    /**
     * 获取外汇交易中的划款指令中的资金调拨数据
     * @param dDate Date
     * @param sCashAccCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @return CashStorageBean
     * @throws YssException
     * MS00538 QDV4海富通2009年06月21日02_AB 沈杰
     */
    private CashStorageBean getTransferWithRateTrade(java.util.Date dDate,
        String sCashAccCode,
        String sPortCode, String sAnalysisCode1,
        String sAnalysisCode2,
        String sAnalysisCode3) throws YssException {
        CashStorageBean reCashStg = null;
        String sqlStr = null;
        ResultSet rs = null;
        BaseOperDeal baseOperDeal = new BaseOperDeal();
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        double baseRate = 0D;
        double portRate = 0D;
        try {
            /*此段sql的目的为：将外汇交易产生的资金调拨中，币种相同的那笔资金调拨获取出来，将其金额计算入余额中*/
            sqlStr = " select finalData.*, finalAccCode.FCuryCode from (select a.*, b.FCashAccCode, b.FInOut " +
                " from (select fromData.* from " +
                " (select count(*) as dataRows, FNum, sum(FMoney) as FMoney, avg(FBaseCuryRate) as FBaseCuryRate, " +//获取资金调拨的条数
                " avg(FPortCuryRate) as FPortCuryRate from (" +
                " select distinct FNum, FMoney, FBaseCuryRate, FPortCuryRate, FCuryCode " + //将币种相同的部分唯一化
                " from (select money.*, accCode.FCuryCode as FCuryCode " +
                " from (select subtrans.* from (select * from " + pub.yssGetTableName("Tb_cash_transfer") +
                " where FCheckState = 1 and FRateTradeNum <> " + dbl.sqlString(" ") +
                " and FTransferDate = " + dbl.sqlDate(dDate) +
                " ) trans join ( " +
                " select * from " + pub.yssGetTableName("Tb_cash_subtransfer") +
                " where FCheckState = 1 " +
                ( (null != sAnalysisCode1 && sAnalysisCode1.trim().length() > 0) ? " and FAnalysisCode1 = " + dbl.sqlString(sAnalysisCode1) : "") +
                ( (null != sAnalysisCode2 && sAnalysisCode2.trim().length() > 0) ? " and FAnalysisCode2 = " + dbl.sqlString(sAnalysisCode2) : "") +
                ( (null != sAnalysisCode3 && sAnalysisCode3.trim().length() > 0) ? " and FAnalysisCode3 = " + dbl.sqlString(sAnalysisCode3) : "") +
                " and FPortCode = " + dbl.sqlString(sPortCode) + " ) subtrans on trans.FNum = subtrans.FNum) money " +
                " left join (select FCashAccCode, FCuryCode from " + pub.yssGetTableName("Tb_para_cashaccount") +
                " where FCheckState = 1) accCode on money.FCashAccCode = accCode.FCashAccCode) allData " +
                " order by FNum,FCuryCode) base " +
                " group by FNum) fromData " + //以FNum为group条件
                " where dataRows = 1 ) a " +//获取一条的记录，若为1条，则说明为币种相同的数据
                "  join (select FNum, FCashAccCode, FInOut from " + pub.yssGetTableName("Tb_cash_subtransfer") +
                " where FCheckState = 1 and FCashAccCode = " + dbl.sqlString(sCashAccCode) +
                " ) b on a.Fnum = b.FNum) finalData " +
                " join (select FCashAccCode, FCuryCode from " + pub.yssGetTableName("Tb_para_cashaccount") +
                " where FCheckState = 1) finalAccCode on finalData.FCashAccCode = finalAccCode.FCashAccCode ";//获取币种
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                if (null == reCashStg) {
                    reCashStg = new CashStorageBean();
                }
                reCashStg.setStrAccBalance(new Double(rs.getDouble("FMoney")).toString());

                baseOperDeal.setYssPub(pub);

                baseRate = baseOperDeal.getCuryRate(dDate,
                    rs.getString("FCuryCode"), sPortCode,
                    YssOperCons.YSS_RATE_BASE);//基础汇率

                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), sPortCode);

                portRate = rateOper.getDPortRate();//组合汇率

                reCashStg.setStrBaseCuryBal(new Double(baseOperDeal.calBaseMoney(rs.getDouble("FMoney"),
                    baseRate)).toString());//基础金额

                reCashStg.setStrPortCuryBal(new Double(baseOperDeal.calPortMoney(rs.getDouble("FMoney"),
                    baseRate, portRate,
                    rs.getString("FCuryCode"), dDate, sPortCode)).toString());//组合金额
            }
        }
        catch (Exception e) {
            throw new YssException("获取划款产生的资金调拨出现异常！", e);
        }
        finally {
            dbl.closeResultSetFinal(rs);
        }
        return reCashStg;
    }



    /**
     *
     * @param dDate Date
     * @param sCashAccCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param judgeRateTrade boolean 多出的这个参数是用来增加对是否将外汇的资金调拨计入的参数控制
     * @return CashStorageBean
     * @throws YssException
     * MS00538 QDV4海富通2009年06月21日02_AB 沈杰
     */
    public CashStorageBean getCashAccStg(java.util.Date dDate,
                                         String sCashAccCode,
                                         String sPortCode, String sAnalysisCode1,
                                         String sAnalysisCode2,
                                         String sAnalysisCode3,
                                         boolean judgeRateTrade) throws
        YssException {
        CashStorageBean reCashStg = null;
        String strTmpSql = "";
        String strSql = "";
        ResultSet rs = null;
        try {
            if (sCashAccCode == null || sCashAccCode.length() == 0) {
                return null;
            }
            if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
                strTmpSql = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dDate, "yyyy") + "00'";
            } else {
                strTmpSql = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dDate, "yyyy") + "00'" +
                    " and FStorageDate = (" +
                    " select max(FStorageDate) from " +
                    pub.yssGetTableName("Tb_Stock_Cash") +
                    " where FStorageDate <= " +
                    dbl.sqlDate(YssFun.addDay(dDate, -1)) + ")";

//            dbl.sqlDate(YssFun.addDay(dDate, -1))
            }
            strSql = "select sum(FAccBalance) as FSumAccBalance,sum(FBaseCuryBal) as FSumBaseCuryBal,sum(FPortCuryBal) as FSumPortCuryBal from (";
            strSql = strSql + " select sum(" + dbl.sqlIsNull("a.FAccBalance", "0") +
                ") as FAccBalance, sum(" + dbl.sqlIsNull("a.fbasecurybal", "0") +
                ") as FBaseCuryBal, sum(" + dbl.sqlIsNull("a.FPORTCURYBAL", "0") +
                ") as FPortCuryBal from " +
                pub.yssGetTableName("Tb_Stock_Cash") +
                " a where FCheckState = 1 and FPortCode='" + sPortCode +
                "' and FCashAccCode ='" + sCashAccCode + "' " + strTmpSql;

            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1='" + sAnalysisCode1 + "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2='" + sAnalysisCode2 + "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0 &&
                !sAnalysisCode3.equals("null")) {
                strSql = strSql + " and FAnalysisCode3='" + sAnalysisCode3 + "'";
            }
            strSql = strSql +
                " union all select FMoney * FinOut AS FAccBalance,FMoney * FinOut * FBaseCuryRate AS FBaseCuryBal," + //彭鹏   2008.1.24    为了兼容DB2
                " FMoney * FinOut * FBaseCuryRate/FPortCuryRate AS FPortCuryBal " +
                " from ( select FNum, FMoney, FInout," +
                " (case when FBaseCuryRate=0 then 1 else FBaseCuryRate end) as FBaseCuryRate, " +
                " (case when FPortCuryRate=0 then 1 else FPortCuryRate end) as FPortCuryRate from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FCheckState=1 and FCashAccCode ='" + sCashAccCode +
                "' and FPortCode='" + sPortCode + "'";
            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1='" + sAnalysisCode1 + "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2='" + sAnalysisCode2 + "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0 &&
                !sAnalysisCode3.equals("null")) {
                strSql = strSql + " and FAnalysisCode3='" + sAnalysisCode3 + "'";
            }
            strSql = strSql + ")b join ( select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FTransferDate = " +
                dbl.sqlDate(dDate) +
                //MS00538 QDV4海富通2009年06月21日02_AB 沈杰 增加对外汇交易产生的资金调拨的筛选---
                (judgeRateTrade?" ":" and (FRateTradeNum = '' or FRateTradeNum is null)") +
                //-------------------------------------------------------------------------
                " and FCheckState=1)c on b.FNum=c.FNum) m";

            //  strSql = strSql + " ) ";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                reCashStg = new CashStorageBean();
                reCashStg.setStrAccBalance(rs.getDouble("FSumAccBalance") + "");
                reCashStg.setStrBaseCuryBal(rs.getDouble("FSumBaseCuryBal") + "");
                reCashStg.setStrPortCuryBal(rs.getDouble("FSumPortCuryBal") + "");
            }
            return reCashStg;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 重载的方法
     * //--------sj modified 20090121 QDV4嘉实2009年1月5日01_B  bugID:MS00143 ------------
     * @param dDate Date
     * @param sCashAccCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param FTsfTypeCode String
     * @param FSubTsfTypeCode String
     * @return double
     * @throws YssException
     */
    public double getCashLX(java.util.Date dDate, String sCashAccCode,
                            String sPortCode, String sAnalysisCode1,
                            String sAnalysisCode2, String sAnalysisCode3,
                            String FTsfTypeCode, String FSubTsfTypeCode) throws YssException {
        return getCashRecPayBalance(dDate, sCashAccCode,
                                    sPortCode, sAnalysisCode1,
                                    sAnalysisCode2, sAnalysisCode3,
                                    FTsfTypeCode, FSubTsfTypeCode);

    }

    public double getCashLX(java.util.Date dDate, String sCashAccCode,
                            String sPortCode, String sAnalysisCode1,
                            String sAnalysisCode2, String sAnalysisCode3,
                            String FTsfTypeCode) throws
        YssException {
        return getCashRecPayBalance(dDate, sCashAccCode,
                                    sPortCode, sAnalysisCode1,
                                    sAnalysisCode2, sAnalysisCode3,
                                    FTsfTypeCode, "");
    }

    public double getCashLX(java.util.Date dDate, String sCashAccCode,
                            String sPortCode, String sAnalysisCode1,
                            String sAnalysisCode2,
                            String FTsfTypeCode) throws
        YssException {
        return getCashRecPayBalance(dDate, sCashAccCode,
                                    sPortCode, sAnalysisCode1,
                                    sAnalysisCode2, "",
                                    FTsfTypeCode, "");
    }

    public double getCashLX(java.util.Date dDate, String sCashAccCode,
                            String sPortCode, String sAnalysisCode1,
                            String sAnalysisCode2
        ) throws
        YssException {
        return getCashRecPayBalance(dDate, sCashAccCode,
                                    sPortCode, sAnalysisCode1,
                                    sAnalysisCode2, "",
                                    "06", "06DE");
    }

    public double getCashLX(java.util.Date dDate, String sCashAccCode,
                            String sPortCode, String sAnalysisCode1

        ) throws
        YssException {
        return getCashRecPayBalance(dDate, sCashAccCode,
                                    sPortCode, sAnalysisCode1,
                                    "", "",
                                    "", "");
    }

    public double getCashRecPayBalance(java.util.Date dDate, String sCashAccCode,
                                       String sPortCode, String sAnalysisCode1,
                                       String sAnalysisCode2,
                                       String sAnalysisCode3,
                                       String sTsfTypeCode,
                                       String sSubTsfTypeCode) throws
        YssException {
        String strSql = "", strError = "", strTmpSql = "";
        ResultSet rs = null;
        double dReAccLX = 0;
        double dPdAccLX = 0;
        double dBdAccLX = 0;
        //--------------------
        double dPdAccPF = 0; //申购款
        double dBdAccPF = 0;
        //--------------------
        //------------------xuqiji 20100327 MS00952 QDV4赢时胜（测试）2010年03月27日03_B----------//
        //---add by songjie 2012.05.04 BUG 4419 QDV4赢时胜(诺安基金)2012年4月27日01_B start---//
        double dPdBankTradeFee02 = 0;//银行间债券交易费用03调拨类型  07FE02
        double dBdBankTradeFee02 = 0;//银行间债券交易费用07调拨类型  03FE02
        double dPdBankTradeFee03 = 0;//银行间债券交易费用03调拨类型  07FE03
        double dBdBankTradeFee03 = 0;//银行间债券交易费用07调拨类型  03FE03
        double dPdBankTradeFee01 = 0;//银行间债券交易费用03调拨类型  07FE01
        double dBdBankTradeFee01 = 0;//银行间债券交易费用07调拨类型  03FE01
        double dPdYJFee = 0;//应付佣金费用03FE 
        double dBdYJFee = 0;//应付佣金费用07FE 
        double dPdHGFee = 0;//03RE
        double dBdHGFee = 0;//07RE
        double dPdHGFee01 = 0;//支付银行间回购交易费用03RE01
        double dBdHGFee01 = 0;//支付银行间回购交易费用07RE01
        double dPdHGFee03 = 0;//added by liubo.Bug #3671
        double dBdHGFee03 = 0;//07RE03
        double dPdHGFee02 = 0;//支付银行间回购交易费用03RE02
        double dBdHGFee02 = 0;//支付银行间回购交易费用07RE02
        //---add by songjie 2012.05.04 BUG 4419 QDV4赢时胜(诺安基金)2012年4月27日01_B end---//
        //-----------------------------end----------------------------------//
        java.util.Date dMaxStgDate = null;
        String sWhereSql = "";
        try {
            if (sCashAccCode == null || sCashAccCode.length() == 0) {
                return 0;
            }

            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FAnalysisCode1='" + sAnalysisCode1 +
                    "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FAnalysisCode2='" + sAnalysisCode2 +
                    "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0 &&
                !sAnalysisCode3.equals("null")) {
                sWhereSql = sWhereSql + " and FAnalysisCode3='" + sAnalysisCode3 +
                    "'";
            }
            if (sTsfTypeCode != null && sTsfTypeCode.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FTsfTypeCode='" + sTsfTypeCode + "'";
            }
            if (sSubTsfTypeCode != null && sSubTsfTypeCode.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FSubTsfTypeCode='" + sSubTsfTypeCode +
                    "'";
            }

            dMaxStgDate = getMaxStgEveDate("tb_stock_cashpayrec", dDate);
            if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
                strTmpSql = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dDate, "yyyy") + "00'";
            } else {
                strTmpSql = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dDate, "yyyy") + "00'" +
                    " and FStorageDate = " + dbl.sqlDate(dMaxStgDate);
            }
            strSql = "select FBal as FSumAccLX from " +
                pub.yssGetTableName("tb_stock_cashpayrec") +
                "  where FCheckState = 1 and FPortCode='" + sPortCode +
                "' and FCashAccCode ='" + sCashAccCode + "' " + strTmpSql;
            strSql = strSql + sWhereSql;
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReAccLX = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
            }

            /*strSql = "select sum(FMoney) as FSumAccLX from " +
                  pub.yssGetTableName("tb_data_CashPayRec") +
                  "  where FCheckState = 1 and FPortCode='" + sPortCode +
                  "' and FCashAccCode ='" + sCashAccCode + "' " +
                  " and (FTransDate between " +
                  dbl.sqlDate(YssFun.addDay(dMaxStgDate, 1)) +
                  " and " + dbl.sqlDate(dDate) + ")";*/
            //--------------若在之前已经支付过，则在表中有一条支付记录，需要减去。所以按照FSubTsfTypeCode
            //--------------Group By,以便在之后冲减前日的库存余额。sj edit 20080214-------------------------//
            strSql = "select sum(FMoney) as FSumAccLX,FSubTsfTypeCode from " +
                pub.yssGetTableName("tb_data_CashPayRec") +
                "  where FCheckState = 1 and FPortCode='" + sPortCode +
                "' and FCashAccCode ='" + sCashAccCode + "' " +
                " and (FTransDate between " +
                dbl.sqlDate(YssFun.addDay(dMaxStgDate, 1)) +
                " and " + dbl.sqlDate(dDate) + ")";
            //strSql = strSql + sWhereSql;
            strSql = strSql + " group by FSubTsfTypeCode ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //dReAccLX = YssD.add(dReAccLX, rs.getDouble("FSumAccLX"));
                if (rs.getString("FSubTsfTypeCode").equalsIgnoreCase("02DE")) {
                    dPdAccLX = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                } else if (rs.getString("FSubTsfTypeCode").equalsIgnoreCase("06DE")) {
                    dBdAccLX = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }
                //--------sj modified 20090121 QDV4嘉实2009年1月5日01_B  bugID:MS00143 -----------------------------
                else if (rs.getString("FSubTsfTypeCode").equalsIgnoreCase("02PF")) { //当为申购款类型时,进行不一样的赋值.
                    dPdAccPF = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                } else if (rs.getString("FSubTsfTypeCode").equalsIgnoreCase("06PF")) { //当为申购款类型时,进行不一样的赋值.
                    dBdAccPF = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                 //---------xuqiji 20100327 MS00952 QDV4赢时胜（测试）2010年03月27日03_B ------------//
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("07FE02")){
                	dBdBankTradeFee02 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("03FE02")){
                	dPdBankTradeFee02 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("07FE")){
                	dBdYJFee = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("03FE")){
                	dPdYJFee = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }
                //20120202 added by liubo.Bug #3671
                //在收益支付界面的现金利息支付中，“支付银行间回购交易费用”没有考虑到调拨子类型“07RE03”结算手续费的
                //=================================
                else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("07RE03")){
                	dBdHGFee03 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                //edit by songjie 2012.05.04 03RE01 改为 03RE03
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("03RE03")){
                	dPdHGFee03 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }
                //=================================
                //-------------------------------end---------------------------//
                //---add by songjie 2012.05.04 BUG 4419 QDV4赢时胜(诺安基金)2012年4月27日01_B start---//
                else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("07RE01")){
                	dBdHGFee01 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("03RE01")){
                	dPdHGFee01 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("07RE")){
                	dBdHGFee = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("03RE")){
                	dPdHGFee = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("07RE02")){
                	dBdHGFee02 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("03RE02")){
                	dPdHGFee02 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("07FE03")){
                	dBdBankTradeFee03 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("03FE03")){
                	dPdBankTradeFee03 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("07FE01")){
                	dBdBankTradeFee01 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }else if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase("03FE01")){
                	dPdBankTradeFee01 = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
                }
                //---add by songjie 2012.05.04 BUG 4419 QDV4赢时胜(诺安基金)2012年4月27日01_B end---//
                //-------------------------------------------------------------------------------------------------
            }
            //-----------前日的余额+需要支付的-以支付的 20080214----------------------------------------//
            if (sSubTsfTypeCode != null && sSubTsfTypeCode.trim().indexOf("PF") > 0) { //sj modified 20090121 QDV4嘉实2009年1月5日01_B  bugID:MS00143
                dReAccLX = YssD.sub(YssD.add(dReAccLX, dBdAccPF), dPdAccPF); //当为申购款计息时,使用不同的变量计算.sj modified 20090121
            //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            }else if(sSubTsfTypeCode != null && sSubTsfTypeCode.equalsIgnoreCase("07FE02")){
            	dReAccLX = YssD.sub(YssD.add(dReAccLX, dBdBankTradeFee02), dPdBankTradeFee02);
            }else if(sSubTsfTypeCode != null && sSubTsfTypeCode.equalsIgnoreCase("07FE")){
            	dReAccLX = YssD.sub(YssD.add(dReAccLX, dBdYJFee), dPdYJFee);
            }else if(sSubTsfTypeCode != null && sSubTsfTypeCode.equalsIgnoreCase("07RE01")){
            	dReAccLX = YssD.sub(YssD.add(dReAccLX, dBdHGFee01), dPdHGFee01);
            //---add by songjie 2012.05.04 BUG 4419 QDV4赢时胜(诺安基金)2012年4月27日01_B start---//
            }else if(sSubTsfTypeCode != null && sSubTsfTypeCode.equalsIgnoreCase("07RE02")){
            	dReAccLX = YssD.sub(YssD.add(dReAccLX, dBdHGFee02), dPdHGFee02);
            }else if(sSubTsfTypeCode != null && sSubTsfTypeCode.equalsIgnoreCase("07RE")){
            	dReAccLX = YssD.sub(YssD.add(dReAccLX, dBdHGFee), dPdHGFee);
            }else if(sSubTsfTypeCode != null && sSubTsfTypeCode.equalsIgnoreCase("07FE03")){
            	dReAccLX = YssD.sub(YssD.add(dReAccLX, dBdBankTradeFee03), dPdBankTradeFee03);
            }else if(sSubTsfTypeCode != null && sSubTsfTypeCode.equalsIgnoreCase("07FE01")){
            	dReAccLX = YssD.sub(YssD.add(dReAccLX, dBdBankTradeFee01), dPdBankTradeFee01);
            }
            //---add by songjie 2012.05.04 BUG 4419 QDV4赢时胜(诺安基金)2012年4月27日01_B end---//
            //20120202 added by liubo.Bug #3671
            //在收益支付界面的现金利息支付中，“支付银行间回购交易费用”没有考虑到调拨子类型“07RE03”结算手续费的
            //=================================
            else if(sSubTsfTypeCode != null && sSubTsfTypeCode.equalsIgnoreCase("07RE03")){
            	dReAccLX = YssD.sub(YssD.add(dReAccLX, dBdHGFee03), dPdHGFee03);
            }
			//---end---
            //============end====================
            else { //当其他的情况时,使用原来的方式计算.sj modified 20090121
                dReAccLX = YssD.sub(YssD.add(dReAccLX, dBdAccLX), dPdAccLX);
            }
            
            return YssFun.roundIt(dReAccLX, 2);
            //-------------------------------------------------------------------------------
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取财务表里面的期末余额 做为估值表里面真正的实际成本
    public double getTotalAmount(java.util.Date dDate, String portCode) throws
        YssException {
        ResultSet rs = null;
        double dAEndBal = 0;
        String strSql = "";
        String strTab = ""; //得到的表名
        String strYear = ""; //月份
        String strMonth = "";
        String strError = "";
        try {
            strYear = YssFun.getYear(dDate) + "";
            strMonth = YssFun.getMonth(dDate) + "";
            strTab = "a" + strYear + portCode + "lbalance"; //获得表名
            //不存在对应的估值表就直接返回
            if (!dbl.yssTableExist("Lsetlist")) {
                return 0;
            }
            strYear = YssFun.getYear(dDate) + "";
            strMonth = YssFun.getMonth(dDate) + "";
            rs = dbl.openResultSet(
                "Select FSetCode from Lsetlist where FSetID = '" + portCode +
                "'");
            if (!rs.next()) {
                return 0;
            }
            return GetAccBalance("4001", dDate, rs.getInt("FSetCode"));
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获得科目余额
    public double GetAccBalance(String sAccCode, java.util.Date dDate,
                                YssType lAmount, int iSet) throws YssException {
        ResultSet rsTmp = null;
        String sField, strSql;
        int lDC = 0;
        int iTmp;
        double dMoney = 0;
        int iYear = YssFun.getYear(dDate);
        int clYear = 0, clMonth = 0;
        try {
            sField = "b";
            String strTab = "a" + iYear + YssFun.formatNumber(iSet, "000");
            strSql = "select FBalDC from " + strTab + "LAccount where FAcctCode='" +
                sAccCode.toUpperCase() + "'";
            rsTmp = dbl.openResultSet(strSql);
            if (rsTmp.next()) {
                lDC = rsTmp.getInt("FBalDc");
            }
            rsTmp.getStatement().close();
            rsTmp = null;
            iTmp = YssFun.getMonth(dDate);
            strSql = "select '" + sAccCode + "' fkmh,sum(case when fjd='J' then f" +
                sField + "bal else -f" + sField +
                "bal end) as fbal,sum(case when fjd='J' then fsl else -fsl end) as fsl" +
                " from " + strTab + "fcwvch where fterm=" + iTmp +
                " and fkmh like '" + sAccCode + "%'" + " and " +
                dbl.sqlDay("fdate") + "<=" + YssFun.getDay(dDate) + "";
            strSql = "select " + dbl.sqlIsNull("a.fendbal") + "+" +
                dbl.sqlIsNull("b.fbal") + " fje," + dbl.sqlIsNull("a.faendbal") +
                "+" + dbl.sqlIsNull("b.fsl") +
                " fsl from (select '" + sAccCode + "' FAcctCode,sum(f" + sField +
                "endbal) fendbal,sum(faendbal) faendbal from " + strTab +
                "LBalance where FAcctcode='" + sAccCode.toUpperCase() +
                "' and fmonth=" +
                ( (iYear > clYear || (iTmp > clMonth && iYear == clYear)) ?
                 iTmp - 1 : 0) + ") a full join (" + strSql +
                ") b on upper(a.facctcode)=upper(b.fkmh)";
            rsTmp = dbl.openResultSet(strSql);
            if (rsTmp.next()) {
                lAmount.setDouble(lDC * rsTmp.getDouble("fsl"));
                dMoney = YssFun.roundIt(YssD.mul(lDC, rsTmp.getDouble("fje")), 2);
            }
            rsTmp.getStatement().close();
            //System.out.print("取科目" + "\t" + sAccCode + "\t" + "余额结束" + "\t" + new java.util.Date()  + "\t" + System.currentTimeMillis()  + "\r\n");
            return dMoney;
        } catch (Exception sqle) {
            throw new YssException("获取科目余额出错！", sqle);
        } finally {
            dbl.closeResultSetFinal(rsTmp);
        }
    }

    public double GetAccBalance(String sAccCode, java.util.Date dDate, int iSet) throws
        YssException {
        YssType lamount = new YssType();
        return GetAccBalance(sAccCode, dDate, lamount, iSet);
    }

    //通过证券代码来获得币种 by sunny
    public String getSecCuryCode(String strSecurityCode) throws YssException {
        ResultSet rs = null;
        String curyCode = "";
        try {
            String strSql = "select * from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FSecurityCode=" + dbl.sqlString(strSecurityCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                curyCode = rs.getString("FTradeCury");
            }
            return curyCode;
        } catch (Exception e) {
            throw new YssException("得到货币代码出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 2009-09-14 蒋锦 修改 添加属性分类代码作为形参 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * @param dDate Date
     * @param sSecurityCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param sTsfTypeCode String
     * @param sSubTsfTypeCode String
     * @param sAttrClsCode String：属性分类
     * @return double
     * @throws YssException
     */
    public double getBondLx(java.util.Date dDate,
                            String sSecurityCode,
                            String sPortCode, String sAnalysisCode1,
                            String sAnalysisCode2,
                            String sAnalysisCode3,
                            String sTsfTypeCode,
                            String sSubTsfTypeCode,
                            String sAttrClsCode) throws
        YssException {
        //----------- MS00278  QDV4中保2009年02月24日03_B ------------------------
        boolean isFourDigit = false;
        CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        String digit = pubpara.getKeepFourDigit(); //获取通用参数中的小数保留位数
        if (digit.toLowerCase().equalsIgnoreCase("two")) { //两位
            isFourDigit = false;
        } else if (digit.toLowerCase().equalsIgnoreCase("four")) { //四位
            isFourDigit = true;
        }
        //----------------------------------------------------------------------
        String strSql = "", strError = "", strTmpSql = "";
        ResultSet rs = null;
        double dReBondLX = 0;
        java.util.Date dMaxStgDate = null;
        String sWhereSql = "";
        YssDbOperSql operSql = new YssDbOperSql(pub); //QDV4赢时胜(上海)2009年1月5日02_B BugId:MS00144 sj modified 20090121
        try {
            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FAnalysisCode1='" + sAnalysisCode1 +
                    "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FAnalysisCode2='" + sAnalysisCode2 +
                    "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0 &&
                !sAnalysisCode3.equals("null")) {
                sWhereSql = sWhereSql + " and FAnalysisCode3='" + sAnalysisCode3 +
                    "'";
            }
            if (sTsfTypeCode != null && sTsfTypeCode.trim().length() > 0) {
                //--------MS00327 QDV4赢时胜（上海）2009年3月16日05_B 可传入多种资金调拨---
//          sWhereSql = sWhereSql + " and FTsfTypeCode='" + sTsfTypeCode + "'";
                sWhereSql = sWhereSql + " and FTsfTypeCode in (" + operSql.sqlCodes(sTsfTypeCode) +
                    ")";
                //-------------------------------------------------------------------
            }
            if (sSubTsfTypeCode != null && sSubTsfTypeCode.trim().length() > 0) {
//          sWhereSql = sWhereSql + " and FSubTsfTypeCode='" + sSubTsfTypeCode +
//          "'";
                //-------QDV4赢时胜(上海)2009年1月5日02_B BugId:MS00144 --允许多个调拨子类型。sj modified 20090121 ---//
                sWhereSql = sWhereSql + " and FSubTsfTypeCode in (" + operSql.sqlCodes(sSubTsfTypeCode) +
                    ")";
                //-----------------------------------------------------------------------------------------------//
            }
            if (sSecurityCode != null && sSecurityCode.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FSecurityCode = " + dbl.sqlString(sSecurityCode);
            }
            if (sPortCode != null && sPortCode.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FPortCode = " + dbl.sqlString(sPortCode);
            }
            //2009-09-14 蒋锦 修改 添加属性分类代码作为查询条件 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
            if (sAttrClsCode != null && sAttrClsCode.trim().length() > 0){
                sWhereSql = sWhereSql + " AND FAttrClsCode = " + dbl.sqlString(sAttrClsCode);
            }
            dMaxStgDate = getMaxStgEveDate("Tb_Stock_SecRecPay", dDate);
            if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
                strTmpSql = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dDate, "yyyy") + "00'";
            } else {
                strTmpSql = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dDate, "yyyy") + "00'" +
                    " and FStorageDate = " + dbl.sqlDate(dMaxStgDate);
            }
            strSql = "select FBal as FSumBondLX from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                "  where FCheckState = 1 and FPortCode=" + dbl.sqlString(sPortCode) +
                strTmpSql;
            strSql = strSql + sWhereSql;
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                //----------- MS00278  QDV4中保2009年02月24日03_B ------------------------
                dReBondLX = YssFun.roundIt(rs.getDouble("FSumBondLX"), isFourDigit ? 4 : 2); //是否保留四位小数
                //----------------------------------------------------------------------
            }
            //-------MS00327 QDV4赢时胜（上海）2009年3月16日05_B 当资金调拨为卖出利息时，减去其数值---------------------------------
            //2009.09.14 蒋锦 修改 统计当天发生额要乘以方向 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
            strSql = "select sum(case when FSubTsfTypeCode = '02FI_B' then -1*FMoney else FMoney * FInOut end) as FSumBondLX from " +
                //--------------------------------------------------------------------------------------------------------------
                pub.yssGetTableName("tb_data_SecRecPay") +
                "  where FCheckState = 1 and FPortCode='" + sPortCode +
                "' " +
                " and FTransDate = " + dbl.sqlDate(dDate);
            strSql = strSql + sWhereSql;
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReBondLX = YssD.add(dReBondLX, rs.getDouble("FSumBondLX"));
            }
            return dReBondLX;
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 获取债券已计提利息的方法 sj add 20071122
     * 2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * @param dDate Date
     * @param sSecurityCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param sTsfTypeCode String
     * @param sSubTsfTypeCode String
     * @param sAttrClsCode String
     * @param sInvestType String
     * @throws YssException
     * @return double
     */
    //edit by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 添加参数 sInvestType
    public double getFixInterestBalance(java.util.Date dDate,
                                        String sSecurityCode,
                                        String sPortCode, String sAnalysisCode1,
                                        String sAnalysisCode2,
                                        String sAnalysisCode3,
                                        String sTsfTypeCode,
                                        String sSubTsfTypeCode,
                                        String sAttrClsCode,
                                        String sInvestType) throws
        YssException {
        String strSql = "", strError = "", strTmpSql = "";
        ResultSet rs = null;
        double dReAccLX = 0;
        java.util.Date dMaxStgDate = null;
        String sWhereSql = "";
        try {
            if(sAttrClsCode != null && sAttrClsCode.trim().length() > 0){
                sWhereSql = sWhereSql + " AND FAttrClsCode = " + dbl.sqlString(sAttrClsCode);
            }
            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FAnalysisCode1='" + sAnalysisCode1 +
                    "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FAnalysisCode2='" + sAnalysisCode2 +
                    "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0 &&
                !sAnalysisCode3.equals("null")) {
                sWhereSql = sWhereSql + " and FAnalysisCode3='" + sAnalysisCode3 +
                    "'";
            }
            if (sTsfTypeCode != null && sTsfTypeCode.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FTsfTypeCode='" + sTsfTypeCode + "'";
            }
            if (sSubTsfTypeCode != null && sSubTsfTypeCode.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FSubTsfTypeCode='" + sSubTsfTypeCode +
                    "'";
            }
            if (sSecurityCode != null && sSecurityCode.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FSecurityCode = " + dbl.sqlString(sSecurityCode);
            }
            //--- add by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if (sInvestType != null && sInvestType.trim().length() > 0){
            	sWhereSql = sWhereSql + " and FInvestType = " + dbl.sqlString(sInvestType);
            }
            //--- add by songjie 2013.04.03 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
			//这里直接取昨日的库存日期,对参数的优化 by leeyu 20100414 合并太平版本代码
	        /*dMaxStgDate = getMaxStgEveDate("Tb_Stock_SecRecPay", dDate);
	        if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
	           strTmpSql = strTmpSql + " and fyearmonth ='" +
	                 YssFun.formatDate(dDate, "yyyy") + "00'";
	        }
	        else {
	           strTmpSql = strTmpSql + " and fyearmonth <>'" +
	                 YssFun.formatDate(dDate, "yyyy") + "00'" +
	                 " and FStorageDate = " + dbl.sqlDate(dMaxStgDate);
	        }*/
	        sWhereSql = sWhereSql +" and "+(new YssDbOperSql(pub)).sqlStoragEve(dDate);
			//这里直接取昨日的库存日期,对参数的优化 合并太平版本代码
            strSql = "select FBal as FSumAccLX from " +
                pub.yssGetTableName("Tb_Stock_SecRecPay") +
                "  where FCheckState = 1 and FPortCode=" + dbl.sqlString(sPortCode) +
                strTmpSql;
            strSql = strSql + sWhereSql;
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReAccLX = YssFun.roundIt(rs.getDouble("FSumAccLX"), 2);
            }
            return dReAccLX;
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public double getInvestPaid(java.util.Date dDate,
                                String sPortCode, String sAnalysisCode1,
                                String sAnalysisCode2, String sAnalysisCode3
        ) throws
        YssException {
        return getInvestPaid(dDate,
                             sPortCode, sAnalysisCode1,
                             sAnalysisCode2, sAnalysisCode3,
                             "07");
    }

    public double getInvestPaid(java.util.Date dDate,
                                String sPortCode, String sAnalysisCode1,
                                String sAnalysisCode2
        ) throws
        YssException {
        return getInvestPaid(dDate,
                             sPortCode, sAnalysisCode1,
                             sAnalysisCode2, "",
                             "07");
    }

    public double getInvestPaid(java.util.Date dDate,
                                String sPortCode, String sAnalysisCode1
        ) throws
        YssException {
        return getInvestPaid(dDate,
                             sPortCode, sAnalysisCode1,
                             "", "",
                             "07");
    }

    public double getInvestPaid(java.util.Date dDate,
                                String sPortCode

        ) throws
        YssException {
        return getInvestPaid(dDate,
                             sPortCode, "",
                             "", "",
                             "07");
    }

    /**
     * 计算运营收支的金额 sj 20071128 add
     * @param dDate Date
     * @param sCashAccCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param sTsfTypeCode String
     * @param sSubTsfTypeCode String
     * @throws YssException
     * @return double
     */
    public double getInvestPaid(java.util.Date dDate,
                                String sPortCode, String sAnalysisCode1,
                                String sAnalysisCode2,
                                String sAnalysisCode3,
                                String sFIVPayCatCode
        ) throws
        YssException {
        String strSql = "", strError = "", strTmpSql = "";
        ResultSet rs = null;
        double dReInvestLX = 0;
        double dPdInvest = 0;
        double dBdInvest = 0;
        java.util.Date dMaxStgDate = null;
        String sWhereSql = "";
        try {

            if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FAnalysisCode1='" + sAnalysisCode1 +
                    "'";
            }
            if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FAnalysisCode2='" + sAnalysisCode2 +
                    "'";
            }
            if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0 &&
                !sAnalysisCode3.equals("null")) {
                sWhereSql = sWhereSql + " and FAnalysisCode3='" + sAnalysisCode3 +
                    "'";
            }
            if (sFIVPayCatCode != null && sFIVPayCatCode.trim().length() > 0) {
                sWhereSql = sWhereSql + " and FIVPayCatCode='" + sFIVPayCatCode + "'";
            }

            dMaxStgDate = getMaxStgEveDate("tb_stock_Invest", dDate);
            if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
                strTmpSql = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dDate, "yyyy") + "00'";
            } else {
                strTmpSql = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dDate, "yyyy") + "00'" +
                    " and FStorageDate = " + dbl.sqlDate(dMaxStgDate);
            }
            strSql = "select FBal as FSumInvestLX from " +
                pub.yssGetTableName("tb_stock_Invest") +
                "  where FCheckState = 1 and FPortCode='" + sPortCode +
                "' " + strTmpSql;
            strSql = strSql + sWhereSql;
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dReInvestLX = YssFun.roundIt(rs.getDouble("FSumInvestLX"), 2);
            }

            /*strSql = "select sum(FMoney) as FSumInvestLX from " +
                  pub.yssGetTableName("tb_data_InvestPayRec") +
                  "  where FCheckState = 1 and FPortCode='" + sPortCode +
                  "' " +
                  " and FTransDate = " + dbl.sqlDate(dDate);*/
            //--------------若在之前已经支付过，则在表中有一条支付记录，需要减去。所以按照FSubTsfTypeCode
            //--------------Group By,以便在之后冲减前日的库存余额。sj edit 20080214-------------------------//
            strSql = "select sum(FMoney) as FSumInvestLX,FSubTsfTypeCode from " +
                pub.yssGetTableName("tb_data_InvestPayRec") +
                "  where FCheckState = 1 and FPortCode='" + sPortCode +
                "' " +
                " and FTransDate = " + dbl.sqlDate(dDate);
            strSql = strSql + sWhereSql + " group by FSubTsfTypeCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FSubTsfTypeCode").equalsIgnoreCase("03IV")) {
                    dPdInvest = YssFun.roundIt(rs.getDouble("FSumInvestLX"), 2);
                } else if (rs.getString("FSubTsfTypeCode").equalsIgnoreCase("07IV")) {
                    dBdInvest = YssFun.roundIt(rs.getDouble("FSumInvestLX"), 2);
                }
                //dReInvestLX = YssD.add(dReInvestLX, rs.getDouble("FSumInvestLX"));
            }
            //-----------前日的余额+需要支付的-以支付的 20080214------------------------------//
            dReInvestLX = YssD.sub(YssD.add(dReInvestLX, dBdInvest), dPdInvest);
            return YssFun.roundIt(dReInvestLX, 2);
            //---------------------------------------------------------------------
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //add by guolongchao STORY 1222 获取货币式基金的应收红利
    public String getSecPayRecBal(String fyearmonth,java.util.Date fstoragedate,String sPortCode, 
    		              String fTsfTypeCode, String fSubTsfTypeCode, String fsecuritycode,
                          String sAnalysisCode1,String sAnalysisCode2,String sAnalysisCode3) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sWhereSql = "";
        String yshl = "";
        try {
        	 strSql="select sum(a.fbal) as FBal from "+pub.yssGetTableName("Tb_Stock_Secrecpay")+" a  where a.FcheckState = 1 ";
        	 
        	         if (fyearmonth != null && fyearmonth.trim().length() > 0) {
		                 sWhereSql = sWhereSql + " and a.fyearmonth='" + fyearmonth + "'";
		             }
		             if (fstoragedate != null) {
		                 sWhereSql = sWhereSql + " and a.fstoragedate=" + dbl.sqlDate(fstoragedate) +"";
		             }
		             if (sPortCode != null && sPortCode.trim().length() > 0 ) {
		                 sWhereSql = sWhereSql + " and a.fportcode='" + sPortCode +"'";
		             }
		             
		             if (fTsfTypeCode != null && fTsfTypeCode.trim().length() > 0) {
		                 sWhereSql = sWhereSql + " and a.FTsfTypeCode='" + fTsfTypeCode + "'";
		             }
		             if (fSubTsfTypeCode != null && fSubTsfTypeCode.trim().length() > 0) {
		                 sWhereSql = sWhereSql + " and a.FSubTsfTypeCode='" + fSubTsfTypeCode +"'";
		             }
		             if (fsecuritycode != null && fsecuritycode.trim().length() > 0 ) {
		                 sWhereSql = sWhereSql + " and a.fsecuritycode='" + fsecuritycode +"'";
		             }		             
		        	 if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
		                 sWhereSql = sWhereSql + " and a.FAnalysisCode1='" + sAnalysisCode1 + "'";
		             }
		             if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
		                 sWhereSql = sWhereSql + " and a.FAnalysisCode2='" + sAnalysisCode2 +"'";
		             }
		             if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0 ) {
		                 sWhereSql = sWhereSql + " and a.FAnalysisCode3='" + sAnalysisCode3 +"'";
		             }            
		    strSql=strSql+sWhereSql;
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	yshl = rs.getDouble("fbal") + "";
            }
        } catch (Exception e) {
        	 throw new YssException("获取开放式基金的应收红利信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return yshl;
    }
}

/*         strTab="a" + strYear + YssFun.formatNumber(rs.getInt("FSetCode") , "000") + "lbalance"; //获得表名
         if (dbl.yssTableExist(strTab.toUpperCase())) {//如果表存在 就把其期末余额得出来
            strSql = "select 	FAEndBal from " + strTab +
                  " where FacctCode='4001' and Fmonth=" +
                  dbl.sqlString(strMonth);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
               dAEndBal = rs.getDouble("FAEndBal");
            }
            return dAEndBal;
         }
         else {
            return 0;
         }
      }
      catch (Exception e) {
         throw new YssException(strError + "\r\n" + e.getMessage(), e);
      }
      finally {
         dbl.closeResultSetFinal(rs);
      }

   }
 */
