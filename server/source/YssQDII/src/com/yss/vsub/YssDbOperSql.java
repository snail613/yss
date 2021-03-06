package com.yss.vsub;

import com.yss.util.*;
import com.yss.dsub.*;
import java.sql.*;
import java.util.ArrayList;

import com.yss.main.orderadmin.SimpleBean;

public class YssDbOperSql {
    private DbBase dbl;
    private YssPub pub = null; //全局变量
    private YssDbFun dbFun = null;
    private YssDbOperSql operSql = null;

    public YssDbOperSql(YssPub ysspub) {
        setYssPub(ysspub);
    }

    public void setYssPub(YssPub ysspub) {
        pub = ysspub;
        dbl = ysspub.getDbLink();
        dbFun = new YssDbFun(ysspub);
    }

    /**
     * 获取分析代码的类型。
     * @param analy String
     * @param storageType String
     * @throws YssException
     * @throws SQLException
     * @return String
     */
    public String storageAnalysisType(String analy, String storageType) throws YssException, SQLException {
        String reStr = "";
        ResultSet rs = null;
        try {
            String strSql = " select " + analy + " from " +
                pub.yssGetTableName("Tb_Para_StorageCfg") +
                " where FStorageType = " + dbl.sqlString(storageType) + " and FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                if (rs.getString(analy) != null && rs.getString(analy).length() != 0) {
                    reStr = rs.getString(analy);
                }
            }
        } catch (Exception e) {

        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return reStr;
    }

    //获取是否有分析代码；杨
    public boolean storageAnalysis(String analy, String storageType) throws YssException,
        SQLException {
        boolean bReturn = false;
        ResultSet rs = null;
        try {
            String strSql = " select " + analy + " from " +
                pub.yssGetTableName("Tb_Para_StorageCfg") +
                " where FStorageType = " + dbl.sqlString(storageType) + " and FCheckState = 1"; //sj modify 20071008 只查询已审核的数据
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                if (rs.getString(analy) != null && rs.getString(analy).length() != 0) {
                    bReturn = true;
                }
            }
            return bReturn;
        } catch (Exception e) {
            throw new YssException("获取库存配置信息出错", e);
        } finally {
        	//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        	if(rs != null){
        		rs.close();
            	dbl.closeResultSetFinal(rs);
        	}
        	//---end---
        }
    }

    //获取前一日的库存日期，去掉期初的部分，适用表Tb_Stock_Cash,Tb_Stock_Security
    public String sqlStoragEve(java.util.Date dDate) {
        String sResult = "";
        if (YssFun.formatDate(dDate, "MMdd").equalsIgnoreCase("0101")) {
            sResult = " FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00");
        } else if (YssFun.formatDate(dDate, "MMdd").equalsIgnoreCase("0102")) {
            sResult = " FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1)) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00");
        } else {
            sResult = " FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1));
        }
        return sResult;
    }

    //获取当日的库存日期，去掉期初的部分，适用表Tb_Stock_Cash,Tb_Stock_Security
    public String sqlStorageDate(java.util.Date dDate) {
        String sResult = "";
        sResult = " FStorageDate = " + dbl.sqlDate(dDate) +
            " and FYearMonth = " +
            dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM"));
        return sResult;
    }

    //获取小于等于 dDate 的最大日期的库存
    public String sqlStock(java.util.Date dDate, String tableName, int i) throws
        YssException,
        SQLException {
        String sResult = "";
        ResultSet rs = null;
        if (i == 0) { //不取前一天
            sResult = " select * from (select * from " +
                pub.yssGetTableName(tableName) +
                " where FStorageDate = (select max(FStorageDate) from " +
                pub.yssGetTableName(tableName) +
                " where FStorageDate <= " + dbl.sqlDate(dDate) +
                " and FYearMonth <> " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00") +
                " and FCheckState = 1) and FYearMonth <> " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00") + " ) ";
            return sResult;

        } else if (i != 0) { //取前i天（例如前一天，就是-1）
            if (!YssFun.formatDate(dDate, "MMdd").equalsIgnoreCase("0101")) {
                sResult = " select * from (select * from " +
                    pub.yssGetTableName(tableName) +
                    " where FStorageDate = (select max(FStorageDate) from " +
                    pub.yssGetTableName(tableName) +
                    " where FStorageDate <= " +
                    dbl.sqlDate(YssFun.addDay(dDate, i)) +
                    " and FYearMonth <> " +
                    dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00") +
                    " and FCheckState = 1) and FYearMonth <> " +
                    dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00") + " ) ";
                return sResult;
            } else {
                sResult = " select * from (select * from " +
                    pub.yssGetTableName(tableName) +
                    " where FStorageDate = (select max(FStorageDate) from " +
                    pub.yssGetTableName(tableName) +
                    " where FStorageDate <= " + dbl.sqlDate(dDate) +
                    " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00") +
                    " and FCheckState = 1) and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(dDate, "yyyy") + "00") + " ) ";
                return sResult;
            }
        }
        return "";
    }

    public String buildSaveFeesSql(byte oper, String fees) throws YssException {
        String[] sFeesAry = null;
        StringBuffer buf = new StringBuffer();
        SimpleBean simple = null;
        try {
            if (fees.length() > 0) {
                sFeesAry = fees.split("\f\n");
                simple = new SimpleBean();
                for (int i = 0; i < 8; i++) {
                    if (sFeesAry.length > i &&
                        !sFeesAry[i].split("\n")[0].equalsIgnoreCase("total")) {
                        simple.parseRowStr(sFeesAry[i]);
                        if (oper == YssCons.OP_EDIT) {
                            buf.append(" FFeeCode" +
                                       (i +
                                        1)).append(" = ").append(dbl.sqlString(
                                            simple.
                                            getCode())).
                                append(" , ");
                            buf.append(" FTradeFee" +
                                       (i +
                                        1)).append(" = ").append(simple.getValue()).
                                append(" , ");
                        } else if (oper == YssCons.OP_ADD) {
                            buf.append(dbl.sqlString(simple.getCode())).append(
                                " , ");
                            buf.append(simple.getValue()).append(" , ");
                        }
                    } else {
                        if (oper == YssCons.OP_EDIT) {
                            buf.append(" FFeeCode" + (i + 1)).append(" = ' ',");
                            buf.append(" FTradeFee" + (i + 1)).append(" = 0,");
                        } else if (oper == YssCons.OP_ADD) {
                            buf.append(" ' ', ");
                            buf.append(" 0, ");
                        }
                    }
                }
                return buf.toString();
            } else {
                if (oper == YssCons.OP_ADD) {
                    return
                        "' ' , 0 , ' ' , 0 , ' ' , 0 ,' ' , 0 ,' ' , 0 ,' ' , 0 ,' ' , 0 ,' ' , 0 ,";
                    //add by zhangfa 20101125 5、	当新建一笔交易数据（此时已经计算出费用），当把这笔费用链接删除之后，重新修改该笔交易数据，此时点击费用计算（即为0），然后保存；再次浏览该数据时却发现新删除的费用没有能够保存下来
                }else if (oper==YssCons.OP_EDIT) {
                	return " FFeeCode1 = ' ' ,  FTradeFee1 = 0 ,  FFeeCode2 = ' ' ,  FTradeFee2 = 0 ,  FFeeCode3 = ' ' ,  FTradeFee3 = 0 ,  FFeeCode4 = ' ' ,  FTradeFee4 = 0 ,  FFeeCode5 = ' ' ,  FTradeFee5 = 0 ,  FFeeCode6 = ' ', FTradeFee6 = 0, FFeeCode7 = ' ', FTradeFee7 = 0, FFeeCode8 = ' ', FTradeFee8 = 0,";
                	//-----------------end---------------------------------------------------------------------------------------------------------------------
                }else {
                    return "";
                }
            }
        } catch (Exception e) {
            throw new YssException("生成保存费用信息的SQL语句出错", e);
        }
    }

    //转换代码，例如 001,002转换成'001','002'
    public String sqlCodes(String sCodes) {
        String strReturn = "";
        String[] sPortAry;
        if (sCodes.trim().length() > 0) {
            if (sCodes.substring(sCodes.length() - 1, sCodes.length()).equalsIgnoreCase(",")) {
                sCodes = sCodes.substring(0, sCodes.length() - 1);
            }
            sPortAry = sCodes.split(",");
            for (int i = 0; i < sPortAry.length; i++) {
                if (YssFun.right(sPortAry[i], 1).equalsIgnoreCase("'") &&
                    YssFun.left(sPortAry[i], 1).equalsIgnoreCase("'")) { //判断如果字符前后都有"'"，那么就没必要在前后都加"'"  胡昆 20070912
                    strReturn = strReturn + sPortAry[i] + ",";
                } else {
                    strReturn = strReturn + "'" + sPortAry[i].replaceAll("'", "''") +
                        "',";
                }
            }
            if (strReturn.length() > 0) {
                strReturn = YssFun.left(strReturn, strReturn.length() - 1);
            }
        } else if (sCodes.length() == 1 && sCodes.equalsIgnoreCase(" ")) { //当传入的代码为空格时
            strReturn = "' '";
        } else if (sCodes.trim().length() == 0 && sCodes.equalsIgnoreCase("")) {
            strReturn = "''";
        }
        return strReturn;
    }

    /**
     * storageAnalysisSql
     *
     * @param sStorageType String
     * @return String[]
     */
    public String[] storageAnalysisSql(String sStorageType) throws YssException {
        String[] sResult = new String[2];
        String strSql = "";
        ResultSet rs = null;
        try {
            sResult[0] = "";
            sResult[1] = "";
            strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
                pub.yssGetTableName("Tb_Para_StorageCfg") +
                " where FCheckState = 1 and FStorageType = '" + sStorageType +
                "'";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                for (int i = 1; i <= 3; i++) {
                    if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                        rs.getString("FAnalysisCode" + String.valueOf(i)).
                        equalsIgnoreCase("002")) {
                        sResult[0] = sResult[0] + " ,FAnalysisCode" + i +
                            " as FBrokerCode, broker.FAnalysisName" + i +
                            " as FBrokerName ";
                        sResult[1] = sResult[1] +
                     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                    
                        
                        " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                        i +
                        " from " +
                        pub.yssGetTableName("tb_para_broker") +
                        " y where y.FCheckState = 1 ) broker on a.FAnalysisCode" +
                        i + " = broker.FBrokerCode";
                        
                        //end by lidaolong
                    } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                               rs.getString("FAnalysisCode" + String.valueOf(i)).
                               equalsIgnoreCase("003")) {
                        sResult[0] = sResult[0] + " ,FAnalysisCode" + i +
                            " as FExchangeCode, exchange.FAnalysisName" + i +
                            " as FExchangeName ";
                        sResult[1] = sResult[1] +
                            " left join (select FExchangeCode,FExchangeName as FAnalysisName" +
                            i +
                            " from tb_base_exchange) exchange on a.FAnalysisCode" +
                            i + " = exchange.FExchangeCode ";
                        /*
                                                +
                                                " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                         pub.yssGetTableName("tb_para_investmanager") +
                                                " where FStartDate < " +
                                                dbl.sqlDate(new java.util.Date()) +
                         " and FCheckState = 1 group by FInvMgrCode )m " +
                                                " join (select * from " +
                                                pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) exchange on a.FAnalysisCode" +
                                                i + " = exchange.FInvMgrCode";
                         */
                    } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                               rs.getString("FAnalysisCode" + String.valueOf(i)).
                               equalsIgnoreCase("001")) {
                        sResult[0] = sResult[0] + " ,FAnalysisCode" + i +
                            " as FInvMgrCode, invmgr.FAnalysisName" + i +
                            " as FInvMgrName ";
                        sResult[1] = sResult[1] +
                            " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                            i +
                            "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                            pub.yssGetTableName("tb_para_investmanager") +
                            " where FStartDate < " +
                            dbl.sqlDate(new java.util.Date()) +
                            " and FCheckState = 1 group by FInvMgrCode )m " +
                            "join (select * from " +
                            pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr on a.FAnalysisCode" +
                            i + " = invmgr.FInvMgrCode ";
                    } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                               rs.getString("FAnalysisCode" + String.valueOf(i)).
                               equalsIgnoreCase("004")) {
                        sResult[0] = sResult[0] + " ,FAnalysisCode" + i +
                            " as FCatCode, category.FCatName";
                        sResult[1] = sResult[1] +
                            " left join (select FCatCode,FCatName from Tb_Base_Category) category on a.FAnalysisCode" +
                            i + " = category.FCatCode";
                    } else {
                        sResult[1] = sResult[1] +
                            " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                            i + " from  " +
                            pub.yssGetTableName("Tb_Para_StorageCfg") +
                            " where 1=2) tn" + i + " on a.FAnalysisCode" + i +
                            " = tn" +
                            i + ".FAnalysisNull ";
                    }
                }
            }
            //2009.01.08 蒋锦 关掉记录集
            dbl.closeResultSetFinal(rs);
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取库存配置SQL出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String sqlSecurityHaveTab(String sInd, String sPortCode,
                                     java.util.Date dDate) throws YssException {
        String strSql = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        double dMValue = 0;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            createHaveTmpTable(sInd);

            strSql = "insert into Tb_tmp_SecHave_"
                + pub.getUserCode() + "_" + sInd +
                " (FSecurityCode,FPortCode,FInvMgrCode,FBrokerCode,FCatCode,FHaveAmount,FHaveCost," +
                " FHaveMCost,FHaveVCost,FHaveBaseCost,FHaveMBaseCost,FHaveVBaseCost," +
                " FHavePortCost,FHaveMPortCost,FHaveVPortCost,FHaveType,FMarketBaseValue,FAccruedinterest)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

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
                " t.FAccruedinterest,'Sec' as FHaveType from " +
                //------------------------------------------------------------------
                " (select FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode," +
                " sum(FTradeAmount*FAmountInd) as FTradeAmount," +
                " sum(FAccruedinterest*FAmountInd) as FAccruedinterest, " +
                " sum(FCost*FAmountInd) as FCost, sum(FMCost*FAmountInd) as FMCost, sum(FVCost*FAmountInd) as FVCost," +
                " sum(FBaseCuryCost*FAmountInd) as FBaseCuryCost, sum(FMBaseCuryCost*FAmountInd) as FMBaseCuryCost,sum(FVBaseCuryCost*FAmountInd) as FVBaseCuryCost," +
                " sum(FPortCuryCost*FAmountInd) as FPortCuryCost, sum(FMPortCuryCost*FAmountInd) as FMPortCuryCost,sum(FVPortCuryCost*FAmountInd) as FVPortCuryCost" +
                " from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " a left join (select * from Tb_Base_TradeType where FCheckState = 1) b " +
                " on a.FTradeTypeCode = b.FTradeTypeCode" +
                " where FBargainDate = " + dbl.sqlDate(dDate) +
                " and FPortCode in (" + sqlCodes(sPortCode) +
                ") and (a.FCheckState = 1)" +
                " group by FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode" +
                " )t full join " +
                //------------------------------------------------------------------
                " (select FSecurityCode,FPortCode,FStorageAmount,FStorageCost,FMStorageCost," +
                " FVStorageCost,FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FBaseCuryCost," +
                " FMBaseCuryCost,FVBaseCuryCost,FAnalysisCode1," +
                " FAnalysisCode2,FAnalysisCode3,FMarketPrice,FBaseCuryRate" +
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FCheckState = 1 and FPortCode in (" + sqlCodes(sPortCode) +
                ") and " + sqlStoragEve(dDate) +
                " )ck on ck.FSecurityCode = t.FSecurityCode and " +
                " ck.FPortCode = t.FPortCode and ck.FAnalysisCode1 = t.FInvMgrCode" +
                " and ck.FAnalysisCode2 = t.FBrokerCode) k";
            //------------------------------------------------------------------
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pst.setString(1, rs.getString("FHSecurityCode"));
                pst.setString(2, rs.getString("FHPortCode"));
                pst.setString(3, rs.getString("FHInvMgrCode"));
                pst.setString(4, rs.getString("FHBrokerCode"));
                pst.setString(5, rs.getString("FHCatCode"));
                pst.setDouble(6, rs.getDouble("FHaveAmount"));
                pst.setDouble(7, rs.getDouble("FHaveCost"));
                pst.setDouble(8, rs.getDouble("FHaveMCost"));
                pst.setDouble(9, rs.getDouble("FHaveVCost"));
                pst.setDouble(10, rs.getDouble("FHaveBaseCost"));
                pst.setDouble(11, rs.getDouble("FHaveMBaseCost"));
                pst.setDouble(12, rs.getDouble("FHaveVBaseCost"));
                pst.setDouble(13, rs.getDouble("FHavePortCost"));
                pst.setDouble(14, rs.getDouble("FHaveMPortCost"));
                pst.setDouble(15, rs.getDouble("FHaveVPortCost"));
                pst.setString(16, rs.getString("FHaveType"));
                dMValue = YssD.mul(rs.getDouble("FHaveAmount"),
                                   rs.getDouble("FMarketPrice"));
                dMValue = YssD.mul(dMValue, rs.getDouble("FBaseCuryRate"));
                dMValue = YssD.round(dMValue, 2);
                pst.setDouble(17, dMValue);
                pst.setDouble(18, rs.getDouble("FAccruedinterest"));
                pst.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            return "Tb_tmp_SecHave_" + pub.getUserCode() + "_" + sInd;
        } catch (Exception e) {
            throw new YssException("设置持有类属性证券数据临时表SQL出错: \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //创建证券持有的临时表
    protected void createHaveTmpTable(String sInd) throws YssException {
        String strSql = "";
        try {
            if (dbl.yssTableExist("Tb_tmp_SecHave_" + pub.getUserCode() + "_" +
                                  sInd)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table Tb_tmp_SecHave_" +
                               pub.getUserCode() + "_" + sInd));
                /**end*/
            }

            strSql = "create table Tb_tmp_SecHave_" + pub.getUserCode() + "_" +
                sInd +
                " (FSecurityCode varchar2(20)," +
                " FPortCode varchar2(20)," +
                " FInvMgrCode varchar2(20)," +
                " FBrokerCode varchar2(20)," +
                " FCatCode varchar2(20)," +
                " FHaveAmount DECIMAL(18, 4)," +
                " FHaveCost DECIMAL(18, 4)," +
                " FHaveMCost DECIMAL(18, 4)," +
                " FHaveVCost DECIMAL(18, 4)," +
                " FHaveBaseCost DECIMAL(18, 4)," +
                " FHaveMBaseCost DECIMAL(18, 4)," +
                " FHaveVBaseCost DECIMAL(18, 4)," +
                " FHavePortCost DECIMAL(18, 4)," +
                " FHaveMPortCost DECIMAL(18, 4)," +
                " FHaveVPortCost DECIMAL(18, 4)," +
                " FMarketBaseValue DECIMAL(18, 4)," +
                " FAccruedinterest  DECIMAL(18, 4)," +
                " FHaveType varchar2(10)" +
//               " FScaleField1 DECIMAL(18, 12)," +
//               " FScaleField2 DECIMAL(18, 12)," +
//               " FScaleField3 DECIMAL(18, 12)," +
//               " FScaleField4 DECIMAL(18, 12)," +
//               " FScaleField5 DECIMAL(18, 12)," +
                ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("创建持有类属性的临时表出错！");
        }
    }

    /**
     * 201401223 added by liubo.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
     * 在SQL写IN语句的时候，如果in里面包含的参数超过1000，就会报ORA-01795的错误。
     * 避免这种问题，可以使用几个IN语句然后用OR进行拼接的处理方法。
     * 比如select * from xxx where num in (1,2,3,4...2000)，这种情况下是一定会报错的
     * 然后可以使用这种写法就不会报错：select * from xxx where num in (1,2,3,4...500) or num in (501...1000) or num in (1001...1500) or num in (1501...2000)
     * 这个新加的方法可以将一个很长的in子句解析成可以正常执行的OR连接的多个IN子句
     * @param sNumsOriginal		原始的in参数
     * @param sFieldCode		需要使用到in子句的字段名
     * @param iLimit			拆分界限。理论上是1000，不过推荐设置小一点，比如800，900
     * @return
     * @throws YssException
     */
    public String getNumsDetail(String sNumsOriginal, String sFieldCode, int iLimit) throws YssException
    {
    	String sReturn = "";
    	String sSubSql = "";	//用这个变量来存储拆分界限下的参数
    	ArrayList<String> arReturnList = new ArrayList<String>();	//这个arraylist用于存储拼接好的OR IN子句的参数
    	try
    	{
	    	String[] sDataDetail = sNumsOriginal.split(",");	//解析原始in参数
	    	
	    	if (sDataDetail.length >= iLimit)	//如果原始in参数个数比拆分界限大，则进行拆分
	    	{
	    		int iSeq = 1;	//这个变量来控制每一个OR IN子句的参数数量
	    		for (int i = 0; i < sDataDetail.length; i++)
	    		{
	    			//若当前正在拼接的OR IN子句的参数数量小于拆分界限，则继续进行拼接
	    			if (iSeq < iLimit)
	    			{
		    			sSubSql += dbl.sqlString(sDataDetail[i]) + ",";
		    			iSeq++;
	    			}
	    			//否则，则表示当前拼接的子句已经拼接完成，存到arraylist中，准备进行下一个子句的拼接
	    			else 
	    			{
	    				if (sSubSql != null && !sSubSql.trim().equals(""))
	    				{
	    					arReturnList.add(this.sqlCodes(sSubSql));	    	
	    					iSeq = 1;
	    					i -= 1;		
	    					sSubSql = "";
	    				}
	    			}
	    		}
	    		
	    		//拼接剩余的参数。比如说一个2000个参数，拆分界限是600，则最后会剩下200个参数，这个时候就需要单独进行处理了
	    		if (sSubSql != null && !sSubSql.trim().equals(""))
	    		{
	    			arReturnList.add(this.sqlCodes(sSubSql));	
	    		}
	    		
	    		for (int i = 0; i < arReturnList.size(); i++)
	    		{
	    			if (i == 0)
	    			{
	    				sReturn += sFieldCode + " in (" + arReturnList.get(i) + ")";
	    			}
	    			else
	    			{
	    				sReturn += " or " + sFieldCode + " in (" + arReturnList.get(i) + ")";
	    			}
	    		}
	    	}
	    	else 
	    	{
	    		sReturn =  sFieldCode + " in (" + this.sqlCodes(sNumsOriginal) + ")";
	    	}
	    	
	    	return sReturn;
    	}
    	catch(Exception ye)
    	{
    		throw new YssException();
    	}
    }

}
