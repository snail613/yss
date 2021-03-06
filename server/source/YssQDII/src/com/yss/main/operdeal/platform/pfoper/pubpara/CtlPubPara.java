package com.yss.main.operdeal.platform.pfoper.pubpara;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.yss.dsub.BaseBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;
import com.yss.commeach.EachPortCode;

public class CtlPubPara
    extends BaseBean {
	
	
    public CtlPubPara() {
    }


	/** 合并太平版本代码
    * 获取债券计息期间日期计算方式：autoCalc－系统自动计算  InfaceCalc－接口参数获取
    * QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
    * @return
    * @throws YssException
    */
	public String getIncomeFIDateCalcType() throws YssException {
		String reStr = "";
		String sResult = "autoCalc";//默认采用系统计算获取方式
		try {
			reStr = (String) getBaseCIGConfig("ComboBox1",
					"IncomeFIStats");
			reStr = parseResults(reStr);
			if (reStr!=null && reStr.length()>0) {
				sResult = reStr;
			}
		} catch (Exception e) {
			throw new YssException("获取债券计息期间日期出错！\r\n" + e.getMessage());
		}
		return sResult;
	}
	
    /**
     * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 2009-08-20 蒋锦 添加
     * 获取不计提债券利息的组合
     * @return HashMap
     * @throws YssException
     */
    public HashMap getDontStatBondIns() throws YssException{
        HashMap hmResult = new HashMap();
        String sValue = "";
        String[] arrPortCode = null;
        try{
            sValue = (String) getParamParas("dontstatbondins", "selPort", "<Port>", "",
                                            "", 4);
            if (sValue == null) {
                return hmResult;
            }

            sValue = sValue.split("[|]")[0];

            arrPortCode = sValue.split(",");
            for (int i = 0; i < arrPortCode.length; i++) {
                hmResult.put(arrPortCode[i], "ture");
            }
        } catch(Exception ex){
            throw new YssException("获取不计提债券利息的组合出错！", ex);
        }
        return hmResult;
    }
	
	    /**
     * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 2009-08-20 蒋锦 添加
     * 获取银行间债券银行手续费每日交收的组合
     * @return HashMap
     * @throws YssException
     */
    public HashMap getIntBakBankFee() throws YssException{
        HashMap hmResult = new HashMap();
        String sValue = "";
        String[] arrPortCode = null;
        try{
            sValue = (String) getParamParas("intbakBankfee", "selPort", "<Port>", "",
                                            "", 4);
            if (sValue == null) {
                return hmResult;
            }

            sValue = sValue.split("[|]")[0];

            arrPortCode = sValue.split(",");
            for (int i = 0; i < arrPortCode.length; i++) {
                hmResult.put(arrPortCode[i], "ture");
            }
        } catch(Exception ex){
            throw new YssException("获取银行间债券银行手续费每日交收的组合！", ex);
        }
        return hmResult;
    }

    /**
     * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 2009-08-20 蒋锦 添加
     * 获取银行间债券交易手续费每日交收的组合
     * @return HashMap
     * @throws YssException
     */
    public HashMap getIntBakTradeFee() throws YssException{
        HashMap hmResult = new HashMap();
        String sValue = "";
        String[] arrPortCode = null;
        try{
            sValue = (String) getParamParas("intbakTradefee", "selPort", "<Port>", "",
                                            "", 4);
            if (sValue == null) {
                return hmResult;
            }

            sValue = sValue.split("[|]")[0];

            arrPortCode = sValue.split(",");
            for (int i = 0; i < arrPortCode.length; i++) {
                hmResult.put(arrPortCode[i], "ture");
            }
        } catch(Exception ex){
            throw new YssException("获取银行间债券交易手续费每日交收的组合！", ex);
        }
        return hmResult;
    }

    /**
     * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 2009-08-20 蒋锦 添加
     * 获取银行间债券结算服务费每日交收的组合
     * @return HashMap
     * @throws YssException
     */
    public HashMap getIntBakSettleFee() throws YssException{
        HashMap hmResult = new HashMap();
        String sValue = "";
        String[] arrPortCode = null;
        try{
            sValue = (String) getParamParas("intbakSettlefee", "selPort", "<Port>", "",
                                            "", 4);
            if (sValue == null) {
                return hmResult;
            }

            sValue = sValue.split("[|]")[0];

            arrPortCode = sValue.split(",");
            for (int i = 0; i < arrPortCode.length; i++) {
                hmResult.put(arrPortCode[i], "ture");
            }
        } catch(Exception ex){
            throw new YssException("获取银行间债券结算服务费每日交收的组合！", ex);
        }
        return hmResult;
    }

    /**
    * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
    * 2009.07.03 蒋锦 添加
    * 获取银行间债券以成本市值孰低法估值的组合
    * @return HashMap
    * @throws YssException
    */
   public HashMap getInterBankBond() throws YssException{
       HashMap hmResult = new HashMap();
       String sValue = "";
       String[] arrPortCode = null;
       try {
           sValue = (String) getParamParas("interbankbond", "selPort", "<Port>", "",
                                           "", 4);
           if(sValue == null){
               return hmResult;
           }

           sValue = sValue.split("[|]")[0];

           arrPortCode = sValue.split(",");
           for(int i = 0; i < arrPortCode.length; i++){
               hmResult.put(arrPortCode[i], "ture");
           }
       } catch (Exception ex) {
           throw new YssException("获取银行间债券交易以成本市值孰低法估值的组合出错！", ex);
       }
       return hmResult;
   }


    /**
     * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 2009.07.03 蒋锦 添加 新增债券交易类型
     * 获取银行间债券以行情加利息税估值的组合
     * @return HashMap
     * @throws YssException
     */
    public HashMap getInterBankInsDuty() throws YssException{
        HashMap hmResult = new HashMap();
        String sValue = "";
        String[] arrPortCode = null;
        try {
            sValue = (String) getParamParas("interbankinsduty", "selPort", "<Port>", "",
                                            "", 4);
            if(sValue == null){
                return hmResult;
            }

            sValue = sValue.split("[|]")[0];

            arrPortCode = sValue.split(",");
            for(int i = 0; i < arrPortCode.length; i++){
                hmResult.put(arrPortCode[i], "ture");
            }
        } catch (Exception ex) {
            throw new YssException("获取银行间债券交易以行情加利息税估值的组合出错！", ex);
        }
        return hmResult;
    }

    /**
     * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 2009.07.03 蒋锦 添加 新增债券交易类型
     * 获取银行间交易按成交编号计算的组合
     * @return HashMap
     * @throws YssException
     */
    public HashMap getInterBankOrderPorts() throws YssException{
        HashMap hmResult = new HashMap();
        String sValue = "";
        String[] arrPortCode = null;
        try {
            sValue = (String) getParamParas("interbankorder", "selPort", "<Port>", "",
                                            "", 4);
            if(sValue == null){
                return hmResult;
            }

            sValue = sValue.split("[|]")[0];

            arrPortCode = sValue.split(",");
            for(int i = 0; i < arrPortCode.length; i++){
                hmResult.put(arrPortCode[i], "ture");
            }
        } catch (Exception ex) {
            throw new YssException("获取银行间债券交易按成交编号计算的组合出错！", ex);
        }
        return hmResult;
    }

    /** 
     * add by  guolongchao  20110815 STORY #1207 
     * 获取银行间债券交易按：成交编号、日期先后、买卖方式，三种不同的计算卖出成本的方法
     * 将组合代码作为key，成本核算方式作为value，存入hashmap中。
     * @return HashMap
     * @throws YssException
     */
    public HashMap getInterBankSelCostPorts(String pubParaCode) throws YssException {
    	HashMap hashmap=new HashMap();
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;     
        try 
        {
            sqlStr = " select distinct FParagroupCode,FPubParaCode,FParaId from "+pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                      " where FPubParaCode = '" + pubParaCode + "' and FParaId <> 0 group by  FParagroupCode,FPubParaCode,FParaId" +
                      " order by FParaId asc";
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) 
            {   
            	String key="";//核算方式对应的投资组合（可以是多个组合，用逗号隔开）
            	String value="";//银行间债券成本的核算方式
                sqlStr = "select * from " +pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FPubParaCode = '" + pubParaCode +"' and FParaId = " +grpRs.getInt("FParaId");
                   
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) 
                {
                	if(rsTest.getString("FCtlCode").equals("ComboBox1"))//银行间债券成本的核算方式
                		value= rsTest.getString("FCtlValue");
                	if(rsTest.getString("FCtlCode").equals("SelectControl1"))//核算方式对应的投资组合（可以是多个组合，用逗号隔开）
                		key=rsTest.getString("FCtlValue");
                }
                dbl.closeResultSetFinal(rsTest);
                
                if(key!=null&&key.length()>0)
                {
                	String[] arry=key.split("[|]")[0].split(",");
                	if(arry!=null&&arry.length>0)
                	{
                		for(int i=0;i<arry.length;i++)
                			if(!hashmap.containsKey(arry[i]))
                			{
                				 hashmap.put(arry[i], value);
                			}                		         
                	}
                }
            }
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally 
        {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }
        return hashmap;
    }
    
    /** 
     * add by  guolongchao  20110928 STORY #1483   从通用参数中获取TA交易数据来源表
     * @param  pubParaCode   通用业务参数代码
     * @param  portCode      组合代码
     * @return String   通用业务参数中设置的临时表
     * @throws YssException
     */
    public String getTaTradeDataSourceTable(String pubParaCode,String portCode) throws YssException {    
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;  
        String tempPortCode = "";
        String tempTableCode = "";
        try 
        {
            sqlStr = " select distinct FParagroupCode,FPubParaCode,FParaId from "+pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                      " where FPubParaCode = '" + pubParaCode + "' and FParaId <> 0 group by  FParagroupCode,FPubParaCode,FParaId" +
                      " order by FParaId asc";
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) 
            {
                sqlStr = "select * from " +pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FPubParaCode = '" + pubParaCode +"' and FParaId = " +grpRs.getInt("FParaId");                   
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) 
                {
                	if(rsTest.getString("FCtlCode").equals("PortCode"))
                		tempPortCode= rsTest.getString("FCtlValue");
                	if(rsTest.getString("FCtlCode").equals("TableCode"))
                		tempTableCode=rsTest.getString("FCtlValue");
                }
                dbl.closeResultSetFinal(rsTest); 
                
                if(tempPortCode.length()>0&&tempPortCode.split("[|]")[0].trim().equals(portCode))
                {
                	if(tempTableCode.length()>0)
                		return tempTableCode.split("[|]")[0].trim();
                }
            }
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally 
        {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }
       return "";
    }
    /**
     * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 2009.07.03 蒋锦 添加 新增债券交易类型
     * 获取银行间电汇费是否计入成本
     * @return HashMap
     * @throws YssException
     */
    public HashMap getInterBankBondFee() throws YssException{
        HashMap hmResult = new HashMap();
        String sValue = "";
        String[] arrPortCode = null;
        try {
            sValue = (String) getParamParas("interbankfee", "selPort", "<Port>", "",
                                            "", 4);
            if(sValue == null){
                return hmResult;
            }

            sValue = sValue.split("[|]")[0];

            arrPortCode = sValue.split(",");
            for(int i = 0; i < arrPortCode.length; i++){
                hmResult.put(arrPortCode[i], "ture");
            }
        } catch (Exception ex) {
            throw new YssException("获取银行间电汇费是否计入成本出错！", ex);
        }
        return hmResult;
    }
	
    /**
     * 设置净值表参数，无与组合相同的参数返回 false
     * 本次参数包括：
     *   设置是否汇总现金类0101类型下的应收应付项
     *   设置是否将表中所有的应收应付项汇总并单独显示在当日小结中
     * QDV4华夏2009年8月24日03_A MS00652 by leeyu 20090831
     * @param portCode String
     * @return HashMap
     * @throws YssException
     */
    public HashMap getNavDataParams(String portCode) throws YssException {
        HashMap hmNav = new HashMap();
        String cashTotal = "false", total = "false";
        String sValue = "";
        String[] arrPort = null;
        try {
            sValue = (String) getParamParas("NavDataParams", "Port", "<Port>", "", "", 4);
            if (sValue == null) {
                sValue = cashTotal + "\t" + total;
                hmNav.put(portCode, sValue);
                return hmNav;
            } else {
                sValue = sValue.split("[|]")[0];
                arrPort = sValue.split(",");
                for (int i = 0; i < arrPort.length; i++) {
                    if (!arrPort[i].equalsIgnoreCase(portCode)){
                        continue;
                    }
                    sValue = (String) getParamParas("NavDataParams", "cboCash", "<CashTotal>", "","", 2);
                    if (sValue != null) {
                        sValue = parseResults(sValue);
                        if (sValue.equalsIgnoreCase("1")){
                            cashTotal = "true";
                        }else{
                            cashTotal = "false";
                        }
                    } else {
                        cashTotal = "false";
                    }

                    sValue = (String) getParamParas("NavDataParams", "cboTotal", "<Total>", "","", 2);
                    if (sValue != null) {
                        sValue = parseResults(sValue);
                        if (sValue.equalsIgnoreCase("1")){
                            total = "true";
                        }else{
                            total = "false";
                        }
                    } else {
                        total = "false";
                    }
                    sValue = cashTotal + "\t" + total;
                    hmNav.put(portCode, sValue);
                    return hmNav;
                }
            }
            if (hmNav.get(portCode) == null)
                hmNav.put(portCode, "false\tfalse");
        } catch (Exception ex) {
            throw new YssException("获取净值表通用参数配置出错！", ex);
        }
        return hmNav;
    }

    /**2009.06.25 蒋锦 添加 MS00013 《QDV4.1赢时胜（上海）2009年4月20日13_A》  国内基金业务
     * 获取基金使用前一日行情估值的交易所和组合代码
     * @return HashMap：key = 组合代码， 交易所代码 = value
     * @throws YssException
     */
    public HashMap getFunMarketValueDate() throws YssException{
        String reStr = "";
        String sPortCode = "";
        String sExchangeCode = "";
        String[] arrPortCode = null;
        HashMap hmResult = new HashMap();
        try {
            reStr = (String) getParamParas("fundmktvalue", "selPortCode", "<port>", "", 4);
            if (reStr != null) {
                sPortCode = reStr.split("[|]")[0];
            } else {
                return null;
            }
            reStr = (String) getParamParas("fundmktvalue", "selExchange", "<exchange>", "", 4);
            if (reStr != null) {
                sExchangeCode = reStr.split("[|]")[0];
            }

            arrPortCode = sPortCode.split(",");
            for(int i = 0; i < arrPortCode.length; i++){
                hmResult.put(arrPortCode[i], sExchangeCode);
            }
        } catch (Exception ex) {
            throw new YssException("获取基金使用前一日行情估值的交易所和组合代码出错！", ex);
        }
        return hmResult;
    }

    /**
     * add by songjie 2013.03.07
     * 获取“股指期货是否统计券商”通用参数数据
     */
    public HashMap getFUStBroker() throws YssException{
        String reStr = "";
        String sPortCode = "";
        String sExchangeCode = "";
        String[] arrPortCode = null;
        HashMap hmResult = new HashMap();
        try {
            reStr = (String) getParamParas("FUStBroker", "selPort1", "<port>", "", 4);
            if (reStr != null) {
                sPortCode = reStr.split("[|]")[0];
            } else {
                return null;
            }
            reStr = (String) getParamParas("FUStBroker", "cboYesOrNo1", "<Result>", "", 2);
            if (reStr != null) {
                sExchangeCode = reStr;
            }

            arrPortCode = sPortCode.split(",");
            for(int i = 0; i < arrPortCode.length; i++){
                hmResult.put(arrPortCode[i], sExchangeCode);
            }
        } catch (Exception ex) {
        	//edit by songjie 2013.05.14 修改“股指期货是否统计券商”通参 获取方法对应的异常提示信息
            throw new YssException("获取股指期货是否统计券商通参出错！", ex);
        }
        return hmResult;
    }
    
    /**
     * MS00013 国内基金业务 QDV4.1赢时胜（上海）2009年4月20日13_A
     * 2009-06-22 蒋锦 添加
     * 获取货币基金计息的相关参数
     * key = 组合代码 + \t + 词汇代码
     * @return HashMap
     * @throws YssException
     */
    public HashMap getMonetaryFundIncomeCalaParams() throws YssException {
        HashMap hmResult = new HashMap();
        ResultSet rs = null;
        ResultSet rsSub = null;
        String sPortCodes = "";
        String[] arrPortCode = null;
        String sVYesOrNo = "";
        String sVInsCals = "";
        String sVRateRes = "";
        //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
        String sVMFRateDate = "";
        String sVStartDate = "";
        //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
        String mfInsMode="";//story 2617 by zhouwei 20120511
        String sqlStr = "";
        try {
            sqlStr = "SELECT FParaID FROM " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                " WHERE FPubParaCode = " + dbl.sqlString("monetaryfund") +
                " AND FParaID <> 0 GROUP BY FParaID ORDER BY FParaID";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                sqlStr = "SELECT * FROM " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " WHERE FPubParaCode = " + dbl.sqlString("monetaryfund") +
                    " AND FParaID = " + dbl.sqlString(rs.getString("FParaID"));
                rsSub = dbl.openResultSet(sqlStr);
                while (rsSub.next()) {
                    if (rsSub.getString("FCtlCode").equalsIgnoreCase("cboYesOrNo")) {
                        sVYesOrNo = rsSub.getString("FCtlValue").split(",")[0];
                    } else if (rsSub.getString("FCtlCode").equalsIgnoreCase("cboRateResult")) {
                        sVRateRes = rsSub.getString("FCtlValue").split(",")[0];
                    } else if (rsSub.getString("FCtlCode").equalsIgnoreCase("cboInsCala")) {
                        sVInsCals = rsSub.getString("FCtlValue").split(",")[0];
                    } else if (rsSub.getString("FCtlCode").equalsIgnoreCase("selPortCode")) {
                        sPortCodes = rsSub.getString("FCtlValue").split("[|]")[0];
                    } 
                    //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
                    else if (rsSub.getString("FCtlCode").equalsIgnoreCase("cboStartDate")) {
                    	sVStartDate = rsSub.getString("FCtlValue").split(",")[0];
                    } else if (rsSub.getString("FCtlCode").equalsIgnoreCase("cboMFRateDate")) {
                    	sVMFRateDate = rsSub.getString("FCtlValue").split(",")[0];
                    }
                    //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
                    //story 2617 add  by zhouwei 20120511  增加计提模式的参数
                    else if(rsSub.getString("FCtlCode").equalsIgnoreCase("cboIncomeMode")){
                    	mfInsMode=rsSub.getString("FCtlValue").split(",")[0];
                    }
                }
                arrPortCode = sPortCodes.split(",");
                for (int i = 0; i < arrPortCode.length; i++) {
                    //是否计息
                    hmResult.put(arrPortCode[i] + "\t" + YssCons.YSS_FUN_CONTINUE, sVYesOrNo);
                    //红利结转方式
                    hmResult.put(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_RATERESULT, sVRateRes);
                    //福利计算方式
                    hmResult.put(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_FUNDINSCALA, sVInsCals);
                    //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A start---//
                    //计提开始日期
                    hmResult.put(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_FUNDSTARTDATE, sVStartDate);
                    //获取基金万份收益的日期
                    hmResult.put(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_FUNDMFRATEDATE, sVMFRateDate);
                    //---add by songjie 2011.08.08 需求 1218 QDV4赢时胜（招商证券）2011年06月10日01_A end---//
                    //story 2617 add  by zhouwei 20120511  增加计提模式的参数
                    hmResult.put(arrPortCode[i] + "\t" + YssCons.YSS_INCOMECAL_MONETARYFUND_INSMODE, mfInsMode);
                }
                dbl.closeResultSetFinal(rsSub);
            }
        } catch (Exception ex) {
            throw new YssException("获取货币基金收益计提相关参数出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmResult;
    }

    /**
     * 获取回购国内的组合级别的通用参数设置
     * @param sPortCode String 组合代码
     * @param ctlCode 要获取值所在的控件代码
     * @param paramSetCode 通用参数设置代码
     * @return String
     * @throws YssException
     * MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A sj
     */
    public String getPurchasePortParams(String sPortCode, String ctlCode, String paramSetCode) throws YssException {
        String reStr = "";
        try {
            reStr = parseResults(getParaWithPortValue(sPortCode, ctlCode,
                paramSetCode)); //直接在这里输入参数编号和控件代号。
            if (reStr == null || reStr.length() == 0) {
                reStr = "Yes"; //默认值
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
    
    
    
    
    

    /**
     * QDV4华夏2009年6月15日01_A MS00509  by leeyu 20090616
     * @return String
     * @throws YssException
     */
    public String getAutoOperData() throws YssException {
        int iOffDay = 0; //偏离天数
        String sExeMode = "singleDay"; //执行模式,(分为singleDay按日循环、interzone区间)默认为按日循环
        String reStr = "";
        try {
            reStr = (String) getParamParas("AutoOperData", "cboMode", "<Mode>", "", 2);
            if (reStr != null) {
                sExeMode = parseResults(reStr);
            } else {
                sExeMode = "singleDay";
            }
            reStr = (String) getParamParas("AutoOperData", "txtDays", "<Day>", "", 1);
            if (reStr != null) {
                iOffDay = Math.abs(YssFun.toInt(reStr));
            } else {
                iOffDay = 0;
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return String.valueOf(iOffDay) + "\t" + sExeMode;
    }

    /**
     * 用于显示财务估值表已实现收益数据
     * 默认不显示，返回　false
     * QDV4海富通2009年05月11日01_AB MS00439
     * by leeyu 20090515
     * @return boolean
     * @throws YssException
     */
    public boolean checkGuessReal(String sPort) throws YssException {
        boolean bResult = false; //默认
        String reStr = "";
        String sShowType = "";
        //---add by songjie 2011.08.04 BUG 2331 QDV4易方达2011年7月28日01_B---//
        String strSql = "";
        ResultSet rs = null;
        //---add by songjie 2011.08.04 BUG 2331 QDV4易方达2011年7月28日01_B---//
        try {
        	//---add by songjie 2011.08.04 BUG 2331 QDV4易方达2011年7月28日01_B---start//
        	strSql = " select pub.FCtlValue from (select * from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
        	" where FPubParaCode = 'finish_ParaSY' and FCtlValue like '" + sPort + "%') para"+
        	" left join " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " pub on para.Fctlgrpcode = pub.fctlgrpcode " +
        	" and para.fparaid = pub.fparaid where pub.FCtlCode = 'cboAcctLevel' ";
        	
        	rs = dbl.openResultSet(strSql);
        	while(rs.next()){
        		sShowType = rs.getString("FCtlValue");
        	}
        	
        	if (!sShowType.equals("") && parseResults(sShowType).equalsIgnoreCase("1")) {
                bResult = true;
            } else {
                bResult = false;
            }
        	//---add by songjie 2011.08.04 BUG 2331 QDV4易方达2011年7月28日01_B---end//
        	//---delete by songjie 2011.08.04 BUG 2331 QDV4易方达2011年7月28日01_B---start//
//            reStr = (String) getParamParas("finish_ParaSY", "selPort",
//                                           "<port>", "", 4);
//            if (reStr != null && reStr.split("[|]")[0].equalsIgnoreCase(sPort)) {
//                sShowType = (String) getParamParas("finish_ParaSY", "cboAcctLevel",
//                    "<check>", "", 2);
//                if (sShowType != null && parseResults(sShowType).equalsIgnoreCase("1")) {
//                    bResult = true;
//                } else {
//                    bResult = false;
//                }
//            } else {
//                bResult = false;
//            }
        	//---delete by songjie 2011.08.04 BUG 2331 QDV4易方达2011年7月28日01_B---end//
            return bResult;
        } catch (Exception ex) {
            return false;
        } 
        //---add by songjie 2011.08.04 BUG 2331 QDV4易方达2011年7月28日01_B---start//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //---add by songjie 2011.08.04 BUG 2331 QDV4易方达2011年7月28日01_B---end//
    }
    
    /**
     * 获取现金差额使用费率
     * MS00002
     * QDV4.1赢时胜（上海）2009年9月28日01_A
     * by 操忠虎   2009/11/24
     * @param sPort
     */
    public double getCashBalFeeRate(String sPort)throws YssException{
    	
    	 double dResult=0;
    	 String reStr = "";
    	try{
    		reStr=(String)this.getParamParas("cashbalFee", "cashbalFeeRate", "<feeRate>", sPort, 1);
    		
    		if(reStr!=null){
    			dResult=Double.valueOf(reStr).doubleValue();
    		}  		
    	}catch(Exception ex){
    		return 0;
    	}
    	return dResult; 	
    }
      

    /**
     * 获取判断是否能够生成财务估值表
     * @param sportCode String 组合代码
     * @return String 通用参数的设置值
     * @throws YssException
     * MS00402 QDV4海富通2009年04月21日01_AB
     * authod sj
     */
    public String getJudgeGreateGuess(String sportCode) throws YssException {
        String resultStr = null;
        sportCode = sportCode.replaceAll("'", "");
        resultStr = getParaWithPortValue(sportCode, "cboYesOrNo",
                                         "finish_JudgeGuess"); //直接在这里输入参数编号和控件代号。
        if (null != resultStr) { //当返回值不为null
            resultStr = parseResults(resultStr); //解析返回值
            if (resultStr.equalsIgnoreCase("1")) { //当保存的数据为1时,将其值解析成yes
                resultStr = "Yes";
            } else { //其它情况下，解析成no
                resultStr = "no";
            }
        } else {
            resultStr = "no"; //默认值为no,不判断是否能够生成财务估值表
        }
        return resultStr;
    }

    /**
     * 2008-11-13 蒋锦 添加
     * 编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
     * 获取使用高精度数计算综合损益汇兑损益的通用参数
     * @return boolean
     * @throws YssException
     */
    public boolean getCalIncomeFxUserRound8() throws YssException {
        String reStr = "";
        boolean bResult = false;
        try {
            reStr = (String) getBaseCIGConfig("cboYesOrNo", "calincomefxuseround8");
            reStr = parseResults(reStr);
            if (reStr.equalsIgnoreCase("1")) {
                bResult = true;
            }
        } catch (Exception e) {
            throw new YssException("获取通用参数计算股值增值是否舍入出错！\r\n" + e.getMessage());
        }
        return bResult;
    }

    /**
     * add by songjie 2013.08.27 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 获取通参：期货占用保证金数据来源
     * 
     * @param portCode
     * @return
     * @throws YssException
     */
    public String getFUOccupiedBailSource(String portCode) throws YssException {
        String reStr = "";
        StringBuffer sbSqlStr = new StringBuffer();
        StringBuffer sbSqlSubStr = new StringBuffer();
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValue = "";
        int paraID = 0;
        HashMap hmID = new HashMap();
        HashMap paraInfo = null;
        try {
            sbSqlStr
            .append("select distinct FParagroupCode,FPubParaCode,FParaId from ")
            .append(pub.yssGetTableName("Tb_Pfoper_Pubpara"))//通用参数设置表
            .append(" para ")
            .append(" join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face ")//通用参数控件设置表
            .append(" on para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode ")
            .append(" where para.FPubParaCode = ").append(dbl.sqlString("FUOccupiedBailsource"))
            .append(" and face.FCtlInd = '<port>' ")
            .append(" and para.FCtlValue like '").append(portCode).append("%'") 
            .append(" and para.FParaId <> 0 order by para.FParaID desc"); 
            grpRs = dbl.openResultSet(sbSqlStr.toString());
            while (grpRs.next()) {
            	sbSqlSubStr.setLength(0);
            	sbSqlSubStr
            	.append("select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from (select * from ")
                .append(pub.yssGetTableName("Tb_Pfoper_Pubpara"))
                .append(" where FPubParaCode = ").append(dbl.sqlString("FUOccupiedBailsource"))
                .append(" and FParaId = ").append(grpRs.getInt("FParaId"))
                .append(") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on ")
                .append(" para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode");
                rsTest = dbl.openResultSet(sbSqlSubStr.toString());
                while (rsTest.next()) {
                	resultValue = " ";
                	paraID = grpRs.getInt("FParaId");
                	
                    if (rsTest.getString("FCtlInd").equalsIgnoreCase("<Result>")) { 
                        resultValue = rsTest.getString("FCtlValue").split(",")[0];
                    }else{
                    	if(rsTest.getString("FCtlValue").split("[|]").length >= 1){
                    		resultValue = rsTest.getString("FCtlValue").split("[|]")[0];
                    	}
                    }
                    
                	if(hmID.get(paraID) == null){
                		paraInfo = new HashMap();
                	}else{
                		paraInfo = (HashMap)hmID.get(paraID);
                	}
                	
            		paraInfo.put(rsTest.getString("FCtlInd"), resultValue);
            		hmID.put(paraID, paraInfo);
                }
                
                dbl.closeResultSetFinal(rsTest);
            }
            
            sbSqlStr.setLength(0);
            
            if(hmID.size() > 0){
            	Iterator iter = hmID.keySet().iterator();
            	while(iter.hasNext()){
            		paraID = ((Integer)iter.next()).intValue();
            		paraInfo = (HashMap)hmID.get(paraID);
            		reStr += (String)paraInfo.get("<port>") + "\t";
            		reStr += (String)paraInfo.get("<exchange>") + "\t";
            		reStr += (String)paraInfo.get("<broker>") + "\t";
            		reStr += (String)paraInfo.get("<Result>");
            /**Start 20131023 modified by liubo.Bug #81708.QDV4招商基金2013年10月21日01_B
             * 换行符给的有点问题，应该没一条记录就给一个，而不是所有记录最后给一个
             * 否则会造成无论多少条记录，最后都只解析出一条*/
            		reStr += "\r\f";
            	}
//            	reStr += "\r\f";
            /**End 20131023 modified by liubo.Bug #81708.QDV4招商基金2013年10月21日01_B*/
            }
            
            if(reStr.indexOf("\r\f") > -1){
            	reStr = reStr.substring(0, reStr.length() - 2);
            }
            
            return reStr;
        } catch (Exception e) {
            throw new YssException("获取通参：期货占用保证金数据来源出错", e);
        } finally {
            dbl.closeResultSetFinal(rsTest, grpRs);
        }
    }
    
    /**
     * 是否使用ACTV来判断停牌信息
     * @return boolean
     * @throws YssException
     */
    public boolean getIsUseACTVInfo(String portCode) throws YssException {
        String reStr = "";
        boolean bResult = false;
        try {
            portCode = portCode.replaceAll("'", "");
            reStr = parseResults(getParaWithPortValue(portCode, "cboYesOrNo",
                "finish_IsUseACTVInfo")); //直接在这里输入参数编号和控件代号。
            if (reStr.equalsIgnoreCase("1")) {
                bResult = true;
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return bResult;
    }

    /**
     * 在计算估值增值做 价格 * 数量 的时候是否四舍五入
     * @return boolean
     * @throws YssException
     */
    public boolean getMVIsRound() throws YssException {
        String reStr = "";
        boolean bResult = true;
        try {
            ParaWithMVRound mv = new ParaWithMVRound();
            mv.setYssPub(pub);
            reStr = parseResults( (String) mv.getParaResult());
            if (reStr.equalsIgnoreCase("0")) {
                bResult = false;
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return bResult;
    }

    /**
     * 是否计算卖出估值增值
     * @return boolean
     * @throws YssException
     */
    public boolean getIsSellTradeRelaCal(String portCode) throws YssException {
        String reStr = "";
        boolean bResult = false;
        try {
            portCode = portCode.replaceAll("'", "");
            reStr = parseResults(getParaWithPortValue(portCode, "cboYesOrNo",
                "finish_IsSellTrade")); //直接在这里输入参数编号和控件代号。
            if (reStr.equalsIgnoreCase("1")) {
                bResult = true;
            }
        } catch (Exception e) {
            //throw new YssException(e.getMessage());
            throw new YssException(e); //by caocheng 2009.02.05 MS00004 QDV4.1-2009.2.1_09A 异常处理
        }
        return bResult;
    }

    /**
     * 接口处理中导出数据时的默认编码类型
     * @return boolean
     * @throws YssException
     */
    public String getEncodingType() throws YssException {
        String reStr = "";
        try {
            ParaWithDAOEncodingType ent = new ParaWithDAOEncodingType();
            ent.setYssPub(pub);
            reStr = parseResults( (String) ent.getParaResult());
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return reStr;
    }

    /**
     * 根据交易类型计算加权平均成本
     * @param sTradeType String
     * @throws YssException
     * @return String
     */
    public String getTradeTypeAvgCost(String sTradeType) throws YssException {
        String reStr = "";
        try {
            reStr = parseResults(getParaWithTradeType(sTradeType, "operdata",
                "trade_avgcost")); //直接在这里输入参数编号和参数组编号。
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }

    /**
     * 返回股指期货的核算类型
     * 需求：QDV4中金2009年02月27日01_A MS00273 by leeyu 20090316
     *      key="FIFO"   value=组合代码   先进先出算法     默认值,若无设置默认为全部组合
     *      key="MODAVG" value=组合代码   移动加权平均算法
     *      key="THEDAYFIRSTFIFO" value=组合代码  当日优先先入先出
     * @return Hashtable
     * @throws YssException
     */
    //不要使用这个方法了
    //使用这个方法时请注意：因为此方法不支持获取多组合时的核算类型，已经被getFurAccountType()取代。
    public Hashtable getFuturesAccountType() throws YssException {
        String sKey = "";
        String sValue = "";
        Hashtable htFutures = new Hashtable();
        try {
            sValue = (String) getParamParas("AccountType", "Combo", "<Ports>", "",
                                            "", 2); //取设置有先进先出的组合列表
            if (sValue == null || this.parseResults(sValue) == null) { //这里添加对空值的判断，QDV4赢时胜（上海）2009年4月8日01_B MS00354 by leeyu 20090409
                sKey = "FIFO"; //先取出所有的先进先出的组合。
                sValue = ""; //默认没有设置取空值
            } else if (this.parseResults(sValue).equalsIgnoreCase("FIFO")) {
                sKey = "FIFO"; //先取出所有的先进先出的组合。
                sValue = (String) getParamParas("AccountType", "PortCode", "<Type>",
                                                "", "", 4);
                if (sValue == null) {
                    sValue = "";
                } else {
                    sValue = sValue.split("[|]")[0];
                }
                htFutures.put(sKey, sValue);
            } else if (this.parseResults(sValue).equalsIgnoreCase("MODAVG")) {
                sKey = "MODAVG"; //取出所有的移动加权平均法的组合
                sValue = (String) getParamParas("AccountType", "PortCode", "<Type>",
                                                "", "", 4);
                if (sValue == null) {
                    sValue = "";
                } else {
                    sValue = sValue.split("[|]")[0];
                }
                htFutures.put(sKey, sValue);
            } else if (this.parseResults(sValue).equalsIgnoreCase("THEDAYFIRSTFIFO")) {
                sKey = "THEDAYFIRSTFIFO"; //取出所有的当日优先移动加权平均法的组合
                sValue = (String) getParamParas("AccountType", "PortCode", "<Type>",
                                                "", "", 4);
                if (sValue == null) {
                    sValue = "";
                } else {
                    sValue = sValue.split("[|]")[0];
                }
                htFutures.put(sKey, sValue);
            }
        } catch (Exception ex) {
            throw new YssException("获取股指期货的核算方式参数出错", ex);
        }
        return htFutures;
    }

    /**
     * 当日单位净值涨跌幅比例 参数
     * key="Market" value=booleanStr 市场设置
     * key="Show" value =boolean 显示设置 默认为不显示
     * QDV4海富通2008年12月31日03_B MS00176 by leeyu 20090211
     * @return Hashtable
     * @throws YssException
     */
    public Hashtable getPercentChange() throws YssException {
        String reStr = "";
        String sValue = "";
        String sKey = "";
        Hashtable htValue = null;
        try {
            htValue = new Hashtable();
            sKey = "Market";
            sValue = (String) getParamParas("dayPercentChange", "SelectControl1", "<Exchange>", "", "", 4); //取主要交易市场
            if (sValue != null && sValue.length() > 0) {
                htValue.put(sKey, sValue.split("[|]")[0]);
            } else { //如果交易市场为空，下面的是否显示默认为不显示
                htValue.put(sKey, "null");
                htValue.put("Show", "false");
                return htValue;
            }
            sKey = "Show";
            sValue = (String) getParamParas("dayPercentChange", "ComboBox1", "<Result>", "", "", 2); //取是否显示
            if (sValue != null && parseResults(sValue).equalsIgnoreCase("1")) {
                sValue = "true";
            } else {
                sValue = "false";
            }
            htValue.put(sKey, sValue);
        } catch (Exception ex) {
            throw new YssException("获取当日净值的涨跌幅比例出错！");
        }
        return htValue;
    }

    /**
     * 加权平均成本是否计算当天交易
     * @param sPortCode String
     * @throws YssException
     * @return String
     */
    public String getAvgCost(String sPortCode) throws YssException {
        String reStr = "";
        try {
            reStr = parseResults(getParaWithPortValue(sPortCode, "cboAvgCast",
                "finish_avgcost")); //直接在这里输入参数编号和控件代号。
            if (reStr == null || reStr.length() == 0) {
                reStr = "Yes"; //默认计入 sj edit 20080602
            } else {
                reStr = parseResults(reStr);
            }

            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }

    /**
     * 单位净值保留位数
     * @param sPortCode String
     * @throws YssException
     * @return String
     */
    public String getCashUnit(String sPortCode) throws YssException {
        String reStr = "";
        try {
            reStr = getParaWithPortValue1(sPortCode, "txtdigit", //modify huangqirong 2012-04-18 story #2088
                                         "finish_unit"); //直接在这里输入参数编号和参数组编号。
            if (reStr == null || reStr.length() == 0) {
                reStr = "3";
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }

    /**
     * 可用头寸是否包含证券清算款
     * @param sPortCode String
     * @throws YssException
     * @return String
     */
    public String getCashLiqu(String sPortCode) throws YssException {
        String reStr = "";
        try {
            reStr = getParaWithPortValue(sPortCode, "cboCashligu",
                                         "finish_cashliqu"); //直接在这里输入参数编号和控件编号。sj edit 20080530
            if (reStr == null || reStr.length() == 0) {
                reStr = "No";
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
    
    /**
     * add by wangzuochun 2009.10.28 MS00756 增加在生成新净值表后，执行某个接口群里的接口的功能  QDV4华夏2009年10月19日01_A 
     * 根据组合代码获取导入接口群代码
     * @param sPortCode
     * @return
     * @throws YssException
     */
    public String getImpInterGroup(String sPortCode) throws YssException {
        String reStr = "";
        try {
            reStr = getParaWithPortValue(sPortCode, "selImpGroup",
                                         "ImpExpGroup"); //直接在这里输入参数编号和控件编号。sj edit 20080530
            if (reStr == null || reStr.length() == 0) {
                reStr = "No";
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
    
    /**
     * add by wangzuochun 2009.10.28 MS00756 增加在生成新净值表后，执行某个接口群里的接口的功能  QDV4华夏2009年10月19日01_A 
     * 根据组合代码获取导出接口群代码
     * @param sPortCode
     * @return
     * @throws YssException
     */
    public String getExpInterGroup(String sPortCode) throws YssException {
        String reStr = "";
        try {
            reStr = getParaWithPortValue(sPortCode, "selExpGroup",
                                         "ImpExpGroup"); //直接在这里输入参数编号和控件编号。sj edit 20080530
            if (reStr == null || reStr.length() == 0) {
                reStr = "No";
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }

    /**
     * 处理公共参数（证券信息、行情、汇率数据）是否跨组合群处理
     * @return Hashtable key="" value=Boolean.toString() 默认为 false 用旧表处理数据
     * key="security" value=Boolean.toString()
     * key="marketvalue" value=Boolean.toString()
     * key="exchangerate" value=Boolean.toString()
     * byleeyu 20090203 MS00131 QDV4建行2008年12月25日01_A
     * @throws YssException
     */
    public Hashtable getPubParamType() throws YssException {
        Hashtable htPub = new Hashtable();
        String sValue = "";
        String sKey = "";
        try {
            sValue = (String) getParamParas("pubdataset", "cboExchangeRate", "<ExchangeRate>", "", "", 2); //取汇率
            sKey = "exchangerate";
            if (sValue != null && parseResults(sValue).equalsIgnoreCase("1")) { //当不为空时，且值为真时
                sValue = "true";
            } else {
                sValue = "false";
            }
            htPub.put(sKey, sValue);
            sValue = (String) getParamParas("pubdataset", "cboMarketValue", "<MarketValue>", "", "", 2); //取行情
            sKey = "marketvalue";
            if (sValue != null && parseResults(sValue).equalsIgnoreCase("1")) { //当不为空时，且值为真时
                sValue = "true";
            } else {
                sValue = "false";
            }
            htPub.put(sKey, sValue);
            sValue = (String) getParamParas("pubdataset", "cboSecurity", "<Security>", "", "", 2); //取证券信息
            sKey = "security";
            if (sValue != null && parseResults(sValue).equalsIgnoreCase("1")) { //当不为空时，且值为真时
                sValue = "true";
            } else {
                sValue = "false";
            }
            htPub.put(sKey, sValue);
        } catch (Exception ex) {
            throw new YssException("获取跨组合群的公共参数信息出错");
        }
        return htPub;
    }

    /**
     * 获取Ta的汇兑损益是否计入已实现
     * @param sPortCode String
     * @throws YssException
     * @return String
     */
    public String getTaIncome(String sPortCode) throws YssException {
        String reStr = "";
        try {
            reStr = parseResults(getParaWithPortValue(sPortCode, "taTest",
                "taInCome")); //直接在这里输入参数编号和参数组编号。
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        return reStr;
    }

    /**
     * 取净值的保留位数设置
     * @param sPortCode String
     * @param sparaGrpCode String
     * @param sPubParaCode String
     * @return String
     * @throws YssException
     */
    /* public int getNetValueDigit(String sPortCode) throws YssException{
        try{
     return Integer.parseInt( getNetValueDigit("dayfinish","finish_unit",sPortCode));
        }catch(Exception ex){
           throw new YssException(ex.toString());
        }
     }*/
    /**
     * TA金额中的结算金额与销售金额
     * @param sPortCode String
     * @param sparaGrpCode String
     * @param sPubParaCode String
     * @return String
     * @throws YssException
     */
    public String getTAFeeUnit(String sPortCode) throws YssException {
        String strResult = "";
        try {
            strResult = getParaWithPortValue(sPortCode, "comFee",
                                             "TA_TAInCome");
            if (strResult == null || strResult.length() == 0) {
                strResult = "-1";
            } else {
                strResult = parseResults(strResult);
            }
            return strResult;
        } catch (Exception ex) {
            throw new YssException(ex.toString());
        }
    }

    /**
     * 2008-5-31
     * 单亮
     * 获取汇兑损益方向
     * @param sPortCode String 传入的组合
     * @return String
     * @throws YssException
     */
    public String getInCome(String sPortCode) throws YssException {
        String resultStr = "";
        try {
            resultStr = getParaWithPortValue(sPortCode, "cboInCome",
                                             "TA_TAInCome");
            if (resultStr == null || resultStr.length() == 0) {
                resultStr = "Yes";
            } else {
                resultStr = parseResults(resultStr);
            }
            return resultStr;
        } catch (Exception ex) {
            throw new YssException(ex.toString());
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    /**
     * 以组合为参数的方法获取的值,所有以组合为判断条件的通用参数都使用这个方法获取返回值。
     * @param sPortCode String
     * @param sCtlGrpCode String
     * @param sPubParaCode String
     * @throws YssException
     * @return String
     */
    private String getParaWithPortValue(String sPortCode, //将参数从参数组出去 sj edit 20080528
                                        String sPubParaCode) throws YssException {
        try {
            ParaWithPort parawithport = new ParaWithPort();
            parawithport.setPortCode(sPortCode);
            //parawithport.setParaGroupCode(sCtlGrpCode);
            parawithport.setPubParaCode(sPubParaCode);
            parawithport.setYssPub(pub);
            //-----------设置参数完成----------------------
            return (String) parawithport.getParaResult();
        } catch (Exception e) {
            throw new YssException("以组合获取参数值出错！", e);
        }
    }
    
    /**
     * 20110620 added by liub.Story #1132
     * 以组合和参数ID作为参数，获取参数的返回值
     * @param sPortCode String
     * @param sCtlGrpCode String
     * @param sPubParaCode String
     * @param sParaID String
     * @throws YssException
     * @return String
     */
    
    private String getParaWithParaID(String sPortCode, //将参数从参数组出去 sj edit 20080528
            String sPubParaCode,String sParaID) throws YssException {
    	try {
			ParaWithPort parawithport = new ParaWithPort();
			parawithport.setPortCode(sPortCode);
			parawithport.setParaID(sParaID);
			parawithport.setPubParaCode(sPubParaCode);
			parawithport.setYssPub(pub);
			//-----------设置参数完成----------------------
			return (String) parawithport.getParaResult();
		} catch (Exception e) {
			throw new YssException("以组合获取参数值出错！", e);
		}
	}
    
    

    /**
     * 通过传入特定的控件,获取指定的控件值.
     * @param sPortCode String
     * @param sCtlCode String
     * @param sPubParaCode String
     * @return String
     * @throws YssException
     */
    private String getParaWithPortValue(String sPortCode, String sCtlCode,
                                        String sPubParaCode) throws YssException {
        try {
            ParaWithPort parawithport = new ParaWithPort();
            parawithport.setPortCode(sPortCode);
            parawithport.setCtlCode(sCtlCode);
            parawithport.setPubParaCode(sPubParaCode);
            parawithport.setYssPub(pub);
            //-----------设置参数完成----------------------
            return (String) parawithport.getSpeParaResult(); //通过特定的控件,获取制定的控件值.
        } catch (Exception e) {
            throw new YssException("以组合获取参数值出错！", e);
        }
    }
    
    /**
     * add by huangqirong 2012-04-18 story #2088
     * @throws YssException
     */
    private String getParaWithPortValue1(String sPortCode, String sCtlCode,
                                        String sPubParaCode) throws YssException {
        try {
            ParaWithPort parawithport = new ParaWithPort();
            parawithport.setPortCode(sPortCode);
            parawithport.setCtlCode(sCtlCode);
            parawithport.setPubParaCode(sPubParaCode);
            parawithport.setYssPub(pub);
            //-----------设置参数完成----------------------
            return (String) parawithport.getSpeParaResult1(); //通过特定的控件,获取制定的控件值.
        } catch (Exception e) {
            throw new YssException("以组合获取参数值出错！", e);
        }
    }

    private String getParaWithTradeType(String sTradeType, String sparaGrpCode,
                                        String sPubParaCode) throws YssException {
        try {
            ParaWithTradeType tradeType = new ParaWithTradeType();
            tradeType.setTradeType(sTradeType);
            tradeType.setParaGroupCode(sparaGrpCode);
            tradeType.setPubParaCode(sPubParaCode);
            tradeType.setYssPub(pub);
            //---------------------------------------------------------------------
            return (String) tradeType.getParaResult();
        } catch (Exception e) {
            throw new YssException("以交易类型获取参数值出错！", e);
        }
    }

    /*private String getNetValueDigit(String sParaGrpCode,String sPubParaCode,String sPortCode) throws YssException{
       try{
          ParaWithDigit paraDigit =new ParaWithDigit();
          paraDigit.setYssPub(pub);
          paraDigit.setParaGroupCode(sParaGrpCode);//("dayfinish");
          paraDigit.setPubParaCode(sPubParaCode);//("finish_unit");
          paraDigit.setSPortCode(sPortCode);//(valReport.getPortCode());
          return (String)paraDigit.getParaResult();
       }catch(Exception ex){
          throw new YssException("取净值保留位数出错",ex);
       }
        }*/

    /*private String getTASellFee(String sParaGrpCode,String sPubParaCode,String sPortCode) throws YssException{
       try{
          ParaWithTAFees paraDeal =new ParaWithTAFees();
          paraDeal.setYssPub(pub);
          paraDeal.setPubParaCode(sPubParaCode);
          paraDeal.setParaGroupCode(sParaGrpCode);
          paraDeal.setSPortCode(sPortCode);
          return parseResults((String)paraDeal.getParaResult());
       }catch(Exception ex){
          throw new YssException("取损益平准金的计算方式出错",ex);
       }
        }*/
    //-----------------------------------------------------------------------------------------------------------------------------------

    /**
     * 为了在以组合为参数的方法获取的值为combox类型的时候解析返回值。
     * @param results String
     * @throws YssException
     * @return String
     */
    public String parseResults(String results) throws YssException {
        String reStr = "";
        try {
            if (results.length() > 0 && results.indexOf(",") > 0) {
                reStr = results.split(",")[0];
                //add by xuqiji 20090513:QDV4海富通2009年05月11日02_AB MS00441  为直销申购款计息时取数建立一张临时表
            } else if (results.length() > 0 && results.indexOf("|") > 0) {
                reStr = results.split("[|]")[0]; //参数为“XXX|XXX”类型，为了获取“|”前的数据
                //---------------------------------end---------------------------------------------------//
            } else {
                reStr = results;
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("解析结果出错！", e);
        }
    }

    public String getAccPaidDigit(String CashAccCode) throws YssException {
        String reStr = "";
        try {
            reStr = parseResults(getAccPaidDigit(CashAccCode, "cboAccPaidDigit",
                                                 "Cat_PaidAcc")); //直接在这里输入参数编号和控件代号。
            if (reStr == null || reStr.length() == 0) {
                reStr = "round"; //默认计入
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }

    }

    private String getAccPaidDigit(String CashAccCode, String sCtlCode,
                                   String sPubParaCode) throws
        YssException {
        try {
            ParaWithAccPaidDigit parawithAcc = new ParaWithAccPaidDigit();
            parawithAcc.setCashAccCode(CashAccCode);
            parawithAcc.setCtlCode(sCtlCode);
            parawithAcc.setPubParaCode(sPubParaCode);
            parawithAcc.setYssPub(pub);
            //-----------设置参数完成----------------------
            return (String) parawithAcc.getSpeParaResult(); //通过特定的控件,获取制定的控件值.
        } catch (Exception e) {
            throw new YssException("以组合获取参数值出错！", e);
        }
    }

    public String getReCalCost() throws YssException {
        String reStr = "";
        try {
            reStr = parseResults(getReCalCost("cboReCalCost",
                                              "DayFinish_ValReCost")); //直接在这里输入参数编号和控件代号。
            if (reStr == null || reStr.length() == 0) {
                reStr = "no"; //默认计入
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }

    }

    private String getReCalCost(String sCtlCode,
                                String sPubParaCode) throws
        YssException {
        try {
            return (String) getBaseCIGConfig(sCtlCode, sPubParaCode); //通过特定的控件,获取制定的控件值.
        } catch (Exception e) {
            throw new YssException("获取参数值出错！", e);
        }
    }

    /**
     * 获取通用参数设置_费用划款指令设置数据
     * add by songjie 
     * 2011.05.13
     * 需求 759
     * QDV4工银2011年3月7日05_A
     * @param portCode
     * @param tradeDate
     * @return
     * @throws YssException
     */
    public HashMap getFeeSetOfCashCommand(String portCode, Date tradeDate)throws YssException{
    	ResultSet rs = null;
    	String strSql = "";
    	HashMap hmFeeTypeSet = null;
    	HashMap hmFeeTypeSets = new HashMap();
    	HashMap hmFeeType = new HashMap();
    	HashMap hmCreateCycle = new HashMap();
    	String fkCode = null;
    	String skCode = null;
    	try{
			BaseOperDeal baseOperDeal = new BaseOperDeal(); // 新建BaseOperDeal
			baseOperDeal.setYssPub(pub);
    		
    		strSql = " select * from Tb_Fun_Vocabulary a where a.FVocTypeCode = 'feetype_cashCommand' ";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			hmFeeType.put(rs.getString("FVocCode"), rs.getString("FVocName"));
    		}
    		
    		dbl.closeResultSetFinal(rs);
    		
    		strSql = " select * from Tb_Fun_Vocabulary a where a.FVocTypeCode = 'CreCycle_cashCommand' ";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			hmCreateCycle.put(rs.getString("FVocCode"), rs.getString("FVocName"));
    		}
    		
    		dbl.closeResultSetFinal(rs);
    		
    		strSql = " select distinct a1.Fparaid,b.Fportcode,b1.feetype,b2.DKCount, " + 
    		" b3.zlcount,b4.fkcode,b5.skcode,b6.holiday,b7.createcycle " +
    		" from (select a.Fpubparacode, a.Fparaid, a.fctlcode, a.fctlvalue from " +
    		pub.yssGetTableName("TB_PFOper_PUBPARA") + 
    		" a where a.Fpubparacode = 'CashCommand_fee' and a.Fparaid <> 0) a1 " + 
    		" left join (select fpubparacode, FCtlCode, FCtlValue as FPortCode, fparaid from " + 
    		pub.yssGetTableName("TB_PFOper_PUBPARA") + 
    		" where fctlcode = '组合') b on a1.fpubparacode = b.fpubparacode and a1.fparaid = b.fparaid " + 
    		" left join (select fpubparacode, FCtlCode, FCtlValue as FeeType, fparaid from " + 
    		pub.yssGetTableName("TB_PFOper_PUBPARA") + 
    		" where fctlcode = '费用类型') b1 on a1.fpubparacode = b1.fpubparacode and a1.fparaid = b1.fparaid " + 
    		" left join (select fpubparacode, FCtlCode, FCtlValue as DKCount, fparaid from " + 
    		pub.yssGetTableName("TB_PFOper_PUBPARA") + 
    		" where fctlcode = '到款日期') b2 on a1.fpubparacode = b2.fpubparacode and a1.fparaid = b2.fparaid " + 
    		" left join (select fpubparacode, FCtlCode, FCtlValue as ZLCount, fparaid from " + 
    		pub.yssGetTableName("TB_PFOper_PUBPARA") + 
    		" where fctlcode = '指令日期') b3 on a1.fpubparacode = b3.fpubparacode and a1.fparaid = b3.fparaid " + 
    		" left join (select fpubparacode, FCtlCode, FCtlValue as FKCode, fparaid from " + 
    		pub.yssGetTableName("TB_PFOper_PUBPARA") + 
    		" where fctlcode = '付款人') b4 on a1.fpubparacode = b4.fpubparacode and a1.fparaid = b4.fparaid " + 
    		" left join (select fpubparacode, FCtlCode, FCtlValue as SKCode, fparaid from " + 
    		pub.yssGetTableName("TB_PFOper_PUBPARA") + 
    		" where fctlcode = '收款人') b5 on a1.fpubparacode = b5.fpubparacode and a1.fparaid = b5.fparaid " + 
    		" left join (select fpubparacode, FCtlCode, FCtlValue as Holiday, fparaid from " + 
    		pub.yssGetTableName("TB_PFOper_PUBPARA") + 
    		" where fctlcode = '节假日群') b6 on a1.fpubparacode = b6.fpubparacode and a1.fparaid = b6.fparaid " + 
    		" left join (select fpubparacode,FCtlCode,FCtlValue as CreateCycle,fparaid from " + 
    		pub.yssGetTableName("TB_PFOper_PUBPARA") + 
    		" where fctlcode = '自动生成周期') b7 on a1.fpubparacode = b7.fpubparacode and a1.fparaid = b7.fparaid " +
    		" where b.FPortCode like '" + portCode + "|%'";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			hmFeeTypeSet = new HashMap();
    			hmFeeTypeSet.put("PortCode", parseResults(rs.getString("FPortCode")));//组合代码
    			hmFeeTypeSet.put("PortName", rs.getString("FPortCode").split("[|]")[1]);//组合名称
    			hmFeeTypeSet.put("FeeType", (String)hmFeeType.get(parseResults(rs.getString("FeeType"))));//费用类型
    			hmFeeTypeSet.put("DKDate", baseOperDeal.getWorkDay(parseResults(rs.getString("Holiday")), tradeDate, rs.getInt("DKCount") - 1));//到款日期
    			hmFeeTypeSet.put("ZLDate", baseOperDeal.getWorkDay(parseResults(rs.getString("Holiday")), tradeDate, rs.getInt("ZLCount") - 1));//指令日期
    			fkCode = parseResults(rs.getString("FKCode"));
    			skCode = parseResults(rs.getString("SKCode"));
    			hmFeeTypeSet.put("FKCode", fkCode);//付款人代码
    			hmFeeTypeSet.put("SKCode", skCode);//收款人代码
    			hmFeeTypeSet = getReceiverInfo(hmFeeTypeSet,fkCode,skCode);//获取付款人收款人相关信息
    			hmFeeTypeSet.put("Holiday", parseResults(rs.getString("Holiday")));//节假日群代码
    			hmFeeTypeSet.put("CreateCycle", (String)hmCreateCycle.get(parseResults(rs.getString("CreateCycle"))));//生成周期
    			hmFeeTypeSets.put(parseResults(rs.getString("FPortCode")) + "," + 
    					(String)hmFeeType.get(parseResults(rs.getString("FeeType"))), hmFeeTypeSet);
    		}
    		return hmFeeTypeSets;
    	}catch(Exception e){
    		throw new YssException("获取参数值出错！", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }    
    
    /**
     * 获取付款人收款人相关信息
     * add by songjie 
     * 2011.05.13
     * 需求 759
     * QDV4工银2011年3月7日05_A
     * @param hmFeeTypeSet
     * @param fkCode
     * @param skCode
     * @return
     * @throws YssException
     */
    public HashMap getReceiverInfo(HashMap hmFeeTypeSet, String fkCode, String skCode)throws YssException{
    	ResultSet rs = null;
    	String strSql = "";
    	try{
    		strSql = " select a.* from " + pub.yssGetTableName("tb_para_receiver") +
    		" a where a.freceivercode in ('" + fkCode + "','" + skCode + "')";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			//若为付款人信息
    			if(fkCode.equals(rs.getString("FRECEIVERCODE"))){
    				hmFeeTypeSet.put("FKName", rs.getString("FRECEIVERNAME"));//付款人名称
    				hmFeeTypeSet.put("FKBank", rs.getString("FOPERBANK"));//付款人银行
    				hmFeeTypeSet.put("FKBankAccount", rs.getString("FACCOUNTNUMBER"));//付款人账号
    				hmFeeTypeSet.put("FKCuryCode", rs.getString("FCURYCODE"));//付款人币种
    			}
    			//若为收款人信息
    			if(skCode.equals(rs.getString("FRECEIVERCODE"))){
    				hmFeeTypeSet.put("SKName", rs.getString("FRECEIVERNAME"));//收款人名称
    				hmFeeTypeSet.put("SKBank", rs.getString("FOPERBANK"));//收款人银行
    				hmFeeTypeSet.put("SKBankAccount", rs.getString("FACCOUNTNUMBER"));//收款人账号
    				hmFeeTypeSet.put("SKCuryCode", rs.getString("FCURYCODE"));//收款人币种
    			}
    		}
    		return hmFeeTypeSet;
    	}catch(Exception e){
    		throw new YssException("获取付款人收款人信息出错！", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    public String getNavType() throws YssException {
        String reStr = "";
        try {
            reStr = parseResults(getNavType("cboNavType",
                                            "DayFinish_ValType")); //直接在这里输入参数编号和控件代号。
            if (reStr == null || reStr.length() == 0) {
                reStr = "new"; //默认用新净值表数据。
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }

    }
    
    /**
     * add by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A
     * @return
     * @throws YssException
     */
    public String getInterestMode() throws YssException {
        String reStr = "";
        try {
            reStr = parseResults(getNavType("interestMode",
                                            "TASellInterest")); //直接在这里输入参数编号和控件代号。
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }

    }
    
    /**
     * add by wangzuochun 2010.05.07 MS01135 外汇交易中有关人民币账户的资金调拨汇率计算不正确 QDV4华夏2010年4月27日01_AB 
     * @return
     * @throws YssException
     */
    public String getRateTradeMode() throws YssException {
        String reStr = "";
        try {
            reStr = parseResults(getNavType("RateTradeGet_mode",
                                            "rateTradeGet")); //直接在这里输入参数编号和控件代号。
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }

    }
    
    private String getNavType(String sCtlCode,
                              String sPubParaCode) throws
        YssException {
        try {
            return (String) getBaseCIGConfig(sCtlCode, sPubParaCode); //通过特定的控件,获取制定的控件值.
        } catch (Exception e) {
            throw new YssException("获取参数值出错！", e);
        }
    }

    public String getDivideCalType(String SecurityCode) throws YssException {
        String reStr = "";
        try {
            reStr = getDivideCalType(SecurityCode, "CboCalCury", "FundData_Divide");
            if (reStr == null) { //没有此项设置时.
                reStr = "no";
                return reStr;
            } else {
                if (reStr.trim().length() == 0) {
                    reStr = "yes"; //默认使用分红币种
                } else {
                    reStr = parseResults(reStr);
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        return reStr;
    }

    private String getDivideCalType(String SecurityCode, String sCtlCode,
                                    String sPubParaCode) throws YssException {
        ParaWithSecurity parawithSec = new ParaWithSecurity();
        parawithSec.setSecurityCode(SecurityCode);
        parawithSec.setCtlCode(sCtlCode);
        parawithSec.setPubParaCode(sPubParaCode);
        parawithSec.setYssPub(pub);
        //-----------设置参数完成----------------------
        return (String) parawithSec.getSpeParaResult(); //通过特定的控件,获取制定的控件值.
    }

    public String getPriMarketPrice() throws YssException {
        String reStr = "";
        try {
            reStr = getPriMarketPrice("CboPriMarketPrice",
                                      "DayFinish_ValMktPri"); //控制20的位数上限。sj 20081112
            if (reStr == null) {
                reStr = "valuation";
                return reStr;
            } else {
                if (reStr.trim().length() == 0) {
                    reStr = "valuation"; //2008.10.31 蒋锦 修改 默认使用老的方法
                } else {
                    reStr = parseResults(reStr);
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        return reStr;
    }

    private String getPriMarketPrice(String sCtlCode,
                                     String sPubParaCode) throws YssException {

        return (String) getBaseCIGConfig(sCtlCode, sPubParaCode); //通过特定的控件,获取制定的控件值.
    }

    public String getKeepFourDigit() throws YssException {
        // MS00344 QDV4富国2009年3月31日01_B  -----
        String reStr = null; //将初始值默认为null,避免if的语句不能执行
        //----------------------------------------
        try {
            reStr = getKeepFourDigit("CboKeepFourDigit",
                                     "DayFinish_ICDigit"); //控制20的位数上限。sj 20081112
            if (reStr == null) {
                reStr = "two";
                return reStr;
            } else {
                if (reStr.trim().length() == 0) {
                    // MS00344 QDV4富国2009年3月31日01_B  --------------
                    reStr = "two"; //当有节点，但没有设置值时，默认为保留两位
                    //-------------------------------------------------
                } else {
                    reStr = parseResults(reStr);
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        return reStr;
    }

    private String getKeepFourDigit(String sCtlCode,
                                    String sPubParaCode) throws YssException {
        return (String) getBaseCIGConfig(sCtlCode, sPubParaCode);
    }

    /**
     * 此参数获取方法为判断是否为中报类型方式来进行流程控制的方法。所有需求辨别是否为中报方式的方法都调用此方法。
     * @param sCtlCode String
     * @param sPubParaCode String
     * @return Object
     * @throws YssException
     */
    private Object getBaseCIGConfig(String sCtlCode,
                                    String sPubParaCode) throws YssException {
        ParaBaseCIGConfig baseCigConfig = new ParaBaseCIGConfig();

        baseCigConfig.setCtlCode(sCtlCode);
        baseCigConfig.setPubParaCode(sPubParaCode);
        baseCigConfig.setYssPub(pub);
        //-----------设置参数完成----------------------
        return baseCigConfig.getSpeParaResult(); //通过特定的控件,获取制定的控件值.

    }

    /**
     * 对月计提费用的判断
     * @param PortCode String
     * @return String
     * @throws YssException
     * sj add MS00052
     */
    public String getInvestInfo(String PortCode) throws YssException {
        String InvestCodes = "";
        String formula = "";
        try {
            InvestCodes = (String) getParamParas("finish_CalcFee", "Fees", "<Fees>", "<Port>", PortCode, 4);
            formula = (String) getParamParas("finish_CalcFee", "SelFormula", "<Formula>", "<Port>", PortCode, 4);
        } catch (Exception e) {
            throw new YssException("获取通用参数-费用计算出错!");
        }
        return InvestCodes + "\t" + formula;
    }

    /**
     * 处理送股权益数据的相关信息 MS00125 leeyu 2009-01-04
     * @param PortCodes String 组合代码,也可以为一组组合代码，其中一组组合代码中间用“,”分隔
     * @return Hashtable,KEY＝组合代码，VALUE＝boolean值［若PortCode在参数中返回true，不在返回false］
     * @throws YssException
     */
    public Hashtable getBondShareInfo(String PortCodes) throws YssException {
        Hashtable htPortValue = new Hashtable();
        boolean bCheck = false; //默认为不做此功能
        String sResult = "";
        String[] portCode = null;
        try {
            portCode = PortCodes.split(",");
            for (int i = 0; i < portCode.length; i++) {
                sResult = (String) getParamParas("BonusShareSet", "CboYesNo", "<Ports>", portCode[i], 4);
//            if(sResult==null ||sResult.length()==0 || sResult.equals("0")){ //若没有查到数据 或查到的数据值为0时 说明这个组合不需要设置这个功能
                if (sResult != null && parseResults(sResult).equals("1")) { //通过parseResults()方法解析
                    bCheck = true;
                } else {
                    bCheck = false;
                }
                htPortValue.put(portCode[i], new Boolean(bCheck));
            }
        } catch (Exception ex) {
            throw new YssException("\r\n" + ex.getMessage());
        }
        return htPortValue;
    }
    
    /**
     * add by songjie 2012.03.05
     * STORY #2147 QDV4赢时胜(海富通)2012年01月29日01_A
     * 获取交易结算参数设置信息
     * @param portCode
     * @param exchangeCode
     * @return
     * @throws YssException
     */
    public HashMap getTradeSettleInfo(String portCode, String exchangeCode) throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	HashMap hmTotal = new HashMap();
    	HashMap hmDetail = null;
    	
    	HashMap hmPara = null;
    	HashMap hmFinal = null;
        String key = null;
        int minKey = 0;
        Iterator iter = null;
    	try{
    		//查询 通用参数  交易结算参数设置 获取所有设置数据
    		strSql = " select * from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
    		" a where a.Fpubparacode = 'tradeDtDelayDay' and a.fparaid <> '0' order by a.fparaid ";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			if(hmTotal.get(rs.getString("FParaId")) == null){
    				hmDetail = new HashMap();
    				setHmTradeInfo(rs, hmDetail);
    			}else{
    				hmDetail = (HashMap)hmTotal.get(rs.getString("FParaId"));
    				setHmTradeInfo(rs, hmDetail);
    			}
    			
    			hmTotal.put(rs.getString("FParaId"), hmDetail);
    		}
    		
            iter = hmTotal.keySet().iterator();

            while(iter.hasNext()){
            	key = (String)iter.next();
            	hmDetail = (HashMap)hmTotal.get(key);
            	//获取相关组合、相关交易所 对应的 id 最小的通用参数数据
            	if(hmDetail.get("port").equals(portCode) && hmDetail.get("exchange").equals(exchangeCode)){
            		if(minKey == 0){
            			minKey = Integer.parseInt(key);
            			hmFinal = hmDetail;
            		}else{
						if (minKey > Integer.parseInt(key)) {
							minKey = Integer.parseInt(key);
							hmFinal = hmDetail;
						}
            		}
            	}
            }
    		
    		return hmFinal;
    	}catch(Exception e){
    		throw new YssException("获取通用参数：交易数据延迟天数设置 信息出错！", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /**
     * add by songjie 2012.03.05
     * STORY #2147 QDV4赢时胜(海富通)2012年01月29日01_A
     * 获取交易结算参数设置 信息
     * @param rs
     * @param hmDetail
     * @throws YssException
     */
    private void setHmTradeInfo(ResultSet rs, HashMap hmDetail) throws YssException{
    	try{
			if(rs.getString("FCtlCode").equals("组合")){
				hmDetail.put("port", rs.getString("FCtlValue").split("[|]")[0]);
			}
			if(rs.getString("FCtlCode").equals("交易所")){
				hmDetail.put("exchange", rs.getString("FCtlValue").split("[|]")[0]);
			}
			if(rs.getString("FCtlCode").equals("节假日群")){
				hmDetail.put("holiday", rs.getString("FCtlValue").split("[|]")[0]);
			}
			if(rs.getString("FCtlCode").equals("结算日期")){
				hmDetail.put("settle", rs.getString("FCtlValue"));
			}
    	}catch(Exception e){
    		throw new YssException("获取通用参数：交易数据延迟天数设置 信息出错！", e);
    	}
    }
    
	/**
	 * //add by lidaolong 20110409 #427 划款指令模板需更新
	 * update by guolongchao 20110929 STORY 1483 划款指令需要支持投资组合
	 * @param portCode 组合代码
	 * @return
	 * @throws YssException
	 */
    public String getCommandModeStyle(String portCode)throws YssException {
    	String modeStyle="systemStyle";//划款指令模板样式
    	ResultSet rs= null;
    	ResultSet rsTest= null;
    	String sqlStr =null;
    	String tempPortCodes ="";//用来存放组合（可以多选）
    	String commandStyle="";//用来存放组合对应的模板设置
    	//modeStyle = (String) getParamParas("templeteStyle", "commandStyle", "<commandStyle>", "", 2);
    	String strSql ="select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from" +
    					" (select * from "  + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
    					" where FPubParaCode = 'templeteStyle') para " +
    			"left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face " +
    			"on   para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode   order by para.fparaid";
    	try {
			rs = dbl.openResultSet(strSql);
			while (rs.next())
			{
//				if (rs.getString("FCtlInd") != null ){
//					modeStyle = rs.getString("FCtlValue").split(",")[0];
//					break;
//				}
				  sqlStr = "select * from " +pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                           " where FPubParaCode = 'templeteStyle' and FParaId = " +rs.getInt("FParaId");                   
	              rsTest = dbl.openResultSet(sqlStr);
	              while (rsTest.next()) 
	              {
	              	if(rsTest.getString("FCtlCode").equals("SelectControl1"))
	              		tempPortCodes= rsTest.getString("FCtlValue");//存放多组合
	              	if(rsTest.getString("FCtlCode").equals("commandStyle"))
	              		commandStyle=rsTest.getString("FCtlValue");//存放模板类型
	              }
	              dbl.closeResultSetFinal(rsTest); 
	              
	              if(tempPortCodes.length()>0&&tempPortCodes.split("[|]")[0].trim().indexOf(portCode)>=0)
	              {
	              	if(commandStyle.length()>0)
	              		return commandStyle.split(",")[0].trim();
	              }
			}
		} catch (Exception e) {
			throw new YssException("获取划款指令模板出错!");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		
    	if (modeStyle == null){
    		modeStyle="systemStyle";//当没有设置划款指令模板时,取系统现有的
    	}
    	return modeStyle;
    }
    
    /**
     * // add by lidaolong #665 凭证导入时检查凭证币种，弹出提示窗口
     * 获取设置的凭证属性代码
     * @return
     * @throws YssException 
     */
    public String getCtlCode() throws YssException{
		ResultSet rs = null;
		String vchCodes = "";
		String strSql ="select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from" +
		" (select * from "  + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
		" where FPubParaCode = 'allowManyCury') para " +
		"left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face " +
		"on   para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode   order by para.fparaid";

		try {
			/*String codes = (String) getParamParas("allowManyCury", "vchCode",
			 "<vchCode>", "", 4);*/
			rs = dbl.openResultSet(strSql);
			while (rs.next()){
				if (rs.getString("FCtlInd") != null ){
					
					vchCodes += rs.getString("FCtlValue").split("\\|")[0]+",";		
				}
			}

		   if (vchCodes ==null){
			   vchCodes="";
		   }
		} catch (Exception ex) {
			throw new YssException("获取允许出现多币种的凭证属性出错！");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return vchCodes;
    }
    
    /**
     * 获取划款指令的通用参数信息的方法 MS00018
     * @return Object
     * @throws YssException
     */
    public String getCommandPara() throws YssException {
        String sTitle = ""; //抬头下的说明信息
        String sShowTitle = "false"; //显示抬头为 不显示
        String sShowAssessor = "true"; //显示审核人为 显示
        String isShowNull ="true";//金额为0时是否为空 lidaolong 20110317  #746 托管行要求，当金额为0时，列表视图打印出来的指令单上金额为空不要显示“0”
        try {
            sTitle = (String) getParamParas("commandCashSet", "TxtComeupDesc", "<Desc>", "", 1);
            sShowTitle = (String) getParamParas("commandCashSet", "cboTitle", "<Title>", "", 2);
            sShowAssessor = (String) getParamParas("commandCashSet", "CboDesc", "<Check>", "", 2);
          //金额为0时是否为空 lidaolong 20110317  #746 托管行要求，当金额为0时，列表视图打印出来的指令单上金额为空不要显示“0”
            isShowNull = (String) getParamParas("commandCashSet", "showNull", "<showNull>", "", 2);
            //end add by lidaolong 
            if (sTitle == null) {
                sTitle = "";
            }
            if (sShowTitle != null) {
                if (sShowTitle.equals("1")) {
                    sShowTitle = "true";
                } else {
                    sShowTitle = "false";
                }
            } else {
                sShowTitle = "false";
            }
            if (sShowAssessor != null) {
                if (sShowAssessor.equals("1")) {
                    sShowAssessor = "true";
                } else {
                    sShowAssessor = "false";
                }
            } else {
                sShowAssessor = "true";
            }
          //金额为0时是否为空 lidaolong 20110317  #746 托管行要求，当金额为0时，列表视图打印出来的指令单上金额为空不要显示“0”
            //默认不显示‘0’
            if (isShowNull ==null){
            	isShowNull ="0";
            }
            //end by lidaolong
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return sTitle + "\t" + sShowTitle + "\t" + sShowAssessor +"\t"+isShowNull+ "\tnull";//edit by lidaolong    20110317  #746 托管行要求，当金额为0时，列表视图打印出来的指令单上金额为空不要显示“0”
    }

    /**
     * 重载的方法，使以前的方法能正确调用。
     * @param pubParaCode String
     * @param sCtlCode String
     * @param ctlInd String
     * @param params String
     * @param iParaType int
     * @return Object
     * @throws YssException
     */
    private Object getParamParas(String pubParaCode, String sCtlCode,
                                 String ctlInd, String params, int iParaType) throws
        YssException {
        return getParamParas(pubParaCode, sCtlCode, ctlInd, "", params, iParaType);
    }

    /**
     * 获取划款指令的通用参数信息的方法 MS00018
     * @param pubParaCode String 通用参数设置的代码
     * @param sCtlCode String 控件代码
     * @param ctlInd String 控件CTlInd值
     * @param params String 辅助参数，如组合控件取其一组合代码
     * @param iParaType int 控件类型 1:文件框 2：下拉框 3：日期控件 4：选择控件 0 label控件
     * @return Object 返回 参数值
     * @throws YssException
     */
    private Object getParamParas(String pubParaCode, String sCtlCode,
                                 String ctlInd, String resultCtlInd, String params, int iParaType) throws
        YssException {
        Object obj = null;
        ParaWithPubBean pubWithBean = null;
        pubWithBean = new ParaWithPubBean();
        pubWithBean.setYssPub(pub);
        pubWithBean.setPubParaCode(pubParaCode);
        pubWithBean.setCtlCode(sCtlCode);
        pubWithBean.setParams(params);
        pubWithBean.setCtlInd(ctlInd);
        //---------------------------------------
        pubWithBean.setResultCtlInd(resultCtlInd);
        //---------------------------------------
        if (iParaType == 1) { //文本框型控件
            obj = pubWithBean.getTextParaResult();
        } else if (iParaType == 2) { //下拉型控件
            obj = pubWithBean.getComboxParaResult();
        } else if (iParaType == 3) { //日期型控件
            obj = pubWithBean.getDateParaResult();
        } else if (iParaType == 4) { //选择型控件
            obj = pubWithBean.getSelectParaResult();
        } else {
            obj = "";
        }
        return obj;
    }

    /**
     * 日终处理 是否统计估值期货现金应收应付
     * @param portCode String 组合代码
     * @return boolean
     * @throws YssException
     * @version sunkey 20081124 BugID:MS00013
     */
    public boolean getIsFuturesRecPay(String portCode) throws YssException {
        String reStr = "";
        boolean bResult = false;
        try {
            portCode = portCode.replaceAll("'", "");
            reStr = parseResults(getParaWithPortValue(portCode, "cboYesOrNo",
                "IsFuturesRecPay")); //直接在这里输入参数编号和控件代号。
            if (reStr.equalsIgnoreCase("1")) {
                bResult = true;
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return bResult;
    }

    /**
     * 获取行情检查的天数
     * @return int 返回一个天数 默认是0
     * @throws YssException
     * @version sunkey 20081208 BugID:MS00051
     */
    public int getMktDayCount() throws YssException {
        ParaWithPubBean pubWithBean = new ParaWithPubBean();
        pubWithBean.setYssPub(pub);
        pubWithBean.setCtlCode("txtDays");
        pubWithBean.setPubParaCode("Market_Check");
        String tmp = (String) pubWithBean.getLatestParaValue();
        return Integer.parseInt(tmp.trim().length() <= 0 ? "0" : tmp);
    }

    /**
     * 获取财务估值表资产类合计、负债类合计计算的科目级别
     * 如果返回-1，代表没有设置科目级别
     * @param portCode 组合代码
     * @return int 科目级别
     * @throws YssException
     * @version sunkey 20081210 BugID:MS00072
     */
    public int getAcctLevel(String portCode) throws YssException {
        ParaWithPort pubWithPort = new ParaWithPort(); //通过组合获取参数值的对象
        pubWithPort.setYssPub(pub); //设置Pub
        pubWithPort.setPortCode(portCode); //设置组合编号
        pubWithPort.setPubParaCode("finish_ParaFVT"); //设置参数编号
        pubWithPort.setCtlCode("cboAcctLevel"); //设置控件编号
        String tmp = parseResults( (String) pubWithPort.getSpeParaResult()); //调用方法，获取值
        return tmp.equals("") ? -1 : Integer.parseInt(tmp); //如果没有控件值返回-1
    }

    /**
     * 获取财务估值表参数的方法
     * 参数:
     *     String portCode 组合编号
     *     String ctlCode  控件编号
     * @param portCode String 组合编号
     * @param ctlCode String  控件编号
     * @return Object  控件值
     * @throws YssException
     * @version sunkey 20081224 BugNO:MS00090
     */
    public Object getGuessValuePara(String portCode, String ctlCode) throws YssException {
        ParaWithPort pubWithPort = new ParaWithPort(); //通过组合获取参数值的对象
        pubWithPort.setYssPub(pub); //设置Pub
        pubWithPort.setPortCode(portCode); //设置组合编号
        pubWithPort.setPubParaCode("finish_ParaFVT"); //设置参数编号
        pubWithPort.setCtlCode(ctlCode); //设置控件编号
        String tmp = parseResults( (String) pubWithPort.getSpeParaResult()); //调用方法，获取值
        return tmp;
    }

    /**
     * 获取TA直销申购临时表中的数据
     * @param portCode String
     * @return boolean
     * @throws YssException
     * add by xuqiji 20090513:QDV4海富通2009年05月11日02_AB MS00441  为直销申购款计息时取数建立一张临时表
     */
    public Hashtable getTAInterestSource(String PortCodes) throws YssException {
        Hashtable htPortValue = new Hashtable();
        String sResult = "";
        String JoinPortCode1 = "";
        String JoinPortCode2 = "";
        String tableName = "";
        String[] portCode = null;
        try {
            portCode = PortCodes.split(",");
            for (int i = 0; i < portCode.length; i++) {
                ParaWithPort pubWithPort = new ParaWithPort(); //通过组合获取参数值的对象
                pubWithPort.setYssPub(pub); //设置Pub
                pubWithPort.setPortCode(portCode[i]); //设置组合编号
                pubWithPort.setPubParaCode("TAInterestSource"); //设置参数编号
                pubWithPort.setCtlCode("selTable"); //设置控件编号
                sResult = parseResults( (String) pubWithPort.getSpeParaResult()); //调用方法，获取值
                if (null != sResult && sResult.trim().length() > 0) { //判断该组合代码对应的表名
                    JoinPortCode1 += portCode[i] + ",";
                    tableName = sResult;
                } else {
                    JoinPortCode2 += portCode[i] + ",";
                }
            }
            if (!"".equals(tableName.trim())) {
                htPortValue.put(JoinPortCode1, tableName);
                htPortValue.put(JoinPortCode2, "");
            } else {
                htPortValue.put(JoinPortCode2, "");
            }
        } catch (Exception e) {
            throw new YssException("获取TA直销申购临时表中的数据出错！", e);
        }
        return htPortValue;
    }
	
	
    /**
     * 是否需新划款指令格式
     * @throws YssException
     * @return String
     * add by yanghaiming 20091110 MS00804 QDV4中金2009年11月10日01_B
     */
    public String getCommandCashStyle() throws YssException {
        String reStr = "";
        try {
        	
            reStr = getParaWithStyle("cboStyle", "commandCash_Style"); //直接在这里输入参数编号和控件编号。yanghaiming add 20091111
            if (reStr == null || reStr.length() == 0) {
                reStr = "0"; //默认设置为0，代表否，不打印英文
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
	
	/**
     * 是否需新划款指令格式
     * @param sCtlCode String
     * @param sPubParaCode String
     * @throws YssException
     * @return String
     * add by yanghaiming 20091110 MS00804 QDV4中金2009年11月10日01_B
     */
    private String getParaWithStyle(String sCtlCode,
    								String sPubParaCode) throws YssException{
    	try{
    		ParaWithStyle parawithstyle = new ParaWithStyle();
    		parawithstyle.setCtlCode(sCtlCode);
    		parawithstyle.setPubParaCode(sPubParaCode);
    		parawithstyle.setYssPub(pub);
    		return (String)parawithstyle.getSpeParaResult();
    	}catch(Exception e){
    		throw new YssException("以组合获取参数值出错！", e);
    	}
    }
    
    /**
     * 获取接口参数，组合所占比例
     * @param sPortCode 组合代码
     * @return	组合对应的比例
     * @author MS00817:QDV4工银2009年11月17日01_A sunkey@Modify 20091120
     * @throws YssException 
     */
    public Hashtable getPortPercent() throws YssException{
    	Hashtable htPortPercent = null;
    	
    	ParaWithPort paraPort = new ParaWithPort();
    	paraPort.setYssPub(pub);
    	paraPort.setPubParaCode("PubPortPercent"); //设置参数编号
    	htPortPercent = (Hashtable)paraPort.getParaResultAll();	//取得值
    	
    	return htPortPercent;
    }
    /**
     * 股指期货估值增值是否记入未实现损益平准金
     * @throws YssException
     * @return String
     * add by yanghaiming 20091118 MS00773 QDV4华夏2009年10月29日01_A
     */
    public String getTaIsCridet() throws YssException{
    	String reStr = "";
        try {
            reStr = getParaWithStyle("cboISCredit", "TA_IsCridet"); //直接在这里输入参数编号和控件编号。yanghaiming add 20091118
            if (reStr == null || reStr.length() == 0) {
                reStr = "0"; //默认设置为0，代表否
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }    
    /**
     * 股指期货估值增值是否记入未实现损益平准金
     * @throws YssException
     * @return String
     * add by yanghaiming 20091118 MS00773 QDV4华夏2009年10月29日01_A
     */
    public boolean isContain(String str) throws YssException{
    	String reStr = "";
    	String[] strs = null;
    	boolean b = false;
        try {
            reStr = getParaWithStyle("selPort", "TA_IsCridet"); //直接在这里输入参数编号和控件编号。yanghaiming add 20091118
            if (reStr != null && reStr.length() > 0) {
            	reStr = reStr.substring(0,reStr.indexOf("|"));
            	strs = reStr.split(",");
            	for(int i = 0; i < strs.length; i++){
            		if(strs[i].equalsIgnoreCase(str)){
            			b = true;
            			break;
            		}
            	}
            }
            return b;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
    
    /**
     * 2010-04-22 蒋锦 添加 期货保证金结转类型
     * 南方东英期权业务需求
     * MS01134 增加股票期权和股指期权业务
     * @param pubParaCode
     * @param sCtlCode
     * @param ctlInd
     * @return
     * @throws YssException
     */
    private Object getParaOptionBailType(String pubParaCode, String sCtlCode,
			String ctlInd) throws YssException {
		Object obj = null;
		ParaWithPubBean pubWithBean = null;
		pubWithBean = new ParaWithPubBean();
		pubWithBean.setYssPub(pub);
		pubWithBean.setPubParaCode(pubParaCode);
		pubWithBean.setCtlCode(sCtlCode);
		pubWithBean.setCtlInd(ctlInd);
		obj = pubWithBean.getSelectOpationBailType();
		return obj;
	}
    
    /**
     * 2010-04-22 蒋锦 添加 期权保证金结转类型
     * 南方东英期权业务需求
     * MS01134 增加股票期权和股指期权业务
     * @return
     * @throws YssException
     */
    public Hashtable getOptionBailCarryType(String sctlParam) throws YssException{
    	String sValue = "";
    	Hashtable htOption = new Hashtable();
    	try{
    		sValue = (String) getParaOptionBailType(sctlParam, "PortCode",
			"<Type>");
			if (sValue != null && sValue.length() > 0) {
				String[] temValue = sValue.split(",");
				for (int i = 0; i < temValue.length; i++) {
					String reValue = temValue[i];
					if (reValue != null && reValue.trim().length() > 0) {
						String[] reKey = reValue.split("[|]");
						if (htOption != null && htOption.containsKey(reKey[0])) {
							String oldValue = (String) htOption.get(reKey[0]);
							htOption.put(reKey[0], oldValue + "," + reKey[1]);
						} else {
							htOption.put(reKey[0], reKey[1]);
						}
					}
				}
			}
    	}catch(Exception ex){
    		throw new YssException("获取期权保证金结转类型出错！", ex);
    	}
    	return htOption;
    }
    
	// ===add by xuxuming,20091223.MS00886,无法用不同的方法对不同品种进行核算成本==========
	private Object getParaFurAccType(String pubParaCode, String sCtlCode,
			String ctlInd) throws YssException {
		Object obj = null;
		ParaWithPubBean pubWithBean = null;
		pubWithBean = new ParaWithPubBean();
		pubWithBean.setYssPub(pub);
		pubWithBean.setPubParaCode(pubParaCode);
		pubWithBean.setCtlCode(sCtlCode);
		pubWithBean.setCtlInd(ctlInd);
		obj = pubWithBean.getSelectFurAccType();
		return obj;
	}
	
	/**
	 * 获取股指期货核算方法，取代以前的获取方法。
	 * 20091228.MS00886.设置了多组合的情况，将核算方式和组合代码保存到HASHTABLE
	 * 如：Key:MODAVG  Value:001,002,003
	 * @return
	 * @throws YssException
	 */
	public Hashtable getFurAccountType(String accountType) throws YssException {
		String sValue = "";
		Hashtable htFutures = new Hashtable();
		try {
			sValue = (String) getParaFurAccType(accountType, "PortCode",
					"<Type>");  // modify by fangjiang 2010.08.25 
			if (sValue != null && sValue.length() > 0) {
				String[] temValue = sValue.split(",");
				for (int i = 0; i < temValue.length; i++) {
					String reValue = temValue[i];
					if (reValue != null && reValue.trim().length() > 0) {
						String[] reKey = reValue.split("[|]");// 如 MODAVG|001
						if(htFutures!=null&&htFutures.containsKey(reKey[0])){
							String oldValue = (String)htFutures.get(reKey[0]);
							htFutures.put(reKey[0], oldValue+","+reKey[1]);
						}else{
							htFutures.put(reKey[0], reKey[1]);// 将核算方法代码作为KEY，组合代码作为VALUE.这是为了和以前的方法兼容
						}
					}
				}
			}
		} catch (Exception ex) {
			throw new YssException("获取股指期货的核算方式参数出错", ex);
		}
		return htFutures;
	}
	// =============end ,xuxuming,20091223.===============================
	
	/*********************************************
     * 外汇估值汇率设置，默认取调拨日期汇率
     * 蒋世超 添加 2009.12.16
     * bugNo: MS00866  QDV4建行2009年12月14日01_AB 
     * @return
     * @throws YssException
     */
    public String getRateMode()throws YssException{
    	String reStr = "";
    	try {
            reStr = getParaWithStyle("rateMode", "RateTrade_mode"); //直接在这里输入参数编号和控件编号。 蒋世超  添加  2009.12.15
            if (reStr == null || reStr.length() == 0) {
                reStr = "1"; //默认设置为0，代表否
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
	    /**
     * 此方法获取权益比例设置通用参数
     * @param portCode String 组合代码
     * @return Object 返回设置的通用参数
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    public Object getRightsRatioMethods(String portCode) throws YssException {
        String tmp="";
        ParaWithPort pubWithPort=null;
        try{
            pubWithPort = new ParaWithPort(); //通过组合获取参数值的对象
            pubWithPort.setYssPub(pub); //设置Pub
            pubWithPort.setPortCode(portCode); //设置组合编号
            pubWithPort.setPubParaCode("RightsRatioMethods"); //设置参数编号
            pubWithPort.setCtlCode("cboRightsMethod"); //设置控件编号
            tmp = parseResults( (String) pubWithPort.getSpeParaResult()); //调用方法，获取值
        }catch(Exception e){
            throw new YssException("获取权益比例设置通用参数出错！",e);
        }
        return tmp;
    }
    
    /**
     * 此方法获取不同权益类型权益比例设置通用参数
     * @param portCode String 组合代码
     * @return Object 返回设置的通用参数
     * panjunfang add 20100507 B股业务
     */
    public String getBRightsRatioMethods(String portCode,String rightType) throws YssException {
        String strRightType = "";
        String strRightRatio = "";
        try {
        	strRightType = (String) getParamParas("BRightsRatioMethods", "rightType", "<Type>", "<Port>", portCode, 4);
        	strRightRatio = (String) getParamParas("BRightsRatioMethods", "cboRightsMethod", "<Result>", "<Port>", portCode, 2);
        	if(strRightType != null){
            	String sRightTypeCode = strRightType.split("\\|")[0];
            	String[] temp = sRightTypeCode.split(",");
            	for(int i =0;i<temp.length;i++){
            		if(temp[i].equals(rightType)){
            			return strRightRatio;
            		}
            	}       
        	} 	
        } catch (Exception e) {
            throw new YssException("获取通用参数-权益比例方式出错!");
        }
        return "";
    }
    
    /**
     * 此方法获取赎回费款的统计方法
     * @param portCode String 组合代码
     * @return Object 返回设置的通用参数
     * yeshenghong add 20130313
     * /**
     * 获取划款指令的通用参数信息的方法 MS00018
     * @param pubParaCode String 通用参数设置的代码
     * @param sCtlCode String 控件代码
     * @param ctlInd String 控件CTlInd值
     * @param params String 辅助参数，如组合控件取其一组合代码
     * @param iParaType int 控件类型 1:文件框 2：下拉框 3：日期控件 4：选择控件 0 label控件
     * @return Object 返回 参数值
     * @throws YssException
     *
     */
    public String getRedeemFeeMethod(String portCode) throws YssException {
        String strSepStatic = "";
        String strColumnType = "";
        try {
        	strSepStatic = (String) getParamParas("CtrlNavRedeemFee", "RedeemFee", "<RedeemFee>", "<portForPara>", portCode, 2);
        	strColumnType = (String) getParamParas("CtrlNavRedeemFee", "ColumnType", "<ColumnType>", "<portForPara>", portCode, 2);
        	if(strSepStatic != null&&strSepStatic.equals("1")){
            	return strColumnType;
        	} 	
        } catch (Exception e) {
            throw new YssException("获取赎回费款的统计方法出错!");
        }
        return "";
    }
    
    
    /**
     * xuqiji 20100312 MS01020  财务估值表和净值表需修改显示单位净值的方式  QDV4易方达2010年3月10日01_A  
     * @param portCode
     * @return
     * @throws YssException
     */
    public Hashtable getGuessValueShowSetData(String portCode) throws YssException{
    	Hashtable hmValueSetData = null;
    	ParaWithPort pubWithPort=null;
    	String tmp="";
    	try{
    		hmValueSetData = new Hashtable();
    		pubWithPort = new ParaWithPort();
    		pubWithPort.setYssPub(pub);
    		pubWithPort.setPortCode(portCode); //设置组合编号
    		pubWithPort.setPubParaCode("CtlGuessUnitPara"); //设置参数编号
    		pubWithPort.setCtlCode("CboAssetValue"); //设置控件编号
            tmp = parseResults( (String) pubWithPort.getSpeParaResult()); //调用方法，获取值
            if(tmp.trim().length() > 0){
            	hmValueSetData.put("CboAssetValue",tmp);
            }else{
            	hmValueSetData.put("CboAssetValue","true");
            }
            pubWithPort.setCtlCode("cboUnitValue"); //设置控件编号
            tmp = parseResults( (String) pubWithPort.getSpeParaResult()); //调用方法，获取值
            if(tmp.trim().length() > 0){
            	hmValueSetData.put("cboUnitValue",tmp);
            }else{
            	hmValueSetData.put("cboUnitValue","true");
            }
    	}catch (Exception e) {
			throw new YssException("获取财物估值表净值显示信息设置出错！",e);
		}
    	return hmValueSetData;
    }
    
    
	
	  /*********************************************
     * 证券估增和汇兑损益计算本币市值时精确到4位有效数字
     * 蒋世超 添加 2010.03.26
     * bugNo: MS01021   QDV4南方2010年3月11日01_B 财务估值表里的净值与余额表里的成本加估值增值的和有尾差
     * @return
     * @throws YssException
     */
    public String getSecRecRound()throws YssException{
    	String reStr = "";
    	try {
            reStr = getParaWithStyle("cboRound4", "dayfinish_secrec"); //直接在这里输入参数编号和控件编号。 蒋世超  添加  2009.12.15
            if (reStr == null || reStr.length() == 0) {
                reStr = "0"; //默认设置为0，代表否
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
    /********************************************************
     * jiangshichao 2010.04.23  QDV4银华2010年04月21日01_A
     *  股票持仓检查时是否包括未交割部分，默认股票持仓包含未交割部分
     * @return
     * @throws YssException
     */
    public String getEQCheckMode()throws YssException{
    	String reStr = "";
    	try {
            reStr = getParaWithStyle("ctlCheckEQ", "finish_EQcheck"); //直接在这里输入参数编号和控件编号。 蒋世超  添加  2009.12.15
            if (reStr == null || reStr.length() == 0) {
                reStr = "1"; //默认设置为1，默认包含未交割数量
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
    /**
     * yanghaiming 20100412 MS00945  QDV4工银2010年1月19日01_A  
     * @return String
     * @throws YssException
     */
    public String getFMIsCalcn() throws YssException{
    	String reStr = "";
    	try {
            reStr = getParaWithStyle("cboFMIsCalcn", "FMIsCalcn");
            if (reStr == null || reStr.length() == 0) {
                reStr = "1"; //默认设置为1，代表否
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
    
    /********************************************************
     * jiangshichao 20100525 MS01160  QDV4招商基金2010年5月6日01_A
     * @return String
     * @throws YssException
     */
     public String getGuessValueShowGZ() throws YssException{
    	String reStr = "";
    	try {
            reStr = getParaWithStyle("cboYesOrNo", "finish_GrpShwoGz");
            if (reStr == null || reStr.length() == 0) {
                reStr = "0"; //默认设置为1，代表否
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
	/**
     * yanghaiming 20100428 B股业务 判断是否显示B股个性化界面  
     * @return String
     * @throws YssException
     */
    public String getPerInterface() throws YssException{
    	String reStr = "";
    	try {
            reStr = getParaWithStyle("cboPerInterface", "bshare_perInterface");
            if (reStr == null || reStr.length() == 0) {
                reStr = "1"; //默认设置为1，代表否
            } else {
                reStr = parseResults(reStr);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
    
    /**
     * yanghaiming 20100621 B股业务 判断是否启用新划款指令格式
     * MS01302 QDV4长盛2010年06月02日01_A
     * @return String
     * @throws YssException
     */
    public String getNewCommandCashStyle() throws YssException{
    	String reStr = "";
    	try{
    		//reStr = getParaWithStyle("txtPlanDate", "commandCash_NewStyle");// modify huangqirong 2013-04-22 bug #7476 控件取值有问题
    		reStr = getParaWithStyle("cboNewStyle", "commandCash_NewStyle");  // modify huangqirong 2013-04-22 bug #7476 控件取值有问题
    		if (reStr == null || reStr.length() == 0) {
    			reStr = "0"; //默认设置为0，代表否
    		}else {
    			reStr = parseResults(reStr);
    		}
    		return reStr;
    	} catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
    /**
     * yanghaiming 20100705 MS01229 QDV4赢时胜(上海)2010年06月02日02_A
     * MS01302 QDV4长盛2010年06月02日01_A
     * @return String
     * @throws YssException
     */
    public int getPlanDate() throws YssException {
    	String reStr = "";
    	try{
    		reStr = getParaWithStyle("cboNewStyle", "BSettingPlanDate");
    		if (reStr == null || reStr.length() == 0) {
    			reStr = "0"; //默认设置为0，代表否
    		}else {
    			reStr = parseResults(reStr);
    		}
    		return Integer.parseInt(reStr);
    	} catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
	
	/*******************************************************
 *  获取资金调拨流出汇率设置 合并太平版本代码
 * bugNo. MS00849 QDV4中保2009年12月07日01_A 
 * @author 蒋世超 添加 2009.12.11 
 * @return
 * @throws YssException
 */
  public String getOutRatePara() throws YssException {
      String para = "";
      try {
         para =(String)getParamParas("cashCommond_OutRate","avgYesOrNo", "<Check>","",2);//直接在这里输入参数编号和控件代号。
         if (para == null || para.length() == 0) {
             para = "0";  //默认按照交易日汇率
         }
         return para;
      }
      catch (Exception ex) {
         throw new YssException(ex.toString());
      }
   } 
  
  
  /*******************************************************
   *  获取现金库存统计设置 合并太平版本代码
   * bugNo. MS00849 QDV4中保2009年12月07日01_A 
   * @author 蒋世超 添加 2009.12.11 
   * @return
   * @throws YssException
   */
    public String getStgCashPara() throws YssException {
        String para = "";
        try {
           para =(String)getParamParas("Para_StgCashSet","Para_StgCash", "<YesOrNo>","",2);//直接在这里输入参数编号和控件代号。
           if (para == null || para.length() == 0) {
               para = "0";  //默认按照交易日汇率
           }
           return para;
        }
        catch (Exception ex) {
           throw new YssException(ex.toString());
        }
     } 
    
    /**
     * 财务估值表计算本位币市值保留位数设置
     * 若返回“是”，则汇率参与计算时保留13位，否则与原有一致即保留15位小数。
     * QDV4交银施罗德2010年07月30日01_B
     * panjunfang add 20100731
     * @param portCode
     * @return
     * @throws YssException
     */
    public String getExactGuessValue(String portCode) throws YssException {
        String tmp="";
        ParaWithPort pubWithPort=null;
        try{
            pubWithPort = new ParaWithPort(); //通过组合获取参数值的对象
            pubWithPort.setYssPub(pub); //设置Pub
            pubWithPort.setPortCode(portCode); //设置组合编号
            pubWithPort.setPubParaCode("ExactGuessValue"); //设置参数编号
            pubWithPort.setCtlCode("cboYesOrNo"); //设置控件编号
            tmp = parseResults( (String) pubWithPort.getSpeParaResult()); //调用方法，获取值
        }catch(Exception e){
            throw new YssException("获取精度计算财务估值表通用参数出错！",e);
        }
        return tmp;
    }
	/*******************************************************
     *  获取应收应付库存统计设置 合并太平版本代码
     * bugNo. MS00849 QDV4中保2009年12月07日01_A 
     * @author 蒋世超 添加 2010.01.18 
     * @return
     * @throws YssException
     */
      public String getRecPayPara() throws YssException {
          String para = "";
          try {
             para =(String)getParamParas("Para_RecPaySet","Para_RecPay", "<YesOrNo>","",2);//直接在这里输入参数编号和控件代号。
             if (para == null || para.length() == 0) {
                 para = "0";  //默认按照交易日汇率
             }
             return para;
          }
          catch (Exception ex) {
             throw new YssException(ex.toString());
          }
       } 
      /***获取外汇交易中基础金额的计算方式 是否采用新算法计算金额
       * @author zhouxiang MS01612
       * @param 为方便重用代码，此处传入： TB_001_PFOper_PUBPARA 通用参数类型设定中的参数编号：FPubParaCode 
       * @param 控件类型：FParaId=1 ，控件下要取的名称 ComboBox1 
       */
      public String getRateCalculateType (String FPubParaCode,String ComboBox1, int  FParaId )throws YssException{
    	  String para="";
    	  try {
              para =(String)getParamParas(FPubParaCode,ComboBox1, " ","",FParaId);//直接在这里输入参数编号FPubParaCode和控件代号名称：ComboBox1 类型为FParaId。
              if (para == null || para.length() == 0) {
                  para = "1,1";  //默认按照结算日汇率
              }
              return para;
           }
           catch (Exception ex) {
              throw new YssException(ex.toString());
           }
    	  
      } 
      
      /**
       * yanghaiming 20101109 QDV4华宝2010年10月08日01_AB
       * @return String
       * @throws YssException
       */
      public boolean getIsUseAmount(String portCode) throws YssException{
    	  String reStr = "";
          boolean bResult = false;
          try {
              portCode = portCode.replaceAll("'", "");
              reStr = parseResults(getParaWithPortValue(portCode, "cboYesOrNo",
                  "finish_IsUseAmount")); //直接在这里输入参数编号和控件代号。
              if (reStr.equalsIgnoreCase("1")) {
                  bResult = true;
              }
          } catch (Exception e) {
              throw new YssException(e.getMessage());
          }
          return bResult;
      }
      
      /**
       * yanghaiming 20101206 
       * 成本计算方式
       * @return boolean
       * @throws YssException
       */
      public boolean getCalculatCost(String portCode) throws YssException {
          String reStr = "";
          boolean bResult = false;
          try {
              portCode = portCode.replaceAll("'", "");
              reStr = parseResults(getParaWithPortValue(portCode, "cboCalculatMode",
                  "CalculatCost")); //直接在这里输入参数编号和控件代号。
              if (reStr.equalsIgnoreCase("FIFO")) {
                  bResult = true;
              }
          } catch (Exception e) {
              //throw new YssException(e.getMessage());
              throw new YssException(e); //by caocheng 2009.02.05 MS00004 QDV4.1-2009.2.1_09A 异常处理
          }
          return bResult;
      }
      
      /**
       * 在计算估值增值汇兑损益时，市值计算过程中间是否保留位数，即本位币市值 = round[数量*价格*基础汇率/组合汇率,2]
       * @return boolean
       * @throws YssException
       */
      public boolean getMValueRoundOfCalFX() throws YssException {
          String reStr = "";
          boolean bResult = false;
          try {
        	  MValueRoundOfCalFX mv = new MValueRoundOfCalFX();
              mv.setYssPub(pub);
              reStr = parseResults( (String) mv.getParaResult());
              if (reStr.equalsIgnoreCase("1")) {
                  bResult = true;
              }
          } catch (Exception e) {
              throw new YssException(e.getMessage());
          }
          return bResult;
      }
      
      /**
       * #1040 目前KB QFII组合存在频繁的缴款入账，无标准接口数据，需开同相应功能。 
       * 此通用参数只针对嘉实基金 
       *  add by jiangshichao 2011.05.16
       * @return boolean
       * @throws YssException
       */
      public boolean getIncomeBalMode(String portCode) throws YssException {
    	  String reStr = "";
          boolean bResult = false;
          try {
              portCode = portCode.replaceAll("'", "");
              reStr = parseResults(getParaWithPortValue(portCode, "cboIncomeBalMode",
                  "CtlIncomeBal")); //直接在这里输入参数编号和控件代号。
              if (reStr.equalsIgnoreCase("1")) {
                  bResult = true;
              }
          } catch (Exception e) {
             throw new YssException(e);
          }
          return bResult;
      }
      
      
      /**
       * 财务估值表停牌信息颜色设置。通过此方法，向前台返回“财务估值表停牌信息颜色设置”参数的参数值
       *  added by liubo 2011.06.03
       * @return String
       * @throws YssException
       */
      public String getGVColor(String sSetCode) throws YssException {
          String strResult = "";
          try {
        	//20110620 added by liubo.Story #1132
        	//通过从前台传过来的套账号，获取投资组合代码。然后查询改组合代码是否有设置停牌颜色规则，若没有设置，直接返回-1
        	//-------------------------------------
        	  YssFinance finance = new YssFinance(); //new 一个YssFinance类，这个类中有一个专门通过套账代码查组合代码的方法
              finance.setYssPub(pub); //设置公共信息
              String sPortCode = finance.getPortCode(sSetCode); //调用方法得到组合代码
              
              String sParaID = getParaWithPortValue(sPortCode,"GVColor");
             
              if (sParaID == null || sParaID.length() == 0)
              {
            	  strResult = "-1";            	  
              }
            //---------------end-------------------------
              else
              {
            	  //20110620 modified by liubo.Story #1132
            	  //停牌颜色信息参数增加了投资组合的选项，因此可能会有多个参数同时存在。在取颜色的时候，就需要添加ParaID作为划分不同参数的条件
            	  //************************************
	              strResult = getParaWithParaID("<Color>","GVColor",sParaID);
	              
	              //20110617 modified by liubo.Story #1132
	              //修改后的财务估值表颜色参数，有两个选项，一个是“是否启用”(<Color>)，一个是“选择颜色种类”(<ColorType>)
	              //当<Color>选项选择为是时，就需要将<ColorType>的值返回给前台，以启用估值表的颜色规则。若选择为否，或无该参数，则返回“-1”，前台将不会启用规则
	              //------------------------------------------
	              
	              if (strResult == null || strResult.length() == 0 || strResult.equals("0")) {
	                  strResult = "-1";
	              } else if (strResult.equals("1"))
	              {
	            	  strResult = getParaWithParaID("<ColorType>","GVColor",sParaID);
	              }
	              //******************end*********************
              }
              //--------------end---------------
              
              return strResult;
          } catch (Exception ex) {
              throw new YssException(ex.toString());
          }
      }     
      
      /**
       * 财务估值表财务估值表统计项显示设置。通过此方法，
       * 向前台返回“财务估值表统计项显示设置”
       * 是否显示的参数值
       *  add by baopingping 2011.06.10
       * @return String
       * @throws YssException
       */
      public  String getvalue(String portSel,String cbovalue,String ProcodeId)throws YssException {
    	
    	ParaWithPubBean para = new ParaWithPubBean();
	    para.setYssPub(pub);
    	ResultSet rs = null;
    	ResultSet rsSet=null;
        String value="0";
  		try {
  	    	rs=para.getResultSetByLike("CtlGuessUnitPara", portSel,ProcodeId+"%", null);
  			while(rs.next()){
  				rsSet=para.getResultSetByLike("CtlGuessUnitPara", cbovalue, "%,%", rs.getString("FParaId"));
  				if(rsSet.next()){
	  				String FCtlValue = rsSet.getString("FCtlValue");
	  			    value=FCtlValue.substring(0,1).trim();
	  			    //return value;
  				}
  				dbl.closeResultSetFinal(rsSet);//baopingping 2011.06.10
  			}
  			return value;  
  		} catch (Exception e1) {
  			// TODO Auto-generated catch block
  			throw new YssException("查询估值统计表业务参数出错！");
  		}finally{
  			try{
  				dbl.closeResultSetFinal(rs, rsSet);
  	  			if(rs !=null){
  	  				rs.close();
  	  				rs = null;
  	  			}
  	  			if(rsSet !=null){
  	  				rsSet.close();
  	  				rsSet = null;
  	  			}
  			}catch(Exception e){
  				e.getMessage();
  			}
  			
  		}
  		
  	}
      
      /**
       * 财务估值表财务估值表统计项显示设置。通过此方法，
       * 向前台返回“财务估值表统计项显示设置”
       * 是否显示的参数值  #1562 不显示减值准备
       *  add by yeshenghong 2011.11.09
       * @return String
       * @throws YssException
       */
      public  String getShowValueSetting(String cbovalue)throws YssException {
    	
    	ParaWithPubBean para = new ParaWithPubBean();
	    para.setYssPub(pub);
    	ResultSet rs = null;
        String value="0";
  		try {
  			String sqlStr = "select a.* from  " + pub.yssGetTableName("Tb_Pfoper_Pubpara")+                 
			" a where a.FCtlGrpCode = 'CtlGuessUnitPara' AND a.FCtlCode =" + dbl.sqlString(cbovalue);

	       rs = dbl.openResultSet(sqlStr,ResultSet.TYPE_SCROLL_INSENSITIVE);
	
  			while(rs.next()){
	  				String FCtlValue = rs.getString("FCTLVALUE");
	  			    value=FCtlValue.substring(0,1).trim();
	  			    //return value;
  			}
  			return value;  
  		} catch (Exception e1) {
  			// TODO Auto-generated catch block
  			throw new YssException("查询估值统计表业务参数出错！");
  		}finally{
  			try{
  				dbl.closeResultSetFinal(rs);
  			}catch(Exception e){
  				e.getMessage();
  			}
  			
  		}
  		
  	}
  	
      public  String getvalueOfPorfile(String portSel,String cbovalue,String ProcodeId)throws YssException {
      	
      	ParaWithPubBean para = new ParaWithPubBean();
  	    para.setYssPub(pub);
  	    String strSql  = "";
      	ResultSet rs = null;
      	ResultSet rsSet=null;
          String value="0";
    		try {
    	    	strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
						 " WHERE FCtlGrpCode = 'CtlGuessUnitPara' " +
						 " AND FCtlCode = '" + portSel + "' " + 
						 " AND FCtlValue LIKE '%' " ;
    	    	rs = dbl.openResultSet(strSql);
    			while(rs.next()){
    				strSql  = "SELECT * FROM " +pub.yssGetTableName("Tb_Pfoper_Pubpara") +
							  " WHERE FCtlGrpCode = 'CtlGuessUnitPara' " +
							  " AND FCtlCode = '" + cbovalue + "' " +
							  " AND FCtlValue LIKE '%,%' " +
							  " AND FParaId = '" + rs.getString("FParaId") + "'" ;
    				rsSet = dbl.openResultSet(strSql);
    				if(rsSet.next()){
  	  				String FCtlValue = rsSet.getString("FCtlValue");
  	  			    value=FCtlValue.substring(0,1).trim();
  	  			    return value;
    				}
    				dbl.closeResultSetFinal(rsSet);
    			}
    		} catch (Exception e1) {
    			// TODO Auto-generated catch block
    			throw new YssException("查询估值统计表业务参数出错！");
    		}finally{
    			dbl.closeResultSetFinal(rsSet);
    			dbl.closeResultSetFinal(rs);
    		}
    		return value;  
    	}
      
      
      /**
       * add by fangjiang 2011.07.08 STORY #1280
       * @return
       * @throws YssException
       */
      public String getRateTradeMode1() throws YssException {
          String reStr = "";
          try {
              reStr = parseResults(getNavType("CtlOutMoney",
                                              "ParaOutAcc")); //直接在这里输入参数编号和控件代号。
              return reStr;
          } catch (Exception e) {
              throw new YssException(e.getMessage(), e);
          }

      }
      
      /**
       * add by fangjiang 2011.07.08 STORY #1280
       * @return
       * @throws YssException
       */
      public String getRateTradeMode2() throws YssException {
          String reStr = "";
          try {
              reStr = parseResults(getNavType("CtlToMoney",
                                              "ParaToAcc")); //直接在这里输入参数编号和控件代号。
              return reStr;
          } catch (Exception e) {
              throw new YssException(e.getMessage(), e);
          }

      }
      
      /**
       * add by fangjiang 2011.07.13 STORY #1291
       * @return
       * @throws YssException
       */
      public boolean getParaValue(
				    		  String FCtlGrpCode, 
				    		  String FCtlCode1, 
				    		  String FCtlCode2, 
				    		  String FCtlValue) throws YssException, SQLException{	  
    	  ParaWithPubBean pubBean = new ParaWithPubBean();
          pubBean.setYssPub(pub);
          ResultSet rs = null;
          ResultSet rs1 = null;
          String paraId = "";
          boolean flag = false;
          try {
        	  rs = pubBean.getResultSetByLike(FCtlGrpCode, FCtlCode1, FCtlValue + "%", null);
          	  if (rs.next()) {
          		  paraId = rs.getString("FParaId");
          		  rs1 = pubBean.getResultSetByLike(FCtlGrpCode, FCtlCode2, "1,1", paraId);
            	  if (rs1.next()) {
            		  flag = true;
            	  }
  			  }        	  
  		  } catch (YssException e) {
  			  dbl.closeResultSetFinal(rs);
  			  dbl.closeResultSetFinal(rs1);
  		  } finally{
  			  dbl.closeResultSetFinal(rs);
  			  dbl.closeResultSetFinal(rs1);
  		  }	  
  		  return flag;
      }
      
      public  String getProfitsParavalue(String portSel,String cbovalue,String ProcodeId)throws YssException {
      	
      	ParaWithPubBean para = new ParaWithPubBean();
  	    para.setYssPub(pub);
      	ResultSet rs = null;
      	ResultSet rsSet=null;
          String value="0";
    		try {
    	    	rs=para.getResultSetByLike("CtlGuessUnitPara", portSel,ProcodeId+"%", null);
    			while(rs.next()){
    				rsSet=para.getResultSetByLike("CtlGuessUnitPara", cbovalue, "%,%", rs.getString("FParaId"));
    				if(rsSet.next()){
  	  				String FCtlValue = rsSet.getString("FCtlValue");
  	  			    value=FCtlValue.substring(0,1).trim();
  	  			    return value;
    				}
    			}
    		} catch (Exception e1) {
    			// TODO Auto-generated catch block
    			throw new YssException("查询估值统计表业务参数出错！");
    		}finally{
    			if(rs!=null)
    				try {
    					rs.getStatement().close();
    					if(rsSet!=null){
    						rsSet.close();
    					}
    				} catch (SQLException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    		}
    		return value;  
    	}
      
   /**
     * story 2253 add by zhouwei 20120222
     * 根据通参分类计算方式来查询组合的分级核算方式信息
     * @param portCode
     * @return
     * @throws YssException
     */
    public HashMap getClassAccMethod() throws YssException{
    	  ResultSet rs=null;
    	  String strSql="";
    	  Map portClassMap=new HashMap();
    	  try{
    		  strSql="select a.fctlvalue as port,b.fctlvalue as clsPort,c.fctlvalue as accway,a.fparaid from "
    				+"  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='paraClassAccMethod'"
    				+" and fparaid<>0 and fctlcode='selPort' order by fparaid asc) a left join "
    				+" (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='paraClassAccMethod'  and fctlcode='selClsPort') b"
    				+" on a.fparaid=b.fparaid  left join (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")
    				+" where fpubparacode='paraClassAccMethod'  and fctlcode='cboCheck') c on a.fparaid=c.fparaid";
    		  rs=dbl.openResultSet(strSql);
    		  while(rs.next()){
    			  String key=rs.getString("port").split("[|]")[0];//组合
    			  String value=rs.getString("accway").split(",")[0]+"\f\t"+ //核算类型：inBasicNetValue 基准份额（如嘉实），inNetValue 净值（如博时），默认
    			               ("|".equalsIgnoreCase(rs.getString("clsPort")) ? "" :rs.getString("clsPort").split("[|]")[0]);//核算类型 与 分级组合，
    			  portClassMap.put(key,value);
    		  }
    	  }catch (Exception ex) {
			throw new YssException("获取分级核算方式出错！", ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	  return (HashMap) portClassMap;
      }
      
    /**
     * add by zhouwei 20120301
     * 获取资本利得税结转方式
     * @param portCode
     * @return
     * @throws YssException
     */
    public boolean  getCgtCarryWay(String portCode) throws YssException{
  	  ResultSet rs=null;
  	  String strSql="";
  	boolean reStr=false;
  	  try{
  		  strSql="select a.fctlvalue as port,b.fctlvalue as checkValue,a.fparaid from "
  				+"   (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='paraCgtCarryWay'"
  				+"  and fparaid<>0 and fctlcode='selPort' order by fparaid asc) a left join "
  				+"  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='paraCgtCarryWay'  and fctlcode='cboYesOrNo')  b"
  				+"  on a.fparaid=b.fparaid ";  	
  		  rs=dbl.openResultSet(strSql);
  		  while(rs.next()){
  			  String key=rs.getString("port").split("[|]")[0];//组合
  			  String value=rs.getString("checkValue").split(",")[0];//是否结转 0 不  1是
  			  if(key!=null && key.equalsIgnoreCase(portCode)){
  				  if(value!=null && value.equals("1")){
  					  reStr=true;
  				  }else{
  					  reStr=false;
  				  }
  			  }
  		  }
  	  }catch (Exception ex) {
			throw new YssException("获取分级核算方式出错！", ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
  	  return reStr;
    }
    
    
    /**
     * add by jsc 20120307
     * 获取财务估值表辅助核算项设置
     * @param portCode
     * @return
     * @throws YssException
     */
    public boolean  getGuessUseAuxiaccSet(String portCode) throws YssException{
  	  ResultSet rs=null;
  	  String strSql="";
  	boolean reStr=true;
  	  try{
  		  strSql="select a.fctlvalue as port,b.fctlvalue as checkValue,a.fparaid from "
  				+"   (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='GuessUseAuxiacc'"
  				+"  and fparaid<>0 and fctlcode='selPort' order by fparaid asc) a left join "
  				+"  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='GuessUseAuxiacc'  and fctlcode='cboYesOrNo')  b"
  				+"  on a.fparaid=b.fparaid ";  	
  		  rs=dbl.openResultSet(strSql);
  		  while(rs.next()){
  			  String key=rs.getString("port").split("[|]")[0];//组合
  			  String value=rs.getString("checkValue").split(",")[0];//使用辅助核算项 0 否  1是
  			  if(key!=null && key.equalsIgnoreCase(portCode)){
  				  if(value!=null && value.equals("0")){
  					  reStr=false;
  				  }else{
  					  reStr=true;
  				  }
  			  }
  		  }
  	  }catch (Exception ex) {
			throw new YssException("获取财务报表辅助核算项出错！", ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
  	  return reStr;
    }
    
    /**
     * add by zhouwei 20120313
     * 获取组合分级估值增值计算方式,分为财务系统和估值系统的算法,默认为估值系统
     * 用于TA损益平准金的计算
     * @param portCode
     * @return
     * @throws YssException
     */
    public String getClsGzzzWay(String portCode) throws YssException{
  	  ResultSet rs=null;
  	  String strSql="";
//  	  Map portClassMap=new HashMap();
  	  String restr="values";
  	  try{
  		  strSql="select a.fctlvalue as port,b.fctlvalue as gzzzway,a.fparaid from "
  				+"  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='paraClsGzzzWay'"
  				+" and fparaid<>0 and fctlcode='selPort' order by fparaid asc) a  "
  				+" left join (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")
  				+" where fpubparacode='paraClsGzzzWay'  and fctlcode='cboCheck') b on a.fparaid=b.fparaid";
  		  rs=dbl.openResultSet(strSql);
  		  while(rs.next()){
  			  String key=rs.getString("port").split("[|]")[0];//组合
  			  if(key.equalsIgnoreCase(portCode)){
  				restr=rs.getString("gzzzway").split(",")[0];//计算方式
  			  }
  		  }
  	  }catch (Exception ex) {
			throw new YssException("获取组合分级估值增值计算方式出错！", ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
  	  return restr;
    }
    /**
     * add by zhouwei 20120322
     * 控制保留的位数 以及是否进行 四舍五入的操作
     * @param portCode
     * @return
     * @throws YssException
     */
    public String getDigitsCalMethod(String portCode) throws YssException{
    	  ResultSet rs=null;
    	  String strSql="";
//    	  Map portClassMap=new HashMap();
    	  String restr="-1\t3";//默认为截位和保留3位
    	  try{
    		  strSql="select a.fctlvalue as portCode,b.fctlvalue as resDigites,c.fctlvalue as roundMethod,a.fparaid from "
    				+"  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='paraGusDigCalMethod'"
    				+" and fparaid<>0 and fctlcode='portCode' order by fparaid asc) a  "
    				+" left join (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")
    				+" where fpubparacode='paraGusDigCalMethod'  and fctlcode='resDigites') b on a.fparaid=b.fparaid"
    				+" left join (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")
    				+" where fpubparacode='paraGusDigCalMethod'  and fctlcode='roundMethod') c on a.fparaid=c.fparaid";
    		  rs=dbl.openResultSet(strSql);
    		  while(rs.next()){
    			  String key=rs.getString("portCode").split("[|]")[0];//组合
    			  if(key.equalsIgnoreCase(portCode)){
    				restr=rs.getString("roundMethod").split(",")[0]+"\t"+rs.getString("resDigites");//计算方式  保留位数
    			  }
    		  }
    	  }catch (Exception ex) {
  			throw new YssException("获取位数计算方式出错！", ex);
  		}finally{
  			dbl.closeResultSetFinal(rs);
  		}
    	  return restr;
      }
    /** 
     * add by zhouwei 20120401 
     * 获取证券的的比率公式和舍入条件，期间代码的hashmap
    * @Title: getMapOfSecManageFeeRate 
    * @Description: TODO
    * @param @param portCode
    * @param @return
    * @param @throws YssException    设定文件 
    * @return Map    返回类型 
    * @throws 
    */
    public Map getMapOfSecManageFeeSet(String portCode,ArrayList secList) throws YssException{
  	  ResultSet rs=null;
  	  String strSql="";
//  	  Map portClassMap=new HashMap();
  	  Map secFeeRateMap=new HashMap();
  	  try{
  		  String key="";
  		  String perExpCode="";//比率公式
  		  String roundCode="";//舍入条件
  		  String periodCode="";//期间代码
  		  String IVPayCatCode="";//收支品种
  		  strSql="select a.fctlvalue as portCode,b.fctlvalue as securityCode,c.fctlvalue as perExpCode,"
  			    +"d.fctlvalue as roundCode,e.fctlvalue as periodCode,f.fctlvalue as IVPayCatCode,a.fparaid from "
  				+"  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='paraSecMangageFee'"
  				+" and fparaid<>0 and fctlcode='portCode' order by fparaid asc) a  "
  				+" left join (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")
  				+" where fpubparacode='paraSecMangageFee'  and fctlcode='securityCode') b on a.fparaid=b.fparaid"
  				+" left join (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")
  				+" where fpubparacode='paraSecMangageFee'  and fctlcode='perExpCode') c on a.fparaid=c.fparaid"
  				+" left join (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")
  				+" where fpubparacode='paraSecMangageFee'  and fctlcode='roundCode') d on a.fparaid=d.fparaid"
  				+" left join (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")
  				+" where fpubparacode='paraSecMangageFee'  and fctlcode='periodCode') e on a.fparaid=e.fparaid"
  				+" left join (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")
  				+" where fpubparacode='paraSecMangageFee'  and fctlcode='IVPayCatCode') f on a.fparaid=f.fparaid";
  		  rs=dbl.openResultSet(strSql);
  		  while(rs.next()){
  			  if((rs.getString("portCode").split("[|]")[0]).equalsIgnoreCase(portCode)){
  				  key=rs.getString("securityCode").split("[|]")[0];
  				  perExpCode=rs.getString("perExpCode").split("[|]")[0];
  				  roundCode=rs.getString("roundCode").split("[|]")[0];
  				  periodCode=rs.getString("periodCode").split("[|]")[0];
  				  IVPayCatCode=rs.getString("IVPayCatCode").split("[|]")[0];
  				  if(!secFeeRateMap.containsKey(key)){
  					  secFeeRateMap.put(key, perExpCode+"\t"+roundCode+"\t"+periodCode+"\t"+IVPayCatCode);
  					  secList.add(key);
  				  }
  			  }
  		  }
  	  }catch (Exception ex) {
			throw new YssException("获取证券管理费设置出错！", ex);
	  }finally{
			dbl.closeResultSetFinal(rs);
	  }
  	  return secFeeRateMap;
    }
    /** 
     * add by zhouwei 20120416 新债中签利息是否入成本
    * @Title: getCostIncludeInterestsOfZQ 
    * @Description: TODO
    * @param @param portCode
    * @param @return
    * @param @throws YssException    设定文件 
    * @return boolean    返回类型 
    * @throws 
    */
    public boolean getCostIncludeInterestsOfZQ(String portCode) throws YssException{
    	  ResultSet rs=null;
    	  String strSql="";
    	  boolean reStr=false;
    	  try{
    		  strSql="select a.fctlvalue as port,b.fctlvalue as checkValue,a.fparaid from "
    				+"   (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='costIncludeIntsZQ'"
    				+"  and fparaid<>0 and fctlcode='selPort' order by fparaid asc) a left join "
    				+"  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='costIncludeIntsZQ'  and fctlcode='cboYesOrNo')  b"
    				+"  on a.fparaid=b.fparaid ";  	
    		  rs=dbl.openResultSet(strSql);
    		  while(rs.next()){
    			  String key=rs.getString("port").split("[|]")[0];//组合
    			  String value=rs.getString("checkValue").split(",")[0];// 0 不  1是
    			  if(key!=null && key.equalsIgnoreCase(portCode)){
    				  if(value!=null && value.equals("1")){
    					  reStr=true;
    				  }else{
    					  reStr=false;
    				  }
    			  }
    		  }
    	  }catch (Exception ex) {
  			throw new YssException("获取新债中签成本是否包含利息出错！", ex);
  		}finally{
  			dbl.closeResultSetFinal(rs);
  		}
    	  return reStr;
      }
    
    /** 
     * add by guolongchao 20120503 博时每次资产估值时提固定一笔费用(根据开放日提)
     */
    public String getSecValFee(String portCode) throws YssException{
  	  ResultSet rs=null;
  	  String strSql="";  	 
  	  try{  		 
  		  strSql=" select a.fparaid,a.fctlvalue as portCode,b.fctlvalue as IVPayCatCode,c.fctlvalue as holiday,d.fctlvalue as je,e.fctlvalue as curyCode "+
  	             " from "+
  	             " (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='SecValFee' and fctlcode='portCode') a "+
  	             " left join  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='SecValFee' and fctlcode='IVPayCatCode') b on a.fparaid=b.fparaid "+
  	             " left join  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='SecValFee' and fctlcode='holiday') c on a.fparaid=c.fparaid "+
  	             " left join  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='SecValFee' and fctlcode='je') d on a.fparaid=d.fparaid "+
  	             " left join  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='SecValFee' and fctlcode='curycode') e on a.fparaid=e.fparaid "+
  	             " order by a.fparaid asc ";
  		  rs=dbl.openResultSet(strSql);
  		  while(rs.next()){
  			  if((rs.getString("portCode").split("[|]")[0]).equalsIgnoreCase(portCode)){
  				return rs.getString("portCode").split("[|]")[0]+"\t"+
  				       rs.getString("IVPayCatCode").split("[|]")[0]+"\t"+
  				       rs.getString("holiday").split("[|]")[0]+"\t"+
  				       rs.getString("je").split("[|]")[0]+"\t"+
  				       rs.getString("curyCode").split("[|]")[0];
  			  }
  		  }
  	  }catch (Exception ex) {
			throw new YssException("博时每次资产估值时提固定一笔费用(根据开放日提)出错！", ex);
	  }finally{
			dbl.closeResultSetFinal(rs);
	  }  
	  return "";
    }
    
    /**
     * add by huangqirong 2012-05-09 story #2565 通用参数获取分级组合保留位数
     * pubpara : 参数编号
     * paragroupcode ：参数组编号
     * ctlgroupcode ：控件组编号
     * ctlcode1 : 控件1 Name
     * ctlcode2 : 控件2 Name
     * value1 ：控件1 查询条件值
     * defaultValue : 默认返回值
     * */
    public String getDigitsPortMethod(String pubpara, String paragroupcode ,String ctlgroupcode , String ctlcode1,String ctlcode2,String value1 ,String defaultValue) throws YssException{
  	  ResultSet rs=null;
  	  String strSql="";
//  	  Map portClassMap=new HashMap();
  	  String restr = defaultValue == null || defaultValue.trim().length() == 0 ? "3" : defaultValue;//默认为截位和保留3位
  	  try{
  		  strSql="select FCtlvalue from ( select max(FParaid) as FParaid from "+pub.yssGetTableName("tb_pfoper_pubpara") +
					         " where fpubparacode = " + dbl.sqlString(pubpara) +
					         " and Fparagroupcode = " + dbl.sqlString(paragroupcode) + 
					         " and Fctlgrpcode = " + dbl.sqlString(ctlgroupcode) + 
					         " and FCtlcode= " + dbl.sqlString(ctlcode1) +
					         " and FCtlvalue like " +dbl.sqlString(value1+"|%") +
					         " and fparaid <> 0 "+
					         " ) tppp1 " +
					         " join(select * from "+pub.yssGetTableName("tb_pfoper_pubpara") +
						         " where fpubparacode = " + dbl.sqlString(pubpara) +
						         " and Fparagroupcode = " + dbl.sqlString(paragroupcode) + 
						         " and Fctlgrpcode = " + dbl.sqlString(ctlgroupcode) +                
					             " and FCtlcode= "+ dbl.sqlString(ctlcode2)+ ") tppp2 "+
					             " on tppp1.FParaid = tppp2.FParaid ";
  		  rs=dbl.openResultSet(strSql);
  		  if(rs.next()){
  			restr = rs.getString("FCtlvalue");
  		  }
  	  }catch (Exception ex) {
			throw new YssException("获取分级组合单位净值保留位数出错！", ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
  	  return restr;
    }
    //add by zhouwei 20120604 通参的作用是对于采用货币是基金新的计提方式的组合设定启用日期
    //未设置或者小于启用日期的时候采用原有的计提方式
    /**
     * add by huangqirong 2012-06-27 story #2727
     * 
     * */
    public String getAddAVG(String portCode , String subType) throws YssException{
    	ResultSet rs = null;
    	String strSql = "";
    	String result = "";
    	
    	strSql =" select tppp3.* from (select max(FParaid) as FParaid from " + pub.yssGetTableName("tb_pfoper_pubpara") +
			  " where fpubparacode = 'finish_avgcost' " +
			  " and Fparagroupcode = 'dayfinish' " +
			  " and Fctlgrpcode = 'CtlPubParaAvgcost' " +
			  " and FCtlcode= 'portSel' " +
			  " and FCtlvalue like " + dbl.sqlString(portCode + "|%") +
			  " and fparaid <> 0 " +
			  " ) tppp1 " +
			  " join(select * from " + pub.yssGetTableName("tb_pfoper_pubpara") +
			  " where fpubparacode = 'finish_avgcost' " +
			  " and Fparagroupcode = 'dayfinish' " +
			  " and Fctlgrpcode = 'CtlPubParaAvgcost' " +
			  " and FCtlcode = 'subType' " +
			  " and FCtlValue like " + dbl.sqlString(subType+ "|%") +                  
			  " ) tppp2 " +
			  " on tppp1.FParaid = tppp2.FParaid " +
			  " join " +
			  " (select * from " + pub.yssGetTableName("tb_pfoper_pubpara") +
			  " 	where FCtlcode = 'cboAvgCast' " +
			  " ) tppp3 " +
			  " on tppp2.FParaid = tppp3.FParaid " +
			  " and tppp2.fpubparacode = tppp3.fpubparacode " +
			  " and tppp2.Fparagroupcode = tppp3.Fparagroupcode "+
			  " and tppp2.Fctlgrpcode = tppp3.Fctlgrpcode ";
                
    	try {
    		rs = dbl.openResultSet(strSql);
    		if(rs.next()){
    			result = rs.getString("FCtlValue");
    		}
		} catch (Exception e) {
			throw new YssException("查询通参加权平均算法出错！");
		}finally {
			dbl.closeResultSetFinal(rs);
		}
    	return result;
    }
    public String getMfIncomeNewWay(Date startDate) throws YssException{
  	  ResultSet rs=null;
  	  String strSql="";
  	  String selPorts="";
  	  try{
  		  strSql="select a.fctlvalue as port,b.fctlvalue as startDate,a.fparaid from "
  				+"   (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='mfIncomeNewWay'"
  				+"  and fparaid<>0 and fctlcode='selPort' order by fparaid asc) a left join "
  				+"  (select * from "+pub.yssGetTableName("tb_pfoper_pubpara")+" where fpubparacode='mfIncomeNewWay'"
  				+"  and fctlcode='startDate' and to_date(fctlvalue,'yyyy-MM-dd')<="+dbl.sqlDate(startDate)+")  b"
  				+"  on a.fparaid=b.fparaid ";  	
  		  rs=dbl.openResultSet(strSql);
  		  while(rs.next()){
  			  String port=rs.getString("port").split("[|]")[0];//组合
  			  //启用日期
  			  if(rs.getString("startDate")!=null && !rs.getString("startDate").equals("")){
  				  selPorts+=port+",";
  			  }
  		  }
  		  if(selPorts.length()>1){
  			  selPorts=selPorts.substring(0,selPorts.length()-1);
  		  }
  	  }catch (Exception ex) {
			throw new YssException("获取启用货币是基金收益计提新方式的组合！", ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
  	  return selPorts;
    }
    
    /**
     * add by huhuichao 2013-10-18 bug  #81248
     * 
     * */
    public String getFUStBrokerTwo(Date date,String portCode) throws YssException{
    	ResultSet rs=null;
    	String strSql="";
    	String Date="";
    	String selPorts="";
    	String yesOrNo="";
    	String para = "";
    	try{
    		strSql = " select * from " + pub.yssGetTableName("tb_pfoper_pubpara")+" pb where pb.fpubparacode = 'FUStBroker'"
    		+ " and pb.fparaid =  (select fparaid from "+pub.yssGetTableName("tb_pfoper_pubpara")
    		+ " where fctlvalue = (select max(pba.fctlvalue) from "+pub.yssGetTableName("tb_pfoper_pubpara")
    		+ " pba  where pba.fpubparacode = 'FUStBroker'  and pba.fctlcode = 'StartDate'" +
    				" and to_date(pba.fctlvalue,'yyyy-mm-dd') < = " + dbl.sqlDate(date) + 
    				" and fparaid in( select fparaid from "+pub.yssGetTableName("tb_pfoper_pubpara")+
    				" pbaa where pbaa.fpubparacode = 'FUStBroker' and " +
    				" SUBSTR(pbaa.fctlvalue,0,INSTR(pbaa.fctlvalue,'|',1,1)-1) = "+
    				
    				/**Start 20131023 added by liubo.Bug #81726.QDV4赢时胜(深圳)2013年10月21日01_B
    				 * 带入portCode的时候没有转成字符串，若portCode不是数字就会产生问题*/
    				dbl.sqlString(portCode) + ")" + " ))";
    				/**End 20131023 added by liubo.Bug #81726.QDV4赢时胜(深圳)2013年10月21日01_B*/
    		
    		rs=dbl.openResultSet(strSql);
    		while(rs.next()){
    	        if (rs.getString("fctlcode").equals("StartDate")){
    	        	Date = rs.getString("fctlvalue");
    	        }else if (rs.getString("fctlcode").equals("selPort1")){
    	        	selPorts = rs.getString("fctlvalue").split("[|]")[0];//组合
    	        }else if (rs.getString("fctlcode").equals("cboYesOrNo1")){
    	        	yesOrNo = rs.getString("fctlvalue").split(",")[1];
    	        }
    		}
    		if(Date.length() != 0){
    		para = Date+"\t"+selPorts+"\t"+yesOrNo;
    		}
    		
    	}catch (Exception ex) {
			throw new YssException("获取股指期货是否统计券商的日期出错！", ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
  	  return para;
    }
	
    //add by fangjiang 2012.07.12 story #2788深圳公司债从T+1改为T+0
    public String getQYRQ(String paraCode) throws YssException{
		  ResultSet rs=null;
		  String strSql="";
		  String result = null;
		  try{
			  strSql = " select a.fctlvalue from " + pub.yssGetTableName("tb_pfoper_pubpara") + " a where fpubparacode= " 
			           + dbl.sqlString(paraCode) + " and fparaid<>0 ";
			  rs=dbl.openResultSet(strSql);
			  while(rs.next()){
				  result = rs.getString("fctlvalue");
			  }   		  
		  }catch (Exception ex) {
			  throw new YssException("获取启用日期出错！", ex);
	  	  }finally{
	  		  dbl.closeResultSetFinal(rs);
	  	  }
	      return result;
      }
    
    /**
	 * add by huangqirong 2012-07-27 story #2760 获取 深交所中小企业板“003000－004999”证券代码区间的启用日期
	 * */
	public java.util.Date getSJSZXQYStartSet(String pubParaCode , String paraGroupCode , String ctlGrpCode ,String CTlCode) throws YssException{
		java.util.Date date = null;
		ResultSet rs = null;
		String sql = " select pfpb1.fctlvalue as fctlvalue from ( " +
					 	" select max(pf.fparaid) as fparaid from " + pub.yssGetTableName("tb_pfoper_pubpara") + 
					 	" pf where pf.fpubparacode= " + dbl.sqlString(pubParaCode) + " and pf.fparaid <> 0 " +
					 	" ) pfpb " +
					 	" join ( select * from " + pub.yssGetTableName("tb_pfoper_pubpara") + 
					 	" pf where pf.fpubparacode= " + dbl.sqlString(pubParaCode) + " and FParaGroupCode = " + dbl.sqlString(paraGroupCode) + 
					 	" and FCtlGrpCode = " + dbl.sqlString(ctlGrpCode) + " and FCTlCode = " + dbl.sqlString(CTlCode) + 
					 	" and pf.fparaid <> 0 ) pfpb1 on pfpb.fparaid = pfpb1.fparaid";
		
		try {
			rs = dbl.openResultSet(sql);
			if(rs.next()){
				date = YssFun.parseDate(rs.getString("fctlvalue"));
			}
		} catch (Exception e) {
			throw new YssException("获取公共参数深交所中小企业板启用日期出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return date;
	}
	
	/**
	 * add by huangqirong 2012-12-07 story #3371
	 * 获取股指期货持仓模式
	 * */
	public String getFutursPositionType(String pubpara, String paragroupcode ,String ctlgroupcode , String ctlcode1,String ctlcode2,String value1 )throws YssException{
		ResultSet rs = null;
		String result ="" ;
		String sql = " select FCtlvalue from ( " +
			  " select max(FParaid) as FParaid from " + pub.yssGetTableName("tb_pfoper_pubpara") + " pf " + 
				  " where pf.fpubparacode = " + dbl.sqlString(pubpara) +
				  " and pf.fparagroupcode = " + dbl.sqlString(paragroupcode) + 
				  " and pf.fctlgrpcode = " + dbl.sqlString(ctlgroupcode) + 
				  " and pf.FCtlcode = " + dbl.sqlString(ctlcode1) + 
				  " and pf.FCtlvalue like " + dbl.sqlString(value1 + "|%") + 
				  " and pf.fparaid <> 0 " +
			  " ) pf1 join ( " +  
			  " select * from " + pub.yssGetTableName("tb_pfoper_pubpara") + " pf " + 
				  " where pf.fpubparacode = " + dbl.sqlString(pubpara) +
				  " and pf.fparagroupcode = " + dbl.sqlString(paragroupcode) + 
				  " and pf.fctlgrpcode = " + dbl.sqlString(ctlgroupcode) + 
				  " and pf.FCtlcode = " + dbl.sqlString(ctlcode2) + 
				  "  ) pf2 " + 
				  " on pf1.FParaid = pf2.fparaid ";
		try {
				rs = dbl.openResultSet(sql);
				if(rs.next()){
					result = rs.getString("FCtlvalue");				
				}
		}catch (Exception e) {
			e.getStackTrace();
			throw new YssException("获取股指期货持仓模式通参出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return result;
	}
}
