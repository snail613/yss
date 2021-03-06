package com.yss.main.operdeal.businesswork.futures.futuresdistilldata;

import java.sql.*;
import java.util.*;

//---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
import com.yss.commeach.*;
import com.yss.main.cashmanage.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.main.operdeal.businesswork.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CashTransferDistill
    extends BaseBusinWork {
	
//	  modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
/*    private ArrayList cashTransArr = null;
    private String securityCodes = "";
    private String sRelaNums = "";
    private Hashtable htAccountType = null;  */
	
	private String sRelaNums = "";
//--------------------	
    public CashTransferDistill() {
    }
    // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
    // modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
    public String doOperation(String sType) throws YssException {
        /*CtlPubPara pubPara = new CtlPubPara();
        pubPara.setYssPub(pub);
//        htAccountType = pubPara.getFuturesAccountType();
        htAccountType = pubPara.getFurAccountType();//add by xuxuming,20091223.MS00886,无法用不同的方法对不同品种进行核算成本
        cashTransArr = getCashTransferData();
        if (cashTransArr.size() > 0) {
            saveCashTransferData(cashTransArr);
        }
        return "";*/
    	//---add by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
    	boolean contractType = false;
    	ParaWithPort para = new ParaWithPort();
    	para.setYssPub(pub);
    	contractType = para.getFutursPositionType(this.portCodes.replace("'", ""));
    	ArrayList result = null;
    	//---add by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
    	
    	ArrayList cashTransArr = new ArrayList();
    	for(int i=0; i<4; i++) { // i=0时处理股指期货，i=1时处理债券期货, i=2时处理外汇期货  //modify huangqirong 2012-08-21  商品期货
    		CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
            Hashtable htAccountType = pubPara.getFurAccountType(YssOperCons.YSS_FU_ACCOUT_TYPE[i]);
            //---edit by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
            if(!contractType){//若持仓模式为 单边 则不在此生成资金调拨
            	result = getCashTransferData(htAccountType, YssOperCons.YSS_FU[i],
                		YssOperCons.YSS_ZJDBZLX_FU[i][0], YssOperCons.YSS_ZJDBZLX_FU[i][1], 
                		YssOperCons.YSS_ZJDBZLX_FU[i][3], YssOperCons.YSS_ZJDBZLX_FU[i][4], i); //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
            }else{
            	result = getCashTransferDataOne(htAccountType, YssOperCons.YSS_FU[i],
                		YssOperCons.YSS_ZJDBZLX_FU[i][0], YssOperCons.YSS_ZJDBZLX_FU[i][1], 
                		YssOperCons.YSS_ZJDBZLX_FU[i][3], YssOperCons.YSS_ZJDBZLX_FU[i][4], i);
            }
            if(result != null){
			//---edit by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
            	Iterator it=result.iterator(); 
            	while(it.hasNext()){
            		cashTransArr.add(it.next());
            	}
            }
    	}
    	if ( cashTransArr.size() > 0) {
            saveCashTransferData(cashTransArr);
        }
    	return "";
    }

    /**
     * add by songjie 2012.12.07 
     * STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
     * @param htAccountType
     * @param subCatCode
     * @param tsfTypeCode1 02FU
     * @param tsfTypeCode2 05FU
     * @param tsfTypeCode3 09FU
     * @param tsfTypeCode4 19FU
     * @param i
     * @return
     * @throws YssException
     */
    private ArrayList getCashTransferDataOne(Hashtable htAccountType, String subCatCode,
    		String tsfTypeCode1, String tsfTypeCode2, String tsfTypeCode3, 
    		String tsfTypeCode4, int i) throws YssException { 
        ArrayList curCashTransArr = null;
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        ArrayList subtransfer = null;
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer(1000);
        boolean analy1;
        boolean analy2;
        boolean analy3;
        String sqlStr = "";
        SecurityBean security = null ;
        double money = 0;
        double dBaseRate = 0;
        double dPortRate = 0;
        String strSql1 = "";
        try {
        	curCashTransArr = new ArrayList();
        	
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");

            sqlStr = " select a.* from " + pub.yssGetTableName("TB_Data_FutTradeSplit") + " a " + 
            " join (select * from " + pub.yssGetTableName("Tb_Para_Security") + 
            " where FCheckState = 1 and FSubCatCode = " + dbl.sqlString(subCatCode) + ") sec " +
            " on a.FSecurityCode = sec.FSecurityCode " +
            " where a.FCheckState = 1 and a.FPortCode in (" + this.portCodes  + 
            ") and a.FBargainDate = " + dbl.sqlDate(this.getWorkDate());
            rs = dbl.openResultSet(sqlStr);
            while(rs.next()){
                //------------------------生成拆分后的期货交易数据  费用资金调拨 start---------------------------//
            	subtransfer = new ArrayList();
            	transfer = new TransferBean();
                transfer.setDtTransferDate(rs.getDate("FSettleDate"));
                transfer.setDtTransDate(this.getWorkDate());
                transfer.setStrTsfTypeCode("05");
                transfer.setStrSubTsfTypeCode(tsfTypeCode2);
                
                transfer.setFRelaNum(rs.getString("FNum"));
                transfer.setStrTradeNum(rs.getString("FNum"));
                transfer.setStrSecurityCode(rs.getString("FSecurityCode"));
                transfer.checkStateId = 1;

                sRelaNums += transfer.getFRelaNum() + ",";
                
                security = new SecurityBean();
                security.setYssPub(pub);
                security.setSecurityCode(rs.getString("FSecurityCode"));
                security.getSetting();
                dBaseRate = this.getSettingOper().getCuryRate(rs.getDate(
                    "FBARGAINDATE"),
                    security.getTradeCuryCode(), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE);
                EachRateOper eachOper = new EachRateOper();
                eachOper.setYssPub(pub);
                eachOper.getInnerPortRate(rs.getDate("FBarGainDate"),
                                          security.getTradeCuryCode(),
                                          rs.getString("FPortCode"));
                dPortRate = eachOper.getDPortRate();
                
                transferset = new TransferSetBean();
                transferset.setSPortCode(rs.getString("FPortCode"));
                transferset.setSAnalysisCode1(" ");
                transferset.setSAnalysisCode2(" ");
                
                //费用 = 所有费用之和
                money = YssD.add(rs.getDouble("FTradeFee1"), rs.getDouble("FTradeFee2"),
  		              rs.getDouble("FTradeFee3"), rs.getDouble("FTradeFee4"),
  		              rs.getDouble("FTradeFee5"), rs.getDouble("FTradeFee6"),
  		              rs.getDouble("FTradeFee7"), rs.getDouble("FTradeFee8"));
                transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));//变动保证金账户
                transferset.setIInOut( -1); 
                
                transferset.setDMoney(money);
                
                transferset.setDBaseRate(dBaseRate);
                transferset.setDPortRate(dPortRate);

                transferset.checkStateId = 1;
                
                subtransfer.add(transferset);
                transfer.setSubTrans(subtransfer);
                curCashTransArr.add(transfer);
                //------------------------生成拆分后的期货交易数据  费用资金调拨 end---------------------------//
            }
            
            dbl.closeResultSetFinal(rs);
            
            sqlStr = " select a.*,b.FMoney as FBMoney, c.FMoney as FCMoney from " + 
            pub.yssGetTableName("TB_Data_FutTradeSplit") + " a " + 
            " join " + 
            " (select * from " + pub.yssGetTableName("TB_Data_FutTradeRela") + " where FTsfTypeCode = " + dbl.sqlString(tsfTypeCode1) + ") b " + //平仓收益
            " on a.FNum = b.FCloseNum " +
            " join " +
            " (select * from " + pub.yssGetTableName("TB_Data_FutTradeRela") + " where FTsfTypeCode = " + dbl.sqlString(tsfTypeCode4) + ") c " + //卖出估值增值
            " on a.FNum = c.FCloseNum " +
            " join " +
            " (select * from " + pub.yssGetTableName("Tb_Para_Security") + 
            " where FCheckState = 1 and FSubCatCode = " + dbl.sqlString(subCatCode) + ") sec " +
            " on a.FSecurityCode = sec.FSecurityCode " +
            " where a.FCheckState = 1 " +
            " and a.FPortCode in (" + this.portCodes  + 
            ") and a.FBargainDate = " + dbl.sqlDate(this.getWorkDate()) + 
            " and FOperType = '21' ";
            rs = dbl.openResultSet(sqlStr);
            while(rs.next()){
                //------------------------生成拆分后的期货交易数据  收入 资金调拨 start---------------------------//
            	subtransfer = new ArrayList();
            	transfer = new TransferBean();
                transfer.setDtTransferDate(rs.getDate("FSettleDate"));
                transfer.setDtTransDate(this.getWorkDate());
                transfer.setStrTsfTypeCode("02");
                transfer.setStrSubTsfTypeCode(tsfTypeCode1);
                
                transfer.setFRelaNum(rs.getString("FNum"));
                transfer.setStrTradeNum(rs.getString("FNum"));
                transfer.setStrSecurityCode(rs.getString("FSecurityCode"));
                transfer.checkStateId = 1;

                sRelaNums += transfer.getFRelaNum() + ",";
                
                security = new SecurityBean();
                security.setYssPub(pub);
                security.setSecurityCode(rs.getString("FSecurityCode"));
                security.getSetting();
                dBaseRate = this.getSettingOper().getCuryRate(rs.getDate(
                    "FBARGAINDATE"),
                    security.getTradeCuryCode(), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE);
                EachRateOper eachOper = new EachRateOper();
                eachOper.setYssPub(pub);
                eachOper.getInnerPortRate(rs.getDate("FBarGainDate"),
                                          security.getTradeCuryCode(),
                                          rs.getString("FPortCode"));
                dPortRate = eachOper.getDPortRate();
                
                transferset = new TransferSetBean();
                transferset.setSPortCode(rs.getString("FPortCode"));
                transferset.setSAnalysisCode1(" ");
                transferset.setSAnalysisCode2(" ");
                
            	//收入 = 平仓收益 - 卖出估值增值；
            	money = rs.getDouble("FBMoney") - rs.getDouble("FCMoney");
            	
                transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));//变动保证金账户
                
                transferset.setIInOut( 1); 
                transferset.setDMoney(money);
                transferset.setDBaseRate(dBaseRate);
                transferset.setDPortRate(dPortRate);
                transferset.checkStateId = 1;
                subtransfer.add(transferset);
                transfer.setSubTrans(subtransfer);
                curCashTransArr.add(transfer);
              //------------------------生成拆分后的期货交易数据  收入  资金调拨 end---------------------------//
            }
            
            dbl.closeResultSetFinal(rs);
            
            HashMap hmBroker = new HashMap();
            FuturesTradeBean ftb = null;
            //在期权期货保证金账户设置中 获取券商对应的初始保证金账户 和 变动保证金账户 数据 
            sqlStr = " select * from " + pub.yssGetTableName("Tb_data_optionsvalcal") + " where FCheckState = 1 ";
            rs = dbl.openResultSet(sqlStr);
            while(rs.next()){
            	ftb = new FuturesTradeBean();
            	ftb.setBrokerCode(rs.getString("FBrokerCode"));//券商
            	ftb.setBegBailAcctCode(rs.getString("FCashAccCode"));//变动保证金账户
            	ftb.setChageBailAcctCode(rs.getString("FSTARTCASHACCCODE"));//初始保证金账户
            	
            	hmBroker.put(rs.getString("FBrokerCode"), ftb);
            }
            
            dbl.closeResultSetFinal(rs);
            
            FutureBailChangeBean fbcb = null;
            HashMap hmChange = new HashMap();
            
            //获取期货保证金调整数据 的保证金比例 和固定保证金
            sqlStr = " select a.* from " + pub.yssGetTableName("TB_DATA_FutureBailChange") + " a " +
            " join " +
            " (select max(FChangeDate) as FChangeDate, FBrokerCode, FSecurityCode, FPORTCODE, FINVMGRCODE " + 
            " from " + pub.yssGetTableName("TB_DATA_FutureBailChange") + 
            " where FCheckState = 1 and FChangeDate <= " + dbl.sqlDate(this.getWorkDate()) +
            " group by FBrokerCode, FSecurityCode, FPORTCODE, FINVMGRCODE) b" +
            " on a.FBrokerCode = b.FBrokerCode and a.FSecurityCode = b.FSecurityCode " + 
            " and a.FPORTCODE = b.FPORTCODE and a.FINVMGRCODE = b.FINVMGRCODE " +
            " where a.FCheckState = 1 and a.FPortCode = " + this.portCodes;
            rs = dbl.openResultSet(sqlStr);
            while(rs.next()){
            	fbcb = new FutureBailChangeBean();
            	fbcb.setSSecurityCode(rs.getString("FSecurityCode"));
            	fbcb.setSBrokerCode(rs.getString("FBrokerCode"));
            	fbcb.setSPortCode(rs.getString("FPortCode"));
            	fbcb.setBailFix(rs.getDouble("FBAILFIX"));
            	fbcb.setBailScale(rs.getDouble("FBAILSCALE"));
            	
            	hmChange.put(rs.getString("FSecurityCode") + "\t" + rs.getString("FBrokerCode"), fbcb);
            }
            
            dbl.closeResultSetFinal(rs);
            
        	sqlStr = " delete from (select a.* from " + pub.yssGetTableName("Tb_Cash_Subtransfer") + 
        	" a join (select d.* from " + pub.yssGetTableName("Tb_Cash_transfer") + 
        	" d where d.FTransDate = " + dbl.sqlDate(this.getWorkDate()) +
        	" and d.FTsfTypeCode = '01' and d.FSubTsfTypeCode = '0001' and exists " +
        	" (select c.* from " + pub.yssGetTableName("Tb_Data_FutTradeRela") + 
        	" c where c.FTransDate = " + dbl.sqlDate(this.getWorkDate()) +
        	" and c.FPortCode in (" + this.portCodes + ") and c.FCloseNum = ' ' and c.FTsfTypeCode = " + 
        	dbl.sqlString(tsfTypeCode2) + " and c.FNum = d.FSecurityCode)) b on a.FNum = b.Fnum) e ";
        	
        	dbl.executeSql(sqlStr);
        	
        	sqlStr = " delete from " + pub.yssGetTableName("Tb_Cash_transfer") + 
        	" d where d.FTransDate = " + dbl.sqlDate(this.getWorkDate()) +
        	" and d.FTsfTypeCode = '01' and d.FSubTsfTypeCode = '0001' and exists (select c.* from " + 
        	pub.yssGetTableName("Tb_Data_FutTradeRela") + 
        	" c where c.FTransDate = " + dbl.sqlDate(this.getWorkDate()) + 
        	" and c.FPortCode in (" + this.portCodes + 
        	") and c.FCloseNum = ' ' and c.FTsfTypeCode = " + dbl.sqlString(tsfTypeCode2) + 
        	" and c.FNum = d.FSecurityCode)";
        	
        	dbl.executeSql(sqlStr);
            
            TransferSetBean transferset2 = null;
            sqlStr = " select a.*,b.FBailType,b.FBailScale, b.FBailFix, c.FBailMoney as yesBailMoney from " + 
            	     pub.yssGetTableName("TB_Data_FutTradeRela") + " a " + 
                     " join " + 
                     " (select * from " + pub.yssGetTableName("Tb_Para_IndexFutures") + " where FCheckState = 1) b " +
                     " on a.FNum = b.FSecurityCode " +
                     " join " +
                     " (select * from " + pub.yssGetTableName("Tb_Para_Security") + 
                     " where FCheckState = 1 and FSubCatCode = " + dbl.sqlString(subCatCode) + ") sec " +
                     " on a.FNum = sec.FSecurityCode " + 
                     " left join " +
                     " (select * from " + pub.yssGetTableName("TB_Data_FutTradeRela") + 
                     " where FTransDate = " + dbl.sqlDate(YssFun.addDay(this.getWorkDate(), -1)) + 
                     " and FTsfTypeCode = " + dbl.sqlString(tsfTypeCode2) + " and FCloseNum = ' ') c " +
                     " on a.FNum = c.FNum and a.FBrokerCode = c.FBrokerCode and a.FPortCode = c.FPortCode "+
                     " where a.FTransDate = " + dbl.sqlDate(this.getWorkDate()) +
                     " and a.FPortCode in (" + this.portCodes  + ") " +
                     " and a.FCloseNum = ' ' " +
                     " and a.FTsfTypeCode = " + dbl.sqlString(tsfTypeCode2);
            rs = dbl.openResultSet(sqlStr);
            while(rs.next()){
            	//------------------------生成期货交易数据库存对应的明细到券商的保证金  资金调拨 end---------------------------//
            	if(rs.getString("FBailType").equals("Fix")){//固定保证金
            		//保证金 = 当日库存数量（取绝对值） * 每手固定保证金金额
            		//优先获取期货保证金调整数据 的保证金比例 和固定保证金  如果取不到 则获取 期货信息设置中的保证金比例 和固定保证金
            		if(hmChange.get(rs.getString("FNum") + "\t" + rs.getString("FBrokerCode")) != null){
            			fbcb = (FutureBailChangeBean)hmChange.get(rs.getString("FNum") + "\t" + rs.getString("FBrokerCode"));
            			money = YssD.mul(Math.abs(rs.getDouble("FStorageAmount")), fbcb.getBailFix());
            		}else{
            			money = YssD.mul(Math.abs(rs.getDouble("FStorageAmount")), rs.getDouble("FBailFix"));
            		}
            	}else{
            		//保证金 = 当日库存成本（取绝对值） * 保证金比例
            		//优先获取期货保证金调整数据 的保证金比例 和固定保证金  如果取不到 则获取 期货信息设置中的保证金比例 和固定保证金
            		if(hmChange.get(rs.getString("FNum") + "\t" + rs.getString("FBrokerCode")) != null){
            			fbcb = (FutureBailChangeBean)hmChange.get(rs.getString("FNum") + "\t" + rs.getString("FBrokerCode"));
            			money = YssD.round(YssD.mul(Math.abs(rs.getDouble("FMoney")), fbcb.getBailScale()), 2);
            		}else{
            			money = YssD.round(YssD.mul(Math.abs(rs.getDouble("FMoney")),rs.getDouble("FBailScale")), 2);
            		}
            	}
            	
                strSql1 = " update " + pub.yssGetTableName("TB_Data_FutTradeRela") + 
                " set FBailMoney = " + money + " where FPortCode = " + this.portCodes + 
                " and FBrokerCode = " + dbl.sqlString(rs.getString("FBrokerCode")) + 
                " and FCloseNum = ' ' and FTransDate = " + dbl.sqlDate(this.getWorkDate()) + 
                " and FTsfTypeCode = " + dbl.sqlString(tsfTypeCode2);//保存保证金金额
                
                dbl.executeSql(strSql1);
            	
            	subtransfer = new ArrayList();
            	transfer = new TransferBean();
            	
                transfer.setDtTransferDate(rs.getDate("FTransDate"));
                transfer.setDtTransDate(this.getWorkDate());
                transfer.setStrTsfTypeCode("01");
                transfer.setStrSubTsfTypeCode("0001");
                
                transfer.setStrSecurityCode(rs.getString("FNum"));
                transfer.checkStateId = 1;
                
                security = new SecurityBean();
                security.setYssPub(pub);
                security.setSecurityCode(rs.getString("FNum"));
                security.getSetting();
                dBaseRate = this.getSettingOper().getCuryRate(rs.getDate(
                    "FTransDate"),
                    security.getTradeCuryCode(), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE);
                EachRateOper eachOper = new EachRateOper();
                eachOper.setYssPub(pub);
                eachOper.getInnerPortRate(rs.getDate("FTransDate"),
                                          security.getTradeCuryCode(),
                                          rs.getString("FPortCode"));
                dPortRate = eachOper.getDPortRate();
                
                transferset = new TransferSetBean();
                transferset.setSPortCode(rs.getString("FPortCode"));
                transferset.setSAnalysisCode1(" ");
                transferset.setSAnalysisCode2(" ");
                
                ftb = (FuturesTradeBean)hmBroker.get(rs.getString("FBrokerCode"));
                
                transferset.setSCashAccCode(ftb.getChageBailAcctCode());//变动保证金账户
                transferset.setIInOut(-1); 
                
                transferset.setDMoney(YssD.sub(money, rs.getDouble("yesBailMoney")));//当日保证金发生额 = 当日保证金余额 - 昨日保证金余额
                transferset.setDBaseRate(dBaseRate);
                transferset.setDPortRate(dPortRate);

                transferset.checkStateId = 1;

                subtransfer.add(transferset);
                
                transferset2 =  (TransferSetBean)transferset.clone();
                transferset2.setIInOut(1); 
                transferset.setSCashAccCode(ftb.getBegBailAcctCode());//初始保证金账户
                
                subtransfer.add(transferset2);
                
                transfer.setSubTrans(subtransfer);
                curCashTransArr.add(transfer);
                //------------------------生成期货交易数据库存对应的明细到券商的保证金  资金调拨 end---------------------------//
            }   
            return curCashTransArr;
        } catch (Exception e) {
            throw new YssException("获取资金调拨数据出错！", e);
        } finally{
        	dbl.closeResultSetFinal(rs);
        }
    }
    
    private ArrayList getCashTransferData(Hashtable htAccountType, String subCatCode,
    		String tsfTypeCode1, String tsfTypeCode2, String tsfTypeCode3, 
    		String tsfTypeCode4, int i) throws YssException { 
        ArrayList curCashTransArr = null;
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        ArrayList subtransfer = null;
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer(1000);
        boolean analy1;
        boolean analy2;
        boolean analy3;
        //-------------------------------------------------------
        String sqlStr = "";
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            buf.append(" select a.* from ( "); // add by fangjiang 2010.09.02  MS01439 QDV4博时2010年7月14日02_A 
            buf.append("select trade.*,");
            buf.append(" appraise.FNum as AFNum,");
            buf.append(" appraise.FTransDate as AFTransDate,");
            buf.append(" appraise.FBaseCuryRate as FBaseCuryRate,");
            buf.append(" appraise.FPortCuryRate as FPortCuryRate,");
            buf.append(" appraise.YFMoney as YFMoney,");
            buf.append(" appraise.YFBaseCuryMoney as YFBaseCuryMoney,");
            buf.append(" appraise.YFPortCuryMoney as YFPortCuryMoney,");
            buf.append(" (case when appraise.FMoney is null then  0");
            buf.append(" else appraise.FMoney end) as AFMoney, ");
            buf.append(" (case when appraise.FBaseCuryMoney is null then 0");
            buf.append(" else appraise.FBaseCuryMoney end) as AFBaseCuryMoney,");
            buf.append(" (case when appraise.FPortCuryMoney is null then 0");
            buf.append(" else appraise.FPortCuryMoney  end) as AFPortCuryMoney,");

            buf.append(" appraise.FTsfTypeCode as FTsfTypeCode,");
            buf.append(" appraise.SFMoney as SFMoney, ");
            buf.append(" appraise.SFBaseCuryMoney as SFBaseCuryMoney,");
            buf.append(" appraise.SFPortCuryMoney as SFPortCuryMoney,");
            buf.append(" appraise.SFTsfTypeCode as SFTsfTypeCode,");

            buf.append(" rec.FMoney as RFMoney,");
            buf.append(" rec.FBaseCuryMoney as RFBaseCuryMoney,");
            buf.append(" rec.FPortCuryMoney as RFPortCuryMoney,");
            buf.append(" rec.FTsfTypeCode as RFTsfTypeCode");

            buf.append(" from (select FNum,FTradeAmount,");
            buf.append(" FSecurityCode,FPortCode,FBrokerCode, FInvMgrCode,FTradeTypeCode,FBegBailAcctCode,FChageBailAcctCode,");
            buf.append(" FBargainDate,FSettleDate,FBaseCuryRate AS FTRDBaseCuryRate, FPortCuryRate AS FTRDPortCuryRate,");
            buf.append(" (case when FSettleMoney is null then 0 ");
            buf.append(" else FSettleMoney end) as FSettleMoney,");
            buf.append(" (case when FBegBailMoney is null then 0");
            buf.append(" else FBegBailMoney end) as FBegBailMoney,");
            buf.append(
                " (case when FTradeFee1 is null then 0 else FTradeFee1 end) as FTradeFee1,");
            buf.append(
                " (case when FTradeFee2 is null then 0 else FTradeFee2 end) as FTradeFee2,");
            buf.append(
                " (case when FTradeFee3 is null then 0 else FTradeFee3 end) as FTradeFee3,");
            buf.append(
                " (case when FTradeFee4 is null then 0 else FTradeFee4 end) as FTradeFee4,");
            buf.append(
                " (case when FTradeFee5 is null then 0 else FTradeFee5 end) as FTradeFee5,");
            buf.append(
                " (case when FTradeFee6 is null then 0 else FTradeFee6 end) as FTradeFee6,");
            buf.append(
                " (case when FTradeFee7 is null then 0 else FTradeFee7 end) as FTradeFee7,");
            buf.append(
                " (case when FTradeFee8 is null then 0 else FTradeFee8 end) as FTradeFee8");
            buf.append(" from ");
            //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
            buf.append(pub.yssGetTableName("TB_Data_FuturesTrade_Tmp")); //交易的数据。
            buf.append("  where FCheckState = 1 ");
            buf.append(" and FPortCode in (");
            buf.append(this.portCodes);
            buf.append(") and FBargainDate = ");
            buf.append(dbl.sqlDate(this.getWorkDate()));
            buf.append(") Trade ");
            buf.append(" full join (select today.*, yestoday.FMoney as YFMoney,yestoday.FBaseCuryMoney as YFBaseCuryMoney,yestoday.FPortCuryMoney as YFPortCuryMoney");

            buf.append(" from (select cur.*, sub.FTsfTypeCode as SFTsfTypeCode,sub.FMoney as SFMoney,sub.FBaseCuryMoney as SFBaseCuryMoney,sub.FPortCuryMoney as SFPortCuryMoney");

            buf.append(" from (select FNum,FTransDate,FTsfTypeCode");

            buf.append(
                ",(case when FMoney is null then 0 else FMoney end) as FMoney");
            buf.append(",(case when FBaseCuryMoney is null then 0 else FBaseCuryMoney end) as FBaseCuryMoney");
            buf.append(",(case when FPortCuryMoney is null then 0 else FPortCuryMoney end) as FPortCuryMoney");

            buf.append(",FBaseCuryRate,FPortCuryRate, FBrokerCode from ");
            buf.append(pub.yssGetTableName("TB_Data_FutTradeRela"));
            buf.append(" where FTransDate = ");
            buf.append(dbl.sqlDate(this.getWorkDate()));
            buf.append(" and FTsfTypeCode = ");
            // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
            buf.append(dbl.sqlString(tsfTypeCode3));
            buf.append(" ) cur ");
            //----------------------
            buf.append(" left join ");

            buf.append(" (select FNum,FTransDate,FTsfTypeCode");

            buf.append(
                ",(case when FMoney is null then 0 else FMoney end) as FMoney");
            buf.append(",(case when FBaseCuryMoney is null then 0 else FBaseCuryMoney end) as FBaseCuryMoney");
            buf.append(",(case when FPortCuryMoney is null then 0 else FPortCuryMoney end) as FPortCuryMoney");

            buf.append(",FBaseCuryRate,FPortCuryRate,FBrokerCode from ");
            buf.append(pub.yssGetTableName("TB_Data_FutTradeRela"));
            buf.append(" where FTransDate = ");
            buf.append(dbl.sqlDate(this.getWorkDate()));
            buf.append(" and FTsfTypeCode =  ");
            // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
            buf.append(dbl.sqlString(tsfTypeCode4));
            buf.append(" ) sub ");
            //----------------------
            buf.append(
                " on cur.FNum = sub.FNum and cur.FTransDate = sub.FTransDate and cur.FBrokerCode = sub.FBrokerCode");

            buf.append(") today"); //今日的应收应付数据。

            buf.append(" left join (select FNum,FTransDate,FTsfTypeCode,");
            buf.append(
                " (case when FMoney is null then  0  else -1 * FMoney end) as FMoney, ");
            buf.append(" (case when FBaseCuryMoney is null then 0 else  -1 * FBaseCuryMoney end) as FBaseCuryMoney,");
            buf.append(" (case when FPortCuryMoney is null then 0 else  -1 * FPortCuryMoney end) as FPortCuryMoney, FBrokerCode from ");
            buf.append(pub.yssGetTableName("TB_Data_FutTradeRela"));
            buf.append(" where FTransDate = ");
            buf.append(dbl.sqlDate(YssFun.addDay(this.getWorkDate(), -1)));
            buf.append(
                " and FTsfTypeCode in ( '09','02')) yestoday on today.FNum =");
            buf.append(" yestoday.FNum) appraise on Trade.FNum = appraise.FNum and Trade.FBrokerCode = appraise.FBrokerCode"); //昨日的应收应付数据。

            buf.append("  left join (select FNum,FTransDate, FTsfTypeCode, ");
            buf.append(
                " (case when FMoney is null then 0 else FMoney end) as FMoney,");
            buf.append(" (case when FBaseCuryMoney is null then 0 else FBaseCuryMoney end) as FBaseCuryMoney,");
            buf.append(" (case when FPortCuryMoney is null then 0 else FPortCuryMoney end) as FPortCuryMoney,");
            buf.append(" FBaseCuryRate,");
            buf.append(" FPortCuryRate, FBrokerCode");
            buf.append(" from ");
            buf.append(pub.yssGetTableName("Tb_Data_FutTradeRela"));
            buf.append(" where FTransDate =  ");
            buf.append(dbl.sqlDate(this.getWorkDate()));
            buf.append(" and FTsfTypeCode = ");
            // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
            buf.append(dbl.sqlString(tsfTypeCode1));
            buf.append(" ) rec on Trade.FNum = rec.FNum and Trade.FBrokerCode = rec.FBrokerCode");
            //----------------------
            //add by fangjiang 2010.09.02 MS01439 QDV4博时2010年7月14日02_A 
            buf.append(" )a join (select fsecuritycode from ");
            buf.append(pub.yssGetTableName("TB_Para_Indexfutures"));
            buf.append(" where FCheckState = 1 and fsubcatcode = "); 
            buf.append(dbl.sqlString(subCatCode));
            buf.append(" ) b on a.fsecuritycode = b.fsecuritycode ");
            //-------------------------------
            sqlStr = buf.toString();
            //rs = dbl.queryByPreparedStatement(sqlStr,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788 9i下解析有问题
            rs = dbl.openResultSet_antReadonly(sqlStr); //modify by fangjiang 2011.12.09 bug 3342
        } catch (Exception e) {
            throw new YssException("获取资金调拨数据出错！", e);
        }
        curCashTransArr = new ArrayList();
        try {
            while (rs.next()) {
//            subtransfer = new ArrayList();
                if (rs.getString("FTradeTypeCode") != null &&
                    rs.getString("FTradeTypeCode").equalsIgnoreCase("21")) {
                    //---------------------------------------------------------------
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "Fee", tsfTypeCode1, 
                    		tsfTypeCode2, tsfTypeCode3); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A  平仓 成本=费用 sunkey 20081126 BugID:MS00013
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "PCIN", htAccountType, subCatCode, tsfTypeCode1, 
                        tsfTypeCode3, tsfTypeCode4, i); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A  modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);
                    //---------------------------------------------------------------
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "IN", tsfTypeCode1,
                    		tsfTypeCode2, tsfTypeCode3); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "PCBailIn", htAccountType, subCatCode, tsfTypeCode1, 
                        tsfTypeCode3, tsfTypeCode4, i); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A  modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
                    subtransfer.add(transferset);
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "PCBailOut", htAccountType, subCatCode, tsfTypeCode1, 
                        tsfTypeCode3, tsfTypeCode4, i); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A  modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);
                    //-------------------------平仓收益 sunkey BugID:MS00013------------------------
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "02", tsfTypeCode1,
                    		tsfTypeCode2, tsfTypeCode3); //收入 modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "PCINCOME", htAccountType, subCatCode, tsfTypeCode1, 
                        tsfTypeCode3, tsfTypeCode4, i); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A  modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);
                } else if (rs.getString("FTradeTypeCode") != null &&
                           rs.getString("FTradeTypeCode").equalsIgnoreCase("20")) {
                    //---------------------------------------------------------------
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "IN", tsfTypeCode1,
                    		tsfTypeCode2, tsfTypeCode3); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "KC", htAccountType, subCatCode, tsfTypeCode1, 
                        tsfTypeCode3, tsfTypeCode4, i); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A  modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
                    subtransfer.add(transferset);
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "KCWITHFEE", htAccountType, subCatCode, tsfTypeCode1, 
                        tsfTypeCode3, tsfTypeCode4, i); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A  modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);

                    //Fee------------------------------------------------------------
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "Fee", tsfTypeCode1,
                    		tsfTypeCode2, tsfTypeCode3); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "KCFee", htAccountType, subCatCode, tsfTypeCode1, 
                        tsfTypeCode3, tsfTypeCode4, i); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A  modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);
                }
            }
        } catch (Exception ex) {
            throw new YssException("设置资金调拨数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return curCashTransArr;
    }

    private void saveCashTransferData(ArrayList cashTransData) throws
        YssException {
        CashTransAdmin cashtrans = null;
        String filtersRelaNums = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            if (cashTransData.size() > 0) {
                cashtrans = new CashTransAdmin();
                cashtrans.setYssPub(pub);
                cashtrans.addList(cashTransData);
                if (sRelaNums.length() > 0 && sRelaNums.endsWith(",")) {
                    filtersRelaNums = this.sRelaNums.substring(0,
                        sRelaNums.length() - 1);
                }
                //2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
                //增加事务控制和锁，以免在多用户同时处理时出现调拨编号重复
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer"));
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));
                cashtrans.insert(this.getWorkDate(), filtersRelaNums);
                conn.commit();
                conn.setAutoCommit(true);
                bTrans = false;
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private TransferBean setTransferAttr(ResultSet rs, String type,
    		String tsfTypeCode1, String tsfTypeCode2, String tsfTypeCode3) throws
        YssException {
        TransferBean transfer = new TransferBean();
        java.util.Date tmpDate = null;
        try {
            tmpDate = rs.getDate("AFTransDate") == null ?
                rs.getDate("FBARGAINDATE") :
                rs.getDate("AFTransDate");
            transfer.setDtTransferDate(rs.getDate("FSettleDate"));
            transfer.setDtTransDate(tmpDate);
            if (rs.getString("FNum") == null || type.equalsIgnoreCase("09")) { //估值时的调拨类型设置
                transfer.setStrTsfTypeCode("09");
                transfer.setStrSubTsfTypeCode(tsfTypeCode3); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A
            } else if (type.equalsIgnoreCase("Fee")) { //费用的调拨类型设置。
                transfer.setStrTsfTypeCode("05");
                transfer.setStrSubTsfTypeCode(tsfTypeCode2); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A
            } else if (type.equalsIgnoreCase("IN")) { //帐户间的内部流动设置。
                transfer.setStrTsfTypeCode("01");
                transfer.setStrSubTsfTypeCode("0001");
            } else if (type.equalsIgnoreCase("02")) { //收入的调拨类型设置。
                transfer.setStrTsfTypeCode("02");
                transfer.setStrSubTsfTypeCode(tsfTypeCode1); //modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A
            }
            transfer.setFRelaNum(rs.getString("FNum") == null ?
                                 rs.getString("AFNum") : rs.getString("FNum"));
            //插入交易编号 这样可以在前台显示 sunkey 20081126 BugID:MS00013
            transfer.setStrTradeNum(rs.getString("FNum") == null ?
                                    rs.getString("AFNum") : rs.getString("FNum"));
            transfer.setStrSecurityCode(rs.getString("FSecurityCode"));
//            securityCodes += transfer.getStrSecurityCode() + ",";   modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
            transfer.checkStateId = 1;

            sRelaNums += transfer.getFRelaNum() + ",";
        } catch (SQLException ex) {
            throw new YssException("设置资金调拨数据出错!", ex);
        }
        return transfer;
    }

    /**
     * 获取交易的相关信息。
     * @param FNum String
     * @return TransferSetBean
     * @throws YssException
     */
    private TransferSetBean getTransfer(String FNum, String type, String subCatCode) throws
        YssException {
        TransferSetBean transfer = null;
        ResultSet rs = null;
        // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
        String sqlStr = "select a.* from ( selec * from " +
            //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
            pub.yssGetTableName("Tb_Data_FuturesTrade_Tmp") +
            " where FCheckState = 1 and FNum = " + dbl.sqlString(FNum) +
            " ) a join ( select * from " +
            pub.yssGetTableName("Tb_Para_IndexFutures") +
            " where fcheckstate = 1 and FSubCatCode = " +
            dbl.sqlString(subCatCode) +
            " ) b on a.FSecurityCode = b.FSecurityCode";
        //----------------------
        try {
            rs = dbl.queryByPreparedStatement(sqlStr,ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                transfer = new TransferSetBean();
                transfer.setSPortCode(rs.getString("FPortCode"));
                transfer.setSAnalysisCode1(rs.getString("FInvMgrCode"));
                transfer.setSAnalysisCode2(rs.getString("FU")); //修改为证券类型 FU期货 FU01股指期货 sunkey 20081127 BugID:MS00013
                transfer.setSAnalysisCode3(" ");
                if (type.equalsIgnoreCase("09In")) {
                    transfer.setSCashAccCode(rs.getString("FBegBailAcctCode"));
                } else if (type.equalsIgnoreCase("09Out")) {
                    transfer.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                }
            }
        } catch (Exception e) {
            throw new YssException("获取现金帐户出错！", e);
        }
        return transfer;
    }

    private TransferSetBean setTransferSetAttr(ResultSet rs, boolean analy1,
        boolean analy2, boolean analy3, String type, Hashtable htAccountType,
        String subCatCode, String tsfTypeCode1, String tsfTypeCode2, String tsfTypeCode3, int i   //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求 
		) throws YssException {
        double dBaseRate = 1;
        double dPortRate = 1;
        double money = 0.0;
        TransferSetBean transferset = new TransferSetBean();
        TransferSetBean tmptransfer = null;
        SecurityBean security = null;
        EachRateOper eachOper = null; //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
        try {
            dBaseRate = rs.getDouble("FBaseCuryRate");
            dPortRate = rs.getDouble("FPortCuryRate");
            if (rs.getString("FNum") == null) { //没有交易
                tmptransfer = getTransfer(rs.getString("AFNum"), type, subCatCode); // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
                transferset.setSPortCode(tmptransfer.getSPortCode());
                if (analy1) {
                    transferset.setSAnalysisCode1(tmptransfer.getSAnalysisCode1());
                } else {
                    transferset.setSAnalysisCode1(" ");
                }
                if (analy2) {
                    transferset.setSAnalysisCode2(tmptransfer.getSAnalysisCode2());
                } else {
                    transferset.setSAnalysisCode2(" ");
                }
                transferset.setSCashAccCode(tmptransfer.getSCashAccCode());

                money = rs.getDouble("AFMoney");
                money = YssD.add(money, rs.getDouble("YFMoney"));

                if (type.equalsIgnoreCase("09Out")) { //估值的流出设置。
                    transferset.setIInOut( -1);
                } else if (type.equalsIgnoreCase("09IN")) { //估值的流入设置。
                    transferset.setIInOut(1);
                }
            } else { //有交易
                security = new SecurityBean();
                security.setYssPub(pub);
                security.setSecurityCode(rs.getString("FSecurityCode"));
                security.getSetting();
                dBaseRate = this.getSettingOper().getCuryRate(rs.getDate(
                    "FBARGAINDATE"),
                    security.getTradeCuryCode(), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE);
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
                eachOper = new EachRateOper();
                eachOper.setYssPub(pub);
                eachOper.getInnerPortRate(rs.getDate("FBarGainDate"),
                                          security.getTradeCuryCode(),
                                          rs.getString("FPortCode"));
                dPortRate = eachOper.getDPortRate();
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
                transferset.setSPortCode(rs.getString("FPortCode"));
                if (analy1) {
                    transferset.setSAnalysisCode1(rs.getString("FInvMgrCode"));
                } else {
                    transferset.setSAnalysisCode1(" ");
                }
                if (analy2) {
                    //将分析代码2改为类型--期货 sunkey 20081127
                    transferset.setSAnalysisCode2("FU"); //xuqiji 2009 0422 ---------------------------
                } else {
                    transferset.setSAnalysisCode2(" ");
                }
                if (type.equalsIgnoreCase("09")) { //估值的流出设置。
                    transferset.setSCashAccCode(rs.getString("FBegBailAcctCode"));
                    money = rs.getDouble("AFMoney");
                    money = YssD.add(money, rs.getDouble("YFMoney"));
                    transferset.setIInOut( -1);
                } else if (type.equalsIgnoreCase("09IN")) { //估值的流入设置。
                    transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                    money = rs.getDouble("AFMoney");
                    money = YssD.add(money, rs.getDouble("YFMoney"));
                    transferset.setIInOut(1);
                } else if (type.equalsIgnoreCase("PCBailIN")) { //平仓保证金流入
                    transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                    money = rs.getDouble("FBegBailMoney"); //FBegBailMoney不包括费用。
                    transferset.setIInOut(1);
                } else if (type.equalsIgnoreCase("PCBailOut")) { //平仓保证金流出
                    transferset.setSCashAccCode(rs.getString("FBegBailAcctCode"));
                    money = rs.getDouble("FBegBailMoney"); //FBegBailMoney不包括费用。
                    transferset.setIInOut( -1);
                } else if (type.equalsIgnoreCase("PC")) { //平仓的设置，流出。
                    transferset.setSCashAccCode(rs.getString("FBegBailAcctCode"));
                    money = rs.getDouble("FSettleMoney"); //FSettleMoney除去了费用。

                    money = YssD.add(money, rs.getDouble("RFMoney")); //加上当天的收益.
                    transferset.setIInOut( -1);
                } else if (type.equalsIgnoreCase("PCIN")) { //平仓的设置，流入。 取清算款 ，原来是代表平仓收益，现在是费用的总计，即成本 sunkey
                    transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                    money = rs.getDouble("FSettleMoney"); //FSettleMoney除去了费用。
                    transferset.setIInOut( -1); //由流入变成流出 由平仓收益变为成本=费用 sunkey 20081126
                } else if (type.equalsIgnoreCase("PCWITHFEE")) { //平仓的收入设置，流出。
                    transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                    money = YssD.sub(rs.getDouble("AFMoney"),
                                     calFees(rs));
                    transferset.setIInOut( -1);
                } else if (type.equalsIgnoreCase("PCWITHFEEIN")) { //平仓的收入设置，流入。
                    transferset.setSCashAccCode(rs.getString("FBegBailAcctCode"));
                    money = YssD.sub(rs.getDouble("AFMoney"),
                                     calFees(rs));
                    transferset.setIInOut(1);
                }
                //平仓收益 ，资金流入 sunkey 20081121 BugID:MS00013
                else if (type.equalsIgnoreCase("PCINCOME")) {
                    transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                    money = this.closeIncome(rs.getString("FNum"), rs.getDouble("FTradeAmount"),
                        rs.getString("FSECURITYCODE"), rs.getString("FPORTCODE"),
                        rs.getDate("FBargainDate"), htAccountType, subCatCode, tsfTypeCode1,
                        tsfTypeCode2, tsfTypeCode3, i); // modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A  modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
                    money=YssD.round(money,2);//xuqiji 20090804:QDV4中金2009年7月28日05_B  MS00607 期货变动保证金组合货币成本不等于组合货币市值
                    transferset.setSDesc("股指期货平仓收益");
                    transferset.setIInOut(1);
                } else if (type.equalsIgnoreCase("KC")) { //开仓的设置，流出。
                    transferset.setSCashAccCode(rs.getString("FChageBailAcctCode")); //变动保证金，流出
                    money = rs.getDouble("FBegBailMoney"); //FBegBailMoney不包括费用。
                    transferset.setIInOut( -1);
                } else if (type.equalsIgnoreCase("KCWITHFEE")) { //开仓的设置，流入。
                    transferset.setSCashAccCode(rs.getString("FBegBailAcctCode")); //初始保证金，流入
                    money = rs.getDouble("FBegBailMoney"); //FBegBailMoney不包括费用。
                    transferset.setIInOut(1);
                } else if (type.equalsIgnoreCase("KCFee")) { //开仓的设置，流出。
                    transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                    money = calFees(rs);
                    //2008.09.27 蒋锦 修改 成本的汇率使用交易中的汇率
                    dBaseRate = rs.getDouble("FTRDBaseCuryRate");
                    dPortRate = rs.getDouble("FTRDPortCuryRate");
                    transferset.setIInOut( -1);
                }
            }
            transferset.setDMoney(money);
        } catch (Exception e) {
            throw new YssException("设置资金调拨子表数据出错！", e);
        }
        transferset.setDBaseRate(dBaseRate);
        transferset.setDPortRate(dPortRate);

        transferset.checkStateId = 1;

        return transferset;
    }

    private double calFees(ResultSet rs) throws YssException {
        double fees = 0.0;
        try {
            //-------2009.03.29 招商基金 蒋锦 修改 --------//
            //MS00346 《QDV4招商证券2009年03月28日01_B》
            //没有开仓费用的 BUG，开仓费用不从费用中取数，直接取清算款
            fees = rs.getDouble("FSettleMoney");
            //-------------------------------------------//
        } catch (Exception e) {
            throw new YssException("计算费用出错！", e);
        }
        return fees;
    }

    /**
     * 通过证券代码获取期货昨日库存数量
     * 2009-03-19 蒋锦 添加
     * @param dWorkDay Date：业务日期
     * @param sSecurityCode String：证券代码
     * @return double：库存数量
     * @throws YssException
     */
    private double getYesterdayStorageAmountBy(java.util.Date dWorkDay,
                                               String sSecurityCode,
                                               String sPortCode,
                                               String tsfTypeCode) throws YssException {
        double dbStorageAmount = 0;
        ResultSet rs = null;
        StringBuffer sqlBuf = new StringBuffer();
        try {
            sqlBuf.append(" SELECT * ");
            sqlBuf.append(" FROM (SELECT SUM(a.Fstorageamount) AS FStorageAmount ");
            sqlBuf.append(" FROM ").append(pub.yssGetTableName("TB_DATA_FUTTRADERELA")).append(" a ");
            //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
            sqlBuf.append(" JOIN ").append(pub.yssGetTableName("Tb_Data_FuturesTrade_Tmp")).append(" b ON a.FNum = b.FNum ");
            sqlBuf.append(" JOIN ").append(pub.yssGetTableName("Tb_Para_Indexfutures")).append(" c ON b.Fsecuritycode = ");
            sqlBuf.append(" c.FSecurityCode ");
            sqlBuf.append(" WHERE c.Fsecuritycode = ").append(dbl.sqlString(sSecurityCode));
            sqlBuf.append(" AND a.ftsftypecode = ").append(dbl.sqlString(tsfTypeCode));// modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
            sqlBuf.append(" AND a.FSettleState = 1 ");
            sqlBuf.append(" AND b.fportcode = ").append(dbl.sqlString(sPortCode));
            sqlBuf.append(" AND a.FTransDate = ").append(dbl.sqlDate(YssFun.addDay(dWorkDay, -1)));
            sqlBuf.append(" GROUP BY c.FSecurityCode) ");
            rs = dbl.queryByPreparedStatement(sqlBuf.toString()); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                dbStorageAmount = rs.getDouble("FStorageAmount");
            }
        } catch (Exception ex) {
            throw new YssException("获取昨日库存出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dbStorageAmount;
    }

    /**
     * 通过证券代码、组合代码获取今日开仓交易总数量
     * 2009-03-19 蒋锦 添加
     * @param sSecuritycode String：证券代码
     * @param dWorkDay Date：业务日期
     * @param sPortCode String：组合代码
     * @return double：开仓总数
     * @throws YssException
     */
    private double getTodayOpenAmount(String sSecuritycode,
                                      java.util.Date dWorkDay,
                                      String sPortCode,
                                      String subCatCode) throws YssException {
        double dbOpenAmount = 0;
        ResultSet rs = null;
        StringBuffer sqlBuf = new StringBuffer();
        try {
        	// modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
        	sqlBuf.append(" SELECT SUM(c.FTradeAmount) AS FTradeAmount ");
            sqlBuf.append(" FROM ( ");
            sqlBuf.append(" select a.* from ");
            sqlBuf.append(" ( select * from ");
            //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
            sqlBuf.append(pub.yssGetTableName("Tb_Data_FuturesTrade_Tmp"));
            sqlBuf.append(" WHERE Fsecuritycode = ").append(dbl.sqlString(sSecuritycode));
            sqlBuf.append(" AND FCheckState = 1 ");
            sqlBuf.append(" AND fbargaindate = ").append(dbl.sqlDate(dWorkDay));
            sqlBuf.append(" AND FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_PC));
            sqlBuf.append(" AND Fportcode = ").append(dbl.sqlString(sPortCode));
            sqlBuf.append(" ) a ");
            sqlBuf.append(" JOIN ( SELECT * FROM ");
			sqlBuf.append(pub.yssGetTableName("Tb_Para_IndexFutures"));
			sqlBuf.append(" where fcheckstate = 1 and FSubCatCode = " );
			sqlBuf.append(dbl.sqlString(subCatCode));
			sqlBuf.append(" ) b ON a.FSecurityCode = b.FSecurityCode");
			sqlBuf.append(" ) c ");
            // ----------------
            rs = dbl.queryByPreparedStatement(sqlBuf.toString()); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                dbOpenAmount = rs.getDouble("FTradeAmount");
            }
        } catch (Exception ex) {
            throw new YssException("获取今日开仓数量出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dbOpenAmount;
    }

    /**
     * 获取当日指定交易之前的平仓总数
     * 2009-03-19 蒋锦 添加
     * @param sTradeNum String：交易编号
     * @param sSecurityCode String：证券代码
     * @param dWorkDay Date：业务日期
     * @param sPortCode String: 组合代码
     * @return double：平仓交易数量
     * @throws YssException
     */
    private double getTodayCloseAmountBeforeThis(String sTradeNum,
                                                 String sSecurityCode,
                                                 java.util.Date dWorkDay,
                                                 String sPortCode,
                                                 String subCatCode) throws YssException {
        double dbCloseAmount = 0;
        ResultSet rs = null;
        StringBuffer sqlBuf = new StringBuffer();
        try {
        	// modify by fangjiang 2010.08.25 MS01439 QDV4博时2010年7月14日02_A 
            sqlBuf.append(" SELECT SUM(c.FTradeAmount) AS FTradeAmount ");
            sqlBuf.append(" FROM ( ");
            sqlBuf.append(" select a.* from ");
            sqlBuf.append(" ( select * from ");
            //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
            sqlBuf.append(pub.yssGetTableName("Tb_Data_FuturesTrade_Tmp"));
            sqlBuf.append(" WHERE Fsecuritycode = ").append(dbl.sqlString(sSecurityCode));
            sqlBuf.append(" AND FCheckState = 1 ");
            sqlBuf.append(" AND fbargaindate = ").append(dbl.sqlDate(dWorkDay));
            sqlBuf.append(" AND FTradeTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_JYLX_PC));
            sqlBuf.append(" AND Fportcode = ").append(dbl.sqlString(sPortCode));
            sqlBuf.append(" AND FNum < ").append(dbl.sqlString(sTradeNum));
            sqlBuf.append(" ) a ");
            sqlBuf.append(" JOIN ( SELECT * FROM ");
			sqlBuf.append(pub.yssGetTableName("Tb_Para_IndexFutures"));
			sqlBuf.append(" where fcheckstate = 1 and FSubCatCode = " );
			sqlBuf.append(dbl.sqlString(subCatCode));
			sqlBuf.append(" ) b ON a.FSecurityCode = b.FSecurityCode");
			sqlBuf.append(" ) c ");
            //------------------------
            rs = dbl.queryByPreparedStatement(sqlBuf.toString()); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                dbCloseAmount = rs.getDouble("FTradeAmount");
            }
        } catch (Exception ex) {
            throw new YssException("获取今日平仓数量出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dbCloseAmount;
    }
    
    /**
	 * 使用组合代码获取期货核算类型 MS00321 QDV4招商证券2009年03月17日01_A 2009.03.18 蒋锦 添加
	 * 
	 * @param sPortCode
	 *            String：组合代码
	 * @param htPortAccountType
	 *            Hashtable：核算类型组合对
	 * @return String：核算类型
	 * @throws YssException
	 * modify by fangjiang 2010.08.27 MS01439 QDV4博时2010年7月14日02_A 
	 * modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
	 */
    private String getAccountTypeBy(String sPortCode,
			Hashtable htPortAccountType, int i) throws YssException {
		// modify by fangjiang 2010.08.27 MS01439 QDV4博时2010年7月14日02_A 
		// i = 0 时处理股指期货，i = 1 时处理债券期货，i = 2时处理外汇期货 i == 3 时处理商品期货
		String sAccountType = "";
		if(i == 0){ //股指期货
			// 默认使用先入先出
			sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO;
			String sTheDayFirstFIFO = "";
			String sModAvg = "";
			try {
				sTheDayFirstFIFO = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO);
				sModAvg = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG);
				if (sTheDayFirstFIFO != null
						&& sTheDayFirstFIFO.indexOf(sPortCode) != -1) {//edit by xuxuming,20091228.以前的代码在此处写反了
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO;
				} else if (sModAvg != null && sModAvg.indexOf(sPortCode) != -1) {//edit by xuxuming,20091228.以前的代码在此处写反了
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG;
				}
			} catch (Exception ex) {
				throw new YssException(ex.getMessage());
			}
		} else if (i == 1 || i==2 || i == 3 ){ //债券期货，外汇期货  ,商品期货
			// 默认移动加权
			sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG;
			String sTheDayFirstFIFO = "";
			String sFIFO = "";
			try {
				sTheDayFirstFIFO = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO);
				sFIFO = (String) htPortAccountType
						.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO);
				if (sTheDayFirstFIFO != null
						&& sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO;
				} else if (sFIFO != null && sFIFO.indexOf(sPortCode) != -1) {
					sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO;
				}
			} catch (Exception ex) {
				throw new YssException(ex.getMessage());
			}
		}
		return sAccountType;
		//-------------------
	}

    /**
     * 获取当天有剩余数量的开仓交易，= 开仓数量 - 平仓数量
     * 2009-03-19 蒋锦 添加
     * @param dWorkDay Date：交易日期
     * @param sSecurityCode String：期货代码
     * @param sPortCode String：组合代码
     * @param sCloseTradeNum String：平仓交易取今天此平仓编号之前的所有平仓数据
     * @param dbLastStorageAmount double：昨日剩余库存
     * @return ArrayList：今日开仓剩余库存
     * @throws YssException
     */
    private ArrayList getTodayLastTradeAmountBy(java.util.Date dWorkDay,
                                                String sSecurityCode,
                                                String sPortCode,
                                                String sCloseTradeNum,
                                                double dbLastStorageAmount,
                                                String sAccountType,
                                                String subCatCode) throws YssException {
        ArrayList alOpenTrade = new ArrayList();
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            FuturesTradeBean trade = null;
            // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
            buf.append(" SELECT FNum, a.FSecurityCode, FTradeTypeCode, FTradeAmount, FTradePrice ");
            buf.append(" FROM ");
            buf.append(" ( select * FROM ");
            //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
            buf.append(pub.yssGetTableName("TB_DATA_FUTURESTRADE_Tmp"));
            buf.append(" WHERE FBARGAINDATE = ").append(dbl.sqlDate(dWorkDay));
            buf.append(" AND FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            buf.append(" AND FPortCode = ").append(dbl.sqlString(sPortCode));
            buf.append(" AND FCheckState = 1");
            if (!sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO)) {
                buf.append(" AND FNum < ").append(dbl.sqlString(sCloseTradeNum));
            }
            //2009.03.29 招商基金 平仓收益资金调拨没有先入先出 蒋锦 添加 MS00347 《QDV4招商证券2009年03月28日02_B》
            buf.append(" ) a join ( SELECT * FROM ");
            buf.append(pub.yssGetTableName("Tb_Para_IndexFutures"));
            buf.append(" where fcheckstate = 1 and FSubCatCode = ");
            buf.append(dbl.sqlString(subCatCode));
            buf.append(" ) b ON a.FSecurityCode = b.FSecurityCode");
            buf.append(" ORDER BY a.FNum");
            //-----------------------------
            rs = dbl.queryByPreparedStatement(buf.toString()); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.
                    YSS_JYLX_KC)) {
                    trade = new FuturesTradeBean();
                    trade.setTradeAmount(rs.getDouble("FTradeAmount"));
                    trade.setTradePrice(rs.getDouble("FTradePrice"));
                    trade.setNum(rs.getString("FNum")); //2009.03.29 蒋锦 添加 加个交易标号吧，没有编号不方便查数据
                    if (YssD.sub(trade.getTradeAmount(), dbLastStorageAmount) <= 0) {
                        dbLastStorageAmount = YssD.sub(dbLastStorageAmount,
                            trade.getTradeAmount());
                        continue;
                    } else {
                        trade.setTradeAmount(YssD.sub(trade.getTradeAmount(),
                            dbLastStorageAmount));
                        dbLastStorageAmount = 0;
                    }
                    alOpenTrade.add(trade);
                }
            }
        } catch (Exception ex) {
            throw new YssException("获取今日剩余开仓数量出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alOpenTrade;
    }

    /**
     * 使用今日开仓交易计算今日平仓收益
     * @param alOpenTrade ArrayList：开仓交易链表
     * @param sTradeNum String：平仓交易编号
     * @param sSecurityCode String：证券代码
     * @param dWorkDay Date：平仓日期
     * @param sPortCode String：组合代码
     * @param sAccountType String：期货核算方式
     * @param outDBLastCloseAmount ArrayList：剩余平仓数量，也就是说如果开仓交易的数量小于平仓数量那么将剩余的平仓数量用该变量返回
     * @return double：平仓收益
     * @throws YssException
     */
    private double calculateCloseIncomeByTodayOpenTrades(ArrayList alOpenTrade,
        String sTradeNum,
        String sSecurityCode,
        java.util.Date dWorkDay,
        String sPortCode,
        double dbLastCloseAmount,
        ArrayList outDBLastCloseAmount,
        String subCatCode) throws YssException {
        double dbFutureGain = 0;
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
        	// modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
            buf.append(" SELECT * FROM (");
            buf.append(" SELECT FNum, FSecurityCode, FTradeTypeCode, FTradeAmount, FTradePrice ");
            buf.append(" FROM ");
            //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
            buf.append(pub.yssGetTableName("TB_DATA_FUTURESTRADE_Tmp"));
            buf.append(" WHERE FBARGAINDATE = ").append(dbl.sqlDate(dWorkDay));
            buf.append(" AND FSecurityCode = ").append(dbl.sqlString(sSecurityCode));
            buf.append(" AND FPortCode = ").append(dbl.sqlString(sPortCode));
            buf.append(" AND FCheckState = 1");
            buf.append(" AND FNum = ").append(dbl.sqlString(sTradeNum)).append(") a");
            buf.append(" JOIN (SELECT FSecurityCode AS FBSecurityCode, FFuType, FMultiple ");
            buf.append(" FROM ").append(pub.yssGetTableName("TB_PARA_INDEXFUTURES"));
            buf.append(" WHERE FCheckState = 1 and FSubCatCode = ");
            buf.append(dbl.sqlString(subCatCode));
            buf.append(" ) b ON a.FSecurityCode = b.FBSecurityCode");
            // --------------------
            rs = dbl.queryByPreparedStatement(buf.toString()); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                if (rs.getString("FBSecurityCode") == null) {
                    throw new YssException("股指期货【" + rs.getString("FSecurityCode") +
                                           "】信息已被删除或反审核，请核对股指期货信息设置！");
                }
                double dbAmount = 0;
                if (YssD.sub(dbLastCloseAmount, 0) > 0) {
                    dbAmount = dbLastCloseAmount;
                } else {
                    dbAmount = rs.getDouble("FTradeAmount");
                }
                for (int i = 0; i < alOpenTrade.size(); i++) {
                    if (YssD.sub(dbAmount, 0) == 0) {
                        break;
                    }
                    FuturesTradeBean trade = (FuturesTradeBean) alOpenTrade.get(i);
                    //平仓数量大于等于开仓数量
                    if (YssD.sub(dbAmount, trade.getTradeAmount()) >= 0) {
                        if (rs.getString("FFUType").trim().equalsIgnoreCase("BuyAM")) {
                            //多头  平仓收益（平仓当天发生）：（平仓价格 - 开仓价格）* 平仓数量 * 放大倍数
                            dbFutureGain +=
                                YssD.mul(YssD.sub(rs.getDouble("FTRADEPRICE"),
                                                  trade.getTradePrice()),
                                         trade.getTradeAmount(),
                                         rs.getDouble("FMULTIPLE"));
                        } else {
                            //空头  平仓收益（平仓当天发生）：（开仓价格 - 平仓价格）* 平仓数量 * 放大倍数
                            dbFutureGain += YssD.mul(YssD.sub(trade.getTradePrice(),
                                rs.getDouble("FTRADEPRICE")),
                                trade.getTradeAmount(),
                                rs.getDouble("FMULTIPLE"));
                        }
                        dbAmount = YssD.sub(dbAmount, trade.getTradeAmount());
                    } else { //平仓数量小于开仓数量
                        if (rs.getString("FFUType").trim().equalsIgnoreCase("BuyAM")) {
                            //多头  平仓收益（平仓当天发生）：（平仓价格 - 开仓价格）* 平仓数量 * 放大倍数
                            dbFutureGain +=
                                YssD.mul(YssD.sub(rs.getDouble("FTRADEPRICE"),
                                                  trade.getTradePrice()),
                                         dbAmount > 0 ? dbAmount : rs.getDouble("FTRADEAMOUNT"),
                                         rs.getDouble("FMULTIPLE"));
                            dbAmount = 0;
                        } else {
                            //空头  平仓收益（平仓当天发生）：（开仓价格 - 平仓价格）* 平仓数量 * 放大倍数
                            dbFutureGain += YssD.mul(YssD.sub(trade.getTradePrice(),
                                rs.getDouble("FTRADEPRICE")),
                                dbAmount > 0 ? dbAmount : rs.getDouble("FTRADEAMOUNT"),
                                rs.getDouble("FMULTIPLE"));
                            dbAmount = 0;
                        }
                        break;
                    }
                }
                if (dbAmount > 0) {
                    //将剩余平仓数量返回
                    outDBLastCloseAmount.add(new Double(dbAmount));
                }
            }
        } catch (Exception ex) {
            throw new YssException("使用今日当日仓交易计算当日平仓收益出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dbFutureGain;
    }

    /**
     * 使用昨日行情计算今日平仓收益
     * 2009-03-19 蒋锦 添加
     * @param sTradeNum String：平仓交易编号
     * @param sSecurityCode String：平仓期货代码
     * @param dWorkDay Date：平仓日期
     * @param sPortCode String：组合代码
     * @param dbCloseAmount double: 平仓数量，当不为零时使用这个值作为平仓数量，不从数据库中取
     * @return double：平仓收益
     * @throws YssException
     */
    private double calculateCloseIncomeByYesterdayMKTPrice(String sTradeNum,
        String sSecurityCode,
        java.util.Date dWorkDay,
        String sPortCode,
        double dbCloseAmount,
        String subCatCode) throws YssException {
        double dbFutureGain = 0;
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
        	// modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
            buf.append("SELECT A.FPRICE,B.FTRADEPRICE,B.FTRADEAMOUNT,C.FMULTIPLE,C.FFUType ");
            buf.append("FROM " + pub.yssGetTableName("TB_DATA_VALMKTPRICE") + " A ");
            buf.append("JOIN ( select * from ");
            //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
            buf.append(pub.yssGetTableName("TB_DATA_FUTURESTRADE_Tmp"));
            buf.append( " ) B ON A.FSECURITYCODE = B.FSECURITYCODE ");
            buf.append(" JOIN ( select * from ");
            buf.append(pub.yssGetTableName("TB_PARA_INDEXFUTURES"));
            buf.append(" where fcheckstate = 1 and FSubCatCode = ");
            buf.append(dbl.sqlString(subCatCode));
            buf.append(" ) C ON B.FSECURITYCODE = C.FSECURITYCODE ");      
            buf.append("WHERE B.FSECURITYCODE = '" + sSecurityCode + "' ");
            buf.append("AND A.FVALDATE = " + dbl.sqlDate(YssFun.addDay(dWorkDay, -1)) + " ");
            buf.append("AND B.FBARGAINDATE = " + dbl.sqlDate(dWorkDay) + " ");
            buf.append("AND A.FPORTCODE = '" + sPortCode + "'");
            buf.append("AND b.FNum = ").append(dbl.sqlString(sTradeNum));
            //-------------------------------
            //根据交易编号从期货交易表获取证券代码，然后根据证券代码，组合号、业务日期获取T-1日的估值行情
            rs = dbl.queryByPreparedStatement(buf.toString()); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
                double dbAmount = 0;
                if (YssD.sub(dbCloseAmount, 0) > 0) {
                    dbAmount = dbCloseAmount;
                } else {
                    dbAmount = rs.getDouble("FTRADEAMOUNT");
                }
                //判断是多头持仓还是空头持仓，采取不同的计算公式计算平仓收益
                if (rs.getString("FFUType").trim().equalsIgnoreCase("BuyAM")) {
                    //多头  平仓收益（平仓当天发生）：（平仓价格 - 昨日收盘价）* 平仓数量 * 放大倍数
                    dbFutureGain = YssD.mul(YssD.sub(rs.getDouble("FTRADEPRICE"),
                        rs.getDouble("FPRICE")),
                                            dbAmount,
                                            rs.getDouble("FMULTIPLE"));
                } else {
                    //空头  平仓收益（平仓当天发生）：（昨日收盘价 - 平仓价格）* 平仓数量 * 放大倍数
                    dbFutureGain = YssD.mul(YssD.sub(rs.getDouble("FPRICE"),
                        rs.getDouble("FTRADEPRICE")),
                                            dbAmount,
                                            rs.getDouble("FMULTIPLE"));
                }
            }
        } catch (Exception ex) {
            throw new YssException("使用昨日行情计算当日平仓收益出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dbFutureGain;
    }

    /**
     * 使用移动加权平均计算当平仓收益发生额
     * 2009-3-24 蒋锦 修改  MS00273 QDV4中金2009年02月27日01_A
     * 平仓收益发生额 = 平仓收益总额-买出估值增值
     * 最后一笔平仓轧差计算
     * @param sTradeNum String
     * @param dWorkDay Date
     * @return double
     * @throws YssException
     */
    private double calculateCloseIncomeByModAvg(String sTradeNum,
                                                String sSecurityCode,
                                                java.util.Date dWorkDay,
                                                String tsfTypeCode1,
                                                String tsfTypeCode2,String subCatCode) throws YssException {
        double dbResult = 0;
        ResultSet rs = null;
        String sqlStr = "";
        try {
        	// modify by fangjiang 2010.09.02 MS01439 QDV4博时2010年7月14日02_A 
            sqlStr = "select d.* from ( SELECT a.*, b.FMoney, b.Fstorageamount, c.FMoney AS FSMoney" +
                //edit by songjie 2013.03.13 STORY #3719 需求上海-[开发部]QDIIV4[中]20130313001 Futurestrade 改为 Futurestrade_Tmp
				" FROM " + pub.yssGetTableName("Tb_Data_Futurestrade_Tmp") + " a" +
                " LEFT JOIN (SELECT * FROM " + pub.yssGetTableName("Tb_Data_Futtraderela") +
                " WHERE FSettleState = 1 AND FTsfTypeCode = " + dbl.sqlString(tsfTypeCode1) + // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                ") b" +
                " ON a.Fsecuritycode = b.FNum" +
                " AND a.FPortCode = b.FPortCode " +
                " AND a.FNum = b.FCloseNum" +
                " AND a.Fbargaindate = b.ftransdate" +
                " and a.FBrokerCode = b.FBrokerCode " +
                " LEFT JOIN (SELECT * FROM " + pub.yssGetTableName("Tb_Data_Futtraderela") +
                " WHERE FTsfTypeCode = " + dbl.sqlString(tsfTypeCode2) + // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                " AND FSettleState = 1" +
                ") c" +
                " ON a.Fsecuritycode = c.FNum" +
                " AND a.FPortCode = c.FPortCode " +
                " AND a.FNum = c.FCloseNum" +
                " AND a.Fbargaindate = c.ftransdate" +
                " and a.FBrokerCode = c.FBrokerCode " +
                " WHERE a.FTradeTypeCode = " + dbl.sqlString(YssOperCons.YSS_JYLX_PC) +
                " AND a.FNum = " + dbl.sqlString(sTradeNum) +
                " AND a.FCheckState = 1" +
                " AND a.fbargaindate = " + dbl.sqlDate(dWorkDay) +
                " ORDER BY a.FNum ) d join ( select Fsecuritycode from "+ pub.yssGetTableName("Tb_Para_Indexfutures") +
                " where FCheckState = 1 and fsubcatcode = " + dbl.sqlString(subCatCode) +
                " ) e on d.fsecuritycode = e.fsecuritycode ";
                //---------------------------
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                dbResult = YssD.sub(rs.getDouble("FMoney"), rs.getDouble("FSMoney"));
            }
        } catch (Exception ex) {
            throw new YssException("使用加权平均计算平仓收益时出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dbResult;
    }

    /**
     * 获取平仓收益
     * 当日优先先入先出法：如果当天有交易先将当天交易平仓，= (平仓价 - 开仓价)*放大倍数*平仓数量，
     * 将当天开仓数量全部平仓后，=(平仓价-昨日收盘价)*放大倍数*平仓数量
     * 先入先出法：先使用昨天的剩余库存计算，=(平仓价-昨日收盘价)*放大倍数*平仓数量，
     * 如果昨日库存被全部平仓还不够平仓数量则使用今天的开仓交易计算，=(平仓价 - 开仓价)*放大倍数*平仓数量
     * 移动加权平均：= 平仓收益余额 - 卖出股指增值
     * @param securityCode String 证券编号
     * @param portcode int 组合号
     * @param dworkDay Date 业务日期
     * @param dbAmount double 交易数量，也就是平仓交易的交易数量
     * @return double
     * @throws YssException
     */
    private double closeIncome(String sTradeNum,
        double dbAmount, String securityCode, String portcode,
        java.util.Date dWorkDay, Hashtable htAccountType,
        String subCatCode, String tsfTypeCode1,
        String tsfTypeCode2, String tsfTypeCode3, int i) throws YssException {  //modify by fangjiang 2011.02.15 STORY #462 外汇期货需求
        double dfutureGain = 0;
        double dbYesterdayStorageAmount = 0;
        double dbCloseAmount = 0;
        double dbOpenAmount = 0;
        ArrayList alOpenTrade = null;
        try {
            if (getAccountTypeBy(portcode, htAccountType, i).equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)) {
                dfutureGain = calculateCloseIncomeByModAvg(sTradeNum, securityCode, 
                		dWorkDay, tsfTypeCode1, tsfTypeCode3, subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
            } else {
                //获取昨日库存数量
                dbYesterdayStorageAmount = getYesterdayStorageAmountBy(dWorkDay,
                    securityCode, portcode, tsfTypeCode2); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                //获取今日平仓数量
                dbCloseAmount = getTodayCloseAmountBeforeThis(sTradeNum,
                    securityCode, dWorkDay, portcode, subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                //获取今日开仓数量
                dbOpenAmount = getTodayOpenAmount(securityCode, dWorkDay, portcode, subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 

                //当天优先先入先出
                if (getAccountTypeBy(portcode, htAccountType, i).equalsIgnoreCase(YssOperCons.
                    YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO)) {
                    //平仓数量大于开仓数量
                    if (YssD.sub(dbCloseAmount, dbOpenAmount) > 0) {
                        //使用昨日行情计算
                        dfutureGain = calculateCloseIncomeByYesterdayMKTPrice(
                            sTradeNum, securityCode, dWorkDay, portcode,
                            0, subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                    } else { //平仓数量小于等于开仓数量
                        ArrayList alLastCloseAmount = new ArrayList();
                        //获取开仓交易数据
                        alOpenTrade = getTodayLastTradeAmountBy(dWorkDay,
                            securityCode, portcode, sTradeNum, dbCloseAmount,
                            YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO,
                            subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                        //使用今日开仓交易计算
                        dfutureGain = calculateCloseIncomeByTodayOpenTrades(
                            alOpenTrade, sTradeNum, securityCode, dWorkDay, portcode, 0,
                            alLastCloseAmount, subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                        //是否已经使用今日开仓数量完全平仓
                        if (alLastCloseAmount.size() > 0) {
                            dfutureGain +=
                                calculateCloseIncomeByYesterdayMKTPrice(sTradeNum, securityCode,
                                dWorkDay, portcode,((Double) alLastCloseAmount.get(0)).doubleValue(),
                                subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                        }
                    }
                } else { //先入先出
                    //今日平仓数量小于库存数量
                    if (YssD.sub(dbCloseAmount, dbYesterdayStorageAmount) < 0) {
                        //得到可以用于平仓的库存数量
                        double dbCanCloseAmount = YssD.sub(dbYesterdayStorageAmount,
                            dbCloseAmount);
                        //使用昨日库存计算
                        dfutureGain = calculateCloseIncomeByYesterdayMKTPrice(
                            sTradeNum, securityCode, dWorkDay, portcode,
                            dbCanCloseAmount > dbAmount ? dbAmount : dbCanCloseAmount,
                            subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                        //如果实际平仓数量大于库存的可平仓数量
                        if (YssD.sub(dbAmount, dbCanCloseAmount) > 0) {
                            //将剩余数量使用今日交易计算
                            alOpenTrade = getTodayLastTradeAmountBy(dWorkDay,
                                securityCode, portcode, sTradeNum, 0,
                                YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO,
                                subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                            //使用今日开仓交易计算平仓收益
                            dfutureGain +=
                                calculateCloseIncomeByTodayOpenTrades(alOpenTrade,
                                sTradeNum, securityCode, dWorkDay, portcode,
                                YssD.sub(dbAmount, dbCanCloseAmount), new ArrayList(),
                                subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                        }
                    } else { //今日平仓数量大于等于库存数量
                        //将储存数量全部平仓完后剩余的平仓数量
                        double dbLastCloseAmount = YssD.sub(dbCloseAmount,
                            dbYesterdayStorageAmount);
                        //获取开仓交易数据
                        alOpenTrade = getTodayLastTradeAmountBy(dWorkDay, securityCode,
                            portcode, sTradeNum, dbLastCloseAmount,
                            YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO,
                            subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                        //使用今日开仓交易计算平仓收益
                        dfutureGain = calculateCloseIncomeByTodayOpenTrades(
                            alOpenTrade, sTradeNum, securityCode, dWorkDay,
                            portcode, 0, new ArrayList(), subCatCode); // modify by fangjiang 2010.08.26 MS01439 QDV4博时2010年7月14日02_A 
                    }
                }
            }
        } catch (Exception ex) {
            throw new YssException("计算平仓收益时出错！", ex);
        }
        return dfutureGain;
    }

}
