/**
 * 
 */
package com.yss.main.operdeal.income.stat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.commeach.EachRateOper;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.parasetting.MTVMethodBean;
import com.yss.main.parasetting.PeriodBean;
import com.yss.manager.SecRecPayAdmin;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssOperCons;

/**
 * @包名：com.yss.main.operdeal.income.stat
 * @文件名：StatSecLendInterest.java
 * @创建人：张发
 * @创建时间：2010-11-10
 * @版本号：0.1
 * @说明：TODO
 * <P> 
 * @修改记录
 * 日期        |   修改人       |   版本         |   说明<br>
 * ----------------------------------------------------------------<br>
 * 2010-11-10 | 张发 | 0.1 |  
 */
public class StatSecLendInterest extends BaseIncomeStatDeal{
	
	 public ArrayList getDayIncomes(java.util.Date dDate) throws YssException {
		ArrayList alIncomes = new ArrayList();
		ArrayList alMtvMethod = new ArrayList();
		String strPortCode = "";
		try{
			String[] secCodes=this.selCodes.split(",");
			for(int i=0;i < secCodes.length;i++){
				String[] strDatas = secCodes[i].split("\t");//解析出证券代码、组合、券商、交易类型
				//modify bu huangqirong 2011-08-09 bug#2389
				if (strDatas.length > 1) {
					if (!strPortCode.equals(strDatas[1])) {
						strPortCode = strDatas[1];
						// 获取估值方法
						alMtvMethod = getValuationMethods(strPortCode);
					}
					String strBonusShares = this.getBonusShare(strDatas[0],
							strDatas[1], strDatas[2], strDatas[3], strDatas[4]);// 获取送股数
					this.calculateBorrowBond(strDatas[0], strDatas[1],
							strDatas[2], strBonusShares, alIncomes,
							alMtvMethod, strDatas[3], strDatas[4]);
				}
				//---end---
			}
			
		}catch(Exception e){
			throw new YssException("系统进行证券借贷计息时出现异常!\n", e);
		}
		return alIncomes;
	 }
	 
	 /**
	  * 借入
	  * 获取参与计提的送股
	  * @throws YssException
	  */
	 private String getBonusShare(String securityCode,String portCode,String brokerCode,String tradeTypeCode,String startDate) throws YssException {
		 StringBuffer bufSql = new StringBuffer();
		 ResultSet rs = null;
		 double dTemp = 0;
		 String strReturn = "";
		 try{
			 bufSql.append("select a.*,b.fsumamount from (select * from ").append(pub.yssGetTableName("tb_data_seclendtrade"))
			 		.append(" where fcheckstate = 1 and ftradetypecode = ")
			 		.append(tradeTypeCode.equals("B") ? dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BInPaySec) : dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BOutRecSec))
			 		.append(" and ").append(startDate).append(" <= ").append(dbl.sqlDate(beginDate))
			 		.append(" and fportcode = ").append(dbl.sqlString(portCode))
			 		.append(" and fsecuritycode = ").append(dbl.sqlString(securityCode))
			 		.append(" and fbrokercode = ").append(dbl.sqlString(brokerCode))
			 		.append(" order by fbargaindate desc,fbargaintime desc ) a")
			 		.append(" left join( select fportcode,fsecuritycode,fbrokercode,sum(ftradeamount) as fsumamount from (")
			 		.append(" select fportcode,fsecuritycode,fbrokercode, case when ftradetypecode = ")
			 		.append(tradeTypeCode.equals("B") ? dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rbsb) : dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Mhlr))
			 		.append(" then -ftradeamount else ftradeamount end as ftradeamount from ")
			 		.append(pub.yssGetTableName("tb_data_seclendtrade"))
			 		.append(" where ftradetypecode in (")
			 		.append(tradeTypeCode.equals("B") ? dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BInPaySec) : dbl.sqlString(YssOperCons.Yss_ZJDBZLX_SEC_BOutRecSec))
			 		.append(",").append(tradeTypeCode.equals("B") ? dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rbsb) : dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Mhlr))
			 		.append(" ) and ").append(startDate).append(" <= ").append(dbl.sqlDate(beginDate))
			 		.append(" and fcheckstate = 1 and fportcode = ").append(dbl.sqlString(portCode))
			 		.append(" and fsecuritycode = ").append(dbl.sqlString(securityCode))
			 		.append(" and fbrokercode = ").append(dbl.sqlString(brokerCode))
			 		.append(") group by fportcode,fsecuritycode,fbrokercode) b")
			 		.append(" on a.fportcode = b.fportcode and a.fsecuritycode = b.fsecuritycode and a.fbrokercode = b.fbrokercode");
			 rs = dbl.queryByPreparedStatement(bufSql.toString()); //modify by fangjiang 2011.08.14 STORY #788
			 while(rs.next()){
				 if(rs.getDouble("fsumamount") <= 0){
					 break;
				 }
				 dTemp += rs.getDouble("ftradeamount");				
				 if(rs.getDouble("fsumamount") - dTemp < 0){
					 strReturn = strReturn + (dTemp - rs.getDouble("fsumamount")) + "\t";
					 break;
				 }else{
					 strReturn += rs.getDouble("ftradeamount") + "\t";
					 if(rs.getDouble("fsumamount") - dTemp == 0){
						 break;
					 }
				 }
			 }
			 if(strReturn.length() > 0){
				 strReturn = strReturn.substring(0, strReturn.length() - 2);
			 }
		 }catch(Exception e){
			 throw new YssException("获取证券借贷送股交易数据出错！", e);
		 }finally{
			 dbl.closeResultSetFinal(rs);
		 }
		 return strReturn;
	 }
	 
	 /**
	  * 计算借入证券利息
	  * @throws YssException
	  */
	 private void calculateBorrowBond(String securityCode,String portCode,String brokerCode,String strBonusShares,ArrayList alIncomes,ArrayList alMtvMethod,String tradeTypeCode,String startDate)
	 		throws YssException {
		 StringBuffer bufSql = new StringBuffer();
		 ResultSet rs = null;
		 SecPecPayBean pay = null;
		 EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
		 rateOper.setYssPub(pub);
		 HashMap hmResult = new HashMap();
		 double baseCuryRate = 0;
		 double portCuryRate = 0;
		 MTVMethodBean vMethod = null;
		 boolean bFirst = true;
		 try{
	         for (int i = 0; i < alMtvMethod.size(); i++) {
	    		 double dTemp = 0;
	    		 double dBond = 0;
	    		 double dAmount = 0;
	    		 double dAmountRatio = 0;
	        	 vMethod = (MTVMethodBean) alMtvMethod.get(i);
	        	 //edit by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A 添加 证券名称
	        	 bufSql.append("select a.*,sec.FSecurityName, b.fsumamount,c.fperiodcode as ffperiodcode,c.froundcode,d.fcurycode,mk.FCsMarketPrice from (select * from ")
	        	 	.append(pub.yssGetTableName("tb_data_seclendtrade"))
			 		.append(" where fcheckstate = 1 and ftradetypecode = ")
			 		.append(tradeTypeCode.equals("B") ? dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow) : dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Loan))
			 		.append(" and ").append(startDate).append(" <= ").append(dbl.sqlDate(beginDate))
			 		.append(" and fportcode = ").append(dbl.sqlString(portCode))
			 		.append(" and fsecuritycode = ").append(dbl.sqlString(securityCode))
			 		.append(" and fbrokercode = ").append(dbl.sqlString(brokerCode))		
			 		.append(" order by fbargaindate desc,fbargaintime desc ) a")
			 		//---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---// 
			 		.append(" left join (select FSecurityCode,FSecurityName from ")//添加 证券名称
			 		.append(pub.yssGetTableName("Tb_Para_Security"))
			 		.append(" where FCheckState = 1) sec on sec.FSecurityCode = a.FSecurityCode ")
			 		//---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---// 
			 		.append(" left join(select fportcode,fsecuritycode,fbrokercode,sum(ftradeamount) as fsumamount from ( ")
			 		.append(" select fportcode,fsecuritycode,fbrokercode, case when ftradetypecode = ")
			 		.append(tradeTypeCode.equals("B") ? dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rcb) : dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Lr))
			 		.append(" then -ftradeamount else ftradeamount end as ftradeamount from ")
			 		.append(pub.yssGetTableName("tb_data_seclendtrade"))
			 		.append(" where ftradetypecode in (")
			 		.append(tradeTypeCode.equals("B") ? dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Borrow) : dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Loan))
			 		.append(",").append(tradeTypeCode.equals("B") ? dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Rcb) : dbl.sqlString(YssOperCons.YSS_SECLEND_JYLX_Lr))
			 		.append(" ) and ").append(startDate).append(" <= ").append(dbl.sqlDate(beginDate))
			 		.append(" and fcheckstate = 1 and fportcode = ").append(dbl.sqlString(portCode))
			 		.append(" and fsecuritycode = ").append(dbl.sqlString(securityCode))
			 		.append(" and fbrokercode = ").append(dbl.sqlString(brokerCode))
			 		.append(") group by fportcode,fsecuritycode,fbrokercode) b")
			 		.append(" on a.fportcode = b.fportcode and a.fsecuritycode = b.fsecuritycode and a.fbrokercode = b.fbrokercode")
			 		.append(" left join (select fsecuritycode,fbrokercode,fperiodcode,froundcode from ").append(pub.yssGetTableName("tb_para_securitylend"))
			 		.append(" where fcheckstate = 1) c on c.fsecuritycode = a.fsecuritycode and c.fbrokercode = a.fbrokercode ")
			 		.append(" left join (select fcashacccode,fcurycode from ").append(pub.yssGetTableName("tb_para_cashaccount"))
			 		.append(" where fcheckstate = 1) d on d.fcashacccode = a.fcashacccode")
			 		.append(" LEFT JOIN (SELECT mk2.FCsMarketPrice, mk2.FSecurityCode, mk1.FMktValueDate FROM (")
			 		.append(" SELECT max(FMktValueDate) as FMktValueDate,FSecurityCode FROM ").append(pub.yssGetTableName("Tb_Data_MarketValue"))
			 		.append(" WHERE FCheckState = 1 AND FMktSrcCode = ").append(dbl.sqlString(vMethod.getMktSrcCode()))
			 		.append(" AND FMktValueDate <= ").append(dbl.sqlDate(beginDate))
			 		.append(" GROUP BY FSecurityCode) mk1 JOIN (SELECT ").append(vMethod.getMktPriceCode())
			 		.append(" as FCsMarketPrice,FSecurityCode,FMktValueDate FROM ").append(pub.yssGetTableName("Tb_Data_MarketValue"))
			 		.append(" WHERE FCheckState = 1  AND FMktSrcCode = ").append(dbl.sqlString(vMethod.getMktSrcCode()))
			 		.append(") mk2 ON mk1.FSecurityCode = mk2.FSecurityCode AND mk1.FMktValueDate = mk2.FMktValueDate ) mk ON  mk.FSecurityCode = a.FSecurityCode");
	        	 rs = dbl.queryByPreparedStatement(bufSql.toString()); //modify by fangjiang 2011.08.14 STORY #788
	        	 while(rs.next()){
	        		 PeriodBean Period = new PeriodBean();
	        		 Period.setYssPub(pub);
	        		 Period.setPeriodCode(rs.getString("ffperiodcode"));
	        		 if (Period.getPeriodCode() == null || Period.getPeriodCode().trim().equals("null") ||
	        				 Period.getPeriodCode().trim().equalsIgnoreCase("")) {
	        			 throw new YssException("请在【证券借贷信息设置】中维护证券" + rs.getString("fsecuritycode") + "的期间设置！");
	        		 } 
	        		 Period.getSetting();
	             
	        		 pay = new SecPecPayBean();
	        		 pay.setTransDate(this.beginDate);
	        		 pay.setStrPortCode(rs.getString("fportcode") + "");
	        		 pay.setInvMgrCode(rs.getString("FInvmgrCode") + "");
	        		 pay.setBrokerCode(rs.getString("FBrokercode") + "");
	        		 pay.setStrSecurityCode(rs.getString("fsecuritycode") + "");
	        		 pay.setStrCuryCode(rs.getString("FCuryCode") + "");
	        		 pay.setStrTsfTypeCode(tradeTypeCode.equals("B") ? YssOperCons.YSS_ZJDBLX_Pay : YssOperCons.YSS_ZJDBLX_Rec);
	        		 pay.setStrSubTsfTypeCode(tradeTypeCode.equals("B") ? YssOperCons.YSS_SECLEND_SUBDBLX_PLI : YssOperCons.YSS_SECLEND_SUBDBLX_RLI);
	        		 pay.setAttrClsCode(rs.getString("FAttrClsCode"));
	        		 pay.setInvestType("C");//设置为交易性
	        		 //基础汇率
	        		 baseCuryRate = this.getSettingOper().getCuryRate(this.beginDate,
	                  rs.getString("FCuryCode"), pay.getStrPortCode(),
	                  YssOperCons.YSS_RATE_BASE);
	        		 //组合汇率
	        		 rateOper.getInnerPortRate(this.beginDate, rs.getString("FCuryCode"), pay.getStrPortCode());
	        		 portCuryRate = rateOper.getDPortRate();
	        		 pay.setBaseCuryRate(baseCuryRate);
	        		 pay.setPortCuryRate(portCuryRate);

	        		 dTemp += rs.getDouble("ftradeamount");
	        		 if(strBonusShares.length() > 0){//如果有送股	        
	        			 String[] bonusShares = strBonusShares.split("\t");
	        			 for(int j = bonusShares.length - 1; j >= 0; j--){
	        				 if (rs.getDouble("fsumamount") - rs.getDouble("ftradeamount") <= 0 && bFirst){
	        					 dAmount += Double.parseDouble(bonusShares[j]);
	        					 if(j == 0){
	        						 dAmount += rs.getDouble("fsumamount");
	        						 bFirst = false;
	        					 }
	        					 continue;
	        				 }
	        				 if(rs.getDouble("fsumamount") - dTemp > 0){
	        					 if(j == bonusShares.length - 1){
	        						 dAmountRatio = YssD.div(rs.getDouble("ftradeamount"), rs.getDouble("fsumamount"));
	        					 }else{
	        						 dAmountRatio = YssD.div(YssD.add(YssD.mul(dAmountRatio,Double.parseDouble(bonusShares[j + 1])),
	        								 rs.getDouble("ftradeamount")), rs.getDouble("fsumamount"));
	        					 }
		        				 dAmount = YssD.add(YssD.mul(Double.parseDouble(bonusShares[j]), dAmountRatio)
		        						 ,rs.getDouble("ftradeamount"));
	        				 }else{
	        					 if(j == bonusShares.length - 1){
	        						 dAmountRatio = YssD.div(rs.getDouble("ftradeamount"), rs.getDouble("fsumamount"));
	        					 }else{
	        						 dAmountRatio = YssD.div(YssD.add(YssD.mul(dAmountRatio,Double.parseDouble(bonusShares[j + 1])),
	        								 rs.getDouble("ftradeamount")), rs.getDouble("fsumamount"));
	        					 }
		        				 dAmount = YssD.add(YssD.mul(Double.parseDouble(bonusShares[j]), dAmountRatio)
		        						 ,YssD.sub(rs.getDouble("ftradeamount"),YssD.sub(dTemp, rs.getDouble("fsumamount"))));
	        				 }
	        			 }
	        		 }else{
	        			 dAmount = rs.getDouble("ftradeamount");
	        		 }

        			 if(rs.getDouble("fsumamount") - dTemp > 0){
	        			 if(rs.getString("FagreementType").equalsIgnoreCase("协商式")){
	        				 dBond += YssD.div(YssD.mul(dAmount, rs.getDouble("FLendRatio"), rs.getDouble("FCsMarketPrice")),
										 Period.getDayOfYear());
	        			 }else if(rs.getString("FagreementType").equalsIgnoreCase("制度式")){
	        				 dBond += YssD.div(YssD.mul(this.getSettingOper().calculatePerExp(rs.getString("FFormulaCode"), dAmount, this.beginDate)
										 , rs.getDouble("FCsMarketPrice")),Period.getDayOfYear());
	        			 }
        			 }
        			 else{
        				 if(rs.getString("FagreementType").equalsIgnoreCase("协商式")){
        					 dBond += YssD.div(YssD.mul(YssD.sub(dAmount, (bFirst ? YssD.sub(dTemp, rs.getDouble("fsumamount")) : 0.0)), 
										 rs.getDouble("FLendRatio"), rs.getDouble("FCsMarketPrice")),
										 Period.getDayOfYear());
        				 }else if(rs.getString("FagreementType").equalsIgnoreCase("制度式")){
        					 dBond += YssD.div(YssD.mul(this.getSettingOper().calculatePerExp(rs.getString("FFormulaCode")
										 , YssD.sub(dAmount, (bFirst ? YssD.sub(dTemp, rs.getDouble("fsumamount")) : 0.0)), this.beginDate)
										 , rs.getDouble("FCsMarketPrice")),Period.getDayOfYear());						 
        				 }	
        				 pay.setMoney(this.getSettingOper().reckonRoundMoney(rs.getString("froundcode"),dBond));//按舍入条件进行舍入处理
        				 pay.setMMoney(pay.getMoney());
        				 pay.setVMoney(pay.getMoney());
        				 pay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(pay.getMoney(), pay.getBaseCuryRate()));
        				 pay.setMBaseCuryMoney(pay.getBaseCuryMoney());
        				 pay.setVBaseCuryMoney(pay.getBaseCuryMoney());
        				 pay.setPortCuryMoney(this.getSettingOper().calPortMoney(pay.getMoney(),
							 pay.getBaseCuryRate(), pay.getPortCuryRate(), pay.getStrCuryCode(), 
							 pay.getTransDate(), pay.getStrPortCode()));
        				 pay.setMPortCuryMoney(pay.getPortCuryMoney());
        				 pay.setVPortCuryMoney(pay.getPortCuryMoney());
        				 String strKey = pay.getStrPortCode() + "\f" + pay.getStrSecurityCode() + "\f" + pay.getBrokerCode() + "\f" + 
        				 (tradeTypeCode.equals("B") ? YssOperCons.YSS_ZJDBLX_Pay : YssOperCons.YSS_ZJDBLX_Rec) + "\f" + 
        				 (tradeTypeCode.equals("B") ? YssOperCons.YSS_SECLEND_SUBDBLX_PLI : YssOperCons.YSS_SECLEND_SUBDBLX_RLI);
        				 

        				 
        				 hmResult.put(strKey, pay);
        				 break;
        			 }
	        	 }
	        	 bufSql.delete(0, bufSql.length());//清空后循环下个估值方法
	         }
			 Iterator iter = hmResult.values().iterator();
			 while (iter.hasNext()) {
				 alIncomes.add((SecPecPayBean)iter.next());
			 }			 
		 }catch(Exception e){
			 throw new YssException("计算证券借贷借入交易数据利息出错！", e);
		 }finally{
			 dbl.closeResultSetFinal(rs);
		 }
	 }
	 
	 /**
	  * 获取估值方法
	  * @return
	  * @throws YssException
	  */
	 private ArrayList getValuationMethods(String strPortCode) throws YssException {	 
		String strSql = "";
		ResultSet rs = null;
		MTVMethodBean vMethod = null;
		ArrayList alResult = new ArrayList();
		try {
			// 获取估值方法信息
			strSql = " select a.*, b.* from "
					+ "(select m.* from "
					+ pub.yssGetTableName("Tb_Para_MTVMethod")
					+ " m join (select FMTVCode, max(FStartDate) as FStartDate from "
					+ pub.yssGetTableName("Tb_Para_MTVMethod")
					+ " where FStartDate <= "
					+ dbl.sqlDate(new java.util.Date())
					+ " and FCheckState = 1 group by FMTVCode) n on m.FMTVCode = n.FMTVCode and m.FStartDate = n.FStartDate "
					+ ") a join (select FSubCode, FPortCode, FRelaGrade from "
					+ pub.yssGetTableName("Tb_Para_Portfolio_Relaship")
					+ " where FRelaType = 'MTV' and FPortCode IN ("
					+ dbl.sqlString(strPortCode)
					+ ") and FCheckState = 1) b on a.FMTVCode = b.FSubCode where a.FCheckState = 1 order by b.FRelaGrade desc";
			rs = dbl.queryByPreparedStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				rs.beforeFirst();
				while (rs.next()) {
					vMethod = new MTVMethodBean();
					vMethod.setMTVCode(rs.getString("FMTVCode") + "");
					vMethod.setMktSrcCode(rs.getString("FMktSrcCode") + "");
					vMethod.setMktPriceCode(rs.getString("FMktPriceCode") + "");
					vMethod.setMTVMethod(rs.getString("FMTVMethod") + "");
					vMethod.setBaseRateSrcCode(rs.getString("FBaseRateSrcCode")
							+ "");
					vMethod.setBaseRateCode(rs.getString("FBaseRateCode") + "");
					vMethod.setPortRateSrcCode(rs.getString("FPortRateSrcCode")
							+ "");
					vMethod.setPortRateCode(rs.getString("FPortRateCode") + "");

					alResult.add(vMethod);
				}
			}
			return alResult;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	 }
	 
	 public void saveIncomes(ArrayList alIncome) throws YssException {
		SecPecPayBean secpecpay = null;
		SecRecPayAdmin recpayRec = null;
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		//add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A
		Date logBeginDate = null;
		try {
			 conn.setAutoCommit(false);
	         bTrans = true;
			
			recpayRec = new SecRecPayAdmin();  
			recpayRec.setYssPub(pub);
			for (int i = 0; i < alIncome.size(); i++) {
				//add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A
				logBeginDate = new Date();
				
				secpecpay = (SecPecPayBean) alIncome.get(i);
				
				secpecpay.checkStateId = 1;				
				recpayRec = new SecRecPayAdmin();
				recpayRec.setYssPub(pub);
				recpayRec.addList(secpecpay);
				recpayRec.insert("", beginDate, endDate, YssOperCons.YSS_ZJDBLX_Rec
						+ "," + YssOperCons.YSS_ZJDBLX_Pay,
						"06RLI" + ","
								+ "07PLI",
						portCodes, "", secpecpay.getBrokerCode(), secpecpay.getStrSecurityCode(), "", -99, true); 
				
				 //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
				logInfo = "证券代码:" + secpecpay.getStrSecurityCode() + 
                		  "\r\n利息:" + secpecpay.getMoney();
                
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, false, 
                		secpecpay.getStrPortCode(), secpecpay.getTransDate(),
                		secpecpay.getTransDate(),secpecpay.getTransDate(),
                		logInfo," ",logBeginDate,logSumCode,new Date());
        		}
				 //---add by songjie 2012.09.20 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
			}
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
	   }catch (Exception e) {
       		//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
       		try{
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this,7,operType, pub, true, 
               		   secpecpay.getStrPortCode(), secpecpay.getTransDate(),
               		   secpecpay.getTransDate(),secpecpay.getTransDate(),
               		   //---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
               		   (logInfo + "\r\n计提证券借贷利息出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
               		   .replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", ""),
               		   //---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
               		   " ",logBeginDate,logSumCode,new Date());
        		}
       		}catch(Exception ex){
       			ex.printStackTrace();
       		}
       		//---add by songjie 2012.09.19 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
		    //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
       		finally{//添加 finally 保证可以抛出异常
		    	//by 曹丞 2009.01.24 保存回购计息异常信息 MS00004 QDV4.1-2009.2.1_09A
		    	throw new YssException("系统保存证券借贷计息时出现异常!" + "\n", e); 
		    }
	        //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
	   }finally{
		   dbl.endTransFinal(conn,bTrans);
	   }
	 }
}
