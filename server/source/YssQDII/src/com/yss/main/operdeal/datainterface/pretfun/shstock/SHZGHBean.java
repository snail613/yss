package com.yss.main.operdeal.datainterface.pretfun.shstock;

import java.sql.*;

import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.util.*;

public class SHZGHBean
    extends DataBase {
    public SHZGHBean(DataMake obj) {
        this.setDataMake(obj);
    }

    /**
     * 实际处理上海固定收益交易过户库
     * @param set int[]  套帐组
     * @throws YssException
     */
    public void makeData(String[] set) throws YssException {
        Connection con = dbl.loadConnection();
        Statement st = null;
        ResultSet rs = null;
        boolean bTrans = false, bHgYj = false;
        String sJyFs = "PT";
        String FSzSh = "G"; //上海固定收益平台
        try {
            String sXw = null, strOZqdm = null, strZqdm = null, strZqbz = null, strYwbz = null, strBs = null;
            String tempywbz = null;
            java.util.Date dDate;
            st = con.createStatement();
//==         YwData ywData = new YwData(pub);
//==         YssGzFun yg = new YssGzFun(pub);
            String strSql = null;
            con.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < set.length; i++) {
                StringBuffer bufName = new StringBuffer(), bufValue = new StringBuffer();
                bufName.append(set[i]).append("交易所回购计算佣金").append("\t");
                bufName.append(set[i]).append("交易所非贴现债以全价计提佣金").append("\t");
                bufName.append(set[i]).append("计算佣金减去结算费").append("\t");
                bufName.append(set[i]).append("不计提债券利息").append("\t");
                bufName.append(set[i]).append("佣金包含经手费，证管费");
                bufValue.append("0\t0\t0\t0\t0");

//==            String[] strValue = ywData.varGetValueMore(bufName.toString(), bufValue.toString()).split("\t");
//==            bHgYj = strValue[0].equalsIgnoreCase("1");
//==            boolean bGzQjYj = strValue[1].equalsIgnoreCase("1");
//==            boolean bSubSxf = strValue[2].equalsIgnoreCase("/");
//==            boolean bJtlx = strValue[3].equalsIgnoreCase("0");
//==            boolean bYjBhJsf = strValue[4].equalsIgnoreCase("1");

                int intCount = 3; //由于java的批处理最大支持32767条SQL语句，所以需要进行对SQL语句的数量进行控制。目前约定为10000条提交一次。
                st.addBatch("delete from HzJkHz where fszsh='G' and fjyfs ='PT' and findate=" + dbl.sqlDate(base.getDate()));
                st.addBatch("delete from HzJkMx");

                strSql = "select a.* from shzgh a join csgddm b on " + dbl.sqlTrim("upper(a.gddm)") + "=" + dbl.sqlTrim("upper(b.fgddm)") +
                    " where a.fdate=" + dbl.sqlDate(base.getDate()) + " and " + dbl.sqlLeft("zqdm", 1) + " in ('0','1') and b.fgddm in(select fgddm from " +
                    " csgddm where fsh=1 and Fstartdate in(select max(fstartdate) from csgddm where fstartdate<=" +
                    dbl.sqlDate(base.getDate()) + " group by fgddm)) order by a.cjbh,a.zqdm"; //先处理同一笔业务,再处理同一只证券
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    double dGhf = 0, dZgf = 0, dJsf = 0, dYhs = 0, dSxf = 0, dYj = 0, dYjMin = 0, dGzlx = 0, dHggain = 0, dCjsl = 0;
                    double dFxj = 0, dZkBl = 1;
                    strZqdm = rs.getString("zqdm");
                    strOZqdm = rs.getString("zqdm");
                    dDate = base.getDate();
                    strBs = rs.getString("bs").toUpperCase();
                    sXw = (rs.getString("gsdm").trim().length() < 5 ? "00" : "") + rs.getString("gsdm").trim().toUpperCase();
                    if (strZqdm.startsWith("0")) { // 国债现券
                        if (strZqdm.startsWith("019")) {
                            strZqbz = "ZQ";
                            strYwbz = "GZXQ";
                            dCjsl = rs.getDouble("cjsl") * 10;
                        }
                    } else if (strZqdm.startsWith("1")) {
                        strZqbz = "ZQ";
                        dCjsl = rs.getDouble("cjsl") * 10;
                        if (strZqdm.startsWith("0", 1) || strZqdm.startsWith("1", 1)) { // 可转债
                            strYwbz = "KZZ";
                        } else if (strZqdm.startsWith("121")) {
                            strYwbz = "ZCZQ";
                        } else if (strZqdm.startsWith("122")) {
                            strYwbz = "GSZQ";
                            dCjsl = rs.getDouble("cjsl") * 10;
                        } else { // 企业债券
                            strYwbz = "QYZQ";
                        }
                        if (rs.getDouble("cjjg") == 0) {
                            strYwbz = "KZZGP";
                        }
                    } else if (strZqdm.startsWith("762") || strZqdm.startsWith("764")) { //配债
                        strZqdm = "113" + YssFun.right(strZqdm, 3);
                        strZqbz = "XZ";
                        strYwbz = "KZZXZ";
                        dCjsl = rs.getDouble("cjsl") * 10;
                        strBs = "B";
                    }
                    if (strZqbz.equalsIgnoreCase("ZQ")) {
                        //先提取交易所国债利息，如果为0则使用非交易所国债利息

//==                  if (bJtlx) {
                        //交易库里面提供的利息是保留4位小数的，这里我们自己计算，保留8位。
                        dGzlx = rs.getDouble("yslx");
//==                     dGzlx = yg.get_FJysGzlx(strZqdm, strYwbz, "H", base.getDate(), set[i], calc.getFundType(set[i]) == 4).doubleJe;
//==                  }


                        dGzlx = YssFun.roundIt(YssD.mul(rs.getInt("cjsl") * 10, dGzlx), 2);
                        if (strYwbz.equalsIgnoreCase("GZXQ") && !calc.isTxZq(strZqdm)) {
                            //上海交易所国债经手费、证管费需加上国债利息计算
                            dJsf = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Cjje") * 10000, dGzlx), base.getGdGzxqJSF_SH()), 2);
                            dZgf = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Cjje") * 10000, dGzlx), base.getGdGzxqZGF_SH()), 2);
                        } else {
                            if (strYwbz.equalsIgnoreCase("QYZQ")) {
                                dJsf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje") * 10000, base.getQyzqJSF_SH()), 2);
                                dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje") * 10000, base.getQyzqZGF_SH()), 2);
                            }
                            if (strYwbz.equalsIgnoreCase("GSZQ")) {
                                dJsf = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Cjje") * 10000, dGzlx), base.getGdQyzqJSF_SH()), 2);
                                dZgf = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Cjje") * 10000, dGzlx), base.getGdQyzqZGF_SH()), 2);
                            } else if (strYwbz.equalsIgnoreCase("KZZ")) {
                                dJsf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje") * 10000, base.getKzzJSF_SH()), 2);
                                dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje") * 10000, base.getKzzZGF_SH()), 2);
                            } else if (strYwbz.equalsIgnoreCase("GZXQ")) { //即贴现债券
                                dJsf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje") * 10000, base.getGzxqJSF_SH()), 2);
                                dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje") * 10000, base.getGzxqZGF_SH()), 2);
                            } else if (strYwbz.equalsIgnoreCase("ZCZQ")) {
                                dJsf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje") * 10000, base.getZcZqJSF_SH()), 2);
                                //dZgf = YssFun.roundIt(YssD.mul(rs.getDouble("cjje"), base.getZcZqZGF_SH()), 2);
                                dSxf = YssFun.roundIt(YssD.mul(rs.getDouble("Cjje") * 10000, base.getZcZqSXF_SH()), 2);
                            }
                        }
                    }
                    if (strYwbz.equalsIgnoreCase("GZXQ")) {
                        dFxj = YssFun.roundIt(YssD.mul(YssD.add(rs.getDouble("Cjje") * 10000, dGzlx), calc.getJyLv(set[i], "GD" + strYwbz, rs.getString("gddm"), sXw, "H", "FXJ")), 2);
                    }
                    if (strZqbz.equalsIgnoreCase("ZQ")) {
                        if (strYwbz.equalsIgnoreCase("GSZQ")) {
                            tempywbz = "QYZQ";
                        }
                        dYj = calc.getYjlv(set[i], "GD" + (strYwbz.equalsIgnoreCase("GSZQ") ? tempywbz : strYwbz), rs.getString("gddm"), sXw, "H");
                        dYjMin = calc.getYjMin(set[i], "GD" + (strYwbz.equalsIgnoreCase("GSZQ") ? tempywbz : strYwbz), rs.getString("gddm"), sXw, "H");
                        dZkBl = calc.getYjZk(set[i], "GD" + (strYwbz.equalsIgnoreCase("GSZQ") ? tempywbz : strYwbz), rs.getString("gddm"), sXw, "H");
                    }
                    if (dYj > 0) {
//==                  if (strYwbz.equalsIgnoreCase("GZXQ") && !calc.isTxZq(strZqdm) && bGzQjYj) {
                        dYj = YssFun.roundIt(YssD.sub(YssD.mul(YssD.add(rs.getDouble("cjje") * 10000, dGzlx), dYj), dJsf, dZgf), 2);
//==                  }
//==                  else {
//==                     dYj = YssFun.roundIt(YssD.sub(YssD.mul(rs.getDouble("cjje")*10000, dYj), dJsf, dZgf, (bSubSxf ? dSxf : 0)), 2);
//==                  }
                        if (dZkBl != 1) {
                            dYj = YssFun.roundIt(YssD.mul(YssD.sub(dYj, dFxj), dZkBl), 2);
                        }
//==                  if (bYjBhJsf) { //佣金包含经手费、征管费
                        dYj = YssD.add(dYj, dJsf, dZgf);
//==                  }
                        if (dYj < dYjMin) {
                            dYj = dYjMin;
                            //回购的佣金没有折扣
                        }
                    }
                    strSql = "insert into HzJkMx(fdate,findate,FJyFs,zqdm,fzqdm,fszsh,fgddm,fjyxwh,fbs,fcjsl,fcjjg,fcjje,"
                        + "fyhs,fjsf,fghf,fzgf,fyj,ffxj,fqtf,fgzlx,fhggain,fzqbz,fywbz,fcjbh) values(" + dbl.sqlDate(dDate) + "," + dbl.sqlDate(base.getDate()) + ",'"
                        + sJyFs + "','" + strOZqdm + "','" + strZqdm + "','G','" + rs.getString("gddm").toUpperCase() + "','" + sXw + "','" + strBs + "'," + dCjsl + ","
                        + rs.getDouble("cjjg") + "," + rs.getDouble("cjje") * 10000 + "," + dYhs + "," + dJsf + "," + dGhf + "," + dZgf + ","
                        + dYj + "," + dFxj + "," + dSxf + "," + dGzlx + "," + dHggain + ",'" + strZqbz + "','" + strYwbz + "','" + rs.getString("cjbh") + "')";

                    st.addBatch(strSql);
                    intCount++;
                    if (intCount > 10000) {
                        st.executeBatch();
                        intCount = 0;
                    }
                }
                rs.getStatement().close();

                if ( (calc.getAssetType(set[i]) == 1 || calc.getAssetType(set[i]) == 2 || calc.getAssetType(set[i]) == 5) &&
                    (calc.getFundType(set[i]) == 0 || calc.getFundType(set[i]) == 4) ||
                    (calc.getAssetType(set[i]) == 4 && calc.getFundType(set[i]) == 7)) {
                    st.executeBatch();
//==               calc.insert_toHzJkHz_fromHzJkMx(st, set[i]); //邵宏伟20060220
                } else {
                    strSql = "insert into HzJkHz " + base.strTableHz + " select fdate,findate,fjyfs,zqdm,fzqdm,fszsh,fgddm,fjyxwh,fbs,sum(fcjsl),"
                        + "sum(fcjje),sum(fyhs),sum(fjsf),sum(fghf),sum(fzgf),sum(fyj), sum(ffxj) ,sum(fqtf),sum(fgzlx),sum(fhggain),fzqbz,fywbz,fcjbh from "
                        + "HzJkMx group by fdate,findate,fjyfs,zqdm,fzqdm,fszsh,fgddm,fjyxwh,fbs,fzqbz,fywbz,fcjbh order by fzqdm,fjyxwh";
                    st.addBatch(strSql);
                    st.executeBatch();
                }
            }
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理上海固定收益库数据出错！", ex);
        } catch (YssException e) {
            throw e;
        } catch (Exception ex) {
            throw new YssException("处理上海固定收益库数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(con, bTrans);
        }
    }
}
