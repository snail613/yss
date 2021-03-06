package com.yss.main.operdeal.income.paid;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachRateOper;
import com.yss.log.SingleLogOper;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.dao.IYssConvert;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.pojo.dayfinish.AccPaid;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class PaidAccIncome
    extends BaseIncomePaid {
    public PaidAccIncome() {
    }

    /**
     * calculateIncome
     *
     * @param bean IYssConvert
     */
    public void calculateIncome(IYssConvert bean) {
    }

    /**
     * getIncomes
     *
     * @return ArrayList
     */
    public ArrayList getIncomes() throws YssException {
        ArrayList alResult = new ArrayList();
        try {
            alResult.addAll(getDayIncomes(dDate));
            return alResult;
        } catch (Exception e) {
            throw new YssException("获取存款利息数据出错", e);
        }
    }

    /**
     *
     * @param dDate 业务日期
     * @param cashAccountCode 现金账户代码
     * @return
     * @throws YssException
     */
    public ArrayList getDayIncomes(java.util.Date dDate) throws YssException {
        ArrayList alPaid = new ArrayList();
        String strSql = "";
        ResultSet rs = null;
        AccPaid paid = null;
        
        double baseCuryRate;
        double portCuryRate;
        boolean analy1 = false;
        boolean analy2 = false;
        
        String strConSql1=null;
        String strConSql2=null;
        String strConSql3=null;
        //zhouxiang 20100830 MS01132 QDV4赢时胜上海2010年04月27日
        String paidTypeFee="";//将为"07RE01"类型的拆分为"07RE01"，"07RE02"，"07RE03"
        if("07RE01".equals(this.paidType)){
        	paidTypeFee="'07RE01','07RE02','07RE03'";
        }        
        else if("07FE02".equals(this.paidType)){
        	paidTypeFee="'07FE02','07FE01','07FE03'";
        }
        else if("07TF".equals(this.paidType)){//add by zhouwei 20120321 固定交易费用
            	paidTypeFee="'07TF_EQ','07TF_FI','07TF_DE','07TF'";
        }else
        	paidTypeFee=dbl.sqlString(this.paidType);
        //--------------end ----------MS01132----------
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        String verType = pubpara.getNavType();
        //-----------------------------------------------------------
        String auxiCuryCode = "";
        String auxiCuryName = "";
        double auxiBaseRate = 0;
        try {
        	//fanghhaoln 20100527 MS01132 QDV4赢时胜上海2010年04月27日03_AB
        	//CtlPubPara pubPara = new CtlPubPara();
            //pubPara.setYssPub(pub);
            //hmSettleFee = pubPara.getIntBakSettleFee();
            //hmTradeFee = pubPara.getIntBakTradeFee();
            //hmBankFee = pubPara.getIntBakBankFee();
            //--------------end ----------MS01132----------
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            BaseStgStatDeal secstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("CashPayRec");
            secstgstat.setYssPub(pub);
            
       
            	 secstgstat.stroageStat(YssFun.addDay(dDate, -1),
                         YssFun.addDay(dDate, -1),
                         operSql.sqlCodes(portCodes));
            
            //by guyichuan 20110520 STORY #561 支付贷款利息时，只能查询账户类型为“存贷款账户”昨日余额为负数的账户
            	 if("07LI".equals(this.paidType)){		//07LI:支付贷款利息
            		 strConSql1=",t.FAccBalance";		//帐户余额
            		 strConSql2=" and FAccType='01' "; //账户类型为“存贷款账户”
            		 strConSql3=" join (select * from "+pub.yssGetTableName("Tb_Stock_Cash")+" where "+operSql.sqlStoragEve(dDate)+") t"
            		 			+" on t.fcashacccode=a.FCashAccCode and t.fportcode=a.FPortCode";
            		 			//+" and t.FStorageDate ="+dbl.sqlDate(YssFun.addDay(dDate, -1));
            	 }else{
            		  strConSql1="";
            	      strConSql2="";
            	      strConSql3="";
            	 }
            //----end-STORY #561-------
            	 
            	 if("06DE".equals(this.paidType)) 	//Add by yeshenghong 20120216 story 2076
            	 {
            		 strConSql1 = ", c.FAuxiCashAccCode, d.FCashAccName as FAuxiCashAccName, d.fcurycode as FAuxiCuryCode ";
            	     strConSql3 = " left join (select FAuxiCashAccCode,FCashAccCode,FPortCode from " +
                     pub.yssGetTableName("Tb_Para_Cashacclink")  + 
                     " where FTradeTypeCode = 'J93' and FCheckState = 1 )c on a.FCashAccCode = c.FCashAccCode and a.FPortCode = c.FPortCode " +
                     " left join (select FCashAccCode, FCashAccName,FCuryCode from " +
                     pub.yssGetTableName("Tb_Para_CashAccount") + 
                     " where FCheckState = 1) d on c.FAuxiCashAccCode = d.fcashacccode";
            	 }else
            	 {
            		strConSql1="";
           	      	strConSql3="";
            	 }
            strSql =
                "select a. *, port.FPortName,port.FPortCury, inv.FInvMgrName,cat.FCatName," +
                "tsf.FTsfTypeName,subtsf.FSubTsfTypeName,cury.FCuryName,acc.FCashAccName,";
                if(!"07TF".equals(this.paidType)){
                	strSql+="sav.FMatureDate,";
                }else{
                	strSql+=dbl.sqlDate(dDate)+" as FMatureDate,";
                }		
                strSql+="h.fassetgroupcode, h.fassetgroupname " + //MS00001《QDV4.1赢时胜（上海）2009年4月20日01_A》fanghaoln 20090514 跨组合群国内项目
                strConSql1+ //by guyichuan 20110520 STORY #561
                " from (select * from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " where FTsfTypeCode = " +
                //MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
                //2009.07.03 蒋锦 修改 资金调拨类型取资金调拨子类型的前两位，不再定死为 06 子类型是从前台传过来有可能是 07
                dbl.sqlString(paidType .length() > 2? paidType.substring(0, 2) : YssOperCons.YSS_ZJDBLX_Rec) + // '06'
                " and FSubTsfTypeCode in (" +paidTypeFee+")"  + //edit by jc
                " and " + operSql.sqlStoragEve(dDate) +
          
                " and FPortCode in (" +operSql.sqlCodes(portCodes) + ")) a " + //------ modify by wangzuochun 2010.09.27  MS01778    现金利息收支，勾选多个操作组合查询报错    QDV4赢时胜(测试)2010年09月20日1_B   
                //-------------------------------------------
                " left join (select FPortCode, FPortName,FPortCury from " +
                pub.yssGetTableName("tb_para_portfolio") +
                " where FCheckState = 1) port on a.FPortCode = port.FPortCode " +
                //----------------------------------------
                " left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FCheckState = 1) inv on a.FAnalysisCode1 = inv.FInvMgrCode " +
                //----------------------------------------
                " left join (select FCatCode, FCatName from Tb_Base_Category" +
                " where FCheckState = 1) cat on a.FAnalysisCode2 = cat.FCatCode " +
                //----------------------------------------
                " left join (select FTsfTypeCode, FTsfTypeName from tb_base_transfertype " +
                " where FCheckState = 1) tsf on a.FTsfTypeCode = tsf.FTsfTypeCode " +
                //----------------------------------------
                " left join (select FSubTsfTypeCode, FSubTsfTypeName from tb_base_subtransfertype " +
                " where FCheckState = 1) subtsf on a.FSubTsfTypeCode = subtsf.FSubTsfTypeCode" +
                //----------------------------------------
                " left join (select FCuryCode, FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) cury on a.FCuryCode = cury.FCuryCode" +
                //----------------------------------------
                " left join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1"+
                strConSql2+   //by guyichuan 20110520 STORY #561
                ") acc on a.FCashAccCode = acc.FCashAccCode" ;
                if(!"07TF".equals(this.paidType)){//固定交易费用的支付不去关联存款业务数据的到期日  edit by zhouwei 20120322
                	strSql+= " left join (select FCashAccCode,FMatureDate,FPortCode,FAnalysisCode1,FAnalysisCode2 from " + //增加分析代码1,去除重复的数据.sj edit 20080710
	                pub.yssGetTableName("Tb_Cash_SavingInAcc") +
	                " where FCheckState = 1 and FPortCode in (" +
	                operSql.sqlCodes(portCodes) +
	                ")) sav " +
	                " on a.FCashAccCode = sav.FCashAccCode and a.FPortCode = sav.FPortCode " +
	                (analy1 ? " and a.FAnalysisCode1 = sav.FAnalysisCode1 " : "") + //增加分析代码1,去除重复的数据.sj edit 20080710
	                (analy2 ? " and a.FAnalysisCode2 = sav.FAnalysisCode2 " : "") ;//增加分析代码2,去除重复的数据.sj edit 20080710
	                // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
	                //=====================================================================================
                }
                strSql+=" left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
                pub.getPrefixTB() + "' "
              	//=====================
            	+strConSql3;	//by guyichuan 20110520 STORY #561
            
            //if (!this.isAll.equalsIgnoreCase("true")) {
                //strSql = strSql + " where FMatureDate = " + dbl.sqlDate(this.dDate);
            //}
            if(!"07TF".equals(this.paidType)){
			    // --- MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj -----------------------------------------------------//
	            //还原太平的自动付息功能，合并太平版本代码 by leeyu 20100811
	            if(verType.equalsIgnoreCase("new")){
	            	strSql = strSql + " where FMatureDate is null";//获取所有非定存的数据
	            }else{
	            	if (!this.isAll.equalsIgnoreCase("true")) {
	                    strSql = strSql + " where FMatureDate = " + dbl.sqlDate(this.dDate);
	                }
	            }
	            //还原太平的自动付息功能，合并太平版本代码 by leeyu 20100811
				//---------------------------------------------------------------------------------------------------------
	//            }
            }
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788

            while (rs.next()) {
            	if("07LI".equals(this.paidType)){//by guyichuan 20110520 STORY #561 余额为负才查询得到
            		if(rs.getDouble("FAccBalance")>=0)
            			continue;
            	}
            	
                paid = new AccPaid(pub);
                // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090526
                paid.setAssetGroupCode(rs.getString("fassetgroupcode"));
                paid.setAssetGroupName(rs.getString("fassetgroupname"));
                //---------------------------------------------------------------------------------------------
                paid.setDDate(dDate);
                paid.setPortCode(rs.getString("FPortCode"));
                paid.setPortName(rs.getString("FPortName"));
                paid.setInvmgrCode(rs.getString("FAnalysisCode1") + "");
                paid.setInvmgrName(rs.getString("FInvMgrName"));
                paid.setCatCode(rs.getString("FAnalysisCode2") + "");
                paid.setCatName(rs.getString("FCatName"));
                paid.setTsfTypeCode(rs.getString("FTsfTypeCode"));
                paid.setTsfTypeName(rs.getString("FTsfTypeName"));
                paid.setSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
                paid.setSubTsfTypeName(rs.getString("FSubTsfTypeName"));
                paid.setCuryCode(rs.getString("FCuryCode"));
                paid.setCuryName(rs.getString("FCuryName"));
                paid.setCashAccCode(rs.getString("FCashAccCode"));
                paid.setCashAccName(rs.getString("FCashAccName"));
                
                paid.setMoney(rs.getDouble("FBal"));
          
                paid.setMatureDate(rs.getDate("FMatureDate"));
                //=========================================================================
                //付息的基础货币、组合货币应该是按照当日的汇率来计算的。 fazmm20070926
                //当天的基础汇率
                baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FCuryCode"), paid.getPortCode(),
                    YssOperCons.YSS_RATE_BASE);
                //当天的组合汇率
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                          paid.getPortCode());
                portCuryRate = rateOper.getDPortRate();
                
                if(this.paidType.equals("06DE")) 	//Add by yeshenghong 20120216 story 2076
                {
                	paid.setChangeCashAccCode(rs.getString("FAuxiCashAccCode"));
                	paid.setChangeCashAccName(rs.getString("FAuxiCashAccName"));
                	auxiCuryCode = rs.getString("FAuxiCuryCode");
                	auxiCuryName = rs.getString("FAuxiCashAccName");
                	if(auxiCuryCode!=null&&!auxiCuryCode.equals(rs.getString("FCuryCode")))
                	{
                		auxiBaseRate = this.getSettingOper().getCuryRate(dDate,
                                rs.getString("FAuxiCuryCode"), paid.getPortCode(),
                                YssOperCons.YSS_RATE_BASE);
                	}
                }
                //-----------------------------------------------------------------------------------
                //--------------------------获取原币的数据位数。再通过它来设置其它金额的数据保留位数。为了冲抵应收的金额。 sj edit 20080807 暂无 bug -- //
                int Digit = this.getSettingOper().getRoundDigit(paid.getMoney());
                paid.setBaseMoney(this.getSettingOper().calBaseMoney(paid.getMoney(),
                    baseCuryRate, Digit));
                paid.setPortMoney(this.getSettingOper().calPortMoney(paid.getMoney(),
                    baseCuryRate, portCuryRate,
                    //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    rs.getString("FCuryCode"), dDate, paid.getPortCode(), Digit));
                
                //--------------------------------------------------------------------------------------------------------------------------//
                paid.setBaseCuryRate(baseCuryRate);
                paid.setPortCuryRate(portCuryRate);
                paid.setLx(paid.getMoney());
                if(this.paidType.equals("06DE")&&auxiBaseRate!=0&&!auxiCuryCode.equals(paid.getCuryCode())) //add by yeshenghong 20120206 story 2076 当账户不一致时折算
                {
                	double money = this.getSettingOper().calBaseMoney(paid.getMoney(),
                			baseCuryRate/auxiBaseRate, Digit);
                	paid.setLx(paid.getMoney());
                	paid.setMoney(money);
                	paid.setCuryCode(auxiCuryCode);
                	paid.setCuryName(auxiCuryName);
                	paid.setBaseCuryRate(auxiBaseRate);
                }
                //==========================================================================
              //fanghhaoln 20100527 MS01132 QDV4赢时胜上海2010年04月27日03_AB
                //if(paid.getMoney()>0){
                    alPaid.add(paid);
                //}
                //----------------------------end-------------MS01132---------------------------
            }
         
            return alPaid;
        }

        catch (Exception e) {
            throw new YssException("取应收利息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    
    /**
     * add by huangqirong 2012-04-17 story #2326
     * @param dDate Date
     * @return ArrayList
     * @throws YssException
     */
    public AccPaid getSingleIncomes(String portCode , java.util.Date dDate, String accCount , String curyCode ) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        AccPaid paid = null;
        
        double baseCuryRate;
        double portCuryRate;
        boolean analy1 = false;
        boolean analy2 = false;
        
        String strConSql1=null;
        String strConSql2=null;
        String strConSql3=null;
        String paidTypeFee="";//将为"07RE01"类型的拆分为"07RE01"，"07RE02"，"07RE03"
        if("07RE01".equals(this.paidType)){
        	paidTypeFee="'07RE01','07RE02','07RE03'";
        }        
        else if("07FE02".equals(this.paidType)){
        	paidTypeFee="'07FE02','07FE01','07FE03'";
        }
        else if("07TF".equals(this.paidType)){//固定交易费用
            	paidTypeFee="'07TF_EQ','07TF_FI','07TF_DE','07TF'";
        }else
        	paidTypeFee=dbl.sqlString(this.paidType);
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        String verType = "";
        
        String auxiCuryCode = "";
        String auxiCuryName = "";
        double auxiBaseRate = 0;
        
        try {

        	verType = pubpara.getNavType();
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash"); //判断分析代码存不存在
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            BaseStgStatDeal secstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("CashPayRec");
            secstgstat.setYssPub(pub);
            
       
            	 secstgstat.stroageStat(YssFun.addDay(dDate, -1),
                         YssFun.addDay(dDate, -1),
                         operSql.sqlCodes(portCodes));
            
            //支付贷款利息时，只能查询账户类型为“存贷款账户”昨日余额为负数的账户
            	 if("07LI".equals(this.paidType)){		//07LI:支付贷款利息
            		 strConSql1=",t.FAccBalance";		//帐户余额
            		 strConSql2=" and FAccType='01' "; //账户类型为“存贷款账户”
            		 strConSql3=" join (select * from "+pub.yssGetTableName("Tb_Stock_Cash")+" where "+operSql.sqlStoragEve(dDate)+") t"
            		 			+" on t.fcashacccode=a.FCashAccCode and t.fportcode=a.FPortCode";            		 			
            	 }else{
            		  strConSql1="";
            	      strConSql2="";
            	      strConSql3="";
            	 }
            	 
            	 if("06DE".equals(this.paidType))
            	 {
            		 strConSql1 = ", c.FAuxiCashAccCode, d.FCashAccName as FAuxiCashAccName, d.fcurycode as FAuxiCuryCode ";
            	     strConSql3 = " left join (select FAuxiCashAccCode,FCashAccCode,FPortCode from " +
                     pub.yssGetTableName("Tb_Para_Cashacclink")  + 
                     " where FTradeTypeCode = 'J93' and FCheckState = 1 )c on a.FCashAccCode = c.FCashAccCode and a.FPortCode = c.FPortCode " +
                     " left join (select FCashAccCode, FCashAccName,FCuryCode from " +
                     pub.yssGetTableName("Tb_Para_CashAccount") + 
                     " where FCheckState = 1) d on c.FAuxiCashAccCode = d.fcashacccode";
            	 }else
            	 {
            		strConSql1="";
           	      	strConSql3="";
            	 }
            strSql =
                "select a. *, port.FPortName,port.FPortCury, inv.FInvMgrName,cat.FCatName," +
                "tsf.FTsfTypeName,subtsf.FSubTsfTypeName,cury.FCuryName,acc.FCashAccName,";
                if(!"07TF".equals(this.paidType)){
                	strSql+="sav.FMatureDate,";
                }else{
                	strSql+=dbl.sqlDate(dDate)+" as FMatureDate,";
                }		
                strSql+="h.fassetgroupcode, h.fassetgroupname " + //跨组合群国内项目
                strConSql1+
                " from (select * from " +
                pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " where FTsfTypeCode = " +
                //资金调拨类型取资金调拨子类型的前两位，不再定死为 06 子类型是从前台传过来有可能是 07
                dbl.sqlString(paidType .length() > 2? paidType.substring(0, 2) : YssOperCons.YSS_ZJDBLX_Rec) + // '06'
                " and FSubTsfTypeCode in (" +paidTypeFee+")"  + 
                " and " + operSql.sqlStoragEve(dDate) +
          
                " and FPortCode in (" +operSql.sqlCodes(portCodes) + ")" +
                " and Fcashacccode = " + dbl.sqlString(accCount) +
                " and Fcurycode = " + dbl.sqlString(curyCode) +
                ") a " + //现金利息收支，勾选多个操作组合查询
                //-------------------------------------------
                " left join (select FPortCode, FPortName,FPortCury from " +
                pub.yssGetTableName("tb_para_portfolio") +
                " where FCheckState = 1) port on a.FPortCode = port.FPortCode " +
                //----------------------------------------
                " left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FCheckState = 1) inv on a.FAnalysisCode1 = inv.FInvMgrCode " +
                //----------------------------------------
                " left join (select FCatCode, FCatName from Tb_Base_Category" +
                " where FCheckState = 1) cat on a.FAnalysisCode2 = cat.FCatCode " +
                //----------------------------------------
                " left join (select FTsfTypeCode, FTsfTypeName from tb_base_transfertype " +
                " where FCheckState = 1) tsf on a.FTsfTypeCode = tsf.FTsfTypeCode " +
                //----------------------------------------
                " left join (select FSubTsfTypeCode, FSubTsfTypeName from tb_base_subtransfertype " +
                " where FCheckState = 1) subtsf on a.FSubTsfTypeCode = subtsf.FSubTsfTypeCode" +
                //----------------------------------------
                " left join (select FCuryCode, FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) cury on a.FCuryCode = cury.FCuryCode" +
                //----------------------------------------
                " left join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1"+
                strConSql2+
                ") acc on a.FCashAccCode = acc.FCashAccCode" ;
                if(!"07TF".equals(this.paidType)){//固定交易费用的支付不去关联存款业务数据的到期日  
                	strSql+= " left join (select FCashAccCode,FMatureDate,FPortCode,FAnalysisCode1,FAnalysisCode2 from " + //增加分析代码1,去除重复的数据
	                pub.yssGetTableName("Tb_Cash_SavingInAcc") +
	                " where FCheckState = 1 and FPortCode in (" +
	                operSql.sqlCodes(portCodes) +
	                ")) sav " +
	                " on a.FCashAccCode = sav.FCashAccCode and a.FPortCode = sav.FPortCode " +
	                (analy1 ? " and a.FAnalysisCode1 = sav.FAnalysisCode1 " : "") + //增加分析代码1,去除重复的数据
	                (analy2 ? " and a.FAnalysisCode2 = sav.FAnalysisCode2 " : "") ;//增加分析代码2,去除重复的数据
	                
                }
                strSql+=" left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
                pub.getPrefixTB() + "' "
            	+strConSql3;
            
            if(!"07TF".equals(this.paidType)){			    
	            //还原太平的自动付息功能，合并太平版本代码 
	            if(verType.equalsIgnoreCase("new")){
	            	strSql = strSql + " where FMatureDate is null";//获取所有非定存的数据
	            }else{
	            	if (!this.isAll.equalsIgnoreCase("true")) {
	                    strSql = strSql + " where FMatureDate = " + dbl.sqlDate(this.dDate);
	                }
	            }
	            //还原太平的自动付息功能，合并太平版本代码
            }
            rs = dbl.queryByPreparedStatement(strSql);

            if (rs.next()) {
            	if("07LI".equals(this.paidType)){//余额为负才查询得到
            		if(rs.getDouble("FAccBalance")>=0)
            			return null;
            	}
            	
                paid = new AccPaid(pub);                
                paid.setAssetGroupCode(rs.getString("fassetgroupcode"));
                paid.setAssetGroupName(rs.getString("fassetgroupname"));
                
                paid.setDDate(dDate);
                paid.setPortCode(rs.getString("FPortCode"));
                paid.setPortName(rs.getString("FPortName"));
                paid.setInvmgrCode(rs.getString("FAnalysisCode1") + "");
                paid.setInvmgrName(rs.getString("FInvMgrName"));
                paid.setCatCode(rs.getString("FAnalysisCode2") + "");
                paid.setCatName(rs.getString("FCatName"));
                paid.setTsfTypeCode(rs.getString("FTsfTypeCode"));
                paid.setTsfTypeName(rs.getString("FTsfTypeName"));
                paid.setSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
                paid.setSubTsfTypeName(rs.getString("FSubTsfTypeName"));
                paid.setCuryCode(rs.getString("FCuryCode"));
                paid.setCuryName(rs.getString("FCuryName"));
                paid.setCashAccCode(rs.getString("FCashAccCode"));
                paid.setCashAccName(rs.getString("FCashAccName"));
                
                paid.setMoney(rs.getDouble("FBal"));
          
                paid.setMatureDate(rs.getDate("FMatureDate"));
                //付息的基础货币、组合货币应该是按照当日的汇率来计算的\
                //当天的基础汇率
                baseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FCuryCode"), paid.getPortCode(),
                    YssOperCons.YSS_RATE_BASE);
                //当天的组合汇率
                
                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                          paid.getPortCode());
                portCuryRate = rateOper.getDPortRate();
                
                if(this.paidType.equals("06DE"))
                {
                	paid.setChangeCashAccCode(rs.getString("FAuxiCashAccCode"));
                	paid.setChangeCashAccName(rs.getString("FAuxiCashAccName"));
                	auxiCuryCode = rs.getString("FAuxiCuryCode");
                	auxiCuryName = rs.getString("FAuxiCashAccName");
                	if(auxiCuryCode!=null&&!auxiCuryCode.equals(rs.getString("FCuryCode")))
                	{
                		auxiBaseRate = this.getSettingOper().getCuryRate(dDate,
                                rs.getString("FAuxiCuryCode"), paid.getPortCode(),
                                YssOperCons.YSS_RATE_BASE);
                	}
                }
                
                //--------------------------获取原币的数据位数。再通过它来设置其它金额的数据保留位数。为了冲抵应收的金额
                int Digit = this.getSettingOper().getRoundDigit(paid.getMoney());
                paid.setBaseMoney(this.getSettingOper().calBaseMoney(paid.getMoney(),
                    baseCuryRate, Digit));
                paid.setPortMoney(this.getSettingOper().calPortMoney(paid.getMoney(),
                    baseCuryRate, portCuryRate,
                    //增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    rs.getString("FCuryCode"), dDate, paid.getPortCode(), Digit));
                
                //--------------------------------------------------------------------------------------------------------------------------//
                paid.setBaseCuryRate(baseCuryRate);
                paid.setPortCuryRate(portCuryRate);
                paid.setLx(paid.getMoney());
                if(this.paidType.equals("06DE")&&auxiBaseRate!=0&&!auxiCuryCode.equals(paid.getCuryCode())) //当账户不一致时折算
                {
                	double money = this.getSettingOper().calBaseMoney(paid.getMoney(),
                			baseCuryRate/auxiBaseRate, Digit);
                	paid.setLx(paid.getMoney());
                	paid.setMoney(money);
                	paid.setCuryCode(auxiCuryCode);
                	paid.setCuryName(auxiCuryName);
                	paid.setBaseCuryRate(auxiBaseRate);
                }
            }
        }
        catch (Exception e) {
        	//---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
        	System.out.println(e.getMessage());
        	throw new YssException(e.getMessage());
        	//---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    	return paid;
    }

    /**
     * 重载方法，使得在其他的地方依旧能够调用。
     * @param alIncome ArrayList
     * @throws YssException
     */
    public void saveIncome(ArrayList alIncome) throws YssException {
        saveIncome(alIncome, "",false);////MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj 默认值
    }

    /**
     * saveIncome
     * 参数sSaveNum的增加是为了在做定存时能获取它的定存编号，以便进行相应的操作。 sj edit 20080326
     * @param alIncome ArrayList
     */
    public void saveIncome(ArrayList alIncome, String sSaveNum,boolean isAutoSaving) throws//MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj 增加boolean值，以判断是否为业务处理来自动生成的数据
        YssException {
        int i = 0;
        AccPaid accpaid = new AccPaid(pub);
        CashPecPayBean cashpecpay = null;
        CashPecPayBean cashBalpay = null;
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        CashTransAdmin cashtrans = null;
        CashPayRecAdmin cashpay = null;
        String cprNum = "";
      	String sDesc = "";//在资金调拨中的描述信息 合并太平版本代码
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String curyCodes = "";
        String cashacc = "";
        String anals1 = "";
        String anals2 = "";
        //----对条删除时用作拼装删除条件字段 sj add 20080123
        String portCodes = "";
        boolean analy1;
        boolean analy2;
        boolean analy3;
        //---------------------------用于在自动付息时,同时有多条资金调拨时汇集筛选条件用.sj edit 20080703.
        String securityCodes = "";
        String curyCash = "";
        //---------------------------
        ArrayList subtransfer = null;
        //-----------------------处理条件中的重复数据 by leeyu
        HashMap hmCuryCodes = new HashMap();
        HashMap hmCashacc = new HashMap();
        HashMap hmAnals1 = new HashMap();
        HashMap hmAnals2 = new HashMap();
        HashMap hmPortCodes = new HashMap();
        HashMap hmSecurityCodes = new HashMap();
        HashMap hmCuryCash = new HashMap();

        String DigitType = "";
        //-- MS00309 QDV4赢时胜（上海）2009年03月11日02_B ------------------//
        String cashRecAcc = ""; //现金应收应付的帐户
        //---------------------------------------------------------------//
        //MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A ---
        String savingFNumType = null;//定存关联编号生成的方式设置。若为自动产生，则为autoSaving,反之为handSaveInterest
        //----------------------------------------------
        
    	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    	Date logStartTime = null;//业务子项开始时间
    	String portCode = "";//组合代码
		//add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A 设置菜单代码
    	this.setFunName("incomepaid");
    	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            //MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A ---
            if(isAutoSaving){//自动产生
                savingFNumType = "autoSaveInterest";
            }else{//手动产生
                savingFNumType = "handSaveInterest";
            }
            //----------------------------------------------
            conn.setAutoCommit(false);
            bTrans = true;
            //---------为了把入库放在for循环之外 sj edit 20080123 ----//
            cashtrans = new CashTransAdmin();
            cashtrans.setYssPub(pub);
            cashpay = new CashPayRecAdmin();
            cashpay.setYssPub(pub);
            //----------------此处调用通用参数的设置，以判断是否向现金应收应付中录入4位小数的值 sj modify--//
            CtlPubPara pubpara = new CtlPubPara();
            pubpara.setYssPub(pub);
            DigitType = pubpara.getNavType();
            //------------------------------------------------------------------------------------//
            for (i = 0; i < alIncome.size(); i++) {
                accpaid = (AccPaid) alIncome.get(i);
                
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    			logInfo = "";//日志信息
    			logStartTime = new Date();//开始时间
    			portCode = accpaid.getPortCode();//组合代码
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
                if(this.dDate==null){
                	this.dDate=accpaid.getDDate();
                }
                analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
                analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
                analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
                accpaid.setInvmgrCode( (analy1 ? accpaid.getInvmgrCode().trim() :
                                        " "));
                accpaid.setCatCode( (analy2 ? accpaid.getCatCode().trim() : " "));
                //2008.10.12 蒋锦 添加 防止 ChangeCashAccCode 为 null
                if (accpaid.getChangeCashAccCode() == null ||
                    accpaid.getChangeCashAccCode().equalsIgnoreCase("null")) {
                    accpaid.setChangeCashAccCode("");
                }
                cashpecpay = setCashPecPayAttr(accpaid, "true");
                //===================================================================================================
                //fanghaoln 20090625 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
                if(this.isCheckData.equalsIgnoreCase("true")){//如果前台传来true表示选中了审核状态统计之后的数据放到已审核里面
                    cashpecpay.checkStateId=1;
                }else{//如果没有选中已审核的状态表示数据放到未审核里面
                    cashpecpay.checkStateId=0;
                }
                //===========================================end=======================================================
                //-------------关联数据排序编号。 MS00141 QDV4交银施罗德2009年01月4日02_B sj modified--//
                cashpecpay.setRelaOrderNum(Integer.toString(i));
                //--------------------------------------------------------------------------------//
                //-------------------获取需要删除的字段 sj add 20080123 ----------------------------//
                if (cashpecpay.getCuryCode() != null &&
                    hmCuryCodes.get(cashpecpay.getCuryCode()) == null) {
                    hmCuryCodes.put(cashpecpay.getCuryCode(), cashpecpay.getCuryCode());
                    curyCodes +=
                        (cashpecpay.getCuryCode() == null ? "" :
                         cashpecpay.getCuryCode()) +
                        ",";
                }
                if (hmCashacc.get(accpaid.getChangeCashAccCode().trim().length() ==
                                  0 ? accpaid.getCashAccCode() :
                                  accpaid.getChangeCashAccCode()) == null) {
                    //-- MS00309 QDV4赢时胜（上海）2009年03月11日02_B ------------------//
                    hmCashacc.put(accpaid.getCashAccCode().trim().length() ==
                                  0 ? accpaid.getChangeCashAccCode() : accpaid.getCashAccCode(),
                                  accpaid.getCashAccCode().trim().length() ==
                                  0 ? accpaid.getChangeCashAccCode() :
                                  accpaid.getCashAccCode()); //应收应付的帐户使用的是没变更的帐户。
                    //---------------------------------------------------------------//
                    cashacc +=
                        (accpaid.getChangeCashAccCode().trim().length() == 0 ?
                         accpaid.getCashAccCode() : accpaid.getChangeCashAccCode()) +
                        ","; //修改前的金额与修改后的金额的差值 sj 20071126 edit
                }
                if (hmAnals1.get(cashpecpay.getInvestManagerCode()) == null) {
                    hmAnals1.put(cashpecpay.getInvestManagerCode(),
                                 cashpecpay.getInvestManagerCode());
                    anals1 += cashpecpay.getInvestManagerCode() + ",";
                }
                if (hmAnals2.get(cashpecpay.getCategoryCode()) == null) {
                    hmAnals2.put(cashpecpay.getCategoryCode(),
                                 cashpecpay.getCategoryCode());
                    anals2 += cashpecpay.getCategoryCode() + ",";
                }
                if (hmPortCodes.get(cashpecpay.getPortCode()) == null) {
                    hmPortCodes.put(cashpecpay.getPortCode(), cashpecpay.getPortCode());
                    portCodes += cashpecpay.getPortCode() + ",";
                }
                //---------------------------------------------------------------------------------
                cashpay.addList(cashpecpay);
               
                //add by jsc 20120323 支付存款利息时，顺带把相应的存款利息税的应付冲减后，就不再产生相应的资金调拨
                if(cashpecpay.getSubTsfTypeCode().equalsIgnoreCase("03LXS_DE")){
                	continue;
                }
                
                if (accpaid.getBalMoney() != 0) { //如果金额是在前台经过修改的,则再加一条应收应付记录 sj add 20071126
                    cashBalpay = setCashPecPayAttr(accpaid, "false");
                    //===================================================================================================
                    //fanghaoln 20090714 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
                    if (this.isCheckData.equalsIgnoreCase("true")) { //如果前台传来true表示选中了审核状态统计之后的数据放到已审核里面
                        cashBalpay.checkStateId = 1;
                    } else { //如果没有选中已审核的状态表示数据放到未审核里面
                        cashBalpay.checkStateId = 0;
                    }
                    //===========================================end=======================================================
                    cashpay.addList(cashBalpay);
                }
                cprNum = cashpay.getInsertNum(); //获得应收应付编号 插到资金调拨表
                transfer = setTransferAttr(accpaid, cprNum);
                //------将赋予现金应收应付的关联数据排序编号再赋予资金调拨,使他们拥有一个编号。 MS00141 QDV4交银施罗德2009年01月4日02_B sj modified--//
                transfer.setRelaOrderNum(cashpecpay.getRelaOrderNum());
                transfer.setFNumType("CashPay"); //编号类型为CashPay
                //-----------------------------------------------------------------------------------------------------------------------//
                transferset = setTransferSetAttr(accpaid);
				// 设置定存派息在资金调拨里的描述信息
				// ----------add by yanghaiming 20091230 MS00888
				// QDV4中保2009年12月25日01_A ---------------//
				ResultSet rs1 = null;
				String cashaccName = "";
				String strSql1 = "select * from " + pub.yssGetTableName("Tb_Para_CashAccount") +
								 " where FCASHACCCODE = '" + cashpecpay.getCashAccCode() + "'";

				rs1 = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
				while (rs1.next()) {
					cashaccName = rs1.getString("FCASHACCNAME");
				}
				dbl.closeResultSetFinal(rs1);
				sDesc = "[" + YssFun.formatDate(cashpecpay.getTradeDate())
						+ "]"; // 调拨日期
				sDesc += "[" + cashpecpay.getInvestManagerName() + "]";// 投资经理
				//delete by songjie 2012.05.04 BUG 4421 QDV4赢时胜(诺安基金)2012年4月27日02_B 根据调拨子类型名称生成描述信息
				//sDesc += "存款派息";// 交易方式
				//---add by songjie 2012.05.04 BUG 4421 QDV4赢时胜(诺安基金)2012年4月27日02_B start---//
				String subTsfTypeCode = cashpecpay.getSubTsfTypeName();
				//subTsfTypeCode.replaceAll("应付", "支付");//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
				sDesc += subTsfTypeCode;//根据调拨子类型名称生成描述信息
				//---add by songjie 2012.05.04 BUG 4421 QDV4赢时胜(诺安基金)2012年4月27日02_B end---//
				sDesc += "[" + cashpecpay.getCashAccCode() + ":";
				sDesc += cashaccName + "]";
				transferset.setSDesc(sDesc);
				// ---------------------------------------------------------------/
                //===================================================================================================
                //fanghaoln 20090625 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
                if(this.isCheckData.equalsIgnoreCase("true")){//如果前台传来true表示选中了审核状态统计之后的数据放到已审核里面
                    transfer.checkStateId=1;
                }else{//如果没有选中已审核的状态表示数据放到未审核里面
                    transfer.checkStateId=0;
                }
                //===========================================end=======================================================
                //--------将子资金调拨放入transferset中的arraylist,并放入Admin中 ---sj 20080123-----//
                subtransfer = new ArrayList();
                subtransfer.add(transferset);
                transfer.setSubTrans(subtransfer);
                
            	//---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
            	logInfo += "到账日期:" + YssFun.formatDate(transfer.getDtTransferDate(),"yyyy-MM-dd") +
            		       "\r\n现金账户代码:" + transferset.getSCashAccCode() +
            	           "\r\n" + ((transferset.getIInOut() == 1) ? "流入":"流出") + 
            	           "利息金额:" + transferset.getDMoney() +
            	           YssCons.YSS_LINESPLITMARK;
            	//---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
                
                cashtrans.addList(transfer);
                if (hmSecurityCodes.get(transfer.getStrSecurityCode()) == null) {
                    hmSecurityCodes.put(transfer.getStrSecurityCode(),
                                        transfer.getStrSecurityCode());
                    securityCodes += transfer.getStrSecurityCode() + ",";
                }
                if (hmCuryCash.get(transfer.getSrcCashAccCode()) == null) {
                    hmCuryCash.put(transfer.getSrcCashAccCode(),
                                   transfer.getSrcCashAccCode());
                    curyCash += transfer.getSrcCashAccCode() + ",";
                }
                //------------------------------------------------------------------
    			
                //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                //生成业务日志数据
                /**shashijie 2012-11-1 BUG BUG 6115 执行调度方案失败,插入日志时logOper对象为空 */
                //edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A
//                if (logOper != null) {//logOper != null 不需要判断两遍
                	//这里不能new,调度方案执行过来是并不是处理收益支付,很有可能是处理定存业务,一旦new存到日志中去可能就是收益支付了
//                	logOper = SingleLogOper.getInstance();
//                }
            	//edit by songjie 2012.11.20 添加非空判断
            	if(logOper != null){
            		logOper.setDayFinishIData(this,20,operType,this.pub,false,
                    	portCode,dDate,dDate,dDate,logInfo,
                    	logStartTime,logSumCode, new Date());//20收益支付 ，operType是处理项目
            	}
				
                /**end shashijie 2012-11-1 BUG */
                //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            }
            //----------删除末尾的, sj add 20080123 -------------------//
            if (curyCodes.length() > 0) {
                curyCodes = curyCodes.substring(0, curyCodes.length() - 1);
            }
            if (cashacc.length() > 0) {
                cashacc = cashacc.substring(0, cashacc.length() - 1);
            }
            if (anals1.length() > 0) {
                anals1 = anals1.substring(0, anals1.length() - 1);
            }
            if (anals2.length() > 0) {
                anals2 = anals2.substring(0, anals2.length() - 1);
            }
            if (portCodes.length() > 0) {
                portCodes = portCodes.substring(0, portCodes.length() - 1);
            }
            //-------------------------------------------------------------------
            //-------------------------------------------------------------------
            //-- MS00309 QDV4赢时胜（上海）2009年03月11日02_B ------------------//
            if (hmCashacc != null && hmCashacc.size() > 0) {
                java.util.Iterator accIt = hmCashacc.values().iterator(); //获取帐户的信息
                while (accIt.hasNext()) {
                    cashRecAcc += (String) accIt.next() + ",";
                }
                if (cashRecAcc.length() > 0) {
                    cashRecAcc = cashRecAcc.substring(0, cashRecAcc.length() - 1);
                }
            }
            String sTsfCode = "";
            String sSubTsfCode = "";
            //------------MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A--------------//
            //2009.07.03 蒋锦 修改 增加支付类型 将调拨类型 和 调拨子类型拿到外面来判断
            if (cashpecpay.getTsfTypeCode().equalsIgnoreCase("02")) {
                sTsfCode = "02" + "," + "98"; //增加调整金额的调拨类型 sj 20080218
//                sSubTsfCode = "02DE" + "," + "9802DE" + "," +
//                    (this.paidType.equalsIgnoreCase("06PF") ?
//                     "02PF,9802PF" :
//                     ""); //edit by jc
              // BUG3447支付申购款利息产生的现金应收应付数据和资金调拨数据会覆盖掉支付现金利息产生的数据 add by jiangshichao 2011.12.30
              sSubTsfCode = (this.paidType.equalsIgnoreCase("06PF") ?"02PF,9802PF" :"02DE,9802DE");
            } else if (cashpecpay.getTsfTypeCode().equalsIgnoreCase("03")) {
            	//fanghaoln 20100430 MS01130 QDV4赢时胜上海2010年04月27日02_B
            	// edited by zhouxiang MS01132
				// 结算服务费、交易手续费及银行费用需分开不同类型进行费用的统计（国内，9月发布）
				if ("03RE01".equalsIgnoreCase(cashpecpay
						.getSubTsfTypeCode())
						|| "03RE02".equalsIgnoreCase(cashpecpay
								.getSubTsfTypeCode())
						|| "03RE03".equalsIgnoreCase(cashpecpay
								.getSubTsfTypeCode())) {
					sTsfCode = YssOperCons.YSS_ZJDBLX_Fee + "," + "98";
					sSubTsfCode = "03RE01" + "," + "03RE02" + "," + "03RE03"
							+ "," + "98" + cashpecpay.getSubTsfTypeCode();
				} else if ("03FE01".equalsIgnoreCase(cashpecpay
						.getSubTsfTypeCode())||"03FE02".equalsIgnoreCase(cashpecpay
								.getSubTsfTypeCode())||"03FE03".equalsIgnoreCase(cashpecpay
										.getSubTsfTypeCode())) {
					sTsfCode = YssOperCons.YSS_ZJDBLX_Fee + "," + "98";
					sSubTsfCode = "03FE01" + "," + "03FE02" + "," + "03FE03"
							+ "," + "98" + cashpecpay.getSubTsfTypeCode();
				}
                //------------------END ms01132  20100830 -------------------------------------------------------------
            	else{
            		sTsfCode = YssOperCons.YSS_ZJDBLX_Fee + "," + "98";
                    sSubTsfCode = cashpecpay.getSubTsfTypeCode() + "," + "98"+cashpecpay.getSubTsfTypeCode();
            	}
            	
                //-------------------------------end----MS01130-------------------------------------------
            }
            //------ add by wangzuochun  2010.08.27   MS01606    定存业务处理后，不能删除历史资金调拨数据    QDV4赢时胜(测试)2010年08月12日07_B 
            //modify by nimengjing 2010.12.27 BUG #738 收益支付界面存在一些缺陷 
            if(sSaveNum.length()==0){//modify by nimengjing 2011.2.11 BUG #1058 做业务处理时会把收益支付产生的现金应收应付数据删除 
            	delOldCashPayRec(cashpay);
            }
            //--------------------end ---BUG #738------------------------------
            //--------MS01606---------//
            //-----MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj -----------------------------------------------------//
            if (sSaveNum.length() > 0) {//若为定存
               cashpay.insert(dDate, "02",
                          "02DE", this.portCodes,
                          0, false, sSaveNum, savingFNumType,DigitType.trim().equalsIgnoreCase("new")?false:true);//以关联编号和类型来删除数据//合并太平版本调整  byleeyu 20100827

            }
            else {//其他类型的数据保持原有的处理方式
                if (DigitType.trim().equalsIgnoreCase("new")) { //为国内的通用方式，2位小数
                    cashpay.insert(cashpecpay.getTradeDate(),
                                   sTsfCode,//2009.07.03 蒋锦 修改 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                                   sSubTsfCode,
                                   portCodes,
                                   anals1,
                                   anals2,
                                   //-- MS00309 QDV4赢时胜（上海）2009年03月11日02_B
                                   //cashacc,
                                   cashRecAcc.length() > 0 ? cashRecAcc : cashacc, //获取现金应收应付的帐户
                                   //--------------------------------------------
                                   curyCodes,
                                   0);
                }
                else { //为中保的方式，4位小数
                    cashpay.insert(cashpecpay.getTradeDate(),
                                   sTsfCode, //增加调整金额的调拨类型 sj 20080218//2009.07.03 蒋锦 修改 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                                   sSubTsfCode, //edit by jc
                                   portCodes,
                                   anals1,
                                   anals2,
                                   //-- MS00309 QDV4赢时胜（上海）2009年03月11日02_B
                                   //cashacc,
                                   cashRecAcc.length() > 0 ? cashRecAcc : cashacc, //获取现金应收应付的帐户
                                   //--------------------------------------------
                                   curyCodes,
                                   0, true);
                }
            }
            
            if (sSaveNum.length() > 0) {
                transfer.setSavingNum(sSaveNum);
                //fanghaoln 20090730 MS00604 QDV4中金2009年7月28日02_B
                //MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj 此处的编号类型加以区分设置，以便将自动和手工数据分离
                transfer.setFRelaNum(sSaveNum);//将定存编号置入关联编号
                transfer.setFNumType(savingFNumType);//重新设置关联编号类型
                cashtrans.insert(dDate, savingFNumType, 1, sSaveNum); //此处将收益的资金调拨编号类型分开，以避免错误删除。并且加入关联编号，以免错误删除
                //=====================================================================
            } else {
                //--在非定存类型之处,才需要此编号 sj modified 20090120 QDV4交银施罗德2009年01月4日02_B  bugId:MS00141--------
//            if (cprNum.length() == 0){//若之前的编号没有获取到值,则在此处获取收入的编号.
//               transfer.setFRelaNum(cashpay.getInComeTypeNum());
//               transfer.setFNumType("CashPay");//编号类型为CashPay
//            }
                //----------------------------------------------------------------------------
                //----sj modified 20090120 QDV4交银施罗德2009年01月4日02_B  bugId:MS00141 将关联编号的排序hashtable传递进资金调拨中，以便准确的设置关联编号--//
                cashtrans.setRelaOrderNum(cashpay.getRelaOrderNum());
                cashtrans.delete("", cashpay.getDeleteNums(), "", "", ""); //先通过关联编号删除数据 QDV4赢时胜（上海）2009年4月16日04_B MS00390 by leeyu 20090429
                //---------------------------------------------------------------------------------------------------------------------------------//
                cashtrans.insert(transfer.getDtTransferDate(),
                                 transfer.getDtTransDate(),
                                 cashpecpay.getTsfTypeCode(),
                                 sSubTsfCode, //edit by jc//2009.07.03 蒋锦 修改 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                                 transfer.getStrSecurityCode(),
                                 -99, cashacc); //MS00334 add by songjie 2009.03.27 即删除信息时不用判断是否是手动录入还是自动录入全部都删除
                //删除的条件应该是按多个账户代码删除，而不是单个  邱健 20080905 修改
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
        	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
			    //生成业务日志数据
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,20,operType,this.pub,
                		true, portCode, dDate, dDate, dDate,
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                		(logInfo + " \r\n 支付现金账户利息出错 \r\n " + e.getMessage())//处理日志信息 除去特殊符号
        				.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
        				//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                		logStartTime,logSumCode, new Date());//20收益支付 ，operType是处理项目
        		}
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	finally{//添加 finally 保证可以抛出异常
            	throw new YssException(e.getMessage(), e);
            }
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally{
            dbl.endTransFinal(conn, bTrans);
        }
    }

    protected CashPecPayBean setCashPecPayAttr(AccPaid accpaid, String equals) throws
        YssException {
        try {
            CashPecPayBean cashpecpay = new CashPecPayBean();
            cashpecpay.setTradeDate(null == accpaid.getMDate()?accpaid.getDDate():accpaid.getMDate());//edit by yanghaiming 20100416 MS00997  QDV4建行2010年02月23日01_B 增加mdate为业务日期
            //cashpecpay.setTradeDate(accpaid.getDDate());
            cashpecpay.setPortCode(accpaid.getPortCode());
            cashpecpay.setInvestManagerCode(accpaid.getInvmgrCode());
            cashpecpay.setCategoryCode(accpaid.getCatCode());
//         cashpecpay.setCashAccCode(accpaid.getChangeCashAccCode().trim().length() == 0 ? accpaid.getCashAccCode() : accpaid.getChangeCashAccCode());
            cashpecpay.setCuryCode(accpaid.getCuryCode());
            cashpecpay.setBaseCuryRate(accpaid.getBaseCuryRate());
            cashpecpay.setPortCuryRate(accpaid.getPortCuryRate());
            
            //---add by lidaolong 374 QDV4国泰2010年12月02日01_A
            cashpecpay.setStrAttrClsCode(accpaid.getAttrClsCode());
            //---end lidaolong 2011.02.22..
            
            // -- MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A  sj 添加对关联编号和编号类型的处理
            if (null != accpaid.getRelaNum() && accpaid.getRelaNum().trim().length() > 0){
                cashpecpay.setRelaNum(accpaid.getRelaNum());
            }
            if (null != accpaid.getNumType() && accpaid.getNumType().trim().length() > 0){
                cashpecpay.setRelaNumType(accpaid.getNumType());
            }
            //-----------------------------------------------------------------------------
            if (equals.equalsIgnoreCase("true")) {
                cashpecpay.setCashAccCode(accpaid.getCashAccCode().trim().length() ==
                                          0 ? accpaid.getChangeCashAccCode() :
                                          accpaid.getCashAccCode()); //收入的账户依旧是原来的账户。sj edit 20081016
                //------add by wangzuochun 2010.06.08  MS01166    普通定存，做“转出”交易后，原账户利息库存没有冲减掉    QDV4国内（测试）2010年05月10日02_AB   
                cashpecpay.setCashAccCode((accpaid.getOutCashAccCode() == null || accpaid.getOutCashAccCode().trim().length() == 0) ?
                							accpaid.getCashAccCode():accpaid.getOutCashAccCode());
                //---------------------------------MS01166----------------------------------------//
                
                //------------MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A--------------//
                //2009.07.03 蒋锦 修改 增加支付类型
                if(accpaid.getTsfTypeCode().equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Pay)){
                    cashpecpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);
                } else {
                    cashpecpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
                }
                if(paidType.startsWith(YssOperCons.YSS_ZJDBLX_Pay)){
                    //edited by zhouxiang MS01132 MS01132    结算服务费、交易手续费及银行费用需分开不同类型进行费用的统计（国内，9月发布） 
                	cashpecpay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee + accpaid.getSubTsfTypeCode().substring(2));
                	//---------------------20100830----------end ms01132-------------------------------------------------------------
                } else {
                    cashpecpay.setSubTsfTypeCode(this.paidType.equalsIgnoreCase("06PF") ?
                                                 "02PF" : "02DE");
                }
				//add by jsc 20120323 支付利息税同时也要产生冲减存款利息税的03数据
                if(accpaid.getSubTsfTypeCode().equalsIgnoreCase(YssOperCons.YSS_ZJDBZLX_LXS_DE)){
                	cashpecpay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);
            		cashpecpay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_LXS_FEE);
            	}
                //-------------------------------------------------------------------------------//
                //-----------------jc
                /*cashpecpay.setMoney(YssD.sub(accpaid.getMoney(),
                                             accpaid.getBalMoney()));
                 cashpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                      accpaid.
                      getMoney(), accpaid.getBaseCuryRate()));
                 cashpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                      accpaid.
                      getMoney(), accpaid.getBaseCuryRate(),
                    accpaid.getPortCuryRate()));*/
                cashpecpay.setMoney(accpaid.getLx() == 0 ? accpaid.getMoney() :
                                    accpaid.getLx()); //LX为没有在前台修改过的数值，若为0则说明是自动付息产生了这样的数据。所以加以判断。获取直接在后台获取的money。sj edit 20080703
                //---------------------根据原币的小数位来设置基础、组合货币的保留小数位。sj edit 20080811. 暂无 bug-------------//
                int Digit = this.getSettingOper().getRoundDigit(accpaid.getLx() ==
                    0 ? accpaid.getMoney() :
                    accpaid.getLx());
                cashpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                    accpaid.getLx() == 0 ? accpaid.getMoney() : accpaid.getLx(),
                    accpaid.getBaseCuryRate(), Digit));
                cashpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                    accpaid.getLx() == 0 ? accpaid.getMoney() : accpaid.getLx(),
                    accpaid.getBaseCuryRate(),
                    accpaid.getPortCuryRate(),
                    //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    accpaid.getCuryCode(), accpaid.getDDate(), accpaid.getPortCode(), Digit));

            } else if (equals.equalsIgnoreCase("false")) {
                cashpecpay.setCashAccCode(accpaid.getCashAccCode()); //调整金额的帐户为原帐户 leeyu 20090429 QDV4赢时胜（上海）2009年4月16日04_B MS00390
                
                //------add by wangzuochun 2010.06.08  MS01166    普通定存，做“转出”交易后，原账户利息库存没有冲减掉    QDV4国内（测试）2010年05月10日02_AB   
                cashpecpay.setCashAccCode((accpaid.getOutCashAccCode() == null || accpaid.getOutCashAccCode().trim().length() == 0) ?
                							accpaid.getCashAccCode():accpaid.getOutCashAccCode());
                //---------------------------------MS01166----------------------------------------//
                
                cashpecpay.setTsfTypeCode("98"); //调拨类型转换成98 sj edit 20080218
                //------------MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A--------------//
                //2009.07.03 蒋锦 修改 增加支付类型
                if(paidType.startsWith(YssOperCons.YSS_ZJDBLX_Pay)){
                    cashpecpay.setSubTsfTypeCode("98" + YssOperCons.YSS_ZJDBLX_Fee + paidType.substring(2));
                } else {
                    cashpecpay.setSubTsfTypeCode(this.paidType.equalsIgnoreCase("06PF") ?
                                             "9802PF" : "9802DE");
                }
                //------------------------------------------------------------------------------//
                //--------------jc
                cashpecpay.setMoney(accpaid.getBalMoney()); //修改前的金额与修改后的金额的差值 sj 20071126 edit
                //------------------------根据原币的小数位来设置基础、组合货币的保留小数位。sj edit 20080811. 暂无 bug----------//
                int Digit = this.getSettingOper().getRoundDigit(cashpecpay.getMoney());
                cashpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                    cashpecpay. //用上一步计算的金额来再次计算
                    getMoney(), accpaid.getBaseCuryRate(), Digit));
                cashpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                    cashpecpay.
                    getMoney(), accpaid.getBaseCuryRate(),
                    accpaid.getPortCuryRate(),
                    //linjunyun 2008-11-26 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    accpaid.getCuryCode(), accpaid.getDDate(), accpaid.getPortCode(), Digit));
                //-------------------------------------------------------------------------------------------------------//
            }
            cashpecpay.checkStateId = 1; //生成的直接进入已审核。sj edit 20080626.
            //add by songjie 2012.05.04 BUG 4421 QDV4赢时胜(诺安基金)2012年4月27日02_B
            cashpecpay.setSubTsfTypeName(getSubTsfTypeName(cashpecpay.getSubTsfTypeCode()));
            return cashpecpay;

        } catch (Exception e) {
            throw new YssException("取应收利息出错", e);
        }

    }
    
    /**
     * add by songjie 2012.05.04
     * BUG 4421 QDV4赢时胜(诺安基金)2012年4月27日02_B
     * @param subTsfTypeCode
     * @return
     * @throws YssException
     */
    private String getSubTsfTypeName(String subTsfTypeCode)throws YssException{
    	String subTsfTypeName = "";
    	ResultSet rs = null;
    	String strSql = "";
    	try{
    		strSql = " select a.Fsubtsftypecode,a.Fsubtsftypename from Tb_Base_Subtransfertype " +
    			     " a where a.Fsubtsftypecode = " + dbl.sqlString(subTsfTypeCode) + " and a.Fcheckstate = 1 ";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			subTsfTypeName = rs.getString("Fsubtsftypename");
    		}
    		return subTsfTypeName;
    	}catch(Exception e){
    		throw new YssException("获取调拨子类型数据出错", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }

    protected TransferBean setTransferAttr(AccPaid accpaid, String strCprNum) {
        TransferBean transfer = new TransferBean();
        transfer.setDtTransferDate(accpaid.getDDate());
        //---若无法获取结算日期，则默认使用传入的业务日期 ----//
        transfer.setDtTransDate(null == accpaid.getMDate()?accpaid.getDDate():accpaid.getMDate());//edit by yanghaiming 20100226 MS00997  QDV4建行2010年02月23日01_B 增加mdate为业务日期
        //------------------------------------------------//
        //MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
        //2009.07.03 蒋锦 修改 增加支付类型
        if (accpaid.getTsfTypeCode().equalsIgnoreCase(YssOperCons.YSS_ZJDBLX_Pay)) {
            transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);
        } else {
            transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
        }
        if (paidType.startsWith(YssOperCons.YSS_ZJDBLX_Pay)) {
            //edited by zhouxiang MS01132    结算服务费、交易手续费及银行费用需分开不同类型进行费用的统计（国内，9月发布 
        	transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee + accpaid.getSubTsfTypeCode().substring(2));
        	//------------------end----------------------------------------------20100908--
        } else {
            transfer.setStrSubTsfTypeCode(this.paidType.equalsIgnoreCase("06PF") ?
                                         "02PF" : "02DE");
        }
        //---------------jc
        transfer.setFNumType(accpaid.getNumType());
        transfer.setCprNum(strCprNum);
        transfer.checkStateId = 1;
        transfer.setSrcCashAccCode(accpaid.getChangeCashAccCode().trim().length() ==
                                   0 ? accpaid.getCashAccCode() :
                                   accpaid.getChangeCashAccCode()); //获取更改过的现金帐户。sj edit 20080924 bug:0000479
        transfer.setDataSource(1); //MS00334 QDV4赢时胜（上海）2009年3月23日01_B add by songjie 2009.03.30 设置数据源为1，及自动
        
        transfer.setStrAttrClsCode((accpaid.getAttrClsCode().trim().length()==0)?" ":accpaid.getAttrClsCode()); //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
        return transfer;
    }

    protected TransferSetBean setTransferSetAttr(AccPaid accpaid) throws
        YssException {
        double dBaseRate = 1;
        double dPortRate = 1;
        /*
               //当天的基础汇率
               dBaseRate = this.getSettingOper().getCuryRate(accpaid.getDDate(),
              accpaid.getCuryCode(), accpaid.getPortCode(),
              YssOperCons.YSS_RATE_BASE);
               //当天的组合汇率
               dPortRate = this.getSettingOper().getCuryRate(accpaid.getDDate(),
              accpaid.getCuryCode(), accpaid.getPortCode(),
              YssOperCons.YSS_RATE_PORT);
         */
        //基础汇率与组合汇率取收益支付时的汇率 by leeyu 080616
        dBaseRate = accpaid.getBaseCuryRate();
        if (dBaseRate == 0) {
            //若为 0 取当天的基础汇率
            dBaseRate = this.getSettingOper().getCuryRate(accpaid.getDDate(),
                accpaid.getCuryCode(), accpaid.getPortCode(),
                YssOperCons.YSS_RATE_BASE);
        }
        dPortRate = accpaid.getPortCuryRate();
        if (dPortRate == 0) {
            //若为 0 取当天的组合汇率
//         dPortRate = this.getSettingOper().getCuryRate(accpaid.getDDate(),
//               accpaid.getCuryCode(), accpaid.getPortCode(),
//               YssOperCons.YSS_RATE_PORT);
            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 --------------------------
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(accpaid.getDDate(), accpaid.getCuryCode(),
                                      accpaid.getPortCode());
            dPortRate = rateOper.getDPortRate();
            //-----------------------------------------------------------------------------------

        }
        TransferSetBean transferset = new TransferSetBean();
        transferset.setSPortCode(accpaid.getPortCode());
        transferset.setStrAttrClsCode((accpaid.getAttrClsCode().trim().length()==0)?" ":accpaid.getAttrClsCode());//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
        transferset.setSAnalysisCode1(accpaid.getInvmgrCode());
        transferset.setSAnalysisCode2(accpaid.getCatCode());
//      transferset.setSCashAccCode(accpaid.getCashAccCode());
        transferset.setSCashAccCode(accpaid.getChangeCashAccCode().trim().length() ==
                                    0 ? accpaid.getCashAccCode() :
                                    accpaid.getChangeCashAccCode()); //获取更改过的现金帐户。sj edit 20080924 bug:0000479
        //-----------------------------通过通用参数的设置来判断小数的方式。sj edit 20080808 bug 0000374---------//
        CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        String digittype = pubpara.getAccPaidDigit(transferset.getSCashAccCode());
        if (digittype.equalsIgnoreCase("cut")) { //是否此帐户为截取方式。
            accpaid.setMoney(getSettingOper().cutDigit(accpaid.getMoney(), 2)); //截取两位小数。
        } else { //其它情况默认为舍入2位.
            accpaid.setMoney(YssD.round(accpaid.getMoney(), 2));
        }
        //-------------------------------------------------------------------------------------//
        transferset.setDMoney(accpaid.getMoney());
        transferset.setDBaseRate(dBaseRate);
        transferset.setDPortRate(dPortRate);
        //-------xuqiji 20100327 MS00954  QDV4赢时胜（测试）2010年03月27日05_B   -------------//
        if(accpaid.getTsfTypeCode().equalsIgnoreCase("07")){
        	transferset.setIInOut(-1); //20070806
        }else{
        	transferset.setIInOut(1); //20070806
        }
       // -----------------------------end--------------------------/
        transferset.checkStateId = 1;
        return transferset;
    }
    
    /**
     * add by wangzuochun 2010.08.25  MS01606    定存业务处理后，不能删除历史资金调拨数据    QDV4赢时胜(测试)2010年08月12日07_B    
     * 删除历史的现金应收应付数据；
     * @param cashpayrecadmin
     * @throws YssException
     */
    public void delOldCashPayRec(CashPayRecAdmin cashpayrecadmin) throws YssException {
    	
        String strSql = "";
        boolean bDel = false;
        CashPecPayBean  cashpecpay = null;
       
		try {
			// 判断当天是否产生02,02DE的现金应收应付数据；
			for (int i = 0; i < cashpayrecadmin.getAddList().size(); i++ ){
				cashpecpay = (CashPecPayBean) cashpayrecadmin.getAddList().get(i);
				if ("02".equals(cashpecpay.getTsfTypeCode()) && 
							"02DE".equals(cashpecpay.getSubTsfTypeCode())){
					
					bDel = true;
					break;
				}
			}
			// 如果当天有产生02,02DE的现金应收应付数据，则删除历史的现金应收应付数据；
			if (bDel) {
				
          	    strSql = " delete from "
						+ pub.yssGetTableName("Tb_data_cashpayrec")
						//edit by songjie 2012.04.06 BUG 4144 QDV4赢时胜(测试)2012年3月28日01_B
						+ " Where FDataOrigin = 0 and FTsfTypeCode = '02' and FSubTsfTypeCode = '02DE' and FRelaType is null and FRelaNum is null "
						+ " and FTransDate = "
						+ dbl.sqlDate(cashpecpay.getTradeDate())
						+ " and FPortCode = " + dbl.sqlString(cashpecpay.getPortCode())
						// add by lidaolong 374 QDV4国泰2010年12月02日01_A，根据账户删除数据
						+" AND FCASHACCCODE =" +dbl.sqlString(cashpecpay.getCashAccCode());
						//－－end lidaolong 374 QDV4国泰2010年12月02日01_A
				dbl.executeSql(strSql);
				
			}
			
		} catch (Exception e) {
			throw new YssException("删除历史的现金应收应付数据出错" + "\r\n" + e.getMessage(),e);
		}
	}
    
    /******************************************************
     *  add by jsc 20120323
     *  支付存款利息的同时，利息税也相应的要支付掉
     * @return
     * @throws YssException
     * @throws  
     */
    public IYssConvert PaidInterTax(IYssConvert bean,HashMap valueMap)throws YssException{
    	AccPaid accPaid = null;
    	double dBal = 0;
    	double dInterTaxBal = 0;
    	double dBaseInterTaxBal = 0;
    	double dPortInterTaxBal = 0;
    	double dscale =0;
    	try{
    		dBal = (Double)valueMap.get("06DE\tbal");
    		accPaid = (AccPaid)((AccPaid)bean).clone();
    		dInterTaxBal = (Double)valueMap.get("07LXS_DE\tbal");
    		dBaseInterTaxBal = (Double)valueMap.get("07LXS_DE\tbasebal");
    		dPortInterTaxBal = (Double)valueMap.get("07LXS_DE\tportbal");
    		
    		if(accPaid.getLx() == dBal){
    			accPaid.setLx(dInterTaxBal);
    			accPaid.setBalMoney(dInterTaxBal);
    			accPaid.setMoney(dInterTaxBal);
    			accPaid.setPortMoney(dPortInterTaxBal);
    			accPaid.setBaseMoney(dBaseInterTaxBal);
    		}else{
    			dscale = YssD.div(accPaid.getLx(), dBal);
    			dInterTaxBal = YssD.round(YssD.mul(dscale, dInterTaxBal), 2);
        		dBaseInterTaxBal = YssD.round(YssD.mul(dscale, dBaseInterTaxBal), 2);
        		dPortInterTaxBal = YssD.round(YssD.mul(dscale, dPortInterTaxBal), 2);
    			
        		accPaid.setLx(dInterTaxBal);
    			accPaid.setBalMoney(dInterTaxBal);
    			accPaid.setMoney(dInterTaxBal);
    			accPaid.setPortMoney(dPortInterTaxBal);
    			accPaid.setBaseMoney(dBaseInterTaxBal);
    		}
    		accPaid.setTsfTypeCode("07");
    		accPaid.setSubTsfTypeCode("07LXS_DE");
    		
    	}catch(Exception e){
    		throw new YssException("存款利息税赋值出错......");
    	}
    	return accPaid;
    }
    
    /**************************************************
     * 获取存款利息税
     * add by jsc 20120323
     * @param bean
     * @return
     * @throws YssException
     */
    public HashMap getPaidInterTax(IYssConvert bean)throws YssException{
    	
    	AccPaid accPaid = (AccPaid)bean;
    	StringBuffer queryBuf = new StringBuffer();
    	ResultSet rs = null;
    	HashMap valueMap = new HashMap();
    	double dInterTax =0;
    	try{
    		queryBuf.append(" select * from ").append(pub.yssGetTableName("tb_stock_cashpayrec"));
    		queryBuf.append(" where fcheckstate=1 and fyearmonth <>").append(dbl.sqlString(YssFun.getYear(YssFun.addDay(dDate, -1))+"00"));
    		queryBuf.append(" and FTSFTYPECODE in ('07','06') and FSUBTSFTYPECODE in('07LXS_DE','06DE') and fportcode=").append(dbl.sqlString(accPaid.getPortCode()));
    		queryBuf.append(" and FCASHACCCODE =").append(dbl.sqlString(accPaid.getCashAccCode()));
    		queryBuf.append(" and FSTORAGEDATE =").append(dbl.sqlDate(YssFun.addDay(accPaid.getDDate(), -1)));
    		rs = dbl.openResultSet(queryBuf.toString());
    		while(rs.next()){
    			valueMap.put(rs.getString("FSUBTSFTYPECODE").trim()+"\tbal", rs.getDouble("fbal"));
    			valueMap.put(rs.getString("FSUBTSFTYPECODE").trim()+"\tbasebal", rs.getDouble("fbasecurybal"));
    			valueMap.put(rs.getString("FSUBTSFTYPECODE").trim()+"\tportbal", rs.getDouble("fportcurybal"));
    		}
    	}catch(Exception e){
    		throw new YssException("获取存款利息税金额出错......");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    	return valueMap;
    }
}
