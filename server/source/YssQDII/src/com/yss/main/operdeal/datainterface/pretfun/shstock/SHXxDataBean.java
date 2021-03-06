package com.yss.main.operdeal.datainterface.pretfun.shstock;

import com.yss.dsub.*;
import com.yss.util.YssException;
import java.sql.*;
import com.yss.base.BaseAPOperValue;
import com.yss.main.operdeal.datainterface.pretfun.*;

public class SHXxDataBean
    extends DataBase {
    public SHXxDataBean(YssPub pub) {
        this.setYssPub(pub);
    }

    /**
     * 实际处理信息库数据
     * @param date Date   读数日期
     * @throws YssException
     */
    public void makeData(java.util.Date date) throws YssException {
        Connection con = dbl.loadConnection();
        ResultSet rs = null;
        Statement st = null;
        boolean bTrans = false;
        String strSql = "";
        try {
            st = con.createStatement();
            bTrans = true;
            con.setAutoCommit(false);
            //1 新增股票
            strSql = "select s1 as zqdm from shhq where s1 not in (select fmarketcode from " + pub.yssGetTableName("TB_PARA_SECURITY") + ") and fdate=" + dbl.sqlDate(date);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("sl").startsWith("6")) { //普通A股
                    strSql = "insert into " + pub.yssGetTableName("TB_PARA_SECURITY") + " select s1,s2,0,0,' ', ' ',1,'" + pub.getUserCode() + "',' '," + dbl.sqlDate("1900-1-1") + " from shhq where s1='" + rs.getString("zqdm") + "' and fdate=" + dbl.sqlDate(date);
                //} else if (rs.getString("sl").startsWith("0")) { //国债现券//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun

                }
                st.addBatch(strSql);
            }
            rs.getStatement().close();
            st.executeBatch();

            //2 更新名称
            //strSql = msd.getMaxStartDateSql("JjHzXx" , "" ,date);
            strSql = "select s1 as zqdm,s2 as zqmc from (" + strSql + ") a,shhq b where a.fzqdm =b.s1 and a.fzqmc<>b.s2 and b.fdate=" + dbl.sqlDate(date);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                strSql = "update jjhzxx set fzqmc='" + rs.getString("zqmc") + "' where fzqdm='" + rs.getString("zqdm") + "'";
                st.addBatch(strSql);
            }
            rs.getStatement().close();

            st.executeBatch();
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理信息库数据出错！", ex);
        } catch (Exception e) {
            throw new YssException("处理信息库数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(con, bTrans);
        }
    }
}
