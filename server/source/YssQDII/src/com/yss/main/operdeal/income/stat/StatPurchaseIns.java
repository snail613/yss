package com.yss.main.operdeal.income.stat;

import java.util.*;
import java.util.Date;

import com.yss.util.*;
import com.yss.main.operdeal.stgstat.*;
import java.sql.*;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.manager.SecRecPayAdmin;
import com.yss.commeach.EachRateOper;
import com.yss.main.operdeal.platform.pfoper.pubpara.innerparams.InnerPubParamsWithPurchase;

public class StatPurchaseIns
    extends BaseIncomeStatDeal {
    java.util.Date currentDate = null;
// -- MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A sj ---------------------------
    private EachRateOper rateOper = null;
    private InnerPubParamsWithPurchase purchaseParams = null; //获取回购业务的通用参数设置
//------------------------------------------------------------------------------

// -- MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A sj ---------------------------
    public void setPortCodes(String sPortCode){
        this.portCodes = sPortCode;
    }
// -----------------------------------------------------------------------------	
	
    public StatPurchaseIns() {
    }

    public ArrayList getDayIncomes(java.util.Date dDate) throws YssException {
    	ArrayList dayIncomes = null;
    	boolean innerOrout = false;
    	
    	innerOrout = judgeInnerOurOut(); //判断是否为国内业务 
    	
    	if(innerOrout){
    		dayIncomes = getDayIncomesWithInner(dDate);//国内业务的处理方式
    	}else{
    		dayIncomes = getDayIncomesWithComm(dDate);//用获取的参数属性来设置是否使用国内的计息方式
    	}
    	
    	return dayIncomes;
    }
	
	 //---------------------------------------------------------------------------------------------------------------------------------------
    /**
     * 计算回购利息（国内）
     * @param dDate Date
     * @return ArrayList
     * @throws YssException
     */
    public ArrayList getDayIncomesWithInner(java.util.Date dDate) throws YssException {
        ArrayList array = new ArrayList();
        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;
        rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        getPurchaseParams(); //获取通用参数
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");

            getDayIncomesWithSubTrade(dDate, analy1, analy2, analy3, array); //计算业务资料下的回购利息
            getDayIncomeWithPurchase(dDate, analy1, analy2, analy3, array);
        } catch (Exception e) {
            throw new YssException("计算回购利息出现异常！", e);
        }
        return array;
    }
	
	    /**
     * 计算业务资料下的回购利息
     * @param dDate Date
     * @param array ArrayList
     * @throws YssException
     */
    private void getDayIncomesWithSubTrade(java.util.Date dDate, boolean analy1, boolean analy2, boolean analy3, ArrayList array) throws YssException {
        String sqlStr = null;
        sqlStr = buildSubTradeIncomesSql(dDate); //获取回购信息
        //edit by songjie 2011.06.03 BUG 2002 QDV4博时2011年05月30日01_B 添加是否包含投资类型的参数
        calcPurchaseIncomes(dDate, array, sqlStr, analy1, analy2, analy3,true); //计算回购利息
    }
	
	 /**
     * 拼装获取业务资料下的回购数据的sql语句
     * @param dDate Date
     * @return String
     */
    private String buildSubTradeIncomesSql(java.util.Date dDate) {
        StringBuffer buf = new StringBuffer();
        buf.append("select a.FSecurityCode as FSecurityCode,a.FPortCode as FPortCode,");
        buf.append("a.FTradeTypeCode as FTradeTypeCode,"); //交易类型
        buf.append("FBargainDate,");
        buf.append("case when d.FInBeginType = ");
        buf.append(dbl.sqlString("trade")).append(" then FBargainDate");
        buf.append(" else FSettleDate end");
        buf.append(" as FBeginDate,"); //通过回购设置的计息日来判断开始日期
        //edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加证券名称
        buf.append("a.FSettleDate as FSettleDate,c.FTradeCury as FCuryCode, c.FSecurityName, ");
        
        //alter by liuwei 增加所属分类 MS01125 
        buf.append("case When FAttrClsCode is null or FAttrClsCode='' then ' ' else FAttrClsCode end as FAttrClsCode ,"); 
       //-------------------------// 
        buf.append("case when d.FInBeginType = ");
        buf.append(dbl.sqlString("trade")).append(" then FMATUREDATE");//调整结束日期的判断，若起息日期为交易日，则结束日期为到期日期
        buf.append(" else FMATUREDATE ");//调整结束日期的判断，若起息日期为结算日期，则结束日期为到期结算日期
        buf.append(" end as FEndDate,");
        buf.append("FMATUREDATE ,");
        buf.append("FInvMgrCode as FAnalysisCode1,FBrokerCode as FAnalysisCode2,");
        buf.append("FAccruedinterest as FPurchaseGain,"); //回购利息
        buf.append("FTradeFee1, FTradeFee2, FTradeFee3, FTradeFee4,");
        //edit by songjie 2011.05.20 BUG 1937 QDV4赢时胜(测试)2011年5月16日02_B 获取投资类型数据
        buf.append("FTradeFee5, FTradeFee6, FTradeFee7, FTradeFee8, FInvestType,");

        buf.append(dbl.sqlString(YssOperCons.YSS_ZQPZZLX_REEXCHANGE));
        buf.append(" as FType "); // 场内

        buf.append(" FROM (");

        buf.append("select * from ");
        buf.append(pub.yssGetTableName("TB_Data_SubTrade")); //获取业务资料信息
        buf.append(" where ");
        buf.append("FCheckState =1 and ");
        //----- add by wangzuochun 2010.03.22 MS00928  回购计提利息时，系统没有判断勾选的回购信息进行计提    QDV4赢时胜（测试）2010年03月22日01_B 
        buf.append("FSecurityCode in (");
        buf.append(operSql.sqlCodes(this.selCodes));
        buf.append(") and ");
        //-----------------------------------------//
        buf.append(dbl.sqlDate(dDate));
        buf.append(" between ");
        buf.append(" FBargainDate and FMATUREDATE and FPortCode in (");
        buf.append(operSql.sqlCodes(this.portCodes));
        buf.append(") and FTradeTypeCode in (");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_ZRE));
        buf.append(",");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_NRE));
        //add by zhouwei 20120508 bug 4284
        buf.append(",");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_REMR));
        buf.append(",");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_REMC));
        //-----------end--------
        buf.append(")) a left join ");
        buf.append("(select * from ");
        buf.append(pub.yssGetTableName("tb_para_purchase")); //获取回购业务数据
        buf.append(" where FCheckState =1 ) b on a.FsecurityCode = b.Fsecuritycode ");
        buf.append(" left join  ");
        //edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加 证券名称
        buf.append("(select FSecurityCode,FTradeCury,FSecurityName from ");
        buf.append(pub.yssGetTableName("tb_para_security")); //获取证券信息设置
        buf.append(" where FCheckState = 1) c on a.FSecurityCode = c.Fsecuritycode");
        buf.append(" left join ");
        buf.append("(select FSecurityCode,FInBeginType,FDepdurCode from ");
        buf.append(pub.yssGetTableName("Tb_para_purchase")); //获取回购信息设置中的数据
        buf.append(" where FCheckState = 1");
        buf.append(") d on a.FSecurityCode = d.FSecurityCode");
        buf.append(" left join (select FDepDurCode,FDuration from ");
        buf.append(pub.yssGetTableName("TB_PARA_DepositDuration")).append(" where FCheckState = 1) e on d.FDepdurCode = e.FDepDurCode"); //获取期间设置中的数据
        return buf.toString();
    }


 /**
     * 计算回购业务下的回购利息
     * @param dDate Date
     * @param array ArrayList
     * @throws YssException
     */
    private void getDayIncomeWithPurchase(java.util.Date dDate, boolean analy1, boolean analy2, boolean analy3, ArrayList array) throws YssException {
        String sqlStr = null;
        sqlStr = buildPurchaseIncomesSql(dDate); //获取回购信息
        //edit by songjie 2011.06.03 BUG 2002 QDV4博时2011年05月30日01_B 添加是否包含投资类型的参数
        calcPurchaseIncomes(dDate, array, sqlStr, analy1, analy2, analy3,false); //计算回购利息
    }

    /**
     * 拼装获取回购业务中回购数据的sql语句
     * @param dDate Date
     * @return String
     */
    private String buildPurchaseIncomesSql(java.util.Date dDate) {
        StringBuffer buf = new StringBuffer();
        buf.append("select a.FSecurityCode as FSecurityCode,a.FPortCode as FPortCode,");
        buf.append("a.FTradeTypeCode as FTradeTypeCode,"); //交易类型
        buf.append("FBargainDate,");
        buf.append("FSettleDate,");//story 1570 add by zhouwei 20111121 对于场外回购（银行间）,有T+0与T+1之分;所以增加结算日期字段
        buf.append("case when d.FInBeginType = ");
        buf.append(dbl.sqlString("trade")).append(" then FBargainDate");
        buf.append(" else FSettleDate end");
        buf.append(" as FBeginDate,"); //通过回购设置的计息日来判断开始日期
        //edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加证券名称
        buf.append("c.FTradeCury as FCuryCode, c.FSecurityName,");

        buf.append("case when d.FInBeginType = ");
        buf.append(dbl.sqlString("trade")).append(" then FMATUREDATE"); //调整结束日期的判断，若起息日期为交易日，则结束日期为到期日期
        //modify by zhouwei 20120509 结束日期为到期日期
        buf.append(" else FMATUREDATE "); //调整结束日期的判断，若起息日期为结算日期，则结束日期为到期结算日期

        buf.append(" end as FEndDate,"); //通过回购设置的计息日来加上回购期间天数来计算结束日期

        buf.append("FMATUREDATE,");
        buf.append("FInvMgrCode as FAnalysisCode1,");
        buf.append("FPurchaseGain,"); //回购利息
        buf.append("FTradeHandleFee, FBankHandleFee, FSetServiceFee,");

        buf.append(dbl.sqlString(YssOperCons.YSS_ZQPZZLX_REBANK));
        buf.append(" as FType ,"); // 场外
        //增加所属分类 liuwei  回购利息中没有加入所属分类 QDV4赢时胜上海2010年04月27日01_AB MS01125 by liuwei 
        buf.append("case when a.FTradeTypeCode='24'  then   'SellRepo' ");
        buf.append(" when a.FTradeTypeCode='25'  then   'AntiRepo' ");
        buf.append("else ' ' end as FATTRCLSCODE");
		//QDV4赢时胜上海2010年04月27日01_AB MS01125 by liuwei 
        buf.append(" FROM (");

        buf.append("select * from ");
        buf.append(pub.yssGetTableName("TB_DATA_PURCHASE")); //获取业务资料信息
        buf.append(" where ");
        buf.append("FCheckState =1 and ");

        //       buf.append("FSecurityCode in(");
        //       buf.append(operSql.sqlCodes(selCodes));
        //       buf.append(") and ");

        buf.append(dbl.sqlDate(dDate));
        buf.append(" between ");
        buf.append(" FBARGAINDATE and FMATUREDATE and FPortCode in (");//调整为业务日期
        buf.append(operSql.sqlCodes(this.portCodes));
        buf.append(") and FTradeTypeCode in (");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_ZRE));
        buf.append(",");
        buf.append(dbl.sqlString(YssOperCons.YSS_JYLX_NRE));
        buf.append(")) a left join ");
        buf.append("(select * from ");
        buf.append(pub.yssGetTableName("tb_para_purchase")); //获取回购业务数据
        buf.append(" where FCheckState =1 ) b on a.FsecurityCode = b.Fsecuritycode ");
        buf.append(" left join  ");
        //edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加证券名称
        buf.append("(select FSecurityCode,FTradeCury, FSecurityName from ");
        buf.append(pub.yssGetTableName("tb_para_security")); //获取证券信息设置
        buf.append(" where FCheckState = 1) c on a.FSecurityCode = c.Fsecuritycode");
        buf.append(" left join ");
        buf.append("(select FSecurityCode,FInBeginType,FDepdurCode from ");
        buf.append(pub.yssGetTableName("Tb_para_purchase")); //获取回购信息设置中的数据
        buf.append(" where FCheckState = 1");
        buf.append(") d on a.FSecurityCode = d.FSecurityCode");
        buf.append(" left join (select FDepDurCode,FDuration from ");
        buf.append(pub.yssGetTableName("TB_PARA_DepositDuration")).append(" where FCheckState = 1) e on d.FDepdurCode = e.FDepDurCode"); //获取期间设置中的数据
        return buf.toString();

    }

    /**
     * 计算回购利息
     * @param arr ArrayList 存放回购利息数据的容器
     * @param sql String 获取回购信息的sql语句
     * @throws YssException
     */
    //edit by songjie 2011.06.03 BUG 2002 QDV4博时2011年05月30日01_B 添加是否包含投资类型的参数
    private void calcPurchaseIncomes(java.util.Date dDate, ArrayList arr, String sql, boolean analy1, boolean analy2, boolean analy3, boolean haveInvestType) throws YssException {
        ResultSet rs = null;
        SecPecPayBean secpecPay = null;
        try {
            rs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	//edit by songjie 2011.06.03 BUG 2002 QDV4博时2011年05月30日01_B 添加是否包含投资类型的参数
                secpecPay = setSecPecPayBean(dDate, rs, analy1, analy2, analy3,haveInvestType); //设置回购利息数据
                arr.add(secpecPay);
                

            }
        } catch (Exception e) {
            throw new YssException("计算回购利息出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 设置回购利息数据
     * @param rs ResultSet 包含回购利息数据的数据集
     * @return SecPecPayBean 回购利息数据
     * @throws YssException
     */
    //edit by songjie 2011.06.03 BUG 2002 QDV4博时2011年05月30日01_B 添加是否包含投资类型的参数
    private SecPecPayBean setSecPecPayBean(java.util.Date dDate, ResultSet rs, boolean analy1, boolean analy2, boolean analy3,boolean haveInvestType) throws YssException {
        SecPecPayBean secpecPay = null;
        int days = 0; //计息天数
        double purchaseIncome = 0D; //回购收益
        double purchaseInterest = 0D; //每日回购利息
        double dBaseRate = 0D;
        double dPortRate = 0D;
        try {
            if (rs.getString("FCuryCode") == null || rs.getString("FCuryCode").trim().length() == 0) {
                throw new YssException("系统进行回购计息,在获取回购计息时检查到代码为【" +
                                       rs.getString("FSecurityCode") +
                                       "】证券对应的币种信息不存在!" + "\n" +
                                       "请核查以下信息：" + "\n" +
                                       "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                       "2.【证券品种信息】中该证券交易币种项设置是否正确!");
            }
            //story 1570 add by zhouwei 20111121 计息天数为到期日到交易日
            
            //modify by zhangjun  BUG4852bug4284修改了计息天数的计算过程，导致计息时结果翻倍             
            //days = YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FMatureDate")); //用到期日期-交易日期                   
            days = YssFun.dateDiff(rs.getDate("FBeginDate"), rs.getDate("FEndDate")); //modify by zhouwei 20120509
            //modify by zhangjun  BUG4852bug4284修改了计息天数的计算过程，导致计息时结果翻倍 
             
            secpecPay = new SecPecPayBean();
            //add by songjie 2011.05.20 BUG 1937 QDV4赢时胜(测试)2011年5月16日02_B 设置投资类型数据
            //---edit by songjie 2011.06.03 BUG 2002 QDV4博时2011年05月30日01_B---//
            if(haveInvestType){//若sql结果集中包含FInvestType字段 则获取投资类型数据
            	secpecPay.setInvestType(rs.getString("FInvestType"));
            }
            //---edit by songjie 2011.06.03 BUG 2002 QDV4博时2011年05月30日01_B---//
            secpecPay.setTransDate(dDate);
            secpecPay.setStrPortCode(rs.getString("FPortCode"));
            secpecPay.setStrSecurityCode(rs.getString("FSecurityCode"));
            if (analy1) {
                secpecPay.setInvMgrCode(rs.getString("FAnalysisCode1"));
            } else {
                secpecPay.setInvMgrCode(" ");
            }
            if (analy2) {
                if (purchaseParams.judgeExistField(rs, "FAnalysisCode2")) { //当记录集中存在分析代码2的字段时，获取并设置
                    secpecPay.setBrokerCode(rs.getString("FAnalysisCode2"));
                } else { //不存在，设置为空字符
                    secpecPay.setBrokerCode(" ");
                }
            } else {
                secpecPay.setBrokerCode(" ");
            }
            if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_NRE)) {//调整回购类型，逆回购为应收类型的数据
                secpecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec); //业务类型 应收
                secpecPay.setStrSubTsfTypeCode(YssOperCons.
                                               YSS_ZJDBZLX_RE_RecInterest); //应收

            } else {
                secpecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); //业务类型 应付
                secpecPay.setStrSubTsfTypeCode(YssOperCons.
                                               YSS_ZJDBZLX_RE_PayInterest);
            }

            purchaseIncome = calcPurchaseIncome(rs); // 计算回购收益
            purchaseInterest = calcePurchaseInterest(rs, dDate, purchaseIncome, days); //计算回购利息

            secpecPay.setMoney(purchaseInterest);
            
            //增加所属分类 liuwei QDV4赢时胜上海2010年04月27日01_AB MS01125 by liuwei 
           secpecPay.setAttrClsCode(rs.getString("FATTRCLSCODE"));

            dBaseRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FCuryCode"),
                rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
            dPortRate = rateOper.getDPortRate();

            if (dBaseRate == 0 || dPortRate == 0) {
                throw new YssException("系统进行回购计息,检查到代码为【" +
                                       rs.getString("FSecurityCode") +
                                       "】证券对应的汇率信息不存在!" + "\n" +
                                       "请核查以下信息：" + "\n" +
                                       "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                       "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");
            }
            secpecPay.setBaseCuryRate(dBaseRate);
            secpecPay.setPortCuryRate(dPortRate);

            secpecPay.setMMoney(secpecPay.getMoney());
            secpecPay.setVMoney(secpecPay.getMoney());

            secpecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(secpecPay.getMoney(), dBaseRate));
            secpecPay.setMBaseCuryMoney(secpecPay.getBaseCuryMoney());
            secpecPay.setVBaseCuryMoney(secpecPay.getBaseCuryMoney());

            secpecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(secpecPay.getMoney(), dBaseRate, dPortRate,
                rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")));
            secpecPay.setMPortCuryMoney(secpecPay.getPortCuryMoney());
            secpecPay.setVPortCuryMoney(secpecPay.getPortCuryMoney());

            secpecPay.setStrCuryCode(rs.getString("FCuryCode"));
        } catch (Exception e) {
            throw new YssException("设置回购利息数据出现异常！", e);
        }
        return secpecPay;
    }
    
  /**
   * 此方法为普通QD的计息方式
   * @param dDate
   * @return
   * @throws YssException
   */
    public ArrayList getDayIncomesWithComm(java.util.Date dDate) throws YssException {
        ArrayList alIncomes = new ArrayList();
//      BaseStgStatDeal secstgstat = null;
        boolean analy1;
        boolean analy2;
        boolean analy3;
        SecPecPayBean secpecPay = null;
        ResultSet rs = null;
        int days = 0;
        String strSql = "";
        double dBaseRate = 0;
        double dPortRate = 0;
        double dPhIncome = 0;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------

        try {
            currentDate = dDate;
//         secstgstat = (BaseStgStatDeal) pub.
//               getOperDealCtx().getBean("SecRecPay");//证券应收应付款库存
//         secstgstat.setYssPub(pub);
//         secstgstat.stroageStat(dDate, dDate, portCodes);

            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");

            strSql = "select a.FSecurityCode as FSecurityCode,a.FPortCode as FPortCode,FBargainDate, " +
                //--------------------
                "a.FSettleDate as FSettleDate,c.FInBeginType as FInBeginType," +
                "a.FTradeTypeCode,"+//用业务类型判断回购品种 MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                //--------------------
                //edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加证券名称
                " b.FTradeCury as FCuryCode, b.FSecurityName, FMATUREDATE, FInvMgrCode as FAnalysisCode1, FBrokerCode as FAnalysisCode2," +
                " FAccruedinterest as FPhIncome,e.FDayInd as FDayInd,c.FPurchaseType as FPurchaseType,f.FPortCury, " +
                " f1.FAW1,f2.FAW2,f3.FAW3,f4.FAW4,f5.FAW5,f6.FAW6,f7.FAW7,f8.FAW8," +
                " FTradeFee1,FTradeFee2,FTradeFee3,FTradeFee4,FTradeFee5,FTradeFee6,FTradeFee7,FTradeFee8 from (" +
                "select * from "
                + pub.yssGetTableName("Tb_Data_SubTrade") +
                " where " + dbl.sqlDate(dDate) + " between FBargainDate and FMATUREDATE  and FPortCode in ("
                + operSql.sqlCodes(this.portCodes) + ")" + " and FTradeTypeCode in ('24','25','78','79') and FCheckState = 1 " + //add by zhouwei 20120508 bug 4284 买断式回购
                " and FSecurityCode in (" + operSql.sqlCodes(this.selCodes) + ")" +// modify by wangzuochun 2010.03.22 MS00928  回购计提利息时，系统没有判断勾选的回购信息进行计提   QDV4赢时胜（测试）2010年03月22日01_B
                //----------------------------------------------
                //edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加证券名称
                ") a left join (select FSecurityCode, FTradeCury,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCheckState = 1) b on a.FSecurityCode = b.FSecurityCode" +
                //--------------------------------------------------------------------------------------------//回购品种信息
                " left join (select FSecurityCode,FDepDurCode,FPurchaseType,FPurchaseRate,FPeriodCode" +
                ",FInBeginType" +
                " from " +
                pub.yssGetTableName("Tb_Para_Purchase") +
                " where FCheckState = 1) c on a.FSecurityCode = c.FSecurityCode" +
                //--------------------------------------------------------------------------------------------
                " left join (select FPeriodCode,FDayInd from " + pub.yssGetTableName("Tb_Para_Period") +
                ") e on c.FPeriodCode = e.FPeriodCode " +
                //--------------------------------------------------------------------------------------------
                " left join (select FPortCode,FPortCury from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                ") f on a.FPortCode = f.FPortCode " +
                //--------------------------------------------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW1 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f1 on a.FFeeCode1 = f1.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW2 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f2 on a.FFeeCode2 = f2.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW3 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f3 on a.FFeeCode3 = f3.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW4 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f4 on a.FFeeCode4 = f4.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW5 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f5 on a.FFeeCode5 = f5.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW6 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f6 on a.FFeeCode6 = f6.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW7 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f7 on a.FFeeCode7 = f7.FFeeCode" +
                //--------------------------------------------------------
                " left join (select FFeeCode,FAccountingWay as FAW8 from " +
                pub.yssGetTableName("Tb_Para_Fee") +
                ") f8 on a.FFeeCode8 = f8.FFeeCode";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //-------------------by 曹丞 2009.01.24 币种有效性检查 MS00004 QDV4.1-2009.2.1_09A---//
                if (rs.getString("FCuryCode") == null || rs.getString("FCuryCode").trim().length() == 0) {
                    throw new YssException("系统进行证券库存统计,在获取库存回购并计息时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的币种信息不存在!" + "\n" +
                                           "请核查以下信息：" + "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                           "2.【证券品种信息】中该证券交易币种项设置是否正确!");
                }
                //-------------------------------------------------------------------------------------//
                //-------------------------MS00088  sj 20081208 ---------------------------
                if (rs.getString("FInBeginType").equalsIgnoreCase("settle")) { //当流通市场为银行时
                    days = YssFun.dateDiff(rs.getDate("FSettleDate"),
                                           rs.getDate("FMATUREDATE"));
                } else if (rs.getString("FInBeginType").equalsIgnoreCase("trade")) { //为交易所时
                    days = YssFun.dateDiff(rs.getDate("FBargainDate"),
                                           rs.getDate("FMATUREDATE")); //要除以的天数
                }
                //------------------------------------------------------------------------
                secpecPay = new SecPecPayBean();
                secpecPay.setTransDate(dDate);
                secpecPay.setStrPortCode(rs.getString("FPortCode"));
                secpecPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                if (analy1) {
                    secpecPay.setInvMgrCode(rs.getString("FAnalysisCode1"));
                } else {
                    secpecPay.setInvMgrCode(" ");
                }
                if (analy2) {
                    secpecPay.setBrokerCode(rs.getString("FAnalysisCode2"));
                } else {
                    secpecPay.setBrokerCode(" ");
                }
                //-------------------------------------------------------------------------------
                //用业务类型判断回购品种 MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
//                if (rs.getString("FPurchaseType").length() > 0) {
//                    if (rs.getString("FPurchaseType").equalsIgnoreCase("UnPh")) {
//                        secpecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec); //业务类型 应收
//                        secpecPay.setStrSubTsfTypeCode(YssOperCons.
//                            YSS_ZJDBZLX_RE_RecInterest); //sub业务类型 应收
//                    } else if (rs.getString("FPurchaseType").equalsIgnoreCase("RePh")) {
//                        secpecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); //业务类型 应付
//                        secpecPay.setStrSubTsfTypeCode(YssOperCons.
//                            YSS_ZJDBZLX_RE_PayInterest);
//                    }
//                }
                if(rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZRE) 
                		|| rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC)){//24正回购   add by zhouwei 20120508 bug 4284 买断式回购
                	secpecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay); //业务类型 应付
                	secpecPay.setStrSubTsfTypeCode(YssOperCons.
                			YSS_ZJDBZLX_RE_PayInterest);
                }else if(rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_NRE)
                		|| rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMR)){//25逆回购
                	secpecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec); //业务类型 应收
                	secpecPay.setStrSubTsfTypeCode(YssOperCons.
                			YSS_ZJDBZLX_RE_RecInterest); //sub业务类型 应收
                }
                //MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                dPhIncome = rs.getDouble("FPhIncome");

                if (rs.getInt("FAw1") == 0) { //计入成本,正回购加费用,逆回购减费用
                    //dPhIncome = YssD.sub(dPhIncome, (rs.getString("FPurchaseType").equalsIgnoreCase("RePh") ? -1 : 1) * rs.getDouble("FTradeFee1"));
                    dPhIncome = YssD.sub(dPhIncome, ((rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZRE) || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC)) ? -1 : 1) * rs.getDouble("FTradeFee1"));//用业务类型判断回购品种 MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                }
                if (rs.getInt("FAw2") == 0) { //计入成本
                    //dPhIncome = YssD.sub(dPhIncome, (rs.getString("FPurchaseType").equalsIgnoreCase("RePh") ? -1 : 1) * rs.getDouble("FTradeFee2"));
                    dPhIncome = YssD.sub(dPhIncome, ((rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZRE) || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC)) ? -1 : 1) * rs.getDouble("FTradeFee2"));//用业务类型判断回购品种 MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                }
                if (rs.getInt("FAw3") == 0) { //计入成本
                    //dPhIncome = YssD.sub(dPhIncome, (rs.getString("FPurchaseType").equalsIgnoreCase("RePh") ? -1 : 1) * rs.getDouble("FTradeFee3"));
                    dPhIncome = YssD.sub(dPhIncome, ((rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZRE) || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC)) ? -1 : 1) * rs.getDouble("FTradeFee3"));//用业务类型判断回购品种 MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                }
                if (rs.getInt("FAw4") == 0) { //计入成本
                    //dPhIncome = YssD.sub(dPhIncome, (rs.getString("FPurchaseType").equalsIgnoreCase("RePh") ? -1 : 1) * rs.getDouble("FTradeFee4"));
                    dPhIncome = YssD.sub(dPhIncome, ((rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZRE) || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC)) ? -1 : 1) * rs.getDouble("FTradeFee4"));//用业务类型判断回购品种 MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                }
                if (rs.getInt("FAw5") == 0) { //计入成本
                    //dPhIncome = YssD.sub(dPhIncome, (rs.getString("FPurchaseType").equalsIgnoreCase("RePh") ? -1 : 1) * rs.getDouble("FTradeFee5"));
                    dPhIncome = YssD.sub(dPhIncome, ((rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZRE) || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC)) ? -1 : 1) * rs.getDouble("FTradeFee5"));//用业务类型判断回购品种 MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                }
                if (rs.getInt("FAw6") == 0) { //计入成本
                    //dPhIncome = YssD.sub(dPhIncome, (rs.getString("FPurchaseType").equalsIgnoreCase("RePh") ? -1 : 1) * rs.getDouble("FTradeFee6"));
                    dPhIncome = YssD.sub(dPhIncome, ((rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZRE) || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC)) ? -1 : 1) * rs.getDouble("FTradeFee6"));//用业务类型判断回购品种 MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                }
                if (rs.getInt("FAw7") == 0) { //计入成本
                    //dPhIncome = YssD.sub(dPhIncome, (rs.getString("FPurchaseType").equalsIgnoreCase("RePh") ? -1 : 1) * rs.getDouble("FTradeFee7"));
                    dPhIncome = YssD.sub(dPhIncome, ((rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZRE) || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC)) ? -1 : 1) * rs.getDouble("FTradeFee7"));//用业务类型判断回购品种 MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                }
                if (rs.getInt("FAw8") == 0) { //计入成本
                    //dPhIncome = YssD.sub(dPhIncome, (rs.getString("FPurchaseType").equalsIgnoreCase("RePh") ? -1 : 1) * rs.getDouble("FTradeFee8"));
                    dPhIncome = YssD.sub(dPhIncome, ((rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_ZRE) || rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_REMC)) ? -1 : 1) * rs.getDouble("FTradeFee8"));//用业务类型判断回购品种 MS00890 QDII4.1赢时胜上海2010年02月25日02_B  by leeyu 20100323
                }

                //--------------------------------------------------------------------------------
                if (rs.getInt("FDayInd") <= 2) { //设置金额
                    if (rs.getInt("FDayInd") == 0) { //计头不计尾
                        if (YssFun.dateDiff( //下面的二元表达式的目的是为了按流通市场分不同的计息起始日.若是银行间则以结算日为交易起始日 MS00088  sj 20081208
                            rs.getString("FInBeginType").equalsIgnoreCase("settle") ? rs.getDate("FSettleDate") : rs.getDate("FBargainDate"), dDate) >= 0 && YssFun.dateDiff(dDate, rs.getDate("FMATUREDATE")) > 0) {
                            if (YssFun.dateDiff(dDate, rs.getDate("FMATUREDATE")) == 1) { //到期日-计息日＝1说明是最后一天计息
                                secpecPay.setMoney(YssD.sub(dPhIncome, YssD.mul(YssD.div(dPhIncome, days, 2), days - 1))); //最后一天采用轧差
                            } else {
                                secpecPay.setMoney(YssD.div(dPhIncome, days));
                            }
                        }
                    } else if (rs.getInt("FDayInd") == 1) { //若是计尾不计头
                        if (YssFun.dateDiff( // MS00060 sj 20081208
                            rs.getString("FInBeginType").equalsIgnoreCase("settle") ? rs.getDate("FSettleDate") : rs.getDate("FBargainDate"), dDate) > 0 && YssFun.dateDiff(dDate, rs.getDate("FMATUREDATE")) >= 0) {
                            if (YssFun.dateDiff(dDate, rs.getDate("FMATUREDATE")) == 0) { //到期日-计息日＝0说明是最后一天计息
                                secpecPay.setMoney(YssD.sub(dPhIncome, YssD.mul(YssD.div(dPhIncome, days, 2), days - 1))); //最后一天采用轧差
                            } else {
                                secpecPay.setMoney(YssD.div(dPhIncome,
                                    days));
                            }
                        }
                    } else if (rs.getInt("FDayInd") == 2) { //头尾均计
                        if (YssFun.dateDiff( // MS00060 sj 20081208
                            rs.getString("FInBeginType").equalsIgnoreCase("settle") ? rs.getDate("FSettleDate") : rs.getDate("FBargainDate"), dDate) >= 0 && YssFun.dateDiff(dDate, rs.getDate("FMATUREDATE")) >= 0) {
                            if (YssFun.dateDiff(dDate, rs.getDate("FMATUREDATE")) == 0) { //到期日-计息日＝0说明是最后一天计息
                                //2008.06.27 蒋锦 修改 头尾均记要多除一天
                                secpecPay.setMoney(YssD.sub(dPhIncome, YssD.mul(YssD.div(dPhIncome, days + 1, 2), days))); //最后一天采用轧差
                            } else {
                                secpecPay.setMoney(YssD.div(dPhIncome,
                                    days + 1)); //如果是头尾均计，那么计息日会多一天，所以这里加1
                            }
                        }
                    }
                }
                dBaseRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FCuryCode"),
                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
                //组合汇率
//                dPortRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FPortCury"),
//                    rs.getString("FPortCode"), YssOperCons.YSS_RATE_PORT);
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
                dPortRate = rateOper.getDPortRate();
                //-----------------------------------------------------------------------------------

                //----------------by caocheng 2009.02.04 增加汇率有效性检查 MS00004 QDV4.1-2009.2.1_09A ---------//
                if (dBaseRate == 0 || dPortRate == 0) {
                    throw new YssException("系统进行证券库存统计,在获取库存回购并计息时检查到代码为【" +
                                           rs.getString("FSecurityCode") +
                                           "】证券对应的汇率信息不存在!" + "\n" +
                                           "请核查以下信息：" + "\n" +
                                           "1.【证券品种信息】中该证券信息是否存在且已审核!" + "\n" +
                                           "2.【汇率资料】中该证券交易币种对应的汇率数据是否存在!");
                }
                //---------------------------------------------------------------------------------------------//
                secpecPay.setBaseCuryRate(dBaseRate);
                secpecPay.setPortCuryRate(dPortRate);

                secpecPay.setMMoney(secpecPay.getMoney());
                secpecPay.setVMoney(secpecPay.getMoney());

                secpecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(secpecPay.getMoney(), dBaseRate));
                secpecPay.setMBaseCuryMoney(secpecPay.getBaseCuryMoney());
                secpecPay.setVBaseCuryMoney(secpecPay.getBaseCuryMoney());

                secpecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(secpecPay.getMoney(), dBaseRate, dPortRate,
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    rs.getString("FCuryCode"), dDate, rs.getString("FPortCode")));
                secpecPay.setMPortCuryMoney(secpecPay.getPortCuryMoney());
                secpecPay.setVPortCuryMoney(secpecPay.getPortCuryMoney());

                secpecPay.setStrCuryCode(rs.getString("FCuryCode"));
                alIncomes.add(secpecPay);
            }
            return alIncomes;
        } catch (Exception e) {
            throw new YssException("系统进行回购计息时出现异常!" + "\n", e); //by 曹丞 2009.01.24 回购计息异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void saveIncomes(ArrayList alIncome) throws YssException {
        int i = 0;
        SecPecPayBean secpecpay = null;
        SecRecPayAdmin recpayRec = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        //add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        Date logBeginDate = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            recpayRec = new SecRecPayAdmin(); //应收
            recpayRec.setYssPub(pub);
            for (i = 0; i < alIncome.size(); i++) {
                secpecpay = (SecPecPayBean) alIncome.get(i);
                secpecpay.checkStateId = 1;
                recpayRec.addList(secpecpay);
                
                //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                logBeginDate = new Date();
                logInfo = "证券代码:" + secpecpay.getStrSecurityCode() + 
                           "\r\n利息:" + secpecpay.getMoney();
                
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, false, 
                		secpecpay.getStrPortCode(), secpecpay.getTransDate(),
                		secpecpay.getTransDate(),secpecpay.getTransDate(),
                		logInfo," ",logBeginDate,logSumCode,new Date());
        		}
                //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            }
            recpayRec.insert("", beginDate, endDate,
                             YssOperCons.YSS_ZJDBLX_Rec + "," + YssOperCons.YSS_ZJDBLX_Pay,
                             YssOperCons.YSS_ZJDBZLX_RE_RecInterest + "," + YssOperCons.YSS_ZJDBZLX_RE_PayInterest,
                             portCodes, "", "", "", "", -99, true); //MS00275 QDV4中保2009年02月27日01_B  将标示改为-99，是为了江所有标示的数据都进行删除。
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
        	//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
        	try{
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, true, 
                		secpecpay.getStrPortCode(), secpecpay.getTransDate(),
                		secpecpay.getTransDate(),secpecpay.getTransDate(),
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                		(logInfo + "\r\n计提回购利息出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
                		.replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", ""),
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                		" ",logBeginDate,logSumCode,new Date());
        		}
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	finally{//添加 finally 保证可以抛出异常
            	//by 曹丞 2009.01.24 保存回购计息异常信息 MS00004 QDV4.1-2009.2.1_09A
                throw new YssException("系统保存回购计息时出现异常!" + "\n", e); 
        	}
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally{
        	dbl.endTransFinal(conn, bTrans);
        }

    }
	
	  /**
     * 计算回购利息
     * @param rs ResultSet
     * @param dDate Date
     * @param purchaseIncome double
     * @param days int
     * @return double
     * @throws YssException
     */
    private double calcePurchaseInterest(ResultSet rs, java.util.Date dDate, double purchaseIncome, int days) throws YssException {
        double purchaseInterest = 0D;
        boolean Type = false; //交易所回购计息方式（包括的选项有“计头不计尾”、“计尾不计头”）-- true为计头不计尾,false计为不计头
        boolean judgeCreateInterestEachday = false;
        int tday=0;
        try {
            //--------@@@@@@@@@@@@@@@@@@@@@@@@@@@ -------------------------------
            if (YssOperCons.YSS_ZQPZZLX_REEXCHANGE.equalsIgnoreCase(rs.getString("FType"))) { //若为场内(交易所)
                Type = ( (Boolean) purchaseParams.getResultWithPortAndKey(rs.getString("FPortCode"), YssOperCons.YSS_INNER_PURCHASEEXT)).booleanValue(); //获取交易所回购计息方式
            } else { //场外(银行间)
                Type = ( (Boolean) purchaseParams.getResultWithPortAndKey(rs.getString("FPortCode"), YssOperCons.YSS_INNER_PURCHASEBNT)).booleanValue(); //获取银行间回购计息方式	
                //tday = YssFun.dateDiff(rs.getDate("FBargainDate"), rs.getDate("FSettleDate")); //判断为T+0还是T+1      story 1570 20111121 by zhouwei        	
            }
        } catch (Exception ex) {
            throw new YssException("获取回购计息方式出现异常！", ex);
        }
        try {
            judgeCreateInterestEachday = getJudgeCreateInterestEachday(rs); //回购计息凭证当日计提参数
//            if(tday>0){//如果为T+1；计息区间为交易日下一个自然日到到期日         story 1570 20111121 by zhouwei
//            	Type=false;//计尾不计头
//        	} 
            if (Type) { //计头不计尾
                if (YssFun.dateDiff(dDate, rs.getDate("FEndDate")) == 1) { //末次
                    purchaseInterest = calcSpecialPurchaseInterest(days, purchaseIncome, judgeCreateInterestEachday); //特殊的回购利息处理
                } else if (YssFun.dateDiff(dDate, rs.getDate("FEndDate")) > 0) { //条件：结束日期大于计提日期，在尾日不计提回购利息
                    if (YssFun.dateDiff(rs.getDate("FBeginDate"),dDate) >= 0){//当为起息日时，进行计息
                        purchaseInterest = calcCommPurchaseInterest(days, purchaseIncome, judgeCreateInterestEachday); //计算每日回购利息
                    }

                }
            } else { //计尾不计头
                if (YssFun.dateDiff(dDate, rs.getDate("FEndDate")) == 0) { //末次
                    purchaseInterest = calcSpecialPurchaseInterest(days, purchaseIncome, judgeCreateInterestEachday); //特殊的回购利息处理
                } else if (YssFun.dateDiff(rs.getDate("FBeginDate"), dDate) >= 1) { //条件：计提日期大于开始日期1日，在头天不计提回购利息
                    purchaseInterest = calcCommPurchaseInterest(days, purchaseIncome, judgeCreateInterestEachday); //计算每日回购利息
                }
            }
        } catch (Exception e) {
            throw new YssException("计算回购计息出现异常！", e);
        }
        return purchaseInterest;
    }
    
    /**
     * 判断是否为国内业务
     * @return
     * @throws YssException
     */
    private boolean judgeInnerOurOut() throws YssException{
    	boolean innerOrout = false;
    	
    	ResultSet rs = null;
    	
    	String sqlStr = null;
    	
    	innerOrout = dbl.yssTableExist(pub.yssGetTableName("TB_DAO_FeeWay"));//判断是否存在国内参数设置表
    	
    	if (innerOrout){//若存在，再获取具体数据来判断是否为国内业务处理
    	   sqlStr = createJudgeInnerOroutWithFeeAssumeSql();
    	   try{
    	      rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
    	      if (rs.next()){//若有相关的设置，则说明为国内业务
    		       innerOrout = true;
    	      }
    	   }
    	   catch(Exception e){
    		   throw new YssException("通过费用承担信息判断是否为国内业务出现异常！", e);
    	   }
    	   finally{
    		    dbl.closeResultSetFinal(rs);
    	   }
    	}
    	
    	return innerOrout;
    }
	
	   /**
     * 获取费用承担方的设置
     * @param rs ResultSet
     * @return boolean
     */
    private boolean getFeeAssume(ResultSet rs) throws YssException {
        boolean assumeWithSet = false;
        ResultSet matchRs = null;
        String sql = createFeeAssumeSql(rs);
        String existInSetAssume = null;
        String[] feeCodes = null;
        try {
            matchRs = dbl.queryByPreparedStatement(sql); //modify by fangjiang 2011.08.14 STORY #788
            while (matchRs.next()) {
                existInSetAssume = matchRs.getString("FProductBear"); //产品承担
                if (existInSetAssume.length() > 0) { //若产品承担中有设置费用代码
                    feeCodes = existInSetAssume.split(","); //分割其中的各个费用代码
                    for (int codes = 0; codes < feeCodes.length; codes++) { //循环存在的费用代码
                        if ("03".equalsIgnoreCase(feeCodes[codes])) { //当其中存在03(经手费)
                            assumeWithSet = true; //设置boolean值
                            break; //退出设置判断
                        } //end if
                    } //end for
                } //end if
            } //end while
        } catch (Exception e) { //end try
            throw new YssException("获取费用承担信息出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(matchRs);
        }
        return assumeWithSet;
    }

    /**
     * 获取接口设置中费用承担中的费用设置
     * @return HashMap
     */
    private HashMap getFeeMatchingInformation() {
        HashMap feeMatching = null;
        feeMatching = new HashMap();
        feeMatching.put("03", "FFeeCode2"); //经手费

        return feeMatching;
    }
    
    /**
     * 拼装通过国内参数设置来判断是否有国内业务的sql语句
     * @return
     * @throws YssException
     */
    private String createJudgeInnerOroutWithFeeAssumeSql() throws YssException{
    	StringBuffer buf = new StringBuffer();
        boolean existSeatCode = false;
        try {
            buf.append("select * from ");
            buf.append(pub.yssGetTableName("TB_DAO_FeeWay"));//这里不再使用pub表，需要注意
            buf.append(" where FAssetGroupCode = ");
            //此处不再需要其他的条件，只需要知道此群中是否进行过国内的参数设置
            buf.append(dbl.sqlString(pub.getAssetGroupCode()));
        } catch (Exception e) {
            throw new YssException("拼装费用设置的信息的获取方式出现异常！", e);
        }
        return buf.toString();
    }

    /**
     * 拼装费用设置的信息sql语句
     * @param sPortCode String
     * @param sBrokeCode String
     * @param sSeatCode String
     * @return String
     */
    private String createFeeAssumeSql(ResultSet rs) throws YssException {
        StringBuffer buf = new StringBuffer();
        boolean existSeatCode = false;
        boolean existAnalysisCode2 = false;
        try {
            buf.append("select * from ");
            buf.append(pub.yssGetTableName("TB_DAO_FeeWay"));//这里不再使用pub表，需要注意
            buf.append(" where FPortCode = ");
            buf.append(dbl.sqlString(rs.getString("FPortCode")));
            existSeatCode = purchaseParams.judgeExistField(rs, "FSeatCode"); //判断记录集中是否存在席位代码
            if (existSeatCode) { //若存在，增加席位代码作为筛选条件
                buf.append(" and FSeatCode = ");
                buf.append(dbl.sqlString(rs.getString("FSeatCode")));
            }
            buf.append(" and FBrokerCode = ");
            existAnalysisCode2 = purchaseParams.judgeExistField(rs, "FAnalysisCode2");
            if (existAnalysisCode2){
            	buf.append(dbl.sqlString(rs.getString("FAnalysisCode2")));
            }else{
            	buf.append(dbl.sqlString(rs.getString("FBrokerCode")));
            }
            //buf.append(dbl.sqlString(null == rs.getString("FAnalysisCode2")?rs.getString("FBrokerCode"):rs.getString("FAnalysisCode2"))); //分析代码2为券商代码
            buf.append(" and FAssetGroupCode = ");
            buf.append(dbl.sqlString(pub.getAssetGroupCode()));
        } catch (Exception e) {
            throw new YssException("拼装费用设置的信息的获取方式出现异常！", e);
        }
        return buf.toString();
    }

    /**
     * xuqiji 20100712 重载方法  处理应收应付和到期收益的金额上有很多出入和问题  QDV4招商基金2010年7月7日01_B_回购
     * 获取回购收益数据，在产生到期回购收益时使用。
     * @param dDate Date
     * @param sSecurityCode String
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @return double
     * @throws YssException
     */
    //double 改 HashMap  同一证券有正回购和逆回购时会有两条数据
    public HashMap calcPurchaseIncome(java.util.Date dDate,String sSecurityCode, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        return calcPurchaseIncome(dDate,"",sSecurityCode,analy1,analy2,analy3);
    }
    
    /**
     * xuqiji 20100712 重载方法  处理应收应付和到期收益的金额上有很多出入和问题  QDV4招商基金2010年7月7日01_B_回购
     * 获取回购收益数据，在产生到期回购收益时使用。
     * @param dDate Date
     * @param sSecurityCode String
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @return double
     * @throws YssException
     */
    //double 改 HashMap  同一证券有正回购和逆回购时会有两条数据
    public HashMap calcPurchaseIncome(java.util.Date dDate,String sNum,String sSecurityCode, boolean analy1, boolean analy2, boolean analy3) throws YssException {
        HashMap hmIncome =new HashMap();
        String key="";//alter by leeyu MS01125  
    	double purchaseIncome = 0D;
        String sqlStr = null;
        ResultSet rs = null;
        sqlStr = buildPurchaseIncomesSql(dDate); //获取回购信息
        sqlStr = sqlStr + " where a.FSecurityCode = " + dbl.sqlString(sSecurityCode) + " and a.FPortCode in (" + operSql.sqlCodes(this.portCodes) + ")";//增加筛选条件
        sqlStr = sqlStr + (sNum.trim().length() > 0 ? " and a.Fnum = " + dbl.sqlString(sNum):"");
        try{        	        	
            rs = dbl.openResultSet(sqlStr); 
            while(rs.next()){
            	key =sSecurityCode+"\f"+YssFun.formatDate(dDate,"yyyy-MM-dd")+"\f"+rs.getString("FPortCode")+"\f"+rs.getString("FTradeTypeCode");//alter by leeyu MS01125 
                purchaseIncome = calcPurchaseIncome(rs); // 计算回购收益
                //alter by leeyu MS01125 
                if(hmIncome.get(key)!=null){
                	purchaseIncome = YssD.add(purchaseIncome, Double.parseDouble(String.valueOf(hmIncome.get(key))));
                }
                hmIncome.put(key, new Double(purchaseIncome));
                //alter by leeyu MS01125 
            }
        }
        catch(Exception e){
            throw new YssException("获取回购收益数据出现异常！",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
        return hmIncome;
    }
    
    public double calcPurcchaseIncomeUtil(ResultSet rs) throws YssException{
    	return calcPurchaseIncome(rs);
    }
    
    /**
     * 计算回购收益
     * @param rs ResultSet
     * @return double
     * @throws YssException
     */
    private double calcPurchaseIncome(ResultSet rs) throws YssException {
        double purchaseIncome = 0D;
        boolean resultFee = true; //回购（包括交易所和银行间）计息包含交易费用,默认包含费用
        try {
            resultFee = ( (Boolean) purchaseParams.getResultWithPortAndKey(rs.getString("FPortCode"), YssOperCons.YSS_INNER_PURCHASEWFEE)).booleanValue();
            if (YssOperCons.YSS_ZQPZZLX_REEXCHANGE.equalsIgnoreCase(rs.getString("FType"))) { //为场内数据(业务资料中)
                if (YssOperCons.YSS_JYLX_NRE.equalsIgnoreCase(rs.getString("FTradeTypeCode"))
                		 || YssOperCons.YSS_JYLX_REMR.equalsIgnoreCase(rs.getString("FTradeTypeCode"))) {//调整回购类型，买入及逆回购    add by zhouwei 20120523 bug 4284 买断式回购
                    if (resultFee) { //回购（包括交易所和银行间）计息包含交易费用(包含费用)
                        if (getFeeAssume(rs)) { //若回购交易费用由受托资产承担
                            purchaseIncome = YssD.sub(rs.getDouble("FPurchaseGain"),
                                YssD.add(rs.getDouble("FTradeFee1"), rs.getDouble("FTradeFee2"), rs.getDouble("FTradeFee3"), rs.getDouble("FTradeFee4"),
                                         rs.getDouble("FTradeFee5"), rs.getDouble("FTradeFee6"), rs.getDouble("FTradeFee7"), rs.getDouble("FTradeFee8"))); //应收利息-交易费用-佣金
                        } else { // end if 受托资产承担, 若回购交易费用由券商承担
                        	//edit by licai 20101130 BUG #459 交易所回购计息只减掉了费用1，应该减掉所有费用 
                            purchaseIncome = YssD.sub(rs.getDouble("FPurchaseGain"), rs.getDouble("FTradeFee1")); //回购利息-佣金  bug4540
//                            purchaseIncome = YssD.sub(rs.getDouble("FPurchaseGain"),  YssD.add(rs.getDouble("FTradeFee1"), rs.getDouble("FTradeFee2"), rs.getDouble("FTradeFee3"), rs.getDouble("FTradeFee4"),
//                                    rs.getDouble("FTradeFee5"), rs.getDouble("FTradeFee6"), rs.getDouble("FTradeFee7"), rs.getDouble("FTradeFee8"))); //回购利息-交易费用-佣金
                           //edit by licai 20101130 BUG #459==========================================end
                        }
                    } else { //end if 回购（包括交易所和银行间）计息包含交易费用, 不包含费用
                        purchaseIncome = rs.getDouble("FPurchaseGain");
                    }
                } else { //end if 买入回购
                    if (resultFee) { //回购（包括交易所和银行间）计息包含交易费用(包含费用)
                        if (getFeeAssume(rs)) { //若回购交易费用由受托资产承担
                            purchaseIncome = YssD.add(rs.getDouble("FPurchaseGain"),
                                YssD.add(rs.getDouble("FTradeFee1"), rs.getDouble("FTradeFee2"), rs.getDouble("FTradeFee3"), rs.getDouble("FTradeFee4"),
                                         rs.getDouble("FTradeFee5"), rs.getDouble("FTradeFee6"), rs.getDouble("FTradeFee7"), rs.getDouble("FTradeFee8"))); //应收利息+交易费用+佣金
                        } else { // end if 受托资产承担, 若回购交易费用由券商承担
                            purchaseIncome = YssD.add(rs.getDouble("FPurchaseGain"), rs.getDouble("FTradeFee1")); //回购利息+佣金
                        }
                    } else { // end if 回购（包括交易所和银行间）计息包含交易费用
                        purchaseIncome = rs.getDouble("FPurchaseGain");
                    }
                }
            } else { // end if 场内数据, 为场外数据(回购资料)
                if (resultFee) { //回购（包括交易所和银行间）计息包含交易费用(包含费用)
                    if (YssOperCons.YSS_JYLX_NRE.equalsIgnoreCase(rs.getString("FTradeTypeCode"))) {//调整回购类型，买入及逆回购
                        purchaseIncome = YssD.sub(rs.getDouble("FPurchaseGain"), YssD.add(rs.getDouble("FTradeHandleFee"), rs.getDouble("FBankHandleFee"), rs.getDouble("FSetServiceFee"))); //回购收益-（交易手续费+银行手续费+结算服务费）
                    } else { //end if 买入回购
                        purchaseIncome = YssD.add(rs.getDouble("FPurchaseGain"), YssD.add(rs.getDouble("FTradeHandleFee"), rs.getDouble("FBankHandleFee"), rs.getDouble("FSetServiceFee"))); //回购收益+（交易手续费+银行手续费+结算服务费）
                    }
                } else { // end if 回购（包括交易所和银行间）计息包含交易费用
                    purchaseIncome = rs.getDouble("FPurchaseGain");
                }
            }
        } catch (Exception e) {
            throw new YssException("计算回购收益出现异常！", e);
        }
        return purchaseIncome;
    }

    /**
     * 计算每日回购利息
     * @param rs ResultSet
     * @param dDate Date
     * @param createInterestEachday boolean 回购计息凭证当日计提参数
     * @param purchaseIncome double
     * @return double
     */
    private double calcCommPurchaseInterest(int days, double purchaseIncome, boolean createInterestEachday) throws YssException {
        double purchaseInterest = 0D;
        try {
            if (createInterestEachday) { //每日计提
                purchaseInterest = YssD.div(purchaseIncome, days);
            } else { //每日不计提
                purchaseInterest = 0D;
            }
        } catch (Exception e) {
            throw new YssException("计算每日回购利息出现异常！", e);
        }
        return purchaseInterest;
    }
    
    /**
     * 获取此组合的回购通用参数设置
     * @throws YssException
     */
    public void getPurchaseParams() throws YssException {//调整为public类型
        purchaseParams = new InnerPubParamsWithPurchase();
        purchaseParams.setYssPub(pub);
        purchaseParams.getAllPubParams(this.portCodes);
    }
	
	 /**
     *  特殊的回购利息处理，包括末次计提和回购计息凭证当日不计提
     * @param days int
     * @param purchaseIncome double
     * @param createInterestEachday boolean 回购计息凭证当日计提参数
     * @return double
     * @throws YssException
     */
    private double calcSpecialPurchaseInterest(int days, double purchaseIncome, boolean createInterestEachday) throws YssException {
        double purchaseInterest = 0D;
        try {
            if (createInterestEachday) { //每日计提
                purchaseInterest = YssD.sub(purchaseIncome, YssD.mul(YssD.div(purchaseIncome, days, 2), days - 1));
            } else { //每日不计提
                purchaseInterest = purchaseIncome;
            }
        } catch (Exception e) {
            throw new YssException("计算每日回购利息(特殊 )出现异常！", e);
        }
        return purchaseInterest;
    }
	
	 /**
     * 获取通用参数回购计息凭证当日计提的设置值
     * @param rs ResultSet
     * @return boolean
     * @throws YssException
     */
    private boolean getJudgeCreateInterestEachday(ResultSet rs) throws YssException {
        boolean eachDay = true; //设置默认值为每日计提回购利息
        try {
            eachDay = ( (Boolean) purchaseParams.getResultWithPortAndKey(rs.getString("FPortCode"), YssOperCons.YSS_INNER_PURCHASEPED)).booleanValue();
        } catch (Exception ex) {
            throw new YssException("获取回购计息凭证当日计提参数设置出现异常！", ex);
        }
        return eachDay;
    }
}
