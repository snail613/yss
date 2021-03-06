package com.yss.main.operdeal.datainterface.pretfun.szstock;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.yss.main.operdeal.*;
import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

public class SZGFBean
    extends DataBase {
    private double dYhs = 0, dJsf = 0, dGhf = 0, dZgf = 0, dQtf = 0, dYj = 0,
    dFxj = 0, dZf = 0; //七个费用
    public SZGFBean() {
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
        hzToQs.setYssPub(pub);
        hzToQs.initDate(this.sDate, "CS", "");
        hzToQs.inertData(); //从交易明细--->汇总到到交易子表(最终表)
    }

    /**
     * 实际处理深圳股份库
     * @param set int[]  套帐组
     * @throws YssException  //交易主表
     */
    public void makeData() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false, bReXg = false;
        ResultSet rs = null;
        Statement st = null;
        Date dSettleDate = null;
        String sXw, strOZqdm, strZqdm, strZqbz = "", strYwbz = "", strBs = "",
            sJyFs = "PT", sCatCode = "";
        String tradeType = "";
        String strSql = "";
        Hashtable feeTable;
        PreparedStatement pstmt = null;
        DataCache dc = new DataCache();
        try {
            //gfywlb='A0' or gfywlb='A3' or gfywlb='A6' 新股
            //gfywlb='22' and gfqrgs<0 "    '配股(GFQRGS<0为确认记录)
            //gfywlb='20' and gfqrgs=0 "    '派息
            //gfywlb='20' and gfzjje=0 "    '送股
            //gfywlb='30' "                 '债转股
            //gfywlb='21' and gfzjje=0      '派息
            //gfywlb='33' and gfzjje=0      '可转债回售
            //gfywlb='40' and gfqrgs<0      '要约收购股份转让
            //gfywlb ='L7'                  '深圳ETF申购赎回现金差额  GfZjje > 0 投资者应收款，否则应付款
            //gfywlb ='L8'                  '深圳ETF申购赎回现金差额  GfZjje > 0 退给投资者资金，否则投资者补交资金
            //                              'gfsfzh 第1～8 位表示委托日期，格式是CCYYMMDD，第9～17位
            //                              '表示委托当日的成交号码，第25～30位表示现金替代的组合证券的证券代码。
            SecurityBean security = new SecurityBean();
            security.setYssPub(pub);

            BaseOperDeal deal = new BaseOperDeal();
            deal.setYssPub(pub);
            con.setAutoCommit(false);
            bTrans = true;

            strSql = "delete from " + pub.yssGetTableName("Tb_Data_TradeDetailA") +
                " where FExchangeCode='CS' and FPortCode in(" + operSql.sqlCodes(this.sPort) + ") and FTradeDate in " +
                "(select gffsrq from tmp_sjsgf) and FTradeTypeCode in('09','06','07','05','08','22','04','03','12','19','26','28')";
            dbl.executeSql(strSql);
            //----------------------------------------------------处理交易明细表-----------------------------------------------------------------------------
            strSql = " insert into " + pub.yssGetTableName("Tb_Data_TradeDetailA") +
                " (FPortCode,FSettleDate,FSecurityCode,FExchangeCode,FStockholderNum,FSeatNum,FTradeTypeCode,FTradeAmoumt," +
                " FTradePrice,FTradeMoney,FYhsCode,FYhs,Fjsfcode,FJsf,FGhfCode,FGhf,FZgfCode,FZgf," +
                " FQtfCode,FQtf,FYjCode,FYj,FFxj,FBondIns ," +
                " FHggain,FTradeOrder,FTradeDate,FSrcSecCode,FANALYSISCODE2)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = dbl.openPreparedStatement(strSql);
            //edit by yanghaiming 20100610 MS01257 QDV4赢时胜(上海)2010年5月26日05_B   这里改取席位号
            strSql = " select a.*,b.FSubCode as FStockholderCode from tmp_sjsgf a " +
                " join (select d.fseatnum as fseatnum, c.fsubcode as FSubCode from " +
                pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                " c left join (select * from " + pub.yssGetTableName("tb_para_tradeseat") + " where FExchangeCode = 'CS') d on c.fsubcode = d.fseatcode" +
                " where c.FPortCode in( " + operSql.sqlCodes(this.sPort) +
                " ) and c.FRelaType='TradeSeat') b on a.gfxwdm=b.fseatnum " + //改用席位
                " where a.gfywlb in ('20','21','22','A0','A3','A5','A6','30','W0','W1','W2','W3','W4','W5','33','40')" +
                " and a.IsDel ='False' and a.GFFSRQ =" + dbl.sqlDate(sDate) +
                " order by a.gfzqdm ";
            //---------------------------------------------------
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dc.clear();
                double dCjje = 0, dCjsl = 0, dJsf = 0, dFxj2 = 0, dYhs = 0, dYj = 0;
                bReXg = false;
                String portCode = "";
                sXw = rs.getString("gfxwdm").toUpperCase();
                strOZqdm = rs.getString("gfzqdm");
                //todo:深交所的股份库在网上中签可转债、企业债时（即为A3时），或为老股东配售（即为22）时
                //GFZJJE是对应的金额而并非单价。处理该业务时不能识别是否为债券，因为都为070开头（业务类别是22的以080开头）。
                //判断方法为 rstDbf!Zjje / rstDbf!Qrgs = 100，但仍不能识别为何种债券。
                //考虑深圳中上海新股权益
                if ( ("30,21,33,40".indexOf(rs.getString("Gfywlb"))) >= 0) {
                    //债转股
                    //可转债记录（125×××，000×××）
                    strZqdm = rs.getString("gfzqdm");
                    tradeType = "09";
                } else {
                    strZqdm = "00" + YssFun.right(rs.getString("gfzqdm"), 4);
                    if (YssFun.left(rs.getString("gfzqdm"), 2).equals("18")) { //交易所封闭式基金分红不用转换代码
                        strZqdm = rs.getString("gfzqdm");
                    }
                    //003:深圳的上海股票
                    strZqdm = strZqdm.startsWith("003") ?
                        "600" + YssFun.right(strZqdm, 3) : strZqdm;
                }
                //------------------------------------------------------------------------
                if (rs.getString("Gfywlb").equalsIgnoreCase("20") && rs.getInt("gfQrgs") == 0 && rs.getDouble("gfzjje") > 0) {
                    if (rs.getString("gfzqdm").startsWith("10") || rs.getString("gfzqdm").startsWith("11") || rs.getString("gfzqdm").startsWith("12")) { //深圳债券派息代码不变
                        strZqdm = rs.getString("gfZqdm");
                        strZqbz = "QY";
                        //strYwbz = "ZQPX";
                        tradeType = "06";
                    } else if (rs.getString("gfzqdm").startsWith("18")) { //深圳基金派息代码不变
                        strZqdm = rs.getString("gfZqdm");
                        strZqbz = "QY";
                        //strYwbz = "JJPX";
                        tradeType = "06";
                    } else {
                        strZqbz = "QY";
                        //strYwbz = "PX";
                        tradeType = "06";
                        //判断如果在现金对价的权益数据里面维护了，那么这条信息标识为"XJDJ"，否则是普通的股票派息凭证。
                        if (calc.getQyLx(strZqdm, "XJDJ") == 1) {
                            strYwbz = "XJDJ";
                            tradeType = "06DV";
                        }
                    }
                } else if (rs.getString("Gfywlb").equalsIgnoreCase("20") && rs.getInt("gfQrgs") > 0 && rs.getDouble("gfzjje") == 0) {
                    strZqbz = "QY";
                    //strYwbz = "SG";
                    tradeType = "06";
                    //判断如果在现金对价的权益数据里面维护了，那么这条信息标识为"XJDJ"，否则是普通的股票派息凭证。
                    if (calc.getQyLx(strZqdm, "GFDJ") == 1) {
                        strYwbz = "GFDJ";
                        tradeType = "06DV";
                    }
                    /*if (calc.getAssetType(set[i]) == 0 && calc.getFundType(set[i]) == 1) {
                       if (calc.isZsXw(set[i], sXw) || calc.isZsGp(set[i], strZqdm)) {
                          strYwbz = "ZSSG";
                          if (calc.getQyLx(strZqdm, "CFDJ") == 1) {
                             strYwbz = "ZSGFDJ";
                          }
                       }
                                       }*/
                } else if (rs.getString("Gfywlb").equalsIgnoreCase("22")) {
                    if (YssD.div(rs.getDouble("gfZjje"), rs.getDouble("gfQrgs")) == 100) {
                        // if (pub.getAssetType() == 4)  //社保
                        //strZqdm = "070" + YssFun.right(rs.getString("gfzqdm"), 3);
                        // else
                        //strZqdm = "125" + YssFun.right(rs.getString("gfzqdm"), 3);
                        strZqbz = "XZ";
                        //strYwbz = "KZZXZ";
                        tradeType = "06";
                    } else {
                        strZqbz = "QY";
                        //strYwbz = "PG";
                        //strYwbz = calc.isZsXw(set[i], sXw) || calc.isZsGp(set[i], strZqdm) ? "ZSPG" : "PG";
                        tradeType = "06";
                    }
                } else if (rs.getString("Gfywlb").equalsIgnoreCase("21")) {
                    strZqbz = "QY";
                    //strYwbz = "QZ";
                    tradeType = "06";
                } else if (rs.getString("Gfywlb").equalsIgnoreCase("A0")) {
                    if (rs.getDouble("GFZJJE") == 100) {
                        //交易所而言，一般而言不会发企业债，现默认为可转债
                        if (rs.getString("gfzqdm").equalsIgnoreCase("101699")) {
                            strZqdm = "111032";
                            strZqbz = "XZ";
                            //strYwbz = "QYZQSG";
                            tradeType = "04";
                        } else {
                            strZqdm = "125" + YssFun.right(rs.getString("gfzqdm"), 3);
                            strZqbz = "XZ";
                            //strYwbz = "KZZSG"; //可转债申购
                            tradeType = "04";
                        }
                    } else {
                        strZqbz = "XG";
                        //strYwbz = "XGSG";
                        tradeType = "04";
                    }
                } else if (rs.getString("Gfywlb").equalsIgnoreCase("A3")) {
                    if (rs.getDouble("gfzjje") > 0) {
                        if (rs.getDouble("gfzjje") == 100) {
                            //交易所而言，一般而言不会发企业债，现默认为可转债
                            if (rs.getString("gfzqdm").equalsIgnoreCase("101699")) {
                                strZqdm = "111032";
                                strZqbz = "XZ";
                                //strYwbz = "QYZQZQ";
                                tradeType = " ";
                            } else {
                                strZqdm = "125" + YssFun.right(rs.getString("gfzqdm"), 3);
                                strZqbz = "XZ";
                                //strYwbz = "KZZZQ"; //可转债中签
                                tradeType = " ";
                            }
                        } else {
                            strZqbz = "XG";
                            strYwbz = rs.getString("gfzqdm").startsWith("07") ? "XGZF" : "XGZQ";
                        }
                        /*if ( (calc.getFundGlr(set[i]).indexOf("华夏") > -1 || calc.getFundGlr(set[i]).indexOf("天同") > -1) && calc.getAssetType(set[i]) == 0 &&
                            calc.getFundType(set[i]) == 1) {
                           if (calc.isZsXw(set[i], sXw) || calc.isZsGp(set[i], strZqdm)) {
                              strYwbz = "ZS" + strYwbz;
                           }
                                              }*/
                    } else {
                        strZqbz = "XG";
                        strYwbz = "SZZQ"; //按市值配售上海股票中签（初次中签T＋1日）
                        tradeType = " ";
                        strZqdm = rs.getString("gfzqdm").startsWith("003") ? "600" + YssFun.right(rs.getString("gfzqdm"), 3) : rs.getString("Gfzqdm");
                        /*if ( (calc.getFundGlr(set[i]).indexOf("华夏") > -1 || calc.getFundGlr(set[i]).indexOf("天同") > -1) && calc.getAssetType(set[i]) == 0 &&
                            calc.getFundType(set[i]) == 1) {
                           if (calc.isZsGp(set[i], strZqdm)) {
                              strYwbz = "ZS" + strYwbz;
                           }
                                              }*/
                    }
                } else if (rs.getString("Gfywlb").equalsIgnoreCase("A6")) {
                    if (rs.getDouble("gfzjje") == 100) {
                        if (rs.getString("gfzqdm").equalsIgnoreCase("101699")) {
                            strZqdm = "111032";
                            strZqbz = "XZ";
                            //strYwbz = "QYZQFK";
                            tradeType = "06";
                        } else {
                            strZqdm = "125" + YssFun.right(rs.getString("gfzqdm"), 3);
                            strZqbz = "XZ";
                            //strYwbz = "KZZFK";
                            tradeType = "06";
                        }
                    } else {
                        strZqbz = "XG";
                        //strYwbz = "XGFK";
                        tradeType = "06";
                    }
                } else if (rs.getString("Gfywlb").equalsIgnoreCase("30")) {
                    strZqbz = rs.getString("gfzqdm").startsWith("12") ? "ZQ" : "GP";
                    //strYwbz = "KZZGP";
                    tradeType = " ";
                } else if (rs.getString("Gfywlb").equalsIgnoreCase("33")) { //可转债回售
                    strZqbz = "ZQ";
                    //strYwbz = "KZZHS";
                    tradeType = "05";
                } else if (rs.getString("Gfywlb").equalsIgnoreCase("40")) { //要约收购股份转让
                    strZqbz = "GP";
                    //strYwbz = "YYSell";
                    tradeType = "26";
                }
                if (strYwbz.startsWith("PX") || strYwbz.startsWith("ZQPX") || strYwbz.startsWith("JJPX") || strYwbz.startsWith("XJDJ")) {
                    dCjje = rs.getDouble("gfzjje"); //实收金额
                    strBs = "S";
                } else if ( (strYwbz.endsWith("SG") || strYwbz.equalsIgnoreCase("QZ")) && strZqbz.equalsIgnoreCase("QY")) { //送股
                    dCjsl = rs.getInt("gfqrgs");
                    strBs = "B";
                } else if (strYwbz.endsWith("PG")) { //配股时原表中为负值
                    dCjje = Math.abs(rs.getDouble("gfzjje"));
                    dCjsl = Math.abs(rs.getInt("gfqrgs"));
                    strBs = "B";
                } else if (strYwbz.equalsIgnoreCase("XGSG")) {
                    dCjsl = rs.getDouble("gfqrgs");
                    dCjje = YssFun.roundIt(YssD.mul(dCjsl, rs.getDouble("gfzjje")), 2); //冻结资金
                    strBs = "B";
                } else if (strYwbz.equalsIgnoreCase("XGZQ") || strYwbz.equalsIgnoreCase("XGZF")) {
                    dCjsl = rs.getDouble("gfqrgs");
                    dCjje = YssFun.roundIt(YssD.mul(dCjsl, rs.getDouble("gfzjje")), 2); //中签金额
                    strBs = "B";
                } else if (strYwbz.equalsIgnoreCase("XGFK") || strYwbz.equalsIgnoreCase("KZZFK") || strYwbz.equalsIgnoreCase("QYZQFK")) {
                    dCjsl = rs.getDouble("gfqrgs");
                    dCjje = YssFun.roundIt(YssD.mul(dCjsl, rs.getDouble("gfzjje")), 2); //返款金额
                    strBs = "S";
                } else if (strYwbz.equalsIgnoreCase("KZZZQ") || strYwbz.equalsIgnoreCase("QYZQZQ")) {
                    dCjje = rs.getDouble("gfzjje");
                    dCjsl = rs.getDouble("gfqrgs");
                    strBs = "B";
                } else if (strYwbz.equalsIgnoreCase("KZZXZ")) { //老股东优先配售
                    dCjje = Math.abs(rs.getDouble("gfzjje"));
                    dCjsl = Math.abs(rs.getDouble("gfqrgs"));
                    strBs = "B";
                } else if (strYwbz.equalsIgnoreCase("SZZQ")) {
                    double dXgjg = dc.getDouble(strZqdm);
                    if (dXgjg == 0) {
                        throw new YssException("无法获取配售新股【" + strZqdm + "】发行价格，请到【公用信息】中维护新股发行价格！");
                    }
                    dCjje = YssD.mul(rs.getInt("gfqrgs"), dXgjg);
                    dCjsl = rs.getInt("gfqrgs");
                    strBs = "B";
                } else if (strYwbz.equalsIgnoreCase("KZZGP")) {
                    if (strZqbz.equalsIgnoreCase("GP")) {
                        dCjsl = rs.getInt("gfqrgs");
                        dCjje = rs.getDouble("gfzjje"); //债转股补差
                        strBs = "B";
                    } else {
                        dCjsl = Math.abs(rs.getInt("gfqrgs"));
                        strBs = "S";
                    }
                } else if (strYwbz.equalsIgnoreCase("KZZHS")) {
                    dCjsl = -rs.getInt("gfqrgs");
                    dCjje = dCjsl * calc.getHsJg(strZqdm);
                    dJsf = YssFun.roundIt(YssD.mul(dCjje, YssD.add(base.getKzzJSF_SZ(), base.getKzzZGF_SZ())), 2);
                    strBs = "S";
                } else if (strYwbz.equalsIgnoreCase("YYSell")) {
                    dCjsl = -rs.getInt("gfqrgs");
                    dCjje = YssFun.roundIt(dCjsl * getYYJg(strZqdm), 2);
                    dJsf = YssFun.roundIt(YssD.mul(dCjje, YssD.add(base.getAgJSF_SZ(), base.getAgZGF_SZ())), 2); //成交数量×成交价格×经手费利率
                    /**shashijie 2012-7-2 STORY 2475 */
                    dFxj2 = YssFun.roundIt(YssD.mul(dCjje, 0.00003), 2);
                    /**end*/
                    dYhs = YssFun.roundIt(YssD.mul(dCjje, base.getAgYHS_SZ()), 2); //成交数量×成交价格×印花税利率
                    //dYj = calc.getYjlv(set[i], strYwbz, rs.getString("gfgddm"), sXw, "S");
                    dYj = 0;
                    dYj = YssFun.roundIt(YssD.mul(dCjje, dYj), 2); //成交金额*佣金利率
                    strBs = "S";
                }

                //------------------------------------------------------------------------
                /*if (rs.getString("Gfywlb").equalsIgnoreCase("20") &&
                    rs.getInt("gfQrgs") == 0 && rs.getDouble("gfzjje") > 0) {
                   dCjje = rs.getDouble("gfzjje"); //实收金额
                   tradeType = "06"; //分红，这里应该增加现金对价的判断
                             }
                             else if (rs.getString("Gfywlb").equalsIgnoreCase("20") &&
                         rs.getInt("gfQrgs") > 0 && rs.getDouble("gfzjje") == 0) {
                   dCjsl = rs.getInt("gfqrgs");
                   tradeType = "07"; //送股，这里应该增加股份对价的判断
                             }
                             else if (rs.getString("Gfywlb").equalsIgnoreCase("22")) {
                   dCjje = Math.abs(rs.getDouble("gfzjje"));
                   dCjsl = Math.abs(rs.getInt("gfqrgs"));
                   if (YssD.div(rs.getDouble("gfZjje"), rs.getDouble("gfQrgs")) ==
                       100) {
                      strZqdm = "125" + YssFun.right(rs.getString("gfzqdm"), 3);
                      tradeType = "05"; //老股东配债
                      bReXg = true;
                   }
                   else {
                      tradeType = "08"; //配股确认
                   }
                             }
                             else if (rs.getString("Gfywlb").equalsIgnoreCase("21")) {
                   dCjsl = rs.getInt("gfqrgs");
                   tradeType = "22"; //权证送配
                             }
                             else if (rs.getString("Gfywlb").equalsIgnoreCase("A0")) { //申购
                   dCjsl = rs.getDouble("gfqrgs");
                   if (rs.getDouble("GFZJJE") == 100) {
                      //交易所而言，一般而言不会发企业债，现默认为可转债
                      if (rs.getString("gfzqdm").equalsIgnoreCase("101699")) {
                         strZqdm = "111032";
                      }
                      else {
                         strZqdm = "125" + YssFun.right(rs.getString("gfzqdm"), 3);
                         bReXg = true;
                      }
                   }
                   else {
                      bReXg = true;
                   }
                   tradeType = "04"; //申购
                             }
                             else if (rs.getString("Gfywlb").equalsIgnoreCase("A3")) {
                   dCjsl = rs.getDouble("gfqrgs");
                   dCjje = YssFun.roundIt(YssD.mul(dCjsl, rs.getDouble("gfzjje")),
                                          2); //中签金额
                   if (rs.getDouble("gfzjje") > 0) {
                      if (rs.getDouble("gfzjje") == 100) {
                         //交易所而言，一般而言不会发企业债，现默认为可转债
                         if (rs.getString("gfzqdm").equalsIgnoreCase("101699")) {
                            strZqdm = "111032";
                         }
                         else {
                            strZqdm = "125" +
                                  YssFun.right(rs.getString("gfzqdm"), 3);
                         }
                         dCjje = YssD.mul(rs.getDouble("gfzjje"), 10);
                         dCjsl = rs.getDouble("gfqrgs");
                         tradeType = "04"; //中签
                      }
                      else {
                         tradeType = rs.getString("gfzqdm").startsWith("07") ? "03" :
                               "04";
                      }
                   }
                   else {
                      strZqdm = rs.getString("gfzqdm").startsWith("003") ?
                            "600" + YssFun.right(rs.getString("gfzqdm"), 3) :
                            rs.getString("Gfzqdm");
                      //double dXgjg = dc.getDouble(strZqdm);
                      //if (dXgjg == 0) {
                      //   throw new YssException("无法获取配售新股【" + strZqdm + "】发行价格，请到【公用信息】中维护新股发行价格！");
                      //}
                      //dCjje = YssD.mul(rs.getInt("gfqrgs"), dXgjg);
                      dCjsl = rs.getInt("gfqrgs");

                      tradeType = "05"; //市值配售
                   }
                             }
                             else if (rs.getString("Gfywlb").equalsIgnoreCase("A6")) {
                   dCjsl = rs.getDouble("gfqrgs");
                   dCjje = YssFun.roundIt(YssD.mul(dCjsl, rs.getDouble("gfzjje")),
                                          2); //返款金额
                   if (rs.getDouble("gfzjje") == 100) {
                      if (rs.getString("gfzqdm").equalsIgnoreCase("101699")) {
                         strZqdm = "111032";
                      }
                      else {
                         strZqdm = "125" + YssFun.right(rs.getString("gfzqdm"), 3);
                      }
                   }
                   tradeType = "12"; //返款
                             }
                             else if (rs.getString("Gfywlb").equalsIgnoreCase("30")) {
                   strZqbz = rs.getString("gfzqdm").startsWith("12") ? "ZQ" : "GP";
                   //else if (strYwbz.equalsIgnoreCase("KZZGP")) {
                   //   if (strZqbz.equalsIgnoreCase("GP")) {
                   //      dCjsl = rs.getInt("gfqrgs");
                   //      dCjje = rs.getDouble("gfzjje"); //债转股补差
                   //   }
                   //   else {
                   //      dCjsl = Math.abs(rs.getInt("gfqrgs"));
                   //   }
                   //}
                   tradeType = "09"; //债转股
                             }
                             else if (rs.getString("Gfywlb").equalsIgnoreCase("33")) { //可转债回售
                   dCjsl = -rs.getInt("gfqrgs");
                   dCjje = dCjsl * calc.getHsJg(strZqdm);
                   dJsf = YssFun.roundIt(YssD.mul(dCjje,
                                                  YssD.add(base.getKzzJSF_SZ(),
                         base.getKzzZGF_SZ())), 2);
                   strBs = "S";
                   tradeType = "19"; //回售
                             }
                             else if (rs.getString("Gfywlb").equalsIgnoreCase("40")) { //要约收购股份转让
                   dCjsl = -rs.getInt("gfqrgs");
                   dCjje = YssFun.roundIt(dCjsl * getYYJg(strZqdm), 2);
                   //成交数量×成交价格×经手费利率
                   //dJsf = YssFun.roundIt(YssD.mul(dCjje, YssD.add(base.getAgJSF_SZ(), base.getAgZGF_SZ())), 2);
                   //dFxj = YssFun.roundIt(YssD.mul(dCjje, 0.00003), 2);
                   //dYhs = YssFun.roundIt(YssD.mul(dCjje, base.getAgYHS_SZ()), 2); //成交数量×成交价格×印花税利率
                   //dYj = calc.getYjlv(set[i], strYwbz, rs.getString("gfgddm"), sXw, "S");
                   //dYj = YssFun.roundIt(YssD.mul(dCjje, dYj), 2); //成交金额*佣金利率
                   tradeType = "26"; //要约收购
                             }
                             else if (rs.getString("Gfywlb").equalsIgnoreCase("L7")) { //T-1日ETF申购/赎回的现金差额
                   strZqdm = rs.getString("gfzqdm");
                   dCjje = rs.getDouble("gfzjje");
                   tradeType = "28"; //现金差额
                             }*/

                security.setSecurityCode(strZqdm + " CS");
                security.setStartDate(rs.getDate("GFFSRQ"));
                security.getSetting();
                sCatCode = security.getCategoryCode();
                dSettleDate = YssFun.toSqlDate(deal.getWorkDay(security.
                    getHolidaysCode(),
                    rs.getDate("GFFSRQ"), security.getSettleDays()));
                portCode = getPortBystockHoderCode(rs.getString("FStockholderCode"));
                pstmt.setString(1, portCode);
                pstmt.setDate(2, dSettleDate);
                pstmt.setString(3, strZqdm + " CS");
                pstmt.setString(4, "CS"); //交易所
                pstmt.setString(5, rs.getString("FStockholderCode")); //股东
                pstmt.setString(6, YssFun.left(rs.getString("GFWTXH"), 6));
                pstmt.setString(7, tradeType);
                pstmt.setDouble(8, dCjsl);
                pstmt.setDouble(9, dCjje);
                if (dCjsl > 0) {
                    pstmt.setDouble(10, YssD.div(dCjje, dCjsl));
                } else {
                    pstmt.setDouble(10, 0);
                }
                //======================================
                pstmt.setString(11, " "); //印花税
                pstmt.setDouble(12, dYhs); //七个费用
                pstmt.setString(13, " ");
                pstmt.setDouble(14, dJsf);
                pstmt.setString(15, " ");
                pstmt.setDouble(16, dGhf);
                pstmt.setString(17, " ");
                pstmt.setDouble(18, dZgf);
                pstmt.setString(19, " ");
                pstmt.setDouble(20, dQtf);
                pstmt.setString(21, " ");
                pstmt.setDouble(22, dYj);
                /* feeTable = this.getFee(sCatCode,
                                        rs.getString("GFGDDM"), "CS",
                                        "", sXw,
                                        dCjje,
                                        dCjsl,
                                        YssFun.formatDate(rs.getDate("GFFSRQ"),"yyyy-MM-dd"),
                                        portCode);
                 if (sCatCode.equalsIgnoreCase("EQ")) {
                    pstmt.setString(11, "SZAGYHS"); //印花税代码
                    if (feeTable.get("SZAGYHS") != null) {
                       pstmt.setDouble(12,
                                           ( (Double) feeTable.get("SHAGYHS")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(12, 0); //印花税
                    }
                     pstmt.setString(13, "SHAGZGF"); //经手费代码
                    if (feeTable.get("SHAGZGF") != null) {
                       pstmt.setDouble(14,
                                           ( (Double) feeTable.get("SHAGZGF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(14, 0); //经手费
                    }
                    pstmt.setString(15, "SHAGGHF"); //过户费代码
                    if (feeTable.get("SHAGGHF") != null) {
                       pstmt.setDouble(16,
                                           ( (Double) feeTable.get("SHAGGHF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(16, 0); //过户费
                    }
                     pstmt.setString(17, "SHAGJSF"); //征管费代码
                    if (feeTable.get("SHAGJSF") != null) {
                       pstmt.setDouble(18,
                                           ( (Double) feeTable.get("SHAGJSF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(18, 0); //征管费
                    }
                    pstmt.setString(19, ""); //其他费代码
                    pstmt.setDouble(20, 0); //其他费
                    pstmt.setString(21, ""); //佣金代码
                    pstmt.setDouble(22, 0); //佣金
                 }
                 else if (security.getCategoryCode().equalsIgnoreCase("TR")) {
                    pstmt.setString(11, "SZJJYHS"); //印花税代码
                    if (feeTable.get("SHJJYHS") != null) {
                       pstmt.setDouble(12,
                                           ( (Double) feeTable.get("SHJJYHS")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(12, 0); //印花税
                    }
                    pstmt.setString(13, "SHJJJSF"); //经手费代码
                    if (feeTable.get("SZJJJSF") != null) {
                       pstmt.setDouble(14,
                                           ( (Double) feeTable.get("SHJJJSF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(14, 0); //经手费
                    }
                    pstmt.setString(15, "SHJJGHF"); //过户费代码
                    if (feeTable.get("SZJJGHF") != null) {
                       pstmt.setDouble(16,
                                           ( (Double) feeTable.get("SHJJGHF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(16, 0); //过户费
                    }
                    pstmt.setString(17, "SZJJZGF"); //征管费代码
                    if (feeTable.get("SZJJZGF") != null) {
                       pstmt.setDouble(18,
                                           ( (Double) feeTable.get("SHJJZGF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(18, 0); //征管费
                    }
                    pstmt.setString(19, ""); //其他费代码
                    pstmt.setDouble(20, 0); //其他费
                    pstmt.setString(21, ""); //佣金代码
                    pstmt.setDouble(22, 0); //佣金
                 }
                 else if (security.getCategoryCode().equalsIgnoreCase("OP")) {
                    if (feeTable.get("SHQZYHS") != null) {
                       pstmt.setDouble(12,
                                           ( (Double) feeTable.get("SHQZYHS")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(12, 0); //印花税
                    }
                    if (feeTable.get("SHQZJSF") != null) {
                       pstmt.setDouble(14,
                                           ( (Double) feeTable.get("SHQZJSF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(14, 0); //经手费
                    }
                    if (feeTable.get("SHQZGHF") != null) {
                       pstmt.setDouble(16,
                                           ( (Double) feeTable.get("SHQZGHF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(16, 0); //过户费
                    }
                    if (feeTable.get("SHQZZGF") != null) {
                       pstmt.setDouble(18,
                                           ( (Double) feeTable.get("SHQZZGF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(18, 0); //征管费
                    }
                    pstmt.setDouble(20, 0); //其他费
                    pstmt.setDouble(22, 0); //佣金
                    pstmt.setString(11, "SHQZYHS"); //印花税代码
                    pstmt.setString(13, "SHQZJSF"); //经手费代码
                    pstmt.setString(15, "SHQZGHF"); //过户费代码
                    pstmt.setString(17, "SHQZZGF"); //征管费代码
                    pstmt.setString(19, ""); //其他费代码
                    pstmt.setString(21, ""); //佣金代码
                 }
                 else if (security.getCategoryCode().equalsIgnoreCase("FI")) {
                    if (feeTable.get("SHZQYHS") != null) {
                       pstmt.setString(11, "SHZQYHS"); //印花税代码
                       pstmt.setDouble(12,
                                           ( (Double) feeTable.get("SHZQYHS")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(12, 0); //印花税
                    }
                    if (feeTable.get("SHZQJSF") != null) {
                        pstmt.setString(13, "SHZQJSF"); //经手费代码
                       pstmt.setDouble(14,
                                           ( (Double) feeTable.get("SHZQJSF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(14, 0); //经手费
                    }
                    if (feeTable.get("SHZQGHF") != null) {
                       pstmt.setString(15, "SHZQGHF"); //过户费代码
                       pstmt.setDouble(16,
                                           ( (Double) feeTable.get("SHZQGHF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(16, 0); //过户费
                    }
                    if (feeTable.get("SHZQZGF") != null) {
                       pstmt.setString(17, "SHZQZGF"); //征管费代码
                       pstmt.setDouble(18,
                                           ( (Double) feeTable.get("SHZQZGF")).
                                           doubleValue());
                    }
                    else {
                       pstmt.setDouble(18, 0); //征管费
                    }
                    pstmt.setString(19, ""); //其他费代码
                    pstmt.setDouble(20, 0); //其他费
                    pstmt.setString(21, ""); //佣金代码
                    pstmt.setDouble(22, 0); //佣金
                 }
                 else if (security.getCategoryCode().equalsIgnoreCase("RE")) {
                    pstmt.setString(11, ""); //印花税代码
                    pstmt.setDouble(12, 0); //印花税
                    pstmt.setString(13, ""); //经手费代码
                    pstmt.setDouble(14, 0); //经手费
                    pstmt.setString(15, ""); //过户费代码
                    pstmt.setDouble(16, 0); //过户费
                    pstmt.setString(17, ""); //征管费代码
                    pstmt.setDouble(18, 0); //征管费
                    pstmt.setString(19, ""); //其他费代码
                    pstmt.setDouble(20, 0); //其他费
                    pstmt.setString(21, ""); //佣金代码
                    pstmt.setDouble(22, 0); //佣金
                 }*/
                //============================================
                pstmt.setDouble(23, 0); //风险金(暂时不填)
                pstmt.setDouble(24, 0); //债券利息
                pstmt.setDouble(25, 0); //回购收益
                pstmt.setString(26, rs.getString("GFXWDM"));
                pstmt.setDate(27, rs.getDate("GFFSRQ"));
                pstmt.setString(28, rs.getString("GFZQDM"));
                pstmt.setString(29, " ");
                pstmt.executeUpdate();
            }
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理深圳股份库数据出错！", ex);
        } catch (Exception ex) {
            throw new YssException("处理深圳股份库数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(con, bTrans);
        }
    }

    private double getYYJg(String sZqdm) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double cjjg = 0;
        try {
            strSql = "select case when FYClosePrice > 0 then FYClosePrice else FAveragePrice end as cjjg from " +
                pub.yssGetTableName("Tb_Data_MarketValue") +
                " where FSecurityCode = '" + sZqdm + "' and FMktValueDate = " +
                dbl.sqlDate(base.getDate()) +
                " and FDataSource = 0 and FMktSrcCode = 'CS' order by FSecurityCode desc ";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                cjjg = rs.getDouble("cjjg");
            } else {
                throw new YssException("请在场外行情维护页面设置要约收购股票的成交价格！");
            }
            rs.getStatement().close();
            return cjjg;
        } catch (Exception ex) {
            throw new YssException("获取要约收购价格出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

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

    /* public Hashtable getFee(String catCode, String stockHoderCode,
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

           feeOper.setBrokerCode("");
           feeOper.setCatCode(catCode);
           feeOper.setTradeSeatCode(tradeSeat);
           feeOper.setStockholderCode("");
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
}
