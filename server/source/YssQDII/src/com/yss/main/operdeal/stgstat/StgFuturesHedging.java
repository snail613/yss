package com.yss.main.operdeal.stgstat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import com.yss.main.operdata.futures.FuturesHedgRelaAdmin;
import com.yss.main.operdata.futures.FuturesHedgingStorageAdmin;
import com.yss.main.operdata.futures.pojo.FuturesHedgRelaBean;
import com.yss.main.operdata.futures.pojo.FuturesHedgingStorageBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 *  期货套期保值库存统计
 * @author xuqiji 20100510 MS01133 现有版本增加国内期货业务及套期保值处理  QDV4深圳2010年04月28日01_A
 *
 */
public class StgFuturesHedging extends BaseStgStatDeal{
	private ArrayList alRealData = new ArrayList();//保存期货套期保值交易关联数据
	private ArrayList alStorageData = new ArrayList();//保存期货被套证券库存数据
	public StgFuturesHedging() {
		super();
	}
	/**
	 * 入口方法
	 * @param dDate 操作日期
	 * @throws YssException
	 */
	public void doHedgingManage(Date dDate) throws YssException{
		// 期货核算方式 
		String sAccountType = "";
		Connection conn = null;
		boolean bTrans = true;
		FuturesHedgRelaAdmin realAdmin =null;//期货套期保值交易关联操作类
		FuturesHedgingStorageAdmin storageAdmin = null;//期货被套证券库存操作类
		try{
			//清空集合
			if(alRealData.size()>0){
				alRealData.clear();
			}
			if(alStorageData.size() > 0){
				alStorageData.clear();
			}
			//获取期货核算方式
			sAccountType = getAccountTypeBy(this.portCodes);
			//处理当天期货交易的成本和估值增值存入"套期保值交易关联表（Tb_XXX_Data_HedgRela）"
			//成本 = 调拨类型为05+证券品种类型，估值增值=调拨类型为19+证券品种类型。
			doTheDayFuturesTradeHedging(dDate,this.portCodes,sAccountType);
			
			//----------------------------保存套期保值交易关联数据------------------------//
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//手动提交事物
			
			realAdmin = new FuturesHedgRelaAdmin();
			realAdmin.setYssPub(pub);
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_HedgRela"));//给表加锁
			realAdmin.deleteRealData(this.portCodes,dDate);//删除数据
			realAdmin.savingRealData(alRealData);//保存数据
			
			conn.commit();//提交
			conn.setAutoCommit(true);//自动提交事物
			bTrans = false;
			//-----------------------------end---------------------------------------//
			
			//------------------------统计当天库存------------------------//
			doTodayFuturesStorageAccount(dDate,this.portCodes,sAccountType);
			//------------------------end-------------------------------//
			//--------------------------保存库存统计数据-------------------------------//
			dbl.endTransFinal(conn,bTrans);//关闭连接
			
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//手动提交事物
			bTrans = true;
			
			storageAdmin = new FuturesHedgingStorageAdmin();
			storageAdmin.setYssPub(pub);
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("TB_Stock_HedgSecurity"));//给表加锁
			storageAdmin.deleteStorageData(this.portCodes,dDate);//删除数据
			storageAdmin.savingRealData(alStorageData,dDate);//保存数据
			
			conn.commit();//提交
			conn.setAutoCommit(true);//自动提交事物
			bTrans = false;
			//----------------------------end---------------------------------------//
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	/**
	 * 统计当天库存
	 * @param date
	 * @param sPortCode
	 * @param sAccountType
	 */
	private void doTodayFuturesStorageAccount(Date date, String sPortCode, String sAccountType) throws YssException{
		ResultSet rs = null;
		HashMap mapYesStorageData = null;//保存昨日库存数据
		FuturesHedgingStorageBean storageBean = null;//库存实体bean
		String sKey = "";
		Iterator it =null;
        double dBaseRate = 1;//基础汇率
        double dPortRate = 1;//组合汇率
		try{
			mapYesStorageData = getYesStorageData(date,sPortCode,sAccountType);//获取昨日库存数据
			rs = dbl.openResultSet(getStorageAccountSQL(date,sPortCode,sAccountType));//获取库存统计的SQL语句
			
			while(rs.next()){
				if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权
					sKey = rs.getString("FNumOrSec") +"\f"+ rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode") + "\f" + rs.getString("FHedgingType");
				}else{//先入先出
					sKey = rs.getString("FNumOrSec") + "\f"+rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode") + "\f" + rs.getString("FHedgingType");
					if(rs.getDouble("FTradeAmount")!=0 && rs.getDouble("fmoney")==0){//先入先出时会产生一笔被平仓完的交易关联数据，此时不需要统计它
						continue;
					}
				}
				if(mapYesStorageData.containsKey(sKey)){//昨日有库存
		            dBaseRate = this.getSettingOper().getCuryRate(date,
		                    rs.getString("ftradecury"),//币种
		                    rs.getString("FPortCode"),//组合代码
		                    YssOperCons.YSS_RATE_BASE);//获取基础汇率的值
		            dPortRate = this.getSettingOper().getCuryRate(date,
		                rs.getString("fportcury"),
		                rs.getString("FPortCode"),
		                YssOperCons.YSS_RATE_PORT);//获取组合汇率的值
					storageBean = (FuturesHedgingStorageBean) mapYesStorageData.get(sKey);
					storageBean.setSStroageDate(YssFun.formatDate(date,"yyyy-MM-dd"));
					storageBean.setDBaseCuryRate(dBaseRate);//基础汇率
					storageBean.setDPortCuryRate(dPortRate);//组合汇率
					if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权
						storageBean.setDStroageAmount(YssD.add(storageBean.getDStroageAmount(),rs.getDouble("FTradeAmount")));//库存数量
						storageBean.setDCuryCost(YssD.add(storageBean.getDCuryCost(),rs.getDouble("fmoney")));//原币成本
						storageBean.setDBaseCuryCost(YssD.add(storageBean.getDBaseCuryCost(),rs.getDouble("fbasecurymoney")));//基础货币成本
						storageBean.setDPortCuryCost(YssD.add(storageBean.getDPortCuryCost(),rs.getDouble("fportcurymoney")));//组合货币成本
					}else{
						if(rs.getString("FTradeTypeCode").equalsIgnoreCase("20")){//当为开仓交易时
							storageBean.setDStroageAmount(YssD.add(storageBean.getDStroageAmount(),rs.getDouble("FTradeAmount")));//库存数量
						}else{//平仓交易
							storageBean.setDStroageAmount(YssD.add(storageBean.getDStroageAmount(),rs.getDouble("FAmount")));//库存数量
						}
						storageBean.setDCuryCost(YssD.add(storageBean.getDCuryCost(),rs.getDouble("fmoney")));//原币成本
						storageBean.setDBaseCuryCost(YssD.add(storageBean.getDBaseCuryCost(),rs.getDouble("fbasecurymoney")));//基础货币成本
						storageBean.setDPortCuryCost(YssD.add(storageBean.getDPortCuryCost(),rs.getDouble("fportcurymoney")));//组合货币成本
					}
				}else{//昨日没有库存时
					storageBean = setStorageBeanData(rs,date);//库存实体bean赋值
					mapYesStorageData.put(sKey,storageBean);
				}
			}
			it = mapYesStorageData.values().iterator();
			while(it.hasNext()){
				alStorageData.add(it.next());
			}
		}catch (Exception e) {
			throw new YssException("统计当天库存出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 获取昨日库存数据
	 * @param date
	 * @param portCode
	 * @param accountType
	 * @return
	 */
	private HashMap getYesStorageData(Date date, String portCode, String accountType) throws YssException{
		HashMap mapYesStorageData = null;//保存昨日库存数据
		String sKey = "";
		ResultSet rs = null;
		FuturesHedgingStorageBean storageBean = null;//库存实体bean
		StringBuffer buff = null;
		try{
			mapYesStorageData = new HashMap();
			buff = new StringBuffer();
			buff.append(" select * from ").append(pub.yssGetTableName("tb_stock_hedgsecurity"));
			buff.append(" where FStroageDate =").append(dbl.sqlDate(YssFun.addDay(date,-1)));
			buff.append(" and FPortCode = ").append(dbl.sqlString(portCode));
			
			rs = dbl.openResultSet(buff.toString());
			while(rs.next()){
				if(accountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权
					sKey = rs.getString("FNumOrSec") + "\f" + rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode") + "\f" + rs.getString("FHedgingType");
				}else{//先入先出
					sKey = rs.getString("FNumOrSec") + "\f"+rs.getString("FSecurityCode") + "\f" + rs.getString("FPortCode") + "\f" + rs.getString("FHedgingType");
				}
				storageBean = new FuturesHedgingStorageBean();
				storageBean.setSNumOrSec(rs.getString("FNumOrSec"));//当使用先入先出核算时存期货交易编号，当使用移动加权核算时存期货证券代码
				storageBean.setSStroageDate(YssFun.formatDate(rs.getDate("FStroageDate"),"yyyy-MM-dd"));//库存日期
				storageBean.setSSecurityCode(rs.getString("FSecurityCode"));//证券代码
				storageBean.setSPortCode(rs.getString("FPortCode"));//组合代码
				storageBean.setSHedgingType(rs.getString("FHedgingType"));//套期类型 ‘fairvalue’-公允价值套期；‘cash’-现金流套期
				storageBean.setDStroageAmount(rs.getDouble("FStroageAmount"));//库存数量
				storageBean.setDCuryCost(rs.getDouble("FCuryCost"));//原币成本
				storageBean.setDBaseCuryCost(rs.getDouble("FBaseCuryCost"));//基础货币成本
				storageBean.setDPortCuryCost(rs.getDouble("FPortCuryCost"));//组合货币成本
				storageBean.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
				storageBean.setDPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
				
				mapYesStorageData.put(sKey,storageBean);
			}
			
		}catch (Exception e) {
			throw new YssException("获取昨日库存数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return mapYesStorageData;
	}
	/**
	 * 库存实体bean赋值
	 * @param rs
	 */
	private FuturesHedgingStorageBean setStorageBeanData(ResultSet rs,Date date) throws YssException{
		FuturesHedgingStorageBean storageBean = null;//库存实体bean
        double dBaseRate = 1;//基础汇率
        double dPortRate = 1;//组合汇率
		try{
            dBaseRate = this.getSettingOper().getCuryRate(date,
                    rs.getString("ftradecury"),//币种
                    rs.getString("FPortCode"),//组合代码
                    YssOperCons.YSS_RATE_BASE);//获取基础汇率的值
            dPortRate = this.getSettingOper().getCuryRate(date,
                rs.getString("fportcury"),
                rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_PORT);//获取组合汇率的值
			storageBean = new FuturesHedgingStorageBean();
			storageBean.setSNumOrSec(rs.getString("FNumOrSec"));//当使用先入先出核算时存期货交易编号,当使用移动加权核算时存期货证券代码
			storageBean.setSStroageDate(YssFun.formatDate(date,"yyyy-MM-dd"));//库存日期
			storageBean.setSSecurityCode(rs.getString("FSecurityCode"));//证券代码
			storageBean.setSPortCode(rs.getString("FPortCode"));//组合代码
			storageBean.setSHedgingType(rs.getString("FHedgingType"));//套期类型 ‘fairvalue’-公允价值套期；‘cash’-现金流套期
			storageBean.setDStroageAmount(rs.getDouble("FTradeAmount"));//库存数量
			storageBean.setDCuryCost(rs.getDouble("fmoney"));//原币成本
			storageBean.setDBaseCuryCost(rs.getDouble("fbasecurymoney"));//基础货币成本
			storageBean.setDPortCuryCost(rs.getDouble("fportcurymoney"));//组合货币成本
			storageBean.setDBaseCuryRate(dBaseRate);//基础汇率
			storageBean.setDPortCuryRate(dPortRate);//组合汇率
		}catch (Exception e) {
			throw new YssException("库存实体bean赋值出错！",e);
		}
		return storageBean;
	}
	/**
	 * 获取库存统计的SQL语句
	 * @param date
	 * @param portCode
	 * @param sAccountType
	 */
	private String getStorageAccountSQL(Date date, String portCode, String sAccountType) throws YssException{
		StringBuffer buff = null;
		try{
			buff = new StringBuffer();
			//取数方法：期货套期证券关联表关联期货套期保值数据表取出当天的套期证券数据，关联期货交易数据表取出交易类型，
			//关联套期保值交易关联表取出当天成本关联证券信息表取出交易币种，关联组合信息设置表取出组合币种
			//当使用先入先出核算时存期货交易编号,当使用移动加权核算时存期货证券代码
			if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权
				buff.append(" select g.*,i.ftradecury,j.fportcury from (select ");
				buff.append(" sum(case when f.FTradeTypeCode = '20' then f.FTradeAmount else -f.FTradeAmount end) as FTradeAmount,");
				buff.append(" sum(case when f.FTradeTypeCode = '20' then f.fmoney else -f.fmoney end) as fmoney,");
				buff.append(" sum(case when f.FTradeTypeCode = '20' then f.fbasecurymoney else -f.fbasecurymoney end) as fbasecurymoney,");
				buff.append(" sum(case when f.FTradeTypeCode = '20' then f.fportcurymoney else -f.fportcurymoney end) as fportcurymoney,");
				buff.append(" f.fsecuritycode,f.fportcode,f.FHedgingType,f.FutureCode as FNumOrSec from ");
				buff.append(" (select c.*, d.FTradeTypeCode, e.* from (select a.*,b.fsecuritycode as FutureCode, b.fhedgingtype from ");
				buff.append(pub.yssGetTableName("TB_Data_FutHedgSecurity"));
				buff.append(" a join (select * from ").append(pub.yssGetTableName("Tb_Data_FuturesHedging"));
				buff.append(" ) b on a.fnum = b.FNum) c");
				//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
				buff.append(" join (select * from ").append(pub.yssGetTableName("tb_data_futurestrade_Tmp"));
				buff.append(" where FBargaindate =").append(dbl.sqlDate(date));
				buff.append(" and FPortCode = ").append(dbl.sqlString(portCode));
				buff.append(" ) d on c.FNum = d.FNum");
				buff.append(" join (select sum(fmoney) as fmoney,sum(fbasecurymoney) as fbasecurymoney,sum(fportcurymoney) as fportcurymoney,");
				buff.append(" fnum as FRealNum,fsecuritycode as FRealSecuritycode,fportcode from ");
				buff.append(pub.yssGetTableName("tb_data_hedgrela"));
				buff.append(" where FTradeDate =").append(dbl.sqlDate(date));
				buff.append(" and FPortCode = ").append(dbl.sqlString(portCode));
				buff.append(" and FTsfTypeCode like '05%' group by fnum, fsecuritycode, fportcode) e on c.FNum = e.FRealNum and c.fsecuritycode = e.FRealSecuritycode) f");
				buff.append(" group by f.fsecuritycode, f.fportcode, f.FHedgingType,f.FutureCode) g");
				buff.append(" join (select * from ").append(pub.yssGetTableName("tb_para_security"));
				buff.append(" where FCheckState = 1) i on g.fsecuritycode = i.fsecuritycode");
				buff.append(" join (select * from ").append(pub.yssGetTableName("tb_para_portfolio"));
				buff.append(" where FCheckState = 1) j on g.fportcode = j.fportcode");
			}else{//先入先出
				buff.append(" select g.*,i.ftradecury,j.fportcury from (select ");
				buff.append(" sum(case when f.FTradeTypeCode = '20' then f.FTradeAmount else -f.FTradeAmount end) as FTradeAmount,");
				buff.append(" sum(case when f.FTradeTypeCode = '20' then f.fmoney else -f.fmoney end) as fmoney,");
				buff.append(" sum(case when f.FTradeTypeCode = '20' then f.fbasecurymoney else -f.fbasecurymoney end) as fbasecurymoney,");
				buff.append(" sum(case when f.FTradeTypeCode = '20' then f.fportcurymoney else -f.fportcurymoney end) as fportcurymoney,");
				buff.append(" sum(case when f.FTradeTypeCode = '20' then f.famount else -f.famount end) as famount,");
				buff.append(" f.FSetNum as FNumOrSec,f.fsecuritycode,f.fportcode,f.FHedgingType,f.FTradeTypeCode from ");
				buff.append(" (select c.*, d.FTradeTypeCode, e.* from (select a.*, b.fhedgingtype from ");
				buff.append(pub.yssGetTableName("TB_Data_FutHedgSecurity"));
				buff.append(" a join (select * from ").append(pub.yssGetTableName("Tb_Data_FuturesHedging"));
				buff.append(" ) b on a.fnum = b.FNum) c");
				//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
				buff.append(" join (select * from ").append(pub.yssGetTableName("tb_data_futurestrade_Tmp"));
				buff.append(" where FBargaindate =").append(dbl.sqlDate(date));
				buff.append(" and FPortCode = ").append(dbl.sqlString(portCode));
				buff.append(" ) d on c.FNum = d.FNum");
				buff.append(" join (select sum(fmoney) as fmoney,sum(fbasecurymoney) as fbasecurymoney,sum(fportcurymoney) as fportcurymoney,sum(famount) as famount,");
				buff.append(" fnum as FRealNum,FSetNum,fsecuritycode as FRealSecuritycode,fportcode from ");
				buff.append(pub.yssGetTableName("tb_data_hedgrela"));
				buff.append(" where FTradeDate =").append(dbl.sqlDate(date));
				buff.append(" and FPortCode = ").append(dbl.sqlString(portCode));
				buff.append(" and FTsfTypeCode like '05%' group by fnum, FSetNum,fsecuritycode, fportcode) e on c.FNum = e.FRealNum and c.fsecuritycode = e.FRealSecuritycode) f");
				buff.append(" group by f.fnum,f.FSetNum,f.fsecuritycode, f.fportcode, f.FHedgingType,f.FTradeTypeCode) g");//先入先出关联表保存每一笔交易的成本
				
				buff.append(" join (select * from ").append(pub.yssGetTableName("tb_para_security"));
				buff.append(" where FCheckState = 1) i on g.fsecuritycode = i.fsecuritycode");
				buff.append(" join (select * from ").append(pub.yssGetTableName("tb_para_portfolio"));
				buff.append(" where FCheckState = 1) j on g.fportcode = j.fportcode order by g.FNumOrSec,g.fsecuritycode");
			}
			
		}catch (Exception e) {
			throw new YssException("获取库存统计的SQL语句出错！",e);
		}
		return buff.toString();
	}
	/**
	 * 处理当天期货交易的成本和估值增值存入"套期保值交易关联表（Tb_XXX_Data_HedgRela）"
	 * @param date
	 * @throws YssException 
	 */
	private void doTheDayFuturesTradeHedging(Date date,String sPortCode,String sAccountType) throws YssException {
		HashMap FuturesKCTrade = null;//保存期货昨日和今日开仓交易数据，按照成交顺序保存
		try{			
			if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)){//移动加权
				doTheDayFuturesTradeHedgingMODAVG(date,sPortCode);//移动加权法处理当天期货交易的成本和估值增值存入"套期保值交易关联表（Tb_XXX_Data_HedgRela）"
			}else{//先入先出
				FuturesKCTrade = getFuturesKCTradeFIFO(date,sPortCode);//先入先出法获取期货昨日和今日开仓交易数据
				doTheDayFuturesPCTradeData(FuturesKCTrade,date,sPortCode);//处理今天平仓交易数据
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	/**
	 * 先入先出法处理今天平仓交易数据
	 * 先入先出法进行平仓交易，要平掉交易编号靠前的数据
	 * @param futuresKCTrade
	 */
	private void doTheDayFuturesPCTradeData(HashMap futuresKCTrade,Date date,String sPortCode) throws YssException{
		StringBuffer buff = null;
		ResultSet rs = null;
		String sKey = "";
		ArrayList alFuturesKCTrade = null;//保存开仓交易数据
		FuturesHedgRelaBean realBean = null;//期货套期保值交易关联表实体bean
		FuturesHedgRelaBean realNewBean = null;//期货套期保值交易关联表实体bean
		FuturesHedgRelaBean realPCBean = null;//期货套期保值交易关联表实体bean
		double sTotalKCAmount = 0;//总的开仓数量
		double [] yesDateKCValueBalance = null;//保存昨日开仓的估值增值
        double thDayTradeAmount = 0;//今天平仓交易数量
        double PCCost = 0;//保存平仓交易平掉的开仓成本
        double dMoney = 0; //原币金额
        double dBaseMoney = 0; //基础货币金额
        double dPortMoney = 0; //组合货币金额
        double dYesDateAmount =0;//昨日库存数量
        FuturesHedgRelaBean realPCAddValueBean = null;//期货套期保值交易关联表实体bean
        HashMap mapPCAddValue =null;// 保存期货平仓估值增值
		try{
			mapPCAddValue = new HashMap();
			buff = new StringBuffer();
			//获取当天每一笔开仓交易数据
			buff.append(" select b.fnum,d.FBargaindate,b.fsecuritycode,d.FPortCode,c.fhedgingtype,b.ftradeamount,b.ftrademoney as fcurycost,");
			buff.append(" round(b.ftrademoney * d.fbasecuryRate, 2) as fbasecurycost,");
			buff.append(" round(b.ftrademoney * d.fbasecuryRate / d.fportcuryRate, 2) as fportcurycost,e.fcatcode,b.fbasecuryrate,b.fportcuryrate from ");
			buff.append(pub.yssGetTableName("TB_Data_FutHedgSecurity"));
			buff.append(" b join (select * from ").append(pub.yssGetTableName("Tb_Data_FuturesHedging"));
			buff.append(" ) c on b.fnum = c.fnum");
			//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_data_futurestrade_Tmp"));
			buff.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(sPortCode));
			buff.append(" and FBargaindate = ").append(dbl.sqlDate(date));
			buff.append(" and FTradeTypeCode = '21') d on b.fnum = d.FNum ");
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_para_security"));
			buff.append(" where FCheckState = 1) e on b.fsecuritycode = e.fsecuritycode");
			buff.append(" order by b.fsecuritycode,b.fnum");
			
			rs= dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			while(rs.next()){
				sKey = rs.getString("FSecurityCode")+"\t" + rs.getString("FPortCode");
				if(futuresKCTrade.containsKey(sKey)){//有当天开仓交易数据
					alFuturesKCTrade = (ArrayList) futuresKCTrade.get(sKey);//获取保存开仓交易的集合
					for(int i =0; i < alFuturesKCTrade.size(); i++){//循环集合
						realBean = (FuturesHedgRelaBean) alFuturesKCTrade.get(i);//获取集合中每一个实体bean
						//获取昨日库存
						dYesDateAmount = getYesDateAmount(realBean.getSNum(),realBean.getSPortCode(),realBean.getSTradeDate(),realBean.getSSecurityCode());
						sTotalKCAmount +=realBean.getDTradeAmount();//把开仓交易进行累加，和当天平仓交易的数量进行比较
						if(i == alFuturesKCTrade.size()-1 && rs.getDouble("ftradeamount") > sTotalKCAmount ){//如果到最后一个集合中的实体bean时，平仓数量依然大于所有开仓数量之和
							throw new YssException("库存不足,无法进行先入先出平仓");
						}else if(rs.getDouble("ftradeamount") <= sTotalKCAmount){//当日平仓数量小于开仓交易之和
							for(int j =0;j <= i; j++){//重新循环开仓数量在每一笔开仓交易集合内的数据
								realNewBean = (FuturesHedgRelaBean) alFuturesKCTrade.get(j);//获取每一笔开仓数据
								//获取昨日的估值增值
								yesDateKCValueBalance = getYesDateKCValueBalance(realNewBean.getSNum(),realNewBean.getSPortCode(),realBean.getSTradeDate(),realBean.getSSecurityCode());//获取昨日每笔开仓交易的估值增值余额
								if(j < i){//此处是为了判断平仓会平掉几笔开仓交易
									realPCBean =new FuturesHedgRelaBean();
									thDayTradeAmount += realNewBean.getDTradeAmount();//累加每一笔开仓交易数量
									//PCCost += realNewBean.getDMoney();
									//----------------------成本数据-------------------------//
									dMoney = realNewBean.getDMoney();
									dBaseMoney =realNewBean.getDBaseCuryMoeny();
									dPortMoney = realNewBean.getDPortCuryMoeny();
									realPCBean.setSSetNum(realNewBean.getSNum());
									//套期保值交易关联表bean赋值出错
									setHedgRelaBeanData(realPCBean,rs,sPortCode,date,dMoney,dBaseMoney,dPortMoney,"05"+rs.getString("fcatcode"));
									realPCBean.setDTradeAmount(realNewBean.getDTradeAmount());
									alRealData.add(realPCBean);
									//-------------------------end-------------------------//
									//------------------------估值增值数据-------------------//
									if(realNewBean.getDTradeAmount() ==0){//当之前的平仓把这笔开仓平完时，直接进行下次循环
										continue;
									}
									//当天有多笔平仓时，前面的平仓没有把这笔开仓平完，估值增值计算时，要把之前平仓带出的估值增值减掉
									if(mapPCAddValue.containsKey(sKey)){
										realPCAddValueBean = (FuturesHedgRelaBean) mapPCAddValue.get(sKey);
										dMoney = YssD.sub(yesDateKCValueBalance[0],realPCAddValueBean.getDMoney());
										dBaseMoney = YssD.sub(yesDateKCValueBalance[1],realPCAddValueBean.getDBaseCuryMoeny());
										dPortMoney = YssD.sub(yesDateKCValueBalance[2],realPCAddValueBean.getDPortCuryMoeny());
									}else{//如果该平仓直接把这笔开仓平完，该开仓的估值增值要全部带出
										dMoney = yesDateKCValueBalance[0];
										dBaseMoney = yesDateKCValueBalance[1];
										dPortMoney = yesDateKCValueBalance[2];
									}
									
									realPCBean =new FuturesHedgRelaBean();
									realPCBean.setSSetNum(realNewBean.getSNum());
									//套期保值交易关联表bean赋值出错
									setHedgRelaBeanData(realPCBean,rs,sPortCode,date,dMoney,dBaseMoney,dPortMoney,"19"+rs.getString("fcatcode"));
									realPCBean.setDTradeAmount(realNewBean.getDTradeAmount());
									alRealData.add(realPCBean);
									//平仓完后，要把这笔开仓的数据清为0
									realNewBean.setSTradeDate(YssFun.formatDate(date,"yyyy-MM-dd"));
									realNewBean.setDTradeAmount(0);
									realNewBean.setDMoney(0);
									realNewBean.setDBaseCuryMoeny(0);
									realNewBean.setDPortCuryMoeny(0);
									//-------------------------end-------------------------//
								}else{
									//----------------------成本数据-------------------------//
									//先进先出平仓成本 = 原先入先出库存成本/原先入先出库存数量 * 平仓数量 + 平仓掉的成本
									dMoney = YssD.round(YssD.add(PCCost,
											YssD.mul(
													YssD.div(realNewBean.getDMoney(),realNewBean.getDTradeAmount()),
											YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount))),2);
									dBaseMoney = YssD.round(YssD.mul(dMoney,rs.getDouble("FBaseCuryRate")),2);
									dPortMoney = YssD.round(YssD.div(YssD.mul(dMoney,rs.getDouble("FBaseCuryRate")),rs.getDouble("FPortCuryRate")),2);
									realPCBean =new FuturesHedgRelaBean();
									realPCBean.setSSetNum(realNewBean.getSNum());
									//套期保值交易关联表bean赋值出错
									setHedgRelaBeanData(realPCBean,rs,sPortCode,date,dMoney,dBaseMoney,dPortMoney,"05"+rs.getString("fcatcode"));
									realPCBean.setDTradeAmount(YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount));
									alRealData.add(realPCBean);
									//-------------------------end-------------------------//
									//------------------------估值增值数据-------------------//
									//估值增值 = 昨日估值增值/昨日数量 * 平仓数量
									dMoney = YssD.round(
											YssD.mul(
													YssD.div(yesDateKCValueBalance[0],dYesDateAmount),
													YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount)),2);
									dBaseMoney = YssD.round(
											YssD.mul(
													YssD.div(yesDateKCValueBalance[1],dYesDateAmount),
													YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount)),2);
									dPortMoney = YssD.round(
											YssD.mul(
													YssD.div(yesDateKCValueBalance[2],dYesDateAmount),
													YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount)),2);
									realPCBean =new FuturesHedgRelaBean();
									realPCBean.setSSetNum(realNewBean.getSNum());
									//套期保值交易关联表bean赋值出错
									setHedgRelaBeanData(realPCBean,rs,sPortCode,date,dMoney,dBaseMoney,dPortMoney,"19"+rs.getString("fcatcode"));
									realPCBean.setDTradeAmount(YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount));
									alRealData.add(realPCBean);
									//当天有多笔平仓时，要把平仓的数据累加保存起来，为了方便计算
									if(mapPCAddValue.containsKey(sKey)){
										realPCAddValueBean = (FuturesHedgRelaBean) mapPCAddValue.get(sKey);
										realPCAddValueBean.setDMoney(YssD.add(realPCAddValueBean.getDMoney(),dMoney));
										realPCAddValueBean.setDBaseCuryMoeny(YssD.add(realPCAddValueBean.getDBaseCuryMoeny(),dBaseMoney));
										realPCAddValueBean.setDPortCuryMoeny(YssD.add(realPCAddValueBean.getDPortCuryMoeny(),dPortMoney));
									}else{
										realPCAddValueBean = new FuturesHedgRelaBean();
										realBean.setSPortCode(sPortCode);//组合代码
										realBean.setSSecurityCode(rs.getString("FSecurityCode"));//证券代码
										realPCAddValueBean.setDMoney(dMoney);
										realPCAddValueBean.setDBaseCuryMoeny(dBaseMoney);
										realPCAddValueBean.setDPortCuryMoeny(dPortMoney);
										mapPCAddValue.put(sKey,realPCAddValueBean);
									}
									//平仓完后要重新计算该笔开仓的数据
									realNewBean.setSTradeDate(YssFun.formatDate(date,"yyyy-MM-dd"));
									//先进先出平仓后剩余成本 = 原库存成本 - 原库存成本/原库存数量 * 平仓数量
									realNewBean.setDMoney(YssD.round(
											YssD.sub(realBean.getDMoney(),
													YssD.mul(
															YssD.div(realBean.getDMoney(),realBean.getDTradeAmount()),
															YssD.sub(rs.getDouble("ftradeamount"),thDayTradeAmount))),2));
									realNewBean.setDTradeAmount(YssD.sub(sTotalKCAmount,rs.getDouble("ftradeamount")));
									realNewBean.setDBaseCuryMoeny(YssD.round(YssD.mul(realNewBean.getDMoney(),rs.getDouble("FBaseCuryRate")),2));
									realNewBean.setDPortCuryMoeny(YssD.round(
											YssD.div(
													YssD.mul(realNewBean.getDMoney(),rs.getDouble("FBaseCuryRate")),
													rs.getDouble("FPortCuryRate")),
													2));
									//-------------------------end-------------------------//
									break;
								}
							}//end fof j
							break;
						}
					}//end for i
					sTotalKCAmount =0;
					thDayTradeAmount =0;
					PCCost= 0;
				}else{
					throw new YssException("库存不足,无法进行先入先出平仓");
				}
			}//end while
			
		}catch (Exception e) {
			throw new YssException("先入先出法处理今天平仓交易数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 获取昨日库存数量
	 * @param num
	 * @param portCode
	 * @param tradeDate
	 * @param securityCode
	 * @return
	 * @throws YssException 
	 */
	private double getYesDateAmount(String num, String portCode, String tradeDate, String securityCode) throws YssException {
		double YesDateAmount = 0;//保存昨日库存数量
		StringBuffer buff = null;
		ResultSet rs = null;
		try{
			buff = new StringBuffer(500);
			buff.append(" select * from ").append(pub.yssGetTableName("tb_stock_hedgsecurity"));
			buff.append(" where FStroageDate = ").append(dbl.sqlDate(YssFun.addDay(YssFun.toDate(tradeDate),-1)));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(portCode)).append(")");
			buff.append(" and FNumorsec = ").append(dbl.sqlString(num));
			buff.append(" and FSecurityCode =").append(dbl.sqlString(securityCode));
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			if(rs.next()){
				YesDateAmount = rs.getDouble("FStroageAmount");
			}
			
		}catch (Exception e) {
			throw new YssException("获取昨日库存数量出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return YesDateAmount;
	}
	/**
	 * 从期货套期保值应收应付库存表中获取昨日每笔开仓交易的估值增值余额
	 * 
	 * @param num
	 * @return
	 * @throws YssException
	 */
	private double[] getYesDateKCValueBalance(String num,String sPortCode,String sDate,String sSecurityCode) throws YssException {
		double [] yesDateKCValueBalance = new double[3];//保存昨日估值增值
		StringBuffer buff = null;
		ResultSet rs = null;
		try{
			buff = new StringBuffer(500);
			buff.append(" select * from ").append(pub.yssGetTableName("Tb_Stock_HedgRecpay"));
			buff.append(" where FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(YssFun.toDate(sDate),-1)));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(sPortCode)).append(")");
			buff.append(" and FNumorsec = ").append(dbl.sqlString(num));
			buff.append(" and FSecurityCode =").append(dbl.sqlString(sSecurityCode));
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			if(rs.next()){
				yesDateKCValueBalance[0] = rs.getDouble("FBal");//原币估值增值
				yesDateKCValueBalance[1] = rs.getDouble("FBaseCuryBal");//基础货币估值增值
				yesDateKCValueBalance[2] = rs.getDouble("FPortCuryBal");//组合货币估值增值
			}
			
		}catch (Exception e) {
			throw new YssException("从期货套期保值应收应付库存表中获取昨日每笔开仓交易的估值增值余额出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return yesDateKCValueBalance;
	}
	/**
	 * 先入先出法获取昨日和今日开仓交易数据
	 * @return
	 */
	private HashMap getFuturesKCTradeFIFO(Date date,String sPortCode) throws YssException{
		HashMap FuturesKCTrade = null;//保存昨日剩余开仓数量和今日开仓数量的hash表
		StringBuffer buff = null;
		ResultSet rs = null;
		ArrayList alFuturesKCTrade = null;//保存昨日剩余开仓数量和今日开仓数量的集合
		String sKey = "";
		FuturesHedgRelaBean realBean = null;//期货套期保值交易关联表实体bean
		try{
			FuturesKCTrade = new HashMap();
			buff = new StringBuffer();
			
			//从期货被套证券库存表中获取昨日没有被平仓完的开仓数据和从期货套期证券关联表关联期货套期保值数据表关联期货交易数据表中获取当天的交易数据
			buff.append(" select * from (select a.fnumorsec as FNum,a.fstroagedate as FBargaindate,a.fsecuritycode,a.fportcode,");
			buff.append(" a.fhedgingtype,a.fstroageamount as ftradeamount,a.fcurycost,a.fbasecurycost,a.fportcurycost,b.fcatcode,a.fbasecuryrate,a.fportcuryrate from ");
			buff.append(pub.yssGetTableName("TB_Stock_HedgSecurity"));
			buff.append(" a join (select * from ").append(pub.yssGetTableName("tb_para_security"));
			buff.append(" where FCheckState = 1) b on a.fsecuritycode = b.fsecuritycode");
			buff.append(" where a.fstroagedate = ").append(dbl.sqlDate(YssFun.addDay(date,-1)));
			buff.append(" and a.fportcode =").append(dbl.sqlString(sPortCode));
			buff.append(" union all ");
			buff.append(" select b.fnum,d.FBargaindate,b.fsecuritycode,d.FPortCode,c.fhedgingtype,b.ftradeamount,b.ftrademoney as fcurycost,");
			buff.append(" round(b.ftrademoney * d.fbasecuryRate, 2) as fbasecurycost,");
			buff.append(" round(b.ftrademoney * d.fbasecuryRate / d.fportcuryRate, 2) as fportcurycost,f.fcatcode,b.fbasecuryRate,b.fportcuryRate from ");
			buff.append(pub.yssGetTableName("TB_Data_FutHedgSecurity"));
			buff.append(" b join (select * from ").append(pub.yssGetTableName("Tb_Data_FuturesHedging"));
			buff.append(" ) c on b.fnum = c.fnum");
			//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_data_futurestrade_Tmp"));
			buff.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(sPortCode));
			buff.append(" and FBargaindate = ").append(dbl.sqlDate(date));
			buff.append(" and FTradeTypeCode = '20') d on b.fnum = d.FNum");
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_para_security"));
			buff.append(" where FCheckState = 1) f on b.fsecuritycode = f.fsecuritycode");
			buff.append(") e order by e.FBargaindate,e.fsecuritycode,e.fnum");
			
			rs = dbl.openResultSet(buff.toString());
			while(rs.next()){
				alFuturesKCTrade = new ArrayList();
				sKey = rs.getString("FSecurityCode")+"\t" + rs.getString("FPortCode");
				realBean = new FuturesHedgRelaBean();
				realBean.setSSetNum(rs.getString("FNum"));
				//套期保值交易关联表bean赋值出错
				setHedgRelaBeanData(realBean,rs,sPortCode,date,rs.getDouble("fcurycost"),rs.getDouble("fbasecurycost"),rs.getDouble("fportcurycost"),"05"+rs.getString("fcatcode"));
				if(YssFun.dateDiff(rs.getDate("FBargaindate"),date) == 0){
					alRealData.add(realBean);
				}
				//把相同的开仓放到一个集合中
				if(FuturesKCTrade.containsKey(sKey)){
					alFuturesKCTrade = (ArrayList) FuturesKCTrade.get(sKey);
					alFuturesKCTrade.add(realBean);
				}else{
					alFuturesKCTrade.add(realBean);
					FuturesKCTrade.put(sKey,alFuturesKCTrade);
				}
			}
			
		}catch (Exception e) {
			throw new YssException("先入先出法获取期货昨日和今日开仓交易数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return FuturesKCTrade;
	}
	/**
	 * 移动加权法处理当天期货交易的成本和估值增值存入"套期保值交易关联表（Tb_XXX_Data_HedgRela）"
	 * 成本 = 调拨类型为05+证券品种类型，估值增值=调拨类型为19+证券品种类型。
	 * @param date
	 * @throws YssException 
	 */
	private void doTheDayFuturesTradeHedgingMODAVG(Date date,String sPortCode) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
		FuturesHedgRelaBean realBean = null;//期货套期保值交易关联表实体bean
		FuturesHedgRelaBean realKCBean = null;//期货套期保值交易关联表实体bean
		double dcost =0;
		double dBaseCost= 0;
		double dPortCost =0;
		HashMap mapTodayKCTrade = new HashMap();//保存当天开仓交易数据
		HashMap mapTodayPCCost = new HashMap();//保存今日平仓成本数据
		HashMap mapTodayPCAddValue = new HashMap();//保存今日平仓估值增值数据
		String sKey = "";
		double yesDStorgeAmount = 0; //昨日的库存数量
		double dPCTradeAmount =0;//保存每一笔平仓数量
		double dPCAddValueAmount =0;//保存平仓数量
		FuturesHedgRelaBean realPCBean = null;
		try{
			buff = new StringBuffer();
			
			rs = dbl.openResultSet(getTradeDateSQL(buff,sPortCode,date));//获取当天期货交易数据的SQL语句
			buff.delete(0,buff.length());
			while(rs.next()){
				realBean = new FuturesHedgRelaBean();
				sKey = rs.getString("FSecurityCode") + "\f" + sPortCode +"\f" + YssOperCons.YSS_ZJDBLX_Cost;
				yesDStorgeAmount = rs.getDouble("FStroageAmount");//昨日库存
				if(rs.getString("FTradeTypeCode").equalsIgnoreCase("20")){//开仓
					dcost = rs.getDouble("FTradeMoney");
					dBaseCost = YssD.round(YssD.mul(dcost,rs.getDouble("FBaseCuryRate")),2);
					dPortCost = YssD.round(YssD.div(YssD.mul(dcost,rs.getDouble("FBaseCuryRate")),rs.getDouble("FPortCuryRate")),2);
					
					//套期保值交易关联表bean赋值出错
					setHedgRelaBeanData(realBean,rs,sPortCode,date,dcost,dBaseCost,dPortCost,YssOperCons.YSS_ZJDBLX_Cost+rs.getString("fcatcode"));
					alRealData.add(realBean);//保存到集合中
					if(mapTodayKCTrade.containsKey(sKey)){//保存每一笔开仓交易，并把数量，成本累加
						realKCBean = (FuturesHedgRelaBean) mapTodayKCTrade.get(sKey);
						realKCBean.setDMoney(YssD.add(realKCBean.getDMoney(),dcost));
						realKCBean.setDBaseCuryMoeny(YssD.add(realKCBean.getDBaseCuryMoeny(),dBaseCost));
						realKCBean.setDPortCuryMoeny(YssD.add(realKCBean.getDPortCuryMoeny(),dPortCost));
						realKCBean.setDTradeAmount(YssD.add(realKCBean.getDTradeAmount(),rs.getDouble("FTradeAmount")));
					}else{
						mapTodayKCTrade.put(sKey,realBean);
					}
				}else{
					if(mapTodayKCTrade.containsKey(sKey)){//获取当天开仓交易
						realKCBean = (FuturesHedgRelaBean) mapTodayKCTrade.get(sKey);
						if(YssD.add(yesDStorgeAmount,(realKCBean == null ? 0 : realKCBean.getDTradeAmount())) == rs.getDouble("FTradeAmount")){
							dcost = YssD.add(rs.getDouble("FCuryCost"),realKCBean.getDMoney());
							dBaseCost = YssD.add(rs.getDouble("FBaseCuryCost"),realKCBean.getDBaseCuryMoeny());
							dPortCost = YssD.add(rs.getDouble("FPortCuryCost"),realKCBean.getDPortCuryMoeny());
						}else{
							if(mapTodayPCCost.containsKey(sKey)){
								realPCBean = (FuturesHedgRelaBean) mapTodayPCCost.get(sKey);
							}
							//----------------------成本数据-------------------------//
							//成本=（昨日成本 + 当天开仓交易成本-当天平仓交易成本）/（昨日库存+当天开仓数量-当天平仓交易数量）*卖出数量，考虑当天的交易数量，
		                    dcost = YssD.round(
		                    		YssD.mul(rs.getDouble("FTradeAmount"), 
		                    				YssD.div(
		                    						YssD.sub(
		                    								YssD.add(rs.getDouble("FCuryCost"),realKCBean!=null ? realKCBean.getDMoney():0),
		                    								realPCBean != null?realPCBean.getDMoney():0), 
		                    						YssD.sub(
		                    								YssD.add(yesDStorgeAmount,realKCBean!=null ? realKCBean.getDTradeAmount():0),
		                    								realPCBean !=null?realPCBean.getDTradeAmount():0))),
		                    						2);
		                    dBaseCost = YssD.round(
		                    		YssD.mul(rs.getDouble("FTradeAmount"), 
		                    				YssD.div(
		                    						YssD.sub(
		                    								YssD.add(rs.getDouble("FBaseCuryCost"),realKCBean!=null ? realKCBean.getDBaseCuryMoeny():0),
		                    								realPCBean != null?realPCBean.getDBaseCuryMoeny():0), 
		                    						YssD.sub(
		                    								YssD.add(yesDStorgeAmount,realKCBean!=null ? realKCBean.getDTradeAmount():0),
		                    								realPCBean !=null?realPCBean.getDTradeAmount():0))),
		                    						2);
		                    dPortCost = YssD.round(
		                    		YssD.mul(rs.getDouble("FTradeAmount"), 
		                    				YssD.div(
		                    						YssD.sub(YssD.add(rs.getDouble("FPortCuryCost"),realKCBean!=null ? realKCBean.getDPortCuryMoeny():0),
		                    								realPCBean != null?realPCBean.getDPortCuryMoeny():0)
		                    						, YssD.sub(YssD.add(yesDStorgeAmount,realKCBean!=null ? realKCBean.getDTradeAmount():0),
		                    								realPCBean !=null?realPCBean.getDTradeAmount():0))),
		                    						2);
						}
					}else{
						if(mapTodayPCCost.containsKey(sKey)){
							dPCTradeAmount += rs.getDouble("FTradeAmount");
							realPCBean = (FuturesHedgRelaBean) mapTodayPCCost.get(sKey);
							//if(dPCTradeAmount == yesDStorgeAmount){//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
							if(YssD.add(dPCTradeAmount, -yesDStorgeAmount)==0){
								dcost = YssD.sub(rs.getDouble("FCuryCost"),realPCBean.getDMoney());
								dBaseCost = YssD.sub(rs.getDouble("FBaseCuryCost"),realPCBean.getDBaseCuryMoeny());
								dPortCost = YssD.sub(rs.getDouble("FPortCuryCost"),realPCBean.getDPortCuryMoeny());
								dPCTradeAmount =0;
							}else{
								//----------------------成本数据-------------------------//
								//成本=round(round((昨日成本 + 当天开仓交易成本 - 当天平仓交易成本）/（昨日库存+当天开仓数量 - 当天平仓交易数量）,2)*卖出数量,2)，考虑当天的交易数量，
			                    dcost = YssD.round(
			                    		YssD.mul(rs.getDouble("FTradeAmount"), 
			                    				YssD.round(YssD.div(
			                    						YssD.sub(
			                    								YssD.add(rs.getDouble("FCuryCost"),realKCBean!=null ? realKCBean.getDMoney():0),
			                    								realPCBean.getDMoney()), 
			                    						YssD.sub(
			                    								YssD.add(yesDStorgeAmount,realKCBean!=null ? realKCBean.getDTradeAmount():0),
			                    								realPCBean.getDTradeAmount())),2)),
			                    						2);
			                    dBaseCost = YssD.round(
			                    		YssD.mul(rs.getDouble("FTradeAmount"), 
			                    				YssD.round(YssD.div(
			                    						YssD.sub(
			                    								YssD.add(rs.getDouble("FBaseCuryCost"),realKCBean!=null ? realKCBean.getDBaseCuryMoeny():0),
			                    								realPCBean.getDBaseCuryMoeny()), 
			                    						YssD.sub(YssD.add(yesDStorgeAmount,realKCBean!=null ? realKCBean.getDTradeAmount():0),
			                    								realPCBean.getDTradeAmount())),2)),
			                    						2);
			                    dPortCost = YssD.round(
			                    		YssD.mul(rs.getDouble("FTradeAmount"), 
			                    				YssD.round(YssD.div(
			                    						YssD.sub(
			                    								YssD.add(rs.getDouble("FPortCuryCost"),realKCBean!=null ? realKCBean.getDPortCuryMoeny():0),
			                    								realPCBean.getDPortCuryMoeny())
			                    						, YssD.sub(
			                    								YssD.add(yesDStorgeAmount,realKCBean!=null ? realKCBean.getDTradeAmount():0),
			                    								realPCBean.getDTradeAmount())),2)),2);
							}
						}else{
							if(!mapTodayPCCost.containsKey(sKey)){
								if(dPCTradeAmount ==0){
									dPCTradeAmount += rs.getDouble("FTradeAmount");
								}else{
									dPCTradeAmount =0;
									dPCTradeAmount += rs.getDouble("FTradeAmount");
								}								
							}
							//----------------------成本数据-------------------------//
							//成本=round(（昨日成本 + 当天开仓交易成本）/（昨日库存+当天开仓数量）*卖出数量,2)，考虑当天的交易数量，
		                    dcost = YssD.round(
		                    		YssD.mul(rs.getDouble("FTradeAmount"), 
		                    				YssD.div(
		                    						YssD.add(rs.getDouble("FCuryCost"),realKCBean!=null ? realKCBean.getDMoney():0), 
		                    						YssD.add(yesDStorgeAmount,realKCBean!=null ? realKCBean.getDTradeAmount():0))),
		                    						2);
		                    dBaseCost = YssD.round(
		                    		YssD.mul(rs.getDouble("FTradeAmount"), 
		                    				YssD.div(YssD.add(rs.getDouble("FBaseCuryCost"),realKCBean!=null ? realKCBean.getDBaseCuryMoeny():0), 
		                    						YssD.add(yesDStorgeAmount,realKCBean!=null ? realKCBean.getDTradeAmount():0))),
		                    						2);
		                    dPortCost = YssD.round(
		                    		YssD.mul(rs.getDouble("FTradeAmount"), 
		                    				YssD.div(YssD.add(rs.getDouble("FPortCuryCost"),realKCBean!=null ? realKCBean.getDPortCuryMoeny():0)
		                    						, YssD.add(yesDStorgeAmount,realKCBean!=null ? realKCBean.getDTradeAmount():0))),2);
						}
					}
					//套期保值交易关联表bean赋值出错
					setHedgRelaBeanData(realBean,rs,sPortCode,date,dcost,dBaseCost,dPortCost,YssOperCons.YSS_ZJDBLX_Cost+rs.getString("fcatcode"));
					alRealData.add(realBean);
					if(mapTodayPCCost.containsKey(sKey)){//把每一笔平仓的数量，成本累加
						realPCBean = (FuturesHedgRelaBean) mapTodayPCCost.get(sKey);
						realPCBean.setDMoney(YssD.add(realPCBean.getDMoney(),dcost));
						realPCBean.setDBaseCuryMoeny(YssD.add(realPCBean.getDBaseCuryMoeny(),dBaseCost));
						realPCBean.setDPortCuryMoeny(YssD.add(realPCBean.getDPortCuryMoeny(),dPortCost));
						realPCBean.setDTradeAmount(YssD.add(realPCBean.getDTradeAmount(),rs.getDouble("FTradeAmount")));
					}else{
						realBean = new FuturesHedgRelaBean();
						setHedgRelaBeanData(realBean,rs,sPortCode,date,dcost,dBaseCost,dPortCost,YssOperCons.YSS_ZJDBLX_Cost+rs.getString("fcatcode"));
						mapTodayPCCost.put(sKey,realBean);
					}
					
					if(mapTodayKCTrade.containsKey(sKey)){//对于多笔平仓，每平一次，要把开仓交易的数量，成本流出平仓交易的
						realKCBean = (FuturesHedgRelaBean) mapTodayKCTrade.get(sKey);
						realKCBean.setDMoney(YssD.sub(realKCBean.getDMoney(),dcost));
						realKCBean.setDBaseCuryMoeny(YssD.sub(realKCBean.getDBaseCuryMoeny(),dBaseCost));
						realKCBean.setDPortCuryMoeny(YssD.sub(realKCBean.getDPortCuryMoeny(),dPortCost));
						realKCBean.setDTradeAmount(YssD.sub(realKCBean.getDTradeAmount(),rs.getDouble("FTradeAmount")));
					}
					//----------------------end----------------------------//
					//----------------------估值增值数据----------------------//
					sKey = rs.getString("FSecurityCode") + "\f" + sPortCode + "19";
                    if(rs.getDouble("FTradeAmount") > yesDStorgeAmount){
                        //原币移动加权计算估值增值=昨日估值增值余额
                    	dcost = rs.getDouble("FBal");
                        //基础货币移动加权计算估值增值=昨日基础货币估值增值
                    	dBaseCost = rs.getDouble("FBaseCuryBal");
                        //组合货币移动加权计算估值增值=昨日组合货币估值增值
                    	dPortCost = rs.getDouble("FPortCuryBal");
                    }else{
                    	if(mapTodayPCAddValue.containsKey(sKey)){
                    		dPCAddValueAmount += rs.getDouble("FTradeAmount");
                    		realPCBean = (FuturesHedgRelaBean) mapTodayPCAddValue.get(sKey);
                    		//if(dPCTradeAmount == yesDStorgeAmount){//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                    		if(YssD.add(dPCTradeAmount, -yesDStorgeAmount) == 0){//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
								dcost = YssD.sub(rs.getDouble("FBal"),realPCBean.getDMoney());
								dBaseCost = YssD.sub(rs.getDouble("FBaseCuryBal"),realPCBean.getDBaseCuryMoeny());
								dPortCost = YssD.sub(rs.getDouble("FPortCuryBal"),realPCBean.getDPortCuryMoeny());
								dPCAddValueAmount =0;
                    		}else{
                                //原币移动加权计算估值增值=round(round((昨日估值增值余额 - 当天平仓估值增值）/（昨日库存-当天平仓数量）,2)*卖出数量,2)
                            	dcost = YssD.round(
                            					YssD.mul(rs.getDouble("FTradeAmount"), 
                            						YssD.round(YssD.div(
                            							YssD.sub(rs.getDouble("FBal"),realPCBean.getDMoney()), 
                            							YssD.sub(yesDStorgeAmount,realPCBean.getDTradeAmount())),2)),
                            							2);
                                //基础货币移动加权计算估值增值=round(round(（昨日基础货币估值增值- 当天平仓估值增值）/（昨日库存-当天平仓数量）,2)*卖出数量,2)
                                dBaseCost = YssD.round(
                                					YssD.mul(rs.getDouble("FTradeAmount"), 
                                						YssD.round(YssD.div(
                                							YssD.sub(rs.getDouble("FBaseCuryBal"),realPCBean.getDBaseCuryMoeny()), 
                                							YssD.sub(yesDStorgeAmount,realPCBean.getDTradeAmount())),2)),
                                							2);
                                //组合货币移动加权计算估值增值=round(round(（昨日组合货币估值增值- 当天平仓估值增值）/（昨日库存-当天平仓数量）,2)*卖出数量,2)
                                dPortCost = YssD.round(
                                					YssD.mul(rs.getDouble("FTradeAmount"), 
                                						YssD.round(YssD.div(
                                							YssD.sub(rs.getDouble("FPortCuryBal"),realPCBean.getDPortCuryMoeny()), 
                                							YssD.sub(yesDStorgeAmount,realPCBean.getDTradeAmount())),2)),
                                							2);
                    		}
                    	}else{
                    		if(!mapTodayPCAddValue.containsKey(sKey)){
                    			if(dPCAddValueAmount ==0){
                    				dPCAddValueAmount += rs.getDouble("FTradeAmount");
								}else{
									dPCAddValueAmount =0;
									dPCAddValueAmount += rs.getDouble("FTradeAmount");
								}	
                    		}
                            //原币移动加权计算估值增值=昨日估值增值余额/昨日库存*卖出数量
                        	dcost = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rs.getDouble("FBal"), yesDStorgeAmount)),2);
                            //基础货币移动加权计算估值增值=昨日基础货币估值增值/昨日库存*卖出数量
                            dBaseCost = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rs.getDouble("FBaseCuryBal"), yesDStorgeAmount)),2);
                            //组合货币移动加权计算估值增值=昨日组合货币估值增值/昨日库存*卖出数量
                            dPortCost = YssD.round(YssD.mul(rs.getDouble("FTradeAmount"), YssD.div(rs.getDouble("FPortCuryBal"), yesDStorgeAmount)),2);
                    	}
                    }
                    realBean = new FuturesHedgRelaBean();
					//套期保值交易关联表bean赋值出错
					setHedgRelaBeanData(realBean,rs,sPortCode,date,dcost,dBaseCost,dPortCost,"19"+rs.getString("fcatcode"));
					alRealData.add(realBean);
					if(mapTodayPCAddValue.containsKey(sKey)){//对于多笔平仓交易，要把平仓交易的估值增值累加保存
						realPCBean = (FuturesHedgRelaBean) mapTodayPCAddValue.get(sKey);
						realPCBean.setDMoney(YssD.add(realPCBean.getDMoney(),dcost));
						realPCBean.setDBaseCuryMoeny(YssD.add(realPCBean.getDBaseCuryMoeny(),dBaseCost));
						realPCBean.setDPortCuryMoeny(YssD.add(realPCBean.getDPortCuryMoeny(),dPortCost));
						realPCBean.setDTradeAmount(YssD.add(realPCBean.getDTradeAmount(),rs.getDouble("FTradeAmount")));
					}else{
						realBean = new FuturesHedgRelaBean();
						//套期保值交易关联表bean赋值出错
						setHedgRelaBeanData(realBean,rs,sPortCode,date,dcost,dBaseCost,dPortCost,"19"+rs.getString("fcatcode"));
						mapTodayPCAddValue.put(sKey,realBean);
					}
					//-----------------------end----------------------------//
				}
			}//end while
		}catch (Exception e) {
			throw new YssException("移动加权法处理当天期货交易的成本和估值增值存入套期保值交易关联表出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 套期保值交易关联表bean赋值出错
	 * @throws YssException
	 */
	private void setHedgRelaBeanData(FuturesHedgRelaBean realBean,ResultSet rs,String sPortCode,Date date,
			double dcost,double dBaseCost,double dPortCost,String sTsfTypeCode)throws YssException{
		try{
			realBean.setSNum(rs.getString("FNum"));//成交编号
			realBean.setSPortCode(sPortCode);//组合代码
			realBean.setSSecurityCode(rs.getString("FSecurityCode"));//证券代码
			realBean.setSTradeDate(YssFun.formatDate(date,"yyyy-MM-dd"));//交易日期
			realBean.setSTsfTypeCode(sTsfTypeCode);//调拨类型
			realBean.setSHedgingType(rs.getString("FHedgingType"));//套期类型
			realBean.setDMoney(dcost);//原币金额
			realBean.setDBaseCuryMoeny(dBaseCost);//基础货币金额
			realBean.setDPortCuryMoeny(dPortCost);//组合货币金额
			realBean.setDBaseCuryRate(rs.getDouble("FBaseCuryRate"));//基础汇率
			realBean.setDPortCuryRate(rs.getDouble("FPortCuryRate"));//组合汇率
			realBean.setDTradeAmount(rs.getDouble("FTradeAmount"));//交易数量
			
		}catch (Exception e) {
			throw new YssException("赋值出错！",e);
		}
	}
	
	/**
	 * 获取当天期货交易数据的SQL语句
	 * @param sAccountType
	 * @return
	 * @throws YssException
	 */
	private String getTradeDateSQL(StringBuffer buff,String sPortCode,Date date)throws YssException{
		try{
			if(buff.length() != 0){
				buff.delete(0,buff.length());
			}
			buff.append(" select a.*,b.FTradeTypeCode,b.FBaseCuryRate,b.FPortCuryRate,c.FHedgingType,d.FStroageAmount,d.FCuryCost,d.FBaseCuryCost,d.FPortCuryCost,");
			buff.append(" e.FBal,e.FBaseCuryBal,e.FPortCuryBal,f.fcatcode from ");
			buff.append(pub.yssGetTableName("TB_Data_FutHedgSecurity"));//期货套期证券关联表
			buff.append(" a join (select * from ").append(pub.yssGetTableName("tb_para_security"));//证券信息设置表
			buff.append(" where FCHeckState = 1) f on a.fsecuritycode = f.fsecuritycode");
			buff.append(" join (select * from ").append(pub.yssGetTableName("Tb_Data_FuturesHedging"));//期货套期保值数据表
			buff.append(" ) c on a.FNum = c.FNum");
			//edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_data_futurestrade_Tmp"));//期货交易数据表
			buff.append(" where FPortCode =").append(dbl.sqlString(sPortCode));
			buff.append(" and FBargaindate = ").append(dbl.sqlDate(date));
			buff.append(" ) b on a.FNum = b.Fnum ");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("TB_Stock_HedgSecurity"));//期货被套证券库存表
			buff.append(" where FStroageDate =").append(dbl.sqlDate(YssFun.addDay(date,-1)));
			buff.append(" and FPortCode  =").append(dbl.sqlString(sPortCode));
			buff.append(" ) d on a.FSecurityCode = d.FSecurityCode and c.fsecuritycode =d.FNumOrSec");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("Tb_Stock_HedgRecpay"));//期货被套证券应收应付库存表
			buff.append(" where FStorageDate =").append(dbl.sqlDate(YssFun.addDay(date,-1)));
			buff.append(" and FPortCode =").append(dbl.sqlString(sPortCode));
			buff.append(" ) e on a.fsecuritycode = e.FSecurityCode and c.fsecuritycode = e.FNumOrSec");
			buff.append(" order by b.FTradeTypeCode,a.fsecuritycode,a.FNum ");
		}catch (Exception e) {
			throw new YssException("获取当天期货交易数据的SQL语句出错！",e);
		}
		return buff.toString();
	}
	
	/**
	 * 使用组合代码获取期货核算类型 
	 * 
	 * @param sPortCode String：组合代码
	 * @return String：核算类型
	 * @throws YssException
	 */
	private String getAccountTypeBy(String sPortCode) throws YssException {
		// 默认使用先入先出
		String sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO;
		String sTheDayFirstFIFO = "";
		String sModAvg = "";
		// 存放组合、核算代码对
		Hashtable htPortAccountType = new Hashtable();
		try {
			CtlPubPara pubPara = new CtlPubPara();
			pubPara.setYssPub(pub);
			htPortAccountType = pubPara.getFurAccountType("AccoutType");
			
			sTheDayFirstFIFO = (String) htPortAccountType
					.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO);
			sModAvg = (String) htPortAccountType
					.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG);
			if (sTheDayFirstFIFO != null
					&& sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
				sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO;
			} else if (sModAvg != null && sModAvg.indexOf(sPortCode) != -1) {
				sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG;
			}
		} catch (Exception ex) {
			throw new YssException(ex.getMessage());
		}
		return sAccountType;
	}
}




















