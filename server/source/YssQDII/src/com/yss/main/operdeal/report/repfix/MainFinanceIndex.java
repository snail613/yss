package com.yss.main.operdeal.report.repfix;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.cusreport.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.*;
import com.yss.main.operdeal.report.netvalueviewpl.*;
import com.yss.main.report.*;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 *
 * <p>Title:主要财务指标 </p>
 *  <p>Author: 陈嘉
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ysstech</p>
 * @author not attributable
 * @version 1.0
 */
public class MainFinanceIndex
    extends BaseBuildCommonRep {
    private String startDate = ""; //期初日期
    private String endDate = ""; //期末日期
    private String portCode = ""; //组合代码
    private String portID = ""; //20110601 modified by liubo 财务估值表中使用的套账代码
    protected double Interest = 0; //本期利润
    protected double Gzzz = 0; //公允价值变动损益
    protected double Hdsy = 0; //汇兑损益
    protected double n = 0; //交易天数
    protected double endNetValue = 0; //期末资产净值
    private CommonRepBean repBean;
    private String holidayCode = ""; //节假日代码   20081014现在的节假日不是写死在代码中，而是从界面上传进来
    YssFinance fc = null;
    private FixPub fixPub = null;
    public MainFinanceIndex() {
    }

    /**
     * buildReport
     *
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        sResult = buildShowData();
        return sResult;
    }

    /**
     * initBuildReport
     *
     * @param bean BaseBean
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        startDate = reqAry[0].split("\r")[1];
        endDate = reqAry[1].split("\r")[1];
        portCode = reqAry[2].split("\r")[1];
        holidayCode = reqAry[3].split("\r")[1];
        fixPub = new FixPub();
        fixPub.setYssPub(pub);
    }

    /**
     * saveReport
     *
     * @param sReport String
     * @return String
     */
    public String saveReport(String sReport) {
        return "";
    }

    protected String buildShowData() throws //报表的显示数据
        YssException {
        StringBuffer buf = new StringBuffer();
        StringBuffer strResult = new StringBuffer();
        String str = "";
        fc = new YssFinance(); //可以调用里面的函数按其年份来生成财务里的表
        fc.setYssPub(pub);
        try {
            //每两条的信息以"\r\n"
            getInterest(); //获得本期利润
            getGZZZ(); //获的公允价值变动损益
            getHdsy(); //获得本期汇兑损益
            endNetValue = getNetValue(YssFun.toSqlDate(endDate)); //获得期末资产净值
            buf.append("本期利润:").append("\t");
            buf.append(YssFun.roundIt(Interest, 4));

            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("本期公允价值变动损益:").append("\t");
            buf.append(YssFun.roundIt(Gzzz, 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("本期汇兑损益:").append("\t");
            buf.append(YssFun.roundIt(Hdsy, 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("本期利润总额扣减公允价值变动损益后的净额:").append("\t");
            buf.append(YssFun.roundIt( (Interest - Gzzz), 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("本期利润总额扣减汇兑损益后的净额:").append("\t");
            buf.append(YssFun.roundIt( (Interest - Hdsy), 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("本期利润总额扣减公允价值变动损益和汇兑损益后的净额:").append("\t");
            buf.append(YssFun.roundIt( (Interest - Gzzz - Hdsy), 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("期末基金资产净值:").append("\t");
            buf.append(YssFun.roundIt(endNetValue, 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("加权平均基金份额本期利润总额:").append("\t");
            buf.append(YssFun.roundIt(getJQPJTotal(), 4)); //在这个BEAN 里有两个获取加权平均基金份额本期利润总额的函数 现在用的是通过简化公式进行计算
            //这两个函数可以进行核对
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("期末可供分配利润:").append("\t");
            buf.append(YssFun.roundIt(getEndAlotInterest(), 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("期末可供分配基金份额利润:").append("\t");
            if (getTotalAmount(YssFun.toSqlDate(endDate)) != 0) {
                //用期末可供分配利润/期末的基金分额
                buf.append(YssFun.roundIt(getEndAlotInterest() /
                                          getTotalAmount(YssFun.toSqlDate(endDate)),
                                          4));
            }
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("期末单位基金资产净值:").append("\t");
            buf.append(YssFun.roundIt(getUnitValue(YssFun.toSqlDate(endDate)), 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("加权平均净值利润率:").append("\t");
            buf.append(YssFun.roundIt(this.getJQAvgInterestRate(), 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("本期基金份额净值增长率:").append("\t");
            buf.append(YssFun.roundIt(this.getAmountRate(), 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            buf.setLength(0); //给buf清空
            buf.append("份额累计净值增长率:").append("\t");
            buf.append(YssFun.roundIt(this.getFELJJZRate(), 4));
            str = buf.toString();
            strResult.append(this.buildRowCompResult(str)).append("\r\n");

            return strResult.toString();
        } catch (Exception e) {
            throw new YssException("获取主要财务指标出错： \n" + e.getMessage());
        } finally {
        }
    }

    /**
     * 这个是获取本期利润的函数
     * sunny
     * @param compRep YssCompRep
     * @throws YssException
     * @return String
     */
    protected void getInterest() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            // YssFinance fc = new YssFinance();
            //fc.setYssPub(pub);
            strSql = "select '本期利润:' as FName, sum(case when (fjd='D') then fbbal else -fbbal end) as FInterest " +
                "  from (select fbbal,a.fjd " +
                " from " +
                fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch") +
                " a join " +
                fc.getCWTabName(portCode, YssFun.toSqlDate(startDate),
                                "laccount") + " b on a.fkmh = b.facctcode " +
                //alter by chenjia 考虑损益股票投资一分钱的调整，做在借方，余额方向为‘-1’，所以还需加个OR条件
                "  where b.facctclass like '损益类%' and ((FBALDC = '-1' and a.fjd='D') or (FBALDC = '1' and fjd='J') or (a.fkmh='6061' and fjd='J' and fvchzy like '%买入股票%')) and b.facctdetail=1 and fdate between " +
                dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate) + ")";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                Interest = YssD.round(rs.getDouble("FInterest"), 4); //得到本期利润
            }
            rs.close();
        } catch (Exception e) {
            throw new YssException("获取主要财务指标中的本期利润出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 这个是获取公允价值变动损益的函数
     * sunny
     * @param compRep YssCompRep
     * @throws YssException
     * @return String
     */
    protected void getGZZZ() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            YssFinance fc = new YssFinance();
            fc.setYssPub(pub);
            strSql = " select '本期公允价值变动损益:' as FName, sum(fbbal) as fgzBbal " +
                " from " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch") + " a join " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "laccount") + " b on a.fkmh = b.facctcode " +
                " where b.facctattr like '公允价值变动损益%'  and a.fjd='D' and b.facctdetail=1 and fdate between " +
                dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                Gzzz = rs.getDouble("fgzBbal"); //获得估值增值
            }
            rs.close();
        } catch (Exception e) {
            throw new YssException("获取主要财务指标中的公允价值变动损益出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 这个是获取汇兑损益的函数
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected void getHdsy() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            YssFinance fc = new YssFinance();
            fc.setYssPub(pub);
            strSql = " select '本期汇兑损益:' as FName, sum(fbbal) as fsyBbal " +
                " from " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch") + " a join " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "laccount") + " b on a.fkmh = b.facctcode " +
                " where b.facctattr like '财务费用_汇兑损益%'   and ((FBALDC = '-1' and a.fjd='D') or (FBALDC = '1' and fjd='J')) and b.facctdetail=1 and fdate between " +
                dbl.sqlDate(startDate) + " and " + dbl.sqlDate(endDate);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                Hdsy = rs.getDouble("fsyBbal"); //获得汇兑损益
            }
            rs.close();
        } catch (Exception e) {
            throw new YssException("获取主要财务指标中的汇兑损益出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 这个是获取交易的天数
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected void getTradeDays() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        java.sql.Date dDate = null;
        java.util.Date inceptionDate = null;
        inceptionDate = fixPub.getInceptionDate(this.portCode);
        try {
            dDate = YssFun.toSqlDate(endDate);
            dDate = YssFun.toSqlDate(YssFun.addDay(dDate, -1)); //modify by fangjiang 2012.02.13 bug 3804 最后一天不管它是不是节假日 都算成是交易日 （还有待确认） 
            strSql = "select count(distinct fdate) as fnum from Tb_Base_ChildHoliday  a left join tb_base_holidays b on a.fholidayscode=b.fholidayscode where fdate between " +
//               dbl.sqlDate(startDate) + " and " + dbl.sqlDate(dDate) + " and b.fcheckstate=1 and b.fholidayscode='CH'";
                dbl.sqlDate(startDate) + " and " + dbl.sqlDate(dDate) + " and b.fcheckstate=1 and b.fholidayscode = " + dbl.sqlString(this.holidayCode); //20081014
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //alter by chenjia 期初日期无论是不是基金成立日，需加1
                //if(YssFun.dateDiff(YssFun.toSqlDate(startDate),inceptionDate)==0){
                n = YssFun.dateDiff(YssFun.toSqlDate(startDate), YssFun.toSqlDate(endDate)) -
                    rs.getDouble("fnum") + 1 + 1; //拿整个的日期-节假日  modify by fangjiang 2012.02.13 bug 3804 加第二个1是因为最后一天不管它是不是节假日 都算成是交易日 （还有待确认）
                // }else{
                //    n = YssFun.dateDiff(YssFun.toSqlDate(startDate),
                //                      YssFun.toSqlDate(endDate)) -
                //     rs.getDouble("fnum")+1; //拿整个的日期-节假日
                // }
            }
            rs.close();
        } catch (Exception e) {
            throw new YssException("获取交易的天数： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 这个是获取加权平均基金份额本期利润总额2
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected double getJQPJTotal2() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double fundJSY = 0; //本期基金净收益
        double iAmount = 0;
        double befAmount = 0;
        double dJQPJTotal = 0;
        //获得期初的基金单位总份额
        double startAmount = this.getTotalAmount(YssFun.toSqlDate(YssFun.addDay(YssFun.toSqlDate(startDate), -1)));
        try {
            fundJSY = Interest - Hdsy - Gzzz;
            this.getTradeDays(); //获取交易天数
            for (int i = 0; i < n; i++) {
                //第 i 日的 基金单位总份额
                iAmount = this.getTotalAmount(YssFun.toSqlDate(YssFun.addDay(YssFun.
                    toSqlDate(startDate), i)));
                //第 i - 1 日的 基金单位总份额
                befAmount = this.getTotalAmount(YssFun.toSqlDate(YssFun.addDay(
                    YssFun.toSqlDate(startDate), i - 1)));
                if ( (startAmount + (iAmount - befAmount) * (n - i) / n) != 0) {
                    dJQPJTotal = dJQPJTotal +
                        fundJSY /
                        (startAmount + (iAmount - befAmount) * (n - i) / n);
                }
            }
            return dJQPJTotal;
        } catch (Exception e) {
            throw new YssException("获取加权平均基金份额本期利润总额： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 这个是获取加权平均基金份额本期利润总额1
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected double getJQPJTotal() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double fundJSY = 0; //本期基金净收益
        double iAmount = 0;
        double befAmount = 0;
        double dJQPJTotal = 0;
        java.util.Date tradeDate = null;
        boolean flagTrade = false; //判断是不是交易日
        java.util.Date inceptionDate = null;
        double startAmount = 0;
        try {
            fundJSY = Interest; //计算加权平均基金份额本期利润总额时本期基金收益不用减掉汇兑损益和公允价值变动损益。
            this.getTradeDays(); //获取交易天数
            for (int i = 1; i <= n; i++) { //i<n 改成  i<=n modify by fangjiang 2012.02.13 bug 3804 最后一天不管它是不是节假日 都算成是交易日 （还有待确认）
                if (i == 1) {
                    tradeDate = YssFun.toSqlDate(startDate); //当是第一天把期初日期赋给我们的交易日
                } else {
                    tradeDate = YssFun.toSqlDate(YssFun.addDay(YssFun.toSqlDate( //每循环一次日期加一天
                        tradeDate), 1));
                } 
                while (!flagTrade) {
                    //判断它是不是节假日 如果是节假日那么就继续加1
                    strSql =
                        "select count(distinct fdate) as fnum from Tb_Base_ChildHoliday where fholidaYscode = " +
                        //  'CH'
                        dbl.sqlString(holidayCode) + " and fdate= " + //20081014
                        dbl.sqlDate(tradeDate);
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        if (rs.getDouble("fnum") != 0 //就说明是节假日
                        		&& YssFun.dateDiff(YssFun.toDate(this.endDate),tradeDate) < 0 ) { //add by fangjiang 2012.02.13 bug 3804 最后一天不管它是不是节假日 都算成是交易日 （还有待确认）                      	
                            tradeDate = YssFun.toSqlDate(YssFun.addDay(YssFun.
                                toSqlDate(
                                    tradeDate), 1)); //把当天日期往后延一天 再进行判断
                        } else {
                            flagTrade = true;
                        }
                    }
                    dbl.closeResultSetFinal(rs);
                }
                if (flagTrade) { //当为TRUE 的时候
                    flagTrade = false;
                }
                iAmount = iAmount + this.getTotalAmount(YssFun.toSqlDate(tradeDate));
            }
            //获得期初的基金单位总份额
            inceptionDate = fixPub.getInceptionDate(this.portCode); //获取基金成立日期
            if (YssFun.dateDiff(YssFun.addDay(YssFun.toDate(startDate), -1), inceptionDate) > 0) { //如果期初-1<基金成立日期
                startAmount = this.getTotalAmount(YssFun.toSqlDate(inceptionDate));
            } else {
                startAmount = this.getTotalAmount(YssFun.toSqlDate(YssFun.addDay(YssFun.toSqlDate(startDate), -1)));
            }
            if (iAmount != 0) {
                dJQPJTotal = fundJSY * n / (iAmount + startAmount);
            }
            return dJQPJTotal;
        } catch (Exception e) {
            throw new YssException("获取加权平均基金份额本期利润总额： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 这个是获取基金单位总份额
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected double getTotalAmount(java.sql.Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            //根据传进来的日期来获得该日期的基金单位总份额
            strSql = "select * from " + pub.yssGetTableName("tb_stock_ta") +
                " where FStorageDate=" + dbl.sqlDate(dDate) + " and FPortCode= " +
                dbl.sqlString(portCode) + " and FCheckState=1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dReturn = rs.getDouble("FStorageAmount");
            }
            rs.close();
            return dReturn;
        } catch (Exception e) {
            throw new YssException("获取基金单位总份额： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 这个是获取基金资产净值的通用方法
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected double getNetValue(java.sql.Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dreturn = 0;
        try {
            strSql = "select * from " + pub.yssGetTableName("tb_rep_guessvalue") +
                " where fdate= " + dbl.sqlDate(dDate) +
                //alter by chenjia 需多加一个判断条件，来判断其‘资产净值’
                " and fCurcode = ' ' and facctattr = '资产净值' and FAcctcode='9000'" + " and FPortCode in " +
                " (select b.fsetcode from (select fassetcode from  " + pub.yssGetTableName("tb_para_portfolio") + " where FportCode=" +
                dbl.sqlString(portCode) + ") a  join (select distinct(fsetid),fsetcode from  lsetlist) b on a.fassetcode = b.fsetid)";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dreturn = rs.getDouble("FStandardMoneyMarketValue");
            }
            rs.close();
            return dreturn;
        } catch (Exception e) {
            throw new YssException("获取基金资产净值： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 这个是获取单位净值
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected double getUnitValue(java.util.Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            strSql = "select * from " + pub.yssGetTableName("tb_rep_guessvalue") +
                " where fdate= " + dbl.sqlDate(dDate) +
                " and fCurcode = ' ' and facctattr = '单位净值' and FAcctCode='9600'" +
                " and FPortCode in " +
                " (select b.fsetcode from (select fassetcode from  " +
                pub.yssGetTableName("tb_para_portfolio") + " where FportCode=" +
                dbl.sqlString(portCode) +
                ") a  join (select distinct(fsetid),fsetcode from  lsetlist) b on a.fassetcode = b.fsetid)";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dReturn = rs.getDouble("FStandardMoneyMarketValue");
            }
            rs.close();
            return dReturn;
        } catch (Exception e) {
            throw new YssException("获取期末单位净值： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * ---期初未分配收益
     --收益分配_未分配收益
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected double getUnAlotInterest() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            //从余额表里面取未分配收益
            strSql = "select * from " +
                fc.getCWTabName(portCode, YssFun.toSqlDate(startDate),
                                "Lbalance") + " a join (select * from " +
                fc.
                getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount") +
                ") b on a.facctcode = b.facctcode " +
                //alter by chenjia 这里应包括本期利润-已实现以及利润分配-未分配收益-已实现收益，期初应该是根据期初日期来确定期初的月份
                " where facctattr like '%利润_已实现%' and fmonth=" + (YssFun.getMonth(YssFun.toSqlDate(startDate)) - 1);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dReturn += rs.getDouble("fbendbal") * rs.getDouble("FBalDC"); //从余额表取数时要考虑科目的余额方向  //alter by chenjia 此处改为取本位币的金额，有可能存在本位币不是原币的情况
            }
            rs.close();
            return dReturn;
        } catch (Exception e) {
            throw new YssException("期初未分配收益： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * ---本期已分配收益
     * --410401  收益分配_应付收益
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected double getAlotInterest() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            strSql = "select NVL(sum(fbbal), 0) as fbbal, NVL(sum(fsl), 0) from " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch") +
                " where fkmh like '410401%' and " +
                " fdate between " + dbl.sqlDate(YssFun.toDate(startDate)) + " and " + dbl.sqlDate(YssFun.toDate(endDate)) + " and Fjd = 'D' ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dReturn = rs.getDouble("fbbal");
            }
            rs.close();
            return dReturn;
        } catch (Exception e) {
            throw new YssException("应付收益分配： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * -----本期损益平准金
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected double getSYPZJ() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            YssFinance fc = new YssFinance();
            fc.setYssPub(pub);
            //alter by chenjia 修改从科目性质取数，而不是从科目取数
            strSql = " select a.FYSXSG,b.FYSXZR,c.FYSXSH,d.FYSXZC from " +
                "(select NVL(sum(fbbal), 0) as FYSXSG, NVL(sum(fsl), 0),1 as FType from " +
                fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch") + " vch left join (select * from " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount") + ") lacc on vch.Fkmh=lacc.facctcode " +
                " where facctattr like '损益平准金_已实现_申购' and fdate between " +
                dbl.sqlDate(YssFun.toDate(startDate)) + " and " +
                dbl.sqlDate(YssFun.toDate(endDate)) + "  and FPZLY = 'TAXS') a join " + // 损益平准金_已实现_申购
                "(select NVL(sum(fbbal), 0) as FYSXZR, NVL(sum(fsl), 0),1 as FType from " +
                fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch") + " vch left join (select * from " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount") + ") lacc on vch.Fkmh=lacc.facctcode " +
                " where facctattr like '损益平准金_已实现_转入' and fdate between " +
                dbl.sqlDate(YssFun.toDate(startDate)) + " and " +
                dbl.sqlDate(YssFun.toDate(endDate)) +
                " and FPZLY = 'TAXS' ) b on a.FType=b.FType " +
                " join " + // 损益平准金_已实现_转入
                "(select NVL(sum(fbbal), 0) as FYSXSH, NVL(sum(fsl), 0),1 as FType from " +
                fc.getCWTabName(portCode,
                                YssFun.toSqlDate(startDate), "fcwvch") + " vch left join (select * from " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount") + ") lacc on vch.Fkmh=lacc.facctcode " +
                " where facctattr like '损益平准金_已实现_赎回' and fdate between " +
                dbl.sqlDate(YssFun.toDate(startDate)) +
                " and " +
                dbl.sqlDate(YssFun.toDate(endDate)) +
                " and FPZLY = 'TAXS' ) c on a.FType = c.FType join " + // 损益平准金_已实现_赎回
                "(select NVL(sum(fbbal), 0) as FYSXZC, NVL(sum(fsl), 0),1 as FType from " +
                fc.getCWTabName(portCode, YssFun.toSqlDate(startDate),
                                "fcwvch") + " vch left join (select * from " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount") + ") lacc on vch.Fkmh=lacc.facctcode " +
                " where facctattr like '损益平准金_已实现_转出' and fdate between " +
                dbl.sqlDate(YssFun.toDate(startDate)) + " and " +
                dbl.sqlDate(YssFun.toDate(endDate)) +
                " and FPZLY = 'TAXS' ) d on c.FType=d.FType"; //损益平准金_已实现_转出
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //---本期损益平准金=损益平准金_已实现_申购+损益平准金_已实现_转入-损益平准金_已实现_赎回-损益平准金_已实现_转出
                dReturn = rs.getDouble("FYSXSG") + rs.getDouble("FYSXZR") - rs.getDouble("FYSXSH") - rs.getDouble("FYSXZC");
            }
            rs.close();
            return dReturn;
        } catch (Exception e) {
            throw new YssException("本期损益平准金： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 未实现利得
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected double getWSXLD() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
            strSql = "select c.kmdm as facctcode,d.facctname,d.famount,d.facctattr,d.FAcctClass,D.FBALDC,c.fbendbal as je,c.faendbal as sl from " +
                "(select NVL(A.fkmh,B.facctcode)  as kmdm,NVL(A.fcyid,B.fcurcode)  as fcurcode,NVL(B.fendbal,0) +NVL(fjje,0) -NVL(fdje,0)  as fendbal, " +
                "NVL(B.fbendbal,0) + NVL(fbjje,0) -NVL(fbdje,0) as fbendbal,NVL(B.faendbal,0) +NVL(fjsl,0) - NVL(fdsl,0) as faendbal from " +
                "(select fkmh,fcyid,fterm,sum(Case when fjd='J' then fbal else 0 end) as fjje,sum(Case when fjd='D' then fbal else 0 end) as fdje," +
                "sum(Case when fjd='J' then fsl else 0 end ) as fjsl,sum(Case when fjd='D' then fsl else 0 end ) as fdsl," +
                "sum(Case when fjd='J' then fbbal else 0 end) as fbjje,sum(Case when fjd='D' then fbbal else 0 end) as fbdje," +
                "sum(Case when fjd='J' then fbsl else 0 end ) as fbjsl,sum(Case when fjd='D' then fbsl else 0 end ) as fbdsl " +
                " from " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch") + " where fterm= " + YssFun.getMonth(YssFun.toSqlDate(endDate)) + "  and to_number(to_char(FDate,'dd'))<= 31 group by fkmh,fcyid,fterm) a " +
                " full join " +
                "(select facctcode,fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faacccredit,faendbal " +
                " from " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "lbalance") + " where fmonth=" + (YssFun.getMonth(YssFun.toSqlDate(endDate)) - 1) + " and fisdetail=1) b on a.fkmh=b.facctcode and a.fcyid=b.fcurcode) c " +
                " join (select facctcode,facctname,famount,facctattr,FAcctClass,FBALDC from " + fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount") + " where  " +
                "(facctattr like '%未实现%' or facctattr like '公允价值变动损益%') ) d on c.kmdm=d.facctcode order by c.kmdm";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dReturn = dReturn + rs.getDouble("je");
            }
            rs.close();
            return dReturn;
        } catch (Exception e) {
            throw new YssException("未实现利得： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 期末可供分配利润
     * sunny
     * @param
     * @throws YssException
     * @return String
     * modify by fangjiang STORY #2125 2012.01.12
     */
    protected double getEndAlotInterest() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double fundJSY = 0;
        double dReturn = 0;
        double qmwfplr = 0.0; //期末未分配利润
        double qmysx = 0.0;   //期末未分配利润中的已实现
        try {
            //本期净收益=本期利润扣减本期公允价值变动损益后的净额
            //fundJSY = Interest - Hdsy - Gzzz;
            //fundJSY = Interest - Gzzz;
            // 期末可供分配基金收益=本期净收益+期初未分配收益+本期损益平准金（若有）-本期已分配收益－期末未实现利得损失（若有）
            /*if (getWSXLD() < 0) {
                dReturn = fundJSY + getUnAlotInterest() + getSYPZJ() -
                    getAlotInterest(); //- getWSXLD();
                dReturn = this.endNetValue - getTotalAmount(YssFun.toSqlDate(endDate));
            } else {
                dReturn = fundJSY + getUnAlotInterest() + getSYPZJ() -
                    getAlotInterest() - Math.abs(getWSXLD());
        	    dReturn = this.endNetValue - getTotalAmount(YssFun.toSqlDate(endDate)) - Math.abs(getWSXLD());
            }*/
        	qmwfplr = this.endNetValue - getTotalAmount(YssFun.toSqlDate(endDate));
        	qmysx = this.getQMYSX();
        	if(qmwfplr < qmysx){
        		dReturn = qmwfplr;
        	}else{
        		dReturn = qmysx;
        	}
            
            return dReturn;
        } catch (Exception e) {
            throw new YssException("期末可供分配利润： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 加权平均净值利润率
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    //此方法和加权平均基金份额本期利润的实现方法相似
    protected double getJQAvgInterestRate() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double fundJSY = 0; //本期基金净收益
        double NAV = 0; //基金资产净值
        double NAV0 = 0; //期初的单位净值
        double dJQAvgInterestRate = 0;
        java.util.Date tradeDate = null;
        boolean flagTrade = false; //判断是不是交易日
        //获得期初的基金单位总份额
        java.util.Date inceptionDate = null;
        try {
            //简化公式得：本期基金净收益*N/（期初到N-1的基金资产净值之和）
            fundJSY = Interest; //计算加权平均净值利润率时本期基金收益不用减掉汇兑损益和公允价值变动损益。
            this.getTradeDays(); //获取交易天数
            for (int i = 1; i <= n; i++) { //i<n 改成  i<=n modify by fangjiang 2012.02.13 bug 3804 最后一天不管它是不是节假日 都算成是交易日 （还有待确认）
                if (i == 1) {
                    tradeDate = YssFun.toSqlDate(startDate); //当是第一天把期初日期赋给我们的交易日
                } else {
                    tradeDate = YssFun.toSqlDate(YssFun.addDay(YssFun.toSqlDate( //每循环一次日期加一天
                        tradeDate), 1));
                } while (!flagTrade) {
                    //判断它是不是节假日 如果是节假日那么就继续加1
                    strSql =
                        "select count(distinct fdate) as fnum from Tb_Base_ChildHoliday where fholidaYscode = " +
                        dbl.sqlString(holidayCode) + " and fdate= " + //节假日代码是从界面上传进来的 20081014
                        dbl.sqlDate(tradeDate);
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        if (rs.getDouble("fnum") != 0 //就说明是节假日
                        		&& YssFun.dateDiff(YssFun.toDate(this.endDate),tradeDate) < 0 ) { //add by fangjiang 2012.02.13 bug 3804 最后一天不管它是不是节假日 都算成是交易日 （还有待确认）
                            tradeDate = YssFun.toSqlDate(YssFun.addDay(YssFun.
                                toSqlDate(
                                    tradeDate), 1)); //把当天日期往后延一天 再进行判断
                        } else {
                            flagTrade = true;
                        }
                    }
                    dbl.closeResultSetFinal(rs);
                }
                if (flagTrade) { //当为TRUE 的时候
                    flagTrade = false;
                }
                NAV = NAV + this.getNetValue(YssFun.toSqlDate(tradeDate));
            }
            inceptionDate = fixPub.getInceptionDate(this.portCode); //获取基金成立日期
            if (YssFun.dateDiff(YssFun.addDay(YssFun.toDate(startDate), -1), inceptionDate) > 0) { //如果期初-1<基金成立日期
                NAV0 = this.getNetValue(YssFun.toSqlDate(inceptionDate));
            } else {
                NAV0 = this.getNetValue(YssFun.toSqlDate(YssFun.addDay(YssFun.
                    toSqlDate(startDate), -1)));
            }
            if (NAV != 0) {
                dJQAvgInterestRate = fundJSY * n / (NAV + NAV0);
            }

            return dJQAvgInterestRate;
        } catch (Exception e) {
            throw new YssException("加权平均净值利润率： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 本期基金份额净值增长率
     * modified by liubo 20110601
     * @param
     * @throws YssException
     * @return double
     */
    //修改后的本期增长率算法为：本期净值增长率=（本期第一次分红前单位基金资产净值÷期初单位基金资产净值）×（本期第二次分红前单位基金资产净值÷本期第一次分红后单位基金资产净值）×…… ×（期末单位基金资产净值÷本期最后一次分红后单位基金资产净值）-1
    //若期内未进行分红，公式可简化为（期末÷期初单位净值）－１
    public double getAmountRate() throws YssException
    {
    	ArrayList accummulate = new ArrayList();
		ArrayList yesdayRate = new ArrayList();
		int i = 0;
		String sqlStr = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
        ResultSet rsRateDate = null;
        String holiday = "";
		java.util.Date RateDate;
		double firstRate = 1.0; // 保存第一次计算
		double centerRate = 1.0; // 保存从第二次到第倒数第二次
		double lastRate = 1.0; // 保存最后一次计算
		double lastDateRate = 0.0; // 期末单位基金资产净值
		try {
			// 期末单位基金资产净值
			sqlStr = "select  fstandardmoneymarketvalue from "
					+ pub.yssGetTableName("tb_rep_guessvalue")
					+ " where facctcode='9600' and fdate ="
					+ dbl.sqlDate(YssFun.toDate(endDate)) + " and FPortCode = "
					+ dbl.sqlString(this.portID); // 期末单位基金资产净值
			rs1 = dbl.openResultSet(sqlStr);
			while (rs1.next()) {
				lastDateRate = rs1.getDouble("fstandardmoneymarketvalue"); // 期末单位基金资产净值
			}
			dbl.closeResultSetFinal(rs1);
			
            sqlStr="select fholidayscode,fportcode from " +
		     pub.yssGetTableName("Tb_TA_CashSettle ")+
		     "where fcheckstate=1  and FSellTypeCode = '03' and (fportcode = ' ' or fportcode = " + dbl.sqlString(portCode) + ")";
       rsRateDate = dbl.openResultSet(sqlStr);
		while(rsRateDate.next())
		{
			if(rsRateDate.getString("fportcode").length() > 0){
				holiday=rsRateDate.getString("fholidayscode");
				break;
			}
			holiday=rsRateDate.getString("fholidayscode");
		}
		if (holiday.equals(""))
		{
			throw new YssException("获取节假日群错误！请在“TA业务模块“>>”TA现金结算链接设置”中为分红交易类型设置对应节假日！");
		}
            BaseOperDeal Bdeal = new BaseOperDeal();
            Bdeal.setYssPub(pub);
			// ========================================================================================================================
			// 得到当天的累计分红
			sqlStr = "select FSellPrice as FAccumulateDivided,FConfimDate from "
					+ pub.yssGetTableName("TB_TA_TRADE")
					+ " where FCheckState = 1 and FConfimDate <="
					+ dbl.sqlDate(YssFun.toDate(endDate))
					+ " and FConfimDate >= "
					+ dbl.sqlDate(YssFun.toDate(startDate))
					+ " and FSellType = '03' "
					+ " and FPortCode = "
					+ dbl.sqlString(this.portCode) + " order by FConfimDate";   //20110601 modified by liubo #936 获取期内分红数据
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {  
				accummulate.add(new Double(rs.getDouble("FAccumulateDivided"))); // 获取第N次分红的金额。
				RateDate = Bdeal.getWorkDay(holiday, rs.getDate("FConfimDate"),-1);

				// 得到分红前的的单位净值
				sqlStr = "select  fstandardmoneymarketvalue from "
						+ pub.yssGetTableName("tb_rep_guessvalue")
						+ " where facctcode='9600' and fdate = " + dbl.sqlDate(RateDate)
						+ " and FPortCode = " + dbl.sqlString(this.portID); // 获取单位净值
				rs2 = dbl.openResultSet(sqlStr);
				if (rs2.next()) {
					yesdayRate.add(new Double(rs2
							.getDouble("fstandardmoneymarketvalue"))); // 获取分红前的的单位净值
					i++;
				}
				dbl.closeResultSetFinal(rs2);
				// ===================================================================================
			}
			dbl.closeResultSetFinal(rs);
			if (i >= 1) {
				firstRate = ((Double)yesdayRate.get(0)).doubleValue()/this.getUnitValue(YssFun.toDate(startDate)); // 保存第一次计算
				for (int j = 1; j < i; j++) {
					centerRate = ((Double) yesdayRate.get(j)).doubleValue()
							/ (((Double) yesdayRate.get(j - 1)).doubleValue() - ((Double) accummulate
									.get(j - 1)).doubleValue()) * centerRate; // 从第二次到最后一次的前一次
				}
				lastRate = lastDateRate
						/ (((Double) yesdayRate.get(i - 1)).doubleValue() - ((Double) accummulate
								.get(i - 1)).doubleValue()); // 保存最后一次计算
				firstRate = firstRate * centerRate * lastRate - 1; // 把第一到最后乘起来就得到了本期净值增长率
			} else {
				double douStartUnitValue = getUnitValue(YssFun.toDate(startDate));
            	
				firstRate = this.getUnitValue(YssFun.toDate(endDate)) / (douStartUnitValue == 0 ? 1:douStartUnitValue) - 1;  //20110601 modified by liubo #936
																														  //期内无分红数据，采用简化公式算出本期净值增长率
			}
		} catch (Exception e) {
			throw new YssException("获取本期分红数据出现异常！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsRateDate);
			dbl.closeResultSetFinal(rs2);
			
		}
		return YssD.round(firstRate, 4);
    	
    }
    
    
//    public double getAmountRate() throws YssException {
//        String strSql = "";
//        ResultSet rs = null;
//        double QMDWJZ = 0;
//        ; //期末单位净值
//        double QCDWJZ = 0;
//        ; //期初单位净值
//        double dReturn = 0;
//        try {
//            //---本期基金份额净值增长率
//            //--QMDWJZ/QCDWJZ-1
//            //日期是要小于等于期初日期 那么取这个记录的最大日期
//            strSql = "select * from " + pub.yssGetTableName("tb_rep_guessvalue") +
//                " where fdate<= " + dbl.sqlDate(YssFun.addDay(YssFun.toSqlDate(startDate), -1)) +
//                " and fCurcode = ' ' and facctattr = '单位净值' and FAcctCode='9600'" +
//                " and FPortCode in " +
//                " (select b.fsetcode from (select fassetcode from  " +
//                pub.yssGetTableName("tb_para_portfolio") + " where FportCode=" +
//                dbl.sqlString(portCode) +
//                ") a  join (select distinct(fsetid),fsetcode from  lsetlist) b on a.fassetcode = b.fsetid) order by fdate desc";
//            rs = dbl.openResultSet(strSql);
//            if (rs.next()) {
//                QCDWJZ = rs.getDouble("FStandardMoneyMarketValue");
//            }
//            QMDWJZ = this.getUnitValue(YssFun.toSqlDate(endDate));
//            if (QCDWJZ == 0) {
//                QCDWJZ = 1;
//            }
//            dReturn = YssD.sub(YssD.div(QMDWJZ, QCDWJZ), 1); //edit by jc
//            return dReturn;
//        } catch (Exception e) {
//            throw new YssException("本期基金份额净值增长率： \n" + e.getMessage());
//        } finally {
//            dbl.closeResultSetFinal(rs);
//        }
//    }

    /**
     * 份额累计净值增长率
     * sunny
     * @param
     * @throws YssException
     * @return String
     */
    protected double getFELJJZRate() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dReturn = 1;
        java.util.Date inceptionDate = null;
        java.util.Date QCDate = null; //期初日期
        java.util.Date QMDate = null; //期末日期
        double dYear = 0; //年度数
        String strQCDate = "";
        String strQMDate = "";
        try {
            //根据这个获取成立日期
            strSql = "select finceptiondate from " + pub.yssGetTableName("tb_para_portfolio") + " where FPortCode = " + dbl.sqlString(portCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                inceptionDate = rs.getDate("finceptiondate");
            }
            if (YssFun.dateDiff(YssFun.toSqlDate(startDate), inceptionDate) < 0) {
                QCDate = inceptionDate; //如果期初日期要小于成立日期 则期初日期为成立日期
            } else {
                QCDate = YssFun.toSqlDate(startDate); //否则则为启用日期
            }
            if (startDate.substring(0, 4).equalsIgnoreCase(endDate.substring(
                0, 4))) { //判断是不是同一个年度
                if (this.getUnitValue(YssFun.addDay(QCDate, -1)) != 0) {
                    dReturn = this.getUnitValue(YssFun.toSqlDate(endDate)) /
                        this.getUnitValue(YssFun.addDay(QCDate, -1)) - 1;
                } else { //如果等于0 就把期初的单位净值默认为1
                    dReturn = this.getUnitValue(YssFun.toSqlDate(endDate)) - 1;
                }
            } else {
                dYear = YssFun.getYear(YssFun.toSqlDate(endDate)) - YssFun.getYear(QCDate) + 1;
                for (int i = 1; i <= dYear; i++) {
                    strQCDate = QCDate.toString().substring(0, 4) + "-01-01";
                    QCDate = YssFun.toSqlDate(strQMDate);
                    if (YssFun.dateDiff(YssFun.toSqlDate(startDate), QCDate) > 0) {
                        QCDate = YssFun.toSqlDate(startDate); //当是第一个年度的时候得把期初日期赋值为用户选择的期初日期
                    }
                    strQMDate = QCDate.toString().substring(0, 4) + "-12-31";
                    QMDate = YssFun.toSqlDate(strQMDate);
                    if (YssFun.dateDiff(QMDate, YssFun.toSqlDate(endDate)) > 0) {
                        QMDate = YssFun.toSqlDate(endDate); //循环到最后一个年度的时候得把期末日期赋值为用户选择的期末日期
                    }
                    if (this.getUnitValue(YssFun.addDay(QCDate, -1)) != 0) {
                        dReturn = dReturn *
                            (this.getUnitValue(YssFun.toSqlDate(QMDate)) /
                             this.getUnitValue(YssFun.addDay(QCDate, -1)));
                    } else { //如果期初为0 默认为1
                        dReturn = dReturn *
                            this.getUnitValue(YssFun.toSqlDate(QMDate));

                    }
                    YssFun.addYear(QCDate, 1); //每循环一次 年份增加1
                }
                dReturn = dReturn - 1;
            }
            return dReturn;
        } catch (Exception e) {
            throw new YssException("份额累计净值增长率： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String buildRowCompResult(String str) throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";
        RepTabCellBean rtc = null;
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("DsXXPL0001");
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("DsXXPL0001") +
                " and FCheckState = 1 order by FOrderIndex";
            rs = dbl.openResultSet(strSql);
            for (int i = 0; i < sArry.length; i++) {
                sKey = "DsXXPL0001" + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append(
                    "\t");
            }
            if (buf.toString().trim().length() > 1) {
                strReturn = buf.toString().substring(0,
                    buf.toString().length() - 1);
            }
            rs.close();
            return strReturn + "\t\t";
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    
    //期末已实现  add by fangjiang STORY #2125 2012.01.12
    protected double getQMYSX() throws YssException {
        String sql = "";
        ResultSet rs = null;
        double dReturn = 0;
        try {
        	int iniMonth = 0;
			iniMonth = YssFun.toInt(YssFun.formatDate(YssFun.addMonth(YssFun.toDate(this.endDate), -1), "MM")); // 取上一个月的日期
			if (YssFun.toInt(YssFun.formatDate(YssFun.toDate(this.endDate), "MM")) == 1) {
				iniMonth = 0; // 判断，如果本月的日期为1,则上一个月的日期为0
			}
			sql = "select sum(JE) as FMoney from ("
					+
					// 本期科目=损益平准金_已实现+本期利润_已实现
					// 损益平准金_已实现
					" select sum(c.fbendbal)*(-1) as JE from (select (case when A.fkmh is null then b.facctcode else a.Fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else a.fcyid end) as fcurcode,"
					+ " (case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,(case when b.fbendbal is null then 0 else b.fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,"
					+ " sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl "
					+ " from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch")
					+ " where fterm = "
					+ YssFun.formatDate(YssFun.toDate(this.endDate), "MM")
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(YssFun.toDate(this.endDate), "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "Lbalance")
					+ " where fmonth ="
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount")
					+ " where FAcctcode like '401101%' and fbalDC in (1, -1, 0)) d on c.kmdm = d.facctcode  "
					+
					// 本期利润_已实现
					" union select sum(c.fbendbal)*(-1) as JE from (select (case when A.fkmh is null then b.facctcode else A.fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else A.fcyid end) as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,"
					+ " (case when b.fbendbal is null then 0 else fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,"
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch")
					+ " where fterm ="
					+ YssFun.formatDate(YssFun.toDate(this.endDate), "MM")
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(YssFun.toDate(this.endDate), "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,"
					+ " fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+  fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "Lbalance")
					+ " where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount")
					+ " where FAcctcode like '410301%' and fbalDC in (1, -1, 0)) d on c.kmdm = d.facctcode  "
					+
					// 利润分配_未分配利润_已实现
					" union select sum(c.fbendbal)*(-1) as JE from (select (case when A.fkmh is null then b.facctcode else A.fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else A.fcyid end) as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,"
					+ " (case when b.fbendbal is null then 0 else fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,"
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch")
					+ " where fterm ="
					+ YssFun.formatDate(YssFun.toDate(this.endDate), "MM")
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(YssFun.toDate(this.endDate), "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,"
					+ " fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "Lbalance")
					+ " where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount")
					+ " where FAcctcode like '41040201%' and fbalDC in (1, -1, 0)) d on c.kmdm = d.facctcode  "
					+
					// 损益类收入
					" union select sum(c.fbendbal) * (-1) as JE from (select (case when A.fkmh is null then b.facctcode else A.fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else A.fcyid end) as fcurcode,NVL(b.fendbal, 0) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,(case when b.fbendbal is null then 0 else b.fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,"
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch")
					+ " where fterm = "
					+ YssFun.formatDate(YssFun.toDate(this.endDate), "MM")
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(YssFun.toDate(this.endDate), "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,"
					+ " fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "Lbalance")
					+ " where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount")
					+ " where FAcctcode like '6%' and FAcctcode not like '6101%' and fbalDC in (-1, -1, -1)) d on c.kmdm = d.facctcode  "
					+
					// 损益类费用
					" union select sum(c.fbendbal)*(-1) as je from (select (case when A.fkmh is null then b.facctcode else A.fkmh end) as kmdm,(case when A.fcyid is null then b.fcurcode else A.fcyid end) as fcurcode,(case when b.fendbal is null then 0 else b.fendbal end) + (case when fjje is null then 0 else fjje end) - (case when fdje is null then 0 else fdje end) as fendbal,(case when b.fbendbal is null then 0 else b.fbendbal end) + (case when fbjje is null then 0 else fbjje end) - (case when fbdje is null then 0 else fbdje end) as fbendbal,(case when b.faendbal is null then 0 else b.faendbal end) + (case when fjsl is null then 0 else fjsl end) - (case when fdsl is null then 0 else fdsl end) as faendbal "
					+ " from (select fkmh,fcyid,fterm,sum(case when fjd = 'J' then fbal else 0 end) as fjje,sum(case when fjd = 'D' then fbal else 0 end) as fdje,sum(case when fjd = 'J' then fsl else 0 end) as fjsl,sum(case when fjd = 'D' then fsl else 0 end) as fdsl,sum(case when fjd = 'J' then fbbal else 0 end) as fbjje,"
					+ " sum(case when fjd = 'D' then fbbal else 0 end) as fbdje,sum(case when fjd = 'J' then fbsl else 0 end) as fbjsl,sum(case when fjd = 'D' then fbsl else 0 end) as fbdsl from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "fcwvch")
					+ " where fterm = "
					+ YssFun.formatDate(YssFun.toDate(this.endDate), "MM")
					+ " and "
					+ dbl.sqlDay("FDate")
					+ " <= "
					+ YssFun.formatDate(YssFun.toDate(this.endDate), "dd")
					+ " group by fkmh, fcyid, fterm) a full join (select facctcode,"
					+ " fmonth,fcurcode,faccdebit,facccredit,fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faendbal from "
					+  fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "Lbalance")
					+ " where fmonth = "
					+ iniMonth
					+ " and fisdetail = 1) b on a.fkmh = b.facctcode and a.fcyid = b.fcurcode) c join (select facctcode, facctname, famount, facctattr, FAcctClass from "
					+ fc.getCWTabName(portCode, YssFun.toSqlDate(startDate), "LAccount")
					+ " where FAcctcode like '6%' and fbalDC in (1, 1, 1)) d on c.kmdm = d.facctcode  "
					+ ")";
			rs = dbl.queryByPreparedStatement(sql); 
			if (rs.next()) {
				dReturn = rs.getDouble("FMoney");
			}
			return dReturn;
        } catch (Exception e) {
            throw new YssException("获取期末已实现出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getPortCode() {
        return portCode;
    }
    //20110601 modified by liubo 获取财务估值表中使用的套账代码
    public String getPortID() {
    	return portID;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }
    //20110601 modified by liubo 设置财务估值表中使用的套账代码
    public void setPortID(String portID){
    	this.portID = portID;
    }
}
