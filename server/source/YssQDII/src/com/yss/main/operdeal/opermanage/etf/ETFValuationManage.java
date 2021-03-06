package com.yss.main.operdeal.opermanage.etf;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachRateOper;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.manager.CashPayRecAdmin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 此类产生ETF基金的估值增值，取值表为台帐子表（tb_XXX_etf_substandingbook）
 * @author Administrator
 *
 */
public class ETFValuationManage extends BaseOperManage{

	/**
	 * 初始化变量
	 */
	public void initOperManageInfo(Date dDate, String portCode) throws YssException {
		this.sPortCode = portCode;
		this.dDate = dDate;
	}
	
	/**
	 * 入口方法
	 */
	public void doOpertion() throws YssException {
		try{
			ETFParamSetAdmin etfParamAdmin = new ETFParamSetAdmin();
			etfParamAdmin.setYssPub(pub);
			ETFParamSetBean etfParamBean = new ETFParamSetBean();
			HashMap hm = new HashMap();
			hm = etfParamAdmin.getETFParamInfo(sPortCode);
			etfParamBean = (ETFParamSetBean)hm.get(sPortCode);
			if(etfParamBean == null){
				throw new YssException("组合【" + sPortCode + "】对应的ETF参数设置不存在或未审核！");
			}
			if(etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_ONE) || 
					etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE) 
					|| etfParamBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ) ){
				//易方达、华夏ETF 不需要统计此项 panjunfang modify 20110815 #1434
				return;
			}
			createETFValue("B");//产生ETF基金的估值增值（申购）
			createETFValue("S");//产生ETF基金的估值增值（赎回）
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	/**
	 * 产生ETF基金的估值增值
	 * @throws YssException 
	 *
	 */
	private void createETFValue(String strBS) throws YssException {
		StringBuffer buff = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean bTrans = true;
		CashPecPayBean cashPay = null;//现金应收应付实体bean
		CashPayRecAdmin cashPayRecAdmin = null;//现金应收应付操作类
		double baseRate =1;//基础汇率
		double portRate =1;//组合汇率
		double dTotalValue = 0;
		String strCuryCode = "";
		String strCashAccCode = "";
		try{
			cashPayRecAdmin = new CashPayRecAdmin();
			cashPayRecAdmin.setYssPub(pub);
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_CashPayRec"));
			
			buff = new StringBuffer(1000);
		
			buff.append("SELECT sub.FPortCode, sub.FTotalValue, sub.FBS, FExRateDate, d.fcashacccode,d.fcurycode");
			buff.append(" FROM (SELECT a.FPortCode,a.FExRateDate,SUM(a.FRateProLoss) AS FTotalValue,b.FBS FROM ").append(pub.yssGetTableName("TB_ETF_SUBSTANDINGBOOK"));
			buff.append(" a JOIN (SELECT FBS,FBuyDate,FPortCode,FSecurityCode,Fgradetype2,");
			buff.append(" CASE WHEN FStockholderCode IS NULL THEN ' ' ELSE FStockholderCode END AS FStockholderCode,");
			buff.append(" CASE WHEN FRateType IS NULL THEN ' ' ELSE FRateType END AS FRateType,");
			buff.append("CASE WHEN FTradeNum IS NULL THEN ' ' ELSE FTradeNum END AS FTradeNum FROM ");
			buff.append(pub.yssGetTableName("TB_ETF_STANDINGBOOK")).append(") b ON a.fbuydate = b.fbuydate ");
			buff.append(" and a.fbs = b.fbs and a.fportcode = b.fportcode and a.fsecuritycode = b.fsecuritycode and a.fstockholdercode = b.fstockholdercode and a.ftradenum = b.ftradenum AND a.FRateType = b.FRateType");
			buff.append(" WHERE a.FExRateDate = ").append(dbl.sqlDate(dDate)).append(" AND a.FPortCode = ").append(dbl.sqlString(sPortCode));
			buff.append(" AND b.FBS =  ").append(dbl.sqlString(strBS));
			buff.append(" AND b.Fgradetype2 IS NOT NULL GROUP BY a.FPortCode, a.FExRateDate,b.FBS) sub");
			buff.append(" join (select p.fportcode,p.fcashacccode,ca.fcurycode from ").append(pub.yssGetTableName("tb_etf_param"));
			buff.append(" p left join (select * from ").append(pub.yssGetTableName("tb_para_cashaccount"));
			buff.append(" where FCheckState = 1) ca on p.fcashacccode =  ca.fcashacccode");
			buff.append(" where p.FCheckState = 1) d on sub.FPortCode = d.FPortCode");
			
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			while(rs.next()){
				
				baseRate = this.getSettingOper().getCuryRate(// 基础汇率
						this.dDate, rs.getString("fcurycode"),
						rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

				EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
				rateOper.setYssPub(pub);// 设置PUB
				rateOper.getInnerPortRate(this.dDate,
						rs.getString("fcurycode"), rs.getString("FPortCode"));
				portRate = rateOper.getDPortRate();// 组合汇率
				strCuryCode = rs.getString("fcurycode");
				strCashAccCode = rs.getString("fcashacccode");
//				if(rs.getString("FBS").equals("S")){
//					dTotalValue = dTotalValue - rs.getDouble("FTotalValue");
//				}else{
//					dTotalValue = dTotalValue + rs.getDouble("FTotalValue");
//				}
				dTotalValue = dTotalValue + rs.getDouble("FTotalValue");
			}

			cashPay = new CashPecPayBean();
			cashPay.setTradeDate(this.dDate);//业务日期
			cashPay.setPortCode(this.sPortCode);//组合代码
			cashPay.setBrokerCode(" ");
			cashPay.setInvestManagerCode(" ");
			cashPay.setCashAccCode(strCashAccCode);//现金帐户
			cashPay.setTsfTypeCode(YssOperCons.YSS_ZJDBLX_Pay);//调拨类型
			
			cashPay.setCuryCode(strCuryCode);//币种
			cashPay.setInOutType(1);//流出方向
			cashPay.setMoney(dTotalValue);//金额（应付替代款的汇兑损益在台账子表中负值为流出，即应付）
			cashPay.setBaseCuryRate(baseRate);//基础汇率
			cashPay.setBaseCuryMoney(YssD.mul(baseRate,dTotalValue));//基础货币金额
			cashPay.setPortCuryRate(portRate);//组合汇率
			cashPay.setPortCuryMoney(YssD.round(YssD.div(YssD.mul(baseRate,dTotalValue),portRate),2));//组合货币金额
			cashPay.setDataSource(0);//数据源
			cashPay.setStockInd(0);//入账标识
			cashPay.checkStateId =1;//审核状态
			cashPay.creatorCode = pub.getUserCode();//创建人
			cashPay.creatorTime = YssFun.formatDate(new Date());//创建时间
			if(strBS.equals("S")){
				cashPay.setSubTsfTypeCode(YssOperCons.YSS_ETF_QUITVALUESell);//调拨子类型
				cashPayRecAdmin.addList(cashPay);
				cashPayRecAdmin.insert(this.dDate, this.dDate, 
						YssOperCons.YSS_ZJDBLX_Pay, 
						YssOperCons.YSS_ETF_QUITVALUESell, 
						"", this.sPortCode, "", "", "", 0);
			}else{
				cashPay.setSubTsfTypeCode(YssOperCons.YSS_ETF_QUITVALUEBuy);//调拨子类型
				cashPayRecAdmin.addList(cashPay);
				cashPayRecAdmin.insert(this.dDate, this.dDate, 
						YssOperCons.YSS_ZJDBLX_Pay, 
						YssOperCons.YSS_ETF_QUITVALUEBuy, 
						"", this.sPortCode, "", "", "", 0);
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		}catch (Exception e) {
			throw new YssException("产生ETF基金的应退款估值增值出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}

}
