package com.yss.main.operdeal.datainterface.pretfun.shstock;

import java.sql.*;

import com.yss.main.operdeal.datainterface.*;
import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.util.*;

public class SHZQLXBean
    extends DataBase {
    public SHZQLXBean() {

    }

    /**
     * 实际处理债券利息库数据
     * @param date Date   读数日期
     * @throws YssException
     */
    /**
     * 修改注释：
     * 此次修改了交易所代码，原上海为SH，深圳为SZ 现改为 上海CG，深圳CS
     * 修改了行情来源，原上海为SH，深圳为SZ，现改为上海CG，深圳CS
     * 修改日期：2008-08-21
     * 这样为了与国际化同步
     * @throws YssException
     */
    public void inertData() throws YssException {
        try {
            makeData(null);

        } catch (Exception e) {
            throw new YssException("插入数据报错!", e);
        }
    }

    public void makeData(java.util.Date date) throws YssException {
        Connection con = dbl.loadConnection();
        ResultSet rs = null;
        Statement st = null;
        boolean bTrans = false;
        java.util.Date dt = null;
        PreparedStatement pstmt = null;
        String sDate = "";
        String strSql = "";
        CommonPretFun pret = new CommonPretFun();
        try {
            pret.setYssPub(pub);
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " where FSecurityCode in(select GZDM||' '||'CG' from tmpSH_gzlx) " +
                " and FCurCpnDate=" + dbl.sqlDate(YssFun.formatDate(this.sDate, "yyyy-MM-dd"));
            dbl.executeSql(strSql);
            strSql = " select * from tmpSH_gzlx where jxrq=" + dbl.sqlString(YssFun.formatDate(this.sDate, "yyyyMMdd")) + " and ISDel='False'";
            rs = dbl.openResultSet(strSql);
            strSql = " insert into " + pub.yssGetTableName("Tb_Data_BondInterest") + "(FSecurityCode,FRecordDate,FCurCpnDate,FNextCpnDate," +
                " FIntAccPer100,FIntDay,FCheckState,FCreator,FCreateTime) values(?,?,?,?,?,?,?,?,?)";
            pstmt = dbl.openPreparedStatement(strSql);
            while (rs.next()) {
                sDate = pret.getDateConvert(rs.getString("JXRQ"));
                pstmt.setString(1, rs.getString("GZDM") + " CG"); //证券代码
                pstmt.setDate(2, YssFun.toSqlDate(sDate));
                pstmt.setDate(3, YssFun.toSqlDate(sDate));
                pstmt.setDate(4, YssFun.toSqlDate(sDate));
                pstmt.setDouble(5, rs.getDouble("YJLX"));
                pstmt.setInt(6, rs.getInt("LXTS"));
                pstmt.setInt(7, 1);
                pstmt.setString(8, pub.getUserCode()); //创建人、修改人
                pstmt.setString(9, YssFun.formatDatetime(new java.util.Date())); //创建、修改时间
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new YssException("插入数据出错", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(con, bTrans);
        }
    }
}
