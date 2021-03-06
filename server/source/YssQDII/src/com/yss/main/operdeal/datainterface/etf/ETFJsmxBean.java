package com.yss.main.operdeal.datainterface.etf;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.yss.util.YssFun;

/**
 * <p>Title: xuqiji 20091012 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A</p>
 *
 * <p>Description:EFT基金接口导入中登结算明细数据 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ETFJsmxBean
    extends DataBase {
    public ETFJsmxBean() {
    }
    /**
     * 接口导入数据的入口方法
     * @throws YssException
     */
    public void inertData() throws YssException {
        Connection con = dbl.loadConnection(); // 新建连接
        boolean bTrans = true;//事务控制标识
        ResultSet rs = null;//结果集声明
        PreparedStatement pst = null; // 声明PreparedStatement
        StringBuffer buff=null;//做拼接SQL语句
        String sSeatNum="";//席位编号
        ResultSet rst=null;
       try{
           buff = new StringBuffer();
           con.setAutoCommit(false);
           // 1.删除中登结算明细表Tb_ETF_JSMXInterface相关导入日期和组合代码的数据
           buff.append(" delete from ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface"));
           buff.append(" where FClearDate =").append(dbl.sqlDate(this.sDate));
           buff.append(" and FPortCode in(").append(operSql.sqlCodes(this.sPort)).append(")");

           dbl.executeSql(buff.toString());
           buff.delete(0, buff.length());

           // 2.查询出临时表tmp_etf_jsmx中的数据
           buff.append(" select QSRQ,JSRQ,SCDM,YWLX,QSBZ,GHLX,CJBH,XWH1,XWHY,ZQZH,ZQDM1,ZQDM2,CJSL,JG1,JG2,QSJE,SJSF,FJSM from tmp_etf_jsmx");

           rs = dbl.openResultSet(buff.toString());
           buff.delete(0, buff.length());
           // 3.向目标表Tb_ETF_JSMXInterface插入数据
            buff.append(" insert into ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface"));
            buff.append(" (FPortCode,FClearDate,FSettleDate,FMarketCode,FTradeTypeCode,FClearMark,FChangeType,FTradeNum,FSeatNum,");
            buff.append(" FClearCode,FStockholderCode,FSecurityCode1,FSecurityCode2,FTradeAmount,FSettlePrice,FTradePrice,FClearMoney,");
            buff.append(" FTotalMoney,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime").append(")");
            buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            pst=dbl.openPreparedStatement(buff.toString());
            buff.delete(0,buff.length());
            String[] arrPortCodes = this.sPort.split(","); // 拆分已选组合代码
            for (int i = 0; i < arrPortCodes.length; i++) {
                //从ETF参数设置表中获取席位编号
                buff.append(" select FETFSeat from ").append(pub.yssGetTableName("Tb_ETF_Param"));
                buff.append(" where FPortCode =").append(dbl.sqlString(arrPortCodes[i]));

                rst = dbl.openResultSet(buff.toString());
                buff.delete(0, buff.length());
                if(rst.next()){
                    sSeatNum = rst.getString("FETFSeat");
                }
                dbl.closeResultSetFinal(rst);
                while (rs.next()) {
                    if(!sSeatNum.equals(rs.getString("XWH1"))){
                        pst.setString(1, arrPortCodes[i]); //组合代码
                        pst.setDate(2, YssFun.toSqlDate(YssFun.parseDate(rs.getString("QSRQ"), "yyyyMMdd"))); //清算日期
                        pst.setDate(3, YssFun.toSqlDate(YssFun.parseDate(rs.getString("JSRQ"), "yyyyMMdd"))); //结算日期
                        pst.setString(4, rs.getString("SCDM")); //市场代码
                        pst.setString(5, rs.getString("YWLX")); //业务类型
                        pst.setString(6, rs.getString("QSBZ").trim().length() > 0 ? rs.getString("QSBZ") : " "); //清算标志
                        pst.setString(7, rs.getString("GHLX").trim().length() > 0 ? rs.getString("GHLX") : " "); //变动类型
                        pst.setString(8, rs.getString("CJBH").trim().length() > 0 ? rs.getString("CJBH") : " "); //成交编号
                        pst.setString(9, rs.getString("XWH1").trim().length() > 0 ? rs.getString("XWH1") : " "); //席位号
                        pst.setString(10, rs.getString("XWHY").trim().length() > 0 ? rs.getString("XWHY") : " "); //清算编号
                        pst.setString(11, rs.getString("ZQZH").trim().length() > 0 ? rs.getString("ZQZH") : " "); //股东代码

                        if (rs.getString("QSBZ").equals("055")) {//清算标志
                            buff.append(" select fsecuritycode from ").append(pub.yssGetTableName("tb_para_security"));
                            buff.append(" s where s.FCheckState = 1 and s.fsecuritycode=")
                                .append(dbl.sqlString(rs.getString("ZQDM1")));

                            rst = dbl.openResultSet(buff.toString());
                            buff.delete(0, buff.length());
                            if (rst.next()) {
                                pst.setString(12,rst.getString("fsecuritycode")); //证券代码1
                            } else {
                                throw new YssException("请检查系统证券信息设置中是否有股票篮中的证券信息！");
                            }
                        } else {
                            pst.setString(12, rs.getString("ZQDM1")); //证券代码1
                        }
                        dbl.closeResultSetFinal(rst);
                        pst.setString(13, rs.getString("ZQDM2").trim().length() > 0 ? rs.getString("ZQDM2") : " "); //证券代码2
                        pst.setDouble(14, Double.parseDouble(rs.getString("CJSL").trim().length() > 0 ? rs.getString("CJSL") : "0")); //成交数量
                        pst.setDouble(15, Double.parseDouble(rs.getString("JG1").trim().length() > 0 ? rs.getString("JG1") : "0")); //结算价格
                        pst.setDouble(16, Double.parseDouble(rs.getString("JG2").trim().length() > 0 ? rs.getString("JG2") : "0")); //交易成交价格
                        pst.setDouble(17, Double.parseDouble(rs.getString("QSJE").trim().length() > 0 ? rs.getString("QSJE") : "0")); //清算金额
                        pst.setDouble(18, Double.parseDouble(rs.getString("SJSF").trim().length() > 0 ? rs.getString("SJSF") : "0")); //实收实付
                        pst.setString(19, rs.getString("FJSM")); //附加说明
                        pst.setInt(20, 1); //审核状态
                        pst.setString(21, pub.getUserCode()); //创建人
                        pst.setString(22, YssFun.formatDatetime(new java.util.Date())); //创建时间
                        pst.setString(23, pub.getUserCode()); //复审人
                        pst.setString(24, YssFun.formatDatetime(new java.util.Date())); //复审时间

                        pst.addBatch();
                    }
                }
                pst.executeBatch();
            }
           con.commit();//提交事务
           bTrans=false;
           con.setAutoCommit(true);//设置为自动提交事务
       }catch(Exception e){
           throw new YssException("接口导入中登结算明细数据出错！",e);
       }finally{
           dbl.closeResultSetFinal(rs);
           dbl.endTransFinal(con,bTrans);
           dbl.closeStatementFinal(pst);
           dbl.closeResultSetFinal(rst);
       }
    }
}









