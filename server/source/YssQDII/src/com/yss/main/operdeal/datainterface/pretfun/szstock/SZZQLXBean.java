package com.yss.main.operdeal.datainterface.pretfun.szstock;

import java.sql.*;

import com.yss.base.*;
import com.yss.util.*;

public class SZZQLXBean
    extends BaseAPOperValue {
    public SZZQLXBean() {
    }

    public void inertData() throws YssException {

    }

    /**
     * 实际处理债券利息库数据
     * @param date Date   读数日期
     * @throws YssException
     */
    public void makeData(java.util.Date date) throws YssException {
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        PreparedStatement stm = null;
        boolean bTrans = false;
        String strSql = "";
        java.util.Date dt = null;
        try {
            //20071204
            //java.util.Date dt = new com.yss.vsub.DSubGz(pub).Get_WorkDay(date, 1);
            //st = con.createStatement();

            //深圳
            //注意：GFWTXH(字段6：委托序号):表示计息日期（CCYYMMDD）；
            //　　　GFZJJE/GFQRGS表示下一交易日（T+1日）的国债每百元应计利息额，单位为元，保留8位小数；
            //strSql = "delete from jjgzlx where fjxrq=" + dbl.sqlDate(dt) + " and fszsh='S'";
            //st.addBatch(strSql);
            //strSql = "insert into jjgzlx select gfzqdm," + dbl.sqlDate(dt) + ",gfzjje/gfqrgs,gfwtgs,0,'S' from szgf where gfywlb='X1' and fdate="
            //      + dbl.sqlDate(date) + " group by gfzqdm,fdate,gfzjje/gfqrgs,gfwtgs";
            //st.addBatch(strSql);
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " where FRecordDate=" + date;
            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_BondInterest") +
                "(FSecurityCode,FRecordDate,FCurCpnDate,FNextCpnDate,FIntAccPer100,FIntDay,FCheckState,FCreator,FCreateTime)" +
                " values(?,?,?,?,?,?,?,?,?)";
//            stm = dbl.openPreparedStatement(strSql);
            strSql = "select * from ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {

            }
            conn.setAutoCommit(false);
            bTrans = true;
            //st.executeBatch();
            conn.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理债券利息库数据出错！", ex);
        } catch (Exception e) {
            throw new YssException("处理债券利息库数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
