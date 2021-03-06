package com.yss.manager;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.storagemanage.*;
import com.yss.util.*;

public class InvestStorageAdmin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    public InvestStorageAdmin() {
    }

    public void addList(InvestBean investstorage) {
        this.addList.add(investstorage);
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports
        ) throws YssException {
        this.insert(dStartDate, dEndDate, ports, "", "", "", "");
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, String invMgr,
                       String broker, String ivpaycatcode, String cury
        ) throws
        YssException {
        String strSql = "";
        InvestBean investstorage = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
//     boolean bTrans = false;
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        int i = 0;
        try {
//       conn.setAutoCommit(false);
//       bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Invest") +
                " where FStorageDate between " + dbl.sqlDate(dStartDate) +
                " and " + dbl.sqlDate(dEndDate) +
                ( (ports == null || ports.length() == 0) ? " " :
                 " and FPortCode in (" + ports + ")") +
                ( (invMgr == null || invMgr.length() == 0) ? " " :
                 " and FAnalysisCode1 = " + dbl.sqlString(invMgr)) +
                ( (broker == null || broker.length() == 0) ? " " :
                 " and FAnalysisCode2 = " +
                 dbl.sqlString(broker)) +
                ( (ivpaycatcode == null || ivpaycatcode.length() == 0) ? " " :
                 " and FIVPayCatCode = " +
                 dbl.sqlString(ivpaycatcode)) +
                ( (cury == null || cury.length() == 0) ? " " :
                 " and FCuryCode = " + dbl.sqlString(cury));
//               " and FStorageInd <> 2 ";//MS00308 QDV4赢时胜上海2009年3月11日01_B 将所有状态的库存数据都删除 modify by shenjie
            if (dStartDate != null && dEndDate != null && dStartDate.equals(dEndDate)) {
                strSql = strSql + " and FYearMonth<>'" +
                    new Integer(YssFun.getYear(dStartDate)).toString() + "00'";
            }
            dbl.executeSql(strSql);
            strSql = "insert into " +
                pub.yssGetTableName("Tb_Stock_Invest") +
                "(FIVPayCatCode,FYearMonth, FStorageDate, FPortCode, FCuryCode, FBal, " +
                " FPortCuryRate, FPortCuryBal, FBaseCuryRate, FBaseCuryBal, FAnalysisCode1, FAnalysisCode2, " +
                " FAnalysisCode3, FStorageInd,  FCheckState, FCreator, FCreateTime,FCheckUser,FCheckTime,fattrclscode) " + //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 ----//
//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            for (i = 0; i < this.addList.size(); i++) {
                investstorage = (InvestBean) addList.get(i);
                yssPst.setString(1, investstorage.getStrIvPayCatCode());
                yssPst.setString(2,
                              YssFun.formatDate(investstorage.getStrStorageDate(), "yyyyMM"));
                yssPst.setDate(3,
                            YssFun.toSqlDate(YssFun.toDate(investstorage.getStrStorageDate())));
                yssPst.setString(4, investstorage.getStrPortCode());
                yssPst.setString(5, investstorage.getStrCuryCode());
                yssPst.setDouble(6, YssFun.toDouble(investstorage.getStrAccBalance()));
                yssPst.setDouble(7, YssFun.toDouble(investstorage.getStrPortCuryRate())); //dPortRate
                yssPst.setDouble(8, YssFun.toDouble(investstorage.getStrPortCuryBal()));
                yssPst.setDouble(9, YssFun.toDouble(investstorage.getStrBaseCuryRate())); //dBaseRate
                yssPst.setDouble(10, YssFun.toDouble(investstorage.getStrBaseCuryBal()));
                yssPst.setString(11, investstorage.getStrFAnalysisCode1());
                yssPst.setString(12, investstorage.getStrFAnalysisCode2());
                yssPst.setString(13, investstorage.getStrFAnalysisCode3());
                yssPst.setInt(14, 0);
                yssPst.setInt(15, 1);
                yssPst.setString(16, pub.getUserCode());
                yssPst.setString(17, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(18, pub.getUserCode());
                yssPst.setString(19, YssFun.formatDatetime(new java.util.Date()));
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao 2011.01.16 ----//
                yssPst.setString(20, (investstorage.getStrAttrClsCode().trim().length()!=0)?investstorage.getStrAttrClsCode():" ");
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类 add by jiangshichao end------------//  
                yssPst.executeUpdate();

            }
//       conn.commit();
//       bTrans = false;
//       conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("系统保存运营库存金额时出现异常!" + "\n", e); //by 曹丞 2009.02.01 保存运营库存金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeStatementFinal(yssPst);
        }

    }

}
