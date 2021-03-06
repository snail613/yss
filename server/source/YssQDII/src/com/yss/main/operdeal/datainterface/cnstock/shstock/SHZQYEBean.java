package com.yss.main.operdeal.datainterface.cnstock.shstock;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.*;
import java.sql.*;
import com.yss.main.operdeal.datainterface.cnstock.CtlStock;
import java.util.HashMap;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SHZQYEBean extends DataBase {
	//delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
    //HashMap hmPortHolderSeat = null; //用于储存组合下对应的券商代码和席位代码
    HashMap hmPortAsset = null; //用于储存组合代码对应的资产代码

    /**
     *  构造函数
     */
    public SHZQYEBean() {

    }

    /**
     * 将上海证券余额库中数据处理到两地对账库中
     * @param date Date
     * @param portCodes String
     * @throws YssException
     */
    public void inertData() throws YssException {
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        ResultSet rs = null; //声明结果集
        String strSql = ""; //用于储存sql语句
        PreparedStatement pstmt = null; //声明PreparedStatement
        String tradeSeats = ""; //席位代码
        String stockHolders = ""; //股东代码
        String ltlx = ""; //流通类型
        String qylb = ""; //权益类别
        String TSInfo = "";
        String[] subTSInfo = null;
        String zqrq = null;
        String zqdm = null; //证券代码
        String subCatCode = null; //品种子类型
        int ye1 = 0; //总发行量
        try {
            strSql = " delete from " + pub.yssGetTableName("Tb_JjHzDz") + " where FInDate = " +
                dbl.sqlDate(this.sDate) + " and FPortCode in(" + operSql.sqlCodes(this.sPort) +
                ") and Fsjly = 'ZQYEK' ";

            dbl.executeSql(strSql); //在两地对账库中删除相关交易日期和组合代码的数据

            CtlStock ctlStock = new CtlStock();
            ctlStock.setYssPub(pub);

            //获取储存已选组合代码对应的股东代码和席位代码的哈希表
            hmPortHolderSeat = ctlStock.getPStockHolderAndSeat(this.sPort);

            con.setAutoCommit(false); //设置手动提交事务
            bTrans = true;

            //在上海证券余额表中删除相关业务日期的数据
            strSql = " delete from SHZQYE where FInDate = " + dbl.sqlDate(this.sDate);
            dbl.executeSql(strSql);

            //在上海证券余额表中添加指定数据
            strSql = " insert into SHZQYE(SCDM,QSBH,ZQZH,XWH,ZQDM,ZQLB,LTLX,QYLB,GPNF,YE1," +
                "YE2,BEIYONG,JZRQ,FInDate)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
            pstmt = dbl.openPreparedStatement(strSql);

            //查询储存上海证券余额数据的临时表
            strSql = " select SCDM, QSBH, ZQZH, XWH, ZQDM, ZQLB, LTLX, QYLB, GPNF, YE1, " +
                "YE2, BEIYONG, JZRQ from tmp_SHZQYE where JZRQ = " +
                dbl.sqlString(YssFun.formatDate(this.sDate, "yyyyMMdd"));
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pstmt.setString(1, rs.getString("SCDM")); //市场代码
                pstmt.setString(2, rs.getString("QSBH")); //清算编号
                pstmt.setString(3, rs.getString("ZQZH")); //证券账户
                pstmt.setString(4, rs.getString("XWH")); //席位代码
                pstmt.setString(5, rs.getString("ZQDM")); //证券代码
                pstmt.setString(6, rs.getString("ZQLB")); //证券类别
                pstmt.setString(7, rs.getString("LTLX")); //流通类型
                pstmt.setString(8, rs.getString("QYLB")); //权益类别
                pstmt.setString(9, rs.getString("GPNF")); //挂牌年份
                pstmt.setInt(10, Integer.parseInt(rs.getString("YE1"))); //余额1
                pstmt.setInt(11, Integer.parseInt(rs.getString("YE2"))); //余额2
                pstmt.setString(12, rs.getString("BEIYONG")); //备用
                pstmt.setString(13, rs.getString("JZRQ")); //截止日期
                pstmt.setDate(14, YssFun.toSqlDate(this.sDate)); //系统读数日期

                pstmt.addBatch();
            }

            pstmt.executeBatch();

            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
            rs = null;
            pstmt = null;

            String[] arrPortCodes = this.sPort.split(","); //拆分已选组合代码
            for (int i = 0; i < arrPortCodes.length; i++) { //循环组合代码
                //获取组合代码对应的席位代码和股东代码
                TSInfo = (String) hmPortHolderSeat.get(arrPortCodes[i]);
                subTSInfo = TSInfo.split("\t"); //拆分席位和股东信息

                if (subTSInfo.length > 1) {
                    tradeSeats = subTSInfo[0]; //获取席位代码
                    stockHolders = subTSInfo[1]; //获取股东代码
                }

                //在两地对账库中插入数据
                strSql = " insert into " + pub.yssGetTableName("Tb_JjHzDz") +
                    "(Fdate,FZqdm,FSzsh,FKcsl,Fsjly,FLtlx,FXwh,FZqzh,FInDate,FPortCode)" +
                    " values(?,?,?,?,?,?,?,?,?,?)";
                pstmt = dbl.openPreparedStatement(strSql);

                //在上海证券余额表中查询相关席位代码和股东代码的数据,且根据证券代码与流通类型汇总余额1作为库存数量
                strSql = "select sub.*, sec.FSubCatCode from ( select s.ZQDM, s.LTLX, s.XWH, " +
                    "s.ZQZH, s.JZRQ, s.FInDate,s.QYLB, sum(Ye1) as Ye1" +
                    " from SHZQYE s " + " where (XWH in (" + operSql.sqlCodes(tradeSeats) +
                    ") or ZQZH in(" + operSql.sqlCodes(stockHolders) + "))" +
                    " group by s.ZQDM, s.LTLX, s.XWH,s.ZQZH, s.JZRQ, s.FInDate,s.QYLB) sub " +
                    " left join " + pub.yssGetTableName("Tb_Para_Security") + " sec on sub.ZQDM = sec.FSecurityCode ";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    ltlx = rs.getString("LTLX");//流通类型
                    qylb = rs.getString("QYLB");//权益类别
                    zqrq = rs.getString("JZRQ");//截止日期
                    zqdm = rs.getString("ZQDM");//证券代码
                    subCatCode = rs.getString("FSubCatCode");//品种子类型
                    ye1 = (int) rs.getDouble("Ye1");//总发行量

                    //若品种子类型为国债
                    if (subCatCode != null && subCatCode.equals("FI12")) {
                        ye1 = (int) YssD.div(ye1, 100);//总发行量 = (余额1)/100
                    }

                    //若证券代码以609 开头，则将证券代码前三位用002替代
                    if (YssFun.left(zqdm, 3).equals("609")) {
                        zqdm = "002" + YssFun.right(zqdm, 3);
                    }

                    //格式化之后的截止日期
                    zqrq = YssFun.left(zqrq, 4) + "-" + YssFun.mid(zqrq, 4, 2) + "-" + YssFun.right(zqrq, 2);

                    //若(ltlx = 'F' or ltlx = '1') and (qylb = 'DF' or qylb = 'P' or qylb = 'S' or qylb is null)
                    if ( (ltlx.equals("F") || ltlx.equals("1")) && (qylb.equals("DF") ||
                        qylb.equals("P") || qylb.equals("S") || qylb.equals(" "))) {
                        pstmt.setString(6, "F");
                    }

                    //若ltlx <> 'F'  and  (qylb is null or qylb = 'S')
                    else if (!ltlx.equals("F") && (qylb.equals(" ") || qylb.equals("S"))) {
                        pstmt.setString(6, "N");
                    }

                    else{
                        continue;
                    }

                    pstmt.setDate(1, YssFun.toSqlDate(zqrq));//设置业务日期
                    pstmt.setString(2, zqdm + " CG");//设置证券代码
                    pstmt.setString(3, "H");//设置交易所代码
                    pstmt.setInt(4, ye1);//设置总发行量
                    pstmt.setString(5, "ZQYEK");//设置数据来源
                    pstmt.setString(7, rs.getString("XWH"));//设置席位代码
                    pstmt.setString(8, rs.getString("ZQZH"));//设置证券账户
                    pstmt.setDate(9, rs.getDate("FInDate"));//设置系统读数日期
                    pstmt.setString(10, arrPortCodes[i]);//设置组合代码
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
            }

            con.commit();//提交事务
            bTrans = false;
            con.setAutoCommit(true);//设置自动提交事务
        } catch (Exception e) {
            throw new YssException("将上海证券余额库数据插入到两地对账表出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
    }
}
