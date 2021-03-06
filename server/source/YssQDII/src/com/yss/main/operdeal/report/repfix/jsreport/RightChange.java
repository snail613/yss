package com.yss.main.operdeal.report.repfix.jsreport;

import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.util.YssException;
import com.yss.dsub.BaseBean;
import java.sql.ResultSet;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssFun;
import com.yss.util.YssD;

/**
 * <p>Title: 权益变动</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author sj
 * @version 1.0
 */
public class RightChange
    extends BaseBuildCommonRep {
    private String sPortCode = null; //组合代码
    private String beginDate = null; //期初日期
    private String endDate = null; //期末日期
    private String sCuryCode = null; //货币代码
    private double dimmRate; //即时汇率
    private String sPrefixTableName = null; //表前缀
    private String strLset = null; //套帐号

    private FixPub fixPub = null;
    private CommonRepBean repBean = null;

    private int QCMonth; //期初月份
    private int QMMonth; //期末月份

    private double enSellAssetChange; //可供出售金融资产公允价值变动额
    private double fairChangeIncome; // 公允价值变动收益
    private double EnSellFairValueQC; //可供出售资产公允价值变动期初余额
    private double UnDivideIncomeQC; //未分配收益期初余额
    private double DepositaryFundQC; //受托社保基金期初余额
    private double runRealized; //已实现收益
    private double currentFundCh; //计算本期基金划入、划出产生的基金权益变动额
    private double currentFundRunRightChange; //计算本期基金运营活动产生的权益变动额

    StringBuffer finBuf = null; //最终返回的数据
    public RightChange() {
    }

    /**
     * initBuildReport
     * 初始化参数
     * @param bean BaseBean
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        fixPub = new FixPub();
        fixPub.setYssPub(pub);
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        beginDate = reqAry[0].split("\r")[1]; //期初日期
        endDate = reqAry[1].split("\r")[1]; //期末日期
        sPortCode = reqAry[2].split("\r")[1]; //组合代码
        sCuryCode = reqAry[3].split("\r")[1]; //币种代码
        if (sCuryCode.length() > 0 && sCuryCode.equalsIgnoreCase("CNY")) { //若为人民币，则获取即时汇率
            this.getimmRate();
        }
        getLset();
        getPrefixTableName();
        QCMonth = this.getMonth(this.beginDate);
        QMMonth = this.getMonth(this.endDate);
    }

    /**
     * buildReport
     * 组装成字符串，传到前台
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        finBuf = new StringBuffer();
        calcFundRightQC(); //计算基金权益期初余额
        currentFundCh = calcCurrentFundCh(); //计算本期基金划入、划出产生的基金权益变动额
        currentFundRunRightChange = calcCurrentFundRunRightChange(); //计算本期基金运营活动产生的权益变动额
        calcCurrentRightChangeValue(currentFundCh, currentFundRunRightChange); //计算本期基金权益变动额
        calcFundQM(); //计算基金权益期末余额
        if (finBuf.length() > 2) {
            return finBuf.toString().substring(0,
                                               finBuf.toString().length() - 2);
        } else {
            return "";
        }
    }

    /**
     * 计算基金权益期初余额
     * @return double
     * @throws YssException
     */
    private void calcFundRightQC() throws YssException {
//      StringBuffer bufItem = new StringBuffer();
        double FundRightQC = 0D; //基金权益期初余额
        try {
            DepositaryFundQC = calcDepositaryFundQC(); //受托社保基金期初余额
            EnSellFairValueQC = calcEnSellFairValueQC(); //可供出售资产公允价值变动期初余额
            UnDivideIncomeQC = calcUnDivideIncomeQC(); //未分配收益期初余额
            FundRightQC = EnSellFairValueQC + UnDivideIncomeQC + DepositaryFundQC;
        } catch (YssException ex) {
            throw new YssException("计算基金权益期初余额出现异常！", ex);
        }
        spellShowItemBuffer("一、基金权益期初余额", FundRightQC);

        spellShowItemBuffer(".   其中：受托社保基金期初余额", DepositaryFundQC);

        spellShowItemBuffer(".         可供出售资产公允价值变动期初余额", EnSellFairValueQC);

        spellShowItemBuffer(".         未分配收益期初余额", UnDivideIncomeQC);

    }

    /**
     * 计算本期基金划入、划出产生的基金权益变动额
     * @return double 返回划入、划出汇总数
     * @throws YssException
     */
    private double calcCurrentFundCh() throws YssException {
//      StringBuffer bufItem = new StringBuffer();
        double fundOut = 0D;
        double fundIn = 0D;
        double fundAll = 0D;
        try {
            fundOut = this.calcCurrentFundOut(); //本期基金划出
            fundIn = this.calcCurrentFundIn(); //本期基金划入
            fundAll = fundIn + fundOut; //划入 + 划出
        } catch (YssException e) {
            throw new YssException("计算本期基金划入、划出产生的基金权益变动额出现异常!", e);
        }
        spellShowItemBuffer("二、本期基金划入、划出产生的基金权益变动额", fundAll);

        spellShowItemBuffer(".   本期划入", fundIn);

        spellShowItemBuffer(".   本期划出", fundOut);

        return fundAll;
    }

    /**
     * 计算本期基金运营活动产生的权益变动额
     * @return double 返回本期基金运营活动产生的权益变动额
     * @throws YssException
     */
    private double calcCurrentFundRunRightChange() throws YssException {
//      StringBuffer bufItem = new StringBuffer();
        double currentFundRunRightChange = 0D;
        double EnSellAssetChange = 0D;
        double FundFairChangeIncome = 0D;
        double FundRunRealized = 0D;
        try {
            FundRunRealized = calcFundRunRealized(); //已实现收益
            FundFairChangeIncome = calcFundFairChangeIncome(); //公允价值变动收益
            EnSellAssetChange = calcEnSellAssetChange(); //可供出售金融资产公允价值变动额
            currentFundRunRightChange = EnSellAssetChange + FundFairChangeIncome +
                FundRunRealized;
        } catch (YssException e) {
            throw new YssException("计算本期基金运营活动产生的权益变动额出现异常!", e);
        }
        spellShowItemBuffer("三、本期基金运营活动产生的权益变动额", currentFundRunRightChange);

        spellShowItemBuffer(".   已实现收益", FundRunRealized);

        spellShowItemBuffer(".   公允价值变动收益", FundFairChangeIncome);

        spellShowItemBuffer(".   可供出售金融资产公允价值变动额", EnSellAssetChange);

        return currentFundRunRightChange;
    }

    /**
     * 计算本期基金权益变动额
     * @param currentFundCh double 本期基金划入、划出产生的基金权益变动额
     * @param CurrentFundRunRightChange double 计算本期基金运营活动产生的权益变动额
     */
    private void calcCurrentRightChangeValue(double currentFundCh,
                                             double CurrentFundRunRightChange) throws
        YssException {
        double currentRightChangeValue = 0D;
//      StringBuffer bufItem = new StringBuffer();
        currentRightChangeValue = YssD.add(CurrentFundRunRightChange,
                                           currentFundCh);
        spellShowItemBuffer("四、本期基金权益变动额", currentRightChangeValue);
    }

    /**
     * 计算基金权益期末余额
     * @throws YssException
     */
    private void calcFundQM() throws YssException {
        double calcFundQM = 0D;
        double DepositaryFundQM = calcDepositaryFundQM(); //受托社保基金期末余额
        double EnSellAssetQM = calcEnSellAssetQM(); //供出售金融资产公允价值期末余额
        double unDivideIncomeQM = calcUnDivideIncomeQM();
        calcFundQM = DepositaryFundQM + EnSellAssetQM + unDivideIncomeQM; //托社保基金期末余额 + 供出售金融资产公允价值期末余额 + 未分配收益期末余额
        try {
            spellShowItemBuffer("五、基金权益期末余额", calcFundQM);
            spellShowItemBuffer(".   其中：受托社保基金期末余额", DepositaryFundQM);
            spellShowItemBuffer(".         可供出售金融资产公允价值期末余额", EnSellAssetQM);
            spellShowItemBuffer(".         未分配收益期末余额", unDivideIncomeQM);
        } catch (YssException ex) {
            throw new YssException("计算基金权益期末余额出现异常!", ex);
        }
    }

    /**
     * 计算未分配收益期末余额
     * @return double
     */
    private double calcUnDivideIncomeQM() {
        double unDivideIncomeQM = 0D;
        unDivideIncomeQM = UnDivideIncomeQC + runRealized + fairChangeIncome +
            enSellAssetChange; //未分配收益期初余额 + 已实现收益 + 公允价值变动收益 + 可供出售金融资产公允价值变动额
        return unDivideIncomeQM;
    }

    /**
     * 计算供出售金融资产公允价值期末余额
     * @return double
     */
    private double calcEnSellAssetQM() {
        double EnSellAssetQM = 0D;
        EnSellAssetQM = YssD.add(EnSellFairValueQC, enSellAssetChange); //可供出售资产公允价值变动期初余额 + 可供出售金融资产公允价值变动额
        return EnSellAssetQM;
    }

    /**
     * 计算受托社保基金期末余额
     * @return double
     */
    private double calcDepositaryFundQM() {
        double DepositaryFundQM = 0D;
        DepositaryFundQM = YssD.add(DepositaryFundQC, currentFundCh); //受托社保基金期初余额 + 计算本期基金划入、划出产生的基金权益变动额
        return DepositaryFundQM;
    }

    /**
     * 获取可供出售金融资产公允价值变动额
     * @return double
     * @throws YssException
     */
    private double calcEnSellAssetChange() throws YssException {
        enSellAssetChange = 0D;
        return enSellAssetChange;
    }

    /**
     * 获取公允价值变动收益(本期基金运营活动产生的权益变动额)
     * @return double
     * @throws YssException
     */
    private double calcFundFairChangeIncome() throws YssException {
        double fairChangeIncome410302 = 0D;
        double fairChangeIncome410402 = 0D;
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        try {
            sqlBuf = new StringBuffer();
            sqlBuf.append("select ");
            sqlBuf.append(
                "sum(case when FJD = 'D' then FBBal else -1*FBBal end) as FBBal from "); //本期贷方发发生额－本期借方发发生额
            sqlBuf.append(sPrefixTableName + "FCWVCH ");
            sqlBuf.append(" where FKMH = '410302' "); //302类型
            sqlBuf.append(" and FTerm in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")");
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                fairChangeIncome410302 = rs.getDouble("FBBal");
            }
            dbl.closeResultSetFinal(rs);
            sqlBuf.delete(0, sqlBuf.length());
            //*********************************************************************//
            sqlBuf = new StringBuffer();
            sqlBuf.append("select ");
            sqlBuf.append(
                "sum(case when FJD = 'D' then FBBal else -1*FBBal end) as FBBal from "); //本期贷方发发生额－本期借方发发生额
            sqlBuf.append(sPrefixTableName + "FCWVCH ");
            sqlBuf.append(" where FKMH = '410402' "); //402类型
            sqlBuf.append(" and FTerm in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")");
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                fairChangeIncome410402 = rs.getDouble("FBBal");
            }
            //***************************************************
             fairChangeIncome = fairChangeIncome410302 + fairChangeIncome410402; //302 + 402类型
            //***************************************************
             if (this.dimmRate != 0) {
                 fairChangeIncome = YssFun.roundIt(fairChangeIncome / this.dimmRate,
                     2); //保留两位小数
             }
        } catch (Exception e) {
            throw new YssException("获取公允价值变动收益出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return fairChangeIncome;
    }

    /**
     * 计算已实现收益(本期基金运营活动产生的权益变动额)
     * @return double
     * @throws YssException
     */
    private double calcFundRunRealized() throws YssException {
        double runRealized410401 = 0D;
        double runRealized410301 = 0D;
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        try {
            sqlBuf = new StringBuffer();
            sqlBuf.append("select ");
            sqlBuf.append(
                "sum(case when FJD = 'D' then FBBal else -1*FBBal end) as FBBal from "); //本期贷方发发生额－本期借方发发生额
            sqlBuf.append(sPrefixTableName + "FCWVCH ");
            sqlBuf.append(" where FKMH = '410301' "); //301类型
            sqlBuf.append(" and FTerm in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")");
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                runRealized410301 = rs.getDouble("FBBal");
            }
            dbl.closeResultSetFinal(rs);
            sqlBuf.delete(0, sqlBuf.length());
            //*********************************************************************//
            sqlBuf = new StringBuffer();
            sqlBuf.append("select ");
            sqlBuf.append(
                "sum(case when FJD = 'D' then FBBal else -1*FBBal end) as FBBal from "); //本期贷方发发生额－本期借方发发生额
            sqlBuf.append(sPrefixTableName + "FCWVCH ");
            sqlBuf.append(" where FKMH = '410401' "); //401类型
            sqlBuf.append(" and FTerm in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")");
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                runRealized410401 = rs.getDouble("FBBal");
            }
            //***************************************************
             runRealized = runRealized410301 + runRealized410401; //301 + 401类型的数据
            //***************************************************
             if (this.dimmRate != 0) {
                 runRealized = YssFun.roundIt(runRealized / this.dimmRate, 2); //保留两位小数
             }
        } catch (Exception e) {
            throw new YssException("获取已实现收益出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return runRealized;
    }

    /**
     * 获取本期基金划出,4001科目本期借方发发生额
     * @return double
     * @throws YssException
     */
    private double calcCurrentFundOut() throws YssException {
        double FundOut = 0D; //本期基金划出
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        try {
            sqlBuf = new StringBuffer();
            sqlBuf.append("select sum(FBal) as FBal,sum(FBBal) as FBBal from ");
            sqlBuf.append(sPrefixTableName + "FCWVCH ");
            sqlBuf.append(" where FJD = 'J' and FKMH = '40010101' ");
            sqlBuf.append(" and FTerm in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")");
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                FundOut += rs.getDouble("FBBal");
            }
            if (this.dimmRate != 0) {
                FundOut = YssFun.roundIt(FundOut / this.dimmRate, 2); //保留两位小数
            }
        } catch (Exception e) {
            throw new YssException("获取受托社保基金期初余额出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

        return FundOut;
    }

    /**
     * 获取本期基金划入,4001科目本期贷方发发生额
     * @return double
     * @throws YssException
     */
    private double calcCurrentFundIn() throws YssException {
        double FundIn = 0D; //本期基金划入
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        try {
            sqlBuf = new StringBuffer();
            sqlBuf.append("select sum(FBal) as FBal,sum(FBBal) as FBBal from ");
            sqlBuf.append(sPrefixTableName + "FCWVCH ");
            sqlBuf.append(" where FJD = 'D' and FKMH = '40010101' ");
            sqlBuf.append(" and FTerm in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")");
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                FundIn += rs.getDouble("FBBal");
            }
            if (this.dimmRate != 0) {
                FundIn = YssFun.roundIt(FundIn / this.dimmRate, 2); //保留两位小数
            }
        } catch (Exception e) {
            throw new YssException("获取受托社保基金期初余额出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return FundIn;
    }

    /**
     * 获取可供出售资产公允价值变动期初余额
     * @return double
     * @throws YssException
     */
    private double calcEnSellFairValueQC() throws YssException {
        double QC = 0D; //可供出售资产公允价值变动期初余额
        return QC; //现默认为0；
    }

    /**
     * 获取未分配收益期初余额，获取科目(4103) + (4104)的值
     * @return double
     * @throws YssException
     */
    private double calcUnDivideIncomeQC() throws YssException {
        double QC = 0D; //未分配收益期初余额
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        try {
            sqlBuf = new StringBuffer();
            sqlBuf.append("select FMonth,a.FAcctCode,FStartBal,FBStartBal,FBalDC from ");
            sqlBuf.append(sPrefixTableName + "LBALANCE a ");
            sqlBuf.append(" left join ");
            sqlBuf.append(sPrefixTableName + "LAccount b on upper(a.FAcctCode) = upper(b.FAcctCode) ");
            sqlBuf.append(" where upper(a.FAcctCode) in ('4103','4104') ");
            sqlBuf.append(" and FMonth = ");
            sqlBuf.append(this.QCMonth);
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                QC += rs.getDouble("FBStartBal") * rs.getDouble("FBalDC"); // 本位币	期初余额(美元)
            }
            if (this.dimmRate != 0) {
                QC = YssFun.roundIt(QC / this.dimmRate, 2); //保留两位小数
            }
        } catch (Exception e) {
            throw new YssException("获取受托社保基金期初余额出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return QC;
    }

    /**
     * 获取受托社保基金期初余额，获取科目为(4001)
     * @return double
     * @throws YssException
     */
    private double calcDepositaryFundQC() throws YssException {
        double QC = 0D; //受托社保基金期初余额
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        try {
            sqlBuf = new StringBuffer();
            sqlBuf.append("select FMonth,a.FAcctCode,FStartBal,FBStartBal,FBalDC from ");
            sqlBuf.append(sPrefixTableName + "LBALANCE a ");
            sqlBuf.append(" left join ");
            sqlBuf.append(sPrefixTableName + "LAccount b on upper(a.FAcctCode) = upper(b.FAcctCode) ");
            sqlBuf.append(" where upper(a.FAcctCode) = '4001' ");
            sqlBuf.append(" and FMonth = ");
            sqlBuf.append(this.QCMonth);
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                QC = rs.getDouble("FBStartBal") * rs.getDouble("FBalDC"); //本位币期初余额(美元)
            }
            if (this.dimmRate != 0) {
                QC = YssFun.roundIt(QC / this.dimmRate, 2); //保留两位小数
            }
        } catch (Exception e) {
            throw new YssException("获取受托社保基金期初余额出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return QC;
    }

    /**
     * 拼装向前台传递数据的StringBuffer
     * @param itemShowMessage String 项目名称
     * @param bal double 金额
     * @throws YssException
     */
    private void spellShowItemBuffer(String itemShowMessage, double bal) throws
        YssException {
        StringBuffer bufItem = new StringBuffer(); //每条记录的buf
        bufItem.append(itemShowMessage).append(",");
        bufItem.append(bal);
        finBuf.append(fixPub.buildRowCompResult(bufItem.toString(),
                                                "DS_RightChange")).append( //为报表数据源代码
            "\r\n");
        bufItem = null;
    }

    /**
     * 串接空字符串，有空字符串的地方直接调用此函数
     * @throws YssException
     * @return StringBuffer
     */
    private StringBuffer buildEmpty() throws YssException {
        StringBuffer strEmpty = new StringBuffer();
        try {
            for (int i = 0; i <= 1; i++) { //循环插入空字符窜,现为两项
                strEmpty.append(" ").append(",");
            }
            return strEmpty;
        } catch (Exception e) {
            throw new YssException("串接空字符串出错!", e);
        }
    }

    /**
     * 拼装跨月份的字符窜,如4,5,6
     * @return String 拼装的月份字符
     */
    private String getAllPeriodFromMonth() {
        String currentMonth = "";
        int months = this.QMMonth - this.QCMonth;
        int cirmonth = 0;
        for (int i = 0; i <= months; i++) {
            cirmonth = this.QCMonth + i;
            currentMonth += cirmonth + ",";
        }
        if (currentMonth.length() > 1) {
            currentMonth = currentMonth.substring(0, currentMonth.length() - 1);
        }
        return currentMonth;
    }

    /**
     * 获取月份信息
     * @param Date String 具体日期
     * @return int
     * @throws YssException
     */
    private int getMonth(String Date) throws YssException {
        int month = 0;
        if (YssFun.isDate(Date)) {
            month = YssFun.getMonth(YssFun.parseDate(Date)); //获取月份
        }
        return month;
    }

    /**
     * 获取财务套表前缀
     * @throws YssException
     */
    private void getPrefixTableName() throws YssException {
        try {
            this.sPrefixTableName = "A" +
                (YssFun.getYear(YssFun.toDate(this.beginDate))) + strLset;
        } catch (YssException ex) {
            throw new YssException("获取财务套表前缀出现异常！", ex);
        }
    }

    /**
     * 获取套帐号
     * @throws YssException
     */
    private void getLset() throws YssException {
        {
            StringBuffer sqlBuf = null;
            ResultSet rs = null;
            try {
                sqlBuf = new StringBuffer();
                sqlBuf.append("select distinct fsetcode from (select * from ");
                sqlBuf.append(pub.yssGetTableName("tb_para_portfolio"));
                sqlBuf.append(" where fportcode = ");
                sqlBuf.append(dbl.sqlString(sPortCode));
                sqlBuf.append(
                    " ) a  join (select * from Lsetlist) b on a.fassetcode = b.fsetid");
                rs = dbl.openResultSet(sqlBuf.toString());
                while (rs.next()) {
                    strLset = rs.getString("fsetcode");
                    strLset = "00" + strLset; //加上一个001，以便生成表名时使用
                }
            } catch (Exception e) {
                throw new YssException("获取套帐号出错！", e);
            } finally {
                dbl.closeResultSetFinal(rs);
            }
        }
    }

    /**
     * 获取即期汇率
     * @throws YssException
     */
    private void getimmRate() throws YssException {
        StringBuffer strBuf = null;
        ResultSet rs = null;
        try {
            strBuf = new StringBuffer();
            strBuf.append("select fexrate1 as FimmRate from ");
            strBuf.append(pub.yssGetTableName("tb_data_exchangerate"));
            strBuf.append(" where fexratedate = ( select max(fexratedate) from ");
            strBuf.append(pub.yssGetTableName("tb_data_exchangerate"));
            strBuf.append(" where fexratedate<=" + dbl.sqlDate(endDate));
            strBuf.append(" and fcurycode='CNY' )");
            strBuf.append(" and fcurycode='CNY'");
            rs = dbl.openResultSet(strBuf.toString());
            while (rs.next()) {
                this.dimmRate = rs.getDouble("FimmRate");
            }
        } catch (Exception e) {
            throw new YssException("获取即期汇率的数出错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
