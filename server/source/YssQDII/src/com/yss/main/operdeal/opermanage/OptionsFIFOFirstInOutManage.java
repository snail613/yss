package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.operdata.futures.OptionsFIFOFirstInOutAdmin;
import com.yss.main.operdata.futures.OptionsTradeCostRealAdmin;
import com.yss.main.operdata.futures.pojo.OptionsFIFOFirstInOutBean;
import com.yss.main.operdata.futures.pojo.OptionsTradeCostRealBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.SecurityBean;
import com.yss.manager.CashTransAdmin;
import com.yss.manager.SecRecPayAdmin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 此类处理期权交易先入先出算法，要保存先入先出数据到期权先入先出库存表（Tb_XXX_Data_OptionsFIFOStock）和保存数据到成本和估值增值表
 * @author xuqiji 20100505 
 *
 */
public class OptionsFIFOFirstInOutManage extends BaseOperManage{
	private ArrayList alLastOptionsTradeData = null;
	private ArrayList alCostAddValueData = new ArrayList();
	private String sRelaNums = ""; //存放关联编号
	private SecRecPayAdmin secpayAdmin = null; //证券应收应付数据库操作类
	public OptionsFIFOFirstInOutManage() {
		super();
		secpayAdmin = new SecRecPayAdmin();
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
	 * 入口方法
	 */
	public void doOpertion() throws YssException {
		try{
			doRealOpertion();//处理业务方法
			createOptionsTradeData();//保存处理后的数据
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	/**
	 * 保存处理后的数据
	 * @throws YssException 
	 *
	 */
	private void createOptionsTradeData() throws YssException {
		Connection conn = null;
        boolean bTrans = true;
        OptionsTradeCostRealBean costRealData = null; //期权估值核对表的POJO类
        ArrayList alOptionKCTrade = null;
		try{
            conn = dbl.loadConnection(); //获取连接
            conn.setAutoCommit(false); //设置为不自动提交
			//if(alCostAddValueData.size() > 0){ 
				OptionsTradeCostRealAdmin costAddValue = new OptionsTradeCostRealAdmin(); //期权估值核对数据库操作类
	            costAddValue.setYssPub(pub);
	            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_OptionsCost")); //给操作表加锁
	            costAddValue.deleteData(this.sPortCode,this.dDate); //modify by fangjiang 2011.09.13 story 1342
	            for (int i = 0; i < alCostAddValueData.size(); i++) {
	                costRealData = (OptionsTradeCostRealBean) alCostAddValueData.get(i);
	                //costAddValue.deleteData(costRealData.getSFNum(), costRealData.getSSetNum(), this.dDate); 
	                costAddValue.saveMutliSetting(costRealData); //保存数据方法
	            }
			//}
			//if(alLastOptionsTradeData.size() > 0){
				OptionsFIFOFirstInOutAdmin fifoAdmin = new OptionsFIFOFirstInOutAdmin();
				fifoAdmin.setYssPub(pub);
				dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_OptiFIFOStock"));
				fifoAdmin.deleteData(this.sPortCode,this.dDate);
				for(int i =0; i < alLastOptionsTradeData.size(); i++){
					alOptionKCTrade = (ArrayList) alLastOptionsTradeData.get(i);
					fifoAdmin.savingData(alOptionKCTrade);
				}
			//}
			//if (secpayAdmin.getList().size() > 0) {
                //把数据插入到证券应收应付表
				secpayAdmin.setYssPub(pub);
                secpayAdmin.insert("", this.dDate,
                                   this.dDate, YssOperCons.YSS_ZJDBLX_MV + "," + YssOperCons.YSS_ZJDBLX_Income,
                                   YssOperCons.YSS_ZJDBZLX_FP01_MV + "," + YssOperCons.YSS_ZJDBZLX_FP01_SR, this.sPortCode,"",
                                   "", "", "",
                                   0, true, 0, true);//0标示数据源状态自动产生 modify by zhouwei 20120414
            //}
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
			
		}catch (Exception e) {
			throw new YssException("保存处理后的数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	/**
	 * 实际处理业务的方法
	 * @throws YssException 
	 *
	 */
	private void doRealOpertion() throws YssException {
		HashMap OptionKCTrade = null;//保存期权昨日和今日开仓交易数据，按照成交顺序保存，从期权先入先出库存表获取数据
		try{
			OptionKCTrade = getOptionsKCTrade();//获取期权昨日和今日开仓交易数据
			doTheDayOptionsPCTradeData(OptionKCTrade);//处理今天平仓交易数据
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	/**
	 * 处理今天平仓交易数据,产生成本和估值增值数据以及先入先出库存数据
	 * @param optionKCTrade
	 * @throws YssException 
	 */
	private void doTheDayOptionsPCTradeData(HashMap optionKCTrade) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
		OptionsFIFOFirstInOutBean fifodata = null;
		ArrayList alOptionKCTrade = null;
		String sKey = "";
        double cost = 0; //原币成本
        double baseCost = 0; //基础货币成本
        double portCost = 0; //组合货币成本
        double addValue = 0; //原币估值增值
        double baseAddValue = 0; //基础货币估值增值
        double portAddValue = 0; //组合货币估值增值

        OptionsTradeCostRealBean costRealData = null;
        double tradeAmount;
		try{
			buff = new StringBuffer(500);
			
			buff.append(" select a.*,market.FClosingPrice,b.FTradeCury from ").append(pub.yssGetTableName("tb_data_optionstraderela"));
			buff.append(" a left join (select m2.* from (select max(FMktValueDate) as FMktValueDate,FSecurityCode from ");
            buff.append(pub.yssGetTableName("Tb_Data_MarketValue"));
            buff.append(" where FCheckState = 1 ");//行情表
            buff.append(" and FMktValueDate <=").append(dbl.sqlDate(this.dDate)).append(" group by FSecurityCode) m1");
            buff.append(" left join (select mar.* from ");
            buff.append(pub.yssGetTableName("Tb_Data_MarketValue"));//行情表
            buff.append(" mar where FCheckState = 1 ").append(") m2 on m1.FSecurityCode =m2.FSecurityCode");
            buff.append(" and m1.FMktValueDate =m2.FMktValueDate) market on a.fsecuritycode =market.FSecurityCode");
            buff.append(" join(select * from ").append(pub.yssGetTableName("tb_para_Security"));
            buff.append(" where FCheckState = 1 and FCatCode = 'FP')").append(" b on a.FSecurityCode = b.FSecurityCode");
			buff.append(" where a.FBargaindate = ").append(dbl.sqlDate(this.dDate));
			buff.append(" and a.FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
			buff.append(" and a.FCloseNum not in ('01', '03')");
			//buff.append(" order by a.FNum, a.FSecurityCode,a.FPortCode");
			buff.append(" order by a.FSecurityCode,a.FPortCode,a.FNum ");
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			while(rs.next()){
				sKey = rs.getString("FSecurityCode") + "\t" + rs.getString("FPortCode");
				if(optionKCTrade.containsKey(sKey)){
					alOptionKCTrade = (ArrayList) optionKCTrade.get(sKey);
					tradeAmount = rs.getDouble("ftradeamount");
					for(int i =0; i < alOptionKCTrade.size(); i++){
						fifodata = (OptionsFIFOFirstInOutBean) alOptionKCTrade.get(i);						
						if(fifodata.getDStorageAmount() == 0){
							break;
						}
						if(tradeAmount >= fifodata.getDStorageAmount()){
							tradeAmount = YssD.sub(tradeAmount, fifodata.getDStorageAmount());
							
							cost = fifodata.getDCuryCost();
							baseCost = fifodata.getDBaseCuryCost();
							portCost = fifodata.getDPortCuryCost();
							addValue = fifodata.getCuryValue();
							baseAddValue = fifodata.getBaseCuryValue();
							portAddValue = fifodata.getPortCuryValue();
							
							costRealData = setDataInCostAddValueBean(rs, cost, baseCost, portCost, addValue, baseAddValue, portAddValue, fifodata.getSNum());//把数据保存到 估值核对的POJO类中
	                        insertSecPecPay(costRealData, rs); //冲减估值增值

	                        fifodata.setDStorageAmount(0);
	                        fifodata.setDCuryCost(0);
	                        fifodata.setDBaseCuryCost(0);
	                        fifodata.setDPortCuryCost(0);
						}else{
							cost = YssD.round
							       (
							    	   YssD.mul
							    	   (
						    			   YssD.div
										   (
											   fifodata.getDCuryCost(),
											   fifodata.getDStorageAmount()
										   ),
										   tradeAmount
										),
										2
									);
							baseCost = YssD.round
							           (
						        		   YssD.mul
						        		   (
					        				   cost,
					        				   rs.getDouble("FBaseCuryRate")
				        				   ),
				        				   2
			        				   );
							portCost = YssD.round
									   (
										   YssD.div
										   (
											   YssD.mul
											   (
												   cost,
												   rs.getDouble("FBaseCuryRate")
											   ),
											   rs.getDouble("FPortCuryRate")
										   ),
										   2
									   );
							
							addValue = YssD.round
								       (
								    	   YssD.mul
								    	   (
							    			   YssD.div
											   (
												   fifodata.getCuryValue(),
												   fifodata.getDStorageAmount()
											   ),
											   tradeAmount
											),
											2
										);
							baseAddValue = YssD.round
								           (
							        		   YssD.mul
							        		   (
						        				   addValue,
						        				   rs.getDouble("FBaseCuryRate")
					        				   ),
					        				   2
				        				   );
							portAddValue = YssD.round
										   (
											   YssD.div
											   (
												   YssD.mul
												   (
													   addValue,
													   rs.getDouble("FBaseCuryRate")
												   ),
												   rs.getDouble("FPortCuryRate")
											   ),
											   2
										   );
							
							costRealData = setDataInCostAddValueBean(rs, cost, baseCost, portCost, addValue, baseAddValue, portAddValue, fifodata.getSNum());//把数据保存到 估值核对的POJO类中
	                        insertSecPecPay(costRealData, rs); //冲减估值增值
	                        
	                        fifodata.setDStorageAmount(
	                        						      YssD.sub
	                        						      (
                        						    		  fifodata.getDStorageAmount(),
                        						    		  tradeAmount
                    						    		  )
                						    		  );
	                        fifodata.setDCuryCost(
	                        					     YssD.round
	                        					     (
                        					    		 YssD.sub
                        					    		 (
                    					    				 fifodata.getDCuryCost(),
                    					    				 cost
                					    				 ),
                					    				 2
            					    				 )
        					    				 );
	                        fifodata.setDBaseCuryCost(
						           					     YssD.round
						           					     (
						       					    		 YssD.sub
						       					    		 (
						   					    				 fifodata.getDBaseCuryCost(),
						   					    				 baseCost
										    				 ),
										    				 2
									    				 )
								    				 );											
		    				fifodata.setDPortCuryCost(
						           					     YssD.round
						           					     (
						       					    		 YssD.sub
						       					    		 (
						   					    				 fifodata.getDPortCuryCost(),
						   					    				 portCost
										    				 ),
										    				 2
									    				 )
								    				 );
		    				
		    				fifodata.setCuryValue(
					           					     YssD.round
					           					     (
					       					    		 YssD.sub
					       					    		 (
					   					    				 fifodata.getCuryValue(),
					   					    				 addValue
									    				 ),
									    				 2
								    				 )
							    				 );
						    fifodata.setBaseCuryValue(
						           					     YssD.round
						           					     (
						       					    		 YssD.sub
						       					    		 (
						   					    				 fifodata.getBaseCuryValue(),
						   					    				 baseAddValue
										    				 ),
										    				 2
									    				 )
								    				 );											
							fifodata.setPortCuryValue(
						           					     YssD.round
						           					     (
						       					    		 YssD.sub
						       					    		 (
						   					    				 fifodata.getPortCuryValue(),
						   					    				 portAddValue
										    				 ),
										    				 2
									    				 )
								    				 );
							break;
						}
					}
				}
			}		
			if(alLastOptionsTradeData == null){
				alLastOptionsTradeData = new ArrayList();
			}
			Iterator it = optionKCTrade.values().iterator();
			while(it.hasNext()){
				alLastOptionsTradeData.add(it.next());
			}
			
		}catch (Exception e) {
			throw new YssException("处理今天平仓交易数据,产生成本和估值增值数据以及先入先出库存数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/*private void doTheDayOptionsPCTradeData(HashMap optionKCTrade) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
		ResultSet rst = null;
		OptionsFIFOFirstInOutBean fifodata = null;
		OptionsFIFOFirstInOutBean fifoNewdata = null;
		ArrayList alOptionKCTrade = null;
		String sKey = "";
		String sOldKey = "";
		double sTotalKCAmount = 0;
        double cost = 0; //原币成本
        double baseCost = 0; //基础货币成本
        double portCost = 0; //组合货币成本
        double addValue = 0; //原币估值增值
        double baseAddValue = 0; //基础货币估值增值
        double portAddValue = 0; //组合货币估值增值
        double [] yesDateKCValueBalance = null;
        double yesDStorgeAmount = 0; //昨日的库存数量
        double yesDStorageCost = 0;//昨日库存成本
        double thDayTradeAmount = 0;//今天平仓交易数量
        double PCCost = 0;//保存平仓交易平掉的开仓成本
        ArrayList costData = null; //返回值
        String sAttrClsCode = " ";
        String sCatType = " ";
		try{
			buff = new StringBuffer(500);
			
			buff.append(" select a.*,market.FClosingPrice,b.FTradeCury from ").append(pub.yssGetTableName("tb_data_optionstraderela"));
			buff.append(" a left join (select m2.* from (select max(FMktValueDate) as FMktValueDate,FSecurityCode from ");
            buff.append(pub.yssGetTableName("Tb_Data_MarketValue"));
            buff.append(" where FCheckState = 1 ");//行情表
            buff.append(" and FMktValueDate <=").append(dbl.sqlDate(this.dDate)).append(" group by FSecurityCode) m1");
            buff.append(" left join (select mar.* from ");
            buff.append(pub.yssGetTableName("Tb_Data_MarketValue"));//行情表
            buff.append(" mar where FCheckState = 1 ").append(") m2 on m1.FSecurityCode =m2.FSecurityCode");
            buff.append(" and m1.FMktValueDate =m2.FMktValueDate) market on a.fsecuritycode =market.FSecurityCode");
            buff.append(" join(select * from ").append(pub.yssGetTableName("tb_para_Security"));
            buff.append(" where FCheckState = 1 and FCatCode = 'FP')").append(" b on a.FSecurityCode = b.FSecurityCode");
			buff.append(" where a.FBargaindate = ").append(dbl.sqlDate(this.dDate));
			buff.append(" and a.FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
			buff.append(" and a.FCloseNum not in ('01', '03')");
			//buff.append(" order by a.FNum, a.FSecurityCode,a.FPortCode");
			buff.append(" order by a.FSecurityCode,a.FPortCode,a.FNum ");
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			while(rs.next()){
				sKey = rs.getString("FSecurityCode")+"\t" + rs.getString("FPortCode");
				if(optionKCTrade.containsKey(sKey)){
					if(!sKey.equalsIgnoreCase(sOldKey)){
						sOldKey = sKey;
						sTotalKCAmount =0;
					}
					alOptionKCTrade = (ArrayList) optionKCTrade.get(sKey);
					for(int i =0; i < alOptionKCTrade.size(); i++){
						fifodata = (OptionsFIFOFirstInOutBean) alOptionKCTrade.get(i);
						
						sTotalKCAmount += fifodata.getDStorageAmount();
						if(i == alOptionKCTrade.size()-1 && rs.getDouble("ftradeamount") > sTotalKCAmount ){
							throw new YssException("库存不足,无法进行先入先出平仓");
						}else if(rs.getDouble("ftradeamount") <= sTotalKCAmount){
							//------------------------------------此处获取下昨日库存----------------------------------//
							buff.append(getYesDStorgeAmount(fifodata.getSSecurityCode(),fifodata.getSPortCode())); //获取昨日的库存数据
							
							rst = dbl.openResultSet(buff.toString());
							buff.delete(0,buff.length());
							if(rst.next()){
								yesDStorgeAmount = rst.getDouble("FStorageAmount");
								yesDStorageCost = rst.getDouble("FStorageCost");
								sAttrClsCode = rst.getString("FAttrClsCode");
								sCatType = rst.getString("FCatType");
							}
							
							dbl.closeResultSetFinal(rst);
							//------------------------------------end---------------------------------------------------//
							for(int j = 0;j <= i;j++){
								fifoNewdata = (OptionsFIFOFirstInOutBean) alOptionKCTrade.get(j);
								yesDateKCValueBalance = getYesDateKCValueBalance(fifoNewdata.getSNum());//获取昨日每笔开仓交易的估值增值余额
								if(j < i){
									
									thDayTradeAmount += fifoNewdata.getDStorageAmount();
									
									PCCost += fifoNewdata.getDCuryCost();
									
									//-------------------产生成本和估值增值数据-----------------//
									cost = fifoNewdata.getDCuryCost();
									baseCost = fifoNewdata.getDBaseCuryCost();
									portCost = fifoNewdata.getDPortCuryCost();
									addValue = yesDateKCValueBalance[0];
									baseAddValue = yesDateKCValueBalance[1];
									portAddValue = yesDateKCValueBalance[2];
									
									costData = setDataInCostAddValueBean(rs, cost, baseCost, portCost, addValue, baseAddValue, portAddValue,fifoNewdata.getSNum());//把数据保存到 估值核对的POJO类中
									if(rs.getString("FCloseNum").equalsIgnoreCase("02") || rs.getString("FCloseNum").equalsIgnoreCase("04")){
										if(yesDStorgeAmount < 0){
											//产生一笔资金调拨
				                            saveCashTransferData(alCostAddValueData, rs);
										}
									}
			                        //把当天交易的估值增值插入到 证券应收应付表，冲减到一笔应收应付
			                        insertSecPecPay(costData, rs, yesDStorgeAmount,sAttrClsCode,sCatType);
									//---------------------end--------------------------//
									
									fifoNewdata.setSStorageDate(YssFun.formatDate(this.dDate,"yyyy-MM-dd"));
									fifoNewdata.setDStorageAmount(0);
									fifoNewdata.setDCuryCost(0);
									fifoNewdata.setDBaseCuryCost(0);
									fifoNewdata.setDPortCuryCost(0);
									fifoNewdata.setDBailMoney(0);
									
								}else{
									//-------------------产生成本和估值增值数据-----------------//
									//先进先出平仓成本 = 原先入先出库存成本/原先入先出库存数量 * 平仓数量 + 平仓掉的成本
									cost = YssD.round(YssD.add(PCCost,
											YssD.mul(
													YssD.div(fifodata.getDCuryCost(),fifodata.getDStorageAmount()),
											YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount))),2);
									baseCost = YssD.round(YssD.mul(cost,rs.getDouble("FBaseCuryRate")),2);
									portCost = YssD.round(YssD.div(YssD.mul(cost,rs.getDouble("FBaseCuryRate")),rs.getDouble("FPortCuryRate")),2);
									addValue = YssD.round(
											YssD.mul(
													YssD.div(yesDateKCValueBalance[0],fifodata.getDStorageAmount()),
													YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount)),2);
									baseAddValue = YssD.round(
											YssD.mul(
													YssD.div(yesDateKCValueBalance[1],fifodata.getDStorageAmount()),
													YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount)),2);
									portAddValue = YssD.round(
											YssD.mul(
													YssD.div(yesDateKCValueBalance[2],fifodata.getDStorageAmount()),
													YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount)),2);
									
									costData = setDataInCostAddValueBean(rs, cost, baseCost, portCost, addValue, baseAddValue, portAddValue,fifoNewdata.getSNum());//把数据保存到 估值核对的POJO类中
									if(rs.getString("FCloseNum").equalsIgnoreCase("02") || rs.getString("FCloseNum").equalsIgnoreCase("04")){
										if(yesDStorgeAmount < 0){
											//产生一笔资金调拨
				                            saveCashTransferData(costData, rs);
										}
									}
									//把当天交易的估值增值插入到 证券应收应付表，冲减到一笔应收应付
									insertSecPecPay(alCostAddValueData, rs, yesDStorgeAmount,sAttrClsCode,sCatType);
									//---------------------end--------------------------//

									fifoNewdata.setSStorageDate(YssFun.formatDate(this.dDate,"yyyy-MM-dd"));
									//先进先出平仓后剩余成本 = 原库存成本 - 原库存成本/原库存数量 * 平仓数量
									fifoNewdata.setDCuryCost(YssD.round(
											YssD.sub(fifodata.getDCuryCost(),
													YssD.mul(
															YssD.div(fifodata.getDCuryCost(),fifodata.getDStorageAmount()),
															YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount))),2));
									fifoNewdata.setDStorageAmount(YssD.sub(sTotalKCAmount,rs.getDouble("ftradeamount")));
									fifoNewdata.setDBaseCuryCost(YssD.round(YssD.mul(fifoNewdata.getDCuryCost(),rs.getDouble("FBaseCuryRate")),2));
									fifoNewdata.setDPortCuryCost(YssD.round(
											YssD.div(
													YssD.mul(fifoNewdata.getDCuryCost(),rs.getDouble("FBaseCuryRate")),
													rs.getDouble("FPortCuryRate")),
													2));
									fifoNewdata.setDBailMoney(rs.getDouble("FBegBailMoney"));
									break;
								}
							}//end for j
							break;//进行while的下次循环
						}
					}//end for i
				}else{
					throw new YssException("库存不足,无法进行先入先出平仓");
				}
			}
			if(alLastOptionsTradeData == null){
				alLastOptionsTradeData = new ArrayList();
			}
			Iterator it = optionKCTrade.values().iterator();
			while(it.hasNext()){
				alLastOptionsTradeData.add(it.next());
			}
			
		}catch (Exception e) {
			throw new YssException("处理今天平仓交易数据,产生成本和估值增值数据以及先入先出库存数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rst);
		}
	}*/
	
	/**
	 * saveCashTransferData 产生一笔资金调拨
	 * 
	 * @param costData
	 *            ArrayList
	 * @param rs
	 *            ResultSet
	 * @param rst
	 *            ResultSet
	 */
	private void saveCashTransferData(ArrayList costData, ResultSet rs)
			throws YssException {
		ArrayList cashTransData = new ArrayList();
		CashTransAdmin cashtrans = null; // 初始化资金调拨操作类
		String filtersRelaNums = ""; // 保存交易编号
		boolean bTrans = false; // 事务控制
		Connection conn = dbl.loadConnection(); // 获取连接
		try {
			cashTransData = getTheDayTradeData(costData, rs);
			if (cashTransData.size() > 0) { // 是否当天有交易
				cashtrans = new CashTransAdmin();
				cashtrans.setYssPub(pub);
				cashtrans.addList(cashTransData); // 子资金调拨的值放入TransferBean中的arrayList中
				if (sRelaNums.length() > 0 && sRelaNums.endsWith(",")) {
					filtersRelaNums = this.sRelaNums.substring(0, sRelaNums
							.length() - 1); // 除去最后的","号
				}
				// 增加事务控制和锁，以免在多用户同时处理时出现调拨编号重复
				conn.setAutoCommit(false); // 设置不自动提交事务
				bTrans = true;
				dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer")); // 给操作的表加锁
				dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));
				cashtrans.insert(this.dDate, "OptionsTrade", filtersRelaNums); // 插入数据到资金调拨表和子表中
				conn.commit(); // 提交事务
				conn.setAutoCommit(true); // 设置为自动提交事务
				bTrans = false;
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 查询期权交易关联表数据，把数据set到TransferBean和TransferSetBean中
	 * 
	 * @return ArrayList
	 * @throws YssException
	 */
	private ArrayList getTheDayTradeData(ArrayList costData, ResultSet rs)
			throws YssException {
		ArrayList curCashTransArr = null;// 保存资金调拨数据
		TransferBean transfer = null;// 调拨类初始化
		TransferSetBean transferset = null;// 调拨子类初始化
		ArrayList subtransfer = null;// 保存资金子调拨
		boolean analy1;// 分析代码1
		boolean analy2;// 分析代码2
		boolean analy3;// 分析代码3
		try {
			analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
			analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
			analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
			curCashTransArr = new ArrayList();

			if (rs.getString("FTradeTypeCode").equals("01")) { // 买入
				subtransfer = new ArrayList();
				transfer = setTransferAttr(rs, "QSKHC"); // 资金调拨表，清算款划出
				transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
						"QSKOUT", costData); // 资金调拨子表，清算款划出
				subtransfer.add(transferset);
				transfer.setSubTrans(subtransfer);
				curCashTransArr.add(transfer);
			} else if (rs.getString("FTradeTypeCode").equals("02")) { // 卖出
				subtransfer = new ArrayList();
				transfer = setTransferAttr(rs, "QSKHR"); // 资金调拨表 清算款划入
				transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
						"QSKIN", costData); // 资金调拨子表，清算款划入
				subtransfer.add(transferset);
				transfer.setSubTrans(subtransfer);
				curCashTransArr.add(transfer);
			} else if (rs.getString("FTradeTypeCode").equals("32FP")) { // 期权行权
				subtransfer = new ArrayList();
				transfer = setTransferAttr(rs, "QSKHR"); // 资金调拨表 清算款划入
				transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
						"QSKIN", costData); // 资金调拨子表，清算款划入
				subtransfer.add(transferset);
				transfer.setSubTrans(subtransfer);
				curCashTransArr.add(transfer);
			} else if (rs.getString("FTradeTypeCode").equals("33FP")) { // 期权结算
				subtransfer = new ArrayList();
				transfer = setTransferAttr(rs, "QSKHC"); // 资金调拨表，清算款划出
				transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
						"QSKOUT", costData); // 资金调拨子表，清算款划出
				subtransfer.add(transferset);
				transfer.setSubTrans(subtransfer);
				curCashTransArr.add(transfer);
			} else { // 期权放弃行权

			}
		} catch (Exception e) {
			throw new YssException("查询期权交易关联表数据出错！\r\t", e);
		}
		return curCashTransArr;
	}

	/**
	 * 设置资金调拨子表数据出错
	 * 
	 * @param rs
	 *            ResultSet
	 * @param analy1
	 *            boolean 分析代码1
	 * @param analy2
	 *            boolean 分析代码2
	 * @param analy3
	 *            boolean 分析代码3
	 * @param type
	 *            String 调拨类型
	 * @return TransferSetBean
	 * @throws YssException
	 */
	private TransferSetBean setTransferSetAttr(ResultSet rs, boolean analy1,
			boolean analy2, boolean analy3, String type, ArrayList costData)
			throws YssException {
		TransferSetBean transferset = new TransferSetBean();// 调拨子类
		double dBaseRate = 0;// 基础汇率
		double dPortRate = 0;// 组合汇率
		double money = 0.0;// 调拨金额
		SecurityBean security = null;// 证券信息类
		OptionsTradeCostRealBean costRealData = null; // 期权估值核对表对应的与数据库交互的实体类
		try {
			costRealData = (OptionsTradeCostRealBean) costData.get(0);
			dBaseRate = rs.getDouble("FBaseCuryRate");
			dPortRate = rs.getDouble("FPortCuryRate");

			security = new SecurityBean();
			security.setYssPub(pub);
			security.setSecurityCode(rs.getString("FSecurityCode"));
			security.getSetting();

			transferset.setSPortCode(rs.getString("FPortCode")); // 设置组合代码
			if (analy1) {
				transferset.setSAnalysisCode1(rs.getString("FInvMgrCode")); // 投资经理
			}
			if (analy2) {
				transferset.setSAnalysisCode2("FBrokerCode"); // 券商代码
			}
			if (type.equalsIgnoreCase("QSKIN")) { // 平仓清算款流入账户。
				transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
				money = costRealData.getFOriginalAddValue(); // 调拨金额
				transferset.setIInOut(1); // 调拨方向
			} else if (type.equalsIgnoreCase("QSKOUT")) { // 开仓的设置，清算款流出账户。
				transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
				money = costRealData.getFOriginalAddValue();
				transferset.setIInOut(-1);
			}
			transferset.setDMoney(money);// 调拨金额
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
	 * 
	 * @param rs
	 *            ResultSet
	 * @param type
	 *            String 类型
	 * @return TransferBean
	 * @throws YssException
	 */
	private TransferBean setTransferAttr(ResultSet rs, String type)
			throws YssException {
		TransferBean transfer = new TransferBean();// 调拨类
		try {
			transfer.setDtTransferDate(rs.getDate("FSettleDate"));// 调拨日期
			transfer.setDtTransDate(rs.getDate("FBargainDate"));// 业务日期
			if (type.equalsIgnoreCase("QSKHC")) { // 划出清算款
				transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Cost);// 应付为清算款
				transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_COST);// 内部账户转账
			} else if (type.equalsIgnoreCase("QSKHR")) {
				transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Cost);// 应付为清算款
				transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_COST);// 内部账户转账
			}
			transfer.setFRelaNum(rs.getString("FNum")+ rs.getString("FCloseNum"));// 编号
			transfer.setStrTradeNum(rs.getString("FNum")+ rs.getString("FCloseNum"));
			transfer.setFNumType("OptionsTrade");// 设置编号类型
			transfer.setStrSecurityCode(rs.getString("FSecurityCode"));// 证券代码

			transfer.checkStateId = 1;
			sRelaNums += transfer.getFRelaNum() + ",";

		} catch (Exception ex) {
			throw new YssException("设置资金调拨数据出错!", ex);
		}
		return transfer;
	}

	/**
	 * 通过证券代码获取昨日的证券库存数量
	 * 
	 * @param string
	 *            sSecurityCode 证券代码
	 * @return double 库存数量
	 */
	private String getYesDStorgeAmount(String sSecurityCode, String sPortCode)
			throws YssException {
		StringBuffer sqlBuf = new StringBuffer();
		ResultSet rs = null;
		try {
			// 通过组合代码、证券代码、库存日期、审核状态从证券库存中取数
			sqlBuf.append("select * from ").append(pub.yssGetTableName("Tb_Stock_Security"));
			sqlBuf.append(" WHERE FCheckState = 1");
			sqlBuf.append(" AND FPortCode = ").append(dbl.sqlString(sPortCode));
			sqlBuf.append(" AND FSecurityCode= ").append(dbl.sqlString(sSecurityCode));
			sqlBuf.append(" AND FStorageDate= ").append(dbl.sqlDate(YssFun.addDay(this.dDate, -1)));
		} catch (Exception e) {
			throw new YssException("期权处理-获取证券库存出错！", e);
		}
		return sqlBuf.toString();
	}
	/**
	 * 从期权先入先出估值增值余额表中获取昨日每笔开仓交易的估值增值余额
	 * 
	 * @param num
	 * @return
	 * @throws YssException
	 */
	private double[] getYesDateKCValueBalance(String num) throws YssException {
		double [] yesDateKCValueBalance = new double[3];
		StringBuffer buff = null;
		ResultSet rs = null;
		try{
			buff = new StringBuffer(500);
			buff.append(" select * from ").append(pub.yssGetTableName("Tb_Data_OptiFIFOAppStk"));
			buff.append(" where FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(this.dDate,-1)));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
			buff.append(" and FNum = ").append(dbl.sqlString(num));
			
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			if(rs.next()){
				yesDateKCValueBalance[0] = rs.getDouble("FCuryValue");
				yesDateKCValueBalance[1] = rs.getDouble("FBaseCuryValue");
				yesDateKCValueBalance[2] = rs.getDouble("FPortCuryValue");
			}
			
		}catch (Exception e) {
			throw new YssException("从期权先入先出估值增值余额表中获取昨日每笔开仓交易的估值增值余额出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return yesDateKCValueBalance;
	}
	/**
	 * 获取期权昨日和今日开仓交易数据
	 * 
	 * @return
	 * @throws YssException
	 */
	private HashMap getOptionsKCTrade() throws YssException {
		HashMap mapOptionKCTrade = null;
		StringBuffer buff = null;
		ResultSet rs = null;
		ArrayList alOptionKCTrade = null;
		OptionsFIFOFirstInOutBean fifodata = null;
		String sKey = "";
        double cost = 0; // 原币成本
        double baseCost = 0; // 基础货币成本
        double portCost = 0; //组合货币成本
        double addValue = 0; //原币估值增值
        double baseAddValue = 0; //基础货币估值增值
        double portAddValue = 0; //组合货币估值增值
		try{
			buff = new StringBuffer(1000);
			mapOptionKCTrade = new HashMap();
			alOptionKCTrade = new ArrayList();
			
			buff.append(" select d.*, e.* from(");
			buff.append(" select a.fnum,a.fclosenum,a.fsecuritycode,a.fportcode,a.fbargaindate as FStorageDate,");
			buff.append(" round(a.ftradeamount * a.ftradeprice * b.fmultiple, 2) as FCuryCost,");
			buff.append(" round(round(a.ftradeamount * a.ftradeprice * b.fmultiple, 2) * a.fbasecuryrate,2) as FBaseCuryCost,");
			buff.append(" round(round(round(a.ftradeamount * a.ftradeprice *b.fmultiple,2) * a.fbasecuryrate,2) / a.fportcuryrate,2) as FPortCuryCost,");
			buff.append(" a.fbasecuryrate,a.fportcuryrate,a.fbegbailmoney as FBailMoney,a.FCreator,a.FCreateTime,a.FCheckUser,a.FCheckTime,a.ftradeamount as FStorageAmount from ");
			buff.append(pub.yssGetTableName("tb_data_optionstraderela"));
			buff.append(" a join (select * from ").append(pub.yssGetTableName("tb_para_optioncontract"));
			buff.append(" where FCheckState = 1) b on a.fsecuritycode = b.FOptionCode");
			buff.append(" where FBargaindate = ").append(dbl.sqlDate(this.dDate));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
			buff.append(" and FCloseNum in ('01', '03')");
			buff.append(" union all ");
			buff.append(" select c.fnum,' ' as fclosenum,c.fsecuritycode,c.fportcode,c.FStorageDate,c.FCuryCost,c.FBaseCuryCost,");
			buff.append(" c.FPortCuryCost,c.fbasecuryrate,c.fportcuryrate,c.FBailMoney,c.FCreator,c.FCreateTime,c.FCheckUser,c.FCheckTime,c.FStorageAmount from ");
			buff.append(pub.yssGetTableName("Tb_Data_OptiFIFOStock"));
			buff.append(" c where c.FStorageDate =").append(dbl.sqlDate(YssFun.addDay(this.dDate,-1)));
			buff.append(" and c.FPortCode in(").append(this.operSql.sqlCodes(this.sPortCode)).append(")");
			buff.append(" and c.FStorageAmount <> 0");
			//buff.append(" ) d order by d.FNum,d.FStorageDate,d.FSecurityCode, d.FPortCode");
			buff.append(" ) d left join (select fnum, FCuryValue, FBaseCuryValue, FPortCuryValue from ");
			buff.append(pub.yssGetTableName("Tb_Data_OptiFIFOAppStk"));
			buff.append(" where FStorageDate =").append(dbl.sqlDate(YssFun.addDay(this.dDate,-1)));
			buff.append(") e on d.fnum =e.fnum order by d.FSecurityCode, d.FPortCode,d.FStorageDate,d.FNum ");
			rs = dbl.queryByPreparedStatement(buff.toString());
			while(rs.next()){
				sKey = rs.getString("FSecurityCode")+"\t" + rs.getString("FPortCode");
				fifodata = new OptionsFIFOFirstInOutBean();
				setOptionsFIFOData(fifodata,rs);//设置先入先出实体bean值
				if(YssFun.dateDiff(rs.getDate("FStorageDate"),this.dDate) == 0){
					cost = rs.getDouble("FCuryCost");
					baseCost = rs.getDouble("FBaseCuryCost");
					portCost = rs.getDouble("FPortCuryCost");
					setDataInCostAddValueBean(rs, cost, baseCost, portCost, addValue, baseAddValue, portAddValue," ");//把数据保存到 估值核对的POJO类中
				}
				if(mapOptionKCTrade.containsKey(sKey)){
					alOptionKCTrade.add(fifodata);
				}else{
					alOptionKCTrade = new ArrayList();
					alOptionKCTrade.add(fifodata);
				}
				mapOptionKCTrade.put(sKey,alOptionKCTrade);
			}
				
		}catch (Exception e) {
			throw new YssException("获取期权昨日开仓交易数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return mapOptionKCTrade;
	}
	/**
	 * 设置先入先出实体bean值
	 * @param fifodata
	 * @throws YssException
	 */
	private void setOptionsFIFOData(OptionsFIFOFirstInOutBean fifodata,ResultSet rs)throws YssException{
		try{
			fifodata.setSNum(rs.getString("FNum"));
			fifodata.setSStorageDate(YssFun.formatDate(this.dDate,"yyyy-MM-dd"));
			fifodata.setSSecurityCode(rs.getString("FSecurityCode"));
			fifodata.setDStorageAmount(rs.getDouble("FStorageAmount"));
			fifodata.setSPortCode(rs.getString("FPortCode"));
			fifodata.setDCuryCost(rs.getDouble("FCuryCost"));
			fifodata.setDBaseCuryCost(rs.getDouble("FBaseCuryCost"));
			fifodata.setDPortCuryCost(rs.getDouble("FPortCuryCost"));
			fifodata.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));
			fifodata.setDPortCuryRate(rs.getDouble("FPortCuryRate"));
			fifodata.setDBailMoney(rs.getDouble("FBailMoney"));
			//add by fangjiang 2011.09.09 story 1342
			fifodata.setCuryValue(rs.getDouble("FCuryValue"));
			fifodata.setBaseCuryValue(rs.getDouble("FBaseCuryValue"));
			fifodata.setPortCuryValue(rs.getDouble("FPortCuryValue"));
			//-------------------
		}catch (Exception e) {
			throw new YssException("设置先入先出数据出错！",e);
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
    private OptionsTradeCostRealBean setDataInCostAddValueBean(ResultSet rs, double cost, double baseCost, double portCost,
                                                double addValue, double baseAddValue, double portAddValue,String sSetNum) throws
        YssException {
        OptionsTradeCostRealBean costRealData = null; //期权估值核对表的POJO类
        ArrayList costData = new ArrayList(); //返回值
        try {
            costRealData = new OptionsTradeCostRealBean();
            costRealData.setSFNum(rs.getString("FNum") + rs.getString("FCloseNum")); //编号
            costRealData.setSSetNum(sSetNum);//开仓编号
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
            costRealData.setPortCode(rs.getString("FPortCode")); //add by fangjiang 2011.09.14 story 1342
            costData.add(costRealData);
            alCostAddValueData.add(costRealData);

        } catch (Exception e) {
            throw new YssException("把数据保存到估值核对的POJO类中出错！", e);
        }
        return costRealData;
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
	        //屏蔽掉每日结转模式
//	        String sTheDayFirstFIFO = (String) htAccountType.get(YssOperCons.
//	        		YSS_TYCS_BAILMONEY_DAYTRANSFER);//获取value值
//	        if (sTheDayFirstFIFO != null && sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
//	            sResult = YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER;//每日结转
//	        }
    	}catch (Exception e) {
			throw new YssException("通过组合代码获取期权保证金结算方式出错！",e);
		}
        return sResult;
    }
    /**
     * insertSecPecPay 把当天交易的估值增值插入到 证券应收应付表，冲减到一笔应收应付
     *
     * @param costData ArrayList
     */
    private void insertSecPecPay(OptionsTradeCostRealBean costRealData, ResultSet rs) throws YssException {
        SecPecPayBean secPay = null; 
        secpayAdmin.setYssPub(pub);
        try {
            secPay = new SecPecPayBean();
            //基础和组合汇率
            secPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
            secPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));
            
            secPay.setTransDate(this.dDate); //业务日期
            
            secPay.setMoney(costRealData.getFOriginalAddValue());
            secPay.setMMoney(costRealData.getFOriginalAddValue());
            secPay.setVMoney(costRealData.getFOriginalAddValue());

            secPay.setBaseCuryMoney(costRealData.getFBaseAddValue());
            secPay.setMBaseCuryMoney(costRealData.getFBaseAddValue());
            secPay.setVBaseCuryMoney(costRealData.getFBaseAddValue());

            secPay.setPortCuryMoney(costRealData.getFPortAddValue());
            secPay.setMPortCuryMoney(costRealData.getFPortAddValue());
            secPay.setVPortCuryMoney(costRealData.getFPortAddValue());

            secPay.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_MV); //09
            secPay.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FP01_MV); //09FP01

            secPay.setStrSecurityCode(rs.getString("FSecurityCode")); //证券代码
            secPay.setStrPortCode(rs.getString("FPortCode")); //组合代码
            secPay.setBrokerCode(rs.getString("FBrokerCode")); //券商代码
            secPay.setInvMgrCode(rs.getString("FInvMgrCode")); //投资经理
            secPay.setStrCuryCode(rs.getString("FTradeCury")); //币种代码
            secPay.setInOutType(-1); //调拨方向
            secPay.setMoney(costRealData.getFOriginalAddValue()); //调拨金额

            secPay.checkStateId = 1;
            
            secpayAdmin.addList(secPay);
        } catch (Exception e) {
            throw new YssException("插入数据到证券应收应付表出错！", e);
        } 
    }
    
    /*private void insertSecPecPay(ArrayList costData, ResultSet rs, double yesDStorgeAmount,String sAttrClsCode,String sCatType) throws YssException {
        SecPecPayBean secPay = null; //证券应收应付Pojo类
        OptionsTradeCostRealBean costRealData = null; //期权估值核对表对应的与数据库交互的实体类
        String sBailMoneyTransferType ="";
        secpayAdmin.setYssPub(pub);
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
                secPay.setStrCuryCode(rs.getString("FTradeCury")); //币种代码
                secPay.setAttrClsCode(sAttrClsCode); //分类类型
                secPay.setInOutType( -1); //调拨方向
                secPay.setCatTypeCode(sCatType); //品种类型
                secPay.setMoney(costRealData.getFOriginalAddValue()); //调拨金额

                secPay.checkStateId = 1;
            }
            secpayAdmin.addList(secPay);
        } catch (Exception e) {
            throw new YssException("插入数据到证券应收应付表库存表中出错！", e);
        } 
    }*/
}




















