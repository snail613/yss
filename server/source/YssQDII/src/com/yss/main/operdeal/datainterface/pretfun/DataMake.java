package com.yss.main.operdeal.datainterface.pretfun;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.operdeal.datainterface.pretfun.shstock.*;
import com.yss.util.*;

public class DataMake
    extends DataBase {
    private CalcBean base = null;
    private DataCache cache = null;
    public DataMake() {}

    public DataMake(YssPub ysspub, java.util.Date date) throws YssException {
        this.setYssPub(ysspub);
        base = new CalcBean(pub, date);
        cache = new DataCache();
    }

    public CalcBean getBase() {
        return base;
    }

    public final void doMake(String[] set) throws YssException {
        init(set);
        //new SHDzDataBean(base).makeData(set);
        //上海数据 顺序：过户库->结算明细库->权益库
//      new SHGHBean(this).makeData(set);
//==      new SHZGHBean(this).makeData(set);
//      new SHDZJYBean(this).makeData(set); //大宗交易jzx
//      new SHMXBean(this).makeData(set);
        if (base.getDate().before(YssFun.toDate("2006-09-21"))) {
            new SHG4Bean(this).makeData(set);
        } else {
//         new SHZQBDBean(this).makeData(set);
        }
        //深圳数据　顺序：回报库->权益库
        // new SZHBBean(this).makeData(set);
        //new SZGFBean(this).makeData(set);
//==       new HzToQs(this).makeData(set);
//==       new EndDataBean(this).makeData(set);
    }

    /**
     * 初始化缓存数据
     * @param set int[]   套帐组
     * @throws YssException
     */
    public void init(String[] set, boolean hasSet) throws YssException {
        ResultSet rs = null;
        String strSql = null;
        try {
            strSql = "select max(fstartdate),FJJdm,fzqdm from cszqxx where flvlx='贴现债券' and fstartdate<="
                + dbl.sqlDate(base.getDate()) + " and fsh=1 group by fzqdm,FJJdm";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                cache.setBoolean("TXZQ" + rs.getString("FJjdm").trim() + rs.getString("fzqdm"), true);
            }
            rs.getStatement().close();

            strSql = "select fgzdm,fyjlx from JjGzLx where fjxrq=" + dbl.sqlDate(base.getDate());
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                cache.setDouble("GZLX" + rs.getString("fgzdm"), rs.getDouble("fyjlx"));
            }
            rs.getStatement().close();
            strSql = "select max(fstartdate),fzqlb,flv,fje,fother,fjjdm,fxwgd,ffvlb,fszsh from csjylv where fstartdate<=" + dbl.sqlDate(base.getDate()) +
                " and fsh=1 group by fzqlb,flv,fje,fother,fjjdm,fxwgd,ffvlb,fszsh";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("ffvlb").equalsIgnoreCase("SXF") && rs.getString("fzqlb").indexOf("HG") >= 0) {
                    cache.setInt("HGTS" + YssFun.right(rs.getString("fzqlb"), 6), rs.getInt("fother"));
                    cache.setDouble("HGLV" + YssFun.right(rs.getString("fzqlb"), 6), rs.getDouble("flv"));
                    cache.setDouble("HGMin" + YssFun.right(rs.getString("fzqlb"), 6), rs.getDouble("fje"));
                } else {
                    cache.setDouble(rs.getString("fjjdm") + rs.getString("fzqlb") + rs.getString("fxwgd").trim() + rs.getString("ffvlb") + "Lv_" + rs.getString("FSzSh"),
                                    rs.getDouble("flv"));
                    cache.setDouble(rs.getString("fjjdm") + rs.getString("fzqlb") + rs.getString("fxwgd").trim() + rs.getString("ffvlb") + "Min_" + rs.getString("FSzSh"),
                                    rs.getDouble("fje"));
                }
            }
            rs.getStatement().close();
            strSql = "select max(fstartdate),fzqdm,fqybl,fqyjg,fqylx from csqyxx where (fqylx='PX' or fqylx='XJDJ') and fqydjr<=" + dbl.sqlDate(base.getDate())
                + " and fjkjzr>=" + dbl.sqlDate(base.getDate()) + " and fstartdate<=" + dbl.sqlDate(base.getDate())
                + " and fqybl not in('银行间','上交所','深交所') and fsh=1 group by fzqdm,fqybl,fqyjg,fqylx";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                double dtem1 = 0, dtem2 = 0;
                if (rs.getString("fqybl").indexOf(":") > 0) {
                    dtem1 = Double.parseDouble(YssFun.left(rs.getString("fqybl"), rs.getString("fqybl").indexOf(":") - 1));
                    dtem2 = Double.parseDouble(YssFun.right(rs.getString("fqybl"),
                        rs.getString("fqybl").length() - rs.getString("fqybl").indexOf(":") - 1));
                    cache.setDouble("PX" + rs.getString("fzqdm"), dtem1 > 0 ? YssFun.roundIt(YssD.div(dtem2, dtem1), 2) : 0);
                } else {
                    cache.setDouble(rs.getString("fqylx") + rs.getString("fzqdm"), Double.parseDouble(rs.getString("fqybl")));
                }
                cache.setDouble("SQ" + rs.getString("fqylx") + rs.getString("fzqdm"), rs.getDouble("fqyjg"));
            }
            rs.getStatement().close();
            strSql = "select max(fstartdate),fzqdm,fqybl,fqylx from csqyxx where fqylx='PG' and fqydjr<=" + dbl.sqlDate(base.getDate())
                + " and fjkjzr>=" + dbl.sqlDate(base.getDate()) + " and fstartdate<=" + dbl.sqlDate(base.getDate())
                + " and fqybl not in('银行间','上交所','深交所') and fsh=1 group by fzqdm,fqybl,fqylx";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                cache.setBoolean("PGGP" + rs.getString("fzqdm"), true);
            }
            rs.getStatement().close();

            if (hasSet) {
                for (int i = 0; i < set.length; i++) {
                    strSql = "select max(fstartdate),fqsdm,fqsxw,fxwlb from CsQsXw where fstartdate<="
                        + dbl.sqlDate(base.getDate()) + " and fsh=1 group by fqsdm,fqsxw,fxwlb";
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) { //指数席位
                        if (rs.getString("fxwlb").equalsIgnoreCase("ZS") || rs.getString("fxwlb").equalsIgnoreCase("ZYZS")) {
                            cache.setBoolean(set[i] + "ZS" + rs.getString("fqsxw").toUpperCase(), true);
                        }
                        cache.setString(set[i] + rs.getString("fqsxw").toUpperCase() + "XWDM", rs.getString("fqsdm").toUpperCase());
                    }
                    rs.getStatement().close();

                    strSql = "select max(fstartdate),fzqdm,fbz from CsTsKm where fstartdate<="
                        + dbl.sqlDate(base.getDate()) + " and fsh=1 and fbz in(2,3) group by fzqdm,fbz";
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        if (rs.getInt("fbz") == 2) { //指标股票
                            cache.setBoolean(set[i] + "ZB" + rs.getString("fzqdm"), true);
                        } else { //指数股票
                            cache.setBoolean(set[i] + "ZS" + rs.getString("fzqdm"), true);
                        }
                    }
                    rs.getStatement().close();
                    strSql = "select max(fstartdate),fjjlb,fjjlx,fjjglren from cssysjj where fstartdate<="
                        + dbl.sqlDate(base.getDate()) + " and fsh=1 group by fjjlb,fjjlx,fjjglren";
                    rs = dbl.openResultSet(strSql);
                    if (rs.next()) {
                        cache.setInt(set[i] + "FundType", rs.getInt("fjjlb"));
                        cache.setInt(set[i] + "AssetType", rs.getInt("fjjlx"));
                        cache.setString(set[i] + "FundGlr", rs.getString("fjjglren"));
                    }
                    rs.getStatement().close();

                    //strSql = msd.getMaxStartDateSql( + "CsYjLv" , "Set" , base.getDate() , "" , "fzqlb,fstr1,flv,flvmin,FSzSh,flvzk" );
                    strSql = "select max(fstartdate),fzqlb,fstr1,flv,flvmin,FSzSh,flvzk from csyjlv where fstartdate<="
                        + dbl.sqlDate(base.getDate()) + " and fsh=1 group by fzqlb,fstr1,flv,flvmin,FSzSh,flvzk";
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        cache.setDouble(set[i] + rs.getString("fzqlb") + rs.getString("fstr1").trim() + "Lv_" + rs.getString("FSzSh"), rs.getDouble("flv"));
                        cache.setDouble(set[i] + rs.getString("fzqlb") + rs.getString("fstr1").trim() + "Min_" + rs.getString("FSzSh"), rs.getDouble("flvmin"));
                        cache.setDouble(set[i] + rs.getString("fzqlb") + rs.getString("fstr1").trim() + "ZK_" + rs.getString("FSzSh"), rs.getDouble("flvzk"));
                    }
                    rs.getStatement().close();

                    strSql = "select max(fstartdate),ffyfs,fqsxw,fzqlb,ffylb from CsXwFy where fstartdate<="
                        + dbl.sqlDate(base.getDate()) + " and fsh=1 group by ffyfs,fqsxw,fzqlb,ffylb";
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        cache.setInt(set[i] + rs.getString("fqsxw").toUpperCase() + rs.getString("fzqlb") + rs.getString("ffylb") + "CPCD", rs.getInt("ffyfs"));
                    }
                    rs.getStatement().close();
                    strSql = "select max(fstartdate),fzqdm,fqylx from csqyxx where fqydjr = " + dbl.sqlDate(base.getDate()) + " and fstartdate<=" + dbl.sqlDate(base.getDate()) +
                        " and fqybl not in('上交所','深交所','银行间') and fqylx in('XJDJ','GFDJ') and fsh=1 group by fzqdm,fqylx";
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        cache.setInt(rs.getString("fzqdm").toUpperCase() + rs.getString("fqylx") + "QY", 1);
                    }
                    rs.getStatement().close();
                }
            }
        } catch (Exception e) {
            throw new YssException("初始化缓存数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void init(String[] set) throws YssException {
        init(set, true);
    }

    public void init() throws YssException {
        init(new String[0], false);
    }

    /**
     * 获取基金代码
     * @param set String  套帐号
     * @return int
     */

    public String getFundCode(String set) {
        return cache.getString(set + "FundCode");
    }

    /**
     * 获取基金名称
     * @param set String  套帐号
     * @return int
     */
    public String getFundName(String set) {
        return cache.getString(set + "FundName");
    }

    /**
     * 获取基金类别
     * @param set String  套帐号
     * @return int
     */
    public int getFundType(String set) {
        return cache.getInt(set + "FundType");
    }

    /**
     * 获取基金类型
     * @param set String  套帐号
     * @return int
     */
    public int getAssetType(String set) {
        return cache.getInt(set + "AssetType");
    }

    /**
     * 获取基金管理人
     * @param set String  套帐号
     * @return String
     */
    public String getFundGlr(String set) {
        return cache.getString(set + "FundGlr");
    }

    /**
     * 获取席位公司代码
     * @param set int       套帐号
     * @param sXwdm String  席位代码
     * @return boolean
     */
    public String getXwDm(String set, String sXwdm) {
        return cache.getString(set + sXwdm.toUpperCase() + "XWDM");
    }

    /**
     * 获取回售价格
     * @param sZqdm String  回售代码
     * @return boolean
     */
    public double getHsJg(String sZqdm) {
        return cache.getDouble("HsJg" + sZqdm.toUpperCase());
    }

    /**
     * 判断是否为指数席位
     * @param set int       套帐号
     * @param sXwdm String  席位代码
     * @return boolean
     */
    public boolean isZsXw(String set, String sXwdm) {
        return cache.getBoolean(set + "ZS" + sXwdm.toUpperCase());
    }

    /**
     * 判断是否为指数股票
     * @param set int       套帐号
     * @param sZqdm String  股票代码
     * @return boolean
     */
    public boolean isZsGp(String set, String sZqdm) {
        return cache.getBoolean(set + "ZS" + sZqdm);
    }

    /**
     * 判断是否为指标股票
     * @param set int       套帐号
     * @param sZqdm String  股票代码
     * @return boolean
     */
    public boolean isZbGp(String set, String sZqdm) {
        return cache.getBoolean(set + "ZB" + sZqdm);
    }

    /**
     * 判断是否为贴现债券
     * @param sZqdm String  债券代码
     * @return boolean
     */
    public boolean isTxZq(String sZqdm) {
        boolean blnRetu;
        blnRetu = cache.getBoolean("TXZQ" + pub.getUserCode() + sZqdm);
        if (!blnRetu) {
            blnRetu = cache.getBoolean("TXZQ" + sZqdm);
        }
        return blnRetu;
    }

    /**
     * 判断是否为配股
     * @param sZqdm String  债券代码
     * @return boolean
     */
    public boolean isPgGp(String sZqdm) {
        boolean blnRetu;
        blnRetu = cache.getBoolean("PGGP" + sZqdm);
        return blnRetu;
    }

    /**
     * 获取已计提的国债利息
     * @param sZqdm String  国债代码
     * @return boolean
     */
    public double getGzlx(String sZqdm) {
        return cache.getDouble("GZLX" + sZqdm);
    }

    /**
     * 获取回购天数
     * @param sZqdm String  回购代码
     * @return int
     */
    public int getHgts(String sZqdm) {
        return cache.getInt("HGTS" + sZqdm);
    }

    /**
     * 获取回购利率
     * @param sZqdm String  回购代码
     * @return double
     */
    public double getHglv(String sZqdm) {
        return cache.getDouble("HGLV" + sZqdm);
    }

    /**
     * 获取回购最小金额
     * @param sZqdm String  回购代码
     * @return double
     */
    public double getHgMin(String sZqdm) {
        return cache.getDouble("HGMin" + sZqdm);
    }

    /**
     * 获得券商佣金利率
     * @param set String      套帐号
     * @param sZqlb String    证券类别
     * @param sGddm String    股东代码
     * @param sXw String      券商席位
     * @return double
     */
    public double getYjlv(String set, String sZqlb, String sGddm, String sXw, String sSzSh) {
        double dTmp = 0;
        dTmp = cache.getDouble(set + sZqlb + sGddm.toUpperCase() + "Lv_" + sSzSh);
        if (dTmp == 0) {
            dTmp = cache.getDouble(set + sZqlb + sXw.toUpperCase() + "Lv_" + sSzSh);
        }
        if (dTmp == 0) {
            dTmp = cache.getDouble(set + sZqlb + "Lv_" + sSzSh);
        }
        if (sZqlb.equalsIgnoreCase("GP") && dTmp == 0) { //按道理来说,不应该给他们默认股票佣金利率的.
            dTmp = 0.001;
        }
        return dTmp;
    }

    /**
     * 获得佣金折扣比例
     * @param set String      套帐号
     * @param sZqlb String    证券类别
     * @param sGddm String    股东代码
     * @param sXw String      券商席位
     * @return double
     */
    public double getYjZk(String set, String sZqlb, String sGddm, String sXw, String sSzSh) {
        double dTmp = 0;
        dTmp = cache.getDouble(set + sZqlb + sGddm + "ZK_" + sSzSh);
        if (dTmp == 0) {
            dTmp = cache.getDouble(set + sZqlb + sXw + "ZK_" + sSzSh);
        }
        if (dTmp == 0) {
            dTmp = cache.getDouble(set + sZqlb + "ZK_" + sSzSh);
        }
        if (dTmp == 0) {
            dTmp = 1;
        }
        return dTmp;
    }

    /**
     * 获得券商最小佣金
     * @param set String      套帐号
     * @param sZqlb String    证券类别
     * @param sGddm String    股东代码
     * @param sXw String      券商席位
     * @return double
     */
    public double getYjMin(String set, String sZqlb, String sGddm, String sXw, String sSzSh) {
        double dTmp = 0;
        dTmp = cache.getDouble(set + sZqlb + sGddm + "Min_" + sSzSh);
        if (dTmp == 0) {
            dTmp = cache.getDouble(set + sZqlb + sXw + "Min_" + sSzSh);
        }
        if (dTmp == 0) {
            dTmp = cache.getDouble(set + sZqlb + "Min_" + sSzSh);
        }
        return dTmp;
    }

    /**
     * 获得权益类别
     * @param sZqdm String    股票代码
     * @param sQylx String    权益类型(现金对价,股份对价)
     * @return int
     */
    public int getQyLx(String sZqdm, String sQylx) {
        int dTmp = 0;
        dTmp = cache.getInt(sZqdm + sQylx + "QY");
        return dTmp;
    }

    /**
     * 获取交易费用费率
     * @param set String
     * @param sZqlb String
     * @param sGddm String
     * @param sXw String
     * @param sSzSh String
     * @param sFylb String
     * @return double
     */
    public double getJyLv(String set, String sZqlb, String sGddm, String sXw, String sSzSh, String sFylb) {
        double dTmp = 0;
        dTmp = cache.getDouble(set + sZqlb + sGddm.toUpperCase() + sFylb + "Lv_" + sSzSh, -1); //-1为默认值,如果dTmp=-1表示没设置当前值,如果dTmp=0则表示设置的个性化值为0,详细说明见 cache.getDouble中的说明
        if (dTmp == -1) {
            dTmp = cache.getDouble(set + sZqlb + sXw.toUpperCase() + sFylb + "Lv_" + sSzSh, -1);
        }
        if (dTmp == -1) {
            dTmp = cache.getDouble(set + sZqlb + "0" + sFylb + "Lv_" + sSzSh, -1);
        }
        if (dTmp == -1) {
            dTmp = cache.getDouble("0" + sZqlb + "0" + sFylb + "Lv_" + sSzSh, sFylb.equalsIgnoreCase("FXJ") ? -1 : 0); //最后返回的是公用费率值,如果没有设置则返回0,如果当前费率为风险金则返回-1
        }
        //如果dTmp=-1,且表示没有设置风险金费率,则给常用默认值
        if ( (sZqlb.equalsIgnoreCase("GP") || sZqlb.equalsIgnoreCase("JJ")) && dTmp == -1 && sFylb.equalsIgnoreCase("FXJ")) {
            dTmp = 0.00003;
        } else if (sZqlb.equalsIgnoreCase("GZXQ") && dTmp == -1 && sFylb.equalsIgnoreCase("FXJ")) {
            dTmp = 0.00001;
        } else if (sZqlb.equalsIgnoreCase("GDGZXQ") && dTmp == -1 && sFylb.equalsIgnoreCase("FXJ")) {
            dTmp = 0.00001;
        } else if (dTmp == -1) {
            dTmp = 0;
        }

        return dTmp;
    }

    /**
     * 获取交易费用费率起点金额
     * @param set String
     * @param sZqlb String
     * @param sGddm String
     * @param sXw String
     * @param sSzSh String
     * @param sFylb String
     * @return double
     */
    public double getJyMin(String set, String sZqlb, String sGddm, String sXw, String sSzSh, String sFylb) {
        double dTmp = 0;
        dTmp = cache.getDouble(set + sZqlb + sGddm.toUpperCase() + sFylb + "Min_" + sSzSh);
        if (dTmp == 0) {
            dTmp = cache.getDouble(set + sZqlb + sXw.toUpperCase() + sFylb + "Min_" + sSzSh);
        }
        if (dTmp == 0) {
            dTmp = cache.getDouble(set + sZqlb + sXw.toUpperCase() + sFylb + "Min_" + sSzSh);
        }
        if (dTmp == 0) {
            dTmp = cache.getDouble(set + sZqlb + "0" + sFylb + "Min_" + sSzSh);
        }
        if (dTmp == 0) {
            dTmp = cache.getDouble("0" + sZqlb + "0" + sFylb + "Min_" + sSzSh);
        }
        return dTmp;
    }

    /**
     * 获取红利派息（通常是每10份派多少）
     * @param sYwlb String   证券类别
     * @return double
     */
    public double getPxJg(String sZqdm) throws YssException {
        double dTmp = cache.getDouble("PX" + sZqdm);
        if (dTmp == 0) {
            dTmp = cache.getDouble("XJDJ" + sZqdm);
        }
        if (dTmp == 0) {
            throw new YssException(sZqdm + "有红利派送，请先在权益信息里维护派送比例！");
        }
        return dTmp;
    }

    /**
     * 获取税前红利派息（通常是每10份派多少）
     * @param sYwlb String   证券类别
     * @return double
     */
    public double getSqPxJg(String sZqdm) throws YssException {
        double dTmp = cache.getDouble("SQPX" + sZqdm);
        if (dTmp == 0) {
            dTmp = cache.getDouble("SQXJDJ" + sZqdm);
        }
        if (dTmp == 0) {
            throw new YssException(sZqdm + "有红利派送，请先在权益信息里维护税前派送比例！");
        }
        return dTmp;
    }

    /**
     * 判断费用承担方式（产品/券商）默认券商承担－－－－费用特别是交易所费用都是产品承担，只有风险金这样的费用才是默认券商承担，请注意cherry
     * @param set String     套帐号
     * @param sXw String     股东代码（席位代码）
     * @param sZqlb String   证券类别
     * @param sFylb String   费用类别
     * @return boolean
     */
    //这个函数的意图应该是判断该项费用是否计入成本或者是否券商承担，若是则将费用置零。原来的描述刚好是反的，请注意cherry
    //跟客户了解了一下需求，需求是：在与券商清算的模式中，产品只对券商提交一笔手续费（佣金），并计入交易成本。故在计算佣金时要扣除的经手费和征管费就
    //不用扣除了，因为这两项费用已经体现在所谓的“佣金利率”里面了。所以要实现这个需求，程序应该判断费用是否计入成本。之前的写法都是错误的，无论是券商承担，
    //还是产品承担，差别只在于科目的走法不同，不应该不计算费用。本来这种模式只是在券商清算模式中出现，但是考虑到一些理财产品或者是年金产品有可能 在交易所
    //清算模式中谈出类似需求，故在数据处理中交易所模式也提供这个处理。cherry

    public boolean isCpCd(String set, String sXw, String sZqlb, String sFylb) {
        //如果为计入成本为TRUE
        //return cache.getInt(set + sXw + sZqlb + sFylb + "CPCD") == 0 || cache.getInt(set + sXw + sZqlb + sFylb + "CPCD") == 2;
        return cache.getInt(set + sXw + sZqlb + sFylb + "CPCD") == 2;
    }

    public int CpCdMs(String set, String sXw, String sZqlb, String sFylb) {
        //如果为产品承担或者计入成本为TRUE
        return cache.getInt(set + sXw + sZqlb + sFylb + "CPCD");
    }

    /**
     * 保存接口明细表数据到接口汇总表
     * @return String
     * @throws YssException
     */
    /*
       protected void insert_toHzJkHz_fromHzJkMx(Statement st, String set) throws YssException {
          ResultSet rs = null;
          try {
             boolean bYjjs, bHgYj, bYjmx, bHYjmx, bYhsmx, bJsfmx, bZgfmx, bGhfmx, bSxfmx, bHFxj, bSFxj, bGzQjYj, bYjJJSF;
             String sZqdm="", oZqdm, sZqbz, sYwbz, sGddm, sXw;
             double dZkBl = 1, YjMin = 0;
             Date dDate;
//==         com.yss.vsub.YwData yw = new com.yss.vsub.YwData(pub);
             //需要注意深圳佣金计算fazmm20060519
             StringBuffer bufName = new StringBuffer();
             StringBuffer bufValue = new StringBuffer();
             bufName.append(set).append("实际收付金额包含佣金").append("\t");
             bufName.append(set).append("交易所回购计算佣金").append("\t");
             bufName.append(set).append("按成交记录计算经手费").append("\t");
             bufName.append(set).append("按成交记录计算征管费").append("\t");
             bufName.append(set).append("按成交记录计算过户费").append("\t");
             bufName.append(set).append("按成交记录计算印花税").append("\t");
             bufName.append(set).append("按成交记录计算结算费").append("\t");
             bufName.append(set).append("S按成交记录计算佣金").append("\t");
             bufName.append(set).append("H按成交记录计算佣金").append("\t");
             bufName.append(set).append("S按成交记录计算风险金").append("\t");
             bufName.append(set).append("H按成交记录计算风险金").append("\t");
             bufName.append(set).append("计算佣金减去结算费").append("\t");
             bufName.append(set).append("交易所非贴现债以全价计提佣金");
             bufValue.append("0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0");
             /*
               String[] strValue = yw.varGetValueMore(bufName.toString(), bufValue.toString()).split("\t");
               bYjjs = strValue[0].equalsIgnoreCase("1");
               bHgYj = strValue[1].equalsIgnoreCase("1");
               //增加了单比算佣金的功能
               bJsfmx = strValue[2].equalsIgnoreCase("1");
               bZgfmx = strValue[3].equalsIgnoreCase("1");
               bGhfmx = strValue[4].equalsIgnoreCase("1");
               bYhsmx = strValue[5].equalsIgnoreCase("1");
               bSxfmx = strValue[6].equalsIgnoreCase("1");
               bYjmx = strValue[7].equalsIgnoreCase("1"); //深圳佣金计算方式
               bHYjmx = strValue[8].equalsIgnoreCase("1");
               bSFxj = strValue[9].equalsIgnoreCase("1"); //深圳佣金计算方式
               bHFxj = strValue[10].equalsIgnoreCase("1");
               bYjJJSF = strValue[11].equalsIgnoreCase("1");
               bGzQjYj = strValue[12].equalsIgnoreCase("1"); //交易所非贴现国债按全价计算佣金b
               int intCount = 0; //由于java的批处理最大支持32767条SQL语句，所以需要进行对SQL语句的数量进行控制。目前约定为10000条提交一次。
               String strSql = "select fdate,findate,fjyfs,zqdm,fzqdm,fszsh,fgddm,fjyxwh,fbs,sum(fcjsl) as fcjsl,sum(fcjje) as fcjje,sum(fyhs) as fyhs,sum("
                     + "fjsf) as fjsf,sum(fghf) as fghf,sum(fzgf) as fzgf,sum(fqtf) as fqtf,sum(fyj) as fyj, sum(ffxj) as ffxj,sum(fgzlx) as fgzlx,sum(fhggain) as fhggain,"
                     + "fzqbz,fywbz,fcjbh from HzJkMx group by fdate,findate,fjyfs,zqdm,fzqdm,fszsh,fgddm,fjyxwh,fbs,fzqbz,fywbz,"
                     + "fcjbh order by fzqdm,fjyxwh";
               rs = dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);
               while (rs.next()) {
                  double dYhs = 0, dJsf = 0, dGhf = 0, dZgf = 0, dSxf = 0, dYj = 0, dFxj = 0, dCjje = 0, dGzlx = 0, dWtsxf = 0; //中行信托厦国投国轩、高特佳券商收取的委托手续费
                  boolean blnIsDzjy;
                  dDate = rs.getDate("FDate");
                  sZqdm = rs.getString("FZqdm");
                  oZqdm = rs.getString("Zqdm");
                  sZqbz = rs.getString("FZqbz");
                  sYwbz = rs.getString("fywbz");
                  sGddm = rs.getString("fgddm");
                  sXw = rs.getString("fjyxwh");
                  dCjje = rs.getDouble("fCjje");
                  dGzlx = rs.getDouble("fGzlx");
                  if (!sYwbz.equalsIgnoreCase("KZZGP")) {
                     if (rs.getString("fszsh").equalsIgnoreCase("H")) {
                        if (sZqbz.equalsIgnoreCase("GP") && !sYwbz.equalsIgnoreCase("KZZGP")) {
                           dYhs = YssFun.roundIt(YssD.mul(dCjje, base.getAgYHS_SH()), 2);
                           dJsf = YssFun.roundIt(YssD.mul(dCjje, base.getAgJSF_SH()), 2);
                           dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getAgZGF_SH()), 2);
                           dGhf = YssFun.roundIt(YssD.mul(rs.getDouble("fCjsl"), getJyLv(set, sZqbz, sGddm, sXw, "H", "GHF")), 2);
                           if (dYhs < base.getAgYHS_SH_Min()) {
                              dYhs = base.getAgYHS_SH_Min();
                           }
                           if (dJsf < base.getAgJSF_SH_Min()) {
                              dJsf = base.getAgJSF_SH_Min();
                           }
                           if (dZgf < base.getAgZGF_SH_Min()) {
                              dZgf = base.getAgZGF_SH_Min();
                           }
                           if (dGhf < getJyMin(set, sZqbz, sGddm, sXw, "H", "GHF")) {
                              dGhf = getJyMin(set, sZqbz, sGddm, sXw, "H", "GHF");
                           }
//==                     if (pub.getAssetType() == 4 && pub.getFundType() == 7) {
                              dGhf = YssFun.roundIt(YssD.mul(rs.getDouble("fCjsl"), 0.001), 2);
                              if (dGhf < 1) {
                                 dGhf = 1;
                              }
//==                     }
//==                     if (pub.getAssetType() == 5 && pub.getFundTgr().indexOf("中国银行") >= 0 && YssFun.oneOf(pub.getFundName(),"高特佳,国轩,日日新4,日日新5")) {
                              dWtsxf = 1;
                              dSxf = YssD.add(dSxf, dWtsxf);
//==                    }
                        }
                        else if (sZqbz.equalsIgnoreCase("JJ")) {
                           dJsf = YssFun.roundIt(YssD.mul(dCjje, base.getJjJSF_SH()), 2);
                           dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getJjZGF_SH()), 2);
                           dYhs = YssFun.roundIt(YssD.mul(dCjje, base.getJjYHS_SH()), 2);
                           dGhf = YssFun.roundIt(YssD.mul(rs.getDouble("fCjsl"), getJyLv(set, sZqbz, sGddm, sXw, "H", "GHF")), 2);
                           if (dYhs < base.getJjYHS_SH_Min()) {
                              dYhs = base.getJjYHS_SH_Min();
                           }
                           if (dJsf < base.getJjJSF_SH_Min()) {
                              dJsf = base.getJjJSF_SH_Min();
                           }
                           if (dZgf < base.getJjZGF_SH_Min()) {
                              dZgf = base.getJjZGF_SH_Min();
                           }
                           if (dGhf < getJyMin(set, sZqbz, sGddm, sXw, "H", "GHF")) {
                              dGhf = getJyMin(set, sZqbz, sGddm, sXw, "H", "GHF");
                           }
                        }
                        else if (sZqbz.equalsIgnoreCase("QZ")) {
                           dJsf = YssFun.roundIt(YssD.mul(dCjje, base.getQzJSF_SH()), 2);
                           dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getQzZGF_SH()), 2);
                           dSxf = YssFun.roundIt(YssD.mul(dCjje, base.getQzSXF_SH()), 2);
                        }
                        else if (sZqbz.equalsIgnoreCase("ZQ") & !sYwbz.equalsIgnoreCase("KZZGP")) {
                           if (sYwbz.equalsIgnoreCase("GZXQ")) {
                              dJsf = YssFun.roundIt(YssD.mul(YssD.add(dCjje, dGzlx), base.getGzxqJSF_SH()), 2);
                              dZgf = YssFun.roundIt(YssD.mul(YssD.add(dCjje, dGzlx), base.getGzxqZGF_SH()), 2);
                              if (dJsf < base.getGzxqJSF_SH_Min()) {
                                 dJsf = base.getGzxqJSF_SH_Min();
                              }
                              if (dZgf < base.getGzxqZGF_SH_Min()) {
                                 dZgf = base.getGzxqZGF_SH_Min();
                              }
                           }
                           else {
                              if (sYwbz.equalsIgnoreCase("QYZQ")) {
                                 dJsf = YssFun.roundIt(YssD.mul(dCjje, base.getQyzqJSF_SH()), 2);
                                 dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getQyzqZGF_SH()), 2);
                                 if (dJsf < base.getQyzqJSF_SH_Min()) {
                                    dJsf = base.getQyzqJSF_SH_Min();
                                 }
                                 if (dZgf < base.getQyzqZGF_SH_Min()) {
                                    dZgf = base.getQyzqZGF_SH_Min();
                                 }
                              }
                              else if (sYwbz.equalsIgnoreCase("KZZ")) {
                                 dJsf = YssFun.roundIt(YssD.mul(dCjje, base.getKzzJSF_SH()), 2);
                                 dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getKzzZGF_SH()), 2);
                                 if (dJsf < base.getKzzJSF_SH_Min()) {
                                    dJsf = base.getKzzJSF_SH_Min();
                                 }
                                 if (dZgf < base.getKzzZGF_SH_Min()) {
                                    dZgf = base.getKzzZGF_SH_Min();
                                 }
                              }
                              else if (sYwbz.equalsIgnoreCase("ZCZQ")) {
                                 dJsf = YssFun.roundIt(YssD.mul(dCjje, base.getZcZqJSF_SH()), 2);
                                 //dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje"), base.getZcZqZGF_SH()), 2);
                                 dSxf = YssFun.roundIt(YssD.mul(dCjje, base.getZcZqSXF_SH()), 2);
                              }
                           }
                        }
                        else if (sZqbz.equalsIgnoreCase("HG")) {
                           dJsf = YssFun.roundIt(YssD.mul(dCjje, getHglv(sZqdm)), 2);
                           if (dJsf < getHgMin(sZqdm)) {
                              dJsf = getHgMin(sZqdm);
                           }
                        }
                     }
                     else {
                        blnIsDzjy = rs.getString("fjyfs").equalsIgnoreCase("DZ");
                        if (sZqbz.equalsIgnoreCase("GP")) {
                           dYhs = YssFun.roundIt(YssD.mul(dCjje, base.getAgYHS_SZ()), 2); //成交数量×成交价格×印花税利率
       dJsf = YssFun.roundIt(YssD.mul(dCjje, YssD.add( (blnIsDzjy ? YssD.mul(base.getAgJSF_SZ(), 0.8) : base.getAgJSF_SZ()), base.getAgZGF_SZ())), 2); //成交数量×成交价格×经手费利率
                           dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getAgZGF_SZ()), 2);
                           if (dYhs < base.getAgYHS_SZ_Min()) {
                              dYhs = base.getAgYHS_SZ_Min();
                           }
                           if (dJsf < base.getAgJSF_SZ_Min()) {
                              dJsf = base.getAgJSF_SZ_Min();
                           }
//==                     if (pub.getAssetType() == 5 && pub.getFundTgr().indexOf("中国银行") >= 0 && YssFun.oneOf(pub.getFundName(),"高特佳,国轩,日日新4,日日新5")) {
                              dWtsxf = 1;
                              dSxf = YssD.add(dSxf, dWtsxf);
//==                     }
                        }
                        else if (sZqbz.equalsIgnoreCase("JJ")) {
                           dYhs = YssFun.roundIt(YssD.mul(dCjje, base.getJjYHS_SZ()), 2); //成交数量×成交价格×印花税利率
       dJsf = YssFun.roundIt(YssD.mul(dCjje, YssD.add( (blnIsDzjy ? YssD.mul(base.getJjJSF_SZ(), 0.8) : base.getJjJSF_SZ()), base.getJjZGF_SZ())), 2); //成交数量×成交价格×经手费利率
                           dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getJjZGF_SZ()), 2);
                           if (dYhs < base.getJjYHS_SZ_Min()) {
                              dYhs = base.getJjYHS_SZ_Min();
                           }
                           if (dJsf < base.getJjJSF_SZ_Min()) {
                              dJsf = base.getJjJSF_SZ_Min();
                           }
                        }
                        else if (sZqbz.equalsIgnoreCase("QZ")) {
                           dJsf = YssFun.roundIt(YssD.mul(dCjje, (blnIsDzjy ? YssD.mul(base.getQzJSF_SZ(), 0.8) : base.getQzJSF_SZ())), 2); //成交数量×成交价格×经手费利率
                           dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getQzZGF_SZ()), 2);
                           dSxf = YssFun.roundIt(YssD.mul(dCjje, base.getQzSXF_SZ()), 2);
                        }
                        else if (sZqbz.equalsIgnoreCase("ZQ")) {
                           if (sYwbz.equalsIgnoreCase("GZXQ")) { //深圳国债经手费不是根据费率计算出来的
                              dJsf = dCjje < 1000000 ? 0.1 : 10;
                           }
                           else if (sYwbz.equalsIgnoreCase("QYZQ")) {
                              dJsf = YssFun.roundIt(YssD.mul(dCjje, YssD.add( (blnIsDzjy ? YssD.mul(base.getQyzqJSF_SZ(), 0.8) : base.getQyzqJSF_SZ()), base.getQyzqZGF_SZ())), 2);
                              dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getQyzqZGF_SZ()), 2);
                           }
                           else if (sYwbz.equalsIgnoreCase("KZZ")) {
                              dJsf = YssFun.roundIt(YssD.mul(dCjje, YssD.add( (blnIsDzjy ? YssD.mul(base.getKzzJSF_SZ(), 0.8) : base.getKzzJSF_SZ()), base.getKzzZGF_SZ())), 2);
                              dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getKzzZGF_SZ()), 2);
                           }
                           else if (sYwbz.equalsIgnoreCase("ZCZQ")) { //资证券化
                              dJsf = YssFun.roundIt(YssD.mul(dCjje, YssD.add( (blnIsDzjy ? YssD.mul(base.getZcZqJSF_SZ(), 0.8) : base.getZcZqJSF_SZ()), base.getZcZqZGF_SZ())), 2);
                              dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getZcZqZGF_SZ()), 2);
                              dSxf = YssFun.roundIt(YssD.mul(dCjje, base.getZcZqSXF_SZ()), 2);
                           }
                        }
                        else if (sZqbz.equalsIgnoreCase("HG")) {
                           if (YssFun.oneCase(sYwbz, "MRHG,MCHG")) {
                              if (rs.getInt("FCjje") <= 1000000) {
                                 dJsf = 0.1;
                              }
                              else {
                                 dJsf = 1;
                              }
                           }
                       }
                     }
                     if (sZqbz.equalsIgnoreCase("GP") || sZqbz.equalsIgnoreCase("JJ")) {
                        dFxj = YssFun.roundIt(YssD.mul(rs.getDouble("fcjje"), getJyLv(set, sZqbz, rs.getString("fgddm"), sXw, rs.getString("fszsh"), "FXJ")), 2);
                     }
                     else if (sYwbz.equalsIgnoreCase("GZXQ")) {
                        dFxj = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("FCjje"), dGzlx), getJyLv(set, sYwbz, rs.getString("fgddm"), sXw, rs.getString("fszsh"), "FXJ")), 2);
                     }
                     else if (sYwbz.equalsIgnoreCase("MRHG") || sYwbz.equalsIgnoreCase("MCHG")) {
                        dFxj = YssFun.roundIt(YssD.mul(rs.getDouble("Fcjje"), getJyLv(set, "HG" + sZqdm, rs.getString("fgddm"), sXw, rs.getString("fszsh"), "FXJ")), 2);
                     }
                     if (sZqbz.equalsIgnoreCase("ZQ")) {
                        if (isCpCd(set, sGddm, sYwbz.equalsIgnoreCase("KZZHS") ? "KZZ" : sYwbz, "JSF")) dJsf = 0;
                        if (isCpCd(set, sGddm, sYwbz.equalsIgnoreCase("KZZHS") ? "KZZ" : sYwbz, "ZGF")) dZgf = 0;
                        //if (isCpCd(set, sGddm, sYwbz.equalsIgnoreCase("KZZHS") ? "KZZ" : sYwbz, "FXJ"))  dFxj = 0;
                     }
                     else {
                        if (isCpCd(set, sGddm, sZqbz, "JSF")) dJsf = 0;
                        if (isCpCd(set, sGddm, sZqbz, "ZGF")) dZgf = 0;
                        if (isCpCd(set, sGddm, sZqbz, "SXF") ) dSxf = 0;
                        //if (isCpCd(set, sGddm, sZqbz, "FXJ"))  dFxj = 0;
                        if (isCpCd(set, sGddm, sZqbz, "GHF")) dGhf = 0;
                     }
                     if (bYhsmx) {
                        dYhs = rs.getDouble("fyhs");
                     }
                     if (bJsfmx) {
                        dJsf = rs.getDouble("fjsf");
                     }
                     if (bZgfmx) {
                        dZgf = rs.getDouble("fzgf");
                     }
                     if (bGhfmx) {
                        if (! (pub.getAssetType() == 4 && pub.getFundType() == 7)) {
                           dGhf = rs.getDouble("fghf");
                        }
                     }

                     if (bSxfmx && !(sZqbz.equalsIgnoreCase("GP") && dWtsxf!=0)) { //权证结算费
                        dSxf = rs.getDouble("fqtf");
                     }
                     if ( (bSFxj && rs.getString("fszsh").equalsIgnoreCase("S")) || (bHFxj && rs.getString("fszsh").equalsIgnoreCase("H"))) {
                        dFxj = rs.getDouble("ffxj");
                     }
                     if ( (bYjmx && rs.getString("fszsh").equalsIgnoreCase("S")) || (bHYjmx && rs.getString("fszsh").equalsIgnoreCase("H"))) {
                        dYj = rs.getDouble("FYj");
                     }
                     else { // 佣金处理
                        if (sZqbz.equalsIgnoreCase("ZQ") || sZqbz.equalsIgnoreCase("JJ")) {
                           dYj = getYjlv(set, sYwbz, sGddm, sXw, rs.getString("fszsh"));
                           dZkBl = getYjZk(set, sYwbz, sGddm, sXw, rs.getString("fszsh"));
                        }
                        else if (sZqbz.equalsIgnoreCase("HG")) {
                           dYj = getYjlv(set, sZqdm, sGddm, sXw, rs.getString("fszsh"));
                           dZkBl = getYjZk(set, sZqdm, sGddm, sXw, rs.getString("fszsh"));
                           if (dYj == 0) {
                              dYj = getYjlv(set, sZqbz, sGddm, sXw, rs.getString("fszsh"));
                              dZkBl = getYjZk(set, sZqbz, sGddm, sXw, rs.getString("fszsh"));
                           }
                        }
                        else {
                           dYj = getYjlv(set, sZqbz, sGddm, sXw, rs.getString("fszsh"));
                           dZkBl = getYjZk(set, sZqbz, sGddm, sXw, rs.getString("fszsh"));
                        }
                        if (dYj > 0) {
                           if (sZqbz.equalsIgnoreCase("ZQ") || sZqbz.equalsIgnoreCase("JJ")) {
                              if (sYwbz.equalsIgnoreCase("GZXQ") && !isTxZq(sZqdm) && bGzQjYj) { //国债是净价交易,如果是全价计算佣金，需要加债券利息
                                 dYj = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("fcjje"), dGzlx), dYj), 2);
                              }
                              else {
                                 dYj = YssFun.roundIt(YssD.mul(rs.getDouble("fcjje"), dYj), 2);
                              }
                              if (dYj < getYjMin(set, sYwbz, sGddm, sXw, rs.getString("fszsh"))) {
                                 dYj = getYjMin(set, sYwbz, sGddm, sXw, rs.getString("fszsh"));
                              }
                              dYj = YssFun.roundIt(YssD.sub(dYj, dJsf,rs.getString("fszsh").equalsIgnoreCase("H")?dZgf:0), 2);
                           }
                           else {
                              dYj = YssFun.roundIt(YssD.mul(rs.getDouble("fcjje"), dYj), 2);
                              if (sZqbz.equalsIgnoreCase("HG")) {
                                 YjMin = getYjMin(set, sZqdm, sGddm, sXw, rs.getString("fszsh"));
                                 if (YjMin == 0) {
                                    YjMin = getYjMin(set, sZqbz, sGddm, sXw, rs.getString("fszsh"));
                                 }
                                 if (dYj < YjMin) {
                                    dYj = YjMin;
                                 }
                              }
                              else {
                                 if (dYj < getYjMin(set, sZqbz, sGddm, sXw, rs.getString("fszsh"))) {
                                    dYj = getYjMin(set, sZqbz, sGddm, sXw, rs.getString("fszsh"));
                                 }
                              }
                              dYj = YssFun.roundIt(YssD.sub(dYj, dJsf, (bYjJJSF ? dSxf : 0),(rs.getString("fszsh").equalsIgnoreCase("H")?dZgf:0)), 2);
                           }
                           if (sZqbz.equalsIgnoreCase("HG") && !bHgYj) {
                              if (bJsfmx && !bYjmx) {
                                 dJsf = rs.getDouble("fjsf");
                              }
                              else {
                                 dJsf = dYj;
                              }
                              dYj = 0;
                           }
                           if (dZkBl != 1) {
                              dYj = YssFun.roundIt(YssD.mul(YssD.sub(dYj, dFxj), dZkBl), 2);
                           }
                        }
                     }
                  }
                  strSql = "insert into HzJkHz " + base.strTableHz + " values(" + dbl.sqlDate(dDate) + "," + dbl.sqlDate(base.getDate()) + ",'" +
                        rs.getString("FJyfs") + "','" + oZqdm + "','" + sZqdm + "','"
                        + rs.getString("fszsh") + "','" + sGddm + "','" + sXw + "','" + rs.getString("FBS") + "'," + rs.getDouble("FCjsl")
                        + "," + dCjje + "," + dYhs + "," + dJsf + "," + dGhf + "," + dZgf + "," + dYj + "," + dFxj + "," + dSxf + "," + dGzlx + ","
                        + rs.getDouble("fhggain") + ",'" + sZqbz + "','" + sYwbz + "','" + rs.getString("FCjBh") + "')";
                  //System.out.println(strSql);
                  //st.execute(strSql);
                  st.addBatch(strSql);
                  intCount++;
                  if (intCount > 10000) {
                     st.executeBatch();
                     intCount = 0;
                  }
               }
               st.executeBatch();
            }
            catch (BatchUpdateException ex) {
               throw new YssException("保存接口明细表数据到接口汇总表时出错！", ex);
            }
            catch (Exception ex) {
               throw new YssException("保存接口明细表数据到接口汇总表时出错！", ex);
            }
            finally {
               dbl.closeResultSetFinal(rs);
            }
         }
      */
}
