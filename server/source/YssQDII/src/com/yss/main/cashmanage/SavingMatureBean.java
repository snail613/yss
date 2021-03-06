package com.yss.main.cashmanage;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.*;
import com.yss.main.operdeal.income.paid.*;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.pojo.dayfinish.*;
import com.yss.util.*;

public class SavingMatureBean
    extends BaseBean implements IClientOperRequest {

    public SavingMatureBean() {
    }

    public String strTransNum = "";
    public String lxTransNum = ""; //利息的调拨编号
    String reqAry[] = null;
    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\r\r\n\n");
        } catch (Exception e) {
            throw new YssException("解析数据出错" + "\r\n" + e.getMessage(), e);
        }
    }

    /**
     * checkRequest
     *
     * @return String
     */
    public String checkRequest(String sType) {
        return "";
    }

    //创建一笔新的定期存款
    private void createSaving(SavingOutAccBean outAcc, SavingBean inAcc) throws
        YssException {
        SavingAdmin savAdmin = new SavingAdmin();
        savAdmin.setYssPub(pub);
//      String sOldSavingNum = savAdmin.loadTransNums(outAcc.getOutAccDate(),
//            outAcc.getOutAccDate(),
//            outAcc.getCashAccCode(),
//            outAcc.getPortCode(),
//            outAcc.getInvMgrCode(),
//            outAcc.getCatCode(), "");
        if (inAcc.getStrSubAccType().equals("0102")) { //定期存款帐户
            //插入到存款业务表
            inAcc.checkStateId = 1;
            outAcc.checkStateId = 1;
            inAcc.setDataSource(0);
            inAcc.addOutAcc(outAcc);
            savAdmin.setYssPub(pub);
            savAdmin.addList(inAcc);
            savAdmin.insert(outAcc.getOutAccDate(), outAcc.getOutAccDate(),
                            outAcc.getCashAccCode(),
                            outAcc.getPortCode(),
                            outAcc.getInvMgrCode(),
                            outAcc.getCatCode(), "");
//         savAdmin.insert();
            createSavCashTrans(outAcc, inAcc, savAdmin.getInsertSavingNum(), "Saving");
        } else {
            createSavCashTrans(outAcc, inAcc, outAcc.getInAccNum(), "SavMature");
        }

//      savAdmin.insert(outAcc.getCashAccCode(),outAcc.getPortCode(),
//                      outAcc.getInvMgrCode(),outAcc.getCatCode(),"");

//      this.strTransNum = savAdmin.getCashTransNums();
    }

    private void createSavCashTrans(SavingOutAccBean outAcc, SavingBean inAcc, String sSavingNum, String sNumType) throws
        YssException {
        TransferBean tran = new TransferBean();
        TransferSetBean transfersetIn = new TransferSetBean();
        TransferSetBean transfersetOut = new TransferSetBean();
        ArrayList tranSetList = new ArrayList();

        //增加资金调拨记录
        tran.setYssPub(pub);
        tran.setDtTransDate(inAcc.getSavingDate()); //存入时间
        tran.setDtTransferDate(inAcc.getSavingDate());
        tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
        tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_SAVING);
        tran.setStrTransferTime("00:00:00");
//      tran.setDataSource(0);
        tran.setDataSource(1); //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
        tran.setSavingNum(sSavingNum);
        tran.setFNumType(sNumType);
        tran.checkStateId = 1;
        tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                             "yyyyMMdd HH:mm:ss");
//      tran.setDataSource(0); //自动计算标志
        tran.setDataSource(1); //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
        //资金流入帐户
        transfersetIn.setDMoney(inAcc.getInMoney());
        transfersetIn.setSPortCode(inAcc.getPortCode());
        transfersetIn.setSAnalysisCode1(inAcc.getInvMgrCode());
        transfersetIn.setSAnalysisCode2(inAcc.getCatCode());
        transfersetIn.setDBaseRate(inAcc.getBaseCuryRate());
        transfersetIn.setDPortRate(inAcc.getPortCuryRate());
        transfersetIn.setSCashAccCode(inAcc.getCashAccCode());
        transfersetIn.setIInOut(1);
        transfersetIn.checkStateId = 1;

        //资金流出帐户
        transfersetOut.setDMoney(outAcc.getOutMoney());
        transfersetOut.setSPortCode(outAcc.getPortCode());
        transfersetOut.setSAnalysisCode1(outAcc.getInvMgrCode());
        transfersetOut.setSAnalysisCode2(outAcc.getCatCode());
        transfersetOut.setDBaseRate(inAcc.getBaseCuryRate());
        transfersetOut.setDPortRate(inAcc.getPortCuryRate());
        transfersetOut.setSCashAccCode(outAcc.getCashAccCode());
        transfersetOut.setIInOut( -1);
        transfersetOut.checkStateId = 1;

        tranSetList.add(transfersetIn);
        tranSetList.add(transfersetOut);

        CashTransAdmin tranAdmin = new CashTransAdmin();
        tranAdmin.setYssPub(pub);
        tranAdmin.addList(tran, tranSetList);
        tranAdmin.insert(sSavingNum, "", sNumType);
    }

    //付息，修改为调用收益支付那边的过程 胡昆 20071011  添加了一个参数，以适应付息方法的修改。 sj edit 20080326
    private void paidAccInterest(SavingOutAccBean outAcc, String inNum) throws YssException {
        AccPaid accpaid = new AccPaid();
        ArrayList alPaid = new ArrayList();
        PaidAccIncome paidAcc = new PaidAccIncome();
        CashAccountBean cashAcc = new CashAccountBean();
        try {
            cashAcc.setYssPub(pub);
            cashAcc.setStrCashAcctCode(outAcc.getCashAccCode());
            cashAcc.getSetting();

            paidAcc.setYssPub(pub);
            accpaid.setDDate(outAcc.getOutAccDate());
            accpaid.setCashAccCode(outAcc.getCashAccCode());
            accpaid.setPortCode(outAcc.getPortCode());
            accpaid.setInvmgrCode(outAcc.getInvMgrCode());
            accpaid.setCatCode(outAcc.getCatCode());
            //   accpaid.setCuryCode(" ");
            accpaid.setCuryCode(cashAcc.getStrCurrencyCode()); //20080411  为了获取现金帐户的货币  chenyb
            accpaid.setLx(outAcc.getRecIntrest()); //??
            //--------在到期处理时，产生一笔利息收入 sj 20080318---
            accpaid.setMoney(outAcc.getRecIntrest());
            //-------------------------------------------------
            accpaid.setTsfTypeCode("02");
            accpaid.setSubTsfTypeCode("02DE");
            accpaid.setBaseCuryRate(outAcc.getBaseRate());
            accpaid.setPortCuryRate(outAcc.getPortRate());
            accpaid.setNumType("autoSaveInterest");//edit by zhouwei 解决现金应收应付重复产生accpaid.setNumType("SavingIns");
            accpaid.setRelaNum(inNum);
            accpaid.setBaseMoney(this.getSettingOper().calBaseMoney(accpaid.
                getMoney(), accpaid.getBaseCuryRate()));
            accpaid.setPortMoney(this.getSettingOper().calPortMoney(accpaid.
                getMoney(), accpaid.getBaseCuryRate(), accpaid.getPortCuryRate(),
                //linjunyun 2008-11-25 bug:MS00011 增加判断计算组合货币成本的方式，直接通过原币成本和直接汇率
                accpaid.getCuryCode(), accpaid.getDDate(), accpaid.getPortCode()));
            alPaid.add(accpaid);
            // MS00020 QDV4.1赢时胜（上海）2009年4月20日20_A 在此设置支付的类型为手工方式。
            //edit by zhouwei 20120319 将手动状态改为自动状态，与业务处理的定存业务保持一致(不作区分)
            paidAcc.saveIncome(alPaid, inNum, true); //添加了定存编号。 sj edit 20080326 此处新加的false，说明此到期处理为手工进行
            //--------------------------------------------------------------------
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    /*
       //付息
       private void paidAccInterest(SavingOutAccBean outAcc) throws YssException {
          //插入到现金应收应付数据表，调拨类型是收入，存款利息收入
          CashPecPayBean cashPay = new CashPecPayBean();
          cashPay.setTradeDate(outAcc.getOutAccDate());
          cashPay.setCashAccCode(outAcc.getCashAccCode());
          cashPay.setPortCode(outAcc.getPortCode());
          cashPay.setInvestManagerCode(outAcc.getInvMgrCode());
          cashPay.setCategoryCode(outAcc.getCatCode());
          cashPay.setMoney(outAcc.getRecIntrest());
          cashPay.setTsfTypeCode("02");
          cashPay.setSubTsfTypeCode("02DE");    //存款利息从1001调整为02DE fazmm20071010
          cashPay.setBaseCuryRate(outAcc.getBaseRate());
          cashPay.setPortCuryRate(outAcc.getPortRate());

          CashAccountBean ca = new CashAccountBean();
          ca.setYssPub(pub);
          ca.setStrCashAcctCode(outAcc.getCashAccCode());
          ca.getSetting();
          cashPay.setCuryCode(ca.getStrCurrencyCode());

          cashPay.checkStateId = 1;

          CashPayRecAdmin cashPayAdmin = new CashPayRecAdmin();
          cashPayAdmin.setYssPub(pub);
          cashPayAdmin.addList(cashPay);
          cashPayAdmin.insert();
//      String sOldCPRNums = cashPayAdmin.loadCashPRNums(outAcc.getOutAccDate(),
//            "02", "1001", outAcc.getCashAccCode(), outAcc.getPortCode(),
//            outAcc.getInvMgrCode(), outAcc.getCatCode());
//      cashPayAdmin.insert(outAcc.getOutAccDate(), "02", "1001",
//                          outAcc.getPortCode(),
//                          outAcc.getInvMgrCode(), outAcc.getCatCode(),
//                          outAcc.getCashAccCode(), "", 0);
//      cashPayAdmin.insert(outAcc.getOutAccDate(),"02","1001",outAcc.getPortCode(),outAcc.getCashAccCode(),"");
          if (cashPayAdmin.getInsertNum().length() > 0) {
             //产生付息的资金流
             createPaidCashTrans(outAcc, cashPayAdmin.getInsertNum());
          }

       }
     */
    private void createPaidCashTrans(SavingOutAccBean outAcc, String sCPRNum) throws
        YssException {
        TransferBean tran = new TransferBean();
        TransferSetBean transfersetIn = new TransferSetBean();
		/**shashijie 2012-7-2 STORY 2475 */
        //TransferSetBean transfersetOut = new TransferSetBean();
		/**end*/
        ArrayList tranSetList = new ArrayList();

//      CashPayRecAdmin cashPayAdmin = new CashPayRecAdmin();
//      cashPayAdmin.setYssPub(pub);
//      cashPayAdmin.insert(outAcc.getOutAccDate(), "02", "1001",
//                          outAcc.getPortCode(),
//                          outAcc.getInvMgrCode(), outAcc.getCatCode(),
//                          outAcc.getCashAccCode(), "", 0);

        //产生资金流，增加资金调拨记录
        tran.setYssPub(pub);
        tran.setDtTransDate(outAcc.getOutAccDate()); //存入时间
        tran.setDtTransferDate(outAcc.getOutAccDate());
        tran.setStrTsfTypeCode("02"); //收入
        tran.setStrSubTsfTypeCode("02DE"); //存款收入  //存款利息从1001调整为02DE fazmm20071010
        tran.setStrTransferTime("00:00:00");
        tran.setCprNum(sCPRNum);
//      tran.setDataSource(0);
        tran.setDataSource(1); //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
        tran.checkStateId = 1;
        tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                             "yyyyMMdd HH:mm:ss");

        //资金流入帐户
        transfersetIn.setDMoney(outAcc.getRecIntrest());
        transfersetIn.setSPortCode(outAcc.getPortCode());
        transfersetIn.setSAnalysisCode1(outAcc.getInvMgrCode());
        transfersetIn.setSAnalysisCode2(outAcc.getCatCode());
        transfersetIn.setDBaseRate(outAcc.getBaseRate());
        transfersetIn.setDPortRate(outAcc.getPortRate());
        transfersetIn.setIInOut(1);
        transfersetIn.checkStateId = 1;
        transfersetIn.setSCashAccCode(outAcc.getCashAccCode());

        tranSetList.add(transfersetIn);

        CashTransAdmin tranAdmin = new CashTransAdmin();
        tranAdmin.setYssPub(pub);
        tranAdmin.addList(tran, tranSetList);
        tranAdmin.insert();
//      tranAdmin.insert("", outAcc.getOutAccDate(), outAcc.getOutAccDate(), "02",
//                       "1001", "", "", 0, outAcc.getCashAccCode(),
//                       outAcc.getPortCode(), -1, outAcc.getInvMgrCode(),
//                       outAcc.getCatCode(), "");

    }

    /**
     * doOperation
     *
     * @param sType String
     * @return String
     */
    public String doOperation(String sType) throws YssException {
        String[] arrInAcc = null;
        SavingAdmin savAdmin = new SavingAdmin();
        savAdmin.setYssPub(pub);
        CashTransAdmin cashTransAdmin = new CashTransAdmin();
        cashTransAdmin.setYssPub(pub);
        CashPayRecAdmin cashPayAdmin = new CashPayRecAdmin();
        cashPayAdmin.setYssPub(pub);

        SavingOutAccBean outAcc = null;
        SavingBean inAcc = null;
        String[] sArr = null;
        String sTmp1, sTmp2 = "";
        Connection conn = dbl.loadConnection();
        
        try {
            conn.setAutoCommit(false);
            if(reqAry==null) 
            	return "";//添加判断，如果reqAry为空，则不做操作 合并太平版本时调整 by leeyu 20100812
            for (int i = 0; i < reqAry.length; i++) { //一笔数据
                //设置流出帐户
                outAcc = new SavingOutAccBean();
                sArr = reqAry[i].split("\r\n");
                outAcc.setYssPub(pub);
                outAcc.parseRowStr(sArr[0]);
                //根据流出帐户找出付息记录
                //存款利息从1001调整为02DE fazmm20071010
                String sOldCPRNums = cashPayAdmin.loadCashPRNums(outAcc.getOutAccDate(),
                    "02", "02DE", outAcc.getCashAccCode(), outAcc.getPortCode(),
                    outAcc.getInvMgrCode(), outAcc.getCatCode());
                if (sOldCPRNums.length() == 0) {
                    sOldCPRNums = "null";
                }
                //根据付息的记录，删除对应资金调拨
                cashTransAdmin.delete("", sOldCPRNums);
                //cashPayAdmin.delete(sOldCPRNums);

                //根据流出帐户先删除存款记录,
                //先通过流出帐户找到对应的存款记录编号
//            String sOldSavingNum = savAdmin.loadTransNums(outAcc.getOutAccDate(),
//                  outAcc.getOutAccDate(),
//                  outAcc.getCashAccCode(),
//                  outAcc.getPortCode(),
//                  outAcc.getInvMgrCode(),
//                  outAcc.getCatCode(), "");
                //再删除这些存款记录
                savAdmin.delete(outAcc.getOutAccDate(),
                                outAcc.getOutAccDate(),
                                outAcc.getCashAccCode(),
                                outAcc.getPortCode(),
                                outAcc.getInvMgrCode(),
                                outAcc.getCatCode(), "");
                //根据存款记录编号删除对应资金调拨
//            if (sOldSavingNum.length() == 0) { //如果没有对应的存款编号，就付值"null",这是为了不删除资金调拨中的记录
//               sOldSavingNum = "null";
//            }
//            cashTransAdmin.delete(sOldSavingNum,"");
                
                delCashTransfer(outAcc); // add by wangzuochun MS01459    关于存款业务目前系统的处理及存在的问题    QDV4国内(测试)2010年07月19日02_B    

                paidAccInterest(outAcc, outAcc.getInAccNum()); //付息  增加了定存编号。
                arrInAcc = sArr[1].split("\f\f"); //分拆流入 一笔的流入
                for (int j = 0; j < arrInAcc.length; j++) {
                    inAcc = new SavingBean();
                    inAcc.setYssPub(pub);
                    inAcc.parseRowStr(arrInAcc[j]);
                    inAcc.setInMoney(YssD.add(inAcc.getInMoney(),
                                              inAcc.getRecInterest()));
                    createSaving(outAcc, inAcc); //创建新的定期存款
                }

//            outacc = new SavingOutAccBean();
//            inacc = new SavingBean();
//            ArrayList outAll = new ArrayList(); //为了适应表结构
//            String[] sArr = null;
//            sArr = reqAry[i].split("\r\n"); //分拆 流出 和 流入
//            outacc.setYssPub(pub);
//            outacc.parseRowStr(sArr[0]);
//
//            arrInAcc = sArr[1].split("\f\f"); //分拆流入 一笔的流入
//            for (int j = 0; j < arrInAcc.length; j++) {
//               inacc = new SavingBean();
//               inacc.setYssPub(pub);
//               inacc.parseRowStr(arrInAcc[j]);
//               insertCashPayRec(inacc); //插入到现金应收应付表
//               insertCashTrans(outacc, arrInAcc[j]); //插入到资金调拨表
//               if (inacc.getStrSubAccType().equals("0102")) { //如果是活期 就不插入到流入 流出帐户表里面去
//                  outacc.checkStateId = 1;
//                  outacc.setOutMoney(inacc.getInMoney() + inacc.getRecInterest()); //把每次流入的金额 和流出金额设成一样
//                  outAll.add(outacc); //把流出帐户放到一个ArrayList里面
//                  inacc.setTransNum(this.strTransNum); //本金的资金调拨编号
//                  inacc.setStrLxTransNum(this.lxTransNum); //利息的资金调拨编号
//                  inacc.checkStateId = 1;
//                  inacc.setAllOutAcc(outAll);
//                  savingmature.addList(inacc); //流入帐户增加到一个ArrayList
//               }
//            }
            }
            conn.commit();
            conn.setAutoCommit(true);
//         savingmature.insert(); //插入到流入帐户表 和流出帐户表
            return "";
        } catch (Exception e) {
            throw new YssException("保存到期数据出错" + "\r\n" + e.getMessage(), e);
        }
    }

    public void insertCashPayRec(SavingBean saving) throws YssException { //插入到应收应付数据表里面
        try {
            CashPayRecAdmin cashpayrec = new CashPayRecAdmin();
            cashpayrec.setYssPub(pub);
            CashPecPayBean cash = new CashPecPayBean();
            cash.setTradeDate(saving.getSavingDate());
            cash.setPortCode(saving.getPortCode());
            cash.setCashAccCode(saving.getCashAccCode());
            cash.setInvestManagerCode(saving.getInvMgrCode());
            cash.setCategoryCode(saving.getCatCode());
            cash.setBrokerCode(" ");
            cash.setTsfTypeCode("02"); //利息收入
            cash.setSubTsfTypeCode("02DE"); //存款利息               //存款利息从1001调整为02DE fazmm20071010
            cash.setMoney(saving.getRecInterest());
            cash.setBaseCuryRate(saving.getBaseCuryRate());
            cash.setBaseCuryMoney(saving.getRecInterest() * saving.getBaseCuryRate());
            cash.setPortCuryRate(saving.getPortCuryRate());
            cash.setPortCuryMoney(saving.getRecInterest() * saving.getBaseCuryRate() /
                                  saving.getPortCuryRate());
            cash.setCuryCode(saving.getCuryCode().equals("") ? " " :
                             saving.getCuryCode());
            cashpayrec.addList(cash);
            cashpayrec.insert(saving.getSavingDate(), "", "", saving.getPortCode(),
                              saving.getInvMgrCode(), saving.getCatCode(),
                              saving.getCashAccCode(), saving.getCuryCode(), 0);
        } catch (Exception e) {
            throw new YssException("插入到现金应收应付数据表出错" + "\r\n" + e.getMessage(), e);
        }
    }

    /**
     * 插入到资金调拨表
     * @param outacc SavingOutAccBean //流出
     * @param inaccs String[]
     * @throws YssException
     */
    public void insertCashTrans(SavingOutAccBean outacc, String inaccs) throws
        YssException { //插入到资金调拨表里面
        try {
            java.util.Date dTransDate = null; //调拨日期  为流入的存入日期

            TransferBean tran = new TransferBean();
            TransferBean tranLX = new TransferBean(); //利息的调拨主表信息
            TransferSetBean transferset = null;
            TransferSetBean transfersetout = null;
            ArrayList transfersets = new ArrayList(); //所有本金的资金调拨信息
            ArrayList transfersetLXs = new ArrayList(); //所有利息的资金调拨信息
            SavingBean saving = null;

            CashTransAdmin cashTransAdmin = null; //插入到资金调拨的BEAN

            saving = new SavingBean();
            saving.setYssPub(pub);
            saving.parseRowStr(inaccs);
            transferset = new TransferSetBean();
            dTransDate = saving.getSavingDate(); //流入的存入日期 就是流出的到期日期
            loadTranSetAttr(saving, null, transferset, 1);
            transfersets.add(transferset);

            //设置流出信息
            transfersetout = new TransferSetBean();
            transfersetout.setYssPub(pub);
            transfersetout.setDMoney(YssD.add(saving.getInMoney(),
                                              saving.getRecInterest())); //流入流出金额相同
            loadTranSetAttr(null, outacc, transfersetout, -1); //流出
            transfersets.add(transfersetout); //流出本金

            //加载本金资金调拨主表
            //-----------------------------------------------------
            tran.setYssPub(pub);
            tran.setDtTransDate(dTransDate); //存入时间
            tran.setDtTransferDate(dTransDate);
            tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
            tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_SAVING);
            tran.setStrTransferTime("00:00:00");
            tran.checkStateId = 1;
            tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                                 "yyyyMMdd HH:mm:ss");

            cashTransAdmin = new CashTransAdmin();
            cashTransAdmin.setYssPub(pub);
            cashTransAdmin.addList(tran, transfersets); //本金
            cashTransAdmin.pInsert(dTransDate, dTransDate,
                                   YssOperCons.YSS_ZJDBLX_InnerAccount,
                                   YssOperCons.YSS_ZJDBZLX_COST_SAVING, 0);
            strTransNum = cashTransAdmin.getTransNum();
            //-----------------------------------------------------

            if (saving.getRecInterest() != 0) {
                transferset.setDMoney(saving.getRecInterest()); //设置利息金额
                transferset.setDBaseRate(saving.getBaseCuryRate()); //设置其利息调拨的汇率为当期的汇率
                transferset.setDPortRate(saving.getPortCuryRate());
                transfersetLXs.add(transferset);

                transfersetout.setDMoney(saving.getRecInterest()); //把利息金额设置到流出帐户里面
                transfersetout.setDBaseRate(outacc.getBaseRate()); //当期汇率  是流出帐户的
                transfersetout.setDPortRate(outacc.getPortRate());
                transfersetLXs.add(transfersetout);

                //加载利息资金调拨主表
                //-----------------------------------------------------
                tranLX.setYssPub(pub);
                tranLX.setDtTransDate(dTransDate); //存入时间
                tranLX.setDtTransferDate(dTransDate);
                tranLX.setStrTsfTypeCode("02"); //收入
                tranLX.setStrSubTsfTypeCode("02DE"); //存款利息  //存款利息从1001调整为02DE fazmm20071010
                tranLX.setStrTransferTime("00:00:00");
                tranLX.checkStateId = 1;
                tranLX.creatorTime = YssFun.formatDate(new java.util.Date(),
                    "yyyyMMdd HH:mm:ss");

                cashTransAdmin = new CashTransAdmin();
                cashTransAdmin.setYssPub(pub);
                cashTransAdmin.addList(tranLX, transfersetLXs); //利息
                cashTransAdmin.pInsert(dTransDate, dTransDate,
                                       YssOperCons.YSS_ZJDBLX_InnerAccount,
                                       YssOperCons.YSS_ZJDBZLX_COST_SAVING, 0);
                this.lxTransNum = cashTransAdmin.getTransNum();
                //-----------------------------------------------------
            }
        } catch (Exception e) {
            throw new YssException("插入到资金调拨数据表出错" + "\r\n" + e.getMessage(), e);
        }
    }

    private void loadTranSetAttr(SavingBean saving,
                                 SavingOutAccBean savingoutacc,
                                 TransferSetBean transferset,
                                 int inOut) throws YssException {
        try {
            if ( (saving != null) && (savingoutacc == null)) {
                transferset.setDMoney(saving.getInMoney());
                transferset.setSPortCode(saving.getPortCode());
                transferset.setSAnalysisCode1(saving.getInvMgrCode());
                transferset.setSAnalysisCode2(saving.getCatCode());
                transferset.setDBaseRate(saving.getAvgBaseCuryRate());
                transferset.setDPortRate(saving.getAvgPortCuryRate());
                transferset.setSCashAccCode(saving.getCashAccCode());
            } else if ( (saving == null) && (savingoutacc != null)) {
                //  transferset.setDMoney(savingoutacc.getOutMoney());
                transferset.setSPortCode(savingoutacc.getPortCode());
                transferset.setSAnalysisCode1(savingoutacc.getInvMgrCode());
                transferset.setSAnalysisCode2(savingoutacc.getCatCode());
                transferset.setDBaseRate(savingoutacc.getAvgBaseRate());
                transferset.setDPortRate(savingoutacc.getAvgPortRate());
                transferset.setSCashAccCode(savingoutacc.getCashAccCode());

            }
            transferset.setIInOut(inOut); //流入流出标识
            transferset.checkStateId = 1;

        } catch (Exception e) {
            throw new YssException("加载资金调拨表数据出错" + "\r\n" + e.getMessage(), e);
        }

    }
    
    /**
     * add by wangzuochun MS01459    关于存款业务目前系统的处理及存在的问题    QDV4国内(测试)2010年07月19日02_B    
     * @param outAcc
     * @throws YssException
     */
    public void delCashTransfer(SavingOutAccBean outAcc) throws YssException {
    	
    	ResultSet rs = null;
        String strTransNum = "";
        
		try {
			String strSql = " Select * from "
					+ pub.yssGetTableName("Tb_cash_transfer")
					+ " Where FNumType in ('autoSaving','autoSaveInterest') and FSavingNum = "
					+ dbl.sqlString(outAcc.getInAccNum()) + " and FRelaNum = "
					+ dbl.sqlString(outAcc.getInAccNum())
					+ " and FTransferDate = "
					+ dbl.sqlDate(outAcc.getOutAccDate());

			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				strTransNum += rs.getString("FNum") + ",";
			}
			if (strTransNum.length() > 1) {
				strTransNum = strTransNum
						.substring(0, strTransNum.length() - 1);
				strTransNum = operSql.sqlCodes(strTransNum);
				if (strTransNum.trim().length() > 0) {
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_Transfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);

					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_SubTransfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);
				}
			}
		} catch (Exception e) {
			throw new YssException("删除业务处理产生的资金调拨出错" + "\r\n" + e.getMessage(),e);
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
	}

}
