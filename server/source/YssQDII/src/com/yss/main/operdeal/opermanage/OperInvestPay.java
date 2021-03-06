package com.yss.main.operdeal.opermanage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.yss.commeach.EachRateOper;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdata.InvestPayRecBean;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.manager.CashTransAdmin;
import com.yss.manager.InvestPayAdimin;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 自动支付两费 lidaolong 20110309 #386 增加一个功能，能够自动支付管理费和托管费
 */
public class OperInvestPay extends BaseOperManage {

	 InvestPayAdimin investpayAdmin = null;
	CashTransAdmin cashtransAdmin = null;
	int iOperCount = 0;			//20121113 added by liubo.Bug #6221.记录进行自动支付操作的两费的数量
	
			
	CashTransAdmin commissionCashTrans = null;	//20130207 added by liubo.Story #3414.生成划款手续费的资金调拨的控制类

	/**
	 * 执行业务处理
	 */
	public void doOpertion() throws YssException {
		ResultSet rs = null;
		ResultSet rsTemp = null;
		String strSql = "";
		
		String cashPayRecSql = builderCashPayRec();

		try {
				investpayAdmin = new InvestPayAdimin();
				investpayAdmin.setYssPub(pub);
	          
				cashtransAdmin = new CashTransAdmin(); // 生成资金调拨控制类
				cashtransAdmin.setYssPub(pub);
	          
				rs = dbl.queryByPreparedStatement(cashPayRecSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
				String strIVPayCatCodes = "";
				while (rs.next()) {			
		//20120514 deleted by liubo.Story #2217
		//自动支付的逻辑变化，废除旧的支付日与否的判断模式
		//========================
//				if ("payByMonth".equals(rs.getString("FAutoPayType"))){//按月支付
//					// 判断T日，是否是两费支付日期
//					if (isAutoPay(rs.getString("FHolidaysCode"), rs.getInt("FAutoPayDay"))) {
//						createCashTransfer(rs); // 此处产生资金调拨
//						createCashPayRecAdmin(rs);// 此处产生运营应收应付
//					}
//					
//				}else if (mMonth == 4 || mMonth == 7 ||
//						  mMonth == 10 || mMonth == 1){//按季度支付
//					// 判断T日，是否是两费支付日期
//					if (isAutoPay(rs.getString("FHolidaysCode"), rs.getInt("FAutoPayDay"))) {
//						createCashTransfer(rs); // 此处产生资金调拨
//						createCashPayRecAdmin(rs);// 此处产生现金应收应付
//					}
//				}
		//==========end==============
				
				//20120514 added by liubo.Story #2217
				//使用新的支付日判断模式。
				//===============================
				String sPayDate = dbl.clobStrValue(rs.getClob("FPayDate"));			//获取投资运营收支设置中的支付日期设置字段的值。该字段的存储形式为：yyyy1-mm1-dd1,yyyy2-mm2-dd2,yyyy3-mm3-dd3...
				double dDMoney = 0.0;												//该变量用于存储实际支付的金额
				
				if (sPayDate != null && !sPayDate.trim().equals(""))
				{
					String[] sDataList = sPayDate.split(",");
					
					//遍历支付日期设置字段中的值，若当前日期符合设置中的某条日期的值，则表示当日为支付日
					for(int i = 0; i < sDataList.length ; i++)
					{
						if (!sDataList[i].trim().equals(""))
						{
							if (YssFun.formatDate(sDataList[i],"yyyyMMdd").equalsIgnoreCase(YssFun.formatDate(dDate,"yyyyMMdd")))
							{
								//若当日为支付日，需要判断投资运营收支设置中的补差时期字段的值。
								//该字段的值为0，表示计提月底费用时补差，支付金额取上月最后一日的应付费用总额
								//值为1，表示支付日前一日计提时补差，支付金额取当日的前一自然日的总额
								//modify huangqirong 2013-01-23 story #3488 增加按补差日期 设定支付
								if("2".equalsIgnoreCase(rs.getString("FPERIODOFBC"))){
									String supplementDates = rs.getString("FsupplementDates");
									Date supplementDate = dDate ; //支付日期
									if(supplementDates != null && supplementDates.trim().length() > 0){
										String [] dates = supplementDates.split(",");
										String tmpDate = tmpDate = YssFun.formatDate(dDate ,"yyyy-MM-dd");
										
										//取小于支付日期 且最大的那个补差日期
										for (int j = 0; j < dates.length; j++) {
											if(YssFun.dateDiff(YssFun.toDate(dates[i]) , dDate) > 0){ //补差日期必须小于支付日期
												if(tmpDate!= null && tmpDate.trim().length() == 0){
													tmpDate = dates[i] ;
													
												}else{
													if(YssFun.dateDiff(YssFun.toDate(dates[i]) , YssFun.toDate(tmpDate)) > 0){
														tmpDate = dates[i] ;
													}
												}
											}
										}
										supplementDate = YssFun.addDay(YssFun.toDate(tmpDate), 1); //日期 + 1 ，下面要减 1
									}
									
									strSql = "Select * from " + pub.yssGetTableName("Tb_Stock_Invest") + " where FIVPayCatCode = " + dbl.sqlString(rs.getString("FIVPayCatCode")) +
	            			 		 " and FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(YssFun.addDay(supplementDate, -1));
									
								}else if ("1".equals(rs.getString("FPERIODOFBC")))
								{
					            	strSql = "Select * from " + pub.yssGetTableName("Tb_Stock_Invest") + " where FIVPayCatCode = " + dbl.sqlString(rs.getString("FIVPayCatCode")) +
			            			 		 " and FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1));
								}
								else if ("0".equals(rs.getString("FPERIODOFBC")))
								{
									Calendar cal = Calendar.getInstance();  
									cal.setTime(dDate);
									int year = cal.get(Calendar.YEAR);  
									int month = cal.get(Calendar.MONTH) + 1;  
									cal.set(Calendar.DAY_OF_MONTH, 1);  
									cal.add(Calendar.DAY_OF_MONTH, -1);  
									int day = cal.get(Calendar.DAY_OF_MONTH);  
									String months = "";  
									String days = "";  
									if (month > 1) {  
									    month--;  
									} else {  
									   year--;  
									   month = 12;  
									}  
									if (!(String.valueOf(month).length() > 1)) {  
										months = "0" + month;  
									} else {  
									    months = String.valueOf(month);  
									}  
									if (!(String.valueOf(day).length() > 1)) {  
									    days = "0" + day;  
									} else {  
									    days = String.valueOf(day);  
									}  
									String lastDay = "" + year + "-" + months + "-" + days;  
									
									strSql = "Select * from " + pub.yssGetTableName("Tb_Stock_Invest") + " where FIVPayCatCode = " + dbl.sqlString(rs.getString("FIVPayCatCode")) +
	            			 		 		 " and FPortCode = " + dbl.sqlString(sPortCode) + " and FStorageDate = " + dbl.sqlDate(lastDay);
								}
								
								rsTemp = dbl.queryByPreparedStatement(strSql);
								
								while(rsTemp.next())
								{
									dDMoney = rsTemp.getDouble("FBAL");
								}
								
								createCashPayRecAdmin(rs,dDMoney);// 此处产生运营应收应付
								
								//---edit by songjie 2012.09.13 BUG 5579 QDV4易方达2012年9月10日01_B start---//
								//先插入运营应收应付数据，然后获取 运营应收应付数据编号 插入 资金调拨数据 的 FIPRNum 字段中，  插入资金调拨数据，以便生成支付费用的凭证
								strIVPayCatCodes = rs.getString("FIVPayCatCode");
								investpayAdmin.insert(dDate, dDate, YssOperCons.YSS_ZJDBLX_Fee,
					    		YssOperCons.YSS_ZJDBZLX_IV_Fee, strIVPayCatCodes,sPortCode, "", 0);
								investpayAdmin.getList().clear();//插入后清空插入列表  以防重复插入
								
								createCashTransfer(rs,dDMoney,investpayAdmin.getInsertNum()); // 此处产生资金调拨
								//---edit by songjie 2012.09.13 BUG 5579 QDV4易方达2012年9月10日01_B end---//
								
								//20130206 added by liubo.Story #3414
								//每生成一笔两费的运营应收应付和资金调拨数据，就生成该笔两费的划款手续费
								//为了不影响总体的两费的支付逻辑，在生成划款手续费出错的情况下，不直接抛异常，只需要将错误信息传回前台
								//=====================================
								try
								{
									if (rs.getInt("FCommission") == 1)
									{
										double dFeeMoney = doOpertionExchangeStock(dDMoney);
								        String cashFNum = createCashTransfer(rs.getString("FIVPayCatCode"),dFeeMoney);
								        createDataIntegrated(cashFNum);
									}
								}
								catch(Exception ye)
								{
									this.sMsg += "运营收支品种为【" + rs.getString("FIVPayCatCode") + "】的两费生成划款手续费出错：\r\n" + ye.getMessage() + "\r\n" ;
								}
								//==================end===================
							}
						}
					}
				}
				//==============end=================
				
				//delete by songjie 2012.09.13 BUG 5579 QDV4易方达2012年9月10日01_B 运营应收应付已改为结果集循环内部 插入
				//strIVPayCatCodes += rs.getString("FIVPayCatCode")+",";
			}
			//---delete by songjie 2012.09.13 BUG 5579 QDV4易方达2012年9月10日01_B start---//
			//运营应收应付已改为结果集循环内部 插入
//		     // BUG3161在做完收益支付业务处理后，产生的运营费用没有了  add by jiangshichao 2011.11.15  start
//			 if (strIVPayCatCodes.length() > 1) {
//	                strIVPayCatCodes = strIVPayCatCodes.substring(0, strIVPayCatCodes.length() - 1);
//	                
//	                //运营应收应付应该在这里删除，不然的话，如果用户没有设置自动支付，由于获取不到运营品种代码会把所有的运营费用数据删除
//	                investpayAdmin.insert(dDate, dDate, YssOperCons.YSS_ZJDBLX_Fee,
//	    					YssOperCons.YSS_ZJDBZLX_IV_Fee, strIVPayCatCodes,
//	    					sPortCode, "", 0);
//	            }
//			 // BUG3161在做完收益支付业务处理后，产生的运营费用没有了 add by jiangshichao 2011.11.15  end 
			//---delete by songjie 2012.09.13 BUG 5579 QDV4易方达2012年9月10日01_B end---//
			//20121113 added by liubo.Bug #6221
			//执行自动支付的两费条数小于0，即当天不存在可以进行支付的两费，需要对运营应收应付进行一次删除操作
			//避免出现某个设置了自动支付的两费数据，取消自动支付，运营应收应付会一致存在的情况
			if(cashtransAdmin != null && cashtransAdmin.getAddList() != null && cashtransAdmin.getAddList().size() > 0){ //add by huangqirong 2013-01-14 bug #6829
				if (iOperCount <= 0)
				{
					investpayAdmin.delete(dDate, dDate, YssOperCons.YSS_ZJDBLX_Fee, YssOperCons.YSS_ZJDBZLX_IV_Fee, "", sPortCode, "", "", "", -1, "", "");
				}
			
				cashtransAdmin.insert("", dDate, dDate, YssOperCons.YSS_ZJDBLX_Fee,
						YssOperCons.YSS_ZJDBZLX_IV_Fee, "", "", "", "", "",
						"managetrusteeFee", "", 1, "", sPortCode, 0, "", "", "", true, "");// 插入资金调拨，传入调拨日期、业务日期、调拨类型、调拨子类型、关联编号类型、组合代码和自动录入标志来删除原有调拨数据
			}//add by huangqirong 2013-01-14 bug #6829
			
 			
 			//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		//当日产生数据，则认为有业务。
    		if(cashtransAdmin.getAddList()==null ||cashtransAdmin.getAddList().size()==0){
    			this.sMsg="        当日无业务";
    		}
		} catch (SQLException e) {
			throw new YssException(e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs,rsTemp);
		}

	}

	/**
	 * 产生资金调拨
	 * 
	 * @throws YssException
	 */
	//edit by songjie 2012.09.13 BUG 5579 QDV4易方达2012年9月10日01_B 
	//添加 运营应收应付数据编号 以便生成费用支付凭证
	//20120514 modified by liubo.Story #2217
	//新的自动支付方式要求根据不同的补差时期取不同时期的库存来作为支付金额，以dDMoney变量直接控制支付金额
	private void createCashTransfer(ResultSet rs, double dDMoney, String IPNum) throws YssException {
		/*
		 * 调拨类型为03费用，子类型为03IV支出运营款项， 调拨日期和业务日期都为T日， 投资组合为费用所关联组合，现金账户为费用关联账户，
		 * 调拨方向为流出，汇率取当日或之前最近那天的汇率， 金额为上月或上季度最后一天的该品种应付款项（子类型为运营费用）
		 */

		TransferBean transfer = null;
		TransferSetBean transferSet = null;
		ArrayList subTransfer = null;
	
		try {
			//edit by songjie 2012.09.13 BUG 5579 QDV4易方达2012年9月10日01_B 添加 IPNum
			transfer = setTransfer(rs,IPNum); // 获取资金调拨数据
			transferSet = setTransferSet(rs,dDMoney); // 获取资金调拨子数据
			subTransfer = new ArrayList(); // 实例化放置资金调拨子数据的容器
			subTransfer.add(transferSet); // 将资金调拨子数据放入容器
			transfer.setSubTrans(subTransfer); // 将子数据放入资金调拨中
			cashtransAdmin.addList(transfer);
			
		} catch (Exception e) {
			throw new YssException("设置资金调拨子数据出现异常！", e);
		}
	}

	/**
	 * 运营应收应付
	 * 
	 * @throws YssException
	 */

	//20120514 modified by liubo.Story #2217
	//新的自动支付方式要求根据不同的补差时期取不同时期的库存来作为支付金额，以dDMoney变量直接控制支付金额
	private void createCashPayRecAdmin(ResultSet rs, double dDMoney) throws YssException {
		/*
		 * 业务日期为T日，投资组合为费用所关联组合， 运营收支品种代码为该费用品种，币种取该费用关联账户币种，
		 * 业务类型为03费用，子类型为03IV支出运营款项， 汇率取当日或之前最近那天的汇率，
		 * 金额为上月或上季度最后一天的该品种应付款项（子类型为运营费用）
		 */
		InvestPayRecBean invest = null;

		/*  investpayAdmin = new InvestPayAdimin();
          investpayAdmin.setYssPub(pub);*/
		try {
				invest = setInvest(rs,dDMoney);
				invest.setTradeDate(dDate);
				investpayAdmin.addList(invest);
				iOperCount ++;//20121113 added by liubo.Bug #6221.记录需要进行生成的两费数据，用于判断当天有无进行自动支付的两费数据
		} catch (Exception ex) {
			throw new YssException("生成运营应收应付数据出现异常！", ex);
		}
	}

	//20120514 modified by liubo.Story #2217
	//新的自动支付方式要求根据不同的补差时期取不同时期的库存来作为支付金额，以dDMoney变量直接控制支付金额
	  private InvestPayRecBean setInvest(ResultSet rs, double dDMoney) throws YssException {
		  InvestPayRecBean invest = new InvestPayRecBean();
		  double BaseCuryRate = 0;
	        double PortCuryRate = 0;
	        double Money = 0;
	        double BaseMoney = 0;
	        double PortMoney = 0;
	        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
	        try {
	        	//如果有昨日的现金库存，用昨天的现金库存算基础汇率和组合汇率，如果没有取今天的基础汇率和组合汇率
	        //	if(rs.getDouble("FYesAccBal") ==0){
	                BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取两费用支付当日的基础汇率
	    	                rs.getString("FCurrencyCode"), rs.getString("FPortCode"),
	    	                YssOperCons.YSS_RATE_BASE);

	    	            rateOper.setYssPub(pub);
	    	            rateOper.getInnerPortRate(dDate, rs.getString("FCurrencyCode"),
	    	                                      rs.getString("FPortCode"));
	    	            PortCuryRate = rateOper.getDPortRate(); //获取两费用支付当日的组合汇率	
	        /*	}else{
	        		//基础汇率A = 昨日库存应付基础货币总额  / 昨日库存应付原币总额，
	        		//组合汇率B = 昨日库存应付基础货币总额  / 昨日库存应付组合货币总额
	        		BaseCuryRate=YssD.div(rs.getDouble("FYESTERDAYBBAL"),rs.getDouble("FYESTERDAYBAL"));
	        		PortCuryRate=YssD.div(rs.getDouble("FYESTERDAYBBAL"),rs.getDouble("FYESTERDAYPBAL"));
	        	}*/
	 
	        	//20120514 modified by liubo.Story #2217
	    	    //==================================
//	            Money = rs.getDouble("FLastMonthBal"); //原币金额为上月或上季度最后一天的该品种应付款项库存（子类型为运营费用）
	    	    Money = dDMoney;
	    	    //================end==================
	          //基础货币金额 = 原币金额 * 基础汇率A；
	         //   BaseMoney = YssD.mul(Money, BaseCuryRate);
	        	//组合货币金额 = 原币金额 * 基础汇率 / 组合汇率B
	       //     PortMoney = YssD.div(BaseMoney,PortCuryRate );
	            
	            BaseMoney = this.getSettingOper().calBaseMoney(Money, BaseCuryRate); //计算基础货币金额
	            PortMoney = this.getSettingOper().calPortMoney(Money, BaseCuryRate,
	                PortCuryRate,
	                rs.getString("FCurrencyCode"),
	                dDate,
	                rs.getString("FPortCode")); //计算组合货币金额
	            
	            invest.setTradeDate(dDate);//设置业务日期
	            invest.setBaseCuryRate(BaseCuryRate);//
	            invest.setPortCuryRate(PortCuryRate);//
	            invest.setMoney(Money);
	            invest.setBaseCuryMoney(BaseMoney);
	            invest.setPortCuryMoney(PortMoney);
	            invest.setFIVPayCatCode(rs.getString("FIVPayCatCode"));
	            invest.setPortCode(rs.getString("FPortCode"));
	            invest.setAnalysisCode1(rs.getString("FAnalysisCode1"));
	            invest.setTradeDate(rs.getDate("FACBeginDate"));
	            invest.setTsftTypeCode(YssOperCons.YSS_ZJDBLX_Fee);
	            invest.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_IV_Fee);
	            invest.setCuryCode(rs.getString("FCurrencyCode"));
	            invest.setCheckState(1); //设置为审核状态，默认情况下为为审核状态
	        } catch (Exception e) {
	            throw new YssException("设置运营应收应付数据时出现异常！", e);
	        }
	        return invest;
	        
	  }

		//20120514 modified by liubo.Story #2217
		//新的自动支付方式要求根据不同的补差时期取不同时期的库存来作为支付金额，以dDMoney变量直接控制支付金额
	private TransferSetBean setTransferSet(ResultSet rs, double dDMoney) throws YssException {
	    TransferSetBean transferSet = null;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        try {
            transferSet = new TransferSetBean();
            double dBaseRate = 1;
            double dPortRate = 1;

          //  if (rs.getDouble("FYesAccBal") ==0){               
                dBaseRate = this.getSettingOper().getCuryRate(dDate,
                    rs.getString("FCurrencyCode"), rs.getString("FPortCode"),
                    YssOperCons.YSS_RATE_BASE); //获取业务当天的基础汇率

                rateOper.setYssPub(pub);
                rateOper.getInnerPortRate(dDate, rs.getString("FCurrencyCode"),
                                          rs.getString("FPortCode"));
                dPortRate = rateOper.getDPortRate(); //获取业务当天的组合汇率
           /* }else{
            	//基础汇率 = 昨日现金库存基础货币总额 / 昨日现金库存原币总额，
            	//组合汇率 = 昨日现金库存基础货币总额  /  昨日现金库存组合货币总额
            	dBaseRate=YssD.div(rs.getDouble("FYesAccBBal"),rs.getDouble("FYesAccBal"));
            	dPortRate=YssD.div(rs.getDouble("FYesAccBBal"),rs.getDouble("FYesAccPBal"));
            }*/


            transferSet.setIInOut( -1); //流出
            transferSet.setSPortCode(rs.getString("FPortCode"));
            transferSet.setSAnalysisCode1(null == rs.getString("FAnalysisCode1") ? "" :
                                          rs.getString("FAnalysisCode1"));
            transferSet.setSAnalysisCode2(null == rs.getString("FAnalysisCode2") ? "" :
                                          rs.getString("FAnalysisCode2"));
            transferSet.setSAnalysisCode3(null == rs.getString("FAnalysisCode3") ? "" :
                                          rs.getString("FAnalysisCode3"));
            transferSet.setSCashAccCode(rs.getString("FCASHACCCODE")); //设置现金账户
            //20120514 modified by liubo.Story #2217
            //===================================
//            transferSet.setDMoney(rs.getDouble("FLastMonthBal")); //调拨金额为上月或上季度最后一天的该品种应付款项库存（子类型为运营费用）
            transferSet.setDMoney(dDMoney);
            //================end===================
            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
		return transferSet;
	}

	//edit by songjie 2012.09.13 BUG 5579 QDV4易方达2012年9月10日01_B 添加 IPNum
	private TransferBean setTransfer(ResultSet rs, String IPNum) throws YssException {
		TransferBean transfer = null;
		try {
			// 关联编号的设置问题
			transfer = new TransferBean();
			transfer.setDtTransDate(dDate); // 业务日期为T日
			transfer.setDtTransferDate(dDate); // 调拨日期也为T日
			transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Fee);
			transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_IV_Fee);
			transfer.setFNumType("managetrusteeFee"); // 设置关联编号类型为两费
			//add by songjie 2012.09.13 BUG 5579 QDV4易方达2012年9月10日01_B 关联 运营应收应付编号
			transfer.setFIPRNum(IPNum);
			transfer.checkStateId = 1;
			transfer.setDataSource(1);
		} catch (Exception e) {
			throw new YssException("设置资金调拨数据出现异常！", e);
		}
		return transfer; // 返回资金调拨数据
	}

	/**
	 * 运营应收应付的sql语句
	 * 
	 * @return
	 * @throws YssException 
	 */
	private String builderCashPayRec() throws YssException {
		String strSql = "";
		
		// 获取当月1号
		String strDate = String.valueOf(YssFun.getYear(dDate)) + "-"
				+ String.valueOf(YssFun.getMonth(dDate)) + "-1";
		Date date = YssFun.toDate(strDate);
		
		// 查询出需要自动支付的运营费用
		// 日期。。品种条件待改
		strSql = "SELECT y.*,CA.FCuryCode as FCurrencyCode " +
					",SI.FLastMonthBal,SI.FLastMonthPBal,SI.FLASTMONTHBBAL "+
					//deleted by liubo.Story #2217
					//================================
//					" SI2.FYESTERDAYBAL,SI2.FYESTERDAYPBAL,SI2.FYESTERDAYBBAL,"+
//					" SC.FYesAccBal,SC.FYesAccPBal,SC.FYesAccBBal "+
					//===========end=====================
					
				" FROM ("
				+ " SELECT IP.*,IP1.*,IP3.Fpaydate,IP3.FPeriodOfBC,ip3.fsupplementdates " 
				+ ", Nvl(FCommission,0) as FCommission "	//20130206 added by liubo.Story #3414.支付两费时是否自动支付划款手续费
				+ " FROM ( SELECT FIVPayCatCode as IPfivpaycatcode ,FPortCode as IPFPortCode,max(fstartdate) as IPFstartdate from " //modify huangqirong 2013-02-02 story #3488 增加ip3.fsupplementdates 补差日期
				+ pub.yssGetTableName("Tb_Para_InvestPay")
				+ " GROUP BY FIVPayCatCode,FPortCode) IP " +
				//20120514 added by liubo.Story #2217
				//====================================
				" Left Join " + pub.yssGetTableName("Tb_Para_InvestPay") + " IP3 on IP.IPfivpaycatcode = IP3.FIVPayCatCode and IP.IPFPortCode = IP3.FPortCode and IP.IPFstartdate = IP3.FStartDate " +
				//===============end=====================
				" JOIN ( "
				+ " SELECT FIVPayCatCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCuryCode,"
				+" FAutoPayDay, FHolidaysCode,FAutoPayType,"
				+ " FCashAccCode,FACRoundCode,FACBeginDate,FACEndDate,FExpirDate,FACTotalMoney,MAX(FStartDate) AS FStartDate "
				+ " FROM "
				+ pub.yssGetTableName("Tb_Para_InvestPay")
				+ " GROUP BY FIVPayCatCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCuryCode,FCashAccCode,FACRoundCode,FACBeginDate,FACEndDate,FExpirDate,FACTotalMoney,FAutoPayDay, FHolidaysCode,FAutoPayType)"
				+ " IP1 ON IP.IPfivpaycatcode = IP1.FIVPayCatCode and IP.IPFPortCode = IP1.FPortCode and IP.IPFstartdate = IP1.FStartDate JOIN (SELECT FIVPayCatCode as bFIVPayCatCode,FPortCode as bFPortCode "
				+ " FROM "
				+ pub.yssGetTableName("Tb_Para_InvestPay")
				+ " WHERE FCHECKSTATE = 1 AND FAutoPay='1'  "
				+" and FStartDate<"+ dbl.sqlDate(dDate)
				+ " and FPortCode = "
				+ dbl.sqlString(this.sPortCode)
				+ " ) IP2 "
				+ " on IP1.FIVPayCatCode = IP2.bFIVPayCatCode  and IP1.FPortCode = IP2.bFPortCode"
				+ " join (select * from Tb_Base_InvestPayCat where FIVType in ('managetrusteeFee') and FCHECKSTATE = 1"
				+ // 两费
				" ) IPC on IPC.FIVPayCatCode = IP1.FIVPayCatCode"
				+ " ) y " +
				// 关联运营应收应付库存，如果上个月未是休息或没有库存，取离上月最近的库存
				//上月未最近的运营应收应付库存
				"left join (select FBal as FLastMonthBal,FPortCuryBal as FLastMonthPBal,FBaseCuryBal as FLastMonthBBal,FPortCode,FIVPayCatCode,max(FStorageDate) from "+ 
				pub.yssGetTableName("Tb_Stock_InvestPayRec") +
					" where FCHECKSTATE = 1 and  FTsfTypeCode ='07' and FSubTsfTypeCode ='07IV' and FStorageDate= "+dbl.sqlDate(YssFun.addDay(date, -1))+"  group by FPortCode,FIVPayCatCode,FBal,FPortCuryBal,FBaseCuryBal) SI"+
						" on (SI.Fportcode=y.FPortCode and SI.Fivpaycatcode=y.FIVPayCatCode)"+
				//昨日的运营应收应付库存（用来计算基础汇率和组合汇率）
						
				//deleted by liubo.Story #2217
				//================================
//				"left join (select FBal as FYesterdayBal,FPortCuryBal as FYesterdayPBal,FBaseCuryBal as FYesterdayBBal,FPortCode,FIVPayCatCode from "+ 
//				pub.yssGetTableName("Tb_Stock_InvestPayRec") +
//							" where FTsfTypeCode ='07' and FSubTsfTypeCode ='07IV'and FStorageDate = "+dbl.sqlDate(YssFun.addDay(dDate, -1))+") SI2"+
//							" on (SI2.Fportcode=y.FPortCode and SI2.Fivpaycatcode=y.FIVPayCatCode)"+
//				//昨日的现金库存（用来计算资金调拨的基础汇和组合汇率）
//				" left join (select FAccBalance as FYesAccBal,FPortCuryBal as FYesAccPBal,FBaseCuryBal as FYesAccBBal,FPortCode,FCashAccCode from  "+pub.yssGetTableName("Tb_Stock_Cash") +
//					" where FCHECKSTATE = 1 and FStorageDate ="+ dbl.sqlDate(YssFun.addDay(dDate, -1)) +") SC on (SC.FPortCode = y.FPortCode and SC.FCashAccCode =y.FCashAccCode)"+
//					// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码

				//=================end===============
						
					" left join (select FCashAccCode,FCuryCode,FStartDate from "
				+ pub.yssGetTableName("Tb_Para_CashAccount")
		
				/*+ " where FStartDate <= "
				+ dbl.sqlDate(dDate)*/
				+ " where   "
				
				//end by lidaolong
				+ "  FCheckState = 1 and FState =0 ) CA on CA.FCashAccCode = y.FCashAccCode";
		
		
		return strSql;
	}

	/**
	 * 判断当天是否支付9
	 * BUG 1956 当支付日期选择为每个月第1天的时候，跑不出来自动支付数据,获取节假日代码有漏洞。
	 * @param holidaysCode
	 * @param dDay
	 * @return
	 * @throws YssException
	 */
	public boolean isAutoPay(String holidaysCode, int dDay)
			throws YssException {
		boolean isPay = false;// 是否支付
//		ResultSet rs = null;
//		String strSql = "select FDate from Tb_Base_ChildHoliday where FHolidaysCode ="
//				+ dbl.sqlString(holidaysCode) + " and FCheckState=1 ";
//		String holidaysDate = "";
//		try {
//			rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
//			while (rs.next()) {
//				holidaysDate += YssFun.formatDate(rs.getDate("FDate"),"yyyy-MM-dd") + ",";
//			}
//
//			// 获取当月1号
//			String strDate = String.valueOf(YssFun.getYear(dDate)) + "-"
//					+ String.valueOf(YssFun.getMonth(dDate)) + "-1";
//			Date date = YssFun.toDate(strDate);
//
//			
//			// 取出第N天支付的具体日期
//			while (dDay > 0) {			
//				if (holidaysDate.indexOf(strDate) == -1) {
//					dDay--;
//				}
//				if (dDay ==0 ){
//					break;
//				}
//				date = YssFun.addDay(date, 1);
//				strDate = YssFun.formatDate(date, "yyyy-MM-dd");
//			}
//
//			// 判断是否支付日期
//			if (strDate.equals(YssFun.formatDate(dDate))) {
//				isPay = true;
//			}
//
//		} catch (Exception e) {
//			throw new YssException(e.getMessage() /* "访问节假日表出错！" */);
//		} finally {
//			dbl.closeResultSetFinal(rs);
//		}
		//zhouss 20110518 获取上个月最后一天 然后往后推算工作日
		Date date = YssFun.yssGetMinDate(YssFun.getYear(dDate),YssFun.getMonth(dDate));
		String strDate = YssFun.formatDate(this. getWorkDay(holidaysCode, YssFun.addDay(date, -1), dDay), "yyyy-MM-dd");
		// 判断所推算工作日 和 业务处理所选工作日 是否相同
		if (strDate.equals(YssFun.formatDate(dDate))) {
			isPay = true;
		}
		return isPay;
	}
	
    /**
     * getWorkDay
     * BUG 1956 当支付日期选择为每个月第1天的时候，跑不出来自动支付数据,获取节假日代码有漏洞。
     * @param HolidaysCode String   //节假日群代码
     * @dDate Date                     //传入日期 上个月最后一天
     * @lOffset int                    //传入延迟天数
     * @return Date
     */
    private java.util.Date getWorkDay(String getHolidaysCode,
                                      java.util.Date dDate, int lOffset) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        int lTmp = 0, lStep;
        try {
            lStep = (lOffset < 0) ? -1 : 1;
            strSql =
                "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = " +
                dbl.sqlString(getHolidaysCode) + " and FDate " +
                ( (lOffset < 0) ? "<=" : ">=")
                + dbl.sqlDate(dDate, false) + " order by FDate " +
                ( (lOffset < 0) ? " desc" : "");
            rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            if (rs.next()) {
                do {
                    if (YssFun.dateDiff(rs.getDate("FDate"), dDate) == 0) {
                        rs.next();
                        if (lTmp == 0) {
                            lTmp += lStep;
                        }
                    } else {
                        if (Math.abs(lTmp) >= Math.abs(lOffset)) {
                            break;
                        }
                        lTmp += lStep;
                    }
                    dDate = YssFun.addDay(dDate, lStep);
                } while (!rs.isAfterLast());
                if (rs.isAfterLast()) {
                    throw new YssException( ( (lOffset < 0) ? "上" : "下") +
                                           "一个工作日已经超越节假日的边界，请增加节假日定义！");
                }
            }
            return dDate;
        } catch (Exception e) {
            throw new YssException("访问节假日表出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

	/**
	 * 初始化信息
	 */
	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.dDate = dDate; // 调拨日期
		this.sPortCode = portCode; // 组合
		
	}
	/**生成划款手续费的资金调拨
     * @throws YssException
     * @author 刘博 ,20130207 , STORY 3414
     */
    private String createCashTransfer(String sIVPayCatCode,double feeMoney) throws YssException {
        ResultSet rs = null;
        String fNum = "";
    	try {
    		
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
    			if (rs.getString("FFormulaCode")==null || rs.getString("FRoundCode")==null) {
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
