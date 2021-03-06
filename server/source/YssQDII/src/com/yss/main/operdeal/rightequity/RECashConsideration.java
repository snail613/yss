package com.yss.main.operdeal.rightequity;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.yss.dsub.YssPreparedStatement;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.parasetting.*;
import com.yss.main.storagemanage.*;
import com.yss.manager.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

/**
 * <p>Title:xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理</p>
 *
 * <p>Description:现金对价权益业务处理类 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RECashConsideration
    extends BaseRightEquity {
    private String sSecurityCode = ""; //保存证券代码
    private ArrayList tradeRealRightData = new ArrayList(); //保存交易关联表中数据
    
    public RECashConsideration() {
    }

    /**
     * 做现金对价权益业务处理，产生业务资料数据保存到业务资料javaBean中,产生两笔业务资料数据，和一笔交易关联数据
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return ArrayList 返回保存交易子表bean数据的集合
     * @throws YssException 异常
     */
    public ArrayList getDayRightEquitys(java.util.Date dDate, String sPortCode) throws
        YssException {
        StringBuffer buff = null;       //sql语句的拼接
        ResultSet rs = null;
        double dSecurityAmount = 0;     //证券数量
        double dSecurityCost = 0;       //证券成本
        double dRight = 0;              //权益（主表）
        double dRightSub = 0;           //权益（子表）
        String strSubRightType = "";    //权益类型
        String strCashAccCode = " ";    //现金帐户
        String strYearMonth = "";       //保存截取日期的年和天
        CashAccountBean caBean = null;  //声明现金账户的bean
        double dBaseRate = 1;           //基础汇率
        double dPortRate = 1;           //组合汇率
        boolean analy1;                 //分析代码1
        boolean analy2;                 //分析代码2
        boolean analy3;                 //分析代码3
        TradeSubBean subTrade = null;   //交易子表的javaBean
        TradeSubBean subTrade1 = null;  //交易子表的javaBean
        TradeRelaBean tradeReal = null; //交易关联表的javaBean
        YssCost cost = null;            //声明成本
        SecurityStorageBean secSto = null;  //证券库存的javaBean
        ArrayList reArr = new ArrayList();  //保存数据
        CashAccLinkBean cashAccLink = null; //声明现金账户链接
        ArrayList linkList = null;
        long sNum = 0;      //为了产生现金对价的编号不重复
        long sDZNum = 0;    //为了产生现金对价到账日的编号不重复
        java.util.Date beforeDate = null;   //保存上次循环的日期
        String strNumDate = "";             //保存现金对价交易编号
        String strDZNumDate = "";           //保存现金对价到账日交易编号
        boolean bCheckData = false;         //为了检查接口导入的现金对价到账数据有没有,默认没有
        Date StorageDate = null; //MS01233  QDV4赢时胜(上海)2010年06月03日01_A add by jiangshichao
        try {
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B start---//
        	TradeDataAdmin tradeData = new TradeDataAdmin();//交易数据操作类
        	tradeData.setYssPub(pub);
        	tradeData.delete("", dDate,dDate,null,null,"",sPortCode, "", 
        			"",YssOperCons.YSS_JYLX_XJDJ, "", "",false, "HD_QY");
        	
        	String strSql = " delete from " + pub.yssGetTableName("Tb_Data_TradeRela") + 
        	" where FRelaType = " + dbl.sqlString(YssOperCons.YSS_JYLX_XJDJ) +
        	" and FNum like 'T" + YssFun.formatDate(dDate, "yyyyMMdd") + "%'" + 
        	" and FPortCode in (" + operSql.sqlCodes(sPortCode) + ")";
            dbl.executeSql(strSql);
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B end---//
        	
            this.doDataPretreatment(dDate, sPortCode); //现金对价权益数据的预处理，主要是考虑跨组合群，组合的处理

            buff = new StringBuffer();
            BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.getOperDealCtx().getBean("cashacclinkdeal"); //账户链接
            operFun.setYssPub(pub);
            strYearMonth = YssFun.left(YssFun.formatDate(dDate), 4) + "00";     //赋值
            //YssType lAmount = new YssType();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");     //判断是否有分析代码
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            strDealInfo = "no";
            //操作子表
            //------ modify by wangzuochun 2010.07.12  MS01417    根据权益信息中的证券信息去查找证券信息维护中的交易所    QDV4上海2010年07月07日01_B    
            buff.append("select a.*, b.*,c.FTradeCury,c.FEXCHANGECODE,d.FPortCury from");
            buff.append("( select FSecurityCode as FSecurityCode1,FRecordDate,FExRightDate,FPayDate,FPreTaxRatio,FAfterTaxRatio from ");
            buff.append(pub.yssGetTableName("Tb_Data_PreCashConsider"));    //从现金对价权益预处理表中获取权益数据
            buff.append(" where FExRightDate = ").append(dbl.sqlDate(dDate));   //权益处理时取除权日数据，做权益确认日处理
            buff.append(" and FCheckState = 1 order by FPayDate) a ");
            //--- MS01233  QDV4赢时胜(上海)2010年06月03日01_A 添加字段FEXCHANGECODE用于判断交易市场是国内市场还是国外市场  add by jiangshichao  2010.06.07
            buff.append(" left join (select FSecurityCode,FTradeCury,FEXCHANGECODE from ");
            buff.append(pub.yssGetTableName("Tb_Para_Security"));   //关联证券信息表
            buff.append(" where FCheckState = 1 ) c on a.FSecurityCode1 = c.FSecurityCode");
            
            buff.append(" join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Stock_Security")); //关联证券库存表
            buff.append(" where FPortCode in (" + operSql.sqlCodes(sPortCode)); //组合代码
            buff.append(")");
            buff.append(" and FYearMonth<>").append(dbl.sqlString(strYearMonth)); //不是期初数库存
            buff.append(" and FCheckState=1 )b  on a.FSecurityCode1 = b.fsecuritycode ");
            buff.append(" and (case when c.fexchangecode in ('CY', 'CS', 'CG') then a.FRecordDate else a.FExRightDate - 1 end) = b.FStorageDate ");     //取权益确认日库存
            
            buff.append(" left join (select FPortCode,FPortCury from ");
            buff.append(pub.yssGetTableName("Tb_Para_Portfolio"));  //关联组合信息表
            buff.append(" where FCheckState = 1 ) d on b.FPortCode = d.FPortCode"); //关取证券信息表和组合表，取出交易货币和组合货币。
            //--------------------------------MS01417-----------------------------------//
            rs = dbl.queryByPreparedStatement(buff.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            buff.delete(0, buff.length());

            if (rs.next()) {
                //--------------------拼接现金对价交易编号---------------------
                strNumDate = YssFun.formatDatetime(dDate).
                    substring(0, 8);
                strNumDate = strNumDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_Trade"),
                                           dbl.sqlRight("FNUM", 6),
                                           "000000",
                                           " where FNum like 'T"
                                           + strNumDate + "%'", 1);
                strNumDate = "T" + strNumDate;
                strNumDate = strNumDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_SubTrade"),
                                           dbl.sqlRight("FNUM", 5), "00000",
                                           " where FNum like '"
                                           +
                                           strNumDate.replaceAll("'", "''") +
                                           "%'");
                String s = strNumDate.substring(9, strNumDate.length());
                sNum = Long.parseLong(s);
                rs.beforeFirst(); //返回第一个rs，此方法要求打开游标类型为 TYPE_SCROLL_INSENSITIVE
                while (rs.next()) {
                    //-------------------------设置现金账户链接属性值----------------------
                    cashacc.setYssPub(pub);
                    cashacc.setLinkParaAttr( (analy1 ? rs.getString("FAnalysisCode1") : " "),   //投资经理
                                            rs.getString("FPortCode"),      //组合代码
                                            rs.getString("FSecurityCode1"), //目标证券代码
                                            (analy2 ? rs.getString("FAnalysisCode2") : " "),    //券商
                                            YssOperCons.YSS_JYLX_XJDJ,      //权益类型为现金对价
                                            rs.getDate("FRecordDate"));     //权益确认日
                    //-------------------------end-----------------------------------------//
                    subTrade = new TradeSubBean();      //业务资料
                    subTrade1 = new TradeSubBean();     //业务资料1
                    tradeReal = new TradeRelaBean();    //交易关联
                    secSto = new SecurityStorageBean(); //证券库存
                    secSto.setYssPub(pub);
                    /********************************************************
                     *  MS01233  QDV4赢时胜(上海)2010年06月03日01_A  add by jiangshichao 2010.06.07
                     *  若交易所为国内交易所，如交易所代码为：CG、CS、CY，即为国内业务，则获取登记日当天相关证券的库存数量作为权益数量，
	                 *  若交易所为国外交易所，即为QDII普通业务，则获取除权日前一天的库存数量作为权益数量
                     */
                    if("CS".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))||"CG".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))
                    	||"CY".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))){
                    	StorageDate = rs.getDate("FRecordDate");
                    }else{
                    	//除权日前一天
                    	java.util.Date date = YssFun.parseDate(YssFun.formatDate(rs.getDate("FEXRightDate"), "yyyy-MM-dd"));
                    	StorageDate = YssFun.toSqlDate(YssFun.addDay(date, -1));
                    }
                    secSto = secSto.getStorageCost(StorageDate,
                            rs.getString("FSecurityCode1"), //证券代码
                            rs.getString("FPortCode"),      //组合代码
                            (analy1 ? rs.getString("FAnalysisCode1") : " "), //分析代码1
                            (analy2 ? rs.getString("FAnalysisCode2") : " "), //分析代码2
                            "", "C",
                            rs.getString("FAttrClsCode"));  //"C"为获取 核算成本
//                  secSto = secSto.getStorageCost(rs.getDate("FRecordDate"), //权益确认日
//                  rs.getString("FSecurityCode1"), //证券代码
//                  rs.getString("FPortCode"),      //组合代码
//                  (analy1 ? rs.getString("FAnalysisCode1") : " "), //分析代码1
//                  (analy2 ? rs.getString("FAnalysisCode2") : " "), //分析代码2
//                  "", "C",
//                  rs.getString("FAttrClsCode"));  //"C"为获取 核算成本
                    
                        if (secSto != null) {
                            dSecurityCost = YssFun.toDouble(secSto.getStrStorageCost());        //为汇总的核算成本赋值
                            dSecurityAmount = YssFun.toDouble(secSto.getStrStorageAmount());    //为汇总的库存数量赋值
                        } else {
                            dSecurityCost = 0.0;    //为汇总的核算成本赋值
                            dSecurityAmount = 0.0;  //为汇总的库存数量赋值
                        }

                    CtlPubPara pubPara = new CtlPubPara(); //通用参数实例化
                    pubPara.setYssPub(pub);     //设置Pub
                    String rightsRatioMethods = (String) pubPara.getRightsRatioMethods(rs.getString("FPortCode")); //获取通用参数值
                    String ratioMethodsDetail = pubPara.getBRightsRatioMethods(rs.getString("FPortCode"),YssOperCons.YSS_JYLX_XJDJ);//按权益类型获取权益比例方式 panjunfang add 20100510 B股业务
                    if(ratioMethodsDetail.length() > 0){
                    	rightsRatioMethods = ratioMethodsDetail;
                    }
                    if (dSecurityAmount > 0) { //判断证券数量是否大于0
                        //现金对价权益=确认日库存数量*权益比例
                        dRightSub = YssD.mul(dSecurityAmount,
                                             (rightsRatioMethods.equalsIgnoreCase("PreTaxRatio") ?
                                              rs.getDouble("FPreTaxRatio") : rs.getDouble("FAfterTaxRatio"))); //通过通用参数获取权益比例方式
                        caBean = cashacc.getCashAccountBean();
                        if (caBean != null) {
                            strCashAccCode = caBean.getStrCashAcctCode(); //现金账户
                        } else {
                            throw new YssException("系统执行现金对价权益时出现异常！" + "\n" + "【" +
                                rs.getString("FSecurityCode1") +
                                "】证券现金对价权益处理时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
                        }
                        //--------------------拼接交易编号---------------------
                        sNum++;
                        String tmp = "";
                        for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                            tmp += "0";
                        }
                        strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                        //------------------------end--------------------------//
                        dBaseRate = this.getSettingOper().getCuryRate(rs.
                            getDate("FExRightDate"),    //除权日
                            rs.getString("FTradeCury"), //交易币种
                            rs.getString("FPortCode"),  //组合代码
                            YssOperCons.YSS_RATE_BASE); //获取基础汇率的值
                        dPortRate = this.getSettingOper().getCuryRate(rs.
                            getDate("FExRightDate"),
                            rs.getString("FPortCury"),
                            rs.getString("FPortCode"),
                            YssOperCons.YSS_RATE_PORT); //获取组合汇率的值
                        /*--------以下为交易子表实体bean赋值,交易类型为：现金对价，日期为：除权日---------------*/

                        subTrade.setNum(strNumDate); //为交易编号赋值

                        subTrade.setSecurityCode(rs.getString("FSecurityCode1"));   //证券代码

                        subTrade.setPortCode(rs.getString("FPortCode"));            //组合代码
                        
                        subTrade.setAttrClsCode(rs.getString("FAttrClsCode"));//所属分类
                        
                        if (analy1) {
                            subTrade.setInvMgrCode(rs.getString("FAnalysisCode1")); //投资经理
                        } else {
                            subTrade.setInvMgrCode(" ");
                        }
                        if (analy2) {
                            subTrade.setBrokerCode(rs.getString("FAnalysisCode2")); //券商
                        } else {
                            subTrade.setBrokerCode(" ");
                        }

                        subTrade.setTradeCode(YssOperCons.YSS_JYLX_XJDJ);   //交易类型-现金对价‘85’

                        subTrade.setTailPortCode(strCashAccCode);           //尾差组合代码

                        subTrade.setAllotProportion(0); //分配比例

                        subTrade.setOldAllotAmount(0);  //原始分配数量和交易数量相同

                        subTrade.setAllotFactor(0);     //分配因子

                        subTrade.setBargainDate(YssFun.formatDate(rs.getDate("FExRightDate"))); //成交日期

                        subTrade.setBargainTime("00:00:00"); //成交时间
                        //------ modify by wangzuochun  MS01140   日终处理权益处理出来的现金对价数据与国内接口处理出来的不一致    QDV4国内（测试）2010年04月28日01_B   
                        //subTrade.setSettleDate(YssFun.formatDate(rs.getDate("FExRightDate")));  //结算日期
                        subTrade.setSettleDate(YssFun.formatDate(rs.getDate("FPayDate"))); //结算日期
                        //----------------------------- MS01140 ---------------------------//
                        subTrade.setSettleTime("00:00:00"); //结算时间

                        subTrade.setAutoSettle(new Integer(1).toString()); //自动结算

                        subTrade.setPortCuryRate(dPortRate); //组合汇率

                        subTrade.setBaseCuryRate(dBaseRate); //基础汇率

                        subTrade.setTradeAmount(0); //交易数量

                        subTrade.setTradePrice(0);  //交易价格

                        subTrade.setTradeMoney(0);  //交易金额
                        //------ modify by wangzuochun  MS01140   日终处理权益处理出来的现金对价数据与国内接口处理出来的不一致    QDV4国内（测试）2010年04月28日01_B   
                        //subTrade.setAccruedInterest(0);     //应计利息
                        subTrade.setAccruedInterest(dRightSub); //应计利息-为权益
                        //----------------------------- MS01140 ---------------------------//
                        //---------------------以下为成本赋值--------------
                        cost = new YssCost();
                        cost.setCost(0);        //原币核算成本

                        cost.setMCost(0);       //原币管理成本

                        cost.setVCost(0);       //原币估值成本

                        cost.setBaseCost(0);    //基础货币核算成本

                        cost.setBaseMCost(0);   //基础货币管理成本

                        cost.setBaseVCost(0);   //基础货币估值成本

                        cost.setPortCost(0);    //组合货币核算成本

                        cost.setPortMCost(0);   //组合货币管理成本

                        cost.setPortVCost(0);   //组合货币估值成本
                        subTrade.setCost(cost); //成本
                        //---------------------end-----------------//
                        subTrade.setDataSource(0);      //数据源

                        subTrade.setDsType("HD_QY");    //操作类型，表示系统操作数据，主要是和接口导入数据进行区分

                        subTrade.checkStateId = 1;      //审核状态

                        subTrade.creatorCode = pub.getUserCode();   //创建人

                        subTrade.checkTime = YssFun.formatDatetime(new java.util.Date()); //审核时间

                        subTrade.checkUserCode = pub.getUserCode(); //审核人

                        subTrade.creatorTime = YssFun.formatDatetime(new java.util.Date()); //创建时间
                        //------ modify by wangzuochun  MS01140   日终处理权益处理出来的现金对价数据与国内接口处理出来的不一致    QDV4国内（测试）2010年04月28日01_B 
                        //subTrade.setTotalCost(0);   //投资总成本
                        subTrade.setTotalCost(dRightSub); //投资总成本
                        //----------------------------- MS01140 ---------------------------//
                        subTrade.setFactSettleDate(YssFun.formatDate(rs.getDate(
                            "FExRightDate")));      //实际结算日期

                        subTrade.setMatureDate("9998-12-31");       //到期日期

                        subTrade.setMatureSettleDate("9998-12-31"); //到期结算日期

                        subTrade.setSettleState(new Integer(0).toString()); //结算状态，未结算“0”

                        subTrade.setFactCashAccCode(strCashAccCode);    //实际结算帐户

                        subTrade.setCashAcctCode(strCashAccCode);       //设置现金账户

                        subTrade.setFactSettleMoney(0); //实际结算金额

                        subTrade.setExRate(1); //兑换汇率

                        subTrade.setFactPortRate(dPortRate); //实际结算组合汇率

                        subTrade.setFactBaseRate(dBaseRate); //实际结算基础汇率

                        reArr.add(subTrade); //把交易子表数据保存到集合中

                        /*--------------------------end------------------------*/

                        /*---------------------------以下为交易关联bean赋值-------------------------*/

                        tradeReal.setSNum(strNumDate); //交易编号
                        
                        //add by yangheng MS01687 QDV4赢时胜(测试)2010年09月01日02_B 2010.09.06
                        tradeReal.setAttrClsCode(rs.getString("FAttrClsCode"));//属性分类
                        //--------------
                        
                        tradeReal.setSRelaType(YssOperCons.YSS_JYLX_XJDJ); //关联类型

                        tradeReal.setSPortCode(sPortCode); //组合代码

                        if (analy1) {
                            tradeReal.setSAnalysisCode1(rs.getString("FAnalysisCode1")); //分析代码1
                        } else {
                            tradeReal.setSAnalysisCode1(" ");
                        }

                        if (analy2) {
                            tradeReal.setSAnalysisCode2(rs.getString("FAnalysisCode2")); //分析代码2
                        } else {
                            tradeReal.setSAnalysisCode2(" ");
                        }

                        if (analy3) {
                            tradeReal.setSAnalysisCode3(rs.getString("FAnalysisCode3")); //分析代码3
                        } else {
                            tradeReal.setSAnalysisCode3(" ");
                        }

                        tradeReal.setSSecurityCode(rs.getString("FSecurityCode1")); //证券代码
                        
                        tradeReal.setDAmount(0);

                        tradeReal.setIInOut( -1); //设置成本流入、流出方向为-流出

                        //--------------设置原币成本------------------
                        tradeReal.setDCost(dRightSub); //核算成本

                        tradeReal.setDMCost(dRightSub); //管理成本

                        tradeReal.setDVCost(dRightSub); //估值成本
                        //---------------end------------------------

                        //---------------设置基础货币成本---------------
                        tradeReal.setDBaseCuryCost(YssD.mul(dRightSub, dBaseRate));     //基础货币核算成本

                        tradeReal.setDMBaseCuryCost(YssD.mul(dRightSub, dBaseRate));    //基础货币管理成本

                        tradeReal.setDVBaseCuryCost(YssD.mul(dRightSub, dBaseRate));    //基础货币估值成本
                        //---------------end--------------------------

                        //---------------设置组合货币成本----------------
                        tradeReal.setDPortCuryCost(YssD.div(YssD.mul(dRightSub, dBaseRate), dPortRate));    //组合货币核算成本

                        tradeReal.setDMPortCuryCost(YssD.div(YssD.mul(dRightSub, dBaseRate), dPortRate));   //组合货币管理成本

                        tradeReal.setDVPortCuryCost(YssD.div(YssD.mul(dRightSub, dBaseRate), dPortRate));   //组合货币估值成本
                        //---------------end--------------------------
                        tradeReal.setSDesc(""); //描述
                     

                        tradeReal.checkStateId = 1; //审核状态

                        tradeReal.creatorCode = pub.getUserCode();      //创建人
                        
                        tradeReal.creatorTime = YssFun.formatDatetime(new java.util.Date()); //创建时间

                        tradeReal.checkUserCode = pub.getUserCode();    //审核人

                        tradeReal.checkTime = YssFun.formatDatetime(new java.util.Date()); //审核时间

                        tradeRealRightData.add(tradeReal); //把交易关联数据保存到集合中

                        /*--------以下为交易子表实体bean赋值,交易类型为：现金对价到账，日期为：到账日-----------*/
                        if (beforeDate == null || (!rs.getDate("FPayDate").toString().endsWith(beforeDate.toString())&&
                            !rs.getDate("FPayDate").toString().endsWith(rs.getDate("FExRightDate").toString()))) {
                            //-------------------------------拼接现金对价到帐日交易编号------------------------//
                            strDZNumDate = YssFun.formatDatetime(rs.getDate("FPayDate")).
                                substring(0, 8);
                            strDZNumDate = strDZNumDate +
                                dbFun.getNextInnerCode(pub.yssGetTableName(
                                    "Tb_Data_Trade"),
                                dbl.sqlRight("FNUM", 6),
                                "000000",
                                " where FNum like 'T"
                                + strDZNumDate + "%'", 1);
                            strDZNumDate = "T" + strDZNumDate;
                            strDZNumDate = strDZNumDate +
                                dbFun.getNextInnerCode(pub.yssGetTableName(
                                    "Tb_Data_SubTrade"),
                                dbl.sqlRight("FNUM", 5), "00000",
                                " where FNum like '"
                                +
                                strDZNumDate.replaceAll("'", "''") +
                                "%'");
                            String ss = strDZNumDate.substring(9, strDZNumDate.length());
                            sDZNum = Long.parseLong(ss);
                        } else if (rs.getDate("FPayDate").toString().endsWith(rs.getDate("FExRightDate").toString())) {
                            sNum++;
                            String tmp1 = "";
                            for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                                tmp1 += "0";
                            }
                            strNumDate = strNumDate.substring(0, 9) + tmp1 + sNum;
                            strDZNumDate = strNumDate;
                        }else{
                            //--------------------拼接现金对价到账日交易编号---------------------
                            sDZNum++;
                            String stmp = "";
                            for (int i = 0; i < s.length() - String.valueOf(sDZNum).length(); i++) {
                                stmp += "0";
                            }
                            strDZNumDate = strDZNumDate.substring(0, 9) + stmp + sDZNum;
                            //------------------------end--------------------------//
                        }
                        //下面产生的业务资料数据比较特殊，成交日期可能不为操作日期当天，所以保存数据前先删除已经存在的日期为到账日的那笔交易数据，接口导入数据除外
                        deleteTradeDataButJK(rs.getDate("FPayDate"), rs.getString("FSecurityCode1"), rs.getString("FPortCode"),
                                             (analy1 ? rs.getString("FAnalysisCode1") : ""), (analy2 ? rs.getString("FAnalysisCode2") : ""),
                                             YssOperCons.YSS_JYLX_XJDJDZ, "HD_QY");

                        beforeDate = rs.getDate("FPayDate"); //把这次循环的日期保存起来

                        subTrade1.setNum(strDZNumDate); //为交易编号赋值

                        subTrade1.setSecurityCode(rs.getString("FSecurityCode1"));  //证券代码

                        subTrade1.setPortCode(rs.getString("FPortCode")); //组合代码
                        
                        subTrade1.setAttrClsCode(rs.getString("FAttrClsCode"));//所属分类
                        
                        if (analy1) {
                            subTrade1.setInvMgrCode(rs.getString("FAnalysisCode1")); //投资经理
                        } else {
                            subTrade1.setInvMgrCode(" ");
                        }
                        if (analy2) {
                            subTrade1.setBrokerCode(rs.getString("FAnalysisCode2")); //券商
                        } else {
                            subTrade1.setBrokerCode(" ");
                        }

                        subTrade1.setTradeCode(YssOperCons.YSS_JYLX_XJDJDZ); //交易类型-现金对价到账‘86’

                        subTrade1.setTailPortCode(strCashAccCode); //尾差组合代码

                        subTrade1.setAllotProportion(0); //分配比例

                        subTrade1.setOldAllotAmount(0); //原始分配数量和交易数量相同

                        subTrade1.setAllotFactor(0); //分配因子

                        subTrade1.setBargainDate(YssFun.formatDate(rs.getDate(
                            "FPayDate"))); //成交日期

                        subTrade1.setBargainTime("00:00:00"); //成交时间

                        subTrade1.setSettleDate(YssFun.formatDate(rs.getDate(
                            "FPayDate"))); //结算日期

                        subTrade1.setSettleTime("00:00:00"); //结算时间

                        subTrade1.setAutoSettle(new Integer(1).toString()); //自动结算

                        subTrade1.setPortCuryRate(dPortRate); //组合汇率

                        subTrade1.setBaseCuryRate(dBaseRate); //基础汇率

                        subTrade1.setTradeAmount(0); //交易数量

                        subTrade1.setTradePrice(0); //交易价格

                        subTrade1.setTradeMoney(0); //交易金额

                        subTrade1.setAccruedInterest(dRightSub); //应计利息-为权益

                        //---------------------以下为成本赋值--------------
                        cost = new YssCost();
                        cost.setCost(0); //原币核算成本

                        cost.setMCost(0); //原币管理成本

                        cost.setVCost(0); //原币估值成本

                        cost.setBaseCost(0); //基础货币核算成本

                        cost.setBaseMCost(0); //基础货币管理成本

                        cost.setBaseVCost(0); //基础货币估值成本

                        cost.setPortCost(0); //组合货币核算成本

                        cost.setPortMCost(0); //组合货币管理成本

                        cost.setPortVCost(0); //组合货币估值成本
                        subTrade1.setCost(cost); //成本
                        //---------------------end-----------------//
                        subTrade1.setDataSource(0); //数据源

                        subTrade1.setDsType("HD_QY"); //操作类型，表示系统操作数据，主要是和接口导入数据进行区分

                        subTrade1.checkStateId = 1; //审核状态

                        subTrade1.creatorCode = pub.getUserCode(); //创建人

                        subTrade1.checkTime = YssFun.formatDatetime(new java.util.Date()); //审核时间

                        subTrade1.checkUserCode = pub.getUserCode(); //审核人

                        subTrade1.creatorTime = YssFun.formatDatetime(new java.util.Date()); //创建时间

                        subTrade1.setTotalCost(dRightSub); //投资总成本

                        subTrade1.setFactSettleDate(YssFun.formatDate(rs.getDate(
                            "FPayDate"))); //实际结算日期

                        subTrade1.setMatureDate("9998-12-31"); //到期日期

                        subTrade1.setMatureSettleDate("9998-12-31"); //到期结算日期

                        subTrade1.setSettleState(new Integer(0).toString()); //结算状态，未结算“0”

                        subTrade1.setFactCashAccCode(strCashAccCode); //实际结算帐户

                        subTrade1.setCashAcctCode(strCashAccCode); //设置现金账户

                        subTrade1.setFactSettleMoney(dRightSub); //实际结算金额

                        subTrade1.setExRate(1); //兑换汇率

                        subTrade1.setFactPortRate(dPortRate); //实际结算组合汇率

                        subTrade1.setFactBaseRate(dBaseRate); //实际结算基础汇率

                        //此方法判断接口导入的数据中是否有要处理的权益信息数据产生的现金到账数据
                        bCheckData = checkSubTradeDataBYInter(rs.getDate("FPayDate"), rs.getString("FSecurityCode1"), rs.getString("FPortCode"),
                        		  // EDIT by lidaolong 20110407 #536 有关国内接口数据处理顺序的变更  
                        		YssOperCons.YSS_JYLX_XJDJDZ, "ZD_QY,ZD_QY_T+1");
                        if (!bCheckData) {
                        	//------ 不产生‘86’类型现金对价数据  modify by wangzuochun  MS01140   日终处理权益处理出来的现金对价数据与国内接口处理出来的不一致    QDV4国内（测试）2010年04月28日01_B 
                            //reArr.add(subTrade1); //把交易子表数据保存到集合中
                        	//-------------MS01140---------//
                        }
                        /*--------------------------end------------------------*/
                    }
                }
                strDealInfo = "true"; //表示有权益数据
            } else {
                strDealInfo = "no"; //表示无权益数据
            }
            return reArr;
        } catch (Exception e) {
            strDealInfo = "false";
            throw new YssException("计算现金对价权益处理出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 此方法判断接口导入的数据中是否有要处理的权益信息数据产生的现金到账数据
     * @param dDate Date 到账日期
     * @param sSecurityCode String 证券代码
     * @param sPortCode String 组合代码
     * @param sTradeType String 交易类型
     * @param sDsType String 操作类型界面上输入 其他业务资料数据用'HD_JK' FDataSouce=0，权益--'HD_QY' ；接口：读入'ZD_JK'   FDataSource=1 ，权益处理 'ZD_QY'  FDataSource=1
     * @return boolean
     */
    private boolean checkSubTradeDataBYInter(Date dDate, String sSecurityCode, String sPortCode, String sTradeType, String sDsType) throws YssException {
        boolean bCheckData = false;
        ResultSet rs = null;
        StringBuffer buff = null;
        try {
            buff = new StringBuffer();
            buff.append(" select * from ");
            buff.append(pub.yssGetTableName("tb_data_subtrade")); //交易子表
            buff.append(" where FCheckState =1 and FBargaindate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FSecurityCode in(").append(this.operSql.sqlCodes(sSecurityCode)).append(")"); //证券代码
            buff.append(" and FPortCode = ").append(dbl.sqlString(sPortCode)); //组合代码
            buff.append(" and FTradeTypeCode =").append(dbl.sqlString(sTradeType)); //交易类型
            buff.append(" and FDs =").append(dbl.sqlString(sDsType)); //操作类型

            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            if (rs.next()) {
                bCheckData = true;
            }
        } catch (Exception e) {
            throw new YssException("判断接口导入的数据中是否有要处理的权益信息数据产生的现金到账数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return bCheckData;
    }

    /**
     * 下面产生的业务资料数据比较特殊，成交日期可能不为操作日期当天，所以保存数据前先删除已经存在的日期为到账日的那笔交易数据，接口导入数据除外
     * @param payDate Date 到账日期
     * @param sSecurityCode String 证券代码
     * @param sPortCode String 组合代码
     * @param analysisCode1 String 分析代码1
     * @param analysisCode2 String 分析代码2
     * @param sTradeType String 交易类型
     * @param sDsType String 操作类型 界面上输入 其他业务资料数据用'HD_JK' FDataSouce=0，权益--'HD_QY' ；接口：读入'ZD_JK'   FDataSource=1 ，权益处理 'ZD_QY'  FDataSource=1
     * @throws YssException
     */
    private void deleteTradeDataButJK(Date payDate, String sSecurityCode, String sPortCode,
                                      String analysisCode1, String analysisCode2, String sTradeType, String sDsType) throws YssException {
        try {
            delCashTransfer(payDate, sSecurityCode, sPortCode,
                            analysisCode1, analysisCode2,
                            sTradeType, sDsType); //根据到帐日，证券代码，分析代码，交易类型和操作类型删除资金调拨数据
            TradeDataAdmin tradeData = new TradeDataAdmin(); //交易数据操作类
            tradeData.setYssPub(pub);
            tradeData.delete(payDate, sSecurityCode, sPortCode,
                             analysisCode1, analysisCode2,
                             sTradeType, false, sDsType); //根据到帐日，证券代码，分析代码，交易类型和操作类型删除业务资料数据
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * delCashTransfer
     *根据到帐日，证券代码，分析代码，交易类型和操作类型删除资金调拨数据
     * @param payDate Date 到帐日
     * @param sSecurityCode String 证券代码
     * @param sPortCode String 组合代码
     * @param analysisCode1 String 分析代码1
     * @param analysisCode2 String 分析代码2
     * @param sTradeType String 交易类型
     * @param sDsType String 操作类型 界面上输入 其他业务资料数据用'HD_JK' FDataSouce=0，权益--'HD_QY' ；接口：读入'ZD_JK'   FDataSource=1 ，权益处理 'ZD_QY'  FDataSource=1
     */
    private void delCashTransfer(Date payDate, String sSecurityCode, String sPortCode, String analysisCode1,
                                 String analysisCode2, String sTradeType, String sDsType) throws YssException {
        String strSql = "";
        String nums = ""; //编号
        ResultSet rs = null;
        boolean bTrans = true; //事务控制标识
        Connection conn = dbl.loadConnection(); //打开数据库连接
        try {
            strSql = "select FNum,FSecurityCode from " +
                pub.yssGetTableName("Tb_Data_SubTrade") + //业务资料表
                " where FTradeTypeCode = " + dbl.sqlString(sTradeType) +
                " and FPortCode in( " + this.operSql.sqlCodes(sPortCode) +
                ")" +
                " and FBargainDate between " + dbl.sqlDate(payDate) +
                " and " + dbl.sqlDate(payDate) +
                " and FSecurityCode =" + dbl.sqlString(sSecurityCode);

            if (analysisCode1.length() > 0) {
                strSql += " and FInvMgrCode = " + dbl.sqlString(analysisCode1); //投资经理
            }
            if (analysisCode2.length() > 0) {
                strSql += " and FBrokerCode = " + dbl.sqlString(analysisCode2); //券商
            }
            if (sDsType.length() > 0) {
                strSql += " and FDs = " + dbl.sqlString(sDsType); //操作类型
            }
            rs = dbl.queryByPreparedStatement(strSql);
            while (rs.next()) {
                nums += rs.getString("FNum") + ","; //拼接交易编号
            }
            if (nums.endsWith(",")) {
                nums = nums.substring(0, nums.length() - 1);
            }
            conn.setAutoCommit(false);
            //删除资金调拨子表
            strSql = "delete from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FNum in (select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FTradeNum in(" +
                this.operSql.sqlCodes(nums) + ") )";
            dbl.executeSql(strSql);
            //删除资金调拨主表
            strSql = "delete from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FTradeNum in (" + this.operSql.sqlCodes(nums) + ") ";
            dbl.executeSql(strSql);
            conn.commit(); //提交事务
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除资金调拨数据出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 调用基类方法，保存数据
     * @param alRightEquitys ArrayList 交易主子表中数据
     * @param tradeRealRightData ArrayList 交易关联表中数据
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @throws YssException 异常
     */
    public void saveRightEquitys(ArrayList alRightEquitys, ArrayList tradeRealRightData, java.util.Date dDate, String sPortCode) throws YssException {
        ArrayList newAlRightEquity = null;
        ArrayList newTradeRealRightData = null;
        try {
            if (alRightEquitys != null && alRightEquitys.size() > 0) {
                newAlRightEquity = checkSubTradeHaveRightData(alRightEquitys, YssOperCons.YSS_JYLX_XJDJ, "ZD_QY", dDate, sPortCode, this.sSecurityCode);
            }
            if (tradeRealRightData != null && tradeRealRightData.size() > 0) {
                newTradeRealRightData = checkTradeRelaHaveRightData(tradeRealRightData);
            }
            super.saveRightEquitys(newAlRightEquity, newTradeRealRightData, dDate, sPortCode); //调用基类保存数据方法
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * 判断接口导入数据中有没有要处理的权益信息的交易关联数据
     * @param tradeRealRightData ArrayList 保存交易关联数据的集合
     * @return ArrayList
     */
    public ArrayList checkTradeRelaHaveRightData(ArrayList tradeRealRightData) throws YssException {
        ArrayList newTradeRealRightData = null;
        try {
            newTradeRealRightData = super.checkTradeRelaHaveRightData(tradeRealRightData);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return newTradeRealRightData;
    }

    /**
     * 判断接口导入数据中有没有要处理的权益信息数据
     * @param alRightEquitys ArrayList 保存权益数据的集合
     * @param sTradeType String 交易类型
     * @param sDsType String 操作类型 界面上输入：其他数据为：'HD_JK' FDataSouce=0，权益处理数据:'HD_QY' FDataSouce=0
     * 接口：读入其他数据'ZD_JK'   FDataSource=1 ，权益处理数据： 'ZD_QY'  FDataSource=1
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @param sSecurityCode String 证券代码
     * @return ArrayList 返回值
     * @throws YssException
     */
    public ArrayList checkSubTradeHaveRightData(ArrayList alRightEquitys, String sTradeType, String sDsType,
                                                java.util.Date dDate, String sPortCode, String sSecurityCode) throws YssException {
        ArrayList newAlRightEquity = null;
        try {
            newAlRightEquity = super.checkSubTradeHaveRightData(alRightEquitys, sTradeType, sDsType, dDate, sPortCode, sSecurityCode);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return newAlRightEquity;
    }

    /**
     * 删除条件,交易主表和交易子表数据
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return TradeBean 返回值
     */
    public TradeBean filterBean(java.util.Date dDate, String sPortCode) {
        TradeBean trade = new TradeBean(); //创建对象
        trade.setTradeCode(YssOperCons.YSS_JYLX_XJDJ); //交易方式为现金对价
        trade.setPortCode(sPortCode); //组合代码
        trade.setBargainDate(YssFun.formatDate(dDate)); //成交日期
        trade.setDsType("HD_QY"); //操作类型，表示此数据是界面输入的数据
        return trade;
    }

    /**
     * 删除条件，删除交易关联表中数据
     * @param sPortCode String 组合代码
     * @return TradeRelaBean 返回值
     */
    public TradeRelaBean filterBean(String sPortCode) throws YssException {
        TradeRelaBean tradeReal = new TradeRelaBean(); //创建对象
        tradeReal.setSPortCode(sPortCode); //组合代码
        tradeReal.setSRelaType(YssOperCons.YSS_JYLX_XJDJ); //关联类型为现金对价
        return tradeReal;
    }

    /**
     * 此方法做现金对价数据的预处理，把处理数据保存到临时表Tb_Pub_Data_PreCashConsider
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @throws YssException 异常
     */
    private void doDataPretreatment(java.util.Date dDate, String sPortCode) throws YssException {
        StringBuffer buff = null; //拼接sql语句
        ResultSet rs = null; //声明结果集
        CashConsiderationBean cashConsideration = null; //现金对价的javaBean
        ArrayList cashConsiderationData = new ArrayList(); //保存预处理后的现金对价权益数据
        try {
            createTmpTable(); //创建现金对价临时表

        	//---add by songjie 2012.12.19 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        	String strSql = " truncate table " + pub.yssGetTableName("Tb_Data_PreCashConsider");
        	dbl.executeSql(strSql);
        	//---add by songjie 2012.12.19 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        	
            /**
             * 以下sql语句处理现金对价权益数据，有组合群和组合代码
             */
            buff = new StringBuffer();
            buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_CashConsider")); //现金对价权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB())); //组合群代码
            buff.append(" and FPortCode =").append(dbl.sqlString(sPortCode)); //组合代码

            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                cashConsideration = new CashConsiderationBean();
                cashConsideration.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                cashConsideration.setSecurityCode(rs.getString("FSecurityCode")); //证券代码
                cashConsideration.setRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                cashConsideration.setExRightDate(rs.getDate("FExRightDate").toString()); //除权日
                cashConsideration.setPayDate(rs.getDate("FPayDate").toString()); //到帐日
                cashConsideration.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                cashConsideration.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                cashConsideration.setPortCode(rs.getString("FPortCode")); //组合代码
                cashConsideration.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                cashConsideration.setDesc(rs.getString("FDesc")); //描述

                cashConsiderationData.add(cashConsideration); //把数据保存到集合中
            }
            saveIntoTmpTable(cashConsiderationData); //保存数据到现金对价临时表
            dbl.closeResultSetFinal(rs); //关闭游标
            cashConsiderationData.clear(); //清空集合

            /**
             * 以下sql语句处理现金对价权益数据，有组合群,但没有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_CashConsider")); //现金对价权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB())); //组合群代码
            buff.append(" and FPortCode =").append(dbl.sqlString(" ")); //组合代码
            buff.append(sSecurityCode.trim().length() != 0 ? " and FSecurityCode not in(" + this.operSql.sqlCodes(sSecurityCode) + ")" : ""); //证券代码

            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                cashConsideration = new CashConsiderationBean();
                cashConsideration.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                cashConsideration.setSecurityCode(rs.getString("FSecurityCode")); //证券代码
                cashConsideration.setRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                cashConsideration.setExRightDate(rs.getDate("FExRightDate").toString()); //除权日
                cashConsideration.setPayDate(rs.getDate("FPayDate").toString()); //到帐日
                cashConsideration.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                cashConsideration.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                cashConsideration.setPortCode(sPortCode); //组合代码
                cashConsideration.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                cashConsideration.setDesc(rs.getString("FDesc")); //描述

                cashConsiderationData.add(cashConsideration); //把数据保存到集合中
            }
            saveIntoTmpTable(cashConsiderationData); //保存数据到现金对价临时表
            dbl.closeResultSetFinal(rs); //关闭游标
            cashConsiderationData.clear(); //清空集合

            /**
             * 以下sql语句处理现金对价权益数据，没有组合群,但没有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_CashConsider")); //现金对价权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(" ")); //组合群代码
            buff.append(" and FPortCode =").append(dbl.sqlString(" ")); //组合代码
            buff.append(sSecurityCode.trim().length() != 0 ? " and FSecurityCode not in(" + this.operSql.sqlCodes(sSecurityCode) + ")" : ""); //证券代码

            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                cashConsideration = new CashConsiderationBean();
                cashConsideration.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ","; //把获取的证券代码拼接起来，作为下面的sql语句条件

                cashConsideration.setSecurityCode(rs.getString("FSecurityCode")); //证券代码
                cashConsideration.setRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                cashConsideration.setExRightDate(rs.getDate("FExRightDate").toString()); //除权日
                cashConsideration.setPayDate(rs.getDate("FPayDate").toString()); //到帐日
                cashConsideration.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                cashConsideration.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                cashConsideration.setPortCode(sPortCode); //组合代码
                cashConsideration.setAssetGroupCode(pub.getPrefixTB()); //组合群代码
                cashConsideration.setDesc(rs.getString("FDesc")); //描述

                cashConsiderationData.add(cashConsideration); //把数据保存到集合中
            }
            saveIntoTmpTable(cashConsiderationData); //保存数据到现金对价临时表

        } catch (Exception e) {
            throw new YssException("现金对价数据的预处理，把处理数据保存到临时表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 保存数据到现金对价临时表
     * @param cashConsiderationData ArrayList 保存数据的集合
     * @throws YssException
     */
    private void saveIntoTmpTable(ArrayList cashConsiderationData) throws YssException {
        StringBuffer buff = null; //拼接SQL语句
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement pst = null;
        //=============end====================
        CashConsiderationBean cashConsideration = null; //声明
        try {
            buff = new StringBuffer();
            buff.append(" insert into ").append(pub.yssGetTableName("Tb_Data_PreCashConsider")); //现金对价权益预处理表
            buff.append(" (");
            buff.append("FSecurityCode,FRecordDate,FExRightDate,FPayDate,");
            buff.append("FPreTaxRatio,FAfterTaxRatio,FPortCode,FAssetGroupCode,FDesc,");
            buff.append("FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime");
            buff.append(")");
            buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			//modified by liubo.Story #2145
			//==============================
//            pst = dbl.getPreparedStatement(buff.toString());
            pst = dbl.getYssPreparedStatement(buff.toString());
			//==============end================
            for (int i = 0; i < cashConsiderationData.size(); i++) { //循环保存数据的集合
                cashConsideration = (CashConsiderationBean) cashConsiderationData.get(i); //获取实例bean
                pst.setString(1, cashConsideration.getSecurityCode()); //证券代码
                pst.setDate(2, YssFun.toSqlDate(cashConsideration.getRecordDate())); //权益确认日
                pst.setDate(3, YssFun.toSqlDate(cashConsideration.getExRightDate())); //除权日
                pst.setDate(4, YssFun.toSqlDate(cashConsideration.getPayDate())); //到帐日
                pst.setDouble(5, Double.parseDouble(cashConsideration.getPreTaxRatio())); //税前权益比例
                pst.setDouble(6, Double.parseDouble(cashConsideration.getAfterTaxRatio())); //税后权益比例
                pst.setString(7, cashConsideration.getPortCode()); //组合代码
                pst.setString(8, cashConsideration.getAssetGroupCode()); //组合群代码
                pst.setString(9, cashConsideration.getDesc()); //描述
                pst.setInt(10, 1); //审核状态
                pst.setString(11, pub.getUserCode()); //创建人
                pst.setString(12, YssFun.formatDatetime(new java.util.Date())); //创建时间
                pst.setString(13, pub.getUserCode()); //审核人
                pst.setString(14, YssFun.formatDatetime(new java.util.Date())); //审核时间

                pst.executeUpdate();
            }
        } catch (Exception e) {
            throw new YssException("保存数据到现金对价临时表出错！", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }

    /**
     * createTmpTable创建现金对价临时表 Tb_Pub_Data_PreCashConsider
     */
    private void createTmpTable() throws YssException {
        StringBuffer buff = null; //用于拼接sql语句
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        String strSql = "";
        ResultSet rs = null;
        String duration = "";//表类型
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
        	//---edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("Tb_Data_PreCashConsider"));
            buff.append(" ( ");
            buff.append(" FSecurityCode   VARCHAR(50)   NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FExRightDate    DATE          NOT NULL,");
            buff.append(" FPayDate        DATE          NOT NULL,");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("Tb_Data_PreCashConsider"));
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("Tb_Data_PreCashConsider".toUpperCase()));
        	
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Data_PreCashConsider"))) { 
        				/**shashijie ,2011-10-12 , STORY 1698*/
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("Tb_Data_PreCashConsider")));
        				/**end*/
        			}

                    dbl.executeSql(buff.toString());
        		}
        	}else{
        		dbl.executeSql(buff.toString());
        	}
        	
        	buff.delete(0, buff.length());
        	//---edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        } catch (Exception e) {
            throw new YssException("创建现金对价临时表出错！", e);
        } 
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
    }

    /**
     * 外面类获取保存交易关联数据集合的方法
     * @return ArrayList
     */
    public ArrayList getTradeRealRightData() {
        return this.tradeRealRightData;
    }
}
