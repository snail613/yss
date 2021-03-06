package com.yss.main.operdeal.report.repfix.jsreport;

import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.dsub.BaseBean;
import com.yss.util.YssFun;
import java.sql.ResultSet;
import com.yss.util.YssD;
import java.util.HashMap;

/**
 * <p>Title: 收益表</p>
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
public class IncomeReport
    extends BaseBuildCommonRep {
    private FixPub fixPub = null;
    private CommonRepBean repBean = null;

    private String sPortCode = null; //组合代码
    private String beginDate = null; //期初日期
    private String endDate = null; //期末日期
    private String sCuryCode = null; //货币代码

    private String sPrefixTableName = null; //表前缀
    private String strLset = null; //套帐号

    StringBuffer finBuf = null; //最终返回的数据

    private int QCMonth; //期初月份
    private int QMMonth; //期末月份

//   private double QCRate; //期初汇率
    private double QMRate; //期末汇率

    //------------------------------
    private static final int INTEREST_KM_ALL = 0; //所有利息收入
    private static final int INTEREST_KM_FI = 1; //债券利息收入
    private static final int INTEREST_KM_SETSECURITY = 2; //资产支持证券利息收入
    private static final int INTEREST_KM_DEPOSIT = 3; //存款利息收入
    private static final int INTEREST_KM_RE = 4; //融券回购利息收入
    private static final int INTEREST_KM_OTHER = 99; //其他利息收入
    private static final int INTEREST_KM_BILL = -1; //票据利息收入
    //------------------------------
    private static final int PRICEDIFFERENCE_KM_ALL = 0; //(差价收入)全部
    private static final int PRICEDIFFERENCE_KM_EQ = 1; //股票
    private static final int PRICEDIFFERENCE_KM_FI = 2; //债券
    private static final int PRICEDIFFERENCE_KM_FUND = 4; //基金
    private static final int PRICEDIFFERENCE_KM_DV = 99; //股利收入
    private static final int PRICEDIFFERENCE_KM_DERIVE5 = 5; //衍生金融工具
    private static final int PRICEDIFFERENCE_KM_DERIVE3 = 3; //衍生金融工具
    private static final int PRICEDIFFERENCE_KM_CASH = -1; // 暂时使用，设为负数
    private static final int PRICEDIFFERENCE_KM_OTHER = -2; // 暂时使用，设为负数
    //---------------------------------------------------------------------------
    private incomeBean InterestIncome; //利息收入
    private incomeBean DepositInterestIncome; //存款利息收入
    private incomeBean FIInterestIncome; //债券利息收入
    private incomeBean BillInterestIncome; //票据利息收入
    private incomeBean SetSecurityInterestIncome; //资产支持证券利息收入
    private incomeBean REInterestIncome; //融券回购利息收入
    private incomeBean OtherInterestIncome; //其他利息收入
    //---------------------------------------------------------------------------
    private incomeBean SumSetChange; //汇总的差价收入

    private incomeBean AllSetChange; //所有类型的差价收入

    private incomeBean ChangeIncomeCauseRate; //汇率变动收入(差价收入)

    private incomeBean EQPriceDifference; //股票价格变动收入
    private incomeBean EQSetChange; //股票价格变动本位币收入
    private incomeBean EQChangeIncomeCauseRate; //股票汇率变动收入(差价收入)

    private incomeBean FIPriceDifference; //债券价格变动收入
    private incomeBean FISetChange; //债券价格变动本位币收入
    private incomeBean FIChangeIncomeCauseRate; //债券汇率变动收入(差价收入)

    private incomeBean FUNDPriceDifference; //基金价格变动收入
    private incomeBean FUNDSetChange; //基金价格变动本位币收入
    private incomeBean FUNDChangeIncomeCauseRate; //基金汇率变动收入(差价收入)

    private incomeBean CashPriceDifference; //票据价格变动收入
    private incomeBean CashSetChange; //票据价格变动本位币收入
    private incomeBean CashChangeIncomeCauseRate; //票据汇率变动收入(差价收入)

    private incomeBean OtherPriceDifference; //其它价格变动收入
    private incomeBean OtherSetChange; //其它价格变动本位币收入
    private incomeBean OtherChangeIncomeCauseRate; //其它汇率变动收入(差价收入)
    //---------------------------------------------------------------------------
    private incomeBean DERIVESetChange; //衍生金融工具汇总
    private incomeBean DERIVEChangeIncomeCauseRate; //衍生金融工具汇率变动

    private incomeBean DERIVE3SetChange; //衍生金融工具
    private incomeBean DERIVE3PriceDifference; //差价收入
    private incomeBean DERIVE3ChangeIncomeCauseRate; //汇率变动收入

    private incomeBean DERIVE5SetChange; //衍生金融工具
    private incomeBean DERIVE5PriceDifference; //差价收入
    private incomeBean DERIVE5ChangeIncomeCauseRate; //汇率变动收入
    //---------------------------------------------------------------------------
    private incomeBean FUNDDividedIncome; //基金红利收入
    private incomeBean DIVIDEDSetChange; //股利收入
    private incomeBean DrawbackInmcome; //退税收入
    //---------------------------------------------------------------------------
    private incomeBean FAIRSetChangeIncome; //公允价值变动
    private incomeBean FAIRPriceDifference; //差价收入
    private incomeBean FAIRChangeIncomeCauseRate; //汇率变动收入
    //---------------------------------------------------------------------------
    private incomeBean FOtherIncome; //其他收入
    //---------------------------------------------------------------------------
    //===========================================================================
    private incomeBean FeeManagerPay; //管理人报酬
    private incomeBean FeeTrusteeship; //托管费
    private incomeBean FeeTrade; //交易费用
    private incomeBean FeeReInterest; //融资回购利息支出
    private incomeBean FeeFX; //汇兑损失
    private incomeBean FeeTax; //税费
    private incomeBean FeeDevalue; //减值损失
    private incomeBean FeeOther; //其他费用
    //===========================================================================
    private incomeBean SumIncome; //收入
    private incomeBean SumFee; //费用
    private incomeBean SumNetIncome; //净收益
    private incomeBean RealizedIncome; //已实现收益
    private incomeBean FAIRChangeIncome; //公允价值变动收益
    //===========================================================================

    public IncomeReport() {
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
        if (sCuryCode.length() > 0 && sCuryCode.equalsIgnoreCase("CNY")) { //若为人民币，则获取期初、期末汇率
//         this.getQCRate();//获取期初汇率
            this.getQMRate(); //获取期末汇率
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
        CltIncome(); //收入显示
        //===========================
        CltCalcFee(); //费用显示
        //===========================
        CltNetIncome(); //净收益显示
        //===========================
        if (finBuf.length() > 2) {
            return finBuf.toString().substring(0,
                                               finBuf.toString().length() - 2);
        } else {
            return "";
        }
    }

//********************************************************************************
//******
//控制计算后的报表显示的方法
//******
       /**
        * 控制所有收入类的显示
        * @throws YssException
        */
       private void CltIncome() throws YssException {
           CltcalcInterestIncome(); //利息收入
           CltcalcPriceDifferenceIncome(); //差价收入
           CltcalcDERIVEIncome(); //衍生金融工具收益
           CltCalcFUNDDividedIncome(); //基金红利收入
           CltCalcDIVIDEDIncome(); //股利收入
           CltCalcDrawbackIncome(); //退税收入
           CltCalcFAIRChangeIncome(); //公允价值变动收益
           CltCalcOtherIncome(); //其他收入
       }

    /**
     * 控制费用的显示
     * @throws YssException
     */
    private void CltCalcFee() throws YssException {
        //--先在此进行相关的计算 ---
        calcManagerFee();
        calcTrusteeship();
        calcTradeFee();
        calcREInterest();
        calcFX();
        calcFeeTax();
        calcFeeDevalue();
        calcOtherFee();
        //--下面进行显示的处理----------------------------------------------------
        buildEmpty("二、费用");
        spellShowItemBuffer(".   1、管理人报酬", this.FeeManagerPay.fsValue,
                            this.FeeManagerPay.ljValue);
        spellShowItemBuffer(".   2、托管费", this.FeeTrusteeship.fsValue,
                            this.FeeTrusteeship.ljValue);
        spellShowItemBuffer(".   3、交易费用", this.FeeTrade.fsValue,
                            this.FeeTrade.ljValue);
        spellShowItemBuffer(".   4、融资回购利息支出", this.FeeReInterest.fsValue,
                            this.FeeReInterest.ljValue);
        spellShowItemBuffer(".   5、汇兑损失", this.FeeFX.fsValue,
                            this.FeeFX.ljValue);
        spellShowItemBuffer(".   6、税费", this.FeeTax.fsValue,
                            this.FeeTax.ljValue);
        spellShowItemBuffer(".   7、减值损失", this.FeeDevalue.fsValue,
                            this.FeeDevalue.ljValue);
        spellShowItemBuffer(".   8、其他费用", this.FeeOther.fsValue,
                            this.FeeOther.ljValue);
    }

    /**
     * 控制净收益的显示
     * @throws YssException
     */
    private void CltNetIncome() throws YssException {
        calcSumNetIncome(); //净收益
        calcRealizedIncome(); //已实现收益
        calcFAIRChangeIncome(); //公允价值变动收益
        spellShowItemBuffer("三、净收益", this.SumNetIncome.fsValue,
                            this.SumNetIncome.ljValue);
        spellShowItemBuffer(".   其中：已实现收益", this.RealizedIncome.fsValue,
                            this.RealizedIncome.ljValue);
        spellShowItemBuffer(".         公允价值变动收益", this.FAIRChangeIncome.fsValue,
                            this.FAIRChangeIncome.ljValue);
    }

    /**
     * 控制类型收入的显示
     * @throws YssException
     */
    private void CltcalcInterestIncome() throws YssException {
        CltCalcInterestIncome(); //计算各个不同类型的本位币利息收入
        buildEmpty("一、收入"); //使金额中显示为空
        spellShowItemBuffer(".   1、利息收入", this.InterestIncome.fsValue,
                            this.InterestIncome.ljValue);
        spellShowItemBuffer(".      其中：存款利息收入", this.DepositInterestIncome.fsValue,
                            this.DepositInterestIncome.ljValue);
        spellShowItemBuffer(".            债券利息收入", this.FIInterestIncome.fsValue,
                            this.FIInterestIncome.ljValue);
        spellShowItemBuffer(".            票据利息收入", this.BillInterestIncome.fsValue,
                            this.BillInterestIncome.ljValue);
        spellShowItemBuffer(".            资产支持证券利息收入",
                            this.SetSecurityInterestIncome.fsValue,
                            this.SetSecurityInterestIncome.ljValue);
        spellShowItemBuffer(".            融券回购利息收入", this.REInterestIncome.fsValue,
                            this.REInterestIncome.ljValue);
        spellShowItemBuffer(".            其他利息收入", this.OtherInterestIncome.fsValue,
                            this.OtherInterestIncome.ljValue);
    }

    /**
     * 控制差价收入的显示
     * @throws YssException
     */
    private void CltcalcPriceDifferenceIncome() throws YssException {
        //--先进行相关的计算
        calcEQPriceDifference();
        calcFIPriceDifference();
        calcFUNDPriceDifference();
        calcCashPriceDifference();
        calcOtherPriceDifference();
        calcChangeIncomeCauseRate(); //放在最后，需要前面计算的数据
        //===--------------------------------------------------------------------
        calcDIVIDEDIncome(); //计算股利收入,放在此处是因为,在计算总的差价收入时需要此数据
        //===--------------------------------------------------------------------
        calcSumChangeSet(); //在这里使用股利收入计算的数据，计算汇总的差价收入
        //--对显示进行处理 ------------------------------------------------------------
        spellShowItemBuffer(".   2、差价收入", this.SumSetChange.fsValue,
                            this.SumSetChange.ljValue);
        spellShowItemBuffer(".      其中：股票价格变动收入", this.EQPriceDifference.fsValue,
                            this.EQPriceDifference.ljValue);

        spellShowItemBuffer(".            债券价格变动收入", this.FIPriceDifference.fsValue,
                            this.FIPriceDifference.ljValue);

        spellShowItemBuffer(".            基金价格变动收入",
                            this.FUNDPriceDifference.fsValue,
                            this.FUNDPriceDifference.ljValue);

        spellShowItemBuffer(".            票据价格变动收入",
                            this.CashPriceDifference.fsValue,
                            this.CashPriceDifference.ljValue);

        spellShowItemBuffer(".            其他价格变动收入",
                            this.OtherPriceDifference.fsValue,
                            this.OtherPriceDifference.ljValue);

        spellShowItemBuffer(".            汇率变动收入", this.ChangeIncomeCauseRate.fsValue,
                            this.ChangeIncomeCauseRate.ljValue);
    }

    /**
     * 控制衍生金融工具收益的显示
     * @throws YssException
     */
    private void CltcalcDERIVEIncome() throws YssException {
        calcSetDERIVEIncome(); //计算衍生金融工具收益
        calcDERIVEChangeIncomeCauseRate(); //计算衍生金融工具汇率变动收益
        spellShowItemBuffer(".   3、衍生金融工具收益", this.DERIVESetChange.fsValue,
                            this.DERIVESetChange.ljValue);
        spellShowItemBuffer(".      其中：汇率变动收益",
                            this.DERIVEChangeIncomeCauseRate.fsValue,
                            this.DERIVEChangeIncomeCauseRate.ljValue);
    }

    /**
     * 控制基金红利收入的显示
     * @throws YssException
     */
    private void CltCalcFUNDDividedIncome() throws YssException {
        calcFUNDDividedIncome(); //计算基金红利收入
        spellShowItemBuffer(".   4、基金红利收入", this.FUNDDividedIncome.fsValue,
                            this.FUNDDividedIncome.ljValue);
    }

    /**
     * 控制股利收入显示
     * @throws YssException
     */
    private void CltCalcDIVIDEDIncome() throws YssException {
        if (null == DIVIDEDSetChange) { //因为计算股利的方法调用放在计算差价收入处，故在此处做一个判断，防止可能出现的null
            DIVIDEDSetChange = new incomeBean();
        }
        spellShowItemBuffer(".   5、股利收入", this.DIVIDEDSetChange.fsValue,
                            this.DIVIDEDSetChange.ljValue);
    }

    /**
     * 控制退税收入显示
     * @throws YssException
     */
    private void CltCalcDrawbackIncome() throws YssException {
        calcDrawbackIncome();
        spellShowItemBuffer(".   6、退税收入", this.DrawbackInmcome.fsValue,
                            this.DrawbackInmcome.ljValue);
    }

    /**
     * 控制公允价值变动的显示
     * @throws YssException
     */
    private void CltCalcFAIRChangeIncome() throws YssException {
        //--先进行相关的计算
        calcFAIRPriceDifferenceIncome();
        calcFAIRSetChangeIncome();
        calcFAIRChangeCauseRateIncome();
        //--对显示进行处理 -------------------------------------------------------------
        spellShowItemBuffer(".   7、公允价值变动收益", this.FAIRSetChangeIncome.fsValue,
                            this.FAIRSetChangeIncome.ljValue);
        spellShowItemBuffer(".      其中：价格变动收益", this.FAIRPriceDifference.fsValue,
                            this.FAIRPriceDifference.ljValue);
        spellShowItemBuffer(".            汇率变动收益",
                            this.FAIRChangeIncomeCauseRate.fsValue,
                            this.FAIRChangeIncomeCauseRate.ljValue);
    }

    /**
     * 控制其它收入的显示
     * @throws YssException
     */
    private void CltCalcOtherIncome() throws YssException {
        calcOtherIncome(); //计算其它收入
        spellShowItemBuffer(".   8、其他收入", this.FOtherIncome.fsValue,
                            this.FOtherIncome.ljValue);
    }

    //******************************************************************************
     //******
      //计算收入的方法
      //******

       /**
        * 计算其他收入
        * @return incomeBean
        * @throws YssException
        */
       private incomeBean calcOtherIncome() throws YssException {
           FOtherIncome = new incomeBean();
           this.FOtherIncome = this.calcChangeSetIncomeWithAcctCode("D",
               "6302", this.FOtherIncome);
           return FOtherIncome;
       }

    /**
     * 计算公允价值变动汇率变动收益
     * @throws YssException
     */
    private void calcFAIRChangeCauseRateIncome() throws YssException {
        if (null != this.FAIRPriceDifference &&
            null != this.FAIRSetChangeIncome) {
            this.FAIRChangeIncomeCauseRate = new incomeBean();
            //发生-- 本位币金额 - 价格变动收入
            FAIRChangeIncomeCauseRate.fsValue = FAIRSetChangeIncome.fsValue -
                FAIRPriceDifference.fsValue;
            //累计-- 本位币金额 - 价格变动收入
            FAIRChangeIncomeCauseRate.ljValue = FAIRPriceDifference.ljValue -
                FAIRSetChangeIncome.ljValue;
        }
    }

    /**
     * 计算公允价值变动收入
     * @throws YssException
     */
    private void calcFAIRSetChangeIncome() throws YssException {
        this.FAIRSetChangeIncome = new incomeBean();
        this.FAIRSetChangeIncome = this.calcChangeSetIncomeWithAcctCode("D",
            "6101", this.FAIRSetChangeIncome);
    }

    /**
     * 计算公允价值变动差价收入
     * @throws YssException
     */
    private void calcFAIRPriceDifferenceIncome() throws YssException {
        this.FAIRPriceDifference = new incomeBean();
        this.FAIRPriceDifference = this.calcChangeIncomeWithAcctCode("D", "6101",
            this.FAIRPriceDifference);
    }

    /**
     * 计算基金红利收入
     * @throws YssException
     */
    private void calcFUNDDividedIncome() throws YssException {
        this.FUNDDividedIncome = new incomeBean();
    }

    /**
     * 计算股利收入
     * @throws YssException
     */
    private void calcDIVIDEDIncome() throws YssException {
        this.DIVIDEDSetChange = new incomeBean();
        this.DIVIDEDSetChange = this.calcChangeSetIncome(this.
            PRICEDIFFERENCE_KM_DV, DIVIDEDSetChange); //股票分红
    }

    /**
     * 计算退税收入
     * @throws YssException
     */
    private void calcDrawbackIncome() throws YssException {
        this.DrawbackInmcome = new incomeBean();
    }

    /**
     * 计算衍生金融工具收益
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcSetDERIVEIncome() throws YssException {
        if (null != this.DERIVE3SetChange && null != DERIVE5SetChange) { //此处的对象在计算汇总的差价收入时，已经生成并计算
            DERIVESetChange = new incomeBean();
            //发生 -- 两个类型的衍生金融工具的金额相加
            DERIVESetChange.fsValue = DERIVE3SetChange.fsValue +
                DERIVE5SetChange.fsValue;
            //累计
            DERIVESetChange.ljValue = DERIVE3SetChange.ljValue +
                DERIVE5SetChange.ljValue;
        }
        return DERIVESetChange;
    }

    /**
     * 计算衍生金融工具汇率变动收益
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcDERIVEChangeIncomeCauseRate() throws YssException {
        calcDERIVE3ChangeIncomeCauseRate(); //计算衍生金融工具3中汇率变动收入部分
        calcDERIVE5ChangeIncomeCauseRate(); //计算衍生金融工具5中汇率变动收入部分
        if (null != DERIVE3ChangeIncomeCauseRate &&
            null != DERIVE5ChangeIncomeCauseRate) {
            DERIVEChangeIncomeCauseRate = new incomeBean();
            //发生 -- 两个类型的衍生金融工具的金额相加
            DERIVEChangeIncomeCauseRate.fsValue = DERIVE3ChangeIncomeCauseRate.
                fsValue + DERIVE5ChangeIncomeCauseRate.fsValue;
            //累计
            DERIVEChangeIncomeCauseRate.ljValue = DERIVE3ChangeIncomeCauseRate.
                ljValue + DERIVE5ChangeIncomeCauseRate.ljValue;
        }
        return DERIVEChangeIncomeCauseRate;
    }

    /**
     * 计算衍生金融工具3中汇率变动收入部分
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcDERIVE3ChangeIncomeCauseRate() throws YssException {
        calcDERIVE3PriceDifference(); //计算衍生金融工具3的差价收入
        this.DERIVE3ChangeIncomeCauseRate = calcChangeIncomeCauseRate(this.
            DERIVE3SetChange,
            this.DERIVE3PriceDifference);
        return this.DERIVE3ChangeIncomeCauseRate;
    }

    /**
     * 计算衍生金融工具5中汇率变动收入部分
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcDERIVE5ChangeIncomeCauseRate() throws YssException {
        calcDERIVE5PriceDifference(); //计算衍生金融工具5的差价收入
        this.DERIVE5ChangeIncomeCauseRate = calcChangeIncomeCauseRate(this.
            DERIVE5SetChange,
            this.DERIVE5PriceDifference);
        return this.DERIVE5ChangeIncomeCauseRate;
    }

    /**
     * 计算衍生金融工具3的差价收入
     * @throws YssException
     */
    private void calcDERIVE3PriceDifference() throws YssException {
        DERIVE3PriceDifference = new incomeBean();
        DERIVE3PriceDifference = calcPriceDifference(this.
            PRICEDIFFERENCE_KM_DERIVE3,
            DERIVE3PriceDifference);
    }

    /**
     * 计算衍生金融工具5的差价收入
     * @throws YssException
     */
    private void calcDERIVE5PriceDifference() throws YssException {
        DERIVE5PriceDifference = new incomeBean();
        DERIVE5PriceDifference = calcPriceDifference(this.
            PRICEDIFFERENCE_KM_DERIVE5,
            DERIVE5PriceDifference);
    }

    /**
     * 计算各个不同类型的本位币利息收入
     * @throws YssException
     */
    private void CltCalcInterestIncome() throws YssException {
        this.DepositInterestIncome = new incomeBean();
        DepositInterestIncome.itemCode = "INTEREST_KM_DEPOSIT";
        DepositInterestIncome = this.calcSetInterestIncome(INTEREST_KM_DEPOSIT,
            DepositInterestIncome); //存款利息收入
        //----------------------------------------------------------------------------//
        this.FIInterestIncome = new incomeBean();
        FIInterestIncome.itemCode = "INTEREST_KM_FI";
        FIInterestIncome = this.calcSetInterestIncome(INTEREST_KM_FI,
            FIInterestIncome); //债券利息收入
        //----------------------------------------------------------------------------//
        this.BillInterestIncome = new incomeBean();
        BillInterestIncome.itemCode = "INTEREST_KM_BILL";
        //BillInterestIncome = this.calcSetInterestIncome(INTEREST_KM_BILL, BillInterestIncome);//票据利息收入
        BillInterestIncome.fsValue = 0D; // 暂时设为0
        BillInterestIncome.ljValue = 0D;
        //----------------------------------------------------------------------------//
        this.SetSecurityInterestIncome = new incomeBean();
        SetSecurityInterestIncome.itemCode = "INTEREST_KM_SETSECURITY";
        SetSecurityInterestIncome = this.calcSetInterestIncome(
            INTEREST_KM_SETSECURITY, SetSecurityInterestIncome); //资产支持收入
        //----------------------------------------------------------------------------//
        this.REInterestIncome = new incomeBean();
        REInterestIncome.itemCode = "INTEREST_KM_RE";
        REInterestIncome = this.calcSetInterestIncome(INTEREST_KM_RE,
            REInterestIncome); //融券回购收入
        //----------------------------------------------------------------------------//
        this.OtherInterestIncome = new incomeBean();
        OtherInterestIncome.itemCode = "INTEREST_KM_OTHER";
        OtherInterestIncome = this.calcSetInterestIncome(INTEREST_KM_OTHER,
            OtherInterestIncome); //其他收入
        //----------------------------------------------------------------------------//
        this.InterestIncome = new incomeBean();
        InterestIncome.itemCode = "INTEREST_KM_ALL";
        InterestIncome = this.calcSetInterestIncome(INTEREST_KM_ALL,
            InterestIncome); //所有项利息收入

    }

    /**
     * 计算汇总的差价收入
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcSumChangeSet() throws YssException {
        this.CltCalcExceptChangeSetIncome(); //计算除外类型的本位币收入
        if (null != this.AllSetChange) {
            this.SumSetChange = new incomeBean();
            //发生 -- 总收入 - 衍生金融工具收益 - 股利收入
            SumSetChange.fsValue = AllSetChange.fsValue - DERIVE3SetChange.fsValue -
                DERIVE5SetChange.fsValue - DIVIDEDSetChange.fsValue;
            //累计
            SumSetChange.ljValue = AllSetChange.ljValue - DERIVE3SetChange.ljValue -
                DERIVE5SetChange.ljValue - DIVIDEDSetChange.ljValue;
        }
        return SumSetChange;
    }

    /**
     * 计算除外类型的本位币收入
     * @throws YssException
     */
    private void CltCalcExceptChangeSetIncome() throws YssException {
        this.DERIVE3SetChange = new incomeBean();
        DERIVE3SetChange.itemCode = "PRICEDIFFERENCE_KM_DERIVE3";
        DERIVE3SetChange = this.calcChangeSetIncome(this.
            PRICEDIFFERENCE_KM_DERIVE3,
            DERIVE3SetChange); //衍生金融工具3
        //-----------------------------------------------------------------------------//
        this.DERIVE5SetChange = new incomeBean();
        DERIVE5SetChange.itemCode = "PRICEDIFFERENCE_KM_DERIVE5";
        DERIVE5SetChange = this.calcChangeSetIncome(this.
            PRICEDIFFERENCE_KM_DERIVE5,
            DERIVE5SetChange); //衍生金融工具5
    }

    /**
     * 计算各个不同类型的本位币差价收入
     * @throws YssException
     */
    private void CltCalcChangeSetIncome() throws YssException {
        this.EQSetChange = new incomeBean();
        EQSetChange.itemCode = "PRICEDIFFERENCE_KM_EQ";
        EQSetChange = this.calcChangeSetIncome(PRICEDIFFERENCE_KM_EQ,
                                               EQSetChange); //股票
        //-----------------------------------------------------------------------------//
        this.FISetChange = new incomeBean();
        FISetChange.itemCode = "PRICEDIFFERENCE_KM_FI";
        FISetChange = this.calcChangeSetIncome(PRICEDIFFERENCE_KM_FI,
                                               FISetChange); //债券
        //-----------------------------------------------------------------------------//
        this.FUNDSetChange = new incomeBean();
        FUNDSetChange.itemCode = "PRICEDIFFERENCE_KM_FUND";
        FUNDSetChange = this.calcChangeSetIncome(PRICEDIFFERENCE_KM_FUND,
                                                 FUNDSetChange); //基金
        //----------------------------------------------------------------------------//
        this.CashSetChange = new incomeBean();
        CashSetChange.itemCode = "PRICEDIFFERENCE_KM_CASH";
        //暂时不做处理
//      CashSetChange = this.calcChangeSetIncome(this.
//                                               PRICEDIFFERENCE_KM_CASH,
//                                               CashSetChange); //票据
        //----------------------------------------------------------------------------//
        this.OtherSetChange = new incomeBean();
        OtherSetChange.itemCode = "PRICEDIFFERENCE_KM_OTHER";
        //暂时不做处理
//      OtherSetChange = this.calcChangeSetIncome(this.
//                                                PRICEDIFFERENCE_KM_CASH,
//                                                OtherSetChange); //其它
        //-----------------------------------------------------------------------------//
        this.AllSetChange = new incomeBean();
        AllSetChange.itemCode = "PRICEDIFFERENCE_KM_ALL";
        AllSetChange = this.calcChangeSetIncome(PRICEDIFFERENCE_KM_ALL,
                                                AllSetChange); //所有类型的差价收入
    }

    /**
     * 计算票据的价格变动收入
     * @throws YssException
     */
    private void calcCashPriceDifference() throws YssException {
        CashPriceDifference = new incomeBean();
//      calcPriceDifference(this.PRICEDIFFERENCE_KM_CASH, CashPriceDifference);
        CashPriceDifference.fsValue = 0D; //暂时为０
        CashPriceDifference.ljValue = 0D;
    }

    /**
     * 计算其他价格变动收入
     * @throws YssException
     */
    private void calcOtherPriceDifference() throws YssException {
        OtherPriceDifference = new incomeBean();
//      calcPriceDifference(this.PRICEDIFFERENCE_KM_OTHER, OtherPriceDifference);
        OtherPriceDifference.fsValue = 0D; //暂时为０
        OtherPriceDifference.ljValue = 0D;
    }

    /**
     * 计算股票的价格变动收入
     * @throws YssException
     */
    private void calcEQPriceDifference() throws YssException {
        EQPriceDifference = new incomeBean();
        calcPriceDifference(this.PRICEDIFFERENCE_KM_EQ, EQPriceDifference);
    }

    /**
     * 计算债券的价格变动收入
     * @throws YssException
     */
    private void calcFIPriceDifference() throws YssException {
        FIPriceDifference = new incomeBean();
        calcPriceDifference(this.PRICEDIFFERENCE_KM_FI, FIPriceDifference);
    }

    /**
     * 计算基金的价格变动收入
     * @throws YssException
     */
    private void calcFUNDPriceDifference() throws YssException {
        FUNDPriceDifference = new incomeBean();
        calcPriceDifference(this.PRICEDIFFERENCE_KM_FUND, FUNDPriceDifference);
    }

    /**
     * 计算汇率变动收入(差价收入)
     * @return double
     * @throws YssException
     */
    private incomeBean calcChangeIncomeCauseRate() throws YssException {
        CltCalcChangeSetIncome(); // 先计算出各个类型的本位币差价收入
        calcEQChangeIncomeCauseRate();
        calcFIChangeIncomeCauseRate();
        calcFUNDChangeIncomeCauseRate();
        calcCashChangeIncomeCauseRate();
        calcOtherChangeIncomeCauseRate();
        ChangeIncomeCauseRate = new incomeBean();
        //发生 ---将各项的发生额相加
        ChangeIncomeCauseRate.fsValue = EQChangeIncomeCauseRate.fsValue +
            FIChangeIncomeCauseRate.fsValue + FUNDChangeIncomeCauseRate.fsValue +
            CashChangeIncomeCauseRate.fsValue +
            OtherChangeIncomeCauseRate.fsValue;
        //累计 ---将各项的累计额相加
        ChangeIncomeCauseRate.ljValue = EQChangeIncomeCauseRate.ljValue +
            FIChangeIncomeCauseRate.ljValue + FUNDChangeIncomeCauseRate.ljValue +
            CashChangeIncomeCauseRate.ljValue +
            OtherChangeIncomeCauseRate.ljValue;
        return ChangeIncomeCauseRate;
    }

    /**
     * 计算股票差价收入中汇率变动收入部分
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcEQChangeIncomeCauseRate() throws YssException {
        this.EQChangeIncomeCauseRate = calcChangeIncomeCauseRate(this.EQSetChange,
            this.EQPriceDifference);
        return this.EQChangeIncomeCauseRate;
    }

    /**
     * 计算债券差价收入中汇率变动收入部分
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcFIChangeIncomeCauseRate() throws YssException {
        this.FIChangeIncomeCauseRate = calcChangeIncomeCauseRate(this.FISetChange,
            this.FIPriceDifference);
        return this.FIChangeIncomeCauseRate;
    }

    /**
     * 计算基金差价收入中汇率变动收入部分
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcFUNDChangeIncomeCauseRate() throws YssException {
        this.FUNDChangeIncomeCauseRate = calcChangeIncomeCauseRate(this.
            FUNDSetChange, this.FUNDPriceDifference);
        return this.FUNDChangeIncomeCauseRate;
    }

    /**
     * 计算票据差价收入中汇率变动收入部分
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcCashChangeIncomeCauseRate() throws YssException {
        this.CashChangeIncomeCauseRate = calcChangeIncomeCauseRate(this.
            CashSetChange, this.CashPriceDifference);
        return this.CashChangeIncomeCauseRate;
    }

    /**
     * 计算票据差价收入中汇率变动收入部分
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcOtherChangeIncomeCauseRate() throws YssException {
        this.OtherChangeIncomeCauseRate = calcChangeIncomeCauseRate(this.
            OtherSetChange, this.OtherPriceDifference);
        return this.OtherChangeIncomeCauseRate;
    }

    //*******
     //计算收入方法结束
     //*******
      //*****************************************************************************

       //*****************************************************************************
        //******
         //费用计算方法开始
         //******
          /**
           * 其他费用
           * @return incomeBean
           * @throws YssException
           */
          private incomeBean calcOtherFee() throws YssException {
              this.FeeOther = new incomeBean();
              this.FeeOther = this.calcChangeSetIncomeWithAcctCode("J",
                  "6605", this.FeeOther);
              return FeeOther;
          }

    /**
     * 减值损失
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcFeeDevalue() throws YssException {
        this.FeeDevalue = new incomeBean();
        return FeeDevalue;
    }

    /**
     * 税费
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcFeeTax() throws YssException {
        this.FeeTax = new incomeBean();
        return FeeTax;
    }

    /**
     * 汇兑损失
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcFX() throws YssException {
        this.FeeFX = new incomeBean();
        this.FeeFX = this.calcChangeSetIncomeWithAcctCode("D",
            "6061", this.FeeFX);
        this.FeeFX.fsValue = YssD.mul(FeeFX.fsValue, -1); //此处为损失，乘以-1
        this.FeeFX.ljValue = YssD.mul(FeeFX.ljValue, -1);
        return FeeFX;
    }

    /**
     * 融资回购利息支出
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcREInterest() throws YssException {
        this.FeeReInterest = new incomeBean();
        this.FeeReInterest = this.calcChangeSetIncomeWithAcctCode("J",
            "6411", this.FeeReInterest);
        return this.FeeReInterest;
    }

    /**
     * 交易费用
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcTradeFee() throws YssException {
        this.FeeTrade = new incomeBean();
        this.FeeTrade = this.calcChangeSetIncomeWithAcctCode("J",
            "6407", this.FeeTrade);
        return this.FeeTrade;
    }

    /**
     * 计算托管费
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcTrusteeship() throws YssException {
        this.FeeTrusteeship = new incomeBean();
        this.FeeTrusteeship = this.calcChangeSetIncomeWithAcctCode("J",
            "6404", this.FeeTrusteeship);
        return this.FeeTrusteeship;
    }

    /**
     * 计算管理人报酬
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcManagerFee() throws YssException {
        this.FeeManagerPay = new incomeBean();
        this.FeeManagerPay = this.calcChangeSetIncomeWithAcctCode("J",
            "6403", this.FeeManagerPay);
        return FeeManagerPay;
    }

    //*******
     //费用计算部分结束
     //*******
      //****************************************************************************

       //****************************************************************************
        //******
         //以下为计算报表中"净收益"部分的方法
         //******
          /**
           * 计算公允价值变动收益
           * @return incomeBean
           * @throws YssException
           */
          private incomeBean calcFAIRChangeIncome() throws YssException {
              this.FAIRChangeIncome = new incomeBean();
              FAIRChangeIncome = this.calcChangeSetIncomeWithAcctCode("D",
                  "6101", this.FAIRChangeIncome);
              return FAIRChangeIncome;
          }

    /**
     * 计算已实现收益
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcRealizedIncome() throws YssException {
        if (null != SumNetIncome && null != FAIRSetChangeIncome) {
            this.RealizedIncome = new incomeBean();
            //发生 净收益 - 公允价值变动收益
            RealizedIncome.fsValue = SumNetIncome.fsValue -
                FAIRSetChangeIncome.fsValue;
            //累计
            RealizedIncome.ljValue = SumNetIncome.ljValue -
                FAIRSetChangeIncome.ljValue;
        }
        return this.RealizedIncome;
    }

    /**
     * 计算净收益
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcSumNetIncome() throws YssException {
        calcSumIncome(); //收入汇总
        calcSumFee(); //费用的汇总
        if (null != SumFee && null != SumIncome) {
            this.SumNetIncome = new incomeBean();
            //发生-- 收入 - 费用
            SumNetIncome.fsValue = SumIncome.fsValue - SumFee.fsValue;
            //累计-- 收入 - 费用
            SumNetIncome.ljValue = SumIncome.ljValue - SumFee.ljValue;
        }
        return this.SumNetIncome;
    }

    /**
     * 计算费用的汇总
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcSumFee() throws YssException {
        double fsValue = 0D;
        double ljValue = 0D;
        this.SumFee = new incomeBean();
        if (null != FeeManagerPay && null != FeeTrusteeship
            && null != FeeTrade && null != FeeReInterest
            && null != FeeFX && null != FeeOther) {
            //发生 将所有不为默认0的费用的金额相加
            fsValue = FeeManagerPay.fsValue + FeeTrusteeship.fsValue +
                FeeTrade.fsValue +
                FeeReInterest.fsValue + FeeFX.fsValue + FeeOther.fsValue;
            //累计
            ljValue = FeeManagerPay.ljValue + FeeTrusteeship.ljValue +
                FeeTrade.ljValue +
                FeeReInterest.ljValue + FeeFX.ljValue + FeeOther.ljValue;
            SumFee.fsValue = fsValue;
            SumFee.ljValue = ljValue;
        }
        return SumFee;
    }

    /**
     * 计算收入汇总
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcSumIncome() throws YssException {
        double fsValue = 0D;
        double ljValue = 0D;
        this.SumIncome = new incomeBean();
        if (null != InterestIncome && null != SumSetChange &&
            null != DERIVESetChange && null != DIVIDEDSetChange &&
            null != FAIRSetChangeIncome && null != FOtherIncome) {
            //发生 将所有不默认为0的收入的金额相加
            fsValue = InterestIncome.fsValue + SumSetChange.fsValue +
                DERIVESetChange.fsValue + DIVIDEDSetChange.fsValue +
                FAIRSetChangeIncome.fsValue + FOtherIncome.fsValue;
            //累计
            ljValue = InterestIncome.ljValue + SumSetChange.ljValue +
                DERIVESetChange.ljValue + DIVIDEDSetChange.ljValue +
                FAIRSetChangeIncome.ljValue + FOtherIncome.ljValue;
            SumIncome.fsValue = fsValue;
            SumIncome.ljValue = ljValue;
        }
        return SumIncome;
    }

    //*****计算净收益部分结束
     //*********************************************************************************************************************************************//

     //********************************************************************************************************************************************//
     //*******
      //以下为报表计算中需要使用的公共方法部分
      //*******
       /**
        * 计算差价收入中汇率变动收入部分
        * @param setChange incomeBean 本位币收入
        * @param priceDifference incomeBean　价差
        * @return incomeBean　汇率变动计算值
        * @throws YssException
        */
       private incomeBean calcChangeIncomeCauseRate(incomeBean setChange,
           incomeBean priceDifference) throws
           YssException {
           double fsChangeIncomeCauseRate = 0D;
           double ljChangeIncomeCauseRate = 0D;
           incomeBean changeIncomeCauseRate = null;
           if (null != setChange && null != priceDifference) {
               //发生 本位币差价收入金额 - 价格变动收益
               fsChangeIncomeCauseRate = YssD.sub(setChange.fsValue,
                                                  priceDifference.fsValue);
               //累计
               ljChangeIncomeCauseRate = YssD.sub(setChange.ljValue,
                                                  priceDifference.ljValue);
           }
           changeIncomeCauseRate = new incomeBean();
           changeIncomeCauseRate.fsValue = fsChangeIncomeCauseRate;
           changeIncomeCauseRate.ljValue = ljChangeIncomeCauseRate;
           return changeIncomeCauseRate;
       }

    /**
     * 计算利息本位币收入( 贷方 )
     * @param Type int 科目类型
     * @param income incomeBean
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcSetInterestIncome(int Type, incomeBean income) throws
        YssException {
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        double fsSetInterestIncome = 0D; //发生
        double ljSetInterestIncome = 0D; //累计
        try {
            sqlBuf = new StringBuffer();
            sqlBuf.append(
                "select sum(FBAccCredit) as FBAccCredit,sum(FBCredit) as FBCredit from ");
            sqlBuf.append(sPrefixTableName + "LBAlANCE");
            sqlBuf.append(" where FAcctCode like ");
            switch (Type) {
                case INTEREST_KM_ALL:
                    sqlBuf.append(dbl.sqlString("6011%"));
                    break;
                case INTEREST_KM_FI:
                    sqlBuf.append(dbl.sqlString("601101%")); //债券类型
                    break;
                case INTEREST_KM_SETSECURITY:
                    sqlBuf.append(dbl.sqlString("601102%")); //资产支持证券类型
                    break;
                case INTEREST_KM_DEPOSIT:
                    sqlBuf.append(dbl.sqlString("601103%")); //存款利息
                    break;
                case INTEREST_KM_RE:
                    sqlBuf.append(dbl.sqlString("601104%")); // 融券回购类型
                    break;
                case INTEREST_KM_OTHER:
                    sqlBuf.append(dbl.sqlString("601199%")); //其它
                    break;
                default:
                    sqlBuf.append(dbl.sqlString("6011%"));
                    break;
            }
            sqlBuf.append(" and 	fisdetail=1");
            sqlBuf.append(" and 	FMonth in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")"); //
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                fsSetInterestIncome = rs.getDouble("FBCredit"); //发生
                ljSetInterestIncome = rs.getDouble("FBAccCredit"); //累计
            }
            if (this.QMRate != 0) { //若为人民币
                fsSetInterestIncome = YssFun.roundIt(YssD.div(fsSetInterestIncome,
                    this.QMRate), 2);
                ljSetInterestIncome = YssFun.roundIt(YssD.div(ljSetInterestIncome,
                    this.QMRate), 2);
            }
            if (null != income) {
                income.fsValue = fsSetInterestIncome;
                income.ljValue = ljSetInterestIncome;
            }
        } catch (Exception e) {
            throw new YssException("获取利息收入本位币收入出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return income;

    }

    /**
     * 计算价格变动收入(原币 贷方)
     * @param Type int
     * @paras incomeBean income
     * @return income
     * @throws YssException
     */
    private incomeBean calcPriceDifference(int Type, incomeBean income) throws
        YssException {
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        double fsPriceDifference = 0D; //发生
        double ljPriceDifference = 0D; //累计
        HashMap rateMap = null;
        double baseRate = 0D;
        double portRate = 0D;
        try {
            sqlBuf = new StringBuffer();
            sqlBuf.append("select sum(FCredit) as FCredit,sum(FAccCredit) as FAccCredit,FCurCode as FCuryCode from ");
            sqlBuf.append(sPrefixTableName + "LBAlANCE");
            sqlBuf.append(" where FAcctCode like ");
            switch (Type) {
                case PRICEDIFFERENCE_KM_ALL:
                    sqlBuf.append(dbl.sqlString("6111%"));
                    break;
                case PRICEDIFFERENCE_KM_EQ:
                    sqlBuf.append(dbl.sqlString("611101%")); //股票类型
                    break;
                case PRICEDIFFERENCE_KM_FI:
                    sqlBuf.append(dbl.sqlString("611102%")); //债券类型
                    break;
                case PRICEDIFFERENCE_KM_DERIVE3:
                    sqlBuf.append(dbl.sqlString("611103%")); //衍生金融工具
                    break;
                case PRICEDIFFERENCE_KM_FUND:
                    sqlBuf.append(dbl.sqlString("611104%")); //基金类型
                    break;
                case PRICEDIFFERENCE_KM_DERIVE5:
                    sqlBuf.append(dbl.sqlString("611105%")); //衍生金融工具
                    break;
                case PRICEDIFFERENCE_KM_DV:
                    sqlBuf.append(dbl.sqlString("611199%")); //股利收入
                    break;
                default:
                    sqlBuf.append(dbl.sqlString("6111%"));
                    break;
            }
            sqlBuf.append(" and 	fisdetail=1");
            sqlBuf.append(" and FMonth in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")");
            sqlBuf.append(" group by FAcctCode,FCurCode");
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                rateMap = this.getRate(this.beginDate, rs.getString("FCuryCode")); //获取此种货币的期初汇率
                if (null != rateMap && rateMap.size() > 0) {
                    baseRate = ( (Double) rateMap.get("baseRate")).doubleValue(); //获取基础汇率
                    portRate = ( (Double) rateMap.get("portRate")).doubleValue(); //获取组合汇率
                    if (baseRate == 0 || portRate == 0) {
                        throw new YssException("获取" + rs.getString("FCuryCode") +
                                               "货币的汇率出现异常！");
                    }
                    fsPriceDifference +=
                        YssFun.roundIt(rs.getDouble("FCredit") * baseRate /
                                       portRate,
                                       2); //将不同币种的发生金额计算并汇总
                    ljPriceDifference +=
                        YssFun.roundIt(rs.getDouble("FAccCredit") * baseRate /
                                       portRate,
                                       2); //将不同币种的累计金额计算并汇总
                }
            }
            if (this.QMRate != 0) { //若为人民币
                fsPriceDifference = YssFun.roundIt(YssD.div(fsPriceDifference,
                    this.QMRate), 2);
                ljPriceDifference = YssFun.roundIt(YssD.div(ljPriceDifference,
                    this.QMRate), 2);
            }
            if (null != income) {
                income.fsValue = fsPriceDifference;
                income.ljValue = fsPriceDifference;
            }
        } catch (Exception e) {
            throw new YssException("获取差价收入出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return income;
    }

    /**
     * 计算价格变动本位币收入( 贷方 )
     * @param type 科目类型
     * @param getColumnType 取列类型,发生、累计
     * @return imcomeBean
     * @throws YssException
     */
    private incomeBean calcChangeSetIncome(int type, incomeBean income) throws
        YssException {
//      double changeSetIncome = 0D;
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        double fsChangeSet = 0D; //发生
        double ljChangeSet = 0D; //累计
        try {
            sqlBuf = new StringBuffer();
            sqlBuf.append(
                "select sum(FBAccCredit) as FBAccCredit,sum(FBCredit) as FBCredit from ");
            sqlBuf.append(sPrefixTableName + "LBAlANCE");
            sqlBuf.append(" where FAcctCode like ");
            switch (type) {
                case PRICEDIFFERENCE_KM_ALL:
                    sqlBuf.append(dbl.sqlString("6111%"));
                    break;
                case PRICEDIFFERENCE_KM_EQ:
                    sqlBuf.append(dbl.sqlString("611101%")); //股票类型
                    break;
                case PRICEDIFFERENCE_KM_FI:
                    sqlBuf.append(dbl.sqlString("611102%")); //债券类型
                    break;
                case PRICEDIFFERENCE_KM_DERIVE3:
                    sqlBuf.append(dbl.sqlString("611103%")); //衍生金融工具
                    break;
                case PRICEDIFFERENCE_KM_FUND:
                    sqlBuf.append(dbl.sqlString("611104%")); //基金类型
                    break;
                case PRICEDIFFERENCE_KM_DERIVE5:
                    sqlBuf.append(dbl.sqlString("611105%")); //衍生金融工具
                    break;
                case PRICEDIFFERENCE_KM_DV:
                    sqlBuf.append(dbl.sqlString("611199%")); //股利收入
                    break;
                default:
                    sqlBuf.append(dbl.sqlString("6111%"));
                    break;
            }
            sqlBuf.append(" and 	fisdetail=1");
            sqlBuf.append(" and 	FMonth in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")"); //
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                fsChangeSet = rs.getDouble("FBCredit"); //发生
                ljChangeSet = rs.getDouble("FBAccCredit"); //累计
            }
            if (this.QMRate != 0) { //若为人民币
                fsChangeSet = YssFun.roundIt(YssD.div(fsChangeSet, this.QMRate), 2);
                ljChangeSet = YssFun.roundIt(YssD.div(ljChangeSet, this.QMRate), 2);
            }
            if (null != income) {
                income.fsValue = fsChangeSet;
                income.ljValue = ljChangeSet;
            }
        } catch (Exception e) {
            throw new YssException("获取价格变动本位币收入出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return income;
    }

    /**
     * 通过科目及借贷类型来计算价格组合变动收入
     * @param JDType String
     * @param AcctCode String
     * @param income incomeBean
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcChangeSetIncomeWithAcctCode(String JDType,
        String AcctCode, incomeBean income) throws YssException {
//      double changeSetIncome = 0D;
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        double fsChangeSet = 0D;
        double ljChangeSet = 0D;
        try {
            sqlBuf = new StringBuffer();
            if (JDType.equalsIgnoreCase("D")) { //贷方
                sqlBuf.append(
                    "select sum(FBAccCredit) as FBAccCredit,sum(FBCredit) as FBCredit from ");
            } else if (JDType.equalsIgnoreCase("J")) { //借方
                sqlBuf.append(
                    "select sum(FBAccDebit) as FBAccDebit,sum(FBDebit) as FBDebit from ");
            }
            sqlBuf.append(sPrefixTableName + "LBAlANCE");
            sqlBuf.append(" where FAcctCode like ");
            sqlBuf.append(dbl.sqlString(AcctCode + "%"));
            sqlBuf.append(" and 	fisdetail=1");
            sqlBuf.append(" and 	FMonth in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")"); //
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                if (JDType.equalsIgnoreCase("D")) { //贷方
                    fsChangeSet = rs.getDouble("FBCredit"); //发生
                    ljChangeSet = rs.getDouble("FBAccCredit"); //累计
                } else if (JDType.equalsIgnoreCase("J")) { //借方
                    fsChangeSet = rs.getDouble("FBDebit");
                    ljChangeSet = rs.getDouble("FBAccDebit");
                }
            }
            if (this.QMRate != 0) { //若为人民币
                fsChangeSet = YssFun.roundIt(YssD.div(fsChangeSet, this.QMRate), 2);
                ljChangeSet = YssFun.roundIt(YssD.div(ljChangeSet, this.QMRate), 2);
            }
            if (null != income) {
                income.fsValue = fsChangeSet;
                income.ljValue = ljChangeSet;
            }
        } catch (Exception e) {
            throw new YssException("获取价格变动本位币收入出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return income;
    }

    /**
     * 通过科目及借贷类型来计算价格变动收入
     * @param JDType 借贷类型
     * @param AcctCode String 科目
     * @param income incomeBean
     * @return incomeBean
     * @throws YssException
     */
    private incomeBean calcChangeIncomeWithAcctCode(String JDType,
        String AcctCode, incomeBean income) throws
        YssException {
        StringBuffer sqlBuf = null;
        ResultSet rs = null;
        double fsPriceDifference = 0D; //发生
        double ljPriceDifference = 0D; //累计
        HashMap rateMap = null;
        double baseRate = 0D;
        double portRate = 0D;
        try {
            sqlBuf = new StringBuffer();
            if (JDType.equalsIgnoreCase("D")) { //贷方
                sqlBuf.append("select sum(FCredit) as FCredit,sum(FAccCredit) as FAccCredit,FCurCode as FCuryCode from ");
            } else if (JDType.equalsIgnoreCase("J")) { //借方
                sqlBuf.append("select sum(FDebit) as FDebit,sum(FAccDebit) as FAccDebit,FCurCode as FCuryCode from ");
            }
            sqlBuf.append(sPrefixTableName + "LBAlANCE");
            sqlBuf.append(" where FAcctCode like ");
            sqlBuf.append(dbl.sqlString(AcctCode + "%"));
            sqlBuf.append(" and 	fisdetail=1");
            sqlBuf.append(" and FMonth in (");
            sqlBuf.append(getAllPeriodFromMonth()); //获取期间的月份信息
            sqlBuf.append(")");
            sqlBuf.append(" group by FAcctCode,FCurCode");
            rs = dbl.openResultSet(sqlBuf.toString());
            while (rs.next()) {
                rateMap = this.getRate(this.beginDate, rs.getString("FCuryCode")); //获取此种货币的期初汇率
                if (null != rateMap && rateMap.size() > 0) {
                    baseRate = ( (Double) rateMap.get("baseRate")).doubleValue(); //获取基础汇率
                    portRate = ( (Double) rateMap.get("portRate")).doubleValue(); //获取组合汇率
                    if (baseRate == 0 || portRate == 0) {
                        throw new YssException("获取" + rs.getString("FCuryCode") +
                                               "货币的汇率出现异常！");
                    }
                    if (JDType.equalsIgnoreCase("D")) { //贷方
                        fsPriceDifference +=
                            YssFun.roundIt(rs.getDouble("FCredit") * baseRate /
                                           portRate,
                                           2); //将不同币种的金额计算并汇总
                        ljPriceDifference +=
                            YssFun.roundIt(rs.getDouble("FAccCredit") * baseRate /
                                           portRate,
                                           2); //将不同币种的金额计算并汇总
                    } else if (JDType.equalsIgnoreCase("J")) { //借方
                        fsPriceDifference +=
                            YssFun.roundIt(rs.getDouble("FDebit") * baseRate /
                                           portRate,
                                           2); //将不同币种的金额计算并汇总
                        ljPriceDifference +=
                            YssFun.roundIt(rs.getDouble("FAccDebit") * baseRate /
                                           portRate,
                                           2); //将不同币种的金额计算并汇总
                    }
                }
            }
            if (this.QMRate != 0) { //若为人民币
                fsPriceDifference = YssFun.roundIt(YssD.div(fsPriceDifference,
                    this.QMRate), 2);
                ljPriceDifference = YssFun.roundIt(YssD.div(ljPriceDifference,
                    this.QMRate), 2);
            }
            if (null != income) {
                income.fsValue = fsPriceDifference;
                income.ljValue = ljPriceDifference;
            }
        } catch (Exception e) {
            throw new YssException("获取差价收入出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return income;
    }

    /**
     * 串接空字符串，有空字符串的地方直接调用此函数
     * @throws YssException
     * @return StringBuffer
     */
    private void buildEmpty(String itemShowMessage) throws YssException {
        StringBuffer bufItem = new StringBuffer(); //每条记录的buf
        bufItem.append(itemShowMessage).append(",");
        bufItem.append("").append(",");
        bufItem.append("");
        finBuf.append(fixPub.buildRowCompResult(bufItem.toString(),
                                                "DS_Income")).append( //为报表数据源代码
            "\r\n");
        bufItem = null;
    }

    /**
     * 拼装向前台传递数据的StringBuffer
     * @param itemShowMessage String 项目名称
     * @param bal double 金额
     * @throws YssException
     */
    private void spellShowItemBuffer(String itemShowMessage, double fsbal,
                                     double ljbal) throws
        YssException {
        StringBuffer bufItem = new StringBuffer(); //每条记录的buf
        bufItem.append(itemShowMessage).append(",");
        bufItem.append(fsbal).append(",");
        bufItem.append(ljbal);
        finBuf.append(fixPub.buildRowCompResult(bufItem.toString(),
                                                "DS_Income")).append( //为报表数据源代码
            "\r\n");
        bufItem = null;
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
     * 获取期初汇率
     * @throws YssException
     */
//   private void getQCRate() throws YssException {
//      this.QCRate = this.getimmRate(this.beginDate);//传入期初日期
//   }

    /**
     * 获取期末汇率
     * @throws YssException
     */
    private void getQMRate() throws YssException {
        this.QMRate = this.getimmRate(this.endDate); //传入期末日期
    }

    /**
     * 获取货币的基础汇率和组合汇率
     * @param dDate String
     * @param sCuryCode String
     * @return HashMap
     * @throws YssException
     */
    private HashMap getRate(String dDate, String sCuryCode) throws YssException {
        StringBuffer strBuf = null;
        ResultSet rs = null;
        double baseRate = 0D;
        double portRate = 0D;
        HashMap rateMap = null;
        try {
            strBuf = new StringBuffer();
            rateMap = new HashMap();
            strBuf.append("select FBaserate,FPortrate from ");
            strBuf.append(pub.yssGetTableName("tb_data_valrate"));
            strBuf.append(" where fvaldate = ( select max(fvaldate) from ");
            strBuf.append(pub.yssGetTableName("tb_data_valrate"));
            strBuf.append(" where fvaldate <=" + dbl.sqlDate(dDate));
            strBuf.append(" and fcurycode = ");
            strBuf.append(dbl.sqlString(sCuryCode));
            strBuf.append(")");
            strBuf.append(" and fcurycode = ");
            strBuf.append(dbl.sqlString(sCuryCode));
            rs = dbl.openResultSet(strBuf.toString());
            while (rs.next()) {
                baseRate = rs.getDouble("FBaserate");
                portRate = rs.getDouble("FPortRate");
            }
            rateMap.put("baseRate", new Double(baseRate));
            rateMap.put("portRate", new Double(portRate));
        } catch (Exception e) {
            throw new YssException("获取基础及组合汇率的出现异常!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return rateMap;
    }

    /**
     * 获取即期汇率
     * @param String dDate 传入日期参数
     * @return double 获取的汇率
     * @throws YssException
     */
    private double getimmRate(String dDate) throws YssException {
        StringBuffer strBuf = null;
        ResultSet rs = null;
        double immRate = 0D;
        try {
            strBuf = new StringBuffer();
            strBuf.append("select fexrate1 as FimmRate from ");
            strBuf.append(pub.yssGetTableName("tb_data_exchangerate"));
            strBuf.append(" where fexratedate = ( select max(fexratedate) from ");
            strBuf.append(pub.yssGetTableName("tb_data_exchangerate"));
            strBuf.append(" where fexratedate<=" + dbl.sqlDate(dDate));
            strBuf.append(" and fcurycode='CNY' )");
            strBuf.append(" and fcurycode='CNY'");
            rs = dbl.openResultSet(strBuf.toString());
            while (rs.next()) {
                immRate = rs.getDouble("FimmRate");
            }
        } catch (Exception e) {
            throw new YssException("获取即期汇率的数出错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return immRate;
    }

    //************
     //公共方法结束
     //************
      //****************************************************************************************************//

      /**
       *
       * <p>Title: 收益报表的私有类</p>
       *
       * <p>Description: 用于封装数据，以便处理</p>
       *
       * <p>Copyright: Copyright (c) 2006</p>
       *
       * <p>Company: </p>
       *
       * @author sj
       * @version 1.0
       */
      private class incomeBean {
          public String itemCode; //项目号
          public double fsValue; //发生额
          public double ljValue; //累计额
      }
}
