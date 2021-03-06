package com.yss.main.operdeal.opermanage.etf;

import java.util.Date;
import java.util.HashMap;
import java.sql.ResultSet;

import com.yss.commeach.EachRateOper;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.manager.CashPayRecAdmin;
import com.yss.util.*;

/**
 * 生成现金替代和现金差额的应收应付
 * MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A 
 * @author panjunfang
 * create 20091022
 */

public class CashInsBalRPManage extends BaseOperManage {

	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.sPortCode = portCode;
		this.dDate = dDate;
	}

	public void doOpertion() throws YssException {
		ETFParamSetAdmin etfParamAdmin = new ETFParamSetAdmin();
		etfParamAdmin.setYssPub(pub);
		ETFParamSetBean etfParamBean = new ETFParamSetBean();
		HashMap hm = new HashMap();
		hm = etfParamAdmin.getETFParamInfo(sPortCode);
		etfParamBean = (ETFParamSetBean)hm.get(sPortCode);
		if(etfParamBean == null){
			throw new YssException("组合【" + sPortCode + "】对应的ETF参数设置不存在或未审核！");
		}
		createCashInsBal(etfParamBean);//生成现金差额应收应付（申购、赎回）
		createReplaceCashPurchase(etfParamBean);//生成现金替代款（可以现金替代和必须现金替代）-ETF申购
		createRedeemMustReplaceCash(etfParamBean);//生成应付赎回款-ETF赎回-必须现金替代款
		createRedeemMayReplaceCash(etfParamBean);//生成应付赎回款-ETF赎回-可以现金替代款		
	}

	/**
	 * 生成现金替代款（可以现金替代和必须现金替代）-ETF申购
	 */
	private void createReplaceCashPurchase(ETFParamSetBean paraSet) throws YssException {		
		StringBuffer sb = new StringBuffer(200);
		ResultSet rs = null;
		CashPecPayBean cashPecPay1 = null;//存放可以现金替代
		CashPecPayBean cashPecPay2 = null;//存放必须现金替代
		CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);
        try{
        	sb.append("select x.*,y.FClearAccCode,y.FNormScale,z.FMustMoney from ")
        	  .append(pub.yssGetTableName("Tb_TA_Trade"))//从TA交易数据中获取申购替代金额总额及结转日期
        	  .append(" x left join (select FPortCode,FClearAccCode,FNormScale from ")
        	  .append(pub.yssGetTableName("Tb_ETF_Param"))//从ETF参数设置中获取备付金结转账户和最小申赎份额
        	  .append(" ) y on y.FPortCode = x.FPortCode ")
        	  .append(" left join (select FPortCode,FDate,sum(FTotalMoney) as FMustMoney from ")
        	  .append(pub.yssGetTableName("Tb_ETF_StockList"))//从股票篮中获取单位篮子必须现金替代金额
        	  .append(" where FCheckState = 1 and FReplaceMark in ('2','6') ")//必须现金替代标识（深交所：2   上交所：6）
        	  .append(" and fsecuritycode <> ").append(dbl.sqlString(paraSet.getCapitalCode()))//去掉虚拟成份股 arealw 20111126 增加
        	  .append(" group by FPortCode,FDate) z on z.FPortCode = x.fportcode and z.FDate = x.ftradedate")
        	  .append(" where x.FCONFIMDATE = ").append(dbl.sqlDate(dDate))
        	  .append(" and x.FPortCode = ").append(dbl.sqlString(sPortCode))
        	  .append(" and x.FSellType in ('01') and x.FCheckState = 1");
        	
        	rs = dbl.queryByPreparedStatement(sb.toString());
        	while(rs.next()){
        		cashPecPay1 = new CashPecPayBean();        		
        		//可以现金替代款金额 = 申购总替代金额 - 必须现金替代金额
        		cashPecPay1.setMoney(YssD.sub(rs.getDouble("FCashRepAmount"), 
        										YssD.mul(rs.getDouble("FMustMoney"),
        												YssD.div(rs.getDouble("FSellAmount"),rs.getDouble("FNormScale")))));
        		cashPecPay1.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);//业务类型
        		cashPecPay1.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPBuy);//业务子类型
    			cashPecPay1.setCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
															rs.getString("FCashAccCode") : rs.getString("FClearAccCode"));//现金账户
        		setCashRecPayProperty(rs,cashPecPay1);
        		cashPayRecAdmin.addList(cashPecPay1);
        		
        		cashPecPay2 = new CashPecPayBean();
        		//必须现金替代金额 = 单位篮子必须现金替代金额 × 篮子数
        		cashPecPay2.setMoney(YssD.mul(rs.getDouble("FMustMoney"),
        												YssD.div(rs.getDouble("FSellAmount"),rs.getDouble("FNormScale"))));
        		cashPecPay2.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);//业务类型
        		cashPecPay2.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashMustInsRPBuy);//业务子类型
        		cashPecPay2.setCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
														rs.getString("FCashAccCode") : rs.getString("FClearAccCode"));//现金账户
        		setCashRecPayProperty(rs,cashPecPay2);
        		cashPayRecAdmin.addList(cashPecPay2);
        	}
        	cashPayRecAdmin.insert(dDate, dDate,
        							YssOperCons.YSS_ZJDBLX_Rec, 
        							YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPBuy + "," + YssOperCons.YSS_ZJDBZLX_ETF_CashMustInsRPBuy,
        							"", sPortCode, "", "", "", 0);
		}catch(Exception e){
			throw new YssException("生成现金替代款（ETF申购可以现金替代和必须现金替代）出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
	 * 生成现金差额应收应付（申购、赎回）
	 * @throws YssException
	 */
	private void createCashInsBal(ETFParamSetBean paraSet) throws YssException {
		String strSql = "";
		ResultSet rs = null;
        CashPecPayBean cashPecPay = null;
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);

		try{
			strSql = "select x.*,y.FClearAccCode from " + pub.yssGetTableName("Tb_TA_Trade") + 
					" x left join (select * from " + pub.yssGetTableName("Tb_ETF_Param") + 
					" ) y on y.FPortCode = x.FPortCode " + 
					" where FCONFIMDATE = " + dbl.sqlDate(dDate) + 
					//易方达申赎在T+1日确认，此处改为取确认日的数据
					//panjunfang modify 20110810 STORY #1434 QDV4易方达基金2011年7月27日01_A
					" and x.FPortCode = " + dbl.sqlString(sPortCode) + 
					" and FSellType in ('01','02') and x.FCheckState = 1";
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next()){
				cashPecPay = new CashPecPayBean();
				cashPecPay.setMoney(rs.getDouble("FCashBal"));//现金差额金额
				if(rs.getString("FSellType").equals("01")){//申购
					cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Rec);//业务类型
					cashPecPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPBuy);//业务子类型
				}else{//赎回
					cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);//业务类型			
					cashPecPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPSell);//业务子类型
				}
        		if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(paraSet.getSupplyMode())
        				|| YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(paraSet.getSupplyMode())){
        			cashPecPay.setCashAccCode(rs.getString("FCashAccCode"));//现金账户
        		}else{
        			cashPecPay.setCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
														rs.getString("FCashAccCode") : rs.getString("FClearAccCode"));//现金账户
        		}
				cashPecPay.setRelaOrderNum(rs.getString("FNum"));
				setCashRecPayProperty(rs,cashPecPay);//设置现金应收应付其他属性
				cashPayRecAdmin.addList(cashPecPay);
			}
			cashPayRecAdmin.insert(dDate, dDate, 
								YssOperCons.YSS_ZJDBLX_Rec + "," + YssOperCons.YSS_ZJDBLX_Pay, 
								YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPBuy + "," + YssOperCons.YSS_ZJDBZLX_ETF_CashBalRPSell, 
								"", sPortCode, "", "", "", 0);
		}catch(Exception e){
			throw new YssException("生成现金差额应收应付出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	
	/**
	 * 初始化现金应收应付属性值
	 * @param rs
	 * @return
	 */
	private void setCashRecPayProperty(ResultSet rs,CashPecPayBean cashPecPay) throws YssException {
		double BaseCuryRate = 0;
		double PortCuryRate = 0;
		double BaseMoney = 0;
		double PortMoney = 0;
		EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
		try {
			BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取当日基础汇率
					rs.getString("FCuryCode"), rs.getString("FPortCode"),
					YssOperCons.YSS_RATE_BASE);

			rateOper.setYssPub(pub);
			rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs
					.getString("FPortCode"));
			PortCuryRate = rateOper.getDPortRate(); //获取当日组合汇率
			
			BaseMoney = this.getSettingOper().calBaseMoney(cashPecPay.getMoney(), BaseCuryRate); //计算基础货币金额
			PortMoney = this.getSettingOper().calPortMoney(cashPecPay.getMoney(), BaseCuryRate,
					PortCuryRate, rs.getString("FCuryCode"), dDate,
					rs.getString("FPortCode")); //计算组合货币金额

			cashPecPay.setTradeDate(dDate);
			cashPecPay.setPortCode(rs.getString("FPortCode"));
			cashPecPay.setBaseCuryRate(BaseCuryRate);
			cashPecPay.setPortCuryRate(PortCuryRate);
			cashPecPay.setMoney(cashPecPay.getMoney());
			cashPecPay.setBaseCuryMoney(BaseMoney);
			cashPecPay.setPortCuryMoney(PortMoney);
			cashPecPay.setInOutType(1); 
			cashPecPay.setCuryCode(rs.getString("FCuryCode"));
			cashPecPay.checkStateId = 1;  
			
		} catch (Exception e) {
			throw new YssException("设置现金差额和现金替代应收应付数据时出现异常！", e);
		}
	}
	
	/**
	 * 产生应付赎回款-ETF赎回-必须现金替代款，即股票篮中替代标志为2（深交所）或6（上交所）的现金替代
	 * @throws YssException
	 */
	private void createRedeemMustReplaceCash(ETFParamSetBean paraSet) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
        CashPecPayBean cashPecPay = null;
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);
        try{
        	buff = new StringBuffer(1000);
        	buff.append("select a.*, c.FCashAccCode, c.FClearAccCode, c.FNormScale,c.FSUPPLYMODE,")
        		.append(" ca.FCuryCode,ta.fsellamount from (select FPortCode,ftradedate,fsellamount from ")
        		.append(pub.yssGetTableName("tb_ta_trade"))//TA交易数据
        		.append(" where fcheckstate = 1 and fselltype in ('02') ")//TA赎回数据
        		.append(" and FCONFIMDATE = ").append(dbl.sqlDate(dDate))//确认日期为当前业务日期
        		.append(" and fportcode = ").append(dbl.sqlString(sPortCode))
        		.append(") ta left join (select FDate,fportcode,sum(FTotalMoney) as FTotalMoney from ")
        		.append(pub.yssGetTableName("Tb_ETF_Stocklist"))//股票篮
        		.append(" where fportcode = ").append(dbl.sqlString(sPortCode))
        		.append(" and freplacemark in('2','6')")//必须现金替代标识：深交所 2， 上交所6		
        		.append(" and fsecuritycode <> ").append(dbl.sqlString(paraSet.getCapitalCode()))//去掉虚拟成份股
        		.append(" group by FDate,fportcode ) a ")
        		.append(" on a.FDate = ta.ftradedate and a.fportcode = ta.FPortCode")
        		.append(" left join (select FPortCode ,FNormScale,FCashAccCode,FClearAccCode,FSUPPLYMODE from ")
        		.append(pub.yssGetTableName("Tb_ETF_Param"))//ETF参数设置
        		.append(" where fcheckstate = 1) c on c.FPortCode = ta.FPortCode ")
        		.append(" left join (select FCashAccCode,FCuryCode from ").append(pub.yssGetTableName("Tb_Para_Cashaccount"))
        		.append(" where FCheckState = 1 and FState =0) ca on ca.FCashAccCode = c.FCashAccCode");
        	
        	rs = dbl.queryByPreparedStatement(buff.toString());
        	while(rs.next()){
        		cashPecPay = new CashPecPayBean();
        		cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);//业务类型
        		cashPecPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustRPSell);//业务子类型
        		cashPecPay.setMoney(YssD.mul(rs.getDouble("FTotalMoney"), //单位篮子必须现金替代金额 × 篮子数
        											YssD.div(rs.getDouble("fsellamount"), rs.getDouble("FNormScale"))));
        		if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(paraSet.getSupplyMode())
        				|| YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(paraSet.getSupplyMode())){
        			cashPecPay.setCashAccCode(rs.getString("FCashAccCode"));
        		}else{
        			cashPecPay.setCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
														rs.getString("FCashAccCode") : rs.getString("FClearAccCode"));
        		}
        		setCashRecPayProperty(rs,cashPecPay);//设置赎回必须现金替代应收应付属性
        		cashPayRecAdmin.addList(cashPecPay);
        	}
			cashPayRecAdmin.insert(dDate, dDate, 
					YssOperCons.YSS_ZJDBLX_Pay, YssOperCons.YSS_ZJDBZLX_ETF_CashInsMustRPSell, 
					"", sPortCode, "", "", "", 0);
        }catch(Exception e){
        	throw new YssException("生成赎回必须现金替代应付款出错！", e);
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	
	/**
	 * 产生应付赎回款-ETF赎回-可以现金替代款，即股票篮中替代标志为1（深交所）或5（上交所）的现金替代
	 * @throws YssException
	 */
	private void createRedeemMayReplaceCash(ETFParamSetBean paraSet) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
        CashPecPayBean cashPecPay = null;
        CashPayRecAdmin cashPayRecAdmin = new CashPayRecAdmin();
        cashPayRecAdmin.setYssPub(pub);
        try{
        	buff = new StringBuffer(500);
        	buff.append("select a.*, c.FCashAccCode, c.FClearAccCode, c.FNormScale,c.FSUPPLYMODE,")
        		.append(" ca.FCuryCode,ta.fsellamount from (select FPortCode,ftradedate,fsellamount from ")
        		.append(pub.yssGetTableName("tb_ta_trade"))//TA交易数据
        		.append(" where fcheckstate = 1 and fselltype in ('02') ")//TA赎回数据
        		.append(" and FCONFIMDATE = ").append(dbl.sqlDate(dDate))//确认日期为当前业务日期
        		.append(" and fportcode = ").append(dbl.sqlString(sPortCode))
        		.append(") ta left join (select fportcode, sum(FHReplaceCash) as freplacecash,fbuydate from ")
        		.append(pub.yssGetTableName("Tb_ETF_Tradestldtl"))//交易结算明细表，获取可以现金替代款金额
        	  	.append(" where fportcode = ").append(dbl.sqlString(sPortCode))
        		.append(" and FBs = 'S' and fsecuritycode <> ' ' ")//去掉汇总数据
        		.append(" and (fstockholdercode <> ' ' or fstockholdercode is null)")//去掉汇总数据
        		.append(" group by fbuydate,fportcode ) a ")
        		.append(" on ta.fportcode = a.fportcode and ta.ftradedate = a.fbuydate")
        		.append(" left join (select FPortCode ,FCashAccCode,FClearAccCode,FNormScale,FSUPPLYMODE from ")
        		.append(pub.yssGetTableName("Tb_ETF_Param"))
        		.append(" where fcheckstate = 1) c on c.FPortCode = ta.FPortCode ")
        		.append(" left join (select FCashAccCode,FCuryCode from ")
        		.append(pub.yssGetTableName("Tb_Para_Cashaccount"))//关联现金账户对应的币种，用于获取汇率
        		.append(" where FCheckState = 1 and FState =0) ca on ca.FCashAccCode = c.FCashAccCode");
        	
        	rs = dbl.queryByPreparedStatement(buff.toString());
        	while(rs.next()){
        		cashPecPay = new CashPecPayBean();
        		cashPecPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);//业务类型
        		cashPecPay.setSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPSell);//业务子类型
        		if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(rs.getString("FSUPPLYMODE"))){
        			//易方达ETF在交易结算明细表中保存的是单位篮子的替代金额，因此需要乘以篮子数
        			cashPecPay.setMoney(YssD.mul(rs.getDouble("freplacecash"), 
        											YssD.div(rs.getDouble("fsellamount"), rs.getDouble("FNormScale"))));        			
        		}else{
        			cashPecPay.setMoney(rs.getDouble("freplacecash"));//现金账户
        		}
        		if(YssOperCons.YSS_ETF_MAKEUP_ONE.equals(rs.getString("FSUPPLYMODE")) ||
        		   YssOperCons.YSS_ETF_MAKEUP_GCJQPJ.equals(rs.getString("FSUPPLYMODE"))){
        			cashPecPay.setCashAccCode(rs.getString("FCashAccCode"));
        		}else if(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE.equals(rs.getString("FSUPPLYMODE"))){
        			cashPecPay.setCashAccCode((rs.getString("FClearAccCode") == null || rs.getString("FClearAccCode").length() == 0) ? 
							rs.getString("FCashAccCode") : rs.getString("FClearAccCode"));//现金账户
        		}
        		setCashRecPayProperty(rs,cashPecPay);//设置其他现金应收应付属性
        		cashPayRecAdmin.addList(cashPecPay);
        	}
			cashPayRecAdmin.insert(dDate, dDate, 
									YssOperCons.YSS_ZJDBLX_Pay, 
									YssOperCons.YSS_ZJDBZLX_ETF_CashInsRPSell, 
									"", sPortCode, "", "", "", 0);
        }catch(Exception e){
        	throw new YssException("生成应付赎回款-ETF赎回-可以现金替代款出错！", e);
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
}
