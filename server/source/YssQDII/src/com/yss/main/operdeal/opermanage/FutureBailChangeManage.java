package com.yss.main.operdeal.opermanage;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.main.cashmanage.*;
import com.yss.main.operdata.ValMktPriceBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.platform.pfoper.pubpara.ParaWithPort;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.main.operdeal.valuation.BaseValDeal;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.util.*;

/**
 * <p>Title:xuqiji 20090710:QDV4中金2009年06月03日01_A  MS00481  满足能随时调整股指期货初始保证金金额的功能 </p>
 *
 * <p>Description: 此类做期货保证金变动时，改变保证金金额的业务处理类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FutureBailChangeManage extends BaseOperManage{
    private ArrayList cashTransArr = null; //存放资金调拨数据
    //--- add by songjie 2013.08.28 STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001 start---//
    HashMap hmValPrice = null;//用于保存估值行情
    String tmpMVTable = "";//用于保存最近一日的行情资料数据
    String selCodes = "";//做库存统计的证券代码
    ArrayList alOutInfo = null;
    String exchangeCodes = "";//交易所代码
    String brokerCodes = "";//券商代码
    HashMap hmPaidBailOfOut = null;
    HashMap hmPaidBail = null;
    HashMap hmOut = new HashMap();
    //--- add by songjie 2013.08.28 STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001 end---//
    public FutureBailChangeManage() {
    }

    /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate;
        this.sPortCode = portCode;
    }
    /**
     * 业务操作类
     * @throws YssException
     */
    public void doOpertion() throws YssException {
        saveCashTransferData();//产生资金调拨数据
    }

    /**
     * saveCashTransferData 产生资金调拨数据
     *
     * @param rs ResultSet
     * @param dChangeMoney double 调整金额s
     */
    private void saveCashTransferData()throws YssException {
        CashTransAdmin cashtrans = null; //初始化资金调拨操作类
        String filtersRelaNums = ""; //保存交易编号
        boolean bTrans = false; //事务控制
        Connection conn = dbl.loadConnection(); //获取连接
        try {
            cashTransArr = setCashTransferDate(); //设置资金调拨数据返回ArrayList
            if (cashTransArr.size() > 0) { //是否当天有交易
                cashtrans = new CashTransAdmin();
                cashtrans.setYssPub(pub);
                cashtrans.addList(cashTransArr); //子资金调拨的值放入TransferBean中的arrayList中
                //增加事务控制和锁，以免在多用户同时处理时出现调拨编号重复
                conn.setAutoCommit(false); //设置不自动提交事务
                bTrans = true;
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer")); //给操作的表加锁
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));
                //编号类型为FUChBailMoney 表示期货保证金调整金额
                cashtrans.insert(this.dDate,"FUChBailMoney", filtersRelaNums); //插入数据到资金调拨表和子表中
                conn.commit(); //提交事务
                conn.setAutoCommit(true); //设置为自动提交事务
                bTrans = false;
            }
            
          //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(cashTransArr.size()==0){
    			this.sMsg="        当日无业务";
    		}
            
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }finally{
            dbl.endTransFinal(conn,bTrans);
        }
    }
    /**
     * setCashTransferDate 设置资金调拨数据返回ArrayList
     *
     * @param rs ResultSet
     * @param rst ResultSet
     * @param dChangeMoney double
     * @return ArrayList
     */
    private ArrayList setCashTransferDate() throws YssException {
        boolean analy1; //分析代码1
        boolean analy2; //分析代码2
        boolean analy3; //分析代码3
        ArrayList curCashTransArr = null; //保存资金调拨数据
        ResultSet rs = null;
        BaseStgStatDeal secstgstat = null;

        boolean contractType = false;
    	ParaWithPort para = new ParaWithPort();
    	
        try{
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            curCashTransArr = new ArrayList();
            
            //--- add by songjie 2013.08.26 STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001 start---//
            getStorageSecCodes();
            
            secstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("SecurityStorage");
            secstgstat.setYssPub(pub);
            secstgstat.setStatCodes(selCodes);
            secstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(this.sPortCode));
            
            //统计期货库存数据
            BaseStgStatDeal futruesstgstat = (BaseStgStatDeal) pub.
            getOperDealCtx().getBean("IndexFuturesStorage");
            futruesstgstat.setYssPub(pub);
            futruesstgstat.initStorageStat(dDate, dDate, operSql.sqlCodes(this.sPortCode), false, false);
            /**add---huhuichao 2013-12-6 BUG #85058 期货业务执行调度方案报违反唯一性约束错误 */
            this.deleteData(dDate, operSql.sqlCodes(this.sPortCode));
            /**end---huhuichao 2013-12-6 BUG #85058 */
            futruesstgstat.getStorageStatData(dDate);
            
    		getPubPara();//获取通参：期货占用保证金数据来源
    		getOccupiedBailOfOut();//从外部接口文件获取占用保证金数据
            
            createTransferOfFix(curCashTransArr, analy1, analy2, analy3);
            
        	para.setYssPub(pub);
        	contractType = para.getFutursPositionType(this.sPortCode.replace("'", ""));
        	
        	if (!contractType)
        	{
        		createTransferOfScale(curCashTransArr, analy1, analy2, analy3);
        	}
        	
            //--- add by songjie 2013.08.26 STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001 end---//            
        }catch(Exception e){
            throw new YssException("设置资金调拨数据返回ArrayList出错！",e);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
        return curCashTransArr;
    }

    /**
     * add by huhuichao 2013-12-6 BUG #85058 期货业务执行调度方案报违反唯一性约束错误
     * 期货交易关联数据表中09FU01,09FU02,09FU03,09FU04的数据不能正常被删除，为了不影响库存统计，在此作一个数据的删除
     * @param dDate
     * @param portCode
     * @throws YssException
     */
    public void deleteData(java.util.Date dDate,String sPortCodes) throws YssException {
        String strSql = "";
        try {
            strSql = "DELETE FROM " + pub.yssGetTableName("TB_Data_FutTradeRela") +
                " WHERE FTransDate = " + dbl.sqlDate(dDate)+
                " and FPortCode in ("+ sPortCodes+")"+
                " and ftsftypecode  in (" + operSql.sqlCodes(com.yss.util.YssOperCons.YSS_FU_09) + ")"; 
                dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("删除期货交易关联数据出错\r\n" + e.getMessage());
        }
    }
    
    /**
     * add by songjie 2013.09.02
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 获取做库存统计的证券代码
     * @throws YssException
     */
    private void getStorageSecCodes() throws YssException{
    	StringBuffer strSql = new StringBuffer();
    	ResultSet rs = null;
    	try{
    		/**add---huhuichao 2013-12-11 BUG  85253 60sp4版本业务处理时违反唯一主键约束问题(期货调整保证金的选项)*/
    		//原来是从Tb_Para_Security取的证券代码，现在改为从Tb_Stock_Security取，加上组合代码的条件，避免获取到其他组合的证券代码
    		strSql.append("select distinct S.FSecurityCode from ").append(pub.yssGetTableName("Tb_Stock_Security"));
    		strSql.append(" S join ( ");
    		strSql.append(" select FSecurityCode from ").append(pub.yssGetTableName("Tb_Para_Security"));
    		strSql.append(" where FCheckState = 1 and FCatCode = 'FU' ) l ");
    		strSql.append(" on s.fsecuritycode = l.fsecuritycode ");
    		strSql.append(" where s.FPortCode in (").append(operSql.sqlCodes(this.sPortCode)).append(" )");
    		strSql.append(" and s.FStorageDate between ").append(dbl.sqlDate(this.dDate)).append(" and ");
    		strSql.append(dbl.sqlDate(this.dDate));
    		/**end---huhuichao 2013-12-11 BUG  85253*/
    		rs = dbl.openResultSet(strSql.toString());
    		while(rs.next()){
    			selCodes += rs.getString("FSecurityCode") + ",";
    		}
    		
    		if(selCodes.length() > 1){
    			selCodes = selCodes.substring(0,selCodes.length() - 1);
    		}
    	}catch(Exception e){
    		throw new YssException("获取做库存统计的证券代码", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /**
     * add by songjie 2013.08.26
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 处理保证金类型为固定的期货保证金调整数据
     * @param curCashTransArr
     * @param analy1
     * @param analy2
     * @param analy3
     * @throws YssException
     */
    private void createTransferOfFix(ArrayList curCashTransArr,boolean analy1,boolean analy2,boolean analy3) 
    throws YssException {
        String outInfo = "";
        String[] outInfos = null;
        String exchangeCodeOfOut = "";
        String brokerCodeOfOut = "";
        Iterator iter = null;
    	try{
            //从期货保证金调整表，库存表，期货交易数据表中，获取调整保证金金额，库存数量，当天之前所有交易的保证金总额
    		if(this.alOutInfo.size() > 0){
    			iter = this.alOutInfo.iterator();
    			while(iter.hasNext()){
    				outInfo = (String)iter.next();
    				outInfos = outInfo.split("\t");
    				exchangeCodeOfOut = outInfos[1];
    				brokerCodeOfOut = outInfos[2];
    				//生成 保证金类型 = 固定 且 占用保证金来源 = 外部获取 的 情况 对应的 期货调整保证金 资金调拨数据
    				createTransOfFixAndOut(curCashTransArr,analy1, analy2, analy3,
    						               exchangeCodeOfOut.trim(),brokerCodeOfOut.trim());
    			}
    		}
    		
    		//生成  保证金类型为固定 且 占用保证金来源 = 自动计算 的 情况 对应的 期货调整保证金 资金调拨数据
    		createTransOfFixAndAutoCalc(curCashTransArr, analy1, analy2, analy3);
    	}catch(Exception e){
    		throw new YssException("处理保证金类型为固定的期货保证金调整数据出错",e);
    	}
    }
    
    /**
     * 生成  保证金类型为固定 且 占用保证金来源 = 自动计算 的 情况 对应的 期货调整保证金 资金调拨数据
     * 
     * add by songjie 2013.09.02 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * @param curCashTransArr
     * @param analy1
     * @param analy2
     * @param analy3
     * @throws YssException
     */
    private void createTransOfFixAndAutoCalc(ArrayList curCashTransArr,boolean analy1,boolean analy2,boolean analy3)throws YssException{
        TransferBean transfer = null; //调拨类初始化
        TransferSetBean transferset = null; //调拨子类初始化
        TransferSetBean transfersetIn = null;
        ArrayList subtransfer = null; //保存资金子调拨
        String sql = "";
        ResultSet rs = null;
        double dBaseRate = 0; //基础汇率
        double dPortRate = 0; //组合汇率
        double money = 0.0; //调拨金额
        boolean isOut = false;
    	try{
            //获取保证金类型 = 固定  且 占用保证金来源 = 自动计算 的已缴纳保证金数据
            getPaidBailInfo(true,false);
    		
            sql = getChangeMoneyStorageMountTotalBegBailMoney();
            rs = dbl.queryByPreparedStatement(sql);
            while(rs.next()) {
            	isOut = judgeIsOut(rs);
            	if(!isOut){            	
            		subtransfer = new ArrayList();
                
            		transfer = new TransferBean();
            		
            		transfer.setDtTransferDate(this.dDate);//调拨日期
            		transfer.setDtTransDate(this.dDate);//业务日期
            		transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);//设置调拨类型--01
            		transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU01_CHM);//设置调拨子类型,调整保证金类型--0005
            		transfer.setFNumType("FUChBailMoney");// 设置编号类型
            		transfer.setStrSecurityCode(rs.getString("FSecurityCode")); //证券代码

            		transfer.checkStateId = 1;
                
            		dBaseRate = rs.getDouble("FBaseCuryRate");//赋值基础汇率
            		dPortRate = rs.getDouble("FPortCuryRate");//赋值组合汇率

            		transferset = new TransferSetBean();
            		
            		transferset.setSPortCode(rs.getString("FPortCode")); //设置组合代码
            		if (analy1) {
            			transferset.setSAnalysisCode1(rs.getString("FInvMgrCode")); //先删除投资经理
            		}else{
            			transferset.setSAnalysisCode1(" ");
            		}
            		if (analy2) {
            			transferset.setSAnalysisCode2(rs.getString("FBrokerCode")); //先删除券商代码
            		}else{
            			transferset.setSAnalysisCode2(" ");
            		}
                
            		//调整保证金金额 = 每手固定保证金金额 * 昨日库存数量-当天之前所有交易的初始保证金总额
            		money =YssD.sub(YssD.mul(rs.getDouble("FBailFix"),rs.getDouble("FStorageAmount")),rs.getDouble("TotalBailMoney"));
                
            		transferset.setSCashAccCode(rs.getString("FChageBailAcctCode")); //变动保证金，流出
            		transferset.setIInOut(-1);//调拨方向 流出
                    
            		transferset.setDMoney(money);//设置调拨金额
            		transferset.setDBaseRate(dBaseRate);//设置基础汇率
            		transferset.setDPortRate(dPortRate);//设置组合汇率

            		transferset.checkStateId = 1;
                
            		subtransfer.add(transferset);
                
            		transfersetIn = (TransferSetBean)transferset.clone();
            		transfersetIn.setSCashAccCode(rs.getString("FBegBailAcctCode")); //初始保证金，流入
                	transfersetIn.setIInOut(1);
                	subtransfer.add(transfersetIn);

                	transfer.setSubTrans(subtransfer); //设置调拨子类型
                	curCashTransArr.add(transfer);
            	}
            }
    	}catch(Exception e){
    		throw new YssException("生成  保证金类型为固定 且 占用保证金来源 = 自动计算 的 情况 对应的 期货调整保证金 资金调拨数据出错",e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    } 
    
    /**
     * 判断占用保证金来源是否为外部获取
     * @param rs
     * @return
     * @throws YssException
     */
    private boolean judgeIsOut(ResultSet rs) throws YssException{
    	boolean isOut = false;
    	try{
        	if(this.alOutInfo.contains(rs.getString("FPortCode") + "\t" + rs.getString("FExchangeCode") + "\t" + rs.getString("FBrokerCode"))){
        		isOut = true;
        	}
        	if(!isOut && this.alOutInfo.contains(rs.getString("FPortCode") + "\t \t" + rs.getString("FBrokerCode"))){
        		isOut = true;
        	}
        	if(!isOut && this.alOutInfo.contains(rs.getString("FPortCode") + "\t" + rs.getString("FExchangeCode") + "\t ")){
        		isOut = true;
        	}
        	if(!isOut && this.alOutInfo.contains(rs.getString("FPortCode") + "\t \t ")){
        		isOut = true;
        	}
    		
    		return isOut;
    	}catch(Exception e){
    		throw new YssException("判断占用保证金来源是否为外部获取出错",e);
    	}
    }
    
    /**
     * 生成 保证金类型 = 固定 且 占用保证金来源 = 外部获取 的 情况 对应的 调整保证金 资金调拨数据
     * 
     * add by songjie 2013.09.02 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * @param curCashTransArr
     * @param analy1
     * @param analy2
     * @param analy3
     * @param exchangeCode
     * @param brokerCode
     * @throws YssException
     */
    private void createTransOfFixAndOut(ArrayList curCashTransArr,boolean analy1,boolean analy2,boolean analy3,
    		String exchangeCode, String brokerCode) throws YssException{
        TransferBean transfer = null; //调拨类初始化
        TransferSetBean transferset = null; //调拨子类初始化
        TransferSetBean transfersetOut = null;
        ArrayList subtransfer = null; //保存资金子调拨
        String sql = "";
        ResultSet rs = null;
        double money = 0;
        double occupiedBail = 0;
        double paidBail = 0;
        double dBaseRate = 0;
        double dPortRate = 0;
    	try{
            //获取保证金类型 = 固定  且 占用保证金来源 = 外部获取的 已缴纳保证金数据
            getPaidBailInfo(true,true);
    		
            sql = getTransOfFixAndOutSql(exchangeCode, brokerCode);
            rs = dbl.queryByPreparedStatement(sql);
            while(rs.next()) {
                subtransfer = new ArrayList();
                
                paidBail = ((Double)hmPaidBailOfOut.get(rs.getString("FMarketCode"))).doubleValue();
                
                occupiedBail = ((Double)hmOut.get(rs.getString("FMarketCode"))).doubleValue();
                
                //T日发生额（即保证金调整的资金调拨数据的金额） = 占用保证金 - 已缴纳保证金
                money = YssD.sub(occupiedBail, paidBail);
                
            	if(money != 0){
            		subtransfer = new ArrayList();
                
            		transferset = new TransferSetBean();
            		
            		transfer = new TransferBean();
            		
                    transfer.setDtTransferDate(this.dDate);//调拨日期
                    transfer.setDtTransDate(this.dDate);//业务日期
                    transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);//设置调拨类型--01
                    transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU01_CHM);//设置调拨子类型,调整保证金类型--0005
                    transfer.setFNumType("FUChBailMoney");// 设置编号类型
                    transfer.setFRelaNum(rs.getString("FMarketCode"));
                    
                    transfer.checkStateId = 1;
            	
            		dBaseRate = rs.getDouble("FBaseCuryRate");//赋值基础汇率
            		dPortRate = rs.getDouble("FPortCuryRate");//赋值组合汇率

            		transferset.setSPortCode(rs.getString("FPortCode")); //设置组合代码
            		transferset.setSAnalysisCode1(" "); //投资经理
                	
            		if (analy2) {
            			transferset.setSAnalysisCode2(rs.getString("FBrokerCode")); //券商代码
            		}else{
            			transferset.setSAnalysisCode2(" ");
            		}
               
            		transferset.setSCashAccCode(rs.getString("FChageBailAcctCode")); //变动保证金，流出
            		transferset.setIInOut(-1);//调拨方向 流出
                
            		transferset.setDMoney(money);//设置调拨金额
            		transferset.setDBaseRate(dBaseRate);//设置基础汇率
            		transferset.setDPortRate(dPortRate);//设置组合汇率

            		transferset.checkStateId = 1;
            		subtransfer.add(transferset);
                
            		transfersetOut = (TransferSetBean)transferset.clone();
            		transfersetOut.setSCashAccCode(rs.getString("FBegBailAcctCode")); //变动保证金，流出
            		transfersetOut.setIInOut(1);//调拨方向 流出
            		subtransfer.add(transfersetOut);
                
            		transfer.setSubTrans(subtransfer); //设置调拨子类型
            		curCashTransArr.add(transfer);
            	}
            }
    	}catch(Exception e){
    		throw new YssException("生成 保证金类型 = 固定 且 占用保证金来源 = 外部获取 的 情况 对应的 调整保证金 资金调拨数据出错" ,e);
    	}finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * add by songjie 2013.08.26
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 处理保证金类型为比例的期货保证金调整数据
     * @param curCashTransArr
     * @param analy1
     * @param analy2
     * @param analy3
     * @throws YssException
     */
    private void createTransferOfScale(ArrayList curCashTransArr,boolean analy1,boolean analy2,boolean analy3) 
    throws YssException {
    	Iterator iter = null;
    	String outInfo = "";
    	String[] outInfos = null;
    	String exchangeCodeOfOut = "";
    	String brokerCodeOfOut = "";
    	try{
    		getValPrice();//获取期货估值行情 ，即 T日每日结算价
                 
    		if(this.alOutInfo.size() > 0){
    			iter = this.alOutInfo.iterator();
    			while(iter.hasNext()){
    				outInfo = (String)iter.next();
    				outInfos = outInfo.split("\t");
    				exchangeCodeOfOut = outInfos[1];
    				brokerCodeOfOut = outInfos[2];
    				//生成 保证金类型 = 固定 且 占用保证金来源 = 外部获取 的 情况 对应的 期货调整保证金 资金调拨数据
    				createTransOfScaleAndOut(curCashTransArr,analy1, analy2, analy3,
    						               exchangeCodeOfOut.trim(),brokerCodeOfOut.trim());
    			}
    		}           
            
            //生成保证金类型 = 比例  且 占用保证金来源 = 自动计算  的期货保证金调整资金调拨数据
            createTransOfScaleAndCalc(analy1,analy2,analy3,curCashTransArr);
    	}catch(Exception e){
    		throw new YssException("处理保证金类型为比例的期货保证金调整数据出错",e);
    	}
    }
    
    /***
     * add by songjie 2013.09.03
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 生成 保证金类型 = 比例 且 占用保证金来源 = 外部获取 的 期货调拨保证金数据
     * 
     * @param exchangeCode
     * @param brokerCode
     * @throws YssException
     */
    private void createTransOfScaleAndOut(ArrayList curCashTransArr,boolean analy1,boolean analy2,boolean analy3, 
    		String exchangeCode, String brokerCode) throws YssException{
    	String sql = "";
    	ResultSet rs = null;
    	ArrayList subtransfer = null;
    	TransferBean transfer = null;
    	double dBaseRate = 0;//基础汇率
    	double dPortRate = 0;//组合汇率
    	double money = 0;//T日发生额
    	double paidBail = 0;//已交纳保证金
    	double occupiedBail = 0;//占用保证金
    	TransferSetBean transferset = null;
    	TransferSetBean transfersetOut = null;
    	try{
            getPaidBailInfo(false,true);//获取 保证金类型 = 比例 且 占用保证金来源 = 外部获取 的 已交纳保证金数据
            
            //生成保证金类型为比例的期货保证金调整sql
            sql = buildScaleAndOutSql(exchangeCode.trim(), brokerCode.trim());
            rs = dbl.queryByPreparedStatement(sql);
            while(rs.next()) {
                //已缴纳保证金金额
                paidBail = ((Double)hmPaidBailOfOut.get(rs.getString("FMarketCode"))).doubleValue();

            	//如果当天可以获取读入的“占用保证金”，则做保证金调整处理，否则不处理。
            	if(hmOut.get(rs.getString("FMarketCode")) != null){
            		occupiedBail = ((Double)hmOut.get(rs.getString("FMarketCode"))).doubleValue();
            	}else{
            		continue;
            	}
            	
                //T日发生额（即保证金调整的资金调拨数据的金额） = 占用保证金 - 已缴纳保证金
                money = YssD.sub(occupiedBail, paidBail);
                
            	if(money != 0){
            		subtransfer = new ArrayList();
                
            		transferset = new TransferSetBean();
            		
            		transfer = new TransferBean();
            		
                    transfer.setDtTransferDate(this.dDate);//调拨日期
                    transfer.setDtTransDate(this.dDate);//业务日期

                    transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);//设置调拨类型--01
                    transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU01_CHM);//设置调拨子类型,调整保证金类型--0005
                    
                    transfer.setFNumType("FUChBailMoney");// 设置编号类型
                    transfer.setFRelaNum(rs.getString("FMarketCode")); //市场代码  即  期货合约代码

                    transfer.checkStateId = 1;
            	
            		dBaseRate = rs.getDouble("FBaseCuryRate");//赋值基础汇率
            		dPortRate = rs.getDouble("FPortCuryRate");//赋值组合汇率

            		transferset.setSPortCode(rs.getString("FPortCode")); //设置组合代码
            		transferset.setSAnalysisCode1(" "); //投资经理
                	
            		if (analy2) {
            			transferset.setSAnalysisCode2(rs.getString("FBrokerCode")); //券商代码
            		}else{
            			transferset.setSAnalysisCode2(" ");
            		}
               
            		transferset.setSCashAccCode(rs.getString("FChageBailAcctCode")); //变动保证金，流出
            		transferset.setIInOut(-1);//调拨方向 流出
                
            		transferset.setDMoney(money);//设置调拨金额
            		transferset.setDBaseRate(dBaseRate);//设置基础汇率
            		transferset.setDPortRate(dPortRate);//设置组合汇率

            		transferset.checkStateId = 1;
            		subtransfer.add(transferset);
                
            		transfersetOut = (TransferSetBean)transferset.clone();
            		transfersetOut.setSCashAccCode(rs.getString("FBegBailAcctCode")); //变动保证金，流出
            		transfersetOut.setIInOut(1);//调拨方向 流出
            		subtransfer.add(transfersetOut);
                
            		transfer.setSubTrans(subtransfer); //设置调拨子类型
            		curCashTransArr.add(transfer);
            	}
            }
    	}catch(Exception e){
    		throw new YssException("生成保证金类型为比例的期货保证金调整数据出错",e);
    	}finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    
    
    /**
     * add by songjie 2013.08.28 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 获取期货估值行情 ，即 T日每日结算价
     * 
     * @throws YssException
     */
    private void getValPrice() throws YssException{
        ArrayList vMethods = null;
        MTVMethodBean valMethod = null;
    	try{
    		hmValPrice = new HashMap();
            
            //生成最大行情日期 对应的行情资料数据临时表
    		createMarketValueTable();
            
            BaseValDeal valuation = new BaseValDeal();
            valuation.setYssPub(pub);
            
            valuation.initValuation(dDate, this.sPortCode, "", null, null);
            //获取已选组合对应的估值方法设置数据
            vMethods = valuation.getValuationMethods();
            //循环估值方法，获取最优的估值行情数据
            for (int i = 0; i < vMethods.size(); i++) {
            	valMethod = (MTVMethodBean) vMethods.get(i);
            	//获取期货估值行情
            	getValPriceOfScale(valMethod);
            }
    	}catch(Exception e){
    		throw new YssException("获取期货估值行情出错",e);
    	}
    }
    
    /**
     * 生成保证金类型为比例的期货保证金调整数据
     * 
     * add by songjie 2013.08.27 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * @param hmPaidBail
     * @param analy1
     * @param analy2
     * @param analy3
     * @param curCashTransArr
     * @throws YssException
     */
    private void createTransOfScaleAndCalc(boolean analy1,boolean analy2,boolean analy3,ArrayList curCashTransArr) 
    throws YssException{
    	String sql = "";
    	ResultSet rs = null;
    	ArrayList subtransfer = null;
    	TransferBean transfer = null;
    	double dBaseRate = 0;//基础汇率
    	double dPortRate = 0;//组合汇率
    	double money = 0;//T日发生额
    	double price = 0;//估值行情
    	double paidBail = 0;//已交纳保证金
    	double occupiedBail = 0;//占用保证金
    	TransferSetBean transferset = null;
    	TransferSetBean transfersetOut = null;
    	
    	ValMktPriceBean mktPrice = null;
    	boolean isOut = false;
    	try{
            getPaidBailInfo(false,false);//获取 保证金类型 = 比例 且 占用保证金来源 = 自动计算 的 已交纳保证金数据
    		    		
            //生成保证金类型为比例的期货保证金调整sql
            sql = buildScaleConditionSql();
            rs = dbl.queryByPreparedStatement(sql);
            while(rs.next()) {            	
            	isOut = judgeIsOut(rs);
            	
                //已缴纳保证金金额
                paidBail = ((Double)hmPaidBail.get(rs.getString("FSecurityCode"))).doubleValue();
            	
            	//自动计算
            	if(!isOut){
                    //T日每日结算价 即 估值行情
                    if(hmValPrice.get(rs.getString("FSecurityCode")) != null){
                    	mktPrice = (ValMktPriceBean)hmValPrice.get(rs.getString("FSecurityCode"));
                    	price = mktPrice.getPrice();
                    }else{
                    	//如果获取不到 T日每日结算价，则不生成保证金调整数据
                    	continue;
                    }
                    
                    //占用保证金（自动计算）  = T日最新保证金比例 * T日期货持仓数量 * 合约放大倍数 * T日每日结算价
                    occupiedBail = YssD.mul(rs.getDouble("FBailScale"),rs.getDouble("FStorageAmount"),rs.getDouble("FMULTIPLE"),price);
                    
                    //T日发生额（即保证金调整的资金调拨数据的金额） = 占用保证金 - 已缴纳保证金
                    money = YssD.sub(occupiedBail, paidBail);
                    
                	if(money != 0){
                		subtransfer = new ArrayList();
                    
                		transferset = new TransferSetBean();
                		transfer = setTransferAttr(rs, "IN"); //设置调拨类型
                	
                		dBaseRate = rs.getDouble("FBaseCuryRate");//赋值基础汇率
                		dPortRate = rs.getDouble("FPortCuryRate");//赋值组合汇率

                		transferset.setSPortCode(rs.getString("FPortCode")); //设置组合代码
                		transferset.setSAnalysisCode1(" "); //投资经理
                    	
                		if (analy2) {
                			transferset.setSAnalysisCode2(rs.getString("FBrokerCode")); //券商代码
                		}else{
                			transferset.setSAnalysisCode2(" ");
                		}
                   
                		transferset.setSCashAccCode(rs.getString("FChageBailAcctCode")); //变动保证金，流出
                		transferset.setIInOut(-1);//调拨方向 流出
                    
                		transferset.setDMoney(money);//设置调拨金额
                		transferset.setDBaseRate(dBaseRate);//设置基础汇率
                		transferset.setDPortRate(dPortRate);//设置组合汇率

                		transferset.checkStateId = 1;
                		subtransfer.add(transferset);
                    
                		transfersetOut = (TransferSetBean)transferset.clone();
                		transfersetOut.setSCashAccCode(rs.getString("FBegBailAcctCode")); //变动保证金，流出
                		transfersetOut.setIInOut(1);//调拨方向 流出
                		subtransfer.add(transfersetOut);
                    
                		transfer.setSubTrans(subtransfer); //设置调拨子类型
                		curCashTransArr.add(transfer);
                	}
            	}
            }
    	}catch(Exception e){
    		throw new YssException("生成保证金类型为比例的期货保证金调整数据出错",e);
    	}finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * 从外部接口文件获取占用保证金数据
     * 
     * add by songjie 2013.08.28 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * @throws YssException
     */
    private void getOccupiedBailOfOut() throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	
    	try{    		
    		strSql = buildOccupiedBailOfOutSql();
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			hmOut.put(rs.getString("FMarketCode"), rs.getDouble("FUsedMargin"));
    		}
    	}catch(Exception e){
    		throw new YssException("从外部接口文件获取占用保证金数据出错",e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /**
     * 拼接外部接口文件获取占用保证金数据sql
     * 
     * add by songjie 2013.08.28 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * @return
     * @throws YssException
     */
    private String buildOccupiedBailOfOutSql() throws YssException{
    	StringBuffer strSql = new StringBuffer();
    	try{
    		strSql
    		.append(" select distinct sum1.FUsedMargin, sum1.FPortCode,sum1.FMarketCode ")
    		.append(" from (select bail.FSecurityCode,bail.FPortCode, bail.FUsedMargin,sec.FMarketCode ")
    		.append(" from (select a.FNum as FSecurityCode, a.FPortCode, b.FUsedMargin ")
    		.append(" from ").append(pub.yssGetTableName("Tb_Data_Futtraderela")).append(" a ")
    		.append(" join (select FSecurityCode,FPortCode,FUsedMargin from ").append(pub.yssGetTableName("Tb_data_UsedMargin"))
    		.append(" where FBargainDate = ").append(dbl.sqlDate(dDate))
    		.append(" and FPortCode <> ' ') b on a.Fnum = b.Fsecuritycode and a.Fportcode = b.FPortCode ")
    		.append(" where a.FTransDate = ").append(dbl.sqlDate(dDate))
    		.append(" and a.FCloseNum = ' ' and a.FTsfTypeCode like '05FU%' and a.FPortCode = ") 
    		.append(dbl.sqlString(this.sPortCode))
    		.append(" and FStorageAmount <> 0 ")
    		.append(" union all ")
    		.append(" select a.FNum as FSecurityCode, a.FPortCode, b.FUsedMargin ")
    		.append(" from ").append(pub.yssGetTableName("Tb_Data_Futtraderela")).append(" a ")
    		.append(" join (select FSecurityCode,FPortCode,FUsedMargin from ").append(pub.yssGetTableName("Tb_data_UsedMargin"))
    		.append(" where FBargainDate = ").append(dbl.sqlDate(dDate))
    		.append(" and FPortCode = ' ') b on a.Fnum = b.Fsecuritycode ")
    		.append(" where a.FTransDate = ").append(dbl.sqlDate(dDate))
    		.append(" and a.FCloseNum = ' ' and a.FTsfTypeCode like '05FU%' and a.FPortCode = ")
    		.append(dbl.sqlString(this.sPortCode))
    		.append(" and FStorageAmount <> 0) bail ")
    		.append(" join ")
    		.append(" (select FSecurityCode,FMarketCode from ").append(pub.yssGetTableName("Tb_Para_Security"))
    		.append(" where FCheckState = 1) sec on sec.FSecurityCode = bail.FSecurityCode ) sum1 ")
    		;
    		
    		return strSql.toString();
    	}catch(Exception e){
    		throw new YssException("拼接外部接口文件获取占用保证金数据sql出错",e);
    	}
    }
    
    /**
     * add by songjie 2013.08.27 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 获取通参：期货占用保证金数据来源
     * 
     * @return
     * @throws YssException
     */
    private void getPubPara() throws YssException{
    	boolean isAutoCalBail = false;
    	String exchangeCode = "";//交易所代码
    	String brokerCode = "";//券商代码
    	String portCode = "";
    	String[] results = null;
    	String subResult = "";
    	String[] subResults = null;
    	try{
            CtlPubPara ctlpubpara = new CtlPubPara();
            ctlpubpara.setYssPub(pub);
            
            String result = ctlpubpara.getFUOccupiedBailSource(this.sPortCode);

            alOutInfo = new ArrayList();
            
            if(result.trim().length() > 0){
            	results = result.split("\r\f");
            	for(int i = 0; i < results.length; i++){
            		subResult = results[i];
            		subResults = subResult.split("\t");
            		
            		portCode = subResults[0];
            		exchangeCode = subResults[1].trim().equals("") ? " " : subResults[1];
            		brokerCode = subResults[2].trim().equals("") ? " " : subResults[2];
            		
            		if(subResults[3].equals("0")){
            			isAutoCalBail = true;//自动计算
            		}else{
            			isAutoCalBail = false;//外部读取
            		}
            		
            		if(!isAutoCalBail){
            			alOutInfo.add(portCode + "\t" + exchangeCode + "\t" + brokerCode);
            		}
            	}
            }
    	}catch(Exception e){
    		throw new YssException("获取通参：期货占用保证金数据来源出错",e);
    	}
    }
    
    /**
     * add by songjie 2013.08.26 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 获取已缴纳保证金数据
     * 
     * @throws YssException
     */
    private HashMap getPaidBailInfo(boolean fix,boolean out) throws YssException{
    	ResultSet rs = null;
    	String strSql = "";
    	hmPaidBailOfOut = new HashMap();
    	hmPaidBail = new HashMap();
    	try{
    		if(!out){
    			strSql = buildPaidBailSql(fix);//获取 占用保证金来源 = 自动计算 的 已缴纳保证金数据
    		
    			rs = dbl.openResultSet(strSql);
    			while(rs.next()){
    				hmPaidBail.put(rs.getString("FSecurityCode"), rs.getDouble("TotalBailMoney"));
    			}
    		}else{
    			strSql = buildPaidBailOfOutSql(fix);//获取 占用保证金来源 = 外部获取 的 已缴纳保证金数据
        		
    			rs = dbl.openResultSet(strSql);
    			while(rs.next()){
    				hmPaidBailOfOut.put(rs.getString("FMarketCode"), rs.getDouble("TotalBailMoney"));
    			}
    		}
    		
    		return hmPaidBail;
    	}catch(Exception e){
    		throw new YssException("处理保证金类型为比例的期货保证金调整数据出错",e);
    	}finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * add by songjie 2013.08.26 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 拼接保证金类型 = 比例 的 已缴纳保证金sql
     * 
     * @return
     * @throws YssException
     */
    private String buildPaidBailSql(boolean fix) throws YssException{
    	StringBuffer sbSql = new StringBuffer();
    	try{
    		sbSql
    		.append(" select out.*,sec.FMarketCode from (select sum(FBegBailMoney) as TotalBailMoney,t.FSecurityCode, t.FPortCode ")
    		.append(" from (select sum(FBegBailMoney) as FBegBailMoney, f.fsecuritycode, f.fportcode ")
    		.append(" from ").append(pub.yssGetTableName("Tb_Data_FuturesTrade")).append(" f where FcheckState = 1 ")
    		.append(" and FBargainDate ").append(fix ? " < " : " <= ").append(dbl.sqlDate(dDate))
    		.append(" and FTradeTypeCode = '20' and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" group by f.fsecuritycode, f.fportcode ")
    		.append(" union all ")
    		.append(" select sum(FBegBailMoney * -1) as FBegBailMoney,f.fsecuritycode,f.fportcode ")
    		.append(" from ").append(pub.yssGetTableName("Tb_Data_FuturesTrade")).append(" f where FcheckState = 1 ")
    		.append(" and FBargainDate").append(fix ? " < " : " <= ").append(dbl.sqlDate(dDate))
    		.append(" and FTradeTypeCode = '21' and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" group by f.fsecuritycode, f.fportcode ")
    		.append(" union all ")
    		.append(" select sum(FMoney) as FBegBailMoney,fer.FSecurityCode,fer.FPortCode ")
    		.append(" from (select tf.*, sub.* from ").append(pub.yssGetTableName("tb_cash_transfer"))
    		.append(" tf join (select * ")
    		.append(" from " ).append(pub.yssGetTableName("tb_cash_subtransfer"))
    		.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" and FInOut = 1) sub on tf.fnum = sub.fnum where tf.FcheckState = 1 ")
    		.append(" and tf.FTransferDate < ").append(dbl.sqlDate(dDate))
    		.append(" and tf.FSubTsfTypeCode = '0005') fer group by fer.FSecurityCode, fer.FPortCode) t ")
    		.append(" join ")
    		.append(" (select FNum as FSecurityCode,FPortCode from ").append(pub.yssGetTableName("Tb_Data_Futtraderela"))
    		.append(" where FCloseNum = ' ' and FTsfTypeCode like '05FU%' and FStorageAmount <> 0 ")
    		.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" and FTransDate = ").append(dbl.sqlDate(dDate)).append(") stock ")
    		.append(" on stock.FSecurityCode = t.FSecurityCode and stock.FPortCode = t.FPortCode ")
    		.append(" join ")
    		.append(" (select fsecuritycode from ").append(pub.yssGetTableName("Tb_Para_IndexFutures"))
    		.append(" where FcheckState = 1 and FBailType = ").append(fix ? "'Fix'" : "'Scale'")
    		.append(") para on para.fsecuritycode = t.FSecurityCode ")
    		.append(" group by t.fsecuritycode, t.fportcode) out ")
    		.append(" join ")
    		.append(" (select FSecurityCode,FMarketCode from ").append(pub.yssGetTableName("Tb_Para_Security"))
    		.append(" where FCheckState = 1 ) sec on sec.FSecurityCode = out.FSecurityCode ")
    		;
    		
    		return sbSql.toString();
    	}catch(Exception e){
    		throw new YssException("拼接已缴纳保证金sql",e);
    	}
    }
    
    
    private String buildPaidBailOfOutSql(boolean fix) throws YssException{
    	StringBuffer sbSql = new StringBuffer();
    	try{
    		sbSql
    		.append(" select sum(sum2.TotalBailMoney) as TotalBailMoney,sum2.FPortCode,sum2.FMarketCode ")
    		.append(" from (select sum(sum1.TotalBailMoney) as TotalBailMoney,sum1.FPortCode,sum1.FMarketCode ")
    		.append(" from (select out.TotalBailMoney,out.FSecurityCode,out.FPortCode,sec.FMarketCode ")
    		.append(" from (select sum(FBegBailMoney) as TotalBailMoney,t.FSecurityCode, t.FPortCode ")
    		.append(" from (select sum(FBegBailMoney) as FBegBailMoney, f.fsecuritycode, f.fportcode ")
    		.append(" from ").append(pub.yssGetTableName("Tb_Data_FuturesTrade")).append(" f where FcheckState = 1 ")
    		.append(" and FBargainDate ").append(fix ? " < " : " <= ").append(dbl.sqlDate(dDate))
    		.append(" and FTradeTypeCode = '20' and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" group by f.fsecuritycode, f.fportcode ")
    		.append(" union all ")
    		.append(" select sum(FBegBailMoney * -1) as FBegBailMoney,f.fsecuritycode,f.fportcode ")
    		.append(" from ").append(pub.yssGetTableName("Tb_Data_FuturesTrade")).append(" f where FcheckState = 1 ")
    		.append(" and FBargainDate").append(fix ? " < " : " <= ").append(dbl.sqlDate(dDate))
    		.append(" and FTradeTypeCode = '21' and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" group by f.fsecuritycode, f.fportcode ")
    		.append(") t ")
    		.append(" group by t.fsecuritycode, t.fportcode) out ")
    		.append(" join ")
    		.append(" (select FSecurityCode,FMarketCode from ").append(pub.yssGetTableName("Tb_Para_Security"))
    		.append(" where FCheckState = 1 ) sec on sec.FSecurityCode = out.FSecurityCode) sum1 ")
    		.append(" group by sum1.FPortCode,sum1.FMarketCode ")
    		.append(" union all ")
    		.append(" select sum(FMoney) as TotalBailMoney, fer.FPortCode, fer.FRelaNum as FMarketCode ")
    		.append(" from (select tf.*, sub.* from ").append(pub.yssGetTableName("tb_cash_transfer"))
    		.append(" tf join (select * ")
    		.append(" from " ).append(pub.yssGetTableName("tb_cash_subtransfer"))
    		.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" and FInOut = 1) sub on tf.fnum = sub.fnum where tf.FcheckState = 1 ")
    		.append(" and tf.FTransferDate < ").append(dbl.sqlDate(dDate))
    		.append(" and tf.FSubTsfTypeCode = '0005' and tf.FRelaNum is not null) fer ")
    		.append(" group by fer.FRelaNum, fer.FPortCode) sum2 group by sum2.FPortCode,sum2.FMarketCode ")
    		;
    		
    		return sbSql.toString();
    	}catch(Exception e){
    		throw new YssException("拼接已缴纳保证金sql",e);
    	}
    }
    
    /**
     * add by songjie 2013.08.26 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 生成估值行情数据临时表
     * 
     * @throws YssException
     */
    private void createMarketValueTable() throws YssException{
    	String tableName = "";
    	StringBuffer sql = new StringBuffer();
    	try{
			tableName = "Tmp_MV_" + pub.getUserCode();// 临时表由日期与估值方法组成
			if (dbl.yssTableExist(tableName)) {
				sql.setLength(0);
				sql.append("drop table " + tableName);
				dbl.executeSql(dbl.doOperSqlDrop(sql.toString()));
			}
			
			sql.setLength(0);
			sql.append("create table ")
			.append(tableName)
			.append(" as (select m1.* from ")
			.append(pub.yssGetTableName("Tb_Data_MarketValue"))
			.append(" m1 join ")
			.append(" (select max(FMktValueDate) as FMktValueDate,FSecurityCode,FMktSrcCode from ")
			.append(pub.yssGetTableName("Tb_Data_MarketValue"))
			.append(" m2 where FCheckState = 1 and FMktValueDate <= ")
			.append(dbl.sqlDate(dDate))
			.append(" group by FSecurityCode,FMktSrcCode) m2 ")
			.append(" on m1.FMKtvalueDate = m2.FMktValueDate and m1.FSecuritycode = m2.FSecurityCode ")
			.append(" and m1.FMktSrcCode = m2.FMktSrcCode ")
			.append(" join ")
			.append(" (select FSecurityCode from ").append(pub.yssGetTableName("Tb_Para_IndexFutures")) 
			.append(" where FCheckState = 1 and FBailType = 'Scale') basic ") 
			.append(" on m1.FSecurityCode = basic.FSecurityCode ")
			.append(" )");
			dbl.executeSql(sql.toString());
			
			sql.setLength(0);
			sql.append("alter table ").append(tableName)
			.append(" add constraints pk_").append(tableName)
			.append(" primary key (FSecurityCode,FPortCode,FMktSrcCode)");// 添加主键
			dbl.executeSql(sql.toString());
			
			this.tmpMVTable = tableName;
    	}catch(Exception e){
    		throw new YssException("生成估值行情数据临时表出错", e);
    	}
    }
    
    /**
     * add by songjie 2013.08.26 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 获取期货估值行情
     * 
     * @param vMethod
     * @throws YssException
     */
    private void getValPriceOfScale(MTVMethodBean vMethod) throws YssException{
		double dMarketPrice = 0;
		String strSql = "";
		ResultSet rs = null;
		ValMktPriceBean mktPrice = null;
    	try{
    		strSql = buildValPriceSql(vMethod);
    		rs = dbl.queryByPreparedStatement(strSql);
    		while(rs.next()){
				dMarketPrice = rs.getDouble("FCsMarketPrice"); // 行情价格

				if (dMarketPrice == 0) {
					continue;
				}
				
				mktPrice = new ValMktPriceBean();
				
				mktPrice.setValDate(rs.getDate("FMktValueDate"));
				mktPrice.setSecurityCode(rs.getString("FCSSecurityCode"));
				mktPrice.setPortCode(rs.getString("FCSPortCode"));
				mktPrice.setPrice(dMarketPrice);
				hmValPrice.put(mktPrice.getSecurityCode(), mktPrice);
    		}
    	}catch(Exception e){
    		throw new YssException("查询期货估值行情出错",e);
    	}finally{
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * add by songjie 2013.08.26 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 拼接获取期货估值行情sql
     * 
     * @return
     * @throws YssException
     */
    private String buildValPriceSql(MTVMethodBean vMethod) throws YssException{
    	StringBuffer sbSql = new StringBuffer();
    	try{
    		sbSql
    		.append(" select cs.*, mk.FCsMarketPrice, mk.FMktValueDate, mk.FMarketStatus, m.FCsPortCury ")
    		.append(" from (select a.FStorageDate, a.FSecurityCode as FCsSecurityCode, ")
    		.append(" FStorageAmount, FStorageCost, a.FPortCode as FCsPortCode, ")
    		.append(" a.FAttrClsCode as FAttrClsCode, sec.FTradeCury as FCsCuryCode, ")
    		.append(" sec.FExchangeCode, sec.FFactor as FCsFactor, FCatCode, FSubCatCode, FInvestType ")
    		.append(" from (select * from ").append(pub.yssGetTableName("tb_stock_security"))
    		.append(" where FCheckState = 1 and FStorageDate = ").append(dbl.sqlDate(dDate))
    		.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode)).append(" ) a ")
    		.append(" join (select FSecurityCode,FSecurityName,FStartDate,FCatCode, ")
    		.append(" FSubCatCode,FTradeCury,FFactor,FHolidaysCode,FExchangeCode  ")
    		.append(" from ").append(pub.yssGetTableName("tb_para_security"))
    		.append(" where FCheckState = 1 and FCatCode = 'FU' ) sec ")
    		.append(" on a.FSecurityCode = sec.FSecurityCode ")
    		.append(" join (select FLinkCode from  ").append(pub.yssGetTableName("Tb_Para_MTVMethodLink"))
    		.append(" where FCheckState = 1 ")
    		.append(" and FMtvCode = ").append(dbl.sqlString(vMethod.getMTVCode()))
    		.append(") b on a.Fsecuritycode = b.FLinkCode) cs ")
    		.append(" left join (select mk2.FCsMarketPrice, mk2.FSecurityCode, mk2.FMarketStatus,mk2.FMktValueDate ")
    		.append(" from (select ").append(vMethod.getMktPriceCode())
    		.append(" as FCsMarketPrice, FSecurityCode, FMktValueDate, FMarketStatus from ").append(tmpMVTable)
    		.append(" where FMktSrcCode = ").append(dbl.sqlString(vMethod.getMktSrcCode()))
    		.append(") mk2) mk on cs.FCsSecurityCode = mk.FSecurityCode ")
    		.append(" left join (select FPortCode, FPortName, FPortCury as FCsPortCury from ")
    		.append(pub.yssGetTableName("Tb_Para_Portfolio"))
    		.append(" where FCheckState = 1) m on cs.FCsPortCode = m.FPortCode ")
    		.append(" join (select FSecuritycode from ").append(pub.yssGetTableName("Tb_Para_IndexFutures"))
    		.append(" where FCheckState = 1 and FBailType = 'Scale') para on cs.FCsSecurityCode =  para.FSecuritycode ");
    		
    		return sbSql.toString();
    	}catch(Exception e){
    		throw new YssException("查询期货估值行情出错",e);
    	}
    }
    
    /**
     * add by songjie 2013.08.26 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 拼接保证金类型为比例的期货保证金调整sql
     * 
     * @return
     * @throws YssException
     */
    private String buildScaleConditionSql() throws YssException{
    	StringBuffer sbSql = new StringBuffer();
    	try{
    		sbSql
    		.append(" select basic.FSecurityCode, basic.FMULTIPLE, ")
    		.append(" case when bailchange.FBailScale is null then basic.FBailScale ")
    		.append(" else bailchange.FBailScale end as FBailScale, ")
    		.append(" st.FStorageAmount, st.FBaseCuryRate, st.FPortCuryRate, st.FPortCode, st.FBrokerCode,st.FExchangeCode, ")
    		.append(" st.FCashAccCode as FCHAGEBAILACCTCODE, st.FStartCashAccCode as FBEGBAILACCTCODE,st.FMarketCode from ")
    		.append(pub.yssGetTableName("Tb_Para_IndexFutures"))
    		.append(" basic join ")
    		.append(" (select distinct stock.*, sec.FExchangeCode, sec.FMarketCode, fcash.FCashAccCode, fcash.FStartCashAccCode ")
    		.append(" from (select FNum as FSecurityCode, FStorageAmount, FBASECURYRATE,FPORTCURYRATE,FPortCode, ")
    		.append(" FBrokerCode from ")
    		.append(pub.yssGetTableName("Tb_Data_Futtraderela"))
    		.append(" where FTransDate = ").append(dbl.sqlDate(dDate))
    		.append(" and FCloseNum = ' ' and FTsfTypeCode like '05FU%' ")
    		.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" and FBrokerCode = ' ' and FStorageAmount <> 0) stock ")
    		.append(" join (select FSecurityCode, FExchangeCode, FMarketCode from ")
    		.append(pub.yssGetTableName("Tb_Para_Security"))
    		.append(" where FCheckstate = 1) sec on stock.FSecurityCode = sec.Fsecuritycode ")
    		.append(" join (select fcashacccode, fstartcashacccode, FBrokerCode, FExchageCode ")
    		.append(" from ").append(pub.yssGetTableName("tb_data_optionsvalcal"))
    		.append(" where FCheckState = 1 ")
    		.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(") fcash on sec.FExchangeCode = fcash.FExchageCode ")
    		.append(" union all ")
    		.append(" select distinct stock.*, sec.FExchangeCode, sec.FMarketCode, fcash.fcashacccode, fcash.fstartcashacccode ")
    		.append(" from (select FNum as FSecurityCode, FStorageAmount, FBASECURYRATE,FPORTCURYRATE,FPortCode ")
    		.append(" ,FBrokerCode from ").append(pub.yssGetTableName("Tb_Data_Futtraderela"))
    		.append(" where FTransDate = ").append(dbl.sqlDate(dDate))
    		.append(" and FCloseNum = ' ' and FTsfTypeCode like '05FU%' ")
    		.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" and FBrokerCode <> ' ' and FStorageAmount <> 0) stock ")
    		.append(" join (select FSecurityCode, FExchangeCode, FMarketCode ")
    		.append(" from ").append(pub.yssGetTableName("Tb_Para_Security"))
    		.append(" where FCheckState = 1) sec on stock.FSecurityCode = sec.Fsecuritycode ")
    		.append(" join (select FCashAccCode, FStartCashAccCode, FBrokerCode, FExchageCode ")
    		.append(" from ").append(pub.yssGetTableName("tb_data_optionsvalcal"))
    		.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(") fcash ")
    		.append(" on stock.FBrokerCode = fcash.fbrokercode and sec.FExchangeCode = fcash.FExchageCode) st  ")
    		.append(" on basic.fsecuritycode = st.FSecurityCode ")
    		.append(" left join (select a.FSecurityCode, a.FBailScale ")
    		.append(" from  ").append(pub.yssGetTableName("tb_data_futurebailchange")).append(" a ")
    		.append(" join (select max(FChangeDate) as FChangeDate, FSecurityCode, FPortCode ")
    		.append(" from ").append(pub.yssGetTableName("tb_data_futurebailchange"))
    		.append(" where FChangeDate <= ").append(dbl.sqlDate(dDate))
    		.append(" and FCheckState = 1 and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" group by FSecurityCode, FPortCode) b  ")
    		.append(" on a.fsecuritycode = b.FSecurityCode and a.FChangeDate = b.FChangeDate ")
    		.append(" and a.FportCode = b.FPortCode) bailchange on basic.FSecurityCode = bailchange.FSecurityCode ")
    		.append(" where basic.FcheckState = 1 and basic.FBailType = 'Scale' ");
    		
    		return sbSql.toString();
    	}catch(Exception e){
    		throw new YssException("拼接保证金类型为比例的期货保证金调整sql出错", e);
    	}
    }
    
    /**
     * add by songjie 2013.09.03 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 拼接保证金类型 = 比例 且 占用保证金来源 = 外部获取  的期货保证金调整sql
     * 
     * @param exchangeCode
     * @param brokerCode
     * @return
     * @throws YssException
     */
    private String buildScaleAndOutSql(String exchangeCode, String brokerCode) throws YssException{
    	StringBuffer sbSql = new StringBuffer();
    	try{
    		sbSql
    		.append(" select sum(sum1.FStorageAmount) as FStorageAmount, sum1.FMULTIPLE, sum1.FBailScale, ")
    		.append(" sum1.FBaseCuryRate, sum1.FPortCuryRate, sum1.FPortCode, sum1.FBrokerCode, ")
    		.append(" sum1.FExchangeCode, sum1.FCHAGEBAILACCTCODE, sum1.FBEGBAILACCTCODE, sum1.FMarketCode from ( ")
    		.append(" select basic.FSecurityCode, basic.FMULTIPLE, ")
    		.append(" case when bailchange.FBailScale is null then basic.FBailScale ")
    		.append(" else bailchange.FBailScale end as FBailScale, ")
    		.append(" st.FStorageAmount, st.FBaseCuryRate, st.FPortCuryRate, st.FPortCode, st.FBrokerCode,st.FExchangeCode, ")
    		.append(" st.FCashAccCode as FCHAGEBAILACCTCODE, st.FStartCashAccCode as FBEGBAILACCTCODE,st.FMarketCode from ")
    		.append(pub.yssGetTableName("Tb_Para_IndexFutures"))
    		.append(" basic join ")
    		.append(" (select stock.*, sec.FExchangeCode, sec.FMarketCode, fcash.FCashAccCode, fcash.FStartCashAccCode ")
    		.append(" from (select FNum as FSecurityCode, FStorageAmount, FBASECURYRATE,FPORTCURYRATE,FPortCode, ")
    		.append(" FBrokerCode from ")
    		.append(pub.yssGetTableName("Tb_Data_Futtraderela"))
    		.append(" where FTransDate = ").append(dbl.sqlDate(dDate))
    		.append(" and FCloseNum = ' ' and FTsfTypeCode like '05FU%' ")
    		.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" and FBrokerCode = ' ' and FStorageAmount <> 0) stock ")
    		.append(" join (select FSecurityCode, FExchangeCode, FMarketCode from ")
    		.append(pub.yssGetTableName("Tb_Para_Security"))
    		.append(" where FCheckstate = 1) sec on stock.FSecurityCode = sec.Fsecuritycode ")
    		.append(" join (select fcashacccode, fstartcashacccode, FBrokerCode, FExchageCode ")
    		.append(" from ").append(pub.yssGetTableName("tb_data_optionsvalcal"))
    		.append(" where FCheckState = 1 ")
    		.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(") fcash on sec.FExchangeCode = fcash.FExchageCode ")
    		.append(" union all ")
    		.append(" select stock.*, sec.FExchangeCode, sec.FMarketCode, fcash.fcashacccode, fcash.fstartcashacccode ")
    		.append(" from (select FNum as FSecurityCode, FStorageAmount, FBASECURYRATE,FPORTCURYRATE,FPortCode ")
    		.append(" ,FBrokerCode from ").append(pub.yssGetTableName("Tb_Data_Futtraderela"))
    		.append(" where FTransDate = ").append(dbl.sqlDate(dDate))
    		.append(" and FCloseNum = ' ' and FTsfTypeCode like '05FU%' ")
    		.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" and FBrokerCode <> ' ' and FStorageAmount <> 0) stock ")
    		.append(" join (select FSecurityCode, FExchangeCode, FMarketCode ")
    		.append(" from ").append(pub.yssGetTableName("Tb_Para_Security"))
    		.append(" where FCheckState = 1) sec on stock.FSecurityCode = sec.Fsecuritycode ")
    		.append(" join (select FCashAccCode, FStartCashAccCode, FBrokerCode, FExchageCode ")
    		.append(" from ").append(pub.yssGetTableName("tb_data_optionsvalcal"))
    		.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(") fcash ")
    		.append(" on stock.FBrokerCode = fcash.fbrokercode and sec.FExchangeCode = fcash.FExchageCode) st  ")
    		.append(" on basic.fsecuritycode = st.FSecurityCode ")
    		.append(" left join (select a.FSecurityCode, a.FBailScale ")
    		.append(" from  ").append(pub.yssGetTableName("tb_data_futurebailchange")).append(" a ")
    		.append(" join (select max(FChangeDate) as FChangeDate, FSecurityCode, FPortCode ")
    		.append(" from ").append(pub.yssGetTableName("tb_data_futurebailchange"))
    		.append(" where FChangeDate <= ").append(dbl.sqlDate(dDate))
    		.append(" and FCheckState = 1 and FPortCode = ").append(dbl.sqlString(this.sPortCode))
    		.append(" group by FSecurityCode, FPortCode) b  ")
    		.append(" on a.fsecuritycode = b.FSecurityCode and a.FChangeDate = b.FChangeDate ")
    		.append(" and a.FportCode = b.FPortCode) bailchange on basic.FSecurityCode = bailchange.FSecurityCode ")
    		.append(" where basic.FcheckState = 1 and basic.FBailType = 'Scale' ")
    		.append(" ) sum1 ")
    		.append(" where 1 = 1 ")
    		.append(exchangeCode.equals("") ? "" : (" and FExchangeCode = " + dbl.sqlString(exchangeCode)))
    		.append(brokerCode.equals("") ? "" : (" and FBrokerCode = " + dbl.sqlString(brokerCode)))
    		.append(" group by sum1.FMULTIPLE, sum1.FBailScale, sum1.FBaseCuryRate, sum1.FPortCuryRate, ")
    		.append(" sum1.FPortCode, sum1.FBrokerCode, sum1.FExchangeCode, sum1.FCHAGEBAILACCTCODE, ")
    		.append(" sum1.FBEGBAILACCTCODE, sum1.FMarketCode ")
    		;
    		
    		return sbSql.toString();
    	}catch(Exception e){
    		throw new YssException("拼接保证金类型为比例的期货保证金调整sql出错", e);
    	}
    }
    
    /**
     * add by songjie 2013.09.02 
     * STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001
     * 
     * 获取保证金类型 = 固定 且 占用保证金来源 = 外部获取 情况对应的sql
     * 
     * @return
     * @throws YssException
     */
    private String getTransOfFixAndOutSql(String exchangeCode, String brokerCode) throws YssException {
        StringBuffer buff = new StringBuffer();
        try{
            buff
            .append(" select sum1.FPortCode, sum1.FBrokerCode, sum1.FInvmgrcode, sum1.FBASECURYRATE, sum1.FPORTCURYRATE, ")
            .append(" sum1.FBegBailAcctCode, sum1.FChageBailAcctCode,sum1.FExchangeCode, sum1.FMarketCode, ")
            .append(" sum(sum1.FChangeMoney) as FChangeMoney, sum(sum1.FStorageAmount) as FStorageAmount ")
            .append(" from (select a.FSecurityCode, a.FPortCode, a.FBrokerCode, a.FInvMgrCode,  ")
            .append(" a.Fbegbailacctcode, a.Fchagebailacctcode, sec.FExchangeCode, ")
            .append(" sec.FMarketCode, a.Fchangemoney, s.FStorageAmount,s.FBASECURYRATE,s.FPORTCURYRATE from ")
            .append(pub.yssGetTableName("tb_data_futurebailchange")).append(" a ")//期货保证金调整表
            .append(" join (select * from ")
            .append(pub.yssGetTableName("tb_stock_security"))//证券库存表，主要是获取前一天的库存数量
            .append(" where FcheckState = 1")
            .append(" and FStorageDate =").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)))
            .append(" and FPortCode =").append(dbl.sqlString(this.sPortCode))
            .append(" ) s on a.fsecuritycode = s.fsecuritycode")
            .append(" and a.fportcode = s.fportcode")
            .append(" join (select fsecuritycode from ")
            .append(pub.yssGetTableName("Tb_Para_IndexFutures"))
            .append(" where FcheckState = 1 and FBailType = 'Fix') para on a.fsecuritycode = para.fsecuritycode ")
            .append(" join (select FSecurityCode,FExchangeCode,FMarketCode from ")
            .append(pub.yssGetTableName("Tb_Para_Security"))
            .append(" where FCheckState = 1) sec on sec.FSecuritycode = a.FSecurityCode ")
            .append(" where a.fportcode =").append(dbl.sqlString(this.sPortCode))
            .append(" and a.fcheckstate = 1")
            .append(" and a.fchangedate = ").append(dbl.sqlDate(this.dDate))
            .append(exchangeCode.equals("")? "" : (" and sec.FExchangeCode = " + dbl.sqlString(exchangeCode)))
            .append(brokerCode.equals("") ? "" : (" and a.FBrokerCode = " + dbl.sqlString(brokerCode)))
            .append(" ) sum1 group by sum1.FPortCode, sum1.FBrokerCode, sum1.FInvMgrCode, ")
            .append(" sum1.FBegBailAcctCode, sum1.FChageBailAcctCode, sum1.FExchangeCode, ")
            .append(" sum1.FMarketCode,sum1.FBASECURYRATE,sum1.FPORTCURYRATE ")
            ;
            
            return buff.toString();
        }catch(Exception e){
            throw new YssException("获取保证金类型 = 固定 且 占用保证金来源 = 外部获取 情况对应的sql出错！",e);
        }
    }
    
    /**从期货保证金调整表，库存表，期货交易数据表中，获取调整保证金金额，库存数量，当天之前所有交易的保证金总额的sql语句
     * getChangeMoneyStorageMountTotalBegBailMoney
     *
     * @return String
     */
    private String getChangeMoneyStorageMountTotalBegBailMoney() throws YssException {
        StringBuffer buff=null;
        try{
            //中间SQL语句的 union all 保证金总额算法=今天之前所有开仓保证金总额-今天之前所有平仓保证金总额+今天之前所有保证金调整资金调拨额
            //库存数量=库存表中前一日库存数量
            //条件：证券代码，组合代码，投资经理，券商
            buff=new StringBuffer();
            //edit by songjie 2013.09.03 STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001 添加交易所代码
            buff.append(" select a.*, s.*, trade.*,sec.FExchangeCode from ");
            buff.append(pub.yssGetTableName("tb_data_futurebailchange")).append(" a ");//期货保证金调整表
            buff.append(" join (select * from ");
            buff.append(pub.yssGetTableName("tb_stock_security"));//证券库存表，主要是获取前一天的库存数量
            buff.append(" where FcheckState = 1");
            buff.append(" and FStorageDate =").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));
            buff.append(" and FPortCode =").append(dbl.sqlString(this.sPortCode));
            buff.append(" ) s on a.fsecuritycode = s.fsecuritycode");
            buff.append(" and a.fportcode = s.fportcode");//" and a.fbrokercode = s.FAnalysisCode2 and a.finvmgrcode = s.FAnalysisCode1");
            buff.append(" join (select sum(FBegBailMoney) as TotalBailMoney,");
            buff.append(" t.fsecuritycode,t.fportcode");//先删除条件投资经理和券商,t.fbrokercode,t.finvmgrcode
            buff.append(" from (select sum(FBegBailMoney) as FBegBailMoney,");
            buff.append(" f.fsecuritycode,f.fportcode from ");//先删除条件投资经理和券商,t.fbrokercode,t.finvmgrcode
            buff.append(pub.yssGetTableName("tb_data_futurestrade")).append(" f ");//期货交易数据表，主要是获取今天之前的所有开仓交易的初始保证金总额
            buff.append(" where FcheckState = 1");
            buff.append(" and FBargainDate <").append(dbl.sqlDate(this.dDate));
            buff.append(" and FTradeTypeCode = '20'");//开仓
            buff.append(" and FPortCode =").append(dbl.sqlString(this.sPortCode));
            buff.append(" group by f.fsecuritycode,f.fportcode");//先删除条件投资经理和券商,t.fbrokercode,t.finvmgrcode
            buff.append(" union all ");
            buff.append(" select sum(FBegBailMoney * -1) as FBegBailMoney,");
            buff.append(" f.fsecuritycode,f.fportcode from ");//先删除条件投资经理和券商,t.fbrokercode,t.finvmgrcode
            buff.append(pub.yssGetTableName("tb_data_futurestrade")).append(" f ");//期货交易数据表，主要是获取今天之前的所有平仓交易的初始保证金总额
            buff.append(" where FcheckState = 1");
            buff.append(" and FBargainDate <").append(dbl.sqlDate(this.dDate));
            buff.append(" and FTradeTypeCode = '21'");//平仓
            buff.append(" and FPortCode =").append(dbl.sqlString(this.sPortCode));
            buff.append(" group by f.fsecuritycode,f.fportcode");//先删除条件投资经理和券商,t.fbrokercode,t.finvmgrcode
            buff.append(" union all ");
            buff.append(" select sum(FMoney) as FBegBailMoney,");
            buff.append(" fer.FSecurityCode,fer.FPortCode");//先删除条件投资经理和券商,fer.FAnalysisCode2 as fbrokercode,fer.FAnalysisCode1 as finvmgrcode
            buff.append(" from (select tf.*, sub.* from ");
            buff.append(pub.yssGetTableName("tb_cash_transfer")).append(" tf ");//资金调拨表，主要是获取，保证金调整产生的资金调拨
            buff.append(" join (select * from ").append(pub.yssGetTableName("tb_cash_subtransfer"));//资金调拨子表
            buff.append(" where FCheckState = 1 ");
            buff.append(" and FPortCode =").append(dbl.sqlString(this.sPortCode));
            buff.append(" and FInOut = 1");//方向为流入
            buff.append(" ) sub on tf.fnum = sub.fnum");
            buff.append(" where tf.FcheckState = 1");
            buff.append(" and tf.FTransferDate <").append(dbl.sqlDate(this.dDate));
            buff.append(" and tf.FSubTsfTypeCode = '0005'").append(") fer");//调拨类型写死了，为0005-保证金调整类型
            buff.append(" group by fer.FSecurityCode,fer.FPortCode");//先删除条件投资经理和券商,fer.FAnalysisCode2 as fbrokercode,fer.FAnalysisCode1 as finvmgrcode
            buff.append(" ) t");
            buff.append(" group by t.fsecuritycode, t.fportcode");//先删除条件投资经理和券商, t.fbrokercode, t.finvmgrcode
            buff.append(" ) trade on a.fsecuritycode = trade.fsecuritycode");
            buff.append(" and a.fportcode = trade.fportcode");// and a.fbrokercode = trade.fbrokercode and a.finvmgrcode = trade.finvmgrcode");
            //--- add by songjie 2013.08.26 STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001 start---//
            buff.append(" join (select fsecuritycode from ");
            buff.append(pub.yssGetTableName("Tb_Para_IndexFutures"));
            buff.append(" where FcheckState = 1 and FBailType = 'Fix') para on a.fsecuritycode = para.fsecuritycode ");
            buff.append(" join (select FSecurityCode,FExchangeCode from " + pub.yssGetTableName("Tb_Para_Security"));
            buff.append(" where FCheckState = 1) sec on sec.FSecurityCode = a.FSecurityCode ");
            //--- add by songjie 2013.08.26 STORY #4152 需求上海-[开发部]QDIIV4[低]20130704001 end---//
            buff.append(" where a.fportcode =").append(dbl.sqlString(this.sPortCode));
            buff.append(" and a.fcheckstate = 1");
            buff.append(" and a.fchangedate = ").append(dbl.sqlDate(this.dDate));

            return buff.toString();
        }catch(Exception e){
            throw new YssException("从期货保证金调整表，库存表，期货交易数据表中，获取调整保证金金额，库存数量，当天之前所有交易的保证金总额出错！",e);
        }
    }

    /**
     * 设置资金调拨子表数据出错
     * @param rs ResultSet
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @param type String
     * @return TransferSetBean
     * @throws YssException
     */
    private TransferSetBean setTransferSetAttr(ResultSet rs, boolean analy1,
                                               boolean analy2, boolean analy3,
                                               String type) throws YssException {
        TransferSetBean transferset = new TransferSetBean();
        double dBaseRate = 0; //基础汇率
        double dPortRate = 0; //组合汇率
        double money = 0.0; //调拨金额
        try {
            dBaseRate = rs.getDouble("FBaseCuryRate");//赋值基础汇率
            dPortRate = rs.getDouble("FPortCuryRate");//赋值组合汇率

            transferset.setSPortCode(rs.getString("FPortCode")); //设置组合代码
            if (analy1) {
                transferset.setSAnalysisCode1(rs.getString("FInvMgrCode")); //先删除投资经理
            }else{
                transferset.setSAnalysisCode1(" ");
            }
            if (analy2) {
                transferset.setSAnalysisCode2(rs.getString("FBrokerCode")); //先删除券商代码
            }else{
                transferset.setSAnalysisCode2(" ");
            }
            if (type.equalsIgnoreCase("KC")) { //开仓的设置，流出。
                transferset.setSCashAccCode(rs.getString("FChageBailAcctCode")); //变动保证金，流出
                //流出金额=调整保证金金额*昨日库存数量-当天之前所有交易的初始保证金总额
                money =YssD.sub(YssD.mul(rs.getDouble("FChangeMoney"),rs.getDouble("FStorageAmount")),rs.getDouble("TotalBailMoney"));
                transferset.setIInOut(-1);//调拨方向 流出
            } else if (type.equalsIgnoreCase("KCWITHFEE")) { //开仓的设置，流入。
                transferset.setSCashAccCode(rs.getString("FBegBailAcctCode")); //初始保证金，流入
                //流入金额=调整保证金金额*昨日库存数量-当天之前所有交易的初始保证金总额
                money = YssD.sub(YssD.mul(rs.getDouble("FChangeMoney"),rs.getDouble("FStorageAmount")),rs.getDouble("TotalBailMoney"));
                transferset.setIInOut(1);//调拨方向 流入
            }
            transferset.setDMoney(money);//设置调拨金额
            transferset.setDBaseRate(dBaseRate);//设置基础汇率
            transferset.setDPortRate(dPortRate);//设置组合汇率

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
    private TransferBean setTransferAttr(ResultSet rs,String type) throws
        YssException {
        TransferBean transfer = new TransferBean();//调拨类型实例化Bean
        try {
            transfer.setDtTransferDate(this.dDate);//调拨日期
            transfer.setDtTransDate(this.dDate);//业务日期
            if (type.equalsIgnoreCase("IN")) { //帐户间的内部流动设置。
                transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);//设置调拨类型--01
                transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU01_CHM);//设置调拨子类型,调整保证金类型--0005
            }
            transfer.setFNumType("FUChBailMoney");// 设置编号类型
            transfer.setStrSecurityCode(rs.getString("FSecurityCode")); //证券代码

            transfer.checkStateId = 1;

        } catch (Exception ex) {
            throw new YssException("设置资金调拨数据出错!", ex);
        }
        return transfer;
    }
}












