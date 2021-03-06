package com.yss.main.operdeal.stgstat;

import java.util.*;

import com.yss.dsub.YssPreparedStatement;
import com.yss.util.*;
import java.sql.*;

public class ZbStgRollAsset
    extends BaseStgStatDeal {
    public ZbStgRollAsset() {
    }

    public ArrayList getStorageStatData(java.util.Date dOperDate) throws
        YssException {
        double dBaseCuryBal = 0, dPortCuryBal = 0;
        java.sql.Date opdate;
        HashMap hmPc = null;
        HashMap hmRoll = null;
        HashMap hmRi = null;
        HashMap hmGz = null;
        HashMap hmCjsr = null;
        HashMap hmIc = null;
        HashMap hmFh = null;
        HashMap hmInvset = null;
        double dTmp = 0;

        String strSql = "";	
        //modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement pst = null;
        //=============end====================
//      java.util.Date dOperDate = null;
        String sYearMonth = "";
        String strTmpSql = "", strTmpSql2 = "";
        ResultSet rs1 = null;
        try {
            String[] sPortCodeAry = portCodes.split(",");

            if (portCodes.length() > 0) {
                strTmpSql = " and FPortCode in (" + portCodes + ")";
                strTmpSql2 = " in (" + portCodes + ")";
            } else {
            	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                strTmpSql = " and FPortCode in ( select FPortCode from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 and Fassetgroupcode='" +
                pub.getAssetGroupCode() +
                "' ) ";
        
                
                //end by lidaolong
            }

            strSql = "insert into " +
                pub.yssGetTableName("Tb_Stock_RollAsset") +
                "(FStorageDate,FPortCode, FYearMonth, FCuryCode, FPaidCapital, " +
                " FRollBal, FPortCuryRate, FBaseCuryRate, " +
                " FCheckState, FCreator, FCreateTime,FCheckUser,FCheckTime) " +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//            pst = dbl.loadConnection().prepareStatement(strSql);
            pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
//         for (int j = 0; j <= YssFun.dateDiff(dStartDate, dEndDate); j++) {
//            dOperDate = YssFun.addDay(dStartDate, j);
            sYearMonth = YssFun.formatDate(dOperDate, "yyyyMM");
            strSql = "delete from " +
                pub.yssGetTableName("Tb_Stock_RollAsset") +
                " where FStorageDate=" + dbl.sqlDate(dOperDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dOperDate, "yyyyMM")) +
                strTmpSql;
            dbl.executeSql(strSql);
            hmPc = getPaidCapital(dOperDate, portCodes);
            hmRoll = getProRollBal(dOperDate, portCodes);
            hmRi = getRecInterest(dOperDate, portCodes);
            hmGz = this.getGZ(portCodes, dOperDate);
            hmCjsr = this.getCJSR(portCodes, dOperDate);
            hmIc = this.getIncome(portCodes, dOperDate);
            hmFh = this.getFh(dOperDate, portCodes);
            hmInvset = this.getInvestMoney(dOperDate, portCodes);
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1 " + strTmpSql;
            rs1 = dbl.openResultSet(strSql);
            while (rs1.next()) {
                pst.setDate(1, YssFun.toSqlDate(dOperDate));
                pst.setString(2, rs1.getString("FPortCode"));
                pst.setString(3, sYearMonth);
                pst.setString(4, rs1.getString("FPortCury"));
//                  dTmp = ((Double)hmPc.get(rs1.getString("FPortCode"))).doubleValue();
                if (hmPc.containsKey(rs1.getString("FPortCode"))) {
                    pst.setDouble(5,
                                  ( (Double) hmPc.get(rs1.getString("FPortCode"))).
                                  doubleValue());
                } else {
                    pst.setDouble(5, 0);
                }
                pst.setDouble(6, 0);
                if (hmRoll.containsKey(rs1.getString("FPortCode"))) {
                    dTmp = ( (Double) hmRoll.get(rs1.getString("FPortCode"))).
                        doubleValue();
                }
                if (hmRi.containsKey(rs1.getString("FPortCode"))) {
                    dTmp = YssD.add(dTmp,
                                    ( (Double) hmRi.get(rs1.getString("FPortCode"))).
                                    doubleValue());
                }
                if (hmGz.containsKey(rs1.getString("FPortCode"))) {
                    dTmp = YssD.add(dTmp,
                                    ( (Double) hmGz.get(rs1.getString("FPortCode"))).
                                    doubleValue());
                }
                if (hmCjsr.containsKey(rs1.getString("FPortCode"))) {
                    dTmp = YssD.add(dTmp,
                                    ( (Double) hmCjsr.get(rs1.getString(
                                        "FPortCode"))).
                                    doubleValue());
                }
                if (hmIc.containsKey(rs1.getString("FPortCode"))) {
                    dTmp = YssD.add(dTmp,
                                    ( (Double) hmIc.get(rs1.getString("FPortCode"))).
                                    doubleValue());
                }
                if (hmFh.containsKey(rs1.getString("FPortCode"))) {
                    dTmp = YssD.add(dTmp,
                                    ( (Double) hmFh.get(rs1.getString("FPortCode"))).
                                    doubleValue());
                }
//                  if (hmInvset.containsKey(rs1.getString("FPortCode"))){
//                     dTmp = YssD.add(dTmp,
//                                     ( (Double) hmInvset.get(rs1.getString("FPortCode"))).
//                                     doubleValue());
//                  }
//
                pst.setDouble(6, dTmp);
//                  if (hmPc.containsKey(rs1.getString("FPortCode"))){
//                     pst.setDouble(6,
//                                   ( (Double) hmPc.get(rs1.getString("FPortCode"))).
//                                   doubleValue());
//                  }
                pst.setDouble(7, 1); //组合汇率
//            pst.setDouble(8,
//                          this.getSettingOper().getCuryRate(dOperDate,
//                  rs1.getString("FPortCury")));
                pst.setInt(9, 1); //审核状态
                pst.setString(10, pub.getUserCode());
                pst.setString(11, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(12, pub.getUserCode());
                pst.setString(13, YssFun.formatDatetime(new java.util.Date()));
                pst.executeUpdate();
            }
//         }
        } catch (Exception e) {
            throw new YssException(e);
        }
        return null;
    }

    public String saveStorageStatData(ArrayList statData) throws YssException {
        return "";
    }

    public HashMap getPaidCapital(java.util.Date dDate, String sPortCodes) throws
        YssException { //获取实收资本
        String strSql = "";
        ResultSet rs = null;
        HashMap hmResult = new HashMap();
        double dTmp = 0;
        try {
            strSql = "select FPortCode, sum(FPaidCapital) as FPaidCapital from " +
                pub.yssGetTableName("Tb_Stock_RollAsset") +
                " where FPortCode in (" + sPortCodes + ") and " +
                operSql.sqlStoragEve(dDate) +
                " group by FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                hmResult.put(rs.getString("FPortCode"),
                             new Double(rs.getDouble("FPaidCapital")));
            }
            dbl.closeResultSetFinal(rs);

            strSql = "select b.FPortCode,sum(FMoney*FInOut) as FMoney from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " a join (select * from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FCheckState = 1 and FPortCode in (" + sPortCodes + ")" +
                " ) b on a.FNum = b.FNum where a.FTsfTypeCode = '04'" +
                " and FTransferDate = " + dbl.sqlDate(dDate) +
                " group by b.FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (hmResult.containsKey(rs.getString("FPortCode") + "")) {
                    dTmp = ( (Double) hmResult.get(rs.getString("FPortCode") + "")).
                        doubleValue();
                } else {
                    dTmp = 0;
                }
                dTmp = YssD.add(dTmp, rs.getDouble("FMoney"));
                hmResult.put(rs.getString("FPortCode"), new Double(dTmp));
            }
            return hmResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private HashMap getProRollBal(java.util.Date dDate, String portCodes) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        String port = "";
        double money;
        HashMap hmMoney = new HashMap();
        try {
            strSql = "select FPortCode, FRollBal from " +
                pub.yssGetTableName("Tb_Stock_RollAsset") +
                " where " + operSql.sqlStoragEve(dDate) +
                " and FCheckState = 1 and FPortCode in (" +
                portCodes + ")";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                port = rs.getString("FPortCode");
                money = rs.getDouble("FRollBal");
                hmMoney.put(port, new Double(money));
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmMoney;
    }

    private HashMap getRecInterest(java.util.Date dDate, String portCodes) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        String port = "";
        double money;
        HashMap hmMoney = new HashMap();
        try {
            strSql = "select sum(FBal) as FRECLX,FPortCode from " +
                " (select sum(FVMoney) as FBal, FPortCode, 'Sec' as FRela from " +
                pub.yssGetTableName("tb_Data_SecRecPay") +
                " where FCheckState = 1 and FTransDate = " + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes +
                ") and FTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) +
                " and FSubTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FI_RecInterest) +
                " group by FPortCode " +
                " union " +
                " select sum(FMoney) as FBal,FPortCode, 'Cash' as FRela from " +
                pub.yssGetTableName("tb_Data_CashPayRec") +
                " where FCheckState = 1 and FTransDate = " + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes +
                ") and FTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec) +
                " and FSubTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest) +
                " group by FPortCode ) group by FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                port = rs.getString("FPortCode");
                money = rs.getDouble("FRECLX");
                hmMoney.put(port, new Double(money));
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmMoney;
    }

    public HashMap getFh(java.util.Date dDate, String sPortCodes) throws
        YssException { //获取分红
        String strSql = "";
        ResultSet rs = null;
        HashMap hmResult = new HashMap();
        double dTmp = 0;
        try {
            strSql = "select FPortCode, sum(FTotalCost) as FTotalCost from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FPortCode in (" + sPortCodes + ") and " +
                " FBargainDate = " + dbl.sqlDate(dDate) +
                " and FTradeTypeCode=" + dbl.sqlString("06") +
                " and FCheckState = 1 group by FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                hmResult.put(rs.getString("FPortCode"),
                             new Double(rs.getDouble("FTotalCost")));
            }
            dbl.closeResultSetFinal(rs);
            return hmResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private HashMap getGZ(String portCodes, java.util.Date dDate) throws //估值增值
        YssException {
        ResultSet rs = null;
        String strSql = "";
        String port = "";
        double money;
        HashMap hmMoney = new HashMap();
        try {
            strSql = "select FPortCode, sum(FVMoney) as FBal from " +
                pub.yssGetTableName("Tb_Data_SecRecPay") +
                " where FTransDate = " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 and FTsfTypeCode = '09' and FPortCode in (" +
                portCodes + ")" +
                " group by FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                port = rs.getString("FPortCode");
                money = rs.getDouble("FBal");
                hmMoney.put(port, new Double(money));
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmMoney;
    }

    private HashMap getCJSR(String portCodes, java.util.Date dDate) throws //差价收入
        YssException {
        ResultSet rs = null;
        String strSql = "";
        String port = "";
        double money;
        HashMap hmMoney = new HashMap();
        try {
            strSql =
                "select FPortCode, sum(FTotalCost - FVCost) as FMoney from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FBargainDate = " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 and FTradeTypeCode = '02' and FPortCode in (" +
                portCodes + ") group by FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                port = rs.getString("FPortCode");
                money = rs.getDouble("FMoney");
                hmMoney.put(port, new Double(money));
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmMoney;
    }

    public HashMap getInvestMoney(java.util.Date dDate, String sPortCodes) throws
        YssException { //获取运营收支
        String strSql = "";
        ResultSet rs = null;
        HashMap hmResult = new HashMap();
        double dTmp = 0;
        try {
            strSql = "select FPortCode, sum(FMoney*FInOut) as FMoney from " +
                pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " a left join (select FIVPayCatCode,(case when FPayType=0 then 1 else -1 end) as FInOut" +
                " from Tb_Base_InvestPayCat where FCheckState = 1) b" +
                " on a.FIVPayCatCode = b.FIVPayCatCode" +
                " where a.FPortCode in (" + sPortCodes + ") and " +
                " a.FTransDate = " + dbl.sqlDate(dDate) +
                " and a.FCheckState = 1 group by a.FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                hmResult.put(rs.getString("FPortCode"),
                             new Double(rs.getDouble("FMoney")));
            }
            dbl.closeResultSetFinal(rs);
            return hmResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private HashMap getIncome(String portCodes, java.util.Date dDate) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        String port = "";
        double money;
        HashMap hmMoney = new HashMap();
        try {
            strSql = "select sum(FIncome) as FIncome,FPortCode from " +
                " (select sum(FVMoney) as FIncome, FPortCode, 'Sec' as FRela from " +
                pub.yssGetTableName("tb_Data_SecRecPay") +
                " where FCheckState = 1 and FTransDate = " + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes +
                ") and FTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) +
                " and FSubTsfTypeCode = '1003'" + //'1003'债券利息
                " group by FPortCode " +
                " union select sum(FMoney) as FIncome,FPortCode, 'Cash' as FRela from " +
                pub.yssGetTableName("tb_Data_CashPayRec") +
                " where FCheckState = 1 and FTransDate = " + dbl.sqlDate(dDate) +
                " and FPortCode in (" + portCodes +
                ") and FTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) +
                " and FSubTsfTypeCode in ('02DE')" + //'1001'存款利息,'1004'证券商存款利息
                //存款利息收入从1001调整为02DE fazmm20071010
                " group by FPortCode ) group by FPortCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                port = rs.getString("FPortCode");
                money = rs.getDouble("FIncome");
                hmMoney.put(port, new Double(money));
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmMoney;
    }

}
