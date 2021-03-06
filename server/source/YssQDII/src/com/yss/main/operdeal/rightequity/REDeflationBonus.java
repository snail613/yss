package com.yss.main.operdeal.rightequity;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.yss.dsub.YssPreparedStatement;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.*;
import com.yss.main.parasetting.*;
import com.yss.main.storagemanage.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.manager.TradeDataAdmin;

/**
 *
 * <p>Title: shashijie 2011-08-09 STORY 1434 , 国内权益处理</p>
 * <p>Description:计算缩股数量  </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */
public class REDeflationBonus
    extends BaseRightEquity {
    private String sSecurityCode = ""; //保存证券代码
    private String tSecurityCode = ""; //保存缩配证券代码
    
    public REDeflationBonus() {
    }

    /**
     * 做缩股权益业务处理，产生业务资料数据保存到业务资料javaBean中
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return ArrayList 返回值
     * @throws YssException 异常
     * shashijie 2011-08-09 STORY 1434 ,  国内权益处理
     */
    public ArrayList getDayRightEquitys(java.util.Date dDate, String sPortCode) throws
        YssException {
        StringBuffer buff = null; //sql语句的拼接
        double dSecurityAmount = 0; //证券数量
        //double dSecurityCost = 0; //证券成本(缩股没有成本)
        //double dRight = 0; //权益（主表）
        double dRightSub = 0; //权益（子表）
        String strRightType = ""; //权益类型
        String strCashAccCode = " "; //现金帐户
        //String strMaxNum = "", strMaxNumSub = "";
        String strYearMonth = ""; //保存截取日期的年和天
        CashAccountBean caBean = null; //声明现金账户的bean
        boolean analy1; //分析代码1
        boolean analy2; //分析代码2
        //boolean analy3; //分析代码3
        ResultSet rs = null; //声明结果集
        TradeSubBean subTrade = null; //交易子表的javaBean
        YssCost cost = null; //声明成本
        SecurityStorageBean secSto = null; //证券库存的javaBean
        ArrayList reArr = new ArrayList();
        long sNum = 0; //为了产生的编号不重复
        double dBaseRate = 1;//基础汇率
        double dPortRate = 1;//组合汇率
        Date StorageDate = null; //MS01233  QDV4赢时胜(上海)2010年06月03日01_A add by jiangshichao
        try {
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B start---//
        	TradeDataAdmin tradeData = new TradeDataAdmin();//交易数据操作类
        	tradeData.setYssPub(pub);
        	tradeData.delete("", dDate,dDate,null,null,"",sPortCode, "", 
        			"",YssOperCons.YSS_JYLX_DE, "", "",false, "HD_QY");
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B end---//
        	
            this.doDataPretreatment(dDate, sPortCode); //此方法做缩股数据的预处理，把处理数据保存到临时表

            buff = new StringBuffer();
            BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.
                getOperDealCtx().getBean("cashacclinkdeal"); //账户链接
            operFun.setYssPub(pub);
            strYearMonth = YssFun.left(YssFun.formatDate(dDate), 4) + "00"; //赋值
            //YssType lAmount = new YssType();
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security"); //判断是否有分析代码
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            //analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            strRightType = YssOperCons.YSS_JYLX_DE; //权益类型为缩股 strRightType = "119";

            //操作子表
            buff.append(" select a.*, b.*,c.FTradeCury,e.FEXCHANGECODE,d.FPortCury from ( select ");
            buff.append(dbl.sqlIsNull("FSSecurityCode", "FTSecurityCode"));
            buff.append(" as FSecurityCode1,FTSecurityCode, FRecordDate, FEXRightDate,FPayDate,FPreTaxRatio," +
            		" FAfterTaxRatio,FRoundCode From ");
            buff.append(pub.yssGetTableName("tb_data_PreDeflationBonus")); //从缩股权益预处理表中获取权益数据
            buff.append(" where FExRightDate = ").append(dbl.sqlDate(dDate)); //权益处理时取除权日数据，做权益确认日处理
            buff.append(" and FCheckState = 1) a ");
            //--- MS01233  QDV4赢时胜(上海)2010年06月03日01_A 
            //---添加字段FEXCHANGECODE用于判断交易市场是国内市场还是国外市场  add by jiangshichao  2010.06.07
            //--- modify by wangzuochun 2010.07.11  MS01417    
            //---根据权益信息中的证券信息去查找证券信息维护中的交易所    QDV4上海2010年07月07日01_B    
            buff.append(" left join (select FSecurityCode,FEXCHANGECODE from "); 
            buff.append(pub.yssGetTableName("Tb_Para_Security")); //关联证券信息表
            buff.append(" where FCheckState = 1) e on a.fsecuritycode1 = e.FSecurityCode");
            buff.append(" join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Stock_Security")); //关联证券库存表
            buff.append(" where FPortCode in (" + operSql.sqlCodes(sPortCode)); //组合代码
            buff.append(") ");
            buff.append(" and FYearMonth<>").append(dbl.sqlString(strYearMonth)); //不是期初数库存
            buff.append(" and FCheckState=1 )b  on a.fsecuritycode1 = b.fsecuritycode and (case when e.fexchangecode " +
            		" in ('CY', 'CS', 'CG') then a.FRecordDate else a.FEXRightDate - 1 end) = b.FStorageDate "); 
            //取权益确认日库存数量
            buff.append(" left join (select FSecurityCode, FTradeCury from "); 
            buff.append(pub.yssGetTableName("Tb_Para_Security")); //关联证券信息表
            buff.append(" where FCheckState = 1) c on a.FTSecurityCode = c.FSecurityCode");
            buff.append(" left join (select FPortCode, FPortCury from ");
            buff.append(pub.yssGetTableName("Tb_Para_Portfolio")); //关联组合信息表
            buff.append(" where FCheckState = 1) d on b.FPortCode = d.FPortCode"); 
            //关取证券信息表和组合表，取出交易货币和组合货币。
          //----------------------------- MS01417 -----------------------------//
            
            rs = dbl.openResultSet_antReadonly(buff.toString());

            if (rs.next()) {
                rs.beforeFirst(); //返回第一个rs
                //--------------------拼接交易编号---------------------
                String strNumDate = YssFun.formatDatetime(dDate).
                    substring(0, 8);
                strNumDate = strNumDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_Trade"),
                                           dbl.sqlRight("FNUM", 6),
                        //将000000改为100000 缩股编号调整为100000开始  QDV4中保2010年07月02日01_B 合并太平版本代码调整
                                           "100000",
                                           " where FNum like 'T"
                        //改为1% 债券兑付编号调整为100000开始  QDV4中保2010年07月02日01_B 合并太平版本代码调整
                                           + strNumDate + "1%'", 1);
                strNumDate = "T" + strNumDate;
                //编号统一调整为分红、配股、缩股编号都从100000开始,下面几行代码逻辑是错误的会产生重复编号故注释掉 
                //合并太平版本代码调整  byleeyu 20100805
                //买入编号为200000开始，债券兑付从800000开始，卖出编号从900000开始
                //========add by xuxuming,20090819.MS00635.QDV4嘉实2009年08月14日01_B============
                //String sReplace = "8"; //替换为
                //strNumDate = YssFun.left(strNumDate, 9) +
                //    sReplace + YssFun.right(strNumDate, 5); //将编号第10位统一替换成'8'
                //===============================================================================
                //合并太平版本代码调整 byleeyu 20100805
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
                //---------------------------end-------------------------------//
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
                                            strRightType, //权益类型为缩股
                                            rs.getDate(
                                                "FRecordDate")); //权益确认日
                    //-------------------------end-----------------------------------------//
                    subTrade = new TradeSubBean(); //实例化
                    secSto = new SecurityStorageBean(); //实例化
                    secSto.setYssPub(pub);
                    /********************************************************
                     *  MS01233  QDV4赢时胜(上海)2010年06月03日01_A  add by jiangshichao 2010.06.07
                     *  若交易所为国内交易所，如交易所代码为：CG、CS、CY，即为国内业务，
                     *  则获取登记日当天相关证券的库存数量作为权益数量，
	                 *  若交易所为国外交易所，即为QDII普通业务，则获取除权日前一天的库存数量作为权益数量
                     */
                    if("CS".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))
                    	||"CG".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))
                    	||"CY".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))){
                    	StorageDate = rs.getDate("FRecordDate");
                    }else{
                    	//除权日前一天
                    	java.util.Date date = YssFun.parseDate(
                    			YssFun.formatDate(rs.getDate("FEXRightDate"), "yyyy-MM-dd"));
                    	StorageDate = YssFun.toSqlDate(YssFun.addDay(date, -1));
                    }
                    //此方法判断传入的日期当天的库存=传日日期前一天库存+当天的发生
                    secSto = secSto.getStorageCost(StorageDate, 
                            rs.getString("FSecurityCode1"), //证券代码
                            rs.getString("FPortCode"), //组合代码
                            (analy1 ?
                             rs.getString("FAnalysisCode1") :
                             " "), //分析代码1
                            (analy1 ?
                             rs.getString("FAnalysisCode2") :
                             " "), //分析代码2
                            "", "C",
                            rs.getString("FAttrClsCode")); //"C"为获取 核算成本
                    //--- MS01233  QDV4赢时胜(上海)2010年06月03日01_A end --------------------------------
                    if (secSto != null) {
                        //dSecurityCost = YssFun.toDouble(secSto.getStrStorageCost()); //为汇总的核算成本赋值
                        dSecurityAmount = YssFun.toDouble(secSto.getStrStorageAmount()); //为汇总的核算成本赋值
                    } else {
                        //dSecurityCost = 0.0; //为汇总的核算成本赋值
                        dSecurityAmount = 0.0; //为汇总的核算成本赋值
                    }
                    CtlPubPara pubPara = new CtlPubPara(); //通用参数实例化
                    pubPara.setYssPub(pub); //设置Pub
                    //获取通用参数
                    String rightsRatioMethods = (String) pubPara.getRightsRatioMethods(rs.getString("FPortCode"));
                    //按权益类型获取权益比例方式 panjunfang add 20100510 B股业务
                    String ratioMethodsDetail = 
                    	pubPara.getBRightsRatioMethods(rs.getString("FPortCode"),YssOperCons.YSS_JYLX_DE);
                    if(ratioMethodsDetail.length() > 0){
                    	rightsRatioMethods = ratioMethodsDetail;
                    }
                    if (dSecurityAmount > 0) { //判断证券数量是否大于0
                        dRightSub = this.getSettingOper().reckonRoundMoney( //缩股权益=确认日库存数量*权益比例
                            rs.
                            getString("FRoundCode") + "",
                            YssD.mul(dSecurityAmount,
                                     (rightsRatioMethods.equalsIgnoreCase("PreTaxRatio") ?
                                      //通过通用参数获取权益比例方式
                                      rs.getDouble("FPreTaxRatio") : rs.getDouble("FAfterTaxRatio"))));
                        caBean = cashacc.getCashAccountBean();
                        if (caBean != null) {
                            strCashAccCode = caBean.getStrCashAcctCode(); //现金账户
                        }else{
                            throw new YssException("系统执行缩股权益时出现异常！" + "\n" + "【" +
                                   rs.getString("FTSecurityCode") +
                                   "】证券缩股权益处理时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
                        }
                        //--------------------拼接交易编号---------------------
                        sNum++;
                        String tmp = "";
                        for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                            tmp += "0";
                        }
                        strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                        //------------------------end--------------------------//

                        dBaseRate = this.getSettingOper().getCuryRate(rs.getDate(
                            "FExRightDate"),
                            rs.getString("FTradeCury"), rs.getString("FPortCode"),
                            YssOperCons.YSS_RATE_BASE); //获取基础汇率的值
                        dPortRate = this.getSettingOper().getCuryRate(rs.getDate(
                            "FExRightDate"),
                            rs.getString("FPortCury"), rs.getString("FPortCode"),
                            YssOperCons.YSS_RATE_PORT); //获取组合汇率的值

                        subTrade.setNum(strNumDate); //为交易编号赋值

                        subTrade.setSecurityCode(rs.getString("FTSecurityCode")); //标的证券代码赋值

                        tSecurityCode += rs.getString("FTSecurityCode") + ",";//赋值

                        subTrade.setPortCode(rs.getString("FPortCode")); //组合代码
                        
                    	//============add by xuxuming,2010.01.15.MS00932    缩股、分红权益处理时，
                        //生成业务资料属性分类没有做判断   缩股时，先查询当天是否有指数调整信息。＝＝＝＝
                        //===============如果有调整，以调整之后的属性分类作为缩股的属性分类＝＝＝＝＝＝＝＝＝＝＝＝＝＝
                        String strSqlSec = "select * from "+ pub.yssGetTableName("Tb_Data_Integrated")+
                        " where FPortCode in ("+dbl.sqlString(rs.getString("FPortCode"))+
                        ") and FSecurityCode='"+rs.getString("FSecurityCode1")+
                        "' and FEXCHANGEDATE = " + dbl.sqlDate(rs.getDate("FRecordDate")) +
                      //只查询流入的数据。有这只证券的流入，则已流入的所属分类作为缩股的属性分类
                        " and FTradeTypeCode='101' and FInOutType='1'";
                        ResultSet rsSec = null;
                        String strSecAttrCls="";
                        rsSec = dbl.openResultSet(strSqlSec);
                        if(rsSec.next()){//一天内，一只证券最多只有一笔'成分股转换'类型的流入数据，故用IF
                        	strSecAttrCls= rsSec.getString("FAttrClsCode");
                        }
                        rsSec.close();
                        //=========================end==========================================
                        //===========add by xuxuming,2010.01.15.MS00932    缩股、分红权益处理时，
                        //生成业务资料属性分类没有做判断   当天有指数信息调整，以调整后的类型作为缩股的类型
                        if(strSecAttrCls!=null&&strSecAttrCls.trim().length()>0){
                        	subTrade.setAttrClsCode(strSecAttrCls);
						} else {
							// //2010.01.07.增加属性分类，MS00903
							subTrade.setAttrClsCode(rs.getString("FAttrClsCode") != null ? 
									rs.getString("FAttrClsCode") : " ");
						}
                        //=================end==========================================
                        
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

                        subTrade.setOldAllotAmount(0); //原始分配数量

                        subTrade.setAllotFactor(0); //分配因子

                        subTrade.setBargainDate(YssFun.formatDate(rs.getDate(
                            "FExRightDate"))); //成交日期

                        subTrade.setBargainTime("00:00:00"); //成交时间

                        subTrade.setSettleDate(YssFun.formatDate(rs.getDate(
                            "FPayDate"))); //结算日期

                        subTrade.setSettleTime("00:00:00"); //结算时间

                        subTrade.setAutoSettle(new Integer(1).toString()); //自动结算

                        subTrade.setPortCuryRate(dPortRate); //组合汇率

                        subTrade.setBaseCuryRate(dBaseRate); //基础汇率

                        //subTrade.setTradeAmount(rs.getDouble("FStorageAmount"));
                        subTrade.setTradeAmount(dRightSub); //交易数量

                        subTrade.setTradePrice(0); //交易价格

                        subTrade.setTradeMoney(0); //交易金额

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

                        subTrade.creatorTime = YssFun.formatDatetime(new java.util.
                            Date()); //创建时间

                        subTrade.setTotalCost(0); //投资总成本

                        subTrade.setFactSettleDate(YssFun.formatDate(rs.getDate(
                            "FPayDate"))); //实际结算日期

                        subTrade.setMatureDate("9998-12-31");//到期日期

                        subTrade.setMatureSettleDate("9998-12-31");//到期结算日期

                        subTrade.setSettleState(new Integer(0).toString()); //结算状态，未结算“0”

                        subTrade.setFactCashAccCode(strCashAccCode); //实际结算帐户

                        subTrade.setCashAcctCode(strCashAccCode); //设置现金账户

                        subTrade.setFactSettleMoney(0); //实际结算金额

                        subTrade.setExRate(1); //兑换汇率

                        subTrade.setFactPortRate(rs.getDouble("FPortCuryRate")); //实际结算组合汇率

                        subTrade.setFactBaseRate(rs.getDouble("FBaseCuryRate")); //实际结算基础汇率

                        reArr.add(subTrade); //把数据保存到集合中
                    }
                }
                strDealInfo = "true"; //表示有权益数据
            } else {
                strDealInfo = "no"; //表示无权益数据
            }
            return reArr;
        } catch (Exception e) {
            strDealInfo = "false";
            throw new YssException("计算缩股权益处理出错！",e);
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
     * shashijie 2011-08-09 STORY 1434 ,  国内权益处理
     */
    public void saveRightEquitys(ArrayList alRightEquitys, java.util.Date dDate, 
    		String sPortCode) throws YssException {
        ArrayList newAlRightEquity = null;
        try {
            if (alRightEquitys != null && alRightEquitys.size() > 0) {
            	//EDIT by lidaolong 20110407 #536 有关国内接口数据处理顺序的变更
                newAlRightEquity = checkSubTradeHaveRightData(alRightEquitys, 
                		YssOperCons.YSS_JYLX_DE, "ZD_QY,ZD_QY_T+1", dDate, sPortCode, this.tSecurityCode);
                super.saveRightEquitys(newAlRightEquity, dDate, sPortCode); //调用基类保存数据方法
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
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
     * @param tSecurityCode String 证券代码
     * @return ArrayList 返回值
     * @throws YssException
     */
    public ArrayList checkSubTradeHaveRightData(ArrayList alRightEquitys, String sTradeType, String sDsType,
              java.util.Date dDate, String sPortCode, String tSecurityCode) throws YssException {
        ArrayList newAlRightEquity = null;
        try {
            newAlRightEquity = super.checkSubTradeHaveRightData(alRightEquitys, sTradeType,
            		sDsType, dDate, sPortCode, tSecurityCode);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return newAlRightEquity;
    }
    
    /**
     * 删除条件，设置删除交易主子表中数据的条件
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return TradeBean 返回值
     * shashijie 2011-08-09 STORY 1434 ,  国内权益处理
     */
    public TradeBean filterBean(java.util.Date dDate, String sPortCode) {
        TradeBean trade = new TradeBean();
        trade.setTradeCode(YssOperCons.YSS_JYLX_DE); //交易方式为“119”缩股
        trade.setPortCode(sPortCode); //组合代码
        trade.setBargainDate(YssFun.formatDate(dDate)); //成交日期
        trade.setDsType("HD_QY");//操作类型，表示此数据时界面输入的数据
        return trade;
    }

    /**
     * 此方法做缩股数据的预处理，把处理数据保存到临时表tb_pub_data_PreDeflationBonus
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @throws YssException 异常
     * shashijie 2011-08-09 STORY 1434 ,  国内权益处理
     */
    private void doDataPretreatment(java.util.Date dDate, String sPortCode) throws YssException {
        StringBuffer buff = null; //拼接sql语句
        ResultSet rs = null; //声明结果集
        DeflationBonusBean bonusShare = null; //缩股的javaBean
        ArrayList bonusShareData = new ArrayList(); //保存预处理后的缩股权益数据
        try {
        	createTmpTable(); //创建缩股临时表

        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        	String strSql = " truncate table " + pub.yssGetTableName("tb_data_PreDeflationBonus");
        	dbl.executeSql(strSql);
        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            
            /**
             * 以下sql语句处理缩股权益数据，有组合群和组合代码
             */
            buff = new StringBuffer();
            buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_DeflationBonus")); //缩股权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and (FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB())); //组合群代码
            buff.append(" or FAssetGroupCode = ").append(dbl.sqlString(" ")).append(" ) ");
            buff.append(" and FPortCode =").append(dbl.sqlString(sPortCode)); //组合代码

            rs = dbl.openResultSet(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                bonusShare = new DeflationBonusBean();
                bonusShare.setYssPub(pub);
                sSecurityCode += rs.getString("FSSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                bonusShare.setSecurityCode(rs.getString("FTSecurityCode")); //目标证券
                bonusShare.setSSecCode(rs.getString("FSSecurityCode")); //原证券代码
                bonusShare.setRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                bonusShare.setExrightDate(rs.getDate("FExRightDate").toString()); //除权日
                bonusShare.setPayDate(rs.getDate("FPayDate").toString()); //到帐日
                bonusShare.setAfficheDate(rs.getDate("FAfficheDate").toString()); //公告日
                bonusShare.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                bonusShare.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                bonusShare.setPortCode(rs.getString("FPortCode")); //组合代码
                bonusShare.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                bonusShare.setRoundCode(rs.getString("FRoundCode")); //舍入代码
                bonusShare.setDesc(rs.getString("FDesc")); //描述
                bonusShareData.add(bonusShare); //把数据保存到集合中
            }
            saveIntoTmpTable(bonusShareData); //保存数据到缩股临时表
            dbl.closeResultSetFinal(rs); //关闭游标
            bonusShareData.clear(); //清空集合

            /**
             * 以下sql语句处理缩股权益数据，有组合群,但没有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_DeflationBonus")); //缩股权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            
            //20120604 modified by liubo.Bug #4714
            //选择了多组合群的的配股数据，权益处理后没有产生交易数据
            //=============================
//            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB())); //组合群代码
            buff.append(" and FAssetGroupCode like ").append(dbl.sqlString("%" + pub.getPrefixTB() + "%")); //组合群代码
            //===========end==================
            
            buff.append(" and FPortCode =").append(dbl.sqlString(" ")); //组合代码
            buff.append(sSecurityCode.trim().length() != 0 ? " and FSSecurityCode not in(" +
            		this.operSql.sqlCodes(sSecurityCode) + ")" : "");//证券代码

            rs = dbl.openResultSet(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                bonusShare = new DeflationBonusBean();
                bonusShare.setYssPub(pub);
                sSecurityCode += rs.getString("FSSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                bonusShare.setSecurityCode(rs.getString("FTSecurityCode")); //目标证券
                bonusShare.setSSecCode(rs.getString("FSSecurityCode")); //原证券代码
                bonusShare.setRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                bonusShare.setExrightDate(rs.getDate("FExRightDate").toString()); //除权日
                bonusShare.setPayDate(rs.getDate("FPayDate").toString()); //到帐日
                bonusShare.setAfficheDate(rs.getDate("FAfficheDate").toString()); //公告日
                bonusShare.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                bonusShare.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                bonusShare.setPortCode(sPortCode); //组合代码
                bonusShare.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                bonusShare.setRoundCode(rs.getString("FRoundCode")); //舍入代码
                bonusShare.setDesc(rs.getString("FDesc")); //描述
                bonusShareData.add(bonusShare); //把数据保存到集合中
            }
            saveIntoTmpTable(bonusShareData); //保存数据到缩股临时表
            dbl.closeResultSetFinal(rs); //关闭游标
            bonusShareData.clear(); //清空集合

            /**
             * 以下sql语句处理缩股权益数据，没有组合群,也没有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_DeflationBonus")); //缩股权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(" ")); //组合群代码
            buff.append(" and FPortCode =").append(dbl.sqlString(" ")); //组合代码
            buff.append(sSecurityCode.trim().length() != 0 ? " and FSSecurityCode not in(" + 
            		this.operSql.sqlCodes(sSecurityCode) + ")" : "");//证券代码

            rs = dbl.openResultSet(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                bonusShare = new DeflationBonusBean();
                bonusShare.setYssPub(pub);
                sSecurityCode += rs.getString("FSSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                bonusShare.setSecurityCode(rs.getString("FTSecurityCode")); //目标证券
                bonusShare.setSSecCode(rs.getString("FSSecurityCode")); //原证券代码
                bonusShare.setRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                bonusShare.setExrightDate(rs.getDate("FExRightDate").toString()); //除权日
                bonusShare.setPayDate(rs.getDate("FPayDate").toString()); //到帐日
                bonusShare.setAfficheDate(rs.getDate("FAfficheDate").toString()); //公告日
                bonusShare.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                bonusShare.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                bonusShare.setPortCode(sPortCode); //组合代码
                bonusShare.setAssetGroupCode(pub.getPrefixTB()); //组合群代码
                bonusShare.setRoundCode(rs.getString("FRoundCode")); //舍入代码
                bonusShare.setDesc(rs.getString("FDesc")); //描述
                bonusShareData.add(bonusShare); //把数据保存到集合中
            }
            saveIntoTmpTable(bonusShareData); //保存数据到缩股临时表

        } catch (Exception e) {
            throw new YssException("缩股数据的预处理，把处理数据保存到临时表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 保存数据到缩股临时表
     * @param DeflationBonusData ArrayList 保存数据的集合
     * @throws YssException 异常
     * shashijie 2011-08-09 STORY 1434 ,  国内权益处理
     */
    private void saveIntoTmpTable(ArrayList bonusShareData) throws YssException {
        StringBuffer buff = null; //做拼接SQL语句
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement pst = null;
        //=============end====================
        DeflationBonusBean bonusShare = null; //声明缩股bean
        try {
            buff = new StringBuffer();
            buff.append(" insert into ").append(pub.yssGetTableName("tb_data_PreDeflationBonus")); //缩股权益预处理表
            buff.append(" (");
            buff.append("FTSecurityCode,FSSecurityCode,FRecordDate,FExRightDate,FPayDate,");
            buff.append("FAfficheDate,FPreTaxRatio,FAfterTaxRatio,");
            buff.append(" FPortCode,FAssetGroupCode,FRoundCode,FDesc,");
            buff.append("FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime");
            buff.append(")");
            buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			//modified by liubo.Story #2145
			//==============================
//            pst = dbl.openPreparedStatement(buff.toString());
            pst = dbl.getYssPreparedStatement(buff.toString());
			//==============end================
            for (int i = 0; i < bonusShareData.size(); i++) { //循环保存数据的集合
                bonusShare = (DeflationBonusBean) bonusShareData.get(i); //获取实例bean
                pst.setString(1, bonusShare.getSecurityCode()); //证券代码
                pst.setString(2, bonusShare.getSSecCode()); //标的证券
                pst.setDate(3, YssFun.toSqlDate(bonusShare.getRecordDate())); //权益确认日
                pst.setDate(4, YssFun.toSqlDate(bonusShare.getExrightDate())); //除权日
                pst.setDate(5, YssFun.toSqlDate(bonusShare.getPayDate())); //到帐日；
                pst.setDate(6, YssFun.toSqlDate(bonusShare.getAfficheDate())); //公告日
                pst.setDouble(7, Double.parseDouble(bonusShare.getPreTaxRatio())); //税前权益比例
                pst.setDouble(8, Double.parseDouble(bonusShare.getAfterTaxRatio())); //税后权益比例
                pst.setString(9, bonusShare.getPortCode()); //组合代码
                pst.setString(10, bonusShare.getAssetGroupCode()); //组合群代码
                pst.setString(11, bonusShare.getRoundCode()); //舍入代码
                pst.setString(12, bonusShare.getDesc()); //描述
                pst.setInt(13, 1); //审核状态
                pst.setString(14, pub.getUserCode()); //创建人
                pst.setString(15, YssFun.formatDatetime(new java.util.Date())); //创建时间
                pst.setString(16, pub.getUserCode()); //审核人
                pst.setString(17, YssFun.formatDatetime(new java.util.Date())); //审核时间
                pst.executeUpdate();
            }
        } catch (Exception e) {
            throw new YssException("保存数据到缩股临时表出错！", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }

    /**
     * createTmpTable创建缩股临时表 tb_pub_data_PreDeflationBonus
     * shashijie 2011-08-09 STORY 1434 ,  国内权益处理
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
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("tb_data_PreDeflationBonus"));
            buff.append(" ( ");
            buff.append(" FTSECURITYCODE  VARCHAR2(50)  NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FSSECURITYCODE  VARCHAR2(50)      NULL, ");
            buff.append(" FEXRIGHTDATE    DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NULL,");
            buff.append(" FPAYDATE        DATE              NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_PreDeflation"));
            //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
            //buff.append(" PRIMARY KEY (FTSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE) ");
            buff.append(" PRIMARY KEY (FTSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE,FPAYDATE) ");
            //-----------------------------------
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_PreDeflationBonus".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_PreDeflationBonus"))) { 
        				dbl.executeSql(" drop table " + pub.yssGetTableName("tb_data_PreDeflationBonus"));
        			}

                    dbl.executeSql(buff.toString());
        		}
        	}else{
        		dbl.executeSql(buff.toString());
        	}
        	
        	buff.delete(0, buff.length());
        	//---edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        } catch (Exception e) {
            throw new YssException("创建缩股临时表出错！", e);
        }
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
    }
}
