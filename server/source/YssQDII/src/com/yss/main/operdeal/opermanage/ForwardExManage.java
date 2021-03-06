package com.yss.main.operdeal.opermanage;

import java.util.*;

import com.yss.util.*;
import com.yss.commeach.EachRateOper;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdata.RateTradeBean;
import com.yss.main.operdeal.BaseCashAccLinkDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.pojo.cache.YssTradeAcc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Title: 远期外汇交易的业务处理</p>
 *
 * <p>Description: 处理远期外汇交易业务的类</p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * <p>Company: Ysstech </p>
 *
 * @author fangjiang
 * @version 1.0
 * STORY #262 #393
 */

public class ForwardExManage extends BaseOperManage {
	
	private boolean bstate=true;//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
	
	public void initOperManageInfo(Date dDate, String portCode) throws YssException {
       this.dDate = dDate;
       this.sPortCode = portCode;
    }
	
	public void doOpertion() throws YssException {
		doForwardMature();     //远期外汇交易的到期处理
		doForwardOffSetting(); //远期外汇交易的平仓处理
		doForwardRollBack();   //远期外汇交易的提前交割处理
		doForwardRollOver();   //远期外汇交易的展期交割处理
		
		//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
		//当日产生数据，则认为有业务。
		if(bstate){
			this.sMsg="        当日无业务";
		}
		
    }
	
    public void doForwardMature() throws YssException {
    	ResultSet rs = null;
		String strSql = getSqlForMature();
		try {
			rs = dbl.queryByPreparedStatement(strSql);
	        while (rs.next()) {
	        	if(rs.getString("FJENum") != null){
	        		createJECashTrans(rs); 
	        	}else{
	        		createMatureCashTrans(rs);
	        	}	        	
	        }
		} catch (Exception e) {
            throw new YssException("远期外汇交易处理出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }	
    }
    
    public void doForwardOffSetting() throws YssException {
    	ResultSet rs = null;
		String strSql = getSqlForOffSetting();
		try {
			rs = dbl.queryByPreparedStatement(strSql);
	        while (rs.next()) {
	        	/**shashijie 2011.04.11 STORY #670 外汇远期交易到期不交收，用反向交易进行平仓，只交收净损益，实现净收益的交收日可选择
	        	 * 如果交易日与交收日相同,则走原来流程*/
	        	if (rs.getDate("FTradeDate").equals(rs.getDate("FTranDate"))) {
	        		createOffCashTrans(rs);
				} else {
					/**若业务日是交易日产生资金调拨与现金应收应付数据*/
					if (this.dDate.equals(rs.getDate("FTradeDate"))) {
						createCashAndTrans(rs);
					} /**若业务日是交收日则产生现金应收应付数据冲减之前的现金应收应付数据*/
					else if (this.dDate.equals(rs.getDate("FTranDate"))) {
						createCashShould(rs,"02");//02 指交收日业务处理
					}
				}
	        	/**end*/
	        }
		} catch (Exception e) {
            throw new YssException("远期外汇交易处理出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }	
    }
    
	public void doForwardRollBack() throws YssException {
    	ResultSet rs = null;
		String strSql = getSqlForRollBack();
		try {
			rs = dbl.queryByPreparedStatement(strSql);
	        while (rs.next()) {
	        	if(rs.getString("FJENum") != null){
	        		createJECashTrans(rs);
	        	}else{
	        		createRollBackCashTrans(rs);
	        	}
	        }	        
		} catch (Exception e) {
            throw new YssException("远期外汇交易处理出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }	
    }
    
    public void doForwardRollOver() throws YssException {
    	ResultSet rs = null;
		String strSql = getSqlForRollOver();
		try {
			rs = dbl.queryByPreparedStatement(strSql);
	        while (rs.next()) {
	        	if(rs.getString("FJENum") != null){
	        		createJECashTrans(rs);
	        	}else{
	        		createRollOverCashTrans(rs);  //资金调拨
	        	}	        	
	        	createCashRecPay(rs);         //现金应收应付
	        }
		} catch (Exception e) {
            throw new YssException("远期外汇交易处理出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //modify by fangjiang 2012.02.10 story 2195
    public String getSqlForMature() throws YssException {
    	String offNum = "";
		String tradeNum = "";
		String offLimit = "";
		String settleLimit = "";
		String strSql = "";
		ResultSet rs = null;
		try {
	    	strSql = " select FOffNum from " + pub.yssGetTableName("Tb_Data_ForwardTrade") + " where FCheckState = 1 and FTradeType = '21' ";
	        rs = dbl.queryByPreparedStatement(strSql);
			while (rs.next()) {
				offNum += operSql.sqlCodes(rs.getString("FOffNum")) + ",";
			}
			if(offNum.equals("")){
				offNum = "' '";
			}else{
				offNum = offNum.substring(0,offNum.length()-1);
			}
			offLimit = " and FNum not in (" + offNum + ")";  //查询未被平仓的编号
			dbl.closeResultSetFinal(rs);
			
			/*strSql = " select FTradeNum from " + pub.yssGetTableName("Tb_Data_FwTradeSettle") + " where FCheckState = 1 ";
	        rs = dbl.queryByPreparedStatement(strSql);
			while (rs.next()) {
				tradeNum += operSql.sqlCodes(rs.getString("FTradeNum")) + ",";
			}
			if(tradeNum.equals("")){
				tradeNum = "' '";
			}else{
				tradeNum = tradeNum.substring(0,tradeNum.length()-1);
			}
			settleLimit = " and FNum not in (" + tradeNum + ")"; //查询未被交割的编号
			dbl.closeResultSetFinal(rs);*/

			strSql = " select a.*, b.Fcashacccode as FBCashAccCode, c.Fcashacccode as FSCashAccCode, " +
					 " d.fnum as FJENum, d.ftradenum as FJETradeNum, d.fsettledate as FJESettleDate," +
					 " d.fcashacccode as FJECashAccCode, d.fmoney as FJEMoney, d.finout as FJEInOut, " +
					 " e.FBCapMoney as FBCapMoney, e.FSCapMoney as FSCapMoney " +
					 " from (select * from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
			         " where FCheckState = 1 and FTradeType = '20' and FMatureDate = " + dbl.sqlDate(this.dDate) +
			         " and FPortCode = " + dbl.sqlString(this.sPortCode) + offLimit  +
			         ") a join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
			         " where Facctype = 'BuyCap') b on a.FNum = b.FNum " +
			         " join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
			         " where Facctype = 'SellCap') c on a.FNum = c.FNum " +
			         " left join (select fnum, ftradenum, fsettledate, fcashacccode, fmoney, finout from " +
			         pub.yssGetTableName("tb_data_fwtradejesettle") + " where fsettletype = '0' and fCheckState = 1) d " +
			         " on a.fnum = d.ftradenum " +
			         " left join (select sum(FBCapMoney) as FBCapMoney, sum(FSCapMoney) as FSCapMoney, ftradenum from " + 
			         pub.yssGetTableName("Tb_Data_FwTradeSettle") + " where FCheckState = 1 and FSettleDate < " +
			         dbl.sqlDate(this.dDate) + " group by ftradenum) e on a.fnum = e.ftradenum " +
			         " where not exists (select Ftradenum from " + pub.yssGetTableName("tb_data_fwtradesettle") +
			         " where FCheckState = 1 and FSettleDate = " + dbl.sqlDate(this.dDate) + " and a.fnum = ftradenum) ";
		}
		catch (Exception e) {
            throw new YssException("远期外汇交易处理出现异常！",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
		
		return strSql;
	}
    
    public String getSqlForOffSetting() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		try {//shashijie 2011.04.11 修改         STORY #670  
			strSql = " select FNum, FPortCode, FTradeAmount, " +
					 " FMatureMoney, FBCashAccCode, FSCashAccCode, " +
					 " sum(FTradeAmount1) as FTradeAmount1, sum(FMatureMoney1) as FMatureMoney1 ,FTradeDate ,FTranDate, FBuyCuryCode, FSellCuryCode, FOffCury FROM " +
					 " ( select a.*, b.FTradeAmount as FTradeAmount1, b.FMatureMoney as FMatureMoney1," +
					 " c.Fcashacccode as FBCashAccCode , d.Fcashacccode as FSCashAccCode, f.FCuryCode as FBuyCuryCode, g.FCuryCode as FSellCuryCode from " +
					 " (select * FROM " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
			         " where FCheckState = 1 and FTradeType = '21' AND ( FTradeDate = " + dbl.sqlDate(this.dDate) +
			         " OR FTranDate = "+ dbl.sqlDate(this.dDate) +" ) " + //shashijie 原先只查询业务日==交易日期的数据,现在还得查出业务是==交收日的
			         " AND FPortCode = " + dbl.sqlString(this.sPortCode) + 
			         ") a join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
			         " where FCheckState = 1 and FTradeType = '20') b on Instr(a.FOffNum, b.FNum) > 0 " +
			         " join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
			         " where Facctype = 'BuyCap') c on a.FNum = c.FNum " +
			         " join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
			         " where Facctype = 'SellCap') d on a.FNum = d.FNum  " +
			         " join (select FCuryCode, FCashAccCode from " + pub.yssGetTableName("tb_Para_CashAccount") +
					 " where FCheckState = 1) f on c.Fcashacccode = f.FCashAccCode " +
			         " join (select FCuryCode, FCashAccCode from " + pub.yssGetTableName("tb_Para_CashAccount") +
					 " where FCheckState = 1) g on d.Fcashacccode = g.FCashAccCode) " +
			         " group by FNum, FPortCode, FTradeAmount, FMatureMoney, FBCashAccCode, FSCashAccCode, FTradeDate,FTranDate,FBuyCuryCode,FSellCuryCode, FOffCury ";
		}//end
		catch (Exception e) {
            throw new YssException("远期外汇交易处理出现异常！",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
		
		return strSql;
	}
    
    public String getSqlForRollBack() throws YssException {
    	String strSql = "";
		ResultSet rs = null;
		try {
			strSql =  " select a.*, b.FPortCode as FPortCode, " +
					 " c.Fcashacccode as FBCashAccCode , d.Fcashacccode as FSCashAccCode, " +
					 " e.fnum as FJENum, e.ftradenum as FJETradeNum, e.fsettledate as FJESettleDate, " +
					 " e.fcashacccode as FJECashAccCode, e.fmoney as FJEMoney, e.finout as FJEInOut from " +					 
					 "(select * from " + pub.yssGetTableName("Tb_Data_FwTradeSettle") +
			         " where FCheckState = 1 and FSettleDate = " + dbl.sqlDate(this.dDate) +
			         ") a join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
			         " where FCheckState = 1 and FTradeType = '20') b on a.FTradeNum = b.FNum " +
			         " join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
			         " where Facctype = 'BuyCap') c on a.FTradeNum = c.FNum " +
			         " join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
			         " where Facctype = 'SellCap') d on a.FTradeNum = d.FNum " +
			         " left join (select fnum, ftradenum, fsettledate, fcashacccode, fmoney, finout from " +
			         pub.yssGetTableName("tb_data_fwtradejesettle") + " where fsettletype = '1' and fCheckState = 1) e " +
			         " on a.fnum = e.ftradenum " +
			         " where a.FSettleDate < b.FMatureDate and b.FPortCode = " + dbl.sqlString(this.sPortCode);
		}
		catch (Exception e) {
            throw new YssException("远期外汇交易处理出现异常！",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
		
		return strSql;
	}
    
    public String getSqlForRollOver() throws YssException {
    	String strSql = "";
		ResultSet rs = null;
		try {
			strSql = " select a.*, b.FPortCode as FPortCode, b.FMatureDate as FMatureDate, " +
					 " b.FTradeAmount as FTradeAmount, b.FMatureMoney as FMatureMoney, " +
					 " c.Fcashacccode as FBCashAccCode, d.FCuryCode as FBCuryCode, " +
					 " e.Fcashacccode as FSCashAccCode, f.FCuryCode as FSCuryCode, " +					 
					 " g.fnum as FJENum, g.ftradenum as FJETradeNum, g.fsettledate as FJESettleDate, " +
					 " g.fcashacccode as FJECashAccCode, g.fmoney as FJEMoney, g.finout as FJEInOut, " +
					 " f.FBCapMoney as FBCapMoney1, f.FSCapMoney as FSCapMoney1 from " +
					 
					 "(select * from " + pub.yssGetTableName("Tb_Data_FwTradeSettle") +
			         " where FCheckState = 1 and FSettleDate = " + dbl.sqlDate(this.dDate) +
			         ") a join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
			         " where FCheckState = 1 and FTradeType = '20') b on a.FTradeNum = b.FNum " +
			         
			         " join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
			         " where Facctype = 'BuyCap') c on a.FTradeNum = c.FNum " +
			         
			         " join (select * from " + pub.yssGetTableName("Tb_Para_CashAccount") +
			         " where FCheckState = 1 ) d on c.FCashAccCode = d.FCashAccCode " +
			         
			         " join (select * from " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
			         " where Facctype = 'SellCap') e on a.FTradeNum = e.FNum " +
			         
			         " join (select * from " + pub.yssGetTableName("Tb_Para_CashAccount") +
			         " where FCheckState = 1 ) f on e.FCashAccCode = f.FCashAccCode " +
			         
			         " left join (select fnum, ftradenum, fsettledate, fcashacccode, fmoney, finout from " +
			         pub.yssGetTableName("tb_data_fwtradejesettle") + " where fsettletype = '2' and fCheckState = 1) g " +
			         " on a.fnum = g.ftradenum " +
			         
			         " left join (select sum(FBCapMoney) as FBCapMoney, sum(FSCapMoney) as FSCapMoney, ftradenum from " + 
			         pub.yssGetTableName("Tb_Data_FwTradeSettle") + " where FCheckState = 1 and FSettleDate < " +
			         dbl.sqlDate(this.dDate) + " group by ftradenum) f on a.ftradenum = f.ftradenum " +
			         
			         " where a.FSettleDate >= b.FMatureDate and b.FPortCode = " + dbl.sqlString(this.sPortCode);
		}
		catch (Exception e) {
            throw new YssException("远期外汇交易处理出现异常！",e);
        }
        finally{
            dbl.closeResultSetFinal(rs);
        }
		
		return strSql;
	}
    
    private void createMatureCashTrans(ResultSet rs) throws YssException, SQLException { 

		TransferSetBean transfersetIn = new TransferSetBean();
		TransferSetBean transfersetOut = new TransferSetBean();
		ArrayList tranSetList = new ArrayList();
		
		TransferBean tran = new TransferBean();
		tran.setYssPub(pub);
		tran.setDtTransDate(this.dDate); 
		//add by songjie 2011.06.15 BUG 2064 QDV4富国2011年6月09日01_B 
		//到期日做业务处理时，生成的资金调拨的调拨日期应为远期外汇交易数据的结算日期
		tran.setDtTransferDate(rs.getDate("FSettleDate"));
		//delete by songjie 2011.06.15 BUG 2064 QDV4富国2011年6月09日01_B
		//tran.setDtTransferDate(this.dDate);
		tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount); //"01"
		tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Forward); //"00005"
		tran.setDataSource(1); 
		tran.setFRelaNum(rs.getString("FNum")); //关联编号
		tran.setFNumType("Forward"); 
		tran.checkStateId = 1;
		tran.creatorTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
		tran.setDataSource(1); 
		
		//资金流入帐户
		if (getCuryTradeType(rs.getString("FNum")).equals("0")){
			transfersetIn.setDMoney(YssD.sub(rs.getDouble("FMatureMoney"), rs.getDouble("FBCapMoney")));
		} else {
			transfersetIn.setDMoney(YssD.sub(rs.getDouble("FTradeAmount"), rs.getDouble("FBCapMoney")));
		} 
		transfersetIn.setSPortCode(rs.getString("FPortCode")); // 组合代码
		transfersetIn.setSCashAccCode(rs.getString("FBCashAccCode")); // 现金帐户代码
		transfersetIn.setDBaseRate(getCuryRate(rs.getString("FBCashAccCode"), 
				                   this.dDate, 
								   0, 
								   rs.getString("FPortCode")));
		transfersetIn.setDPortRate(getCuryRate(rs.getString("FBCashAccCode"), 
				                   this.dDate, 
								   1, 
								   rs.getString("FPortCode")));
		transfersetIn.checkStateId = 1;
		transfersetIn.setIInOut(1);
		tranSetList.add(transfersetIn);
		
		//资金流出帐户
		if (getCuryTradeType(rs.getString("FNum")).equals("0")){
			transfersetOut.setDMoney(YssD.sub(rs.getDouble("FTradeAmount"), rs.getDouble("FSCapMoney")));
		} else {
			transfersetOut.setDMoney(YssD.sub(rs.getDouble("FMatureMoney"), rs.getDouble("FSCapMoney")));
		} 
		transfersetOut.setSPortCode(rs.getString("FPortCode")); // 组合代码
		transfersetOut.setSCashAccCode(rs.getString("FSCashAccCode")); // 现金帐户代码
		transfersetOut.setDBaseRate(getCuryRate(rs.getString("FSCashAccCode"), 
				                    this.dDate,
								   0, 
								   rs.getString("FPortCode")));
		transfersetOut.setDPortRate(getCuryRate(rs.getString("FSCashAccCode"), 
				                   this.dDate,
								   1, 
								   rs.getString("FPortCode")));
		transfersetOut.checkStateId = 1;
		transfersetOut.setIInOut( -1);
		tranSetList.add(transfersetOut);
		
		CashTransAdmin tranAdmin = new CashTransAdmin();
		tranAdmin.setYssPub(pub);

		tran.setSubTrans(tranSetList);
		tranAdmin.addList(tran);   
		//--- edit by songjie 2013.06.09 BUG 8199 QDV4赢时胜(上海)2013年06月07日01_B start---//	
		//业务日期 改为 结算日期
		tranAdmin.insert(rs.getDate("FSettleDate"), "Forward", 1, rs.getString("FNum"));	
		//--- edit by songjie 2013.06.09 BUG 8199 QDV4赢时胜(上海)2013年06月07日01_B end---//	
		this.bstate = false;
    }
    
	/**
     * modify by fangjiang 2011.07.18 STORY #1353 
     */
    private void createOffCashTrans(ResultSet rs) throws YssException, SQLException { 

		TransferSetBean transfersetIn = new TransferSetBean();
		TransferSetBean transfersetOut = new TransferSetBean();
		ArrayList tranSetList = new ArrayList();
		
		TransferBean tran = new TransferBean();
		tran.setYssPub(pub);
		tran.setDtTransDate(this.dDate); 
		tran.setDtTransferDate(this.dDate); 
		tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount); //"01"
		tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Forward); //"00005"
		tran.setDataSource(1); 
		tran.setFRelaNum(rs.getString("FNum")); //关联编号
		tran.setFNumType("Forward_Off"); 
		tran.checkStateId = 1;
		tran.creatorTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
		tran.setDataSource(1); 
		
		double tradeAmount = rs.getDouble("FTradeAmount");
		double matureMoney = rs.getDouble("FMatureMoney");
		double tradeAmount1 = rs.getDouble("FTradeAmount1");
		double matureMoney1 = rs.getDouble("FMatureMoney1");
		double offIncome = 0.0; //平仓收益
		double scale;
		double baseRate;
		double portRate;
		
		CashAccountBean caBean = null;//声明现金账户的bean
		String cashAccCode = "";
		
		if (getCuryTradeType(rs.getString("FNum")).equals("0")){
			scale = YssD.div(matureMoney, matureMoney1);
			offIncome = YssD.sub(YssD.mul(tradeAmount1, scale), tradeAmount);	
		} else {
			scale = YssD.div(tradeAmount, tradeAmount1);
			offIncome = YssD.sub(YssD.mul(matureMoney1, scale), matureMoney);		
		} 
		
		if(rs.getString("FOffCury").trim().length() == 0 || rs.getString("FOffCury").equalsIgnoreCase(rs.getString("FSellCuryCode"))){
			if(offIncome > 0){
				transfersetIn.setDMoney(offIncome);
				transfersetIn.setSPortCode(rs.getString("FPortCode")); // 组合代码
				transfersetIn.setSCashAccCode(rs.getString("FSCashAccCode")); // 现金帐户代码
				transfersetIn.setDBaseRate(getCuryRate(rs.getString("FSCashAccCode"), 
										   this.dDate, 
										   0, 
										   rs.getString("FPortCode")));
				transfersetIn.setDPortRate(getCuryRate(rs.getString("FSCashAccCode"), 
						                   this.dDate, 
										   1, 
										   rs.getString("FPortCode")));
				transfersetIn.checkStateId = 1;
				transfersetIn.setIInOut(1);
				tranSetList.add(transfersetIn);
			} else if(offIncome < 0){
				transfersetOut.setDMoney(YssD.mul(-1, offIncome));
				transfersetOut.setSPortCode(rs.getString("FPortCode")); // 组合代码
				transfersetOut.setSCashAccCode(rs.getString("FSCashAccCode")); // 现金帐户代码
				transfersetOut.setDBaseRate(getCuryRate(rs.getString("FSCashAccCode"), 
										   this.dDate, 
										   0, 
										   rs.getString("FPortCode")));
				transfersetOut.setDPortRate(getCuryRate(rs.getString("FSCashAccCode"), 
						                   this.dDate, 
										   1, 
										   rs.getString("FPortCode")));
				transfersetOut.checkStateId = 1;
				transfersetOut.setIInOut(-1);
				tranSetList.add(transfersetOut);
			}
		}else{
			if(rs.getString("FOffCury").equalsIgnoreCase(rs.getString("FBuyCuryCode"))){
				cashAccCode = rs.getString("FBCashAccCode"); // 现金帐户代码
			}else{
				BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.getOperDealCtx().getBean("cashacclinkdeal");
	            cashacc.setYssPub(pub);
	            cashacc.setLinkParaAttr("", this.sPortCode, "", "", "", this.dDate, rs.getString("FOffCury"), ""); //币种
	            caBean = cashacc.getCashAccountBean();
	            if (caBean != null) {
	            	cashAccCode = caBean.getStrCashAcctCode(); // 现金帐户代码
	            } else {
	                throw new YssException("请设置现金账户链接");
	            }
			}
			baseRate = this.getSettingOper().getCuryRate(
					  this.dDate, 
					  rs.getString("FSellCuryCode"), 
					  this.sPortCode, 
					  YssOperCons.YSS_RATE_BASE);
		    portRate = this.getSettingOper().getRate(
		 		      this.dDate, 
		 		      "",
				      rs.getString("FOffCury"), 
				      this.sPortCode);

		    offIncome =  YssD.round
		    			 (
	                     	YssD.div
	                     	(
	                     		YssD.mul
	                     		(
	                     			offIncome, 
	                     			baseRate
	                     		), 
	                     		portRate
                     		),
                            2
                        );
		    if(offIncome > 0){
				transfersetIn.setDMoney(offIncome);
				transfersetIn.setSPortCode(rs.getString("FPortCode")); // 组合代码
				transfersetIn.setSCashAccCode(cashAccCode); // 现金帐户代码
				transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(this.dDate, rs.getString("FOffCury"), this.sPortCode, YssOperCons.YSS_RATE_BASE));
				transfersetIn.setDPortRate(this.getSettingOper().getCuryRate(this.dDate, rs.getString("FOffCury"), this.sPortCode, YssOperCons.YSS_RATE_PORT));
				transfersetIn.checkStateId = 1;
				transfersetIn.setIInOut(1);
				tranSetList.add(transfersetIn);
			} else if(offIncome < 0){
				transfersetOut.setDMoney(YssD.mul(-1, offIncome));
				transfersetOut.setSPortCode(rs.getString("FPortCode")); // 组合代码
				transfersetOut.setSCashAccCode(cashAccCode); // 现金帐户代码
				transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(this.dDate, rs.getString("FOffCury"), this.sPortCode, YssOperCons.YSS_RATE_BASE));
				transfersetOut.setDPortRate(this.getSettingOper().getCuryRate(this.dDate, rs.getString("FOffCury"), this.sPortCode, YssOperCons.YSS_RATE_PORT));
				transfersetOut.checkStateId = 1;
				transfersetOut.setIInOut(-1);
				tranSetList.add(transfersetOut);
			}
		}		
		
		CashTransAdmin tranAdmin = new CashTransAdmin();
		tranAdmin.setYssPub(pub);

		tran.setSubTrans(tranSetList);
		tranAdmin.addList(tran);   
			
		tranAdmin.insert(this.dDate, "Forward_Off", 1, rs.getString("FNum"));	
		
		this.bstate = false;
    }
    
    private void createRollBackCashTrans(ResultSet rs) throws YssException, SQLException { 

		TransferSetBean transfersetIn = new TransferSetBean();
		TransferSetBean transfersetOut = new TransferSetBean();
		ArrayList tranSetList = new ArrayList();
		
		TransferBean tran = new TransferBean();
		tran.setYssPub(pub);
		tran.setDtTransDate(this.dDate); 
		tran.setDtTransferDate(this.dDate); 
		tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount); //"01"
		tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Forward); //"00005"
		tran.setDataSource(1); 
		tran.setFRelaNum(rs.getString("FNum")); //关联编号
		tran.setFNumType("Forward_RollBack"); 
		tran.checkStateId = 1;
		tran.creatorTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
		tran.setDataSource(1); 
		
		//资金流入帐户
		transfersetIn.setDMoney(rs.getDouble("FBCapMoney"));
		transfersetIn.setSPortCode(rs.getString("FPortCode")); // 组合代码
		transfersetIn.setSCashAccCode(rs.getString("FBCashAccCode")); // 现金帐户代码
		transfersetIn.setDBaseRate(getCuryRate(rs.getString("FBCashAccCode"), 
								   this.dDate, 
								   0, 
								   rs.getString("FPortCode")));
		transfersetIn.setDPortRate(getCuryRate(rs.getString("FBCashAccCode"), 
								   this.dDate, 
								   1, 
								   rs.getString("FPortCode")));
		transfersetIn.checkStateId = 1;
		transfersetIn.setIInOut(1);
		tranSetList.add(transfersetIn);
		
		//资金流出帐户
		transfersetOut.setDMoney(rs.getDouble("FSCapMoney"));
		transfersetOut.setSPortCode(rs.getString("FPortCode")); // 组合代码
		transfersetOut.setSCashAccCode(rs.getString("FSCashAccCode")); // 现金帐户代码
		transfersetOut.setDBaseRate(getCuryRate(rs.getString("FSCashAccCode"), 
								   this.dDate, 
								   0, 
								   rs.getString("FPortCode")));
		transfersetOut.setDPortRate(getCuryRate(rs.getString("FSCashAccCode"), 
								   this.dDate, 
								   1, 
								   rs.getString("FPortCode")));
		transfersetOut.checkStateId = 1;
		transfersetOut.setIInOut( -1);
		tranSetList.add(transfersetOut);
		
		CashTransAdmin tranAdmin = new CashTransAdmin();
		tranAdmin.setYssPub(pub);

		tran.setSubTrans(tranSetList);
		tranAdmin.addList(tran);   
			
		tranAdmin.insert(this.dDate, "Forward_RollBack", 1, rs.getString("FNum"));	
		
		this.bstate = false;
    }
    
    private void createRollOverCashTrans(ResultSet rs) throws YssException, SQLException { 

		TransferSetBean transfersetIn = new TransferSetBean();
		TransferSetBean transfersetOut = new TransferSetBean();
		ArrayList tranSetList = new ArrayList();
		
		TransferBean tran = new TransferBean();
		tran.setYssPub(pub);
		tran.setDtTransDate(this.dDate); 
		tran.setDtTransferDate(this.dDate); 
		tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount); //"01"
		tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Forward); //"00005"
		tran.setDataSource(1); 
		tran.setFRelaNum(rs.getString("FNum")); //关联编号
		tran.setFNumType("Forward_RollOver"); 
		tran.checkStateId = 1;
		tran.creatorTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
		tran.setDataSource(1); 
		
		//资金流入帐户
		transfersetIn.setDMoney(rs.getDouble("FBCapMoney"));
		transfersetIn.setSPortCode(rs.getString("FPortCode")); // 组合代码
		transfersetIn.setSCashAccCode(rs.getString("FBCashAccCode")); // 现金帐户代码
		transfersetIn.setDBaseRate(getCuryRate(rs.getString("FBCashAccCode"), 
								   this.dDate, 
								   0, 
								   rs.getString("FPortCode")));
		transfersetIn.setDPortRate(getCuryRate(rs.getString("FBCashAccCode"), 
								   this.dDate, 
								   1, 
								   rs.getString("FPortCode")));
		transfersetIn.checkStateId = 1;
		transfersetIn.setIInOut(1);
		tranSetList.add(transfersetIn);
		
		//资金流出帐户
		transfersetOut.setDMoney(rs.getDouble("FSCapMoney"));
		transfersetOut.setSPortCode(rs.getString("FPortCode")); // 组合代码
		transfersetOut.setSCashAccCode(rs.getString("FSCashAccCode")); // 现金帐户代码
		transfersetOut.setDBaseRate(getCuryRate(rs.getString("FSCashAccCode"), 
								   this.dDate, 
								   0, 
								   rs.getString("FPortCode")));
		transfersetOut.setDPortRate(getCuryRate(rs.getString("FSCashAccCode"), 
								   this.dDate, 
								   1, 
								   rs.getString("FPortCode")));
		transfersetOut.checkStateId = 1;
		transfersetOut.setIInOut( -1);
		tranSetList.add(transfersetOut);
		
		CashTransAdmin tranAdmin = new CashTransAdmin();
		tranAdmin.setYssPub(pub);

		tran.setSubTrans(tranSetList);
		tranAdmin.addList(tran);   
			
		tranAdmin.insert(this.dDate, "Forward_RollOver", 1, rs.getString("FNum"));
		
		this.bstate = false;
    }
    
    private void createCashRecPay(ResultSet rs) throws YssException, SQLException{
    	 boolean bTrans = false;
    	 Connection conn = dbl.loadConnection();	
    	 CashPecPayBean cashpecpayIn = null;
    	 CashPecPayBean cashpecpayOut = null;
    	 CashPayRecAdmin cashpayrecadminIn = new CashPayRecAdmin(); 
    	 CashPayRecAdmin cashpayrecadminOut = new CashPayRecAdmin(); 
    	 cashpayrecadminIn.setYssPub(pub);
    	 cashpayrecadminOut.setYssPub(pub);
		 int diff = YssFun.dateDiff(rs.getDate("FSettleDate"), rs.getDate("FMatureDate"));
		 double dBaseRateIn = getCuryRate(rs.getString("FBCashAccCode"), this.dDate, 0, rs.getString("FPortCode"));
	     double dPortRateIn = getCuryRate(rs.getString("FBCashAccCode"), this.dDate, 1, rs.getString("FPortCode"));
	     double dBaseRateOut = getCuryRate(rs.getString("FSCashAccCode"), this.dDate, 0, rs.getString("FPortCode"));
	     double dPortRateOut = getCuryRate(rs.getString("FSCashAccCode"), this.dDate, 1, rs.getString("FPortCode"));
	     double bacecurymoney = 0.0;
	     double portcurymoney = 0.0;
	     double moneyIn = 0.0;
	     double moneyOut = 0.0;
	     conn.setAutoCommit(false);
         bTrans = true;
    	 try{
    		 if(diff == 0) {
    			 //应收
    			 cashpecpayIn = new CashPecPayBean();
    			 cashpecpayIn.setTradeDate(this.dDate);//业务日期
    			 cashpecpayIn.setTsfTypeCode("06");//业务类型
    			 cashpecpayIn.setSubTsfTypeCode("06OT");//业务子类型
    			 cashpecpayIn.setNum(rs.getString("FNum"));
    			 cashpecpayIn.setRelaNum(rs.getString("FNum"));//关联编号
    			 cashpecpayIn.setRelaNumType("Forward_RollOver");//关联编号类型
    			 if (getCuryTradeType(rs.getString("FTradeNum")).equals("0")){
    				 moneyIn = YssD.sub(rs.getDouble("FMatureMoney"), 
    						 						rs.getDouble("FBCapMoney"), rs.getDouble("FBCapMoney1"));//金额
    			 }else{
    				 moneyIn = YssD.sub(rs.getDouble("FTradeAmount"), 
		 						rs.getDouble("FBCapMoney"), rs.getDouble("FBCapMoney1"));//金额
    			 }
    			 cashpecpayIn.setMoney(moneyIn);
    			 cashpecpayIn.setDataSource(1);//来源标志
    			 cashpecpayIn.checkStateId = 1;
    			 cashpecpayIn.setPortCode(rs.getString("FPortCode"));//组合代码  			 
    			 cashpecpayIn.setCashAccCode(rs.getString("FBCashAccCode"));//现金账户
    			 cashpecpayIn.setCuryCode(rs.getString("FBCuryCode"));//币种代码
    			 cashpecpayIn.setInOutType(1);//方向
    			 cashpecpayIn.setBaseCuryRate(dBaseRateIn);//基础汇率
    			 cashpecpayIn.setPortCuryRate(dPortRateIn);//组合汇率
    			 bacecurymoney = this.getSettingOper().calBaseMoney(moneyIn,dBaseRateIn);
    			 portcurymoney = this.getSettingOper().calPortMoney(
    					                      moneyIn,
    					                      dBaseRateIn,
    					                      dPortRateIn,
    					                      rs.getString("FBCuryCode"),
    					                      dDate,
    					                      sPortCode); 
    			 cashpecpayIn.setBaseCuryMoney(bacecurymoney);
    			 cashpecpayIn.setPortCuryMoney(portcurymoney);

    			 cashpayrecadminIn.addList(cashpecpayIn);
    			 cashpayrecadminIn.insert(dDate, "06", "06OT", "", 1, false, "", "Forward_RollOver", false);
                 
                 //应付
    			 cashpecpayOut = new CashPecPayBean();
    			 cashpecpayOut.setTradeDate(this.dDate);//业务日期
    			 cashpecpayOut.setTsfTypeCode("07");//业务类型
    			 cashpecpayOut.setSubTsfTypeCode("07OT");//业务子类型
    			 cashpecpayOut.setNum(rs.getString("FNum"));
    			 cashpecpayOut.setRelaNum(rs.getString("FNum"));//关联编号
    			 cashpecpayOut.setRelaNumType("Forward_RollOver");//关联编号类型
    			 if (getCuryTradeType(rs.getString("FTradeNum")).equals("0")){
    				 moneyOut = YssD.sub(rs.getDouble("FTradeAmount"), 
    						 						rs.getDouble("FSCapMoney"), rs.getDouble("FSCapMoney1"));//金额
    			 }else{
    				 moneyOut = YssD.sub(rs.getDouble("FMatureMoney"), 
		 						rs.getDouble("FSCapMoney"), rs.getDouble("FSCapMoney1"));//金额
    			 }
    			 cashpecpayOut.setMoney(moneyOut);
    			 cashpecpayOut.setDataSource(1);//来源标志
    			 cashpecpayOut.checkStateId = 1;
    			 cashpecpayOut.setPortCode(rs.getString("FPortCode"));//组合代码  			 
    			 cashpecpayOut.setCashAccCode(rs.getString("FSCashAccCode"));//现金账户
    			 cashpecpayOut.setCuryCode(rs.getString("FSCuryCode"));//币种代码
    			 cashpecpayOut.setInOutType(-1);//方向
    			 cashpecpayOut.setBaseCuryRate(dBaseRateOut);//基础汇率
    			 cashpecpayOut.setPortCuryRate(dPortRateOut);//组合汇率
    			 bacecurymoney = this.getSettingOper().calBaseMoney(moneyOut,dBaseRateOut);
    			 portcurymoney = this.getSettingOper().calPortMoney(
    					                         moneyOut,
    					                         dBaseRateOut,
    					                         dPortRateOut,
    					                         rs.getString("FSCuryCode"),
    					                         dDate,
    					                         sPortCode); 
    			 cashpecpayOut.setBaseCuryMoney(bacecurymoney);
    			 cashpecpayOut.setPortCuryMoney(portcurymoney);
    			 
    			 cashpayrecadminOut.addList(cashpecpayOut);
    			 cashpayrecadminOut.insert(dDate, "07", "07OT", "", 1, false, "", "Forward_RollOver", false);
    		 } else {
    			 //实应
    			 cashpecpayIn = new CashPecPayBean();
    			 cashpecpayIn.setTradeDate(this.dDate);//业务日期
    			 cashpecpayIn.setTsfTypeCode("02");//业务类型
    			 cashpecpayIn.setSubTsfTypeCode("02OT");//业务子类型
    			 cashpecpayIn.setNum(rs.getString("FNum"));
    			 cashpecpayIn.setRelaNum(rs.getString("FNum"));//关联编号
    			 cashpecpayIn.setRelaNumType("Forward_RollOver");//关联编号类型
    		     moneyIn = rs.getDouble("FBCapMoney");//金额
    			 cashpecpayIn.setMoney(moneyIn);
    			 cashpecpayIn.setDataSource(1);//来源标志
    			 cashpecpayIn.checkStateId = 1;
    			 cashpecpayIn.setPortCode(rs.getString("FPortCode"));//组合代码  			 
    			 cashpecpayIn.setCashAccCode(rs.getString("FBCashAccCode"));//现金账户
    			 cashpecpayIn.setCuryCode(rs.getString("FBCuryCode"));//币种代码
    			 cashpecpayIn.setInOutType(1);//方向
    			 cashpecpayIn.setBaseCuryRate(dBaseRateIn);//基础汇率
    			 cashpecpayIn.setPortCuryRate(dPortRateIn);//组合汇率
    			 bacecurymoney = this.getSettingOper().calBaseMoney(moneyIn,dBaseRateIn);
    			 portcurymoney = this.getSettingOper().calPortMoney(
    					                      moneyIn,
    					                      dBaseRateIn,
    					                      dPortRateIn,
    					                      rs.getString("FBCuryCode"),
    					                      dDate,
    					                      sPortCode); 
    			 cashpecpayIn.setBaseCuryMoney(bacecurymoney);
    			 cashpecpayIn.setPortCuryMoney(portcurymoney);

    			 cashpayrecadminIn.addList(cashpecpayIn);
    			 cashpayrecadminIn.insert(dDate, "02", "02OT", "", 1, false, "", "Forward_RollOver", false);
                 
                 //实付
    			 cashpecpayOut = new CashPecPayBean();
    			 cashpecpayOut.setTradeDate(this.dDate);//业务日期
    			 cashpecpayOut.setTsfTypeCode("03");//业务类型
    			 cashpecpayOut.setSubTsfTypeCode("03OT");//业务子类型
    			 cashpecpayOut.setNum(rs.getString("FNum"));
    			 cashpecpayOut.setRelaNum(rs.getString("FNum"));//关联编号
    			 cashpecpayOut.setRelaNumType("Forward_RollOver");//关联编号类型
    			 moneyOut = rs.getDouble("FSCapMoney");//金额
    			 cashpecpayOut.setMoney(moneyOut);
    			 cashpecpayOut.setDataSource(1);//来源标志
    			 cashpecpayOut.checkStateId = 1;
    			 cashpecpayOut.setPortCode(rs.getString("FPortCode"));//组合代码  			 
    			 cashpecpayOut.setCashAccCode(rs.getString("FSCashAccCode"));//现金账户
    			 cashpecpayOut.setCuryCode(rs.getString("FSCuryCode"));//币种代码
    			 cashpecpayOut.setInOutType(-1);//方向
    			 cashpecpayOut.setBaseCuryRate(dBaseRateOut);//基础汇率
    			 cashpecpayOut.setPortCuryRate(dPortRateOut);//组合汇率
    			 bacecurymoney = this.getSettingOper().calBaseMoney(moneyOut,dBaseRateOut);
    			 portcurymoney = this.getSettingOper().calPortMoney(
    					                         moneyOut,
    					                         dBaseRateOut,
    					                         dPortRateOut,
    					                         rs.getString("FSCuryCode"),
    					                         dDate,
    					                         sPortCode); 
    			 cashpecpayOut.setBaseCuryMoney(bacecurymoney);
    			 cashpecpayOut.setPortCuryMoney(portcurymoney);
    			 
    			 cashpayrecadminOut.addList(cashpecpayOut);
    			 cashpayrecadminOut.insert(dDate, "03", "03OT", "", 1, false, "", "Forward_RollOver", false);
    		 }	
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
    		 
    	 }catch (Exception ex) {
             throw new YssException("生成现金应收应付出现异常！", ex);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }    	
    } 
    
    public double getCuryRate(String cashacc, java.util.Date tradeDate, int CuryRateType, String portCode) throws YssException {
		String strSql = "";
		double reCuryRate = 0.0;
		String Cury = "";
		ResultSet rs = null;
		EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
		rateOper.setYssPub(pub);
		try {
			strSql = "select FCuryCode from " + pub.yssGetTableName("tb_Para_CashAccount") +
					 " where FCashAccCode = " + dbl.sqlString(cashacc) + " and FCheckState = 1";
			rs = dbl.queryByPreparedStatement(strSql);
			if (rs.next()) {
				Cury = rs.getString("FCuryCode");
			}
			if (CuryRateType == 0) {
				reCuryRate = this.getSettingOper().getCuryRate(tradeDate, Cury, "", YssOperCons.YSS_RATE_BASE); //基础汇率
			} else if (CuryRateType == 1) {
				rateOper.getInnerPortRate(tradeDate, Cury, portCode);
				reCuryRate = rateOper.getDPortRate(); //组合汇率
			}
			return reCuryRate;
		} catch (Exception ex) {
			throw new YssException("获取数据出错");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    
    private String getCuryTradeType(String sNum) throws YssException {
		String flag = "0";// 返回标识
		// 使用远期交易数据的编号查询该证券对应的交易货币
		ResultSet tradeRs = null;
		ResultSet tradeTypeRs = null;
		String tradeCury = "";
		String tradeSql = "select b.ftradecury,a.* from "// 编号查询交易货币类型
				+ pub.yssGetTableName("Tb_Data_ForwardTrade")
				+ " a left join  (select fsecuritycode,ftradecury from "
				+ pub.yssGetTableName("tb_para_security")
				+ ") b on a.fsecuritycode=b.fsecuritycode where fnum="
				+ dbl.sqlString(sNum);
		String tradeTypeSql = "select b.FSaleCury,b.fbuycury,a.* from "// 编号查询买入卖出货币类型
				+ pub.yssGetTableName("Tb_Data_ForwardTrade")
				+ " a left join  (select fsecuritycode,FBuyCury,FSaleCury from "
				+ pub.yssGetTableName("Tb_Para_Forward")
				+ " ) b on a.fsecuritycode=b.fsecuritycode where fnum="
				+ dbl.sqlString(sNum);
		try {
			tradeRs = dbl.queryByPreparedStatement(tradeSql);
			if (tradeRs.next()) {
				tradeCury = tradeRs.getString("ftradecury");// 获取交易货币
			}
			tradeTypeRs = dbl.queryByPreparedStatement(tradeTypeSql);
			if (tradeTypeRs.next()) {
				if (tradeCury.equals(tradeTypeRs.getString("fbuycury"))) {
					flag = "0";
				} else if (tradeCury.equals(tradeTypeRs.getString("FSaleCury"))) {
					flag = "1";
				}
			}	
		} catch (Exception e) {
			throw new YssException("远期外汇交易查询交易货币或匹配买入卖出货币出错!", e);
		} finally {
			dbl.closeResultSetFinal(tradeRs);
			dbl.closeResultSetFinal(tradeTypeRs);
		}
		return flag;
    }
    
    /**shashijie 2011.04.11 产生资金调拨与现金应收应付数据*/
    private void createCashAndTrans(ResultSet rs) throws YssException ,SQLException {
    	createFundsPaying(rs);//资金调拨
    	createCashShould(rs,"01");//现金应收应付		01指业务日业务处理
	}

	/**
     * modify by fangjiang 2011.07.18 STORY #1353 
     */
    /**shashijie 2011.04.11 现金应收应付*/
    private void createCashShould(ResultSet rs, String  boole ) throws YssException, SQLException{
    	double tradeAmount = rs.getDouble("FTradeAmount");
		double matureMoney = rs.getDouble("FMatureMoney");
		double tradeAmount1 = rs.getDouble("FTradeAmount1");
		double matureMoney1 = rs.getDouble("FMatureMoney1");
		double offIncome = 0.0; //平仓收益		
		double scale;
		
		String TsfTypeCode = "";//现金应收应付业务类型
		String SubTsfTypeCode = "";//业务子类型
		
		double baseRate;
		double portRate;
	
		//判断交易证券货币对应卖出货币还是买入货币
		if (getCuryTradeType(rs.getString("FNum")).equals("0")){
			scale = YssD.div(matureMoney, matureMoney1);
			offIncome = YssD.sub(YssD.mul(tradeAmount1, scale), tradeAmount);	
		} else {
			scale = YssD.div(tradeAmount, tradeAmount1);
			offIncome = YssD.sub(YssD.mul(matureMoney1, scale), matureMoney);		
		} 	
		if(rs.getString("FOffCury").trim().length() != 0 && !rs.getString("FOffCury").equalsIgnoreCase(rs.getString("FSellCuryCode"))){
			
			baseRate = this.getSettingOper().getCuryRate(
					  this.dDate, 
					  rs.getString("FSellCuryCode"), 
					  this.sPortCode, 
					  YssOperCons.YSS_RATE_BASE);
		    portRate = this.getSettingOper().getRate(
		 		      this.dDate, 
		 		      "",
				      rs.getString("FOffCury"), 
				      this.sPortCode);
		    offIncome =  YssD.round
		    			 (
	                     	YssD.div
	                     	(
	                     		YssD.mul
	                     		(
	                     			offIncome, 
	                     			baseRate
	                     		), 
	                     		portRate
                     		),
                            2
                        );
		}
		   
		if(offIncome > 0){//应收
			if (boole.equals("01")) {
				TsfTypeCode = "06";
				SubTsfTypeCode = "06FWCP";
			} else if (boole.equals("02")) {
				TsfTypeCode = "02";			//收入
				SubTsfTypeCode = "02FWCP";
			}
			shouldIncome(rs,offIncome,TsfTypeCode,SubTsfTypeCode);
		} else if(offIncome < 0){//应付
			if (boole.equals("01")) {
				TsfTypeCode = "07";
				SubTsfTypeCode = "07FWCP";
			} else if (boole.equals("02")) {
				TsfTypeCode = "03";			//费用
				SubTsfTypeCode = "03FWCP";
			}
			shouldPaying(rs,offIncome,TsfTypeCode,SubTsfTypeCode);
		}	
    }

    /**现金应付数据*/
    private void shouldPaying(ResultSet rs,double offIncome,String TsfTypeCode,String  SubTsfTypeCode) throws YssException , SQLException {
    	CashPecPayBean cashpecpayOut = new CashPecPayBean();
	   	CashPayRecAdmin cashpayrecadminOut = new CashPayRecAdmin(); 
	   	cashpayrecadminOut.setYssPub(pub);
    	
	   	offIncome = offIncome * -1;//应收应付都为正数
	   	setCashPecPayBean(cashpecpayOut,TsfTypeCode,SubTsfTypeCode,rs,offIncome);
		
		cashpayrecadminOut.addList(cashpecpayOut);
		cashpayrecadminOut.insert(dDate, TsfTypeCode, SubTsfTypeCode, "", 1, false, "", "Forward_RollOver", false);
		
		
		this.bstate = false;
	}

    /**现金应收应付赋值
     * @param CashPecPayBean 现金应收应付实体对象
     * @param TsfTypeCode 业务类型
     * @param SubTsfTypeCode 业务子类型
     * @param ResultSet 平仓sql查询对象
     * @param offIncome 原币金额
     * */
	private void setCashPecPayBean(CashPecPayBean cashpecpay, String TsfTypeCode,
			String SubTsfTypeCode, ResultSet rs , double offIncome) throws YssException , SQLException {
		if (cashpecpay==null) {
			cashpecpay = new CashPecPayBean();
		}
		
		double bacecurymoney = 0.0;//基础货币金额
	    double portcurymoney = 0.0;//组合货币金额
	    
	   	//基础汇率
	   	double dBaseRateIn = this.getSettingOper().getCuryRate(this.dDate, rs.getString("FOffCury").trim().length() != 0 ? rs.getString("FOffCury") : rs.getString("FSellCuryCode"), this.sPortCode, YssOperCons.YSS_RATE_BASE);
	   	//组合汇率
	    double dPortRateIn = this.getSettingOper().getCuryRate(this.dDate, rs.getString("FOffCury").trim().length() != 0 ? rs.getString("FOffCury") : rs.getString("FSellCuryCode"), this.sPortCode, YssOperCons.YSS_RATE_PORT);
	    	
		cashpecpay.setTradeDate(this.dDate);//业务日期
		cashpecpay.setTsfTypeCode(TsfTypeCode);//业务类型
		cashpecpay.setSubTsfTypeCode(SubTsfTypeCode);//业务子类型
		cashpecpay.setNum(rs.getString("FNum"));
		cashpecpay.setRelaNum(rs.getString("FNum"));//关联编号
		cashpecpay.setRelaNumType("Forward_RollOver");//关联编号类型
		
		cashpecpay.setMoney(offIncome);//原币金额
		cashpecpay.setDataSource(1);//来源标志
		cashpecpay.checkStateId = 1;
		cashpecpay.setPortCode(rs.getString("FPortCode"));//组合代码  			 
		cashpecpay.setCashAccCode(rs.getString("FSCashAccCode"));//现金账户,平仓卖出账户
		String FBCuryCode = getFBCuryCode(rs.getString("FSCashAccCode"));
		cashpecpay.setCuryCode(FBCuryCode);//币种代码
		cashpecpay.setInOutType(1);//方向
		cashpecpay.setBaseCuryRate(dBaseRateIn);//基础汇率
		cashpecpay.setPortCuryRate(dPortRateIn);//组合汇率
		bacecurymoney = this.getSettingOper().calBaseMoney(offIncome,dBaseRateIn);
		portcurymoney = this.getSettingOper().calPortMoney(offIncome,dBaseRateIn,dPortRateIn,FBCuryCode,dDate,sPortCode); 
		cashpecpay.setBaseCuryMoney(bacecurymoney);//基础货币金额
		cashpecpay.setPortCuryMoney(portcurymoney);//组合货币金额
	}

	/**现金应收数据*/
	private void shouldIncome(ResultSet rs,double offIncome,String TsfTypeCode,String  SubTsfTypeCode) throws YssException , SQLException {
		CashPecPayBean cashpecpayIn = new CashPecPayBean();
	   	CashPayRecAdmin cashpayrecadminIn = new CashPayRecAdmin(); 
	   	cashpayrecadminIn.setYssPub(pub);
	   	
	   	setCashPecPayBean(cashpecpayIn,TsfTypeCode, SubTsfTypeCode,rs,offIncome);
	   	
		cashpayrecadminIn.addList(cashpecpayIn);
		cashpayrecadminIn.insert(dDate, TsfTypeCode, SubTsfTypeCode, "", 1, false, "", "Forward_RollOver", false);
		
		this.bstate = false;
	}

	/**根据现金账户获取币种
	 * shashijie 2011.04.11 */
	public String getFBCuryCode(String CashAccCode) {
		ResultSet rs = null;
		String FBCuryCode = "";
		try {
			rs = dbl.queryByPreparedStatement("SELECT FCuryCode FROM "+ pub.yssGetTableName("Tb_Para_CashAccount") +" WHERE FCashAccCode = "
					+ dbl.sqlString(CashAccCode) + " AND FCheckState = 1 ");
			if (rs.next()) {
				FBCuryCode = rs.getString("FCuryCode");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
		finally{
			dbl.closeResultSetFinal(rs);
		}
		//---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
		return FBCuryCode;
	}

	/**
     * modify by fangjiang 2011.07.18 STORY #1353 
     */
	/**shashijie 2011.04.11   产生资金调拨*/
	private void createFundsPaying(ResultSet rs) throws YssException ,SQLException{
		TransferSetBean transfersetIn = new TransferSetBean();
		TransferSetBean transfersetOut = new TransferSetBean();
		ArrayList tranSetList = new ArrayList();
		
		TransferBean tran = new TransferBean();
		tran.setYssPub(pub);
		tran.setDtTransDate(this.dDate); 
		tran.setDtTransferDate(rs.getDate("FTranDate"));//调拨日期为交收日	shashijie 2011.04.11
		tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount); //"01"
		tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Forward); //"00005"
		tran.setDataSource(1); 
		tran.setFRelaNum(rs.getString("FNum")); //关联编号
		tran.setFNumType("Forward_Off"); 
		tran.checkStateId = 1;
		tran.creatorTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
		tran.setDataSource(1); 
		
		double tradeAmount = rs.getDouble("FTradeAmount");
		double matureMoney = rs.getDouble("FMatureMoney");
		double tradeAmount1 = rs.getDouble("FTradeAmount1");
		double matureMoney1 = rs.getDouble("FMatureMoney1");
		double offIncome = 0.0; //平仓收益		
		double scale;
		double baseRate;
		double portRate;
		
		CashAccountBean caBean = null;//声明现金账户的bean
		String cashAccCode = "";
		
		if (getCuryTradeType(rs.getString("FNum")).equals("0")){
			scale = YssD.div(matureMoney, matureMoney1);
			offIncome = YssD.sub(YssD.mul(tradeAmount1, scale), tradeAmount);	
		} else {
			scale = YssD.div(tradeAmount, tradeAmount1);
			offIncome = YssD.sub(YssD.mul(matureMoney1, scale), matureMoney);		
		} 
		
		if(rs.getString("FOffCury").trim().length() == 0 || rs.getString("FOffCury").equalsIgnoreCase(rs.getString("FSellCuryCode"))){
			if(offIncome > 0){
				transfersetIn.setDMoney(offIncome);
				transfersetIn.setSPortCode(rs.getString("FPortCode")); // 组合代码
				transfersetIn.setSCashAccCode(rs.getString("FSCashAccCode")); // 现金帐户代码
				transfersetIn.setDBaseRate(getCuryRate(rs.getString("FSCashAccCode"), 
										   this.dDate, 
										   0, 
										   rs.getString("FPortCode")));
				transfersetIn.setDPortRate(getCuryRate(rs.getString("FSCashAccCode"), 
						                   this.dDate, 
										   1, 
										   rs.getString("FPortCode")));
				transfersetIn.checkStateId = 1;
				transfersetIn.setIInOut(1);
				tranSetList.add(transfersetIn);
			} else if(offIncome < 0){
				transfersetOut.setDMoney(YssD.mul(-1, offIncome));
				transfersetOut.setSPortCode(rs.getString("FPortCode")); // 组合代码
				transfersetOut.setSCashAccCode(rs.getString("FSCashAccCode")); // 现金帐户代码
				transfersetOut.setDBaseRate(getCuryRate(rs.getString("FSCashAccCode"), 
										   this.dDate, 
										   0, 
										   rs.getString("FPortCode")));
				transfersetOut.setDPortRate(getCuryRate(rs.getString("FSCashAccCode"), 
						                   this.dDate, 
										   1, 
										   rs.getString("FPortCode")));
				transfersetOut.checkStateId = 1;
				transfersetOut.setIInOut(-1);
				tranSetList.add(transfersetOut);
			}
		}else{
			if(rs.getString("FOffCury").equalsIgnoreCase(rs.getString("FBuyCuryCode"))){
				cashAccCode = rs.getString("FBCashAccCode"); // 现金帐户代码
			}else{
				BaseCashAccLinkDeal cashacc = (BaseCashAccLinkDeal) pub.getOperDealCtx().getBean("cashacclinkdeal");
	            cashacc.setYssPub(pub);
	            cashacc.setLinkParaAttr("", this.sPortCode, "", "", "", this.dDate, rs.getString("FOffCury"), ""); //币种
	            caBean = cashacc.getCashAccountBean();
	            if (caBean != null) {
	            	cashAccCode = caBean.getStrCashAcctCode();; // 现金帐户代码
	            } else {
	                throw new YssException("请设置现金账户链接");
	            }
			}
			baseRate = this.getSettingOper().getCuryRate(
					  this.dDate, 
					  rs.getString("FSellCuryCode"), 
					  this.sPortCode, 
					  YssOperCons.YSS_RATE_BASE);
		    portRate = this.getSettingOper().getRate(
		 		      this.dDate, 
		 		      "",
				      rs.getString("FOffCury"), 
				      this.sPortCode);

		    offIncome =  YssD.round
		    			 (
	                     	YssD.div
	                     	(
	                     		YssD.mul
	                     		(
	                     			offIncome, 
	                     			baseRate
	                     		), 
	                     		portRate
                     		),
                            2
                        );
		    if(offIncome > 0){
				transfersetIn.setDMoney(offIncome);
				transfersetIn.setSPortCode(rs.getString("FPortCode")); // 组合代码
				transfersetIn.setSCashAccCode(cashAccCode); // 现金帐户代码
				transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(this.dDate, rs.getString("FOffCury"), this.sPortCode, YssOperCons.YSS_RATE_BASE));
				transfersetIn.setDPortRate(this.getSettingOper().getCuryRate(this.dDate, rs.getString("FOffCury"), this.sPortCode, YssOperCons.YSS_RATE_PORT));
				transfersetIn.checkStateId = 1;
				transfersetIn.setIInOut(1);
				tranSetList.add(transfersetIn);
			} else if(offIncome < 0){
				transfersetOut.setDMoney(YssD.mul(-1, offIncome));
				transfersetOut.setSPortCode(rs.getString("FPortCode")); // 组合代码
				transfersetOut.setSCashAccCode(cashAccCode); // 现金帐户代码
				transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(this.dDate, rs.getString("FOffCury"), this.sPortCode, YssOperCons.YSS_RATE_BASE));
				transfersetOut.setDPortRate(this.getSettingOper().getCuryRate(this.dDate, rs.getString("FOffCury"), this.sPortCode, YssOperCons.YSS_RATE_PORT));
				transfersetOut.checkStateId = 1;
				transfersetOut.setIInOut(-1);
				tranSetList.add(transfersetOut);
			}
		}
		
		CashTransAdmin tranAdmin = new CashTransAdmin();
		tranAdmin.setYssPub(pub);

		tran.setSubTrans(tranSetList);
		tranAdmin.addList(tran);   
		//这里第一个参数得传调拨日期,要不然系统就不删除原先的数据了
		tranAdmin.insert(rs.getDate("FTranDate"), "Forward_Off", 1, rs.getString("FNum"));	
		
		this.bstate = false;
	}
	
	private void createJECashTrans(ResultSet rs) throws YssException, SQLException { 

		TransferSetBean transferset = new TransferSetBean();
		ArrayList tranSetList = new ArrayList();
		
		TransferBean tran = new TransferBean();
		tran.setYssPub(pub);
		tran.setDtTransDate(rs.getDate("FJESettleDate")); 
		tran.setDtTransferDate(rs.getDate("FJESettleDate"));
		tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount); //"01"
		tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Forward); //"00005"
		tran.setDataSource(1); 
		tran.setFRelaNum(rs.getString("FJENum")); //关联编号
		tran.setFNumType("JEForward"); 
		tran.checkStateId = 1;
		tran.creatorTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
		
		transferset.setDMoney(rs.getDouble("FJEMoney"));
		transferset.setSPortCode(rs.getString("FPortCode")); // 组合代码
		transferset.setSCashAccCode(rs.getString("FJECashAccCode")); // 现金帐户代码
		transferset.setDBaseRate(getCuryRate(rs.getString("FJECashAccCode"), 
				                   this.dDate, 
								   0, 
								   rs.getString("FPortCode")));
		transferset.setDPortRate(getCuryRate(rs.getString("FJECashAccCode"), 
				                   this.dDate, 
								   1, 
								   rs.getString("FPortCode")));
		transferset.checkStateId = 1;
		if("1".equalsIgnoreCase(rs.getString("FJEInOut"))){
			transferset.setIInOut(1);
		}else{
			transferset.setIInOut(-1);
		}		
		tranSetList.add(transferset);
		
		CashTransAdmin tranAdmin = new CashTransAdmin();
		tranAdmin.setYssPub(pub);

		tran.setSubTrans(tranSetList);
		tranAdmin.addList(tran);   
			
		tranAdmin.insert(this.dDate, "JEForward", 1, rs.getString("FJENum"));	
		
		this.bstate = false;
    }
}
