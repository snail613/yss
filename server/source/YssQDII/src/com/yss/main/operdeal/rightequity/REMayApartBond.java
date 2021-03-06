package com.yss.main.operdeal.rightequity;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.YssPreparedStatement;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.linkInfo.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.parasetting.*;
import com.yss.main.storagemanage.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.manager.TradeDataAdmin;
import com.yss.manager.TradeRelaDataAdmin;

/**
 * <p>Title: xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理</p>
 *
 * <p>Description: 可分离债送配权益处理</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class REMayApartBond extends BaseRightEquity{
    private String sSecurityCode="";//保存证券代码
    private ArrayList tradeRealRightData=new ArrayList();//保存交易关联表中数据
    private String sAllDeleteNum="";//保存交易编号
    private String tSecurityCode = ""; //保存送配证券代码
    
    public REMayApartBond() {
    }

    /**
     * 做可分离债送配权益业务处理，产生业务资料数据保存到业务资料javaBean中
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return ArrayList 返回保存交易子表bean数据的集合
     * @throws YssException 异常
     */
    public ArrayList getDayRightEquitys(java.util.Date dDate, String sPortCode) throws
        YssException {
        StringBuffer buff = null; //sql语句的拼接
        ResultSet rs = null;
        double dSecurityAmount = 0; //证券数量
        double dSecurityCost = 0; //证券成本
        double dRight = 0; //权益（主表）
        double dRightSub = 0; //权益（子表）
        String strRightType = ""; //权益类型
        String strSubRightType = ""; //权益类型
        String strCashAccCode = " "; //现金帐户
        String strYearMonth = ""; //保存截取日期的年和天
        CashAccountBean caBean = null; //声明现金账户的bean
        double dBaseRate = 1; //基础汇率
        double dPortRate = 1; //组合汇率
        boolean analy1; //分析代码1
        boolean analy2; //分析代码2
        boolean analy3; //分析代码3
        TradeSubBean subTrade = null; //交易子表的javaBean
        TradeRelaBean tradeReal=null;//交易关联表的javaBean
        YssCost cost = null; //声明成本
        SecurityStorageBean secSto = null; //证券库存的javaBean
        ArrayList reArr = new ArrayList();
        CashAccLinkBean cashAccLink = null; //声明现金账户链接
        ArrayList linkList = null;
        long sNum = 0; //为了产生的编号不重复
        double dWarrantCost=0;//权证成本
        Date StorageDate = null; //MS01233  QDV4赢时胜(上海)2010年06月03日01_A add by jiangshichao
        try {
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B start---//
        	TradeDataAdmin tradeData = new TradeDataAdmin();//交易数据操作类
        	tradeData.setYssPub(pub);
        	tradeData.delete("", dDate,dDate,null,null,"",sPortCode, "", 
        			"",YssOperCons.YSS_JYLX_KFLSP, "", "",false, "HD_QY");
        	
        	String strSql = " delete from " + pub.yssGetTableName("Tb_Data_TradeRela") + 
        	" where FRelaType = " + dbl.sqlString(YssOperCons.YSS_JYLX_KFLSP) +
        	" and FNum like 'T" + YssFun.formatDate(dDate, "yyyyMMdd") + "%'" + 
        	" and FPortCode in (" + operSql.sqlCodes(sPortCode) + ")";
            dbl.executeSql(strSql);
        	
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B end---//
        	
            this.doDataPretreatment(dDate, sPortCode); //可分离债送配权益数据的预处理，主要是考虑跨组合群，组合的处理

            buff = new StringBuffer();
            BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.getOperDealCtx().getBean("cashacclinkdeal"); //账户链接
            operFun.setYssPub(pub);//设置PUB
            strYearMonth = YssFun.left(YssFun.formatDate(dDate), 4) + "00"; //赋值
            //YssType lAmount = new YssType();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security"); //判断是否有分析代码
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            strRightType = YssOperCons.YSS_JYLX_KFLSP; //权益类型为可分离债送配 strRightType = "84";
            strDealInfo = "no";
            //操作子表
            //------ modify by wangzuochun 2010.07.12  MS01417    根据权益信息中的证券信息去查找证券信息维护中的交易所    QDV4上海2010年07月07日01_B    
            buff.append("select a.*, b.*,ma1.FClosingPrice as bondMarketPrice,ma2.FClosingPrice as warrantMPrice,c.FTradeCury,x.FEXCHANGECODE,d.FPortCury from");
            buff.append("( select FSecurityCode as FSecurityCode1,FTSecurityCode,FRecordDate,FExRightDate,FAccountType,FPreTaxRatio,FAfterTaxRatio from ");
            buff.append(pub.yssGetTableName("Tb_Data_PreMayApartBond")); //从可分离债送配权益预处理表中获取权益数据
            buff.append(" where FExRightDate = ").append(dbl.sqlDate(dDate)); //权益处理时取除权日数据，做权益确认日处理
            buff.append(" and FCheckState = 1) a ");
            //--- MS01233  QDV4赢时胜(上海)2010年06月03日01_A 添加字段FEXCHANGECODE用于判断交易市场是国内市场还是国外市场  add by jiangshichao  2010.06.07
            buff.append(" left join (select FSecurityCode,FEXCHANGECODE from ");
            buff.append(pub.yssGetTableName("Tb_Para_Security")); //关联证券信息表
            buff.append(" where FCheckState = 1 ) x on a.fsecuritycode1 = x.FSecurityCode");
            buff.append(" join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Stock_Security")); //关联证券库存表
            buff.append(" where FPortCode in (" + operSql.sqlCodes(sPortCode)); //组合代码
            buff.append(")");
            buff.append(" and FYearMonth<>").append(dbl.sqlString(strYearMonth)); //不是期初数库存
            buff.append(" and FCheckState=1 )b  on a.fsecuritycode1 = b.fsecuritycode ");
            buff.append(" and (case when x.fexchangecode in ('CY', 'CS', 'CG') then a.FRecordDate else a.FExRightDate - 1 end) = b.FStorageDate "); //取权益确认日库存
            buff.append(" left join (select FSecurityCode,FTradeCury from ");
            buff.append(pub.yssGetTableName("Tb_Para_Security")); //关联证券信息表
            buff.append(" where FCheckState = 1 ) c on a.FTSecurityCode = c.FSecurityCode");
            buff.append(" left join (select FPortCode,FPortCury from ");
            buff.append(pub.yssGetTableName("Tb_Para_Portfolio")); //关联组合信息表
            buff.append(" where FCheckState = 1 ) d on b.FPortCode = d.FPortCode"); //关取证券信息表和组合表，取出交易货币和组合货币。
            buff.append(" left join (select m2.* from (select max(FMktValueDate) as FMktValueDate,FSecurityCode from ");
            buff.append(pub.yssGetTableName("tb_data_marketvalue"));//行情表
            buff.append(" where FCheckState = 1");
            buff.append(" and FMktValueDate <=").append(dbl.sqlDate(dDate));
            buff.append(" group by FSecurityCode) m1 left join (select * from ");
            buff.append(pub.yssGetTableName("tb_data_marketvalue"));//行情表
            buff.append(" where FCheckState = 1");
            buff.append(" ) m2 on m1.FSecurityCode = m2.FSecurityCode and m1.FMktValueDate = m2.FMktValueDate");
            buff.append(" ) ma1 on a.FSecurityCode1 = ma1.FSecurityCode");//条件是取可分离债券的收盘价
            buff.append(" left join (select m3.* from (select max(FMktValueDate) as FMktValueDate,FSecurityCode from ");
            buff.append(pub.yssGetTableName("tb_data_marketvalue"));//行情表
            buff.append(" where FCheckState = 1");
            buff.append(" and FMktValueDate <=").append(dbl.sqlDate(dDate));
            buff.append(" group by FSecurityCode) m4 left join (select * from ");
            buff.append(pub.yssGetTableName("tb_data_marketvalue"));//行情表
            buff.append(" where FCheckState = 1");
            buff.append(" ) m3 on m4.FSecurityCode = m3.FSecurityCode and m4.FMktValueDate = m3.FMktValueDate");
            buff.append(" ) ma2 on a.FTSecurityCode = ma2.FSecurityCode");//条件是可分离债送配权益临时表权证代码关联行情表证券代码取权证的收盘价
            
            //--------------------------------MS01417-----------------------------------//
            rs = dbl.queryByPreparedStatement(buff.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            buff.delete(0, buff.length());

            if (rs.next()) {
                rs.beforeFirst(); //返回第一个rs，此方法要求打开游标类型为 TYPE_SCROLL_INSENSITIVE
                //--------------------拼接交易编号---------------------
                String strNumDate = YssFun.formatDatetime(dDate).
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
                //---------------------------end-------------------------//
                while (rs.next()) {
                    //-------------------------设置现金账户链接属性值----------------------
                    cashacc.setYssPub(pub);
                    cashacc.setLinkParaAttr( (analy1 ?
                                              rs.getString("FAnalysisCode1") :
                                              " "), //投资经理
                                            rs.getString("FPortCode"), //组合代码
                                            rs.getString("FTSecurityCode"), //目标证券代码
                                            (analy2 ?
                                             rs.getString("FAnalysisCode2") :
                                             " "), //券商
                                            strRightType, //权益类型为可分离送配
                                            rs.getDate("FRecordDate")); //权益确认日
                    //-------------------------end-----------------------------------------//
                    subTrade = new TradeSubBean(); //实例化
                    tradeReal=new TradeRelaBean();//实例化
                    secSto = new SecurityStorageBean(); //实例化
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
                    secSto = secSto.getStorageCost(StorageDate, //权益确认日
                        rs.getString("FSecurityCode1"), //证券代码
                        rs.getString("FPortCode"), //组合代码
                        (analy1 ?
                         rs.getString("FAnalysisCode1") :
                         " "), //分析代码1
                        (analy2 ?
                         rs.getString("FAnalysisCode2") :
                         " "), //分析代码2
                        "", "C",
                        rs.getString("FAttrClsCode")); //"C"为获取 核算成本
                    if (secSto != null) {
                        dSecurityCost = YssFun.toDouble(secSto.getStrStorageCost()); //为汇总的核算成本赋值
                        dSecurityAmount = YssFun.toDouble(secSto.getStrStorageAmount()); //为汇总的库存数量赋值
                    } else {
                        dSecurityCost = 0.0; //为汇总的核算成本赋值
                        dSecurityAmount = 0.0; //为汇总的库存数量赋值
                    }
                    CtlPubPara pubPara = new CtlPubPara(); //通用参数实例化
                    pubPara.setYssPub(pub); //设置Pub
                    String rightsRatioMethods = (String) pubPara.getRightsRatioMethods(rs.getString("FPortCode")); //获取通用参数值
                    String ratioMethodsDetail = pubPara.getBRightsRatioMethods(rs.getString("FPortCode"),YssOperCons.YSS_JYLX_KFLSP);//按权益类型获取权益比例方式 panjunfang add 20100510 B股业务
                    if(ratioMethodsDetail.length() > 0){
                    	rightsRatioMethods = ratioMethodsDetail;
                    }
                    if (dSecurityAmount > 0) { //判断证券数量是否大于0
                        //可分离债送配权益数量=确认日库存数量*权益比例
                        dRightSub = YssD.mul(dSecurityAmount,
                                     (rightsRatioMethods.equalsIgnoreCase("PreTaxRatio") ?
                                      rs.getDouble("FPreTaxRatio") : rs.getDouble("FAfterTaxRatio"))); //通过通用参数获取权益比例方式
                        caBean = cashacc.getCashAccountBean();
                        if (caBean != null) {
                            strCashAccCode = caBean.getStrCashAcctCode(); //现金账户
                        } else {
                            throw new YssException("系统执行可分离债送配权益时出现异常！" + "\n" + "【" +
                                rs.getString("FTSecurityCode") +
                                "】证券可分离债送配权益处理时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
                        }
                        /**权证成本其处理方式有两种：一种是可分离债送配权证确认投资而不冲减债券成本，一种是可分离债送配权证冲减债券成本，
                         方式1(不冲减成本)计算方式为--‘0’不冲减债券成本
                         方式2（冲减成本）计算方式为-- ‘1’冲减且按净价计算，‘2’冲减且按全价计，
                         1）(按净价计算权证成本)、权证成本=可分离债认购成本*【每张债券对应数量权证的公允价值/
                           （每张债券的净价公允价值+每张债券对应数量权证的公允价值）】
                         2）（按全价计算权证成本）、权证成本=可分离债认购成本*【每张债券对应数量权证的公允价值/
                            （每张债券的全价公允价值+每张债券对应数量权证的公允价值）】
                         可分离债认购成本：债券数量（既权益确认日当天库存数量dSecurityAmount）*100
                         每张债券对应数量权证的公允价值：权证价格（当天权证的收市价warrantMPrice）*送配的权证数量（dRightSub）
                         每张债券的净价公允价值：债券价格（行情的收市价（为净价bondMarketPrice））*债券的数量（dSecurityAmount）
                         每张债券的全价公允价值：债券价格（行情的收市价（为全价bondMarketPrice））*债券的数量（dSecurityAmount）
                         权证成本保留两位
                         **/
                        double dMayABondCost=YssD.mul(dSecurityAmount,100);//可分离债认购成本
                        double dWarrantOpenValue=YssD.mul(rs.getDouble("warrantMPrice"),dRightSub);//每张债券对应数量权证的公允价值
                        double dBondOpenValue=YssD.mul(rs.getDouble("bondMarketPrice"),dSecurityAmount);// 每张债券的公允价值
                        if (rs.getInt("FAccountType") != 0) {//对于净价还是全价，由用户自己录入行情数据，只要计算方式不为--不冲减债券成本，就用以下计算方式
                             dWarrantCost =YssD.round(YssD.div(YssD.mul(dMayABondCost, dWarrantOpenValue),//权证成本，计算方法如上注释详解
                                YssD.add(dWarrantOpenValue, dBondOpenValue)),2);//保留两位

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
                            getDate("FExRightDate"), //除权日
                            rs.getString("FTradeCury"), //交易币种
                            rs.getString("FPortCode"), //组合代码
                            YssOperCons.YSS_RATE_BASE); //获取基础汇率的值
                        dPortRate = this.getSettingOper().getCuryRate(rs.
                            getDate("FExRightDate"),
                            rs.getString("FPortCury"),
                            rs.getString("FPortCode"),
                            YssOperCons.YSS_RATE_PORT); //获取组合汇率的值
                        /*------------------------------以下为交易子表实体bean赋值-------------------------*/

                        sAllDeleteNum+=strNumDate+",";

                        subTrade.setNum(strNumDate); //为交易编号赋值

                        subTrade.setSecurityCode(rs.getString("FTSecurityCode")); //权证代码赋值

                        tSecurityCode += rs.getString("FTSecurityCode") + ",";//赋值

                        subTrade.setPortCode(rs.getString("FPortCode")); //组合代码
                        
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

                        subTrade.setTradeCode(strRightType); //交易类型

                        subTrade.setTailPortCode(strCashAccCode); //尾差组合代码

                        subTrade.setAllotProportion(0); //分配比例

                        subTrade.setOldAllotAmount(dRightSub); //原始分配数量和交易数量相同

                        subTrade.setAllotFactor(0); //分配因子

                        subTrade.setBargainDate(YssFun.formatDate(rs.getDate(
                            "FExRightDate"))); //成交日期

                        subTrade.setBargainTime("00:00:00"); //成交时间

                        subTrade.setSettleDate(YssFun.formatDate(rs.getDate(
                            "FExRightDate"))); //结算日期

                        subTrade.setMatureDate("9998-12-31");//到期日期

                        subTrade.setMatureSettleDate("9998-12-31");//到期结算日期

                        subTrade.setSettleState(new Integer(0).toString());//结算状态，未结算“0”

                        subTrade.setSettleTime("00:00:00"); //结算时间

                        subTrade.setAutoSettle(new Integer(1).toString()); //自动结算

                        subTrade.setPortCuryRate(dPortRate); //组合汇率

                        subTrade.setBaseCuryRate(dBaseRate); //基础汇率

                        subTrade.setTradeAmount(dRightSub); //交易数量

                        if (rs.getInt("FAccountType") == 0) {//计算方式为--不冲减债券成本
                            subTrade.setTradePrice(0); //交易价格

                            subTrade.setTradeMoney(0); //交易金额
                        }else{ //计算方式为--冲减且按净价计算或者为冲减且按全价计算，不同点是，用户输入的行情是净价还是全价
                            subTrade.setTradePrice(YssD.div(dWarrantCost,dRightSub)); //交易价格=交易金额/交易数量

                            subTrade.setTradeMoney(dWarrantCost); //交易金额
                        }
                        subTrade.setAccruedInterest(0); //应计利息

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
                        subTrade.setCost(cost); //成本
                        //---------------------end-----------------//
                        subTrade.setDataSource(0); //数据源

                        subTrade.setDsType("HD_QY");//操作类型，表示系统操作数据，主要是和接口导入数据进行区分

                        subTrade.checkStateId = 1; //审核状态

                        subTrade.creatorCode = pub.getUserCode(); //创建人

                        subTrade.checkTime = YssFun.formatDatetime(new java.util.Date()); //审核时间

                        subTrade.checkUserCode = pub.getUserCode(); //审核人

                        subTrade.creatorTime = YssFun.formatDatetime(new java.util.Date()); //创建时间

                        subTrade.setTotalCost(0); //投资总成本

                        subTrade.setFactSettleDate(YssFun.formatDate(rs.getDate(
                            "FExRightDate"))); //实际结算日期

                        subTrade.setFactCashAccCode(strCashAccCode); //实际结算帐户

                        subTrade.setCashAcctCode(strCashAccCode); //设置现金账户

                        subTrade.setFactSettleMoney(0); //实际结算金额

                        subTrade.setExRate(1); //兑换汇率

                        subTrade.setFactPortRate(dPortRate); //实际结算组合汇率

                        subTrade.setFactBaseRate(dBaseRate); //实际结算基础汇率

                        reArr.add(subTrade); //把交易子表数据保存到集合中

                        /*---------------------------以下为交易关联bean赋值-------------------------*/
                        if(rs.getInt("FAccountType") != 0){//对于计算方式为--不冲减债券成本，不产生交易关联数据
                            tradeReal.setSNum(strNumDate); //交易编号

                            tradeReal.setSRelaType(strRightType); //关联类型

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

                            tradeReal.setSSecurityCode(rs.getString("FSecurityCode1")); //可分离债代码

                            tradeReal.setDAmount(0);

                            tradeReal.setIInOut(-1);//设置成本流入、流出方向为-流出

                            //--------------设置原币成本------------------
                            tradeReal.setDCost(dWarrantCost); //核算成本

                            tradeReal.setDMCost(dWarrantCost); //管理成本

                            tradeReal.setDVCost(dWarrantCost); //估值成本
                            //---------------end------------------------

                            //---------------设置基础货币成本---------------
                            tradeReal.setDBaseCuryCost(YssD.mul(dWarrantCost,dBaseRate)); //基础货币核算成本

                            tradeReal.setDMBaseCuryCost(YssD.mul(dWarrantCost,dBaseRate)); //基础货币管理成本

                            tradeReal.setDVBaseCuryCost(YssD.mul(dWarrantCost,dBaseRate)); //基础货币估值成本
                            //---------------end--------------------------

                            //---------------设置组合货币成本----------------
                            tradeReal.setDPortCuryCost(YssD.div(YssD.mul(dWarrantCost,dPortRate),dPortRate)); //组合货币核算成本

                            tradeReal.setDMPortCuryCost(YssD.div(YssD.mul(dWarrantCost,dPortRate),dPortRate)); //组合货币管理成本

                            tradeReal.setDVPortCuryCost(YssD.div(YssD.mul(dWarrantCost,dPortRate),dPortRate)); //组合货币估值成本
                            //---------------end--------------------------
                            tradeReal.setSDesc(""); //描述

                            tradeReal.checkStateId = 1; //审核状态

                            tradeReal.creatorCode = pub.getUserCode(); //创建人

                            tradeReal.creatorTime = YssFun.formatDatetime(new java.util.Date()); //创建时间

                            tradeReal.checkUserCode = pub.getUserCode(); //审核人

                            tradeReal.checkTime = YssFun.formatDatetime(new java.util.Date()); //审核时间

                            tradeRealRightData.add(tradeReal); //把交易关联数据保存到集合中
                        }
                    }
                }
                strDealInfo = "true"; //表示有权益数据
            } else {
                strDealInfo = "no"; //表示无权益数据
            }
            return reArr;
        } catch (Exception e) {
            strDealInfo = "false";
            throw new YssException("计算可分离债送配权益处理出错！",e);
        } finally {
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
    public void saveRightEquitys(ArrayList alRightEquitys,ArrayList tradeRealRightData, java.util.Date dDate, String sPortCode) throws YssException {
        ArrayList newAlRightEquity = null;
        ArrayList newTradeRealRightData=null;
        try {
            if (alRightEquitys != null && alRightEquitys.size() > 0) {
            	 // EDIT by lidaolong 20110407 #536 有关国内接口数据处理顺序的变更 
                newAlRightEquity = checkSubTradeHaveRightData(alRightEquitys, YssOperCons.YSS_JYLX_KFLSP, "ZD_QY,ZD_QY_T+1", dDate, sPortCode, this.tSecurityCode);
            }
            if(tradeRealRightData!=null&&tradeRealRightData.size()>0){
                newTradeRealRightData=checkTradeRelaHaveRightData(tradeRealRightData);
            }
            super.saveRightEquitys(newAlRightEquity,newTradeRealRightData, dDate, sPortCode); //调用基类保存数据方法
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
     * @param tSecurityCode String 证券代码
     * @return ArrayList 返回值
     * @throws YssException
     */
    public ArrayList checkSubTradeHaveRightData(ArrayList alRightEquitys, String sTradeType, String sDsType,
                                                java.util.Date dDate, String sPortCode, String tSecurityCode) throws YssException {
        ArrayList newAlRightEquity = null;
        try {
            newAlRightEquity = super.checkSubTradeHaveRightData(alRightEquitys, sTradeType, sDsType, dDate, sPortCode, tSecurityCode);
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
        TradeBean trade = new TradeBean();//创建对象
        trade.setTradeCode(YssOperCons.YSS_JYLX_KFLSP); //交易方式为可分离债送配
        trade.setPortCode(sPortCode); //组合代码
        trade.setBargainDate(YssFun.formatDate(dDate)); //成交日期
        trade.setDsType("HD_QY");//操作类型，表示此数据时界面输入的数据
        return trade;
    }

    /**
     * 删除条件，删除交易关联表中数据
     * @param sPortCode String 组合代码
     * @return TradeRelaBean 返回值
     */
    public TradeRelaBean filterBean(String sPortCode) throws YssException {
       TradeRelaBean tradeReal=new TradeRelaBean();//创建对象
       tradeReal.setSPortCode(sPortCode);//组合代码
       tradeReal.setSRelaType(YssOperCons.YSS_JYLX_KFLSP);//关联类型为可分离债送配
       return tradeReal;
    }


    /**
     * 此方法做可分离债送配数据的预处理，把处理数据保存到临时表tb_pub_data_Predividend
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @throws YssException 异常
     */
    private void doDataPretreatment(java.util.Date dDate, String sPortCode) throws YssException {
        StringBuffer buff = null; //拼接sql语句
        ResultSet rs = null; //声明结果集
        MayApartBondBean mayApartBond = null; //可分离债送配的javaBean
        ArrayList mayApartBondData = new ArrayList(); //保存预处理后的可分离债送配权益数据
        try {
        	createTmpTable(); //创建可分离债送配临时表

        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        	String strSql = " truncate table " + pub.yssGetTableName("Tb_Data_PreMayApartBond");
        	dbl.executeSql(strSql);
        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            
            /**
             * 以下sql语句处理可分离债送配权益数据，有组合群和组合代码
             */
            buff = new StringBuffer();
            buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_MayApartBond")); //可分离债送配权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB())); //组合群代码
            buff.append(" and FPortCode =").append(dbl.sqlString(sPortCode)); //组合代码

            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                mayApartBond = new MayApartBondBean();
                mayApartBond.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ","; //把获取的证券代码拼接起来，作为下面的sql语句条件

                mayApartBond.setSecurityCode(rs.getString("FSecurityCode")); //证券代码
                mayApartBond.setTSecurityCode(rs.getString("FTSecurityCode"));//权证代码
                mayApartBond.setRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                mayApartBond.setExRightDate(rs.getDate("FExRightDate").toString()); //除权日
                mayApartBond.setAccountType(Integer.toString(rs.getInt("FAccountType")));//计算方式
                mayApartBond.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                mayApartBond.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                mayApartBond.setPortCode(rs.getString("FPortCode")); //组合代码
                mayApartBond.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                mayApartBond.setDesc(rs.getString("FDesc")); //描述
                mayApartBondData.add(mayApartBond); //把数据保存到集合中
            }
            saveIntoTmpTable(mayApartBondData); //保存数据到可分离债送配临时表
            dbl.closeResultSetFinal(rs); //关闭游标
            mayApartBondData.clear(); //清空集合

            /**
             * 以下sql语句处理可分离债送配权益数据，有组合群,但没有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_MayApartBond")); //可分离债送配权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB())); //组合群代码
            buff.append(" and FPortCode =").append(dbl.sqlString(" ")); //组合代码
            buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":"");//证券代码

            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                mayApartBond = new MayApartBondBean();
                mayApartBond.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                mayApartBond.setSecurityCode(rs.getString("FSecurityCode")); //证券代码
                mayApartBond.setTSecurityCode(rs.getString("FTSecurityCode"));//权证代码
                mayApartBond.setRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                mayApartBond.setExRightDate(rs.getDate("FExRightDate").toString()); //除权日
                mayApartBond.setAccountType(Integer.toString(rs.getInt("FAccountType")));//计算方式
                mayApartBond.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                mayApartBond.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                mayApartBond.setPortCode(sPortCode); //组合代码
                mayApartBond.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                mayApartBond.setDesc(rs.getString("FDesc")); //描述
                mayApartBondData.add(mayApartBond); //把数据保存到集合中
            }
            saveIntoTmpTable(mayApartBondData); //保存数据到可分离债送配临时表
            dbl.closeResultSetFinal(rs); //关闭游标
            mayApartBondData.clear(); //清空集合

            /**
             * 以下sql语句处理可分离债送配权益数据，没有组合群,但没有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_MayApartBond")); //可分离债送配权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(" ")); //组合群代码
            buff.append(" and FPortCode =").append(dbl.sqlString(" ")); //组合代码
            buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":""); //证券代码

            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                mayApartBond = new MayApartBondBean();
                mayApartBond.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                mayApartBond.setSecurityCode(rs.getString("FSecurityCode")); //证券代码
                mayApartBond.setTSecurityCode(rs.getString("FTSecurityCode"));//权证代码
                mayApartBond.setRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                mayApartBond.setExRightDate(rs.getDate("FExRightDate").toString()); //除权日
                mayApartBond.setAccountType(Integer.toString(rs.getInt("FAccountType")));//计算方式
                mayApartBond.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                mayApartBond.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                mayApartBond.setPortCode(sPortCode); //组合代码
                mayApartBond.setAssetGroupCode(pub.getPrefixTB()); //组合群代码
                mayApartBond.setDesc(rs.getString("FDesc")); //描述
                mayApartBondData.add(mayApartBond); //把数据保存到集合中
            }
            saveIntoTmpTable(mayApartBondData); //保存数据到可分离债送配临时表

        } catch (Exception e) {
            throw new YssException("可分离债送配数据的预处理，把处理数据保存到临时表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 保存数据到可分离债送配临时表
     * @param mayApartBondData ArrayList 保存数据的集合
     * @throws YssException
     */
    private void saveIntoTmpTable(ArrayList mayApartBondData) throws YssException {
        StringBuffer buff = null;//拼接SQL语句
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement  pst = null;
        //=============end====================
        MayApartBondBean mayApartBond = null;//声明
        try {
            buff = new StringBuffer();
            buff.append(" insert into ").append(pub.yssGetTableName("Tb_Data_PreMayApartBond")); //可分离债送配权益预处理表
            buff.append(" (");
            buff.append("FSecurityCode,FTSecurityCode,FRecordDate,FExRightDate,FAccountType,");
            buff.append("FPreTaxRatio,FAfterTaxRatio,FPortCode,FAssetGroupCode,FDesc,");
            buff.append("FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime");
            buff.append(")");
            buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			//modified by liubo.Story #2145
			//==============================
//            pst = dbl.getPreparedStatement(buff.toString());
            pst = dbl.getYssPreparedStatement(buff.toString());
			//==============end================
            for (int i = 0; i < mayApartBondData.size(); i++) { //循环保存数据的集合
                mayApartBond = (MayApartBondBean) mayApartBondData.get(i); //获取实例bean
                pst.setString(1, mayApartBond.getSecurityCode()); //证券代码
                pst.setString(2,mayApartBond.getTSecurityCode());//权证代码
                pst.setDate(3, YssFun.toSqlDate(mayApartBond.getRecordDate())); //权益确认日
                pst.setDate(4, YssFun.toSqlDate(mayApartBond.getExRightDate())); //除权日
                pst.setInt(5,Integer.parseInt(mayApartBond.getAccountType()));//计算方式
                pst.setDouble(6, Double.parseDouble(mayApartBond.getPreTaxRatio())); //税前权益比例
                pst.setDouble(7, Double.parseDouble(mayApartBond.getAfterTaxRatio())); //税后权益比例
                pst.setString(8, mayApartBond.getPortCode()); //组合代码
                pst.setString(9, mayApartBond.getAssetGroupCode()); //组合群代码
                pst.setString(10, mayApartBond.getDesc()); //描述
                pst.setInt(11, 1); //审核状态
                pst.setString(12, pub.getUserCode()); //创建人
                pst.setString(13, YssFun.formatDatetime(new java.util.Date())); //创建时间
                pst.setString(14, pub.getUserCode()); //审核人
                pst.setString(15, YssFun.formatDatetime(new java.util.Date())); //审核时间
                pst.executeUpdate();
            }
        } catch (Exception e) {
            throw new YssException("保存数据到可分离债送配临时表出错！", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }

    /**
     * createTmpTable创建可分离债送配临时表 Tb_Pub_Data_PreMayApartBond
     */
    private void createTmpTable() throws YssException {
        StringBuffer buff = null;
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        String strSql = "";
        ResultSet rs = null;
        String duration = "";//表类型
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
        	//---edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
            buff = new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("Tb_Data_PreMayApartBond"));
            buff.append(" ( ");
            buff.append(" FSecurityCode   VARCHAR(50)   NOT NULL, ");
            buff.append(" FTSecurityCode  VARCHAR(50)   NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FExRightDate    DATE          NOT NULL,");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FAccountType    NUMBER(2)     NOT NULL,");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("Tb_Data_PreMayApartBond"));
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE) ");
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("Tb_Data_PreMayApartBond".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Data_PreMayApartBond"))) { 
        				/**shashijie ,2011-10-12 , STORY 1698*/
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("Tb_Data_PreMayApartBond")));
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
            throw new YssException("创建可分离债送配临时表出错！", e);
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
