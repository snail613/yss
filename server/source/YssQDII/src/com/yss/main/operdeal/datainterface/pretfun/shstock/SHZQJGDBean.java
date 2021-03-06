package com.yss.main.operdeal.datainterface.pretfun.shstock;

import java.sql.*;

import com.yss.base.*;
import com.yss.util.*;

public class SHZQJGDBean
    extends BaseAPOperValue {
    //private com.yss.vsub.DSubGz ds = null;
    //private com.yss.vsub.YwData yw = null;

    //  public SHZQJGDBean(DataMake obj) {
    //     this.setDataMake(obj);
    //    yw = new com.yss.vsub.YwData(pub);
    //    ds = new com.yss.vsub.DSubGz(pub);
    //}

    /**
     * 实际处理证券交割单库
     * @param set int[]  套帐组
     * @throws YssException
     */

    public void makeData(String[] set) throws YssException {
        for (int i = 0; i < set.length; i++) {
            zqjgd(set[i]);
            xgSxdYw(set[i]);
        }
    }

    private void zqjgd(String set) throws YssException {
        Connection con = dbl.loadConnection();
        Statement st = null;
        ResultSet rs = null;
        boolean bTrans = false;
        boolean bIsSxd = false;
        String sBs = "";
        int intTemp = 0;
        try {
            st = con.createStatement();
            bTrans = true;
            String strSql = null;
//==            bIsSxd = yw.varGetValue(set + "采用申购新股核算规则", "0").equalsIgnoreCase("1");//随心打核算规则－刘永宜2007－11－08
//==            intTemp = Integer.parseInt(yw.varGetValue(set + "直接采用券商清算数据", "0"));
            //交行信托外部数据库：证券交割单库，如果选择“直接采用券商清算数据”那么，在插入qs中后还要更新。否则，按正常的流程走。
            if (intTemp != 1) {
                return;
            }

            con.setAutoCommit(false);

            strSql = "select * from ZQJGD a join csgddm b on " + dbl.sqlTrim("upper(a.gddm)") + "=" + dbl.sqlTrim("upper(b.fgddm)");
//==                  "where fdate = " + dbl.sqlDate(base.getDate());
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sBs = (rs.getDouble("qsje") > 0 ? "S" : "B");
                if (bIsSxd) {
                    strSql = "update XgHzJkQs " +
                        "set FsCjJe=" + rs.getDouble("CJJE") + "," + "FsJyFy=" + YssD.add(rs.getDouble("Yj"), rs.getDouble("Yhs"),
                        rs.getDouble("JSf"), rs.getDouble("Zgf"), rs.getDouble("Ghf"), YssD.add(rs.getDouble("Fxj"), rs.getDouble("Qtf"))) + "," +
                        "FsSsJe=" + Math.abs(rs.getDouble("qsje")) + " where FZqdm='" + rs.getString("zqdm") + "' and FGddm= '";
//==                		rs.getString("gddm")+ "' and FJyxwh='" + rs.getString("xwh") + "' and FsDate = " + dbl.sqlDate(base.getDate());
                    st.addBatch(strSql);
                }
                strSql = "update HzJkQs set F" + sBs + "Je = " + rs.getDouble("CJJE")
                    + " , F" + sBs + "Yj = " + rs.getDouble("Yj")
                    + " , F" + sBs + "Fxj = " + rs.getDouble("Fxj") + " ,   F" + sBs + "Yhs = " + rs.getDouble("Yhs")
                    + " , F" + sBs + "Jsf = " + rs.getDouble("JSf") + " , F" + sBs + "Zgf = " + rs.getDouble("Zgf")
                    + " , F" + sBs + "Ghf = " + rs.getDouble("Ghf") + " , F" + sBs + "Qtf = " + rs.getDouble("Qtf")
                    + " , FHgGain = " + rs.getDouble("fhggain") + (sBs.equalsIgnoreCase("B") ? ",FBSFJE = " : ",FSSSJe = ") + Math.abs(rs.getDouble("qsje"));
//==            + " where FZqdm = '" + rs.getString("Zqdm") + "' and FJyXwh = '" + rs.getString("xwh") + "' and fzqbz not in('QY','XG','HG','XZ','XGLT','XZLT') and fdate = " + dbl.sqlDate(base.getDate());
                st.addBatch(strSql);
            }
            rs.getStatement().close();
            st.executeBatch();
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理证券交割单库出错！", ex);
        } catch (YssException e) {
            throw e;
        } catch (Exception ex) {
            throw new YssException("处理证券交割单库出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(con, bTrans);
        }
    }

    /*
     * 信托新股随心打，计算结存成本，投资净收益，卖空后计算托管费、管理费
     */
    private void xgSxdYw(String set) throws YssException {
        Connection con = dbl.loadConnection();
        Statement st = null;
        ResultSet rs = null;
        boolean bTrans = false;

        String sql = "";
        double dZqSl = 0, dZqJg = 0, dZqJe = 0, dSsl = 0, dCjJe = 0, dJyFy = 0, dSsje = 0; //中签数量，中签单价，中签金额，卖出数量，卖出成交金额，交易费用，实收金额
        double dJcSl = 0, dJcCb = 0, dTzSy = 0, dGlf = 0, dTgf = 0, dYfSy = 0, dSyJe = 0; //结存数量，结存成本，投资净收益，管理费，托管费，受益人收益，本金及收益合计
        try {
            st = con.createStatement();
            con.setAutoCommit(false);
            bTrans = true;
//==		   sql = "select * from XgHzJkQs where FsDate = " + dbl.sqlDate(base.getDate()) ;
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                dZqSl = rs.getDouble("FZqSl");
                dZqJe = rs.getDouble("FZqJe"); //中签金额
                dZqJg = rs.getDouble("FZqJg"); //每股中签单价
                dSsl = rs.getDouble("FsSl");
                dCjJe = rs.getDouble("FsCjJe");
                dJyFy = rs.getDouble("FsJyFy");
                dSsje = rs.getDouble("FsSsJe");

                dJcSl = YssD.sub(dZqSl, dSsl); //结存数量
                dJcCb = YssD.round(YssD.mul(dJcSl, dZqJg), 2);

                dTzSy = YssD.round(YssD.sub(dSsje, YssD.mul(dSsl, dZqJg)), 2); //投资净收益
                dGlf = YssD.round(YssD.mul(dTzSy, 0.11), 2);
                dTgf = YssD.round(YssD.mul(dTzSy, 0.03), 2);
                dYfSy = YssD.sub(dTzSy, dGlf, dTgf); //受益人信托利益
                dSyJe = YssD.add(dZqJe, dYfSy); //受益人本金及收益合计

                sql = "update XgHzJkQs set FJcSl = " + dJcSl + ", FJcCb = " + dJcCb + ", " +
                    "FTzSy = " + dTzSy + ", FGlf = " + dGlf + ", FTgf = " + dTgf + ", FYfSy = " + dYfSy + "," +
//==			   		"FSyDate = " + dbl.sqlDate(ds.Get_WorkDay(rs.getDate("FsDate") , 1))+ ", FSyJe = " + dSyJe +
                    " where fzqdm = '" + rs.getString("FZqdm") + "'  and fgddm = '" + rs.getString("FGddm") + "'";
                st.addBatch(sql);

            }
            rs.getStatement().close();
            st.executeBatch();
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ee) {
            throw new YssException("处理新股随心打交易数据出错", ee.getNextException());
        } catch (Exception e) {
            throw new YssException("处理新股随心打交易数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(con, bTrans);
        }
    }

}
