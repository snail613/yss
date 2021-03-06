package com.yss.main.operdeal.datainterface.pretfun.shstock;

import java.sql.*;

import com.yss.main.basesetting.*;
import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.util.*;

public class SHHqDataBean
    extends DataBase {

    public SHHqDataBean() {

    }

    /**
     * 修改注释：
     * 此次修改了交易所代码，原上海为SH，深圳为SZ 现改为 上海CG，深圳CS
     * 修改了行情来源，原上海为SH，深圳为SZ，现改为上海CG，深圳CS
     * 修改日期：2008-08-21
     * 这样为了与国际化同步
     * @throws YssException
     */

    public void inertData() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String sDate = "";
        try {
            strSql = "select S6 from sh_show2003 where S1=" +
                dbl.sqlString("000000");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sDate = rs.getString("S6");
            }
            rs = null;
            if (sDate.trim().length() != 0 && sDate != "null") {
                if (YssFun.isDate(sDate)) {
                    //checkTradeSub(YssFun.toDate(sDate));//增加对当日交易数据的检查
                    shhqData(YssFun.toDate(sDate));
                } else {
                    sDate = YssFun.left(sDate, 4) +
                        "-" + YssFun.mid(sDate, 4, 2) + "-" +
                        YssFun.right(sDate, 2);
                    //checkTradeSub(YssFun.toDate(sDate));//增加对当日交易数据的检查
                    shhqData(YssFun.toDate(sDate));
                }
            }
        } catch (Exception e) {
            throw new YssException("获取数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //by leeyu 添加，关闭RS 2008-12-3 MS00031
        }
    }

    /**
     * 上海行情库的处理
     * @param date Date   读数日期
     * @throws YssException
     */
    public void shhqData(java.util.Date date) throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        Statement st = null;
        ResultSet rs = null;
        ResultSet rsTmp = null;
        String strSql = "";
        String strDate;
        PreparedStatement pstmt = null;

        String securityCode = "";
        String osecurityCode = "";
        String catCode = "";
        String cusCatCode = "";
        String subCatCode = " ";
        ExchangeBean exchange = new ExchangeBean();
        try {
            exchange.setStrExchangeCode("CG");
            exchange.setYssPub(pub);
            exchange.getSetting();
            strDate = dbl.sqlDate(date);
            // 先删除行情数据表中寸在的数据
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_MarketValue") +
                " where FMktSrcCode='CG' and fdatasource = 1 and FMktValueDate=" + strDate;
            dbl.executeSql(strSql);

            //strSql = "delete from " + pub.yssGetTableName("tb_para_security") +
            //      " where FsecurityCode in" +
            //      "(select S1 || ' ' || 'SH' from sh_show2003 where isdel=" +
            //      dbl.sqlString("False") + ")";
            //dbl.executeSql(strSql);

            // 由于上交所的证券信息是从行情数据中得到的所以要先插入证券信息
            //---------------------------------------------------------------------
            strSql =
                "insert into " + pub.yssGetTableName("tb_para_security") +
                "(FSecurityCode, FSecurityName, FExchangeCode, FMarketCode, FCatCode, FSubCatCode, FCusCatCode," +
                " FISINCode, FExternalCode, FTradeCury, FSettleDayType, FHolidaysCode, FSettleDays, FSectorCode, " +
                " FTotalShare, FCurrentShare,FHandAmount,FFactor,FIssueCorpCode,FStartDate,FDesc,FCheckState," +
                " FCreator, FCreateTime,FCheckUser,FCheckTime) " +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(strSql);

            strSql = "select * from sh_show2003 a where a.isdel=" +
                dbl.sqlString("False") + " and " +
                dbl.sqlLeft("s1", 1) + " in ('6','0','2','1','5') and "
                + dbl.sqlLeft("s1", 4) +
                " not in ('0000') and s1 <> '000300' " +
                " and not exists(select * from " +
                pub.yssGetTableName("tb_para_security") +
                " where fmarketcode = a.s1 and FCheckState=1)";
            rs = dbl.openResultSet(strSql);
            con.setAutoCommit(false);
            while (rs.next()) {
                osecurityCode = rs.getString("S1");
                securityCode = rs.getString("S1");
                catCode = " ";
                subCatCode = " ";
                cusCatCode = " ";
                if (securityCode.startsWith("6")) {
                    catCode = "EQ";
                    subCatCode = "EQ01";
                } else if (securityCode.startsWith("58")) {
                    catCode = "OP";
                    subCatCode = "OP01";
                }

                else if (securityCode.startsWith("5")) {
                    catCode = "TR";
                    if (securityCode.startsWith("510")) { //ETF基金
                        subCatCode = "TR04";
                    } else {
                        subCatCode = "TR01";
                    }
                } else if (securityCode.startsWith("0") ||
                           securityCode.startsWith("1")) { //债券
                    catCode = "FI"; // by leeyu
                    subCatCode = "FI01";
                    if (securityCode.startsWith("0")) { //国债现券
                        cusCatCode = "FIGZXQ";
                    } else if (securityCode.startsWith("1", 1)) { //可转债
                        cusCatCode = "FIJysKzhZQ";
                    } else if (securityCode.startsWith("121")) {
                        cusCatCode = "FIJysKzhZQ";
                    } else if (securityCode.startsWith("122")) {
                        cusCatCode = "FIJysKzhZQ";
                    } else { // 企业债券
                        cusCatCode = "FIGSZQ";
                    }
                } else if (securityCode.startsWith("20")) { //回购
                    catCode = "RE";
                    if (securityCode.startsWith("1", 2) ||
                        securityCode.startsWith("4", 2)) { // 国债回购、质压式回购
                        subCatCode = "RE01";
                    } else if (securityCode.startsWith("2", 2)) { // 企业债券回购
                        subCatCode = "RE02";
                    } else if (securityCode.startsWith("3", 2)) { // 买断式国债回购
                        subCatCode = "RE11";
                    }
                } else if (securityCode.startsWith("740") ||
                           securityCode.startsWith("730") ||
                           securityCode.startsWith("731")) {
                    securityCode = "600" + YssFun.right(securityCode, 3);
                    catCode = "EQ";
                    subCatCode = "EQ02";
                } else if (securityCode.startsWith("737") ||
                           securityCode.startsWith("739")) {
                    if (securityCode.startsWith("7", 2)) { // 按市值配售新股T＋1日初次中签(上海中签上海股票)
                        securityCode = securityCode.replaceFirst("737", "600");
                    } else if (securityCode.startsWith("9", 2)) { // 按市值配售新股T＋1日初次中签(上海中签深圳股票)
                        securityCode = securityCode.replaceFirst("739", "002");
                    }
                    catCode = "EQ";
                    subCatCode = "EQ02";
                }

                else if (securityCode.startsWith("790") ||
                         securityCode.startsWith("780")) {
                    securityCode = "601" + YssFun.right(securityCode, 3);
                    catCode = "EQ";
                    subCatCode = "EQ02";
                }

                else if (securityCode.startsWith("760") ||
                         securityCode.startsWith("781")) {
                    securityCode = "601" + YssFun.right(securityCode, 3);
                    catCode = "EQ";
                    subCatCode = "EQ01";
                } else if (securityCode.startsWith("733") ||
                           securityCode.startsWith("743") ||
                           securityCode.startsWith("780") ||
                           securityCode.startsWith("793")) {
                    securityCode = "110" + YssFun.right(securityCode, 3);
                    catCode = "FI";
                    cusCatCode = "FIKZZ"; ;
                } else if (securityCode.startsWith("762") ||
                           securityCode.startsWith("764") ||
                           securityCode.startsWith("783") ||
                           securityCode.startsWith("793")) {
                    securityCode = "113" + YssFun.right(securityCode, 3);
                    catCode = "FI";
                    cusCatCode = "FIKZZ"; ;
                }

                else if (securityCode.startsWith("70")) {
                    if (rs.getDouble("cjjg") == 100) { // // 老股东配债
                        securityCode = "110" + YssFun.right(securityCode, 3);
                        catCode = "FI";
                        cusCatCode = "FIKZZ"; ;
                    } else { // 配股
                        securityCode = "600" + YssFun.right(securityCode, 3);
                        catCode = "EQ";
                        subCatCode = "EQ01";
                    }
                }
                if (!securityCode.equalsIgnoreCase(osecurityCode)) {
                    dbl.closeResultSetFinal(rsTmp);
                    strSql = "select * from " +
                        pub.yssGetTableName("tb_para_security") +
                        " where fsecuritycode = " + securityCode + " CG";
                    rsTmp = dbl.openResultSet(strSql);
                    while (rsTmp.next()) {
                        continue;
                    }
                }
                pstmt.setString(1, securityCode + " CG");
                pstmt.setString(2, rs.getString("S2"));
                pstmt.setString(3, "CG");
                pstmt.setString(4, securityCode);
                pstmt.setString(5, catCode);
                pstmt.setString(6, subCatCode);
                pstmt.setString(7, cusCatCode);
                pstmt.setString(8, " "); //==
                pstmt.setString(9, " "); //==
                pstmt.setString(10, "CNY");
                pstmt.setInt(11, Integer.parseInt(exchange.getSettleDayType()));
                pstmt.setString(12, exchange.getHolidaysCode());
                pstmt.setInt(13, Integer.parseInt(exchange.getSettleDays()));
                pstmt.setString(14, " ");
                pstmt.setInt(15, 0);
                pstmt.setInt(16, 0);
                pstmt.setInt(17, 0);
                pstmt.setInt(18, 1);
                pstmt.setString(19, "");
                pstmt.setDate(20, YssFun.toSqlDate(date));
                pstmt.setString(21, "");
                pstmt.setInt(22, 1);
                pstmt.setString(23, pub.getUserCode());
                pstmt.setString(24, YssFun.formatDatetime(new java.util.Date()));
                pstmt.setString(25, pub.getUserCode());
                pstmt.setString(26, YssFun.formatDatetime(new java.util.Date()));
                pstmt.executeUpdate();
            }
            con.commit();
            con.setAutoCommit(true);
            //---------------------------------------------------------------------
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_MarketValue") +
                "(FMktSrcCode,FSecurityCode,FMktValueDate,"
                + "FMktValueTime,FPortCode,FBargainAmount,FBargainMoney,FYClosePrice,FOpenPrice,FTopPrice,FLowPrice,"
                + "FClosingPrice,FAveragePrice,FNewPrice,FMktPrice1,FMktPrice2,FDesc,FDataSource,FCheckState,FCreator, FCreateTime,FCheckUser,FCheckTime)"
                + " select 'CG',s1" + dbl.sqlJoinString() + "' '" + dbl.sqlJoinString() + "'CG'," + strDate + "," +
                dbl.sqlString("00:00:00") + ",' ',s11,s5,s3,s4,s6,s7,s8,"
                + " round(case when s11 <> 0 then (case when " +
                dbl.sqlLeft("s1", 1)
                + "='0' or " + dbl.sqlLeft("s1", 2) + "='10' or " +
                dbl.sqlLeft("s1", 2) + "='11' or " + dbl.sqlLeft("s1", 2)
                + "='12' then s5/(s11*10) when " + dbl.sqlLeft("s1", 2) +
                " ='58'  then s5/(s11*100) else s5/s11 end) else s5 end ,case when " +
                dbl.sqlLeft("s1", 1)
                + "='5' then 3 else 2 end) as hqpjj,"
                + "s8,0,0,'',1,1,"
                + dbl.sqlString(pub.getUserCode()) + "," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
                dbl.sqlString(pub.getUserCode()) + "," +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                //================================将这里修改根据头一天持仓的证券代码情况来取证券 MS00031
                //" from sh_show2003 where isdel=" + dbl.sqlString("False")
//               " from sh_show2003 a join (select FSecurityCode from " +
//               pub.yssGetTableName("tb_stock_security") + " where FPortCode=" +
//               dbl.sqlString(this.sPort) +
//               " and FStorageDate=(select max(FStorageDate) from " +
//               pub.yssGetTableName("tb_stock_security") + " where FPortCode=" +
//               dbl.sqlString(sPort) + " and FStorageDate<" + dbl.sqlDate(date) +
//               ") )b on a.S1"+dbl.sqlJoinString()+"' CG' = b.FSecurityCode " +
//               " where isdel=" + dbl.sqlString("False")
                //==========================MS00031
                //============== 这里的代码先前写的有问题，现改为从当天的交易与头天的库存中取证券信息，然后再导入到行情里 by leeyu 修改 MS00031
                " from sh_show2003 where isdel=" + dbl.sqlString("False") +
                " and s1" + dbl.sqlJoinString() + "' CG' in(" +
                " select FSecurityCode from " +
                pub.yssGetTableName("tb_data_subtrade") + " where FBargainDate=" +
                dbl.sqlDate(date) + " and " + dbl.sqlRight("FSecurityCode", 2) +
                "='CG' and FPortCode=" + dbl.sqlString(sPort) + " " +
                " union select FSecurityCode from " + pub.yssGetTableName("tb_stock_security") +
                " where FStorageDate =" + dbl.sqlDate(YssFun.addDay(date, -1)) + " and FportCode=" + dbl.sqlString(sPort) + " and " + dbl.sqlRight("FSecurityCode", 2) + " ='CG' )"
                //===============
                + " and s11 <> 0 and s5 <> 0 and s8 <> 0 and " +
                dbl.sqlLeft("s1", 1) + " in ('6','0','1','5') and "
                + dbl.sqlLeft("s1", 4) +
                " not in ('0000') and s1 <> '000300' order by s1";
            st = con.createStatement();
            st.addBatch(strSql);
            //================================将这里添加根据当日交易数据情况来添加新券 MS00031
//         strSql = "insert into " + pub.yssGetTableName("Tb_Data_MarketValue") +
//               "(FMktSrcCode,FSecurityCode,FMktValueDate,"
//               + "FMktValueTime,FPortCode,FBargainAmount,FBargainMoney,FYClosePrice,FOpenPrice,FTopPrice,FLowPrice,"
//               + "FClosingPrice,FAveragePrice,FNewPrice,FMktPrice1,FMktPrice2,FDesc,FDataSource,FCheckState,FCreator, FCreateTime,FCheckUser,FCheckTime)"
//               + " select 'CG',s1"  + dbl.sqlJoinString() + "' '" + dbl.sqlJoinString() + "'CG'," + strDate + "," +
//               dbl.sqlString("00:00:00") + ",' ',s11,s5,s3,s4,s6,s7,s8,"
//               + " round(case when s11 <> 0 then (case when " +
//               dbl.sqlLeft("s1", 1)
//               + "='0' or " + dbl.sqlLeft("s1", 2) + "='10' or " +
//               dbl.sqlLeft("s1", 2) + "='11' or " + dbl.sqlLeft("s1", 2)
//               + "='12' then s5/(s11*10) when " + dbl.sqlLeft("s1", 2) +
//               " ='58'  then s5/(s11*100) else s5/s11 end) else s5 end ,case when " +
//               dbl.sqlLeft("s1", 1)
//               + "='5' then 3 else 2 end) as hqpjj,"
//               + "s8,0,0,'',1,1,"
//               + dbl.sqlString(pub.getUserCode()) + "," +
//               dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
//               dbl.sqlString(pub.getUserCode()) + "," +
//               dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
//               " from sh_show2003 a join (select FSecurityCode from " +
//               pub.yssGetTableName("tb_data_subtrade") + " b where FPortCode=" +
//               dbl.sqlString(sPort) + " and FBargainDate=" + dbl.sqlDate(date) +
//               " and " + dbl.sqlRight("FSecurityCode", 2) +
//               "='CG' and not exists(select 1 from "+pub.yssGetTableName("tb_stock_security")+"  where FSecurityCode=b.FSecuritycode))b on a.S1"+dbl.sqlJoinString()+"' CG' = b.FSecurityCode " +
//               " where isdel=" + dbl.sqlString("False")
//               + " and s11 <> 0 and s5 <> 0 and s8 <> 0 and " +
//               dbl.sqlLeft("s1", 1) + " in ('6','0','1','5') and "
//               + dbl.sqlLeft("s1", 4) +
//               " not in ('0000') and s1 <> '000300' order by s1";
//
//         st.addBatch(strSql);
            //===================MS00031
            con.setAutoCommit(false);
            bTrans = true;
            st.executeBatch();
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理上海行情库出错！", ex);
        } catch (Exception ex) {
            throw new YssException("处理上海行情库出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs, rsTmp);
            dbl.closeStatementFinal(st, pstmt);
            dbl.endTransFinal(con, bTrans);
        }
    }

    /**
     * //shuo20070905增加导入上海固定收益平台行情数据
     * @param date Date   读数日期
     * @throws YssException
     */
    public void shBjQbData(java.util.Date date) throws YssException {
        String strDate;
        Statement st = null;
        boolean bTrans = false;
        ResultSet rs = null;
        try {
            Connection con = dbl.loadConnection();
            st = con.createStatement();
            strDate = dbl.sqlDate(date);
            String strSql =
                "delete from JjHzHq where fszsh='H' and flybz='ZD' and fjjdm=' ' and fdate=" +
                strDate
                + " and (FZrsp = 0 and FJrKp=0 ) ";
            st.addBatch(strSql);

            strSql =
                "insert into JjhzHq (FDate,FZqdm,FSzSh,FHqSsj,FHqPjj,FZrsp,FJrkp,FZgcj,FZdcj," +
                "FCjsl,FCjje,FZf,FLybz,Fjjdm,FSyqx) select a.fdate,a.fzqdm,'H'  fszsh, round((fmrjj + fmcjj)/2000 ,2) fhqssj," +
                "round((fmrjj + fmcjj)/2000 ,2) fhqpjj, 0 fzrsb, 0 fjrkp,0 fzgcj,0 fzdcj,0 fcjsl,0 fcjje,0 fzf,'ZD' flybz, " +
                "' ' fjjdm,0 fsyqx from shbjqb a inner join (select fzqdm,fdate,min(fxh) fxh from shbjqb where fdate = " +
                strDate + " group by fzqdm,fdate) b on a.fzqdm = b.fzqdm and a.fdate = b.fdate and a.fxh = b.fxh";
            st.addBatch(strSql);

            con.setAutoCommit(false);
            bTrans = true;
            st.executeBatch();
            con.commit();
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理上海固定收益平台行情数据出错！", ex);
        } catch (Exception ex) {
            throw new YssException("处理上海固定收益平台行情数据出错！", ex);
        } finally {
            dbl.closeStatementFinal(st);
            //   dbl.endTransFinal(con, bTrans);
        }
    }

    //上海两个行情根据选项处理
    public void shDoubleHq(java.util.Date date) throws YssException {
        String strDate, sqlShhq = "", sqlGdpthq = "";
        boolean gdpt = false;
//==	      DSubGz dsubGz = new DSubGz(pub);
//==	      YwData yw = new YwData(pub);
        strDate = dbl.sqlDate(date);
        sqlShhq = "select s1 as fzqdm from shhq where fdate = " + strDate;
        sqlGdpthq = "select fzqdm from shbjqb where fdate = " + strDate;
        try {
//==	        	 if(dsubGz.SqlRecordCount(sqlShhq) > 0 && ! (dsubGz.SqlRecordCount(sqlGdpthq) > 0))
            shhqData(date);
//==	        	 else if(! (dsubGz.SqlRecordCount(sqlShhq) > 0) && dsubGz.SqlRecordCount(sqlGdpthq) > 0)
//==	        		 shBjQbData();
//==	        	 else if(dsubGz.SqlRecordCount(sqlShhq) > 0 && dsubGz.SqlRecordCount(sqlGdpthq) > 0) {
            shhqData(date);
            shBjQbData(date);
            sqlShhq = "select distinct a.fzqdm from (" + sqlShhq + ") a join (" +
                sqlGdpthq + ") b on a.fzqdm = b.fzqdm";
//==	        		 if(dsubGz.SqlRecordCount(sqlShhq) > 0) {//如果上海行情和固定平台行情有重复部分,删除不要的即可
            ResultSet rs = dbl.openResultSet(sqlShhq);
            String sqlDel = " delete from JjHzHq where fszsh='H' and flybz='ZD' " +
                " and fjjdm=' ' and fdate=" + strDate;

//==	        		 gdpt = yw.varGetValue(pub.getCurrentSet() + "新公司债取固定收益平台").equalsIgnoreCase("1");
            if (gdpt) {
                sqlDel = sqlDel + " and (FZrsp <> 0 or FJrKp <> 0 )";
            } else {
                sqlDel = sqlDel + " and (FZrsp = 0 and FJrKp=0 ) and fzf = 0 ";
            } while (rs.next()) {
                dbl.executeSql(sqlDel + " and fzqdm = '" + rs.getString(1) + "'");
            }
        }
//==	        	 }
//==	        }
        catch (Exception ex) {
            throw new YssException("处理上海两个行情库出错！", ex);
        } finally {

        }
    }

    /**
     * 深圳行情库的处理
     * @param date Date   读数日期
     * @throws YssException
     */
    public void szhqData(java.util.Date date) throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        Statement st = null;
        try {
            java.util.Date dateT = new java.text.SimpleDateFormat("yyyy-MM-dd").
                parse("2003-12-08");
            st = con.createStatement();
            String strSql =
                "delete from JjHzHq where fszsh='S' and flybz='ZD' and fjjdm=' ' and fdate=" +
                dbl.sqlDate(date);
            st.addBatch(strSql);
            //深圳行情:
            //注：2003-12-8深交所调整：债券、债券回购买卖申报数量单位由“手”（以人民币1,000元面额为1手）改为“张”（以人民币100元面额为1张）；
            if (date.compareTo(dateT) == -1) {
                strSql = "insert into JjhzHq (FDate,FZqdm,FSzSh,FHqSsj,FHqPjj,FZrsp,FJrkp,FZgcj,FZdcj,FCjsl,FCjje,FZf,FLybz,Fjjdm,FSyqx) "
                    + "select fdate,hqzqdm,'S',hqzjcj,case when hqcjsl>0 and "
                    + dbl.sqlLeft("hqzqdm", 1) + "='1' and " +
                    dbl.sqlLeft("hqzqdm", 2) +
                    " not in('18','15') then hqcjje/(hqcjsl*10) when "
                    + "hqcjsl>0 then hqcjje/hqcjsl else 0 end,hqzrsp,hqjrkp,hqzgcj,hqzdcj,hqcjsl,hqcjje,case when hqzrsp>0 then "
                    +
                    "(hqzjcj-hqzrsp)/hqzrsp*100 else 0 end,'ZD',' ',0 from szhq where fdate="
                    + dbl.sqlDate(date) +
                    " and hqcjje<>0 and hqcjsl<>0 and hqzqdm <> '000000' and " +
                    dbl.sqlLeft("hqzqdm", 1) + " <> '2' and " +
                    dbl.sqlLeft("hqzqdm", 1) + " <> '3' order by hqzqdm";
            } else {
                strSql = "insert into JjhzHq (FDate,FZqdm,FSzSh,FHqSsj,FHqPjj,FZrsp,FJrkp,FZgcj,FZdcj,FCjsl,FCjje,FZf,FLybz,Fjjdm,FSyqx) "
                    +
                    "select fdate,hqzqdm,'S',hqzjcj,round(case when hqcjsl>0 then hqcjje/"
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
                    " not in ('2','3') order by hqzqdm";
            }
            st.addBatch(strSql);
            con.setAutoCommit(false);
            bTrans = true;
            st.executeBatch();
            con.commit();
            szetfhq(date, con, st); //etf行情特殊处理
            bTrans = false;
        } catch (BatchUpdateException ex) {
            throw new YssException("处理深圳行情库出错！", ex);
        } catch (Exception ex) {
            throw new YssException("处理深圳行情库出错！", ex);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(con, bTrans);
        }
    }

    public void szetfhq(java.util.Date date, Connection con, Statement st) throws
        YssException {
        //etf行情特殊处理
        //  If YssFundType = FT_ETF And HzRs!fzqdm = YssFundCode Then
        //  深交所ETF最低成交价格用来保存市盈率2，表示交易所计算的IOPV收盘值
        //   HzRs!fzdcj = rstDbf!HQSYL2
        String strSql = "";
        double hqsyl = 0;
        try {
            strSql = "select HQSYL2 from szhq where" +
                " hqzqdm='ETF100' and fdate=" + dbl.sqlDate(date);
            ResultSet rs = st.executeQuery(strSql);
            if (rs != null) {
                while (rs.next()) {
                    hqsyl = rs.getDouble("HQSYL2");
                }
                rs.close();
                strSql = "update JjHzHq set FZdcj= " + hqsyl +
                    " where fszsh='S' and flybz='ZD' and fjjdm=' '" +
                    " and fzqdm='ETF100' and fdate=" +
                    dbl.sqlDate(date);
                st.executeUpdate(strSql);
                con.commit();
            }
        } catch (Exception ex) {
            throw new YssException("处理深圳行情库出错！", ex);
        }
    }

    private void checkTradeSub(java.util.Date date) throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select 1 from " +
                pub.yssGetTableName("tb_data_subtrade") + " where FPortCode=" +
                dbl.sqlString(sPort) + " and FBargainDate=" + dbl.sqlDate(date) +
                " and " + dbl.sqlRight("FSecurityCode", 2) +
                "='CG' ";
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
            } else {
                throw new YssException("在做上交所行情数据接口导入之前请先导入上交所的交易数据接口!");
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
