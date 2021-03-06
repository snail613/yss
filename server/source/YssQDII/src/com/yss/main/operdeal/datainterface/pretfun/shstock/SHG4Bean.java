package com.yss.main.operdeal.datainterface.pretfun.shstock;

import java.sql.*;

import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.util.*;

public class SHG4Bean
    extends DataBase {
    public SHG4Bean(DataMake obj) {
        this.setDataMake(obj);
    }

    /**
     * 上海G4库的处理
     * @param set int[]    套帐组
     * @throws YssException
     */
    public void makeData(String[] set) throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        ResultSet rs = null;
        Statement st = null;
        String sJyFs = "PT";
        try {
            String sXw = null, strOZqdm = null, strZqdm = null, strZqbz = null, strYwbz = null, strBs = null;
            java.util.Date dDate;
            st = con.createStatement();
            String strSql = null;
            con.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < set.length; i++) {
                //HL:红利（ghlx='J' 派息;ghlx='K'派息款到帐）S:送股(注：P配股:qylb='P'，在过户库中处理;DF兑付、DX兑息　无处理)
                st.addBatch("delete from HzJkMx");
                strSql = "select a.* from shg4 a join csgddm b on " + dbl.sqlTrim("upper(gdzh)") + "="
                    + dbl.sqlTrim("upper(b.fgddm)") + " where a.fdate=" + dbl.sqlDate(base.getDate()) + " and (a.qylb in ('HL','S','DX') or (a.zqlb ='GZ' and " +
                    dbl.sqlTrimNull("a.qylb") + " and a.ghlx ='C') or (a.zqlb = 'PZ' and a.ltlx = 'N' and " + dbl.sqlTrimNull("a.qylb") + " and a.GHLX='J')) and b.fgddm in(select fgddm from " +
                    "csgddm where fsh=1 and Fstartdate in(select max(fstartdate) from " +
                    "csgddm where fstartdate<=" + dbl.sqlDate(base.getDate()) + " and fsh =1 group by fgddm))";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    double dCjje = 0, dCjsl = 0, dJsf = 0, dZgf = 0;
                    boolean bFhState = true;
                    sXw = rs.getString("jyxw").trim().length() < 5 ? "00" + rs.getString("jyxw").trim().toUpperCase() : rs.getString("jyxw").trim().toUpperCase();
                    dDate = base.getDate();
                    strOZqdm = rs.getString("zqdm");
                    strZqdm = rs.getString("zqdm").startsWith("609") ? rs.getString("zqdm").replaceFirst("609", "002") : rs.getString("zqdm");
                    //深圳新股派息数据通过对应深圳股票派息获取
                    if (rs.getString("Qylb").equalsIgnoreCase("HL")) {
                        if (rs.getString("Ghlx").equalsIgnoreCase("J")) { //派息
                            strZqbz = "QY";
                            strYwbz = "PX";
                            //判断如果在现金对价的权益数据里面维护了，那么这条信息标识为"XJDJ"，否则是普通的股票派息凭证。
                            if (calc.getQyLx(strZqdm, "XJDJ") == 1) {
                                strYwbz = "XJDJ";
                            }
                            //此时G4库中cjjg=0,到用户维护的权益信息中获取
                            dCjje = YssFun.roundIt(YssD.mul(rs.getInt("ghsl"), calc.getPxJg(strZqdm)), 2);
                            if (dCjje == 0) {
                                bFhState = false;
                            }
                            strBs = "S"; //视同卖出，收到钱
                        } else if (rs.getString("Ghlx").equalsIgnoreCase("K")) { //派息款到帐
                            strZqbz = "QY";
                            strYwbz = "PXDZ";
                            //判断如果在现金对价的权益数据里面维护了，那么这条信息标识为"XJDJ"，否则是普通的股票派息凭证。
                            if (calc.getQyLx(strZqdm, "XJDJ") == 1) {
                                strYwbz = "XJDJDZ";
                            }
                            dCjje = Math.abs(YssFun.roundIt(YssD.mul(rs.getInt("Ghsl"), rs.getDouble("Cjjg")), 2));
                            strBs = "S"; //视同卖出，收到钱
                        }
                    } else if (rs.getString("Qylb").equalsIgnoreCase("S")) { //送股
                        strZqbz = "QY";
                        strYwbz = "SG";
                        //判断如果在现金对价的权益数据里面维护了，那么这条信息标识为"XJDJ"，否则是普通的股票派息凭证。
                        if (calc.getQyLx(strZqdm, "GFDJ") == 1) {
                            strYwbz = "GFDJ";
                        }
                        if (calc.getAssetType(set[i]) == 0 && calc.getFundType(set[i]) == 1) {
                            if (calc.isZsXw(set[i], sXw) || calc.isZsGp(set[i], strZqdm)) {
                                strYwbz = "ZSSG";
                            }
                            if (calc.getQyLx(strZqdm, "GFDJ") == 1) {
                                strYwbz = "ZSGFDJ";
                            }
                        }
                        dCjsl = Math.abs(rs.getInt("ghsl")); //如果通过Qylb为S，获取送股数据的话，那么需要进行绝对值的处理。原因这条记录的数量为负的。fazmm20060228
                        strBs = "B";
                    } else if (rs.getString("zqlb").equals("PZ") && rs.getString("Ghlx").equals("J")) {
                        strZqbz = "QY";
                        strYwbz = "QZ";
                        dCjsl = rs.getInt("ghsl");
                        strBs = "B";
                    } else { //可转债回售
                        strZqbz = "ZQ";
                        strYwbz = "KZZHS";
                        dCjsl = -rs.getInt("ghsl") / 100;
                        dCjje = dCjsl * calc.getHsJg(strZqdm);
                        dJsf = YssFun.roundIt(YssD.mul(dCjje, base.getKzzJSF_SH()), 2);
                        dZgf = YssFun.roundIt(YssD.mul(dCjje, base.getKzzZGF_SH()), 2);
                        strBs = "S";
                    }
                    if (bFhState) {
                        strSql = "insert into HzJkMx " + base.strTableMx + " values(" + dbl.sqlDate(dDate) + "," + dbl.sqlDate(base.getDate()) + ",'"
                            + sJyFs + "','" + strOZqdm + "','" + strZqdm + "','H','" + rs.getString("gdzh").toUpperCase() + "','" + sXw + "','" + strBs + "'," + dCjsl
                            + ",0," + dCjje + ",0," + dJsf + ",0," + dZgf + ",0,0,0,0,0,'" + strZqbz + "','" + strYwbz + "',' ')";
                        st.addBatch(strSql);
                    }
                }
                rs.getStatement().close();

                if ( (calc.getAssetType(set[i]) == 1 || calc.getAssetType(set[i]) == 2) && calc.getFundType(set[i]) == 0) {
                    st.executeBatch();
//==               calc.insert_toHzJkHz_fromHzJkMx(st,set[i]);   //邵宏伟20060220
                } else {
                    strSql = "insert into HzJkHz" + base.strTableHz + " select fdate,findate,fjyfs,zqdm,fzqdm,fszsh,fgddm,fjyxwh,fbs,sum(fcjsl),"
                        + "sum(fcjje),sum(fyhs),sum(fjsf),sum(fghf),sum(fzgf),sum(fyj) , sum(ffxj),sum(fqtf),sum(fgzlx),sum(fhggain),fzqbz,fywbz,' ' from "
                        + "HzJkMx group by fdate,findate,fjyfs,zqdm,fzqdm,fszsh,fgddm,fjyxwh,fbs,fzqbz,fywbz,fcjbh order by fzqdm,fjyxwh";
                    st.addBatch(strSql);
                    st.executeBatch();
                }
            }
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理上海G4库出错！", ex);
        } catch (YssException e) {
            throw e;
        } catch (Exception ex) {
            throw new YssException("处理上海G4库出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(con, bTrans);
        }
    }
}
