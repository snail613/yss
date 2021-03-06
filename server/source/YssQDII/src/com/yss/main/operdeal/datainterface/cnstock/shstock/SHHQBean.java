package com.yss.main.operdeal.datainterface.cnstock.shstock;

import com.yss.util.YssException;
import com.yss.util.YssFun;
import java.sql.ResultSet;
import java.util.Date;
import com.yss.dsub.*;
import java.sql.*;
import com.yss.util.*;
import com.yss.main.operdeal.datainterface.pretfun.*;
import java.util.*;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadTypeBean;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;

/**
 * 上海行情库储存的是上交所的行情数据
 * 将上海行情库文件的数据经过处理分别读入到公共行情表和指数信息表
 * created by songjie
 * 2009-06-04
 */
public class SHHQBean
    extends DataBase {
    //delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
	//HashMap hmReadType = null; //用于储存数据接口参数设置界面的读数处理方式分页的各种参数 key--组合群代码,组合代码

    /**
     * 构造函数
     */
    public SHHQBean() {
    }

    /**
     *用于处理国内接口的数据
     */
    public void inertData() throws YssException {
        String strSql = "";//储存sql语句
        ResultSet rs = null;//声明结果集
        String tradeDate = "";//交易日期
        String sTime = "";//交易时间
        try {
            //新建CNInterfaceParamAdmin
            CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin();
            interfaceParam.setYssPub(pub);

            //获取数据接口参数设置的读书处理方式界面设置的参数对应的HashMap
            hmReadType = (HashMap) interfaceParam.getReadTypeBean();

            //查询S1字段为000000的特殊数据中的交易日期和交易时间
            strSql = "select S6,S2 from sh_show2003 where S1 = '000000'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                tradeDate = rs.getString("S6");//获取交易日期
                sTime = rs.getString("S2");//获取交易时间
            }

            if(!tradeDate.equals(YssFun.formatDate(this.sDate, "yyyyMMdd"))){
            	//edit by songjie 2012.04.01 STORY #2437 QDV4华安基金2012年3月26日02_A 若数据日期 和 界面所选日期不一致 则提示重新选择
            	throw new YssException("界面所选日期与接口文件日期不一致，请重新选择！");
            }

            //转换交易时间格式
            sTime = YssFun.left(tradeDate, 2) + ":" + YssFun.mid(tradeDate, 2, 2) + ":" + YssFun.right(tradeDate, 2);

            //关闭结果集
            dbl.closeResultSetFinal(rs);

            //若交易日期数据长度不为零且不等于'null'
            if (tradeDate.trim().length() != 0 && tradeDate != "null") {
                if (YssFun.isDate(tradeDate)) {//且交易日期字段属于日期格式
                    makeData(YssFun.toDate(tradeDate), sTime);//处理上海行情库数据
                } else {
                    tradeDate = YssFun.left(tradeDate, 4) +
                        "-" + YssFun.mid(tradeDate, 4, 2) + "-" +
                        YssFun.right(tradeDate, 2);//转换交易日期数据的日期格式为指定格式
                    makeData(YssFun.toDate(tradeDate), sTime);//处理上海行情库数据
                }
            }
        } catch (Exception e) {
            throw new YssException("获取数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 用于处理上海行情库到行情数据表和的数据转换
     * @throws YssException
     */
    public void makeData(Date date, String time) throws YssException {
        try {
            dealInfoToHQ(date, time); //临时表的数据经过筛选和处理读入到行情数据表中
            dealInfoToZS(date, time); //临时表的数据经过筛选和处理读入到指数信息表中
        } catch (Exception e) {
            throw new YssException("获取上海行情数据出错", e);
        } finally {
        }
    }

    /**
     * 临时表的数据经过筛选和处理读入到行情数据表中
     * @throws YssException
     */
    public void dealInfoToHQ(Date date, String time) throws YssException {
        Connection con = dbl.loadConnection();//新建连接
        boolean bTrans = false;//用于判断事务是否开启
        ResultSet rs = null;//声明结果集
        String strSql = "";//用于储存sql语句
        String strSqlD = "";//add by songjie 2012.09.05 BUG 5493 QDV4嘉实2012年09月04日01_B
        PreparedStatement pstmt = null;//声明PreparedStatement
        ReadTypeBean readType = null;//声明数据接口参数设置的读书处理方式分页的参数
        List alParameters = null;//声明数据接口参数设置的读书处理方式的参数列表
        try {
            con.setAutoCommit(false);//设置手动提事务
            bTrans = true;

            strSql = "insert into " + pub.yssGetTableName("Tb_Data_MarketValue") + 
                "(FMktSrcCode,FSecurityCode,FMktValueDate,FMktValueTime,FPortCode,FBargainAmount,FBargainMoney," +
                "FYClosePrice,FOpenPrice,FTopPrice,FLowPrice,FClosingPrice,FAveragePrice,FNewPrice," +
                "FMktPrice1,FMktPrice2,FDesc,FDataSource,FCheckState,FCreator, FCreateTime, FisDel,FCheckUser,FCheckTime)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
            pstmt = dbl.openPreparedStatement(strSql);//将指定数据插入到行情表中

            String[] portCodes = this.sPort.split(",");//拆分已选组合代码

            //循环已选组合
            for (int i = 0; i < portCodes.length; i++) {
                         	
                //获取数据接口参数设置的读书处理方式分页的参数设置
                readType = (ReadTypeBean) hmReadType.get(pub.getAssetGroupCode() + " " + portCodes[i]);

                if(readType == null){
                    alParameters = new ArrayList();
                }
                else{
                    //获取读书处理方式分页的参数
                    alParameters = readType.getParameters();
                }

                //若参数不为空,同时参数包括为根据持仓获取行情
                if (alParameters != null && alParameters.contains("01")) {
                    //在上海行情库中查询证券库存中包含，且不属于指数行情的数据
                    strSql = " select * from sh_show2003 where S1 || ' CG' " +
                        "in(select distinct FSecurityCode from " +
                        pub.yssGetTableName("Tb_Stock_Security") +
                        " union select distinct FSecurityCode from " +
                        pub.yssGetTableName("tb_data_subtrade") +
                        //----------add by yanghaiming 20091221 MS00874 QDV4赢时胜（上海）2009年12月17日02_B
                        " union select distinct FSecurityCode from " +
                        pub.yssGetTableName("tb_data_integrated") +
                        " where fnumtype = ' ' and FRelaNum = ' ' and FPortCode = '" +
                        portCodes[i] +
                        "' and FEXCHANGEDATE = " + dbl.sqlDate(date) +
                        //-----------------------------------------------------------------
                        //edit by songjie 2012.09.05 BUG 5493 QDV4嘉实2012年09月04日01_B 添加 s5 <> 0 and s11 <> 0 成交数量、成交金额 不为零 的条件
                        ") and s5 <> 0 and s11 <> 0 and (S1 like '6%' or S1 like '0%' or S1 like '10%' " +
                        "or S1 like '11%' or S1 like '12%' or S1 like '5%' or S1 like '900%') " +//900开头的为上海B股 add by yanghaiming 20100420
                        "  and s1>'000017' and s1 <> '000300' and s1 <> '000901' " +
                        "and s1 <> '000902' and s1 <> '000903' and s1 <> '000904' " +
                        " and s1 <> '000905' and s1 <> '000906' and s1 <> '000907' " +
                        //add by songjie 2012.09.05 BUG 5493 QDV4嘉实2012年09月04日01_B 删除 (s9 <> 0 or s10 <> 0 即 买入价格、卖出价格 不为零的条件
                        "and s8 <> 0 and isDel = 'False' ";
                        //delete by songjie 2012.09.05 BUG 5493 QDV4嘉实2012年09月04日01_B 删除 (s9 <> 0 or s10 <> 0 即 买入价格、卖出价格 不为零的条件
                        //"and s8 <> 0 and (s9 <> 0 or s10 <> 0) and isDel = 'False' ";
                } else {
                    //在上海行情库中查询部署去指数行情的数据
                	//edit by songjie 2012.09.05 BUG 5493 QDV4嘉实2012年09月04日01_B 添加 s5 <> 0 and s11 <> 0 成交数量、成交金额 不为零 的条件
                    strSql = " select * from sh_show2003 where s5 <> 0 and s11 <> 0 and (S1 like '6%' or S1 like '0%' or S1 like '10%' or " +
                        " S1 like '11%' or S1 like '12%' or S1 like '5%' or S1 like '900%') and s1>'000017' and s1 <> '000300' and " +//900开头的为上海B股 add by yanghaiming 20100420
                        " s1 <> '000901' and s1 <> '000902' and s1 <> '000903' and s1 <> '000904' and s1 <> '000905' " +
                        //delete by songjie 2012.09.05 BUG 5493 QDV4嘉实2012年09月04日01_B 删除 (s9 <> 0 or s10 <> 0 即 买入价格、卖出价格 不为零的条件
                        //" and s1 <> '000906' and s1 <> '000907' and s8 <> 0 and (s9 <> 0 or s10 <> 0 ) and isDel = 'False' ";
                        //add by songjie 2012.09.05 BUG 5493 QDV4嘉实2012年09月04日01_B 删除 (s9 <> 0 or s10 <> 0 即 买入价格、卖出价格 不为零的条件
                        " and s1 <> '000906' and s1 <> '000907' and s8 <> 0  and isDel = 'False' ";
                }
                
            	//add by zhouwei 20120220 行情来源改为YSS_JYS start----------
            	strSqlD = "delete from " + pub.yssGetTableName("Tb_Data_MarketValue") + 
            	" where FMktSrcCode='YSS_JYS' and fdatasource = 1 and FMktValueDate = " + 
            	dbl.sqlDate(date) +" and FSecurityCode in (select S1 || ' CG' from (" + 
            	strSql + "))";
            	
                dbl.executeSql(strSqlD);//删除行情表中交易所代码为'CG'的相关行情日期的数据
                //add by zhouwei 20120220 行情来源改为YSS_JYS end----------

                rs = dbl.openResultSet(strSql);

                while (rs.next()) {
                	//edit by zhouwei 20120220 行情来源改为YSS_JYS
                    pstmt.setString(1, "YSS_JYS");//交易所代码
                    pstmt.setString(2, rs.getString("S1") + " CG");//证券代码
                    pstmt.setDate(3, YssFun.toSqlDate(date));//交易日期
                    pstmt.setString(4, time);//交易时间
                    pstmt.setString(5, " ");//组合代码
                    pstmt.setDouble(6, rs.getDouble("S11"));//交易数量
                    pstmt.setDouble(7, rs.getDouble("S5"));//交易金额
                    pstmt.setDouble(8, rs.getDouble("S3"));//昨日收盘价
                    pstmt.setDouble(9, rs.getDouble("S4"));//开盘价
                    pstmt.setDouble(10, rs.getDouble("S6"));//最高价
                    pstmt.setDouble(11, rs.getDouble("S7"));//最低价
                    pstmt.setDouble(12, rs.getDouble("S8"));//今日收盘价

                    if (rs.getDouble("S11") != 0) {
                        if (YssFun.left(rs.getString("S1"), 2).equals("10") ||
                            YssFun.left(rs.getString("S1"), 2).equals("12") ||
                            YssFun.left(rs.getString("S1"), 1).equals("0")) {
                            pstmt.setDouble(13, YssFun.roundIt(YssD.div(rs.getDouble("S5"), rs.getDouble("S11") * 10), 2)); //平均价
                        } else if (YssFun.left(rs.getString("S1"), 1).equals("5")) {
                            if (YssFun.left(rs.getString("S1"), 2).equals("58") && date.after(YssFun.toDate("2006-03-02"))) {
                                pstmt.setDouble(13, YssFun.roundIt(YssD.div(rs.getDouble("S5"), rs.getDouble("S11") * 100), 3)); //平均价
                            } else {
                                pstmt.setDouble(13, YssFun.roundIt(YssD.div(rs.getDouble("S5"), rs.getDouble("S11")), 3)); //平均价
                            }
                        }else if (YssFun.left(rs.getString("S1"), 3).equals("900")) {
                        	pstmt.setDouble(13, YssFun.roundIt(YssD.div(rs.getDouble("S5"), rs.getDouble("S11")), 3)); //上海B股平均价  add by yanghaiming 20100420
                        }else {
                            pstmt.setDouble(13, YssFun.roundIt(YssD.div(rs.getDouble("S5"), rs.getDouble("S11")), 2)); //平均价
                        }
                    } else {
                        pstmt.setDouble(13, rs.getDouble("S5")); //平均价
                    }
                    pstmt.setDouble(14, rs.getDouble("S8"));//最新价
                    pstmt.setDouble(15, 0);//行情备用1
                    pstmt.setDouble(16, 0);//行情备用2
                    pstmt.setString(17, " ");//行情状态
                    pstmt.setInt(18, 1);//描述
                    pstmt.setInt(19, 1);//数据来源
                    //delete by songjie 2009.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
//                    pstmt.setString(20, dbl.sqlString(pub.getUserCode()));//用户代码
//                    pstmt.setString(21, dbl.sqlString(YssFun.formatDate(pub.getUserDate(), "yyyy-MM-dd")));//创建时间
                    //delete by songjie 2009.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
                    //add by songjie 2009.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
                    pstmt.setString(20, pub.getUserCode());//用户代码
                    //edit by songjie 2011.12.08 BUG 3338 QDV4农业银行2011年12月07日01_B 由pub.getUserDate()改为new java.util.Date()
                    pstmt.setString(21, YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd HH:mm:ss"));//创建时间
                    //add by songjie 2009.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
                    pstmt.setString(22, rs.getString("isDel"));//删除标志
                    pstmt.setString(23, pub.getUserCode());//审核人
                    pstmt.setString(24, YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd HH:mm:ss"));//审核时间
                    pstmt.execute();
//                    pstmt.addBatch();
                }

                dbl.closeResultSetFinal(rs);//关闭结果集
//                pstmt.executeBatch();
            }

            con.commit();//提交事务
            bTrans = false;
            con.setAutoCommit(true);//设置自动提交事务
        } catch (Exception e) {
            throw new YssException("插入上海行情数据到行情数据表出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
            dbl.closeStatementFinal(pstmt);
        }
    }

    /**
     * 临时表的数据经过筛选和处理读入到指数信息表中
     * @throws YssException
     */
    public void dealInfoToZS(Date date, String time) throws YssException {
        Connection con = dbl.loadConnection();//新建连接
        boolean bTrans = false;
        ResultSet rs = null;//声明结果集
        String strSql = "";//声明sql语句
        PreparedStatement pstmt = null;//声明PreparedStatement
        try {
            con.setAutoCommit(false);//设置手动提交事务
            bTrans = true;

            //将已选数据插入到指数信息设置表中
            strSql = " insert into " + pub.yssGetTableName("Tb_Para_Index") + "(FIndexCode,FIndexName,FExchangeCode,FCheckState,FCreator,FCreateTime)values(?,?,?,?,?,?) ";
            pstmt = dbl.openPreparedStatement(strSql);

            //从上海行情库中查询最低价大于零且最新价大于零且指数代码前四位为0000或指数代码为000300且指数信息设置表中不包含的数据
            strSql = " select distinct S1, S2 from sh_show2003 where S7 > 0 and S8 > 0 and (S1 like '0000%' or S1 = '000300') and S1 || ' CG' not in " +
                "(select FIndexCode from " + pub.yssGetTableName("Tb_Para_Index") + ")";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pstmt.setString(1, rs.getString("S1") + " CG");//指数代码
                pstmt.setString(2, rs.getString("S2"));//指数名称
                pstmt.setString(3, "CG");//交易所代码
                pstmt.setInt(4, 1);//审核状态
                //delete by songjie 2009.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
//                pstmt.setString(5, dbl.sqlString(pub.getUserCode()));//用户代码
//                pstmt.setString(6, dbl.sqlString(YssFun.formatDate(pub.getUserDate(), "yyyy-MM-dd")));//创建时间
                //delete by songjie 2009.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
                //add by songjie 2009.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉
                pstmt.setString(5, pub.getUserCode());//用户代码
                //edit by songjie 2011.12.08 BUG 3338 QDV4农业银行2011年12月07日01_B 由pub.getUserDate()改为new java.util.Date()
                pstmt.setString(6, YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd"));//创建时间
                //add by songjie 2009.12.21 QDII国内：MS00858 QDV4赢时胜(上海)2009年12月09日07_B 将创建人 和 创建时间的 引号去掉

                pstmt.addBatch();
            }
            pstmt.executeBatch();

            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
            pstmt = null;
            rs = null;

            //在指数行情表中删除行情日期为交易日期，且属于上海行情库中的证券数据
            strSql = " delete from " + pub.yssGetTableName("Tb_Data_Index") + 
            " where FIndexCode in(select S1 from sh_show2003) and FDate = " + dbl.sqlDate(date);
            dbl.executeSql(strSql);

            //将已选的数据插入到指数行情表中
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_Index") + "(FIndexCode,FIndexSource,FDate,FTime,FTopValue," +
                "FLowValue,FOpenValue,FNewValue,FDataSource,FCheckState,FCreator,FCreateTime)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?) ";
            pstmt = dbl.openPreparedStatement(strSql);

            //在上海行情表中查询最低价大于零，最新价大于零，且证券代码以0000打头的或证券代码为000300的数据
            strSql = " select * from sh_show2003 where S7 > 0 and S8 > 0 and (S1 like '0000%' or S1 = '000300') " +
                " and S1 || ' CG' not in(select distinct FIndexCode from " + pub.yssGetTableName("Tb_Data_Index") + 
                " where FIndexSource = 'CG' and FDate = " +
                dbl.sqlDate(date) +  " and FTime = " + dbl.sqlString(time) + ")";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pstmt.setString(1, rs.getString("S1") + " CG");//指数代码
                pstmt.setString(2, "CG");//指数来源
                pstmt.setDate(3, YssFun.toSqlDate(date));//指数日期
                pstmt.setString(4, time);//指数时间
                pstmt.setDouble(5, rs.getDouble("S6"));//最高价
                pstmt.setDouble(6, rs.getDouble("S7"));//最低价
                pstmt.setDouble(7, rs.getDouble("S4"));//开盘价
                pstmt.setDouble(8, rs.getDouble("S8"));//最新价
                pstmt.setInt(9, 1);//数据来源
                pstmt.setInt(10, 1);//审核状态
                pstmt.setString(11, pub.getUserCode());//创建人
                //edit by songjie 2011.12.08 BUG 3338 QDV4农业银行2011年12月07日01_B 由pub.getUserDate()改为new java.util.Date()
                pstmt.setString(12, YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd"));//创建时间
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            con.commit();//提交事务
            bTrans = false;
            con.setAutoCommit(true);//设置为自动提交事务
        } catch (Exception e) {
            throw new YssException("插入上海行情数据到指数信息表出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
    }
}
