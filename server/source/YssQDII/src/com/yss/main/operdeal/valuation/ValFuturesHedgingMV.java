package com.yss.main.operdeal.valuation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.main.operdata.futures.FuturesHedRecpayStorageAdmin;
import com.yss.main.operdata.futures.FuturesHedgIncomeAdmin;
import com.yss.main.operdata.futures.FuturesHedgRecpayAdmin;
import com.yss.main.operdata.futures.pojo.FuturesHedRecpayStorageBean;
import com.yss.main.operdata.futures.pojo.FuturesHedgIncomeBean;
import com.yss.main.operdata.futures.pojo.FuturesHedgRecpayBean;
import com.yss.main.parasetting.MTVMethodBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 计算当天估值增值发生额，并统计应收应付库存和套期保值收益
 * @author xuqiji 20100512 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A
 *
 */
public class ValFuturesHedgingMV extends BaseValDeal{
	private ArrayList alTodayAddValue = new ArrayList();//保存当天估值增值发生额数据
	public ValFuturesHedgingMV() {
		super();
	}
	/**
	 * 入口方法
	 * @param dWorkDay
	 * @param portCode
	 * @param vMethod
	 * @param sAccountType
	 * @throws YssException
	 */
	public void doManage(java.util.Date dWorkDay,String portCode,
			MTVMethodBean vMethod,String sAccountType)throws YssException{
		try{
			//计算当天的估值增值数据,并保存当天估值增值发生额到期货被套证券应收应付数据表
			getTodayFuturesHedRecpayData(dWorkDay,portCode,vMethod,sAccountType);
			//统计应收应付库存数据，保存到期货被套证券应收应付库存表
			statFuturesHedRecpayStorageData(dWorkDay,portCode,vMethod,sAccountType);
			//统计套期保值效果，保存到套期保值交易关联表
			statFuturesHedgIncomeData(dWorkDay,portCode,vMethod,sAccountType);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	/**
	 * 统计套期保值效果，保存到套期保值收益表
	 * @param workDay 操作日期
	 * @param portCode 组合代码
	 * @param accountType 核算方式
	 * @throws YssException 
	 */
	private void statFuturesHedgIncomeData(Date workDay, String portCode,MTVMethodBean vMethod, String accountType) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
		String sKey = "";
		Connection conn =null;
		boolean bTrans = true;
		HashMap mapYesStorageIncome = null;//保存昨日套期保值收益
		FuturesHedgIncomeBean incomeBean = null;//套期保值收益表实体Bean
		FuturesHedgIncomeAdmin incomeAdmin = null;//套期保值收益表实体操作类
		try{
			mapYesStorageIncome = getYesStorageIncome(workDay,portCode,vMethod,accountType);//获取昨日套期保值收益
			buff = new StringBuffer();
			buff.append(" select a.ftradenum as FNumOrSec,a.fsecuritycode,b.fportcode,a.ftsftypecode,case when c.fhedgingtype is null then j.fhedgingtype else c.fhedgingtype end as fhedgingtype,b.fmoney,");
			buff.append(" round(b.fmoney * b.fbasecuryrate, 2) as FBaseCuryMoeny,");
			buff.append(" round(b.fmoney * b.fbasecuryrate / b.fportcuryrate, 2) as FPortCuryMoeny,");
			buff.append(" b.fbasecuryrate,b.fportcuryrate,e.FStorageAmount,c.fInComemount,c.ftradeamount,j.FStorageAmount as FYesStorageAmount from ").append(pub.yssGetTableName("tb_cash_transfer"));
			buff.append(" a join (select * from ").append(pub.yssGetTableName("tb_cash_subtransfer"));
			buff.append(" where FPortCode = ").append(dbl.sqlString(portCode));
			buff.append(" and fcheckstate = 1) b on a.fnum = b.fnum");
			
			buff.append(" left join (select sum(fInComemount) as fInComemount,sum(ftradeamount) as ftradeamount,fsecuritycode,fhedgingtype ");
			if(!accountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//先入先出
				buff.append(" ,FNum ");
			}
			buff.append(" from (select distinct g.fnum,g.fsecuritycode,g.fhedgingtype,");
			buff.append("case when i.FTradeTypeCode = '20' then g.ftradeamount else -g.ftradeamount end as fInComemount,");
			buff.append("case when i.FTradeTypeCode = '20' then i.ftradeamount else -i.ftradeamount end as ftradeamount from ");
			buff.append(pub.yssGetTableName("tb_data_futureshedging"));
			buff.append(" g join (select * from ").append(pub.yssGetTableName("tb_data_futhedgsecurity"));
			//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
			buff.append(" ) h on g.fnum = h.fnum join (select * from ").append(pub.yssGetTableName("tb_data_futurestrade_Tmp"));
			buff.append(" where FBargaindate = ").append(dbl.sqlDate(workDay));
			buff.append(" and fportcode = ").append(dbl.sqlString(portCode));
			buff.append(" ) i on g.fnum = i.FNum)group by fsecuritycode, fhedgingtype");
			if(!accountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//先入先出
				buff.append(" ,FNum ");
			}
			buff.append(" ) c on ");
			if(accountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权，关联条件不同
				buff.append(" a.ftradenum = c.FSecurityCode");
			}else{//先入先出
				buff.append(" a.ftradenum = c.FNum");
			}
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_hedgincome"));
			buff.append(" where fdate = ").append(dbl.sqlDate(YssFun.addDay(workDay,-1)));
			buff.append(" and fportcode = ").append(dbl.sqlString(portCode));
			buff.append(" ) j on a.ftradenum = j.FNumOrSec");
			
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_futtraderela"));
			buff.append(" where FTransDate = ").append(dbl.sqlDate(workDay));
			buff.append(" and FPortCode =").append(dbl.sqlString(portCode));
			buff.append(" and ftsftypecode like '09%'");
			buff.append(") e on a.ftradenum = e.FNum");
			buff.append(" join (select FLinkCode from ").append(pub.yssGetTableName("Tb_Para_MTVMethodLink"));
			buff.append(" where FCheckState = 1 and FMtvCode= ").append(dbl.sqlString(vMethod.getMTVCode())).append(" ) f on a.Fsecuritycode = f.FLinkCode");
			buff.append(" where a.fcheckstate = 1 and a.ftsftypecode = '09' and a.fsubtsftypecode = '09FU01'");
			buff.append(" and a.ftransdate = ").append(dbl.sqlDate(workDay));
			buff.append(" union all ");
			buff.append(" select d.fnumorsec,case when e.FSecurityCode is null then k.FSecurityCode else e.FSecurityCode end as FSecurityCode,d.fportcode,d.ftsftypecode,d.fhedgingtype,d.fmoney,d.fbasecurymoney,");
			buff.append(" d.fportcurymoney,d.fbasecuryrate,d.fportcuryrate,0 as FStorageAmount,0 as fInComemount,0 as ftradeamount,0 as FYesStorageAmount from ");
			buff.append(pub.yssGetTableName("Tb_Data_HedgRecpay"));
			buff.append(" d join (select FLinkCode from ").append(pub.yssGetTableName("Tb_Para_MTVMethodLink"));
			buff.append(" where FCheckState = 1 and FMtvCode= ").append(dbl.sqlString(vMethod.getMTVCode())).append(" ) f on d.Fsecuritycode = f.FLinkCode");
			buff.append(" left join (select distinct ");
			if(!accountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//先入先出
				buff.append(" g.fnum,");
			}
			buff.append(" g.fsecuritycode from ").append(pub.yssGetTableName("tb_data_futureshedging"));
			buff.append(" g join (select * from ").append(pub.yssGetTableName("tb_data_futhedgsecurity"));
			//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
			buff.append(" ) h on g.fnum = h.fnum join (select * from ").append(pub.yssGetTableName("tb_data_futurestrade_Tmp"));
			buff.append(" where FBargaindate = ").append(dbl.sqlDate(workDay));
			buff.append(" and fportcode = ").append(dbl.sqlString(portCode));
			buff.append(" ) i on g.fnum = i.FNum) e on ");
			if(accountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权，关联条件不同
				buff.append(" d.fnumorsec = e.FSecurityCode");
			}else{//先入先出
				buff.append(" d.fnumorsec = e.FNum");
			}
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_hedgincome"));
			buff.append(" where fdate = ").append(dbl.sqlDate(YssFun.addDay(workDay,-1)));
			buff.append(" and fportcode = ").append(dbl.sqlString(portCode));
			buff.append(" ) k on d.FNumOrSec = k.FNumOrSec");
			
			buff.append(" where d.FPortCode = ").append(dbl.sqlString(portCode));
			buff.append(" and d.FTradeDate =").append(dbl.sqlDate(workDay));
			
			rs = dbl.openResultSet(buff.toString());
			while(rs.next()){
				if(rs.getString("fhedgingtype") == null){ 
					continue;
				}
				sKey = rs.getString("FNumOrSec") + "\f" + rs.getString("FPortCode") + "\f" + rs.getString("FHedgingType");
				if(mapYesStorageIncome.containsKey(sKey)){//昨日有库存时要加上当天发生
					incomeBean = (FuturesHedgIncomeBean) mapYesStorageIncome.get(sKey);
					incomeBean.setSDate(YssFun.formatDate(workDay,"yyyy-MM-dd"));//库存日期
					if(accountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权，关联条件不同
						incomeBean.setDStroageAmount(YssD.add(incomeBean.getDStroageAmount(),rs.getDouble("ftradeamount")));//库存数量
					}else{
						if(rs.getDouble("ftradeamount") !=0){
							incomeBean.setDStroageAmount(YssD.add(incomeBean.getDStroageAmount(),rs.getDouble("ftradeamount")));//库存数量
						}else{
							if(!rs.getString("ftsftypecode").equalsIgnoreCase("09FU01")){//只取资金调拨中的数量
								incomeBean.setDStroageAmount(rs.getDouble("FStorageAmount"));
							}
						}
					}
					if(rs.getString("ftsftypecode").equalsIgnoreCase("09FU01")){//取套期关联表中数据是直接累加
						incomeBean.setDBal(YssD.add(incomeBean.getDBal(),rs.getDouble("fmoney")));//原币收益
						incomeBean.setDBaseCuryBal(YssD.add(incomeBean.getDBaseCuryBal(),rs.getDouble("fbasecurymoeny")));//基础货币收益
						incomeBean.setDPortCuryBal(YssD.add(incomeBean.getDPortCuryBal(),rs.getDouble("fportcurymoeny")));//组合货币收益
					}else{
						if(rs.getDouble("fInComemount") != 0 && rs.getDouble("ftradeamount") !=0 && rs.getDouble("FYesStorageAmount") ==0){
							incomeBean.setDBal(YssD.add(incomeBean.getDBal(),YssD.round(
									YssD.mul(
											YssD.div(rs.getDouble("fInComemount"),rs.getDouble("ftradeamount")),
											rs.getDouble("fmoney")),2)));//原币收益
							incomeBean.setDBaseCuryBal(YssD.add(incomeBean.getDBaseCuryBal(),YssD.round(
									YssD.mul(
											YssD.div(rs.getDouble("fInComemount"),rs.getDouble("ftradeamount")),
											rs.getDouble("fbasecurymoeny")),2)));//基础货币收益
							incomeBean.setDPortCuryBal(YssD.add(incomeBean.getDPortCuryBal(),YssD.round(
									YssD.mul(
											YssD.div(rs.getDouble("fInComemount"),rs.getDouble("ftradeamount")),
											rs.getDouble("fportcurymoeny")),2)));//组合货币收益
						}else{
							incomeBean.setDBal(YssD.add(incomeBean.getDBal(),YssD.round(
									YssD.mul(
											YssD.div(rs.getDouble("FYesStorageAmount"),rs.getDouble("FYesStorageAmount")),
											rs.getDouble("fmoney")),2)));//原币收益
							incomeBean.setDBaseCuryBal(YssD.add(incomeBean.getDBaseCuryBal(),YssD.round(
									YssD.mul(
											YssD.div(rs.getDouble("FYesStorageAmount"),rs.getDouble("FYesStorageAmount")),
											rs.getDouble("fbasecurymoeny")),2)));//基础货币收益
							incomeBean.setDPortCuryBal(YssD.add(incomeBean.getDPortCuryBal(),YssD.round(
									YssD.mul(
											YssD.div(rs.getDouble("FYesStorageAmount"),rs.getDouble("FYesStorageAmount")),
											rs.getDouble("fportcurymoeny")),2)));//组合货币收益
						}
					}
				}else{//昨日无库存
					incomeBean =new FuturesHedgIncomeBean();
					incomeBean.setSNumOrSec(rs.getString("FNumOrSec"));//交易编号
					incomeBean.setSSecurityCode(rs.getString("FSecurityCode"));//期货代码
					incomeBean.setSDate(YssFun.formatDate(workDay,"yyyy-MM-dd"));//日期
					incomeBean.setSPortCode(rs.getString("FPortCode"));//组合代码
					incomeBean.setSHedgingType(rs.getString("FHedgingType"));//套期类型
					incomeBean.setDStroageAmount(rs.getDouble("ftradeamount"));//库存数量
					incomeBean.setDBal(YssD.round(
							YssD.mul(
									YssD.div(rs.getDouble("fInComemount"),rs.getDouble("ftradeamount")),
									rs.getDouble("fmoney")),2));//原币收益
					incomeBean.setDBaseCuryBal(YssD.round(
							YssD.mul(
									YssD.div(rs.getDouble("fInComemount"),rs.getDouble("ftradeamount")),
									rs.getDouble("fbasecurymoeny")),2));//基础货币收益
					incomeBean.setDPortCuryBal(YssD.round(
							YssD.mul(
									YssD.div(rs.getDouble("fInComemount"),rs.getDouble("ftradeamount")),
									rs.getDouble("fportcurymoeny")),2));//组合货币收益
					incomeBean.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
					incomeBean.setDPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
					
					mapYesStorageIncome.put(sKey,incomeBean);
				}
			}
			//---------------------保存到期货套期保值收益表------------------//
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_HedgIncome"));//加锁
			incomeAdmin = new FuturesHedgIncomeAdmin();
			incomeAdmin.setYssPub(pub);
			incomeAdmin.deleteRealData(portCode,workDay);//删除数据
			incomeAdmin.savingRealData(mapYesStorageIncome,workDay);//保存数据
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			//--------------------------end-----------------------------------//
		}catch (Exception e) {
			throw new YssException("统计套期保值效果，保存到套期保值收益表出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn,bTrans);
		}
		
	}
	/**
	 * 获取昨日套期保值收益
	 * @param workDay
	 * @param portCode
	 * @param accountType
	 * @return
	 */
	private HashMap getYesStorageIncome(Date workDay, String portCode,MTVMethodBean vMethod, String accountType) throws YssException{
		ResultSet rs = null;
		String sKey = "";
		StringBuffer buff = null;
		HashMap mapYesStorageIncome = null;//保存昨日套期保值收益
		FuturesHedgIncomeBean incomeBean = null;//套期保值收益表实体Bean
		try{
			mapYesStorageIncome = new HashMap();
			buff =new StringBuffer();
			buff.append(" select a.* from ").append(pub.yssGetTableName("Tb_Data_HedgIncome"));
			buff.append(" a join (select FLinkCode from ").append(pub.yssGetTableName("Tb_Para_MTVMethodLink"));
			buff.append(" where FCheckState = 1 and FMtvCode= ").append(dbl.sqlString(vMethod.getMTVCode())).append(" ) f on a.Fsecuritycode = f.FLinkCode");
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_data_futtraderela"));
			buff.append(" where FTransdate = ").append(dbl.sqlDate(YssFun.addDay(workDay,0)));
			buff.append(" and fportcode = ").append(dbl.sqlString(portCode));
			buff.append(" and ftsftypecode like '09%' and fstorageamount <> 0) b on ");
			buff.append(" a.fnumorsec = b.fnum ");
			//buff.append(" where FPortCode =").append(dbl.sqlString(portCode));
			//buff.append(" and FDate = ").append(dbl.sqlDate(YssFun.addDay(workDay,-1)));
			buff.append(" where a.FPortCode =").append(dbl.sqlString(portCode));//给字段添加表别名，防止部分数据库运行时报未明确列错 by leeyu 20100909
			buff.append(" and a.FDate = ").append(dbl.sqlDate(YssFun.addDay(workDay,-1)));//给字段添加表别名，防止部分数据库运行时报未明确列错 by leeyu 20100909
			
			rs = dbl.openResultSet(buff.toString());
			while(rs.next()){
				sKey = rs.getString("FNumOrSec") + "\f" + rs.getString("FPortCode") + "\f" + rs.getString("FHedgingType");
				
				incomeBean =new FuturesHedgIncomeBean();
				incomeBean.setSNumOrSec(rs.getString("FNumOrSec"));//交易编号
				incomeBean.setSDate(YssFun.formatDate(rs.getDate("FDate"),"yyyy-MM-dd"));//日期
				incomeBean.setSSecurityCode(rs.getString("FSecurityCode"));//期货代码
				incomeBean.setSPortCode(rs.getString("FPortCode"));//组合代码
				incomeBean.setSHedgingType(rs.getString("FHedgingType"));//套期类型
				incomeBean.setDStroageAmount(rs.getDouble("FStorageAmount"));//库存数量
				incomeBean.setDBal(rs.getDouble("FBal"));//原币收益
				incomeBean.setDBaseCuryBal(rs.getDouble("FBaseCuryBal"));//基础货币收益
				incomeBean.setDPortCuryBal(rs.getDouble("FPortCuryBal"));//组合货币收益
				incomeBean.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
				incomeBean.setDPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
				
				mapYesStorageIncome.put(sKey,incomeBean);
			}
			
		}catch (Exception e) {
			throw new YssException("获取昨日套期保值收益出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);//合并太平版本调整 2010.08.24
		}
		return mapYesStorageIncome;
	}
	/**
	 * 统计应收应付库存数据，保存到期货被套证券应收应付库存表
	 * @param workDay
	 * @param portCode
	 * @param method
	 * @param sAccountType
	 */
	private void statFuturesHedRecpayStorageData(Date workDay, String portCode,MTVMethodBean vMethod,String sAccountType) throws YssException{
		StringBuffer buff = null;
		ResultSet rs = null;
		HashMap mapYesStorageValue = null;//保存昨日估值增值余额
		String sKey = "";
		FuturesHedRecpayStorageBean recpayStorageBean = null;//期货被套证券应收应付库存表实体bean
		FuturesHedRecpayStorageAdmin recpayStorageAdmin = null;//期货被套证券应收应付库存表实体操作类
		Connection conn =null;
		boolean bTrans = true;
		try{
			mapYesStorageValue = getYesStorageValue(workDay,portCode,vMethod,sAccountType);//获取昨日估值增值余额
			buff = new StringBuffer();
			buff.append(" select a.fnumorsec,a.ftsftypecode,a.fsecuritycode,a.ftradedate,a.fportcode,a.fhedgingtype,a.fmoney,");
			buff.append(" a.fbasecurymoney,a.fportcurymoney,a.fbasecuryrate,a.fportcuryrate from ");
			buff.append(pub.yssGetTableName("Tb_Data_HedgRecpay"));
			buff.append(" a join (select FLinkCode from ").append(pub.yssGetTableName("Tb_Para_MTVMethodLink"));
			buff.append(" where FCheckState = 1 and FMtvCode= ").append(dbl.sqlString(vMethod.getMTVCode())).append(" ) f on a.Fsecuritycode = f.FLinkCode");
			//modify by nimengjing BUG #744 业务处理报错  2010.12.24
			buff.append(" where a.FPortCode =").append(dbl.sqlString(portCode));
			buff.append(" and a.FTradeDate = ").append(dbl.sqlDate(workDay));
			//-------------------------------end --BUG #744-------------------------------------
			buff.append(" union all ");
			buff.append(" select ");
			if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权
				buff.append(" g.fFuturescode as fnumorsec,");
			}else{
				buff.append(" b.FSetNum as fnumorsec,");
			}
			buff.append(" b.ftsftypecode,b.fsecuritycode,b.FTradeDate,b.fportcode,g.fhedgingtype,b.fmoney,b.fbasecurymoney,");
			buff.append(" b.fportcurymoney,b.fbasecuryrate,b.fportcuryrate from ");
			buff.append(pub.yssGetTableName("tb_data_hedgrela"));
			buff.append(" b join (select c.fsecuritycode as fFuturescode,c.fhedgingtype,c.fnum,d.fsecuritycode as fsecurity ");
			buff.append(" from ").append(pub.yssGetTableName("tb_data_futureshedging"));
			buff.append(" c join (select * from ").append(pub.yssGetTableName("tb_data_futhedgsecurity"));
			buff.append(" ) d on c.fnum = d.fnum");
			//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_data_futurestrade_Tmp"));
			buff.append(" where FBargaindate =").append(dbl.sqlDate(workDay));
			buff.append(" and FPortCode = ").append(dbl.sqlString(portCode));
			buff.append(" ) e on c.fnum = e.fnum where c.fcheckstate = 1) g on b.fnum =g.fnum and b.fsecuritycode = g.fsecurity");
			buff.append(" join (select FLinkCode from ").append(pub.yssGetTableName("Tb_Para_MTVMethodLink"));
			buff.append(" where FCheckState = 1 and FMtvCode= ").append(dbl.sqlString(vMethod.getMTVCode())).append(" ) i on b.Fsecuritycode = i.FLinkCode");
			buff.append(" where b.FTradeDate =").append(dbl.sqlDate(workDay));
			buff.append(" and b.FPortCode =").append(dbl.sqlString(portCode));
			buff.append(" and b.FTsftypeCode like '19%'");
			
			
			rs = dbl.openResultSet(buff.toString());
			while(rs.next()){
				if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权
					//移动加权保存每个证券代码的数据
					sKey = rs.getString("FNumOrSec") + "\f" + rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode") + "\f" + rs.getString("FHedgingType");
				}else{
					//先入先出保存每笔开仓交易的数据
					sKey = rs.getString("FNumOrSec") + "\f"+rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode") + "\f" + rs.getString("FHedgingType");
				}
				if(mapYesStorageValue.containsKey(sKey)){//昨日有库存，要加上当天发生 - 当天的卖出估值增值
					recpayStorageBean = (FuturesHedRecpayStorageBean) mapYesStorageValue.get(sKey);
					recpayStorageBean.setSStroageDate(YssFun.formatDate(workDay,"yyyy-MM-dd"));//库存日期
					if(rs.getString("FTsfTypeCode").indexOf("19")!=-1){
						recpayStorageBean.setDBal(YssD.sub(recpayStorageBean.getDBal(),rs.getDouble("FMoney")));//原币估值增值
						recpayStorageBean.setDBaseCuryBal(YssD.sub(recpayStorageBean.getDBaseCuryBal(),rs.getDouble("FBaseCuryMoney")));//基础货币估值增值
						recpayStorageBean.setDPortCuryBal(YssD.sub(recpayStorageBean.getDPortCuryBal(),rs.getDouble("FPortCuryMoney")));//组合货币估值增值
					}else{
						recpayStorageBean.setDBal(YssD.add(recpayStorageBean.getDBal(),rs.getDouble("FMoney")));//原币估值增值
						recpayStorageBean.setDBaseCuryBal(YssD.add(recpayStorageBean.getDBaseCuryBal(),rs.getDouble("FBaseCuryMoney")));//基础货币估值增值
						recpayStorageBean.setDPortCuryBal(YssD.add(recpayStorageBean.getDPortCuryBal(),rs.getDouble("FPortCuryMoney")));//组合货币估值增值
					}
				}else{
					recpayStorageBean = new FuturesHedRecpayStorageBean();
					
					recpayStorageBean.setSNumOrSec(rs.getString("FNumOrSec"));//成交编号或者证券代码
					recpayStorageBean.setSStroageDate(YssFun.formatDate(rs.getDate("FTradeDate"),"yyyy-MM-dd"));//库存日期
					recpayStorageBean.setSSecurityCode(rs.getString("FSecurityCode"));//证券代码
					recpayStorageBean.setSPortCode(rs.getString("FPortCode"));//组合代码
					recpayStorageBean.setSHedgingType(rs.getString("FHedgingType"));//套期类型
					recpayStorageBean.setSTsfTypeCode(rs.getString("FTsfTypeCode"));//调拨类型
					recpayStorageBean.setDBal(rs.getDouble("FMoney"));//原币金额
					recpayStorageBean.setDBaseCuryBal(rs.getDouble("FBaseCuryMoney"));//基础货币金额
					recpayStorageBean.setDPortCuryBal(rs.getDouble("FPortCuryMoney"));//组合货币金额
					recpayStorageBean.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
					recpayStorageBean.setDPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
					
					mapYesStorageValue.put(sKey,recpayStorageBean);
				}
			}
			//---------------------保存到期货被套证券应收应付库存表------------------//
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Stock_HedgRecpay"));//加锁
			recpayStorageAdmin = new FuturesHedRecpayStorageAdmin();
			recpayStorageAdmin.setYssPub(pub);
			recpayStorageAdmin.deleteRealData(portCode,workDay);//删除数据
			recpayStorageAdmin.savingRealData(mapYesStorageValue,workDay);//保存数据
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			//--------------------------end-----------------------------------//
		}catch (Exception e) {
			throw new YssException("统计应收应付库存数据，保存到期货被套证券应收应付库存表出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn,bTrans);
		}
	}
	/**
	 * 获取昨日估值增值余额
	 * @param workDay
	 * @param portCode
	 * @param accountType
	 * @return
	 */
	private HashMap getYesStorageValue(Date workDay, String portCode,MTVMethodBean vMethod, String accountType) throws YssException{
		HashMap mapYesStorageValue = null;//保存昨日估值增值余额
		StringBuffer buff = null;
		ResultSet rs = null;
		String sKey = "";
		FuturesHedRecpayStorageBean recpayStorageBean = null;//期货被套证券应收应付库存表实体bean
		try{
			mapYesStorageValue = new HashMap();
			buff = new StringBuffer();
			buff.append(" select * from ").append(pub.yssGetTableName("Tb_Stock_HedgRecpay"));
			buff.append(" a join (select FLinkCode from ").append(pub.yssGetTableName("Tb_Para_MTVMethodLink"));
			buff.append(" where FCheckState = 1 and FMtvCode= ").append(dbl.sqlString(vMethod.getMTVCode())).append(" ) f on a.Fsecuritycode = f.FLinkCode");
			buff.append(" where a.FPortCode =").append(dbl.sqlString(portCode));
			buff.append(" and a.FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(workDay,-1)));
			
			rs = dbl.openResultSet(buff.toString());
			while(rs.next()){
				if(accountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权
					sKey = rs.getString("FNumOrSec") + "\f" + rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode") + "\f" + rs.getString("FHedgingType");
				}else{//先入先出
					sKey = rs.getString("FNumOrSec") + "\f"+rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode") + "\f" + rs.getString("FHedgingType");
				}
				recpayStorageBean = new FuturesHedRecpayStorageBean();
				recpayStorageBean.setSNumOrSec(rs.getString("FNumOrSec"));//成交编号
				recpayStorageBean.setSStroageDate(YssFun.formatDate(rs.getDate("FStorageDate"),"yyyy-MM-dd"));//库存日期
				recpayStorageBean.setSSecurityCode(rs.getString("FSecurityCode"));//证券代码
				recpayStorageBean.setSPortCode(rs.getString("FPortCode"));//组合代码
				recpayStorageBean.setSHedgingType(rs.getString("FHedgingType"));//套期类型
				recpayStorageBean.setSTsfTypeCode(rs.getString("FTsfTypeCode"));//调拨类型
				recpayStorageBean.setDBal(rs.getDouble("FBal"));//原币估值增值
				recpayStorageBean.setDBaseCuryBal(rs.getDouble("FBaseCuryBal"));//基础货币估值增值
				recpayStorageBean.setDPortCuryBal(rs.getDouble("FPortCuryBal"));//组合货币估值增值
				recpayStorageBean.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
				recpayStorageBean.setDPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
				
				mapYesStorageValue.put(sKey,recpayStorageBean);
			}
			
		}catch (Exception e) {
			throw new YssException("获取昨日估值增值余额出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return mapYesStorageValue;
	}
	/**
	 * 计算当天的估值增值数据
	 * @param dWorkDay
	 * @param portCode
	 * @param vMethod
	 * @param sAccountType
	 */
	private void getTodayFuturesHedRecpayData(Date dWorkDay, String portCode, MTVMethodBean vMethod, String sAccountType) throws YssException{
		ResultSet rs = null;
		Connection conn =null;
		boolean bTrans = true;
		FuturesHedgRecpayAdmin recpayAdmin = null;//期货被套证券应收应付表操作类
		try{
			//获取计算当天的估值增值数据的SQL语句
			rs = dbl.openResultSet(getTodayFuturesHedRecpayDataSQL(dWorkDay,portCode,vMethod,sAccountType));
			while(rs.next()){
				setHedgRecpayBeanData(rs);//为应收应付数据实体bean赋值
			}
			//--------------------保存当天估值增值发生额到期货被套证券应收应付数据表----------------//
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_HedgRecpay"));
			recpayAdmin = new FuturesHedgRecpayAdmin();
			recpayAdmin.setYssPub(pub);
			recpayAdmin.deleteRealData(portCode,dWorkDay);//删除数据
			recpayAdmin.savingRealData(alTodayAddValue);//保存数据
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			//--------------------------------------end-----------------------------------//
		}catch (Exception e) {
			throw new YssException("计算当天的估值增值数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn,bTrans);
		}
	}
	/**
	 * 为应收应付数据实体bean赋值
	 * @param rs
	 */
	private void setHedgRecpayBeanData(ResultSet rs) throws YssException{
		FuturesHedgRecpayBean recpayBean = null;
		double dMoney =0;//被套证券原币估值增值发生额
		double dBaseMoney =0;//被套证券基础货币估值增值发生额
		double dPortMoney =0;//被套证券组合货币估值增值发生额
		try{
			recpayBean = new FuturesHedgRecpayBean();
			//被套证券原币估值增值发生额 = Round(库存数量*行情-库存成本-昨日估值增值余额+今日卖出估值增值,2)
			dMoney = YssD.round(
					YssD.add(
							YssD.sub(YssD.div(YssD.mul(rs.getDouble("FStroageAmount"),rs.getDouble("fclosingprice")),rs.getInt("FFactor")),
									rs.getDouble("FCuryCost"),rs.getDouble("FBal")),
									rs.getDouble("FMoney")),
									2);
			//被套证券基础货币估值增值发生额=Round(库存数量*行情*基础汇率-基础货币库存成本-昨日基础货币估值增值余额+今日基础货币卖出估值增值,2)
			dBaseMoney = YssD.round(
					YssD.add(
							YssD.sub(
									YssD.mul(
											YssD.div(
													YssD.mul(rs.getDouble("FStroageAmount"),rs.getDouble("fclosingprice")),
													rs.getInt("FFactor")),
													rs.getDouble("FBaseCuryRate")),
													rs.getDouble("FBaseCuryCost"),rs.getDouble("FBaseCuryBal")),
													rs.getDouble("FBaseCuryMoney")),
													2);
			//被套证券组合货币估值增值发生额 = Round(库存数量*行情*基础汇率/组合汇率-组合货币库存成本-昨日组合货币估值增值+今日组合货币卖出估值增值)
			dPortMoney = YssD.round(
					YssD.add(
							YssD.sub(YssD.div(
										YssD.mul(
												YssD.div(
														YssD.mul(rs.getDouble("FStroageAmount"),rs.getDouble("fclosingprice")),
														rs.getInt("FFactor")),
														rs.getDouble("FBaseCuryRate")),
														rs.getDouble("FPortCuryRate")),
														rs.getDouble("FPortCuryCost"),rs.getDouble("FPortCuryBal")),
														rs.getDouble("FPortCuryMoney")),
														2);
			recpayBean.setSNumOrSec(rs.getString("FNumOrSec"));//成交编号
			recpayBean.setSTradeDate(YssFun.formatDate(rs.getDate("FStroageDate"),"yyyy-MM-dd"));//日期
			recpayBean.setSSecurityCode(rs.getString("FSecurityCode"));//证券代码
			recpayBean.setSPortCode(rs.getString("FPortCode"));//组合代码
			recpayBean.setSHedgingType(rs.getString("FHedgingType"));//套期类型
			recpayBean.setSTsfTypeCode(YssOperCons.YSS_ZJDBZLX_FU01_MV);//调拨类型
			recpayBean.setDMoney(dMoney);//原币估值增值
			recpayBean.setDBaseCuryMoeny(dBaseMoney);//基础货币估值增值
			recpayBean.setDPortCuryMoeny(dPortMoney);//组合货币估值增值
			recpayBean.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
			recpayBean.setDPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
			recpayBean.setIInOut(1);//流入流出方向
			
			alTodayAddValue.add(recpayBean);
			
		}catch (Exception e) {
			throw new YssException("为应收应付数据实体bean赋值出错！",e);
		}
		
	}
	/**
	 * 获取计算当天的估值增值数据的SQL语句
	 * @param workDay
	 * @param portCode
	 * @param vMethod
	 * @param sAccountType
	 */
	private String getTodayFuturesHedRecpayDataSQL(Date workDay, String portCode, MTVMethodBean vMethod, String sAccountType) throws YssException{
		StringBuffer buff = null;
		try{
			buff = new StringBuffer();
			
			if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权
				buff.append(" select a.*, b.fclosingprice,c.FMoney,c.FBaseCuryMoney,c.FPortCuryMoney,d.FBal,d.FBaseCuryBal,d.FPortCuryBal,e.FFactor from ");
				buff.append(pub.yssGetTableName("TB_Stock_HedgSecurity"));//
				buff.append(" a left join (select ").append(vMethod.getMktPriceCode()).append(" as fclosingprice, m2.fsecuritycode");
				buff.append(" from (select max(fmktvaluedate) as fmktvaluedate,fsecuritycode from ");
				buff.append(pub.yssGetTableName("tb_data_marketvalue"));
				buff.append(" where FCheckState = 1 and fmktvaluedate <= ").append(dbl.sqlDate(workDay));
				buff.append(" and fmktsrccode = ").append(dbl.sqlString(vMethod.getMktSrcCode()));
				buff.append(" group by fsecuritycode) m1 join (select * from ").append(pub.yssGetTableName("tb_data_marketvalue"));
				buff.append(" where FCheckState = 1 and fmktsrccode =").append(dbl.sqlString(vMethod.getMktSrcCode()));
				buff.append(" ) m2 on m1.fsecuritycode = m2.fsecuritycode and m1.fmktvaluedate = m2.fmktvaluedate) b on a.fsecuritycode = b.fsecuritycode");
				
				buff.append(" left join (select sum(FMoney) as FMoney,sum(FBaseCuryMoney) as FBaseCuryMoney,sum(FPortCuryMoney) as FPortCuryMoney,FSecurityCode from ");
				buff.append(pub.yssGetTableName("tb_data_hedgrela"));
				buff.append(" where FTradeDate =").append(dbl.sqlDate(workDay));
				buff.append(" and FPortCode =").append(dbl.sqlString(portCode));
				buff.append(" and FTsfTypeCode like '19%' group by FSecurityCode ) c on a.fsecuritycode = c.FSecurityCode");
				
				buff.append(" left join (select * from ").append(pub.yssGetTableName("Tb_Stock_HedgRecpay"));
				buff.append(" where FPortCode = ").append(dbl.sqlString(portCode));
				buff.append(" and FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(workDay,-1)));
				buff.append(" and FTsfTypeCode like '09%') d on a.fsecuritycode = d.FSecurityCode and a.fnumorsec = d.FNumOrSec and a.fhedgingtype =d.fhedgingtype");
				buff.append(" join(select * from ").append(pub.yssGetTableName("tb_para_Security")).append(" where FCheckState =1");
				buff.append(" ) e on a.fsecuritycode = e.fsecuritycode");
				buff.append(" join (select FLinkCode from ").append(pub.yssGetTableName("Tb_Para_MTVMethodLink"));
				buff.append(" where FCheckState = 1 and FMtvCode= ").append(dbl.sqlString(vMethod.getMTVCode())).append(" ) f on a.Fsecuritycode = f.FLinkCode");
				buff.append(" where a.fstroagedate = ").append(dbl.sqlDate(workDay));
				buff.append(" and a.fportcode =").append(dbl.sqlString(portCode)).append(" and a.fstroageamount <> 0");
			}else{//先入先出
				buff.append(" select a.*, b.fclosingprice,c.FMoney,c.FBaseCuryMoney,c.FPortCuryMoney,d.FBal,d.FBaseCuryBal,d.FPortCuryBal,e.FFactor from ");
				buff.append(pub.yssGetTableName("TB_Stock_HedgSecurity"));
				buff.append(" a left join (select ").append(vMethod.getMktPriceCode()).append(" as fclosingprice, m2.fsecuritycode");
				buff.append(" from (select max(fmktvaluedate) as fmktvaluedate,fsecuritycode from ");
				buff.append(pub.yssGetTableName("tb_data_marketvalue"));
				buff.append(" where FCheckState = 1 and fmktvaluedate <= ").append(dbl.sqlDate(workDay));
				buff.append(" and fmktsrccode = ").append(dbl.sqlString(vMethod.getMktSrcCode()));
				buff.append(" group by fsecuritycode) m1 join (select * from ").append(pub.yssGetTableName("tb_data_marketvalue"));
				buff.append(" where FCheckState = 1 and fmktsrccode =").append(dbl.sqlString(vMethod.getMktSrcCode()));
				buff.append(" ) m2 on m1.fsecuritycode = m2.fsecuritycode and m1.fmktvaluedate = m2.fmktvaluedate) b on a.fsecuritycode = b.fsecuritycode");
				
				buff.append(" left join (select sum(Fmoney) as Fmoney,sum(FBaseCuryMoney) as FBaseCuryMoney,sum(FPortCuryMoney) as FPortCuryMoney,");
				buff.append(" FSetNum,fsecuritycode from ");
				buff.append(pub.yssGetTableName("tb_data_hedgrela"));
				buff.append(" where FTradeDate =").append(dbl.sqlDate(workDay));
				buff.append(" and FPortCode =").append(dbl.sqlString(portCode));
				buff.append(" and FTsfTypeCode like '19%' group by FSetNum,fsecuritycode) c on a.fsecuritycode = c.FSecurityCode and c.FSetNum = a.fnumorsec");
				
				buff.append(" left join (select * from ").append(pub.yssGetTableName("Tb_Stock_HedgRecpay"));
				buff.append(" where FPortCode = ").append(dbl.sqlString(portCode));
				buff.append(" and FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(workDay,-1)));
				buff.append(" and FTsfTypeCode like '09%') d on a.fsecuritycode = d.FSecurityCode and a.fnumorsec = d.FNumOrSec and a.fhedgingtype =d.fhedgingtype");
				buff.append(" join(select * from ").append(pub.yssGetTableName("tb_para_Security")).append(" where FCheckState =1");
				buff.append(" ) e on a.fsecuritycode = e.fsecuritycode");
				buff.append(" join (select FLinkCode from ").append(pub.yssGetTableName("Tb_Para_MTVMethodLink"));
				buff.append(" where FCheckState = 1 and FMtvCode= ").append(dbl.sqlString(vMethod.getMTVCode())).append(" ) f on a.Fsecuritycode = f.FLinkCode");
				buff.append(" where a.fstroagedate = ").append(dbl.sqlDate(workDay));
				buff.append(" and a.fportcode =").append(dbl.sqlString(portCode)).append(" and a.fstroageamount <> 0");
			}
			
		}catch (Exception e) {
			throw new YssException("获取计算当天的估值增值数据的SQL语句出错！",e);
		}
		return buff.toString();
	}
}






















