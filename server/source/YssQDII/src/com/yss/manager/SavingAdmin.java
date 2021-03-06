package com.yss.manager;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.cashmanage.*;
import com.yss.util.*;

public class SavingAdmin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    String insertSavingNum = "";
    public String getInsertSavingNum() {
        return insertSavingNum;
    }

    public SavingAdmin() {
    }

    public void addList(SavingBean inacc) {
        this.addList.add(inacc);
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String port
        ) throws YssException {
    }

    public String buildWhereSql(String sInAccNums, java.util.Date beginSavDate,
                                java.util.Date endSavDate,
                                String sCashAccCode, String sPortCode,
                                String sAnalysisCode1, String sAnalysisCode2,
                                String sAnalysisCode3, java.util.Date matureDate,
                                int iDsInd,
                                String sOutCashAccCode, String sOutPortCode,
                                String sOutAnalysisCode1,
                                String sOutAnalysisCode2,
                                String sOutAnalysisCode3) {
        String sResult = " where 1=1 ";
        if (sInAccNums.length() > 0) {
            sResult += " and FNum in(" + operSql.sqlCodes(sInAccNums) + ")";
        }
        if (beginSavDate != null && endSavDate != null) {
            sResult += " and (FSavingDate between " + dbl.sqlDate(beginSavDate) +
                " and " + dbl.sqlDate(endSavDate) + ")";
        }
        if (sCashAccCode.length() > 0) {
            sResult += " and FCashAccCode = " + dbl.sqlString(sCashAccCode);
        }
        if (sPortCode.length() > 0) {
            sResult += " and FPortCode = " + dbl.sqlString(sPortCode);
        }
        if (sAnalysisCode1.length() > 0) {
            sResult += " and FAnalysisCode1 = " + dbl.sqlString(sAnalysisCode1);
        }
        if (sAnalysisCode2.length() > 0) {
            sResult += " and FAnalysisCode2 = " + dbl.sqlString(sAnalysisCode2);
        }
        if (sAnalysisCode3.length() > 0) {
            sResult += " and FAnalysisCode3 = " + dbl.sqlString(sAnalysisCode3);
        }
        if (matureDate != null) {
            sResult += " and FMatureDate = " + dbl.sqlDate(matureDate);
        }
        if (iDsInd > -1) {
            sResult += " and FDataSource = " + iDsInd;
        }

        if (sOutCashAccCode.length() > 0) {
            sResult += " and FOutCashAccCode = " + dbl.sqlString(sOutCashAccCode);
        }
        if (sOutPortCode.length() > 0) {
            sResult += " and FOutPortCode = " + dbl.sqlString(sOutPortCode);
        }
        if (sOutAnalysisCode1.length() > 0) {
            sResult += " and FOutAnalysisCode1 = " +
                dbl.sqlString(sOutAnalysisCode1);
        }
        if (sOutAnalysisCode2.length() > 0) {
            sResult += " and FOutAnalysisCode2 = " +
                dbl.sqlString(sOutAnalysisCode2);
        }
        if (sOutAnalysisCode3.length() > 0) {
            sResult += " and FOutAnalysisCode3 = " +
                dbl.sqlString(sOutAnalysisCode3);
        }

        return sResult;
    }

    public String loadTransNums(java.util.Date beginSavDate,
                                java.util.Date endSavDate,
                                String sOutCashAccCode, String sOutPortCode,
                                String sOutAnalysisCode1,
                                String sOutAnalysisCode2,
                                String sOutAnalysisCode3, int iDsInd) throws
        YssException {
        return this.loadTransNums("", beginSavDate, endSavDate, "", "", "", "", "", null, iDsInd, sOutCashAccCode, sOutPortCode,
                                  sOutAnalysisCode1, sOutAnalysisCode2,
                                  sOutAnalysisCode3);
    }

    public String loadTransNums(java.util.Date beginSavDate,
                                java.util.Date endSavDate,
                                String sOutCashAccCode, String sOutPortCode,
                                String sOutAnalysisCode1,
                                String sOutAnalysisCode2,
                                String sOutAnalysisCode3) throws
        YssException {
        return this.loadTransNums(beginSavDate,
                                  endSavDate,
                                  sOutCashAccCode, sOutPortCode,
                                  sOutAnalysisCode1, sOutAnalysisCode2,
                                  sOutAnalysisCode3, 0);
    }

    public String loadTransNums(String sInAccNum, java.util.Date beginSavDate,
                                java.util.Date endSavDate,
                                String sCashAccCode, String sPortCode,
                                String sAnalysisCode1, String sAnalysisCode2,
                                String sAnalysisCode3, java.util.Date matureDate,
                                int iDsInd,
                                String sOutCashAccCode, String sOutPortCode,
                                String sOutAnalysisCode1,
                                String sOutAnalysisCode2,
                                String sOutAnalysisCode3) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String sResult = "";
        try {
            strSql = "select FNum from " +
                pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                " a join (select FInAccNum, FCashAccCode as FOutCashAccCode,FPortCode as FOutPortCode,FAnalysisCode1 as FOutAnalysisCode1," +
                " FAnalysisCode2 as FOutAnalysisCode2,FAnalysisCode3 as FOutAnalysisCode3 from " +
                pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
                ") b on a.FNum = b.FInAccNum " +
                this.buildWhereSql(sInAccNum, beginSavDate, endSavDate,
                                   sCashAccCode,
                                   sPortCode, sAnalysisCode1, sAnalysisCode2,
                                   sAnalysisCode3,
                                   matureDate, iDsInd, sOutCashAccCode,
                                   sOutPortCode, sOutAnalysisCode1,
                                   sOutAnalysisCode2, sOutAnalysisCode3);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult += rs.getString("FNum") + ",";
            }

//         nums = operSql.sqlCodes(nums);
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void insert() throws YssException {
        insert("", null, null, "", "", "", "", "", null, -1, "",
               "",
               "", "", "", false);
    }

    public void insert(java.util.Date beginSavDate,
                       java.util.Date endSavDate,
                       String sOutCashAccCode, String sOutPortCode,
                       String sOutAnalysisCode1, String sOutAnalysisCode2,
                       String sOutAnalysisCode3, int iDsInd) throws YssException {
        insert("", beginSavDate, endSavDate, "", "", "", "", "", null, iDsInd, sOutCashAccCode,
               sOutPortCode,
               sOutAnalysisCode1, sOutAnalysisCode2, sOutAnalysisCode3, true);
    }

    public void insert(java.util.Date beginSavDate,
                       java.util.Date endSavDate,
                       String sOutCashAccCode, String sOutPortCode,
                       String sOutAnalysisCode1, String sOutAnalysisCode2,
                       String sOutAnalysisCode3) throws YssException {
        insert("", beginSavDate, endSavDate, "", "", "", "", "", null, 0, sOutCashAccCode,
               sOutPortCode,
               sOutAnalysisCode1, sOutAnalysisCode2, sOutAnalysisCode3, true);
    }

    public void insert(String sInAccNum, java.util.Date beginSavDate,
                       java.util.Date endSavDate,
                       String sCashAccCode, String sPortCode,
                       String sAnalysisCode1, String sAnalysisCode2,
                       String sAnalysisCode3, java.util.Date matureDate,
                       int iDsInd) throws YssException {
        insert(sInAccNum, beginSavDate, endSavDate, sCashAccCode, sPortCode,
               sAnalysisCode1,
               sAnalysisCode2, sAnalysisCode3, matureDate, iDsInd, "", "",
               "", "", "", true);
    }

    public void insert(String sInAccNum, java.util.Date beginSavDate,
                       java.util.Date endSavDate,
                       String sCashAccCode, String sPortCode,
                       String sAnalysisCode1, String sAnalysisCode2,
                       String sAnalysisCode3, java.util.Date matureDate) throws
        YssException {
        insert(sInAccNum, beginSavDate, endSavDate, sCashAccCode, sPortCode,
               sAnalysisCode1,
               sAnalysisCode2, sAnalysisCode3, matureDate, 0, "", "",
               "", "", "", true);
    }

    public void delete(java.util.Date beginSavDate,
                       java.util.Date endSavDate,
                       String sOutCashAccCode, String sOutPortCode,
                       String sOutAnalysisCode1, String sOutAnalysisCode2,
                       String sOutAnalysisCode3) throws YssException {
        delete("", beginSavDate, endSavDate, "", "", "", "", "", null, 0, sOutCashAccCode,
               sOutPortCode,
               sOutAnalysisCode1, sOutAnalysisCode2, sOutAnalysisCode3);
    }

    public void delete(String sInAccNum, java.util.Date beginSavDate,
                       java.util.Date endSavDate,
                       String sCashAccCode, String sPortCode,
                       String sAnalysisCode1, String sAnalysisCode2,
                       String sAnalysisCode3, java.util.Date matureDate,
                       int iDsInd,
                       String sOutCashAccCode, String sOutPortCode,
                       String sOutAnalysisCode1, String sOutAnalysisCode2,
                       String sOutAnalysisCode3) throws YssException {
        String sNums = "";
        String strSql = "";
        try {
            sNums = loadTransNums(sInAccNum, beginSavDate, endSavDate,
                                  sCashAccCode,
                                  sPortCode, sAnalysisCode1, sAnalysisCode2,
                                  sAnalysisCode3,
                                  matureDate, iDsInd, sOutCashAccCode,
                                  sOutPortCode,
                                  sOutAnalysisCode1,
                                  sOutAnalysisCode2, sOutAnalysisCode3);
            if (sNums.length() > 0) {
                sNums = operSql.sqlCodes(sNums);
                strSql = "delete from " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                    " where FNum in (" + sNums + ")";
                dbl.executeSql(strSql);

                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
                    " where FInAccNum in (" + sNums + ")";
                dbl.executeSql(strSql);
            }
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    public void insert(String sInAccNum, java.util.Date beginSavDate,
                       java.util.Date endSavDate,
                       String sCashAccCode, String sPortCode,
                       String sAnalysisCode1, String sAnalysisCode2,
                       String sAnalysisCode3, java.util.Date matureDate,
                       int iDsInd,
                       String sOutCashAccCode, String sOutPortCode,
                       String sOutAnalysisCode1, String sOutAnalysisCode2,
                       String sOutAnalysisCode3, boolean bAutoDel) throws YssException {
        String strSql = "";
        SavingBean inAcc = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
//        PreparedStatement pstout = null;
        YssPreparedStatement yssPstOut = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        //java.sql.Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        ResultSet rs = null;
        int i = 0;
        String sNums = "";
        String sTmpNum = "";
        int iTmpNum = 0;
        HashMap htDiffDate = new HashMap();
        try {

            if (bAutoDel) {
                delete(sInAccNum, beginSavDate, endSavDate,
                       sCashAccCode,
                       sPortCode, sAnalysisCode1, sAnalysisCode2,
                       sAnalysisCode3,
                       matureDate, iDsInd, sOutCashAccCode,
                       sOutPortCode,
                       sOutAnalysisCode1,
                       sOutAnalysisCode2, sOutAnalysisCode3);
            }
//            pst = conn.prepareStatement(
//                "insert into " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
//                " (FNum,FCashAccCode,FDepDurCode,FSavingType,FSavingDate,FSavingTime," +
//                " FMatureDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FTransNum,FInMoney,FRecInterest,FInterestAccCode," +
//                " FBaseCuryRate,FPortCuryRate,FAvgBaseCuryRate,FAvgPortCuryRate,FDesc,FFormulaCode,FRoundCode,FCheckState,FCreator,FCreateTime,FDataSource)" +
//                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            yssPst = dbl.getYssPreparedStatement(
                "insert into " + pub.yssGetTableName("Tb_Cash_SavingInAcc") +
                " (FNum,FCashAccCode,FDepDurCode,FSavingType,FSavingDate,FSavingTime," +
                " FMatureDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FTransNum,FInMoney,FRecInterest,FInterestAccCode," +
                " FBaseCuryRate,FPortCuryRate,FAvgBaseCuryRate,FAvgPortCuryRate,FDesc,FFormulaCode,FRoundCode,FCheckState,FCreator,FCreateTime,FDataSource)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
//            pstout = conn.prepareStatement(
//                "insert into " + pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
//                "(FNum, FInAccNum, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FCashAccCode," +
//                "FOutMoney, FCheckState, FCreator, FCreateTime,FDesc) " +
//                " values (?,?,?,?,?,?,?,?,?,?,?,?)");
            
            yssPstOut = dbl.getYssPreparedStatement(
                "insert into " + pub.yssGetTableName("Tb_Cash_SavingOutAcc") +
                "(FNum, FInAccNum, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3, FCashAccCode," +
                "FOutMoney, FCheckState, FCreator, FCreateTime,FDesc) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?)");
            
            for (i = 0; i < this.addList.size(); i++) {
                inAcc = (SavingBean) addList.get(i); //流入帐户
                ArrayList alloutacc = inAcc.getAllOutAcc(); //获得所有流出帐户
                if (i == 0) {
                    sTmpNum = "CA" +
                        YssFun.formatDate(inAcc.getSavingDate(), "yyyyMMdd") +
                        dbFun.getNextInnerCode(pub.yssGetTableName(
                            "Tb_Cash_SavingInAcc"),
                                               dbl.sqlRight("FNUM", 6),
                                               "000001");
                    htDiffDate.put(inAcc.getSavingDate(), sTmpNum);
                } //这里只取一次最大编号
                if (htDiffDate.get(inAcc.getSavingDate()) == null) {
                    sTmpNum = "CA" +
                        YssFun.formatDate(inAcc.getSavingDate(), "yyyyMMdd") +
                        dbFun.getNextInnerCode(pub.yssGetTableName(
                            "Tb_Cash_SavingInAcc"),
                                               dbl.sqlRight("FNUM", 6),
                                               "000001");
                    htDiffDate.put(inAcc.getSavingDate(), sTmpNum);
                }
                if (sTmpNum.trim().length() > 0 && sTmpNum.length() > 10) {
                    iTmpNum = YssFun.toInt(YssFun.right(sTmpNum, 6));
                    sTmpNum = YssFun.left(sTmpNum, 10);
                    iTmpNum++;
                    sTmpNum += YssFun.formatNumber(iTmpNum, "000000");
                }
                this.insertSavingNum = sTmpNum;
                yssPst.setString(1, sTmpNum);
                yssPst.setString(2, inAcc.getCashAccCode());
                yssPst.setString(3, inAcc.getDepDurCode());
                yssPst.setString(4, inAcc.getSavingType());
                yssPst.setDate(5, YssFun.toSqlDate(inAcc.getSavingDate()));
                yssPst.setString(6, inAcc.getSavingTime());
                yssPst.setDate(7, YssFun.toSqlDate(inAcc.getMatureDate()));
                yssPst.setString(8, inAcc.getPortCode());
                yssPst.setString(9, inAcc.getInvMgrCode());
                yssPst.setString(10, inAcc.getCatCode());
                yssPst.setString(11, " ");
                yssPst.setString(12,
                              inAcc.getTransNum().equals("") ? " " :
                              inAcc.getTransNum()); //资金调拨编号
                //pst.setString(13, inAcc.getStrLxTransNum()); //利息资金调拨编号
                yssPst.setDouble(13, inAcc.getInMoney());
                yssPst.setDouble(14, inAcc.getRecInterest());
                yssPst.setString(15, inAcc.getInterestAccCode());
                yssPst.setDouble(16, inAcc.getBaseCuryRate());
                yssPst.setDouble(17, inAcc.getPortCuryRate());
                yssPst.setDouble(18, inAcc.getAvgBaseCuryRate());
                yssPst.setDouble(19, inAcc.getAvgPortCuryRate());
                yssPst.setString(20, inAcc.getDesc());
                yssPst.setString(21, inAcc.getFormulaCode());
                yssPst.setString(22, inAcc.getRoundCode());
                yssPst.setInt(23, inAcc.checkStateId);
                yssPst.setString(24, pub.getUserCode());
                yssPst.setString(25, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setInt(26, inAcc.getDataSource());
                yssPst.executeUpdate();

                for (int j = 0; j < alloutacc.size(); j++) {
                    SavingOutAccBean outacc = (SavingOutAccBean) alloutacc.get(j); //流出帐户
                    String sNum = YssFun.formatNumber(j + 1, "00000");
                    yssPstOut.setString(1, sNum);
                    yssPstOut.setString(2, sTmpNum); //改成sTmpNum
                    yssPstOut.setString(3, outacc.getPortCode());
                    yssPstOut.setString(4, outacc.getInvMgrCode());
                    yssPstOut.setString(5, outacc.getCatCode());
                    yssPstOut.setString(6,
                                     (outacc.getBrokerCode() == null ||
                                      outacc.getBrokerCode().equals("")) ? " " :
                                     outacc.getBrokerCode());
                    yssPstOut.setString(7, outacc.getCashAccCode());
                    yssPstOut.setDouble(8, outacc.getOutMoney()); //应该和流入金额一样 到期的时候
                    yssPstOut.setInt(9, outacc.checkStateId);
                    yssPstOut.setString(10, pub.getUserCode());
                    yssPstOut.setString(11, YssFun.formatDatetime(new java.util.Date()));
                    yssPstOut.setString(12, outacc.getDesc());
                    yssPstOut.executeUpdate();
                }

            }
        } catch (Exception e) {
            throw new YssException("系统保存存款金额时出现异常!" + "\n", e); //by 曹丞 2009.02.01 保存存款金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeStatementFinal(yssPst);
            dbl.closeStatementFinal(yssPstOut);
        }
    }

}
