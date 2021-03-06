package com.yss.main.operdeal.datainterface.pretfun.szstock;

import java.sql.*;

import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.util.*;

public class SZHqDataBean
    extends DataBase {

    public SZHqDataBean() {

    }

    public void inertData() throws YssException {
        szhqData(null);
    }

    /**
     * 深圳行情库的处理
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
    public void szhqData(java.util.Date date) throws YssException {
        Connection con = dbl.loadConnection();
        PreparedStatement stm = null;
        ResultSet rs = null;
        boolean bTrans = false;
        // Statement st = null;
        String strSql = "";
        String sMktValueDate = "", sMktValueTime = "", HQzsyz = "", HQhqzt = "", HQLatelyTime = "", HQLatelyUpDataTime = "";
        try {
            java.util.Date dateT = new java.text.SimpleDateFormat("yyyy-MM-dd").
                parse("2003-12-08");
            //st = con.createStatement();
            strSql = "select * from tmp_sjshq where hqzqdm='000000'"; //取第一条
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sMktValueDate = rs.getString("HQZQJC");
                sMktValueTime = rs.getString("HQCJBS");
                HQzsyz = rs.getString("HQZRSP");
                HQhqzt = rs.getString("HQCJSL");
                HQLatelyTime = rs.getString("HQBSL4");
                HQLatelyUpDataTime = rs.getString("HQBSL5");
            }
            //  "delete from JjHzHq where fszsh='S' and flybz='ZD' and fjjdm=' ' and fdate=" +
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_MarketValue") +
                " where FMktValueDate =" + dbl.sqlDate(formatDate(sMktValueDate)) + " and FMktSrcCode='CS'" + //按行情日期与行情来源删除
                " and fdatasource = 1";
            dbl.executeSql(strSql);
            // dbl.sqlDate(date);
            // st.addBatch(strSql);
            //深圳行情:
            //注：2003-12-8深交所调整：债券、债券回购买卖申报数量单位由“手”（以人民币1,000元面额为1手）改为“张”（以人民币100元面额为1张）；
            if (YssFun.toDate(formatDate(sMktValueDate)).compareTo(dateT) == -1) {
                strSql = "insert into " + pub.yssGetTableName("Tb_Data_MarketValue") +
                    "(FMktSrcCode,FSecurityCode,FMktValueDate,FMktValueTime,FPortCode,FBargainAmount,FBargainMoney," +
                    "FYClosePrice,FOpenPrice,FTopPrice,FLowPrice,FClosingPrice,FAveragePrice,FNewPrice,FMktPrice1,FMktPrice2," +
                    "FDesc,FDataSource,FCheckState,FCreator,FCreateTime) " +
                    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                stm = dbl.openPreparedStatement(strSql);
                strSql = "select * from tmp_sjshq where hqzqdm <>'000000' and IsDel ='False'";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    stm.setString(1, "CS");
                    stm.setString(2, rs.getString("HQZQDM") + " CS");
                    stm.setDate(3, YssFun.toSqlDate(formatDate(sMktValueDate)));
                    stm.setString(4, "00:00:00");
                    stm.setString(5, " ");
                    stm.setDouble(6, YssFun.roundIt(rs.getDouble("HQCJSL"), 2));
                    stm.setDouble(7, YssFun.roundIt(rs.getDouble("HQCJJE"), 2));
                    stm.setDouble(8, YssFun.roundIt(rs.getDouble("HQZRSP"), 2));
                    stm.setDouble(9, YssFun.roundIt(rs.getDouble("HQJRKP"), 2));
                    stm.setDouble(10, YssFun.roundIt(rs.getDouble("HQZGCJ"), 2));
                    stm.setDouble(11, YssFun.roundIt(rs.getDouble("HQZDCJ"), 2));
                    stm.setDouble(12, YssFun.roundIt(rs.getDouble("HQZJCJ"), 2));
                    if (rs.getDouble("HQCJSL") > 0) {
                        stm.setDouble(13, (rs.getDouble("HQCJJE") / (rs.getDouble("HQCJSL"))));
                        if (!YssFun.left(rs.getString("HQZQDM"), 2).equals("15") || !YssFun.left(rs.getString("HQZQDM"), 2).equals("18")) {
                            stm.setDouble(13, YssFun.roundIt(rs.getDouble("HQCJJE") / (rs.getDouble("HQCJSL") * 10), 2));
                        }
                    } else {
                        stm.setDouble(13, 0);
                    }
                    stm.setDouble(14, YssFun.roundIt(rs.getDouble("HQZJCJ"), 2));
                    stm.setDouble(15, 0);
                    stm.setDouble(16, 0);
                    stm.setString(17, " ");
                    stm.setInt(18, 1);
                    stm.setInt(19, 1);
                    stm.setString(20, dbl.sqlString(pub.getUserCode()));
                    stm.setString(21, dbl.sqlString(YssFun.formatDate(pub.getUserDate(), "yyyy-MM-dd")));
                }
                stm.executeUpdate();
                /* "select 'CS', hqzqdm,hqzqdm,'S',hqzjcj,case when hqcjsl>0 and "
                 + dbl.sqlLeft("hqzqdm", 1) + "='1' and " +
                 dbl.sqlLeft("hqzqdm", 2) +
                 " not in('18','15') then hqcjje/(hqcjsl*10) when "
                 + "hqcjsl>0 then hqcjje/hqcjsl else 0 end,hqzrsp,hqjrkp,hqzgcj,hqzdcj,hqcjsl,hqcjje,case when hqzrsp>0 then "
                 +
                 "(hqzjcj-hqzrsp)/hqzrsp*100 else 0 end,'ZD',' ',0 from szhq where fdate="
                 + dbl.sqlDate(date) +
                 " and hqcjje<>0 and hqcjsl<>0 and hqzqdm <> '000000' and " +
                 dbl.sqlLeft("hqzqdm", 1) + " <> '2' and " +
                 dbl.sqlLeft("hqzqdm", 1) + " <> '3' order by hqzqdm";*/
            } else {
                // strSql = "insert into JjhzHq (FDate,FZqdm,FSzSh,FHqSsj,FHqPjj,FZrsp,FJrkp,FZgcj,FZdcj,FCjsl,FCjje,FZf,FLybz,Fjjdm,FSyqx) "
                /* +"select fdate,hqzqdm,'S',hqzjcj,round(case when hqcjsl>0 then hqcjje/"
                 + "hqcjsl else 0 end,case when " + dbl.sqlLeft("hqzqdm", 2) +
                 "= '03' then 3 else 2 end),hqzrsp,hqjrkp,hqzgcj,hqzdcj,hqcjsl,hqcjje,case when hqzrsp>0 then (hqzjcj-hqzrsp)/hqzrsp*100"
                 + " else 0 end,'ZD',' ',0  from szhq where fdate=" +
                 dbl.sqlDate(date) + " and "
                 + dbl.sqlLeft("hqzqdm", 2) + " not in('16','18','15')  and hqcjje<>0 and hqcjsl<>0 and hqzqdm <> '000000' and " +
                 dbl.sqlLeft("hqzqdm", 1) + " not in('2','3') order by hqzqdm";
                             st.addBatch(strSql);
                             strSql = "insert into JjhzHq (FDate,FZqdm,FSzSh,FHqSsj,FHqPjj,FZrsp,FJrkp,FZgcj,FZdcj,FCjsl,FCjje,FZf,FLybz,Fjjdm,FSyqx) "
                 +
                 "select fdate,hqzqdm,'S',hqzjcj,round(case when hqcjsl>0 then "
                 + "hqcjje/hqcjsl else 0 end,case when " +
                 dbl.sqlLeft("hqzqdm", 2) +
                 "= '16' then 4 else 3 end),hqzrsp,hqjrkp,hqzgcj,hqzdcj,hqcjsl,hqcjje,case when hqzrsp>0 then (hqzjcj-hqzrsp)/"
                 + "hqzrsp*100 else 0 end,'ZD',' ',0 from szhq where fdate=" +
                 dbl.sqlDate(date) + " and ("
                 + dbl.sqlLeft("hqzqdm", 2) +
                 " in ('16','18','15'))  and hqcjje<>0 and hqcjsl<>0 and hqzqdm <> '000000' and " +
                 dbl.sqlLeft("hqzqdm", 1) +
                 " not in ('2','3') order by hqzqdm";*/
                strSql = "insert into " + pub.yssGetTableName("Tb_Data_MarketValue") +
                    "(FMktSrcCode,FSecurityCode,FMktValueDate,FMktValueTime,FPortCode,FBargainAmount,FBargainMoney," +
                    "FYClosePrice,FOpenPrice,FTopPrice,FLowPrice,FClosingPrice,FAveragePrice,FNewPrice,FMktPrice1,FMktPrice2," +
                    "FDesc,FDataSource,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime) " +
                    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                stm = dbl.openPreparedStatement(strSql);
                strSql = "select * from tmp_sjshq where (substr(hqzqdm, 0, 2) NOT in ('16', '18', '15')) and hqcjje <> 0 and " +
                    " hqcjsl <> 0 and hqzqdm <> '000000' and  substr(hqzqdm, 0, 1) not in ('2', '3')";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    stm.setString(1, "CS");
                    stm.setString(2, rs.getString("HQZQDM") + " CS");
                    stm.setDate(3, YssFun.toSqlDate(formatDate(sMktValueDate)));
                    stm.setString(4, "00:00:00");
                    stm.setString(5, " ");
                    stm.setDouble(6, YssFun.roundIt(rs.getDouble("HQCJSL"), 2));
                    stm.setDouble(7, YssFun.roundIt(rs.getDouble("HQCJJE"), 2));
                    stm.setDouble(8, YssFun.roundIt(rs.getDouble("HQZRSP"), 2));
                    stm.setDouble(9, YssFun.roundIt(rs.getDouble("HQJRKP"), 2));
                    stm.setDouble(10, YssFun.roundIt(rs.getDouble("HQZGCJ"), 2));
                    stm.setDouble(11, YssFun.roundIt(rs.getDouble("HQZDCJ"), 2));
                    stm.setDouble(12, YssFun.roundIt(rs.getDouble("HQZJCJ"), 2));

                    if (rs.getDouble("HQCJSL") > 0) {
                        int iRound = 0;
                        if (YssFun.left(rs.getString("HQZQDM"), 2).equals("16")) {
                            iRound = 4;
                        } else {
                            iRound = 3;
                        }
                        stm.setDouble(13, YssFun.roundIt(rs.getDouble("HQCJJE") / (rs.getDouble("HQCJSL")), iRound));
                    } else {
                        stm.setDouble(13, 0);
                    }
                    stm.setDouble(14, YssFun.roundIt(rs.getDouble("HQZJCJ"), 2));
                    stm.setDouble(15, 0);
                    stm.setDouble(16, 0);
                    stm.setString(17, " ");
                    stm.setInt(18, 1);
                    stm.setInt(19, 1);
                    stm.setString(20, pub.getUserCode());
                    //stm.setString(21,YssFun.formatDate(pub.getUserDate(),"yyyy-MM-dd"));
                    stm.setString(21, YssFun.formatDate(formatDate(sMktValueDate), "yyyy-MM-dd")); //by leeyu 这里改为行情日期
                    stm.setString(22, pub.getUserCode());
                    stm.setString(23, YssFun.formatDatetime(new java.util.Date())); //这里再加上审核人及时间 by leeyu 080701
                    stm.executeUpdate();
                }
            }
            con.setAutoCommit(false);
            bTrans = true;
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理深圳行情库出错！", ex);
        } catch (Exception ex) {
            throw new YssException("处理深圳行情库出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(con, bTrans);
        }
    }

    private String formatDate(String sDate) throws YssException {
        //格式化yyyyMMdd的日期格式
        //返回 yyyy-MM-dd
        return YssFun.left(sDate, 4) + "-" + YssFun.mid(sDate, 4, 2) + "-" + YssFun.right(sDate, 2);
    }

    private String formatTime(String sTime) throws YssException {
        //格式化HHMMSS HHMMSSWW的时间格式
        //返回 HH:MM:SS
        if (sTime.length() == 6) {
            return YssFun.left(sTime, 2) + "-" + YssFun.mid(sTime, 2, 2) + "-" + YssFun.right(sTime, 2);
        } else if (sTime.length() == 8) {
            return YssFun.left(sTime, 2) + "-" + YssFun.mid(sTime, 2, 2) + "-" + YssFun.mid(sTime, 4, 2);
        } else {
            return sTime;
        }
    }
}
