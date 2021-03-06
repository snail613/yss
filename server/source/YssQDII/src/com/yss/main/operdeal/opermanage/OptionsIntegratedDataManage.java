package com.yss.main.operdeal.opermanage;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.main.operdata.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;
import com.yss.vsub.YssOperFun;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.linkInfo.BaseLinkInfoDeal;
import com.yss.manager.TradeDataAdmin;
import com.yss.pojo.cache.YssCost;
import com.yss.pojo.cache.YssFeeType;

/**
 * <P>xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
 * 
 * <p>Title:by xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持 </p>
 *
 * <p>Description:对期权业务的处理- 生成证券变动的数据，即插入综合业务表</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OptionsIntegratedDataManage extends OptionsControlManage{
    private String securityCodes = "";//保存证券代码
    private String analysisCode1 = "";//分析代码1
    private String analysisCode2 = "";//分析代码2
    private String analysisCode3 = "";//分析代码3
    private ArrayList subTrade = new ArrayList();//股票期权还会产生交易数据，插入到交易数据表（tb_001_data_subTrade）xuqiji 20100421
    public OptionsIntegratedDataManage() {
    }

    /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate; //调拨日期
        this.sPortCode = portCode; //组合
    }

    /**
     * 执行业务处理
     * @throws YssException
     */
    public void doOpertion() throws YssException {
        createIntegratedData(); //生成证券变动的数据
    }

	/**
     * createIntegratedData 生成证券变动的数据,即插入综合业务表(tb_001_data_integraded)
     */
    private void createIntegratedData() throws YssException {
        ArrayList optionsTradeData = null;
        TradeDataAdmin tradeadmin = null;
        
        try {
        	tradeadmin = new TradeDataAdmin();
        	tradeadmin.setYssPub(pub);
            optionsTradeData = getOptionsTradeData();//此方法查询期权交易数据表以及关联表期权成本以及估值增值表中的关联数据
            saveSecurityStorageData(optionsTradeData);//保存数据

            //if(subTrade.size() > 0){//保存交易子表数据
            	tradeadmin.addAll(subTrade);
            	tradeadmin.insert(this.dDate,this.dDate,this.sPortCode,
            			YssOperCons.YSS_JYLX_REGOU_BSTATEExercis + "," + YssOperCons.YSS_JYLX_REGOU_SSTATEExercis + "," 
            			+ YssOperCons.YSS_JYLX_REGU_BSTATEExercis + "," +YssOperCons.YSS_JYLX_REGU_SSTATEExercis);
            //}
            	
            	//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
        		//当日产生数据，则认为有业务。
        		if((optionsTradeData==null ||optionsTradeData.size()==0) && (subTrade==null ||subTrade.size()==0)){
        			this.sMsg="        当日无业务";
        		}	
        } catch (Exception e) {
            throw new YssException("插入综合业务表数据出错！\r\t", e);
        }
    }
    /**
     * 保存证券变动数据
     * @param optionsTradeData
     * @throws YssException
     */
	private void saveSecurityStorageData(ArrayList optionsTradeData) throws
        YssException {
        String filterAnalysisCode1 = " ";//分析代码1筛选
        String filterAnalysisCode2 = " ";//分析代码2筛选
        String filterAnalysisCode3 = " ";//分析代码3筛选
        String filterSecurityCode = "";//证券代码筛选
        if (analysisCode1.length() > 0 &&
            analysisCode1.endsWith(",")) {
            filterAnalysisCode1 = this.analysisCode1.substring(0,
                analysisCode1.length() - 1);//去掉最后的“，”号
        }
        if (analysisCode2.length() > 0 &&
            analysisCode2.endsWith(",")) {
            filterAnalysisCode2 = this.analysisCode2.substring(0,
                analysisCode2.length() - 1);//去掉最后的“，”号
        }
        if (analysisCode3.length() > 0 &&
            analysisCode3.endsWith(",")) {
            filterAnalysisCode3 = this.analysisCode3.substring(0,
                analysisCode3.length() - 1);//去掉最后的“，”号
        }
        if (securityCodes.length() > 0 &&
            securityCodes.endsWith(",")) {
            filterSecurityCode = this.securityCodes.substring(0,
                securityCodes.length() - 1);//去掉最后的“，”号
        }
        //此方法为把数据最后插入到综合业务表的方法
        insertData(optionsTradeData, filterSecurityCode, this.dDate, this.sPortCode, filterAnalysisCode1,
                   filterAnalysisCode2, filterAnalysisCode3);
    }

    /**
     * insertData 此方法为把数据最后插入到综合业务表的方法
     *
     * @param optionsTradeData ArrayList 存放数据的ArrayList
     * @param filterSecurityCode String 证券代码
     * @param date Date 操作日期
     * @param sPortCode String 组合代码
     * @param filterAnalysisCode1 String 分析代码1
     * @param filterAnalysisCode2 String 分析代码2
     * @param filterAnalysisCode3 String 分析代码3
     */
    private void insertData(ArrayList optionsTradeData, String filterSecurityCode, Date dWorkDay, String sPortCode, String analysisCode1, String analysisCode2,
                            String analysisCode3) throws YssException {
        Connection conn=null;
        boolean bTrans=true;
        try {
            OptionsIntegratedAdmin optIntegrated = new OptionsIntegratedAdmin();//综合业务表的数据库操作类
            optIntegrated.setYssPub(pub);
            conn=dbl.loadConnection();//获取连接
            conn.setAutoCommit(false);//设置为手动打开连接
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Integrated"));//给操作表加锁
            //根据条件删除数据，日期，组合代码，分析代码
            optIntegrated.deleteData(dWorkDay,sPortCode,analysisCode1,analysisCode2,analysisCode3,"OptionsTrade");
            //保存数据
            optIntegrated.saveMutliSetting(optionsTradeData,dWorkDay);
            conn.commit();
            conn.setAutoCommit(true);
            bTrans=false;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }finally{
            dbl.endTransFinal(conn,bTrans);
        }
    }

    /**
     * getOptionsTradeData 此方法查询期权交易数据表以及关联表期权成本以及估值增值表中的关联数据
     *
     * @return ArrayList
     */
    private ArrayList getOptionsTradeData() throws YssException {
        StringBuffer buf = null;
        ArrayList optionsTradeData = null;
        ResultSet rs = null;
        OptionsIntegratedAdmin optIntegrate = null;
        boolean analy1;
        boolean analy2;
        boolean analy3;
        long sNum=0;//为了产生的编号不重复
        try {
            buf = new StringBuffer();
            /**
             * 期权交易数据关联表关联期权成本和估值增值表关联证券信息表关联期权信息表，主要是股票期权在行权时，还会产生标的证券的交易数据
             * modify by fangjiang 2011.09.13 story 1342
             */
            buf.append(" select trade.FNum,trade.fclosenum,trade.FSecurityCode,trade.FPortCode,trade.FBrokerCode,trade.FInvMgrCode,trade.FTradeTypeCode,");
            buf.append(" trade.FBegBailAcctCode,trade.FChageBailAcctCode,trade.FBargainDate,trade.FSettleDate,trade.FTradeAmount,");
            buf.append(" ocost.FCuryCost,ocost.FPortCuryCost,ocost.FBaseCuryCost,");
            buf.append(" trade.FBasecuryrate,trade.FPortcuryrate,(case when trade.FBegBailMoney is null then 0 ");
            buf.append(" else trade.FBegBailMoney end) as FBegBailMoney,SFNum,");
            buf.append(
                " (case when trade.FTradeFee1 is null then 0 else trade.FTradeFee1 end) as FTradeFee1,");
            buf.append(
                " (case when trade.FTradeFee2 is null then 0 else trade.FTradeFee2 end) as FTradeFee2,");
            buf.append(
                " (case when trade.FTradeFee3 is null then 0 else trade.FTradeFee3 end) as FTradeFee3,");
            buf.append(
                " (case when trade.FTradeFee4 is null then 0 else trade.FTradeFee4 end) as FTradeFee4,");
            buf.append(
                " (case when trade.FTradeFee5 is null then 0 else trade.FTradeFee5 end) as FTradeFee5,");
            buf.append(
                " (case when trade.FTradeFee6 is null then 0 else trade.FTradeFee6 end) as FTradeFee6,");
            buf.append(
                " (case when trade.FTradeFee7 is null then 0 else trade.FTradeFee7 end) as FTradeFee7,");
            buf.append(
                " (case when trade.FTradeFee8 is null then 0 else trade.FTradeFee8 end) as FTradeFee8");
            buf.append(",op.fmultiple,op.fsubcatcode,op.ftradecury,op.fexerciseprice,op.ftradeType,op.ftsecuritycode ");//xuqiji 20100421
            buf.append(" from(select a.*, a.FNum || a.FCloseNum as SFNum from ");
            buf.append(pub.yssGetTableName("TB_Data_Optionstraderela")).append(" a)trade left join(select fnum, sum(FCuryCost) as FCuryCost, sum(FBaseCuryCost) as FBaseCuryCost, sum(FPortCuryCost) as FPortCuryCost from ");//期权交易数据关联子表
            //-------------------------------xuqiji 20100421----------------------------------------//
            buf.append(pub.yssGetTableName("tb_data_optionscost")).append(" group by fnum )ocost on trade.SFNum=ocost.fnum ");//期权成本和估值增值表
            buf.append(" join(select a.ftradecury, a.FSUBCATCODE, b.fmultiple,a.fsecuritycode,b.fexerciseprice,b.ftradetypecode as ftradeType,b.ftsecuritycode from ");
            buf.append(pub.yssGetTableName("tb_para_security")).append(" a ");//证券信息表
            buf.append(" join (select * from ").append(pub.yssGetTableName("tb_para_optioncontract"));//期权信息表
            buf.append(" where FCheckState = 1) b on a.fsecuritycode = b.foptioncode where a.fcheckstate = 1) op on trade.fsecuritycode = op.fsecuritycode ");
            buf.append(" where trade.FCheckState = 1 and trade.FPortCode in (");
            //---------------------------------------end-------------------------------------------//
            buf.append(this.operSql.sqlCodes(this.sPortCode))
                .append(") and trade.FBargainDate = ").append(dbl.sqlDate(this.dDate));
            try {
                rs = dbl.queryByPreparedStatement(buf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            } catch (Exception e) {
                throw new YssException("获取关联数据出错！", e);
            }
            //分析代码
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            optionsTradeData = new ArrayList();
            //-------------------------------------20100421-----------------------------------//
            if(subTrade.size() > 0){
            	subTrade.clear();
            }
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
            //--------------------------------end--------------------------//
            while (rs.next()) {
                optIntegrate = setSecIntegrate(rs, analy1, analy2, analy3);//此方法主要是设置综合业务中数据，把数据保存到综合业务的Bean中
                if(rs.getString("FTradeTypeCode").equalsIgnoreCase("32FP")&&rs.getString("fsubcatcode").equalsIgnoreCase("FP02")){//股票期权行权时还要产生交易数据 FP02-股票期权
                	 //--------------------拼接交易编号---------------------
                    sNum++;
                    String tmp = "";
                    for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                        tmp += "0";
                    }
                    strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                    //------------------------end--------------------------//
                	setSubTradeData(rs,strNumDate);
                }
                optionsTradeData.add(optIntegrate); //把数据放到一个ArrayList中
            }
            //---------------------------------------end-------------------------------------//
        } catch (Exception e) {
            throw new YssException("查询期权交易数据出错！\r\t", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return optionsTradeData;
    }

    /**
     * 此方法主要是设置综合业务中数据，把数据保存到综合业务的Bean中
     * @param rs ResultSet
     * @param analy1 boolean 分析代码1
     * @param analy2 boolean 分析代码2
     * @param analy3 boolean 分析代码3
     * @return OptionsIntegratedBean
     * @throws YssException
     */
    private OptionsIntegratedAdmin setSecIntegrate(ResultSet rs, boolean analy1,
        boolean analy2, boolean analy3) throws
        YssException {
        OptionsIntegratedAdmin optIntegrate = null;//综合业务表的操作数据库类
        SecurityBean security = null;//证券库存操作类
        double yesDStorgeAmount = 0;
        try {
        	
        	yesDStorgeAmount = getYesDStorgeAmount(rs.getString("FSecurityCode")); //获取昨日的库存数量
        	
            optIntegrate = new OptionsIntegratedAdmin();
            if (rs.getString("fclosenum").equalsIgnoreCase("01")) {//买入状态-开仓，
                optIntegrate.setIInOutType(1);//流入
                optIntegrate.setDAmount(YssD.mul(rs.getDouble("FTradeAmount"), optIntegrate.getIInOutType()));//交易数量
            }else if (rs.getString("fclosenum").equalsIgnoreCase("02")){//买入状态-平仓
                optIntegrate.setIInOutType(-1);//流出
                optIntegrate.setDAmount(YssD.mul(rs.getDouble("FTradeAmount"), optIntegrate.getIInOutType()));//交易数量
            }else if (rs.getString("fclosenum").equalsIgnoreCase("03")){//卖出状态-开仓
            	optIntegrate.setIInOutType(1);//流入
                optIntegrate.setDAmount(YssD.mul(rs.getDouble("FTradeAmount"), optIntegrate.getIInOutType()));//交易数量
            }else if(rs.getString("fclosenum").equalsIgnoreCase("04")){//卖出状态-平仓
                optIntegrate.setIInOutType(-1);//流出
                optIntegrate.setDAmount(YssD.mul(rs.getDouble("FTradeAmount"), optIntegrate.getIInOutType()));//交易数量
            }else if (rs.getString("FTradeTypeCode").equalsIgnoreCase("32FP")){//32FP-期权行权，
            	/*if(yesDStorgeAmount > 0){
            		optIntegrate.setIInOutType(-1);//流出
            	}else{
            		optIntegrate.setIInOutType(1);//流入
            	}*/
            	optIntegrate.setIInOutType(-1);//流出
                optIntegrate.setDAmount(YssD.mul(rs.getDouble("FTradeAmount"), optIntegrate.getIInOutType()));//交易数量
            }else if(rs.getString("FTradeTypeCode").equalsIgnoreCase("33FP")){//33FP-期权结算
            	/*if(yesDStorgeAmount > 0){
            		optIntegrate.setIInOutType(-1);//流出
            	}else{
            		optIntegrate.setIInOutType(1);//流入
            	}*/
            	optIntegrate.setIInOutType(-1);//流出
                optIntegrate.setDAmount(rs.getDouble("FTradeAmount"));//交易数量
            }else{//34FP-期权放弃行权
            	/*if(yesDStorgeAmount > 0){
            		optIntegrate.setIInOutType( -1);//流出
            	}else{
            		optIntegrate.setIInOutType(1);//流入
            	}*/
            	optIntegrate.setIInOutType(-1);//流出
                optIntegrate.setDAmount(YssD.mul(rs.getDouble("FTradeAmount"), optIntegrate.getIInOutType()));//交易数量
            }
            optIntegrate.setSSecurityCode(rs.getString("FSecurityCode"));//证券代码
            securityCodes += optIntegrate.getSSecurityCode() + ",";
            optIntegrate.setSExchangeDate(YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd"));//操作日期
            optIntegrate.setSOperDate(YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd"));//业务日期
            optIntegrate.setSRelaNum(" ");
            optIntegrate.setSNumType("OptionsTrade");//删除条件

            optIntegrate.setSTradeTypeCode(rs.getString("FTradeTypeCode"));//交易方式

            optIntegrate.setSPortCode(rs.getString("FPortCode"));//组合代码
            if (analy1) {
                optIntegrate.setSAnalysisCode1(rs.getString("FInvMgrCode"));//投资经理
            } else {
                optIntegrate.setSAnalysisCode1(" ");
            }
            analysisCode1 += optIntegrate.getSAnalysisCode1() + ",";
            if (analy2) {
                optIntegrate.setSAnalysisCode2(rs.getString("FBrokerCode"));//券商
            } else {
                optIntegrate.setSAnalysisCode2(" ");
            }
            analysisCode2 += optIntegrate.getSAnalysisCode2() + ",";
            if (analy3) {
                optIntegrate.setSAnalysisCode3(" ");//分析代码3
            } else {
                optIntegrate.setSAnalysisCode3(" ");
            }
            analysisCode3 += optIntegrate.getSAnalysisCode3() + ",";

            //设置原币成本---------
            optIntegrate.setDCost(YssD.mul(rs.getDouble("FCuryCost"), optIntegrate.getIInOutType()));
            optIntegrate.setDMCost(YssD.mul(rs.getDouble("FCuryCost"), optIntegrate.getIInOutType()));
            optIntegrate.setDVCost(YssD.mul(rs.getDouble("FCuryCost"), optIntegrate.getIInOutType()));
            //----------------

            security = new SecurityBean();
            security.setYssPub(pub);
            security.setSecurityCode(optIntegrate.getSSecurityCode());
            security.getSetting();

            optIntegrate.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));
            optIntegrate.setDPortCuryRate(rs.getDouble("FPortCuryRate"));
            //设置基础货币成本----------
            optIntegrate.setDBaseCost(YssD.mul(rs.getDouble("FBaseCuryCost"), optIntegrate.getIInOutType()));
            optIntegrate.setDMBaseCost(YssD.mul(rs.getDouble("FBaseCuryCost"), optIntegrate.getIInOutType()));
            optIntegrate.setDVBaseCost(YssD.mul(rs.getDouble("FBaseCuryCost"), optIntegrate.getIInOutType()));
            //-----------------------

            //设置组合货币成本----------
            optIntegrate.setDPortCost(YssD.mul(rs.getDouble("FPortCuryCost"), optIntegrate.getIInOutType()));
            optIntegrate.setDMPortCost(YssD.mul(rs.getDouble("FPortCuryCost"), optIntegrate.getIInOutType()));
            optIntegrate.setDVPortCost(YssD.mul(rs.getDouble("FPortCuryCost"), optIntegrate.getIInOutType()));
            //--------------------

            optIntegrate.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置综合业务数据出错！\r\t", e);
        }
        return optIntegrate;
    }
    /**
     * 设置交易子表数据 xuqiji 20100421
     * @param rs
     * @throws YssException
     */
    private void setSubTradeData(ResultSet rs,String strNumDate)throws YssException{
    	TradeSubBean subTradeBean = null;
    	CashAccountBean caBean = null;//声明现金账户的bean
    	String strCashAccCode = " "; //现金帐户
    	FeeLinkBean FeeLink = new FeeLinkBean(); //费用链接
    	ArrayList alFeeBeans = null;
    	YssFeeType feeType = new YssFeeType(); //费用类型
    	FeeBean feeBean = null;
    	double dFeeMoney = 0;
    	BaseOperDeal baseOper = new BaseOperDeal(); //处理汇率，行情，费用的类
    	String feeA = ""; //把所有费用拼接起来
    	double Total = 0; //总费用
    	YssCost cost = null;//声明成本
    	double yesDStorgeAmount = 0; //昨日的库存数量
    	try{
    		yesDStorgeAmount = getYesDStorgeAmount(rs.getString("FSecurityCode")); //获取昨日的库存数量
    		
    		subTradeBean = new TradeSubBean();
    		subTradeBean.setInvestType("C");//投资类型
    		subTradeBean.setNum(strNumDate);//编号
    		subTradeBean.setSecurityCode(rs.getString("ftsecuritycode"));//证券代码
    		subTradeBean.setPortCode(rs.getString("FPortCode"));//组合代码
    		subTradeBean.setBrokerCode(rs.getString("FBrokerCode"));//券商
    		subTradeBean.setInvMgrCode(rs.getString("FInvMgrCode"));//投资经理
    		if(yesDStorgeAmount > 0&&rs.getString("FTradeType").equalsIgnoreCase("CALL")){
    			subTradeBean.setTradeCode(YssOperCons.YSS_JYLX_REGOU_BSTATEExercis);//交易方式--"92" 认购期权买入状态行权
    		}else if(yesDStorgeAmount > 0&&rs.getString("FTradeType").equalsIgnoreCase("PUT")){
    			subTradeBean.setTradeCode(YssOperCons.YSS_JYLX_REGU_BSTATEExercis);//交易方式--"94" 认沽期权买入状态行权
    		}else if(yesDStorgeAmount < 0&&rs.getString("FTradeType").equalsIgnoreCase("CALL")){
    			subTradeBean.setTradeCode(YssOperCons.YSS_JYLX_REGOU_SSTATEExercis);//交易方式--"93" 认购期权卖出状态行权
    		}else if(yesDStorgeAmount < 0&&rs.getString("FTradeType").equalsIgnoreCase("PUT")){
    			subTradeBean.setTradeCode(YssOperCons.YSS_JYLX_REGU_SSTATEExercis);//交易方式--"95" 认沽期权卖出状态行权
    		}
    		 //-------------------------设置现金账户链接属性值----------------------
    		BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.getOperDealCtx().getBean("cashacclinkdeal");
            cashacc.setYssPub(pub);
            cashacc.setLinkParaAttr( (rs.getString("FInvMgrCode").trim().length() > 0 ?
            							rs.getString("FInvMgrCode") :" "), //投资经理
                                    rs.getString("FPortCode"), //组合代码
                                    rs.getString("ftsecuritycode"), //证券代码
                                    (rs.getString("FBrokerCode").trim().length() > 0 ?
                                     rs.getString("FBrokerCode") : " "), //券商
                                     rs.getString("FTradeTypeCode"),
                                    rs.getDate("FBargainDate"), //日期
                                    rs.getString("ftradecury"), //币种
                                    YssOperCons.YSS_JYLX_Excerise); //交易类型为分发派息
            caBean = cashacc.getCashAccountBean();
            if (caBean != null) {
                strCashAccCode = caBean.getStrCashAcctCode();//获取现金账户代码
            } else {
                throw new YssException("系统执行期权业务处理时出现异常！" + "\n" + "【" +
                    rs.getString("ftsecuritycode") +
                    "】处理股票期权产生交易数据没有获取到链接现金帐户，请查看现金帐户链接设置中是否有相关设置！");
            }
            //--------------------------------------------------------------------
            subTradeBean.setCashAcctCode(strCashAccCode);//现金账户
            subTradeBean.setRateDate(YssFun.formatDate(rs.getDate("FBargainDate"),"yyyy-MM-dd"));//汇率日期
            subTradeBean.setBargainDate(YssFun.formatDate(rs.getDate("FBargainDate"),"yyyy-MM-dd"));//成交日期
            subTradeBean.setBargainTime("00:00:00");//成交时间
            subTradeBean.setSettleDate(YssFun.formatDate(rs.getDate("FSettleDate"),"yyyy-MM-dd"));//结算日期
            subTradeBean.setSettleTime("00:00:00");//结算时间
            subTradeBean.setFactSettleDate(subTradeBean.getSettleDate());//实际结算日期
            subTradeBean.setFactCashAccCode(subTradeBean.getCashAcctCode());//实际结算账户
            subTradeBean.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
            subTradeBean.setPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
            //交易数量=期货行权合约数量*每合约所规定的正股数量；
            subTradeBean.setTradeAmount(YssD.mul(Math.abs(rs.getDouble("FTradeAmount")),rs.getDouble("fmultiple")));
    		//交易金额=期货行权合约数量*行权价格
            subTradeBean.setTradeMoney(YssD.round(YssD.mul(subTradeBean.getTradeAmount(),rs.getDouble("fexerciseprice")),2));
            //交易价格=交易金额/交易数量
            subTradeBean.setTradePrice(YssD.round(YssD.div(subTradeBean.getTradeMoney(),subTradeBean.getTradeAmount()),2));
            
            //------------------------------------以下计算费用--------------------------------------//
            BaseLinkInfoDeal feeOper = (BaseLinkInfoDeal) pub.getOperDealCtx().getBean("FeeLinkDeal");
	        feeOper.setYssPub(pub);
	        baseOper.setYssPub(pub);
	        //设置变量，证券代码，交易方式，组合代码，券商代码
	        FeeLink.setSecurityCode(subTradeBean.getSecurityCode());
	        FeeLink.setTradeTypeCode(subTradeBean.getTradeCode());
	        FeeLink.setPortCode(subTradeBean.getPortCode());
	        FeeLink.setBrokerCode(subTradeBean.getBrokerCode());
	        feeOper.setLinkAttr(FeeLink);
	        alFeeBeans = feeOper.getLinkInfoBeans();//获取费用链接信息
	        if (alFeeBeans != null) {
	            //设置费用类型：数量，成本，利息，金额，等
	            feeType = new YssFeeType();
	            feeType.setAmount(subTradeBean.getTradeAmount());
	            feeType.setCost( -1);
	            feeType.setInterest(0);
	            feeType.setMoney(subTradeBean.getTradeMoney());
	            feeType.setIncome( -1);
	            feeType.setFee( -1);
	            for (int j = 0; j < alFeeBeans.size(); j++) {
	                feeBean = (FeeBean) alFeeBeans.get(j);
	                dFeeMoney = baseOper.calFeeMoney(feeType, feeBean);
	                feeBean.setFee(dFeeMoney);
	                feeA += feeBean.getFeeCode() + "\n" + feeBean.getFeeName() + "\n" + Double.toString(dFeeMoney) + "\f\n";
	                Total += dFeeMoney;
	            }
	        }
	        subTradeBean.setFees(feeA);//按一定格式拼接好的费用赋值给变量
	        feeA = "";
            //------------------------------------end-----------------------------------//
	        subTradeBean.setFactSettleMoney(YssD.add(subTradeBean.getTradeMoney(),Total));//清算款 = 成交金额 + 费用
	        subTradeBean.setTotalCost(YssD.add(subTradeBean.getTradeMoney(),Total));//投资总成本
	        subTradeBean.setAutoSettle(new Integer(1).toString()); //自动结算
	        subTradeBean.setTailPortCode(strCashAccCode);//尾差组合代码
	        subTradeBean.setAllotProportion(0);//分配比例
	        subTradeBean.setOldAllotAmount(0);//原始分配数量
	        subTradeBean.setAllotFactor(0);//分配因子
	        //---------------------以下为成本赋值--------------
            cost = new YssCost();
            cost.setCost(0);//原币核算成本
            cost.setMCost(0);//原币管理成本
            cost.setVCost(0);//原币估值成本
            cost.setBaseCost(0);//基础货币核算成本
            cost.setBaseMCost(0);//基础货币管理成本
            cost.setBaseVCost(0);//基础货币估值成本
            cost.setPortCost(0);//组合货币核算成本
            cost.setPortMCost(0);//组合货币管理成本
            cost.setPortVCost(0);//组合货币估值成本
            subTradeBean.setCost(cost);//成本
            //---------------------end-----------------//
            subTradeBean.setDataSource(0);//数据源
            subTradeBean.setSettleState(new Integer(0).toString());//结算状态，未结算“0”
            subTradeBean.checkStateId = 1;//审核状态
            subTradeBean.creatorCode = pub.getUserCode();//创建人
            subTradeBean.checkTime = YssFun.formatDatetime(new java.util.Date());//审核时间
            subTradeBean.checkUserCode = pub.getUserCode();//审核人
            subTradeBean.creatorTime = YssFun.formatDatetime(new java.util.Date());//创建时间
            subTrade.add(subTradeBean);
    	}catch (Exception e) {
			throw new YssException("设置交易子表数据出错！",e);
		}
    }
    /**
     * 通过证券代码获取昨日的证券库存数量
     * @param string sSecurityCode 证券代码
     * @return double 库存数量
     */
    private double getYesDStorgeAmount(String sSecurityCode) throws YssException {
        StringBuffer sqlBuf = new StringBuffer();
        double yesDStorgeAmount = 0; //初始库存为0
        ResultSet rs = null;
        try {
            //通过组合代码、证券代码、库存日期、审核状态从证券库存中取数
            sqlBuf.append("select FStorageAmount from ").append(pub.yssGetTableName("Tb_Stock_Security"));
            sqlBuf.append(" WHERE FCheckState = 1");
            sqlBuf.append(" AND FPortCode = (").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
            sqlBuf.append(" AND FSecurityCode= ").append(dbl.sqlString(sSecurityCode));
            sqlBuf.append(" AND FStorageDate= ").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));

            rs = dbl.queryByPreparedStatement(sqlBuf.toString());
            if (rs.next()) {
                yesDStorgeAmount = rs.getDouble("FStorageAmount");
            }
        } catch (Exception e) {
            throw new YssException("期权处理-获取证券库存出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return yesDStorgeAmount;
    }
}













