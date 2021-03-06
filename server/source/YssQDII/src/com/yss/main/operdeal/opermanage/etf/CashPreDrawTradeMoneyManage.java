package com.yss.main.operdeal.opermanage.etf;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

import com.yss.commeach.EachRateOper;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.manager.CashPayRecAdmin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 此类产生预提交易收入的现金应收应付数据
 * @author xuqiji 20091203 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 *
 */
public class CashPreDrawTradeMoneyManage extends BaseOperManage{
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
			createCashTradeFeeRecPay();//产生交易费用的现金应收应付数据
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	/**
	 * 产生交易费用的现金应收应付数据
	 *
	 */
	private void createCashTradeFeeRecPay() throws YssException{
		StringBuffer buff =null;
		Connection conn = null;
		boolean bTrans = true;
		ResultSet rs = null;
		String strYearMonth ="";
		double tradeTotalFeeBuy =0;
		double tradeTotalFeeSell =0;
		CashPayRecAdmin cashPayAdmin = new CashPayRecAdmin();
		EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
		double BaseCuryRate = 0;
		double PortCuryRate = 0;
		String []yesDatePreDrawTradeMoneyStockDataSG = null ;//保存昨日现金应收应付申购数据
		String []yesDatePreDrawTradeMoneyStockDataSH = null ;//保存昨日现金应收应付赎回数据
		String []sData =new String[]{"0","0"};//保存拼接的数据
		try{
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			buff = new StringBuffer(500);
			
			strYearMonth = YssFun.left(YssFun.formatDate(this.dDate), 4) + "00";
			
			buff.append(" select * from ").append(pub.yssGetTableName("tb_stock_cashpayrec"));
			buff.append(" where FTsFTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_ZJDBLX_Pay));
			buff.append(" and FSubTsfTypeCode in( ").append(dbl.sqlString(YssOperCons.YSS_ETF_CashTradeCost_SG));
			buff.append(",").append(dbl.sqlString(YssOperCons.YSS_ETF_CashTradeCost_SH)).append(")");
			buff.append(" and FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(this.dDate,-1)));
			buff.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode));
			buff.append(" and FYearmonth <> ").append(dbl.sqlString(strYearMonth));
			
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			while(rs.next()){
				if(rs.getString("FSubTsfTypeCode").equalsIgnoreCase(YssOperCons.YSS_ETF_CashTradeCost_SG)){
					sData[0]=rs.getDouble("FBal") + "," + rs.getString("FCashAccCode") + "," +rs.getString("FCuryCode") +","+rs.getString("FAnalysisCode1") + "," +rs.getString("FAnalysisCode2");
				}else{
					sData[1]=rs.getDouble("FBal") + "," + rs.getString("FCashAccCode") + "," +rs.getString("FCuryCode") +","+rs.getString("FAnalysisCode1") + "," +rs.getString("FAnalysisCode2");
				}
			}
			dbl.closeResultSetFinal(rs);
			
			yesDatePreDrawTradeMoneyStockDataSG = sData[0].split(",");
			yesDatePreDrawTradeMoneyStockDataSH = sData[1].split(",");
			
			if(Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSG[0])!=0||Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSH[0])!=0){
				buff.append(" select sub.* from (select * from ").append(pub.yssGetTableName("tb_data_subtrade")).append(" a ");
				buff.append(" where FBargainDate =").append(dbl.sqlDate(this.dDate)).append(" and FCheckState = 1 and FPortCode =").append(dbl.sqlString(this.sPortCode));
				buff.append( Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSG[0])!=0&&Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSH[0])==0?" and FTradeTypecode ='01'":"");
				buff.append(Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSH[0])!=0&&Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSG[0])==0?" and FTradeTypecode ='02'":"");
				buff.append(Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSG[0])!=0&&Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSH[0])!=0?" and FTradeTypecode in('01','02')":"");
				buff.append(" order by FNum) sub ");
				buff.append(" join (select * from ").append(pub.yssGetTableName("tb_etf_stocklist"));
				buff.append(" where FPortCode =").append(dbl.sqlString(this.sPortCode)).append(" and FDate =").append(dbl.sqlDate(this.dDate));
				buff.append(" ) s on sub.fsecuritycode = s.fsecuritycode");
				buff.append(" join(select * from ").append(pub.yssGetTableName("tb_para_portfolio"));
				buff.append(" where FCheckState =1 and FSubAssetType ='0106' ) op on op.FPortCode = sub.FPortCode");
				
				rs =dbl.queryByPreparedStatement(buff.toString());
				buff.delete(0,buff.length());
				
				while (rs.next()) {
					for (int i = 1; i <= 8; i++) {
						if(rs.getString("FTradeTypeCode").equals("01")){
							tradeTotalFeeBuy += rs.getDouble("FTradeFee" + i);
						}else{
							tradeTotalFeeSell += rs.getDouble("FTradeFee" + i);
						}
					}
				}
				BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取当日基础汇率
						yesDatePreDrawTradeMoneyStockDataSG[2], this.sPortCode,
						YssOperCons.YSS_RATE_BASE);

				rateOper.setYssPub(pub);
				rateOper.getInnerPortRate(dDate, yesDatePreDrawTradeMoneyStockDataSG[2], this.sPortCode);
				PortCuryRate = rateOper.getDPortRate(); //获取当日组合汇率
				if(tradeTotalFeeBuy!=0){
					//买入费用应收应付
					setTACashPecPay(this.dDate,
	                        this.sPortCode,
	                        yesDatePreDrawTradeMoneyStockDataSG[1],
	                        yesDatePreDrawTradeMoneyStockDataSG[3],
	                        yesDatePreDrawTradeMoneyStockDataSG[4], "",
	                        yesDatePreDrawTradeMoneyStockDataSG[2],
	                        1,
	                        tradeTotalFeeBuy,
	                        BaseCuryRate,
	                        PortCuryRate,
	                        cashPayAdmin,
	                        this.dDate,
	                        YssOperCons.YSS_ZJDBLX_Fee,
	                        YssOperCons.YSS_ETF_CashTradeFee_SG); 
				}
				if(tradeTotalFeeSell!=0){
					//卖入费用应收应付
					setTACashPecPay(this.dDate,
	                        this.sPortCode,
	                        yesDatePreDrawTradeMoneyStockDataSH[1],
	                        yesDatePreDrawTradeMoneyStockDataSH[3],
	                        yesDatePreDrawTradeMoneyStockDataSH[4], "",
	                        yesDatePreDrawTradeMoneyStockDataSH[2],
	                        1,
	                        tradeTotalFeeSell,
	                        BaseCuryRate,
	                        PortCuryRate,
	                        cashPayAdmin,
	                        this.dDate,
	                        YssOperCons.YSS_ZJDBLX_Fee,
	                        YssOperCons.YSS_ETF_CashTradeFee_SH); 
				}
			}
			if(tradeTotalFeeBuy < Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSG[0])){
				//买入费用应收应付
				setTACashPecPay(this.dDate,
                        this.sPortCode,
                        yesDatePreDrawTradeMoneyStockDataSG[1],
                        yesDatePreDrawTradeMoneyStockDataSG[3],
                        yesDatePreDrawTradeMoneyStockDataSG[4], "",
                        yesDatePreDrawTradeMoneyStockDataSG[2],
                        -1,
                        YssD.sub(Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSG[0]),tradeTotalFeeBuy),
                        BaseCuryRate,
                        PortCuryRate,
                        cashPayAdmin,
                        this.dDate,
                        YssOperCons.YSS_ZJDBLX_Pay,
                        YssOperCons.YSS_ETF_CashTradeCost_SG); 
			}
			if(tradeTotalFeeSell < Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSH[0])){
				//卖入费用应收应付
				setTACashPecPay(this.dDate,
                        this.sPortCode,
                        yesDatePreDrawTradeMoneyStockDataSH[1],
                        yesDatePreDrawTradeMoneyStockDataSH[3],
                        yesDatePreDrawTradeMoneyStockDataSH[4], "",
                        yesDatePreDrawTradeMoneyStockDataSH[2],
                        -1,
                        YssD.sub(Double.parseDouble(yesDatePreDrawTradeMoneyStockDataSH[0]),tradeTotalFeeSell),
                        BaseCuryRate,
                        PortCuryRate,
                        cashPayAdmin,
                        this.dDate,
                        YssOperCons.YSS_ZJDBLX_Pay,
                        YssOperCons.YSS_ETF_CashTradeCost_SH); 
			}
			cashPayAdmin.setYssPub(pub);
			cashPayAdmin.insert(dDate, YssOperCons.YSS_ZJDBLX_Fee + "," + YssOperCons.YSS_ZJDBLX_Pay,
                    YssOperCons.YSS_ETF_CashTradeCost_SG+","+YssOperCons.YSS_ETF_CashTradeCost_SH + "," +YssOperCons.YSS_ETF_CashTradeFee_SG +"," + YssOperCons.YSS_ETF_CashTradeFee_SH,
                    this.sPortCode, 0, false);
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("产生交易费用的现金应收应付数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn,bTrans);
		}
	}
	 /**
     * ETF基金为应收应付数据赋值
     * @param dTransDate 业务日期
     * @param sPortCode 组合代码
     * @param sCashAccCode 现金账户
     * @param sAnalysisCode1 分析代码1
     * @param sAnalysisCode2 分析代码2
     * @param sAnalysisCode3 分析代码3
     * @param sTradeCury 币种
     * @param dFee 金额
     * @param dBaseRate 基础汇率
     * @param dPortRate 组合汇率
     * @param cashPayAdmin 应收应付操作类
     * @param dRateDate 汇率日期
     * @throws YssException
     */
    private void setTACashPecPay(java.util.Date dTransDate, String sPortCode,String sCashAccCode, 
    		String sAnalysisCode1, String sAnalysisCode2,String sAnalysisCode3, String sTradeCury,int  iInOutType,double dFee,
			double dBaseRate, double dPortRate, CashPayRecAdmin cashPayAdmin,java.util.Date dRateDate,String sTsFTypeCode,String sSubTsfTypeCode) throws YssException {
		if (dFee == 0) {
			return;
		}
		CashPecPayBean cashpecpay = new CashPecPayBean();
		cashpecpay.setTradeDate(dTransDate);
		cashpecpay.setPortCode(sPortCode);
		cashpecpay.setInvestManagerCode(sAnalysisCode1);
		cashpecpay.setBrokerCode(sAnalysisCode2);
		cashpecpay.setCashAccCode(sCashAccCode);
		cashpecpay.setCuryCode(sTradeCury);
		cashpecpay.setMoney(dFee);
		cashpecpay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(dFee,
				dBaseRate));
		cashpecpay.setPortCuryMoney(this.getSettingOper().calPortMoney(dFee,
				dBaseRate, dPortRate,
				sTradeCury, dRateDate, sPortCode));
		cashpecpay.setBaseCuryRate(dBaseRate);
		cashpecpay.setPortCuryRate(dPortRate);
		cashpecpay.setInOutType(iInOutType);//方向
		cashpecpay.checkStateId = 1;
		cashpecpay.setTsfTypeCode(sTsFTypeCode); // 应付款项
		cashpecpay.setSubTsfTypeCode(sSubTsfTypeCode);//调拨子类型
		cashPayAdmin.addList(cashpecpay);
	}
}



















