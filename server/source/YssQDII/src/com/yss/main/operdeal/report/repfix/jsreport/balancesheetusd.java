package com.yss.main.operdeal.report.repfix.jsreport;

import com.yss.dsub.*;

import com.yss.main.report.*;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import java.util.*;
import java.sql.*;

import com.yss.main.operdeal.report.*;
import com.yss.util.*;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;

/**
 *
 * <p>Title: 博时资产负债表（USD）</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company:YSSTECH </p>
 * <p>Author:陈嘉
 * @author not attributable
 * @version 1.0
 */
public class balancesheetusd
    extends BaseBuildCommonRep {
    StringBuffer finBuf = new StringBuffer(); //最终返回的数据
    StringBuffer rowBuf = null; //每一行数据
    private String startDate = ""; //期初日期
    private String endDate = ""; //期末日期
    private String strLset = ""; //套帐号
    private String portCode = ""; //组合代码
    private String curyCode = ""; //币种代码
    private String holidayCode = ""; //节假日群代码
    private double davgRate = 1; //当期平均汇率
    private double dimmRate = 1; //即期汇率
    private double dNCrepCe = 0; //年初的报表折算差额
    private double dQMrepCe = 0; //期末的报表折算差额
    int year;
    private String sTabpre = ""; //表名的半部分 如：A2008001

    /********证券清算款****************/
    double dNcYsSettleM = 0;
    double dQmYsSettleM = 0;
    double dNcYfSettleM = 0;
    double dQmYfSettleM = 0;

    /********获取每一个子项的数据***************/
    double dNcCost = 0;
    double dNcMarketValue = 0;
    double dQmCost = 0;
    double dQmMarketValue = 0;

    /***********************/

    /********获取资产总值的数据，是一个累加值***************/
    double dNcCostZc = 0;
    double dNcMarketValueZc = 0;
    double dQmCostZc = 0;
    double dQmMarketValueZc = 0;

    /***********************/
    /********获取负债总值的数据，是一个累加值***************/
    double dNcCostFz = 0;
    double dNcMarketValueFz = 0;
    double dQmCostFz = 0;
    double dQmMarketValueFz = 0;

    /***********************/
    /********获取受托社保基金权益合计，是一个累加值***************/
    double dNcCostQy = 0;
    double dNcMarketValueQy = 0;
    double dQmCostQy = 0;
    double dQmMarketValueQy = 0;

    /***********************/

    /********获取其它负债合计，是一个累加值***************/
    double dNcCostOfz = 0;
    double dNcMarketValueQfz = 0;
    double dQmCostQfz = 0;
    double dQmMarketValueQfz = 0;

    /***********************/

    private FixPub fixPub = null;
    private CommonRepBean repBean;
    public balancesheetusd() {
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
        endDate = reqAry[0].split("\r")[1]; //期末日期
        portCode = reqAry[1].split("\r")[1]; //组合代码
        curyCode = reqAry[2].split("\r")[1]; //币种代码
        holidayCode = reqAry[3].split("\r")[1]; //节假日群代码
        if (curyCode.equalsIgnoreCase("CNY")) { //当是人民币的时候，由USD折成CNY
            getimmRate(); //获取即期汇率
            getAvgRate(); //获取平均汇率
        }
        year = YssFun.getYear(YssFun.toDate(endDate)); //获取当期的年份
        startDate = year + "-12-31"; //获取当期的年初值时，取的日期是前一年的最后一天
        getLset(); //根据组合获得套帐号

    }

    /**
     * buildReport
     * 组装成字符串，传到前台
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        try {
            rowBuf = new StringBuffer(); //每行数据
            rowBuf.append("银行存款").append(",");
            getNcCost("1002"); //获取银行存款期初的成本，市值
            getQmMarketValue("1002");
            dNcCostZc = dNcCostZc + dNcCost;
            dNcMarketValueZc = dNcMarketValueZc + dNcMarketValue;
            this.dQmCostZc = dQmCostZc + dQmCost;
            this.dQmMarketValueZc = dQmMarketValueZc + dQmMarketValue;
            rowBuf.append(dNcCost).append(","); //银行存款成本的年期初值
            rowBuf.append(dQmCost).append(","); //银行存款市值的年期初值
            rowBuf.append(dNcMarketValue).append(","); //银行存款成本的年期初值
            rowBuf.append(dQmMarketValue).append(","); //银行存款市值的年期初值
            rowBuf.append("交易类金融负债").append(",");
            getNcCost("2101"); //获取交易类金融负债期初的成本，市值
            getQmMarketValue("2101"); //获取交易类金融负债期末的成本，市值
            dNcCostFz = dNcCostFz + dNcCost;
            dNcMarketValueFz = dNcMarketValueFz + dNcMarketValue;
            this.dQmCostFz = dQmCostFz + dQmCost;
            this.dQmMarketValueFz = dQmMarketValueFz + dQmMarketValue;
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            rowBuf.append(dNcMarketValue).append(",");
            rowBuf.append(dQmMarketValue).append(",");
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            rowBuf = new StringBuffer(); //每行数据
            rowBuf.append("存出保证金").append(",");
			//---- MS00823 QDV4博时2009年11月18日01_B  2009.11.24 -----------------
            getNcCost("1031"); //获取存出保证金期初的成本，市值
            getQmMarketValue("1031"); //获取存出保证金期末的成本，市值
			//----  MS00823 QDV4博时2009年11月18日01_B end -------------------------
            dNcCostZc = dNcCostZc + dNcCost;
            dNcMarketValueZc = dNcMarketValueZc + dNcMarketValue;
            this.dQmCostZc = dQmCostZc + dQmCost;
            this.dQmMarketValueZc = dQmMarketValueZc + dQmMarketValue;
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            rowBuf.append(dNcMarketValue).append(",");
            rowBuf.append(dQmMarketValue).append(",");
            rowBuf.append("其中：衍生工具贷项").append(","); //sunny
			//---- MS00823 QDV4博时2009年11月18日01_B  2009.11.24 -----------------
            getNcCost("2101"); //获取衍生工具贷项期初的成本，市值
            getQmMarketValue("2101"); //获取衍生工具贷项期末的成本，市值
		    //---- MS00823 QDV4博时2009年11月18日01_B  end--- -----------------
            dNcCostFz = dNcCostFz + dNcCost;
            dNcMarketValueFz = dNcMarketValueFz + dNcMarketValue;
            this.dQmCostFz = dQmCostFz + dQmCost;
            this.dQmMarketValueFz = dQmMarketValueFz + dQmMarketValue;
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            rowBuf.append(dNcMarketValue).append(",");
            rowBuf.append(dQmMarketValue).append(",");
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");
            //交易类金融资产
            buildJYLJRZC();
            //融券回购至应收税款返还
            buildMiddle();
            //串接可供出售的金融资产的字符串
            buildMarketable();
            this.buildCYZDQZC(); //串接持有至到期资产的字符串
            buildTotal(); //合计数据
            if (finBuf.toString().length() > 2) {
                return finBuf.toString().substring(0,
                    finBuf.toString().length() - 2);
            } else {
                return "";
            }
        } catch (Exception e) {
            throw new YssException("获取返回的前台字符串出错" + e.getMessage());
        }
    }

    /**
     * 串接交易类金融资产的字符串
     * @throws YssException
     */
    public void buildJYLJRZC() throws YssException {
        try {
            StringBuffer EQrowBuf = new StringBuffer(); //股票
            StringBuffer FIrowBuf = new StringBuffer(); //债券
            StringBuffer TRrowBuf = new StringBuffer(); //基金
            StringBuffer PJrowBuf = new StringBuffer(); //基金
            StringBuffer ZCZCZQrowBuf = new StringBuffer(); //资产支持证券
            StringBuffer YSJRGJJXrowBuf = new StringBuffer(); //衍生金融工具借项
            double dNcJyljrzcCb = 0; //期初交易类金融资产成本
            double dQmJyljrzcCb = 0; //期末交易类金融资产成本
            double dNcJyljrzcSz = 0; //期初交易类金融资产市值
            double dQmJyljrzcSz = 0; //期末交易类金融资产市值

            EQrowBuf.append(".     其中：股票").append(",");
            getNcCost("1102"); //获取股票期初的成本，市值
            getQmMarketValue("1102"); //获取股票期末的成本，市值
            dNcJyljrzcCb = dNcJyljrzcCb + dNcCost;
            dNcJyljrzcSz = dNcJyljrzcSz + dNcMarketValue;
            dQmJyljrzcCb = dQmJyljrzcCb + dQmCost;
            dQmJyljrzcSz = dQmJyljrzcSz + dQmMarketValue;
            EQrowBuf.append(dNcCost).append(",");
            EQrowBuf.append(dQmCost).append(",");
            EQrowBuf.append(dNcMarketValue).append(",");
            EQrowBuf.append(dQmMarketValue).append(",");
            EQrowBuf.append("应付证券清算款").append(",");
            this.getSettleMoney("NC"); //调用应收应付证券清算款
            this.getSettleMoney("QM"); //调用应收应付证券清算款
            dNcCostFz = dNcCostFz + dNcYfSettleM;
            dNcMarketValueFz = dNcMarketValueFz + this.dNcYfSettleM;
            this.dQmCostFz = dQmCostFz + this.dQmYfSettleM;
            this.dQmMarketValueFz = dQmMarketValueFz + dQmYfSettleM;
            EQrowBuf.append(dNcYfSettleM).append(","); //年初数成本
            EQrowBuf.append(dQmYfSettleM).append(","); //期末数成本
            EQrowBuf.append(dNcYfSettleM).append(","); //年初数市值
            EQrowBuf.append(dQmYfSettleM).append(","); //期末数市值

            FIrowBuf.append(".     债券").append(",");
            getNcCost("1103"); //获取债券期初的成本，市值
            getQmMarketValue("1103"); //获取债券期末的成本，市值
            dNcJyljrzcCb = dNcJyljrzcCb + dNcCost;
            dNcJyljrzcSz = dNcJyljrzcSz + dNcMarketValue;
            dQmJyljrzcCb = dQmJyljrzcCb + dQmCost;
            dQmJyljrzcSz = dQmJyljrzcSz + dQmMarketValue;
            FIrowBuf.append(dNcCost).append(",");
            FIrowBuf.append(dQmCost).append(",");
            FIrowBuf.append(dNcMarketValue).append(",");
            FIrowBuf.append(dQmMarketValue).append(",");
            FIrowBuf.append("应付管理人报酬").append(",");
            getNcCost("2206"); //获取应付管理人报酬期初的成本，市值
            getQmMarketValue("2206"); //获取应付管理人报酬期末的成本，市值
            dNcCostFz = dNcCostFz + dNcCost;
            dNcMarketValueFz = dNcMarketValueFz + dNcMarketValue;
            this.dQmCostFz = dQmCostFz + dQmCost;
            this.dQmMarketValueFz = dQmMarketValueFz + dQmMarketValue;
            FIrowBuf.append(dNcCost).append(",");
            FIrowBuf.append(dQmCost).append(",");
            FIrowBuf.append(dNcMarketValue).append(",");
            FIrowBuf.append(dQmMarketValue).append(",");

            TRrowBuf.append(".     基金").append(",");
            getNcCost("1105"); //获取基金期初的成本，市值
            getQmMarketValue("1105"); //获取基金期末的成本，市值
            dNcJyljrzcCb = dNcJyljrzcCb + dNcCost;
            dNcJyljrzcSz = dNcJyljrzcSz + dNcMarketValue;
            dQmJyljrzcCb = dQmJyljrzcCb + dQmCost;
            dQmJyljrzcSz = dQmJyljrzcSz + dQmMarketValue;
            TRrowBuf.append(dNcCost).append(",");
            TRrowBuf.append(dQmCost).append(",");
            TRrowBuf.append(dNcMarketValue).append(",");
            TRrowBuf.append(dQmMarketValue).append(",");
            TRrowBuf.append("应付托管费").append(",");
            getNcCost("2207"); //获取应付托管费期初的成本，市值
            getQmMarketValue("2207"); //获取应付托管费期末的成本，市值
            dNcCostFz = dNcCostFz + dNcCost;
            dNcMarketValueFz = dNcMarketValueFz + dNcMarketValue;
            this.dQmCostFz = dQmCostFz + dQmCost;
            this.dQmMarketValueFz = dQmMarketValueFz + dQmMarketValue;
            TRrowBuf.append(dNcCost).append(",");
            TRrowBuf.append(dQmCost).append(",");
            TRrowBuf.append(dNcMarketValue).append(",");
            TRrowBuf.append(dQmMarketValue).append(",");

            PJrowBuf.append(".     票据").append(","); //alter by sunny
            // getNcCost("1105"); //获取票据期初的成本，市值
            // getQmMarketValue("1105"); //获取票据期末的成本，市值
            dNcCost = dQmCost = dNcMarketValue = dQmMarketValue = 0;
            dNcJyljrzcCb = dNcJyljrzcCb + dNcCost;
            dNcJyljrzcSz = dNcJyljrzcSz + dNcMarketValue;
            dQmJyljrzcCb = dQmJyljrzcCb + dQmCost;
            dQmJyljrzcSz = dQmJyljrzcSz + dQmMarketValue;
            PJrowBuf.append(dNcCost).append(",");
            PJrowBuf.append(dQmCost).append(",");
            PJrowBuf.append(dNcMarketValue).append(",");
            PJrowBuf.append(dQmMarketValue).append(",");
            PJrowBuf.append("应付利息").append(","); //sunny
            getNcCost("2231"); //获取应付利息期初的成本，市值
            getQmMarketValue("2231"); //获取应付利息期末的成本，市值
            dNcCostFz = dNcCostFz + dNcCost;
            dNcMarketValueFz = dNcMarketValueFz + dNcMarketValue;
            this.dQmCostFz = dQmCostFz + dQmCost;
            this.dQmMarketValueFz = dQmMarketValueFz + dQmMarketValue;
            PJrowBuf.append(dNcCost).append(",");
            PJrowBuf.append(dQmCost).append(",");
            PJrowBuf.append(dNcMarketValue).append(",");
            PJrowBuf.append(dQmMarketValue).append(",");

            ZCZCZQrowBuf.append(".     资产支持证券").append(","); //alter by sunny
            //getNcCost("1105"); //获取资产支持证券期初的成本，市值
            //getQmMarketValue("1105"); //获取资产支持证券期末的成本，市值
            dNcCost = dQmCost = dNcMarketValue = dQmMarketValue = 0;
            dNcJyljrzcCb = dNcJyljrzcCb + dNcCost;
            dNcJyljrzcSz = dNcJyljrzcSz + dNcMarketValue;
            dQmJyljrzcCb = dQmJyljrzcCb + dQmCost;
            dQmJyljrzcSz = dQmJyljrzcSz + dQmMarketValue;
            ZCZCZQrowBuf.append(dNcCost).append(",");
            ZCZCZQrowBuf.append(dQmCost).append(",");
            ZCZCZQrowBuf.append(dNcMarketValue).append(",");
            ZCZCZQrowBuf.append(dQmMarketValue).append(",");
            ZCZCZQrowBuf.append("应付税款").append(","); //sunny
            getNcCost("2221"); //获取应付税款期初的成本，市值
            getQmMarketValue("2221"); //获取应付税款期末的成本，市值
            dNcCostFz = dNcCostFz + dNcCost;
            dNcMarketValueFz = dNcMarketValueFz + dNcMarketValue;
            this.dQmCostFz = dQmCostFz + dQmCost;
            this.dQmMarketValueFz = dQmMarketValueFz + dQmMarketValue;
            ZCZCZQrowBuf.append(dNcCost).append(",");
            ZCZCZQrowBuf.append(dNcMarketValue).append(",");
            ZCZCZQrowBuf.append(dQmCost).append(",");
            ZCZCZQrowBuf.append(dQmMarketValue).append(",");

            YSJRGJJXrowBuf.append(".     衍生金融工具借项").append(","); //alter by sunny
            //getNcCost("1105"); //获取衍生金融工具借项期初的成本，市值
            //getQmMarketValue("1105"); //获取衍生金融工具借项期末的成本，市值
            dNcCost = dQmCost = dNcMarketValue = dQmMarketValue = 0;
            dNcJyljrzcCb = dNcJyljrzcCb + dNcCost;
            dNcJyljrzcSz = dNcJyljrzcSz + dNcMarketValue;
            dQmJyljrzcCb = dQmJyljrzcCb + dQmCost;
            dQmJyljrzcSz = dQmJyljrzcSz + dQmMarketValue;
            YSJRGJJXrowBuf.append(dNcCost).append(",");
            YSJRGJJXrowBuf.append(dQmCost).append(",");
            YSJRGJJXrowBuf.append(dNcMarketValue).append(",");
            YSJRGJJXrowBuf.append(dQmMarketValue).append(",");
            YSJRGJJXrowBuf.append("其它负债").append(",");
            /****************************************/
            getNcCost("2241"); //获取其它负债期初的成本，市值
            getQmMarketValue("2241"); //获取其它负债期末的成本，市值
            this.dNcCostOfz = dNcCostOfz + dNcCost;
            dNcMarketValueQfz = dNcMarketValueQfz + dNcMarketValue;
            this.dQmCostQfz = dQmCostQfz + dQmCost;
            this.dQmMarketValueQfz = dQmMarketValueQfz + dQmMarketValue;
            /****************************************************/
            getNcCost("2501"); //获取其它负债期初的成本，市值
            getQmMarketValue("2501"); //获取其它负债期末的成本，市值
            this.dNcCostOfz = dNcCostOfz + dNcCost;
            dNcMarketValueQfz = dNcMarketValueQfz + dNcMarketValue;
            this.dQmCostQfz = dQmCostQfz + dQmCost;
            this.dQmMarketValueQfz = dQmMarketValueQfz + dQmMarketValue;
            /*****************************************************/
            dNcCostFz = dNcCostFz + dNcCostOfz;
            dNcMarketValueFz = dNcMarketValueFz + dNcMarketValueQfz;
            this.dQmCostFz = dQmCostFz + dQmCostQfz;
            this.dQmMarketValueFz = dQmMarketValueFz + dQmMarketValueQfz;
            YSJRGJJXrowBuf.append(dNcCostOfz).append(",");
            YSJRGJJXrowBuf.append(dQmCostQfz).append(",");
            YSJRGJJXrowBuf.append(dNcMarketValueQfz).append(",");
            YSJRGJJXrowBuf.append(dQmMarketValueQfz).append(",");

            rowBuf = new StringBuffer(); //每行数据
            rowBuf.append("交易类金融资产").append(","); //获取交易类金融资产期初的成本，市值
            dNcCostZc = dNcCostZc + dNcJyljrzcCb; //把交易类金融资产累加到资产合计里面去
            dNcMarketValueZc = dNcMarketValueZc + dNcJyljrzcSz;
            rowBuf.append(dNcJyljrzcCb).append(",");
            rowBuf.append(dQmJyljrzcCb).append(",");
            //获取交易类金融资产期末的成本，市值
            this.dQmCostZc = dQmCostZc + dQmJyljrzcCb; //把交易类金融资产市值累加到资产合计的市值里面去
            this.dQmMarketValueZc = dQmMarketValueZc + dQmJyljrzcSz;
            rowBuf.append(dNcJyljrzcSz).append(",");
            rowBuf.append(dQmJyljrzcSz).append(",");
            rowBuf.append("融资回购").append(",");
            getNcCost("2202"); //获取融资回购期初的成本，市值
            getQmMarketValue("2202"); //获取融资回购期末的成本，市值
            dNcCostFz = dNcCostFz + dNcCost;
            dNcMarketValueFz = dNcMarketValueFz + dNcMarketValue;
            this.dQmCostFz = dQmCostFz + dQmCost;
            this.dQmMarketValueFz = dQmMarketValueFz + dQmMarketValue;
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            rowBuf.append(dNcMarketValue).append(",");
            rowBuf.append(dQmMarketValue).append(",");
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");
            finBuf.append(fixPub.buildRowCompResult(EQrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");
            finBuf.append(fixPub.buildRowCompResult(FIrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            finBuf.append(fixPub.buildRowCompResult(TRrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            finBuf.append(fixPub.buildRowCompResult(PJrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            finBuf.append(fixPub.buildRowCompResult(ZCZCZQrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            finBuf.append(fixPub.buildRowCompResult(YSJRGJJXrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

        } catch (Exception e) {
            throw new YssException("串接交易类金融资产的字符串出错" + e.getMessage());
        }
    }

    /**
     * 串接融券回购到应收税款返还的字符串
     * @throws YssException
     */
    public void buildMiddle() throws YssException {
        try {
            rowBuf = new StringBuffer();
            rowBuf.append("融券回购").append(",");
            getNcCost("1202"); //获取融券回购期初的成本，市值
            getQmMarketValue("1202"); //获取融券回购期末的成本，市值
            dNcCostZc = dNcCostZc + dNcCost;
            dNcMarketValueZc = dNcMarketValueZc + dNcMarketValue;
            this.dQmCostZc = dQmCostZc + dQmCost;
            this.dQmMarketValueZc = dQmMarketValueZc + dQmMarketValue;
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            rowBuf.append(dNcMarketValue).append(",");
            rowBuf.append(dQmMarketValue).append(",");
            rowBuf.append(buildEmpty()); //串接空字符串
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            rowBuf = new StringBuffer();
            rowBuf.append("应收证券清算款").append(",");
            this.getSettleMoney("NC"); //调用应收应付证券清算款
            this.getSettleMoney("QM"); //调用应收应付证券清算款
            dNcCostZc = dNcCostZc + dNcYsSettleM;
            dNcMarketValueZc = dNcMarketValueZc + dNcYsSettleM;
            this.dQmCostZc = dQmCostZc + dQmYsSettleM;
            this.dQmMarketValueZc = dQmMarketValueZc + dQmYsSettleM;
            rowBuf.append(dNcYsSettleM).append(",");
            rowBuf.append(dQmYsSettleM).append(",");
            rowBuf.append(dNcYsSettleM).append(",");
            rowBuf.append(dQmYsSettleM).append(",");
            rowBuf.append(buildEmpty()); //串接空字符串
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            rowBuf = new StringBuffer();
            rowBuf.append("应收利息").append(",");
            getNcCost("1204"); //获取应收利息期初的成本，市值
            getQmMarketValue("1204"); //获取应收利息期末的成本，市值
            dNcCostZc = dNcCostZc + dNcCost;
            dNcMarketValueZc = dNcMarketValueZc + dNcMarketValue;
            this.dQmCostZc = dQmCostZc + dQmCost;
            this.dQmMarketValueZc = dQmMarketValueZc + dQmMarketValue;
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            rowBuf.append(dNcMarketValue).append(",");
            rowBuf.append(dQmMarketValue).append(",");
            rowBuf.append(buildEmpty()); //串接空字符串
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            rowBuf = new StringBuffer();
            rowBuf.append("应收股利").append(",");
            getNcCost("1203"); //获取应收股利期初的成本，市值
            dNcCostZc = dNcCostZc + dNcCost;
            dNcMarketValueZc = dNcMarketValueZc + dNcMarketValue;
            getQmMarketValue("1203"); //获取应收股利期末的成本，市值
            this.dQmCostZc = dQmCostZc + dQmCost;
            this.dQmMarketValueZc = dQmMarketValueZc + dQmMarketValue;
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            rowBuf.append(dNcMarketValue).append(",");
            rowBuf.append(dQmMarketValue).append(",");
            rowBuf.append(buildEmpty()); //串接空字符串
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            rowBuf = new StringBuffer(); //by sunny
            rowBuf.append("应收税款返还").append(",");
            //getNcCost("1203"); //获取应收税款返还期初的成本，市值
            //getQmMarketValue("1203"); //获取应收税款返还期末的成本，市值
            dNcCost = dQmCost = dNcMarketValue = dQmMarketValue = 0;
            dNcCostZc = dNcCostZc + dNcCost;
            dNcMarketValueZc = dNcMarketValueZc + dNcMarketValue;
            this.dQmCostZc = dQmCostZc + dQmCost;
            this.dQmMarketValueZc = dQmMarketValueZc + dQmMarketValue;
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            rowBuf.append(dNcMarketValue).append(",");
            rowBuf.append(dQmMarketValue).append(",");
            rowBuf.append("负债合计").append(",");
            rowBuf.append(dNcCostFz).append(",");
            rowBuf.append(dQmCostFz).append(",");
            rowBuf.append(dNcMarketValueFz).append(",");
            rowBuf.append(dQmMarketValueFz).append(",");
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

        } catch (Exception e) {
            throw new YssException("串接融券回购到应收税款返还的字符串出错" + e.getMessage());
        }
    }

    /**
     * 串接可供出售的金融资产的字符串
     * @throws YssException
     */
    public void buildMarketable() throws YssException {
        StringBuffer EQrowBuf = new StringBuffer(); //股票
        StringBuffer FIrowBuf = new StringBuffer(); //债券
        StringBuffer ZCZCZQrowBuf = new StringBuffer(); //资产支持证券
        double dNcKgcsjrzcCb = 0; //年初可供出售金融资产成本
        double dQmKgcsjrzcCb = 0; //期末可供出售金融资产成本
        double dNcKgcsjrzcSz = 0; //期初可供出售金融资产市值
        double dQmKgcsjrzcSz = 0; //期末可供出售金融资产市值
        try {
            EQrowBuf.append(".     其中：股票").append(",");
            //getNcCost("1102"); //获取股票期初的成本，市值
            // getQmMarketValue("1102"); //获取股票期末的成本，市值
            dNcCost = dQmCost = dNcMarketValue = dQmMarketValue = 0;
            dNcKgcsjrzcCb = dNcKgcsjrzcCb + dNcCost;
            dNcKgcsjrzcSz = dNcKgcsjrzcSz + dNcMarketValue;
            dQmKgcsjrzcCb = dQmKgcsjrzcCb + dQmCost;
            dQmKgcsjrzcSz = dQmKgcsjrzcSz + dQmMarketValue;
            EQrowBuf.append(dNcCost).append(",");
            EQrowBuf.append(dQmCost).append(",");
            EQrowBuf.append(dNcMarketValue).append(",");
            EQrowBuf.append(dQmMarketValue).append(",");
            EQrowBuf.append("受托社保基金").append(","); //sunny
            getQyData("40010101", "NC"); //获取受托社保基金期初的成本，市值
            getQyData("40010101", "QM"); //获取受托社保基金期末的成本，市值
            this.dNcCostQy = this.dNcCostQy + dNcCost;
            this.dNcMarketValueQy = this.dNcMarketValueQy + dNcCost;
            this.dQmCostQy = this.dQmCostQy + dQmCost;
            this.dQmMarketValueQy = this.dQmMarketValueQy + dQmCost;
            EQrowBuf.append(dNcCost).append(",");
            EQrowBuf.append(dQmCost).append(",");
            EQrowBuf.append(dNcCost).append(",");
            EQrowBuf.append(dQmCost).append(",");

            FIrowBuf.append(".      债券").append(",");
            //getNcCost("1103"); //获取债券期初的成本，市值
            //getQmMarketValue("1103"); //获取债券期末的成本，市值
            dNcCost = dQmCost = dNcMarketValue = dQmMarketValue = 0;
            dNcKgcsjrzcCb = dNcKgcsjrzcCb + dNcCost;
            dNcKgcsjrzcSz = dNcKgcsjrzcSz + dNcMarketValue;
            dQmKgcsjrzcCb = dQmKgcsjrzcCb + dQmCost;
            dQmKgcsjrzcSz = dQmKgcsjrzcSz + dQmMarketValue;
            FIrowBuf.append(dNcCost).append(",");
            FIrowBuf.append(dQmCost).append(",");
            FIrowBuf.append(dNcMarketValue).append(",");
            FIrowBuf.append(dQmMarketValue).append(",");
            FIrowBuf.append("可供出售金融资产公允价值变动").append(",");
            FIrowBuf.append(0).append(",");
            FIrowBuf.append(0).append(",");
            FIrowBuf.append(0).append(",");
            FIrowBuf.append(0).append(",");

            ZCZCZQrowBuf.append(".      资产支持证券").append(","); //alter by sunny
            //getNcCost("1105"); //获取资产支持证券期初的成本，市值
            //getQmMarketValue("1105"); //获取资产支持证券期末的成本，市值
            dNcCost = dQmCost = dNcMarketValue = dQmMarketValue = 0;
            dNcKgcsjrzcCb = dNcKgcsjrzcCb + dNcCost;
            dNcKgcsjrzcSz = dNcKgcsjrzcSz + dNcMarketValue;
            dQmKgcsjrzcCb = dQmKgcsjrzcCb + dQmCost;
            dQmKgcsjrzcSz = dQmKgcsjrzcSz + dQmMarketValue;
            ZCZCZQrowBuf.append(dNcCost).append(",");
            ZCZCZQrowBuf.append(dQmCost).append(",");
            ZCZCZQrowBuf.append(dNcMarketValue).append(",");
            ZCZCZQrowBuf.append(dQmMarketValue).append(",");
            ZCZCZQrowBuf.append("未分配收益").append(","); //sunny
            getQyData("4103", "NC"); //获取未分配收益期初的成本，市值
            getQyData("4103", "QM"); //获取未分配收益期末的成本，市值
            //fanghaoln 20100222 MS00966 QDV4博时2010年1月29日01_A
            double s03NcCost=dNcCost;
            double s03QmCost=dQmCost;
            getQyData("4104", "NC"); //获取未分配收益期初的成本，市值
            getQyData("4104", "QM"); //获取未分配收益期末的成本，市值
            dNcCost=dNcCost+s03NcCost;//需要取发生额
            dQmCost=dQmCost+s03QmCost;//需要取发生额
            //----------------MS00966---end--------------------------
            this.dNcCostQy = this.dNcCostQy + dNcCost;
            this.dNcMarketValueQy = this.dNcMarketValueQy + dNcCost;
            this.dQmCostQy = this.dQmCostQy + dQmCost;
            this.dQmMarketValueQy = this.dQmMarketValueQy + dQmCost;
            ZCZCZQrowBuf.append(dNcCost).append(",");
            ZCZCZQrowBuf.append(dQmCost).append(",");
            ZCZCZQrowBuf.append(dNcCost).append(",");
            ZCZCZQrowBuf.append(dQmCost).append(",");

            rowBuf = new StringBuffer(); //每行数据
            rowBuf.append("可供出售金融资产").append(","); //获取可供出售金融资产期初的成本，市值
            dNcCostZc = dNcCostZc + dNcKgcsjrzcCb; //把可供出售金融资产累加到资产合计里面去
            dNcMarketValueZc = dNcMarketValueZc + dNcKgcsjrzcSz;
            rowBuf.append(dNcKgcsjrzcCb).append(",");
            rowBuf.append(dQmKgcsjrzcCb).append(",");
            //获取可供出售金融资产期末的成本，市值
            this.dQmCostZc = dQmCostZc + dQmKgcsjrzcCb; //把交易类金融资产市值累加到资产合计的市值里面去
            this.dQmMarketValueZc = dQmMarketValueZc + dQmKgcsjrzcSz;
            rowBuf.append(dNcKgcsjrzcSz).append(",");
            rowBuf.append(dQmKgcsjrzcSz).append(",");
            rowBuf.append("基金权益：").append(",");
            rowBuf.append(" ").append(",");
            rowBuf.append(" ").append(",");
            rowBuf.append(" ").append(",");
            rowBuf.append(" ").append(",");
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");
            finBuf.append(fixPub.buildRowCompResult(EQrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");
            finBuf.append(fixPub.buildRowCompResult(FIrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");
            finBuf.append(fixPub.buildRowCompResult(ZCZCZQrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");
        } catch (Exception e) {
            throw new YssException("串接可供出售的金融资产的字符串出错" + e.getMessage());
        }
    }

    /**
     * 串接持有至到期资产的字符串
     * @throws YssException
     */
    public void buildCYZDQZC() throws YssException {
        StringBuffer FIrowBuf = new StringBuffer(); //债券
        StringBuffer ZCZCZQrowBuf = new StringBuffer(); //资产支持证券
        StringBuffer DErowBuf = new StringBuffer(); //定期并存款
        double dNcCzdqzcCb = 0; //年初可供出售金融资产成本
        double dQmCzdqzcCb = 0; //期末可供出售金融资产成本
        double dNcCzdqzcSz = 0; //期初可供出售金融资产市值
        double dQmCzdqzcSz = 0; //期末可供出售金融资产市值
        try {

            FIrowBuf.append(".      其中：债券").append(",");
            //getNcCost("1103"); //获取债券期初的成本，市值
            //getQmMarketValue("1103"); //获取债券期末的成本，市值
            dNcCost = dQmCost = dNcMarketValue = dQmMarketValue = 0;
            dNcCzdqzcCb = dNcCzdqzcCb + dNcCost;
            dNcCzdqzcSz = dNcCzdqzcSz + dNcMarketValue;
            dQmCzdqzcCb = dQmCzdqzcCb + dQmCost;
            dQmCzdqzcSz = dQmCzdqzcSz + dQmMarketValue;
            FIrowBuf.append(dNcCost).append(",");
            FIrowBuf.append(dQmCost).append(",");
            FIrowBuf.append(dNcMarketValue).append(",");
            FIrowBuf.append(dQmMarketValue).append(",");
            FIrowBuf.append(".      公允价值变动损益").append(",");
            getQyData("410302", "NC"); //获取公允价值变动损益期初的成本，市值
            getQyData("410302", "QM"); //获取公允价值变动损益期末的成本，市值
            FIrowBuf.append(dNcCost).append(",");
            FIrowBuf.append(dQmCost).append(",");
            FIrowBuf.append(dNcCost).append(",");
            FIrowBuf.append(dQmCost).append(",");

            ZCZCZQrowBuf.append(".      资产支持证券").append(","); //alter by sunny
            // getNcCost("1105"); //获取资产支持证券期初的成本，市值
            // getQmMarketValue("1105"); //获取资产支持证券期末的成本，市值
            dNcCost = dQmCost = dNcMarketValue = dQmMarketValue = 0;
            dNcCzdqzcCb = dNcCzdqzcCb + dNcCost;
            dNcCzdqzcSz = dNcCzdqzcSz + dNcMarketValue;
            dQmCzdqzcCb = dQmCzdqzcCb + dQmCost;
            dQmCzdqzcSz = dQmCzdqzcSz + dQmMarketValue;
            ZCZCZQrowBuf.append(dNcCost).append(",");
            ZCZCZQrowBuf.append(dQmCost).append(",");
            ZCZCZQrowBuf.append(dNcMarketValue).append(",");
            ZCZCZQrowBuf.append(dQmMarketValue).append(",");
            if (dNCrepCe != 0 || dQMrepCe != 0) { //判断是不是有折算差额 如果有的话将要显示出来
                ZCZCZQrowBuf.append("报表折算差额").append(","); //alter by sunny
                ZCZCZQrowBuf.append(dNCrepCe).append(",");
                ZCZCZQrowBuf.append(dQMrepCe).append(",");
                ZCZCZQrowBuf.append(dNCrepCe).append(",");
                ZCZCZQrowBuf.append(dQMrepCe).append(",");
            } else {
                ZCZCZQrowBuf.append(this.buildEmpty()); //串接空字符串
            }

            DErowBuf.append(".      定期类存款").append(",");
            // getNcCost("1102"); //获取股票期初的成本，市值
            //getQmMarketValue("1102"); //获取股票期末的成本，市值
            dNcCost = dQmCost = dNcMarketValue = dQmMarketValue = 0;
            dNcCzdqzcCb = dNcCzdqzcCb + dNcCost;
            dNcCzdqzcSz = dNcCzdqzcSz + dNcMarketValue;
            dQmCzdqzcCb = dQmCzdqzcCb + dQmCost;
            dQmCzdqzcSz = dQmCzdqzcSz + dQmMarketValue;
            DErowBuf.append(dNcCost).append(",");
            DErowBuf.append(dQmCost).append(",");
            DErowBuf.append(dNcMarketValue).append(",");
            DErowBuf.append(dQmMarketValue).append(",");
            DErowBuf.append(this.buildEmpty()); //串接空字符串

            rowBuf = new StringBuffer(); //每行数据
            rowBuf.append("持有至到期资产").append(","); //获取持有至到期资产期初的成本，市值
            dNcCostZc = dNcCostZc + dNcCzdqzcCb; //把持有至到期资产累加到资产合计里面去
            dNcMarketValueZc = dNcMarketValueZc + dNcCzdqzcSz;
            rowBuf.append(dNcCzdqzcCb).append(",");
            rowBuf.append(dQmCzdqzcCb).append(",");
            //获取持有至到期资产期末的成本，市值
            this.dQmCostZc = dQmCostZc + dQmCzdqzcCb; //把交易类金融资产市值累加到资产合计的市值里面去
            this.dQmMarketValueZc = dQmMarketValueZc + dQmCzdqzcSz;
            rowBuf.append(dNcCzdqzcSz).append(",");
            rowBuf.append(dQmCzdqzcSz).append(",");
            rowBuf.append("其中：已实现收益").append(",");
            getQyData("410301", "NC"); //获取其中：已实现收益期初的成本，市值
            getQyData("410301", "QM"); //获取其中：已实现收益期末的成本，市值
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            this.finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            finBuf.append(fixPub.buildRowCompResult(FIrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");
            finBuf.append(fixPub.buildRowCompResult(ZCZCZQrowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");
            finBuf.append(fixPub.buildRowCompResult(DErowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");
        } catch (Exception e) {
            throw new YssException("串接可供出售的金融资产的字符串出错" + e.getMessage());
        }
    }

    /**
     * 串接合计字符串
     * @throws YssException
     */
    public void buildTotal() throws YssException {
        try {
            rowBuf = new StringBuffer(); //by sunny
            rowBuf.append("其它资产").append(",");
            getNcCost("1221"); //获取其它资产期初的成本，市值
            getQmMarketValue("1221"); //获取其它资产期末的成本，市值
            dNcCostZc = dNcCostZc + dNcCost;
            dNcMarketValueZc = dNcMarketValueZc + dNcMarketValue;
            this.dQmCostZc = dQmCostZc + dQmCost;
            this.dQmMarketValueZc = dQmMarketValueZc + dQmMarketValue;
            rowBuf.append(dNcCost).append(",");
            rowBuf.append(dQmCost).append(",");
            rowBuf.append(dNcMarketValue).append(",");
            rowBuf.append(dQmMarketValue).append(",");
            rowBuf.append("受托社保基金权益合计").append(",");
            rowBuf.append(dNcCostQy).append(",");
            rowBuf.append(dQmCostQy).append(",");
            rowBuf.append(dNcMarketValueQy).append(",");
            rowBuf.append(dQmMarketValueQy).append(",");
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

            rowBuf = new StringBuffer();
            rowBuf.append("资产总计").append(",");
            rowBuf.append(dNcCostZc).append(",");
            rowBuf.append(dQmCostZc).append(",");
            rowBuf.append(dNcMarketValueZc).append(",");
            rowBuf.append(dQmMarketValueZc).append(",");
            rowBuf.append("负债和受托社保基金权益总计").append(","); //要加上报表折算差额
            rowBuf.append(dNcCostFz + this.dNcCostQy + this.dNCrepCe).append(",");
            rowBuf.append(dQmCostFz + this.dQmCostQy + this.dQMrepCe).append(",");
            rowBuf.append(dNcMarketValueFz + this.dNcMarketValueQy + dNCrepCe).append(",");
            rowBuf.append(dQmMarketValueFz + this.dQmMarketValueQy + dQMrepCe).append(",");
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DS_BalanceSheet_USD")).append(
                    "\r\n");

        } catch (Exception e) {
            throw new YssException("串接合计的字符串出错" + e.getMessage());
        }
    }

    /**
     * 串接空字符串，有空字符串的地方直接调用此函数
     * @throws YssException
     * @return StringBuffer
     */
    public StringBuffer buildEmpty() throws YssException {
        StringBuffer strEmpty = new StringBuffer();
        try {
            strEmpty.append(" ").append(",");
            strEmpty.append(" ").append(",");
            strEmpty.append(" ").append(",");
            strEmpty.append(" ").append(",");
            strEmpty.append(" ").append(",");
            return strEmpty;
        } catch (Exception e) {
            throw new YssException("串接空字符串出错" + e.getMessage());
        }
    }

    /**
     * 获取清算款
     * 如果为正，即为应收证券清算款；如果为负，即为应付证券清算款
     * param strBZ 此参数是用来标示是年初还是期末
     * NC，QM 分别代表年初和期末
     * @throws YssException
     */
    public void getSettleMoney(String strBZ) throws YssException {
        String strSql = "";
        String strCon = "";
        ResultSet rs = null;
        dNcYsSettleM = 0;
        dQmYsSettleM = 0;
        double dTemp = 0;
        try {
            if (strBZ.equalsIgnoreCase("NC")) {
                strCon = "fmonth= 12";
                sTabpre = "A" + (YssFun.getYear(YssFun.toDate(startDate)) - 1) +
                    strLset;
            } else if (strBZ.equalsIgnoreCase("QM")) {
                strCon = "fmonth= " + YssFun.getMonth(YssFun.toDate(endDate));
                sTabpre = "A" + (YssFun.getYear(YssFun.toDate(endDate))) + strLset;
            }
            if (dbl.yssTableExist(sTabpre + "laccount")) { //首先检查表是否存在
                strSql = "select * from " + sTabpre +
                    "lbalance where " + strCon +
                    " and facctcode in (select facctcode from " +
                    sTabpre +
                    "laccount where facctcode like '3003%' and facctdetail=1)";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    dTemp = rs.getDouble("FBEndBal");
                    if (strBZ.equalsIgnoreCase("NC")) {
                        if (dTemp > 0) {
                            this.dNcYsSettleM = dNcYsSettleM + dTemp / dimmRate; //应收证券清算款
                        } else if (dTemp < 0) {
                            this.dNcYfSettleM = dNcYfSettleM + dTemp / dimmRate; //应付证券清算款
                            this.dNcYfSettleM = -this.dNcYfSettleM;
                        }
                    } else if (strBZ.equalsIgnoreCase("QM")) {
                        if (dTemp > 0) {
                            this.dQmYsSettleM = dQmYsSettleM + dTemp / dimmRate; //应收证券清算款
                        } else if (dTemp < 0) {
                            this.dQmYfSettleM = dQmYfSettleM + dTemp / dimmRate; //应付证券清算款
                            this.dQmYfSettleM = -this.dQmYfSettleM;
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new YssException("获取清算款出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取权益类数据
     * @param strKmh String  科目代码
     * @param strBZ  String  期初期末标示
     * @throws YssException
     */
    public void getQyData(String strKmh, String strBZ) throws YssException {
        String strSql = "";
        String strCon = "";
        ResultSet rs = null;
        try {
            if (strBZ.equalsIgnoreCase("NC")) { //如果是年初 应该取的是报表日期的前一年份的最后一天
                strCon = "fmonth= 12";
                sTabpre = "A" + (YssFun.getYear(YssFun.toDate(startDate)) - 1) +
                    strLset;
            } else if (strBZ.equalsIgnoreCase("QM")) {
                strCon = "fmonth= " + YssFun.getMonth(YssFun.toDate(endDate));
                sTabpre = "A" + (YssFun.getYear(YssFun.toDate(endDate))) + strLset;
            }
            if (dbl.yssTableExist(sTabpre + "laccount")) { //首先检查表是否存在
                strSql = "select * from (select * from " + sTabpre +
                    "lbalance where " + strCon +
                    " and facctcode = " + dbl.sqlString(strKmh) +
                    ") a join (select * from " + sTabpre +
                    "laccount) b on a.facctcode = b.facctcode";
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    if (strKmh.equalsIgnoreCase("410402")) {
                        if (strBZ.equalsIgnoreCase("NC")) {
                            this.dNcCost = rs.getDouble("FBEndBal") *
                                rs.getDouble("fbaldc") / this.davgRate;
                            this.dNCrepCe = rs.getDouble("FBEndBal") *
                                rs.getDouble("fbaldc") / this.dimmRate - dNcCost;
                        } else if (strBZ.equalsIgnoreCase("QM")) {
                            this.dQmCost = rs.getDouble("FEndBal") *
                                rs.getDouble("fbaldc") / this.davgRate;
                            this.dQMrepCe = rs.getDouble("FBEndBal") *
                                rs.getDouble("fbaldc") / this.dimmRate - dQmCost;
                        }

                    } else {
                        if (strBZ.equalsIgnoreCase("NC")) {
                            this.dNcCost = rs.getDouble("FBEndBal") *
                                rs.getDouble("fbaldc") / this.dimmRate;
                        } else if (strBZ.equalsIgnoreCase("QM")) {
                            this.dQmCost = rs.getDouble("FEndBal") *
                                rs.getDouble("fbaldc") / this.dimmRate;
                        }

                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("获取权益类数据出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 说明：本函数的功能是获取成本,市值的年初数
     * @param strKmh String 此参数是科目号
     * @throws YssException
     * @return double   返回DOUBLE类型
     */
    public void getNcCost(String strKmh) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            dNcCost = 0; //首先初始化为0
            dNcMarketValue = 0;
            sTabpre = "A" + (YssFun.getYear(YssFun.toDate(endDate)) - 1) + strLset;
            if (dbl.yssTableExist(sTabpre + "laccount")) { //首先检查表是否存在
                strSql = "select * from (select facctcode,sum(fstandardmoneycost) as fNcCost,sum(fstandardmoneymarketvalue) as fNcMarketValue from " +
                    pub.yssGetTableName("tb_rep_guessvalue") +
                    " where facctcode = " +
                    dbl.sqlString(strKmh) +
                    " and fdate= " + dbl.sqlDate(startDate) +
                    "  group by facctcode)a join (select * from  " + sTabpre +
                    "laccount) b on a.facctcode = b.facctcode";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    dNcCost = rs.getDouble("fNcCost") * rs.getDouble("fbaldc") /
                        this.dimmRate;
                    dQmMarketValue = rs.getDouble("fNcMarketValue") *
                        rs.getDouble("fbaldc") / this.dimmRate; //如是市值，就取市值字段
                }
            }
        } catch (Exception e) {
            throw new YssException("获取科目的年初数出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 说明：本函数的功能是获取成本，市值的期末数
     * @param strKmh String 此参数是科目号
     * @throws YssException
     * @return double   返回DOUBLE类型
     */
    public void getQmMarketValue(String strKmh) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        dQmCost = 0; //首先初始化为0
        dQmMarketValue = 0;
        try {
            sTabpre = "A" + (YssFun.getYear(YssFun.toDate(endDate))) + strLset;
            if (dbl.yssTableExist(sTabpre + "laccount")) { //首先检查表是否存在
                strSql = "select * from (select facctcode,sum(fstandardmoneycost) as fQmCost,sum(fstandardmoneymarketvalue) as fQmMarketValue from " +
                    pub.yssGetTableName("tb_rep_guessvalue") +
                    " where facctcode = " +
                    dbl.sqlString(strKmh) +
                    " and fdate= " + dbl.sqlDate(endDate) +
                    " group by facctcode )a join (select * from " + sTabpre +
                    "laccount) b on a.facctcode = b.facctcode";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    dQmCost = rs.getDouble("fQmCost") * rs.getDouble("fbaldc") /
                        this.dimmRate;
                    dQmMarketValue = rs.getDouble("fQmMarketValue") *
                        rs.getDouble("fbaldc") / this.dimmRate;
                }
            }
        } catch (Exception e) {
            throw new YssException("获取科目的期末数出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 本函数的功能是获取套帐号，可能一个套帐
     * @throws YssException
     */
    public void getLset() throws YssException {
        {
            String strSql = "";
            ResultSet rs = null;
            try {
                strSql = "select distinct fsetcode from (select * from " +
                    pub.yssGetTableName("tb_para_portfolio") +
                    " where fportcode = " +
                    dbl.sqlString(portCode) +
                    " ) a  join (select * from Lsetlist) b on a.fassetcode = b.fsetid";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    strLset = rs.getString("fsetcode");
                    strLset = "00" + strLset; //串上一个“00”
                }
            } catch (Exception e) {
                throw new YssException("获取套帐号出错" + e.getMessage());
            } finally {
                dbl.closeResultSetFinal(rs);
            }

        }
    }

    /**
     * 获取即期汇率
     * @throws YssException
     */
    public void getimmRate() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select fexrate1 as FimmRate from " +
                pub.yssGetTableName("tb_data_exchangerate") +
                " where fexratedate = ( select max(fexratedate) from " + pub.yssGetTableName("tb_data_exchangerate") + " where fexratedate<=" + dbl.sqlDate(endDate) + " and fcurycode='CNY' )" +
                " and fcurycode='CNY'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.dimmRate = rs.getDouble("FimmRate");
            }
        } catch (Exception e) {
            throw new YssException("获取即期汇率的数出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 当期简单平均汇率=当期各工作日中国人民银行美元对人民币汇率中间价/当期工作日天数
     * 获取平均汇率 获取所有工作日的汇率之和
     * @throws YssException
     */
    public void getAvgRate() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String dDate = new String();
        dDate = YssFun.getYear(YssFun.toDate(endDate)) + "-" +
            (YssFun.getMonth(YssFun.toDate(endDate))) + "-1"; //获取当期的起始日
        try {
            strSql = "select avg(fexrate1) as FAvgRate from " +
                pub.yssGetTableName("tb_data_exchangerate") +
                " where fexratedate not in (select fdate from Tb_Base_ChildHoliday where fholidayscode = " +
                dbl.sqlString(this.holidayCode) + " ) and fexratedate between " +
                dbl.sqlDate(dDate) + " and "
                + dbl.sqlDate(endDate) + " and fcurycode='CNY'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.davgRate = rs.getDouble("FAvgRate");
            }
        } catch (Exception e) {
            throw new YssException("获取平均汇率的数出错" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

}
