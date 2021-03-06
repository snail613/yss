package com.yss.imp;

import com.yss.util.YssException;
import com.yss.dsub.YssPub;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssCons;
import com.yss.util.YssFun;
import java.sql.*;

import com.yss.main.parasetting.FeeBean;
import com.yss.util.YssD;
import com.yss.util.YssOperCons;
import com.yss.commeach.EachRateOper; //增加取汇率的方法 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427

public class ImpBloomberg
    extends BaseDataSettingBean {
    private String sPortCode = ""; //增加组合代码 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
    EachRateOper eachOper = null; //增加取汇率的方法 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
    public ImpBloomberg() {
    }

    public void saveBloombergData(String strValues) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        PreparedStatement ps = null;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        if (!dbl.yssTableExist("tb_bloomberg_data")) {
            try {
                dbl.executeSql("create table tb_bloomberg_data ("
                               + "Fdate Date not null,"
                               + "Security varchar2(64),"
                               + "Side varchar2(4) not null,"
                               + "Amount decimal(18,2),"
                               + "Preice decimal(18,12),"
                               + "Yield varchar2(16),"
                               + "SettlementDate Date not null,"
                               + "Commission decimal(18,2),"
                               + "TicketNum varchar2(8),"
                               + "TicketType varchar(8),"
                               + "Tradedate  date not null,"
                               + "ISIN varchar2(64) not null,"
                               + "TCshorName varchar2(64),"
                               + "BrokerID varchar2(64) not null,"
                               + "checkout varchar2(4),"
                               + "FNum int not null,"
                               + "primary key (FDate,ISIN,Side,SettlementDate,Tradedate,brokerID,FNum))");
            } catch (Exception e) {
                throw new YssException("保存澎博接口数据失败！", e);
            }
        }

        try {
            int intNum = 1;
            String[] strSplitRow = strValues.split("\f\f\f"); //通过此分隔符分隔数据 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
            sPortCode = strSplitRow[0]; //取第一条分隔符后的数据得到前台传组合代码 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
            //String[] strRows = strValues.split("\r\n");
            String[] strRows = strSplitRow[1].split("\r\n"); //这里取第二条分隔符后的数据 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
            Date dateTrade = YssFun.toSqlDate(strRows[0].split(" ")[0]);
            String[] strField = strRows[1].split("\t");
            String strSql = "insert into tb_bloomberg_data (Fdate,Security , Side , Amount , Preice , Yield , SettlementDate , Commission , TicketNum , TicketType , Tradedate  , ISIN , TCshorName , BrokerID , checkout,FNum) "
                + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            ps = dbl.openPreparedStatement(strSql);
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql("delete from tb_bloomberg_data where Fdate = " +
                           dbl.sqlDate(dateTrade));
            for (int i = 2; i < strRows.length; i++) {
                String[] strCols = strRows[i].split("\t", -1);
                if (strCols.length < 14) {
                    throw new YssException("保存澎博接口数据失败！\r\n数据格式不正确，请检查第" + i + "行。");
                }
                if (strCols[0].equalsIgnoreCase("CASH")) {
                    continue;
                }
                String strValue;
                ps.setDate(1, dateTrade);
//            ps.setString(2, strCols[0]);
                ps.setString(2, strCols[0].split(" ")[0]);
                ps.setString(3, strCols[1]);
                //澎博中1个M字符代表"000"
                strValue = strCols[2].toUpperCase().replaceAll("M", "000");
                ps.setDouble(4, YssFun.toDouble(strValue));
                ps.setDouble(5, YssFun.toDouble(strCols[3]));
                ps.setString(6, strCols[4]);
                //系统的日期格式为"yyyy-MM-dd",导入的澎博接口为"20MM-dd-yy" , 需要转换
//            String[] aryDate = YssFun.right(strCols[5],strCols[5].length() - 2).split("-");
                String[] aryDate = strCols[5].split("/");
                strValue = YssFun.formatNumber(YssFun.toInt(aryDate[2]), "2000") + "-" + aryDate[0] + "-" + aryDate[1];
                ps.setDate(7, YssFun.toSqlDate(strValue));
                ps.setDouble(8, YssFun.toDouble(strCols[6]));
                ps.setString(9, strCols[7]);
                ps.setString(10, strCols[8]);
                //系统的日期格式为"yyyy-MM-dd",导入的澎博接口为"20MM-dd-yy" , 需要转换
                //           aryDate = YssFun.right(strCols[9],strCols[9].length() - 2).split("-");
                aryDate = strCols[9].split("/");
                strValue = YssFun.formatNumber(YssFun.toInt(aryDate[2]), "2000") + "-" + aryDate[0] + "-" + aryDate[1];
                if (YssFun.dateDiff(dateTrade, YssFun.toDate(strValue)) != 0) {
                    throw new YssException("保存澎博接口数据失败！\r\n交易日期不正确，存在有非【" + strRows[0].split(" ")[0] + "】的日期。");
                }
                ps.setDate(11, YssFun.toSqlDate(strValue));
                ps.setString(12, strCols[10]);
                ps.setString(13, strCols[11]);
                ps.setString(14, strCols[12]);
                ps.setString(15, strCols[13]);
                ps.setInt(16, intNum);
                ps.addBatch();
                intNum++;
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
            changeToTradeData(dateTrade);
        } catch (BatchUpdateException bue) {
            throw new YssException("保存澎博接口数据失败！", bue);
        } catch (Exception e) {
            throw new YssException("保存澎博接口数据失败！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeStatementFinal(ps);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
    }

    public void changeToTradeData(Date dateTrade) throws YssException {
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        PreparedStatement psSub = null;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            int intCount = 0;
            //
            String strNowDate = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");

            if (!dbl.yssTableExist(pub.yssGetTableName("Tb_Data_SubTrade_blm"))) {
                try {
                    dbl.executeSql("CREATE Table " + pub.yssGetTableName("Tb_Data_SubTrade_blm") + "("
                                   + "FNum                VARCHAR2(20)      NOT NULL,"
                                   + "FSecurityCode       VARCHAR2(20)      NOT NULL,"
                                   + "FPortCode           VARCHAR2(20),"
                                   + "FBrokerCode         VARCHAR2(20)      NOT NULL,"
                                   + "FInvMgrCode         VARCHAR2(20)      NOT NULL,"
                                   + "FTradeTypeCode      VARCHAR2(20)      NOT NULL,"
                                   + "FCashAccCode        VARCHAR2(20)      NOT NULL,"
                                   + "FAttrClsCode        VARCHAR2(20),"
                                   + "FBargainDate        DATE              NOT NULL,"
                                   + "FBargainTime        VARCHAR2(20)      NOT NULL,"
                                   + "FSettleDate         DATE              NOT NULL,"
                                   + "FSettleTime         VARCHAR2(20)      NOT NULL,"
                                   + "FAutoSettle         NUMBER(1, 0)      NOT NULL,"
                                   + "FPortCuryRate       NUMBER(18, 12)    NOT NULL,"
                                   + "FBaseCuryRate       NUMBER(18, 12)    NOT NULL,"
                                   + "FAllotProportion    NUMBER(18, 8)     NOT NULL,"
                                   + "FOldAllotAmount     NUMBER(18, 4)     NOT NULL,"
                                   + "FAllotFactor        NUMBER(18, 4)     NOT NULL,"
                                   + "FTradeAmount        NUMBER(18, 4)     NOT NULL,"
                                   + "FTradePrice         NUMBER(18, 4)     NOT NULL,"
                                   + "FTradeMoney         NUMBER(18, 4)     NOT NULL,"
                                   + "FAccruedInterest    NUMBER(18, 4),"
                                   + "FFeeCode1           VARCHAR2(20),"
                                   + "FTradeFee1          NUMBER(18, 4),"
                                   + "FFeeCode2           VARCHAR2(20),"
                                   + "FTradeFee2          NUMBER(18, 4),"
                                   + "FFeeCode3           VARCHAR2(20),"
                                   + "FTradeFee3          NUMBER(18, 4),"
                                   + "FFeeCode4           VARCHAR2(20),"
                                   + "FTradeFee4          NUMBER(18, 4),"
                                   + "FFeeCode5           VARCHAR2(20),"
                                   + "FTradeFee5          NUMBER(18, 4),"
                                   + "FFeeCode6           VARCHAR2(20),"
                                   + "FTradeFee6          NUMBER(18, 4),"
                                   + "FFeeCode7           VARCHAR2(20),"
                                   + "FTradeFee7          NUMBER(18, 4),"
                                   + "FFeeCode8           VARCHAR2(20),"
                                   + "FTradeFee8          NUMBER(18, 4),"
                                   + "FTotalCost          NUMBER(18, 4),"
                                   + "FCost               NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                                   + "FMCost              NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                                   + "FVCost              NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                                   + "FBaseCuryCost       NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                                   + "FMBaseCuryCost      NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                                   + "FVBaseCuryCost      NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                                   + "FPortCuryCost       NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                                   + "FMPortCuryCost      NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                                   + "FVPortCuryCost      NUMBER(18, 4)     DEFAULT 0 NOT NULL,"
                                   + "FSettleState        NUMBER(1, 0)      DEFAULT 0 NOT NULL,"
                                   + "FOrderNum           VARCHAR2(20),"
                                   + "FDataSource         NUMBER(1, 0)      NOT NULL,"
                                   + "FDesc               VARCHAR2(100),"
                                   + "FCheckState         NUMBER(1, 0)      NOT NULL,"
                                   + "FCreator            VARCHAR2(20)      NOT NULL,"
                                   + "FCreateTime         VARCHAR2(20)      NOT NULL,"
                                   + "FCheckUser          VARCHAR2(20),"
                                   + "FCheckTime          VARCHAR2(20),"
                                   + "CONSTRAINT " + pub.yssGetTableName("PK_Tb_Data_SubTrade_blm") + " PRIMARY KEY (FNum))");
                } catch (Exception e) {
                    throw new YssException("保存澎博接口数据失败！", e);
                }
            }

            String strSql = "insert into " + pub.yssGetTableName("Tb_Data_SubTrade_blm") +
                "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
                " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
                " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTPROPORTION,FOLDALLOTAMOUNT,FALLOTFACTOR," +
                " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FACCRUEDINTEREST," +
                " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
                " FTotalCost, FCost,FMCost,FVCost,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FPortCuryCost,FMPortCuryCost,FVPortCuryCost, " +
                " FOrderNum, FDataSource, FDesc, FCheckState, FCreator, FCreateTime, FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            psSub = dbl.openPreparedStatement(strSql);
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            dbl.executeSql("delete from " + pub.yssGetTableName("Tb_Data_SubTrade_blm") + " where FBargainDate = " + dbl.sqlDate(dateTrade));
            strSql = "select Security,ISIN,Side,BRokerID,TradeDate,SettlementDate,sum(amount) as amount ,sum(preice*amount)as money from  tb_bloomberg_data where TradeDate = " + dbl.sqlDate(dateTrade) + " group by Security,ISIN,SIDe,BRokerID,TradeDate,SettlementDate";

            //先检查是否有未设定信息的股票
            StringBuffer bufTmp = new StringBuffer();
//         rs = dbl.openResultSet("Select * from (" + strSql + ") a left join (select FSecurityCode,FMarketCode,FTradeCury,max(fstartDate) from " + pub.yssGetTableName("Tb_Para_Security") + " group by FSecurityCode,FMarketCode,FTradeCury ) b on a.ISIN = b.FMarketCode where FMarketCode is null ");
            rs = dbl.openResultSet("Select * from (" + strSql + ") a left join (select FSecurityCode,FMarketCode,FTradeCury,max(fstartDate) from " + pub.yssGetTableName("Tb_Para_Security") +
                                   " group by FSecurityCode,FMarketCode,FTradeCury ) b on a.Security = b.FSecurityCode where FSecurityCode is null ");
            while (rs.next()) {
                bufTmp.append("[").append(rs.getString("Security")).append("]");
            }
            rs.getStatement().close();
            rs = null;
            if (bufTmp.length() > 0) {
                throw new YssException("系统中没有下列证券代码的证券" + bufTmp.toString() +
                                       "，清检查设置！");
            }
            //
            strSql = "Select * from (" + strSql + ") a join (select FSecurityCode,FMarketCode,FTradeCury,max(fstartDate) from " + pub.yssGetTableName("Tb_Para_Security") + " group by FSecurityCode,FMarketCode,FTradeCury ) b on a.Security = b.FSecurityCode ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                String strNum = "T" + YssFun.formatDate(rs.getDate("TradeDate"), "yyyyMMdd") + YssFun.formatNumber(intCount, "00000") + "00000";
                String strCode = rs.getString("FSecurityCode");
                String strBrokerCode = rs.getString("BRokerID");
                String strBS = (rs.getString("Side").equalsIgnoreCase("B")) ? "01" : "02";
                double dubBaseRate = this.getSettingOper().getCuryRate(dateTrade,
                    rs.getString("FTradeCury"), "", YssOperCons.YSS_RATE_BASE); //基础汇率
                //通过通用方法获取汇率 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
                eachOper = new EachRateOper();
                eachOper.setYssPub(pub);
                eachOper.getInnerPortRate(dateTrade, rs.getString("FTradeCury"), sPortCode);
                double dubPortRate = eachOper.getDPortRate();
                //double dubPortRate = this.getSettingOper().getCuryRate(dateTrade,
                //     pub.getBaseCury(),"",YssOperCons.YSS_RATE_PORT); //组合汇率
                StringBuffer buf = new StringBuffer();
                buf.append("").append("\t"); // 费用代码
                buf.append("").append("\t"); //费用名称
                buf.append("0").append("\t"); //费用类型
                buf.append("").append("\t"); //上市代码
                buf.append("").append("\t"); //外部代码
                buf.append("").append("\t"); //描述
                buf.append(strCode).append("\t"); //证券代码
                buf.append("").append("\t"); //品种代码
                buf.append(strBrokerCode).append("\t"); //券商代码
                buf.append(strBS).append("\t"); //交易类型
                buf.append(rs.getDouble("money")).append("\t"); //成交数量
                buf.append(rs.getDouble("Amount")).append("\t"); //成交金额
                buf.append("-1").append("\t"); //单价
                buf.append("-1").append("\t"); //收益
                buf.append("0").append("\t"); //利益
                buf.append("-1").append("\t"); //费用
                buf.append("0").append("\t"); //审核状态
                buf.append("0").append("\t"); //结算方式
                buf.append(" ").append("\t"); //旧费用代码
                double[] dubFee = getFee(buf.toString());

                psSub.setString(1, strNum); //编号
                psSub.setString(2, strCode); //证券代码
                //psSub.setString(3,"GFund"); //组合代码
                psSub.setString(3, sPortCode); //增加组合代码 QDV4建行2009年4月22日01_B MS00410 by leeyu 20090427
                psSub.setString(4, strBrokerCode); //券商代码
                psSub.setString(5, " "); //投资经理代码
                psSub.setString(6, strBS); //交易方式
                psSub.setString(7, " "); //现金帐户代码
                psSub.setString(8, " "); //所属分类
                psSub.setDate(9, rs.getDate("TradeDate")); //成交日期
                psSub.setString(10, "00:00:00"); //成交时间
                psSub.setDate(11, rs.getDate("SettlementDate")); //结算日期
                psSub.setString(12, "00:00:00"); //结算时间
                psSub.setInt(13, 1); //自动结算
                psSub.setDouble(14, 1); //组合汇率
                psSub.setDouble(15, 1); //基础汇率

                psSub.setDouble(16, 1); //分配比例
                psSub.setDouble(17, rs.getDouble("Amount")); //原始分配数量

                psSub.setDouble(18, 1); //分配因子
                psSub.setDouble(19, rs.getDouble("Amount")); //交易数量
                psSub.setDouble(20, rs.getDouble("money") / rs.getDouble("Amount")); //交易价格
                psSub.setDouble(21, rs.getDouble("money")); //交易金额
                psSub.setDouble(22, 0); //应计利息
                psSub.setString(23, "COMMISSIONS"); //费用代码1
                psSub.setDouble(24, dubFee[0]); //交易费用1
                psSub.setString(25, "Fee"); //费用代码2
                psSub.setDouble(26, dubFee[1]); //交易费用2
                psSub.setString(27, " "); //费用代码3
                psSub.setDouble(28, 0); //交易费用3
                psSub.setString(29, " "); //费用代码4
                psSub.setDouble(30, 0); //交易费用4
                psSub.setString(31, " "); //费用代码5
                psSub.setDouble(32, 0); //交易费用5
                psSub.setString(33, " "); //费用代码6
                psSub.setDouble(34, 0); //交易费用6
                psSub.setString(35, " "); //费用代码7
                psSub.setDouble(36, 0); //交易费用7
                psSub.setString(37, " "); //费用代码8
                psSub.setDouble(38, 0); //交易费用8
                double dubTotal = rs.getDouble("money") + (YssD.add(dubFee[0], dubFee[1])) * (rs.getString("Side").equalsIgnoreCase("B") ? 1 : -1);
                psSub.setDouble(39, dubTotal); //投资总成本
                double dubCurr = YssFun.roundIt(dubBaseRate * 0, 2);
                psSub.setDouble(40, 0); //原币核算成本
                psSub.setDouble(41, 0); //原币管理成本
                psSub.setDouble(42, 0); //原币估值成本
                psSub.setDouble(43, dubCurr); //基础货币核算成本
                psSub.setDouble(44, dubCurr); //基础货币管理成本
                psSub.setDouble(45, dubCurr); //基础货币估值成本
                dubCurr = YssFun.roundIt(dubCurr / dubPortRate, 2);
                psSub.setDouble(46, dubCurr); //组合货币核算成本
                psSub.setDouble(47, dubCurr); //组合货币管理成本
                psSub.setDouble(48, dubCurr); //组合货币估值成本
                psSub.setString(49, " "); //订单编号
                psSub.setInt(50, 1); //数据来源
                psSub.setString(51, " "); //描述
                psSub.setInt(52, 0); //审核状态
                psSub.setString(53, pub.getUserName()); //创建人、修改人
                psSub.setString(54, strNowDate); //创建、修改时间
                psSub.setString(55, " "); //复核人

                psSub.addBatch();
                intCount++;
            }
            rs.getStatement().close();
            rs = null;
            psSub.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (BatchUpdateException bue) {
            throw new YssException("保存澎博接口数据失败！", bue);
        } catch (Exception e) {
            throw new YssException("保存澎博接口数据失败！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeStatementFinal(psSub);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
    }

    private double[] getFee(String strValue) throws YssException {
        FeeBean fee = new FeeBean();
        fee.setYssPub(pub);
        fee.parseRowStr(strValue);
        double dubReturn[] = new double[2]; //0 为佣金， 1 为其他费用合计
        dubReturn[0] = 0;
        dubReturn[1] = 0;
        String[] strList = fee.getListViewData3().split("\r\f");
        if (strList.length < 2) {
            return dubReturn;
        }
        strList = strList[2].split("\f\f");
        for (int i = 0; i < strList.length - 1; i++) { //最后一个是total可以不用考虑
            String[] strCols = strList[i].split("\n");
            if (strCols[0].equalsIgnoreCase("COMMISSIONS")) {
                dubReturn[0] = YssFun.toDouble(strCols[2]);
            } else {
                dubReturn[1] = YssD.add(dubReturn[1], YssFun.toDouble(strCols[2]));
            }
        }
        return dubReturn;

    }
}
