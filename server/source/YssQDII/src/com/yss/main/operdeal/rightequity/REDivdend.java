package com.yss.main.operdeal.rightequity;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.yss.dsub.YssPreparedStatement;
import com.yss.main.operdata.*;
import com.yss.main.operdata.overthecounter.pojo.OpenFundTradeBean;
import com.yss.main.parasetting.*;
import com.yss.main.storagemanage.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.manager.TradeDataAdmin;

/**
 *
 * <p>Title: xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理</p>
 * <p>Description:计算股票分红金额  </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */

public class REDivdend
    extends BaseRightEquity {
    private String sSecurityCode="";//保存证券代码　
    private String msg="";//MS01288 QDV4交银施罗德2010年6月10日01_A  add by jiangshichao 2010.07.02 
    
    public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public REDivdend() {
    }
    /**
     * 做分红权益业务处理，产生业务资料数据保存到业务资料javaBean中
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return ArrayList 返回值
     * @throws YssException 异常
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    public ArrayList getDayRightEquitys(java.util.Date dDate, String sPortCode) throws
        YssException {
        StringBuffer buff = null;//sql语句的拼接
        ResultSet rs = null;
        double dSecurityAmount = 0; //证券数量
        double dSecurityCost = 0; //证券成本
        double dRight = 0; //权益（主表）
        double dRightSub = 0; //权益（子表）
        String strRightType = ""; //权益类型
        String strSubRightType = ""; //权益类型
        String strCashAccCode = " "; //现金帐户
        String strYearMonth = "";//保存截取日期的年和天
        CashAccountBean caBean = null;//声明现金账户的bean
        double dBaseRate = 1;//基础汇率
        double dPortRate = 1;//组合汇率
        boolean analy1;//分析代码1
        boolean analy2;//分析代码2
        boolean analy3;//分析代码3
        TradeSubBean subTrade = null;//交易子表的javaBean
        YssCost cost = null;//声明成本
        SecurityStorageBean secSto = null;//证券库存的javaBean
        ArrayList reArr = new ArrayList();
        CashAccLinkBean cashAccLink = null;//声明现金账户链接
        ArrayList linkList = null;
        long sNum=0;//为了产生的编号不重复
        String strSecAttrCls="";//add by xuxuming,2010.01.15.保存所属分类代码
        Date StorageDate = null; //MS01233  QDV4赢时胜(上海)2010年06月03日01_A add by jiangshichao
        //add by zhangfa 20101223 1760 有关权益处理国内的分红数据的处理变更
        boolean bDivdendByIn=false;
        boolean bDistribute=false;
        //--------------------end 20101223 -----------------------------
        //--- add by songjie 2013.04.12 STORY #3365 需求北京-[中国银行]QDV4.0[中]20121203001 start---//
        double minCost = 0;
        double storageCost = 0;
        //--- add by songjie 2013.04.12 STORY #3365 需求北京-[中国银行]QDV4.0[中]20121203001 end---//
        try {
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B start---//
        	TradeDataAdmin tradeData = new TradeDataAdmin();//交易数据操作类
        	tradeData.setYssPub(pub);
        	tradeData.delete("", dDate,dDate,null,null,"",sPortCode, "", 
        			"",YssOperCons.YSS_JYLX_PX + "," + YssOperCons.YSS_JYLX_PXRC, "", "",false, "HD_QY");
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B end---//
        	
            this.doDataPretreatment(dDate,sPortCode);//分红权益数据的预处理，主要是考虑跨组合群，组合的处理

            buff = new StringBuffer();
            BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.
                getOperDealCtx().getBean("cashacclinkdeal");
            operFun.setYssPub(pub);
            strYearMonth = YssFun.left(YssFun.formatDate(dDate), 4) + "00";//赋值
            //YssType lAmount = new YssType();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");//判断是否有分析代码
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            strRightType = YssOperCons.YSS_JYLX_PX; //权益类型为分发派息 strRightType = "06";
            strDealInfo = "no";
            //操作子表
            //------ modify by wangzuochun 2010.07.12  MS01417    根据权益信息中的证券信息去查找证券信息维护中的交易所    QDV4上海2010年07月07日01_B    
            buff.append("select a.*, b.*,c.FTradeCury,c.FEXCHANGECODE,d.FPortCury from");
            buff.append("( select FSecurityCode as FSecurityCode1, FRecordDate, FDividendDate, FDistributeDate,");
            buff.append(" FPreTaxRatio,FAfterTaxRatio,FRoundCode,FCuryCode as FDividendCuryCode,FDivdendType from ");//modified by guyichuan STORY #741 增加FDivdendType
            buff.append(pub.yssGetTableName("tb_data_Predividend"));//从分红权益预处理表中获取权益数据
            buff.append(" where FDividendDate = ").append(dbl.sqlDate(dDate));//权益处理时取除权日数据，做权益确认日处理
            buff.append(" and FCheckState = 1) a "); 
            
            //--- MS01233  QDV4赢时胜(上海)2010年06月03日01_A 添加字段FEXCHANGECODE用于判断交易市场是国内市场还是国外市场  add by jiangshichao  2010.06.07
            buff.append(" left join (select FSecurityCode,FTradeCury, FEXCHANGECODE from ");
            buff.append(pub.yssGetTableName("Tb_Para_Security"));//关联证券信息表
            buff.append(" where FCheckState = 1 ) c on a.FSecurityCode1 = c.FSecurityCode");
            		
            buff.append(" join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Stock_Security"));//关联证券库存表
            buff.append(" where FPortCode in (" + operSql.sqlCodes(sPortCode));//组合代码
            buff.append(")");
            buff.append(" and FYearMonth<>").append(dbl.sqlString(strYearMonth));//不是期初数库存
            buff.append(" and FCheckState=1 )b  on a.fsecuritycode1 = b.fsecuritycode ");
            buff.append(" and (case when c.fexchangecode in ('CY', 'CS', 'CG') then a.FRecordDate else a.FDividendDate - 1 end) = b.FStorageDate ");//取权益确认日库存
            buff.append(" and c.fexchangecode <> 'CY'"); //story 1574 add by zhouwei 20111101 CY代表场外的分红数据，不产生交易数据而产生场外开发式基金业务数据
            buff.append(" left join (select FPortCode,FPortCury from ");
            buff.append(pub.yssGetTableName("Tb_Para_Portfolio"));//关联组合信息表
            buff.append(" where FCheckState = 1 ) d on b.FPortCode = d.FPortCode"); //关取证券信息表和组合表，取出交易货币和组合货币。
            //----------------------------MS01417--------------------------------//
            rs = dbl.queryByPreparedStatement(buff.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            buff.delete(0,buff.length());

            if (rs.next()) {
                rs.beforeFirst();//返回第一个rs
                //--------------------拼接交易编号---------------------
                String strNumDate = YssFun.formatDatetime(dDate).
                    substring(0, 8);
                strNumDate = strNumDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_Trade"),
                                           dbl.sqlRight("FNUM", 6),
                                           "100000",//将000000改为100000 分红编号调整为100000开始  QDV4中保2010年07月02日01_B 合并太平版本代码调整
                                           " where FNum like 'T"
                                           + strNumDate + "1%'", 1);//改为1% 分红编号调整为100000开始  QDV4中保2010年07月02日01_B 合并太平版本代码调整
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
                //--------------------------------end--------------------------//
                while (rs.next()) {
                    //-------------------------设置现金账户链接属性值----------------------
                    cashacc.setYssPub(pub);
                    cashacc.setLinkParaAttr( (analy1 ?
                                              rs.getString("FAnalysisCode1") :
                                              " "), //投资经理
                                            rs.getString("FPortCode"), //组合代码,证券库存的组合代码
                                            rs.getString("FSecurityCode1"), //证券代码
                                            (analy2 ?
                                             rs.getString("FAnalysisCode2") :
                                             " "), //券商
                                            strRightType,
                                            rs.getDate("FRecordDate"), //权益确认日
                                            rs.getString("FDividendCuryCode"), //分红币种
                                            YssOperCons.YSS_JYLX_PX); //交易类型为分发派息
                    //--------------------------------------------------------------------
                    subTrade = new TradeSubBean();//实例化
                    secSto = new SecurityStorageBean();//实例化
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
                    	java.util.Date date = YssFun.parseDate(YssFun.formatDate(rs.getDate("FDividendDate"), "yyyy-MM-dd"));
                    	StorageDate = YssFun.toSqlDate(YssFun.addDay(date, -1));
                    }
                    secSto = secSto.getStorageCost(StorageDate,
                            rs.getString("FSecurityCode"),//证券代码
                            rs.getString("FPortCode"),//组合代码
                            (analy1 ?
                             rs.getString("FAnalysisCode1") :
                             " "),//分析代码1
                            (analy2 ?
                             rs.getString("FAnalysisCode2") :
                             " "),//分析代码2
                            "", "C",
                            rs.getString("FAttrClsCode")); //"C"为获取 核算成本
//                    secSto = secSto.getStorageCost(rs.getDate("FRecordDate"),//权益确认日
//                        rs.getString("FSecurityCode"),//证券代码
//                        rs.getString("FPortCode"),//组合代码
//                        (analy1 ?
//                         rs.getString("FAnalysisCode1") :
//                         " "),//分析代码1
//                        (analy2 ?
//                         rs.getString("FAnalysisCode2") :
//                         " "),//分析代码2
//                        "", "C",
//                        rs.getString("FAttrClsCode")); //"C"为获取 核算成本
                    if (secSto != null) {
                        dSecurityCost = YssFun.toDouble(secSto.getStrStorageCost()); //为汇总的核算成本赋值
                        dSecurityAmount = YssFun.toDouble(secSto.getStrStorageAmount()); //为汇总的库存数量赋值
                    } else {
                        dSecurityCost = 0.0;//为汇总的核算成本赋值
                        dSecurityAmount = 0.0;//为汇总的库存数量赋值
                    }
                    CtlPubPara pubPara=new CtlPubPara();//通用参数实例化
                    pubPara.setYssPub(pub);//设置Pub
                    String rightsRatioMethods=(String)pubPara.getRightsRatioMethods(rs.getString("FPortCode"));//获取通用参数值
                    String ratioMethodsDetail = pubPara.getBRightsRatioMethods(rs.getString("FPortCode"),YssOperCons.YSS_JYLX_PX);//按权益类型获取权益比例方式 panjunfang add 20100510 B股业务
                    if(ratioMethodsDetail.length() > 0){
                    	rightsRatioMethods = ratioMethodsDetail;
                    }
                    if (dSecurityAmount > 0) {//判断证券数量是否大于0
                        dRightSub = this.getSettingOper().reckonRoundMoney(//分红权益=确认日库存数量*权益比例
                            rs.getString("FRoundCode") + "",
                            YssD.mul(dSecurityAmount,
                                     (rightsRatioMethods.equalsIgnoreCase("PreTaxRatio")?
                                      rs.getDouble("FPreTaxRatio"):rs.getDouble("FAfterTaxRatio"))));//通过通用参数获取权益比例方式
                        caBean = cashacc.getCashAccountBean();
                        //======MS01626 QDV4赢时胜(测试)2010年8月20日02_B add by yangheng 2010.08.25
                        if (caBean != null) {
                            strCashAccCode = caBean.getStrCashAcctCode();
                        } else { //MS00173 当分红处理时没有现金帐户时提示用户 by leeyu 2009-01-09
                            throw new YssException("系统执行分红权益时出现异常！" + "\n" + "【" +
                                rs.getString("FSecurityCode") +
                                "】证券分红权益处理时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
                        }
                        //=======
                      //MS01288 QDV4交银施罗德2010年6月10日01_A  add by jiangshichao 2010.07.02 
                        if(caBean.getCount()>1 && msg.indexOf(rs.getString("FTradeCury"))==-1){
                        	//一个币种对应多个账户，提示用户。这里拼接的币种不可以重复
                        	if(msg.length()>0){
                        		msg +=","+rs.getString("FTradeCury");
                        	}else{
                        		msg =rs.getString("FTradeCury");
                        	}
                        		

                        }
                      //======MS01626 QDV4赢时胜(测试)2010年8月20日02_B add by yangheng 2010.08.25
                      //MS01288 QDV4交银施罗德2010年6月10日01_A  add by jiangshichao 2010.07.02 
                        /*if (caBean != null) {
                            strCashAccCode = caBean.getStrCashAcctCode();
                        } else { //MS00173 当分红处理时没有现金帐户时提示用户 by leeyu 2009-01-09
                            throw new YssException("系统执行分红权益时出现异常！" + "\n" + "【" +
                                rs.getString("FSecurityCode") +
                                "】证券分红权益处理时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
                        }*/
                      //=======
                        //--------------------拼接交易编号---------------------
                        sNum++;
                        String tmp = "";
                        for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                            tmp += "0";
                        }
                        strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                        //------------------------end--------------------------//
                        dBaseRate = this.getSettingOper().getCuryRate(rs.
                            getDate("FDividendDate"),//除权日
                            rs.getString("FDividendCuryCode"),//分红币种
                            rs.getString("FPortCode"),//组合代码
                            YssOperCons.YSS_RATE_BASE);//获取基础汇率的值
                        dPortRate = this.getSettingOper().getCuryRate(rs.
                            getDate("FDividendDate"),
                            rs.getString("FPortCury"),
                            rs.getString("FPortCode"),
                            YssOperCons.YSS_RATE_PORT);//获取组合汇率的值

                        subTrade.setNum(strNumDate);//为交易编号赋值

                        subTrade.setSecurityCode(rs.getString("FSecurityCode"));//证券代码赋值

                        subTrade.setPortCode(rs.getString("FPortCode"));//组合代码
                        
                    	//============MS00932    送股、分红权益处理时，生成业务资料属性分类没有做判断   add by xuxuming,2010.01.15.送股时，先查询当天是否有指数调整信息。＝＝＝＝
                        //===============如果有调整，以调整之后的属性分类作为送股的属性分类＝＝＝＝＝＝＝＝＝＝＝＝＝＝
                        String strSqlSec = "select * from "+ pub.yssGetTableName("Tb_Data_Integrated")+
                        " where FPortCode in ("+dbl.sqlString(sPortCode)+
                        ") and FSecurityCode='"+rs.getString("FSecurityCode1")+
                        "' and FEXCHANGEDATE = " + dbl.sqlDate(rs.getDate("FRecordDate")) +
                        " and FTradeTypeCode='101' and FInOutType='1'";//只查询流入的数据。有这只证券的流入，则已流入的所属分类作为送股的属性分类
                        ResultSet rsSec = null;                
                        rsSec = dbl.queryByPreparedStatement(strSqlSec);
                        if(rsSec.next()){//一天内，一只证券最多只有一笔'成分股转换'类型的流入数据，故用IF
                        	strSecAttrCls= rsSec.getString("FAttrClsCode");
                        }
                        rsSec.close();
                        //=========================end==========================================
    					// ===========MS00932    送股、分红权益处理时，生成业务资料属性分类没有做判断   add by
    					// xuxuming,2010.01.15.当天有指数信息调整，以调整后的类型作为分红的类型，出凭证要用
    					if (strSecAttrCls != null
    							&& strSecAttrCls.trim().length() > 0) {
    						subTrade.setAttrClsCode(strSecAttrCls);
    					} else {
    						subTrade.setAttrClsCode(
    								rs.getString("FAttrClsCode") != null
    										&& rs.getString("FAttrClsCode").length() > 0 ? rs.getString("FAttrClsCode") : " "); // 所属分类代码
    					}
    					// ==================end===========================
                        
                        if (analy1) {
                            subTrade.setInvMgrCode(rs.getString("FAnalysisCode1"));//投资经理
                        } else {
                            subTrade.setInvMgrCode(" ");
                        }
                        if (analy2) {
                            subTrade.setBrokerCode(rs.getString("FAnalysisCode2"));//券商
                        } else {
                            subTrade.setBrokerCode(" ");
                        }
                        subTrade.setTradeCode(strRightType);//交易类型

                        subTrade.setTailPortCode(strCashAccCode);//尾差组合代码

                        subTrade.setAllotProportion(0);//分配比例

                        subTrade.setOldAllotAmount(0);//原始分配数量

                        subTrade.setAllotFactor(0);//分配因子
                        //add by zhangfa 20101222 1760 有关权益处理国内的分红数据的处理变更  
                        bDistribute=checkDistribute(sPortCode);
                       
                        if("CS".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))||"CG".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))
                            	||"CY".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))){
							if (bDistribute) {
								subTrade.setBargainDate(YssFun.formatDate(rs.getDate("FRecordDate")));
							}else{
								subTrade.setBargainDate(YssFun.formatDate(rs.getDate("FDividendDate")));//成交日期
							}
                        }else {
                        	subTrade.setBargainDate(YssFun.formatDate(rs.getDate(
                            "FDividendDate")));//成交日期
                        }
                        
                        //----------------end 20101223---------------------------------------------
                        subTrade.setBargainTime("00:00:00");//成交时间

                        subTrade.setSettleDate(YssFun.formatDate(rs.getDate(
                            "FDistributeDate")));//结算日期

                        subTrade.setSettleTime("00:00:00");//结算时间

                        subTrade.setAutoSettle(new Integer(1).toString()); //自动结算

                        subTrade.setPortCuryRate(dPortRate);//组合汇率

                        subTrade.setBaseCuryRate(dBaseRate);//基础汇率

                        subTrade.setTradeAmount(0);//交易数量

                        subTrade.setTradePrice(0);//交易价格

                        subTrade.setTradeMoney(0);//交易金额

                        subTrade.setAccruedInterest(dRightSub);//应计利息
                        //---------------------以下为成本赋值--------------
                        cost = new YssCost();
                        
                        //--- add by songjie 2013.04.12 STORY #3365 需求北京-[中国银行]QDV4.0[中]20121203001 start---//
                        if(rs.getInt("FDivdendType") == 36){
                        	subTrade.setTradeCode(YssOperCons.YSS_JYLX_PXRC);//交易类型:分发派息(资本返还)
                        	
                        	storageCost = rs.getDouble("FStorageCost");
                        	
                        	//取昨日库存成本（原币） 和 分红金额（原币）中的最小值作为 冲减成本金额（原币）（防止成本被冲减为负数）
                        	if(storageCost <= dRightSub){
                        		minCost = storageCost;
                        	}else{
                        		minCost = dRightSub;
                        	}
                        	
                            cost.setCost(minCost);//原币核算成本
                            cost.setMCost(minCost);//原币管理成本
                            cost.setVCost(minCost);//原币估值成本
                            cost.setBaseCost(this.getSettingOper().calBaseMoney(minCost,dBaseRate));//基础货币核算成本
                            cost.setBaseMCost(cost.getBaseCost());//基础货币管理成本
                            cost.setBaseVCost(cost.getBaseCost());//基础货币估值成本
                            cost.setPortCost(this.getSettingOper().calPortMoney(
                            		minCost, dBaseRate, dPortRate, 
                            		rs.getString("FDividendCuryCode"), 
                            		rs.getDate("FDividendDate"), 
                            		rs.getString("FPortCode")));//组合货币核算成本
                            cost.setPortMCost(cost.getPortCost());//组合货币管理成本
                            cost.setPortVCost(cost.getPortCost());//组合货币估值成本
                        }else{
                        	//--- add by songjie 2013.04.12 STORY #3365 需求北京-[中国银行]QDV4.0[中]20121203001 end---//
                            cost.setCost(0);//原币核算成本
                            cost.setMCost(0);//原币管理成本
                            cost.setVCost(0);//原币估值成本
                            cost.setBaseCost(0);//基础货币核算成本
                            cost.setBaseMCost(0);//基础货币管理成本
                            cost.setBaseVCost(0);//基础货币估值成本
                            cost.setPortCost(0);//组合货币核算成本
                            cost.setPortMCost(0);//组合货币管理成本
                            cost.setPortVCost(0);//组合货币估值成本
                        }//add by songjie 2013.04.12 STORY #3365 需求北京-[中国银行]QDV4.0[中]2012120300

                        subTrade.setCost(cost);//成本
                        //---------------------end-----------------//
                        subTrade.setDataSource(0);//数据源

                        subTrade.setDsType("HD_QY");//操作类型，表示系统操作数据，主要是和接口导入数据进行区分

                        subTrade.checkStateId = 1;//审核状态

                        subTrade.creatorCode = pub.getUserCode();//创建人

                        subTrade.checkTime = YssFun.formatDatetime(new java.util.Date());//审核时间

                        subTrade.checkUserCode = pub.getUserCode();//审核人

                        subTrade.creatorTime = YssFun.formatDatetime(new java.util.
                            Date());//创建时间

                        subTrade.setTotalCost(dRightSub);//投资总成本

                        subTrade.setSettleState(new Integer(0).toString());//结算状态，未结算“0”

                        subTrade.setFactSettleDate(YssFun.formatDate(rs.getDate(
                            "FDistributeDate")));//实际结算日期

                        subTrade.setMatureDate("9998-12-31");//到期日期

                        subTrade.setMatureSettleDate("9998-12-31");//到期结算日期

                        subTrade.setFactCashAccCode(strCashAccCode);//实际结算帐户

                        subTrade.setCashAcctCode(strCashAccCode);//设置现金账户

                        subTrade.setFactSettleMoney(dRightSub);//实际结算金额

                        subTrade.setExRate(1);//兑换汇率

                        subTrade.setFactPortRate(dPortRate); //实际结算组合汇率

                        subTrade.setFactBaseRate(dBaseRate);//实际结算基础汇率
                        
                        //---add by guyichuan 　20110514　STORY #741 QDV4富国基金2011年3月2日01_A 
                        subTrade.setStrDivdendType(rs.getString("FDivdendType"));				//分红类型
                        subTrade.setStrRecordDate(YssFun.formatDate(rs.getDate("FRecordDate")));//权益登记日
                        //------end-------

                        reArr.add(subTrade);//把数据保存到集合中

                    }
                }
                strDealInfo = "true";//表示有权益数据
            } else {
                strDealInfo = "no";//表示无权益数据
            }
            return reArr;
        } catch (Exception e) {
            strDealInfo = "false";
            throw new YssException("计算分红权益处理出错！",e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    //story 1574 add by zhouwei 20111107 获取场外的股票分红数据，转化成开发式基金业务数据
    public ArrayList getBankDividendData(java.util.Date dDate, String sPortCode) throws
    YssException {
    StringBuffer buff = null;//sql语句的拼接
    ResultSet rs = null;
    double dSecurityAmount = 0; //证券数量
    double dSecurityCost = 0; //证券成本
    double dRight = 0; //权益（主表）
    double dRightSub = 0; //权益（子表）
    String strRightType = ""; //权益类型
    String strSubRightType = ""; //权益类型
    String strCashAccCode = " "; //现金帐户
    String strYearMonth = "";//保存截取日期的年和天
    CashAccountBean caBean = null;//声明现金账户的bean
    boolean analy1;//分析代码1
    boolean analy2;//分析代码2
    boolean analy3;//分析代码3
    OpenFundTradeBean openFund = null;//交易子表的javaBean
    SecurityStorageBean secSto = null;//证券库存的javaBean
    ArrayList reArr = new ArrayList();
    CashAccLinkBean cashAccLink = null;//声明现金账户链接
    long sNum=0;//为了产生的编号不重复
    Date StorageDate = null; 
    boolean bDistribute=false;
    //--------------------end 20101223 -----------------------------
    try {
//        this.doDataPretreatment(dDate,sPortCode);//分红权益数据的预处理，主要是考虑跨组合群，组合的处理
        buff = new StringBuffer();
        BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.
            getOperDealCtx().getBean("cashacclinkdeal");
        operFun.setYssPub(pub);
        strYearMonth = YssFun.left(YssFun.formatDate(dDate), 4) + "00";//赋值
        analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");//判断是否有分析代码
        analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
        analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
        strRightType = YssOperCons.YSS_JYLX_PX; //权益类型为分发派息 strRightType = "06";
        strDealInfo = "no";
        // 根据权益信息中的证券信息去查找证券信息维护中的交易所  (CTY代表场外)   
        buff.append("select a.*, b.*,c.FTradeCury,c.FEXCHANGECODE,d.FPortCury from");
        buff.append("( select FSecurityCode as FSecurityCode1, FRecordDate, FDividendDate, FDistributeDate,");
        buff.append(" FPreTaxRatio,FAfterTaxRatio,FRoundCode,FCuryCode as FDividendCuryCode,FDivdendType from ");
        buff.append(pub.yssGetTableName("tb_data_Predividend"));//从分红权益预处理表中获取权益数据
        buff.append(" where FDividendDate = ").append(dbl.sqlDate(dDate));//权益处理时取除权日数据，做权益确认日处理
        buff.append(" and FCheckState = 1) a "); 
        
        //字段FEXCHANGECODE用于判断交易市场是国内市场还是国外市场 
        buff.append(" left join (select FSecurityCode,FTradeCury, FEXCHANGECODE from ");
        buff.append(pub.yssGetTableName("Tb_Para_Security"));//关联证券信息表
        buff.append(" where FCheckState = 1 ) c on a.FSecurityCode1 = c.FSecurityCode");
        		
        buff.append(" join (select * from ");
        buff.append(pub.yssGetTableName("Tb_Stock_Security"));//关联证券库存表
        buff.append(" where FPortCode in (" + operSql.sqlCodes(sPortCode));//组合代码
        buff.append(")");
        buff.append(" and FYearMonth<>").append(dbl.sqlString(strYearMonth));//不是期初数库存
        buff.append(" and FCheckState=1 ) b  on a.fsecuritycode1 = b.fsecuritycode ");
        buff.append(" and a.FRecordDate = b.FStorageDate ");//取权益确认日库存
        buff.append(" and c.fexchangecode = 'CY'"); //CTY代表场外的分红数据，不产生交易数据而产生场外开发式基金业务数据
        buff.append(" left join (select FPortCode,FPortCury from ");
        buff.append(pub.yssGetTableName("Tb_Para_Portfolio"));//关联组合信息表
        buff.append(" where FCheckState = 1 ) d on b.FPortCode = d.FPortCode"); //关取证券信息表和组合表，取出交易货币和组合货币。
        rs = dbl.queryByPreparedStatement(buff.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        buff.delete(0,buff.length());

        if (rs.next()) {
            rs.beforeFirst();//返回第一个rs
            //--------------------拼接交易编号---------------------
            String strNumDate = YssFun.formatDatetime(dDate).
                substring(0, 8);           
            strNumDate = "OTC" + strNumDate;
            strNumDate = strNumDate +
                dbFun.getNextInnerCode(pub.yssGetTableName(
                    "Tb_Data_OpenFundTrade"),
                                       dbl.sqlRight("FNUM", 6), "000000000",
                                       " where FNum like '"
                                       +
                                       strNumDate.replaceAll("'", "''") +
                                       "%'");
            String s = strNumDate.substring(11, strNumDate.length());
            sNum = Long.parseLong(s);
            //--------------------------------end--------------------------//
            while (rs.next()) {
                //-------------------------设置现金账户链接属性值----------------------
                cashacc.setYssPub(pub);
                cashacc.setLinkParaAttr( (analy1 ?
                                          rs.getString("FAnalysisCode1") :
                                          " "), //投资经理
                                        rs.getString("FPortCode"), //组合代码,证券库存的组合代码
                                        rs.getString("FSecurityCode1"), //证券代码
                                        (analy2 ?
                                         rs.getString("FAnalysisCode2") :
                                         " "), //券商
                                        strRightType,
                                        rs.getDate("FRecordDate"), //权益确认日
                                        rs.getString("FDividendCuryCode"), //分红币种
                                        YssOperCons.YSS_JYLX_PX); //交易类型为分发派息
                //--------------------------------------------------------------------
                openFund = new OpenFundTradeBean();//实例化
                secSto = new SecurityStorageBean();//实例化
                secSto.setYssPub(pub);        
            	StorageDate = rs.getDate("FRecordDate");
                secSto = secSto.getStorageCost(StorageDate,
                        rs.getString("FSecurityCode"),//证券代码
                        rs.getString("FPortCode"),//组合代码
                        (analy1 ?
                         rs.getString("FAnalysisCode1") :
                         " "),//分析代码1
                        (analy2 ?
                         rs.getString("FAnalysisCode2") :
                         " "),//分析代码2
                        "", "C",
                        rs.getString("FAttrClsCode")); //"C"为获取 核算成本
                if (secSto != null) {
                    dSecurityCost = YssFun.toDouble(secSto.getStrStorageCost()); //为汇总的核算成本赋值
                    dSecurityAmount = YssFun.toDouble(secSto.getStrStorageAmount()); //为汇总的库存数量赋值
                } else {
                    dSecurityCost = 0.0;//为汇总的核算成本赋值
                    dSecurityAmount = 0.0;//为汇总的库存数量赋值
                }
                CtlPubPara pubPara=new CtlPubPara();//通用参数实例化
                pubPara.setYssPub(pub);//设置Pub
                String rightsRatioMethods=(String)pubPara.getRightsRatioMethods(rs.getString("FPortCode"));//获取通用参数值
                String ratioMethodsDetail = pubPara.getBRightsRatioMethods(rs.getString("FPortCode"),YssOperCons.YSS_JYLX_PX);//按权益类型获取权益比例方式 
                if(ratioMethodsDetail.length() > 0){
                	rightsRatioMethods = ratioMethodsDetail;
                }
                if (dSecurityAmount > 0) {//判断证券数量是否大于0
                    dRightSub = this.getSettingOper().reckonRoundMoney(//分红权益=确认日库存数量*权益比例
                        rs.getString("FRoundCode") + "",
                        YssD.mul(dSecurityAmount,
                                 (rightsRatioMethods.equalsIgnoreCase("PreTaxRatio")?
                                  rs.getDouble("FPreTaxRatio"):rs.getDouble("FAfterTaxRatio"))));//通过通用参数获取权益比例方式
                    caBean = cashacc.getCashAccountBean();
                    if (caBean != null) {
                        strCashAccCode = caBean.getStrCashAcctCode();
                    } else { //当分红处理时没有现金帐户时提示用户
                        throw new YssException("系统执行分红权益时出现异常！" + "\n" + "【" +
                            rs.getString("FSecurityCode") +
                            "】证券分红权益处理时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
                    }
                    if(caBean.getCount()>1 && msg.indexOf(rs.getString("FTradeCury"))==-1){
                    	//一个币种对应多个账户，提示用户。这里拼接的币种不可以重复
                    	if(msg.length()>0){
                    		msg +=","+rs.getString("FTradeCury");
                    	}else{
                    		msg =rs.getString("FTradeCury");
                    	}
                    		

                    }
                
                   
                    //--------------------拼接交易编号---------------------
                    String tmp = "";
                    for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                        tmp += "0";
                    }
                    strNumDate = strNumDate.substring(0, 11) + tmp + sNum;
                    //------------------------end--------------------------//
                    openFund.setNum(strNumDate);//为交易编号赋值

                    openFund.setSecurityCode(rs.getString("FSecurityCode"));//证券代码赋值

                    openFund.setPortCode(rs.getString("FPortCode"));//组合代码
                    if (analy1) {
                    	openFund.setInvMgrCode(rs.getString("FAnalysisCode1"));//投资经理
                    } else {
                    	openFund.setInvMgrCode(" ");
                    }

                    openFund.setTradeTypeCode(strRightType);//交易类型

                   
                    //有关权益处理国内的分红数据的处理变更  
                    bDistribute=checkDistribute(sPortCode);
                    if (bDistribute) {
						openFund.setBargainDate(rs.getDate("FRecordDate"));//确认日期
						  openFund.setApplyDate(rs.getDate(
	                        "FRecordDate"));//成交日期
					}else{
						openFund.setBargainDate(rs.getDate("FDividendDate"));//确认日期
						openFund.setApplyDate(rs.getDate(
                        "FDividendDate"));//成交日期
					}
                    openFund.setInvestType("C");//投资类型
                    openFund.setApplyMoney(dRightSub);//金额
                    openFund.setCheckState("1");//审核状态
                    openFund.setApplyCashAccCode(strCashAccCode);
                    openFund.setDataType("apply");                
                    reArr.add(openFund);//把数据保存到集合中
                    sNum++;
                }
            }
            strDealInfo = "true";//表示有权益数据
        } else {
            strDealInfo = "no";//表示无权益数据
        }
        return reArr;
    } catch (Exception e) {
        strDealInfo = "false";
        throw new YssException("计算场外分红权益处理出错！",e);
    } finally {
        dbl.closeResultSetFinal(rs);
    }
}
    /**
     * 调用基类方法，保存数据
     * @param alRightEquitys ArrayList 保存数据的集合
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @throws YssException 异常
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    public void saveRightEquitys(ArrayList alRightEquitys, java.util.Date dDate, String sPortCode) throws YssException {
        ArrayList newAlRightEquity=null;
        try{
            if(alRightEquitys != null && alRightEquitys.size() > 0){
            	 // EDIT by lidaolong 20110407 #536 有关国内接口数据处理顺序的变更 
                newAlRightEquity = checkSubTradeHaveRightData(alRightEquitys,YssOperCons.YSS_JYLX_PX,"ZD_QY,ZD_QY_T+1",dDate,sPortCode,this.sSecurityCode);
                /**shashijie 2012-6-7 BUG 4733 这里删除已经晚了,之前的交易数据已经删除了,这里查不出数据,必须放到前面 */
                //保存业务资料数据之前，先删除已经结算产生的资金调拨数据              
                //delCashTransfer(newAlRightEquity,dDate,sPortCode);
				/**end*/
                super.saveRightEquitys(newAlRightEquity, dDate, sPortCode); //调用基类保存数据方法
            }
        }catch(Exception e){
            throw new YssException(e.getMessage());
        }
    }
    //story 1574 add by zhouwei 20111107 保存权益数据到开放式基金加以数据表
    public void saveBankRightEquitys(ArrayList alRightEquitys, java.util.Date dDate, String sPortCode) throws YssException {
        ArrayList newAlRightEquity=null;
        try{
            if(alRightEquitys != null && alRightEquitys.size() > 0){
            	//QY_CL是经过权益处理操作生成的场外基金交易数据的来源标示
                newAlRightEquity = checkOpenFundTradeHaveRightData(alRightEquitys,YssOperCons.YSS_JYLX_PX,"QY_CL",dDate,sPortCode);                    
                super.saveBankRightEquitys(newAlRightEquity, dDate, sPortCode); //调用基类保存数据方法
            }
        }catch(Exception e){
            throw new YssException(e.getMessage());
        }
    }
    //STORY 1574 ADD BY ZHOUWEI 20111107用来筛选没有经过权益处理操作的场外的权益信息
    public ArrayList checkOpenFundTradeHaveRightData(ArrayList alRightEquitys,String sTradeType,String sDsType,
            java.util.Date dDate,String sPortCode)throws YssException{

        ResultSet rs=null;
        StringBuffer buff=null;
        OpenFundTradeBean openTrade = null;
        try{
            buff=new StringBuffer();
            buff.append(" select * from ");
            buff.append(pub.yssGetTableName("Tb_Data_OpenFundTrade"));
            buff.append(" where FCheckState =1 and FBargaindate =").append(dbl.sqlDate(dDate));//操作日期
            buff.append(" and FSecurityCode in(").append(this.operSql.sqlCodes(this.sSecurityCode)).append(")");//证券代码
            buff.append(" and FPortCode = ").append(dbl.sqlString(sPortCode));//组合代码
            buff.append(" and FTradeTypeCode =").append(dbl.sqlString(sTradeType));//交易类型
            buff.append(" and FDataBirth in(").append(operSql.sqlCodes((sDsType))).append(")");//操作类型

            rs=dbl.openResultSet(buff.toString());
            buff.delete(0,buff.length());
            while (rs.next()) {
                for (int i = 0; i <alRightEquitys.size(); i++) {
                    if(alRightEquitys.size()==1){
                    	openTrade = (OpenFundTradeBean) alRightEquitys.get(0);
                    }else{
                    	openTrade = (OpenFundTradeBean) alRightEquitys.get(i);
                    }
                    //根据证券代码，组合代码，交易日期，交易类型，权益处理，五个条件进行判断，如果存在，那么系统中的这条权益数据要删除掉，不用再产生权益数据了
                    if(openTrade.getSecurityCode().equals(rs.getString("FSecurityCode"))
                       &&openTrade.getPortCode().equals(rs.getString("FPortCode"))
                        &&YssFun.formatDate(openTrade.getBargainDate()).equals(rs.getDate("FBargaindate").toString())
                         &&openTrade.getTradeTypeCode().equals(rs.getString("FTradeTypeCode"))
                          &&(rs.getString("FDataBirth").equals("QY_CL"))){
                        alRightEquitys.remove(openTrade);
                        i--;
                        if(alRightEquitys.size()==0){
                            break;
                        }
                    }
                }
            }
        }catch(Exception e){
            throw new YssException("判断接口导入数据中有没有要处理的权益信息的业务资料数据出错！",e);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
        return alRightEquitys;
    
}
    /**
     * 删除已经结算的资金调拨数据。
     * 参数中去掉证券代码作为条件，将参数更改为交易类型，组合，起始日期与结束日期和证券代码
     * 备注：此方法事物必须在外部进行控制
     * @param tradeType String 交易类型
     * @param dBeginDate Date  业务开始日期
     * @param dEndDate Date    业务结束日期
     * @param portCode String  组合代码，多个以“，”分隔
     * @param sSecurityCode String 证券代码，多个以“，”分隔
     * @throws YssException
     */
    public void delCashTransfer(ArrayList newAlRightEquity,java.util.Date dDate, String sPortCode) throws
        YssException {
        String sqlStr = "";//拼接SQL语句
        ResultSet rs = null;
        String nums = "";//保存交易编号
        StringBuffer buf = new StringBuffer();
        TradeSubBean subTrade = null;//交易子表的javaBean
        String sSecurityCode="";//保存证券代码
        try {
            for (int i = 0; i < newAlRightEquity.size(); i++) {//遍历和数据接口导入数据比对后的集合
                subTrade = (TradeSubBean) newAlRightEquity.get(i);
                sSecurityCode += subTrade.getSecurityCode() + ",";//拼接证券代码
            }
            if(sSecurityCode.endsWith(",")){
                sSecurityCode = sSecurityCode.substring(0,sSecurityCode.length()-1);
            }
            sqlStr = "select FNum,FSecurityCode from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +//业务资料
                " where FTradeTypeCode = " + dbl.sqlString(YssOperCons.YSS_JYLX_PX) +//分红
                " and FPortCode in( " + this.operSql.sqlCodes(sPortCode) +
                ")" +
                " and FBargainDate between " + dbl.sqlDate(dDate) +
                " and " + dbl.sqlDate(dDate)+
                (sSecurityCode.trim().length()>0?(" and FSecurityCode in("+this.operSql.sqlCodes(sSecurityCode)+")"):"");
            rs = dbl.queryByPreparedStatement(sqlStr);
            while (rs.next()) {
                buf.append(rs.getString("FNum")).append(",");//拼接交易编号
            }
            if (buf.length() > 0) {
                nums = buf.substring(0, buf.length() - 1);
                buf.delete(0, buf.length());
            }
            //删除资金调拨子表
            sqlStr = "delete from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FNum in (select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FTradeNum in(" +
                this.operSql.sqlCodes(nums) + ") )";
            dbl.executeSql(sqlStr);
            //删除资金调拨主表
            sqlStr = "delete from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FTradeNum in (" + this.operSql.sqlCodes(nums) +
                ") ";
            dbl.executeSql(sqlStr);
        } catch (Exception e) {
            throw new YssException("删除资金调拨出错!" + "\n", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
    public ArrayList checkSubTradeHaveRightData(ArrayList alRightEquitys,String sTradeType,String sDsType,
                                                java.util.Date dDate,String sPortCode,String sSecurityCode) throws YssException{
        ArrayList newAlRightEquity=null;
        try{
            newAlRightEquity = super.checkSubTradeHaveRightData(alRightEquitys, sTradeType,sDsType,dDate, sPortCode, sSecurityCode);
        }catch(Exception e){
            throw new YssException(e.getMessage());
        }
        return newAlRightEquity;
    }

    /**
     * 删除条件，设置删除交易主子表数据的条件
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return TradeBean 返回值
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    public TradeBean filterBean(java.util.Date dDate, String sPortCode) {
        TradeBean trade = new TradeBean();
        trade.setTradeCode(YssOperCons.YSS_JYLX_PX);//交易方式为分发派息
        trade.setPortCode(sPortCode);//组合代码
        trade.setBargainDate(YssFun.formatDate(dDate));//成交日期
        trade.setDsType("HD_QY");//操作类型，表示此数据时界面输入的数据
        return trade;
    }

    /**
     * 此方法做分红数据的预处理，把处理数据保存到临时表tb_pub_data_Predividend
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @throws YssException 异常
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    private void doDataPretreatment(java.util.Date dDate, String sPortCode) throws YssException{
        StringBuffer buff=null;//拼接sql语句
        ResultSet rs=null;//声明结果集
        DividendBean dividend=null;//分红的javaBean
        ArrayList dividendData=new ArrayList();//保存预处理后的分红权益数据
        try{
        	createTmpTable();//创建分红临时表

        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        	String strSql = " truncate table " + pub.yssGetTableName("tb_data_Predividend");
        	dbl.executeSql(strSql);
        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            
            /**
             * 以下sql语句处理分红权益数据，有组合群和组合代码
             */
            buff=new StringBuffer();
            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_dividend"));//分红权益表
            buff.append(" where FDividendDate =").append(dbl.sqlDate(dDate));//操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB()));//组合群代码
            //edit by songjie 2011.09.07 BUG  2593 QDV4建行2011年08月29日02_B
            buff.append(" and FPortCode in(").append(operSql.sqlCodes(sPortCode) + ")");//组合代码

            rs=dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0,buff.length());
            while(rs.next()){
                dividend = new DividendBean();
                dividend.setYssPub(pub);
                sSecurityCode+=rs.getString("FSecurityCode")+",";//把获取的证券代码拼接 起来，作为下面的sql语句条件

                dividend.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
                dividend.setRecordDate(rs.getDate("FRecordDate").toString());//权益确认日
                dividend.setDividendDate(rs.getDate("FDividendDate").toString());//除权日
                dividend.setDistributeDate(rs.getDate("FDistributeDate").toString());//到帐日
                dividend.setAfficheDate(rs.getDate("FAfficheDate").toString());//公告日
                dividend.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio")));//税前权益比例
                dividend.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio")));//税后权益比例
                dividend.setPortCode(rs.getString("FPortCode"));//组合代码
                dividend.setAssetGroupCode(rs.getString("FAssetGroupCode"));//组合群代码
                dividend.setRoundCode(rs.getString("FRoundCode"));//舍入代码
                dividend.setDesc(rs.getString("FDesc"));//描述
                dividend.setDividentType(Integer.toString(rs.getInt("FDivdendType")));//分红类型
                dividend.setDividentCuryCode(rs.getString("FCuryCode"));//币种代码
                dividendData.add(dividend);//把数据保存到集合中
            }
            saveIntoTmpTable(dividendData); //保存数据到分红临时表
            dbl.closeResultSetFinal(rs);//关闭游标
            dividendData.clear();//清空集合

            /**
             * 以下sql语句处理分红权益数据，有组合群,但没有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_dividend"));//分红权益表
            buff.append(" where FDividendDate =").append(dbl.sqlDate(dDate));//操作日期
            buff.append(" and FCheckState = 1");
            
            //20120604 modified by liubo.Bug #4714
            //某条股票分红数据选择了多组合群后，权益处理无法产生交易数据
            //================================
//            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB()));//组合群代码
            buff.append(" and FAssetGroupCode like ").append(dbl.sqlString("%" + pub.getPrefixTB() + "%"));
            //=============end===================
            buff.append(" and FPortCode =").append(dbl.sqlString(" "));//组合代码
            buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":"");//证券代码

            rs=dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0,buff.length());
            while(rs.next()){
                dividend = new DividendBean();
                dividend.setYssPub(pub);
                sSecurityCode+=rs.getString("FSecurityCode")+",";//把获取的证券代码拼接 起来，作为下面的sql语句条件
                dividend.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
                dividend.setRecordDate(rs.getDate("FRecordDate").toString());//权益确认日
                dividend.setDividendDate(rs.getDate("FDividendDate").toString());//除权日
                dividend.setDistributeDate(rs.getDate("FDistributeDate").toString());//到帐日
                dividend.setAfficheDate(rs.getDate("FAfficheDate").toString());//公告日
                dividend.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio")));//税前权益比例
                dividend.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio")));//税后权益比例
                dividend.setPortCode(sPortCode);//组合代码
                dividend.setAssetGroupCode(rs.getString("FAssetGroupCode"));//组合群代码
                dividend.setRoundCode(rs.getString("FRoundCode"));//舍入代码
                dividend.setDesc(rs.getString("FDesc"));//描述
                dividend.setDividentType(Integer.toString(rs.getInt("FDivdendType")));//分红类型
                dividend.setDividentCuryCode(rs.getString("FCuryCode"));//币种代码
                dividendData.add(dividend);//把数据保存到集合中
            }
            saveIntoTmpTable(dividendData); //保存数据到分红临时表
            dbl.closeResultSetFinal(rs); //关闭游标
            dividendData.clear(); //清空集合

            //---add by songjie 2011.09.07 BUG 2593 QDV4建行2011年08月29日02_B start---//
            /**
             * 以下sql语句处理分红权益数据，没有组合群,有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_dividend"));//分红权益表
            buff.append(" where FDividendDate =").append(dbl.sqlDate(dDate));//操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(" "));//组合群代码
            buff.append(" and FPortCode in(").append(operSql.sqlCodes(sPortCode) + ")");//组合代码
            buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":"");//证券代码

            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                dividend = new DividendBean();
                dividend.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ",";//把获取的证券代码拼接 起来，作为下面的sql语句条件
                dividend.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
                dividend.setRecordDate(rs.getDate("FRecordDate").toString());//权益确认日
                dividend.setDividendDate(rs.getDate("FDividendDate").toString());//除权日
                dividend.setDistributeDate(rs.getDate("FDistributeDate").toString());//到帐日
                dividend.setAfficheDate(rs.getDate("FAfficheDate").toString());//公告日
                dividend.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio")));//税前权益比例
                dividend.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio")));//税后权益比例
                dividend.setPortCode(sPortCode);//组合代码
                dividend.setAssetGroupCode(pub.getPrefixTB());//组合群代码
                dividend.setRoundCode(rs.getString("FRoundCode"));//舍入代码
                dividend.setDesc(rs.getString("FDesc"));//描述
                dividend.setDividentType(Integer.toString(rs.getInt("FDivdendType")));//分红类型
                dividend.setDividentCuryCode(rs.getString("FCuryCode"));//币种代码
                dividendData.add(dividend);//把数据保存到集合中
            }
            saveIntoTmpTable(dividendData); //保存数据到分红临时表
            dbl.closeResultSetFinal(rs);//关闭游标
            dividendData.clear();//清空集合
            //---add by songjie 2011.09.07 BUG 2593 QDV4建行2011年08月29日02_B start---//
            
            /**
            * 以下sql语句处理分红权益数据，没有组合群,但没有组合代码
            */
           buff.append(" select * from ").append(pub.yssGetTableName("tb_data_dividend"));//分红权益表
           buff.append(" where FDividendDate =").append(dbl.sqlDate(dDate));//操作日期
           buff.append(" and FCheckState = 1");
           buff.append(" and FAssetGroupCode =").append(dbl.sqlString(" "));//组合群代码
           buff.append(" and FPortCode =").append(dbl.sqlString(" "));//组合代码
           buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":"");//证券代码

           rs = dbl.queryByPreparedStatement(buff.toString());
           buff.delete(0, buff.length());
           while (rs.next()) {
               dividend = new DividendBean();
               dividend.setYssPub(pub);
               sSecurityCode += rs.getString("FSecurityCode") + ",";//把获取的证券代码拼接 起来，作为下面的sql语句条件
               dividend.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
               dividend.setRecordDate(rs.getDate("FRecordDate").toString());//权益确认日
               dividend.setDividendDate(rs.getDate("FDividendDate").toString());//除权日
               dividend.setDistributeDate(rs.getDate("FDistributeDate").toString());//到帐日
               dividend.setAfficheDate(rs.getDate("FAfficheDate").toString());//公告日
               dividend.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio")));//税前权益比例
               dividend.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio")));//税后权益比例
               dividend.setPortCode(sPortCode);//组合代码
               dividend.setAssetGroupCode(pub.getPrefixTB());//组合群代码
               dividend.setRoundCode(rs.getString("FRoundCode"));//舍入代码
               dividend.setDesc(rs.getString("FDesc"));//描述
               dividend.setDividentType(Integer.toString(rs.getInt("FDivdendType")));//分红类型
               dividend.setDividentCuryCode(rs.getString("FCuryCode"));//币种代码
               dividendData.add(dividend);//把数据保存到集合中
           }
           saveIntoTmpTable(dividendData); //保存数据到分红临时表
        }catch(Exception e){
            throw new YssException("分红数据的预处理，把处理数据保存到临时表出错！",e);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 保存数据到分红临时表
     * @param dividendData ArrayList 保存数据的集合
     * @throws YssException 异常
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    private void saveIntoTmpTable(ArrayList dividendData) throws YssException {
        StringBuffer buff = null;//拼接SQL语句
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement pst = null;
        //=============end====================
        DividendBean dividend=null;//分红实体bean
        try {
            buff=new StringBuffer();
            buff.append(" insert into ").append(pub.yssGetTableName("tb_data_Predividend"));//分红权益预处理表
            buff.append(" (");
            buff.append("FSecurityCode,FRecordDate,FDividendDate,FDistributeDate,");
            buff.append("FAfficheDate,FPreTaxRatio,FAfterTaxRatio,");
            buff.append(" FPortCode,FAssetGroupCode,FRoundCode,FDesc,");
            buff.append("FCheckState,FCreator,FCreateTime,FCheckUser,FDivdendType,FCuryCode,FCheckTime");
            buff.append(")");
            buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			//modified by liubo.Story #2145
			//==============================
//            pst = dbl.getPreparedStatement(buff.toString());
            pst = dbl.getYssPreparedStatement(buff.toString());
			//==============end================
            for(int i=0;i<dividendData.size();i++){//循环保存数据的集合
                dividend=(DividendBean)dividendData.get(i);//获取实例bean
                pst.setString(1,dividend.getSecurityCode());//证券代码
                pst.setDate(2,YssFun.toSqlDate(dividend.getRecordDate()));//权益确认日
                pst.setDate(3,YssFun.toSqlDate(dividend.getDividendDate()));//除权日
                pst.setDate(4,YssFun.toSqlDate(dividend.getDistributeDate()));//权益派息日
                pst.setDate(5,YssFun.toSqlDate(dividend.getAfficheDate()));//公告日
                pst.setDouble(6,Double.parseDouble(dividend.getPreTaxRatio()));//税前权益比例
                pst.setDouble(7,Double.parseDouble(dividend.getAfterTaxRatio()));//税后权益比例
                pst.setString(8,dividend.getPortCode());//组合代码
                pst.setString(9,dividend.getAssetGroupCode());//组合群代码
                pst.setString(10,dividend.getRoundCode());//舍入代码
                pst.setString(11,dividend.getDesc());//描述
                pst.setInt(12,1);//审核状态
                pst.setString(13,pub.getUserCode());//创建人
                pst.setString(14,YssFun.formatDatetime(new java.util.Date()));//创建时间
                pst.setString(15,pub.getUserCode());//审核人
                pst.setInt(16,Integer.parseInt(dividend.getDividentType()));//分红类型
                pst.setString(17,dividend.getDividentCuryCode());//分红币种
                pst.setString(18,YssFun.formatDatetime(new java.util.Date()));//审核时间
                pst.executeUpdate();
            }
        } catch (Exception e) {
            throw new YssException("保存数据到分红临时表出错！", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }
    /**
     * createTmpTable创建分红临时表 tb_pub_data_Predividend
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    private void createTmpTable() throws YssException {
        StringBuffer buff=null;
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        String strSql = "";
        ResultSet rs = null;
        String duration = "";//表类型
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try{
        	//---edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
       	 	buff=new StringBuffer();
            buff.append(" CREATE GLOBAL TEMPORARY TABLE  ").append(pub.yssGetTableName("tb_data_Predividend"));
            buff.append(" ( ");
            buff.append(" FSecurityCode   VARCHAR(50)   NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FDIVDENDTYPE    NUMBER(2)     NOT NULL, ");
            buff.append(" FCURYCODE       VARCHAR2(20)  DEFAULT ' ' NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FDIVIDENDDATE   DATE          NOT NULL,");
            buff.append(" FDISTRIBUTEDATE DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_Predividend"));
            //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
            //buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FDIVDENDTYPE,FCURYCODE,FPORTCODE,FASSETGROUPCODE) ");
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FDIVDENDTYPE,FCURYCODE,FPORTCODE,FASSETGROUPCODE,FDISTRIBUTEDATE) ");
            //----------------------------------------------------
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_Predividend".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_Predividend"))) { 
        				/**shashijie ,2011-10-12 , STORY 1698*/
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("tb_data_Predividend")));
        				/**end*/
        			}

                    dbl.executeSql(buff.toString());
        		}
        	}else{
        		dbl.executeSql(buff.toString());
        	}
        	
        	buff.delete(0,buff.length());
        	//---edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        }catch(Exception e){
            throw new YssException("创建分红临时表出错！",e);
        }
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
    }
    //
    
    /**
     * @throws YssException 
     * @方法名：checkDistribute
     * @参数：sPortCode 组合代码
     * @返回类型：boolean
     * @说明： 	权益处理国内的分红业务时，判断相关组合对应的分红派息提前一天入账是否被勾选
     * add by zhangfa 20101222 1760 有关权益处理国内的分红数据的处理变更  
     */
    private boolean checkDistribute(String sPortCode) throws YssException{
		boolean flag = false;
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = " select FParameter from "+pub.yssGetTableName("tb_DAO_ReadType") +
    		         " where FPortCode="+dbl.sqlString(sPortCode)+
    		         " and  FParameter='02' ";
			rs = dbl.queryByPreparedStatement(strSql);
			if (rs.next()) {
				flag = true;
			}
    	}catch(Exception e){
    		throw new YssException("判断相关组合对应的分红派息提前一天入账是否被勾选出错！",e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    	return flag;
    }
    
	/**shashijie 2012-6-7 BUG 4733 重载删除资金调拨方法 
	* @param type
	* @param dDate
	* @param sPortCode*/
	public void delCashTransfer(String type, java.util.Date dDate,
			String FPortCode) throws YssException {
		String nums = "";
		//获取交易数据代码
		nums = getNums(type,dDate,FPortCode);
		//删除资金调拨
		deleteSubTransfer(nums);
	}
	
	/**shashijie 2012-6-7 BUG 4733 删除资金调拨*/
	private void deleteSubTransfer(String nums) throws YssException{
		
		try {
			//删除资金调拨子表
            String sqlStr = "delete from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FNum in (select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FTradeNum in(" +
                this.operSql.sqlCodes(nums) + ") )";
            dbl.executeSql(sqlStr);
            //删除资金调拨主表
            sqlStr = "delete from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FTradeNum in (" + this.operSql.sqlCodes(nums) +
                ") ";
            dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("删除权益数据对应资金调拨出错!",e);
		} finally {
			//dbl.closeResultSetFinal(rs);
		}
	}
	
	/**shashijie 2012-6-7 BUG 4733 */
	private String getNums(String type, java.util.Date dDate, String FPortCode) throws YssException {
		ResultSet rs = null;
		String nums = "";
		try {
			String strSql = "Select FNum From "+pub.yssGetTableName("Tb_Data_Subtrade")+
			" Where Fbargaindate = "+dbl.sqlDate(dDate)+
			" And Fportcode = "+dbl.sqlString(FPortCode)+
			" And Ftradetypecode = "+dbl.sqlString(type)
			+ " and FCreator <> 'GCS'"; //add by huangqirong 2012-12-18 story #2327 GCS 类型的权益数据产生的交易数据不删除
			if(type.equals("06")){// bug BUG #83060 add dongqingsong 2013-11-12
				strSql = strSql+ "and Ftradetypecode not in ('06') and FSettleState = 1" ;
			}
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				nums += rs.getString("FNum") + ",";
			}
			if (nums.length()>0) {
				nums = YssFun.getSubString(nums);
			}
		} catch (Exception e) {
			throw new YssException("获取交易数据编号出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return nums;
	}
    
  
}





