package com.yss.main.operdeal.opermanage.securitymanage;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.main.operdata.*;
import com.yss.pojo.cache.YssCost;
import com.yss.main.dao.ICostCalculate;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.manager.SecRecPayStorageAdmin;
import com.yss.main.storagemanage.SecRecPayBalBean;

/**
 *
 * <p>Title: 银行间债券业务处理</p>
 *
 * <p>Description: MS00022    国内债券业务    QDV4.1赢时胜（上海）2009年4月20日22_A</p></p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable 2009.07.03 蒋锦 添加
 * @version 1.0
 */
public class InterBankBondManage extends BaseBean{

    HashMap hmFee = null;

    private String portCode = "";
    private java.util.Date bargainDate;
    private boolean analy1;
    private boolean analy2;
    private boolean analy3;
    private String cashTradeNum = "";

    private HashMap hmSettleFee = null; //结算服务费当日交收的组合
    private HashMap hmTradeFee = null;  //交易手续费当日交收的组合
    private HashMap hmBankFee = null;   //银行手续费当日交收的组合
    private HashMap hmOrderPorts = null; //按成交比号排序
    //MS00656  QDV4赢时胜(上海)2009年8月24日01_A
    //2009.09.07 蒋锦
    private YssCost allCost = null;//摊余成本的卖出成本

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



    public InterBankBondManage(YssPub pub) {
        setYssPub(pub);
    }

    public void interBankBondDeal(ArrayList alCashTrans,
                                  ArrayList alRecPay,
                                  ArrayList alIntegrated,
                                  ArrayList alCashRecPay) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            hmFee = pubPara.getInterBankBondFee();
            hmSettleFee = pubPara.getIntBakSettleFee();
            hmTradeFee = pubPara.getIntBakTradeFee();
            hmBankFee = pubPara.getIntBakBankFee();
            hmOrderPorts = pubPara.getInterBankOrderPorts();

            //MS00656  QDV4赢时胜(上海)2009年8月24日01_A
            //2009.09.07 蒋锦 修改 SQL 语句 查询昨日库存数量
            strSql = "SELECT a.*, b.FAMOUNTIND, b.FCASHIND, c.FTradeCury, c.FCatCode, d.FSecurityCode, d.FFaceValue, e.FStorageAmount FROM " + pub.yssGetTableName("TB_data_IntBakBond") + " a" +
                " LEFT JOIN Tb_Base_Tradetype b ON a.FTradeTypeCode = b.FTradeTypeCode" +
                " LEFT JOIN " + pub.yssGetTableName("TB_Para_Security") + " c" +
                " ON a.FSecurityCode = c.FSecurityCode" +
                " LEFT JOIN " + pub.yssGetTableName("Tb_Para_Fixinterest") +
                " d ON c.FSecuritycode = d.FSecurityCode " +
                " LEFT JOIN (SELECT * FROM " + pub.yssGetTableName("Tb_Stock_Security") +
                " WHERE FCheckState = 1 AND " + operSql.sqlStoragEve(bargainDate) +
                ") e ON a.FSecurityCode = e.FSecurityCode" +
                " AND a.FPortCode = e.FPortCode AND a.FInvestType = e.FInvestType AND a.finvmgrcode = e.FAnalysisCode1" +
                " WHERE a.FCheckState = 1" +
                " AND a.FPortCode IN (" + operSql.sqlCodes(portCode) + ")" +
                " AND (a.FBARGAINDATE = " + dbl.sqlDate(bargainDate) + " OR" +
                " a.FSettleDate = " + dbl.sqlDate(bargainDate) + ")" +
                " ORDER BY a.FTradeTypeCode";
            rs = dbl.queryByPreparedStatement(strSql);
            while(rs.next()){
                //如果业务日期是今天，产生除应收应付清算款之外的所有数据
                if(YssFun.dateDiff(rs.getDate("FBARGAINDATE"), bargainDate) == 0){
                    getRecPayData(alRecPay, alCashRecPay, rs);
                    getCashTransferData(alCashTrans, rs);
                    //-----------------begin zhouxiang MS01132    结算服务费、交易手续费及银行费用需分开不同类型进行费用的统计（国内，9月发布）    
                    //getCashTransFee(alCashTrans,rs);
                    //-----------------end------------------20100909---------------------------------------------------------------------
                    getIntegRateData(alIntegrated, rs);
                }
                //产生应收应付清算款
                getSettleRecPayData(alCashRecPay, rs);
            }
        } catch (Exception ex) {
            throw new YssException("处理银行间债券业务出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

   
	/**
     * 产生应收应付清算款
     * @param alRecPay ArrayList
     * @param rs ResultSet
     * @throws YssException
     */
    private void getSettleRecPayData(ArrayList alCashRecPay, ResultSet rs) throws YssException{
    	CashPecPayBean cashRec=null;
        java.util.Date dTradeDate = null;//交易日期
        java.util.Date dSettleDate = null;//结算日期
        java.util.Date dTransDate = null;//调拨日期
        int iCashInd = 0;
        try {
            dTradeDate = rs.getDate("FBARGAINDATE");
            dSettleDate = rs.getDate("FSettleDate");
            iCashInd = rs.getInt("FCASHIND");
            if(YssFun.dateDiff(dTradeDate, dSettleDate) == 0){
                //如果交易日期和结算日期是同一天，资金调拨当天划出，不产生应收应付清算款
                return;
            }
            cashRec = new CashPecPayBean();
            //如果今天是交易日期，产生应收或者应付清算款，否则说明结算日期是今天，产生费用或收入
            if(YssFun.dateDiff(bargainDate, dTradeDate) == 0){
                //资金方向流出，产生应付,否则产生应收
                if(iCashInd < 0){
                    cashRec.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
                    cashRec.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_TD_PayUnAcc);
                } else{
                    cashRec.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                    cashRec.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_TD_RecUnAcc);
                }
                //调拨日期为业务日期
                dTransDate = dTradeDate;
            } else {
                //资金方向流出，产生费用,否则产生收入
                if(iCashInd < 0){
                    cashRec.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);
                    cashRec.setSubTsfTypeCode("03TD");
                } else{
                    cashRec.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
                    cashRec.setSubTsfTypeCode("02TD");
                }
                //调拨日期为结算日期
                dTransDate = dSettleDate;
            }
            cashRec.setTradeDate(dTransDate);
            cashRec.checkStateId = 1;
            cashRec.setRelaNum(rs.getString("FNum"));
            cashRec.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_INTERBANKBOND);
            cashRec.setPortCode(rs.getString("FPortCode"));
            if (this.analy1) {
                cashRec.setInvestManagerCode(rs.getString("FInvMgrCode"));
            } else {
                cashRec.setInvestManagerCode(" ");
            }
            cashRec.setBrokerCode(" ");

            cashRec.setCashAccCode(rs.getString("FCashAccCode"));
            cashRec.setCuryCode(rs.getString("FTradeCury"));

            cashRec.setInOutType(1);

            cashRec.setMoney(rs.getDouble("FSettleMoney"));
            cashRec.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
            cashRec.setPortCuryRate(rs.getDouble("FPortCuryRate"));

            cashRec.setBaseCuryMoney(this.getSettingOper().calBaseMoney(cashRec.getMoney(),
                cashRec.getBaseCuryRate(), 2));

            cashRec.setPortCuryMoney(this.getSettingOper().calPortMoney(cashRec.getMoney(),
                cashRec.getBaseCuryRate(), cashRec.getPortCuryRate(),
                rs.getString("FTradeCury"), dTransDate, cashRec.getPortCode(), 2));

            alCashRecPay.add(cashRec);

        } catch (Exception ex) {
            throw new YssException("生成应收应付清算款出错！", ex);
        }
    }

    private void getRecPayData(ArrayList alRecPay, ArrayList alCashRecPay, ResultSet rs) throws YssException{
        SecPecPayBean recPay = null;
        CashPecPayBean cashRec = null;
        String sPortCode = "";
        double dbFee = 0;
        double dbCost = 0;
        YssCost cost = null;
        try {
            sPortCode = rs.getString("FPortCode");
            //---------------应收债券利息----------------//
            recPay = new SecPecPayBean();
            recPay.setInvestType(rs.getString("FInvestType"));
            recPay.setTransDate(rs.getDate("FBargainDate"));
            recPay.setCheckState(1);
            recPay.setAttrClsCode(rs.getString("FAttrClsCode"));
            recPay.setRelaNum(rs.getString("FNum"));
            recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_INTERBANKBOND);
            recPay.setStrPortCode(sPortCode);
            if (this.analy1) {
                recPay.setInvMgrCode(rs.getString("FInvMgrCode"));
            } else {
                recPay.setInvMgrCode(" ");
            }
            recPay.setBrokerCode(" ");

            recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                recPay.setStrCuryCode(rs.getString("FTradeCury"));
            if(rs.getInt("FAMOUNTIND") > 0){
                recPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);
                recPay.setStrSubTsfTypeCode("06FI_B");
            } else {
                recPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
                recPay.setStrSubTsfTypeCode("02FI_B");
            }
            recPay.setInOutType(1);
            recPay.setMoney(rs.getDouble("FBondIns"));
            recPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
            recPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));

            recPay.setMMoney(recPay.getMoney());
            recPay.setVMoney(recPay.getMoney());

            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                recPay.getBaseCuryRate(), 2));
            recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
            recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());

            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                recPay.getBaseCuryRate(), recPay.getPortCuryRate(),
                rs.getString("FTradeCury"), bargainDate, recPay.getStrPortCode(), 2));
            recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
            recPay.setVPortCuryMoney(recPay.getPortCuryMoney());

            recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
            recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
            recPay.setMoneyF(recPay.getMoney());
            alRecPay.add(recPay);

            //MS00656  QDV4赢时胜(上海)2009年8月24日01_A
            //2009.09.07 蒋锦 判断如果是持有到期或者可供出售则计算溢折价面值成本和买卖变动成本
            if(rs.getString("FInvestType").equalsIgnoreCase(YssOperCons.YSS_INVESTTYPE_CYDQ) ||
                rs.getString("FInvestType").equalsIgnoreCase(YssOperCons.YSS_INVESTTYPE_KGCS)){

                if(hmFee.get(sPortCode) != null){
                    dbCost = YssD.add(rs.getDouble("FTradeMoney"), rs.getDouble("FBankFee"));
                } else {
                    dbCost = rs.getDouble("FTradeMoney");
                }

                //------------------溢折价面值成本------------------//
                recPay = new SecPecPayBean();
                recPay.setInvestType(rs.getString("FInvestType"));
                recPay.setTransDate(rs.getDate("FBargainDate"));
                recPay.setCheckState(1);
                recPay.setAttrClsCode(rs.getString("FAttrClsCode"));
                recPay.setRelaNum(rs.getString("FNum"));
                recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_INTERBANKBOND);
                recPay.setStrPortCode(sPortCode);
                if (this.analy1) {
                    recPay.setInvMgrCode(rs.getString("FInvMgrCode"));
                } else {
                    recPay.setInvMgrCode(" ");
                }
                recPay.setBrokerCode(" ");

                recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                recPay.setStrCuryCode(rs.getString("FTradeCury"));
                recPay.setStrTsfTypeCode("05");
                recPay.setStrSubTsfTypeCode("05FIFA");

                recPay.setInOutType(1);
                recPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                recPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));

                if(rs.getDouble("FAMOUNTIND") > 0){
                    recPay.setMoney(YssD.mul(rs.getDouble("FFaceValue"), rs.getDouble("FTradeAmount")));
                    recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                        recPay.getBaseCuryRate(), 2));
                    recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                        recPay.getBaseCuryRate(), recPay.getPortCuryRate(),
                        rs.getString("FTradeCury"), bargainDate, recPay.getStrPortCode(), 2));
                } else {
                    cost = getSellPADCost("05", "05FIFA", rs);
                    recPay.setMoney(YssD.mul(cost.getCost(), -1));
                    recPay.setBaseCuryMoney(YssD.mul(cost.getBaseCost(), -1));
                    recPay.setPortCuryMoney(YssD.mul(cost.getPortCost(), -1));

                    allCost = new YssCost();
                    allCost.setCost(cost.getCost());
                    allCost.setBaseCost(cost.getBaseCost());
                    allCost.setPortCost(cost.getPortCost());
                }
                recPay.setMMoney(recPay.getMoney());
                recPay.setVMoney(recPay.getMoney());


                recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
                recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());


                recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
                recPay.setVPortCuryMoney(recPay.getPortCuryMoney());

                recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
                recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
                recPay.setMoneyF(recPay.getMoney());
                alRecPay.add(recPay);

                //------------------溢折价买入卖出变动------------------//
                recPay = new SecPecPayBean();
                recPay.setInvestType(rs.getString("FInvestType"));
                recPay.setTransDate(rs.getDate("FBargainDate"));
                recPay.setCheckState(1);
                recPay.setAttrClsCode(rs.getString("FAttrClsCode"));
                recPay.setRelaNum(rs.getString("FNum"));
                recPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_INTERBANKBOND);
                recPay.setStrPortCode(sPortCode);
                if (this.analy1) {
                    recPay.setInvMgrCode(rs.getString("FInvMgrCode"));
                } else {
                    recPay.setInvMgrCode(" ");
                }
                recPay.setBrokerCode(" ");

                recPay.setStrSecurityCode(rs.getString("FSecurityCode"));
                recPay.setStrCuryCode(rs.getString("FTradeCury"));
                recPay.setStrTsfTypeCode("05");
                recPay.setStrSubTsfTypeCode("05FIDI");

                recPay.setInOutType(1);
                recPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                recPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));

                if(rs.getDouble("FAMOUNTIND") > 0){
                    recPay.setMoney(
                        YssD.sub(
                            dbCost,
                            YssD.mul(
                                rs.getDouble("FFaceValue"),
                                rs.getDouble("FTradeAmount"))));
                    recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                        recPay.getBaseCuryRate(), 2));
                    recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                        recPay.getBaseCuryRate(), recPay.getPortCuryRate(),
                        rs.getString("FTradeCury"), bargainDate, recPay.getStrPortCode(), 2));
                } else{
                    cost = getSellPADCost("05", "05FIDI", rs);
                    recPay.setMoney(YssD.mul(cost.getCost(), -1));
                    recPay.setBaseCuryMoney(YssD.mul(cost.getBaseCost(), -1));
                    recPay.setPortCuryMoney(YssD.mul(cost.getPortCost(), -1));

                    allCost.setCost(YssD.add(allCost.getCost(), cost.getCost()));
                    allCost.setBaseCost(YssD.add(allCost.getBaseCost(), cost.getBaseCost()));
                    allCost.setPortCost(YssD.add(allCost.getPortCost(), cost.getPortCost()));
                }

                recPay.setMMoney(recPay.getMoney());
                recPay.setVMoney(recPay.getMoney());

                recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
                recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());

                recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
                recPay.setVPortCuryMoney(recPay.getPortCuryMoney());

                recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
                recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
                recPay.setMoneyF(recPay.getMoney());
                alRecPay.add(recPay);
            }

            //------------------应付费用---------------//
            //判断结算服务费、交易手续费、银行手续费是否当日交收，如果是当日交收则不需要产生现金应收应付了
            //-----------------begin zhouxiang MS01132    结算服务费、交易手续费及银行费用需分开不同类型进行费用的统计（国内，9月发布）    
            if(hmSettleFee.get(sPortCode) == null){
            	double settle=rs.getDouble("FSettleFee");
            	if(settle>0)
            		getIntFeeCashPecPay(alCashRecPay,rs,settle,YssOperCons.YSS_ZJDBZLX_FE_PAYSETTLEFEE);
            }
            if(hmTradeFee.get(sPortCode) == null){
                double tradeFee=rs.getDouble("FFEE");
            	if(tradeFee>0)
            		getIntFeeCashPecPay(alCashRecPay,rs,tradeFee,YssOperCons.YSS_ZJDBZLX_FE_PAYTRADEFEE);
            }
            if (hmBankFee.get(sPortCode) == null) {
                double bankFee=rs.getDouble("FBankFee");
                if(bankFee>0){
                	getIntFeeCashPecPay(alCashRecPay,rs,bankFee,YssOperCons.YSS_ZJDBZLX_FE_PAYBANKFEE);
                }		
            }
            //--------------end 20100831 zhouxiang MS01132    结算服务费、交易手续费及银行费用需分开不同类型进行费用的统计（国内，9月发布）    
        } catch (Exception ex) {
            throw new YssException("获取应收应付数据出错！", ex);
        }
    }

   

	/**
     * 获取资金调拨
     * @param alCashTrans ArrayList
     * @param rs ResultSet
     * @throws YssException
     */
    private void getCashTransferData(ArrayList alCashTrans,
                                     ResultSet rs) throws YssException {
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        ArrayList alSubTrans = new ArrayList();
        try {
            transfer = new TransferBean();
            transfer.setCheckStateId(1);
            transfer.setDtTransDate(rs.getDate("FBARGAINDATE"));
            transfer.setDtTransferDate(rs.getDate("FSettleDate"));
            transfer.setStrPortCode(rs.getString("FPortCode"));
            transfer.setStrSecurityCode(rs.getString("FSecurityCode"));
            transfer.setStrSubTsfTypeCode("05" + rs.getString("FCatCode"));
            transfer.setStrTsfTypeCode("05");
            transfer.setFRelaNum(rs.getString("FNum"));
            transfer.setFNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_INTERBANKBOND);
            cashTradeNum += (transfer.getFRelaNum() + ",");

            transferset = new TransferSetBean();
            transferset.setIInOut(rs.getInt("FCASHIND"));

            transferset.setDBaseRate(rs.getDouble("FBaseCuryRate"));
            transferset.setDPortRate(rs.getDouble("FPortCuryRate"));
            transferset.setDMoney(rs.getDouble("FSettleMoney"));

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

        } catch (Exception ex) {
            throw new YssException("生成资金调拨数据出错！", ex);
        }
    }

    /**
     * 产生证券变动数据
     * @param alIntegrate ArrayList
     * @param rs ResultSet
     * @throws YssException
     */
    private void getIntegRateData(ArrayList alIntegrated,
                                  ResultSet rs) throws YssException{
        SecIntegratedBean inteCost = null;
        double baseCuryRate = 0;
        double portCuryRate = 0;
        try {
            baseCuryRate = rs.getDouble("FBaseCuryRate");
            portCuryRate = rs.getDouble("FPortCuryRate");
            inteCost = new SecIntegratedBean();
            inteCost.setIInOutType(1);
            inteCost.setInvestType(rs.getString("FInvestType"));
            inteCost.setSSecurityCode(rs.getString("FSecurityCode"));
            inteCost.setSExchangeDate(YssFun.formatDate(rs.getDate("FBARGAINDATE"),//bug4465 by zhouwei 20120504 FSettleDate-->FBARGAINDATE
                "yyyy-MM-dd"));
            inteCost.setSOperDate(YssFun.formatDate(rs.getDate("FBARGAINDATE"),
                "yyyy-MM-dd"));
            inteCost.setSRelaNum(" ");
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


            inteCost.setDBaseCuryRate(baseCuryRate);
            inteCost.setDPortCuryRate(portCuryRate);
            inteCost.setDAmount(YssD.mul(rs.getDouble("FTradeAmount"), rs.getDouble("FAMOUNTIND")));
            //----MS00929  银行间债券交易成本的交易关联编号的插入综合业务表中，方便凭证取数----//
            //20100327 蒋锦 添加
            inteCost.setSRelaNum(rs.getString("FNum")); 
            inteCost.setSNumType("IBB");
            //---------------//

            if(rs.getInt("FAMOUNTIND") < 0){
                //MS00656  QDV4赢时胜(上海)2009年8月24日01_A
                //2009.09.07 蒋锦
                //如果是持有到期和可供出售卖出成本等于卖出面值成本+卖出溢折价成本
                if (rs.getString("FInvestType").equalsIgnoreCase(YssOperCons.YSS_INVESTTYPE_CYDQ) ||
                    rs.getString("FInvestType").equalsIgnoreCase(YssOperCons.YSS_INVESTTYPE_KGCS)) {
                    inteCost.setDCost(YssD.mul(allCost.getCost(), -1));
                    inteCost.setDBaseCost(YssD.mul(allCost.getBaseCost(), -1));
                    inteCost.setDPortCost(YssD.mul(allCost.getPortCost(), -1));
                }else{
                    ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
                        "avgcostcalculate");

                    costCal.initCostCalcutate(rs.getDate("FBARGAINDATE"),
                                              rs.getString("FPortCode"),
                                              rs.getString("FInvMgrCode"),
                                              "",
                                              rs.getString("FAttrClsCode"));
                    costCal.setYssPub(pub);                                    
                    //获取冲减的成本
                    YssCost cost = costCal.getCarryCost(rs.getString("FSecurityCode"),
                                                        rs.getDouble("FTradeAmount"),
                                                        rs.getString("FNum"),
                                                        rs.getDate("FSettleDate"),//add by guolongchao 20110815 STORY #1207 获取结算日期
                                                        "interbankbond",
                                                        YssOperCons.YSS_JYLX_Buy + "," + YssOperCons.YSS_JYLX_Sale + "," + YssOperCons.YSS_JYLX_YHJZQCX);
                    costCal.roundCost(cost, 2);
                    inteCost.setDCost(YssD.mul(cost.getCost(), rs.getDouble("FAMOUNTIND")));
                    inteCost.setDBaseCost(YssD.mul(cost.getBaseCost(), rs.getDouble("FAMOUNTIND")));
                    inteCost.setDPortCost(YssD.mul(cost.getPortCost(), rs.getDouble("FAMOUNTIND")));
                }
            } else {
                if(hmFee.get(inteCost.getSPortCode()) != null){
                    inteCost.setDCost(YssD.add(rs.getDouble("FTradeMoney"), rs.getDouble("FBankFee")));
                } else {
                    inteCost.setDCost(rs.getDouble("FTradeMoney"));
                }
                inteCost.setDBaseCost(this.getSettingOper().calBaseMoney(inteCost.getDCost(),
                    baseCuryRate, 2));
                inteCost.setDPortCost(this.getSettingOper().calPortMoney(inteCost.getDCost(),
                    baseCuryRate, portCuryRate,
                    rs.getString("FTradeCury"), bargainDate, inteCost.getSPortCode(), 2));
            }
            inteCost.setDMCost(inteCost.getDCost());
            inteCost.setDVCost(inteCost.getDCost());

            inteCost.setDMBaseCost(inteCost.getDBaseCost());
            inteCost.setDVBaseCost(inteCost.getDBaseCost());

            inteCost.setDMPortCost(inteCost.getDPortCost());
            inteCost.setDVPortCost(inteCost.getDPortCost());

            inteCost.checkStateId = 1;
            inteCost.setSTsfTypeCode("05");
            inteCost.setSSubTsfTypeCode("05FI");

            inteCost.setAttrClsCode(rs.getString("FAttrClsCode"));

            alIntegrated.add(inteCost);

        } catch (Exception ex) {
            throw new YssException("获取证券库存变动出错！", ex);
        }
    }

    /**
     * 计算实际利率的卖出成本，取证券应收应付库存进行加权平均
     * MS00656  QDV4赢时胜(上海)2009年8月24日01_A
     * 2009.09.07 蒋锦
     * @param sTsfTypeCode String：调拨类型
     * @param sSubTsfTypeCode String：调拨子类型
     * @param rs ResultSet
     * @return YssCost：流出成本
     * @throws YssException
     */
    private YssCost getSellPADCost(String sTsfTypeCode, String sSubTsfTypeCode, ResultSet rs) throws YssException{
        ResultSet subRs = null;
        SecRecPayStorageAdmin recPayStg = new SecRecPayStorageAdmin();
        SecRecPayBalBean recBalBean = null;
        ArrayList alRecPayStg = null;
        StringBuffer bufSql = new StringBuffer();
        YssCost cost = new YssCost();
        double baseCuryRate = 0;
        double portCuryRate = 0;
        try {
            recPayStg.setYssPub(pub);
            //获取昨日的应收应付库存
            alRecPayStg =
                recPayStg.getSecRecBeans(rs.getString("FSecurityCode"),
                                         YssFun.addDay(bargainDate, -1),
                                         YssFun.addDay(bargainDate, -1),
                                         sTsfTypeCode,
                                         sSubTsfTypeCode,
                                         rs.getString("FPortCode"),
                                         rs.getString("FInvMgrCode"),
                                         "",
                                         rs.getString("FTradeCury"),
                                         false,
                                         rs.getString("FAttrClsCode"),
                                         rs.getString("FInvestType"));

            if (alRecPayStg != null && alRecPayStg.get(0) != null) {
                recBalBean = (SecRecPayBalBean) alRecPayStg.get(0);
                cost.setCost(recBalBean.getDBal());
                cost.setBaseCost(recBalBean.getDBaseBal());
                cost.setPortCost(recBalBean.getDPortBal());
                cost.setAmount(rs.getDouble("FStorageAmount"));

                bufSql.append(" SELECT a.*, b.FTradeCury ");
                bufSql.append(" FROM ").append(pub.yssGetTableName("TB_Data_IntBakBond")).append(" a");
                bufSql.append(" LEFT JOIN " + pub.yssGetTableName("TB_Para_Security") + " b ");
                bufSql.append(" ON a.FSecurityCode = b.FSecurityCode");
                bufSql.append(" WHERE a.FCheckState = 1 ");
                bufSql.append(" AND a.FSecurityCode = ").append(dbl.sqlString(rs.getString("FSecurityCode")));
                bufSql.append(" AND a.FPortCode = ").append(dbl.sqlString(rs.getString("FPortCode")));
                bufSql.append(" AND a.FBARGAINDATE = ").append(dbl.sqlDate(bargainDate));
                if (rs.getString("FAttrClsCode") != null && rs.getString("FAttrClsCode").length() > 0) {
                    bufSql.append(" AND a.FAttrClsCode = " + dbl.sqlString(rs.getString("FAttrClsCode")));
                }
                if (rs.getString("FInvMgrCode").trim().length() > 0) {
                    bufSql.append(" AND a.FInvMgrCode = ").append(dbl.sqlString(rs.getString("FInvMgrCode")));
                }
                //按成交比号排序
                if (hmOrderPorts.get(rs.getString("FPortCode")) != null) {
                    bufSql.append(" AND a.FNum < ").append(dbl.sqlString(rs.getString("FNum")));
                    bufSql.append(" ORDER BY a.FNum ");
                } else { //按先买后卖计算
                    //如果按照先买后卖计算则卖出的交易编号不能大于需要计算的编号
                    bufSql.append(" AND (a.FTradeTypeCode = '01' OR (a.FTradeTypeCode <> '01' AND a.FNum < " + dbl.sqlString(rs.getString("FNum")) + "))");
                    bufSql.append(" ORDER BY a.FTradeTypeCode");
                }

                subRs = dbl.queryByPreparedStatement(bufSql.toString());
                while (subRs.next()) {
                    double dbCarryedAmount = subRs.getDouble("FTradeAmount");

                    if (subRs.getString("FTradeTypeCode").equalsIgnoreCase("01")) {
                        double dbTradeMoney = 0;
                        baseCuryRate = subRs.getDouble("FBaseCuryRate");
                        portCuryRate = subRs.getDouble("FPortCuryRate");
                        if (hmFee.get(subRs.getString("FPortCode")) != null) {
                            dbTradeMoney = YssD.add(subRs.getDouble("FTradeMoney"), subRs.getDouble("FBankFee"));
                        } else {
                            dbTradeMoney = subRs.getDouble("FTradeMoney");
                        }
                        if(sSubTsfTypeCode.equalsIgnoreCase("05FIFA")){
                            cost.setCost(
                                YssD.add(
                                    cost.getCost(),
                                    YssD.mul(
                                        dbCarryedAmount,
                                        rs.getDouble("FFaceValue"))));
                        } else {
                            cost.setCost(
                                YssD.add(
                                    cost.getCost(),
                                    YssD.sub(
                                        dbTradeMoney,
                                        YssD.mul(
                                            dbCarryedAmount,
                                            rs.getDouble("FFaceValue")))));
                        }
                        cost.setMCost(cost.getCost());
                        cost.setVCost(cost.getCost());
                        cost.setBaseCost(YssD.add(this.getSettingOper().calBaseMoney(dbTradeMoney,
                            baseCuryRate, 2), cost.getBaseCost()));
                        cost.setPortCost(YssD.add(this.getSettingOper().calPortMoney(dbTradeMoney,
                            baseCuryRate, portCuryRate,
                            subRs.getString("FTradeCury"), subRs.getDate("FBARGAINDATE"), subRs.getString("FPortCode"), 2), cost.getPortCost()));
                        cost.setBaseMCost(cost.getBaseCost());
                        cost.setBaseVCost(cost.getBaseCost());
                        cost.setPortMCost(cost.getPortCost());
                        cost.setPortVCost(cost.getPortCost());
                        cost.setAmount(YssD.add(cost.getAmount(), subRs.getDouble("FTradeAmount")));
                    } else {
                        cost.setCost(
                            YssD.sub(cost.getCost(),
                                     YssD.mul(YssD.round(YssD.div(cost.getCost(), cost.getAmount()), 2), dbCarryedAmount)));
                        cost.setMCost(cost.getCost());
                        cost.setVCost(cost.getCost());

                        cost.setBaseCost(
                            YssD.sub(cost.getBaseCost(),
                                     YssD.mul(YssD.round(YssD.div(cost.getBaseCost(), cost.getAmount()), 2), dbCarryedAmount)));
                        cost.setBaseMCost(cost.getBaseCost());
                        cost.setBaseVCost(cost.getBaseCost());

                        cost.setPortCost(
                            YssD.sub(cost.getPortCost(),
                                     YssD.mul(YssD.round(YssD.div(cost.getPortCost(), cost.getAmount()), 2), dbCarryedAmount)));
                        cost.setPortMCost(cost.getPortCost());
                        cost.setPortVCost(cost.getPortCost());

                        cost.setAmount(YssD.sub(cost.getAmount(), dbCarryedAmount));
                    }
                }

                if (cost.getAmount() != rs.getDouble("FTradeAmount")) {
                    cost.setCost(
                        YssD.mul(YssD.round(YssD.div(cost.getCost(), cost.getAmount()), 2), rs.getDouble("FTradeAmount")));
                    cost.setMCost(cost.getCost());
                    cost.setVCost(cost.getCost());

                    cost.setBaseCost(
                        YssD.mul(YssD.round(YssD.div(cost.getBaseCost(), cost.getAmount()), 2), rs.getDouble("FTradeAmount")));
                    cost.setBaseMCost(cost.getBaseCost());
                    cost.setBaseVCost(cost.getBaseCost());

                    cost.setPortCost(
                        YssD.mul(YssD.round(YssD.div(cost.getPortCost(), cost.getAmount()), 2), rs.getDouble("FTradeAmount")));
                    cost.setPortMCost(cost.getPortCost());
                    cost.setPortVCost(cost.getPortCost());
                }

            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        } finally{
            dbl.closeResultSetFinal(subRs);
        }
        return cost;
    }
    /***
     * 
     * @方法名：getIntFeeCashPecPay
     * @参数：alCashRecPay 应收应付list,用来增加应收应付bean ；rs 银行间债券交易记录用来初始化bean中的参数; tradeFee,将费用取出来方便重复调用产生bean
     * @param: feeType 费用类型07FE01   应付银行间债券交易手续费            07FE02   应付银行间债券银行手续费            07FE03   应付银行间债券结算服务费
     * @throws SQLException 
     * @throws YssException 
     * @返回类型：void
     * @说明：add zhouxiang 20100831 
     */
    private void getIntFeeCashPecPay(ArrayList alCashRecPay, ResultSet rs,
			double tradeFee, String feeType) throws SQLException, YssException {
    	CashPecPayBean cashRec = null;
    	cashRec = new CashPecPayBean();
        cashRec.setTradeDate(rs.getDate("FBargainDate"));
        cashRec.checkStateId = 1;
        cashRec.setRelaNum(rs.getString("FNum"));
        cashRec.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_INTERBANKBOND);
        cashRec.setPortCode(rs.getString("FPortCode"));
        if (this.analy1) {
            cashRec.setInvestManagerCode(rs.getString("FInvMgrCode"));
        } else {
            cashRec.setInvestManagerCode(" ");
        }
        cashRec.setBrokerCode(" ");

        cashRec.setCashAccCode(rs.getString("FCashAccCode"));
        cashRec.setCuryCode(rs.getString("FTradeCury"));
        cashRec.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);
        cashRec.setSubTsfTypeCode(feeType);
        cashRec.setInOutType(1);

        cashRec.setMoney(tradeFee);
        cashRec.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
        cashRec.setPortCuryRate(rs.getDouble("FPortCuryRate"));



        cashRec.setBaseCuryMoney(this.getSettingOper().calBaseMoney(cashRec.getMoney(),
            cashRec.getBaseCuryRate(), 2));

        cashRec.setPortCuryMoney(this.getSettingOper().calPortMoney(cashRec.getMoney(),
            cashRec.getBaseCuryRate(), cashRec.getPortCuryRate(),
            rs.getString("FTradeCury"), bargainDate, cashRec.getPortCode(), 2));
        alCashRecPay.add(cashRec);
	}
 
    /****
     * 
     * @throws YssException 
     * @方法名：getCashTransFee
     * @参数： 
     * @返回类型：void
     * @说明：获取每日收付中设置了每日收付的费用的资金调拨
     */
    private void getCashTransFee(ArrayList alCashTrans, ResultSet rs) throws YssException {
	        try {
        	String portcode=rs.getString("FPortCode");
        	if("ture".equals(hmSettleFee.get(portcode))){            	//判断是否设置了每日收付中的结算费用收付
            	double settle=rs.getDouble("FSettleFee");
            	if(settle>0)
            	GetFeeTypeTransfer("03FE03",settle,alCashTrans,rs);
            }
            if("ture".equals(hmTradeFee.get(portcode))){				//判断是否设置了每日收付中的交易费用收付
                double tradeFee=rs.getDouble("FFEE");
            	if(tradeFee>0)
            	GetFeeTypeTransfer("03FE01",tradeFee,alCashTrans,rs);	
            }
            if ("ture".equals(hmBankFee.get(portcode))) {				//判断是否设置了每日收付中的银行手续收付	
                double bankFee=rs.getDouble("FBankFee");
                if(bankFee>0){
                GetFeeTypeTransfer("03FE02",bankFee,alCashTrans,rs);		
                }		
            }
  
        } catch (Exception ex) {
            throw new YssException("生成资金调拨数据出错！", ex);
        }
	}

    /***
     * 
     * @throws SQLException 
     * @方法名：GetFeeTypeTransfer
     * @参数：SubTsfTypeCode 费用的子调拨类型  money 费用的调拨金额  资金调拨列表  alCashTrans
     * @返回类型：void
     * @说明：对传入的资金调拨类型和金额产生相应的资金调拨
     */
    private void GetFeeTypeTransfer(String SubTsfTypeCode, double money,
			ArrayList alCashTrans,ResultSet rs) throws SQLException {
    	TransferBean transfer = null;
        TransferSetBean transferset = null;
        ArrayList alSubTrans = new ArrayList();
        transfer = new TransferBean();
        transfer.setCheckStateId(1);
        transfer.setDtTransDate(rs.getDate("FBARGAINDATE"));
        transfer.setDtTransferDate(rs.getDate("FSettleDate"));
        transfer.setStrPortCode(rs.getString("FPortCode"));
        transfer.setStrSecurityCode(rs.getString("FSecurityCode"));
        transfer.setStrSubTsfTypeCode(SubTsfTypeCode); //设置费用的类型
      
        transfer.setStrTsfTypeCode("03");
        transfer.setFRelaNum(rs.getString("FNum"));
        transfer.setFNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_INTERBANKBOND);
        cashTradeNum += (transfer.getFRelaNum() + ",");

        transferset = new TransferSetBean();
        transferset.setIInOut(-1);
        transferset.setDMoney(money);			       //设置费用的金额
       
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

	}

}
