package com.yss.imp;

import java.sql.*;
import java.sql.Date;
import java.util.*;

//QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
import com.yss.commeach.*;
import com.yss.dsub.*;
import com.yss.main.operdeal.linkInfo.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;
import com.yss.main.operdata.ExchangeRateBean;

public class ImpHbBroker
    extends BaseDataSettingBean {
    private String sPortCode = ""; //QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
    private EachRateOper eachOper = null; //更改汇率的获取方式 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
    public ImpHbBroker() {
    }

    /**
     * 获取汇率数据
     * add by xuxuming,20090924.MS00709,交易数据接口汇率提示,QDV4赢时胜上海2009年9月23日01_A
     * @param strValues String
     * @return String
     * @throws YssException
     */
    public String getExRateData(String strValues) throws YssException {
        ExchangeRateBean exRateFilter = new ExchangeRateBean();
        ExchangeRateBean exRateData = new ExchangeRateBean();
        String[] strSplitRow = strValues.split("\f\f\f");   //通过此分隔符分隔数据
        sPortCode = strSplitRow[0];                         //取第一条分隔符后的数据得到前台传组合代码
        String[] strRows = strSplitRow[1].split("\r\n");    //这里取第二条分隔符后的数据
        Date dateTrade = YssFun.toSqlDate(strRows[0].split(" ")[0]);
        try {
            exRateFilter.setStrExRateDate(YssFun.formatDate(dateTrade, "yyyy-MM-dd"));
            exRateData.setYssPub(pub);
            exRateData.setFilterType(exRateFilter);
            exRateData.getSetting();

            //当没有任何汇率数据时 返回none
            if (exRateData.getStrExRateSrcCode() == null || exRateData.getStrExRateSrcCode().trim().length() <= 0) {
                return "none";
            }
        } catch (Exception e) {
            throw new YssException("获取汇率数据失败！", e);
        }
        return "yes";
    }

    public void saveBrokerData(String strValues) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        PreparedStatement ps = null;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        if (!dbl.yssTableExist("tb_HbBroker_data")) {
            try {
                //alter by sunny  加了买入利息 和交易所
                dbl.executeSql("create table tb_HbBroker_data ("
                               + "TransNum varchar2(8) not null,"
                               + "TradeDate Date not null," //交易日
                               + "SettlementDate Date not null," //清算日
                               + " FCode varchar2(64)," //基金代号
                               + " ISIN  varchar2(64) not null,"
                               + " BloombergCode  varchar2(64) ," //上市代号
                               + " SecurityName varchar2(64) ," //股票名称

                               + " TradedPrice decimal(18,12)," //交易价钱
                               + " Curr  varchar2(4) ," //交易货币
                               + " ExchangeCode  varchar2(15) ," //交易所
                               + " TransType  varchar2(5) not null," //买或卖
                               + " Quantity decimal(18,2)," //交易数量
                               + " BrokerCode varchar2(64) ,"
                               + " GrossAmtLCY decimal(18,2),"
                               + " GrossAmtPfCurr decimal(18,2),"

                               + " Interest decimal(18,2)," //买入利息
                               + " Fees decimal(18,2)," //其他费用
                               + " Commission decimal(18,2)," //交易佣金

                               + " NetAmtLCY decimal(18,2) ," //净值
                               + " NetAmtPfCurr decimal(18,2),"

                               + " primary key (TransNum,TradeDate,ISIN,BloombergCode,TransType,SettlementDate,BrokerCode))");
            } catch (Exception e) {
                throw new YssException("保存华宝券商接口数据失败！", e);
            }
        }

        try {
            String[] strSplitRow = strValues.split("\f\f\f"); //通过此分隔符分隔数据 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
            sPortCode = strSplitRow[0]; //取第一条分隔符后的数据得到前台传组合代码 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
            //String[] strRows = strValues.split("\r\n");
            String[] strRows = strSplitRow[1].split("\r\n"); //这里取第二条分隔符后的数据 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
            Date dateTrade = YssFun.toSqlDate(strRows[0].split(" ")[0]);
            String[] strField = strRows[1].split("\t");
            String strSql = "insert into tb_HbBroker_data (TransNum,TradeDate,SettlementDate ,FCode,ISIN,BloombergCode,SecurityName,TradedPrice,Curr,ExchangeCode,TransType,Quantity,BrokerCode,GrossAmtLCY,GrossAmtPfCurr,Interest,Fees,Commission,NetAmtLCY,NetAmtPfCurr) "
                + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            ps = dbl.openPreparedStatement(strSql);
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql("delete from tb_HbBroker_data where TradeDate = " +
                           dbl.sqlDate(dateTrade));
            for (int i = 2; i < strRows.length; i++) {
                String[] strCols = strRows[i].split("\t", -1);
                if (strCols.length < 15) {
                    throw new YssException("保存华宝券商接口数据失败！\r\n数据格式不正确，请检查第" + i + "行。");
                }
                if (strCols[1].length() == 0) {
                    continue;
                }
                String strValue;
                if (YssFun.dateDiff(dateTrade, YssFun.toDate(strCols[1])) != 0) {
                    throw new YssException("保存华宝券商接口数据失败！\r\n交易日期不正确，存在有非【" + strRows[0].split(" ")[0] + "】的日期。");
                }
                ps.setString(1, strCols[0]);
                ps.setDate(2, YssFun.toSqlDate(strCols[1]));
                ps.setDate(3, YssFun.toSqlDate(strCols[2]));
                ps.setString(4, strCols[3]);
                ps.setString(5, strCols[4]);
                ps.setString(6, strCols[5]);
                ps.setString(7, strCols[6]);
                ps.setDouble(8, YssFun.toDouble(strCols[7]));
                ps.setString(9, strCols[8]);
                ps.setString(10, strCols[9]);
                ps.setString(11, strCols[10]);
                ps.setDouble(12, YssFun.toDouble(strCols[11]));
                ps.setString(13, strCols[12]);
                ps.setDouble(14, YssFun.toDouble(strCols[13]));
                ps.setDouble(15, YssFun.toDouble(strCols[14]));
                ps.setDouble(16, YssFun.toDouble(strCols[15]));
                ps.setDouble(17, YssFun.toDouble(strCols[16]));
                ps.setDouble(18, YssFun.toDouble(strCols[17]));
                ps.setDouble(19, YssFun.toDouble(strCols[18]));
                ps.setDouble(20, YssFun.toDouble(strCols[19]));
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            changeToTradeData(dateTrade);
        } catch (BatchUpdateException bue) {
            throw new YssException("保存华宝券商接口数据失败！", bue);
        } catch (Exception e) {
            throw new YssException("保存华宝券商接口数据失败！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeStatementFinal(ps);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
    }

    public void changeToTradeData(Date dateTrade) throws YssException {
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        CashAccLinkBean cashAccLink = null;
        ArrayList list = null;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        PreparedStatement psSub = null;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            int intCount = 1;
            //
            String strNowDate = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
            BaseLinkInfoDeal cashacc = (BaseLinkInfoDeal) pub.getOperDealCtx().
                getBean("CashLinkDeal");
            //  BaseCashAccLinkDeal cashacc = new BaseCashAccLinkDeal();
            cashacc.setYssPub(pub);
            CashAccountBean caBean = null;

            /*String strSql =  "insert into " + pub.yssGetTableName("Tb_Data_Trade") +
                  "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
                  " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
                  " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTFACTOR," +
                  " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FUNITCOST,FACCRUEDINTEREST," +
                  " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                  " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
                  " FTotalCost , FOrderNum, FDesc, FCheckState, FCreator, FCreateTime,FCheckUser,FCheckTime)" +
                  " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                     PreparedStatement ps = dbl.openPreparedStatement(strSql);*/
            String strSql = "insert into " + pub.yssGetTableName("Tb_Data_SubTrade") +
                "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
                " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
                " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
                " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST," +
                " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
                " FTotalCost, FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryCost,FMPortCuryCost,FVPortCuryCost, " +
                " FOrderNum, FDataSource, FDesc, FCheckState, FCreator, FCreateTime, FCheckUser,FCheckTime,FFactCashAccCode,FFactSettleMoney,FExRate,FFactSettleDate,FFactBaseRate,FFactPortRate)" + //新增三个字段 liyu 1128
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            psSub = dbl.openPreparedStatement(strSql);
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            dbl.executeSql("delete from " + pub.yssGetTableName("Tb_Data_Trade") + " where FBargainDate = " + dbl.sqlDate(dateTrade));
            dbl.executeSql("delete from " + pub.yssGetTableName("Tb_Data_SubTrade") + " where FBargainDate = " + dbl.sqlDate(dateTrade)); //alter by sunny  价格是针对100报价的
            strSql = "Select TransNum,FCode,ISIN,BloombergCode,Curr,TransType,TradeDate,SettlementDate,BrokerCode,(case when b.FCatCode='FI' then sum(Quantity/100) else sum(Quantity) end) as Amount,(case when b.FCatCode = 'FI' then sum(TradedPrice*Quantity/100) else sum(TradedPrice*Quantity) end)  as money ,sum(Commission) as Commission,sum(Fees) as Fees,sum(GrossAmtLCY) as CostAmount,sum(NetAmtLCY) as NetAmount,sum(Interest) as Interest,b.FCatCode,a.tradedprice  from "
                + "tb_HbBroker_data a left join (select * from " + pub.yssGetTableName("tb_para_security") + ") b on a.BloombergCode= b.fsecuritycode where a.TradeDate  = " + dbl.sqlDate(dateTrade) +
                " group by a.TransNum,a.FCode,a.ISIN,a.BloombergCode,a.Curr,a.TransType,a.TradeDate,a.SettlementDate,a.BrokerCode,a.tradedprice,b.FCatCode";
            //先检查是否有未设定信息的股票
            StringBuffer buf = new StringBuffer();
            String strNumDate = YssFun.formatDate(dateTrade,
                                                  "yyyyMMdd");
            String strNum = strNumDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_subtrade"),
                                       dbl.sqlRight("FNUM", 6), "000000",
                                       " where FNum like 'T"
                                       + strNumDate + "%'", 1);
            strNum = "T" + strNum;

            rs = dbl.openResultSet("Select * from (" + strSql + ") a left join (select FSecurityCode,FMarketCode,FTradeCury,max(fstartDate) from " + pub.yssGetTableName("Tb_Para_Security") +
                                   " where FCheckState=1 group by FSecurityCode,FMarketCode,FTradeCury ) b on a.BloombergCode = b.FSecurityCode where b.FSecurityCode is null"); //alter by sunny  怎么能拿基金代码 关联证券代码呢？
            while (rs.next()) {
                buf.append("[").append(rs.getString("BloombergCode")).append("]"); //这里改为 BloombergCode，SQL中是通过此字段与证券信息表的FSecurityCode关联的，FCode 为基金代号，提示不明确 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
            }
            rs.getStatement().close();
            rs = null;
            if (buf.length() > 0) {
                throw new YssException("系统中没有下列代码的证券" + buf.toString() + "，清检查设置！");
            }

            strSql = "Select * from (" + strSql + ") a join (select FSecurityCode,FMarketCode,FTradeCury,max(fstartDate) from " + pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1 group by FSecurityCode,FMarketCode,FTradeCury ) b on a.BloombergCode = b.FSecurityCode order by a.transnum"; //alter by sunny  要拿上市代码和证券代码关联
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                String strCode = "";
                strCode = rs.getString("FSecurityCode");
                String strBrokerCode = rs.getString("BrokerCode").trim();
                String strBS = "";
                if (rs.getString("TransType").equalsIgnoreCase("B")) {
                    strBS = "01";
                } else if (rs.getString("TransType").equalsIgnoreCase("S")) {
                    strBS = "02";
                } else if (rs.getString("TransType").equalsIgnoreCase("PLA")) {
                    strBS = "11";
                }
                double dubBaseRate = this.getSettingOper().getCuryRate(dateTrade, rs.getString("Curr"), sPortCode,
                    YssOperCons.YSS_RATE_BASE); //基础汇率
                //通过公共方法获取组合汇率 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
                eachOper = new EachRateOper();
                eachOper.setYssPub(pub);
                eachOper.getInnerPortRate(dateTrade, rs.getString("Curr"), sPortCode);
                double dubPortRate = eachOper.getDPortRate(); //组合汇率
                String strCashAccCode = " ";
                cashAccLink = new CashAccLinkBean();
                cashAccLink.setStrInvMgrCode("002");

                cashAccLink.setStrPortCode(sPortCode); //QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
                cashAccLink.setStrSecurityCode(strCode);
                cashAccLink.setStrBrokerCode(strBrokerCode);
                cashAccLink.setStrTradeTypeCode(strBS);
                cashAccLink.setDtStartDate(dateTrade);
                cashacc.setLinkAttr(cashAccLink);
                list = cashacc.getLinkInfoBeans();
                if (list != null) {
                    caBean = (CashAccountBean) list.get(0);
                    if (caBean != null) {
                        strCashAccCode = caBean.getStrCashAcctCode();
                    }
                }

                int i = YssFun.toInt(strNum.substring(9, strNum.length() - 5)) + 1;
                String stri = "";
                if (i < 10) {
                    stri = "00000" + i;
                } else if (i >= 10 && i < 100) {
                    stri = "0000" + i;
                } else if (i >= 100 && i < 1000) {
                    stri = "000" + i;
                } else if (i >= 1000 && i < 10000) {
                    stri = "00" + i;
                } else if (i >= 10000 && i < 100000) {
                    stri = "0" + i;
                } else if (i >= 100000) {
                    stri = "" + i;
                }
                strNum = strNum.substring(0, 9) + stri + "00000";

                psSub.setString(1, strNum); //编号 可能需要改动字段长度
                psSub.setString(2, strCode); //证券代码
                psSub.setString(3, sPortCode);
                psSub.setString(4, strBrokerCode.trim()); //券商代码
                psSub.setString(5, "002"); //投资经理代码
                psSub.setString(6, strBS); //交易方式
                psSub.setString(7, strCashAccCode); //现金帐户代码
                psSub.setString(8, " "); //所属分类
                psSub.setDate(9, rs.getDate("TradeDate")); //成交日期
                psSub.setString(10, "00:00:00"); //成交时间
                psSub.setDate(11, rs.getDate("SettlementDate")); //结算日期
                psSub.setString(12, "00:00:00"); //结算时间
                psSub.setInt(13, 1); //自动结算
                psSub.setDouble(14, dubPortRate); //组合汇率
                psSub.setDouble(15, dubBaseRate); //基础汇率

                psSub.setDouble(16, 1); //分配比例
                psSub.setDouble(17, rs.getDouble("Amount")); //原始分配数量

                psSub.setDouble(18, 1); //分配因子
                psSub.setDouble(19, rs.getDouble("Amount")); //交易数量
                psSub.setDouble(20, rs.getDouble("TradedPrice")); //交易价格
                psSub.setDouble(21, YssFun.roundIt(rs.getDouble("money"), 2)); //交易金额
                psSub.setDouble(22, rs.getDouble("Interest")); //应计利息
                psSub.setString(23, "COMMISSIONS"); //费用代码1
                psSub.setDouble(24, rs.getDouble("Commission")); //交易费用1
                psSub.setString(25, "Fee"); //费用代码2
                psSub.setDouble(26, rs.getDouble("Fees")); //交易费用2
                psSub.setString(27, " "); //费用代码3
                psSub.setDouble(28, 0); //交易费用3
                psSub.setString(29, " "); //费用代码4
                psSub.setDouble(30, 0); //交易费用4
                psSub.setString(31, " "); //费用代码5
                psSub.setDouble(32, 0); //交易费用5
                psSub.setString(33, " "); //费用代码6
                psSub.setDouble(34, 0); //交易费用6
                psSub.setString(35, " "); //费用代码7
                psSub.setDouble(36, 0); //交易费用7
                psSub.setString(37, " "); //费用代码8
                psSub.setDouble(38, 0); //交易费用8
                if (rs.getDouble("NetAmount") < 0) {
                    psSub.setDouble(39, -rs.getDouble("NetAmount")); //投资总成本 //要取净金额这个字段
                } else {
                    psSub.setDouble(39, rs.getDouble("NetAmount")); //投资总成本 //要取净金额这个字段
                }
                double dubCurr = YssFun.roundIt(dubBaseRate *
                                                rs.getDouble("NetAmount"), 2);
                psSub.setDouble(40, 0); //原币核算成本 rs.getDouble("money")
                psSub.setDouble(41, 0); //原币管理成本
                psSub.setDouble(42, 0); //原币估值成本
                psSub.setDouble(43, 0); //基础货币核算成本
                psSub.setDouble(44, 0); //基础货币管理成本
                psSub.setDouble(45, 0); //基础货币估值成本
                dubCurr = YssFun.roundIt(dubCurr / dubPortRate, 2);
                psSub.setDouble(46, 0); //组合货币核算成本
                psSub.setDouble(47, 0); //组合货币管理成本
                psSub.setDouble(48, 0); //组合货币估值成本
                psSub.setString(49, " "); //订单编号
                psSub.setInt(50, 1); //数据来源
                psSub.setString(51, " "); //描述
                psSub.setInt(52, 1); //审核状态
                psSub.setString(53, pub.getUserName()); //创建人、修改人
                psSub.setString(54, strNowDate); //创建、修改时间
                psSub.setString(55, pub.getUserName()); //复核人
                psSub.setString(56, strNowDate); //复核时间
                psSub.setString(57, strCashAccCode); //实际结算帐户
                if (rs.getDouble("NetAmount") < 0) {
                    psSub.setDouble(58, -rs.getDouble("NetAmount")); //实际结算金额
                } else {
                    psSub.setDouble(58, rs.getDouble("NetAmount")); //实际结算金额
                }
                psSub.setDouble(59, 1); //兑换汇率

                psSub.setDate(60, rs.getDate("SettlementDate")); //实际结算日期
                psSub.setDouble(61, dubBaseRate); //实际基础汇率
                psSub.setDouble(62, dubPortRate); //实际组合汇率

                psSub.addBatch();
                intCount++;
            }
            rs.getStatement().close();
            rs = null;
            psSub.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (BatchUpdateException bue) {
            throw new YssException("保存华宝券商接口数据失败！", bue);
        } catch (Exception e) {
            throw new YssException("保存华宝券商接口数据失败！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeStatementFinal(psSub);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
    }

}
