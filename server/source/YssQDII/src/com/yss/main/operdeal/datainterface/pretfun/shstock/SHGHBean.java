package com.yss.main.operdeal.datainterface.pretfun.shstock;

import java.sql.*;
import java.util.*;

import com.yss.main.operdata.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.bond.*;
import com.yss.main.operdeal.datainterface.*;
import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.main.parasetting.*;
import com.yss.pojo.param.bond.*;
import com.yss.util.*;
import java.math.BigDecimal;

public class SHGHBean
    extends DataBase {
    public SHGHBean() {
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
        try {
            this.makeData(); //从临时表--->交易明显--->
            HzToQs hzToQs = new HzToQs();
            hzToQs.setYssPub(pub);
            hzToQs.initDate(this.sDate, "CG", "");
            hzToQs.inertData(); //从交易明细--->汇总到到交易子表(最终表)
        } catch (Exception e) {
            throw new YssException("插入数据报错！", e);
        }
    }

    /**
     * 实际处理上海过户库
     * @param set int[]  套帐组
     * @throws YssException
     */
    public void makeData() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        PreparedStatement pstmt = null; //交易子表的pstmt
        ResultSet rs = null; //交易子表的rs
        String strSql = "";
        String portCode = "";
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

            strSql = " select FSubCode from " +
                pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                " where  FPortCode in (" + operSql.sqlCodes(this.sPort) +
                " ) and FRelaType=" + dbl.sqlString("Stockholder"); //根据股东代码来读数据
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
                " and FExchangeCode =" + dbl.sqlString("CG") +
                " and FPortCode in (" + operSql.sqlCodes(this.sPort) + ")";
            dbl.executeSql(strSql);

            rs = null;
//----------------------------------------------------处理交易明细表------------------------------------------------
            strSql = " insert into " + pub.yssGetTableName("Tb_Data_TradeDetailA") +
                " (FPortCode,FSettleDate,FSecurityCode,FExchangeCode,FStockholderNum,FSeatNum,FTradeTypeCode,FTradeAmoumt," +
                " FTradePrice,FTradeMoney,FYhs,FJsf,FGhf,FZgf," +
                " FQtf,FYj,FYhsCode,Fjsfcode,FGhfCode,FZgfCode,FQtfCode,FYjCode ,FFxj,FBondIns ," +
                " FHggain,FTradeOrder,FTradeDate,FSrcSecCode,FANALYSISCODE2)" + //表里有这个字段FANALYSISCODE2 by leeyu 080701
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; // add by leeyu
            pstmt = dbl.openPreparedStatement(strSql);
            strSql = "select a.* from tmpSH_gh a join " +
                pub.yssGetTableName("Tb_Para_Stockholder") + " b on " +
                dbl.sqlTrim("upper(a.GDDM)") + "=" +
                dbl.sqlTrim("upper(b.FStockholderCode)") +
                " where  GDDM in (" + operSql.sqlCodes(StockholderCode) + " )" +
                " and a.isdel=" + dbl.sqlString("False") + " and a.BCRQ=" +
                dbl.sqlString(YssFun.formatDate(this.sDate, "yyyyMMdd")) +
                " and ( (" + dbl.sqlLeft("ZQDM", 1) +
                " in ('6','0','1','5') and not (CJJG=0 and CJJE=0 and " +
                dbl.sqlLeft("ZQDM", 2) + " in ('00','01','09'))" +
                " ) or " + dbl.sqlLeft("ZQDM", 2) + " in ('20','70') or " +
                dbl.sqlLeft("ZQDM", 3) +
                "  in ('737','739','740','730','731','704','743','733','780','790','760','762','783','764','781','793','751')) and b.FStockholderCode in(select FStockholderCode from " +
                pub.yssGetTableName("Tb_Para_Stockholder") +
                " where FCheckState=1 group by FStockholderCode) order by a.ZQDM"; //先处理同一笔业务,再处理同一只证券
            rs = dbl.openResultSet(strSql);
            String num = "";

            String ETFSQBH = "", ETFSGSH = "";

            while (rs.next()) {
                double bondinterest = 0;
                double reviewInfo = 0; //回购收益
                double tradeAmount = 0;
                double tradeMoney = 0;
                double tradePrice = 0;
                tradeAmount = rs.getDouble("CJSL"); //成交数量
                tradeMoney = rs.getDouble("CJJE"); //成交金额
                tradePrice = rs.getDouble("CJJG"); //成交价格
                tradeType = rs.getString("bs").toUpperCase();
                if (tradeType.equalsIgnoreCase("B")) { //现在考虑买卖方式
                    tradeType = "01";
                } else if (tradeType.equalsIgnoreCase("S")) {
                    tradeType = "02";
                }
                tradeSeatCode = (rs.getString("gsdm").trim().length() < 5 ? "00" : "") +
                    rs.getString("gsdm").trim().toUpperCase();
                StockholderCode = rs.getString("GDDM");
                securityCode = rs.getString("zqdm");
                srcSecurityCode = rs.getString("zqdm");
                tradeDate = pret.getDateConvert(rs.getString("BCRQ")); //获取日期
                //----------------------------------------计算费用-------------------------------------------------------------
                //-----------------------------股票--------------------------
                if (securityCode.startsWith("6")) { // 股票
                    if (securityCode.startsWith("609")) { //上海卖出市值配售的深圳股票，代码替换为深圳股票代码
                        securityCode = securityCode.replaceFirst("609", "002");
                    }
                    if (rs.getString("sqdh").equalsIgnoreCase(ETFSQBH)) {
                        srcSecurityCode = ETFSGSH;
                        //if (tradeType.equalsIgnoreCase("S")) {
                            //strYwbz = "ETFSG"; //ETF申购
                       //} else {
                            //strYwbz = "ETFSH"; //ETF赎回
                        //}  //STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
                    } else if (rs.getDouble("cjjg") == 0) {
                        //strYwbz = "KZZGP"; //可转债股票
                    }
                }
                //------------------------------基金---------------------------------
                else if (securityCode.startsWith("5")) { // && !YssFun.oneStart(strZqdm, "580,582")) { // 基金
                    tradeAmount = rs.getDouble("cjsl");
                    if (rs.getDouble("cjjg") == 0) { // 可转债券转成股票--其中债转股的补差数据汇总完毕后处理
                        if (securityCode.equalsIgnoreCase("510050")) {
                            ETFSQBH = rs.getString("sqdh");
                            ETFSGSH = securityCode;
                            if (tradeType.equalsIgnoreCase("B")) { //ETF申购
                                tradeType = "15";
                            } else { //ETF赎回
                                tradeType = "16";
                            }
                        }
                    }
                }
                //----------------------------回购----------------------------
                else if (securityCode.startsWith("20")) { // 回购
                    //=======================2008-12-02 MS00062 回购收益的新公式 by leeyu 修改
                    //reviewInfo=YssD.mul(YssD.div(YssD.div(tradePrice,100),360),tradeAmount,1000);
                    //新的回购收益公式 RoundIt(RoundIt((cjjg / 100) / 360 * HgTs, 5) * cjsl * 1000, 2)
                    double HgTs = 0; //回购天数
                    HgTs = getPurchareDurateion(securityCode + " CG"); //此方法在定义在下面
                    reviewInfo = YssD.mul(YssD.round(YssD.mul(YssD.div(YssD.div(tradePrice, 100), 360), HgTs), 5), tradeAmount, 1000);
                    //============2008-12-02
                    if (rs.getString("bs").equalsIgnoreCase("B")) {
                        tradeType = "24";

                    } else if (rs.getString("bs").equalsIgnoreCase("S")) {
                        tradeType = "25";
                    }
                    tradeAmount = rs.getDouble("cjsl") * 10;
                }
                //----------------------------债券---------------------------------
                else if (securityCode.startsWith("0")) { // 国债现券
                    if (rs.getDouble("cjjg") == 0) {
                        continue;
                    }
                    tradeAmount = rs.getDouble("cjsl") * 10;
                } else if (securityCode.startsWith("1")) {
                    tradeAmount = rs.getDouble("cjsl") * 10;
                }
                //-----------------------------回购----------------------------------------
                else if (securityCode.startsWith("20")) { // 回购
                    tradeType = rs.getString("bs").equalsIgnoreCase("S") ? "24" :
                        "25";
                    tradeAmount = rs.getDouble("cjsl") * 10;
                }
                //---------------------------- 权证 -----------------------------------------
                else if (securityCode.startsWith("58")) { // 权证
                    tradeAmount = rs.getDouble("cjsl");
                }
                //------------------------------新股---------------------------------
                else if (YssFun.left(securityCode, 3).equalsIgnoreCase("740")) {
                    securityCode = "600" + YssFun.right(securityCode, 3);
                    if (tradeType.equalsIgnoreCase("B")) {
                        tradeType = "04"; //新股申购
                    } else {
                        tradeType = "12"; //新股还款
                    }
                } else if (YssFun.left(securityCode, 3).equalsIgnoreCase("730")) { //新股中签
                    securityCode = "600" + YssFun.right(securityCode, 3);
                    tradeAmount = rs.getDouble("cjsl");
                    tradeType = "11";
                } else if (YssFun.left(securityCode, 3).equalsIgnoreCase("731")) { //增发
                    securityCode = "600" + YssFun.right(securityCode, 3);
                    tradeAmount = rs.getDouble("cjsl");
                    tradeType = "03";
                } else if (YssFun.left(securityCode, 3).equalsIgnoreCase("743")) {
                    securityCode = "110" + YssFun.right(securityCode, 3);
                    if (tradeType.equalsIgnoreCase("B")) {
                        tradeType = "04"; //可转债申购
                    } else {
                        tradeType = "12"; //可转债还款
                    }
                } else if (YssFun.left(securityCode, 3).equalsIgnoreCase("733")) { //新债中签
                    securityCode = "110" + YssFun.right(securityCode, 3);
                    tradeAmount = rs.getDouble("cjsl") * 10;
                    tradeType = "11";
                } else if (securityCode.startsWith("70")) {
                    if (rs.getDouble("cjjg") == 100) { // // 老股东配债
                        securityCode = "110" + YssFun.right(securityCode, 3);
                        tradeAmount = rs.getDouble("cjsl") * 10;
                        tradeType = "05";
                    } else {
                        securityCode = securityCode.replaceFirst("7", "6");
                        tradeAmount = rs.getDouble("cjsl");
                        if (calc.isPgGp(securityCode)) { // 配股
                            tradeType = "08";
                        } else { //新股
                            tradeType = "05";
                        }
                    }
                } else if (securityCode.startsWith("73")) { //市值配售
                    tradeAmount = rs.getDouble("cjsl");
                    if (securityCode.startsWith("7", 2)) { // 按市值配售新股T＋1日初次中签(上海中签上海股票)
                        securityCode = securityCode.replaceFirst("737", "600");
                    } else if (securityCode.startsWith("9", 2)) { // 按市值配售新股T＋1日初次中签(上海中签深圳股票)
                        securityCode = securityCode.replaceFirst("739", "002");
                    }
                    tradeType = "05";
                } else if (securityCode.startsWith("780")) { //601新股中签
                    securityCode = "601" + YssFun.right(securityCode, 3);
                    tradeAmount = rs.getDouble("cjsl");
                    tradeType = "11";
                } else if (securityCode.startsWith("783")) { //新债
                    securityCode = "113" + YssFun.right(securityCode, 3);
                    tradeAmount = rs.getDouble("cjsl") * 10;
                    tradeType = "11";
                } else if (securityCode.startsWith("760") || securityCode.startsWith("781")) { //配股，老股东配股
                    securityCode = "601" + YssFun.right(securityCode, 3);
                    tradeAmount = rs.getDouble("cjsl");
                    if (calc.isPgGp(securityCode)) {
                        tradeType = "08";
                    } else {
                        tradeType = "11";
                    }
                } else if (securityCode.startsWith("762") || securityCode.startsWith("764")) { //配债
                    securityCode = "113" + YssFun.right(securityCode, 3);
                    tradeAmount = rs.getDouble("cjsl") * 10;
                    tradeType = "11";
                } else if (YssFun.left(securityCode, 3).equalsIgnoreCase("790")) { //申购新股
                    securityCode = "601" + YssFun.right(securityCode, 3);
                    if (tradeType.equalsIgnoreCase("B")) {
                        tradeType = "04"; //新股申购
                    } else {
                        tradeType = "12"; //新股还款
                    }
                } else if (YssFun.left(securityCode, 3).equalsIgnoreCase("793")) {
                    securityCode = "113" + YssFun.right(securityCode, 3);
                    if (tradeType.equalsIgnoreCase("B")) {
                        tradeType = "04"; //可转债申购
                    } else { //可转债返款
                        tradeType = "12";
                    }
                } else if (securityCode.startsWith("75199")) { //上交所新公司债，当天确认申购
                    tradeAmount = rs.getDouble("cjsl") * 10;
                    tradeType = "11";
                }

                //获取证券信息
                security.setSecurityCode(securityCode + " CG");
                security.setStartDate(YssFun.toSqlDate(tradeDate));
                security.getSetting();
                catCode = security.getCategoryCode();
                portCode = this.getPortBystockHoderCode(StockholderCode);

                if (security.getCategoryCode().equalsIgnoreCase("FI")) {
                    interest.setYssPub(pub);
                    interest.setSecurityCode(securityCode + " CG");
                    interest.getSetting();

                    if (this.isFiExist(security.getSecurityCode())) {
                        //蒋锦 MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A 修改了债券利息的字段类型
                        bondinterest = YssD.mul(interest.getIntAccPer100(), new BigDecimal(tradeAmount + ""));
                    } else {
                        bondinterest = this.calBondInterest(security.getSecurityCode(), tradeType,
                            tradeAmount, portCode, tradeDate);

                    }
                }
//-------------------------------------------------------------------------------------------------------------------
                tradeSeat.setSeatCode(tradeSeatCode); //获取券商
                tradeSeat.getSetting();

                pstmt.setString(1, portCode); //组合
                pstmt.setDate(2, YssFun.toSqlDate(operDeal.getWorkDay(security.getHolidaysCode(),
                    YssFun.toDate(tradeDate), security.getIntSettleDays()))); //结算日期(清算日期)
                pstmt.setString(3, securityCode + " CG"); //转换后的证券代码
                pstmt.setString(4, "CG"); //交易所代码
                pstmt.setString(5, StockholderCode); //股东代码
                pstmt.setString(6, tradeSeatCode); //席位代码
                pstmt.setString(7, tradeType); //交易类型
                pstmt.setDouble(8, tradeAmount); //成交数量
                pstmt.setDouble(9, tradePrice); //成交价格
                pstmt.setDouble(10, tradeMoney); //成交金额

                feeTable = this.getFee(catCode,
                                       StockholderCode, "CG",
                                       (tradeSeat.getBrokerCode() == null ? "" :
                                        tradeSeat.getBrokerCode()), tradeSeatCode,
                                       tradeMoney + bondinterest, //国内债券计算费用时是要加上国债利息的
                                       tradeAmount,
                                       tradeDate,
                                       portCode); // add by leeyu
                if (catCode.equalsIgnoreCase("EQ")) {
                    if (feeTable.get("SHAGYHS") != null) {
                        pstmt.setDouble(11,
                                        ( (Double) feeTable.get("SHAGYHS")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(11, 0); //印花税
                    }
                    if (feeTable.get("SHAGZGF") != null) {
                        pstmt.setDouble(12,
                                        ( (Double) feeTable.get("SHAGZGF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(12, 0); //经手费
                    }
                    if (feeTable.get("SHAGGHF") != null) {
                        pstmt.setDouble(13,
                                        ( (Double) feeTable.get("SHAGGHF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(13, 0); //过户费
                    }
                    if (feeTable.get("SHAGJSF") != null) {
                        pstmt.setDouble(14,
                                        ( (Double) feeTable.get("SHAGJSF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(14, 0); //征管费
                    }
                    pstmt.setDouble(15, 0); //其他费
                    //pstmt.setDouble(16, 0); //佣金
                    if (feeTable.get("SHAGYJ") != null) { //加上佣金代码 by leeyu 080701
                        pstmt.setDouble(16, ( (Double) feeTable.get("SHAGYJ")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(16, 0);
                    }
                    pstmt.setString(17, "SHAGYHS"); //印花税代码
                    pstmt.setString(18, "SHAGZGF"); //经手费代码
                    pstmt.setString(19, "SHAGGHF"); //过户费代码
                    pstmt.setString(20, "SHAGJSF"); //征管费代码
                    pstmt.setString(21, " "); //其他费代码
                    //pstmt.setString(22, " "); //佣金代码
                    pstmt.setString(22, "SHAGYJ"); //佣金代码 alter by leeyu

                } else if (catCode.equalsIgnoreCase("TR")) {
                    if (feeTable.get("SHJJYHS") != null) {
                        pstmt.setDouble(11,
                                        ( (Double) feeTable.get("SHJJYHS")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(11, 0); //印花税
                    }
                    if (feeTable.get("SHJJJSF") != null) {
                        pstmt.setDouble(12,
                                        ( (Double) feeTable.get("SHJJJSF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(12, 0); //经手费
                    }
                    if (feeTable.get("SHJJGHF") != null) {
                        pstmt.setDouble(13,
                                        ( (Double) feeTable.get("SHJJGHF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(13, 0); //过户费
                    }
                    if (feeTable.get("SHJJZGF") != null) {
                        pstmt.setDouble(14,
                                        ( (Double) feeTable.get("SHJJZGF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(14, 0); //征管费
                    }
                    pstmt.setDouble(15, 0); //其他费
                    //pstmt.setDouble(16, 0); //佣金
                    if (feeTable.get("SHJJYJ") != null) {
                        pstmt.setDouble(16,
                                        ( (Double) feeTable.get("SHJJYJ")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(16, 0);
                    }
                    pstmt.setString(17, "SHJJYHS"); //印花税代码
                    pstmt.setString(18, "SHJJJSF"); //经手费代码
                    pstmt.setString(19, "SHJJGHF"); //过户费代码
                    pstmt.setString(20, "SHJJZGF"); //征管费代码
                    pstmt.setString(21, " "); //其他费代码
                    //pstmt.setString(22, " "); //佣金代码
                    pstmt.setString(22, "SHJJYJ"); //佣金代码

                } else if (catCode.equalsIgnoreCase("OP")) {
                    if (feeTable.get("SHQZYHS") != null) {
                        pstmt.setDouble(11,
                                        ( (Double) feeTable.get("SHQZYHS")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(11, 0); //印花税
                    }
                    if (feeTable.get("SHQZJSF") != null) {
                        pstmt.setDouble(12,
                                        ( (Double) feeTable.get("SHQZJSF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(12, 0); //经手费
                    }
                    if (feeTable.get("SHQZGHF") != null) {
                        pstmt.setDouble(13,
                                        ( (Double) feeTable.get("SHQZGHF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(13, 0); //过户费
                    }
                    if (feeTable.get("SHQZZGF") != null) {
                        pstmt.setDouble(14,
                                        ( (Double) feeTable.get("SHQZZGF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(14, 0); //征管费
                    }
                    pstmt.setDouble(15, 0); //其他费
                    //pstmt.setDouble(16, 0); //佣金
                    if (feeTable.get("SHQZYJ") != null) {
                        pstmt.setDouble(16,
                                        ( (Double) feeTable.get("SHQZYJ")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(16, 0);
                    }
                    pstmt.setString(17, "SHQZYHS"); //印花税代码
                    pstmt.setString(18, "SHQZJSF"); //经手费代码
                    pstmt.setString(19, "SHQZGHF"); //过户费代码
                    pstmt.setString(20, "SHQZZGF"); //征管费代码
                    pstmt.setString(21, " "); //其他费代码
                    //pstmt.setString(22, " "); //佣金代码
                    pstmt.setString(22, "SHQZYJ"); //佣金代码
                } else if (catCode.equalsIgnoreCase("FI")) {

                    if (feeTable.get("SHZQYHS") != null) {
                        pstmt.setDouble(11,
                                        ( (Double) feeTable.get("SHZQYHS")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(11, 0); //印花税
                    }
                    if (feeTable.get("SHZQJSF") != null) {
                        pstmt.setDouble(12,
                                        ( (Double) feeTable.get("SHZQJSF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(12, 0); //经手费
                    }
                    if (feeTable.get("SHZQGHF") != null) {
                        pstmt.setDouble(13,
                                        ( (Double) feeTable.get("SHZQGHF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(13, 0); //过户费
                    }
                    if (feeTable.get("SHZQZGF") != null) {
                        pstmt.setDouble(14,
                                        ( (Double) feeTable.get("SHZQZGF")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(14, 0); //征管费
                    }
                    pstmt.setDouble(15, 0); //其他费
                    //pstmt.setDouble(16, 0); //佣金
                    if (feeTable.get("SHZQYJ") != null) {
                        pstmt.setDouble(16,
                                        ( (Double) feeTable.get("SHZQYJ")).
                                        doubleValue());
                    } else {
                        pstmt.setDouble(16, 0);
                    }
                    pstmt.setString(17, "SHZQYHS"); //印花税代码
                    pstmt.setString(18, "SHZQJSF"); //经手费代码
                    pstmt.setString(19, "SHZQGHF"); //过户费代码
                    pstmt.setString(20, "SHZQZGF"); //征管费代码
                    pstmt.setString(21, " "); //其他费代码
                    //pstmt.setString(22, " "); //佣金代码
                    pstmt.setString(22, "SHZQYJ"); //佣金代码

                } else if (catCode.equalsIgnoreCase("RE")) {
                    pstmt.setDouble(11, 0); //印花税
                    /*if(feeTable.get("SH"+securityCode.substring(0,3)+"-"+securityCode.substring(3,6)+"HGJSF") != null){
                       pstmt.setDouble(12, ( (Double) feeTable.get("SH"+securityCode.substring(0,3)+"-"+securityCode.substring(3,6)+"HGJSF")).doubleValue()); //经手费
                       pstmt.setString(18, "SH"+securityCode.substring(0,3)+"-"+securityCode.substring(3,6)+"HGJSF"); //经手费代码
                                    }
                                    else{
                                    pstmt.setDouble(12, 0); //经手费
                       pstmt.setString(18, "SH"+securityCode.substring(0,3)+"-"+securityCode.substring(3,6)+"HGJSF"); //经手费代码
                                    }*/
                    if (feeTable.get("SHHGJSF") != null) {
                        pstmt.setDouble(12, ( (Double) feeTable.get("SHHGJSF")).doubleValue()); //经手费
                        pstmt.setString(18, "SHHGJSF");
                    } else {
                        pstmt.setDouble(12, 0);
                        pstmt.setString(18, "SHHGJSF");
                    }
                    pstmt.setDouble(13, 0); //过户费
                    pstmt.setDouble(14, 0); //征管费
                    pstmt.setDouble(15, 0); //其他费
                    pstmt.setDouble(16, 0); //佣金
                    pstmt.setString(17, " "); //印花税代码
                    pstmt.setString(19, " "); //过户费代码
                    pstmt.setString(20, " "); //征管费代码
                    pstmt.setString(21, " "); //其他费代码
                    pstmt.setString(22, " "); //佣金代码
                }

//-----------------------------------------------------------------------
                pstmt.setDouble(23, 0); //风险金(暂时不填)

                pstmt.setDouble(24, bondinterest); //债券利息
                pstmt.setDouble(25, reviewInfo); //回购收益
                pstmt.setString(26, " "); //成交编号 若没有就传进去一个空格  by leeyu 080701
                pstmt.setDate(27, YssFun.toSqlDate(tradeDate)); //成交日期
                pstmt.setString(28, srcSecurityCode); //转化前的证券代码
                pstmt.setString(29, " "); //FANALYSISCODE2 字段,原表这个字段不能为空
                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
    }

    //---------------------------------------------------------------------------------------
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

    /*public Hashtable getFee(String catCode, String stockHoderCode,
                            String exchangeCode, String brokeCode,
                            String tradeSeat,
                            double tradeMoney, double tradeAmount,
                            String tradeDate,
                            String sPortCode) throws YssException {    //by leeyu 新增组合
       String sResult = "";
       ArrayList alFeeBeans = new ArrayList();
       YssFeeType feeType = null;
       FeeBean fee = null;
       double dFeeMoney = 0.0;
       StringBuffer bufShow = new StringBuffer();
       Hashtable table = new Hashtable();
       String sKey="";
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
          feeOper.setCurrencyCode("CNY");      //add by leeyu 080701
          sKey=catCode+"\t"+tradeSeat+"\t"+exchangeCode+"\t"+sPortCode+"\t"+"CNY";
          if(htKeyFee.get(sKey)==null){
             alFeeBeans = feeOper.getFeeBeans();
             htKeyFee.put(sKey,alFeeBeans);
          }else{
             alFeeBeans=(ArrayList)htKeyFee.get(sKey);
          }
          //alFeeBeans = feeOper.getFeeBeans();
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
    public boolean isFiExist(String strSecurityCode) throws YssException {
        ResultSet rs = null;
        boolean flag = false;
        try {
            String strSql = " select FSecurityCode from " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " where  FSecurityCode =" + dbl.sqlString(strSecurityCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                flag = true;
            }
            return flag;
        } catch (Exception e) {
            throw new YssException("判断债券信息是否存在出错");
        }
    }

    public double calBondInterest(String strSecurityCode, String tradeType,
                                  double tradeAmount, String portCode, String bargainDate
        ) throws YssException {
        String strSql = "";
        double accruedInterest = 0.0;
        BaseBondOper bondOper = null;
        YssBondIns bondIns = null;
        BaseOperDeal operDeal = null;
        try {
            operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);
            bondIns = new YssBondIns();
            if (tradeType.equals(YssOperCons.YSS_JYLX_Buy)) {
                bondOper = operDeal.getSpringRe(strSecurityCode, "Buy"); //生成BaseBondOper
                bondIns.setInsType("Buy");
            } else if (tradeType.equals(YssOperCons.YSS_JYLX_Sale)) {
                bondOper = operDeal.getSpringRe(strSecurityCode, "Sell"); //生成BaseBondOper
                bondIns.setInsType("Sell");
            }
            if (bondOper == null) {
                return 0;
            }
            bondIns.setSecurityCode(strSecurityCode);
            bondIns.setInsDate(YssFun.toDate(bargainDate));
            bondIns.setInsAmount(tradeAmount);
            bondIns.setPortCode(portCode);
            bondOper.setYssPub(pub);
            bondOper.init(bondIns);
            accruedInterest = bondOper.calBondInterest();
            return accruedInterest;
        } catch (Exception e) {
            throw new YssException("");
        }
    }

    //增加方法用于取回购的天数 MS00062
    private double getPurchareDurateion(String securityCode) throws YssException {
        double duration = 0;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select b.Fduration from " + pub.yssGetTableName("tb_para_purchase") + " " +
                " a left join (select case FDurUnit when '0' then FDuration when '1' then FDuration*7 when '2' then FDuration*30 when '3' then FDuration*365 else 0 end as FDuration,FDepDurCode from " +
                " " + pub.yssGetTableName("tb_para_depositduration") + " ) b on a.FDepDurCode=b.FDepDurCode where a.FSecurityCode=" + dbl.sqlString(securityCode);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                duration = rs.getDouble("FDuration");
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return duration;
    }
}
