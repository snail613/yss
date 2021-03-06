package com.yss.main.operdeal.datainterface.pretfun;

import com.yss.util.*;
import java.sql.*;
import java.util.Date;
import com.yss.vsub.*;
import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.main.parasetting.*;
import java.util.ArrayList;
import com.yss.main.operdeal.datainterface.CommonPretFun;
import com.yss.main.operdeal.datainterface.pojo.CommonPrepFunBean; //新加传参数的POJO用于给CommonPretFun传参数 add by leeyu 2008-11-7
import com.yss.main.operdeal.BaseOperDeal;

public class HzToQs
    extends DataBase {

    public HzToQs() {

    }

    public void inertData() throws YssException {
        PreparedStatement pstmt = null; //交易子表的pstmt
        SecurityBean security = null;
        String strSql = "";
        TradeSeatBean tradeSeat = new TradeSeatBean();
        tradeSeat.setYssPub(pub);
        ResultSet rs = null;
        String strBegin = "";
        String strDate = "";
        String num = "";
        String strNum = "";
        CommonPrepFunBean prepBean = null; // 新加传参数的POJO用于给CommonPretFun传参数 add by leeyu 2008-11-7
        CommonPretFun pret = new CommonPretFun();
        pret.setYssPub(pub);

        String portRate = "";
        String baseRate = "";
        String tradeType = "";

        double totalCost = 0.0;
        double tradePrice = 0.0;

        Connection conn = null;
        boolean bTrans = false;
        ArrayList accList = new ArrayList(); //获取现金帐户的参数
        ArrayList rateBaseList = new ArrayList(); //获取基础汇率的参数
        ArrayList ratePortList = new ArrayList(); //获取组合汇率的参数
        BaseOperDeal operDeal = new BaseOperDeal();
        FeeBean feeBean = null; //处理系统中的费用的相关信息
        //------------------------------------------------------交易数据表tb_001_data_subTrade----------------------------------------------------------
        try {
            operDeal.setYssPub(pub);
            conn = dbl.loadConnection();
//         strSql = "delete from " + pub.yssGetTableName("tb_data_subTrade") +
//               " where FBargainDate=" + dbl.sqlDate(YssFun.toSqlDate(this.sDate))+
//               " and fdatasource = 1 and FsecurityCode in (select FsecurityCode"+
//               " from " + pub.yssGetTableName("tb_para_security")+" where FExchangeCode="+
//               dbl.sqlString(this.sExchange)+")";
            //=================重新修改了业务资料的删除条件,当没有证券代码信息时也可以删除 by leeyu MS00062 2008-12-2
            strSql = "delete from " + pub.yssGetTableName("tb_data_subTrade") +
                " where FBargainDate=" + dbl.sqlDate(YssFun.toSqlDate(this.sDate)) +
                " and fdatasource = 1 and " +
                dbl.sqlRight("FsecurityCode", "2") + //根据上交所与深交所的证券代码特点：用最后两位的交易所代码来删除数据
                "=" + dbl.sqlString(this.sExchange);
            //==================== 2008-12-2
            dbl.executeSql(strSql);
            strSql = strSql = "insert into " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
                " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
                " FSETTLEDATE,FSettleTime,FFactSettleDate,FSettleDesc,FMATUREDATE,FMATURESETTLEDATE," +
                " FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate," +
                " FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
                " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBailMoney," +
                " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, " +
                " FTotalCost,FSettleState, FOrderNum, FDesc, FDataSource, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,FRATEDATE" + // 这里补加一个字段FRATEDATE by leeyu 2008-12-3 MS00031
                " )" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                " ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; //MS00031
            pstmt = dbl.openPreparedStatement(strSql);

            //strSql = " select FSettleDate,FTradeDate,FSecurityCode,FExchangeCode,FTradeTypeCode,FStockholderNum," +
            //这里先不按股东代码分组 by leeyu 080702
            strSql = " select FSettleDate,FTradeDate,FSecurityCode,FExchangeCode,FTradeTypeCode," +
                " FYhsCode,Fjsfcode ,FGhfCode ,FZgfCode ,FQtfCode ,FYjCode,FQtfCode,FPORTCODE,FSEATNUM,(Ftradedate+fduration) As fmaturedate," +
                " fduration,fdurunitname,fholidayscode,fsettledaytype,FSettledays," +
                " sum(FTradeAmoumt) as FTradeAmoumt," +
                " sum(FTradeMoney)as FTradeMoney,sum(FYhs)as FYhs,sum(FJsf) as FJsf,sum(FGhf)as FGhf,sum(FZgf) as FZgf," +
                " sum(FYj) as FYj, sum(FFxj) as FFxj,sum(FQtf) as FQtf,Case When fcatcode='RE' Then Sum(round(round(ftradeprice*fduration/36000,5)*FTradeAmoumt*1000,2)) Else Sum(Fbondins)End  As Fbondins,sum(FHggain) as FHggain from (Select dt.*,ps.fduration,ps.fdurunitname,ps.fholidayscode,ps.fsettledaytype,ps.fcatcode,ps.FSettledays From " +
                pub.yssGetTableName("Tb_Data_Tradedetaila") + " dt 	Left Join (Select aa.*,bb.fholidayscode,bb.fsettledaytype ,bb.fcatcode,bb.FSettledays From(" +
                " Select fsecuritycode,fduration,fdurunitname From  " + pub.yssGetTableName("Tb_Para_Purchase") + " pp Inner Join ( select y.* from ( select FDepDurCode  from " + pub.yssGetTableName("Tb_Para_DepositDuration") +
                " )  x join (select a.*,b.FUserName as creator,c.FUserName as checkuser,f.FVocName as FDurUnitName from " + pub.yssGetTableName("Tb_Para_DepositDuration") + " a  left join(select FUserCode,FUserName from  Tb_Sys_UserList )b on b.FUserCode=a.FCreator  left join(select FUserCode,FUserName from  Tb_Sys_UserList)c on c.FUserCode=a.FCheckUser  left join Tb_Fun_Vocabulary f on a.FDurUnit = f.FVocCode and f.FVocTypeCode = 'dep_unit') y on y.FDepDurCode=x.FDepDurCode order by y.FDepDurCode " +
                " ) pd On pp.fdepdurcode=pd.fdepdurcode) aa Inner Join " + pub.yssGetTableName("Tb_Para_Security") + " bb On aa.fsecuritycode=bb.fsecuritycode " +
                " ) ps On dt.fsecuritycode=ps.fsecuritycode	)" +
                " where FExchangeCode =" + dbl.sqlString(this.sExchange) +
                " and FTradeDate=" + dbl.sqlDate(this.sDate) +
                " group by FSettleDate,FTradeDate,FSecurityCode,FExchangeCode,FTradeTypeCode," +
                //" FYhsCode,Fjsfcode ,FGhfCode ,FZgfCode ,FQtfCode ,FYjCode,FQtfCode,FStockholderNum,FSEATNUM,FPORTCODE,fduration,fdurunitname,fholidayscode,fsettledaytype,fcatcode,FSettledays";
                " FYhsCode,Fjsfcode ,FGhfCode ,FZgfCode ,FQtfCode ,FYjCode,FQtfCode,FSEATNUM,FPORTCODE,fduration,fdurunitname,fholidayscode,fsettledaytype,fcatcode,FSettledays";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                security = new SecurityBean();
                security.setYssPub(pub);
                security.setSecurityCode(rs.getString("FSecurityCode"));
                security.getSetting();
                tradeSeat.setSeatCode(rs.getString("FSEATNUM")); //获取券商
                tradeType = rs.getString("FTradeTypeCode");
                tradeSeat.getSetting();
                double dYJLX = 0; //交易中的应计利息
                if (security != null && security.getCategoryCode().equalsIgnoreCase("RE")) { //回购
                    dYJLX = rs.getDouble("FHggain");
                } else if (security != null && security.getCategoryCode().equalsIgnoreCase("FI")) { //债券
                    dYJLX = rs.getDouble("Fbondins");
                } else {
                    dYJLX = 0;
                } //处理应计利息部分，因为债券利息与回购利息是分开处理的 by leeyu

                if (rs.getString("FTradeTypeCode").equalsIgnoreCase("02")) {
                    strBegin = "900000";
                } else {
                    strBegin = "000000";
                }
                strDate = YssFun.formatDatetime(rs.getDate("FTradeDate")).
                    substring(0, 8);

                num = "T" + strDate + strBegin;
                strNum = num + dbFun.getNextInnerCode
                    (pub.yssGetTableName("Tb_Data_SubTrade"),
                     dbl.sqlRight("FNUM", 5), "00001",
                     " where FNUM like '"
                     + num.replaceAll("'", "''") + "%'");
                pstmt.setString(1, strNum); //交易流水号
                pstmt.setString(2, (rs.getString("FSecurityCode") == null ? " " : rs.getString("FSecurityCode"))); //证券代码
                pstmt.setString(3, (rs.getString("FPORTCODE") == null ? " " : rs.getString("FPORTCODE"))); //组合代码
                pstmt.setString(4, (tradeSeat.getBrokerCode() == null ? " " : tradeSeat.getBrokerCode())); //券商
                pstmt.setString(5, " "); //投资经理
                pstmt.setString(6, rs.getString("FTradeTypeCode")); //交易方式
                //-----------------------获取现金帐户的参数---------------------
                accList.add("");
                accList.add(rs.getString("FPORTCODE"));
                accList.add(rs.getString("FSecurityCode"));
                accList.add(tradeSeat.getBrokerCode());
                accList.add(tradeType);
                accList.add("CNY");
                accList.add(rs.getDate("FTradeDate"));
                accList.add(rs.getDate("FTradeDate"));
                //============新加传参数的POJO用于给CommonPretFun传参数 add by leeyu 2008-11-7
                prepBean = new CommonPrepFunBean();
                prepBean.setObj(accList);
                pret.init(prepBean);
//            pret.init(accList);
                //============= 2008-11-7
                pstmt.setString(7, pret.getCashAcc()); //现金帐户
                //---------------------------------------------------
                pstmt.setString(8, " "); //所属分类 这里放空格，因为业务资料查询时此字段中有空格 by leeyu
                pstmt.setDate(9, rs.getDate("FTradeDate")); //成交日期
                pstmt.setString(10, "00:00:00"); //成交时间
                pstmt.setDate(11, rs.getDate("FSettleDate")); //结算日期
                pstmt.setString(12, "00:00:00"); //结算时间
                pstmt.setDate(13, rs.getDate("FSettleDate")); //实际结算日期
                pstmt.setString(14, " "); //成交描述
                if (!tradeType.trim().equalsIgnoreCase("24") && !tradeType.trim().equalsIgnoreCase("25")) {
                    pstmt.setDate(15, YssFun.toSqlDate(YssFun.toDate("1900-01-01"))); //到期日期
                    pstmt.setDate(16, YssFun.toSqlDate(YssFun.toDate("1900-01-01"))); //到期结算日期

                } else { //回购到期时间需要重新计算fazmm20071219
                    pstmt.setDate(15, YssFun.toSqlDate(operDeal.getWorkDay(rs.getString("fholidayscode") == null ? "CN" : rs.getString("fholidayscode"), rs.getDate("fmaturedate") == null ? rs.getDate("Ftradedate") : rs.getDate("fmaturedate"), rs.getInt("fsettledaytype")))); //到期日期
                    pstmt.setDate(16,
                                  YssFun.toSqlDate(operDeal.getWorkDay(rs.getString("fholidayscode") == null ? "CN" : rs.getString("fholidayscode"),
                        YssFun.addDay(operDeal.getWorkDay(rs.getString("fholidayscode") == null ? "CN" : rs.getString("fholidayscode"), rs.getDate("fmaturedate") == null ? rs.getDate("Ftradedate") : rs.getDate("fmaturedate"), rs.getInt("fsettledaytype")), rs.getInt("FSettledays")),
                        rs.getInt("fsettledaytype")))); //到期结算日期

                }
                pstmt.setString(17, pret.getCashAcc()); //实际结算帐户

                if (tradeType.equalsIgnoreCase("01") || tradeType.equalsIgnoreCase("25")) {
                    totalCost = YssFun.roundIt(
                        YssD.add(rs.getDouble("FTradeMoney"),
                                 rs.getDouble("FYhs"),
                                 rs.getDouble("FJsf"), rs.getDouble("FGhf"),
                                 rs.getDouble("FZgf"), rs.getDouble("FQtf")), 2
                        );
                    if (security != null && security.getCategoryCode().equalsIgnoreCase("FI")) {
                        totalCost = YssD.add(totalCost, dYJLX); // 对于债券的买入时实际结算金额是要加上利息的
                    }
                } else if (tradeType.equalsIgnoreCase("02") || tradeType.equalsIgnoreCase("24")) {
                    totalCost = YssFun.roundIt(
                        YssD.sub(rs.getDouble("FTradeMoney"),
                                 YssD.add(rs.getDouble("FYhs"),
                                          rs.getDouble("FJsf"),
                                          rs.getDouble("FGhf"),
                                          rs.getDouble("FZgf"),
                                          rs.getDouble("FQtf"))), 2
                        );
                    if (security != null && security.getCategoryCode().equalsIgnoreCase("FI")) {
                        totalCost = YssD.sub(totalCost, dYJLX); // 对于债券的买入时实际结算金额是要减去利息的
                    }
                }
                pstmt.setDouble(18, totalCost); //实际结算金额
                pstmt.setDouble(19, 1); //兑换汇率
                //================================汇率参数===========================
                ratePortList.add("CNY");
                ratePortList.add(rs.getString("FPORTCODE"));
                //ratePortList.add("Base");
                ratePortList.add(YssOperCons.YSS_RATE_PORT);
                ratePortList.add(rs.getDate("FTradeDate"));
                //===============================新加传参数的POJO用于给CommonPretFun传参数 add by leeyu 2008-11-7
                prepBean = new CommonPrepFunBean();
                prepBean.setObj(ratePortList);
                pret.init(prepBean);
//            pret.init(ratePortList);
                //=====================================2008-11-7
                portRate = pret.getExchangeRate();
                //---------------------------
                rateBaseList.add("CNY");
                rateBaseList.add(rs.getString("FPORTCODE"));
                //rateBaseList.add("Base");
                rateBaseList.add(YssOperCons.YSS_RATE_BASE);
                rateBaseList.add(rs.getDate("FTradeDate"));
                //================================新加传参数的POJO用于给CommonPretFun传参数 add by leeyu 2008-11-7
                prepBean = new CommonPrepFunBean();
                prepBean.setObj(rateBaseList);
                pret.init(prepBean);
//            pret.init(rateBaseList);
                //================================2008-11-7
                baseRate = pret.getExchangeRate();
                //=========================================
                pstmt.setDouble(20, YssFun.toDouble(baseRate)); //实际结算基础汇率
                pstmt.setDouble(21, YssFun.toDouble(portRate)); //实际结算组合汇率
                pstmt.setInt(22, 1); //自动结算

                pstmt.setDouble(23, YssFun.toDouble(portRate)); //组合汇率
                pstmt.setDouble(24, YssFun.toDouble(baseRate)); //基础汇率

                pstmt.setDouble(25, 1); //分配比例
                pstmt.setDouble(26, 0); //原始分配数量
                pstmt.setInt(27, 1); //原分配因子
                if (rs.getDouble("FTradeAmoumt") == 0) {
                    tradePrice = 0;
                } else {
                    tradePrice = YssD.div(rs.getDouble("FTradeMoney"),
                                          rs.getDouble("FTradeAmoumt"));
                }
                pstmt.setDouble(28, rs.getDouble("FTradeAmoumt")); //成交数量
                pstmt.setDouble(29, tradePrice); //成交价格
                pstmt.setDouble(30, rs.getDouble("FTradeMoney")); //成交金额
                //pstmt.setDouble(31,  YssFun.roundIt(rs.getDouble("FBondIns"),2)); //应计利息
                pstmt.setDouble(31, YssFun.roundIt(dYJLX, 2)); //应计利息
                pstmt.setDouble(32, 0); //保证金金额
                double dYJ = 0, dYHS = 0, dJSF = 0, dGHF = 0, dZGF = 0; //五个费用 处理费用承担者 -- 券商 by leeyu 080702
                {
                    feeBean = new FeeBean();
                    feeBean.setYssPub(pub);
                    feeBean.setFeeCode(rs.getString("FYhsCode")); //印花税
                    feeBean.getSetting();
                    if (feeBean.getAssumeMan().equals("0")) {
                        dYHS = rs.getDouble("FYhs");
                    }
                    feeBean.setFeeCode(rs.getString("Fjsfcode")); //经手费
                    feeBean.getSetting();
                    if (feeBean.getAssumeMan().equals("0")) {
                        dJSF = rs.getDouble("FJsf");
                    }
                    feeBean.setFeeCode(rs.getString("FGhfCode")); //过户费
                    feeBean.getSetting();
                    if (feeBean.getAssumeMan().equals("0")) {
                        dGHF = rs.getDouble("FGhf");
                    }
                    feeBean.setFeeCode(rs.getString("FZgfCode")); //征管费
                    feeBean.getSetting();
                    if (feeBean.getAssumeMan().equals("0")) {
                        dZGF = rs.getDouble("FZgf");
                    }
                    dYJ = rs.getDouble("FYj");
                    dYJ = YssFun.roundIt(YssD.sub(dYJ, dYHS, dJSF, dGHF, dZGF), 2);
                }
                pstmt.setString(33, rs.getString("FYhsCode")); //印花税代码
                pstmt.setDouble(34, YssFun.roundIt(rs.getDouble("FYhs"), 2)); //印花税
                pstmt.setString(35, rs.getString("Fjsfcode")); //经手费代码
                pstmt.setDouble(36, YssFun.roundIt(rs.getDouble("FJsf"), 2)); //经手费
                pstmt.setString(37, rs.getString("FGhfCode")); //过户费代码
                pstmt.setDouble(38, YssFun.roundIt(rs.getDouble("FGhf"), 2)); //过户费
                pstmt.setString(39, rs.getString("FZgfCode")); //征管费代码
                pstmt.setDouble(40, YssFun.roundIt(rs.getDouble("FZgf"), 2)); //征管费
                pstmt.setString(41, rs.getString("FQtfCode")); //其他费代码
                pstmt.setDouble(42, YssFun.roundIt(rs.getDouble("FQtf"), 2)); //其他费
                pstmt.setString(43, rs.getString("FYjCode")); //佣金代码
                //pstmt.setDouble(44, YssFun.roundIt(rs.getDouble("FYj"),2)); //佣金
                pstmt.setDouble(44, dYJ);
                pstmt.setDouble(45, totalCost); //实收实付金额
                pstmt.setInt(46, 0); //结算标识
                pstmt.setString(47, ""); //订单编号
                pstmt.setString(48, ""); //描述
                pstmt.setInt(49, 1);
                pstmt.setInt(50, 1); //审核状态
                pstmt.setString(51, pub.getUserCode()); //创建人、修改人
                pstmt.setString(52,
                                YssFun.formatDatetime(new java.util.Date())); //创建、修改时间
                pstmt.setString(53, pub.getUserCode()); //审核人
                pstmt.setString(54,
                                YssFun.formatDatetime(new java.util.Date())); //审核时间
                pstmt.setDate(55, rs.getDate("FTradeDate")); //补加字段FRATEDATE by leeyu 2008-12-3 MS00031

                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true); //  chenyibo  20071002

        } catch (Exception e) {
            throw new YssException(e);
        } finally { //leeyu add 20080701
            dbl.closeStatementFinal(pstmt);
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, false);
        }
    }
}
