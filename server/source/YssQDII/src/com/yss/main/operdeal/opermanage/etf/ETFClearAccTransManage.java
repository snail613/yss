package com.yss.main.operdeal.opermanage.etf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.sql.*;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.commeach.EachRateOper;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.manager.CashTransAdmin;
import com.yss.util.*;

/**
 * 备付金结转
 * 现金替代、现金差额、应付替代款结转至备付金账户
 * @author Administrator
 *
 */
public class ETFClearAccTransManage extends BaseOperManage {

	private ETFParamSetBean etfParamBean = null;
	
	public void doOpertion() throws YssException {

		if(	!etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_ONE)
				&& !etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)
				&& !etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)){
			//目前只有易方达H股、华夏恒指ETF考虑备付金结转
			return;
		}
		
		if(null == etfParamBean.getClearAccCode() || 
				etfParamBean.getClearAccCode().trim().length() == 0){
			//如果没有设置备付金帐户，就不用处理了
			return;
		}
		
		EachExchangeHolidays holiday = null;//节假日代码类
		String sRowStr ="";
		Date yesDate = null;//操作日当天的前一个工作日
		holiday = new EachExchangeHolidays();//实例化
		holiday.setYssPub(pub);//设置pub
		
		//拼接参数：节假日代码+当天的偏离天数+操作日期
		String holidayCode = "";
		if(etfParamBean.getSHolidayCode().trim().length() > 0){
			holidayCode = etfParamBean.getSHolidayCode();
		}else{
			holidayCode = (String)etfParamBean.getHoildaysRela().get("sgreplaceover");
		}
		sRowStr = holidayCode + "\t" + 0 + "\t" + YssFun.formatDate(dDate);
		holiday.parseRowStr(sRowStr);//解析参数
		if(YssFun.dateDiff(dDate,YssFun.toSqlDate(holiday.getOperValue("getWorkDate"))) != 0){
			//如果当天为节假日，不处理
			return;
		}
		
		//拼接参数：节假日代码+当天的偏离天数+操作日期
		sRowStr = holidayCode + "\t" + -1 + "\t" + YssFun.formatDate(dDate);
		
		holiday.parseRowStr(sRowStr);//解析参数
		yesDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取操作日期当天的最近一个工作日
				
		if(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE.equals(etfParamBean.getSupplyMode())){
			//华夏恒指ETF备付金结转
			/*跨境ETF备付金结转规则
				1.当日的收款，当日不结转，下一日可用
				2.当日的付款，查看前一日余额：
				若前一日余额>付款金额， 从ETF备付金往银行存款结转（前一日余额-付款金额）；
				若前一日余额<付款金额，从银行存款往ETF备付金结转（付款金额-前一日余额）
				其中：各款项收付款分别处理，收款与付款不钆差
			*/
			doSjsbfjTrans();
			return;

		} else if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(etfParamBean.getSupplyMode())
				|| YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(etfParamBean.getSupplyMode())){
			
			doOldTrans(yesDate);
			return;
		}

	}
	
	/**
	 *	恒指ETF备付金结转（深交所）
	 */
	private void doSjsbfjTrans() throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
		double dZrye = 0;
		double dYfk = 0;
		double dJzje = 0;
		double dBaseRate = 1;
		double dPortRate = 1;
		double dXjce = 0;//现金差额

		try{
			buff = new StringBuffer(500);
			
			//获取备付金帐户昨日余额
			buff.append("select c.fcashacccode,c.fportcode,c.fstoragedate,c.fcurycode,c.faccbalance")
				.append(" from ").append(pub.yssGetTableName("tb_stock_cash"))
				.append(" c where c.fcashacccode = ").append(dbl.sqlString(etfParamBean.getClearAccCode()))
				.append(" and ").append(operSql.sqlStoragEve(dDate))
				.append(" and c.fportcode = ").append(dbl.sqlString(sPortCode));
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0, buff.length());
			
			while(rs.next()){
				dZrye = rs.getDouble("faccbalance");
			}
			
			dbl.closeResultSetFinal(rs);
			
			//统计当日付款金额
			buff.append("select a.*,b.FCuryCode,c.FPortCury from (")
				.append("select FCashAccCode, FPortCode, FInOut, FMoney,FBaseCuryRate,FPortCuryRate,")
                .append("FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransDate,fattrclscode")
                .append(" from (select FCashAccCode,FPortCode,FInOut,FMoney,FTsfTypeCode,FSubTsfTypeCode,")
                .append(" FBaseCuryRate,FPortCuryRate,FTransferDate,FTransDate")
                .append(",' ' as fattrclscode from ")
                .append(pub.yssGetTableName("Tb_Cash_SubTransfer"))
                .append(" a61 join (select FNum,FTsfTypeCode,FSubTsfTypeCode,FTransferDate,FTransDate from ")
                .append(pub.yssGetTableName("Tb_Cash_Transfer"))
                .append(" where FCheckState = 1 and FTransferDate = ").append(dbl.sqlDate(dDate))
                //将备付金结转资金调拨本身排除，避免重复处理将备付金结转资金调拨统计进去导致出错
                .append(" and FSubTsfTypeCode not in ('0007ETF')")
                .append(") a62 on a61.FNum = a62.FNum where FPortCode = ").append(dbl.sqlString(sPortCode))
                .append(" and FCheckState = 1")
                .append(" and a61.FCashAccCode = ").append(dbl.sqlString(etfParamBean.getClearAccCode()))
                .append(") t ) a left join (select FCashAccCode,FCuryCode from ")
                .append(pub.yssGetTableName("tb_para_cashaccount"))
                .append(" where FCheckState = 1 ) b on a.FCashAccCode = b.FCashAccCode ")
                .append(" left join (select FPortCode, FPortName, FPortCury from ")
                .append(pub.yssGetTableName("Tb_Para_Portfolio"))
                .append(" where  FCheckState = 1 ) c on a.FPortCode = c.FPortCode ");
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0, buff.length());
			
			while(rs.next()){
				dBaseRate = rs.getDouble("FBaseCuryRate");
				dPortRate = rs.getDouble("FPortCuryRate");
				
				double dInOut = rs.getDouble("FInOut");
				double dMoney = rs.getDouble("FMoney");
				
				
				if(dInOut > 0 && dMoney < 0 ){
					//资金调拨方向为流入（1），金额为负，即为付款
					dYfk += Math.abs(dMoney);
				}
				
				if(dInOut < 0 && dMoney > 0 ){
					//资金调拨方向为流出（-1），金额为正，即为付款
					dYfk += Math.abs(dMoney);
				}
			}
				
			//结转金额 = 昨日账户余额 - 当日付款金额
			dJzje = YssD.sub(dZrye, dYfk);
			
			//以下生成备付金结转资金调拨
	        CashTransAdmin tranAdmin = new CashTransAdmin();
	        tranAdmin.setYssPub(pub);
	        
	        TransferBean tran = new TransferBean();
	        TransferSetBean transfersetIn = new TransferSetBean();
	        TransferSetBean transfersetOut = new TransferSetBean();
	        ArrayList tranSetList = new ArrayList();
	        
	        //增加资金调拨记录
	        tran.setYssPub(pub);
	        tran.setDtTransDate(dDate); 
	        tran.setDtTransferDate(dDate);
	        tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
	        tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_ClearTrans);
	        tran.setFNumType("ETFClearAccTransCurr");
	        tran.setStrTransferTime("00:00:00");
	        tran.setDataSource(1);  
	        tran.checkStateId = 1;
	        tran.creatorTime = YssFun.formatDate(new java.util.Date(),"yyyyMMdd HH:mm:ss");
	        //资金流入帐户
	        transfersetIn.setDMoney(Math.abs(dJzje));
	        transfersetIn.setDBaseRate(dBaseRate);
	        transfersetIn.setDPortRate(dPortRate);
	        transfersetIn.setSPortCode(this.sPortCode);
	        transfersetIn.setSAnalysisCode1("");
	        transfersetIn.setSAnalysisCode2("");
	        transfersetIn.setSAnalysisCode3("");
	        transfersetIn.setSCashAccCode(dJzje > 0 ? etfParamBean.getCashAccCode() : 
	        												etfParamBean.getClearAccCode());
	        transfersetIn.setIInOut(1);
	        transfersetIn.checkStateId = 1;
	        //资金流出账户
	        transfersetOut.setDMoney(Math.abs(dJzje));
	        transfersetOut.setDBaseRate(dBaseRate);
	        transfersetOut.setDPortRate(dPortRate);
	        transfersetOut.setSPortCode(this.sPortCode);
	        transfersetOut.setSAnalysisCode1("");
	        transfersetOut.setSAnalysisCode2("");
	        transfersetOut.setSAnalysisCode3("");
	        transfersetOut.setSCashAccCode(dJzje > 0 ? etfParamBean.getClearAccCode() : 
															etfParamBean.getCashAccCode());
	        transfersetOut.setIInOut(-1);
	        transfersetOut.checkStateId = 1;
	        
	        tranSetList.add(transfersetIn);
	        tranSetList.add(transfersetOut);
            tranAdmin.addList(tran, tranSetList);

			tranAdmin.insert("",dDate,dDate,
					YssOperCons.YSS_ZJDBLX_InnerAccount,YssOperCons.YSS_ZJDBZLX_ETF_ClearTrans,
					"","","","","","ETFClearAccTransCurr" + "," + "ETFClearAccTrans",
						"",1,"",sPortCode,0,"","","",true,"");	
			
		}catch(Exception e){
			throw new YssException("ETF备付金结转出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}


	/**
	 * 备付金结转原则：
	 * 1、如果当日结算完成后ETF备付金为负值（小于0），则当日即从存款划转金额到ETF备付金；
	   2、如果当日结算完成后ETF备付金为正值（大于0），则明日从ETF备付金划转到存款。
	 */
	private void doOldTrans(Date yesDate) throws YssException {
        BaseStgStatDeal cashstgstat = null;
        
		doClearAccTrans(etfParamBean,yesDate);//若昨日ETF备付金为正值（大于0），结转昨日备付金
		
		//此处将业务日期为当天的备付金资金调拨删除
		//避免重新生成导致备付金结转不正确
        CashTransAdmin tranAdmin = new CashTransAdmin();
        tranAdmin.setYssPub(pub);
        tranAdmin.delete("",dDate,dDate,
				YssOperCons.YSS_ZJDBLX_InnerAccount,YssOperCons.YSS_ZJDBZLX_ETF_ClearTrans,
				"","","","","","ETFClearAccTransCurr","",1,"",sPortCode,0,"","","","");	
        
		//结转昨日备付金后统计现金库存
        cashstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("CashStorage");
        cashstgstat.setYssPub(pub);
        cashstgstat.stroageStat(dDate, dDate, operSql.sqlCodes(sPortCode));
        
		doClearAccTrans(etfParamBean,dDate);//当日ETF备付金为负值（小于0），结转当日备付金
	}

	
	private void doClearAccTrans(ETFParamSetBean etfParamBean, Date date) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
        TransferBean tran = new TransferBean();
        TransferSetBean transfersetIn = new TransferSetBean();
        TransferSetBean transfersetOut = new TransferSetBean();
        ArrayList tranSetList = new ArrayList();

        CashTransAdmin tranAdmin = new CashTransAdmin();
        tranAdmin.setYssPub(pub);
        double dMoney = 0;
		try{
			buff = new StringBuffer();
			buff.append("select a.*,b.FAccBalance,b.FBaseCuryBal,b.FPortCuryBal,b.FCuryCode as FClearCuryCode,b.FStorageDate,d.fcurycode from (");
			buff.append("select * from ").append(pub.yssGetTableName("tb_etf_param")).append(" where fportcode = ").append(dbl.sqlString(sPortCode));
			buff.append(" ) a left join (");
			buff.append("select * from ").append(pub.yssGetTableName("tb_stock_cash")).append(" where fstoragedate = ").append(dbl.sqlDate(date));
			buff.append(") b on a.fclearacccode =  b.fcashacccode and a.fportcode = b.fportcode");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_para_cashaccount"));
			buff.append(" where fcheckstate = 1) d on d.fportcode = a.fportcode and a.fcashacccode = d.fcashacccode");
			
			rs = dbl.queryByPreparedStatement(buff.toString());
			while(rs.next()){
				if((YssFun.dateDiff(dDate, date) == 0 && rs.getDouble("FAccBalance") > 0) || rs.getDouble("FAccBalance") == 0){
					continue;
				}

		        //增加资金调拨记录
		        tran.setYssPub(pub);
		        tran.setDtTransDate(dDate); 
		        tran.setDtTransferDate(dDate);
		        tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
		        tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_ClearTrans);
		        if(YssFun.dateDiff(dDate, date) == 0){
		        	tran.setFNumType("ETFClearAccTransCurr");//当日ETF备付金为负值（小于0），结转当日备付金
		        }else{
		        	tran.setFNumType("ETFClearAccTrans");
		        }
		        tran.setStrTransferTime("00:00:00");
		        tran.setDataSource(1);  
		        tran.checkStateId = 1;
		        tran.creatorTime = YssFun.formatDate(new java.util.Date(),"yyyyMMdd HH:mm:ss");
		        dMoney = rs.getDouble("FAccBalance");
		        if(dMoney < 0){
		        	dMoney = -dMoney;
		        }
		        //资金流入帐户
		        transfersetIn.setDMoney(dMoney);
		        transfersetIn.setDBaseRate(YssD.div(rs.getDouble("FBaseCuryBal"), rs.getDouble("FAccBalance")));
		        transfersetIn.setDPortRate(YssD.div(rs.getDouble("FBaseCuryBal"), rs.getDouble("FPortCuryBal")));
		        transfersetIn.setSPortCode(this.sPortCode);
		        transfersetIn.setSAnalysisCode1("");
		        transfersetIn.setSAnalysisCode2("");
		        transfersetIn.setSAnalysisCode3("");
		        transfersetIn.setSCashAccCode(rs.getDouble("FAccBalance") > 0 ? rs.getString("fcashacccode") : rs.getString("fclearacccode"));
		        transfersetIn.setIInOut(1);
		        transfersetIn.checkStateId = 1;
		        //资金流出账户
		        transfersetOut.setDMoney(dMoney);
		        transfersetOut.setDBaseRate(YssD.div(rs.getDouble("FBaseCuryBal"), rs.getDouble("FAccBalance")));
		        transfersetOut.setDPortRate(YssD.div(rs.getDouble("FBaseCuryBal"), rs.getDouble("FPortCuryBal")));
		        transfersetOut.setSPortCode(this.sPortCode);
		        transfersetOut.setSAnalysisCode1("");
		        transfersetOut.setSAnalysisCode2("");
		        transfersetOut.setSAnalysisCode3("");
		        transfersetOut.setSCashAccCode(rs.getDouble("FAccBalance") > 0 ? rs.getString("fclearacccode") : rs.getString("fcashacccode"));
		        transfersetOut.setIInOut(-1);
		        transfersetOut.checkStateId = 1;
		        
		        tranSetList.add(transfersetIn);
		        tranSetList.add(transfersetOut);
	            tranAdmin.addList(tran, tranSetList);
	            
	            if(YssFun.dateDiff(dDate, date) == 0){
	            	//根据编号类型、业务类型等删除原有调拨数据 (当日ETF备付金为负值（小于0），从存款账户流入资金到备付金账户)
					tranAdmin.insert("",dDate,dDate,
							YssOperCons.YSS_ZJDBLX_InnerAccount,YssOperCons.YSS_ZJDBZLX_ETF_ClearTrans,
							"","","","","","ETFClearAccTransCurr","",1,"",sPortCode,0,"","","",true,"");	
	            }else{
	            	//根据编号类型、业务类型等删除原有调拨数据 (结转前一工作日的备付金库存金额到存款账户)
					tranAdmin.insert("",dDate,dDate,
							YssOperCons.YSS_ZJDBLX_InnerAccount,YssOperCons.YSS_ZJDBZLX_ETF_ClearTrans,
							"","","","","","ETFClearAccTrans","",1,"",sPortCode,0,"","","",true,"");	//根据编号类型、业务类型等删除原有调拨数据
	            }
			}
		}catch(Exception e){
			throw new YssException("ETF备付金结转出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}

	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.sPortCode = portCode;
		this.dDate = dDate;
		
		ETFParamSetAdmin etfParamAdmin = new ETFParamSetAdmin();
		etfParamAdmin.setYssPub(pub);
		etfParamBean = new ETFParamSetBean();
		HashMap hm = new HashMap();
		hm = etfParamAdmin.getETFParamInfo(sPortCode);
		etfParamBean = (ETFParamSetBean)hm.get(sPortCode);
		if(etfParamBean == null){
			throw new YssException("组合【" + sPortCode + "】对应的ETF参数设置不存在或未审核！");
		}
	}

}
