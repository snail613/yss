package com.yss.main.operdeal.datainterface.pretfun.shstock;

import java.sql.*;
import java.util.*;

import com.yss.main.basesetting.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.datainterface.*;
import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.main.parasetting.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

public class SHZQBDBean
    extends DataBase {
    public SHZQBDBean() {
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
        makeData(null);
        HzToQs hzToQs = new HzToQs();
        hzToQs.setYssPub(pub);
        hzToQs.initDate(this.sDate, "CG", "");
        hzToQs.inertData(); //从交易明细--->汇总到到交易子表(最终表)

    }

    /**
     * 处理上海变动库
     * @param set int[]  套帐组
     * @throws YssException
     */
    public void makeData(String[] set) throws YssException {
        Connection con = dbl.loadConnection();
        Statement st = null;

        boolean bTrans = false, bHgYj = false;
        String ETFSQBH = "", ETFSGSH = "";
        String sJyFs = "PT";
        CommonPretFun pret = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt_tmp = null;
        ResultSet rs = null;
        ResultSet rs_tmp = null;

        ExchangeBean exchange = null;
        BaseOperDeal operDeal = null;
        TradeSeatBean tradeSeat = null;
        int settleDays = 0;
        String holidaysCode = "";
        String tradeType = "";

        String portRate = ""; //组合汇率
        String baseRate = ""; //基础汇率

        String strZqbz = ""; //品种
        String securityCode = ""; //证券代码

//        ArrayList accList = new ArrayList();//findbugs风险调整，定义的变量未使用  胡坤  20120625
        ArrayList feeList = new ArrayList();
        ArrayList rateBaseList = new ArrayList();
        ArrayList ratePortList = new ArrayList();
        StringBuffer buf = new StringBuffer();
        String[] arrFee = null;
        Hashtable hashTable = new Hashtable();
        String tradeDate = "";
        Connection conn = dbl.loadConnection();

        String strNum = "";
        String strDate = "";
        String strBegin = "";
        String strYwbz = "";
        String catCode = "";
        String sType = "";
        String StockholderCodes = "";

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            st = con.createStatement();
            String strSql = null;
            con.setAutoCommit(false);
            bTrans = true;

            pret = new CommonPretFun();
            pret.setYssPub(pub);

            exchange = new ExchangeBean();
            exchange.setYssPub(pub);
            exchange.setStrExchangeCode("CG");
            exchange.getSetting();
            holidaysCode = exchange.getStrHolidaysCode();
            settleDays = Integer.parseInt(exchange.getSettleDays());

            operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);

            tradeSeat = new TradeSeatBean();
            tradeSeat.setYssPub(pub);

            strSql = " select FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                " where  FPortCode in (" + operSql.sqlCodes(this.sPort) +
                " ) and FRelaType=" + dbl.sqlString("Stockholder");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append(rs.getString("FSubCode")).append(",");
            }
            if (buf.toString().length() > 1) {
                StockholderCodes = buf.toString().substring(0,
                    buf.toString().length() - 1);
            }

            //----------------------------------------------------交易临时表-----------------------------------------------------------------------------
            strSql = " insert into " + pub.yssGetTableName("Tb_Data_TradeDetailA") +
                " (FPortCode,FSettleDate,FSecurityCode,FExchangeCode,FStockholderNum,FSeatNum,FTradeTypeCode,FTradeAmoumt,FTradePrice,FTradeMoney,FYhsCode,FYhs,Fjsfcode,FJsf,FGhfCode,FGhf,FZgfCode,FZgf," +
                " FQtfCode,FQtf,FYjCode,FYj,FFxj,FBondIns,FHggain,FTradeOrder,FTradeDate,FSrcSecCode,FANALYSISCODE2)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt_tmp = dbl.openPreparedStatement(strSql);
            /* strSql =
                   " select * from tmp_zqbd "+
                   " where BDRQ=" + dbl.sqlDate(base.getDate()) + " and ZQZH in (" + StockholderCodes + ")"+
                   " and ((bdlx ='00G' and bdsl > 0) or ((qylb in('HL','S','DX') and bdlx <>'00G')"+
                   " or (zqlb='PZ' and ltlx='N' and bdlx ='00J' and " + dbl.sqlTrimNull("qylb")+
                   ") or (zqlb='XL' and ltlx='F' and bdlx ='00J' and " + dbl.sqlTrimNull("qylb") + ")) or BDLX = '003') and "+
                   " isDel='False'"; */
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_TradeDetailA") + " where FPortCode in(" + operSql.sqlCodes(sPort) + ") and FExchangeCode='CG' and FTradeDate=" +
                dbl.sqlDate(YssFun.formatDate(sDate, "yyyy-MM-dd")) + " and FTradeTypeCode in('06','07')";
            dbl.executeSql(strSql);
            strSql =
                " select * from tmp_zqbd " +
                " where BDRQ=" + dbl.sqlString(YssFun.formatDate(sDate, "yyyyMMdd")) + " and ZQZH in (" + operSql.sqlCodes(StockholderCodes) + ")" +
                " and ((bdlx ='00G' and bdsl > 0) or ((qylb in('HL','S','DX') and bdlx <>'00G')" +
                " or (zqlb='PZ' and ltlx='N' and bdlx ='00J' and " + dbl.sqlTrimNull("qylb") +
                ") or (zqlb='XL' and ltlx='F' and bdlx ='00J' and " + dbl.sqlTrimNull("qylb") + ")) or BDLX = '003') and " +
                " isDel='False'";
            /* strSql= "select * from tmp_zqbd where BDRQ = "+dbl.sqlString(YssFun.formatDate(sDate,"yyyyMMdd"))+
                   " and ZQZH in ("+operSql.sqlCodes(StockholderCodes)+") and "+
                   " ((bdlx='00G' and bdsl>0) or (bdlx<>'00G') or (zqlb in('PZ','PT') and ltlx in('0','N') and bdlx in('00A','00J') and " +
                   " (trim(qylb) is null) or(zqlb = 'XL' and ltlx = 'F' and bdlx = '00J' and trim(qylb) is null) or BDLX = '003')) and "+
                   " isDel = 'False'";*/
            rs_tmp = dbl.openResultSet(strSql);
            String num = "";
            Hashtable feeTable;

            while (rs_tmp.next()) {
                double dCjje = 0, dGzlx = 0, dCjsl = 0;
                StockholderCodes = rs_tmp.getString("ZQZH");
                tradeSeat.setSeatCode(rs_tmp.getString("XWH")); //获取券商
                tradeSeat.getSetting();
                securityCode = rs_tmp.getString("ZQDM"); //证券代码
                tradeDate = pret.getDateConvert(rs_tmp.getString("BDRQ")); //获取日期

                pstmt_tmp.setString(1, getPortBystockHoderCode(StockholderCodes));
                pstmt_tmp.setDate(2,
                                  YssFun.toSqlDate(operDeal.getWorkDay(holidaysCode,
                    YssFun.toDate(tradeDate), settleDays))); //结算日期(清算日期)
                pstmt_tmp.setString(3, securityCode + " CG"); //转换后的证券代码
                pstmt_tmp.setString(4, "CG"); //交易所代码
                pstmt_tmp.setString(5, rs_tmp.getString("ZQZH")); //股东代码
                pstmt_tmp.setString(6, rs_tmp.getString("XWH")); //席位代码
                /* if(rs_tmp.getString("QYLB").equalsIgnoreCase("HL")&& rs_tmp.getString("BDLX").equalsIgnoreCase("00J")){//股票分红
                 sType="06";//派息
                              }else if(rs_tmp.getString("QYLB").equalsIgnoreCase("HL")&& rs_tmp.getString("BDLX").equalsIgnoreCase("00K")){//股票分红到帐
                 sType="06";//分红到帐
                              }else if (rs_tmp.getString("QYLB").equalsIgnoreCase("S")&& rs_tmp.getString("BDLX").equalsIgnoreCase("00J")){//送股
                 sType="07";
                              }else if (rs_tmp.getString("QYLB").equalsIgnoreCase(" ")&& rs_tmp.getString("BDLX").equalsIgnoreCase("00J")&&rs_tmp.getString("LTLX").equalsIgnoreCase("N")&&rs_tmp.getString("ZQLB").equalsIgnoreCase("PZ")){//债券派息
                 sType="06";
                              }else if(rs_tmp.getString("BDLX").equalsIgnoreCase("00G")&&rs_tmp.getString("QYLB").equalsIgnoreCase(" ")&&rs_tmp.getString("LTLX").equalsIgnoreCase("N")&& rs_tmp.getString("ZQLB").equalsIgnoreCase("PT")){//新股转上市
                 sType=" ";
                              }else if (rs_tmp.getString("BDLX").equalsIgnoreCase("00G")&&rs_tmp.getString("QYLB").equalsIgnoreCase(" ")&&rs_tmp.getString("LTLX").equalsIgnoreCase("N")&& rs_tmp.getString("ZQLB").equalsIgnoreCase("GZ")){ //新债转上市
                 sType=" ";
                              }else if(rs_tmp.getString("BDLX").equalsIgnoreCase("00G")&&rs_tmp.getString("QYLB").equalsIgnoreCase(" ")&&rs_tmp.getString("LTLX").equalsIgnoreCase("F")&& rs_tmp.getString("ZQLB").equalsIgnoreCase("XL")){
                 sType="07";//送股
                              }else if(rs_tmp.getString("QYLB").equalsIgnoreCase("DX")&&rs_tmp.getString("BDLX").equalsIgnoreCase("00J")){
                 sType="06";
                              }else sType=" ";
                 */
                String strZqdm = rs.getString("zqdm");
                if (rs_tmp.getString("BDLX").equalsIgnoreCase("HL")) {
                    dCjje = Math.abs( (YssFun.roundIt(YssD.mul(rs_tmp.getInt("bdsl"),
                        calc.getPxJg(strZqdm)), 2)));
                    if (rs_tmp.getString("Bdlx").equalsIgnoreCase("00J")) { //派息
                        sType = "06";
                        //判断如果在现金对价的权益数据里面维护了，那么这条信息标识为"XJDJ"，否则是普通的股票派息凭证。
                        if (calc.getQyLx(strZqdm, "XJDJ") == 1) {
                            strYwbz = "XJDJ";
                        }
                        if (dCjje == 0) {
                            sType = "06";
                        }
                        //bFhState = false;
                        //strBs = "S"; //视同卖出，收到钱
                    } else if (rs_tmp.getString("Bdlx").equalsIgnoreCase("00K")) { //派息款到帐
                        strZqbz = "QY";
                        //判断如果在现金对价的权益数据里面维护了，那么这条信息标识为"XJDJ"，否则是普通的股票派息凭证。
                        if (calc.getQyLx(strZqdm, "XJDJ") == 1) {
                            if (dCjje == 0) {
                                sType = "06DV"; //要做结算
                            }
                        }
                    }
                } else if (rs_tmp.getString("Qylb").equalsIgnoreCase("S") ||
                           (rs_tmp.getString("Zqlb").equalsIgnoreCase("XL") &&
                            rs_tmp.getString("Ltlx").equalsIgnoreCase("F"))) { //送股
                    strZqbz = "QY";
                    sType = "07";
                    //判断如果在现金对价的权益数据里面维护了，那么这条信息标识为"XJDJ"，否则是普通的股票派息凭证。
                    if (calc.getQyLx(strZqdm, "GFDJ") == 1) {
                        sType = "07";
                    }
                    sType = "07";
                    dCjsl = Math.abs(rs.getInt("bdsl"));
                } else if (rs_tmp.getString("Qylb").equalsIgnoreCase("DX")) {
                    strZqbz = "QY";
                    sType = " ";
                    dCjsl = Math.abs(rs.getInt("bdsl"));
                    //先提取交易所国债利息，如果为0则使用非交易所国债利息
                    dCjje = 0;
                    /*dCjje = YssFun.roundIt(YssD.mul(rs.getInt("bdsl") * 10,
                                                    dGzlx == 0 ?
                                                    yg.get_FJysGzlx(strZqdm,
                          strYwbz, "H",
                          base.getDate(), set[i], calc.getFundType(set[i]) == 4).
                                                    doubleJe : dGzlx), 2);*/
                    //先不处理债券
                } else if (rs_tmp.getString("Qylb").equalsIgnoreCase(" ") &&
                           rs_tmp.getString("Bdlx").equalsIgnoreCase("00G") &&
                           rs_tmp.getString("Ltlx").equalsIgnoreCase("N")) {
                    strZqbz = "XG";
                    strYwbz = "ZLT";
                    sType = " ";
                    //strBs = "B"; //新股上市流通作为买，股票投资成本增加
                    dCjsl = Math.abs(rs_tmp.getInt("bdsl"));
                    dCjje = 0;
                }
                pstmt_tmp.setString(7, sType); //交易类型
                pstmt_tmp.setDouble(8, dCjsl); //成交数量
                if (dCjsl == 0) {
                    dCjsl = 1;
                }
                double dPrice = 0;
                dPrice = YssFun.roundIt(YssD.div(dCjje, dCjsl), 2);
                pstmt_tmp.setDouble(9, dPrice); //成交价格
                pstmt_tmp.setDouble(10, dCjje); //成交金额
                //------------------------------------------------------------------
                pstmt_tmp.setString(11, ""); //印花税
                pstmt_tmp.setDouble(12, 0); //经手费
                pstmt_tmp.setString(13, ""); //过户费
                pstmt_tmp.setDouble(14, 0); //征管费
                pstmt_tmp.setString(15, ""); //其他费
                pstmt_tmp.setDouble(16, 0); //佣金
                pstmt_tmp.setString(17, ""); //印花税代码
                pstmt_tmp.setDouble(18, 0); //经手费代码
                pstmt_tmp.setString(19, ""); //过户费代码
                pstmt_tmp.setDouble(20, 0); //征管费代码
                pstmt_tmp.setString(21, ""); //其他费代码
                pstmt_tmp.setDouble(22, 0); //佣金代码

//-----------------------------------------------------------------------
                pstmt_tmp.setDouble(23, 0); //风险金(暂时不填)
                pstmt_tmp.setDouble(24, 0); //债券利息
                pstmt_tmp.setDouble(25, 0); //回购收益
                pstmt_tmp.setString(26, " "); //成交编号
                pstmt_tmp.setDate(27, YssFun.toSqlDate(tradeDate)); //成交日期
                pstmt_tmp.setString(28, securityCode + " CG"); //转化前的证券代码
                pstmt_tmp.setString(29, " ");
                pstmt_tmp.executeUpdate();
            }
            //------------------------------------------------------交易数据表tb_001_data_subTrade----------------------------------------------------------

            /*  strSql = strSql = "insert into " +
                    pub.yssGetTableName("Tb_Data_SubTrade") +
                    "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
                    " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
                    " FSETTLEDATE,FSettleTime,FFactSettleDate,FSettleDesc,FMATUREDATE,FMATURESETTLEDATE," +
                    " FFactCashAccCode,FFactSettleMoney,FExRate,FFactBaseRate,FFactPortRate," +
                    " FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
                    " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST,FBailMoney," +
                    " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                    " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, " +
                    " FTotalCost,FSettleState, FOrderNum, FDesc, FDataSource, FCheckState, FCreator, FCreateTime" +
                    " )" +
                    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                    " ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
              pstmt = dbl.openPreparedStatement(strSql);

              strSql = "delete from "+pub.yssGetTableName("tb_data_subtrade")+" where Fordernum in (select FCjBh from tmp_001_hzjkmx where FExchangeCode=" +
                    dbl.sqlString("SH") + ")";
              dbl.executeSql(strSql);
              strSql = " select * from tmp_001_hzjkmx" +
                    " where FExchangeCode=" + dbl.sqlString("SH");
              rs = dbl.openResultSet(strSql);
              while (rs.next()) {
                 tradeSeat.setSeatCode(rs.getString("FGDDM")); //获取券商
                 tradeSeat.getSetting();
                 if (rs.getString("FTradeTypeCode").equalsIgnoreCase("01")) {
                    strBegin = "200000";
                 }
                 else if (rs.getString("FTradeTypeCode").equalsIgnoreCase("02")) {
                    strBegin = "900000";
                 }

                 strDate = YssFun.formatDatetime(rs.getDate("FInDate")).
                       substring(0, 8);

                 num = "T" + strDate + strBegin;
                 strNum = num + dbFun.getNextInnerCode
                       (pub.yssGetTableName("Tb_Data_SubTrade"),
                        dbl.sqlRight("FNUM", 5), "00001",
                        " where FNUM like '"
                        + num.replaceAll("'", "''") + "%'");
                 pstmt.setString(1, strNum); //交易流水号
                 pstmt.setString(2, rs.getString("FZqdm")); //证券代码
                 pstmt.setString(3, rs.getString("FPortCode")); //组合代码
                 pstmt.setString(4, tradeSeat.getBrokerCode()); //券商
                 pstmt.setString(5, " "); //投资经理
                 pstmt.setString(6, rs.getString("FTradeTypeCode")); //交易方式
                 //-----------------------获取现金帐户的参数---------------------
                 accList.add("");
                 accList.add(sPort);
                 accList.add(rs.getString("FZqdm"));
                 accList.add(tradeSeat.getBrokerCode());
                 accList.add(tradeType);
                 accList.add("CNY");
                 pret.init(accList);
                 pstmt.setString(7, pret.getCashAcc()); //现金帐户
                 //---------------------------------------------------
                 pstmt.setString(8, ""); //所属分类
                 pstmt.setDate(9, rs.getDate("FInDate")); //成交日期
                 pstmt.setString(10, "00:00:00"); //成交时间
                 pstmt.setDate(11, rs.getDate("FDate")); //结算日期
                 pstmt.setString(12, "00:00:00"); //结算时间
                 pstmt.setDate(13, rs.getDate("FDate")); //实际结算日期
                 pstmt.setString(14, " "); //成交描述
                 pstmt.setDate(15, YssFun.toSqlDate(YssFun.toDate("1900-01-01"))); //到期日期
                 pstmt.setDate(16, YssFun.toSqlDate(YssFun.toDate("1900-01-01"))); //到期结算日期
                 pstmt.setString(17, pret.getCashAcc()); //实际结算帐户
                 pstmt.setDouble(18, 0); //实际结算金额
                 pstmt.setDouble(19, 1); //兑换汇率
                 //================================汇率参数===========================
                 ratePortList.add("CNY");
                 ratePortList.add("GFund");
                 ratePortList.add("Base");
                 ratePortList.add(rs.getDate("FDate"));
                 pret.init(ratePortList);
                 portRate = pret.getExchangeRate();
                 //---------------------------
                 rateBaseList.add("CNY");
                 rateBaseList.add("GFund");
                 rateBaseList.add("Base");
                 rateBaseList.add(rs.getDate("FDate"));
                 pret.init(rateBaseList);
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
                 pstmt.setDouble(28, rs.getDouble("FCjsl")); //成交数量
                 pstmt.setDouble(29, rs.getDouble("FCjjg")); //成交价格
                 pstmt.setDouble(30, rs.getDouble("FCJJE")); //成交金额
                 pstmt.setDouble(31, 0); //应计利息
                 pstmt.setDouble(32, 0); //保证金金额
                 pstmt.setString(33, rs.getString("FYhsCode")); //印花税代码
                 pstmt.setDouble(34, rs.getDouble("FYhs")); //印花税
                 pstmt.setString(35, rs.getString("Fjsfcode")); //经手费代码
                 pstmt.setDouble(36, rs.getDouble("FJsf")); //经手费
                 pstmt.setString(37, rs.getString("FGhfCode")); //过户费代码
                 pstmt.setDouble(38, rs.getDouble("FGhf")); //过户费
                 pstmt.setString(39, rs.getString("FZgfCode")); //征管费代码
                 pstmt.setDouble(40, rs.getDouble("FZgf")); //征管费
                 pstmt.setString(41, rs.getString("FQtfCode")); //其他费代码
                 pstmt.setDouble(42, rs.getDouble("FQtf")); //其他费
                 pstmt.setString(43, rs.getString("FYjCode")); //佣金代码
                 pstmt.setDouble(44, rs.getDouble("FYj")); //佣金
                 pstmt.setDouble(45, 0); //实收实付金额
                 pstmt.setInt(46, 1); //结算标识
                 pstmt.setString(47, rs.getString("FCjBh")); //订单编号
                 pstmt.setString(48, ""); //描述
                 pstmt.setInt(49, 1);
                 pstmt.setInt(50, 1); //审核状态
                 pstmt.setString(51, pub.getUserCode()); //创建人、修改人
                 pstmt.setString(52,
                                 YssFun.formatDate(rs.getDate("FInDate"),
                                                   "yyyy-MM-dd")); //创建、修改时间
                 pstmt.executeUpdate();
              }*/

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true); //  chenyibo  20071002
        } catch (Exception e) {
            throw new YssException("", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            dbl.closeStatementFinal(pstmt_tmp);
            dbl.closeResultSetFinal(rs_tmp);
            dbl.closeResultSetFinal(rs);
        }
    }

    public Hashtable getFee(String catCode, String stockHoderCode,
                            String exchangeCode, String brokeCode,
                            String tradeSeat,
                            double tradeMoney, double tradeAmount,
                            String tradeDate) throws YssException {
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

            feeOper.setBrokerCode(brokeCode);
            feeOper.setCatCode(catCode);
            feeOper.setTradeSeatCode(tradeSeat);
            feeOper.setStockholderCode(stockHoderCode);
            feeOper.setExchangeCode(exchangeCode);

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
                        //       table.put(fee.getFeeCode());
                    }
                    for (int j = 0; j < 8 - table.size(); j++) {
                        bufShow.append(" ").append(",");
                        bufShow.append(" ").append(",");
                    }
                }
                if (bufShow.toString().length() > 1) {
                    sResult = bufShow.toString().substring(0,
                        bufShow.toString().length() - 1);
                }

            }
            return table;
        } catch (Exception e) {
            throw new YssException();
        }
    }

    public String getPortBystockHoderCode(String stockHoderCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String sResult = "";
        try {
            strSql = " select FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
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
