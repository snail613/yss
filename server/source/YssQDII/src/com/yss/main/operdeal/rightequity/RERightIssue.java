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
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.TradeDataAdmin;

/**
 *
 * <p>Title: xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理</p>
 * <p>Description:计算配股数量  </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */
public class RERightIssue
    extends BaseRightEquity {
    private String sSecurityCode="";//保存证券代码
    private String tSecurityCode = ""; //保存送配证券代码
	//story 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A add by zhouwei 20120517 成交数量
    private double tradeAmount=0;
    CashPayRecAdmin prAdmin =null;

    public RERightIssue() {
    }
    /**
     * 做配股权益业务处理，产生业务资料数据保存到业务资料javaBean中
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return ArrayList 返回值
     * @throws YssException 异常
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    public ArrayList getDayRightEquitys(java.util.Date dDate,String sPortCode) throws
        YssException {        StringBuffer buff = null;//sql语句的拼接
        double dSecurityAmount = 0; //证券数量
        double dSecurityCost = 0; //证券成本
        double dRight = 0; //权益（主表）
        double dRightSub = 0; //权益（子表）
        String strRightType = ""; //权益类型
        String strCashAccCode = " "; //现金帐户
        String strYearMonth = "";//保存截取日期的年和天
        CashAccountBean caBean = null;//声明现金账户的bean
        String[] sFee = null;
        boolean analy1;//分析代码1
        boolean analy2;//分析代码2
        boolean analy3;//分析代码3
        double dTradeMoney = 0;//成交金额
        double dBaseRate = 1;//基础汇率
        double dPortRate = 1;//组合汇率
        TradeSubBean subTrade = null;//交易子表的javaBean
        YssCost cost = null;//声明成本
        ArrayList reArr = new ArrayList();
        ResultSet rs = null;//声明结果集
        SecurityStorageBean secSto = null;//证券库存的javaBean
        long sNum=0;//为了产生的编号不重复
        Date StorageDate = null; //MS01233  QDV4赢时胜(上海)2010年06月03日01_A add by jiangshichao
        Date dateTradeDate=null;//这里增加对交易日期的处理   STORY #431 有关权益处理国内的配股缴款数据的处理变更 add by nimengjing 2010.12.23
        try {
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B start---//
        	TradeDataAdmin tradeData = new TradeDataAdmin();//交易数据操作类
        	tradeData.setYssPub(pub);
        	tradeData.delete("", dDate,dDate,null,null,"",sPortCode, "", 
        			"",YssOperCons.YSS_JYLX_QZSP + "," + YssOperCons.YSS_JYLX_PG + "," + YssOperCons.YSS_JYLX_PGJK, "", "",false, "HD_QY");
        	//---add by songjie 2012.04.16 BUG 4212 QDV4赢时胜(上海)2012年04月05日05_B end---//
        	
            this.doDataPretreatment(dDate,sPortCode);//配股权益数据的预处理，主要是考虑跨组合群，组合的处理

            buff=new StringBuffer();
            BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.
                getOperDealCtx().getBean("cashacclinkdeal");//账户链接
            operFun.setYssPub(pub);
            strYearMonth = YssFun.left(this.strOperStartDate, 4) + "00";//赋值
            //YssType lAmount = new YssType();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            String fees = "";

            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");//判断是否有分析代码
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            
            strDealInfo = "no";
            //操作子表
            //------ modify by wangzuochun 2010.07.12  MS01417    根据权益信息中的证券信息去查找证券信息维护中的交易所    QDV4上海2010年07月07日01_B    
            buff.append("select a.*, b.*,c.FTradeCury,e.FEXCHANGECODE,d.FPortCury" +
            //modify by zhangfa 20100929 MS01815    库存所属分类为EQ时，做配股权益应产生对应所属分类的权证    QDV4赢时胜(测试)2010年09月26日03_B  
		    ",c.FCatCode as FTAttrClsCode "+
		    //---------------------------------------------------------------------------------------------------------------------------
            		" from ( select FSecurityCode,FTSecurityCode, FRecordDate,");
            buff.append(" FEXRightDate, FExpirationDate, FPayDate, FRIPrice, FPreTaxRatio,FAfterTaxRatio,FRoundCode," +
            		 //MS01354    add by zhangfa 20100716    QDV4赢时胜(上海)2010年06月25日01_A   
            		"FTradeCode "+
            		//------------------------------------------------------------------------
            		" from ");
            buff.append(pub.yssGetTableName("tb_data_PreRightsissue"));//从配股权益预处理表中获取权益数据
            // modify by nimengjing 2010.12.23 STORY #431 有关权益处理国内的配股缴款数据的处理变更
            //edit by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A
			buff.append(" where FEXRightDate = ").append(dbl.sqlDate(dDate));//权益处理时取除权日数据，做权益确认日处理
            //----------------------------------end--STORY #431--------------------------------------------------------------- 
            //MS01354    add by zhangfa 20100720    QDV4赢时胜(上海)2010年06月25日01_A  
             // buff.append(" and FExpirationDate >").append(dbl.sqlDate(dDate));//缴款截止日大于除权日即操作当天为除权日期
            //------------------------------------------------------------------------------------------------------
            buff.append(" and FCheckState = 1) a ");
            //--- MS01233  QDV4赢时胜(上海)2010年06月03日01_A 添加字段FEXCHANGECODE用于判断交易市场是国内市场还是国外市场  add by jiangshichao  2010.06.07
            buff.append(" left join (select FSecurityCode,FEXCHANGECODE from "); 
            buff.append(pub.yssGetTableName("Tb_Para_Security")); //关联证券信息表
            buff.append(" where FCheckState = 1) e on a.fsecuritycode = e.FSecurityCode");
            
            buff.append(" join (select * from ");
            buff.append(pub.yssGetTableName("Tb_Stock_Security"));//关联证券库存表
            buff.append(" where FPortCode in (" + operSql.sqlCodes(sPortCode)).append(") ");//组合代码
            buff.append(" and FYearMonth<>").append(dbl.sqlString(strYearMonth));//不是期初数库存
            buff.append(" and FCheckState=1 )b  on a.fsecuritycode = b.fsecuritycode and (case when e.fexchangecode in ('CY', 'CS', 'CG') then a.FRecordDate else a.FEXRightDate - 1 end) = b.FStorageDate ");//取权益确认日的库存数量
            //--- MS01233  QDV4赢时胜(上海)2010年06月03日01_A 添加字段FEXCHANGECODE用于判断交易市场是国内市场还是国外市场  add by jiangshichao  2010.06.07
            buff.append(" left join (select FSecurityCode, FTradeCury,FEXCHANGECODE " +
            //modify by zhangfa 20100929 MS01815    库存所属分类为EQ时，做配股权益应产生对应所属分类的权证    QDV4赢时胜(测试)2010年09月26日03_B  
            " ,FCatCode from ");
            //---------------------------------------------------------------------------------------------------------------------------

            buff.append(pub.yssGetTableName("Tb_Para_Security"));//关联证券信息表
            buff.append(" where FCheckState = 1) c on a.FTSecurityCode = c.FSecurityCode");
          //modify by zhangfa 20101014 MS01850    启用日期引起的多比数据    QDV4上海(33测试)2010年10月11日01_B
            buff.append(" left join (select FPortCode, FPortCury  from ");
            buff.append(pub.yssGetTableName("Tb_Para_Portfolio"));//关联组合信息表
            buff.append(" where FCheckState = 1 ) d on b.FPortCode = d.FPortCode");//关取证券信息表和组合表，取出交易货币和组合货币。
            //--------------------------MS01850-------------------------------------------------------
            
            //--------------------------------MS01417-----------------------------------//
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
                                           "100000",//将000000改为100000 配股编号调整为100000开始  QDV4中保2010年07月02日01_B 合并太平版本代码调整
                                           " where FNum like 'T"
                                           + strNumDate + "1%'", 1);//改为1% 配股编号调整为100000开始  QDV4中保2010年07月02日01_B 合并太平版本代码调整
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
                //---------------------------end-----------------------//
                while (rs.next()) {
                	dateTradeDate=rs.getDate("FEXRightDate");//这里先对交易日期做总的处理，将其设为除权日日期 add by nimengjing 2010.12.23 STORY #431 有关权益处理国内的配股缴款数据的处理变更
                	//MS01354    add by zhangfa 20100716    QDV4赢时胜(上海)2010年06月25日01_A 
					//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
//               	 if(rs.getString("FTradeCode")!=null&&rs.getString("FTradeCode").length()!=0&&rs.getString("FTradeCode").equalsIgnoreCase("08")){
//               		 strRightType=rs.getString("FTradeCode");
//               	 }else if(rs.getString("FTradeCode")!=null&&rs.getString("FTradeCode").length()!=0&&rs.getString("FTradeCode").equalsIgnoreCase("23")){
//               		 strRightType=rs.getString("FTradeCode");
//               		 //add by nimengjing 2010.12.23 STORY #431 有关权益处理国内的配股缴款数据的处理变更
//                     if("CS".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))||"CG".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))
//                         	||"CY".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))){
//                         	dateTradeDate= rs.getDate("FPayDate");//这里增加对交易日期的处理   如果为国内配股缴款业务交易日期为到账日期  
//                         }
//                     //-----------------------------------------end STORY #431----------------------------------------------
//               	 }
//               	 else {
//               		 strRightType = YssOperCons.YSS_JYLX_QZSP; //strRightType = "08";//权证送配
//               	 }
					//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
                	//---edit by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
                    /*若配股模式为先缴款后除权，则应判断已审核的交易数据中是否存在该证券交
                	 	易方式为配股缴款、权益登记日<= 成交日期 <= 缴款日 的交易数据*/
                	if("0".equals(rs.getString("FTradeCode")) && !checkSubTrade(rs.getString("FPortCode"), rs.getString("FTSecurityCode"), 
                			rs.getDate("FRecordDate"), rs.getDate("FExpirationDate"))){
							continue;
                	}   
					//---edit by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//           	
               	//-----------------------------------------------------------------------
                    //-------------------------设置现金账户链接属性值----------------------
                    cashacc.setYssPub(pub);
                    cashacc.setLinkParaAttr(analy1 ?
                                            rs.getString("FAnalysisCode1") :
                                            " ", //投资经理
                                            rs.getString("FPortCode"), //组合代码
                                            rs.getString("FTSecurityCode"), //证券代码
                                            (analy2 ?
                                             rs.getString("FAnalysisCode2") :
                                             " "), //券商
                                            strRightType, //交易类型为 权证送配
                                            rs.getDate("FRecordDate")); //权益确认日
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
                    	java.util.Date date = YssFun.parseDate(YssFun.formatDate(rs.getDate("FEXRightDate"), "yyyy-MM-dd"));
                    	StorageDate = YssFun.toSqlDate(YssFun.addDay(date, -1));
                    }
					//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
//                    secSto = secSto.getStorageCost(StorageDate,
//                            rs.getString("FSecurityCode"),//证券代码
//                            rs.getString("FPortCode"),//组合代码
//                            (analy1 ?
//                             rs.getString("FAnalysisCode1") :
//                             " "),//分析代码1
//                            (analy1 ?
//                             rs.getString("FAnalysisCode2") :
//                             " "),//分析代码2
//                            "", "C",
//                            rs.getString("FAttrClsCode")); //"C"为获取 核算成本
					//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
//                    secSto = secSto.getStorageCost(rs.getDate("FRecordDate"),//权益确认日
//                        rs.getString("FSecurityCode"),//证券代码
//                        rs.getString("FPortCode"),//组合代码
//                        (analy1 ?
//                         rs.getString("FAnalysisCode1") :
//                         " "),//分析代码1
//                        (analy1 ?
//                         rs.getString("FAnalysisCode2") :
//                         " "),//分析代码2
//                        "", "C",
//                        rs.getString("FAttrClsCode")); //"C"为获取 核算成本
					//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
//                    if (secSto != null) {
//                        dSecurityCost = YssFun.toDouble(secSto.getStrStorageCost()); //为汇总的核算成本赋值
//                        dSecurityAmount = YssFun.toDouble(secSto.getStrStorageAmount()); //为汇总的库存数量赋值
//                    } else {
//                        dSecurityCost = 0.0;//为汇总的核算成本赋值
//                        dSecurityAmount = 0.0;//为汇总的库存数量赋值
//                    }
					//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
					//---add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
                    dSecurityAmount=rs.getDouble("fstorageAmount");
                    dSecurityCost=rs.getDouble("fstorageCost");
					//---add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
                    CtlPubPara pubPara = new CtlPubPara(); //通用参数实例化
                    pubPara.setYssPub(pub); //设置Pub
                    String rightsRatioMethods = (String) pubPara.getRightsRatioMethods(rs.getString("FPortCode")); //获取通用参数值
                    String ratioMethodsDetail = pubPara.getBRightsRatioMethods(rs.getString("FPortCode"),YssOperCons.YSS_JYLX_QZSP);//按权益类型获取权益比例方式 panjunfang add 20100510 B股业务
                    if(ratioMethodsDetail.length() > 0){
                    	rightsRatioMethods = ratioMethodsDetail;
                    }
                    if (dSecurityAmount > 0) {//判断证券数量是否大于0
                        dRightSub = this.getSettingOper().reckonRoundMoney(//配股权益=确认日库存数量*权益比例
                            rs.
                            getString("FRoundCode") + "",
                            YssD.mul(dSecurityAmount,
                                     (rightsRatioMethods.equalsIgnoreCase("PreTaxRatio")?
                                      rs.getDouble("FPreTaxRatio"):rs.getDouble("FAfterTaxRatio"))));//通过通用参数获取权益比例方式
                        caBean = cashacc.getCashAccountBean();
                        if (caBean != null) {
                            strCashAccCode = caBean.getStrCashAcctCode();//现金账户
                        } else {
                            throw new YssException("系统执行配股权益时出现异常！" + "\n" + "【" +
                                rs.getString("FTSecurityCode") +
                                "】证券配股权益处理时没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
                        }
                        //--------------------拼接交易编号---------------------
                        sNum++;
                        String tmp = "";
                        for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                            tmp += "0";
                        }
                        strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                        //------------------------end--------------------------//
                        //modify by nimengjing 2010.12.23 STORY #431 有关权益处理国内的配股缴款数据的处理变更
                        dBaseRate = this.getSettingOper().getCuryRate(dateTradeDate,
                            rs.getString("FTradeCury"), rs.getString("FPortCode"),
                            YssOperCons.YSS_RATE_BASE);//获取基础汇率的值
                        dPortRate = this.getSettingOper().getCuryRate(dateTradeDate,
                            rs.getString("FPortCury"), rs.getString("FPortCode"),
                            YssOperCons.YSS_RATE_PORT);//获取组合汇率的值
                        //-------------------end STORY #431-------------------------------
                        subTrade.setNum(strNumDate);//为交易编号赋值
                 
                        subTrade.setSecurityCode(rs.getString("FTSecurityCode"));//配股权证代码赋值

                        tSecurityCode += rs.getString("FTSecurityCode") + ",";//赋值

                        subTrade.setPortCode(rs.getString("FPortCode"));//组合代码
                    	//============add by panjunfang,2010.03.11.MS00932    送股、分红权益处理时，生成业务资料属性分类没有做判断   送股时，先查询当天是否有指数调整信息。＝＝＝＝
                        //===============如果有调整，以调整之后的属性分类作为送股的属性分类＝＝＝＝＝＝＝＝＝＝＝＝＝＝
                        String strSqlSec = "select * from "+ pub.yssGetTableName("Tb_Data_Integrated")+
                        " where FPortCode in ("+dbl.sqlString(rs.getString("FPortCode"))+
                        ") and FSecurityCode='"+rs.getString("FSecurityCode")+
                        "' and FEXCHANGEDATE = " + dbl.sqlDate(rs.getDate("FRecordDate")) +
                        " and FTradeTypeCode='101' and FInOutType='1'";//只查询流入的数据。有这只证券的流入，则已流入的所属分类作为送股的属性分类
                        ResultSet rsSec = null;
                        String strSecAttrCls="";
                        rsSec = dbl.queryByPreparedStatement(strSqlSec);
                        if(rsSec.next()){//一天内，一只证券最多只有一笔'成分股转换'类型的流入数据，故用IF
                        	strSecAttrCls= rsSec.getString("FAttrClsCode");
                        }
                        rsSec.close();
                        //=========================end==========================================
                        //===========add by panjunfang,2010.03.11.MS00932    送股、分红权益处理时，生成业务资料属性分类没有做判断   当天有指数信息调整，以调整后的类型作为送股的类型                
                        if(strSecAttrCls!=null&&strSecAttrCls.trim().length()>0){
                        	subTrade.setAttrClsCode(strSecAttrCls);
                        }else{
						//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
//                        	//modify by zhangfa 20100929 MS01815    库存所属分类为EQ时，做配股权益应产生对应所属分类的权证    QDV4赢时胜(测试)2010年09月26日03_B    
//                        	if(rs.getString("FTradeCode").equalsIgnoreCase("22")){
//                        		//modify by zhangfa 20101014 MS01850    启用日期引起的多比数据    QDV4上海(33测试)2010年10月11日01_B  
//                        		/**
//                        		if(rs.getString("FAttrClsCode")!=null&&rs.getString("FAttrClsCode").equals("EQ")){
//                        			 subTrade.setAttrClsCode(rs.getString("FTAttrClsCode")!=null?rs.getString("FTAttrClsCode"):" ");////2010.01.07.增加属性分类，MS00903
//                        		}else{
//                        			subTrade.setAttrClsCode(" ");
//                        		}
//                        		*/
//                        		subTrade.setAttrClsCode(" ");
//                        		//------------------MS01850-------------------------------------------------------------------
//                        	}else{
//	                        	
//                        	}
						//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
                        	//-------------------------------------------------------------------------------------------------------------------------
                        	
                        	/**Start 20131231 deleted by liubo.Bug #86337.QDV4建行2013年12月25日01_B
                        	 * 不应该为配股的交易数据设置，否则统计库存时，同一只权证会产生两笔库存数据*/
//                        	subTrade.setAttrClsCode(rs.getString("FAttrClsCode")!=null?rs.getString("FAttrClsCode"):" ");////2010.01.07.增加属性分类，MS00903
                        	/**End 20131231 deleted by liubo.Bug #86337.QDV4建行2013年12月25日01_B*/
                        }
                        //=================end==========================================
                        
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
                        //---add by zhouwei 20120517 story 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
						//需求前的数据默认为先除权后缴款处理
						cost = new YssCost();
                    	if("08".equals(rs.getString("FTradeCode")) || "22".equals(rs.getString("FTradeCode")) 
                    			|| "23".equals(rs.getString("FTradeCode")) || "1".equals(rs.getString("FTradeCode"))){
                            subTrade.setTradeAmount(dRightSub);//交易数量
                    		strRightType = YssOperCons.YSS_JYLX_QZSP; 
                    		subTrade.setTradePrice(0);// 交易价格                   		
							subTrade.setTradeMoney(0);// 交易金额

                    	}else if("0".equals(rs.getString("FTradeCode"))){//先缴款后除权
                    		strRightType = "08";//配股 
                    		//edit by songjie 2012.08.17 STORY # 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A 
                    		subTrade.setTradeAmount(this.tradeAmount);//交易数量 = 配股缴款交易数据中的交易数量
                    		subTrade.setTradePrice(Double.valueOf(
									rs.getString("FRIPrice"))
									.doubleValue());
                    		subTrade.setTradeMoney(subTrade.getTradePrice()
									* subTrade.getTradeAmount());
                    	}
						subTrade.setTotalCost(0);//投资总成本
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
						//---add by zhouwei 20120517 story 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
                        subTrade.setTradeCode(strRightType);//交易类型

                        subTrade.setTailPortCode(strCashAccCode);//尾差组合代码

                        subTrade.setAllotProportion(0);//分配比例

                        subTrade.setOldAllotAmount(0);//原始分配数量

                        subTrade.setAllotFactor(0);//分配因子

                        subTrade.setBargainDate(YssFun.formatDate(dateTradeDate));//成交日期 modify by nimengjing 2010.12.23 STORY #431 有关权益处理国内的配股缴款数据的处理变更

                        subTrade.setBargainTime("00:00:00");//成交时间

                        subTrade.setSettleDate(YssFun.formatDate(rs.getDate(
                            "FPayDate")));//结算日期

                        subTrade.setSettleTime("00:00:00");//结算时间

                        subTrade.setAutoSettle(new Integer(1).toString());//自动结算

                        subTrade.setPortCuryRate(dPortRate);//组合汇率

                        subTrade.setBaseCuryRate(dBaseRate);//基础汇率
                      //MS01354    add by zhangfa 20100716    QDV4赢时胜(上海)2010年06月25日01_A  
                      //modify by nimengjing 2011.1.7 STORY #431 有关权益处理国内的配股缴款数据的处理变更
                        //Date expirationDate=rs.getDate("FExpirationDate");
                        
                        
						//if (expirationDate.getTime() > YssFun.toSqlDate(dDate)
								//.getTime()) {//国外情况
                        //---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
//                        if("CS".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))||"CG".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))
//                            	||"CY".equalsIgnoreCase(rs.getString("FEXCHANGECODE"))){
//                        	// 国内情况
//							if (strRightType != null
//									&& strRightType.equalsIgnoreCase("23")) {
//								subTrade
//										.setTradePrice(Double.valueOf(
//												rs.getString("FRIPrice"))
//												.doubleValue());
//								subTrade.setTradeMoney(subTrade.getTradePrice()
//										* subTrade.getTradeAmount());
//								subTrade.setTotalCost(subTrade.getTradeMoney());
//                        }else{//国外情况
//							if (strRightType != null
//									&& strRightType.equalsIgnoreCase("08")) {
//								subTrade
//										.setTradePrice(Double.valueOf(
//												rs.getString("FRIPrice"))
//												.doubleValue());
//								subTrade.setTradeMoney(subTrade.getTradePrice()
//										* subTrade.getTradeAmount());
//								subTrade.setTotalCost(subTrade.getTradeMoney());
//							} else {
//								subTrade.setTradePrice(0);// 交易价格
//
//								subTrade.setTradeMoney(0);// 交易金额
//								subTrade.setTotalCost(0);//投资总成本
//
//							}
//                        }
					   //---------------------------------------------end STORY #431---------------------------------------------------------------------
//						}
                       //---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
                        
                       //----------------------------------------------------------------------- 
                        subTrade.setAccruedInterest(0);//应计利息

                        //---------------------以下为成本赋值--------------
                        //subTrade.setFees(fees);此处费用计算先不用，只有行权才有交易费用                      
                        //---------------------end-----------------//
                        subTrade.setDataSource(0);//数据源

                        subTrade.setDsType("HD_QY");//操作类型，表示系统操作数据，主要是和接口导入数据进行区分

                        subTrade.checkStateId = 1;//审核状态

                        subTrade.creatorCode = pub.getUserCode();//创建人

                        subTrade.checkTime = YssFun.formatDatetime(new java.util.Date());//审核时间

                        subTrade.checkUserCode = pub.getUserCode();//审核人

                        subTrade.creatorTime = YssFun.formatDatetime(new java.util.
                            Date());//创建时间

                       

                        subTrade.setFactSettleDate(YssFun.formatDate(rs.getDate(
                            "FPayDate")));//实际结算日期

                        subTrade.setMatureDate("9998-12-31");//到期日期

                        subTrade.setMatureSettleDate("9998-12-31");//到期结算日期

                        subTrade.setSettleState(new Integer(0).toString());//结算状态，未结算“0”

                        subTrade.setFactCashAccCode(strCashAccCode);//实际结算帐户

                        subTrade.setCashAcctCode(strCashAccCode);//设置现金账户

                        subTrade.setFactSettleMoney(0);//实际结算金额

                        subTrade.setExRate(1);//兑换汇率

                        subTrade.setFactPortRate(dPortRate);//实际结算组合汇率

                        subTrade.setFactBaseRate(dBaseRate);//实际结算基础汇率
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
            throw new YssException("计算配股权益处理出错！",e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /*story 2538 add by zhouwei 20120517 交易数据中是否存在该证券交易方式为配股缴款、权益登记日<= 成交日期 <= 缴款日 的交易数据 */
    private boolean checkSubTrade(String sportCode,String sSecurityCode,Date recordDate,Date expireDate) throws YssException{
    	boolean isExist=false;
    	String sql="";
    	ResultSet rs=null;
    	try{
    		sql="select fnum,FTradeAmount from "+pub.yssGetTableName("tb_data_SubTrade")
    		   +" where fcheckstate=1 and FSecurityCode="+dbl.sqlString(sSecurityCode)
    		   +" and FTradeTypeCode='23' and FPortCode="+dbl.sqlString(sportCode)//交易方式为配股缴款
    		   +" and FBargainDate>="+dbl.sqlDate(recordDate)
    		   +" and FBargainDate<="+dbl.sqlDate(expireDate)
    		   //add by songjie 2012.08.17 STORY #2538 QDV4赢时胜(上海开发部)2012年04月21日01_A
    		   +" and FCheckState = 1 ";
    		rs=dbl.openResultSet(sql);
    		if(rs.next()){
    			tradeAmount=rs.getDouble("FTradeAmount");//成交数量
    			isExist=true;
    		}
    	}catch (Exception e) {
    		throw new YssException("检查配股缴款的交易数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return isExist;
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
        ArrayList newAlRightEquity = null;
        try {
            if (alRightEquitys != null && alRightEquitys.size() > 0) {
            	// EDIT by lidaolong 20110407 #536 有关国内接口数据处理顺序的变更
                newAlRightEquity = checkSubTradeHaveRightData(alRightEquitys, YssOperCons.YSS_JYLX_QZSP, "ZD_QY,ZD_QY_T+1", dDate, sPortCode, this.tSecurityCode);
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
            newAlRightEquity = super.checkSubTradeHaveRightData(alRightEquitys, sTradeType, sDsType, dDate, sPortCode, tSecurityCode);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return newAlRightEquity;
    }
    /**
     * 计算费用
     * @param sSecCode String 证券代码
     * @param sTradeType String 交易类型
     * @param sPortCode String 组合代码
     * @param sBrokerCode String 券商代码
     * @param dMoney double 成交金额
     * @param dAmount double 成交数量
     * @param dCost double 成本
     * @param dDate Date 操作日期
     * @return String 返回值
     * @throws YssException 异常
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    private String buildFeesStr(String sSecCode, String sTradeType,
                                String sPortCode,
                                String sBrokerCode, double dMoney,
                                double dAmount, double dCost,
                                java.util.Date dDate) throws
        YssException {
        String fees = "";//保存费用的拼接
        double dFeeMoney;//费用
        FeeBean fee = null;//费用的类
        YssFeeType feeType = null;//费用类型类
        ArrayList alFeeBeans = null;//保存费用集合
        StringBuffer bufAll = new StringBuffer();
        try {
            BaseOperDeal baseOper = this.getSettingOper();
            BaseFeeDeal feeOper = (BaseFeeDeal) pub.getOperDealCtx().
                getBean(
                    "feedeal");//反射调用
            baseOper.setYssPub(pub);//设置PUB
            feeOper.setYssPub(pub);//设置PUB
            feeOper.setFeeAttr(sSecCode, sTradeType,
                               sPortCode,
                               sBrokerCode,
                               dMoney);//设置属性值
            alFeeBeans = feeOper.getFeeBeans();//返回获取费用的集合
            if (alFeeBeans != null) {
                feeType = new YssFeeType();//创建费用类型对象
                feeType.setMoney(dMoney);//设置金额
                feeType.setInterest( -1);//设置利息
                feeType.setAmount(dAmount);//设置数量
                for (int i = 0; i < alFeeBeans.size(); i++) {//循环费用数组
                    fee = (FeeBean) alFeeBeans.get(i);//得到费用bean
                    dFeeMoney = baseOper.calFeeMoney(feeType, fee, dDate);//得到费用值
                    bufAll.append(fee.getFeeCode()).append(YssCons.
                        YSS_ITEMSPLITMARK2);//拼接费用代码
                    bufAll.append(fee.getFeeName()).append(YssCons.
                        YSS_ITEMSPLITMARK2);//拼接费用名称
                    bufAll.append(YssFun.formatNumber(dFeeMoney, "###0.##")).
                        append("\f\n");//拼接费用
                }
                if (bufAll.toString().length() > 2) {
                    fees = bufAll.toString().substring(0,
                        bufAll.toString().length() -
                        2);//赋值
                }
            }
            return fees;//返回拼接的费用代码，费用名称，费用金额
        } catch (Exception e) {
            throw new YssException("获取费用信息出错！",e);
        }
    }

    /**
     * 删除条件，设置删除交易主子表中数据的条件
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @return TradeBean 返回值
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    public TradeBean filterBean(java.util.Date dDate, String sPortCode) {
        TradeBean trade = new TradeBean();
       
        //MS01354   add by zhangfa 20100716 MS01354    QDV4赢时胜(上海)2010年06月25日01_A  
        trade.setTradeCode(YssOperCons.YSS_JYLX_QZSP + "," + YssOperCons.YSS_JYLX_PG + "," + YssOperCons.YSS_JYLX_PGJK);//交易方式为权证配送
       
        //-------------------------------------------------------------------------------
        trade.setPortCode(sPortCode);//组合代码
        trade.setBargainDate(YssFun.formatDate(dDate));//成交日期
        trade.setDsType("HD_QY");//操作类型，表示此数据时界面输入的数据
        return trade;
    }

    /**
     * 此方法做配股数据的预处理，把处理数据保存到临时表tb_pub_data_PreRightsissue
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @throws YssException 异常
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    private void doDataPretreatment(java.util.Date dDate, String sPortCode) throws YssException {
        StringBuffer buff = null; //拼接sql语句
        ResultSet rs = null; //声明结果集
        RightsIssueBean rightsIssue = null; //配股的javaBean
        ArrayList rightsIssueData = new ArrayList(); //保存预处理后的配股权益数据
        try {
        	createTmpTable(); //创建配股临时表

        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        	String strSql = " truncate table " + pub.yssGetTableName("tb_data_PreRightsissue");
        	dbl.executeSql(strSql);
        	//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            
            /**
             * 以下sql语句处理配股权益数据，有组合群和组合代码
             */
            buff = new StringBuffer();
            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_rightsissue")); //配股权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
			//edit by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A
            buff.append(" and FAssetGroupCode like '%").append(pub.getPrefixTB()).append("%'"); //组合群代码
            buff.append(" and FPortCode =").append(dbl.sqlString(sPortCode)); //组合代码
            //add by nimengjing 2010.12.23 STORY #431有关权益处理国内的配股缴款数据的处理变更
			//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//  
//            buff.append(" union");
//            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_rightsissue")); //配股权益表
//            buff.append(" where FPAYDATE =").append(dbl.sqlDate(dDate)); //操作日期
//            buff.append(" and FCheckState = 1");
//            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB())); //组合群代码
//            buff.append(" and FPortCode =").append(dbl.sqlString(sPortCode)); //组合代码
//            buff.append(" and FTradeCode=").append(dbl.sqlString(YssOperCons.YSS_JYLX_PGJK));
			//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
            //---------------------------------------end ---------------------------------------------
            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                rightsIssue = new RightsIssueBean();
                rightsIssue.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                rightsIssue.setStrSecurityCode(rs.getString("FSecurityCode")); //证券代码
                rightsIssue.setStrTSecurityCode(rs.getString("FTSecurityCode"));//权证代码
                rightsIssue.setSCuryCode(rs.getString("FRICuryCode"));//币种代码
                rightsIssue.setStrRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                rightsIssue.setStrExRightDate(rs.getDate("FExRightDate").toString()); //除权日
                rightsIssue.setPayDate(rs.getDate("FPayDate").toString()); //到帐日
                rightsIssue.setAfficheDate(rs.getDate("FAfficheDate").toString()); //公告日
                rightsIssue.setBeginScriDat(rs.getDate("FBeginScriDate").toString()); //认购起始日
                rightsIssue.setEndScriDate(rs.getDate("FEndScriDate").toString()); //认购截至日
                rightsIssue.setBeginTradeDate(rs.getDate("FBeginTradeDate").toString()); //交易起始日
                rightsIssue.setEndTradeDate(rs.getDate("FEndTradeDate").toString()); //交易截至日
                rightsIssue.setStrExpirationDate(rs.getDate("FExpirationDate").toString()); //缴款截至日
                rightsIssue.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                rightsIssue.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                rightsIssue.setPortCode(rs.getString("FPortCode")); //组合代码
                rightsIssue.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                rightsIssue.setStrRIPrice(Double.toString(rs.getDouble("FRIPrice")));//配股价格
                rightsIssue.setStrRoundCode(rs.getString("FRoundCode")); //舍入配置
                rightsIssue.setStrDesc(rs.getString("FDesc")); //描述
              //MS01354   add by zhangfa 20100716 MS01354    QDV4赢时胜(上海)2010年06月25日01_A  
                rightsIssue.setTradeCode(rs.getString("FTradeCode"));
              //----------------------------------------------------------------------------- 
                rightsIssueData.add(rightsIssue); //把数据保存到集合中
            }
            saveIntoTmpTable(rightsIssueData); //保存数据到配股临时表
            dbl.closeResultSetFinal(rs); //关闭游标
            rightsIssueData.clear(); //清空集合

            /**
             * 以下sql语句处理配股权益数据，有组合群,但没有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_rightsissue")); //配股权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            //20120604 modified by liubo.Bug #4714
            //选择了多组合群的的配股数据，权益处理后没有产生交易数据
            //=============================
//            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB())); //组合群代码
            buff.append(" and FAssetGroupCode like ").append(dbl.sqlString("%" + pub.getPrefixTB() + "%"));
            //===============end==============
            buff.append(" and FPortCode =").append(dbl.sqlString(" ")); //组合代码
            buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":"");//证券代码
          //add by nimengjing 2010.12.23 STORY #431有关权益处理国内的配股缴款数据的处理变更  
		    //---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
//            buff.append(" union");
//            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_rightsissue")); //配股权益表
//            buff.append(" where FPAYDATE =").append(dbl.sqlDate(dDate)); //操作日期
//            buff.append(" and FCheckState = 1");
            //---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//

            //20120604 modified by liubo.Bug #4714
            //选择了多组合群的的配股数据，权益处理后没有产生交易数据
            //=============================
//            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(pub.getPrefixTB())); //组合群代码
              //---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
//            buff.append(" and FPortCode =").append(dbl.sqlString(" ")); //组合代码
//            buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":"");//证券代码
              //---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
            //---------------------------------------end ---------------------------------------------

            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                rightsIssue = new RightsIssueBean();
                rightsIssue.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                rightsIssue.setStrSecurityCode(rs.getString("FSecurityCode")); //证券代码
                rightsIssue.setStrTSecurityCode(rs.getString("FTSecurityCode")); //权证代码
                rightsIssue.setSCuryCode(rs.getString("FRICuryCode")); //币种代码
                rightsIssue.setStrRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                rightsIssue.setStrExRightDate(rs.getDate("FExRightDate").toString()); //除权日
                rightsIssue.setPayDate(rs.getDate("FPayDate").toString()); //到帐日
                rightsIssue.setAfficheDate(rs.getDate("FAfficheDate").toString()); //公告日
                rightsIssue.setBeginScriDat(rs.getDate("FBeginScriDate").toString()); //认购起始日
                rightsIssue.setEndScriDate(rs.getDate("FEndScriDate").toString()); //认购截至日
                rightsIssue.setBeginTradeDate(rs.getDate("FBeginTradeDate").toString()); //交易起始日
                rightsIssue.setEndTradeDate(rs.getDate("FEndTradeDate").toString()); //交易截至日
                rightsIssue.setStrExpirationDate(rs.getDate("FExpirationDate").toString()); //缴款截至日
                rightsIssue.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                rightsIssue.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                rightsIssue.setPortCode(sPortCode); //组合代码
                rightsIssue.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                rightsIssue.setStrRIPrice(Double.toString(rs.getDouble("FRIPrice"))); //配股价格
                rightsIssue.setStrRoundCode(rs.getString("FRoundCode")); //舍入配置
                rightsIssue.setStrDesc(rs.getString("FDesc")); //描述
                //MS01354   add by zhangfa 20100716 MS01354    QDV4赢时胜(上海)2010年06月25日01_A  
                rightsIssue.setTradeCode(rs.getString("FTradeCode"));
              //----------------------------------------------------------------------------- 
                rightsIssueData.add(rightsIssue); //把数据保存到集合中
            }
            saveIntoTmpTable(rightsIssueData); //保存数据到配股临时表
            dbl.closeResultSetFinal(rs); //关闭游标
            rightsIssueData.clear(); //清空集合
            //---add by zhangjun  2011-11-02 BUG2992在做卖空业务期间发生权益，维护送股、分红、配股权益数据bug----- //
            /**
             * 以下sql语句处理配股权益数据，没有组合群,有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_rightsissue")); //配股权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");            
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(" ")); //组合群代码
            buff.append(" and FPortCode =").append(dbl.sqlString(sPortCode)); //组合代码            
            buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":"");//证券代码
            //---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
//            buff.append(" union");
//            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_rightsissue")); //配股权益表
//            buff.append(" where FPAYDATE =").append(dbl.sqlDate(dDate)); //操作日期
//            buff.append(" and FCheckState = 1");            
//            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(" ")); //组合群代码
//            buff.append(" and FPortCode =").append(dbl.sqlString(sPortCode)); //组合代码            
//            buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":"");//证券代码
            //---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
            

            rs = dbl.openResultSet(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                rightsIssue = new RightsIssueBean();
                rightsIssue.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                rightsIssue.setStrSecurityCode(rs.getString("FSecurityCode")); //证券代码
                rightsIssue.setStrTSecurityCode(rs.getString("FTSecurityCode")); //权证代码
                rightsIssue.setSCuryCode(rs.getString("FRICuryCode")); //币种代码
                rightsIssue.setStrRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                rightsIssue.setStrExRightDate(rs.getDate("FExRightDate").toString()); //除权日
                rightsIssue.setPayDate(rs.getDate("FPayDate").toString()); //到帐日
                rightsIssue.setAfficheDate(rs.getDate("FAfficheDate").toString()); //公告日
                rightsIssue.setBeginScriDat(rs.getDate("FBeginScriDate").toString()); //认购起始日
                rightsIssue.setEndScriDate(rs.getDate("FEndScriDate").toString()); //认购截至日
                rightsIssue.setBeginTradeDate(rs.getDate("FBeginTradeDate").toString()); //交易起始日
                rightsIssue.setEndTradeDate(rs.getDate("FEndTradeDate").toString()); //交易截至日
                rightsIssue.setStrExpirationDate(rs.getDate("FExpirationDate").toString()); //缴款截至日
                rightsIssue.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                rightsIssue.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                rightsIssue.setPortCode(sPortCode); //组合代码
                rightsIssue.setAssetGroupCode(rs.getString("FAssetGroupCode")); //组合群代码
                rightsIssue.setStrRIPrice(Double.toString(rs.getDouble("FRIPrice"))); //配股价格
                rightsIssue.setStrRoundCode(rs.getString("FRoundCode")); //舍入配置
                rightsIssue.setStrDesc(rs.getString("FDesc")); //描述
                
                rightsIssue.setTradeCode(rs.getString("FTradeCode"));              
                rightsIssueData.add(rightsIssue); //把数据保存到集合中
            }
            saveIntoTmpTable(rightsIssueData); //保存数据到配股临时表
            dbl.closeResultSetFinal(rs); //关闭游标
            rightsIssueData.clear(); //清空集合
            //---add by zhangjun  2011-11-02 BUG2992在做卖空业务期间发生权益，维护送股、分红、配股权益数据bug----//   

            /**
             * 以下sql语句处理配股权益数据，没有组合群,但没有组合代码
             */
            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_rightsissue")); //配股权益表
            buff.append(" where FExRightDate =").append(dbl.sqlDate(dDate)); //操作日期
            buff.append(" and FCheckState = 1");
            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(" ")); //组合群代码
            buff.append(" and FPortCode =").append(dbl.sqlString(" ")); //组合代码
            buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":""); //证券代码
            //add by nimengjing 2010.12.23 STORY #431有关权益处理国内的配股缴款数据的处理变更  
			//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
//            buff.append(" union");
//            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_rightsissue")); //配股权益表
//            buff.append(" where FPAYDATE =").append(dbl.sqlDate(dDate)); //操作日期
//            buff.append(" and FCheckState = 1");
//            buff.append(" and FAssetGroupCode =").append(dbl.sqlString(" ")); //组合群代码
//            buff.append(" and FPortCode =").append(dbl.sqlString(" ")); //组合代码
//            buff.append(sSecurityCode.trim().length()!=0?" and FSecurityCode not in("+this.operSql.sqlCodes(sSecurityCode)+")":"");//证券代码
            //---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
            //---------------------------------------end ---------------------------------------------

            rs = dbl.queryByPreparedStatement(buff.toString());
            buff.delete(0, buff.length());
            while (rs.next()) {
                rightsIssue = new RightsIssueBean();
                rightsIssue.setYssPub(pub);
                sSecurityCode += rs.getString("FSecurityCode") + ","; //把获取的证券代码拼接 起来，作为下面的sql语句条件

                rightsIssue.setStrSecurityCode(rs.getString("FSecurityCode")); //证券代码
                rightsIssue.setStrTSecurityCode(rs.getString("FTSecurityCode"));//权证代码
                rightsIssue.setSCuryCode(rs.getString("FRICuryCode"));//币种代码
                rightsIssue.setStrRecordDate(rs.getDate("FRecordDate").toString()); //权益确认日
                rightsIssue.setStrExRightDate(rs.getDate("FExRightDate").toString()); //除权日
                rightsIssue.setPayDate(rs.getDate("FPayDate").toString()); //到帐日
                rightsIssue.setAfficheDate(rs.getDate("FAfficheDate").toString()); //公告日
                rightsIssue.setBeginScriDat(rs.getDate("FBeginScriDate").toString()); //认购起始日
                rightsIssue.setEndScriDate(rs.getDate("FEndScriDate").toString()); //认购截至日
                rightsIssue.setBeginTradeDate(rs.getDate("FBeginTradeDate").toString()); //交易起始日
                rightsIssue.setEndTradeDate(rs.getDate("FEndTradeDate").toString()); //交易截至日
                rightsIssue.setStrExpirationDate(rs.getDate("FExpirationDate").toString()); //缴款截至日
                rightsIssue.setPreTaxRatio(Double.toString(rs.getDouble("FPreTaxRatio"))); //税前权益比例
                rightsIssue.setAfterTaxRatio(Double.toString(rs.getDouble("FAfterTaxRatio"))); //税后权益比例
                rightsIssue.setPortCode(sPortCode); //组合代码
                rightsIssue.setAssetGroupCode(pub.getPrefixTB()); //组合群代码
                rightsIssue.setStrRIPrice(Double.toString(rs.getDouble("FRIPrice")));//配股价格
                rightsIssue.setStrRoundCode(rs.getString("FRoundCode")); //舍入配置
                rightsIssue.setStrDesc(rs.getString("FDesc")); //描述
                //MS01354   add by zhangfa 20100716 MS01354    QDV4赢时胜(上海)2010年06月25日01_A  
                rightsIssue.setTradeCode(rs.getString("FTradeCode"));
              //----------------------------------------------------------------------------- 
                rightsIssueData.add(rightsIssue); //把数据保存到集合中
            }
            saveIntoTmpTable(rightsIssueData); //保存数据到配股临时表

        } catch (Exception e) {
            throw new YssException("配股数据的预处理，把处理数据保存到临时表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 保存数据到配股临时表
     * @param rightsIssueData ArrayList 保存配股数据的集合
     * @throws YssException 异常
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    private void saveIntoTmpTable(ArrayList rightsIssueData) throws YssException {
        StringBuffer buff = null;//做拼接SQL语句
    	//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement  pst = null;
        //=============end====================
        RightsIssueBean rightsIssue = null;//声明配股实体bean
        try {
            buff = new StringBuffer();
            buff.append(" insert into ").append(pub.yssGetTableName("tb_data_PreRightsissue")); //配股权益预处理表
            buff.append(" (");
            buff.append("FSecurityCode,FTSecurityCode,FRICuryCode,FAfficheDate,FRecordDate,FPayDate,FExRightDate,");
            buff.append("FBeginScriDate,FEndScriDate,FBeginTradeDate,FEndTradeDate,FExpirationDate,FPreTaxRatio,FAfterTaxRatio,");
            buff.append("FPortCode,FAssetGroupCode,FRIPrice,FRoundCode,FDesc,");
            buff.append("FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime," +
           //MS01354    add by zhangfa 20100716    QDV4赢时胜(上海)2010年06月25日01_A   
            		"FTradeCode");
           //------------------------------------------------------------------------- 
            buff.append(")");
            buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			//modified by liubo.Story #2145
			//==============================
//            pst = dbl.getPreparedStatement(buff.toString());
            pst = dbl.getYssPreparedStatement(buff.toString());
			//==============end================
            for (int i = 0; i < rightsIssueData.size(); i++) {//循环保存数据的集合
                rightsIssue = (RightsIssueBean) rightsIssueData.get(i);//获取实例化bean
                pst.setString(1, rightsIssue.getStrSecurityCode());//证券代码
                pst.setString(2, rightsIssue.getStrTSecurityCode());//权证代码
                pst.setString(3, rightsIssue.getSCuryCode());//币种代码
                pst.setDate(4, YssFun.toSqlDate(rightsIssue.getAfficheDate()));//公告日
                pst.setDate(5, YssFun.toSqlDate(rightsIssue.getStrRecordDate()));//权益确认日
                pst.setDate(6, YssFun.toSqlDate(rightsIssue.getPayDate()));//到帐日
                pst.setDate(7, YssFun.toSqlDate(rightsIssue.getStrExRightDate()));//除权日
                pst.setDate(8, YssFun.toSqlDate(rightsIssue.getBeginScriDat()));//认购起始日
                pst.setDate(9, YssFun.toSqlDate(rightsIssue.getEndScriDate()));//认购截至日
                pst.setDate(10, YssFun.toSqlDate(rightsIssue.getBeginTradeDate()));//交易起始日
                pst.setDate(11, YssFun.toSqlDate(rightsIssue.getEndTradeDate()));//交易截至日
                pst.setDate(12, YssFun.toSqlDate(rightsIssue.getStrExpirationDate()));//缴款截至日
                pst.setDouble(13, Double.parseDouble(rightsIssue.getPreTaxRatio()));//税前权益比例
                pst.setDouble(14, Double.parseDouble(rightsIssue.getAfterTaxRatio()));//税后权益比例
                pst.setString(15, rightsIssue.getPortCode());//组合代码
                pst.setString(16, rightsIssue.getAssetGroupCode());//组合群代码
                pst.setDouble(17, Double.parseDouble(rightsIssue.getStrRIPrice()));//配股价格
                pst.setString(18, rightsIssue.getStrRoundCode());//舍入配置
                pst.setString(19, rightsIssue.getStrDesc());//描述
                pst.setInt(20, 1);//审核状态
                pst.setString(21, pub.getUserCode());//创建人
                pst.setString(22, YssFun.formatDatetime(new java.util.Date()));//创建时间
                pst.setString(23, pub.getUserCode());//审核人
                pst.setString(24, YssFun.formatDatetime(new java.util.Date()));//审核时间
                //MS01354    add by zhangfa 20100716    QDV4赢时胜(上海)2010年06月25日01_A
                pst.setString(25, rightsIssue.getTradeCode());
                //-----------------------------------------------------------------------
                pst.executeUpdate();
            }
        } catch (Exception e) {
            throw new YssException("保存数据到配股临时表出错！", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }

    /**
     * createTmpTable创建配股临时表 tb_pub_data_Predividend
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
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
            buff.append(" CREATE GLOBAL TEMPORARY TABLE ").append(pub.yssGetTableName("tb_data_PreRightsissue"));
            buff.append(" ( ");
            buff.append(" FSECURITYCODE   VARCHAR2(50)  NOT NULL, ");
            buff.append(" FRECORDDATE     DATE          NOT NULL, ");
            buff.append(" FPORTCODE       VARCHAR2(20)  NOT NULL, ");
            buff.append(" FASSETGROUPCODE VARCHAR2(20)  NOT NULL, ");
            buff.append(" FRICURYCODE     VARCHAR2(20)      NULL, ");
            buff.append(" FTSECURITYCODE  VARCHAR2(50)      NULL, ");
            buff.append(" FEXRIGHTDATE    DATE          NOT NULL,");
            buff.append(" FEXPIRATIONDATE DATE          NOT NULL,");
            buff.append(" FAFFICHEDATE    DATE          NOT NULL,");
            buff.append(" FPAYDATE        DATE          NOT NULL,");
            buff.append(" FBEGINSCRIDATE  DATE          NOT NULL,");
            buff.append(" FENDSCRIDATE    DATE          NOT NULL,");
            buff.append(" FBEGINTRADEDATE DATE          NOT NULL,");
            buff.append(" FENDTRADEDATE   DATE          NOT NULL,");
            buff.append(" FPRETAXRATIO    NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FAFTERTAXRATIO  NUMBER(25,15) DEFAULT 0     NULL,");
            buff.append(" FRIPRICE        NUMBER(18,4)  NOT NULL,");
            buff.append(" FROUNDCODE      VARCHAR2(20)  NOT NULL,");
            buff.append(" FDesc           VARCHAR(100)  NULL,");
            buff.append(" FCHECKSTATE     NUMBER(1)     NOT NULL, ");
            buff.append(" FCreator        VARCHAR(20)   NOT NULL, ");
            buff.append(" FCreateTime     VARCHAR(20)   NOT NULL, ");
            buff.append(" FCHECKUSER      VARCHAR2(20)  NULL,");
            buff.append(" FCHECKTIME      VARCHAR2(20)  NULL, ");
            //MS01354    add by zhangfa 20100716    QDV4赢时胜(上海)2010年06月25日01_A
            buff.append("FTradeCode     VARCHAR2(20)  NULL, ");
            //------------------------------------------------------------------------  
            buff.append(" CONSTRAINT PK_").append(pub.yssGetTableName("tb_data_PreRightsissue"));
            //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
            //buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE) ");
            buff.append(" PRIMARY KEY (FSECURITYCODE,FRECORDDATE,FPORTCODE,FASSETGROUPCODE,FPAYDATE) ");
            //-----------------------------------
            
            buff.append(" ) ON COMMIT PRESERVE ROWS");
        	
        	//获取表类型，如果不是会话级的临时表，则删除该表，并创建同名会话级临时表
        	strSql = " select DURATION from user_tables where TABLE_NAME = " + 
        	dbl.sqlString(pub.yssGetTableName("tb_data_PreRightsissue".toUpperCase()));
        	rs = dbl.openResultSet(strSql);
        	if(rs.next()){
        		duration = rs.getString("DURATION");
        		if(duration == null || (duration != null && !duration.equals("SYS$SESSION"))){
        			if (dbl.yssTableExist(pub.yssGetTableName("tb_data_PreRightsissue"))) { 
        				/**shashijie ,2011-10-12 , STORY 1698*/
        				dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pub.yssGetTableName("tb_data_PreRightsissue")));
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
            throw new YssException("创建配股临时表出错！", e);
        }
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
    }
    
    
    /********************************************************************************************
     *  add by jiangshichao 2010.04.17
     * 配股缴款业务处理
     * 交易日: 产生冲减的应收应付数据,和资金调拨数据(业务日期为交易日，调拨日期为到帐日)
     * 除权日：确认股票成本，统计其证券库存并冲减应收新股清算款
     * 到帐日：资金划出并将应付清算款冲减
     * 
     * 注意：到帐日就是业务资料中的结算日期
     * 
     * @param dDate,sPortCode
     *  通过对日期的判断，来做相应的业务处理
     * @throws YssException
     */
    public void rightIssuePaymentDeal(java.util.Date dDate, String sPortCode) throws YssException{
    	prAdmin = new CashPayRecAdmin();
    	String query = "";
    	ResultSet rs = null;
    	Connection conn = dbl.loadConnection();
        boolean bTrans = false;
    	query = " select a.fexrightdate,a.FCashAccCode,'AP_EQ'as FType,a.FPortCode,FTotalCost,FBargainDate,FSettleDate,"+
    	        " FFactSettleDate, a.FInvMgrCode,a.FBrokerCode,a.FTradeTypeCode,FTradeAmount,FBaseCuryRate,"+
    	        " FPortCuryRate,c.FCuryCode as FCashCuryCode,d.FCatCode,e.FInvMgrName as FInvMgrName,"+
    	        " f.FBrokerName as FBrokerName,d.FSecurityname as FSecurityName,d.FSecurityCode as FSecurityCode from "+
    	        " (select a1.*,a2.fexrightdate from (select * from " + pub.yssGetTableName("tb_data_subtrade")+
    	        " where fcheckstate=1 and ftradetypecode =" +dbl.sqlString(YssOperCons.YSS_JYLX_PGJK)+"and fportcode ="+dbl.sqlString(sPortCode)+
    	        " and fbargaindate <="+dbl.sqlDate(dDate)+")a1"+//story 2538 by zhouwei 20120521 配股缴款+" and fsettledate>="+dbl.sqlDate(dDate)
    	        " left join (select fsecuritycode,fexrightdate from "+pub.yssGetTableName("tb_data_rightsissue")+" where fcheckstate=1 and "+
    	        " fexrightdate="+dbl.sqlDate(dDate)+")a2 on a1.fsecuritycode = a2.fsecuritycode) a"+
    	        " left join (select * from Tb_Base_TradeType where FCheckState = 1) b on a.FTradeTypeCode = b.FTradeTypeCode"+
    	        " left join (select * from "+pub.yssGetTableName("Tb_Para_CashAccount")+" where FCheckState = 1) c on a.FCashAccCode = c.FCashAccCode"+
    	        " left join (select * from "+pub.yssGetTableName("Tb_Para_Security")+" where FCheckState = 1) d on a.FSecurityCode = d.FSecurityCode"+
    	        " left join (select * from "+pub.yssGetTableName("Tb_Para_InvestManager")+" where FCheckState = 1) e on a.FInvMgrCode = e.FInvMgrCode"+
    	        " left join (select * from "+pub.yssGetTableName("Tb_Para_Broker")+" where FCheckState = 1) f on a.FBrokerCode = f.FBrokerCode";
    	
    	try {
			rs = dbl.queryByPreparedStatement(query);
			getRecPayData(rs,dDate);
			
			    prAdmin.setYssPub(pub);
			    bTrans = true;
	            conn.setAutoCommit(false);
//	            prAdmin.delete("", dDate, dDate, "02", "02TD%", "", "", sPortCode, "",
//	                           "", "", 0);
//	            prAdmin.delete("", dDate, dDate, "06", "06TD%", "", "", sPortCode, "",
//	                           "", "", 0);
//	            conn.commit();
	            
	            prAdmin.insert(dDate, "06,02",
	                           "06AP_EQ,02AP_EQ", sPortCode,
	                           0, false);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
		} catch (Exception e) {

			e.printStackTrace();
		}finally{
			dbl.closeResultSetFinal(rs);//关闭游标 by leeyu 20100909
		}
    }
 
    /**
     * 产生应收应付清算款
     * @param alRecPay ArrayList
     * @param rs ResultSet
     * @throws YssException
     */
    private void getRecPayData(ResultSet rs,java.util.Date dDate) throws YssException{
        CashPecPayBean cashpecpay = null; 
        boolean analy1;
        boolean analy2;
        boolean analy3;
        double dBaseRate =0;
        double dPortRate=0;
        
        
    	//--------------------------by 曹丞2009.01.22	增加币种有效性检查 MS00004 QDV4.1-2009.2.1_09A----//
        try {
        	while (rs.next()) {
				if (rs.getString("FCashCuryCode") == null
						|| rs.getString("FCashCuryCode").trim().length() == 0) {
					throw new YssException("系统在权益处理时检查到代码为【"
							+ rs.getString("FCashAccCode")
							+ "】的现金账户对应的货币信息不存在!" + "\n" + "请核查以下信息：" + "\n"
							+ "1.【现金账户设置】中该账户信息是否存在且已审核!" + "\n"
							+ "2.【现金账户设置】中该现金账户代码设置是否正确!");
				}
				// -----------------------------------------------------------------------------------------------//
				analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
				analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
				analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

				cashpecpay = new CashPecPayBean();
				cashpecpay.setTradeDate(dDate);
				cashpecpay.setCashAccCode(rs.getString("FCashAccCode"));
				cashpecpay.setPortCode(rs.getString("FPortCode"));
				dBaseRate = rs.getDouble("FBaseCuryRate");
				dPortRate = rs.getDouble("FPortCuryRate");
				if (analy1) {
					cashpecpay
							.setInvestManagerCode(rs.getString("FInvMgrCode"));
				} else {
					cashpecpay.setInvestManagerCode(" ");
				}
				if (analy2) {
					cashpecpay.setCategoryCode(rs.getString("FCatCode"));
				} else {
					cashpecpay.setCategoryCode(" ");
				}
				cashpecpay.setDataSource(0); // 自动
				cashpecpay.setCuryCode(rs.getString("FCashCuryCode"));
				// ---------------------------------------------------------------------//
				if (YssFun.dateDiff(dDate, rs.getDate("FBargainDate")) == 0) {
					// 配股缴款业务在交易日产生应收款项
					cashpecpay.setMoney(rs.getDouble("FTotalCost"));
					cashpecpay.setBaseCuryMoney(this.getSettingOper()
									.calBaseMoney(rs.getDouble("FTotalCost"),dBaseRate));
					cashpecpay.setPortCuryMoney(this.getSettingOper()
							.calPortMoney(rs.getDouble("FTotalCost"),dBaseRate, dPortRate,
									rs.getString("FCashCuryCode"), dDate,rs.getString("FPortCode")));
					cashpecpay.setTsfTypeCode("06"); // 应收未清算款
					cashpecpay.setSubTsfTypeCode("06" + rs.getString("FType")); // 应收未清算款项
				//---add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
				}else if(rs.getDate("fexrightdate")==null){//不到除权日，不产生02现金应收应付
					continue;
				//---add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
				} else if (YssFun.dateDiff(dDate, rs.getDate("fexrightdate")) == 0) {
					// 应收款项在除权日冲减
					cashpecpay.setMoney(rs.getDouble("FTotalCost"));
					cashpecpay.setBaseCuryMoney(this.getSettingOper()
									.calBaseMoney(rs.getDouble("FTotalCost"),dBaseRate));
					cashpecpay.setPortCuryMoney(this.getSettingOper()
							.calPortMoney(rs.getDouble("FTotalCost"),
									dBaseRate, dPortRate,rs.getString("FCashCuryCode"), dDate,
									rs.getString("FPortCode")));
					cashpecpay.setTsfTypeCode("02"); // 应收未清算款
					cashpecpay.setSubTsfTypeCode("02" + rs.getString("FType")); // 应收未清算款项
				//---add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
				}else{
					continue;
				//---add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
				}
				cashpecpay.setBaseCuryRate(dBaseRate);
				cashpecpay.setPortCuryRate(dPortRate);
				cashpecpay.checkStateId = 1;

				prAdmin.addList(cashpecpay);
			}
		} catch (Exception e) {
			
			throw new YssException("配股缴款业务生成应收清算款出错！");
		}
    }
    
}











