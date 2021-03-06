package com.yss.main.operdeal.datainterface.cnstock.szstock;

import com.yss.util.YssException;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import java.sql.ResultSet;
import com.yss.util.YssFun;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.yss.main.operdeal.datainterface.cnstock.CtlStock;
import java.util.HashMap;

/**
 * 深圳对账库，储存对账数据
 * 将深圳对账库对应的临时表的数据经过处理读入到两地对账库
 * created by songjie
 * 2009-06-04
 */
public class SZDZBean extends DataBase {
	//delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
    //HashMap hmPortHolderSeat = null; //用于储存组合下对应的券商代码和席位代码

    /**
     * 构造函数
     */
    public SZDZBean() {
    }

    /**
     * 用于处理深圳对账库到两地对账库的数据转换
     * @throws YssException
     */
    public void inertData() throws YssException {
        Connection con = dbl.loadConnection();//新建连接
        boolean bTrans = false;
        ResultSet rs = null;//声明结果集
        String strSql = "";//用于储存sql语句
        PreparedStatement pstmt = null;//声明PreparedStatement
        String tradeSeats = "";//席位代码
        String stockHolders = "";//股东代码
        String[] arrPortCode = null;//用于储存拆分后的已选组合代码
        String TSInfo = "";
        String[] subTSInfo = null;
        String zqdm = null;//证券代码
        try {
            strSql = " delete from " + pub.yssGetTableName("Tb_JjHzDz") + " where FInDate = " +
                dbl.sqlDate(this.sDate) + " and FPortCode in(" + operSql.sqlCodes(this.sPort) +
                ") and Fsjly = 'SZDZK' ";

            dbl.executeSql(strSql); //在两地对账库中删除相关交易日期和组合代码的数据

            CtlStock ctlStock = new CtlStock();
            ctlStock.setYssPub(pub);

            //获取储存已选组合代码对应的股东代码和席位代码的哈希表
            hmPortHolderSeat = ctlStock.getPStockHolderAndSeat(this.sPort);

            con.setAutoCommit(false);//设置手动提交事务
            bTrans = true;

            //在深圳对账表中删除相关业务日期的数据
            strSql = " delete from SZDZ where FInDate = " + dbl.sqlDate(this.sDate);
            dbl.executeSql(strSql);

            //在深圳对账中添加储存
            strSql = " insert into SZDZ(DZXWDM,DZZQDM,DZGDDM,DZZYGS,DZFSRQ,DZBYBZ,FInDate) select tmp.DZXWDM, tmp.DZZQDM," +
                " tmp.DZGDDM, tmp.DZZYGS, tmp.DZFSRQ, tmp.DZBYBZ, " + dbl.sqlDate(this.sDate) +
                " from tmp_SZDZ tmp where DZFSRQ = " + dbl.sqlDate(this.sDate);
            dbl.executeSql(strSql);

            arrPortCode = this.sPort.split(",");//拆分已选组合代码

            for (int i = 0; i < arrPortCode.length; i++) {//循环组合代码
                //获取组合代码对应的席位代码和股东代码
                TSInfo = (String) hmPortHolderSeat.get(arrPortCode[i]);
                subTSInfo = TSInfo.split("\t");//拆分席位和股东信息

                if (subTSInfo.length > 1) {
                    tradeSeats = subTSInfo[0];//获取席位代码
                    stockHolders = subTSInfo[1];//获取股东代码
                }

                //在两地对账库中插入数据
                strSql = " insert into " + pub.yssGetTableName("Tb_JjHzDz") +
                    "(Fdate,FZqdm,FSzsh,FKcsl,Fsjly,FLtlx,FXwh,FZqzh,FInDate,FPortCode)" +
                    " values(?,?,?,?,?,?,?,?,?,?)";

                pstmt = dbl.openPreparedStatement(strSql);

                //在深圳对账表中查询相关席位代码和股东代码的数据,且根据证券代码汇总总拥股数作为库存数量
                strSql = " select DZZQDM, DZXWDM, DZGDDM, DZFSRQ, FInDate, sum(DZZYGS) as DZZYGS from SZDZ  " +
                    " where (DZXWDM in (" + operSql.sqlCodes(tradeSeats) + ")" +
                    " or DZGDDM in(" + operSql.sqlCodes(stockHolders) + "))" +
                    " and FInDate = "  + dbl.sqlDate(this.sDate) +
                    " group by DZZQDM, DZXWDM, DZGDDM, DZFSRQ, FInDate";

                rs = dbl.openResultSet(strSql);

                while (rs.next()) {
                    zqdm = rs.getString("DZZQDM");//证券代码

                    //若证券代码以600 开头，则将证券代码前三位用003替代
                    if (YssFun.left(zqdm, 3).equals("003")) {
                        zqdm = "600" + YssFun.right(zqdm, 3);
                    }

                    pstmt.setDate(1, rs.getDate("DZFSRQ"));//设置业务日期
                    pstmt.setString(2, zqdm + " CS");//设置证券代码
                    pstmt.setString(3, "S");//设置交易所代码
                    pstmt.setInt(4, rs.getInt("DZZYGS"));//设置总发行量
                    pstmt.setString(5, "SZDZK");//设置数据来源

                    if(stockHolders.indexOf(rs.getString("DZGDDM")) != -1){
                        pstmt.setString(6, "N");//设置流通类型
                    }

                    else{
                        continue;
                    }

                    pstmt.setString(7, rs.getString("DZXWDM"));//设置席位代码
                    pstmt.setString(8, rs.getString("DZGDDM"));//设置股东代码
                    pstmt.setDate(9, rs.getDate("FInDate"));//设置系统读数日期
                    pstmt.setString(10, arrPortCode[i]);//设置组合代码

                    pstmt.addBatch();
                }

                pstmt.executeBatch();
            }

            con.commit();//提交事务
            bTrans = false;
            con.setAutoCommit(true);//设置自动提交事务

        } catch (Exception e) {
            throw new YssException("插入深圳对账库数据到两地对账表出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
    }
}
