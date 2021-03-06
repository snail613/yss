package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.yss.commeach.EachRateOper;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.manager.TradeDataAdmin;
import com.yss.pojo.cache.YssCost;

/**
 * add by songjie 2012.03.01 
 * STORY #2014 QDV4赢时胜(上海开发部)2011年12月14日01_A
 * @author 宋洁
 *
 */
public class DmsDivIvstDeal extends BaseOperManage{
	private String dividendIvstNum = "";
	EachRateOper rateOper = null;
	CashTransAdmin cashtransAdmin = null;
    ArrayList<String> alDeleteDate = new ArrayList<String>();//调拨日期 + 业务日期
    boolean analy1 = false;
    boolean analy2 = false;
    double dBaseRate = 1;//基础汇率
    double dPortRate = 1;//组合汇率
    
	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.dDate = dDate; // 业务日期
		this.sPortCode = portCode; // 组合
	}
	
	/**
	 * 做分红转投业务处理
	 */
	public void doOpertion() throws YssException {
		Connection conn = dbl.loadConnection();
   	 	boolean bTrans = false;
   	 	
   	 	ArrayList<CashPecPayBean> alCashRecPay = new ArrayList<CashPecPayBean>();
   	 	ArrayList<TradeSubBean> alTradeSub = new ArrayList<TradeSubBean>();
   	 	try{
	        rateOper = new EachRateOper(); //新建获取利率的通用类
	        rateOper.setYssPub(pub);
   	 		
            cashtransAdmin = new CashTransAdmin(); // 生成资金调拨控制类
    		cashtransAdmin.setYssPub(pub);
	        
   	 		createData(alTradeSub, alCashRecPay);
   	 		
            conn.setAutoCommit(false);
            bTrans = true;
            
            if(alTradeSub.size() > 0){
            	TradeDataAdmin tradeAdmin = new TradeDataAdmin();
   	 			tradeAdmin.setYssPub(pub);
   	 		
   	 			String subTradeNum = getSubTradeCodes();
   	 			tradeAdmin.addAll(alTradeSub);

   	 			tradeAdmin.insert(subTradeNum, this.dDate, this.dDate, null, null, "", 
   	 				              sPortCode, "", "", "39", "", "", true, false, "");
            }
            
            if(alCashRecPay.size() > 0){
            	CashPayRecAdmin cashPayAdmin = new CashPayRecAdmin();
            	cashPayAdmin.setYssPub(pub);
            	cashPayAdmin.getList().addAll(alCashRecPay);
            	
                cashPayAdmin.insert("",dDate,dDate,"","","","",sPortCode,"","","",
                        0,true,false,false,0,"",YssOperCons.YSS_SECRECPAY_RELATYPE_DIVIDINVEST);
            }
            
    		if(cashtransAdmin.getAddList().size() > 0){
    			if(alDeleteDate.size() > 0){
    				Iterator<String> iter = alDeleteDate.iterator();
    				while(iter.hasNext()){
    					String deleteDates = (String)iter.next();
    					java.util.Date transferDate = YssFun.parseDate(deleteDates);//调拨日期
    					
    					cashtransAdmin.delete("", transferDate, null, "02", "02DV", "", "", "", "", "",
    							YssOperCons.YSS_SECRECPAY_RELATYPE_DIVIDINVEST, "", 1, "", 
    							this.sPortCode, 0, "", "", "", "");
    				}
    			}
    			
    			cashtransAdmin.insert("", dDate, dDate, "02", "02DV", "", "", "", "", "",
					YssOperCons.YSS_SECRECPAY_RELATYPE_DIVIDINVEST, "", 1, "", 
					this.sPortCode, 0, "", "", "", false, "", "");
    		}
			
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
   	 	}catch(Exception e){
			throw new YssException(e.getMessage());
   	 	}finally{
			dbl.endTransFinal(conn, bTrans);
   	 	}
	}
	
	/**
	 * 生成分红转投数据对应的交易数据 和 现金应收应付数据
	 * @param alTradeSub
	 * @param alCashRecPay
	 * @throws YssException
	 */
	private void createData(ArrayList<TradeSubBean> alTradeSub,ArrayList<CashPecPayBean> alCashRecPay) throws YssException{
		StringBuffer strSql = new StringBuffer();
		ResultSet rs = null;

   	 	TradeSubBean tradeSub = null;
   	 	CashPecPayBean cashRecPay = null;
   		String cashAccCode = "";
   		String cashCjAccCode = "";
   		String caTradeAccCode = "";
   		double tradeCuryBsRate = 1;//交易币种基础汇率
   		double divCuryBsRate = 1;//分红币种基础汇率
   		double tradeCashCuryRate = 1;//原分红数据对应的现金账户币种
   		long sNum = 0; //为了产生的交易编号不重复
   		double dMoney = 0;
   		double dBaseMoney = 0;
   		double dPortMoney = 0;
   		boolean haveInfo = false;//判断是否有需要处理的分红转投数据
   	 	try{
			BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.getOperDealCtx().getBean("cashacclinkdeal");
			cashacc.setYssPub(pub);
   	 	
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            
            //查找业务日期等于到账日期、相关组合对应的已审核的分红转投数据，生成分红转投对应的交易数据
            strSql.append(" select a.FNum as FInum, a.FSecurityCode as IFSecurityCode,a.FPortCode as IFPortCode, ");
            strSql.append(" a.FPayDate, a.FBusinessDate, a.FConfirmAmount, a.FConfirmMoney, a.FAdjustMoney, ");
            strSql.append(" a.FPrice, a.FCuryCode, a.FReceiveMoney, a.FInAccType, a.FAccCode, ");
            strSql.append(" sec.FTradeCury, cash1.FCashAccCuryCode, cash2.FDivAccCuryCode, b.* from ");
            strSql.append( pub.yssGetTableName("Tb_Data_DividendToInvest"));
            strSql.append(" a left join (select * from " + pub.yssGetTableName("Tb_Data_SubTrade"));
            strSql.append(" where FCheckState = 1 and FTradeTypeCode = '06') b on a.FTradeNum = b.FNum ");
            strSql.append(" join (select FSecurityCode, FTradeCury from " + pub.yssGetTableName("Tb_Para_Security"));
            strSql.append(" ) sec on a.FSecurityCode = sec.FSecurityCode ");
            strSql.append(" left join (select FCashAccCode, FCuryCode as FCashAccCuryCode from ");
            strSql.append( pub.yssGetTableName("Tb_Para_CashAccount") + ") cash1 on cash1.FCashAccCode = a.FAccCode ");
            strSql.append(" left join (select FCashAccCode, FCuryCode as FDivAccCuryCode from ");
            strSql.append( pub.yssGetTableName("Tb_Para_CashAccount") + ") cash2 on cash2.FCashAccCode = b.FCashAccCode ");
            strSql.append(" where a.FCheckState = 1 and a.FBusinessDate = " + dbl.sqlDate(this.dDate));
            strSql.append(" and a.FPortCode = " + dbl.sqlString(this.sPortCode));

            rs = dbl.queryByPreparedStatement(strSql.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			if (rs.next()) {
				rs.beforeFirst(); // 返回第一个rs
				// --------------------拼接交易编号---------------------
				String strNumDate = YssFun.formatDatetime(dDate).substring(0, 8);
				strNumDate = strNumDate
						+ dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Trade"), dbl.sqlRight("FNUM", 6),
								"100000",// 将000000改为100000
								// 分红转投编号调整为100000开始
								" where FNum like 'T" + strNumDate + "1%'", 1);
				strNumDate = "T" + strNumDate;
				strNumDate = strNumDate
						+ dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_SubTrade"), dbl.sqlRight("FNUM", 5),
								"00000", " where FNum like '" + strNumDate.replaceAll("'", "''") + "%'");
				String s = strNumDate.substring(9, strNumDate.length());
				sNum = Long.parseLong(s);

				while (rs.next()) {
					if (rs.getString("FNum") == null) {
						continue;
					}
					dMoney = rs.getDouble("FTotalCost");
					dBaseMoney = this.getSettingOper().calBaseMoney(dMoney, rs.getDouble("FBaseCuryRate"), 2);
					dPortMoney = this.getSettingOper().calPortMoney(dMoney, rs.getDouble("FBaseCuryRate"),
							rs.getDouble("FPortCuryRate"), rs.getString("FDivAccCuryCode"), dDate,
							rs.getString("FPortCode"));
					cashCjAccCode = rs.getString("FCashAccCode");

					// 获取交易方式为 分发派息 、币种为分红货币 的现金账户连接设置数据
					if (rs.getString("FCuryCode") != null) {
						cashAccCode = getAccCodeByLink(YssFun.formatDate(dDate, "yyyy-MM-dd"), 
								rs.getString("IFPortCode"), rs.getString("FCuryCode"), "06");
					}

					dBaseRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FTradeCury"),
							rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);// 获取交易货币对应的基础汇率的值

					if (rs.getString("FCuryCode") != null) {
						divCuryBsRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FCuryCode"),
								rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);// 获取分红货币基础汇率
					}

					tradeCashCuryRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FDivAccCuryCode"),
							rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);// 获取原分红权益数据对应现金账户币种的基础汇率

					tradeCuryBsRate = dBaseRate;// 交易币种基础汇率

					rateOper.getInnerPortRate(dDate, rs.getString("FTradeCury"), rs.getString("FPortCode"));

					dPortRate = rateOper.getDPortRate();// 获取交易货币对应的组合汇率的值

					if (rs.getDouble("FConfirmAmount") > 0) {
						if (rs.getString("FTradeCury") != null) {
							caTradeAccCode = getAccCodeByLink(YssFun.formatDate(dDate, "yyyy-MM-dd"), rs
									.getString("IFPortCode"), rs.getString("FTradeCury"), "");
							if (caTradeAccCode.trim().equals("")) {
								throw new YssException("请在现金账户链接设置中设置交易货币【" + rs.getString("FTradeCury") + "】对应的分发派息现金账户!");
							}
						}

						tradeSub = new TradeSubBean();

						// --------------------拼接交易编号---------------------
						sNum++;
						String tmp = "";
						for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
							tmp += "0";
						}
						strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
						// ------------------------end--------------------------//

						tradeSub.setNum(strNumDate);
						createSubTradeData(tradeSub, rs, caTradeAccCode);
						alTradeSub.add(tradeSub);
					}

					dBaseRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FDivAccCuryCode"),
							rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);// 获取06分发派息现金账户币种对应的基础汇率的值
					
					rateOper.getInnerPortRate(dDate, rs.getString("FDivAccCuryCode"), rs.getString("FPortCode"));

					dPortRate = rateOper.getDPortRate();// 获取06分发派息现金账户币种对应的组合汇率的值
					
					// 生成 应收红利金额 = - 确认金额 的现金应收应付数据
					if (rs.getDouble("FConfirmMoney") != 0) {
						if(!haveInfo){
							haveInfo = true;
						}
						cashRecPay = new CashPecPayBean();

						setCashRecPay(cashRecPay, rs, dBaseRate, dPortRate, "06", "06DV");

						cashRecPay.setCashAccCode(rs.getString("FCashAccCode"));

						cashRecPay.setMoney(-YssD.round(YssD.div(YssD.mul(rs.getDouble("FConfirmMoney"),
								tradeCuryBsRate), tradeCashCuryRate), 2));

						cashRecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(cashRecPay.getMoney(),
								dBaseRate, 2));
						cashRecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(cashRecPay.getMoney(),
								dBaseRate, dPortRate, rs.getString("FTradeCury"), dDate, cashRecPay.getPortCode(), 2));

						alCashRecPay.add(cashRecPay);

						dMoney = YssD.add(dMoney, cashRecPay.getMoney());
						dBaseMoney = YssD.add(dBaseMoney, cashRecPay.getBaseCuryMoney());
						dPortMoney = YssD.add(dPortMoney, cashRecPay.getPortCuryMoney());
					}
					// 生成现金应收应付 的 调整金额
					if (rs.getDouble("FAdjustMoney") != 0) {
						if(!haveInfo){
							haveInfo = true;
						}
						cashRecPay = new CashPecPayBean();

						setCashRecPay(cashRecPay, rs, dBaseRate, dPortRate, "06", "06DV_TZ");

						cashRecPay.setCashAccCode(rs.getString("FCashAccCode"));
						cashRecPay.setMoney(-rs.getDouble("FAdjustMoney"));
						cashRecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(cashRecPay.getMoney(),
								dBaseRate, 2));

						cashRecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(cashRecPay.getMoney(),
								dBaseRate, dPortRate, rs.getString("FTradeCury"), dDate, cashRecPay.getPortCode(), 2));

						alCashRecPay.add(cashRecPay);

						dMoney = YssD.add(dMoney, cashRecPay.getMoney());
						dBaseMoney = YssD.add(dBaseMoney, cashRecPay.getBaseCuryMoney());
						dPortMoney = YssD.add(dPortMoney, cashRecPay.getPortCuryMoney());
					}

					// 若 入账方式 = 按最终分红币种入账
					if (rs.getString("FInAccType").equals("01")) {
						if (rs.getDouble("FReceiveMoney") != 0) {
							double convertReceiveM = -YssD.round(YssD.div(YssD.mul(rs.getDouble("FReceiveMoney"),
									divCuryBsRate), tradeCashCuryRate), 2);// 折算后的到账金额

							// 若到帐币种与分红权益信息设置中的币种不一致
							if ((rs.getString("FCashAccCuryCode") != null && !rs.getString("FCashAccCuryCode").equals(
									rs.getString("FDivAccCuryCode")))
									|| (rs.getString("FCashAccCuryCode") == null && !rs.getString("FCuryCode").equals(
											rs.getString("FDivAccCuryCode")))) {

								if(!haveInfo){
									haveInfo = true;
								}
								// 产生一笔现金应收应付数据（现金到账金额反冲应收股息数据）
								cashRecPay = new CashPecPayBean();

								cashRecPay.setCashAccCode(rs.getString("FCashAccCode"));
								setCashRecPay(cashRecPay, rs, dBaseRate, dPortRate, "06", "06DV");
								cashRecPay.setMoney(convertReceiveM);
								cashRecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(cashRecPay.getMoney(),
										dBaseRate, 2));
								cashRecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(cashRecPay.getMoney(),
										dBaseRate, dPortRate, rs.getString("FTradeCury"), dDate,
										cashRecPay.getPortCode(), 2));
								alCashRecPay.add(cashRecPay);

								// 产生实际到帐币种的应收股利
								cashRecPay = new CashPecPayBean();

								getRate(rs);
								
								setCashRecPay(cashRecPay, rs, dBaseRate, dPortRate, "06", "06DV");
								if (rs.getString("FAccCode") != null) {
									cashRecPay.setCashAccCode(rs.getString("FAccCode"));
									cashRecPay.setCuryCode(rs.getString("FCashAccCuryCode"));
								} else {
									if (cashAccCode.trim().equals("")) {
										throw new YssException("请在现金账户链接设置中设置分红币种【"+ rs.getString("FCuryCode") +"】对应的分发派息现金账户!");
									}
									cashRecPay.setCashAccCode(cashAccCode);
									cashRecPay.setCuryCode(rs.getString("FCuryCode"));
								}
								double dBaseRateX = this.getSettingOper().getCuryRate(dDate, cashRecPay.getCuryCode(),
										rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);
								cashRecPay.setMoney(rs.getDouble("FReceiveMoney"));
								cashRecPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(cashRecPay.getMoney(),
										dBaseRateX));

								rateOper.getInnerPortRate(dDate, cashRecPay.getCuryCode(), rs.getString("FPortCode"));
								double dPortRateX = rateOper.getDPortRate();
								cashRecPay.setPortCuryMoney(this.getSettingOper().calPortMoney(cashRecPay.getMoney(),
										dBaseRateX, dPortRateX, cashRecPay.getCuryCode(), dDate,
										cashRecPay.getPortCode(), 2));
								alCashRecPay.add(cashRecPay);

								dMoney = cashRecPay.getMoney();
								dBaseMoney = cashRecPay.getBaseCuryMoney();
								dPortMoney = cashRecPay.getPortCuryMoney();
								cashCjAccCode = cashRecPay.getCashAccCode();
							}
						}
					}
					
					updateReceiveMoney(rs.getString("FInum"), cashCjAccCode, dMoney, dBaseMoney, dPortMoney);

					if(rs.getDouble("FReceiveMoney") != 0){
						if(!haveInfo){
							haveInfo = true;
						}
						getRate(rs);
						createTsfOfReceiveM(rs, cashAccCode);
					}
				}
			}
			
			if(!haveInfo){
				this.sMsg = "        当日无业务";
			}
   	 	}catch(Exception e){
			throw new YssException(e.getMessage());
   	 	}finally{
			dbl.closeResultSetFinal(rs);
   	 	}
	}

	private void getRate(ResultSet rs)throws YssException {
		try{
		if (rs.getString("FAccCode") != null) {
			dBaseRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FCashAccCuryCode"),
					rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);// 获取到账现金账户币种对应的基础汇率的值
			
			rateOper.getInnerPortRate(dDate, rs.getString("FCashAccCuryCode"), rs.getString("FPortCode"));

			dPortRate = rateOper.getDPortRate();// 获取06分发派息现金账户币种对应的组合汇率的值
		}else{
			dBaseRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FCuryCode"),
					rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);// 获取到账现金账户币种对应的基础汇率的值
			
			rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));

			dPortRate = rateOper.getDPortRate();// 获取06分发派息现金账户币种对应的组合汇率的值
		}
		}catch(Exception e){
			throw new YssException("获取汇率数据出错!", e);
		}
	}
	
	/**
	 * 获取现金账户链接设置对应的现金账户代码
	 * @param businessDate
	 * @param portCode
	 * @param curyCode
	 * @return
	 * @throws YssException
	 */
	private String getAccCodeByLink(String businessDate, String portCode, String curyCode, String tradeTypeCode) throws YssException {
		String cashAccCode = "";
		StringBuffer strSql = new StringBuffer();
		ResultSet rs = null; 
		try {
			strSql.append(" select a.* from (select FInvMgrCode, FPortCode, FCatCode, FSubCatCode, ");
			strSql.append(" FBrokerCode, FTradeTypeCode, FExchangeCode, FLinkLevel, FAuxiCashAccCode, ");
			strSql.append(" FCashAccCode, max(FStartDate) as FStartDate, FCuryCode from " + pub.yssGetTableName("Tb_Para_CashAccLink"));
			strSql.append(" where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(businessDate));
			strSql.append(" and (FSubCatCode = '' or FSubCatCode = ' ') and (FInvMgrCode = '' or FInvMgrCode = ' ') ");
			strSql.append((tradeTypeCode.equals("") ? "" : " and FTradeTypeCode = '06' "));
			strSql.append(" and (FBrokerCode = '' or FBrokerCode = ' ') ");
			strSql.append(" and (FExchangeCode = '' or FExchangeCode = ' ') " + (curyCode.equals("") ? "" : " and FCuryCode = " + dbl.sqlString(curyCode)));
			strSql.append( (portCode.equals("") ? "" : " and FPortCode = " + dbl.sqlString(portCode)));
			strSql.append(" group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode, ");
			strSql.append(" FAuxiCashAccCode, FTradeTypeCode, FExchangeCode, FCashAccCode, ");
			strSql.append(" FLinkLevel, FCuryCode) a join (select FCashAccCode, max(FStartDate) as FStartDate from ");
			strSql.append(pub.yssGetTableName("Tb_Para_CashAccount"));
			strSql.append(" where FCheckState = 1 " + (curyCode.equals("") ? "" : " and FCuryCode = " + dbl.sqlString(curyCode)));
			strSql.append(" and FStartDate <= " + dbl.sqlDate(businessDate));
			strSql.append(" group by FCashAccCode) b on a.FCashAccCode = b.FCashAccCode order by a.FLinkLevel desc ");

			rs = dbl.openResultSet(strSql.toString());
			if(rs.next()){
				cashAccCode = rs.getString("FCashAccCode");
			}
			
			return cashAccCode;
		} catch (Exception e) {
			throw new YssException("获取现金账户链接数据出错！", e);
		} finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 更新到帐金额的基础货币金额和组合货币金额及币种对应的现金账户
	 * @param sFNum
	 * @param sCashAccCode
	 * @param dBaseMoney
	 * @param dPortMoney
	 * @throws YssException
	 */
	private void updateReceiveMoney(String sFNum, String sCashAccCode,
			double dMoney, double dBaseMoney, double dPortMoney)
			throws YssException {
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		PreparedStatement pst = null;
		String strSql = "";
		try {
			bTrans = true;
			conn.setAutoCommit(false);
			strSql = "update "
					+ pub.yssGetTableName("Tb_Data_DividendToInvest")
					+ " set FCJCOST = ? ,FBASECJCOST = ? , FPORTCJCOST = ? , fCuryCashAccCode = ?"
					+ " where fnum = ?";
			pst = conn.prepareStatement(strSql);
			pst.setDouble(1, dMoney);
			pst.setDouble(2, dBaseMoney);
			pst.setDouble(3, dPortMoney);
			pst.setString(4, sCashAccCode);
			pst.setString(5, sFNum);

			pst.executeUpdate();
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
   	 	} catch (Exception e) {
			throw new YssException(e.getMessage());
   	 	} finally {
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
   	 	}
	}

	/**
	 * 产生一笔资金调拨（现金实际到账数据）
	 * @param rs
	 * @throws YssException
	 */
	private void createTsfOfReceiveM(ResultSet rs, String cashAccCode) throws YssException{
		TransferBean transfer = null;
		TransferSetBean transferSet = null;
		ArrayList<TransferSetBean> subTransfer = new ArrayList<TransferSetBean>();
		try{
			transfer = setTransfer(rs); // 获取资金调拨数据
			transferSet = setTransferSet(rs, cashAccCode); // 获取资金调拨子数据
            transferSet.setDBaseRate(dBaseRate);
            transferSet.setDPortRate(dPortRate);
            
			subTransfer = new ArrayList<TransferSetBean>(); // 实例化放置资金调拨子数据的容器
			subTransfer.add(transferSet); // 将资金调拨子数据放入容器
			transfer.setSubTrans(subTransfer); // 将子数据放入资金调拨中
			
			cashtransAdmin.addList(transfer);
		}catch(Exception e){
			throw new YssException("生成资金调拨数据出错！", e);
		}
	}
	
	/**
	 * 设置资金调拨数据
	 * @param rs
	 * @return
	 * @throws YssException
	 */
	private TransferBean setTransfer(ResultSet rs) throws YssException {
		TransferBean transfer = null;
		try {
			// 关联编号的设置问题
			transfer = new TransferBean();
		
			transfer.setDtTransDate(rs.getDate("FPayDate")); // 业务日期为分红转投结算日

			transfer.setDtTransferDate(rs.getDate("FPayDate")); // 调拨日期为分红转投结算日
			
			// 调拨日期
			String keyDate = YssFun.formatDate(transfer.getDtTransferDate(),"yyyy-MM-dd");
			
			if(!alDeleteDate.contains(keyDate)){
				alDeleteDate.add(keyDate);
			}

			transfer.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_Income);//02 收入
			transfer.setStrSubTsfTypeCode("02DV");//股票分红
			transfer.setFNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_DIVIDINVEST); // 分红转投业务
			transfer.setFRelaNum(rs.getString("FInum"));
			transfer.checkStateId = 1;
			transfer.setDataSource(1);
		} catch (Exception e) {
			throw new YssException("设置资金调拨数据出现异常！", e);
		}
		return transfer; // 返回资金调拨数据
	}
	
	/**
	 * 设置资金调拨子数据
	 * @param rs
	 * @return
	 * @throws YssException
	 */
	private TransferSetBean setTransferSet(ResultSet rs, String cashAccCode) throws YssException {
	    TransferSetBean transferSet = null;
        try {
            transferSet = new TransferSetBean();

            transferSet.setIInOut(1); //流入
            transferSet.setSPortCode(rs.getString("FPortCode"));
            transferSet.setSAnalysisCode1(analy1 ? rs.getString("FInvMgrCode") : " ");
            transferSet.setSAnalysisCode2(analy2 ? rs.getString("FCusCatCode") : " ");
            transferSet.setSAnalysisCode3(" ");
            if(rs.getString("FAccCode") != null && !rs.getString("FAccCode").trim().equals("")){
            	transferSet.setSCashAccCode(rs.getString("FAccCode")); //现金到账账户代码
            }else{
				if (cashAccCode.trim().equals("")) {
					throw new YssException("请在现金账户链接设置中设置分红币种【"+ rs.getString("FCuryCode") +"】对应的分发派息现金账户!");
				}
            	transferSet.setSCashAccCode(cashAccCode); //分红转投币种 对应的 根据 分红现金账户链接找到的现金账户代码
            }

            transferSet.setDMoney(rs.getDouble("FReceiveMoney")); //调拨金额为到账金额
            transferSet.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置资金调拨子数据出现异常！", e);
        }
		return transferSet;
	}
	
	
	/**
	 * 生成交易数据
	 * @param rs
	 * @throws YssException
	 */
	private void createSubTradeData(TradeSubBean tradeSub,ResultSet rs, String tradeAccCode)throws YssException{
		try{
			tradeSub.setSecurityCode(rs.getString("IFSecurityCode"));//证券代码赋值
			tradeSub.setPortCode(rs.getString("IFPortCode"));//组合代码              
			tradeSub.setAttrClsCode(rs.getString("FAttrClscode"));
			tradeSub.setInvMgrCode(rs.getString("FInvMgrCode"));
			tradeSub.setBrokerCode(rs.getString("FBrokerCode"));
			tradeSub.setTradeCode("39");// 交易类型 分红转投
			tradeSub.setAllotProportion(0);// 分配比例
			tradeSub.setOldAllotAmount(0);// 原始分配数量
			tradeSub.setAllotFactor(0);// 分配因子
			tradeSub.setBargainDate(YssFun.formatDate(this.dDate));// 成交日期
			tradeSub.setBargainTime("00:00:00");// 成交时间
			tradeSub.setSettleDate(YssFun.formatDate(this.dDate));// 结算日期
			tradeSub.setSettleTime("00:00:00");// 结算时间
			tradeSub.setAutoSettle(new Integer(1).toString()); // 自动结算
			tradeSub.setPortCuryRate(dPortRate);// 组合汇率
			tradeSub.setBaseCuryRate(dBaseRate);// 基础汇率
			tradeSub.setTradeAmount(rs.getDouble("FConfirmAmount"));// 交易数量
			tradeSub.setTradePrice(rs.getDouble("FPrice"));// 交易价格
			tradeSub.setTradeMoney(rs.getDouble("FConfirmMoney"));// 交易金额
   
			YssCost cost = new YssCost();
    
			cost.setCost(rs.getDouble("FConfirmMoney"));
			cost.setMCost(rs.getDouble("FConfirmMoney"));
			cost.setVCost(rs.getDouble("FConfirmMoney"));
			cost.setBaseCost(YssD.round(YssD.mul(rs.getDouble("FConfirmMoney"),dBaseRate), 2));
			cost.setBaseMCost(YssD.round(YssD.mul(rs.getDouble("FConfirmMoney"),dBaseRate), 2));
			cost.setBaseVCost(YssD.round(YssD.mul(rs.getDouble("FConfirmMoney"),dBaseRate), 2));
	
			if (dPortRate != 0) {
				cost.setPortCost(YssD.round(YssD.div(cost.getBaseCost(),dPortRate), 2));
				cost.setPortMCost(YssD.round(YssD.div(cost.getBaseMCost(), dPortRate), 2));
				cost.setPortVCost(YssD.round(YssD.div(cost.getBaseVCost(), dPortRate), 2));
			} else {
				cost.setPortCost(0);
				cost.setPortMCost(0);
				cost.setPortVCost(0);
			}
	
			tradeSub.setCost(cost);// 成本

			tradeSub.setDataSource(0);// 数据源
			tradeSub.setDsType("HD_QY");// 操作类型，表示系统操作数据，主要是和接口导入数据进行区分
    
			tradeSub.checkStateId = 1;// 审核状态
			tradeSub.creatorCode = pub.getUserCode();// 创建人
			tradeSub.checkTime = YssFun.formatDatetime(new java.util.Date());// 审核时间
			tradeSub.checkUserCode = pub.getUserCode();// 审核人
			tradeSub.creatorTime = YssFun.formatDatetime(new java.util.Date());// 创建时间
    
			tradeSub.setTotalCost(0);
			tradeSub.setSettleState(new Integer(0).toString());// 结算状态，未结算“0”
			tradeSub.setFactSettleDate(YssFun.formatDate(rs.getDate("FFactSettleDate")));// 实际结算日期
			tradeSub.setMatureDate("9998-12-31");// 到期日期
			tradeSub.setMatureSettleDate("9998-12-31");// 到期结算日期
			tradeSub.setFactCashAccCode(rs.getString("FCashAccCode"));// 实际结算帐户

			tradeSub.setCashAcctCode(tradeAccCode);// 设置现金账户
			tradeSub.setFactSettleMoney(0);// 实际结算金额
			tradeSub.setExRate(1);// 兑换汇率
			tradeSub.setFactPortRate(dPortRate); // 实际结算组合汇率
			tradeSub.setFactBaseRate(dBaseRate);// 实际结算基础汇率
			tradeSub.setStrDivdendType("");// 分红类型
			tradeSub.setStrRecordDate("9998-12-31");
    
			tradeSub.setFdealNum(rs.getString("FINum"));
			tradeSub.setDataBirth(YssOperCons.YSS_SECRECPAY_RELATYPE_DIVIDINVEST);
		}catch(Exception e){
			throw new YssException("生成交易数据出错！",e);
		}
	}
	

	/**
	 * 设置现金应收应付数据
	 * @param cashRecPay
	 * @param rs
	 * @param dBaseRate
	 * @param dPortRate
	 * @param tsfTypeCode
	 * @param subTsfTypeCode
	 * @throws YssException
	 */
	private void setCashRecPay(CashPecPayBean cashRecPay, ResultSet rs, double dBaseRate, 
			double dPortRate,String tsfTypeCode, String subTsfTypeCode) throws YssException{
		try{
        cashRecPay.setRelaNum(rs.getString("FINum"));
        cashRecPay.setRelaNumType(YssOperCons.YSS_SECRECPAY_RELATYPE_DIVIDINVEST);
        cashRecPay.setTradeDate(dDate);
        cashRecPay.setPortCode(rs.getString("FPortCode"));
        cashRecPay.setInvestManagerCode(rs.getString("FInvMgrCode"));
        cashRecPay.setBrokerCode(rs.getString("FBrokerCode"));
        cashRecPay.setStrAttrClsCode(rs.getString("FAttrClsCode"));
        cashRecPay.setCuryCode(rs.getString("FDivAccCuryCode"));
        
        cashRecPay.setDataSource(0);
        cashRecPay.setStockInd(1);
        cashRecPay.checkStateId = 1;
        cashRecPay.setTsfTypeCode(tsfTypeCode);
        cashRecPay.setSubTsfTypeCode(subTsfTypeCode);
        cashRecPay.setInOutType(1);
        
        cashRecPay.setBaseCuryRate(dBaseRate);
        cashRecPay.setPortCuryRate(dPortRate);
        

		} catch(Exception e){
			throw new YssException("设置现金应收应付数据出错！",e);
		}
	}
	
	/**
	 * 获取分红转投相关的已审核、交易类型为分红转投的交易数据编号
	 * @return
	 * @throws YssException
	 */
    public String getSubTradeCodes() throws YssException{
    	String relaNums = this.dividendIvstNum;
    	ResultSet rs = null;
    	String subTradeNum = "";
    	try{
    		String sql = "";
	    	if(!relaNums.equals("")){
	    		sql = "select * from "+pub.yssGetTableName("tb_data_subtrade") +
	    		" where fdealnum in ("+operSql.sqlCodes(relaNums)+") and FTradeTypeCode = '39' and FCheckState = 1 ";
	    		rs=dbl.openResultSet(sql);
	    		while(rs.next()){
	    			subTradeNum += rs.getString("fnum") + ",";
	    		}
	    		
	    		if(subTradeNum.length() > 1){
	    			subTradeNum = subTradeNum.substring(0, subTradeNum.length() - 1);
	    		}
	    	}
	    	return subTradeNum;
    	}catch(Exception e){
    		throw new YssException("获取交易编号出错", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
}
