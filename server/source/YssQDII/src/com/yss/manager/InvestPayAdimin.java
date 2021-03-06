package com.yss.manager;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.operdata.*;
import com.yss.util.*;

public class InvestPayAdimin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    String insertNum = "";
    public String getInsertNum() {
        return insertNum;
    }

    public InvestPayAdimin() {
    }

    public void addList(InvestPayRecBean invest) {
        this.addList.add(invest);
    }

    public ArrayList getList() {
        return addList;
    }

    public void setList(ArrayList investList){
    	this.addList=investList;
    }
    /**
     * 重载的方法，增加对分析代码2、分析代码3的筛选
     * @throws YssException
     * MS00237 QDV4中保2009年02月05日01_A sj modified
     */
    public void inset() throws YssException {

    }

    /**
     * 重载的方法，只有对分析代码1的处理
     * @throws YssException
     * MS00237 QDV4中保2009年02月05日01_A sj modified
     */
    public void insert(java.util.Date beginDate, java.util.Date endDate,
                       String tsfType,
                       String subTsfType,
                       String iVPayCatCode, String port, String invMgr,
                       int datasource) throws
        YssException {
        insert(beginDate, endDate, tsfType, subTsfType, iVPayCatCode, port,
               invMgr, "", "", //只有对分析代码1的处理
               datasource,
               "","");  //add by fangjiang 2011.02.14 #2279   modyfy by zhouwei 20120401 增加关联类型
    }

    public void insert(java.util.Date transDate, String tsfType,
                       String subTsfType, String iVPayCatCode, String port,
                       String invMgr,
                       int datasource) throws YssException {
        insert(transDate, transDate, tsfType, subTsfType, iVPayCatCode, port,
               invMgr,
               datasource);
    }

    /**
     * 重载的方法，以便使旧有的调用能够继续运行，只包括投资经理
     * @param beginDate Date
     * @param endDate Date
     * @param tsfType String
     * @param subTsfType String
     * @param iVPayCatCode String
     * @param port String
     * @param invMgr String
     * @param datasource int
     * @return String
     * @throws YssException
     */
    public String getFNum(java.util.Date beginDate, java.util.Date endDate,
                          String tsfType,
                          String subTsfType,
                          String iVPayCatCode, String port, String invMgr,
                          int datasource) throws YssException {
        return getFNum(beginDate, endDate, tsfType, subTsfType, iVPayCatCode, port, invMgr, "", "", datasource, ""); //modify by fangjiang 2011.02.14 #2279
    }

    public String getFNum(java.util.Date beginDate, java.util.Date endDate,
                          String tsfType,
                          String subTsfType,
                          String iVPayCatCode, String port, String invMgr,
                          String analysisCode2, String analysisCode3, //MS00237 QDV4中保2009年02月05日01_A增加对分析代码2、3的处理
                          int datasource,
                          String curyCode) throws YssException { //modify by fangjiang 2011.02.14 #2279
        String strSql = "";
        String FNum = "";
        //InvestPayRecBean invest = null;
        //PreparedStatement pst = null;
        //Connection conn = dbl.loadConnection();
        //String sFNum = "";
        ResultSet rs = null;
        //boolean bTrans = false;
        int i = 0;
        try {
            strSql = "select FNUM from " +
                pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " where FTransDate between " + dbl.sqlDate(beginDate) +
                " and " +
                dbl.sqlDate(endDate) +
                ( (tsfType == null || tsfType.length() == 0) ? " " :
                 " and FTsfTypeCode = " + dbl.sqlString(tsfType)) +
                ( (subTsfType == null || subTsfType.length() == 0) ? " " :
                 " and FSubTsfTypeCode in (" +
                 operSql.sqlCodes(subTsfType) + ")") +
                ( (iVPayCatCode == null || iVPayCatCode.length() == 0) ? "" :
                 " and FIVPayCatCode in (" + operSql.sqlCodes(iVPayCatCode)) +
                ")" +
                ( (port == null || port.length() == 0) ? " " :
                 " and FPortCode in (" +
                 operSql.sqlCodes(port) + ")") +
                ( (invMgr == null || invMgr.trim().length() == 0) ? " " :
                 " and FAnalysisCode1 = " + dbl.sqlString(invMgr)) +
                //MS00237 QDV4中保2009年02月05日01_A增加对分析代码2、3的处理 sj modified----//
                ( (analysisCode2 == null || analysisCode2.trim().length() == 0) ? " " :
                 " and FanalysisCode2 = " + dbl.sqlString(analysisCode2)) +
                ( (analysisCode3 == null || analysisCode3.trim().length() == 0) ? " " :
                 " and FanalysisCode3 = " + dbl.sqlString(analysisCode3)) +
                //---------------------------------------------------------------------//
                /* ( (broker == null || broker.length() == 0) ? " " :
                  " and FAnalysisCode2 = " +
                  dbl.sqlString(broker)) +
                 ( (security == null || security.length() == 0) ? " " :
                  " and FSecurityCode = " +
                  dbl.sqlString(security)) +
                 ( (cury == null || cury.length() == 0) ? " " :
                  " and FCuryCode = " + dbl.sqlString(cury)) +*/
                " and FDataSource = " + datasource +
                " and FCuryCode = " + dbl.sqlString(curyCode) ; //add by fangjiang 2011.02.14 #2279
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                FNum += rs.getString("FNum") + ",";
            }
            if (FNum.length() > 0) {
                FNum = FNum.substring(0, FNum.length() - 1);
            }
            return FNum;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            //2008.06.05 蒋锦 修改 使用 closeResultSetFinal 代替原来的 closeConntion
            dbl.closeResultSetFinal(rs);
        }

    }

    public String buildWhereSql(java.util.Date beginDate, java.util.Date endDate,
                                String tsfType,
                                String subTsfType,
                                String iVPayCatCode, String port, String invMgr,
                                String analysisCode2, String analysisCode3, //MS00237 QDV4中保2009年02月05日01_A增加对分析代码2、3的处理
                                int datasource,
                                String curyCode,String relaType) { //modify by fangjiang 2011.02.14 #2279 modyfy by zhouwei 20120401 增加关联类型
        //edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B FDataOrigin = 0 表示为自动生成的数据
    	String sResult = " where 1=1 and FDataOrigin = 0 ";
        if (beginDate != null && endDate != null) {
            sResult += " and (FTransDate between " + dbl.sqlDate(beginDate) +
                " and " + dbl.sqlDate(endDate) + ")";
        }
        if (tsfType.length() > 0) {
            if (tsfType.indexOf(",") > 0) {
                sResult += " and FTsfTypeCode in (" + operSql.sqlCodes(tsfType) + //sj 参数引用错误 20080127 edit
                    ")";
            } else {
                sResult += " and FTsfTypeCode = " + dbl.sqlString(tsfType);
            }
        }
        if (subTsfType.length() > 0) {
            if (subTsfType.indexOf(",") > 0) {
                sResult += " and FSubTsfTypeCode in (" +
                    operSql.sqlCodes(subTsfType) + ")";
            } else if (subTsfType.indexOf("%") > 0) {
                sResult += " and FSubTsfTypeCode like " +
                    operSql.sqlCodes(subTsfType);
            } else {
                sResult += " and FSubTsfTypeCode = " +
                    dbl.sqlString(subTsfType);
            }
        }
        if (iVPayCatCode.length() > 0) {
            if (iVPayCatCode.indexOf(",") > 0) {
                sResult += " and FIVPayCatCode in (" +
                    operSql.sqlCodes(iVPayCatCode) + ")";
            } else {
                sResult += " and FIVPayCatCode = " + dbl.sqlString(iVPayCatCode);
            }
        }
        if (datasource > -1) {
            sResult += " and FDataSource = " + datasource;
        }
        if (port.length() > 0) {
            sResult += " and FPortCode in (" + operSql.sqlCodes(port) + ")";
        }
        if (invMgr.length() > 0) { //为可删除多种条件 sj edit 20080123
            if (invMgr.indexOf(",") > 0) {
                sResult += " and FAnalysisCode1 in (" + operSql.sqlCodes(invMgr) +
                    ")";
            } else {
                sResult += " and FAnalysisCode1 = " + dbl.sqlString(invMgr);
            }
        }
        //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码2、3处理 sj modified ------//
        if (analysisCode2.length() > 0) {
            if (analysisCode2.indexOf(",") > 0) { //为可删除多种条件
                sResult += " and FAnalysisCode2 in (" + operSql.sqlCodes(analysisCode2) +
                    ")";
            } else {
                sResult += " and FAnalysisCode2 = " + dbl.sqlString(analysisCode2);
            }
        }
        if (analysisCode3.length() > 0) {
            if (analysisCode3.indexOf(",") > 0) { //为可删除多种条件
                sResult += " and FAnalysisCode3 in (" + operSql.sqlCodes(analysisCode3) +
                    ")";
            } else {
                sResult += " and FAnalysisCode3 = " + dbl.sqlString(analysisCode3);
            }
        }
        //--------------------------------------------------------------------------//
        
        //add by fangjiang 2011.02.14 #2279
        if (curyCode.length() > 0) {
            if (curyCode.indexOf(",") > 0) { //为可删除多种条件
                sResult += " and FCuryCode in (" + operSql.sqlCodes(curyCode) +
                    ")";
            } else {
                sResult += " and FCuryCode = " + dbl.sqlString(curyCode);
            }
        }
        //-------------------
      //add by zhouwei 20120401 增加关联类型
        if(relaType!=null && relaType.length()>0){
        	if (relaType.indexOf(",") > 0) { //为可删除多种条件
                sResult += " and FRELATYPE in (" + operSql.sqlCodes(relaType) +
                    ")";
            } else {
                sResult += " and FRELATYPE = " + dbl.sqlString(relaType);
            }
        }else{
        	  sResult += " and FRELATYPE  is null ";
        }
        //----------------------end-------
        return sResult;
    }

    public void delete(java.util.Date beginDate, java.util.Date endDate,
                       String tsfType,
                       String subTsfType,
                       String iVPayCatCode, String port, String invMgr,
                       String analysisCode2, String analysisCode3, //MS00237 QDV4中保2009年02月05日01_A增加对分析代码2、3的处理
                       int datasource,
                       String curyCode,  //add by fangjiang 2011.02.14 #2279
                       String relaType//modyfy by zhouwei 20120401 增加关联类型
        ) throws
        YssException {
        String strSql = "";
        try {
            strSql = "delete from " +
                pub.yssGetTableName("Tb_Data_InvestPayRec") +
                this.buildWhereSql(beginDate, endDate, tsfType,
                                   subTsfType, iVPayCatCode,
                                   port, invMgr,
                                   analysisCode2, analysisCode3, //MS00237 QDV4中保2009年02月05日01_A增加对分析代码2、3的处理
                                   datasource,
                                   curyCode,relaType); //add by fangjiang 2011.02.14 #2279
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    public void insert(java.util.Date beginDate, java.util.Date endDate,
                       String tsfType,
                       String subTsfType,
                       String iVPayCatCode, String port, String invMgr,
                       String analysisCode2, String analysisCode3, //MS00237 QDV4中保2009年02月05日01_A增加对分析代码2、3的处理
                       int datasource,
                       String curyCode,String relaType) throws //modify by fangjiang 2011.02.14 #2279    modyfy by zhouwei 20120401 增加关联类型
        YssException {
        // boolean hasDel = del;
        String strSql = "";
        InvestPayRecBean invest = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        Connection conn = dbl.loadConnection();
        String sFNum = "";
        boolean bTrans = false;
        int i = 0;
        HashMap htDiffDate = new HashMap(); //根据交易日期不同取存不同的最大编号
        int iFNum = 0;
        try {
            //------------改变了删除方式 sj add 20080123------------------------//
            delete(beginDate, endDate, tsfType, subTsfType, iVPayCatCode, port,
                   invMgr,
                   analysisCode2, analysisCode3, //MS00237 QDV4中保2009年02月05日01_A增加对分析代码2、3的处理
                   datasource,
                   curyCode,relaType); //add by fangjiang 2011.02.14 #2279
            //------------------------------------------------------------------
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                "(FNum,FIVPayCatCode,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FTsfTypeCode" +
                ",FSubTsfTypeCode,FCURYCODE,FMoney,FBaseCuryRate,FBaseCuryMoney" +
                ",FPortCuryRate,FPortCuryMoney,FDataSource,FStockInd" +
                //---edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B start---//
                ",FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDataOrigin,FRELATYPE)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                //---edit by songjie 2012.03.30 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B end---//
//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            for (i = 0; i < this.addList.size(); i++) {
                invest = (InvestPayRecBean) addList.get(i);
                //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//                if (i == 0) {
//                    sFNum = getNum(invest);
//                    htDiffDate.put(invest.getTradeDate(), sFNum);
//                }
//                if (htDiffDate.get(invest.getTradeDate()) == null) {
//                    sFNum = getNum(invest);
//                    htDiffDate.put(invest.getTradeDate(), sFNum);
//                }
                //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
                //add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                sFNum = getNum();
                //如果金额全部为零时  就不进行保存
                // 8-2
                if (YssFun.roundIt(invest.getMoney(), 2) == 0 &&
                    YssFun.roundIt(invest.getBaseCuryMoney(), 2) == 0 &&
                    YssFun.roundIt(invest.getPortCuryMoney(), 2) == 0) {
                    continue;
                }
                /* sFNum = "IPR" +
                       YssFun.formatDatetime(invest.getTradeDate()).
                       substring(0, 8) +
                       dbFun.getNextInnerCode(pub.yssGetTableName(
                       "Tb_Data_InvestPayRec"),
                                              dbl.sqlRight("FNUM", 9), "000000001",
                                              " where FTransDate = " +
                                              dbl.sqlDate(invest.getTradeDate())); */
                //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//                if (sFNum.trim().length() > 0 && sFNum.length() > 11) {
//                    iFNum = YssFun.toInt(YssFun.right(sFNum, 9));
//                    sFNum = YssFun.left(sFNum, 11);
//                    iFNum++;
//                    sFNum += YssFun.formatNumber(iFNum, "000000000");
//                }
                //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
                this.insertNum = sFNum;

                yssPst.setString(1, sFNum);
                yssPst.setString(2, invest.getFIVPayCatCode());
                yssPst.setDate(3, YssFun.toSqlDate(invest.getTradeDate()));
                yssPst.setString(4, invest.getPortCode());
                yssPst.setString(5, invest.getAnalysisCode1());
                yssPst.setString(6, invest.getAnalysisCode2() == null || invest.getAnalysisCode2().trim().length() == 0 ? " " : invest.getAnalysisCode2()); //sj modified 20081218 MS00108 增加对分析代码2的处理。
                //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
                yssPst.setString(7, invest.getAnalysisCode3() == null || invest.getAnalysisCode3().trim().length() == 0 ? " " : invest.getAnalysisCode3());
                //--------------------------------------------------------------------------------//
                yssPst.setString(8, invest.getTsftTypeCode());
                yssPst.setString(9, invest.getSubTsfTypeCode());
                yssPst.setString(10, invest.getCuryCode());
                yssPst.setDouble(11, YssFun.roundIt(invest.getMoney(), 2));
                yssPst.setDouble(12,
                              YssFun.roundIt(invest.getBaseCuryRate(), 12));
                yssPst.setDouble(13,
                              YssFun.roundIt(invest.getBaseCuryMoney(), 2));
                yssPst.setDouble(14,
                              YssFun.roundIt(invest.getPortCuryRate(), 12));
                yssPst.setDouble(15,
                              YssFun.roundIt(invest.getPortCuryMoney(), 2));
                yssPst.setInt(16,
                           0);
                yssPst.setInt(17,
                           0);
                yssPst.setInt(18, invest.checkStateId);//fanghaoln 20090714 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能

                yssPst.setString(19, pub.getUserCode());
                yssPst.setString(20, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(21, pub.getUserCode());
                yssPst.setString(22, YssFun.formatDatetime(new java.util.Date()));   
                yssPst.setString(23, "0");
                yssPst.setString(24, invest.getRelaType());
                conn.setAutoCommit(false);
                bTrans = true;
                yssPst.executeUpdate();
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("系统保存运营应收应付金额时出现异常!" + "\n", e); //by 曹丞 2009.02.01 保存运营应收应付金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeStatementFinal(yssPst);
        }

    }

    public void insert(java.util.Date beginDate, java.util.Date endDate,
                       String port, String invMgr,
                       int datasource) throws
        YssException {
        // boolean hasDel = del;
        String strSql = "";
        InvestPayRecBean invest = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        Connection conn = dbl.loadConnection();
        String sFNum = "";
        boolean bTrans = false;
        int i = 0;
        int iFNum = 0;
        HashMap htDiffDate = new HashMap();
        try {
            //if(!hasDel)//判断是否以删除过当天的记录,避免多条插入时反复删除 20070806
            //{
            strSql = "delete from " +
                pub.yssGetTableName("Tb_Data_InvestPayRec") +
                " where FTransDate between " + dbl.sqlDate(beginDate) +
                " and " +
                dbl.sqlDate(endDate) +
                " and FTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Income) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_Fee) + ") " +

                " and FSubTsfTypeCode in (" +
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_IV_Income) + "," +
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_IV_Fee) + ")" +
                ( (port == null || port.length() == 0) ? " " :
                 " and FPortCode in (" +
                 operSql.sqlCodes(port) + ")") +
                ( (invMgr == null || invMgr.trim().length() == 0) ? " " :
                 " and FAnalysisCode1 = " + dbl.sqlString(invMgr)) +
                /* ( (broker == null || broker.length() == 0) ? " " :
                  " and FAnalysisCode2 = " +
                  dbl.sqlString(broker)) +
                 ( (security == null || security.length() == 0) ? " " :
                  " and FSecurityCode = " +
                  dbl.sqlString(security)) +
                 ( (cury == null || cury.length() == 0) ? " " :
                  " and FCuryCode = " + dbl.sqlString(cury)) +*/
                " and FDataSource = " + datasource;
            dbl.executeSql(strSql);
            // hasDel = true;
            // }

            strSql = "insert into " + pub.yssGetTableName("Tb_Data_InvestPayRec") +
                "(FNum,FIVPayCatCode,FTransDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FTsfTypeCode" +
                ",FSubTsfTypeCode,FCURYCODE,FMoney,FBaseCuryRate,FBaseCuryMoney" +
                ",FPortCuryRate,FPortCuryMoney,FDataSource,FStockInd" +
                ",FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            for (i = 0; i < this.addList.size(); i++) {
                invest = (InvestPayRecBean) addList.get(i);
                //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//                if (i == 0) {
//                    sFNum = getNum(invest); //只获取一次编号
//                    htDiffDate.put(invest.getTradeDate(), sFNum);
//                }
//                if (htDiffDate.get(invest.getTradeDate()) == null) {
//                    sFNum = getNum(invest); //只获取一次编号
//                    htDiffDate.put(invest.getTradeDate(), sFNum);
//                }
                //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
                //add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                sFNum = getNum();
                //如果金额全部为零时  就不进行保存
                // 8-2
                if (invest.getMoney() == 0 &&
                    invest.getBaseCuryMoney() == 0 &&
                    invest.getPortCuryMoney() == 0) {
                    continue;
                }
                /*sFNum = "IPR" +
                      YssFun.formatDatetime(invest.getTradeDate()).
                      substring(0, 8) +
                      dbFun.getNextInnerCode(pub.yssGetTableName(
                      "Tb_Data_InvestPayRec"),
                                             dbl.sqlRight("FNUM", 9), "000000001",
                                             " where FTransDate = " +
                                             dbl.sqlDate(invest.getTradeDate()));*/
                //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//                if (sFNum.trim().length() > 0 && sFNum.length() > 11) {
//                    iFNum = YssFun.toInt(YssFun.right(sFNum, 9));
//                    sFNum = YssFun.left(sFNum, 11);
//                    iFNum++;
//                    sFNum += YssFun.formatNumber(iFNum, "000000000");
//                }
                //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
                yssPst.setString(1, sFNum);
                yssPst.setString(2, invest.getFIVPayCatCode());
                yssPst.setDate(3, YssFun.toSqlDate(invest.getTradeDate()));
                yssPst.setString(4, invest.getPortCode());
                yssPst.setString(5, invest.getAnalysisCode1());
                yssPst.setString(6, " ");
                yssPst.setString(7, " ");
                yssPst.setString(8, invest.getTsftTypeCode());
                yssPst.setString(9, invest.getSubTsfTypeCode());
                yssPst.setString(10, invest.getCuryCode());
                yssPst.setDouble(11, YssFun.roundIt(invest.getMoney(), 2));
                yssPst.setDouble(12,
                              YssFun.roundIt(invest.getBaseCuryRate(), 12));
                yssPst.setDouble(13,
                              YssFun.roundIt(invest.getBaseCuryMoney(), 2));
                yssPst.setDouble(14,
                              YssFun.roundIt(invest.getPortCuryRate(), 12));
                yssPst.setDouble(15,
                              YssFun.roundIt(invest.getPortCuryMoney(), 2));
                yssPst.setInt(16,
                           0);
                yssPst.setInt(17,
                           0);
                yssPst.setInt(18, invest.checkStateId);//fanghaoln 20090714 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能

                yssPst.setString(19, pub.getUserCode());
                yssPst.setString(20, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(21, pub.getUserCode());
                yssPst.setString(22, YssFun.formatDatetime(new java.util.Date()));
                conn.setAutoCommit(false);
                bTrans = true;
                yssPst.executeUpdate();
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(yssPst);
        }

    }

    //增加一个获取当前最大编号的方法 by liyu
    //edit by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
    public String getNum() throws YssException {
    	//---delete by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//        String sFNum = "";
//        try {
//            sFNum = "IPR" +
//                YssFun.formatDatetime(invest.getTradeDate()).
//                substring(0, 8) +
//                dbFun.getNextInnerCode(pub.yssGetTableName(
//                    "Tb_Data_InvestPayRec"),
//                                       dbl.sqlRight("FNUM", 9), "000000001",
//                                       " where FTransDate = " +
//                                       dbl.sqlDate(invest.getTradeDate()));
//            return sFNum;
        //---delete by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
    	//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
		String num = "";
		String strSql = "";
		ResultSet rs = null;
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		int maxNum = 0;
		try{
			conn.setAutoCommit(false);
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_INVESTPAYREC")){
				strSql = " select max(FNum) as FNum from " + pub.yssGetTableName("Tb_Data_INVESTPAYREC") + 
				" where SUBSTR(FNum,0,3) <> 'IPR' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_INVESTPAYREC " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
				dbl.executeSql(strSql);
			}
			
			strSql = " select trim(to_char(SEQ_" + pub.getPrefixTB() + 
			"_Data_INVESTPAYREC.NextVal,'00000000000000000000')) as FNum from dual ";
			rs = dbl.openResultSet(strSql);
			if(rs.next()){
    			num = rs.getString("FNum");
    		}
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			
			return num;
		}catch(Exception e){
			throw new YssException("计算编号出错", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
		//---add by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
    }

}
