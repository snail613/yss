package com.yss.main.operdeal.datainterface.pretfun.szstock;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import com.yss.main.basesetting.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.datainterface.*;
import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.main.parasetting.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;
import com.yss.main.operdata.BondInterestBean;
import java.math.BigDecimal;

public class SZHBBean
    extends DataBase {
    public SZHBBean() {
    }

    /**
     * 修改注释：
     * 此次修改了交易所代码，原上海为SH，深圳为SZ 现改为 上海CG，深圳CS
     * 修改了行情来源，原上海为SH，深圳为SZ，现改为上海CG，深圳CS
     * 修改日期：2008-08-21
     * 这样为了与国际化同步
     * @throws YssException
     */
    public void inertData() throws YssException {
        makeData();
        HzToQs hzToQs = new HzToQs();
        hzToQs = new HzToQs();
        hzToQs.setYssPub(pub);
        hzToQs.initDate(this.sDate, "CS", "");
        hzToQs.inertData(); //从交易明细--->汇总到到交易子表(最终表)

    }

    /**
     * 实际处理深圳回报库
     * @param set int[]  套帐组
     * @throws YssException
     */
    public void makeData() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        PreparedStatement pstmt = null; //交易子表的pstmt
        ResultSet rs = null; //交易子表的rs
        String strSql = "";
        //------------------------------------------------------------------------
        CommonPretFun pret = null; //预处理javaBean
        BaseOperDeal operDeal = null;
        TradeSeatBean tradeSeat = null;
        BondInterestBean interest = null;
        SecurityBean security = null;
        //------------------------------------------------------------------------
        int settleDays = 0; //结算延迟天数
        String holidaysCode = ""; //节假日群代码
        String tradeDate = ""; //交易日期
        String catCode = ""; //品种
        String tradeType = ""; //交易类型
        String StockholderCode = ""; //股东代码
        String tradeSeatCode = ""; //席位号
        String securityCode = ""; //证券代码
        String srcSecurityCode = ""; //原证券代码
        String sPortCode = ""; //组合代码  add  by leeyu 080701
        //------------------------------------------------------------------------
        Hashtable feeTable = new Hashtable(); //存放费用的hash表
        StringBuffer buf = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;

            interest = new BondInterestBean();
            buf = new StringBuffer();

            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;

            pret = new CommonPretFun();
            pret.setYssPub(pub);

            operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);

            tradeSeat = new TradeSeatBean();
            tradeSeat.setYssPub(pub);

            security = new SecurityBean();
            security.setYssPub(pub);

            operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);

            tradeSeat = new TradeSeatBean();
            tradeSeat.setYssPub(pub);

            String strYwbz = null;
            String sJyFs = "PT", ETFSQBH = "", ETFSGSH = "";
            conn.setAutoCommit(false);
            bTrans = true;

            strSql = " select FSubCode from " +
                pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                " where  FPortCode in (" + operSql.sqlCodes(this.sPort) +
                " ) and FRelaType=" + dbl.sqlString("Stockholder");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append(rs.getString("FSubCode")).append(",");
            }
            if (buf.toString().length() > 1) {
                StockholderCode = buf.toString().substring(0,
                    buf.toString().length() - 1);
            }
            strSql = " delete from " + pub.yssGetTableName("Tb_Data_TradeDetailA") +
                " where FTradeDate=" + dbl.sqlDate(this.sDate) +
                " and FExchangeCode =" + dbl.sqlString("CS") +
                " and FPortCode in (" + operSql.sqlCodes(this.sPort) + ")";
            dbl.executeSql(strSql);

            strSql = " insert into " + pub.yssGetTableName("Tb_Data_TradeDetailA") +
                " (FPortCode,FSettleDate,FSecurityCode,FExchangeCode,FStockholderNum,FSeatNum,FTradeTypeCode,FTradeAmoumt," +
                " FTradePrice,FTradeMoney,FYhs,FJsf,FGhf,FZgf," +
                " FQtf,FYj,FYhsCode,Fjsfcode,FGhfCode,FZgfCode,FQtfCode,FYjCode ,FFxj,FBondIns ," +
                " FHggain,FTradeOrder,FTradeDate,FSrcSecCode,FANALYSISCODE2)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = dbl.openPreparedStatement(strSql);
            strSql = "select " + dbl.sqlLeft("a.hbhtxh", 6) +
                " as qsxw,a.* from tmp_sjshb a join " +
                pub.yssGetTableName("Tb_Para_Stockholder") + " b on " +
                dbl.sqlTrim("upper(a.hbgddm)") + "=" +
                dbl.sqlTrim("upper(b.FStockholderCode)") + " where a.HBCJRQ=" +
                dbl.sqlDate(YssFun.toSqlDate(this.sDate))
                + " and HBGDDM in (" + operSql.sqlCodes(StockholderCode) + " )"
                + " and a.hbywlb in('0B','0S','9B','9S','KB','KS','1B','1S','7B','4B','ZS') and hbcjsl>0 "
                + " and b.FStockholderCode in(select FStockholderCode from " +
                pub.yssGetTableName("Tb_Para_Stockholder") +
                " where FCheckState=1) and a.IDDel='False' ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                double tradeMoney = 0;
                double tradePrice = 0;
                double tradeAmount = 0;
                double bondinterest = 0.0;

                //tradeMoney=rs.getDouble("HBCJJG");
                tradePrice = rs.getDouble("HBCJJG");
                tradeAmount = rs.getDouble("HBCJSL");
                tradeMoney = YssD.mul(tradePrice, tradeAmount); //这里应取积 by leeyu 080702

                //深圳的征管费是按汇总记录计算的，这里就不算了，放到hztoqs里面算cherry
                tradeSeatCode = rs.getString("qsxw").toUpperCase(); //席位号
                StockholderCode = rs.getString("hbgddm").toUpperCase(); //股东代码
                tradeType = (rs.getString("hbywlb").equalsIgnoreCase("0B") ||
                             rs.getString("hbywlb").equalsIgnoreCase("1B") ||
                             rs.getString("hbywlb").equalsIgnoreCase("9S")) ? "B" : "S"; //买卖 这里加上括号 by leeyu 080701
                securityCode = rs.getString("hbzqdm"); //转换后证券代码
                srcSecurityCode = rs.getString("hbzqdm"); //原始证券代码
                //获取证券信息

                if (securityCode.startsWith("00")) { // 股票
                    catCode = "EQ";
                    if (tradeType.equalsIgnoreCase("B")) { //这里先做普通股处理 by leeyu 080701
                        tradeType = "01";
                    } else {
                        tradeType = "02";
                    }
                    if (rs.getString("hbywlb").equalsIgnoreCase("7B")) { //新股申购的处理,使在                     strZqbz = "XG";
                        tradeType = "04";
                    } else {
                        if (securityCode.startsWith("3", 2)) { //深圳卖出市值配售的上海股票，代码替换为上海股票代码
                            securityCode = securityCode.replaceFirst("003", "600");
                        }
                        if (rs.getDouble("hbcjjg") == 0) {
                            if (rs.getString("hbywlb").equalsIgnoreCase("KB")) {
                                tradeType = "01";
                            } else if (rs.getString("hbywlb").equalsIgnoreCase("KS")) {
                                tradeType = "02";
                            }
                        }
                    }
                }
                //-----------A股增发
                else if (securityCode.startsWith("07")) { //申购
                    //新债业务
                    tradeType = "04";
                    if (rs.getString("hbywlb").equalsIgnoreCase("7B")) { //可转债申购
                        securityCode = "125" + YssFun.right(rs.getString("hbzqdm"), 3);
                        tradeType = "01";
                    }
                    interest.setYssPub(pub);
                    interest.setSecurityCode(securityCode + " CS");
                    interest.getSetting();
                    //蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A 修改了债券利息的字段类型
                    bondinterest = YssD.mul(interest.getIntAccPer100(),
                                            new BigDecimal(interest.getIntDay() + ""), new BigDecimal(tradeAmount + ""));
                } else if (securityCode.startsWith("08")) {
                    tradeType = "05";
                    //老股东配债
                    if (rs.getString("hbywlb").equalsIgnoreCase("4B")) {
                        securityCode = "125" + YssFun.right(rs.getString("hbzqdm"), 3);
                    }
                    interest.setYssPub(pub);
                    interest.setSecurityCode(securityCode + " CS");
                    interest.getSetting();
                    //蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A 修改了债券利息的字段类型
                    bondinterest = YssD.mul(interest.getIntAccPer100(),
                                            new BigDecimal(interest.getIntDay() + ""), new BigDecimal(tradeAmount + ""));
                } else if (securityCode.startsWith("10")) { // 国债现券
                    interest.setYssPub(pub);
                    interest.setSecurityCode(securityCode + " CS");
                    interest.getSetting();
                    //蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A 修改了债券利息的字段类型
                    bondinterest = YssD.mul(interest.getIntAccPer100(),
                                            new BigDecimal(interest.getIntDay() + ""), new BigDecimal(tradeAmount + ""));
                } else if (securityCode.startsWith("11")) { // 企业债券(资产证券)
                    catCode = "FI";
                    if (securityCode.startsWith("119")) {
                        strYwbz = "ZCZQ";
                    } else {
                        strYwbz = "QYZQ";
                    }
                    interest.setYssPub(pub);
                    interest.setSecurityCode(securityCode + " CS");
                    interest.getSetting();
                    //蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A 修改了债券利息的字段类型
                    bondinterest = YssD.mul(interest.getIntAccPer100(),
                                            new BigDecimal(interest.getIntDay() + ""), new BigDecimal(tradeAmount + ""));
                } else if (securityCode.startsWith("12")) { // 可转债
                    catCode = "FI";
                    interest.setYssPub(pub);
                    interest.setSecurityCode(securityCode + " CS");
                    interest.getSetting();
                    //蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A 修改了债券利息的字段类型
                    bondinterest = YssD.mul(interest.getIntAccPer100(),
                                            new BigDecimal(interest.getIntDay() + ""), new BigDecimal(tradeAmount + ""));
                } else if (securityCode.startsWith("13")) { // 回购
                    catCode = "RE";
                    if (rs.getString("hbywlb").equalsIgnoreCase("0B")) {
                        strYwbz = "MRHG";
                    } else if (rs.getString("hbywlb").equalsIgnoreCase("9B")) {
                        strYwbz = "MRHGDQ";
                    } else if (rs.getString("hbywlb").equalsIgnoreCase("0S")) {
                        strYwbz = "MCHG";
                    } else if (rs.getString("hbywlb").equalsIgnoreCase("9S")) {
                        strYwbz = "MCHGDQ";
                    }
                } else if (securityCode.startsWith("16")) {
                    catCode = "TR";
                    strYwbz = "LOF";
                } else if (securityCode.startsWith("18")) {
                    catCode = "TR";
                    strYwbz = "FBS";
                } else if (securityCode.startsWith("15")) { //目前只有易方达发行了ETF100
                    catCode = "TR";
                    if (rs.getString("hbywlb").equalsIgnoreCase("KB")) {
                        tradeType = "15";
                        ETFSQBH = rs.getString("hbhtxh").trim();
                        ETFSGSH = securityCode;
                    } else if (rs.getString("hbywlb").equalsIgnoreCase("KS")) {
                        tradeType = "16";
                        ETFSQBH = rs.getString("hbhtxh").trim();
                        ETFSGSH = securityCode;
                    } else {
                        catCode = "TR";
                        strYwbz = "ETF";
                    }
                } else if (YssFun.left(securityCode, 2).equalsIgnoreCase("03")) {
                    catCode = "OP";
                    if (YssFun.toInt(YssFun.left(securityCode, 3)) >= 30 &&
                        YssFun.toInt(YssFun.left(securityCode, 3)) <= 32) {
                        strYwbz = "RGQZ";
                    } else if (YssFun.toInt(YssFun.left(securityCode, 3)) >= 38 &&
                               YssFun.toInt(YssFun.left(securityCode, 3)) <= 39) {
                        strYwbz = "RZQZ";
                    }
                }
                tradeSeat.setSeatCode(tradeSeatCode); //获取席位号
                tradeSeat.getSetting();
                tradeDate = pret.getDateConvert(rs.getString("HBCJRQ")); //获取日期

                security.setSecurityCode(securityCode + " CS");
                security.setStartDate(YssFun.toSqlDate(tradeDate));
                security.getSetting();
                catCode = security.getCategoryCode();
                sPortCode = this.getPortBystockHoderCode(StockholderCode);

                //pstmt.setString(1, this.getPortBystockHoderCode(StockholderCode)); //组合
                pstmt.setString(1, sPortCode);
                pstmt.setDate(2,
                              YssFun.toSqlDate(operDeal.getWorkDay(security.getHolidaysCode(),
                    YssFun.toDate(tradeDate), security.getIntSettleDays()))); //结算日期(清算日期)
                pstmt.setString(3, securityCode + " CS"); //转换后的证券代码
                pstmt.setString(4, "CS"); //交易所代码
                pstmt.setString(5, StockholderCode); //股东代码
                pstmt.setString(6, tradeSeatCode); //席位代码
                pstmt.setString(7, tradeType); //交易类型

                pstmt.setDouble(8, tradeAmount); //成交数量
                pstmt.setDouble(9, tradePrice); //成交价格
                pstmt.setDouble(10, tradeMoney); //成交金额
                if (catCode.equals("FI")) {
                    feeTable = this.getFee(catCode, StockholderCode, "CS",
                                           (tradeSeat.getBrokerCode() == null ? "" :
                                            tradeSeat.getBrokerCode()),
                                           (tradeSeatCode == null ? "" :
                                            tradeSeatCode),
                                           tradeMoney,
                                           tradeAmount,
                                           tradeDate,
                                           sPortCode);
                } else {
                    feeTable = this.getFee(catCode, StockholderCode, "CS",
                                           (tradeSeat.getBrokerCode() == null ? "" :
                                            tradeSeat.getBrokerCode()),
                                           (tradeSeatCode == null ? "" :
                                            tradeSeatCode),
                                           tradeMoney,
                                           tradeAmount,
                                           tradeDate,
                                           sPortCode);
                }
                if (catCode.equalsIgnoreCase("EQ")) {
                    if (feeTable.get("SZAGYHS") != null) {
                        pstmt.setDouble(11, ( (Double) feeTable.get("SZAGYHS")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(11, 0); //印花税
                    }
                    if (feeTable.get("SZAGZGF") != null) {
                        pstmt.setDouble(12,
                                        ( (Double) feeTable.get("SZAGZGF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(12, 0); //经手费
                    }
                    if (feeTable.get("SZAGGHF") != null) {
                        pstmt.setDouble(13,
                                        ( (Double) feeTable.get("SZAGGHF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(13, 0); //过户费
                    }
                    if (feeTable.get("SZAGJSF") != null) {
                        pstmt.setDouble(14,
                                        ( (Double) feeTable.get("SZAGJSF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(14, 0); //征管费
                    }
                    pstmt.setDouble(15, 0); //其他费
                    if (feeTable.get("SZAGYJ") != null) { //增加佣金 by leeyu 080701
                        pstmt.setDouble(16, ( (Double) feeTable.get("SZAGYJ")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(16, 0);
                    }
                    //pstmt.setDouble(16, 0); //佣金
                    pstmt.setString(17, "SZAGYHS"); //印花税代码
                    pstmt.setString(18, "SZAGZGF"); //经手费代码
                    pstmt.setString(19, "SZAGGHF"); //过户费代码
                    pstmt.setString(20, "SZAGJSF"); //征管费代码
                    pstmt.setString(21, " "); //其他费代码
                    pstmt.setString(22, "SZAGYJ"); //佣金代码

                } else if (catCode.equalsIgnoreCase("TR")) {
                    if (feeTable.get("SZJJYHS") != null) {
                        pstmt.setDouble(11,
                                        ( (Double) feeTable.get("SZJJYHS")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(11, 0); //印花税
                    }
                    if (feeTable.get("SZJJJSF") != null) {
                        pstmt.setDouble(12,
                                        ( (Double) feeTable.get("SZJJJSF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(12, 0); //经手费
                    }
                    if (feeTable.get("SZJJGHF") != null) {
                        pstmt.setDouble(13,
                                        ( (Double) feeTable.get("SZJJGHF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(13, 0); //过户费
                    }
                    if (feeTable.get("SZJJZGF") != null) {
                        pstmt.setDouble(14,
                                        ( (Double) feeTable.get("SZJJZGF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(14, 0); //征管费
                    }
                    pstmt.setDouble(15, 0); //其他费
                    //pstmt.setDouble(16, 0); //佣金
                    if (feeTable.get("SZJJYJ") != null) { //增加佣金 by leeyu 080701
                        pstmt.setDouble(16, ( (Double) feeTable.get("SZJJYJ")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(16, 0);
                    }
                    pstmt.setString(17, "SZJJYHS"); //印花税代码
                    pstmt.setString(18, "SZJJJSF"); //经手费代码
                    pstmt.setString(19, "SZJJGHF"); //过户费代码
                    pstmt.setString(20, "SZJJZGF"); //征管费代码
                    pstmt.setString(21, " "); //其他费代码
                    pstmt.setString(22, "SZJJYJ"); //佣金代码

                } else if (catCode.equalsIgnoreCase("OP")) {
                    if (feeTable.get("SZQZYHS") != null) {
                        pstmt.setDouble(11,
                                        ( (Double) feeTable.get("SZQZYHS")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(11, 0); //印花税
                    }
                    if (feeTable.get("SZQZJSF") != null) {
                        pstmt.setDouble(12,
                                        ( (Double) feeTable.get("SZQZJSF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(12, 0); //经手费
                    }
                    if (feeTable.get("SZQZGHF") != null) {
                        pstmt.setDouble(13,
                                        ( (Double) feeTable.get("SZQZGHF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(13, 0); //过户费
                    }
                    if (feeTable.get("SZQZZGF") != null) { //这里是 SZQZZGF 不是 SZJJZFG by leeyu 080701
                        pstmt.setDouble(14,
                                        ( (Double) feeTable.get("SZQZZGF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(14, 0); //征管费
                    }
                    pstmt.setDouble(15, 0); //其他费
                    //pstmt.setDouble(16, 0); //佣金
                    if (feeTable.get("SZQZYJ") != null) { //增加佣金 by leeyu 080701
                        pstmt.setDouble(16, ( (Double) feeTable.get("SZQZYJ")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(16, 0);
                    }
                    pstmt.setString(17, "SZQZYHS"); //印花税代码
                    pstmt.setString(18, "SZQZJSF"); //经手费代码
                    pstmt.setString(19, "SZQZGHF"); //过户费代码
                    pstmt.setString(20, "SZQZZGF"); //征管费代码
                    pstmt.setString(21, " "); //其他费代码
                    pstmt.setString(22, "SZQZYJ"); //佣金代码

                } else if (catCode.equalsIgnoreCase("FI")) {

                    if (feeTable.get("SZZQYHS") != null) {
                        pstmt.setDouble(11,
                                        ( (Double) feeTable.get("SZZQYHS")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(11, 0); //印花税
                    }
                    if (feeTable.get("SZZQJSF") != null) {
                        pstmt.setDouble(12,
                                        ( (Double) feeTable.get("SZZQJSF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(12, 0); //经手费
                    }
                    if (feeTable.get("SZZQGHF") != null) {
                        pstmt.setDouble(13,
                                        ( (Double) feeTable.get("SZZQGHF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(13, 0); //过户费
                    }
                    if (feeTable.get("SZZQZGF") != null) {
                        pstmt.setDouble(14,
                                        ( (Double) feeTable.get("SZZQZGF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(14, 0); //征管费
                    }
                    pstmt.setDouble(15, 0); //其他费
                    //pstmt.setDouble(16, 0); //佣金
                    if (feeTable.get("SZZQYJ") != null) { //增加佣金 by leeyu 080701
                        pstmt.setDouble(16, ( (Double) feeTable.get("SZZQYJ")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(16, 0);
                    }
                    pstmt.setString(17, "SZZQYHS"); //印花税代码
                    pstmt.setString(18, "SZZQJSF"); //经手费代码
                    pstmt.setString(19, "SZZQGHF"); //过户费代码
                    pstmt.setString(20, "SZZQZGF"); //征管费代码
                    pstmt.setString(21, " "); //其他费代码
                    pstmt.setString(22, "SZZQYJ"); //佣金代码

                } else if (catCode.equalsIgnoreCase("RE")) {
                    pstmt.setDouble(11, 0); //印花税
                    pstmt.setDouble(12, 0); //经手费
                    pstmt.setDouble(13, 0); //过户费
                    pstmt.setDouble(14, 0); //征管费
                    pstmt.setDouble(15, 0); //其他费
                    pstmt.setDouble(16, 0); //佣金
                    pstmt.setString(17, " "); //印花税代码
                    pstmt.setString(18, " "); //经手费代码
                    pstmt.setString(19, " "); //过户费代码
                    pstmt.setString(20, " "); //征管费代码
                    pstmt.setString(21, " "); //其他费代码
                    pstmt.setString(22, " "); //佣金代码
                }

//-----------------------------------------------------------------------
                pstmt.setDouble(23, 0); //风险金(暂时不填)
                pstmt.setDouble(24, bondinterest); //债券利息
                pstmt.setDouble(25, 0); //回购收益
                pstmt.setString(26, " "); //成交编号
                pstmt.setDate(27, YssFun.toSqlDate(tradeDate)); //成交日期
                pstmt.setString(28, srcSecurityCode + " CS"); //转化前的证券代码
                pstmt.setString(29, " ");
                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception ex) {
            throw new YssException("处理深圳回报库数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private String formatDate(String sDate) throws YssException {
        //格式化yyyyMMdd的日期格式
        //返回 yyyy-MM-dd
        return YssFun.left(sDate, 4) + "-" + YssFun.mid(sDate, 4, 2) + "-" +
            YssFun.right(sDate, 2);
    }

    private String formatTime(String sTime) throws YssException {
        //格式化HHMMSS HHMMSSWW的时间格式
        //返回 HH:MM:SS
        if (sTime.length() == 6) {
            return YssFun.left(sTime, 2) + ":" + YssFun.mid(sTime, 2, 2) + ":" +
                YssFun.right(sTime, 2);
        } else if (sTime.length() == 8) {
            return YssFun.left(sTime, 2) + ":" + YssFun.mid(sTime, 2, 2) + ":" +
                YssFun.mid(sTime, 4, 2);
        } else {
            return sTime;
        }
    }

    /* public Hashtable getFee(String catCode, String stockHoderCode,
                              String exchangeCode, String brokeCode,
                              String tradeSeat,
                              double tradeMoney, double tradeAmount,
                              String tradeDate,
                              String sPortCode) throws YssException { //增加组合代码 by leeyu 080701
         String sResult = "";
         ArrayList alFeeBeans = new ArrayList();
         YssFeeType feeType = null;
         FeeBean fee = null;
         double dFeeMoney = 0.0;
         StringBuffer bufShow = new StringBuffer();
         Hashtable table = new Hashtable();
         try {
            BaseOperDeal baseOper = this.getSettingOper();
            BaseFeeDeal feeOper = (BaseFeeDeal) pub.getOperDealCtx().getBean(
                  "feedeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);

            feeOper.setBrokerCode("");
            feeOper.setCatCode(catCode);
            feeOper.setTradeSeatCode(tradeSeat);
            feeOper.setStockholderCode("");
            feeOper.setExchangeCode(exchangeCode);
            feeOper.setPortCode(sPortCode);
            feeOper.setCurrencyCode("CNY");           //增加组合代码与币种 by leeyu 080701

            alFeeBeans = feeOper.getFeeBeans();
            if (alFeeBeans != null) {
               feeType = new YssFeeType();
               feeType.setMoney(tradeMoney);
               feeType.setInterest( -1);
               feeType.setAmount(tradeAmount);
               feeType.setCost( -1);
               feeType.setIncome( -1);
               feeType.setFee( -1);
               if (alFeeBeans.size() > 0) {
                  for (int i = 0; i < alFeeBeans.size(); i++) {
                     fee = (FeeBean) alFeeBeans.get(i);
                     dFeeMoney = baseOper.calFeeMoney(feeType, fee,
                           YssFun.toDate(tradeDate));
                     table.put(fee.getFeeCode(), new Double(dFeeMoney));
                  }
                  for (int j = 0; j < 8 - table.size(); j++) {
                     table.put("", new Double(0.0));
                  }
               }
            }
            return table;
         }
         catch (Exception e) {
            throw new YssException();
         }
      }*/

    public String getPortBystockHoderCode(String stockHoderCode) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        String sResult = "";
        try {
            strSql = " select FPortCode from " +
                pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                " where  FSubCode =" + dbl.sqlString(stockHoderCode) +
                " and FRelaType=" + dbl.sqlString("Stockholder");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getString("FPortCode");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取组合出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
