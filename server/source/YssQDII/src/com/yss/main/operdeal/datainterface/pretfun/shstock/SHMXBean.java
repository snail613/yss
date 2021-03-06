package com.yss.main.operdeal.datainterface.pretfun.shstock;

import com.yss.util.*;
import java.sql.*;
import com.yss.main.operdeal.datainterface.pretfun.*;

public class SHMXBean
    extends DataBase {
    public SHMXBean(DataMake obj) throws YssException {
        this.setDataMake(obj);
    }

    /**
     * 实际处理上海结算明细库过程
     * @param set String[]    套帐组
     * @throws YssException
     */
    public void makeData(String[] set) throws YssException {
        Connection con = dbl.loadConnection();
        Statement st = null;
        ResultSet rs = null;
        boolean bTrans = false;
        java.util.Date dateQs, dateJs;
        try {
            String strSql = null, sJllx, sYwlx, sQsbz, sXw, sZqlb;
            st = con.createStatement();
            for (int i = 0; i < set.length; i++) {
                int intCount = 3; //由于java的批处理最大支持32767条SQL语句，所以需要进行对SQL语句的数量进行控制。目前约定为10000条提交一次。
                strSql = "delete from HzJsMx where fszsh='H' and findate=" + dbl.sqlDate(base.getDate()); //
                st.addBatch(strSql);
                strSql = "delete from HzJkHz where fszsh='H' and findate=" + dbl.sqlDate(base.getDate()) + " and (FZqbz = 'GP' and FYwBz = 'YYSell') or (FZqbz = 'XJCE' and FYwBz = 'ETFSGSH')"; //============
                st.addBatch(strSql);
                st.addBatch("delete from HzJkMx");
                strSql = "select case when " + dbl.sqlLen(dbl.sqlTrim("a.xwh1")) + "=3 then '00' " + dbl.sqlJN() + dbl.sqlTrim("a.xwh1")
                    + " else " + dbl.sqlTrim("a.xwh1") + " end as xwlb,a.qsrq,a.jsrq,a.jyrq,a.qtrq,a.jllx,a.ywlx,a.qsbz,a.zqzh,a.jyfs"
                    + ",a.mmbz,a.zqdm1,a.zqdm2,a.sl,a.cjsl,a.jg1,a.jg2,a.yhs,a.jsf,a.ghf,a.zgf,a.sxf,a.qsje,a.sjsf,a.cjbh,a.jgdm,a.zqlb from"
                    + " shjsmx a  join csgddm b on a.zqzh=b.fgddm where a.fdate=" + dbl.sqlDate(base.getDate()) + " and a.jllx = '001' and  (a.ywlx = '102' or a.ywlx = '103')  and a.qsbz = '279' and b.fgddm in(select fgddm from " +
                    "csgddm where fsh=1 and Fstartdate in(select max(fstartdate) from csgddm where fstartdate<=" +
                    dbl.sqlDate(base.getDate()) + " group by fgddm))";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    sJllx = rs.getString("Jllx");
                    sYwlx = rs.getString("Ywlx");
                    sQsbz = rs.getString("Qsbz");
                    sZqlb = rs.getString("zqlb");
                    sXw = rs.getString("xwlb");
                    dateQs = YssFun.toDate(YssFun.formatDate(rs.getString("qsrq")));
                    dateJs = YssFun.toDate(YssFun.formatDate(rs.getString("jsrq")));

                    if (dateQs.compareTo(base.getDate()) == 0) {
                        if (sYwlx.indexOf("301") > -1) { //要约收购
                            st.addBatch(jsmxInsert(rs, set[i], sXw, "H", "GP", "YYSell"));
                        } else {
                            if (sJllx.equalsIgnoreCase("001")) {
                                if (sYwlx.equalsIgnoreCase("100")) { //买断式回购：T日本金和保证金数据
                                    if (sQsbz.equalsIgnoreCase("022")) { //本金
                                        st.addBatch(jsmxData(rs, set[i], sYwlx, "HG", "ZQ", "GZXQ"));
                                    } else if (sQsbz.equalsIgnoreCase("271")) { //保证金
                                        st.addBatch(jsmxData(rs, set[i], sYwlx, "HG", "ZQ", "GZXQ"));
                                    }
                                } else if (sYwlx.equalsIgnoreCase("101")) { //买断式回购：R＋1日保证金数据
                                    st.addBatch(jsmxData(rs, set[i], "", "HG", "ZQ", "GZXQ"));
                                } else if (sYwlx.equalsIgnoreCase("102") || sYwlx.equalsIgnoreCase("103")) { //ETF基金申购赎回
                                    sYwlx = sYwlx.equalsIgnoreCase("102") ? "ETFSG" : "ETFSH";
                                    if (sQsbz.equalsIgnoreCase("279")) { //现金差额
                                        st.addBatch(jsmxInsert(rs, set[i], sXw, "H", "XJCE", "ETFSGSH"));
                                    }
                                }
                            } else if (sJllx.equalsIgnoreCase("002")) {
                                if (sYwlx.equalsIgnoreCase("101")) {
                                    st.addBatch(jsmxData(rs, set[i], "QS", "HG", "ZQ", "GZXQ"));
                                }
                            }
                        }
                    } else if (dateJs.compareTo(base.getDate()) == 0) {
                        if (sJllx.equalsIgnoreCase("003") && sYwlx.equalsIgnoreCase("101")) {
                            st.addBatch(jsmxData(rs, set[i], "QS", "HG", "ZQ", "GZXQ"));
                        }
                    }
                    intCount++;
                    if (intCount > 10000) {
                        st.executeBatch();
                        intCount = 0;
                    }
                }
                st.executeBatch();
//==        calc.insert_toHzJkHz_fromHzJkMx(st,set[i]);
            }
            con.setAutoCommit(false); //邵宏伟20060220－调整事务控制
            bTrans = true; //邵宏伟20060220－调整事务控制
            st.executeBatch();
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理上海结算明细库出错！", ex);
        } catch (Exception ex) {
            throw new YssException("处理上海结算明细库出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(con, bTrans);
        }
    }

    /**
     * 保存数据到结算明细库
     * @param rs ResultSet   原始的上海结算明细
     * @param sYwlx String   业务类型（QS是指清算数据，""为系统有提前计入的数据，现在进行更新）
     * @param sXw String     席位号
     * @param sZqbz String   证券标志（主证券类别，买断式回购用）
     * @param sZqlx String   证券标志（）
     * @param sYwbz String   业务类型
     * @return String
     * @throws YssException
     */
    private String jsmxData(ResultSet rs, String set, String Ywlx, String Zqlx, String Zqbz, String Ywbz) throws YssException {
        java.util.Date dateJy = null, dateQt = null, dateQs, dateJs;
        try {
            //==    com.yss.vsub.YssGzFun ys = new com.yss.vsub.YssGzFun(pub);
            String strSql = null, sYwbz = null, sBs = null, sQsbz = null, sJyFs = null, sZqdm = null;
            double dHg = 0, dMoney = 0, dCjsl = 0, dSl = 0, dQsJe = 0, dSfJe = 0;
            sBs = rs.getString("mmbz");
            sJyFs = rs.getString("jyfs");
            sQsbz = rs.getString("qsbz");
            sZqdm = rs.getString("zqdm2");
            if (!Zqlx.equalsIgnoreCase("QZ")) {
                dCjsl = YssD.div(Math.abs(rs.getDouble("cjsl")), 100);
                dSl = YssD.div(Math.abs(rs.getDouble("sl")), 100);
            } else {
                dCjsl = Math.abs(rs.getDouble("cjsl"));
                dSl = Math.abs(rs.getDouble("sl"));
            }
            dateJy = YssFun.toDate(YssFun.formatDate(rs.getString("jyrq")));
            dateQs = YssFun.toDate(YssFun.formatDate(rs.getString("qsrq")));
            dateJs = YssFun.toDate(YssFun.formatDate(rs.getString("jsrq")));

            if (rs.getString("QtRq").trim().length() == 8) {
                dateQt = YssFun.toDate(YssFun.formatDate(
                    rs.getString("QtRq")));
            } else {
                dateQt = dateQs;
            }
            if (Zqlx.equalsIgnoreCase("JJ")) {
                dQsJe = rs.getDouble("QsJe");
                dSfJe = rs.getDouble("Sjsf");
            } else {
                dQsJe = Math.abs(rs.getDouble("QsJe"));
                dSfJe = Math.abs(rs.getDouble("Sjsf"));
            }

            if (Ywlx.trim().length() > 0) {
                if (Ywlx.equalsIgnoreCase("YYSell")) {
                    dateJy = base.getDate();
                } else {
                    dateJy = YssFun.toDate(YssFun.formatDate(rs.getString("jyrq")));

                    if (Ywlx.equalsIgnoreCase("QS")) {
                        if (sQsbz.equalsIgnoreCase("022")) { //买断式回购第一次结算
                            if (sJyFs.equalsIgnoreCase("001")) { //普通交易
                                sYwbz = sBs.equalsIgnoreCase("S") ? "MDMRHG" : "MDMCHG";
                            } else if (sJyFs.equalsIgnoreCase("002")) { //大宗交易
                                sYwbz = sBs.equalsIgnoreCase("S") ? "DZMDMRHG" : "DZMDMCHG";
                            }
                        } else if (sQsbz.equalsIgnoreCase("222")) { //买断式回购第二次结算
                            if (sJyFs.equalsIgnoreCase("001")) { //普通交易
                                sYwbz = sBs.equalsIgnoreCase("S") ? "MDMRHGDQ" : "MDMCHGDQ";
                            } else if (sJyFs.equalsIgnoreCase("002")) { //大宗交易
                                sYwbz = sBs.equalsIgnoreCase("S") ? "DZMDMRHGDQ" : "DZMDMCHGDQ";
                            }
                        } else {
                            sYwbz = " ";
                        }
                    } else if (Ywlx.equalsIgnoreCase("QZ")) {
                        if (sQsbz.equalsIgnoreCase("580")) {
                            sYwbz = "RGQZXQ";
                        } else if (sQsbz.equalsIgnoreCase("590")) {
                            sYwbz = "RZQZXQ";
                        }
                    } else {
                        sYwbz = Ywlx;
                    }
                    /*
                                   if (sYwbz.indexOf("MD") >= 0) { //计算每天的回购收益
                                      com.yss.vsub.CsList cs = ys.get_ZqLxRq(sZqdm, Zqbz, "H", base.getDate());
                                      if (dateQt.compareTo(cs.dateEndRq) > 0) { //期间有债券付息
                                         dMoney = ys.get_FJysGzlx(sZqdm, Zqbz, "H", cs.dateEndRq, 8).doubleJe;
                                         dHg = YssD.add(dHg, YssFun.roundIt(YssD.mul(dMoney, java.lang.Math.abs(dCjsl)), 2));
                                      }
                                      if (dateQt.compareTo(cs.dateStartRq) != 1) {
                                        dHg = YssD.add(dHg, YssFun.roundIt(YssD.mul(ys.get_FJysGzlx(sZqdm, Zqbz, "H", cs.dateStartRq, 8).doubleJe, dCjsl), 2));
                                      }
                                     dHg = YssD.add(dHg, YssFun.roundIt(YssD.mul(ys.get_FJysGzlx(sZqdm, Zqbz, "H", dateQt, 8).doubleJe, dCjsl), 2));
                                      dHg = YssD.sub(dHg, YssFun.roundIt(YssD.mul(ys.get_FJysGzlx(sZqdm, Zqbz, "H", dateJy, 8).doubleJe, dCjsl), 2));
                                   }
                     */
                    strSql = "insert into HzJsMx(FInDate,FJyRq,FQsRq,FJsRq,FQtRq,FSzsh,FJyxwh,FGdDm,FZqbz,FYwbz,"
                        + "FZqdm,FZqbz1,FYwbz1,FZqdm1,FSl,FCjsl,FJsJg,FCjJg,FQsJe,FYhs,FJsf,FGhf,FZgf,FSxf,FBzj,FSfJe,FHgGain,FCjBh,"
                        + "FJgDm,FQsbz) values (" + dbl.sqlDate(base.getDate()) + "," + dbl.sqlDate(dateJy) + "," + dbl.sqlDate(dateQs)
                        + "," + dbl.sqlDate(dateJs) + "," + dbl.sqlDate(dateQt) + ",'H','" + rs.getString("xwlb") + "','"
                        + rs.getString("zqzh") + "','" + Zqlx + "','" + sYwbz + "','" + rs.getString("zqdm1") + "','" + Zqbz + "','"
                        + Ywbz + "','" + sZqdm + "'," + dSl + "," + dCjsl + "," + rs.getDouble("Jg1") + "," + rs.getDouble("Jg2") + ","
                        + dQsJe + "," + rs.getDouble("Yhs") + "," + rs.getDouble("Jsf") + "," + rs.getDouble("ghf") + "," + rs.getDouble("zgf")
                        + "," + rs.getDouble("sxf") + ",0," + dSfJe + "," + dHg + ", '" + rs.getString("Cjbh") + "','"
                        + rs.getString("Jgdm") + "','" + sQsbz + "')";
                }
            } else {
                strSql = "update HzJsMx set fbzj=" + Math.abs(dSfJe) + " where fjyrq="
                    + dbl.sqlDate(dateJy) + " and fqtrq=" + dbl.sqlDate(dateQt) + " and fcjbh='" + rs.getString("Cjbh").trim()
                    + "' and fjyxwh='" + rs.getString("xwlb") + "' and fgddm='" + rs.getString("zqzh") + "'";
                if (rs.getString("ywlx").equalsIgnoreCase("101")) {
                    strSql += " and fqsbz='003'";
                } else if (rs.getString("ywlx").equalsIgnoreCase("100")) {
                    strSql += " and fqsbz='001'";
                }
            }
            return strSql;
        } catch (Exception e) {
            throw new YssException("保存上海结算明细库出错！\tSHMXBean.jsmxData", e);
        }
    }

    /**
     * 保存结算明细库数据到接口明细表
     * @param rs ResultSet
     * @param set String
     * @param sXw String
     * @return String
     * @throws YssException
     */
    private String jsmxInsert(ResultSet rs, String set, String sXw, String sSzSh, String ZqBz, String YwBz) throws YssException {
        String strSql = null;
        try {
            double dYj = 0, dYjMin = 0;
            double dubCjsl = 0;
            if (calc.getAssetType(set) == 0 || calc.getAssetType(set) == 4 || (calc.getAssetType(set) == 1 && calc.getFundType(set) == 1)) { //交易所来源数据的处理办法
//==            dYj = calc.getXwYj(set, sXw, "JJ");
            } else {
                if (ZqBz.equalsIgnoreCase("JJ")) {
                    dubCjsl = rs.getDouble("CjSl");
                    dYj = calc.getYjlv(set, "JJ", rs.getString("zqzh"), sXw, sSzSh);
                    dYjMin = calc.getYjMin(set, "JJ", rs.getString("zqzh"), sXw,
                                           sSzSh);
                }
            }
            if (dYj > 0) {
                dYj = YssFun.roundIt(YssD.mul(rs.getDouble("qsje"), dYj), 2);
                if (dYj < dYjMin) {
                    dYj = dYjMin;
                }
            } else {
                dubCjsl = Math.abs(rs.getDouble("CjSl"));
                dYj = calc.getYjlv(set, YwBz, rs.getString("zqzh"), sXw, "H");
                dYj = YssFun.roundIt(YssD.mul(Math.abs(rs.getDouble("QsJe")), dYj), 2); //成交金额*佣金利率
            }
            strSql = "insert into HzJkMx(fdate,fzqdm,fszsh,fgddm,fjyxwh,fbs,fcjsl,fcjjg,fcjje,fyhs,fjsf,"
                + "fghf,fzgf,fyj,fgzlx,fhggain,fzqbz,fywbz,fcjbh,Zqdm,FJyfs,Findate,FQtf,FFxj) values(" + dbl.sqlDate(base.getDate()) + ",'" + rs.getString("Zqdm1")
                + "','H','" + rs.getString("zqzh") + "','" + sXw + "','" + rs.getString("mmbz") + "'," + dubCjsl + ","
                + rs.getDouble("Jg1") + "," + Math.abs(rs.getDouble("QsJe")) + "," + Math.abs(rs.getDouble("Yhs")) + ","
                + Math.abs(rs.getDouble("Jsf")) + "," + Math.abs(rs.getDouble("Ghf")) + "," + Math.abs(rs.getDouble("Zgf"))
                + "," + dYj + ",0,0,'" + ZqBz + "','" + YwBz + "','" + rs.getString("CJBH") + "','" + rs.getString("zqdm1") + "','" + rs.getString("Qsbz") + "'," + dbl.sqlDate(base.getDate()) + " , 0 , 0)";
            return strSql;
        } catch (Exception e) {
            throw new YssException("保存结算明细库数据到接口明细表出错！\tSHMXBean.jsmxInsert", e);
        }
    }
}
