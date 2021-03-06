package com.yss.main.operdeal.datainterface.cnstock.shstock;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.*;
import java.sql.*;
import java.util.*;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadTypeBean;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;
import com.yss.main.operdeal.datainterface.cnstock.CtlStock;

/**
 * QDV4.1赢时胜（上海）2009年4月20日12_A
 * 国内：MS00012
 * create by songjie
 * 2009-06-17
 * 用于将上海其他数量库的数据处理到两地对账库中
 */
public class SHQTSLBean extends DataBase {
    //--- delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
	//HashMap hmPortHolderSeat = null; //用于储存组合下对应的券商代码和席位代码
    //HashMap hmReadType = null; //用于储存数据接口参数设置的读书处理方式分页的实体类
	//--- delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
	HashMap hmPortAsset = null; //用于储存组合代码对应的资产代码


    /**
     * 构造函数
     */
    public SHQTSLBean() {

    }

    /**
     * 将上海其他数量库的数据处理到两地对账库中
     * @param date Date
     * @param portCodes String
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
        String[] arrPortCode = null;//储存已经拆分的组合代码
        String ltlx = "";//流通类型
        String TSInfo = "";
        String[] subTSInfo = null;
        ReadTypeBean readType = null;//数据接口参数设置
        String shNum = null;//上海对账库邮箱号
        String rq = null;//日期
        String zqdm = null;//证券代码
        int ye1 = 0;//总发行量
        String subCatCode = null; //品种子类型
        try {
            strSql = " delete from " + pub.yssGetTableName("Tb_JjHzDz") + " where FInDate = " +
                dbl.sqlDate(this.sDate) + " and FPortCode in(" + operSql.sqlCodes(this.sPort) +
                ") and Fsjly = 'QTSLK' ";

            dbl.executeSql(strSql); //在两地对账库中删除相关交易日期和组合代码的数据

            CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin(); //新建CNInterfaceParamAdmin
            interfaceParam.setYssPub(pub);

            //获取数据接口参数设置的读书处理方式界面设置的参数对应的HashMap
            hmReadType = (HashMap) interfaceParam.getReadTypeBean();

            CtlStock ctlStock = new CtlStock();
            ctlStock.setYssPub(pub);

            //获取储存已选组合代码对应的股东代码和席位代码的哈希表
            hmPortHolderSeat = ctlStock.getPStockHolderAndSeat(this.sPort);

            con.setAutoCommit(false);//设置手动提交事务
            bTrans = true;

            //在上海其他数量表中删除相关业务日期的数据
            strSql = " delete from SHQTSL where FInDate = " + dbl.sqlDate(this.sDate);
            dbl.executeSql(strSql);

            //在上海其他数量表中添加指定数据
            strSql = " insert into SHQTSL(SCDM,HYDM,SJLX,ZQZH,XWH,ZQDM,ZQLB,LTLX,QYLB,GPNF,SL1,SL2,BH1,BH2,FZDM,RQ,BCSM,BeiYong,FInDate)" +
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
            pstmt = dbl.openPreparedStatement(strSql);

            //查询储存上海其他数量数据的临时表
            strSql = " select SCDM, HYDM, SJLX, ZQZH, XWH, ZQDM, ZQLB, LTLX, " +
                "QYLB, GPNF, SL1, SL2, BH1, BH2, FZDM, RQ, BCSM, BeiYong from tmp_SHQTSL where RQ = " +
                dbl.sqlString(YssFun.formatDate(this.sDate,"yyyyMMdd"));
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pstmt.setString(1, rs.getString("SCDM"));//市场代码
                pstmt.setString(2, rs.getString("HYDM"));//清算编号
                pstmt.setString(3, rs.getString("SJLX"));//数据类型
                pstmt.setString(4, rs.getString("ZQZH"));//证券账户
                pstmt.setString(5, rs.getString("XWH"));//席位号
                pstmt.setString(6, rs.getString("ZQDM"));//证券代码
                pstmt.setString(7, rs.getString("ZQLB"));//证券类别
                pstmt.setString(8, rs.getString("LTLX"));//流通类型
                pstmt.setString(9, rs.getString("QYLB"));//权益类别
                pstmt.setString(10, rs.getString("GPNF"));//挂牌年份
                pstmt.setInt(11, Integer.parseInt(rs.getString("SL1")));//数量1
                pstmt.setInt(12, Integer.parseInt(rs.getString("SL2")));//数量2
                pstmt.setString(13, rs.getString("BH1"));//编号1
                pstmt.setString(14, rs.getString("BH2"));//编号2
                pstmt.setString(15, rs.getString("FZDM"));//辅助代码
                pstmt.setString(16, rs.getString("RQ"));//日期
                pstmt.setString(17, rs.getString("BCSM"));//补充说明
                pstmt.setString(18, rs.getString("BEIYONG"));//备用
                pstmt.setDate(19, YssFun.toSqlDate(this.sDate));//系统读数日期
                pstmt.addBatch();
            }

            pstmt.executeBatch();

            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
            rs = null;
            pstmt = null;

            arrPortCode = this.sPort.split(",");//拆分已选组合代码

            for (int i = 0; i < arrPortCode.length; i++) {//循环组合代码
                //获取组合代码对应的读书处理方式参数
                readType = (ReadTypeBean)hmReadType.get(pub.getAssetGroupCode() + " " + arrPortCode[i]);

                if(readType != null){
                    shNum = readType.getShNum(); //获取上海对账库邮箱号
                }

                //若邮箱号为空
                if (shNum == null) {
                    shNum = "";//则设置邮箱号为空字符串
                }

                TSInfo = (String) hmPortHolderSeat.get(arrPortCode[i]);//获取组合代码对应的席位代码和股东代码
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

                //在上海其他数量表中查询相关席位代码和股东代码的数据,且根据证券代码与流通类型汇总数量1作为库存数量
                strSql = " select sub.*, sec.FSubCatCode from (select s.ZQDM, " +
                    "s.LTLX, s.XWH, s.ZQZH, s.RQ, s.SJLX, s.HYDM, s.FInDate, sum(SL1) as SL1 from " +
                    " SHQTSL s where (XWH in (" + operSql.sqlCodes(tradeSeats) +
                    ") or ZQZH in (" + operSql.sqlCodes(stockHolders) + "))" +
                    " group by s.ZQDM, s.LTLX, s.XWH, s.ZQZH, s.RQ, s.SJLX, s.HYDM, s.FInDate) sub " +
                    " left join " + pub.yssGetTableName("Tb_Para_Security") + " sec on sub.ZQDM = sec.FSecurityCode ";
                rs = dbl.openResultSet(strSql);

                while (rs.next()) {
                    ltlx = rs.getString("LTLX");//流通类型
                    rq = rs.getString("RQ");//日期
                    rq = YssFun.left(rq, 4) + "-" + YssFun.mid(rq, 4, 2) + "-" + YssFun.right(rq, 2);//格式化之后的日期
                    zqdm = rs.getString("ZQDM");//证券代码
                    ye1 = (int) rs.getDouble("SL1");//数量1
                    subCatCode = rs.getString("FSubCatCode");//品种子类型

                    //若品种子类型为国债
                    if (subCatCode != null && subCatCode.equals("FI12")) {
                        //总发行量 = (数量1)/100
                        ye1 = (int) YssD.div(ye1, 100);
                    }

                    //若证券代码以609 开头，则将证券代码前三位用002替代
                    if (YssFun.left(zqdm, 3).equals("609")) {
                        zqdm = "002" + YssFun.right(zqdm, 3);
                    }

                    pstmt.setDate(1, YssFun.toSqlDate(rq));//设置对账日期
                    pstmt.setString(2, zqdm + " CG");//设置证券代码
                    pstmt.setString(3, "H");//设置交易所代码
                    pstmt.setInt(4, ye1);//设置总发行量
                    pstmt.setString(5, "QTSLK");//设置数据来源

                    //若ltlx =’F’ and Zqdm <> '888***' and (((sjlx ='007' or sjlx = '002')
                    //and zqzh 与组合股东代码相符 and hydm=该组合上海对帐库信箱号)或
                    //(sjlx ='003' and zqzh 为空  xwh与组合席位号相符))
                    if (ltlx.equals("F") && !YssFun.left(rs.getString("ZQDM"), 3).equals("888") &&
                        (( (rs.getString("SJLX").equals("007") || rs.getString("SJLX").equals("002")) &&
                         rs.getString("HYDM").equals(shNum)) || (rs.getString("SJLX").equals("003") &&
                                                                rs.getString("ZQZH").equals(" ")))) {
                        pstmt.setString(6, "F"); //统一其流通类型均为'F'
                    }

                    //若ltlx <> ’F’ and Zqdm <> '888***' and (((sjlx ='007' or sjlx = '002')
                    //and zqzh 与组合股东代码相符and hydm=该组合上海对帐库信箱号)或
                    //(sjlx ='003' and zqzh 为空  xwh与组合席位号相符))
                    else if (!ltlx.equals("F") && !YssFun.left(rs.getString("ZQDM"), 3).equals("888") &&
                        ( ( (rs.getString("SJLX").equals("007") || rs.getString("SJLX").equals("002")) &&
                           rs.getString("HYDM").equals(shNum)) || (rs.getString("SJLX").equals("003") &&
                        rs.getString("ZQZH").equals(" ")))) {
                        pstmt.setString(6, "N");
                    }

                    else{
                        continue;
                    }

                    pstmt.setString(7, rs.getString("XWH"));//设置席位号
                    pstmt.setString(8, rs.getString("ZQZH"));//设置股东代码
                    pstmt.setDate(9, rs.getDate("FInDate"));//设置系统读数日期
                    pstmt.setString(10, arrPortCode[i]);//设置组合代码
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
            }

            con.commit();//提交事务
            bTrans = false;
            con.setAutoCommit(true);//设置为自动提交事务
        } catch (Exception e) {
            throw new YssException("将上海其他数量库数据插入到两地对账表出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
    }
}
