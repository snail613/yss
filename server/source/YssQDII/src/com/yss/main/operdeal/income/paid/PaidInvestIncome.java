package com.yss.main.operdeal.income.paid;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.cashmanage.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.*;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.income.stat.StatInvestFee;
import com.yss.main.operdeal.opermanage.OperInvestPay;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.pojo.dayfinish.*;
import com.yss.util.*;

public class PaidInvestIncome
    extends BaseIncomePaid {
    public PaidInvestIncome() {
    }

    public void calculateIncome(IYssConvert bean) {
    }

    public ArrayList getIncomes() throws YssException {
        ArrayList alResult = new ArrayList();
        try {
            alResult.addAll(getDayIncomes(dDate));
            return alResult;
        } catch (Exception e) {
            throw new YssException("获取债券利息数据出错", e);
        }
    }

    /**
     * 修改记录：
     * V1. 修改人 ： 王作春、潘君方 @ 20090624
     *    BugNO ： MS00017:QDV4.1赢时胜（上海）2009年4月20日17_A
     *    修改内容：新增《国内预提待摊》业务的处理
     * @param dDate Date
     * @return ArrayList
     * @throws YssException
     */
    protected ArrayList getDayIncomes(java.util.Date dDate) throws YssException { 
        ArrayList alPaid = new ArrayList();
    	ResultSet rsTemp = null;
        InvestPaid paid;
        String strSql = "";
        String strCashAcc = "";
        ResultSet rsCashAcc = null;
        String CashAccCode = " ";
        String CashAccName = " ";
        ResultSet rs = null;
        double BaseCuryRate;
        double PortCuryRate;
        BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.
            getOperDealCtx().getBean("cashacclinkdeal");
        CashAccountBean caBean = null;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 --------------------------
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------------------------------
        //add by fangjiang 2011.02.11 #2279
        ResultSet rs1 = null;//声明结果集
        ResultSet rs2 = null;//声明结果集
        StatInvestFee invest = new StatInvestFee();
        invest.setYssPub(pub);
        //----------------------
        try {
            strSql = "select y.*,  m.FPortName, n.FInvMgrName, bro.FBrokerName as FAnalysisName2, m.FPortCury, cury.FCuryName,pcat.FPayType " + // MS00237 QDV4中保2009年02月05日01_A
                ",cat.FCatName as FAnalysisName3,h.fassetgroupcode, h.fassetgroupname,kk.FFeeType,kk.FFeeTypeCode,jj.FASetTypeName,CA.FCashAccName as CashAccName " +
                " from " +
                "(select * from (select * from " +
                pub.yssGetTableName("Tb_Stock_Invest") +
                //" where " + operSql.sqlStoragEve(dDate) + //不获取起初数 sj 20080102
                " where " + this.sqlStoragEve(dDate) + //BUG3688 modified by yeshenghong 20120208
                " and FPortCode in (" +
                operSql.sqlCodes(portCodes)

                + ") and FcheckState = 1" +
                //MS00017 运营品种类型为预提和两费的才存在有收益支付，待摊不存在收益支付 panjunfang
                " and FIVPayCatCode in (select FIVPayCatCode from Tb_Base_InvestPayCat where FIVTYPE in('accruedFee','managetrusteeFee'))" +
                ") a" +
                " left join (select FIVPayCatCode as bfivpaycatcode,FIVPayCatName from Tb_Base_InvestPayCat where fcheckstate = 1) b" +
                " on a.FIVPayCatCode = b.bfivpaycatcode " +
                //MS00017 从运营收支品种设置中关联出现金账户代码 panjunfang modify 20090814
                
                //20120220 modified by liubo.Story #2139
                //添加FTransition，FTransitionDate两个字段，用于过滤掉预提转待摊中，FTransitionDate=1且FTransitionDate(转换日期)大于等于支付日期的情况
                //=======================================
                " left join (select iip.iipfivpaycatcode as ipfivpaycatcode,iip.iipfportcode as ipfportcode,iiip.cashacccode,iip.FTransition,iip.FTransitionDate from " +
                " (select fivpaycatcode as iipfivpaycatcode,fportcode as iipfportcode,max(fstartdate) as iipfstartdate,FTransition,FTransitionDate from " + pub.yssGetTableName("tb_para_investpay") +
                
                " where  FCheckState = 1 group by fivpaycatcode,fportcode,FTransition,FTransitionDate) iip left join " +
                
                //=====================end==================
                " (select FCashAccCode as CashAccCode,FPortCode as iiipfportcode,FIVPayCatCode as iiipfivpaycatcode, FStartDate as iiipfstartdate from "
                + pub.yssGetTableName("tb_para_investpay") +
                " where FCheckState = 1) iiip on iiip.iiipfivpaycatcode = iip.iipfivpaycatcode and iiip.iiipfportcode = iip.iipfportcode and iiip.iiipfstartdate = iip.iipfstartdate)" +
                " ip on ip.ipfivpaycatcode = a.FIVPayCatCode  and ip.ipfportcode = a.FPORTCODE" +
                " ) y " +
                "  left join (select FIVPayCatCode,FPayType from Tb_Base_InvestPayCat) pcat on pcat.FIVPayCatCode = y.FIVPayCatCode " +
                //MS00017 增加运营收支品种类型名称列 add by wangzuochun 2009.06.24
                /**add---shashijie 2013-02-04 STORY 3513 没有BUG编号Tb_Base_InvestPayCat运营收支品种设置新加字段FFeeType与原有别名字段重复SQL报错*/
                " left join (select a.FIVPayCatCode,fiv.FVocName as FFeeType, fiv.FVocCode as FFeeTypeCode from Tb_Base_InvestPayCat a " +
                /**end---shashijie 2013-02-04 */
                " left join Tb_Fun_Vocabulary fiv on a.FIVType = fiv.FVocCode and fiv.FVocTypeCode = 'fiv_feeType') kk " +
                " on kk.FIVPayCatCode = y.FIVPayCatCode " +
                //MS00017 增加资产类型名称列
                " left join (select a.*,fiv.FVocName as FASetTypeName from Tb_Base_InvestPayCat a " +
                " left join Tb_Fun_Vocabulary fiv on a.FPayType = fiv.FVocCode and fiv.FVocTypeCode = 'account_Subject') jj " +
                " on jj.FIVPayCatCode = y.FIVPayCatCode " +
                " left join (select FCuryCode, FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) cury on y.FCuryCode = cury.FCuryCode" +

                "  left join (select FPortCode, FPortName,FPortCury from " +
                pub.yssGetTableName("tb_para_portfolio") +
                " where fcheckstate=1" + //BUG1505查询#018组合业务日期2011-01-31两费收支数据时发现，关联到组合#018错误  modify by jiangshichao 2011.03.16 添加过滤条件为审核状态下的组合
                " ) m on y.FPortCode = m.FPortCode" +
                "  left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FCheckState = 1) n on y.FAnalysisCode1 = n.FInvMgrCode" +
                "  left join (select FBrokerCode, FBrokerName from " +
                pub.yssGetTableName("Tb_Para_Broker") +
                " where FCheckState = 1) bro on y.FAnalysisCode2 = bro.FBrokerCode" +
                //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
                "  left join (select FCatCode, FCatName from " +
                pub.yssGetTableName("Tb_Base_Category") +
                " where FCheckState = 1) cat on y.FAnalysisCode3 = cat.FCatCode " +
                //--从现金账户表中关联出现金账户名称，panjunfang add 20090709，MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A--
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
            
                " left join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") + " where  FCheckState = 1 and FState =0 ) CA on CA.FCashAccCode = y.CashAccCode" +
             
                //end by lidaolong
                //--------------------------------------------------------------------------------//
                // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
                //=====================================================================================
                " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
                pub.getPrefixTB() + "' ";
            //=============================================================================================
            if (!this.isAll.equalsIgnoreCase("true")) {
                strSql = strSql + " where y.FStorageDate <= " +
                    dbl.sqlDate(YssFun.addDay(this.dDate, -1));
            }
            //20120220 added by liubo.Story #2139
            //添加FTransition，FTransitionDate两个字段，用于过滤掉预提转待摊中，FTransitionDate=1且FTransitionDate(转换日期)大于等于支付日期的情况
            //=======================================
            strSql = strSql + "and ((y.FTransition <> '1') or (y.FTransition = '1' and y.FTransitionDate > " + dbl.sqlDate(this.dDate) +"))";
            //====================end===================
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
            	
            	//20120524 added by liubo.Story #2217
            	//要求设置了自动支付的两费项目不要显示在收益支付中。
            	//=====================================
            	strSql = "select * from " + pub.yssGetTableName("tb_para_investpay") + " where FIVPAYCATCODE = " + dbl.sqlString(rs.getString("FIVPayCatCode")) +
            			 " and FPORTCODE = " + dbl.sqlString(rs.getString("FPortCode")) + " and FAUTOPAY = '1'";
            	rsTemp = dbl.queryByPreparedStatement(strSql);
            	if (rsTemp.next())
            	{
            		dbl.closeResultSetFinal(rsTemp);
            		continue;
            	}
            	dbl.closeResultSetFinal(rsTemp);
            	//=================end====================
            	
                paid = new InvestPaid();
                // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090526
                paid.setAssetGroupCode(rs.getString("fassetgroupcode"));
                paid.setAssetGroupName(rs.getString("fassetgroupname"));
                //---------------------------------------------------------------------------------------------
                paid.setDDate(this.dDate);
                paid.setIVPayCatCode(rs.getString("FIVPayCatCode"));
                paid.setIVPayCatName(rs.getString("FIVPayCatName"));
                //------ MS00017  设置各项的值 add by wangzuochun 2009.06.24
                paid.setFeeType(rs.getString("FFeeType"));
                paid.setASetTypeName(rs.getString("FASetTypeName"));
                paid.setPortCode(rs.getString("FPortCode"));
                paid.setPortName(rs.getString("FPortName"));
                paid.setCuryCode(rs.getString("FCURYCODE"));
                paid.setCuryName(rs.getString("FCURYNAME"));
                paid.setAnalysisCode1(rs.getString("FAnalysisCode1"));
                paid.setAnalysisName1(rs.getString("FInvMgrName"));
                paid.setAnalysisCode2(rs.getString("FAnalysisCode2"));
                paid.setAnalysisName2(rs.getString("FAnalysisName2")); //MS00237 QDV4中保2009年02月05日01_A
                //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
                paid.setAnalysisCode3(rs.getString("FAnalysisCode3"));
                paid.setAnalysisName3(rs.getString("FAnalysisName3"));
                //--------------------------------------------------------------------------------//
                paid.setPayType(rs.getInt("FPayType"));
                paid.setMoney(rs.getDouble("FBal"));
                paid.setBaseCuryMoney(rs.getDouble("FBaseCuryBal"));
                paid.setPortCuryMoney(rs.getDouble("FPortCuryBal"));
                paid.setMatureDate(rs.getDate("FStorageDate"));
                //当天的基础汇率
                BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FCuryCode"), paid.getPortCode(),
                    YssOperCons.YSS_RATE_BASE);
                //当天的组合汇率
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090420 --------------------------
                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                          paid.getPortCode());
                PortCuryRate = rateOper.getDPortRate();
                //-----------------------------------------------------------------------------------

                //获取现金帐户
                cashacc.setYssPub(pub);
                cashacc.setLinkParaAttr(rs.getString("FAnalysisCode1"),
                                        rs.getString("FPortCode"),
                                        "", "",
                                        "", "",
                                        "", dDate, rs.getString("FCuryCode"));

                caBean = cashacc.getCashAccountBean();
                if (caBean != null) {
                    CashAccCode = caBean.getStrCashAcctCode();
                    if (CashAccCode == null) {
                        CashAccCode = "";
                    }
                }
                strCashAcc = "select FCashAccName from " +
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where FCheckState = 1 and FCashAccCode = " +
                    dbl.sqlString(CashAccCode);
                rsCashAcc = dbl.queryByPreparedStatement(strCashAcc); //modify by fangjiang 2011.08.14 STORY #788
                if (rsCashAcc.next()) {
                    CashAccName = rsCashAcc.getString("FCashAccName");
                }
                if (rsCashAcc != null) {
                    dbl.closeResultSetFinal(rsCashAcc);
                }
                //modify by fangjiang 2011.02.11 #2279
                String strCashAccCode = "";
                String strCashAccCode1 = "";
                String strCashAccName = "";
                if(invest.getAutoCharge(rs.getString("FPortCode"))&& rs.getString("CashAccCode").indexOf(",") != -1){
                	strSql = " select FCuryCode, FCashAccCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+
       	         			 " where FCheckState =1 and FCashAccCode in ("+operSql.sqlCodes(rs.getString("CashAccCode"))+")";
			       	rs1 = dbl.queryByPreparedStatement(strSql);  //modify by fangjiang 2011.08.14 STORY #788
			       	while(rs1.next()){
			       		if(rs1.getString("FCuryCode").equalsIgnoreCase(rs.getString("FCuryCode"))){
			       			strCashAccCode1 = rs1.getString("FCashAccCode");
			       			strSql = " select FCashAccName from "+pub.yssGetTableName("Tb_Para_CashAccount")+
      	         			 		 " where FCashAccCode = " +  dbl.sqlString(strCashAccCode1);
			       			rs2 = dbl.queryByPreparedStatement(strSql);  //modify by fangjiang 2011.08.14 STORY #788
			       			while(rs2.next()){
			       				strCashAccName += rs2.getString("FCashAccName")+",";
			       			}
			       			strCashAccCode += strCashAccCode1 + ",";
			       		}	
			       	}
			       	if(strCashAccCode.endsWith(",")){
			       		strCashAccCode = strCashAccCode.substring(0, strCashAccCode.length()-1);
			       	}
			       	if(strCashAccName.endsWith(",")){
			       		strCashAccName = strCashAccName.substring(0, strCashAccName.length()-1);
			       	}
			       	paid.setCashAccCode(strCashAccCode);
			       	paid.setCashAccName(strCashAccName);
                }else{
                	paid.setCashAccCode(rs.getString("CashAccCode"));//收益支付默认现金账户和运营收支品种设置中的一致，panjunfang add 20090709，MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                	paid.setCashAccName(rs.getString("CashAccName"));//panjunfang add 20090709，MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
                }
                //---------------------------
                paid.setBaseCuryRate(BaseCuryRate);
                paid.setPortCuryRate(PortCuryRate);

                alPaid.add(paid);
            }
            return alPaid;
        } catch (Exception e) {
            throw new YssException("取应收利息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs,rsTemp);
            dbl.closeResultSetFinal(rs1); //add by fangjiang 2011.02.11 #2279
            dbl.closeResultSetFinal(rs2); //add by fangjiang 2011.02.11 #2279
        }
    }
    
    /**
     * add by huangqirong 2012-04-17 story #2326
     * @param dDate Date
     * @return ArrayList
     * @throws YssException
     */
    public InvestPaid getSingleIncomes(String portCode , java.util.Date dDate, String ivPayCatCode) throws YssException{         
        InvestPaid paid =null;
        String strSql = "";
        String strCashAcc = "";
        ResultSet rsCashAcc = null;
        String CashAccCode = " ";
        String CashAccName = " ";
        ResultSet rs = null;
        double BaseCuryRate;
        double PortCuryRate;
        BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.
            getOperDealCtx().getBean("cashacclinkdeal");
        CashAccountBean caBean = null;        
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        
        ResultSet rs1 = null;//声明结果集
        ResultSet rs2 = null;//声明结果集
        StatInvestFee invest = new StatInvestFee();
        invest.setYssPub(pub);
        //----------------------
        try {
            strSql = "select y.*,  m.FPortName, n.FInvMgrName, bro.FBrokerName as FAnalysisName2, m.FPortCury, cury.FCuryName,pcat.FPayType " + 
                ",cat.FCatName as FAnalysisName3,h.fassetgroupcode, h.fassetgroupname,kk.FFeeType,kk.FFeeTypeCode,jj.FASetTypeName,CA.FCashAccName as CashAccName " +
                " from " +
                "(select * from (select * from " +
                pub.yssGetTableName("Tb_Stock_Investpayrec") +                
                " where " + this.sqlStoragEve(dDate) + 
                " and FPortCode in (" +
                operSql.sqlCodes(portCode)

                + ") and FcheckState = 1" +
                " and FIVPayCatCode in (select FIVPayCatCode from Tb_Base_InvestPayCat where FIVTYPE in('accruedFee','managetrusteeFee')" +
                " and FIVPAYCATCODE="+dbl.sqlString(ivPayCatCode)+")" +
                ") a" +
                " left join (select FIVPayCatCode as bfivpaycatcode,FIVPayCatName from Tb_Base_InvestPayCat where fcheckstate = 1) b" +
                " on a.FIVPayCatCode = b.bfivpaycatcode " +
                " left join (select iip.iipfivpaycatcode as ipfivpaycatcode,iip.iipfportcode as ipfportcode,iiip.cashacccode,iip.FTransition,iip.FTransitionDate from " +
                " (select fivpaycatcode as iipfivpaycatcode,fportcode as iipfportcode,max(fstartdate) as iipfstartdate,FTransition,FTransitionDate from " + pub.yssGetTableName("tb_para_investpay") +
                " where  FCheckState = 1 group by fivpaycatcode,fportcode,FTransition,FTransitionDate) iip left join " +
                 " (select FCashAccCode as CashAccCode,FPortCode as iiipfportcode,FIVPayCatCode as iiipfivpaycatcode, FStartDate as iiipfstartdate from "
                + pub.yssGetTableName("tb_para_investpay") +
                " where FCheckState = 1) iiip on iiip.iiipfivpaycatcode = iip.iipfivpaycatcode and iiip.iiipfportcode = iip.iipfportcode and iiip.iiipfstartdate = iip.iipfstartdate)" +
                " ip on ip.ipfivpaycatcode = a.FIVPayCatCode  and ip.ipfportcode = a.FPORTCODE" +
                " ) y " +
                "  left join (select FIVPayCatCode,FPayType from Tb_Base_InvestPayCat) pcat on pcat.FIVPayCatCode = y.FIVPayCatCode " +
                // 增加运营收支品种类型名称列
                " left join (select a.*,fiv.FVocName as FFeeType, fiv.FVocCode as FFeeTypeCode from Tb_Base_InvestPayCat a " +
                " left join Tb_Fun_Vocabulary fiv on a.FIVType = fiv.FVocCode and fiv.FVocTypeCode = 'fiv_feeType') kk " +
                " on kk.FIVPayCatCode = y.FIVPayCatCode " +
                // 增加资产类型名称列
                " left join (select a.*,fiv.FVocName as FASetTypeName from Tb_Base_InvestPayCat a " +
                " left join Tb_Fun_Vocabulary fiv on a.FPayType = fiv.FVocCode and fiv.FVocTypeCode = 'account_Subject') jj " +
                " on jj.FIVPayCatCode = y.FIVPayCatCode " +
                " left join (select FCuryCode, FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) cury on y.FCuryCode = cury.FCuryCode" +

                "  left join (select FPortCode, FPortName,FPortCury from " +
                pub.yssGetTableName("tb_para_portfolio") +
                " where fcheckstate=1" +
                " ) m on y.FPortCode = m.FPortCode" +
                "  left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FCheckState = 1) n on y.FAnalysisCode1 = n.FInvMgrCode" +
                "  left join (select FBrokerCode, FBrokerName from " +
                pub.yssGetTableName("Tb_Para_Broker") +
                " where FCheckState = 1) bro on y.FAnalysisCode2 = bro.FBrokerCode" +
                "  left join (select FCatCode, FCatName from " +
                pub.yssGetTableName("Tb_Base_Category") +
                " where FCheckState = 1) cat on y.FAnalysisCode3 = cat.FCatCode " +
                //--从现金账户表中关联出现金账户名称
                // 清理系统界面无效启用日期，调整前后台代码            
                " left join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") + " where  FCheckState = 1 and FState =0 ) CA on CA.FCashAccCode = y.CashAccCode" +
             
                " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
                pub.getPrefixTB() + "' ";
            //=============================================================================================
            if (!this.isAll.equalsIgnoreCase("true")) {
                strSql = strSql + " where y.FStorageDate = " +
                    dbl.sqlDate(YssFun.addDay(dDate, -1));
            }
            strSql = strSql + "and ((y.FTransition <> '1') or (y.FTransition = '1' and y.FTransitionDate > " + dbl.sqlDate(dDate) +"))";
            rs = dbl.queryByPreparedStatement(strSql);
            if (rs.next()) {
                paid = new InvestPaid();
                paid.setAssetGroupCode(rs.getString("fassetgroupcode"));
                paid.setAssetGroupName(rs.getString("fassetgroupname"));
                //---------------------------------------------------------------------------------------------
                paid.setTsfTypeCode(rs.getString("FTSFTYPECODE"));
                paid.setSubTsfTypeCode(rs.getString("FSUBTSFTYPECODE"));
                paid.setDDate(dDate);
                paid.setIVPayCatCode(rs.getString("FIVPayCatCode"));
                paid.setIVPayCatName(rs.getString("FIVPayCatName"));
                paid.setFeeType(rs.getString("FFeeType"));
                paid.setASetTypeName(rs.getString("FASetTypeName"));
                paid.setPortCode(rs.getString("FPortCode"));
                paid.setPortName(rs.getString("FPortName"));
                paid.setCuryCode(rs.getString("FCURYCODE"));
                paid.setCuryName(rs.getString("FCURYNAME"));
                paid.setAnalysisCode1(rs.getString("FAnalysisCode1"));
                paid.setAnalysisName1(rs.getString("FInvMgrName"));
                paid.setAnalysisCode2(rs.getString("FAnalysisCode2"));
                paid.setAnalysisName2(rs.getString("FAnalysisName2"));
                paid.setAnalysisCode3(rs.getString("FAnalysisCode3"));
                paid.setAnalysisName3(rs.getString("FAnalysisName3"));
                //--------------------------------------------------------------------------------//
                paid.setPayType(rs.getInt("FPayType"));
                paid.setMoney(rs.getDouble("FBal"));
                paid.setBaseCuryMoney(rs.getDouble("FBaseCuryBal"));
                paid.setPortCuryMoney(rs.getDouble("FPortCuryBal"));
                paid.setMatureDate(rs.getDate("FStorageDate"));
                //当天的基础汇率
                BaseCuryRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FCuryCode"), paid.getPortCode(),
                    YssOperCons.YSS_RATE_BASE);
                //当天的组合汇率
                rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"),
                                          paid.getPortCode());
                PortCuryRate = rateOper.getDPortRate();
                //-----------------------------------------------------------------------------------

                //获取现金帐户
                cashacc.setYssPub(pub);
                cashacc.setLinkParaAttr(rs.getString("FAnalysisCode1"),
                                        rs.getString("FPortCode"),
                                        "", "",
                                        "", "",
                                        "", dDate, rs.getString("FCuryCode"));

                caBean = cashacc.getCashAccountBean();
                if (caBean != null) {
                    CashAccCode = caBean.getStrCashAcctCode();
                    if (CashAccCode == null) {
                        CashAccCode = "";
                    }
                }
                strCashAcc = "select FCashAccName from " +
                    pub.yssGetTableName("Tb_Para_CashAccount") +
                    " where FCheckState = 1 and FCashAccCode = " +
                    dbl.sqlString(CashAccCode);
                rsCashAcc = dbl.queryByPreparedStatement(strCashAcc);
                if (rsCashAcc.next()) {
                    CashAccName = rsCashAcc.getString("FCashAccName");
                }
                if (rsCashAcc != null) {
                    dbl.closeResultSetFinal(rsCashAcc);
                }
                String strCashAccCode = "";
                String strCashAccCode1 = "";
                String strCashAccName = "";
                if(invest.getAutoCharge(rs.getString("FPortCode"))&& rs.getString("CashAccCode").indexOf(",") != -1){
                	strSql = " select FCuryCode, FCashAccCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+
       	         			 " where FCheckState =1 and FCashAccCode in ("+operSql.sqlCodes(rs.getString("CashAccCode"))+")";
			       	rs1 = dbl.queryByPreparedStatement(strSql);
			       	while(rs1.next()){
			       		if(rs1.getString("FCuryCode").equalsIgnoreCase(rs.getString("FCuryCode"))){
			       			strCashAccCode1 = rs1.getString("FCashAccCode");
			       			strSql = " select FCashAccName from "+pub.yssGetTableName("Tb_Para_CashAccount")+
      	         			 		 " where FCashAccCode = " +  dbl.sqlString(strCashAccCode1);
			       			rs2 = dbl.queryByPreparedStatement(strSql);
			       			while(rs2.next()){
			       				strCashAccName += rs2.getString("FCashAccName")+",";
			       			}
			       			strCashAccCode += strCashAccCode1 + ",";
			       		}	
			       	}
			       	if(strCashAccCode.endsWith(",")){
			       		strCashAccCode = strCashAccCode.substring(0, strCashAccCode.length()-1);
			       	}
			       	if(strCashAccName.endsWith(",")){
			       		strCashAccName = strCashAccName.substring(0, strCashAccName.length()-1);
			       	}
			       	paid.setCashAccCode(strCashAccCode);
			       	paid.setCashAccName(strCashAccName);
                }else{
                	paid.setCashAccCode(rs.getString("CashAccCode"));
                	paid.setCashAccName(rs.getString("CashAccName"));
                }
                paid.setBaseCuryRate(BaseCuryRate);
                paid.setPortCuryRate(PortCuryRate);                
            }
        } catch (Exception e) {
        	//---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
            System.out.println(e.getMessage());
        	throw new YssException(e.getMessage());
        	//---edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rs1);
            dbl.closeResultSetFinal(rs2);
        }
		return paid;
    }
    
    
  //获取前一日的库存日期，add by yeshenghong 20120208 BUG3688
    private String sqlStoragEve(java.util.Date dDate) {
        String sResult = "";
        sResult = " FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1));
        return sResult;
    }


    public void saveIncome(ArrayList alIncome) throws YssException {
        int i = 0;
        String sOldNum = "";
        InvestPaid paid = new InvestPaid();
        InvestPayAdimin investpay = null;
        CashTransAdmin cashtrans = null;
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        InvestPayRecBean invest = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        ArrayList subTrans = null;
    	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    	Date logStartTime = null;//业务子项开始时间
    	String portCode = "";//组合代码
		//add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A 添加菜单代码
    	this.setFunName("incomepaid");
    	//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            cashtrans = new CashTransAdmin();
            cashtrans.setYssPub(pub);
            for (i = 0; i < alIncome.size(); i++) {
                paid = (InvestPaid) alIncome.get(i);
                
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    			logInfo = "";//日志信息
    			logStartTime = new Date();//开始时间
    			portCode = paid.getPortCode();//组合代码
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                
                investpay = new InvestPayAdimin();
                investpay.setYssPub(pub);
                invest = setInvestRecPayAttr(paid, "true");
                transfer = setTransferAttr(paid);
                transferset = setTransferSetAttr(paid);
                
              //add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
                //判断今天是否是两费自动支付日期
                isHasInvestPay(invest);                       
               
              //end by lidaolong 20110309 
                
                //===================================================================================================
                //fanghaoln 20090625 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
                if (this.isCheckData.equalsIgnoreCase("true")) { //如果前台传来true表示选中了审核状态统计之后的数据放到已审核里面
                    invest.checkStateId = 1;
                    transfer.checkStateId = 1;
                } else { //如果没有选中已审核的状态表示数据放到未审核里面
                    invest.checkStateId = 0;
                    transfer.checkStateId = 0;
                }
                //===========================================end=======================================================
                //--------将子资金调拨放入transferset中的arraylist ---sj 20080123-----//
                subTrans = new ArrayList();
                subTrans.add(transferset);
                transfer.setSubTrans(subTrans);
                //------------------------------------------------------------------
                investpay.addList(invest);
                //-------------------因需要在资金调拨记录中插入一个运营编号，故只能在循环中作插入动作。而且要在插入运营记录之前获取一个运营编号以便在
                //-------------------之后的资金调拨中删除之前的有相应运营编号的资金调拨。 sj edit 20080213---------------------------------//
                sOldNum +=
                    investpay.getFNum(invest.getTradeDate(), invest.getTradeDate(),
                                      invest.getTsftTypeCode(),
                                      "03IV,02IV", //之前没有考虑收入的情况,现在加入.
                                      invest.getFIVPayCatCode(),
                                      this.portCodes, invest.getAnalysisCode1(),
                                      invest.getAnalysisCode2(), invest.getAnalysisCode3(), //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified
                                      0, //+ ",";//fanghaoln  20090724 MS00580 QDV4赢时胜上海2009年7月24日05_B  当交易编号为空时加个，号后面SQL语句名报错
                                      invest.getCuryCode()); //add by fangjiang 2011.02.14 #2279
                //fanghaoln  20090724 MS00580 QDV4赢时胜上海2009年7月24日05_B 当交易编号为空时加个，号后面SQL语句名报错 加上判断为空时不加，号
                if (sOldNum.length() > 0) { //
                    sOldNum += ",";
                //===============================================================================================================
                }
                //为了在循环外删除多条记录 sj edit 20080213
                investpay.insert(invest.getTradeDate(), invest.getTradeDate(),
                                 "02" + "," + //之前没有考虑收入的情况,现在加入.
                                 "03" + "," + "98", //在这里将支付和调整的数据都删除。sj 20080806 bug 0000372
                                 "02IV" + "," + //之前没有考虑收入的情况,现在加入.
                                 "03IV" + "," + "9803IV",
                                 invest.getFIVPayCatCode(),
                                 invest.getPortCode(),
                                 invest.getAnalysisCode1(),
                                 invest.getAnalysisCode2(), invest.getAnalysisCode3(), //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码2、3处理 sj modified
                                 0,
                                 invest.getCuryCode(),""); //add by fangjiang 2011.02.14 #2279 modyfy by zhouwei 20120401 增加关联类型
                //------------------------------------------------------------------------------------------------------------------
                transfer.setFIPRNum(investpay.getInsertNum());
                if (paid.getBalMoney() != 0) {
                    investpay = new InvestPayAdimin();
                    investpay.setYssPub(pub);
                    invest = setInvestRecPayAttr(paid, "false");
                    //fanghaoln 20090714 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
                    if (this.isCheckData.equalsIgnoreCase("true")) { //如果前台传来true表示选中了审核状态统计之后的数据放到已审核里面
                        invest.checkStateId = 1;
                    } else { //如果没有选中已审核的状态表示数据放到未审核里面
                        invest.checkStateId = 0;
                    }
                    //===========================================end=======================================================
                    investpay.addList(invest);
                    investpay.insert(invest.getTradeDate(), invest.getTradeDate(),
                                     "02" + "," + //之前没有考虑收入的情况,现在加入.
                                     "03" + "," + "98", //调拨类型换成98,03是为了删除以前有可能存在的错误数据 sj 20080218
                                     invest.getSubTsfTypeCode(),
                                     invest.getFIVPayCatCode(),
                                     invest.getPortCode(),
                                     invest.getAnalysisCode1(),
                                     invest.getAnalysisCode2(), invest.getAnalysisCode3(), //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码2、3处理 sj modified
                                     0,
                                     invest.getCuryCode(),""); //add by fangjiang 2011.02.14 #2279 modyfy by zhouwei 20120401 增加关联类型
                }
                
            	//---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
            	logInfo += "运营费用代码:" + invest.getFIVPayCatCode() +
            		       "\r\n到账日期:" + YssFun.formatDate(transfer.getDtTransferDate(),"yyyy-MM-dd") +
            		       "\r\n现金账户代码:" + transferset.getSCashAccCode() +
            	           "\r\n" + ((transferset.getIInOut() == 1) ? "流入":"流出") + 
            	           "金额:" + transferset.getDMoney() +
            	           YssCons.YSS_LINESPLITMARK;
            	//---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
                
                cashtrans.addList(transfer); //放入含有子资金调拨arraylist的transfer
                //-----------------------------------------------------------------------------------------------
            
    			//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                //生成业务日志数据
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,20,operType,this.pub,false,
                		portCode,dDate,dDate,dDate,logInfo,
                		logStartTime,logSumCode, new Date());//20收益支付 ，operType是处理项目
        		}
                //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            }
            if (sOldNum.length() > 0) {
                sOldNum = sOldNum.substring(0, sOldNum.length() - 1);
            }
            cashtrans.delete("", "", sOldNum, "");
            cashtrans.insert();
            
            //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            conn.commit();//提交事务
            bTrans = false;
            conn.setAutoCommit(true);
            //---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        } catch (Exception e) {
    		//---add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    		try{
			    //生成业务日志数据
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,20,operType,this.pub,
                		true, portCode, dDate, dDate, dDate,
                		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
                		(logInfo + " \r\n 支付费用出错 \r\n " + e.getMessage())//处理日志信息 除去特殊符号
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
    			throw new YssException("系统保存费用支付时出现异常!" + "\n", e); //by 曹丞 2009.02.01 保存费用支付异常信息 MS00004 QDV4.1-2009.2.1_09A
    		}
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally{
		    //add by songjie 2012.10.29 关闭事务
        	dbl.endTransFinal(conn, bTrans);
        }
    }

    protected InvestPayRecBean setInvestRecPayAttr(InvestPaid paid,
        String equals) throws
        YssException {
        try {
            InvestPayRecBean investpay = new InvestPayRecBean();
            //investpay.setTradeDate(paid.getPDate()); //业务日期为支付日期。sj edit 20080806 暂无 0000372
            investpay.setTradeDate(paid.getmDate());//edit by yanghaiming 20100416 MS00997  QDV4建行2010年02月23日01_B 增加mdate为业务日期
            investpay.setFIVPayCatCode(paid.getIVPayCatCode());
            investpay.setPortCode(paid.getPortCode());
            investpay.setAnalysisCode1(paid.getAnalysisCode1());
            investpay.setAnalysisCode2(paid.getAnalysisCode2());
            //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
            investpay.setAnalysisCode3(paid.getAnalysisCode3());
            //--------------------------------------------------------------------------------//
            investpay.setTsftTypeCode(paid.getTsfTypeCode());
            investpay.setCuryCode(paid.getCuryCode());

          //add by lidaolong 20110314 #386 增加一个功能，能够自动支付管理费和托管费
            ResultSet rs = getNewRate(paid);
        	double BaseCuryRate = 0;//
            double PortCuryRate = 0;//
        	 if (rs.next()){
        		 //基础汇率 = 昨日库存应付基础货币总额  / 昨日库存应付原币总额，
        		 //组合汇率 = 昨日库存应付基础货币总额  / 昨日库存应付组合货币总额；
        		 BaseCuryRate=YssD.div(rs.getDouble("FYESTERDAYBBAL"),rs.getDouble("FYESTERDAYBAL"));
	        	 PortCuryRate=YssD.div(rs.getDouble("FYESTERDAYBBAL"),rs.getDouble("FYESTERDAYPBAL"));
        	 }
        	 //end by lidaolong 20110314
        	 
            if (equals.equalsIgnoreCase("true")) {
            	//----modify by wuweiqi 20110211 BUG #1036 两非支付后，查看运营应收应付原币发现为0 -------//
                //investpay.setMoney(paid.getInvestMoney());
                //investpay.setMoney(paid.getMoney());  //delete by jiangshichao BUG2855运营支付时，结转的计提金额不正确
                //-----BUG #1036  end by wuweiqi 20110211----------------------------------------------//
            	
            	//-----BUG #2855  运营支付时，结转的计提金额不正确 add by jiangshichao 2011.10.13 start -------------//
            	investpay.setMoney(paid.getInvestMoney());
            	//-----BUG #2855  运营支付时，结转的计提金额不正确 end   -------------//
                if (paid.getInvestMoney() == 0.0) {
//                    investpay.setBaseCuryMoney(paid.getBaseCuryMoney());
//                    investpay.setPortCuryMoney(paid.getPortCuryMoney());
                	investpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                			investpay.getMoney(),
                            paid.getBaseCuryRate()));
                	investpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                			investpay.getMoney(),
                            paid.getBaseCuryRate(), paid.getPortCuryRate(),
                            //yeshenghong #3176 20111202
                            paid.getCuryCode(), paid.getDDate(), paid.getPortCode()));
                } else {
                	//---add by songjie 2011.07.01 BUG 2161 QDV4建行2011年06月23日01_B  start---//
                	//若实付运营金额 不等于应付运营金额，则已实付运营金额为准生成运营应收应付数据
                	if(paid.getMoney() != paid.getInvestMoney()){
                        investpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                        		investpay.getMoney(),//BUG3602运营应收应付中费用的支出运营款项的‘基础货币金额’‘与组合货币金额’不一致  add by jiangshichao 2012.01.31
                                paid.getBaseCuryRate()));
                            investpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                            		investpay.getMoney(),
                                paid.getBaseCuryRate(), paid.getPortCuryRate(),
                                //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                                paid.getCuryCode(), paid.getDDate(), paid.getPortCode()));
                	}else{
                		//---add by songjie 2011.07.01 BUG 2161 QDV4建行2011年06月23日01_B  end---//
                		//edit by lidaolong 用昨日应收应付中的数据
                		//改成用公有的计算基础货币金额的方法来计算
                		investpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                			paid.getInvestMoney(),
                			paid.getBaseCuryRate()));
                		investpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                				paid.getInvestMoney(),
                				paid.getBaseCuryRate(), paid.getPortCuryRate(),
                				//linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                				paid.getCuryCode(), paid.getDDate(), paid.getPortCode()));
                		////end by lidaolong 20110314 
                	}//add by songjie 2011.07.01 BUG 2161 QDV4建行2011年06月23日01_B 添加右大括号
                }
        
                investpay.setSubTsfTypeCode(paid.getSubTsfTypeCode());
            } else if (equals.equalsIgnoreCase("false")) {
                investpay.setMoney(paid.getBalMoney());

                investpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(
                    investpay.getMoney(),
                    paid.getBaseCuryRate()));
                investpay.setPortCuryMoney(this.getSettingOper().calPortMoney(
                    investpay.getMoney(),
                    paid.getBaseCuryRate(), paid.getPortCuryRate(),
                    //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                    paid.getCuryCode(), paid.getDDate(), paid.getPortCode()));
                investpay.setTsftTypeCode("98"); //调拨类型转换成98 sj edit 20080218
                investpay.setSubTsfTypeCode("98" + paid.getSubTsfTypeCode());
            
            } 
       
            //add by lidaolong 20110316  #386 增加一个功能，能够自动支付管理费和托管费
             investpay.setBaseCuryRate(paid.getBaseCuryRate());
            investpay.setPortCuryRate(paid.getPortCuryRate());
          /*  investpay.setBaseCuryRate(BaseCuryRate);
            investpay.setPortCuryRate(PortCuryRate);*/
            //end by lidaolong 20110316    
           
            /*paid.getMoney金额均为0，所以基础货币金额和组合货币金额不能用paid.getMoney进行计算fazmm20070804
                 investpay.setBaseCuryMoney(YssD.mul(paid.getMoney(),
                    paid.getBaseCuryRate()));
                 investpay.setPortCuryMoney(YssD.div(YssD.mul(paid.getMoney(),
                  paid.getBaseCuryRate()), paid.getPortCuryRate()));*/
            investpay.setCheckState(1); //生成的应收应付直接进入已审核。sj edit 20080626.
            return investpay;

        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }

    protected TransferBean setTransferAttr(InvestPaid paid) {
        TransferBean transfer = new TransferBean();
        transfer.setDtTransferDate(paid.getPDate()); //修改为调拨日期。sj edit 20081017
        transfer.setDtTransDate(paid.getmDate());//edit by yanghaiming 20100416 MS00997  QDV4建行2010年02月23日01_B 增加mdate为业务日期
        transfer.setCheckStateId(1);
        //---- MS00331 QDV4中保2009年03月20日01_B 自动数据的标示调整为正确的数据 ---------
        transfer.setDataSource(1); //1为自动，0为手工
        //--------------------------------------------------------------------------
        transfer.setStrTsfTypeCode(paid.getTsfTypeCode());
        transfer.setStrSubTsfTypeCode(paid.getSubTsfTypeCode());
        return transfer;
    }

    protected TransferSetBean setTransferSetAttr(InvestPaid paid) throws YssException, SQLException {
        TransferSetBean transferset = new TransferSetBean();
        transferset.setSPortCode(paid.getPortCode());
        transferset.setSAnalysisCode1(paid.getAnalysisCode1());
        transferset.setCheckStateId(1);
        //---------QDV4中保2008-11-4日01_B ----若为02IV(YSS_ZJDBZLX_IV_Income)的，则为收入类，03IV的则为支出类。sj modified 20081218  ---//
        transferset.setSAnalysisCode2(paid.getAnalysisCode22()); //使用现金类的分析代码来填充值。
        //-----MS00237 QDV4中保2009年02月05日01_A 增加对分析代码3处理 sj modified ------------//
        transferset.setSAnalysisCode3(paid.getAnalysisCode23()); //使用现金类的分析代码来填充值。
        //--------------------------------------------------------------------------------//
        transferset.setIInOut(paid.getSubTsfTypeCode().equalsIgnoreCase(
            YssOperCons.YSS_ZJDBZLX_IV_Income) ? 1 : -1); //20070806
        //-------------------------------------------------------------------------------------------------------------------------//
        transferset.setSCashAccCode(paid.getCashAccCode());
        transferset.setDMoney(paid.getMoney());
        
      //add by lidaolong 20110314 #386 增加一个功能，能够自动支付管理费和托管费
    	ResultSet rs = getNewRate(paid);
    	//基础汇率 = 昨日现金库存基础货币总额 / 昨日现金库存原币总额，
    	//组合汇率 = 昨日现金库存基础货币总额  /  昨日现金库存组合货币总额
    	 double dBaseRate = 1;//
         double dPortRate = 1;//
    	if (rs.next()){
    		//---add by songjie 2011.05.16 BUG 1887 QDV4海富通2011年05月06日01_B---//
    		if(rs.getDouble("FYESTERDAYBAL") != 0){
    		dBaseRate=YssD.div(rs.getDouble("FYESTERDAYBBAL"),rs.getDouble("FYESTERDAYBAL"));
    		dPortRate=YssD.div(rs.getDouble("FYESTERDAYBBAL"),rs.getDouble("FYESTERDAYPBAL"));
    		transferset.setDBaseRate(paid.getBaseCuryRate());
    		transferset.setDPortRate(paid.getPortCuryRate());
    		//---add by songjie 2011.05.16 BUG 1887 QDV4海富通2011年05月06日01_B---//
            //---delete by songjie 2011.05.16 BUG 1887 QDV4海富通2011年05月06日01_B---//
    		//    		if(rs.getDouble("FYesAccBal") !=0){
//    		dBaseRate=YssD.div(rs.getDouble("FYesAccBBal"),rs.getDouble("FYesAccBal"));
//    		dPortRate=YssD.div(rs.getDouble("FYesAccBBal"),rs.getDouble("FYesAccPBal"));
//    		transferset.setDBaseRate(dBaseRate);
//    		transferset.setDPortRate(dPortRate);	
    		//---delete by songjie 2011.05.16 BUG 1887 QDV4海富通2011年05月06日01_B---//    		
    	 }else{
    			transferset.setDBaseRate(paid.getBaseCuryRate());
        		transferset.setDPortRate(paid.getPortCuryRate());
    	 }
        dbl.closeResultSetFinal(rs);
    	}else{
			transferset.setDBaseRate(paid.getBaseCuryRate());
    		transferset.setDPortRate(paid.getPortCuryRate());
	 }
    	//end by lidaolong 20110314
        return transferset;
    }
    
    /**
     * 
     * 判断今天选中的组合是否有两费自动支付，如果有，那么给出提示
     * //add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
     * @param paid
     * @return
     * @throws YssException 
     */
    private boolean isHasInvestPay(InvestPayRecBean investPay) throws YssException{
    	boolean isInvestPay = false;
    	ResultSet rs = null;
    	OperInvestPay operPay=null;
    	
    	operPay = new OperInvestPay();
    	operPay.setYssPub(pub);
    	operPay.initOperManageInfo(dDate, investPay.getPortCode());
    	
    	String strSql ="select * from "+pub.yssGetTableName("Tb_Para_InvestPay")
    					+" where FAutoPay = '1'and FCHECKSTATE = 1" +
    							" and FPortCode ="+dbl.sqlString(investPay.getPortCode())
    							+" and FIVPayCatCode =" + dbl.sqlString(investPay.getFIVPayCatCode());

		try {
			rs = dbl.openResultSet(strSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE);
			if (rs.next()) {
				//if(operPay.isAutoPay(rs.getString("FHolidaysCode"), rs.getInt("FAutoPayDay"))){
				  throw new YssException("您将支付的费用已设置自动支付功能，请勿重复支付！");
			//	}
			}
		} catch (SQLException e) {
			throw new YssException(e.getMessage());
		}finally{
		dbl.closeResultSetFinal(rs);
		}
    	return isInvestPay;
    }
    /**
     * 修改产生的运营应收应付数据和资金调拨
     * add by lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
     * @throws YssException 
     */
    private ResultSet getNewRate(InvestPaid investPay) throws YssException{
   
    	ResultSet rs = null;
      
    	String strSql = //昨日的运营应收应付库存（用来计算基础汇率和组合汇率）
    	 "select SI.*,SC.*  from ( "+
			  "select y.FBal as FYesterdayBal,y.FPortCuryBal as FYesterdayPBal,y.FBaseCuryBal as FYesterdayBBal" +
			         	",y.FPortCode,y.FIVPayCatCode "+
			         " from " + pub.yssGetTableName("Tb_Stock_InvestPayRec") + " y"+
			      " where FTsfTypeCode ='07' and FSubTsfTypeCode ='07IV'and FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1))+
			      		" and y.Fportcode="+dbl.sqlString(investPay.getPortCode())+" and y.Fivpaycatcode="+dbl.sqlString(investPay.getIVPayCatCode())
			      +" group by y.FPortCode,y.FIVPayCatCode,y.FBal,y.FPortCuryBal,y.FBaseCuryBal) SI"+
			//昨日的现金库存（用来计算资金调拨的基础汇和组合汇率）
		" left join (" +
			"select FAccBalance as FYesAccBal,FPortCuryBal as FYesAccPBal,FBaseCuryBal as FYesAccBBal,FPortCode,FCashAccCode"+
	" from  "+ pub.yssGetTableName("Tb_Stock_Cash") +
	" where FStorageDate ="+ dbl.sqlDate(YssFun.addDay(dDate, -1)) +" and FCuryCode ="+dbl.sqlString(investPay.getCuryCode())+") SC" +
			" on (SC.FPortCode = SI.FPortCode )";
    	
    	try {
			rs = dbl.openResultSet(strSql,
					ResultSet.TYPE_SCROLL_INSENSITIVE);
			
		} catch (SQLException e) {
			throw new YssException(e.getMessage());
		}
    	return rs;
    }
    
    private String sPortCode = "";
    
    //20130216 added by liubo.Story #3414
    //支付两费时，生成划款手续费
    //================================
    public int calcCommission(ArrayList alIncome) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;
    	
    	InvestPaid paid = null;
    	
    	int iReturn = 0;
    	
    	try
    	{
	    	for (int i = 0; i < alIncome.size(); i++) 
	    	{
	            paid = (InvestPaid) alIncome.get(i);

	        	this.sPortCode = paid.getPortCode();
	        	
	        	//获取当前运营收支品种，是否启用了自动生成划款手续费
				strSql = "select * from " + pub.yssGetTableName("tb_para_investpay") + " a " +
						 " where a.fstartdate in (select max(FStartDate) from " + pub.yssGetTableName("tb_para_investpay") + 
						 " where FIvPayCatCode = " + dbl.sqlString(paid.getIVPayCatCode()) + " and FStartDate <= " + dbl.sqlDate(this.dDate) + " and FPortCode = " + dbl.sqlString(this.sPortCode) + ") " +
						 " and a.fivpaycatcode = " + dbl.sqlString(paid.getIVPayCatCode()) + 
						 " and a.FPortCode = " + dbl.sqlString(this.sPortCode);
				
				rs = dbl.queryByPreparedStatement(strSql);
				
				while(rs.next())
				{
					if (rs.getInt("FCommission") == 1)
					{
						 double dFeeMoney = doOpertionExchangeStock(paid.getMoney());
						//生成划款手续费的资金调拨数据
					     String cashFNum = createCashTransfer(rs.getString("FIVPayCatCode"),dFeeMoney);
					    //生成划款手续费的综合业务数据
					     createDataIntegrated(cashFNum);
					     
					     iReturn++;
					}
				}
					
				dbl.closeResultSetFinal(rs);
	    	}
    	}
    	catch(Exception ye)
    	{
    		throw new YssException("生成划款手续费出现错误：" + "\n", ye);
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	return iReturn;
    }
    

	/**生成划款手续费的资金调拨
     * @throws YssException
     * @author 刘博 ,20130207 , STORY 3414
     */
    private String createCashTransfer(String sIVPayCatCode,double feeMoney) throws YssException {
        ResultSet rs = null;
        String fNum = "";
        CashTransAdmin cashtransAdmin = null;
    	try {

			cashtransAdmin = new CashTransAdmin(); // 生成资金调拨控制类
			cashtransAdmin.setYssPub(pub);
			cashtransAdmin.delete("", this.dDate, this.dDate, "03", "0303", "", "", "", "", "", "FeeSMoney", "", 0, "", "", 0, "", "", "", "");
    		
    		String arrCash = getArrCash(sIVPayCatCode,feeMoney);
    		//资金调拨类
    		TransferBean cashBean = new TransferBean();
    		cashBean.setYssPub(pub);
    		cashBean.parseRowStr(arrCash);
    		pub.setbSysCheckState(false);//状态已审核
    		cashBean.setFNumType("FeeSMoney");//编号类型
    		cashBean.addSetting();
    		
            fNum = cashBean.getStrNum();
        } catch (Exception ex) {
            throw new YssException("生成资金调拨出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
    		pub.setbSysCheckState(true);//将状态还原为未审核
        }
        return fNum;
    }
    

    /**设置生成供资金调拨的POJO类解析的字符串
     * @throws YssException
     * @author 刘博 ,20130207 , STORY 3414
     */
    private String getArrCash(String sIVPayCatCode,double money) throws YssException {
    	ResultSet rs = null;
    	String pramString = "";
    	String srcCashAccCode = " ";//现金账户代码(不考虑有多个账户情况)
    	//String srcCashAccName = " ";//现金账户名称(泰达那里只有一个组合一个现金账户)
    	double FBaseCuryRate = 1;//基础汇率
    	double FPortCuryRate = 1;//组合汇率
    	try {
			String strSql = "select b.FCuryCode,a.* from " + pub.yssGetTableName("tb_para_investpay") + " a " +
			" left join " + pub.yssGetTableName("tb_para_cashaccount") + " b on a.fcashacccode = b.fcashacccode " +
			" where fivpaycatcode = " + dbl.sqlString(sIVPayCatCode) +
			" and a.FStartDate in (select Max(FStartDate) from " + pub.yssGetTableName("tb_para_investpay") + 
			" where FStartDate <= " + dbl.sqlDate(this.dDate) + " and FPortCode = " + dbl.sqlString(this.sPortCode) + " and fivpaycatcode = " + dbl.sqlString(sIVPayCatCode) + ")";
			rs = dbl.queryByPreparedStatement(strSql);
			if (rs.next()) {
				srcCashAccCode = rs.getString("FCashAccCode");//现金账户
				/**shashijie 2011-11-15 BUG 3144 */
				//公共获取汇率类
				FBaseCuryRate = this.getSettingOper().getCuryRate( //基础汇率
						this.dDate, 
						rs.getString("FCuryCode"), 
						this.sPortCode, 
						YssOperCons.YSS_RATE_BASE); 
				FPortCuryRate = this.getSettingOper().getCuryRate( //组合汇率
						this.dDate, 
						"", 
						this.sPortCode, 
						YssOperCons.YSS_RATE_PORT);
				/**end*/
    		}
			
			pramString = " \t03\t0303\t \t \t"+YssFun.formatDate(dDate)+"\t"+YssFun.formatDate(dDate)+"\t00:00:00" +//主表
					"\t" + YssFun.formatDate(dDate)+"\t"+YssFun.formatDate(dDate) +
					"\t \t \t0\t1\t \t \t \t \t" + //主表
					"\r\t[null]\r\t" +
					" \t \t-1\t"+sPortCode+"\tnull\tnull\tnull\t"+//子表
					srcCashAccCode+"\t"+money+"\t"+FBaseCuryRate+"\t"+FPortCuryRate+"\t1\t \t \t \t";//子表
			
    	} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("获取TA交易数据中的基准金额出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return pramString;
	}
    

	/** 根据支付的两费金额，生成划款手续费
     * @author liubo ,20130204,Story #3414
     * @modified 
     */
    private double doOpertionExchangeStock(double money) throws YssException {
    	ResultSet rs = null;
    	double resulte = 0;//赎回款费用
    	try {
    		String strSql = getStrSql();//获取TA费用连接设置等关联
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		if (rs.next()) {
    			//验证数据完整性
    			//if (rs.getString("FFormulaCode")==null || rs.getString("FRoundCode")==null) {//modify huangqirong 2013-02-27 bug #7146
				if (rs.getString("FFormulaCode")==null ) { //modify huangqirong 2013-02-27 bug #7146
    				throw new YssException("统计成功。未生成划款手续费。请到【销售业务管理】->【TA费用链接设置】中设置TA赎回款手续费！");
				}
    			
    			//公共计算费用类
    			BaseOperDeal base = new BaseOperDeal();
    			base.setYssPub(pub);
    			//计算赎回款费用数据,传参:比率代码 ,舍入代码,金额,日期范围
    			resulte = base.calMoneyByPerExp(rs.getString("FFormulaCode"), 
    					rs.getString("FRoundCode"), money, dDate);
			}
    		else
    		{
    			throw new YssException("统计成功。未生成划款手续费。请到【销售业务管理】->【TA费用链接设置】中设置TA赎回款手续费！");
    		}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return resulte;
	}


	//20130204 added by liubo.Story #3414
	private String getStrSql() {
		String strSql = " select b.FPortCode, b.FFeeCode1,"+
			" c.FRoundCode, d.FFormulaCode From "+pub.yssGetTableName("Tb_TA_FeeLink")+" b "+//TA费用连接
			" Left Join "+pub.yssGetTableName("Tb_Para_Fee")+" c on b.FFeeCode1 = c.FFeeCode "+//费用设置
			" Left Join "+pub.yssGetTableName("tb_para_performula")+" d on c.FPerExpCode = d.FFormulaCode"+//比率设置
			" where b.FStartDate <= "+dbl.sqlDate(dDate)+" and b.FCheckState = 1 and b.FFeeType = 1 "+
			" and b.FPortCode = "+dbl.sqlString(sPortCode)+" and b.FSellTypeCode = '02' ";
			//operSql.sqlCodes(sPortCode)
		return strSql;
	}
	
    

    /**产生划款手续费的综合业务数据
     * @throws YssException
     * @author 刘博 ,20130207 , STORY 3414
     */
	private void createDataIntegrated(String cashFNum) throws YssException{
		//综合业务自动编号
		String sNewNum = "E" + YssFun.formatDate(dDate, "yyyyMMdd")+
				dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Integrated"),
                dbl.sqlRight("FNUM", 6),
                "000001",
                " where FExchangeDate=" + dbl.sqlDate(dDate) +
                " or FExchangeDate=" + dbl.sqlDate("9998-12-31") +
                " or FNum like 'E" + YssFun.formatDate(dDate, "yyyyMMdd") + "%'");
		
		saveRelaDatas("Cash", cashFNum, sNewNum);
		
	}

	/**生成划款手续费的综合业务数据
	 * @param sNumType 编号类型
	 * @param FRelaNum 关联编号(这里是资金调拨编号)
	 * @param sNewNum 交易子编号
     * @throws YssException
     * @author 刘博 ,20130207 , STORY 3414
	 * @modified 
	 */
	private void saveRelaDatas(String sNumType, String FRelaNum,
            String sNewNum) throws YssException {
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pst = null;
		YssPreparedStatement pst = null;
        //=============end====================
        String strSql = "insert into " +
            pub.yssGetTableName("Tb_Data_Integrated") +
            " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
            " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
            " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
            " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,FCheckState,FCreator," +
            " FCreateTime,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode) " +
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        integrateAdmin.setYssPub(pub);
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
			//modified by liubo.Story #2145
			//==============================
//            pst = dbl.getPreparedStatement(strSql);
        	pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
            //交易子编号
        	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//            String sSubNum = sNewNum +
//                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Integrated"),
//                                       dbl.sqlRight("FSubNUM", 5),
//                                       "00000",
//                                       " where FNum =" + dbl.sqlString(sNewNum));
        	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        	
        	strSql = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") + 
        			 " where FPortCode = " + dbl.sqlString(this.sPortCode) +
        			 " and FTradeTypeCode = '34'" +
        			 " and FExchangeDate = " + dbl.sqlDate(this.dDate) +
        			 " and FOperDate = " + dbl.sqlDate(this.dDate) +
        			 " and FTsfTypeCode = '03' and FSubTsfTypeCode = '0303'";
        	dbl.executeSql(strSql);
        	
            pst.setString(1, sNewNum);//取前面的
            //edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
            pst.setString(2, integrateAdmin.getKeyNum());
            pst.setInt(3, 0);//方向
            pst.setString(4, " ");//证券代码
            pst.setDate(5, YssFun.toSqlDate(dDate));//兑换日期(操作日期)
            pst.setDate(6, YssFun.toSqlDate(dDate));//业务日期
            pst.setString(7, "34");//设置业务类型为 34挂款手续费
            pst.setString(8, FRelaNum);//关联编号(这里是资金调拨编号)
            pst.setString(9, sNumType);//编号类型
            pst.setString(10, sPortCode);//组合
            pst.setString(11, " ");
            pst.setString(12, " ");
            pst.setString(13, " ");
            pst.setDouble(14, 0.0);
            pst.setDouble(15, 0.0);
            pst.setDouble(16, 0.0);
            pst.setDouble(17, 0.0);
            pst.setDouble(18, 0.0);
            pst.setDouble(19, 0.0);
            pst.setDouble(20, 0.0);
            pst.setDouble(21, 0.0);
            pst.setDouble(22, 0.0);
            pst.setDouble(23, 0.0);
            pst.setDouble(24, 0.0);
            pst.setDouble(25, 0.0);
            pst.setString(26, " ");
            pst.setString(27, " ");//描述
            pst.setInt(28, 1);//审核状态
            pst.setString(29, pub.getUserCode());//创建人
            pst.setString(30, YssFun.formatDatetime(new Date()));//创建时间
            pst.setString(31, "03");
            pst.setString(32, "0303");

            pst.setString(33, " ");
            pst.executeUpdate();
        } catch (Exception e) {
            throw new YssException("保存综合业务表出错", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
	}
}
