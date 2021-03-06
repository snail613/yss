package com.yss.main.operdeal.datainterface.cnstock.szstock;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.yss.util.YssFun;
import java.sql.PreparedStatement;
import com.yss.util.YssD;

/**
 * 深圳行情库，储存深交所交易数据
 * 用于将深圳行情文件经过处理读入到系统的交易接口清算表中
 * created by songjie
 * 2009-06-04
 */
public class SZHQBean extends DataBase {
    /**
     * 构造函数
     */
    public SZHQBean() {
    }

    /**
     * 将深圳行情库的数据经过处理储存到行情数据表中
     * @throws YssException
     */
    public void inertData() throws YssException {
        String strSql = "";//储存sql语句
        ResultSet rs = null;//声明结果集
        String tradeDate = "";//交易日期
        try {
            //在深圳行情库中查找证券代码为000000的数据中对应的储存行情日期的字段数据
            strSql = "select HQZQJC from tmp_sjshq where HQZQDM =" + dbl.sqlString("000000");

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                tradeDate = rs.getString("HQZQJC");//行情日期
            }

            dbl.closeResultSetFinal(rs);//关闭结果集
            rs = null;

            if(!tradeDate.equals(YssFun.formatDate(this.sDate, "yyyyMMdd"))){
            	//edit by songjie 2012.04.01 STORY #2437 QDV4华安基金2012年3月26日02_A 若数据日期 和 界面所选日期不一致 则提示重新选择
            	throw new YssException("界面所选日期与接口文件日期不一致，请重新选择！");
            }

            //行情日期数据长度不为零 且 数据不等于'null'
            if (tradeDate.trim().length() != 0 && tradeDate != "null") {
                if (YssFun.isDate(tradeDate)) {//若行情日期数据为标准为日期格式数据
                    makeData(YssFun.toDate(formatDate(tradeDate)));//处理深圳行情库中的数据
                } else {
                    tradeDate = YssFun.left(tradeDate, 4) +
                        "-" + YssFun.mid(tradeDate, 4, 2) + "-" +
                        YssFun.right(tradeDate, 2);//将行情日期数据转换成标准的日期格式数据
                    makeData(YssFun.toDate(tradeDate));//处理深圳行情库中的数据
                }
            }
        } catch (Exception e) {
            throw new YssException("获取数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 用于处理深圳行情库到行情数据表的数据转换
     * @throws YssException
     */
    public void makeData(java.util.Date date) throws YssException {
        Connection conn = dbl.loadConnection();//新建连接
        PreparedStatement stm = null;//声明PreparedStatement
        boolean bTrans = false;
        ResultSet rs = null;//声明结果集
        String strSql = "";//储存sql语句

        try {
            conn.setAutoCommit(false);//设置手动提交事务
            bTrans = true;
            
            //将查询出的数据插入到行情数据表中
            strSql = " insert into " + pub.yssGetTableName("Tb_Data_MarketValue") + " (FMktSrcCode,FSecurityCode,FMktValueDate," +
                "FMktValueTime,FPortCode,FBargainAmount,FBargainMoney,FYClosePrice,FOpenPrice,FTopPrice,FLowPrice,FClosingPrice," +
                "FAveragePrice,FNewPrice,FMktPrice1,FMktPrice2,FDesc,FDataSource,FCheckState,FCreator, FCreateTime,FCheckUser,FCheckTime)" +
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            stm = dbl.openPreparedStatement(strSql);

            //在深圳行情库中查询证券代码不为000000 且 成交金额不为零 且成交数量不为零 且 证券代码不以2打头 且 证券代码不以3打头的数据
            strSql = "select * from tmp_sjshq where HQZQDM <>'000000' and hqcjje <> 0" +
                //edit by sogngjie 2012.11.16 BUG 6341 QDV4嘉实2012年11月15日01_B 不过滤以3打头的证券代码对应的行情数据
                " and hqcjsl <> 0 and hqzqdm not like '2%' and isDel = 'False'" + 
                //add by yanghaiming 20100421 B股业务
                " union select * from tmp_sjshq where HQZQDM <>'000000' and hqcjje <> 0" + //在不影响国内业务的情况下增加深圳B股行情数据的读取
                " and hqcjsl <> 0 and hqzqdm like '20%' and isDel = 'False'";//MS01307 QDII_B股中行2010年06月13日001_B 添加删除字段的过滤

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
            	//add by zhouwei 20120220 行情来源改为YSS_JYS start------------
            	//在行情数据表中删除交易所代码为'CS' 且数据来源为1 且 行情日期为深圳行情库的数据
                strSql = "delete from " + pub.yssGetTableName("Tb_Data_MarketValue") + 
                       " where FMktSrcCode='YSS_JYS' and fdatasource = 1 and FMktValueDate=" + dbl.sqlDate(date)
                      +" and FSecurityCode="+dbl.sqlString(rs.getString("HQZQDM") + " CS");
                dbl.executeSql(strSql);
                stm.setString(1, "YSS_JYS");//交易所代码
              //add by zhouwei 20120220 行情来源改为YSS_JYS end------------
                stm.setString(2, rs.getString("HQZQDM") + " CS");//证券代码
                stm.setDate(3, YssFun.toSqlDate(date));//行情日期
                stm.setString(4, "00:00:00");//行情时间
                stm.setString(5, " ");//组合代码
                stm.setDouble(6, YssFun.roundIt(rs.getDouble("HQCJSL"), 2));//交易数量
                stm.setDouble(7, YssFun.roundIt(rs.getDouble("HQCJJE"), 2));//交易金额
                stm.setDouble(8, YssFun.roundIt(rs.getDouble("HQZRSP"), 2));//昨日收盘价
                stm.setDouble(9, YssFun.roundIt(rs.getDouble("HQJRKP"), 2));//开盘价
                stm.setDouble(10, YssFun.roundIt(rs.getDouble("HQZGCJ"), 2));//最高价

                if (YssFun.left(rs.getString("HQZQDM"), 2).equals("18") ||
                    YssFun.left(rs.getString("HQZQDM"), 2).equals("15")) {//若证券代码以 18 或 15 打头 的ETF基金
                    //最低成交价=市盈率2（直接从Sz_sjshq中取市盈率2的字段），并且保留2位小数
                    stm.setDouble(11, YssFun.roundIt(rs.getDouble("HQSYL2"), 2));
                } else {
                    //最低成交价从Sz_sjshq中取最低成交价,并且保留2位小数
                    stm.setDouble(11, YssFun.roundIt(rs.getDouble("HQZDCJ"), 2));
                }

                //如果最近成交价格(HQZJCJ)不等于0
                if (rs.getDouble("HQZJCJ") != 0) {
                    //收盘价=最近成交价，并且保留4位小数
                    stm.setDouble(12, YssFun.roundIt(rs.getDouble("HQZJCJ"), 4));
                } else {
                    //如果证券代码是以16开头的，并且平均价>50,并且最近成交价格=0
                    if (YssFun.left(rs.getString("HQZQDM"), 2).equals("16") &&
                        YssFun.roundIt(YssD.div(rs.getDouble("HQCJJE"), rs.getDouble("HQCJSL")), 3) > 50) {
                        //收盘价=HQZRSP(昨日收盘价)/100，并且保留4位小数
                        stm.setDouble(12, YssFun.roundIt(YssD.div(rs.getDouble("HQZRSP"), 100), 4));
                    }
                    else{
                        stm.setDouble(12, 0); //收盘价
                    }
                }

                //如果证券代码是以1开头，并且不是以18、16、15开头的证券
                if (YssFun.left(rs.getString("HQZQDM"), 1).equals("1") &&
                    !YssFun.left(rs.getString("HQZQDM"), 2).equals("18") &&
                    !YssFun.left(rs.getString("HQZQDM"), 2).equals("16") &&
                    !YssFun.left(rs.getString("HQZQDM"), 2).equals("15")) {
                    //平均价=roundIt（hqcjje（成交金额）/ (hqcjsl (成交数量)* 深交所债券单位)，2）（深交所债券单位指每手交易所对应的张数，这个值固定写死为10）
                    stm.setDouble(13, YssFun.roundIt(YssD.div(rs.getDouble("HQCJJE"), rs.getDouble("HQCJSL") * 10), 2));
                }
                //如果证券代码是以18或者16或者15或者03开头
                else if (YssFun.left(rs.getString("HQZQDM"), 2).equals("18") ||
                           YssFun.left(rs.getString("HQZQDM"), 2).equals("16") ||
                           YssFun.left(rs.getString("HQZQDM"), 2).equals("15") ||
                           YssFun.left(rs.getString("HQZQDM"), 2).equals("03")) {
                    //如果证券代码是以16开头的，并且平均价>50,并且最近成交价格=0
                    if (YssFun.left(rs.getString("HQZQDM"), 2).equals("16") &&
                        YssFun.roundIt(YssD.div(rs.getDouble("HQCJJE"), rs.getDouble("HQCJSL")), 3) > 50 &&
                        rs.getDouble("HQZJCJ") == 0) {
                        //平均价=hqzrsp(昨日收盘价)/100，并且保留4位小数
                        stm.setDouble(13, YssFun.roundIt(YssD.div(rs.getDouble("HQZRSP"), 100), 4));
                    } else {
                        //平均价= roundIt（hqcjje（成交金额）/hqcjsl(成交数量)，3）
                        stm.setDouble(13, YssFun.roundIt(YssD.div(rs.getDouble("HQCJJE"), rs.getDouble("HQCJSL")), 3));
                    }
                } else {
                    //平均价= hqcjje（成交金额）/hqcjsl(成交数量),并且小数位数保留2位
                    stm.setDouble(13, YssFun.roundIt(YssD.div(rs.getDouble("HQCJJE"), rs.getDouble("HQCJSL")), 2));
                }

                stm.setDouble(14, YssFun.roundIt(rs.getDouble("HQZJCJ"), 2));//最新价
                stm.setDouble(15, 0);//行情备用1
                stm.setDouble(16, 0);//行情备用2
                stm.setString(17, " ");//描述
                stm.setInt(18, 1);//行情来源
                stm.setInt(19, 1);//审核状态
                //delete by songjie 209.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
                stm.setString(20, dbl.sqlString(pub.getUserCode()));//创建人
                stm.setString(21, dbl.sqlString(YssFun.formatDate(pub.getUserDate(), "yyyy-MM-dd")));//创建时间
                //delete by songjie 209.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
                //add by songjie 209.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
                stm.setString(20, pub.getUserCode());//创建人
                stm.setString(21, YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd HH:mm:ss"));//创建时间
                //add by songjie 209.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
                stm.setString(22, pub.getUserCode());//审核人
                stm.setString(23, YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd HH:mm:ss"));//审核时间
                stm.addBatch();
            }

            stm.executeBatch();

            conn.commit();//提交事务
            bTrans = false;
            conn.setAutoCommit(true);//设置为自动提交事务
        } catch (Exception e) {
            throw new YssException("处理深圳行情库出错！", e);
        } finally {
            dbl.closeStatementFinal(stm); //sunkey@Modify
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 格式化yyyyMMdd的日期格式，返回 yyyy-MM-dd
     * @param tradeDate String
     * @return String
     * @throws YssException
     */
    private String formatDate(String tradeDate) throws YssException {
        return YssFun.left(tradeDate, 4) + "-" + YssFun.mid(tradeDate, 4, 2) + "-" + YssFun.right(tradeDate, 2);
    }
}
