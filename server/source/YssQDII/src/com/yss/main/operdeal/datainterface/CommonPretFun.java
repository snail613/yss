package com.yss.main.operdeal.datainterface;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.base.*;
import com.yss.main.cashmanage.*;
import com.yss.main.operdata.*;
// 新需求：QDV4交银施罗德2008年10月20日01_A byleeyu 2008-10-20
import com.yss.main.operdata.futures.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.datainterface.pojo.*;
import com.yss.main.operdeal.linkInfo.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.*;
import com.yss.main.taoperation.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

public class CommonPretFun
    extends BaseAPOperValue {
    //private Object obj = ""; // 新需求：QDV4交银施罗德2008年10月20日01_A byleeyu 2008-10-20
    //在取obj值时系统加上了 起始日期,终止日期,组合集表三个参数,如果要取这三个参数,请在原有的参数基础上再向后顺序取这三个参数
    private CommonPrepFunBean prepFun = null; // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
    public CommonPretFun() {

    }

    /**
     * init
     *
     * @param bean Object
     */
    public void init(Object bean) throws YssException {
        // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
        //obj = bean;
        try {
            prepFun = (CommonPrepFunBean) bean;
        } catch (Exception ex) {
            throw new YssException("初始化通用预处理函数出错", ex);
        }
        // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
    }

    /**
     * getTypeValue
     *
     * @param sType String
     * @return Object
     */
    public Object getTypeValue(String sType) throws YssException {
        String sResult = "";
        try {
        	if (sType.equalsIgnoreCase("getPurchaseNum")) { //银行间回购交易编号
        		sResult = getPurchaseNum();
			} else if (sType.equalsIgnoreCase("getIntBakBondNum")) { //银行间债券交易编号
        		sResult = getIntBakBondNum();
			} else if (sType.equalsIgnoreCase("getNewIssueTradeNum")) { //网下新股新债交易编号
        		sResult = getNewIssueTradeNum();
			} else if (sType.equalsIgnoreCase("getDevTrustBondNum")) { //获取债券转托管及上市交易编号
        		sResult = getDevTrustBondNum();
			} else if (sType.equalsIgnoreCase("getOpenFundTradeNum")) { //获取开放式基金交易数据编号
        		sResult = getOpenFundTradeNum();
			} else if (sType.equalsIgnoreCase("getFuturesTradeNum")) { //获取期货交易数据编号
				sResult = getFuturesTradeNum();
			} else if (sType.equalsIgnoreCase("getOptionsTradeNum")) { //获取期权交易数据编号
				sResult = getOptionsTradeNum();
			} else if (sType.equalsIgnoreCase("getTradeNum")) { //获取交易主表数据编号
                sResult = getTradeNum();
            } else if (sType.equalsIgnoreCase("getSubTradeNum")) { //获取交易子表数据编号
                sResult = getSubTradeNum();
            } else if (sType.equalsIgnoreCase("getSubTradeNum_blm")) { //获取交易子表数据编号 //alter by sunny
                sResult = getSubTradeNumBlm();
            } else if (sType.equalsIgnoreCase("getTATradeNum")) { //获取TA交易数据编号
                sResult = getTATradeNum();
            }else if ( sType.equalsIgnoreCase("getDZAccountNum")){ //获取电子对账编号 合并南方42上线版本改动代码
                sResult = getDZAccountNum(); 
            }else if (sType.equalsIgnoreCase("calFee")) { //计算费用
                //这里调用处理费用的过程
                sResult = calFee(); //获取费用代码和金额
            } else if (sType.equalsIgnoreCase("getCashAcc")) { //获取现金帐户
                //这里调用获取现金帐户的过程
                sResult = getCashAcc();
            } else if (sType.equalsIgnoreCase("getExchangeRate")) {
                sResult = getExchangeRate();
            }
            //.........这里根据不同的需要，可能以后还要增加方法
            else if (sType.equalsIgnoreCase("getUnPL")) {
                sResult = getUnPL(); //获取损益平准金(未实现)     20070929  chenyb
            } else if (sType.equalsIgnoreCase("getPL")) {
                sResult = getPL(); //获取损益平准金             20070929 chenyb
            } else if (sType.equalsIgnoreCase("getTotalCost")) { //获取成交总金额  20071016  chenyb
                sResult = getTotalCost();
            } else if (sType.equalsIgnoreCase("getExchangeCode")) { //处理交易所有代码         20071010 liyu
                sResult = getExchangeCode();
            } else if (sType.equalsIgnoreCase("getPrice")) { //处理SWIFT报文中的价格   20071021 liyu
                sResult = getPrices();
            } else if (sType.equalsIgnoreCase("getTACashAcc")) {
                sResult = this.getTaCashAcc(); //20071024   chenyibo   通过ta模块中的现金帐户联接获取现金帐户
            } else if (sType.equalsIgnoreCase("getTaSettleDate")) { //20071026   chenyibo   获取ta的结算日期
                sResult = this.getTaSettleDate();
            } else if (sType.equalsIgnoreCase("getTaConfirmDate")) { //20071016  chenyibo    获取ta的确认日期
                sResult = this.getTaConfirmDate();        
            } else if (sType.equalsIgnoreCase("taTrade")) { //处理申购金额,赎回金额,分红金额为0的数据
                sResult = this.dealTaTrade();
            } else if (sType.equalsIgnoreCase("getPortCode")) { //通过分级代码获取投资组合        20071122  chenyibo
                sResult = this.getPortCode();
            } else if (sType.equalsIgnoreCase("getRateTradeNum")) {
                sResult = this.getRateTradeNum();
            } else if (sType.equalsIgnoreCase("updateTrade")) { // 获取实际结算日期，实际结算金额 根据交易号修改交易子表中这两字段  lzp 08-01-04
                this.updateTrade();
            } else if (sType.equalsIgnoreCase("tradeSettle")) { // 获取交易号  做结算
                this.operTradeSettle();
            } else if (sType.equalsIgnoreCase("getportMoney")) { // 获取外汇交易时组合货币金额
                sResult = getPortMoney();
            } else if (sType.equalsIgnoreCase("getbaseMoney")) { //获取外汇交易基础货币金额
                sResult = getBaseMoney();
            } else if (sType.equalsIgnoreCase("getreteFX")) { //获取外汇交易时汇兑损益
                sResult = getRateFX();
            } else if (sType.equalsIgnoreCase("getRateNum")) { //计算外汇交易的编号
                sResult = getRateNum();
            } else if (sType.equalsIgnoreCase("insetCashTransfer")) { //由外汇交易接口来更新划款指令
                insertCashTransfer();
            } else if (sType.equalsIgnoreCase("insertCashCommand")) { //由外汇交易数据接口产生划款指令
                insertCashCommand();
            } else if (sType.equalsIgnoreCase("getWorkDay")) { //根据节假日群获取工作日
                sResult = getWorkDay();
            } else if (sType.equalsIgnoreCase("getCashTransferNum")) { //获取资金调拨的编号  add by ly 080317
                sResult = getCashTransferNum();
            } else if (sType.equalsIgnoreCase("getCashPayRecNum")) { //获取现金应收应付编号 add by ly 080317
                sResult = getCashPayRecNum();
            } else if (sType.equalsIgnoreCase("calcExchangeRate")) { //计算汇率的方法 add by liyu 080507
                sResult = calcExchangeRate() + "";
            } else if (sType.equalsIgnoreCase("getTaTradeDateByConfirmDate")) { //通过TA确认日获取申请日 add by zhouss
                sResult = this.getTaTradeDateByConfirmDate();
            } else if (sType.equalsIgnoreCase("getTaSettleDateByConfirmDate")) { //通过TA确认日获取结算日 add by zhouss
                sResult = this.getTaSettleDateByConfirmDate();
            } else if (sType.equalsIgnoreCase("getMaxNum")) { //通过表明和列名生成最大编号 2008.08.15 蒋锦 添加
                sResult = this.getMaxNum();
            } else if (sType.equalsIgnoreCase("getSecRecPayNum")) { //获取证券应收应付编号 add by shw 080909
                sResult = getSecRecPayNum();
            } else if (sType.equalsIgnoreCase("getSecrityLinkMtvMethod")) { //2008-02-24 蒋锦 添加 QDV4.1 MS00008 《QDV4.1赢时胜上海2009年2月1日07_A》证券绑定估值方法
                sResult = getSecrityLinkMtvMethod();
            } else if (sType.equalsIgnoreCase("getFutruesBegAccCode")) { //获取股指期货初始保证金帐户 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090507
                sResult = getFutruesBegAccCode();
            } else if (sType.equalsIgnoreCase("getFutruesChangeAccCode")) { //获取股指期货变动保证金帐户 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090507
                sResult = getFutruesChangeAccCode();
            } else if (sType.equalsIgnoreCase("getFutruesNum")) { //获取股指期货编号 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090507
                sResult = getFutruesNum();
            } else if (sType.equalsIgnoreCase("getFutruesBail")) { //获取股指期货保证金额 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090507
                sResult = String.valueOf(getFutruesBail());
            //--------------------------------xuqiji 20100417-------------------//
            } else if(sType.equalsIgnoreCase("getAHFutruesNum")){
            	sResult = getAHFutruesNum();
            }else if(sType.equalsIgnoreCase("getAHFutruesFeeCode")){
            	sResult = getAHFutruesFeeCode();
            }else if(sType.equalsIgnoreCase("getAHFutruesFeeMoney")){
            	sResult = getAHFutruesFeeMoney();
            }
            else if(sType.equalsIgnoreCase("getCashTradeNum"))  //获取期间头寸交易数据的编号
            {
            	sResult =getCashTradeNum();
            }
            else if(sType.equalsIgnoreCase("getCashPreTradeNum"))//获取预估数据交易编号
            {
            	sResult =getCashPreTradeNum();
            }
            else if(sType.equalsIgnoreCase("getCashRateTradeNum"))//获取换汇表数据编号
            {
            	sResult =getCashRateTradeNum();
            //add by nimengjing 2011.1.28 BUG #990 配置通过函数自动获取成交编号，系统处理时因编号重复造成无法插入该交易记录 
            }else if(sType.equalsIgnoreCase("getFuTradeNum")){//获取期货交易编号
            	sResult=getFuTradeNum();
            }
			else if (sType.equalsIgnoreCase("getBakBondNum"))//获取银行间交易编号，临时添加；shenjie
            {
            	sResult = getBakBondNum();
            }
            //---------------------------------------------------end bug#990-------------------------------------------------
            //---------------------------------end------------------------------//
        	//---add by songjie 2012.03.05 STORY #2147 QDV4赢时胜(海富通)2012年01月29日01_A start---//
            else if(sType.equalsIgnoreCase("getTradeSettleDate")){
            	sResult = getTradeSettleDate();
            }
        	//---add by songjie 2012.03.05 STORY #2147 QDV4赢时胜(海富通)2012年01月29日01_A end---//
           //story 2683 add by zhouwei 20120612 获取实收基金金额
            else if(sType.equalsIgnoreCase("getPaidUpFundsMoney")){
            	sResult=getPaidUpFundsMoney();
            }
        	//---story 2727 add by zhouwei 20120618 根据TA确认日来计算损益平准金及汇率 start---//
            else if(sType.equalsIgnoreCase("getPLByConfirmDate")){
            	sResult=getPLByConfirmDate();
            }else if(sType.equalsIgnoreCase("getUnPLByConfirmDate")){
            	sResult=getUnPLByConfirmDate();
            }else if(sType.equalsIgnoreCase("getTAExRateByConfirmDate")){
            	sResult=getTAExRateByConfirmDate();
            }
			//---story 2727 add by zhouwei 20120618 根据TA确认日来计算损益平准金及汇率 end---//
        	//--- add by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 start---//
            else if(sType.equalsIgnoreCase("getTAPaidInMoney")){
            	sResult = getTAPaidInMoney();
            }
        	//--- add by songjie 2013.06.27 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 end---//
        	
        	/**Start 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001
        	 * 计算实收基金*/
            else if(sType.equalsIgnoreCase("getPaidinMoney")){
            	sResult = getPaidinMoney();
            }
        	/**End 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001*/
            return sResult;
        } catch (Exception e) {
            throw new YssException("通过函数获取数据出错",e);
        }
    }
    
    /**
     * add by songjie 2012.03.05 
     * STORY #2147 QDV4赢时胜(海富通)2012年01月29日01_A
     * 根据通用参数：交易数据延迟天数设置 获取结算日期
     * @return
     * @throws YssException
     */
    private String getTradeSettleDate() throws YssException{
    	ArrayList list = new ArrayList();
    	String portCode = "";
    	String securityCode = "";
    	java.util.Date settleDate = null;
    	String exchangeCode = "";
    	String strSql = "";
    	ResultSet rs = null;
    	HashMap hmPara = null;
    	String holidayCode = "";
    	String settleDelayValue = "";
    	String finalDate = "";
    	try {
    		list = (ArrayList) prepFun.getObj();
    		
    		portCode = (String)list.get(0);//组合代码
    		securityCode = (String)list.get(1);//证券代码
    		if(list.get(2) instanceof Timestamp){
    			settleDate = YssFun.parseDate(((Timestamp)list.get(2)).toString());//结算日期
    		}
    		if(list.get(2) instanceof String){
    			settleDate = YssFun.parseDate((String)list.get(2));//结算日期
    		}
    		
    		strSql = " select * from " + pub.yssGetTableName("Tb_Para_Security") +
    		" where FSecurityCode = " + dbl.sqlString(securityCode) +
    		" and FCheckState = 1 ";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			exchangeCode = rs.getString("FExchangeCode");//交易所代码
    		}
    		
            CtlPubPara ctlPara = new CtlPubPara();
            ctlPara.setYssPub(pub);
            //根据组合代码  和 交易所代码 到通用参数：交易数据延迟天数设置 中查找 相关组合 和 交易所 对应的  节假日群代码  和  结算延迟天数
            hmPara = ctlPara.getTradeSettleInfo(portCode,exchangeCode);
            //若没有设置相关组合 和 交易所的 交易数据延迟天数设置 则 返回 接口预处理数据源中的 结算日期
            if(hmPara == null){
            	return YssFun.formatDate(settleDate,"yyyy-MM-dd");
            }
            else{
            	holidayCode = (String)hmPara.get("holiday");//节假日群代码
            	settleDelayValue = (String)hmPara.get("settle");//结算延迟天数
            }
            
			BaseOperDeal baseOperDeal = new BaseOperDeal(); // 新建BaseOperDeal
			baseOperDeal.setYssPub(pub);
            //根据 节假日群代码 、日期、延迟天数  获取 最终的交易结算日期
			finalDate = YssFun.formatDate(baseOperDeal.getWorkDay
					(holidayCode, settleDate, Integer.parseInt(settleDelayValue)));
    		
    		return finalDate;
    	}catch(Exception e){
    		throw new YssException("获取交易结算日期出错",e);
    	} finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /**
     * 获取导入文件中的时间一列,在预处理"字段匹配"中设置,如果没有则是系统当前时间
     * TODO <Method comments>
     * @return String 时间格式 yyyyDDmm
     * @author shashijie ,2011-3-2
     * @modified
     */
    private String getStrNumDate() throws YssException {
    	try {
	    	String bargainDate = "";
	    	ArrayList list = (ArrayList) prepFun.getObj(); 
	    	//如配置了的数据源能获取日期就用日期,如果不是合法日期就用系统当前时间
	        if (String.valueOf(list.get(0)).length() >= 10) {
	        	try {
	        		bargainDate = String.valueOf(list.get(0)).substring(0, 10);
	        		bargainDate = YssFun.formatDate(bargainDate);
				} catch (Exception e) {
					bargainDate = YssFun.formatDate(new java.util.Date());
				}
	        } else {
	            bargainDate = YssFun.formatDate(new java.util.Date());
	        }
	        //获取导入文件中的时间一列,在预处理"字段匹配"中设置
	        String strNumDate = YssFun.formatDatetime(YssFun.toDate(bargainDate)).substring(0, 8);
	        return strNumDate;
    	} catch (Exception e) {
    		throw new YssException("获取时间出错",e);
		}
    }
    
    /**
     *  银行间回购交易编号
     * TODO <Method comments>
     * @return	TASK #2673::请开发部增加交易编号获取的SPRING调用（需要尽快开发）
     * @author shashijie ,2011-3-1
     * @modified
     */
    private String getPurchaseNum() throws YssException{
        String tradeNum = "";
        String FNum = "";
        int tmpNum;
        try {
        	String strNumDate = getStrNumDate();
            //系统获取下一个交易编号的值
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName(
            					"Tb_Data_Purchase"), dbl.sqlRight("FNUM", 6), "000000",
					            " WHERE FNum LIKE 'T" + strNumDate + "%'", 1);
            tmpNum = YssFun.toInt(tradeNum);
            //T+日期+序号:交易编号生成规则
            FNum = "T" + strNumDate + YssFun.formatNumber(tmpNum, "000000");
            return FNum;
        } catch (Exception e) {
            throw new YssException("通过函数获取银行间回购交易编号出错",e);
        }
	}
    
    /**
     *  银行间债券交易编号
     * TODO <Method comments>
     * @return	TASK #2673::请开发部增加交易编号获取的SPRING调用（需要尽快开发）
     * @author shashijie ,2011-3-1
     * @modified
     */
    private String getIntBakBondNum() throws YssException{
        String tradeNum = "";
        String FNum = "";
        int tmpNum;
        try {
        	String strNumDate = getStrNumDate();
            //系统获取下一个交易编号的值
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName(
            					"Tb_Data_IntBakBond"), dbl.sqlRight("FNUM", 9), "000000000",
					            " WHERE FNum LIKE 'IBB" + strNumDate + "%'", 1);
            tmpNum = YssFun.toInt(tradeNum);
            //IBB+日期+序号:交易编号生成规则
            FNum = "IBB" + strNumDate + YssFun.formatNumber(tmpNum, "000000000");
            return FNum;
        } catch (Exception e) {
            throw new YssException("通过函数获取银行间债券交易编号出错",e);
        }
	}

	/**
     * 网下新股新债交易编号
     * TODO <Method comments>
     * @return	TASK #2673::请开发部增加交易编号获取的SPRING调用（需要尽快开发）
     * @author shashijie ,2011-3-1
     * @modified
     */
    private String getNewIssueTradeNum() throws YssException {
        String tradeNum = "";
        String FNum = "";
        int tmpNum;
        try {
        	String strNumDate = getStrNumDate();
            //系统获取下一个交易编号的值
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName(
            					"Tb_Data_NewIssueTrade"), dbl.sqlRight("FNUM", 9), "000000000",
					            " WHERE FNum LIKE 'NSB" + strNumDate + "%'", 1);
            tmpNum = YssFun.toInt(tradeNum);
            //NSB+日期+序号:交易编号生成规则
            FNum = "NSB" + strNumDate + YssFun.formatNumber(tmpNum, "000000000");
            return FNum;
        } catch (Exception e) {
            throw new YssException("通过函数获取网下新股新债交易编号出错",e);
        }
	}
    

	/**
     * 债券转托管及上市交易编号
     * TODO <Method comments>
     * @return	TASK #2673::请开发部增加交易编号获取的SPRING调用（需要尽快开发）
     * @author shashijie ,2011-3-1
     * @modified
     */
    private String getDevTrustBondNum() throws YssException{
        String tradeNum = "";
        String FNum = "";
        int tmpNum;
        try {
        	String strNumDate = getStrNumDate();
            //系统获取下一个交易编号的值
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName(
					            "Tb_Data_DevTrustBond"), dbl.sqlRight("FNUM", 9), "000000000",
					            " WHERE FNum LIKE 'DTB" + strNumDate + "%'", 1);
            tmpNum = YssFun.toInt(tradeNum);
            //DTB+日期+序号:交易编号生成规则
            FNum = "DTB" + strNumDate + YssFun.formatNumber(tmpNum, "000000000");
            return FNum;
        } catch (Exception e) {
            throw new YssException("通过函数获取债券转托管及上市交易编号出错",e);
        }
	}

	/**
     * 获取开放式基金交易数据编号
     * TODO <Method comments>
     * @return	TASK #2673::请开发部增加交易编号获取的SPRING调用（需要尽快开发）
     * @author shashijie ,2011-3-1
     * @modified
     */
    private String getOpenFundTradeNum() throws YssException {
        String tradeNum = "";
        String FNum = "";
        int tmpNum;
        try {
        	String strNumDate = getStrNumDate();
            //系统获取下一个交易编号的值
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName(
					            "Tb_Data_OpenFundTrade"), dbl.sqlRight("FNUM", 9), "000000000",
					            " WHERE FNum LIKE 'OTC" + strNumDate + "%'", 1);
            tmpNum = YssFun.toInt(tradeNum);
            //OCT+日期+序号:交易编号生成规则
            FNum = "OTC" + strNumDate + YssFun.formatNumber(tmpNum, "000000000");
            return FNum;
        } catch (Exception e) {
            throw new YssException("通过函数获取开放式基金交易编号出错",e);
        }
	}

	/**
     * 获取期权交易数据编号
     * TODO <Method comments>
     * @return	TASK #2673::请开发部增加交易编号获取的SPRING调用（需要尽快开发）
     * @author shashijie ,2011-3-1
     * @modified
     */
    private String getOptionsTradeNum() throws YssException {
        String tradeNum = "";
        String FNum = "";
        int tmpNum;
        try {
        	String strNumDate = getStrNumDate();
            //系统获取下一个交易编号的值
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName(
					            "TB_Data_OptionsTrade"), dbl.sqlRight("FNUM", 6), "000000",
					            " WHERE FNum LIKE 'P" + strNumDate + "%'", 1);//期权交易数据这里是10,需求规格说明书上写的是1增长,这里以规格说明书为准
            tmpNum = YssFun.toInt(tradeNum);
            //P+日期+序号:交易编号生成规则
            FNum = "P" + strNumDate + YssFun.formatNumber(tmpNum, "000000");
            return FNum;
        } catch (Exception e) {
            throw new YssException("通过函数获取期权交易编号出错",e);
        }
	}

	/**
     * 获取期货交易数据编号
     * TODO <Method comments>
     * @return	TASK #2673::请开发部增加交易编号获取的SPRING调用（需要尽快开发）
     * @author shashijie ,2011-3-1
     * @modified
     */
    private String getFuturesTradeNum() throws YssException{
        String tradeNum = "";
        String FNum = "";
        int tmpNum;
        try {
        	String strNumDate = getStrNumDate();
            //系统获取下一个交易编号的值
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName(
					            "TB_Data_FuturesTrade"), dbl.sqlRight("FNUM", 6), "000000",
					            " WHERE FNum LIKE 'T" + strNumDate + "%'", 1);//期货交易数据这里是10,需求规格说明书上写的是1增长,这里以规格说明书为准
            tmpNum = YssFun.toInt(tradeNum);
            //T+日期+序号:交易编号生成规则
            FNum = "T" + strNumDate + YssFun.formatNumber(tmpNum, "000000");
            return FNum;
        } catch (Exception e) {
            throw new YssException("通过函数获取期权交易编号出错",e);
        }
	}

	/**
     * 获取交易费用金额 xuqiji 20100417
     * @return
     * @throws YssException 
     */
    private String getAHFutruesFeeMoney() throws YssException {
    	String securityCode = "";
        String tradeType = "";
        String portCode = "";
        String brokerCode = "";
        double sumMoney = 0.0;
        ArrayList list = null;
        ArrayList alFeeBeans = null;
        StringBuffer bufShow = new StringBuffer();
        FeeBean fee = null;
        String sShowDataStr = "";
        double dFeeMoney = 0.0;
        double amount = 0.0;
        double accruedinterest = 0.0;
        String dBargainDate = "";
        String curyCode = ""; 
        YssFeeType feeType = null;
        String sFeeNum ="";//取第几个费用的值
        String [] sShowData = null;
        try {
            list = (ArrayList) prepFun.getObj(); 
            securityCode = (String) list.get(0);
            tradeType = (String) list.get(1);
            portCode = (String) list.get(2);
            brokerCode = (String) list.get(3);
            sumMoney = YssFun.toDouble(String.valueOf(list.get(4)));
            amount = YssFun.toDouble(String.valueOf(list.get(5)));
            accruedinterest = YssFun.toDouble(String.valueOf(list.get(6)));
            if (String.valueOf(list.get(7)).length() > 10) { 
                dBargainDate = String.valueOf(list.get(7)).substring(0, 10);
            } else {
                dBargainDate = String.valueOf(list.get(7));
                dBargainDate = YssFun.left(dBargainDate, 4) +
                    "-" + YssFun.mid(dBargainDate, 4, 2) + "-" +
                    YssFun.right(dBargainDate, 2);
            }
            curyCode = String.valueOf(list.get(8)); 
            sFeeNum = String.valueOf(list.get(9));
            BaseOperDeal baseOper = this.getSettingOper();
            BaseFeeDeal feeOper = (BaseFeeDeal) pub.getOperDealCtx().getBean(
                "feedeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);
            feeOper.setFeeAttr(securityCode.trim(), tradeType, portCode, //去掉证券代码后的多余空格
                               brokerCode, sumMoney);
            feeOper.setCurrencyCode(curyCode); 
            alFeeBeans = feeOper.getFeeBeans();
            if (alFeeBeans != null) {
                feeType = new YssFeeType();
                feeType.setMoney(sumMoney);
                feeType.setInterest(accruedinterest);
                feeType.setAmount(amount);
                feeType.setCost( -1);
                feeType.setIncome( -1);
                feeType.setFee( -1);
                if (alFeeBeans.size() > 0) {
                    for (int i = 0; i < alFeeBeans.size(); i++) {
                        fee = (FeeBean) alFeeBeans.get(i);
                        dFeeMoney = baseOper.calFeeMoney(feeType, fee,
                            YssFun.toDate(dBargainDate));
                        bufShow.append(fee.getFeeCode()).append(",");
                        bufShow.append(dFeeMoney).append(",");
                    }
                    for (int j = 0; j < 8 - alFeeBeans.size(); j++) {
                        bufShow.append(" ").append(",");
                        bufShow.append(" ").append(",");
                    }
                } else { //如果在系统中没有关联费用链接的话,就存入空的
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                }
            }
            if (bufShow.toString().length() > 1) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 1);
            }
            sShowData = sShowDataStr.split(",");
            if(sFeeNum.equalsIgnoreCase("1")){
            	sShowDataStr = sShowData[1];
            }else if(sFeeNum.equalsIgnoreCase("2")){
            	sShowDataStr = sShowData[3];
            }else if(sFeeNum.equalsIgnoreCase("3")){
            	sShowDataStr = sShowData[5];
            }else if(sFeeNum.equalsIgnoreCase("4")){
            	sShowDataStr = sShowData[7];
            }else if(sFeeNum.equalsIgnoreCase("5")){
            	sShowDataStr = sShowData[9];
            }else if(sFeeNum.equalsIgnoreCase("6")){
            	sShowDataStr = sShowData[11];
            }else if(sFeeNum.equalsIgnoreCase("7")){
            	sShowDataStr = sShowData[13];
            }else if(sFeeNum.equalsIgnoreCase("8")){
            	sShowDataStr = sShowData[15];
            }
            return sShowDataStr;
        } catch (Exception e) {
            throw new YssException("获取交易费用金额出错！");
        }
	}
    /**
     * 获取交易费用代码 xuqiji 20100417
     * @return
     * @throws YssException 
     */
	private String getAHFutruesFeeCode() throws YssException {
		String securityCode = "";
        String tradeType = "";
        String portCode = "";
        String brokerCode = "";
        double sumMoney = 0.0;
        ArrayList list = null;
        ArrayList alFeeBeans = null;
        StringBuffer bufShow = new StringBuffer();
        FeeBean fee = null;
        String sShowDataStr = "";
        double dFeeMoney = 0.0;
        double amount = 0.0;
        double accruedinterest = 0.0;
        String dBargainDate = "";
        String curyCode = ""; //现在的货币从交易数据中取；而不是通过证券中取
        YssFeeType feeType = null;
        String sFeeNum ="";//取第几个费用的值
        String [] sShowData = null;
        try {
            list = (ArrayList) prepFun.getObj(); 
            securityCode = (String) list.get(0);
            tradeType = (String) list.get(1);
            portCode = (String) list.get(2);
            brokerCode = (String) list.get(3);
            sumMoney = YssFun.toDouble(String.valueOf(list.get(4)));
            amount = YssFun.toDouble(String.valueOf(list.get(5)));
            accruedinterest = YssFun.toDouble(String.valueOf(list.get(6)));
            if (String.valueOf(list.get(7)).length() > 10) {
                dBargainDate = String.valueOf(list.get(7)).substring(0, 10);
            } else {
                dBargainDate = String.valueOf(list.get(7));
                dBargainDate = YssFun.left(dBargainDate, 4) +
                    "-" + YssFun.mid(dBargainDate, 4, 2) + "-" +
                    YssFun.right(dBargainDate, 2);
            }
            curyCode = String.valueOf(list.get(8)); 
            sFeeNum = String.valueOf(list.get(9));
            
            BaseOperDeal baseOper = this.getSettingOper();
            BaseFeeDeal feeOper = (BaseFeeDeal) pub.getOperDealCtx().getBean(
                "feedeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);
            feeOper.setFeeAttr(securityCode.trim(), tradeType, portCode, //去掉证券代码后的多余空格
                               brokerCode, sumMoney);
            feeOper.setCurrencyCode(curyCode); 
            alFeeBeans = feeOper.getFeeBeans();
            if (alFeeBeans != null) {
                feeType = new YssFeeType();
                feeType.setMoney(sumMoney);
                feeType.setInterest(accruedinterest);
                feeType.setAmount(amount);
                feeType.setCost( -1);
                feeType.setIncome( -1);
                feeType.setFee( -1);
                if (alFeeBeans.size() > 0) {
                    for (int i = 0; i < alFeeBeans.size(); i++) {
                        fee = (FeeBean) alFeeBeans.get(i);
                        dFeeMoney = baseOper.calFeeMoney(feeType, fee,
                            YssFun.toDate(dBargainDate));
                        bufShow.append(fee.getFeeCode()).append(",");
                        bufShow.append(dFeeMoney).append(",");
                    }
                    for (int j = 0; j < 8 - alFeeBeans.size(); j++) {
                        bufShow.append(" ").append(",");
                        bufShow.append(" ").append(",");
                    }
                } else { //如果在系统中没有关联费用链接的话,就存入空的 
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                }
            }
            if (bufShow.toString().length() > 1) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 1);
            }
            sShowData = sShowDataStr.split(",");
            if(sFeeNum.equalsIgnoreCase("1")){
            	sShowDataStr = sShowData[0];
            }else if(sFeeNum.equalsIgnoreCase("2")){
            	sShowDataStr = sShowData[2];
            }else if(sFeeNum.equalsIgnoreCase("3")){
            	sShowDataStr = sShowData[4];
            }else if(sFeeNum.equalsIgnoreCase("4")){
            	sShowDataStr = sShowData[6];
            }else if(sFeeNum.equalsIgnoreCase("5")){
            	sShowDataStr = sShowData[8];
            }else if(sFeeNum.equalsIgnoreCase("6")){
            	sShowDataStr = sShowData[10];
            }else if(sFeeNum.equalsIgnoreCase("7")){
            	sShowDataStr = sShowData[12];
            }else if(sFeeNum.equalsIgnoreCase("8")){
            	sShowDataStr = sShowData[14];
            }
            return sShowDataStr;
        } catch (Exception e) {
            throw new YssException("获取交易费用代码出错！");
        }
	}

	/**
     * 获取股指期货成交编号 xuqiji 20100417
     * @return
     * @throws YssException 
     */
    private String getAHFutruesNum() throws YssException {
    	 ArrayList list = null;
         String sTradeDate = "";
         String tradeNum = "";
         String key = "";
         String strNumDate ="";
         String strTmp = "";
         try {
        	 list = (ArrayList) prepFun.getObj();
             sTradeDate = String.valueOf(list.get(0)).substring(0, 10); //业务日期
             ImpCusInterface cusInterface = (ImpCusInterface) list.get(1);
             
             strNumDate = YssFun.formatDatetime(YssFun.toDate(
            		 sTradeDate)).
                 substring(0, 8);
             //修改原因:由于同一只证券是通过两个券商下单的,所以要读两次数据
             tradeNum = "T" + YssFun.formatDate(sTradeDate, "yyyyMMdd") +
             	dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_FuturesTrade"),
                                    dbl.sqlRight("FNUM", 5), "000000",
                                    " where FNum like 'T"
                                    + YssFun.formatDate(sTradeDate, "yyyyMMdd") +
                                    "%'",10);

             if (tradeNum.endsWith("000000")) { //判断是否在交易表中当天是否读过买或者卖的数据,
            	 tradeNum = tradeNum.substring(0,13) + "10"; //如果数据表中没有读过当天的数据的话，就用前台传过来的数据作为初始值；为何要+"1"因为第一笔数据总是从10001或者90001开头的
             } 
             key = tradeNum;
             strTmp = cusInterface.getNumForStep(key, tradeNum.substring(9),10);
             if(strTmp.length() == 6){
            	 tradeNum = tradeNum.substring(0, 9) + strTmp;
             }else{
            	 tradeNum = tradeNum.substring(0, 9) + YssFun.formatNumber(Double.parseDouble(strTmp),"000000");
             }             	 
             return tradeNum ;
         } catch (Exception ex) {
             throw new YssException("获取股指期货当日" + sTradeDate + "的最大交易编号出错！", ex);
         }
	}

	/**
     * QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
     * 获取股指期货的交易编号
     * @return String
     * @throws YssException
     */
    //参数: 成交日期
    public String getFutruesNum() throws YssException {
        ArrayList list = null;
        String sTradeDate = "";
        try {
            list = (ArrayList) prepFun.getObj();
            sTradeDate = String.valueOf(list.get(0)).substring(0, 10); //业务日期
            return "T" + YssFun.formatDate(sTradeDate, "yyyyMMdd") +
                dbFun.getNextInnerCode(pub.yssGetTableName(
                    "Tb_Data_FuturesTrade"),
                                       dbl.sqlRight("FNUM", 5), "000000",
                                       " where FNum like 'T"
                                       + YssFun.formatDate(sTradeDate, "yyyyMMdd") +
                                       "%'");
        } catch (Exception ex) {
            throw new YssException("获取股指期货当日" + sTradeDate + "的最大交易编号出错！", ex);
        }
    }

    /**
     * QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
     * 计算或获取股指期货的保证金
     * @return double
     * @throws YssException
     */
    //参数：证券代码、组合代码、交易类型、成交日期、成交数量、成交价格
    public double getFutruesBail() throws YssException {
        String sSecurityCode = ""; //股指期货证券代码
        String sPortCode = ""; //组合代码
        String sTradeDate = ""; //交易日期
        String sTradeTypeCode = ""; //业务类型
        double dTradeAmount = 0D; //交易数量
        double dTradePrice = 0D; //交易价格
        ArrayList list = null;
        IndexFuturesBean indexFutures = null;
        double dFutruesBail = 0D; //保证金金额
        try {
            list = (ArrayList) prepFun.getObj();
            sSecurityCode = (String) list.get(0); //证券代码
            sPortCode = (String) list.get(1); //组合代码
            sTradeTypeCode = (String) list.get(2); //交易类型，平仓/开仓
            sTradeDate = String.valueOf(list.get(3)).substring(0, 10); //业务日期
            dTradeAmount = YssFun.toDouble(String.valueOf(list.get(4))); //成交数量
            dTradePrice = YssFun.toDouble(String.valueOf(list.get(5))); //成交价格
            if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_KC)) { //开仓 20
                indexFutures = new IndexFuturesBean();
                indexFutures.setYssPub(pub);
                indexFutures.setSecurityCode(sSecurityCode);
                indexFutures.getSetting();
                if (indexFutures.getBailType().equalsIgnoreCase("Scale")) { //比例
                    //比例     保证金=交易数量 * 成交价格 * 放大倍数 * 保证金比例
                    dFutruesBail = YssD.round(YssD.mul(dTradeAmount, dTradePrice, indexFutures.getMultiple(), indexFutures.getBailScale()), 4); //保留四位小数
                } else if (indexFutures.getBailType().equalsIgnoreCase("Fix")) { //固定
                    //每手固定 保证金=交易数量 * 每手固定保证金
                    dFutruesBail = YssD.round(YssD.mul(dTradeAmount, indexFutures.getBailFix()), 4);
                } else {
                    dFutruesBail = 0D; //其他情况返回0
                }
            } else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PC)) { //平仓 21
                //保证金=昨日初始保证金余额 / 昨日库存数量 * 今日平仓数量
                FuturesTradeBean futures = new FuturesTradeBean();
                futures.setSecurityCode(sSecurityCode);
                futures.setPortCode(sPortCode);
                futures.setBargainDate(sTradeDate);
                futures.setTradeAmount(dTradeAmount);
                FuturesTradeAdmin futuresTrade = new FuturesTradeAdmin();
                futuresTrade.setYssPub(pub);
                dFutruesBail = futuresTrade.getPCBZJ(futures);
            }
        } catch (Exception ex) {
            throw new YssException("获取股指期货的保证金出错！", ex);
        }
        return dFutruesBail;
    }

    /**
     * 获取股指期货初始保证金的帐户
     * @return String
     * QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
     * @throws YssException
     */
    //参数：证券代码、交易类型、券商、投资经理、组合
    public String getFutruesBegAccCode() throws YssException {
        String portCode = ""; //组合代码
        String invMgrCode = ""; //投资经理
        String tradeTypeCode = ""; //交易类型
        String brokerCode = ""; //券商
        String securityCode = ""; //证券
        java.util.Date startDate; //调用日期，自动获取接口的执行日期
        CashAccountBean account = null, auxiAccount = null;
        ArrayList list;
        try {
            list = (ArrayList) prepFun.getObj();
            securityCode = (String) list.get(0); //证券代码
            tradeTypeCode = (String) list.get(1); //交易类型
            brokerCode = (String) list.get(2); //券商
            invMgrCode = (String) list.get(3); //投资经理
            portCode = (String) list.get(4); //组合

            startDate = (java.util.Date) list.get(5); //调用日期,自动获取

            BaseCashAccLinkDeal cashacc = new BaseCashAccLinkDeal();
            cashacc.setYssPub(pub);
            cashacc.setLinkParaAttr(invMgrCode, portCode, securityCode, brokerCode, tradeTypeCode, startDate);
            account = cashacc.getCashAccountBean();
            auxiAccount = cashacc.getAuxiCashAccount();
            if (auxiAccount == null) {
                if (account == null) {
                    auxiAccount = new CashAccountBean();
                    auxiAccount.setStrCashAcctCode(" "); //如果变动保证金帐户为空的话,初始保证金帐户也为空
                } else {
                    auxiAccount = account; //如果初始保证金帐户为空，则取变动保证金帐户的信息
                }
            }
        } catch (Exception ex) {
            throw new YssException("获取证券初始保证金帐户出错！", ex);
        }
        return auxiAccount.getStrCashAcctCode();
    }

    /**
     * 获取股指期货变动保证金的帐户
     * @return String
     * QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
     * @throws YssException
     */
    //参数：证券代码、交易类型、券商、投资经理、组合
    public String getFutruesChangeAccCode() throws YssException {
        String portCode = ""; //组合代码
        String invMgrCode = ""; //投资经理
        String tradeTypeCode = ""; //交易类型
        String brokerCode = ""; //券商
        String securityCode = ""; //证券
        java.util.Date startDate; //调用日期，自动获取接口的执行日期
        CashAccountBean account = null;
        ArrayList list;
        try {
            list = (ArrayList) prepFun.getObj();
            securityCode = (String) list.get(0); //证券代码
            tradeTypeCode = (String) list.get(1); //交易类型
            brokerCode = (String) list.get(2); //券商
            invMgrCode = (String) list.get(3); //投资经理
            portCode = (String) list.get(4); //组合

            startDate = (java.util.Date) list.get(5); //调用日期

            BaseCashAccLinkDeal cashacc = new BaseCashAccLinkDeal();
            cashacc.setYssPub(pub);
            cashacc.setLinkParaAttr(invMgrCode, portCode, securityCode, brokerCode, tradeTypeCode, startDate);
            account = cashacc.getCashAccountBean();
            if (account == null) {
                account = new CashAccountBean();
                account.setStrCashAcctCode(" "); //如果为空，赋空值
            }
        } catch (Exception ex) {
            throw new YssException("获取证券变动保证金帐户出错", ex);
        }
        return account.getStrCashAcctCode();
    }

    public String getPortCode() throws YssException {
        ArrayList list = null;
        String portClsCode = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            portClsCode = (String) list.get(0);
            TaPortClsBean portCls = new TaPortClsBean();
            portCls.setYssPub(pub);
            portCls.setPortClsCode(portClsCode);
            portCls.getSetting();
            return portCls.getPortCode();
        } catch (Exception e) {
            throw new YssException("获取投资组合出错");
        }
    }

    /**
     * 20071109    chenyibo
     * @return String
     * @throws YssException
     */
    public String dealTaTrade() throws YssException {
        String strSql = "";
        ArrayList list = null;
        String targetTable = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            targetTable = (String) list.get(0);
            strSql = "delete from " + pub.yssGetTableName(targetTable) +
                " where FSellMoney=0";
            dbl.executeSql(strSql);
            return "";
        } catch (Exception e) {
            throw new YssException("处理TA交易数据出错");
        }
    }

    /**
     *
     * @return String
     * @throws YssException
     */
    public String getSubTradeNumBlm() throws YssException {
        String subTradenum = "";
        String strNumDate = "";
        String bargainDate = "";
        ArrayList list = null;
        String tradeNum = "";
        String subTradeNum = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            if (String.valueOf(list.get(0)).length() >= 10) {
                bargainDate = String.valueOf(list.get(0)).substring(0, 10);
            } else {
                bargainDate = String.valueOf(list.get(0));
                bargainDate = YssFun.left(bargainDate, 4) +
                    "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
                    YssFun.right(bargainDate, 2);
            }
            strNumDate = YssFun.formatDatetime(YssFun.toDate(
                bargainDate)).
                substring(0, 8);

            subTradenum = strNumDate +
                dbFun.getNextInnerCode(pub.yssGetTableName(
                    "Tb_Data_SubTrade_blm"),
                                       dbl.sqlRight("FNUM", 5), "00000",
                                       " where FNum like '"
                                       + subTradenum.replaceAll("'", "''") +
                                       "%'");

            subTradenum = "T" + subTradenum;
            return subTradenum;
        } catch (Exception e) {
            throw new YssException("获取彭博交易子表Num出错");
        }
    }

    /**
     *
     * @throws YssException
     */
    //  考虑到农行的接口是从swift接口转换过来的      20071103   chenyibo
    //  所以有一个取消和修改数据的概念
    public void dealTradeData() throws YssException {
        String desc = "";
        String tradeNum = "";
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from tmp_subTrade";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                desc = rs.getString("FDESC");
                if (desc.length() > 0) {
                    if (desc.substring(0, 1).equalsIgnoreCase("C")) {
                        tradeNum = desc.substring(3);
                        strSql = "delete from tmp_subTrade where FORDERNUM=" +
                            dbl.sqlString(tradeNum);
                        dbl.executeSql(strSql);
                        strSql = "delete from tmp_subTrade where FDESC=" +
                            dbl.sqlString(desc);
                        dbl.executeSql(strSql);
                    } else if (desc.substring(0, 1).equalsIgnoreCase("U")) {
                        tradeNum = desc.substring(3);
                        strSql = "delete from tmp_subTrade where FORDERNUM=" +
                            dbl.sqlString(tradeNum);
                        dbl.executeSql(strSql);
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("处理swift数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * //20071022  chenyibo   获取TA确认日期
     * @return String
     * @throws YssException
     */
    public String getTaConfirmDate() throws YssException {
        String dBargainDate = "";
        ArrayList list = new ArrayList();
        String sellNetCode = "";
        String sellTypeCode = "";
        String curyCode = "";
        String portClsCode = ""; //数据接口预处理中还需要增加分级组合代码和组合代码的数值fazmm20071122
        String portCode = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sellNetCode = (String) list.get(0);
            sellTypeCode = (String) list.get(1); //sellNetCode,sellTypeCode,curyCode 做为查寻的条件   chenyibo  20071029
            curyCode = (String) list.get(2);
            if (String.valueOf(list.get(3)).length() >= 10) {
                dBargainDate = String.valueOf(list.get(3)).substring(0, 10);
            } else {
                dBargainDate = String.valueOf(list.get(3));
                dBargainDate = YssFun.left(dBargainDate, 4) +
                    "-" + YssFun.mid(dBargainDate, 4, 2) + "-" +
                    YssFun.right(dBargainDate, 2);
            }

            portClsCode = (String) list.get(4);

            TaPortClsBean portCls = new TaPortClsBean();
            portCls.setYssPub(pub);
            portCls.setPortClsCode(portClsCode);
            portCls.getSetting();
            portCode = portCls.getPortCode();

            TaTradeBean ta = new TaTradeBean();
            ta.setYssPub(pub);
            dBargainDate = YssFun.formatDate(ta.getConfirmDay(sellNetCode,
                portClsCode, portCode, sellTypeCode, curyCode,
                YssFun.parseDate(dBargainDate)));
            return dBargainDate;
        //add by zhaoxianlin 20130313 STORY #3445--start  
        }catch (YssException ye) {
            throw new YssException(ye.getMessage());
        } 
       //add by zhaoxianlin 20130313 STORY #3445--end
        catch (Exception e) {
            throw new YssException("获取TA确认日期出错");
        }
    }

    /**
     *  //20071022  chenyibo   获取TA结算日期
     * @return String
     * @throws YssException
     */
    //修改说明    chenyibo   20071115       改成从TA结算javabean中取结算延迟天数和节假日群
    public String getTaSettleDate() throws YssException {
        String sellNetCode = "";
        String sellTypeCode = "";
        String curyCode = "";
        ArrayList list = new ArrayList();
        ResultSet rs = null;
        String dBargainDate = "";
        String portClsCode = ""; //预处理传入的变量中还需增加分级组合代码fazmm20071122
        String portCode = ""; //预处理传入的变量中还需增加组合代码fazmm20071122
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sellNetCode = (String) list.get(0);
            sellTypeCode = (String) list.get(1);
            curyCode = (String) list.get(2);
            if (String.valueOf(list.get(3)).length() >= 10) {
                dBargainDate = String.valueOf(list.get(3)).substring(0, 10);
            } else {
                dBargainDate = String.valueOf(list.get(3));
                dBargainDate = YssFun.left(dBargainDate, 4) +
                    "-" + YssFun.mid(dBargainDate, 4, 2) + "-" +
                    YssFun.right(dBargainDate, 2);
            }
            portClsCode = (String) list.get(4);

            TaPortClsBean portCls = new TaPortClsBean();
            portCls.setYssPub(pub);
            portCls.setPortClsCode(portClsCode);
            portCls.getSetting();
            portCode = portCls.getPortCode();

            TaTradeBean ta = new TaTradeBean();
            ta.setYssPub(pub);
            dBargainDate = YssFun.formatDate(ta.getSettleDay(sellNetCode,
                portClsCode, portCode, sellTypeCode, curyCode,
                YssFun.parseDate(dBargainDate)));
            return dBargainDate;
        }
        //------ add by wangzuochun 2011.01.13 BUG #723 读取TA赎回数据时提示获取结算日出错 
        catch(YssException ye){
        	 throw new YssException(ye.getMessage());
        }
        //---------------- BUG #723 -----------------//
        catch (Exception e) {
            throw new YssException("获取TA结算日期出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * //通过日期获取电子对账数据编号；2011-8-12 ；；；
     * @return String
     * @throws YssException
     */
    
    public String getDZAccountNum() throws YssException{
	      String DZNum ="";
	      String DZDate="";
	      ArrayList list=null;
	      try{
//	         list =(ArrayList)obj;
	         list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
	         DZDate=YssFun.formatDate(YssFun.toDate(String.valueOf(list.get(0)).substring(0,10)),"yyyyMMdd");
	         DZNum = DZDate +
	               dbFun.getNextInnerCode("tdzbbinfo",
	                                      dbl.sqlRight("FSN", 5), "00001",
	                                      " where FSN like 'DZ"
	                                      + DZDate + "%'", 1);
	         DZNum="DZ"+DZNum;
	         return DZNum;
	      }catch(Exception e){
	         throw new YssException("获取电子对账编号出错",e);
	      }
	   }

    /**
     * //20080808   通过确认日获取TA业务日期 zhouss
     * @return String
     * @throws YssException
     */
    public String getTaTradeDateByConfirmDate() throws YssException {
        String confirmDate = "";
        ArrayList list = new ArrayList();
        String sellNetCode = "";
        String sellTypeCode = "";
        String curyCode = "";
        String portClsCode = ""; //数据接口预处理中还需要增加分级组合代码和组合代码的数值fazmm20071122
        String portCode = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sellNetCode = (String) list.get(0);
            sellTypeCode = (String) list.get(1); //sellNetCode,sellTypeCode,curyCode 做为查寻的条件   chenyibo  20071029
            curyCode = (String) list.get(2);
            if (String.valueOf(list.get(3)).length() >= 10) {
                confirmDate = String.valueOf(list.get(3)).substring(0, 10);
            } else {
                confirmDate = String.valueOf(list.get(3));
                confirmDate = YssFun.left(confirmDate, 4) +
                    "-" + YssFun.mid(confirmDate, 4, 2) + "-" +
                    YssFun.right(confirmDate, 2);
            }

            portClsCode = (String) list.get(4);

            TaPortClsBean portCls = new TaPortClsBean();
            portCls.setYssPub(pub);
            portCls.setPortClsCode(portClsCode);
            portCls.getSetting();
            portCode = portCls.getPortCode();

            TaTradeBean ta = new TaTradeBean();
            ta.setYssPub(pub);
            String tradeDate = YssFun.formatDate(ta.gettaTradeDateByconfirmDate(sellNetCode,
                portClsCode, portCode, sellTypeCode, curyCode,
                YssFun.parseDate(confirmDate)));
            return tradeDate;
        } catch (Exception e) {
            throw new YssException("获取TA业务日期出错");
        }
    }

    /**
     * //20080808   通过确认日获取TA结算日日期 zhouss
     * @return String
     * @throws YssException
     */
    public String getTaSettleDateByConfirmDate() throws YssException {
        String sellNetCode = "";
        String sellTypeCode = "";
        String curyCode = "";
        ArrayList list = new ArrayList();
        ResultSet rs = null;
        String confirmDate = "";
        String portClsCode = ""; //预处理传入的变量中还需增加分级组合代码fazmm20071122
        String portCode = ""; //预处理传入的变量中还需增加组合代码fazmm20071122
        try {
//      list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sellNetCode = (String) list.get(0);
            sellTypeCode = (String) list.get(1);
            curyCode = (String) list.get(2);
            if (String.valueOf(list.get(3)).length() >= 10) {
                confirmDate = String.valueOf(list.get(3)).substring(0, 10);
            } else {
                confirmDate = String.valueOf(list.get(3));
                confirmDate = YssFun.left(confirmDate, 4) +
                    "-" + YssFun.mid(confirmDate, 4, 2) + "-" +
                    YssFun.right(confirmDate, 2);
            }
            portClsCode = (String) list.get(4);

            TaPortClsBean portCls = new TaPortClsBean();
            portCls.setYssPub(pub);
            portCls.setPortClsCode(portClsCode);
            portCls.getSetting();
            portCode = portCls.getPortCode();
            TaTradeBean ta = new TaTradeBean();
            ta.setYssPub(pub);
            String tradeDate = YssFun.formatDate(ta.gettaTradeDateByconfirmDate(sellNetCode,
                portClsCode, portCode, sellTypeCode, curyCode,
                YssFun.parseDate(confirmDate)));

            String settleDate = YssFun.formatDate(ta.getSettleDay(sellNetCode,
                portClsCode, portCode, sellTypeCode, curyCode,
                YssFun.parseDate(tradeDate)));
            return settleDate;
        } catch (Exception e) {
            throw new YssException("获取TA结算日期出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getTotalCost() throws YssException {
        String securityCode = "";
        String tradeType = "";
        String portCode = "";
        String brokerCode = "";
        double sumMoney = 0.0;
        ArrayList list = null;
        ArrayList alFeeBeans = null;
        FeeBean fee = null;
        String sShowDataStr = "";
        double dFeeMoney = 0.0;
        double amount = 0.0;
        double accruedinterest = 0.0;
        YssFeeType feeType = null;
        double totalCost = 0.0;
        double totalFee = 0.0;
        String dBargainDate = "";
        String curyCode = "";
        try {

//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            securityCode = (String) list.get(0);
            tradeType = (String) list.get(1);
            portCode = (String) list.get(2);
            brokerCode = (String) list.get(3);
            sumMoney = YssFun.toDouble(String.valueOf(list.get(4)));
            amount = YssFun.toDouble(String.valueOf(list.get(5)));
            accruedinterest = YssFun.toDouble(String.valueOf(list.get(6)));
            if (String.valueOf(list.get(7)).length() > 10) { //20071022  chenyibo
                dBargainDate = String.valueOf(list.get(7)).substring(0, 10);
            } else {
                dBargainDate = String.valueOf(list.get(7));
                dBargainDate = YssFun.left(dBargainDate, 4) +
                    "-" + YssFun.mid(dBargainDate, 4, 2) + "-" +
                    YssFun.right(dBargainDate, 2);
            }
            curyCode = String.valueOf(list.get(8));
            BaseOperDeal baseOper = this.getSettingOper();
            BaseFeeDeal feeOper = (BaseFeeDeal) pub.getOperDealCtx().getBean(
                "feedeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);
            feeOper.setFeeAttr(securityCode, tradeType, portCode,
                               brokerCode, sumMoney);
            feeOper.setCurrencyCode(curyCode); //20071022  chenyibo  现在到交易数据中取货币
            alFeeBeans = feeOper.getFeeBeans();
            totalCost = sumMoney;
            if (alFeeBeans != null) {
                feeType = new YssFeeType();
                feeType.setMoney(sumMoney);
                feeType.setInterest(accruedinterest);
                feeType.setAmount(amount);
                feeType.setCost( -1);
                feeType.setIncome( -1);
                feeType.setFee( -1);
                if (alFeeBeans.size() > 0) {
                    for (int i = 0; i < alFeeBeans.size(); i++) {
                        fee = (FeeBean) alFeeBeans.get(i);
                        dFeeMoney = baseOper.calFeeMoney(feeType, fee,
                            YssFun.toDate(dBargainDate));
                        totalFee = totalFee + dFeeMoney;

                    }
                }
                if (tradeType.equalsIgnoreCase("01")) {
                    totalCost = YssD.add(totalCost, totalFee);
                } else {
                    totalCost = YssD.sub(totalCost, totalFee);
                }
            }
            return totalCost + "";
        } catch (Exception e) {
            throw new YssException("获取费用出错");
        }

    }

    /**
     *
     * @return String
     * @throws YssException
     */
    public String getTradeNum() throws YssException {
        String strNumDate = "";
        String bargainDate = "";
        ArrayList list = null;
        String tradeNum = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            if (String.valueOf(list.get(0)).length() >= 10) {
                bargainDate = String.valueOf(list.get(0)).substring(0, 10);
            } else {
                bargainDate = String.valueOf(list.get(0));
                bargainDate = YssFun.left(bargainDate, 4) +
                    "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
                    YssFun.right(bargainDate, 2);
            }
            strNumDate = YssFun.formatDatetime(YssFun.toDate(
                bargainDate)).
                substring(0, 8);
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Trade"),
                                              dbl.sqlRight("FNum", 6), "000000",
                                              " where FNum like '"
                                              + tradeNum.replaceAll("'", "''") +
                                              "%'");
            tradeNum = "T" + strNumDate + tradeNum;
            return tradeNum;
        } catch (Exception e) {
            throw new YssException("获取交易主表Num出错");
        }
    }

    public String getRateTradeNum() throws YssException {
        String subTradenum = "";
        String strNumDate = "";
        String bargainDate = "";
        ArrayList list = null;
        String beginNum = "000000";
        String tradeNum = "";
        String NumHeader = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            if (String.valueOf(list.get(0)).length() >= 10) {
                bargainDate = String.valueOf(list.get(0)).substring(0, 10);
            } else {
                bargainDate = String.valueOf(list.get(0));
                bargainDate = YssFun.left(bargainDate, 4) +
                    "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
                    YssFun.right(bargainDate, 2);
            }

            strNumDate = YssFun.formatDatetime(YssFun.toDate(
                bargainDate)).
                substring(0, 8);
            NumHeader = "T" + strNumDate;
            tradeNum = dbFun.getNextInnerCode
                (pub.yssGetTableName("Tb_Data_RateTrade"),
                 dbl.sqlSubStr("FNUM", "10", "6"),
                 beginNum,
                 " where FNUM like '"
                 + NumHeader.replaceAll("'", "''") + "%'");
            return NumHeader + tradeNum;
        } catch (Exception e) {
            throw new YssException("获取外汇交易表Num出错");
        }
    }

    /**
     *
     * @return String
     * @throws YssException
     */
    public String getSubTradeNum() throws YssException {
        String strNumDate = "";
        String bargainDate = "";
        ArrayList list = null;
        String tradeNum = "";
        String subTradeNum = "00000";
        String beginNum = "";
        StringBuffer buf = null;
        String key = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            if (String.valueOf(list.get(0)).length() >= 10) {
                bargainDate = String.valueOf(list.get(0)).substring(0, 10);
            } else {
                bargainDate = String.valueOf(list.get(0));
                bargainDate = YssFun.left(bargainDate, 4) +
                    "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
                    YssFun.right(bargainDate, 2);
            }
            beginNum = (String) list.get(1); //20071114   chenyibo   读入的交易数据的交易流水号要按照买入用200001,卖出用900001;分红用100001
            ImpCusInterface cusInterface = (ImpCusInterface) list.get(2);

            strNumDate = YssFun.formatDatetime(YssFun.toDate(
                bargainDate)).
                substring(0, 8);
            //修改原因:由于同一只证券是通过两个券商下单的,所以要读两次数据 //20080623
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName("tb_data_subtrade"),
                                              dbl.sqlSubStr("Fnum", "10", "6"), "000000",
                                              " where FNum like '"
                                              + ("T" + strNumDate + beginNum.substring(0, 1)).replaceAll("'", "''") +
                                              "%'");
            if (tradeNum.equalsIgnoreCase("000000")) { //判断是否在交易表中当天是否读过买或者卖的数据,
                beginNum = beginNum.substring(0, 5) + "1"; //如果数据表中没有读过当天的数据的话，就用前台传过来的数据作为初始值；为何要+"1"因为第一笔数据总是从10001或者90001开头的
            } else {
                beginNum = tradeNum; //如果有数据存在就用数据表中最大的那个编号作为初始值
            }
            key = beginNum.substring(0, 1);
            tradeNum = cusInterface.getNum(key, beginNum);
            return "T" + strNumDate + tradeNum + subTradeNum;
        } catch (Exception e) {
            throw new YssException("获取交易子表Num出错");
        }
    }

    public String getBeginNum(String strNum) throws YssException {
        String result = "";
        int iNum = 0;
        String sFormat = "";
        try {
            iNum = Integer.parseInt(strNum);
            iNum = iNum + 1;
            if (strNum.length() > 0) {
                for (int j = 0; j < strNum.length(); j++) {
                    sFormat += "0";
                }
                result = YssFun.formatNumber(iNum, sFormat);
            }

            return result;
        } catch (Exception e) {
            throw new YssException("获取交易子表Num出错");
        }
    }
    
    
    /**
    * 获取银行间债券交易流水号  临时添加
    * shenjie 
    * @return String
    * @throws YssException
    */
   public String getBakBondNum() throws YssException {
       String subTradenum = "";
       String strNumDate = "";
       String tabName = "";
       String bargainDate = "";
       ArrayList list = null;
       String tradeNum = "";
       String subTradeNum = "";
       String NumHeader = "";
       try {
//        list = (ArrayList) obj;
           list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
           if (String.valueOf(list.get(0)).length() >= 10) {
               bargainDate = String.valueOf(list.get(0)).substring(0, 10);
           } else {
               bargainDate = String.valueOf(list.get(0));
               bargainDate = YssFun.left(bargainDate, 4) +
                   "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
                   YssFun.right(bargainDate, 2);
           }
           strNumDate = YssFun.formatDatetime(YssFun.toDate(
               bargainDate)).
               substring(0, 8);
           NumHeader = "IBB" + strNumDate;
           subTradeNum = NumHeader +
               dbFun.getNextInnerCode(pub.yssGetTableName("tb_Data_IntBakBond"),
                                      dbl.sqlRight("FNum", 6), "000000000",
                                      " where FNum like '"
                                      + NumHeader.replaceAll("'", "''") + "%'");
           return subTradeNum;
       } catch (Exception e) {
           throw new YssException("获取银行间债券交易流水号Num出错");
       }
   }

    /**
     *
     * @return String
     * @throws YssException
     */
    public String getTATradeNum() throws YssException {
        String subTradenum = "";
        String strNumDate = "";
        String tabName = "";
        String bargainDate = "";
        ArrayList list = null;
        String tradeNum = "";
        String subTradeNum = "";
        String NumHeader = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            if (String.valueOf(list.get(0)).length() >= 10) {
                bargainDate = String.valueOf(list.get(0)).substring(0, 10);
            } else {
                bargainDate = String.valueOf(list.get(0));
                bargainDate = YssFun.left(bargainDate, 4) +
                    "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
                    YssFun.right(bargainDate, 2);
            }
            strNumDate = YssFun.formatDatetime(YssFun.toDate(
                bargainDate)).
                substring(0, 8);
            NumHeader = "T" + strNumDate;
            subTradeNum = NumHeader +
                dbFun.getNextInnerCode(pub.yssGetTableName("tb_ta_trade"),
                                       dbl.sqlRight("FNum", 6), "000000",
                                       " where FNum like '"
                                       + NumHeader.replaceAll("'", "''") + "%'");
            return subTradeNum;
        } catch (Exception e) {
            throw new YssException("获ta交易数据Num出错");
        }
    }

    //-----------------------------------------------
    /**
     * 计算费用
     * @throws YssException
     * @return String
     */
    public String calFee() throws YssException {
        String securityCode = "";
        String tradeType = "";
        String portCode = "";
        String brokerCode = "";
        double sumMoney = 0.0;
        ArrayList list = null;
        ArrayList alFeeBeans = null;
        StringBuffer bufShow = new StringBuffer();
        FeeBean fee = null;
        String sShowDataStr = "";
        double dFeeMoney = 0.0;
        double amount = 0.0;
        double accruedinterest = 0.0;
        String dBargainDate = "";
        String curyCode = ""; //20071022   chenyibo   现在的货币从交易数据中取；而不是通过证券中取
        YssFeeType feeType = null;
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            securityCode = (String) list.get(0);
            tradeType = (String) list.get(1);
            portCode = (String) list.get(2);
            brokerCode = (String) list.get(3);
            sumMoney = YssFun.toDouble(String.valueOf(list.get(4)));
            amount = YssFun.toDouble(String.valueOf(list.get(5)));
            accruedinterest = YssFun.toDouble(String.valueOf(list.get(6)));
            if (String.valueOf(list.get(7)).length() > 10) { //20071022  chenyibo
                dBargainDate = String.valueOf(list.get(7)).substring(0, 10);
            } else {
                dBargainDate = String.valueOf(list.get(7));
                dBargainDate = YssFun.left(dBargainDate, 4) +
                    "-" + YssFun.mid(dBargainDate, 4, 2) + "-" +
                    YssFun.right(dBargainDate, 2);
            }
            curyCode = String.valueOf(list.get(8)); //20071022       chenyibo

            BaseOperDeal baseOper = this.getSettingOper();
            BaseFeeDeal feeOper = (BaseFeeDeal) pub.getOperDealCtx().getBean(
                "feedeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);
            feeOper.setFeeAttr(securityCode.trim(), tradeType, portCode, //彭彪20071029 去掉证券代码后的多余空格
                               brokerCode, sumMoney);
            feeOper.setCurrencyCode(curyCode); //20071022          chenyibo
            alFeeBeans = feeOper.getFeeBeans();
            if (alFeeBeans != null) {
                feeType = new YssFeeType();
                feeType.setMoney(sumMoney);
                feeType.setInterest(accruedinterest);
                feeType.setAmount(amount);
                feeType.setCost( -1);
                feeType.setIncome( -1);
                feeType.setFee( -1);
                if (alFeeBeans.size() > 0) {
                    for (int i = 0; i < alFeeBeans.size(); i++) {
                        fee = (FeeBean) alFeeBeans.get(i);
                        dFeeMoney = baseOper.calFeeMoney(feeType, fee,
                            YssFun.toDate(dBargainDate));
                        bufShow.append(fee.getFeeCode()).append(",");
                        bufShow.append(dFeeMoney).append(",");
                    }
                    for (int j = 0; j < 8 - alFeeBeans.size(); j++) {
                        bufShow.append(" ").append(",");
                        bufShow.append(" ").append(",");
                    }
                } else { //如果在系统中没有关联费用链接的话,就存入空的  chenyibo  20071008
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                    bufShow.append(" ").append(",");
                }
            }
            if (bufShow.toString().length() > 1) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 1);
            }
            return sShowDataStr;
        } catch (Exception e) {
            throw new YssException("获取费用出错");
        }
    }

    //20071024   chenyibo   通过ta模块中的现金帐户联接获取现金帐户
    public String getTaCashAcc() throws YssException {
        ArrayList list = new ArrayList();
        String sellNetCode = "";
        String portClsCode = "";
        String portCode = "";
        String sellTypeCode = "";
        String curyCode = "";
        ResultSet rs = null;
        String strSql = "";
        String cashAcc = "";
        String bargainDate = "";
        TaCashAccLinkBean taCashAccLink = null;
        ArrayList reList = null;
        CashAccountBean account = null;
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sellNetCode = (String) list.get(0);
            portClsCode = (String) list.get(1);
            //  portCode = (String) list.get(2);
            sellTypeCode = (String) list.get(2);
            curyCode = (String) list.get(3);
            java.util.Date begingdate = (java.util.Date) list.get(4);
            java.util.Date enddate = (java.util.Date) list.get(5);
            //     String sPortCodes=(String)list.get(6);        //这里不需要这样处理,组合代码是根据组合分级代码带出来的
            TaPortClsBean portCls = new TaPortClsBean();
            portCls.setYssPub(pub);
            portCls.setPortClsCode(portClsCode);
            portCls.getSetting();

            portCode = portCls.getPortCode();

            /*TaCashAccLinkBean cashAccLink = new TaCashAccLinkBean();
                      cashAccLink.setYssPub(pub);
                      cashAccLink.setSellNetCode(sellNetCode);
                      cashAccLink.setPortClsCode(portClsCode);
                      cashAccLink.setPortCode(portCode);
                      cashAccLink.setSellTypeCode(sellTypeCode);
                      cashAccLink.setCuryCode(curyCode);
                      cashAccLink.getSetting();
                      return cashAccLink.getCashAccCode();*/
            BaseLinkInfoDeal taCashAccOper = (BaseLinkInfoDeal) pub.
                getOperDealCtx().getBean(
                    "TaCashLinkDeal");
            taCashAccOper.setYssPub(pub);
            taCashAccLink = new TaCashAccLinkBean();
            taCashAccLink.setSellNetCode(sellNetCode);
            taCashAccLink.setPortClsCode(portClsCode);
            taCashAccLink.setPortCode(portCode); //20080228  chenyibo
            taCashAccLink.setSellTypeCode(sellTypeCode);
            taCashAccLink.setStartDate(YssFun.formatDate(begingdate));
            taCashAccLink.setCuryCode(curyCode);

            taCashAccOper.setLinkAttr(taCashAccLink);
            reList = taCashAccOper.getLinkInfoBeans();
            if (reList != null) {
                account = (CashAccountBean) reList.get(0);
                if (account != null) {
                    return account.getStrCashAcctCode();
                } else {
                    return " ";
                }
            } else {
                return " ";
            }
        } catch (Exception e) {
            throw new YssException("获取TA现金帐户出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取现金帐户
     * @throws YssException
     * @return String
     */
    public String getCashAcc() throws YssException {
        ArrayList list = new ArrayList();
        String sInvMgrCode = "";
        String sPortCode = "";
        String sSecurityCode = "";
        String sBrokerCode = "";
        String sTradeTypeCode = "";
        String sCuryCode = "";
        CashAccountBean caBean = null;
        CashAccLinkBean cashAccLink = null;
        ArrayList accList = null;
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sInvMgrCode = (String) list.get(0);
            sPortCode = (String) list.get(1);
            sSecurityCode = (String) list.get(2);
            sBrokerCode = (String) list.get(3);
            sTradeTypeCode = (String) list.get(4);
            sCuryCode = (String) list.get(5);
            java.util.Date beginDate = (java.util.Date) list.get(6);
            java.util.Date endDate = (java.util.Date) list.get(7);
            //BaseCashAccLinkDeal cashAccLink = (BaseCashAccLinkDeal) pub.
            //getOperDealCtx().getBean("cashacclinkdeal");
            //cashAccLink.setYssPub(pub);
            //cashAccLink.setCuryCode(sCuryCode);
            //cashAccLink.setLinkParaAttr(sInvMgrCode,
            //sPortCode,
            //sSecurityCode.trim(), //彭彪20071029 去掉证券代码后的多余空格
            //(sBrokerCode == null ? "" : sBrokerCode),
            //(sTradeTypeCode == null ? "" :
            //sTradeTypeCode),
            //new java.util.Date(), sCuryCode); //将币种也传入，才能把交易帐户和实际结算帐户，根据不同币种链接到现金帐户，杨文奇20080107
            //CashAccountBean cashAcc = cashAccLink.getCashAccountBean();
            //if (cashAcc != null) {
            //return cashAcc.getStrCashAcctCode();
            //}
            //else {
            //return "  ";
            //}
            BaseLinkInfoDeal cashacc = (BaseLinkInfoDeal) pub.getOperDealCtx().
                getBean("CashLinkDeal");
            cashacc.setYssPub(pub);
            cashAccLink = new CashAccLinkBean();
            cashAccLink.setStrInvMgrCode(sInvMgrCode);
            cashAccLink.setStrPortCode(sPortCode);
            cashAccLink.setStrSecurityCode(sSecurityCode.trim());
            cashAccLink.setStrBrokerCode(sBrokerCode == null ? "" : sBrokerCode);
            cashAccLink.setStrTradeTypeCode(sTradeTypeCode == null ? "" :
                                            sTradeTypeCode);
            cashAccLink.setDtStartDate(beginDate);
            cashAccLink.setCuryCode(sCuryCode);
            //cashAccLink.setDtStartDate(beginDate);
            cashacc.setLinkAttr(cashAccLink);
            accList = cashacc.getLinkInfoBeans();
            if (accList != null) {
                caBean = (CashAccountBean) accList.get(0);
                if (caBean != null) {
                    return caBean.getStrCashAcctCode();
                } else {
                    return " ";
                }
            } else {
                return " ";
            }
        } catch (Exception e) {
            throw new YssException("获取现金帐户出错");
        }
    }
    
    /**
    * 获取分级组合确认日汇率
    * add by  yeshenghong 20130810 4151
    * @return double
    * @throws YssException
    */
   public double getExchangeRateOnConfirmDate(ArrayList list) throws YssException {
	   String dBargainDate = "";
//       ArrayList list = new ArrayList();
       String sellNetCode = "";
       String sellTypeCode = "";
       String curyCode = "";
       String portClsCode = ""; //数据接口预处理中还需要增加分级组合代码和组合代码的数值fazmm20071122
       String portCode = "";
       
       String bargainDate = "";
       String rateType = "";
       double sResult = 0.0;

       try {
//    	   list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
           sellNetCode = (String) list.get(0);
           sellTypeCode = (String) list.get(1); //sellNetCode,sellTypeCode,curyCode 做为查寻的条件   chenyibo  20071029
           curyCode = (String) list.get(2);
           if (String.valueOf(list.get(3)).length() >= 10) {
               dBargainDate = String.valueOf(list.get(3)).substring(0, 10);
           } else {
               dBargainDate = String.valueOf(list.get(3));
               dBargainDate = YssFun.left(dBargainDate, 4) +
                   "-" + YssFun.mid(dBargainDate, 4, 2) + "-" +
                   YssFun.right(dBargainDate, 2);//获取确认日
           }
           
           portClsCode = (String) list.get(4);

           TaPortClsBean portCls = new TaPortClsBean();
           portCls.setYssPub(pub);
           portCls.setPortClsCode(portClsCode);
           portCls.getSetting();
           portCode = portCls.getPortCode();

           TaTradeBean ta = new TaTradeBean();
           ta.setYssPub(pub);
           dBargainDate = YssFun.formatDate(ta.getConfirmDay(sellNetCode,
               portClsCode, portCode, sellTypeCode, curyCode,
               YssFun.parseDate(dBargainDate)));//获取确认日
           //开始获取汇率
           portCode = (String) list.get(5);
           rateType = (String) list.get(6);
          
           BaseOperDeal operDeal = new BaseOperDeal();
           operDeal.setYssPub(pub);
           sResult = operDeal.getCuryRate(YssFun.toDate(dBargainDate), curyCode,
                                          portCode, rateType);
           return sResult;
       } catch (Exception e) {
           throw new YssException("获取确认日汇率出错");
       }
   }


    /**
     *
     * @return String
     * @throws YssException
     */
    public String getExchangeRate() throws YssException {
        ArrayList list = new ArrayList();
        String bargainDate = "";
        String curyCode = "";
        String portCode = "";
        String rateType = "";
        double sResult = 0.0;

        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); 
            if(list.size()==10)//modified by yeshenghong 4151  获取确认日的汇率
            {
            	sResult = this.getExchangeRateOnConfirmDate(list);
            }else
            {
	            // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
	            //    bargainDate=YssFun.formatDate(new java.util.Date());
	            curyCode = (String) list.get(0);
	            portCode = (String) list.get(1);
	            rateType = (String) list.get(2);
	
	            //bargainDate = YssFun.left(String.valueOf(list.get(3)), 10); //modify huangqirong 2012-05-10 story #2565 下面报错
	            bargainDate = YssFun.formatDate((java.util.Date)list.get(3),"yyyy-MM-dd");//add huangqirong 2012-05-10 story #2565 下面报错
	            if (YssFun.isDate(bargainDate)) {
	                bargainDate = bargainDate;
	            } else {
	                if (String.valueOf(list.get(3)).length() > 10) {
	                    bargainDate = String.valueOf(list.get(3)).substring(0, 10);
	                } else {
	                    bargainDate = String.valueOf(list.get(3));
	                    bargainDate = YssFun.left(bargainDate, 4) +
	                        "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
	                        YssFun.right(bargainDate, 2);
	                }
	            }
	            BaseOperDeal operDeal = new BaseOperDeal();
	            operDeal.setYssPub(pub);
	            sResult = operDeal.getCuryRate(YssFun.toDate(bargainDate), curyCode,
	                                           portCode, rateType);
	            //     sResult = operDeal.getCuryRate(YssFun.toDate(bargainDate), curyCode,
	            //                                    portCode, rateType);
//	            return String.valueOf(sResult);
            }
            return String.valueOf(sResult);
        } catch (Exception e) {
            throw new YssException("获取汇率出错");
        }
    }

    // 20070929  chenyibo  增加
    public String getPL() throws YssException {
        //获得实现损益平准金
        ArrayList list = null;

        double dSellAmount = 0; //销售数量
        double dSellMoney = 0; //销售金额
        double dUnPl = 0; //未实现损益平准金
        double dOtherMoney = 0; //其他金额

        String dTradeDate = ""; //交易日期
        String strPortCode = ""; //组合代码
        String strPortClsCode = ""; //分级组合代码（还需传入分级组合代码fazmm20071122)
        String strSellNetCode = ""; //网点代码
        String strAnalysisCode1 = "";
        String strAnalysisCode2 = "";
        String strAnalysisCode3 = "";
        String strSellCode = ""; //销售类型 add by huangqirong 2012-07-04 bug #4870
        String strCuryCode = "";// add by huangqirong 2012-07-04 bug #4870
        
        double baseRate = 0;	//add by huangqirong 2012-04-28 story #2565
        double portRate = 0;	//add by huangqirong 2012-04-28 story #2565
        
        double yfshf = 0; // 应付赎回费
        double xsfsr = 0; // 销售费收入
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            strPortClsCode = (String) list.get(0);
            dTradeDate = String.valueOf(list.get(1)).substring(0, 10);
            strSellNetCode = (String) list.get(2);
            strAnalysisCode1 = (String) list.get(3);
            dSellMoney = Double.parseDouble(String.valueOf(list.get(4)));
            dSellAmount = Double.parseDouble(String.valueOf(list.get(5)));
            
            //modify by huangqirong 2012-04-28 story #2565 ETF联接基金添加多币种处理基准金额
            TaPortClsBean portCls = new TaPortClsBean(); //20071122   chenyibo 通过组合分级得到组合代码
            portCls.setYssPub(pub);
            portCls.setPortClsCode(strPortClsCode);
            portCls.getSetting();

            strPortCode = portCls.getPortCode();
            
            TaTradeBean ta = new TaTradeBean();
            ta.setYssPub(pub);
            if(list.size() == 10){            	
            	String curyCode = String.valueOf(list.get(6));            	
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "port");
            	/*dSellMoney = YssD.mul(dSellMoney , baseRate);
            	dSellMoney = YssD.div(dSellMoney , portRate);*/
            }
            // add by huangqirong 2012-07-04 bug #4870
            else if(list.size() == 11){
            	strCuryCode = String.valueOf(list.get(6)); 
            	strSellCode = String.valueOf(list.get(7));            	
            	//获取确认日
                Date confirmDate= ta.getConfirmDay(strSellNetCode,
                		strPortClsCode, strPortCode, strSellCode, strCuryCode,
                        YssFun.parseDate(dTradeDate));
                BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(confirmDate, strCuryCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(confirmDate, strCuryCode, strPortCode, "port");
	            ta.setDConfimDate(confirmDate);
	            //--- add by songjie 2013.06.28 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 start---//
	            ta.setStrSellTypeCode(strSellCode);//设置销售类型代码
	            //--- add by songjie 2013.06.28 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 end---//
            }
            //---end---
            else if(list.size() == 12){
            	strCuryCode = String.valueOf(list.get(6));     
            	yfshf = Double.parseDouble(String.valueOf(list.get(7)));
            	xsfsr = Double.parseDouble(String.valueOf(list.get(8)));
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), strCuryCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), strCuryCode, strPortCode, "port");            	
            }
            else if(list.size() == 13){
            	strCuryCode = String.valueOf(list.get(6)); 
            	yfshf = Double.parseDouble(String.valueOf(list.get(7)));
            	xsfsr = Double.parseDouble(String.valueOf(list.get(8)));
            	strSellCode = String.valueOf(list.get(9));
            	//获取确认日
                Date confirmDate = ta.getConfirmDay(strSellNetCode,
                		strPortClsCode, strPortCode, strSellCode, strCuryCode,
                        YssFun.parseDate(dTradeDate));
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(confirmDate, strCuryCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(confirmDate, strCuryCode, strPortCode, "port"); 
	            //--- add by songjie 2013.06.28 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 start---//
	            ta.setStrSellTypeCode(strSellCode);//设置销售类型代码
	            ta.setStrCuryCode(strCuryCode);//add by yeshenghong  4127 20130909
	            //--- add by songjie 2013.06.28 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 end---//
            }
            //--- add by songjie 2013.07.11 STORY 4166 需求北京-[嘉实基金]QDV4.0[高]20130710001 start---//
            else if(list.size() == 14){
            	strCuryCode = String.valueOf(list.get(6));     
            	yfshf = Double.parseDouble(String.valueOf(list.get(7)));
            	xsfsr = Double.parseDouble(String.valueOf(list.get(8)));
            	strSellCode = String.valueOf(list.get(9));
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), strCuryCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), strCuryCode, strPortCode, "port");      
            	ta.setStrSellTypeCode(strSellCode);//设置销售类型代码
            }
            //--- add by songjie 2013.07.11 STORY 4166 需求北京-[嘉实基金]QDV4.0[高]20130710001 end---//
            else {            
	            if (list.size() > 9) {
	                if (list.get(6) == null) {
	                    dOtherMoney = 0;
	                } else {
	                    dOtherMoney = Double.parseDouble(String.valueOf(list.get(6)));
	                }
	            }
            }
            //---end---           
            
            ta.setStrPortCode(strPortCode);
            ta.setSPortClsCode(strPortClsCode);
            ta.setDTradeDate(YssFun.toSqlDate(dTradeDate));
            ta.setStrSellNetCode(strSellNetCode);
            ta.setStrAnalysisCode1(strAnalysisCode1);
            //edit by shenjie 20120307--------------------------
            ta.setBeMarkMoney(YssD.add(dSellMoney, dOtherMoney));//从接口处设置基准金额
            ta.setDSellAmount(dSellAmount);//从接口处设置销售数量
            //--------------------------------------------------
            ta.setDBaseCuryRate(baseRate);//add by huangqirong 2012-04-28 story #2565
            ta.setDPortCuryRate(portRate);//add by huangqirong 2012-04-28 story #2565     
            ta.setYfshf(yfshf);
            ta.setXsfsr(xsfsr);
            ta.getPL();
            //返回的是未实现损益平准金 用(销售金额 减去 (库存成本/库存数量)*销售数量) * 比例
            //if (ta.getDAmount() != 0) { //chenyibo   20071002 增加对dAmount的判断
            //   dUnPl = dSellMoney -
            //         dSellAmount * (ta.getDCost() / ta.getDAmount());
            //}
            //else {
            //   dUnPl = 0;
            //}
            //dUnPl = dUnPl * ta.getDScale();
            //以上步骤计算未实现，已实现为轧差数（申购赎回费-实收基金-未实现损益平准进）
            //edit by zhouwei 20120307 
            dUnPl=ta.getDIncomeBal();
//            dUnPl = 0;
//            if (dSellMoney != 0 && dSellAmount != 0) {
//                dUnPl = YssFun.roundIt( (dSellMoney + dOtherMoney) - dSellAmount -
//                                       YssFun.roundIt( (dSellMoney + dOtherMoney) * ta.getDScale(),
//                    2), 2);
//            }
            return dUnPl + "";
        } catch (Exception e) {
            throw new YssException("获取实现损益平准金报错", e);
        }
    }
    
    /** 
     *story2683  add by zhouwei 20120611 计算实收基金金额
    * @Title: getPaidUpFundsMoney 
    * @Description: TODO
    * @param @return
    * @param @throws YssException    设定文件 
    * @return String    返回类型 
    * @throws 
    */
    public String getPaidUpFundsMoney() throws YssException {
        //计算实收基金金额
        ArrayList list = null;

        double dSellAmount = 0; //销售数量
        double dSellMoney = 0; //销售金额
        String dTradeDate = ""; //交易日期
        String strPortCode = ""; //组合代码
        String strAnalysisCode1 = "";
        String strAnalysisCode2 = "";
        String strAnalysisCode3 = "";
		//--- bug 4870 QDV4建行2012年06月25日02_B by zhouwei 20120626 start---//
        String sellNetCode="";//网点
        String portClsCode="";//组合分级
        String sellTypeCode="";
        String curyCode="";
		//--- bug 4870 QDV4建行2012年06月25日02_B by zhouwei 20120626 end---//
        try {
            list = (ArrayList) prepFun.getObj();
            dTradeDate = String.valueOf(list.get(0)).substring(0, 10);
            dSellMoney = Double.parseDouble(String.valueOf(list.get(1)));
            dSellAmount = Double.parseDouble(String.valueOf(list.get(2)));
            strPortCode= String.valueOf(list.get(3));
			//--- bug 4870 QDV4建行2012年06月25日02_B by zhouwei 20120626 start---//
            portClsCode=String.valueOf(list.get(4));
            sellNetCode=String.valueOf(list.get(5));
            sellTypeCode=String.valueOf(list.get(6));
            curyCode=String.valueOf(list.get(7));
			//--- bug 4870 QDV4建行2012年06月25日02_B by zhouwei 20120626 end---//
            TaTradeBean ta = new TaTradeBean();
            ta.setYssPub(pub);
            ta.setStrPortCode(strPortCode);
            ta.setDTradeDate(YssFun.toSqlDate(dTradeDate));
			//--- bug 4870 QDV4建行2012年06月25日02_B by zhouwei 20120626 start---//
            ta.setDConfimDate(ta.getConfirmDay(sellNetCode,
                portClsCode, strPortCode, sellTypeCode, curyCode,
                YssFun.parseDate(dTradeDate)));//确认日 bug 4870 by zhouwei 20120626
			//--- bug 4870 QDV4建行2012年06月25日02_B by zhouwei 20120626 end---//
            ta.setStrAnalysisCode1(strAnalysisCode1);
            ta.setBeMarkMoney(dSellMoney);//从接口处设置基准金额
            ta.setDSellAmount(dSellAmount);//从接口处设置销售数量
            ta.getPaidUpFundsMoney();        
            return ta.getdPaidInMoney() + "";
        } catch (Exception e) {
            throw new YssException("获取实收基金金额出错！", e);
        }
    }
    
    // 20070929  chenyibo  增加
    public String getUnPL() throws YssException {
        //获得未实现损益平准金
        ArrayList list = null;
        double dSellAmount = 0; //销售数量
        double dSellMoney = 0; //销售金额
        double dUnPl = 0; //未实现损益平准金
        double dOtherMoney = 0; //其他金额

        String dTradeDate = ""; //交易日期
        String strPortCode = ""; //组合代码
        String strPortClsCode = ""; //需增加分级组合代码fazmm20071122
        String strSellNetCode = ""; //网点代码
        String strAnalysisCode1 = "";
        String strAnalysisCode2 = "";
        String strAnalysisCode3 = "";
        String strSellCode = ""; //销售类型 add by huangqirong 2012-07-04 bug #4870
        
        double baseRate = 0;//add by huangqirong 2012-04-28 story #2565
        double portRate = 0;//add by huangqirong 2012-04-28 story #2565
        double yfshf = 0; // 应付赎回费
        double xsfsr = 0; // 销售费收入
        
        String curyCode = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            strPortClsCode = (String) list.get(0);
            dTradeDate = String.valueOf(list.get(1)).substring(0, 10);
            strSellNetCode = (String) list.get(2);
            strAnalysisCode1 = (String) list.get(3);

            dSellMoney = Double.parseDouble(String.valueOf(list.get(4)));
            
            dSellAmount = Double.parseDouble(String.valueOf(list.get(5)));
            
            //modify by huangqirong 2012-04-28 story #2565 ETF联接基金添加多币种处理基准金额
            TaPortClsBean portCls = new TaPortClsBean(); //20071122   chenyibo 通过组合分级得到组合代码
            portCls.setYssPub(pub);
            portCls.setPortClsCode(strPortClsCode);
            portCls.getSetting();

            strPortCode = portCls.getPortCode();
            
            TaTradeBean ta = new TaTradeBean();
            ta.setYssPub(pub);
            if(list.size() == 10){            	
            	curyCode = String.valueOf(list.get(6));            	
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "port");
            }
            // add by huangqirong 2012-07-04 bug #4870
            else if(list.size() == 11){
            	curyCode = String.valueOf(list.get(6)); 
            	strSellCode = String.valueOf(list.get(7));            	
            	//获取确认日            	
                Date confirmDate = ta.getConfirmDay(strSellNetCode,
                		strPortClsCode, strPortCode, strSellCode, curyCode,
                        YssFun.parseDate(dTradeDate));
                BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "port");
	            ta.setDConfimDate(confirmDate);
	            //--- add by songjie 2013.06.28 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 start---//
	            ta.setStrSellTypeCode(strSellCode);//设置销售类型代码
	            //--- add by songjie 2013.06.28 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 end---//
            }
            //---end---
            else if(list.size() == 12){
            	curyCode = String.valueOf(list.get(6));     
            	yfshf = Double.parseDouble(String.valueOf(list.get(7)));
            	xsfsr = Double.parseDouble(String.valueOf(list.get(8)));
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "port");            	
            }
            else if(list.size() == 13){
            	curyCode = String.valueOf(list.get(6)); 
            	yfshf = Double.parseDouble(String.valueOf(list.get(7)));
            	xsfsr = Double.parseDouble(String.valueOf(list.get(8)));
            	strSellCode = String.valueOf(list.get(9));
            	//获取确认日
                Date confirmDate = ta.getConfirmDay(strSellNetCode,
                		strPortClsCode, strPortCode, strSellCode, curyCode,
                        YssFun.parseDate(dTradeDate));
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "port");    
	            //--- add by songjie 2013.06.28 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 start---//
	            ta.setStrSellTypeCode(strSellCode);//设置销售类型代码
	            ta.setStrCuryCode(curyCode);//add by yeshenghong 4127 20130909
	            //--- add by songjie 2013.06.28 STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001 end---//
            }
            //--- add by songjie 2013.07.11 STORY 4166 需求北京-[嘉实基金]QDV4.0[高]20130710001 start---//
            else if(list.size() == 14){
            	curyCode = String.valueOf(list.get(6));     
            	yfshf = Double.parseDouble(String.valueOf(list.get(7)));
            	xsfsr = Double.parseDouble(String.valueOf(list.get(8)));
            	strSellCode = String.valueOf(list.get(9));
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "port");      
            	ta.setStrSellTypeCode(strSellCode);//设置销售类型代码
            }
            //--- add by songjie 2013.07.11 STORY 4166 需求北京-[嘉实基金]QDV4.0[高]20130710001 end---//
            else{            
	            //增加一个金额参数  胡坤  20080530
	            if (list.size() > 9) {
	                if (list.get(6) == null) {
	                    dOtherMoney = 0;
	                } else {
	                    dOtherMoney = Double.parseDouble(String.valueOf(list.get(6)));
	                }
	            }
            }
            //---end---
            //获得相应的比例 是从净值表里面取的数据             
            ta.setStrPortCode(strPortCode);
            ta.setSPortClsCode(strPortClsCode);
            ta.setDTradeDate(YssFun.toSqlDate(dTradeDate));
            ta.setStrSellNetCode(strSellNetCode);
            ta.setStrAnalysisCode1(strAnalysisCode1);
            //edit by shenjie 20120307--------------------------
            ta.setBeMarkMoney(YssD.add(dSellMoney, dOtherMoney));//从接口处设置基准金额
            ta.setDSellAmount(dSellAmount);//从接口处设置销售数量
            //--------------------------------------------------
            ta.setDBaseCuryRate(baseRate);//add by huangqirong 2012-04-28 story #2565
            ta.setDPortCuryRate(portRate);//add by huangqirong 2012-04-28 story #2565
            ta.setYfshf(yfshf);
            ta.setXsfsr(xsfsr);
            //---end---
            ta.getPL();

//            //返回的是未实现损益平准金 用(销售金额 减去 (库存成本/库存数量)*销售数量) * 比例
//            //如果要得到损益平准金的话就直接拿 dUnPl - dUnPl*dScale
//            if (dSellMoney != 0 && dSellAmount != 0) { //chenyibo   20071002 增加对dAmount的判断
//                dUnPl = YssD.add(dSellMoney, dOtherMoney);
//            } else {
//                dUnPl = 0;
//            }
//            return YssFun.roundIt(dUnPl * ta.getDScale(), 2) + "";
            return ta.getDIncomeNotBal()+"";//edit by zhouwei 20120307
        } catch (Exception e) {
            throw new YssException("获取未实现损益平准金报错", e);
        }
    }

    /*
     * story 2727 add by zhouwei 20120618
     * 根据确认日获取未实现损益平准金
     * */
    public String getUnPLByConfirmDate() throws YssException {
        //获得未实现损益平准金
        ArrayList list = null;
        double dSellAmount = 0; //销售数量
        double dSellMoney = 0; //销售金额
        double dUnPl = 0; //未实现损益平准金
        double dOtherMoney = 0; //其他金额

        String dTradeDate = ""; //交易日期
        String strPortCode = ""; //组合代码
        String strPortClsCode = ""; //需增加分级组合代码
        String strSellNetCode = ""; //网点代码
        String strAnalysisCode1 = "";
        String strAnalysisCode2 = "";
        String strAnalysisCode3 = "";
        
        double baseRate = 0;
        double portRate = 0;
        double yfshf = 0; // 应付赎回费
        double xsfsr = 0; // 销售费收入
        String sellTypeCode = "";//销售类型
        String curyCode = "";
        try {
            list = (ArrayList) prepFun.getObj(); 
            strPortClsCode = (String) list.get(0);
            dTradeDate = String.valueOf(list.get(1)).substring(0, 10);
            strSellNetCode = (String) list.get(2);
            strAnalysisCode1 = (String) list.get(3);

            dSellMoney = Double.parseDouble(String.valueOf(list.get(4)));
            
            dSellAmount = Double.parseDouble(String.valueOf(list.get(5)));      	
        	curyCode = String.valueOf(list.get(6));    
            sellTypeCode=(String) list.get(7);
            TaPortClsBean portCls = new TaPortClsBean(); //通过组合分级得到组合代码
            portCls.setYssPub(pub);
            portCls.setPortClsCode(strPortClsCode);
            portCls.getSetting();
            strPortCode = portCls.getPortCode();  
            
            //获得相应的比例 是从净值表里面取的数据
            TaTradeBean ta = new TaTradeBean();
            ta.setYssPub(pub);
            //获取确认日
            Date confirmDate= ta.getConfirmDay(strSellNetCode,
            		strPortClsCode, strPortCode, sellTypeCode, curyCode,
                    YssFun.parseDate(dTradeDate));
            //获取汇率
            BaseOperDeal operDeal = new BaseOperDeal();
        	operDeal.setYssPub(pub);
        	baseRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "base");
        	portRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "port");
        	
            ta.setStrPortCode(strPortCode);
            ta.setSPortClsCode(strPortClsCode);
            ta.setDTradeDate(YssFun.toSqlDate(dTradeDate));
            ta.setStrSellNetCode(strSellNetCode);
            ta.setStrAnalysisCode1(strAnalysisCode1);
            ta.setBeMarkMoney(dSellMoney);//从接口处设置基准金额
            ta.setDSellAmount(dSellAmount);//从接口处设置销售数量
            //--------------------------------------------------
            ta.setDBaseCuryRate(baseRate);
            ta.setDPortCuryRate(portRate);
            ta.setYfshf(yfshf);
            ta.setXsfsr(xsfsr);
            ta.getPL();

            return ta.getDIncomeNotBal()+"";
        } catch (Exception e) {
            throw new YssException("根据确认日获取未实现损益平准金报错", e);
        }
    }
    /*
     * story 2727 add by zhouwei 20120618
     * 根据确认日获取已实现损益平准金
     * */
    public String getPLByConfirmDate() throws YssException {
        //获得实现损益平准金
        ArrayList list = null;
        double dSellAmount = 0; //销售数量
        double dSellMoney = 0; //销售金额
        double dUnPl = 0; //未实现损益平准金
        double dOtherMoney = 0; //其他金额

        String dTradeDate = ""; //交易日期
        String strPortCode = ""; //组合代码
        String strPortClsCode = ""; //需增加分级组合代码
        String strSellNetCode = ""; //网点代码
        String strAnalysisCode1 = "";
        String strAnalysisCode2 = "";
        String strAnalysisCode3 = "";
        
        double baseRate = 0;
        double portRate = 0;
        double yfshf = 0; // 应付赎回费
        double xsfsr = 0; // 销售费收入
        String sellTypeCode = "";//销售类型
        String curyCode = "";
        try {
            list = (ArrayList) prepFun.getObj(); 
            strPortClsCode = (String) list.get(0);
            dTradeDate = String.valueOf(list.get(1)).substring(0, 10);
            strSellNetCode = (String) list.get(2);
            strAnalysisCode1 = (String) list.get(3);

            dSellMoney = Double.parseDouble(String.valueOf(list.get(4)));
            
            dSellAmount = Double.parseDouble(String.valueOf(list.get(5)));      	
        	curyCode = String.valueOf(list.get(6));    
            sellTypeCode=(String) list.get(7);
            TaPortClsBean portCls = new TaPortClsBean(); //通过组合分级得到组合代码
            portCls.setYssPub(pub);
            portCls.setPortClsCode(strPortClsCode);
            portCls.getSetting();
            strPortCode = portCls.getPortCode();  
            
            //获得相应的比例 是从净值表里面取的数据
            TaTradeBean ta = new TaTradeBean();
            ta.setYssPub(pub);
            //获取确认日
            Date confirmDate= ta.getConfirmDay(strSellNetCode,
            		strPortClsCode, strPortCode, sellTypeCode, curyCode,
                    YssFun.parseDate(dTradeDate));
            //获取汇率
            BaseOperDeal operDeal = new BaseOperDeal();
        	operDeal.setYssPub(pub);
        	baseRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "base");
        	portRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "port");
        	
            ta.setStrPortCode(strPortCode);
            ta.setSPortClsCode(strPortClsCode);
            ta.setDTradeDate(YssFun.toSqlDate(dTradeDate));
            ta.setStrSellNetCode(strSellNetCode);
            ta.setStrAnalysisCode1(strAnalysisCode1);
            ta.setBeMarkMoney(dSellMoney);//从接口处设置基准金额
            ta.setDSellAmount(dSellAmount);//从接口处设置销售数量
            //--------------------------------------------------
            ta.setDBaseCuryRate(baseRate);
            ta.setDPortCuryRate(portRate);
            ta.setYfshf(yfshf);
            ta.setXsfsr(xsfsr);
            ta.getPL();

            return ta.getDIncomeBal()+"";
        } catch (Exception e) {
            throw new YssException("根据确认日获取已实现损益平准金报错", e);
        }
    }
    
    /**
     * add by songjie 2013.06.27 
     * STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001
     * 计算 TA实收基金本位币金额
     * @return
     * @throws YssException
     */
    public String getTAPaidInMoney() throws YssException{
    	double sResult = 0.0;
        ArrayList list = new ArrayList();
        String bargainDate = "";//成交日期
        String portClsCode = "";//组合分级代码
        String portCode="";//组合代码
        String portClsRank = "";//组合分级级别
        double sellAmount = 0;//销售数量
    	try{
            list = (ArrayList) prepFun.getObj(); 
            portClsCode = (String) list.get(0);//组合分级代码
            sellAmount = Double.parseDouble(String.valueOf(list.get(1)));//销售数量   
            bargainDate = String.valueOf(list.get(2)).substring(0, 10);//成交日期
            
            TaPortClsBean portCls = new TaPortClsBean(); //通过组合分级得到组合代码
            portCls.setYssPub(pub);
            portCls.setPortClsCode(portClsCode);
            portCls.getSetting();
            portClsRank = portCls.getPortClsRank();//组合分级级别
            portCode = portCls.getPortCode(); //组合代码
            
            //计算 TA实收基金本位币金额
            sResult = calTaPaidInMoney(YssFun.parseDate(bargainDate), 
    				portClsCode, sellAmount, portCode, portClsRank);
            
    		return String.valueOf(sResult);
    	}catch(Exception e){
    		throw new YssException("计算 TA实收基金本位币金额出错",e);
    	}
    }
    
    /**
     * 计算 TA实收基金本位币金额
     * 
     * add by songjie 2013.06.27 
     * STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001
     * 
     * @param tradeDate  TA成交日期
     * @param portClsCode 组合分级代码
     * @param baseRate 基础汇率
     * @param portRate 组合汇率
     * @param sellAmount 销售数量
     * @return
     * @throws YssException
     */
    public double calTaPaidInMoney(java.util.Date tradeDate, String portClsCode, 
    		 double sellAmount,String portCode, String portClsRank) throws YssException{
    	double result = 0;
    	double storageAmount = 0;//库存数量
    	double paidInCapital = 0;//成交日该组合分级代码对应的实收资本
    	try{
    		//获取组合分级代码对应的TA库存数量
    		storageAmount = getTAStorageAmount(tradeDate, portCode, portClsCode);
    		
    		//获取成交日该组合分级代码对应的实收资本
    		paidInCapital = getPaidInCapital(portCode, tradeDate, portClsRank);
    		
    		//实收基金金额 = round（（赎回数/成交日库存数 ）* 成交日该分级实收资本，2）
    		result = YssD.round(YssD.mul(YssD.div(sellAmount, storageAmount), paidInCapital), 2);
    		
    		return result;
    	}catch(Exception e){
    		throw new YssException("计算 TA实收基金本位币金额出错",e);
    	}
    }
    
    /**
     * add by songjie 2013.06.27 
     * STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001
     * 
     * 获取组合分级代码对应的TA库存数量
     * 
     * @param tradeDate TA成交日期
     * @param portCode 组合代码
     * @param portClsCode 组合分级代码
     * 
     * @return TA库存数量
     * @throws YssException
     */
	private double getTAStorageAmount(java.util.Date tradeDate, String portCode, String portClsCode) 
	throws YssException {
		double storageAmount= 0;//TA库存数量
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		try{
    		//获取 TA成交日期 TA分级代码 对应的 TA库存数量
    		sb.append(" select FStorageAmount from ").append(pub.yssGetTableName("Tb_Stock_TA"))
    		  .append(" where FStorageDate = ").append(dbl.sqlDate(tradeDate))
    		  .append(" and FPortCode = ").append(dbl.sqlString(portCode))
    		  .append(" and FPortClsCode = ").append(dbl.sqlString(portClsCode))
    		  .append(" and FCheckState = 1 ");
    				
    		rs = dbl.openResultSet(sb.toString());
    		if(rs.next()){
    			storageAmount = rs.getDouble("FStorageAmount");
    		}
    		
    		sb.setLength(0);
			
			return storageAmount;
		} catch (Exception e) {
			throw new YssException("获取 TA 库存数量出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
    
	/**
	 * add by songjie 2013.06.27 
     * STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001
     * 
     * 获取组合对应的资产代码
     * 
	 * @param portCode 组合代码 
	 * @return 资产代码
	 * @throws YssException
	 */
	private String getAssetCodeOfPort(String portCode) throws YssException{
		ResultSet rs = null;
		String assetCode = "";//资产代码
		StringBuffer sb = new StringBuffer();
		try{
    		sb.append(" select FAssetCode from ")
  		  	  .append(pub.yssGetTableName("Tb_para_Portfolio"))
  		  	  .append(" where FPortCode = ").append(dbl.sqlString(portCode))
  		  	  .append(" and FCheckState = 1 ");
  		
    		rs = dbl.openResultSet(sb.toString());
    		if(rs.next()){
    			assetCode = rs.getString("FAssetCode");//资产代码
    		}
    		
    		sb.setLength(0);
    		
    		return assetCode;
		}catch(Exception e){
    		throw new YssException("获取组合对应的资产代码出错", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
	}
	
	/**
     * add by songjie 2013.06.27 
     * STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001
     * 
     * 获取组合代码、TA成交日期 对应的套帐代码
     * 
	 * @param assetCode 资产代码
	 * @param tradeDate TA成交日期
	 * 
	 * @return 套帐代码
	 * @throws YssException
	 */
	private int getSetCodeOfPort(String assetCode, java.util.Date tradeDate) throws YssException{
		ResultSet rs = null;
		int setCode = 0;//套帐代码
		StringBuffer sb = new StringBuffer();
		try{
    	    sb.append(" select FSetCode from lsetList where FSetId = ")
  	      	.append(dbl.sqlString(assetCode))
  	      	.append(" and FYear = to_number(to_char(")
  	      	.append(dbl.sqlDate(tradeDate)).append(", 'YYYY'))");
    	    rs = dbl.openResultSet(sb.toString());
    	    if(rs.next()){
    	    	setCode = rs.getInt("FSetCode");//套账号
    	    }
  	   
    	    sb.setLength(0);
    	    
    	    return setCode;
		}catch(Exception e){
    		throw new YssException("获取组合代码 对应的套帐代码出错", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
	}
	
    /**
     * add by songjie 2013.06.27 
     * STORY #4112 需求北京-[嘉实基金]QDV4.0[高]20130624001
     * 
     * 计算成交日分级实收资本
     * @return
     * @throws YssException
     */
    private double getPaidInCapital(String portCode,java.util.Date tradeDate,String portClsRank) throws YssException{
    	double paidInCapital = 0;
    	ResultSet rs = null;
    	String strSql = "";
    	String assetCode = "";//资产代码
    	int setCode = 0;//套帐代码
    	StringBuffer sb = new StringBuffer();
    	try{
    		assetCode = getAssetCodeOfPort(portCode);//获取组合对应的资产代码
    	    
    	    setCode = getSetCodeOfPort(assetCode, tradeDate);//获取组合代码、TA成交日期 对应的套帐代码
    	    
    	    //获取相关分级、成交日期对应的实收资本本位币金额
    	    sb.append(" select fstandardmoneymarketvalue from ")//modified by yeshenghong story4127  20130909
    	      .append(pub.yssGetTableName("Tb_Rep_Guessvalue"))
    	      .append(" where FacctAttr = '实收资本' and FDate = ")
    	      .append(dbl.sqlDate(tradeDate))
    	      .append(" and FPortCode = ").append(setCode)
    	      .append(" and FAcctClass = ").append(dbl.sqlString("class\t" + portClsRank))
    	      .append(" and FAcctDetail = 1 ");
    	    rs = dbl.openResultSet(sb.toString());
    	    if(rs.next()){
    	    	paidInCapital = rs.getDouble("fstandardmoneymarketvalue");//modified by yeshenghong story4127  20130909
    	    }
    	    
    	    sb.setLength(0);
    	    
    		return paidInCapital;
    	}catch(Exception e){
    		throw new YssException("计算成交日分级实收资本出错", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /**
     *  story 2727 add by zhouwei 20120618
    *根据TA确认日获取汇率
    * @return String
    * @throws YssException
    */
   public String getTAExRateByConfirmDate() throws YssException {
       ArrayList list = new ArrayList();
       String bargainDate = "";
       String curyCode = "";
       String portClsCode = "";//组合分级
       String rateType = "";
       double sResult = 0.0;
       String portCode="";
       String strSellNetCode = ""; //网点代码
       String sellTypeCode = "";//销售类型
       try {
           list = (ArrayList) prepFun.getObj(); 
           curyCode = (String) list.get(0);
           portClsCode = (String) list.get(1);
           rateType = (String) list.get(2);     
           bargainDate = String.valueOf(list.get(3)).substring(0, 10);  
           strSellNetCode = (String) list.get(4);
           sellTypeCode=(String) list.get(5);
           TaPortClsBean portCls = new TaPortClsBean(); //通过组合分级得到组合代码
           portCls.setYssPub(pub);
           portCls.setPortClsCode(portClsCode);
           portCls.getSetting();
           portCode = portCls.getPortCode();  
           TaTradeBean ta = new TaTradeBean();
           ta.setYssPub(pub);
           //获取确认日期
           Date confirmDate= ta.getConfirmDay(strSellNetCode,
        		   portClsCode, portCode, sellTypeCode, curyCode,
                   YssFun.parseDate(bargainDate));
           BaseOperDeal operDeal = new BaseOperDeal();
           operDeal.setYssPub(pub);
           sResult = operDeal.getCuryRate(confirmDate, curyCode,
                                          portCode, rateType);
           return String.valueOf(sResult);
       } catch (Exception e) {
           throw new YssException("根据TA确认日获取汇率出错");
       }
   }
   
    //用于处理彭勃的交易所代码 导出彭勃文件时用  add liyu 1010
    private String getExchangeCode() throws YssException {
        String sResult = "";
        String sTmp = "";
        try {
//         sTmp = (String) obj;
            sTmp = (String) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            if (sTmp.trim().length() > 0 && sTmp.indexOf(" ") > 0) {
                sResult = sTmp.substring(sTmp.indexOf(" ") + 1, sTmp.length());
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取交易所代码出错", e);
        }
    }

    private String getPrices() throws YssException {
        //此方法将 CNY123.34 处理成 123.34  add liyu 1021
        ArrayList list = null;
        String sPrice = "";
        StringBuffer buf = new StringBuffer();
        char[] cPrice = null;
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sPrice = (String) list.get(0);
            if (sPrice.trim().length() > 0) {
                cPrice = sPrice.toCharArray();
                for (int i = 0; i < cPrice.length; i++) {
                    if (cPrice[i] <= '9' && cPrice[i] >= '0' || cPrice[i] == '.') {
                        buf.append(cPrice[i]);
                    }
                }
            } else {
                buf.append("0.0");
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("处理SWIFt报文价格出错", e);
        }
    }

    public String getDateConvert(String srcDate) throws YssException {
        String sResult = "";
        try {
            if (String.valueOf(srcDate).length() > 10) {
                sResult = String.valueOf(srcDate).substring(0, 10);
            } else {
                sResult = String.valueOf(srcDate);
                sResult = YssFun.left(sResult, 4) +
                    "-" + YssFun.mid(sResult, 4, 2) + "-" +
                    YssFun.right(sResult, 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("转换日期出错", e);
        }
    }

    public void updateTrade() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务

        ArrayList list = new ArrayList();
//      list = (ArrayList) obj;
        list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
        Iterator it = list.iterator();
        try {
            while (it.hasNext()) {
                String str = (String) it.next();
                String[] str1 = str.split(",");

                String nums = str1[0];
                String settleDate = str1[1];
                String settleMoeny = str1[2];
                if (!settleDate.equalsIgnoreCase(" ")) {
                    conn.setAutoCommit(false);
                    bTrans = true;

                    strSql = " update " + pub.yssGetTableName("tb_data_subtrade") +
                        " set FFactSettleDate=" + dbl.sqlDate(settleDate) +
                        " , FFactSettleMoney=" + settleMoeny +
                        " where Fnum =" + dbl.sqlString(nums);
                    dbl.executeSql(strSql);
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);
                }
            }

        } catch (Exception e) {
            throw new YssException("更新交易子表出错!", e);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
    }

    public void operTradeSettle() throws YssException {
        String nums = "";
        ArrayList list = null;
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            for (int i = 0; i < list.size(); i++) { //因为 list 里有多条 by ly 080317
                nums = (String) list.get(i);
                BaseTradeSettlement tradeset = new BaseTradeSettlement();
                tradeset.setNums(nums);
                tradeset.setYssPub(pub);
                tradeset.createCashTransfer();
            }
        } catch (Exception e) {
            throw new YssException("交易结算出错!", e);
        }
    }

    /***
     *外汇交易 获取基础货币金额
     * 参数 交易日期,卖出现金帐户,组合,三个分析代码(交易地点,投资经理,券商代码,证券品种),卖出金额
     */

    public String getBaseMoney() throws YssException {
        ArrayList list = null;
        RateTradeBean rateTrade = null;
        double dScale = 0;
        com.yss.main.storagemanage.CashStorageBean cashStg = null;
        double accBaseMoney = 0;
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            rateTrade = new RateTradeBean();
            rateTrade.setYssPub(pub);
            rateTrade.setTradeDate(YssFun.toDate(String.valueOf(list.get(0)).substring(0, 10))); //交易日期
            rateTrade.setSCashAccCode( (String) list.get(1)); //卖出现金帐户
            rateTrade.setPortCode( (String) list.get(2)); //组合
            rateTrade.setAnalysisCode1( (String) list.get(3)); //代码1
            rateTrade.setAnalysisCode2( (String) list.get(4)); //代码2
            rateTrade.setAnalysisCode3( (String) list.get(5)); //代码3
            rateTrade.setSMoney(YssFun.toDouble(String.valueOf(list.get(6)))); //卖出金额
            cashStg = operFun.getCashAccStg(rateTrade.getTradeDate(),
                                            rateTrade.getSCashAccCode(),
                                            rateTrade.getPortCode(),
                                            rateTrade.getAnalysisCode1(),
                                            rateTrade.getAnalysisCode2(),
                                            rateTrade.getAnalysisCode3());
            if (cashStg == null) {
                return "0";
            }
            if (YssFun.toDouble(cashStg.getStrAccBalance()) != 0) {
                dScale = YssD.div(rateTrade.getSMoney(),
                                  YssFun.toDouble(cashStg.getStrAccBalance()));
            }
            accBaseMoney = YssD.round(YssD.mul(YssFun.toDouble(cashStg.
                getStrBaseCuryBal()), dScale), 2);
        } catch (Exception e) {
            throw new YssException("获取基础货币金额出错!", e);
        }
        return accBaseMoney + "";
    }

    /***
     * 外汇交易时 获取组合货币金额
     *  参数:交易日期,卖出现金帐户,组合,分析代码(交易地点,投资经理,券商代码,证券品种),卖出金额
     */

    public String getPortMoney() throws YssException {
        double dScale = 0;
        double accPortMoney = 0;
        ArrayList list = null;
        com.yss.main.storagemanage.CashStorageBean cashStg = null;
        RateTradeBean rate = null;
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            rate = new RateTradeBean();
            rate.setYssPub(pub);
            rate.setTradeDate(YssFun.toDate(String.valueOf(list.get(0)).substring(0, 10))); //交易日期
            rate.setSCashAccCode( (String) list.get(1)); //卖出现金帐户
            rate.setPortCode( (String) list.get(2)); //组合
            rate.setAnalysisCode1( (String) list.get(3)); //代码1
            rate.setAnalysisCode2( (String) list.get(4)); //代码2
            rate.setAnalysisCode3( (String) list.get(5)); //代码3
            rate.setSMoney(YssFun.toDouble(String.valueOf(list.get(6)))); //卖出金额
            cashStg = operFun.getCashAccStg(rate.getTradeDate(),
                                            rate.getSCashAccCode(),
                                            rate.getPortCode(),
                                            rate.getAnalysisCode1(),
                                            rate.getAnalysisCode2(),
                                            rate.getAnalysisCode3());
            if (cashStg == null) {
                return "0";
            }
            if (YssFun.toDouble(cashStg.getStrAccBalance()) != 0) {
                dScale = YssD.div(rate.getSMoney(),
                                  YssFun.toDouble(cashStg.getStrAccBalance()));
            }
            accPortMoney = YssD.round(YssD.mul(YssFun.toDouble(cashStg.
                getStrPortCuryBal()), dScale), 2);

            return accPortMoney + "";
        } catch (Exception e) {
            throw new YssException("计算外汇交易时的组合货币金额出错", e);
        }

    }

    /***
     * 获取外汇交易时 汇兑损益
     * 参数: 组合代码,买入货币代码,卖出货币代码,买入金额,卖出金额,交易日期,兑换汇率,卖出现金帐户,三个分析代码(交易地点,投资经理,券商代码,证券品种)
     */

    public String getRateFX() throws YssException {
        double dScale = 0;
        double accPortMoney = 0;
        double dhdsy = 0;
        double dBBaseRate = 1; //买入货币基础汇率
        double dSBaseRate = 1; //卖出汇率基础汇率
        double dPortRate = 1; //组合汇率
        double dBSetMoney = 0; //买入本位币金额
        double dSSetMoney = 0; //卖出本位币金额
        com.yss.main.storagemanage.CashStorageBean cashStg = null;
        RateTradeBean rate = null;
        ArrayList list = null;
        try {
            rate = new RateTradeBean();
            rate.setYssPub(pub);
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            rate.setPortCode( (String) list.get(0));
            rate.setBCuryCode( (String) list.get(1));
            rate.setSCuryCode( (String) list.get(2));
            rate.setBMoney(YssFun.toDouble(String.valueOf(list.get(3))));
            rate.setSMoney(YssFun.toDouble(String.valueOf(list.get(4))));
            rate.setTradeDate(YssFun.toDate(String.valueOf(list.get(5)).substring(0, 10)));
            rate.setExCuryRate(YssFun.toDouble(String.valueOf(list.get(6))));
            rate.setSCashAccCode( (String) list.get(7));
            rate.setAnalysisCode1( (String) list.get(8)); //代码1
            rate.setAnalysisCode2( (String) list.get(9)); //代码2
            rate.setAnalysisCode3( (String) list.get(10)); //代码3
            cashStg = operFun.getCashAccStg(rate.getTradeDate(),
                                            rate.getSCashAccCode(),
                                            rate.getPortCode(),
                                            rate.getAnalysisCode1(),
                                            rate.getAnalysisCode1(),
                                            rate.getAnalysisCode1());
            if (cashStg != null) {
                if (YssFun.toDouble(cashStg.getStrAccBalance()) != 0) {
                    dScale = YssD.div(rate.getSMoney(),
                                      YssFun.toDouble(cashStg.getStrAccBalance()));
                }
                accPortMoney = YssD.round(YssD.mul(YssFun.toDouble(cashStg.
                    getStrPortCuryBal()), dScale), 2);

            }
            PortfolioBean port = new PortfolioBean();
            port.setYssPub(pub);
            port.setPortCode(rate.getPortCode());
            port.getSetting();
            if (rate.getBCuryCode().equalsIgnoreCase(port.getCurrencyCode()) &&
                !rate.getSCuryCode().equalsIgnoreCase(port.getCurrencyCode())) { //当买入货币是组合货币时计算汇兑损益
                dhdsy = YssD.round(YssD.sub(rate.getBMoney(), accPortMoney), 2); //汇兑损益的计算方法是 流入货币－计算出的组合货币成本
            } else if (rate.getSCuryCode().equalsIgnoreCase(port.getCurrencyCode()) &&
                       !rate.getBCuryCode().equalsIgnoreCase(port.getCurrencyCode())) { //当卖出货币是组合货币时计算汇兑损益
                dBBaseRate = this.getSettingOper().getCuryRate(rate.getTradeDate(),
                    rate.getBCuryCode(),
                    rate.getPortCode(), YssOperCons.YSS_RATE_BASE);
                dhdsy = YssD.sub(YssD.round(YssD.mul(rate.getBMoney(),
                    rate.getExCuryRate()), 2),
                                 YssD.round(YssD.mul(rate.getBMoney(), dBBaseRate),
                                            2)); //汇兑损益的计算方法是 买入货币金额*交易汇率－买入货币金额*中间价汇率(汇率数据表中的汇率)
            } else if (!rate.getSCuryCode().equalsIgnoreCase(port.getCurrencyCode()) &&
                       !rate.getBCuryCode().equalsIgnoreCase(port.getCurrencyCode())) { //当卖出货币和买入货币都不是组合货币时计算汇兑损益 胡昆 20070925
                dBBaseRate = this.getSettingOper().getCuryRate(rate.getTradeDate(),
                    rate.getBCuryCode(),
                    rate.getPortCode(), YssOperCons.YSS_RATE_BASE);
                dSBaseRate = this.getSettingOper().getCuryRate(rate.getTradeDate(),
                    rate.getSCuryCode(),
                    rate.getPortCode(), YssOperCons.YSS_RATE_BASE);
                dPortRate = this.getSettingOper().getCuryRate(rate.getTradeDate(),
                    port.getCurrencyCode(),
                    rate.getPortCode(), YssOperCons.YSS_RATE_PORT);
                dBSetMoney = this.getSettingOper().calPortMoney(rate.getBMoney(),
                    dBBaseRate, dPortRate, //计算买入货币本位币金额
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和汇率
                    rate.getBCuryCode(), rate.getTradeDate(), rate.getPortCode());
                dSSetMoney = this.getSettingOper().calPortMoney(rate.getSMoney(),
                    dSBaseRate, dPortRate, //计算卖出货币本位币金额
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和汇率
                    rate.getSCuryCode(), rate.getTradeDate(), rate.getPortCode());
                dhdsy = YssD.sub(dSSetMoney, dBSetMoney);
            }

            return dhdsy + "";
        } catch (Exception e) {
            throw new YssException("计算外汇交易时的汇兑损益出错", e);
        }

    }

    /***
     * 计算外汇交易的编号
     * 参数: 结算日期
     */

    public String getRateNum() throws YssException {
        String sNum = "";
        String strNumberDate = "";
        String settleDate = "";
        ArrayList list = null;
        try {
//         list=(ArrayList)obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            settleDate = String.valueOf(list.get(0)).substring(0, 10);
            strNumberDate = YssFun.formatDate(YssFun.formatDate(settleDate),
                                              YssCons.YSS_DATETIMEFORMAT).
                substring(0, 8);

            sNum = "T" + strNumberDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_RateTrade"),
                                       dbl.sqlRight("FNum", 6), "000001",
                                       " where FNum like 'T"
                                       + strNumberDate + "%'", 1);
            return sNum;
        } catch (Exception e) {
            throw new YssException("计算外汇交易表的编号出错", e);
        }
    }

    public void insertCashCommand() throws YssException {
        String sNum = "";
        String sqlStr = "";
        String sAry[] = null;
        ArrayList list = null;
        ResultSet rs = null;
        CommandBean comm = null;
        double exCuryRate = 0.0;
        RateTradeBean rateTrade = new RateTradeBean();
        rateTrade.setYssPub(pub);
        ReceiverBean receiverBean = null; //ly 修改,原因是划款指令中的代码已更改
        Connection conn = dbl.loadConnection(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
        try {
//        list=(ArrayList)obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            for (int i = 0; i < list.size(); i++) {
                sNum += (String) list.get(i) + ",";
            }
            sqlStr = "select * from " + pub.yssGetTableName("Tb_data_RateTrade") +
                " where FNum in( " + operSql.sqlCodes(sNum) + " )";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                comm = new CommandBean();
                comm.setYssPub(pub);
                comm.setSRelaNum(rs.getString("FNum"));
                comm.setSRelaType("RateTrade");
                comm.setAccountDate(YssFun.formatDate(rs.getDate("FSettleDate"), "yyyy-MM-dd"));
                comm.setAccountTime("00:00:00");
                comm.setOrder("0");
                comm.setSCommandDate(YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd"));
                comm.setSCommandTime(YssFun.formatDate(new java.util.Date(), "HH:mm:ss"));
                receiverBean = new ReceiverBean();
                receiverBean.setYssPub(pub);
                receiverBean.setOldReceiverCode(rs.getString("FPAYCODE"));
                receiverBean.getSetting();
                comm.setPayName(receiverBean.getReceiverName());
                comm.setPayCuryCode(rs.getString("FSCURYCODE"));
                if (rs.getDouble("FExCuryRate") == 0) {
                    exCuryRate = 1.0;
                } else {
                    exCuryRate = rs.getDouble("FExCuryRate");
                }
                comm.setReMoney(YssD.mul(rs.getDouble("FBMoney"), exCuryRate)); //卖出金额=买入金额*兑换汇率
                comm.setDRate(exCuryRate);
                receiverBean = new ReceiverBean();
                receiverBean.setYssPub(pub);
                receiverBean.setOldReceiverCode(rs.getString("FReceiverCode"));
                receiverBean.getSetting();
                comm.setReceiverName(receiverBean.getReceiverName());
                comm.setReCuryCode(rs.getString("FBCuryCode"));
                comm.setPayMoney(rs.getDouble("FBMoney"));

                rateTrade.addCashCommon(comm); //产生划款指令
            }
            //// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            if (prepFun.getCheck().equalsIgnoreCase("true")) { //若前台为审核时
                conn.setAutoCommit(false);
                sqlStr = "update " + pub.yssGetTableName("tb_cash_command") + " set FCheckState=1  where FNumType='" + comm.getSRelaType() + "' and FRelaNum in(" + operSql.sqlCodes(sNum) + ")";
                dbl.executeSql(sqlStr);
                conn.commit();
                conn.setAutoCommit(true);
            }
            // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, false); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
        }
    }

    /***
     * 导入外汇交易来产生资金调拨
     * 参数:sFNum集(外汇交易表的编号集)
     */

    public String insertCashTransfer() throws YssException {
        String sNum = "";
        String sqlStr = "";
        String sAry[] = null;
        boolean analy1 = false, analy2 = false, analy3 = false;
        ArrayList list = null;
        ResultSet rs = null;
        com.yss.main.operdata.RateTradeBean rate = null;
        com.yss.main.operdata.RateTradeBean filterType = null;
        try {
//         list=(ArrayList)obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            for (int i = 0; i < list.size(); i++) {
                sNum += (String) list.get(i) + ",";
            }
            rate = new RateTradeBean();
            rate.setYssPub(pub);
            filterType = new RateTradeBean();
            filterType.setYssPub(pub);
            //filterType.setSettleDate(YssFun.toDate(String.valueOf(list.get(0)).substring(0,10)));
            //rate.setFilterType(filterType);
            sqlStr = "select * from " + pub.yssGetTableName("Tb_data_RateTrade") +
                " where FNum in( " + operSql.sqlCodes(sNum) + " )";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                analy1 = operSql.storageAnalysis("FAnalysisCode1",
                                                 YssOperCons.YSS_KCLX_Cash); //判断分析代码存不存在
                analy2 = operSql.storageAnalysis("FAnalysisCode2",
                                                 YssOperCons.YSS_KCLX_Cash);
                analy3 = operSql.storageAnalysis("FAnalysisCode3",
                                                 YssOperCons.YSS_KCLX_Cash);
                rate.setNum(rs.getString("FNum"));
                rate.setTradeDate(rs.getDate("FtradeDate"));
                rate.setTradeTime(rs.getString("FtradeTime"));
                rate.setPortCode(rs.getString("FportCode"));
                if (analy1) {
                    rate.setAnalysisCode1(rs.getString("FanalysisCode1") + "");
                    rate.setBAnalysisCode1(rs.getString("FBanalysisCode1") + "");
                }
                if (analy2) {
                    rate.setAnalysisCode2(rs.getString("FanalysisCode2") + "");

                    rate.setBAnalysisCode2(rs.getString("FBanalysisCode2") + "");
                }
                if (analy3) {
                    rate.setAnalysisCode3(rs.getString("FanalysisCode3") + "");
                    rate.setBAnalysisCode3(rs.getString("FBanalysisCode3") + "");
                }
                rate.setBPortCode(rs.getString("FBPortCode"));
                //  rate.setBPortName(rs.getString("FBPortName"));
                rate.setBCashAccCode(rs.getString("FbCashAccCode"));
                //this.bCashAccName = rs.getString("FbCashAccName");
                rate.setSCashAccCode(rs.getString("FsCashAccCode"));
                // this.sCashAccName = rs.getString("FsCashAccName");
                rate.setSettleTime(rs.getString("FsettleTime"));
                rate.setSettleDate(rs.getDate("FsettleDate"));
                rate.setBSettleTime(rs.getString("FBSettleTime")); //ALTER BY SUNNY
                rate.setBSettleDate(rs.getDate("FBSettleDate"));
                rate.setTradeType(rs.getString("FtradeType"));
                //this.tradeTypeName = rs.getString("FtradeTypeName");
                rate.setCatType(rs.getString("FcatType"));
                //this.catTypeName = rs.getString("FcatTypeName");
                rate.setExCuryRate(rs.getDouble("FexCuryRate"));
                rate.setLingCuryRate(rs.getDouble("FlongCuryRate"));
                rate.setBMoney(rs.getDouble("FbMoney"));
                rate.setSMoney(rs.getDouble("FsMoney"));
                rate.setBaseMoney(rs.getDouble("FbaseMoney"));
                rate.setPortMoney(rs.getDouble("FportMoney"));
                rate.setRateFx(rs.getDouble("FrateFx"));
                rate.setUpDown(rs.getDouble("FupDown"));
                rate.setDesc(rs.getString("FDesc"));
                rate.setBCuryCode(rs.getString("FBCuryCode"));
                // this.bCuryName = rs.getString("FBCuryName");
                rate.setSCuryCode(rs.getString("FSCuryCode"));
                //this.sCuryName = rs.getString("FSCuryName");
                rate.setBCuryFee(rs.getDouble("FBCuryFee"));
                rate.setSCuryFee(rs.getDouble("FSCuryFee"));
                //// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
//            rate.createSavCashTrans(rate.getNum(), rate.getNum());
                rate.createSavCashTrans(rate.getNum(), rate.getNum(), (prepFun.getCheck().equalsIgnoreCase("true") ? 1 : 0)); //这里从前台传审核状态到这里
                //// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            }
            rs.close();
            return "";
        } catch (Exception e) {
            throw new YssException("产生资金调拨出错", e);
        } finally {
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        	dbl.closeResultSetFinal(rs);
        	//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
    }

    /**
     * 按节假日群获取工作日
     * 参数:参照日期,节假日群代码,相差的天数
     * @return String
     * @throws YssException
     */
    public String getWorkDay() throws YssException {
        java.util.Date dWorkDay = null;
        ArrayList list = null;
        int iSubDay = 0;
        BaseOperDeal deal = new BaseOperDeal();
        try {
            deal.setYssPub(pub);
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            iSubDay = YssFun.toInt( (String) list.get(2));
            dWorkDay = deal.getWorkDay( (String) list.get(1), YssFun.toDate(String.valueOf(list.get(0)).substring(0, 10)), iSubDay);
            return YssFun.formatDate(dWorkDay);
        } catch (Exception e) {
            throw new YssException("获取的工作日期出错", e);
        }

    }

    /**
     * 获取资金调拔的编号
     * 参数:调拨日期
     * @return String
     * @throws YssException
     */
    public String getCashTransferNum() throws YssException {
        String sCashNum = "";
        String sTransDate = "";
        ArrayList list = null;
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sTransDate = String.valueOf(list.get(0)).substring(0, 10);
            sCashNum = "C" + YssFun.formatDate(YssFun.toDate(sTransDate), "yyyyMMdd") +
                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"),
                                       dbl.sqlRight("FNUM", 6), "000001");

            return sCashNum;
        } catch (Exception e) {
            throw new YssException("获取现金应收应付编号出错", e);
        }
    }

    /**
     * 获取现金应收应付编号
     * 参数:业务日期
     * @return String
     * @throws YssException
     */
    public String getCashPayRecNum() throws YssException {
        String sCashPRNum = "";
        String sPRDate = "";
        ArrayList list = null;
        try {
//         list =(ArrayList)obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sPRDate = YssFun.formatDate(YssFun.toDate(String.valueOf(list.get(0)).substring(0, 10)), "yyyyMMdd");
            sCashPRNum = sPRDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_CashPayRec"),
                                       dbl.sqlRight("FNum", 9), "000000001",
                                       " where FNum like 'SRP"
                                       + sPRDate + "%'", 1);
            sCashPRNum = "SRP" + sCashPRNum;
            return sCashPRNum;
        } catch (Exception e) {
            throw new YssException("获取现金应收应付编号出错", e);
        }
    }

    /**
     * 获取证券应收应付编号
     * 参数:业务日期
     * 2008-09-18
     * 邵宏伟 添加
     * BUG：0000473
     * @return String
     * @throws YssException
     */
    public String getSecRecPayNum() throws YssException {
        String sCashPRNum = "";
        String sPRDate = "";
        ArrayList list = null;
        try {
//         list =(ArrayList)obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sPRDate = YssFun.formatDate(YssFun.toDate(String.valueOf(list.get(0)).substring(0, 10)), "yyyyMMdd");
            sCashPRNum = sPRDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("tb_data_secrecpay"),
                                       dbl.sqlRight("FNum", 9), "000000001",
                                       " where FNum like 'SRP"
                                       + sPRDate + "%'", 1);
            sCashPRNum = "SRP" + sCashPRNum;
            return sCashPRNum;
        } catch (Exception e) {
            throw new YssException("获取证券应收应付编号出错", e);
        }
    }

    //参数:1参照货币 2美元兑原货币的汇率 3 美元兑基准货币的汇率
    public double calcExchangeRate() throws YssException {
        String sMarkCury = "CNY"; //默认为人民币
        String sCuryCode = "";
        double dMarkRate = 0.0;
        double dCuryRate = 0.0;
        double dResult = 0.0;
        ArrayList list = null;
        try {
//         list=(ArrayList)obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sMarkCury = pub.getBaseCury(); //取出组合群中的基准货币
            sCuryCode = (String) list.get(0); //原币所参照的货币
            dCuryRate = YssFun.toDouble(String.valueOf(list.get(1))); //美元兑原货币的汇率
            dMarkRate = YssFun.toDouble(String.valueOf(list.get(2))); //美元兑基准货币的汇率
            if (!sCuryCode.equals(sMarkCury)) { //当不等于系统的基础货币时转换
                dResult = YssD.div(dMarkRate, dCuryRate);
            } else {
                dResult = dCuryRate;
            }
        } catch (Exception ex) {
            throw new YssException("计算汇率转换出错", ex);
        }
        return dResult;
    }

    /**
     * 获取最大编号
     * @return String
     * @throws YssException
     */
    public String getMaxNum() throws YssException {
        ResultSet rs = null;
        ArrayList list = null;
        String strSql = "";
        String sResult = "";
        String sTableName = "";
        String sColName = "";
        String sOldMaxNum = "";
        String sNewMaxNum = "";
        String sPrefix = "";
        long iMaxNum = 0;
        try {
//         list = (ArrayList)this.obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            if (list.size() < 3) {
                throw new YssException("获取最大编号传入参数错误，请核对！");
            }
            sTableName = (String) list.get(0);
            sColName = (String) list.get(1);
            sPrefix = (String) list.get(2);
            strSql = "Select " + sColName + " FROM " + pub.yssGetTableName(sTableName) +
                " WHERE " + sColName + " LIKE '" + sPrefix + "%'" +
                //2008.08.28 蒋锦 修改 在排序时将排序字段转换为数值型 BUG：0000414
                " ORDER BY " + dbl.sqlToNumber(dbl.sqlSubStr(sColName, String.valueOf(sPrefix.length() + 1))) + " DESC";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                sOldMaxNum = rs.getString(sColName);
            }
            for (int i = 0; i < sOldMaxNum.length(); i++) {
                if (Character.isDigit(sOldMaxNum.charAt(i))) {
                    sNewMaxNum = sOldMaxNum.substring(i);
                    sPrefix = sOldMaxNum.substring(0, i);
                    break;
                }
            }
            iMaxNum = Long.parseLong(sNewMaxNum);
            iMaxNum += 1;
            sResult = sPrefix + String.valueOf(iMaxNum);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * 2008-02-24 蒋锦 添加 QDV4.1 MS00008 《QDV4.1赢时胜上海2009年2月1日07_A》
     * 完成接口导入证券信息时证券与估值方法绑定
     * @return String：绑定后的信息信息
     * @throws YssException
     */
    public String getSecrityLinkMtvMethod() throws YssException {
        String sResult = "";
        ArrayList list = null;
        ArrayList alSecurityCode = new ArrayList();
        try {
            list = (ArrayList) prepFun.getObj();
            //如果导入时不审核则不进行绑定直接退出方法
            if (prepFun.getCheck().equalsIgnoreCase("false")) {
                return sResult;
            }

            if (list.size() < 1) {
                return sResult;
            }
            alSecurityCode.add( (String) list.get(0));
            //调用逻辑处理
            MTVMethodLinkBean mtvLink = new MTVMethodLinkBean();
            mtvLink.setYssPub(pub);
            sResult = mtvLink.operSecurityLinkMtvMethod(alSecurityCode, "add");
        } catch (Exception e) {
            throw new YssException("证券绑定估值方法出错！", e);
        }
        return sResult;
    }
//-------------------------------------获取期间头寸交易数据编号------------------------------
    public String getCashTradeNum() throws YssException {   
        String strNumDate = "";
        String bargainDate = "";
        ArrayList list = null;
        String tradeNum = "";
        String subTradeNum = "00000";
        String beginNum = "";
        StringBuffer buf = null;
        String key = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); 
            if (String.valueOf(list.get(0)).length() >= 10) {
                bargainDate = String.valueOf(list.get(0)).substring(0, 10);
            } else {
                bargainDate = String.valueOf(list.get(0));
                bargainDate = YssFun.left(bargainDate, 4) +
                    "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
                    YssFun.right(bargainDate, 2);
            }
            beginNum = (String) list.get(1); //20071114   chenyibo   读入的交易数据的交易流水号要按照买入用200001,卖出用900001;分红用100001
            ImpCusInterface cusInterface = (ImpCusInterface) list.get(2);

            strNumDate = YssFun.formatDatetime(YssFun.toDate(
                bargainDate)).
                substring(0, 8);
            //修改原因:由于同一只证券是通过两个券商下单的,所以要读两次数据 //20080623
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName("TB_DATA_DIVINETRADEDATA"),
                                              dbl.sqlSubStr("Fnum", "10", "6"), "000000",
                                              " where FNum like '"
                                              + ("T" + strNumDate + beginNum.substring(0, 1)).replaceAll("'", "''") +
                                              "%'");
            if (tradeNum.equalsIgnoreCase("000000")) { //判断是否在交易表中当天是否读过买或者卖的数据,
                beginNum = beginNum.substring(0, 5) + "1"; //如果数据表中没有读过当天的数据的话，就用前台传过来的数据作为初始值；为何要+"1"因为第一笔数据总是从10001或者90001开头的
            } else {
                beginNum = tradeNum; //如果有数据存在就用数据表中最大的那个编号作为初始值
            }
            key = beginNum.substring(0, 1);
            tradeNum = cusInterface.getNum(key, beginNum);
            return "T" + strNumDate + tradeNum + subTradeNum;
        } catch (Exception e) {
            throw new YssException("获取期间交易数据Num出错");
        }
    }
 //----------------------------------------获取预估交易数据编号---------------------------------
    public String getCashPreTradeNum() throws YssException {  
        String strNumDate = "";
        String bargainDate = "";
        ArrayList list = null;
        String tradeNum = "";
        String subTradeNum = "00000";
        String beginNum = "";
        StringBuffer buf = null;
        String key = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); 
            if (String.valueOf(list.get(0)).length() >= 10) {
                bargainDate = String.valueOf(list.get(0)).substring(0, 10);
            } else {
                bargainDate = String.valueOf(list.get(0));
                bargainDate = YssFun.left(bargainDate, 4) +
                    "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
                    YssFun.right(bargainDate, 2);
            }
            beginNum = (String) list.get(1); //20071114   chenyibo   读入的交易数据的交易流水号要按照买入用200001,卖出用900001;分红用100001
            ImpCusInterface cusInterface = (ImpCusInterface) list.get(2);

            strNumDate = YssFun.formatDatetime(YssFun.toDate(
                bargainDate)).
                substring(0, 8);
            //修改原因:由于同一只证券是通过两个券商下单的,所以要读两次数据 //20080623
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName("TB_DATA_DIVINEESTIMATE"),
                                              dbl.sqlSubStr("Fnum", "10", "6"), "000000",
                                              " where FNum like '"
                                              + ("T" + strNumDate + beginNum.substring(0, 1)).replaceAll("'", "''") +
                                              "%'");
            if (tradeNum.equalsIgnoreCase("000000")) { //判断是否在交易表中当天是否读过买或者卖的数据,
                beginNum = beginNum.substring(0, 5) + "1"; //如果数据表中没有读过当天的数据的话，就用前台传过来的数据作为初始值；为何要+"1"因为第一笔数据总是从10001或者90001开头的
            } else {
                beginNum = tradeNum; //如果有数据存在就用数据表中最大的那个编号作为初始值
            }
            key = beginNum.substring(0, 1);
            tradeNum = cusInterface.getNum(key, beginNum);
            return "T" + strNumDate + tradeNum + subTradeNum;
        } catch (Exception e) {
            throw new YssException("获取期间预估交易数据Num出错");
        }
    }
  //----------------------------------------获取换汇表数据编号---------------------------------
    public String  getCashRateTradeNum() throws YssException {   
        String strNumDate = "";
        String bargainDate = "";
        ArrayList list = null;
        String tradeNum = "";
        String subTradeNum = "00000";
        String beginNum = "";
        StringBuffer buf = null;
        String key = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); 
            if (String.valueOf(list.get(0)).length() >= 10) {
                bargainDate = String.valueOf(list.get(0)).substring(0, 10);
            } else {
                bargainDate = String.valueOf(list.get(0));
                bargainDate = YssFun.left(bargainDate, 4) +
                    "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
                    YssFun.right(bargainDate, 2);
            }
            beginNum = (String) list.get(1); //20071114   chenyibo   读入的交易数据的交易流水号要按照买入用200001,卖出用900001;分红用100001
            ImpCusInterface cusInterface = (ImpCusInterface) list.get(2);

            strNumDate = YssFun.formatDatetime(YssFun.toDate(
                bargainDate)).
                substring(0, 8);
            //修改原因:由于同一只证券是通过两个券商下单的,所以要读两次数据 //20080623
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName("TB_DATA_DIVINERATETRADE"),
                                              dbl.sqlSubStr("Fnum", "10", "6"), "000000",
                                              " where FNum like '"
                                              + ("T" + strNumDate + beginNum.substring(0, 1)).replaceAll("'", "''") +
                                              "%'");
            if (tradeNum.equalsIgnoreCase("000000")) { //判断是否在交易表中当天是否读过买或者卖的数据,
                beginNum = beginNum.substring(0, 5) + "1"; //如果数据表中没有读过当天的数据的话，就用前台传过来的数据作为初始值；为何要+"1"因为第一笔数据总是从10001或者90001开头的
            } else {
                beginNum = tradeNum; //如果有数据存在就用数据表中最大的那个编号作为初始值
            }
            key = beginNum.substring(0, 1);
            tradeNum = cusInterface.getNum(key, beginNum);
            return "T" + strNumDate + tradeNum + subTradeNum;
        } catch (Exception e) {
            throw new YssException("获取期间预估交易数据Num出错");
        }
    }
    //add by nimengjing 2011.1.28 BUG #990 配置通过函数自动获取成交编号，系统处理时因编号重复造成无法插入该交易记录 
    //---------------------------自动获取成交编号------------------------------------------------
    public String getFuTradeNum()throws YssException{
    	String strNumDate = "";
        String bargainDate = "";
        ArrayList list = null;
        String tradeNum = "";
        String subTradeNum = "00000";
        String beginNum = "";
        StringBuffer buf = null;
        String key = "";
        try {
//         list = (ArrayList) obj;
            list = (ArrayList) prepFun.getObj(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            if (String.valueOf(list.get(0)).length() >= 10) {
                bargainDate = String.valueOf(list.get(0)).substring(0, 10);
            } else {
                bargainDate = String.valueOf(list.get(0));
                bargainDate = YssFun.left(bargainDate, 4) +
                    "-" + YssFun.mid(bargainDate, 4, 2) + "-" +
                    YssFun.right(bargainDate, 2);
            }
            beginNum = (String) list.get(1); //20071114   chenyibo   读入的交易数据的交易流水号要按照买入用200001,卖出用900001;分红用100001
            ImpCusInterface cusInterface = (ImpCusInterface) list.get(2);

            strNumDate = YssFun.formatDatetime(YssFun.toDate(
                bargainDate)).
                substring(0, 8);
            //修改原因:由于同一只证券是通过两个券商下单的,所以要读两次数据 //20080623
            tradeNum = dbFun.getNextInnerCode(pub.yssGetTableName("tb_data_futurestrade"),
                                              dbl.sqlSubStr("Fnum", "10", "6"), "000000",
                                              " where FNum like '"
                                              + ("T" + strNumDate + beginNum.substring(0, 1)).replaceAll("'", "''") +
                                              "%'");
            if (tradeNum.equalsIgnoreCase("000000")) { //判断是否在交易表中当天是否读过买或者卖的数据,
                beginNum = beginNum.substring(0, 5) + "1"; //如果数据表中没有读过当天的数据的话，就用前台传过来的数据作为初始值；为何要+"1"因为第一笔数据总是从10001或者90001开头的
            } else {
                beginNum = tradeNum; //如果有数据存在就用数据表中最大的那个编号作为初始值
            }
            key = beginNum.substring(0, 1);
            tradeNum = cusInterface.getNum(key, beginNum);
            return "T" + strNumDate + tradeNum + subTradeNum;
        } catch (Exception e) {
            throw new YssException("获取交易子表Num出错");
        }
    }
    //----------------------------------------------------end BUG #990---------------------------------------------------------------
    

    /**
     * 20131010 added by liubo.Story #13257.[海富通基金]QDIIV4.0[高]20131010001
     * 计算实收基金
     */
    public String getPaidinMoney() throws YssException {
        ArrayList list = null;
        double dSellAmount = 0; //销售数量
        double dSellMoney = 0; //销售金额
        double dUnPl = 0; //未实现损益平准金
        double dOtherMoney = 0; //其他金额

        String dTradeDate = ""; //交易日期
        String strPortCode = ""; //组合代码
        String strPortClsCode = ""; //需增加分级组合代码
        String strSellNetCode = ""; //网点代码
        String strAnalysisCode1 = "";
        String strAnalysisCode2 = "";
        String strAnalysisCode3 = "";
        String strSellCode = ""; //销售类型 
        
        double baseRate = 0;
        double portRate = 0;
        double yfshf = 0; // 应付赎回费
        double xsfsr = 0; // 销售费收入
        
        String curyCode = "";
        try {
            list = (ArrayList) prepFun.getObj();
            strPortClsCode = (String) list.get(0);
            dTradeDate = String.valueOf(list.get(1)).substring(0, 10);
            strSellNetCode = (String) list.get(2);
            strAnalysisCode1 = (String) list.get(3);

            dSellMoney = Double.parseDouble(String.valueOf(list.get(4)));
            
            dSellAmount = Double.parseDouble(String.valueOf(list.get(5)));
            
            TaPortClsBean portCls = new TaPortClsBean(); 
            portCls.setYssPub(pub);
            portCls.setPortClsCode(strPortClsCode);
            portCls.getSetting();

            strPortCode = portCls.getPortCode();
            
            TaTradeBean ta = new TaTradeBean();
            ta.setYssPub(pub);
            if(list.size() == 10){            	
            	curyCode = String.valueOf(list.get(6));            	
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "port");
            }
            else if(list.size() == 11){
            	curyCode = String.valueOf(list.get(6)); 
            	strSellCode = String.valueOf(list.get(7));            	
            	//获取确认日            	
                Date confirmDate = ta.getConfirmDay(strSellNetCode,
                		strPortClsCode, strPortCode, strSellCode, curyCode,
                        YssFun.parseDate(dTradeDate));
                BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "port");
	            ta.setDConfimDate(confirmDate);
	            ta.setStrSellTypeCode(strSellCode);//设置销售类型代码
            }
            else if(list.size() == 12){
            	curyCode = String.valueOf(list.get(6));     
            	yfshf = Double.parseDouble(String.valueOf(list.get(7)));
            	xsfsr = Double.parseDouble(String.valueOf(list.get(8)));
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "port");            	
            }
            else if(list.size() == 13){
            	curyCode = String.valueOf(list.get(6)); 
            	yfshf = Double.parseDouble(String.valueOf(list.get(7)));
            	xsfsr = Double.parseDouble(String.valueOf(list.get(8)));
            	strSellCode = String.valueOf(list.get(9));
            	//获取确认日
                Date confirmDate = ta.getConfirmDay(strSellNetCode,
                		strPortClsCode, strPortCode, strSellCode, curyCode,
                        YssFun.parseDate(dTradeDate));
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(confirmDate, curyCode, strPortCode, "port");    
	            ta.setStrSellTypeCode(strSellCode);//设置销售类型代码
	            ta.setStrCuryCode(curyCode);
            }
            else if(list.size() == 14){
            	curyCode = String.valueOf(list.get(6));     
            	yfshf = Double.parseDouble(String.valueOf(list.get(7)));
            	xsfsr = Double.parseDouble(String.valueOf(list.get(8)));
            	strSellCode = String.valueOf(list.get(9));
            	BaseOperDeal operDeal = new BaseOperDeal();
            	operDeal.setYssPub(pub);
            	baseRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "base");
            	portRate = operDeal.getCuryRate(YssFun.parseDate(dTradeDate, "yyyy-MM-dd"), curyCode, strPortCode, "port");      
            	ta.setStrSellTypeCode(strSellCode);//设置销售类型代码
            }
            else{            
	            if (list.size() > 9) {
	                if (list.get(6) == null) {
	                    dOtherMoney = 0;
	                } else {
	                    dOtherMoney = Double.parseDouble(String.valueOf(list.get(6)));
	                }
	            }
            }
            //获得相应的比例 是从净值表里面取的数据             
            ta.setStrPortCode(strPortCode);
            ta.setSPortClsCode(strPortClsCode);
            ta.setDTradeDate(YssFun.toSqlDate(dTradeDate));
            ta.setStrSellNetCode(strSellNetCode);
            ta.setStrAnalysisCode1(strAnalysisCode1);
            ta.setBeMarkMoney(YssD.add(dSellMoney, dOtherMoney));//从接口处设置基准金额
            ta.setDSellAmount(dSellAmount);//从接口处设置销售数量
            ta.setDBaseCuryRate(baseRate);
            ta.setDPortCuryRate(portRate);
            ta.setYfshf(yfshf);
            ta.setXsfsr(xsfsr);
            //---end---
            ta.getPL();

            return ta.getdPaidInMoney()+"";
        } catch (Exception e) {
            throw new YssException("获取实收基金出错！", e);
        }
    }

}
