package com.yss.main.operdeal.opermanage.securitymanage;

import java.util.*;

import com.yss.util.*;
import java.sql.*;
import com.yss.main.cashmanage.*;
import com.yss.commeach.EachRateOper;
import com.yss.main.operdata.*;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.stgstat.StgSecurity;
import com.yss.main.dao.*;
import com.yss.pojo.cache.*;
import com.yss.manager.*;
import com.yss.dsub.*;
/**
 *
 * <p>Title: 新股新债业务处理（网下）</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 * 2009-07-13 蒋锦 MS00021 QDV4.1赢时胜（上海）2009年4月20日21_A
 * @version 1.0
 */
public class NewIssueManage
    extends BaseBean {

    //产生资金调拨的交易编号
    private String cashTradeNum = "";
    //分析代码
    private boolean analy1;
    private boolean analy2;
    private boolean analy3;

    private java.util.Date bargainDate;
    private String portCode;

    public boolean isAnaly1() {
       return analy1;
   }

   public boolean isAnaly2() {
       return analy2;
   }

   public boolean isAnaly3() {
       return analy3;
   }

   public java.util.Date getBargainDate() {
       return bargainDate;
   }

   public String getPortCode() {
       return portCode;
   }

   public String getCashTradeNum() {
       return cashTradeNum;
   }

   public void setAnaly3(boolean analy3) {
       this.analy3 = analy3;
   }

   public void setAnaly2(boolean analy2) {
       this.analy2 = analy2;
   }

   public void setAnaly1(boolean analy1) {
       this.analy1 = analy1;
   }

   public void setBargainDate(java.util.Date bargainDate) {
       this.bargainDate = bargainDate;
   }

   public void setPortCode(String portCode) {
       this.portCode = portCode;
   }

   public void setCashTradeNum(String cashTradeNum) {
       this.cashTradeNum = cashTradeNum;
   }


    public NewIssueManage(YssPub pub) {
        setYssPub(pub);
    }

    /**
     * 新股新债业务网上交易处理，产生冲减的应收应付数据
     * @param alRecPay ArrayList
     * @throws YssException
     */
    //edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B 添加ArrayList alIntegrated参数
    public String newIssueTradeDeal(ArrayList alRecPay,ArrayList alIntegrated) throws YssException{
        StringBuffer bufSql = new StringBuffer();
        CashPecPayBean recPay = null;
        ResultSet rs = null;
        String sTradeNum = "";
		//---add by songjie 2011.11.01 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B start---//
        SecIntegratedBean secIntegrate = null;
        YssCost cost = null;
        double baseCuryRate = 0;
        double portCuryRate = 0;
		//---add by songjie 2011.11.01 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B end---//
        try {
        	updateXGLTTradeCost();//add by songjie 2011.11.10 BUG 3076 QDV4赢时胜(测试)2011年11月07日02_B
        	
            //查找交易子表中新股业务的数据
            bufSql.append(" SELECT a.*, b.FCatCode, b.FSubCatCode, b.FTradeCury ");
            bufSql.append(" FROM (SELECT * ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Subtrade"));
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FTradeTypeCode IN (" +
                          dbl.sqlString(YssOperCons.YSS_JYLX_XGSG) + "," +
                          dbl.sqlString(YssOperCons.YSS_JYLX_WSZQ) + "," +
                          dbl.sqlString(YssOperCons.YSS_JYLX_ZQFK) + "," +
                          dbl.sqlString(YssOperCons.YSS_JYLX_XZSG) + "," +
                          dbl.sqlString(YssOperCons.YSS_JYLX_XZWSZQ) + "," +
                          dbl.sqlString(YssOperCons.YSS_JYLX_XZLT) + ","+//add by guolongchao 20111103 bug 3001 添加新债流通交易类型
                          //---edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B start---//
                          dbl.sqlString(YssOperCons.YSS_JYLX_XZZQFK) + "," +
                          dbl.sqlString(YssOperCons.YSS_JYLX_XGLT) + ") ");//添加 新股流通交易类型
            			  //---edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B end---//
            bufSql.append(" AND FBargainDate = ").append(dbl.sqlDate(bargainDate));
            bufSql.append(" AND FPortCode = " + dbl.sqlString(portCode) + ") a ");
            bufSql.append(" JOIN (SELECT FSecurityCode, FCatCode, FSubCatCode, FTradeCury ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Security"));
            bufSql.append(" WHERE FCheckState = 1) b ON a.fsecuritycode = b.fsecuritycode ");

            rs = dbl.queryByPreparedStatement(bufSql.toString());
            
            //---edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B start---//
            ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
            "avgcostcalculate");
            costCal.setYssPub(pub);
            
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
            //---edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B end---//
            
            while(rs.next()){
                String sTradeTypeCode = rs.getString("FTradeTypeCode");
                //edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B
                //非新股流通数据，则不生成现金应收应付数据                                                             
                if(!sTradeTypeCode.equals(YssOperCons.YSS_JYLX_XGLT)&&!sTradeTypeCode.equals(YssOperCons.YSS_JYLX_XZLT)){//update by guolongchao bug 3001 20111103 非新债流通数据，则不生成现金应收应付数据      
                	recPay = new CashPecPayBean();
                	recPay.setTradeDate(rs.getDate("FBargainDate"));
                	recPay.checkStateId = 1;
                	recPay.setRelaNum(rs.getString("FNum"));
                	recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_SUBTRADE);
                	recPay.setPortCode(rs.getString("FPortCode"));
                	if (this.analy1) {
                		recPay.setInvestManagerCode(rs.getString("FInvMgrCode"));
                	} else {
                		recPay.setInvestManagerCode(" ");
                	}
                	if (this.analy2){
                		recPay.setCategoryCode(rs.getString("FCatCode"));
                	} else{
                		recPay.setCategoryCode(" ");
                	}
                	recPay.setCashAccCode(rs.getString("FCashAccCode"));
                	recPay.setCuryCode(rs.getString("FTradeCury"));
                	if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_XGSG) ||
                		sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_XZSG)) {
                		//申购
                		recPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                		recPay.setSubTsfTypeCode("06AP_" + rs.getString("FCatCode"));
                		recPay.setInOutType(1);

                	} else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_WSZQ) ||
                		(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_XZWSZQ) && 
                		 //edit by songjie 2012.04.27 BUG 4432 QDV4赢时胜(测试)2012年04月27日03_B
                		(rs.getString("FSubCatCode").equals("FI06") || rs.getString("FSubCatCode").equals("FI07"))
                		)) {
                		//中签
                		recPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                		recPay.setSubTsfTypeCode("06AP_" + rs.getString("FCatCode"));
                		recPay.setInOutType(-1);

                	} else {
                		//返款
                		recPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
                		recPay.setSubTsfTypeCode("02AP_" + rs.getString("FCatCode"));
                	}
                	recPay.setDataSource(0);
                	recPay.setStockInd(1);

                	recPay.setMoney(rs.getDouble("FFactSettleMoney"));
                	if(sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_WSZQ)){//QDII4.1赢时胜上海2010年03月02日03_B MS00899 by leeyu  20100303
                		//由于新股中签的交易数据实际结算金额为0，故取成交金额
                		recPay.setMoney(rs.getDouble("FTradeMoney"));
                	}
                	//----- add by wangzuochun 2010.06.23 MS01226    国内网上新债中签做业务处理后，缺少一笔流出金额去冲减申购时的确认金额    QDV4赢时胜（测试）2010年05月28日02_B  
                	else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_XZWSZQ)){
                		recPay.setMoney(rs.getDouble("FTradeMoney"));
                	}
                	//--------------------- MS01226 ------------------------//
                	recPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                	recPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                	
                	recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                			recPay.getBaseCuryRate(), 2));

                	recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                			recPay.getBaseCuryRate(), recPay.getPortCuryRate(),
                			rs.getString("FTradeCury"), bargainDate, recPay.getPortCode(), 2));

                	alRecPay.add(recPay);
                	
                	//---edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B start---//
                	sTradeNum += (rs.getString("FNum") + ",");
                }else{
                    costCal.initCostCalcutate(rs.getDate("FBargainDate"),
                            rs.getString("FPortCode"),
                            rs.getString("FInvMgrCode"),
                            "",
                            rs.getString("FAttrClsCode"));
                    
                    cost = costCal.getCarryCost(rs.getString("FSecurityCode"),
                            rs.getDouble("FTradeAmount"),
                            rs.getString("FNum"),
                            null,//null参数：add by guolongchao 20110815  STORY #1207  添加结算日期参数
                            "newissue",
                            sTradeTypeCode);//YssOperCons.YSS_JYLX_XGLT); update by guolongchao 20111103 bug3001 将原来的新股流通数据 产生综合业务数据改为：新股流通、新债流通数据都产生综合业务数据                 
                    costCal.roundCost(cost, 2);


                    baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FBargainDate"),
                    		rs.getString("FTradeCury"), rs.getString("FPortCode"),
                    		YssOperCons.YSS_RATE_BASE);

                    rateOper.getInnerPortRate(bargainDate, rs.getString("FTradeCury"),
                    		rs.getString("FPortCode"));
                    portCuryRate = rateOper.getDPortRate();
                    
                	secIntegrate = new SecIntegratedBean();

                    secIntegrate.setIInOutType(-1);
                    secIntegrate.setInvestType(rs.getString("FInvestTYpe"));
                    secIntegrate.setSExchangeDate(YssFun.formatDate(rs.getDate(
                        "FBargainDate"),
                        "yyyy-MM-dd"));
                    secIntegrate.setSOperDate(YssFun.formatDate(rs.getDate("FBargainDate"),
                        "yyyy-MM-dd"));
                    secIntegrate.setSRelaNum(" ");
                    secIntegrate.setSNumType("securitymanage"); //------ modify by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B  

                    secIntegrate.setSTradeTypeCode(rs.getString("FTradeTypeCode"));

                    secIntegrate.setSPortCode(rs.getString("FPortCode"));
                    if (this.analy1) {
                        secIntegrate.setSAnalysisCode1(rs.getString("FInvMgrCode"));
                    } else {
                        secIntegrate.setSAnalysisCode1(" ");
                    }
                    secIntegrate.setSAnalysisCode2(" ");
                    secIntegrate.setSAnalysisCode3(" ");

                    secIntegrate.setDAmount(YssD.mul(rs.getDouble("FTradeAmount"), secIntegrate.getIInOutType()));
                    secIntegrate.setSSecurityCode(rs.getString("FSecurityCode"));

                    secIntegrate.setDCost(YssD.mul(cost.getCost(), secIntegrate.getIInOutType()));
                    secIntegrate.setDMCost(YssD.mul(cost.getCost(), secIntegrate.getIInOutType()));
                    secIntegrate.setDVCost(YssD.mul(cost.getCost(), secIntegrate.getIInOutType()));

                    secIntegrate.setDBaseCost(YssD.mul(cost.getBaseCost(), secIntegrate.getIInOutType()));
                    secIntegrate.setDMBaseCost(YssD.mul(cost.getBaseCost(), secIntegrate.getIInOutType()));
                    secIntegrate.setDVBaseCost(YssD.mul(cost.getBaseCost(), secIntegrate.getIInOutType()));

                    secIntegrate.setDPortCost(YssD.mul(cost.getPortCost(), secIntegrate.getIInOutType()));
                    secIntegrate.setDMPortCost(YssD.mul(cost.getPortCost(), secIntegrate.getIInOutType()));
                    secIntegrate.setDVPortCost(YssD.mul(cost.getPortCost(), secIntegrate.getIInOutType()));

                    secIntegrate.setDPortCuryRate(portCuryRate);

                    secIntegrate.setDBaseCuryRate(baseCuryRate);

                    secIntegrate.checkStateId = 1;
                    secIntegrate.setSTsfTypeCode("05");
                    secIntegrate.setSSubTsfTypeCode("05" + rs.getString("FCatCode"));
                    secIntegrate.setAttrClsCode(rs.getString("FAttrClsCode"));
                    
                    alIntegrated.add(secIntegrate);

                    //--------------转到流通的证券变动---------------//
                    secIntegrate = new SecIntegratedBean();

                    secIntegrate.setIInOutType(1);
                    secIntegrate.setInvestType(rs.getString("FInvestType"));
                    secIntegrate.setSExchangeDate(YssFun.formatDate(rs.getDate(
                        "FBargainDate"),
                        "yyyy-MM-dd"));
                    secIntegrate.setSOperDate(YssFun.formatDate(rs.getDate("FBargainDate"),
                        "yyyy-MM-dd"));
                    secIntegrate.setSRelaNum(" ");
                    secIntegrate.setSNumType("securitymanage");  //------ modify by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B 

                    secIntegrate.setSTradeTypeCode(rs.getString("FTradeTypeCode"));

                    secIntegrate.setSPortCode(rs.getString("FPortCode"));
                    if (this.analy1) {
                        secIntegrate.setSAnalysisCode1(rs.getString("FInvMgrCode"));
                    } else {
                        secIntegrate.setSAnalysisCode1(" ");
                    }
                    secIntegrate.setSAnalysisCode2(" ");
                    secIntegrate.setSAnalysisCode3(" ");

                    secIntegrate.setDAmount(rs.getDouble("FTradeAmount"));
                    secIntegrate.setSSecurityCode(rs.getString("FSecurityCode"));

                    secIntegrate.setDCost(rs.getDouble("FCost"));
                    secIntegrate.setDMCost(secIntegrate.getDCost());
                    secIntegrate.setDVCost(secIntegrate.getDCost());

                    secIntegrate.setDBaseCost(this.getSettingOper().calBaseMoney(secIntegrate.getDCost(),
                        baseCuryRate, 2));
                    secIntegrate.setDMBaseCost(secIntegrate.getDBaseCost());
                    secIntegrate.setDVBaseCost(secIntegrate.getDBaseCost());

                    secIntegrate.setDPortCost(this.getSettingOper().calPortMoney(secIntegrate.getDCost(),
                        baseCuryRate, portCuryRate,
                        rs.getString("FTradeCury"), bargainDate, secIntegrate.getSPortCode(), 2));
                    secIntegrate.setDMPortCost(secIntegrate.getDPortCost());
                    secIntegrate.setDVPortCost(secIntegrate.getDPortCost());
                   
                    secIntegrate.setDPortCuryRate(portCuryRate);
                    secIntegrate.setDBaseCuryRate(baseCuryRate);

                    secIntegrate.checkStateId = 1;
                    secIntegrate.setSTsfTypeCode("05");
                    secIntegrate.setSSubTsfTypeCode("05" + rs.getString("FCatCode"));
                    secIntegrate.setAttrClsCode(" "); //转到公开发行新股
                    
                    alIntegrated.add(secIntegrate);
                }
                //---edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B end---//
            }
            if(sTradeNum.length() > 0){
                sTradeNum = sTradeNum.substring(0, sTradeNum.length() - 1);
            }
        } catch (Exception ex) {
            throw new YssException("处理新股新债业务网上交易出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sTradeNum;
    }

    /**
     * add by songjie 2011.11.10 
     * BUG 3076 QDV4赢时胜(测试)2011年11月07日02_B
     * @throws YssException
     */
    private void updateXGLTTradeCost()throws YssException,SQLException{
    	String strSql = "";
    	ResultSet rs = null;
    	String securityCode = "";
    	
		strSql = " select distinct FSecurityCode from " + pub.yssGetTableName("Tb_Data_SubTrade") + 
		" where FTradeTypeCode = " + dbl.sqlString(YssOperCons.YSS_JYLX_XGLT) + 
		" and FBargainDate = " + dbl.sqlDate(bargainDate) +
		" and FPortCode = " + dbl.sqlString(portCode) +
		" and FCheckState = 1 ";
    	try{
    		rs = dbl.queryByPreparedStatement(strSql);
    		while(rs.next()){
    			securityCode += rs.getString("FSecurityCode") + ",";
    		}
    		
    		if(securityCode.length() > 0){
    			securityCode = securityCode.substring(0, securityCode.length() - 1);
    		}
    	
    		StgSecurity securityCost = new StgSecurity();
			securityCost.setYssPub(pub);
			securityCost.setStatCodes(securityCode);
			securityCost.refreshTradeCost(bargainDate,bargainDate,portCode);
    	}catch(YssException ex){
    		throw new YssException("生成新股流通交易数据成本出错！", ex);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /**
     * 新股新债网下业务处理
     * @throws YssException
     */
    public void newIssueOTCDeal(ArrayList alCashTrans,
                                 ArrayList alIntegrated,
                                 ArrayList alRecPay,
                                 ArrayList alCashRecPay) throws YssException {
        ResultSet rs = null;
        StringBuffer bufSql = new StringBuffer();
        try {
        	//edit by songjie 2011.10.24 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B
            bufSql.append("SELECT a.*, b.FCatCode, b.FSubCatCode, b.FTradeCury, c.FAccountCode");
            bufSql.append(" FROM (SELECT * ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_NewIssueTrade"));
            bufSql.append(" WHERE FCheckState = 1 ");
            bufSql.append(" AND FTransDate = ").append(dbl.sqlDate(bargainDate));
            //add by songjie 2012.09.26 BUG 5854 QDV4海富通2012年09月25日02_B 根据已选组合处理数据
            bufSql.append(" and FPortCode in ( " + operSql.sqlCodes(this.portCode) + ")");
            bufSql.append(" AND FTradeTypeCode <> ").append(dbl.sqlString(YssOperCons.YSS_JYLX_SD)).append(") a");
            bufSql.append(" JOIN (SELECT FSecurityCode, FCatCode, FSubCatCode, FTradeCury ");
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Para_Security"));
            bufSql.append(" WHERE FCheckState = 1) b ON a.FSecurityCode = b.FSecurityCode ");
            bufSql.append(" left join (select FNum, case when FTradeTypeCode = '44' then ' ' else FCashAccCode end As FAccountCode");
            bufSql.append(" from ").append(pub.yssGetTableName("Tb_Data_NewIssueTrade"));
            bufSql.append(" WHERE FCheckState = 1 ");
            //bufSql.append(" AND FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_XGSG)).append(") c");
            //update by guolongchao 20111103 bug 3001 添加新债申购   // modified by yeshenghong story3995 20130204
            bufSql.append(" AND ((FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_XGSG)).append(" OR FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_XZSG)).append(")");
            bufSql.append(" OR (FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_WXZQ)).append(" AND FDirBallot = 1 ))) c ");
            bufSql.append(" ON (a.FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_ZFZQ));
            bufSql.append(" OR a.FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_WXZQ));
            bufSql.append(" OR a.FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_ZQFK));
            //modify by zhangfa 20100901 MS01682    网下新股新债做流通业务处理，已设置现金账户仍给出提示    QDV4赢时胜(测试)2010年09月01日01_B  
            bufSql.append(" OR a.FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_XGLT));
            bufSql.append(" OR a.FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_XZLT)); //update by guolongchao 20111103 bug 3001 添加新债流通
            bufSql.append(" OR a.FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_XZSG)); //update by guolongchao 20111103 bug 3001 添加新债申购
            //------------------------------------------------------------------------------------------------------------------------
            //modify by zhangfa 20100830 MS01651    申购数据有现金账户时，系统在做业务处理时仍会有提示    QDV4赢时胜(测试)2010年08月26日01_B   
            bufSql.append(" OR a.FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_XGSG)).append(")");
            //-----------------------------------------------------------------------------------------------------------------------
            bufSql.append(" and a.FNum = c.FNum");

            rs = dbl.queryByPreparedStatement(bufSql.toString());
            while(rs.next()){
                String sTradeTypeCode = rs.getString("FTradeTypeCode");
                //xuqiji 20100708 MS01362  网下新股新债中签业务处理时，获取不到现金账户时系统报错   QDV4国内(测试)2010年06月25日05_B --//   
                if(rs.getString("FAccountCode") == null){
                	throw new YssException("证券代码为【"+ rs.getString("FSecurityCode")+"】" +"\n"
                			+ "组合代码为【" + rs.getString("FPortCode")+"】" +"\n"
                			+ "交易类型为【"+sTradeTypeCode+"==>>网下中签】" +"\n"		
                			+ "以上描述的网下新股新债业务数据的现金账户没有关联上，请检查是否关联上申购数据！");
                }
                //------------------------------------end-----------------------------//
                if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_XGSG) ||
                	sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_XZSG) ||//update by guolongchao 20111103 bug 3001 添加新债申购
                    sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_ZQFK)) { //新股申购、返款
                    applyAndReturnCashDeal(alCashTrans, alCashRecPay, rs);
                } else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_ZFZQ) ||
                           sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_WXZQ)) { //增发中签 网下中签
                    lucklyDeal(alIntegrated, alRecPay, alCashRecPay, rs);
                } else if (sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_XGLT) || sTradeTypeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_XZLT)) { //新股流通       //update by guolongchao 20111103 bug 3001 添加新债流通
                    //edit by songjie 2011.11.01 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B
					issueDeal(alIntegrated, alRecPay, rs);
                }
            }
        } catch (Exception ex) {
            throw new YssException("新股新债网下业务处理出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 增发中签 网下中签业务处理
     * @param alIntegrated ArrayList
     * @param rs ResultSet
     * @throws YssException
     */
    private void lucklyDeal(ArrayList alIntegrated,
                            ArrayList alRecPay,
                            ArrayList alCashRecPay,
                            ResultSet rs) throws YssException, SQLException{
        SecIntegratedBean inteCost = null;
        SecPecPayBean secRecPay = null;
        CashPecPayBean recPay = null;

        double baseCuryRate = 0;
        double portCuryRate = 0;
		//add by songjie 2011.12.29 BUG 3517 QDV4赢时胜(上海开发部)2011年12月26日01_B
        String securityType = rs.getString("FSecurityType");
        try {
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
            baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FTransDate"),
                rs.getString("FTradeCury"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(bargainDate, rs.getString("FTradeCury"),
                                      rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();

            //-------------------------证券变动----------------------------//
            inteCost = new SecIntegratedBean();
            inteCost.setInvestType(rs.getString("FInvestType"));
            inteCost.setIInOutType(1);
            inteCost.setSSecurityCode(rs.getString("FSecurityCode"));
            inteCost.setSExchangeDate(YssFun.formatDate(rs.getDate("FTransDate"),
                "yyyy-MM-dd"));
            inteCost.setSOperDate(YssFun.formatDate(rs.getDate("FTransDate"),
                "yyyy-MM-dd"));
            //edit by songjie 2012.12.13 BUG 6610 QDV4南方2012年12月12日01_B 添加交易编号 作为关联编号
            inteCost.setSRelaNum(rs.getString("FNum"));
            inteCost.setSNumType("securitymanage"); //------ modify by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B  

            inteCost.setSTradeTypeCode(rs.getString("FTradeTypeCode"));

            inteCost.setSPortCode(rs.getString("FPortCode"));
            if (analy1) {
                inteCost.setSAnalysisCode1(rs.getString("FInvMgrCode"));
            } else {
                inteCost.setSAnalysisCode1(" ");
            }

            inteCost.setSAnalysisCode2(" ");

            inteCost.setSAnalysisCode3(" ");

            inteCost.setDAmount(rs.getDouble("FAmount"));

            inteCost.setDBaseCuryRate(baseCuryRate);
            inteCost.setDPortCuryRate(portCuryRate);
            //modify by zhouwei 20120416 根据通参设定，判断利息是否入成本
            CtlPubPara pubPara=new CtlPubPara();
            pubPara.setYssPub(pub);
            boolean costIncludeBondIns=pubPara.getCostIncludeInterestsOfZQ(rs.getString("FPortCode"));//债券利息是否入成本，false不入，true入
            if(!costIncludeBondIns){ //中签金额减去债券利息入成本
            	 inteCost.setDCost(YssD.sub(rs.getDouble("FMoney"), rs.getDouble("FBondIns")));
            }else{
            	 inteCost.setDCost(rs.getDouble("FMoney"));
            }     
            //----------end------------
            inteCost.setDBaseCost(this.getSettingOper().calBaseMoney(inteCost.getDCost(),
                baseCuryRate, 2));
            inteCost.setDPortCost(this.getSettingOper().calPortMoney(inteCost.getDCost(),
                baseCuryRate, portCuryRate,
                rs.getString("FTradeCury"), bargainDate, inteCost.getSPortCode(), 2));

            inteCost.setDMCost(inteCost.getDCost());
            inteCost.setDVCost(inteCost.getDCost());

            inteCost.setDMBaseCost(inteCost.getDBaseCost());
            inteCost.setDVBaseCost(inteCost.getDBaseCost());

            inteCost.setDMPortCost(inteCost.getDPortCost());
            inteCost.setDVPortCost(inteCost.getDPortCost());

            inteCost.checkStateId = 1;
            inteCost.setSTsfTypeCode("05");
            inteCost.setSSubTsfTypeCode("05" + rs.getString("FCatCode"));

            inteCost.setAttrClsCode(rs.getString("FAttrClsCode"));

            alIntegrated.add(inteCost);
            
            if(rs.getString("FDIRBALLOT").equals("1"))//add by yeshenghong 20130204 story3395 直接中签 不产生现金应收应付和资金调拨数据
            {
            	return;
            }

            //------------------------------现金应收应付---------------------------------------//
            recPay = new CashPecPayBean();
            recPay.setTradeDate(rs.getDate("FTransDate"));
            recPay.checkStateId = 1;
            recPay.setRelaNum(rs.getString("FNum"));
            recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE);
            recPay.setPortCode(rs.getString("FPortCode"));
            if (this.analy1) {
                recPay.setInvestManagerCode(rs.getString("FInvMgrCode"));
            } else{
                recPay.setInvestManagerCode(" ");
            }
            if (this.analy2) {
                recPay.setCategoryCode(rs.getString("FCatCode"));
            } else {
                recPay.setCategoryCode(" ");
            }

            recPay.setBrokerCode(" ");
            recPay.setCashAccCode(rs.getString("FAccountCode")); // 取的是申购中数据中的账户代码
            recPay.setCuryCode(rs.getString("FTradeCury"));
            recPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
            recPay.setSubTsfTypeCode("06AP_" + rs.getString("FCatCode"));

            //edit by songjie 2011.10.24 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B
            recPay.setInOutType(-1);//edit by songjie 2011.12.28 BUG 3517 QDV4赢时胜(上海开发部)2011年12月26日01_B
            recPay.setDataSource(0);
            recPay.setStockInd(1);
            
            //delete by songjie 2011.12.29 BUG 3517 QDV4赢时胜(上海开发部)2011年12月26日01_B
            //edit by songjie 2011.10.24 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B
            //recPay.setMoney(YssD.sub(getApplyMoney(rs), rs.getDouble("FMoney")));
            //add by songjie 2011.12.29 BUG 3517 QDV4赢时胜(上海开发部)2011年12月26日01_B
            recPay.setMoney(rs.getDouble("FMoney"));
            recPay.setBaseCuryRate(baseCuryRate);
            recPay.setPortCuryRate(portCuryRate);

            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                baseCuryRate, 2));;

            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                baseCuryRate, portCuryRate,
                rs.getString("FTradeCury"), bargainDate, recPay.getPortCode(), 2));

            alCashRecPay.add(recPay);
            //add by zhouwei 20120424 如果利息入成本，则不需要产生06FI_B的证券应收应付
            if(costIncludeBondIns){
            	return;
            }
            //-----------------------------应收债券利息---------------------------------------------------//
            //---edit by songjie 2011.12.28 BUG 3517 QDV4赢时胜(上海开发部)2011年12月26日01_B start---//
            if(securityType.equalsIgnoreCase("KZZ") || 
               securityType.equalsIgnoreCase("QYZ") ||
               securityType.equalsIgnoreCase("GZXQ")){
            	//---edit by songjie 2011.12.28 BUG 3517 QDV4赢时胜(上海开发部)2011年12月26日01_B end---//
            	secRecPay = new SecPecPayBean();
            	secRecPay.setInvestType(rs.getString("FInvestType"));
            	secRecPay.setTransDate(rs.getDate("FTransDate"));
            	secRecPay.setCheckState(1);
				secRecPay.setAttrClsCode(rs.getString("FAttrClsCode"));
				secRecPay.setRelaNum(rs.getString("FNum"));
				secRecPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE);
				secRecPay.setStrPortCode(rs.getString("FPortCode"));
				if (this.analy1) {
					secRecPay.setInvMgrCode(rs.getString("FInvMgrCode"));
				} else {
					secRecPay.setInvMgrCode(" ");
				}
				secRecPay.setBrokerCode(" ");
				secRecPay.setStrSecurityCode(rs.getString("FSecurityCode"));
				secRecPay.setStrCuryCode(rs.getString("FTradeCury"));
				secRecPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
				secRecPay.setStrSubTsfTypeCode("06FI_B");
            
				secRecPay.setMoney(rs.getDouble("FBondIns"));
				secRecPay.setBaseCuryRate(baseCuryRate);
				secRecPay.setPortCuryRate(portCuryRate);

				secRecPay.setMMoney(secRecPay.getMoney());
				secRecPay.setVMoney(secRecPay.getMoney());

				secRecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(secRecPay.getMoney(),
						baseCuryRate, 2));
				secRecPay.setMBaseCuryMoney(secRecPay.getBaseCuryMoney());
				secRecPay.setVBaseCuryMoney(secRecPay.getBaseCuryMoney());

				secRecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(secRecPay.getMoney(),
						baseCuryRate, portCuryRate,
						rs.getString("FTradeCury"), bargainDate, secRecPay.getStrPortCode(), 2));
				secRecPay.setMPortCuryMoney(secRecPay.getPortCuryMoney());
				secRecPay.setVPortCuryMoney(secRecPay.getPortCuryMoney());

				secRecPay.setPortCuryMoneyF(secRecPay.getPortCuryMoney());
				secRecPay.setBaseCuryMoneyF(secRecPay.getBaseCuryMoney());
				secRecPay.setMoneyF(secRecPay.getMoney());
				alRecPay.add(secRecPay);
            }

        } catch (Exception ex) {
            throw new YssException("处理中签业务出错！", ex);
        }
    }

    /**
     * add by songjie 211.10.24 
     * BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B
     * @param rs
     * @return
     * @throws YssException
     */
    private double getApplyMoney(ResultSet rs)throws YssException{
    	String strSql = "";
    	ResultSet rs1 = null;
    	double applyMoney = 0;
    	try{
    		strSql = " select a.* from " + pub.yssGetTableName("Tb_Data_NewIssueTrade") + 
    		" a WHERE a.FCheckState = 1 AND a.FTradeTypeCode = '40' and a.FNum = " + 
    		dbl.sqlString(rs.getString("FNum"));
    		
    		rs1 = dbl.openResultSet(strSql);
    		if(rs1.next()){
    			applyMoney = rs1.getDouble("FMONEY");
    		}
    		
    		return applyMoney;
    	}catch(Exception ex){
    		throw new YssException("获取网下新股申购数据出错！", ex);
    	}finally{
    		dbl.closeResultSetFinal(rs1);
    	}
    }
    
    /**
     * 中签和返款业务处理
     * @param alCashTrans ArrayList
     * @param alRecPay ArrayList
     * @param rs ResultSet
     * @throws YssException
     */
    private void applyAndReturnCashDeal(ArrayList alCashTrans,
                                        ArrayList alCashRecPay,
                                        ResultSet rs) throws YssException{
        CashPecPayBean recPay = null;
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        ArrayList alSubTrans = new ArrayList();
        double baseCuryRate = 0;
        double portCuryRate = 0;
        try {
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
            baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FTransDate"),
                rs.getString("FTradeCury"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(bargainDate, rs.getString("FTradeCury"),
                                      rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();

            //--------------------------------资金调拨----------------------------------------//
            transfer = new TransferBean();
            transfer.setCheckStateId(1);
            transfer.setDtTransDate(rs.getDate("FTransDate"));
            transfer.setDtTransferDate(rs.getDate("FTransDate"));
            transfer.setStrPortCode(rs.getString("FPortCode"));
            transfer.setStrSecurityCode(rs.getString("FSecurityCode"));
            transfer.setStrSubTsfTypeCode("05" + rs.getString("FCatCode"));
            transfer.setStrTsfTypeCode("05");
            transfer.setFRelaNum(rs.getString("FNum"));
            transfer.setFNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE);
            transfer.setStrAttrClsCode(" ");
            this.cashTradeNum += (transfer.getFRelaNum() + ",");

            transferset = new TransferSetBean();
            //update by guolongchao 20111103 bug 3001 添加新债申购
            if(rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_XGSG)||rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_XZSG)){//申购的资金方向为流出
                transferset.setIInOut(-1);
            } else {//返款的资金方向为流入
                transferset.setIInOut(1);
            }
            transferset.setDBaseRate(baseCuryRate);
            transferset.setDPortRate(portCuryRate);
            transferset.setDMoney(rs.getDouble("FMoney"));
            if (this.analy1) {
                transferset.setSAnalysisCode1(rs.getString("FInvMgrCode"));
            } else {
                transferset.setSAnalysisCode1(" ");
            }
            transferset.setSAnalysisCode2(" ");
            transferset.setSAnalysisCode3(" ");

            transferset.setSCashAccCode(rs.getString("FCashAccCode"));
            transferset.setSPortCode(rs.getString("FPortCode"));
            alSubTrans.add(transferset);
            transfer.setSubTrans(alSubTrans);
            alCashTrans.add(transfer);

           //------------------------------现金应收应付---------------------------------------//
           recPay = new CashPecPayBean();
           recPay.setTradeDate(rs.getDate("FTransDate"));
           recPay.checkStateId = 1;
           recPay.setRelaNum(rs.getString("FNum"));
           recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE);
           recPay.setPortCode(rs.getString("FPortCode"));
           if (this.analy1) {
               recPay.setInvestManagerCode(rs.getString("FInvMgrCode"));
           } else {
               recPay.setInvestManagerCode(" ");
           }
           if (this.analy2){
               recPay.setCategoryCode(rs.getString("FCatCode"));
           } else {
               recPay.setCategoryCode(" ");
           }
           recPay.setBrokerCode(" ");


           recPay.setCuryCode(rs.getString("FTradeCury"));
            //update by guolongchao 20111103 bug 3001 添加新债申购
           if(rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_XGSG)||rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_XZSG)){//申购时产生应收款
               recPay.setCashAccCode(rs.getString("FCashAccCode"));
               //---edit by songjie 2011.12.27 BUG 3517 QDV4赢时胜(上海开发部)2011年12月26日01_B start---//
               recPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
               recPay.setSubTsfTypeCode("06AP_" + rs.getString("FCatCode"));
               recPay.setInOutType(1);//申购时产生方向为流入的应收申购款
               //---edit by songjie 2011.12.27 BUG 3517 QDV4赢时胜(上海开发部)2011年12月26日01_B end---//
           } else{
               recPay.setCashAccCode(rs.getString("FAccountCode"));
             //---edit by songjie 2011.12.27 BUG 3517 QDV4赢时胜(上海开发部)2011年12月26日01_B start---//
               recPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
               recPay.setSubTsfTypeCode("06AP_" + rs.getString("FCatCode"));
               recPay.setInOutType(-1);//返款时产生方向为流出的应收申购款
             //---edit by songjie 2011.12.27 BUG 3517 QDV4赢时胜(上海开发部)2011年12月26日01_B end---//
           }           
           recPay.setDataSource(0);
           recPay.setStockInd(1);
           recPay.setMoney(rs.getDouble("FMoney"));
           recPay.setBaseCuryRate(baseCuryRate);
           recPay.setPortCuryRate(portCuryRate);
           recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
               baseCuryRate, 2));
           recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
               baseCuryRate, portCuryRate,
               rs.getString("FTradeCury"), bargainDate, recPay.getPortCode(), 2));

           alCashRecPay.add(recPay);
        } catch (Exception ex) {
            throw new YssException("处理返款业务出错！", ex);
        }
    }

    private void issueDeal(ArrayList alIntegrated,
                           ArrayList alRecPay,
                           ResultSet rs) throws YssException{
        SecIntegratedBean secIntegrate = null;
        SecPecPayBean recPay = null;
        String sCatCode = "";//证券品种
        double baseCuryRate = 0;
        double portCuryRate = 0;
        try {
            sCatCode = rs.getString("FCatCode");
            ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
                "avgcostcalculate");

            costCal.initCostCalcutate(rs.getDate("FTransDate"),
                                      rs.getString("FPortCode"),
                                      rs.getString("FInvMgrCode"),
                                      "",
                                      rs.getString("FAttrClsCode"));
            costCal.setYssPub(pub);
            //获取冲减的成本
            YssCost cost = costCal.getCarryCost(rs.getString("FSecurityCode"),
                                                rs.getDouble("FAmount"),
                                                rs.getString("FNum"),
                                                null,//null参数：add by guolongchao 20110815  STORY #1207  添加结算日期参数
                                                "newissue",
                                                rs.getString("FTradeTypeCode"));//YssOperCons.YSS_JYLX_XGLT); update by guolongchao 20111103 bug 3001 将新股流通的交易类型改为rs.getString("FTradeTypeCode")
            costCal.roundCost(cost, 2);
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);

            baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FTransDate"),
                rs.getString("FTradeCury"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(bargainDate, rs.getString("FTradeCury"),
                                      rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();

            //-------------冲减的成本-------------//
            secIntegrate = new SecIntegratedBean();

            secIntegrate.setIInOutType(-1);
            secIntegrate.setInvestType(rs.getString("FInvestTYpe"));
            secIntegrate.setSExchangeDate(YssFun.formatDate(rs.getDate(
                "FTransDate"),
                "yyyy-MM-dd"));
            secIntegrate.setSOperDate(YssFun.formatDate(rs.getDate("FTransDate"),
                "yyyy-MM-dd"));
            //edit by songjie 2012.12.13 BUG 6610 QDV4南方2012年12月12日01_B 保存交易编号数据到关联编号
            secIntegrate.setSRelaNum(rs.getString("FNum"));
            secIntegrate.setSNumType("securitymanage"); //------ modify by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B  

            secIntegrate.setSTradeTypeCode(rs.getString("FTradeTypeCode"));

            secIntegrate.setSPortCode(rs.getString("FPortCode"));
            if (this.analy1) {
                secIntegrate.setSAnalysisCode1(rs.getString("FInvMgrCode"));
            } else {
                secIntegrate.setSAnalysisCode1(" ");
            }
            secIntegrate.setSAnalysisCode2(" ");
            secIntegrate.setSAnalysisCode3(" ");

            secIntegrate.setDAmount(YssD.mul(rs.getDouble("FAmount"), secIntegrate.getIInOutType()));
            secIntegrate.setSSecurityCode(rs.getString("FSecurityCode"));

            secIntegrate.setDCost(YssD.mul(cost.getCost(), secIntegrate.getIInOutType()));
            secIntegrate.setDMCost(YssD.mul(cost.getCost(), secIntegrate.getIInOutType()));
            secIntegrate.setDVCost(YssD.mul(cost.getCost(), secIntegrate.getIInOutType()));

            secIntegrate.setDBaseCost(YssD.mul(cost.getBaseCost(), secIntegrate.getIInOutType()));
            secIntegrate.setDMBaseCost(YssD.mul(cost.getBaseCost(), secIntegrate.getIInOutType()));
            secIntegrate.setDVBaseCost(YssD.mul(cost.getBaseCost(), secIntegrate.getIInOutType()));

            secIntegrate.setDPortCost(YssD.mul(cost.getPortCost(), secIntegrate.getIInOutType()));
            secIntegrate.setDMPortCost(YssD.mul(cost.getPortCost(), secIntegrate.getIInOutType()));
            secIntegrate.setDVPortCost(YssD.mul(cost.getPortCost(), secIntegrate.getIInOutType()));

            secIntegrate.setDPortCuryRate(portCuryRate);

            secIntegrate.setDBaseCuryRate(baseCuryRate);

            secIntegrate.checkStateId = 1;
            secIntegrate.setSTsfTypeCode("05");
            secIntegrate.setSSubTsfTypeCode("05" + rs.getString("FCatCode"));
            secIntegrate.setAttrClsCode(rs.getString("FAttrClsCode"));
            alIntegrated.add(secIntegrate);

            //--------------转到流通的证券变动---------------//
            secIntegrate = new SecIntegratedBean();

            secIntegrate.setIInOutType(1);
            secIntegrate.setInvestType(rs.getString("FInvestType"));
            secIntegrate.setSExchangeDate(YssFun.formatDate(rs.getDate(
                "FTransDate"),
                "yyyy-MM-dd"));
            secIntegrate.setSOperDate(YssFun.formatDate(rs.getDate("FTransDate"),
                "yyyy-MM-dd"));
            //edit by songjie 2012.12.13 BUG 6610 QDV4南方2012年12月12日01_B 保存交易编号数据到关联编号
            secIntegrate.setSRelaNum(rs.getString("FNum"));
            secIntegrate.setSNumType("securitymanage");  //------ modify by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B 

            secIntegrate.setSTradeTypeCode(rs.getString("FTradeTypeCode"));

            secIntegrate.setSPortCode(rs.getString("FPortCode"));
            if (this.analy1) {
                secIntegrate.setSAnalysisCode1(rs.getString("FInvMgrCode"));
            } else {
                secIntegrate.setSAnalysisCode1(" ");
            }
            secIntegrate.setSAnalysisCode2(" ");
            secIntegrate.setSAnalysisCode3(" ");

            secIntegrate.setDAmount(rs.getDouble("FAmount"));
            secIntegrate.setSSecurityCode(rs.getString("FSecurityCode"));

            secIntegrate.setDCost(rs.getDouble("FMoney"));
            secIntegrate.setDMCost(secIntegrate.getDCost());
            secIntegrate.setDVCost(secIntegrate.getDCost());

            secIntegrate.setDBaseCost(this.getSettingOper().calBaseMoney(secIntegrate.getDCost(),
                baseCuryRate, 2));
            secIntegrate.setDMBaseCost(secIntegrate.getDBaseCost());
            secIntegrate.setDVBaseCost(secIntegrate.getDBaseCost());

            secIntegrate.setDPortCost(this.getSettingOper().calPortMoney(secIntegrate.getDCost(),
                baseCuryRate, portCuryRate,
                rs.getString("FTradeCury"), bargainDate, secIntegrate.getSPortCode(), 2));
            secIntegrate.setDMPortCost(secIntegrate.getDPortCost());
            secIntegrate.setDVPortCost(secIntegrate.getDPortCost());

            secIntegrate.setDPortCuryRate(portCuryRate);

            secIntegrate.setDBaseCuryRate(baseCuryRate);

            secIntegrate.checkStateId = 1;
            secIntegrate.setSTsfTypeCode("05");
            secIntegrate.setSSubTsfTypeCode("05" + rs.getString("FCatCode"));
            //edit by songjie 2011.10.25 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B 流通的新股的所属分类应为空
            secIntegrate.setAttrClsCode(" "); //转到公开发行新股
            
            alIntegrated.add(secIntegrate);

            //如果证券品种是债券则需要将利息转到公开发行
            if(sCatCode.equalsIgnoreCase("FI")){
                //冲减的债券利息
                SecPecPayBean carryPay = costCal.getCarryRecPay(rs.getString("FSecurityCode"),
                    rs.getDouble("FAmount"),
                    rs.getString("FNum"),
                    "newissue",
                    rs.getString("FTradeTypeCode"),//YssOperCons.YSS_JYLX_XGLT); update by guolongchao 20111103 bug 3001 将新股流通的交易类型改为rs.getString("FTradeTypeCode")
                    YssOperCons.YSS_ZJDBLX_Rec,
                    YssOperCons.YSS_ZJDBLX_Rec + rs.getString("FCatCode"));

                //----------冲减的利息-----------//
                if (carryPay == null) {
                    return;
                }
                recPay = new SecPecPayBean();
                recPay.setTransDate(rs.getDate("FTransDate"));
                recPay.setInvestType(rs.getString("FInvestType"));
                recPay.setCheckState(1);
                recPay.setInOutType( -1);
                recPay.setAttrClsCode(rs.getString("FAttrClsCode"));
                recPay.setRelaNum(rs.getString("FNum"));
                recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE);
                recPay.setStrPortCode(rs.getString("FPortCode"));
                if (this.analy1) {
                    recPay.setInvMgrCode(rs.getString("FInvMgrCode"));
                } else {
                    recPay.setInvMgrCode(" ");
                }
                recPay.setBrokerCode(" ");
                recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                recPay.setStrCuryCode(rs.getString("FTradeCury"));
                recPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                recPay.setStrSubTsfTypeCode("06FI_B");

                recPay.setMoney(carryPay.getMoney());
                recPay.setBaseCuryRate(baseCuryRate);
                recPay.setPortCuryRate(portCuryRate);

                recPay.setMMoney(carryPay.getMMoney());
                recPay.setVMoney(carryPay.getVMoney());

                recPay.setBaseCuryMoney(carryPay.getBaseCuryMoney());
                recPay.setMBaseCuryMoney(carryPay.getMBaseCuryMoney());
                recPay.setVBaseCuryMoney(carryPay.getVBaseCuryMoney());

                recPay.setPortCuryMoney(carryPay.getPortCuryMoney());
                recPay.setMPortCuryMoney(carryPay.getMPortCuryMoney());
                recPay.setVPortCuryMoney(carryPay.getVPortCuryMoney());

                recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
                recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
                recPay.setMoneyF(recPay.getMoney());
                alRecPay.add(recPay);

                //----------转到流通的利息-----------//
                recPay = new SecPecPayBean();
                recPay.setTransDate(rs.getDate("FTransDate"));
                recPay.setInvestType(rs.getString("FInvestType"));
                recPay.setCheckState(1);
                recPay.setInOutType(1);
                recPay.setAttrClsCode(" ");//modify by zhouwei 20120413 YssOperCons.YSS_SXFL_PONS分类变成空值
                recPay.setRelaNum(rs.getString("FNum"));
                recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE);
                recPay.setStrPortCode(rs.getString("FPortCode"));
                if (this.analy1) {
                    recPay.setInvMgrCode(rs.getString("FInvMgrCode"));
                } else {
                    recPay.setInvMgrCode(" ");
                }
                recPay.setBrokerCode(" ");
                recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                recPay.setStrCuryCode(rs.getString("FTradeCury"));
                recPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                recPay.setStrSubTsfTypeCode("06FI_B");

                recPay.setMoney(carryPay.getMoney());
                recPay.setBaseCuryRate(baseCuryRate);
                recPay.setPortCuryRate(portCuryRate);

                recPay.setMMoney(carryPay.getMMoney());
                recPay.setVMoney(carryPay.getVMoney());

                recPay.setBaseCuryMoney(carryPay.getBaseCuryMoney());
                recPay.setMBaseCuryMoney(carryPay.getMBaseCuryMoney());
                recPay.setVBaseCuryMoney(carryPay.getVBaseCuryMoney());

                recPay.setPortCuryMoney(carryPay.getPortCuryMoney());
                recPay.setMPortCuryMoney(carryPay.getMPortCuryMoney());
                recPay.setVPortCuryMoney(carryPay.getVPortCuryMoney());

                recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
                recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
                recPay.setMoneyF(recPay.getMoney());
                alRecPay.add(recPay);
            }
        } catch (Exception ex) {
            throw new YssException("处理流通业务出错！", ex);
        }
    }

    /**
    * 将证券变动数据插入综合业务表
    * @param list ArrayList：装载证券变动数据
    * @throws YssException
    */
   private void insertData(ArrayList list) throws YssException {
       String sqlStr = "";
       PreparedStatement pst = null;
       boolean bTrans = false;
       Connection conn = dbl.loadConnection();
       String sNewNum = "";
       SecIntegratedBean secIntegrade = null;

       dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Integrated"));
       sqlStr = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") +
           " where " +
           " FTradeTypeCode in ('41','43','44','46')" +
           " and FPortCode in(" + this.portCode + ")" +
           " and FOperDate = " + dbl.sqlDate(bargainDate) +
           " AND FTSFTYPECODE = '05'";
       //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
       OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
       integrateAdmin.setYssPub(pub);
       //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
       try {
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(sqlStr);
       } catch (Exception e) {
           throw new YssException("删除综合业务出错！", e);
       }
       sqlStr = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
           " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
           " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
           " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
           " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,FCheckState,FCreator,FCreateTime, FTSFTYPECODE, FSUBTSFTYPECODE,FAttrClsCode) " +
           " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

       try {
           pst = dbl.getPreparedStatement(sqlStr);

           for (int i = 0; i < list.size(); i++) {
               secIntegrade = (SecIntegratedBean) list.get(i);
               sNewNum = "E" +
                   YssFun.formatDate(this.bargainDate,
                                     "yyyyMMdd") +
                   dbFun.getNextInnerCode(pub.yssGetTableName(
                       "Tb_Data_Integrated"),
                                          dbl.sqlRight("FNUM", 6),
                                          "000001",
                                          " where FExchangeDate=" +
                                          dbl.sqlDate(this.bargainDate) +
                                          " or FExchangeDate=" +
                                          dbl.sqlDate("9998-12-31"));
               pst.setString(1, sNewNum);
               //edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
               pst.setString(2, integrateAdmin.getKeyNum());
               pst.setInt(3, secIntegrade.getIInOutType());
               pst.setString(4, secIntegrade.getSSecurityCode());
               pst.setDate(5, YssFun.toSqlDate(secIntegrade.getSExchangeDate()));
               pst.setDate(6, YssFun.toSqlDate(secIntegrade.getSOperDate()));
               pst.setString(7, secIntegrade.getSTradeTypeCode());
               pst.setString(8, secIntegrade.getSRelaNum());
               pst.setString(9, " ");
               pst.setString(10, secIntegrade.getSPortCode());
               pst.setString(11, secIntegrade.getSAnalysisCode1());
               pst.setString(12, secIntegrade.getSAnalysisCode2());
               pst.setString(13, secIntegrade.getSAnalysisCode3());
               pst.setDouble(14, secIntegrade.getDAmount());
               pst.setDouble(15, secIntegrade.getDCost());
               pst.setDouble(16, secIntegrade.getDMCost());
               pst.setDouble(17, secIntegrade.getDVCost());
               pst.setDouble(18, secIntegrade.getDBaseCost());
               pst.setDouble(19, secIntegrade.getDMBaseCost());
               pst.setDouble(20, secIntegrade.getDVBaseCost());
               pst.setDouble(21, secIntegrade.getDPortCost());
               pst.setDouble(22, secIntegrade.getDMPortCost());
               pst.setDouble(23, secIntegrade.getDVPortCost());
               pst.setDouble(24, secIntegrade.getDBaseCuryRate());
               pst.setDouble(25, secIntegrade.getDPortCuryRate());
               pst.setString(26, "");
               pst.setString(27, "");
               pst.setInt(28, secIntegrade.checkStateId);
               pst.setString(29, pub.getUserCode());
               pst.setString(30, YssFun.formatDatetime(new java.util.Date()));
               pst.setString(31, secIntegrade.getSTsfTypeCode());
               pst.setString(32, secIntegrade.getSSubTsfTypeCode());
               pst.setString(33, secIntegrade.getAttrClsCode());

               pst.executeUpdate();
           }
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
       } catch (Exception e) {
           throw new YssException("插入综合业务出错！", e);
       } finally {
           dbl.endTransFinal(conn, bTrans);
       }
   }

}
