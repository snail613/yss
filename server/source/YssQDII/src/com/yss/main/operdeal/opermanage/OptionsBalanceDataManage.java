package com.yss.main.operdeal.opermanage;

import java.util.*;

import com.yss.util.*;
import com.yss.main.cashmanage.TransferBean;
import java.sql.ResultSet;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.commeach.EachGetPubPara;
import com.yss.commeach.EachRateOper;
import com.yss.main.parasetting.SecurityBean;
import java.sql.Connection;
import com.yss.manager.CashTransAdmin;

/**
 * <p>Title:by xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持</p>
 * 
 * <P> xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
 * 
 * <p>Description:  对期权业务的处理-产生期权结算的数据，即当日结算数据产生资金调拨</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OptionsBalanceDataManage extends BaseOperManage{
    private ArrayList cashTransArr = null; //存放期权交易关联表数据
    private String securityCodes = ""; //存放期权代码
    public OptionsBalanceDataManage() {
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
     * 产生期权结算的数据，即当日结算数据产生资金调拨
     * @throws YssException
     */
    public void doOpertion() throws YssException {
        createBalanceCashTranf();
    }

    /**
     * createBalanceCashTranf 当日结算数据产生资金调拨
     */
    private void createBalanceCashTranf() throws YssException {
        cashTransArr = getTheDayTradeData();//获取今天期权交易关联表中数据的方法，返回ArrayList
        //if (cashTransArr.size() > 0) {//判断今天是否有交易数据，有调用保存数据方法saveCashTransferData
            saveCashTransferData(cashTransArr);
        //}
    }

    /**
     * saveCashTransferData 把结算数据插入到资金调拨表和资金调拨子表
     *
     * @param cashTransArr ArrayList
     */
    private void saveCashTransferData(ArrayList cashTransData) throws YssException {
        CashTransAdmin cashtrans = null;//初始化资金调拨操作类
        String filtersRelaNums = "";//保存交易编号
        boolean bTrans = false;//事务控制
        Connection conn = dbl.loadConnection();//获取连接
        try {
            //if (cashTransData.size() > 0) {//是否当天有交易
                cashtrans = new CashTransAdmin();
                cashtrans.setYssPub(pub);
                cashtrans.addList(cashTransData);//子资金调拨的值放入TransferBean中的arrayList中
                //增加事务控制和锁，以免在多用户同时处理时出现调拨编号重复
                conn.setAutoCommit(false);//设置不自动提交事务
                bTrans = true;
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer"));//给操作的表加锁
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));
                cashtrans.insert(this.dDate,"05FP01,02FP01","OptionsTrade", this.sPortCode,"");//插入数据到资金调拨表和子表中 modify by fangjiang 2011.09.13 story 1342
                conn.commit();//提交事务
                conn.setAutoCommit(true);//设置为自动提交事务
                bTrans = false;
            //}
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 查询期权交易关联表数据，把数据set到TransferBean和TransferSetBean中
     * @return ArrayList
     * @throws YssException
     */
    private ArrayList getTheDayTradeData() throws YssException {
        ArrayList curCashTransArr = null;//保存资金调拨数据
        TransferBean transfer = null;//调拨类初始化
        TransferSetBean transferset = null;//调拨子类初始化
        ArrayList subtransfer = null;//保存资金子调拨
        ResultSet rs = null;
        boolean analy1;//分析代码1
        boolean analy2;//分析代码2
        boolean analy3;//分析代码3
        StringBuffer buff = null;
        //STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  start
        EachGetPubPara pubPara = new EachGetPubPara();;
        pubPara.setYssPub(pub);
        String sCostAccount_Para="";//是否核算成本参数
        //STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  end
        
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            buff = new StringBuffer();
            buff.append(" select a.*, b.FCuryCost, c.fstorageamount from ").append(pub.yssGetTableName("tb_data_optionstraderela"));
            buff.append(" a join (select fnum, sum(FCuryCost) as FCuryCost from ");
            buff.append(pub.yssGetTableName("tb_data_optionscost")).append(" group by fnum) b on (a.fnum || a.fclosenum) = b.fnum "); 
            buff.append(" left join (select fsecuritycode,fstorageamount from ").append(pub.yssGetTableName("tb_stock_security"));
            buff.append(" where FCheckState = 1 and fstoragedate = ").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));
            //wqh 2012-12-16 对库存也区分组和
            buff.append("and fportcode in (").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
            buff.append(" and FyearMonth = '").append(YssFun.getYear(dDate) + "01'");  //add by zhaoxianin 20130116 bug #6865  QDV4博时2013年01月08日01_B
            buff.append(" ) c on a.fsecuritycode = c.fsecuritycode where a.FPortCode in (").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
            buff.append(" and a.FBargainDate=").append(dbl.sqlDate(this.dDate));
            rs = dbl.queryByPreparedStatement(buff.toString());//从期权交易关联表中获取数据
            buff.delete(0, buff.length());
            curCashTransArr = new ArrayList();
            while (rs.next()) {
            	//STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  start
            	pubPara.setSPortCode(rs.getString("FPortCode"));
                pubPara.setSPubPara(rs.getString("fsecuritycode"));
                pubPara.setsDate(YssFun.formatDate(rs.getDate("fbargaindate")));
                //20120816 added by liubo.Story #2754
                //与钱有关的项的判断，不在判断通参的“是否核算成本”的值，改为判断“是否有资金流动”的值
                //=================================
                pubPara.setCtlFlag("selTransfering");
                if("32FP".equalsIgnoreCase(rs.getString("FTradeTypeCode"))){
                	if(rs.getDouble("fstorageamount") > 0){
                		pubPara.setTradeType("01");
                	}else{
                		pubPara.setTradeType("02");
                	}
                }else{
                	pubPara.setTradeType(rs.getString("FTradeTypeCode"));
                }        
                //==============end===================
                sCostAccount_Para = pubPara.getOptCostAccountSet();
            	//STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  end
                if (rs.getString("FTradeTypeCode").equals("01")) { //买入
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "QSKHC");
                    if(rs.getString("FCloseNum").equals("02")){//买入平仓
                    	if(sCostAccount_Para.equalsIgnoreCase("false")){
                    		transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"QSKOUT"); //不核算成本，清算款=费用
                    	}else{
                    		transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"QSKIN"); //核算成本，清算款=成交金额-费用
                    	}                        
                    }else{
                        transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"QSKOUT"); //不核算成本，清算款=费用；核算成本，清算款=成交金额+费用
                    }
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);
                  
                    //不核算成本,平仓的时候要产生平仓损益  add by jiangshichao 2011.07.19
                    if(rs.getString("FCloseNum").equals("02") && sCostAccount_Para.equalsIgnoreCase("false")){
                    	 subtransfer = new ArrayList();
                         transfer = setTransferAttr(rs, "PCSY");
                         transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"PCSY");
                         subtransfer.add(transferset);
                         transfer.setSubTrans(subtransfer);
                         curCashTransArr.add(transfer);
                    }
                } else if (rs.getString("FTradeTypeCode").equals("02")) { //卖出
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "QSKHR");
                    if(rs.getString("FCloseNum").equals("04")){//卖出平仓
                        transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"QSKOUT");//不核算成本，清算款=费用；核算成本，清算款=成交金额+费用
                    }else{
                    	if(sCostAccount_Para.equalsIgnoreCase("false")){
                    		transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"QSKOUT");//不核算成本，清算款=费用
                    	}else{
                    		transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"QSKIN");//核算成本，清算款=成交金额-费用
                    	}                        
                    }
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);
                    
                    //不核算成本,平仓的时候要产生平仓损益  add by jiangshichao 2011.07.19
                    if(rs.getString("FCloseNum").equals("04") && sCostAccount_Para.equalsIgnoreCase("false")){
                    	 subtransfer = new ArrayList();
                         transfer = setTransferAttr(rs, "PCSY");
                         transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"PCSY");
                         subtransfer.add(transferset);
                         transfer.setSubTrans(subtransfer);
                         curCashTransArr.add(transfer);
                    }                  
                } else if (rs.getString("FTradeTypeCode").equals("32FP")){//期权行权
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "QSKHR"); 
                    if(sCostAccount_Para.equalsIgnoreCase("false")){
                		transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"QSKOUT"); 
                	}else{
                		if(rs.getDouble("fstorageamount") > 0){
                			transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"QSKIN"); 
                		}else{
                			transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"QSKOUT"); 
                		}                		
                	}     
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);
                    
                    //不核算成本, 产生期权行权的浮动盈亏
                    if(sCostAccount_Para.equalsIgnoreCase("false")){
                    	 subtransfer = new ArrayList();
                         transfer = setTransferAttr(rs, "XQSY");
                         transferset = setTransferSetAttr(rs, analy1, analy2, analy3,"XQSY");
                         subtransfer.add(transferset);
                         transfer.setSubTrans(subtransfer);
                         curCashTransArr.add(transfer);
                    }     
                } else if (rs.getString("FTradeTypeCode").equals("33FP")){//期权结算
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "QSKHC"); 
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3, "QSKOUT"); //资金调拨子表，清算款划出
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);
                }else{//期权放弃行权
                    continue;
                }
            }
        } catch (Exception e) {
            throw new YssException("查询期权交易关联表数据出错！\r\t", e);
        }finally{
        	dbl.closeResultSetFinal(rs);//关闭游标  by leeyu 20100909
        }
        return curCashTransArr;
    }

    /**
     * 设置资金调拨子表数据出错
     * @param rs ResultSet
     * @param analy1 boolean 分析代码1
     * @param analy2 boolean 分析代码2
     * @param analy3 boolean 分析代码3
     * @param type String 调拨类型
     * @return TransferSetBean
     * @throws YssException
     */
    private TransferSetBean setTransferSetAttr(ResultSet rs, boolean analy1,
                                               boolean analy2, boolean analy3,
                                               String type) throws YssException {
        TransferSetBean transferset = new TransferSetBean();//调拨子类
        double dBaseRate = 0;//基础汇率
        double dPortRate = 0;//组合汇率
        double money = 0.0;//调拨金额
        SecurityBean security = null;//证券信息类
        try {      	
            dBaseRate = rs.getDouble("FBaseCuryRate");
            dPortRate = rs.getDouble("FPortCuryRate");

            security = new SecurityBean();
            security.setYssPub(pub);
            security.setSecurityCode(rs.getString("FSecurityCode"));
            security.getSetting();

            transferset.setSPortCode(rs.getString("FPortCode"));//设置组合代码
            if (analy1) {
                transferset.setSAnalysisCode1(rs.getString("FInvMgrCode"));//投资经理
            }
            if (analy2) {
                transferset.setSAnalysisCode2("FBrokerCode");//券商代码
            }
            if (type.equalsIgnoreCase("QSKIN")) { //平仓清算款流入账户。
                transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                money = rs.getDouble("FSettleMoney");//调拨金额
                transferset.setIInOut(1);//调拨方向
            } else if (type.equalsIgnoreCase("QSKOUT")) { //开仓的设置，清算款流出账户。
                transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                money = rs.getDouble("FSettleMoney");//调拨金额
                transferset.setIInOut(-1);
            } else if (type.equalsIgnoreCase("PCSY")){
            	 transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));   
            	 if(rs.getString("fclosenum").equalsIgnoreCase("02")){
            		 money = YssD.round(YssD.sub(rs.getDouble("ftrademoney"), Math.abs(rs.getDouble("FCuryCost"))), 2);
            	 }else{
            		 money = YssD.round(YssD.sub(Math.abs(rs.getDouble("FCuryCost")), rs.getDouble("ftrademoney")), 2);
            	 }
            	 transferset.setIInOut(1);
            } else if (type.equalsIgnoreCase("XQSY")){
	           	 transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));	 
	        	 if(rs.getDouble("fstorageamount") > 0){
	        		 money = YssD.round(YssD.sub(rs.getDouble("ftrademoney"), Math.abs(rs.getDouble("FCuryCost"))), 2);
	        	 }else{
	        		 money = YssD.round(YssD.sub(Math.abs(rs.getDouble("FCuryCost")), rs.getDouble("ftrademoney")), 2);
	        	 }
	        	 transferset.setIInOut(1);
            }                      
            transferset.setDMoney(money);
            transferset.setDBaseRate(dBaseRate);
            transferset.setDPortRate(dPortRate);

            transferset.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子表数据出错！", e);
        }
        return transferset;
    }

    /**
     * 设置调拨数据
     * @param rs ResultSet
     * @param type String 类型
     * @return TransferBean
     * @throws YssException
     */
    private TransferBean setTransferAttr(ResultSet rs, String type) throws
        YssException {
        TransferBean transfer = new TransferBean();//调拨类
        try {
            transfer.setDtTransferDate(rs.getDate("FSettleDate"));//调拨日期
            transfer.setDtTransDate(rs.getDate("FBargainDate"));//业务日期
            if (type.equalsIgnoreCase("QSKHC")) { 
                transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Cost);
                transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_COST);
            }else if(type.equalsIgnoreCase("QSKHR")){
                transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Cost);
                transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_COST);
            }else if(type.equalsIgnoreCase("PCSY")){ //平仓收益            	
            	transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
                transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_SR);
            }else if(type.equalsIgnoreCase("XQSY")){ //行权收益            	
            	transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);
                transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_SR);
            }
            transfer.setFRelaNum(rs.getString("FNum"));//编号
            transfer.setStrTradeNum(rs.getString("FNum"));
            transfer.setFNumType("OptionsTrade");//设置编号类型
            transfer.setStrSecurityCode(rs.getString("FSecurityCode"));//证券代码
            securityCodes += transfer.getStrSecurityCode() + ",";

            transfer.checkStateId = 1;

        } catch (Exception ex) {
            throw new YssException("设置资金调拨数据出错!", ex);
        }
        return transfer;
    }

    
    private double getPCcost (ResultSet rs ) throws  YssException{
    	double cost =0;
    	ResultSet rs1 = null;
    	StringBuffer buff = new StringBuffer();
    	try{
    		buff.append("select * from ").append(pub.yssGetTableName("tb_data_integrated"));
    		buff.append(" where fcheckstate=1 and fnumtype = 'OptionsTrade'  and ftsftypecode='05' and fsubtsftypecode='05FP01'");
    		buff.append(" and ftradetypecode =").append(dbl.sqlString(rs.getString("ftradetypecode")));
    		buff.append(" and fsecuritycode = ").append(dbl.sqlString(rs.getString("fsecuritycode")));
    		buff.append(" and fexchangedate = ").append(dbl.sqlDate(rs.getDate("FBargainDate")));
    		buff.append(" and fportcode = ").append(dbl.sqlString(rs.getString("fportcode")));
    		rs1 = dbl.queryByPreparedStatement(buff.toString());
    		if(rs1.next()){
    			cost = YssD.mul(rs1.getDouble("fexchangecost"), rs1.getInt("FINOUTTYPE"));
    		}
    		return cost;
    	}catch(Exception e ){
    		throw new YssException("获取平仓成本出错......");
    	}finally{
    		dbl.closeResultSetFinal(rs1);
    	}
    }
    
}
