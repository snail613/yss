package com.yss.main.operdeal.datainterface.pretfun.shstock;

import java.sql.*;

import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.util.*;

public class SHDZJYBean
    extends DataBase {
    public SHDZJYBean(DataMake obj) {
        this.setDataMake(obj);
    }

    /**
     * 实际处理上海大宗交易库
     * @param set int[]  套帐组
     * @throws YssException
     */
    public void makeData(String[] set) throws YssException {
        Connection con = dbl.loadConnection();
        Statement st = null;
        ResultSet rs = null;
        boolean bTrans = false, bHgYj = false;
        String ETFSQBH = "", ETFSGSH = "";
        String sJyFs = "DZ";
        try {
            String sXw = null, strOZqdm = null, strZqdm = null, strZqbz = null, strYwbz = null, strBs = null;
            java.util.Date dDate;
            st = con.createStatement();
            String strSql = null;
            con.setAutoCommit(false);
            bTrans = true;
            /*
             A、B股、证券投资基金 经手费 相对于竞价市场同品种费率下浮 30％
             国债、企业债券现券、回购，可转换公司债券 经手费 相对于竞价市场同品种费率下浮 10％
             */
            for (int i = 0; i < set.length; i++) {
                //bHgYj = ywData.varGetValue(set[i] + "交易所回购计算佣金", "0").equalsIgnoreCase("1");
                st.addBatch(
                    "delete from HzJkHz where fszsh='H' and fjyfs ='DZ' and findate=" +
                    dbl.sqlDate(base.getDate()));
                st.addBatch("delete from HzJkMx");
                strSql = "select a.* from SHDZJY a join csgddm b on " +
                    dbl.sqlTrim("upper(a.gddm)") + "=" +
                    dbl.sqlTrim("upper(b.fgddm)") +
                    " where a.fdate=" + dbl.sqlDate(base.getDate()) + " and (" +
                    dbl.sqlLeft("zqdm", 1) + " in ('6','0','1','5') or " +
                    dbl.sqlLeft("zqdm", 2) +
                    " in ('20','70') or " + dbl.sqlLeft("zqdm", 3) + " in ('737','739','740','730','731','704','743','733')) and b.fgddm in(select fgddm from " +
                    "csgddm where fsh=1 and Fstartdate in(select max(fstartdate) from csgddm where fstartdate<=" +
                    dbl.sqlDate(base.getDate()) +
                    " group by fgddm)) order by a.sqbh,a.zqdm"; //先处理同一笔业务,再处理同一只证券
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    double dGhf = 0, dZgf = 0, dJsf = 0, dYhs = 0, dSxf = 0, dYj = 0,
                        dYjMin = 0, dGzlx = 0, dHggain = 0, dCjsl = 0;
                    double TempJsf = 0, TempZgf = 0, dFxj = 0, dZkBl = 1;
                    strZqdm = rs.getString("zqdm");
                    strOZqdm = rs.getString("zqdm");
                    dDate = base.getDate();
                    strBs = rs.getString("bs").toUpperCase();
                    sXw = (rs.getString("gsdm").trim().length() < 5 ? "00" : "") +
                        rs.getString("gsdm").trim().toUpperCase();
                    if (rs.getDouble("cjjg") == 0) { // 可转债券转成股票--其中债转股的补差数据汇总完毕后处理
                        if (strZqdm.equalsIgnoreCase("510050")) {
                            ETFSQBH = rs.getString("sqbh");
                            ETFSGSH = strZqdm;
                        } else {
                            strYwbz = "KZZGP";
                        }
                    }
                    //以下计算各项费用

                    if (strZqdm.startsWith("6")) { // 股票
                        strZqbz = "GP";
                        dCjsl = rs.getDouble("cjsl");
                        if (strZqdm.startsWith("609")) { //上海卖出市值配售的深圳股票，代码替换为深圳股票代码
                            strZqbz = "GP";
                            strYwbz = "PT";
                            strZqdm = strZqdm.replaceFirst("609", "002");
                        }

                        if (calc.getFundType(set[i]) == 1 &&
                            (calc.isZsXw(set[i], sXw) || calc.isZsGp(set[i], strZqdm))) { //指数股票
                            strYwbz = "ZS";
                        } else if (calc.getFundType(set[i]) == 2 &&
                                   calc.isZbGp(set[i], strZqdm)) { //指标股票
                            strYwbz = "ZB";
                        } else {
                            strYwbz = "PT";
                        }
                        if (rs.getString("sqbh").equalsIgnoreCase(ETFSQBH)) {
                            strOZqdm = ETFSGSH;
                            if (strBs.equalsIgnoreCase("S")) {
                                strYwbz = "ETFSG";
                            } else {
                                strYwbz = "ETFSH";
                            }
                        } else if (rs.getDouble("cjjg") == 0) {
                            strYwbz = "KZZGP";
                        }
                    } else if (strZqdm.startsWith("5")) { //==&& !YssFun.oneStart(strZqdm, "580,582")) { // 基金
                        dCjsl = rs.getDouble("cjsl");
                        if (rs.getDouble("cjjg") == 0) { // 可转债券转成股票--其中债转股的补差数据汇总完毕后处理
                            if (strZqdm.equalsIgnoreCase("510050")) {
                                ETFSQBH = rs.getString("sqbh");
                                ETFSGSH = strZqdm;
                                if (strBs.equalsIgnoreCase("B")) {
                                    strYwbz = "ETFSG";
                                } else {
                                    strYwbz = "ETFSH";
                                }
                                strZqbz = "JJ";
                            } else if (strZqdm.equalsIgnoreCase("510051") &&
                                       rs.getString("sqbh").equalsIgnoreCase(ETFSQBH)) {
                                strOZqdm = ETFSGSH;
                                if (strBs.equalsIgnoreCase("B")) {
                                    strYwbz = "ETFSG";
                                } else {
                                    strYwbz = "ETFSH";
                                }
                                strZqbz = "EJDM";
                            } else {
                                strYwbz = "KZZGP";
                            }
                            //以下计算各项费用
                        } else if (strZqdm.equalsIgnoreCase("510052") &&
                                   rs.getString("sqbh").equalsIgnoreCase(ETFSQBH)) {
                            strOZqdm = ETFSGSH;
                            if (strBs.equalsIgnoreCase("B")) {
                                strYwbz = "ETFSG";
                            } else {
                                strYwbz = "ETFSH";
                            }
                            strZqbz = "XJTD";
                        } else {
                            strZqbz = "JJ";
                            if (strZqdm.equalsIgnoreCase("510050")) {
                                strYwbz = "ETF";
                            } else {
                                strYwbz = "FBS";
                            }
                        }
                    } else if (strZqdm.startsWith("0")) { // 国债现券
                        strZqbz = "ZQ";
                        strYwbz = "GZXQ";
                        dCjsl = rs.getDouble("cjsl") * 10;
                    } else if (strZqdm.startsWith("1")) {
                        strZqbz = "ZQ";
                        dCjsl = rs.getDouble("cjsl") * 10;
                        if (strZqdm.startsWith("0", 1) || strZqdm.startsWith("1", 1)) { // 可转债
                            strYwbz = "KZZ";
                        } else if (strZqdm.startsWith("121")) {
                            strYwbz = "ZCZQ";
                        } else { // 企业债券
                            strYwbz = "QYZQ";
                        }
                        if (rs.getDouble("cjjg") == 0) {
                            strYwbz = "KZZGP";
                        }
                    } else if (strZqdm.startsWith("20")) { // 回购
                        strZqbz = "HG";
                        strYwbz = rs.getString("bs").equalsIgnoreCase("S") ? "MR" :
                            "MC";
                        dCjsl = rs.getDouble("cjsl") * 10;
                        if (strZqdm.startsWith("1", 2)) { // 国债回购
                            strYwbz += "HG";
                        } else if (strZqdm.startsWith("2", 2)) { // 企业债券回购
                            strYwbz += "HG_QY";
                        }
                    } else if (strZqdm.startsWith("58")) { // 权证
                        strZqbz = "QZ";
                        dCjsl = rs.getDouble("cjsl");
                        if (YssFun.toInt(YssFun.right(strZqdm, 3)) < 800) {
                            strYwbz = "RGQZ" + (strZqdm.startsWith("0", 2) ? "" : "XQ");
                        } else {
                            strYwbz = "RZQZ" + (strZqdm.startsWith("0", 2) ? "" : "XQ");
                        }
                    } else if (strZqdm.startsWith("70")) {
                        if (rs.getDouble("cjjg") == 100) { // // 老股东配债
                            strZqdm = "110" + YssFun.right(strZqdm, 3);
                            strZqbz = "XZ";
                            strYwbz = "KZZXZ";
                            dCjsl = rs.getDouble("cjsl") * 10;
                            strBs = "B";
                        } else { // 配股
                            strZqdm = strZqdm.replaceFirst("7", "6");
                            strZqbz = "QY";
                            dCjsl = rs.getDouble("cjsl");
                            if (calc.isZsGp(set[i], sXw) ||
                                calc.isZsGp(set[i], strZqdm)) {
                                strYwbz = "ZSPG";
                            } else {
                                strYwbz = "PG";
                            }
                        }
                    } else if (strZqdm.startsWith("73")) {
                        strZqbz = "XG";
                        strYwbz = "SHZQ";
                        dCjsl = rs.getDouble("cjsl");
                        if (strZqdm.startsWith("7", 2)) { // 按市值配售新股T＋1日初次中签(上海中签上海股票)
                            strZqdm = strZqdm.replaceFirst("737", "600");
                        } else if (strZqdm.startsWith("9", 2)) { // 按市值配售新股T＋1日初次中签(上海中签深圳股票)
                            strZqdm = strZqdm.replaceFirst("739", "002");
                        }
                    }
                    if (strZqbz.equalsIgnoreCase("GP") &&
                        !strYwbz.equalsIgnoreCase("KZZGP")) {
                        dYhs = YssFun.roundIt(YssD.mul(rs.getDouble("Cjje"),
                            base.getAgYHS_SH()), 2);
                        dJsf = YssFun.roundIt(YssD.mul(YssD.mul(rs.getDouble("Cjje"),
                            base.getAgJSF_SH()), 0.7), 2);
                        dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("Cjje"),
                            base.getAgZGF_SH()), 2);
                        dGhf = YssFun.roundIt(YssD.mul(rs.getDouble("Cjsl"),
                            base.getAgGHF_SH()), 2);
                    } else if (strZqbz.equalsIgnoreCase("JJ")) {
                        dJsf = YssFun.roundIt(YssD.mul(YssD.mul(rs.getDouble("Cjje"),
                            base.getJjJSF_SH()), 0.7), 2);
                        dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("Cjje"),
                            base.getJjZGF_SH()), 2);
                    } else if (strZqbz.equalsIgnoreCase("ZQ") &
                               !strYwbz.equalsIgnoreCase("KZZGP")) {
                        //先提取交易所国债利息，如果为0则使用非交易所国债利息
                        dGzlx = calc.getGzlx(strZqdm);
//==                  dGzlx = YssFun.roundIt(YssD.mul(rs.getInt("cjsl") * 10,
//==                                                  dGzlx == 0 ?
//==                                                  yg.get_FJysGzlx(strZqdm,
//==                        strYwbz, "H",
//==                        base.getDate(), set[i], calc.getFundType(set[i]) == 4).
//==                                                  doubleJe : dGzlx), 2);
                        if (strYwbz.equalsIgnoreCase("GZXQ") && !calc.isTxZq(strZqdm)) {
                            //上海交易所国债经手费、证管费需加上国债利息计算
                            dJsf = YssFun.roundIt(YssD.mul(YssD.mul(YssD.add(rs.
                                getDouble("Cjje"), dGzlx), base.getGzxqJSF_SH()),
                                0.9), 2);
                            dZgf = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble(
                                "Cjje"), dGzlx), base.getGzxqZGF_SH()), 2);
                        } else {
                            if (strYwbz.equalsIgnoreCase("QYZQ")) {
                                dJsf = YssFun.roundIt(YssD.mul(YssD.mul(rs.getDouble(
                                    "cjje"), base.getQyzqJSF_SH()), 0.9), 2);
                                dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje"),
                                    base.getQyzqZGF_SH()), 2);
                            } else if (strYwbz.equalsIgnoreCase("KZZ")) {
                                dJsf = YssFun.roundIt(YssD.mul(YssD.mul(rs.getDouble(
                                    "cjje"), base.getKzzJSF_SH()), 0.9), 2);
                                dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje"),
                                    base.getKzzZGF_SH()), 2);
                            } else if (strYwbz.equalsIgnoreCase("GZXQ")) { //即贴现债券
                                dJsf = YssFun.roundIt(YssD.mul(YssD.mul(rs.getDouble(
                                    "cjje"), base.getGzxqJSF_SH()), 0.9), 2);
                                dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje"),
                                    base.getGzxqZGF_SH()), 2);
                            } else if (strYwbz.equalsIgnoreCase("ZCZQ")) {
                                dJsf = YssFun.roundIt(YssD.mul(YssD.mul(rs.getDouble(
                                    "cjje"), base.getZcZqJSF_SH()), 0.9), 2);
                                //dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje"), base.getZcZqZGF_SH()), 2);
                                dSxf = YssFun.roundIt(YssD.mul(rs.getDouble("Cjje"),
                                    base.getZcZqSXF_SH()), 2);
                            }
                        }
                    } else if (strZqbz.equalsIgnoreCase("QZ")) { //交易所权证经手费、证管费单独计算，不再按基金标准
                        dJsf = YssFun.roundIt(YssD.mul(rs.getDouble("Cjje"),
                            base.getQzJSF_SH()), 2);
                        dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("Cjje"),
                            base.getQzZGF_SH()), 2);
                        //特别注意：因为不想调整表结构，暂时放在印花税变量里面了，但费率是放在手续费率字段中的。以后再调整。cherry
                        dSxf = YssFun.roundIt(YssD.mul(rs.getDouble("Cjje"),
                            base.getQzSXF_SH()), 2);
                    } else if (strZqbz.equalsIgnoreCase("HG")) { // 计算回购收益
                        if (calc.getHgts(strZqdm) == 0) {
                            throw new YssException("请在交易所费率设置中维护回购品种【" + strZqdm +
                                "】的信息！");
                        }
                        dJsf = YssFun.roundIt(YssD.mul(YssD.mul(rs.getDouble("cjje"),
                            calc.getHglv(strZqdm)), 0.9), 2);
                        dJsf = dJsf < calc.getHgMin(strZqdm) ? calc.getHgMin(strZqdm) :
                            dJsf;
                        dHggain = YssFun.roundIt(YssFun.roundIt(YssD.div(rs.getDouble(
                            "cjjg") * calc.getHgts(strZqdm), 36000), 5) *
                                                 rs.getDouble("Cjsl") * 1000, 2);
                        strSql =
                            "insert into  hggainxx(fdate,fzqdm,fszsh,fts,flv,fhglv,fsl,fhggain) values("
                            + dbl.sqlDate(base.getDate()) + ",'" + strZqdm +
                            "','H'," + calc.getHgts(strZqdm) + "," +
                            rs.getDouble("cjjg")
                            + "," +
                            YssD.round( (rs.getDouble("cjjg") *
                                         calc.getHgts(strZqdm)) / (100 * 360), 5)
                            + "," + rs.getInt("cjsl") + " , " + dHggain + ")";
                        st.addBatch(strSql);
                    } else if (strZqbz.equalsIgnoreCase("GP") &&
                               (strYwbz.equalsIgnoreCase("ETFSG") ||
                                strYwbz.equalsIgnoreCase("ETFSH"))) {
                        dGhf = YssFun.roundIt(YssD.mul(rs.getDouble("Cjsl"),
                            base.getEtfGhf_SH()), 2);
                    }
                    if (strZqbz.equalsIgnoreCase("GP") ||
                        strZqbz.equalsIgnoreCase("JJ")) {
                        dFxj = YssFun.roundIt(YssD.mul(rs.getDouble("cjje"), 0.00003),
                                              2);
                    } else if (strYwbz.equalsIgnoreCase("GZXQ")) {
                        dFxj = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Cjje"),
                            dGzlx), 0.00001), 2);
                    } else if (strYwbz.equalsIgnoreCase("MRHG") ||
                               strYwbz.equalsIgnoreCase("MCHG")) {
                        dFxj = YssFun.roundIt(YssD.mul(rs.getDouble("cjje"), 0.00001),
                                              2);
                    }
                    TempJsf = dJsf;
                    TempZgf = dZgf;

                    if (strZqbz.equalsIgnoreCase("ZQ") ||
                        strZqbz.equalsIgnoreCase("JJ")) {
                        dYj = calc.getYjlv(set[i], strYwbz, rs.getString("gddm"), sXw,
                                           "H");
                        dYjMin = calc.getYjMin(set[i], strYwbz, rs.getString("gddm"),
                                               sXw, "H");
                        dZkBl = calc.getYjZk(set[i], strYwbz, rs.getString("gddm"),
                                             sXw, "H");
                    } else if (strZqbz.equalsIgnoreCase("HG")) {
                        dYj = calc.getYjlv(set[i], strZqdm, rs.getString("gddm"), sXw,
                                           "H");
                        dYjMin = calc.getYjMin(set[i], strZqdm, rs.getString("gddm"),
                                               sXw, "H");
                        if (dYj == 0) {
                            dYj = calc.getYjlv(set[i], strZqbz, rs.getString("gddm"),
                                               sXw, "H");
                            dYjMin = calc.getYjMin(set[i], strZqbz,
                                rs.getString("gddm"), sXw, "H");
                        }
                    } else {
                        dYj = calc.getYjlv(set[i], strZqbz, rs.getString("gddm"), sXw,
                                           "H");
                        dYjMin = calc.getYjMin(set[i], strZqbz, rs.getString("gddm"),
                                               sXw, "H");
                        dZkBl = calc.getYjZk(set[i], strZqbz, rs.getString("gddm"),
                                             sXw, "H");
                    }
                    if (strZqbz.equalsIgnoreCase("ZQ")) {
                        if (calc.isCpCd(set[i], rs.getString("gddm"), strYwbz, "JSF")) {
                            dJsf = 0;
                        }
                        if (calc.isCpCd(set[i], rs.getString("gddm"), strYwbz, "ZGF")) {
                            dZgf = 0;
                        } else {
                            if (calc.isCpCd(set[i], rs.getString("gddm"), strZqbz,
                                            "JSF")) {
                                dJsf = 0;
                            }
                            if (calc.isCpCd(set[i], rs.getString("gddm"), strZqbz,
                                            "ZGF")) {
                                dZgf = 0;
                            }
                            if (calc.isCpCd(set[i], rs.getString("gddm"), strZqbz,
                                            "SXF")) {
                                dSxf = 0;
                            }
                        }
                        if (dYj > 0) {
                            if (strYwbz.equalsIgnoreCase("GZXQ") &&
                                !calc.isTxZq(strZqdm)) {
                                dYj = YssFun.roundIt(YssD.sub(YssD.mul(YssD.add(rs.
                                    getDouble("cjje"), dGzlx), dYj), dJsf, dZgf), 2);
                            } else {
                                dYj = YssFun.roundIt(YssD.sub(YssD.mul(rs.getDouble(
                                    "cjje"), dYj), dJsf, dZgf), 2);
                            }
                            if (dZkBl != 1) {
                                dYj = YssFun.roundIt(YssD.mul(YssD.sub(dYj, dFxj),
                                    dZkBl), 2);
                            }
                            if (dYj < dYjMin) {
                                dYj = dYjMin;
                                //回购的佣金没有折扣
                            }
                            if (strZqbz.equalsIgnoreCase("HG")) {
                                if ( (calc.getAssetType(set[i]) == 1 ||
                                      calc.getAssetType(set[i]) == 2) &&
                                    calc.getFundType(set[i]) == 0) {
                                    if (!bHgYj) {
                                        dJsf = dYj;
                                        dYj = 0;
                                    }
                                } else {
                                    if (bHgYj) {
                                        dYj = 0;
                                    } else {
                                        dYj = dJsf;
                                    }
                                }
                            }
                        }
                        strSql = "insert into HzJkMx(fdate,findate,FJyFs,zqdm,fzqdm,fszsh,fgddm,fjyxwh,fbs,fcjsl,fcjjg,fcjje,"
                            +
                            "fyhs,fjsf,fghf,fzgf,fyj,ffxj,fqtf,fgzlx,fhggain,fzqbz,fywbz,fcjbh) values(" +
                            dbl.sqlDate(dDate) + "," + dbl.sqlDate(base.getDate()) +
                            ",'"
                            + sJyFs + "','" + strOZqdm + "','" + strZqdm +
                            "','H','" + rs.getString("gddm").toUpperCase() + "','" +
                            sXw + "','" + strBs + "'," + dCjsl + ","
                            + rs.getDouble("cjjg") + "," + rs.getDouble("cjje") +
                            "," + dYhs + "," + dJsf + "," + dGhf + "," + dZgf + ","
                            + dYj + "," + dFxj + "," + dSxf + "," + dGzlx + "," +
                            dHggain + ",'" + strZqbz + "','" + strYwbz + "','" +
                            rs.getString("sqbh") + "')";
                        st.addBatch(strSql);
                    }
                    rs.getStatement().close();

                    if ( (calc.getAssetType(set[i]) == 1 ||
                          calc.getAssetType(set[i]) == 2) &&
                        calc.getFundType(set[i]) == 0 ||
                        (calc.getAssetType(set[i]) == 4 &&
                         calc.getFundType(set[i]) == 7)) {
                        st.executeBatch();
//                  calc.insert_toHzJkHz_fromHzJkMx(st, set[i]); //邵宏伟20060220
                    } else {
                        strSql = "insert into HzJkHz " + base.strTableHz +
                            " select fdate,findate,fjyfs,zqdm,fzqdm,fszsh,fgddm,fjyxwh,fbs,sum(fcjsl)," +
                            "sum(fcjje),sum(fyhs),sum(fjsf),sum(fghf),sum(fzgf),sum(fyj), sum(ffxj) ,sum(fqtf),sum(fgzlx),sum(fhggain),fzqbz,fywbz,fcjbh from "
                            + "HzJkMx group by fdate,findate,fjyfs,zqdm,fzqdm,fszsh,fgddm,fjyxwh,fbs,fzqbz,fywbz,fcjbh order by fzqdm,fjyxwh";
                        st.addBatch(strSql);
                        st.executeBatch();
                    }
                }
            }
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理上海大宗交易库数据出错！", ex);
        } catch (YssException e) {
            throw e;
        } catch (Exception ex) {
            throw new YssException("处理上海大宗交易库数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(con, bTrans);
        }
    }
}
