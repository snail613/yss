package com.yss.main.operdeal.opermanage.securitymanage;

import java.util.*;

import com.yss.commeach.EachRateOper;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.opermanage.*;
import com.yss.util.*;
import com.yss.main.parasetting.*;
import java.sql.*;
import com.yss.manager.*;

/**
 *
 * <p>Title: 证券业务处理</p>
 *
 * <p>Description: MS00022    国内债券业务    QDV4.1赢时胜（上海）2009年4月20日22_A</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable 2009.07.03 蒋锦 添加
 * @version 1.0
 */
public class CtlSecManage
    extends BaseOperManage {

    protected boolean analy1;
    protected boolean analy2;
    protected boolean analy3;

    public CtlSecManage() {
    }

    /**
    * 初始化信息
    *
    * @param dDate Date 处理日期
    * @param portCode String 组合代码
    * @throws YssException
    * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage method
    */
   public void initOperManageInfo(java.util.Date dDate, String portCode) throws YssException {
       try{
           this.dDate = dDate;
           this.sPortCode = portCode;
           analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
           analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
           analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
       } catch(Exception ex){
           throw new YssException(ex.getMessage(), ex);
       }
   }


    /**
     * 执行业务处理
     *
     * @throws YssException
     * @todo Implement this com.yss.main.operdeal.opermanage.BaseOperManage method
     */
    public void doOpertion() throws YssException {
        ArrayList alCashTrans = new ArrayList();
        ArrayList alRecPay = new ArrayList();
        ArrayList alCashRecPay = new ArrayList();
        ArrayList alIntegrated = new ArrayList();
        ArrayList alSecurity = new ArrayList();
        ArrayList inBankIntegrated = new ArrayList();//add by zhouwei 20120423 银行间债券产生的综合业务数据
        //----新增
        ArrayList alCash = new ArrayList(); //网下新股新债业务 现金应收应付
        CashPayRecAdmin CashPayAdmin = new CashPayRecAdmin();
        //---
        SecRecPayAdmin recPayAdmin = new SecRecPayAdmin();
        CashTransAdmin cashAdmin = new CashTransAdmin();
        SecIntegratedAdmin integratedAdmin = new SecIntegratedAdmin(pub);
        CashPayRecAdmin cashRecAdmin = new  CashPayRecAdmin();
        String sCashTransNum = "";
        String sTradeNum = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            recPayAdmin.setYssPub(pub);
            cashAdmin.setYssPub(pub);
            cashRecAdmin.setYssPub(pub);

            //插入债券利息
            BondInterestManage bondManage = new BondInterestManage(pub);
            bondManage.calBondInterestManage(dDate, sPortCode);

            //处理新股新债业务
            NewIssueManage newIssue = new NewIssueManage(pub);
            newIssue.setAnaly1(analy1);
            newIssue.setAnaly2(analy2);
            newIssue.setAnaly3(analy3);
            newIssue.setPortCode(sPortCode);
            newIssue.setBargainDate(dDate);


            //处理银行间债券业务
            InterBankBondManage interBank = new InterBankBondManage(pub);
            interBank.setAnaly1(analy1);
            interBank.setAnaly2(analy2);
            interBank.setAnaly3(analy3);
            interBank.setPortCode(sPortCode);
            interBank.setBargainDate(dDate);

            //处理债券转托管业务
            DevTrustBondManage devTrust = new DevTrustBondManage(pub);
            devTrust.setAnaly1(analy1);
            devTrust.setAnaly2(analy2);
            devTrust.setAnaly3(analy3);
            devTrust.setPortCode(sPortCode);
            devTrust.setBargainDate(dDate);

            //网上新股新债业务
            //edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B 添加ArrayList alIntegrated参数
            sTradeNum = newIssue.newIssueTradeDeal(alCash,alIntegrated);
            //网下新股新债业务
            newIssue.newIssueOTCDeal(alCashTrans, alIntegrated, alRecPay, alCash);         
            //债券转托管
            devTrust.doManage(alRecPay, alIntegrated, alSecurity);
            
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Integrated"));
            integratedAdmin.addList(alIntegrated);
            //------ modify by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B  
            integratedAdmin.insert(dDate,
                                   sPortCode,
                                   YssOperCons.YSS_JYLX_Buy + "," +
                                   YssOperCons.YSS_JYLX_Sale + "," +
                                   YssOperCons.YSS_JYLX_YHJZQCX + "," +
                                   YssOperCons.YSS_JYLX_WSZQ + "," +
                                   YssOperCons.YSS_JYLX_ZFZQ + "," +
                                   YssOperCons.YSS_JYLX_WXZQ + "," +
                                   YssOperCons.YSS_JYLX_XGLT + "," +
                                   YssOperCons.YSS_JYLX_XZLT + "," +
                                   YssOperCons.YSS_JYLX_ZQZTG,
                                   true,"securitymanage,SecRecPay");
            //--------------------------- MS01098 ------------------------//
            //插入新的证券信息
            insertSecurity(alSecurity);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            //modify by zhouwei 20120423 由于银行间债券成本计算需要考虑到转托管生成综合业务数据，所以银行间债券业务放在最后处理
            //银行间债券业务
            interBank.interBankBondDeal(alCashTrans, alRecPay, inBankIntegrated, alCashRecPay);
            integratedAdmin = new SecIntegratedAdmin(pub);
            conn.setAutoCommit(false);
            bTrans = true;
            integratedAdmin.addList(inBankIntegrated);
            integratedAdmin.insert(dDate,
                    sPortCode,
                    "",
                    true,"IBB");
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //-------------------end----------------------
            
            conn.setAutoCommit(false);
            bTrans = true;
            //银行间的
            cashRecAdmin.getList().addAll(alCashRecPay);
            cashRecAdmin.insert("",
                                dDate,
                                dDate,
                                "",
                                "",
                                "","",
                                sPortCode,
                                "","","",
                                0,
                                true,
                                false,
                                false,
                                0,
                                "",
                                YssOperCons.YSS_SECRECPAY_RELATYPE_INTERBANKBOND);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //------------
            CashPayAdmin.setYssPub(pub);
            CashPayAdmin.getList().addAll(alCash);
            conn.setAutoCommit(false);
            bTrans = true;
            //删除交易子表产生的数据
            CashPayAdmin.delete("",
                               dDate,
                               dDate,
                               "", "", "", "",
                               sPortCode,
                               "","", "",
                               0,
                               0,
                               sTradeNum,
                               YssOperCons.YSS_SECRECPAY_RELATYPE_SUBTRADE);
            //网下的
            CashPayAdmin.insert("",
                            dDate,
                            dDate,
                            "06,02",
                            "","","",
                            sPortCode,
                            "","","",
                            0,
                            true,
                            false,
                            false,
                            0,
                            "",
                            YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------


            conn.setAutoCommit(false);
            bTrans = true;
            cashAdmin.addList(alCashTrans);
            cashAdmin.insert("", null,
                             dDate,
                             "05", "",
                             "", "", "", "", "",
                             YssOperCons.YSS_SECRECPAY_RELATYPE_INTERBANKBOND + "," + YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE,
                             "",
                             0, "", sPortCode,
                             0, "", "", "", true, "");
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            conn.setAutoCommit(false);
            bTrans = true;
            recPayAdmin.addList(alRecPay);
            recPayAdmin.insert("",
                               dDate,
                               dDate,
                               "",
                               "",
                               sPortCode,
                               "", "", "", "",
                               0,
                               true,
                               0,
                               false,
                               "", "",
                               YssOperCons.YSS_SECRECPAY_RELATYPE_INTERBANKBOND + "," +
                               YssOperCons.YSS_SECRECPAY_RELATYPE_DEVTRUSTBOND + "," +
                               YssOperCons.YSS_SECRECPAY_RELATYPE_NEWISSUE);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
          
            //add by zhouwei 20120321 保存固定费用的现金应收应付数据
            ArrayList fixedCashRecPay=new ArrayList();
            this.generateFixedFee(fixedCashRecPay);
            CashPayRecAdmin cashFixedFee = new  CashPayRecAdmin();
            cashFixedFee.setYssPub(pub);
            conn.setAutoCommit(false);
            bTrans = true;
            cashFixedFee.getList().addAll((fixedCashRecPay));
            /* YssOperCons.YSS_ZJDBZLX_TF+","
                    +YssOperCons.YSS_ZJDBZLX_TF_EQ+","
                    +YssOperCons.YSS_ZJDBZLX_TF_FI+","+
                    YssOperCons.YSS_ZJDBZLX_TF_DE*/
            cashFixedFee.insert("",
                    dDate,
                    dDate,
                    "",
                    "",
                    "","",
                    sPortCode,
                    "","","",
                    0,
                    true,
                    false,
                    false,
                    0,
                    "",
                    YssOperCons.YSS_CASHRECPAY_RELATYPE_FIXEDFEE);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
          //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
            if((alCashRecPay==null || alCashRecPay.size()==0)&&(fixedCashRecPay==null || fixedCashRecPay.size()==0)&&(alCash==null || alCash.size()==0)&&(alIntegrated==null || alIntegrated.size()==0)){
            	this.sMsg="        当日无业务";
            }
            

        } catch (Exception ex) {
            throw new YssException("执行证券业务处理出错！", ex);
        } finally{
            dbl.endTransFinal(conn, bTrans);
        }
    }
    /**add by zhouwei 20120321
     * 构造固定交易费用的现金应收应付数据
     * @param fixedCashRecPay
     * @throws YssException
     */
    public void generateFixedFee(ArrayList fixedCashRecPay) throws YssException{
		ResultSet rs=null;
		String sqlStr="";
		try{
			sqlStr="select  a.FPortCode,a.FInvMgrCode,a.FTradeTypeCode,a.FCashAccCode,a.FBargainDate,a.FNum,a.fbussinessType,a.fsavingType ,b.fcatcode,b.ftradecury,0 as Flag from "  //modify by zhangjun 2012.06.15 story#2579
				  +" (select FSecurityCode,FPortCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,FBargainDate,FNum,'0' as fbussinessType,'1' as fsavingType"
				  +" from "+pub.yssGetTableName("tb_Data_SubTrade")//网上交易
				  +" where fcheckstate=1 and FBargainDate="+dbl.sqlDate(dDate)
				  +" and FPortCode="+dbl.sqlString(sPortCode)
				  +" union all"
				  +" select FSecurityCode,FPortCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,FBargainDate,FNum,'1' as fbussinessType,'1' as fsavingType"
				  +" from "+pub.yssGetTableName("tb_Data_Purchase")//场外回购业务
				  +" where fcheckstate=1 and FBargainDate="+dbl.sqlDate(dDate)
				  +" and FPortCode="+dbl.sqlString(sPortCode)
				  +" union all"
				  +" select FSecurityCode,FPortCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,FBargainDate,FNum,'2' as fbussinessType,'1' as fsavingType"
				  +" from "+pub.yssGetTableName("tb_Data_IntBakBond")//银行间债券业务
				  +" where fcheckstate=1 and FBargainDate="+dbl.sqlDate(dDate)
				  +" and FPortCode="+dbl.sqlString(sPortCode)
				  +" union all"
				  +" select FSecurityCode,FPortCode,FInvMgrCode,FTradeTypeCode,FCashAccCode,FBargainDate,FNum,'3' as fbussinessType,'1' as fsavingType"
				  +" from "+pub.yssGetTableName("tb_Data_NewIssueTrade")//网下新股新债业务
				  +" where fcheckstate=1 and FBargainDate="+dbl.sqlDate(dDate)
				  +" and  FTradeTypeCode='40'"//申购
				  +" and FPortCode="+dbl.sqlString(sPortCode)
				  +" union all"
				  +" select FSecurityCode,FPortCode,FInvMgrCode,FTradeTypeCode,FApplyCashAccCode as FCashAccCode,FBargainDate,FNum,'4' as fbussinessType,'1' as fsavingType"
				  +" from "+pub.yssGetTableName("tb_Data_OpenFundTrade")//开放式基金业务
				  +" where fcheckstate=1 and FBargainDate="+dbl.sqlDate(dDate)
				  +" and  FDataType='apply' "//申请
				  +" and FPortCode="+dbl.sqlString(sPortCode)
				  +" ) a left join ("
				  +" select sec.fsecuritycode,sec.ftradecury,cat.fcatcode from "+pub.yssGetTableName("tb_Para_Security")+" sec "
				  +" join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) cat"
				  +" on sec.fcatcode=cat.fcatcode where sec.fcheckstate=1 ) b on a.fsecuritycode=b.fsecuritycode"
				  +" union all"
				  +" select x.FPortCode,' ' as FInvMgrCode,ftradetype as FTradeTypeCode,x.FCashAccCode,FSavingDate as FBargainDate,FNum,'6' as fbussinessType,"
				  +" ( FSavingType || '') as FSavingType,' ' as fcatcode,y.fcurycode as ftradecury,x.Flag  from "+pub.yssGetTableName("tb_Cash_SavingInAcc")+" x"  //modify by zhangjun 2012.06.15 story#2579
				  +" left join "+pub.yssGetTableName("Tb_Para_CashAccount")+" y on x.fcashacccode=y.fcashacccode"
				  +" where x.fcheckstate=1  and x.FSavingDate="+dbl.sqlDate(dDate)
				  +" and  x.FPortCode="+dbl.sqlString(sPortCode);
			rs=dbl.openResultSet(sqlStr);
			setFixedFeeList(rs,fixedCashRecPay);
		}catch (Exception e) {
			throw new YssException("生成固定交易费用出错!", e);
		}finally{	
			dbl.closeResultSetFinal(rs);
		}	
    }
    /**
     * add by zhouwei 20120321
     * 根据条件获取，固定费用，并对现金应收应付对象赋值
     * @param rs
     * @param fixedCashRecPay
     * @param an1
     * @param an2
     * @throws YssException
     */
    public void setFixedFeeList(ResultSet rs,ArrayList fixedCashRecPay) throws YssException{
    	CashPecPayBean recPay=null;
    	String bussinessType="";
    	FixedFeeCfgBean fixFeeCfg=new FixedFeeCfgBean();
    	fixFeeCfg.setYssPub(pub);
    	double feeMoney=0;
    	double baseCuryRate = 0;
        double portCuryRate = 0;
        try{
	    	while(rs.next()){
	    		//固定交易费用的所属类别 0-网上交易，1-场外回购业务，2-银行间债券业务，3-网下新股新债业务，4-开放式基金业务，5-债券转托管业务，6-存款业务
	    		bussinessType=rs.getString("fbussinessType");
	    		 //------------------------------现金应收应付---------------------------------------//
	    		EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
	            rateOper.setYssPub(pub);
	            baseCuryRate = this.getSettingOper().getCuryRate(rs.getDate("FBargainDate"),
	                rs.getString("FTradeCury"), rs.getString("FPortCode"),
	                YssOperCons.YSS_RATE_BASE);
	
	            rateOper.getInnerPortRate(rs.getDate("FBargainDate"), rs.getString("FTradeCury"),
	                                      rs.getString("FPortCode"));
	            portCuryRate = rateOper.getDPortRate();
	            
	            recPay = new CashPecPayBean();
	            recPay.setTradeDate(rs.getDate("FBargainDate"));
	            recPay.checkStateId = 1;
	            recPay.setRelaNum(rs.getString("FNum"));
	            recPay.setRelaNumType(YssOperCons.YSS_CASHRECPAY_RELATYPE_FIXEDFEE);
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
	            recPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
	            //获取固定交易费用设置
	            
	            String[] arrStr=fixFeeCfg.getFixedFeeMoney(rs.getDate("FBargainDate"), bussinessType, rs.getString("FPortCode"),
	            		rs.getString("FTradeTypeCode"), rs.getString("FSavingType"), rs.getString("FCatCode"),rs.getDouble("Flag")).split("\t");  //modify by zhangjun 2012.06.16 Story#2579
	            
	            feeMoney=YssFun.toDouble(arrStr[0]);
	            if(feeMoney==0){
	            	continue;
	            }
	            recPay.setSubTsfTypeCode(arrStr[1]);
	            if(arrStr[2]!=null && !arrStr[2].equals("") && !arrStr[2].equals("null")){//现金账户
	            	recPay.setCashAccCode(arrStr[2]); 
	            }else{
	            	recPay.setCashAccCode(rs.getString("FCashAccCode")); 
	            }
//	            if(bussinessType.equals("6")){//存款
//	                recPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_TF_DE);
//	            }else
//	            {
//	            	if(rs.getString("FCatCode").equalsIgnoreCase("EQ")){
//	            		recPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_TF_EQ);
//	            	}else if(rs.getString("FCatCode").equalsIgnoreCase("FI")){
//	            		recPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_TF_FI);
//	            	}else{
//	            		continue;
//	            	}
//	            }
	            recPay.setInOutType(1);
	            recPay.setDataSource(0);
	            recPay.setStockInd(0);           
	            recPay.setMoney(feeMoney);
	            recPay.setBaseCuryRate(baseCuryRate);
	            recPay.setPortCuryRate(portCuryRate);
	            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
	                baseCuryRate, 2));
	            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
	                baseCuryRate, portCuryRate,
	                rs.getString("FTradeCury"), rs.getDate("FBargainDate"), recPay.getPortCode(), 2));
	
	            fixedCashRecPay.add(recPay);
	    	}
        }catch (Exception e) {
        	throw new YssException("固定交易费用对象赋值出错!", e);
		}
    }
    /**
     * 插入债券转托管业务生成的证券信息
     * @param alSecurity ArrayList
     * @throws YssException
     */
    private void insertSecurity(ArrayList alSecurity) throws YssException{
        SecurityBean security = null;
        ResultSet rs = null;
        String strSql = "";
        String insertSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            for(int i = 0; i < alSecurity.size(); i++){
                security = (SecurityBean)alSecurity.get(i);
                strSql = "SELECT FSecurityCode, FCheckState FROM " + pub.yssGetTableName("Tb_Para_Security") +
                    " WHERE FSecurityCode = " + dbl.sqlString(security.getStrSecurityCode());
                rs = dbl.queryByPreparedStatement(strSql);
                if(rs.next()){
                    if(rs.getInt("FCheckState") != 1){
                        throw new YssException("证券代码【" + rs.getString("FSecurityCode") + "】已被反审核或已被删除，请修改证券信息设置，令证券【"+ rs.getString("FSecurityCode") + "】处以已审核状态！");
                    }
                    continue;
                } else {
                    conn.setAutoCommit(false);
                    bTrans = true;
                    //--------------2010.01.26 国内基金股票债券合并中  蒋锦 修改 pub 等公共表并未在这测合并所以先去除SQL中的组合群代码-----------// 
                    insertSql = "INSERT INTO " + pub.yssGetTableName("Tb_Para_Security") +
                        " (FSECURITYCODE, " + 
                        //"FASSETGROUPCODE, " +
                        "FSTARTDATE, FSECURITYNAME, FSECURITYSHORTNAME, FSECURITYCORPNAME, FCATCODE, FSUBCATCODE, FCUSCATCODE, FEXCHANGECODE, " +
                        "FMARKETCODE, FEXTERNALCODE, FISINCODE, FTRADECURY, FHOLIDAYSCODE, FSETTLEDAYTYPE, FSETTLEDAYS, FSECTORCODE, FTOTALSHARE, FCURRENTSHARE, FHANDAMOUNT, FFACTOR, FISSUECORPCODE, FDESC, FCHECKSTATE, FCREATOR, FCREATETIME)" +
                        " SELECT " + dbl.sqlString(security.getStrSecurityCode()) + " AS FSecurityCode, " +
                        //"FASSETGROUPCODE, " +
                        "FSTARTDATE, FSECURITYNAME, FSECURITYSHORTNAME, FSECURITYCORPNAME, FCATCODE, FSUBCATCODE, FCUSCATCODE, "+
                        dbl.sqlString(security.getStrExchangeCode()) +
                        " AS FEXCHANGECODE, FMARKETCODE, FEXTERNALCODE, FISINCODE, FTRADECURY, FHOLIDAYSCODE, FSETTLEDAYTYPE, FSETTLEDAYS, FSECTORCODE, FTOTALSHARE, FCURRENTSHARE, FHANDAMOUNT, FFACTOR, FISSUECORPCODE, FDESC, FCHECKSTATE, " +
                        dbl.sqlString(pub.getUserCode()) + " AS FCREATOR, " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + " AS FCREATETIME " +
                        " FROM " + pub.yssGetTableName("Tb_Para_Security") +
                        " WHERE FSecurityCode = " + dbl.sqlString(security.getStrOldSecurityCode());
                        //" AND FASSETGROUPCODE = " + dbl.sqlString(security.getSAssetGroupCode());
                    //----------------------------------//
                    dbl.executeSql(insertSql);
                    insertSql = "INSERT INTO " + pub.yssGetTableName("Tb_Para_Fixinterest") +
                        " (FSECURITYCODE, FSTARTDATE, FISSUEDATE, FISSUEPRICE, FINSSTARTDATE, FINSENDDATE, FINSCASHDATE, FFACEVALUE, FFACERATE, FINSFREQUENCY, FQUOTEWAY, FCREDITLEVEL, FCALCINSMETICDAY, FCALCINSMETICBUY, FCALCINSMETICSELL, FCALCPRICEMETIC, FAMORTIZATION, FFACTRATE, FCALCINSCFGDAY, FCALCINSCFGBUY, FCALCINSCFGSELL, FCALCINSWAY, FINTERESTORIGIN, FPEREXPCODE, FPERIODCODE, FROUNDCODE, FDESC, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME, FBEFOREFACERATE,FINTERTAXPEREXPCODE)" +//add by zhouwei 20120419 增加利息税字段
                        " SELECT " + dbl.sqlString(security.getStrSecurityCode()) + " AS FSecurityCode, FSTARTDATE, FISSUEDATE, FISSUEPRICE, FINSSTARTDATE, FINSENDDATE, FINSCASHDATE, FFACEVALUE, FFACERATE, FINSFREQUENCY, FQUOTEWAY, FCREDITLEVEL, FCALCINSMETICDAY, FCALCINSMETICBUY, FCALCINSMETICSELL, FCALCPRICEMETIC, FAMORTIZATION, FFACTRATE, FCALCINSCFGDAY, FCALCINSCFGBUY, FCALCINSCFGSELL, FCALCINSWAY, FINTERESTORIGIN, FPEREXPCODE, FPERIODCODE, FROUNDCODE, FDESC, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME, FBEFOREFACERATE,FINTERTAXPEREXPCODE " +
                        " FROM " + pub.yssGetTableName("Tb_Para_Fixinterest") +
                        " WHERE FSecurityCode = " + dbl.sqlString(security.getStrOldSecurityCode());
                    dbl.executeSql(insertSql);
                    conn.commit();
                    bTrans = true;
                    conn.setAutoCommit(true);

                    ArrayList alSecCode = new ArrayList();
                    alSecCode.add(security.getStrSecurityCode());
                    MTVMethodLinkBean mtvLink = new MTVMethodLinkBean();
                    mtvLink.setYssPub(pub);
                    mtvLink.operSecurityLinkMtvMethod(alSecCode, "add");
                }
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        } finally{
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
