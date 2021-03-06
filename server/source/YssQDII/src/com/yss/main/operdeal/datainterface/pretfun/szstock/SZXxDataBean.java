package com.yss.main.operdeal.datainterface.pretfun.szstock;

import java.sql.*;
import java.util.*;

import com.yss.main.basesetting.*;
import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.util.*;

public class SZXxDataBean
    extends DataBase {
    public SZXxDataBean() {
    }

    public void inertData() throws YssException {
        makeData(null);
    }

    /**
     * 实际处理信息库数据
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
    public void makeData(java.util.Date date) throws YssException {
        ExchangeBean exchange = new ExchangeBean();
        Connection con = dbl.loadConnection();
        ResultSet rs = null;
        PreparedStatement stm = null;
        boolean bTrans = false;
        String strSql = "";
        String sDate = "", catCode = "", subCatCode = " ", sCusCatCode = " ";
        HashMap hmSip = new HashMap();
        try {
            bTrans = true;
            con.setAutoCommit(false);
            exchange.setYssPub(pub);
            exchange.setStrExchangeCode("CS");
            exchange.getSetting();
            strSql = "select * from tmp_sjsxx where XXZQDM='000000'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sDate = formatDate(rs.getString("xxzqjc"));
            }
            rs.close();
            strSql = " delete from " + pub.yssGetTableName("tb_para_security") +
                " where FsecurityCode in (select XXZQDM||' '||'CS' from tmp_sjsxx) and FStartDate = " +
                dbl.sqlDate(sDate);
            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("TB_PARA_SECURITY") +
                " (FSecurityCode,FSecurityName,FExchangeCode,FMarketCode,FCatCode,FSubCatCode,FCusCatCode," +
                " FISINCode,FExternalCode,FTradeCury,FSettleDayType,FHolidaysCode,FSettleDays,FSectorCode," +
                " FTotalShare,FCurrentShare,FHandAmount,FFactor,FIssueCorpCode,FStartDate,FDesc,FCheckState," +
                " FCreator,FCreateTime,FCheckUser,FCheckTime)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            stm = dbl.openPreparedStatement(strSql);
            strSql =
                "select * from tmp_sjsxx where substr(XXZQDM,0,2) in('00','10','11','13','12','15','18','03') and " +
                "xxzqdm ||' '||'CS' not in (select FSECURITYCODE from  " + pub.yssGetTableName("TB_PARA_SECURITY") + ") " +
                " and XXZQDM<>'000000' and IsDel ='False'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) { //深圳证券信息
                if (hmSip.get( (rs.getString("XXZQDM") + " CS")) == null) {
                    hmSip.put( (rs.getString("XXZQDM") + " CS"),
                              (rs.getString("XXZQDM") + " CS"));
                } else {
                    continue;
                }
                catCode = " ";
                subCatCode = " ";
                sCusCatCode = " ";
                stm.setString(1, rs.getString("XXZQDM") + " CS");
                stm.setString(2, rs.getString("XXZQJC"));
                stm.setString(3, "CS"); //交易所
                stm.setString(4, rs.getString("XXZQDM")); //上市代码
                if (rs.getString("XXZQDM").startsWith("00")) {
                    catCode = "EQ";
                    subCatCode = "EQ01";
                } else if (rs.getString("XXZQDM").startsWith("10")) { //债券
                    catCode = "FI";
                    sCusCatCode = "FIGZXQ";
                } else if (rs.getString("XXZQDM").startsWith("11")) { //债券
                    catCode = "FI";
                    sCusCatCode = "FIQYZ";
                } else if (rs.getString("XXZQDM").startsWith("12")) { //债券
                    catCode = "FI";
                    sCusCatCode = "FIKZZ";
                } else if (rs.getString("XXZQDM").startsWith("13")) { //回购
                    catCode = "RE";
                    subCatCode = "RE01";
                } else if (rs.getString("XXZQDM").startsWith("15")) { //ETF基金
                    catCode = "TR";
                    subCatCode = "TR04";
                } else if (rs.getString("XXZQDM").startsWith("16")) { //开放式基金
                    catCode = "TR";
                    subCatCode = "TR02";
                } else if (rs.getString("XXZQDM").startsWith("18")) { //封闭式基金
                    catCode = "TR";
                    subCatCode = "TR01";
                } else if (rs.getString("XXZQDM").startsWith("03")) { //权证
                    catCode = "OP";
                    subCatCode = "OP01";
                }
                stm.setString(5, catCode);
                stm.setString(6, subCatCode);
                stm.setString(7, sCusCatCode); //自定义品种类型
                stm.setString(8, " ");
                stm.setString(9, " ");
                stm.setString(10, "CNY"); //人民币
                stm.setInt(11, YssFun.toInt(exchange.getSettleDayType())); //工作日
                stm.setString(12, exchange.getHolidaysCode()); //节假日群
                stm.setInt(13, YssFun.toInt(exchange.getSettleDays())); //延迟天数
                stm.setString(14, rs.getString("XXHYZL")); //板块
                stm.setDouble(15, rs.getDouble("XXZFXL") * rs.getDouble("XXMGMZ")); //总股本=总发行量* 每股面值
                stm.setDouble(16, rs.getDouble("XXLTGS") * rs.getDouble("XXMGMZ")); //流通股本=每股面值*流通股数
                stm.setDouble(17, rs.getDouble("XXBLDW")); //每手数量
                stm.setDouble(18, rs.getDouble("XXMGMZ")); //报价因子
                stm.setString(19, " ");
                stm.setDate(20, YssFun.toSqlDate(sDate));
                stm.setString(21, " ");
                stm.setInt(22, 1);
                stm.setString(23, pub.getUserCode());
                stm.setString(24, YssFun.formatDatetime(new java.util.Date()));
                stm.setString(25, pub.getUserCode());
                stm.setString(26, YssFun.formatDatetime(new java.util.Date()));
                stm.executeUpdate();
            }
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理信息库数据出错！", ex);
        } catch (Exception e) {
            throw new YssException("处理信息库数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(con, bTrans);
        }
    }

    private String formatDate(String sDate) throws YssException {
        //格式化yyyyMMDD的日期格式
        //返回 yyyy-MM-dd
        return YssFun.left(sDate, 4) + "-" + YssFun.mid(sDate, 4, 2) + "-" +
            YssFun.right(sDate, 2);
    }
}
