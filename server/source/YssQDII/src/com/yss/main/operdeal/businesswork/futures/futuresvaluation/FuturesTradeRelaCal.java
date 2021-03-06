package com.yss.main.operdeal.businesswork.futures.futuresvaluation;

import java.sql.*;
import java.util.*;

//---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
import com.yss.commeach.*;
import com.yss.main.operdata.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.main.operdeal.businesswork.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description:计算期货交易关联数据：股值增值、投资收益、卖出估值增值 </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FuturesTradeRelaCal
    extends BaseBusinWork {
    public FuturesTradeRelaCal() {
    }

//   /**
//    * 计算卖出估值增值
//    * @param dWorkDay Date：计算日期
//    * @param sPortCodes String：组合代码
//    * @return ArrayList：纪录投资收益的交易关联数据
//    * @throws YssException
//    */
//   public ArrayList getSellAppreci(java.util.Date dWorkDay, String sPortCodes) throws
//         YssException {
//      String strSql = "";
//      ResultSet rs = null;
//      ArrayList alResult = new ArrayList();
//      try {
//         //如果今日库存小于昨日库存说明有卖出
//         strSql = "SELECT * FROM (SELECT FNum, FMoney, FStorageAmount, FBaseCuryRate, FPortCuryRate, FBaseCuryMoney, FPortCuryMoney" +
//               " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
//               " WHERE FTransDate = " + dbl.sqlDate(dWorkDay) +
//               "  AND FTsfTypeCode = '09FU01') a" +
//               " JOIN (SELECT FNum AS FYNum, FMoney AS FYMoney, FStorageAmount AS FYStorageAmount, FBaseCuryRate AS FYBaseCuryRate, FPortCuryRate AS FYPortCuryRate, FBaseCuryMoney AS FYBaseCuryMoney, FPortCuryMoney AS FYPortCuryMoney" +
//               " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
//               " WHERE FTransDate = " + dbl.sqlDate(YssFun.addDay(dWorkDay, -1)) +
//               "  AND FTsfTypeCode = '09FU01') b ON a.FNum = b.FYNum" +
//               " JOIN (SELECT *" +
//               " FROM " + pub.yssGetTableName("TB_Data_FuturesTrade") +
//               " WHERE FPortCode IN (" + sPortCodes + ")" +
//               " ) c ON a.FNum = c.FNum" +
//               " WHERE FStorageAmount < FYStorageAmount";
//         rs = dbl.openResultSet(strSql);
//         while (rs.next()) {
//            FuturesTradeRelaBean trdRela = new FuturesTradeRelaBean();
//            //卖出数量 = 昨日数量 - 今日数量
//            double dbAmount = YssD.sub(rs.getDouble("FYStorageAmount"),
//                                       rs.getDouble("FStorageAmount"));
//            trdRela.setNum(rs.getString("FNum"));
//            trdRela.setTransDate(dWorkDay);
//            trdRela.setStorageAmount(dbAmount);
//            trdRela.setTsfTypeCode("19FU01");
//            //没有卖光
//            if (rs.getDouble("FStorageAmount") > 0) {
//               //卖出估值增值 = 昨日估值增值 / 昨日数量 * 卖出数量
//               trdRela.setMoney(YssD.round(YssD.mul(YssD.div(rs.getDouble(
//                     "FYMoney"),
//                     rs.
//                     getDouble("FYStorageAmount")),
//                     dbAmount), 2));
//               trdRela.setBaseCuryMoney(YssD.round(YssD.mul(YssD.div(rs.
//                     getDouble(
//                           "FYBaseCuryMoney"), rs.getDouble("FYStorageAmount")),
//                     dbAmount), 2));
//               trdRela.setPortCuryMoney(YssD.round(YssD.mul(YssD.div(rs.
//                     getDouble(
//                           "FYPortCuryMoney"), rs.getDouble("FYStorageAmount")),
//                     dbAmount), 2));
//               trdRela.setBaseCuryRate(rs.getDouble("FYBaseCuryRate"));
//               trdRela.setPortCuryRate(rs.getDouble("FYPortCuryRate"));
//            }
//            //卖光
//            else {
//               //卖出估值增值 = 昨日估值增值
//               trdRela.setMoney(rs.getDouble("FYMoney"));
//               trdRela.setBaseCuryMoney(rs.getDouble("FYBaseCuryMoney"));
//               trdRela.setPortCuryMoney(rs.getDouble("FYPortCuryMoney"));
//               trdRela.setBaseCuryRate(rs.getDouble("FYBaseCuryRate"));
//               trdRela.setPortCuryRate(rs.getDouble("FYPortCuryRate"));
//            }
//            trdRela.setSettleState(1);
//            alResult.add(trdRela);
//         }
//      }
//      catch (Exception e) {
//         throw new YssException("计算期货卖出估值增值出错！\r\n" + e.getMessage());
//      }
//      finally {
//         dbl.closeResultSetFinal(rs);
//      }
//      return alResult;
//   }

    private FuturesTradeRelaBean getSellAppreci(java.util.Date dWorkDay, FuturesTradeBean trade, ResultSet rs, double dbSellAmount) throws
        YssException {
        FuturesTradeRelaBean sellRela = new FuturesTradeRelaBean();
        ResultSet rsApp = null;
        String strSql = "";
        try {
            strSql = "SELECT * FROM " +
                pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FNum = " + dbl.sqlString(trade.getNum()) +
                " AND FTransDate = " + dbl.sqlDate(YssFun.addDay(dWorkDay, -1)) +
                " AND FTsfTypeCode = " +
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU01_MV);
            rsApp = dbl.openResultSet(strSql);
            if (rsApp.next()) {
                sellRela.setNum(trade.getNum());
                sellRela.setCloseNum(rs.getString("FNum"));
                sellRela.setTransDate(dWorkDay);
                sellRela.setTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU01_SMV);
                sellRela.setStorageAmount(dbSellAmount);
                if (dbSellAmount == trade.getCloseAmount()) {
                    sellRela.setMoney(trade.getLastAppMoney());
                } else {
                    double dbMoney = YssD.mul(YssD.div(rsApp.getDouble("FMoney"),
                        rsApp.getDouble(
                            "FStorageAmount")),
                                              dbSellAmount);
                    sellRela.setMoney(dbMoney);
                }
                sellRela.setBaseCuryRate(rsApp.getDouble("FBaseCuryRate"));
                sellRela.setPortCuryRate(rsApp.getDouble("FPortCuryRate"));
                sellRela.setBaseCuryMoney(YssD.mul(sellRela.getMoney(),
                    sellRela.getBaseCuryRate()));
                sellRela.setPortCuryMoney(YssD.div(sellRela.getBaseCuryMoney(),
                    sellRela.getPortCuryRate()));
                sellRela.setSettleState(1);
            }
        } catch (Exception e) {
            throw new YssException("计算期货卖出估值增值出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rsApp);
        }
        return sellRela;
    }

    /**
     * 获取期货库存数量
     * @param dWorkDay Date：业务日期
     * @param sPortCodes String：组合代码
     * @return HashMap：以组合代码和证券代码为 Key，ArrayList 为 Value 的结果哈希表
     * @throws YssException
     */
    public HashMap getFutTradeStock(java.util.Date dWorkDay, String sPortCodes) throws
        YssException {
        HashMap hmResult = new HashMap();
        ArrayList alTrade = null;
        ResultSet rs = null;
        String strSql = "";
        try {
            //---------获取计算日前一天未完全平仓的交易数据，和库存数量余额-----------//
            strSql = "SELECT ft.*, ftr.FStorageAmount, ftr.FMoney" +
                " FROM (SELECT FNum, FStorageAmount, FMoney" +
                " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(YssFun.addDay(dWorkDay, -1)) +
                " AND FTsfTypeCode = '09FU01'" +
                " AND FStorageAmount <> 0) ftr" +
                " JOIN (SELECT *" +
                //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
                " FROM " + pub.yssGetTableName("TB_Data_FuturesTrade_Tmp") +
                " WHERE FCheckState = 1" +
                " AND FBargainDate <= " + dbl.sqlDate(YssFun.addDay(dWorkDay, -1)) +
                " AND FPortCode IN (" + sPortCodes + ")" +
                " AND FTradeTypeCode = '20') ft ON ftr.FNum = ft.FNum" +
                " ORDER BY ft.FNum";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //Hash Key
                String sKey = rs.getString("FPortCode") + "\t" +
                    rs.getString("FSecurityCode");
                alTrade = (ArrayList) hmResult.get(sKey);
                if (alTrade == null) {
                    alTrade = new ArrayList();
                }
                FuturesTradeBean trade = new FuturesTradeBean();
                trade.setNum(rs.getString("FNUM"));
                trade.setBargainDate(YssFun.formatDate(rs.getDate("FBargainDate")));
                trade.setSecurityCode(rs.getString("FSecurityCode"));
                trade.setPortCode(rs.getString("FPortCode"));
                trade.setBrokerCode(rs.getString("FBrokerCode"));
                trade.setInvMgrCode(rs.getString("FInvMgrCode"));
                trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                trade.setTradeAmount(rs.getDouble("FTradeAmount"));
                trade.setBegBailMoney(rs.getDouble("FBegBailMoney"));
                //将交易关联数据中的库存数量放在交易记录中
                trade.setCloseAmount(rs.getDouble("FStorageAmount"));
                trade.setTradePrice(rs.getDouble("FTradePrice"));
                //将昨日股值增值余额关联到交易，计算卖出估值增值时使用
                trade.setLastAppMoney(rs.getDouble("FMoney"));
                //组合代码和证券代码相同的交易记录存放在同一个 ArrayList 中
                alTrade.add(trade);
                hmResult.put(sKey, alTrade);
            }
        } catch (Exception e) {
            throw new YssException("获取期货库存数量出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmResult;
    }

    /**
     * 计算开仓交易的剩余数量、保证金和平仓交易的投资收益
     * 平仓收益使用先入先出法进行计算，计算一条平仓收益时循环之前的开仓交易数据，先开仓先计算
     * 计算平仓收益的同时得到开仓交易在经过平仓后的剩余库存，最后计算剩余的保证金
     * @param dWorkDay Date
     * @param sPortCodes String
     * @param alMtvMethod ArrayList
     * @return ArrayList
     * @throws YssException
     */
    public ArrayList getOpenTradeAmountAndCloseTradeIncome(java.util.Date
        dWorkDay, String sPortCodes, ArrayList alMtvMethod) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        //存放前一天有库存的交易记录
        HashMap hmTrade = new HashMap();
        //交易记录 HashMap 的只读备份
        Map mapTradeBak = null;
        //存放投资收益的交易关联记录, 必须使用哈希表否则在循环估值方法时会出现重复数据
        HashMap hmTradeRela = new HashMap();
        //作为 hmTrade 的 Value
        ArrayList alTrade = null;
        //方法的最终结果，存放估值增值和投资收益的交易关联数据
        ArrayList alResult = new ArrayList();
        MTVMethodBean vMethod = null;
        EachRateOper rateOper = new EachRateOper(); //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
        rateOper.setYssPub(pub);
        try {
            hmTrade = getFutTradeStock(dWorkDay, sPortCodes);
            //得到 hmTrade 的只读备份
            //该方法只能返回 Map
            mapTradeBak = Collections.unmodifiableMap(hmTrade);

            //循环股值方法
            for (int j = 0; j < alMtvMethod.size(); j++) {
                vMethod = (MTVMethodBean) alMtvMethod.get(j);
                //查询当天的交易记录和交易证券相关的行情汇率
                strSql = "SELECT a.*, m.*, idf.FMultiple, idf.FFUType, m.FCsPortCury, sec.FTradeCury FROM" +
                    " (SELECT * FROM " +
                    //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
                    pub.yssGetTableName("TB_Data_FuturesTrade_Tmp") +
                    " WHERE FBargainDate = " + dbl.sqlDate(dWorkDay) +
                    " AND FCheckState = 1" +
                    " AND FPortCode IN (" + sPortCodes + ")" +
                    " ORDER BY FNum DESC) a" +
                    " JOIN (SELECT ma.FLinkCode, mb.FPortCode" +
                    " FROM " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " ma" +
                    " JOIN " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                    " mb" +
                    " ON ma.Fmtvcode = mb.fsubcode" +
                    " WHERE ma.FCheckState = 1" +
                    " AND mb.FCheckState = 1" +
                    " AND ma.FMtvCode = " + dbl.sqlString(vMethod.getMTVCode()) +
                    " AND mb.FRelaType = 'MTV'" +
                    " ) b ON a.Fsecuritycode = b.FLinkCode AND a.FPortCode = b.FPortCode" +
                    " LEFT JOIN (SELECT * FROM " +
                    pub.yssGetTableName("Tb_Para_IndexFutures") +
                    " ) idf ON a.FSecurityCode = idf.FSecurityCode" +
                    " LEFT JOIN (SELECT *" +
                    " FROM " + pub.yssGetTableName("Tb_Para_Security") +
                    " ) sec ON sec.fsecuritycode = a.FSecurityCode" +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            
                    " LEFT JOIN (SELECT FPortCode,FPortName,FPortCury as FCsPortCury " +              
                    " FROM " + pub.yssGetTableName("Tb_Para_Portfolio") +
                    " WHERE FCheckState = 1) m ON a.FPortCode = m.FPortCode";
                
                
                //end by lidaolong
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    //开仓
                    //只获取交易数据
                    if (rs.getString("FTradeTypeCode").equalsIgnoreCase("20")) {
                        FuturesTradeBean trade = new FuturesTradeBean();
                        trade.setNum(rs.getString("FNUM"));
                        trade.setSecurityCode(rs.getString("FSecurityCode"));
                        trade.setBargainDate(YssFun.formatDate(rs.getDate("FBargainDate")));
                        trade.setPortCode(rs.getString("FPortCode"));
                        trade.setBrokerCode(rs.getString("FBrokerCode"));
                        trade.setInvMgrCode(rs.getString("FInvMgrCode"));
                        trade.setTradeTypeCode(rs.getString("FTradeTypeCode"));
                        trade.setTradeAmount(rs.getDouble("FTradeAmount"));
                        trade.setCloseAmount(rs.getDouble("FTradeAmount"));
                        trade.setTradePrice(rs.getDouble("FTradePrice"));
                        trade.setBegBailMoney(rs.getDouble("FBegBailMoney"));
                        alTrade = (ArrayList) hmTrade.get(trade.getPortCode() + "\t" +
                            trade.getSecurityCode());
                        if (alTrade == null) {
                            alTrade = new ArrayList();
                        }
                        alTrade.add(trade);
                        hmTrade.put(trade.getPortCode() + "\t" +
                                    trade.getSecurityCode(),
                                    alTrade);
                    }
                    //平仓
                    //计算平仓收益，和经过平仓后开仓交易的剩余库存数量
                    else if (rs.getString("FTradeTypeCode").equalsIgnoreCase("21")) {
                        //从备份中取出平仓交易对应的开仓交易数据，因为在循环估值方法的过程中，开仓的交易数据的库存数量有可能已经改变
                        hmTrade.put(rs.getString("FPortCode") + "\t" +
                                    rs.getString("FSecurityCode"),
                                    mapTradeBak.get(rs.getString("FPortCode") + "\t" +
                            rs.getString("FSecurityCode")));
                        //取出平仓交易所对应的开仓交易
                        alTrade = (ArrayList) hmTrade.get(rs.getString("FPortCode") +
                            "\t" +
                            rs.getString("FSecurityCode"));
                        //存放平仓的投资收益
                        FuturesTradeRelaBean trdRela = new FuturesTradeRelaBean();
                        //本比平仓交易的交易数量
                        //每循环一笔开仓交易就减去相应的数量，直到平仓数量==0
                        double dbCloseAmount = rs.getDouble("FTradeAmount");
                        //循环平仓交易对应的开仓交易数据，进行先入先出计算
                        for (int i = 0; i < alTrade.size(); i++) {
                            //卖出估值增值的关联数据
                            FuturesTradeRelaBean sellRela = null;

                            FuturesTradeBean trade = (FuturesTradeBean) alTrade.get(i);
                            //如果平仓数量小于开仓交易的库存数量
                            if (trade.getCloseAmount() >= dbCloseAmount) {
                                trdRela.setNum(rs.getString("FNum"));
                                trdRela.setTsfTypeCode("02FU01");
                                trdRela.setTransDate(dWorkDay);
                                //多头
                                if (rs.getString("FFUType").equalsIgnoreCase("BuyAM")) {
                                    //原币投资收益 = (平仓价格 - 开仓价格) * 平仓数量 * 放大倍数
                                    trdRela.setMoney(YssD.add(trdRela.getMoney(),
                                        YssD.round(YssD.mul(YssD.mul(YssD.sub(rs.
                                        getDouble("FTradePrice"), trade.getTradePrice()),
                                        rs.getDouble("FMultiple")), dbCloseAmount), 2)));
                                }
                                //空头
                                else {
                                    //原币投资收益 = (开仓价格 - 平仓价格) * 平仓数量 * 放大倍数
                                    trdRela.setMoney(YssD.add(trdRela.getMoney(),
                                        YssD.
                                        round(YssD.mul(YssD.mul(YssD.sub(trade.getTradePrice(),
                                        rs.
                                        getDouble("FTradePrice")),
                                        rs.getDouble("FMultiple")), dbCloseAmount), 2)));
                                }
                                trdRela.setStorageAmount(trdRela.getStorageAmount() +
                                    dbCloseAmount);
                                //开仓交易的库存数量 = 昨日库存 - 今日平仓数量
                                trade.setCloseAmount(YssD.sub(trade.getCloseAmount(),
                                    dbCloseAmount));
                                //计算卖出估值增值
                                sellRela = getSellAppreci(dWorkDay, trade, rs, dbCloseAmount);
                                hmTradeRela.put(sellRela.getNum() + "\t" + sellRela.getCloseNum(), sellRela);
                                break;
                            }
                            //如果平仓数量大于开仓交易的库存数量
                            else {
                                //平仓数量减去开仓数量
                                dbCloseAmount = YssD.sub(dbCloseAmount,
                                    trade.getCloseAmount());
                                trdRela.setNum(rs.getString("FNum"));
                                trdRela.setTsfTypeCode("02FU01");
                                trdRela.setTransDate(dWorkDay);
                                //多头
                                if (rs.getString("FFUType").equalsIgnoreCase("BuyAM")) {
                                    trdRela.setMoney(YssD.add(trdRela.getMoney(),
                                        YssD.round(YssD.mul(
                                            YssD.mul(YssD.sub(rs.
                                        getDouble("FTradePrice"), trade.getTradePrice()),
                                        rs.getDouble("FMultiple")),
                                            trade.getCloseAmount()), 2)));
                                }
                                //空头
                                else {
                                    trdRela.setMoney(YssD.add(trdRela.getMoney(),
                                        YssD.round(YssD.mul(
                                            YssD.mul(YssD.sub(trade.getTradePrice(),
                                        rs.getDouble("FTradePrice")),
                                        rs.getDouble("FMultiple")),
                                            trade.getCloseAmount()), 2)));
                                }
                                trdRela.setStorageAmount(YssD.add(trdRela.
                                    getStorageAmount(), trade.getCloseAmount()));
                                //计算卖出估值增值
                                sellRela = getSellAppreci(dWorkDay, trade, rs, trade.getCloseAmount());
                                hmTradeRela.put(sellRela.getNum() + "\t" + sellRela.getCloseNum(), sellRela);
                                //开仓交易被完全平仓，库存数量为 0
                                trade.setCloseAmount(0);
                            }
                        }
                        trdRela.setSettleState(1);
                        //----------------计算投资收益的基础货币和组合货币金额-----------------//
                        //组合汇率
                        double dBaseRate = 1;
                        //基础汇率
                        double dPortRate = 1;
                        if (!rs.getString("FTradeCury").equalsIgnoreCase(pub.
                        		getPortBaseCury(rs.getString("FPortCode")))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                            dBaseRate = this.getSettingOper().getCuryRate(dWorkDay,
                                vMethod.getBaseRateSrcCode(),
                                vMethod.getBaseRateCode(),
                                vMethod.getPortRateSrcCode(),
                                vMethod.getPortRateCode(),
                                rs.getString("FTradeCury"), rs.getString("FPortCode"),
                                YssOperCons.YSS_RATE_BASE);
                        }
                        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
//                  dPortRate = this.getSettingOper().getCuryRate(dWorkDay,
//                        vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
//                        vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
//                        rs.getString("FCsPortCury"), rs.getString("FPortCode"),
//                        YssOperCons.YSS_RATE_PORT);
                        rateOper.getInnerPortRate(dWorkDay,
                                                  vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                                  vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                                                  rs.getString("FCsPortCury"), rs.getString("FPortCode"));
                        dPortRate = rateOper.getDPortRate();
                        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
                        trdRela.setBaseCuryRate(dBaseRate);
                        trdRela.setPortCuryRate(dPortRate);
                        //基础货币估值增值
                        trdRela.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                            trdRela.getMoney(),
                            trdRela.getBaseCuryRate()));
                        //组合货币估值增值
                        trdRela.setPortCuryMoney(this.getSettingOper().calPortMoney(
                            trdRela.getMoney(),
                            trdRela.getBaseCuryRate(), trdRela.getPortCuryRate(),
                            //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                            rs.getString("FTradeCury"), dWorkDay, rs.getString("FPortCode")));
                        //----------------------------------------------------//
                        hmTradeRela.put(trdRela.getNum(), trdRela); ;
                    }
                }
                dbl.closeResultSetFinal(rs);
            }
            //将进行过平仓的开仓交易的剩数量存入交易关联表
            Iterator it = hmTrade.values().iterator();
            while (it.hasNext()) {
                ArrayList alTradeRlut = (ArrayList) it.next();
                for (int i = 0; i < alTradeRlut.size(); i++) {
                    FuturesTradeBean trade = (FuturesTradeBean) alTradeRlut.get(i);
                    FuturesTradeRelaBean tradeRela = new FuturesTradeRelaBean();
                    tradeRela.setNum(trade.getNum());
                    tradeRela.setTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU01_MV);
                    tradeRela.setStorageAmount(trade.getCloseAmount());
                    //计算剩余保证金
                    if (tradeRela.getStorageAmount() == trade.getTradeAmount()) {
                        tradeRela.setBailMoney(trade.getBegBailMoney());
                    } else {
                        tradeRela.setBailMoney(YssD.sub(trade.getBegBailMoney(),
                            YssD.
                            mul(YssD.div(trade.
                                         getBegBailMoney(),
                                         trade.getTradeAmount()),
                                YssD.sub(trade.getTradeAmount(),
                                         tradeRela.getStorageAmount()))));
                    }
                    tradeRela.setTransDate(dWorkDay);
                    tradeRela.setSettleState(1);
                    alResult.add(tradeRela);
                }
            }
            //将平仓收益存入交易关联表
            it = hmTradeRela.values().iterator();
            while (it.hasNext()) {
                alResult.add( (FuturesTradeRelaBean) it.next());
            }
        } catch (Exception e) {
            throw new YssException("计算期货平仓收益出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alResult;
    }

    /**
     * 获取有库存的交易的关联数据,计算估值增值
     * @param dWorkDay Date
     * @param sPortCodes String
     * @return ArrayList
     * @throws YssException
     */
    public void getTodayStockFutruesRelaData(java.util.Date dWorkDay,
                                             String sPortCodes,
                                             ArrayList alMtvMethod) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        HashMap hmTradeRela = new HashMap();
        MTVMethodBean vMethod = null;
        ArrayList alValMktPrice = new ArrayList();
        EachRateOper eachOper = null; //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
        try {
            //循环估值方法
            eachOper = new EachRateOper();
            eachOper.setYssPub(pub); //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
            for (int i = 0; i < alMtvMethod.size(); i++) {
                vMethod = (MTVMethodBean) alMtvMethod.get(i);
                //当日库存数量不为0的开仓交易关联记录，就是要计算估值增值的记录
                strSql = "SELECT cs.*, rela.FMoney, rela.FBaseCuryMoney,rela.FPortCuryMoney,mk.FCsMarketPrice,m.FCsPortCury" +
                    " FROM (SELECT a.FNum,a.FBargainDate,a.FSecurityCode as FCsSecurityCode, a.FTradePrice, tr.FStorageAmount,a.FPortCode as FCsPortCode,sec.FTradeCury as FCsCuryCode,sec.FCatCode as FCsCatCode,sec.FFactor as FCsFactor,sec.FSubCatCode,fi.*" +
                    " FROM (SELECT a.*" +
                    //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
                    " FROM " + pub.yssGetTableName("TB_Data_FuturesTrade_Tmp") + " a" +
                    " WHERE FCheckState = 1" +
                    " AND FBargainDate <= " + dbl.sqlDate(dWorkDay) +
                    " AND FPortCode IN (" + sPortCodes + ")) a" +
                    " JOIN (SELECT FNUM, FTsfTypeCode, FStorageAmount" +
                    " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                    " WHERE FTransDate = " + dbl.sqlDate(dWorkDay) +
                    " AND FTsfTypeCode = '09FU01'" +
                    " AND FStorageAmount <> 0) tr ON a.FNum = tr.FNum" +
                    " JOIN (SELECT FSecurityCode,FSecurityName,FCatCode,FSubCatCode,FTradeCury,FFactor" +
                    " FROM " + pub.yssGetTableName("tb_para_security") +
                    " WHERE FCheckState = 1" +
                    " AND FCatCode = 'FU') sec ON a.FSecurityCode = sec.FSecurityCode" +
                    " JOIN (SELECT ma.FLinkCode, mb.FPortCode" +
                    " FROM " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " ma" +
                    " JOIN " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                    " mb" +
                    " ON ma.Fmtvcode = mb.fsubcode" +
                    " WHERE ma.FCheckState = 1" +
                    " AND mb.FCheckState = 1" +
                    " AND ma.FMtvCode = " + dbl.sqlString(vMethod.getMTVCode()) +
                    " AND mb.FRelaType = 'MTV'" +
                    " ) b ON a.Fsecuritycode = b.FLinkCode AND a.FPortCode = b.FPortCode" +
                    " LEFT JOIN (SELECT FSecurityCode,FMultiple,FBailType,FFUType,FBailScale,FBailFix,FBeginBail" +
                    " FROM " + pub.yssGetTableName("Tb_Para_IndexFutures") +
                    " WHERE FCheckState = 1) fi on a.Fsecuritycode = fi.FSecurityCode" +
                    " WHERE tr.FStorageAmount > 0) cs" +
                    " LEFT JOIN (SELECT *" +
                    " FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                    " WHERE FTransDate = " +
                    dbl.sqlDate(YssFun.addDay(dWorkDay, -1)) +
                    " AND FTsfTypeCode = '09FU01') rela on cs.FNum = rela.FNum" +
                    " LEFT JOIN (SELECT mk2.FCsMarketPrice, mk2.FSecurityCode, mk1.FMktValueDate" +
                    " FROM (SELECT max(FMktValueDate) as FMktValueDate,FSecurityCode" +
                    " FROM " + pub.yssGetTableName("Tb_Data_MarketValue") +
                    " WHERE FCheckState = 1" +
                    " AND FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
                    " AND FMktValueDate <= " + dbl.sqlDate(dWorkDay) +
                    " GROUP BY FSecurityCode) mk1" +
                    " JOIN (SELECT " + vMethod.getMktPriceCode() +
                    " as FCsMarketPrice," +
                    " FSecurityCode,FMktValueDate" +
                    " FROM " + pub.yssGetTableName("Tb_Data_MarketValue") +
                    " WHERE FCheckState = 1" +
                    " AND FMktSrcCode = " + dbl.sqlString(vMethod.getMktSrcCode()) +
                    " ) mk2 ON mk1.FSecurityCode = mk2.FSecurityCode AND mk1.FMktValueDate = mk2.FMktValueDate" +
                    " ) mk ON cs.FCsSecurityCode = mk.FSecurityCode" +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                    
             /*       " LEFT JOIN (SELECT mb.*" +
                    " FROM (SELECT FPortCode, MAX(FStartDate) as FStartDate" +
                    " FROM " + pub.yssGetTableName("Tb_Para_Portfolio") +
                    " WHERE FStartDate <= " + dbl.sqlDate(dWorkDay) +
                    " AND FCheckState = 1" +
                    " GROUP BY FPortCode) ma" +
                    " JOIN (SELECT FPortCode,FPortName,FStartDate,FPortCury as FCsPortCury" +
                    " FROM " + pub.yssGetTableName("Tb_Para_Portfolio") +
                    " ) mb ON ma.FPortCode = mb.FPortCode AND ma.FStartDate = mb.FStartDate) m ON cs.FCsPortCode = m.FPortCode";
             */
                    
                    " LEFT JOIN (SELECT FPortCode,FPortName,FPortCury as FCsPortCury" +
                    " FROM " + pub.yssGetTableName("Tb_Para_Portfolio") +
                    " WHERE  FCheckState = 1) m ON cs.FCsPortCode = m.FPortCode";
             
                //end by lidaolong 
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    double dMarketPrice = rs.getDouble("FCsMarketPrice"); //行情价格
                    double dTradePrice = rs.getDouble("FTradePrice"); //交易价格
                    double dTmpAmount = rs.getDouble("FStorageAmount"); //库存数量
                    double dTmpBal = 0; //估值增值
                    double dBaseRate = 1; //基础汇率
                    double dPortRate = 1; //组合汇率
                    if (dMarketPrice == 0) {
                        continue;
                    }
                    if (rs.getString("FCsPortCury") == null) {
                        throw new YssException("请检查投资组合【" +
                                               rs.getString("FCsPortCode") +
                                               "】的币种设置！");
                    }
                    if (!rs.getString("FCsCuryCode").equalsIgnoreCase(pub.
                    		getPortBaseCury(rs.getString("FCsPortCode")))) {// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                        dBaseRate = this.getSettingOper().getCuryRate(dWorkDay,
                            vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                            vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                            rs.getString("FCsCuryCode"), rs.getString("FCsPortCode"),
                            YssOperCons.YSS_RATE_BASE);
                    }
                    //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
//               dPortRate = this.getSettingOper().getCuryRate(dWorkDay,
//                     vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
//                     vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
//                     rs.getString("FCsPortCury"), rs.getString("FCsPortCode"),
//                     YssOperCons.YSS_RATE_PORT);
                    eachOper.getInnerPortRate(dWorkDay,
                                              vMethod.getBaseRateSrcCode(), vMethod.getBaseRateCode(),
                                              vMethod.getPortRateSrcCode(), vMethod.getPortRateCode(),
                                              rs.getString("FCsPortCury"), rs.getString("FCsPortCode"));
                    dPortRate = eachOper.getDPortRate();
                    //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
                    FuturesTradeRelaBean futRale = new FuturesTradeRelaBean();
                    futRale.setNum(rs.getString("FNum"));
                    futRale.setTransDate(dWorkDay);
                    futRale.setBaseCuryRate(dBaseRate);
                    futRale.setPortCuryRate(dPortRate);
                    futRale.setTsfTypeCode("09FU01");

                    //多头
                    if (rs.getString("FFUType").equalsIgnoreCase("BuyAM")) {
                        //今日估值增值余额 = (今日行情 - 交易价格) * 交易数量 * 放大倍数
                        dTmpBal = YssD.mul(YssD.div(YssD.sub(
                            dMarketPrice, dTradePrice), rs.getInt("FCsFactor")),
                                           dTmpAmount, rs.getDouble("FMulTiple"));
                    }
                    //空头
                    else {
                        //今日估值增值余额 = (交易价格 - 今日行情) * 交易数量 * 放大倍数
                        dTmpBal = YssD.mul(YssD.div(YssD.sub(
                            dTradePrice, dMarketPrice), rs.getInt("FCsFactor")),
                                           dTmpAmount, rs.getDouble("FMulTiple"));
                    }
                    futRale.setMoney(YssD.round(dTmpBal, 2));
                    //基础货币估值增值
                    futRale.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                        futRale.getMoney(),
                        futRale.getBaseCuryRate()));
                    //组合货币估值增值
                    futRale.setPortCuryMoney(this.getSettingOper().calPortMoney(
                        futRale.getMoney(),
                        futRale.getBaseCuryRate(), futRale.getPortCuryRate(),
                        //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                        rs.getString("FCsCuryCode"), dWorkDay, rs.getString("FCsPortCode")));
                    hmTradeRela.put(futRale.getNum(), futRale);

                    ValMktPriceBean mktPrice = new ValMktPriceBean();
                    mktPrice.setValDate(dWorkDay);
                    mktPrice.setSecurityCode(rs.getString("FCSSecurityCode"));
                    mktPrice.setPortCode(rs.getString("FCSPortCode"));
                    mktPrice.setPrice(dMarketPrice);
                    alValMktPrice.add(mktPrice);
                }
                //将行情插入估值行情表
                insertValMktPrice(alValMktPrice);
                dbl.closeResultSetFinal(rs);
            }
            //使用计算好的金额更新交易关联记录
            Iterator it = hmTradeRela.values().iterator();
            while (it.hasNext()) {
                FuturesTradeRelaBean trdRela = (FuturesTradeRelaBean) it.next();
                strSql = "UPDATE " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                    " SET FMoney = " + trdRela.getMoney() +
                    " ,FBaseCuryRate = " + trdRela.getBaseCuryRate() +
                    " ,FBaseCuryMoney = " + trdRela.getBaseCuryMoney() +
                    " ,FPortCuryRate = " + trdRela.getPortCuryRate() +
                    " ,FPortCuryMoney = " + trdRela.getPortCuryMoney() +
                    " ,FSettleState = 1" +
                    " WHERE FNum = " + dbl.sqlString(trdRela.getNum()) +
                    " AND FTsfTypeCode = " + dbl.sqlString("09FU01") +
                    " AND FTransDate = " + dbl.sqlDate(dWorkDay);
                dbl.executeSql(strSql);
            }
        } catch (Exception e) {
            throw new YssException("统计期货估值增值出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
