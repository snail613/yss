package com.yss.main.operdeal.report.repfix;

import com.yss.main.operdeal.report.BaseBuildCommonRep;

import com.yss.dsub.*;

import com.yss.main.report.*;
import com.yss.main.report.CommonRepBean;
import com.yss.main.operdata.*;
import com.yss.util.YssException;
import com.yss.pojo.param.comp.*;
import java.util.*;
import com.yss.main.compliance.*;
import java.sql.*;

import com.yss.main.operdeal.report.*;
import com.yss.util.*;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.parasetting.SecurityBean;

/**
 *
 * <p>Title:交银施罗德 - 咨询顾问费报表 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: YSSTECH</p>
 * @author 陈嘉,周述晟
 * @version 1.0
 */
public class ConsultantFee
    extends BaseBuildCommonRep {
    private CommonRepBean repBean;
    private String startDate = ""; //期初日期
    private String endDate = ""; //期末日期
    private String portCode = ""; //组合代码
    private String holidayCode = ""; //节假日代码
    private String kmCodesIgn = ""; //不计的特殊科目号
    private String accCodes = ""; //不计入资产的现金账户代码

    private static final double FEERATE = 0.0057; //费率
    private FixPub fixPub = null;
    public ConsultantFee() {
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
        fixPub = new FixPub();
        fixPub.setYssPub(pub);
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        for (int i = 0; i < reqAry.length; i++) {
            int paraIndex = Integer.parseInt(reqAry[i].split("\r")[0]);
            switch (paraIndex) {
                case 1: {
                    startDate = reqAry[i].split("\r")[1]; //期初日期
                    break;
                }
                case 2: {
                    endDate = reqAry[i].split("\r")[1]; //期末日期
                    break;
                }
                case 3: {
                    portCode = reqAry[i].split("\r")[1]; //组合代码
                    break;
                }
                case 4: {
                    holidayCode = reqAry[i].split("\r")[1]; //节假日代码
                    break;
                }
                case 5: {
                    kmCodesIgn = reqAry[i].split("\r")[1]; //科目号
                    break;
                }
                case 6: {
                    accCodes = reqAry[i].split("\r")[1]; //现金账户代码
                    break;
                }
            }

        }
    }

    protected String buildShowData() throws //报表的显示数据
        YssException {
        StringBuffer finBuf = new StringBuffer(); //最终返回的数据
        double totalFee = 0; //合计费用
        //java.util.Date date
        int days = YssFun.dateDiff(YssFun.toSqlDate(startDate),
                                   YssFun.toSqlDate(endDate));
        for (int i = 0; i <= days; i++) {
            //每天的日期
            java.util.Date date = YssFun.addDay(YssFun.toSqlDate(startDate), i);
            //对应有资产净值的工作日
            java.util.Date latestWorkDay = getWorkDay(holidayCode,
                YssFun.addDay(YssFun.toSqlDate(
                    startDate), i + 1), -1);
            //该工作日的资产净值和投资咨询顾问费
            int dayOfTheYear = (YssFun.isLeapYear(latestWorkDay) ? 366 :
                                365);
            double assetsTotal = getAssetsTotal(latestWorkDay); //取得资产合计
            double liabilityTotal = getLiabilityTotal(latestWorkDay); //负债类合计
            double rateTradeYingShou = getRateTradeYingShou(latestWorkDay, accCodes); //其它币种与人民币或港币兑换时外汇交易产生的应收
            double rateTradeYingFu = getRateTradeYingFu(latestWorkDay, accCodes); //其它币种与人民币或港币兑换时外汇交易产生的应付
            double kmTotal = getKmTotal(latestWorkDay, kmCodesIgn); //忽略的科目总金额,

            double total = assetsTotal + liabilityTotal - rateTradeYingShou +
                rateTradeYingFu - kmTotal; //总资产

            double settleMoneyTotal = getSettleMoneyAssetTotal(latestWorkDay); //所有清算款合计
            double settleMoney = getSettleMoney(latestWorkDay, accCodes); //应计入资产的清算金额

            double accTotal = getAccTotal(latestWorkDay, accCodes);

            total = total - settleMoneyTotal + settleMoney - accTotal;

            double fee = total * FEERATE / dayOfTheYear;

            totalFee = totalFee + YssFun.roundIt(fee, 2); //将每天费用加入到合计中

            StringBuffer rowBuf = new StringBuffer(); //每行数据

            rowBuf.append(YssFun.formatDate(date, "yyyy-MM-dd")).append(",");
            rowBuf.append(YssFun.roundIt(total, 2)).append(",");
            rowBuf.append(YssFun.roundIt(fee, 2)).append(",");

            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_ConsultantFee")).append(
                    "\r\n");
        }

        StringBuffer rowBuf = new StringBuffer(); //每行数据

        rowBuf.append("合计：,");
        rowBuf.append(",");
        rowBuf.append(YssFun.roundIt(totalFee, 2)).append(",");

        finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                                                "DS_ConsultantFee")).append("\r\n");

        try {
            if (finBuf.toString().length() > 2) {
                return finBuf.toString().substring(0, finBuf.toString().length() - 2);
            } else {
                return "";
            }
        } catch (Exception e) {
            throw new YssException("统计合计费用出错", e);
        }
    }

    /**
     * getWorkDay
     *
     * @param HolidaysCode String   //节假日群代码
     * @dDate Date                     //传入日期
     * @lOffset int                    //传入延迟天数
     * @return Date
     */
    private java.util.Date getWorkDay(String getHolidaysCode,
                                      java.util.Date dDate, int lOffset) throws
        YssException {
        SecurityBean secBean = new SecurityBean();
        secBean.setYssPub(pub);
        secBean.getSetting();
        String strSql = "";
        ResultSet rs = null;
        int lTmp = 0, lStep;
        try {
            lStep = (lOffset < 0) ? -1 : 1;
            strSql =
                "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = " +
                dbl.sqlString(getHolidaysCode) + " and FDate " +
                ( (lOffset < 0) ? "<=" : ">=")
                + dbl.sqlDate(dDate, false) + " order by FDate " +
                ( (lOffset < 0) ? " desc" : "");
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (rs.next()) {
                do {
                    if (YssFun.dateDiff(rs.getDate("FDate"), dDate) == 0) {
                        rs.next();
                        if (lTmp == 0) {
                            lTmp += lStep;
                        }
                    } else {
                        if (Math.abs(lTmp) >= Math.abs(lOffset)) {
                            break;
                        }
                        lTmp += lStep;
                    }
                    dDate = YssFun.addDay(dDate, lStep);
                } while (!rs.isAfterLast());
                if (rs.isAfterLast()) {
                    throw new YssException( ( (lOffset < 0) ? "上" : "下") +
                                           "一个工作日已经超越节假日的边界，请增加节假日定义！");
                }
            }
            return dDate;
        } catch (Exception e) {
            throw new YssException("访问节假日表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

//资产类合计
    private double getAssetsTotal(java.util.Date date) throws YssException {
        double assetsTotal = 0;
        ResultSet rs = null;
        String sql = "";
        try {
            sql =
                "select sum(a.fstandardmoneymarketvalue) as fstandardmoneymarketvalue from " +
                pub.yssGetTableName("tb_rep_guessvalue") + " a left join (select distinct(fsetid),fsetcode from  lsetlist) b on a.fportcode = b.fsetCode left join (select fportcode,fassetcode from " +
                pub.yssGetTableName("tb_para_portfolio") + ") c on  b.fsetid = c.fassetcode where a.facctcode = '8800' and a.fcurcode <> 'CNY' and a.fcurcode <> 'HKD' and a.fcurcode <> ' ' and a.fdate = " +
                dbl.sqlDate(date) + "and c.fportcode = '" + portCode + "'";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                assetsTotal = rs.getDouble("fstandardmoneymarketvalue");
            }
            return assetsTotal;
        } catch (Exception e) {
            throw new YssException("获取资产合计金额出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

//负载类合计
    private double getLiabilityTotal(java.util.Date date) throws YssException {
        double liabilityTotal = 0;
        ResultSet rs = null;
        String sql = "";
        try {
            sql =
                "select sum(a.fstandardmoneymarketvalue) as fstandardmoneymarketvalue from " +
                pub.yssGetTableName("tb_rep_guessvalue") + " a left join (select distinct(fsetid),fsetcode from  lsetlist) b on a.fportcode = b.fsetCode left join (select fportcode,fassetcode from " +
                pub.yssGetTableName("tb_para_portfolio") + ") c on  b.fsetid = c.fassetcode where a.facctcode = '8801' and a.fcurcode <> 'CNY' and a.fcurcode <> 'HKD' and a.fcurcode <> ' ' and a.fdate = " +
                dbl.sqlDate(date) + "and c.fportcode = '" + portCode + "'";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                liabilityTotal = rs.getDouble("fstandardmoneymarketvalue");
            }
            return liabilityTotal;
        } catch (Exception e) {
            throw new YssException("获取负债合计金额出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

//其它币种与人民币或港币兑换时外汇交易产生的应收
//需要排除的现金账户的应收在途资金不计在内，在后面专门计算账户资产时减掉
    private double getRateTradeYingShou(java.util.Date date, String accCodes) throws
        YssException {
        double rateTradeYingShou = 0;
        ResultSet rs = null;
        String codes[] = accCodes.split(",");
        String code = "";
        for (int i = 0; i < codes.length; i++) {
            code = code + ",'" + codes[i] + "'";
        }
        code = code.substring(1);

        String sql = "";
        try {
            sql = "select a.ftradedate,a.fbsettledate, a.fsettledate, a.FBCuryCode, a.fscurycode,a.FBMoney *  nvl(b.fexrate1,1) /(select fexrate1 from " +
                pub.yssGetTableName("tb_data_exchangerate") +
                " where fcurycode = 'CNY' and fexratedate = (select max(fexratedate) from " +
                pub.yssGetTableName("tb_data_exchangerate") + " where fexratedate<= " +
                dbl.sqlDate(date) + " and fcurycode = 'CNY'))as portMoney  from " +
                pub.yssGetTableName("tb_data_ratetrade") +
                " a left join ( select bb.fexrate1,bb.fcurycode from " +
                pub.yssGetTableName("tb_data_exchangerate") +
                "  bb where  bb.fexratedate = (select max(fexratedate) from " +
                pub.yssGetTableName("tb_data_exchangerate") + " where fexratedate<= " +
                dbl.sqlDate(date) + ")) b on a.fbcurycode = b.fcurycode where (a.FBCuryCode <> 'CNY' and a.FBCuryCode <> 'HKD' )and (a.fscurycode = 'CNY' OR a.fscurycode = 'HKD') and  " +
                dbl.sqlDate(date) + " >= a.ftradedate and (a.fbsettledate > " +
                dbl.sqlDate(date) + ") and a.fportcode = '" + portCode +
                "' and a.fbcashacccode not in (" + code + ") ";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                rateTradeYingShou = rs.getDouble("portmoney");
            }
            return rateTradeYingShou;
        } catch (Exception e) {
            throw new YssException("获取外汇交易应收金额出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

//其它币种与人民币或港币兑换时外汇交易产生的应付
//需要排除的现金账户的应收在途资金不计在内
    private double getRateTradeYingFu(java.util.Date date, String accCodes) throws
        YssException {
        double rateTradeYingFu = 0;
        ResultSet rs = null;
        String codes[] = accCodes.split(",");
        String code = "";
        for (int i = 0; i < codes.length; i++) {
            code = code + ",'" + codes[i] + "'";
        }
        code = code.substring(1);

        String sql = "";
        try {
            sql = "select a.ftradedate,a.fbsettledate, a.fsettledate, a.FBCuryCode, a.fscurycode,a.fsmoney *  nvl(b.fexrate1,1) /(select fexrate1 from " +
                pub.yssGetTableName("tb_data_exchangerate") +
                " where fcurycode = 'CNY' and fexratedate = (select max(fexratedate) from " +
                pub.yssGetTableName("tb_data_exchangerate") + " where fexratedate<= " +
                dbl.sqlDate(date) + " and fcurycode = 'CNY'))as portMoney  from " +
                pub.yssGetTableName("tb_data_ratetrade") +
                " a left join ( select bb.fexrate1,bb.fcurycode from " +
                pub.yssGetTableName("tb_data_exchangerate") +
                "  bb where  bb.fexratedate = (select max(fexratedate) from " +
                pub.yssGetTableName("tb_data_exchangerate") + " where fexratedate<= " +
                dbl.sqlDate(date) + ")) b on a.fbcurycode = b.fcurycode where (a.FBCuryCode = 'CNY' OR a.FBCuryCode = 'HKD' )and (a.fscurycode <> 'CNY' AND a.fscurycode <> 'HKD') and  " +
                dbl.sqlDate(date) + " >= a.ftradedate and (a.fsettledate > " +
                dbl.sqlDate(date) + ") and a.fportcode = '" + portCode +
                "' and a.fscashacccode not in (" + code + ") ";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                rateTradeYingFu = rs.getDouble("portmoney");
            }
            return rateTradeYingFu;
        } catch (Exception e) {
            throw new YssException("获取外汇交易应付金额出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //
    private double getKmTotal(java.util.Date date, String kmCodesIgn) throws
        YssException {
        double kmTotal = 0;
        ResultSet rs = null;
        String sql = "";
        String codes[] = kmCodesIgn.split(",");
        String code = "";
        for (int i = 0; i < codes.length; i++) {
            code = code + ",'" + codes[i] + "'";
        }
        code = code.substring(1);
        try {
            sql =
                "select sum(a.fstandardmoneymarketvalue) as fstandardmoneymarketvalue from " +
                pub.yssGetTableName("tb_rep_guessvalue") + " a left join (select distinct(fsetid),fsetcode from  lsetlist) b on a.fportcode = b.fsetCode left join (select fportcode,fassetcode from " +
                pub.yssGetTableName("tb_para_portfolio") + ") c on  b.fsetid = c.fassetcode where a.fcurcode <> 'CNY' and a.fcurcode <> 'HKD' and a.fcurcode <> ' ' and a.fdate = " +
                dbl.sqlDate(date) + "and c.fportcode = '" + portCode + "'" +
                "and a.facctcode in (" + code + ")";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                kmTotal = rs.getDouble("fstandardmoneymarketvalue");
            }
            return kmTotal;
        } catch (Exception e) {
            throw new YssException("获取科目对应余额出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取所有证券清算款合计
    //参数：最近的工作日
    private double getSettleMoneyAssetTotal(java.util.Date date) throws
        YssException {
        double settleMoneyTotal = 0;
        ResultSet rs = null;
        String sql = "";
        try {
            sql =
                "select sum(a.fstandardmoneymarketvalue) as settleMoneyTotal  from " +
                pub.yssGetTableName("tb_rep_guessvalue") + " a left join (select distinct (fsetid), fsetcode from lsetlist) b on a.fportcode = b.fsetCode left join (select fportcode, fassetcode from " +
                pub.yssGetTableName("tb_para_portfolio") + ") c on b.fsetid = c.fassetcode where a.facctcode = '3003' and  a.fcurcode <> 'CNY' and a.fcurcode <> 'HKD' and a.fcurcode <> ' ' and a.fdate = " +
                dbl.sqlDate(date) + " and c.fportcode =  '" + portCode + "'  ";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                settleMoneyTotal = rs.getDouble("settleMoneyTotal");
            }
            return settleMoneyTotal;
        } catch (Exception e) {
            throw new YssException("获取总结算金额出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //不计入资产的现金账户代码，资产包括银行存款，交易相关证券的市值，除清算款外的应收款项
    //参数：最近的工作日，账户代码
    private double getAccTotal(java.util.Date date, String accCodes) throws
        YssException {
        double accBalance = 0; //账户余额市值
        double secMaketValue = 0; //账户交易相关证券市值
        double otherRec = 0; //除清算款外的应收款项
        double otherPay = 0;

        accBalance = getAccBalance(date, accCodes);
        secMaketValue = getSecMaketValue(date, accCodes);
        otherRec = getOtherRec(date, accCodes);
        otherPay = getOtherPay(date, accCodes);
        return accBalance + secMaketValue + otherRec - otherPay;
    }

    //获取账户市值
    //参数：最近的工作日，账户代码
    private double getAccBalance(java.util.Date date, String accCodes) throws
        YssException {
        double accBalance = 0;
        ResultSet rs = null;
        String sql = "";
        String codes[] = accCodes.split(",");
        String code = "";
        for (int i = 0; i < codes.length; i++) {
            code = code + ",'" + codes[i] + "'";
        }
        code = code.substring(1);
        try {
            sql = "select sum(a.fportmarketvalue) as accBalance from " +
                pub.yssGetTableName("tb_data_navdata") +
                " a where  a.fretypecode = 'Cash' and a.fdetail = 0 and a.fnavdate = " +
                dbl.sqlDate(date) + " and a.fkeycode in (" + code +
                ") and a.fportcode = '" + portCode + "'";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                accBalance = rs.getDouble("accBalance");
            }
            return accBalance;
        } catch (Exception e) {
            throw new YssException("获取现金账户余额出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取证券市值
    //参数：最近的工作日，账户代码
    private double getSecMaketValue(java.util.Date date, String accCodes) throws
        YssException {
        double secMaketValue = 0;
        ResultSet rs = null;
        String sql = "";
        String codes[] = accCodes.split(",");
        String code = "";
        for (int i = 0; i < codes.length; i++) {
            code = code + ",'" + codes[i] + "'";
        }
        code = code.substring(1);
        try {
            sql = "select sum(b.fportmarketvalue) as secMaketValue from " +
                pub.yssGetTableName("tb_data_navdata") + " b where b.fretypecode = 'Security' and b.fdetail = 0 and b.fgradetype5 in (select distinct(a.fsecuritycode) from " +
                pub.yssGetTableName("tb_data_subtrade") +
                " a where a.fcashacccode in (" + code + ")) and b.fnavdate = " +
                dbl.sqlDate(date) + "and b.fportcode = '" + portCode + "'";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                secMaketValue = rs.getDouble("secMaketValue");
            }
            return secMaketValue;
        } catch (Exception e) {
            throw new YssException("获取现金账户相关证券市值出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取应计的清算款
    //参数：最近的工作日，账户代码
    private double getSettleMoney(java.util.Date date, String accCodes) throws
        YssException {
        double settleMoney = 0;
        ResultSet rs = null;
        String sql = "";
        String codes[] = accCodes.split(",");
        String code = "";
        for (int i = 0; i < codes.length; i++) {
            code = code + ",'" + codes[i] + "'";
        }
        code = code.substring(1);
        try {
            sql = "select sum (qingsuankuan) as settleMoney from(   select round(sum(a.ffactsettlemoney * d.fcashind *fbaserate/fportrate ),2)  as qingsuankuan  from " +
                pub.yssGetTableName("tb_data_subtrade") + " a  left join " +
                pub.yssGetTableName("tb_para_security") +
                " b on a.fsecuritycode = b.fsecuritycode  left join " +
                pub.yssGetTableName("tb_para_cashaccount") + " c on a.ffactcashacccode = c.fcashacccode   left join tb_base_tradetype d on a.ftradetypecode = d.ftradetypecode  left join (select fcurycode,fbaserate,fportrate from  " +
                pub.yssGetTableName("tb_data_valrate") + " where fvaldate = " +
                dbl.sqlDate(date) +
                ") e on c.fcurycode  = e.fcurycode  where a.fbargaindate <= " +
                dbl.sqlDate(date) + " and a.ffactsettledate > " + dbl.sqlDate(date) +
                " and a.ftradetypecode in ('01','02','17') and a.ffactcashacccode not in (" +
                code +
                ")  and c.fcurycode not in ('HKD','CNY') group by fexchangecode,fcurycode)  x ";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                settleMoney = rs.getDouble("settleMoney");
            }
            return settleMoney;
        } catch (Exception e) {
            throw new YssException("获取应收清算款出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取其他应收款
    //参数：最近的工作日，账户代码
    private double getOtherRec(java.util.Date date, String accCodes) throws
        YssException {
        double otherRec = 0;
        ResultSet rs = null;
        String sql = "";
        String codes[] = accCodes.split(",");
        String code = "";
        for (int i = 0; i < codes.length; i++) {
            code = code + ",'" + codes[i] + "'";
        }
        code = code.substring(1);
        try {
            sql = "select sum(a.fportmarketvalue) as otherRec  from " +
                pub.yssGetTableName("tb_data_navdata ") + " a  where  a.fretypecode = 'Cash' and a.fdetail = 0 and a.finout =1 and a.fnavdate = " +
                dbl.sqlDate(date) +
                //#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目 
                "and a.fgradetype6 is not null and a.fgradetype5 in (" + code +
                ")  and a.fportcode = '" + portCode + "' and a.fgradetype6 <> '06TD'";
               //#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目 
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                otherRec = rs.getDouble("otherRec");
            }
            return otherRec;
        } catch (Exception e) {
            throw new YssException("获取应收款项出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取其他应付款
    //参数：最近的工作日，账户代码
    private double getOtherPay(java.util.Date date, String accCodes) throws
        YssException {
        double otherPay = 0;
        ResultSet rs = null;
        String sql = "";
        String codes[] = accCodes.split(",");
        String code = "";
        for (int i = 0; i < codes.length; i++) {
            code = code + ",'" + codes[i] + "'";
        }
        code = code.substring(1);
        try {
            sql = "select sum(a.fportmarketvalue) as otherPay  from " +
                pub.yssGetTableName("tb_data_navdata ") + " a  where  a.fretypecode = 'Cash' and a.fdetail = 0 and a.finout =-1 and a.fnavdate = " +
                dbl.sqlDate(date) +
                "and a.fgradetype5 is not null and a.fgradetype4 in (" + code +
                ")  and a.fportcode = '" + portCode + "' and a.fgradetype5 <> '07TD'";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                otherPay = rs.getDouble("otherPay");
            }
            return otherPay;
        } catch (Exception e) {
            throw new YssException("获取应付款项出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
