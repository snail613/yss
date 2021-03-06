package com.yss.main.operdeal.opermanage;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.EachGetPubPara;
import com.yss.main.operdata.*;
import com.yss.main.operdata.futures.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.parasetting.SecurityBean;

/**
 * <P> xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
 * 
 * <p>Title:计算期权成本及估值增值 </p>
 *
 * <p>Description:
 *  对期权业务的处理-生成移动加权平均成本数据和生成移动加权估值增值数据，
 *  主要是计算该期权的成本和估值增值，
 *  插入到期权成本以及估值增值表（TB_001_DATA_OptionsCost）
 * </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech </p>
 *
 * <p>Cause: QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持</p>
 * @author xuqiji 20090626
 * @version 1.0
 */
public class OptionsCostAddValueManage
    extends OptionsControlManage {
    private String securityCodes = ""; //存放期权代码
    private String sRelaNums = ""; //存放关联编号
    public OptionsCostAddValueManage() {
    }

    /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate;         //调拨日期
        this.sPortCode = portCode;  //组合
    }

    /**
     * @throws YssException
     */
    public void doOpertion() throws YssException {
        //生成移动加权平均成本数据和生成移动加权估值增值数据
        createCostAddValue();

    }

    /**
     * 生成移动加权平均成本数据和生成移动加权估值增值数据，主要是计算该期权的成本和估值增值，
     * 插入到期权成本以及估值增值表（TB_001_DATA_OptionsCost）
     */
    private void createCostAddValue() throws YssException {
        String sql = "";
        ResultSet rs = null;
        ResultSet rst = null;
        double yesDStorgeAmount = 0; //昨日的库存数量
        double cost = 0; //原币成本
        double baseCost = 0; //基础货币成本
        double portCost = 0; //组合货币成本
        double addValue = 0; //原币估值增值
        double baseAddValue = 0; //基础货币估值增值
        double portAddValue = 0; //组合货币估值增值
        HashMap kcTradedata = new HashMap();//保存当天开仓交易的数据
        OptionsTradeCostRealBean costRealBean = null;
        String sKey = "";
        String tmpKey = "";
        ArrayList costData = null; //保存估值核对的数据
//        HashMap mapPCTrade = new HashMap();//保存当天平仓交易
        double dPCTradeAmount =0;//保存当天平仓交易数据
        OptionsTradeCostRealBean costRealBean1 = null;
        try {
        	//删除历史数据（Tb_001_Data_OptionsCost、Tb_001_Data_SecRecPay）
        	sql = " delete from " + pub.yssGetTableName("TB_Data_OptionsCost")
        	      + " where fdate = " + dbl.sqlDate(this.dDate) 
        	      + " and FPortCode = " + dbl.sqlString(this.sPortCode);
        	dbl.executeSql(sql);
        	
        	sql = " delete from " + pub.yssGetTableName("TB_Data_SecRecPay")
		  	      + " where ftransdate = " + dbl.sqlDate(this.dDate) 
		  	      + " and FPortCode = " + dbl.sqlString(this.sPortCode)
		  	      + " and fsubtsftypecode in ('09FP01','09FP02')";
        	dbl.executeSql(sql);
        	
            sql = getTheDateTradeAmount(); //获取当天期权交易的方法
            rs = dbl.queryByPreparedStatement(sql);
            while (rs.next()) {
                yesDStorgeAmount = getYesDStorgeAmount(rs.getString("FSecurityCode")); //获取昨日的库存数量
                sql = getYesDCostAddValue(rs.getString("FSecurityCode")); //获取昨日的成本和估值增值
                rst = dbl.queryByPreparedStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                sKey = rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode"); 
                       // + "\f" + rs.getString("FBrokerCode") + "\f" + rs.getString("FInvMgrCode");
                //状态为：01-买入状态开仓，03-卖出状态开仓，当天没有估值增值
                if (rs.getString("FCloseNum").equalsIgnoreCase("01") || rs.getString("FCloseNum").equalsIgnoreCase("03")) {
                    //原币成本=买入数量*交易价格
                    cost = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), rs.getDouble("FTradePrice"),rs.getDouble("fmultiple")),2);
                    cost = this.getCostByPara(yesDStorgeAmount, rs, cost);
                    //基础货币成本=原币成本*基础汇率
                    baseCost = YssD.round(YssD.mul(rs.getDouble("FBaseCuryRate"), cost),2);
                    //组合货币成本=原币成本*基础汇率/组合汇率
                    portCost = YssD.round(YssD.div(YssD.mul(rs.getDouble("FBaseCuryRate"), cost), rs.getDouble("FPortCuryRate")),2);
                    addValue = 0; //原币估值增值
                    baseAddValue = 0; //基础货币估值增值
                    portAddValue = 0; //组合货币估值增值
                    costData = setDataInCostAddValueBean(rs, cost, baseCost, portCost, addValue, baseAddValue, portAddValue);
                    insertOptionsCostAddValue(costData); // 插入到期权成本以及估值增值表（TB_001_DATA_OptionsCost）
                    
                    if(kcTradedata.containsKey(sKey)){
                    	costRealBean = (OptionsTradeCostRealBean) kcTradedata.get(sKey);
                    	costRealBean.setFCuryCost(YssD.add(costRealBean.getFCuryCost(),cost));
                    	costRealBean.setFBaseCuryCost(YssD.add(costRealBean.getFBaseCuryCost(),baseCost));
                    	costRealBean.setFPortCuryCost(YssD.add(costRealBean.getFPortCuryCost(),portCost));
                    	costRealBean.setDTradeAmount(YssD.add(rs.getDouble("FTradeAmount"),costRealBean.getDTradeAmount()));
                    }else{
                    	kcTradedata.put(sKey,(OptionsTradeCostRealBean)costData.get(0));
                    }
                } else if (rs.getString("FCloseNum").equalsIgnoreCase("02") || rs.getString("FCloseNum").equalsIgnoreCase("04")) { //02-买入状态平仓 ，04-卖出状态平仓，
                	if(rst.next()){
                		if(kcTradedata.containsKey(sKey)){
                			costRealBean = (OptionsTradeCostRealBean) kcTradedata.get(sKey);
                			costRealBean.setDTradeAmount(YssD.add(yesDStorgeAmount,costRealBean.getDTradeAmount()));
                			costRealBean.setFCuryCost(YssD.add(rst.getDouble("FStorageCost"),costRealBean.getFCuryCost()));
                			costRealBean.setFBaseCuryCost(YssD.add(rst.getDouble("FBaseCuryCost"),costRealBean.getFBaseCuryCost()));
                			costRealBean.setFPortCuryCost(YssD.add(rst.getDouble("FPortCuryCost"),costRealBean.getFPortCuryCost()));
                		}else{
                			costRealBean = new OptionsTradeCostRealBean();
                			costRealBean.setDTradeAmount(yesDStorgeAmount);
                			costRealBean.setFCuryCost(rst.getDouble("FStorageCost"));
                			costRealBean.setFBaseCuryCost(rst.getDouble("FBaseCuryCost"));
                			costRealBean.setFPortCuryCost(rst.getDouble("FPortCuryCost"));
                		}
                		rst.previous();
                	}else{
                		costRealBean.setDTradeAmount(costRealBean.getDTradeAmount());
            			costRealBean.setFCuryCost(costRealBean.getFCuryCost());
            			costRealBean.setFBaseCuryCost(costRealBean.getFBaseCuryCost());
            			costRealBean.setFPortCuryCost(costRealBean.getFPortCuryCost());
                	}
                	
                	if(!(rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode")).equalsIgnoreCase(tmpKey)){
                		costRealBean1 = (OptionsTradeCostRealBean)costRealBean.clone();           	
                    }
                	tmpKey = rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode"); 

            		if(rs.getDouble("FTradeAmount") == costRealBean1.getDTradeAmount()){
            			cost = costRealBean1.getFCuryCost();
						baseCost = costRealBean1.getFBaseCuryCost();
						portCost = costRealBean1.getFPortCuryCost();
            		}else{
                        //成本=（昨日成本 + 当天开仓交易成本）/（昨日库存+当天开仓数量）*卖出数量，考虑当天的交易数量，
                        cost = 
                        		YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), 
	                       				   YssD.div(
	                     						costRealBean.getFCuryCost(), 
	                     						costRealBean.getDTradeAmount()
	             						   )), 
                						   2);
                        baseCost = 
                        	       YssD.round(
                        			YssD.mul(rs.getDouble("FTradeAmount"), 
	                       				 YssD.div(
	                     						costRealBean.getFBaseCuryCost(), 
	                     						costRealBean.getDTradeAmount()
	             						 )),
	             						 2);
                        portCost = 
                        	       YssD.round(
                                	YssD.mul(rs.getDouble("FTradeAmount"), 
		                       				 YssD.div(
		                     						costRealBean.getFPortCuryCost(), 
		                     						costRealBean.getDTradeAmount()
		             						 )),
		             						 2);
            		}  
            		if(rst.next()){
	                    //原币移动加权计算估值增值=昨日估值增值余额/昨日库存*卖出数量
	                    addValue = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rst.getDouble("FVBal"), costRealBean.getDTradeAmount())),2);
	                    //基础货币移动加权计算估值增值=昨日基础货币估值增值/昨日库存*卖出数量
	                    baseAddValue = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rst.getDouble("FVBaseCuryBal"), costRealBean.getDTradeAmount())),2);
	                    //组合货币移动加权计算估值增值=昨日组合货币估值增值/昨日库存*卖出数量
	                    portAddValue = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rst.getDouble("FVPortCuryBal"), costRealBean.getDTradeAmount())),2);
	                    rst.previous();
            		}
                    costRealBean1.setDTradeAmount(YssD.sub(costRealBean1.getDTradeAmount(), rs.getDouble("FTradeAmount")));
                    costRealBean1.setFCuryCost(YssD.sub(costRealBean1.getFCuryCost(), cost));
                    costRealBean1.setFBaseCuryCost(YssD.sub(costRealBean1.getFBaseCuryCost(), baseCost));
                    costRealBean1.setFPortCuryCost(YssD.sub(costRealBean1.getFPortCuryCost(),portCost));
                    
                    //把数据保存到 估值核对的POJO类中
                    costData = setDataInCostAddValueBean(rs, cost, baseCost, portCost, addValue, baseAddValue, portAddValue);
                    //把卖出和行权的数据插入到期权成本以及估值增值表
                    insertOptionsCostAddValue(costData);
                    if(rst.next()){
	                    //把当天交易的估值增值插入到 证券应收应付表，冲减到一笔应收应付
	                    insertSecPecPay(costData, rs, rst, yesDStorgeAmount);
                    }
                    
                } else if (rs.getString("FCloseNum").equalsIgnoreCase("05")) { //05-期权行权
                    if (rst.next()) {
                        if (Math.abs(rs.getDouble("FTradeAmount")) > Math.abs(yesDStorgeAmount)) {
                            throw new YssException("昨日库存不足，无法行权！");
                        } else {
                        	if(kcTradedata.containsKey(sKey)){
                        		costRealBean = (OptionsTradeCostRealBean) kcTradedata.get(sKey);
                        	}
                            //成本=（昨日成本 + 当天开仓交易成本）/（昨日库存+当天开仓数量）*卖出数量，考虑当天的交易数量，
                            cost = YssD.round(
                            		YssD.mul(rs.getDouble("FTradeAmount"), 
                            				YssD.div(
                            						YssD.add(rst.getDouble("FStorageCost"),costRealBean!=null ? costRealBean.getFCuryCost():0), 
                            						YssD.add(yesDStorgeAmount,costRealBean!=null ? costRealBean.getDTradeAmount():0))),
                            						2);
                            cost = this.getCostByPara(yesDStorgeAmount, rs, cost);
                            baseCost = YssD.round(
                            		YssD.mul(rs.getDouble("FTradeAmount"), 
                            				YssD.div(YssD.add(rst.getDouble("FBaseCuryCost"),costRealBean!=null ? costRealBean.getFBaseCuryCost():0), 
                            						YssD.add(yesDStorgeAmount,costRealBean!=null ? costRealBean.getDTradeAmount():0))),
                            						2);
                            portCost = YssD.round(
                            		YssD.mul(rs.getDouble("FTradeAmount"), 
                            				YssD.div(YssD.add(rst.getDouble("FPortCuryCost"),costRealBean!=null ? costRealBean.getFPortCuryCost():0)
                            						, YssD.add(yesDStorgeAmount,costRealBean!=null ? costRealBean.getDTradeAmount():0))),2);
                            //原币移动加权计算估值增值=昨日估值增值余额/昨日库存*行权数量
                            addValue = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rst.getDouble("FVBal"), yesDStorgeAmount)),2);
                            //基础货币移动加权计算估值增值=昨日基础货币估值增值/昨日库存*行权数量
                            baseAddValue = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rst.getDouble("FVBaseCuryBal"), yesDStorgeAmount)),2);
                            //组合货币移动加权计算估值增值=昨日组合货币估值增值/昨日库存*行权数量
                            portAddValue = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rst.getDouble("FVPortCuryBal"), yesDStorgeAmount)),2);

                        }
                        //把数据保存到 估值核对的POJO类中
                        costData = setDataInCostAddValueBean(rs, cost, baseCost, portCost, addValue, baseAddValue, portAddValue);
                        //把卖出和行权的数据插入到期权成本以及估值增值表
                        insertOptionsCostAddValue(costData);
                        //把当天交易的估值增值插入到 证券应收应付表，冲减到一笔应收应付
                        insertSecPecPay(costData, rs, rst, yesDStorgeAmount);
                    }
                }else if(rs.getString("FCloseNum").equalsIgnoreCase("06")){//06-期权结算，
               	 if (rst.next()) {
                     //成本=昨日核算成本
                     cost = rst.getDouble("FStorageCost");
                     baseCost = rst.getDouble("FBaseCuryCost");
                     portCost = rst.getDouble("FPortCuryCost");

                     //原币移动加权计算估值增值=昨日估值增值余额
                     addValue = rst.getDouble("FVBal");
                     //基础货币移动加权计算估值增值=昨日基础货币估值增值
                     baseAddValue = rst.getDouble("FVBaseCuryBal");
                     //组合货币移动加权计算估值增值=昨日组合货币估值增值
                     portAddValue = rst.getDouble("FVPortCuryBal");

                     //把数据保存到 估值核对的POJO类中
                     costData = setDataInCostAddValueBean(rs, cost, baseCost, portCost, addValue, baseAddValue, portAddValue);
                     //把卖出和行权的数据插入到期权成本以及估值增值表
                     insertOptionsCostAddValue(costData);
                     //把当天交易的估值增值插入到 证券应收应付表，冲减到一笔应收应付
                     insertSecPecPay(costData, rs, rst, yesDStorgeAmount);
                 }
                }else if (rs.getString("FCloseNum").equalsIgnoreCase("07")) { //07-期权放弃行权
                	 if (rst.next()) {
                         if (Math.abs(rs.getDouble("FTradeAmount")) > Math.abs(yesDStorgeAmount)) {
                             throw new YssException("昨日库存不足，无法放弃行权！");
                         } else {
                        	 if(kcTradedata.containsKey(sKey)){
                         		costRealBean = (OptionsTradeCostRealBean) kcTradedata.get(sKey);
                        	 }
                             //成本=（昨日成本 + 当天开仓交易成本）/（昨日库存+当天开仓数量）*卖出数量，考虑当天的交易数量，
                             cost = YssD.round(
                             		YssD.mul(rs.getDouble("FTradeAmount"), 
                             				YssD.div(
                             						YssD.add(rst.getDouble("FStorageCost"),costRealBean!=null ? costRealBean.getFCuryCost():0), 
                             						YssD.add(yesDStorgeAmount,costRealBean!=null ? costRealBean.getDTradeAmount():0))),
                             						2);
                             baseCost = YssD.round(
                             		YssD.mul(rs.getDouble("FTradeAmount"), 
                             				YssD.div(YssD.add(rst.getDouble("FBaseCuryCost"),costRealBean!=null ? costRealBean.getFBaseCuryCost():0), 
                             						YssD.add(yesDStorgeAmount,costRealBean!=null ? costRealBean.getDTradeAmount():0))),
                             						2);
                             portCost = YssD.round(
                             		YssD.mul(rs.getDouble("FTradeAmount"), 
                             				YssD.div(YssD.add(rst.getDouble("FPortCuryCost"),costRealBean!=null ? costRealBean.getFPortCuryCost():0)
                             						, YssD.add(yesDStorgeAmount,costRealBean!=null ? costRealBean.getDTradeAmount():0))),2);

                             //原币移动加权计算估值增值=昨日估值增值余额/昨日库存*行权数量
                             addValue = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rst.getDouble("FVBal"), yesDStorgeAmount)),2);
                             //基础货币移动加权计算估值增值=昨日基础货币估值增值/昨日库存*行权数量
                             baseAddValue = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rst.getDouble("FVBaseCuryBal"), yesDStorgeAmount)),2);
                             //组合货币移动加权计算估值增值=昨日组合货币估值增值/昨日库存*行权数量
                             portAddValue = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rst.getDouble("FVPortCuryBal"), yesDStorgeAmount)),2);

                         }
                         //把数据保存到 估值核对的POJO类中
                         costData = setDataInCostAddValueBean(rs, cost, baseCost, portCost, addValue, baseAddValue, portAddValue);
                         //把卖出和行权的数据插入到期权成本以及估值增值表
                         insertOptionsCostAddValue(costData);
                         //把当天交易的估值增值插入到 证券应收应付表，冲减到一笔应收应付
                         insertSecPecPay(costData, rs, rst, yesDStorgeAmount);
                     }
                }                
                dbl.closeResultSetFinal(rst); //外围的Rs每循环一次，关闭内层游标
            }
        } catch (Exception e) {
            throw new YssException("生成移动加权平均成本数据和生成移动加权估值增值数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rst);
            dbl.closeResultSetFinal(rs);
        }
    }

   /**
    * saveCashTransferData 产生一笔资金调拨
    *
    * @param costData ArrayList
    * @param rs ResultSet
    * @param rst ResultSet
    */
   private void saveCashTransferData(ArrayList costData, ResultSet rs, ResultSet rst) throws YssException {
      ArrayList cashTransData=new ArrayList();
      CashTransAdmin cashtrans = null; //初始化资金调拨操作类
      String filtersRelaNums = ""; //保存交易编号
      boolean bTrans = false; //事务控制
      Connection conn = dbl.loadConnection(); //获取连接
      try{
         cashTransData=getTheDayTradeData(costData,rs,rst);
         if (cashTransData.size() > 0) { //是否当天有交易
            cashtrans = new CashTransAdmin();
            cashtrans.setYssPub(pub);
            cashtrans.addList(cashTransData); //子资金调拨的值放入TransferBean中的arrayList中
            if (sRelaNums.length() > 0 && sRelaNums.endsWith(",")) {
               filtersRelaNums = this.sRelaNums.substring(0,
                     sRelaNums.length() - 1); //除去最后的","号
            }
            //增加事务控制和锁，以免在多用户同时处理时出现调拨编号重复
            conn.setAutoCommit(false); //设置不自动提交事务
            bTrans = true;
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer")); //给操作的表加锁
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));
            cashtrans.insert(this.dDate, "OptionsTrade", filtersRelaNums); //插入数据到资金调拨表和子表中
            conn.commit(); //提交事务
            conn.setAutoCommit(true); //设置为自动提交事务
            bTrans = false;
         }
      }catch(Exception e){
         throw new YssException(e.getMessage());
      }finally{
         dbl.endTransFinal(conn,bTrans);
      }
   }
   /**
    * 查询期权交易关联表数据，把数据set到TransferBean和TransferSetBean中
    * @return ArrayList
    * @throws YssException
    */
   private ArrayList getTheDayTradeData(ArrayList costData, ResultSet rs, ResultSet rst) throws YssException {
       ArrayList curCashTransArr = null;//保存资金调拨数据
       TransferBean transfer = null;//调拨类初始化
       TransferSetBean transferset = null;//调拨子类初始化
       ArrayList subtransfer = null;//保存资金子调拨
       boolean analy1;//分析代码1
       boolean analy2;//分析代码2
       boolean analy3;//分析代码3
       try {
           analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
           analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
           analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
           curCashTransArr = new ArrayList();

           if (rs.getString("FTradeTypeCode").equals("01")) { //买入
              subtransfer = new ArrayList();
              transfer = setTransferAttr(rs, "QSKHC"); //资金调拨表，清算款划出
              transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                                               "QSKOUT",costData); //资金调拨子表，清算款划出
              subtransfer.add(transferset);
              transfer.setSubTrans(subtransfer);
              curCashTransArr.add(transfer);
           }
           else if (rs.getString("FTradeTypeCode").equals("02")) { //卖出
              subtransfer = new ArrayList();
              transfer = setTransferAttr(rs, "QSKHR"); //资金调拨表 清算款划入
              transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                                               "QSKIN",costData); //资金调拨子表，清算款划入
              subtransfer.add(transferset);
              transfer.setSubTrans(subtransfer);
              curCashTransArr.add(transfer);
           }
           else if (rs.getString("FTradeTypeCode").equals("32FP")) { //期权行权
              subtransfer = new ArrayList();
              transfer = setTransferAttr(rs, "QSKHR"); //资金调拨表 清算款划入
              transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                                               "QSKIN",costData); //资金调拨子表，清算款划入
              subtransfer.add(transferset);
              transfer.setSubTrans(subtransfer);
              curCashTransArr.add(transfer);
           }
           else if (rs.getString("FTradeTypeCode").equals("33FP")) { //期权结算
              subtransfer = new ArrayList();
              transfer = setTransferAttr(rs, "QSKHC"); //资金调拨表，清算款划出
              transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                                               "QSKOUT",costData); //资金调拨子表，清算款划出
              subtransfer.add(transferset);
              transfer.setSubTrans(subtransfer);
              curCashTransArr.add(transfer);
           }
           else { //期权放弃行权

           }
       } catch (Exception e) {
           throw new YssException("查询期权交易关联表数据出错！\r\t", e);
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
                                              String type,ArrayList costData) throws YssException {
       TransferSetBean transferset = new TransferSetBean();//调拨子类
       double dBaseRate = 0;//基础汇率
       double dPortRate = 0;//组合汇率
       double money = 0.0;//调拨金额
       SecurityBean security = null;//证券信息类
       OptionsTradeCostRealBean costRealData = null; //期权估值核对表对应的与数据库交互的实体类
       try {
          costRealData = (OptionsTradeCostRealBean) costData.get(0);
          dBaseRate = rs.getDouble("FBaseCuryRate");
          dPortRate = rs.getDouble("FPortCuryRate");

          security = new SecurityBean();
          security.setYssPub(pub);
          security.setSecurityCode(rs.getString("FSecurityCode"));
          security.getSetting();

          transferset.setSPortCode(rs.getString("FPortCode")); //设置组合代码
          if (analy1) {
             transferset.setSAnalysisCode1(rs.getString("FInvMgrCode")); //投资经理
          }
          if (analy2) {
             transferset.setSAnalysisCode2("FBrokerCode"); //券商代码
          }
          if (type.equalsIgnoreCase("QSKIN")) { //平仓清算款流入账户。
             transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
             money = costRealData.getFOriginalAddValue(); //调拨金额
             transferset.setIInOut(1); //调拨方向
          }
          else if (type.equalsIgnoreCase("QSKOUT")) { //开仓的设置，清算款流出账户。
             transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
             money = costRealData.getFOriginalAddValue();
             transferset.setIInOut( -1);
          }
          transferset.setDMoney(money);//调拨金额
          transferset.setDBaseRate(dBaseRate);
          transferset.setDPortRate(dPortRate);

          transferset.checkStateId = 1;
       }
       catch (Exception e) {
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
           if (type.equalsIgnoreCase("QSKHC")) { //划出清算款
               transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Cost);//应付为清算款
               transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_COST);//内部账户转账
           }else if(type.equalsIgnoreCase("QSKHR")){
               transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Cost);//应付为清算款
               transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_COST);//内部账户转账
           }
           transfer.setFRelaNum(rs.getString("FNum")+rs.getString("FCloseNum"));//编号
           transfer.setStrTradeNum(rs.getString("FNum")+rs.getString("FCloseNum"));
           transfer.setFNumType("OptionsTrade");//设置编号类型
           transfer.setStrSecurityCode(rs.getString("FSecurityCode"));//证券代码
           securityCodes += transfer.getStrSecurityCode() + ",";

           transfer.checkStateId = 1;
           sRelaNums += transfer.getFRelaNum() + ",";

       } catch (Exception ex) {
           throw new YssException("设置资金调拨数据出错!", ex);
       }
       return transfer;
   }


   /**
     * insertSecPecPay 把当天交易的估值增值插入到 证券应收应付表，冲减到一笔应收应付
     *
     * @param costData ArrayList
     */
    private void insertSecPecPay(ArrayList costData, ResultSet rs, ResultSet rst, double yesDStorgeAmount) throws YssException {
        SecPecPayBean secPay = null; //证券应收应付Pojo类
        OptionsTradeCostRealBean costRealData = null; //期权估值核对表对应的与数据库交互的实体类
        SecRecPayAdmin secpayAdmin = new SecRecPayAdmin(); //证券应收应付数据库操作类
        secpayAdmin.setYssPub(pub);
        Connection conn = dbl.loadConnection(); //获取连接
        boolean bTrans = true; //连接状态
        String sBailMoneyTransferType ="";
        try {
      	  //通过组合代码获取期权保证金结算方式
            sBailMoneyTransferType = getAccountTypeBy(this.sPortCode);
            for (int i = 0; i < costData.size(); i++) { //判断当天是否有交易数据
                costRealData = (OptionsTradeCostRealBean) costData.get(i);
                secPay = new SecPecPayBean();
                //基础和组合汇率
                secPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                secPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));

                //业务日期
                secPay.setTransDate(this.dDate);
                //--------------------设置原币增值----------------------
                secPay.setMoney(costRealData.getFOriginalAddValue());
                secPay.setMMoney(costRealData.getFOriginalAddValue());
                secPay.setVMoney(costRealData.getFOriginalAddValue());
                //---------------------------------------------------
                //-----------------------------设置基础货币增值------------
                secPay.setBaseCuryMoney(costRealData.getFBaseAddValue());
                secPay.setMBaseCuryMoney(costRealData.getFBaseAddValue());
                secPay.setVBaseCuryMoney(costRealData.getFBaseAddValue());
                //---------------------------------------------------
                //------------------------------设置组合货币增值------------

                secPay.setPortCuryMoney(costRealData.getFPortAddValue());

                secPay.setMPortCuryMoney(costRealData.getFPortAddValue());

                secPay.setVPortCuryMoney(costRealData.getFPortAddValue());
                //-----------------------------------------------------
                if (yesDStorgeAmount >= 0) { //设置调拨类型
                    secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV); //09估值增值
                    secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_MV); //09FP01 期权估值增值
                } else {
                	if(sBailMoneyTransferType.equalsIgnoreCase(YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER)){//保证金每日结转时产生资金调拨
                		secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income); //02收入
                        secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_SR); //02FP01 期权收入
                	}else{
                		secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV); //09估值增值
                        secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_MV); //09FP01 期权估值增值
                	}
                }
                secPay.setStrSecurityCode(rs.getString("FSecurityCode")); //证券代码
                secPay.setStrPortCode(rs.getString("FPortCode")); //组合代码
                secPay.setBrokerCode(rs.getString("FBrokerCode")); //券商代码
                secPay.setInvMgrCode(rs.getString("FInvMgrCode")); //投资经理
                secPay.setStrCuryCode(rst.getString("FCuryCode")); //币种代码
                secPay.setAttrClsCode(rst.getString("FAttrClsCode")); //分类类型
                secPay.setInOutType( -1); //调拨方向
                secPay.setCatTypeCode(rst.getString("FCatType")); //品种类型
                secPay.setMoney(costRealData.getFOriginalAddValue()); //调拨金额
                secPay.setRelaNum(costRealData.getSFNum());
                secPay.checkStateId = 1;
            }
            secpayAdmin.addList(secPay);
            if (secpayAdmin.getList().size() > 0) {
                conn.setAutoCommit(false);
                //把数据插入到证券应收应付表
                secpayAdmin.insert("", this.dDate,
                                   this.dDate, secPay.getStrTsfTypeCode(),
                                   secPay.getStrSubTsfTypeCode(), this.sPortCode, secPay.getInvMgrCode(),
                                   secPay.getBrokerCode(), secPay.getStrSecurityCode(), secPay.getStrCuryCode(),
                                   0, true, secPay.getInOutType(), true,"",secPay.getRelaNum(),"");//0标示数据源状态自动产生 modify by zhouwei 20120414

                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("插入数据到证券应收应付表库存表中出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * setDataInCostAddValueBean 把数据保存到 估值核对的POJO类中
     *
     * @param rs ResultSet
     * @param cost double 原币成本
     * @param baseCost double 基础货币成本
     * @param portCost double 组合货币成本
     * @param addValue double 原币估值增值
     * @param baseAddValue double 基础货币估值增值
     * @param portAddValue double 组合货币估值增值
     */
    private ArrayList setDataInCostAddValueBean(ResultSet rs, double cost, double baseCost, double portCost,
                                                double addValue, double baseAddValue, double portAddValue) throws
        YssException {
        ArrayList costData = new ArrayList(); //返回值
        OptionsTradeCostRealBean costRealData = null; //期权估值核对表的POJO类
        try {
            costRealData = new OptionsTradeCostRealBean();
            costRealData.setSFNum(rs.getString("FNum") + rs.getString("FCloseNum")); //编号
            costRealData.setSFCatType("1"); //品种类型，暂时不用
            costRealData.setFCuryCost(cost); //原币成本
            costRealData.setFBaseCuryCost(baseCost); //基础货币成本
            costRealData.setFPortCuryCost(portCost); //组合货币成本
            costRealData.setFOriginalAddValue(addValue); //原币估值增值
            costRealData.setFBaseAddValue(baseAddValue); //基础货币估值增值
            costRealData.setFPortAddValue(portAddValue); //组合货币估值增值
            costRealData.setScreator(rs.getString("FCreator"));
            costRealData.setSCreatorTime(rs.getString("FCreateTime"));
            costRealData.setScreator(rs.getString("FCheckUser"));
            costRealData.setSCreatorTime(rs.getString("FCheckTime"));
            costRealData.setFBaseCuryRate(rs.getDouble("FBaseCuryRate"));
            costRealData.setFPortCuryRate(rs.getDouble("FPortCuryRate"));
            costRealData.setSDate(YssFun.formatDate(this.dDate,"yyyy-MM-dd"));
            costRealData.setDTradeAmount(rs.getDouble("FTradeAmount"));
            //add by fangjiang 2011.12.05 bug 3311
            costRealData.setPortCode(rs.getString("FPortCode"));
            costRealData.setSSetNum(" ");
            //-------------------
            costData.add(costRealData);
        } catch (Exception e) {
            throw new YssException("把数据保存到估值核对的POJO类中出错！", e);
        }
        return costData;
    }

    /**
     * 把卖出和行权的数据插入到期权成本以及估值增值表
     */
    private void insertOptionsCostAddValue(ArrayList costData) throws YssException {
        Connection conn = null;
        boolean bTrans = true;
        OptionsTradeCostRealBean costRealData = null; //期权估值核对表的POJO类
        try {
            OptionsTradeCostRealAdmin costAddValue = new OptionsTradeCostRealAdmin(); //期权估值核对数据库操作类
            costAddValue.setYssPub(pub);
            conn = dbl.loadConnection(); //获取连接
            conn.setAutoCommit(false); //设置为不自动提交
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_OptionsCost")); //给操作表加锁
            for (int i = 0; i < costData.size(); i++) {
                costRealData = (OptionsTradeCostRealBean) costData.get(i);
                costAddValue.deleteData(costRealData.getSFNum(),costRealData.getSSetNum(),this.dDate); //根据编号删除数据方法
                costAddValue.saveMutliSetting(costRealData); //保存数据方法
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("插入期权卖出和行权数据出错！",e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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

    /**
     * 获取指定证券(期权)昨日的成本和估值增值sql语句
     * 实际上可以获取证券库存的信息、原币估值余额、基础货币估值余额、组合货币估值余额
     * @param string sSecurityCode 要查询的证券代码(期权)
     * @return String 目的SQL语句
     */
    private String getYesDCostAddValue(String sSecurityCode) throws YssException {
    	  //通过组合代码获取期权保证金结算方式
        String sBailMoneyTransferType = getAccountTypeBy(this.sPortCode);
        StringBuffer buff = new StringBuffer();
        //取数逻辑:
        //1.从证券库存中取T-1日审核过的数据，并且根据库存数据来产生调拨类型，库存>=0时取估值增值，否则取收入
        //2.通过第一步获取的信息连接期权信息表，通过证券代码和期权代码匹配，从而查询出期权的信息
        //3.通过1、2部取出的期权相关信息连接证券应收应付库存，通过证券代码、库存日期、调拨类型、组合代码匹配
        //综上步骤，即通过证券库存、期权信息、证券应收应付库存三表匹配取出T-1日的库存信息、原币估值余额、基础货币估值余额、组合货币估值余额
        buff.append(" select stock.*,sec.FVBal,sec.FVBaseCuryBal,sec.FVPortCuryBal from (")
            .append(" select s.*")
            .append(" from ").append(pub.yssGetTableName("Tb_Stock_Security"))
            .append(" s where FCheckState = 1").append(" and FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1))).append(") stock join (");
        buff.append(" select * from ").append(pub.yssGetTableName("tb_para_optioncontract")).append(" where FOptionCode=");
        buff.append(dbl.sqlString(sSecurityCode)).append(" and FCheckState = 1) tract on tract.FOptionCode = stock.fsecuritycode");
        buff.append(" left join (select FSecuritycode, FPortCode, FStorageDate, sum(FVBal) as FVBal, sum(FVBaseCuryBal) as FVBaseCuryBal, sum(FVPortCuryBal) as FVPortCuryBal from ").append(pub.yssGetTableName("tb_stock_secrecpay"));
        buff.append("  where FCheckState = 1 and fstoragedate =").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));
        buff.append(" and FSecuritycode=").append(dbl.sqlString(sSecurityCode));
        buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(") and ftsftypecode in ('09','99') group by FSecuritycode, FPortCode, FStorageDate");
        buff.append(" ) sec on sec.fsecuritycode =stock.fsecuritycode and sec.fstoragedate =stock.FStorageDate");
        buff.append(" and sec.FPortCode =stock.fportcode ");
        buff.append(" where stock.FStorageDate=").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));
        buff.append(" and stock.FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
        buff.append(" and stock.FCheckState = 1");
        return buff.toString();
    }

    /**
     * 获取指定日期、组合的期权交易SQL语句
     * @return String 目的SQL语句
     */
    private String getTheDateTradeAmount() throws YssException {
        //取数逻辑：
        //1.直接从期权交易关联表取已审核的数据，通过日期、组合匹配
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append("SELECT a.*,b.fmultiple ");
        sqlBuf.append(" FROM " + pub.yssGetTableName("TB_Data_OptionsTraderela"));
        sqlBuf.append(" a join(select * from ").append(pub.yssGetTableName("tb_para_optioncontract"));
        sqlBuf.append(" where FCheckState = 1) b on a.fsecuritycode = b.FOptionCode");
        sqlBuf.append(" WHERE a.FCheckState = 1");
        sqlBuf.append(" AND a.FBargainDate = " + dbl.sqlDate(this.dDate));
        sqlBuf.append(" AND a.FPortCode = (").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
        sqlBuf.append(" ORDER by FSecurityCode, FPortCode, FCloseNum, FNum"); //modify by fangjiang 2012.06.01 STORY #2496
        return sqlBuf.toString();
    }
    /**
     * 通过组合代码获取期权保证金结算方式
     * @param sPortCode String：组合代码
     * @return String
     */
    private String getAccountTypeBy(String sPortCode) throws YssException {
    	java.util.Hashtable htAccountType = null;
    	String sResult ="";
    	try{
	        CtlPubPara pubPara = new CtlPubPara();
	        pubPara.setYssPub(pub);
	        htAccountType = pubPara.getOptionBailCarryType("bailcarrytype");//通用参数获取期权保证金结转类型，默认-平仓结转
	        sResult = YssOperCons.YSS_TYCS_BAILMONEY_PCTRANSFER;
	        String sTheDayFirstFIFO = (String) htAccountType.get(YssOperCons.
	        		YSS_TYCS_BAILMONEY_DAYTRANSFER);//获取value值
	        if (sTheDayFirstFIFO != null && sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
	            sResult = YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER;//每日结转
	        }
    	}catch (Exception e) {
			throw new YssException("通过组合代码获取期权保证金结算方式出错！",e);
		}
        return sResult;
    }
    
    //20120816 added by liubo.Story #2754
    private double getCostByPara(double yesDStorgeAmount, ResultSet rs, double cost) throws YssException {  
    	try{
    		EachGetPubPara pubPara = new EachGetPubPara();
            pubPara.setYssPub(pub);
            pubPara.setSPortCode(rs.getString("FPortCode"));
            pubPara.setSPubPara(rs.getString("FSecurityCode"));
            pubPara.setsDate(YssFun.formatDate(this.dDate,"yyyy-MM-dd"));
            
            if(rs.getString("FCloseNum").equalsIgnoreCase("05")){
            	if(yesDStorgeAmount > 0){
            		pubPara.setTradeType("01");
            	}else{
            		pubPara.setTradeType("02");
            	}
        	}else{
        		pubPara.setTradeType(rs.getString("FTRADETYPECODE"));
        	}
            
            //获取“期权成本核算设置”通参中的“是否核算成本”控件的值
            pubPara.setCtlFlag("cboAccountCost");
            String sCostAccount_Para = pubPara.getOptCostAccountSet();
            
            //获取“期权成本核算设置”通参中的“是否有资金流动”控件的值
            pubPara.setCtlFlag("selTransfering");
            String sHaveTranfering_Para = pubPara.getOptCostAccountSet();
            
            if (sCostAccount_Para.equalsIgnoreCase("false") && sHaveTranfering_Para.equalsIgnoreCase("true"))
            {
            	return 0;
            }
            else
            {	
            	return cost;
            }
    	}catch (Exception e) {
			throw new YssException("获取期权通用参数出错！",e);
		}        	
    }
}
