package com.yss.main.operdeal.datainterface.pretfun.szstock;

import java.sql.*;

import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.util.*;

public class SZDzDataBean
    extends DataBase {
    private CalcBean base = null;
    public SZDzDataBean(CalcBean base) {
        this.base = base;
        this.setYssPub(base.getYssPub());
    }

    public void inertData() throws YssException {

    }

    /**
     * 两地对帐库的处理
     * @param set int[]    套帐组
     * @throws YssException
     */
    public void makeData(String[] set) throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        ResultSet rs = null;
        Statement st = null;
        String strSql = null;
        String strGddm = null;
        boolean bSave = false;
        try {
            st = con.createStatement();
            for (int i = 0; i < set.length; i++) {
//==       MaxStartDate msd = new MaxStartDate(dbl);
//==       strGddm = msd.getMaxStartDateSql(pub.yssSetPrefix(set[i]) + "CsGdDm" , "SET" , base.getDate()  , "FSzSh = 'H'" , "FGdDm" );

                st.addBatch("delete from JjHzDz where fdate=" + dbl.sqlDate(base.getDate()));
                //上海D4库中同一只股票会因为zqlb不一样(如，一只为“PT”（普通股），一只为“PS”（普通股_未流通）)，而出现两条记录，写数的时候应该把它们合成一条
                //上海D4库中同一只股票会因为zqlb不一样(如，一只为“PT”（普通股），一只为“PS”（普通股_未流通）)，而出现两条记录，写数的时候应该把它们合成一条
                /*strSql = "select zqdm,zqlb,sum(bcye) as kcsl from SHD4 where fdate=" + dbl.sqlDate(base.getDate()) + " and yybdm =(select"
                      + " fileprefix from " + pub.yssSetPrefix(set[i]) + "Jjprefix where filename='SHDZXXH') group by zqdm,zqlb";*/
                //原来这样写能满足注释中的要求么？太奇怪了，group by zqlb以后还怎么合并数据啊？？？cherry
                strSql = "select zqdm,max(zqlb) as zqlb,sum(bcye) as kcsl from SHD4 where fdate=" + dbl.sqlDate(base.getDate()) + " and yybdm =(select"
                    + " fileprefix from Jjprefix where filename='SHDZXXH') group by zqdm";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    strSql = "insert into JjHzDz(fdate,fzqdm,fszsh,fkcsl) values("
                        + dbl.sqlDate(base.getDate()) + ",'" + rs.getString("zqdm") + "','H',"
                        + (rs.getString("zqlb").equalsIgnoreCase("GZ") ? rs.getInt("kcsl") / 100 : rs.getInt("kcsl")) + ")";
                    st.addBatch(strSql);
                    bSave = true;
                }
                rs.getStatement().close();

                bSave = false;
                //2006年9月21日起中登上海分公司改发上海证券余额库替换原来的D4库
                //上海证券余额库中同一只股票会因为zqlb不一样(如，一只为“PT”（普通股），一只为“PS”（普通股_未流通）)，而出现两条记录，写数的时候应该把它们合成一条
                //ltlx = '1' 为兑付，过滤掉

                //strSql = msd.getMaxStartDateSql( + "csqsxw" , "SET" , base.getDate(), "FSzSh = 'H' and fxwlb ='ZY'" , "fqsxw" );
                strSql = "select a.zqdm,sum(a.kcsl) as kcsl from (select zqdm,sum(case when zqlb='GZ' then  ye1/100  else ye1  end ) as kcsl from SHZQYE"
                    + " where fdate=" + dbl.sqlDate(base.getDate()) + " and (" + dbl.sqlTrimNull("Qylb") +
                    " or Upper(Qylb)= 'P' )and zqzh in (" + strGddm + ") group by zqdm "
                    + " union all select zqdm,sum(case when zqlb='GZ' then  sl1/100 else sl1  end ) as kcsl from SHQTSL"
                    + " where " + dbl.sqlLeft("zqdm", 3) + " <> '888' and ltlx <> '1' and fdate=" + dbl.sqlDate(base.getDate())
                    + " and (sjlx ='007' and zqzh in (" + strGddm + ") or (" + dbl.sqlTrimNull("zqzh") + " and sjlx ='003' and xwh in ("
                    + strSql + ") and hydm in (select fileprefix from Jjprefix where filename='SHDZXXH')))  group by zqdm) a group by a.zqdm";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    strSql = "delete from JjHzDz where fdate = " + dbl.sqlDate(base.getDate()) + " and fzqdm ='" + rs.getString("zqdm") + "' and fszsh = 'H'";
                    st.addBatch(strSql);
                    strSql = "insert into JjHzDz(fdate,fzqdm,fszsh,fkcsl) values("
                        + dbl.sqlDate(base.getDate()) + ",'" + rs.getString("zqdm") + "','H'," + rs.getInt("kcsl") + ")";
                    st.addBatch(strSql);
                    bSave = true;
                }
                rs.getStatement().close();
            }
            con.setAutoCommit(false);
            bTrans = true;
            st.executeBatch();
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理两地对账库数据出错！", ex);
        } catch (Exception ex) {
            throw new YssException("处理两地对账库数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(con, bTrans);
        }
    }
}
